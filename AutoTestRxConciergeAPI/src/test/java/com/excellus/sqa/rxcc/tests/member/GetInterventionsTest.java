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
import com.excellus.sqa.rxcc.cosmos.MemberInterventionQueries;
import com.excellus.sqa.rxcc.cosmos.ProviderQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.Provider;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 *  GetInterventions
 *  Get Member Type Interventions for {memberId}
 *  
 * 
 *  GET https://apim-lbs-rxc-dev-east-001.azure-api.net/api/member/members/{memberId}/interventions
 *  
 *  Cosmos rxcc-tenant: member
 *  Required Header: X-RXCC-SUB <ehp | exe> to specify Tenant
 *  Request Parameters :  memberId
 * 
 * @author Manish Sharma (msharma)
 * @since 09/08/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("INTERVENTION")
@Tag("MEMBERINTERVENTION")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetMemberInterventions")
public class GetInterventionsTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(GetInterventionsTest.class);
	private String subscriptionName; 


	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("22603:Member Interventions ALL Happy Path")
	@Order(1)
	public List<DynamicNode> happyPathALL() throws JsonProcessingException {

		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "";
				memberId = MemberInterventionQueries.getRandomMemberWithIntervention(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				//Query Cosmos for member intervention
				List<MemberIntervention> expectedMemberInterventions = MemberInterventionQueries.getIntervention(subscriptionName, memberId);

				Integer numberOfRecords = expectedMemberInterventions.size();
				for (int i = 0; i<numberOfRecords; i++)
				{
					//replacing member intervention  provider details  record fetch for validation as in api response are pulled from the provider.provider document referenced by 'originalNpi' {'originalProviderContact', 'originalProviderName', 'originalOfficeFaxVerified'}
					if (expectedMemberInterventions.get(i).getOriginalNpi() != null)
					{
						String originalNpi = "";
						originalNpi = expectedMemberInterventions.get(i).getOriginalNpi();
						String whereClauseExtension = " where c.npi = " + "\"" + originalNpi + "\"" ;

						// Get the provider from the Cosmos for updating {'overrideProviderName', 'overrideOfficeFaxVerified', 'overrideProviderContact'}
						Provider actual = ProviderQueries.getSpecificProvider(whereClauseExtension);

						expectedMemberInterventions.get(i).overrideOriginalProviderContact(actual);
						expectedMemberInterventions.get(i).overrideOriginalProviderName(actual);
						expectedMemberInterventions.get(i).overrideOriginalOfficeFaxVerified(actual);
					}
					//Updating Cosmos record fetch for validation as  in api response are pulled from the provider.provider document referenced by 'overrideNpi' {'overrideProviderName', 'overrideOfficeFaxVerified', 'overrideProviderContact'}
					if (expectedMemberInterventions.get(i).getOverrideNpi() != null)
					{
						String overrideNpi = "";
						overrideNpi = expectedMemberInterventions.get(i).getOverrideNpi();
						String whereClauseExtension = " where c.npi = " + "\"" + overrideNpi + "\"" ;

						// Get the provider from the Cosmos for updating {'overrideProviderName', 'overrideOfficeFaxVerified', 'overrideProviderContact'}
						Provider actual = ProviderQueries.getSpecificProvider(whereClauseExtension);

						expectedMemberInterventions.get(i).overrideOverrideProviderContact(actual);
						expectedMemberInterventions.get(i).overrideOverrideProviderName(actual);
						expectedMemberInterventions.get(i).overrideOverrideOfficeFaxVerified(actual);
					}
				}

				//Log that the API call is starting
				logger.info("Starting API call");

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)), MEMBER_INTERVENTIONS_GET_ENDPOINT, new Object[] {memberId}, 200, null);
				apiGetStep.run();

				//Log that the API call has completed successfully
				logger.debug("API call completed successfully");

				List<MemberIntervention> actualMemberInterventions = apiGetStep.convertToJsonDTOs(MemberIntervention.class);

				if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED)
				{
					return apiGetStep.getTestResults();
				}

				for ( MemberIntervention expected : expectedMemberInterventions)
				{
					boolean found = false;
					for (MemberIntervention actual : actualMemberInterventions ) 
					{
						if ( expected.equals(actual) )
						{
							found = true;
							apiGetStep.getTestResults().add(dynamicContainer("MemberIntervention Id  [" + expected.getMemberId(), expected.compare(actual)));
							break;
						}
					}

					if ( !found )
					{
						apiGetStep.getTestResults().add(dynamicTest("MemberIntervention Id [" + expected.getMemberId(), 
								fail("Unable to find the  member intervention from API response")));
					}
				}
				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults()));
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),
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
	@DisplayName("22609: Member Interventions Invalid Auth")
	@Order(2)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();

			Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionName));
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_INTERVENTIONS_GET_ENDPOINT,
					new Object[]{memberId}, 401, HTTP_401_UNAUTHORIZED);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("153011: Member Interventions Header Null")
	@Order(3)
	public List<DynamicNode>  nullHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders();
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_INTERVENTIONS_GET_ENDPOINT,
					new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("153012: Member Interventions Invalid Header")
	@Order(4)
	public List<DynamicNode> invalidHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{

			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME+"invalid", subscriptionName));
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_INTERVENTIONS_GET_ENDPOINT,
					new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("153013: Member Interventions Valid Parm With Incorrect Header Value")
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
				memberId = MemberInterventionQueries.getRandomMemberWithIntervention(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME + "invalid", subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_INTERVENTIONS_GET_ENDPOINT,
						new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

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
	@DisplayName("153014: Member Interventions Invalid Parm")
	@Order(6)
	public List<DynamicNode>  invalidParm() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = "invalid";

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_INTERVENTIONS_GET_ENDPOINT,
					new Object[]{memberId}, 400, HTTP_400_BAD_REQUEST);

			apiGetStep.run();

			// validate the API message
			final String EXPECTED_MSG = "\"Member id "+  memberId +" not found!\"";
			String actualMsg = apiGetStep.getResponse().then().extract().asString();
			apiGetStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("153015: Member Interventions Header Missing")
	@Order(7)
	public List<DynamicNode> missingHeader()
	{
		String memberId = UUID.randomUUID().toString();

		ApiGetStep apiGetStep= new ApiGetStep(getGenericHeaders(), MEMBER_INTERVENTIONS_GET_ENDPOINT,
				new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

		apiGetStep.run();

		return apiGetStep.getTestResults();
	}
}
