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
import org.junit.jupiter.api.Disabled;
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
import com.excellus.sqa.restapi.steps.ApiPatchStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.MemberCorrespondenceQueries;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionNoteQueries;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionQueries;
import com.excellus.sqa.rxcc.cosmos.Queries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.MemberInterventionNote;
import com.excellus.sqa.rxcc.dto.MemberInterventionNote.NewAction;
import com.excellus.sqa.rxcc.dto.MemberInterventionNote.PreviousAction;
import com.excellus.sqa.rxcc.dto.MemberInterventionNote.Type;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.IStep;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * PATCH - https://apim-lbs-rxc-dev-east-001.azure-api.net/api/member/members/{memberId}/interventions/{interventionId}/intervention-notes/{interventionNoteId}
 * 
 * 
 * {
 *   "interventionId": "67f0939e-a003-4890-bb86-19d21073726f",
 *   "id": "5136a36e-901d-4681-821d-8e2b181abd86",
 *   "previousStatusCode": "3",
 *   "newStatusCode": "4",
 *   "newStatusDescription": "Call Pharmacy", 
 *   "newAction": "2",
 *   "note": "Called to Provider"
 * }
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 02/09/2023
 */

@Tag("ALL")
@Tag("MEMBER")
@Tag("INTERVENTIONNOTE")
@Tag("MEMBERINTERVENTIONNOTE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PatchMemberInterventionNote")
public class PatchMemberInterventionNoteTest extends RxConciergeAPITestBaseV2 
{
	private static final Logger logger = LoggerFactory.getLogger(PatchMemberInterventionNoteTest.class);
	private String createdDateTime;
	private List<MemberInterventionNote> toBeDeletedList;
	private String subscriptionName;
	private String updateDateTime;

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
	public void deleteMemberInterventionNote() {
		if (toBeDeletedList != null && toBeDeletedList.size() > 0 ) {
			for (MemberInterventionNote memberInterventionNote : toBeDeletedList) {
				MemberInterventionNoteQueries.deleteInterventionNote(memberInterventionNote.getId(), memberInterventionNote.getMemberId());
			}
			toBeDeletedList.clear(); // clear the list after deleting all notes
		}
	}


