/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto.formulary;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 09/23/2022
 */
public enum FormularyGroupsColumns {
    
    TENANT_ID                     ("Tenant ID"),       
    TENANT_NAME                    ("Tenant Name"),  
    GROUP_ID                      ("Group ID"),       
    RXCC_GROUP_NAME               ("RxCC Group Name"),
    EMPLOYEE_GROUP_INDICATOR      ("Employee Group Indicator"),  
    EFFECTIVE_DATE                ("Effective Date"),    
    TERMINATION_DATE              ("Termination Date"),    
    FORMULARY_CODE                 ("Formulary Code"),  
    CREATED_USER                  ("Created By"),    
    CREATE_DATE                   ("Create Date"),  
    LAST_MODIFIED_USER            ("Last Modified user"),   
    LAST_UPDATED                  ("Last Updated"),
    ACTION                        ("Actions")  
    ;

    private String columnName;

    private FormularyGroupsColumns(String columnName ) {
        this.columnName = columnName;
    }

    /**
     * This method will return the list of columns
     */
    public final static List<String> getFormularyGroupsColumns() {

        return Stream.of(FormularyGroupsColumns.values())
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

    }

    



