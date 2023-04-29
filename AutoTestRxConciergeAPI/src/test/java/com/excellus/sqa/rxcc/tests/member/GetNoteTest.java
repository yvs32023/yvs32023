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
 *
 * GET https://apim-lbs-rxc-tst-east-001.azure-api.net/api/member/members/{memberId}/notes/{noteId}
 *
 *Required Header: X-RXCC-SUB <ehp|exe>
 *Request Parameters :  memberId & noteId
 *
 * 
 * @author Manish Sharma (msharma)
 * @since 03/24/2022
 */
@SuppressWarnings("unused")
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_NOTE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetMemberNote")
public class GetNoteTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(GetMemberTest.class);
	private String subscriptionName;

	@TestFactory
	@DisplayName("3680: GetNote Happy Path (ALL member)")
	@Order(1)
	public List<DynamicNode> happyPathGetNoteAll() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "";
				memberId = MemberNoteQueries.getRandomMemberWithNote(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				List<MemberNote> list = MemberNoteQueries.getMemberNote(subscriptionName, memberId);

				//Query Cosmos with one expected member that should have note
				MemberNote expected =list.get(0);

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)),
						MEMBER_NOTE_GET_ENDPOINT, new Object[] { expected.getMemberId() , expected.getId()}, 200, null);
				apiGetStep.run();

				MemberNote actual = apiGetStep.convertToJsonDTO(MemberNote.class);
				apiGetStep.getTestResults().add( dynamicContainer("MemberNote DTO", expected.compare(actual)));

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
	@DisplayName("3615: GetNote Invalid Auth")
	@Order(2)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String id = UUID.randomUUID().toString();

			Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionName));

			ApiGetStep apiGetStep= new ApiGetStep(headers, MEMBER_NOTE_GET_ENDPOINT,
					new Object[]{memberId,id}, 401, HTTP_401_UNAUTHORIZED);
			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3616: GetNote Invalid Method")
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

				List<MemberNote> list = MemberNoteQueries.getMemberNote(subscriptionName, memberId);

				//Query Cosmos with one expected member that should have note
				MemberNote expected =list.get(0);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_NOTE_GET_ENDPOINT,null,
						new Object[]{expected.getMemberId() , expected.getId()}, 404, HTTP_404_RESOURCE_NOT_FOUND);
				apiPostStep.run();

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
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
	@DisplayName("3682: GetNote Invalid Parm(Both Member Id & Note Id)")
	@Order(4)
	public List<DynamicNode> invalidParam() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String invalidMemberId = "invalid";
			String invalidNoteId = "invalid";

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_NOTE_GET_ENDPOINT,
					new Object[]{invalidMemberId, invalidNoteId}, 400, HTTP_400_BAD_REQUEST);
			apiGetStep.run();

			// validate the API message
			final String EXPECTED_MSG = "\"Member id "+  invalidMemberId +" not found!\"";
			String actualMsg = apiGetStep.getResponse().then().extract().asString();
			apiGetStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3686: GetNote  Partial Parm Combo Exist(Invalid Note Id)")
	@Order(5)
	public List<DynamicNode> partialParamComboExists() throws JsonProcessingException 
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

				List<MemberNote> list = MemberNoteQueries.getMemberNote(subscriptionName, memberId);

				//Query Cosmos with one expected member that should have note
				MemberNote expected =list.get(0);
				String id = UUID.randomUUID().toString();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));
				ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_NOTE_GET_ENDPOINT,null,
						new Object[]{expected.getMemberId() , id}, 404, HTTP_404_RESOURCE_NOT_FOUND);
				apiPostStep.run();

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
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
	@DisplayName("3691: GetNote Invalid Header")
	@Order(6)
	public List<DynamicNode> invalidHeader()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String noteId = UUID.randomUUID().toString();

			Headers headers = new Headers(new Header(API_HEADER_NAME + "invalid", subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(headers), MEMBER_NOTE_GET_ENDPOINT , new Object[]{memberId,noteId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	} 

	@TestFactory
	@DisplayName("3692: GetNote Header Missing")
	@Order(7)
	public List<DynamicNode> missingHeader()
	{
		String memberId = UUID.randomUUID().toString();
		String noteId = UUID.randomUUID().toString();

		ApiGetStep apiGetStep= new ApiGetStep(getGenericHeaders(), MEMBER_NOTE_GET_ENDPOINT,
				new Object[]{memberId, noteId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("3693: GetNote Valid Parm With Incorrect Header Value")
	@Order(8)
	public List<DynamicNode> incorrectHeaderValue() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName().toUpperCase();

				String memberId = "";
				memberId =  MemberNoteQueries.getRandomMemberWithNote(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				List<MemberNote> list = MemberNoteQueries.getMemberNote(subscriptionName, memberId);

				//Query Cosmos with one expected member that should have note
				MemberNote expected =list.get(0);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, "abc"));

				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_NOTE_GET_ENDPOINT, new Object[]{expected.getMemberId(),expected.getId()}, 500, HTTP_500_INTERNAL_SERVER_ERR);
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
			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}

}
