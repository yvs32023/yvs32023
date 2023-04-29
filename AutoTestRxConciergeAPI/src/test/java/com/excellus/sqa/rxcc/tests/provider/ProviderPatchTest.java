/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;

import com.excellus.sqa.restapi.steps.ApiPatchStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.ProviderQueries;
import com.excellus.sqa.rxcc.dto.Provider;
import com.excellus.sqa.utilities.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * <a href="https://apim-lbs-rxc-dev-east-001.developer.azure-api.net/api-details#api=provider&operation=61f006c023f02288a55dc9d1">Provider - Patch (update)</a>
 *
 * <pre>
 *   {
 *   	"firstName": "string",
 *   	"lastName": "string",
 *   	"statusInd": "string",
 *   	"taxonomyCode": "string",
 *   	"taxonomyDescr": "string",
 *   	"credential": "string",
 *   }
 * </pre>
 *
 * <p><b>Request</b><br/>
 * PATCH    https://apim-lbs-rxc-tst-east-001.azure-api.net/api/provider/providers/{providerId}
 *
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/30/2022
 */
@Tag("ALL")
@Tag("PROVIDER")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PatchProvider")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class ProviderPatchTest extends RxConciergeAPITestBaseV2 
{
	private static final Logger logger = LoggerFactory.getLogger(ProviderPatchTest.class);

	static Provider originalProvider;
	static Provider testData;

	@AfterEach
	public void resetOriginalProvider()
	{
		if ( originalProvider != null )
		{
			JsonObject apiBody = createApiBody(originalProvider.getFirstName(), originalProvider.getLastName(), 
					originalProvider.getStatusInd(), originalProvider.getTaxonomyCode(), originalProvider.getTaxonomyDescr(), originalProvider.getCredential());

			ApiPatchStep apiPatchStep = new ApiPatchStep(getGenericHeaders(), PROVIDER_PATCH_ENDPOINT, apiBody.toString(), new Object[]{originalProvider.getId()}, 200, HTTP_200_OK);
			apiPatchStep.run();
		}

		originalProvider = null;
		testData = null;
	}

	@TestFactory
	@DisplayName("5532: PatchProvider-V2 Happy Path (first and last name)")
	@Order(1)
	public List<DynamicNode> happyPathPatchName() throws Exception
	{
		// Create test data
		createTestData();

		// Setup test data
		testData.setFirstName(testData.getFirstName() + " - Test Automation");
		testData.setLastName(testData.getLastName() + " - Test Automation");

		// Create API body
		JsonObject body = createApiBody(testData.getFirstName(), testData.getLastName(), testData.getStatusInd(), testData.getTaxonomyCode(), testData.getTaxonomyDescr(), testData.getCredential());

		return happyPathPatch(body.toString());
	}

	@TestFactory
	@DisplayName("5532: PatchProvider-V2 Happy Path (status)")
	@Order(2)
	public List<DynamicNode> happyPathPatchStatus() throws Exception
	{
		// Create test data
		createTestData();

		// Setup test data
		testData.setStatusInd(testData.getStatusInd() + " - Test Automation");

		// Create API body
		JsonObject body = createApiBody(null, null, testData.getStatusInd(), null, null, null);

		return happyPathPatch(body.toString());
	}

	@TestFactory
	@DisplayName("5532: PatchProvider-V2 Happy Path (taxonomy)")
	@Order(3)
	public List<DynamicNode> happyPathPatchTaxonomy() throws Exception
	{
		// Create test data
		createTestData();

		// Setup test data
		testData.setTaxonomyCode(testData.getTaxonomyCode() + " - Test Automation");
		testData.setTaxonomyDescr(testData.getTaxonomyDescr() + " - Test Automation");


		// Create API body
		JsonObject body = createApiBody(null, null, null, testData.getTaxonomyCode(), testData.getTaxonomyDescr(), null);

		return happyPathPatch(body.toString());
	}

	@TestFactory
	@DisplayName("5532: PatchProvider-V2 Happy Path (credential)")
	@Order(4)
	public List<DynamicNode> happyPathPatchCredential() throws Exception
	{
		// Create test data
		createTestData();

		// Setup test data
		testData.setCredential( StringUtils.isNotBlank(testData.getCredential())
				? testData.getCredential() + " - Test Automation" : "Test Automation");

		// Create API body
		JsonObject body = createApiBody(null, null, null, testData.getTaxonomyCode(), testData.getTaxonomyDescr(), testData.getCredential());

		return happyPathPatch(body.toString());
	}

	@TestFactory
	@DisplayName("5538: PatchProvider-V2 Parm String with Spl Char (special characters)")
	@Order(4)
	public List<DynamicNode> happyPathPatchSpecialChars() throws Exception
	{
		// Create test data
		createTestData();

		// Setup test data
		testData.setFirstName(testData.getFirstName() + " - Garçon Test Automation");
		testData.setLastName(testData.getLastName() + " - !@#$%^&* Test Automation");
		testData.setStatusInd(testData.getStatusInd() + " - Déjà Test Automation");
		testData.setTaxonomyCode(testData.getTaxonomyCode() + " - Maïs Test Automation");
		testData.setTaxonomyDescr(testData.getTaxonomyDescr() + " - Sûr Test Automation");
		testData.setCredential( StringUtils.isNotBlank(testData.getCredential())
				? testData.getCredential() + " - Hôpital Test Automation" : "Hôpital Test Automation");

		// Create API body
		JsonObject body = createApiBody(testData.getFirstName(), testData.getLastName(), testData.getStatusInd(), 
				testData.getTaxonomyCode(), testData.getTaxonomyDescr(), testData.getCredential());

		return happyPathPatch(body.toString());
	}

	/*
	 * Negative test cases
	 */

	@TestFactory
	@DisplayName("5534: PatchProvider-V2 Invalid Parm")
	@Order(5)
	public List<DynamicNode> invalidParam()
	{
		String providerId = "9876543210";

		// Create API body
		JsonObject body = createApiBody("firstName", "lastName", "Active", "Taxonomy code", "Taxonomy description", "Credential");

		ApiPatchStep apiPatchStep = new ApiPatchStep(getGenericHeaders(), PROVIDER_PATCH_ENDPOINT, body.toString(), new Object[]{providerId}, 404, HTTP_404_NOT_FOUND);
		apiPatchStep.run();

		// validate the API message
		final String EXPECTED_MSG = "Entity with the specified id does not exist in the system.";
		String actualMsg = apiPatchStep.getResponse().then().extract().asString();
		apiPatchStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

		return apiPatchStep.getTestResults();
	}

	@TestFactory
	@DisplayName("5542: PatchProvider-V2 Invalid Auth")
	@Order(6)
	public List<DynamicNode>  invalidToken()
	{
		Provider provider = ProviderQueries.getRandomProvider();

		// Create API body
		JsonObject body = createApiBody("firstName", "lastName", "Active", "Taxonomy code", "Taxonomy description", "Credential");

		ApiPatchStep apiPatchStep = new ApiPatchStep(getHeadersInvalidAuth(), PROVIDER_PATCH_ENDPOINT, body.toString(), new Object[]{provider.getId()}, 401, HTTP_401_UNAUTHORIZED);
		apiPatchStep.run();

		// validate the API json response "message"
		String actualMsg = apiPatchStep.getResponse().then().extract().jsonPath().get("message");
		apiPatchStep.getTestResults().add(dynamicTest("API response - message [" + UNAUTHORIZED_MSG + "]", () -> assertEquals(UNAUTHORIZED_MSG, actualMsg)));

		return apiPatchStep.getTestResults();

	}

	@TestFactory
	@DisplayName("5539: PatchProvider-V2 With Body (invalid field in the body)")
	@Order(7)
	public List<DynamicNode> invalidBodyField()
	{
		Provider provider = ProviderQueries.getRandomProvider();

		// Create API body
		String apiBody = "{ \"invalidField\": \"Invalid field\" }";

		ApiPatchStep apiPatchStep = new ApiPatchStep(getGenericHeaders(), PROVIDER_PATCH_ENDPOINT, apiBody, new Object[]{provider.getId()}, 400, HTTP_400_BAD_REQUEST);
		apiPatchStep.run();

		// validate the API message
		final String EXPECTED_MSG = "\"No patchable field was found in request body!\"";
		String actualMsg = apiPatchStep.getResponse().then().extract().asString();
		apiPatchStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertEquals(EXPECTED_MSG, actualMsg)));

		return apiPatchStep.getTestResults();
	}
	
	/*
	 * Helper methods
	 */

	/**
	 * Submit API request and validate the results
	 * 
	 * @param apiBody API request body
	 * @return list of {@link DynamicNode} of test
	 */
	private List<DynamicNode> happyPathPatch(String apiBody)
	{
		// Call API
		ApiPatchStep apiPatchStep = new ApiPatchStep(getGenericHeaders(), PROVIDER_PATCH_ENDPOINT, apiBody, new Object[]{testData.getId()}, 200, HTTP_200_OK);
		apiPatchStep.run();

		if (apiPatchStep.getResponseStatusCode() != 200)
		{
			return apiPatchStep.getTestResults();
		}

		// Get the member from the Cosmos
		Provider actual = ProviderQueries.getProviderById(testData.getId());

		// Validate the Cosmos data after the API patch
		apiPatchStep.getTestResults().add(dynamicContainer("Cosmos db", testData.compare( actual )));

		return apiPatchStep.getTestResults();
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
	 * @throws JsonProcessingException if exception occurs
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
