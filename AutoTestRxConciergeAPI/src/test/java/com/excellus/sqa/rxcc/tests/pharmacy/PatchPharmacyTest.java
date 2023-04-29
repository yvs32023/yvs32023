/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.pharmacy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.steps.ApiPatchStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.PharmacyQueries;
import com.excellus.sqa.rxcc.dto.Pharmacy;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;

/**
 * 
 *  <a href="https://apim-lbs-rxc-dev-east-001.developer.azure-api.net/api-details#api=pharmacy&operation=patchpharmacy">Pharmacy - Patch (update)</a>
 * 
 * <pre>
 *   {
 *   	
 *   	"taxonomyCode": "string",
 *   	"taxonomyDescr": "string",
 *   	"statusInd": "string"
 *   
 *   }
 * </pre>
 * 
 * <p><b>Request</b><br/>
 * PATCH    https://apim-lbs-rxc-dev-east-001.azure-api.net/api/pharmacy/pharmacies/{providerId}
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 04/26/2022
 */
@Tag("ALL")
@Tag("PHARMACY")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PatchPharmacy")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class PatchPharmacyTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(PatchPharmacyTest.class);

	static Pharmacy originalPharmacy;
	static Pharmacy testData;

	@AfterEach
	public void resetOriginalPharmacy()
	{
		if ( originalPharmacy != null )
		{
			JsonObject apiBody = createApiBody( originalPharmacy.getTaxonomyCode(), originalPharmacy.getTaxonomyDescr(), originalPharmacy.getStatusInd());

			ApiPatchStep apiPatchStep = new ApiPatchStep(getGenericHeaders(),
												PHARMACY_PATCH_ENDPOINT,
												apiBody.toString(),
												new Object[] {originalPharmacy.getId()},
												200,
												HTTP_200_OK);
			apiPatchStep.run();
		}

		originalPharmacy = null;
		testData = null;
	}

	@TestFactory
	@DisplayName("5898: PatchPharmacy Happy Path (TaxonomyCode)")
	@Order(1)
	public List<DynamicNode> happyPathPatchNameTaxonomyCode() throws Exception
	{
		// Create test data
		createTestData();

		// Setup test data
		testData.setTaxonomyCode(testData.getTaxonomyCode() + " - Test Automation");

		// Create API body
		JsonObject body = createApiBody(testData.getTaxonomyCode(), testData.getTaxonomyDescr(),testData.getStatusInd());

		return happyPathPatch(body.toString());
	}

	@TestFactory
	@DisplayName("5898: PatchPharmacy Happy Path (TaxonomyCode and TaxonomyDescr)")
	@Order(2)
	public List<DynamicNode> happyPathPatchNameTaxonomyCodeAndDescr() throws Exception
	{
		// Create test data
		createTestData();

		// Setup test data
		testData.setTaxonomyCode(testData.getTaxonomyCode() + " - Test Automation");
		testData.setTaxonomyDescr(testData.getTaxonomyDescr() + " - Test Automation");

		// Create API body
		JsonObject body = createApiBody(testData.getTaxonomyCode(), testData.getTaxonomyDescr(),testData.getStatusInd());

		return happyPathPatch(body.toString());
	}

	@TestFactory
	@DisplayName("5898: PatchPharmacy Happy Path (TaxonomyCode, TaxonomyDescr and StatusInd)")
	@Order(3)
	public List<DynamicNode> happyPathPatchName() throws Exception
	{
		// Create test data
		createTestData();

		// Setup test data
		testData.setTaxonomyCode(testData.getTaxonomyCode() + " - Test Automation");
		testData.setTaxonomyDescr(testData.getTaxonomyDescr() + " - Test Automation");
		testData.setStatusInd(testData.getStatusInd() + " - Test Automation");

		// Create API body
		JsonObject body = createApiBody(testData.getTaxonomyCode(), testData.getTaxonomyDescr(),testData.getStatusInd());

		return happyPathPatch(body.toString());
	}

	/*
	 * Negative test cases
	 */
	
	@TestFactory
	@DisplayName("5900: PatchPharmacy Invalid Parm")
	@Order(4)
	public List<DynamicNode> invalidParam()
	{
		String providerId = "9876543210";
		
		// Create API body
		JsonObject body = createApiBody("Taxonomy code", "Taxonomy description", "StatusInd");

		ApiPatchStep apiPatchStep = new ApiPatchStep(getGenericHeaders(),
											PHARMACY_PATCH_ENDPOINT,
											body.toString(),
											new Object[] {providerId},
											404,
											HTTP_404_NOT_FOUND);
		apiPatchStep.run();

		// GC (04/06/23) commented this since the expected message is empty string
		// validate the API message
		//final String EXPECTED_MSG = "Entity with the specified id does not exist in the system.";
//		final String EXPECTED_MSG = "";
//		String actualMsg = response.then().extract().asString();
//		testResults.add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

		return apiPatchStep.getTestResults();
	}
	
	@TestFactory
	@DisplayName("5908: PatchPharmacy Parm String with Spl Char (special characters)")
	@Order(5)
	public List<DynamicNode> happyPathPatchSpecialChars() throws Exception
	{
		// Create test data
		createTestData();

		// Setup test data
		testData.setTaxonomyCode(testData.getTaxonomyCode() + " - Maïs Test Automation");
		testData.setTaxonomyDescr(testData.getTaxonomyDescr() + " - Sûr Test Automation");
		testData.setStatusInd(testData.getStatusInd() + " - Déjà Test Automation");

		// Create API body
		JsonObject body = createApiBody(testData.getTaxonomyCode(), testData.getTaxonomyDescr(),testData.getStatusInd());

		return happyPathPatch(body.toString());
	}
	
	@TestFactory
	@DisplayName("5911: PatchPharmacy Invalid Auth")
	@Order(6)
	public List<DynamicNode>  invalidToken()
	{
		Pharmacy pharmacy = PharmacyQueries.getRandomPharmacy();
		
		// Create API body
		JsonObject body = createApiBody("Taxonomy code", "Taxonomy description", "StatusInd");

		ApiPatchStep apiPatchStep = new ApiPatchStep(getHeadersInvalidAuth(),
										PHARMACY_PATCH_ENDPOINT,
										body.toString(),
										new Object[] {pharmacy.getId()},
										401,
										HTTP_401_UNAUTHORIZED);
		apiPatchStep.run();

		// validate the API json response "message"
		String actualMsg = apiPatchStep.getResponse().then().extract().jsonPath().get("message");
		apiPatchStep.getTestResults().add(dynamicTest("API response - message [" + UNAUTHORIZED_MSG + "]", () -> assertEquals(UNAUTHORIZED_MSG, actualMsg)));
		
		return apiPatchStep.getTestResults();
	}
	
	
	@TestFactory
	@DisplayName("5907: PatchPharmacy With Body (invalid field in the body)")
	@Order(7)
	public List<DynamicNode> invalidBodyField()
	{
		Pharmacy pharmacy = PharmacyQueries.getRandomPharmacy();
		
		// Create API body
		String apiBody = "{ \"invalidField\": \"Invalid field\" }";

		ApiPatchStep apiPatchStep = new ApiPatchStep(getGenericHeaders(),
										PHARMACY_PATCH_ENDPOINT,
										apiBody,
										new Object[] {pharmacy.getId()},
										400,
										HTTP_400_BAD_REQUEST);
		apiPatchStep.run();

		// validate the API message
		final String EXPECTED_MSG = "\"No patch field was found in the request body!\"";
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
	 * @param apiBody Request API body
	 * @return list of {@link DynamicNode} of test
	 */
	private List<DynamicNode> happyPathPatch(String apiBody)
	{
		// Call API
		ApiPatchStep apiPatchStep = new ApiPatchStep(getGenericHeaders(),
											PHARMACY_PATCH_ENDPOINT,
											apiBody,
											new Object[] {originalPharmacy.getId()},
											200,
											HTTP_200_OK);
		apiPatchStep.run();

		if ( apiPatchStep.getResponseStatusCode() != 200 )
			return apiPatchStep.getTestResults();

		// Get the member from the Cosmos
		Pharmacy actual = PharmacyQueries.getPharmacyById(testData.getId());

		// Validate the Cosmos data after the API patch
		apiPatchStep.getTestResults().add(dynamicContainer("Cosmos db", testData.compare( actual )));

		return apiPatchStep.getTestResults();
	}


	/**
	 * Create API body
	 * 
	 *  @param taxonomyCode to be updated
	 *  @param taxonomyDescr to be updated
	 *  @param statusInd to be updated
	 * @return JsonObject that represent the params
	 */
	private JsonObject createApiBody(String taxonomyCode, String taxonomyDescr, String statusInd)
	{
		JsonObject requestBody = new JsonObject();

		if ( StringUtils.isNotBlank(taxonomyCode) )
		{
			requestBody.addProperty("taxonomyCode", taxonomyCode);
		}

		if ( StringUtils.isNotBlank(taxonomyDescr) )
		{
			requestBody.addProperty("taxonomyDescr", taxonomyDescr);
		}

		if ( StringUtils.isNotBlank(statusInd) )
		{
			requestBody.addProperty("statusInd", statusInd);
		}

		return requestBody;
	}	

	/**
	 * Create a test data to use
	 * @throws JsonProcessingException if an exception occurs
	 */
	private void createTestData() throws JsonProcessingException
	{
		// Get random pharmacy
		originalPharmacy = PharmacyQueries.getRandomPharmacy();

		// Create a deep copy and update id
		testData = originalPharmacy.deepCopy(Pharmacy.class);
		testData.setLastUpdatedBy(RxConciergeUILogin.getAcctName());
		
 		try {
 			testData.setLastUpdated(DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE));
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}
	}
}
