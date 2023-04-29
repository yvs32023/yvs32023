/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */

package com.excellus.sqa.rxcc.steps.formulary;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.pages.formulary.FormularyGroupsPO;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 09/29/2022
 */
public class FormularyGroupColumnFilterStep extends AbstractUIStep {
    
    private static final Logger logger = LoggerFactory.getLogger(FormularyGroupColumnFilterStep.class);
    
    private final static String STEP_NAME = "FormularyGroupColumnFilterStep";
    private final static String STEP_DESC = "Perform filter on a given column";
    
    private final String column;
    private final String filterValue;
    private final String filterName;
    
    
    private String filterRowCount;
    private String labelRowCount;

   
    
    public FormularyGroupColumnFilterStep(WebDriver driver, String column, String filterValue, String filterName)
    {
        super(STEP_NAME, STEP_DESC, driver, BeanLoader.loadBean(BeanNames.PROVIDER_PAGE, PageConfiguration.class));
        
      
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
            FormularyGroupsPO formularyGroupsPO = new FormularyGroupsPO(driver, pageConfiguration);
            
            formularyGroupsPO.filterFormularyGroups(filterName);
            formularyGroupsPO.filterDropDownFormularyGroup(column);
            formularyGroupsPO.filterDropDownInputTextBoxFormularyGroup(filterValue);
            formularyGroupsPO.selectFilterSelectionFormularyGroup();
            formularyGroupsPO.closeFilterSelectionFormularyGroup();
            formularyGroupsPO.filterApplyFormularyGroup();
            filterRowCount = formularyGroupsPO.clickFirsFormularyGroupFilterResult(filterValue);
            
            logger.info("******Number of Records*********  "+ filterRowCount);
            SeleniumPageHelperAndWaiter.pause(3000);
            labelRowCount = formularyGroupsPO.filterDropDownRecordCountFormularyGroup();
            logger.info("******Number of Records in Label *********  "+ labelRowCount);
            formularyGroupsPO.filterClearFormularyGroup();
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