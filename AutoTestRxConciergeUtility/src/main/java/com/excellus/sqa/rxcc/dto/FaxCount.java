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

/**
 * DTO for Fax Count
 * 
 * @author Manish Sharma (msharma)
 * @since 02/28/2022
 */
public class FaxCount extends Item {
	
	private String npi;
	private String currentDate;
	private int specificProviderNpi;
	private String id;
	
			

	/*
	 * Validations
	 */
	
	
	/**
	 * Compare two objects
	 * 
	 * @param FaxCount
	 * @return
	 */
	public List<DynamicNode> compare(FaxCount faxCount)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		tests.add(dynamicTest("npi: [" + npi + "]", () -> assertEquals(npi, faxCount.getNpi(), getApiInfo(faxCount))));
		tests.add(dynamicTest("currentDate: [" + currentDate + "]", () -> assertEquals(currentDate, faxCount.getCurrentDate(), getApiInfo(faxCount))));
		tests.add(dynamicTest("specificProviderNpi: [" + specificProviderNpi + "]", () -> assertEquals(specificProviderNpi, faxCount.getSpecificProviderNpi(), getApiInfo(faxCount))));
		tests.add(dynamicTest("id: [" + id + "]", () -> assertEquals(id, faxCount.getId(), getApiInfo(faxCount))));
		 
		tests.addAll( super.compare(faxCount) );
		
		return tests;
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

	public String getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}

	public int getSpecificProviderNpi() {
		return specificProviderNpi;
	}

	public void setId(String id) {
		this.id = id;
	}

		

}
