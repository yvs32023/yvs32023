/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.pharmacy;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import com.excellus.sqa.rxcc.cosmos.PharmacyQueries;
import com.excellus.sqa.rxcc.dto.Pharmacy;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * POST https://apim-lbs-rxc-dev-east-001.azure-api.net/api/pharmacy/pharmacies
 * 
 * Returns a collection of pharmacies for a collection of NPIs in the request body. 
 * e.g. ["string"]
 * 
 * @author Manish Sharma (msharma)
 * @since 04/01/2022
 */
@SuppressWarnings("unused")
@Tag("ALL")
@Tag("PHARMACY")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetPharmacies")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class GetPharmaciesTest extends RxConciergeAPITestBaseV2 
{
	private static final Logger logger = LoggerFactory.getLogger(GetPharmaciesTest.class);

	@TestFactory
	@DisplayName("754: GetPharmacies Body Happy Path with more than 1 values ")
	@Order(1)
	public List<DynamicNode> happyPathOne()
	{
		int numberOfRecords = 1;
		return processHappyPath(numberOfRecords);
	}
	
	@TestFactory
	@DisplayName("755: GetPharmacies Body Happy Path with more than 1 values : 2 values")
	@Order(2)
	public List<DynamicNode> happyPathhappyPathMoreThanOneValue()
	{
		int numberOfRecords = 2;
		return processHappyPath(numberOfRecords);
	}	
		
	@TestFactory
	@DisplayName("761: GetPharmacies Body Happy Path with more than 1 values : 3 values")
	@Order(3)
	public List<DynamicNode> happyPathMoreThanTwoValue()
	{
		int numberOfRecords = 3;
		return processHappyPath(numberOfRecords);
	}

	/**
	 * Performs the test given the number of providers to use
	 * @param numberOfRecords to use in the test
	 * @return list of {@link DynamicNode} tests to be performed
	 */
	private List<DynamicNode> processHappyPath(int numberOfRecords)
	{
		List<Pharmacy> expectedPharmacies = PharmacyQueries.getPharmacies().stream()
													.collect(Collectors.collectingAndThen(Collectors.toList(), collected ->
													{
														Collections.shuffle(collected);
														return collected.stream();
													}))
													.limit(numberOfRecords)
													.collect(Collectors.toList());

		List<String> pharmacyNpis = expectedPharmacies.stream()
											.map(e -> e.getNpi())
											.collect(Collectors.toList());

		String requestBody = pharmacyNpis.stream().collect(Collectors.joining("\",\"", "\"", "\""));
		requestBody = "[" + requestBody + "]";

		// API Call
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), GETPHARMACIES_POST_ENDPOINT, requestBody, null, 200, HTTP_200_OK);
		apiPostStep.run();

		List<DynamicNode> test = new ArrayList<>(apiPostStep.getTestResults());

		List<Pharmacy> actualPharmacies = apiPostStep.convertToJsonDTOs(Pharmacy.class);

		// Validation
		for (Pharmacy expected : expectedPharmacies)
		{
			boolean found = false;
			for (Pharmacy actual : actualPharmacies )
			{
				if ( expected.equals(actual) )
				{
					found = true;
					test.add(dynamicContainer("Pharmacy npi [" + expected.getNpi() + "]", expected.compare(actual)));
					break;
				}
			}

			if ( !found )
			{
				test.add(dynamicTest("Pharmacy npi [" + expected.getNpi() + "]", fail("Unable to find the pharmacy from API response")));
			}
		}

		return test;
	}

	/*
	 * Negative test cases
	 */

	@TestFactory
	@DisplayName("759:GetPharmacies Invalid Method")
	@Order(4)
	public List<DynamicNode> invalidRestApiGetMethod()
	{
		Pharmacy pharmacy = PharmacyQueries.getRandomPharmacy();
		String body = "[ \"" + pharmacy.getNpi() + "\" ]";

		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), GETPHARMACIES_POST_ENDPOINT, null, 404, HTTP_404_RESOURCE_NOT_FOUND);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("758:GetPharmacies Invalid Auth")
	@Order(5)
	public List<DynamicNode> invalidAuthentication()
	{
		Pharmacy pharmacy = PharmacyQueries.getRandomPharmacy();
		String body = "[ \"" + pharmacy.getNpi() + "\" ]";

		ApiPostStep apiPostStep = new ApiPostStep(getHeadersInvalidAuth(), GETPHARMACIES_POST_ENDPOINT, body, null, 401, HTTP_401_UNAUTHORIZED);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}
}
