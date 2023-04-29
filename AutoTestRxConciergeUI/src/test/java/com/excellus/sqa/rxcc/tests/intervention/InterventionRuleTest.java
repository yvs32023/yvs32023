/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
/**
 * 
 */
package com.excellus.sqa.rxcc.tests.intervention;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.excellus.sqa.rxcc.Utility;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.InterventionRuleQueries;
import com.excellus.sqa.rxcc.dto.InterventionRule;
import com.excellus.sqa.rxcc.dto.interventionrules.InterventionRulesColumns;
import com.excellus.sqa.rxcc.pages.home.MainMenuPO;
import com.excellus.sqa.rxcc.pages.interventionrule.InterventionRuleLibraryPO;
import com.excellus.sqa.rxcc.steps.interventionrules.InterventionRulesColumnFilterStep;
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
 * @since 09/01/2022
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_FULL_SINGLE"})
@Tag("ALL")
@Tag("INTERVENTION")
@Tag("INTERVENTION-RULE")
public class InterventionRuleTest extends RxConciergeUITestBase{

    private static final Logger logger = LoggerFactory.getLogger(InterventionRuleTest.class);

    static PageConfiguration pageConfiguration;
    static MainMenuPO mainMenuPO;
    static InterventionRuleLibraryPO interventionRulePO;

    static List<InterventionRule> interventionRule;

    static List<InterventionRule> expectedInterventionRule;
    static List<InterventionRule> actualInterventionRule;
    final String INTERVENTION_RULE_NAME = "";
    @BeforeAll
    public static void setupPageObject() throws ElementNotFoundException
    {
        pageConfiguration = (PageConfiguration) BeanLoader.loadBean("memberPage");

        interventionRulePO = new InterventionRuleLibraryPO(driverBase.getWebDriver(), pageConfiguration);

        interventionRulePO.clickRuleLibrary();
        String searchTerm = "Standard Womens Biomultiple Oral Tablet Brand Multiple Vitamins w/ Minerals Tablet Test Text for formName";
        SeleniumPageHelperAndWaiter.pause(5000);

        String whereClauseExtension ="where c.ruleName =\"Standard Womens Biomultiple Oral Tablet Brand Multiple Vitamins w/ Minerals Tablet Test Text for formName\"";

        expectedInterventionRule = InterventionRuleQueries.getInterventionRulesUI(whereClauseExtension); // list of  the record based on the where clause

        Map<String, List<String>> filters = new HashMap<String, List<String>>();
        filters.put("Rule Name", Arrays.asList("Standard Womens Biomultiple Oral Tablet Brand Multiple Vitamins w/ Minerals Tablet Test Text for formName"));
        logger.info("***Search result expected value**** " + expectedInterventionRule);

        //UI 
        interventionRulePO.RuleLibrarySearch(searchTerm.toUpperCase());
        SeleniumPageHelperAndWaiter.pause(2000);
        actualInterventionRule = interventionRulePO.retrieveRuleLibraryInfo();

        logger.info("***Search result actual value**** " + actualInterventionRule);

        SeleniumPageHelperAndWaiter.pause(2000);
    }

    @Test
    @Order(1)
    @DisplayName("9263 - Intervention Rule Library - Screen Verification (column headers)")
    public void validateColumnHeaders()
    {
        List<String> expected = InterventionRulesColumns.getInterventionRulesColumns();
        List<String> actual = interventionRulePO.retrieveColumnHeaders();

        assertThat(actual, contains(expected.toArray(new String[expected.size()])));
    }

    @Test
    @Order(2)
    @DisplayName("9261: RxCC Interventions Rule Library - Search Forms ")
    public void interventionRuleSearchType() throws ElementNotFoundException
    {

        for ( InterventionRule expected : expectedInterventionRule )

        { 
            for ( InterventionRule actual : actualInterventionRule )
            {

                String test= expected.getRuleName();
                String test2= actual.getRuleName();
                assertEquals(test,test2);

            }
        }
    } 

