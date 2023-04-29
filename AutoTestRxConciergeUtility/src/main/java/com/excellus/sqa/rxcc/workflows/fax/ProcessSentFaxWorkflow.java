/**
 * 
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.workflows.fax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.cosmos.FaxRequestQueries;
import com.excellus.sqa.rxcc.cosmos.MemberCorrespondenceQueries;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionNoteQueries;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionQueries;
import com.excellus.sqa.rxcc.dto.FaxRequest;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.MemberInterventionNote;
import com.excellus.sqa.rxcc.steps.fax.TriggerOutboundTimerStep;
import com.excellus.sqa.rxcc.steps.fax.WaitForFaxRequestStep;
import com.excellus.sqa.rxcc.workflows.AbstractRxCCWorkflow;
import com.excellus.sqa.rxcc.workflows.WorkflowNames;
import com.excellus.sqa.step.IStep.Status;
import com.google.gson.JsonObject;

/**
 * Process the fax that was sent successfully
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 01/06/2023
 */
public class ProcessSentFaxWorkflow extends AbstractRxCCWorkflow
{
	private static final Logger logger = LoggerFactory.getLogger(ProcessSentFaxWorkflow.class);

	private final String bearerToken;
	private final String interventionId;
	private final JsonObject jsonPdfFaxRequest;
	private final Map<String, Integer> originalNpiCounts;

