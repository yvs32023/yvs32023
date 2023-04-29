/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicNode;
import org.junit.platform.commons.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;

/**
 * DTO for office location
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/03/2022
 */
@JsonPropertyOrder({ "id", "status", "default", "address1", "address2", "address3", "city", "state", "postalCode", "timeZone",
	"phoneNumber", "faxNumber", "contactName", "contactNumber", "contactExt", "faxVerified",
	"monHours", "tueHours", "wedHours", "thurHours", "friHours", "satHours", "sunHours"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfficeLocation extends AbstractJsonDTO<OfficeLocation> {

	@JsonProperty(required=true)
	private String id;

	@JsonProperty("default")
	private Boolean defaultVal;

	private Boolean status;

	@JsonProperty(required=true)
	private String address1;

	private String address2;
	private String address3;
	private String city;
	private String state;
	private String postalCode;
	private String timeZone;
	private String phoneNumber;
	private String faxNumber;
	private String contactName;
	private String contactNumber;
	private String contactExt;
	private Boolean faxVerified;
	private Hours monHours;
	private Hours tueHours;
	private Hours wedHours;
	private Hours thurHours;
	private Hours friHours;
	private Hours satHours;
	private Hours sunHours;


	/**
	 * Default constructor
	 */
	public OfficeLocation() {
	}

	/**
	 * Constructor
	 * @param type
	 * @param number
	 */
	public OfficeLocation(String id,Boolean defaultVal,Boolean status, String address1, String address2, String address3, String city, String state,String postalCode,String timeZone, String phoneNumber, String faxNumber, String contactName , String contactNumber, String contactExt, Boolean faxVerified,Hours monHours,Hours tueHours, Hours wedHours, Hours thurHours, Hours friHours,Hours satHours, Hours sunHours) 
	{
		this.id = id;
		this.defaultVal = defaultVal;
		this.status = status;
		this.address1 = address1;
		this.address2 = address2;
		this.address3 = address3;
		this.city = city;
		this.state = state;
		this.postalCode = postalCode;
		this.timeZone = timeZone;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
		this.contactName = contactName;
		this.contactExt = contactExt;
		this.faxVerified = faxVerified;
		this.monHours = monHours;
		this.tueHours = tueHours;
		this.wedHours = wedHours;
		this.thurHours = thurHours;
		this.friHours = friHours;
		this.satHours = satHours;
		this.sunHours = sunHours;

	}

	/*
	 * Validations
	 */

	/**
	 * Compare two objects
	 * 
	 * @param Officelocation
	 * @return
	 */
	public List<DynamicNode> compare(OfficeLocation officeLocation)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();


		tests.add(dynamicTest("id: [" + id + "]", () -> assertEquals(id, officeLocation.getId(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("default: [" + defaultVal + "]", () -> assertEquals(defaultVal, officeLocation.isDefaultVal(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("status: [" + status + "]", () -> assertEquals(status, officeLocation.isStatus(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("address1: [" + address1 + "]", () -> assertEquals(address1, officeLocation.getAddress1(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("address2: [" + address2 + "]", () -> assertEquals(address2, officeLocation.getAddress2(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("address3: [" + address3 + "]", () -> assertEquals(address3, officeLocation.getAddress3(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("city: [" + city + "]", () -> assertEquals(city, officeLocation.getCity(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("state: [" + state + "]", () -> assertEquals(state, officeLocation.getState(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("Postal Code: [" + postalCode + "]", () -> assertEquals(postalCode, officeLocation.getPostalCode(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("timeZone: [" + timeZone + "]", () -> assertEquals(timeZone, officeLocation.getTimeZone(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("phoneNumber: [" + phoneNumber + "]", () -> assertEquals(phoneNumber, officeLocation.getPhoneNumber(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("faxNumber: [" + faxNumber + "]", () -> assertEquals(faxNumber, officeLocation.getFaxNumber(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("contactName: [" + contactName + "]", () -> assertEquals(contactName, officeLocation.getContactName(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("contactNumber: [" + contactNumber + "]", () -> assertEquals(contactNumber, officeLocation.getContactNumber(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("contactExt: [" + contactExt + "]", () -> assertEquals(contactExt, officeLocation.getContactExt(), getApiInfo(officeLocation))));
		tests.add(dynamicTest("faxVerified: [" + faxVerified + "]", () -> assertEquals(faxVerified, officeLocation.isFaxVerified(), getApiInfo(officeLocation))));

		// Validate office hours

		tests.add(dynamicContainer("monHours", monHours.compare(officeLocation.getMonHours())));
		tests.add(dynamicContainer("tueHours", tueHours.compare(officeLocation.getTueHours())));
		tests.add(dynamicContainer("wedHours", wedHours.compare(officeLocation.getWedHours())));
		tests.add(dynamicContainer("thurHours", thurHours.compare(officeLocation.getThurHours())));
		tests.add(dynamicContainer("friHours", friHours.compare(officeLocation.getFriHours())));
		tests.add(dynamicContainer("satHours", satHours.compare(officeLocation.getSatHours())));
		tests.add(dynamicContainer("sunHours", sunHours.compare(officeLocation.getSunHours())));

		return tests;
	}
	

	/**
	 * Compare two UI objects
	 * 
	 * @param Officelocation
	 * @return
	 */	
	public List<DynamicNode> compareUI(OfficeLocation officeLocation)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("id: [" + id + "]", () -> assertEquals(id, officeLocation.getId())));
		tests.add(dynamicTest("city: [" + city + "]", () -> assertEquals(city.toLowerCase(), officeLocation.getCity().toLowerCase())));
		tests.add(dynamicTest("state: [" + state + "]", () -> assertEquals(state, officeLocation.getState().toUpperCase())));
		tests.add(dynamicTest("phoneNumber: [" + phoneNumber + "]", 
					() -> assertEquals(getNormalizeNumber(phoneNumber), officeLocation.getNormalizeNumber(phoneNumber))));

		tests.add(dynamicTest("faxNumber: [" + faxNumber + "]", 
					() -> assertEquals(getNormalizeNumber(faxNumber), officeLocation.getNormalizeNumber(faxNumber))));

		tests.add(dynamicTest("faxVerified: [" + faxVerified + "]", () -> assertEquals(faxVerified, officeLocation.isFaxVerified())));

		return tests;
	}

	@JsonIgnore
	public String getNormalizeNumber(String number) {
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
	private String getNormalizePhoneNumberUI() {
		return getNormalizeNumber(phoneNumber);
	  }
   
	/**
	 * method to compare phone number
	 *
	 */
	@JsonIgnore
	private String getNormalizeFaxNumberUI() {
		return getNormalizeNumber(faxNumber);
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
		if ( obj instanceof OfficeLocation )
		{
			OfficeLocation officeLocation = (OfficeLocation) obj;

			if ( id != null && id.length() > 0)
			{
				if ( id.equals(officeLocation.getId()) )
					return true;
			}

		}

		return false;
	}

	/*
	 * Setter / Getter
	 */

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean isDefaultVal() { 
		return defaultVal;
	}

	public void setDefaultVal(Boolean defaultVal) { 
		this.defaultVal = defaultVal;
	}

	public Boolean isStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
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

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
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

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
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

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getContactExt() {
		return contactExt;
	}

	public void setContactExt(String contactExt) {
		this.contactExt = contactExt;
	}

	public Boolean isFaxVerified() {
		return faxVerified;
	}

	public void setFaxVerified(Boolean faxVerified) {
		this.faxVerified = faxVerified;
	}

	public Hours getMonHours() {
		return monHours;
	}

	public void setMonHours(Hours monHours) {
		this.monHours = monHours;
	}

	public Hours getTueHours() {
		return tueHours;
	}

	public void setTueHours(Hours tueHours) {
		this.tueHours = tueHours;
	}

	public Hours getWedHours() {
		return wedHours;
	}

	public void setWedHours(Hours wedHours) {
		this.wedHours = wedHours;
	}

	public Hours getThurHours() {
		return thurHours;
	}

	public void setThurHours(Hours thurHours) {
		this.thurHours = thurHours;
	}

	public Hours getFriHours() {
		return friHours;
	}

	public void setFriHours(Hours friHours) {
		this.friHours = friHours;
	}

	public Hours getSatHours() {
		return satHours;
	}

	public void setSatHours(Hours satHours) {
		this.satHours = satHours;
	}

	public Hours getSunHours() {
		return sunHours;
	}

	public void setSunHours(Hours sunHours) {
		this.sunHours = sunHours;
	}


}

