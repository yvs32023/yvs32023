/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.user;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.steps.ApiPatchStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.UserQueries;
import com.excellus.sqa.rxcc.dto.Filter;
import com.excellus.sqa.rxcc.dto.SavedSearch;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.User;
import com.excellus.sqa.step.IStep;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * As of 10/05/2022, only ehp has data in TST
 *  
 * PATCH -  https://apim-lbs-rxc-dev-east-001.azure-api.net/api/user/users/me
 * 
 *{
 *   "lastSearch": "__LAST__",
 *    "savedSearches": [
 *        {
 *            "searchName": "__LAST__",
 *            "default": false,
 *           "filters": [
 *                {
 *                    "filterName": "group",
 *                    "tags": [
 *                        "dddd",
 *                        "test Inc"
 *                    ]
 *                },
 *                {
 *                    "filterName": "provider",
 *                    "tags": [
 *                        "01234567890",
 *                        "34569013445"
 *                    ]
 *                }
 *            ]
 *        },
 *        {
 *            "searchName": "test2",
 *            "default": false,
 *            "filters": [
 *                {
 *                    "filterName": "group",
 *                    "tags": [
 *                        "aaa",
 *                        "bbb"
 *                    ]
 *                },
 *                {
 *                    "filterName": "drug",
 *                    "tags": [
 *                        "ccc",
 *                        "ddd"
 *                    ]
 *                }
 *            ]
 *        }
 *    ]
 *}
 *
 * @author Manish Sharma (msharma)
 * @since 09/29/2022
 */
