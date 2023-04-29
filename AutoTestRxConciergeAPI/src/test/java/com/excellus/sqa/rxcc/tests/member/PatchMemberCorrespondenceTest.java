/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.steps.ApiPatchStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.MemberCorrespondenceQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence.CorrespondenceType;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence.MemberType;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.IStep;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.google.gson.JsonObject;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;


//Body has been updated seen last update.. need to work on this too
/**
 * PATCH - https://apim-lbs-rxc-dev-east-001.azure-api.net/api/member/members/{memberId}/interventions/{interventionId}/correspondences/{id}
 * 
 * 
 * {
 *   "correspondenceType": "Fax event",
 *   "correspondenceOutcome": "Clinical Consult Completed",
 *   "correspondenceDateTime": "2022-04-04T11:46:28Z",
 *   "contactName": "VANESSA SPRAGUE",
 *   "contactTitle": "Recptionist",
 *   "contactComment": "Section 1.10.33 of de Finibus Bonorum et Malorum, writ non ut perferendis doloribus asperiores repellat.",
 *   "targetDrug": "Multivitamin Adult (Minerals) Oral Tablet",
 *   "note": "Section 1.10.33 of de ralized by t best, every business it f selection: he rejects pleasures to secure other greater pleasures, or else he endures pains to avoid worse pains.",
 *   "pdfUri": https://faw-lbs-rxc-dev-storage-east-001.azurewebsites.net/fax-outbound-processed/23452758-7626-42f9-90c5-10bda15bb58d_1162281_67f0939e-a003-4890-bb86-19d21073726f_20220523_140454.pdf,
 *   "npi": "1437206554",
 *   "providerName": "JUDITH GREENBERG MD"
 * }
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 08/23/2022
 */


