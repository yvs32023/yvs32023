/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.opentest4j.TestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.Utility;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.MemberNoteQueries;
import com.excellus.sqa.rxcc.cosmos.MemberRxClaimQueries;
import com.excellus.sqa.rxcc.dto.MemberRxclaim;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.member.MemberTabMenu;
import com.excellus.sqa.rxcc.dto.member.PrescriptionClaimColumns;
import com.excellus.sqa.rxcc.pages.member.MemberPrescriptionClaimPO;
import com.excellus.sqa.rxcc.steps.member.MemberPrescriptionColumnFilterStep;
import com.excellus.sqa.rxcc.steps.member.OpenMemberStep;
import com.excellus.sqa.rxcc.workflows.member.OpenMemberWorkflow;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.utilities.SortOrder;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.step.IStep.Status;

/**
 * Notes:
 * 
 * @author Neeru Tagore (ntagore)
 * @since 02/18/2022
 *  
 */

// 04/12/2022 @nt Changes to use member to meet the criteria
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_FULL_LOA", "RXCC_FULL_MED"})
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_PRESCRIPTION")
public class PrescriptionClaimTest extends RxConciergeUITestBase
{
    private static final Logger logger = LoggerFactory.getLogger(PrescriptionClaimTest.class);
    static PageConfiguration pageConfiguration;
    static MemberPrescriptionClaimPO memberPrescriptionClaimPO;
    static List<MemberRxclaim> memberRxClaims;
    static List<MemberRxclaim> originalMemberRxClaims;
    static MemberRxclaim memberClaim;
    static String memberList;   
    static String memberId;

    @BeforeAll
    public static void setup() throws ElementNotFoundException{
        {   if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.EHP.getSubscriptionName());         
            logger.info("*************listOfMemberId******" + memberList);                      
            memberClaim= MemberRxClaimQueries.getRandomMemberWithRxclaimWithNoteAndAdjDateStart(memberList, Tenant.Type.EHP.getSubscriptionName());
            }        
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) {
            memberList= MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.MED.getSubscriptionName());
            memberClaim= MemberRxClaimQueries.getRandomMemberWithRxclaimWithNoteAndAdjDateStart(memberList, Tenant.Type.MED.getSubscriptionName());}
            
        else {
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.LOA.getSubscriptionName());
            memberClaim = MemberRxClaimQueries.getRandomMemberWithRxclaimWithNoteAndAdjDateStart(memberList, Tenant.Type.LOA.getSubscriptionName());}
        }
        memberId = (memberClaim == null) ? "" : memberClaim.getMemberId();
       
