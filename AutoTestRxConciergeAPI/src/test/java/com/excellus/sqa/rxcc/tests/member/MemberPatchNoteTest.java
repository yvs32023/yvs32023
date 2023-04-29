/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import com.excellus.sqa.restapi.steps.ApiPatchStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.MemberNoteQueries;
import com.excellus.sqa.rxcc.cosmos.MemberQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.MemberNote;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.IStep;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.google.gson.JsonObject;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * <a href="https://apim-lbs-rxc-tst-east-001.developer.azure-api.net/api-details#api=member&operation=patchnote">Member - Patch (update) Note</a>
 * Required Header: X-RXCC-SUB <ehp|exe|???>
 * 
 * <pre>
 *   {
 *     "alert": "boolean",
 *     "hidden": "boolean",
 *     "tombStoned": "boolean",
 *     "upi": "number",
 *     "note": "string"
 *   }
 * </pre>
 * 
 * <p><b>Request</b><br/>
 * PATCH    https://apim-lbs-rxc-tst-east-001.azure-api.net/api/member/{adTenantId}/members/{memberId}/notes/{noteId}
 * 
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/16/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_NOTE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PatchMemberNote")
public class MemberPatchNoteTest extends RxConciergeAPITestBaseV2 
{

	private static final Logger logger = LoggerFactory.getLogger(MemberPatchNoteTest.class);

	private final String NOTE = "This is a note from regression test automation - "; 

	private String createdDateTime;
	private String updateDateTime;
	private String subscriptionId;
	private MemberNote toBeDeleted;
	
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

	//	/**
	//	 * Delete any member note that was created with the test
	//	 */
	//	@AfterEach
	//	public void deleteMemberNoteFromCosmos()
	//	{
	//		if ( toBeDeleted != null )
	//		{
	//			MemberNoteQueries.deleteMemberNote(subscriptionId, toBeDeleted.getId(), toBeDeleted.getMemberId());
	//		}
	//		
	//		toBeDeleted = null;
	//		subscriptionId = null;
	//	}
	//	
	@TestFactory
	@DisplayName("3527: PatchNote Happy Path (ehp member note)")
	@Order(1)
	public List<DynamicNode> happyPathEhpPatch()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			List<DynamicNode> testSubResults = new ArrayList<>();
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				// Get member
				Member member = MemberQueries.getRandomMember(Tenant.Type.valueOf(subscriptionId));

				// Create API body
				updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
				JsonObject body = createApiBody(true, true, true, member.getUpi(), NOTE + " - updated on " + updateDateTime);

