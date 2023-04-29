/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

import java.util.List;

import org.junit.jupiter.api.DynamicNode;

/**
 * Generic JSON object that has single string property
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 04/13/2022
 */
public class GenericStringItem extends AbstractJsonDTO<GenericStringItem>
{

	private String item;
	
	private String[] items;
	
	/*
     * Validations
     */
    
	@Override
	public List<DynamicNode> compare(GenericStringItem dto) {
		// Do nothing - no validation is required
		return null;
	}
	
	/*
	 * Setter and Getter 
	 */

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String[] getItems() {
		return items;
	}

	public void setItems(String[] items) {
		this.items = items;
	}
	
	
}

