/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.workflows.member;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Arrays;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.dto.Entity;
import com.excellus.sqa.rxcc.dto.MemberNote;
import com.excellus.sqa.rxcc.steps.member.AddMemberNoteStep;
import com.excellus.sqa.rxcc.steps.member.OpenMemberStep;
import com.excellus.sqa.rxcc.steps.member.RetrieveMemberNoteStep;
import com.excellus.sqa.rxcc.workflows.AbstractRxCCWorkflow;
import com.excellus.sqa.rxcc.workflows.WorkflowNames;
import com.excellus.sqa.step.IStep.Status;

/**
 * Add new note to a member
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/28/2022
 */
public class AddNewMemberNoteWorkflow extends AbstractRxCCWorkflow 
{

	private static final Logger logger = LoggerFactory.getLogger(AddNewMemberNoteWorkflow.class);
	
	private final MemberNote memberNote;
	private final String uniqueNoteIdentifier;
	
	/**
	 * Constructor for the workflow
	 * 
	 * @param memberNote {@link MemberNote} to add to the member
	 * @param uniqueNoteIdentifier of the note
	 * @param executionMode to defined if validation is required or not
	 * @param driver {@link WebDriver}
	 */
	public AddNewMemberNoteWorkflow(MemberNote memberNote, String uniqueNoteIdentifier, Mode executionMode, WebDriver driver) 
	{
		super(Entity.MEMBER, WorkflowNames.MEMBER_NOTES, executionMode, null, driver, null);
		
		this.memberNote = memberNote;
		this.uniqueNoteIdentifier = uniqueNoteIdentifier;
		
		if ( this.memberNote == null )
			throw new TestConfigurationException("MemberNote cannot be null");
	}

	@Override
	public void run() 
	{
		workflowStatus = Status.IN_PROGRESS;
		
		try
		{
			// Open member
			super.currentStep = new OpenMemberStep(driver, memberNote.getMemberId());
			if ( super.processStep() != Status.COMPLETED ) {
				return;
			}
			
			
			// Add a new note
			super.currentStep = new AddMemberNoteStep(driver, memberNote);
			if ( super.processStep() != Status.COMPLETED ) {
				return;
			}
			
			
			// Validate the note
			if ( super.executionMode == Mode.TEST )
			{
				// Retrieve newly create note
				RetrieveMemberNoteStep retrieveMemberNoteStep = new RetrieveMemberNoteStep(driver, uniqueNoteIdentifier);
				super.currentStep = retrieveMemberNoteStep;
				if ( super.processStep() != Status.COMPLETED ) {
					return;
				}
				
				MemberNote actual = retrieveMemberNoteStep.getMemberNote();
				
				workflowTestResults.add(dynamicContainer("Note validation", memberNote.compareUI(actual)));
			}
		
			workflowStatus = Status.COMPLETED;
		}
		catch (Exception e)
		{
			super.workflowStatus = Status.ERROR;
			super.workflowException = e;
			super.workflowStepResults.add(dynamicTest("Unexpected error", () -> {throw super.workflowException;}));
		}
		finally
		{
			// Wrap all step results with dynamic containers
			super.workflowStepResults = Arrays.asList(dynamicContainer("Workflow [" + super.workflowType + "_ADD] - Status [" + super.workflowStatus() + "]",
					super.workflowStepResults));
			
			logger.info(print());
		}
		
	}


}

