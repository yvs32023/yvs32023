/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;

import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.ProviderQueries;
import com.excellus.sqa.rxcc.dto.Provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 03/09/2022
 */


@Tag("ALL")
@UserRole(role = {"RXCC_FULL_MULTI"})
@Tag("PROVIDER")
@DisplayName("GetProviders")
public class GetProvidersTest extends RxConciergeAPITestBaseV2 
{
	private static final Logger logger = LoggerFactory.getLogger(GetProviderTest.class);	

	@TestFactory
	@DisplayName("794: GetProviders Body Happy Path with more than 1 values : 3 values")
	public List<DynamicNode> happyPath() throws JsonProcessingException 
	{
		int numberOfRecords = 3;
		return processHappyPath(numberOfRecords);
	}

	@TestFactory
	@DisplayName("795: GetProviders Body Happy Path with more than 1 values : 2 values")
	public List<DynamicNode> happyPathtwo()
	{
		int numberOfRecords = 2;
		return processHappyPath(numberOfRecords);
	}

	@TestFactory
	@DisplayName("796: GetProviders Body Happy Path with 1 values")
	public List<DynamicNode> happyPathOne()
	{
		int numberOfRecords = 1;
		return processHappyPath(numberOfRecords);
	}

	/**
	 * Performs the test given the number of providers to use
	 * @param numberOfProviders to use in the test
	 * @return list of {@link DynamicNode} tests to be performed
	 */
	private List<DynamicNode> processHappyPath(int numberOfProviders)
	{
		// Cosmos db - retrieve random providers
		List<Provider> expectedProviders = ProviderQueries.getProviders().stream()
												   .collect(Collectors.collectingAndThen(Collectors.toList(), collected ->
												   {
													   Collections.shuffle(collected);
													   return collected.stream();
												   }))
												   .limit(numberOfProviders)
												   .collect(Collectors.toList());

		List<String> providerNpis = expectedProviders.stream()
											.map(e -> e.getNpi())
											.collect(Collectors.toList());

		String requestBody = providerNpis.stream().collect(Collectors.joining("\",\"", "\"", "\""));
		requestBody = "[" + requestBody + "]";

		// API Call
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDERS_POST_ENDPOINT, requestBody, null, 200, null);
		apiPostStep.run();

		List<DynamicNode> test = new ArrayList<>(apiPostStep.getTestResults());

		List<Provider> actualProviders = apiPostStep.convertToJsonDTOs(Provider.class);

		// Validation
		for ( Provider expected : expectedProviders)
		{
			boolean found = false;
			for (Provider actual : actualProviders )
			{
				if ( expected.equals(actual) )
				{
					found = true;
					test.add(dynamicContainer("Provider npi [" + expected.getNpi() + "]", expected.compare(actual)));
					break;
				}
			}

			if ( !found )
			{
				test.add(dynamicTest("Provider npi [" + expected.getNpi() + "]", fail("Unable to fund the provider from API response")));
			}
		}

		return test;
	}

	@TestFactory
	@DisplayName("800:GetProviders Invalid Method")
	public List<DynamicNode> invalidRestApiGetMethod()
	{
		Provider provider = ProviderQueries.getRandomProvider();
		String body = "[ \"" + provider.getNpi() + "\" ]";

		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), PROVIDERS_POST_ENDPOINT, null, 404, HTTP_404_RESOURCE_NOT_FOUND);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("799: GetProviders Invalid Auth")
	public List<DynamicNode> invalidAuthentication()
	{
		Provider provider = ProviderQueries.getRandomProvider();
		String body = "[ \"" + provider.getNpi() + "\" ]";

		ApiPostStep apiPostStep = new ApiPostStep(getHeadersInvalidAuth(), PROVIDERS_POST_ENDPOINT, body, null, 401, HTTP_401_UNAUTHORIZED);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

}