	@TestFactory
	@DisplayName("40023: Patch Member InterventionNote Happy Path ")
	@Order(1)
	public List<DynamicNode> happyPathPatch() throws JsonProcessingException {
		List<DynamicNode> test = new ArrayList<>();

		// Get list of tenants from Cosmos
		List<Tenant> tenants = TenantQueries.getTenants();

		// Iterate through the tenants
		for (Tenant tenant : tenants) {
			List<DynamicNode> testSubResults = new ArrayList<>();

			try {
				// Get subscription name
				subscriptionName = tenant.getSubscriptionName();

				// Retrieve a list of IDs and intervention IDs from a list of member interventions, excluding those with a specific queue status code
				List<GenericCount> genericCountList = MemberInterventionQueries.getMembersWithInterventionsExcludingQueueStatusCode(subscriptionName, ("12"));
				GenericCount genericCount = Queries.getRandomItem(genericCountList);

				if (genericCount == null) {
					throw new IllegalArgumentException("Intervention ID is a required field to execute the test and cannot be null.");
				}

				String interventionId = genericCount.getIntId();

				// If interventionId is not found, skip the test
				if (StringUtils.isBlank(interventionId)) {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// Get a random member intervention note from the list
				List<MemberIntervention> list = MemberInterventionQueries.getInterventionById(subscriptionName, interventionId);
				MemberIntervention memberInterventionNoteTestData = list.get(0);

				// Create member intervention note manually in the Cosmos DB container
				MemberInterventionNote expected = createInterventionNote(memberInterventionNoteTestData.getMemberId(), memberInterventionNoteTestData.getId());

				// Setup test data
				MemberInterventionNote memberInterventionNote = getNewRegularMemberInterventionNote(expected.getInterventionId(), expected.getId());

				// Create API body
				String apiBody = createApiBody(memberInterventionNote);

				// Call happy path testing
				testSubResults = happyPathPatch(expected, apiBody);
			} catch (Exception e) {
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				testSubResults.add(dynamicTest("Unexpected exception",
						() -> fail(apiInfo + "\n" + e.getMessage(), e)));
			}
			test.add(dynamicContainer(subscriptionName.toUpperCase(), testSubResults));
			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("Negative 113863: Patch Member Intervention Note Only latest generated note can be edited")
	@Order(2)
	public List<DynamicNode> bodyPatchingLatestGeneratedNoteTry()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try {
				// Get subscription name
				subscriptionName = tenant.getSubscriptionName();

				// Retrieve a list of IDs and intervention IDs from a list of member interventions, excluding those with a specific queue status code
				List<GenericCount> genericCountList = MemberInterventionQueries.getMembersWithInterventionsExcludingQueueStatusCode(subscriptionName, ("12"));
				GenericCount genericCount = Queries.getRandomItem(genericCountList);

				if (genericCount == null) {
					throw new IllegalArgumentException("Intervention ID is a required field to execute the test and cannot be null.");
				}

				String interventionId = genericCount.getIntId();

				// If interventionId is not found, skip the test
				if (StringUtils.isBlank(interventionId)) {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// Get a random member intervention note from the list
				List<MemberIntervention> list = MemberInterventionQueries.getInterventionById(subscriptionName, interventionId);
				MemberIntervention memberInterventionNoteTestData = list.get(0);

				// Create member intervention note manually in the Cosmos DB container
				MemberInterventionNote expected1 = createInterventionNote(memberInterventionNoteTestData.getMemberId(), memberInterventionNoteTestData.getId());

				MemberInterventionNote expected2 = createInterventionNote(memberInterventionNoteTestData.getMemberId(), memberInterventionNoteTestData.getId());

				// Setup test data
				MemberInterventionNote memberInterventionNote = getNewRegularMemberInterventionNote(expected1.getInterventionId(), expected1.getId());

				// Create API body
				String apiBody = createApiBody(memberInterventionNote);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

				// Call API
				ApiPatchStep apiPatchStep = new ApiPatchStep(headers, MEMBER_INTERVENTION_NOTE_PATCH_ENDPOINT,
						apiBody, new Object[]{expected1.getMemberId(), expected2.getInterventionId(), expected1.getId()}, 400, HTTP_400_BAD_REQUEST);
				apiPatchStep.run(); // perform the actual API call

				// validate the API message
				final String EXPECTED_MSG = String.format("PatchInterventionNote - HttpRequestException ValidateRequest - InterventionNote id %s not latest note", expected1.getId());

				String actualMsg = apiPatchStep.getResponse().then().extract().asString();
				apiPatchStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPatchStep.getApiInfo())) );

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
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
	@DisplayName("Negative TBD: Patch Member Intervention Note Body must have atleast InterventionNote id")
	@Order(3)
	public List<DynamicNode> bodyPatchingAtleastInterventionNoteId()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try {
				// Get subscription name
				subscriptionName = tenant.getSubscriptionName();

				// Retrieve a list of IDs and intervention IDs from a list of member interventions, excluding those with a specific queue status code
				List<GenericCount> genericCountList = MemberInterventionQueries.getMembersWithInterventionsExcludingQueueStatusCode(subscriptionName, ("12"));
				GenericCount genericCount = Queries.getRandomItem(genericCountList);

				if (genericCount == null) {
					throw new IllegalArgumentException("Intervention ID is a required field to execute the test and cannot be null.");
				}

				String interventionId = genericCount.getIntId();

				// If interventionId is not found, skip the test
				if (StringUtils.isBlank(interventionId)) {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// Get a random member intervention note from the list
				List<MemberIntervention> list = MemberInterventionQueries.getInterventionById(subscriptionName, interventionId);
				MemberIntervention memberInterventionNoteTestData = list.get(0);

				// Create member intervention note manually in the Cosmos DB container
				MemberInterventionNote expected1 = createInterventionNote(memberInterventionNoteTestData.getMemberId(), memberInterventionNoteTestData.getId());

				// Setup test data
				MemberInterventionNote memberInterventionNote = new MemberInterventionNote();

				// Create API body
				String apiBody = createApiBody(memberInterventionNote);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

				// Call API
				ApiPatchStep apiPatchStep = new ApiPatchStep(headers, MEMBER_INTERVENTION_NOTE_PATCH_ENDPOINT,
						apiBody, new Object[]{expected1.getMemberId(), expected1.getInterventionId(), expected1.getId()}, 400, HTTP_400_BAD_REQUEST);
				apiPatchStep.run(); // perform the actual API call

				// validate the API message
				final String EXPECTED_MSG = "\"PatchInterventionNote - HttpRequestException ValidateRequest - InterventionNote id is required\"";

				String actualMsg = apiPatchStep.getResponse().then().extract().asString();
				apiPatchStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPatchStep.getApiInfo())) );

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
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
	@DisplayName("Negative TBD: Patch Member Intervention Note in path and body must match")
	@Order(4)
	public List<DynamicNode> bodyPatchingMatchPathAndBodyInId()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try {
				// Get subscription name
				subscriptionName = tenant.getSubscriptionName();

				// Retrieve a list of IDs and intervention IDs from a list of member interventions, excluding those with a specific queue status code
				List<GenericCount> genericCountList = MemberInterventionQueries.getMembersWithInterventionsExcludingQueueStatusCode(subscriptionName, ("12"));
				GenericCount genericCount = Queries.getRandomItem(genericCountList);

				if (genericCount == null) {
					throw new IllegalArgumentException("Intervention ID is a required field to execute the test and cannot be null.");
				}

				String interventionId = genericCount.getIntId();

				// If interventionId is not found, skip the test
				if (StringUtils.isBlank(interventionId)) {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// Get a random member intervention note from the list
				List<MemberIntervention> list = MemberInterventionQueries.getInterventionById(subscriptionName, interventionId);
				MemberIntervention memberInterventionNoteTestData = list.get(0);

				// Create member intervention note manually in the Cosmos DB container
				MemberInterventionNote expected1 = createInterventionNote(memberInterventionNoteTestData.getMemberId(), memberInterventionNoteTestData.getId());

				// Setup test data
				MemberInterventionNote memberInterventionNote = new MemberInterventionNote();
				memberInterventionNote.setId(UUID.randomUUID().toString());

				// Create API body
				String apiBody = createApiBody(memberInterventionNote);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

				// Call API
				ApiPatchStep apiPatchStep = new ApiPatchStep(headers, MEMBER_INTERVENTION_NOTE_PATCH_ENDPOINT,
						apiBody, new Object[]{expected1.getMemberId(), expected1.getInterventionId(), expected1.getId()}, 400, HTTP_400_BAD_REQUEST);
				apiPatchStep.run(); // perform the actual API call

				// validate the API message
				final String EXPECTED_MSG = "\"PatchInterventionNote - HttpRequestException ValidateRequest - interventionNote id in path and body must match\"";

				String actualMsg = apiPatchStep.getResponse().then().extract().asString();
				apiPatchStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPatchStep.getApiInfo())) );

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
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
	@DisplayName("Negative TBD: Patch Member Intervention Note System Generated note cannot be edited")
	@Order(5)
	public List<DynamicNode> bodyPatchingSystemGeneratedNote()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				// get subscription name
				subscriptionName = tenant.getSubscriptionName();

