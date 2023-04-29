/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.commons.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.RequestLoggingFilter;
import com.excellus.sqa.restapi.steps.ApiPatchStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.MemberQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * <a href="https://apim-lbs-rxc-tst-east-001.developer.azure-api.net/api-details#api=member&operation=patchmember">Member - Patch (update)</a>
 * Required Header: X-RXCC-SUB <ehp|exe|???>
 * 
 * <pre>
 *   {
 *     "deceased": "boolean",
 *     "optinComm": "boolean",
 *     "correspondenceType": "string"
 *   }
 * </pre>
 * 
 * <p><b>Request</b><br/>
 * PATCH    https://apim-lbs-rxc-tst-east-001.azure-api.net/api/member/{adTenantId}/members/{memberId}
 * 
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/16/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PatchMember")
public class MemberPatchTest extends RxConciergeAPITestBaseV2 
{

	private static final Logger logger = LoggerFactory.getLogger(MemberPatchTest.class);

	private String subscriptionId;

	private Member resetToOriginalMember;	// the Member test data that needs to be reset to the original value/s

	// test data
	private Boolean isDeceased;
	private Boolean isOptInComm;
	private String correspondenceType;

	/**
	 * Delete any member note that was created with the test
	 */
	//	@AfterEach
	//	public void resetToOriginalInCosmos()
	//	{
	//		if ( resetToOriginalMember != null && (isDeceased != null || isOptInComm != null || StringUtils.isNotBlank(correspondenceType)) )
	//		{
	//			MemberQueries.replaceMember(subscriptionId, resetToOriginalMember, 
	//					resetToOriginalMember.getDeceased(), resetToOriginalMember.getOptinComm(), resetToOriginalMember.getCorrespondenceType());
	//		}
	//
	//		isDeceased = null;
	//		isOptInComm = null;
	//		correspondenceType = null;
	//	}

	@TestFactory
	@DisplayName("3411: PatchMember Happy Path (ehp - deceased set to true)")
	@Order(1)
	public List<DynamicNode> happyPathEhpPatch() throws Exception
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
				Member member = MemberQueries.getRandomNonDeceasedMember(subscriptionId);

				// Setup test data
				setupTestData(member);

				// Create API body
				JsonObject body = createApiBody(isDeceased, isOptInComm, correspondenceType);