@Tag("ALL")
@Tag("USER")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PatchUser")
public class PatchUserTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(PatchUserTest.class);


	private String subscriptionId;

	static User originalUser;
	static User testData;

	//TODO need to replace api call to directly updating the cosmos db when restoring original data
	@AfterEach
	public void resetOriginalUser() throws JsonProcessingException
	{
		if ( originalUser != null )
		{
			String apiBody = createApiBody(originalUser.getLastSearch(), originalUser.getSavedSearches());


			patchUserAPI(subscriptionId, apiBody.toString());
		}

		originalUser = null;
		testData = null;
	}

	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("26274: PatchUser Happy Path(ehp member correspondence)")
	@Order(1)
	public List<DynamicNode> happyPathEhpPatch() throws JsonProcessingException
	{
		subscriptionId = Tenant.Type.EHP.getSubscriptionName();

		// Create test data
		createTestData();

		String lastSearch = "__TEST__";

		//setup test patch data for body
		SavedSearch saveSearch1 = new SavedSearch("__TEST__", true);
		saveSearch1.addFilter(new Filter("group", Arrays.asList("test","test a")));
		saveSearch1.addFilter(new Filter("provider", Arrays.asList("01234567890","34569013445")));

		SavedSearch saveSearch2 = new SavedSearch("test2", false);
		saveSearch2.addFilter(new Filter("group", Arrays.asList("test b","test c")));
		saveSearch2.addFilter(new Filter("drug", Arrays.asList("test d","test e")));

		List<SavedSearch> savedSearches = Arrays.asList(saveSearch1, saveSearch2);

		testData.setLastSearch( lastSearch );
		testData.setSavedSearches( savedSearches );

		String body = createApiBody(testData.getLastSearch(),testData.getSavedSearches());

		// call happy path testing
		return happyPathPatch(body);
	}

	@TestFactory
	@DisplayName("26288: PatchUser With Body(ehp member correspondence)") 
	@Order(2)
	public List<DynamicNode> happyPathWithBodyEhpPatch() throws JsonProcessingException
	{
		subscriptionId = Tenant.Type.EHP.getSubscriptionName();

		// Create test data
		createTestData();

		String lastSearch = "__TEST__";

		//setup test patch data for body
		SavedSearch saveSearch1 = new SavedSearch("__TEST__", true);
		saveSearch1.addFilter(new Filter("group", Arrays.asList("test","test a")));
		saveSearch1.addFilter(new Filter("provider", Arrays.asList("01234567890","34569013445")));

		List<SavedSearch> savedSearches = Arrays.asList(saveSearch1);

		testData.setLastSearch( lastSearch );
		testData.setSavedSearches( savedSearches );

		String body = createApiBody(lastSearch,savedSearches);

		// call happy path testing
		return happyPathPatch(body);
	}


	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("26284: PatchUser Invalid Header")
	@Order(3)
	public List<DynamicNode> invalidHeader() throws JsonProcessingException 
	{
		subscriptionId = Tenant.Type.EHP.getSubscriptionName();

		// Create test data
		createTestData();

		String lastSearch = "__TEST__";

		//setup test patch data for body
		SavedSearch saveSearch1 = new SavedSearch("__TEST__", true);
		saveSearch1.addFilter(new Filter("group", Arrays.asList("test","test a")));
		saveSearch1.addFilter(new Filter("provider", Arrays.asList("01234567890","34569013445")));

		List<SavedSearch> savedSearches = Arrays.asList(saveSearch1);

		testData.setLastSearch( lastSearch );
		testData.setSavedSearches( savedSearches );

		String body = createApiBody(testData.getLastSearch(),testData.getSavedSearches());

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME + "invalid", subscriptionId));

		ApiPatchStep apiPatchStep = new ApiPatchStep(headers, USER_PATCH_ENDPOINT,
				body.toString(), new Object[]{}, 500, HTTP_500_INTERNAL_SERVER_ERR);

		apiPatchStep.run();

		return apiPatchStep.getTestResults();
	}

	@TestFactory
	@DisplayName("26285: PatchUser Header Missing")
	@Order(4)
	public List<DynamicNode> missingRequiredHeader()  throws JsonProcessingException 
	{
		subscriptionId = Tenant.Type.EHP.getSubscriptionName();

		// Create test data
		createTestData();

		String lastSearch = "__TEST__";

		//setup test patch data for body
		SavedSearch saveSearch1 = new SavedSearch("__TEST__", true);
		saveSearch1.addFilter(new Filter("group", Arrays.asList("test","test a")));
		saveSearch1.addFilter(new Filter("provider", Arrays.asList("01234567890","34569013445")));

		List<SavedSearch> savedSearches = Arrays.asList(saveSearch1);

		testData.setLastSearch( lastSearch );
		testData.setSavedSearches( savedSearches );

		String body = createApiBody(testData.getLastSearch(),testData.getSavedSearches());
		Headers headers = getGenericHeaders();

		ApiPatchStep apiPatchStep = new ApiPatchStep(headers, USER_PATCH_ENDPOINT,
				body.toString(), new Object[]{}, 500, HTTP_500_INTERNAL_SERVER_ERR);

		apiPatchStep.run();

		return apiPatchStep.getTestResults();
	}

	@TestFactory
	@DisplayName("26283:PatchUser Header Null")
	@Order(5)
	public List<DynamicNode> invalidHeaderValue()  throws JsonProcessingException 
	{
		subscriptionId = "";	// invalid header value

		// Create test data
		createTestData();

		String lastSearch = "__TEST__";

		//setup test patch data for body
		SavedSearch saveSearch1 = new SavedSearch("__TEST__", true);
		saveSearch1.addFilter(new Filter("group", Arrays.asList("test","test a")));
		saveSearch1.addFilter(new Filter("provider", Arrays.asList("01234567890","34569013445")));

		List<SavedSearch> savedSearches = Arrays.asList(saveSearch1);

		testData.setLastSearch( lastSearch );
		testData.setSavedSearches( savedSearches );

		String body = createApiBody(testData.getLastSearch(),testData.getSavedSearches());

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

		ApiPatchStep apiPatchStep = new ApiPatchStep(headers, USER_PATCH_ENDPOINT,
				body.toString(), new Object[]{}, 500, HTTP_500_INTERNAL_SERVER_ERR);

		apiPatchStep.run();

		return apiPatchStep.getTestResults();
	}


	@TestFactory
	@DisplayName("26291: PatchUser Invalid Auth")
	@Order(6)
	public List<DynamicNode>  invalidToken() throws JsonProcessingException 
	{
		subscriptionId = Tenant.Type.EHP.getSubscriptionName();
		// Create test data
		createTestData();

		String lastSearch = "__TEST__";

		//setup test patch data for body
		SavedSearch saveSearch1 = new SavedSearch("__TEST__", true);
		saveSearch1.addFilter(new Filter("group", Arrays.asList("test","test a")));
		saveSearch1.addFilter(new Filter("provider", Arrays.asList("01234567890","34569013445")));

		List<SavedSearch> savedSearches = Arrays.asList(saveSearch1);

		testData.setLastSearch( lastSearch );
		testData.setSavedSearches( savedSearches );

		String body = createApiBody(testData.getLastSearch(),testData.getSavedSearches());

		Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionId));

		ApiPatchStep apiPatchStep = new ApiPatchStep(headers, USER_PATCH_ENDPOINT,
				body.toString(), new Object[]{}, 401, HTTP_401_UNAUTHORIZED);

		apiPatchStep.run();

		return apiPatchStep.getTestResults();
	}


	@Test
	@DisplayName("TBD: PatchUser Invalid Body")
	@Order(7)
	public  List<DynamicNode>  invalidBody() throws JsonProcessingException 
	{
		subscriptionId = Tenant.Type.EHP.getSubscriptionName();

		// Create test data
		createTestData();

		String body = "{\r\n"
				+ "    \"lastLogin\": \"No\",\r\n"
				+ "    \"savedSearches\": \"Yes\",\r\n"
				+ "}";;
				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiPatchStep apiPatchStep = new ApiPatchStep(headers, USER_PATCH_ENDPOINT,
						body.toString(), new Object[]{}, 500, HTTP_500_INTERNAL_SERVER_ERR);

				apiPatchStep.run();

				return apiPatchStep.getTestResults();
	}

	/*
	 * Helper methods
	 */

	/**
	 * Perform the actual test
	 * 
	 * @param body
	 */
	private List<DynamicNode> happyPathPatch(String body)
	{
		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

		// Call API
		ApiPatchStep apiPatchStep = new ApiPatchStep(headers, USER_PATCH_ENDPOINT, body, new Object[] {},200, null);
		apiPatchStep.run(); // perform the actual API call

		if ( apiPatchStep.stepStatus() != IStep.Status.COMPLETED )
		{
			return apiPatchStep.getTestResults();    // Do not continue if the step did not complete
		}

		/*
		 * Validation
		 */
		// Get the user from the cosmos db
		User actual = UserQueries.getUser(subscriptionId, testData.getId(), testData.getOid());

		// Validate the Cosmos data after the API patch
		apiPatchStep.getTestResults().add(dynamicContainer("Cosmos db", testData.compare( actual )));

		return apiPatchStep.getTestResults();
	}

	/**
	 * Create API body
	 * 
	 * @param lastSearch lastSearch
	 * @param savedSearches List<SavedSearch>
	 * 
	 * 
	 * @return
	 * @throws JsonProcessingException 
	 */
	//private String createApiBody(User user) throws JsonProcessingException
	private String createApiBody(String lastSearch, List<SavedSearch> savedSearches) throws JsonProcessingException
	{
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if ( lastSearch != null )
			map.put("lastSearch", lastSearch);
		if ( savedSearches != null )
			map.put("savedSearches", savedSearches);

		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			logger.error("Unable to covert JSON object to string", e);
			return "Unable to covert JSON object to string --> " + e.getMessage();
		}
	}


	/**
	 * Create a test data to use
	 * @throws IllegalArgumentException 
	 * @throws JsonProcessingException 
	 */
	private void createTestData() throws JsonProcessingException, IllegalArgumentException
	{
		//Cosmos DB
		originalUser= UserQueries.getEHPUser(RxConciergeUILogin.getAcctName());

		// Create a deep copy and update id
		testData = originalUser.deepCopy(User.class);
		//testData.setLastUpdatedBy(RxConciergeUILogin.getAcctName());   //need to look into in future, properties missing in both api response and cosmos db
 		
		try {
			testData.setLastUpdated(DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE));
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}
	}

	/**
	 * Call API for patching (updating) user
	 * 
	 * @param subscriptionId to be patched by API
	 * @param body that contains detailed of what will be updated
	 */
	private ApiPatchStep patchUserAPI( String subscriptionId,String body) {
		logger.debug("Starting API call");

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

		ApiPatchStep apiPatchStep = new ApiPatchStep(headers, USER_PATCH_ENDPOINT, body, new Object[] {},201, null);
		apiPatchStep.run();

		logger.debug("API user patch completed");
		return apiPatchStep;

	}

}
