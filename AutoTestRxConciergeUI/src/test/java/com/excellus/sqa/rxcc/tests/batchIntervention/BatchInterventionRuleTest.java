/**
 * 
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 * 
 */
/**
 * 
 */
package com.excellus.sqa.rxcc.tests.batchIntervention;

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
import com.excellus.sqa.rxcc.pages.batchinterventionrule.BatchInterventionRuleLibraryPO;
import com.excellus.sqa.rxcc.pages.home.MainMenuPO;
import com.excellus.sqa.rxcc.steps.batchinterventionrules.BatchInterventionRulesColumnFilterStep;
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
 * @since 03/20/2023
 */
/**
 * @author hzia
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_FULL_SINGLE"})
@Tag("ALL")
@Tag("BATCH-INTERVENTION")
@Tag("BATCH-INTERVENTION-RULE")
public class BatchInterventionRuleTest extends RxConciergeUITestBase{

	  private static final Logger logger = LoggerFactory.getLogger(BatchInterventionRuleTest.class);

	    static PageConfiguration pageConfiguration;
	    static MainMenuPO mainMenuPO;
	    static BatchInterventionRuleLibraryPO batchInterventionRulePO;

	    static List<InterventionRule> interventionRule;

	    static List<InterventionRule> expectedInterventionRule;
	    static List<InterventionRule> actualInterventionRule;
	    final String INTERVENTION_RULE_NAME = "BatchIntervention";
	    @BeforeAll
	    public static void setupPageObject() throws ElementNotFoundException
	    {
	        pageConfiguration = (PageConfiguration) BeanLoader.loadBean("memberPage");

	        batchInterventionRulePO = new BatchInterventionRuleLibraryPO(driverBase.getWebDriver(), pageConfiguration);

	        batchInterventionRulePO.clickRuleLibrary();
	        String searchTerm = "BatchIntervention";
	        SeleniumPageHelperAndWaiter.pause(5000);

	        String whereClauseExtension ="where c.ruleId =\"4536689e-2bb9-4da1-b582-b8b4d918d71e\"";

	        expectedInterventionRule = InterventionRuleQueries.getInterventionRulesUI(whereClauseExtension); // list of  the record based on the where clause

	        Map<String, List<String>> filters = new HashMap<String, List<String>>();
	        filters.put("Rule Name", Arrays.asList("BatchIntervention"));
	        logger.info("***Search result expected value**** " + expectedInterventionRule);

	        //UI 
	        batchInterventionRulePO.RuleLibrarySearch(searchTerm.toUpperCase());
	        SeleniumPageHelperAndWaiter.pause(2000);
	        actualInterventionRule = batchInterventionRulePO.retrieveRuleLibraryInfo();

	        logger.info("***Search result actual value**** " + actualInterventionRule);

	        SeleniumPageHelperAndWaiter.pause(2000);
	    }

	    @Test
	    @Order(1)
	    @DisplayName("9263 -Batch Intervention Rule Library - Screen Verification (column headers)")
	    public void validateColumnHeaders()
	    {
	        List<String> expected = InterventionRulesColumns.getInterventionRulesColumns();
	        List<String> actual = batchInterventionRulePO.retrieveColumnHeaders();

	        assertThat(actual, contains(expected.toArray(new String[expected.size()])));
	    }

	    @Test
	    @Order(2)
	    @DisplayName("3740: RxCC Batch Interventions Rule Library - Search Forms ")
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
	    @DisplayName("3719: RxCC Batch Interventions Form Library Sort Rule Name ")
	    public void sortRuleNameDes() throws ElementNotFoundException
	    {

	        List<String> expected = expectedInterventionRule.stream().map(directory -> directory.getRuleName()).collect(Collectors.toList());
	        expected.sort(String.CASE_INSENSITIVE_ORDER);
	        batchInterventionRulePO.sortColumn(InterventionRulesColumns.RULE_NAME, SortOrder.DESCENDING);

	        List<String> actual = batchInterventionRulePO.retrieveColumnData(InterventionRulesColumns.RULE_NAME);
	        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
	    }

	    @Test
	    @Order(4)
	    @DisplayName("3719: RxCC Batch Interventions Form Library Sort Create Date ")
	    public void sortCreateDateAsc() throws ElementNotFoundException
	    {

	        List<String> expected = expectedInterventionRule.stream().map(directory -> Utility.convertCosmosDateToUI(directory.getCreatedDateTime(),"MM/dd/yyyy")).collect(Collectors.toList());
	        expected.sort(String.CASE_INSENSITIVE_ORDER);
	        batchInterventionRulePO.sortColumn(InterventionRulesColumns.CREATE_DATE, SortOrder.ASCENDING);

	        List<String> actual = batchInterventionRulePO.retrieveColumnData(InterventionRulesColumns.CREATE_DATE);
	        actual.replaceAll(data -> data.toUpperCase());
	        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
	    }  
	    
	    @Test
	    @Order(5)
	    @DisplayName("3719: RxCC Batch Interventions Form Library Sort Modify Date")
	    public void sortModifyDateAsc() throws ElementNotFoundException
	    {

	        List<String> expected = expectedInterventionRule.stream().map(directory -> Utility.convertCosmosDateToUI(directory.getLastUpdatedDateTime(),"MM/dd/yyyy")).collect(Collectors.toList());
	        expected.sort(String.CASE_INSENSITIVE_ORDER);
	        batchInterventionRulePO.sortColumn(InterventionRulesColumns.MODIFY_DATE, SortOrder.ASCENDING);

	        List<String> actual = batchInterventionRulePO.retrieveColumnData(InterventionRulesColumns.MODIFY_DATE);
	        actual.replaceAll(data -> data.toUpperCase());
	        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
	    }  
	    
	    @Test
	    @Order(6)
	    @DisplayName("3719: RxCC Batch Interventions Form Library Sort Rule Status ")
	    public void sortRuleStatusDes() throws ElementNotFoundException
	    {

	        List<Integer> expected = expectedInterventionRule.stream().map(directory -> directory.getRuleStatus()).collect(Collectors.toList());
	        batchInterventionRulePO.sortColumn(InterventionRulesColumns.STATUS, SortOrder.DESCENDING);

	        List<String> actual = batchInterventionRulePO.retrieveColumnData(InterventionRulesColumns.STATUS);
	        if (actual.contains("Active"))
	        {
	        	actual.removeAll(actual);
	        	actual.add(0, "[1]");
	        }
	        if (actual.contains("Inactive"))
	        {
	        	actual.removeAll(actual);
	        	actual.add(0, "[2]");
	        }
	        assertThat(actual, Matchers.containsInAnyOrder(expected.toString()));
	    }
	    
	    @Test
	    @Order(7)
	    @DisplayName("3719: RxCC Batch Interventions Form Library Sort Formularies Assigned")
	    public void sortFormAssignedDes() throws ElementNotFoundException
	    {

	        List<Integer> expected = expectedInterventionRule.stream().map(directory -> directory.getNumberFormularies()).collect(Collectors.toList());
	        batchInterventionRulePO.sortColumn(InterventionRulesColumns.FORMULARIES_ASSIGNED, SortOrder.DESCENDING);

	        List<String> actual = batchInterventionRulePO.retrieveColumnData(InterventionRulesColumns.FORMULARIES_ASSIGNED);
	        assertThat(actual.toString(), Matchers.containsInAnyOrder(expected.toString()) != null);
	    }
	    
	    @Test
	    @Order(8)
	    @DisplayName("3719: RxCC Batch Interventions Form Library Sort Tenants Assigned")
	    public void sortTenAssignedDes() throws ElementNotFoundException
	    {

	        List<Integer> expected = expectedInterventionRule.stream().map(directory -> directory.getNumberFormularies()).collect(Collectors.toList());
	        batchInterventionRulePO.sortColumn(InterventionRulesColumns.TENANTS_ASSIGNED, SortOrder.DESCENDING);

	        List<String> actual = batchInterventionRulePO.retrieveColumnData(InterventionRulesColumns.TENANTS_ASSIGNED);
	        assertThat(actual.toString(), Matchers.containsInAnyOrder(expected.toString()) != null);
	    }
	   
	    @Test
	    @Order(9)
	    @DisplayName("3719: RxCC Batch Interventions Form Library Sort Run Daily ")
	    public void sortRunDailyDes() throws ElementNotFoundException
	    {

	        List<Boolean> expected = expectedInterventionRule.stream().map(directory -> directory.getRunDailyInd()).collect(Collectors.toList());
	        batchInterventionRulePO.sortColumn(InterventionRulesColumns.RUN_DAILY, SortOrder.DESCENDING);

	        List<String> actual = batchInterventionRulePO.retrieveColumnData(InterventionRulesColumns.RUN_DAILY);
	        if (actual.contains("No"))
	        {
	        	actual.removeAll(actual);
	        	actual.add(0, "[false]");
	        }
	        if (actual.contains("Yes"))
	        {
	        	actual.removeAll(actual);
	        	actual.add(0, "[true]");
	        }

	        assertThat(actual, Matchers.containsInAnyOrder(expected.toString()));
	    }
	    
	    @TestFactory
	    @Order(10)
	    @DisplayName("3759: RxCC Batch Interventions Rules Library Filter //Currently blocked by #197207")
	    public List<DynamicNode> filterRuleName() throws ElementNotFoundException 
	    {
	        List<String> filterValueList = expectedInterventionRule.stream().map(directory -> directory.getRuleName()).collect(Collectors.toList());
	        String filterValue = filterValueList.get(0); 
	        return runFilter(InterventionRulesColumns.RULE_NAME, filterValue);

	    }

	    private List<DynamicNode> runFilter(InterventionRulesColumns column, String filterValue)  //, String selection )
	    {
	        List<DynamicNode> test = new ArrayList<DynamicNode>();

	        BatchInterventionRulesColumnFilterStep step = new BatchInterventionRulesColumnFilterStep(driverBase.getWebDriver(), column, filterValue);

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

	    } 
	    

