/**
 * 
 * @copyright 2023 Excellus BCBS
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionNoteQueries;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionQueries;
import com.excellus.sqa.rxcc.cosmos.Queries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.MemberInterventionNote;
import com.excellus.sqa.rxcc.dto.MemberInterventionNote.NewAction;
import com.excellus.sqa.rxcc.dto.MemberInterventionNote.Type;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.IStep;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * Post Intervention Note
 * 
 * Post https://apim-lbs-rxc-dev-east-001.azure-api.net/api/member/members/{memberId}/interventions/{interventionId}/intervention-notes
 * 
 * 
 * Cosmos rxcc-tenant: member
 * Required Header: X-RXCC-SUB <ehp | exe | loa | med> to specify Tenant
 * Request Parameters :  memberId & interventionId
 *  
 *  Body used :
 *  
 *   {
 *   "memberId": "87f4a82d-e2b6-44cc-9fff-828f94cf8ec9_2461230",
 *   "interventionId": "87f4a82d-e2b6-44cc-9fff-828f94cf8ec9_2461230_ace43295-6731-47fb-a747-711b3afc02ba_29033003206_1_1891050027_20221228",
 *   "previousStatusCode": 3,
 *   "newStatusCode": 4,
 *   "newStatusDescription": "Call Pharmacy",
 *   "newAction": "Validate",
 *   "note": "test note"
 *    }
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 01/30/2023
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("NOTE")
@Tag("INTERVENTIONNOTE")
@Tag("INTERVENTION")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostMemberInterventionNote")
public class PostMemberInterventionNoteTest extends RxConciergeAPITestBaseV2 
{
	private static final Logger logger = LoggerFactory.getLogger(PostMemberInterventionNoteTest.class);
	private String createdDateTime;
	private List<MemberInterventionNote> toBeDeletedList;
	private String subscriptionName;


	/**
	 * Setup the created date/time
	 */
	@BeforeEach
	public void dataSetup() {
		try {
			createdDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
		}
		catch(Exception e) {
			logger.error("An unexpected error is caught while generating timestamp", e);
		}
	}

	/**
	 * Delete any member note that was created with the test
	 */
	@AfterEach
	public void deleteMemberInterventionNote()
	{
		if (toBeDeletedList != null && toBeDeletedList.size() > 0 )
		{
			// iterate through the list and delete each record
			for (MemberInterventionNote memberInterventionNote : toBeDeletedList)
			{
				// call delete endpoint with note id & member id
				MemberInterventionNoteQueries.deleteInterventionNote( memberInterventionNote.getId(), memberInterventionNote.getMemberId());
			}
			toBeDeletedList = null;
		}
	}

	//Positive
	@TestFactory
	@DisplayName("34969: Post Member Intervention Note Happy Path Excluding Only Status 12(Fax Queued)")
	@Order(1)
	public List<DynamicNode> happyPathPostMemberInterventionNoteALL() throws JsonProcessingException 
	{
		// create a new list to store dynamic nodes
		List<DynamicNode> test = new ArrayList<>();

		// get list of tenants from cosmos
		List<Tenant> tenants = TenantQueries.getTenants();

		// iterate through the tenants
		for (Tenant tenant : tenants) 
		{
			try 
			{
				// get subscription name
				subscriptionName = tenant.getSubscriptionName();

				String interventionId = "";
				// Retrieve a list of IDs and intervention IDs from a list of member interventions, excluding those with a specific queue status code
				List<GenericCount> genericCountList = MemberInterventionQueries.getMembersWithInterventionsExcludingQueueStatusCode(subscriptionName, ("12") );
				GenericCount genericCount = Queries.getRandomItem( genericCountList);
				if (genericCount != null) {
					interventionId = genericCount.getIntId();
					// rest of the code
				} else {
					throw new IllegalArgumentException("Intervention ID is a required field and cannot be null.");
				}

				// if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(interventionId)) {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// get member intervention note list
				List<MemberIntervention> list = MemberInterventionQueries.getInterventionById(subscriptionName,interventionId);

				// get a random member intervention note from the list
				MemberIntervention memberinterventionnoteTestData = list.get(0);

				// create a new list to store dynamic nodes
				List<DynamicNode> newActionNode = new ArrayList<>();

				// iterate through the new action enum
				for (NewAction newAction : NewAction.values()) 
				{
					switch (newAction) {
					case Validate : 
						int[] a = {3,4,5,6,7,8,9,13,17,18,20,21,22,23,24,25}; //run this if  newAction == Validate
						newActionNode.addAll(getNewActionNode(a, memberinterventionnoteTestData, newAction));
						break;
					case Decline : 
						int[] b = {14,15,16}; //runthis if newAction == Decline
						newActionNode.addAll(getNewActionNode(b, memberinterventionnoteTestData, newAction));
						break;
					case Correspondence_Dependent : 
						int[] c = {}; //runthis if newAction == Correspondence_Dependent
						newActionNode.addAll(getNewActionNode(c, memberinterventionnoteTestData, newAction));
						break;
					default : throw new IllegalArgumentException("Invalid newAction"); 
					}
				}
				test.add(dynamicContainer(subscriptionName.toUpperCase(),
						newActionNode));
			}

			catch (Exception e) {
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add(dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),
						() -> fail(apiInfo + "\n" + e.getMessage(), e)));
			}
			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}


	//Negative
	@TestFactory
	@DisplayName("Negative 178385: Post Member Intervention Note User should be not allowed to post a intervention note when fax is queued")
	@Order(2)
	public List<DynamicNode> PostMemberInterventionNoteFaxQueued() throws JsonProcessingException 
	{
		// create a new list to store dynamic nodes
		List<DynamicNode> test = new ArrayList<>();

		// get list of tenants from cosmos
		List<Tenant> tenants = TenantQueries.getTenants();

		// iterate through the tenants
		for (Tenant tenant : tenants) 
		{
			try 
			{
				// get subscription name
				subscriptionName = tenant.getSubscriptionName();

				String interventionId = "";
				// Retrieve a list of IDs and intervention IDs from a list of member interventions, including those with a specific queue status code
				List<GenericCount> genericCountList = MemberInterventionQueries.getMembersWithInterventionCertainQueueStatusCode(subscriptionName,("12"));
				GenericCount genericCount = Queries.getRandomItem( genericCountList);
				if (genericCount != null) {
					interventionId = genericCount.getIntId();
					// rest of the code
				} else {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(interventionId)) {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// get member intervention note list
				List<MemberIntervention> list = MemberInterventionQueries.getInterventionById(subscriptionName,interventionId);

				// get a random member intervention note from the list
				MemberIntervention memberinterventionnoteTestData = list.get(0);

				// Setup test data
				MemberInterventionNote 	memberInterventionnote = getNewMemberInterventionNote(NewAction.forValue(NewAction.Validate.getEnumValue()),3);
				memberInterventionnote.setMemberId(memberinterventionnoteTestData.getMemberId());
				memberInterventionnote.setInterventionId(memberinterventionnoteTestData.getId());

				// Create API body
				String apiBody = createApiBody(memberInterventionnote);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

				ApiPostStep apiPostStep = new ApiPostStep(headers,  MEMBER_INTERVENTION_NOTE_POST_ENDPOINT, apiBody.toString(),
						new Object[]{memberinterventionnoteTestData.getMemberId(), memberinterventionnoteTestData.getId()},
						400, HTTP_400_BAD_REQUEST);
				apiPostStep.run();

				// validate the API message
				final String EXPECTED_MSG = "\"PostInterventionNote - intervention " + memberinterventionnoteTestData.getId() + " is in a FaxQueued state\"";

				String actualMsg = apiPostStep.getResponse().then().extract().asString();
				apiPostStep.getTestResults().add( dynamicTest("API message [" + actualMsg + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())) );

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return 	test;
	}

	/**
	 * Gets a list of DynamicNodes based on the given index, MemberInterventionNote test data, and NewAction.
	 *
	 * @param index the indices of the new status code
	 * @param memberinterventionnoteTestData the MemberInterventionNote test data
	 * @param newAction the action taken on the member
	 * @return a list of DynamicNodes
	 * @throws JsonProcessingException if an exception occurs while processing the JSON
	 */
	private List<DynamicNode> getNewActionNode(int[] index, MemberIntervention memberinterventionnoteTestData, NewAction newAction) throws JsonProcessingException{
		// Create a new list to store dynamic nodes
		List<DynamicNode> newActionNode = new ArrayList<>();

		for (int i : index){
			// Setup test data
			MemberInterventionNote memberInterventionnote = getNewMemberInterventionNote(newAction, i);
			memberInterventionnote.setMemberId(memberinterventionnoteTestData.getMemberId());
			memberInterventionnote.setInterventionId(memberinterventionnoteTestData.getId());
			// Create API body
			String apiBody = createApiBody(memberInterventionnote);
			// Run the test
			newActionNode.add(dynamicContainer( newAction + " : "  + memberInterventionnote.getNewStatusCode()
			+ " : " + memberInterventionnote.getNewStatusDescription() , happyPath(memberInterventionnote,apiBody)));
		}
		return newActionNode;
	}


	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("Negative 178386: Post Member Intervention Note with missing partial Body (missing  interventionId)")
	@Order(3)
	public List<DynamicNode> bodyMissingInterventionId()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				// get subscription name
				subscriptionName = tenant.getSubscriptionName();

				String interventionId = "";
				// Retrieve a list of IDs and intervention IDs from a list of member interventions, excluding those with a specific queue status code
				List<GenericCount> genericCountList = MemberInterventionQueries.getMembersWithInterventionsExcludingQueueStatusCode(subscriptionName, ("12") );
				GenericCount genericCount = Queries.getRandomItem( genericCountList);
				if (genericCount != null) {
					interventionId = genericCount.getIntId();
					// rest of the code
				} else {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(interventionId)) {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// get member intervention note list
				List<MemberIntervention> list = MemberInterventionQueries.getInterventionById(subscriptionName,interventionId);

				// get a random member intervention note from the list
				MemberIntervention memberinterventionnoteTestData = list.get(0);


				// Setup test data
				MemberInterventionNote memberInterventionnote = new MemberInterventionNote();


				memberInterventionnote = getNewMemberInterventionNote(NewAction.forValue(NewAction.Validate.getEnumValue()),3);
				memberInterventionnote.setMemberId(memberinterventionnoteTestData.getMemberId());

				// Create API body
				String apiBody = createApiBody(memberInterventionnote);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

				ApiPostStep apiPostStep = new ApiPostStep(headers,  MEMBER_INTERVENTION_NOTE_POST_ENDPOINT, apiBody.toString(),
						new Object[]{memberinterventionnoteTestData.getMemberId(), memberinterventionnoteTestData.getId()},
						400, HTTP_400_BAD_REQUEST);
				apiPostStep.run();

				// validate the API message
				final String EXPECTED_MSG = "\"PostInterventionNote - HttpRequestException ValidateRequest - interventionId in path and body must match\"";

				String actualMsg = apiPostStep.getResponse().then().extract().asString();
				apiPostStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())) );

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
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
	@DisplayName("Negative 178387: Post Member Intervention Note with missing partial Body (missing  memberId)")
	@Order(4)
	public List<DynamicNode> bodyMissingMemberId()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				// get subscription name
				subscriptionName = tenant.getSubscriptionName();

				String interventionId = "";
				// Retrieve a list of IDs and intervention IDs from a list of member interventions, excluding those with a specific queue status code
				List<GenericCount> genericCountList = MemberInterventionQueries.getMembersWithInterventionsExcludingQueueStatusCode(subscriptionName, ("12") );
				GenericCount genericCount = Queries.getRandomItem( genericCountList);
				if (genericCount != null) {
					interventionId = genericCount.getIntId();
					// rest of the code
				} else {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(interventionId)) {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// get member intervention note list
				List<MemberIntervention> list = MemberInterventionQueries.getInterventionById(subscriptionName,interventionId);

				// get a random member intervention note from the list
				MemberIntervention memberinterventionnoteTestData = list.get(0);


				// Setup test data
				MemberInterventionNote memberInterventionnote = new MemberInterventionNote();

				memberInterventionnote = getNewMemberInterventionNote(NewAction.forValue(NewAction.Validate.getEnumValue()),3);

				//memberInterventionnote.setMemberId(memberinterventionnoteTestData.getMemberId());
				memberInterventionnote.setInterventionId(memberinterventionnoteTestData.getId());

				// Create API body
				String apiBody = createApiBody(memberInterventionnote);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

				ApiPostStep apiPostStep = new ApiPostStep(headers,  MEMBER_INTERVENTION_NOTE_POST_ENDPOINT, apiBody.toString(),
						new Object[]{memberinterventionnoteTestData.getMemberId(), memberinterventionnoteTestData.getId()},
						400, HTTP_400_BAD_REQUEST);
				apiPostStep.run();


				// validate the API message
				final String EXPECTED_MSG = "\"PostInterventionNote - HttpRequestException ValidateRequest - memberId in path and body must match\"";

				String actualMsg = apiPostStep.getResponse().then().extract().asString();
				apiPostStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())) );

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
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
	@DisplayName("Negative 178388: Post Member Intervention Note with missing partial Body (missing  newAction)")
	@Order(5)
	public List<DynamicNode> bodyMissingNewAction()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				// get subscription name
				subscriptionName = tenant.getSubscriptionName();

				String interventionId = "";
				// Retrieve a list of IDs and intervention IDs from a list of member interventions, excluding those with a specific queue status code
				List<GenericCount> genericCountList = MemberInterventionQueries.getMembersWithInterventionsExcludingQueueStatusCode(subscriptionName, ("12") );
				GenericCount genericCount = Queries.getRandomItem( genericCountList);
				if (genericCount != null) {
					interventionId = genericCount.getIntId();
					// rest of the code
				} else {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(interventionId)) {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// get member intervention note list
				List<MemberIntervention> list = MemberInterventionQueries.getInterventionById(subscriptionName,interventionId);

				// get a random member intervention note from the list
				MemberIntervention memberinterventionnoteTestData = list.get(0);

				// Setup test data
				MemberInterventionNote memberInterventionnote = new MemberInterventionNote();

				memberInterventionnote.setMemberId(memberinterventionnoteTestData.getMemberId());
				memberInterventionnote.setInterventionId(memberinterventionnoteTestData.getId());
				memberInterventionnote.setNewStatusCode("6");
				memberInterventionnote.setNewStatusDescription("Generated");

				// Create API body
				String apiBody = createApiBody(memberInterventionnote);


				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

				ApiPostStep apiPostStep = new ApiPostStep(headers,  MEMBER_INTERVENTION_NOTE_POST_ENDPOINT, apiBody.toString(),
						new Object[]{memberinterventionnoteTestData.getMemberId(), memberinterventionnoteTestData.getId()},
						400, HTTP_400_BAD_REQUEST);
				apiPostStep.run();


				// validate the API message
				final String EXPECTED_MSG = "\"PostInterventionNote - HttpRequestException ValidateRequest - newAction is required\"";

				String actualMsg = apiPostStep.getResponse().then().extract().asString();
				apiPostStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())) );

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
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
	@DisplayName("Negative 178389: Post Member Intervention Note with missing partial Body (missing  newStatusCode)")
	@Order(6)
	public List<DynamicNode> bodyMissingNewStatusCode()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				// get subscription name
				subscriptionName = tenant.getSubscriptionName();

				String interventionId = "";
				// Retrieve a list of IDs and intervention IDs from a list of member interventions, excluding those with a specific queue status code
				List<GenericCount> genericCountList = MemberInterventionQueries.getMembersWithInterventionsExcludingQueueStatusCode(subscriptionName, ("12") );
				GenericCount genericCount = Queries.getRandomItem( genericCountList);
				if (genericCount != null) {
					interventionId = genericCount.getIntId();
					// rest of the code
				} else {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(interventionId)) {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// get member intervention note list
				List<MemberIntervention> list = MemberInterventionQueries.getInterventionById(subscriptionName,interventionId);

				// get a random member intervention note from the list
				MemberIntervention memberinterventionnoteTestData = list.get(0);


				// Setup test data
				MemberInterventionNote memberInterventionnote = new MemberInterventionNote();

				memberInterventionnote = getNewMemberInterventionNote(NewAction.forValue(NewAction.Validate.getEnumValue()),3);
				memberInterventionnote.setMemberId(memberinterventionnoteTestData.getMemberId());
				memberInterventionnote.setInterventionId(memberinterventionnoteTestData.getId());
				memberInterventionnote.setNewStatusCode(null);

				// Create API body
				String apiBody = createApiBody(memberInterventionnote);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

				ApiPostStep apiPostStep = new ApiPostStep(headers,  MEMBER_INTERVENTION_NOTE_POST_ENDPOINT, apiBody.toString(),
						new Object[]{memberinterventionnoteTestData.getMemberId(), memberinterventionnoteTestData.getId()},
						400, HTTP_400_BAD_REQUEST);
				apiPostStep.run();


				// validate the API message
				final String EXPECTED_MSG = "\"PostInterventionNote - HttpRequestException ValidateRequest - newStatusCode is required\"";

				String actualMsg = apiPostStep.getResponse().then().extract().asString();
				apiPostStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())) );

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
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
	@DisplayName("Negative 178390: Post Member Intervention Note with missing partial Body (missing  newStatusDescription)")
	@Order(7)
	public List<DynamicNode> bodyMissingNewStatusDescription()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				// get subscription name
				subscriptionName = tenant.getSubscriptionName();

				String interventionId = "";
				// Retrieve a list of IDs and intervention IDs from a list of member interventions, excluding those with a specific queue status code
				List<GenericCount> genericCountList = MemberInterventionQueries.getMembersWithInterventionsExcludingQueueStatusCode(subscriptionName, ("12") );
				GenericCount genericCount = Queries.getRandomItem( genericCountList);
				if (genericCount != null) {
					interventionId = genericCount.getIntId();
					// rest of the code
				} else {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(interventionId)) {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// get member intervention note list
				List<MemberIntervention> list = MemberInterventionQueries.getInterventionById(subscriptionName,interventionId);

				// get a random member intervention note from the list
				MemberIntervention memberinterventionnoteTestData = list.get(0);


				// Setup test data
				MemberInterventionNote memberInterventionnote = new MemberInterventionNote();

				memberInterventionnote = getNewMemberInterventionNote(NewAction.forValue(NewAction.Validate.getEnumValue()),3);
				memberInterventionnote.setMemberId(memberinterventionnoteTestData.getMemberId());
				memberInterventionnote.setInterventionId(memberinterventionnoteTestData.getId());
				memberInterventionnote.setNewStatusDescription(null);

				// Create API body
				String apiBody = createApiBody(memberInterventionnote);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

				ApiPostStep apiPostStep = new ApiPostStep(headers,  MEMBER_INTERVENTION_NOTE_POST_ENDPOINT, apiBody.toString(),
						new Object[]{memberinterventionnoteTestData.getMemberId(), memberinterventionnoteTestData.getId()},
						400, HTTP_400_BAD_REQUEST);
				apiPostStep.run();

				// validate the API message
				final String EXPECTED_MSG = "\"PostInterventionNote - HttpRequestException ValidateRequest - newStatusDescription is required\"";

				String actualMsg = apiPostStep.getResponse().then().extract().asString();
				apiPostStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())) );

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
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
	@DisplayName("Negative 178391: Post Member Intervention Note  Valid Parm With Incorrect Header Value")
	@Order(8)
	public List<DynamicNode> incorrectHeaderValue() throws JsonProcessingException
	{
		// Setup test data
		MemberInterventionNote memberInterventionnote = new MemberInterventionNote();
		memberInterventionnote = getNewMemberInterventionNote(NewAction.forValue(NewAction.Validate.getEnumValue()),3);
		memberInterventionnote.setMemberId(UUID.randomUUID().toString());
		memberInterventionnote.setInterventionId(UUID.randomUUID().toString());

		// Create API body
		String apiBody = createApiBody(memberInterventionnote);

		Headers headers = new Headers(new Header(API_HEADER_NAME, "abc"));

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(headers),  MEMBER_INTERVENTION_NOTE_POST_ENDPOINT, apiBody.toString(),
				new Object[]{memberInterventionnote.getMemberId(), memberInterventionnote.getInterventionId()}, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("Negative 178392: Post Member Intervention Note  Header Missing")
	@Order(9)
	public List<DynamicNode> missingHeader() throws JsonProcessingException
	{
		// Setup test data
		MemberInterventionNote memberInterventionnote = new MemberInterventionNote();
		memberInterventionnote = getNewMemberInterventionNote(NewAction.forValue(NewAction.Validate.getEnumValue()),3);
		memberInterventionnote.setMemberId(UUID.randomUUID().toString());
		memberInterventionnote.setInterventionId(UUID.randomUUID().toString());

		// Create API body
		String apiBody = createApiBody(memberInterventionnote);

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), MEMBER_INTERVENTION_NOTE_POST_ENDPOINT, apiBody.toString(),
				new Object[]{memberInterventionnote.getMemberId(), memberInterventionnote.getInterventionId()}, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}


	@TestFactory
	@DisplayName("Negative 178394: Post Member Intervention Note Invalid Header")
	@Order(10)
	public List<DynamicNode> invalidHeader() throws JsonProcessingException
	{
		List<DynamicNode> testAllResults = new ArrayList<DynamicNode>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			// Setup test data
			MemberInterventionNote memberInterventionnote = new MemberInterventionNote();
			memberInterventionnote = getNewMemberInterventionNote(NewAction.forValue(NewAction.Validate.getEnumValue()),3);
			memberInterventionnote.setMemberId(UUID.randomUUID().toString());
			memberInterventionnote.setInterventionId(UUID.randomUUID().toString());

			// Create API body
			String apiBody = createApiBody(memberInterventionnote);

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME + "invalid", subscriptionName));

			ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_INTERVENTION_NOTE_POST_ENDPOINT, apiBody.toString(),
					new Object[]{memberInterventionnote.getMemberId(), memberInterventionnote.getInterventionId()}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiPostStep.run();

			testAllResults.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result

			resetApiInfo(); // reset API info and test validation results
		}

		return testAllResults;
	}

	@TestFactory
	@DisplayName("Negative 178393: Post Member Intervention Note Invalid Auth")
	@Order(11)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException
	{
		List<DynamicNode> testAllResults = new ArrayList<DynamicNode>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			// Setup test data
			MemberInterventionNote memberInterventionnote = new MemberInterventionNote();
			memberInterventionnote = getNewMemberInterventionNote(NewAction.forValue(NewAction.Validate.getEnumValue()),3);
			memberInterventionnote.setMemberId(UUID.randomUUID().toString());
			memberInterventionnote.setInterventionId(UUID.randomUUID().toString());

			// Create API body
			String apiBody = createApiBody(memberInterventionnote);

			ApiPostStep apiPostStep = new ApiPostStep(getHeadersInvalidAuth(), MEMBER_INTERVENTION_NOTE_POST_ENDPOINT, apiBody.toString(),
					new Object[]{memberInterventionnote.getMemberId(), memberInterventionnote.getInterventionId()}, 401, HTTP_401_UNAUTHORIZED);
			apiPostStep.run();

			testAllResults.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result

			resetApiInfo(); // reset API info and test validation results
		}

		return testAllResults;
	}


	/**
	 * Creates a new MemberInterventionNote object based on the given newAction and newStatusCode.
	 * 
	 * @param newAction the action taken on the member
	 * @param newStatusCode the new status of the member
	 * @return a new MemberInterventionNote object
	 */
	private MemberInterventionNote getNewMemberInterventionNote(NewAction newAction, Integer newStatusCode) 
	{
		MemberInterventionNote memberInterventionnote = new MemberInterventionNote();
		memberInterventionnote.setPreviousStatusCode("3");
		memberInterventionnote.setNewStatusCode(newStatusCode.toString());
		String newStatusDescription = "";

		switch (newAction) {
		case Validate:
			switch (newStatusCode) {
			case 3:  newStatusDescription = "Call Provider"; break;
			case 4:  newStatusDescription = "Call Pharmacy"; break;
			case 5:  newStatusDescription = "Verify Provider"; break;
			case 6:  newStatusDescription = "Generated"; break;
			case 7:  newStatusDescription = "General Task"; break;
			case 8:  newStatusDescription = "Provider WCB"; break;
			case 9:  newStatusDescription = "Pharmacy WCB"; break;
			case 13: newStatusDescription = "Fax Follow-up Call"; break;
			case 17: newStatusDescription = "Provider Accepted"; break;
			case 18: newStatusDescription = "Provider Declined"; break;
			case 20: newStatusDescription = "No Provider Response"; break;
			case 21: newStatusDescription = "Unable to Reach Provider"; break;
			case 22: newStatusDescription = "Member No Longer Taking"; break;
			case 23: newStatusDescription = "Not a Cost Savings Opportunity"; break;
			case 24: newStatusDescription = "Member Termed"; break;
			case 25: newStatusDescription = "Archive"; break;
			}
			break;
		case Decline:
			switch (newStatusCode) {
			case 14: newStatusDescription = "Declined: Default re-trigger [180 days]"; break;
			case 15: newStatusDescription = "Declined: Custom re-trigger [on mm/dd/yyyy]"; break;
			case 16: newStatusDescription = "Declined: Indefinitely"; break;
			}
			break;
		default:
			break;
		}

		memberInterventionnote.setNewStatusDescription(newStatusDescription);
		memberInterventionnote.setNewAction(newAction);
		memberInterventionnote.setNote("API - Test Automation note");

		memberInterventionnote.setNewStatusDescription(newStatusDescription);

		return memberInterventionnote;
	}


	/**
	 * Generic test for happy path
	 * @param memberCorrespondence {@link MemberCorrespondence}
	 * @param apiBody {@link JsonObject}
	 * @return test validation result
	 */
	private List<DynamicNode> happyPath( MemberInterventionNote memberInterventionnote, String apiBody)
	{
		List<DynamicNode> test = new ArrayList<DynamicNode>();

		// Call API
		ApiPostStep apiPostStep = insertMemberInterventionNoteAPI(subscriptionName, memberInterventionnote.getMemberId(), memberInterventionnote.getInterventionId(), apiBody.toString());
		MemberInterventionNote actualAPIResponse = apiPostStep.convertToJsonDTO(MemberInterventionNote.class);
		if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
		{
			return apiPostStep.getTestResults();
		}
		else {
			test.addAll(apiPostStep.getTestResults());
		}

		// Create expected member intervention note  -fetched id from API response as it is autogenerated
		MemberInterventionNote expected = createExpectedInterventionNote( memberInterventionnote , actualAPIResponse, createdDateTime, apiBody);

		/*
		 * Validations
		 */
		// API
		test.add(dynamicContainer("API response", expected.compare(actualAPIResponse)));

		// Query Cosmos - Get the member correspondence from the cosmos
		MemberInterventionNote actual = MemberInterventionNoteQueries.getSpecificInterventionNote( memberInterventionnote.getMemberId(), memberInterventionnote.getInterventionId() , actualAPIResponse.getId());

		test.add(dynamicContainer("Cosmos DB", expected.compare(actual)));

		//	capturing for after each annotation
		if ( toBeDeletedList == null )
		{
			toBeDeletedList = new ArrayList<MemberInterventionNote>();
		}

		toBeDeletedList.add(actualAPIResponse);		// capture the list of MemberInterventionNote which needs to be deleted for after each test case
		return test;
	}


	/**
	 * Creates the API body for a MemberInterventionNote.
	 * 
	 * @param memberInterventionNote the MemberInterventionNote to create the API body for
	 * @return a String representing the API body
	 * @throws JsonProcessingException if an error occurs while processing the JSON
	 */
	private String createApiBody(MemberInterventionNote memberInterventionNote) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new LinkedHashMap<>();

		if (memberInterventionNote.getMemberId() != null) {
			map.put("memberId", memberInterventionNote.getMemberId());
		}

		if (memberInterventionNote.getInterventionId() != null) {
			map.put("interventionId", memberInterventionNote.getInterventionId());
		}

		if (memberInterventionNote.getPreviousStatusCode() != null) {
			map.put("previousStatusCode", memberInterventionNote.getPreviousStatusCode());
		}

		if (memberInterventionNote.getNewStatusCode() != null) {
			map.put("newStatusCode", memberInterventionNote.getNewStatusCode());
		}

		if (memberInterventionNote.getNewStatusDescription() != null && 
				memberInterventionNote.getNewStatusDescription().length() <= 255) {
			map.put("newStatusDescription", memberInterventionNote.getNewStatusDescription());
		}

		if (memberInterventionNote.getNewAction() != null) {
			map.put("newAction", memberInterventionNote.getNewAction());
		}

		if (memberInterventionNote.getNote() != null) {
			map.put("note", memberInterventionNote.getNote());
		}


		try {
			return mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			logger.error("Unable to convert JSON object to string", e);
			return "Unable to convert JSON object to string --> " + e.getMessage();
		}
	}


	/**
	 * Creates an expected MemberInterventionNote object based on the given parameters.
	 *
	 * @param memberInterventionnote the {@link MemberInterventionNote} to be used as a template 
	 * @param actualAPIResponse the {@link MemberInterventionNote} returned from the API
	 * @param createdDateTime the date and time string for when the MemberInterventionNote was created
	 * @param apiBody the JSON body string for the MemberInterventionNote
	 * @return the expected MemberInterventionNote
	 */
	private MemberInterventionNote createExpectedInterventionNote(MemberInterventionNote memberInterventionnote,  MemberInterventionNote actualAPIResponse, String createdDateTime, String apiBody)
	{

		Gson gson = new Gson();
		MemberInterventionNote expectedMemberInterventionNote = gson.fromJson(apiBody, MemberInterventionNote.class);

		expectedMemberInterventionNote.setType(Type.interventionnote);
		expectedMemberInterventionNote.setCreatedBy(RxConciergeUILogin.getAcctName());

		try {
			expectedMemberInterventionNote.setCreatedDateTime(DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT,
					RxConciergeCosmoConfig.COSMOS_TIMEZONE));
		}
		catch(Exception e) {
			logger.error("An unexpected error is caught while generating timestamp", e);
		}

		expectedMemberInterventionNote.setLastUpdatedBy(RxConciergeUILogin.getAcctName());

		try {
			expectedMemberInterventionNote.setLastUpdatedDateTime(DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT,
					RxConciergeCosmoConfig.COSMOS_TIMEZONE));
		}
		catch(Exception e) {
			logger.error("An unexpected error is caught while generating timestamp", e);
		}

		expectedMemberInterventionNote.setVersion("1");                                   
		expectedMemberInterventionNote.setId(actualAPIResponse.getId());

		return expectedMemberInterventionNote;
	}

	/**
	 * This method makes an API call to post a Member Intervention Note.
	 * 
	 * @param subscriberId The subscriberId for the API call
	 * @param memberId The memberId for the API call
	 * @param interventionId The interventionId for the API call
	 * @param body The body for the API call
	 * @return An ApiPostStep object representing the API call
	 */
	private ApiPostStep insertMemberInterventionNoteAPI(String subscriberId, String memberId, String interventionId, String body)
	{
		logger.info("Starting API call");

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriberId));

		ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_INTERVENTION_NOTE_POST_ENDPOINT, body, new Object[] {memberId,interventionId},201, null);
		apiPostStep.run();

		logger.debug("API call completed successfully");
		return apiPostStep;
	}

}