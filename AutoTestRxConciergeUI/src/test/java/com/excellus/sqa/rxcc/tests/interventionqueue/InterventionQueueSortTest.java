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
public class InterventionQueueSortTest extends RxConciergeUITestBase {


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

  // @Test
  // @Order(2)
  // @DisplayName("157333: Using the 'Columns Selected' drop down, select all checkboxes to show
  // columns")
  // public void validateDisplayColumnHeaders() throws Exception {
  //
  // interventionQueuePO.clickViewRPHQueue();
  // interventionQueuePO.setAllColumnsSelected(true);
  // SeleniumPageHelperAndWaiter.pause(1000);
  // interventionQueuePO.selectATenant("Excellus Health Plan");
  // SeleniumPageHelperAndWaiter.pause(1000);
  // verifyDisplayColumnHeaders(DisplayColumns.getDisplayColumnsList());
  // SeleniumPageHelperAndWaiter.pause(1000);
  //
  // List<Tenant> tenants = TenantQueries.getTenants(); // grab the 4 subscription name, exe, ehp,
  // // loa, med
  //
  // for (Tenant tenant : tenants) {
  // String subscriptionId = tenant.getSubscriptionName();
  //
  // String where = "c.type='intervention' \r\n"
  // + "AND c.memberCost >100";
  //
  // List<MemberIntervention> example = MemberInterventionQueries.searchInterventionsQueue(
  // subscriptionId, where);
  //
  // System.out.println(example.get(0).getCreatedBy());
  //
  // MemberIntervention memberIntervention = example.get(0);
  //
  // // Double memberCost = memberIntervention.getMemberCost();
  //
  //
  // // list of plan cost for sortField Validation (limiting to 50)
  // // List<String> memberCost = memberIntervention.
  //
  // Stream<Double> memberCost1 = example.stream().map(MemberIntervention::getMemberCost);
  //
  // Stream<Double> planCost = example.stream().map(MemberIntervention::getPlanCost);
  //
  // Stream<String> memberId = example.stream().map(MemberIntervention::getMemberId);
  //
  // System.out.println("List of memberCost values");
  // memberCost1.forEach(System.out::println);
  //
  // }
  //
  //
  // }


  @Test
  @Order(1)
  @DisplayName("157333: Use the Search keyword text box and perform several searches on the Date & Time column for EHP:: Defect 183006")
  public void validateDateTimeSearch_RHP_EHP() throws Exception {


    interventionQueuePO.clickResetAll();
    interventionQueuePO.clickViewRPHQueue();
    interventionQueuePO.setAllColumnsSelected(true);
    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Health Plan");
    SeleniumPageHelperAndWaiter.pause(1000);
    // interventionQueuePO.selectColumnFilter("Date & Time");
    // interventionQueuePO.setFilterStartDate("04/05/2022");
    // interventionQueuePO.setFilterEndDate("04/05/2022");

    // interventionQueuePO.setSearchIntervention("02/10/2013");
    // SeleniumPageHelperAndWaiter.pause(1000);

    String query =
        "SELECT concat(ToString(DateTimePart('month', c.queueStatusChangeDateTime)),\"/\",ToString(DateTimePart('day', c.queueStatusChangeDateTime)),\"/\",ToString(DateTimePart('yyyy', c.queueStatusChangeDateTime))) AS queueStatusChangeDateTime \r\n"
            + "FROM c where RegexMatch (c.memberId,\"[0-99]\",\"\") \r\n"
            + "AND c.type='intervention' \r\n"
            + "AND ToString(DateTimePart('yyyy', c.queueStatusChangeDateTime)) = '2022' \r\n"
            + "AND ToString(DateTimePart('month', c.queueStatusChangeDateTime))= '4' \r\n"
            + "AND ToString(DateTimePart('day', c.queueStatusChangeDateTime))= '5' \r\n"
            + "AND c.targetInterventionQueueType='RPH'";

    List<MemberIntervention> search = MemberInterventionQueries.searchInterventionsByQuery(
        "EHP", query);

    // MemberIntervention memberIntervention = search.get(0);
    // System.out.println("from DB Expected " + memberIntervention);


    List<String> expected = search.stream().map(intervention -> intervention
        .getQueueStatusChangeDateTime())
        .collect(Collectors.toList());

    List<String> expectedChange = new ArrayList<>();

    for (String expect : expected) {

      StringTokenizer token = new StringTokenizer(expect, "/");
      int month = Integer.parseInt(token.nextToken());
      int day = Integer.parseInt(token.nextToken());
      int year = Integer.parseInt(token.nextToken());

      String format = (month < 10 ? "0" + month : month) + "/" + (day < 10 ? "0" + day : day) + "/"
          + year;
      expectedChange.add(format);
    }

    List<String> actual = interventionQueuePO.retrieveMemberInterventionColValues(
        DisplayColumns.DATE_TIME.toString());

    System.out.println("from UI Actual " + actual);
    System.out.println("from DB Expected " + expectedChange);

    int matchingCount = 0;

    matchingCount = countMatches(actual, expectedChange);
    System.out.println(
        " Number of matching records between actual from application and expected from cosmos DB are "
            + matchingCount);

    // assertThat(actual, Matchers.containsInAnyOrder(expectedChange.toArray()));
    assertThat("no matching records found between application and cosmos DB", matchingCount >= 1);



  }

