/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.pharmacy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
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
import com.excellus.sqa.rxcc.cosmos.PharmacyQueries;
import com.excellus.sqa.rxcc.dto.FieldSearchPharmacyResponse;
import com.excellus.sqa.rxcc.tests.provider.FieldSearchTest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * POST https://apim-lbs-rxc-dev-east-001.azure-api.net/api/pharmacy/field-search
 * 
 * @author Manish Sharma (msharma)
 * @since 04/18/2022
 */
@Tag("ALL")
@Tag("PHARMACY")
@Tag("SEARCH")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostPharmacyFieldSearch")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class PharmacyFieldSearchTest extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(FieldSearchTest.class);

	private final String GENERIC_BODY;

	public PharmacyFieldSearchTest()
	{
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("query", "Blue");

		GENERIC_BODY = requestBody.toString();
	}

	private final int API_MAX_ITEMS = 173;

	@TestFactory
	@DisplayName("743: Pharmacy FieldSearch  Happy Path without Filter (Query:Store NAME without filter Size:200)") 
	@Order(1)
	public List<DynamicNode> withBodyStoreNameWithoutFilter()
	{
		String fieldSearchName = "storeName";
		String searchTerm = "OS";

		List<String> expected = PharmacyQueries.fieldSearch(fieldSearchName, searchTerm, null);

		//  Use Pharmacy.storename as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);

		//Use filter fields if needed, but not for this test case we need empty filter
		JsonObject filter = new JsonObject();
		body.add("filter", filter);

		// Call API
		return performSearchAndValidation(expected, body, expected.size() < API_MAX_ITEMS);
	}

	@TestFactory
	@DisplayName("743: Pharmacy FieldSearch  Happy Path without Filter (Query:Phone Number without filter Size:200)")
	@Order(2)
	public List<DynamicNode> withBodyPhoneNumberWithoutFilter()
	{
		String fieldSearchName = "phoneNumber";
		String searchTerm = "718";

		List<String> expected = PharmacyQueries.fieldSearch(fieldSearchName, searchTerm, null);

		//  Use Pharmacy.phoneNumber as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);

		//Use filter fields if needed, but not for this test case we need empty filter
		JsonObject filter = new JsonObject();
		body.add("filter", filter);

		// Call API
		return performSearchAndValidation(expected, body, expected.size() < API_MAX_ITEMS);
	}

	@TestFactory
	@DisplayName("741: Pharmacy FieldSearch  Body/Happy Path with Filter (Query:Store Name with filter: State Size:200)") //failed
	@Order(3)
	public List<DynamicNode> withBodyStoreNameWithFilterOnState()
	{
		String fieldSearchName = "storeName";
		String searchTerm = "OSCO";

		Map<String, List<String>> filters = new HashMap<>();
		filters.put("state", Arrays.asList("WY"));

		List<String> expected = PharmacyQueries.fieldSearch(fieldSearchName, searchTerm, filters);

		//  Use Provider.storeName as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);

		//Use filter fields if needed, but not for this test case we need empty filter
		Gson gson = new Gson ();
		body.add("filter", gson.toJsonTree(filters));

		// Call API
		return performSearchAndValidation(expected, body, expected.size() > API_MAX_ITEMS);
	}
	
	@TestFactory
	@DisplayName("741: Pharmacy FieldSearch  Body/Happy  Path with Filter (Query:City with filter: StatusInd Size:200)") //{fieldQuery: "city", query: "a", filter: {statusInd: ["Active"]}}
	@Order(4)
	public List<DynamicNode> withBodyCityWithFilterOnStatusInd()
	{
		String fieldSearchName = "city";
		String searchTerm = "abb";

		Map<String, List<String>> filters = new HashMap<>();
		filters.put("statusInd", Arrays.asList("Active"));

		List<String> expected = PharmacyQueries.fieldSearch(fieldSearchName, searchTerm, filters);

		//  Use Provider.city as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);

		//Use filter fields if needed, but not for this test case we need empty filter
		Gson gson = new Gson ();
		body.add("filter", gson.toJsonTree(filters));

		// Call API
		return performSearchAndValidation(expected, body, expected.size() > API_MAX_ITEMS);
	}	

	@TestFactory
	@DisplayName("741: Pharmacy FieldSearch  Body/Happy Path with Filter (Query:Phone Number with filter: statusInd, taxonomyDescr Size:200)")
	@Order(5)
	public List<DynamicNode> withBodyPhoneNumberWithFilterAndTaxonomyDescrOnStatusInd()
	{
		String fieldSearchName = "phoneNumber";
		String searchTerm = "410";

		Map<String, List<String>> filters = new HashMap<>();
		filters.put("statusInd", Arrays.asList("Active"));
		filters.put("taxonomyDescr", Arrays.asList("Department of Veterans Affairs (VA) Pharmacy"));

		List<String> expected = PharmacyQueries.fieldSearch(fieldSearchName, searchTerm, filters);

		//  Use Provider.phoneNumber as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);

		//Use filter fields if needed, but not for this test case we need empty filter
		Gson gson = new Gson ();
		body.add("filter", gson.toJsonTree(filters));

		// Call API
		return performSearchAndValidation(expected, body, expected.size() > API_MAX_ITEMS);
	}	

	@TestFactory
	@DisplayName("741: Pharmacy FieldSearch  Body/Happy Path with Filter (Query:NPI with filter: city Size:200)")
	@Order(6)
	public List<DynamicNode> BodywithNpiWithFilterOnCity()
	{
		String fieldSearchName = "npi";
		String searchTerm = "1";

		Map<String, List<String>> filters = new HashMap<>();
		filters.put("city", Arrays.asList("ALEXANDER CITY"));
	

		List<String> expected = PharmacyQueries.fieldSearch(fieldSearchName, searchTerm, filters);

		//  Use Provider.phoneNumber as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);

		//Use filter fields if needed, but not for this test case we need empty filter
		Gson gson = new Gson ();
		body.add("filter", gson.toJsonTree(filters));

		// Call API
		return performSearchAndValidation(expected, body, expected.size() > API_MAX_ITEMS);
	}	

	@TestFactory
	@DisplayName("738: Pharmacy FieldSearch Invalid Auth")
	@Order(7)
	public List<DynamicNode> invalidAuthentication()
	{
		ApiPostStep apiPostStep = new ApiPostStep(getHeadersInvalidAuth(), PHARMACY_FIELD_SEARCH_POST_ENDPOINT, GENERIC_BODY,null,401, HTTP_401_UNAUTHORIZED);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("737: Pharmacy FieldSearch Invalid Method")
	@Order(8)
	public List<DynamicNode> invalidMethod()
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), PHARMACY_FIELD_SEARCH_POST_ENDPOINT, null, 404, HTTP_404_RESOURCE_NOT_FOUND);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("4594: Pharmacy FieldSearch With Body Spl Char")
	@Order(9)
	public List<DynamicNode> SpecialChars()
	{
		//Special Chars on searchTerm
		String fieldSearchName = "storeName";
		String searchTerm = "%%%";

		//Use Provider.storeName as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);

		//Use filter fields if needed, but not for this test case we need empty filter
		JsonObject filter = new JsonObject();
		body.add("filter", filter);

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PHARMACY_FIELD_SEARCH_POST_ENDPOINT, body.toString(),null,null, null);
		apiPostStep.run();

		List<DynamicNode> tests = new ArrayList<>();
		tests.add(dynamicTest("Response status code [" + 400 + "]",
				() -> assertThat("Defect #35063\n" + apiPostStep.getApiInfo(), apiPostStep.getResponseStatusCode(), is(400) )));

		tests.add(dynamicTest("Response status line [" + HTTP_400_BAD_REQUEST + "]",
				() -> assertThat("Defect #35063\n" + apiPostStep.getApiInfo(), apiPostStep.getResponseStatusLine(), is(HTTP_400_BAD_REQUEST)) ));

		return tests;
	}

	/**
	 * Validate the search result
	 *
	 * @param expected list of expected response
	 * @param requestBody the API request body
	 * @param actualMoreResults determines if there are more results in the API response than the expected
	 * @return list of {@link DynamicNode} of test
	 */
	private List<DynamicNode> performSearchAndValidation(List<String> expected, JsonObject requestBody, Boolean actualMoreResults)
	{
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(),
											PHARMACY_FIELD_SEARCH_POST_ENDPOINT,
											requestBody.toString(),
											null,
											200,
											HTTP_200_OK);
		apiPostStep.run();

		FieldSearchPharmacyResponse searchResult = apiPostStep.convertToJsonDTO(FieldSearchPharmacyResponse.class);

		List<DynamicNode> test = new ArrayList<>();

		if ( expected.size() < 50) {

			test.add(dynamicTest("More Results [" + searchResult.isMoreResults() + "]",
					() -> assertEquals(searchResult.isMoreResults(), actualMoreResults, apiPostStep.getApiInfo())));

			test.add(dynamicTest("Validate results with API Body: " + requestBody,
					() -> assertThat(apiPostStep.getApiInfo(), searchResult.getSearchResults(), containsInAnyOrder(expected.toArray(new String[expected.size()]))) ));
		}

		else {

			//convert apibody to json, grab the property request from the json and then make that assearch value/term, then do a for loop and verify the search term that strats with that value

			JSONObject obj = new JSONObject(requestBody.toString());
			String searchTerm = obj.query("/query").toString();

			String failureMessage = "";
			for (String fieldSearch : searchResult.getSearchResults() )
			{
				if(!StringUtils.containsAny(fieldSearch.toUpperCase(), searchTerm.toUpperCase()))
				{
					failureMessage += fieldSearch + " did not contain the search term [" + searchTerm + "]\n";
				}
			}

			if ( StringUtils.isEmpty(failureMessage))
			{
				test.add(dynamicTest("Validate search term " + searchTerm + " in field query information", () -> assertTrue(true)));
			}
			else
			{
				final String msg = failureMessage + apiPostStep.getApiInfo();
				test.add(dynamicTest("Validate search term " + searchTerm + " in field query information", () -> fail(msg)));
			}
		}

		return test;
	}
}
