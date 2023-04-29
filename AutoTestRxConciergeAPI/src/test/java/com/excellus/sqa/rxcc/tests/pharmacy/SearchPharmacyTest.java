/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.pharmacy;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;

import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.restapi.steps.IApiStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.PharmacyQueries;
import com.excellus.sqa.rxcc.dto.PharmaciesSearch;
import com.excellus.sqa.rxcc.dto.Pharmacy;
import com.excellus.sqa.rxcc.dto.SearchPharmacyResponse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 04/03/2022
 */
@Tag("ALL")
@Tag("PHARMACY")
@Tag("SEARCH")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostPharmacySearch")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class SearchPharmacyTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(SearchPharmacyTest.class);

	private final String GENERIC_BODY;

	public SearchPharmacyTest()
	{
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("query", "1003003856");

		GENERIC_BODY = requestBody.toString();
	}	


	@TestFactory
	@DisplayName("744: SearchPharmacy With Happy Path with Multiple Filter (Query:PHARMACY NAME adding state and city filter Size:200)")
	@Order(1)
	public List<DynamicNode> withBodyFirstNameWithFilter()
	{
		// Query the Cosmos db for a random pharmacy
		Pharmacy expected = PharmacyQueries.getRandomPharmacy(); 

		// Use Pharmacy.storeName as query
		JsonObject body = new JsonObject();
		body.addProperty("query", expected.getStoreName().toLowerCase());

		//Use filter fields (upper case) in Pharmacy.state 
		JsonArray state = new JsonArray();
		state.add(expected.getState().toUpperCase());

		//Use filter fields (upper case) in Pharmacy.city
		JsonArray cities = new JsonArray();
		cities.add(expected.getCity().toUpperCase());

		JsonObject filter = new JsonObject();
		filter.add("state", state);
		filter.add("city", cities);

		body.add("filter", filter);

		// Call API
		IApiStep apiStep = pharmacySearch(body);

		// Validate
		return validateSearchResult(apiStep, expected.getStoreName());
	}	

	@TestFactory
	@DisplayName("744: SearchPharmacy With Happy Path with One Filter (Query:PHARMACY NAME adding taxonomyDescr filter Size:200)")
	@Order(2)
	public List<DynamicNode> withBodyFirstNameWithOneFilter()
	{

		// Query the Cosmos db for a random pharmacy
		Pharmacy expected = PharmacyQueries.getRandomPharmacy(); 

		//  Use Pharmacy.storeName as query
		JsonObject body = new JsonObject();
		body.addProperty("query", expected.getStoreName().toLowerCase());

		//Use filter fields (upper case) in Pharmacy.taxonomyDescr 
		JsonArray taxonomyDescr = new JsonArray();
		taxonomyDescr.add(expected.getTaxonomyDescr());

		JsonObject filter = new JsonObject();
		filter.add("taxonomyDescr", taxonomyDescr);

		body.add("filter", filter);

		// Call API
		IApiStep apiStep = pharmacySearch(body);

		// Validate
		return validateSearchResult(apiStep, expected.getStoreName());
	}	

	@TestFactory
	@DisplayName("744: SearchPharmacy With Happy Path with ASC Sort (Query:STATE adding city as sort Size:200)")
	@Order(3)
	public List<DynamicNode> withBodyStoreNameWithAscSort()
	{

		// Query the Cosmos db for a random pharmacy
		Pharmacy expected = PharmacyQueries.getRandomPharmacy(); 

		//Sort with descending order in last field after querying state from random pharmacy 		
		JsonObject body = new JsonObject();
		body.addProperty("query", expected.getState().toLowerCase());
		body.addProperty("sortField", "city");
		body.addProperty("sortDirection", "asc");

		// Call API
		IApiStep apiStep = pharmacySearch(body);

		SearchPharmacyResponse searchResult = apiStep.convertToJsonDTO(SearchPharmacyResponse.class);

		List<PharmaciesSearch> actual = searchResult.getPharmacies(); 

		List<String> actualOrder = new LinkedList<>();
		for ( PharmaciesSearch pharmacy : actual )
			actualOrder.add(pharmacy.getCity());

		//Query Result from API and make sure the sort is working, FYI : not validating the data from API results
		List<String> expectedOrder = new LinkedList<>(actualOrder);

		//Checking api result sorting order to external sorting
		expectedOrder.sort(String.CASE_INSENSITIVE_ORDER);

		apiStep.getTestResults().add(dynamicTest("Ascending order validation",
				() -> assertThat(actualOrder, contains(expectedOrder.toArray(new String[expectedOrder.size()])))) );

		// Validate
		return validateSearchResult(apiStep, expected.getState());

	}

	@TestFactory
	@DisplayName("744: SearchPharmacy With Happy Path with DESC Sort (Query:Taxonomy Descr adding Store Name as sort Size:200)")
	@Order(4)
	public List<DynamicNode> withBodyTaxonomyDescrWithDescSort()
	{

		// Query the Cosmos db for a random pharmacy
		Pharmacy expected = PharmacyQueries.getRandomPharmacy(); 

		//Sort with descending order in last field after querying storeName from random pharmacy 		
		JsonObject body = new JsonObject();
		body.addProperty("query", expected.getTaxonomyDescr());
		body.addProperty("sortField", "storeName");
		body.addProperty("sortDirection", "desc");

		// Call API
		IApiStep apiStep = pharmacySearch(body);

		SearchPharmacyResponse searchResult = apiStep.convertToJsonDTO(SearchPharmacyResponse.class);

		List<PharmaciesSearch> actual = searchResult.getPharmacies(); 

		List<String> actualOrder = new LinkedList<>();
		for ( PharmaciesSearch pharmacy : actual )
			actualOrder.add(pharmacy.getStoreName());

		//Query Result from API and make sure the sort is working, FYI : not validating the data from API results
		List<String> expectedOrder = new LinkedList<>(actualOrder);

		//Checking api result sorting order to external sorting
		expectedOrder.sort(String.CASE_INSENSITIVE_ORDER.reversed());

		apiStep.getTestResults().add(dynamicTest("Ascending order validation",
				() -> assertThat(actualOrder, contains(expectedOrder.toArray(new String[expectedOrder.size()])))) );

		// Validate
		return validateSearchResult(apiStep, expected.getTaxonomyDescr());

	}


	@TestFactory
	@DisplayName("744: SearchPharmacy With Happy Path  (Query:NPI; Size:200)")
	@Order(5)
	public List<DynamicNode> withBodyNpi()
	{		
		// Cosmos db
		Pharmacy pharmacy = PharmacyQueries.getRandomPharmacy();// retrieve random provider
		String searchTerm = pharmacy.getNpi();

		// Call API
		IApiStep apiStep = pharmacySearch(searchTerm, 200);

		// Validate
		return validateSearchResult(apiStep, searchTerm);
	}

	@TestFactory
	@DisplayName("744: SearchPharmacy With Happy Path  (Query:STATE; Size:200)")
	@Order(6)
	public List<DynamicNode> withBodyState()
	{		
		// Cosmos db
		Pharmacy pharmacy = PharmacyQueries.getRandomPharmacy();// retrieve random provider
		String searchTerm = pharmacy.getState();

		// Call API
		IApiStep apiStep = pharmacySearch(searchTerm, 200);

		// Validate
		return validateSearchResult(apiStep, searchTerm);
	}


	@TestFactory
	@DisplayName("744: SearchPharmacy With Happy Path  (Query:PARTIAL STORENAME; Size:200)")
	@Order(7)
	public List<DynamicNode> withBodyStoreName()
	{
		String searchTerm = "WALGREENS";	

		// Call API
		IApiStep apiStep = pharmacySearch(searchTerm, 200);

		// Validate
		return validateSearchResult(apiStep, searchTerm);
	}

	@TestFactory
	@DisplayName("744: SearchPharmacy With Happy Path  (Query:FULL STORENAME with space; Size:200)")
	@Order(8)
	public List<DynamicNode> withBodyFullStoreName()
	{
		String searchTerm = "WALGREENS #17442";	

		// Call API
		IApiStep apiStep = pharmacySearch(searchTerm, 200);

		// Validate
		return validateSearchResult(apiStep, searchTerm);
	}	

	@TestFactory
	@DisplayName("749: SearchPharmacy Invalid Auth")
	@Order(9)
	public List<DynamicNode> invalidAuthentication()
	{
		ApiPostStep apiPostStep = new ApiPostStep(getHeadersInvalidAuth(), PHARMACY_SEARCH_POST_ENDPOINT, GENERIC_BODY, null, 401, HTTP_401_UNAUTHORIZED);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("746: SearchPharmacy With Body (invalid JSON format)")
	@Order(10)
	public List<DynamicNode> invalidBody()
	{
		String jsonBody = "{\n\"query\":\"WALGREENS #17442\"\n";

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PHARMACY_SEARCH_POST_ENDPOINT, jsonBody, null, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("750: SearchPharmacy Invalid Method")
	@Order(11)
	public List<DynamicNode> invalidMethod()
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), PHARMACY_SEARCH_POST_ENDPOINT, null, 404, HTTP_404_RESOURCE_NOT_FOUND);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("4592: SearchPharmacy With Special Character Body (special character only)")
	@Order(12)
	public List<DynamicNode> invalidSpecialBody()
	{
		String jsonBody = "{\n\"query\":\"##\"\n}";

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PHARMACY_SEARCH_POST_ENDPOINT, jsonBody, null, null, null);
		apiPostStep.run();

		List<DynamicNode> tests = new ArrayList<>();
		tests.add(dynamicTest("Response status code [" + 400 + "]",
				() -> assertThat("Defect #35063\n" + apiPostStep.getApiInfo(), apiPostStep.getResponseStatusCode(), is(400) )));

		tests.add(dynamicTest("Response status line [" + HTTP_400_BAD_REQUEST + "]",
				() -> assertThat("Defect #35063\n" + apiPostStep.getApiInfo(), apiPostStep.getResponseStatusLine(), is(HTTP_400_BAD_REQUEST)) ));

		return tests;
	}

	/**
	 * Perform the API call
	 *
	 * @param body to be search with
	 * @return {@link IApiStep} that performed the API call
	 */
	private IApiStep pharmacySearch(JsonObject body)
	{
		logger.debug("Starting API call");

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PHARMACY_SEARCH_POST_ENDPOINT, body.toString(), null, 200, HTTP_200_OK);
		apiPostStep.run();

		return apiPostStep;
	}

	/**
	 * Perform the API call
	 *
	 * 
	 * @param searchTerm to be search with
	 * @param size of the response
	 * @return {@link IApiStep} that performed the API call
	 */
	private IApiStep pharmacySearch(String searchTerm, int size)
	{
		logger.debug("Starting API call");

		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("query", searchTerm);

		if ( size > 0 )
			requestBody.addProperty("size", String.valueOf(size));

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PHARMACY_SEARCH_POST_ENDPOINT, requestBody.toString(), null, 200, HTTP_200_OK);
		apiPostStep.run();

		return apiPostStep;
	}

	/**
	 * Validate the search result
	 *
	 * @param apiStep the step that performed the API call
	 * @param searchTerm search terms
	 * @return list of {@link DynamicNode} of test
	 */
	private List<DynamicNode> validateSearchResult(IApiStep apiStep, String searchTerm)
	{
		List<DynamicNode> test = new ArrayList<>(apiStep.getTestResults());

		SearchPharmacyResponse searchResult = apiStep.convertToJsonDTO(SearchPharmacyResponse.class);

		test.add(dynamicTest("Total Results [" + searchResult.getTotalResults() + "]", () -> assertTrue(searchResult.getTotalResults() > 0, "Expecting API return at least 1 or more pharmacy")));

		test.add(dynamicTest("Validate search term [" + searchTerm + "] in pharmacy information",
				() -> {
					String failureMessage = "";
					for (PharmaciesSearch pharmacySearch : searchResult.getPharmacies() )
					{
						if ( !pharmacySearch.pharmacyContains(searchTerm) )
						{
							failureMessage += pharmacySearch.getSearchFieldValues() + " did not contain the search term [" + searchTerm + "]\n";
						}
					}

					if (StringUtils.isEmpty(failureMessage))
					{
						assertTrue(true);
					}
					else
					{
						failureMessage += apiStep.getApiInfo();
						fail(failureMessage);
					}
				}));

		return test;
	}

}
