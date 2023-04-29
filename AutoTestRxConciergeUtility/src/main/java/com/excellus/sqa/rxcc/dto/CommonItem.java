/**
 *
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 *
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;

import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * DTO for generic properties that applies to several DTO
 *
 * @author Manish Sharma (msharma)
 * @since 06/22/2022
 */
@JsonPropertyOrder({ "createdBy",  "createdDateTime",  "lastUpdatedBy", "lastUpdatedDateTime",  "version",  "id"})
public class CommonItem extends AbstractJsonDTO<CommonItem>
{
	protected String  createdBy;
	protected String  createdDateTime;
	protected String  lastUpdatedBy;
	protected String  lastUpdatedDateTime;
	protected String  version;
	protected String  id;

	final int TIME_LIMIT = 60; // seconds

	/*
	 * Validation
	 */

	public List<DynamicNode> compare(CommonItem commonItem)
	{
		List<DynamicNode> tests = new ArrayList<>();

		tests.add(dynamicTest("createdBy [" + createdBy + "]", () -> assertEquals(createdBy, commonItem.getCreatedBy())));

		// Perform date validation
		if ( StringUtils.isNotBlank(createdDateTime) && StringUtils.isNotBlank(commonItem.getCreatedDateTime()) )
		{
			tests.add(compareDates("createdDateTime: [" + createdDateTime + "]", createdDateTime, commonItem.getCreatedDateTime(),
					new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, getApiInfo(commonItem)));
		}
		else
		{
			tests.add(dynamicTest("createdDateTime: [" + createdDateTime + "]", () -> assertEquals(createdDateTime, commonItem.getCreatedDateTime(), getApiInfo(commonItem))));
		}


		tests.add(dynamicTest("lastUpdatedBy [" + lastUpdatedBy + "]", () -> assertEquals(lastUpdatedBy, commonItem.getLastUpdatedBy(), getApiInfo(commonItem))));

		// Perform date validation
		if ( StringUtils.isNotBlank(lastUpdatedDateTime) && StringUtils.isNotBlank(commonItem.getLastUpdatedDateTime()) )
		{
			tests.add(compareDates("lastUpdatedDateTime: [" + lastUpdatedDateTime + "]", lastUpdatedDateTime, commonItem.getLastUpdatedDateTime(),
					new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, getApiInfo(commonItem)));
		}
		else
		{
			tests.add(dynamicTest("lastUpdatedDateTime [" + lastUpdatedDateTime + "]", () -> assertEquals(lastUpdatedDateTime, commonItem.getLastUpdatedDateTime())));
		}


		tests.add(dynamicTest("version [" + version + "]", () -> assertEquals(version, commonItem.getVersion())));
		tests.add(dynamicTest("id [" + id + "]", () -> assertEquals(id, commonItem.getId())));

		return tests;
	}


	/**
	 * Compare two dates
	 *
	 * @param testName to be displayed in the report
	 * @param expected date
	 * @param actual date
	 * @param dateTimeFormat an array of valid format
	 * @return {@link DynamicNode} that represent the test result
	 */
	protected DynamicNode compareDates(String testName, String expected, String actual, String[] dateTimeFormat, String apiInfo)
	{
		try {
			int secDiffLastUpdated = DateTimeUtils.dateDifference(expected.replaceAll("\\.[0-9]*Z", "Z"), 	// remove the micro seconds
					actual.replaceAll("\\.[0-9]*Z", "Z"), // remove the micro seconds
					dateTimeFormat); // remove the micro seconds

			return dynamicTest(testName,
					() -> assertTrue( secDiffLastUpdated >= 0 && secDiffLastUpdated <= TIME_LIMIT,
							String.format("Expected [%s] and Actual [%s] time difference is %d seconds, beyond the %d seconds limit",
									expected, actual, secDiffLastUpdated, TIME_LIMIT) + "\n" + apiInfo) );
		}
		catch(Exception e) {
			return dynamicTest(testName,
					() -> fail(String.format("Unable to compare dates between expected %s and actual %s", expected, actual), e));
		}
	}

	/*
	 * Setter / Getter
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public String getLastUpdatedDateTime() {
		return lastUpdatedDateTime;
	}

	public void setLastUpdatedDateTime(String lastUpdatedDateTime) {
		this.lastUpdatedDateTime = lastUpdatedDateTime;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


}
