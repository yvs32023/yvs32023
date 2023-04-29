/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;

import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.ProviderQueries;
import com.excellus.sqa.rxcc.dto.FieldSearchProviderResponse;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 *  https://apim-lbs-rxc-dev-east-001.developer.azure-api.net/api-details#api=provider&operation=fieldsearch
 * 
 * @author Manish Sharma (msharma)
 * @since 03/31/2022
 */
@Tag("ALL")
@Tag("PROVIDER")
@Tag("SEARCH")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostProviderFieldSearch")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class FieldSearchTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(FieldSearchTest.class);

	private static String GENERIC_BODY;

	public static void dataSetup()
	{
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("query", "Blue");

		GENERIC_BODY = requestBody.toString();
	}

	@TestFactory
	@DisplayName("4575: FieldProvider With Happy Path without Filter (Query:FIRST NAME without filter Size:200)")
	@Order(1)
	public List<DynamicNode> withBodyFirstNameWithoutFilter()
	{
		String fieldSearchName = "firstName";
		String searchTerm = "MAN";

		List<String> expected = ProviderQueries.fieldSearch(fieldSearchName, searchTerm, null, null);

		// Use Provider.firstname as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);

		// Use filter fields if needed, but not for this test case we need empty filter
		JsonObject filter = new JsonObject();
		body.add("filter", filter);

		// Call API
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_FIELD_SEARCH_POST_ENDPOINT, body.toString(), null, 200,null);
		apiPostStep.run();

		List<DynamicNode> tests = new ArrayList<>(apiPostStep.getTestResults());

		FieldSearchProviderResponse fieldSearchProviderResponse = apiPostStep.convertToJsonDTO(FieldSearchProviderResponse.class);

		// Validate
		// return validateSearchResult(searchResult,searchTerm,false);
		tests.addAll( validateSearchResult(expected, fieldSearchProviderResponse, body.toString()) );

		return tests;
	}

	@TestFactory
	@DisplayName("4575: FieldProvider With Happy Path without Filter (Query:LAST NAME without filter Size:200)") //failed
	@Order(2)
	public List<DynamicNode> withBodyFirstNameWithoutFilterLastName()
	{
		String fieldSearchName = "lastName";
		String searchTerm = "A";

		List<String> expected = ProviderQueries.fieldSearch(fieldSearchName, searchTerm, null, null);

		//  Use Provider.firstname as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);

		//Use filter fields if needed, but not for this test case we need empty filter
		JsonObject filter = new JsonObject();
		body.add("filter", filter);

		// Call API
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_FIELD_SEARCH_POST_ENDPOINT, body.toString(), null, 200,null);
		apiPostStep.run();

		List<DynamicNode> tests = new ArrayList<>(apiPostStep.getTestResults());

		FieldSearchProviderResponse fieldSearchProviderResponse = apiPostStep.convertToJsonDTO(FieldSearchProviderResponse.class);

		// Validate
		//return validateSearchResult(searchResult,searchTerm,true);
		tests.addAll( validateSearchResult(expected, fieldSearchProviderResponse, body.toString()) );

		return tests;
	}

	@TestFactory
	@DisplayName("4575: FieldProvider With Happy Path without Filter (Query:Phone Number without filter Size:200)") //failed
	@Order(3)
	public List<DynamicNode> withBodyFirstNameWithoutFilterOnPartialPhoneNumber()
	{
		String fieldSearchName = "phoneNumber";
		String searchTerm = "607";

		List<String> expected = ProviderQueries.fieldSearch(fieldSearchName, searchTerm, null, null);

		//  Use Provider.firstname as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);

		//Use filter fields if needed, but not for this test case we need empty filter
		JsonObject filter = new JsonObject();
		body.add("filter", filter);

		// Call API
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_FIELD_SEARCH_POST_ENDPOINT, body.toString(), null, 200,null);
		apiPostStep.run();

		List<DynamicNode> tests = new ArrayList<>(apiPostStep.getTestResults());

		FieldSearchProviderResponse fieldSearchProviderResponse = apiPostStep.convertToJsonDTO(FieldSearchProviderResponse.class);

		// Validate
		//return validateSearchResult(searchResult,searchTerm,true);
		tests.addAll( validateSearchResult(expected, fieldSearchProviderResponse, body.toString()) );

		return tests;
	}

	@TestFactory
	@DisplayName("4576: FieldProvider With Body/Happy Path with Filter (Query:Last Name with filter: Phone Number Size:200)") 
	@Order(4)
	public List<DynamicNode> withBodyFirstNameWithoutFilterOnPhoneNumber()
	{
		String fieldSearchName = "lastName";
		String searchTerm = "W";

		Map<String, List<String>> filters = new HashMap<>();
		filters.put("phoneNumber", Arrays.asList("6072561167"));

		List<String> expected = ProviderQueries.fieldSearch(fieldSearchName, searchTerm, filters, null);

		//  Use Provider.firstname as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);

		//Use filter fields if needed, but not for this test case we need empty filter
		Gson gson = new Gson ();
		body.add("filter", gson.toJsonTree(filters));

		// Call API
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_FIELD_SEARCH_POST_ENDPOINT, body.toString(), null, 200,null);
		apiPostStep.run();

		List<DynamicNode> tests = new ArrayList<>(apiPostStep.getTestResults());

		FieldSearchProviderResponse fieldSearchProviderResponse = apiPostStep.convertToJsonDTO(FieldSearchProviderResponse.class);

		// Validate
		tests.addAll( validateSearchResult(expected, fieldSearchProviderResponse, body.toString()) );

		return tests;
	}	

	@TestFactory
	@DisplayName("4576: Provider FieldSearch With Body/Happy Path with Filter (Query:lastName with	filters:City,State)")
	@Order(5)
	public List<DynamicNode> withBodyFirstNameWithFilter()
	{
		String fieldSearchName = "lastName";
		String searchTerm = "La";

		Map<String, List<String>> filters = new HashMap<>();
		filters.put("city", Arrays.asList("Rochester", "Syracuse"));
		filters.put("state", Arrays.asList("NY"));

		List<String> expected = ProviderQueries.fieldSearch(fieldSearchName, searchTerm, filters, null);

		//  Use Provider.firstname as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);

		//Use filter fields if needed, but not for this test case we need empty filter
		Gson gson = new Gson ();
		body.add("filter", gson.toJsonTree(filters));

		// Call API
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_FIELD_SEARCH_POST_ENDPOINT, body.toString(), null, 200,null);
		apiPostStep.run();

		List<DynamicNode> tests = new ArrayList<>(apiPostStep.getTestResults());

		FieldSearchProviderResponse fieldSearchProviderResponse = apiPostStep.convertToJsonDTO(FieldSearchProviderResponse.class);

		// Validate
		tests.addAll( validateSearchResult(expected, fieldSearchProviderResponse, body.toString()) );

		return tests;
	}	

	@TestFactory
	@DisplayName("4576: Provider FieldSearch With Body/Happy Path with Filter (Query:Taxonomy Descr with	filters: Status Indicator)")
	@Order(6)
	public List<DynamicNode> withBodyFirstNameWithFilterOne()
	{
		String fieldSearchName = "taxonomyDescr";
		String searchTerm = "practi";

		Map<String, List<String>> filters = new HashMap<>();
		filters.put("statusInd", Arrays.asList("Active"));

		//filters.put("faxVerified","false");  //{fieldQuery: "taxonomyDescr", query: "practi", filter: {statusInd: ["Active"], faxVerified : false}}
		List<String> expected = ProviderQueries.fieldSearch(fieldSearchName, searchTerm, filters, null);

		//  Use Provider.statusInd as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);

		//Use filter fields if needed, but not for this test case we need empty filter
		Gson gson = new Gson ();
		body.add("filter", gson.toJsonTree(filters));

		// Call API
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_FIELD_SEARCH_POST_ENDPOINT, body.toString(), null, 200,null);
		apiPostStep.run();

		List<DynamicNode> tests = new ArrayList<>(apiPostStep.getTestResults());

		FieldSearchProviderResponse fieldSearchProviderResponse = apiPostStep.convertToJsonDTO(FieldSearchProviderResponse.class);

		// Validate
		tests.addAll( validateSearchResult(expected, fieldSearchProviderResponse, body.toString()) );

		return tests;
	}	

	@TestFactory
	@DisplayName("4576: Provider FieldSearch With Body/Happy Path with Filter (Query:Taxonomy Descr with	filters: Status Indicator as ACTIVE with faxVerified as true)")
	@Order(7)
	public List<DynamicNode> withBodyTaxonomyDescrWithFilterOnStatusAndFaxVerified()
	{

		String fieldSearchName = "taxonomyDescr";
		String searchTerm = "practi";
		Integer size = 250;
		
	    Map<String, List<String>> mapString = new HashMap<>();
        mapString.put("statusInd", Arrays.asList("Active"));
        
        Map<String, Boolean> mapBoolean = new HashMap<>();
        mapBoolean.put("faxVerified", true);
                  
        List<String> expected = ProviderQueries.fieldSearch(fieldSearchName, searchTerm, mapString, mapBoolean);
        
        JsonObject body = new JsonObject();
        body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);
        Gson gson = new Gson();
        
        body.add("filter", gson.toJsonTree(mapString));
        mapBoolean.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );
        
        body.addProperty("size", size);

		// Call API
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_FIELD_SEARCH_POST_ENDPOINT, body.toString(), null, 200,null);
		apiPostStep.run();

		List<DynamicNode> tests = new ArrayList<>(apiPostStep.getTestResults());

		FieldSearchProviderResponse fieldSearchProviderResponse = apiPostStep.convertToJsonDTO(FieldSearchProviderResponse.class);

		// Validate
		tests.addAll( validateSearchResult(expected, fieldSearchProviderResponse, body.toString()) );

		return tests;
	}	

	@TestFactory
	@DisplayName("4575: FieldProvider With Happy Path with Filter (Query:NPI with filter: fax verified Size:200)") //failed : db returning around 63-64 but api returning only 30
	@Order(8)
	public List<DynamicNode> withBodyNPIWithFilterOnFaxVerified()
	{
		String fieldSearchName = "npi";
		String searchTerm = "192";
		
		 Map<String, Boolean> mapBoolean = new HashMap<>();
	     mapBoolean.put("faxVerified", true);

		List<String> expected = ProviderQueries.fieldSearch(fieldSearchName, searchTerm, null, mapBoolean);

		//  Use Provider.firstname as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);
		Gson gson = new Gson();
        
        body.add("filter", gson.toJsonTree(mapBoolean));

		// Call API
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_FIELD_SEARCH_POST_ENDPOINT, body.toString(), null, 200,null);
		apiPostStep.run();

		List<DynamicNode> tests = new ArrayList<>(apiPostStep.getTestResults());

		FieldSearchProviderResponse fieldSearchProviderResponse = apiPostStep.convertToJsonDTO(FieldSearchProviderResponse.class);

		// Validate
		//return validateSearchResult(searchResult,searchTerm,false);
		tests.addAll( validateSearchResult(expected, fieldSearchProviderResponse, body.toString()) );

		return tests;
	}

	@TestFactory
	@DisplayName("4579: FieldProvider Invalid Auth")
	@Order(9)
	public List<DynamicNode> invalidAuthentication()
	{
		ApiPostStep apiPostStep = new ApiPostStep( getHeadersInvalidAuth(), PROVIDER_FIELD_SEARCH_POST_ENDPOINT, GENERIC_BODY, null, 401, HTTP_401_UNAUTHORIZED);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("4580: FieldProvider Invalid Method")
	@Order(10)
	public List<DynamicNode> invalidMethod()
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), PROVIDER_FIELD_SEARCH_POST_ENDPOINT, null, 404, HTTP_404_RESOURCE_NOT_FOUND);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("4582: FieldProvider With Body Spl Char")
	@Order(11)
	public List<DynamicNode> specialChars()
	{
		//Special Chars on searchTerm
		String fieldSearchName = "firstName";
		String searchTerm = "%%%";

		//Use Provider.firstname as fieldquery
		JsonObject body = new JsonObject();
		body.addProperty("fieldquery",fieldSearchName);
		body.addProperty("query", searchTerm);

		//Use filter fields if needed, but not for this test case we need empty filter
		JsonObject filter = new JsonObject();
		body.add("filter", filter);

		ApiPostStep apiPostStep = new ApiPostStep( getGenericHeaders(), PROVIDER_FIELD_SEARCH_POST_ENDPOINT, GENERIC_BODY, null, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	/**
	 * Validate the search result
	 *
	 * @param cosmosResult list
	 * @param searchResult the API response that contains list of providers
	 * @param apiBody api request body
	 * @return list of {@link DynamicNode} of test
	 */
	private List<DynamicNode> validateSearchResult(List<String> cosmosResult, FieldSearchProviderResponse searchResult, String apiBody)
	{
		List<DynamicNode> test = new ArrayList<>();

		/*
		 * GC (04/04/23)
		 * There is not a good Cosmos query that would match what the API response since it uses
		 * Cognisant search functionality of Azure Cosmos. Having said that, modified how the result is validated
		 */

		/*
		 * First, if there is more result in APU response than the Cosmos query then
		 * make sure at the very least that all search result from the API contains a prefix
		 * of the search term.
		 */
		if ( searchResult.getSearchResults().size() > cosmosResult.size() )
		{
			//convert apibody to json, grab the property request from the json and then make that assearch value/term, then do a for loop and verify the search term that strats with that value
			org.json.JSONObject obj = new org.json.JSONObject(apiBody);
			String searchTerm = obj.query("/query").toString();

			final String regEx = ".*\\b" + searchTerm.toUpperCase() + "[a-zA-Z0-9]*\\b.*";	// RegEx for word that starts with the searchTerm
			final Pattern pattern = Pattern.compile(regEx);

			test.add(dynamicTest("Validate search term " + searchTerm + " in field query information",
					() -> {
						assertAll("Query name",
								() -> {
									for (String fieldSearch : searchResult.getSearchResults() )
									{
										try
										{
											String theFieldSearch = fieldSearch.toUpperCase().replaceAll("-", " "); // this is to handle last name with hyphen
											Matcher matcher = pattern.matcher(theFieldSearch);
											boolean result = matcher.matches();
											assertTrue(result, fieldSearch + " did not contain the search term [" + searchTerm + "]");
										}
										catch (Exception e)
										{
											fail("Field search result: " + fieldSearch, e);
										}

									}
								});
					}));
		}
		/*
		 * Second, if the Cosmos query returned more result than API then make sure every items
		 * in the API response is in Cosmos query
		 */
		else if ( cosmosResult.size() > searchResult.getSearchResults().size() )
		{
			test.add(dynamicTest("Validate results with API Body: " + apiBody,
					() -> assertThat(cosmosResult, hasItems(searchResult.getSearchResults().toArray(new String[searchResult.getSearchResults().size()]))) ));
		}
		/*
		 * Third, if both Cosmos and API has the same number of result then make sure the items are the same
		 */
		else
		{
			test.add(dynamicTest("Validate results with API Body: " + apiBody,
					() -> assertThat(searchResult.getSearchResults(), containsInAnyOrder(cosmosResult.toArray(new String[cosmosResult.size()]))) ));
		}

		return test;
	}
}