				//run the test
				testSubResults = happyPathPatch(member, body);
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				testSubResults.add( dynamicTest("Unexpected exception",
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			test.add(dynamicContainer(subscriptionId.toUpperCase(), testSubResults));
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}




	@TestFactory
	@DisplayName("3426: PatchMember With Body (missing 'deceased' property body)")
	@Order(2)
	public List<DynamicNode> missingAlertBody() throws Exception
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
				Member member = MemberQueries.getRandomNonDeceasedMember(subscriptionId);

				// Setup the test data
				setupTestData(member);

				// Create API body
				JsonObject body = createApiBody(null, isOptInComm, correspondenceType);

				//run the test
				testSubResults = happyPathPatch(member, body);
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				testSubResults.add( dynamicTest("Unexpected exception",
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}

			test.add(dynamicContainer(subscriptionId.toUpperCase(), testSubResults));
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}


	@TestFactory
	@DisplayName("3426: PatchMember With Body (missing 'optInComm' property in body)")
	@Order(3)
	public List<DynamicNode> missingHiddenBody() throws Exception
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
				Member member = MemberQueries.getRandomNonDeceasedMember(subscriptionId);

				// Setup the test data
				setupTestData(member);

				// Create API body
				JsonObject body = createApiBody(isDeceased, null, correspondenceType);

				//run the test
				testSubResults = happyPathPatch(member, body);
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				testSubResults.add( dynamicTest("Unexpected exception",
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			test.add(dynamicContainer(subscriptionId.toUpperCase(), testSubResults));
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}


	@TestFactory
	@DisplayName("3426: PatchMember With Body (missing 'correspondenceType' property in body)")
	@Order(4)
	public List<DynamicNode> missingTombStoneBody() throws Exception
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
				Member member = MemberQueries.getRandomNonDeceasedMember(subscriptionId);

				// Setup the test data
				setupTestData(member);

				// Create API body
				JsonObject body = createApiBody(isDeceased, isOptInComm, null);

				//run the test
				testSubResults = happyPathPatch(member, body);
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				testSubResults.add( dynamicTest("Unexpected exception",
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}

			test.add(dynamicContainer(subscriptionId.toUpperCase(), testSubResults));
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	/*
	 * Negative test cases
	 */

	@TestFactory
	@DisplayName("3426: PatchMember With Body (invalid body properties)")
	@Order(5)
	//	@Disabled("API allows this and updates the data in cosmos but not base on the properties but instead removes some properties. "
	//			+ "Need to follow up with Dev/Venkata")
	public List<DynamicNode> bodyInvalidProperties() throws Exception
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
				Member member = MemberQueries.getRandomNonDeceasedMember(subscriptionId);

				// Setup the test data
				setupTestData(member);

				JsonObject requestBody = new JsonObject();
				requestBody.addProperty("gender", "M");
				requestBody.addProperty("address3", "Suite A1");
				requestBody.addProperty("postalCode", "12345");

				//run the test
				testSubResults = happyPathPatch(member, requestBody);
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				testSubResults.add( dynamicTest("Unexpected exception",
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}

			test.add(dynamicContainer(subscriptionId.toUpperCase(), testSubResults));
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("3426: PatchMember With Body (invalid values)")
	@Order(6)
	public List<DynamicNode>  bodyInvalidValues()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			// Get member
			Member member = MemberQueries.getRandomNonDeceasedMember(subscriptionId);

			String body = "{\r\n"
					+ "    \"deceased\": \"No\",\r\n"
					+ "    \"optinComm\": \"Yes\",\r\n"
					+ "    \"correspondenceType\": true\r\n"
					+ "}";

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

			ApiPatchStep apiPatchStep = new ApiPatchStep( headers, MEMBER_PATCH_POST_ENDPOINT, body.toString(), new Object[]{member.getMemberId()}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiPatchStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3413: PatchMember Invalid Parm")
	@Order(7)
	public List<DynamicNode> invalidParam()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String invalidMemberId = "9_123456";

			JsonObject body = createApiBody(true, true, "Other");

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));
			ApiPatchStep apiPatchStep = new ApiPatchStep( headers, MEMBER_PATCH_POST_ENDPOINT, body.toString(), new Object[]{invalidMemberId}, 404, HTTP_404_NOT_FOUND);
			apiPatchStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3417: PatchMember Partial Parm Combo Exist")
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
				// Member doesn't exists
				String invalidMemberId = "9_123456";

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiPatchStep apiPatchStep = new ApiPatchStep( headers, MEMBER_PATCH_POST_ENDPOINT, null, new Object[]{invalidMemberId}, 404, HTTP_404_NOT_FOUND);
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
	@DisplayName("3422: PatchMember Invalid Header")
	@Order(9)
	public List<DynamicNode> invalidHeader()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = "9_123456";

			JsonObject body = createApiBody(true, true, "Other");

			Headers headers = getGenericHeaders(new Header("INVALID_HEADER", subscriptionId));

			ApiPatchStep apiPatchStep = new ApiPatchStep( headers, MEMBER_PATCH_POST_ENDPOINT, body.toString(), new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiPatchStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3423: PatchMember Header Missing")
	@Order(10)
	public  List<DynamicNode> missingRequiredHeader()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = "9_123456";

			JsonObject body = createApiBody(true, true, "Other");
			ApiPatchStep apiPatchStep = new ApiPatchStep( getGenericHeaders(), MEMBER_PATCH_POST_ENDPOINT, body.toString(), new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiPatchStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3424: PatchMember Valid Parm With Incorrect Header Value")
	@Order(11)
	public List<DynamicNode> invalidHeaderValue()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName().toUpperCase();
			String memberId = "9_123456";

			JsonObject body = createApiBody(true, true, "Other");

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, "abc"));

			ApiPatchStep apiPatchStep = new ApiPatchStep(headers, MEMBER_PATCH_POST_ENDPOINT, body.toString(), new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiPatchStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPatchStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3429: PatchMember Invalid Auth")
	@Order(12)
	public List<DynamicNode> invalidToken()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = "9_123456";

			JsonObject body = createApiBody(true, true, "Other");

			Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionId));
			ApiPatchStep apiPatchStep = new ApiPatchStep( getHeadersInvalidAuth(headers), MEMBER_PATCH_POST_ENDPOINT, body.toString(), new Object[]{memberId}, 401, HTTP_401_UNAUTHORIZED);
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
	 * @param expected
	 * @throws IllegalArgumentException 
	 * @throws JsonProcessingException 
	 */
	private List<DynamicNode> happyPathPatch(Member expected, JsonObject body) throws JsonProcessingException, IllegalArgumentException
	{
		// Update the expected member base on the API body
		Member updatedMember = updateMember(expected, body);

		// Call API patch member
		//Response response = patchMemberNoteAPI(expected, body.toString());

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

		// Call API
		ApiPatchStep apiPatchStep = new ApiPatchStep(headers, MEMBER_PATCH_POST_ENDPOINT,
				body.toString(), new Object[]{expected.getMemberId()}, 200, null);

		apiPatchStep.run();  // perform the actual API call

		Member actualAPIResponse = apiPatchStep.convertToJsonDTO(Member.class);  // convert the API response to JSON object

		/*
		 * Validation
		 */

		// Get the member from the Cosmos
		Member actual = MemberQueries.getMember(subscriptionId, expected.getId());

		List<DynamicNode> test = new ArrayList<DynamicNode>();

		// This is just to display the member id being used
		test.add(dynamicTest("MemberId [" + updatedMember.getMemberId() + "]", 
				() -> assertEquals( updatedMember.getMemberId(), actual.getMemberId()) ));

		test.add(dynamicTest("API status code [200]", () -> assertEquals( 200, apiPatchStep.getResponseStatusCode()) ));

		// Set resetToOriginalMember to the member that is being updated
		if ( apiPatchStep.getResponseStatusCode() == 200 )
			resetToOriginalMember = expected;
		else
			return test;


		test.add(dynamicContainer("API response", updatedMember.compare( actualAPIResponse)));
		test.add(dynamicContainer("Cosmos db", updatedMember.compare( actual )));

		if ( resetToOriginalMember != null && (isDeceased != null || isOptInComm != null || StringUtils.isNotBlank(correspondenceType)) )
		{
			MemberQueries.replaceMember(subscriptionId, resetToOriginalMember, 
					resetToOriginalMember.getDeceased(), resetToOriginalMember.getOptinComm(), resetToOriginalMember.getCorrespondenceType());
		}

		isDeceased = null;

		return test;
	}

	/**
	 * Setup the test data. This will just reverse the the boolean values of deceased & optinComm and update correspondentType value
	 * @param member
	 */
	public void setupTestData(Member member)
	{
		isDeceased = (member.getDeceased() != null && member.getDeceased()) ? false : true;
		isOptInComm = (member.getOptinComm() != null && member.getOptinComm()) ? false : true;

		switch ( member.getCorrespondenceType().toLowerCase() ) {
		case ("home"):
			correspondenceType = "Cell";
		break;

		case ("cell"):
			correspondenceType = "Home";
		break;

		default:
			correspondenceType = "Other";
			break;
		}
	}

	/**
	 * Create API body
	 * 
	 * @param theDeceased property true/false
	 * @param theOptInComm property true/false
	 * @param theCorrespondentType property, either email or letter
	 * @return JsonObject that represent the params
	 */
	private JsonObject createApiBody(Boolean theDeceased, Boolean theOptInComm, String theCorrespondentType)
	{
		JsonObject requestBody = new JsonObject();

		if ( this.isDeceased != null )
			requestBody.addProperty("deceased", String.valueOf(isDeceased));

		if ( this.isOptInComm != null )
			requestBody.addProperty("optinComm", String.valueOf(isOptInComm));

		if ( this.correspondenceType != null )
			requestBody.addProperty("correspondenceType", correspondenceType);

		return requestBody;
	}

	/**
	 * Update the expected Member base on the API body
	 * 
	 * @param expected {@link Member} to be updated
	 * @param body {@link JsonObject} that contains the updated member data
	 * @throws IllegalArgumentException 
	 * @throws JsonProcessingException 
	 */
	private Member updateMember(Member expected, JsonObject body) throws JsonProcessingException, IllegalArgumentException
	{
		Member updatedMember = expected.deepCopy(Member.class);

		if ( body.get("deceased") != null && !body.get("deceased").isJsonNull() )
			updatedMember.setDeceased(isDeceased);

		if ( body.get("optinComm") != null && !body.get("optinComm").isJsonNull() )
			updatedMember.setOptinComm(isOptInComm);

		if ( body.get("correspondenceType") != null && !body.get("correspondenceType").isJsonNull() )
			updatedMember.setCorrespondenceType(correspondenceType);

		try {
			updatedMember.setLastUpdated(DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE));
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}
		
		updatedMember.setLastUpdatedBy( RxConciergeUILogin.getAcctName() );

		return updatedMember;
	}
}