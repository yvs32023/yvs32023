/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */


package com.excellus.sqa.rxcc.steps.provider;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.provider.ProviderDirectoryColumns;
import com.excellus.sqa.rxcc.pages.provider.ProviderDirectoryPO;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 06/30/2022
 */
public class ProviderDirectoryColumnFilterStep extends AbstractUIStep {

    private static final Logger logger = LoggerFactory.getLogger(ProviderDirectoryColumnFilterStep.class);

    private final static String STEP_NAME = "ProviderDirectoryColumnFilterStep (Colum: %s --- Filter Value: %s)";
    private final static String STEP_DESC = "Perform filter on a given column";

    private final ProviderDirectoryColumns column;
    private final String filterValue;


    private int filterRowCount;
    private String labelRowCount;
    private List<String> columnValues;

    public ProviderDirectoryColumnFilterStep(WebDriver driver, ProviderDirectoryColumns column, String filterValue)
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
            ProviderDirectoryPO providerDirectoryPO = new ProviderDirectoryPO(driver, pageConfiguration);
            providerDirectoryPO.waitForPageObjectToLoad();

            /*
             * Perform the filter search
             */

            providerDirectoryPO.iconFilterProviderDirectory(column);
            providerDirectoryPO.clickTextBoxFilterProviderDirectory(column);
            if (column == ProviderDirectoryColumns.NUMBER_OF_LOCATIONS) {
                providerDirectoryPO.filterInputSearchNumLocationsProviderDirectory(filterValue);
            }else if (column == ProviderDirectoryColumns.FAX_VERIFIED) {               
                providerDirectoryPO.filterInputSearchFaxVerifiedProviderDirectory(filterValue); 
            }else{
                providerDirectoryPO.filterInputSearchBoxProviderDirectory(filterValue);
                providerDirectoryPO.selectFilterList();
            }
            SeleniumPageHelperAndWaiter.pause(2000);

            /*
             * Retrieve the data on the column that was filtered
             */

            columnValues = providerDirectoryPO.retrieveColumnData(column);

            // Set actual to upper case for PHARMACY NAME & CITY only
            if ( column == ProviderDirectoryColumns.LAST_NAME || column == ProviderDirectoryColumns.CITY || column == ProviderDirectoryColumns.FIRST_NAME )
            {
                columnValues = columnValues.stream().map(value -> value.toUpperCase()).collect(Collectors.toList());
            }

            /*
             * Retrieve the number of rows of provider and the displayed record count
             */

            filterRowCount = columnValues.size();

            logger.info("******Number of Records*********  "+ filterRowCount);


            labelRowCount = providerDirectoryPO.filterProviderDirectoryRecordCount();
            labelRowCount = StringUtils.substringBefore(labelRowCount, ("(")) + "Found";

            logger.info("******Number of Records in Label *********  "+ labelRowCount);

            /*
             * clear the filter
             */

            if (column == ProviderDirectoryColumns.NUMBER_OF_LOCATIONS) {
                providerDirectoryPO.iconFilterProviderDirectory(column);   // get the filter textbox to appear
                providerDirectoryPO.filterClearNumLocations();             // remove the filter textbox
            }else if  (column == ProviderDirectoryColumns.FAX_VERIFIED)  {
                providerDirectoryPO.iconFilterProviderDirectory(column);
                providerDirectoryPO.filterClearFaxVerified();             
            }else {
                boolean isDisplayed = providerDirectoryPO.isFilterIconDisplayed(column.toString());
                logger.info("******isDisplayed *********  "+ isDisplayed);
                providerDirectoryPO.iconFilterProviderDirectory(column);
                providerDirectoryPO.filterClearProviderDirectory();
                providerDirectoryPO.iconFilterProviderDirectory(column); 
            }

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