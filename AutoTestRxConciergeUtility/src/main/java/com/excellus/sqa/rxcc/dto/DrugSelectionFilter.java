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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicNode;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 06/07/2022
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DrugSelectionFilter extends AbstractJsonDTO<DrugSelectionFilter>
{


	private SearchBy searchBy;   //enum

	private String search;

	private List<BgtInd> bgtInd;  //enum 

	private String genericDrugName;

	private String productName;

	private String manufacturer;

	private List<RxOTC> rxOTC; //enum

	private List <String> fdaCode;

	private List <String> dosageForm;

	/**
	 * Default constructor
	 */
	public DrugSelectionFilter() {

	}

	public DrugSelectionFilter(SearchBy searchBy, String search, List<BgtInd> bgtInd, String genericDrugName,
			String productName, String manufacturer, List<RxOTC> rxOTC, List<String> fdaCode, List<String> dosageForm)
	{

		this.searchBy = searchBy;
		this.search = search;
		this.bgtInd = bgtInd;
		this.genericDrugName = genericDrugName;
		this.productName = productName;
		this.manufacturer = manufacturer;
		this.rxOTC = rxOTC;
		this.fdaCode = fdaCode;
		this.dosageForm = dosageForm;
	}

	/*
	 * Validations
	 */

	/**
	 * Compare two objects
	 * 
	 * @param drugSelectionFilter
	 * @return
	 */
	public List<DynamicNode> compare(DrugSelectionFilter drugSelectionFilter)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("searchBy [" + searchBy + "]", () -> assertEquals(searchBy, drugSelectionFilter.getSearchBy(), getApiInfo(drugSelectionFilter))));
		tests.add(dynamicTest("search [" + search + "]", () -> assertEquals(search, drugSelectionFilter.getSearch(), getApiInfo(drugSelectionFilter))));
		tests.add(dynamicTest("bgtInd [" + bgtInd + "]", () -> assertEquals(bgtInd, drugSelectionFilter.getBgtInd(), getApiInfo(drugSelectionFilter))));
		tests.add(dynamicTest("genericDrugName [" + genericDrugName + "]", () -> assertEquals(genericDrugName, drugSelectionFilter.getGenericDrugName(), getApiInfo(drugSelectionFilter))));
		tests.add(dynamicTest("productName [" + productName + "]", () -> assertEquals(productName, drugSelectionFilter.getProductName(), getApiInfo(drugSelectionFilter))));
		tests.add(dynamicTest("manufacturer [" + manufacturer + "]", () -> assertEquals(manufacturer, drugSelectionFilter.getManufacturer(), getApiInfo(drugSelectionFilter))));
		tests.add(dynamicTest("rxOTC [" + rxOTC + "]", () -> assertEquals(rxOTC, drugSelectionFilter.getRxOTC(), getApiInfo(drugSelectionFilter))));
		tests.add(dynamicTest("fdaCode [" + fdaCode + "]", () -> assertThat(getApiInfo(drugSelectionFilter), drugSelectionFilter.getFdaCode(), containsInAnyOrder(fdaCode.toArray(new String[fdaCode.size()])) ) ));
		tests.add(dynamicTest("dosageForm [" + dosageForm + "]", () -> assertThat(getApiInfo(drugSelectionFilter), drugSelectionFilter.getDosageForm(), containsInAnyOrder(dosageForm.toArray(new String[dosageForm.size()])) ) ));

		return tests;
	}

	/*
	 * Helper methods
	 */

	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof DrugSelectionFilter)
		{
			DrugSelectionFilter drugSelectionFilter = (DrugSelectionFilter) obj;

			if ( search.equals(drugSelectionFilter.getSearch()) )
				return true;
		}

		return false;
	}

	/*
	 * Setter / Getter
	 */

	public SearchBy getSearchBy() {
		return searchBy;
	}

	public void setSearchBy(SearchBy searchBy) {
		this.searchBy = searchBy;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public List<BgtInd> getBgtInd() {
		return bgtInd;
	}

	public void setBgtInd(List<BgtInd> bgtInd) {
		this.bgtInd = bgtInd;
	}

	public String getGenericDrugName() {
		return genericDrugName;
	}

	public void setGenericDrugName(String genericDrugName) {
		this.genericDrugName = genericDrugName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public List<RxOTC> getRxOTC() {
		return rxOTC;
	}

	public void setRxOTC(List<RxOTC> rxOTC) {
		this.rxOTC = rxOTC;
	}

	public List<String> getFdaCode() {
		return fdaCode;
	}

	public void setFdaCode(List<String> fdaCode) {
		this.fdaCode = fdaCode;
	}

	public List<String> getDosageForm() {
		return dosageForm;
	}

	public void setDosageForm(List<String> dosageForm) {
		this.dosageForm = dosageForm;
	}

	//Enum SearchBy
	public enum SearchBy
	{
		gpi,ndc;
	}

	//Enum BgtInd
	public enum BgtInd 
	{
		B ("B"),
		G ("G"),
		T ("T");

		private String columnName;

		private BgtInd(String columnName)
		{
			this.columnName = columnName;
		}
		/**
		 * This method will return the list of columns
		 */
		public final static List<String> getBgtInd() 
		{

			return Stream.of(BgtInd.values())
					.map(columnName -> columnName.toString())
					.collect(Collectors.toList());
		}

		@Override
		public String toString() {
			return this.columnName;
		}
	}


	//Enum RxOTC
	public enum RxOTC {
		O ("O"),
		R ("R");

		private String columnName;

		private RxOTC(String columnName)
		{
			this.columnName = columnName;
		}
		/**
		 * This method will return the list of columns
		 */
		public final static List<String> getRxOTC() 
		{

			return Stream.of(RxOTC.values())
					.map(column -> column.toString())
					.collect(Collectors.toList());
		}

		@Override
		public String toString() {
			return this.columnName;
		}
	}
}





