/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.excellus.sqa.restapi.RequestLoggingFilter;
import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.MemberQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.MemberSearch;
import com.excellus.sqa.rxcc.dto.MemberSearchResponse;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;

import io.restassured.http.Headers;

/**
 * <a href="https://apim-lbs-rxc-tst-east-001.developer.azure-api.net/api-details#api=search&operation=post-membersearch">Search - MemberSearch</a>
 *
 * <p><b>MemberSearch</b><br/>
 * Search tenant by {adTenantId}
 *
 * <pre>
 *   {
 *     "query":"string",
 *     "size": "25" (default/optional)
 *   }
 * </pre>
 *
 * <p><b>Request</b><br/>
 * POST    https://apim-lbs-rxc-tst-east-001.azure-api.net/api/search/member-search/{adTenantId}
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/25/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBERSEARCH")
@Tag("SEARCH")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostMemberSearches")
public class MemberSearchesTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(MemberSearchesTest.class);
	private String subscriptionName; 

	private final String GENERIC_BODY;

	public MemberSearchesTest()
	{
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("query", "Blue");

		GENERIC_BODY = requestBody.toString();
	}

	@TestFactory
	@DisplayName("3375: MemberSearches Happy Path (Query: First and Last Name)")
	@Order(1)
	public List<DynamicNode> happyPathName() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			List<DynamicNode> testSubResults = new ArrayList<>();
			try
			{
				subscriptionName = tenant.getSubscriptionName().toUpperCase();

				// Get member
				Member expected = MemberQueries.getRandomMember(Tenant.Type.valueOf(subscriptionName));

				String searchTerm = expected.getFirstName() + " " + expected.getLastName();

				//run the test
				testSubResults = happyPath( searchTerm, 0);

			}
			catch (Exception e)
			{
				String apiInfo = RequestLoggingFilter.getApiInfo();  testSubResults.add( dynamicTest("Unexpected exception",() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			test.add(dynamicContainer(subscriptionName.toUpperCase(), testSubResults));
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("3379: MemberSearches With Body (Query: Name and DOB; Size:100)")
	@Order(2)
	public List<DynamicNode> withBodyNameDOB() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			List<DynamicNode> testSubResults = new ArrayList<>();
			try
			{
				subscriptionName = tenant.getSubscriptionName().toUpperCase();

				// Get member
				Member expected = MemberQueries.getRandomMember(Tenant.Type.valueOf(subscriptionName));

				String searchTerm = expected.getFirstName() + " " + expected.getDateBirth();

				//run the test
				testSubResults = happyPath(searchTerm, 100);

			}
			catch (Exception e)
			{
				String apiInfo = RequestLoggingFilter.getApiInfo();  testSubResults.add( dynamicTest("Unexpected exception",() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			test.add(dynamicContainer(subscriptionName.toUpperCase(), testSubResults));
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("3379: MemberSearches With Body (Query:Group Name; Size:50)")
	@Order(3)
	public List<DynamicNode> withBodyGroupName() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			List<DynamicNode> testSubResults = new ArrayList<>();
			try
			{
				subscriptionName = tenant.getSubscriptionName().toUpperCase();

				// Get member
				Member expected = MemberQueries.getRandomMember(Tenant.Type.valueOf(subscriptionName));

				String searchTerm = expected.getGroupName();

				//run the test
				testSubResults = happyPath( searchTerm, 50);
			}
			catch (Exception e)
			{
				String apiInfo = RequestLoggingFilter.getApiInfo();  testSubResults.add( dynamicTest("Unexpected exception",() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			test.add(dynamicContainer(subscriptionName.toUpperCase(), testSubResults));
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("3379: MemberSearches With Body (Subscriber Id)")
	@Order(4)
	public List<DynamicNode> happyPathNameSubscriberId() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			List<DynamicNode> testSubResults = new ArrayList<>();
			try
			{
				//List<DynamicNode> testSub = new ArrayList<>();
				subscriptionName = tenant.getSubscriptionName().toUpperCase();

				// Get member
				Member expected = MemberQueries.getRandomMember(Tenant.Type.valueOf(subscriptionName));

				String searchTerm = expected.getSubscriberId();

				//run the test
				testSubResults = happyPath(searchTerm, 0);
			}
			catch (Exception e)
			{
				String apiInfo = RequestLoggingFilter.getApiInfo();  testSubResults.add( dynamicTest("Unexpected exception",() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			test.add(dynamicContainer(subscriptionName.toUpperCase(), testSubResults));
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("3379: MemberSearches With Body (Query: Smith)")
	@Order(4)
	public List<DynamicNode> happyPathTenants() throws JsonProcessingException
	{
		String searchTerm = "Smith";

		List<DynamicNode> test = new ArrayList<DynamicNode>();

		// Call API
		ApiPostStep apiPostStep = memberSearch(searchTerm, 50);
		MemberSearchResponse searchResult = apiPostStep.convertToJsonDTO(MemberSearchResponse.class);
		if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
		{
			return apiPostStep.getTestResults();
		}
		else {
			test.addAll(apiPostStep.getTestResults());
		}
		// API
		test.add(dynamicContainer("API response", validateSearchResult(searchResult, searchTerm)));

		// Validate the API is pulling members from multiple tenant
		Map<String, Integer> tenantCounts =  new HashMap<String, Integer>();
		for (MemberSearch memberSearch : searchResult.getMembers() )
		{
			String adTenantId = memberSearch.getAdTenantId();
			if ( tenantCounts.containsKey(adTenantId) ) {
				tenantCounts.put(adTenantId, tenantCounts.get(adTenantId)+1);   // increment the count of member pulled from the tenant
			}
			else {
				tenantCounts.put(adTenantId, 1);    // new member found on new tenant
			}
		}

		test.add(dynamicTest("Member retrieved from multiple tenants",
				() -> assertTrue(tenantCounts.size() > 1, "Expecting members being retrieve from multiple tenants")));

		logger.info("Members retrieve from each tenant: " + StringUtils.join(tenantCounts));

		return test;
	}


	/**
	 * Generic test helper method for happy path
	 * @param tenantSubscriberName {@link MemberSearchResponse}
	 * @param searchTerm {@link JsonObject}
	 * @param size {@link JsonObject}
	 * @return test validation result
	 */
	private List<DynamicNode> happyPath( String searchTerm, int size)
	{
		List<DynamicNode> test = new ArrayList<DynamicNode>();

		// Call API
		ApiPostStep apiPostStep = memberSearch(searchTerm, size);
		MemberSearchResponse searchResult = apiPostStep.convertToJsonDTO(MemberSearchResponse.class);
		if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
		{
			return apiPostStep.getTestResults();
		}
		else {
			test.addAll(apiPostStep.getTestResults());
		}

		/*
		 * Validations
		 */

		// API
		test.add(dynamicContainer("API response", validateSearchResult(searchResult, searchTerm)));

		return test;
	}

	private ApiPostStep memberSearch(String searchTerm, int size)
	{
		logger.debug("Starting API call");

		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("query", searchTerm);

		if ( size > 0 )
			requestBody.addProperty("size", String.valueOf(size));

		Headers headers = getGenericHeaders();

		ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_SEARCHES_POST_ENDPOINT, requestBody.toString(), null,200, null);
		apiPostStep.run();
		return apiPostStep;
	}

	/**
	 * Validate the search result
	 *
	 * @param searchResult the API response that contains list of members
	 * @param searchTerm search terms
	 * @return
	 */
	private List<DynamicNode> validateSearchResult(MemberSearchResponse searchResult, String searchTerm)
	{
		List<DynamicNode> test = new ArrayList<DynamicNode>();

		test.add(dynamicTest("Total count [" + searchResult.getTotalCount() + "]", () -> assertTrue(searchResult.getTotalCount() > 0, "Expecting API return at least 1 or more member")));
		test.add(dynamicTest("Count [" + searchResult.getCount() + "]", () -> assertTrue(searchResult.getCount() > 0, "Expecting API return at least 1 or more member")));

		test.add(dynamicTest("Validate search term [" + searchTerm + "] in member information",
				() -> {
					assertAll("Member name",
							() -> {
								for (MemberSearch memberSearch : searchResult.getMembers() )
								{
									assertTrue(memberSearch.memberContains(searchTerm),
											memberSearch.getSearchFieldValues() + " did not contain the search term [" + searchTerm + "]");
								}
							});
				}));

		return test;
	}

	@TestFactory
	@DisplayName("3382: MemberSearches Invalid Auth")
	@Order(5)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException 
	{
		List<DynamicNode> testAllResults = new ArrayList<DynamicNode>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			ApiPostStep apiPostStep = new ApiPostStep(getHeadersInvalidAuth(), MEMBER_SEARCHES_POST_ENDPOINT, GENERIC_BODY,
					null, 401, HTTP_401_UNAUTHORIZED);
			apiPostStep.run();

			testAllResults.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result

			resetApiInfo(); // reset API info and test validation results
		}
		return testAllResults;
	}


	@TestFactory
	@DisplayName("3379: MemberSearches With Body (invalid JSON format)")
	@Order(6)
	public  List<DynamicNode>  invalidBody()
	{
		List<DynamicNode> testAllResults = new ArrayList<DynamicNode>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String jsonBody = "{\n\"query\":\"Garrett Cosmiano\"\n";

			Headers headers = getGenericHeaders();

			ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_SEARCHES_POST_ENDPOINT, jsonBody,
					null, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiPostStep.run();

			testAllResults.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result

			resetApiInfo(); // reset API info and test validation results
		}
		return testAllResults;
	}


	@TestFactory
	@DisplayName("3379: MemberSearches With Body (invalid json property name)")
	@Order(7)
	public List<DynamicNode> invalidParamBody()
	{
		List<DynamicNode> testAllResults = new ArrayList<DynamicNode>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			String jsonBody = "{\n\"queries\":\"Garrett Cosmiano\"\n}";

			Headers headers = getGenericHeaders();

			ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_SEARCHES_POST_ENDPOINT, jsonBody,
					null, 500, HTTP_500_INTERNAL_SERVER_ERR);
			apiPostStep.run();

			testAllResults.add(dynamicContainer(subscriptionName.toUpperCase(), apiPostStep.getTestResults())); // add all step test result

			resetApiInfo(); // reset API info and test validation results
		}
		return testAllResults;
	}

	@TestFactory
	@DisplayName("3383: MemberSearches Invalid Method")
	@Order(8)
	public  List<DynamicNode> invalidMethod()
	{
		List<DynamicNode> testAllResults = new ArrayList<DynamicNode>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionName = tenant.getSubscriptionName();

			Headers headers = getGenericHeaders();

			ApiGetStep apiGetStep = new ApiGetStep(headers, MEMBER_SEARCHES_POST_ENDPOINT, 
					null, 404, HTTP_404_RESOURCE_NOT_FOUND);
			apiGetStep.run();

			testAllResults.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults())); // add all step test result

			resetApiInfo(); // reset API info and test validation results
		}
		return testAllResults;
	}
}
