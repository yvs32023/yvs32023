/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
/**
 * 
 */
package com.excellus.sqa.rxcc.steps.member;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.member.MemberInterventionsColumns;
import com.excellus.sqa.rxcc.pages.member.MemberInterventionsPO;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 10/04/2022
 */
/**
 * @author hzia
 *
 */
public class MemberInterventionsColumnFilterStep extends AbstractUIStep {

    private static final Logger logger = LoggerFactory.getLogger(MemberInterventionsColumnFilterStep.class);

    private final static String STEP_NAME = "MemberInterventionsColumnFilterStep (Colum: %s --- Filter Value: %s)";
    private final static String STEP_DESC = "Perform filter on a given column";

    private final MemberInterventionsColumns column;
    private final String filterValue;


    private int filterRowCount;
    private String labelRowCount;
    private List<String> columnValues;


    public MemberInterventionsColumnFilterStep(WebDriver driver, MemberInterventionsColumns column, String filterValue)
    {
        super(String.format(STEP_NAME, column.toString(), filterValue), STEP_DESC, 
                driver, BeanLoader.loadBean(BeanNames.PROVIDER_PAGE, PageConfiguration.class));

        this.column = column;

        this.filterValue = filterValue;
    }

    @Override
    public void run() {
        super.stepStatus = Status.IN_PROGRESS;
        logger.info(super.print());

        try
        {
            MemberInterventionsPO memberInterventionsPO = new MemberInterventionsPO(driver, pageConfiguration);
            memberInterventionsPO.waitForPageObjectToLoad();
            
            /*
             * Perform the filter search
             */
            if (column == MemberInterventionsColumns.CREATED_DATE)
            {
            	memberInterventionsPO.iconFilterMemberInterventions(column);
            	memberInterventionsPO.clickTextBoxFilterMemberInterventions(column);
            	memberInterventionsPO.filterInputSearchBoxMemberInterventionsCreatedDate(filterValue);
            	SeleniumPageHelperAndWaiter.pause(2000);
            	memberInterventionsPO.clickEnterCreatedDateFilter();
            	memberInterventionsPO.clickApply();

            	SeleniumPageHelperAndWaiter.pause(1000);
            }
            if (column == MemberInterventionsColumns.TARGET_DRUG)
            {
            	memberInterventionsPO.iconFilterMemberInterventions(column);
            	memberInterventionsPO.selectTargetDrugDropDown(filterValue);
            	SeleniumPageHelperAndWaiter.pause(2000);
            	memberInterventionsPO.clickApply();

            	SeleniumPageHelperAndWaiter.pause(1000);
            }
            if (column == MemberInterventionsColumns.PROVIDER)
            {
            	memberInterventionsPO.iconFilterMemberInterventions(column);
            	memberInterventionsPO.selectTargetDrugDropDown(filterValue);
            	SeleniumPageHelperAndWaiter.pause(2000);
            	memberInterventionsPO.clickApply();

            	SeleniumPageHelperAndWaiter.pause(1000);
            }
            if (column == MemberInterventionsColumns.MEMBER_COST)
            {
            	memberInterventionsPO.iconFilterMemberInterventions(column);
            	memberInterventionsPO.selectTargetDrugDropDown(filterValue);
            	SeleniumPageHelperAndWaiter.pause(2000);
            	memberInterventionsPO.clickApply();

            	SeleniumPageHelperAndWaiter.pause(1000);
            }
            if (column == MemberInterventionsColumns.PLAN_COST)
            {
            	memberInterventionsPO.iconFilterMemberInterventions(column);
            	memberInterventionsPO.selectTargetDrugDropDown(filterValue);
            	SeleniumPageHelperAndWaiter.pause(2000);
            	memberInterventionsPO.clickApply();

            	SeleniumPageHelperAndWaiter.pause(1000);
            }
            /*
             * Retrieve the data on the column that was filtered
             */
            
            columnValues = memberInterventionsPO.retrieveColumnData(column);
            
            // Set actual to upper case for TARGET_DRUG & PROVIDER only
            if ( column == MemberInterventionsColumns.TARGET_DRUG || column == MemberInterventionsColumns.PROVIDER )
            {
                columnValues = columnValues.stream().map(value -> value.toUpperCase()).collect(Collectors.toList());
            }
            
            if ( column == MemberInterventionsColumns.MEMBER_COST || column == MemberInterventionsColumns.PLAN_COST )
            {
                columnValues = columnValues.stream().map(value -> value.replaceAll("[$,]", "")).collect(Collectors.toList());
            }
            
            /*
             * Retrieve the number of rows of interventions and the displayed record count
             */
            
            filterRowCount = columnValues.size();

            logger.info("******Number of Records*********  "+ filterRowCount);


            labelRowCount = memberInterventionsPO.filterMemberInterventionsRecordCount();
            labelRowCount = StringUtils.substringBefore(labelRowCount, ("(")) + "Found";

            logger.info("******Number of Records in Label *********  "+ labelRowCount);

            boolean isDisplayed = memberInterventionsPO.isFilterIconDisplayed(column.toString());
            logger.info("******isDisplayed *********  "+ isDisplayed);
            
            /*
             * clear the filter
             */
           
//            if (!isDisplayed) {               
//                memberInterventionsPO.iconFilterMemberInterventions(column);    // get the filter textbox to appear
//            }
//            
//            memberInterventionsPO.filterClearMemberInterventions();
//            memberInterventionsPO.iconFilterMemberInterventions(column);        // remove the filter textbox
            
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

