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
import com.excellus.sqa.rxcc.dto.member.CorrespondenceColumns;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.IPage;
import com.excellus.sqa.selenium.utilities.SortOrder;

/**
 * Page object that handles member demographics
 * 
 * @author  Neeru Tagore
 * @since 04/20/2022
 */
public class MemberCorrespondencePO extends AbstractCommonPage implements IPage {

    private static final Logger logger = LoggerFactory.getLogger(MemberCorrespondencePO.class);

   // @FindBy(xpath = "//span[normalize-space()='Correspondence']")// //rxc-member-details/p-tabmenu/div/ul/li/a/span[normalize-space()='Correspondence']
    
    @FindBy(xpath = "//html/body/rxc-app/rxc-members/rxc-tenant-content/main/section[2]/rxc-member-details/p-tabmenu/div/ul/li[5]/a/span[1]")
    private WebElement headerCorrespondence;

    @FindBy(xpath = "//rxc-member-correspondence-table//input[@placeholder='Search keyword']")
    private WebElement textboxCorrespondenceSearch;

    private final String textboxClickCorrespondenceSearch ="//rxc-member-correspondence-table//input[@placeholder='Search keyword']";

    @FindBy(xpath = "//p-sorticon[@field='typeCorrespondence']")
    private WebElement iconTypeCorrespondenceSort;

    @FindBy(xpath = "//p-sorticon[@field='contactName']")
    private WebElement iconContactNameSort;

    @FindBy(xpath = "//p-sorticon[@field='contactTitle']")
    private WebElement iconContactTitleSort;

    @FindBy(xpath = "//p-sorticon[@field='createdBy']")
    private WebElement iconCreatedBySort;

    @FindBy(xpath = "//p-sorticon[@field='created']")
    private WebElement iconCreatedDateSort;

    @FindBy(xpath = "//p-sorticon[@field='lastUpdated']")
    private WebElement iconEditedDateSort;

    @FindBy(xpath = "//p-sorticon[@field='interventionId']")
    private WebElement iconInterventionIdSort;

    @FindBy(xpath = "//p-sorticon[@field='outcome']")
    private WebElement iconOutcomeSort;

    @FindBy(xpath = "//p-sorticon[@field='targetDrug']")
    private WebElement iconTargetDrugSort;

    @FindBy(xpath = "//p-sorticon[@field='providerName']")
    private WebElement iconProviderNameSort; 

    @FindBy(xpath = "//rxc-member-correspondence-table//div[1]//button[2]//span")
    private WebElement buttonCollapseAll;

    @FindBy(xpath = "//rxc-member-correspondence-table//div[1]//button[3]//span")
    private WebElement buttonExpandAll;

    @FindBy(xpath = "//rxc-member-correspondence-table/p-table/div/div[2]/table/tbody/tr[1]/td[2]")
    private WebElement memberCorrespondenceSearchValue;

    // DTO check

    @FindBy(xpath = "//rxc-member-correspondence-table/p-table/div/div[2]/table/tbody/tr/td[2]")
    private List<WebElement> labelTypeCorrespondence;

    @FindBy(xpath = "//rxc-member-correspondence-table/p-table/div/div[2]/table/tbody/tr/td[3]")
    private List<WebElement> labelContactName;

    @FindBy(xpath = "//rxc-member-correspondence-table/p-table/div/div[2]/table/tbody/tr/td[4]")
    private List<WebElement> labelContactTitle;

    @FindBy(xpath = "//rxc-member-correspondence-table/p-table/div/div[2]/table/tbody/tr/td[5]")
    private List<WebElement> labelCreatedBy;

    @FindBy(xpath = "//rxc-member-correspondence-table/p-table/div/div[2]/table/tbody/tr/td[6]")
    private List<WebElement> labelCreatedDate;

    @FindBy(xpath = "//rxc-member-correspondence-table/p-table/div/div[2]/table/tbody/tr/td[7]")
    private List<WebElement> labelEditedDate;

    @FindBy(xpath = "//rxc-member-correspondence-table/p-table/div/div[2]/table/tbody/tr/td[8]")
    private List<WebElement> labelOutcome;

    @FindBy(xpath = "//rxc-member-correspondence-table/p-table/div/div[2]/table/tbody/tr/td[9]")
    private List<WebElement> labelTargetDrug;

    @FindBy(xpath = "//rxc-member-correspondence-table/p-table/div/div[2]/table/tbody/tr/td[10]")
    private List<WebElement> labelRecipientName;

