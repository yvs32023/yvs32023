/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.member;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.IPage;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 12/16/2022
 */
public class MemberCorrespondenceViewFaxPO extends AbstractCommonPage implements IPage{
    private static final Logger logger = LoggerFactory.getLogger(MemberCorrespondenceViewFaxPO.class);

    @FindBy(xpath = "//span[normalize-space()='Correspondence']")      
    private WebElement headerCorrespondence;

    @FindBy(xpath = "//rxc-correspondence-fax-component/a[normalize-space()='Outbound Fax Provider']")
    private WebElement linkOutboundFaxProvider;

    @FindBy(xpath = "//rxc-correspondence-fax-component/a[normalize-space()='Inbound Fax Provider']")
    private WebElement linkInboundFaxProvider;

    @FindBy(xpath = "//object[@type='application/pdf']")
    private WebElement pdfCheck;

    @FindBy(xpath = "//button[@class='p-ripple p-element p-button-sm p-4 p-button p-component']")
    private WebElement buttonCancel;


    /**
     * Constructor
     *
     * @param driver WebDriver for PageObject
     * @param page   PageConfiguration for the UI page
     */
    public MemberCorrespondenceViewFaxPO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
        waitForPageObjectToLoad();
    }

    @Override
    public void waitForPageObjectToLoad() {
        logger.debug("Waiting for member detail page to load");

    }

    /**
     * select the Member Correpondence Tab
     * @return 
     * @throws ElementNotFoundException if issue occurs
     */

    public void clickCorrespondence() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, headerCorrespondence);

    }

    /**
     * Click the Member Correpondence Outbound Fax Provider Link
     * @return 
     * @throws ElementNotFoundException if issue occurs
     */
    public void clickOutboundFaxProvider() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, linkOutboundFaxProvider);

    }

    /**
     * Click the Member Correpondence Inbound Fax Provider Link
     * @return 
     * @throws ElementNotFoundException if issue occurs
     */
    public void clickInboundFaxProvider() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, linkInboundFaxProvider);

    }

    /**
     * select button cancel to close the PDF
     * @return 
     * @throws ElementNotFoundException if issue occurs
     */
    public void buttonCancel() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonCancel);

    }

    /**
     * Validate Member Correpondence Inbound & Outbound Fax Provider Link
     * @return true if present else false
     * @throws ElementNotFoundException if issue occurs
     */
    public boolean isPdfPresent() throws ElementNotFoundException {
        boolean isPDFPresent = false;

        if (SeleniumPageHelperAndWaiter.isWebElementVisible(driver, pdfCheck, getElementRetry())) {
            SeleniumPageHelperAndWaiter.clickWebElement(this, pdfCheck);
            isPDFPresent =  true; 
        }
        return isPDFPresent;
    }
}
