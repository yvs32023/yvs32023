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

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 03/21/2022
 */
public class DefaultOfficeLocation extends AbstractJsonDTO<DefaultOfficeLocation>
{
	private static final Logger logger = LoggerFactory.getLogger(DefaultOfficeLocation.class);
	
	@JsonProperty("default")
	private boolean defaultVal;
	
	private String city;
	private String state;
	private String phoneNumber;
	private String faxNumber;
	private boolean faxVerified;
	
	
	/*
	 * Validations
	 */
	
	/**
	 * Compare two objects
	 * 
	 * @param DefaultOfficeLocation
	 * @return
	 */
	public List<DynamicNode> compare(DefaultOfficeLocation defaultOfficeLocation)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		
		
		tests.add(dynamicTest("default: [" + defaultVal + "]", () -> assertEquals(defaultVal, defaultOfficeLocation.isDefaultVal(), getApiInfo(defaultOfficeLocation))));
		tests.add(dynamicTest("city: [" + city + "]", () -> assertEquals(city, defaultOfficeLocation.getCity(), getApiInfo(defaultOfficeLocation))));
		tests.add(dynamicTest("state: [" + state + "]", () -> assertEquals(state, defaultOfficeLocation.getState(), getApiInfo(defaultOfficeLocation))));
		tests.add(dynamicTest("phoneNumber: [" + phoneNumber + "]", () -> assertEquals(phoneNumber, defaultOfficeLocation.getPhoneNumber(), getApiInfo(defaultOfficeLocation))));
		tests.add(dynamicTest("faxNumber: [" + faxNumber + "]", () -> assertEquals(faxNumber, defaultOfficeLocation.getFaxNumber(), getApiInfo(defaultOfficeLocation))));
		tests.add(dynamicTest("faxVerified: [" + faxVerified + "]", () -> assertEquals(faxVerified, defaultOfficeLocation.isFaxVerified(), getApiInfo(defaultOfficeLocation))));
		
				
		return tests;
	}
	
	/*
	 * Helper method
	 */
	
	/**
	 * Check if the provider information contains the search terms.
	 * @param theSearchTerm
	 * @return
	 */
	public boolean containsSearchTerm(String theSearchTerm) 
	{
		logger.debug("Searching for search term " + theSearchTerm);
		
		if ( StringUtils.containsIgnoreCase(city, theSearchTerm) 
				|| StringUtils.containsIgnoreCase(state, theSearchTerm) 
				|| StringUtils.containsIgnoreCase(phoneNumber, theSearchTerm) 
				|| StringUtils.containsIgnoreCase(faxNumber, theSearchTerm) )
		 {
			return true;
		 }
		return false;
		}
	
		
	
	/*
	 * Setter / Getter
	 */
	
	public boolean isDefaultVal() {
		return defaultVal;
	}
	public void setDefaultVal(boolean defaultVal) {
		this.defaultVal = defaultVal;
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
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getFaxNumber() {
		return faxNumber;
	}
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}
	public boolean isFaxVerified() {
		return faxVerified;
	}
	public void setFaxVerified(boolean faxVerified) {
		this.faxVerified = faxVerified;
	}
}
