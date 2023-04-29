/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto.member;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 05/18/2022
 */
public enum CorrespondenceColumns 
{
    TYPE						("Type"),
    CONTACT_NAME				("Contact Name"),
    CONTACT_TITLE				("Contact Title"),
    CREATED_BY					("Created By"),
    CREATED_DATE				("Created Date"),
    EDITED_DATE					("Edited Date"),
    OUTCOME						("Outcome"),
    TARGET_DRUG					("Target Drug"),
    RECIPIENT_NAME				("Recipient Name"),
    ;
    private String columnName;
    
    private CorrespondenceColumns(String columnName) {
        this.columnName = columnName;
    }
  
    /**
     * This method will return the list of columns
     */
    public List<String> getCorrespondenceColumnList() {

        return Stream.of(CorrespondenceColumns.values())
                .map(column -> column.toString())
                .collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        return this.columnName;
    }
}