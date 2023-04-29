/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.provider;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.azure.cosmos.implementation.apachecommons.lang.StringUtils;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.ProviderQueries;
import com.excellus.sqa.rxcc.dto.MainMenu;
import com.excellus.sqa.rxcc.dto.Provider;
import com.excellus.sqa.rxcc.dto.provider.ProviderDirectoryColumns;
import com.excellus.sqa.rxcc.pages.home.MainMenuPO;
import com.excellus.sqa.rxcc.pages.provider.ProviderDirectoryPO;
import com.excellus.sqa.rxcc.steps.provider.ProviderDirectoryColumnFilterStep;
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
 * @since 03/31/2022
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_READ_MULTI", "RXCC_OPS_MULTI", 
        "RXCC_FULL_SINGLE", "RXCC_READ_SINGLE", "RXCC_OPS_SINGLE", "RXCC_REPORT_MULTI",
        "RXCC_FULL_LOA", "RXCC_READ_LOA", "RXCC_OPS_LOA", "RXCC_REPORT_LOA",
        "RXCC_FULL_MED", "RXCC_READ_MED", "RXCC_OPS_MED", "RXCC_REPORT_MED"})
@Tag("ALL")
@Tag("PROVIDER")
public class ProviderDirectoryTest extends RxConciergeUITestBase{

    private static final Logger logger = LoggerFactory.getLogger(ProviderDirectoryTest.class);

    static PageConfiguration pageConfiguration;
    static MainMenuPO mainMenuPO;
    static ProviderDirectoryPO providerPO;

    static List<Provider> provider;

    static List<Provider> expectedProviderDirctory;
    static List<Provider> actualProviderDirectory;
  

    @BeforeAll
    public static void setupPageObject() throws ElementNotFoundException
    {
        pageConfiguration = (PageConfiguration) BeanLoader.loadBean("memberPage");

        providerPO = new ProviderDirectoryPO(driverBase.getWebDriver(), pageConfiguration);

        mainMenuPO = new MainMenuPO(driverBase.getWebDriver(), pageConfiguration);

        providerPO.clickProviderDirectory();

        SeleniumPageHelperAndWaiter.pause(5000);
        String searchTerm = "Edison"; 

        Map<String, List<String>> filters = new HashMap<>();
        filters.put("city", Arrays.asList("Edison"));

        expectedProviderDirctory = ProviderQueries.providerSearch(searchTerm);
        logger.info("***Search result expected value**** " + expectedProviderDirctory);
        
        //UI 
        providerPO.providerDirectorySearch(searchTerm.toUpperCase());
        SeleniumPageHelperAndWaiter.pause(2000);
        actualProviderDirectory = providerPO.retrieveProviderDirectoryInfo();

        logger.info("***Search result actual value**** " + actualProviderDirectory);

        SeleniumPageHelperAndWaiter.pause(2000);
    }

    /**
     * Refactorng
     * @author Neeru Tagore (ntagore)
     * @since 07/05/2022
     */ 


    @Test
    @Order(1)
    @DisplayName("3870 - RxCC Provider Directory - Screen Verification (column headers)")
    public void validateColumnHeaders()
    {
        List<String> expected = ProviderDirectoryColumns.getProviderDirectoryColumns();
        List<String> actual = providerPO.retrieveColumnHeaders();

        assertThat(actual, contains(expected.toArray(new String[expected.size()])));
    }

    @TestFactory
    @Order(2)
    @DisplayName("4965 - RxCC Provider Directory Search")
    public List<DynamicNode> providerDirectorySearchType()
    {

        List<DynamicNode> tests = new ArrayList<>();
        for ( Provider expected : expectedProviderDirctory )
        { 
            boolean found = false; 
            for ( Provider actual : actualProviderDirectory )
            {
                if ( expected.equals(actual) )
                {
                    tests.add(dynamicContainer("Provider NPI " + expected.getNpi(), expected.compareUI(actual)));
                    found = true;
                    break;
                }
            }

            if ( !found )
            {
                tests.add(dynamicTest("Provider NPI " + expected.getNpi() + " is missing from UI search result", 
                        () -> fail("Provider [" + expected + "] not found in UI search result")));
            }
        }
        return tests;
    } 

