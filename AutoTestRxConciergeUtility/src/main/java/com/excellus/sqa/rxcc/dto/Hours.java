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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * 
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/03/2022
 */
@JsonPropertyOrder({ "closed", "startTime", "endTime"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Hours extends AbstractJsonDTO<Hours> {
	
	@JsonProperty(required=true)
	protected Boolean closed;
	
	//protected boolean callHours; // GC (04/03/22) removed
	protected String startTime;
	protected String endTime;
	
	
	/**
	 * Default constructor
	 */
	public Hours() {
	}
	
	/**
	 * Constructor
	 * @param closed
	 * @param startTime
	 * @param endTime
	 */
	public Hours(Boolean closed,String startTime,String endTime) 
{
		this.closed = closed;
		this.startTime = startTime;
		this.endTime = endTime;

}
		
	
	/*
	 * Validations
	 */
	
	/**
	 * Compare two objects
	 * 
	 * @param hours
	 * @return
	 */
	public List<DynamicNode> compare(Hours hours)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		tests.add(dynamicTest("closed: [" + closed + "]", () -> assertEquals(closed, hours.isClosed(), getApiInfo(hours))));
		//tests.add(dynamicTest("callHours: [" + callHours + "]", () -> assertEquals(callHours, hours.isCallHours())));
		tests.add(dynamicTest("startTime: [" + startTime + "]", () -> assertEquals(startTime, hours.getStartTime(), getApiInfo(hours))));
		tests.add(dynamicTest("endTime: [" + endTime + "]", () -> assertEquals(endTime, hours.getEndTime(), getApiInfo(hours))));
		 
		return tests;
	}
	
	/*
	 * Setter / Getter
	 */
	
	public Boolean isClosed() {
		return closed;
	}

	public void setClosed(Boolean closed) {
		this.closed = closed;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
}

