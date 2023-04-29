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
 * DTO for Intervention
 * 
 * JSON schema is not defined as of yet - 3/7/2022
 * 
 * @author Manish Sharma (msharma)
 * @since 03/02/2022
 */
  		   
 @JsonPropertyOrder({"ruleId","type","ruleName","ruleVersion","ruleStatus","avgLengthTherapy"
	 ,"savingsInformationText","ageAndBelow","ageAndGreater","daysSupply","daysSupplySign","costThresholdAmount"
	 ,"costThresholdSign","interventionType","runDailyInd","acceptText","declineText","qualityTitleText"
	 ,"qualityFreeformTargetPrerequisiteInd","qualityFreeFormText","qualityInterventionText","numberTenants",
	 "numberFormularies","formularyTenantsGroups","prerequisites","target","alternatives","faxAlternatives","phoneFollowupInd"})
 @JsonInclude(JsonInclude.Include.NON_NULL)        		   
public class Intervention extends CommonItem
{
	@JsonProperty(required=true)
	private String  ruleId;
	
	@JsonProperty(required=true)
	private String  type;  //constant
	
	@JsonProperty(required=true)
	private String  ruleName;
	
	@JsonProperty(required=true)
	private Integer  ruleVersion;
	
	@JsonProperty(required=true)
	private String  ruleStatus; //enum
	
	@JsonProperty(required=true)
	private Integer  avgLengthTherapy;

	private String  savingsInformationText;
	
	@JsonProperty(required=true)
	private Integer  ageAndBelow;
	
	@JsonProperty(required=true)
	private Integer  ageAndGreater;
	
	@JsonProperty(required=true)
	private Integer  daysSupply;
	
	@JsonProperty(required=true)
	private String  daysSupplySign;  //comparitors
	
	@JsonProperty(required=true)
	private Integer  costThresholdAmount;
	
	@JsonProperty(required=true)
	private String  costThresholdSign; //comparitors
	
	@JsonProperty(required=true)
	private String  interventionType;
	
	@JsonProperty(required=true)
	private Boolean  runDailyInd;
	
	private String  acceptText;
	
	private String  declineText;
	
	private String  qualityTitleText;
	
	private String  qualityFreeformTargetPrerequisiteInd;  //enum
	
	private String  qualityFreeFormText;
	
	private String  qualityInterventionText;
	
	@JsonProperty(required=true)
	private Integer  numberTenants;
	
	@JsonProperty(required=true)
	private Integer  numberFormularies;

	@JsonProperty(required=true)
	private List<FormularyTenantsGroups> formularyTenantsGroups;

	private List<Prerequisites> prerequisites;

	private Target target;

	@JsonProperty(required=true)
	private List<Alternatives> alternatives;

	private List<FaxAlternatives> faxAlternatives;

	private Boolean phoneFollowupInd;

	
	/*
	 * Validations
	 */
	
