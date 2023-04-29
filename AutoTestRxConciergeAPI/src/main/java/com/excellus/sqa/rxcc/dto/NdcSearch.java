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

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 05/20/2022
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NdcSearch extends AbstractJsonDTO<NdcSearch>
{
	private static final Logger logger = LoggerFactory.getLogger(NdcSearch.class);

	private Integer awpCurrUnitPrice; 
	private String brandNameCode;
	private String gpi2Group;
	private String gpi2GroupCode;
	private String gpi4Class;
	private String gpi4ClassCode;
	private String gpi6Subclass; 
	private String gpi6SubclassCode;
	private String gpi8BaseName;
	private String gpi8BaseNameCode;
	private String gpi10GenericName;
	private String gpi10GenericNameCode;
	private String gpi12GenericNameDoseForm;
	private String gpi12GenericNameDoseFormCode;
	private String gpi14Name; 
	private String gpi14Unformatted;
	private String labeler;
	private String ndc; 
	private String productName;
	private String wacCurrentUnitPrice;
	private String marketingCategory;
	private String gpi0CategoryRange;

	
	/*
	 * Validations
	 */

	/**
	 * Check if the ndc information contains the search terms.
	 *
	 * @return true if the search terms are found in provider's information. Otherwise, it returns false.
	 */
	public boolean ndcContains(List<NdcSearch> drugs)
	{
		logger.debug("Starting ndc search response validation");
		
		
		for (NdcSearch drug : drugs)
		{
			if (this.compareDrugs(drug))
				return true;
			
		}
		return false;

	}	


	/**
	 * Compare two objects
	 * 
	 * @param ndcSearch
	 * @return
	 */
	public boolean compareDrugs(NdcSearch ndcSearch)
	{
		logger.debug("Starting ndc search response validation");

		Boolean compareCheck = true;

		if (ndcSearch.getAwpCurrUnitPrice() != this.awpCurrUnitPrice) 
			return false;

		if  ( !StringUtils.equalsIgnoreCase(ndcSearch.getBrandNameCode(),this.brandNameCode) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getGpi2Group(),this.gpi2Group) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getGpi2GroupCode(),this.gpi2GroupCode) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getGpi8BaseName(),this.gpi8BaseName) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getGpi8BaseNameCode(),this.gpi8BaseNameCode) )
			return false;			

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getProductName(),this.productName) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getMarketingCategory(),this.marketingCategory) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getGpi4Class(),this.gpi4Class) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getGpi4ClassCode(),this.gpi4ClassCode) )
			return false;
		
		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getGpi6Subclass(),this.gpi6Subclass) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getGpi6SubclassCode(),this.gpi6SubclassCode) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getGpi10GenericName(),this.gpi10GenericName) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getGpi10GenericNameCode(),this.gpi10GenericNameCode) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getGpi12GenericNameDoseForm(),this.gpi12GenericNameDoseForm) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getGpi12GenericNameDoseFormCode(),this.gpi12GenericNameDoseFormCode) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getGpi14Unformatted(),this.gpi14Unformatted) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getLabeler(),this.labeler) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getNdc(),this.ndc) )
			return false;
		
		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getWacCurrentUnitPrice(),this.wacCurrentUnitPrice) )
			return false;

		if ( !StringUtils.equalsIgnoreCase(ndcSearch.getGpi0CategoryRange(),this.gpi0CategoryRange) )
			return false;
	
		return compareCheck;

	}


	/**
	 * Compare two objects
	 * 
	 * @param ndcSearch
	 * @return
	 */
	public List<DynamicNode> compare(NdcSearch ndcSearch)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("awpCurrUnitPrice: [" + awpCurrUnitPrice + "]", () -> assertEquals(awpCurrUnitPrice, ndcSearch.getAwpCurrUnitPrice(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("brandNameCode: [" + brandNameCode + "]", () -> assertEquals(brandNameCode, ndcSearch.getBrandNameCode(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi2Group: [" + gpi2Group + "]", () -> assertEquals(gpi2Group, ndcSearch.getGpi2Group(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi2GroupCode: [" + gpi2GroupCode + "]", () -> assertEquals(gpi2GroupCode, ndcSearch.getGpi2GroupCode(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi4Class: [" + gpi4Class + "]", () -> assertEquals(gpi4Class, ndcSearch.getGpi4Class(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi4ClassCode: [" + gpi4ClassCode + "]", () -> assertEquals(gpi4ClassCode, ndcSearch.getGpi4ClassCode(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi6Subclass: [" + gpi6Subclass + "]", () -> assertEquals(gpi6Subclass, ndcSearch.getGpi6Subclass(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi6SubclassCode: [" + gpi6SubclassCode + "]", () -> assertEquals(gpi6SubclassCode, ndcSearch.getGpi6SubclassCode(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi8BaseName: [" + gpi8BaseName + "]", () -> assertEquals(gpi8BaseName, ndcSearch.getGpi8BaseName(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi8BaseNameCode: [" + gpi8BaseNameCode + "]", () -> assertEquals(gpi8BaseNameCode, ndcSearch.getGpi8BaseNameCode(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi10GenericName: [" + gpi10GenericName + "]", () -> assertEquals(gpi10GenericName, ndcSearch.getGpi10GenericName(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi10GenericNameCode: [" + gpi10GenericNameCode + "]", () -> assertEquals(gpi10GenericNameCode, ndcSearch.getGpi10GenericNameCode(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi12GenericNameDoseForm: [" + gpi12GenericNameDoseForm + "]", () -> assertEquals(gpi12GenericNameDoseForm, ndcSearch.getGpi12GenericNameDoseForm(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi12GenericNameDoseFormCode: [" + gpi12GenericNameDoseFormCode + "]", () -> assertEquals(gpi12GenericNameDoseFormCode, ndcSearch.getGpi12GenericNameDoseFormCode(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi14Name: [" + gpi14Name + "]", () -> assertEquals(gpi14Name, ndcSearch.getGpi14Name(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi14Unformatted: [" + gpi14Unformatted + "]", () -> assertEquals(gpi14Unformatted, ndcSearch.getGpi14Unformatted(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("labeler: [" + labeler + "]", () -> assertEquals(labeler, ndcSearch.getLabeler(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("ndc: [" + ndc + "]", () -> assertEquals(ndc, ndcSearch.getNdc(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("productName: [" + productName + "]", () -> assertEquals(productName, ndcSearch.getProductName(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("wacCurrentUnitPrice: [" + wacCurrentUnitPrice + "]", () -> assertEquals(wacCurrentUnitPrice, ndcSearch.getWacCurrentUnitPrice(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("marketingCategory: [" + marketingCategory + "]", () -> assertEquals(marketingCategory, ndcSearch.getMarketingCategory(), getApiInfo(ndcSearch))));
		tests.add(dynamicTest("gpi0CategoryRange: [" + gpi0CategoryRange + "]", () -> assertEquals(gpi0CategoryRange, ndcSearch.getGpi0CategoryRange(), getApiInfo(ndcSearch))));


		return tests;
	}

	/*
	 * Setter / Getter
	 */

	public String getBrandNameCode() {
		return brandNameCode;
	}
	
	public void setBrandNameCode(String brandNameCode) {
		this.brandNameCode = brandNameCode;
	}

	public String getGpi2Group() {
		return gpi2Group;
	}
	
	public void setGpi2Group(String gpi2Group) {
		this.gpi2Group = gpi2Group;
	}

	public String getGpi2GroupCode() {
		return gpi2GroupCode;
	}
	public void setGpi2GroupCode(String gpi2GroupCode) {
		this.gpi2GroupCode = gpi2GroupCode;
	}

	public String getGpi4Class() {
		return gpi4Class;
	}
	
	public void setGpi4Class(String gpi4Class) {
		this.gpi4Class = gpi4Class;
	}

	public String getGpi4ClassCode() {
		return gpi4ClassCode;
	}
	
	public void setGpi4ClassCode(String gpi4ClassCode) {
		this.gpi4ClassCode = gpi4ClassCode;
	}

	public String getGpi6Subclass() {
		return gpi6Subclass;
	}
	
	public void setGpi6Subclass(String gpi6Subclass) {
		this.gpi6Subclass = gpi6Subclass;
	}

	public String getGpi6SubclassCode() {
		return gpi6SubclassCode;
	}
	
	public void setGpi6SubclassCode(String gpi6SubclassCode) {
		this.gpi6SubclassCode = gpi6SubclassCode;
	}

	public String getGpi8BaseName() {
		return gpi8BaseName;
	}
	
	public void setGpi8BaseName(String gpi8BaseName) {
		this.gpi8BaseName = gpi8BaseName;
	}

	public String getGpi8BaseNameCode() {
		return gpi8BaseNameCode;
	}
	
	public void setGpi8BaseNameCode(String gpi8BaseNameCode) {
		this.gpi8BaseNameCode = gpi8BaseNameCode;
	}

	public String getGpi10GenericName() {
		return gpi10GenericName;
	}
	
	public void setGpi10GenericName(String gpi10GenericName) {
		this.gpi10GenericName = gpi10GenericName;
	}

	public String getGpi10GenericNameCode() {
		return gpi10GenericNameCode;
	}
	
	public void setGpi10GenericNameCode(String gpi10GenericNameCode) {
		this.gpi10GenericNameCode = gpi10GenericNameCode;
	}

	public String getGpi12GenericNameDoseForm() {
		return gpi12GenericNameDoseForm;
	}
	
	public void setGpi12GenericNameDoseForm(String gpi12GenericNameDoseForm) {
		this.gpi12GenericNameDoseForm = gpi12GenericNameDoseForm;
	}

	public String getGpi12GenericNameDoseFormCode() {
		return gpi12GenericNameDoseFormCode;
	}
	
	public void setGpi12GenericNameDoseFormCode(String gpi12GenericNameDoseFormCode) {
		this.gpi12GenericNameDoseFormCode = gpi12GenericNameDoseFormCode;
	}

	public String getGpi14Name() {
		return gpi14Name;
	}
	public void setGpi14Name(String gpi14Name) {
		this.gpi14Name = gpi14Name;
	}

	public String getGpi14Unformatted() {
		return gpi14Unformatted;
	}
	
	public void setGpi14Unformatted(String gpi14Unformatted) {
		this.gpi14Unformatted = gpi14Unformatted;
	}

	public String getLabeler() {
		return labeler;
	}
	
	public void setLabeler(String labeler) {
		this.labeler = labeler;
	}

	public String getNdc() {
		return ndc;
	}
	
	public void setNdc(String ndc) {
		this.ndc = ndc;
	}

	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getWacCurrentUnitPrice() {
		return wacCurrentUnitPrice;
	}
	
	public void setWacCurrentUnitPrice(String wacCurrentUnitPrice) {
		this.wacCurrentUnitPrice = wacCurrentUnitPrice;
	}

	public String getMarketingCategory() {
		return marketingCategory;
	}
	
	public void setAppNumber(String marketingCategory) {
		this.marketingCategory = marketingCategory;
	}

	public static Logger getLogger() {
		return logger;
	}

	public String getGpi0CategoryRange() {
		return gpi0CategoryRange;
	}

	public void setGpi0CategoryRange(String gpi0CategoryRange) {
		this.gpi0CategoryRange = gpi0CategoryRange;
	}

	public Integer getAwpCurrUnitPrice() {
		return awpCurrUnitPrice;
	}

	public void setAwpCurrUnitPrice(Integer awpCurrUnitPrice) {
		this.awpCurrUnitPrice = awpCurrUnitPrice;
	}

}
