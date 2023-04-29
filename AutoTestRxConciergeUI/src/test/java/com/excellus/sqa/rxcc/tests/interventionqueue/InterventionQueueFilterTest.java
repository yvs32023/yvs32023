/**
 * 
 * @copyright 2023 Excellus BCBS All rights reserved.
 * 
 */
/**
 * 
 */
package com.excellus.sqa.rxcc.tests.interventionqueue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.text.StringTokenizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionQueries;
import com.excellus.sqa.rxcc.dto.MainMenu;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.intervention.DisplayColumns;
import com.excellus.sqa.rxcc.pages.home.MainMenuPO;
import com.excellus.sqa.rxcc.pages.intervention.InterventionQueuePO;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;


/**
 * 
 * 
 * @author Vijaya Sekhar Yeleswarapu (vyeleswa)
 * @since 02/06/2023
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_FULL_SINGLE", "RXCC_FULL_LOA", "RXCC_FULL_MED"})
@Tag("ALL")
@Tag("INTERVENTION-QUEUE")
public class InterventionQueueFilterTest extends RxConciergeUITestBase {


  static InterventionQueuePO interventionQueuePO;
  static PageConfiguration pageConfigurationIntervention;
  static MainMenuPO mainMenuPO;
  static PageConfiguration pageConfigurationHome;

  /**
   * Verifies display column headers
   * 
   * @throws ElementNotFoundException if issue occurs
   */
  public static void verifyDisplayColumnHeaders(List<String> columns)
      throws ElementNotFoundException {
    for (String column : columns) {

      assertTrue((interventionQueuePO.getDisplayColumnNames()).contains(column));
    }
  }

  public static int countMatches(List<String> list1, List<String> list2) {

    return list1.stream().filter(list2::contains).collect(Collectors.toSet()).size();

  }



  @BeforeAll
  public static void goToInteventionQueue() throws ElementNotFoundException, InterruptedException {

    pageConfigurationHome = BeanLoader
        .loadBean(BeanNames.HOME_PAGE, PageConfiguration.class);

    mainMenuPO = new MainMenuPO(driverBase.getWebDriver(),
        pageConfigurationHome);
    mainMenuPO.selectMenu(MainMenu.INTERVENTION_QUEUE);

    pageConfigurationIntervention = BeanLoader
        .loadBean(BeanNames.INTERVENTION_PAGE, PageConfiguration.class);

    interventionQueuePO = new InterventionQueuePO(
        driverBase.getWebDriver(), pageConfigurationIntervention);

  }

  @Order(1)
  @DisplayName("157333: Verify Filter for Provider Name column for RHP Queue::")
  public void validateProviderNameFilter_RHP_EHP() throws Exception {

    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectColumnFilter("Provider Name");
    interventionQueuePO.setFilterColumnName("Provider Name","SANDRA YALE");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectColumnFilterValue("SANDRA YALE");
    SeleniumPageHelperAndWaiter.pause(1000);

    String query ="SELECT * FROM c "
        + "where c.type='intervention' "
        + "and c.originalProviderName =  'SANDRA YALE' "
        + "AND c.targetInterventionQueueType='RPH' "
        + "AND c.queueStatusCode IN ( \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\", \"11\", \"12\", \"13\", \"14\", \"15\", \"16\", \"17\", \"18\", \"19\", \"20\", \"21\")\r\n"
        ;
        
        
    List<MemberIntervention> search = MemberInterventionQueries.searchInterventionsByQuery(
        "EHP", query);

    List<String> expected = search.stream().map(intervention -> intervention.getProviderName()).collect(Collectors.toList());

  
    List<String> actual = interventionQueuePO.retrieveMemberInterventionColValues(DisplayColumns.PROVIDER_NAME.toString());

    System.out.println("from UI Actual " + actual);
    System.out.println("from DB Expected " + expected);

    int matchingCount = 0;

    matchingCount = countMatches(actual, expected);
    System.out.println(" Number of matching records between actual from application and expected from cosmos DB are " + matchingCount);

    assertThat("no matching records found between application and cosmos DB", matchingCount >= 1);

  }
  
  @Test
  @Order(2)
  @DisplayName("157333: Verify Filter for TargetDrugName column for RHP Queue::")
  public void validateTargetDrugNameSearch_RHP_EHP() throws Exception {

    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectColumnFilter("Target Drug Name");
    interventionQueuePO.setFilterColumnName("Target Drug Name","Emollient Base External Cream");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectColumnFilterValue("Emollient Base External Cream");
    SeleniumPageHelperAndWaiter.pause(1000);


    String query ="SELECT * FROM c "
        + "where c.type='intervention' "
        + "and c.targetProductName =  'Emollient Base External Cream' "
        + "AND c.targetInterventionQueueType='RPH' "
        + "AND c.queueStatusCode IN ( \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\", \"11\", \"12\", \"13\", \"14\", \"15\", \"16\", \"17\", \"18\", \"19\", \"20\", \"21\")\r\n"
        ;
        
        

    List<MemberIntervention> search = MemberInterventionQueries.searchInterventionsByQuery(
        "EHP", query);

    List<String> expected = search.stream().map(intervention -> intervention.getTargetProductName()).collect(Collectors.toList());

  
    List<String> actual = interventionQueuePO.retrieveMemberInterventionColValues(DisplayColumns.TARGET_DRUG_NAME.toString());

    System.out.println("from UI Actual " + actual);
    System.out.println("from DB Expected " + expected);

    int matchingCount = 0;

    matchingCount = countMatches(actual, expected);
    System.out.println(" Number of matching records between actual from application and expected from cosmos DB are " + matchingCount);

    assertThat("no matching records found between application and cosmos DB", matchingCount >= 1);

  }
  
  
  @Test
  @Order(3)
  @DisplayName("157333: Verify Filter for Member Name column for RHP Queue::::")
  public void validateMemberNameSearch_RHP_EHP() throws Exception {
      
    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectColumnFilter("Member Name");
    interventionQueuePO.setFilterColumnName("Member Name","Kata Tolley");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectColumnFilterValue("Kata Tolley");
    SeleniumPageHelperAndWaiter.pause(1000);

    String query ="SELECT * FROM c "
        + "where c.type='intervention' "
        + "and c.memberName =  'Kata Tolley' "
        + "AND c.targetInterventionQueueType='RPH' "
        + "AND c.queueStatusCode IN ( \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\", \"11\", \"12\", \"13\", \"14\", \"15\", \"16\", \"17\", \"18\", \"19\", \"20\", \"21\")\r\n"
        ;

    List<MemberIntervention> search = MemberInterventionQueries.searchInterventionsByQuery(
        "EHP", query);

    List<String> expected = search.stream().map(intervention -> intervention.getMemberName()).collect(Collectors.toList());

  
    List<String> actual = interventionQueuePO.retrieveMemberInterventionColValues(DisplayColumns.MEMBER_NAME.toString());

    System.out.println("from UI Actual " + actual);
    System.out.println("from DB Expected " + expected);

    int matchingCount = 0;

    matchingCount = countMatches(actual, expected);
    System.out.println(" Number of matching records between actual from application and expected from cosmos DB are " + matchingCount);

    assertThat("no matching records found between application and cosmos DB", matchingCount >= 1);

  }
  
  
  @Test
  @Order(4)
  @DisplayName("157333: Verify Filter for RxCC Group Name column for RHP Queue::::")
  public void validateRxCCGroupNameSearch_RHP_EHP() throws Exception {
      
    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectColumnFilter("RxCC Group Name");
    interventionQueuePO.setFilterColumnName("RxCC Group Name","Tcat Tompkins Consolidated Area Transit");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectColumnFilterValue("Tcat Tompkins Consolidated Area Transit");
    SeleniumPageHelperAndWaiter.pause(1000);

    String query ="SELECT * FROM c "
        + "where c.type='intervention' "
        + "and c.rxccGroupName =  'Tcat Tompkins Consolidated Area Transit' "
        + "AND c.targetInterventionQueueType='RPH' "
        + "AND c.queueStatusCode IN ( \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\", \"11\", \"12\", \"13\", \"14\", \"15\", \"16\", \"17\", \"18\", \"19\", \"20\", \"21\")\r\n"
        ;

    List<MemberIntervention> search = MemberInterventionQueries.searchInterventionsByQuery(
        "EHP", query);

    List<String> expected = search.stream().map(intervention -> intervention.getRxccGroupName()).collect(Collectors.toList());

  
    List<String> actual = interventionQueuePO.retrieveMemberInterventionColValues(DisplayColumns.GROUP_NAME.toString());

    System.out.println("from UI Actual " + actual);
    System.out.println("from DB Expected " + expected);

    int matchingCount = 0;

    matchingCount = countMatches(actual, expected);
    System.out.println(" Number of matching records between actual from application and expected from cosmos DB are " + matchingCount);

    assertThat("no matching records found between application and cosmos DB", matchingCount >= 1);

  }
  
  @Test
  @Order(5)
  @DisplayName("157333: Verify Filter for Status column for RHP Queue::::")
  public void validateStatusFilter_RHP_EHP() throws Exception {
      
    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectColumnFilter("Status");
    interventionQueuePO.setFilterColumnName("Status","Generated");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectColumnFilterValue("Generated");
    SeleniumPageHelperAndWaiter.pause(1000);

    String query ="SELECT * FROM c "
        + "where c.type='intervention' "
        + "and c.queueStatusCode='6' "
        + "AND c.targetInterventionQueueType='RPH' "
        + "AND c.queueStatusCode IN ( \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\", \"11\", \"12\", \"13\", \"14\", \"15\", \"16\", \"17\", \"18\", \"19\", \"20\", \"21\")\r\n"
        ;

    List<MemberIntervention> search = MemberInterventionQueries.searchInterventionsByQuery(
        "EHP", query);

    List<String> expected = search.stream().map(intervention -> intervention.getQueueStatusCode()).collect(Collectors.toList());

  
    List<String> actual = interventionQueuePO.retrieveMemberInterventionColValues(DisplayColumns.STATUS.toString());

    System.out.println("from UI Actual " + actual);
    System.out.println("from DB Expected " + expected);
    
    String queueStatusCode = expected.get(0);
    String actualStatusUI = actual.get(0);
   
    int matchingCount = 0;
    
    if(queueStatusCode.equals("6") && actualStatusUI.equals("Generated")){
      matchingCount = 1;
    }
   
    assertThat("no matching records found between application and cosmos DB", matchingCount >= 1);

  }
  
  
  @Test
  @Order(6)
  @DisplayName("157333: Verify Filter for Plan Cost column for RHP Queue::::")
  public void validatePlanCostFilter_RHP_EHP() throws Exception {
      
    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    
    interventionQueuePO.selectColumnFilter("Plan Cost");
    interventionQueuePO.setFilterColumnMinMaxCost("16.38","16.38");
    SeleniumPageHelperAndWaiter.pause(1000);
   
    String query ="SELECT * FROM c "
        + "where c.type='intervention' "
        + "and c.planCost<17 "
        + "AND c.targetInterventionQueueType='RPH' "
        + "AND c.queueStatusCode IN ( \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\", \"11\", \"12\", \"13\", \"14\", \"15\", \"16\", \"17\", \"18\", \"19\", \"20\", \"21\")\r\n"
        ;

    List<MemberIntervention> search = MemberInterventionQueries.searchInterventionsByQuery(
        "EHP", query);

    List<String> expected = search.stream().map(intervention -> String.valueOf(intervention.getPlanCost())).collect(Collectors.toList());

  
    List<String> actualUI = interventionQueuePO.retrieveMemberInterventionColValues(DisplayColumns.PLAN_COST.toString());
    List<String> actualUIUpdate = new ArrayList<String>();
    actualUI.stream().forEach((c) -> actualUIUpdate.add(c.replace("$", "")));

    System.out.println("from UI Actual " + actualUIUpdate);
    System.out.println("from DB Expected " + expected);
    
    int matchingCount = 0;

    matchingCount = countMatches(actualUIUpdate, expected);
    System.out.println(" Number of matching records between actual from application and expected from cosmos DB are " + matchingCount);

   
    assertThat("no matching records found between application and cosmos DB", matchingCount >= 1);

  }
  
  
  @Test
  @Order(7)
  @DisplayName("157333: Verify Filter for Member Cost column for RHP Queue::::")
  public void validateMemberCostFilter_RHP_EHP() throws Exception {
      
    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    
    interventionQueuePO.selectColumnFilter("Member Cost");
    interventionQueuePO.setFilterColumnMinMaxCost("325.95","325.95");
    SeleniumPageHelperAndWaiter.pause(1000);
   
    String query ="SELECT * FROM c "
        + "where c.type='intervention' "
        + "and c.memberCost<=326 "
        + "AND c.targetInterventionQueueType='RPH' "
        + "AND c.queueStatusCode IN ( \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\", \"11\", \"12\", \"13\", \"14\", \"15\", \"16\", \"17\", \"18\", \"19\", \"20\", \"21\")\r\n"
        ;

    List<MemberIntervention> search = MemberInterventionQueries.searchInterventionsByQuery(
        "EHP", query);

    List<String> expected = search.stream().map(intervention -> String.valueOf(intervention.getMemberCost())).collect(Collectors.toList());

  
    List<String> actualUI = interventionQueuePO.retrieveMemberInterventionColValues(DisplayColumns.MEMBER_COST.toString());
    List<String> actualUIUpdate = new ArrayList<String>();
    actualUI.stream().forEach((r) -> actualUIUpdate.add(r.replace("$", "")));

    System.out.println("from UI Actual " + actualUIUpdate);
    System.out.println("from DB Expected " + expected);
    
    int matchingCount = 0;

    matchingCount = countMatches(actualUIUpdate, expected);
    System.out.println(" Number of matching records between actual from application and expected from cosmos DB are " + matchingCount);

   
    assertThat("no matching records found between application and cosmos DB", matchingCount >= 1);

  }
  
  @Test
  @Order(8)
  @DisplayName("157333: Verify Filter for Date column for RHP Queue::::")
  public void validateDateFilter_RHP_EHP() throws Exception {
      
    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    
    interventionQueuePO.selectColumnFilter("Member Cost");
    interventionQueuePO.setFilterColumnMinMaxCost("325.95","325.95");
    SeleniumPageHelperAndWaiter.pause(1000);
   
    String query ="SELECT * FROM c "
        + "where c.type='intervention' "
        + "and c.memberCost<=326 "
        + "AND c.targetInterventionQueueType='RPH' "
        + "AND c.queueStatusCode IN ( \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\", \"11\", \"12\", \"13\", \"14\", \"15\", \"16\", \"17\", \"18\", \"19\", \"20\", \"21\")\r\n"
        ;

    List<MemberIntervention> search = MemberInterventionQueries.searchInterventionsByQuery(
        "EHP", query);

    List<String> expected = search.stream().map(intervention -> String.valueOf(intervention.getMemberCost())).collect(Collectors.toList());

  
    List<String> actualUI = interventionQueuePO.retrieveMemberInterventionColValues(DisplayColumns.MEMBER_COST.toString());
    List<String> actualUIUpdate = new ArrayList<String>();
    actualUI.stream().forEach((r) -> actualUIUpdate.add(r.replace("$", "")));

    System.out.println("from UI Actual " + actualUIUpdate);
    System.out.println("from DB Expected " + expected);
    
    int matchingCount = 0;

    matchingCount = countMatches(actualUIUpdate, expected);
    System.out.println(" Number of matching records between actual from application and expected from cosmos DB are " + matchingCount);

   
    assertThat("no matching records found between application and cosmos DB", matchingCount >= 1);

  }
  

}
