/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.steps.member;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.pages.member.MemberDetailsNotesPO;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * Restore a discarded note
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/28/2022
 */
public class RestoreMemberNoteStep extends AbstractUIStep {

	private static final Logger logger = LoggerFactory.getLogger(RestoreMemberNoteStep.class);

	private final static String STEP_NAME = "DiscardMemberNote";
	private final static String STEP_DESC = "Discard member note";
	
	private String uniqueNoteIdentifier;
	
	public RestoreMemberNoteStep(String uniqueNoteIdentifier, WebDriver driver) {
		super(STEP_NAME, STEP_DESC, driver, BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
		this.uniqueNoteIdentifier = uniqueNoteIdentifier;
	}
	
	
	@Override
	public void run() 
	{
		super.stepStatus = Status.IN_PROGRESS;
		logger.info(super.print());
		
		try
		{
			MemberDetailsNotesPO memberNotesPO = new MemberDetailsNotesPO(driver, pageConfiguration);
			
			memberNotesPO.clickNotesTab();
			
			if ( StringUtils.isNotBlank(uniqueNoteIdentifier) )
			{
				memberNotesPO.restoreNote(uniqueNoteIdentifier);
			}
			else
			{
				memberNotesPO.restoreNote();
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

