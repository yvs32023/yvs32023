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
import com.excellus.sqa.rxcc.cosmos.MemberCorrespondenceQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * 
 * GetCorrespondence
 * Get Member type Correspondence by {id} for {memberId} and {interventionId}
 * 
 * GET https://apim-lbs-rxc-dev-east-001.azure-api.net/api/member/members/{memberId}/interventions/{interventionId}/correspondences/{id}
 *
 *Cosmos rxcc-tenant: member
 *Required Header: X-RXCC-SUB <ehp | exe> to specify Tenant
 *Request Parameters :  memberId , interventionId & id
 * 
 * @author Manish Sharma (msharma)
 * @since 08/15/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("CORRESPONDENCE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetMemberCorrespondence")
public class GetMemberCorrespondenceTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(GetMemberCorrespondenceTest.class);
	private String subscriptionName;

	/*
	 * Positive test cases
	 */

	@TestFactory
	@DisplayName("20956: GetMemberCorrespondence Happy Path (ALL member)")
	@Order(1)
	public List<DynamicNode> happyPathGetCorrespondenceEXE() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				//Query Cosmos with one expected member that should have correspondence
				String memberId = "";
				memberId = MemberCorrespondenceQueries.getRandomMembersWithCorrespondenceMoreThanX(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				//Query Cosmos for a member that should have type as correspondence
				MemberCorrespondence expected = MemberCorrespondenceQueries.getALLTenantMemberCorrespondences(subscriptionName,memberId).get(0);

				//Log that the API call is starting
				logger.info("Starting API call");

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)), MEMBER_CORRESPONDENCE_GET_ENDPOINT, new Object[] {expected.getMemberId() , expected.getInterventionId(), expected.getId()}, 200, null);
				apiGetStep.run();

				//Log that the API call has completed successfully
				logger.debug("API call completed successfully");

				MemberCorrespondence actual = apiGetStep.convertToJsonDTO(MemberCorrespondence.class);

				if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED)
				{
					return apiGetStep.getTestResults();
				}

				//return expected.compare(actual);
				test.add(dynamicContainer(subscriptionName.toUpperCase() + " : " + memberId, expected.compare(actual)));
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}

	/*
	 * Negative test cases
	 */

	@TestFactory
	@DisplayName("20963: GetMemberCorrespondence Invalid Auth")
	@Order(2)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String interventionId = UUID.randomUUID().toString();
			String id = UUID.randomUUID().toString();

			Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionName));
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_CORRESPONDENCE_GET_ENDPOINT, new Object[]{memberId,interventionId,id}, 401, HTTP_401_UNAUTHORIZED);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("20968: GetMemberCorrespondence Header Null")
	@Order(3)
	public List<DynamicNode>  nullHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String interventionId = UUID.randomUUID().toString();
			String id = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders();
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_CORRESPONDENCE_GET_ENDPOINT, new Object[]{memberId,interventionId,id}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("20969: GetMemberCorrespondence Invalid Header")
	@Order(4)
	public List<DynamicNode> invalidHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String interventionId = UUID.randomUUID().toString();
			String id = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME+"invalid", subscriptionName));
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_CORRESPONDENCE_GET_ENDPOINT, new Object[]{memberId,interventionId,id}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("20970: GetMemberCorrespondence Header Missing")
	@Order(6)
	public List<DynamicNode> missingHeader()
	{
		String memberId = UUID.randomUUID().toString();
		String interventionId = UUID.randomUUID().toString();
		String id = UUID.randomUUID().toString();

		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), MEMBER_CORRESPONDENCE_GET_ENDPOINT, new Object[]{memberId,interventionId,id}, 500, HTTP_500_INTERNAL_SERVER_ERR);

		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("20971: GetMemberCorrespondence Valid Parm With Incorrect Header Value")
	@Order(7)
	public List<DynamicNode>  incorrectHeaderValue() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				//Query Cosmos with one expected member that should have correspondence
				String memberId = "";
				memberId = MemberCorrespondenceQueries.getRandomMembersWithCorrespondenceMoreThanX(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				//Query Cosmos for a member that should have type as correspondence
				MemberCorrespondence expected = MemberCorrespondenceQueries.getALLTenantMemberCorrespondences(subscriptionName,memberId).get(0);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME+"invalid", subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_CORRESPONDENCE_GET_ENDPOINT, new Object[]{expected.getMemberId() , expected.getInterventionId(), expected.getId()}, 500, HTTP_500_INTERNAL_SERVER_ERR);

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
	@DisplayName("20972: GetMemberCorrespondence All Parm Combo Not Exist")
	@Order(8)
	public List<DynamicNode> allParamComboNotExists() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String randomMemberId = UUID.randomUUID().toString();
				String randomInterventionId = UUID.randomUUID().toString();
				String randomId = UUID.randomUUID().toString();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_CORRESPONDENCE_GET_ENDPOINT, new Object[]{randomMemberId, randomInterventionId, randomId}, 400, HTTP_400_BAD_REQUEST);
				apiGetStep.run();

				// validate the API message
				final String EXPECTED_MSG = "\"GetCorrespondence - Member id "+  randomMemberId +" not found\"";
				String actualMsg = apiGetStep.getResponse().then().extract().asString();
				apiGetStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
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



	@TestFactory
	@DisplayName("20972: GetMemberCorrespondence   Partial Parm Combo Exist (Random Intervention Id)")
	@Order(9)
	public List<DynamicNode> partialParamComboExistsEhp() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				//Query Cosmos with one expected member that should have correspondence
				String memberId = "";
				memberId = MemberCorrespondenceQueries.getRandomMembersWithCorrespondenceMoreThanX(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				//Query Cosmos for a member that should have type as correspondence
				MemberCorrespondence expected = MemberCorrespondenceQueries.getALLTenantMemberCorrespondences(subscriptionName,memberId).get(0);

				//InvalidInterventionId 
				String randomInterventionId = UUID.randomUUID().toString();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_CORRESPONDENCE_GET_ENDPOINT, new Object[]{expected.getMemberId() , randomInterventionId, expected.getId()}, 400, HTTP_400_BAD_REQUEST);

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
	@DisplayName("20972: GetMemberCorrespondence Partial Parm Combo Exist (Random Id)")
	@Order(10)
	public List<DynamicNode> partialParamComboExistsExe() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				//Query Cosmos with one expected member that should have correspondence
				String memberId = "";
				memberId = MemberCorrespondenceQueries.getRandomMembersWithCorrespondenceMoreThanX(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				//Query Cosmos for a member that should have type as correspondence
				MemberCorrespondence expected = MemberCorrespondenceQueries.getALLTenantMemberCorrespondences(subscriptionName,memberId).get(0);

				//InvalidId 
				String randomId = UUID.randomUUID().toString();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_CORRESPONDENCE_GET_ENDPOINT, new Object[]{expected.getMemberId() , expected.getInterventionId(), randomId}, 404, HTTP_404_NOT_FOUND);

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
