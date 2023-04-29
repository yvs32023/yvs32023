/**
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.steps.fax;

import static com.excellus.sqa.step.IStep.Status.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.FaxRequest;
import com.excellus.sqa.step.AbstractStep;
import com.google.gson.JsonObject;

/**
 * Validate the PDF fax request
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 01/03/2023
 */
public class ValidateFaxRequestStep extends AbstractStep
{
	private static final Logger logger = LoggerFactory.getLogger(ValidateFaxRequestStep.class);

	private final static String STEP_NAME = ValidateFaxRequestStep.class.getSimpleName();
	private final static String STEP_DESC = "Validate the PDF fax request";

	private final String acctName;
	private final JsonObject jsonFaxRequestBody;
	private final FaxRequest faxRequest;
	private final Boolean validateQueued;


	/**
	 * Constructor
	 * @param acctName the account name used for the test
	 * @param jsonFaxRequestBody {@link JsonObject}
	 * @param faxRequest {@link FaxRequest}
	 */
	public ValidateFaxRequestStep(String acctName, JsonObject jsonFaxRequestBody, FaxRequest faxRequest)
	{
		super(STEP_NAME, STEP_DESC);
		this.acctName = acctName;
		this.jsonFaxRequestBody = jsonFaxRequestBody;
		this.faxRequest = faxRequest;
		this.validateQueued = false;
	}

	/**
	 * Constructor
	 * @param acctName the account name used for the test
	 * @param jsonFaxRequestBody {@link JsonObject}
	 * @param faxRequest {@link FaxRequest}
	 * @param validateQueued true to validate that the fax request has been queued
	 */
	public ValidateFaxRequestStep(String acctName, JsonObject jsonFaxRequestBody, FaxRequest faxRequest, boolean validateQueued)
	{
		super(STEP_NAME, STEP_DESC);
		this.acctName = acctName;
		this.jsonFaxRequestBody = jsonFaxRequestBody;
		this.faxRequest = faxRequest;
		this.validateQueued = validateQueued;
	}

	@Override
	public void run()
	{
		super.stepStatus = IN_PROGRESS;

		try
		{
			String type = retrieveStringFromJson("type");
			stepTestResults.add(dynamicTest("type [" + type + "]",
					() -> assertEquals(type, faxRequest.getType().name())));

			String faxNumber = retrieveStringFromJson("faxNumber");
			stepTestResults.add(dynamicTest("faxNumber [" + faxNumber + "]",
					() -> assertEquals(faxNumber, faxRequest.getFaxNumber())));

			String npi = retrieveStringFromJson("npi");
			stepTestResults.add(dynamicTest("npi [" + npi + "]", () -> assertEquals(npi, faxRequest.getNpi())));

			String memberId = retrieveStringFromJson("memberId");
			stepTestResults.add(dynamicTest("memberId [" + memberId + "]",
					() -> assertEquals(memberId, faxRequest.getMemberId())));

			String interventionId = retrieveStringFromJson("interventionId");
			stepTestResults.add(dynamicTest("interventionId [" + interventionId + "]",
					() -> assertEquals(interventionId, faxRequest.getInterventionId())));

			stepTestResults.add(dynamicTest("pdfUri (has value)", () -> assertTrue(StringUtils.isNotBlank(faxRequest.getPdfUri()))));
			jsonFaxRequestBody.addProperty("pdfUri", faxRequest.getPdfUri());	// Update the request with the PDF URI

			stepTestResults.add(dynamicTest("documentId (has value)", () -> assertNotNull(faxRequest.getDocumentId())));
			jsonFaxRequestBody.addProperty("documentId", faxRequest.getDocumentId());	// Update the request with the documentId

			Boolean override = retrieveBooleanFromJson("override");
			if ( override != null )
			{
				stepTestResults.add(dynamicTest("override [" + override + "]",
						() -> assertEquals(override, faxRequest.isOverride())));
			}

			if ( StringUtils.isNoneBlank(acctName) )
			{
				stepTestResults.add(dynamicTest("createdBy [" + acctName + "]",
						() -> assertEquals(acctName, faxRequest.getCreatedBy())));
			}

			if ( this.validateQueued )
			{
				String jobId = StringUtils.isNotBlank(faxRequest.getFaxJobId()) ? faxRequest.getFaxJobId() : "";
				stepTestResults.add(dynamicTest("faxJobId present " + jobId, () -> assertTrue(StringUtils.isNotBlank(jobId))));

				stepTestResults.add(dynamicTest("retryCount [0]",
						() -> assertEquals(0, faxRequest.getRetryCount())));

				stepTestResults.add(dynamicTest("lastUpdatedBy [System API]",
						() -> assertEquals("System API", faxRequest.getLastUpdatedBy())));
			}
			else
			{
				stepTestResults.add(dynamicTest("faxJobId (not present)", () -> assertTrue(StringUtils.isBlank(faxRequest.getFaxJobId()))));

				stepTestResults.add(dynamicTest("retryCount [-1]", () -> assertEquals(-1, faxRequest.getRetryCount())));

				if ( StringUtils.isNoneBlank(acctName) )
				{
					stepTestResults.add(dynamicTest("lastUpdatedBy [" + acctName + "]",
							() -> assertEquals(acctName, faxRequest.getCreatedBy())));
				}
			}

		}
		catch (Exception e)
		{
			stepTestResults.add(dynamicTest(stepName, () -> fail(e)));
		}

		super.stepStatus = Status.COMPLETED;
		logger.info(print());
	}

	/*
	 * Getter
	 */

	public JsonObject getJsonFaxRequestBody()
	{
		return jsonFaxRequestBody;
	}

	/*
	 * Helper methods
	 */

	private String retrieveStringFromJson(String field)
	{
		try
		{
			String temp = jsonFaxRequestBody.get(field).getAsString();
			return StringUtils.normalizeSpace(temp);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	private Boolean retrieveBooleanFromJson(String field)
	{
		try
		{
			return jsonFaxRequestBody.get(field).getAsBoolean();
		}
		catch (Exception e)
		{
			return null;
		}
	}

}
