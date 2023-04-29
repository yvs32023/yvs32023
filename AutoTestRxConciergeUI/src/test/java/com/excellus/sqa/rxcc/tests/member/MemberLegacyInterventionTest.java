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
import com.excellus.sqa.rxcc.cosmos.MemberLegacyInterventionQueries;
import com.excellus.sqa.rxcc.cosmos.MemberNoteQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.MemberLegacyIntervention;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.Tenant.Type;
import com.excellus.sqa.rxcc.dto.member.LegacyInterventionColumns;
import com.excellus.sqa.rxcc.dto.member.MemberTabMenu;
import com.excellus.sqa.rxcc.pages.member.MemberLegacyInterventionPO;
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
 * @author Neeru Tagore (ntagore)
 * @since 09/13/2022
 *  
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_FULL_MED","RXCC_FULL_LOA"})
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_LEGACY_INTERVENTION")
@DisplayName("Member Legacy Intervention")

public class MemberLegacyInterventionTest extends RxConciergeUITestBase
{
    private static final Logger logger = LoggerFactory.getLogger(MemberLegacyInterventionTest.class);
    static PageConfiguration pageConfiguration;


    //Declared variables
    static String memberList;
    static String memberId;
    static MemberLegacyIntervention memberLegacyIntervention;

    @BeforeAll
    public static void setup()
    {
        pageConfiguration = BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class);
    }

    @Order(1)
    @DisplayName("Columns Validation")
    @TestFactory
    public List<DynamicNode> columns()
    {
        logger.info("Validation columns");

        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        {   if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.EHP.getSubscriptionName());
            memberLegacyIntervention = MemberLegacyInterventionQueries.getLegacyInterventionWithNoteUI(memberList, Tenant.Type.EHP.getSubscriptionName());}
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) {
            memberList= MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.MED.getSubscriptionName());
            memberLegacyIntervention = MemberLegacyInterventionQueries.getLegacyInterventionWithNoteUI(memberList, Tenant.Type.MED.getSubscriptionName());}

        else {
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.LOA.getSubscriptionName());
            memberLegacyIntervention = MemberLegacyInterventionQueries.getLegacyInterventionWithNoteUI(memberList, Tenant.Type.LOA.getSubscriptionName());}
        }       

        memberId = (memberLegacyIntervention == null) ? "" : memberLegacyIntervention.getMemberId();
        // Open the member
        OpenMemberWorkflow openMemberWorkflow = new OpenMemberWorkflow(driverBase.getWebDriver(), pageConfiguration, memberId, true, MemberTabMenu.LEGACY_INTERVENTION);
        openMemberWorkflow.run();
        tests.addAll(openMemberWorkflow.workflowStepResults());

        if ( openMemberWorkflow.workflowStatus() != Status.COMPLETED )
        {
            return tests;
        }

        try
        {
            MemberLegacyInterventionPO po = new MemberLegacyInterventionPO(driverBase.getWebDriver(), pageConfiguration);
            List<String> actual = po.retrieveColumns();
            List<String> expected = LegacyInterventionColumns.getLegacyInterventionColumns();

            tests.add( dynamicTest("Column [" + String.join(", ", expected)  + "]", () -> assertThat(actual, contains( expected.toArray(new String[expected.size()]) ) )) );
        }
        catch(Exception e)
        {
            tests.add(dynamicTest("Retrieving UI Columns", () -> fail("Unexpected exception", e)));
        }

        return tests;
    }

    @Order(2)
    @DisplayName("Member without legacy intervention Records")
    @TestFactory
    public List<DynamicNode> noLegacyIntervention()
    {
        logger.info("Meber without legacy intervention");

        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        // Insert a dummy member
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
        final String testDesc = "Validation of member tab menu, Legacy Intervention"; 
        try
        {
            MemberPO memberPO = new MemberPO(driverBase.getWebDriver(), pageConfiguration);
            boolean isLegacyIntervention = memberPO.isMemberTabMenuPresent(MemberTabMenu.LEGACY_INTERVENTION);
            tests.add(dynamicTest(testDesc, () -> assertFalse(isLegacyIntervention, "Menu is present but expected to be not present")));
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
    @DisplayName("EHP Legacy intervention Data Validation")
    @TestFactory
    public List<DynamicNode> ehpMember()
    {
        logger.info("Validating EHP member with legacy intervention");

        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.EHP.getSubscriptionName());
            return memberLegacyInterventionTest(Type.EHP, memberList);}
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) {
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.MED.getSubscriptionName());
            return memberLegacyInterventionTest(Type.MED, memberList);}
        else {
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.LOA.getSubscriptionName());
            return memberLegacyInterventionTest(Type.LOA,memberList);}      
    }

    @Order(4)
    @DisplayName("EXE Legacy intervention Data Validation")
    @TestFactory
    public List<DynamicNode> exeMember()
    {
        logger.info("Validating EXE member with legacy intervention");

        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.EXE.getSubscriptionName());
            return memberLegacyInterventionTest(Type.EXE, memberList);}
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) {
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.MED.getSubscriptionName());
            return memberLegacyInterventionTest(Type.MED, memberList);}
        else {
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.LOA.getSubscriptionName());
            return memberLegacyInterventionTest(Type.LOA,memberList);} 
    }

    /**
     * Helper method to validate all legacy intervention
     * 
     * @param tenantType {@link Type}
     * @return list of test results
     */
    private List<DynamicNode> memberLegacyInterventionTest(Type tenantType,String memberNoteList)
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        // Using EHP to validate columns
        String memberId = MemberLegacyInterventionQueries.getRandomMemberWithLegacyInterventionUI(tenantType, memberNoteList);
        // Open the member
        OpenMemberWorkflow openMemberWorkflow = new OpenMemberWorkflow(driverBase.getWebDriver(), pageConfiguration, memberId, true, MemberTabMenu.LEGACY_INTERVENTION);
        openMemberWorkflow.run();
        tests.addAll(openMemberWorkflow.workflowStepResults());

        if ( openMemberWorkflow.workflowStatus() != Status.COMPLETED )
        {
            return tests;
        }

        // Validation
        try
        {
            MemberLegacyInterventionPO po = new MemberLegacyInterventionPO(driverBase.getWebDriver(), pageConfiguration);
            List<MemberLegacyIntervention> actual	= po.retrieveLegacyIntervention();
            List<MemberLegacyIntervention> expected	= MemberLegacyInterventionQueries.getLegacyIntervention(tenantType.getSubscriptionName(), memberId);   

            tests.add( validateListOfLegacyIntervention(expected, actual) );
        }
        catch(Exception e)
        {
            tests.add(dynamicTest("Retrieving UI Columns", () -> fail("Unexpected exception", e)));
        }

        return tests;
    }

    /**
     * Validate each legacy intervention
     * 
     * @param expected list of expected legacy intervention
     * @param actual list of actual (derived from UI) legacy intervention
     * @return list of test results
     */
    private DynamicContainer validateListOfLegacyIntervention(List<MemberLegacyIntervention> expected, List<MemberLegacyIntervention> actual)
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        for ( MemberLegacyIntervention theExpected : expected )
        {
            boolean found = false;

            for ( MemberLegacyIntervention theActual : actual )
            {
                if ( theActual.equalsUI(theExpected) )
                {
                    tests.add(dynamicContainer("Legacy intervention (" + theExpected.getRuleName() + ")", 
                            theExpected.compareLegacyInterventionUI(theActual) ));

                    found = true;
                    break;
                }
            }

            if ( !found ) {
                tests.add(dynamicTest("Legacy Intervention (" + theExpected.getRuleName() + ")", 
                        () -> fail("Legacy intervention not found:\n" + theExpected.toString())));
            }
        }

        return dynamicContainer("Legacy Intervention", tests);
    }

}



