/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * DTO for phone number
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/20/2022
 */
@JsonPropertyOrder({ "type", "number"})
public class PhoneNumber extends AbstractJsonDTO<PhoneNumber>{

	private static final Logger logger = LoggerFactory.getLogger(PhoneNumber.class);

    private String type;
	private String number;

	/**
	 * Default constructor
	 */
	public PhoneNumber() {
	}
	
	/**
	 * Constructor
	 * @param type
	 * @param number
	 */
	public PhoneNumber(String type, String number) {
		this.type = type;
		this.number = number;
	}

	/*
	 * Validations
	 */
	
	/**
	 * Compare two objects
	 *
	 * @param phoneNumber another {@link PhoneNumber} to compare with
	 * @return list of {@link DynamicNode} that represent the assertion of each properties
	 */
	public List<DynamicNode> compare(PhoneNumber phoneNumber) {
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("type: [" + this.type + "]", () -> assertEquals(type, phoneNumber.getType(), getApiInfo(phoneNumber))));
		tests.add(dynamicTest("number: [" + this.getNormalizeNumber() + "]", () -> assertEquals(getNormalizeNumber(), phoneNumber.getNormalizeNumber(), getApiInfo(phoneNumber))));

		return tests;
	}
	
	
	/*
	 * Helper methods
	 */

	/**
	 * Format the phone number into (###) ###-####
	 * @return formatted phone number if it is a US valid phone otherwise it returns as is
	 */
	@JsonIgnore
	public String getNormalizeNumber() {
		if ( StringUtils.isBlank(this.number) )
			return this.number;

		logger.debug("Normalize phone number " + this.number);

		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

		try
		{
			Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(this.number, "US");
			return phoneUtil.format(phoneNumber, PhoneNumberFormat.NATIONAL);
		}
		catch (NumberParseException e)
		{
			logger.warn("Unable to normalize phone number", e);
			return this.number;
		}
	}

	@Override
	public boolean equals(Object o) {
		if ( !(o instanceof PhoneNumber) ) {
			return false;
		}

		PhoneNumber phoneNumber = (PhoneNumber) o;
		if ( StringUtils.equalsIgnoreCase(type, phoneNumber.getType()) &&
				     ((StringUtils.equalsIgnoreCase(number, phoneNumber.getNumber())) ||
						      (StringUtils.equalsIgnoreCase(getNormalizeNumber(), phoneNumber.getNormalizeNumber()))) ) {
			return true;
		}

		return false;
	}

	/*
	 * Setter and getter
	 */

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