  @Test
  @Order(2)
  @DisplayName("157333: Use the Search keyword text box and perform several searches on the Date & Time column for MED:: Defect 183006")
  public void validateDateTimeSearch_RHP_MED() throws Exception {



    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus BCBS Medicare");
    SeleniumPageHelperAndWaiter.pause(10000);

    String query =
        "SELECT concat(ToString(DateTimePart('month', c.queueStatusChangeDateTime)),\"/\",ToString(DateTimePart('day', c.queueStatusChangeDateTime)),\"/\",ToString(DateTimePart('yyyy', c.queueStatusChangeDateTime))) AS queueStatusChangeDateTime \r\n"
            + "FROM c where RegexMatch (c.memberId,\"[0-99]\",\"\") \r\n"
            + "AND c.type='intervention' \r\n"
            + "AND ToString(DateTimePart('yyyy', c.queueStatusChangeDateTime)) = '2023' \r\n"
            + "AND ToString(DateTimePart('month', c.queueStatusChangeDateTime))= '1' \r\n"
            + "AND ToString(DateTimePart('day', c.queueStatusChangeDateTime))= '16' \r\n"
            + "AND c.targetInterventionQueueType='RPH'";

    List<MemberIntervention> search = MemberInterventionQueries.searchInterventionsByQuery(
        "MED", query);

    // MemberIntervention memberIntervention = search.get(0);



    List<String> expected = search.stream().map(intervention -> intervention
        .getQueueStatusChangeDateTime())
        .collect(Collectors.toList());

    List<String> expectedChange = new ArrayList<>();

    for (String expect : expected) {

      StringTokenizer token = new StringTokenizer(expect, "/");
      int month = Integer.parseInt(token.nextToken());
      int day = Integer.parseInt(token.nextToken());
      int year = Integer.parseInt(token.nextToken());
      String format = (month < 10 ? "0" + month : month) + "/" + (day < 10 ? "0" + day : day) + "/"
          + year;
      expectedChange.add(format);

    }



    List<String> actual = interventionQueuePO.retrieveMemberInterventionColValues(
        DisplayColumns.DATE_TIME.toString());

    System.out.println("from UI Actual " + actual);
    System.out.println("from DB Expected " + expectedChange);


    int matchingCount = 0;

    matchingCount = countMatches(actual, expectedChange);
    System.out.println(
        " Number of matching records between actual from application and expected from cosmos DB are"
            + matchingCount);

    // assertThat(actual, Matchers.containsInAnyOrder(expectedChange.toArray()));
    assertThat("no matching records found between application and cosmos DB", matchingCount >= 1);



  }


  @Test
  @Order(3)
  @DisplayName("157333: Use the Search keyword text box and perform several searches on the Date & Time column for EXE:: Defect 183006")
  public void validateDateTimeSearch_RHP_EXE() throws Exception {



    SeleniumPageHelperAndWaiter.pause(1000);
    interventionQueuePO.selectATenant("Excellus Employee");
    SeleniumPageHelperAndWaiter.pause(10000);

    String query =
        "SELECT concat(ToString(DateTimePart('month', c.queueStatusChangeDateTime)),\"/\",ToString(DateTimePart('day', c.queueStatusChangeDateTime)),\"/\",ToString(DateTimePart('yyyy', c.queueStatusChangeDateTime))) AS queueStatusChangeDateTime \r\n"
            + "FROM c where RegexMatch (c.memberId,\"[0-99]\",\"\") \r\n"
            + "AND c.type='intervention' \r\n"
            + "AND ToString(DateTimePart('yyyy', c.queueStatusChangeDateTime)) = '2023' \r\n"
            + "AND ToString(DateTimePart('month', c.queueStatusChangeDateTime))= '1' \r\n"
            + "AND ToString(DateTimePart('day', c.queueStatusChangeDateTime))= '16' \r\n"
            + "AND c.targetInterventionQueueType='RPH'";

    List<MemberIntervention> search = MemberInterventionQueries.searchInterventionsByQuery(
        "EXE", query);

    // MemberIntervention memberIntervention = search.get(0);



    List<String> expected = search.stream().map(intervention -> intervention
        .getQueueStatusChangeDateTime())
        .collect(Collectors.toList());

    List<String> expectedChange = new ArrayList<>();

    for (String expect : expected) {

      StringTokenizer token = new StringTokenizer(expect, "/");
      int month = Integer.parseInt(token.nextToken());
      int day = Integer.parseInt(token.nextToken());
      int year = Integer.parseInt(token.nextToken());
      String format = (month < 10 ? "0" + month : month) + "/" + (day < 10 ? "0" + day : day) + "/"
          + year;
      expectedChange.add(format);

    }



    List<String> actual = interventionQueuePO.retrieveMemberInterventionColValues(
        DisplayColumns.DATE_TIME.toString());

    System.out.println("from UI Actual " + actual);
    System.out.println("from DB Expected " + expectedChange);


    int matchingCount = 0;

    matchingCount = countMatches(actual, expectedChange);
    System.out.println(
        " Number of matching records between actual from application and expected from cosmos DB are"
            + matchingCount);

    // assertThat(actual, Matchers.containsInAnyOrder(expectedChange.toArray()));
    assertThat("no matching records found between application and cosmos DB", matchingCount >= 1);



  }

}
