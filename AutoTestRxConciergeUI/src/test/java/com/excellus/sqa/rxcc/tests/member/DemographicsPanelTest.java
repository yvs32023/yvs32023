/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.MemberQueries;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.PhoneNumber;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.pages.member.MemberDemographicPO;
import com.excellus.sqa.rxcc.steps.member.InsertMemberStep;
import com.excellus.sqa.rxcc.steps.member.OpenMemberStep;
import com.excellus.sqa.rxcc.steps.member.RetrieveMemberDemographicStep;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.step.IStep.Status;

/**
 * Notes:
 * 
 * GC (04/05/22) updated to make use of steps
 * 
 * @author Neeru Tagore (ntagore)
 * @since 02/18/2022
 * 
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_FULL_LOA", "RXCC_FULL_MED"})
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_DEMOGRAPHICS")
public class DemographicsPanelTest extends RxConciergeUITestBase
{
    static PageConfiguration pageConfiguration;
    static MemberDemographicPO memberDemographicPO;

    private static Member memberTestData;
    private static InsertMemberStep insertMemberStep;
    @BeforeAll
    public static void setup()
    {
        pageConfiguration = (PageConfiguration) BeanLoader.loadBean("memberPage");
        // create the member
        createMember();

        // STEP 1 - Insert member into Cosmos
        insertMemberStep = new InsertMemberStep(memberTestData);
        insertMemberStep.run();
    }

    @AfterAll
    public static void cleanUp()
    {
        if ( insertMemberStep.stepStatus() == Status.COMPLETED )
        {
            if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) {
            MemberQueries.deleteMember(Tenant.Type.EHP.getSubscriptionName(), memberTestData.getId(), memberTestData.getMemberId());}
            else if  (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_LOA")) {
                MemberQueries.deleteMember(Tenant.Type.LOA.getSubscriptionName(), memberTestData.getId(), memberTestData.getMemberId());}
            else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) {
                MemberQueries.deleteMember(Tenant.Type.MED.getSubscriptionName(), memberTestData.getId(), memberTestData.getMemberId());}        
        }
    }

    /*
     * Please Note there is an open defect related to Phone failure
     * https://dev.azure.com/ExcellusBCBS/EHP/_workitems/edit/9337 
     */

    @TestFactory
    @Test
    @Order(1)
    @DisplayName("3848-RxCC Member Panel")
    public List<DynamicNode> memberEHP() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        // STEP 1 - Make sure the test data was inserted into Cosmos
        tests.addAll(insertMemberStep.getTestResults());
        if ( insertMemberStep.stepStatus() != Status.COMPLETED )
            return tests;

        // STEP 2 - Open member demographic
        OpenMemberStep openMemberStep = new OpenMemberStep(driverBase.getWebDriver(), memberTestData.getMemberId());
        openMemberStep.run();
        tests.addAll(openMemberStep.getTestResults());

        if ( openMemberStep.stepStatus() != Status.COMPLETED )
            return tests;

        // STEP 3 - Get member demographic
        RetrieveMemberDemographicStep retrieveMemberDemographicStep = new RetrieveMemberDemographicStep(driverBase.getWebDriver());
        retrieveMemberDemographicStep.run();
        tests.addAll(retrieveMemberDemographicStep.getTestResults());

        if ( retrieveMemberDemographicStep.stepStatus() != Status.COMPLETED )
            return tests;
        // Member Demographic Validation

        tests.add(dynamicContainer("Member demographic data validation", memberTestData.compareMemberDemographics(retrieveMemberDemographicStep.getMember())));

        memberDemographicPO = new MemberDemographicPO(driverBase.getWebDriver(), pageConfiguration);
        memberDemographicPO.expandCollapseMemberDemographic(false);
        boolean isCollapsed = memberDemographicPO.isExpandCollapseMemberDemographic();
        tests.add(dynamicTest("Member Demographic section is collapsed", () -> assertFalse(isCollapsed)));

        memberDemographicPO.expandCollapseMemberDemographic(true);
        boolean isExpanded = memberDemographicPO.isExpandCollapseMemberDemographic();
        tests.add(dynamicTest("Member Demographic section is expanded", () -> assertTrue(isExpanded)));

        // Step 4 - Member Group Info validation

        tests.add(dynamicContainer("Member Group Information data validation", memberTestData.compareGroupInfo(retrieveMemberDemographicStep.getMember())));

        memberDemographicPO.expandCollapseMemberGroupInfo(false);
        boolean isGroupInfoCollapsed = memberDemographicPO.isExpandCollapseMemberGroupInfo();
        tests.add(dynamicTest("Member Group Info section is collapsed", () -> assertFalse(isGroupInfoCollapsed)));

        memberDemographicPO.expandCollapseMemberGroupInfo(true);
        boolean isGroupInfoExpanded = memberDemographicPO.isExpandCollapseMemberGroupInfo();
        tests.add(dynamicTest("Member Group Info section is expanded", () -> assertTrue(isGroupInfoExpanded)));

        // Step 5 -  Member Plan Info validation

        tests.add(dynamicContainer("Member Plan Information data validation", memberTestData.comparePlanInfo(retrieveMemberDemographicStep.getMember())));

        memberDemographicPO.expandCollapseMemberPlanInfo(false);
        boolean isPlanInfoCollapsed = memberDemographicPO.isExpandCollapseMemberPlanInfo();
        tests.add(dynamicTest("Member Plan Info section is collapsed", () -> assertFalse(isPlanInfoCollapsed)));

        memberDemographicPO.expandCollapseMemberPlanInfo(true);
        boolean isPlanInfoExpanded = memberDemographicPO.isExpandCollapseMemberPlanInfo();
        tests.add(dynamicTest("Member Plan Info section is expanded", () -> assertTrue(isPlanInfoExpanded)));

        return tests;
    }

    @Test
    @Order(3)	@DisplayName( "3852-RxCC Member Panel - Demographics - Termed Alert") 
    public void displayMemberLabelTermed()  throws ElementNotFoundException {
        boolean isLabelDisplayed = memberDemographicPO.isTermedLabelDisplayed();
        assertTrue(isLabelDisplayed);

    }


    @Test
    @Order(4)
    @DisplayName( "3855- RxCC Member Panel - Demographics - Opted out of Communication")
    public void editOptOutOfCommunication() throws ElementNotFoundException {

        String expectedValue = "";
        String actualValue = "";

        actualValue = memberDemographicPO.valueofOptOutofCommunication();
        if (actualValue.equalsIgnoreCase("Yes")) {
            expectedValue= "No"; 
        } else {
            expectedValue= "Yes";	
        }

        memberDemographicPO.clickMemberEditIcon();

        memberDemographicPO.selectOptOutOfCommunications();

        memberDemographicPO.clickSave();
        actualValue = "";
        SeleniumPageHelperAndWaiter.pause(2000);
        actualValue = memberDemographicPO.valueofOptOutofCommunication();

        assertEquals(expectedValue, actualValue);

    }


    @Test
    @Order(5)
    @DisplayName( "3853 - RxCC Member Panel - Demographics - Deceased Alert") 
    public void displayDeceasedAlert() throws ElementNotFoundException {
        memberDemographicPO.clickMemberEditIcon();
        memberDemographicPO.selectDeceased();
        memberDemographicPO.clickSave();
        boolean isDeceasedDisplayed  = memberDemographicPO.isLabelDeceasedDisplayed();
        if(isDeceasedDisplayed) {
            assertTrue(isDeceasedDisplayed);
        }
    }


    @Test
    @Order(6)
    @DisplayName( "3856 - RxCC Member Panel - Demographics - Correspondence Type") 
    public void updateCorrespondenceType() throws ElementNotFoundException {

        String currentValue = "";
        String expectedValue = "";
        String actualValue = "";

        currentValue = memberDemographicPO.retrieveSubscriberCorrespondenceType();

        if (currentValue.equalsIgnoreCase("Letter")) {
            expectedValue = "Email";
        } else if (currentValue.equalsIgnoreCase("Email")) {
            expectedValue = "Letter";
        }   else {
            expectedValue = "Letter"; 
        }

        memberDemographicPO.selectCorrespondenceType(currentValue);

        SeleniumPageHelperAndWaiter.pause(5000);
        actualValue = memberDemographicPO.retrieveSubscriberCorrespondenceType();
        assertEquals(expectedValue, actualValue);

    }

    /*
     * Helper method
     */

    private static void createMember()
    {

        memberTestData = new Member();
        String adTenantId;
        String upi = "333333";
        
        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_LOA")) {  
            adTenantId = "2f926508-ef7c-4226-a3e7-a3fc6f048b5d";
        } else if  (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) {
            adTenantId = "b89aec2f-fcec-4674-820b-dfa057e9507e";
        } else { 
            adTenantId = "87f4a82d-e2b6-44cc-9fff-828f94cf8ec9";
        }

            memberTestData.setMemberId(adTenantId + "_" + upi);
            memberTestData.setType("member");
            memberTestData.setCarrierId("7375");
            memberTestData.setCarrierName("Excellus BCBS");
            memberTestData.setUpi(upi);
            memberTestData.setGroupId("112576");
            memberTestData.setGroupName("MACNY");
            memberTestData.setSubscriberId("202275406");
            memberTestData.setDependentCode("01");
            memberTestData.setFirstName("Jarred");
            memberTestData.setMiddleInitial("R");
            memberTestData.setLastName("Asals");
            memberTestData.setGender("M");
            memberTestData.setDateBirth("19750802");
            memberTestData.setMemberRelationshipCode("LIFE PARTNER");
            memberTestData.setMemberRelationship("");
            memberTestData.setAddress1("96094 Coleman Road");
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
            memberTestData.setId(adTenantId + "_" + upi);

    }
}