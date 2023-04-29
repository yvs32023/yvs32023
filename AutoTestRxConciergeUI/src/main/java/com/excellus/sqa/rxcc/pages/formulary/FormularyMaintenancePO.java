/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.formulary;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.Formulary;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.IPage;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * 
 * @since 10/26/2022
 */

public class FormularyMaintenancePO extends AbstractCommonPage implements IPage {

    private static final Logger logger = LoggerFactory.getLogger(FormularyMaintenancePO.class);

    @FindBy(xpath = "//p-menubar//a[text()='Rx Clinical Concierge']")
    private WebElement menuHomePage;

    @FindBy(xpath = "//div[@class='flex items-center gap-2 ng-star-inserted']/*[7]//*[name()='svg']")
    private WebElement iconFormularyMaintenance;

    @FindBy(xpath = "//rxc-formulary-maintenance/main/div/p-table/div/div[1]/div/div[2]/span/input[@placeholder='Search Formularies']")
    private WebElement textboxFormularySearch;
    
    @FindBy(xpath = "//tbody")
    private List<WebElement> tableFormularyMaintenance;
    
    @FindBy(xpath = "//rxc-formulary-maintenance/main/div/p-table/div/div[2]/table/thead/tr/th")
    private List<WebElement> columnHeaders; 

    // DTO check

    @FindBy(xpath = "//rxc-formulary-maintenance/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Formulary Code')]/preceding-sibling::th)+1]")
    private List<WebElement> labelFormularyCode;

    @FindBy(xpath = "//rxc-formulary-maintenance/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Formulary Description')]/preceding-sibling::th)+1]")
    private List<WebElement> labelFormularyDescription;

    @FindBy(xpath = "//rxc-formulary-maintenance/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Created By')]/preceding-sibling::th)+1]")
    private List<WebElement> labelCreatedBy;

    @FindBy(xpath = "//rxc-formulary-maintenance/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Create Date')]/preceding-sibling::th)+1]")
    private List<WebElement> labelCreateDate;

    @FindBy(xpath = "//rxc-formulary-maintenance/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Modified By')]/preceding-sibling::th)+1]")
    private List<WebElement> labelModifiedBy;

    @FindBy(xpath = "//rxc-formulary-maintenance/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Modified Date')]/preceding-sibling::th)+1]")
    private List<WebElement> labelModifiedDate;

    @FindBy(xpath = "//rxc-formulary-maintenance/main/div/p-table/div/div[2]/table/tbody/tr")
    private List<WebElement> rowFormularyMaintenanceRecord;

    @FindBy(xpath = "//rxc-formulary-maintenance/main/div/p-table/div/div[1]/div/p")
    private WebElement labelRecordFound;
  
    // Edit Xpath's

    @FindBy(xpath = "//tbody/tr[1]/td[7]/div/button[normalize-space()='Edit']")
    private WebElement buttonEdit;

    @FindBy(xpath = "//rxc-formulary-maintenance/main/div/p-dialog[2]/div/div/div[2]/form/input[1]")
    private WebElement inputFormularyCode;

    @FindBy(xpath = "//form/input[@placeholder='Enter Formulary Description']")
    private WebElement textboxFormularyDescription;

    @FindBy(xpath = "//button[@class='p-element p-button-secondary p-button p-component ng-star-inserted'and contains(., 'Cancel')]")
    private WebElement buttonUpdateCancel;

    @FindBy(xpath = "//button[@class='p-element p-button-primary p-button p-component ng-star-inserted'and contains(., 'Update')]")
    private WebElement buttonUpdate;
    
    @FindBy(xpath ="//button[@class='p-element p-button-primary p-button p-component ng-star-inserted' and contains(., 'Save')]")
    private WebElement buttonSave;
    
    // Add Formulary
    
    @FindBy(xpath = "//rxc-formulary-maintenance/main/div/p-table/div/div[1]/div/div[1]/button[contains(.,'Add New Formulary')]")
    private WebElement buttonAddNewFormulary;
       
    @FindBy(xpath = "//rxc-formulary-maintenance/rxc-formulary-dialog/p-dialog/div/div/div[2]/form/input[@placeholder='Enter Formulary Description']")
    private WebElement textboxAddNewFormulary;
       
    @FindBy(xpath = "//input[@formcontrolname='formularyCode']")
    private WebElement textboxFormularyCode;
    
    @FindBy(xpath = "//rxc-formulary-maintenance/main/div/p-dialog[1]/div/div/div[3]/button[contains(.,'Add')]")
    private WebElement buttonAdd;

