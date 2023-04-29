/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.steps.fax;

import static com.excellus.sqa.step.IStep.Status.COMPLETED;
import static com.excellus.sqa.step.IStep.Status.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.RequestLoggingFilter;
import com.excellus.sqa.rxcc.dto.OutboundFaxStatus;
import com.excellus.sqa.step.AbstractStep;

/**
 * Retrieve the fax status given a token and job id
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 12/30/2022
 */
public class OutboundJobStep extends AbstractStep
{
	private static final Logger logger = LoggerFactory.getLogger(OutboundJobStep.class);

	private final static String STEP_NAME = OutboundJobStep.class.getSimpleName();
	private final static String STEP_DESC = "Retrieve the status of an outbound fax";

	private final String token;
	private final int jobId;
	private final static String ENDPOINT_OUTBOUND_JOB_STATUS = "https://apim-lbs-rxc-tst-east-001.azure-api.net/api/extrafax/out_jobs?id=";

	private final long timeoutInSec = 120;
	private final Boolean waitForFaxJobStatusSent;
	private int responseStatus;
	private OutboundFaxStatus outboundFaxStatus;

	/**
	 *
	 * @param token (Required) Token to be used for authentication
	 * @param jobId (Required) Fax job id
	 */
	public OutboundJobStep(String token, int jobId)
	{
		super(STEP_NAME, STEP_DESC);
		this.token = token;
		this.jobId = jobId;
		this.waitForFaxJobStatusSent = false;
	}

	/**
	 * Use this contractor to wait for the fax job to be sent
	 * @param token (Required) Token to be used for authentication
	 * @param jobId (Required) Fax job id
	 * @param waitForFaxJobStatusSent true to wait for the status to change to SENT
	 */
	public OutboundJobStep(String token, int jobId, boolean waitForFaxJobStatusSent)
	{
		super(STEP_NAME, STEP_DESC);
		this.token = token;
		this.jobId = jobId;
		this.waitForFaxJobStatusSent = waitForFaxJobStatusSent;
	}

	@Override
	public void run()
	{
		super.stepStatus = IN_PROGRESS;

		try
		{
			String url = ENDPOINT_OUTBOUND_JOB_STATUS + jobId;
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
										  .uri(URI.create(url))
										  .headers("Content-Type", "application/json",
												  "Authorization", "Bearer " + token)
										  .GET()
										  .build();

			LocalDateTime startTime = LocalDateTime.now();
			while (ChronoUnit.SECONDS.between(startTime, LocalDateTime.now()) <= timeoutInSec)
			{
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

				responseStatus = response.statusCode();
				this.outboundFaxStatus = new OutboundFaxStatus(response.body());

				if ( this.waitForFaxJobStatusSent )
				{
					if ( StringUtils.equalsIgnoreCase(outboundFaxStatus.getJobStatus(), "SENT") ) {
						break;
					}
				}
				// If step is set up not to wait for the status to change to SENT
				else {
					break;
				}

				Thread.sleep(5000);
			}

			super.stepStatus = COMPLETED;
		}
		catch (Exception e)
		{
			String apiInfo = RequestLoggingFilter.getApiInfo();
			super.stepTestResults.add(dynamicTest(STEP_DESC, () -> fail(apiInfo + "\n" + e.getMessage())));

			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}

		logger.info(print());
	}

	@Override
	public List<DynamicNode> getTestResults()
	{
		if ( stepTestResults.size() == 0 && stepStatus != Status.IDLE && stepStatus != Status.IN_PROGRESS && stepStatus != Status.RETRY )
		{
			stepTestResults.add(
					dynamicTest("Step [" + stepName + "] - Status [" + stepStatus + "] - Job id [" + this.jobId + "]",
							() ->
							{
								if ( (stepStatus == Status.ERROR || stepStatus == Status.FAILURE) && stepException != null )
									throw stepException;
								else
									assertTrue(true);
							}
					)
			);
		}

		return stepTestResults;
	}

	public int getResponseStatus()
	{
		return responseStatus;
	}

	public OutboundFaxStatus getOutboundFaxStatus()
	{
		return outboundFaxStatus;
	}
}
