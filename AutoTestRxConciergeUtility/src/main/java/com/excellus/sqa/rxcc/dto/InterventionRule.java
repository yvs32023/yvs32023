/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicNode;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.Utility;
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

@JsonPropertyOrder({"ruleId","type","ruleName","ruleVersion","ruleStatus","averageLengthTherapy"
	,"savingsInformationText","ageAndBelow","ageAndGreater","daysSupply","daysSupplySign","costThresholdAmount"
	,"costThresholdSign","interventionType","runDailyInd","acceptText","declineText","qualityTitleText"
	,"qualityFreeformTargetPrerequisiteInd","qualityFreeFormText","qualityInterventionText","numberTenants",
	"numberFormularies","formularyTenantsGroups","prerequisites","target","alternatives","faxAlternatives","phoneFollowupInd"})
@JsonInclude(JsonInclude.Include.NON_NULL)                 
public class InterventionRule extends CommonItem
{
	@JsonProperty(required=true)
	private String  ruleId;

	@JsonProperty(required=true)
	private String  type;

	@JsonProperty(required=true)
	private String  ruleName;

	@JsonProperty(required=true)
	private Integer  ruleVersion;

	@JsonProperty(required=true)
	//private RuleStatus  ruleStatus; //enum
	private Integer  ruleStatus; //enum

	@JsonProperty(required=true)
	private Integer  averageLengthTherapy;

	private String  savingsInformationText;

	@JsonProperty(required=true)
	private Integer  ageAndBelow;

	@JsonProperty(required=true)
	private Integer  ageAndGreater;

	@JsonProperty(required=true)
	private Integer  daysSupply;

	@JsonProperty(required=true)
	//private Comparitors  daysSupplySign;  //comparitors
	private String  daysSupplySign;  //comparitors

	@JsonProperty(required=true)
	private Integer  costThresholdAmount;

	@JsonProperty(required=true)
	//private Comparitors  costThresholdSign; //comparitors
	private String  costThresholdSign; //comparitors

	@JsonProperty(required=true)
	private String  interventionType;

	@JsonProperty(required=true)
	private Boolean  runDailyInd;

	private String  acceptText;

	private String  declineText;

	private String  qualityTitleText;

	private Integer  qualityFreeformTargetPrerequisiteInd;  //enum
	//private QualityFreeformTargetPrerequisiteInd  qualityFreeformTargetPrerequisiteInd;  //enum

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

	private String faxAlternativeText; 

	private List<FaxAlternatives> faxAlternatives;

	private Boolean phoneFollowupInd;


	/*
	 * Validations
	 */

