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
public class InterventionQueueDisplayColumnsTest extends RxConciergeUITestBase {


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
  @DisplayName("157333: validateDisplayColumnsRHP_EHP")
  public void validateDisplayColumnsRHP_EHP() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    List<String> expected = DisplayColumns.getDisplayColumnsList();

    verifyDisplayColumnHeaders(expected);

  }
  
  @Test
  @Order(2)
  @DisplayName("157333: validateDisplayColumnsRHP_EE")
  public void validateDisplayColumnsRHP_EE() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Employee");
    SeleniumPageHelperAndWaiter.pause(1000);
    List<String> expected = DisplayColumns.getDisplayColumnsList();

    verifyDisplayColumnHeaders(expected);

  }
  
  @Test
  @Order(3)
  @DisplayName("157333: validateDisplayColumnsRHP_LBS")
  public void validateDisplayColumnsRHP_LBS() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("LBS Out Of Area");
    SeleniumPageHelperAndWaiter.pause(1000);
    List<String> expected = DisplayColumns.getDisplayColumnsList();

    verifyDisplayColumnHeaders(expected);

  }
  
  
  @Test
  @Order(4)
  @DisplayName("157333: validateDisplayColumnsRHP_MED")
  public void validateDisplayColumnsRHP_MED() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus BCBS Medicare");
    SeleniumPageHelperAndWaiter.pause(1000);
    List<String> expected = DisplayColumns.getDisplayColumnsList();

    verifyDisplayColumnHeaders(expected);

  }
  
  
  @Test
  @Order(5)
  @DisplayName("157333: validateDisplayColumnsOCQ_EHP")
  public void validateDisplayColumnsOCQ_EHP() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewOCQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    List<String> expected = DisplayColumns.getDisplayColumnsList();

    verifyDisplayColumnHeaders(expected);

  }
  
  @Test
  @Order(6)
  @DisplayName("157333:  validateDisplayColumnsOCQ_EE")
  public void validateDisplayColumnsOCQ_EE() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewOCQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Employee");
    SeleniumPageHelperAndWaiter.pause(1000);
    List<String> expected = DisplayColumns.getDisplayColumnsList();

    verifyDisplayColumnHeaders(expected);

  }
  
  @Test
  @Order(7)
  @DisplayName("157333: validateDisplayColumnsOCQ_LBS")
  public void validateDisplayColumnsOCQ_LBS() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewOCQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("LBS Out Of Area");
    SeleniumPageHelperAndWaiter.pause(1000);
    List<String> expected = DisplayColumns.getDisplayColumnsList();

    verifyDisplayColumnHeaders(expected);

  }
  
  
  @Test
  @Order(8)
  @DisplayName("157333: validateDisplayColumnsOCQ_MED")
  public void validateDisplayColumnsOCQ_MED() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewOCQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus BCBS Medicare");
    SeleniumPageHelperAndWaiter.pause(1000);
    List<String> expected = DisplayColumns.getDisplayColumnsList();

    verifyDisplayColumnHeaders(expected);

  }


}
