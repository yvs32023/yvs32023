/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.interventionrule;
import java.util.List;

import org.openqa.selenium.By;
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
 * @since 10/05/2022
 */
public class CreateInterventionPO extends AbstractCommonPage implements IPage {
    private static final Logger logger = LoggerFactory.getLogger(CreateInterventionPO.class);
    @FindBy(xpath = "//p-menubar//a[text()='Rx Clinical Concierge']")
    private WebElement menuHomePage;

    @FindBy(xpath = "/html/body/rxc-app/ng-component/rxc-header/header/p-menubar/div/div[1]/div/svg-icon[4]")
    private WebElement iconInterventionRuleLib;

    final String SECTION_PAGE_XPATH = "//rxc-intervention-rule-library";

    @FindBy(xpath = SECTION_PAGE_XPATH)
    private WebElement sectionInterventionRuleLibrary;

    @FindBy(xpath = "//button[contains(@label,'Add new Intervention Rules')]")
    private WebElement labelAddNewInterventionRules;

    @FindBy(xpath = "//rxc-target-drug/rxc-selected-drugs-table/p-table/div/div[1]/div/button[contains(., 'Add Target')]")
    private WebElement buttonAddTarget;

    @FindBy(xpath = "//input[@formcontrolname='search']")
    private WebElement searchGPI;

    //   @FindBy(xpath = "//button[@icon ='pi pi-save']")  
    @FindBy(xpath = "//span[@class='p-button-icon pi pi-save']")
    private WebElement buttonSave;

    @FindBy(xpath = "//rxc-wizard-button-bar/button[@icon ='pi pi-chevron-right']")
    private WebElement clickRightIcon;

    @FindBy(xpath = "//rxc-selected-drugs-table/p-table/div/div[1]/div/button[contains(., 'Add Alternative')]")
    private WebElement labelAddAlternative;

    @FindBy(xpath = "//button[@type='button']//span[@class='p-button-icon pi pi-chevron-right']")
    private WebElement buttonExpandAlternative;

    @FindBy(xpath = "//rxc-copy-text[@copytexttitle='Product name']")
    private WebElement copyTextProductName;

    @FindBy(xpath = "//rxc-fax-alternative-text/textarea[@id='faxAlternativeText']")
    private WebElement textFaxAlternative;

    @FindBy(xpath = "//rxc-audience-ui/div/button/span[normalize-space()='Add A Formulary']")
    private WebElement buttonAddFormulary;
    
    @FindBy(xpath = "//rxc-selected-drugs-table/p-table/div/div[1]/div/button[contains(., 'Add Prerequisite')]")
    private WebElement buttonAddPreReq;

    @FindBy(xpath = "//rxc-audience/rxc-audience-ui/div/button/span[normalize-space()='Add A Formulary']")
    private WebElement buttonAddFormularyRow;

    @FindBy(xpath = "//rxc-audience-row-ui/form/div/p-dropdown[@placeholder='Formulary']")
    private WebElement dropdownFormulary;

    //for Formulary Row 2 and Row 3
    private String dropdownFormularyRows = "//rxc-audience-ui/div/form/rxc-audience-row/rxc-audience-row-ui/form/div[1]/p-dropdown/div/div[2]/span"; 

    private String rowFilter = "//form/div[1]/p-dropdown/div/div[3]/div/ul/p-dropdownitem[%d]"; 

    private final String rowFilterSearchResultXpath = "//form/div[1]/p-dropdown/div/div[3]/div/ul/p-dropdownitem";

    @FindBy(xpath = rowFilterSearchResultXpath)
    private List<WebElement> rowSearchResults;

    @FindBy(xpath = "//rxc-audience-row-ui/form/div/p-multiselect[@placeholder='Tenants']")
    private WebElement dropdownTenants;

    //for dropdown Tenant Row 2 and Row 3
    private String dropdownTenantsRows = "//rxc-audience-ui/div/rxc-audience-row[%d]/rxc-audience-row-ui/form/div[2]/p-multiselect/div/div[3]/span"; 

    @FindBy(xpath = "//rxc-audience-row-ui/form/div/p-multiselect[@placeholder='Group']")
    private WebElement dropdownGroup;

    //for dropdown Group Row 2 and Row 3
    private String dropdownGroupRows = "//rxc-audience-ui/div/rxc-audience-row[%d]/rxc-audience-row-ui/form/div[3]/p-multiselect/div/div[3]/span";

