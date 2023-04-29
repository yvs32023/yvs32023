/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Tenant;

/**
 * 
 * 
 * @author Garrett Cosmiano (gcosmian)
 * @since 01/12/2022
 */
@UserRole(role = {"SSO", "RXCC_FULL_MULTI"})
@TestMethodOrder(OrderAnnotation.class)
@Tag("DUMMY")

public class ApiTest extends RxConciergeAPITestBaseV2 
{
	
	@Test
	@Order(1)
	public void loginSuccessful()
	{
		assertTrue(StringUtils.isNotBlank(RxConciergeUILogin.getAcctName()));
		assertTrue(StringUtils.isNotBlank(RxConciergeUILogin.getAcctName()));
	}

	@TestFactory
	@Order(2)
	public List<DynamicNode> exampleRestTest() 
	{
		List<DynamicNode> test = new ArrayList<DynamicNode>();
		
		List<Tenant> tenants = TenantQueries.getTenants();
		assertTrue(tenants.size() > 0, "Unable to get any tenant");
		
		Tenant tenant = tenants.get(0);
		
		Tenant actual = super.restGet(getGenericHeaders(), TENANT_GET_ENDPOINT, new Object[] {tenant.getAdTenantId()}, Tenant.class, 200, null);
		
		test.add(dynamicContainer("Tenant validation", tenant.compare(actual)));
		
		return test;
	}
}