	/**
	 * Compare intervention properties defined in the schema
	 * 
	 * @param intervention {@link InterventionRule} to compare with
	 * @return test results of comparison
	 */
	public List<DynamicNode> compare(Intervention intervention)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		tests.add(dynamicTest("ruleId [" + ruleId + "]", () -> assertEquals(ruleId, intervention.getRuleId(), getApiInfo(intervention))));
		tests.add(dynamicTest("type [" + type + "]", () -> assertEquals(type, intervention.getType(), getApiInfo(intervention))));
		tests.add(dynamicTest("ruleName [" + ruleName + "]", () -> assertEquals(ruleName, intervention.getRuleName(), getApiInfo(intervention))));
		tests.add(dynamicTest("ruleVersion [" + ruleVersion + "]", () -> assertEquals(ruleVersion, intervention.getRuleVersion(), getApiInfo(intervention))));
		tests.add(dynamicTest("ruleStatus [" + ruleStatus + "]", () -> assertEquals(ruleStatus, intervention.getRuleStatus(), getApiInfo(intervention))));
		tests.add(dynamicTest("avgLengthTherapy [" + avgLengthTherapy + "]", () -> assertEquals(avgLengthTherapy, intervention.getAvgLengthTherapy(), getApiInfo(intervention))));
		tests.add(dynamicTest("savingsInformationText [" + savingsInformationText + "]", () -> assertEquals(savingsInformationText, intervention.getSavingsInformationText(), getApiInfo(intervention))));
		tests.add(dynamicTest("ageAndBelow [" + ageAndBelow + "]", () -> assertEquals(ageAndBelow, intervention.getAgeAndBelow(), getApiInfo(intervention))));
		tests.add(dynamicTest("ageAndGreater [" + ageAndGreater + "]", () -> assertEquals(ageAndGreater, intervention.getAgeAndGreater(), getApiInfo(intervention))));
		tests.add(dynamicTest("daysSupply [" + daysSupply + "]", () -> assertEquals(daysSupply, intervention.getDaysSupply(), getApiInfo(intervention))));
		tests.add(dynamicTest("daysSupplySign [" + daysSupplySign + "]", () -> assertEquals(daysSupplySign, intervention.getDaysSupplySign(), getApiInfo(intervention))));
		tests.add(dynamicTest("costThresholdAmount [" + costThresholdAmount + "]", () -> assertEquals(costThresholdAmount, intervention.getCostThresholdAmount(), getApiInfo(intervention))));
		tests.add(dynamicTest("costThresholdSign [" + costThresholdSign + "]", () -> assertEquals(costThresholdSign, intervention.getCostThresholdSign(), getApiInfo(intervention))));
		tests.add(dynamicTest("interventionType [" + interventionType + "]", () -> assertEquals(interventionType, intervention.getInterventionType(), getApiInfo(intervention))));
		tests.add(dynamicTest("runDailyInd [" + runDailyInd + "]", () -> assertEquals(runDailyInd, intervention.getRunDailyInd(), getApiInfo(intervention))));
		tests.add(dynamicTest("acceptText [" + acceptText + "]", () -> assertEquals(acceptText, intervention.getAcceptText(), getApiInfo(intervention))));
		tests.add(dynamicTest("declineText [" + declineText + "]", () -> assertEquals(declineText, intervention.getDeclineText(), getApiInfo(intervention))));
		tests.add(dynamicTest("qualityTitleText [" + qualityTitleText + "]", () -> assertEquals(qualityTitleText, intervention.getQualityTitleText())));
		tests.add(dynamicTest("qualityFreeformTargetPrerequisiteInd [" + qualityFreeformTargetPrerequisiteInd + "]", () -> assertEquals(qualityFreeformTargetPrerequisiteInd, intervention.getQualityFreeformTargetPrerequisiteInd(), getApiInfo(intervention))));
		tests.add(dynamicTest("qualityFreeFormText [" + qualityFreeFormText + "]", () -> assertEquals(qualityFreeFormText, intervention.getQualityFreeFormText(), getApiInfo(intervention))));
		tests.add(dynamicTest("qualityInterventionText [" + qualityInterventionText + "]", () -> assertEquals(qualityInterventionText, intervention.getQualityInterventionText(), getApiInfo(intervention))));
		tests.add(dynamicTest("numberTenants [" + numberTenants + "]", () -> assertEquals(numberTenants, intervention.getNumberTenants(), getApiInfo(intervention))));
		tests.add(dynamicTest("numberFormularies [" + numberFormularies + "]", () -> assertEquals(numberFormularies, intervention.getNumberFormularies(), getApiInfo(intervention))));
		//tests.add(dynamicContainer("Phone numbers", comparePhones(anotherMember.getPhoneNumbers())));
		tests.add(dynamicTest("formularyTenantsGroups [" + formularyTenantsGroups + "]", () -> assertEquals(formularyTenantsGroups, intervention.getFormularyTenantsGroups(), getApiInfo(intervention))));
		tests.add(dynamicTest("prerequisites [" + prerequisites + "]", () -> assertEquals(prerequisites, intervention.getPrerequisites(), getApiInfo(intervention))));
		tests.add(dynamicTest("target [" + target + "]", () -> assertEquals(target, intervention.getTarget(), getApiInfo(intervention))));
		tests.add(dynamicTest("alternatives  [" + alternatives  + "]", () -> assertEquals(alternatives , intervention.getAlternatives(), getApiInfo(intervention))));
		tests.add(dynamicTest("faxAlternatives [" + faxAlternatives + "]", () -> assertEquals(faxAlternatives, intervention.getFaxAlternatives(), getApiInfo(intervention))));
		tests.add(dynamicTest("phoneFollowupInd [" + phoneFollowupInd + "]", () -> assertEquals(phoneFollowupInd, intervention.getPhoneFollowupInd(), getApiInfo(intervention))));
		
		
		tests.addAll( super.compare(intervention) );
		
