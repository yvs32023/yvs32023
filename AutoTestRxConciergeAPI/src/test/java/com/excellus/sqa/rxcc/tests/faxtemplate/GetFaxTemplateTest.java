/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.faxtemplate;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

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
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.FaxTemplateQueries;
import com.excellus.sqa.rxcc.dto.FaxTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Cosmos DB | Database id: rxcc-shared | Container id: faxtemplate Get a Fax
 * Template by {id}.
 * 
 * GET
 * https://apim-lbs-rxc-dev-east-001.azure-api.net/api/fax-template/fax-templates/{id}
 * 
 * Request Parameter : id
 * 
 * @author Manish Sharma (msharma)
 * @since 09/27/2022
 */
@Tag("ALL")
@Tag("FAX")
@Tag("FAXTEMPLATE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetFaxTemplate")
public class GetFaxTemplateTest extends RxConciergeAPITestBaseV2 {
	private static final Logger logger = LoggerFactory.getLogger(GetFaxTemplateTest.class);

	@TestFactory
	@DisplayName("39633: FaxTemplate GetFaxTemplate Happy Path")
	@Order(1)
	public List<DynamicNode> happyPath() throws JsonProcessingException {
		// Cosmos db
		FaxTemplate expected = FaxTemplateQueries.getRandomFaxTemplate();

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), FAX_TEMPLATE_GET_ENDPOINT, new Object[] {expected.getId()}, 200, null);
		apiGetStep.run();

		FaxTemplate actual = apiGetStep.convertToJsonDTO(FaxTemplate.class);

		apiGetStep.getTestResults().add( dynamicContainer("FaxTemplate DTO", expected.compare(actual)) );
		return apiGetStep.getTestResults();
	}

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("20928: FaxTemplate Happy Path with Param Invalid")
	@Order(2)
	public List<DynamicNode> invalidParam() throws JsonProcessingException {

		String id = "invalid";

		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), FAX_TEMPLATE_GET_ENDPOINT, new Object[] {id}, 404, HTTP_404_NOT_FOUND);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("20933: FaxTemplate Invalid Auth")
	@Order(3)
	public List<DynamicNode> invalidTokenRestGet() {
		// Cosmos db
		FaxTemplate expected = FaxTemplateQueries.getRandomFaxTemplate();

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getHeadersInvalidAuth(), FAX_TEMPLATE_GET_ENDPOINT, new Object[] {expected.getId()}, 401, HTTP_401_UNAUTHORIZED);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("20934: FaxTemplate Invalid Method")
	@Order(4)
	public List<DynamicNode> invalidRestApiPostMethod() {
		// Cosmos db
		FaxTemplate expected = FaxTemplateQueries.getRandomFaxTemplate();

		// API call
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), FAX_TEMPLATE_GET_ENDPOINT, null, new Object[]{expected.getId()}, 404, HTTP_404_RESOURCE_NOT_FOUND);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("20929: FaxTemplate Empty Parm")
	@Order(5)
	public List<DynamicNode> emptyParam() {
		String id = "";

		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), FAX_TEMPLATE_GET_ENDPOINT, new Object[] {id}, 200, HTTP_200_OK);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}
}
