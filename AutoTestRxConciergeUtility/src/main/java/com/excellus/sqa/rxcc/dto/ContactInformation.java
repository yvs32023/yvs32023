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

/**
 * MemberAddress is part of MemberSimulationIntervention
 * 
 * @author Manish Sharma (msharma)
 * @since 09/09/2022
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactInformation extends AbstractJsonDTO<ContactInformation> 
{

	private String address1;
	private String city;
	private String state;
	private String postalCode;
	private String phoneNumber;
	private String faxNumber;
	
	/**
	 * Default constructor
	 */
	public ContactInformation() {
	}

	/**
	 * Constructor
	 * @param address1
	 * @param city
	 * @param state
	 * @param postalCode
	 * @param phoneNumber
	 * @param faxNumber
	 * 
	 */
	public ContactInformation(String address1,String city,String state,String postalCode,String phoneNumber, String faxNumber) 
	{
		this.address1 = address1;
		this.city = city;
		this.state = state;
		this.postalCode = postalCode;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
	}


	
	

	@Override
	public List<DynamicNode> compare(ContactInformation contactInformation)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		tests.add(dynamicTest("address1: [" + address1 + "]", () -> assertEquals(address1, contactInformation.getAddress1(), getApiInfo(contactInformation))));
		tests.add(dynamicTest("city: [" + city + "]", () -> assertEquals(city, contactInformation.getCity(), getApiInfo(contactInformation))));
		tests.add(dynamicTest("state: [" + state + "]", () -> assertEquals(state, contactInformation.getState(), getApiInfo(contactInformation))));
		tests.add(dynamicTest("postalCode: [" + postalCode + "]", () -> assertEquals(postalCode, contactInformation.getPostalCode(), getApiInfo(contactInformation))));
		tests.add(dynamicTest("phoneNumber: [" + phoneNumber + "]", () -> assertEquals(phoneNumber, contactInformation.getPhoneNumber(), getApiInfo(contactInformation))));
		tests.add(dynamicTest("faxNumber: [" + faxNumber + "]", () -> assertEquals(faxNumber, contactInformation.getFaxNumber(), getApiInfo(contactInformation))));
		
		return tests;
	}
	
	/*
	 * Helper methods
	 */

	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof ContactInformation)
		{
			ContactInformation contactInformation = (ContactInformation) obj;

			if ( address1.equals(contactInformation.getAddress1()) )
				return true;
		}
		
		return false;
	}

	/*
	 * Setter / Getter
	 */
	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
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

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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


}
