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
import com.excellus.sqa.rxcc.cosmos.MemberInterventionNoteQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.MemberInterventionNote;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 10/12/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("NOTE")
@Tag("INTERVENTIONNOTE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetMemberInterventionNote")
public class GetMemberInterventionNoteTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(GetMemberInterventionNoteTest.class);
	private String subscriptionName; 


	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("34480: GetMemberInterventionNote Happy Path (ALL member)")
	@Order(1)
	public List<DynamicNode> happyPathGetMemberInterventionNoteALL() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "";
				memberId = MemberInterventionNoteQueries.getRandomMemberWithMemberInterventionNote(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				List<MemberInterventionNote> list = MemberInterventionNoteQueries.getInterventionNote(subscriptionName, memberId);

				//Query Cosmos with one expected member that should have interventionnote
				MemberInterventionNote expected =list.get(0);

				//Log that the API call is starting
				logger.info("Starting API call");

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)),
						MEMBER_INTERVENTION_NOTE_GET_ENDPOINT, new Object[] { expected.getMemberId() ,expected.getInterventionId(), expected.getId()}, 200, null);
				apiGetStep.run();

				//Log that the API call has completed successfully
				logger.debug("API call completed successfully");

				MemberInterventionNote actual = apiGetStep.convertToJsonDTO(MemberInterventionNote.class);

				apiGetStep.getTestResults().add( dynamicContainer("MemberIntervention Note DTO", expected.compare(actual)));
				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Unexpected exception",
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
	@DisplayName("TBD: GetMemberInterventionNote ALL Invalid Parm")
	@Order(2)
	public List<DynamicNode>  invalidParm() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = "invalid";
			String interventionId =  "invalid";
			String interventionNoteId =  "invalid";

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_INTERVENTION_NOTE_GET_ENDPOINT,
					new Object[]{memberId, interventionId, interventionNoteId}, 400, HTTP_400_BAD_REQUEST);

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
	@DisplayName("TBD: GetMemberInterventionNote InterventionNoteId Invalid Parm")
	@Order(3)
	public List<DynamicNode>  interventionNoteIdinvalidParm() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "";
				memberId = MemberInterventionNoteQueries.getRandomMemberWithMemberInterventionNote(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				List<MemberInterventionNote> list = MemberInterventionNoteQueries.getInterventionNote(subscriptionName, memberId);

				//Query Cosmos with one expected member that should have interventionnote
				MemberInterventionNote expected =list.get(0);

				String interventionNoteId =  UUID.randomUUID().toString();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_INTERVENTION_NOTE_GET_ENDPOINT,
						new Object[]{expected.getMemberId(), expected.getInterventionId(), interventionNoteId}, 404, HTTP_404_NOT_FOUND);

				apiGetStep.run();

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Unexpected exception",
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("TBD: GetMemberInterventionNote MemberId Invalid Parm")
	@Order(4)
	public List<DynamicNode>  memberIdinvalidParm() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName();

				String memberId = "";
				memberId = MemberInterventionNoteQueries.getRandomMemberWithMemberInterventionNote(subscriptionName);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(memberId ))
				{
					test.add( dynamicContainer(subscriptionName.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				List<MemberInterventionNote> list = MemberInterventionNoteQueries.getInterventionNote(subscriptionName, memberId);

				//Query Cosmos with one expected member that should have interventionnote
				MemberInterventionNote expected =list.get(0);

				String randomMemberId =  UUID.randomUUID().toString();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_INTERVENTION_NOTE_GET_ENDPOINT,
						new Object[]{randomMemberId, expected.getInterventionId(), expected.getId()}, 400, HTTP_400_BAD_REQUEST);

				apiGetStep.run();

				// validate the API message
				final String EXPECTED_MSG = "\"Member id "+  randomMemberId +" not found!\"";
				String actualMsg = apiGetStep.getResponse().then().extract().asString();
				apiGetStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Unexpected exception",
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}


	@TestFactory
	@DisplayName("TBD: GetMemberInterventionNote Invalid Auth")
	@Order(5)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String interventionId = UUID.randomUUID().toString();
			String interventionNoteId = UUID.randomUUID().toString();


			Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_INTERVENTION_NOTE_GET_ENDPOINT,
					new Object[]{memberId, interventionId, interventionNoteId}, 401, HTTP_401_UNAUTHORIZED);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result

			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("TBD: GetMemberInterventionNote Invalid Header")
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
			String interventionNoteId = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME+"invalid", subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_INTERVENTION_NOTE_GET_ENDPOINT,
					new Object[]{memberId, interventionId, interventionNoteId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result

			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("TBD: GetMemberInterventionNote Header Null")
	@Order(7)
	public List<DynamicNode>  nullHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String interventionId = UUID.randomUUID().toString();
			String interventionNoteId = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders();
			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_INTERVENTION_NOTE_GET_ENDPOINT,
					new Object[]{memberId, interventionId, interventionNoteId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result

			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}
}
