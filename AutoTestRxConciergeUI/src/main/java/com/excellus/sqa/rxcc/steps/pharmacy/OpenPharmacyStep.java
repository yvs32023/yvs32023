/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.steps.pharmacy;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.configuration.RxConciergeBeanConfig;
import com.excellus.sqa.rxcc.pages.pharmacy.PharmacyDemographicPO;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * Open pharmacy demographic web page
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 04/01/2022
 */
public class OpenPharmacyStep extends AbstractUIStep {

	private static final Logger logger = LoggerFactory.getLogger(OpenPharmacyStep.class);
	
	private final static String STEP_NAME = "OpenPharmacyDemographic";
	private final static String STEP_DESC = "Open pharmacy (%s) demographic";
	
	private String npi;

	/**
	 * Open the pharmacy demographic web page given the npi
	 * 
	 * @param driver {@link WebDriver}
	 * @param npi to open
	 */
	public OpenPharmacyStep(WebDriver driver, String npi) 
	{
		super(STEP_NAME, String.format(STEP_DESC, npi), driver, BeanLoader.loadBean(BeanNames.PHARMACIES_PAGE, PageConfiguration.class));
		this.npi = npi;
	}

	@Override
	public void run() 
	{
		super.stepStatus = Status.IN_PROGRESS;
		logger.info(super.print());
		
		try
		{
			// https://tst.rxcc.excellus.com/pharmacies/{npi}
			String url = RxConciergeBeanConfig.envConfig().getProperty("url.home");
			driver.get(url + "/pharmacies/" + npi);
			
			// Wait for the page to load
			new PharmacyDemographicPO(super.driver, super.pageConfiguration);
			
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

