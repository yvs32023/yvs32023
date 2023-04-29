/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.tests.lookup;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.LookupQueries;
import com.excellus.sqa.rxcc.dto.Lookup;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Headers;

/**
 * Cosmos DB | Database id: rxcc-shared | Container id: lookup
 *
 * GetLookup
 * Returns all lookups by the provided enum {type} member, rxclaim, note.
 *
 * Request
 * GET  https://apim-lbs-rxc-tst-east-001.azure-api.net/api/lookup/lookups/{type}
 *
 * Request parameters
 *  Name    In          Required    Type    Description
 *  type    template    true
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/05/2022
 */
@Tag("ALL")
@Tag("LOOKUP")
@DisplayName("GetLookup")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class GetLookupTest extends RxConciergeAPITestBaseV2 {

	private static final Logger logger = LoggerFactory.getLogger(GetLookupTest.class);

	@TestFactory
	@DisplayName("3432: GetLookup Happy Path")
	@Order(1)
	public List<DynamicNode> credentials() throws JsonProcessingException {

		List<DynamicNode> test = new ArrayList<DynamicNode>();
		try
		{
			for ( Lookup.LookupType type: LookupQueries.getLookupTypes() ) {
				test.add(dynamicContainer(type.toString(), getLookupAndValidate(type)));
			}
		}
		catch (Exception e)
		{
			test.add(dynamicContainer("Lookup Type",() -> fail("Unexpected exception",e)));
		}
		return test;
	}

	/**
	 * Perform API call and Cosmos DB query then compare results
	 * @param type of Lookup
	 * @return list of test results represented in {@link DynamicNode}
	 */
	private List<DynamicNode> getLookupAndValidate(Lookup.LookupType type) {

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(),	LOOKUP_GET_ENDPOINT, new Object[] {type.toString()}, 200, null);
		apiGetStep.run();

		Lookup actual = apiGetStep.convertToJsonDTO(Lookup.class);

		if (apiGetStep.stepStatus() != IStep.Status.COMPLETED) {
			return apiGetStep.getTestResults();
		}

		logger.debug("API response: " + actual.toString());

		Lookup expected = LookupQueries.getLookup(type);
		logger.debug("Cosmos DB: " + expected.toString());

		apiGetStep.getTestResults().add(dynamicContainer(expected.getType().toString(), expected.compare(actual)));

		return apiGetStep.getTestResults();
	}

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("3434: GetLookup Invalid Parm")
	@Order(2)
	public  List<DynamicNode> invalidType() throws JsonProcessingException {

		String type = "invalid";

		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), LOOKUP_GET_ENDPOINT, new Object[] { type }, 404, HTTP_404_NOT_FOUND);

		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@ParameterizedTest
	@DisplayName("3355: GetLookup Parm Alpha")
	@Order(3)
	@ValueSource(strings = { "DAW", "CREDENTIALS", "TAXONOMY", "PROVIDER_STATUS" , "PHARMACY_STATUS", "FORMULARY", "QUEUESTATUSCODE",
			"VALIDATESTATUSCODE", "RETRIGGERAFTER180STATUS", "DECLINESTATUSCODE" , "RETRIGGERCUSTOMSTATUS", "FORM_STATUS"})
	public void allCapsType(String type)
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), LOOKUP_GET_ENDPOINT, new Object[] { type }, 404, HTTP_404_NOT_FOUND);

		apiGetStep.run();
	}

	@ParameterizedTest
	@DisplayName("3356: GetLookup Parm String with Spl Char")
	@Order(4)
	@ValueSource(strings = { "d@w", "cr3d3nt!@ls", "t&x#n*my","pr@V!d#r_$t@t#$" , "p#@rm@cy_$t@t#$", "f0rmu!@ry", "queue$t@tu$c0de",
			"v@l!d@Te$t@tu$c0de", "retr!gger@fter180$t@tu$", "de(l!#e$t@tu$c0de" , "retr!gger(u$t0m$t@tu$", "f0r#_$t@tu$" })
	public void specialCharType(String type)
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), LOOKUP_GET_ENDPOINT, new Object[] { type }, 404, HTTP_404_NOT_FOUND);

		apiGetStep.run();
	}

	@ParameterizedTest
	@DisplayName("3354: GetLookup Parm Numerical")
	@Order(5)
	@ValueSource(strings = { "12345", "67890" })
	public void parmNumerical(String type)
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), LOOKUP_GET_ENDPOINT, new Object[] { type }, 404, HTTP_404_NOT_FOUND);

		apiGetStep.run();
	}

	@TestFactory
	@DisplayName("3350: GetLookup Invalid Auth")
	@Order(6)
	public  List<DynamicNode> invalidAuth() throws JsonProcessingException {

		String type = "daw";

		Headers headers = getHeadersInvalidAuth();
		ApiGetStep apiGetStep = new ApiGetStep(headers, LOOKUP_GET_ENDPOINT, new Object[] { type }, 401, HTTP_401_UNAUTHORIZED);

		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("3351: GetLookup Invalid Method")
	@Order(7)
	public List<DynamicNode> invalidMethod() {

		String type = "daw";

		ApiPostStep apiPostStep  = new ApiPostStep(getGenericHeaders(), LOOKUP_GET_ENDPOINT,null,  new Object[] { type }, 404, HTTP_404_RESOURCE_NOT_FOUND);

		apiPostStep.run();
		return apiPostStep.getTestResults();
	}

}
