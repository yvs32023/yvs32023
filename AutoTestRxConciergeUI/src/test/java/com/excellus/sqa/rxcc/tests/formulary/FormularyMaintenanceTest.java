package com.excellus.sqa.rxcc.tests.formulary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.FormularyQueries;
import com.excellus.sqa.rxcc.dto.Formulary;
import com.excellus.sqa.rxcc.dto.formulary.FormularyColumns;
import com.excellus.sqa.rxcc.pages.formulary.FormularyMaintenancePO;
import com.excellus.sqa.rxcc.pages.home.MainMenuPO;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 11/09/2022
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_FULL_SINGLE","RXCC_FULL_LOA","RXCC_FULL_MED"})
@Tag("ALL")
@Tag("FORMULARY")
public class FormularyMaintenanceTest extends RxConciergeUITestBase{

    private static final Logger logger = LoggerFactory.getLogger(FormularyMaintenanceTest.class);

    static PageConfiguration pageConfiguration;
    static MainMenuPO mainMenuPO;
    static FormularyMaintenancePO formularyPO;

    static List<Formulary> formulary;
    static String formularies;
    final static String searchTerm = "Group";

    static String formularyCode;
    static List<Formulary> expectedformularies;
    static List<Formulary> actualformularies;


    private static String uid;
    private static String code;

    @BeforeAll
    public static void setupPageObject() throws ElementNotFoundException
    {
        logger.info("Setup FormularyTest");

        pageConfiguration = (PageConfiguration) BeanLoader.loadBean("memberPage");

        formularyPO = new FormularyMaintenancePO(driverBase.getWebDriver(), pageConfiguration);

        formularyPO.clickFormularyMaintenanceIcon(); 
        SeleniumPageHelperAndWaiter.pause(5000);
        // Cosmos      

        expectedformularies = FormularyQueries.search(searchTerm);       
        //UI 
        formularyPO.formularySearch(searchTerm.toUpperCase());
        SeleniumPageHelperAndWaiter.pause(2000);
        actualformularies = formularyPO.retrieveFormularyInfo();
        SeleniumPageHelperAndWaiter.pause(2000);
    }

    @Test
    @Order(1)
    @DisplayName("18384 -Formulary Maintenance View - Screen Verification (column headers)")
    public void validateColumnHeaders()
    {
        List<String> expected = FormularyColumns.getFormularyColumns();
        List<String> actual = formularyPO.retrieveColumnHeaders();

        assertThat(actual, contains(expected.toArray(new String[expected.size()])));
    }

    @TestFactory
    @Order(2)
    @DisplayName("18970-Formulary Maintenance Search")
    public List<DynamicNode> formularySearch() throws ElementNotFoundException
    {

        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        for ( Formulary expected : expectedformularies )
        { 
            boolean found = false; 
            for ( Formulary actual : actualformularies )
            {
                if ( expected.getFormularyCode().equals(actual.getFormularyCode()) )
                {
                    tests.add(dynamicContainer("Formulary Code " + expected.getFormularyCode(), expected.compareUI(actual)));
                    found = true;
                    break;
                }
            }

            if ( !found )
            {
                tests.add(dynamicTest("Formulary Code" + expected.getFormularyCode() + " is missing from UI search result", 
                        () -> fail("Formulary [" + expected.toString() + "] not found in UI search result")));
            }
        }
        return tests;
    } 

    @TestFactory
    @Order(3)
    @DisplayName("19996-Formulary Maintenance Edit")
  //  @Disabled ("Defect 109966- Unable to edit formulary")
    public List<DynamicNode> formularyEdit() throws ElementNotFoundException
    {

        List<DynamicNode> tests = new ArrayList<DynamicNode>();


        /* edit the first row formulry description
         * retrieve the current description and update
         * 
         */
        String currentDescription = formularyPO.retrieveFormularyDescription();
        formularyPO.updateFormularyDescription(currentDescription);

        SeleniumPageHelperAndWaiter.pause(5000);

        expectedformularies = FormularyQueries.search(searchTerm);
        //UI 
        formularyPO.formularySearch(searchTerm.toUpperCase());
        SeleniumPageHelperAndWaiter.pause(2000);
        actualformularies = formularyPO.retrieveFormularyInfo();
        SeleniumPageHelperAndWaiter.pause(2000);


        for ( Formulary expected : expectedformularies )
        { 
            boolean found = false; 
            for ( Formulary actual : actualformularies )
            {
                if ( expected.getFormularyCode().equals(actual.getFormularyCode()) )
                {
                    tests.add(dynamicContainer("Formulary code " + expected.getFormularyCode(), expected.compareUI(actual)));
                    found = true;
                    break;
                }
            }

            if ( !found )
            {
                tests.add(dynamicTest("Formulary code " + expected.getFormularyCode() + " is missing from UI search result", 
                        () -> fail("Formulary [" + expected.toString() + "] not found in UI search result")));
            }
        }
        // revert the updates
        formularyPO.revertFormularyDescription(currentDescription);
        return tests;
    } 

    @TestFactory
    @Order(4)
    @DisplayName("19414-Formulary Maintenance Add")
    public List<DynamicNode> addFormularyCode() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        formularyPO.clickAddFormulary();
        formularyPO.enterFormularyDescription("Group Testing");
        formularyCode = formularyPO.getFormularyCode();
        formularyPO.buttonClickAdd();
        SeleniumPageHelperAndWaiter.pause(5000);

        expectedformularies = FormularyQueries.search(formularyCode);
        SeleniumPageHelperAndWaiter.pause(1000);

        actualformularies = formularyPO.retrieveFormularyInfo();
     
        uid = expectedformularies.get(0).getId();
        code = expectedformularies.get(0).getFormularyCode();

        for ( Formulary expected : expectedformularies )
        { 
            boolean found = false; 
            for ( Formulary actual : actualformularies )
            {
                if ( expected.getFormularyCode().equals(actual.getFormularyCode()) )
                {
                    tests.add(dynamicContainer("Id " + expected.getId(), expected.compareUI(actual)));
                    found = true;
                    
                    break;
                }
            }

            if ( !found )
            {
                tests.add(dynamicTest("Id " + expected.getId() + " is missing from UI search result", 
                        () -> fail("Formulary [" + expected.toString() + "] not found in UI search result")));
            }
        }
        return tests;
    }
    
    /*   Deleting the added formulary after test is completed.
     * 
     */
  
    @AfterAll
    public static void cleanUp()
    {
        FormularyQueries.deleteFormulary(uid, code);
    }
}