	/**
	 * Compare interventionRule properties defined in the schema
	 * 
	 * @param interventionRule {@link InterventionRule} to compare with
	 * @return test results of comparison
	 */
	public List<DynamicNode> compare(InterventionRule interventionRule)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("ruleId [" + ruleId + "]", () -> assertEquals(ruleId, interventionRule.getRuleId(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("type [" + type + "]", () -> assertEquals(type, interventionRule.getType(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("ruleName [" + ruleName + "]", () -> assertEquals(ruleName, interventionRule.getRuleName(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("ruleVersion [" + ruleVersion + "]", () -> assertEquals(ruleVersion, interventionRule.getRuleVersion(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("ruleStatus [" + ruleStatus + "]", () -> assertEquals(ruleStatus, interventionRule.getRuleStatus(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("averageLengthTherapy [" + averageLengthTherapy + "]", () -> assertEquals(averageLengthTherapy, interventionRule.getAverageLengthTherapy(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("savingsInformationText [" + savingsInformationText + "]", () -> assertEquals(savingsInformationText, interventionRule.getSavingsInformationText(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("ageAndBelow [" + ageAndBelow + "]", () -> assertEquals(ageAndBelow, interventionRule.getAgeAndBelow(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("ageAndGreater [" + ageAndGreater + "]", () -> assertEquals(ageAndGreater, interventionRule.getAgeAndGreater(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("daysSupply [" + daysSupply + "]", () -> assertEquals(daysSupply, interventionRule.getDaysSupply(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("daysSupplySign [" + daysSupplySign + "]", () -> assertEquals(daysSupplySign, interventionRule.getDaysSupplySign(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("costThresholdAmount [" + costThresholdAmount + "]", () -> assertEquals(costThresholdAmount, interventionRule.getCostThresholdAmount(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("costThresholdSign [" + costThresholdSign + "]", () -> assertEquals(costThresholdSign, interventionRule.getCostThresholdSign(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("interventionType [" + interventionType + "]", () -> assertEquals(interventionType, interventionRule.getInterventionType(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("runDailyInd [" + runDailyInd + "]", () -> assertEquals(runDailyInd, interventionRule.getRunDailyInd(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("acceptText [" + acceptText + "]", () -> assertEquals(acceptText, interventionRule.getAcceptText(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("declineText [" + declineText + "]", () -> assertEquals(declineText, interventionRule.getDeclineText(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("qualityTitleText [" + qualityTitleText + "]", () -> assertEquals(qualityTitleText, interventionRule.getQualityTitleText(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("qualityFreeformTargetPrerequisiteInd [" + qualityFreeformTargetPrerequisiteInd + "]", () -> assertEquals(qualityFreeformTargetPrerequisiteInd, interventionRule.getQualityFreeformTargetPrerequisiteInd(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("qualityFreeFormText [" + qualityFreeFormText + "]", () -> assertEquals(qualityFreeFormText, interventionRule.getQualityFreeFormText(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("qualityInterventionText [" + qualityInterventionText + "]", () -> assertEquals(qualityInterventionText, interventionRule.getQualityInterventionText(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("numberTenants [" + numberTenants + "]", () -> assertEquals(numberTenants, interventionRule.getNumberTenants(), getApiInfo(interventionRule))));
		tests.add(dynamicTest("numberFormularies [" + numberFormularies + "]", () -> assertEquals(numberFormularies, interventionRule.getNumberFormularies(), getApiInfo(interventionRule))));

		tests.add(dynamicTest("FormularyTenantsGroups  (count) [" + formularyTenantsGroups.size() + "]", () -> assertEquals(formularyTenantsGroups.size(), interventionRule.getFormularyTenantsGroups().size(), getApiInfo(interventionRule))));

		/*
		 * Validate each of the formularyTenantsGroups
		 */

		for ( FormularyTenantsGroups expected : formularyTenantsGroups )
		{
			boolean found = false;
			for ( FormularyTenantsGroups actual : interventionRule.getFormularyTenantsGroups() )
			{
				if ( expected.equals(actual) )
				{
					found = true;

					tests.add(dynamicContainer("FormularyTenantsGroups [" + id + "]", expected.compare(actual)) );  // compare the formularyTenantsGroups

					break;
				}
			}

			if ( !found ) {
				tests.add(dynamicTest("FormularyTenantsGroups not found", () -> fail("The formularyTenantsGroups with ID " + id + " is missing \n" + getApiInfo(interventionRule))));
			}
		}


		//performs object null validation
		if (interventionRule.getPrerequisites() != null)
		{
			tests.add(dynamicTest("Prerequisites (count) [" + prerequisites.size() + "]", () -> assertEquals(prerequisites.size(), interventionRule.getPrerequisites().size(), getApiInfo(interventionRule))));

			/*
			 * Validate each of the prerequisites
			 */

			for ( Prerequisites expected : prerequisites )
			{
				boolean found = false;
				for ( Prerequisites actual : interventionRule.getPrerequisites() )
				{
					if ( expected.equals(actual) )
					{
						found = true;

						tests.add(dynamicContainer("Prerequisites [" + id + "]", expected.compare(actual)) );   // compare the prerequisites

						break;
					}
				}

				if ( !found ) {
					tests.add(dynamicTest("Prerequisites not found", () -> fail("The prerequisites with ID " + id + " is missing \n" + getApiInfo(interventionRule))));
				}
			}
		}
		else
		{
			tests.add(dynamicTest("prerequisites: [" + prerequisites + "]", () -> assertEquals(prerequisites, interventionRule.getPrerequisites(), getApiInfo(interventionRule))));	
		}


		//performs object null validation
		if (interventionRule.getTarget() != null)
		{
			tests.add(dynamicContainer("Target", target.compare(interventionRule.getTarget())));
		}
		else
		{
			tests.add(dynamicTest("target: [" + target + "]", () -> assertEquals(target, interventionRule.getTarget(), getApiInfo(interventionRule))));	
		}

		tests.add(dynamicTest("Alternatives (count) [" + alternatives.size() + "]", () -> assertEquals(alternatives.size() , interventionRule.getAlternatives().size(), getApiInfo(interventionRule))));

		/*
		 * Validate each of the alternatives
		 */

		for ( Alternatives expected : alternatives )
		{
			boolean found = false;
			for ( Alternatives actual : interventionRule.getAlternatives() )
			{
				if ( expected.equals(actual) )
				{
					found = true;

					tests.add(dynamicContainer("Alternatives [" + id + "]", expected.compare(actual)) );    // compare the alternatives

					break;
				}
			}

			if ( !found ) {
				tests.add(dynamicTest("Alternatives not found", () -> fail("The alternatives with ID " + id + " is missing \n" + getApiInfo(interventionRule))));
			}
		}

		tests.add(dynamicTest("faxAlternativeText [" + faxAlternativeText + "]", () -> assertEquals(faxAlternativeText, interventionRule.getFaxAlternativeText(), getApiInfo(interventionRule))));

		//performs object null validation
		if(interventionRule.getFaxAlternatives() != null)
		{
			tests.add(dynamicTest("FaxAlternatives (count) [" + faxAlternatives.size() + "]", () -> assertEquals(faxAlternatives.size(), interventionRule.getFaxAlternatives().size(), getApiInfo(interventionRule))));

			/*
			 * Validate each of the faxAlternatives
			 */

			for ( FaxAlternatives expected : faxAlternatives )
			{
				boolean found = false;
				for ( FaxAlternatives actual : interventionRule.getFaxAlternatives() )
				{
					if ( expected.equals(actual) )
					{
						found = true;

						tests.add(dynamicContainer("FaxAlternatives [" + id + "]", expected.compare(actual)) ); // compare the faxAlternatives

						break;
					}
				}

				if ( !found ) {
					tests.add(dynamicTest("FaxAlternatives not found", () -> fail("The faxAlternatives with ID " + id + " is missing \n" + getApiInfo(interventionRule))));
				}
			}
		}
		else
		{
			tests.add(dynamicTest("faxAlternatives [" + faxAlternatives + "]", () -> assertEquals(faxAlternatives, interventionRule.getFaxAlternatives(), getApiInfo(interventionRule))));
		}

		tests.add(dynamicTest("phoneFollowupInd [" + phoneFollowupInd + "]", () -> assertEquals(phoneFollowupInd, interventionRule.getPhoneFollowupInd(), getApiInfo(interventionRule))));

		tests.addAll( super.compare(interventionRule) );
		return tests;
	}
	/**
	 * Compare UI interventionRule properties defined in the schema
	 * @ntagore ( 10/11/22)
	 * @param interventionRule {@link InterventionRule} to compare with
	 * @return test results of comparison
	 */
	public List<DynamicNode> compareUI(InterventionRule interventionRule)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("ruleName [" + ruleName + "]", () -> assertEquals(ruleName, interventionRule.getRuleName())));

		tests.add(dynamicTest("ruleStatus [" + ruleStatus + "]", () -> assertEquals(ruleStatus, interventionRule.getRuleStatus())));

		tests.add(dynamicTest("runDailyInd [" + runDailyInd + "]", () -> assertEquals(runDailyInd, interventionRule.getRunDailyInd())));
		tests.add(dynamicTest("numberTenants [" + numberTenants + "]", () -> assertEquals(numberTenants, interventionRule.getNumberTenants())));
		tests.add(dynamicTest("numberFormularies [" + numberFormularies + "]", () -> assertEquals(numberFormularies, interventionRule.getNumberFormularies())));
		System.out.println(" *************interventionRule.getCreatedDateTime() ****" + interventionRule.getCreatedDateTime());
		tests.add(dynamicTest("createdDateTime: [" + createdDateTime + "]", 
				() -> assertEquals(Utility.convertCosmosDateToUI(createdDateTime, "MM/dd/YYYY"), Utility.convertCosmosDateToUI(interventionRule.getCreatedDateTime(), "MM/dd/YYYY")    )));
		System.out.println(" *************LastUpdateddate time ****" + interventionRule.getLastUpdatedDateTime());
		tests.add(dynamicTest("lastUpdatedDateTime: [" + lastUpdatedDateTime + "]", 
				() -> assertEquals(Utility.convertCosmosDateToUI(lastUpdatedDateTime, "MM/dd/YYYY"), Utility.convertCosmosDateToUI(interventionRule.getLastUpdatedDateTime(), "MM/dd/YYYY") )));

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
			InterventionRule interventionRule = (InterventionRule) obj;

			if ( ruleId.equals(interventionRule.getRuleId()) )
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

	//	public int getRuleStatus() {
	//		return ruleStatus.getRuleStatusIndex();
	//	}
	//
	//	public void setRuleStatus(RuleStatus ruleStatus) {
	//		this.ruleStatus = ruleStatus;
	//	}
	//	
	//	public void setRuleStatus(Integer ruleStatus) {
	//		this.ruleStatus = RuleStatus.valueOfEnum(ruleStatus);
	//	}

	public Integer getRuleStatus() {
		return ruleStatus;
	}
	public void setRuleStatus(Integer ruleStatus) {
		this.ruleStatus = ruleStatus;
	}

	public Integer getAverageLengthTherapy() {
		return averageLengthTherapy;
	}

	public void setAverageLengthTherapy(Integer averageLengthTherapy) {
		this.averageLengthTherapy = averageLengthTherapy;
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

	//	public Comparitors getDaysSupplySign() {
	//		return daysSupplySign;
	//	}
	//
	//	public void setDaysSupplySign(Comparitors daysSupplySign) {
	//		this.daysSupplySign = daysSupplySign;
	//	}
	//
	//	public void setDaysSupplySign(String daysSupplySign) {
	//		this.daysSupplySign = Comparitors.valueOfEnum(daysSupplySign);
	//	}

	public String getDaysSupplySign() {
		return daysSupplySign;
	}
	public void setDaysSupplySign(String daysSupplySign) {
		this.daysSupplySign = daysSupplySign;
	}

	public Integer getCostThresholdAmount() {
		return costThresholdAmount;
	}

	public void setCostThresholdAmount(float costThresholdAmount) {
		this.costThresholdAmount = (int)costThresholdAmount;
	}

	//	public Comparitors getCostThresholdSign() {
	//		return costThresholdSign;
	//	}
	//
	//	public void setCostThresholdSign(Comparitors costThresholdSign) {
	//		this.costThresholdSign = costThresholdSign;
	//	}
	//
	//	public void setCostThresholdSign(String costThresholdSign) {
	//		this.costThresholdSign = Comparitors.valueOfEnum(costThresholdSign);
	//	}
	//	
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

	//	public QualityFreeformTargetPrerequisiteInd getQualityFreeformTargetPrerequisiteInd() {
	//		return qualityFreeformTargetPrerequisiteInd;
	//	}
	//
	//	public void setQualityFreeformTargetPrerequisiteInd(QualityFreeformTargetPrerequisiteInd qualityFreeformTargetPrerequisiteInd) {
	//		this.qualityFreeformTargetPrerequisiteInd = qualityFreeformTargetPrerequisiteInd;
	//	}

	public Integer getQualityFreeformTargetPrerequisiteInd() {
		return qualityFreeformTargetPrerequisiteInd;
	}
	public void setQualityFreeformTargetPrerequisiteInd(Integer qualityFreeformTargetPrerequisiteInd) {
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

	public String getFaxAlternativeText() {
		return faxAlternativeText;
	}
	public void setFaxAlternativeText(String faxAlternativeText) {
		this.faxAlternativeText = faxAlternativeText;
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

	public enum RuleStatus 
	{
		SIMULATION  (0, "Simulation"),
		ACTIVE      (1, "Active"),
		INACTIVE    (2, "Inactive"),
		DISABLED    (3, "Disabled");

		private int ruleStatusIndex;
		private String item;

		private RuleStatus(int ruleStatus, String item)
		{
			this.ruleStatusIndex = ruleStatus; 
			this.item = item;
		}
		public int getRuleStatusIndex()
		{
			return this.ruleStatusIndex;
		}

		public  String getRuleStatusValue()
		{
			return item;
		}

		/*
		 * UI validation of the RuleStatus
		 *  @ntagore 10/17/22
		 */
		public static final RuleStatus valueOfEnum(String valueOf)
		{
			if (valueOf.equalsIgnoreCase("Active"))
			{
				return ACTIVE;
			}

			else if (valueOf.equalsIgnoreCase("Simulation"))
			{
				return SIMULATION;
			}

			else if (valueOf.equalsIgnoreCase("Inactive"))
			{
				return INACTIVE;
			}

			else if (valueOf.equalsIgnoreCase("Disabled"))
			{
				return DISABLED;
			}

			else
			{
				return null;
			}
		}

		/*
		 * API validation of the RuleStatus
		 * retrveing integer enum of the rule status
		 */
		public static final RuleStatus valueOfEnum(int ruleStatusIndex)
		{
			if (ACTIVE.ruleStatusIndex == ruleStatusIndex )
			{
				return ACTIVE;
			}

			else if (SIMULATION.ruleStatusIndex == ruleStatusIndex)
			{
				return SIMULATION;
			}

			else if (INACTIVE.ruleStatusIndex == ruleStatusIndex)
			{
				return INACTIVE;
			}

			else if (DISABLED.ruleStatusIndex == ruleStatusIndex)
			{
				return DISABLED;
			}

			else
			{
				throw new TestConfigurationException(String.format("Invalid enum value for ruleStatus", ruleStatusIndex) );
			}
		}

		@Override
		public String toString() {
			return item;
		}

	}

	public enum QualityFreeformTargetPrerequisiteInd 
	{
		NONE         (0, "None"),
		TARGET       (1, "Target"),
		FREE_FORM    (2, "Free form");

		private int qualityFreeformTargetPrerequisiteIndIndex;
		private String item;

		private QualityFreeformTargetPrerequisiteInd(int qualityFreeformTargetPrerequisiteInd, String item)
		{
			this.qualityFreeformTargetPrerequisiteIndIndex = qualityFreeformTargetPrerequisiteInd; 
			this.item = item;
		}
		public int getQualityFreeformTargetPrerequisiteIndIndex()
		{
			return this.qualityFreeformTargetPrerequisiteIndIndex;
		}

		public  String getQualityFreeformTargetPrerequisiteIndValue()
		{
			return item;
		}

		@Override
		public String toString() {
			return item;
		}

	}





}
//	//Comparitors for both daysSupplySign & costThresholdSign
//	public enum Comparitors 
//	{
//		LESS_THAN                  ("<"),
//		GREATER_THAN               (">"),
//		LESS_THAN_OR_EQUAL_TO      ("<="),
//		GREATER_THAN_OR_EQUAL_TO   (">="),
//		EQUAL_TO                   ("=");
//
//		private String comparitors;
//
//		private Comparitors( String comparitors)
//		{
//
//			this.comparitors = comparitors;
//		}
//
//		public  String getComparitors()
//		{
//			return comparitors;
//		}
//
//		public static final Comparitors valueOfEnum(String valueOf)
//		{
//			if (valueOf.equalsIgnoreCase("<"))
//			{
//				return LESS_THAN;
//			}
//
//			else if (valueOf.equalsIgnoreCase(">"))
//			{
//				return GREATER_THAN;
//			}
//
//			else if (valueOf.equalsIgnoreCase("<="))
//			{
//				return LESS_THAN_OR_EQUAL_TO;
//			}
//
//			else if (valueOf.equalsIgnoreCase(">="))
//			{
//				return GREATER_THAN_OR_EQUAL_TO;
//			}
//
//			else if (valueOf.equalsIgnoreCase("="))
//			{
//				return EQUAL_TO;
//			}
//			else
//			{
//				return null;
//			}
//		}
//
//
//		@Override
//		public String toString() {
//			return comparitors;
//		} 
//
//	}