		return tests;
	}
	
	
	/*
	 * Helper methods
	 */

	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof InterventionRule)
		{
			InterventionRule intervention = (InterventionRule) obj;

			if ( ruleId.equals(intervention.getRuleId()) )
				return true;
		}
		
		return false;
	}
	
	
	/*
	 * Setter / Getter
	 */


	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public Integer getRuleVersion() {
		return ruleVersion;
	}

	public void setRuleVersion(Integer ruleVersion) {
		this.ruleVersion = ruleVersion;
	}

	public String getRuleStatus() {
		return ruleStatus;
	}

	public void setRuleStatus(String ruleStatus) {
		this.ruleStatus = ruleStatus;
	}

	public Integer getAvgLengthTherapy() {
		return avgLengthTherapy;
	}

	public void setAvgLengthTherapy(Integer avgLengthTherapy) {
		this.avgLengthTherapy = avgLengthTherapy;
	}

	public String getSavingsInformationText() {
		return savingsInformationText;
	}

	public void setSavingsInformationText(String savingsInformationText) {
		this.savingsInformationText = savingsInformationText;
	}

	public Integer getAgeAndBelow() {
		return ageAndBelow;
	}

	public void setAgeAndBelow(Integer ageAndBelow) {
		this.ageAndBelow = ageAndBelow;
	}

	public Integer getAgeAndGreater() {
		return ageAndGreater;
	}

	public void setAgeAndGreater(Integer ageAndGreater) {
		this.ageAndGreater = ageAndGreater;
	}

	public Integer getDaysSupply() {
		return daysSupply;
	}

	public void setDaysSupply(Integer daysSupply) {
		this.daysSupply = daysSupply;
	}

	public String getDaysSupplySign() {
		return daysSupplySign;
	}

	public void setDaysSupplySign(String daysSupplySign) {
		this.daysSupplySign = daysSupplySign;
	}

	public Integer getCostThresholdAmount() {
		return costThresholdAmount;
	}

	public void setCostThresholdAmount(Integer costThresholdAmount) {
		this.costThresholdAmount = costThresholdAmount;
	}

	public String getCostThresholdSign() {
		return costThresholdSign;
	}

	public void setCostThresholdSign(String costThresholdSign) {
		this.costThresholdSign = costThresholdSign;
	}

	public String getInterventionType() {
		return interventionType;
	}

	public void setInterventionType(String interventionType) {
		this.interventionType = interventionType;
	}

	public Boolean getRunDailyInd() {
		return runDailyInd;
	}

	public void setRunDailyInd(Boolean runDailyInd) {
		this.runDailyInd = runDailyInd;
	}

	public String getAcceptText() {
		return acceptText;
	}

	public void setAcceptText(String acceptText) {
		this.acceptText = acceptText;
	}

	public String getDeclineText() {
		return declineText;
	}

	public void setDeclineText(String declineText) {
		this.declineText = declineText;
	}

	public String getQualityTitleText() {
		return qualityTitleText;
	}

	public void setQualityTitleText(String qualityTitleText) {
		this.qualityTitleText = qualityTitleText;
	}

	public String getQualityFreeformTargetPrerequisiteInd() {
		return qualityFreeformTargetPrerequisiteInd;
	}

	public void setQualityFreeformTargetPrerequisiteInd(String qualityFreeformTargetPrerequisiteInd) {
		this.qualityFreeformTargetPrerequisiteInd = qualityFreeformTargetPrerequisiteInd;
	}

	public String getQualityFreeFormText() {
		return qualityFreeFormText;
	}

	public void setQualityFreeFormText(String qualityFreeFormText) {
		this.qualityFreeFormText = qualityFreeFormText;
	}

	public String getQualityInterventionText() {
		return qualityInterventionText;
	}

	public void setQualityInterventionText(String qualityInterventionText) {
		this.qualityInterventionText = qualityInterventionText;
	}

	public Integer getNumberTenants() {
		return numberTenants;
	}

	public void setNumberTenants(Integer numberTenants) {
		this.numberTenants = numberTenants;
	}

	public Integer getNumberFormularies() {
		return numberFormularies;
	}

	public void setNumberFormularies(Integer numberFormularies) {
		this.numberFormularies = numberFormularies;
	}

	public List<FormularyTenantsGroups> getFormularyTenantsGroups() {
		return formularyTenantsGroups;
	}

	public void setFormularyTenantsGroups(List<FormularyTenantsGroups> formularyTenantsGroups) {
		this.formularyTenantsGroups = formularyTenantsGroups;
	}

	public List<Prerequisites> getPrerequisites() {
		return prerequisites;
	}

	public void setPrerequisites(List<Prerequisites> prerequisites) {
		this.prerequisites = prerequisites;
	}

	public Target getTarget() {
		return target;
	}


	public void setTarget(Target target) {
		this.target = target;
	}

	public List<Alternatives> getAlternatives() {
		return alternatives;
	}

	public void setAlternatives(List<Alternatives> alternatives) {
		this.alternatives = alternatives;
	}

	public List<FaxAlternatives> getFaxAlternatives() {
		return faxAlternatives;
	}

	public void setFaxAlternatives(List<FaxAlternatives> faxAlternatives) {
		this.faxAlternatives = faxAlternatives;
	}

	public Boolean getPhoneFollowupInd() {
		return phoneFollowupInd;
	}

	public void setPhoneFollowupInd(Boolean phoneFollowupInd) {
		this.phoneFollowupInd = phoneFollowupInd;
	}


}
