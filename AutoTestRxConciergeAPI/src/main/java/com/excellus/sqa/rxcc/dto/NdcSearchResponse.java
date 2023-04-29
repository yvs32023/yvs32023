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

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 05/20/2022
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NdcSearchResponse extends AbstractJsonDTO<NdcSearchResponse>
{

	private LinkedList<NdcSearch> drugs;
	private List <String> ndcs;
	
	@Override
	public List<DynamicNode> compare(NdcSearchResponse dto) {
		// not validation required
		return null;
	}
	
	
	/*
	 * Setter / Getter
	 */
	public LinkedList<NdcSearch> getDrugs() {
		return drugs;
	}
	public void setDrugs(LinkedList<NdcSearch> drugs) {
		this.drugs = drugs;
	}
	public List<String> getNdcs() {
		return ndcs;
	}
	public void setNdcs(List<String> ndcs) {
		this.ndcs = ndcs;
	}
}
