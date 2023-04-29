/**
 * 
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.workflows.fax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionNoteQueries;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionQueries;
import com.excellus.sqa.rxcc.dto.FaxRequest;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.MemberInterventionNote;
import com.excellus.sqa.rxcc.steps.fax.OutboundJobStep;
import com.excellus.sqa.rxcc.steps.fax.TriggerOutboundTimerStep;
import com.excellus.sqa.rxcc.steps.fax.ValidateFaxRequestStep;
import com.excellus.sqa.rxcc.steps.fax.WaitForFaxRequestStep;
import com.excellus.sqa.rxcc.workflows.AbstractRxCCWorkflow;
import com.excellus.sqa.rxcc.workflows.WorkflowNames;
import com.excellus.sqa.step.IStep.Status;
import com.google.gson.JsonObject;

/**
 * Sends fax/es through Extrafax system
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 01/06/2023
 */
public class SendExtrafaxWorkflow extends AbstractRxCCWorkflow
{
	private static final Logger logger = LoggerFactory.getLogger(SendExtrafaxWorkflow.class);

	private final JsonObject jsonPdfFaxRequest;
	private final String interventionId;
	private final String bearerToken;
	private final String acctName;
	private FaxRequest faxRequest;

	public SendExtrafaxWorkflow(@Nonnull Mode executionMode, @Nonnull String bearerToken, JsonObject jsonPdfFaxRequest, @Nonnull String interventionId, String acctName)
	{
		super(null, WorkflowNames.SEND_FAX, executionMode);
		this.jsonPdfFaxRequest = jsonPdfFaxRequest;
		this.interventionId = interventionId;
		this.bearerToken = bearerToken;
		this.acctName = acctName;

		if ( StringUtils.isBlank(this.bearerToken) ) {
			throw new TestConfigurationException("The Bearer token cannot be empty or null");
		}

		if ( StringUtils.isBlank(this.interventionId) ) {
			throw new TestConfigurationException("The interventionId cannot be empty or null");
		}
	}


