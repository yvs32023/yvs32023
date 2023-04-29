/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.dto;

import java.util.List;

import org.junit.jupiter.api.DynamicNode;

/**
 *
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 03/01/2022
 */
public class MemberSearchResponse extends AbstractJsonDTO<MemberSearchResponse> {

	private int totalCount;
	private int count;
	private List<MemberSearch> members;

	@Override
	public List<DynamicNode> compare(MemberSearchResponse dto) {
		// not validation required
		return null;
	}

	/*
	 * Setter / Getter
	 */

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<MemberSearch> getMembers() {
		return members;
	}

	public void setMembers(List<MemberSearch> members) {
		this.members = members;
	}
}
