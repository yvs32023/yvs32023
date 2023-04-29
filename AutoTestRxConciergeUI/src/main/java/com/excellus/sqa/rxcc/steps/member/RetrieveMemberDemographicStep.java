/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.steps.member;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.pages.member.MemberDemographicPO;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * Retrieves the member demographic, plan and group information from the UI
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 04/05/2022
 */
public class RetrieveMemberDemographicStep extends AbstractUIStep {

	private static final Logger logger = LoggerFactory.getLogger(RetrieveMemberDemographicStep.class);
	
	private final static String STEP_NAME = "RetrieveMemberDemographic";
	private final static String STEP_DESC = "Retrieve member demographic";
	
	private Member member;
	
	/**
	 * Constructor - use this if retrieving all notes from the member notes table
	 * 
	 * @param driver {@link WebElement}
	 */
	public RetrieveMemberDemographicStep(WebDriver driver) 
	{
		super(STEP_NAME, STEP_DESC, driver, BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
	}

	@Override
	public void run() 
	{
		super.stepStatus = Status.IN_PROGRESS;
		logger.info(super.print());
		
		try
		{
			MemberDemographicPO memberDemographicPO = new MemberDemographicPO(super.driver, super.pageConfiguration);
			member = memberDemographicPO.retrieveMemberInfo();
			
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
	 * Retrieve the note
	 * @return
	 */
	public Member getMember() 
	{
		return member;
	}
	
}

