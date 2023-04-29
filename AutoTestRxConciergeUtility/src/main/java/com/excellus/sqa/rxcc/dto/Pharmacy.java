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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber;

/**
  * DTO for LBS Pharmacy
 * 
 * @author Manish Sharma (msharma)
 * @since 02/28/2022
 */
public class Pharmacy extends Item {
	
	private String npi;
	private String type;
	private String statusInd;
	private String storeName;
	private String taxonomyCode;
	private String taxonomyDescr;
	private String phoneNumber;
	private String faxNumber;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String postalCode;
	private String country;
	

	/*
	 * Validations
	 */
	
	/**
	 * Compare two objects
	 *
	 * @param pharmacy
	 * @return
	 */
	public List<DynamicNode> compare(Pharmacy pharmacy)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		tests.add(dynamicTest("npi: [" + npi + "]", () -> assertEquals(npi, pharmacy.getNpi(), getApiInfo(pharmacy))));
		tests.add(dynamicTest("type: [" + type + "]", () -> assertEquals(type, pharmacy.getType(), getApiInfo(pharmacy))));
		tests.add(dynamicTest("statusInd: [" + statusInd + "]", () -> assertEquals(statusInd, pharmacy.getStatusInd(), getApiInfo(pharmacy))));
		tests.add(dynamicTest("storeName: [" + storeName + "]", () -> assertEquals(storeName, pharmacy.getStoreName(), getApiInfo(pharmacy))));
		tests.add(dynamicTest("taxonomyCode: [" + taxonomyCode + "]", () -> assertEquals(taxonomyCode, pharmacy.getTaxonomyCode(), getApiInfo(pharmacy))));
		tests.add(dynamicTest("taxonomyDescr: [" + taxonomyDescr + "]", () -> assertEquals(taxonomyDescr, pharmacy.getTaxonomyDescr(), getApiInfo(pharmacy))));
		tests.add(dynamicTest("phoneNumber: [" + phoneNumber + "]", () -> assertEquals(phoneNumber, pharmacy.getPhoneNumber(), getApiInfo(pharmacy))));
		tests.add(dynamicTest("faxNumber: [" + faxNumber + "]", () -> assertEquals(faxNumber, pharmacy.getFaxNumber(), getApiInfo(pharmacy))));
		tests.add(dynamicTest("address1: [" + address1 + "]", () -> assertEquals(address1, pharmacy.getAddress1(), getApiInfo(pharmacy))));
		tests.add(dynamicTest("address2: [" + address2 + "]", () -> assertEquals(address2, pharmacy.getAddress2(), getApiInfo(pharmacy))));
		tests.add(dynamicTest("city: [" + city + "]", () -> assertEquals(city, pharmacy.getCity(), getApiInfo(pharmacy))));
		tests.add(dynamicTest("state: [" + state + "]", () -> assertEquals(state, pharmacy.getState(), getApiInfo(pharmacy))));
		tests.add(dynamicTest("postalCode: [" + postalCode + "]", () -> assertEquals(postalCode, pharmacy.getPostalCode(), getApiInfo(pharmacy))));
		tests.add(dynamicTest("country: [" + country + "]", () -> assertEquals(country, pharmacy.getCountry(), getApiInfo(pharmacy))));
		
		tests.addAll( super.compare(pharmacy) );
		
