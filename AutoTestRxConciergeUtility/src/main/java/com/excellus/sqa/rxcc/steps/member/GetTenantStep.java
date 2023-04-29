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
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.AbstractStep;

/**
 * Retrieve tenant provided either member id, subscription name or tenant id
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 09/07/2022
 */
public class GetTenantStep extends AbstractStep 
{
	
	private static final Logger logger = LoggerFactory.getLogger(GetTenantStep.class);
	
	private final static String STEP_NAME = "GetTenantStep";
	private final static String STEP_DESC = "Retrieves the tenant provided either subscription name or tenant id";
	
	private String memberId;
	private String subscriptionName;
	private int tenantId;
	
	private Tenant tenant;
	
	/**
	 * Constructor
	 * @param subscriptionName of tenant
	 */
	public GetTenantStep(String subscriptionName, String memberId)
	{
		super(STEP_NAME, STEP_DESC);
		
		this.subscriptionName = subscriptionName;
		this.memberId = memberId;
	}
	
	/**
	 * Constructor
	 * @param tenantId of tenant
	 */
	public GetTenantStep(int tenantId)
	{
		super(STEP_NAME, STEP_DESC);
		
		this.tenantId = tenantId;
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
				String[] ids = memberId.split("_");
				
				try {
					int id = Integer.valueOf(ids[0]).intValue();
					tenant = TenantQueries.getTenantByTenantId(id);
				}
				catch (NumberFormatException e) {
					tenant = TenantQueries.getTenant(ids[0]);
				}
			}
			else if ( StringUtils.isNotBlank(subscriptionName) )
			{
				tenant = TenantQueries.getTenants()
										.stream()
										.filter(item -> StringUtils.equalsIgnoreCase(item.getSubscriptionName(), subscriptionName))
										.findAny()
										.orElse(null);
			}
			else if ( tenantId > 0 )
			{
				tenant = TenantQueries.getTenantByTenantId(tenantId);
			}
			else
			{
				throw new TestConfigurationException( "Unable to determine the tenant" );
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

	/*
	 * Getter
	 */
	
	public Tenant getTenant()
	{
		return this.tenant;
	}
}

