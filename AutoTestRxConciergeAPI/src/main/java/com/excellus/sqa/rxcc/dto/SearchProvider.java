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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 03/21/2022
 */
public class SearchProvider extends Item
{

private static final Logger logger = LoggerFactory.getLogger(SearchProvider.class);
	
	private int officeLocationsCount;
	private DefaultOfficeLocation defaultOfficeLocation;
	private String id;
	private String npi;
	private String statusInd;
	private String firstName;
	private String lastName;
	private String taxonomyCode;
	private String taxonomyDescr;
	
		
	/*
	 * Validations
	 */
	
	/**
	 * Compare two objects
	 * 
	 * @param Provider Search
	 * @return
	 */
	public List<DynamicNode> compare(SearchProvider providerSearch)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
				
		tests.add(dynamicTest("npi: [" + npi + "]", () -> assertEquals(npi, providerSearch.getNpi(), getApiInfo(providerSearch))));
		tests.add(dynamicTest("firstName: [" + firstName + "]", () -> assertEquals(firstName, providerSearch.getFirstName(), getApiInfo(providerSearch))));
		tests.add(dynamicTest("lastName: [" + lastName + "]", () -> assertEquals(lastName, providerSearch.getLastName(), getApiInfo(providerSearch))));
		tests.add(dynamicTest("taxonomyDescr: [" + taxonomyDescr + "]", () -> assertEquals(taxonomyDescr, providerSearch.getTaxonomyDescr(), getApiInfo(providerSearch))));
		tests.add(dynamicTest("id: [" + id + "]", () -> assertEquals(id, providerSearch.getId(), getApiInfo(providerSearch))));
		tests.add(dynamicTest("officeLocations (count): [" + officeLocationsCount + "]", () -> assertEquals(officeLocationsCount, providerSearch.getOfficeLocationsCount(), getApiInfo(providerSearch))));
		tests.add(dynamicTest("statusInd: [" + statusInd + "]", () -> assertEquals(statusInd, providerSearch.getStatusInd(), getApiInfo(providerSearch))));
		tests.add(dynamicTest("defaultOfficeLocation: [" + defaultOfficeLocation + "]", () -> assertEquals(defaultOfficeLocation, providerSearch.getDefaultOfficeLocation(), getApiInfo(providerSearch))));
		
		return tests;
	}

	/**
	 * Check if the provider information contains the search terms.
	 * If the search terms contains multiple WORD then it will spit it and goes through each WORD to determine if
	 * provider information (i.e. firstName, lastName) contains it.
	 *
	 * @param searchTerm to validate that it is either part of first or last name
	 * @return true if the search terms are found in provider's information. Otherwise, it returns false.
	 */
	public boolean providerContains(String searchTerm)
	{
		logger.debug("Starting provider search response validation");
		
		Map<String, Boolean> result = new HashMap<String, Boolean>();

		for ( String theSearchTerm : StringUtils.split(searchTerm, " ") )
		{
			if ( StringUtils.containsIgnoreCase(firstName, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(lastName, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(npi, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(taxonomyCode, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(taxonomyDescr, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(id, theSearchTerm)	
					|| StringUtils.containsIgnoreCase(statusInd, theSearchTerm)
					|| defaultOfficeLocation.containsSearchTerm(theSearchTerm))
				 {
				    result.put(theSearchTerm, true);
			}
			else {
				    result.put(theSearchTerm, false);
			}
		}

		logger.debug("Provider search response validation completed");
		
		return ( result.containsValue(false) ? false : true );
	}
	
	
	/*
	 * Helper methods
	 */

	/**
	 * Return the provider information on these fields
	 * <ul>
	 *     <li>firstName</li>
	 *     <li>lastName</li>
	 * </ul>
	 * @return
	 */
	public String getSearchFieldValues()
	{
		String providerInfo = "firstName='" + firstName + '\'' +
		                    ", lastName='" + lastName + '\'' +
		                    ", npi='" + npi + '\'' +
		                    ", id='" + id + '\'' +
		                    ", taxonomyCode='" + taxonomyCode + '\'' +
		                    ", taxonomyDescr='" + taxonomyDescr + '\'' +
		                    ", defaultOfficeLocation='" + defaultOfficeLocation + '\''+
							", statusInd='" + statusInd + '\''+
							", officeLocationsCount='" + officeLocationsCount + '\'';
		return providerInfo;
	}
	
	
	/*
	 * Setter / Getter
	 */
	
	
	public int getOfficeLocationsCount() {
		return officeLocationsCount;
	}


	public void setOfficeLocationsCount(int officeLocationsCount) {
		this.officeLocationsCount = officeLocationsCount;
	}


	public DefaultOfficeLocation getDefaultOfficeLocation() {
		return defaultOfficeLocation;
	}


	public void setDefaultOfficeLocation(DefaultOfficeLocation defaultOfficeLocation) {
		this.defaultOfficeLocation = defaultOfficeLocation;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getNpi() {
		return npi;
	}


	public void setNpi(String npi) {
		this.npi = npi;
	}


	public String getStatusInd() {
		return statusInd;
	}


	public void setStatusInd(String statusInd) {
		this.statusInd = statusInd;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getTaxonomyCode() {
		return taxonomyCode;
	}


	public void setTaxonomyCode(String taxonomyCode) {
		this.taxonomyCode = taxonomyCode;
	}


	public String getTaxonomyDescr() {
		return taxonomyDescr;
	}


	public void setTaxonomyDescr(String taxonomyDescr) {
		this.taxonomyDescr = taxonomyDescr;
	}


	public static Logger getLogger() {
		return logger;
	}
	
}
