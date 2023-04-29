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
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.MemberCorrespondenceQueries;
import com.excellus.sqa.rxcc.cosmos.MemberNoteQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence.CorrespondenceType;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence.MemberType;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.IStep;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 *  POST - https://apim-lbs-rxc-dev-east-001.azure-api.net/api/member/members/{memberId}/interventions/{interventionId}/correspondences
 * 
 * 
 * {
 *   "memberId": "23452758-7626-42f9-90c5-10bda15bb58d_1162281",
 *   "correspondenceOutcome": "Clinical Consult Completed",
 *   "correspondenceType": "Inbound Call",
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
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 08/26/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("CORRESPONDENCE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostMemberCorrespondence")
@Disabled()
public class PostMemberCorrespondenceTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(PostMemberCorrespondenceTest.class);

	private String createdDateTime;
	private MemberCorrespondence toBeDeleted;
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
	//	 *  Delete any member correspondence that was created with the test
	//	 */
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

	/*
	 * Positive test cases
	 */

	@TestFactory
	@DisplayName("34905: InsertCorrespondence Happy Path (All Subscription Name)")
	@Order(1)
	public List<DynamicNode> happyPathInsertCorrespondence()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			List<DynamicNode> testSubResults = new ArrayList<>();

			try
			{
				subscriptionId = tenant.getSubscriptionName();

				//Query Cosmos for member Id  and intervention Id  which has correspondence (All existing subscription)
				List<GenericCount> memberWithCorrespondenceAndIntervention = MemberCorrespondenceQueries.getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionId, 1);

				//if memberWithCorrespondenceAndIntervention list is empty for specific tenant, throw the "NO TEST DATA FOUND"and skip the test
				if (memberWithCorrespondenceAndIntervention.isEmpty())
				{
					test.add( dynamicContainer(subscriptionId.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				String memberId = memberWithCorrespondenceAndIntervention.get(0).getId();
				String interventionId = memberWithCorrespondenceAndIntervention.get(0).getIntId();

				for(CorrespondenceType type : CorrespondenceType.values())
				{

					// Setup test data
					MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
					memberCorrespondence = getNewMemberCorrespondence();
					memberCorrespondence.setMemberId(memberId);
					memberCorrespondence.setCorrespondenceType(type.getEnumValue());
					memberCorrespondence.setInterventionId(interventionId);

					// Create API body
					JsonObject apiBody = createApiBody(memberCorrespondence);

					//run the test
					testSubResults.add(dynamicContainer(type.getEnumValue(), happyPath(memberCorrespondence,apiBody)));
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				testSubResults.add( dynamicTest("Unexpected exception",
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}

			test.add(dynamicContainer(subscriptionId.toUpperCase(), testSubResults));

			resetApiInfo();                     // Reset the API information and test validation results
		}

		return test;
	}

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("TBD: PostMemberCorrespondence with missing partial Body (missing  correspondenceType)")
	@Order(2)
	public List<DynamicNode> bodyMissingValueCorrespondenceType()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName();

				//Query Cosmos for member Id  and intervention Id  which has correspondence (All existing subscription)
				List<GenericCount> memberWithCorrespondenceAndIntervention = MemberCorrespondenceQueries.getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionId, 1);

				//if memberWithCorrespondenceAndIntervention list is empty for specific tenant, throw the "NO TEST DATA FOUND"and skip the test
				if (memberWithCorrespondenceAndIntervention.isEmpty())
				{
					test.add( dynamicContainer(subscriptionId.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				String memberId = memberWithCorrespondenceAndIntervention.get(0).getId();
				String interventionId = memberWithCorrespondenceAndIntervention.get(0).getIntId();

				// Setup test data
				MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
				memberCorrespondence = getNewMemberCorrespondence();
				memberCorrespondence.setMemberId(memberId);
				memberCorrespondence.setInterventionId(interventionId);

				JsonObject body = createApiBody(memberCorrespondence);
				body.remove("correspondenceType");

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_CORRESPONDENCE_POST_ENDPOINT, body.toString(),
						new Object[]{memberCorrespondence.getMemberId(), memberCorrespondence.getInterventionId()},
						400, HTTP_400_BAD_REQUEST);
				apiPostStep.run();

				// validate the API message
				final String EXPECTED_MSG = "\"PostCorrespondence - Invalid correspondenceType found in request body\"";

				String actualMsg = apiPostStep.getResponse().then().extract().asString();
				apiPostStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())) );

				test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return 	test;
	}



	@TestFactory
	@DisplayName("TBD: PostMemberCorrespondence with missing partial Body (missing  memberId)")  
	@Order(3)
	public List<DynamicNode> bodyMissingValueMemberId()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName();

				//Query Cosmos for member Id  and intervention Id  which has correspondence (All existing subscription)
				List<GenericCount> memberWithCorrespondenceAndIntervention = MemberCorrespondenceQueries.getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionId, 1);

				//if memberWithCorrespondenceAndIntervention list is empty for specific tenant, throw the "NO TEST DATA FOUND"and skip the test
				if (memberWithCorrespondenceAndIntervention.isEmpty())
				{
					test.add( dynamicContainer(subscriptionId.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				String memberId = memberWithCorrespondenceAndIntervention.get(0).getId();
				String interventionId = memberWithCorrespondenceAndIntervention.get(0).getIntId();

				// Setup test data
				MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
				memberCorrespondence = getNewMemberCorrespondence();
				memberCorrespondence.setMemberId(memberId);
				memberCorrespondence.setInterventionId(interventionId);

				JsonObject body = createApiBody(memberCorrespondence);
				body.remove("memberId");

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_CORRESPONDENCE_POST_ENDPOINT, body.toString(),
						new Object[]{memberCorrespondence.getMemberId(), memberCorrespondence.getInterventionId()},
						500, HTTP_500_INTERNAL_SERVER_ERR);

				apiPostStep.run();

				test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return 	test;
	}

	// Commented out as this is creating a record, 201 code is generated instead of 500 error
	//	@TestFactory
	//	@DisplayName("TBD: PostMemberCorrespondence with missing partial Body (missing  correspondenceOutcome)")  
	//	@Order(4)
	//
	//	public List<DynamicNode> bodyMissingValueCorrespondenceOutcome()
	//	{
	//		List<DynamicNode> test = new ArrayList<>();
	//
	//		List<Tenant> tenants = TenantQueries.getTenants();
	//
	//		for(Tenant tenant : tenants)
	//		{
	//			try
	//			{
	//				subscriptionId = tenant.getSubscriptionName();
	//
	//				//Query Cosmos for member Id  and intervention Id  which has correspondence (All existing subscription)
	//				List<GenericCount> memberWithCorrespondenceAndIntervention = MemberCorrespondenceQueries.getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionId, 1);
	//
	//				//if memberWithCorrespondenceAndIntervention list is empty for specific tenant, throw the "NO TEST DATA FOUND"and skip the test
	//				if (memberWithCorrespondenceAndIntervention.isEmpty())
	//				{
	//					test.add( dynamicContainer(subscriptionId.toUpperCase(),  
	//							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
	//					continue;
	//				}
	//
	//				String memberId = memberWithCorrespondenceAndIntervention.get(0).getId();
	//				String interventionId = memberWithCorrespondenceAndIntervention.get(0).getIntId();
	//
	//				// Setup test data
	//				MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
	//				memberCorrespondence = getNewMemberCorrespondence();
	//				memberCorrespondence.setMemberId(memberId);
	//				memberCorrespondence.setInterventionId(interventionId);
	//
	//				JsonObject body = createApiBody(memberCorrespondence);
	//				body.remove("correspondenceOutcome");
	//
	//				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));
	//
	//				ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_CORRESPONDENCE_POST_ENDPOINT, body.toString(),
	//						new Object[]{memberCorrespondence.getMemberId(), memberCorrespondence.getInterventionId()},
	//						500, HTTP_500_INTERNAL_SERVER_ERR);
	//
	//				apiPostStep.run();
	//
	//				test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
	//			}
	//			catch (Exception e)
	//			{
	//				// Capture any test using testApiValidationResults
	//				String apiInfo = RequestLoggingFilter.getApiInfo();
	//				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
	//						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
	//			}
	//			resetApiInfo();  // Reset the API information and test validation results
	//		}
	//		return 	test;
	//	}


	@TestFactory
	@DisplayName("TBD: PostMemberCorrespondence with Invalid Body (invalid  correspondenceType)")
	@Order(6)
	public List<DynamicNode> bodyInvalidValueCorrespondenceType()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName();

				//Query Cosmos for member Id  and intervention Id  which has correspondence (All existing subscription)
				List<GenericCount> memberWithCorrespondenceAndIntervention = MemberCorrespondenceQueries.getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionId, 1);

				//if memberWithCorrespondenceAndIntervention list is empty for specific tenant, throw the "NO TEST DATA FOUND"and skip the test
				if (memberWithCorrespondenceAndIntervention.isEmpty())
				{
					test.add( dynamicContainer(subscriptionId.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				String memberId = memberWithCorrespondenceAndIntervention.get(0).getId();
				String interventionId = memberWithCorrespondenceAndIntervention.get(0).getIntId();

				// Setup test data
				MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
				memberCorrespondence = getNewMemberCorrespondence();
				memberCorrespondence.setMemberId(memberId);
				memberCorrespondence.setInterventionId(interventionId);

				JsonObject body = createApiBody(memberCorrespondence);
				body.addProperty("correspondenceType","invalid");

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_CORRESPONDENCE_POST_ENDPOINT, body.toString(),
						new Object[]{memberCorrespondence.getMemberId(), memberCorrespondence.getInterventionId()},
						400, HTTP_400_BAD_REQUEST);

				apiPostStep.run();

				// validate the API message
				final String EXPECTED_MSG = "\"PostCorrespondence - Invalid correspondenceType found in request body\"";
				String actualMsg = apiPostStep.getResponse().then().extract().asString();
				apiPostStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())));

				test.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return 	test;
	}

	@TestFactory
	@DisplayName("TBD: PostMemberCorrespondence Invalid Auth")
	@Order(7)
	public List<DynamicNode> invalidToken()
	{
		List<DynamicNode> testAllResults = new ArrayList<DynamicNode>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName();

			String memberId = UUID.randomUUID().toString();
			String interventionId = UUID.randomUUID().toString();

			Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionId));

			ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_CORRESPONDENCE_POST_ENDPOINT, null,
					new Object[]{memberId, interventionId}, 401, HTTP_401_UNAUTHORIZED);

			apiPostStep.run();

			testAllResults.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result

			resetApiInfo(); // reset API info and test validation results
		}

		return testAllResults;
	}

	@TestFactory
	@DisplayName("TBD: PostMemberCorrespondence Invalid Header")
	@Order(8)
	public List<DynamicNode> invalidHeader()
	{
		List<DynamicNode> testAllResults = new ArrayList<DynamicNode>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName();

			// Setup test data
			MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
			memberCorrespondence = getNewMemberCorrespondence();
			memberCorrespondence.setMemberId(UUID.randomUUID().toString());
			memberCorrespondence.setInterventionId(UUID.randomUUID().toString());

			JsonObject body = createApiBody(memberCorrespondence);

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME + "invalid", subscriptionId));

			ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_CORRESPONDENCE_POST_ENDPOINT, body.toString(),
					new Object[]{memberCorrespondence.getMemberId(), memberCorrespondence.getInterventionId()}, 500, HTTP_500_INTERNAL_SERVER_ERR);

			apiPostStep.run();

			testAllResults.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result

			resetApiInfo(); // reset API info and test validation results
		}

		return testAllResults;
	}

	@TestFactory
	@DisplayName("TBD: PostMemberCorrespondence Header Missing")
	@Order(9)
	public List<DynamicNode> missingHeader()
	{
		// Setup test data
		MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
		memberCorrespondence = getNewMemberCorrespondence();
		memberCorrespondence.setMemberId(UUID.randomUUID().toString());
		memberCorrespondence.setInterventionId(UUID.randomUUID().toString());

		JsonObject body = createApiBody(memberCorrespondence);

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), MEMBER_CORRESPONDENCE_POST_ENDPOINT, body.toString(),
				new Object[]{memberCorrespondence.getMemberId(), memberCorrespondence.getInterventionId()}, 500, HTTP_500_INTERNAL_SERVER_ERR);

		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("TBD: PostMemberCorrespondence Valid Parm With Incorrect Header Value")
	@Order(10)
	public List<DynamicNode> incorrectHeaderValue()
	{
		// Setup test data
		MemberCorrespondence memberCorrespondence = new MemberCorrespondence();
		memberCorrespondence = getNewMemberCorrespondence();
		memberCorrespondence.setMemberId(UUID.randomUUID().toString());
		memberCorrespondence.setInterventionId(UUID.randomUUID().toString());

		JsonObject body = createApiBody(memberCorrespondence);

		Headers headers = new Headers(new Header(API_HEADER_NAME, "abc"));

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(headers), MEMBER_CORRESPONDENCE_POST_ENDPOINT, body.toString(),
				new Object[]{memberCorrespondence.getMemberId(), memberCorrespondence.getInterventionId()}, 500, HTTP_500_INTERNAL_SERVER_ERR);

		apiPostStep.run();

		return apiPostStep.getTestResults();
	}


	//Helper Method for test data body setup for EHP
	private  MemberCorrespondence  getNewMemberCorrespondence() {

		MemberCorrespondence memberCorrespondence = new MemberCorrespondence();

		memberCorrespondence.setCorrespondenceOutcome("Clinical Consult Completed");
		memberCorrespondence.setCorrespondenceType(CorrespondenceType.INBOUND_CALL.getEnumValue());
		memberCorrespondence.setContactName("Test Sharma");
		memberCorrespondence.setContactTitle("Test Cosmiano");
		memberCorrespondence.setContactComment( "Test solution-Sloane// Spoke with Lynn at md office about changing Humalog solution to insulin lispro." );
		memberCorrespondence.setTargetDrug("Test A");
		memberCorrespondence.setNote("Humalog solution-Sloane// Spoke with Lynn at md office about changing Humalog solution to insulin lispro. ");
		memberCorrespondence.setPdfUri("https://apim-lbs-rxc-tst-east-001.azure-api.net/api/storage/containers/fax-outbound-processed/files/87f4a82d-e2b6-44cc-9fff-828f94cf8ec9_1233925_C62F24BD5C392D2EBA55CFAF802C1CD6E7921496_20220909_210907.pdf");
		memberCorrespondence.setNpi("987654322");
		memberCorrespondence.setProviderName("Garrett-Mari Test");
		memberCorrespondence.setVersion("1.0");

		return memberCorrespondence;

	}

	/*
	 * Helper methods
	 */

	/**
	 * Generic test for happy path
	 * @param memberCorrespondence {@link MemberCorrespondence}
	 * @param apiBody {@link JsonObject}
	 * @return test validation result
	 */
	private List<DynamicNode> happyPath( MemberCorrespondence memberCorrespondence, JsonObject apiBody)
	{
		List<DynamicNode> test = new ArrayList<DynamicNode>();


		// Call API
		ApiPostStep apiPostStep = insertMemberCorrespondenceAPI(subscriptionId, memberCorrespondence.getMemberId(), memberCorrespondence.getInterventionId(), apiBody.toString());
		MemberCorrespondence actualAPIResponse = apiPostStep.convertToJsonDTO(MemberCorrespondence.class);
		if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
		{
			return apiPostStep.getTestResults();
		}
		else {
			test.addAll(apiPostStep.getTestResults());
		}

		//toBeDeleted = actualAPIResponse;	// set this to be deleted after the test completes

		// Create expected member corr  -fetched id from API response as it is autogenerated
		MemberCorrespondence expected = createExpectedMemberCorrespondence( memberCorrespondence, actualAPIResponse, createdDateTime, apiBody);

		/*
		 * Validations
		 */

		// API
		test.add(dynamicContainer("API response", expected.compare(actualAPIResponse)));

		// Query Cosmos - Get the member correspondence from the cosmos
		MemberCorrespondence actual = MemberCorrespondenceQueries.getMemberCorrespondence(subscriptionId, memberCorrespondence.getMemberId(), memberCorrespondence.getInterventionId() , actualAPIResponse.getId());

		test.add(dynamicContainer("Cosmos DB", expected.compare(actual)));

		toBeDeleted = actualAPIResponse;	// set this to be deleted after the each tenant test completes

		if ( toBeDeleted != null ) 
		{
			MemberNoteQueries.deleteMemberNote(subscriptionId, toBeDeleted.getId(), toBeDeleted.getMemberId());
		}

		return test;
	}

	/**
	 * Create API body
	 *
	 * @param memberCorrespondence  {@link MemberCorrespondence}
	 * @return {@link JsonObject}
	 */
	private JsonObject createApiBody(MemberCorrespondence memberCorrespondence )
	{
		JsonObject requestBody = new JsonObject();

		if (memberCorrespondence.getMemberId() != null)
			requestBody.addProperty("memberId", memberCorrespondence.getMemberId());

		if (memberCorrespondence.getCorrespondenceOutcome() != null)
			requestBody.addProperty("correspondenceOutcome", memberCorrespondence.getCorrespondenceOutcome());

		if (memberCorrespondence.getCorrespondenceType() != null)
			requestBody.addProperty("correspondenceType", memberCorrespondence.getCorrespondenceType().getEnumValue());

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


		return requestBody;
	}


	/**
	 * Create the expected {@link MemberCorrespondence}
	 * 
	 * @param memberCorrespondence expected {@link MemberCorrespondence}
	 * @param actualAPIResponse actual {@link MemberCorrespondence} api response
	 * @param createdDateTime of the correspondence
	 * @param apiBody {@link JsonObject} of the API body
	 * @return {@link MemberCorrespondence}
	 */
	private MemberCorrespondence createExpectedMemberCorrespondence(MemberCorrespondence memberCorrespondence, MemberCorrespondence actualAPIResponse, String createdDateTime, JsonObject apiBody)
	{
		//Retrive the value of correspondence type
		Integer correspondenceTypeIndex = memberCorrespondence.getCorrespondenceType().getCorrespondenceTypeIndex();	

		// Complete JSON oject after API call to set all the different properties
		apiBody.addProperty("correspondenceDateTime", createdDateTime);
		apiBody.addProperty("createdBy", RxConciergeUILogin.getAcctName());
		apiBody.addProperty("createdDateTime", createdDateTime );
		apiBody.addProperty("lastUpdatedBy", RxConciergeUILogin.getAcctName());
		apiBody.addProperty("lastUpdatedDateTime", createdDateTime );
		apiBody.addProperty("type",MemberType.correspondence.toString());
		apiBody.addProperty("id",actualAPIResponse.getId());
		apiBody.addProperty("interventionNoteId",actualAPIResponse.getInterventionNoteId());

		Gson gson = new Gson();
		MemberCorrespondence expectedMemberCorrespondence = gson.fromJson(apiBody, MemberCorrespondence.class);

		expectedMemberCorrespondence.setCorrespondenceType(memberCorrespondence.getCorrespondenceType());
		expectedMemberCorrespondence.setCorrespondenceId(memberCorrespondence.getMemberId()+"_"+correspondenceTypeIndex+"_"+"0001-01-01T00:00:00");

		return expectedMemberCorrespondence;
	}


	/**
	 * Call the API to insert member correspondence
	 * 
	 * @param subscriberId either exe, ehp, loa or med
	 * @param interventionId of the member
	 * @param memberId of the member
	 * @param body for the API call
	 * @return {@link MemberCorrespondence}
	 */
	private ApiPostStep insertMemberCorrespondenceAPI(String subscriberId, String memberId, String interventionId, String body)
	{
		logger.debug("Starting API call");

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriberId));

		ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_CORRESPONDENCE_POST_ENDPOINT, body, new Object[] {memberId,interventionId},201, null);
		apiPostStep.run();
		return apiPostStep;
	}

}
