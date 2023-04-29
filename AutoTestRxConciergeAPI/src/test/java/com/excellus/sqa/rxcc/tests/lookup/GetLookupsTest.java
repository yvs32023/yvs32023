/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.tests.lookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.excellus.sqa.rxcc.cosmos.LookupQueries;
import com.excellus.sqa.rxcc.dto.Lookup;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Headers;

/**
 * Cosmos DB | Database id: rxcc-shared | Container id: lookup
 *
 * GetLookup
 * Returns lookups for all types.
 *
 * Request
 * GET  https://apim-lbs-rxc-tst-east-001.azure-api.net/api/lookup/lookups
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/10/2022
 */
@Tag("ALL")
@Tag("LOOKUP")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetLookups")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class GetLookupsTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(GetLookupsTest.class);

	@TestFactory
	@DisplayName("3353: GetLookups Happy Path")
	@Order(1)
	public List<DynamicNode> getLookups() throws JsonProcessingException {

		List<DynamicNode> test = new ArrayList<DynamicNode>();

		logger.debug("Started GetLookupsTest.getLookups");
		try
		{
			// API call
			ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(),	LOOKUPS_GET_ENDPOINT, null, 200, null);
			apiGetStep.run();

			List<Lookup> actual = apiGetStep.convertToJsonDTOs(Lookup.class);

			if (apiGetStep.stepStatus() != IStep.Status.COMPLETED) {
				return apiGetStep.getTestResults();
			}

			/*
			 * Query Cosmos
			 */
			List<Lookup> expected = LookupQueries.getLookups();

			/*
			 * Validate
			 */
			if ( expected == null && actual != null ) {
				apiGetStep.getTestResults().add(dynamicTest("Lookups", () -> fail("Cosmos query returned null result but API returned " + actual.size() + " lookups")));
				return test;
			}
			else if ( expected != null && actual == null ) {
				apiGetStep.getTestResults().add(dynamicTest("Lookups", () -> fail("Cosmos query returned " + expected.size() + " lookups but API returned null")));
				return apiGetStep.getTestResults();
			}

			apiGetStep.getTestResults().add(dynamicTest("Lookup count", () -> assertEquals(expected.size(), actual.size())));  // size

			for ( Lookup theExpected : expected )
			{
				boolean found = false;
				for ( Lookup theActual : actual )
				{
					if ( theExpected.getType() == theActual.getType() ) {
						apiGetStep.getTestResults().add(dynamicContainer(theExpected.getType().toString(), theExpected.compare(theActual)));
						found = true;
						break;
					}
				}

				if (!found) {
					apiGetStep.getTestResults().add(dynamicTest(theExpected.getType().toString(), () -> fail("Lookup was not found")));
				}
			}
			test.add(dynamicContainer("Lookup Type", apiGetStep.getTestResults()));
		}

		catch (Exception e)
		{
			test.add(dynamicContainer("Lookup Type",() -> fail("Unexpected exception",e)));
		}

		return test;
	}
	
	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("3332: GetLookups Invalid Auth")
	@Order(2)
	public  List<DynamicNode> invalidAuth() throws JsonProcessingException {
		
		Headers headers = getHeadersInvalidAuth();
		ApiGetStep apiGetStep = new ApiGetStep(headers, LOOKUPS_GET_ENDPOINT, null, 401, HTTP_401_UNAUTHORIZED);

		apiGetStep.run();
		return apiGetStep.getTestResults();
	}
	
	@TestFactory
	@DisplayName("3333: GetLookups Invalid Method")
	@Order(3)
	public List<DynamicNode> invalidMethod() {
		
		ApiPostStep apiPostStep  = new ApiPostStep(getGenericHeaders(), LOOKUPS_GET_ENDPOINT,null, null, 404, HTTP_404_RESOURCE_NOT_FOUND);

		apiPostStep.run();
		return apiPostStep.getTestResults();
	}
}