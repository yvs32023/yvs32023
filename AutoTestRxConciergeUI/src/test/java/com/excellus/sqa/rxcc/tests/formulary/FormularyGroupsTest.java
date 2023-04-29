/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.formulary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.Comparator;
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
import com.excellus.sqa.rxcc.Utility;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.GroupQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Group;
import com.excellus.sqa.rxcc.dto.Group.Type;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.TenantSubscription;
import com.excellus.sqa.rxcc.dto.formulary.FormularyGroupsColumns;
import com.excellus.sqa.rxcc.pages.formulary.FormularyGroupsPO;
import com.excellus.sqa.rxcc.pages.home.MainMenuPO;
import com.excellus.sqa.rxcc.steps.formulary.FormularyGroupColumnFilterStep;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.utilities.SortOrder;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.step.IStep.Status;

/**
 * @author Neeru Tagore (ntagore)
 * @since 09/23/2022
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_FULL_SINGLE"})
@Tag("ALL")
@Tag("FORMULARY")
public class FormularyGroupsTest extends RxConciergeUITestBase {
    
    private static final Logger logger = LoggerFactory.getLogger(FormularyGroupsTest.class);

    static PageConfiguration pageConfiguration;
    static MainMenuPO mainMenuPO;
    static FormularyGroupsPO po; 
    static List<Group> group;  
    static Group groupFilter;
    final static String searchTerm = "campus";
    
    static Tenant tenantName; //newly added
    
    static List<Group> expectedgroups;
    static List<Group> actualgroups;
    
    @BeforeAll
    public static void setupPageObject() throws ElementNotFoundException
    {
        logger.info("Setup FormularyGroupsTest");
        
        pageConfiguration = (PageConfiguration) BeanLoader.loadBean("memberPage");

        po = new FormularyGroupsPO(driverBase.getWebDriver(), pageConfiguration);

        po.clickFormularyGroupsIcon(); 
        SeleniumPageHelperAndWaiter.pause(20000);

        // Cosmos
        if (StringUtils.endsWithIgnoreCase(acctName, "_multi") ) {
            expectedgroups = GroupQueries.search(searchTerm);
        }
        else if ( StringUtils.endsWithIgnoreCase(acctName, "_single") ) {
            expectedgroups = GroupQueries.search(searchTerm, TenantQueries.getAdTenantId(TenantSubscription.EHP.getAdTenantId()));
        }
        else if ( StringUtils.endsWithIgnoreCase(acctName, "_loa") ) {
            expectedgroups = GroupQueries.search(searchTerm, TenantQueries.getAdTenantId(Tenant.Type.LOA.getSubscriptionName()));
        }
        else if ( StringUtils.endsWithIgnoreCase(acctName, "_med") ) {
            expectedgroups = GroupQueries.search(searchTerm, TenantQueries.getAdTenantId(Tenant.Type.MED.getSubscriptionName()));
        }
        SeleniumPageHelperAndWaiter.pause(2000);

        //UI 
        po.searchKeyword(searchTerm.toUpperCase());
        SeleniumPageHelperAndWaiter.pause(2000);
        actualgroups = po.retrieveFormularyGroupsInfo();
        SeleniumPageHelperAndWaiter.pause(2000);
    }

    @Test
    @Order(1)
    @DisplayName("42677- Group Maintenance- Screen Verification (column headers)")
    public void validateColumnHeaders()
    {
        List<String> expected = FormularyGroupsColumns.getFormularyGroupsColumns();
        logger.info("**********expected****  "  +      expected);
        List<String> actual = po.retrieveColumnHeaders();
        
        logger.info("**********actual****  "  +  actual);
        
        assertThat(actual, contains(expected.toArray(new String[expected.size()])));
    }
    
    
    @Test
    @Order(2)
    @DisplayName("42271: RxCC Group Maintenance Sort Group ID - Desc")
    public void sortGroupIdDes() throws ElementNotFoundException
    {

        List<String> expected = expectedgroups.stream().map(directory -> directory.getGroupId()).collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        po.sortColumn(FormularyGroupsColumns.GROUP_ID, SortOrder.DESCENDING);

        List<String> actual = po.retrieveColumnData(FormularyGroupsColumns.GROUP_ID);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }

    @Test
    @Order(3)
    @DisplayName("42271: RxCC Group Maintenance Sort Group ID- Asc")
    public void sortGroupIdAsc() throws ElementNotFoundException
    {

        List<String> expected = expectedgroups.stream().map(directory -> directory.getGroupId()).collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        po.sortColumn(FormularyGroupsColumns.GROUP_ID, SortOrder.ASCENDING);

        List<String> actual = po.retrieveColumnData(FormularyGroupsColumns.GROUP_ID);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    
    @TestFactory
    @Order(4)
    @DisplayName("42291: RxCC Group - Search Forms")
    public List<DynamicNode> formularySearch() throws ElementNotFoundException
    {
      
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        
        for ( Group expected : expectedgroups )
        { 
           boolean found = false; 
            for ( Group actual : actualgroups )
            {
                if ( expected.getGroupId().equals(actual.getGroupId()))
                {
                    tests.add(dynamicContainer("Group Id " + expected.getGroupId(), expected.compareUI(actual)));
                    found = true;
                    break;
                }
            }
            
            if ( !found )
            {
                tests.add(dynamicTest("Group Id" + expected.getGroupId() + " is missing from UI search result", 
                        () -> fail("Group Id [" + expected.toString() + "] not found in UI search result")));
            }
        }
        return tests;
    }

    @TestFactory
    @Order(5)
    @DisplayName("42282: RxCC Group Maintenance Filter ")
    public List<DynamicNode> filterGroupId() throws ElementNotFoundException 
    {
        List<String> filterValueList = expectedgroups.stream().map(directory -> directory.getGroupId()).collect(Collectors.toList());
        String filterValue = filterValueList.get(0); 
        return runFilter("Group Id", filterValue);
    } 
        
    private List<DynamicNode> runFilter(String column, String filterValue)  //, String selection )
    {
        List<DynamicNode> test = new ArrayList<DynamicNode>();
        
        FormularyGroupColumnFilterStep step = new FormularyGroupColumnFilterStep(driverBase.getWebDriver(), column, filterValue, column);;
        step.run();
        
        test.addAll(step.getTestResults());
        
        if ( step.stepStatus() != Status.COMPLETED ) {
            return test;
        }
        
        List<String> expectedValues = new ArrayList<String>();
        for ( Group group : expectedgroups )
        {
      
                if ( StringUtils.equalsIgnoreCase (group.getGroupId(), filterValue) ) {
                  expectedValues.add(group.getGroupId());
                }
  
        }
            test.add(dynamicTest("Record count display [" + expectedValues.size() + "]", () -> assertEquals(expectedValues.size() + " Records Found", step.getLabelRowCount())));
            test.add(dynamicTest("Record rows [" + expectedValues.size() +  "]", () -> assertEquals(expectedValues.size() + " Records Found" , step.getFilterRowCount())));
            
        return test;
    }

    @Test
    @Order(6)
    @DisplayName("42271: RxCC Group Maintenance Sort Tenant ID - Asc")
    public void sortTenantIdAsc() throws ElementNotFoundException
    {

        List<String> expected = expectedgroups.stream().map(directory -> directory.getTenantId()).collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        po.sortColumn(FormularyGroupsColumns.TENANT_ID, SortOrder.ASCENDING);

        List<String> actual = po.retrieveColumnData(FormularyGroupsColumns.TENANT_ID);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    
    @Test
    @Order(7)
    @DisplayName("42271: RxCC Group Maintenance Sort Tenant Name - Asc")
    public void sortTenantNameAsc() throws ElementNotFoundException
    {
        List<String> expected = expectedgroups.stream().map(directory -> Type.valueOfEnum(directory.getTenantId()).toString()).collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER);
        po.sortColumn(FormularyGroupsColumns.TENANT_NAME, SortOrder.ASCENDING);

        List<String> actual = po.retrieveColumnData(FormularyGroupsColumns.TENANT_NAME);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));

    }
    
    @Test
    @Order(8)
    @DisplayName("42271: RxCC Group Maintenance Sort RxCC Group Name -Desc")
    public void sortGroupNameDes() throws ElementNotFoundException
    {

        List<String> expected = expectedgroups.stream().map(directory -> directory.getRxccGroupName()).collect(Collectors.toList());
        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());
        po.sortColumn(FormularyGroupsColumns.RXCC_GROUP_NAME, SortOrder.DESCENDING);
        
        List<String> actual = po.retrieveColumnData(FormularyGroupsColumns.RXCC_GROUP_NAME);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    
    @Test
    @Order(9)
    @DisplayName("42271: RxCC Group Maintenance Sort Employee Group Indicator -Asc")
    public void sortEmployeeGroupIndicatorAsc() throws ElementNotFoundException
    {    
        List<Boolean> expected = expectedgroups.stream().map(directory -> directory.isMemberCommunicationInd()).sorted().collect(Collectors.toList());
        
        expected.sort(Comparator.naturalOrder());
        po.sortColumn(FormularyGroupsColumns.EMPLOYEE_GROUP_INDICATOR, SortOrder.DESCENDING);
        
        List<Boolean> actual = po.retrieveEmpGrpIndData(FormularyGroupsColumns.EMPLOYEE_GROUP_INDICATOR);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    
    @Test
    @Order(10)
    @DisplayName("42271: RxCC Group Maintenance Sort Effective Date -Desc")
    public void sortEffectiveDateDes() throws ElementNotFoundException
    {

        List<String> expected = expectedgroups.stream().map(directory ->Utility.convertCosmosDateToUI(directory.getEffectiveDate(), "yyyyMMdd"))
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());
        po.sortColumn(FormularyGroupsColumns.EFFECTIVE_DATE, SortOrder.DESCENDING);
        
        List<String> actualTemp = po.retrieveColumnData(FormularyGroupsColumns.EFFECTIVE_DATE);
        List<String> actual = new ArrayList<>();
        for (String date : actualTemp) {
            String[] dateParts = date.split(" ")[0].split("/");
            actual.add(dateParts[2] + dateParts[0] + dateParts[1]);
        }
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    
    @Test
    @Order(11)
    @DisplayName("42271: RxCC Group Maintenance Sort Termination Date -Asc")
    public void sortTerminationDateAsc() throws ElementNotFoundException
    {

        List<String> expected = expectedgroups.stream().map(directory ->Utility.convertCosmosDateToUI(directory.getTermDate(), "yyyyMMdd"))
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER);
        po.sortColumn(FormularyGroupsColumns.TERMINATION_DATE, SortOrder.DESCENDING);
        
        List<String> actualTemp = po.retrieveColumnData(FormularyGroupsColumns.TERMINATION_DATE);
        List<String> actual = new ArrayList<>();
        for (String date : actualTemp) {
            String[] dateParts = date.split(" ")[0].split("/");
            actual.add(dateParts[2] + dateParts[0] + dateParts[1]);
        }
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    
    @Test
    @Order(12)
    @DisplayName("42271: RxCC Group Maintenance Sort Formulary Code -Desc")
    public void sortFormularyCodeDesc() throws ElementNotFoundException
    {

        List<String> expected = expectedgroups.stream().map(directory ->directory.getFormularyId())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());
        po.sortColumn(FormularyGroupsColumns.FORMULARY_CODE, SortOrder.DESCENDING);
        
        List<String> actual = po.retrieveColumnData(FormularyGroupsColumns.FORMULARY_CODE);  
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    
    @Test
    @Order(13)
    @DisplayName("42271: RxCC Group Maintenance Sort Created By -Asc")
    public void sortCreatedByAsc() throws ElementNotFoundException
    {

        List<String> expected = expectedgroups.stream().map(directory ->directory.getCreatedBy())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER);
        po.sortColumn(FormularyGroupsColumns.CREATED_USER, SortOrder.ASCENDING);
        
        List<String> actual = po.retrieveColumnData(FormularyGroupsColumns.CREATED_USER);  
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    
    @Test
    @Order(14)
    @DisplayName("42271: RxCC Group Maintenance Sort Create Date-Asc")

    public void sortCreateDateAsc() throws ElementNotFoundException
    {
        List<String> expected = expectedgroups.stream()
                .map(correspondence -> Utility.convertCosmosDateToUI(correspondence.getCreatedDateTime(),  "MM/dd/yyyy"))
                .collect(Collectors.toList());   

        expected.sort(String.CASE_INSENSITIVE_ORDER);

        po.sortColumn(FormularyGroupsColumns.CREATE_DATE, SortOrder.ASCENDING);
        List<String> actual = po.retrieveColumnData(FormularyGroupsColumns.CREATE_DATE);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    
    @Test
    @Order(15)
    @DisplayName("42271: RxCC Group Maintenance Sort Last Modified User -Asc")
    public void sortModifiedUserAsc() throws ElementNotFoundException
    {

        List<String> expected = expectedgroups.stream().map(directory ->directory.getLastUpdatedBy())
                .collect(Collectors.toList());

        expected.sort(String.CASE_INSENSITIVE_ORDER);
        po.sortColumn(FormularyGroupsColumns.LAST_MODIFIED_USER, SortOrder.ASCENDING);
        
        List<String> actual = po.retrieveColumnData(FormularyGroupsColumns.LAST_MODIFIED_USER);  
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }
    
    @Test
    @Order(16)
    @DisplayName("42271: RxCC Group Maintenance Sort Last Updated-Desc")

    public void sortLastUpdatedDesc() throws ElementNotFoundException
    {
        List<String> expected = expectedgroups.stream()
                .map(correspondence -> Utility.convertCosmosDateToUI(correspondence.getLastUpdatedDateTime(),  "MM/dd/yyyy"))
                .collect(Collectors.toList());   

        expected.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        po.sortColumn(FormularyGroupsColumns.LAST_UPDATED, SortOrder.DESCENDING);
        List<String> actual = po.retrieveColumnData(FormularyGroupsColumns.LAST_UPDATED);
        assertThat(actual, Matchers.containsInAnyOrder(expected.toArray()));
    }


}

