    @FindBy(xpath = "//rxc-member-correspondence-table/p-table/div/div[2]/table/tbody/tr/td/i")
    private WebElement buttonExpandCollapseMemberCorrespondence;

    @FindBy(xpath = "//rxc-member-correspondence-table/p-table/div/div[2]/table/tbody/tr")
    private List<WebElement> rowMemberCorrespondenceRecord; 

    private final String retrieveCorrespondenceRecord ="//rxc-member-correspondence-table[1]/p-table[1]/div[1]/div[1]/div[1]/p";

    @FindBy(xpath = "//rxc-member-correspondence-table[1]/p-table[1]/div[1]/div[1]/div[1]/div[1]/button[1]/span[contains(.,'Clear')]")
    private WebElement buttonClear;



    /**
     * Constructor
     *
     * @param driver WebDriver for PageObject
     * @param page   PageConfiguration for the UI page
     */


    public MemberCorrespondencePO(WebDriver driver, PageConfiguration page) {
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
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, headerCorrespondence);
        SeleniumPageHelperAndWaiter.clickWebElement(this, headerCorrespondence);
        
    }
    /**
     * Retrieve the text of the member Correspondence
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public List<MemberCorrespondence> retrieveMemberCorrespondenceInfo() throws ElementNotFoundException
    {
        List<MemberCorrespondence> list = new ArrayList<>();
        logger.info("***Inside retrieveMemberCorrespondenceInfo **** " + list);

        for ( WebElement row : rowMemberCorrespondenceRecord )
        {
            MemberCorrespondence memberCorrespondence = new MemberCorrespondence();

            memberCorrespondence.setCorrespondenceType( SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[2]")));
            memberCorrespondence.setContactName( SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[3]")));
            memberCorrespondence.setContactTitle(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[4]")));
            memberCorrespondence.setCreatedBy(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[5]")));
            memberCorrespondence.setCreatedDateTime(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[6]")));
            memberCorrespondence.setLastUpdatedDateTime(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[7]")));
            memberCorrespondence.setCorrespondenceOutcome(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[8]")));
            memberCorrespondence.setTargetDrug(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[9]")));
            memberCorrespondence.setProviderName(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[10]")));
            list.add(memberCorrespondence);
        }

        return list;
    }
    /**
     * @ntagore 01/19/2023
     * Retrieve the Outbound fax of the member Correspondence after Fax Successful status
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public List<MemberCorrespondence> retrieveCorrespondenceTypeInfo() throws ElementNotFoundException
    {
        List<MemberCorrespondence> list = new ArrayList<>();
        logger.info("***Inside retrieveMemberCorrespondenceInfo **** " + list);

        for ( WebElement row : rowMemberCorrespondenceRecord )
        {
            MemberCorrespondence memberCorrespondence = new MemberCorrespondence();

            memberCorrespondence.setCorrespondenceType( SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[2]")));
            memberCorrespondence.setCreatedBy(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[5]")));
            memberCorrespondence.setCreatedDateTime(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[6]")));
            memberCorrespondence.setLastUpdatedDateTime(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[7]")));
            memberCorrespondence.setCorrespondenceOutcome(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[8]")));
            memberCorrespondence.setTargetDrug(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[9]")));
            memberCorrespondence.setProviderName(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[10]")));
            list.add(memberCorrespondence);
        }

        return list;
    }

    /**
     * This method will sort a column in certain order, i.e clicks the icon sort to make it asc or desc
     * 
     * @param column
     * @param order
     * @throws ElementNotFoundException
     */
    public void sortColumn(CorrespondenceColumns column, SortOrder order) throws ElementNotFoundException
    {
        switch (column) {

        case TYPE:
            sortColumn(iconTypeCorrespondenceSort, order);
            break;

        case CONTACT_NAME:
            sortColumn(iconContactNameSort, order);
            break;

        case CONTACT_TITLE:
            sortColumn(iconContactTitleSort, order);
            break;   

        case CREATED_DATE:
            sortColumn(iconCreatedDateSort, order);
            break; 

        case CREATED_BY:
            sortColumn(iconCreatedBySort, order);
            break;

        case EDITED_DATE:
            sortColumn(iconEditedDateSort, order);
            break;

        case OUTCOME:
            sortColumn(iconOutcomeSort, order);
            break;

        case TARGET_DRUG:
            sortColumn(iconTargetDrugSort, order);
            break;

        case RECIPIENT_NAME:
            sortColumn(iconProviderNameSort, order);
            break;    

        default:
            break;
        }
    }

    private void sortColumn(WebElement element, SortOrder order) throws ElementNotFoundException
    {
        String sortValue = SeleniumPageHelperAndWaiter.retrieveAttribute(this, element, "aria-sort");

        if ( sortValue==null ) {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, element);
        }

        if ( order == SortOrder.ASCENDING )
        {
            if ( !StringUtils.equalsIgnoreCase(SortOrder.DESCENDING.name(), sortValue) )
            {
                SeleniumPageHelperAndWaiter.clickWebElement(this, element);
            }
        }  
        else if ( order == SortOrder.DESCENDING )
        {
            if (   !StringUtils.equalsIgnoreCase(SortOrder.ASCENDING.name(), sortValue) )
            {
                SeleniumPageHelperAndWaiter.clickWebElement(this, element);
            }
            SeleniumPageHelperAndWaiter.pause(500);
        }
    }

    /**
     * Retrieve all the data in a given column
     * 
     * @param column
     * @return
     */
    public List<String> retrieveColumnData(CorrespondenceColumns column)
    {
        switch (column) {
        case TYPE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelTypeCorrespondence);

        case CONTACT_NAME:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelContactName);

        case CONTACT_TITLE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelContactTitle);    

        case CREATED_DATE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelCreatedDate); 


        case CREATED_BY:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelCreatedBy); 

        case EDITED_DATE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelEditedDate);

        case OUTCOME:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelOutcome);

        case TARGET_DRUG:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelTargetDrug); 

        case RECIPIENT_NAME:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelRecipientName); 

        default:
            break;
        }

        return null;
    }
    /**
     * Retrieve the number of the Member Correspondence records
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public String numberOfCorrespondenceRecords() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.pause(2000);
        int resultSize = SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, rowMemberCorrespondenceRecord).size();
        String numberOfRecords = resultSize + " Records Found";
        return numberOfRecords;
    }

    /**
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public String retrieveCorrespondenceLabelRecord() throws ElementNotFoundException {

        return (driver.findElement(By.xpath(retrieveCorrespondenceRecord)).getText());
    }
    /**
     * method used for correspondence search box
     * Search value can be Type value returned from the first row
     * @throws ElementNotFoundException if issue occurs
     */

    public void correspondenceSearch(String searchValue) throws ElementNotFoundException {
        try {
            logger.info("***Pass the search value**** " + searchValue);
            boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(this, By.xpath(textboxClickCorrespondenceSearch));
            logger.info("***is displayed **** " + displayed);
            if ( displayed )
                SeleniumPageHelperAndWaiter.clickBy(this, By.xpath(textboxClickCorrespondenceSearch));
            SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textboxCorrespondenceSearch, searchValue);
        }
        catch (Exception e)
        {
            logger.error("Correspondence Search element not found");

        }

    }

    /**
     * Expand or collapse the member Correspondence
     * 
     * @param expand the member Correspondence if true. Otherwise collapse it if it is false
     * @throws ElementNotFoundException if there is an issue
     */
    public void buttonExpandCollapseMemberCorrespondence(boolean expand) throws ElementNotFoundException {
        final String COLLAPSED = "-right";
        final String EXPANDED = "-down";

        String classAttr = SeleniumPageHelperAndWaiter.retrieveAttribute(this, buttonExpandCollapseMemberCorrespondence, "class");
        if (expand && StringUtils.endsWithIgnoreCase(classAttr, COLLAPSED)) {
            SeleniumPageHelperAndWaiter.pause(5000);
            SeleniumPageHelperAndWaiter.clickWebElement(this, buttonExpandAll);
            SeleniumPageHelperAndWaiter.pause(5000);
        } else if (!expand && StringUtils.endsWithIgnoreCase(classAttr, EXPANDED)) {
            SeleniumPageHelperAndWaiter.clickWebElement(this, buttonCollapseAll);
            SeleniumPageHelperAndWaiter.pause(5000);
        }
    }

    /**
     * Check if the Member Correspondence section is expanded or collapsed
     * 
     * @return true if expanded otherwise return false
     * @throws ElementNotFoundException
     */

    public boolean isbuttonExpandCollapseMemberCorrespondence() throws ElementNotFoundException {
        final String EXPANDED = "-down";

        String classAttr = SeleniumPageHelperAndWaiter.retrieveAttribute(this,buttonExpandCollapseMemberCorrespondence, "class");

        if (StringUtils.endsWithIgnoreCase(classAttr, EXPANDED)) {
            return true;
        }

        return false;
    }

}    	