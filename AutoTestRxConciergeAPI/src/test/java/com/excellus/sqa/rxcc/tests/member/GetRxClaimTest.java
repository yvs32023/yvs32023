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
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.MemberRxClaimQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.MemberRxclaim;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * GET https://apim-lbs-rxc-dev-east-001.azure-api.net/api/member/members/{memberId}/rxclaims/{rxClaimId}[?adjudicationDateStart]
 * 
 * Required Header: X-RXCC-SUB <ehp|exe>
 * Request Parameters :  memberId, rxClaimId
 * 
 * rxClaimId is id in member type='rxclaim'
 * 
 * @author Manish Sharma (msharma)
 * @since 04/13/2022
 */
@SuppressWarnings("unused")
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_RXCLAIM")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetRxClaim")
public class GetRxClaimTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(GetRxClaimTest.class);
	private String subscriptionName; 

	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("3598: GetRxclaim Happy Path With Default Adj Date Start Date 18 month ago from today if not submitted. (ALL member)")
	@Order(1)
	public List<DynamicNode> happyPath() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String  adjudicationDateStart = "";

				//Query Cosmos with one expected member that should have rxclaimid within the last 18 months
				MemberRxclaim expected = MemberRxClaimQueries.getRandomMemberWithRxclaimWithAdjDateStart(subscriptionName);

				//if memberId is not found in MemberRxclaim, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(expected.getMemberId()))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)),
						MEMBER_RX_CLAIM_GET_ENDPOINT, new Object[] { expected.getMemberId(), expected.getId(), adjudicationDateStart}, 200, null);
				apiGetStep.run();

				MemberRxclaim actual = apiGetStep.convertToJsonDTO(MemberRxclaim.class);
				apiGetStep.getTestResults().add( dynamicContainer("MemberRxclaim DTO", expected.compare(actual)));

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
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

	@TestFactory
	@DisplayName("3598: GetRxclaim Happy Path (ALL member- 6 months ago)")
	@Order(2)
	public List<DynamicNode> happyPathSixMonths() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String adjudicationDateStart = MemberRxClaimQueries.getGivenMonthsAgo(6);

				//Query Cosmos with one expected member that should have rxclaimid within the last 6 months
				MemberRxclaim expected = MemberRxClaimQueries.getRandomMemberWithRxclaimWithAdjDateStart(subscriptionName, adjudicationDateStart);

				//if memberId is not found in MemberRxclaim, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(expected.getMemberId()))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				/// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)),
						MEMBER_RX_CLAIM_GET_ENDPOINT, new Object[] { expected.getMemberId(), expected.getId(), adjudicationDateStart}, 200, null);
				apiGetStep.run();

				MemberRxclaim actual = apiGetStep.convertToJsonDTO(MemberRxclaim.class);
				apiGetStep.getTestResults().add( dynamicContainer("MemberRxclaim DTO", expected.compare(actual)));

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
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

	@TestFactory
	@DisplayName("3598: GetRxclaim Happy Path (ALL member- 3 months ago)")
	@Order(3)
	public List<DynamicNode> happyPathThreeMonths() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String adjudicationDateStart = MemberRxClaimQueries.getGivenMonthsAgo(3);

				//Query Cosmos with one expected member that should have rxclaimid within the last 3 months
				MemberRxclaim expected = MemberRxClaimQueries.getRandomMemberWithRxclaimWithAdjDateStart(subscriptionName, adjudicationDateStart);

				//if memberId is not found in MemberRxclaim, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(expected.getMemberId()))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				/// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)),
						MEMBER_RX_CLAIM_GET_ENDPOINT, new Object[] { expected.getMemberId(), expected.getId(), adjudicationDateStart}, 200, null);
				apiGetStep.run();

				MemberRxclaim actual = apiGetStep.convertToJsonDTO(MemberRxclaim.class);
				apiGetStep.getTestResults().add( dynamicContainer("MemberRxclaim DTO", expected.compare(actual)));

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
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
	@DisplayName("3575: GetRxclaim Invalid Auth")
	@Order(4)
	public  List<DynamicNode> invalidAuth() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String rxclaimId = UUID.randomUUID().toString();

			String  adjudicationDateStart = "";

			Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionName));

			ApiGetStep apiGetStep= new ApiGetStep(headers, MEMBER_RX_CLAIM_GET_ENDPOINT,
					new Object[]{memberId,rxclaimId,adjudicationDateStart}, 401, HTTP_401_UNAUTHORIZED);
			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3576: GetRxclaim Invalid Method")
	@Order(5)
	public List<DynamicNode>  invalidRestApiPostMethod() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String  adjudicationDateStart = "";

				//Query Cosmos with one expected member that should have rxclaimid within the last 18 months
				MemberRxclaim expected = MemberRxClaimQueries.getRandomMemberWithRxclaimWithAdjDateStart(subscriptionName);

				//if memberId is not found in MemberRxclaim, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(expected.getMemberId()))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_RX_CLAIM_GET_ENDPOINT,null,
						new Object[]{expected.getMemberId() , expected.getId(), adjudicationDateStart}, 404, HTTP_404_RESOURCE_NOT_FOUND);
				apiPostStep.run();

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
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
	@DisplayName("3600: GetRxclaim Invalid Parm")
	@Order(6)
	public List<DynamicNode> invalidParam() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String invalidMemberId = UUID.randomUUID().toString();
			String invalidRxclaimId = UUID.randomUUID().toString();

			String  invalidadjudicationDateStart = "19911012";

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_RX_CLAIM_GET_ENDPOINT,
					new Object[]{  invalidMemberId, invalidRxclaimId, invalidadjudicationDateStart}, 404, HTTP_404_NOT_FOUND);
			apiGetStep.run();

			// validate the API message
			final String EXPECTED_MSG = "\"RxClaim for Member Id "+  invalidMemberId + " and RxClaimId "+ invalidRxclaimId +" not found!\"";

			String actualMsg = apiGetStep.getResponse().then().extract().asString();
			apiGetStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3604: GetRxclaim  Partial Parm Combo Exist")
	@Order(7)
	public List<DynamicNode> partialParamComboExists() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			//Query Cosmos with one expected member that should have rxclaimid within the last 18 months
			MemberRxclaim expected = MemberRxClaimQueries.getRandomMemberWithRxclaimWithAdjDateStart(subscriptionName);

			//if id is not found in MemberRxclaim, throw the "NO TEST DATA FOUND"and skip the test
			if (StringUtils.isBlank(expected.getId()))
			{
				test.add( dynamicContainer(subscriptionName.toUpperCase(),  
						Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
				continue;
			}

			String invalidMemberId = UUID.randomUUID().toString();
			String rxclaimId = expected.getId();

			//Valid AdjDateStart
			String  adjudicationDateStart = "";

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_RX_CLAIM_GET_ENDPOINT,
					new Object[]{  invalidMemberId, rxclaimId, adjudicationDateStart}, 404, HTTP_404_NOT_FOUND);
			apiGetStep.run();

			// validate the API message
			final String EXPECTED_MSG = "\"RxClaim for Member Id "+  invalidMemberId + " and RxClaimId "+ rxclaimId +" not found!\"";
			String actualMsg = apiGetStep.getResponse().then().extract().asString();
			apiGetStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3609: GetRxclaim Invalid Header")
	@Order(8)
	public List<DynamicNode> invalidHeader()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String rxclaimId = UUID.randomUUID().toString();
			String  adjudicationDateStart = "";

			Headers headers = new Headers(new Header(API_HEADER_NAME + "invalid", subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(headers), MEMBER_RX_CLAIM_GET_ENDPOINT , new Object[]{memberId,rxclaimId,adjudicationDateStart}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	} 

	@TestFactory
	@DisplayName("3610: GetRxclaim Header Missing")
	@Order(9)
	public List<DynamicNode> missingHeader()
	{
		String memberId = UUID.randomUUID().toString();
		String rxclaimId = UUID.randomUUID().toString();
		String  adjudicationDateStart = "";

		ApiGetStep apiGetStep= new ApiGetStep(getGenericHeaders(), MEMBER_RX_CLAIM_GET_ENDPOINT,
				new Object[]{memberId,rxclaimId,adjudicationDateStart}, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}


	@TestFactory
	@DisplayName("3611: GetRxclaim Valid Parm With Incorrect Header Value")
	@Order(10)
	public List<DynamicNode> incorrectHeaderValue() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName().toUpperCase();

				String  adjudicationDateStart = "";

				//Query Cosmos with one expected member that should have rxclaimid within the last 18 months
				MemberRxclaim expected = MemberRxClaimQueries.getRandomMemberWithRxclaimWithAdjDateStart(subscriptionName);

				//if memberId is not found in MemberRxclaim, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(expected.getMemberId()))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, "abc"));

				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_RX_CLAIM_GET_ENDPOINT, new Object[]{expected.getMemberId(),expected.getId(),adjudicationDateStart}, 500, HTTP_500_INTERNAL_SERVER_ERR);
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
