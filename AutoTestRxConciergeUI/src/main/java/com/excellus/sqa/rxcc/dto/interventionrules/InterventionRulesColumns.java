/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
/**
 * 
 */
package com.excellus.sqa.rxcc.dto.interventionrules;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 08/23/2022
 */
/**
 * @author hzia
 *
 */
public enum InterventionRulesColumns
{
    RULE_NAME               ("Rule Name" ,     "ruleName" ),
    CREATE_DATE             ("Create Date" ,   "createDate"),
    MODIFY_DATE             ("Modify Date" ,   "modifyDate"),
    STATUS                  ("Status" ,        "statusInd"),
    FORMULARIES_ASSIGNED    ("Formularies Assigned" ,  "formulariesAssigned"),
    TENANTS_ASSIGNED        ("Tenants Assigned" , "tenantsAssigned"),
    RUN_DAILY               ("Run Daily" ,     "runDaily"),    
    
    ;
    

    private String columnName;
    private String cosmosDbField;

    private InterventionRulesColumns(String columnName, String cosmosDbField) {
        this.columnName = columnName;
        this.cosmosDbField = cosmosDbField;
    }

    /**
     * This method will return the list of columns
     */
    public final static List<String> getInterventionRulesColumns() {

        return Stream.of(InterventionRulesColumns.values())
                .map(column -> column.toString())
                .collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        return this.columnName;
    }
    
    
    public String getUIColumnName() {
        return this.columnName;
    }
    
    public String getCosmosDbField() {
        return this.cosmosDbField;
    }
}

