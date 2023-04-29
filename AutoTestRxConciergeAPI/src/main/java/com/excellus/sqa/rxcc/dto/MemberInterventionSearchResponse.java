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
 * @since 10/13/2022
 */
public class MemberInterventionSearchResponse extends AbstractJsonDTO<MemberInterventionSearchResponse>
{

	private int count;
	private LinkedList<SearchMemberIntervention> interventions;

	@Override
	public List<DynamicNode> compare(MemberInterventionSearchResponse dto) {
		// not validation required
		return null;
	}

	/*
	 * Setter / Getter
	 */
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public LinkedList<SearchMemberIntervention> getInterventions() {
		return interventions;
	}

	public void setInterventions(LinkedList<SearchMemberIntervention> interventions) {
		this.interventions = interventions;
	}
}
