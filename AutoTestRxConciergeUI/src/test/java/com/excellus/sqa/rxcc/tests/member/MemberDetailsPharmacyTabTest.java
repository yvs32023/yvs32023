/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opentest4j.TestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.MemberRxClaimQueries;
import com.excellus.sqa.rxcc.dto.MainMenu;
import com.excellus.sqa.rxcc.dto.MemberRxclaim;
import com.excellus.sqa.rxcc.dto.Pharmacy;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.Tenant.Type;
import com.excellus.sqa.rxcc.pages.home.MainMenuPO;
import com.excellus.sqa.rxcc.pages.member.MemberDetailsPharmacyPO;
import com.excellus.sqa.rxcc.pages.member.MemberSearchPO;
import com.excellus.sqa.rxcc.steps.member.GetMemberRXClaimStep;
import com.excellus.sqa.rxcc.steps.member.OpenMemberStep;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.step.IStep.Status;

/**
 * 
 * @author Husnain Zia (hzia)
 * @since 03/06/2022
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI","RXCC_FULL_MED","RXCC_FULL_LOA"})
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_PHARMACY")
public class MemberDetailsPharmacyTabTest extends RxConciergeUITestBase {
    private static final Logger logger = LoggerFactory.getLogger(MemberDetailsPharmacyTabTest.class);
    //Reference 
    PageConfiguration memberPage = (PageConfiguration) BeanLoader.loadBean("memberPage");
    MainMenuPO mainMenuPO = new MainMenuPO(driverBase.getWebDriver(), memberPage);
    MemberSearchPO memberSearchPO = new MemberSearchPO(driverBase.getWebDriver(), memberPage);
    MemberDetailsPharmacyPO Pharmacy = new MemberDetailsPharmacyPO(driverBase.getWebDriver(), memberPage);
    static String memberId;
    static List<Pharmacy> pharmList;
    @SuppressWarnings("null")
    @BeforeAll
    public static void setup() throws ElementNotFoundException{
        {
            MemberRxclaim memberWithRxclaim;          
            if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
                memberWithRxclaim= MemberRxClaimQueries.getRandomMemberWithRxclaimWithAdjDateStart(Tenant.Type.EHP.getSubscriptionName());}
            else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) { 
                memberWithRxclaim= MemberRxClaimQueries.getRandomMemberWithRxclaimWithAdjDateStart(Tenant.Type.MED.getSubscriptionName());}
            else {
                memberWithRxclaim = MemberRxClaimQueries.getRandomMemberWithRxclaimWithAdjDateStart(Tenant.Type.LOA.getSubscriptionName());}
        //    memberId= memberWithRxclaim.getMemberId();
            memberId = "87f4a82d-e2b6-44cc-9fff-828f94cf8ec9_T839246"; // Temporary solution to avoid member with lengthy note alrts causing Stale Element exception
                       
            OpenMemberStep step = new OpenMemberStep(driverBase.getWebDriver(), memberId);
            step.run();

            logger.info("*******memberId************"+memberId);

            if ( step.stepStatus() != Status.COMPLETED ) {
                throw new TestAbortedException("Unable to open the member id [" + memberId + "]", step.getStepException());
            }
          
            GetMemberRXClaimStep step2;
            if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
                step2 = new GetMemberRXClaimStep(Tenant.Type.EHP.getSubscriptionName(), memberId);}
            else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) { 
                step2 = new GetMemberRXClaimStep(Tenant.Type.MED.getSubscriptionName(), memberId );}
            else {
                step2 = new GetMemberRXClaimStep(Tenant.Type.LOA.getSubscriptionName(), memberId );}

            step2.run();
            if ( step2.stepStatus() != Status.COMPLETED ) {
                step2.getTestResults();
            }
            pharmList = step2.getPharmacysList();
            
        }

    }
    //Before each test the Member will be searched and Member Details page will display
    @BeforeEach
    public void searchMember() throws ElementNotFoundException {

        OpenMemberStep step = new OpenMemberStep(driverBase.getWebDriver(), memberId);
        step.run();

        logger.info("*******memberId************"+memberId);

        if ( step.stepStatus() != Status.COMPLETED ) {
            throw new TestAbortedException("Unable to open the member id [" + memberId + "]", step.getStepException());
        }
    }
    //After each test the Homepage will be loaded
    @AfterEach
    public void returnToHome() throws ElementNotFoundException {

        mainMenuPO.clickHomePage();
    }

    @Test
    @Order(1)
    @DisplayName ("3279 - RxCC Member Pharmacy Tab UI - Screen Verification")
    public void expandAll() throws ElementNotFoundException {
        //Click Pharmacy Tab
        Pharmacy.clickPharmacy();
        //Click Expand All button
        Pharmacy.clickExpandAll();
        //Check if expanded
        boolean displayed = Pharmacy.checkClaimsVisible();
        assertTrue(displayed);
    }

    @Test
    @Order(2)
    @DisplayName ("3279 - RxCC Member Pharmacy Tab UI - Screen Verification")
    public void collapseAll() throws ElementNotFoundException {
        //Click Pharmacy Tab
        Pharmacy.clickPharmacy();
        //Click Collapse All button
        Pharmacy.clickCollapseAll();
        //Check if expanded
        boolean displayed = Pharmacy.checkClaimsVisible();
        assertFalse(displayed);
    }

    @Test
    @Order(3)
    @DisplayName ("3279 - RxCC Member Pharmacy Tab UI - Screen Verification")
    //@Disabled (" need to review")
    public void searchPharmacy() throws ElementNotFoundException {
        //Click Pharmacy Tab
        Pharmacy.clickPharmacy(); 
        String pharmacyName=pharmList.get(0).getNpi();
        //Search Pharmacy string
        Pharmacy.searchPharmacy(pharmacyName);
        //Check search results
        List<String> expected=Arrays.asList(pharmacyName);
        String searchResults = Pharmacy.searchPharmacy(pharmacyName);
        assertThat(searchResults, containsInAnyOrder( expected.toArray(new String[expected.size()]) ) != null);	
    }
    @Test
    @Order(4)
    @DisplayName ("3281 - RxCC Member Pharmacy Tab - Common Layout Shell")
    public void commonLayoutShell() throws ElementNotFoundException {
        //Click Pharmacy Tab
        Pharmacy.clickPharmacy(); 
        //Assert Provider icon displayed
        boolean displayedProvider = mainMenuPO.iconValidation(MainMenu.PROVIDER_DIRECTORY);
        assertTrue(displayedProvider);
        //Assert Pharmacy icon displayed
        boolean displayedPharmacy = mainMenuPO.iconValidation(MainMenu.PHARMACY_DIRECTORY);
        assertTrue(displayedPharmacy);
        //Assert Intervention icon displayed
        boolean displayedInterventionRule = mainMenuPO.iconValidation(MainMenu.INTERVENTION_RULE_LIBRARY);;
        assertTrue(displayedInterventionRule);
        //Assert Intervention Queue icon displayed
        boolean displayedInterventionQueue = mainMenuPO.iconValidation(MainMenu.INTERVENTION_QUEUE);;
        assertTrue(displayedInterventionQueue);
        //Assert Formulary Icon displayed
        boolean displayedFormulary = mainMenuPO.iconValidation(MainMenu.FORMULARY_MAINTENANCE);;
        assertTrue(displayedFormulary);
        //Assert Simulation icon displayed
        boolean displayedSimulation = mainMenuPO.iconValidation(MainMenu.SIMULATION_QUEUE);;
        assertTrue(displayedSimulation);
        //Assert Fax Discrepancy icon displayed
        boolean displayedFaxDiscrepancy = mainMenuPO.iconValidation(MainMenu.FAX_DISCREPANCY_QUEUE);;
        assertTrue(displayedFaxDiscrepancy);
        //Assert Fuzzy search displayed
        boolean displayedSearch = mainMenuPO.validateFuzzySearchExist();
        assertTrue(displayedSearch);
        //Assert Logout button displayed
        boolean displayedLogout = mainMenuPO.validateLogoutExist();
        assertTrue(displayedLogout);


    }

    /**
     * GC (03/31/22) changes to use member that meets the criteria
     * Member to use must not have any Rx Claim (example 2_628164)
     * @throws ElementNotFoundException
     */
    @Test
    @Order(5)
    @Disabled("No current queries to find member with no claims")
    @DisplayName ("3282 - RxCC Member Pharmacy Tab - No Claims")
    public void noPharmacy() throws ElementNotFoundException {

        //Click claims tab
        Pharmacy.clickClaims();
        //Retrieve number of records from claims tab page
        List<String> claimsActualValue = Pharmacy.checkNumberOfrecords();
        String claimsText = claimsActualValue.get(0);
        //Check to see if claim records zero
        if(claimsText.matches("No claims found!")) {
            //Click Pharmacy tab
            Pharmacy.clickPharmacy();
            List<String> pharmacyActualValue = Pharmacy.checkNumberOfrecords();
            String pharmacyText = pharmacyActualValue.get(0);
            //Assert pharmacy tab has zero records and correct text
            assertEquals("No pharmacies found!", pharmacyText);
        }
        else {
            //			logger.info(claimsText + "is not zero");
            fail(claimsText + " is not zero - does not meet the requirement to set the 'No pharmacies found!' display");
        }
    }

}

