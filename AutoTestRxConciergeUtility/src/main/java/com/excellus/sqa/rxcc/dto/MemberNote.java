/**
 *
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 *
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;

import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DTO for Member Note
 *
 * @author Manish Sharma (msharma)
 * @since 03/01/2022
 */
@JsonPropertyOrder({ "memberId", "type", "alert", "hidden", "tombStoned", "note", "createUser", "createDateTime", "lastUpdatedBy", "lastUpdated", "id", "ver"})
public class MemberNote extends Item
{
	private static final Logger logger = LoggerFactory.getLogger(MemberNote.class);

	final String SCHEMA_FILENAME = "classpath:member.note.schema.json";

	@JsonIgnore
	static File jsonSchemaFile = null;

	private String memberId;

	private String type;

	private boolean alert;

	private boolean hidden;

	private boolean tombStoned;

	private String note;

	private String createUser;

	private String createDateTime;

	public MemberNote()
	{
		if ( jsonSchemaFile == null )
		{
			jsonSchemaFile = super.setJsonSchemaFile(SCHEMA_FILENAME);
		}
	}

	/*
	 * Validations
	 */

	/**
	 * Compare two objects
	 *
	 * @param memberNote {@link MemberNote} to compare with this instance
	 * @return list of test
	 */
	public List<DynamicNode> compare(MemberNote memberNote)
	{
		List<DynamicNode> tests = new ArrayList<>();

		tests.add(dynamicTest("memberId: [" + memberId + "]", () -> assertEquals(memberId, memberNote.getMemberId(), getApiInfo(memberNote))));
		tests.add(dynamicTest("type: [" + type + "]", () -> assertEquals(type, memberNote.getType(), getApiInfo(memberNote))));
		tests.add(dynamicTest("note: [" + note + "]", () -> assertEquals(note, memberNote.getNote(), getApiInfo(memberNote))));
		tests.add(dynamicTest("alert: [" + alert + "]", () -> assertEquals(alert, memberNote.isAlert(), getApiInfo(memberNote))));
		tests.add(dynamicTest("hidden: [" + hidden + "]", () -> assertEquals(hidden, memberNote.isHidden(), getApiInfo(memberNote))));
		tests.add(dynamicTest("tombStoned: [" + tombStoned + "]", () -> assertEquals(tombStoned, memberNote.isTombStoned(), getApiInfo(memberNote))));
		tests.add(dynamicTest("createUser: [" + createUser + "]", () -> assertEquals(createUser, memberNote.getCreateUser(), getApiInfo(memberNote))));

		// GC (03/14/22) Validate the created date is within the limit
		if ( StringUtils.isNotBlank(createDateTime) && StringUtils.isNotBlank(memberNote.getCreateDateTime()) )
		{
			tests.add(compareDates("createDateTime: [" + createDateTime + "]", createDateTime, memberNote.getCreateDateTime(),
					new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, getApiInfo(memberNote)));
		}
		else
		{
			tests.add(dynamicTest("createDateTime: [" + createDateTime + "]", () -> assertEquals(createDateTime, memberNote.getCreateDateTime(), getApiInfo(memberNote))));
		}

		// Perform custom validation on the item

		if ( StringUtils.isNotBlank(lastUpdated) && StringUtils.isNotBlank(memberNote.getLastUpdated()) )
		{
			tests.add(compareDates("lastUpdated: [" + lastUpdated + "]", lastUpdated, memberNote.getLastUpdated(),
					new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, getApiInfo(memberNote)));
		}
		else
		{
			tests.add(dynamicTest("lastUpdated: [" + lastUpdated + "]", () -> assertEquals(lastUpdated, memberNote.getLastUpdated(), getApiInfo(memberNote))));
		}

		tests.add(dynamicTest("lastUpdatedBy: [" + lastUpdatedBy + "]", () -> assertEquals(lastUpdatedBy, memberNote.getLastUpdatedBy(), getApiInfo(memberNote))));
		tests.add(dynamicTest("ver: [" + ver + "]", () -> assertEquals(ver, memberNote.getVer(), getApiInfo(memberNote))));

		// Validate id value if provided
		if ( StringUtils.isNotBlank(id) )
		{
			tests.add(dynamicTest("id [" + id + "]", () -> assertEquals(id, memberNote.getId(), getApiInfo(memberNote))));
		}
		// validate the actual member id is not null
		else
		{
			tests.add(dynamicTest("id (not null)", () -> assertNotNull(memberNote.getId(), "id must not null \n" + getApiInfo(memberNote))));
		}

		return tests;
	}

	public List<DynamicNode> compareUI(MemberNote otherNote)
	{
		List<DynamicNode> tests = new ArrayList<>();

		if ( otherNote == null )
		{
			tests.add(dynamicTest("Note [" + note + "]", () -> fail("Actua Note is null")));
			return tests;
		}

		tests.add(dynamicTest("note: [" + note + "]", () -> assertEquals(note, otherNote.getNote())));
		tests.add(dynamicTest("alert: [" + alert + "]", () -> assertEquals(alert, otherNote.isAlert())));
		tests.add(dynamicTest("hidden: [" + hidden + "]", () -> assertEquals(hidden, otherNote.isHidden())));
		tests.add(dynamicTest("createUser: [" + createUser + "]", () -> assertEquals(createUser, otherNote.getCreateUser())));
		tests.add(dynamicTest("createDateTime: [" + createDateTime + "]", () -> assertEquals(createDateTime, otherNote.getCreateDateTime())));
		tests.add(dynamicTest("lastUpdatedBy: [" + lastUpdatedBy + "]", () -> assertEquals(lastUpdatedBy, otherNote.getLastUpdatedBy())));
		tests.add(dynamicTest("lastUpdated: [" + lastUpdated + "]", () -> assertEquals(lastUpdated, otherNote.getLastUpdated())));

		return tests;
	}

	/**
	 * Validate this instance with the json schema
	 * @return schema validation test
	 */
	public DynamicNode schemaValidation()
	{
		return super.schemaValidation(jsonSchemaFile);
	}

	/*
	 * Helper methods
	 */

	/**
	 * Equals is defined if the following are met
	 * - note id are the same
	 *
	 * @return true if it mets the criteria
	 */
	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof MemberNote )
		{
			MemberNote memberNote = (MemberNote) obj;

			return id.equals(memberNote.getId());
		}

		return false;
	}

	/*
	 * Setter / Getter
	 */

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
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



