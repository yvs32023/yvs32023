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
import com.excellus.sqa.rxcc.cosmos.MemberNoteQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.MemberNote;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * https://apim-lbs-rxc-dev-east-001.azure-api.net/api/member/members/{memberId}/notes/
 * 
 *Required Header: X-RXCC-SUB <ehp|exe>
 *Request Parameters :  memberId 
 * 
 * @author Manish Sharma (msharma)
 * @since 03/28/2022
 */
@SuppressWarnings("unused")
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_NOTE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetMemberNotes")
public class GetNotesTest extends RxConciergeAPITestBaseV2 
{
	private static final Logger logger = LoggerFactory.getLogger(GetNotesTest.class);
	private String subscriptionName;	

	@TestFactory
	@DisplayName("3618: GetMemberNotes Happy Path (ALL member)")
	@Order(1)
	public List<DynamicNode> happypathAll  () throws JsonProcessingException 	
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "";
				memberId =  MemberNoteQueries.getRandomMemberWithNote(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				//Query Cosmos
				List<MemberNote> expectedMemberNotes = MemberNoteQueries.getMemberNote(subscriptionName, memberId);

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)), MEMBER_NOTES_GET_ENDPOINT, new Object[] {memberId}, 200, null);
				apiGetStep.run();

				List<MemberNote> actualMemberNotes = apiGetStep.convertToJsonDTOs(MemberNote.class);

				for ( MemberNote expected : expectedMemberNotes)
				{
					boolean found = false;
					for (MemberNote actual : actualMemberNotes ) 
					{
						if ( expected.equals(actual) )
						{
							found = true;
							apiGetStep.getTestResults().add(dynamicContainer("Member Id [" + expected.getMemberId() + "]", expected.compare(actual)));
							break;
						}
					}
					if ( !found )
					{
						apiGetStep.getTestResults().add(dynamicTest("Member Id  [" + expected.getMemberId() + "]", fail("Unable to fund the MemberNote from API response")));
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
	@DisplayName("3503: GetNotes Invalid Auth")
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
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_NOTES_GET_ENDPOINT,
					new Object[]{memberId}, 401, HTTP_401_UNAUTHORIZED);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3504: GetNotes Invalid Method")
	@Order(3)
	public List<DynamicNode>  invalidRestApiPostMethod() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "";
				memberId =  MemberNoteQueries.getRandomMemberWithNote(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_NOTES_GET_ENDPOINT,null,
						new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

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
	@DisplayName("3620: GetNotes Invalid Parm")
	@Order(4)
	public List<DynamicNode>  invalidPathParam()  throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = "invalid";

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_NOTES_GET_ENDPOINT,
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
	@DisplayName("3623: GetNotes  ALL Parm Combo Not Exist")
	@Order(5)
	public List<DynamicNode>  partialParamComboExists() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_NOTES_GET_ENDPOINT,
					new Object[]{memberId}, 400, HTTP_400_BAD_REQUEST);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3629: GetNotes Invalid Header")
	@Order(6)
	public  List<DynamicNode> invalidHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{

			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME+"invalid", subscriptionName));
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_NOTES_GET_ENDPOINT,
					new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3630: GetNotes Header Missing")
	@Order(7)
	public List<DynamicNode> missingHeader()
	{
		String memberId = UUID.randomUUID().toString();

		ApiGetStep apiGetStep= new ApiGetStep(getGenericHeaders(), MEMBER_INTERVENTIONS_GET_ENDPOINT,
				new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("3631: GetNotes Valid Parm With Incorrect Header Value")
	@Order(8)
	public  List<DynamicNode>  validParmInvalidHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "";
				memberId =  MemberNoteQueries.getRandomMemberWithNote(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME + "invalid", subscriptionName));
				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_NOTES_GET_ENDPOINT,
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
