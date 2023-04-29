/**
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.steps.fax;

import static com.excellus.sqa.step.IStep.Status.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.AbstractStep;


/**
 * Retrieves the PDF fax.
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 01/03/2023
 */
public class RetrievePdfFaxStep extends AbstractStep
{
	private static final Logger logger = LoggerFactory.getLogger(RetrievePdfFaxStep.class);

	private final static String STEP_NAME = RetrievePdfFaxStep.class.getSimpleName();
	private final static String STEP_DESC = "Retrieve outbound pdf fax";

	private final String token;
	private final URI uri;

	private int responseStatus;
	private byte[] pdfAsByteArray; 

	/**
	 * Constructor
	 * @param token taken from the UI
	 * @param uri complete URI where the PDF will be retrieved from
	 */
	public RetrievePdfFaxStep(String token, URI uri)
	{
		super(STEP_NAME, STEP_DESC);
		this.token = token;
		this.uri = uri;
	}

	/**
	 * Constructor
	 * @param token taken from the UI
	 * @param url complete URL where the PDF will be retrieved from
	 */
	public RetrievePdfFaxStep(String token, String url)
	{
		super(STEP_NAME, STEP_DESC);
		this.token = token;
		this.uri = URI.create(url);
	}


	@Override
	public void run()
	{
		super.stepStatus = IN_PROGRESS;

		try
		{
			logger.info("Retrieving PDF file, " + this.uri.toString());

			// Identify the tenant associated with the URI/URL
			Tenant tenant = TenantQueries.getTenantById(this.uri.toString());
			if ( tenant == null )
			{
				throw new TestConfigurationException("Unable to determine the tenant corresponding to the URL " + this.uri);
			}

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
										  .uri(this.uri)
										  .headers("X-RXCC-SUB", tenant.getSubscriptionName(),
												  "Authorization", "Bearer " + token)
										  .GET()
										  .build();

			HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
			
			responseStatus = response.statusCode();
			pdfAsByteArray = response.body().readAllBytes();

			super.stepStatus = Status.COMPLETED;
		}
		catch (Exception e)
		{
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
					dynamicTest("Step [" + stepName + "] - Status [" + stepStatus + "] - PDF URI [" + this.uri.toString() + "]",
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

	public byte[] getPdfAsByteArray()
	{
		return pdfAsByteArray;
	}
}
