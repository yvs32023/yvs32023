/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.pharmacy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.PharmacyQueries;
import com.excellus.sqa.rxcc.dto.MainMenu;
import com.excellus.sqa.rxcc.dto.Pharmacy;
import com.excellus.sqa.rxcc.dto.pharmacy.PharmacyDirectoryColumns;
import com.excellus.sqa.rxcc.pages.home.MainMenuPO;
import com.excellus.sqa.rxcc.pages.pharmacy.PharmacyDirectoryPO;
import com.excellus.sqa.rxcc.steps.pharmacy.PharmacyColumnFilterStep;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.utilities.SortOrder;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.step.IStep.Status;

/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 03/17/2022
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_READ_MULTI", "RXCC_OPS_MULTI", 
            "RXCC_FULL_SINGLE", "RXCC_READ_SINGLE", "RXCC_OPS_SINGLE", "RXCC_REPORT_MULTI",
            "RXCC_FULL_LOA", "RXCC_READ_LOA", "RXCC_OPS_LOA", "RXCC_REPORT_LOA",
            "RXCC_FULL_MED", "RXCC_READ_MED", "RXCC_OPS_MED", "RXCC_REPORT_MED"})
@Tag("ALL")
@Tag("PHARMACY")
public class PharmacyDirectoryTest extends RxConciergeUITestBase{

    private static final Logger logger = LoggerFactory.getLogger(PharmacyDirectoryTest.class);
    static PageConfiguration pageConfiguration;
    static MainMenuPO mainMenuPO;
    static PharmacyDirectoryPO pharmacyPO;

    static List<Pharmacy> pharmacy;
    static String pharmacies;
    final static String searchTerm = "Davidson";

    static List<Pharmacy> expectedPharmacyDirctory;
    static List<Pharmacy> actualPharmacyDirectory;

    @BeforeAll
    public static void setupPageObject() throws ElementNotFoundException
    {
        logger.info("Setup PharmacyDirectoryTest");

        pageConfiguration = (PageConfiguration) BeanLoader.loadBean("memberPage");

        pharmacyPO = new PharmacyDirectoryPO(driverBase.getWebDriver(), pageConfiguration);
        mainMenuPO = new MainMenuPO(driverBase.getWebDriver(), pageConfiguration);

        pharmacyPO.clickPharmacyDirectory(); 
        SeleniumPageHelperAndWaiter.pause(5000);

        // Cosmos 
        expectedPharmacyDirctory = PharmacyQueries.search(searchTerm, null);

        //UI 
        pharmacyPO.pharmacyDirectorySearch(searchTerm.toUpperCase());
        SeleniumPageHelperAndWaiter.pause(2000);
        actualPharmacyDirectory = pharmacyPO.retrievePharmacyDirectoryInfo();
        SeleniumPageHelperAndWaiter.pause(2000);
    }

    /**
     * Refactorng
     * @author Neeru Tagore (ntagore)
     * @since 06/03/2022
     */ 

    /**
     * @since 06/23/22
     * @author Garrett Cosmiano (gcosmian)
     */
    @Test
    @Order(1)
    @DisplayName("6071 - RxCC Global Pharmacy Directory - Screen Verification (column headers)")
    public void validateColumnHeaders()
    {
        List<String> expected = PharmacyDirectoryColumns.getPharmacyDirectoryColumns();
        List<String> actual = pharmacyPO.retrieveColumnHeaders();

        assertThat(actual, contains(expected.toArray(new String[expected.size()])));
    }

    @TestFactory
    @Order(1)
    @DisplayName("3335-RxCC Pharmacy directory Search Type")
    public List<DynamicNode> pharmacyDirectorySearchType() throws ElementNotFoundException
    {
        // GC (06/23/22) corrected logic to validate each pharmacy

        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        for ( Pharmacy expected : expectedPharmacyDirctory )
        { 
            boolean found = false; 
            for ( Pharmacy actual : actualPharmacyDirectory )
            {
                if ( expected.equals(actual) )
                {
                    tests.add(dynamicContainer("Pharmacy NPI " + expected.getNpi(), expected.comparePharmacyDirectoryUI(actual)));
                    found = true;
                    break;
                }
            }

            if ( !found )
            {
                tests.add(dynamicTest("Pharmacy NPI " + expected.getNpi() + " is missing from UI search result", 
                        () -> fail("Pharmacy [" + expected.toString() + "] not found in UI search result")));
            }
        }
        return tests;
    } 

