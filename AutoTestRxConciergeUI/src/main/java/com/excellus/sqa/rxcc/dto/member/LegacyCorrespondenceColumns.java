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
 * @since 09/06/2022
 */
public enum LegacyCorrespondenceColumns 
{
	CALL_DATE_TIME		("Call Date Time"),
	CALL_TYPE			("Call Type"),
	CALL_OUTCOME		("Call Outcome"),
	POINT_OF_CONTACT	("Point of Contact"),
	CRETED_BY			("Created By"); 
 
	private String columnName;
	
	private LegacyCorrespondenceColumns(String columnName)
	{
		this.columnName = columnName;
	}
	
	/**
	 * Retrieve the column names
	 * @return list of column names
	 */
    public static List<String> getLegacyCorrespondenceColumns() {

        return Stream.of(LegacyCorrespondenceColumns.values())
                .map(column -> column.toString())
                .collect(Collectors.toList());
    }
	
	@Override
	public String toString() {
		return columnName;
	}
	
}