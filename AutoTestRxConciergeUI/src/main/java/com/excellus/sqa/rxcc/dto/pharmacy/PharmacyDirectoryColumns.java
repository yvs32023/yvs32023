/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto.pharmacy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 06/03/2022
 */
public enum PharmacyDirectoryColumns 
{
    NPI						("NPI"),
    PHARMACY_NAME			("Pharmacy Name"),
    TAXONOMY_DESCRIPTION	("Taxonomy Description"),
    PHONE_NUMBER			("Phone Number"),
    CITY					("City"),
    STATE					("State"),
    STATUS					("Status"),
    ;
    private String columnName;

    private PharmacyDirectoryColumns(String columnName) {
        this.columnName = columnName;
    }

    /**
     * This method will return the list of columns
     */
    public final static List<String> getPharmacyDirectoryColumns() {

        return Stream.of(PharmacyDirectoryColumns.values())
                .map(column -> column.toString())
                .collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        return this.columnName;
    }
}
