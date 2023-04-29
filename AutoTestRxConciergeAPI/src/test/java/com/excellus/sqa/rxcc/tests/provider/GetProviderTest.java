/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.provider;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.ProviderQueries;
import com.excellus.sqa.rxcc.dto.Provider;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Get Provider by {providerId}. providerId is mapped to npi in Pharmacy container.
 *
 *
 * GET    https://apim-lbs-rxc-tst-east-001.azure-api.net/api/provider/providers/{providerId}
 * 
 * 
 * 
 * 
 *
 * 
 * @author Manish Sharma (msharma)
 * @since 03/08/2022
 */

@Tag("ALL")
@UserRole(role = {"RXCC_FULL_MULTI"})
@Tag("PROVIDER")
@DisplayName("GetProvider")
public class GetProviderTest extends RxConciergeAPITestBaseV2
{
	
	private static final Logger logger = LoggerFactory.getLogger(GetProviderTest.class);

	
	@TestFactory
	@DisplayName("785: GetProvider Happy Path")
	public List<DynamicNode> happyPath() throws JsonProcessingException
	{
		// Cosmos db
		Provider expected = ProviderQueries.getRandomProvider();

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), PROVIDER_GET_ENDPOINT, new Object[]{expected.getNpi()}, 200, null);
		apiGetStep.run();

		List<DynamicNode> test = new ArrayList<>(apiGetStep.getTestResults());

		Provider actual = apiGetStep.convertToJsonDTO(Provider.class);
		test.addAll( expected.compare(actual) );

		return test;
	}

	@TestFactory
	@DisplayName("787: GetProvider Invalid Parm")
	public List<DynamicNode> invalidType()
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), PROVIDER_GET_ENDPOINT, new Object[]{"invalid"}, 404, HTTP_404_NOT_FOUND);
		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("787a: GetProvider Parm Alpha")
	public List<DynamicNode> alphaParm()
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), PROVIDER_GET_ENDPOINT, new Object[]{"abcdefgh-ijkl-mnop-qrst-uvwxyzabcdef"}, 404, HTTP_404_NOT_FOUND);
		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("787b: GetProvider Parm Numerical")
	public List<DynamicNode> numericalParm()
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), PROVIDER_GET_ENDPOINT, new Object[]{"abcdefgh-ijkl-mnop-qrst-uvwxyzabcdef"}, 404, HTTP_404_NOT_FOUND);
		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("787c: GetProvider Parm String with Spl Char")
	public List<DynamicNode> specialChar()
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), PROVIDER_GET_ENDPOINT, new Object[]{"5ebc@f4d#a684-4@93-9c36-!de$f5^8&83*"}, 404, HTTP_404_NOT_FOUND);
		apiGetStep.run();
		return apiGetStep.getTestResults();
	}
	
	@TestFactory
	@DisplayName("791: GetProvider Invalid Auth")
	public List<DynamicNode> invalidTokenRestGet()
	{
		
		// Cosmos db
		Provider expected = ProviderQueries.getRandomProvider();
	  
		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getHeadersInvalidAuth(), PROVIDER_GET_ENDPOINT, new Object[]{expected.getNpi()}, 401, HTTP_401_UNAUTHORIZED);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("792:GetProvider Invalid Method")
	public List<DynamicNode> invalidRestApiPostMethod()
	{
	
		// Cosmos db
		Provider expected = ProviderQueries.getRandomProvider();
					
		// API call
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_GET_ENDPOINT, null, new Object[]{expected.getNpi()}, 404, HTTP_404_RESOURCE_NOT_FOUND);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	
}


