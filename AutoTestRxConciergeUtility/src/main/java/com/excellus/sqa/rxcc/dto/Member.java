/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * DTO for member
 * 
 * GC (04/05/22) updated to reflect json schema <a href="https://dev.azure.com/ExcellusBCBS/EHP/_git/rxcc-schemas?path=/schemas/member.member.schema.json">member.member.schema.json</a>
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/20/2022
 */
@JsonPropertyOrder({ "memberId", "type", 
	"tenantId", "tenantName", // GC (03/22/22) these aren't in schema but we have bad data and it needs to be dealt with
	"carrierId", "carrierName", // optional
	"upi", "groupId", "groupName", "subscriberId", "dependentCode", "firstName", 
	"middleInitial",  // optional
	"lastName", "gender", "dateBirth", "memberRelationshipCode", "memberRelationship", "address1", 
	"address2", "address3",  // optional
	"city", "state", "postalCode", "country", 
	"county",  // optional
	"phoneNumbers",
	"email", "rxBenefitId", "rxBenefitDescr", "effDate", "termDate", "fundingArrangementId", "fundingArrangementDescr",  // optional
	"vendorFormularyCode", "vendorFormularyDescr",
	"benefitHierarchyId", "benefitHierarchyDescr", "reportGroup1", "reportGroup2", "reportGroup3",  // optional
	"deceased", "optinComm", "correspondenceType","edwPersonUniqueId", "batchNumber"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Member extends Item
{
	private static final Logger logger = LoggerFactory.getLogger(Member.class);

	/*
	 * Cosmos JSON schema
	 */

	@JsonProperty(required=true)
	private String memberId;
	
	@JsonProperty(required=true) 
	private String type;
	
	private String carrierId;
	
	private String carrierName;
	
	@JsonProperty(required=true) 
	private String upi;
	
	@JsonProperty(required=true) 
	private String groupId;
	
	@JsonProperty(required=true) 
	private String groupName;
	
	@JsonProperty(required=true) 
	private String subscriberId;
	
	@JsonProperty(required=true) 
	private String dependentCode;
	
	@JsonProperty(required=true) 
	private String firstName;
	
	private String middleInitial;
	
	@JsonProperty(required=true) 
	private String lastName;
	
	@JsonProperty(required=true) 
	private String gender;
	
	@JsonProperty(required=true) 
	private String dateBirth;
	
	@JsonProperty(required=true)
	private String memberRelationshipCode;	// GC (03/21/22) newly added
	
	@JsonProperty(required=true)
	private String memberRelationship;		// GC (03/21/22) newly added
	
	@JsonProperty(required=true)
	private String address1;
	
	private String address2;
	
	private String address3;
	
	@JsonProperty(required=true)
	private String city;
	
	@JsonProperty(required=true)
	private String state;
	
	@JsonProperty(required=true)
	private String postalCode;
	
	@JsonProperty(required=true)
	private String country;
	
	private String county;		// GC (03/21/22) newly added
    private String countyName;	// GC (03/21/22) removed -- MS added back on 6/10

	@JsonProperty(required=true)
	private List<PhoneNumber> phoneNumbers;
	
	private String email;
	
	private String rxBenefitId;
	
	private String rxBenefitDescr;
	
	private String effDate;
	
	private String termDate;
	
	private String fundingArrangementId;
	
	private String fundingArrangementDescr;
	
	@JsonProperty(required=true)
	private String vendorFormularyCode;
	
	@JsonProperty(required=true)
	private String vendorFormularyDescr;
	
	private String benefitHierarchyId;
	
	private String benefitHierarchyDescr;
	
	private String reportGroup1;
	
	private String reportGroup2;
	
	private String reportGroup3;
	
	@JsonProperty(required=true)
	private Boolean deceased;
	
	@JsonProperty(required=true)
	private Boolean optinComm;
	
	@JsonProperty(required=true)
	private String correspondenceType;
	
	private String edwPersonUniqueId; //Added 3/20/2023

	@JsonProperty(required=true)
	private int batchNumber; 

	/*
	 * Not part of the member schema (bad data?)
	 */
	private String tenantName;
	private int tenantId;
	
	/*
	 * To be used by web UI
	 */
	@JsonIgnore 
	private String firstLastName;
	

	/*
	 * Validations
	 */
	
	/**
	 * Compare the UI Member Demographics data
	 * 
	 * @param anotherMember {@link Member}
	 * @return test results of comparison
	 */
	public List<DynamicNode> compareMemberDemographics(Member anotherMember)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		tests.add(dynamicTest("Name: [" + getFirstLastName() + "]", () -> assertEquals(getFirstLastName(), anotherMember.getFirstLastName())));
		tests.add(dynamicTest("Subscriber id: [" + subscriberId + "]", () -> assertEquals(subscriberId, anotherMember.getSubscriberId())));
		tests.add(dynamicTest("Dependent code: [" + dependentCode + "]", () -> assertEquals(dependentCode, anotherMember.getDependentCode())));
		tests.add(dynamicTest("Unique ID: [" + upi + "]", () -> assertEquals(upi, anotherMember.getUpi())));
		tests.add(dynamicTest("DOB: [" + dateBirth + "]", () -> assertEquals(dateBirth, anotherMember.getDateBirth())));
		tests.add(dynamicTest("Gender: [" + gender + "]", () -> assertEquals(gender, anotherMember.getGender())));
		tests.add(dynamicTest("Address: [" + address1 + "]", () -> assertEquals(address1, anotherMember.getAddress1())));
		tests.add(dynamicTest("Address: [" + address2 + "]", () -> assertEquals(address2, anotherMember.getAddress2())));
		tests.add(dynamicTest("Address: [" + address3 + "]", () -> assertEquals(address3, anotherMember.getAddress3())));
		tests.add(dynamicTest("City: [" + city + "]", () -> assertEquals(city, anotherMember.getCity())));
		tests.add(dynamicTest("State: [" + state + "]", () -> assertEquals(state, anotherMember.getState())));
		tests.add(dynamicTest("Postal Code: [" + postalCode + "]", () -> assertEquals(postalCode, anotherMember.getPostalCode())));
		
		// Per requirement, ONY 'home' and 'cell' type are displayed in the UI. Thus, ignore the rest
		tests.add(dynamicContainer("Phone numbers", comparePhones(anotherMember.getPhoneNumbers(), "home", "cell")));

		tests.add(dynamicTest("Email: [" + email + "]", () -> assertEquals(email, anotherMember.getEmail())));
		tests.add(dynamicTest("Opted out of Communication: [" + optinComm + "]", () -> assertEquals(optinComm, anotherMember.getOptinComm())));
		tests.add(dynamicTest("Correspondence Type: [" + correspondenceType + "]", () -> assertEquals(correspondenceType, anotherMember.getCorrespondenceType())));
			  
		return tests;
	}
	
	/**
	 * Compare the UI member plan information
	 * 
	 * @param anotherMember {@link Member}
	 * @return test results of comparison
	 */
	public List<DynamicNode> comparePlanInfo(Member anotherMember)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		tests.add(dynamicTest("RX Product Description: [" + rxBenefitDescr + "]", () -> assertEquals(getFirstLastName(), anotherMember.getFirstLastName())));
		tests.add(dynamicTest("Fomulary: [" + vendorFormularyCode + "]", () -> assertEquals(vendorFormularyCode, anotherMember.getVendorFormularyCode())));
		tests.add(dynamicTest("Fomulary: [" + vendorFormularyDescr + "]", () -> assertEquals(vendorFormularyDescr, anotherMember.getVendorFormularyDescr())));
		//tests.add(dynamicTest("Carrier ID Description: [" + vendorFormularyDescr + "]", () -> assertEquals(dependentCode, anotherMember.getDependentCode())));
		return tests;
	}
	
	/**
	 * Compare the UI member group information
	 * 
	 * @param anotherMember {@link Member}
	 * @return test results of comparison
	 */
	public List<DynamicNode> compareGroupInfo(Member anotherMember)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		tests.add(dynamicTest("RxCC Group Name: [" + groupName + "]", () -> assertEquals(groupName, anotherMember.getGroupName())));
		tests.add(dynamicTest("RxCC Group ID: [" + groupId + "]", () -> assertEquals(groupId, anotherMember.getGroupId())));
		tests.add(dynamicTest("Reporting Group 1: [" + reportGroup1 + "]", () -> assertEquals(reportGroup1, anotherMember.getReportGroup1())));
		tests.add(dynamicTest("Reporting Group 2: [" + reportGroup2 + "]", () -> assertEquals(reportGroup2, anotherMember.getReportGroup2())));
		tests.add(dynamicTest("Reporting Group 3: [" + reportGroup3 + "]", () -> assertEquals(reportGroup3, anotherMember.getReportGroup3())));
		tests.add(dynamicTest("Funding Arrangement ID: [" + fundingArrangementId + "]", () -> assertEquals(fundingArrangementId, anotherMember.getFundingArrangementId())));
		tests.add(dynamicTest("Funding Arrangement Desc: [" + fundingArrangementDescr + "]", () -> assertEquals(fundingArrangementDescr, anotherMember.getFundingArrangementDescr())));
		return tests;
	}
	
	/**
	 * Compare member properties defined in the schema
	 * 
	 * @param anotherMember {@link Member} to compare with
	 * @return test results of comparison
	 */
	public List<DynamicNode> compare(Member anotherMember)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		tests.add(dynamicTest("memberId [" + memberId + "]", () -> assertEquals(memberId, anotherMember.getMemberId(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("carrierId [" + carrierId + "]", () -> assertEquals(carrierId, anotherMember.getCarrierId(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("carrierName [" + carrierName + "]", () -> assertEquals(carrierName, anotherMember.getCarrierName(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("upi [" + upi + "]", () -> assertEquals(upi, anotherMember.getUpi(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("groupId [" + groupId + "]", () -> assertEquals(groupId, anotherMember.getGroupId(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("groupName [" + groupName + "]", () -> assertEquals(groupName, anotherMember.getGroupName(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("subscriberId [" + subscriberId + "]", () -> assertEquals(subscriberId, anotherMember.getSubscriberId(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("dependentCode [" + dependentCode + "]", () -> assertEquals(dependentCode, anotherMember.getDependentCode(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("firstName [" + firstName + "]", () -> assertEquals(firstName, anotherMember.getFirstName(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("middleInitial [" + middleInitial + "]", () -> assertEquals(middleInitial, anotherMember.getMiddleInitial(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("lastName [" + lastName + "]", () -> assertEquals(lastName, anotherMember.getLastName(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("gender [" + gender + "]", () -> assertEquals(gender, anotherMember.getGender(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("dateBirth [" + dateBirth + "]", () -> assertEquals(dateBirth, anotherMember.getDateBirth())));
		tests.add(dynamicTest("memberRelationshipCode [" + memberRelationshipCode + "]", () -> assertEquals(memberRelationshipCode, anotherMember.getMemberRelationshipCode(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("memberRelationship [" + memberRelationship + "]", () -> assertEquals(memberRelationship, anotherMember.getMemberRelationship(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("address1 [" + address1 + "]", () -> assertEquals(address1, anotherMember.getAddress1(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("address2 [" + address2 + "]", () -> assertEquals(address2, anotherMember.getAddress2(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("address3 [" + address3 + "]", () -> assertEquals(address3, anotherMember.getAddress3(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("city [" + city + "]", () -> assertEquals(city, anotherMember.getCity(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("state [" + state + "]", () -> assertEquals(state, anotherMember.getState(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("postalCode [" + postalCode + "]", () -> assertEquals(postalCode, anotherMember.getPostalCode(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("country [" + country + "]", () -> assertEquals(country, anotherMember.getCountry(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("county [" + county + "]", () -> assertEquals(county, anotherMember.getCounty(), getApiInfo(anotherMember))));
		tests.add(dynamicContainer("Phone numbers", comparePhones(anotherMember.getPhoneNumbers(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("email [" + email + "]", () -> assertEquals(email, anotherMember.getEmail(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("rxBenefitId [" + rxBenefitId + "]", () -> assertEquals(rxBenefitId, anotherMember.getRxBenefitId(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("rxBenefitDescr [" + rxBenefitDescr + "]", () -> assertEquals(rxBenefitDescr, anotherMember.getRxBenefitDescr(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("effDate  [" + effDate  + "]", () -> assertEquals(effDate , anotherMember.getEffDate(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("termDate [" + termDate + "]", () -> assertEquals(termDate, anotherMember.getTermDate(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("fundingArrangementId [" + fundingArrangementId + "]", () -> assertEquals(fundingArrangementId, anotherMember.getFundingArrangementId(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("fundingArrangementDescr [" + fundingArrangementDescr + "]", () -> assertEquals(fundingArrangementDescr, anotherMember.getFundingArrangementDescr(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("vendorFormularyCode [" + vendorFormularyCode + "]", () -> assertEquals(vendorFormularyCode, anotherMember.getVendorFormularyCode(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("vendorFormularyDescr [" + vendorFormularyDescr + "]", () -> assertEquals(vendorFormularyDescr, anotherMember.getVendorFormularyDescr(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("benefitHierarchyId [" + benefitHierarchyId + "]", () -> assertEquals(benefitHierarchyId, anotherMember.getBenefitHierarchyId(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("benefitHierarchyDescr [" + benefitHierarchyDescr + "]", () -> assertEquals(benefitHierarchyDescr, anotherMember.getBenefitHierarchyDescr(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("reportGroup1 [" + reportGroup1 + "]", () -> assertEquals(reportGroup1, anotherMember.getReportGroup1(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("reportGroup2 [" + reportGroup2 + "]", () -> assertEquals(reportGroup2, anotherMember.getReportGroup2(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("reportGroup3 [" + reportGroup3 + "]", () -> assertEquals(reportGroup3, anotherMember.getReportGroup3(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("deceased [" + deceased + "]", () -> assertEquals(deceased, anotherMember.getDeceased(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("optinComm [" + optinComm + "]", () -> assertEquals(optinComm, anotherMember.getOptinComm(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("correspondenceType [" + correspondenceType + "]", () -> assertEquals(correspondenceType, anotherMember.getCorrespondenceType(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("edwPersonUniqueId [" + edwPersonUniqueId + "]", () -> assertEquals(edwPersonUniqueId, anotherMember.getEdwPersonUniqueId(), getApiInfo(anotherMember))));
		tests.add(dynamicTest("batchNumber [" + batchNumber + "]", () -> assertEquals(batchNumber, anotherMember.getBatchNumber(), getApiInfo(anotherMember))));
		
		tests.addAll( super.compare(anotherMember) );
		
		return tests;
	}
	
	/**
	 * Validate list of phone numbers
	 * 
	 * @param thePhoneNumbers to be validated
	 * @param validateType (optional) list of phone number type to validate.
	 *        If null then it validates all. 
	 *        Per requirement, ONY 'home' and 'cell' type are displayed in the UI. Thus, ignore the rest
	 * @return
	 */
	public List<DynamicNode> comparePhones(List<PhoneNumber> thePhoneNumbers, String apiInfo, String... validateType)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		for ( PhoneNumber expected : phoneNumbers )
		{
			if ( validateType != null  && validateType.length > 0 &&
					! Arrays.asList(validateType).stream().anyMatch( type -> type.equalsIgnoreCase(expected.getType()) ) )
				continue;
			
			boolean found = false;
			for ( PhoneNumber actual : thePhoneNumbers )
			{
				if ( expected.equals(actual) )
				{
					found = true;
					tests.add(dynamicContainer("Phone number [" + expected.getType() + "]", expected.compare(actual)));
					break;
				}
			}
			
			if ( !found) 
			{
				tests.add(dynamicTest("Phone - " + expected.toString(), () -> fail("Not found\n" + apiInfo)));
			}
		}
		
		return tests;
	}
	
	
	
	/*
	 * Helper methods
	 */

	
	/**
	 * Equals is defined if the following are met
	 * - member id are the same
	 * - subscriber id and (first/lastname or dependent code) are the same
	 *
	 * @param obj to be compared
	 * @return true if it mets the criteria
	 */
	@Override
	public boolean equals(Object obj) {
		
		logger.debug("comparing two object to be the same");

		if ( !(obj instanceof Member) ) {
			return false;
		}

		Member member = (Member) obj;

		if ( StringUtils.equals(memberId, member.getMemberId()) ) {
			return true;
		}
		else if ( StringUtils.equals(subscriberId, member.getSubscriberId()) &&
						  ( StringUtils.equals(getFirstLastName(), member.getFirstLastName()) ||
									StringUtils.equals(dependentCode, member.getDependentCode()) ) ) {
			return true;
		}

		return false;
	}
	

	/*
	 * Setter and getter
	 */

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

	public String getCarrierId() {
		return carrierId;
	}

	public void setCarrierId(String carrierId) {
		this.carrierId = carrierId;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getUpi() {
		return upi;
	}

	public void setUpi(String upi) {
		this.upi = upi;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleInitial() {
		return middleInitial;
	}

	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDateBirth() {
		return dateBirth;
	}

	public void setDateBirth(String dateBirth) {
		this.dateBirth = dateBirth;
	}

	public String getMemberRelationshipCode() {
		return memberRelationshipCode;
	}
	
	public void setMemberRelationshipCode(String memberRelationshipCode) {
		this.memberRelationshipCode = memberRelationshipCode;
	}
	
	public String getMemberRelationship() {
		return memberRelationship;
	}

	public void setMemberRelationship(String memberRelationship) {
		this.memberRelationship = memberRelationship;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getCountyName() {
		return countyName;
	}

	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}

	public List<PhoneNumber> getPhoneNumbers() {
		return phoneNumbers;
	}

	public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRxBenefitId() {
		return rxBenefitId;
	}

	public void setRxBenefitId(String rxBenefitId) {
		this.rxBenefitId = rxBenefitId;
	}

	public String getRxBenefitDescr() {
		return rxBenefitDescr;
	}

	public void setRxBenefitDescr(String rxBenefitDescr) {
		this.rxBenefitDescr = rxBenefitDescr;
	}

	public String getEffDate() {
		return effDate;
	}

	public void setEffDate(String effDate) {
		this.effDate = effDate;
	}

	public String getTermDate() {
		return termDate;
	}

	public void setTermDate(String termDate) {
		this.termDate = termDate;
	}

	public String getFundingArrangementId() {
		return fundingArrangementId;
	}

	public void setFundingArrangementId(String fundingArrangementId) {
		this.fundingArrangementId = fundingArrangementId;
	}

	public String getFundingArrangementDescr() {
		return fundingArrangementDescr;
	}

	public void setFundingArrangementDescr(String fundingArrangementDescr) {
		this.fundingArrangementDescr = fundingArrangementDescr;
	}

	public String getVendorFormularyCode() {
		return vendorFormularyCode;
	}

	public void setVendorFormularyCode(String vendorFormularyCode) {
		this.vendorFormularyCode = vendorFormularyCode;
	}

	public String getVendorFormularyDescr() {
		return vendorFormularyDescr;
	}

	public void setVendorFormularyDescr(String vendorFormularyDescr) {
		this.vendorFormularyDescr = vendorFormularyDescr;
	}

	public String getBenefitHierarchyId() {
		return benefitHierarchyId;
	}

	public void setBenefitHierarchyId(String benefitHierarchyId) {
		this.benefitHierarchyId = benefitHierarchyId;
	}

	public String getBenefitHierarchyDescr() {
		return benefitHierarchyDescr;
	}

	public void setBenefitHierarchyDescr(String benefitHierarchyDescr) {
		this.benefitHierarchyDescr = benefitHierarchyDescr;
	}

	public String getReportGroup1() {
		return reportGroup1;
	}

	public void setReportGroup1(String reportGroup1) {
		this.reportGroup1 = reportGroup1;
	}

	public String getReportGroup2() {
		return reportGroup2;
	}

	public void setReportGroup2(String reportGroup2) {
		this.reportGroup2 = reportGroup2;
	}

	public String getReportGroup3() {
		return reportGroup3;
	}

	public void setReportGroup3(String reportGroup3) {
		this.reportGroup3 = reportGroup3;
	}

	public Boolean getDeceased() {
		return deceased;
	}

	public void setDeceased(Boolean deceased) {
		this.deceased = deceased;
	}

	public Boolean getOptinComm() {
		return optinComm;
	}

	public void setOptinComm(Boolean optinComm) {
		this.optinComm = optinComm;
	}

	public String getCorrespondenceType() {
		return correspondenceType;
	}

	public void setCorrespondenceType(String correspondenceType) {
		this.correspondenceType = correspondenceType;
	}

	public String getEdwPersonUniqueId() {
		return edwPersonUniqueId;
	}

	public void setEdwPersonUniqueId(String edwPersonUniqueId) {
		this.edwPersonUniqueId = edwPersonUniqueId;
	}
	
	/**
	 * Return the first and last name
	 * @return the property {@link #firstLastName} if it is not blank otherwise returns the combination of
	 * first and last name
	 */
	public String getFirstLastName() {
		if (StringUtils.isNotBlank(firstLastName))
			return firstLastName;

		return firstName + " " + lastName;
	}

	public void setFirstLastName(String firstLastName) {
		this.firstLastName = firstLastName;
	}

	public int getBatchNumber() {
		return batchNumber;
	}
	
	public void setBatchNumber(float batchNumber) {
		this.batchNumber = (int)batchNumber;
	}

	@JsonIgnore
	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	@JsonIgnore
	public int getTenantId() {

		String[] ids = memberId.split("_");

		try {
			return Integer.valueOf(ids[0]).intValue();
		}
		catch (NumberFormatException e) {
			return 0;
		}
	}

	public void setTenantId(Object tenantId) {
		try {
			this.tenantId = (int)tenantId;
		}
		catch (Exception e) {
			// do nothing
		}
	}

}
