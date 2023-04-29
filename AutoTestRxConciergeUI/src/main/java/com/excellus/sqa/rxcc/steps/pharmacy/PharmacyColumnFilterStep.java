/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.steps.pharmacy;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.pharmacy.PharmacyDirectoryColumns;
import com.excellus.sqa.rxcc.pages.pharmacy.PharmacyDirectoryPO;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 06/09/2022
 */
public class PharmacyColumnFilterStep extends AbstractUIStep {

    private static final Logger logger = LoggerFactory.getLogger(PharmacyColumnFilterStep.class);

    private final static String STEP_NAME = "PharmacyDirectoryColumnFilterStep (Colum: %s --- Filter Value: %s)";
    private final static String STEP_DESC = "Perform filter on a given column";

    private final PharmacyDirectoryColumns column;
    private final String filterValue;


    private int filterRowCount;
    private String labelRowCount;
    private List<String> columnValues;


    public PharmacyColumnFilterStep(WebDriver driver, PharmacyDirectoryColumns column, String filterValue)
    {
        super(String.format(STEP_NAME, column.toString(), filterValue), STEP_DESC, 
        		driver, BeanLoader.loadBean(BeanNames.PHARMACIES_PAGE, PageConfiguration.class));

        this.column = column;

        this.filterValue = filterValue;
    }

    @Override
    public void run() {
        super.stepStatus = Status.IN_PROGRESS;
        logger.info(super.print());

        try
        {
            PharmacyDirectoryPO pharmacyDirectoryPO = new PharmacyDirectoryPO(driver, pageConfiguration);
            pharmacyDirectoryPO.waitForPageObjectToLoad();
            
            /*
             * Perform the filter search
             */
            
            pharmacyDirectoryPO.iconFilterPharmacyDirectory(column);
            pharmacyDirectoryPO.clickTextBoxFilterPharmacyDirectory(column);
            pharmacyDirectoryPO.filterInputSearchBoxPharmacyDirectory(filterValue);
            SeleniumPageHelperAndWaiter.pause(2000);
            pharmacyDirectoryPO.selectFilterList();

            SeleniumPageHelperAndWaiter.pause(1000);

            /*
             * Retrieve the data on the column that was filtered
             */
            
            columnValues = pharmacyDirectoryPO.retrieveColumnData(column);
            
            // Set actual to upper case for PHARMACY NAME & CITY only
            if ( column == PharmacyDirectoryColumns.PHARMACY_NAME || column == PharmacyDirectoryColumns.CITY )
            {
            	columnValues = columnValues.stream().map(value -> value.toUpperCase()).collect(Collectors.toList());
            }
            
            /*
             * Retrieve the number of rows of pharmacy and the displayed record count
             */
            
            filterRowCount = columnValues.size();

            logger.info("******Number of Records*********  "+ filterRowCount);


            labelRowCount = pharmacyDirectoryPO.filterPharmacyDirectoryRecordCount();
            labelRowCount = StringUtils.substringBefore(labelRowCount, ("(")) + "Found";

            logger.info("******Number of Records in Label *********  "+ labelRowCount);

            boolean isDisplayed = pharmacyDirectoryPO.isFilterIconDisplayed(column.toString());
            logger.info("******isDisplayed *********  "+ isDisplayed);
            
            /*
             * clear the filter
             */
           
            if (!isDisplayed) {               
            	pharmacyDirectoryPO.iconFilterPharmacyDirectory(column);	// get the filter textbox to appear
            }
            
            pharmacyDirectoryPO.filterClearPharmacyDirectory();
            pharmacyDirectoryPO.iconFilterPharmacyDirectory(column);		// remove the filter textbox
            
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
