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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.excellus.sqa.rxcc.dto.Ndc;
import com.excellus.sqa.rxcc.dto.Ndc.NdcField;
import com.excellus.sqa.rxcc.dto.NdcSearch;
import com.excellus.sqa.rxcc.dto.NdcSearchResponse;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.restassured.http.Headers;

/**
 * 
 * POST https://apim-lbs-rxc-dev-east-001.azure-api.net/api/ndc/ndc-search
 *
 * 
 * @author Manish Sharma (msharma)
 * @since 05/23/2022
 */
@Tag("ALL")
@Tag("NDC")
@Tag("SEARCH")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostNdcSearch")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class SearchNdcTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(SearchNdcTest.class);

	private final String GENERIC_BODY;
	private final Integer size = 200;

	public SearchNdcTest()
	{

		String search = "DéjàMaïs";	  
		String fieldSelections = "gpi10GenericName";

		Map<String, String> mapString = new HashMap<String, String>();
		mapString.put("searchBy", "ndc");

		Map<String, String> mapString2 = new HashMap<String, String>();
		mapString2.put("search", search);


		JsonObject requestBody = new JsonObject();

		JsonArray list = new JsonArray();
		Stream.of(new String[] {fieldSelections,"gpi14Name","gpi12GenericNameDoseForm"})
		.forEach(list::add);
		requestBody.add("fieldSelections", list );

		Gson gson = new Gson();
		requestBody.add("filter", gson.toJsonTree(mapString2));

		requestBody.addProperty("size", size);

		GENERIC_BODY = requestBody.toString();
	}	


	@TestFactory
	@DisplayName("17719: SearchNdc With Happy Path Search by ndc with Filter (Field Selection : 6 Filters")
	@Order(1)
	public List<DynamicNode> withBodyFirstNameWithFilter() throws JsonProcessingException
	{

		//Grabbing random Ndc from Db
		Ndc randomNdc =  NdcQueries.getRandomNdc();

		//field selections
		List<NdcField> fieldSelections = Arrays.asList(NdcField.labeler, NdcField.marketingCategory, NdcField.brandNameCode);

		String searchBy = "ndc";

		Map<String, List<String>> mapListString = new HashMap<String, List<String>>();
		mapListString = ListString(randomNdc, "brandNameCode,marketingCategory,dosageForm");

		Map<String, String> mapString = new HashMap<String, String>();
		mapString = MapString(randomNdc, "labeler,productName,rxOTC");

		Map<String, String> mapStringOnlyApi = new HashMap<String, String>();
		mapStringOnlyApi.put("searchBy", searchBy);
		mapStringOnlyApi.put("search",  randomNdc.getNdc().substring(0,5));

		List<String> fieldsString = fieldSelections.stream()
				.map(field -> field.toString())
				.collect(Collectors.toList());


		// Query the Cosmos db for a random ndc
		List<Ndc> expected = NdcQueries.fieldSelections(searchBy,  randomNdc.getNdc().substring(0,5), mapListString, mapString); 

		JsonObject body = new JsonObject();

		JsonArray jsonArray = new JsonArray();
		Stream.of( fieldsString.toArray(new String[fieldsString.size()]))
		.forEach(jsonArray::add);

		body.add("fieldSelections", jsonArray);

		Gson gson = new Gson();

		//updated to brandNameCodes as per Api body requirement
		mapListString.put("brandNameCodes",mapListString.get("brandNameCode"));
		mapListString.remove("brandNameCode");

		mapListString.put("marketingCategories",mapListString.get("marketingCategory"));
		mapListString.remove("marketingCategory");

		mapListString.put("dosageForms",mapListString.get("dosageForm"));
		mapListString.remove("dosageForm");

		body.add("filter", gson.toJsonTree(mapListString));
		mapString.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );
		mapStringOnlyApi.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );

		body.addProperty("size", size);

		List<DynamicNode> test = new ArrayList<>();

		// Run the Test // Call API
		test=  happyPath(expected,  randomNdc.getNdc(), fieldSelections, body, size);

		return test;
	}	

	@TestFactory
	@DisplayName("17719: SearchNdc With Happy Path Search by ndc with Filter (Field Selection : 4 Filters")
	@Order(2)
	public List<DynamicNode> withBodyFirstNameWithFilterss() throws JsonProcessingException
	{

		//Grabbing random Ndc from Db
		Ndc randomNdc =  NdcQueries.getRandomNdc();

		//field selections
		List<NdcField> fieldSelections = Arrays.asList(NdcField.productName, NdcField.marketingCategory, NdcField.gpi0CategoryRange);

		String searchBy = "ndc";

		//List String filter selection for api body
		Map<String, List<String>> mapListString = new HashMap<String, List<String>>();
		mapListString = ListString(randomNdc, "brandNameCode,appNumber,dosageForm");

		//String filter selection for api body
		Map<String, String> mapString = new HashMap<String, String>();
		mapString = MapString(randomNdc, "labeler");

		Map<String, String> mapString2 = new HashMap<String, String>();
		mapString2.put("searchBy", searchBy);
		mapString2.put("search",  randomNdc.getNdc().substring(0,6));

		List<String> fieldsString = fieldSelections.stream()
				.map(field -> field.toString())
				.collect(Collectors.toList());


		// Query the Cosmos db for a random ndc
		List<Ndc> expected = NdcQueries.fieldSelections(searchBy,  randomNdc.getNdc().substring(0,6), mapListString, mapString); 

		JsonObject body = new JsonObject();

		JsonArray jsonArray = new JsonArray();
		Stream.of( fieldsString.toArray(new String[fieldsString.size()]))
		.forEach(jsonArray::add);

		body.add("fieldSelections", jsonArray);

		Gson gson = new Gson();

		//updated to brandNameCodes as per Api body requirement
		mapListString.put("brandNameCodes",mapListString.get("brandNameCode"));
		mapListString.remove("brandNameCode");

		mapListString.put("marketingCategories",mapListString.get("marketingCategory"));
		mapListString.remove("marketingCategory");

		mapListString.put("dosageForms",mapListString.get("dosageForm"));
		mapListString.remove("dosageForm");

		body.add("filter", gson.toJsonTree(mapListString));
		mapString.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );
		mapString2.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );

		body.addProperty("size", size);

		List<DynamicNode> test = new ArrayList<>();

		// Run the Test // Call API
		test=  happyPath(expected,  randomNdc.getNdc(), fieldSelections, body, size);

		return test;
	}	

	@TestFactory
	@DisplayName("17719: SearchNdc With Happy Path Search by gpi14Unformatted with Filter (Field Selection : 6 Fields, Filter 3")
	@Order(3)
	public List<DynamicNode> withBodyGpi14UnformattedWithFilterss() throws JsonProcessingException
	{

		//Grabbing random Ndc from Db
		Ndc randomNdc =  NdcQueries.getRandomNdc();

		//field selections
		List<NdcField> fieldSelections = Arrays.asList(NdcField.productName, NdcField.marketingCategory, NdcField.brandNameCode, NdcField.gpi8BaseName, NdcField.gpi8BaseNameCode);

		String searchBy = "gpi14Unformatted";

		//List String filter selection for api body
		Map<String, List<String>> mapListString = new HashMap<String, List<String>>();
		mapListString = ListString(randomNdc, "brandNameCode");

		//String filter selection for api body
		Map<String, String> mapString = new HashMap<String, String>();
		mapString = MapString(randomNdc, "labeler,rxOTC");

		Map<String, String> mapString2 = new HashMap<String, String>();
		mapString2.put("searchBy", searchBy);
		mapString2.put("search",  randomNdc.getGpi14Unformatted().substring(0,6));

		List<String> fieldsString = fieldSelections.stream()
				.map(field -> field.toString())
				.collect(Collectors.toList());


		// Query the Cosmos db for a random ndc
		List<Ndc> expected = NdcQueries.fieldSelections(searchBy,  randomNdc.getGpi14Unformatted().substring(0,6), mapListString, mapString); 

		JsonObject body = new JsonObject();

		JsonArray jsonArray = new JsonArray();
		Stream.of( fieldsString.toArray(new String[fieldsString.size()]))
		.forEach(jsonArray::add);

		body.add("fieldSelections", jsonArray);

		Gson gson = new Gson();

		//updated to brandNameCodes as per Api body requirement
		mapListString.put("brandNameCodes",mapListString.get("brandNameCode"));
		mapListString.remove("brandNameCode");

		body.add("filter", gson.toJsonTree(mapListString));
		mapString.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );
		mapString2.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );

		body.addProperty("size", size);

		List<DynamicNode> test = new ArrayList<>();

		// Run the Test // Call API
		test=  happyPath(expected,  randomNdc.getNdc(), fieldSelections, body, size);

		return test;
	}	


	@TestFactory
	@DisplayName("17717: SearchNdc With Happy Path Search by GPI14UNFORMATTED 2 Filter" )
	@Order(4)
	public List<DynamicNode> withBodyGpi14UnformattedWithOutFilterss() throws JsonProcessingException
	{

		//Grabbing random Ndc from Db
		Ndc randomNdc =  NdcQueries.getRandomNdc();

		//field selections
		List<NdcField> fieldSelections = Arrays.asList(NdcField.gpi10GenericNameCode, NdcField.gpi12GenericNameDoseForm, NdcField.gpi14Name, NdcField.gpi14Unformatted, NdcField.gpi2Group, NdcField.gpi6Subclass, NdcField.gpi6SubclassCode);

		String searchBy = "gpi14Unformatted";

		//String filter selection for api body
		Map<String, String> mapString = new HashMap<String, String>();
		mapString = MapString(randomNdc, "labeler,rxOTC");

		Map<String, String> mapString2 = new HashMap<String, String>();
		mapString2.put("searchBy", searchBy);

		mapString2.put("search",  randomNdc.getGpi14Unformatted().substring(0,6));


		List<String> fieldsString = fieldSelections.stream()
				.map(field -> field.toString())
				.collect(Collectors.toList());

		// Query the Cosmos db for a random ndc
		List<Ndc> expected = NdcQueries.fieldSelections(searchBy,  randomNdc.getGpi14Unformatted().substring(0,6), null, mapString); 

		JsonObject body = new JsonObject();

		JsonArray jsonArray = new JsonArray();
		Stream.of( fieldsString.toArray(new String[fieldsString.size()]))
		.forEach(jsonArray::add);

		body.add("fieldSelections", jsonArray);

		Gson gson = new Gson();

		body.add("filter", gson.toJsonTree(mapString));
		mapString2.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );

		body.addProperty("size", size);

		List<DynamicNode> test = new ArrayList<>();

		// Run the Test // Call API
		test=  happyPath(expected,  randomNdc.getNdc(), fieldSelections, body, size);

		return test;
	}	


	@TestFactory
	@DisplayName("17717: SearchNdc With Happy Path Search by NDC with 1 Filter" )
	@Order(5)
	public List<DynamicNode> withBodyNdcWithOutFiltersOnNdc() throws JsonProcessingException
	{

		//Grabbing random Ndc from Db
		Ndc randomNdc =  NdcQueries.getRandomNdc();

		//field selections
		List<NdcField> fieldSelections = Arrays.asList(NdcField.ndc,NdcField.marketingCategory, NdcField.brandNameCode,  NdcField.ndc, NdcField.gpi0CategoryRange,NdcField.productName,  NdcField.gpi10GenericName);


		String searchBy = "ndc";

		//List String filter selection for api body
		Map<String, List<String>> mapListString = new HashMap<String, List<String>>();
		mapListString = ListString(randomNdc, "brandNameCode,marketingCategory");

		Map<String, String> mapString2 = new HashMap<String, String>();
		mapString2.put("searchBy", searchBy);
		mapString2.put("search",  randomNdc.getNdc().substring(0,6));

		List<String> fieldsString = fieldSelections.stream()
				.map(field -> field.toString())
				.collect(Collectors.toList());


		// Query the Cosmos db for a random ndc
		List<Ndc> expected = NdcQueries.fieldSelections(searchBy,  randomNdc.getNdc().substring(0,6), mapListString, null); 

		JsonObject body = new JsonObject();

		JsonArray jsonArray = new JsonArray();
		Stream.of( fieldsString.toArray(new String[fieldsString.size()]))
		.forEach(jsonArray::add);

		body.add("fieldSelections", jsonArray);

		Gson gson = new Gson();

		//updated to brandNameCodes as per Api body requirement
		mapListString.put("brandNameCodes",mapListString.get("brandNameCode"));
		mapListString.remove("brandNameCode");

		//updated to marketingCategories as per Api body requirement
		mapListString.put("marketingCategories",mapListString.get("marketingCategory"));
		mapListString.remove("marketingCategory");

		body.add("filter", gson.toJsonTree(mapListString));
		mapString2.forEach( (key, value) -> body.getAsJsonObject("filter").addProperty(key, value) );

		body.addProperty("size", size);

		List<DynamicNode> test = new ArrayList<>();

		// Run the Test // Call API
		test=  happyPath(expected,  randomNdc.getNdc(), fieldSelections, body, size);

		return test;
	}	

	@TestFactory
	@DisplayName("17720: SearchNdc With Special Character Status Check (Search by :Ndc on Special Character )")
	@Order(6)
	public List<DynamicNode> SpecialChars() 
	{
		Headers headers = getGenericHeaders();
		ApiPostStep apiPostStep = new ApiPostStep(headers, NDC_SEARCH_POST_ENDPOINT,
				GENERIC_BODY, null, 400, HTTP_400_BAD_REQUEST);
		apiPostStep.run();
		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("17714: SearchNdc Invalid Auth")
	@Order(7)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException 
	{

		//invalidHeader
		Headers headers = getHeadersInvalidAuth();
		ApiPostStep apiPostStep = new ApiPostStep(headers, NDC_SEARCH_POST_ENDPOINT, GENERIC_BODY,
				new Object[]{}, 401, HTTP_401_UNAUTHORIZED);
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
		Map<String, List<String>> mapListString = new HashMap<String, List<String>>();

		for ( String field : fields.toUpperCase().split(","))
		{
			switch (field) {		
			case "BRANDNAMECODE":
				if ( StringUtils.isNotBlank(randomNdc.getBrandNameCode()) )
					mapListString.put("brandNameCode", Arrays.asList(randomNdc.getBrandNameCode()));
				break;

			case "MARKETINGCATEGORY":
				if ( StringUtils.isNotBlank(randomNdc.getMarketingCategory()) )
					mapListString.put("marketingCategory", Arrays.asList(randomNdc.getMarketingCategory()));
				break;

			case "DOSAGEFORM":
				if ( StringUtils.isNotBlank(randomNdc.getDosageForm()) )
					mapListString.put("dosageForm",Arrays.asList(randomNdc.getDosageForm()));
				break;

			default:
				break;
			}
		}
		return mapListString;
	}

	/**
	 * 
	 * Helper Method for API filter, String
	 * 
	 */
	private Map<String, String>  MapString (Ndc randomNdc, String fields)
	{
		Map<String, String> mapString = new HashMap<String, String>();

		for ( String field : fields.toUpperCase().split(","))
		{
			switch (field) {		
			case "LABELER":
				if ( StringUtils.isNotBlank(randomNdc.getLabeler()) )
					mapString.put("labeler",randomNdc.getLabeler());
				break;

			case "PRODUCTNAME":
				if ( StringUtils.isNotBlank(randomNdc.getProductName()) )
					mapString.put("productName", randomNdc.getProductName());
				break;

			case "RXOTC":
				if ( StringUtils.isNotBlank(randomNdc.getRxOTC()) )
					mapString.put("rxOTC", randomNdc.getRxOTC());
				break;

			case "GPI10GENERICNAME":
				if ( StringUtils.isNotBlank(randomNdc.getGpi10GenericName()) )
					mapString.put("gpi10GenericName", randomNdc.getGpi10GenericName());

			default:
				break;
			}
		}
		return mapString;
	}

	/**
	 * Generic test for happy path
	 * @param expected {@link List<String>}
	 * @param searchTerm {@link JsonObject}
	 * @param size {@link JsonObject}
	 * @return test validation result
	 */
	private  List<DynamicNode> happyPath(List<Ndc> expected,String ndc, List<NdcField> fieldSelections,JsonObject body, int size)
	{ 		

		List<DynamicNode> test = new ArrayList<DynamicNode>();

		logger.debug("Starting API call");
		// Call API
		ApiPostStep apiPostStep = ndcSearch( body,  size);
		NdcSearchResponse searchResult = apiPostStep.convertToJsonDTO(NdcSearchResponse.class);
		if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
		{
			return apiPostStep.getTestResults();
		}
		else {
			test.addAll(apiPostStep.getTestResults());
		}

		// Validate
		test.add(dynamicContainer("API response", validateSearchResult(expected, searchResult, ndc, fieldSelections, body)));

		return test;
	}

	private ApiPostStep ndcSearch(JsonObject requestBody, int size)
	{
		logger.debug("Starting API call");

		Headers headers = getGenericHeaders();

		ApiPostStep apiPostStep = new ApiPostStep(headers, NDC_SEARCH_POST_ENDPOINT, requestBody.toString(), new Object[] {},200, null);
		apiPostStep.run();

		return apiPostStep;
	}

	/**
	 * Validate the search result
	 *
	 * @param searchResult the API response that contains list of providers
	 * @param searchTerm search terms
	 * @return
	 */
	public static List<DynamicNode> validateSearchResult(List<Ndc> expected, NdcSearchResponse searchResult, String searchTerm, List<NdcField> fieldSelections, JsonObject apiBody)

	{
		List<DynamicNode> test = new ArrayList<DynamicNode>();

		test.add(dynamicTest("Api Body :  " + apiBody.toString(), () -> assertTrue(true)));

		List<String> expectedNdcs = new ArrayList<>();
		List<NdcSearch> expectedDrugs = new ArrayList<>();

		//Convert expected (List<Ndc>) to NdcSearchResponse base on fieldSelections
		for ( Ndc ndc : expected )
		{
			JsonObject jsonObj = ndc.convertToNdcSearch(fieldSelections);

			Gson gson= new Gson();
			NdcSearch drug = gson.fromJson(jsonObj.toString(),NdcSearch.class);

			if (expectedDrugs.size() == 0) {

				expectedDrugs.add (drug);
			}
			else  {
				//Compare expectedDrugs and if its one duplicate than don't add to the list
				boolean found =false;
				for ( NdcSearch uniqueDrugs : expectedDrugs ) 
				{
					if(drug.compareDrugs(uniqueDrugs)) 
					{
						found = true;
						break;
					}
				}

				if(!found)
				{

					expectedDrugs.add (drug);
				}

			}
			expectedNdcs.add(ndc.getNdc());
		}

		test.add(dynamicTest("NDCS :  " + Arrays.toString(expectedNdcs.toArray())  , 
				() -> assertThat(searchResult.getApiInfo(),searchResult.getNdcs(), containsInAnyOrder(expectedNdcs.toArray()))));

		test.add(dynamicTest("Validate Field Selection in drugs information",
				() -> {
					assertAll("Drugs Details",
							() -> {
								for (NdcSearch ndcSearch : expectedDrugs)
								{
									assertTrue(ndcSearch.ndcContains(searchResult.getDrugs()), ndcSearch.toString() + " is not found in API repsonse\n" + searchResult.getApiInfo());		
								}
								for (NdcSearch ndcSearch : searchResult.getDrugs())
								{
									assertTrue(ndcSearch.ndcContains(expectedDrugs), ndcSearch.toString() + " not expected\n" + searchResult.getApiInfo());			
								}

							});
				}));

		return test;
	}

}

