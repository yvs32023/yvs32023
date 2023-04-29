/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicNode;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 04/18/2022
 */
public class FieldSearchPharmacyResponse extends AbstractJsonDTO<FieldSearchPharmacyResponse> {
	
	private ArrayList<String> searchResults;
	private boolean moreResults;
	
	@Override
	public List<DynamicNode> compare(FieldSearchPharmacyResponse dto) {
		// not validation required
		return null;
	}

	/*
	 * Setter / Getter
	 */
	
	public ArrayList<String> getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(ArrayList<String> searchResults) {
		this.searchResults = searchResults;
	}

	public boolean isMoreResults() {
		return moreResults;
	}

	public void setMoreResults(boolean moreResults) {
		this.moreResults = moreResults;
	}

}
