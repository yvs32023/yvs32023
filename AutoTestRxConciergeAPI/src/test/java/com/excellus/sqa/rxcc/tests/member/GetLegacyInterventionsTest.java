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
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 *  GetLegacyInterventions
 *  Get all member type legacy-interventions for {memberId}
 *  
 * 
 *  GET https://apim-lbs-rxc-dev-east-001.azure-api.net/api/member/members/{memberId}/legacy-interventions
 *  
 *  Cosmos rxcc-tenant: member
 *  Required Header: X-RXCC-SUB <ehp | exe> to specify Tenant
 *  Request Parameters :  memberId
 *  
 * @author Manish Sharma (msharma)
 * @since 09/21/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("INTERVENTION")
@Tag("LEGACYINTERVENTION")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetMemberLegacyInterventions")
public class GetLegacyInterventionsTest  extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(GetLegacyInterventionsTest.class);
	private String subscriptionName; 

	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("25370: Member GetLegacyInterventions Happy Path (ALL Member)")
	@Order(1)
	public List<DynamicNode> happyPathLegacyInterventions() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "";
				memberId =  MemberLegacyInterventionQueries.getRandomMemberWithLegacyIntervention(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				//Query Cosmos
				List<MemberLegacyIntervention> expectedMemberLegacyInterventions = MemberLegacyInterventionQueries.getLegacyIntervention(subscriptionName, memberId);

				// API call
				logger.debug("Starting API call");
				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_LEGACY_INTERVENTIONS_GET_ENDPOINT, new Object[] {memberId}, 200, null);
				apiGetStep.run();

				List<MemberLegacyIntervention> actualMemberLegacyInterventions  = apiGetStep.convertToJsonDTOs(MemberLegacyIntervention.class);

				if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED )
				{
					return apiGetStep.getTestResults();
				}

				for ( MemberLegacyIntervention expected : expectedMemberLegacyInterventions)
				{
					boolean found = false;
					for (MemberLegacyIntervention actual : actualMemberLegacyInterventions ) 
					{
						if ( expected.equals(actual) )
						{
							found = true;
							apiGetStep.getTestResults().add(dynamicContainer("Member id  [" + expected.getMemberId(), expected.compare(actual)));
							break;
						}
					}
					if ( !found )
					{
						apiGetStep.getTestResults().add(dynamicTest("Member id / Id [" + expected.getMemberId(), fail("Unable to find the" +  subscriptionName.toUpperCase()  + "member correspondence from API response")));
					}
				}
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
	@DisplayName("25375: Member GetLegacyInterventions Invalid Auth")
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

				Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_LEGACY_INTERVENTIONS_GET_ENDPOINT,
						new Object[]{memberId}, 401, HTTP_401_UNAUTHORIZED);
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
	@DisplayName("25739: Member GetLegacyInterventions Invalid Parm")
	@Order(3)
	public List<DynamicNode>  invalidParm() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "invalid";

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_LEGACY_INTERVENTIONS_GET_ENDPOINT,
						new Object[]{memberId}, 400, HTTP_400_BAD_REQUEST);
				apiGetStep.run();

				// validate the API message
				final String EXPECTED_MSG = "\"Member id "+  memberId +" not found!\"";
				String actualMsg = apiGetStep.getResponse().then().extract().asString();
				apiGetStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
				resetApiInfo();  // Reset the API information and test validation
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
	@DisplayName("25382: Member GetLegacyInterventions Header Missing")
	@Order(4)
	public List<DynamicNode> missingHeader()
	{
		String memberId = UUID.randomUUID().toString();

		ApiGetStep apiGetStep= new ApiGetStep(getGenericHeaders(), MEMBER_LEGACY_INTERVENTIONS_GET_ENDPOINT,
				new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("25381: Member GetLegacyInterventions Invalid Header")
	@Order(5)
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

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME + "invalid", subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_LEGACY_INTERVENTIONS_GET_ENDPOINT,
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
	@DisplayName("25383: Member GetLegacyInterventions Valid Parm With Incorrect Header Value")
	@Order(6)
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
				memberId =  MemberLegacyInterventionQueries.getRandomMemberWithLegacyIntervention(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME + "invalid", subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_LEGACY_INTERVENTIONS_GET_ENDPOINT,
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
}
