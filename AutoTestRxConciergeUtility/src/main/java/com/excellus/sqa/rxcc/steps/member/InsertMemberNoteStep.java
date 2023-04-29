/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.steps.member;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.cosmos.MemberNoteQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.MemberNote;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.AbstractStep;

/**
 * Insert member note
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/29/2022
 */
public class InsertMemberNoteStep extends AbstractStep {

	private static final Logger logger = LoggerFactory.getLogger(InsertMemberNoteStep.class);

	private final static String STEP_NAME = "InsertMemberNote";
	private final static String STEP_DESC = "Inserts member (%s) note manually into cosmos db";
	
	private MemberNote memberNote;
	
	public InsertMemberNoteStep(MemberNote memberNote)
	{
		super(STEP_NAME, String.format(STEP_DESC, memberNote.getMemberId()));
		this.memberNote = memberNote;
	}

	@Override
	public void run() 
	{
		super.stepStatus = Status.IN_PROGRESS;
		logger.info(super.print());
		
		try
		{
			// GC (04/05/22) schema change - member id starts now with adTenantId but EXE still uses the tenantId.
			String[] ids = memberNote.getMemberId().split("_");

			Tenant tenant = null;
			
			try {
				int id = Integer.valueOf(ids[0]).intValue();
				tenant = TenantQueries.getTenantByTenantId(id);
			}
			catch (NumberFormatException e) {
				tenant = TenantQueries.getTenant(ids[0]);
			}
			
			if ( tenant == null )
			{
				throw new TestConfigurationException( String.format("Member id [%s] starts with unknown tenant id", memberNote.getMemberId()) );
			}
			
			MemberNoteQueries.insertMemberNote(tenant.getSubscriptionName(), memberNote);
			
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

