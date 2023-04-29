/**
 * 
 * @copyright 2023 Excellus BCBS All rights reserved.
 * 
 */
/**
 * 
 */
package com.excellus.sqa.rxcc.dto.intervention;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * AutoTestRxConciergeUI/src/main/java/com/excellus/sqa/rxcc/dto/interventions
 * 
 * @author Vijaya Sekhar Yeleswarapu (vyeleswa)
 * @since 01/16/2023
 */

public enum DisplayColumns {
  DATE_TIME("Date & Time"),
  STATUS("Status"),
  GROUP_NAME("RxCC Group Name"),
  PLAN_COST("Plan Cost"),
  MEMBER_COST("Member Cost"),
  MEMBER_ID("Member Id"),
  MEMBER_NAME("Member Name"),
  MEMBER_DOB("Member DOB"),
  TARGET_DRUG_NAME("Target Drug Name"),
  PROVIDER_NAME("Provider Name"),
  PROVIDER_PHONE_NUMBER("Provider Phone Number"),
  LAST_UPDATED_BY("Last Updated By"),
  USER_NAME("Username"),
  ;

  private String columnName;


  private DisplayColumns(String columnName) {
    this.columnName = columnName;
  }

  /**
   * This method will return the list of columns
   */
  public static List<String> getDisplayColumnsList() {

    return Stream.of(DisplayColumns.values())
        .map(column -> column.toString()).collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return this.columnName;
  }

}
