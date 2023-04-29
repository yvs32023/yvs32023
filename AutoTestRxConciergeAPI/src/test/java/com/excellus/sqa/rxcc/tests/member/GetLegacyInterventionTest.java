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

import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.MemberLegacyInterventionQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.MemberLegacyIntervention;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 *  GetLegacyIntervention
 *  Get member type legacyintervention by {id} for {memberId}
 *  
 * 
 *  GET https://apim-lbs-rxc-dev-east-001.azure-api.net/api/member/members/{memberId}/legacy-interventions/{id}
 *  
 *  Cosmos rxcc-tenant: member
 *  Required Header: X-RXCC-SUB <ehp | exe> to specify Tenant
 *  Request Parameters :  memberId & id
 *  
 * @author Manish Sharma (msharma)
 * @since 09/09/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("INTERVENTION")
@Tag("LEGACYINTERVENTION")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetMemberLegacyIntervention")
public class GetLegacyInterventionTest  extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(GetLegacyInterventionTest.class);
	private String subscriptionName; 

	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("25350:Member GetLegacyIntervention Happy Path (ALL Member)")
	@Order(1)
	public List<DynamicNode> happyPathLegacyIntervention() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "";
				memberId = MemberLegacyInterventionQueries.getRandomMemberWithLegacyIntervention(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				//Query Cosmos with one expected member that should have legacycorrespondence
				MemberLegacyIntervention expected = MemberLegacyInterventionQueries.getLegacyIntervention(subscriptionName, memberId).get(0);;

				// API call
				logger.debug("Starting API call");
				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_LEGACY_INTERVENTION_GET_ENDPOINT, new Object[] {expected.getMemberId() , expected.getId()}, 200, null);
				apiGetStep.run();

				MemberLegacyIntervention actual  = apiGetStep.convertToJsonDTO(MemberLegacyIntervention.class);
				apiGetStep.getTestResults().add( dynamicContainer("MemberLegacyIntervention DTO", expected.compare(actual)));

				//return expected.compare(actual);
				test.add(dynamicContainer(subscriptionName.toUpperCase() + " : " + memberId, apiGetStep.getTestResults()));
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}	


	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("25355:Member GetLegacyIntervention Invalid Auth")
	@Order(2)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = UUID.randomUUID().toString();
				String id = UUID.randomUUID().toString();

				Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_LEGACY_INTERVENTION_GET_ENDPOINT,
						new Object[]{memberId, id}, 401, HTTP_401_UNAUTHORIZED);
				apiGetStep.run();

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("25360:Member GetLegacyIntervention Header Null")
	@Order(3)
	public List<DynamicNode>  nullHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = UUID.randomUUID().toString();
				String id = UUID.randomUUID().toString();

				Headers headers = getGenericHeaders();
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_LEGACY_INTERVENTION_GET_ENDPOINT,
						new Object[]{memberId, id}, 500, HTTP_500_INTERNAL_SERVER_ERR);
				apiGetStep.run();

				test.add(dynamicContainer(subscriptionName.toUpperCase(),apiGetStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("25361:Member GetLegacyIntervention Invalid Header")
	@Order(4)
	public List<DynamicNode> invalidHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = UUID.randomUUID().toString();
				String id = UUID.randomUUID().toString();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME+"invalid", subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_LEGACY_INTERVENTION_GET_ENDPOINT,
						new Object[]{memberId, id}, 500, HTTP_500_INTERNAL_SERVER_ERR);
				apiGetStep.run();

				test.add(dynamicContainer(subscriptionName.toUpperCase(),apiGetStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("25363:Member GetLegacyIntervention Valid Parm With Incorrect Header Value")
	@Order(5)
	public List<DynamicNode>  validParmInvalidHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "";
				memberId = MemberLegacyInterventionQueries.getRandomMemberWithLegacyIntervention(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				//Query Cosmos with one expected member that should have legacycorrespondence
				MemberLegacyIntervention expected = MemberLegacyInterventionQueries.getLegacyIntervention(subscriptionName, memberId).get(0);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME + "invalid", subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_LEGACY_INTERVENTION_GET_ENDPOINT,
						new Object[]{expected.getMemberId(), expected.getId()}, 500, HTTP_500_INTERNAL_SERVER_ERR);
				apiGetStep.run();

				test.add(dynamicContainer(subscriptionName.toUpperCase(),apiGetStep.getTestResults())); // add all step test result

			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("25362:Member GetLegacyIntervention Header Missing")
	@Order(6)
	public List<DynamicNode> missingHeader()
	{
		String memberId = UUID.randomUUID().toString();
		String id = UUID.randomUUID().toString();

		ApiGetStep apiGetStep= new ApiGetStep(getGenericHeaders(), MEMBER_LEGACY_INTERVENTION_GET_ENDPOINT,
				new Object[]{memberId, id}, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}


	@TestFactory
	@DisplayName("25352:Member GetLegacyIntervention Happy Path with Param Invalid")
	@Order(7)
	public List<DynamicNode> invalidParam() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "invalid";
				String id = "invalid";

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_LEGACY_INTERVENTION_GET_ENDPOINT,
						new Object[]{memberId, id}, 400, HTTP_400_BAD_REQUEST);
				apiGetStep.run();

				// validate the API message
				final String EXPECTED_MSG = "\"Member id "+  memberId +" not found!\"";
				String actualMsg = apiGetStep.getResponse().then().extract().asString();
				apiGetStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("25364:Member GetLegacyIntervention All Parm Combo Not Exist")
	@Order(8)
	public List<DynamicNode> allParmNotExists() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = UUID.randomUUID().toString();
				String id = UUID.randomUUID().toString();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_LEGACY_INTERVENTION_GET_ENDPOINT,
						new Object[]{memberId, id}, 400, HTTP_400_BAD_REQUEST);
				apiGetStep.run();

				// validate the API message
				final String EXPECTED_MSG = "\"Member id "+  memberId +" not found!\"";
				String actualMsg = apiGetStep.getResponse().then().extract().asString();
				apiGetStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("25365:Member GetLegacyIntervention Partial Parm Combo Not Exist")
	@Order(9)
	public List<DynamicNode> partialParmNotExist() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "";
				memberId = MemberLegacyInterventionQueries.getRandomMemberWithLegacyIntervention(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				//Query Cosmos with one expected member that should have legacycorrespondence
				MemberLegacyIntervention expected  = MemberLegacyInterventionQueries.getLegacyIntervention(subscriptionName, memberId).get(0);

				//random id
				String id = UUID.randomUUID().toString();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_LEGACY_INTERVENTION_GET_ENDPOINT,
						new Object[]{expected.getMemberId(), id}, 404, HTTP_404_NOT_FOUND);
				apiGetStep.run();

				test.add(dynamicContainer(subscriptionName.toUpperCase(),apiGetStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}
}