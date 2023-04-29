/** 

 *  

 * @copyright 2022 Excellus BCBS 

 * All rights reserved. 

 *  

 */ 


package com.excellus.sqa.rxcc.tests.faxrequest;

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
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.FaxRequestQueries;
import com.excellus.sqa.rxcc.dto.FaxRequest;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Headers;


/** 
 *  Cosmos | Database: rxcc-shared | Container: faxRequest
 *  GetFaxRequests Get a FaxRequests by {faxRequestId}.
 *  
 *  GET https://apim-lbs-rxc-dev-east-001.azure-api.net/api/fax-request/fax-requests
 *  
 * @author Mahesh Chowdary (mmanchar) 
 * @since 10/04/2022 
 */
@Tag("ALL")
@Tag("FAXREQUEST")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetFaxRequests")
public class GetFaxRequestsTest extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(GetFaxRequestsTest.class);

	@TestFactory
	@DisplayName("34184: GetFaxRequests Happy Path")
	@Order(1)
	public List<DynamicNode> happyPathParamEmpty() throws JsonProcessingException {

		// Cosmos db
		List<FaxRequest> expectedFaxRequests = new ArrayList<FaxRequest>();
		expectedFaxRequests = FaxRequestQueries.getFaxRequests();

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(),FAX_REQUESTS_GET_ENDPOINT, null, 200,null);
		apiGetStep.run();

		List<FaxRequest> actualFaxRequests = apiGetStep.convertToJsonDTOs(FaxRequest.class);

		if (apiGetStep.stepStatus() != IStep.Status.COMPLETED) {
			return apiGetStep.getTestResults();
		}

		for ( FaxRequest expected : expectedFaxRequests)
		{
			boolean found = false;
			for (FaxRequest actual : actualFaxRequests ) 
			{
				if ( expected.equals(actual) )
				{
					found = true;
					apiGetStep.getTestResults().add(dynamicContainer("FaxRequest id [" + expected.getId() + "]", expected.compare(actual)));
					break;
				}
			}

			if ( !found )
			{
				apiGetStep.getTestResults().add(dynamicTest("FaxRequest id [" + expected.getId() + "]", fail("Unable to find the formulary from API response")));
			}
		}

		return apiGetStep.getTestResults();
	}

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("TBD:  GetFaxRequests  Invalid Auth")
	@Order(2)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException {

		Headers headers = getHeadersInvalidAuth();
		ApiGetStep apiGetStep = new ApiGetStep(headers, FAX_REQUESTS_GET_ENDPOINT, null, 401, HTTP_401_UNAUTHORIZED);

		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

}
