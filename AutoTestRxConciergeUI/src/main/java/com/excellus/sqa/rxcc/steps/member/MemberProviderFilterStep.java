/**
 * 
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.steps.member;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.member.MemberProviderTabColumns;
import com.excellus.sqa.rxcc.dto.provider.ProviderDirectoryColumns;
import com.excellus.sqa.rxcc.pages.member.MemberDetailsProviderPO;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 03/28/2023
 */
public class MemberProviderFilterStep extends AbstractUIStep {

    private static final Logger logger = LoggerFactory.getLogger(MemberProviderFilterStep.class);

    private final static String STEP_NAME = "MemberProviderFilterStep";
    private final static String STEP_DESC = "Perform filter on a given column";

    private final MemberProviderTabColumns column;
    private final String filterValue;

    private String filterRowCount;
    private String labelRowCount;

    public MemberProviderFilterStep(WebDriver driver, MemberProviderTabColumns column, String filterValue) {
        super(STEP_NAME, STEP_DESC, driver, BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));      

        this.column = column;

        this.filterValue = filterValue;

    }

    @Override
    public void run() {
        super.stepStatus = Status.IN_PROGRESS;
        logger.info(super.print());
        try {
            MemberDetailsProviderPO memberProviderPO = new MemberDetailsProviderPO(driver, pageConfiguration);

            memberProviderPO.filterProvider(column);
            memberProviderPO.filterDropDownProvider(column);

            if (column == MemberProviderTabColumns.PROVIDER_VERIFIED) {
                memberProviderPO.filterInputSearchFaxVerifiedProvider(filterValue); 
                memberProviderPO.applyFilterSelection();
                filterRowCount = memberProviderPO.getProviderVerifiedFilterResult();
            }else {
                memberProviderPO.filterDropDownInputTextBoxProvider(filterValue);
                filterRowCount = memberProviderPO.clickFirstProviderFilterResult(filterValue);
                SeleniumPageHelperAndWaiter.pause(1000);
            }         
            logger.info("******Number of Records*********  "+ filterRowCount);
            SeleniumPageHelperAndWaiter.pause(1000);
            labelRowCount = memberProviderPO.filterDropDownRecordCountProvider();
            logger.info("******Number of Records in Label *********  "+ labelRowCount);
            memberProviderPO.filterClearProvider();
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
