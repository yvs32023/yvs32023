/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.tests.tenant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Cosmos DB | Database id: rxcc-shared | Container id: tenant
 *
 * /tenants
 * Get all user group tenants.
 *
 * Request
 * GET https://apim-lbs-rxc-tst-east-001.azure-api.net/api/tenant/tenants
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/15/2022
 */
@Tag("ALL")
@Tag("TENANT")
@DisplayName("GetTenants")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class GetTenantsTest extends RxConciergeAPITestBaseV2 
{

	private static final Logger logger = LoggerFactory.getLogger(GetTenantsTest.class);

	@TestFactory
	@DisplayName("3385: GetTenants Happy Path")
	public List<DynamicNode> happyPath() throws JsonProcessingException 
	{
		logger.debug("Started GetTenantsTest.happyPath");

		/*
		 * Call API
		 */
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), TENANTS_GET_ENDPOINT, null, 200, null);
		apiGetStep.run();
		List<Tenant> actual = apiGetStep.convertToJsonDTOs(Tenant.class);

		if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED )
		{
			return apiGetStep.getTestResults();
		}

		/*
		 * GC (10/25/22) Query Cosmos based on the user role
		 */
		List<Tenant> expectedTenants = new ArrayList<>();

		if ( StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_multi") )
		{
			expectedTenants = TenantQueries.getTenants();
		}
		else if ( StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_single") )
		{
			expectedTenants = TenantQueries.getTenants().stream()
									  .filter( tenant -> tenant.getSubscriptionName().equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()))
									  .collect(Collectors.toList());
		}
		else if ( StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_loa") )
		{
			expectedTenants = TenantQueries.getTenants().stream()
									  .filter( tenant -> tenant.getSubscriptionName().equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()))
									  .collect(Collectors.toList());
		}
		else if ( StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_med") )
		{
			expectedTenants = TenantQueries.getTenants().stream()
									  .filter( tenant -> tenant.getSubscriptionName().equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()))
									  .collect(Collectors.toList());
		}

		/*
		 * Validate
		 */
		List<Tenant> expected = new ArrayList<Tenant>(expectedTenants);
		if ( expected == null && actual != null ) {
			apiGetStep.getTestResults().add(dynamicTest("Tenants", () -> fail("Cosmos query returned null result but API returned " + actual.size() + " tenants")));
			return apiGetStep.getTestResults();
		}
		else if ( expected != null && actual == null ) {
			apiGetStep.getTestResults().add(dynamicTest("Tenants", () -> fail("Cosmos query returned " + expected.size() + " tenants but API returned null")));
			return apiGetStep.getTestResults();
		}

		apiGetStep.getTestResults().add(dynamicTest("Tenant count - Actual Size :"  + actual.size() + " Expected Size :" + expected.size()  , () -> assertEquals(expected.size(), actual.size())));  // size


		for ( Tenant theExpected : expected )
		{
			boolean found = false;
			for ( Tenant theActual : actual )
			{
				if ( theExpected.equals(theActual) ) {
					apiGetStep.getTestResults().add(dynamicContainer(theExpected.getTenantName().toUpperCase(), theExpected.compare(theActual)));
					found = true;
					break;
				}
			}

			if ( !found ) {
				apiGetStep.getTestResults().add(dynamicTest(theExpected.getTenantName(), () -> fail("Tenant was not found")));
			}
		}

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("3392: GetTenants Invalid Auth")
	public List<DynamicNode> invalidTokenRestGet()
	{
		ApiGetStep apiGetStep = new ApiGetStep(getHeadersInvalidAuth(), TENANTS_GET_ENDPOINT,null,401, HTTP_401_UNAUTHORIZED);
		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("3393: GetTenants Invalid Method")
	public List<DynamicNode> invalidRestApiPostMethod()
	{
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), TENANTS_GET_ENDPOINT,null,null,404, HTTP_404_RESOURCE_NOT_FOUND);
		apiPostStep.run();
		return apiPostStep.getTestResults();
	}

}
