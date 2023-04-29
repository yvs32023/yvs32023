/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.Utility;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.pages.home.TenantPO;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;

/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 01/17/2022
 */
@UserRole(role = {"SSO", "RXCC_FULL_MULTI"})
@Tag("ALL")
@Tag("TENANT")
@Tag("TENANT_SEARCH")
@Tag("SEARCH")
public class TenantSearchTest extends RxConciergeUITestBase
{

	//10.27 HZ updated tests to include OOA and Medicare, also added new check to validate phone and fax
	
	@ParameterizedTest(name = "{0}")
	@MethodSource("getTenantNames")
	public void searchTenant(String tenantName) throws InterruptedException, ElementNotFoundException 
	{
		PageConfiguration loginPage = (PageConfiguration) BeanLoader.loadBean("homePage");
		TenantPO Tenant = new TenantPO(driverBase.getWebDriver(), loginPage);
		String ActualTenantName=Tenant.searchTenant(tenantName);
		assertEquals(tenantName,ActualTenantName);
	}

	static Stream<String> getTenantNames()
	{
		return TenantQueries.getTenants().stream()
					   .map(Tenant::getTenantName)
					   .collect(Collectors.toList())
					   .stream();
	}
	
	@ParameterizedTest(name = "{0} - Phone {1}")
	@MethodSource("getPhones")
	public void verifyTenantPhone(String tenantName, String tenantPhone) throws InterruptedException, ElementNotFoundException
	{
		PageConfiguration loginPage = (PageConfiguration) BeanLoader.loadBean("homePage");
		TenantPO Tenant = new TenantPO(driverBase.getWebDriver(), loginPage);
		Tenant.searchTenant(tenantName);
		String listTenantPhone=Tenant.retrieveTenantPhoneNumber();
		assertEquals("Phone: "+ tenantPhone, listTenantPhone);
	}

	static Stream<Arguments> getPhones()
	{
		List<Arguments> args = new ArrayList<>();

		TenantQueries.getTenants().stream()
				.forEach(tenant -> args.add(arguments(tenant.getTenantName(), Utility.normalizePhoneNumber(tenant.getPhoneNumber()))) );

		return args.stream();
	}
	
	@ParameterizedTest(name = "{0} - Fax {1}")
	@MethodSource("getFaxes")
	public void verifyTenantFax(String tenantName, String tenantFax) throws InterruptedException, ElementNotFoundException
	{
		PageConfiguration loginPage = (PageConfiguration) BeanLoader.loadBean("homePage");
		TenantPO Tenant = new TenantPO(driverBase.getWebDriver(), loginPage);
		Tenant.searchTenant(tenantName);
		String listTenantFax=Tenant.retrieveTenantFaxNumber();
		assertEquals("Fax: " + tenantFax, listTenantFax);
		
	}

	static Stream<Arguments> getFaxes()
	{
		List<Arguments> args = new ArrayList<>();

		TenantQueries.getTenants().stream()
				.forEach(tenant -> args.add(arguments(tenant.getTenantName(), Utility.normalizePhoneNumber(tenant.getFaxNumber()))) );

		return args.stream();
	}

	@Test
	public void searchMultipleTenant() throws ElementNotFoundException {

		String searchTerm = "Excellus";

		List<String> expected = TenantQueries.getTenants().stream()
										.filter(tenant -> StringUtils.containsIgnoreCase(tenant.getTenantName(), searchTerm))
										.map(Tenant::getTenantName)
										.collect(Collectors.toList());

		PageConfiguration loginPage = (PageConfiguration) BeanLoader.loadBean("homePage");
		TenantPO Tenant = new TenantPO(driverBase.getWebDriver(), loginPage);
		Tenant.searchTenant(searchTerm);
		List<String> listTenantCard=Tenant.retrieveTenants();
		assertThat(listTenantCard, containsInAnyOrder( expected.toArray() ));
	}

}