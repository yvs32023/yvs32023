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
 * @since 03/21/2022
 */
public class SearchProviderResponse extends AbstractJsonDTO<SearchProviderResponse>
{
 	private LinkedList<SearchProvider> providers;
	private int totalResults;
	
	@Override
	public List<DynamicNode> compare(SearchProviderResponse dto) {
		// not validation required
		return null;
	}
	
	
	/*
	 * Setter / Getter
	 */
	public LinkedList<SearchProvider> getProviders() {
	return providers;
	}

	public void setProviders(LinkedList<SearchProvider> providers) {
		this.providers = providers;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}
	
	

}
