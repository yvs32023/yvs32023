/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.steps.member;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.cosmos.MemberQueries;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.AbstractStep;



/**
 * Deletes a member from Cosmos db given a member id
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 09/07/2022
 */
public class DeleteMemberStep extends AbstractStep 
{
	
	private static final Logger logger = LoggerFactory.getLogger(DeleteMemberStep.class);

	private final static String STEP_NAME = "DeleteMember - (%s)";
	private final static String STEP_DESC = "Delete member manually from cosmos db";
	
	private final String memberId;
	
	/**
	 * Constructor
	 * 
	 * @param memberId to delete
	 */
	public DeleteMemberStep(String memberId) 
	{
		super(String.format(STEP_NAME, memberId), STEP_DESC);
		this.memberId = memberId;
	}

	@Override
	public void run() 
	{
		super.stepStatus = Status.IN_PROGRESS;
		logger.info(super.print());

		try
		{
			if ( StringUtils.isNotBlank(memberId) )
			{
				// Get tenant
				GetTenantStep getTenantStep = new GetTenantStep(null, memberId);
				getTenantStep.run();
				Tenant tenant = getTenantStep.getTenant();
				
				if ( tenant == null )
				{
					throw new TestConfigurationException( String.format("Member id [%s] starts with unknown tenant id", memberId) );
				}
				
				MemberQueries.deleteMember(tenant.getSubscriptionName(), memberId, memberId);
			}
			
			super.stepStatus = Status.COMPLETED;
		}
		catch (Exception e)
		{
			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}
		
		logger.info(print());
	}

}

