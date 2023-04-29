/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

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
@JsonPropertyOrder({ "productName",
    "faxAlternativeProductName"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FaxAlternatives extends AbstractJsonDTO<FaxAlternatives>
{


	@JsonProperty(required=true)
	private String productName;
	
	@JsonProperty(required=true)
	private String faxAlternativeProductName;
	
	/**
	 * Default constructor
	 */
	public FaxAlternatives() {

	}
	
	/**
	 * Constructor
	 * @param productName
	 * @param faxAlternativeProductName
	 */
	public FaxAlternatives(String productName,String faxAlternativeProductName) 
	{
		this.productName = productName;
		this.faxAlternativeProductName = faxAlternativeProductName;
	}
	
	/*
	 * Validations
	 */


	/**
	 * Compare faxAlternatives properties defined in the schema
	 * 
	 * @param faxAlternatives {@link FaxAlternatives} to compare with
	 * @return test results of comparison
	 */
	public List<DynamicNode> compare(FaxAlternatives faxAlternatives)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();


		tests.add(dynamicTest("productName: [" + productName + "]", () -> assertEquals(productName, faxAlternatives.getProductName(), getApiInfo(faxAlternatives))));
		tests.add(dynamicTest("faxAlternativeProductName: [" + faxAlternativeProductName + "]", () -> assertEquals(faxAlternativeProductName, faxAlternatives.getFaxAlternativeProductName(), getApiInfo(faxAlternatives))));
		
		return tests;
	}

		
	/*
	 * Helper methods
	 */
	
	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof FaxAlternatives)
		{
			FaxAlternatives faxAlternatives = (FaxAlternatives) obj;

			if ( faxAlternativeProductName.equals(faxAlternatives.getFaxAlternativeProductName()) )
				return true;
		}

		return false;
	}
	
	/*
	 * Setter / Getter
	 */

	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getFaxAlternativeProductName() {
		return faxAlternativeProductName;
	}

	public void setFaxAlternativeProductName(String faxAlternativeProductName) {
		this.faxAlternativeProductName = faxAlternativeProductName;
	}
	
}
