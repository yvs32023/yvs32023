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
  * DTO for LBS Pharmacy
 * 
 * @author Manish Sharma (msharma)
 * @since 04/03/2022
 */
public class PharmaciesSearch extends Item 
{

	private static final Logger logger = LoggerFactory.getLogger(PharmaciesSearch.class);
	
	private String storeName;
	private String npi;
	private String city;
	private String state;
	private String phoneNumber;
	private String taxonomyDescr;
	private String statusInd;
	
	
	
	/*
	 * Validations
	 */


	/**
	 * Check if the pharmacy information contains the search terms.
	 * If the search terms contains multiple WORD then it will spit it and goes through each WORD to determine if
	 * pharmacy information (i.e. firstName, lastName) contains it.
	 *
	 * @param searchTerm to validate that it is either part of first or last name
	 * @return true if the search terms are found in provider's information. Otherwise, it returns false.
	 */
	public boolean pharmacyContains(String searchTerm)
	{
		logger.debug("Starting pharmacy search response validation");
		
		Map<String, Boolean> result = new HashMap<String, Boolean>();

		for ( String theSearchTerm : StringUtils.split(searchTerm, " ") )
		{
			if ( StringUtils.containsIgnoreCase(storeName, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(npi, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(city, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(taxonomyDescr, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(state, theSearchTerm)	
					|| StringUtils.containsIgnoreCase(statusInd, theSearchTerm)
					|| StringUtils.containsIgnoreCase(phoneNumber, theSearchTerm))
				 {
				    result.put(theSearchTerm, true);
			}
			else {
				    result.put(theSearchTerm, false);
			}
		}

		logger.debug("Pharmacy search response validation completed");
		
		return ( result.containsValue(false) ? false : true );
	}
		
	/**
	 * Compare two objects
	 * 
	 * @param pharmacy
	 * @return
	 */
	public List<DynamicNode> compare(PharmaciesSearch pharmacies)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		tests.add(dynamicTest("npi: [" + npi + "]", () -> assertEquals(npi, pharmacies.getNpi(), getApiInfo(pharmacies))));
		tests.add(dynamicTest("statusInd: [" + statusInd + "]", () -> assertEquals(statusInd, pharmacies.getStatusInd(), getApiInfo(pharmacies))));
		tests.add(dynamicTest("storeName: [" + storeName + "]", () -> assertEquals(storeName, pharmacies.getStoreName(), getApiInfo(pharmacies))));
		tests.add(dynamicTest("taxonomyDescr: [" + taxonomyDescr + "]", () -> assertEquals(taxonomyDescr, pharmacies.getTaxonomyDescr(), getApiInfo(pharmacies))));
		tests.add(dynamicTest("phoneNumber: [" + phoneNumber + "]", () -> assertEquals(phoneNumber, pharmacies.getPhoneNumber(), getApiInfo(pharmacies))));
		tests.add(dynamicTest("city: [" + city + "]", () -> assertEquals(city, pharmacies.getCity(), getApiInfo(pharmacies))));
		tests.add(dynamicTest("state: [" + state + "]", () -> assertEquals(state, pharmacies.getState(), getApiInfo(pharmacies))));
		
		
		tests.addAll( super.compare(pharmacies) );
		
		return tests;
	}

	
	/*
	 * Helper methods
	 */
	
	/**
	 * Return the pharmacy information on these fields
	 * <ul>
	 *     <li>storeName</li>
	 *     <li>npi</li>
	 * </ul>
	 * @return
	 */
	public String getSearchFieldValues()
	{
		String pharmacyInfo = "storeName='" + storeName + '\'' +
		                    ", npi='" + npi + '\'' +
		                    ", id='" + id + '\'' +
		                    ", city='" + city + '\'' +
		                    ", taxonomyDescr='" + taxonomyDescr + '\'' +
		                    ", state='" + state + '\''+
							", statusInd='" + statusInd + '\''+
							", phoneNumber='" + phoneNumber + '\'';
		return pharmacyInfo;
	}
	
	/**
	 * Equals is defined if the following are met
	 * - npi are the same
	 * 
	 *
	 * @param obj to be compared
	 * @return true if it mets the criteria
	 */
	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof PharmaciesSearch)
		{
			PharmaciesSearch pharmacies = (PharmaciesSearch) obj;

			if ( npi.equals(pharmacies.getNpi()) )
				return true;
		}
		
		return false;
	}
	
	
	
	/*
	 * Setter / Getter
	 */
	
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

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
		
	public String getTaxonomyDescr() {
		return taxonomyDescr;
	}

	public void setTaxonomyDescr(String taxonomyDescr) {
		this.taxonomyDescr = taxonomyDescr;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	
	

}