				// run the test
				testSubResults = happyPathPatch(member, body);
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				testSubResults.add( dynamicTest("Unexpected exception",	() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			test.add(dynamicContainer(subscriptionId.toUpperCase(), testSubResults));
			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}	

	@TestFactory
	@DisplayName("3542: PatchNote With Body (missing 'alert' property body)")
	@Order(2)
	public List<DynamicNode> missingAlertBody()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			List<DynamicNode> testSubResults = new ArrayList<>();
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				// Get member
				Member member = MemberQueries.getRandomMember(Tenant.Type.valueOf(subscriptionId));

				// Create API body
				updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
				JsonObject body = createApiBody(null, true, true, member.getUpi(), NOTE + " - updated on " + updateDateTime);

				// run the test
				testSubResults = happyPathPatch(member, body);
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				testSubResults.add( dynamicTest("Unexpected exception",	() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			test.add(dynamicContainer(subscriptionId.toUpperCase(), testSubResults));
			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}	

	@TestFactory
	@DisplayName("3542: PatchNote With Body (missing 'hidden' property in body)")
	@Order(3)
	public List<DynamicNode> missingHiddenBody()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			List<DynamicNode> testSubResults = new ArrayList<>();
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				// Get member
				Member member = MemberQueries.getRandomMember(Tenant.Type.valueOf(subscriptionId));

				// Create API body
				updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
				JsonObject body = createApiBody(true, null, true, member.getUpi(), NOTE + " - updated on " + updateDateTime);

				// run the test
				testSubResults = happyPathPatch(member, body);
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				testSubResults.add( dynamicTest("Unexpected exception",	() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			test.add(dynamicContainer(subscriptionId.toUpperCase(), testSubResults));
			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}	

	@TestFactory
	@DisplayName("3542: PatchNote With Body (missing 'tombStone' property in body)")
	@Order(4)
	public List<DynamicNode> missingTombStoneBody()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			List<DynamicNode> testSubResults = new ArrayList<>();
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				// Get member
				Member member = MemberQueries.getRandomMember(Tenant.Type.valueOf(subscriptionId));

				// Create API body
				updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
				JsonObject body = createApiBody(true, true, null, member.getUpi(), NOTE + " - updated on " + updateDateTime);

				// run the test
				testSubResults = happyPathPatch(member, body);
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				testSubResults.add( dynamicTest("Unexpected exception",	() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			test.add(dynamicContainer(subscriptionId.toUpperCase(), testSubResults));
			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}	

	@TestFactory
	@DisplayName("3542: PatchNote With Body (missing 'upi' property in body)")
	@Order(5)
	public List<DynamicNode> missingUpiBody()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			List<DynamicNode> testSubResults = new ArrayList<>();
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				// Get member
				Member member = MemberQueries.getRandomMember(Tenant.Type.valueOf(subscriptionId));

				// Create API body
				updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
				JsonObject body = createApiBody(true, true, true, null, NOTE + " - updated on " + updateDateTime);

				// run the test
				testSubResults = happyPathPatch(member, body);
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				testSubResults.add( dynamicTest("Unexpected exception",	() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			test.add(dynamicContainer(subscriptionId.toUpperCase(), testSubResults));
			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}	

	@TestFactory
	@DisplayName("3542: PatchNote With Body (missing 'note' property in body)")
	@Order(6)
	public List<DynamicNode> missingNoteBody()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			List<DynamicNode> testSubResults = new ArrayList<>();
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				// Get member
				Member member = MemberQueries.getRandomMember(Tenant.Type.valueOf(subscriptionId));

				// Create API body
				updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
				JsonObject body = createApiBody(true, true, true, member.getUpi(), null);

				// run the test
				testSubResults = happyPathPatch(member, body);
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				testSubResults.add( dynamicTest("Unexpected exception",	() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			test.add(dynamicContainer(subscriptionId.toUpperCase(), testSubResults));
			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}	


	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("3529: PatchNote Invalid Parm")
	@Order(7)
	public List<DynamicNode> invalidParam()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				String invalidMemberId = "9_123456";
				String invalidNoteId = UUID.randomUUID().toString();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiPatchStep apiPatchStep = new ApiPatchStep( headers, MEMBER_NOTE_PATCH_POST_ENDPOINT, null, new Object[]{invalidMemberId, invalidNoteId}, 404, HTTP_404_NOT_FOUND);
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


	@TestFactory
	@DisplayName("3533: PatchNote Partial Parm Combo Exist")
	@Order(8)
	public List<DynamicNode> partialParamComboExists()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				// Get member
				Member member = MemberQueries.getRandomMember(Tenant.Type.valueOf(subscriptionId));

				// Note id doesn't exists
				String noteIdExists = UUID.randomUUID().toString();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiPatchStep apiPatchStep = new ApiPatchStep( headers, MEMBER_NOTE_PATCH_POST_ENDPOINT, null, new Object[]{member.getMemberId(), noteIdExists}, 404, HTTP_404_NOT_FOUND);
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

	@TestFactory
	@DisplayName("3538: PatchNote Invalid Header")
	@Order(9)
	public List<DynamicNode> invalidHeader()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = "9_123456";
			String noteId = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders(new Header("INVALID_HEADER", subscriptionId));

			ApiPatchStep apiPatchStep = new ApiPatchStep( headers, MEMBER_NOTE_PATCH_POST_ENDPOINT, null, new Object[]{memberId, noteId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiPatchStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3539: PatchNote Header Missing")
	@Order(10)
	public List<DynamicNode> missingRequiredHeader()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = "9_123456";
			String noteId = UUID.randomUUID().toString();

			ApiPatchStep apiPatchStep = new ApiPatchStep( getGenericHeaders(), MEMBER_NOTE_PATCH_POST_ENDPOINT, null, new Object[]{memberId, noteId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiPatchStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3540: PatchNote Valid Parm With Incorrect Header Value")
	@Order(11)
	public List<DynamicNode> invalidHeaderValue()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = "9_123456";
			String noteId = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, "abc"));

			ApiPatchStep apiPatchStep = new ApiPatchStep( headers, MEMBER_NOTE_PATCH_POST_ENDPOINT, null, new Object[]{memberId, noteId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiPatchStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3545: PatchNote Invalid Auth")
	@Order(12)
	public List<DynamicNode> invalidToken()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = "9_123456";
			String noteId = UUID.randomUUID().toString();

			Headers headers = new Headers(new Header(API_HEADER_NAME, subscriptionId));

			ApiPatchStep apiPatchStep = new ApiPatchStep( getHeadersInvalidAuth(headers), MEMBER_NOTE_PATCH_POST_ENDPOINT, null, new Object[]{memberId, noteId}, 401, HTTP_401_UNAUTHORIZED);
			apiPatchStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	/*
	 * Helper methods
	 */

	/**
	 * Perform the actual test
	 * 
	 * @param member
	 */
	private List<DynamicNode> happyPathPatch(Member member, JsonObject body)
	{
		// Create member note manually in the Cosmos DB container
		MemberNote expected = createMemberNote(member.getMemberId());

		// Update the expected member note base on the member note inserted into Cosmos db
		updateMemberNote(expected, body);

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

		// Call API
		ApiPatchStep apiPatchStep = new ApiPatchStep(headers, MEMBER_NOTE_PATCH_POST_ENDPOINT,
				body.toString(), new Object[]{expected.getMemberId(), expected.getId()}, 200, null);

		apiPatchStep.run();  // perform the actual API call

		MemberNote actualAPIResponse = apiPatchStep.convertToJsonDTO(MemberNote.class);  // convert the API response to JSON object

		if ( apiPatchStep.stepStatus() != IStep.Status.COMPLETED )
			
		{
			MemberNoteQueries.deleteMemberNote(subscriptionId, toBeDeleted.getId(), toBeDeleted.getMemberId());
			return apiPatchStep.getTestResults();    // Do not continue if the step did not complete
		}

		/*
		 * Validation
		 */
		// API
		apiPatchStep.getTestResults().add(dynamicContainer("API response", expected.compare(actualAPIResponse)));

		// Get the member note from the 
		MemberNote actual = MemberNoteQueries.getMemberNote(subscriptionId, expected.getId(), expected.getMemberId());

		apiPatchStep.getTestResults().add(dynamicContainer("Cosmos DB", expected.compare(actual)));

		if ( toBeDeleted != null ) 
		{
			MemberNoteQueries.deleteMemberNote(subscriptionId, toBeDeleted.getId(), toBeDeleted.getMemberId());
		}

		return apiPatchStep.getTestResults();
	}

	/**
	 * Create the member note in the Cosmos DB container
	 * 
	 * @param memberId to associate the member note
	 * @return {@link MemberNote} that has been inserted into Cosmos DB container
	 */
	private MemberNote createMemberNote(String memberId)
	{
		MemberNote memberNote = new MemberNote();
		memberNote.setMemberId(memberId);
		memberNote.setType("note");
		memberNote.setAlert(false);
		memberNote.setHidden(false);
		memberNote.setTombStoned(false);
		memberNote.setNote(NOTE + "created around " + createdDateTime);
		memberNote.setCreateUser("Garrett-Mari Cosmiano");
		memberNote.setCreateDateTime( createdDateTime );
		memberNote.setVer("1.0");
		memberNote.setId( UUID.randomUUID().toString() );

		// Manually insert note (test data) into Cosmos DB
		MemberNoteQueries.insertMemberNote(subscriptionId, memberNote);

		// Set toBeDeleted for data clean up after the test
		toBeDeleted = memberNote;

		return memberNote;
	}

	/**
	 * Create API body
	 * 
	 * @param alert property true/false
	 * @param hidden property true/false
	 * @param tombStoned property true/false
	 * @param upi of the member
	 * @param note to be updated
	 * @return
	 */
	private JsonObject createApiBody(Boolean alert, Boolean hidden, Boolean tombStoned, String upi, String note)
	{
		JsonObject requestBody = new JsonObject();

		if ( alert != null )
			requestBody.addProperty("alert", "false");

		if ( hidden != null )
			requestBody.addProperty("hidden", "false");

		if ( tombStoned != null )
			requestBody.addProperty("tombStoned", "false");

		if ( upi != null )
			requestBody.addProperty("upi", upi);

		if ( note != null )
			requestBody.addProperty("note", note);

		return requestBody;
	}

	/**
	 * Update the expected Member Note base on the API body
	 * 
	 * @param expected {@link MemberNote} to be updated
	 * @param body {@link JsonObject} that contains the updated member note data
	 */
	private void updateMemberNote(MemberNote expected, JsonObject body)
	{
		if ( body.get("alert") != null && !body.get("alert").isJsonNull() )
			expected.setAlert( body.get("alert").getAsBoolean() );

		if ( body.get("hidden") != null && !body.get("hidden").isJsonNull() )
			expected.setHidden( body.get("hidden").getAsBoolean() );

		if ( body.get("tombStoned") != null && !body.get("tombStoned").isJsonNull() )
			expected.setTombStoned( body.get("tombStoned").getAsBoolean() );

		if ( body.get("note") != null && !body.get("note").isJsonNull() )
			expected.setNote( body.get("note").getAsString() );

		expected.setLastUpdated(updateDateTime);
		expected.setLastUpdatedBy(RxConciergeUILogin.getAcctName());

	}

}