//        memberRxClaims = MemberRxClaimQueries.getRxClaimMemberUI(Tenant.Type.EHP.getSubscriptionName(), memberId);
        
        {
            if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) {
                memberRxClaims = MemberRxClaimQueries.getRxClaimMemberUI(Tenant.Type.EHP.getSubscriptionName(), memberId);}
            else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) {
                memberRxClaims = MemberRxClaimQueries.getRxClaimMemberUI(Tenant.Type.MED.getSubscriptionName(), memberId);}
            else {
                memberRxClaims = MemberRxClaimQueries.getRxClaimMemberUI(Tenant.Type.LOA.getSubscriptionName(), memberId);
            }
        }
        originalMemberRxClaims = memberRxClaims; // Store memberRxClaims for search by columns use

        OpenMemberStep step = new OpenMemberStep(driverBase.getWebDriver(), memberId);
        step.run();

        logger.info("*******memberId************"+memberClaim.getMemberId());

        if ( step.stepStatus() != Status.COMPLETED ) {
            throw new TestAbortedException("Unable to open the member id [" + memberId + "]", step.getStepException());
        }

        pageConfiguration = (PageConfiguration) BeanLoader.loadBean("memberPage");
        memberPrescriptionClaimPO = new MemberPrescriptionClaimPO(driverBase.getWebDriver(), pageConfiguration);
    }
    
    @BeforeEach
    public void clickPrescriptionClaims() throws ElementNotFoundException
    {

        memberPrescriptionClaimPO.clickheaderPrescriptionClaims();
    }
    @Test
    @Order(1)
    @DisplayName("3887-RxCC Member Claims Tab - Records Found")
    public void membersRecord() throws ElementNotFoundException 
    {

        int numberOfRecords = memberPrescriptionClaimPO.numberOfClaimRecords();

        logger.info("******Number of Rows ********* 2 "+ numberOfRecords);
        String claimRecord = memberPrescriptionClaimPO.retrieveMemberClaimRecord();
        logger.info("******Number of Records********* 3 "+ claimRecord);

    } 
    @Test
    @Order(2)
    @DisplayName("3880-RxCC Member Claims Tab - Sort Options")
    public void sortOptions() throws ElementNotFoundException
    {

        memberPrescriptionClaimPO.memberClaimsSort();
    }
    @TestFactory
    @Order(3)
    @DisplayName("3879-RxCC Member Claims Tab - Filters - Drug Name")
    public List<DynamicNode> filterDrugNames() throws ElementNotFoundException 
    {

        String filterValue = memberClaim.getDrugName();

        return runFilter("drugName", filterValue); 
    }

    @TestFactory
    @Order(4)
    @DisplayName("3879-RxCC Member Claims Tab - Filters - Drug Strength")
    public List<DynamicNode> filterStrength() throws ElementNotFoundException 
    {

        String filterValue = memberClaim.getProductStrength();

        return runFilter("strength", filterValue);

    }
    /*
     * @ntagore 01/24/23
     * added new Dosage Form filter functionality 
     * 
     */
    @TestFactory
    @Order(5)
    @DisplayName("3879-RxCC Member Claims Tab - Filters - Dosage Form")
    public List<DynamicNode> filterDosageForm() throws ElementNotFoundException 
    {

        String filterValue = memberClaim.getDosageForm();

        return runFilter("dosageForm", filterValue);

    }
    private List<DynamicNode> runFilter(String column, String filterValue)  //, String selection )
    {
        List<DynamicNode> test = new ArrayList<DynamicNode>();

        MemberPrescriptionColumnFilterStep step = new MemberPrescriptionColumnFilterStep(driverBase.getWebDriver(), column, filterValue, column);;
        step.run();

        test.addAll(step.getTestResults());

        if ( step.stepStatus() != Status.COMPLETED ) {
            return test;
        }

        test.add(dynamicTest("Member Id [" + memberId + "]", () -> assertEquals(step.getFilterRowCount(), step.getLabelRowCount())));

        return test;
    }
    @TestFactory
    @Order(6)
    @DisplayName("3879-RxCC Member Claims Tab - Filters - Provider Name")
    public List<DynamicNode> filterproviderName() throws ElementNotFoundException
    {

        String filterValue = memberClaim.getPrescriberFirstName()+ ' '+memberClaim.getPrescriberLastName() ;

        return runFilter ("providerName", filterValue);
    }
    @TestFactory
    @Order(7)
    @DisplayName("3879-RxCC Member Claims Tab - Filters - Pharmacy Name")
    public List<DynamicNode> filterpharmacyName() throws ElementNotFoundException 
    {
        String filterValue = memberClaim.getServiceProviderName();

        return runFilter ("pharmacyName", filterValue);
    }
    @TestFactory
    @Order(8)
    @DisplayName("3879-RxCC Member Claims Tab - Filters - Quantity")
    public List<DynamicNode> filterQuantity() throws ElementNotFoundException
    {
        int quantityDispensed = memberClaim.getQuantityDispensed();

        String filterValue = String.valueOf(quantityDispensed);

        return runFilter ("quantity", filterValue);
    }
    @TestFactory
    @Order(9)
    @DisplayName("3879-RxCC Member Claims Tab - Filters - Day Supply")
    public List<DynamicNode> filterQuantityDaySupply() throws ElementNotFoundException
    {
        int quantityDaySupply = memberClaim.getDaySupply();
        String filterValue = String.valueOf(quantityDaySupply);

        return runFilter ("daySupply", filterValue);
    }   
    @TestFactory
    @Order(10)
    @DisplayName("3879-RxCC Member Claims Tab - Filters - Status")
    public List<DynamicNode> filterStatus() throws ElementNotFoundException
    {
        String filterValue = memberClaim.getClaimStatusDescr();
        return runFilter ("status", filterValue);  
    }
    @TestFactory
    @Order(11)
    @DisplayName("3879-RxCC Member Claims Tab - Filters - Claim Id")
    public List<DynamicNode> filterClaimId() throws ElementNotFoundException 
    {
        String filterValue = memberClaim.getTransactionIdOrg();
        return runFilter ("claimId", filterValue);   
    }
    @Test
    @Order(12)
    @DisplayName("3878-RxCC Member Claims Tab - Dispense as Written (DAW)")
    public void memberClaimDaw() throws ElementNotFoundException {
        logger.info("Expand Button clicked");
        memberPrescriptionClaimPO.expandClaimIcon();
        logger.info("Expand Button passed");
        String labelDaw = memberPrescriptionClaimPO.memberClaimDAW();
        logger.info("******* DAW Label ***** " + labelDaw);
        memberPrescriptionClaimPO.expandClaimIcon();
        SeleniumPageHelperAndWaiter.pause(1000);

    }
    /*
     * added @ntagore 01/25/23
     */

    @Test
    @Order(13)
    @DisplayName("3880- RxCC Member RxClaim - Sort 'DrugName' by descending")
    public void sortDrugNameDesc() throws ElementNotFoundException
    {
        List<String> expected = memberRxClaims.stream()
                .map(claims -> claims.getDrugName().toUpperCase())
                .collect(Collectors.toList());
        
        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());
        
        memberPrescriptionClaimPO.sortColumn(PrescriptionClaimColumns.DRUG_NAME , SortOrder.DESCENDING);
        List<String> actual = memberPrescriptionClaimPO.retrieveColumnData(PrescriptionClaimColumns.DRUG_NAME);
        actual.replaceAll(data -> data.toUpperCase());

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(14)
    @DisplayName("3880- RxCC Member RxClaim - Sort Date by ascending")
    public void sortDateAsc() throws ElementNotFoundException
    {
        List<String> expected = memberRxClaims.stream()
                .map(claims -> Utility.convertCosmosDateToUI(claims.getAdjudicationDate(),  "yyyyMMdd"))
                .collect(Collectors.toList());   
        expected.sort(String.CASE_INSENSITIVE_ORDER);

        memberPrescriptionClaimPO.sortColumn(PrescriptionClaimColumns.DATE, SortOrder.ASCENDING);
        List<String> actualTemp = memberPrescriptionClaimPO.retrieveColumnData(PrescriptionClaimColumns.DATE);

        List<String> actual = new ArrayList<>();
        for (String date : actualTemp) {
            String[] dateParts = date.split(" ")[0].split("/");
            actual.add(dateParts[2] + dateParts[0] + dateParts[1]);
        }        
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(15)
    @DisplayName("3880- RxCC Member RxClaim - Sort 'DosageForm' by descending")
    public void sortDosageFormDesc() throws ElementNotFoundException
    {
        List<String> expected = memberRxClaims.stream()
                .map(claims -> claims.getDosageForm().toUpperCase())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        memberPrescriptionClaimPO.sortColumn(PrescriptionClaimColumns.DOSAGE_FORM , SortOrder.DESCENDING);
        List<String> actual = memberPrescriptionClaimPO.retrieveColumnData(PrescriptionClaimColumns.DOSAGE_FORM);
        actual.replaceAll(data -> data.toUpperCase());
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(16)
    @DisplayName("3880- RxCC Member RxClaim - Sort Strength by ascending")
    public void sortStrengthAsc() throws ElementNotFoundException
    {
        List<String> expected = memberRxClaims.stream()
                .map(claims -> claims.getProductStrength())
                .collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        memberPrescriptionClaimPO.sortColumn(PrescriptionClaimColumns.STRENGTH, SortOrder.ASCENDING);
        List<String> actual = memberPrescriptionClaimPO.retrieveColumnData(PrescriptionClaimColumns.STRENGTH);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(17)
    @DisplayName("3880- RxCC Member RxClaim - Sort ProviderName by descending")
    public void sortProviderNameDesc() throws ElementNotFoundException
    {
        List<String> expected = memberRxClaims.stream()
                .map(claims -> claims.getPrescriberFirstName().toUpperCase() + " "+ claims.getPrescriberLastName().toUpperCase())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        memberPrescriptionClaimPO.sortColumn(PrescriptionClaimColumns.PROVIDER_NPI , SortOrder.DESCENDING);
        List<String> actualTemp = memberPrescriptionClaimPO.retrieveColumnData(PrescriptionClaimColumns.PROVIDER_NPI);

        List<String> actual = actualTemp.stream()
                .map(s -> s.split("\\(")[0].trim())
                .collect(Collectors.toList());

        actual.replaceAll(data -> data.toUpperCase());
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(18)
    @DisplayName("3880- RxCC Member RxClaim - Sort Status by descending")
    public void sortStatusDesc() throws ElementNotFoundException
    {
        List<String> expected = memberRxClaims.stream()
                .map(claims -> claims.getClaimStatusDescr().toUpperCase())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        memberPrescriptionClaimPO.sortColumn(PrescriptionClaimColumns.STATUS , SortOrder.DESCENDING);
        List<String> actual = memberPrescriptionClaimPO.retrieveColumnData(PrescriptionClaimColumns.STATUS);
        actual.replaceAll(data -> data.toUpperCase());

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(19)
    @DisplayName("3880- RxCC Member RxClaim - Sort DaySupply by ascending")
    public void sortDaySupplyAsc() throws ElementNotFoundException
    {       
        List<String> expected = memberRxClaims.stream()
                .map(claims -> String.valueOf(claims.getDaySupply()))
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER);

        memberPrescriptionClaimPO.sortColumn(PrescriptionClaimColumns.DS, SortOrder.ASCENDING);
        List<String> actual = memberPrescriptionClaimPO.retrieveColumnData(PrescriptionClaimColumns.DS);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    @Test
    @Order(20)
    @DisplayName("3880- RxCC Member RxClaim - Sort ClaimId by descending")
    public void sortClaimIdDesc() throws ElementNotFoundException
    {       
        List<String> expected = memberRxClaims.stream()
                .map(claims -> claims.getTransactionIdOrg())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        memberPrescriptionClaimPO.sortColumn(PrescriptionClaimColumns.CLAIM_ID, SortOrder.DESCENDING);
        List<String> actual = memberPrescriptionClaimPO.retrieveColumnData(PrescriptionClaimColumns.CLAIM_ID);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    @Test
    @Order(21)
    @DisplayName("3880- RxCC Member RxClaim - Sort Quantity by descending")
    public void sortQuantityDesc() throws ElementNotFoundException
    {       
        List<String> expected = memberRxClaims.stream()
                .map(claims -> String.valueOf(claims.getQuantityDispensed()))
                .collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());
        memberPrescriptionClaimPO.sortColumn(PrescriptionClaimColumns.QTY, SortOrder.DESCENDING);
        List<String> actual = memberPrescriptionClaimPO.retrieveColumnData(PrescriptionClaimColumns.QTY);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    @Test
    @Order(22)
    @DisplayName("3880- RxCC Member RxClaim - Sort PharmacyName by ascending")
    public void sortPharmacyNameAsc() throws ElementNotFoundException
    {       
        List<String> expected = memberRxClaims.stream()
                .map(claims -> String.valueOf(claims.getServiceProviderName().toUpperCase()))
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER);

        memberPrescriptionClaimPO.sortColumn(PrescriptionClaimColumns.PHARMACY_NPI, SortOrder.ASCENDING);
        List<String> actualTemp = memberPrescriptionClaimPO.retrieveColumnData(PrescriptionClaimColumns.PHARMACY_NPI);       
        List<String> actual = actualTemp.stream()
                .map(s -> s.split("\\(")[0].trim())
                .collect(Collectors.toList());

        actual.replaceAll(data -> data.toUpperCase());

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    @TestFactory
    @Test
    @Order(23)
    @DisplayName("104616-RxCC Member claim Search Type by Dosage")    
    public List<DynamicNode> searchByDosage() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        SeleniumPageHelperAndWaiter.pause(3000);
        tests = memberPrescriptionClaimPO.searchRXClaimsColumns(memberRxClaims, "dosage", memberPrescriptionClaimPO.getSearchValueByColumn(5));
        return tests;
    }
    @TestFactory
    @Test
    @Order(24)
    @DisplayName("104616-RxCC Member claim Search Type by Drug Name")        
    public List<DynamicNode> searchByDrugName() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        List<MemberRxclaim> memberRxClaimsOriginalList;
        SeleniumPageHelperAndWaiter.pause(3000);
        memberRxClaimsOriginalList = originalMemberRxClaims;
        tests = memberPrescriptionClaimPO.searchRXClaimsColumns(memberRxClaimsOriginalList, "drugName", memberPrescriptionClaimPO.getSearchValueByColumn(3));
        return tests;
    }
    @TestFactory
    @Test
    @Order(25)
    @DisplayName("104616-RxCC Member claim Search Type by Provider Name")
    public List<DynamicNode> searchByProviderName() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        List<MemberRxclaim> memberRxClaimsOriginalList;
        SeleniumPageHelperAndWaiter.pause(3000);
        memberRxClaimsOriginalList = originalMemberRxClaims;
        tests = memberPrescriptionClaimPO.searchRXClaimsColumns(memberRxClaimsOriginalList, "providerName", memberPrescriptionClaimPO.getSearchValueByColumn(6));
        return tests;
    }
    @TestFactory
    @Test
    @Order(26)
    @DisplayName("104616-RxCC Member claim Search Type by Pharmacy Name")
    public List<DynamicNode> searchByPharmacyName() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        List<MemberRxclaim> memberRxClaimsOriginalList;
        SeleniumPageHelperAndWaiter.pause(3000);
        memberRxClaimsOriginalList = originalMemberRxClaims;
        tests = memberPrescriptionClaimPO.searchRXClaimsColumns(memberRxClaimsOriginalList, "pharmacyName", memberPrescriptionClaimPO.getSearchValueByColumn(7));
        return tests;
    }
    @TestFactory
    @Test
    @Order(27)
    @DisplayName("104616-RxCC Member claim Search Type by Quantity")        
    public List<DynamicNode> searchByQuantity() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        List<MemberRxclaim> memberRxClaimsOriginalList;
        SeleniumPageHelperAndWaiter.pause(3000);
        memberRxClaimsOriginalList = originalMemberRxClaims;
        tests = memberPrescriptionClaimPO.searchRXClaimsColumns(memberRxClaimsOriginalList, "quantity", memberPrescriptionClaimPO.getSearchValueByColumn(8));
        return tests;
    }
    @TestFactory
    @Test
    @Order(28)
    @DisplayName("104616-RxCC Member claim Search Type by Day Supply")        
    public List<DynamicNode> searchByDaySupply() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        List<MemberRxclaim> memberRxClaimsOriginalList;
        SeleniumPageHelperAndWaiter.pause(3000);
        memberRxClaimsOriginalList = originalMemberRxClaims;
        tests = memberPrescriptionClaimPO.searchRXClaimsColumns(memberRxClaimsOriginalList, "daySupply", memberPrescriptionClaimPO.getSearchValueByColumn(9));
        return tests;
    }
    @TestFactory
    @Test
    @Order(29)
    @DisplayName("104616-RxCC Member claim Search Type by Strength")
    public List<DynamicNode> searchByStrength() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        List<MemberRxclaim> memberRxClaimsOriginalList;
        SeleniumPageHelperAndWaiter.pause(3000);
        memberRxClaimsOriginalList = originalMemberRxClaims;
        tests = memberPrescriptionClaimPO.searchRXClaimsColumns(memberRxClaimsOriginalList, "strength", memberPrescriptionClaimPO.getSearchValueByColumn(4));
        return tests;
    }
    @TestFactory
    @Test
    @Order(30)
    @DisplayName("104616-RxCC Member claim Search Type by Date-Defect- 161152")
    @Disabled
    public List<DynamicNode> searchByDate() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        List<MemberRxclaim> memberRxClaimsOriginalList;
        SeleniumPageHelperAndWaiter.pause(3000);
        memberRxClaimsOriginalList = originalMemberRxClaims;
        tests = memberPrescriptionClaimPO.searchRXClaimsColumns(memberRxClaimsOriginalList, "date", memberPrescriptionClaimPO.getSearchValueByColumn(2));  
        return tests;
    }
    @TestFactory
    @Test
    @Order(31)
    @DisplayName("104616-RxCC Member claim Search Type by ClaimId-Defect- 173603")
    @Disabled
    public List<DynamicNode> searchByClaimId() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        List<MemberRxclaim> memberRxClaimsOriginalList;
        SeleniumPageHelperAndWaiter.pause(3000);
        memberRxClaimsOriginalList = originalMemberRxClaims;
        tests = memberPrescriptionClaimPO.searchRXClaimsColumns(memberRxClaimsOriginalList, "claimId", memberPrescriptionClaimPO.getSearchValueByColumn(11));
        return tests;
    }
    @Order(32)
    @DisplayName("RX Columns")
    @TestFactory
    public List<DynamicNode> columns()
    {
        logger.info("Validation columns");

        List<DynamicNode> tests = new ArrayList<DynamicNode>();      
        // Open the member
        OpenMemberWorkflow openMemberWorkflow = new OpenMemberWorkflow(driverBase.getWebDriver(), pageConfiguration, memberId, true, MemberTabMenu.PRESCRIPTION_CLAIMS);
        openMemberWorkflow.run();
        tests.addAll(openMemberWorkflow.workflowStepResults());

        if ( openMemberWorkflow.workflowStatus() != Status.COMPLETED )
        {
            return tests;
        }

        try
        {
            MemberPrescriptionClaimPO memberPrescriptionClaimPO = new MemberPrescriptionClaimPO(driverBase.getWebDriver(), pageConfiguration);

            List<String> actual = memberPrescriptionClaimPO.retrieveColumns();
            List<String> expected = PrescriptionClaimColumns.getRxColumnList();

            tests.add( dynamicTest("Column [" + String.join(", ", expected)  + "]", () -> assertThat(actual, contains( expected.toArray(new String[expected.size()]) ) )) );
        }
        catch(Exception e)
        {
            tests.add(dynamicTest("Retrieving UI Columns", () -> fail("Unexpected exception", e)));
        }     
        return tests;
    }  

}
