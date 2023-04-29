/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
/**
 * 
 */
package com.excellus.sqa.rxcc.dto.member;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 10/04/2022
 */
/**
 * @author hzia
 *
 */
public enum MemberInterventionsColumns {
	
	    CREATED_DATE                     ("Created Date" ,           "createdDate" ),
	    STATUS_CHANGE_DATE              ("Status Change Date" ,       "statusChangeDate"),
	    STATUS              ("Status" ,         "status"),
	    PLAN_COST   ("Plan Cost" ,  "planCost"),
	    MEMBER_COST            ("Member Cost" , "memberCost"),
	    TARGET_DRUG              ("Target Drug" , "targetDrug"),
	    PROVIDER                   ("Provider" ,        "provider"),
	    
	    ;
	    

	    private String columnName;
	    private String cosmosDbField;

	    private MemberInterventionsColumns(String columnName, String cosmosDbField) {
	        this.columnName = columnName;
	        this.cosmosDbField = cosmosDbField;
	    }

	    /**
	     * This method will return the list of columns
	     */
	    public final static List<String> getMemberInterventionsColumns() {

	        return Stream.of(MemberInterventionsColumns.values())
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


