/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.steps.member;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.MemberNote;
import com.excellus.sqa.rxcc.pages.member.MemberDetailsNotesPO;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * Add member note 
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/28/2022
 */
public class AddMemberNoteStep extends AbstractUIStep {
	
	private static final Logger logger = LoggerFactory.getLogger(AddMemberNoteStep.class);

	private final static String STEP_NAME = "AddMemberNote";
	private final static String STEP_DESC = "Add new member note";
	
	private final MemberNote memberNote;

	public AddMemberNoteStep(WebDriver driver, MemberNote memberNote) 
	{
		super(STEP_NAME, STEP_DESC, driver, BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
		this.memberNote = memberNote;
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
			memberNotesPO.addNewNote(memberNote);

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

