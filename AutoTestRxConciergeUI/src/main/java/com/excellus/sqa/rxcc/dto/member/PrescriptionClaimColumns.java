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
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 01/24/2023
 */
public enum PrescriptionClaimColumns {
    DATE                        ("Date"),
    DRUG_NAME                   ("Drug Name"),
    STRENGTH                    ("Strength"),
    DOSAGE_FORM                 ("Dosage Form"),
    PROVIDER_NPI                ("Provider (NPI)"),
    PHARMACY_NPI                ("Pharmacy (NPI)"),
    QTY                         ("QTY"),
    DS                          ("DS"),
    STATUS                      ("Status"),
    CLAIM_ID                    ("Claim ID")    
    ;
    private String columnName;
    
    private PrescriptionClaimColumns(String columnName) {
        this.columnName = columnName;
    }
  
    /**
     * This method will return the list of columns
     */
    public static List<String> getRxColumnList() {

        return Stream.of(PrescriptionClaimColumns.values())
                .map(column -> column.toString())
                .collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        return this.columnName;
    }

}










