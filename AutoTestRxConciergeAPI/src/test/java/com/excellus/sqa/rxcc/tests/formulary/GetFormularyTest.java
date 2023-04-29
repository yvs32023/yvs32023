/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.formulary;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.FormularyQueries;
import com.excellus.sqa.rxcc.dto.Formulary;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Headers;

/**
 * Cosmos DB | Database id: rxcc-shared | Container id: formulary
 * GetFormulary Get a Formulary by {formularyId}.
 * 
 * GET https://apim-lbs-rxc-dev-east-001.azure-api.net/api/formulary/formularies/{formularyId}
 * 
 * Request Parameters : formularyId
 * 
 * @author Manish Sharma (msharma)
 * @since 06/20/2022
 */
@Tag("ALL")
@Tag("FORMULARY")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetFormulary")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class GetFormularyTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(GetFormularyTest.class);

	@TestFactory
	@DisplayName("20993: GetFormulary Happy Path")
	@Order(1)
	public List<DynamicNode> happyPath() throws JsonProcessingException {

		// Cosmos db
		Formulary expected = FormularyQueries.getRandomFormulary();

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(),FORMULARY_GET_ENDPOINT, new Object[]{expected.getFormularyId()},  200, null);
		apiGetStep.run();

		Formulary actual = apiGetStep.convertToJsonDTO(Formulary.class);

		if (apiGetStep.stepStatus() != IStep.Status.COMPLETED) {
			return apiGetStep.getTestResults();
		}
		apiGetStep.getTestResults().add( dynamicContainer("Formulary DTO", expected.compare(actual)) );

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("20993: GetFormulary Happy Path with Param Empty")
	@Order(2)
	public List<DynamicNode> happyPathParamEmpty() throws JsonProcessingException {

		// Cosmos db
		List<Formulary> expectedFormularies = new ArrayList<Formulary>();
		expectedFormularies = FormularyQueries.getFormularies();

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(),FORMULARY_GET_ENDPOINT, new Object[]{""},  200, null);
		apiGetStep.run();

		List<Formulary> actualFormularies = apiGetStep.convertToJsonDTOs(Formulary.class);

		if (apiGetStep.stepStatus() != IStep.Status.COMPLETED) {
			return apiGetStep.getTestResults();
		}

		for ( Formulary expected : expectedFormularies)
		{
			boolean found = false;
			for (Formulary actual : actualFormularies ) 
			{
				if ( expected.equals(actual) )
				{
					found = true;
					apiGetStep.getTestResults().add(dynamicContainer("Formulary id [" + expected.getFormularyId() + "]", expected.compare(actual)));
					break;
				}
			}

			if ( !found )
			{
				apiGetStep.getTestResults().add(dynamicTest("Formulary id [" + expected.getFormularyId() + "]", fail("Unable to find the formulary from API response")));
			}
		}
		return apiGetStep.getTestResults();
	}	

	/*
	 * Negative test cases
	 */
	@Order(3)
	@TestFactory
	@DisplayName("20994: GetFormulary Happy Path with Param Null")
	public List<DynamicNode> nullParam() throws JsonProcessingException {

		String formularyId = "null";

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(),FORMULARY_GET_ENDPOINT, new Object[]{formularyId},  404, HTTP_404_NOT_FOUND);
		apiGetStep.run();

		// validate the API message
		final String EXPECTED_MSG = "\"Formulary Id "+  formularyId +" not found!\"";
		String actualMsg = apiGetStep.getResponse().then().extract().asString();
		apiGetStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("20995: GetFormulary Happy Path with Param Invalid")
	@Order(4)
	public List<DynamicNode> invalidParam() throws JsonProcessingException {

		String formularyId = "invalid";

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(),FORMULARY_GET_ENDPOINT, new Object[]{formularyId},  404, HTTP_404_NOT_FOUND);
		apiGetStep.run();

		// validate the API message
		final String EXPECTED_MSG = "\"Formulary Id "+  formularyId +" not found!\"";
		String actualMsg = apiGetStep.getResponse().then().extract().asString();
		apiGetStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("20998: GetFormulary Invalid Auth")
	@Order(5)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException {

		String formularyId = "101";

		Headers headers = getHeadersInvalidAuth();
		// API call
		ApiGetStep apiGetStep = new ApiGetStep(headers, FORMULARY_GET_ENDPOINT, new Object[]{formularyId}, 401, HTTP_401_UNAUTHORIZED);

		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("20999: GetFormulary Invalid Method")
	@Order(6)
	public List<DynamicNode> invalidMethod() {

		// Cosmos db
		Formulary expected = FormularyQueries.getRandomFormulary();
		// API call
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), FORMULARY_GET_ENDPOINT, null, new Object[]{expected.getFormularyId()}, 404, HTTP_404_RESOURCE_NOT_FOUND);

		apiPostStep.run();
		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("20996: GetFormulary Invalid EndPoint")
	@Order(7)
	public List<DynamicNode> invalidEndPoint() throws JsonProcessingException {

		// invalid endpoint
		String formularyId = "101";
		String FORMULARY_INVALID_GET_ENDPOINT = "\"/api/formulary/formularie/{formularyId}\"";

		// API call
		ApiGetStep apiGetStep  = new ApiGetStep(getGenericHeaders(), FORMULARY_INVALID_GET_ENDPOINT, new Object[]{formularyId}, 404, HTTP_404_RESOURCE_NOT_FOUND);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}
}