		return tests;
	}
	
	
	/**
     * Compare two objects
     *
     * @param pharmacy
     * @return
     */
    public List<DynamicNode> compareUI(Pharmacy pharmacy)
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        
        tests.add(dynamicTest("npi: [" + npi + "]", () -> assertEquals(npi, pharmacy.getNpi())));
        tests.add(dynamicTest("statusInd: [" + statusInd + "]", () -> assertEquals(statusInd, pharmacy.getStatusInd())));
        tests.add(dynamicTest("storeName: [" + storeName + "]", () -> assertEquals(storeName, pharmacy.getStoreName())));
        tests.add(dynamicTest("taxonomyCode: [" + taxonomyCode + "]", () -> assertEquals(taxonomyCode, pharmacy.getTaxonomyCode())));
        tests.add(dynamicTest("taxonomyDescr: [" + taxonomyDescr + "]", () -> assertEquals(taxonomyDescr, pharmacy.getTaxonomyDescr())));
        
        tests.add(dynamicTest("phoneNumber: [" + phoneNumber + "]", 
                () -> assertEquals(getNormalizeNumber(phoneNumber), pharmacy.getPhoneNumber())));
        
        tests.add(dynamicTest("faxNumber: [" + faxNumber + "]", 
                () -> assertEquals(getNormalizeNumber(faxNumber), 
                        StringUtils.isBlank(pharmacy.getFaxNumber()) ? null : pharmacy.getFaxNumber())));
        
        tests.add(dynamicTest("address1: [" + address1 + "]", () -> assertEquals(address1, pharmacy.getAddress1())));
        
        tests.add(dynamicTest("address2: [" + address2 + "]", 
                () -> assertEquals(address2, 
                        StringUtils.isBlank(pharmacy.getAddress2()) ? null : pharmacy.getAddress2()))); // if UI is empty then compare it as null
        
        tests.add(dynamicTest("city: [" + city + "]", () -> assertEquals(city, pharmacy.getCity())));
        tests.add(dynamicTest("state: [" + state + "]", () -> assertEquals(state, pharmacy.getState())));
        tests.add(dynamicTest("postalCode: [" + postalCode + "]", () -> assertEquals(postalCode, pharmacy.getPostalCode())));
        
        return tests;
    }
    
    
    
    /**
     * Compare two objects
     *
     * @param pharmacy
     * @return
     */
    public List<DynamicNode> comparePharmacyDirectoryUI(Pharmacy pharmacy)
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        
        tests.add(dynamicTest("npi: [" + npi + "]", () -> assertEquals(npi, pharmacy.getNpi())));
       
        tests.add(dynamicTest("storeName: [" + storeName + "]", () -> assertEquals(storeName, pharmacy.getStoreName().toUpperCase())));
        tests.add(dynamicTest("taxonomyDescr: [" + taxonomyDescr + "]", () -> assertEquals(taxonomyDescr, pharmacy.getTaxonomyDescr())));
        
        tests.add(dynamicTest("phoneNumber: [" + phoneNumber + "]", 
                () -> assertEquals(getNormalizeNumber(phoneNumber), pharmacy.getNormalizeNumber(phoneNumber))));
        
       
        tests.add(dynamicTest("city: [" + city + "]", () -> assertEquals(city, pharmacy.getCity().toUpperCase())));
        tests.add(dynamicTest("state: [" + state + "]", () -> assertEquals(state, pharmacy.getState())));
        
        tests.add(dynamicTest("statusInd: [" + statusInd + "]", () -> assertEquals(statusInd, pharmacy.getStatusInd())));
        
        return tests;
    }
    
    
	/*
	 * Helper methods
	 */

    @JsonIgnore
    private String getNormalizeNumber(String number) {
        if ( StringUtils.isBlank(number) )
            return number;

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try
        {
            Phonenumber.PhoneNumber thePhoneNumber = phoneUtil.parse(number, "US");
            return phoneUtil.format(thePhoneNumber, PhoneNumberFormat.NATIONAL);
        }
        catch (NumberParseException e)
        {
            return number;
        }
    }
    
    /**
     * method to compare phone number
     *
     */
	
	@JsonIgnore
	public String getNormalizePhoneNumberUI() {
		return getNormalizeNumber(phoneNumber);
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
		if ( obj instanceof Pharmacy)
		{
			Pharmacy pharmacy = (Pharmacy) obj;

			if ( npi.equals(pharmacy.getNpi()) )
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	
	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
