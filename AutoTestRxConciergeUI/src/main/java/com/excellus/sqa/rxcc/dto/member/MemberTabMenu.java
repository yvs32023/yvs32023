/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto.member;

/**
 * 
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 09/06/2022
 */
public enum MemberTabMenu 
{
	INTERVENTIONS			("Interventions"),
	PRESCRIPTION_CLAIMS		("Prescription Claims"),
	PROVIDERS				("Providers"),
	PHARMACIES				("Pharmacies"),
	CORRESPONDENCE			("Correspondence"),	 
	NOTES					("Notes"),	
	LEGACY_INTERVENTION		("Legacy Intervention"),
	LEGACY_CORRESPONDENCE	("Legacy Correspondence"); 
 
	private String item;
	
	private MemberTabMenu(String item)
	{
		this.item = item;
	}
	
	@Override
	public String toString() {
		return item;
	}
}

