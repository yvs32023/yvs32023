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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
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
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.MemberNoteQueries;
import com.excellus.sqa.rxcc.cosmos.MemberRxClaimQueries;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.member.MemberProviderTabColumns;
import com.excellus.sqa.rxcc.pages.home.MainMenuPO;
import com.excellus.sqa.rxcc.pages.member.MemberDetailsProviderPO;
import com.excellus.sqa.rxcc.pages.member.MemberSearchPO;
import com.excellus.sqa.rxcc.steps.member.GetMemberRXClaimStep;
import com.excellus.sqa.rxcc.steps.member.MemberProviderFilterStep;
import com.excellus.sqa.rxcc.steps.member.OpenMemberStep;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.utilities.SortOrder;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.step.IStep.Status;
import com.excellus.sqa.rxcc.dto.MainMenu;
import com.excellus.sqa.rxcc.dto.Provider;
import com.excellus.sqa.rxcc.dto.MemberRxclaim;

/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 02/24/2022
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI","RXCC_FULL_MED", "RXCC_FULL_LOA"})
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_PROVIDER")
public class MemberDetailsProviderTabTest extends RxConciergeUITestBase {
    private static final Logger logger = LoggerFactory.getLogger(MemberDetailsProviderTabTest.class);
    //Reference 
    PageConfiguration memberPage = (PageConfiguration) BeanLoader.loadBean("memberPage");
    MainMenuPO mainMenuPO = new MainMenuPO(driverBase.getWebDriver(), memberPage);
    MemberSearchPO memberSearchPO = new MemberSearchPO(driverBase.getWebDriver(), memberPage);
    MemberDetailsProviderPO Provider = new MemberDetailsProviderPO(driverBase.getWebDriver(), memberPage);

    static String memberId;
    static List<Provider> providerList;
    static String memberList;
    static Provider memberProvider;
    static MemberRxclaim memberWithRxclaim;

    static List<Provider> expectedProviderList;
    static List<Provider> actualProviderList;

