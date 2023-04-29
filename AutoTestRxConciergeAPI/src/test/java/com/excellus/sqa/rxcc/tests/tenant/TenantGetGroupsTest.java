/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.tenant;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.TenantGroupQueries;
import com.excellus.sqa.rxcc.dto.TenantGetGroups;
import com.excellus.sqa.rxcc.dto.TenantSubscription;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Headers;

/**
 * Get all tenant type groups the user has access to on the users JWT access token.
 * 
 * GET  https://apim-lbs-rxc-dev-east-001.azure-api.net/api/tenant/groups/{formularyCode}
 * Request Parameters :  formularyCode 
 * 
 * @author Manish Sharma (msharma)
 * @since 05/18/2022
 */
//@Tag("ALL")
//@Tag("TENANT")
//@Tag("GROUP")
//@DisplayName("GetTenantGroups")
public class TenantGetGroupsTest extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(TenantGetGroupsTest.class);
	@TestFactory
	@DisplayName("17454: GetGroups Tenant EXE Happy Path")
	@Order(1)
	@Deprecated
	public List<DynamicNode> happyPathTenantExeGetGroups() throws JsonProcessingException 
	{

		List<DynamicNode> test = new ArrayList<DynamicNode>();

		//GET random fomulary Code from cosmos db for the EHP adTenantId access
		String formularyCode = TenantGroupQueries.getTenantGetGroupsFormularyCode(TenantSubscription.EXE.getAdTenantId()); 

		//Space for query alignment
		String whereClauseExtension = " where c.formularyCode = \'" + formularyCode + "\'" ;

		//Query Cosmos db
		List<TenantGetGroups> expectedGetGroups = TenantGroupQueries.getGroupsEXETenant(whereClauseExtension);

		//Call API
		List<TenantGetGroups> actualGetGroups = getGroupTenantAPI(expectedGetGroups.get(0).getFormularyCode());

		//Validation
		for ( TenantGetGroups expected : expectedGetGroups)
		{
			boolean found = false;
			for (TenantGetGroups actual : actualGetGroups ) 
			{
				if ( expected.equals(actual) )
				{
					found = true;
					test.add(dynamicContainer("TenantGetGroups formularyCode [" + expected.getFormularyCode() + "]", expected.compare(actual)));
					break;
				}
			}
			if ( !found )
			{
				test.add(dynamicTest("TenantGetGroups formularyCode  [" + expected.getFormularyCode() + "]", fail("Unable to find the GetGroups from API response")));
			}
		}
		return test;
	}

	@Deprecated
	@TestFactory
	@DisplayName("17454: GetGroups Tenant EHP Happy Path")
	@Order(2)
	public List<DynamicNode> happyPathTenantEhpGetGroups() throws JsonProcessingException 
	{

		List<DynamicNode> test = new ArrayList<DynamicNode>();

		//GET random fomulary Code from cosmos db for the EHP adTenantId access
		String formularyCode = TenantGroupQueries.getTenantGetGroupsFormularyCode(TenantSubscription.EHP.getAdTenantId()); 

		//Space for query alignment
		String whereClauseExtension = " where c.formularyCode = \'" + formularyCode + "\'" ;

		//Query Cosmos db
		List<TenantGetGroups> expectedGetGroups = TenantGroupQueries.getGroupsEHPTenant(whereClauseExtension);

		//Call API
		List<TenantGetGroups> actualGetGroups = getGroupTenantAPI(expectedGetGroups.get(0).getFormularyCode());

		//Validation
		for ( TenantGetGroups expected : expectedGetGroups)
		{
			boolean found = false;
			for (TenantGetGroups actual : actualGetGroups ) 
			{
				if ( expected.equals(actual) )
				{
					found = true;
					test.add(dynamicContainer("TenantGetGroups formularyCode [" + expected.getFormularyCode() + "]", expected.compare(actual)));
					break;
				}
			}
			if ( !found )
			{
				test.add(dynamicTest("TenantGetGroups formularyCode  [" + expected.getFormularyCode() + "]", fail("Unable to find the GetGroups from API response")));
			}
		}
		return test;
	}

	@Deprecated
	@Test
	@DisplayName("17456: GetGroups Invalid Method")
	@Order(3)
	public void invalidRestApiPostMethod()
	{

		String formularyCode = TenantGroupQueries.getTenantGetGroupsFormularyCode(TenantSubscription.EXE.getAdTenantId());
		Headers headers = getGenericHeaders();
		invalidRestPost(TENANT_GET_GROUPS_ENDPOINT, headers, 404, HTTP_404_RESOURCE_NOT_FOUND,formularyCode);
	}

	@Deprecated
	@Test
	@DisplayName("17460: GetGroups Invalid Auth")
	@Order(4)
	public void invalidAuth()
	{

		String formularyCode = TenantGroupQueries.getTenantGetGroupsFormularyCode(TenantSubscription.EHP.getAdTenantId());
		invalidTokenRestGet(TENANT_GET_GROUPS_ENDPOINT,formularyCode);
	}

	/**
	 * Call the API to get tenant get groups
	 * 
	 * @author msharma 
	 * 
	 * @param formularyCode of the group
	 * 
	 * @return {@link GetGroupsTenant}
	 */
	private List<TenantGetGroups> getGroupTenantAPI(String formularyCode )
	{
		logger.debug("Starting API call");

		Headers headers = getGenericHeaders();

		List<TenantGetGroups> getGroupsTenant = super.restGets(headers, TENANT_GET_GROUPS_ENDPOINT, new Object[] {formularyCode}, TenantGetGroups.class, 200, null);

		logger.debug("API call completed with the response:\n" + getGroupsTenant.toString());
		return getGroupsTenant;
	}

}
