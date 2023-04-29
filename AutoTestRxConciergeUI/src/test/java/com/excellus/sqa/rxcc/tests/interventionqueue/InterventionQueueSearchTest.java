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
public class InterventionQueueSearchTest extends RxConciergeUITestBase {


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
  @DisplayName("157333: Use the Search keyword text box and perform several searches on the RxCC Group Name column for RHP Queue::")
  public void validateRXCCGroupNameSearch_RHP_EHP() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.setSearchIntervention("RTMA");
    SeleniumPageHelperAndWaiter.pause(1000);

    String query ="SELECT * FROM c "
        + "where c.type='intervention' "
        + "and c.rxccGroupName =  'RTMA' "
        + "AND c.targetInterventionQueueType='RPH' "
        + "AND c.queueStatusCode IN ( \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\", \"11\", \"12\", \"13\", \"14\", \"15\", \"16\", \"17\", \"18\", \"19\", \"20\", \"21\")\r\n"
        ;
        
        

    List<MemberIntervention> search = MemberInterventionQueries.searchInterventionsByQuery(
        "EHP", query);

    List<String> expected = search.stream().map(intervention -> intervention.getRxccGroupName()).collect(Collectors.toList());

  
    List<String> actual = interventionQueuePO.retrieveMemberInterventionColValues(
        DisplayColumns.GROUP_NAME.toString());

    System.out.println("from UI Actual " + actual);
    System.out.println("from DB Expected " + expected);

    int matchingCount = 0;

    matchingCount = countMatches(actual, expected);
    System.out.println(" Number of matching records between actual from application and expected from cosmos DB are " + matchingCount);

    assertThat("no matching records found between application and cosmos DB", matchingCount >= 1);

  }

  
  @Test
  @Order(2)
  @DisplayName("157333: Use the Search keyword text box and perform several searches on the Member Name column for RHP Queue::")
  public void validateMemberNameSearch_RHP_EHP() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.setSearchIntervention("Kata Tolley");
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
  @Order(3)
  @DisplayName("157333: Use the Search keyword text box and perform several searches on the Target Drug Name column for RHP Queue::")
  public void validateTargetDrugNameSearch_RHP_EHP() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.setSearchIntervention("Emollient Base External Cream");
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
  @Order(4)
  @DisplayName("157333: Use the Search keyword text box and perform several searches on the Provider Name column for RHP Queue::")
  public void validateProviderNameSearch_RHP_EHP() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.setSearchIntervention("SANDRA YALE");
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
  @Order(5)
  @DisplayName("157333: Use the Search keyword text box and perform several searches on the Last Updated By column for RHP Queue::")
  public void validateLastUpdatedBySearch_RHP_EHP() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.setSearchIntervention("faxAlternativeFix");
    SeleniumPageHelperAndWaiter.pause(1000);

    String query ="SELECT * FROM c "
        + "where c.type='intervention' "
        + "and c.lastUpdatedBy =  'faxAlternativeFix' "
        + "AND c.targetInterventionQueueType='RPH' "
        + "AND c.queueStatusCode IN ( \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\", \"11\", \"12\", \"13\", \"14\", \"15\", \"16\", \"17\", \"18\", \"19\", \"20\", \"21\")\r\n"
        ;
        
        

    List<MemberIntervention> search = MemberInterventionQueries.searchInterventionsByQuery(
        "EHP", query);

    List<String> expected = search.stream().map(intervention -> intervention.getLastUpdatedBy()).collect(Collectors.toList());

  
    List<String> actual = interventionQueuePO.retrieveMemberInterventionColValues(DisplayColumns.LAST_UPDATED_BY.toString());

    System.out.println("from UI Actual " + actual);
    System.out.println("from DB Expected " + expected);

    int matchingCount = 0;

    matchingCount = countMatches(actual, expected);
    System.out.println(" Number of matching records between actual from application and expected from cosmos DB are " + matchingCount);

    assertThat("no matching records found between application and cosmos DB", matchingCount >= 1);

  }
  
  
  @Test
  @Order(6)
  @DisplayName("157333: Use the Search keyword text box and perform several searches on the User Name column for RHP Queue::")
  public void validateUserNameSearch_RHP_EHP() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.setSearchIntervention("smccreery2");
    SeleniumPageHelperAndWaiter.pause(1000);

    String query ="SELECT * FROM c "
        + "where c.type='intervention' "
        + "and c.createdBy =  'smccreery2' "
        + "AND c.targetInterventionQueueType='RPH' "
        + "AND c.queueStatusCode IN ( \"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\", \"11\", \"12\", \"13\", \"14\", \"15\", \"16\", \"17\", \"18\", \"19\", \"20\", \"21\")\r\n"
        ;
        
        

    List<MemberIntervention> search = MemberInterventionQueries.searchInterventionsByQuery(
        "EHP", query);

    List<String> expected = search.stream().map(intervention -> intervention.getCreatedBy()).collect(Collectors.toList());

    List<String> actual = interventionQueuePO.retrieveMemberInterventionColValues(DisplayColumns.USER_NAME.toString());

    System.out.println("from UI Actual " + actual);
    System.out.println("from DB Expected " + expected);

    int matchingCount = 0;

    matchingCount = countMatches(actual, expected);
    System.out.println(" Number of matching records between actual from application and expected from cosmos DB are " + matchingCount);

    assertThat("no matching records found between application and cosmos DB", matchingCount >= 1);

  }
  

  
}