    @BeforeAll
    public static void setup() throws ElementNotFoundException{

        {   if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.EXE.getSubscriptionName());         
            logger.info("*************listOfMemberId******" + memberList);                      
            memberWithRxclaim= MemberRxClaimQueries.getRandomMemberWithRxclaimWithNoteAndAdjDateStart(memberList, Tenant.Type.EXE.getSubscriptionName());
        }        
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) {
            memberList= MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.MED.getSubscriptionName());
            memberWithRxclaim= MemberRxClaimQueries.getRandomMemberWithRxclaimWithNoteAndAdjDateStart(memberList, Tenant.Type.MED.getSubscriptionName());}

        else {
            memberList = MemberNoteQueries.getMemberWithNoteSizeLimit(Tenant.Type.LOA.getSubscriptionName());
            memberWithRxclaim = MemberRxClaimQueries.getRandomMemberWithRxclaimWithNoteAndAdjDateStart(memberList, Tenant.Type.LOA.getSubscriptionName());}
        }

        memberId= memberWithRxclaim.getMemberId();

        OpenMemberStep step = new OpenMemberStep(driverBase.getWebDriver(), memberId);
        step.run();

        logger.info("*******memberId************"+memberId);

        if ( step.stepStatus() != Status.COMPLETED ) {
            throw new TestAbortedException("Unable to open the member id [" + memberId + "]", step.getStepException());
        }
        GetMemberRXClaimStep step2;

        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
            step2 = new GetMemberRXClaimStep(Tenant.Type.EXE.getSubscriptionName(), memberId);}
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) { 
            step2 = new GetMemberRXClaimStep(Tenant.Type.MED.getSubscriptionName(), memberId);}
        else {
            step2 = new GetMemberRXClaimStep(Tenant.Type.LOA.getSubscriptionName(), memberId);}

        step2.run();

        if ( step2.stepStatus() != Status.COMPLETED ) {
            step2.getTestResults();
        }
        providerList = step2.getProvidersList();
    }

    //Before each test the Member will be searched and Member Details page will display
    @BeforeEach
    public void searchMember() throws ElementNotFoundException {
        Provider.clickProvider();
        //Provider.filterClearProvider();
    }

    /**
     * GC (03/31/22) changes to use member that meets the criteria
     */
    @Test
    @Order(1)
    @Disabled("No current queries to find member with no claims")
    @DisplayName ("3099 - RxCC Member Providers Tab - No Providers")
    public void noProviders() throws ElementNotFoundException {
        //Retrieve number of records from claims tab page
        List<String> claimsActualValue = Provider.checkNumberOfrecords();
        String claimsText = claimsActualValue.get(0);
        //Check to see if claim records zero
        if(claimsText.matches("No claims found!")) {
            //Click Providers tab
            Provider.clickProvider();
            List<String> providerActualValue = Provider.checkNumberOfrecords();
            String providerText = providerActualValue.get(0);
            //Assert providers tab has zero records and correct text
            assertEquals("No providers found!", providerText);
        }
        else {
            //			logger.info(claimsText + "is not zero");
            fail(claimsText + " is not zero - does not meet the requirement to set the 'No providers found!' display");
        }
    }

    @Test
    @Order(2)
    @DisplayName ("3325 - RxCC Member Providers Tab - Expand/Collapse Rows")
    public void expandAll() throws ElementNotFoundException {
        //Click Expand All button
        Provider.clickExpandAll();
        //Check if expanded
        boolean displayed = Provider.checkClaimsVisible();
        assertTrue(displayed);
    }

    @Test
    @Order(3)
    @DisplayName ("3325 - RxCC Member Providers Tab - Expand/Collapse Rows")
    public void collapseAll() throws ElementNotFoundException {
        //Click Collapse All button
        Provider.clickCollapseAll();
        //Check if expanded
        boolean displayed = Provider.checkClaimsVisible();
        assertTrue(displayed);
    }

    @Test
    @Order(4)
    @DisplayName ("3130 - RxCC Member Providers Tab - Common Layout Shell")
    public void commonLayoutShell() throws ElementNotFoundException {
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

    @Test
    @Order(5)
    @DisplayName("Member Provider Tab- Screen Verification (column headers)")
    public void validateColumnHeaders() throws ElementNotFoundException
    {
        List<String> expected = MemberProviderTabColumns.getProviderColumnList();
        List<String> actual = Provider.retrieveColumnHeaders();
        assertThat(actual, contains(expected.toArray(new String[expected.size()])));
    }

    @Test
    @Order(6)
    @DisplayName("3326- Provider - Sort ProviderName by descending")
    public void sortProviderNameDesc() throws ElementNotFoundException
    {
        List<String> expected = providerList.stream()
                .map(providers -> providers.getFirstName().toUpperCase() + " "+ providers.getLastName().toUpperCase())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        Provider.sortColumn(MemberProviderTabColumns.NAME_NPI, SortOrder.DESCENDING);
        List<String> actualTemp = Provider.retrieveColumnData(MemberProviderTabColumns.NAME_NPI);

        List<String> actual = actualTemp.stream()
                .map(s -> s.split("\\(")[0].trim())
                .collect(Collectors.toList());

        actual.replaceAll(data -> data.toUpperCase());
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(7)
    @DisplayName("3326- Provider - Sort Phone Number by descending")
    public void sortPhoneNumberDesc() throws ElementNotFoundException
    {
        List<String> expected = providerList.stream()
                .map(providers -> providers.getDefaultOfficeLocation().getNormalizeNumber(providers.getDefaultOfficeLocation().getPhoneNumber()))
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        Provider.sortColumn(MemberProviderTabColumns.PHONE, SortOrder.DESCENDING);
        List<String> actual  = Provider.retrieveColumnData(MemberProviderTabColumns.PHONE);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(8)
    @DisplayName("3326- Provider - Sort Fax Number by descending")
    public void sortFaxNumberDesc() throws ElementNotFoundException
    {
        List<String> expected = providerList.stream()
                .map(providers -> providers.getDefaultOfficeLocation().getNormalizeNumber(providers.getDefaultOfficeLocation().getFaxNumber()))
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        Provider.sortColumn(MemberProviderTabColumns.FAX, SortOrder.DESCENDING);
        List<String> actual  = Provider.retrieveColumnData(MemberProviderTabColumns.FAX);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(9)
    @DisplayName("3326- Provider - Sort City by ascending")
    public void sortCityAsc() throws ElementNotFoundException
    {
        List<String> expected = providerList.stream()
                .map(providers -> providers.getDefaultOfficeLocation().getCity().toUpperCase())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        Provider.sortColumn(MemberProviderTabColumns.CITY, SortOrder.ASCENDING);
        List<String> actual  = Provider.retrieveColumnData(MemberProviderTabColumns.CITY);
        actual.replaceAll(data -> data.toUpperCase());
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(10)
    @DisplayName("3326- Provider - Sort State by ascending")
    public void sortStateAsc() throws ElementNotFoundException
    {
        List<String> expected = providerList.stream()
                .map(providers -> providers.getDefaultOfficeLocation().getState().toUpperCase())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        Provider.sortColumn(MemberProviderTabColumns.STATE, SortOrder.ASCENDING);
        List<String> actual  = Provider.retrieveColumnData(MemberProviderTabColumns.STATE);
        actual.replaceAll(data -> data.toUpperCase());
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(11)
    @DisplayName("3326- Provider - Sort Postal Code by ascending")
    public void sortPostalCodeAsc() throws ElementNotFoundException
    {
        List<String> expected = providerList.stream()
                .map(providers -> providers.getDefaultOfficeLocation().getPostalCode())
                .collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        Provider.sortColumn(MemberProviderTabColumns.POSTAL_CODE, SortOrder.ASCENDING);
        List<String> actual  = Provider.retrieveColumnData(MemberProviderTabColumns.POSTAL_CODE);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(12)
    @DisplayName("3326- Provider - Sort # of Location by ascending")
    public void sortNumberOfLocationDesc() throws ElementNotFoundException
    {
        List<String> expected = providerList.stream()
                .map(providers -> providers.getDefaultOfficeLocation().getId())
                .collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        Provider.sortColumn(MemberProviderTabColumns.NUMBER_OF_LOCATIONS, SortOrder.DESCENDING);
        List<String> actual  = Provider.retrieveColumnData(MemberProviderTabColumns.NUMBER_OF_LOCATIONS);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(13)
    @DisplayName("3326- Provider - Sort Taxonomy by ascending")
    public void sortTaxonomyDesc() throws ElementNotFoundException
    {
        List<String> expected = providerList.stream()
                .map(providers -> providers.getTaxonomyDescr().toUpperCase())
                .collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        Provider.sortColumn(MemberProviderTabColumns.TAXONOMY, SortOrder.DESCENDING);
        List<String> actual  = Provider.retrieveColumnData(MemberProviderTabColumns.TAXONOMY);
        actual.replaceAll(data -> data.toUpperCase());
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(14)
    @DisplayName("3326- Provider - Sort Provider Status by ascending")
    public void sortProviderStatusDesc() throws ElementNotFoundException
    {
        List<String> expected = providerList.stream()
                .map(providers -> providers.getStatusInd().toUpperCase())
                .collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        Provider.sortColumn(MemberProviderTabColumns.PROVIDER_STATUS, SortOrder.DESCENDING);
        List<String> actual  = Provider.retrieveColumnData(MemberProviderTabColumns.PROVIDER_STATUS);
        actual.replaceAll(data -> data.toUpperCase());
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(15)
    @DisplayName("3326- Provider - Sort Provider Verified by ascending")
    public void sortProviderVerifiedDesc() throws ElementNotFoundException
    {
        List<String> expected = providerList.stream()
                .map(providers -> providers.getDefaultOfficeLocation().isFaxVerified().toString() )
                .collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());
        Provider.sortColumn(MemberProviderTabColumns.PROVIDER_VERIFIED, SortOrder.DESCENDING);
        List<String> actual  = Provider.retrieveColumnData(MemberProviderTabColumns.PROVIDER_VERIFIED);
        actual.replaceAll(data -> data.toString());
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    private List<DynamicNode> runFilter(MemberProviderTabColumns column, String filterValue) 
    {
        List<DynamicNode> test = new ArrayList<DynamicNode>();
        MemberProviderFilterStep step = new MemberProviderFilterStep(driverBase.getWebDriver(),column , filterValue);
        step.run();

        test.addAll(step.getTestResults());

        if ( step.stepStatus() != Status.COMPLETED ) {
            return test;
        }
        List<String> expectedValues = new ArrayList<String>();
        for ( Provider provider : providerList )
        {
            switch (column) {
            case NAME_NPI:
                if ( StringUtils.equalsIgnoreCase (provider.getNpi(), filterValue) ) {
                    expectedValues.add(provider.getNpi());
                }
                break;
            case POSTAL_CODE:
                if ( StringUtils.equalsIgnoreCase(provider.getDefaultOfficeLocation().getPostalCode(), filterValue) ) {
                    expectedValues.add(provider.getLastName());
                }
                break;
            case TAXONOMY:
                if ( StringUtils.equalsIgnoreCase(provider.getTaxonomyDescr(), filterValue) ) {
                    expectedValues.add(provider.getTaxonomyDescr());
                }
                break; 
            case PHONE:
                if ( StringUtils.equalsIgnoreCase(provider.getDefaultOfficeLocation().getPhoneNumber(), filterValue) ) {
                    expectedValues.add(Provider.getNormalizePhoneOrFaxNumber(provider.getDefaultOfficeLocation().getPhoneNumber()));
                }
                break;
            case FAX:
                if ( StringUtils.equalsIgnoreCase(provider.getDefaultOfficeLocation().getFaxNumber(), filterValue) ) {
                    expectedValues.add(Provider.getNormalizePhoneOrFaxNumber(provider.getDefaultOfficeLocation().getFaxNumber()));
                }
                break;
            case CITY:
                if ( StringUtils.equalsIgnoreCase(provider.getDefaultOfficeLocation().getCity(), filterValue) ) {
                    expectedValues.add(provider.getDefaultOfficeLocation().getCity());
                }
                break;
            case STATE:
                if ( StringUtils.equalsIgnoreCase(provider.getDefaultOfficeLocation().getState(), filterValue) ) {
                    expectedValues.add(provider.getDefaultOfficeLocation().getState());
                }
                break;
            case NUMBER_OF_LOCATIONS:
                if ( StringUtils.equalsIgnoreCase(provider.getDefaultOfficeLocation().getId(), filterValue) ) {
                    expectedValues.add(provider.getDefaultOfficeLocation().getId());
                }
                break;
            case PROVIDER_STATUS:
                if ( StringUtils.equalsIgnoreCase(provider.getStatusInd(), filterValue) ) {
                    expectedValues.add(provider.getStatusInd());
                }
                break;
            case PROVIDER_VERIFIED:
                if ( StringUtils.equalsIgnoreCase(provider.getDefaultOfficeLocation().isFaxVerified().toString(), filterValue) ) {
                    expectedValues.add(provider.getDefaultOfficeLocation().isFaxVerified().toString());
                }
                break;

            default:
                break;
            }
        }
        test.add(dynamicTest("Member Id [" + memberId + "]", () -> assertEquals(step.getFilterRowCount(), step.getLabelRowCount())));
        test.add(dynamicTest("Record count display [" + expectedValues.size() + "]", () -> assertEquals(expectedValues.size() + " Records Found", step.getLabelRowCount())));
        test.add(dynamicTest("Provider record rows [" + expectedValues.size() + "]", () -> assertEquals(expectedValues.size() + " Records Found", step.getFilterRowCount())));

        return test;
    }

    @TestFactory
    @Order(16)
    @DisplayName("108935-RxCC Member Provider Tab - Filters - NPI")
    public List<DynamicNode> filterNPI() throws ElementNotFoundException 
    {
        List<String> filterValueList = providerList.stream().map(provider -> provider.getNpi()).collect(Collectors.toList());       
        logger.info("*********** filterValueList ****" + filterValueList);
        String filterValue = filterValueList.get(0);

        logger.info("*********** filterValue  ****" + filterValue );
        return runFilter(MemberProviderTabColumns.NAME_NPI, filterValue);

    }

    @TestFactory
    @Order(17)
    @DisplayName("108935-RxCC Member Provider Tab - Filters - PhoneNumber")
    public List<DynamicNode> filterPhone() throws ElementNotFoundException 
    {
        List<String> filterValueList = providerList.stream().map(provider -> provider.getDefaultOfficeLocation().getPhoneNumber()).collect(Collectors.toList());       
        logger.info("*********** filterValueList ****" + filterValueList);
        String filterValue = filterValueList.get(0);

        logger.info("*********** filterValue  ****" + filterValue );
        return runFilter(MemberProviderTabColumns.PHONE, filterValue);

    }

    @TestFactory
    @Order(18)
    @DisplayName("108935-RxCC Member Provider Tab - Filters - Fax")
    public List<DynamicNode> filterFax() throws ElementNotFoundException 
    {
        List<String> filterValueList = providerList.stream().map(provider -> provider.getDefaultOfficeLocation().getFaxNumber()).collect(Collectors.toList());       
        logger.info("*********** filterValueList ****" + filterValueList);
        String filterValue = filterValueList.get(0);

        logger.info("*********** filterValue  ****" + filterValue );
        return runFilter(MemberProviderTabColumns.FAX, filterValue);

    }

    @TestFactory
    @Order(19)
    @DisplayName("108935-RxCC Member Provider Tab - Filters - City")
    public List<DynamicNode> filterCity() throws ElementNotFoundException 
    {
        List<String> filterValueList = providerList.stream().map(provider -> provider.getDefaultOfficeLocation().getCity()).collect(Collectors.toList());       
        logger.info("*********** filterValueList ****" + filterValueList);
        String filterValue = filterValueList.get(0);

        logger.info("*********** filterValue  ****" + filterValue );
        return runFilter(MemberProviderTabColumns.CITY, filterValue);

    }

    @TestFactory
    @Order(20)
    @DisplayName("108935-RxCC Member Provider Tab - Filters - State")
    public List<DynamicNode> filterState() throws ElementNotFoundException 
    {        
        List<String> filterValueList = providerList.stream().map(provider -> provider.getDefaultOfficeLocation().getState()).collect(Collectors.toList());       
        logger.info("*********** filterValueList ****" + filterValueList);
        String filterValue = filterValueList.get(0);

        logger.info("*********** filterValue  ****" + filterValue );
        return runFilter(MemberProviderTabColumns.STATE, filterValue);

    }

    @TestFactory
    @Order(21)
    @DisplayName("108935-RxCC Member Provider Tab - Filters - PostalCode")
    public List<DynamicNode> filterPostalCode() throws ElementNotFoundException 
    {        
        List<String> filterValueList = providerList.stream().map(provider -> provider.getDefaultOfficeLocation().getPostalCode()).collect(Collectors.toList());       
        logger.info("*********** filterValueList ****" + filterValueList);
        String filterValue = filterValueList.get(0);

        logger.info("*********** filterValue  ****" + filterValue );
        return runFilter(MemberProviderTabColumns.POSTAL_CODE, filterValue);

    }

    @TestFactory
    @Order(22)
    @DisplayName("108935-RxCC Member Provider Tab - Filters - # of Locations")
    public List<DynamicNode> filterNumberOfLocations() throws ElementNotFoundException 
    {        
        List<String> filterValueList = providerList.stream().map(provider -> provider.getDefaultOfficeLocation().getId()).collect(Collectors.toList());       
        logger.info("*********** filterValueList ****" + filterValueList);
        String filterValue = filterValueList.get(0);

        logger.info("*********** filterValue  ****" + filterValue );
        return runFilter(MemberProviderTabColumns.NUMBER_OF_LOCATIONS, filterValue);

    }

    @TestFactory
    @Order(23)
    @DisplayName("108935-RxCC Member Provider Tab - Filters -Taxonomy")
    public List<DynamicNode> filterTaxonomy() throws ElementNotFoundException 
    {        
        List<String> filterValueList = providerList.stream().map(provider -> provider.getTaxonomyDescr()).collect(Collectors.toList());       
        logger.info("*********** filterValueList ****" + filterValueList);
        String filterValue = filterValueList.get(0);

        logger.info("*********** filterValue  ****" + filterValue );
        return runFilter(MemberProviderTabColumns.TAXONOMY, filterValue);

    }

    @TestFactory
    @Order(24)
    @DisplayName("108935-RxCC Member Provider Tab - Filters -Status")
    public List<DynamicNode> filterStatus() throws ElementNotFoundException 
    {        
        List<String> filterValueList = providerList.stream().map(provider -> provider.getStatusInd()).collect(Collectors.toList());       
        logger.info("*********** filterValueList ****" + filterValueList);
        String filterValue = filterValueList.get(0);

        logger.info("*********** filterValue  ****" + filterValue );
        return runFilter(MemberProviderTabColumns.PROVIDER_STATUS, filterValue);

    }

    @TestFactory
    @Order(25)
    @DisplayName("108935-RxCC Member Provider Tab - Filters -Provider Verified")
    public List<DynamicNode> filterProviderVerified() throws ElementNotFoundException 
    {        
        List<Boolean> providerVerifiedList = providerList.stream().map(provider -> provider.getDefaultOfficeLocation().isFaxVerified()).collect(Collectors.toList());       
        logger.info("*********** filterValueList ****" + providerVerifiedList);

        return runFilter(MemberProviderTabColumns.PROVIDER_VERIFIED, "true");

    }

    @TestFactory
    @Order(26)
    @DisplayName("108935-RxCC Member Provider Tab - Filters -Provider Not Verified")
    public List<DynamicNode> filterProviderNotVerified() throws ElementNotFoundException 
    {        
        List<Boolean> providerVerifiedList = providerList.stream().map(provider -> provider.getDefaultOfficeLocation().isFaxVerified()).collect(Collectors.toList());       
        logger.info("*********** filterValueList ****" + providerVerifiedList);

        return runFilter(MemberProviderTabColumns.PROVIDER_VERIFIED, "false");

    }

    @TestFactory
    @Order(27)
    @DisplayName("3320: Provider  Search Forms")
    public List<DynamicNode> providerSearch() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        //  Cosmos
        String expectedNpi = providerList.get(0).getNpi();
        expectedProviderList = providerList.stream()
                .filter(providerList -> providerList.getNpi().contains(expectedNpi))
                .collect(Collectors.toList());

        //UI
        Provider.providerSearch(expectedNpi);
        SeleniumPageHelperAndWaiter.pause(3000); // to avoid stale element exception
        actualProviderList= Provider.retrieveProviderInfo();
        for ( Provider expected : expectedProviderList )
        { 
            boolean found = false; 
            for ( Provider actual : actualProviderList )
            {
                if ( expected.equals(actual) )
                {
                    tests.add(dynamicContainer("Provider NPI " + expected.getNpi(), expected.compareMemberProviderUI(actual)));
                    found = true;
                    break;
                }
            }

            if ( !found )
            {
                tests.add(dynamicTest("Provider NPI " + expected.getNpi() + " is missing from UI search result", 
                        () -> fail("Provider [" + expected.toString() + "] not found in UI search result")));
            }
        }
        return tests;
    }

}

