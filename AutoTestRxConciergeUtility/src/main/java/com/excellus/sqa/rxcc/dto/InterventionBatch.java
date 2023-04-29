/**
 * 
 * @copyright 2023 Excellus BCBS
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


/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 03/04/2023
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterventionBatch extends CommonItem {

	@JsonProperty(required=true)
	private String interventionBatchId;

	@JsonProperty(required=true)
	private String type;

	@JsonProperty(required=true)
	private Integer  tenantId;

	@JsonProperty(required=true)
	private String ruleId;

	private String memberId;

	@JsonProperty(required=true)
	private String groupId;

	@JsonProperty(required=true)
	private String subscriberId;

	@JsonProperty(required=true)
	private String dependentCode;

	private String edwPersonUniqueId;

	private String rxccGroupName;

	private String providerNPI;

	private String memberFirstName;

	private String memberLastName;

	private String memberDateOfBirth;

	private String targetNDC;

	private String targetNDCDescription;

	private String prerequisiteNDC;

	private String prerequisiteNDCDescription;

	private boolean hasErrorsInd = false;

	private List<String> errorDescription;

	private String batchQualityFileName;


	/**
	 * Default constructor
	 */
	public InterventionBatch() {
		this.interventionBatchId = "";
		this.type = "";
		this.tenantId = 0;
		this.ruleId = "";
		this.groupId = "";
		this.subscriberId = "";
		this.dependentCode = "";
	}

	/**
	 * Constructor
	 * @param interventionBatchId,type,tenantId,ruleId,groupId,subscriberId,dependentCode
	 */
	public InterventionBatch( String interventionBatchId,  String type,  Integer tenantId,
			String ruleId,  String groupId,  String subscriberId,
			String dependentCode) {
		super();
		this.interventionBatchId = interventionBatchId;
		this.type = type;
		this.tenantId = tenantId;
		this.ruleId = ruleId;
		this.groupId = groupId;
		this.subscriberId = subscriberId;
		this.dependentCode = dependentCode;
	}

	/*
	 * Validations
	 */

	/**
	 * Compare interventionBatch properties defined in the schema
	 * 
	 * @param interventionBatch {@link InterventionBatch} to compare with
	 * @return test results of comparison
	 */
	public List<DynamicNode> compare(InterventionBatch interventionBatch)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("interventionBatchId [" + interventionBatchId + "]", () -> assertEquals(interventionBatchId, interventionBatch.getInterventionBatchId(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("type [" + type + "]", () -> assertEquals(type, interventionBatch.getType(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("tenantId [" + tenantId + "]", () -> assertEquals(tenantId, interventionBatch.getTenantId(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("ruleId [" + ruleId + "]", () -> assertEquals(ruleId, interventionBatch.getRuleId(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("memberId [" + memberId + "]", () -> assertEquals(memberId, interventionBatch.getMemberId(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("groupId [" + groupId + "]", () -> assertEquals(groupId, interventionBatch.getGroupId(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("subscriberId [" + subscriberId + "]", () -> assertEquals(subscriberId, interventionBatch.getSubscriberId(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("dependentCode [" + dependentCode + "]", () -> assertEquals(dependentCode, interventionBatch.getDependentCode(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("edwPersonUniqueId [" + edwPersonUniqueId + "]", () -> assertEquals(edwPersonUniqueId, interventionBatch.getEdwPersonUniqueId(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("rxccGroupName [" + rxccGroupName + "]", () -> assertEquals(rxccGroupName, interventionBatch.getRxccGroupName(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("providerNPI [" + providerNPI + "]", () -> assertEquals(providerNPI, interventionBatch.getProviderNPI(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("memberFirstName [" + memberFirstName + "]", () -> assertEquals(memberFirstName, interventionBatch.getMemberFirstName(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("memberLastName [" + memberLastName + "]", () -> assertEquals(memberLastName, interventionBatch.getMemberLastName(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("memberDateOfBirth [" + memberDateOfBirth + "]", () -> assertEquals(memberDateOfBirth, interventionBatch.getMemberDateOfBirth(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("targetNDC [" + targetNDC + "]", () -> assertEquals(targetNDC, interventionBatch.getTargetNDC(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("targetNDCDescription [" + targetNDCDescription + "]", () -> assertEquals(targetNDCDescription, interventionBatch.getTargetNDCDescription(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("prerequisiteNDC [" + prerequisiteNDC + "]", () -> assertEquals(prerequisiteNDC, interventionBatch.getPrerequisiteNDC(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("prerequisiteNDCDescription [" + prerequisiteNDCDescription + "]", () -> assertEquals(prerequisiteNDCDescription, interventionBatch.getPrerequisiteNDCDescription(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("hasErrorsInd [" + hasErrorsInd + "]", () -> assertEquals(hasErrorsInd, interventionBatch.isHasErrorsInd(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("errorDescription [" + errorDescription + "]", () -> assertEquals(errorDescription, interventionBatch.getErrorDescription(), getApiInfo(interventionBatch))));
		tests.add(dynamicTest("batchQualityFileName [" + batchQualityFileName + "]", () -> assertEquals(batchQualityFileName, interventionBatch.getBatchQualityFileName(), getApiInfo(interventionBatch))));

		tests.addAll( super.compare(interventionBatch) );

		return tests;
	}



	/*
	 * Setter / Getter
	 */
	public String getInterventionBatchId() {
		return interventionBatchId;
	}

	public void setInterventionBatchId(String interventionBatchId) {
		this.interventionBatchId = interventionBatchId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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

	public String getEdwPersonUniqueId() {
		return edwPersonUniqueId;
	}

	public void setEdwPersonUniqueId(String edwPersonUniqueId) {
		this.edwPersonUniqueId = edwPersonUniqueId;
	}

	public String getRxccGroupName() {
		return rxccGroupName;
	}

	public void setRxccGroupName(String rxccGroupName) {
		this.rxccGroupName = rxccGroupName;
	}

	public String getProviderNPI() {
		return providerNPI;
	}

	public void setProviderNPI(String providerNPI) {
		this.providerNPI = providerNPI;
	}

	public String getMemberFirstName() {
		return memberFirstName;
	}

	public void setMemberFirstName(String memberFirstName) {
		this.memberFirstName = memberFirstName;
	}

	public String getMemberLastName() {
		return memberLastName;
	}

	public void setMemberLastName(String memberLastName) {
		this.memberLastName = memberLastName;
	}

	public String getMemberDateOfBirth() {
		return memberDateOfBirth;
	}

	public void setMemberDateOfBirth(String memberDateOfBirth) {
		this.memberDateOfBirth = memberDateOfBirth;
	}

	public String getTargetNDC() {
		return targetNDC;
	}

	public void setTargetNDC(String targetNDC) {
		this.targetNDC = targetNDC;
	}

	public String getTargetNDCDescription() {
		return targetNDCDescription;
	}

	public void setTargetNDCDescription(String targetNDCDescription) {
		this.targetNDCDescription = targetNDCDescription;
	}

	public String getPrerequisiteNDC() {
		return prerequisiteNDC;
	}

	public void setPrerequisiteNDC(String prerequisiteNDC) {
		this.prerequisiteNDC = prerequisiteNDC;
	}

	public String getPrerequisiteNDCDescription() {
		return prerequisiteNDCDescription;
	}

	public void setPrerequisiteNDCDescription(String prerequisiteNDCDescription) {
		this.prerequisiteNDCDescription = prerequisiteNDCDescription;
	}

	public boolean isHasErrorsInd() {
		return hasErrorsInd;
	}

	public void setHasErrorsInd(boolean hasErrorsInd) {
		this.hasErrorsInd = hasErrorsInd;
	}

	public List<String> getErrorDescription() {
		return errorDescription;
	}

	public void setErrorDescription(List<String> errorDescription) {
		this.errorDescription = errorDescription;
	}

	public String getBatchQualityFileName() {
		return batchQualityFileName;
	}

	public void setBatchQualityFileName(String batchQualityFileName) {
		this.batchQualityFileName = batchQualityFileName;
	}
}
