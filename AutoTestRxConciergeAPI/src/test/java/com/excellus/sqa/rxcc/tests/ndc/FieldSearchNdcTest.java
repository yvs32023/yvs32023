/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.ndc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.NdcQueries;
import com.excellus.sqa.rxcc.dto.FieldSearchNdcResponse;
import com.excellus.sqa.rxcc.dto.Ndc;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.restassured.http.Headers;

/**
 * 
 * In the request body, "fieldQuery" specifies which field the search should execute on and can be one of the following values : {"labeler", "appNumber", "productName", "dosageForm", "gpi10GenericName"}
 * 
 * POST https://apim-lbs-rxc-dev-east-001.azure-api.net/api/ndc/field-search
 * 
 * {
 *  "query": "string",
 *  "fieldQuery": "string",
 * "filter":
 *  {
 *    "brandNameCodes": ["string"],
 *    "labeler": "string",
 *    "appNumbers": ["string"],
 *    "dosageForms": ["string"],
 *    "gpi10GenericName": "string",
 *    "productName": "string",
 *    "rxOTC": "string"		
 *  },
 *  "size": number 
 *}
 * 
 * @author Manish Sharma (msharma)
 * @since 06/05/2022
 */
@Tag("ALL")
@Tag("NDC")
@Tag("SEARCH")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostNdcFieldSearch")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class FieldSearchNdcTest extends RxConciergeAPITestBaseV2{

	private static final Logger logger = LoggerFactory.getLogger(FieldSearchNdcTest.class);

	private final String GENERIC_BODY;
	private final Integer size = 20000;

	public FieldSearchNdcTest()
	{
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("query", "Blue");

		GENERIC_BODY = requestBody.toString();
	}

	private final int API_MAX_ITEMS = 173;

	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("18542: NDC FieldSearch  Happy Path (query, fieldQuery) without Filter (Query:getProductName and FieldQuery: productName)") 
	@Order(1)
	public List<DynamicNode> withBodyStoreNameWithoutFilterTwo() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();

		//Grabbing random Ndc from Db
		Ndc randomNdc =  NdcQueries.getRandomNdc();

		String fieldQuery = "productName";
		String searchTerm = randomNdc.getProductName().toUpperCase();

		List<String> expected = NdcQueries.fieldSearch( searchTerm, fieldQuery, null, null);

		//  Use Ndc.productName as query & fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldQuery",fieldQuery);
		body.addProperty("query", searchTerm);
		body.addProperty("size", size);

		//Use filter fields if needed, but not for this test case we need empty filter
		JsonObject filter = new JsonObject();
		body.add("filter", filter);

		// Run the Test // Call API
		test=  happyPath(expected, body, size);

		return test;

	}

	@TestFactory
	@DisplayName("18540: NDC FieldSearch  Happy Path (query, fieldQuery) with all Filter (Query:get labeler and FieldQuery: labeler)") 
	@Order(2)
	public List<DynamicNode> withBodyStoreNameWithAllFilter() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();

		//Grabbing random Ndc from Db
		Ndc randomNdc =  NdcQueries.getRandomNdc();

		String fieldQuery = "labeler";
		String searchTerm = randomNdc.getLabeler().substring(0,5);

		Map<String, List<String>> filterListStrings = new HashMap<String, List<String>>();
		filterListStrings = ListString(randomNdc, "brandNameCode,appNumber,dosageForm");

		//String filter selection for api body
		Map<String, String> filterStrings = new HashMap<String, String>();
		filterStrings = MapString(randomNdc, "labeler,rxOTC");

		//call cosmos
		List<String> expected = NdcQueries.fieldSearch( searchTerm.toUpperCase(), fieldQuery, filterListStrings, filterStrings);

		//  Use Ndc.labeler as query & fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldQuery",fieldQuery);
		body.addProperty("query",searchTerm );

		Gson gson = new Gson();

		//updated to brandNameCodes as per Api body requirement
		filterListStrings.put("brandNameCodes",filterListStrings.get("brandNameCode"));
		filterListStrings.remove("brandNameCode");

		//updated to appNumbers as per Api body requirement
		filterListStrings.put("appNumbers",filterListStrings.get("appNumber"));
		filterListStrings.remove("appNumber");

		//updated to dosageForms as per Api body requirement
		filterListStrings.put("dosageForms",filterListStrings.get("dosageForm"));
		filterListStrings.remove("dosageForm");

		body.add("filter", gson.toJsonTree(filterListStrings));
		filterStrings.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );

		body.addProperty("size", size);

		// Run the Test // Call API
		test=  happyPath(expected, body, 20000);

		return test;
	}


	@TestFactory
	@DisplayName("18540: NDC FieldSearch  Happy Path (query, fieldQuery) with all Filter (Query:get getDosageForm and FieldQuery: dosageForm)") 
	@Order(3)
	public List<DynamicNode> withBodyproductNameWithAllFilter() throws JsonProcessingException
	{


		//Grabbing random Ndc from Db
		Ndc randomNdc =  NdcQueries.getRandomNdc();

		String searchTerm = randomNdc.getDosageForm().toUpperCase();
		String fieldQuery = "dosageForm";


		Map<String, List<String>> filterListStrings = new HashMap<String, List<String>>();
		filterListStrings = ListString(randomNdc, "brandNameCode,appNumber,dosageForm");

		//String filter selection for api body
		Map<String, String> filterStrings = new HashMap<String, String>();
		filterStrings = MapString(randomNdc, "labeler,rxOTC,gpi10GenericName");

		//call cosmos
		List<String> expected = NdcQueries.fieldSearch( searchTerm, fieldQuery, filterListStrings, filterStrings);

		//  Use Ndc.dosageForm as query & fieldquery
		JsonObject body = new JsonObject();

		body.addProperty("fieldQuery",fieldQuery);
		body.addProperty("query",searchTerm );

		Gson gson = new Gson();

		//updated to brandNameCodes as per Api body requirement
		filterListStrings.put("brandNameCodes",filterListStrings.get("brandNameCode"));
		filterListStrings.remove("brandNameCode");

		//updated to appNumbers as per Api body requirement
		filterListStrings.put("appNumbers",filterListStrings.get("appNumber"));
		filterListStrings.remove("appNumber");

		//updated to dosageForms as per Api body requirement
		filterListStrings.put("dosageForms",filterListStrings.get("dosageForm"));
		filterListStrings.remove("dosageForm");


		body.add("filter", gson.toJsonTree(filterListStrings));
		filterStrings.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );

		body.addProperty("size", size);

		List<DynamicNode> test = new ArrayList<>();

		// Run the Test // Call API
		test=  happyPath(expected, body, size);

		return test;

	}

	@TestFactory
	@DisplayName("18540: NDC FieldSearch  Happy Path (query, fieldQuery) with all Filter (Query:getGpi10GenericName and FieldQuery: gpi10GenericName)") 
	@Order(4)
	public List<DynamicNode> withBodyproductNameWithAllFilterOnGpi10GenericName() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();

		//Grabbing random Ndc from Db
		Ndc randomNdc =  NdcQueries.getRandomNdc();

		String fieldQuery = "gpi10GenericName";
		String searchTerm = randomNdc.getGpi10GenericName().toUpperCase();

		Map<String, List<String>> filterListStrings = new HashMap<String, List<String>>();
		filterListStrings = ListString(randomNdc, "brandNameCode,appNumber,dosageForm");

		//String filter selection for api body
		Map<String, String> filterStrings = new HashMap<String, String>();
		filterStrings = MapString(randomNdc, "labeler,rxOTC,gpi10GenericName");

		//call cosmos
		List<String> expected = NdcQueries.fieldSearch( searchTerm, fieldQuery, filterListStrings, filterStrings);

		//  Use Ndc.gpi10GenericName as query & fieldquery
		JsonObject body = new JsonObject();

		body.addProperty("fieldQuery",fieldQuery);
		body.addProperty("query",searchTerm );

		Gson gson = new Gson();

		//updated to brandNameCodes as per Api body requirement
		filterListStrings.put("brandNameCodes",filterListStrings.get("brandNameCode"));
		filterListStrings.remove("brandNameCode");

		//updated to appNumbers as per Api body requirement
		filterListStrings.put("appNumbers",filterListStrings.get("appNumber"));
		filterListStrings.remove("appNumber");

		//updated to dosageForms as per Api body requirement
		filterListStrings.put("dosageForms",filterListStrings.get("dosageForm"));
		filterListStrings.remove("dosageForm");


		body.add("filter", gson.toJsonTree(filterListStrings));
		filterStrings.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );

		body.addProperty("size", size);

		// Run the Test // Call API
		test=  happyPath(expected, body, size);

		return test;
	}

	@TestFactory
	@DisplayName("18540: NDC FieldSearch  Happy Path (query, fieldQuery) with all Filter (Query:get getMarketingCategory and FieldQuery: marketingCategory)") 
	@Order(5)
	public List<DynamicNode> withBodyproductNameWithAllFilterOnMarketingCategory() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();

		//Grabbing random Ndc from Db
		Ndc randomNdc =  NdcQueries.getRandomNdc();

		String fieldQuery = "marketingCategory";
		String searchTerm = randomNdc.getAppNumber();

		Map<String, List<String>> filterListStrings = new HashMap<String, List<String>>();
		filterListStrings = ListString(randomNdc, "brandNameCode,marketingCategory,dosageForm");

		//String filter selection for api body
		Map<String, String> filterStrings = new HashMap<String, String>();
		filterStrings = MapString(randomNdc, "labeler,rxOTC,gpi10GenericName");

		//call cosmos
		List<String> expected = NdcQueries.fieldSearch( searchTerm, fieldQuery, filterListStrings, filterStrings);


		//  Use Ndc.appNumber as query & fieldquery
		JsonObject body = new JsonObject();

		body.addProperty("fieldQuery",fieldQuery);
		body.addProperty("query",searchTerm );

		Gson gson = new Gson();
		//updated to brandNameCodes as per Api body requirement
		filterListStrings.put("brandNameCodes",filterListStrings.get("brandNameCode"));
		filterListStrings.remove("brandNameCode");

		//updated to marketingCategories as per Api body requirement
		filterListStrings.put("marketingCategories",filterListStrings.get("marketingCategory"));
		filterListStrings.remove("marketingCategory");

		//updated to dosageForms as per Api body requirement
		filterListStrings.put("dosageForms",filterListStrings.get("dosageForm"));
		filterListStrings.remove("dosageForm");


		body.add("filter", gson.toJsonTree(filterListStrings));
		filterStrings.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );

		body.addProperty("size", size);

		// Run the Test // Call API
		test=  happyPath(expected, body, size);

		return test;
	}

	/**
	 * Generic test for happy path
	 * @param expected {@link List<String>}
	 * @param searchTerm {@link JsonObject}
	 * @param size {@link JsonObject}
	 * @return test validation result
	 */
	private  List<DynamicNode> happyPath(List<String> expected,JsonObject requestBody, int size)
	{
		List<DynamicNode> test = new ArrayList<DynamicNode>();

		logger.debug("Starting API call");
		// Call API
		ApiPostStep apiPostStep = fieldSearch( requestBody,  size);
		FieldSearchNdcResponse searchResult = apiPostStep.convertToJsonDTO(FieldSearchNdcResponse.class);
		if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
		{
			return apiPostStep.getTestResults();
		}
		else {
			test.addAll(apiPostStep.getTestResults());
		}

		// Validate
		test.add(dynamicContainer("API response", validateSearchResult(expected, searchResult, requestBody, expected.size() < API_MAX_ITEMS)));

		return test;
	}

	private ApiPostStep fieldSearch(JsonObject requestBody, int size)
	{
		logger.debug("Starting API call");

		Headers headers = getGenericHeaders();

		ApiPostStep apiPostStep = new ApiPostStep(headers, NDC_FIELD_SEARCH_POST_ENDPOINT, requestBody.toString(), new Object[] {},200, null);
		apiPostStep.run();

		return apiPostStep;
	}

	@TestFactory
	@DisplayName("18537: NDC FieldSearch Invalid Auth")
	@Order(6)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException 
	{
		//invalidHeader
		Headers headers = getHeadersInvalidAuth();
		ApiPostStep apiPostStep = new ApiPostStep(headers, NDC_FIELD_SEARCH_POST_ENDPOINT, GENERIC_BODY,
				new Object[]{}, 401, HTTP_401_UNAUTHORIZED);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("18543: NDC FieldSearch With Body Spl Char")
	@Order(7)
	public List<DynamicNode> SpecialChars()
	{
		//Special Chars on searchTerm
		String fieldQuery = "gpi10GenericName";
		String searchTerm = "%%%";

		//Use Provider.storeName as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldQuery);
		body.addProperty("query", searchTerm);

		//Use filter fields if needed, but not for this test case we need empty filter
		JsonObject filter = new JsonObject();
		body.add("filter", filter);

		Headers headers = getGenericHeaders();
		ApiPostStep apiPostStep = new ApiPostStep(headers, NDC_FIELD_SEARCH_POST_ENDPOINT,
				body.toString(), null, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiPostStep.run();
		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("18544: NDC FieldSearch With Special Character Status Check ( Special Character on Filter )")
	@Order(8)
	public List<DynamicNode> withBodySpecialChars() 
	{

		//Grabbing random Ndc from Db
		Ndc randomNdc =  NdcQueries.getRandomNdc();

		String fieldQuery = "marketingCategory";
		String searchTerm = randomNdc.getMarketingCategory();

		Integer size = 20000;

		//String filter selection for api body
		Map<String, String> filterStrings = new HashMap<String, String>();
		filterStrings.put("labeler", "%%");

		// Use Ndc.labeler as query & fieldquery
		JsonObject body = new JsonObject();

		body.addProperty("fieldQuery",fieldQuery);
		body.addProperty("query",searchTerm );

		Gson gson = new Gson();

		body.add("filter", gson.toJsonTree(filterStrings));
		body.addProperty("size", size);

		Headers headers = getGenericHeaders();
		ApiPostStep apiPostStep = new ApiPostStep(headers, NDC_FIELD_SEARCH_POST_ENDPOINT,
				body.toString(), null, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiPostStep.run();
		return apiPostStep.getTestResults();
	}


	/**
	 * 
	 * Helper Method for API filter, List String
	 * 
	 */
	private  Map<String, List<String>> ListString (Ndc randomNdc, String fields)
	{
		Map<String, List<String>> filterListStrings = new HashMap<String, List<String>>();

		for ( String field : fields.toUpperCase().split(","))
		{
			switch (field) {		
			case "BRANDNAMECODE":
				if ( StringUtils.isNotBlank(randomNdc.getBrandNameCode()) )
					filterListStrings.put("brandNameCode", Arrays.asList(randomNdc.getBrandNameCode()));
				break;

			case "MARKETINGCATEGORY":
				if ( StringUtils.isNotBlank(randomNdc.getMarketingCategory()) )
					filterListStrings.put("marketingCategory", Arrays.asList(randomNdc.getMarketingCategory()));
				break;

			case "DOSAGEFORM":
				if ( StringUtils.isNotBlank(randomNdc.getDosageForm()) )
					filterListStrings.put("dosageForm",Arrays.asList(randomNdc.getDosageForm()));
				break;

			default:
				break;
			}
		}
		return filterListStrings;
	}

	/**
	 * 
	 * Helper Method for API filter, String
	 * 
	 */
	private Map<String, String>  MapString (Ndc randomNdc, String fields)
	{
		Map<String, String> filterStrings = new HashMap<String, String>();

		for ( String field : fields.toUpperCase().split(","))
		{
			switch (field) {		
			case "LABELER":
				if ( StringUtils.isNotBlank(randomNdc.getLabeler()) )
					filterStrings.put("labeler",randomNdc.getLabeler());
				break;

			case "PRODUCTNAME":
				if ( StringUtils.isNotBlank(randomNdc.getProductName()) )
					filterStrings.put("productName", randomNdc.getProductName());
				break;

			case "RXOTC":
				if ( StringUtils.isNotBlank(randomNdc.getRxOTC()) )
					filterStrings.put("rxOTC", randomNdc.getRxOTC());
				break;

			case "GPI10GENERICNAME":
				if ( StringUtils.isNotBlank(randomNdc.getGpi10GenericName()) )
					filterStrings.put("gpi10GenericName", randomNdc.getGpi10GenericName());

			default:
				break;
			}
		}
		return filterStrings;
	}

	/**
	 * Validate the search result
	 *
	 * @param expected
	 * @param searchResult the API response that contains list of ndc
	 * @param apiBody
	 * @param actualMoreResults
	 * @return
	 */
	private List<DynamicNode> validateSearchResult(List<String> expected, FieldSearchNdcResponse searchResult, JsonObject apiBody, Boolean actualMoreResults) 
	{
		List<DynamicNode> test = new ArrayList<DynamicNode>();

		test.add(dynamicTest("Api Body :  " + apiBody.toString(), () -> assertTrue(true))); 
		//TODO Evaluate further with validaiton
		if ( expected.size() > API_MAX_ITEMS) {

			//test.add(dynamicTest("More Results [" + searchResult.isMoreResults() + "]", () -> assertTrue(searchResult.isMoreResults() == actualMoreResults)));

			test.add(dynamicTest("Validate results with API Body: " + apiBody, 
					() -> assertThat(searchResult.getApiInfo(), searchResult.getSearchResults(), containsInAnyOrder(expected.toArray(new String[expected.size()]))) ));

		}

		else {

			//convert apibody to json, grab the property request from the json and then make that assearch value/term, then do a for loop and verify the search term that starts with that value 

			org.json.JSONObject obj = new org.json.JSONObject(apiBody.toString());
			String searchTerm = obj.query("/query").toString();


			test.add(dynamicTest("Validate search term " + searchTerm + " in field query information",
					() -> {
						assertAll("Query name",
								() -> {
									for (String fieldSearch : searchResult.getSearchResults() )
									{
										assertTrue(StringUtils.containsAny(fieldSearch.toUpperCase(), searchTerm.toUpperCase()),
												fieldSearch + " did not contain the search term [" + searchTerm + "]\n" + searchResult.getApiInfo());
									}
								});
					}));

		}
		return test;

	}	


}
