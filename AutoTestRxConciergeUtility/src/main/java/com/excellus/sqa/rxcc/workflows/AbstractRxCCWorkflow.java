/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.workflows;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.ITestConfiguration;
import com.excellus.sqa.rxcc.dto.Entity;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.step.IStep.Status;
import com.excellus.sqa.workflow.AbstractWorkflow;
import com.excellus.sqa.workflow.IEnumWorkflow;
import com.excellus.sqa.workflow.IWorkflow;

/**
 * 
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/27/2022
 */
public abstract class AbstractRxCCWorkflow extends AbstractWorkflow {
	
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractRxCCWorkflow.class);

	// OPTIONAL: only instantiate this if workflow need to know the entity type
	protected final Entity entity;
	
	// OPTIONAL: only instantiate these when using workflow for Web UI
	protected final PageConfiguration pageConfig;
	protected final WebDriver driver;
	
	/**
	 * Constructor - use this for non-web workflow 
	 * @param workflowType
	 */
	public AbstractRxCCWorkflow(IEnumWorkflow workflowType)
	{
		super(workflowType, Mode.RUN);
		
		// Workflow will not use entity type
		this.entity = null;

		// Workflow will not be used for Web UI
		this.driver = null;
		this.pageConfig = null;
	}
	
	/**
	 * Constructor - use this for non-web workflow
	 * 
	 * @param entityType that will used within the workflow
	 * @param workflowType an enum that defines the workflow type
	 * @param executionMode either run the workflow with or without test validation
	 */
	public AbstractRxCCWorkflow(Entity entity, IEnumWorkflow workflowType, Mode executionMode)
	{
		super(workflowType, executionMode);
		this.entity = entity;

		// Workflow will not be used for Web UI
		this.driver = null;
		this.pageConfig = null;
	}

	/**
	 * Constructor - use this when workflow is used with UI web application.
	 *               This workflow will NOT handle the login/logout since that is done in 
	 *               {@link com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase}
	 *               
	 * @param entityType that will used within the workflow
	 * @param workflowType an enum that defines the workflow type
	 * @param executionMode either run the workflow with or without test validation
	 * @param pageConfig to be used within the workflow
	 * @param driver as in Selenium {@link WebDriver)
	 * @param testConfiguration that contains the credentials
	 */
	public AbstractRxCCWorkflow(Entity entity, IEnumWorkflow workflowType, Mode executionMode, 
			                  PageConfiguration pageConfig, WebDriver driver, 
			                  ITestConfiguration testConfiguration)
	{
		super(workflowType, executionMode, testConfiguration, null);
		
		this.entity = entity;
		this.driver = driver;
		this.pageConfig = pageConfig;
	}

	/**
	 * Common may to run/process a step
	 * 
	 * @return Status of the step
	 */
	@Override
	public Status processStep()
	{
		// run the step
		super.currentStep.run();
		
		/*
		 * Set workflow status if the current step is not COMPLETED after it ran.
		 * Take a screenshot of the UI web page if there is an exception
		 */
		if(super.currentStep.stepStatus() != Status.COMPLETED)
		{
			if ( super.currentStep.getStepException() != null && super.currentStep instanceof AbstractUIStep )
			{
				((AbstractUIStep) super.currentStep).snagIt();
			}
			
			super.workflowException = super.currentStep.getStepException();
		}
		
		// update the workflow with step result
		super.workflowStepResults.addAll(super.currentStep.getTestResults());
		
		// update workflow step base on current step status
		super.workflowStatus = super.currentStep.stepStatus();
		
		logger.info(print());
		
		return super.currentStep.stepStatus();
	}
	
	
	/**
	 * Common may to run/process a workflow
	 * 
	 * @return Status of the workflow
	 */
	@Override
	public Status processWorkflow(IWorkflow theWorkflow)
	{
		// run the workflow
		theWorkflow.run();
		
		if ( theWorkflow.workflowStepResults().size() > 0 )
		{
			super.workflowStepResults.addAll(theWorkflow.workflowStepResults());
		}
		
		if ( theWorkflow.workflowTestResults().size() > 0 )
		{
			super.workflowTestResults.addAll(theWorkflow.workflowTestResults());
		}
		
		// update parent workflow base on the child workflow
		super.workflowStatus = theWorkflow.workflowStatus();
		
		logger.info(print());
				
		return theWorkflow.workflowStatus();
	}
	
	@Override
	public void logWorkflowFailure(IEnumWorkflow workflowType, Status status, Exception errorEncountered)
	{
		super.workflowStatus = status;
		super.workflowException = errorEncountered;
		
		this.workflowTestResults.add( dynamicTest("Workflow [" + workflowType + "] Status [" + status +"]", ()-> {throw errorEncountered;}) );
		
		logger.error(print());
	}

}

