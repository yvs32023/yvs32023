/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.steps.member;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
//import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.pages.member.MemberCorrespondencePO;
//import com.excellus.sqa.rxcc.pages.member.MemberDemographicPO;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * Retrieves the member correspondence information from the UI
 * 
 * @author Neeru Tagore(ntagore)
 * @since 05/13/2022
 */
public class RetrieveMemberCorrespondenceStep extends AbstractUIStep {

	private static final Logger logger = LoggerFactory.getLogger(RetrieveMemberCorrespondenceStep.class);
	
	private final static String STEP_NAME = "RetrieveMemberCorrespondence";
	private final static String STEP_DESC = "Retrieve member correspondence";
	
	private List<MemberCorrespondence> memberCorrespondences;
	/**
	 * Constructor - use this if retrieving all notes from the member notes table
	 * 
	 * @param driver {@link WebElement}
	 */
	public RetrieveMemberCorrespondenceStep(WebDriver driver) 
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
			MemberCorrespondencePO memberCorrespondencePO = new MemberCorrespondencePO(super.driver, super.pageConfiguration);
			memberCorrespondences = memberCorrespondencePO.retrieveMemberCorrespondenceInfo();
			
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
	public List<MemberCorrespondence> getMemberCorrespondences() 
	{
		return memberCorrespondences;
	}
	
}

