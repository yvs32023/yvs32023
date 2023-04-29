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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;



/** 

 *  DTO for User Search Filter

 *  

 * @author Ponnani Ananthanarayanan (panantha) 

 * @since 02/28/2022 

 */
@JsonPropertyOrder({"filterName", "tags"})
public class Filter extends AbstractJsonDTO<Filter> {

	private String filterName;
	private List <String> tags;

	/**
	 * Default constructor
	 */
	public Filter() {
	}

	/**
	 * Constructor
	 * @param filterName
	 * @param tags
	 */
	public Filter(String filterName, List<String> tags) 
	{
		this.filterName = filterName;
		this.tags = tags;
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
	public List<DynamicNode> compare(Filter filter)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("filterName: [" + filterName + "]", () -> assertEquals(filterName, filter.getFilterName(), getApiInfo(filter))));
		tests.add(dynamicTest("tags: [" + tags + "]", () -> assertEquals(tags, filter.getTags(), getApiInfo(filter))));

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
		if ( obj instanceof Filter )
		{
			Filter filter = (Filter) obj;

			if ( filterName != null && filterName.length() > 0)
			{
				if ( filterName.equals(filter.getFilterName()) )
					return true;
			}

		}

		return false;
	}

	/*
	 * Setter / Getter
	 */


	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}


}