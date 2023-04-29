/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.pharmacy;

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
import com.excellus.sqa.rxcc.cosmos.PharmacyQueries;
import com.excellus.sqa.rxcc.dto.Pharmacy;
import com.fasterxml.jackson.core.JsonProcessingException;


/**
 * Get Pharmacy by {providerId}. providerId is mapped to npi in Pharmacy container.
 * 
 * GET https://apim-lbs-rxc-tst-east-001.azure-api.net/api/pharmacy/pharmacies/{providerId}
 * 
 * @author Manish Sharma (msharma)
 * @since 03/09/2022
 */
@SuppressWarnings("unused")
@Tag("ALL")	
@Tag("PHARMACY")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetPharmacy")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class GetPharmacyTest extends RxConciergeAPITestBaseV2 
{
	private static final Logger logger = LoggerFactory.getLogger(GetPharmacyTest.class);
	
	@TestFactory
	@DisplayName("762: GetPharmacy Happy Path")
	@Order(1)
	public List<DynamicNode> happyPath() throws JsonProcessingException
	{

		List<DynamicNode> test = new ArrayList<>();

		// Cosmos db - added extension of whereClause to check address2 NOTNULL

		String whereClauseExtension = "where NOT IS_NULL(c.address2)";
		Pharmacy expected = PharmacyQueries.getPharmacy(whereClauseExtension);

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), PHARMACY_GET_ENDPOINT, new Object[]{expected.getNpi()}, 200, HTTP_200_OK);
		apiGetStep.run();

		Pharmacy actual = apiGetStep.convertToJsonDTO(Pharmacy.class);
		apiGetStep.getTestResults().addAll(expected.compare(actual));

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("764: GetPharmacy Invalid Parm")
	@Order(2)
	public List<DynamicNode> invalidType()
	{
		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), PHARMACY_GET_ENDPOINT, new Object[] {"invalid"}, 404, HTTP_404_NOT_FOUND);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}
		
	@TestFactory
	@DisplayName("771: GetPharmacy Invalid Auth")
	@Order(3)
	public List<DynamicNode> invalidTokenRestGet()
	{
		// Cosmos db
		Pharmacy expected = PharmacyQueries.getRandomPharmacy();

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getHeadersInvalidAuth(), PHARMACY_GET_ENDPOINT, new Object[] {expected.getNpi()}, 401, HTTP_401_UNAUTHORIZED);
		apiGetStep.run();
									
		return apiGetStep.getTestResults();
	}
	
	@TestFactory
	@DisplayName("772: GetPharmacy Invalid Method")
	@Order(4)
	public List<DynamicNode> invalidRestApiPostMethod()
	{
		// Cosmos db
		Pharmacy expected = PharmacyQueries.getRandomPharmacy();

		// API Call
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PHARMACY_GET_ENDPOINT, null, new Object[] {expected.getNpi()}, 404, HTTP_404_RESOURCE_NOT_FOUND);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}
}