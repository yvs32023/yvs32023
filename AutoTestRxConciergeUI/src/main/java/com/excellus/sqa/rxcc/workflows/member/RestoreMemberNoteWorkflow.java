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

import com.excellus.sqa.rxcc.dto.Entity;
import com.excellus.sqa.rxcc.dto.MemberNote;
import com.excellus.sqa.rxcc.steps.member.OpenMemberStep;
import com.excellus.sqa.rxcc.steps.member.RestoreMemberNoteStep;
import com.excellus.sqa.rxcc.steps.member.RetrieveMemberNoteStep;
import com.excellus.sqa.rxcc.workflows.AbstractRxCCWorkflow;
import com.excellus.sqa.rxcc.workflows.WorkflowNames;
import com.excellus.sqa.step.IStep.Status;

/**
 * Restore a discard member note
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/28/2022
 */
public class RestoreMemberNoteWorkflow extends AbstractRxCCWorkflow 
{

	private static final Logger logger = LoggerFactory.getLogger(RestoreMemberNoteWorkflow.class);
	
	private final MemberNote memberNote;
	private String uniqueNoteIdentifier;
	
	public RestoreMemberNoteWorkflow(MemberNote memberNote, Mode executionMode, WebDriver driver) 
	{
		super(Entity.MEMBER, WorkflowNames.MEMBER_NOTES, executionMode, null, driver, null);
		this.memberNote = memberNote;
	}
	
	public RestoreMemberNoteWorkflow(MemberNote memberNote, String uniqueNoteIdentifier, Mode executionMode, WebDriver driver) 
	{
		super(Entity.MEMBER, WorkflowNames.MEMBER_NOTES, executionMode, null, driver, null);
		this.memberNote = memberNote;
		this.uniqueNoteIdentifier = uniqueNoteIdentifier;
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
			
			
			// Restore note
			super.currentStep = new RestoreMemberNoteStep(uniqueNoteIdentifier, driver);
			if ( super.processStep() != Status.COMPLETED ) {
				return;
			}

			
			// Validate the note
			if ( super.executionMode == Mode.TEST )
			{
				// Retrieve updated note
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
			super.workflowStepResults = Arrays.asList(dynamicContainer("Workflow [" + super.workflowType + "_RESTORE] - Status [" + super.workflowStatus() + "]",
					super.workflowStepResults));
			
			logger.info(print());
		}
		
	}


}

