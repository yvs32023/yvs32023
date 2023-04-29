/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.steps.fax;

import static com.excellus.sqa.step.IStep.Status.COMPLETED;
import static com.excellus.sqa.step.IStep.Status.IN_PROGRESS;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.cosmos.FaxRequestQueries;
import com.excellus.sqa.rxcc.dto.FaxRequest;
import com.excellus.sqa.step.AbstractStep;

/**
 * Waits for the fax requests.
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 12/30/2022
 */
public class WaitForFaxRequestStep extends AbstractStep
{
	private static final Logger logger = LoggerFactory.getLogger(WaitForFaxRequestStep.class);

	private final static String STEP_NAME = WaitForFaxRequestStep.class.getSimpleName();
	private final static String STEP_DESC = "Wait for fax request";

	private List<FaxRequest> faxRequestList = new ArrayList<>();
	private final String[] interventionIds;
	private final Boolean waitForJobId;
	private final Boolean waitForRemoved;
	private final long timeoutInSec = 60;

	/**
	 * This constructor will be used to wait for the fax request is created.
	 * @param interventionIds list of intervention ids
	 */
	public WaitForFaxRequestStep(String... interventionIds)
	{
		super(STEP_NAME, STEP_DESC);
		this.interventionIds = interventionIds;
		this.waitForJobId = false;
		this.waitForRemoved = false;
	}

	/**
	 * This contructor will be used to wait for the fax request to be updated with job id or has been removed
	 * @param waitForJobId true if it needs to wait for the job id
	 * @param waitForRemoved true if it needs to wait for the fax request to be removed from DB
	 * @param interventionIds list of intervention ids
	 */
	public WaitForFaxRequestStep(boolean waitForJobId, boolean waitForRemoved, String... interventionIds)
	{
		super(STEP_NAME, STEP_DESC);
		this.interventionIds = interventionIds;
		this.waitForJobId = waitForJobId;
		this.waitForRemoved = waitForRemoved;
	}

	@Override
	public void run()
	{
		super.stepStatus = IN_PROGRESS;

		try
		{
			if ( this.waitForJobId && !this.waitForRemoved )
			{
				logger.info("Waiting for fax request to have job id");

				LocalDateTime startTime = LocalDateTime.now();
				while (ChronoUnit.SECONDS.between(startTime, LocalDateTime.now()) <= timeoutInSec)
				{
					boolean faxRequestsHasJobId = true;

					faxRequestList = FaxRequestQueries.getFaxRequestByInterventionIds(interventionIds);

					for ( FaxRequest faxRequest : faxRequestList )
					{
						if ( faxRequest!=null && StringUtils.isBlank(faxRequest.getFaxJobId()) )
						{
							faxRequestsHasJobId = false;
							break;
						}
					}

					if ( faxRequestsHasJobId ) {	// All fax request has job id
						break;
					}

					Thread.sleep(5000);
				}
			}
			else if ( !this.waitForJobId && this.waitForRemoved )
			{
				logger.info("Waiting for fax request to be removed");

				LocalDateTime startTime = LocalDateTime.now();
				while (ChronoUnit.SECONDS.between(startTime, LocalDateTime.now()) <= timeoutInSec)
				{
					faxRequestList = FaxRequestQueries.getFaxRequestByInterventionIds(interventionIds);
					if ( faxRequestList.size() == 0 )
					{
						break;
					}

					Thread.sleep(5000);
				}
			}
			else
			{
				logger.info("Waiting for fax request to be created");

				LocalDateTime startTime = LocalDateTime.now();
				while (ChronoUnit.SECONDS.between(startTime, LocalDateTime.now()) <= timeoutInSec)
				{
					faxRequestList = FaxRequestQueries.getFaxRequestByInterventionIds(interventionIds);
					if ( faxRequestList.size() == interventionIds.length ) // All fax request has been created
					{
						break;
					}

					Thread.sleep(5000);
				}
			}

			super.stepStatus = COMPLETED;
		}
		catch (Exception e)
		{
			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}

		logger.info(print());
	}

	/* Getter */

	public List<FaxRequest> getFaxRequestList()
	{
		return faxRequestList;
	}
}
