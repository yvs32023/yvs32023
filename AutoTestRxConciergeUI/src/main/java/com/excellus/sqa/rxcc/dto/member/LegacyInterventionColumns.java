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
 * @author Neeru Tagore (ntagore)
 * @since 09/13/2022
 */
public enum LegacyInterventionColumns {
    
    INTERVENTION_DATE      ("Intervention Date"),
    DRUG_FOCUS             ("Drug Focus"),
    REPLY                   ("Reply"),
    INTERVENTION_NAME    ("Intervention Name"),
    OUTCOME_DOCUMENTED_BY           ("Outcome Documented By"),
    INTERVENTION_MADE_BY           ("Intervention Made By"),
    CONVERSION_DATE           ("Conversion Date"); 
 
    private String columnName;
    
    private LegacyInterventionColumns(String columnName)
    {
        this.columnName = columnName;
    }
    
    /**
     * Retrieve the column names
     * @return list of column names
     */
    public static List<String> getLegacyInterventionColumns() {

        return Stream.of(LegacyInterventionColumns.values())
                .map(column -> column.toString())
                .collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        return columnName;
    }

}