	@Override
	public void run() 
	{
		workflowStatus = Status.IN_PROGRESS;

		try
		{
			String[] interventionIds = new String[] {interventionId};

			/*
			 * Wait for Fax request to be created in LBS faxrequest container
			 */

			WaitForFaxRequestStep waitForFaxRequestStep1 = new WaitForFaxRequestStep(interventionIds);
			super.currentStep = waitForFaxRequestStep1;
			if ( processStep() != Status.COMPLETED )
			{
				return;
			}

			/*
			 * Validate the newly created faxrequest item in the LBS container
			 */

			List<FaxRequest> list1 = waitForFaxRequestStep1.getFaxRequestList();
			if ( list1 != null && list1.size()>0 ) 
			{
				FaxRequest theFaxRequest = list1.get(0);

				if ( executionMode == Mode.TEST && theFaxRequest != null && jsonPdfFaxRequest != null )
				{
					ValidateFaxRequestStep validateFaxRequestStep = new ValidateFaxRequestStep(acctName, jsonPdfFaxRequest, theFaxRequest);
					validateFaxRequestStep.run();
					super.workflowTestResults.add(
							dynamicContainer("Fax Request created - interventionId: " + interventionId, validateFaxRequestStep.getTestResults()));
				}
			}
			else
			{
				// This means there are no faxrequest created in the LBS container
				super.workflowTestResults.add(dynamicTest("Fax request", () -> fail("No faxrequest created")));
				return;
			}

			/*
			 * Trigger the Outbound timer
			 */

			super.currentStep = new TriggerOutboundTimerStep(this.bearerToken);
			if ( processStep() != Status.COMPLETED )
			{
				return;
			}

			/*
			 * Wait for Fax request to be queued (i.e. property 'jobId' is added in the faxrequest item
			 */

			WaitForFaxRequestStep waitForFaxRequestStep2 = new WaitForFaxRequestStep(true, false, interventionIds);
			super.currentStep = waitForFaxRequestStep2;
			if ( processStep() != Status.COMPLETED )
			{
				return;
			}

			/*
			 * Validate the faxrequest is updated with jobId 
			 */

			List<FaxRequest> list2 = waitForFaxRequestStep2.getFaxRequestList();
			if ( list2 != null && list2.size()>0 ) 
			{
				faxRequest = list2.get(0);

				if ( executionMode == Mode.TEST && faxRequest != null && jsonPdfFaxRequest != null )
				{
					ValidateFaxRequestStep validateFaxRequestStep = new ValidateFaxRequestStep(acctName, jsonPdfFaxRequest, faxRequest, true);
					validateFaxRequestStep.run();

					/*
					 * Validate as well as the intervention is update with status code 12 (Fax Queued)
					 */

					List<DynamicNode> interventionTests = new LinkedList<>();

					MemberIntervention memberIntervention = MemberInterventionQueries.getIntervention(interventionId);
					if ( memberIntervention == null )
					{
						throw new RuntimeException("Intervention was not found in the Cosmos DB");
					}

					interventionTests.add(dynamicTest("Fax queue status code (12) -- Cosmos db Value -- [" + memberIntervention.getQueueStatusCode() + "])",
							() -> assertEquals("12", memberIntervention.getQueueStatusCode(),		// 12 - Fax Queued
									"The queue status code is expected to be 'Fax Queued' with code status of 12")));

					interventionTests.add(dynamicTest("Last updated by [System API]",
							() -> assertEquals("System API", memberIntervention.getLastUpdatedBy())));


					//Make sure document id is present in the intervention 
					interventionTests.add(dynamicTest("DocumentId in intervention (has value)",
							() -> assertNotNull(memberIntervention.getId())));

					validateFaxRequestStep.getTestResults().add(dynamicContainer("Intervention updated after fax queued in ExtraFax", interventionTests));

					/*
					 * Validate interventionnote is updated with status code 12 (Fax Queued)
					 */

					List<DynamicNode> interventionNoteTests = new LinkedList<>();

					MemberInterventionNote memberInterventionNote = MemberInterventionNoteQueries.getInterventionNote(interventionId);
					if ( memberInterventionNote == null )
					{
						throw new RuntimeException("Intervention note was not found in the Cosmos DB");
					}

					interventionNoteTests.add(dynamicTest("Intervention Note new status code (12))",
							() -> assertEquals("12", memberInterventionNote.getNewStatusCode(),		// 12 - Fax Queued updated in intervention note
									"The newStatusCode is expected to be 'Fax Queued' with code status of 12")));

					interventionNoteTests.add(dynamicTest("CreatedBy -- Cosmos db Value -- [" + memberInterventionNote.getLastUpdatedBy() + "]",
							() -> assertEquals(acctName, memberInterventionNote.getCreatedBy())));  //Created By the login id used for creating fax queued

					interventionNoteTests.add(dynamicTest("Last updated by -- Cosmos db Value -- [" + memberInterventionNote.getLastUpdatedBy() + "]",
							() -> assertEquals(acctName, memberInterventionNote.getLastUpdatedBy())));

					validateFaxRequestStep.getTestResults().add(dynamicContainer("Intervention note updated after fax queued in ExtraFax", interventionNoteTests));

					super.workflowTestResults.add(
							dynamicContainer("Fax Request queued in Extrafax", validateFaxRequestStep.getTestResults()));
				}
			}

			/*
			 * Wait for Extrafax to successfully send the fax
			 */

			int jobId = Integer.parseInt(faxRequest.getFaxJobId());
			OutboundJobStep outboundJobStep = new OutboundJobStep(this.bearerToken, jobId, true);
			super.currentStep = outboundJobStep;
			if ( processStep() != Status.COMPLETED )
			{
				return;
			}

			workflowStatus = Status.COMPLETED;
		}
		catch (Exception e)
		{
			super.workflowStatus = Status.ERROR;
			super.workflowException = e;

			super.workflowStepResults.add(dynamicTest("Unexpected error", () -> {throw super.workflowException;}));
		}


		logger.info(print());
	}

	public FaxRequest getFaxRequest()
	{
		return this.faxRequest;
	}

}

