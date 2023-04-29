/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.RequestLoggingFilter;
import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.MemberSimulationQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 *  GetSimulations
 *  Get all member intervention type simulation events created by a Simulation Run using ruleId and simulationRunNumber.
 *  
 * 
 *  GET https://apim-lbs-rxc-dev-east-001.azure-api.net/api/member/simulations[?ruleId][&simulationRunNumber]
 *  
 *  Cosmos rxcc-tenant: member
 *  Required Header: X-RXCC-SUB <ehp | exe> to specify Tenant
 *  Request Parameters :  ruleId & simulationRunNumber
 * 
 * @author Manish Sharma (msharma)
 * @since 09/19/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("SIMULATIONS")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetMemberSimulations")
public class GetSimulationsTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(GetSimulationsTest.class);
	private String subscriptionName; 



	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("26118: Member Simulation Happy Path")
	@Order(1)
	public List<DynamicNode> happyPathAllParam() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<DynamicNode>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				//List<DynamicNode> testSub = new ArrayList<>();

				String memberId = "";
				memberId = MemberSimulationQueries.getRandomMemberWithSimulation(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				List<MemberIntervention> list = MemberSimulationQueries.getSimulations(subscriptionName, memberId);

				//Query Cosmos with one expected member that should have simulation
				MemberIntervention memberSimulation =list.get(0);

				String ruleId = memberSimulation.getRuleId();
				Integer simulationRunNumber = memberSimulation.getSimulationRunNumber();

				// Cosmos db
				List<MemberIntervention> expectedSimulations = new ArrayList<MemberIntervention>();
				expectedSimulations = MemberSimulationQueries.getSimulations(subscriptionName,ruleId,simulationRunNumber);
				
				//Log that the API call is starting
				logger.info("Starting API call");
				
				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)), MEMBER_SIMULATIONS_GET_ENDPOINT, new Object[] {ruleId,simulationRunNumber}, 200, null);
				apiGetStep.run();
				
				//Log that the API call has completed successfully
				logger.debug("API call completed successfully");

				List<MemberIntervention> actualSimulations = apiGetStep.convertToJsonDTOs(MemberIntervention.class);

				if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED)
				{
					return apiGetStep.getTestResults();
				}

				for ( MemberIntervention expected : expectedSimulations)
				{
					boolean found = false;
					for (MemberIntervention actual : actualSimulations ) 
					{
						if ( expected.equals(actual) )
						{
							found = true;
							apiGetStep.getTestResults().add(dynamicContainer("rule id [" + expected.getRuleId() + "]", expected.compare(actual)));
							break;
						}
					}

					if ( !found )
					{
						apiGetStep.getTestResults().add(dynamicTest("rule id [" + expected.getRuleId() + "]", fail("Unable to find the member simulations from API response")));
					}
				}
				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults()));
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Unexpected exception",
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return 	test;
	}

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("26123: Member Simulations Invalid Auth")
	@Order(2)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String ruleId = UUID.randomUUID().toString();
			Integer simulationRunNumber = (int)(Math.random()*(100-1+1)+1);//fetch random number from 1 to 100

			ApiGetStep apiGetStep = new ApiGetStep(getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionName)), MEMBER_SIMULATIONS_GET_ENDPOINT,
					new Object[]{ruleId, simulationRunNumber}, 401, HTTP_401_UNAUTHORIZED);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result

			resetApiInfo();  // Reset the API information and test validation
		}

		return test;

	}


	@TestFactory
	@DisplayName("26120: Member Simulations Invalid Parm")
	@Order(3)
	public List<DynamicNode>  invalidParm() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String ruleId = "invalid";
			String simulationRunNumber =  "invalid";

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_SIMULATIONS_GET_ENDPOINT,
					new Object[]{ruleId, simulationRunNumber}, 400, HTTP_400_BAD_REQUEST);

			apiGetStep.run();

			// validate the API message
			final String EXPECTED_MSG = "\"simulationRunNumber must be an integer!\"";
			String actualMsg = apiGetStep.getResponse().then().extract().asString();
			apiGetStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result

			resetApiInfo();  // Reset the API information and test validation
		}

		return test;

	}

	@TestFactory
	@DisplayName("26128: Member Simulations Header Missing")
	@Order(4)
	public List<DynamicNode> missingHeader()
	{
		String ruleId = UUID.randomUUID().toString();
		Integer simulationRunNumber = (int)(Math.random()*(100-1+1)+1);//fetch random number from 1 to 100

		ApiGetStep apiGetStep= new ApiGetStep(getGenericHeaders(), MEMBER_SIMULATIONS_GET_ENDPOINT,
				new Object[]{ruleId, simulationRunNumber}, 500, HTTP_500_INTERNAL_SERVER_ERR);

		apiGetStep.run();

		return apiGetStep.getTestResults();

	}


	@TestFactory
	@DisplayName("TBD: Member Simulations All Parm Combo Not Exist")
	@Order(5)
	public List<DynamicNode> allParmNotExists() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String ruleId = "";
			String simulationRunNumber = "";

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));


			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_SIMULATIONS_GET_ENDPOINT,
					new Object[]{ruleId, simulationRunNumber}, 400, HTTP_400_BAD_REQUEST);

			apiGetStep.run();

			// validate the API message
			final String EXPECTED_MSG = "\"ruleId and simulationRunNumber are required!\"";
			String actualMsg = apiGetStep.getResponse().then().extract().asString();
			apiGetStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result

			resetApiInfo();  // Reset the API information and test validation
		}

		return test;

	}

	@TestFactory
	@DisplayName("26126: Member Simulations Header Null")
	@Order(6)
	public List<DynamicNode>  nullHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String ruleId = UUID.randomUUID().toString();
			Integer simulationRunNumber = (int)(Math.random()*(100-1+1)+1);//fetch random number from 1 to 100

			Headers headers = getGenericHeaders();

			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_SIMULATIONS_GET_ENDPOINT,
					new Object[]{ruleId, simulationRunNumber}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result

			resetApiInfo();  // Reset the API information and test validation
		}

		return test;

	}


	@TestFactory
	@DisplayName("26127: Member Simulations Invalid Header")
	@Order(7)
	public List<DynamicNode> invalidHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String ruleId = UUID.randomUUID().toString();
			Integer simulationRunNumber = (int)(Math.random()*(100-1+1)+1);//fetch random number from 1 to 100

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME+"invalid", subscriptionName));
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_SIMULATIONS_GET_ENDPOINT,
					new Object[]{ruleId, simulationRunNumber}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result

			resetApiInfo();  // Reset the API information and test validation
		}

		return test;

	}
}
