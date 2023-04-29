/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.formulary;

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
 * Cosmos | Database: rxcc-shared | Container: formulary
 * 
 * GET https://apim-lbs-rxc-dev-east-001.azure-api.net/api/formulary/formularies
 * 
 * @author Manish Sharma (msharma)
 * @since 06/21/2022
 */
@Tag("ALL")
@Tag("FORMULARY")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetFormularies")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class GetFormulariesTest extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(GetFormulariesTest.class);

	@TestFactory
	@DisplayName("20993: GetFormularies Happy Path")
	@Order(1)
	public List<DynamicNode> happyPathParamEmpty() throws JsonProcessingException {

		// Cosmos db
		List<Formulary> expectedFormularies = new ArrayList<Formulary>();
		expectedFormularies = FormularyQueries.getFormularies();

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(),FORMULARIES_GET_ENDPOINT, null, 200,null);
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
	@TestFactory
	@DisplayName("20987: GetFormularies Invalid EndPoint")
	@Order(2)
	public List<DynamicNode> invalidEndPoint() throws JsonProcessingException {

		// invalid endpoint
		String FORMULARIES_INVALID_GET_ENDPOINT = "\"/api/formulary/formulari\"";

		// API call
		ApiGetStep apiGetStep  = new ApiGetStep(getGenericHeaders(), FORMULARIES_INVALID_GET_ENDPOINT, null, 404, HTTP_404_RESOURCE_NOT_FOUND);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("20998: GetFormularies Invalid Auth")
	@Order(3)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException {

		Headers headers = getHeadersInvalidAuth();
		// API call
		ApiGetStep apiGetStep = new ApiGetStep(headers, FORMULARIES_GET_ENDPOINT, null, 401, HTTP_401_UNAUTHORIZED);

		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("20999: GetFormularies Invalid Method")
	@Order(4)
	public List<DynamicNode> invalidMethod() {

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), FORMULARIES_GET_ENDPOINT, null, null, 500, HTTP_500_INTERNAL_SERVER_ERR);

		apiPostStep.run();
		return apiPostStep.getTestResults();
	}

}