    @Test
    @Order(3)
    @DisplayName("15886: RxCC Interventions Form Library Sort Rule Name ")
    public void sortRuleNameDes() throws ElementNotFoundException
    {

        List<String> expected = expectedInterventionRule.stream().map(directory -> directory.getRuleName()).collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        interventionRulePO.sortColumn(InterventionRulesColumns.RULE_NAME, SortOrder.DESCENDING);

        List<String> actual = interventionRulePO.retrieveColumnData(InterventionRulesColumns.RULE_NAME);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(4)
    @DisplayName("15886: RxCC Interventions Form Library Sort Create Date ")
    public void sortCreateDateAsc() throws ElementNotFoundException
    {

        List<String> expected = expectedInterventionRule.stream().map(directory -> Utility.convertCosmosDateToUI(directory.getCreatedDateTime(),"MM/dd/yyyy")).collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        interventionRulePO.sortColumn(InterventionRulesColumns.CREATE_DATE, SortOrder.ASCENDING);

        List<String> actual = interventionRulePO.retrieveColumnData(InterventionRulesColumns.CREATE_DATE);
        actual.replaceAll(data -> data.toUpperCase());
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }  

    @TestFactory
    @Order(6)
    @DisplayName("9262: RxCC Interventions Rules Library Filter ")
    public List<DynamicNode> filterRuleName() throws ElementNotFoundException 
    {
        List<String> filterValueList = expectedInterventionRule.stream().map(directory -> directory.getRuleName()).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter(InterventionRulesColumns.RULE_NAME, filterValue);

    }

    private List<DynamicNode> runFilter(InterventionRulesColumns column, String filterValue)  //, String selection )
    {
        List<DynamicNode> test = new ArrayList<DynamicNode>();

        InterventionRulesColumnFilterStep step = new InterventionRulesColumnFilterStep(driverBase.getWebDriver(), column, filterValue);

        step.run();

        test.addAll(step.getTestResults());

        if ( step.stepStatus() != Status.COMPLETED ) {
            return test;
        }

        List<String> expectedValues = new ArrayList<String>();
        for ( InterventionRule interventionRule : expectedInterventionRule )
        {
            switch (column) {

            case RULE_NAME:
                if ( StringUtils.equalsIgnoreCase (interventionRule.getRuleName(), filterValue) ) {
                    expectedValues.add(interventionRule.getRuleName());
                    expectedValues.replaceAll(data -> data.toUpperCase());
                }
            default:
                break;
            }
        }

        test.add(dynamicTest("Record count display [" + expectedValues.size() + "]", () -> assertEquals(expectedValues.size() + " Records Found", step.getLabelRowCount())));
        test.add(dynamicTest("Record rows [" + expectedValues.size() + "]", () -> assertEquals(expectedValues.size(), step.getFilterRowCount())));
        test.add(dynamicTest("Column value validation", () -> assertEquals(step.getColumnValues(), expectedValues)));

        return test;
    }
    
     /**
     * This test case will validate the negative search of intervention Rule
     * @author ntagore 02/27/23
     */
    @TestFactory
    @Test
    @Order(7)
    @DisplayName("RxCC Interventions Rules Library Negative Search")
    public List<DynamicNode> interventionRuleNegativeSearchType() throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        
        // Cosmos        
        
        String searchValue = "JaneDoe";
        List<InterventionRule> expected = expectedInterventionRule.stream()
                .filter(directory -> directory.getRuleName().toLowerCase().contains(searchValue.toLowerCase()))
                .collect(Collectors.toList());
        System.out.println("*****expected size********"+ expected.size());
        
            
        // UI 
        interventionRulePO.RuleLibrarySearch(searchValue);
        int rowCount = interventionRulePO.getFilterResultRowCount();
        SeleniumPageHelperAndWaiter.pause(1000);
        System.out.println("*****actual size********"+ rowCount);
        SeleniumPageHelperAndWaiter.pause(1000);
        String actualLabelRowCount = interventionRulePO.labelRowCount();
        String actualNoInterventionRuleFound = interventionRulePO.labelNoInterventionFound();
        
        if (rowCount==1 && !actualNoInterventionRuleFound.isBlank() && expected.size()==0) {
            
                  tests.add(dynamicTest("Row count validation", () -> assertEquals("0 Records", actualLabelRowCount)));
                  tests.add(dynamicTest("No intervention Rule found", () -> assertEquals("No intervention rules found", actualNoInterventionRuleFound)));
        }

        return tests;
    } 
    

}