    @FindBy(xpath= "//rxc-audience-ui/div/rxc-audience-row[%d]/rxc-audience-row-ui/form/div[3]/p-multiselect/div/div[4]/div[1]/div[1]/div[2]")
    private WebElement checkboxGroup;

    @FindBy(xpath = "//div[@role='checkbox']")
    private WebElement checkboxSelectAll;

    @FindBy(xpath = "//p-multiselect/div/div[4]/div[1]/button[@type='button']")
    private WebElement buttonClose;

    @FindBy(xpath = "//form/div/p-dropdown[@placeholder='select a Intervention Type']")
    private WebElement dropdownInterventionType;

    @FindBy(xpath = "//p-dropdownitem[1]/li[1]/span[1]")
    private WebElement selectInterventionType;
    
    @FindBy(xpath = "//p-dropdownitem[2]/li[1]/span[1]")
    private WebElement selectInterventionTypeFax;

    @FindBy(xpath = "//input[@name='formName']")
    private WebElement textRuleName;

    @FindBy(xpath = "//button/span[normalize-space()='Save']")
    private WebElement buttonReviewSave;

    @FindBy(xpath = "//p-toastitem//button[contains(@class, 'p-toast-icon-close')]")
    private WebElement iconClose;
    
    //Checkbox Xpaths
 
    @FindBy(xpath = "//tbody//tr//td[3]//p-checkbox//div//div[2]")
    private WebElement checkboxShowOnFaxForm;
    
    //Input Xpaths
    
    @FindBy(xpath = "//div/div/rxc-average-length-therapy/p-inputnumber/span/input")
    private WebElement inputAverageLengthTherapy;
    
    @FindBy(xpath = "//main//div//rxc-fax-form//div//form//div[1]//textarea")
    private WebElement inputSavingsInformation;
    
    //Button Xpaths
    
    @FindBy(xpath = "//div/rxc-fax-form/rxc-wizard-button-bar/div/button/span[1]")
    private WebElement buttonPreviewFax;
    
    @FindBy(xpath = "//rxc-rule-type-dialog//p-dialog//div//div//div[2]//div//div[1]//p-radiobutton//div//div[2]")
    private WebElement radioButtonStandardIntervention;
    
    @FindBy(xpath = "//rxc-rule-type-dialog//p-dialog//div//div//div[3]//p-button[2]//button")
    private WebElement buttonInterventionTypeDiag;
    
    //Window Xpaths
    
    @FindBy(xpath = "//main/div/rxc-fax-form/p-dialog/div")
    private WebElement windowFaxForm;
    
    @FindBy(xpath = "//main/div/rxc-fax-form/p-dialog/div/div/div[1]/div/button/span[1]")
    private WebElement buttonClosePDF;
    
    @FindBy(xpath = "//object[@type='application/pdf']")
    private WebElement embeddedPDF;
    
    @FindBy(xpath = "//rxc-selected-drugs-table/p-table/div/div[2]/table/tbody/tr/td[2]/rxc-criteria-summary")
    private WebElement targetDrugText;
    

