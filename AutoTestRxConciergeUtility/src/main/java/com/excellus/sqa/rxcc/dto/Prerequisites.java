/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicNode;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 06/08/2022
 */
@JsonPropertyOrder({ "condition","drugSelectionFilter","claimsLookbackInitial","claimsLookbackOngoing",
	"selectedDrugsNDC","staticVal","showOnFax"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Prerequisites extends AbstractJsonDTO<Prerequisites>
{

	private Condition condition;  //enum

	private DrugSelectionFilter drugSelectionFilter;

	private Integer claimsLookbackInitial;

	private Integer claimsLookbackOngoing;

	private List <String> selectedDrugsNDC;

	@JsonProperty("static")
	private Boolean staticVal;

	private Boolean showOnFax;
	
	/**
	 * Default constructor
	 */
	public Prerequisites() {

	}

	/**
	 * Constructor
	 * @param condition
	 * @param drugSelectionFilter
	 * @param claimsLookbackInitial
	 * @param claimsLookbackOngoing
	 * @param selectedDrugsNDC
	 * @param staticVal
	 * @param showOnFax
	 */
	public  Prerequisites(Condition condition, DrugSelectionFilter drugSelectionFilter, Integer claimsLookbackInitial,
			Integer claimsLookbackOngoing, List<String> selectedDrugsNDC, Boolean staticValue, Boolean showOnFax) 
	{
		this.condition = condition;
		this.drugSelectionFilter = drugSelectionFilter;
		this.claimsLookbackInitial = claimsLookbackInitial;
		this.claimsLookbackOngoing = claimsLookbackOngoing;
		this.selectedDrugsNDC = selectedDrugsNDC;
		this.staticVal = staticValue;
		this.showOnFax = showOnFax;
	}


	/*
	 * Validations
	 */

	/**
	 * Compare prerequisites properties defined in the schema
	 * 
	 * @param prerequisites {@link Prerequisites} to compare with
	 * @return test results of comparison
	 */
	public List<DynamicNode> compare(Prerequisites prerequisites)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();


		tests.add(dynamicTest("condition [" + condition + "]", () -> assertEquals(condition, prerequisites.getCondition(), getApiInfo(prerequisites))));
		tests.add(dynamicContainer("Drug Selection Filter", drugSelectionFilter.compare(prerequisites.getDrugSelectionFilter()) ));
		tests.add(dynamicTest("claimsLookbackInitial [" + claimsLookbackInitial + "]", () -> assertEquals(claimsLookbackInitial, prerequisites.getClaimsLookbackInitial(), getApiInfo(prerequisites))));
		tests.add(dynamicTest("claimsLookbackOngoing [" + claimsLookbackOngoing + "]", () -> assertEquals(claimsLookbackOngoing, prerequisites.getClaimsLookbackOngoing(), getApiInfo(prerequisites))));
		tests.add(dynamicTest("selectedDrugsNDC [" + selectedDrugsNDC + "]", () -> assertThat(getApiInfo(prerequisites), prerequisites.getSelectedDrugsNDC(), containsInAnyOrder(selectedDrugsNDC.toArray(new String[selectedDrugsNDC.size()])) ) ));
		tests.add(dynamicTest("static [" + staticVal + "]", () -> assertEquals(staticVal, prerequisites.isStaticVal(), getApiInfo(prerequisites))));
		tests.add(dynamicTest("showOnFax [" + showOnFax + "]", () -> assertEquals(showOnFax, prerequisites.getShowOnFax(), getApiInfo(prerequisites))));

		return tests;
	}


	/*
	 * Helper methods
	 */

	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof Prerequisites)
		{
			Prerequisites prerequisites = (Prerequisites) obj;

			if ( selectedDrugsNDC.equals(prerequisites.getSelectedDrugsNDC()) )
				return true;
		}

		return false;
	}


	/*
	 * Setter / Getter
	 */

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public DrugSelectionFilter getDrugSelectionFilter() {
		return drugSelectionFilter;
	}

	public void setDrugSelectionFilter(DrugSelectionFilter drugSelectionFilter) {
		this.drugSelectionFilter = drugSelectionFilter;
	}

	public Integer getClaimsLookbackInitial() {
		return claimsLookbackInitial;
	}

	public void setClaimsLookbackInitial(Integer claimsLookbackInitial) {
		this.claimsLookbackInitial = claimsLookbackInitial;
	}

	public Integer getClaimsLookbackOngoing() {
		return claimsLookbackOngoing;
	}

	public void setClaimsLookbackOngoing(Integer claimsLookbackOngoing) {
		this.claimsLookbackOngoing = claimsLookbackOngoing;
	}

	public List<String> getSelectedDrugsNDC() {
		return selectedDrugsNDC;
	}

	public void setSelectedDrugsNDC(List<String> selectedDrugsNDC) {
		this.selectedDrugsNDC = selectedDrugsNDC;
	}

	public Boolean isStaticVal() {
		return staticVal;
	}

	public void setStaticVal(Boolean staticVal) {
		this.staticVal = staticVal;
	}

	public Boolean getShowOnFax() {
		return showOnFax;
	}

	public void setShowOnFax(Boolean showOnFax) {
		this.showOnFax = showOnFax;
	}
	//Enum Condition
	public enum Condition{
		any,all,notAny,notAll;	
	}

}
