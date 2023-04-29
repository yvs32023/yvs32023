/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.member;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.MemberLegacyIntervention;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.IPage;
import com.excellus.sqa.spring.BeanLoader;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 09/08/2022
 */
public class MemberLegacyInterventionPO extends AbstractCommonPage implements IPage {
private static final Logger logger = LoggerFactory.getLogger(MemberLegacyInterventionPO.class);
    
    @FindBy(xpath = "//span[normalize-space()='Legacy Intervention']")
    private WebElement headerLegacyIntervention;
    
    final String SECTION_PAGE_XPATH = "//rxc-member-legacy-interventions";
    
    @FindBy(xpath = SECTION_PAGE_XPATH)
    private WebElement sectionLegacyIntervention;
    
    @FindBy(xpath = SECTION_PAGE_XPATH + "//p[contains(text(), 'Records')]")
    private WebElement labelRecords;
    
    @FindBy(xpath = SECTION_PAGE_XPATH + "//table/thead/tr/th")
    private List<WebElement> columnNames;
    
    @FindBy(xpath = SECTION_PAGE_XPATH + "//table/tbody/tr") 
    private List<WebElement> rowRecords;
    
    private String rowRecordXpath = SECTION_PAGE_XPATH + "//p-table/div/div[2]/table/tbody/tr[%d]";   

    @FindBy(xpath = SECTION_PAGE_XPATH + "//table/tbody/tr/td/div")
    private WebElement labelOutcomeNotes; 
   
    private By labelInterventionDate    = By.xpath(".//td[count(//tr/th[contains(.,'Intervention Date')]/preceding-sibling::th)+1]");
    private By labelDrugFocus =           By.xpath(".//td[count(//tr/th[contains(.,'Drug Focus')]/preceding-sibling::th)+1]");
    private By labelReply    =            By.xpath(".//td[count(//tr/th[contains(.,'Reply')]/preceding-sibling::th)+1]");
    private By labelInterventionName =    By.xpath(".//td[count(//tr/th[contains(.,'Intervention Name')]/preceding-sibling::th)+1]");  
    private By labelOutcomeDocumentedBy=  By.xpath(".//td[count(//tr/th[contains(.,'Outcome Documented By')]/preceding-sibling::th)+1]");
    private By labelInterventionMadeBy =  By.xpath(".//td[count(//tr/th[contains(.,'Intervention Made By')]/preceding-sibling::th)+1]");
    private By labelConversionDate  =     By.xpath(".//td[count(//tr/th[contains(.,'Conversion Date')]/preceding-sibling::th)+1]");  
   
    private By buttonExpand         =     By.xpath(".//td/button/span[contains(@class,'p-button-icon')]");  
   
    
    public MemberLegacyInterventionPO(WebDriver driver) {
        super(driver, BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
    }

    /**
     * Constructor
     *
     * @param driver WebDriver for PageObject
     * @param page   PageConfiguration for the UI page
     */
    public MemberLegacyInterventionPO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
    }

    @Override
    public void waitForPageObjectToLoad() {
        logger.debug("Waiting for member legacy intervention to load");
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, sectionLegacyIntervention);
    }
    
    /**
     * Retrieves the number of records displayed in the label
     * @return number of records
     * @throws ElementNotFoundException
     */
    public int retrieveNumOfRecrods() throws ElementNotFoundException
    {
        String temp = SeleniumPageHelperAndWaiter.retrieveTextFromTextbox(this, labelRecords);
        temp = StringUtils.replace(temp, "Records Found", "").trim();
        
        try {
            return Integer.valueOf(temp);
        }
        catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Retrieve all the legacy intervention
     * @return list of {@link MemberLegacyIntervention}
     * @throws ElementNotFoundException if there are issues
     */
    public List<MemberLegacyIntervention> retrieveLegacyIntervention() throws ElementNotFoundException
    {
        logger.info("Retrieving legacy intervention");
        
        List<MemberLegacyIntervention> records = new ArrayList<>();
        
        int numRows = rowRecords.size();
        
        for ( int index = 0; index < numRows;  index++ )
        {
          records.add( retrieveLegacyIntervention(index) );
        }
        logger.info("****************reco ***********"+records);
        return records;
    }
    
    /**
     * Retrieves the legacy intervention base on the index of the row
     * @param index first row starts with index 0 
     * @return {@link MemberLegacyIntervention}
     * @throws ElementNotFoundException if there are issues
     */
    public MemberLegacyIntervention retrieveLegacyIntervention(int index) throws ElementNotFoundException
    {
        logger.info("***Retrieving legacy intervention data***");
       
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, By.xpath( String.format(rowRecordXpath, index+1) ));
        WebElement row = driver.findElement( By.xpath( String.format(rowRecordXpath, index+1) ) );
        expandLegacyIntervention(row.findElement(buttonExpand), false);
        MemberLegacyIntervention record = new MemberLegacyIntervention();
        
        String temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, labelInterventionDate);
        record.setRecommendationDate(temp);
        
        temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, labelDrugFocus);
        record.setTargetProductName(temp);
        
        temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, labelReply);
        record.setAssignedTo(temp);
        
        temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, labelInterventionName);
        record.setRuleName(temp);
        
        temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, labelOutcomeDocumentedBy);
        record.setLastUpdatedBy(temp);
        
        temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, labelInterventionMadeBy);
        record.setCreatedBy(temp);
        
        temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, labelConversionDate);
        record.setConversionDate(temp);
       
         
        
        expandLegacyIntervention(row.findElement(buttonExpand), true);
        temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, driver.findElement( By.xpath( String.format(rowRecordXpath, index+2) ) ));
 
        record.setOutcome(temp.substring(15).stripLeading());
        expandLegacyIntervention(row.findElement(buttonExpand), false);
        
        return record;
    }
    
    /**
     * Expand/collapse a record to display outcome notes
     * @param iconExpand WebElement that represent the icon for expanding/collapsing
     * @param expand true to expand it otherwise false
     * @throws ElementNotFoundException if there are issues
     */
    private void expandLegacyIntervention(WebElement iconExpand, boolean expand) throws ElementNotFoundException
    {
        logger.info("Expanding row");
        String attr = SeleniumPageHelperAndWaiter.retrieveAttribute(this, iconExpand, "class");
        
        if ( expand && StringUtils.endsWithIgnoreCase(attr, "-right") )
        {
            SeleniumPageHelperAndWaiter.clickWebElement(this, iconExpand);
            SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, labelOutcomeNotes);
        }
        else if ( !expand && StringUtils.endsWithIgnoreCase(attr, "-down") )
        {
            SeleniumPageHelperAndWaiter.clickWebElement(this, iconExpand);
            SeleniumPageHelperAndWaiter.pause(5000);
        }
    }
    
    /**
     * Retrieve all the columns
     * 
     * @return list of columns
     * @throws ElementNotFoundException
     */
    public List<String> retrieveColumns() throws ElementNotFoundException
    {
        List<String> columns = SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, columnNames);
        columns.remove(0);  // do not include the first entry which is blank
        return columns;
    }

}
