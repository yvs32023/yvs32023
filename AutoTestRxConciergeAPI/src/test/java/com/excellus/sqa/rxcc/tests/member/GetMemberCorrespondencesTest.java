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
 * 
 * @author Manish Sharma (msharma)
 * @since 08/16/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("CORRESPONDENCE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetMemberCorrespondences")
public class GetMemberCorrespondencesTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(GetMemberCorrespondencesTest.class);
	private String subscriptionName;

	@TestFactory
	@DisplayName("20940: GetMemberCorrespondences ALL Tenant Happy Path")
	@Order(1)
	public List<DynamicNode> happyPath() throws JsonProcessingException {

		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();
				List<DynamicNode> testSub = new ArrayList<DynamicNode>();

				//Query Cosmos for member Id with correspondence for all tenants
				List<GenericCount> memberWithCorrespondenceAndIntervention = MemberCorrespondenceQueries.getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionName, 1);

				//if memberWithCorrespondenceAndIntervention list is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (memberWithCorrespondenceAndIntervention.isEmpty())
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				String memberId = memberWithCorrespondenceAndIntervention.get(0).getId();
				String interventionId = memberWithCorrespondenceAndIntervention.get(0).getIntId();

				//Query Cosmos for retrieving the member correspondence for all tenants with respective member id
				List<MemberCorrespondence>  expectedMemberCorrespondences = MemberCorrespondenceQueries.getALLTenantMemberCorrespondences(subscriptionName,memberId,interventionId);

				//Log that the API call is starting
				logger.info("Starting API call");

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)), MEMBER_CORRESPONDENCES_GET_ENDPOINT, new Object[] {memberId,interventionId}, 200, null);
				apiGetStep.run();

				//Log that the API call has completed successfully
				logger.debug("API call completed successfully");

				List<MemberCorrespondence> actualMemberCorrespondences = apiGetStep.convertToJsonDTOs(MemberCorrespondence.class);

				if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED)
				{
					return apiGetStep.getTestResults();
				}
				else {
					test.addAll(apiGetStep.getTestResults());
				}

				for ( MemberCorrespondence expected : expectedMemberCorrespondences)
				{
					boolean found = false;
					for (MemberCorrespondence actual : actualMemberCorrespondences ) 
					{
						if ( expected.equals(actual))
						{
							found = true;
							testSub.add(dynamicContainer("Member id / Id [" + expected.getMemberId() + " / " +  expected.getInterventionId() + "]", expected.compare(actual)));
							break;
						}
					}
					if ( !found )
					{
						testSub.add(dynamicTest("Member id / Id [" + expected.getMemberId() + " / " +  expected.getInterventionId() + "]", fail("Unable to find the member correspondence from API response")));
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
			resetApiInfo();  // Reset the API information and test validation results
		}
		return 	test;
	}

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("20942: GetMemberCorrespondences Happy Path with Param Invalid")
	@Order(3)
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
				String interventionId = "invalid";

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_CORRESPONDENCES_GET_ENDPOINT, new Object[]{memberId,interventionId}, 400, HTTP_400_BAD_REQUEST);
				apiGetStep.run();

				// validate the API message
				final String EXPECTED_MSG = "\"GetCorrespondence - Member id "+  memberId +" not found\"";
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
	@DisplayName("20943: GetMemberCorrespondences Parm Missing")
	@Order(3)
	public List<DynamicNode> missingParam() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "";
				String interventionId = "";

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_CORRESPONDENCES_GET_ENDPOINT, new Object[]{memberId,interventionId}, 400, HTTP_400_BAD_REQUEST);

				apiGetStep.run();
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
	@DisplayName("20947: GetMemberCorrespondences Invalid Auth")
	@Order(4)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String interventionId = UUID.randomUUID().toString();

			Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionName));
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_CORRESPONDENCES_GET_ENDPOINT, new Object[]{memberId,interventionId}, 401, HTTP_401_UNAUTHORIZED);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("20952: GetMemberCorrespondences Header Null")
	@Order(5)
	public List<DynamicNode>  nullHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String interventionId = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders();
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_CORRESPONDENCES_GET_ENDPOINT, new Object[]{memberId,interventionId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("20953: GetMemberCorrespondences Invalid Header")
	@Order(6)
	public List<DynamicNode> invalidHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String interventionId = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME+"invalid", subscriptionName));
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_CORRESPONDENCES_GET_ENDPOINT, new Object[]{memberId,interventionId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("20954: GetMemberCorrespondences Header Missing")
	@Order(7)
	public List<DynamicNode> missingHeader()
	{
		String memberId = UUID.randomUUID().toString();
		String interventionId = UUID.randomUUID().toString();

		ApiGetStep apiGetStep= new ApiGetStep(getGenericHeaders(), MEMBER_CORRESPONDENCES_GET_ENDPOINT, new Object[]{memberId,interventionId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("20955: GetMemberCorrespondences Valid Parm With Incorrect Header Value")
	@Order(8)
	public List<DynamicNode>  incorrectHeaderValue() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				//Query Cosmos for member Id with correspondence (exe)
				List<GenericCount> memberWithCorrespondenceAndIntervention = MemberCorrespondenceQueries.getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionName, 1);

				//if memberWithCorrespondenceAndIntervention list is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (memberWithCorrespondenceAndIntervention.isEmpty())
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				String memberId = memberWithCorrespondenceAndIntervention.get(0).getId();
				String interventionId = memberWithCorrespondenceAndIntervention.get(0).getIntId();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME+"invalid", subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_CORRESPONDENCES_GET_ENDPOINT, new Object[]{memberId,interventionId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

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
	@DisplayName("43450: GetMemberCorrespondences Partial Parm Missing (Missing Member Id)")
	@Order(9)
	public List<DynamicNode> missingPartialParam() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				//Query Cosmos for member Id with correspondence 
				List<GenericCount> memberWithCorrespondenceAndIntervention = MemberCorrespondenceQueries.getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionName, 1);

				//if memberWithCorrespondenceAndIntervention list is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (memberWithCorrespondenceAndIntervention.isEmpty())
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				String memberId = "";
				String interventionId = memberWithCorrespondenceAndIntervention.get(0).getIntId();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_CORRESPONDENCES_GET_ENDPOINT, new Object[]{memberId,interventionId}, 404, HTTP_404_RESOURCE_NOT_FOUND);

				apiGetStep.run();
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

}
