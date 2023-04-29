/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.MemberLegacyCorrespondenceQueries;
//import com.excellus.sqa.rxcc.cosmos.MemberLegacyInterventionQueries;
import com.excellus.sqa.rxcc.cosmos.MemberNoteQueries;
//import com.excellus.sqa.rxcc.cosmos.MemberRxClaimQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
//import com.excellus.sqa.rxcc.dto.MemberRxclaim;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.Tenant.Type;
import com.excellus.sqa.rxcc.dto.member.LegacyCorrespondenceColumns;
import com.excellus.sqa.rxcc.dto.member.MemberTabMenu;
import com.excellus.sqa.rxcc.pages.member.MemberLegacyCorrespondencePO;
import com.excellus.sqa.rxcc.pages.member.MemberPO;
import com.excellus.sqa.rxcc.steps.member.DeleteMemberStep;
import com.excellus.sqa.rxcc.steps.member.InsertMemberStep;
import com.excellus.sqa.rxcc.steps.member.OpenMemberStep;
import com.excellus.sqa.rxcc.workflows.member.OpenMemberWorkflow;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.step.IStep.Status;

/**
 * 
 * 
 * @author Garrett Cosmiano (gcosmian)
 * @since 09/06/2022
 *  
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI","RXCC_FULL_MED", "RXCC_FULL_LOA"})
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_LEGACY_CORRESPONDENCE")
@DisplayName("25042: Member Legacy Correspondence")
public class MemberLegacyCorrespondenceTest extends RxConciergeUITestBase

{
	private static final Logger logger = LoggerFactory.getLogger(MemberLegacyCorrespondenceTest.class);
	static PageConfiguration pageConfiguration;
	
	//Declared variables
	static String memberList;
    static MemberCorrespondence memberLegacyCorrespondence;
    static String memberId;
	
	@BeforeAll
	public static void setup()
	{
		pageConfiguration = BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class);
	}
	@Order(1)
	@DisplayName("Columns")
	@TestFactory
	public List<DynamicNode> columns()
	{
		logger.info("Validation columns");
		
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		// Using EHP to validate columns
		
		{   if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.EHP.getSubscriptionName());         
            logger.info("*************listOfMemberId******" + memberList);                      
            memberLegacyCorrespondence= MemberLegacyCorrespondenceQueries.getRandomMemberWithLegacyCorrespondenceandNote(memberList, Tenant.Type.EHP.getSubscriptionName());
            }        
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) {
            memberList= MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.MED.getSubscriptionName());
            memberLegacyCorrespondence= MemberLegacyCorrespondenceQueries.getRandomMemberWithLegacyCorrespondenceandNote(memberList, Tenant.Type.MED.getSubscriptionName());}
            
        else {
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.LOA.getSubscriptionName());
            memberLegacyCorrespondence = MemberLegacyCorrespondenceQueries.getRandomMemberWithLegacyCorrespondenceandNote(memberList, Tenant.Type.LOA.getSubscriptionName());}
        }

		memberId = (memberLegacyCorrespondence == null) ? "" : memberLegacyCorrespondence.getMemberId();
		// Open the member
		OpenMemberWorkflow openMemberWorkflow = new OpenMemberWorkflow(driverBase.getWebDriver(), pageConfiguration, memberId, true, MemberTabMenu.LEGACY_CORRESPONDENCE);
		openMemberWorkflow.run();
		tests.addAll(openMemberWorkflow.workflowStepResults());
		
		if ( openMemberWorkflow.workflowStatus() != Status.COMPLETED )
		{
			return tests;
		}
		
		try
		{
			MemberLegacyCorrespondencePO po = new MemberLegacyCorrespondencePO(driverBase.getWebDriver(), pageConfiguration);
			
			List<String> actual = po.retrieveColumns();
			List<String> expected = LegacyCorrespondenceColumns.getLegacyCorrespondenceColumns();
			
			tests.add( dynamicTest("Column [" + String.join(", ", expected)  + "]", () -> assertThat(actual, contains( expected.toArray(new String[expected.size()]) ) )) );
		}
		catch(Exception e)
		{
			tests.add(dynamicTest("Retrieving UI Columns", () -> fail("Unexpected exception", e)));
		}
		
		return tests;
	}
	
	@Order(2)
	@DisplayName("Member without legacy correspondence")
	@TestFactory
	public List<DynamicNode> noLegacyCorrespondence()
	{
		logger.info("Meber without legacy correspondence");
		
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
	
        Tenant tenant;

        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
            tenant = TenantQueries.getTenantByTenantId(Type.EHP.getTenantId());;}
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) { 
            tenant = TenantQueries.getTenantByTenantId(Type.MED.getTenantId());}
        else {
            tenant = TenantQueries.getTenantByTenantId(Type.LOA.getTenantId());}
		
		
		String memberId = tenant.getAdTenantId() + "_" + RandomStringUtils.randomNumeric(6);
		InsertMemberStep insertMemberStep = new InsertMemberStep(memberId);
		insertMemberStep.run();
		tests.addAll(insertMemberStep.getTestResults());
		
		if ( insertMemberStep.stepStatus() != Status.COMPLETED ) {
			return tests;
		}
		
		// Open member
		OpenMemberStep openMemberStep = new OpenMemberStep(driverBase.getWebDriver(), pageConfiguration, insertMemberStep.getMember().getMemberId());
		openMemberStep.run();
		tests.addAll(openMemberStep.getTestResults());
		
		if ( insertMemberStep.stepStatus() != Status.COMPLETED ) {
			return tests;
		}
		
		// Validation
		final String testDesc = "Validation of member tab menu, Legacy Correspondence"; 
		try
		{
			MemberPO memberPO = new MemberPO(driverBase.getWebDriver(), pageConfiguration);
			boolean isLegacyCorrespondence = memberPO.isMemberTabMenuPresent(MemberTabMenu.LEGACY_CORRESPONDENCE);
			tests.add(dynamicTest(testDesc, () -> assertFalse(isLegacyCorrespondence, "Menu is present but expected to be not present")));
		}
		catch (Exception e)
		{
			tests.add(dynamicTest(testDesc, () -> fail("Unexpected exception", e)));
		}
		
		// Delete dummy member
		DeleteMemberStep deleteMemberStep = new DeleteMemberStep(insertMemberStep.getMember().getMemberId());
		deleteMemberStep.run();
		tests.addAll(deleteMemberStep.getTestResults());
		
		return tests;
	}
	
	
	@Order(3)
	@DisplayName("EHP Member with legacy correspondence")
	@TestFactory
	public List<DynamicNode> ehpMember()
	{
		logger.info("Validating EHP member with legacy correspondnece");	
		if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) {
		    memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.EHP.getSubscriptionName()); 
		    return memberLegacyCorrespondenceTest(Type.EHP, memberList );}
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) {
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.MED.getSubscriptionName()); 
            return memberLegacyCorrespondenceTest(Type.MED, memberList);}
        else {
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.LOA.getSubscriptionName()); 
            return memberLegacyCorrespondenceTest(Type.LOA, memberList);}
	}
	
	@Order(4)
	@DisplayName("EXE Member with legacy correspondence")
	@TestFactory
	public List<DynamicNode> exeMember()
	{
		logger.info("Validating EXE member with legacy correspondnece");
	      if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) {
	          memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.EXE.getSubscriptionName()); 
	            return memberLegacyCorrespondenceTest(Type.EXE, memberList);}
	        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) { 
	            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.MED.getSubscriptionName());
	            return memberLegacyCorrespondenceTest(Type.MED, memberList);}
	        else {
	            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.LOA.getSubscriptionName()); 
	            return memberLegacyCorrespondenceTest(Type.LOA, memberList);}
	}
	
	/**
	 * Helper method to validate all legacy correspondence
	 * 
	 * @param tenantType {@link Type}
	 * @return list of test results
	 */
	private List<DynamicNode> memberLegacyCorrespondenceTest(Type tenantType, String memberNoteList)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		// Using EHP to validate columns
		
		String memberId = MemberLegacyCorrespondenceQueries.getRandomMemberWithLegacyCorrespondenceUI(tenantType, memberNoteList);	
		
		// Open the member
		OpenMemberWorkflow openMemberWorkflow = new OpenMemberWorkflow(driverBase.getWebDriver(), pageConfiguration, memberId, true, MemberTabMenu.LEGACY_CORRESPONDENCE);
		openMemberWorkflow.run();
		tests.addAll(openMemberWorkflow.workflowStepResults());
		
		if ( openMemberWorkflow.workflowStatus() != Status.COMPLETED )
		{
			return tests;
		}
		
		// Validation
		try
		{
			MemberLegacyCorrespondencePO po = new MemberLegacyCorrespondencePO(driverBase.getWebDriver(), pageConfiguration);
			
			List<MemberCorrespondence> actual	= po.retrieveLegacyCorrespondence();
			List<MemberCorrespondence> expected	= MemberLegacyCorrespondenceQueries.getLegacyCorrespondence(tenantType.getSubscriptionName(), memberId);
			
			
			tests.add( validateListOfLegacyCorrespondnece(expected, actual) );
		}
		catch(Exception e)
		{
			tests.add(dynamicTest("Retrieving UI Columns", () -> fail("Unexpected exception", e)));
		}
		
		return tests;
	}
	
	/**
	 * Validate each legacy correspondences
	 * 
	 * @param expected list of expected legacy correspondences
	 * @param actual list of actual (derived from UI) legacy correspondences
	 * @return list of test results
	 */
	private DynamicContainer validateListOfLegacyCorrespondnece(List<MemberCorrespondence> expected, List<MemberCorrespondence> actual)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		for ( MemberCorrespondence theExpected : expected )
		{
			boolean found = false;
			for ( MemberCorrespondence theActual : actual )
			{  
				if ( theExpected.equalsByNonId(theActual) )
				{
					tests.add(dynamicContainer("Legacy Correspondence (" + theExpected.getCorrespondenceId() + ")", 
							theExpected.compareLegacyCorrespondenceUI(theActual) ));
					
					found = true;
					break;
				}
			}
		
		
			if ( !found ) {
				tests.add(dynamicTest("Legacy Correspondence (" + theExpected.getCorrespondenceId() + ")", 
						() -> fail("Legacy correspondence not found:\n" + theExpected.toString())));
			}
		}
		
		return dynamicContainer("Legacy Correspondences", tests);
	}
	
 	
}


