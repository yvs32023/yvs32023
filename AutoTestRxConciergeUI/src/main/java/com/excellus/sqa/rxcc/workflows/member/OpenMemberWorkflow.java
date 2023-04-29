/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.workflows.member;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.util.Arrays;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.Entity;
import com.excellus.sqa.rxcc.dto.member.MemberTabMenu;
import com.excellus.sqa.rxcc.steps.member.HideMemberDemographicsStep;
import com.excellus.sqa.rxcc.steps.member.OpenMemberStep;
import com.excellus.sqa.rxcc.steps.member.SelectMemberTabMenuStep;
import com.excellus.sqa.rxcc.workflows.AbstractRxCCWorkflow;
import com.excellus.sqa.rxcc.workflows.WorkflowNames;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.step.IStep.Status;

/**
 * Open member and hide/expose demographic and select menu tab
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 09/06/2022
 */
public class OpenMemberWorkflow extends AbstractRxCCWorkflow 
{
	
	private static final Logger logger = LoggerFactory.getLogger(OpenMemberWorkflow.class);

	private final String memberId;
	private final Boolean hideDemographic;
	private final MemberTabMenu menu;
	
	public OpenMemberWorkflow(WebDriver driver, PageConfiguration pageConfiguration, String memberId, Boolean hideDemographic, MemberTabMenu menu) 
	{
		super(Entity.MEMBER, WorkflowNames.MEMBER, Mode.RUN, pageConfiguration, driver, null);
		
		this.memberId = memberId;
		this.hideDemographic = hideDemographic;
		this.menu = menu;
	}

	@Override
	public void run() 
	{
		try
		{
			// Open the member
			super.currentStep = new OpenMemberStep(driver, pageConfig, memberId);
			if ( processStep() != Status.COMPLETED ) {
				return;
			}
			
			// Hide/expose member demographic
			super.currentStep = new HideMemberDemographicsStep(driver, pageConfig, hideDemographic);
			if ( processStep() != Status.COMPLETED ) {
				return;
			}
			
			// Select menu
			if ( menu != null )
			{
				super.currentStep = new SelectMemberTabMenuStep(driver, pageConfig, menu);
				if ( processStep() != Status.COMPLETED ) {
					return;
				}
			}
		}
		catch (Exception e)
		{
			super.workflowStatus = Status.ERROR;
			super.workflowException = e;
		}
		finally
		{
			// Wrap all step results with dynamic containers
			super.workflowStepResults = Arrays.asList(dynamicContainer("Workflow [" + super.workflowType + "] - Status [" + super.workflowStatus() + "]",
					super.workflowStepResults));
			
			logger.info(print());
		}
	}

}

