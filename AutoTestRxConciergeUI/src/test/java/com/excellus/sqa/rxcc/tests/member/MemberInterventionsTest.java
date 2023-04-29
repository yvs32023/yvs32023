/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
/**
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
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
import com.excellus.sqa.rxcc.cosmos.MemberInterventionQueries;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.member.MemberInterventionsColumns;
import com.excellus.sqa.rxcc.dto.member.MemberTabMenu;
import com.excellus.sqa.rxcc.pages.home.MainMenuPO;
import com.excellus.sqa.rxcc.pages.member.MemberInterventionsPO;
import com.excellus.sqa.rxcc.steps.member.MemberInterventionsColumnFilterStep;
import com.excellus.sqa.rxcc.workflows.member.OpenMemberWorkflow;
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
 * @since 11/14/2022
 */
/**
 * @author hzia
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI"})
@Tag("ALL")
@Tag("INTERVENTIONS")
public class MemberInterventionsTest extends RxConciergeUITestBase{

    private static final Logger logger = LoggerFactory.getLogger(MemberInterventionsTest.class);

    static PageConfiguration pageConfiguration;
    static MainMenuPO mainMenuPO;
    static MemberInterventionsPO memberInterventionsPO;

    static List<MemberIntervention> memberInterventions;

    static List<MemberIntervention> expectedMemberInterventions;
    static List<MemberIntervention> actualMemberInterventions;

    @BeforeAll
    public static void setupPageObject() throws ElementNotFoundException
    {
        pageConfiguration = (PageConfiguration) BeanLoader.loadBean("memberPage");
        String searchTerm = "EHP"; 
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        memberInterventionsPO = new MemberInterventionsPO(driverBase.getWebDriver(), pageConfiguration);
    	// Using EHP to validate columns
		String memberId = MemberInterventionQueries.getRandomMemberWithIntervention(searchTerm);
		// Open the member
		OpenMemberWorkflow openMemberWorkflow = new OpenMemberWorkflow(driverBase.getWebDriver(), pageConfiguration, memberId, true, MemberTabMenu.INTERVENTIONS);
		openMemberWorkflow.run();
		tests.addAll(openMemberWorkflow.workflowStepResults());
		
		if ( openMemberWorkflow.workflowStatus() != Status.COMPLETED )
		{
			return;
		}

        SeleniumPageHelperAndWaiter.pause(5000);
        
        expectedMemberInterventions = MemberInterventionQueries.getIntervention(searchTerm, memberId);
        logger.info("***Search result expected value**** " + expectedMemberInterventions);

        actualMemberInterventions = memberInterventionsPO.retrieveMemberInterventionsInfo();
        
        logger.info("***Search result actual value**** " + actualMemberInterventions);
        
        SeleniumPageHelperAndWaiter.pause(2000);
    }
    
    @TestFactory
    @Order(1)
    @DisplayName("3870 - RxCC Member Interventions")
    public List<DynamicTest> validateMemberId()
    {
        return Arrays.asList( dynamicTest("Member Id: " + expectedMemberInterventions.get(0).getMemberId(), 
        		() -> assertNotNull(expectedMemberInterventions.get(0).getMemberId())) );
    }


    @Test
    @Order(2)
    @DisplayName("3870 - RxCC Member Interventions - Screen Verification (column headers)")
    public void validateColumnHeaders()
    {
        List<String> expected = MemberInterventionsColumns.getMemberInterventionsColumns();
        List<String> actual = memberInterventionsPO.retrieveColumnHeaders();

        assertThat(actual, contains(expected.toArray(new String[expected.size()])));
        
    }


    @TestFactory
    @Order(3)
    @DisplayName("4965 - RxCC Member Interventions Search")
    public List<DynamicNode> memberInterventionsSearchType() throws ElementNotFoundException
    {

        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        for ( MemberIntervention expected : expectedMemberInterventions ) 
        { 
            boolean found = false; 
            for ( MemberIntervention actual : actualMemberInterventions )
            {
            	  if ( expected.getTargetProductName().equals(actual.getTargetProductName())    &&
                          expected.getOverrideProviderName().equals(actual.getOverrideProviderName())  )
            	  {
            		  tests.add(dynamicContainer("Interventions Target Drug " + expected.getTargetProductName(), expected.compareMemberInterventionsUI(actual)));
                        found = true;
                        break;
            }

            if ( !found )
            {
                tests.add(dynamicTest("Interventions Target Drug " + expected.getTargetProductName() + " is missing from UI search result", 
                        () -> fail("Interventions [" + expected.toString() + "] not found in UI search result")));
            }
            }
        }
        return tests;

    } 

    @Test
    @Order(4)
    @DisplayName("4967-RxCC Member Interventions sorting-Created Date")
    public void sortCreatedDateDes() throws ElementNotFoundException
    {
    	List<String> expected = expectedMemberInterventions.stream().map(directory -> Utility.convertCosmosDateToUI(directory.getCreatedDateTime(),"MM/dd/yyyy HH:mm:ss")).collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        memberInterventionsPO.sortColumn(MemberInterventionsColumns.CREATED_DATE, SortOrder.DESCENDING);

        List<String> actual = memberInterventionsPO.retrieveColumnData(MemberInterventionsColumns.CREATED_DATE);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    
    @Test
    @Order(5)
    @DisplayName("4967-RxCC Member Interventions sorting-Status Change Date")
    public void sortStatusChangeDateDes() throws ElementNotFoundException
    {
    	List<String> expected = expectedMemberInterventions.stream().map(directory -> Utility.convertCosmosDateToUI(directory.getQueueStatusChangeDateTime(),"MM/dd/yyyy HH:mm:ss")).collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        memberInterventionsPO.sortColumn(MemberInterventionsColumns.STATUS_CHANGE_DATE, SortOrder.DESCENDING);

        List<String> actual = memberInterventionsPO.retrieveColumnData(MemberInterventionsColumns.STATUS_CHANGE_DATE);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(6)
    @DisplayName("4967-RxCC Member Interventions sorting-Target Drug")
    public void sortLastNameAsc() throws ElementNotFoundException
    {

        List<String> expected = expectedMemberInterventions.stream().map(directory -> directory.getTargetProductName()).collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        memberInterventionsPO.sortColumn(MemberInterventionsColumns.TARGET_DRUG, SortOrder.ASCENDING);

        List<String> actual = memberInterventionsPO.retrieveColumnData(MemberInterventionsColumns.TARGET_DRUG);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    
    @Test
    @Order(7)
    @DisplayName("4967-RxCC Member Interventions sorting-Provider")
    public void sortProvider() throws ElementNotFoundException
    {

        List<String> expected = expectedMemberInterventions.stream().map(directory -> directory.getOverrideProviderName()).collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        memberInterventionsPO.sortColumn(MemberInterventionsColumns.PROVIDER, SortOrder.ASCENDING);

        List<String> actual = memberInterventionsPO.retrieveColumnData(MemberInterventionsColumns.PROVIDER);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @TestFactory
    @Order(8)
    @DisplayName("3101-Member Interventions - Filters - CREATED_DATE")
    public List<DynamicNode> filterCreatedDate() throws ElementNotFoundException 
    {
        List<String> filterValueList = expectedMemberInterventions.stream().map(directory -> Utility.convertCosmosDateToUI(directory.getCreatedDateTime(),"MM/dd/yyyy HH:mm:ss")).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter(MemberInterventionsColumns.CREATED_DATE, filterValue);	
    }   
    
    @TestFactory
    @Order(9)
    @DisplayName("3101-Member Interventions - Filters - STATUS_CHANGE_DATE")
    public List<DynamicNode> filterStatusChangeDate() throws ElementNotFoundException 
    {
        List<String> filterValueList = expectedMemberInterventions.stream().map(directory -> Utility.convertCosmosDateToUI(directory.getQueueStatusChangeDateTime(),"MM/dd/yyyy HH:mm:ss")).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter(MemberInterventionsColumns.STATUS_CHANGE_DATE, filterValue);	
    }   
    
    @TestFactory
    @Order(10)
    @DisplayName("3101-Member Interventions - Filters - MEMBER_COST")
    public List<DynamicNode> filterMemberCost() throws ElementNotFoundException 
    {
        List<Double> filterValueList = expectedMemberInterventions.stream().map(directory -> directory.getMemberCost()).collect(Collectors.toList());
        String filterValue = Double.toString(filterValueList.get(0)); 
        return runFilter(MemberInterventionsColumns.MEMBER_COST, filterValue);

     }
        
    @TestFactory
    @Order(11)
    @DisplayName("3101-Member Interventions - Filters - PLAN_COST")
    public List<DynamicNode> filterPlanCost() throws ElementNotFoundException 
    {
        List<Double> filterValueList = expectedMemberInterventions.stream().map(directory -> directory.getPlanCost()).collect(Collectors.toList());
        String filterValue = Double.toString(filterValueList.get(0)); 
        return runFilter(MemberInterventionsColumns.PLAN_COST, filterValue);

     }
    
    @TestFactory
    @Order(12)
    @DisplayName("3101-Member Interventions - Filters - PROVIDER")
    public List<DynamicNode> filterProvider() throws ElementNotFoundException 
    {
        List<String> filterValueList = expectedMemberInterventions.stream().map(directory -> directory.getOverrideProviderName()).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter(MemberInterventionsColumns.PROVIDER, filterValue);

     }
    
   @TestFactory
   @Order(13)
   @DisplayName("3101-Member Interventions - Filters - TARGET_DRUG")
   public List<DynamicNode> filterTargetDrug() throws ElementNotFoundException 
   {
       List<String> filterValueList = expectedMemberInterventions.stream().map(directory -> directory.getTargetProductName()).collect(Collectors.toList());
       String filterValue = filterValueList.get(0); 
       return runFilter(MemberInterventionsColumns.TARGET_DRUG, filterValue);

    }
   

        private List<DynamicNode> runFilter(MemberInterventionsColumns column, String filterValue)  //, String selection )
        {
            List<DynamicNode> test = new ArrayList<DynamicNode>();

            MemberInterventionsColumnFilterStep step = new MemberInterventionsColumnFilterStep(driverBase.getWebDriver(), column, filterValue);

            step.run();

            test.addAll(step.getTestResults());

            if ( step.stepStatus() != Status.COMPLETED ) {
                return test;
            }

            List<String> expectedValues = new ArrayList<String>();
            for ( MemberIntervention memberInterventions : expectedMemberInterventions )
            {
                switch (column) {

                case CREATED_DATE:
                	String expectedDate = Utility.convertCosmosDateToUI(memberInterventions.getCreatedDateTime(),"MM/dd/yyyy HH:mm:ss");
                    if ( StringUtils.equalsIgnoreCase (expectedDate, filterValue) ) {
                        expectedValues.add(expectedDate);
                    }
                    break;
                    
                case STATUS_CHANGE_DATE:
                	String expectedStatusDate = Utility.convertCosmosDateToUI(memberInterventions.getQueueStatusChangeDateTime(),"MM/dd/yyyy HH:mm:ss");
                    if ( StringUtils.equalsIgnoreCase (expectedStatusDate, filterValue) ) {
                        expectedValues.add(expectedStatusDate);
                    }
                    break;
                    
                case PLAN_COST:
                	String expectedPlanCost = Double.toString(memberInterventions.getPlanCost());
                    if ( StringUtils.equalsIgnoreCase(expectedPlanCost.trim(), filterValue.trim()) ) {
                        expectedValues.add(expectedPlanCost);
                    }
                    break;
                    
                case MEMBER_COST:
                	String expectedMemberCost = Double.toString(memberInterventions.getMemberCost());
                    if ( StringUtils.equalsIgnoreCase(expectedMemberCost, filterValue) ) {
                        expectedValues.add(expectedMemberCost);
                    }
                    break;

                case PROVIDER:
                    if ( StringUtils.equalsIgnoreCase(memberInterventions.getOverrideProviderName(), filterValue) ) {
                        expectedValues.add(memberInterventions.getOverrideProviderName());
                    }
                    break;

                case TARGET_DRUG:
                	String expectedTargetDrug = memberInterventions.getTargetProductName().toUpperCase();
                    if ( StringUtils.equalsIgnoreCase(expectedTargetDrug, filterValue) ) {
                        expectedValues.add(expectedTargetDrug);
                    }
                default:
                    break;
                }
            }
            test.add(dynamicTest("Record count display [" + expectedValues.size() + "]", () -> assertEquals(expectedValues.size() + " Records FoundFound", step.getLabelRowCount())));
            test.add(dynamicTest("Provider record rows [" + expectedValues.size() + "]", () -> assertEquals(expectedValues.size(), step.getFilterRowCount())));
            test.add(dynamicTest("Column value validation", () -> assertThat(step.getColumnValues(), containsInAnyOrder(expectedValues.toArray(new String[expectedValues.size()]))) ));
            return test;
        }

    }

