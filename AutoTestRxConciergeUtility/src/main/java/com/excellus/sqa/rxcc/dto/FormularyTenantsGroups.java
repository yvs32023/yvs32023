/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
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
@JsonPropertyOrder({ "formularyId","formularyCode","formularyDescription", "tenantGroups"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormularyTenantsGroups extends AbstractJsonDTO<FormularyTenantsGroups>
{

	@JsonProperty(required=true)
	private String  formularyId;

	private String  formularyCode;

	private String  formularyDescription;

	@JsonProperty(required=true)
	private List<TenantGroups> tenantGroups;

	/**
	 * Default constructor
	 */
	public FormularyTenantsGroups() {

	}

	/**
	 * Constructor
	 * @param formularyCode
	 * @param tenantGroups
	 */
	public FormularyTenantsGroups(String formularyId, String formularyCode, String formularyDescription,
			List<TenantGroups> tenantGroups) 
	{
		this.formularyId = formularyId;
		this.formularyCode = formularyCode;
		this.formularyDescription = formularyDescription;
		this.tenantGroups = tenantGroups;
	}

	/*
	 * Validations
	 */
	/**
	 * Compare formularyTenantsGroups properties defined in the schema
	 * 
	 * @param formularyTenantsGroups {@link FormularyTenantsGroups} to compare with
	 * @return test results of comparison
	 */
	public List<DynamicNode> compare(FormularyTenantsGroups formularyTenantsGroups)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();


		tests.add(dynamicTest("formularyCode [" + formularyCode + "]", () -> assertEquals(formularyCode, formularyTenantsGroups.getFormularyCode(), getApiInfo(formularyTenantsGroups))));


		tests.add(dynamicTest("TenantGroups (count) [" + tenantGroups.size() + "]", () -> assertEquals(tenantGroups.size(), formularyTenantsGroups.getTenantGroups().size(), getApiInfo(formularyTenantsGroups))));

		/*
		 * Validate each of the tenantGroups
		 */

		for  ( TenantGroups expected : tenantGroups )
		{
			boolean found = false;
			for ( TenantGroups actual : formularyTenantsGroups.getTenantGroups())
			{
				if ( expected.equals(actual) )
				{
					found = true;

					tests.add(dynamicContainer("TenantGroups [" + formularyCode + "]", expected.compare(actual)) );	// compare the tenantGroups

					break;
				}
			}

			if ( !found ) {
				tests.add(dynamicTest("TenantGroups not found", () -> fail("The tenantGroups with ID " + formularyCode + " is missing \n" + getApiInfo(formularyTenantsGroups))));
			}
		}

		return tests;
	}


	/*
	 * Helper methods
	 */

	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof FormularyTenantsGroups)
		{
			FormularyTenantsGroups formularyTenantsGroups = (FormularyTenantsGroups) obj;

			if ( tenantGroups.equals(formularyTenantsGroups.getTenantGroups()) )
				return true;
		}

		return false;
	}

	/*
	 * Setter / Getter
	 */
	public String getFormularyId() {
		return formularyId;
	}

	public void setFormularyId(String formularyId) {
		this.formularyId = formularyId;
	}

	public String getFormularyDescription() {
		return formularyDescription;
	}

	public void setFormularyDescription(String formularyDescription) {
		this.formularyDescription = formularyDescription;
	}

	public String getFormularyCode() {
		return formularyCode;
	}

	public void setFormularyCode(String formularyCode) {
		this.formularyCode = formularyCode;
	}

	public List<TenantGroups> getTenantGroups() {
		return tenantGroups;
	}

	public void setTenantGroups(List<TenantGroups> tenantGroups) {
		this.tenantGroups = tenantGroups;
	}


}
