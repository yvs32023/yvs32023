package com.excellus.sqa.rxcc.dto.formulary;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 10/27/2022
 */
public enum FormularyColumns 
{
    FORMULARY_CODE                     ("Formulary Code",         "formularyCode"),
    FORMULARY_DESCRIPTION              ("Formulary Description",  "formularyDescription"),
    CREATED_BY                         ("Created By",             "createdBy"),
    CREATE_DATE                        ("Create Date",             "createdDateTime"),
    MODIFIED_BY                        ("Modified By",            "lastUpdatedBy"),
    MODIFIED_DATE                      ("Modified Date",         "lastUpdatedDateTime")
    ;
    private String columnName;
    private String cosmosDbField;

    private FormularyColumns(String columnName, String cosmosDbField) {
        this.columnName = columnName;
        this.cosmosDbField = cosmosDbField;
    }

    /**
     * This method will return the list of columns
     */
    public final static List<String> getFormularyColumns() {

        return Stream.of(FormularyColumns.values())
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