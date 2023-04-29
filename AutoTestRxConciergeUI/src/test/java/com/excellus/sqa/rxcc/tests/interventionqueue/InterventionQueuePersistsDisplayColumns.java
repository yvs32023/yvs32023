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
import org.hamcrest.Matchers;
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
@Tag("INTERVENTION-QUEUE-DISPLAY-COLUMNS")
public class InterventionQueuePersistsDisplayColumns extends RxConciergeUITestBase {


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

  


  @Test
  @Order(1)
  @DisplayName("205320: RxCC Intervention Queue - Refactor Persisting Queue Selection RHP_EHP")
  public void validateQueueSelectionPersistsAllColumnsRHP_EHP() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(2000);
    interventionQueuePO.selectRow(1);
    List<String> expected = DisplayColumns.getDisplayColumnsList();
    verifyDisplayColumnHeaders(expected);
  

  }
  
  @Test
  @Order(2)
  @DisplayName("205295: RxCC Intervention Queue - Persist Storing Columns RHP_EHP")
  public void validateQueueSelectionPersistsStoringRHP_EHP() throws Exception {

    SeleniumPageHelperAndWaiter.pause(10000);
    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    SeleniumPageHelperAndWaiter.pause(10000);
    interventionQueuePO.setDisplayColumn("Member Id");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(10000);
    interventionQueuePO.selectRow(2);
    SeleniumPageHelperAndWaiter.pause(10000);
    
    List<String> expected = new ArrayList<String>();
    expected.add("Date & Time");
    expected.add("Status");
    expected.add("RxCC Group Name");
    expected.add("Plan Cost");
    expected.add("Member Cost");
    expected.add("Member Id");
    expected.add("Member Name");
    expected.add("Target Drug Name");
    expected.add("Provider Name");
    expected.add("Provider Phone Number");
   
    
    
    verifyDisplayColumnHeaders(expected);
  

  }
  
  
  @Test
  @Order(3)
  @DisplayName("205346: Verify Persists Filter for Status column for RHP Queue::::")
  public void validateStatusFilter_RHP_EHP() throws Exception {
      
    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(5000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(5000);
    interventionQueuePO.selectColumnFilter("Status");
    interventionQueuePO.setFilterStatus("Generated");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectColumnFilterValue("Generated");
    SeleniumPageHelperAndWaiter.pause(10000);
    
    interventionQueuePO.selectRow(2);
    
    
    SeleniumPageHelperAndWaiter.pause(10000);

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
  
  
  
 

}
