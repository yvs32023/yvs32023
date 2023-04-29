/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;

import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.restapi.steps.IApiStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.ProviderQueries;
import com.excellus.sqa.rxcc.dto.Provider;
import com.excellus.sqa.rxcc.dto.SearchProvider;
import com.excellus.sqa.rxcc.dto.SearchProviderResponse;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;


/**
 * 
 * 
 * {
 * "query": "string",
 *  "filter": {
 *      "npi": ["string"],
 *      "firstName": ["string"],
 * 	    "lastName": ["string"],
 *      "taxonomyCode": ["string"],
 * 		"taxonomyDescr": ["string"],
 * 		"phoneNumber": ["string"],
 * 		"faxNumber": ["string"],
 *      "city": ["string"],
 *      "state": ["string"],
 *      "statusInd": ["string"],
 *      "faxVerified": boolean,	
 *      "officeLocationsCount": number,
 * 
 * },
 *  "sortField": "string", 
 *  "sortDirection": "string",
 *  "size": 200 (default/optional)
 *  }
 * 
 * 
 * 
 * 
 * POST https://apim-lbs-rxc-tst-east-001.azure-api.net/api/provider/provider-search
 * 
 * @author Manish Sharma (msharma)
 * @since 03/16/2022
 */

@Tag("ALL")
@Tag("PROVIDER")
@Tag("SEARCH")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostProviderSearch")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class SearchProviderTest extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(SearchProviderTest.class);

	private final String GENERIC_BODY;

	public SearchProviderTest()
	{
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("query", "1003003856");

		GENERIC_BODY = requestBody.toString();
	}	


	@TestFactory
	@DisplayName("777: SearchProvider With Happy Path with Filter (Query:FIRST NAME adding lastname and city filter Size:200)")
	@Order(1)
	public List<DynamicNode> withBodyFirstNameWithFilter()
	{
		// Query the Cosmos db for a random provider
		Provider expected = ProviderQueries.getRandomProvider(); 

		//  Use Provider.firstname as query
		JsonObject body = new JsonObject();
		body.addProperty("query", expected.getFirstName().toUpperCase());

		//Use filter fields (upper case) in Provider.lastname 
		JsonArray lastNames = new JsonArray();
		lastNames.add(expected.getLastName());

		//Use filter fields (upper case) in Provider.city
		JsonArray cities = new JsonArray();
		cities.add(expected.getOfficeLocations().get(0).getCity());

		JsonObject filter = new JsonObject();
		filter.add("lastNames", lastNames);
		filter.add("city", cities);

		body.add("filter", filter);

		// Call API
		IApiStep apiStep = providerSearch(body);

		// Validate
		return validateSearchResult(apiStep, expected.getFirstName());
	}	
	
	@TestFactory
	@DisplayName("7229: SearchProvider With Happy Path with Filter (Query:FaxNumber adding lastname as Null ,taxonomyDescr,statusInd,phone number and taxonomyCode filter Size:200)")
	@Order(2)
	public List<DynamicNode> withBodyFaxNumberwithFilterOnLastNameAsNull()
	{
		// Query the Cosmos db for a random provider
		Provider expected = ProviderQueries.getRandomProvider(); 

		//  Use Provider.firstname as query
		JsonObject body = new JsonObject();
		body.addProperty("query", expected.getOfficeLocations().get(0).getFaxNumber());

		//Use filter fields (upper case) in Provider.lastname 
		JsonArray lastNames = new JsonArray();
		lastNames.add("");

		//Use filter fields  in Provider.taxonomyCode
		JsonArray taxonomyCode = new JsonArray();
		taxonomyCode.add(expected.getTaxonomyCode());

		//Use filter fields  in Provider.taxonomyDescr
		JsonArray taxonomyDescr = new JsonArray();
		taxonomyDescr.add(expected.getTaxonomyDescr());

		//Use filter fields  in Provider.statusInd
		JsonArray statusInd = new JsonArray();
		statusInd.add(expected.getStatusInd());

		//Use filter fields  in Provider.phoneNumber
		JsonArray phoneNumber = new JsonArray();
		phoneNumber.add(expected.getOfficeLocations().get(0).getPhoneNumber());

		JsonObject filter = new JsonObject();
		filter.add("lastNames", lastNames);
		filter.add("taxonomyCode", taxonomyCode);
		filter.add("taxonomyDescr", taxonomyDescr);
		filter.add("statusInd", statusInd);
		filter.add("phoneNumber", phoneNumber);

		body.add("filter", filter);

		// Call API
		IApiStep apiStep = providerSearch(body);

		// Validate
		return validateSearchResult(apiStep, expected.getOfficeLocations().get(0).getFaxNumber());
	}	

	@TestFactory
	@DisplayName("7602: SearchProvider With Happy Path with Filter (Query:TaxonomyCode adding statusInd ,npi, firstName, phone lastName, officeLocationsCount and faxVerified filter Size:200)")
	@Order(3)
	public List<DynamicNode> withBodyTaxonomyCodewithAllFilter()
	{
		// Query the Cosmos db for a random provider
		Provider expected = ProviderQueries.getRandomProvider(); 

		Map<String, List<String>> mapString = new HashMap<>();
		mapString.put("statusInd", Arrays.asList(expected.getStatusInd()));
		mapString.put("npi", Arrays.asList(expected.getNpi()));
		mapString.put("firstName", Arrays.asList(expected.getFirstName()));
		mapString.put("lastName", Arrays.asList(expected.getLastName()));

		Map<String, Integer> mapInteger = new HashMap<>();
		mapInteger.put("officeLocationsCount", expected.getOfficeLocations().size());

		Map<String, Boolean> mapBoolean = new HashMap<>();
		mapBoolean.put("faxVerified", expected.getOfficeLocations().get(0).isFaxVerified());

		// List<String> expected = ProviderQueries.fieldSearch(fieldSearchName, searchTerm, mapString, null, mapInteger);

		JsonObject body = new JsonObject();
		body.addProperty("query", expected.getTaxonomyCode());
		body.addProperty("sortField", "npi");
		body.addProperty("sortDirection", "asc");
		body.addProperty("size", 200);

		Gson gson = new Gson();

		body.add("filter", gson.toJsonTree(mapString));
		mapInteger.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );
		mapBoolean.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );

		logger.debug("withBodyTaxonomyCodewithAllFilter: " + body);

		// Call API
		IApiStep apiStep = providerSearch(body);

		logger.debug("response: " + apiStep.getResponse().asString());

		// Validate
		return validateSearchResult(apiStep,expected.getTaxonomyCode());
	}


	@TestFactory
	@DisplayName("777: SearchProvider With Happy Path with ASC Sort (Query:FIRST NAME adding lastname as sort Size:200)")
	@Order(4)
	public List<DynamicNode> withBodyFirstNameWithAscSort()
	{
		// Query the Cosmos db for a random provider
		Provider expected = ProviderQueries.getRandomProvider(); 

		//Sort with descending order in last field after querying firstName from random provider 		
		JsonObject body = new JsonObject();
		body.addProperty("query", expected.getFirstName().toLowerCase());
		body.addProperty("sortField", "lastName");
		body.addProperty("sortDirection", "asc");

		// Call API
		IApiStep apiStep = providerSearch(body); //actual

		List<DynamicNode> results = validateSearchResult(apiStep, expected.getFirstName());

		SearchProviderResponse searchResult = apiStep.convertToJsonDTO(SearchProviderResponse.class);
		List<SearchProvider> actual = searchResult.getProviders(); 

		List<String> actualOrder = new LinkedList<>();
		for ( SearchProvider provider : actual )
			actualOrder.add(provider.getLastName().toUpperCase());

		//Query Result from API and make sure the sort is working, FYI : not validating the data from API results
		List<String> expectedOrder = new LinkedList<>(actualOrder);

		//Checking api result sorting order to external sorting
		expectedOrder.sort(String.CASE_INSENSITIVE_ORDER);

		results.add( dynamicTest("Sorting order", () -> assertThat(actualOrder, contains(expectedOrder.toArray(new String[expectedOrder.size()])))));

		return results;
	}

	@TestFactory
	@DisplayName("777: SearchProvider With Happy Path with  DESC Sort (Query:FIRST NAME adding city as sort Size:200)")
	@Order(5)
	public List<DynamicNode> withBodyFirstNameWithDescSort()
	{
		// Query the Cosmos db for a random provider after querying firstName from random provider
		Provider expected = ProviderQueries.getRandomProvider(); 

		//Sort with descending order in city field  				
		JsonObject body = new JsonObject();
		body.addProperty("query", expected.getFirstName().toLowerCase());
		body.addProperty("sortField", "city");
		body.addProperty("sortDirection", "desc");

		logger.debug("withBodyFirstNameWithDescSort: " + body);

		// Call API
		IApiStep apiStep = providerSearch(body);

		List<DynamicNode> results = validateSearchResult(apiStep, expected.getFirstName());

		SearchProviderResponse searchResult = apiStep.convertToJsonDTO(SearchProviderResponse.class);
		List<SearchProvider> actual = searchResult.getProviders(); 

		List<String> actualOrder = new LinkedList<>();
		for ( SearchProvider provider : actual )
		{
			// Do not include where there is no City
			if (provider.getDefaultOfficeLocation() != null && StringUtils.isNotEmpty(provider.getDefaultOfficeLocation().getCity()) ) {
				actualOrder.add(provider.getDefaultOfficeLocation().getCity());
			}
			else {
				actualOrder.add("");	// Set City to empty string to avoid null pointer
			}
		}

		logger.debug("actualOrder: " + actualOrder);

		// Copy the result from API then sort it in descending order. FYI - not validating the data from API results
		List<String> expectedOrder = new ArrayList<>(actualOrder);
		expectedOrder.sort(String.CASE_INSENSITIVE_ORDER.reversed());

		logger.debug("expectedList: " + expectedOrder);

		assertThat(actualOrder, contains(expectedOrder.toArray(new String[expectedOrder.size()])));

		return results;
	}

	@TestFactory
	@DisplayName("777: SearchProvider With Happy Path  (Query:NPI; Size:200)")
	@Order(6)
	public List<DynamicNode> withBodyNpi()
	{		
		// Cosmos db
		Provider provider = ProviderQueries.getRandomProvider(); // retrieve random provider
		String searchTerm = provider.getNpi();

		// Call API
		IApiStep apiStep = providerSearch(searchTerm, 200);

		// Validate
		return validateSearchResult(apiStep, searchTerm);
	}


	@TestFactory
	@DisplayName("777: SearchProvider With Happy Path  (Query:FIRST NAME; Size:200)")
	@Order(7)
	public List<DynamicNode> withBodyFirstName()
	{
		String searchTerm = "MAUREEN";	

		// Call API
		IApiStep apiStep = providerSearch(searchTerm, 200);

		// Validate
		return validateSearchResult(apiStep, searchTerm);
	}
	
	@TestFactory
	@DisplayName("7608: SearchProvider With Special Character Status Check (Query:FIRST NAME; Size:200)")
	@Order(8)
	public List<DynamicNode> withBodySpecialChars()
	{
		String searchTerm = "Déjà";

		// Call API
		IApiStep apiStep = providerSearch(searchTerm, 0);

		SearchProviderResponse searchProviderResponse = apiStep.convertToJsonDTO(SearchProviderResponse.class);

		apiStep.getTestResults().add(dynamicTest("Search result count", () -> assertEquals(0, searchProviderResponse.getTotalResults())));

		return apiStep.getTestResults();
	}

	@TestFactory
	@DisplayName("777: SearchProvider With Happy Path  (Query:FIRST NAME and LASTNAME; Size:200)")
	@Order(9)
	public List<DynamicNode> withBodyFirstNameAndLastName()
	{
		Provider provider = ProviderQueries.getRandomProvider();
		String searchTerm = provider.getFirstName() + " " + provider.getLastName();

		// Call API
		IApiStep apiStep = providerSearch(searchTerm, 200);

		// Validate
		return validateSearchResult(apiStep, searchTerm);
	}	

	@TestFactory
	@DisplayName("782: SearchProvider Invalid Auth")
	@Order(10)
	public List<DynamicNode> invalidAuthentication()
	{
		ApiPostStep apiPostStep = new ApiPostStep(getHeadersInvalidAuth(), PROVIDER_SEARCH_POST_ENDPOINT, GENERIC_BODY, null, 401, HTTP_401_UNAUTHORIZED);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("4569: SearchProvider With Body (invalid JSON format)")
	@Order(11)
	public List<DynamicNode> invalidBody()
	{
		String jsonBody = "{\n\"query\":\"Garrett Weston\"\n";

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_SEARCH_POST_ENDPOINT, jsonBody, null, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("783: SearchProvider Invalid Method")
	@Order(12)
	public List<DynamicNode> invalidMethod()
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), PROVIDER_SEARCH_POST_ENDPOINT, null,404, HTTP_404_RESOURCE_NOT_FOUND);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	/**
	 * Perform the API call
	 *
	 * 
	 * @param body to be search with
	 * @return {@link IApiStep} that is  used for API call
	 */
	public static IApiStep providerSearch(JsonObject body)
	{
		logger.debug("Starting API call");

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(),PROVIDER_SEARCH_POST_ENDPOINT, body.toString(), null, 200, HTTP_200_OK);
		apiPostStep.run();

		logger.debug("API call completed with the response:\n" + apiPostStep.getResponse().asString());

		return apiPostStep;
	}

	/**
	 * Perform the API call
	 *
	 * 
	 * @param searchTerm to be search with
	 * @param size of the response to return
	 * @return {@link IApiStep} that is  used for API call
	 */
	public static IApiStep providerSearch(String searchTerm, int size)
	{
		logger.debug("Starting API call");

		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("query", searchTerm);

		if ( size > 0 )
			requestBody.addProperty("size", String.valueOf(size));

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_SEARCH_POST_ENDPOINT, requestBody.toString(), null, 200, null);
		apiPostStep.run();

		return apiPostStep;
	}

	/**
	 * Validate the search result
	 *
	 * @param apiStep the API step that was performed which contains the API response
	 * @param searchTerm search terms
	 * @return list of {@link DynamicNode}
	 */
	public static List<DynamicNode> validateSearchResult(IApiStep apiStep, String searchTerm)
	{
		List<DynamicNode> test = new ArrayList<>(apiStep.getTestResults());

		SearchProviderResponse searchResult = apiStep.convertToJsonDTO(SearchProviderResponse.class);

		test.add(dynamicTest("Total Results [" + searchResult.getTotalResults() + "]",
				() -> assertTrue(searchResult.getTotalResults() > 0,
						"Expecting API return at least 1 or more provider\n" + apiStep.getApiInfo())));	// GC (04/05/23) add API info when it fails

		// GC (04/05/23) return test if there are no search result
		if ( searchResult.getTotalResults() < 1 ) {
			return test;
		}

		// GC (04/05/23) refactor to use StringBuffer to collect failures instead of assertAll which sometimes causes java.lang.NoClassDefFoundError
		test.add(dynamicTest("Validate search term [" + searchTerm + "] in provider information",
				() -> {
					String failureMessage = "";
					for (SearchProvider providerSearch : searchResult.getProviders() )
					{
						if ( !providerSearch.providerContains(searchTerm) )
						{
							failureMessage += providerSearch.getSearchFieldValues() + " did not contain the search term [" + searchTerm + "]\n";
						}
					}

					if ( failureMessage.length() > 0 )
					{
						failureMessage += apiStep.getApiInfo();
						fail(failureMessage);
					}
					else
					{
						assertTrue(true);
					}
				}));

		return test;
	}
}