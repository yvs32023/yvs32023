/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.steps.fax;

import static com.excellus.sqa.step.IStep.Status.COMPLETED;
import static com.excellus.sqa.step.IStep.Status.IN_PROGRESS;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.step.AbstractStep;
import com.google.gson.JsonObject;

/**
 * Triggers the outbound timer.
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 12/30/2022
 */
public class TriggerOutboundTimerStep extends AbstractStep
{
	private static final Logger logger = LoggerFactory.getLogger(TriggerOutboundTimerStep.class);

	private final static String STEP_NAME = TriggerOutboundTimerStep.class.getSimpleName();
	private final static String STEP_DESC = "Trigger the Outbound timer";

	private final String token;
	private final static String ENDPOINT_OUTBOUND_TIMER = "https://faw-lbs-rxc-tst-outbound-east-001.azurewebsites.net/admin/functions/OutboundTimer";
	private final static String OUTBOUND_X_FUNCTION_KEY = "57EWTYwbBE1f3RBP_F20O21zZkyv_oPG7E0hFkUDvDTlAzFu96-yOg==";

	private int responseStatus;

	public TriggerOutboundTimerStep(String token)
	{
		super(STEP_NAME, STEP_DESC);
		this.token = token;
	}

	@Override
	public void run()
	{
		super.stepStatus = IN_PROGRESS;

		try
		{
			JsonObject requestBody = new JsonObject();
			requestBody.add("input", null);

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
										  .uri(URI.create(ENDPOINT_OUTBOUND_TIMER))
										  .headers("Content-Type", "application/json",
												  "x-functions-key", OUTBOUND_X_FUNCTION_KEY,
												  "Authorization", "Bearer " + token)
										  .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
										  .build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			responseStatus = response.statusCode();

			Thread.sleep(10000);	// give time for the fax process to complete

			super.stepStatus = COMPLETED;
		}
		catch (Exception e)
		{
			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}

		logger.info(print());
	}

	public int getResponseStatus()
	{
		return responseStatus;
	}
}