    @Test
    @Order(3)
    @DisplayName("4967-RxCC Provider Directory sorting-NPI")
    public void sortNPIDes() throws ElementNotFoundException
    {

        List<String> expected = expectedProviderDirctory.stream().map(directory -> directory.getNpi()).collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        providerPO.sortColumn(ProviderDirectoryColumns.NPI, SortOrder.DESCENDING);

        List<String> actual = providerPO.retrieveColumnData(ProviderDirectoryColumns.NPI);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(4)
    @DisplayName("4967-RxCC Provider Directory sorting-LastName")
    public void sortLastNameAsc() throws ElementNotFoundException
    {

        List<String> expected = expectedProviderDirctory.stream().map(directory -> directory.getLastName()).collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        providerPO.sortColumn(ProviderDirectoryColumns.LAST_NAME, SortOrder.ASCENDING);

        List<String> actual = providerPO.retrieveColumnData(ProviderDirectoryColumns.LAST_NAME);
        actual.replaceAll(data -> data.toUpperCase());
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @TestFactory
    @Order(5)
    @DisplayName("3101-Provider Directory - Filters - NPI")
    public List<DynamicNode> filterNPI()
    {
        List<String> filterValueList = expectedProviderDirctory.stream().map(directory -> directory.getNpi()).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter(ProviderDirectoryColumns.NPI, filterValue);
    }
    
    @TestFactory
    @Order(6)
    @DisplayName("90585-Provider Directory - Filters - FirstName")
    public List<DynamicNode> filterFirstName()
    {
        List<String> filterValueList = expectedProviderDirctory.stream().map(directory -> directory.getFirstName()).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter(ProviderDirectoryColumns.FIRST_NAME, filterValue);

    }
  @TestFactory
  @Order(7)
  @DisplayName("3101-Provider Directory - Filters - LastName")
  public List<DynamicNode> filterLastName()
  {
      List<String> filterValueList = expectedProviderDirctory.stream().map(directory -> directory.getLastName()).collect(Collectors.toList());
      String filterValue = filterValueList.get(0); 
      return runFilter(ProviderDirectoryColumns.LAST_NAME, filterValue);

  }
    
    @TestFactory
    @Order(8)
    @DisplayName("90585-Provider Directory - Filters - TaxanomyDescription")
    public List<DynamicNode> filterTaxonomyDescription()
    {
        List<String> filterValueList = expectedProviderDirctory.stream().map(directory -> directory.getTaxonomyDescr()).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter(ProviderDirectoryColumns.TAXONOMY_DESCRIPTION, filterValue);

    }
       
    @TestFactory
    @Order(9)
    @DisplayName("90585-Provider Directory - Filters - Phone Number")
    public List<DynamicNode> filterPhoneNumber()
    {
        List<String> filterValueList = expectedProviderDirctory.stream().map(provider -> provider.getDefaultOfficeLocation().getPhoneNumber()).collect(Collectors.toList());       
        String filterValue = filterValueList.get(0); 
        return runFilter(ProviderDirectoryColumns.PHONE_NUMBER, filterValue);

    }
    
    @TestFactory
    @Order(10)
    @DisplayName("90585-Provider Directory - Filters - Fax Number")
    public List<DynamicNode> filterFaxNumber()
    {
        List<String> filterValueList = expectedProviderDirctory.stream().map(provider -> provider.getDefaultOfficeLocation().getFaxNumber()).collect(Collectors.toList());       
        String filterValue = filterValueList.get(0); 
        return runFilter(ProviderDirectoryColumns.FAX_NUMBER, filterValue);

    }
    
    @TestFactory
    @Order(11)
    @DisplayName("90585-Provider Directory - Filters - City")
    public List<DynamicNode> filterCity()
    {
        List<String> filterValueList = expectedProviderDirctory.stream().map(provider -> provider.getDefaultOfficeLocation().getCity()).collect(Collectors.toList());       
        String filterValue = filterValueList.get(0); 
        return runFilter(ProviderDirectoryColumns.CITY, filterValue);

    }
    
    @TestFactory
    @Order(12)
    @DisplayName("90585-Provider Directory - Filters - State")
    public List<DynamicNode> filterState()
    {
        List<String> filterValueList = expectedProviderDirctory.stream().map(provider -> provider.getDefaultOfficeLocation().getState()).collect(Collectors.toList());       
        String filterValue = filterValueList.get(0); 
        return runFilter(ProviderDirectoryColumns.STATE, filterValue);

    }
    
    @TestFactory
    @Order(13)
    @DisplayName("90585-Provider Directory - Filters - Number of Locations")
    public List<DynamicNode> filterNumberOfLocations()
    {
        List<String> filterValueList = expectedProviderDirctory.stream().map(provider -> provider.getDefaultOfficeLocation().getId()).collect(Collectors.toList());       
        String filterValue = filterValueList.get(0); 
        return runFilter(ProviderDirectoryColumns.NUMBER_OF_LOCATIONS, filterValue);

    }
    
    @TestFactory
    @Order(14)
    @DisplayName("90585-Provider Directory - Filters - Status")
    public List<DynamicNode> filterStatus()
    {
        List<String> filterValueList = expectedProviderDirctory.stream().map(directory -> directory.getStatusInd()).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter(ProviderDirectoryColumns.STATUS, filterValue);

    }
    
    @TestFactory
    @Order(15)
    @DisplayName("90585-Provider Directory - Filters - Fax Verified")
    public List<DynamicNode> faxVerified()
    {
        List<Boolean> faxVerifiedList = expectedProviderDirctory.stream().map(provider -> provider.getDefaultOfficeLocation().isFaxVerified()).collect(Collectors.toList());
        return runFilter(ProviderDirectoryColumns.FAX_VERIFIED, "true");

    }
    
    @TestFactory
    @Order(16)
    @DisplayName("90585-Provider Directory - Filters - Fax Not Verified")
    public List<DynamicNode> faxNotVerified()
    {
        List<Boolean> faxVerifiedList = expectedProviderDirctory.stream().map(provider -> provider.getDefaultOfficeLocation().isFaxVerified()).collect(Collectors.toList());
        return runFilter(ProviderDirectoryColumns.FAX_VERIFIED, "false");

    }

    private List<DynamicNode> runFilter(ProviderDirectoryColumns column, String filterValue)  //, String selection )
    {
        ProviderDirectoryColumnFilterStep step = new ProviderDirectoryColumnFilterStep(driverBase.getWebDriver(), column, filterValue);

        step.run();

        List<DynamicNode> test = new ArrayList<>(step.getTestResults());

        if ( step.stepStatus() != Status.COMPLETED ) {
            return test;
        }

        List<String> expectedValues = new ArrayList<>();
        for ( Provider provider : expectedProviderDirctory )
        {
            switch (column) {

            case NPI:
                if ( StringUtils.equalsIgnoreCase (provider.getNpi(), filterValue) ) {
                    expectedValues.add(provider.getNpi());
                }
                break;

            case FIRST_NAME:
                if ( StringUtils.equalsIgnoreCase(provider.getFirstName(), filterValue) ) {
                    expectedValues.add(provider.getFirstName());
                }
                break;

            case LAST_NAME:
                if ( StringUtils.equalsIgnoreCase(provider.getLastName(), filterValue) ) {
                    expectedValues.add(provider.getLastName());
                }
                break;

            case TAXONOMY_DESCRIPTION:
                if ( StringUtils.equalsIgnoreCase(provider.getTaxonomyDescr(), filterValue) ) {
                    expectedValues.add(provider.getTaxonomyDescr());
                }
                break; 

            case PHONE_NUMBER:
                if ( StringUtils.equalsIgnoreCase(provider.getDefaultOfficeLocation().getPhoneNumber(), filterValue) ) {
                    expectedValues.add(providerPO.getNormalizeNumber(provider.getDefaultOfficeLocation().getPhoneNumber()));
                }
                break;
                
            case FAX_NUMBER:
                if ( StringUtils.equalsIgnoreCase(provider.getDefaultOfficeLocation().getFaxNumber(), filterValue) ) {
                    expectedValues.add(providerPO.getNormalizeNumber(provider.getDefaultOfficeLocation().getFaxNumber()));
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
                
            case STATUS:
                if ( StringUtils.equalsIgnoreCase(provider.getStatusInd(), filterValue) ) {
                    expectedValues.add(provider.getStatusInd());
                }
                break;
            case FAX_VERIFIED:
                if ( StringUtils.equalsIgnoreCase(provider.getDefaultOfficeLocation().isFaxVerified().toString(), filterValue) ) {
                    expectedValues.add(provider.getDefaultOfficeLocation().isFaxVerified().toString());
                }
                break;
                            
            default:
                break;
            }
        }
        test.add(dynamicTest("Record count display [" + expectedValues.size() + "]", () -> assertEquals(expectedValues.size() + " Records Found", step.getLabelRowCount())));
        test.add(dynamicTest("Provider record rows [" + expectedValues.size() + "]", () -> assertEquals(expectedValues.size(), step.getFilterRowCount())));
        test.add(dynamicTest("Column value validation", () -> assertThat(step.getColumnValues(), containsInAnyOrder(expectedValues.toArray(new String[expectedValues.size()]))) ));

        return test;
    }

    //Test case to validate Pharmacy Directory icon at user level
    //@ntagore (11/09/22)
    @Test
    @Order(17)
    @DisplayName("User Level- Negative & Positive testing to validate provider Directory icon")
    public void UserRoleMenuTest() throws ElementNotFoundException
    {

        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")||StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_SINGLE") ) {
            boolean displayedProvider = mainMenuPO.iconValidation(MainMenu.PROVIDER_DIRECTORY);
            assertTrue(displayedProvider);

        }
        else if ( StringUtils.equalsIgnoreCase(acctName, "RXCC_REPORT_MULTI") ) {
            boolean displayedProvider = mainMenuPO.iconValidation(MainMenu.PROVIDER_DIRECTORY);
            assertFalse(displayedProvider);

        }
    }

}

