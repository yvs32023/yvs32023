/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.pharmacy;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.excellus.sqa.rxcc.dto.Pharmacy;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.IPage;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 03/23/2022
 */
public class PharmacyDemographicPO extends AbstractCommonPage implements IPage {

    private static final Logger logger = LoggerFactory.getLogger(PharmacyDemographicPO.class);

    @FindBy(xpath = "//rxc-pharmacy-details//p-accordion//a[contains(.,'Pharmacy Information')]")
    private WebElement headerPharmacyInformation;

   // @FindBy(xpath = "//form/p/span[normalize-space()]")
    @FindBy(xpath = "//rxc-pharmacy-details//p-accordion//p[@class='grid grid-cols-1 relative ng-star-inserted']")
    private WebElement labelStoreName;

    @FindBy(xpath = "//dt[text()='Status:']/following-sibling::dd[1]")
    private WebElement labelStatusInd;

    @FindBy(xpath = "//dt[text()='NPI:']/following-sibling::dd[1]")
    private WebElement labelNpi;

    @FindBy(xpath = "//dt[text()='Pharmacy Taxonomy Code:']/following-sibling::dd[1]")
    private WebElement labelTaxonomyCode;

    @FindBy(xpath = "//dt[normalize-space()='Pharmacy Taxonomy Description:']/following-sibling::dd[1]")
    private WebElement labelTaxonomyDescription;

    @FindBy(xpath = "//dt[text()='Address 1:']/following-sibling::dd[1]")
    private WebElement labelAddress1;

    @FindBy(xpath = "//dt[text()='Address 2:']/following-sibling::dd[1]")
    private WebElement labelAddress2;

    @FindBy(xpath = "//dt[text()='Phone Number:']/following-sibling::dd[1]")
    private WebElement labelPhoneNumber;

    @FindBy(xpath = "//dt[text()='Fax Number:']/following-sibling::dd[1]")
    private WebElement labelFaxNumber;

    @FindBy(xpath = "//dt[text()='City:']/following-sibling::dd[1]")
    private WebElement labelCity;

    @FindBy(xpath = "//dt[text()='State:']/following-sibling::dd[1]")
    private WebElement labelState;

    @FindBy(xpath = "//dt[text()='Postal code:']/following-sibling::dd[1]")
    private WebElement labelPostalCode;

    @FindBy(xpath = "//p-accordiontab/div/div[1]")
    private WebElement expandCollapsePharmacyInformation;

    /**
     * Constructor
     *
     *
     * @param driver WebDriver for PageObject
     * @param page   PageConfiguration for the UI page
     */

