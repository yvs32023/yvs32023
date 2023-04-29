/** 

 *  

 * @copyright 2022 Excellus BCBS 

 * All rights reserved. 

 *  

 */ 


package com.excellus.sqa.rxcc.tests.faxrequest;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.util.List;
import java.util.UUID;

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
 * Cosmos DB | Database id: rxcc-shared | Container id: faxRequest
 * GetFaxRequest Get a FaxRequest by {faxRequestId}.
 * 	
 * GET https://apim-lbs-rxc-dev-east-001.azure-api.net/fax-request/fax-requests/{faxRequestId}/phone-numbers/{phoneNumber}
 * 
 * Request Parameters : faxRequestId,phoneNumber
 * 
 * @author Mahesh Chowdary (mmanchar)
 * @since 09/29/2022
 */
@Tag("ALL")
@Tag("FAXREQUEST")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetFaxRequest")
public class GetFaxRequestTest extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(GetFaxRequestTest.class);


	@TestFactory
	@DisplayName("34182: GetFaxRequest Happy Path")
	@Order(1)
	public List<DynamicNode> happyPath() throws JsonProcessingException {

		// Cosmos db
		FaxRequest expected = FaxRequestQueries.getRandomFaxRequest();
		
		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(),	FAX_REQUEST_GET_ENDPOINT, new Object[] {expected.getId(),expected.getPhoneNumber()}, 200, null);
		apiGetStep.run();

		FaxRequest actual = apiGetStep.convertToJsonDTO(FaxRequest.class);

		if (apiGetStep.stepStatus() != IStep.Status.COMPLETED) {
			return apiGetStep.getTestResults();
		}

		apiGetStep.getTestResults().add( dynamicContainer("GetFaxRequest DTO", expected.compare(actual)) );
		return apiGetStep.getTestResults();

	}
	
	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("TBD: GetFaxRequest Invalid Auth")
	@Order(2)
	public  List<DynamicNode> invalidAuth() throws JsonProcessingException {

		String faxRequestId = UUID.randomUUID().toString();
		String phoneNumber = UUID.randomUUID().toString();

		Headers headers = getHeadersInvalidAuth();
		ApiGetStep apiGetStep = new ApiGetStep(headers, FAX_REQUEST_GET_ENDPOINT, new Object[] {faxRequestId,phoneNumber}, 401, HTTP_401_UNAUTHORIZED);

		apiGetStep.run();
		return apiGetStep.getTestResults();

	}
}
