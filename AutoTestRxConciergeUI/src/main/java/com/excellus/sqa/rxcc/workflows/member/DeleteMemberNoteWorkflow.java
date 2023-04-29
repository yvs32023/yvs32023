/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.workflows.member;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.Arrays;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.Entity;
import com.excellus.sqa.rxcc.dto.MemberNote;
import com.excellus.sqa.rxcc.steps.member.DeleteMemberNoteStep;
import com.excellus.sqa.rxcc.steps.member.GetMemberNoteStep;
import com.excellus.sqa.rxcc.steps.member.OpenMemberStep;
import com.excellus.sqa.rxcc.workflows.AbstractRxCCWorkflow;
import com.excellus.sqa.rxcc.workflows.WorkflowNames;
import com.excellus.sqa.step.IStep.Status;

/**
 * A workflow to discard a member note
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/28/2022
 */
public class DeleteMemberNoteWorkflow extends AbstractRxCCWorkflow 
{

	private static final Logger logger = LoggerFactory.getLogger(DeleteMemberNoteWorkflow.class);
	
	private final MemberNote memberNote;
	private String uniqueNoteIdentifier;
	
	public DeleteMemberNoteWorkflow(MemberNote memberNote, Mode executionMode, WebDriver driver) 
	{
		super(Entity.MEMBER, WorkflowNames.MEMBER_NOTES, executionMode, null, driver, null);
		this.memberNote = memberNote;
	}
	
	public DeleteMemberNoteWorkflow(MemberNote memberNote, String uniqueNoteIdentifier, Mode executionMode, WebDriver driver) 
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
			
			
			// Delete note
			super.currentStep = new DeleteMemberNoteStep(uniqueNoteIdentifier, driver);
			if ( super.processStep() != Status.COMPLETED ) {
				return;
			}

			
			// Validate the note is deleted
			if ( super.executionMode == Mode.TEST )
			{
				GetMemberNoteStep getMemberNoteStep = new GetMemberNoteStep(memberNote.getMemberId());
				super.currentStep = getMemberNoteStep;
				if ( super.processStep() != Status.COMPLETED ) {
					return;
				}
				
				MemberNote actual = getMemberNoteStep.getMemberNote(memberNote.getId());
				workflowTestResults.add(dynamicContainer("Note validation", 
						Arrays.asList( dynamicTest("Deleted from cosmos", 
								() -> assertNull(actual, "Member note not deleted from cosmos")) )));
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
			super.workflowStepResults = Arrays.asList(dynamicContainer("Workflow [" + super.workflowType + "_DELETE] - Status [" + super.workflowStatus() + "]",
					super.workflowStepResults));
			
			logger.info(print());
		}
		
	}


}

