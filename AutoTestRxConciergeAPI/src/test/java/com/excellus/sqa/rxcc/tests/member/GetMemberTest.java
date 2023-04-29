/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;


import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
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

import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.MemberQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * Get member by {memberId}.
 * 
 * GET https://apim-lbs-rxc-tst-east-001.azure-api.net/api/member/{adTenantId}/members/{memberId}
 * 
 * Request parameters : adTenantId , memberId
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 03/14/2022
 */
@SuppressWarnings("unused")
@Tag("ALL")
@Tag("MEMBER")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetMember")
@UserRole(role = {"RXCC_FULL_SINGLE", "RXCC_FULL_LOA", "RXCC_FULL_MED"})
public class GetMemberTest extends RxConciergeAPITestBaseV2 
{
	private static final Logger logger = LoggerFactory.getLogger(GetMemberTest.class);
	private String subscriptionName; 

	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("3548: GetMember Happy Path (ALL member)")
	@Order(1)
	public List<DynamicNode> happyPathGetMemberAll() throws JsonProcessingException{

		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				List<DynamicNode> testSub = new ArrayList<>();
				subscriptionName = tenant.getSubscriptionName().toUpperCase();

				// Get member
				Member expected = MemberQueries.getRandomMember(Tenant.Type.valueOf(subscriptionName));

				// API call
				testSub =  getMemberAPI(Tenant.Type.valueOf(subscriptionName).getSubscriptionName(), expected);
				test.add(dynamicContainer(subscriptionName.toUpperCase(), testSub)); // add all step test result
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + Tenant.Type.valueOf(subscriptionName).getSubscriptionName().toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("3559: GetMember Invalid Header")
	@Order(2)
	public List<DynamicNode> invalidHeader()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = "1_078229";

			Headers headers = new Headers(new Header(API_HEADER_NAME + "invalid", subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(headers), MEMBER_GET_ENDPOINT, new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3560: GetMember Header Missing")
	@Order(3)
	public List<DynamicNode> missingHeader()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = "1_078229";

			ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), MEMBER_GET_ENDPOINT, new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3561: GetMember Valid Parm With Incorrect Header Value")
	@Order(4)
	public List<DynamicNode> incorrectHeaderValue() throws JsonProcessingException 
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				subscriptionName = tenant.getSubscriptionName().toUpperCase();

				// Get member
				Member expected = MemberQueries.getRandomMember(Tenant.Type.valueOf(subscriptionName));

				String memberId = "";
				memberId =  expected.getMemberId();

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, "abc"));

				ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_GET_ENDPOINT, new Object[]{memberId}, 500, HTTP_500_INTERNAL_SERVER_ERR);
				apiGetStep.run();

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			}
			catch (Exception e)
			{
				test.add(dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),() -> fail(e.getMessage(),e)));
			}
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	@TestFactory
	@DisplayName("3595: GetMember Invalid Auth")
	@Order(5)
	public List<DynamicNode> invalidAuth()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = "1_078229";

			Headers headers = new Headers(new Header(API_HEADER_NAME, subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep( getHeadersInvalidAuth(headers), MEMBER_GET_ENDPOINT, new Object[]{memberId}, 401, HTTP_401_UNAUTHORIZED);
			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3596: GetMember Invalid Method")
	@Order(6)
	public List<DynamicNode> invalidRestApiPostMethod()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String memberId = "1_078229";

			Headers headers = new Headers(new Header(API_HEADER_NAME, subscriptionName));

			ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(headers), MEMBER_GET_ENDPOINT, null, new Object[]{memberId}, 404, HTTP_404_RESOURCE_NOT_FOUND);
			apiPostStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3550: GetMember Invalid Parm")
	@Order(7)
	public List<DynamicNode> invalidPathParam()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String invalidMemberId = UUID.randomUUID().toString();

			Headers headers = new Headers(new Header(API_HEADER_NAME, subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep( getGenericHeaders(headers), MEMBER_GET_ENDPOINT, new Object[]{invalidMemberId}, 404, HTTP_404_NOT_FOUND);
			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}


	@TestFactory
	@DisplayName("3554: GetMember Partial Parm Combo Exist")
	@Order(8)
	public List<DynamicNode> partialParamComboExists()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			// Member doesn't exists
			String invalidMemberId = "9_123456";;

			Headers headers = new Headers(new Header(API_HEADER_NAME, subscriptionName));

			ApiGetStep apiGetStep = new ApiGetStep( getGenericHeaders(headers), MEMBER_GET_ENDPOINT, new Object[]{invalidMemberId}, 404, HTTP_404_NOT_FOUND);
			apiGetStep.run();

			test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result
			resetApiInfo();  // Reset the API information and test validation
		}
		return test;
	}

	/**
	 * Call the API to get member 
	 * 
	 * @param subscriptionName either exe or ehp
	 * @param expected member to test with
	 * @return list of {@link DynamicNode}
	 */
	private List<DynamicNode> getMemberAPI(String subscriptionName, Member expected)
	{
		assumeTrue(expected != null, "There are no members to test with");

		ApiGetStep apiGetStep = null;

		logger.debug("Starting API call");

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

		if ( StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_multi") ||
				( StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_single") && StringUtils.equalsIgnoreCase(subscriptionName, Tenant.Type.EHP.getSubscriptionName()) ) ||
				( StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_loa") && StringUtils.equalsIgnoreCase(subscriptionName, Tenant.Type.LOA.getSubscriptionName()) ) ||
				( StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_med") && StringUtils.equalsIgnoreCase(subscriptionName, Tenant.Type.MED.getSubscriptionName()) ) )
		{
			apiGetStep = new ApiGetStep(headers, MEMBER_GET_ENDPOINT, new Object[] {expected.getMemberId()}, 200, null);
			apiGetStep.run();

			Member actual = apiGetStep.convertToJsonDTO(Member.class);

			if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED )
			{
				return apiGetStep.getTestResults();
			}

			apiGetStep.getTestResults().add(dynamicContainer("Member validation", expected.compare(actual)));
		}
		else
		{
			apiGetStep = new ApiGetStep(headers, MEMBER_GET_ENDPOINT, new Object[] {expected.getMemberId()}, 401, HTTP_401_UNAUTHORIZED);
			apiGetStep.run();
		}

		return apiGetStep.getTestResults();
	}
}
