/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;

import com.excellus.sqa.rxcc.Utility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for Member Intervention Note
 * https://dev.azure.com/ExcellusBCBS/EHP/_git/rxcc-schemas?path=/schemas/member.interventionnote.schema.json
 * 
 * @author Manish Sharma (msharma)
 * @since 10/11/2022
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberInterventionNote  extends CommonItem
{

	@JsonProperty(required=true)
	private String memberId;

	@JsonProperty(required=true)
	private Type type; //const

	private String  interventionId;

	private String previousStatusCode;

	private String previousStatusDescription;

	private PreviousAction previousAction;  //enum

	@JsonProperty(required=true)
	private String newStatusCode; 

	@JsonProperty(required=true)
	private String newStatusDescription;  

	@JsonProperty(required=true)
	private NewAction newAction;  //enum

	@JsonProperty(required=true)
	private String note; 

	/*
	 * Validations
	 */

	/**
	 * Compare two objects
	 * 
	 * @param Member InterventionNote
	 * @return
	 */

	public List<DynamicNode> compare(MemberInterventionNote memberInterventionNote)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("memberId: [" + memberId + "]", () -> assertEquals(memberId, memberInterventionNote.getMemberId(), getApiInfo(memberInterventionNote))));
		tests.add(dynamicTest("type: [" + type + "]", () -> assertEquals(type, memberInterventionNote.getType(), getApiInfo(memberInterventionNote))));
		tests.add(dynamicTest("interventionId: [" + interventionId + "]", () -> assertEquals(interventionId, memberInterventionNote.getInterventionId(), getApiInfo(memberInterventionNote))));
		tests.add(dynamicTest("previousStatusCode: [" + previousStatusCode + "]", () -> assertEquals(previousStatusCode, memberInterventionNote.getPreviousStatusCode(), getApiInfo(memberInterventionNote))));
		tests.add(dynamicTest("previousStatusDescription: [" + previousStatusDescription + "]", () -> assertEquals(previousStatusDescription, memberInterventionNote.getPreviousStatusDescription(), getApiInfo(memberInterventionNote))));
		tests.add(dynamicTest("previousAction: [" + previousAction + "]", () -> assertEquals(previousAction, memberInterventionNote.getPreviousAction(), getApiInfo(memberInterventionNote))));
		tests.add(dynamicTest("newStatusCode: [" + newStatusCode + "]", () -> assertEquals(newStatusCode, memberInterventionNote.getNewStatusCode(), getApiInfo(memberInterventionNote))));
		tests.add(dynamicTest("newStatusDescription: [" + newStatusDescription + "]", () -> assertEquals(newStatusDescription, memberInterventionNote.getNewStatusDescription(), getApiInfo(memberInterventionNote))));
		tests.add(dynamicTest("newAction: [" + newAction + "]", () -> assertEquals(newAction, memberInterventionNote.getNewAction(), getApiInfo(memberInterventionNote))));
		tests.add(dynamicTest("note: [" + note + "]", () -> assertEquals(note, memberInterventionNote.getNote(), getApiInfo(memberInterventionNote))));

		tests.addAll( super.compare(memberInterventionNote) );

		return tests;
	}


	/**
	 * Compare two UI objects
	 * 
	 * @param MemberInterventionNote
	 * @return
	 * @author neerutagore 01/18/2023
	 */
	public List<DynamicNode> compareMemberInterventionNoteUI(MemberInterventionNote memberInterventionNote)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		tests.add(dynamicTest("Status : [" + newStatusDescription + "]", () -> assertEquals(newStatusDescription, memberInterventionNote.getNewStatusDescription())));
		tests.add(dynamicTest("Created By: [" + createdBy + "]", () -> assertEquals(createdBy, memberInterventionNote.getCreatedBy())));

		// Perform MembetInterventionNoteDateTime validation

		if ( StringUtils.isNotBlank(createdDateTime) && StringUtils.isNotBlank(memberInterventionNote.getCreatedDateTime()) )
		{
			String expectedDate = Utility.convertCosmosDateToUI(createdDateTime, "MM/dd/yyyy");
			tests.add(dynamicTest("Created Date: [" + createdDateTime + "]", 
					() -> assertEquals(expectedDate, memberInterventionNote.getCreatedDateTime()) ));
		}
		else
		{
			tests.add(dynamicTest("Created Date: [" + createdDateTime + "]", 
					() -> assertEquals(createdDateTime, memberInterventionNote.getCreatedDateTime())));
		}

		tests.add(dynamicTest("Created By: [" + lastUpdatedBy + "]", () -> assertEquals(lastUpdatedBy, memberInterventionNote.getLastUpdatedBy())));

		// Perform MembetInterventionNote updated datetime validation
		if ( StringUtils.isNotBlank(lastUpdatedDateTime) && StringUtils.isNotBlank(memberInterventionNote.getLastUpdatedDateTime()) )
		{
			String expectedDate = Utility.convertCosmosDateToUI(lastUpdatedDateTime, "MM/dd/yyyy");
			tests.add(dynamicTest("Modified Date: [" + lastUpdatedDateTime + "]", 
					() -> assertEquals(expectedDate, memberInterventionNote.getLastUpdatedDateTime()) ));
		}
		else
		{
			tests.add(dynamicTest("Modified Date: [" + lastUpdatedDateTime + "]", 
					() -> assertEquals(lastUpdatedDateTime, memberInterventionNote.getLastUpdatedDateTime())));
		}

		tests.add(dynamicTest("Note Content: [" + note + "]", () -> assertEquals(note, memberInterventionNote.getNote())));
		return tests;
	}

	/*
	 * Helper methods
	 */

	/**
	 * Equals is defined if the following are met
	 * - intervention note id are the same
	 *
	 * @return true if it mets the criteria
	 */
	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof MemberInterventionNote )
		{
			MemberInterventionNote memberInterventionNote = (MemberInterventionNote) obj;

			if ( id.equals(memberInterventionNote.getId()) ) {
				return true;
			}
		}

		return false;
	}

	/**
	 * This is meant to be used for UI validation
	 * @ntagore 01/16/23
	 * @param theInterventionNote {@link MemberInterventionNote}   
	 * @return true if the fields matches otherwise false
	 */

	public boolean equalsByNonId(MemberInterventionNote theInterventionNote)
	{
		if (  StringUtils.equalsIgnoreCase(lastUpdatedBy, theInterventionNote.getLastUpdatedBy()) &&
				StringUtils.equalsIgnoreCase(note, theInterventionNote.getNote()) &&
				StringUtils.equalsIgnoreCase(newStatusDescription, theInterventionNote.getNewStatusDescription()) &&
				StringUtils.equalsIgnoreCase(createdBy, theInterventionNote.getCreatedBy())) {
			return true;
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getInterventionId() {
		return interventionId;
	}

	public void setInterventionId(String interventionId) {
		this.interventionId = interventionId;
	}

	public String getPreviousStatusCode() {
		return previousStatusCode;
	}

	public void setPreviousStatusCode(String previousStatusCode) {
		this.previousStatusCode = previousStatusCode;
	}

	public String getPreviousStatusDescription() {
		return previousStatusDescription;
	}

	public void setPreviousStatusDescription(String previousStatusDescription) {
		this.previousStatusDescription = previousStatusDescription;
	}

	public PreviousAction getPreviousAction() {
		return previousAction;
	}

	public void setPreviousAction(PreviousAction previousAction) {
		this.previousAction = previousAction;
	}
	
	public void setPreviousAction(String value) {
		this.previousAction = PreviousAction.forValue(value);
	}

	public String getNewStatusCode() {
		return newStatusCode;
	}

	public void setNewStatusCode(String newStatusCode) {
		this.newStatusCode = newStatusCode;
	}

	public String getNewStatusDescription() {
		return newStatusDescription;
	}

	public void setNewStatusDescription(String newStatusDescription) {
		this.newStatusDescription = newStatusDescription;
	}

	public NewAction getNewAction() {
		return newAction;
	}

	public void setNewAction(NewAction newAction) {
		this.newAction = newAction;
	}

	public void setNewAction(String value) {
		this.newAction = NewAction.forValue(value);
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	//Const Type
	public enum Type {
		interventionnote;
	}

	//Enum PreviousAction
	public enum PreviousAction
	{
		Validate  (0, "Validate"),
		Decline   (1, "Decline");

		private int previousActionIndex;
		private String item;

		private PreviousAction(int previousActionType, String item)
		{
			this.previousActionIndex = previousActionType; 
			this.item = item;


		}

		public int getPreviousActionIndex()
		{
			return this.previousActionIndex;
		}

		public  String getEnumValue()
		{
			return item;
		}
		
		public static final PreviousAction forValue(String value) {
			for (PreviousAction action : PreviousAction.values()) {
				if (action.getEnumValue().equals(value)) {
					return action;
				}
			}
			throw new IllegalArgumentException(value);
		}
	}

	//Enum NewAction
	public enum NewAction
	{
		Validate  					(0, "Validate"),
		Decline   					(1, "Decline"),
		Correspondence_Dependent 	(2, "Correspondence Dependent"); // might have to re-visit in the future Triage Task #154792

		private int newActionIndex;
		private String item;

		private NewAction(int newActionType, String item)
		{
			this.newActionIndex = newActionType; 
			this.item = item;


		}

		public int getNewActionIndex()
		{
			return this.newActionIndex;
		}

		public  String getEnumValue()
		{
			return item;
		}


		public static final NewAction forValue(String value) {
			for (NewAction action : NewAction.values()) {
				if (action.getEnumValue().equals(value)) {
					return action;
				}
			}
			throw new IllegalArgumentException(value);
		}


		@Override
		public String toString() {
			return item;
		}

	}


}