    public PharmacyDemographicPO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
        waitForPageObjectToLoad();
    }

    @Override
    public void waitForPageObjectToLoad() {
        logger.debug("Waiting for Pharmacy Demographic page to load");
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, headerPharmacyInformation);
    }

    /**
     * Retrieve the text of the member demographic WebElement
     * 
     * @return String that represents the text of the WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public Pharmacy retrievePharmacyInfo() throws ElementNotFoundException {
        Pharmacy pharmacy = new Pharmacy();

        pharmacy.setStoreName(isDisplayedStoreName());
        pharmacy.setStatusInd(isDisplayedPharmacyStatus());
        pharmacy.setNpi(isDisplayedPharmacyNpi());
        pharmacy.setTaxonomyCode(isDisplayedPharmacyTaxonomyCode());
        pharmacy.setTaxonomyDescr(isDisplayedPharmacyTaxonomyDescription());
        pharmacy.setAddress1(isDisplayedPharmacyAddress1());
        pharmacy.setAddress2(isDisplayedPharmacyAddress2());
        pharmacy.setPhoneNumber(isDisplayedPharmacyPhoneNumber());
        pharmacy.setFaxNumber(isDisplayedPharmacyFaxNumber());
        pharmacy.setCity(isDisplayedPharmacyCity());
        pharmacy.setState(isDisplayedPharmacyState());
        pharmacy.setPostalCode(isDisplayedPharmacyPostalCode());

        return pharmacy;

    }

    /**
     * Retrieve the text of the field PharmacyInformation header WebElement
     * @return String that represents the text of the label WebElement
     *
     */
    public String isDisplayedPharmacyInformation() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, headerPharmacyInformation);
    }

    /**
     * Retrieve the text of the field StoreName label WebElement
     * @return String that represents the text of the label WebElement
     * 
     */
    public String isDisplayedStoreName() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelStoreName);
    }

    /**
     * Retrieve the text of the field StatusIndicator label WebElement
     * @return String that represents the text of the label WebElement
     *
     */

    public String isDisplayedPharmacyStatus() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelStatusInd);
    }

    /**
     * Retrieve the text of the field NPI label WebElement
     * @return String that represents the text of the label WebElement
     *
     */

    public String isDisplayedPharmacyNpi() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelNpi);
    }

    /**
     * Retrieve the text of the field TaxonomyCode label WebElement
     * @return String that represents the text of the label WebElement
     *
     */

    public String isDisplayedPharmacyTaxonomyCode() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelTaxonomyCode);
    }

    /**
     * Retrieve the text of the field TaxonomyDescription label WebElement
     * @return String that represents the text of the label WebElement
     *
     */

    public String isDisplayedPharmacyTaxonomyDescription() throws ElementNotFoundException {
        return  SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelTaxonomyDescription);
    }

    /**
     * Retrieve the text of the field Address1 label WebElement
     * @return String that represents the text of the label WebElement
     *
     */

    public String isDisplayedPharmacyAddress1() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelAddress1);
    }

    /**
     * Retrieve the text of the field Address 2 label WebElement
     * @return String that represents the text of the label WebElement
     *
     */

    public String isDisplayedPharmacyAddress2() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelAddress2);
    }

    /**
     * Retrieve the text of the field PhoneNumber label WebElement
     * @return String that represents the text of the label WebElement
     *
     */

    public String isDisplayedPharmacyPhoneNumber() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelPhoneNumber);
    }

    /**
     * Retrieve the text of the field FaxNumber label WebElement
     * @return String that represents the text of the label WebElement
     *
     */

    public String isDisplayedPharmacyFaxNumber() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelFaxNumber);
    }

    /**
     * Retrieve the text of the field City label WebElement
     * @return String that represents the text of the label WebElement
     *
     */

    public String isDisplayedPharmacyCity() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelCity);
    }

    /**
     * Retrieve the text of the field State label WebElement
     * @return String that represents the text of the label WebElement
     *
     */

    public String isDisplayedPharmacyState() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelState);
    }

    /**
     * Retrieve the text of the field PostalCode label WebElement
     * @return String that represents the text of the label WebElement
     *
     */

    public String isDisplayedPharmacyPostalCode() throws ElementNotFoundException {
     return   StringUtils.replace(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelPostalCode), "-", "");
    }

    /**
     * method to collapse Pharmacy Information
     * @throws error if issue occurs
     */

    public void isCollapsedPharmacyInformation() {
        try {
            boolean headerCollapsePharmacyInformation = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, expandCollapsePharmacyInformation, 1);
            if ( headerCollapsePharmacyInformation )
                SeleniumPageHelperAndWaiter.clickWebElement(this, expandCollapsePharmacyInformation);

        }
        catch (Exception e)
        {
            logger.error("Pharmacy Info Collapse error occured");

        }
    }
    /**
     * method to Expand Pharmacy Information
     * @throws error if issue occurs
     */

    public void isExpandedPharmacyInformation() {
        try {
            boolean headerExpandPharmacyInformation = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, expandCollapsePharmacyInformation, 1);
            if ( headerExpandPharmacyInformation )
                SeleniumPageHelperAndWaiter.clickWebElement(this, expandCollapsePharmacyInformation);

        }
        catch (Exception e)
        {
            logger.error("Pharmacy Info Expand error occured");

        }
    }
}
  
     

