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

import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.dto.MemberInterventionNote;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.IPage;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 12/29/2022
 */
public class MemberCommandCenterPO extends AbstractCommonPage implements IPage{
    private static final Logger logger = LoggerFactory.getLogger(MemberCommandCenterPO.class);

    final String SECTION_PAGE_XPATH = "//rxc-command-center/div/rxc-intervention-notes";

    @FindBy(xpath = SECTION_PAGE_XPATH)
    private WebElement sectionInterventionStatusHistory;

    @FindBy(xpath = "//span[normalize-space()='Preview Fax']")
    private WebElement selectPreviewFax;

    @FindBy(xpath ="//span[normalize-space()='Send Fax']")
    private WebElement labelSendFax;

    @FindBy(xpath ="//rxc-member-details/p-tabmenu/div/ul/li[7]")
    private WebElement headerCommandCenter;

    // Intervention History Status DTO check 

    @FindBy(xpath = "//rxc-command-center/div/rxc-intervention-notes/p-table/div/div[2]/table/tbody/tr/td[1]")
    private List<WebElement> labelStatus;

    @FindBy(xpath = "//rxc-command-center/div/rxc-intervention-notes/p-table/div/div[2]/table/tbody/tr/td[2]")
    private List<WebElement> labelStatusCreatedDate;

    @FindBy(xpath = "//rxc-command-center/div/rxc-intervention-notes/p-table/div/div[2]/table/tbody/tr/td[4]")
    private List<WebElement> labelModifiedDate;

    @FindBy(xpath = "//rxc-command-center/div/rxc-intervention-notes/p-table/div/div[2]/table/tbody/tr/td[3]")
    private List<WebElement> labelStatusCreatedBy;

    @FindBy(xpath = "//rxc-command-center/div/rxc-intervention-notes/p-table/div/div[2]/table/tbody/tr/td[5]")
    private List<WebElement> labelUpdatedBy;

    @FindBy(xpath = "//rxc-command-center/div/rxc-intervention-notes/p-table/div/div[2]/table/tbody/tr/td[6]")
    private List<WebElement> labelNoteContent;

    @FindBy(xpath = "//rxc-command-center/div/rxc-intervention-notes/p-table/div/div[2]/table/tbody/tr")
    private List<WebElement> rowInterventionNoteRecord; 

    // Intervention Correspondence DTO check

    @FindBy(xpath = "//rxc-intervention-correspondence/p-table/div/div[2]/table/tbody/tr/td[2]")
    private List<WebElement> labelInterventionCorrespondenceType;

    @FindBy(xpath = "//rxc-intervention-correspondence/p-table/div/div[2]/table/tbody/tr/td[3]")
    private List<WebElement> labelInterventionCorrespondenceCreatedDate;
    
    @FindBy(xpath = "//rxc-intervention-correspondence/p-table/div/div[2]/table/tbody/tr/td[4]")
    private List<WebElement> labelInterventionCorrespondenceCreatedBy;

    @FindBy(xpath = "//rxc-intervention-correspondence/p-table/div/div[2]/table/tbody/tr/td[5]")
    private List<WebElement> labelInterventionCorrespondenceModifiedDate;
   
    @FindBy(xpath = "//rxc-intervention-correspondence/p-table/div/div[2]/table/tbody/tr/td[6]")
    private List<WebElement> labelInterventionCorrespondenceModifiedBy;

    @FindBy(xpath = "//rxc-intervention-correspondence/p-table/div/div[2]/table/tbody/tr/td[7]")
    private List<WebElement> labelInterventionCorrespondenceProviderName;

    @FindBy(xpath = "//rxc-intervention-correspondence/p-table/div/div[2]/table/tbody/tr/td[8]")
    private List<WebElement> labelInterventionCorrespondenceContactName;

    @FindBy(xpath = "//rxc-intervention-correspondence/p-table/div/div[2]/table/tbody/tr/td[9]")
    private List<WebElement> labelInterventionCorrespondenceContactTitle;

    @FindBy(xpath = "//rxc-intervention-correspondence/p-table/div/div[2]/table/tbody/tr/td[10]")
    private List<WebElement> labelInterventionCorrespondenceOutcome;

    @FindBy(xpath = "//rxc-command-center/rxc-intervention-correspondence/p-table/div/div[2]/table/tbody/tr")
    private List<WebElement> rowInterventionCorrespondenceRecord;
    
    // Command Center Xpaths
    
    @FindBy(xpath ="//rxc-command-center/div/rxc-intervention-title/div/div/div[3]/div/a")
    private By labelRuleLinkVisible;
    
    @FindBy(xpath ="//a[@data-testid='editRuleNameId']")
    private WebElement labelRuleLink;
    