    @Test
    @Order(2)
    @DisplayName("3302-RxCC Pharmacy Directory - Sort NPI by ascending")
    public void sortNPIAsc() throws ElementNotFoundException
    {

        List<String> expected = expectedPharmacyDirctory.stream().map(directory -> directory.getNpi()).collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        pharmacyPO.sortColumn(PharmacyDirectoryColumns.NPI, SortOrder.ASCENDING);

        List<String> actual = pharmacyPO.retrieveColumnData(PharmacyDirectoryColumns.NPI);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(3)
    @DisplayName("3302-RxCC Pharmacy Directory - Sort Store Name by descending")
    public void sortStoreNameDesc() throws ElementNotFoundException
    {
        List<String> expected = expectedPharmacyDirctory.stream()
                .map(directory -> directory.getStoreName())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        pharmacyPO.sortColumn(PharmacyDirectoryColumns.PHARMACY_NAME, SortOrder.DESCENDING);
        List<String> actual = pharmacyPO.retrieveColumnData(PharmacyDirectoryColumns.PHARMACY_NAME);
        actual.replaceAll(data -> data.toUpperCase());

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(4)
    @DisplayName("3302-RxCC Pharmacy Directory - Sort Taxonomy Description by ascending")
    public void sortTaxonomyDescriptionAsc() throws ElementNotFoundException
    {

        List<String> expected = expectedPharmacyDirctory.stream()
                .map(directory -> directory.getTaxonomyDescr())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER);
        pharmacyPO.sortColumn(PharmacyDirectoryColumns.TAXONOMY_DESCRIPTION, SortOrder.ASCENDING);
        List<String> actual = pharmacyPO.retrieveColumnData(PharmacyDirectoryColumns.TAXONOMY_DESCRIPTION);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(5)
    @DisplayName("3302-RxCC Pharmacy Directory - Sort Phone Number by descending")
    public void sortPhoneNumberDesc() throws ElementNotFoundException
    {
        List<String> expected = expectedPharmacyDirctory.stream()
                .map(directory -> directory.getNormalizePhoneNumberUI())   
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        pharmacyPO.sortColumn(PharmacyDirectoryColumns.PHONE_NUMBER, SortOrder.DESCENDING);
        List<String> actual = pharmacyPO.retrieveColumnData(PharmacyDirectoryColumns.PHONE_NUMBER);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(6)
    @DisplayName("3302-RxCC Pharmacy Directory - Sort City by descending")
    public void sortCityDesc() throws ElementNotFoundException
    {
        List<String> expected = expectedPharmacyDirctory.stream()
                .map(directory -> directory.getCity())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        pharmacyPO.sortColumn(PharmacyDirectoryColumns.CITY, SortOrder.DESCENDING);
        List<String> actual = pharmacyPO.retrieveColumnData(PharmacyDirectoryColumns.CITY);
        actual.replaceAll(data -> data.toUpperCase());

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(7)
    @DisplayName("3302-RxCC Pharmacy Directory - Sort State by ascending")
    public void sortStateAsc() throws ElementNotFoundException
    {

        List<String> expected = expectedPharmacyDirctory.stream()
                .map(directory -> directory.getState())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER);
        pharmacyPO.sortColumn(PharmacyDirectoryColumns.STATE, SortOrder.ASCENDING);
        List<String> actual = pharmacyPO.retrieveColumnData(PharmacyDirectoryColumns.STATE);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(8)
    @DisplayName("3302-RxCC Pharmacy Directory - Sort status indicator by ascending")
    public void sortStatusIndicatorAsc() throws ElementNotFoundException
    {

        List<String> expected = expectedPharmacyDirctory.stream().map(directory -> directory.getStatusInd()).collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER);
        pharmacyPO.sortColumn(PharmacyDirectoryColumns.STATUS, SortOrder.ASCENDING);
        List<String> actual = pharmacyPO.retrieveColumnData(PharmacyDirectoryColumns.STATUS);

        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    } 

    @TestFactory
    @Order(9)
    @DisplayName("3148-Pharmacy Directory - Filters - NPI")
    public List<DynamicNode> filterNPI() throws ElementNotFoundException 
    {
        List<String> filterValueList = expectedPharmacyDirctory.stream().map(directory -> directory.getNpi()).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter(PharmacyDirectoryColumns.NPI, filterValue);

    }

    @TestFactory
    @Order(10)
    @DisplayName("3148-Pharmacy Directory - Filters - PharmacyName")
    public List<DynamicNode> filterPharmacyName() throws ElementNotFoundException 
    {
        List<String> filterValueList = expectedPharmacyDirctory.stream().map(directory -> directory.getStoreName()).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter(PharmacyDirectoryColumns.PHARMACY_NAME, filterValue);

    }

    @TestFactory
    @Order(11)
    @DisplayName("3148-Pharmacy Directory - Filters - Taxonomy Description")
    public List<DynamicNode> filterTaxonomyDescription() throws ElementNotFoundException 
    {

        List<String> filterValueList = expectedPharmacyDirctory.stream().map(directory -> directory.getTaxonomyDescr()).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter(PharmacyDirectoryColumns.TAXONOMY_DESCRIPTION, filterValue);

    }

    @TestFactory
    @Order(12)
    @DisplayName("3148-Pharmacy Directory - Filters - PhoneNumber")
    public List<DynamicNode> filterPhoneNumber() throws ElementNotFoundException 
    {
        List<String> filterValueList = expectedPharmacyDirctory.stream().map(directory -> directory.getPhoneNumber()).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        filterValue = filterValue.replaceAll("[^0-9]", "");
        return runFilter(PharmacyDirectoryColumns.PHONE_NUMBER, filterValue);

    }

    @TestFactory
    @Order(13)
    @DisplayName("3148-Pharmacy Directory - Filters - City")
    public List<DynamicNode> filterCity() throws ElementNotFoundException 
    {
        List<String> filterValueList = expectedPharmacyDirctory.stream().map(directory -> directory.getCity()).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter(PharmacyDirectoryColumns.CITY, filterValue);

    } 

    @TestFactory
    @Order(14)
    @DisplayName("3148-Pharmacy Directory - Filters - State")
    public List<DynamicNode> filterState() throws ElementNotFoundException 
    {
        List<String> filterValueList = expectedPharmacyDirctory.stream().map(directory -> directory.getState()).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter(PharmacyDirectoryColumns.STATE, filterValue);

    } 

    @TestFactory
    @Order(15)
    @DisplayName("3148-Pharmacy Directory - Filters - status")
    public List<DynamicNode> filterStatus() throws ElementNotFoundException 
    {
        List<String> filterValueList = expectedPharmacyDirctory.stream().map(directory -> directory.getStatusInd()).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter(PharmacyDirectoryColumns.STATUS, filterValue);

    }

    private List<DynamicNode> runFilter(PharmacyDirectoryColumns column, String filterValue)  //, String selection )
    {
        List<DynamicNode> test = new ArrayList<DynamicNode>();

        PharmacyColumnFilterStep step = new PharmacyColumnFilterStep(driverBase.getWebDriver(), column, filterValue);

        step.run();

        test.addAll(step.getTestResults());

        if ( step.stepStatus() != Status.COMPLETED ) {
            return test;
        }

        // GC (06/23/22) retrieve the expected column values base on the records retrieved from Cosmos
        List<String> expectedValues = new ArrayList<String>();
        for ( Pharmacy pharmacy : expectedPharmacyDirctory )
        {
            switch (column) {

            case NPI:
                if ( StringUtils.equalsIgnoreCase (pharmacy.getNpi(), filterValue) ) {
                    expectedValues.add(pharmacy.getNpi());
                }
                break;

            case PHARMACY_NAME:
                if ( StringUtils.equalsIgnoreCase(pharmacy.getStoreName(), filterValue) ) {
                    expectedValues.add(pharmacy.getStoreName());
                }
                break;

            case TAXONOMY_DESCRIPTION:
                if ( StringUtils.equalsIgnoreCase(pharmacy.getTaxonomyDescr(), filterValue) ) {
                    expectedValues.add(pharmacy.getTaxonomyDescr());
                }
                break;

            case PHONE_NUMBER:
                if ( StringUtils.equalsIgnoreCase(pharmacy.getPhoneNumber(), filterValue) ) {
                    expectedValues.add(pharmacy.getNormalizePhoneNumberUI());
                }
                break;

            case CITY:
                if ( StringUtils.equalsIgnoreCase(pharmacy.getCity(), filterValue) ) {
                    expectedValues.add(pharmacy.getCity());
                }
                break;

            case STATE:
                if ( StringUtils.equalsIgnoreCase(pharmacy.getState(), filterValue) ) {
                    expectedValues.add(pharmacy.getState());
                }
                break;

            case STATUS:
                if ( StringUtils.equalsIgnoreCase(pharmacy.getStatusInd(), filterValue) ) {
                    expectedValues.add(pharmacy.getStatusInd());
                }
                break;

            default:
                break;
            }
        }

        test.add(dynamicTest("Record count display [" + expectedValues.size() + "]", () -> assertEquals(expectedValues.size() + " Records Found", step.getLabelRowCount())));
        test.add(dynamicTest("Pharmacy record rows [" + expectedValues.size() + "]", () -> assertEquals(expectedValues.size(), step.getFilterRowCount())));
        test.add(dynamicTest("Column value validation", () -> assertThat(step.getColumnValues(), containsInAnyOrder(expectedValues.toArray(new String[expectedValues.size()]))) ));

        return test;
    }

    //Test case to validate Pharmacy Directory icon at user level
    //@ntagore (11/04/22)
    @Test
    @Order(16)
    @DisplayName("User Level- Negative & Positive testing to validate pharmacy Directory icon")
    public void UserRoleMenuTest() throws ElementNotFoundException
    {

        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")||StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_SINGLE") ) {
            boolean displayedPharmacy = mainMenuPO.iconValidation(MainMenu.PHARMACY_DIRECTORY);
            assertTrue(displayedPharmacy);

        }
        else if ( StringUtils.equalsIgnoreCase(acctName, "RXCC_REPORT_MULTI") ) {
            boolean displayedPharmacy = mainMenuPO.iconValidation(MainMenu.PHARMACY_DIRECTORY);
            assertFalse(displayedPharmacy);

        }
    }

}



