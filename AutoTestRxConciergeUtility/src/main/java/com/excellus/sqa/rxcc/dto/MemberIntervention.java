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

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;

import com.excellus.sqa.rxcc.Utility;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for Member Intervention
 * 
 * @author Manish Sharma (msharma)
 * @since 03/01/2022
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberIntervention extends CommonItem
{

	@JsonProperty(required=true)
	private String memberId;

	@JsonProperty(required=true)
	private Type type; //enum

	@JsonProperty(required=true)
	private String  ruleId;

	@JsonProperty(required=true)
	private int  simulationRunNumber;

	@JsonProperty(required=true)
	private String  interventionType;

	@JsonProperty(required=true)
	private String  rxccGroupName;

	@JsonProperty(required=true)
	private String  memberName;

	private PdfLogoAddress  memberAddress;  //may use pdfAddress

	@JsonProperty(required=true)
	private String  memberDateBirth;

	@JsonProperty(required=true)
	private String  queueStatusCode;

	@JsonProperty(required=true)
	private String  queueStatusChangeDateTime;

	@JsonProperty(required=true)
	private String  createdDateTime;

	@JsonProperty(required=true)
	private String  ndc;

	@JsonProperty(required=true)
	private String  targetProductName;

	private List <String> prerequisiteProductName;

	private String  ruleName;

	@JsonProperty(required=true)
	private String faxAlternativeText;

	@JsonProperty(required=true)  //need to be removed in the future (Requirement changes)
	private List<String> faxAlternativeProductName;

	@JsonProperty(required=true)
	private boolean providerOverride;

	@JsonProperty(required=true)
	private String  originalNpi;

	@JsonProperty(required=true)
	private String  originalOfficeLocationId;

	@JsonProperty(required=true)
	private boolean  originalOfficeFaxVerified;

	private String  originalProviderName;

	@JsonProperty(required=true)
	private ContactInformation  originalProviderContact;

	private String  overrideNpi;

	private String  overrideOfficeLocationId;

	private boolean  overrideOfficeFaxVerified;

	private String  overrideProviderName;

	private ContactInformation  overrideProviderContact;  

	@JsonProperty(required=true)
	private String  assignedTo;

	private String  interventionStatusNote;

	@JsonProperty(required=true)
	private int  deferredNumberOfDays;

	@JsonProperty(required=true)
	private double  planCost;

	@JsonProperty(required=true)
	private double  memberCost;

	@JsonProperty(required=true)
	private int  daysSupply;

	private String  acceptText;

	private String  declineText;

	private QualityFreeFormTargetPrerequisiteInd  qualityFreeFormTargetPrerequisiteInd; //enum

	private String  qualityFreeFormText;

	private String  qualityInterventionText;

	private String  declineStatusReason;

	@JsonProperty(required=true)
	private String  pdfLogoUri;

	private PdfLogoAddress  pdfLogoAddress;

	@JsonProperty(required=true)
	private TargetInterventionQueueType  targetInterventionQueueType; //enum

	private String  documentId;

	private RuleType  ruleType; //enum



	/*
	 * Validations
	 */

	/**
	 * Compare two objects
	 * 
	 * @param MemberIntervention
	 * @return
	 */

	public List<DynamicNode> compare(MemberIntervention memberIntervention)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("memberId: [" + memberId + "]", () -> assertEquals(memberId, memberIntervention.getMemberId(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("type: [" + type + "]", () -> assertEquals(type, memberIntervention.getType(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("ruleId: [" + ruleId + "]", () -> assertEquals(ruleId, memberIntervention.getRuleId(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("simulationRunNumber: [" + simulationRunNumber + "]", () -> assertEquals(simulationRunNumber, memberIntervention.getSimulationRunNumber(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("interventionType: [" + interventionType + "]", () -> assertEquals(interventionType, memberIntervention.getInterventionType(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("rxccGroupName: [" + rxccGroupName + "]", () -> assertEquals(rxccGroupName, memberIntervention.getRxccGroupName(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("memberName: [" + memberName + "]", () -> assertEquals(memberName, memberIntervention.getMemberName(), getApiInfo(memberIntervention))));

		//performs object null validation
		if (memberIntervention.getMemberAddress() != null)
		{
			tests.add(dynamicContainer("Member Address", memberAddress.compare(memberIntervention.getMemberAddress())));
		}
		else
		{
			tests.add(dynamicTest("memberAddress: [" + memberAddress + "]", () -> assertEquals(memberAddress, memberIntervention.getMemberAddress(), getApiInfo(memberIntervention))));

		}

		tests.add(dynamicTest("memberDateBirth: [" + memberDateBirth + "]", () -> assertEquals(memberDateBirth, memberIntervention.getMemberDateBirth(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("queueStatusCode: [" + queueStatusCode + "]", () -> assertEquals(queueStatusCode, memberIntervention.getQueueStatusCode(), getApiInfo(memberIntervention))));

		// Perform queueStatusChangeDateTime date validation			
		if ( StringUtils.isNotBlank(queueStatusChangeDateTime) && StringUtils.isNotBlank(memberIntervention.getQueueStatusChangeDateTime()) )
		{
			tests.add(compareDates("queueStatusChangeDateTime: [" + queueStatusChangeDateTime + "]", queueStatusChangeDateTime, memberIntervention.getQueueStatusChangeDateTime(),
					new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, getApiInfo(memberIntervention)));
		}
		else
		{
			tests.add(dynamicTest("queueStatusChangeDateTime: [" + queueStatusChangeDateTime + "]", () -> assertEquals(queueStatusChangeDateTime, memberIntervention.getQueueStatusChangeDateTime(), getApiInfo(memberIntervention))));
		}

		// Perform createdDateTime date validation			
		if ( StringUtils.isNotBlank(createdDateTime) && StringUtils.isNotBlank(memberIntervention.getCreatedDateTime()) )
		{
			tests.add(compareDates("createdDateTime: [" + createdDateTime + "]", createdDateTime, memberIntervention.getCreatedDateTime(),
					new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, getApiInfo(memberIntervention)));
		}
		else
		{
			tests.add(dynamicTest("createdDateTime: [" + createdDateTime + "]", () -> assertEquals(createdDateTime, memberIntervention.getCreatedDateTime(), getApiInfo(memberIntervention))));
		}

		tests.add(dynamicTest("ndc: [" + ndc + "]", () -> assertEquals(ndc, memberIntervention.getNdc(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("targetProductName: [" + targetProductName + "]", () -> assertEquals(targetProductName, memberIntervention.getTargetProductName(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("prerequisiteProductName: [" + prerequisiteProductName + "]", () -> assertEquals(prerequisiteProductName, memberIntervention.getPrerequisiteProductName(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("ruleName: [" + ruleName + "]", () -> assertEquals(ruleName, memberIntervention.getRuleName(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("faxAlternativeText: [" + faxAlternativeText + "]", () -> assertEquals(faxAlternativeText, memberIntervention.getFaxAlternativeText(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("faxAlternativeProductName: [" + faxAlternativeProductName + "]", () -> assertEquals(faxAlternativeProductName, memberIntervention.getFaxAlternativeProductName(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("providerOverride: [" + providerOverride + "]", () -> assertEquals(providerOverride, memberIntervention.isProviderOverride(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("originalNpi: [" + originalNpi + "]", () -> assertEquals(originalNpi, memberIntervention.getOriginalNpi(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("originalOfficeLocationId: [" + originalOfficeLocationId + "]", () -> assertEquals(originalOfficeLocationId, memberIntervention.getOriginalOfficeLocationId(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("originalOfficeFaxVerified: [" + originalOfficeFaxVerified + "]", () -> assertEquals(originalOfficeFaxVerified, memberIntervention.isOriginalOfficeFaxVerified(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("originalProviderName: [" + originalProviderName + "]", () -> assertEquals(originalProviderName, memberIntervention.getOriginalProviderName(), getApiInfo(memberIntervention))));
		tests.add(dynamicContainer("Original Provider Contact", originalProviderContact.compare(memberIntervention.getOriginalProviderContact())));

		//tests.add(dynamicTest("originalProviderContact: [" + originalProviderContact + "]", () -> assertEquals(originalProviderContact, memberIntervention.getOriginalProviderContact(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("overrideNpi: [" + overrideNpi + "]", () -> assertEquals(overrideNpi, memberIntervention.getOverrideNpi(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("overrideOfficeLocationId: [" + overrideOfficeLocationId + "]", () -> assertEquals(overrideOfficeLocationId, memberIntervention.getOverrideOfficeLocationId(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("overrideOfficeFaxVerified: [" + overrideOfficeFaxVerified + "]", () -> assertEquals(overrideOfficeFaxVerified, memberIntervention.isOverrideOfficeFaxVerified(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("overrideProviderName: [" + overrideProviderName + "]", () -> assertEquals(overrideProviderName, memberIntervention.getOverrideProviderName(), getApiInfo(memberIntervention))));

		//performs object null validation
		if (memberIntervention.getOverrideProviderContact() != null)
		{
			tests.add(dynamicContainer("Override Provider Contact", overrideProviderContact.compare(memberIntervention.getOverrideProviderContact())));
		}
		else
		{
			tests.add(dynamicTest("overrideProviderContact: [" + overrideProviderContact + "]", () -> assertEquals(overrideProviderContact, memberIntervention.getOverrideProviderContact(), getApiInfo(memberIntervention))));

		}

		tests.add(dynamicTest("assignedTo: [" + assignedTo + "]", () -> assertEquals(assignedTo, memberIntervention.getAssignedTo(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("interventionStatusNote: [" + interventionStatusNote + "]", () -> assertEquals(interventionStatusNote, memberIntervention.getInterventionStatusNote(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("deferredNumberOfDays: [" + deferredNumberOfDays + "]", () -> assertEquals(deferredNumberOfDays, memberIntervention.getDeferredNumberOfDays(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("planCost: [" + planCost + "]", () -> assertEquals(planCost, memberIntervention.getPlanCost(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("memberCost: [" + memberCost + "]", () -> assertEquals(memberCost, memberIntervention.getMemberCost(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("daysSupply: [" + daysSupply + "]", () -> assertEquals(daysSupply, memberIntervention.getDaysSupply(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("acceptText: [" + acceptText + "]", () -> assertEquals(acceptText, memberIntervention.getAcceptText(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("declineText: [" + declineText + "]", () -> assertEquals(declineText, memberIntervention.getDeclineText(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("qualityFreeFormTargetPrerequisiteInd: [" + qualityFreeFormTargetPrerequisiteInd + "]", () -> assertEquals(qualityFreeFormTargetPrerequisiteInd, memberIntervention.getQualityFreeFormTargetPrerequisiteInd(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("qualityFreeFormText: [" + qualityFreeFormText + "]", () -> assertEquals(qualityFreeFormText, memberIntervention.getQualityFreeFormText(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("qualityInterventionText: [" + qualityInterventionText + "]", () -> assertEquals(qualityInterventionText, memberIntervention.getQualityInterventionText(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("declineStatusReason: [" + declineStatusReason + "]", () -> assertEquals(declineStatusReason, memberIntervention.getDeclineStatusReason(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("pdfLogoUri: [" + pdfLogoUri + "]", () -> assertEquals(pdfLogoUri, memberIntervention.getPdfLogoUri(), getApiInfo(memberIntervention))));

		//performs object null validation
		if (memberIntervention.getPdfLogoAddress() != null)
		{
			tests.add(dynamicContainer("Pdf Logo Address", pdfLogoAddress.compare(memberIntervention.getPdfLogoAddress())));
		}
		else
		{
			tests.add(dynamicTest("pdfLogoAddress: [" + pdfLogoAddress + "]", () -> assertEquals(pdfLogoAddress, memberIntervention.getPdfLogoAddress(), getApiInfo(memberIntervention))));

		}

		tests.add(dynamicTest("targetInterventionQueueType: [" + targetInterventionQueueType + "]", () -> assertEquals(targetInterventionQueueType, memberIntervention.getTargetInterventionQueueType(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("documentId: [" + documentId + "]", () -> assertEquals(documentId, memberIntervention.getDocumentId(), getApiInfo(memberIntervention))));
		tests.add(dynamicTest("ruleType: [" + ruleType + "]", () -> assertEquals(ruleType, memberIntervention.getRuleType(), getApiInfo(memberIntervention))));

		tests.addAll( super.compare(memberIntervention) );

		return tests;
	}


	/*
	 * Helper methods
	 */

	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof MemberIntervention)
		{
			MemberIntervention memberIntervention = (MemberIntervention) obj;

			if ( id.equals(memberIntervention.getId()) )
				return true;
		}

		return false;
	}

	/**
	 * This method evaluate if the provided correspondence matches these fields
	 * - type
	 * - outcome
	 * - contact name
	 * - created by
	 * 
	 * This is meant to be used for UI validation
	 * 
	 * @param theCorrespondence {@link MemberCorrespondence}   
	 * @return true if the fields matches otherwise false
	 */
	public boolean equalsByNonId(MemberIntervention memberIntervention)
	{
		if (  (StringUtils.equalsIgnoreCase(targetProductName, memberIntervention.getTargetProductName())) &&
				//				StringUtils.equalsIgnoreCase(memberCost, memberIntervention.getMemberCost()) &&  //
				StringUtils.equalsIgnoreCase(overrideProviderName, memberIntervention.getOverrideProviderName())) {
			return true;
		}

		return false;
	}

	/**
	 * Compare UI two objects
	 * 
	 * @param MemberCorrespondence
	 * @return
	 * @author neerutagore
	 */
	public List<DynamicNode> compareMemberInterventionsUI(MemberIntervention memberIntervention)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		// cosmos data inconsistencey with space trimming and making the upper cases
		tests.add(dynamicTest("targetProductName: [" + targetProductName + "]", () -> assertEquals(targetProductName.trim().toUpperCase(), memberIntervention.getTargetProductName().toUpperCase())));  


		tests.add(dynamicTest("overrideProviderName: [" + overrideProviderName + "]", () -> assertEquals(overrideProviderName.replace("  ", " "), memberIntervention.getOverrideProviderName())));

		return tests;
	}
	/**
	 * This method evaluate if the intervention matches these fields for Fax Testing
	 * This is meant to be used for UI validation
	 * @return true if the fields matches otherwise false
	 * @ntagore 01/23/23 to validte Fax Successful status
	 */
	public boolean equalsByNonIds(MemberIntervention memberIntervention)
	{
		if (  (StringUtils.equalsIgnoreCase(targetProductName, memberIntervention.getTargetProductName())) &&
				StringUtils.equalsIgnoreCase(this.getProviderName(), memberIntervention.getProviderName())) {
			return true;
		}

		return false;
	}

	public String getProviderName()
	{
		if ( !providerOverride && StringUtils.isNotBlank(this.overrideProviderName) )
		{
			return this.overrideProviderName;
		}

		return this.originalProviderName;
	}

	/**
	 * Compare UI two objects
	 * 
	 * @param MemberIntervention
	 * @return
	 * @author neerutagore 01/23/23
	 */
	public List<DynamicNode> compareInterventionsFaxUI(MemberIntervention memberIntervention)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("targetProductName: [" + targetProductName + "]", () -> assertEquals(targetProductName.trim().toUpperCase(), memberIntervention.getTargetProductName().toUpperCase())));

		tests.add(dynamicTest("providerName: [" + this.getProviderName() + "]", 
				() -> assertEquals(StringUtils.normalizeSpace(this.getProviderName()), StringUtils.normalizeSpace(memberIntervention.getProviderName()))));

		tests.add(dynamicTest("createdDateTime: [" + createdDateTime + "]", 
				() -> assertEquals(Utility.convertCosmosDateToUI(createdDateTime,  "MM/dd/yyyy"),memberIntervention.getCreatedDateTime()))); 

		//Perform custom validation of Status Change Date 02/23/23(ntagore)
		if ( StringUtils.isNotBlank(queueStatusChangeDateTime) && StringUtils.isNotBlank(memberIntervention.getQueueStatusChangeDateTime()) )
		{

			String dateYear = queueStatusChangeDateTime.substring(0, 4);
			String dateMonth = queueStatusChangeDateTime.substring(5, 7);
			String dateDay = queueStatusChangeDateTime.substring(8, 10);

			String expectedDate = dateMonth+"/"+dateDay+"/"+dateYear;

			tests.add(dynamicTest("Status Change Date: [" + queueStatusChangeDateTime + "]",
					() -> assertEquals(expectedDate, memberIntervention.getQueueStatusChangeDateTime()) ));
		}
		else
		{
			tests.add(dynamicTest("Status Change Date: [" + queueStatusChangeDateTime + "]", () -> assertEquals(queueStatusChangeDateTime, memberIntervention.getQueueStatusChangeDateTime())));
		}    

		tests.add(dynamicTest("queueStatusCode: [" + queueStatusCode + "]", () -> assertEquals(queueStatusCode, memberIntervention.getQueueStatusCode())));

		tests.add(dynamicTest("planCost: [" + planCost + "]", () -> assertEquals(planCost, memberIntervention.getPlanCost())));

		tests.add(dynamicTest("memberCost: [" + memberCost + "]", () -> assertEquals(memberCost, memberIntervention.getMemberCost())));

		return tests;
	}
	/*
	 * Setter / Getter
	 */
	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public int getSimulationRunNumber() {
		return simulationRunNumber;
	}

	public void setSimulationRunNumber(int simulationRunNumber) {
		this.simulationRunNumber = simulationRunNumber;
	}

	public String getInterventionType() {
		return interventionType;
	}

	public void setInterventionType(String interventionType) {
		this.interventionType = interventionType;
	}

	public String getRxccGroupName() {
		return rxccGroupName;
	}

	public void setRxccGroupName(String rxccGroupName) {
		this.rxccGroupName = rxccGroupName;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public PdfLogoAddress getMemberAddress() {   //Used same class for memberAddress & PdfLogoAddress
		return memberAddress;
	}

	public void setMemberAddress(PdfLogoAddress memberAddress) {
		this.memberAddress = memberAddress;
	}

	public String getMemberDateBirth() {
		return memberDateBirth;
	}

	public void setMemberDateBirth(String memberDateBirth) {
		this.memberDateBirth = memberDateBirth;
	}

	public String getQueueStatusCode() {
		return queueStatusCode;
	}

	public void setQueueStatusCode(String queueStatusCode) {
		this.queueStatusCode = queueStatusCode;
	}

	public String getQueueStatusChangeDateTime() {
		return queueStatusChangeDateTime;
	}

	public void setQueueStatusChangeDateTime(String queueStatusChangeDateTime) {
		this.queueStatusChangeDateTime = queueStatusChangeDateTime;
	}

	public String getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(String createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public String getNdc() {
		return ndc;
	}

	public void setNdc(String ndc) {
		this.ndc = ndc;
	}

	public String getTargetProductName() {
		return targetProductName;
	}

	public void setTargetProductName(String targetProductName) {
		this.targetProductName = targetProductName;
	}

	public List<String> getPrerequisiteProductName() {
		return prerequisiteProductName;
	}

	public void setPrerequisiteProductName(List<String> prerequisiteProductName) {
		this.prerequisiteProductName = prerequisiteProductName;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getFaxAlternativeText() {
		return faxAlternativeText;
	}

	public void setFaxAlternativeText(String faxAlternativeText) {
		this.faxAlternativeText = faxAlternativeText;
	}

	public boolean isProviderOverride() {
		return providerOverride;
	}

	public void setProviderOverride(boolean providerOverride) {
		this.providerOverride = providerOverride;
	}

	public String getOriginalNpi() {
		return originalNpi;
	}

	public void setOriginalNpi(String originalNpi) {
		this.originalNpi = originalNpi;
	}

	public String getOriginalOfficeLocationId() {
		return originalOfficeLocationId;
	}

	public void setOriginalOfficeLocationId(String originalOfficeLocationId) {
		this.originalOfficeLocationId = originalOfficeLocationId;
	}

	public boolean isOriginalOfficeFaxVerified() {
		return originalOfficeFaxVerified;
	}

	public void setOriginalOfficeFaxVerified(boolean originalOfficeFaxVerified) {
		this.originalOfficeFaxVerified = originalOfficeFaxVerified;
	}

	public String getOriginalProviderName() {
		return originalProviderName;
	}

	public void setOriginalProviderName(String originalProviderName) {
		this.originalProviderName = originalProviderName;
	}

	public ContactInformation getOriginalProviderContact() {
		return originalProviderContact;
	}

	public void setOriginalProviderContact(ContactInformation originalProviderContact) {
		this.originalProviderContact = originalProviderContact;
	}

	//	public void setOriginalProviderContact(String address1, String city, String state, String postalCode,
	//			String phoneNumber, String faxNumber) {
	//		// TODO Auto-generated method stub
	//		
	//		this.originalProviderContact = new ContactInformation(address1, city);
	//	}

	public String getOverrideNpi() {
		return overrideNpi;
	}

	public void setOverrideNpi(String overrideNpi) {
		this.overrideNpi = overrideNpi;
	}

	public String getOverrideOfficeLocationId() {
		return overrideOfficeLocationId;
	}

	public void setOverrideOfficeLocationId(String overrideOfficeLocationId) {
		this.overrideOfficeLocationId = overrideOfficeLocationId;
	}

	public boolean isOverrideOfficeFaxVerified() {
		return overrideOfficeFaxVerified;
	}

	public void setOverrideOfficeFaxVerified(boolean overrideOfficeFaxVerified) {
		this.overrideOfficeFaxVerified = overrideOfficeFaxVerified;
	}

	public String getOverrideProviderName() {
		return overrideProviderName;
	}

	public void setOverrideProviderName(String overrideProviderName) {
		this.overrideProviderName = overrideProviderName;
	}

	public ContactInformation getOverrideProviderContact() {
		return overrideProviderContact;
	}

	public void setOverrideProviderContact(ContactInformation overrideProviderContact) {
		this.overrideProviderContact = overrideProviderContact;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getInterventionStatusNote() {
		return interventionStatusNote;
	}

	public void setInterventionStatusNote(String interventionStatusNote) {
		this.interventionStatusNote = interventionStatusNote;
	}

	public int getDeferredNumberOfDays() {
		return deferredNumberOfDays;
	}

	public void setDeferredNumberOfDays(int deferredNumberOfDays) {
		this.deferredNumberOfDays = deferredNumberOfDays;
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

	public int getDaysSupply() {
		return daysSupply;
	}

	public void setDaysSupply(int daysSupply) {
		this.daysSupply = daysSupply;
	}

	public String getAcceptText() {
		return acceptText;
	}

	public void setAcceptText(String accepteText) {
		this.acceptText = accepteText;
	}

	public String getDeclineText() {
		return declineText;
	}

	public void setDeclineText(String declineText) {
		this.declineText = declineText;
	}

	public QualityFreeFormTargetPrerequisiteInd getQualityFreeFormTargetPrerequisiteInd() {
		return qualityFreeFormTargetPrerequisiteInd;
	}

	public void setQualityFreeFormTargetPrerequisiteInd(
			QualityFreeFormTargetPrerequisiteInd qualityFreeFormTargetPrerequisiteInd) {
		this.qualityFreeFormTargetPrerequisiteInd = qualityFreeFormTargetPrerequisiteInd;
	}

	public List<String> getFaxAlternativeProductName() //need to be removed in the future (Requirement changes)
	{
		return faxAlternativeProductName;
	}

	public void setFaxAlternativeProductName(List<String> faxAlternativeProductName) {
		this.faxAlternativeProductName = faxAlternativeProductName;
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

	public String getDeclineStatusReason() {
		return declineStatusReason;
	}

	public void setDeclineStatusReason(String declineStatusReason) {
		this.declineStatusReason = declineStatusReason;
	}

	public String getPdfLogoUri() {
		return pdfLogoUri;
	}

	public void setPdfLogoUri(String pdfLogoUri) {
		this.pdfLogoUri = pdfLogoUri;
	}

	public PdfLogoAddress getPdfLogoAddress() {
		return pdfLogoAddress;
	}

	public void setPdfLogoAddress(PdfLogoAddress pdfLogoAddress) {
		this.pdfLogoAddress = pdfLogoAddress;
	}

	public TargetInterventionQueueType getTargetInterventionQueueType() {
		return targetInterventionQueueType;
	}

	public void setTargetInterventionQueueType(TargetInterventionQueueType targetInterventionQueueType) {
		this.targetInterventionQueueType = targetInterventionQueueType;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public RuleType getRuleType() {
		return ruleType;
	}

	public void setRuleType(RuleType ruleType) {
		this.ruleType = ruleType;
	}

	//Enum Type
	public enum Type {
		intervention,
		simulation;
	}

	//Enum TargetInterventionQueueType
	public enum TargetInterventionQueueType
	{
		RPH  (0, "RPH"),
		OC   (1, "OC");

		private int targetInterventionQueueTypeIndex;
		private String item;

		private TargetInterventionQueueType(int targetInterventionQueueType, String item)
		{
			this.targetInterventionQueueTypeIndex = targetInterventionQueueType; 
			this.item = item;
		}

		public int getTargetInterventionQueueTypeIndex()
		{
			return this.targetInterventionQueueTypeIndex;
		}

		public  String getEnumValue()
		{
			return item;
		}
	}

	//Enum QualityFreeformTargetPrerequisiteInd
	public enum QualityFreeFormTargetPrerequisiteInd 
	{
		NONE         (0, "None"),
		TARGET       (1, "Target"),
		FREE_FORM    (2, "Free form");

		private int qualityFreeFormTargetPrerequisiteIndIndex;
		private String item;

		private QualityFreeFormTargetPrerequisiteInd(int qualityFreeFormTargetPrerequisiteInd, String item)
		{
			this.qualityFreeFormTargetPrerequisiteIndIndex = qualityFreeFormTargetPrerequisiteInd; 
			this.item = item;
		}
		public int getQualityFreeFormTargetPrerequisiteIndIndex()
		{
			return this.qualityFreeFormTargetPrerequisiteIndIndex;
		}

		public  String getQualityFreeFormTargetPrerequisiteIndValue()
		{
			return item;
		}

		@Override
		public String toString() {
			return item;
		}

	}

	//Enum RuleType
	public enum RuleType
	{
		batch  	   (0, "batch"),
		standard   (1, "standard");

		private int ruleTypeIndex;
		private String item;

		private RuleType(int ruleType, String item)
		{
			this.ruleTypeIndex = ruleType; 
			this.item = item;
		}

		public int getRuleTypeIndex()
		{
			return this.ruleTypeIndex;
		}

		public  String getEnumValue()
		{
			return item;
		}
	}

	// member intervention data overriden by  provider details  record fetch for validation as in api response are pulled from the provider for GetInterventions API
	//provider document referenced by 'originalNpi' {'originalProviderContact', 'originalProviderName', 'originalOfficeFaxVerified'} 

	public void overrideOriginalProviderContact(Provider actual)
	{
		setOverrideOriginalProviderContact(actual);
	}

	public void setOverrideOriginalProviderContact(Provider originalProvider)
	{
		this.originalProviderContact = new ContactInformation(originalProvider.getOfficeLocations().get(0).getAddress1(), originalProvider.getOfficeLocations().get(0).getCity(),
				originalProvider.getOfficeLocations().get(0).getState(),originalProvider.getOfficeLocations().get(0).getPostalCode(),originalProvider.getOfficeLocations().get(0).getPhoneNumber(),
				originalProvider.getOfficeLocations().get(0).getFaxNumber());
	}

	public void overrideOriginalOfficeFaxVerified(Provider actual)
	{
		setOverrideOriginalOfficeFaxVerified(actual);
	}

	public void setOverrideOriginalOfficeFaxVerified(Provider originalProvider)
	{
		this.originalOfficeFaxVerified = originalProvider.getOfficeLocations().get(0).isFaxVerified();
	}

	public void overrideOriginalProviderName(Provider actual)
	{
		setOverrideOriginalProviderName(actual);
	}

	public void setOverrideOriginalProviderName(Provider originalProvider) 
	{
		if(originalProvider.getCredential() != null)
		{
			this.originalProviderName =	originalProvider.getFirstName()+" "+originalProvider.getLastName()+", "+originalProvider.getCredential().toUpperCase();	
		}
		else 
		{
			this.originalProviderName =	originalProvider.getFirstName()+" "+originalProvider.getLastName();
		}
	}

	// member intervention data overriden by  provider details  record fetch for validation as in api response are pulled from the provider for GetInterventions API
	// provider document referenced by 'overrideNpi' {'overrideProviderName', 'overrideOfficeFaxVerified', 'overrideProviderContact'}

	public void overrideOverrideProviderContact(Provider actual)
	{
		setOverridOverrideProviderContact(actual);
	}

	public void setOverridOverrideProviderContact(Provider originalProvider) 
	{
		this.overrideProviderContact = new ContactInformation(originalProvider.getOfficeLocations().get(0).getAddress1(), originalProvider.getOfficeLocations().get(0).getCity(),
				originalProvider.getOfficeLocations().get(0).getState(),originalProvider.getOfficeLocations().get(0).getPostalCode(),originalProvider.getOfficeLocations().get(0).getPhoneNumber(),
				originalProvider.getOfficeLocations().get(0).getFaxNumber());
	}

	public void overrideOverrideOfficeFaxVerified(Provider actual)
	{
		setOverrideOverrideOfficeFaxVerified(actual);
	}

	public void setOverrideOverrideOfficeFaxVerified(Provider originalProvider) 
	{
		this.overrideOfficeFaxVerified = originalProvider.getOfficeLocations().get(0).isFaxVerified();
	}

	public void overrideOverrideProviderName(Provider actual)
	{
		setOverrideOverrideProviderName(actual);
	}

	public void setOverrideOverrideProviderName(Provider originalProvider) 
	{
		if(originalProvider.getCredential() != null)
		{
			this.overrideProviderName =	originalProvider.getFirstName()+" "+originalProvider.getLastName()+", "+originalProvider.getCredential().toUpperCase();	
		}
		else 
		{
			this.overrideProviderName =	originalProvider.getFirstName()+" "+originalProvider.getLastName();
		}
	}

}
