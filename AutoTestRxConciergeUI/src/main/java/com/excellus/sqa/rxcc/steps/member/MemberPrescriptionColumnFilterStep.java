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
import com.excellus.sqa.rxcc.pages.member.MemberPrescriptionClaimPO;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
//import com.excellus.sqa.step.IStep.Status;
import com.excellus.sqa.spring.BeanLoader;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 04/27/2022
 */
public class MemberPrescriptionColumnFilterStep extends AbstractUIStep {
    
    private static final Logger logger = LoggerFactory.getLogger(MemberPrescriptionColumnFilterStep.class);
    
    private final static String STEP_NAME = "MemberPrescriptionColumnFilterStep";
    private final static String STEP_DESC = "Perform filter on a given column";
    
    private final String column;
    private final String filterValue;
    private final String filterName;
    
    
    private String filterRowCount;
    private String labelRowCount;

   
    
    public MemberPrescriptionColumnFilterStep(WebDriver driver, String column, String filterValue, String filterName)
    {
        super(STEP_NAME, STEP_DESC, driver, BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
        
      
        this.column = column;
       
        this.filterValue = filterValue;
      
        this.filterName = filterName;
        
    }
    
    @Override
    public void run() {
        super.stepStatus = Status.IN_PROGRESS;
        logger.info(super.print());
        
        try
        {
            MemberPrescriptionClaimPO memberPrescriptionClaimPO = new MemberPrescriptionClaimPO(driver, pageConfiguration);
            
            memberPrescriptionClaimPO.filterPrescriptionClaims(filterName);
            memberPrescriptionClaimPO.filterDropDownPrescriptionClaims(column);
            memberPrescriptionClaimPO.filterDropDownInputTextBoxPrescriptionClaims(filterValue);
           

            filterRowCount = memberPrescriptionClaimPO.clickFirsPrescriptionClaimFilterResult(filterValue);
            
            
            logger.info("******Number of Records*********  "+ filterRowCount);
            SeleniumPageHelperAndWaiter.pause(1000);
            labelRowCount = memberPrescriptionClaimPO.filterDropDownRecordCountPrescriptionClaims();
            logger.info("******Number of Records in Label *********  "+ labelRowCount);
            memberPrescriptionClaimPO.filterClearPrescriptionClaims();
            super.stepStatus = Status.COMPLETED;

        }
        catch (Exception e)
        {
            super.stepException = e;
            super.stepStatus = Status.ERROR;
        }
        
        logger.info(print());
    }

    public String getFilterRowCount() {
        return filterRowCount;
    }

    public String getLabelRowCount() {
        return labelRowCount;
    }

    
}
