/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.DynamicNode;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 04/03/2022
 */
public class SearchPharmacyResponse extends AbstractJsonDTO<SearchPharmacyResponse>
{

	private LinkedList<PharmaciesSearch> pharmacies;
	private int totalResults;

	@Override
	public List<DynamicNode> compare(SearchPharmacyResponse dto) {
		// not validation required
		return null;
	}
	
	
	/*
	 * Setter / Getter
	 */
	public LinkedList<PharmaciesSearch> getPharmacies() {
		return pharmacies;
	}

	public void setPharmacies(LinkedList<PharmaciesSearch> pharmacies) {
		this.pharmacies = pharmacies;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}

	
	
}
