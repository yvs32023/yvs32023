/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
/**
 * 
 */
package com.excellus.sqa.rxcc.steps.interventionrules;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.interventionrules.InterventionRulesColumns;
import com.excellus.sqa.rxcc.pages.interventionrule.InterventionRuleLibraryPO;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;
/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 09/08/2022
 */
/**
 * @author hzia
 *
 */
public class InterventionRulesColumnFilterStep extends AbstractUIStep {

    private static final Logger logger = LoggerFactory.getLogger(InterventionRulesColumnFilterStep.class);

    private final static String STEP_NAME = "InterventionRulesColumnFilterStep (Colum: %s --- Filter Value: %s)";
    private final static String STEP_DESC = "Perform filter on a given column";

    private final InterventionRulesColumns column;
    private final String filterValue;


    private int filterRowCount;
    private String labelRowCount;
    private List<String> columnValues;


    public InterventionRulesColumnFilterStep(WebDriver driver, InterventionRulesColumns column, String filterValue)
    {
        super(String.format(STEP_NAME, column.toString(), filterValue), STEP_DESC, 
                driver, BeanLoader.loadBean("providerPage", PageConfiguration.class));

        this.column = column;

        this.filterValue = filterValue;
    }

    @Override
    public void run() {
        super.stepStatus = Status.IN_PROGRESS;
        logger.info(super.print());

        try
        {
            InterventionRuleLibraryPO interventionRuleLibraryPO = new InterventionRuleLibraryPO(driver, pageConfiguration);
            interventionRuleLibraryPO.waitForPageObjectToLoad();
            
            /*
             * Perform the filter search
             */
            
            interventionRuleLibraryPO.iconFilterRuleLibrary(column);
            interventionRuleLibraryPO.clickTextBoxFilterRuleLibrary(column);
            interventionRuleLibraryPO.filterInputSearchBoxRuleLibrary(filterValue);
            interventionRuleLibraryPO.selectFilterList();

            SeleniumPageHelperAndWaiter.pause(1000);

            /*
             * Retrieve the data on the column that was filtered
             */
            
            columnValues = interventionRuleLibraryPO.retrieveColumnData(column);
            
            // Set actual to upper case for RULE NAME & STATUS only
            if ( column == InterventionRulesColumns.RULE_NAME || column == InterventionRulesColumns.STATUS )
            {
                columnValues = columnValues.stream().map(value -> value.toUpperCase()).collect(Collectors.toList());
            }
            
            /*
             * Retrieve the number of rows of rules and the displayed record count
             */
            
            filterRowCount = columnValues.size();

            logger.info("******Number of Records*********  "+ filterRowCount);


            labelRowCount = interventionRuleLibraryPO.filterRuleLibraryRecordCount();
            labelRowCount = StringUtils.substringBefore(labelRowCount, ("(")) + "Found";

            logger.info("******Number of Records in Label *********  "+ labelRowCount);

            boolean isDisplayed = interventionRuleLibraryPO.isFilterIconDisplayed(column.toString());
            logger.info("******isDisplayed *********  "+ isDisplayed);
            
            /*
             * clear the filter
             */
           
            if (!isDisplayed) {               
                interventionRuleLibraryPO.iconFilterRuleLibrary(column);    // get the filter textbox to appear
            }
            
            interventionRuleLibraryPO.filterClearRuleLibrary();
            interventionRuleLibraryPO.iconFilterRuleLibrary(column);        // remove the filter textbox
            
            super.stepStatus = Status.COMPLETED;

        }
        catch (Exception e)
        {
            super.stepException = e;
            super.stepStatus = Status.ERROR;
        }

        logger.info(print());
    }

    public int getFilterRowCount() {
        return filterRowCount;
    }

    public String getLabelRowCount() {
        return labelRowCount;
    }

    public List<String> getColumnValues() {
        return this.columnValues;
    }
}