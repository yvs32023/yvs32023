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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.RequestLoggingFilter;
import com.excellus.sqa.restapi.steps.ApiPostStep;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * <a href="https://apim-lbs-rxc-tst-east-001.developer.azure-api.net/api-details#api=member&operation=insertnote">Member - Insert Note</a>
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
 * POST    https://apim-lbs-rxc-tst-east-001.azure-api.net/api/member/{adTenantId}/members/{memberId}/notes
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/14/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_NOTE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostMemberNoteInsert")
public class MemberInsertNoteTest extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(MemberInsertNoteTest.class);

	private String createdDateTime;
	private MemberNote toBeDeleted;
	private String subscriptionId; 

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
//	 * Delete any member note that was create with the test
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


	/*
	 * Positive test cases
	 */

	@TestFactory
	@DisplayName("3506: InsertNote Happy Path (ALL member)")
	@Order(1)
	public List<DynamicNode> happyPathAllInsertNote()
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
				JsonObject apiBody = createApiBody(member.getUpi(), "This note is created by test automation around " + createdDateTime + " (local time)");

				// run the test
				testSubResults = happyPath(member, apiBody);
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
	@DisplayName("3521: InsertNote With Body (alert set to true)")
	@Order(3)
	public List<DynamicNode> alertTrue()
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
				JsonObject apiBody = createApiBody(member.getUpi(), "This note is created by test automation around " + createdDateTime + " (local time)");
				apiBody.addProperty("alert", "true");

				// run the test
				testSubResults = happyPath(member, apiBody);
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
	@DisplayName("3521: InsertNote With Body (hidden set to true)")
	@Order(4)
	public List<DynamicNode> hiddenTrue()
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
				JsonObject apiBody = createApiBody(member.getUpi(), "This note is created by test automation around " + createdDateTime + " (local time)");
				apiBody.addProperty("hidden", "true");

				// run the test
				testSubResults = happyPath(member, apiBody);
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
	@DisplayName("3521: InsertNote With Body (note set to null)")
	@Order(5)
	public List<DynamicNode> noteNull()
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
				JsonObject apiBody = createApiBody(member.getUpi(), null);
				apiBody.add("", null);

				// run the test
				testSubResults = happyPath(member, apiBody);
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
	@DisplayName("5811: InsertNote With Body missing Alert")
	@Order(5)
	public List<DynamicNode> missingAlert()
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
				JsonObject apiBody = createApiBody(member.getUpi(), "This note is created by test automation around " + createdDateTime + " (local time)");
				apiBody.remove("alert");

				// run the test
				testSubResults = happyPath(member, apiBody);
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
	@DisplayName("5812: InsertNote With Body missing Hidden")
	@Order(5)
	public List<DynamicNode> missingHidden()
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
				JsonObject apiBody = createApiBody(member.getUpi(), "This note is created by test automation around " + createdDateTime + " (local time)");
				apiBody.remove("hidden");

				// run the test
				testSubResults = happyPath(member, apiBody);
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
	@DisplayName("5813: InsertNote With Body missing Tombstoned")
	@Order(5)
	public List<DynamicNode> missingTombstoned()
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
				JsonObject apiBody = createApiBody(member.getUpi(), "This note is created by test automation around " + createdDateTime + " (local time)");
				apiBody.remove("tombStoned");

				// run the test
				testSubResults = happyPath(member, apiBody);
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
	@DisplayName("5745: InsertNote With Body with out UPI")
	@Order(5)
	public List<DynamicNode> missingUpi()
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
				JsonObject apiBody = createApiBody(member.getUpi(), "This note is created by test automation around " + createdDateTime + " (local time)");
				apiBody.remove("upi");

				// run the test
				testSubResults = happyPath(member, apiBody);
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
	@DisplayName("5739: InsertNote With Body with UPI (invalid upi)")
	@Order(5)
	public List<DynamicNode> invalidUpi()
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
				JsonObject apiBody = createApiBody(member.getUpi(), "This note is created by test automation around " + createdDateTime + " (local time)");
				apiBody.addProperty("upi", "99999");

				// run the test
				testSubResults = happyPath(member, apiBody);
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
	 * Negative test case
	 */

	@TestFactory
	@DisplayName("3508: InsertNote Invalid Parm")
	@Order(6)
	public List<DynamicNode> invalidParam()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				String memberId = "2_598114";
				String upi = "598114";

				JsonObject apiBody = createApiBody(upi, "This note is created by test automation around " + createdDateTime + " (local time)");

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_NOTE_POST_ENDPOINT,
						apiBody.toString(), new Object[]{memberId}, 400, HTTP_400_BAD_REQUEST);

				apiPostStep.run();
				test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
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
	@DisplayName("3511: InsertNote All Parm Combo Not Exist")
	@Order(7)
	public List<DynamicNode> allInvalidParam()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				String memberId = "9_999999";
				String upi = "999999";

				JsonObject apiBody = createApiBody(upi, "This note is created by test automation around " + createdDateTime + " (local time)");

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_NOTE_POST_ENDPOINT,
						apiBody.toString(), new Object[]{memberId}, 400, HTTP_400_BAD_REQUEST);

				apiPostStep.run();
				test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
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
	@DisplayName("3512: InsertNote Partial Parm Combo Exist")
	@Order(8)
	public List<DynamicNode> partialInvalidParam()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				String memberId = "9_999999";
				String upi = "999999";

				JsonObject apiBody = createApiBody(upi, "This note is created by test automation around " + createdDateTime + " (local time)");

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_NOTE_POST_ENDPOINT,
						apiBody.toString(), new Object[]{memberId}, 400, HTTP_400_BAD_REQUEST);

				apiPostStep.run();
				test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
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
	@DisplayName("3517: InsertNote Invalid Header")
	@Order(9)
	public List<DynamicNode> invalidHeader()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{

			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = "1_078229";
			String upi = "078229";

			JsonObject apiBody = createApiBody(upi, "This note is created by test automation around " + createdDateTime + " (local time)");

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME + "invalid", subscriptionId));

			ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_NOTE_POST_ENDPOINT,
					apiBody.toString(), new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiPostStep.run();
			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3518: InsertNote Header Missing")
	@Order(9)
	public List<DynamicNode> missingHeader()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{

			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = "1_078229";
			String upi = "078229";

			JsonObject apiBody = createApiBody(upi, "This note is created by test automation around " + createdDateTime + " (local time)");

			ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), MEMBER_NOTE_POST_ENDPOINT,
					apiBody.toString(), new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiPostStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3519: InsertNote Valid Parm With Incorrect Header Value")
	@Order(10)
	public List<DynamicNode> incorrectHeaderValue()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{

			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = "1_078229";
			String upi = "078229";

			JsonObject apiBody = createApiBody(upi, "This note is created by test automation around " + createdDateTime + " (local time)");

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, "abc"));

			ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_NOTE_POST_ENDPOINT,
					apiBody.toString(), new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiPostStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3521: InsertNote With Body (alert/hidden set to yes/no; missing tombStone, upi, note")
	@Order(11)
	public List<DynamicNode> invalidBody()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{

			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = "1_078229";

			String jsonBody = "{\r\n"
					+ "    \"alert\": \"yes\",\r\n"
					+ "    \"hidden\": \"no\",\r\n"
					+ "}";

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

			ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_NOTE_POST_ENDPOINT,
					jsonBody, new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiPostStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@Test
	@DisplayName("3524: InsertNote Invalid Auth")
	@Order(12)
	public List<DynamicNode> invalidAuth()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{

			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = "1_078229";
			String upi = "078229";
			JsonObject apiBody = createApiBody(upi, "This note is created by test automation around " + createdDateTime + " (local time)");

			Headers headers = new Headers(new Header(API_HEADER_NAME, subscriptionId));

			ApiPostStep apiPostStep = new ApiPostStep(getHeadersInvalidAuth(headers), MEMBER_NOTE_POST_ENDPOINT,
					apiBody.toString(), new Object[]{memberId}, 401, HTTP_401_UNAUTHORIZED);

			apiPostStep.run();
			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	/**
	 * Generic test for happy path
	 * @param member {@link Member}
	 * @param apiBody {@link JsonObject}
	 * @return test validation result
	 */
	private List<DynamicNode> happyPath(Member member, JsonObject apiBody)
	{
		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

		// Call API
		ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_NOTE_POST_ENDPOINT,
				apiBody.toString(), new Object[]{member.getMemberId()}, 201, null);

		apiPostStep.run();  // perform the actual API call

		MemberNote actualAPIResponse = apiPostStep.convertToJsonDTO(MemberNote.class);  // convert the API response to JSON object

		if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
		{
			return apiPostStep.getTestResults();    // Do not continue if the step did not complete
		}

		//toBeDeleted = actualAPIResponse;	// set this to be deleted after the test completes

		// Create expected member note
		MemberNote expected = createExpectedMemberNote(member.getMemberId(), createdDateTime, apiBody);

		/*
		 * Validations
		 */

		// API
		apiPostStep.getTestResults().add(dynamicContainer("API response", expected.compare(actualAPIResponse)));

		// Query Cosmos
		List<MemberNote> notes =  MemberNoteQueries.getMemberNote(subscriptionId, member.getMemberId());

		apiPostStep.getTestResults().add(dynamicContainer("Cosmos DB", expected.compare(notes.get(0))));
		apiPostStep.getTestResults().add(notes.get(0).schemaValidation() );

		//clean up the data from cosmos db	
		toBeDeleted = actualAPIResponse;	// set this to be deleted after the each tenant test completes

		if ( toBeDeleted != null ) 
		{
			MemberNoteQueries.deleteMemberNote(subscriptionId, toBeDeleted.getId(), toBeDeleted.getMemberId());
		}
		return apiPostStep.getTestResults();
	}

	/**
	 * Create the API body
	 * 
	 * @param upi of the member
	 * @param note to be appended to the note
	 * @return {@link JsonObject}
	 */
	private JsonObject createApiBody(String upi, String note)
	{
		JsonObject requestBody = new JsonObject();

		requestBody.addProperty("alert", "false");
		requestBody.addProperty("hidden", "false");
		requestBody.addProperty("tombStoned", "false");
		requestBody.addProperty("upi", upi);
		requestBody.addProperty("note", note);

		return requestBody;
	}

	/**
	 * Create the expected {@link MemberNote}
	 * 
	 * @param memberId of the member
	 * @param createdDateTime of the note
	 * @param apiBody {@link JsonObject} of the API body
	 * @return {@link MemberNote}
	 */
	private MemberNote createExpectedMemberNote(String memberId, String createdDateTime, JsonObject apiBody)
	{
		// Complete JSON object after API call to set all the different properties
		apiBody.addProperty("memberId",memberId);
		apiBody.addProperty("createUser", RxConciergeUILogin.getAcctName());

 		try {
 			apiBody.addProperty("createDateTime", DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE));
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}
 		
		apiBody.addProperty("type", "note");
		apiBody.addProperty("ver", "1.0");

		Gson gson = new Gson();
		return gson.fromJson(apiBody, MemberNote.class);
	}
}