@Tag("ALL")
@Tag("MEMBER")
@Tag("CORRESPONDENCE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PatchMemberCorrespondence")
@Disabled()
public class PatchMemberCorrespondenceTest extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(PatchMemberCorrespondenceTest.class);

	private final String NOTE = "This is a note from regression test automation - "; 

	private String createdDateTime;
	private String updateDateTime;
	private String subscriptionId;
	private MemberCorrespondence toBeDeleted;

	/**
	 * Setup the created date/time
	 */
	@BeforeEach
	public void dataSetup()
	{
 		try {
 			createdDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}
	}

	/**
	 * Delete any member correspondence that was created with the test
	 */
	//	@AfterEach
	//	public void deleteMemberCorrespondenceFromCosmos()
	//	{
	//		if ( toBeDeleted != null )
	//		{
	//			MemberCorrespondenceQueries.deleteMemberCorrespondence(subscriptionId, toBeDeleted.getId(), toBeDeleted.getMemberId());
	//		}
	//
	//		toBeDeleted = null;
	//		subscriptionId = null;
	//	}

	@TestFactory
	@DisplayName("40023: Member PatchCorrespondence Happy Path (ehp member correspondence)")
	@Order(1)
	public List<DynamicNode> happyPathEhpPatch()
	{
		subscriptionId = Tenant.Type.EHP.getSubscriptionName().toUpperCase();

		//Query Cosmos for member Id with correspondence for all tenants
		List<GenericCount> memberWithCorrespondenceAndIntervention = MemberCorrespondenceQueries.getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionId, 2);

		String memberId = memberWithCorrespondenceAndIntervention.get(0).getId();
		String interventionId = memberWithCorrespondenceAndIntervention.get(0).getIntId();

		//Query Cosmos for retrieving the member correspondence for all tenants with respective member id
		MemberCorrespondence  expectedMemberCorrespondence = MemberCorrespondenceQueries.getALLTenantMemberCorrespondence(subscriptionId,memberId,interventionId);

		// Create API body
 		try {
 			updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}

		// Setup test data
		MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
		memberCorrespondence = getNewRegularMemberCorrespondence();

		// Create API body
		JsonObject body = createApiBody(memberCorrespondence);

		// call happy path testing
		return happyPathPatch(expectedMemberCorrespondence, body);
	}


	@TestFactory
	@DisplayName("40023: Member PatchCorrespondence Happy Path (exe member correspondence)")
	@Order(2)
	public List<DynamicNode> happyPathExePatch()
	{
		subscriptionId = Tenant.Type.EXE.getSubscriptionName().toUpperCase();

		//Query Cosmos for member Id with correspondence for all tenants
		List<GenericCount> memberWithCorrespondenceAndIntervention = MemberCorrespondenceQueries.getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionId, 2);

		String memberId = memberWithCorrespondenceAndIntervention.get(0).getId();
		String interventionId = memberWithCorrespondenceAndIntervention.get(0).getIntId();

		//Query Cosmos for retrieving the member correspondence for all tenants with respective member id
		MemberCorrespondence  expectedMemberCorrespondence = MemberCorrespondenceQueries.getALLTenantMemberCorrespondence(subscriptionId,memberId,interventionId);

		// Create API body
 		try {
 			updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}

		// Setup test data
		MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
		memberCorrespondence = getNewRegularMemberCorrespondence();

		JsonObject body = createApiBody(memberCorrespondence);

		// call happy path testing
		return happyPathPatch(expectedMemberCorrespondence, body);
	}

	//	/*
	//	 * Negative test cases
	//	 */
	//
	@TestFactory
	@DisplayName("42556: Member PatchCorrespondence Invalid Auth")
	@Order(3)
	public List<DynamicNode> invalidToken()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = UUID.randomUUID().toString();
			String interventionId = UUID.randomUUID().toString();
			String id = UUID.randomUUID().toString();

			Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionId));

			ApiPatchStep apiPatchStep = new ApiPatchStep( getHeadersInvalidAuth(headers), MEMBER_CORRESPONDENCE_PATCH_ENDPOINT, null, new Object[]{memberId, interventionId, id}, 401, HTTP_401_UNAUTHORIZED);
			apiPatchStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("42553: Member PatchCorrespondence Invalid Parm Combo Exist(Invalid Member Id)")
	@Order(4)
	public List<DynamicNode> initialParamInvalid()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				//Query Cosmos for member Id with correspondence for all tenants
				List<GenericCount> memberWithCorrespondenceAndIntervention = MemberCorrespondenceQueries.getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionId, 2);

				//if memberWithCorrespondence list is empty for specific tenant, throw the "NO TEST DATA FOUND"and skip the test
				if (memberWithCorrespondenceAndIntervention.isEmpty())
				{
					test.add( dynamicContainer(subscriptionId.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				//Query Cosmos for retrieving the member correspondence for all tenants with respective member id
				MemberCorrespondence  expected = MemberCorrespondenceQueries.getALLTenantMemberCorrespondence(subscriptionId,memberWithCorrespondenceAndIntervention.get(0).getId(),memberWithCorrespondenceAndIntervention.get(0).getIntId());

				//param setup
				String invalidMemberId = UUID.randomUUID().toString();
				String interventionId = expected.getInterventionId();
				String id = expected.getId();

				// Setup test data
				MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
				memberCorrespondence = getNewRegularMemberCorrespondence();

				JsonObject body = createApiBody(memberCorrespondence);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiPatchStep apiPatchStep = new ApiPatchStep( headers, MEMBER_CORRESPONDENCE_PATCH_ENDPOINT, body.toString(), new Object[]{invalidMemberId, interventionId,id }, 404, HTTP_404_NOT_FOUND);
				apiPatchStep.run();

				test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@Test
	@DisplayName("40025: Member PatchCorrespondence Invalid Parm Combo Exist(Invalid Intervention Id)")
	@Order(5)
	public void secondParamInvalid()
	{
		subscriptionId = Tenant.Type.EHP.getSubscriptionName();

		//Query Cosmos to fetch member id which has type as correspondence
		String memberIdCorrespondence = MemberCorrespondenceQueries.getRandomMembersWithCorrespondenceMoreThanX(subscriptionId);

		//Query Cosmos for a member details that should have type as correspondence
		MemberCorrespondence expected = MemberCorrespondenceQueries.getALLTenantMemberCorrespondences(subscriptionId,memberIdCorrespondence).get(0);

		//param setup
		String memberId = expected.getMemberId();
		String invalidInterventionId = UUID.randomUUID().toString();
		String id = expected.getId();

		// Setup test data
		MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
		memberCorrespondence = getNewRegularMemberCorrespondence();

		JsonObject body = createApiBody(memberCorrespondence);

		logger.info("Starting API call");

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

		Response response = super.restPatch(headers, MEMBER_CORRESPONDENCE_PATCH_ENDPOINT, body.toString(), new Object[] { memberId, invalidInterventionId, id});

		logger.info("API provider patch completed");

		assertAll(() -> {
			assertEquals(400, response.getStatusCode());
			assertEquals(HTTP_400_BAD_REQUEST, response.getStatusLine());
		});
	}

	@Test
	@DisplayName("34902: Member PatchCorrespondence Invalid Parm Combo Exist(Invalid Correspondence Id)")
	@Order(6)
	public void thirdParamInvalid()
	{
		subscriptionId = Tenant.Type.EHP.getSubscriptionName();

		//Query Cosmos to fetch member id which has type as correspondence
		String memberIdCorrespondence = MemberCorrespondenceQueries.getRandomMembersWithCorrespondenceMoreThanX(subscriptionId);

		//Query Cosmos for a member details that should have type as correspondence
		MemberCorrespondence expected = MemberCorrespondenceQueries.getALLTenantMemberCorrespondences(subscriptionId,memberIdCorrespondence).get(0);

		//param setup
		String memberId = expected.getMemberId();
		String interventionId = expected.getInterventionId();
		String invalidId = UUID.randomUUID().toString();

		// Setup test data
		MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
		memberCorrespondence = getNewRegularMemberCorrespondence();

		JsonObject body = createApiBody(memberCorrespondence);

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

		Response response = super.restPatch(headers, MEMBER_CORRESPONDENCE_PATCH_ENDPOINT, body.toString(), new Object[] {memberId, interventionId, invalidId});

		assertAll(() -> {
			assertEquals(404, response.getStatusCode());
			assertEquals(HTTP_404_NOT_FOUND, response.getStatusLine());
		});
	}

	//Throwing response error as "Correspondence not createdBy user: RxConciergeUILogin.getAcctName()"
	@TestFactory
	@DisplayName("TBD: Member PatchCorrespondence with Invalid Body (patching interventionId)")
	@Order(7)
	public List<DynamicNode> bodyInvalidValueInterventionId()
	{
		subscriptionId = Tenant.Type.EXE.getSubscriptionName();

		//Query Cosmos to fetch member id which has type as correspondence
		String memberIdCorrespondence = MemberCorrespondenceQueries.getRandomMembersWithCorrespondenceMoreThanX(subscriptionId);

		//Query Cosmos for a member details that should have type as correspondence
		MemberCorrespondence expected = MemberCorrespondenceQueries.getALLTenantMemberCorrespondences(subscriptionId,memberIdCorrespondence).get(0);

		// Create API body
 		try {
 			updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}

		// Setup test data
		MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
		memberCorrespondence.setInterventionId(UUID.randomUUID().toString());

		JsonObject body = createApiBody(memberCorrespondence);

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

		Response response = super.restPatch(headers, MEMBER_CORRESPONDENCE_PATCH_ENDPOINT, body.toString(), new Object[] {expected.getMemberId() , expected.getInterventionId(), expected.getId()});

		assertAll(() -> {
			assertEquals(400, response.getStatusCode());
			assertEquals(HTTP_400_BAD_REQUEST, response.getStatusLine());
		});

		List<DynamicNode> testResults = new ArrayList<DynamicNode>();

		// validate the response code
		int expectedStatusCode = 400;
		int actualStatusCode = response.getStatusCode();
		testResults.add(dynamicTest("API response status code [" + expectedStatusCode + "]", () -> assertEquals(expectedStatusCode, actualStatusCode)));

		// validate the response line
		String actualStatusLine = response.getStatusLine();
		testResults.add(dynamicTest("API response status line [" + HTTP_400_BAD_REQUEST + "]", () -> assertEquals(HTTP_400_BAD_REQUEST, actualStatusLine)));

		// validate the API message
		final String EXPECTED_MSG = "\"ValidateCorrespondencePatch - interventionId cannot be patched\"";
		String actualMsg = response.then().extract().asString();
		testResults.add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

		return testResults;
	}

	//Throwing response error as "Correspondence not createdBy user: RxConciergeUILogin.getAcctName()"
	@TestFactory
	@DisplayName("TBD: Member PatchCorrespondence with Invalid Body (patching memberId)")
	@Order(8)
	public List<DynamicNode> bodyInvalidValueMemberId()
	{
		subscriptionId = Tenant.Type.EXE.getSubscriptionName();

		//Query Cosmos to fetch member id which has type as correspondence
		String memberIdCorrespondence = MemberCorrespondenceQueries.getRandomMembersWithCorrespondenceMoreThanX(subscriptionId);

		//Query Cosmos for a member details that should have type as correspondence
		MemberCorrespondence expected = MemberCorrespondenceQueries.getALLTenantMemberCorrespondences(subscriptionId,memberIdCorrespondence).get(0);

		// Create API body
 		try {
 			updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}

		// Setup test data
		MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
		memberCorrespondence.setMemberId(UUID.randomUUID().toString());

		JsonObject body = createApiBody(memberCorrespondence);

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

		Response response = super.restPatch(headers, MEMBER_CORRESPONDENCE_PATCH_ENDPOINT, body.toString(), new Object[] {expected.getMemberId() , expected.getInterventionId(), expected.getId()});

		assertAll(() -> {
			assertEquals(400, response.getStatusCode());
			assertEquals(HTTP_400_BAD_REQUEST, response.getStatusLine());
		});

		List<DynamicNode> testResults = new ArrayList<DynamicNode>();

		// validate the response code
		int expectedStatusCode = 400;
		int actualStatusCode = response.getStatusCode();
		testResults.add(dynamicTest("API response status code [" + expectedStatusCode + "]", () -> assertEquals(expectedStatusCode, actualStatusCode)));

		// validate the response line
		String actualStatusLine = response.getStatusLine();
		testResults.add(dynamicTest("API response status line [" + HTTP_400_BAD_REQUEST + "]", () -> assertEquals(HTTP_400_BAD_REQUEST, actualStatusLine)));

		// validate the API message
		final String EXPECTED_MSG = "\"ValidateCorrespondencePatch - memberId cannot be patched\"";
		String actualMsg = response.then().extract().asString();
		testResults.add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

		return testResults;
	}

	//Throwing response error as "Correspondence not createdBy user: RxConciergeUILogin.getAcctName()"
	@TestFactory
	@DisplayName("TBD: Member PatchCorrespondence with Invalid Body (patching id of correspondence)")
	@Order(9)
	public List<DynamicNode> bodyInvalidValueId()
	{
		subscriptionId = Tenant.Type.EXE.getSubscriptionName();

		//Query Cosmos to fetch member id which has type as correspondence
		String memberIdCorrespondence = MemberCorrespondenceQueries.getRandomMembersWithCorrespondenceMoreThanX(subscriptionId);

		//Query Cosmos for a member details that should have type as correspondence
		MemberCorrespondence expected = MemberCorrespondenceQueries.getALLTenantMemberCorrespondences(subscriptionId,memberIdCorrespondence).get(0);

		// Create API body
 		try {
 			updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}

		// Setup test data
		MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
		memberCorrespondence.setId(UUID.randomUUID().toString());

		JsonObject body = createApiBody(memberCorrespondence);

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

		Response response = super.restPatch(headers, MEMBER_CORRESPONDENCE_PATCH_ENDPOINT, body.toString(), new Object[] {expected.getMemberId() , expected.getInterventionId(), expected.getId()});

		assertAll(() -> {
			assertEquals(400, response.getStatusCode());
			assertEquals(HTTP_400_BAD_REQUEST, response.getStatusLine());
		});

		List<DynamicNode> testResults = new ArrayList<DynamicNode>();

		// validate the response code
		int expectedStatusCode = 400;
		int actualStatusCode = response.getStatusCode();
		testResults.add(dynamicTest("API response status code [" + expectedStatusCode + "]", () -> assertEquals(expectedStatusCode, actualStatusCode)));

		// validate the response line
		String actualStatusLine = response.getStatusLine();
		testResults.add(dynamicTest("API response status line [" + HTTP_400_BAD_REQUEST + "]", () -> assertEquals(HTTP_400_BAD_REQUEST, actualStatusLine)));

		// validate the API message
		final String EXPECTED_MSG = "\"ValidateCorrespondencePatch - id of Correspondence cannot be patched\"";
		String actualMsg = response.then().extract().asString();
		testResults.add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

		return testResults;
	}

	@TestFactory
	@DisplayName("42557: Member PatchCorrespondence Header Missing")
	@Order(10)
	public List<DynamicNode> validParamHeaderMissing()
	{

		// Setup test data
		String memberId = UUID.randomUUID().toString();
		String interventionId = UUID.randomUUID().toString();
		String id = UUID.randomUUID().toString();

		// Setup test data
		MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
		memberCorrespondence.setId(id);

		JsonObject body = createApiBody(memberCorrespondence);

		ApiPatchStep apiPatchStep = new ApiPatchStep( getGenericHeaders(), MEMBER_CORRESPONDENCE_PATCH_ENDPOINT, body.toString(), new Object[]{memberId, interventionId, id}, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiPatchStep.run();

		return apiPatchStep.getTestResults();
	}


	@TestFactory
	@DisplayName("42553: Member PatchCorrespondence Invalid ALL Parm Combo Exist")
	@Order(11)
	public  List<DynamicNode> allParamInvalid()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				//param setup
				String invalidMemberId = UUID.randomUUID().toString();
				String invalidInterventionId = UUID.randomUUID().toString();
				String invalidId = UUID.randomUUID().toString();

				// Setup test data
				MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
				memberCorrespondence = getNewRegularMemberCorrespondence();

				JsonObject body = createApiBody(memberCorrespondence);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiPatchStep apiPatchStep = new ApiPatchStep( headers, MEMBER_CORRESPONDENCE_PATCH_ENDPOINT, body.toString(), new Object[]{invalidMemberId, invalidInterventionId,invalidId }, 404, HTTP_404_NOT_FOUND);
				apiPatchStep.run();

				test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	//Helper Method for test data body setup Without Special Character
	private  MemberCorrespondence  getNewRegularMemberCorrespondence() {

		MemberCorrespondence memberCorrespondence = new MemberCorrespondence();

		memberCorrespondence.setCorrespondenceOutcome("Test Consult Completed");    //this looks like has a business rule, need to check
		memberCorrespondence.setCorrespondenceType(CorrespondenceType.INBOUND_CALL);
		memberCorrespondence.setCorrespondenceDateTime(updateDateTime);
		memberCorrespondence.setContactName("Test Sharma");
		memberCorrespondence.setContactTitle("Test Cosmiano");
		memberCorrespondence.setContactComment( "Test solution-Sloane// Spoke with Lynn at md office about changing Humalog solution to insulin lispro." );
		memberCorrespondence.setTargetDrug("Test A");
		memberCorrespondence.setNote( NOTE + " - updated on " + updateDateTime);
		memberCorrespondence.setPdfUri("https://pdfstorage123.test.blob.core.windows.net/sample-workitems/FaxTest3.pdf");
		memberCorrespondence.setNpi("987654322");
		memberCorrespondence.setProviderName("Garrett-Mari Test");
		memberCorrespondence.setVersion("1.0");

		return memberCorrespondence;

	}

	/*
	 * Helper methods
	 */

	/**
	 * Perform the actual test
	 * 
	 * @param member
	 */
	private List<DynamicNode> happyPathPatch(MemberCorrespondence expectedMemberCorrespondence, JsonObject body)
	{
		// Create member correspondence manually in the Cosmos DB container
		MemberCorrespondence expected = createMemberCorrespondence(expectedMemberCorrespondence);

		// Update the expected member correspondence base on the member correspondence inserted into Cosmos db
		updateMemberCorrespondence(expected, body);

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

		// Call API
		ApiPatchStep apiPatchStep = new ApiPatchStep(headers, MEMBER_CORRESPONDENCE_PATCH_ENDPOINT,
				body.toString(), new Object[]{expected.getMemberId(), expected.getInterventionId(),expected.getId()}, 200, null);

		apiPatchStep.run();  // perform the actual API call

		MemberCorrespondence actualAPIResponse = apiPatchStep.convertToJsonDTO(MemberCorrespondence.class);  // convert the API response to JSON object

		if ( apiPatchStep.stepStatus() != IStep.Status.COMPLETED )
		{
			MemberCorrespondenceQueries.deleteMemberCorrespondence(subscriptionId, toBeDeleted.getId(), toBeDeleted.getMemberId());
			return apiPatchStep.getTestResults();    // Do not continue if the step did not complete
		}

		/*
		 * Validation
		 */
		// API
		apiPatchStep.getTestResults().add(dynamicContainer("API response", expected.compare(actualAPIResponse)));

		// Get the member correspondence from the 
		MemberCorrespondence actual = MemberCorrespondenceQueries.getMemberCorrespondence(subscriptionId, expected.getMemberId(), expected.getInterventionId(),expected.getId());

		apiPatchStep.getTestResults().add(dynamicContainer("Cosmos DB", expected.compare(actual)));

		if ( toBeDeleted != null ) 
		{
			MemberCorrespondenceQueries.deleteMemberCorrespondence(subscriptionId, toBeDeleted.getId(), toBeDeleted.getMemberId());
		}

		return apiPatchStep.getTestResults();
	}


	/**
	 * Create the member correspondence in the Cosmos DB container
	 * 
	 * @param memberId to associate the member correspondence
	 * @return {@link MemberCorrespondence} that has been inserted into Cosmos DB container
	 */
	private MemberCorrespondence createMemberCorrespondence(MemberCorrespondence expectedMemberCorrespondence)
	{
		MemberCorrespondence memberCorrespondence = new MemberCorrespondence();

		memberCorrespondence.setMemberId(expectedMemberCorrespondence.getMemberId());
		memberCorrespondence.setType(MemberType.correspondence);
		memberCorrespondence.setCorrespondenceOutcome("Clinical Consult Completed");
		memberCorrespondence.setCorrespondenceDateTime(createdDateTime); 
		memberCorrespondence.setCorrespondenceType("Inbound Fax Provider");
		memberCorrespondence.setCorrespondenceId(UUID.randomUUID().toString());
		memberCorrespondence.setInterventionId(expectedMemberCorrespondence.getInterventionId());
		memberCorrespondence.setContactName("Manish Sharma");
		memberCorrespondence.setContactTitle("Garrett-Mari Cosmiano");
		memberCorrespondence.setContactComment( "Humalog solution-Sloane// Spoke with Lynn at md office about changing Humalog solution to insulin lispro." );
		memberCorrespondence.setTargetDrug("Drug A");
		memberCorrespondence.setNote("Great savings idea");
		memberCorrespondence.setPdfUri("https://pdfstorage123.blob.core.windows.net/sample-workitems/FaxTest3.pdf");
		memberCorrespondence.setNpi("987654321");
		memberCorrespondence.setProviderName("Garrett-Mari Cosmiano");
		memberCorrespondence.setCreatedBy(RxConciergeUILogin.getAcctName());
		memberCorrespondence.setCreatedDateTime(createdDateTime);
		memberCorrespondence.setVersion("1.0");
		memberCorrespondence.setId(UUID.randomUUID().toString());


		// Manually insert correspondence (test data) into Cosmos DB
		MemberCorrespondenceQueries.insertMemberCorrespondence(subscriptionId, memberCorrespondence);

		// Set toBeDeleted for data clean up after the test
		toBeDeleted = memberCorrespondence;

		return memberCorrespondence;
	}


	/**
	 * Create API body
	 * 
	 * @param memberCorrespondence
	 * @return
	 */
	private JsonObject createApiBody(MemberCorrespondence memberCorrespondence) 
	{
		JsonObject requestBody = new JsonObject();

		//if (memberCorrespondence.getMemberId() != null)
		//requestBody.addProperty("memberId", memberCorrespondence.getMemberId());

		if (memberCorrespondence.getCorrespondenceOutcome() != null)
			requestBody.addProperty("correspondenceOutcome", memberCorrespondence.getCorrespondenceOutcome());

		if (memberCorrespondence.getCorrespondenceType() != null)
			requestBody.addProperty("correspondenceType", memberCorrespondence.getCorrespondenceType().toString());

		if (memberCorrespondence.getCorrespondenceDateTime() != null)
			requestBody.addProperty("correspondenceDateTime", memberCorrespondence.getCorrespondenceDateTime());

		if (memberCorrespondence.getInterventionId() != null)
			requestBody.addProperty("interventionId", memberCorrespondence.getInterventionId());

		if (memberCorrespondence.getContactName() != null)
			requestBody.addProperty("contactName", memberCorrespondence.getContactName());

		if (memberCorrespondence.getContactTitle() != null)
			requestBody.addProperty("contactTitle", memberCorrespondence.getContactTitle());

		if (memberCorrespondence.getContactComment() != null)
			requestBody.addProperty("contactComment", memberCorrespondence.getContactComment());

		if (memberCorrespondence.getTargetDrug() != null)
			requestBody.addProperty("targetDrug", memberCorrespondence.getTargetDrug());

		if (memberCorrespondence.getNote() != null)
			requestBody.addProperty("note", memberCorrespondence.getNote());

		if (memberCorrespondence.getPdfUri() != null)
			requestBody.addProperty("pdfUri", memberCorrespondence.getPdfUri());

		if (memberCorrespondence.getNpi() != null)
			requestBody.addProperty("npi", memberCorrespondence.getNpi());

		if (memberCorrespondence.getProviderName() != null)
			requestBody.addProperty("providerName", memberCorrespondence.getProviderName());

		if (memberCorrespondence.getVersion() != null)
			requestBody.addProperty("version", memberCorrespondence.getVersion());

		//if (memberCorrespondence.getId() != null)
		//requestBody.addProperty("id", memberCorrespondence.getId());

		return requestBody;
	}

	/**
	 * Update the expected Member Correspondence base on the API body
	 * 
	 * @param expected {@link MemberCorrespondence} to be updated
	 * @param body {@link JsonObject} that contains the updated member correspondence data
	 */
	private void updateMemberCorrespondence(MemberCorrespondence expected, JsonObject body)
	{

		if ( body.get("correspondenceOutcome") != null && !body.get("correspondenceOutcome").isJsonNull() )
			expected.setCorrespondenceOutcome( body.get("correspondenceOutcome").getAsString() );

		if ( body.get("correspondenceType") != null && !body.get("correspondenceType").isJsonNull() )
			expected.setCorrespondenceType( body.get("correspondenceType").getAsString() );

		if ( body.get("interventionId") != null && !body.get("interventionId").isJsonNull() )
			expected.setInterventionId( body.get("interventionId").getAsString() );

		if ( body.get("contactName") != null && !body.get("contactName").isJsonNull() )
			expected.setContactName( body.get("contactName").getAsString() );

		if ( body.get("contactTitle") != null && !body.get("contactTitle").isJsonNull() )
			expected.setContactTitle( body.get("contactTitle").getAsString() );

		if ( body.get("contactComment") != null && !body.get("contactComment").isJsonNull() )
			expected.setContactComment( body.get("contactComment").getAsString() );

		if ( body.get("targetDrug") != null && !body.get("targetDrug").isJsonNull() )
			expected.setTargetDrug( body.get("targetDrug").getAsString() );

		if ( body.get("note") != null && !body.get("note").isJsonNull() )
			expected.setNote( body.get("note").getAsString() );

		if ( body.get("pdfUri") != null && !body.get("pdfUri").isJsonNull() )
			expected.setPdfUri( body.get("pdfUri").getAsString() );

		if ( body.get("npi") != null && !body.get("npi").isJsonNull() )
			expected.setNpi( body.get("npi").getAsString() );

		if ( body.get("providerName") != null && !body.get("providerName").isJsonNull() )
			expected.setProviderName( body.get("providerName").getAsString() );

		if ( body.get("version") != null && !body.get("version").isJsonNull() )
			expected.setVersion( body.get("version").getAsString() );

		expected.setLastUpdatedDateTime(updateDateTime);
		expected.setLastUpdatedBy(RxConciergeUILogin.getAcctName());

	}

}
