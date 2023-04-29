/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

/**
 * DTO for provider note
 * 
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/03/2022
 */
public class ProviderNote extends Item {
	
	private String npi;
	
	private String type;
	
	private boolean alert;
	
	private boolean hidden;
	
	private boolean tombStoned;
	
	private String note;
	
	private String createUser;
	
	private String createDateTime;
	
	
	/*
	 * Setter / Getter
	 */

	public String getNpi() {
		return npi;
	}

	public void setNpi(String npi) {
		this.npi = npi;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAlert() {
		return alert;
	}

	public void setAlert(boolean alert) {
		this.alert = alert;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isTombStoned() {
		return tombStoned;
	}

	public void setTombStoned(boolean tombStoned) {
		this.tombStoned = tombStoned;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(String createDateTime) {
		this.createDateTime = createDateTime;
	}
	
	
}

