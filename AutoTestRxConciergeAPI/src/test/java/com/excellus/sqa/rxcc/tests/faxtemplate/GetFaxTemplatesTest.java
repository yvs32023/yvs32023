/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.faxtemplate;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

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
import com.excellus.sqa.rxcc.tests.formulary.GetFormulariesTest;
import com.excellus.sqa.step.IStep;

/**
 * Cosmos | Database: rxcc-shared | Container: faxtemplate
 * 
 * GET
 * https://apim-lbs-rxc-dev-east-001.azure-api.net/api/fax-template/fax-templates
 * 
 * @author Manish Sharma (msharma)
 * @since 09/27/2022
 */
@Tag("ALL")
@Tag("FAX")
@Tag("FAXTEMPLATE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetFaxTemplates")
public class GetFaxTemplatesTest extends RxConciergeAPITestBaseV2 {
	private static final Logger logger = LoggerFactory.getLogger(GetFormulariesTest.class);

	@TestFactory
	@DisplayName("20926/39634: FaxTemplates Happy Path")
	@Order(1)
	public List<DynamicNode> happyPathParamEmpty()
	{

//		List<DynamicNode> test = new ArrayList<DynamicNode>();

		// Cosmos db
		List<FaxTemplate> expectedFaxTemplates = FaxTemplateQueries.getFaxTemplates();

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), FAX_TEMPLATES_GET_ENDPOINT, null, 200, null);
		apiGetStep.run();

		List<FaxTemplate> actualFaxTemplates = apiGetStep.convertToJsonDTOs(FaxTemplate.class);

		if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED)
		{
			return apiGetStep.getTestResults();
		}

		for (FaxTemplate expected : expectedFaxTemplates) {
			boolean found = false;
			for (FaxTemplate actual : actualFaxTemplates) {
				if (expected.equals(actual)) {
					found = true;
					apiGetStep.getTestResults().add(dynamicContainer("Fax Template id [" + expected.getFaxTemplateId() + "]",
							expected.compare(actual)));
					break;
				}
			}

			if (!found) {
				apiGetStep.getTestResults().add(dynamicTest("Fax Template id [" + expected.getFaxTemplateId() + "]",
						fail("Unable to find the formulary from API response")));
			}
		}
		return apiGetStep.getTestResults();
	}

	/*
	 * Negative test cases
	 */

	@TestFactory
	@DisplayName("20931: FaxTemplates Invalid EndPoint")
	@Order(2)
	public List<DynamicNode> invalidEndPoint()
	{

		// invalid endpoint
		String FAX_TEMPLATES_INVALID_GET_ENDPOINT = "\"/api/fax-template/fax-templa\"";

		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), FAX_TEMPLATES_INVALID_GET_ENDPOINT, null, 404, HTTP_404_RESOURCE_NOT_FOUND);
		apiGetStep.run();

		return apiGetStep.getTestResults();

	}

	@TestFactory
	@DisplayName("20933: FaxTemplates Invalid Auth")
	@Order(3)
	public List<DynamicNode> invalidTokenRestGet()
	{
		ApiGetStep apiGetStep = new ApiGetStep(getHeadersInvalidAuth(), FAX_TEMPLATES_GET_ENDPOINT, null, 401, HTTP_401_UNAUTHORIZED);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("20934: FaxTemplates Invalid Method")
	@Order(4)
	public List<DynamicNode> invalidMethod()
	{
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), FAX_TEMPLATES_GET_ENDPOINT, null, null, 404, HTTP_404_RESOURCE_NOT_FOUND);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

}
