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
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * 
 * GetAllCorrespondences
 * Get all correspondences for {memberId} in the Member container. (type=correspondence)
 * 
 * GET https://apim-lbs-rxc-tst-east-001.azure-api.net/api/member/members/{memberId}/correspondences
 *
 *Cosmos rxcc-tenant: member
 *Required Header: X-RXCC-SUB <ehp | exe> to specify Tenant
 *Request Parameters :  memberId 
 * 
 * @author Manish Sharma (msharma)
 * @since 08/15/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("CORRESPONDENCE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetMemberAllCorrespondences")
public class GetMemberAllCorrespondencesTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(GetMemberAllCorrespondencesTest.class);
	private String subscriptionName; 

	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("40451: GetMemberAllCorrespondences Happy Path (ALL Member")
	@Order(1)
	public List<DynamicNode> happyPath() throws JsonProcessingException {

		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();
				List<DynamicNode> testSub = new ArrayList<>();

				//Query Cosmos for member Id with correspondence for all tenants
				List<GenericCount> memberWithCorrespondence = MemberCorrespondenceQueries.getMembersWithCorrespondenceMoreThanX(subscriptionName, 3);
			
				//if memberWithCorrespondence list is empty for specific tenant, throw the "NO TEST DATA FOUND"and skip the test
				if (memberWithCorrespondence.isEmpty())
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}
				
				String memberId = "";
				memberId = memberWithCorrespondence.get(0).getId();

				//Query Cosmos for retrieving the member correspondence from all tenant with provided subscription name
				List<MemberCorrespondence>  expectedMemberCorrespondences = MemberCorrespondenceQueries.getALLTenantMemberCorrespondences(subscriptionName, memberId);
				
				//Log that the API call is starting
				logger.info("Starting API call");

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)), MEMBER_ALL_CORRESPONDENCES_GET_ENDPOINT, new Object[] {memberId}, 200, null);
				apiGetStep.run();
				
				//Log that the API call has completed successfully
				logger.debug("API call completed successfully");

				List<MemberCorrespondence> actualMemberCorrespondences = apiGetStep.convertToJsonDTOs(MemberCorrespondence.class);

				if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED)
				{
					return apiGetStep.getTestResults();
				}

				for ( MemberCorrespondence expected : expectedMemberCorrespondences)
				{
					boolean found = false;
					for (MemberCorrespondence actual : actualMemberCorrespondences ) 
					{
						if ( expected.equals(actual) )
						{
							found = true;
							testSub.add(dynamicContainer("Member id / Id [" + expected.getMemberId() + " / " +  expected.getId() + "]", expected.compare(actual)));
							break;
						}
					}
					if ( !found )
					{
						testSub.add(dynamicTest("Member id / Id [" + expected.getMemberId() + " / " +  expected.getId() + "]", fail("Unable to find the member correspondence from API response")));
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
		return 	test;
	}


	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("42538: GetMemberAllCorrespondences Invalid Auth")
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
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_ALL_CORRESPONDENCES_GET_ENDPOINT,
					new Object[]{memberId}, 401, HTTP_401_UNAUTHORIZED);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("42539: GetMemberAllCorrespondences Header Null")
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
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_ALL_CORRESPONDENCES_GET_ENDPOINT,
					new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("42539: GetMemberAllCorrespondences Invalid Header")
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
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_ALL_CORRESPONDENCES_GET_ENDPOINT,
					new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("42539: GetMemberAllCorrespondences Header Missing")
	@Order(5)
	public List<DynamicNode> missingHeader()
	{
		String memberId = UUID.randomUUID().toString();

		ApiGetStep apiGetStep= new ApiGetStep(getGenericHeaders(), MEMBER_ALL_CORRESPONDENCES_GET_ENDPOINT,
				new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("42539: GetMemberAllCorrespondences Valid Parm With Incorrect Header Value")
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

				//Query Cosmos for member Id with correspondence for all tenants
				List<GenericCount> memberWithCorrespondence = MemberCorrespondenceQueries.getMembersWithCorrespondenceMoreThanX(subscriptionName, 3);
				
				//if memberWithCorrespondence list is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (memberWithCorrespondence.isEmpty())
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}
				
				String memberId = "";
				memberId = memberWithCorrespondence.get(0).getId();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME + "invalid", subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_ALL_CORRESPONDENCES_GET_ENDPOINT,
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
	@DisplayName("42535: GetMemberAllCorrespondences All Parm Combo Not Exist")
	@Order(7)
	public List<DynamicNode> allParamComboNotExists() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_ALL_CORRESPONDENCES_GET_ENDPOINT,
					new Object[]{memberId}, 400, HTTP_400_BAD_REQUEST);

			apiGetStep.run();

			// validate the API message
			final String EXPECTED_MSG = "\"Member id "+  memberId +" not found!\"";
			String actualMsg = apiGetStep.getResponse().then().extract().asString();
			apiGetStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]",
					() -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}
}
