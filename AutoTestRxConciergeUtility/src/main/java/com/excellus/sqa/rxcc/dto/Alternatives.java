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
"selectedDrugsNDC","static","alternativeCriteriaText","showOnFax"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Alternatives extends AbstractJsonDTO<Alternatives>
{


	private DrugSelectionFilter drugSelectionFilter;

	private Integer claimsLookbackInitial;

	private Integer claimsLookbackOngoing;

	private List <String> selectedDrugsNDC;

	@JsonProperty("static")
	private Boolean staticVal;

	private String alternativeCriteriaText;

	private Boolean showOnFax;

	/**
	 * Default constructor
	 */
	public Alternatives() {

	}
	
	/**
	 * Constructor
	 * @param drugSelectionFilter
	 * @param claimsLookbackInitial
	 * @param claimsLookbackOngoing
	 * @param selectedDrugsNDC
	 * @param staticVal
	 * @param alternativeCriteriaText
	 * @param showOnFax
	 */
	public Alternatives(DrugSelectionFilter drugSelectionFilter, Integer claimsLookbackInitial,
			Integer claimsLookbackOngoing, List<String> selectedDrugsNDC, Boolean staticVal,
			String alternativeCriteriaText, Boolean showOnFax) 
	{

		this.drugSelectionFilter = drugSelectionFilter;
		this.claimsLookbackInitial = claimsLookbackInitial;
		this.claimsLookbackOngoing = claimsLookbackOngoing;
		this.selectedDrugsNDC = selectedDrugsNDC;
		this.staticVal = staticVal;
		this.alternativeCriteriaText = alternativeCriteriaText;
		this.showOnFax = showOnFax;
	}


	/*
	 * Helper methods
	 */


	/**
	 * Compare alternatives properties defined in the schema
	 * 
	 * @param alternatives {@link Alternatives} to compare with
	 * @return test results of comparison
	 */
	public List<DynamicNode> compare(Alternatives alternatives)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicContainer("Drug Selection Filter",  drugSelectionFilter.compare(alternatives.getDrugSelectionFilter()) ));
		tests.add(dynamicTest("claimsLookbackInitial [" + claimsLookbackInitial + "]", () -> assertEquals(claimsLookbackInitial, alternatives.getClaimsLookbackInitial(), getApiInfo(alternatives))));
		tests.add(dynamicTest("claimsLookbackOngoing [" + claimsLookbackOngoing + "]", () -> assertEquals(claimsLookbackOngoing, alternatives.getClaimsLookbackOngoing(), getApiInfo(alternatives))));
		tests.add(dynamicTest("selectedDrugsNDC [" + selectedDrugsNDC + "]", () -> assertThat(getApiInfo(alternatives), alternatives.getSelectedDrugsNDC(), containsInAnyOrder(selectedDrugsNDC.toArray(new String[selectedDrugsNDC.size()])) ) ));
		tests.add(dynamicTest("static [" + staticVal + "]", () -> assertEquals(staticVal, alternatives.isStaticVal(), getApiInfo(alternatives))));
		tests.add(dynamicTest("alternativeCriteriaText [" + alternativeCriteriaText + "]", () -> assertEquals(alternativeCriteriaText, alternatives.getAlternativeCriteriaText(), getApiInfo(alternatives))));
		tests.add(dynamicTest("showOnFax [" + showOnFax + "]", () -> assertEquals(showOnFax, alternatives.getShowOnFax(), getApiInfo(alternatives))));
		
		return tests;
	}
	
	
	/*
	 * Helper methods
	 */
	
	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof Alternatives)
		{
			Alternatives alternatives = (Alternatives) obj;

			if ( selectedDrugsNDC.equals(alternatives.getSelectedDrugsNDC()) )
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

	public String getAlternativeCriteriaText() {
		return alternativeCriteriaText;
	}

	public void setAlternativeCriteriaText(String alternativeCriteriaText) {
		this.alternativeCriteriaText = alternativeCriteriaText;
	}

	public Boolean getShowOnFax() {
		return showOnFax;
	}

	public void setShowOnFax(Boolean showOnFax) {
		this.showOnFax = showOnFax;
	}
}
