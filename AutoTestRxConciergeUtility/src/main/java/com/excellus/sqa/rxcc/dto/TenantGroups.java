/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicNode;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 06/07/2022
 */
@JsonPropertyOrder({ "adTenantId",
"rxccGroupName"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantGroups  extends AbstractJsonDTO<TenantGroups>
{

	@JsonProperty(required=true)
	private String adTenantId;

	@JsonProperty(required=true)
	private List <String> rxccGroupName;

	/**
	 * Default constructor
	 */
	public TenantGroups() {

	}

	/**
	 * Constructor
	 * @param adTenantId
	 * @param rxccGroupName
	 */	
	public TenantGroups(String adTenantId, List<String> rxccGroupName) {
		this.adTenantId = adTenantId;
		this.rxccGroupName = rxccGroupName;
	}


	/*
	 * Validations
	 */

	/**
	 * Compare two objects
	 * 
	 * @param tenantGroups
	 * @return
	 */
	public List<DynamicNode> compare(TenantGroups tenantGroups)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("adTenantId [" + adTenantId + "]", () -> assertEquals(adTenantId, tenantGroups.getAdTenantId(), getApiInfo(tenantGroups))));

		tests.add(dynamicTest("rxccGroupName [" + rxccGroupName + "]", () -> assertThat(tenantGroups.getRxccGroupName(), containsInAnyOrder(rxccGroupName.toArray(new String[rxccGroupName.size()])) ) ));

		return tests;
	}


	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof TenantGroups)
		{
			TenantGroups tenantGroups = (TenantGroups) obj;

			if ( rxccGroupName.equals(tenantGroups.getRxccGroupName())  )
				return true;
		}

		return false;
	}

	/*
	 * Setter / Getter
	 */
	public String getAdTenantId() {
		return adTenantId;
	}

	public void setAdTenantId(String adTenantId) {
		this.adTenantId = adTenantId;
	}

	public List<String> getRxccGroupName() {
		return rxccGroupName;
	}

	public void setRxccGroupName(List<String> rxccGroupName) {
		this.rxccGroupName = rxccGroupName;
	}

}
