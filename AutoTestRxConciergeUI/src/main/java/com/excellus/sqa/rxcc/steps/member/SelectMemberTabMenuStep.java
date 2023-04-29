/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.steps.member;

import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.member.MemberTabMenu;
import com.excellus.sqa.rxcc.pages.member.MemberPO;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * Selects the member tab menu
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 09/06/2022
 */
public class SelectMemberTabMenuStep extends AbstractUIStep 
{

	private static final Logger logger = LoggerFactory.getLogger(SelectMemberTabMenuStep.class);
	
	private final static String STEP_NAME = "SelectMemberTabMenuStep - %s";
	private final static String STEP_DESC = "Select member tab menu, %s";

	private final MemberTabMenu menu;
	
	public SelectMemberTabMenuStep(WebDriver driver, MemberTabMenu menu) {
		super(String.format(STEP_NAME, menu.toString()), 
				String.format(STEP_DESC, menu.toString()), 
				driver, 
				BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
		
		this.menu = menu;
	}
	
	public SelectMemberTabMenuStep(WebDriver driver, PageConfiguration pageConfiguration, MemberTabMenu menu) {
		super(String.format(STEP_NAME, menu.toString()), 
				String.format(STEP_DESC, menu.toString()), 
				driver, 
				pageConfiguration);
		
		this.menu = menu;
	}

	@Override
	public void run() 
	{
		super.stepStatus = Status.IN_PROGRESS;
		logger.info( print() );
		
		try
		{
			MemberPO memberPO = new MemberPO(driver);
			memberPO.clickTabMenu(menu);
		}
		catch (TimeoutException | NotFoundException e)
		{
			super.stepStatus = Status.FAILURE;
			super.stepException = new TestConfigurationException("The member tab menu '" + menu.toString() + "' is not present/visible");
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

