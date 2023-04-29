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
import java.util.stream.Collectors;

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
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Header;
import io.restassured.http.Headers;


/**
 * Required Header: X-RXCC-SUB <ehp|exe>
 * Request Parameters :  memberId 
 * 
 * GET https://apim-lbs-rxc-dev-east-001.azure-api.net/api/member/members/{memberId}/rxclaims/[?adjudicationDateStart]
 * 
 * @author Manish Sharma (msharma)
 * @since 04/08/2022
 */
@SuppressWarnings("unused")
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_RXCLAIM")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetRxClaims")
public class GetRxClaimsTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(GetRxClaimsTest.class);
	private String subscriptionName;

	@TestFactory
	@DisplayName("3578: GetMemberRxclaims Happy Path  With Default Adj Date Start Date 18 month ago from today if not submitted. (ALL member)")
	@Order(1)
	public List<DynamicNode> happypath  () throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();
				List<DynamicNode> testSub = new ArrayList<>();

				String  adjudicationDateStart = "";
				String memberId = "";
				memberId = MemberRxClaimQueries.getRandomMemberWithRxClaim(subscriptionName);
				
				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				//Query Cosmos
				List<MemberRxclaim> expectedMemberRxclaims = MemberRxClaimQueries.getMemberRxclaim(memberId); 

				List<MemberRxclaim> expectedFinal = expectedMemberRxclaims.stream()
						.filter(claim ->MemberRxClaimQueries.compareDate(claim.getAdjudicationDate(),adjudicationDateStart))
						.collect(Collectors.toList());

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)), MEMBER_RX_CLAIMS_GET_ENDPOINT, new Object[] {memberId, adjudicationDateStart}, 200, null);
				apiGetStep.run();

				List<MemberRxclaim> actualMemberRxclaims = apiGetStep.convertToJsonDTOs(MemberRxclaim.class);

				if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED)
				{
					return apiGetStep.getTestResults();
				}

				for ( MemberRxclaim expected : expectedFinal)

				{
					boolean found = false;
					for (MemberRxclaim actual : actualMemberRxclaims ) 
					{
						if ( expected.equals(actual) )
						{
							found = true;
							testSub.add(dynamicContainer("MemberRxclaim memberid [" + expected.getId() + "]", expected.compare(actual)));

							break;
						}
					}

					if ( !found )
					{
						testSub.add(dynamicTest("MemberRxclaim id  [" + expected.getId() + "]", fail("Unable to find the MemberRxclaim from API response")));
					}
				}
				test.add(dynamicContainer(subscriptionName.toUpperCase() + " : " + memberId, testSub));
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3578: GetMemberRxclaims Happy Path With Adj Date Start Date 6 month (ALL member)")
	@Order(2)
	public List<DynamicNode> happypathSixMonths  () throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();
				List<DynamicNode> testSub = new ArrayList<>();

				String adjudicationDateStart = MemberRxClaimQueries.getGivenMonthsAgo(6);
				String memberId = "";
				memberId = MemberRxClaimQueries.getRandomMemberWithRxClaim(subscriptionName);
				
				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				//Query Cosmos
				List<MemberRxclaim> expectedMemberRxclaims = MemberRxClaimQueries.getMemberRxclaim(memberId); 

				List<MemberRxclaim> expectedFinal = expectedMemberRxclaims.stream()
						.filter(claim -> MemberRxClaimQueries.compareDate(claim.getAdjudicationDate(),adjudicationDateStart))
						.collect(Collectors.toList());

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)), MEMBER_RX_CLAIMS_GET_ENDPOINT, new Object[] {memberId, adjudicationDateStart}, 200, null);
				apiGetStep.run();

				List<MemberRxclaim> actualMemberRxclaims = apiGetStep.convertToJsonDTOs(MemberRxclaim.class);

				if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED)
				{
					return apiGetStep.getTestResults();
				}

				for ( MemberRxclaim expected : expectedFinal)
				{
					boolean found = false;
					for (MemberRxclaim actual : actualMemberRxclaims ) 
					{
						if ( expected.equals(actual) )
						{
							found = true;
							testSub.add(dynamicContainer("MemberRxclaim memberid [" + expected.getId() + "]", expected.compare(actual)));

							break;
						}
					}

					if ( !found )
					{
						testSub.add(dynamicTest("MemberRxclaim id  [" + expected.getId() + "]", fail("Unable to find the MemberRxclaim from API response")));
					}
				}
				test.add(dynamicContainer(subscriptionName.toUpperCase() + " : " + memberId, testSub));
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3578: GetMemberRxclaims Happy Path With Adj Date Start Date 3 month (ALL member)")
	@Order(3)
	public List<DynamicNode> happypathThreeMonths  () throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();
				List<DynamicNode> testSub = new ArrayList<>();

				String adjudicationDateStart = MemberRxClaimQueries.getGivenMonthsAgo(3);
				String memberId = "";
				memberId = MemberRxClaimQueries.getRandomMemberWithRxClaim(subscriptionName);
				
				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				//Query Cosmos
				List<MemberRxclaim> expectedMemberRxclaims = MemberRxClaimQueries.getMemberRxclaim(memberId); 

				List<MemberRxclaim> expectedFinal = expectedMemberRxclaims.stream()
						.filter(claim -> MemberRxClaimQueries.compareDate(claim.getAdjudicationDate(),adjudicationDateStart))
						.collect(Collectors.toList());

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)), MEMBER_RX_CLAIMS_GET_ENDPOINT, new Object[] {memberId, adjudicationDateStart}, 200, null);
				apiGetStep.run();

				List<MemberRxclaim> actualMemberRxclaims = apiGetStep.convertToJsonDTOs(MemberRxclaim.class);

				if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED)
				{
					return apiGetStep.getTestResults();
				}

				for ( MemberRxclaim expected : expectedFinal)
				{
					boolean found = false;
					for (MemberRxclaim actual : actualMemberRxclaims ) 
					{
						if ( expected.equals(actual) )
						{
							found = true;
							testSub.add(dynamicContainer("MemberRxclaim memberid [" + expected.getId() + "]", expected.compare(actual)));

							break;
						}
					}

					if ( !found )
					{
						testSub.add(dynamicTest("MemberRxclaim id  [" + expected.getId() + "]", fail("Unable to find the MemberRxclaim from API response")));
					}
				}
				test.add(dynamicContainer(subscriptionName.toUpperCase() + " : " + memberId, testSub));
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("3565: GetRxClaims Invalid Auth")
	@Order(4)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String  adjudicationDateStart = "";

			Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionName));

			ApiGetStep apiGetStep= new ApiGetStep(headers, MEMBER_RX_CLAIMS_GET_ENDPOINT,
					new Object[]{memberId,adjudicationDateStart}, 401, HTTP_401_UNAUTHORIZED);
			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3566: GetRxClaims Invalid Method")
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
				ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_RX_CLAIMS_GET_ENDPOINT,null,
						new Object[]{expected.getMemberId() , adjudicationDateStart}, 404, HTTP_404_RESOURCE_NOT_FOUND);
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
	@DisplayName("3580: GetRxClaims Invalid Parm")
	@Order(7)
	public List<DynamicNode> invalidParam() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String invalidMemberId = UUID.randomUUID().toString();

			String  adjudicationDateStart = "";

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_RX_CLAIMS_GET_ENDPOINT,
					new Object[]{invalidMemberId, adjudicationDateStart}, 200, HTTP_200_OK); //Change 400 t0 200, as per Venkata comment on the defect
			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3583: GetRxClaims  ALL Parm Combo Not Exist")
	@Order(8)
	public List<DynamicNode> partialParamComboExists() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();

			String  adjudicationDateStart = "19941212";

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_RX_CLAIMS_GET_ENDPOINT,
					new Object[]{memberId, adjudicationDateStart}, 200, HTTP_200_OK); //Change 400 t0 200, as per Venkata comment on the defect
			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3589: GetRxClaims Invalid Header")
	@Order(9)
	public List<DynamicNode> invalidHeader()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String  adjudicationDateStart = "";

			Headers headers = new Headers(new Header(API_HEADER_NAME + "invalid", subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(headers), MEMBER_RX_CLAIMS_GET_ENDPOINT , new Object[]{memberId,adjudicationDateStart}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	} 


	@TestFactory
	@DisplayName("3590: GetRxClaims Header Missing")
	@Order(10)
	public List<DynamicNode> missingHeader()
	{
		String memberId = UUID.randomUUID().toString();
		String  adjudicationDateStart = "";

		ApiGetStep apiGetStep= new ApiGetStep(getGenericHeaders(), MEMBER_RX_CLAIMS_GET_ENDPOINT,
				new Object[]{memberId,adjudicationDateStart}, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}



	@TestFactory
	@DisplayName("3591: GetRxClaims Valid Parm With Incorrect Header Value")
	@Order(11)
	public  List<DynamicNode> incorrectHeaderValue() throws JsonProcessingException 
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

				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_RX_CLAIMS_GET_ENDPOINT, new Object[]{expected.getMemberId(),adjudicationDateStart}, 500, HTTP_500_INTERNAL_SERVER_ERR);
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
