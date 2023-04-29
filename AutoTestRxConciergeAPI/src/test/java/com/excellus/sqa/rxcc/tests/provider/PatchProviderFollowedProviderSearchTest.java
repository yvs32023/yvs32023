/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;

import com.excellus.sqa.restapi.steps.ApiPatchStep;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.restapi.steps.IApiStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.ProviderQueries;
import com.excellus.sqa.rxcc.dto.Provider;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.utilities.DateTimeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 05/08/2022
 */
@Tag("ALL")
@Tag("PROVIDER")
@Tag("SEARCH")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PatchProviderFollowedPostProviderSearch")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class PatchProviderFollowedProviderSearchTest  extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(PatchProviderFollowedProviderSearchTest.class);

	static Provider originalProvider;
	static Provider testData;

	//Patch Provider Deep Copy
	@AfterEach
	public void resetOriginalProvider()
	{
		if ( originalProvider != null )
		{
			JsonObject apiBody = createApiBody(originalProvider.getFirstName(), originalProvider.getLastName(), 
					originalProvider.getStatusInd(), originalProvider.getTaxonomyCode(), originalProvider.getTaxonomyDescr(), originalProvider.getCredential());

			ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_PATCH_ENDPOINT, apiBody.toString(), new Object[] {originalProvider.getId()}, null, null);
			apiPostStep.run();
		}

		originalProvider = null;
		testData = null;
	}

	@TestFactory
	@DisplayName("7438: PatchProvider-V2 Happy Path (ALL Fields) followed by Provider Search(With filters on lastName, taxonomyCode and taxonomy descr)  ")
	@Order(1)
	public List<DynamicNode> patchProviderFollowedByProviderSearch() throws Exception
	{
		List<DynamicNode> result = new ArrayList<>();

		Map<String, String> patch = new HashMap<>();
		patch.put("firstname", "XXTest");
		patch.put("lastname", "XTestAutomation");
		patch.put("taxonomycode", "914055000X");
		patch.put("taxonomydescr", "Manish");
		patch.put("statusind", "Active");
		patch.put("credential", "Nurse");

		result.add(dynamicContainer("PATCH: update the provider",  happyPathPatchName(patch)) );

		SeleniumPageHelperAndWaiter.pause(20000);	// Allow the Cognisant search indexer to performed

		logger.debug("Validate SearchResult response: " + result);

		//  Use Provider.firstname as query
		JsonObject body = new JsonObject();
		body.addProperty("query", testData.getFirstName().toUpperCase());

		//Use filter fields (upper case) in Provider.lastname 
		JsonArray lastName = new JsonArray();
		lastName.add(testData.getLastName());

		//Use filter fields (upper case) in Provider.taxonomycode
		JsonArray taxonomyCode = new JsonArray();
		taxonomyCode.add(testData.getTaxonomyCode());

		//Use filter fields (upper case) in Provider.taxonomycode
		JsonArray taxonomyDescr = new JsonArray();
		taxonomyDescr.add(testData.getTaxonomyDescr());

		JsonObject filter = new JsonObject();
		filter.add("lastName", lastName);
		filter.add("taxonomyCode", taxonomyCode);
		filter.add("taxonomyDescr", taxonomyDescr);

		body.add("filter", filter);

		// Call API
		IApiStep apiStep = SearchProviderTest.providerSearch(body);

		// Validate
		result.add( dynamicContainer("SearchProviderResponse validation", SearchProviderTest.validateSearchResult(apiStep, testData.getFirstName())));

		return result;
	}

	@TestFactory
	@DisplayName("7438: PatchProvider-V2 Happy Path (First Name Only) followed by Provider Search")
	@Order(2)
	public List<DynamicNode> happyPathPatchNameOnlyfirstName() throws Exception
	{
		Map<String, String> patch = new HashMap<>();
		patch.put("firstname", "TEST");

		List<DynamicNode> result = new ArrayList<>();
		result.add(dynamicContainer("PATCH: update the provider",  happyPathPatchName(patch)) );

		logger.debug("Patch response: " + result);

		SeleniumPageHelperAndWaiter.pause(20000);	// Allow the Cognisant search indexer to performed

		String searchTerm = testData.getFirstName();	

		// Call API
		IApiStep apiStep = SearchProviderTest.providerSearch(searchTerm, 200);

		// Validate
		result.add( dynamicContainer("SearchProviderResponse validation", SearchProviderTest.validateSearchResult(apiStep, testData.getFirstName())));

		return result;
	}

	@TestFactory
	@DisplayName("7438: PatchProvider-V2 Happy Path (First Name & Last Name) followed by ProviderSearch(Query:FIRST NAME adding lastname as sort Size:200)")
	@Order(3)
	public List<DynamicNode> patchFollowedSearchWithStatusIndFilter() throws Exception
	{
		Map<String, String> patch = new HashMap<>();
		patch.put("firstname", "IIITTRXXSNSDHSH");
		patch.put("lastname", "EXAMPLE");
		patch.put("statusind", "Active");

		List<DynamicNode> result = new ArrayList<>();
		result.add(dynamicContainer("PATCH: update the provider",  happyPathPatchName(patch)) );

		//result.addAll(happyPathPatch(body.toString()));

		logger.debug("Patch response: " + result);

		SeleniumPageHelperAndWaiter.pause(20000);	// Allow the Cognisant search indexer to performed

		//Sort with descending order in last field after querying firstName from  random testData provider 	
		JsonObject searchBody = new JsonObject();
		searchBody.addProperty("query", testData.getFirstName().toLowerCase());

		//Use filter fields (upper case) in Provider.lastname 
		JsonArray statusInd = new JsonArray();
		statusInd.add(testData.getStatusInd());

		JsonObject filter = new JsonObject();
		filter.add("statusInd", statusInd);

		searchBody.add("filter", filter);

		// Call API
		IApiStep apiStep = SearchProviderTest.providerSearch(searchBody);

		// Validate
		result.add( dynamicContainer("SearchProviderResponse validation", SearchProviderTest.validateSearchResult(apiStep, testData.getFirstName())));

		return result;
	}

	@TestFactory
	@DisplayName("7438: PatchProvider-V2 Happy Path (First Name, Taxonomy code, Taxonomy Descr) followed by ProviderSearch ")
	@Order(4)
	public List<DynamicNode> patchLastNameFollowedSearchWithStatusIndFilter() throws Exception
	{
		Map<String, String> patch = new HashMap<>();
		patch.put("lastname", "ZESTRTESTUSER");
		patch.put("taxonomycode", "36346F0000X");
		patch.put("taxonomydescr", "Family Medicine");

		List<DynamicNode> result = new ArrayList<>();
		result.add(dynamicContainer("Patch Provider", happyPathPatchName(patch)));

		logger.debug("Patch response: " + result);

		SeleniumPageHelperAndWaiter.pause(20000);	// Allow the Cognisant search indexer to performed

		//Sort with descending order in last field after querying LastName from random testData provider 		
		JsonObject searchBody = new JsonObject();
		searchBody.addProperty("query", testData.getLastName().toUpperCase());

		//Use filter fields (upper case) in Provider.taxonomy code 
		JsonArray taxonomycode = new JsonArray();
		taxonomycode.add(testData.getTaxonomyCode());
		
		//Use filter fields (upper case) in Provider.taxonomy descr 
		JsonArray taxonomydescr = new JsonArray();
		taxonomydescr.add(testData.getTaxonomyDescr());

		JsonObject filter = new JsonObject();
		filter.add("taxonomyCode", taxonomycode);
		filter.add("taxonomyDescr", taxonomydescr);

		searchBody.add("filter", filter);

		// Call API
		IApiStep apiStep = SearchProviderTest.providerSearch(searchBody);

		// Validate
		result.add( dynamicContainer("SearchProviderResponse validation",
				SearchProviderTest.validateSearchResult(apiStep, testData.getLastName())));

		return result;
	}

	/*
	 * Helper methods
	 */

	//patch helper method
	private List<DynamicNode> happyPathPatchName(Map<String, String> patch) throws Exception
	{
		// Create test data
		createTestData();

		String firstname = null;
		String lastname = null;
		String statusInd = null;
		String taxonomyCode = null;
		String taxonomyDescr = null;
		String credential = null;

		// Setup test data
		if ( patch.containsKey("firstname") ) {
			testData.setFirstName(patch.get("firstname"));
			firstname = testData.getFirstName();
		}

		if ( patch.containsKey("lastname") ) {
			testData.setLastName(patch.get("lastname"));
			lastname = testData.getLastName();
		}

		if ( patch.containsKey("statusind") ) {
			testData.setStatusInd(patch.get("statusind"));
			statusInd = testData.getStatusInd();
		}
		if ( patch.containsKey("taxonomycode") ) {
			testData.setTaxonomyCode(patch.get("taxonomycode"));
			taxonomyCode = testData.getTaxonomyCode();
		}

		if ( patch.containsKey("taxonomydescr") ) {
			testData.setTaxonomyDescr(patch.get("taxonomydescr"));
			taxonomyDescr = testData.getTaxonomyDescr();
		}

		if ( patch.containsKey("credential") ) {
			testData.setCredential(patch.get("credential"));
			credential = testData.getCredential();
		}

		// Create API body
		JsonObject body = createApiBody(firstname, lastname, statusInd, taxonomyCode,  taxonomyDescr, credential);

		return happyPathPatch(body.toString());
	}

	/**
	 * Submit API request and validate the results
	 * 
	 * @param apiBody request body to be sent with the API request
	 * @return list of {@link DynamicNode} that will be tested
	 */
	private List<DynamicNode> happyPathPatch(String apiBody)
	{
		// Call API
		ApiPatchStep apiPatchStep = new ApiPatchStep(getGenericHeaders(),
											PROVIDER_PATCH_ENDPOINT,
											apiBody,
											new Object[]{testData.getId()},
											200,
											HTTP_200_OK);
		apiPatchStep.run();

		List<DynamicNode> testResults = new ArrayList<>(apiPatchStep.getTestResults());

		logger.info("Patch response (" + PROVIDER_PATCH_ENDPOINT + "): " + apiPatchStep.getResponse().asString());

		if ( apiPatchStep.getResponseStatusCode() != 200 )
			return testResults;

		// Get the provider from the Cosmos
		Provider actual = ProviderQueries.getProviderById(testData.getId());

		// Validate the Cosmos data after the API patch
		testResults.add(dynamicContainer("Validate API Patch (cosmos vs api response)", testData.compare( actual )));

		return testResults;
	}

	/**
	 * Create API body
	 * 
	 *  @param firstName to be updated
	 *  @param lastName to be updated
	 *  @param statusInd to be updated
	 *  @param taxonomyCode to be updated
	 *  @param taxonomyDescr to be updated
	 *  @param credential to be updated
	 * @return JsonObject that represent the params
	 */
	private JsonObject createApiBody(String firstName, String lastName, String statusInd, String taxonomyCode, String taxonomyDescr, String credential)
	{
		JsonObject requestBody = new JsonObject();

		if ( StringUtils.isNotBlank(firstName) )
			requestBody.addProperty("firstName", firstName);

		if ( StringUtils.isNotBlank(lastName) )
			requestBody.addProperty("lastName", lastName);

		if ( StringUtils.isNotBlank(statusInd) )
			requestBody.addProperty("statusInd", statusInd);

		if ( StringUtils.isNotBlank(taxonomyCode) )
			requestBody.addProperty("taxonomyCode", taxonomyCode);

		if ( StringUtils.isNotBlank(taxonomyDescr) )
			requestBody.addProperty("taxonomyDescr", taxonomyDescr);

		if ( StringUtils.isNotBlank(credential) )
			requestBody.addProperty("credential", credential);

		return requestBody;
	}

	/**
	 * Create a test data to use
	 * @throws JsonProcessingException is exception occurs
	 */
	private void createTestData() throws JsonProcessingException
	{
		// Get random provider
		originalProvider = ProviderQueries.getRandomProvider();

		// Create a deep copy and update id
		testData = originalProvider.deepCopy(Provider.class);
		testData.setLastUpdatedBy(RxConciergeUILogin.getAcctName());
		
 		try {
 			testData.setLastUpdated(DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE));
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}
	}
}
