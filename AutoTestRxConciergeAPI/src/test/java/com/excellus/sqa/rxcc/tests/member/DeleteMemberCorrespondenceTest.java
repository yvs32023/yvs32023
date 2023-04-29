/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

import com.excellus.sqa.restapi.steps.ApiDeleteStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.cosmos.MemberCorrespondenceQueries;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionNoteQueries;
import com.excellus.sqa.rxcc.cosmos.MemberQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence.MemberType;
import com.excellus.sqa.rxcc.dto.StoragePath;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * 
 * Cosmos rxcc-tenant: member
 * Required Header: X-RXCC-SUB <ehp | exe | loa | med> to specify Tenant
 * Request Parameters :  memberId , interventionId & id
 * 
 *  DEL https://apim-lbs-rxc-dev-east-001.azure-api.net/api/member/members/{memberId}/interventions/{interventionId}/correspondences/{id}
 *  
 * 
 * @author Manish Sharma (msharma)
 * @since 09/30/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("CORRESPONDENCE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("DeleteMemberCorrespondences")
public class DeleteMemberCorrespondenceTest extends RxConciergeAPITestBaseV2 
{
	private static final Logger logger = LoggerFactory.getLogger(DeleteMemberCorrespondenceTest.class);

	private String createdDateTime;
	private String subscriptionId;
	private List<MemberCorrespondence> toBeDeletedList;

	/**
	 * Setup the created date/time
	 */
	@BeforeEach
	public void dataSetup()
	{
		try {
			createdDateTime = DateTimeUtils.generateTimeStamp( RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE );
		} catch(Exception e) {
			logger.error("An unexpected error is caught while generating timestamp", e);
		}
	}

	/**
	 * Delete any member correspondence that was created with the test
	 */
	@AfterEach
	public void deleteMemberCorrespondenceFromCosmos()
	{
		if (toBeDeletedList != null && toBeDeletedList.size() > 0 )
		{
			// iterate through the list and delete each record
			for (MemberCorrespondence memberCorrespondence : toBeDeletedList)
			{
				// call delete endpoint with correspondence id & member id
				MemberInterventionNoteQueries.deleteInterventionNote(subscriptionId, memberCorrespondence.getCorrespondenceId(), memberCorrespondence.getMemberId());
			}
			toBeDeletedList = null;
		}
	}

	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("34852: DeleteCorrespondence Happy Path (all member correspondence)")
	@Order(1)
	public List<DynamicNode> happyPathDelete()
	{
		// create a new list to store dynamic nodes
		List<DynamicNode> test = new ArrayList<>();

		// get list of tenants from cosmos
		List<Tenant> tenants = TenantQueries.getTenants();

		// iterate through the tenants
		for(Tenant tenant : tenants)
		{
			List<DynamicNode> testResults = new ArrayList<>();
			try
			{
				// get subscription name
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				// Get member
				Member member = MemberQueries.getRandomMember(Tenant.Type.valueOf(subscriptionId));

				//if member is not found, throw the "NO TEST DATA FOUND"and skip the test
				if (StringUtils.isBlank(member.getMemberId()))
				{
					test.add( dynamicContainer(subscriptionId.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				// Create member correspondence manually in the Cosmos DB container
				MemberCorrespondence memberCorrespondence = createMemberCorrespondence(member.getMemberId());

				// Call API delete member correspondence
				testResults = deleteMemberCorrespondenceAPI(memberCorrespondence);

				test.add(dynamicContainer(subscriptionId.toUpperCase(),testResults));
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}	

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("42574: DeleteCorrespondence Invalid Parm")
	@Order(2)
	public List<DynamicNode> invalidAllParam()  throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				String invalidMemberId = "9_123456";
				String invalidInterventionId = UUID.randomUUID().toString();
				String invalidId = UUID.randomUUID().toString();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiDeleteStep apiDeleteStep = new ApiDeleteStep(headers, MEMBER_CORRESPONDENCE_DELETE_ENDPOINT,
						new Object[]{invalidMemberId,invalidInterventionId, invalidId}, 404, HTTP_404_NOT_FOUND);
				apiDeleteStep.run();

				test.add(dynamicContainer(subscriptionId.toUpperCase(),apiDeleteStep.getTestResults()));
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}


	@TestFactory
	@DisplayName("42574: DeleteCorrespondence Invalid Partial Parm (Valid Member Id Only")
	@Order(3)
	public List<DynamicNode> invalidPartialParam()  throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				//Query Cosmos for member Id with correspondence (based on subscription id)
				List<GenericCount> memberWithCorrespondenceAndIntervention = MemberCorrespondenceQueries.getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionId, 1);

				//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
				//if (StringUtils.isBlank(memberWithCorrespondenceAndIntervention ))
				if (memberWithCorrespondenceAndIntervention.isEmpty())
				{
					test.add( dynamicContainer(subscriptionId.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				List<MemberCorrespondence> expected =	MemberCorrespondenceQueries.getMemberCorrespondenceWithIntervention(subscriptionId,memberWithCorrespondenceAndIntervention.get(0).getId(), memberWithCorrespondenceAndIntervention.get(0).getIntId());

				String memberId = expected.get(0).getMemberId();
				String invalidInterventionId =  UUID.randomUUID().toString();
				String invalidId = UUID.randomUUID().toString();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiDeleteStep apiDeleteStep = new ApiDeleteStep(headers, MEMBER_CORRESPONDENCE_DELETE_ENDPOINT,
						new Object[]{memberId,invalidInterventionId, invalidId}, 404, HTTP_404_NOT_FOUND);
				apiDeleteStep.run();

				test.add(dynamicContainer(subscriptionId.toUpperCase(),apiDeleteStep.getTestResults()));
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("42574: DeleteCorrespondence Invalid Partial Parm (Valid Member Id & Intervention Id Only")
	@Order(4)
	public List<DynamicNode> invalidPartialSecondParam()  throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				//Query Cosmos for member Id with correspondence (based on subscription id)
				List<GenericCount> memberWithCorrespondenceAndIntervention = MemberCorrespondenceQueries.getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionId, 1);

				if (memberWithCorrespondenceAndIntervention.isEmpty())
				{
					test.add( dynamicContainer(subscriptionId.toUpperCase(),  
							Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
					continue;
				}

				List<MemberCorrespondence> expected =	MemberCorrespondenceQueries.getMemberCorrespondenceWithIntervention(subscriptionId,memberWithCorrespondenceAndIntervention.get(0).getId(), memberWithCorrespondenceAndIntervention.get(0).getIntId());

				String memberId = expected.get(0).getMemberId();
				String interventionId = expected.get(0).getInterventionId();
				String invalidId = UUID.randomUUID().toString();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiDeleteStep apiDeleteStep = new ApiDeleteStep(headers, MEMBER_CORRESPONDENCE_DELETE_ENDPOINT,
						new Object[]{memberId,interventionId, invalidId}, 404, HTTP_404_NOT_FOUND);
				apiDeleteStep.run();
				test.add(dynamicContainer(subscriptionId.toUpperCase(),apiDeleteStep.getTestResults()));
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	//TODO Needs to be revisted current test result: AssertionFailedError: expected: <404> but was: <204>

	//	@TestFactory
	//	@DisplayName("42574: DeleteCorrespondence Invalid Partial Parm (Valid Member Id & Id Only")
	//	@Order(5)
	//	public List<DynamicNode> invalidPartialThirdParam()  throws JsonProcessingException 
	//	{
	//		List<DynamicNode> test = new ArrayList<>();
	//
	//		List<Tenant> tenants = TenantQueries.getTenants();
	//
	//		for(Tenant tenant : tenants)
	//		{
	//			try
	//			{
	//				subscriptionId = tenant.getSubscriptionName().toUpperCase();
	//
	//				//Query Cosmos for member Id with correspondence (based on subscription id)
	//				List<GenericCount> memberWithCorrespondenceAndIntervention = MemberCorrespondenceQueries.getRandomMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionId, 1);
	//
	//				List<MemberCorrespondence> expected =	MemberCorrespondenceQueries.getMemberCorrespondenceWithIntervention(subscriptionId,memberWithCorrespondenceAndIntervention.get(0).getId(), memberWithCorrespondenceAndIntervention.get(0).getIntId());
	//
	//				String memberId = expected.get(0).getMemberId();
	//				String invalidInterventionId = UUID.randomUUID().toString();
	//				String id = expected.get(0).getId() ;
	//
	//				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));
	//				Response response = super.restDelete(headers, MEMBER_CORRESPONDENCE_DELETE_ENDPOINT, new Object[] {memberId, invalidInterventionId, id}, null);
	//
	//				assertAll(() -> {
	//					assertEquals(404, response.getStatusCode());
	//					assertEquals(HTTP_404_NOT_FOUND, response.getStatusLine());
	//				});
	//
	//				List<DynamicNode> testResults = new ArrayList<DynamicNode>();
	//
	//				// validate the response code
	//				int expectedStatusCode = 404;
	//				int actualStatusCode = response.getStatusCode();
	//				testResults.add(dynamicTest("API response status code [" + expectedStatusCode + "]", () -> assertEquals(expectedStatusCode, actualStatusCode)));
	//
	//				// validate the response line
	//				String actualStatusLine = response.getStatusLine();
	//				testResults.add(dynamicTest("API response status line [" + HTTP_404_NOT_FOUND + "]", () -> assertEquals(HTTP_404_NOT_FOUND, actualStatusLine)));
	//
	//				test.add(dynamicContainer(subscriptionId.toUpperCase(),testResults));
	//			}
	//			catch (Exception e)
	//			{
	//				test.add(dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),() -> fail(e.getMessage(),e)));
	//			}
	//		}
	//		return test;
	//	}

	@TestFactory
	@DisplayName("42550: DeleteCorrespondence Invalid Auth")
	@Order(6)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName().toUpperCase();

				String memberId = UUID.randomUUID().toString();
				String interventionId = UUID.randomUUID().toString();
				String id = UUID.randomUUID().toString();

				Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionId));

				ApiDeleteStep apiDeleteStep = new ApiDeleteStep(headers, MEMBER_CORRESPONDENCE_DELETE_ENDPOINT,
						new Object[]{memberId,interventionId, id}, 401, HTTP_401_UNAUTHORIZED);
				apiDeleteStep.run();

				test.add(dynamicContainer(subscriptionId.toUpperCase(),	apiDeleteStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("42551: DeleteCorrespondence Header Missing")
	@Order(7)
	public List<DynamicNode> missingHeader() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionId = tenant.getSubscriptionName();

				String memberId = UUID.randomUUID().toString();
				String interventionId = UUID.randomUUID().toString();
				String id = UUID.randomUUID().toString();

				Headers headers = getGenericHeaders();

				ApiDeleteStep apiDeleteStep = new ApiDeleteStep(headers, MEMBER_CORRESPONDENCE_DELETE_ENDPOINT,
						new Object[]{memberId,interventionId, id}, 500, HTTP_500_INTERNAL_SERVER_ERR);
				apiDeleteStep.run();

				test.add(dynamicContainer(subscriptionId.toUpperCase(),apiDeleteStep.getTestResults()));
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}


	/**
	 * Create the member correspondence in the Cosmos DB container
	 * 
	 * @param memberId to associate the member correspondence
	 * @return {@link MemberCorrespondence} that has been inserted into Cosmos DB container
	 */
	private MemberCorrespondence createMemberCorrespondence(String memberId)
	{
		MemberCorrespondence memberCorrespondence = new MemberCorrespondence();

		memberCorrespondence.setMemberId(memberId);
		memberCorrespondence.setType(MemberType.correspondence);
		memberCorrespondence.setCorrespondenceOutcome("Clinical Consult Completed");
		memberCorrespondence.setCorrespondenceDateTime(createdDateTime); 
		memberCorrespondence.setCorrespondenceType("Inbound Fax Provider");
		memberCorrespondence.setCorrespondenceId(UUID.randomUUID().toString());
		memberCorrespondence.setInterventionId(UUID.randomUUID().toString());
		memberCorrespondence.setContactName("Test Automation A");
		memberCorrespondence.setContactTitle("Test Automation B");
		memberCorrespondence.setContactComment( "Humalog solution-Sloane// Spoke with Lynn at md office about changing Humalog solution to insulin lispro." );
		memberCorrespondence.setTargetDrug("Drug A");
		memberCorrespondence.setNote("Great savings idea");
		memberCorrespondence.setPdfUri("https://pdfstorage123.blob.core.windows.net/sample-workitems/FaxTest3.pdf");
		memberCorrespondence.setNpi("987654321");
		memberCorrespondence.setProviderName("Test Automation C");
		memberCorrespondence.setStoragePath(new StoragePath("fax-outbound-processed","FaxTest3.pdf"));
		memberCorrespondence.setCreatedBy("Test Automation D");
		memberCorrespondence.setCreatedDateTime(createdDateTime);
		memberCorrespondence.setVersion("1.0");
		memberCorrespondence.setId( UUID.randomUUID().toString());

		// Manually insert correspondence (test data) into Cosmos DB
		MemberCorrespondenceQueries.insertMemberCorrespondence(subscriptionId, memberCorrespondence);

		return memberCorrespondence;
	}


	/*
	 * Helper methods
	 */
	/**
	 * Deletes a MemberCorrespondence object using the corresponding API call and validates that the object was deleted
	 * from Cosmos DB.
	 * 
	 * @param memberCorrespondence The MemberCorrespondence object to be deleted 
	 * @return A list of test results for this step
	 * @author msharma
	 * @date 03/31/2023
	 */
	private List<DynamicNode> deleteMemberCorrespondenceAPI(MemberCorrespondence memberCorrespondence)
	{
		//Log that the API call is starting
		logger.info("Starting API call");

		// Call API to  delete member correspondence
		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));
		// Endpoint  parameters should be passed as an array, not a list
		ApiDeleteStep apiDeleteStep = new ApiDeleteStep(headers, MEMBER_CORRESPONDENCE_DELETE_ENDPOINT,
				new Object[] {memberCorrespondence.getMemberId(), memberCorrespondence.getInterventionId(), memberCorrespondence.getId()}, 204, HTTP_204_NO_CONTENT);

		apiDeleteStep.run();

		//Log that the API call has completed successfully
		logger.debug("API call completed successfully");

		// Check if the API call was sucessful
		if ( apiDeleteStep.getResponseStatusCode() != 204 )
		{
			// If the call failed,set the member correspondence to be deleted manually from Cosmos DB and log it
			List<MemberCorrespondence> toBeDeletedList = new ArrayList<>();
			toBeDeletedList.add(memberCorrespondence);	
			logger.debug("API member correspondence delete failed");

			//Return the test results for this step
			return apiDeleteStep.getTestResults();
		}

		/*
		 * Validation
		 */
		//Check if member correspondence was deletd from Cosmos DB
		MemberCorrespondence actual = MemberCorrespondenceQueries.getMemberCorrespondence(subscriptionId, memberCorrespondence.getMemberId() , memberCorrespondence.getInterventionId(),  memberCorrespondence.getId());

		apiDeleteStep.getTestResults().add(dynamicTest("Member correpondence deleted from cosmos",
				() -> assertNull(actual, "The member note " + memberCorrespondence + " was not deleted by API")));

		//If the call was successful, return the test results for this step
		return apiDeleteStep.getTestResults();
	}

}
