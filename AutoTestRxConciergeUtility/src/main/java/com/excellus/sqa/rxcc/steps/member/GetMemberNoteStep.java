/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.steps.member;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.cosmos.implementation.apachecommons.lang.StringUtils;
import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.cosmos.MemberNoteQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.MemberNote;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.AbstractStep;

/**
 * Get member note/s
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/29/2022
 */
public class GetMemberNoteStep extends AbstractStep {

	private static final Logger logger = LoggerFactory.getLogger(GetMemberNoteStep.class);

	private final static String STEP_NAME = "GetMemberNote";
	private final static String STEP_DESC = "Get member note/s";
	
	private final String memberId;
	private List<MemberNote> memberNotes;
	
	/**
	 * Constructor
	 * 
	 * @param memberId
	 */
	public GetMemberNoteStep(String memberId)
	{
		super(STEP_NAME, STEP_DESC);
		this.memberId = memberId;
		memberNotes = new ArrayList<MemberNote>();
	}

	@Override
	public void run() 
	{
		super.stepStatus = Status.IN_PROGRESS;
		logger.info(super.print());
		
		try
		{
			// GC (04/05/22) schema change - member id starts now with adTenantId but EXE still uses the tenantId.
			String[] ids = memberId.split("_");

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
				throw new TestConfigurationException( String.format("Member id [%s] starts with unknown tenant id", memberId) );
			}
			
			memberNotes = MemberNoteQueries.getMemberNote(memberId);
			
			super.stepStatus = Status.COMPLETED;
		}
		catch (Exception e)
		{
			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}

		logger.info(print());
	}
	
	/**
	 * Find the member note where the id or note contains the identifier
	 * @param identifier to find from note id or note itself
	 * @return member note if found otherwise returns null
	 */
	public MemberNote getMemberNote(String identifier)
	{
		for ( MemberNote note : memberNotes )
		{
			if ( StringUtils.containsIgnoreCase(note.getId(), identifier) ||
					StringUtils.containsIgnoreCase(note.getNote(), identifier) ) {
				return note;
			}
		}
		
		return null;
	}
	
	public List<MemberNote> getMemberNotes() {
		return memberNotes;
	}

}