    @FindBy(xpath ="//main/div/rxc-review/article/section[1]/dl/dd[2]")
    private WebElement labelRuleName;
    
    
    /**
     * Constructor
     *
     * @param driver WebDriver for PageObject
     * @param page   PageConfiguration for the UI page
     */
    public MemberCommandCenterPO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
        waitForPageObjectToLoad();
    }


    @Override
    public void waitForPageObjectToLoad() {
        logger.debug("Waiting for Intervention Status History to load");
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, sectionInterventionStatusHistory);

    }

    /**
     * Retrieve the data of the new added InterventionNote
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public List<MemberInterventionNote> retrieveInterventionNote() throws ElementNotFoundException
    {
        List<MemberInterventionNote> list = new ArrayList<>();
        logger.info("***Inside retrieveInterventionNote **** " + list);

        for ( WebElement row : rowInterventionNoteRecord )
        {
            MemberInterventionNote InterventionNote = new MemberInterventionNote();

            InterventionNote.setNewStatusDescription(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[1]")));
            // Perform UI Created validation
            String createdDateTime = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[2]"));
            if ( StringUtils.isNotBlank(createdDateTime) ){
                String dateYear = createdDateTime.substring(6, 10);
                String dateMonth = createdDateTime.substring(0, 2);
                String dateDay = createdDateTime.substring(3, 5);

                createdDateTime = dateMonth+"/"+dateDay+"/"+dateYear; 
            }
            InterventionNote.setCreatedDateTime(createdDateTime);

            InterventionNote.setCreatedBy(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[3]")));
            
            // Perform UI Last Updated date validation
            String lastUpdatedDateTime = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[4]"));
            if ( StringUtils.isNotBlank(lastUpdatedDateTime) ){
                String dateYear = lastUpdatedDateTime.substring(6, 10);
                String dateMonth = lastUpdatedDateTime.substring(0, 2);
                String dateDay = lastUpdatedDateTime.substring(3, 5);

                lastUpdatedDateTime = dateMonth+"/"+dateDay+"/"+dateYear; 
            }
            InterventionNote.setLastUpdatedDateTime(lastUpdatedDateTime);

            
            InterventionNote.setLastUpdatedBy(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[5]")));
            InterventionNote.setNote(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[6]")));

            list.add(InterventionNote);
        }

        return list;
    }

    /**
     * Determine if the Previe Fax WebElement is displayed in the UI
     * 
     * @throws ElementNotFoundException if issue occurs
     */

    public void previewFax() {
        try {
            boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, selectPreviewFax, 10);
            if (displayed)
                SeleniumPageHelperAndWaiter.clickWebElement(this, selectPreviewFax);

        } catch (Exception e) {
            logger.error("Preview fax not found");

        }
    }
    /**
     * Determine if the Send Fax WebElement is displayed in the UI
     * 
     * @throws ElementNotFoundException if issue occurs
     */
    public void sendFax() {
        try {
            boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, labelSendFax, 10);
            if (displayed)
                SeleniumPageHelperAndWaiter.clickWebElement(this, labelSendFax);
        } catch (Exception e) {
            logger.error("Send fax not found");
        }
    }

    /**
     * Retrieve the data of the Intervention Correspondence
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public List<MemberCorrespondence> retrieveInterventionCorrespondenceInfo() throws ElementNotFoundException
    {
        List<MemberCorrespondence> list = new ArrayList<>();
        logger.info("***Inside retrieveInterventionCorrespondenceInfo **** " + list);

        for ( WebElement row : rowInterventionCorrespondenceRecord )
        {
            MemberCorrespondence memberCorrespondence = new MemberCorrespondence();

            memberCorrespondence.setCorrespondenceType( SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[2]")));

            // Perform UI Created validation
            String createdDateTime = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[3]"));
            logger.info("******** createdDateTime ******** " + createdDateTime);
            if ( StringUtils.isNotBlank(createdDateTime) ){
                String dateYear = createdDateTime.substring(6, 10);
                String dateMonth = createdDateTime.substring(0, 2);
                String dateDay = createdDateTime.substring(3, 5);

                createdDateTime = dateMonth+"/"+dateDay+"/"+dateYear; 
            }
            memberCorrespondence.setCreatedDateTime(createdDateTime);
            
            memberCorrespondence.setCreatedBy(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[4]")));

            // Perform UI Last Updated date validation
            String lastUpdatedDateTime = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[5]"));
           logger.info("******** lastUpdatedDateTime ********"+ lastUpdatedDateTime);
            if ( StringUtils.isNotBlank(lastUpdatedDateTime) ){
                String dateYear = lastUpdatedDateTime.substring(6, 10);
                String dateMonth = lastUpdatedDateTime.substring(0, 2);
                String dateDay = lastUpdatedDateTime.substring(3, 5);

                lastUpdatedDateTime = dateMonth+"/"+dateDay+"/"+dateYear; 
            }
            memberCorrespondence.setLastUpdatedDateTime(lastUpdatedDateTime);

            memberCorrespondence.setLastUpdatedBy(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[6]")));
            memberCorrespondence.setProviderName(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[7]")));
            memberCorrespondence.setCorrespondenceOutcome(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[10]")));

            list.add(memberCorrespondence);
        }
        return list;
    }
    /**
     * Select Command Center
     * 
     * @throws ElementNotFoundException
     */
    public void selectCommandCenter() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, headerCommandCenter);
    }
    /**
     * Check if Rule Link Name visible
     * @return 
     * @return 
     * 
     * @throws ElementNotFoundException
     */
    public boolean isClickableRuleLinkName() throws ElementNotFoundException {
       boolean visible=SeleniumPageHelperAndWaiter.isWebElementVisible(driver, labelRuleLink, 2);
        return visible;
    }
    /**
     * Select Rule Name Link
     * 
     * @throws ElementNotFoundException
     */
    public void selectRuleLink() throws ElementNotFoundException {
    	SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, labelRuleLink);
        SeleniumPageHelperAndWaiter.clickWebElement(this, labelRuleLink);
    }
    /**
     * Get Rule Name Text from Command center
     * @return 
     * 
     * @throws ElementNotFoundException
     */
    public String getRuleLinkText() throws ElementNotFoundException {
    	String ruleText=SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelRuleLink);
    	return ruleText;
    }
    /**
     * Get Rule Name Text from Intervention Wizard
     * @return 
     * 
     * @throws ElementNotFoundException
     */
    public String getRuleNameText() throws ElementNotFoundException {
    	String ruleText=SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelRuleName);
    	return ruleText;
    }

}
