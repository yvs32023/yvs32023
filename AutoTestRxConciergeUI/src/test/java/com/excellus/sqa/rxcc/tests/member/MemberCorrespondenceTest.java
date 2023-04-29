/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

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
import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.MemberCorrespondenceQueries;
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.member.CorrespondenceColumns;
import com.excellus.sqa.rxcc.pages.member.MemberCorrespondencePO;
import com.excellus.sqa.rxcc.steps.member.OpenMemberStep;
import com.excellus.sqa.rxcc.steps.member.RetrieveMemberCorrespondenceStep;
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
 * @since 04/22/2022
 *  
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI","RXCC_FULL_LOA", "RXCC_FULL_MED"})
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_CORRESPONDENCE")
public class MemberCorrespondenceTest extends RxConciergeUITestBase
{
    private static final Logger logger = LoggerFactory.getLogger(MemberCorrespondenceTest.class);

    static PageConfiguration pageConfiguration;

    static MemberCorrespondencePO memberCorrespondencePO;

    static MemberCorrespondence memberCorrespondence;

    static List<MemberCorrespondence> memberCorrespondences;

    static String memberId;

    @BeforeAll
    public static void setup() throws ElementNotFoundException{
        {
            List<GenericCount> memberWithCorrespondence;     
            if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
                 memberWithCorrespondence = MemberCorrespondenceQueries.getMembersWithCorrespondenceMoreThanX(Tenant.Type.EHP.getSubscriptionName(), 10);}
                else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) { 
                   memberWithCorrespondence = MemberCorrespondenceQueries.getMembersWithCorrespondenceMoreThanX(Tenant.Type.MED.getSubscriptionName(), 0);}
                else {
                  memberWithCorrespondence = MemberCorrespondenceQueries.getMembersWithCorrespondenceMoreThanX(Tenant.Type.LOA.getSubscriptionName(), 0);}
            
            memberId = memberWithCorrespondence.get(0).getId();
           
