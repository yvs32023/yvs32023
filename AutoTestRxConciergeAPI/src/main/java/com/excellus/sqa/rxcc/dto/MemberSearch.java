/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DTO that corresponds to API member search response
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 03/01/2022
 */
public class MemberSearch extends AbstractJsonDTO<MemberSearch> {

	private static final Logger logger = LoggerFactory.getLogger(MemberSearch.class);

	private String adTenantId;
	private String id;
	private String tenantName;
	private String subscriberId;
	private String dependentCode;
	private String lastName;
	private String firstName;
	private String middleInitial;
	private String dateBirth;
	private String groupName;
	private String tenantId;
	private String memberId;
	private String type;

	//added as part of regression fail 6/2/2022
	private int batchNumber; 

	
	/*
	 * Validations
	 */
	
	@Override
	public List<DynamicNode> compare(MemberSearch dto) {
		// not validation required
		return null;
	}

	/**
	 * Check if the member information contains the search terms.
	 * If the search terms contains multiple WORD then it will spit it and goes through each WORD to determine if
	 * member information (i.e. firstName, lastName, groupName, dateBirth, subscriberId) contains it.
	 *
	 * @param searchTerm to validate that it is either part of first or last name
	 * @return true if the search terms are found in member's information. Otherwise, it returns false.
	 */
	public boolean memberContains(String searchTerm)
	{
		logger.debug("Starting member search response validation");

		Map<String, Boolean> result = new HashMap<String, Boolean>();

		for ( String theSearchTerm : StringUtils.split(searchTerm, " ") )
		{
			if ( StringUtils.containsIgnoreCase(firstName, theSearchTerm) || StringUtils.containsIgnoreCase(lastName, theSearchTerm) ||
					StringUtils.containsIgnoreCase(groupName, theSearchTerm) || StringUtils.containsIgnoreCase(dateBirth, theSearchTerm) ||
					StringUtils.containsIgnoreCase(subscriberId, theSearchTerm) ) {
				result.put(theSearchTerm, true);
			}
			else {
				result.put(theSearchTerm, false);
			}
		}

		logger.debug("Member search response validation completed");

		return ( result.containsValue(false) ? false : true );
	}

	
	/*
	 * Helper methods
	 */

	/**
	 * Return the member information on these fields
	 * <ul>
	 *     <li>firstName</li>
	 *     <li>lastName</li>
	 *     <li>subscriberId</li>
	 *     <li>dateBirth</li>
	 *     <li>groupName</li>
	 * </ul>
	 * @return
	 */
	public String getSearchFieldValues()
	{
		String memberInfo = "firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", subscriberId='" + subscriberId + '\'' +
				", dateBirth='" + dateBirth + '\'' +
				", groupName='" + groupName + '\'';

		return memberInfo;
	}


	/*
	 * Setter / Getter
	 */

	public String getAdTenantId() {
		return adTenantId;
	}

	public void setAdTenantId(String adTenantId) {
		this.adTenantId = adTenantId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getSubscriberId() {
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId) {
		this.subscriberId = subscriberId;
	}

	public String getDependentCode() {
		return dependentCode;
	}

	public void setDependentCode(String dependentCode) {
		this.dependentCode = dependentCode;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstLastName() {
		return this.firstName + " " + this.lastName;
	}

	public String getMiddleInitial() {
		return middleInitial;
	}

	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}

	public String getDateBirth() {
		return dateBirth;
	}

	public void setDateBirth(String dateBirth) {
		this.dateBirth = dateBirth;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int getBatchNumber() {
		return batchNumber;
	}
	
	public void setBatchNumber(float batchNumber) {
		this.batchNumber = (int)batchNumber;
	}
}