    public CreateInterventionPO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
    }

    @Override
    public void waitForPageObjectToLoad() {
        logger.debug("Waiting for Intervention Rule Library data to load");
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, sectionInterventionRuleLibrary);

    }
    /**
     * Click the Intervention Rule Library icon
     * 
     * @throws ElementNotFoundException
     */
    public void clickInterventionRuleLibrary() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, iconInterventionRuleLib);
    }

    /**
     * Click Add New Intervention Rule Library
     * 
     * @throws ElementNotFoundException
     */
    public void clickAddNewInterventionRuleLibrary() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, labelAddNewInterventionRules);
    }

    /**
     * Click Add Target
     * 
     * @throws ElementNotFoundException
     */
    public void clickAddTarget() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonAddTarget);
    }
    /**
     * Click Add PreRequisite
     * 
     * @throws ElementNotFoundException
     */
    public void clickAddPreReq() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonAddPreReq);
    }
    /**
     * Enter and Search GPI  
     * @throws ElementNotFoundException
     */
    public void enterGPI (String value) throws ElementNotFoundException {

        SeleniumPageHelperAndWaiter.clickWebElement(this, searchGPI);
        SeleniumPageHelperAndWaiter.enterTextByWebElement(this, searchGPI, value.trim());       
    }

    /**
     * Click Save
     * @throws ElementNotFoundException
     */
    public void clickSave() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this,buttonSave);
        //  SeleniumPageHelperAndWaiter.clickWebElement(this, buttonSave);
        //  Using Xpath instead of webElement buttonSave to avoid timeout exception error during run time.
        SeleniumPageHelperAndWaiter.clickBy(this, By.xpath("//span[@class='p-button-icon pi pi-save']")); 
    } 
    /**
     * Click Right Icon
     * @throws ElementNotFoundException
     */
    public void clickRightIcon() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, clickRightIcon);
    }

    /**
     * Click Add Alternative
     * 
     * @throws ElementNotFoundException
     */
    public void clickAddAlternative() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, labelAddAlternative);
    }

    /**
     * Expand Filter Criteria icon
     * @throws ElementNotFoundException
     */
    public void clickIconFilterCriteria() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonExpandAlternative);
    }

    /**
     * Select Copy Product Name icon
     * @throws ElementNotFoundException
     */
    public void clickCopyProductName() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, copyTextProductName);
    }

    /**
     * Enter and Fax Alternative Text  
     * @throws ElementNotFoundException
     */
    public void enterTextFaxAlternative (String value) throws ElementNotFoundException {

        SeleniumPageHelperAndWaiter.clickWebElement(this, textFaxAlternative);
        SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textFaxAlternative, value.trim());       
    }

    /**
     * Click Add Formulary
     * @throws ElementNotFoundException
     */
    public void clickAddFormulary() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonAddFormulary);
    }

    /**
     * Click Add Formulary Row
     * @throws ElementNotFoundException
     */
    public void clickAddFormularyRow() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonAddFormularyRow);
    }

    /**
     * Click Add Formulary Data
     * @throws ElementNotFoundException
     */
    public void clickAddFormularyData() throws ElementNotFoundException {

        int numberOfRows = 3;
        // for loop  
        for (int i = 1; i <= numberOfRows; ++i) {

            filterFormulary(i);
            SeleniumPageHelperAndWaiter.pause(500);
            filterTenants(i);
            SeleniumPageHelperAndWaiter.pause(1000);
            filterGroup(i);
            SeleniumPageHelperAndWaiter.pause(1000);
            if (i < numberOfRows) {
                clickAddFormularyRow();
            }
        }
    }
    /**
     * Filter for the Formulary 
     * Select the filter by passing the filter name
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterFormulary(int index) throws ElementNotFoundException {
        if (index == 1) {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownFormulary);
            SeleniumPageHelperAndWaiter.pause(1000);
            SeleniumPageHelperAndWaiter.clickBy(this, By.xpath( String.format(rowFilter, index) ));
            SeleniumPageHelperAndWaiter.pause(1000);// wait to avoid stale element exception
        }else {
            SeleniumPageHelperAndWaiter.clickBy(this, By.xpath( String.format(dropdownFormularyRows, 1) ));
            SeleniumPageHelperAndWaiter.pause(1000);
            SeleniumPageHelperAndWaiter.clickBy(this, By.xpath( String.format(rowFilter, 1) ));
            SeleniumPageHelperAndWaiter.pause(1000);  // wait to avoid stale element exception
        }
    }

    /**
     * Filter for the Tenants 
     * Select the filter by passing the filter name
     * @throws ElementNotFoundException if issue occurs
     */ 

    public void filterTenants(int index) throws ElementNotFoundException {

        if (index == 1){
            SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownTenants);
        } else {
            SeleniumPageHelperAndWaiter.clickBy(this, By.xpath( String.format(dropdownTenantsRows, index) ));          
        }
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, checkboxSelectAll);
        SeleniumPageHelperAndWaiter.clickWebElement(driver, checkboxSelectAll );
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, buttonClose);
        SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonClose );
        SeleniumPageHelperAndWaiter.pause(1000);
    }    

    /**
     * Filter for the Group 
     * Select the filter by passing the filter name
     * @throws ElementNotFoundException if issue occurs
     */

    public void filterGroup(int index) throws ElementNotFoundException {
        if (index == 1) {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownGroup);
            SeleniumPageHelperAndWaiter.pause(1000);
        }else {
            SeleniumPageHelperAndWaiter.clickBy(this, By.xpath( String.format(dropdownGroupRows, index) ));
            SeleniumPageHelperAndWaiter.pause(2000);
        }
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, checkboxSelectAll);   
        SeleniumPageHelperAndWaiter.pause(3000);  // wait to avoid stale element exception
        SeleniumPageHelperAndWaiter.clickBy(this,By.xpath("//div[@role='checkbox']"));
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, buttonClose);
        SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonClose );        
        SeleniumPageHelperAndWaiter.pause(1000);
    }

    /**
     * Filter for the Intervention Type 
     * Select the filter by passing the filter name
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterInterventionType() throws ElementNotFoundException {

        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, dropdownInterventionType);
        SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownInterventionType);
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, selectInterventionType);
        SeleniumPageHelperAndWaiter.clickWebElement(driver, selectInterventionType);  
        SeleniumPageHelperAndWaiter.pause(3000);
    }
    
    /**
     * Filter for the Intervention Type for Fax 
     * Select the filter by passing the filter name
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterInterventionTypeFax() throws ElementNotFoundException {

        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, dropdownInterventionType);
        SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownInterventionType);
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, selectInterventionTypeFax);
        SeleniumPageHelperAndWaiter.clickWebElement(driver, selectInterventionTypeFax);  
        SeleniumPageHelperAndWaiter.pause(3000);
    }

    /**
     * Enter Rule Name  
     * @throws ElementNotFoundException
     */
    public void enterRuleName (String value) throws ElementNotFoundException {

        SeleniumPageHelperAndWaiter.clickWebElement(this, textRuleName);
        SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textRuleName, value.trim());       
    }

    /**
     * Click Review Save
     * @throws ElementNotFoundException
     */
    public void clickReviewSave() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonReviewSave);
    }

    /**
     * Close the confirmation icon
     * 
     * @throws ElementNotFoundException
     */
    public void closeIcon() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this,iconClose);
        SeleniumPageHelperAndWaiter.clickWebElement(this, iconClose);
    }
    /**
     * Checks if CheckBox Value is false 
     * @throws ElementNotFoundException
     */
    public void checkCheckbox(WebElement element) throws ElementNotFoundException
    {
        String value = SeleniumPageHelperAndWaiter.retrieveAttribute(this, element, "aria-checked");

        if ( value=="false" ) {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, element);
        }
        else if ( value=="true" ) {
        	//do nothing
        }
    }
    /**
     * Check Show on Fax Form
     * @throws ElementNotFoundException
     */
    public void checkShowOnFax() throws ElementNotFoundException{
    	checkCheckbox(checkboxShowOnFaxForm);
    }
    /**
     * Enter Average Length Of Therapy 
     * @throws ElementNotFoundException
     */
    public void enterAverLenOfTherapy (String value) throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.enterTextByWebElement(this, inputAverageLengthTherapy, value);       
    }
    /**
     * Enter Savings Information 
     * @throws ElementNotFoundException
     */
    public void enterSavingsInformation (String value) throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.enterTextByWebElement(this, inputSavingsInformation, value);       
    }
    /**
     * Click Preview Fax Button
     * @throws ElementNotFoundException
     */
    public void clickPreviewFax() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonPreviewFax);
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, windowFaxForm);
    }
    /**
     * Verify PDF Fax Window Opens
     * @throws ElementNotFoundException
     */
    public void verifyFax() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.isWebElementVisible(driver, windowFaxForm, getElementRetry());
    }
    /**
     * Select Intervention Type
     * @throws ElementNotFoundException
     */
    public void selectInterventionType() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, radioButtonStandardIntervention);
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonInterventionTypeDiag);
    }
    /**
     * Close Preview Fax Window
     * @throws ElementNotFoundException
     */
    public void clickClosePDF() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonClosePDF);
    }
    /**
     * Retrieve Fax PDF to Text
     * @throws ElementNotFoundException
     */
    public String retrieveEmdededPDFData() throws ElementNotFoundException {
    	// Set the embedded PDF locator
    			SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, embeddedPDF);	// wait for the PDF to load
    			
    			// Grab the PDF data in 64bit format
    			String dataValue = SeleniumPageHelperAndWaiter.retrieveAttribute(this, embeddedPDF, "data");
    			return dataValue;
    }
    /**
     * Close Preview Fax Window
     * @throws ElementNotFoundException
     */
    public void retrieveTargetDrugName() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.retrieveWebElementText(this, targetDrugText);
    }
    public void retrievePreDrugName() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.retrieveWebElementText(this, targetDrugText);
    }
}