            if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
                memberCorrespondences = MemberCorrespondenceQueries.getALLTenantMemberCorrespondences(Tenant.Type.EHP.getSubscriptionName(),memberId);}
               else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) { 
                   memberCorrespondences = MemberCorrespondenceQueries.getALLTenantMemberCorrespondences(Tenant.Type.MED.getSubscriptionName(),memberId);}
               else {
                   memberCorrespondences = MemberCorrespondenceQueries.getALLTenantMemberCorrespondences(Tenant.Type.LOA.getSubscriptionName(),memberId);}
            
            memberCorrespondences.forEach(correspondence -> logger.info("*************Print list memberCorrespondences*******************" + correspondence.toString()));

            logger.info("*******memberId************"+ memberId);

            OpenMemberStep step = new OpenMemberStep(driverBase.getWebDriver(), memberId);
            step.run();

            logger.info("*******memberId************"+memberId);

            if ( step.stepStatus() != Status.COMPLETED ) {
                throw new TestAbortedException("Unable to open the member id [" + memberId + "]", step.getStepException());
            }

            pageConfiguration = (PageConfiguration) BeanLoader.loadBean(BeanNames.MEMBER_PAGE);
            memberCorrespondencePO = new MemberCorrespondencePO(driverBase.getWebDriver(), pageConfiguration);
        }

    }

    @BeforeEach
    public void clickHeaderCorrespondence() throws ElementNotFoundException{

        memberCorrespondencePO.clickCorrespondence();
    }

    @TestFactory
    @Test
    @Order(1)
    @DisplayName("RxCC Member Correspondence Data")
    public List<DynamicNode> memberEHP() throws ElementNotFoundException { 

        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        // STEP - Get member correspondence
        RetrieveMemberCorrespondenceStep retrieveMemberCorrespondenceStep = new RetrieveMemberCorrespondenceStep(driverBase.getWebDriver());
        retrieveMemberCorrespondenceStep.run();

        tests.addAll(retrieveMemberCorrespondenceStep.getTestResults());

        if ( retrieveMemberCorrespondenceStep.stepStatus() != Status.COMPLETED )
            return tests;

        // Member Correspondence Validation
        List<MemberCorrespondence> actualCorrespondences = retrieveMemberCorrespondenceStep.getMemberCorrespondences(); // Taken from the UI

        int index = 1;
        for ( MemberCorrespondence expected : memberCorrespondences )
        {
            @SuppressWarnings("unused")
            boolean found = false;
            for ( MemberCorrespondence actual : actualCorrespondences )
            {
                if ( expected.getType().equals(actual.getType())    &&
                      expected.getCreatedBy().equals(actual.getCreatedBy())  )
                    
           {
                    tests.add(dynamicContainer("Member correspondence data validation [" + index + "]", expected.compareCorrespondenceUI(actual)));
                    index++;
                    found = true;
                    break;
                }
            }
        }
        return tests;
    }


    @Test
    @Order(2)
    @DisplayName("RxCC Member Correspondence Record validation")
    public void memberCorrespondenceRecord() throws ElementNotFoundException 
    {
        int cosmosDbCount = memberCorrespondences.size();

        String numberOfRecords = memberCorrespondencePO.numberOfCorrespondenceRecords();
        assertEquals(cosmosDbCount + " Records Found", numberOfRecords);

        String correspondenceRecord = memberCorrespondencePO.retrieveCorrespondenceLabelRecord();
        assertEquals(cosmosDbCount + " Records Found", correspondenceRecord);
    } 


    @Test
    @Order(3)
    @DisplayName("RxCC Member Correspondence - Sort Type by ascending")
    @Disabled("Need Review")
    public void sortTypeAsc() throws ElementNotFoundException
    {
//        List<CorrespondenceType> expected = memberCorrespondences.stream().map(correspondence -> correspondence.getCorrespondenceType())
//                .collect(Collectors.toList());
        
          
      //  expected.sort(String.CASE_INSENSITIVE_ORDER);
        
        List<String> expected = memberCorrespondences.stream().map(correspondence -> correspondence.getCorrespondenceType().toString())
                .collect(Collectors.toList());
        
        logger.info("*************Expected value of Type from DB 1 ************" +  expected);
        
//        expected.stream().sorted(Comparator.naturalOrder());  
        expected.sort(String.CASE_INSENSITIVE_ORDER);
       
        logger.info("*************Expected value of Type from DB 2 ************" +  expected);
     
    //    sort(expected, Comparator.reverseOrder());
        
        memberCorrespondencePO.sortColumn(CorrespondenceColumns.TYPE, SortOrder.ASCENDING);
        List<String> actual = memberCorrespondencePO.retrieveColumnData(CorrespondenceColumns.TYPE);
   //     actual.replaceAll(data -> data.toUpperCase());
        
        logger.info("*************actual value of Type from PO ************" +  actual);
        
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(4)
    @DisplayName("RxCC Member Correspondences - Sort Contact Name by descending")
    public void sortContactNameeDesc() throws ElementNotFoundException
    {
        List<String> expected = memberCorrespondences.stream()
                .map(correspondence -> correspondence.getContactName().toUpperCase())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        memberCorrespondencePO.sortColumn(CorrespondenceColumns.CONTACT_NAME, SortOrder.DESCENDING);
        List<String> actual = memberCorrespondencePO.retrieveColumnData(CorrespondenceColumns.CONTACT_NAME);
        actual.replaceAll(data -> data.toUpperCase());

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(5)
    @DisplayName("RxCC Member Correspondence - Sort 'Contact Title' by descending")

    public void sortContactTitleDesc() throws ElementNotFoundException
    {
        List<String> expected = memberCorrespondences.stream()
                .map(correspondence -> correspondence.getContactTitle())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        memberCorrespondencePO.sortColumn(CorrespondenceColumns.CONTACT_TITLE, SortOrder.DESCENDING);
        List<String> actual = memberCorrespondencePO.retrieveColumnData(CorrespondenceColumns.CONTACT_TITLE);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(6)
    @DisplayName("RxCC Member Correspondence - Sort 'CreatedBy' by descending")

    public void sortCreatedByDesc() throws ElementNotFoundException
    {
        List<String> expected = memberCorrespondences.stream()
                .map(correspondence -> correspondence.getCreatedBy())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        memberCorrespondencePO.sortColumn(CorrespondenceColumns.CREATED_BY, SortOrder.DESCENDING);
        List<String> actual = memberCorrespondencePO.retrieveColumnData(CorrespondenceColumns.CREATED_BY);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(7)
    @DisplayName("RxCC Member Correspondence - Sort 'Created date' by ascending")

    public void sortCreatedDateAsc() throws ElementNotFoundException
    {
        List<String> expected = memberCorrespondences.stream()
                .map(correspondence -> Utility.convertCosmosDateToUI(correspondence.getCreatedDateTime(),  "MM/dd/yyyy"))
                .collect(Collectors.toList());   

        expected.sort(String.CASE_INSENSITIVE_ORDER);

        memberCorrespondencePO.sortColumn(CorrespondenceColumns.CREATED_DATE, SortOrder.ASCENDING);
        List<String> actual = memberCorrespondencePO.retrieveColumnData(CorrespondenceColumns.CREATED_DATE);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(8)
    @DisplayName("RxCC Member Correspondence - Sort 'Edited date' by ascending")

    public void sortEditedDateAsc() throws ElementNotFoundException
    {
        List<String> expected = memberCorrespondences.stream()
                .map(correspondence -> Utility.convertCosmosDateToUI(correspondence.getLastUpdatedDateTime(),  "MM/dd/yyyy"))
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER);

        memberCorrespondencePO.sortColumn(CorrespondenceColumns.EDITED_DATE, SortOrder.ASCENDING);
        List<String> actual = memberCorrespondencePO.retrieveColumnData(CorrespondenceColumns.EDITED_DATE);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(9)
    @DisplayName("RxCC Member Correspondence - Sort 'Outcome' by descending")
    public void sortOutcomeDesc() throws ElementNotFoundException
    {
        List<String> expected = memberCorrespondences.stream()
                .map(correspondence -> correspondence.getCorrespondenceOutcome())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());
        
        memberCorrespondencePO.sortColumn(CorrespondenceColumns.OUTCOME, SortOrder.DESCENDING);
        List<String> actual = memberCorrespondencePO.retrieveColumnData(CorrespondenceColumns.OUTCOME);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(10)
    @DisplayName("RxCC Member Correspondence - Sort 'TargetDrug' by ascending")

    public void sortTargetDrugAsc() throws ElementNotFoundException
    {
        List<String> expected = memberCorrespondences.stream()
                .map(correspondence -> correspondence.getTargetDrug())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER);

        memberCorrespondencePO.sortColumn(CorrespondenceColumns.TARGET_DRUG, SortOrder.ASCENDING);
        List<String> actual = memberCorrespondencePO.retrieveColumnData(CorrespondenceColumns.TARGET_DRUG);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(11)
    @DisplayName("RxCC Member Correspondence - Sort 'Recipient Name' by ascending")

    public void sortRecipientNameAsc() throws ElementNotFoundException
    {
        List<String> expected = memberCorrespondences.stream()
                .map(correspondence -> correspondence.getProviderName().replace("  ", " "))
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER);

        memberCorrespondencePO.sortColumn(CorrespondenceColumns.RECIPIENT_NAME, SortOrder.ASCENDING);
        List<String> actual = memberCorrespondencePO.retrieveColumnData(CorrespondenceColumns.RECIPIENT_NAME);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(12)
    @DisplayName("RxCC Member Correspondence Search")

    public void memberCorrespondenceSearch() throws ElementNotFoundException
    {

        SeleniumPageHelperAndWaiter.pause(3000);
        memberCorrespondencePO.correspondenceSearch(memberCorrespondences.get(0).getContactName());
        String numberOfRecords = memberCorrespondencePO.numberOfCorrespondenceRecords();

        String correspondenceRecord = memberCorrespondencePO.retrieveCorrespondenceLabelRecord();
        assertEquals(numberOfRecords, correspondenceRecord);
    } 

    @TestFactory
    @Test
    @Order(13)
    @DisplayName("RxCC Member Correspondence Search Type")
    public List<DynamicNode> memberCorrespondenceSearchType() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        // Cosmos
        List<MemberCorrespondence> expectedCorrespondences = memberCorrespondences.stream()
                .filter(correspondence -> correspondence.getContactTitle().equalsIgnoreCase("Nurse"))
                .collect(Collectors.toList());
        // UI
        memberCorrespondencePO.correspondenceSearch("Nurse");
        SeleniumPageHelperAndWaiter.pause(3000);
        List<MemberCorrespondence> actualCorrespondences = memberCorrespondencePO.retrieveMemberCorrespondenceInfo();
        SeleniumPageHelperAndWaiter.pause(3000);
        for ( MemberCorrespondence expected : expectedCorrespondences )
        { 
            @SuppressWarnings("unused")
            boolean found = false; 
            for ( MemberCorrespondence actual : actualCorrespondences )
            {
                if ( expected.getContactTitle().equals(actual.getContactTitle()) 
                        &&
                     //   expected.getContactName().equals(actual.getContactName()) )
                    expected.getProviderName().equals(actual.getProviderName()) )
                {
                    tests.addAll(expected.compareCorrespondenceUI(actual));
                    found = true;
                    break;
                }
            }

        }
        return tests;
    } 

    @Test
    @Order(14)
    @DisplayName ("RxCC Member Correspondence Expand All")

    public void expandAll() throws ElementNotFoundException
    {
        memberCorrespondencePO.buttonExpandCollapseMemberCorrespondence(true);
        boolean isCorrespondenceExpanded = memberCorrespondencePO.isbuttonExpandCollapseMemberCorrespondence();
        assertTrue(isCorrespondenceExpanded);
    }

    @Test
    @Order(15)
    @DisplayName ("RxCC member Correspondence Collapse All")

    public void collapseAll() throws ElementNotFoundException 
    {
        memberCorrespondencePO.buttonExpandCollapseMemberCorrespondence(false);
        boolean isCorrespondenceCollapsed = memberCorrespondencePO.isbuttonExpandCollapseMemberCorrespondence();
        assertFalse(isCorrespondenceCollapsed);  
    }

}


