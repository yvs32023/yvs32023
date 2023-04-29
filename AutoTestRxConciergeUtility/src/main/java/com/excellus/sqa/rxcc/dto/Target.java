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
 * @since 06/07/2022
 */
@JsonPropertyOrder({"drugSelectionFilter","claimsLookbackInitial","claimsLookbackOngoing",
	"selectedDrugsNDC","static","showOnFax"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Target extends AbstractJsonDTO<Target>
{

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
	public Target() {

	}

	/**
	 * Constructor
	 * @param drugSelectionFilter
	 * @param claimsLookbackInitial
	 * @param claimsLookbackOngoing
	 * @param selectedDrugsNDC
	 * @param staticVal
	 * @param showOnFax
	 */
	public Target(DrugSelectionFilter drugSelectionFilter, Integer claimsLookbackInitial, Integer claimsLookbackOngoing,
			List<String> selectedDrugsNDC, Boolean staticVal, Boolean showOnFax) 
	{
		this.drugSelectionFilter = drugSelectionFilter;
		this.claimsLookbackInitial = claimsLookbackInitial;
		this.claimsLookbackOngoing = claimsLookbackOngoing;
		this.selectedDrugsNDC = selectedDrugsNDC;
		this.staticVal = staticVal;
		this.showOnFax = showOnFax;
	}



	/*
	 * Validations
	 */

	/**
	 * Compare two objects
	 * 
	 * @param target
	 * @return
	 */
	public List<DynamicNode> compare(Target target)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicContainer("Drug Selection Filter", drugSelectionFilter.compare(target.getDrugSelectionFilter())));
		tests.add(dynamicTest("claimsLookbackInitial [" + claimsLookbackInitial + "]", () -> assertEquals(claimsLookbackInitial, target.getClaimsLookbackInitial(), getApiInfo(target))));
		tests.add(dynamicTest("claimsLookbackOngoing [" + claimsLookbackOngoing + "]", () -> assertEquals(claimsLookbackOngoing, target.getClaimsLookbackOngoing(), getApiInfo(target))));
		tests.add(dynamicTest("selectedDrugsNDC [" + selectedDrugsNDC + "]", () -> assertThat(getApiInfo(target), target.getSelectedDrugsNDC(), containsInAnyOrder(selectedDrugsNDC.toArray(new String[selectedDrugsNDC.size()])) ) ));
		tests.add(dynamicTest("static [" + staticVal + "]", () -> assertEquals(staticVal, target.isStaticVal(), getApiInfo(target))));// TODO JUNit show the staticVal to be null when it's not. Need to investigate
		tests.add(dynamicTest("showOnFax [" + showOnFax + "]", () -> assertEquals(showOnFax, target.getShowOnFax(), getApiInfo(target))));

		return tests;
	}

	/*
	 * Helper methods
	 */

	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof Target)
		{
			Target target = (Target) obj;

			if ( selectedDrugsNDC.equals(target.getSelectedDrugsNDC()) )
				return true;
		}

		return false;
	}

	/*
	 * Setter / Getter
	 */

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


}


