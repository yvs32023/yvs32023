/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto.provider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 06/30/2022
 */
public enum ProviderDirectoryColumns 
{
    NPI                     ("NPI" ,           "npi" ),
    FIRST_NAME              ("First Name" ,       "firstName"),
    LAST_NAME               ("Last Name" ,         "lastName"),
    TAXONOMY_DESCRIPTION    ("Taxonomy Description" ,  "taxonomyDescr"),
    PHONE_NUMBER            ("Phone Number" , "phoneNumber"),
    FAX_NUMBER              ("Fax Number" , "faxNumber"),
    CITY                    ("City" ,        "city"),
    STATE                   ("State" ,      "state"),
    NUMBER_OF_LOCATIONS     ("# of Locations" ,  "officeLocations"),    
    STATUS                  ("Status" ,         "statusInd"),
    FAX_VERIFIED            ("Fax Verified" ,    "faxVerified"),
    
    ;
    

    private String columnName;
    private String cosmosDbField;

    private ProviderDirectoryColumns(String columnName, String cosmosDbField) {
        this.columnName = columnName;
        this.cosmosDbField = cosmosDbField;
    }

    /**
     * This method will return the list of columns
     */
    public final static List<String> getProviderDirectoryColumns() {

        return Stream.of(ProviderDirectoryColumns.values())
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