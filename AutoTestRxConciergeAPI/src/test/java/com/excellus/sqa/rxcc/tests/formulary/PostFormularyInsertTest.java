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

import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.FormularyQueries;
import com.excellus.sqa.rxcc.dto.Formulary;
import com.excellus.sqa.step.IStep;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.restassured.http.Headers;

/**
 * POST - https://apim-lbs-rxc-dev-east-001.azure-api.net/api/formulary/formularies
 * 
 * {
 *  "formularyId": "string",
 *  "formularyCode": "string",
 *  "formularyDescription": "string",
 * }
 * 
 * @author Manish Sharma (msharma)
 * @since 08/01/2022
 */
@Tag("ALL")
@Tag("FORMULARY")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostFormulary")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class PostFormularyInsertTest extends RxConciergeAPITestBaseV2{

	private static final Logger logger = LoggerFactory.getLogger(PostFormularyInsertTest.class);

	private String createdDateTime;
	private Formulary toBeDeleted;

	/**
	 * Setup the created date/time
	 */
	@BeforeEach
	public void dataSetup()
	{
		try {
			createdDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
		}
		catch(Exception e) {
			logger.error("An unexpected error is caught while generating timestamp", e);
		}
	}

	/**
	 * Delete any formulary  that was create with the test
	 */
	@AfterEach
	public void deleteFormularyFromCosmos()
	{
		if ( toBeDeleted != null )
		{
			FormularyQueries.deleteFormulary( toBeDeleted.getId(),toBeDeleted.getFormularyId());
		}
		toBeDeleted = null;
	}

	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("21002: InsertFormulary Happy Path")
	@Order(1)
	public List<DynamicNode> happyPathInsertFormulary() throws JsonProcessingException{

		// Get max formularyId from cosmos db
		List<String> formularyId = FormularyQueries.getMaxFormularyId();

		List<Integer> maxFormularyIdList = formularyId.stream()
				.map(maxFormularyId -> Integer.parseInt(maxFormularyId) + 1)
				.collect(Collectors.toList());


		String maxFormularyId = (maxFormularyIdList.get(0).toString());
		String maxFormularyCode = (maxFormularyIdList.get(0).toString());

		// Create API body
		JsonObject apiBody = createApiBody(maxFormularyId, maxFormularyCode, "This formulary description is created by test automation around " + createdDateTime + " (local time)");

		// run the test
		return happyPath(maxFormularyId,apiBody);
	}


	@TestFactory
	@DisplayName("21003: InsertFormulary String with Special Character")
	@Order(2)
	public List<DynamicNode> specialCharacterInsertFormulary() throws JsonProcessingException
	{
		String formularyId = "DjMaïs";

		// Create API body
		JsonObject apiBody = createApiBody(formularyId, "DéjàMaïs", "This formulary description is DéjàMaïs by test automation around " + createdDateTime + " (local time)");

		// run the test
		return happyPath(formularyId,apiBody);
	}

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("21004: InsertFormulary Formulary Id String Null")
	@Order(3)
	public List<DynamicNode> nullFormularyIdBody() throws JsonProcessingException {

		String formularyId = "";

		// Create API body
		JsonObject apiBody = createApiBody(formularyId, "DéjàMaïs", "This formulary description is created by test automation around " + createdDateTime + " (local time)");

		Headers headers = getGenericHeaders();
		ApiPostStep apiPostStep = new ApiPostStep(headers, FORMULARY_INSERT_POST_ENDPOINT,
				apiBody.toString(), null, 400, HTTP_400_BAD_REQUEST);
		apiPostStep.run();

		// validate the API message
		final String EXPECTED_MSG = "\"formularyId is required!\"";
		String actualMsg = apiPostStep.getResponse().then().extract().asString();
		apiPostStep.getTestResults().add(
				dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));
		return apiPostStep.getTestResults();
	}


	@TestFactory
	@DisplayName("21004: InsertFormulary Formulary Code String Null")
	@Order(4)
	public List<DynamicNode> nullFormularyCodeBody() throws JsonProcessingException {

		String formularyCode = "";

		// Create API body
		JsonObject apiBody = createApiBody("999", formularyCode, "This formulary description is created by test automation around " + createdDateTime + " (local time)");

		Headers headers = getGenericHeaders();
		ApiPostStep apiPostStep = new ApiPostStep(headers, FORMULARY_INSERT_POST_ENDPOINT,
				apiBody.toString(), null, 400, HTTP_400_BAD_REQUEST);
		apiPostStep.run();

		// validate the API message
		final String EXPECTED_MSG = "\"formularyCode is required!\"";
		String actualMsg = apiPostStep.getResponse().then().extract().asString();
		apiPostStep.getTestResults().add(
				dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));
		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("21004: InsertFormulary Formulary Description String Null")
	@Order(5)
	public List<DynamicNode> nullFormularyDescriptionBody() throws JsonProcessingException {

		String formularyDescription = "";

		// Create API body
		JsonObject apiBody = createApiBody("999", "999", formularyDescription);

		Headers headers = getGenericHeaders();
		ApiPostStep apiPostStep = new ApiPostStep(headers, FORMULARY_INSERT_POST_ENDPOINT,
				apiBody.toString(), null, 400, HTTP_400_BAD_REQUEST);
		apiPostStep.run();

		// validate the API message
		final String EXPECTED_MSG = "\"formularyDescription is required!\"";
		String actualMsg = apiPostStep.getResponse().then().extract().asString();
		apiPostStep.getTestResults().add(
				dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));
		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("21005: InsertFormulary Duplicate Formulary Id")
	@Order(6)
	public List<DynamicNode> duplicateFormularyIdBody() throws JsonProcessingException {

		// Cosmos db
		Formulary expected = FormularyQueries.getRandomFormulary();
		String formularyId = expected.getFormularyId();

		// Create API body
		JsonObject apiBody = createApiBody(formularyId, "999", "This formulary description is created by test automation around " + createdDateTime + " (local time)");

		Headers headers = getGenericHeaders();
		ApiPostStep apiPostStep = new ApiPostStep(headers, FORMULARY_INSERT_POST_ENDPOINT,
				apiBody.toString(), null, 400, HTTP_400_BAD_REQUEST);
		apiPostStep.run();

		// validate the API message
		final String EXPECTED_MSG = "\"Formulary Id "+  formularyId +" already exists!\"";
		String actualMsg = apiPostStep.getResponse().then().extract().asString();
		apiPostStep.getTestResults().add(
				dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));
		return apiPostStep.getTestResults();
	}


	@TestFactory
	@DisplayName("21008: InsertFormulary Invalid Auth")
	@Order(7)
	public  List<DynamicNode> invalidAuth() throws JsonProcessingException {

		String formularyId = "DéjàMaïs";

		// Create API body
		JsonObject apiBody = createApiBody(formularyId, "DéjàMaïs", "This formulary description is created by test automation around " + createdDateTime + " (local time)");

		//invalidHeader
		Headers headers = getHeadersInvalidAuth();
		ApiPostStep apiPostStep = new ApiPostStep(headers, FORMULARY_INSERT_POST_ENDPOINT,
				apiBody.toString(),null,  401, HTTP_401_UNAUTHORIZED);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	/*
	 * Helper methods
	 */
	/**
	 * Generic test for happy path
	 * @param apiBody {@link JsonObject}
	 * @return test validation result
	 */
	private List<DynamicNode> happyPath(String formularyId,JsonObject apiBody){

		List<DynamicNode> test = new ArrayList<DynamicNode>();

		// Call API
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), FORMULARY_INSERT_POST_ENDPOINT, apiBody.toString(),null,201, null);
		apiPostStep.run(); // perform the actual API call

		Formulary actualAPIResponse = apiPostStep.convertToJsonDTO(Formulary.class);  // convert the API response to JSON object
		toBeDeleted = actualAPIResponse;	// set this to be deleted after the test completes
		if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
		{
			return apiPostStep.getTestResults();
		}
		else 
		{
			test.addAll(apiPostStep.getTestResults());
		}

		// Create expected 
		Formulary expected = createExpectedFormulary(formularyId,createdDateTime, apiBody);

		/*
		 * Validations
		 */
		// API
		test.add(dynamicContainer("API response", expected.compare(actualAPIResponse)));

		// Query Cosmos
		List<Formulary> formularies =  FormularyQueries.getFormulary(formularyId);

		test.add(dynamicContainer("Cosmos DB", expected.compare(formularies.get(0))));

		return test;
	}

	/**
	 * Create the API body
	 * 
	 * @param formularyId of the formulary
	 * @param formularyCode to be appended to the formulary
	 * @return {@link JsonObject}
	 */
	private JsonObject createApiBody(String formularyId, String formularyCode, String formularyDescription )
	{
		JsonObject requestBody = new JsonObject();

		if (formularyId != null)
			requestBody.addProperty("formularyId", formularyId);

		if (formularyCode != null)
			requestBody.addProperty("formularyCode", formularyCode);

		if (formularyDescription != null)
			requestBody.addProperty("formularyDescription", formularyDescription);

		return requestBody;
	}

	/**
	 * Create the expected {@link Formulary}
	 * 
	 * @param formularyId of the formulary
	 * @param createdDateTime of the formulary
	 * @param apiBody {@link JsonObject} of the API body
	 * @return {@link Formulary}
	 */
	private Formulary createExpectedFormulary(String formularyId,String createdDateTime, JsonObject apiBody)
	{
		// Complete JSON oject after API call to set all the different properties
		apiBody.addProperty("formularyId",formularyId);
		apiBody.addProperty("createdBy", RxConciergeUILogin.getAcctName());
		apiBody.addProperty("createdDateTime", createdDateTime );
		apiBody.addProperty("lastUpdatedBy", RxConciergeUILogin.getAcctName());
		apiBody.addProperty("lastUpdatedDateTime", createdDateTime );
		apiBody.addProperty("type", "formulary");
		apiBody.addProperty("version", "1.0");

		Gson gson = new Gson();
		Formulary expectedFormulary = gson.fromJson(apiBody, Formulary.class);

		return expectedFormulary;
	}
}
