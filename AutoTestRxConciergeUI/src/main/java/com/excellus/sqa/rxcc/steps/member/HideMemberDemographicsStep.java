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
import com.excellus.sqa.rxcc.pages.member.MemberPO;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * Hide or expose the member demographics
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 09/06/2022
 */
public class HideMemberDemographicsStep extends AbstractUIStep 
{

	private static final Logger logger = LoggerFactory.getLogger(HideMemberDemographicsStep.class);
	
	private final static String STEP_NAME = "HideMemberDemographics";
	private final static String STEP_DESC = "Hides or expose member demographics";

	private final boolean hide;
	
	/**
	 * Constructor
	 * 
	 * @param driver {@link WebDriver}
	 * @param hide true to hide it otherwise false
	 */
	public HideMemberDemographicsStep(WebDriver driver, boolean hide) {
		super(STEP_NAME, STEP_DESC, driver, 
				BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
		
		this.hide = hide;
	}
	
	/**
	 * Constructor
	 * 
	 * @param driver {@link WebDriver}
	 * @param pageConfiguration {@link PageConfiguration}
	 * @param hide true to hide it otherwise false
	 */
	public HideMemberDemographicsStep(WebDriver driver, PageConfiguration pageConfiguration, boolean hide) {
		super(STEP_NAME, STEP_DESC, driver, pageConfiguration);
		
		this.hide = hide;
	}

	@Override
	public void run() 
	{
		super.stepStatus = Status.IN_PROGRESS;
		logger.info( print() );
		
		try
		{
			MemberPO memberPO = new MemberPO(driver);
			memberPO.hideMemberDemographics(hide);
		}
		catch (Exception e)
		{
			super.stepStatus = Status.ERROR;
			super.stepException = e;
		}
		
		super.stepStatus = Status.COMPLETED;
		logger.info( print() );
	}

}