    public FormularyMaintenancePO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
    }

    @Override
    public void waitForPageObjectToLoad() {
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, textboxFormularySearch);

    }

    /**
     * Retrieve the data of the Formulary maintenance
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */

        public List<Formulary> retrieveFormularyInfo() throws ElementNotFoundException
        {
            List<Formulary> list = new ArrayList<>();
            logger.info("***Inside retrieveFormularyInfo **** " + list);
    
            for ( WebElement row : rowFormularyMaintenanceRecord )
            {
                Formulary formularyMaintenance = new Formulary();
            
              formularyMaintenance.setFormularyCode(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[1]")));
              formularyMaintenance.setFormularyDescription(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[2]")));
              formularyMaintenance.setCreatedBy(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[3]")));
              formularyMaintenance.setCreatedDateTime(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[4]")));
              formularyMaintenance.setLastUpdatedBy(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[5]")));
              formularyMaintenance.setLastUpdatedDateTime(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[6]")));
   
                list.add(formularyMaintenance);
            }
    
            return list;
        }

    /**
     * Click the Formulary Maintenance icon
     * 
     * @throws ElementNotFoundException
     */
    public void clickFormularyMaintenanceIcon() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, iconFormularyMaintenance);
    }
    
    /**
     * Retrieve all the columns from formulary maintenance
     * @return list of columns
     */
    public List<String> retrieveColumnHeaders()
    {
        logger.info("***retrieveColumnHeaders**** " + SeleniumPageHelperAndWaiter.retrieveWebElementsText(columnHeaders));
        List<String> columnNames = SeleniumPageHelperAndWaiter.retrieveWebElementsText(columnHeaders);
        int indexNumber = columnNames.indexOf("");
        logger.info("***retrieveColumnHeaders**** " + indexNumber);
        columnNames.remove(indexNumber);
        
        logger.info("***ColumnNames**** " + columnNames);
        return columnNames;
    }

    /**
     * method used for search formulary box
     * Search value can be Type value returned from the first row
     * @throws ElementNotFoundException if issue occurs
     */

    public void formularySearch(String searchValue) throws ElementNotFoundException {
        logger.info("***Pass the Formulary search value**** " + searchValue);
        SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxFormularySearch, searchValue);   
        SeleniumPageHelperAndWaiter.pause(1000);
    }
   
    /**
     * Click the Formulary Edit 
     * 
     * @throws ElementNotFoundException
     */
    public void clickFormularyEdit() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonEdit);
    }
    
    
    /**
     * Click the Formulary Descrption 
     *      * 
     * @throws ElementNotFoundException
     */
    public void clickFormularyDescription() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, textboxFormularyDescription);
    }
    
    
    /**
     * Retrieve the Formulary Edit Form Descrption value
     *      * 
     * @throws ElementNotFoundException
     */
    public String retrieveFormularyDescription() throws ElementNotFoundException {
        clickFormularyEdit();
        clickFormularyDescription();
        String currentDescription = SeleniumPageHelperAndWaiter.retrieveTextFromTextbox(this, textboxFormularyDescription);
        logger.info("*********** currentDescription **************" + currentDescription);
        return currentDescription;
    }
    
    
    /**
     * Update the Formulary Descrption 
     *      * 
     * @throws ElementNotFoundException
     */
    public void updateFormularyDescription(String currentDescription) throws ElementNotFoundException {
        logger.info("***Pass the Formulary description value**** " + currentDescription);
        String updatedValue = currentDescription + " Testing Update";
        SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxFormularyDescription, updatedValue);
       // SeleniumPageHelperAndWaiter.clickWebElement(this, buttonUpdate);
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonSave);
    }
    
    /**
     * revert the update changes for the Formulary Descrption 
     *      * 
     * @throws ElementNotFoundException
     */
    public void revertFormularyDescription(String revertValue) throws ElementNotFoundException {
        logger.info("***Pass the Formulary description value**** " + revertValue);
        String originalValue = revertValue.replace(" Testing Update", "");
        clickFormularyEdit();
        clickFormularyDescription();
        SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxFormularyDescription, originalValue);
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonSave);

    }
   
    /**
     * Click the Add Formulary webElement
     * @throws ElementNotFoundException
     */
    public void clickAddFormulary() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonAddNewFormulary);
    }
    
    /**
     * Add the Formulary Descrption 
     * @throws ElementNotFoundException
     */
    public void enterFormularyDescription (String value) throws ElementNotFoundException {
        
        SeleniumPageHelperAndWaiter.clickWebElement(this, textboxAddNewFormulary);
        SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxAddNewFormulary, value.trim());       
    }
    
    /**
     * Return Formulary Code 
     * @throws ElementNotFoundException
     */
    public String getFormularyCode () throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.pause(1000);
        return SeleniumPageHelperAndWaiter.retrieveTextFromTextbox(this, textboxFormularyCode);
        
    }
    
    /**
     * Click the Add button 
     * @throws ElementNotFoundException
     */
    public void buttonClickAdd() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, buttonSave);
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonSave);

    }

}