	/**
	 * 
	 * @param executionMode {@link Mode}
	 * @param bearerToken token to be used for authentication process when calling API
	 * @param interventionId that is being processed by this workflow
	 * @param jsonPdfFaxRequest (optional) if the execution mode is set for TEST
	 * @param originalNpiCounts (optional) if the execution mode is set for TEST
	 */
	public ProcessSentFaxWorkflow(@Nonnull Mode executionMode, @Nonnull String bearerToken, @Nonnull String interventionId, JsonObject jsonPdfFaxRequest, Map<String, Integer> originalNpiCounts)
	{
		super(null, WorkflowNames.PROCESS_SENT_FAX, executionMode);
		this.bearerToken = bearerToken;
		this.interventionId = interventionId;
		this.jsonPdfFaxRequest = jsonPdfFaxRequest;
		this.originalNpiCounts = originalNpiCounts;

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
			 * Trigger the Outbound timer
			 */

			super.currentStep = new TriggerOutboundTimerStep(this.bearerToken);
			if ( processStep() != Status.COMPLETED )
			{
				return;
			}

			/*
			 * Wait for the faxrequest to be removed from the LBS container
			 */


			WaitForFaxRequestStep waitForFaxRequestStep2 = new WaitForFaxRequestStep(false, true, interventionIds);
			super.currentStep = waitForFaxRequestStep2;
			if ( processStep() != Status.COMPLETED )
			{
				return;
			}

			/*
			 * Validate
			 */

			if ( executionMode == Mode.TEST && jsonPdfFaxRequest != null )
			{
				String phoneNumber = jsonPdfFaxRequest.get("phoneNumber").getAsString();
				String memberId = jsonPdfFaxRequest.get("memberId").getAsString();


				/*
				 * Validate the faxrequest (phone number 0000000000) is updated
				 */

				// retrieve fax request
				List<FaxRequest> newFaxRequests = FaxRequestQueries.getAllFaxRequests();

				// Validate faxrequest entry with phone number 0000000000
				FaxRequest newFaxRequestMain = newFaxRequests.stream()
						.filter( faxRequest -> StringUtils.equalsIgnoreCase(faxRequest.getPhoneNumber(), "0000000000"))
						.findFirst()
						.orElse(null);

				List<DynamicNode> theTests = new LinkedList<>();

				if ( newFaxRequestMain != null )
				{
					theTests.add(dynamicTest("phoneNumber (0000000000)",
							() -> assertEquals("0000000000", newFaxRequestMain.getPhoneNumber())));

					theTests.add(dynamicTest("type (" + FaxRequest.Type.count.name() + ")",
							() -> assertEquals(FaxRequest.Type.count, newFaxRequestMain.getType())));

					/*
					 * Validate entries in the faxrequest container that corresponds to fax requests
					 */

					try
					{
						// validation
						List<DynamicNode> theFaxRequestTest = new LinkedList<>();

						int expectedCount = originalNpiCounts != null && originalNpiCounts.get(phoneNumber) != null ? 
								originalNpiCounts.get(phoneNumber) + 1 : 1;

						// Make sure the phone number is added in the faxrequest type 'count'
						Map<String, Integer> npiCounts = newFaxRequestMain.getNpiCount();
						if ( npiCounts != null && npiCounts.containsKey(phoneNumber) )
						{
							theFaxRequestTest.add(dynamicTest("npiCount -> phone (" + phoneNumber + ")",
									() -> assertTrue(npiCounts.containsKey(phoneNumber))));

							theFaxRequestTest.add(dynamicTest("npiCount [" + expectedCount + "]",
									() -> assertEquals(expectedCount, npiCounts.get(phoneNumber))));
						}
						else
						{
							theFaxRequestTest.add(dynamicTest("phoneNumber (0000000000) -> npiCount -> count",
									() -> fail("The phone number " + phoneNumber + " is not found in the npiCount")));
						}

						theTests.add(dynamicContainer(memberId + " [phoneNumber (0000000000)]", theFaxRequestTest));
					}
					catch (Exception e)
					{
						theTests.add(dynamicTest(memberId,
								() -> fail("Unable to validate faxrequest (type: count) with phone number " + phoneNumber)));
					}
				}
				else
				{
					theTests.add(dynamicTest("Entry faxrequest id 0000000000", () -> fail("No entry created in the 'faxrequest' container with id 0000000000")));
				}


				/*
				 * Validate intervention is updated with status code 11 (Fax Successful)
				 */

				List<DynamicNode> interventionTests = new LinkedList<>();

				MemberIntervention memberIntervention = MemberInterventionQueries.getIntervention(interventionId);
				if ( memberIntervention == null )
				{
					throw new RuntimeException("Intervention was not found in the Cosmos DB");
				}

				interventionTests.add(dynamicTest("Fax queue status code (11))",
						() -> assertEquals("11", memberIntervention.getQueueStatusCode(),		// 11 - Fax Successful
								"The queue status code is expected to be 'Fax Successful' with code status of 11")));

				theTests.add(dynamicContainer("Intervention updated with the fax successful", interventionTests));


				/*
				 * Validate intervention correpondence is updated with status code 11 (Fax Successful)
				 */

				List<DynamicNode> correspondenceTests = new LinkedList<>();

				MemberCorrespondence memberCorrespondence = MemberCorrespondenceQueries.getCorrespondenceWithInterventionId(interventionId);
				if ( memberCorrespondence == null )
				{
					throw new RuntimeException("Correspondence was not found in the Cosmos DB");
				}

				correspondenceTests.add(dynamicTest("Intervention Correspondence with Fax Succesful Outcome",
						() -> assertEquals("Fax Successful", memberCorrespondence.getCorrespondenceOutcome(),	 //this might change to Fax Successful, potential bug
								"System Generated Correspondence Outcome is expected to be 'Fax Successful'")));

				correspondenceTests.add(dynamicTest("Intervention Correspondence type  Outbound Fax Provider",
						() -> assertEquals("Outbound Fax Provider", memberCorrespondence.getCorrespondenceType().getEnumValue(),		
								"System Generated Correspondence Type is expected to be 'Outbound Fax Provider'")));

				correspondenceTests.add(dynamicTest("CreatedBy [System API]",
						() -> assertEquals("System API", memberCorrespondence.getCreatedBy())));  //Created By the login id used for creating fax queued

				theTests.add(dynamicContainer("Intervention correspondence updated with the fax successful", correspondenceTests));

				TimeUnit.MINUTES.sleep(1); //wait as it takes some time to generated the intervention note by System API

				/*
				 * Validate intervention note is updated with status code 11 (Fax Successful)
				 */

				List<DynamicNode> interventionNoteTests = new LinkedList<>();

				MemberInterventionNote memberInterventionNote = MemberInterventionNoteQueries.getInterventionNote(interventionId);
				if ( memberInterventionNote == null )
				{
					throw new RuntimeException("Intervention note was not found in the Cosmos DB");
				}

				interventionNoteTests.add(dynamicTest("Intervention Note new status code (11))",
						() -> assertEquals("11", memberInterventionNote.getNewStatusCode(),		// 11 - Fax Sucessful updated in intervention note
								"The newStatusCode is expected to be 'Fax Queued' with code status of 11")));

				interventionNoteTests.add(dynamicTest("Intervention Note new Action - Correspondence Dependent",
						() -> assertEquals("Correspondence Dependent", memberInterventionNote.getNewAction().getEnumValue(),		// new Action set to be - Fax Sucessful updated in intervention note but due to bug it is set Correspondence Dependent
								"The newAction is expected to be 'Correspondence Dependent' with code status of 11")));

				interventionNoteTests.add(dynamicTest("CreatedBy -- Cosmos db Value -- [" + memberInterventionNote.getLastUpdatedBy() + "]",
						() -> assertEquals("System API", memberInterventionNote.getCreatedBy())));  //System API creating the fax successful intervention note

				interventionNoteTests.add(dynamicTest("Last updated by -- Cosmos db Value -- [" + memberInterventionNote.getLastUpdatedBy() + "]",
						() -> assertEquals("System API", memberInterventionNote.getLastUpdatedBy())));

				theTests.add(dynamicContainer("Intervention note  updated with the fax successful", interventionNoteTests));


				super.workflowTestResults.add(dynamicContainer("Fax request " + memberId, theTests));
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
}