				String whereClause = "Where LENGTH(c.interventionId) > 100 AND  c.createdBy = \"System API\" ORDER BY c.lastUpdatedDateTime DESC OFFSET 2 LIMIT 1";

				List<MemberInterventionNote> meberInterventionNoteListCreatedBy = MemberInterventionNoteQueries.getMemberInterventionNotes(subscriptionName, whereClause );

				if (meberInterventionNoteListCreatedBy == null) {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// get a random member intervention note from the list
				MemberInterventionNote testData = meberInterventionNoteListCreatedBy.get(0);

				// Setup test data
				MemberInterventionNote memberInterventionNote = new MemberInterventionNote();

				memberInterventionNote.setInterventionId(testData.getInterventionId());
				memberInterventionNote.setId(testData.getId());

				// Create API body
				String apiBody = createApiBody(memberInterventionNote);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

				// Call API
				ApiPatchStep apiPatchStep = new ApiPatchStep(headers, MEMBER_INTERVENTION_NOTE_PATCH_ENDPOINT,
						apiBody, new Object[]{testData.getMemberId(), testData.getInterventionId(), testData.getId()}, 400, HTTP_400_BAD_REQUEST);
				apiPatchStep.run(); // perform the actual API call

				// validate the API message
				final String EXPECTED_MSG = String.format("PatchInterventionNote - HttpRequestException System generated interventions notes cannot be modified.");

				String actualMsg = apiPatchStep.getResponse().then().extract().asString();
				apiPatchStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPatchStep.getApiInfo())) );

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
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
	@DisplayName("Negative TBD: Patch Member Intervention Note Invalid Auth")
	@Order(6)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException
	{
		List<DynamicNode> testAllResults = new ArrayList<DynamicNode>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();

			// Setup test data
			MemberInterventionNote memberInterventionNote = new MemberInterventionNote();
			memberInterventionNote.setInterventionId(UUID.randomUUID().toString());
			memberInterventionNote.setId(UUID.randomUUID().toString());

			// Create API body
			String apiBody = createApiBody(memberInterventionNote);

			// Call API
			ApiPatchStep apiPatchStep = new ApiPatchStep(getHeadersInvalidAuth(), MEMBER_INTERVENTION_NOTE_PATCH_ENDPOINT,
					apiBody, new Object[]{memberId, memberInterventionNote.getInterventionId(), memberInterventionNote.getId()}, 401, HTTP_401_UNAUTHORIZED);
			apiPatchStep.run(); // perform the actual API call

			testAllResults.add(dynamicContainer(subscriptionName.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result

			resetApiInfo(); // reset API info and test validation results
		}

		return testAllResults;
	}

	@TestFactory
	@DisplayName("Negative TBD: Patch Member Intervention Note Invalid Header")
	@Order(7)
	public List<DynamicNode> invalidHeader() throws JsonProcessingException
	{
		List<DynamicNode> testAllResults = new ArrayList<DynamicNode>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();

			// Setup test data
			MemberInterventionNote memberInterventionNote = new MemberInterventionNote();
			memberInterventionNote.setInterventionId(UUID.randomUUID().toString());

			// memberInterventionNote.setInterventionId(interventionId);
			memberInterventionNote.setId(UUID.randomUUID().toString());

			// Create API body
			String apiBody = createApiBody(memberInterventionNote);

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME + "invalid", subscriptionName));

			ApiPatchStep apiPatchStep = new ApiPatchStep(headers, MEMBER_INTERVENTION_NOTE_PATCH_ENDPOINT, apiBody.toString(),
					new Object[]{memberId, memberInterventionNote.getInterventionId(), memberInterventionNote.getId()}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiPatchStep.run();

			testAllResults.add(dynamicContainer(subscriptionName.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result

			resetApiInfo(); // reset API info and test validation results
		}

		return testAllResults;
	}

	@TestFactory
	@DisplayName("Negative TBD: Patch Member Intervention Note Only Created by user can edit the note")  //TODO Admin can edit any record, but we don't have admin role configured yet,  and all access have admin role right now so cannot be automated now
	@Order(8)
	@Disabled
	public List<DynamicNode> bodyPatchingCreatedbyAnotherUser()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				// get subscription name
				subscriptionName = tenant.getSubscriptionName();

				List<MemberInterventionNote> meberInterventionNoteListCreatedBy = MemberInterventionNoteQueries.getMemberInterventionNotesLatestCreatedBy(subscriptionName,"Lisa Dailey");

				if (meberInterventionNoteListCreatedBy == null) {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// get a random member intervention note from the list
				MemberInterventionNote testData = meberInterventionNoteListCreatedBy.get(0);

				// Setup test data
				MemberInterventionNote memberInterventionNote = new MemberInterventionNote();

				memberInterventionNote.setInterventionId(testData.getInterventionId());
				memberInterventionNote.setId(testData.getId());

				// Create API body
				String apiBody = createApiBody(memberInterventionNote);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

				// Call API
				ApiPatchStep apiPatchStep = new ApiPatchStep(headers, MEMBER_INTERVENTION_NOTE_PATCH_ENDPOINT,
						apiBody, new Object[]{testData.getMemberId(), testData.getInterventionId(), testData.getId()}, 400, HTTP_400_BAD_REQUEST);
				apiPatchStep.run(); // perform the actual API call

				// validate the API message
				final String EXPECTED_MSG = String.format(String.format("PatchInterventionNote - HttpRequestException ValidateRequest - InterventionNote not createdBy User: %s", RxConciergeUILogin.getAcctName()));

				String actualMsg = apiPatchStep.getResponse().then().extract().asString();
				apiPatchStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPatchStep.getApiInfo())) );

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
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
	@DisplayName("Negative TBD: Patch Member Intervention note must not be linked to an existing correspondence.")
	@Order(9)
	public List<DynamicNode> bodyPatchingLinkedToCorrespondence()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try {
				// Get subscription name
				subscriptionName = tenant.getSubscriptionName();

				List<GenericCount> genericCountList = MemberCorrespondenceQueries.getMembersWithCorrespondenceIncludeInterventionNoteId(subscriptionName);
				GenericCount genericCount = Queries.getRandomItem(genericCountList);

				if (genericCount == null) {
					throw new IllegalArgumentException("Intervention ID is a required field to execute the test and cannot be null.");
				}

				String interventionId = genericCount.getIntId();
				String memberId = genericCount.getId();
				String interventionNoteId = genericCount.getTypeId();

				// If interventionId is not found, skip the test
				if (StringUtils.isBlank(interventionId)) {
					test.add(dynamicContainer(subscriptionName.toUpperCase(),
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// Setup test data
				MemberInterventionNote memberInterventionNote = new MemberInterventionNote();
				memberInterventionNote.setId(interventionNoteId);

				// Create API body
				String apiBody = createApiBody(memberInterventionNote);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

				// Call API
				ApiPatchStep apiPatchStep = new ApiPatchStep(headers, MEMBER_INTERVENTION_NOTE_PATCH_ENDPOINT,
						apiBody, new Object[]{memberId, interventionId, interventionNoteId}, 400, HTTP_400_BAD_REQUEST);
				apiPatchStep.run(); // perform the actual API call

				// validate the API message
				final String EXPECTED_MSG = "\"PatchInterventionNote - HttpRequestException Correspondence dependent intervention notes cannot be modified.\"";

				String actualMsg = apiPatchStep.getResponse().then().extract().asString();
				apiPatchStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPatchStep.getApiInfo())) );

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
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


	//Helper Method for test data body setup Without Special Character
	private  MemberInterventionNote  getNewRegularMemberInterventionNote(String interventionId,String id) {
 		try {
 			updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}

		MemberInterventionNote memberInterventionNote = new MemberInterventionNote();

		memberInterventionNote.setInterventionId(interventionId);
		memberInterventionNote.setType(Type.interventionnote);
		memberInterventionNote.setPreviousStatusCode("4");
		memberInterventionNote.setPreviousAction(PreviousAction.Validate.getEnumValue());
		memberInterventionNote.setPreviousStatusDescription("Call Pharmacy"); 
		memberInterventionNote.setNewStatusCode("5");
		memberInterventionNote.setNewStatusDescription("Verify Provider");
		memberInterventionNote.setNewAction(NewAction.Validate.getEnumValue());
		memberInterventionNote.setNote("Test Automation - updated on " + updateDateTime);
		memberInterventionNote.setCreatedBy(RxConciergeUILogin.getAcctName());
		memberInterventionNote.setCreatedDateTime(createdDateTime);
		memberInterventionNote.setVersion("1.0");
		memberInterventionNote.setId(id);  

		return memberInterventionNote;
	}

	/*
	 * Helper methods
	 */
	/**
	 * Perform the actual test
	 * 
	 * @param memberInterventionNote
	 */
	private List<DynamicNode> happyPathPatch(MemberInterventionNote expected, String apiBody) {
		List<DynamicNode> test = new ArrayList<DynamicNode>();

		// Update the expected member interventionnote base on the member interventionnote inserted into Cosmos db
		MemberInterventionNote updatedMemberInterventionNote = createUpdatedInterventionNote(expected, createdDateTime, apiBody);

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

		// Call API
		ApiPatchStep apiPatchStep = new ApiPatchStep(headers, MEMBER_INTERVENTION_NOTE_PATCH_ENDPOINT,
				apiBody, new Object[]{expected.getMemberId(), expected.getInterventionId(), expected.getId()}, 200, null);
		apiPatchStep.run(); // perform the actual API call

		MemberInterventionNote actualAPIResponse = apiPatchStep.convertToJsonDTO(MemberInterventionNote.class); // convert the API response to JSON object

		if (apiPatchStep.stepStatus() != IStep.Status.COMPLETED) {
			test.addAll(apiPatchStep.getTestResults());
			toBeDeletedList.add(expected);
			return apiPatchStep.getTestResults(); // Do not continue if the step did not complete
		} else {
			test.addAll(apiPatchStep.getTestResults());
		}

		/*
		 * Validation
		 */
		// API
		test.add(dynamicContainer("API response", updatedMemberInterventionNote.compare(actualAPIResponse)));

		// Query the database for the member intervention note that matches the provided IDs
		MemberInterventionNote actual = MemberInterventionNoteQueries.getSpecificInterventionNote(expected.getMemberId(), expected.getInterventionId(), expected.getId());

		test.add(dynamicContainer("Cosmos DB", updatedMemberInterventionNote.compare(actual)));
		return test;
	}

	/**
	 * Create the member intervention note in the Cosmos DB container
	 * 
	 * @param memberId, intervention id , note id  to associate the member intervention note
	 * @return {@link MemberInterventionNote} that has been inserted into Cosmos DB container
	 */
	private MemberInterventionNote createInterventionNote(String memberId, String interventionId)
	{
		MemberInterventionNote memberInterventionNote = new MemberInterventionNote();

		memberInterventionNote.setMemberId(memberId);
		memberInterventionNote.setType(Type.interventionnote);
		memberInterventionNote.setInterventionId(interventionId);
		memberInterventionNote.setPreviousStatusCode("3");
		memberInterventionNote.setPreviousAction(PreviousAction.Validate.getEnumValue());
		memberInterventionNote.setPreviousStatusDescription("Call Provider"); 
		memberInterventionNote.setNewStatusCode("4");
		memberInterventionNote.setNewStatusDescription("Call Pharmacy");
		memberInterventionNote.setNewAction(NewAction.Validate.getEnumValue());
		memberInterventionNote.setNote("Test API Automation");
		memberInterventionNote.setCreatedBy(RxConciergeUILogin.getAcctName());
		memberInterventionNote.setCreatedDateTime(createdDateTime);
		memberInterventionNote.setVersion("1.0");
		memberInterventionNote.setId(UUID.randomUUID().toString());

		// Manually insert interventionnote (test data) into Cosmos DB
		MemberInterventionNoteQueries.insertInterventionNote(memberInterventionNote);

		// Set toBeDeleted for data clean up after the test
		//capturing for after each annotation
		if ( toBeDeletedList == null )
		{
			toBeDeletedList = new ArrayList<MemberInterventionNote>();
		}
		toBeDeletedList.add(memberInterventionNote);	

		return memberInterventionNote;
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

		if (memberInterventionNote.getInterventionId() != null) {
			map.put("interventionId", memberInterventionNote.getInterventionId());
		}

		if (memberInterventionNote.getId() != null) {
			map.put("id", memberInterventionNote.getId());
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
	 * @param createdDateTime the date and time string for when the MemberInterventionNote was created
	 * @param apiBody the JSON body string for the MemberInterventionNote
	 * @return the expected MemberInterventionNote
	 */
	private MemberInterventionNote createUpdatedInterventionNote(MemberInterventionNote memberInterventionnote,String createdDateTime, String apiBody)
	{
		Gson gson = new Gson();
		MemberInterventionNote expectedMemberInterventionNote = gson.fromJson(apiBody, MemberInterventionNote.class);

		expectedMemberInterventionNote.setMemberId(memberInterventionnote.getMemberId());
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
 		
		//expectedMemberInterventionNote.setVersion("1");                                      // TODO BUG 174377:

		return expectedMemberInterventionNote;
	}
}