/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

/**
 * Entities
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/27/2022
 */
public enum Entity {
	
	TENANT			("Tenant", 			"tenantPage"),
	MEMBER			("Member",			"memberPage"),
	PROVIDER		("Provider",		"providerPage"),
	PHARMACY		("Pharmacy",		"pharmaciesPage"),
	INTERVENTION	("Intervention",	"interventionPage");

	private final String type;
	private final String uiPageConfigBeanName;
	
	Entity(String type, String uiPageConfigBeanName) {
		this.type = type;
		this.uiPageConfigBeanName = uiPageConfigBeanName;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getUiPageConfigBeanName() {
		return this.uiPageConfigBeanName;
	}
	
	@Override
	public String toString() {
		return this.type;
	}
}

