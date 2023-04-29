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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** 

 *  DTO for User/Saved Search

 *  

 * @author Ponnani Ananthanarayanan (panantha) 

 * @since 03/02/2022 

 */
@JsonPropertyOrder({"searchName", "default", "filters"})
public class SavedSearch extends AbstractJsonDTO<SavedSearch> {


	private String searchName;

	@JsonProperty("default")
	private Boolean defaultVal;

	private List<Filter> filters;

	/**
	 * Default constructor
	 */
	public SavedSearch() {
	}

	/**
	 * Constructor
	 * @param containerName
	 * @param fileName
	 */
	public SavedSearch(String searchName,Boolean defaultVal) 
	{
		this.searchName = searchName;
		this.defaultVal = defaultVal;
	}

	/*
	 * Validations
	 */

	/**
	 * Compare two objects
	 * 
	 * @param savedsearch
	 * @return
	 */
	public List<DynamicNode> compare(SavedSearch savedSearch)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("searchName: [" + searchName + "]", () -> assertEquals(searchName, savedSearch.getSearchName(), getApiInfo(savedSearch))));
		tests.add(dynamicTest("default: [" + defaultVal + "]", () -> assertEquals(defaultVal, savedSearch.isDefaultVal(), getApiInfo(savedSearch))));
		//tests.add(dynamicTest("filters: [" + filters + "]", () -> assertEquals(filters, savedSearch.getFilters(), getApiInfo(savedSearch))));
		
		tests.add(dynamicTest("filters (count): [" + filters.size() + "]", () -> assertEquals(filters.size(), savedSearch.getFilters().size(), getApiInfo(savedSearch))));

		/*
		 * Validate each of the filters
		 */

		for ( Filter expected : filters )
		{
			boolean found = false;
			for ( Filter actual : savedSearch.getFilters())
			{
				if ( expected.equals(actual) )
				{
					found = true;

					tests.add(dynamicContainer("Filters [" + searchName + "]", expected.compare(actual)) );	// compare the office location

					break;
				}
			}

			if ( !found ) {
				tests.add(dynamicTest("Filters not found", () -> fail("The Filters with search Name " + searchName + " is missing")));
			}
		}

		return tests;
	}

	
	/*
	 * Helper methods
	 */

	/**
	 * Equals is defined if the following are met
	 * - office location id are the same
	 *
	 * @return true if it mets the criteria
	 */
	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof SavedSearch )
		{
			SavedSearch savedSearch = (SavedSearch) obj;

			if ( searchName != null && searchName.length() > 0)
			{
				if ( searchName.equals(savedSearch.getSearchName()) )
					return true;
			}

		}

		return false;
	}

	/*
	 * Setter / Getter
	 */

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public Boolean isDefaultVal() { 
		return defaultVal;
	}

	public void setDefaultVal(Boolean defaultVal) { 
		this.defaultVal = defaultVal;
	}


	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public void addFilter(Filter filter) {
		if ( this.filters == null) {
			this.filters = new ArrayList<Filter>();
		}
		
		this.filters.add(filter);
	}
}
