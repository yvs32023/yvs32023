/**
 *
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 *
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * DTO for generic properties that applies to several DTO
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/03/2022
 */
@JsonPropertyOrder({ "lastUpdated", "lastUpdatedBy", "ver", "id"})
public class Item extends AbstractJsonDTO<Item> {
	private final static Logger logger = LoggerFactory.getLogger(Item.class);

	protected String lastUpdated;
	protected String lastUpdatedBy;
	protected String ver;
	protected String id;

	final int TIME_LIMIT = 60; // seconds

	/*
	 * Validations
	 */
	public List<DynamicNode> compare(Item item)
	{
		List<DynamicNode> tests = new ArrayList<>();

		// GC (04/05/22) Perform date validation
		if ( StringUtils.isNotBlank(lastUpdated) && StringUtils.isNotBlank(item.getLastUpdated()) )
		{
			tests.add(compareDates("lastUpdated: [" + lastUpdated + "]", lastUpdated, item.getLastUpdated(),
					new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, getApiInfo(item)));
		}
		else
		{
			tests.add(dynamicTest("lastUpdated: [" + lastUpdated + "]", () -> assertEquals(lastUpdated, item.getLastUpdated(), getApiInfo(item))));
		}

		tests.add(dynamicTest("lastUpdatedBy: [" + lastUpdatedBy + "]", () -> assertEquals(lastUpdatedBy, item.getLastUpdatedBy(), getApiInfo(item))));
		tests.add(dynamicTest("ver: [" + ver + "]", () -> assertEquals(ver, item.getVer(), getApiInfo(item))));
		tests.add(dynamicTest("id: [" + id + "]", () -> assertEquals(id, item.getId(), getApiInfo(item))));

		return tests;
	}

	/*
	 * Validations
	 */
	public List<DynamicNode> compareLookUpSpecific(Item item)
	{
		List<DynamicNode> tests = new ArrayList<>();

		tests.add(dynamicTest("ver: [" + ver + "]", () -> assertEquals(ver, item.getVer(), getApiInfo(item))));
		tests.add(dynamicTest("id: [" + id + "]", () -> assertEquals(id, item.getId(), getApiInfo(item))));

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
			logger.error("An unexpected error is caught while generating timestamp", e);

			return null;
		}
	}

	/*
	 * Setter / Getter
	 */

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

