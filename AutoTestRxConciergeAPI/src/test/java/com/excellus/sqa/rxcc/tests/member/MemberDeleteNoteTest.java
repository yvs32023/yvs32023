/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import com.excellus.sqa.restapi.steps.ApiDeleteStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.cosmos.MemberNoteQueries;
import com.excellus.sqa.rxcc.cosmos.MemberQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.MemberNote;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.utilities.DateTimeUtils;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * <a href="https://apim-lbs-rxc-tst-east-001.developer.azure-api.net/api-details#api=member&operation=deletenote">Member - Delete Note</a>
 * Required Header: X-RXCC-SUB <ehp|exe|???>
 * 
 * <p><b>Request</b><br/>
 * DEL    https://apim-lbs-rxc-tst-east-001.azure-api.net/api/member/{adTenantId}/members/{memberId}/notes/{noteId}
 * 
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/16/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_NOTE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("DeleteMemberNote")
public class MemberDeleteNoteTest extends RxConciergeAPITestBaseV2 
{

	private static final Logger logger = LoggerFactory.getLogger(MemberDeleteNoteTest.class);

	private String createdDateTime;
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

	/**
	 * Delete any member note that was create with the test
	 */
	@AfterEach
	public void deleteMemberNoteFromCosmos()
	{
		if ( toBeDeleted != null )
		{
			MemberNoteQueries.deleteMemberNote(subscriptionId, toBeDeleted.getId(), toBeDeleted.getMemberId());
		}

		toBeDeleted = null;
		subscriptionId = null;
	}

	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("3659: DeleteNote Happy Path (ALL member note)")
	@Order(1)
	public List<DynamicNode> happyPathAllDelete()
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

				//run the test
				testSubResults = happyPathDelete(member);
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
	@DisplayName("3661: DeleteNote Invalid Parm")
	@Order(2)
	public List<DynamicNode> invalidParam()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String invalidMemberId = "9_123456";
			String invalidNoteId = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

			ApiDeleteStep apiDeleteStep = new ApiDeleteStep(headers, MEMBER_NOTE_DELETE_POST_ENDPOINT,
					new Object[]{invalidMemberId, invalidNoteId}, 404, HTTP_404_NOT_FOUND);
			apiDeleteStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiDeleteStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3665: DeleteNote Partial Parm Combo Exist")
	@Order(3)
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

				ApiDeleteStep apiDeleteStep = new ApiDeleteStep(headers, MEMBER_NOTE_DELETE_POST_ENDPOINT,
						new Object[]{member.getMemberId(), noteIdExists}, 404, HTTP_404_NOT_FOUND);

				apiDeleteStep.run();
				test.add(dynamicContainer(subscriptionId.toUpperCase(), apiDeleteStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Unexpected exception",
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3670: DeleteNote Invalid Header")
	@Order(4)
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

			ApiDeleteStep apiDeleteStep = new ApiDeleteStep(headers, MEMBER_NOTE_DELETE_POST_ENDPOINT,
					new Object[]{memberId, noteId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiDeleteStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiDeleteStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3671: DeleteNote Header Missing")
	@Order(5)
	public List<DynamicNode> missingRequiredHeader()
	{
		String memberId = "9_123456";
		String noteId = UUID.randomUUID().toString();

		Headers headers = getGenericHeaders();

		ApiDeleteStep apiDeleteStep = new ApiDeleteStep(headers, MEMBER_NOTE_DELETE_POST_ENDPOINT,
				new Object[]{memberId, noteId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiDeleteStep.run();

		return apiDeleteStep.getTestResults();
	}

	@TestFactory
	@DisplayName("3672: DeleteNote Valid Parm With Incorrect Header Value")
	@Order(6)
	public List<DynamicNode> invalidHeaderValue()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{

			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = "9_123456";
			String noteId = UUID.randomUUID().toString();

			Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId + "invalid"));

			ApiDeleteStep apiDeleteStep = new ApiDeleteStep(headers, MEMBER_NOTE_DELETE_POST_ENDPOINT,
					new Object[]{memberId, noteId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiDeleteStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiDeleteStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3677: DeleteNote Invalid Auth")
	@Order(7)
	public List<DynamicNode> invalidToken()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{

			subscriptionId = tenant.getSubscriptionName().toUpperCase();

			String memberId = "9_123456";
			String noteId = UUID.randomUUID().toString();

			Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionId));

			ApiDeleteStep apiDeleteStep = new ApiDeleteStep(headers, MEMBER_NOTE_DELETE_POST_ENDPOINT,
					new Object[]{memberId, noteId}, 401, HTTP_401_UNAUTHORIZED);
			apiDeleteStep.run();

			test.add(dynamicContainer(subscriptionId.toUpperCase(), apiDeleteStep.getTestResults())); // add all step test result
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
	 * @param member {@link Member}
	 * @return list of test results
	 */
	private List<DynamicNode> happyPathDelete(Member member)
	{
		// Create member note manually in the Cosmos DB container
		MemberNote memberNote = createMemberNote(member.getMemberId());

		// Call API delete member note
		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

		ApiDeleteStep apiDeleteStep = new ApiDeleteStep(headers, MEMBER_NOTE_DELETE_POST_ENDPOINT,
				new Object[] {memberNote.getMemberId(), memberNote.getId()}, 204, HTTP_204_NO_CONTENT);

		apiDeleteStep.run();

		// set member note to be deleted manually from Cosmos DB since it failed the test
		if ( apiDeleteStep.getResponseStatusCode() != 204 )
		{
			toBeDeleted = memberNote;
			MemberNoteQueries.deleteMemberNote(subscriptionId, toBeDeleted.getId(), toBeDeleted.getMemberId());
			return apiDeleteStep.getTestResults();
		}

		/*
		 * Validation
		 */

		MemberNote actual = MemberNoteQueries.getMemberNote(subscriptionId, memberNote.getId(), memberNote.getMemberId());

		apiDeleteStep.getTestResults().add(dynamicTest("Member note deleted from cosmos",
				() -> assertNull(actual, "The member note " + memberNote + " was not deleted by API")));

		return apiDeleteStep.getTestResults();

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
		memberNote.setNote("This is a note from regression test automation, created around " + createdDateTime);
		memberNote.setCreateUser("Garrett-Mari Cosmiano");
		memberNote.setCreateDateTime( createdDateTime );
		memberNote.setVer("1.0");
		memberNote.setId( UUID.randomUUID().toString() );

		// Manually insert note (test data) into Cosmos DB
		MemberNoteQueries.insertMemberNote(subscriptionId, memberNote);

		return memberNote;
	}
}

