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
import com.excellus.sqa.rxcc.dto.Pharmacy;
import com.excellus.sqa.rxcc.pages.pharmacy.PharmacyDemographicPO;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * Retrieve the pharmacy demographics
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 04/01/2022
 */
public class RetrievePharmacyDemographicStep extends AbstractUIStep {

	private static final Logger logger = LoggerFactory.getLogger(RetrievePharmacyDemographicStep.class);

	private final static String STEP_NAME = "RetrievePharmacyDemographic";
	private final static String STEP_DESC = "Retrieve pharmacy demographic data";
	
	private Pharmacy pharmacy;
	
	/**
	 * Constructor - this step assumes that the pharmacy demography web page is up
	 * 
	 * @param driver
	 */
	public RetrievePharmacyDemographicStep(WebDriver driver) 
	{
		super(STEP_NAME, STEP_DESC, driver, BeanLoader.loadBean(BeanNames.PHARMACIES_PAGE, PageConfiguration.class));
	}
	
	@Override
	public void run() {
		super.stepStatus = Status.IN_PROGRESS;
		logger.info(super.print());
		
		try
		{
			PharmacyDemographicPO pharmacyDemographicPO = new PharmacyDemographicPO(driver, pageConfiguration);
			
			pharmacy = pharmacyDemographicPO.retrievePharmacyInfo();
			
			super.stepStatus = Status.COMPLETED;
		}
		catch (Exception e)
		{
			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}
		
		logger.info(print());
	}

	public Pharmacy getPharmacy() {
		return pharmacy;
	}
}

