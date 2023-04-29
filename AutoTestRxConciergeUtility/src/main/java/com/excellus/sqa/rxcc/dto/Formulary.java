/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicNode;
import org.junit.platform.commons.util.StringUtils;

import com.excellus.sqa.rxcc.Utility;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 06/20/2022
 */
@JsonPropertyOrder({ "formularyId",  "type", "formularyCode",  "formularyDescription"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Formulary extends CommonItem {

	@JsonProperty(required=true)
	private String  formularyId;

	@JsonProperty(required=true)
	private String  type;   //constant

	@JsonProperty(required=true)
	private String  formularyCode;

	@JsonProperty(required=true)
	private String  formularyDescription;


	/*
	 * Validations
	 */

	/**
	 * Compare formulary properties defined in the schema
	 * 
	 * @param formulary {@link formulary} to compare with
	 * @return test results of comparison
	 */
	public List<DynamicNode> compare(Formulary formulary)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("formularyId [" + formularyId + "]", () -> assertEquals(formularyId, formulary.getFormularyId(), getApiInfo(formulary))));
		tests.add(dynamicTest("type [" + type + "]", () -> assertEquals(type, formulary.getType(), getApiInfo(formulary))));
		tests.add(dynamicTest("formularyCode [" + formularyCode + "]", () -> assertEquals(formularyCode, formulary.getFormularyCode(), getApiInfo(formulary))));
		tests.add(dynamicTest("formularyDescription [" + formularyDescription + "]", () -> assertEquals(formularyDescription, formulary.getFormularyDescription(), getApiInfo(formulary))));
		tests.add(dynamicTest("createdBy [" + createdBy + "]", () -> assertEquals(createdBy, formulary.getCreatedBy(), getApiInfo(formulary))));

		// Perform date validation			
		if ( StringUtils.isNotBlank(createdDateTime) && StringUtils.isNotBlank(formulary.getCreatedDateTime()) )
		{
			tests.add(compareDates("createdDateTime: [" + createdDateTime + "]", createdDateTime, formulary.getCreatedDateTime(),
					new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, getApiInfo(formulary)));
		}
		else
		{
			tests.add(dynamicTest("createdDateTime: [" + createdDateTime + "]", () -> assertEquals(createdDateTime, formulary.getCreatedDateTime(), getApiInfo(formulary))));
		}


		tests.add(dynamicTest("lastUpdatedBy [" + lastUpdatedBy + "]", () -> assertEquals(lastUpdatedBy, formulary.getLastUpdatedBy(), getApiInfo(formulary))));

		// Perform date validation	
		if ( StringUtils.isNotBlank(lastUpdatedDateTime) && StringUtils.isNotBlank(formulary.getLastUpdatedDateTime()) )
		{
			tests.add(compareDates("lastUpdatedDateTime: [" + lastUpdatedDateTime + "]", lastUpdatedDateTime, formulary.getLastUpdatedDateTime(),
					new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, getApiInfo(formulary)));
		}
		else
		{
			tests.add(dynamicTest("lastUpdatedDateTime [" + lastUpdatedDateTime + "]", () -> assertEquals(lastUpdatedDateTime, formulary.getLastUpdatedDateTime(), getApiInfo(formulary))));
		}


		tests.add(dynamicTest("version [" + version + "]", () -> assertEquals(version, formulary.getVersion(), getApiInfo(formulary))));

		// Validate id value if provided
		if ( StringUtils.isNotBlank(id) )
		{
			tests.add(dynamicTest("id [" + id + "]", () -> assertEquals(id, formulary.getId(), getApiInfo(formulary))));
		}
		// validate the actual member id is not null
		else
		{
			tests.add(dynamicTest("id (not null)", () -> assertNotNull(formulary.getId(), "id must not null: " + getApiInfo(formulary))));
		}

		return tests;
	}
	
    /**
     * Compare UI formulary properties defined in the schema
     * @ntagore 11/04/22
     * @param formulary {@link formulary} to compare with
     * @return test results of comparison
     */
    public List<DynamicNode> compareUI(Formulary formulary)
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        tests.add(dynamicTest("formularyCode [" + formularyCode + "]", () -> assertEquals(formularyCode, formulary.getFormularyCode())));
        tests.add(dynamicTest("formularyDescription [" + formularyDescription + "]", () -> assertEquals(formularyDescription, formulary.getFormularyDescription())));
        tests.add(dynamicTest("createdBy [" + createdBy + "]", () -> assertEquals(createdBy, formulary.getCreatedBy())));
        tests.add(dynamicTest("lastUpdatedBy [" + lastUpdatedBy + "]", () -> assertEquals(lastUpdatedBy, formulary.getLastUpdatedBy())));       
        tests.add(dynamicTest("createdDateTime: [" + createdDateTime + "]", 
                () -> assertEquals(Utility.convertCosmosDateToUI(createdDateTime, "MM/dd/YYYY"), Utility.convertCosmosDateToUI(formulary.getCreatedDateTime(), "MM/dd/YYYY")    )));
        tests.add(dynamicTest("lastUpdatedDateTime: [" + lastUpdatedDateTime + "]", 
                () -> assertEquals(Utility.convertCosmosDateToUI(lastUpdatedDateTime, "MM/dd/YYYY"), Utility.convertCosmosDateToUI(formulary.getLastUpdatedDateTime(), "MM/dd/YYYY") )));
        
        return tests;
    }


	/*
	 * Helper methods
	 */
	
	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof Formulary)
		{
			Formulary formulary = (Formulary) obj;

			if ( id.equals(formulary.getId()) )
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFormularyCode() {
		return formularyCode;
	}

	public void setFormularyCode(String formularyCode) {
		this.formularyCode = formularyCode;
	}

	public String getFormularyDescription() {
		return formularyDescription;
	}

	public void setFormularyDescription(String formularyDescription) {
		this.formularyDescription = formularyDescription;
	}

}
