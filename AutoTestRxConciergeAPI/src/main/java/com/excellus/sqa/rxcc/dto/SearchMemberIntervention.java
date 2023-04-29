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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.dto.MemberIntervention.TargetInterventionQueueType;
import com.excellus.sqa.utilities.DateTimeUtils;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 10/13/2022
 */
public class SearchMemberIntervention 
{
	private static Logger logger = LoggerFactory.getLogger(SearchMemberIntervention.class);

	private String id;
	private String queueStatusChangeDateTime;
	private String queueStatusCode;
	private String rxccGroupName;
	private double planCost;
	private double memberCost;
	private String memberId;
	private String  memberName;
	private String  memberDateBirth;
	private String  providerNpi;
	private String  providerName;
	private String  providerPhoneNumber;
	private String  assignedTo;
	private String  targetProductName;
	private String  createdDateTime;
	private String  lastUpdatedBy;
	private TargetInterventionQueueType  targetInterventionQueueType; //enum

	final int TIME_LIMIT = 60; // seconds

	/*
	 * Validations
	 */

	/**
	 * Compare two objects
	 * 
	 * @param SearchMemberIntervention
	 * @return
	 */

	public List<DynamicNode> compare(SearchMemberIntervention searchMemberIntervention)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("id: [" + id + "]", () -> assertEquals(id, searchMemberIntervention.getId())));

		// Perform queueStatusChangeDateTime date validation			
		if ( StringUtils.isNotBlank(queueStatusChangeDateTime) && StringUtils.isNotBlank(searchMemberIntervention.getQueueStatusChangeDateTime()) )
		{
			tests.add(compareDates("queueStatusChangeDateTime: [" + queueStatusChangeDateTime + "]", queueStatusChangeDateTime, searchMemberIntervention.getQueueStatusChangeDateTime(),
					new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, null));
		}
		else
		{
			tests.add(dynamicTest("queueStatusChangeDateTime: [" + queueStatusChangeDateTime + "]", () -> assertEquals(queueStatusChangeDateTime, searchMemberIntervention.getQueueStatusChangeDateTime())));
		}

		tests.add(dynamicTest("queueStatusCode: [" + queueStatusCode + "]", () -> assertEquals(queueStatusCode, searchMemberIntervention.getQueueStatusCode())));
		tests.add(dynamicTest("rxccGroupName: [" + rxccGroupName + "]", () -> assertEquals(rxccGroupName, searchMemberIntervention.getRxccGroupName())));
		tests.add(dynamicTest("planCost: [" + planCost + "]", () -> assertEquals(planCost, searchMemberIntervention.getPlanCost())));
		tests.add(dynamicTest("memberCost: [" + memberCost + "]", () -> assertEquals(memberCost, searchMemberIntervention.getMemberCost())));
		tests.add(dynamicTest("memberId: [" + memberId + "]", () -> assertEquals(memberId, searchMemberIntervention.getMemberId())));
		tests.add(dynamicTest("memberName: [" + memberName + "]", () -> assertEquals(memberName, searchMemberIntervention.getMemberName())));
		tests.add(dynamicTest("memberDateBirth: [" + memberDateBirth + "]", () -> assertEquals(memberDateBirth, searchMemberIntervention.getMemberDateBirth())));
		tests.add(dynamicTest("providerNpi: [" + providerNpi + "]", () -> assertEquals(providerNpi, searchMemberIntervention.getProviderNpi())));
		tests.add(dynamicTest("providerName: [" + providerName + "]", () -> assertEquals(providerName, searchMemberIntervention.getProviderName())));
		tests.add(dynamicTest("providerPhoneNumber: [" + providerPhoneNumber + "]", () -> assertEquals(providerPhoneNumber, searchMemberIntervention.getProviderPhoneNumber())));
		tests.add(dynamicTest("assignedTo: [" + assignedTo + "]", () -> assertEquals(assignedTo, searchMemberIntervention.getAssignedTo())));
		tests.add(dynamicTest("targetProductName: [" + targetProductName + "]", () -> assertEquals(targetProductName, searchMemberIntervention.getTargetProductName())));

		// Perform queueStatusChangeDateTime date validation			
		if ( StringUtils.isNotBlank(createdDateTime) && StringUtils.isNotBlank(searchMemberIntervention.getCreatedDateTime()) )
		{
			tests.add(compareDates("createdDateTime: [" + createdDateTime + "]", createdDateTime, searchMemberIntervention.getCreatedDateTime(),
					new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, null));
		}
		else
		{
			tests.add(dynamicTest("createdDateTime: [" + createdDateTime + "]", () -> assertEquals(createdDateTime, searchMemberIntervention.getCreatedDateTime())));
		}
		
		tests.add(dynamicTest("lastUpdatedBy: [" + lastUpdatedBy + "]", () -> assertEquals(lastUpdatedBy, searchMemberIntervention.getLastUpdatedBy())));
		tests.add(dynamicTest("targetInterventionQueueType: [" + targetInterventionQueueType + "]", () -> assertEquals(targetInterventionQueueType, searchMemberIntervention.getTargetInterventionQueueType())));

		return tests;
	}

	/*
	 * Validations
	 */

	/**
	 * Check if the intervention information contains the search terms.
	 * If the search terms contains multiple WORD then it will spit it and goes through each WORD to determine if
	 * intervention information (i.e. rxccGroupName, memberName, providerName, targetProductName ) contains it.
	 *
	 * @param searchTerm to validate that it is either part of i.e. rxccGroupName, memberName, providerName, targetProductName
	 * @return true if the search terms are found in intervention's information. Otherwise, it returns false.
	 */
	public boolean interventionContains(String searchTerm)
	{
		logger.debug("Starting intervention search response validation");

		Map<String, Boolean> result = new HashMap<String, Boolean>();

		for ( String theSearchTerm : StringUtils.split(searchTerm, " ") )
		{
			if ( StringUtils.containsIgnoreCase(queueStatusChangeDateTime, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(queueStatusCode, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(rxccGroupName, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(memberId, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(memberName, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(memberDateBirth, theSearchTerm)  
					|| StringUtils.containsIgnoreCase(assignedTo, theSearchTerm)  
					|| StringUtils.containsIgnoreCase(targetProductName, theSearchTerm)	 
					|| StringUtils.containsIgnoreCase(lastUpdatedBy, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(providerNpi, theSearchTerm) 
					|| StringUtils.containsIgnoreCase(providerName, theSearchTerm)
					|| StringUtils.containsIgnoreCase(providerPhoneNumber, theSearchTerm)
					|| StringUtils.containsIgnoreCase(createdDateTime, theSearchTerm)
					)

			{
				result.put(theSearchTerm, true);
			}
			else {
				result.put(theSearchTerm, false);
			}
		}

		logger.debug("Intervention search response validation completed");

		return ( result.containsValue(false) ? false : true );
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
	 * Helper methods
	 */

	/**
	 * Return the member intervention information on these fields
	 * <ul>
	 *     <li>queueStatusChangeDateTime</li>
	 *     <li>queueStatusCode</li>
	 *     <li>rxccGroupName</li>
	 *     <li>memberId</li>
	 *     <li>memberName</li>
	 *     <li>memberDateBirth</li>
	 *     <li>providerNpi</li>
	 *     <li>providerName</li>
	 *     <li>providerPhoneNumber</li>
	 *     <li>targetProductName</li>
	 *     <li>assignedTo</li>
	 *     <li>lastUpdatedBy</li>
	 * </ul>
	 * @return
	 */
	public String getSearchFieldValues()
	{
		String interventionInfo = ", queueStatusChangeDateTime='" + queueStatusChangeDateTime + '\'' +
				", queueStatusCode='" + queueStatusCode + '\'' +
				", rxccGroupName='" + rxccGroupName + '\'' +
				", memberId='" + memberId + '\'' +
				", memberName='" + memberName + '\''+
				", memberDateBirth='" + memberDateBirth + '\'' +
				", providerNpi='" + providerNpi + '\'' +
				", providerName='" + providerName + '\'' +
				", providerPhoneNumber='" + providerPhoneNumber + '\''+
				", targetProductName='" + targetProductName + '\''+
				", assignedTo='" + assignedTo + '\''+
				", createdDateTime='" + createdDateTime + '\''+
				", lastUpdatedBy='" + lastUpdatedBy + '\'';
		return interventionInfo;
	}


	/**
	 * Return the member intervention information on these fields
	 * <ul>
	 *     <li>targetInterventionQueueType</li>
	 *  	 * </ul>
	 * @return
	 */
	public String getTargetInterventionQueueTypeFieldValues()
	{
		String targetInterventionQueueTypeInfo = "targetInterventionQueueType='" + targetInterventionQueueType + '\'';
		return targetInterventionQueueTypeInfo;
	}

	/**
	 * Equals is defined if the following are met
	 * - id are the same
	 * 
	 *
	 * @param obj to be compared
	 * @return true if it mets the criteria
	 */
	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof SearchMemberIntervention)
		{
			SearchMemberIntervention searchMemberIntervention = (SearchMemberIntervention) obj;

			if ( id.equals(searchMemberIntervention.getId()) )
				return true;
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

	public String getQueueStatusChangeDateTime() {
		return queueStatusChangeDateTime;
	}

	public void setQueueStatusChangeDateTime(String queueStatusChangeDateTime) {
		this.queueStatusChangeDateTime = queueStatusChangeDateTime;
	}

	public String getQueueStatusCode() {
		return queueStatusCode;
	}

	public void setQueueStatusCode(String queueStatusCode) {
		this.queueStatusCode = queueStatusCode;
	}

	public String getRxccGroupName() {
		return rxccGroupName;
	}

	public void setRxccGroupName(String rxccGroupName) {
		this.rxccGroupName = rxccGroupName;
	}

	public double getPlanCost() {
		return planCost;
	}

	public void setPlanCost(double planCost) {
		this.planCost = planCost;
	}

	public double getMemberCost() {
		return memberCost;
	}

	public void setMemberCost(double memberCost) {
		this.memberCost = memberCost;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberDateBirth() {
		return memberDateBirth;
	}

	public void setMemberDateBirth(String memberDateBirth) {
		this.memberDateBirth = memberDateBirth;
	}

	public String getProviderNpi() {
		return providerNpi;
	}

	public void setProviderNpi(String providerNpi) {
		this.providerNpi = providerNpi;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getProviderPhoneNumber() {
		return providerPhoneNumber;
	}

	public void setProviderPhoneNumber(String providerPhoneNumber) {
		this.providerPhoneNumber = providerPhoneNumber;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getTargetProductName() {
		return targetProductName;
	}

	public void setTargetProductName(String targetProductName) {
		this.targetProductName = targetProductName;
	}

	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public TargetInterventionQueueType getTargetInterventionQueueType() {
		return targetInterventionQueueType;
	}

	public void setTargetInterventionQueueType(TargetInterventionQueueType targetInterventionQueueType) {
		this.targetInterventionQueueType = targetInterventionQueueType;
	}

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}
	
}
