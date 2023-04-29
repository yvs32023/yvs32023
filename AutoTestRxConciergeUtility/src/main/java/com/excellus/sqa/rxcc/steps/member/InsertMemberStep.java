/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.steps.member;

import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.cosmos.GroupQueries;
import com.excellus.sqa.rxcc.cosmos.MemberQueries;
import com.excellus.sqa.rxcc.dto.Group;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.PhoneNumber;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.Tenant.Type;
import com.excellus.sqa.step.AbstractStep;

/**
 * Insert member
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 04/05/2022
 */
public class InsertMemberStep extends AbstractStep {

	private static final Logger logger = LoggerFactory.getLogger(InsertMemberStep.class);

	private final static String STEP_NAME = "InsertMember - (%s)";
	private final static String STEP_DESC = "Inserts member manually into cosmos db";
	
	private Member member;
	private String memberId;
	private Tenant tenant;
	
	public InsertMemberStep(String memberId)
	{
		super(String.format(STEP_NAME, memberId), STEP_DESC);
		this.memberId = memberId;
	}
	
	/**
	 * Constructor with a member data
	 * 
	 * @param member {@link Member}
	 */
	public InsertMemberStep(Member member)
	{
		super(String.format(STEP_NAME, member.getMemberId()), STEP_DESC);
		this.member = member;
	}

	@Override
	public void run() 
	{
		super.stepStatus = Status.IN_PROGRESS;
		logger.info(super.print());
		
		try
		{
			// Get member if not provided
			if ( this.member == null ) {
				this.member = createMember(memberId);
			}
			
			// Get tenant if has not been determined
			if ( this.tenant == null ) {
				getTenant(member.getMemberId());
			}
			
			MemberQueries.insertMember(tenant.getSubscriptionName(), member);
			
			super.stepStatus = Status.COMPLETED;
		}
		catch (Exception e)
		{
			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}

		logger.info(print());
	}
	
	/**
	 * Retrieve tenant that will be associated with member id
	 * @param memberId of member
	 * @since 09/06/22
	 * @author Garrett Cosmiano (gcosmian)
	 */
	private void getTenant(String memberId)
	{
		GetTenantStep getTenantStep = new GetTenantStep(null, memberId);
		getTenantStep.run();
		this.tenant = getTenantStep.getTenant();
	}
	
	/**
	 * Create random member
	 * @return {@link Member}
	 * @since 09/06/22
	 * @author Garrett Cosmiano (gcosmian)
	 */
	private Member createMember(String memberId)
	{
		Member memberTestData = new Member();
		
		String[] ids = memberId.split("_");
		String upi = ids[1];
				
		memberTestData.setMemberId(memberId);
		memberTestData.setType("member");
		memberTestData.setCarrierId(RandomStringUtils.randomNumeric(4));
		memberTestData.setCarrierName("Excellus BCBS");
		memberTestData.setUpi(upi);
		
		// Get tenant
		if ( this.tenant == null ) {
			getTenant(memberId);
		}
		
		// Get random tenant
		Group group;
		if ( tenant.getTenantId() == Type.EHP.getTenantId() ) {
			group = GroupQueries.getRandomGroupEHP();
		}
		else if ( tenant.getTenantId() == Type.EXE.getTenantId() ) {
			group = GroupQueries.getRandomGroupEXE();
		}
		else {
			group = GroupQueries.getRandomGroup();
		}
		
		memberTestData.setGroupId(group.getGroupId());
		memberTestData.setGroupName(group.getRxccGroupName());
		
		memberTestData.setSubscriberId("" + RandomStringUtils.randomNumeric(9));
		memberTestData.setDependentCode("01");
		memberTestData.setFirstName(RandomStringUtils.randomAlphabetic(5, 15));
		memberTestData.setMiddleInitial("R");
		memberTestData.setLastName(RandomStringUtils.randomAlphabetic(5, 15));
		memberTestData.setGender("M");
		memberTestData.setDateBirth("19750802");
		memberTestData.setMemberRelationshipCode("LIFE PARTNER");
		memberTestData.setMemberRelationship("");
		memberTestData.setAddress1(RandomStringUtils.randomNumeric(5) + " Test Automation");
		memberTestData.setAddress2("Apt # 1");
		memberTestData.setAddress3("PO Box 1");
		memberTestData.setCity("Rochester");
		memberTestData.setState("NY");
		memberTestData.setPostalCode("14604");
		memberTestData.setCountry("US");
		memberTestData.setCounty("Monroe");
		memberTestData.setPhoneNumbers(Arrays.asList(new PhoneNumber("Home", "760-632-9845"), new PhoneNumber("Cell", "975-848-8932"), new PhoneNumber("Other", "658-415-7135")));
		memberTestData.setEmail("rasals7@unicef.org");
		memberTestData.setRxBenefitId("RX000100");
		memberTestData.setRxBenefitDescr("Pharmacy Plan $5/$25/$50");
		memberTestData.setEffDate("20220101");
		memberTestData.setTermDate("20430718");
		memberTestData.setFundingArrangementId("20430718");
		memberTestData.setFundingArrangementDescr("Prospective");
		memberTestData.setVendorFormularyCode("101");
		memberTestData.setVendorFormularyDescr("Excellus Commercial 3 Tier Open");
		memberTestData.setBenefitHierarchyId("101010");
		memberTestData.setBenefitHierarchyDescr("Benefit Hierachy Seven Description");
		memberTestData.setReportGroup1("Reporting Group 1 - Description 11");
		memberTestData.setReportGroup2("Reporting Group 2 - Description 12");
		memberTestData.setReportGroup3("Reporting Group 3 - Description 19");
		memberTestData.setDeceased(false);
		memberTestData.setOptinComm(false);
		memberTestData.setCorrespondenceType("Other");
		memberTestData.setBatchNumber(842719);
		memberTestData.setLastUpdated("");
		memberTestData.setLastUpdatedBy("");
		memberTestData.setVer("1.0");
		memberTestData.setId(memberId);
		
		return memberTestData;		
	}

	/*
	 * Getter
	 */
	
	public Member getMember() {
		return this.member;
	}
}

