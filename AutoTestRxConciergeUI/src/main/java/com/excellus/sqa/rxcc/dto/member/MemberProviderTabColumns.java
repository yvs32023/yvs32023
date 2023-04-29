/**
 * 
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto.member;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Neeru Tagore (ntagore)
 * @since 02/13/2023
 */
public enum MemberProviderTabColumns {

    NAME_NPI                        ("Name(NPI)"),
    PHONE                           ("Phone"),
    FAX                             ("Fax"),
    CITY                            ("City"),
    STATE                           ("State"),
    POSTAL_CODE                     ("Postal Code"),
    NUMBER_OF_LOCATIONS             ("# of Locations"),
    TAXONOMY                        ("Taxonomy"),
    PROVIDER_STATUS                 ("Provider Status"),
    PROVIDER_VERIFIED                ("Provider Verified")    
    ;

    private String columnName;

    private MemberProviderTabColumns(String columnName) {
        this.columnName = columnName;
    }

    /**
     * This method will return the list of columns
     */
    public static List<String> getProviderColumnList() {

        return Stream.of(MemberProviderTabColumns.values())
                .map(column -> column.toString())
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return this.columnName;
    }
















}
