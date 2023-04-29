/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.formulary;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import com.excellus.sqa.rxcc.cosmos.FormularyQueries;
import com.excellus.sqa.rxcc.dto.Formulary;
import com.excellus.sqa.step.IStep;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;

import io.restassured.http.Headers;

/**
 * PATCH -  https://apim-lbs-rxc-dev-east-001.azure-api.net/api/formulary/formularies/{formularyId}
 * 
 * {
 *  "formularyDescription": "Testing Data",
 * }
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 08/07/2022
 */
@Tag("ALL")
@Tag("FORMULARY")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PatchFormulary")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class PatchFormularyTest extends RxConciergeAPITestBaseV2 {
	private static final Logger logger = LoggerFactory.getLogger(PatchFormularyTest.class);

	private final String FORMULARYDESCRIPTION = "This formulary description is created by test automation around - ";

	private String createdDateTime;
	private String updateDateTime;
	private Formulary toBeDeleted;

	/**
	 * Setup the created date/time
	 */
	@BeforeEach
	public void dataSetup() {
		try {
			createdDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
		}
		catch(Exception e) {
			logger.error("An unexpected error is caught while generating timestamp", e);
		}
	}

	/**
	 * Delete any formulary that was create with the test
	 */
	@AfterEach
	public void deleteFormularyFromCosmos() {
		if (toBeDeleted != null) {
			FormularyQueries.deleteFormulary(toBeDeleted.getId(), toBeDeleted.getFormularyId());
		}

		toBeDeleted = null;
	}

	@TestFactory
	@DisplayName("21010: PatchFormulary Happy Path")
	@Order(1)
	public List<DynamicNode> happyPathPatch() {

		// Get max formularyId from cosmos db
		List<String> formularyId = FormularyQueries.getMaxFormularyId();

		List<Integer> maxFormularyIdList = formularyId.stream()
				.map(maxFormularyId -> Integer.parseInt(maxFormularyId) + 1)
				.collect(Collectors.toList());

		String maxFormularyId = (maxFormularyIdList.get(0).toString());

		// Create API body
		try {
			updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
		}
		catch(Exception e) {
			logger.error("An unexpected error is caught while generating timestamp", e);
		}

		// Create formulary manually in the Cosmos DB container
		Formulary expected = createFormulary(maxFormularyId);

		JsonObject body = createApiBody(null, null, null, FORMULARYDESCRIPTION + " -  updated on " + updateDateTime, null ,null, null);

		// call happy path testing
		return happyPathPatch(expected, body);

	}

	@TestFactory
	@DisplayName("21012: PatchFormulary Happy Path : Special Character")
	@Order(2)
	public List<DynamicNode> happyPathPatchFormularyDescriptionSpecialCharacter() {

		// Get max formularyId from cosmos db
		List<String> formularyId = FormularyQueries.getMaxFormularyId();

		List<Integer> maxFormularyIdList = formularyId.stream()
				.map(maxFormularyId -> Integer.parseInt(maxFormularyId) + 1)
				.collect(Collectors.toList());

		String maxFormularyId = (maxFormularyIdList.get(0).toString());

		// Create API body
		try {
			updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
		}
		catch(Exception e) {
			logger.error("An unexpected error is caught while generating timestamp", e);
		}

		// Create formulary manually in the Cosmos DB container
		Formulary expected = createFormulary(maxFormularyId);

		JsonObject body = createApiBody(expected.getFormularyId(), null, null, FORMULARYDESCRIPTION + " - Maïs Sûr  Déjà Test Automation " + updateDateTime, null ,expected.getCreatedBy(), null);

		// call happy path testing
		return happyPathPatch(expected, body);
	}

	@TestFactory
	@DisplayName("21013: PatchFormulary Happy Path : Numerical")
	@Order(3)
	public List<DynamicNode> happyPathPatchFormularyDescriptionNumerical() {

		// Get max formularyId from cosmos db
		List<String> formularyId = FormularyQueries.getMaxFormularyId();

		List<Integer> maxFormularyIdList = formularyId.stream()
				.map(maxFormularyId -> Integer.parseInt(maxFormularyId) + 1)
				.collect(Collectors.toList());

		String maxFormularyId = (maxFormularyIdList.get(0).toString());

		// Create API body
		try {
			updateDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
		}
		catch(Exception e) {
			logger.error("An unexpected error is caught while generating timestamp", e);
		}

		// Create formulary manually in the Cosmos DB container
		Formulary expected = createFormulary(maxFormularyId);

		JsonObject body = createApiBody(null, null, null, FORMULARYDESCRIPTION + " -  12 345 46 " + updateDateTime, null ,null, null);

		// call happy path testing
		return happyPathPatch(expected, body);
	}

	/*
	 * Helper methods
	 */
	/**
	 * Perform the actual test
	 * 
	 * @param expected
	 */
	private List<DynamicNode> happyPathPatch( Formulary expected, JsonObject body)
	{
		List<DynamicNode> test = new ArrayList<DynamicNode>();

		// Update the expected formulary base on the formulary inserted into Cosmos db
		updateFormulary(expected, body);

		// Call API
		ApiPatchStep apiPatchStep = new ApiPatchStep(getGenericHeaders(), FORMULARY_PATCH_ENDPOINT, body.toString(), new Object[] {expected.getFormularyId() },200, null);
		apiPatchStep.run(); // perform the actual API call

		Formulary actualAPIResponse = apiPatchStep.convertToJsonDTO(Formulary.class);  // convert the API response to JSON object

		if ( apiPatchStep.stepStatus() != IStep.Status.COMPLETED )
		{
			return apiPatchStep.getTestResults();
		}
		else 
		{
			test.addAll(apiPatchStep.getTestResults());
		}

		/*
		 * Validation
		 */
		// API
		apiPatchStep.getTestResults().add(dynamicContainer("API response", expected.compare(actualAPIResponse)));

		// Get the formulary from the cosmos
		Formulary actual = FormularyQueries.getFormulary(expected.getId(), expected.getFormularyId());

		// Validate the Cosmos data after the API patch
		apiPatchStep.getTestResults().add(dynamicContainer("Cosmos db", expected.compare( actual )));

		return apiPatchStep.getTestResults();
	}


	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("21015: PatchFormulary Missing Formulary Description")
	@Order(4)
	public List<DynamicNode> FormularyDescriptionMissingBody() throws JsonProcessingException {

		// Get formulary
		String formularyId = "9_123456";

		//API body as empty
		JsonObject body = createApiBody(null, null, null, null, null, null, null);

		Headers headers = getGenericHeaders();

		ApiPatchStep apiPatchStep = new ApiPatchStep(headers, FORMULARY_PATCH_ENDPOINT,
				body.toString(), new Object[]{formularyId}, 400, HTTP_400_BAD_REQUEST);

		apiPatchStep.run();

		// validate the API message
		final String EXPECTED_MSG = "\"Submit a valid field for patching!\"";
		String actualMsg = apiPatchStep.getResponse().then().extract().asString();
		apiPatchStep.getTestResults().add(
				dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));
		return apiPatchStep.getTestResults();
	}

	@TestFactory
	@DisplayName("21016: PatchFormulary Invalid Auth")
	@Order(5)
	public  List<DynamicNode> invalidAuth() throws JsonProcessingException {

		String formularyId = "9_123456";

		//invalidHeader
		Headers headers = getHeadersInvalidAuth();


		ApiPatchStep apiPatchStep = new ApiPatchStep(headers, FORMULARY_PATCH_ENDPOINT,
				null, new Object[]{formularyId}, 401, HTTP_401_UNAUTHORIZED);
		apiPatchStep.run();

		return apiPatchStep.getTestResults();
	}

	/*
	 * Helper methods
	 */
	/**
	 * Create the formulary in the Cosmos DB container
	 * 
	 * @param formularyId to associate the formulary
	 * @return {@link Formulary} that has been inserted into Cosmos DB container
	 */
	private Formulary createFormulary(String formularyId) {
		Formulary formulary = new Formulary();
		formulary.setFormularyId(formularyId);
		formulary.setType("formulary");
		formulary.setFormularyCode(formularyId);
		formulary.setFormularyDescription(FORMULARYDESCRIPTION + "created around " + createdDateTime);
		formulary.setCreatedBy("Test Automation");
		formulary.setCreatedDateTime(createdDateTime);
		formulary.setVersion("1.0");
		formulary.setId(UUID.randomUUID().toString());

		// Manually insert note (test data) into Cosmos DB
		FormularyQueries.insertFormulary(formulary);

		// Set toBeDeleted for data clean up after the test
		toBeDeleted = formulary;

		return formulary;
	}

	/**
	 * Create API body
	 * 
	 * @param formularyId          of the formulary
	 * @param type                 of the formulary
	 * @param formularyCode        of the formulary
	 * @param formularyDescription of the formulary
	 * @param createdBy            of the formulary
	 * @param lastUpdatedBy        of the formulary
	 * @param version              of the formulary
	 * @return
	 */
	private JsonObject createApiBody(String formularyId, String type, String formularyCode, String formularyDescription,String createdBy, String lastUpdatedBy, String version) 
	{
		JsonObject requestBody = new JsonObject();

		if (formularyId != null)
			requestBody.addProperty("formularyId", formularyId);

		if (type != null)
			requestBody.addProperty("type", type);

		if (formularyCode != null)
			requestBody.addProperty("formularyCode", formularyCode);

		if (formularyDescription != null)
			requestBody.addProperty("formularyDescription", formularyDescription);

		if (createdBy != null)
			requestBody.addProperty("createdBy", createdBy);

		if (lastUpdatedBy != null)
			requestBody.addProperty("lastUpdatedBy", lastUpdatedBy);

		if (version != null)
			requestBody.addProperty("version", version);

		return requestBody;
	}

	/**
	 * Update the expected Formulary base on the API body
	 * 
	 * @param expected {@link Formulary} to be updated
	 * @param body     {@link JsonObject} that contains the updated formulary data
	 */
	private void updateFormulary(Formulary expected, JsonObject body)
	{
		if (body.get("formularyId") != null && !body.get("formularyId").isJsonNull())
			expected.setFormularyId(body.get("formularyId").getAsString());

		if (body.get("type") != null && !body.get("type").isJsonNull())
			expected.setType(body.get("type").getAsString());

		if (body.get("formularyCode") != null && !body.get("formularyCode").isJsonNull())
			expected.setFormularyCode(body.get("formularyCode").getAsString());

		if (body.get("formularyDescription") != null && !body.get("formularyDescription").isJsonNull())
			expected.setFormularyDescription(body.get("formularyDescription").getAsString());

		if (body.get("createdBy") != null && !body.get("createdBy").isJsonNull())
			expected.setCreatedBy(body.get("createdBy").getAsString());

		if (body.get("lastUpdatedBy") != null && !body.get("lastUpdatedBy").isJsonNull())
			expected.setLastUpdatedBy(body.get("lastUpdatedBy").getAsString());

		if (body.get("version") != null && !body.get("version").isJsonNull())
			expected.setVersion(body.get("version").getAsString());

		expected.setLastUpdatedDateTime(updateDateTime);
		expected.setLastUpdatedBy(RxConciergeUILogin.getAcctName());

	}
}
