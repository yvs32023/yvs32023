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

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.configuration.RxConciergeBeanConfig;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.pages.member.MemberDetailsNotesPO;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * Handles the alert notification displayed for a member
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/28/2022
 */
public class AlertMemberNoteStep extends AbstractUIStep {
	
	private static final Logger logger = LoggerFactory.getLogger(AlertMemberNoteStep.class);

	private final static String STEP_NAME = "AlertMemberNote";
	private final static String STEP_DESC = "Alert member note";
	
	private final String memberId;
	private String alertNote;

	public AlertMemberNoteStep(WebDriver driver, String memberId) 
	{
		super(STEP_NAME, STEP_DESC, driver, BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
		this.memberId = memberId;
	}
	
	@Override
	public void run() 
	{
		super.stepStatus = Status.IN_PROGRESS;
		logger.info(super.print());
		
		try
		{
			// Page object for member note we page
			MemberDetailsNotesPO memberNotesPO = new MemberDetailsNotesPO(driver, pageConfiguration);
			
			/*
			 * Get adTenantId
			 */
			
			// GC (04/05/22) schema change - member id starts now with adTenantId but EXE still uses the tenantId.
			String[] ids = memberId.split("_");

			String adTenantId = null;
			
			try {
				int id = Integer.valueOf(ids[0]).intValue();
				adTenantId = TenantQueries.getTenantByTenantId(id).getAdTenantId();
			}
			catch (NumberFormatException e) {
				adTenantId = ids[0];
			}
			
			if ( StringUtils.isBlank(adTenantId) )
			{
				throw new TestConfigurationException( String.format("Member id [%s] starts with unknown tenant id", memberId) );
			}
			
			/*
			 * Open member demographic
			 */
			
			// https://tst.rxcc.excellus.com/tenants/{adTenantId}/members/{memberId}
			String url = RxConciergeBeanConfig.envConfig().getProperty("url.home");
			driver.get(url + "/tenants/" + adTenantId + "/members/" + memberId);

	        SeleniumPageHelperAndWaiter.pause(2000);  // wait for note to be retrieved
			alertNote = memberNotesPO.retrieveNoteAlert();	
			memberNotesPO.clickNoteAlertExit();

			super.stepStatus = Status.COMPLETED;
		}
		catch (Exception e)
		{
			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}
		
		logger.info(print());
	}

	
	public String getAlertNote() {
		return alertNote;
	}
}

