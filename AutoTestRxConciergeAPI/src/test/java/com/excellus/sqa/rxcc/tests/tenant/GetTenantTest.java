/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.tests.tenant;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Tenant;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetTenant")
@UserRole(role = {"RXCC_FULL_SINGLE", "RXCC_FULL_LOA", "RXCC_FULL_MED"})
public class GetTenantTest extends RxConciergeAPITestBaseV2 
{

	private static final Logger logger = LoggerFactory.getLogger(GetTenantTest.class);

	@TestFactory
	@DisplayName("3178: GetTenant Happy Path (ALL Member)")
	@Order(1)
	public List<DynamicNode> happyPathTenant() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();

		try
		{
			List<Tenant> tenants = TenantQueries.getTenants();

			for(Tenant tenant : tenants)
			{
				test.add(dynamicContainer("Subscription name : " + tenant.getSubscriptionName(), getTenantAndValidate(tenant)));
			}
		}

		catch (Exception e)
		{
			test.add(dynamicContainer("Subscription Name",() -> fail("Unexpected exception",e)));
		}
		return test;
	}	

	/**
	 * Perform API call and Cosmos DB query then compare results
	 *
	 * @param tenant to test
	 * @return list of test results represented in {@link DynamicNode}
	 */
	private List<DynamicNode> getTenantAndValidate(Tenant tenant)
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), TENANT_GET_ENDPOINT, new Object[] {tenant.getAdTenantId()}, 200, HTTP_200_OK);;

		/*
		 * GC (10/26/22) Retrieve tenant based on the user role
		 */
		Tenant expected = null;
		if ( StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_multi") ||
			( StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_single") && StringUtils.equalsIgnoreCase(tenant.getSubscriptionName(), Tenant.Type.EHP.getSubscriptionName()) ) ||
			( StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_loa") && StringUtils.equalsIgnoreCase(tenant.getSubscriptionName(), Tenant.Type.LOA.getSubscriptionName()) ) ||
			( StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_med") && StringUtils.equalsIgnoreCase(tenant.getSubscriptionName(), Tenant.Type.MED.getSubscriptionName()) ) )
		{
			expected = tenant;
		}
		else
		{
			// Override the step with expected status code 401 because the user role has no access to given tenant
			apiGetStep = new ApiGetStep(getGenericHeaders(), TENANT_GET_ENDPOINT, new Object[] {tenant.getAdTenantId()}, 401, "HTTP/1.1 401 Unauthorized");
		}

		// Perform API call
		apiGetStep.run();

		if ( apiGetStep.getResponseStatusCode() == 200 )
		{
			Tenant actual = apiGetStep.convertToJsonDTO(Tenant.class);
			apiGetStep.getTestResults().add(dynamicContainer("Tenant validation", expected.compare(actual)));
			return apiGetStep.getTestResults();
		}

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("3450: GetTenant Invalid Auth")
	public List<DynamicNode> invalidTokenRestGet()
	{
		String adTenantId = TenantQueries.getAdTenantIds().get(0);

		ApiGetStep apiGetStep = new ApiGetStep(getHeadersInvalidAuth(), TENANT_GET_ENDPOINT, new Object[]{adTenantId}, 401, HTTP_401_UNAUTHORIZED);
		apiGetStep.run();
		return apiGetStep.getTestResults();
	}


	@TestFactory
	@DisplayName("3451: GetTenant Invalid Method")
	public List<DynamicNode> invalidRestApiPostMethod()
	{
		String adTenantId = TenantQueries.getAdTenantIds().get(0);

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), TENANT_GET_ENDPOINT, null, new Object[]{adTenantId},404, HTTP_404_RESOURCE_NOT_FOUND);
		apiPostStep.run();
		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("3438: GetTenant Invalid Parm")
	public List<DynamicNode> invalidType()
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), TENANT_GET_ENDPOINT, new Object[]{"invalid"}, 404, HTTP_404_NOT_FOUND);
		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("3444: GetTenant Parm Alpha")
	public List<DynamicNode> alphaParm()
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), TENANT_GET_ENDPOINT,
								new Object[]{"abcdefgh-ijkl-mnop-qrst-uvwxyzabcdef"}, 404, HTTP_404_NOT_FOUND);
		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("3443: GetTenant Parm Numerical")
	public List<DynamicNode> numericalParm()
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), TENANT_GET_ENDPOINT,
				new Object[]{"12345678-9012-3456-7890-123456789012"}, 404, HTTP_404_NOT_FOUND);
		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("3445: GetTenant Parm String with Spl Char")
	public List<DynamicNode> specialChar()
	{
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), TENANT_GET_ENDPOINT,
				new Object[]{"5ebc@f4d#a684-4@93-9c36-!de$f5^8&83*"}, 404, HTTP_404_NOT_FOUND);
		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

}
