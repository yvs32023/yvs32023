/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.formulary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.Group;
import com.excellus.sqa.rxcc.dto.formulary.FormularyGroupsColumns;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.IPage;
import com.excellus.sqa.selenium.utilities.SortOrder;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 09/23/2022
 */
public class FormularyGroupsPO extends AbstractCommonPage implements IPage {
    
    private static final Logger logger = LoggerFactory.getLogger(FormularyGroupsPO.class);

    @FindBy(xpath = "//p-menubar//a[text()='Rx Clinical Concierge']")
    private WebElement menuHomePage;

    @FindBy(xpath = "//div[@class='flex items-center gap-2 ng-star-inserted']/*[9]//*[name()='svg']")
    private WebElement iconFormularyGroups;
    
    final String SECTION_PAGE_XPATH = "//rxc-adtenant-groups";
    
    @FindBy(xpath = SECTION_PAGE_XPATH)
    private WebElement sectionFormularyGroups;

    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[1]/div/div/span/input[@placeholder='Search keyword']")
    private WebElement textboxFormularyGroupsSearch;
    
    @FindBy(xpath = SECTION_PAGE_XPATH + "//table/tbody/tr") 
    private List<WebElement> rowsFormularyGroups;
    
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/thead/tr/th")
    private List<WebElement> columnHeaders; 

    // DTO check

    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/tbody/tr/td[count(//tr/th[contains(./span,'Tenant Id')]/preceding-sibling::th)+1]")
    private List<WebElement> labelTenantId;

    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/tbody/tr/td[count(//tr/th[contains(./span,'Tenant Name')]/preceding-sibling::th)+1]")
    private List<WebElement> labelTenantName;

    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/tbody/tr/td[count(//tr/th[contains(./span,'Group ID')]/preceding-sibling::th)+1]")
    private List<WebElement> labelGroupId;

    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/tbody/tr/td[count(//tr/th[contains(./span,'RxCC Group Name')]/preceding-sibling::th)+1]")
    private List<WebElement> labelRxccGroupName;

    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/tbody/tr/td[count(//tr/th[contains(./span,'Employee Group Indicator')]/preceding-sibling::th)+1]")
    private List<WebElement> labelEmployeeGroupIndicator;

    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/tbody/tr/td[count(//tr/th[contains(./span,'Effective Date')]/preceding-sibling::th)+1]")
    private List<WebElement> labelEffectiveDate ;
    
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/tbody/tr/td[count(//tr/th[contains(./span,'Termination Date')]/preceding-sibling::th)+1]")
    private List<WebElement> labelTerminationDate;
    
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/tbody/tr/td[count(//tr/th[contains(./span,'Formulary Code')]/preceding-sibling::th)+1]")
    private List<WebElement> labelFormularyCode;
    
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/tbody/tr/td[count(//tr/th[contains(./span,'Created By')]/preceding-sibling::th)+1]")
    private List<WebElement> labelCreatedUser;
    
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/tbody/tr/td[count(//tr/th[contains(./span,'Create Date')]/preceding-sibling::th)+1]")
    private List<WebElement> labelCreateDate;
    
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/tbody/tr/td[count(//tr/th[contains(./span,'Last Modified user')]/preceding-sibling::th)+1]")
    private List<WebElement> labelLastModifieduser;
    
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/tbody/tr/td[count(//tr/th[contains(./span,'Last Updated')]/preceding-sibling::th)+1]")
    private List<WebElement> labelLastUpdated;

    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/tbody/tr")  
    private List<WebElement> rowsFormularyGroupsRecord;
    
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[1]/div/p")
    private WebElement labelRecordFound;
   
    //Sort Xpath's
    
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Tenant ID')]/p-sorticon/i")
    private WebElement iconTenantIdSort;
    
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Tenant Name')]/p-sorticon/i")
    private WebElement iconTenantNameSort;

    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Group ID')]/p-sorticon/i")
    private WebElement iconGroupIdSort;

    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'RxCC Group Name')]/p-sorticon/i")
    private WebElement iconRxccGroupNameSort;
    
    //new added
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Employee Group Indicator')]/p-sorticon/i")
    private WebElement iconEmployeeGroupIndicatorSort;

    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Effective Date ')]/p-sorticon/i")
    private WebElement iconEffectiveDateSort;

    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Termination Date')]/p-sorticon/i")
    private WebElement iconTerminationDateSort;

    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Formulary Code')]/p-sorticon/i")
    private WebElement iconFormularyCodeSort;

    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Created By')]/p-sorticon/i")
    private WebElement iconCreatedUserSort;
    
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Create Date ')]/p-sorticon/i")
    private WebElement iconCreateDateSort;
    
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Last Modified user')]/p-sorticon/i")
    private WebElement iconLastModifiedUserSort;
    
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Last Updated ')]/p-sorticon/i")
    private WebElement iconLastUpdatedSort;
    
    //Filter Xpath's
    
    @FindBy(xpath = "//th/span[contains(text(),'Group ID')]/following-sibling::p-columnfilter/div/button")
    private WebElement iconGroupIdFilter;
    
    @FindBy(xpath = "//p-columnfilterformelement/*/div[.='Select Group Id']")
    private WebElement dropdownFilterGroupId;
    
    @FindBy(xpath = "//p-columnfilterformelement/p-multiselect/*/div/*/div/input[@role='textbox']")
    private WebElement textboxGroupIdFilterDropDown; 
    
    @FindBy(xpath = "//p-multiselect[1]/div[1]/div[4]/div[1]/button[1]")
    private WebElement closeFilterSelection; 
    
    @FindBy(xpath = "//p-columnfilterformelement/p-multiselect/div/div[4]/div[1]/div[1]/div[@role='checkbox']")
    private WebElement selectFilterSelection;
      
    @FindBy(xpath = "//html/body/div[2]/div[2]/button/span[contains(.,'Apply')]")
    private WebElement buttonApply;
    
    private final String groupFilterSearchResultXpath = "//p-columnfilterformelement/p-multiselect/div/div[4]/div[2]/ul/p-multiselectitem";

    @FindBy(xpath = groupFilterSearchResultXpath)
    private List<WebElement> rowGroupSearchResults;

    @FindBy(xpath = "//p-columnfilterformelement/p-multiselect/*/div/*/button[@type='button']")
    private WebElement buttonFilterDropDownExit; 
    
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[1]/div/p")
    private WebElement labelFilterRecordCount; 
    
    private final String groupFilterSearchCountXpath = "//rxc-adtenant-groups/p-table/div/div[2]/table/tbody/tr";
    
    @FindBy(xpath = groupFilterSearchCountXpath)
    private List<WebElement> rowGroupSearchCount;
       
    @FindBy(xpath = "//rxc-adtenant-groups/p-table/div/div[1]/div/button/span[contains(.,'Clear')]")
    private WebElement buttonClear;

    
    /**
     * Constructor
     *
     * @param driver WebDriver for PageObject
     * @param page   PageConfiguration for the UI page
     */
    public FormularyGroupsPO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
    }

    @Override
    public void waitForPageObjectToLoad() {
        logger.debug("Waiting for formulary group data to load");
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, sectionFormularyGroups);

    }


    /**
     * Retrieve the data of the Formulary groups
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */

        public List<Group> retrieveFormularyGroupsInfo() throws ElementNotFoundException
        {
            List<Group> list = new ArrayList<>();
            logger.info("***Inside retrieveFormularyGroupInfo **** " + list);
            
            for ( WebElement row : rowsFormularyGroupsRecord )
            {
                Group formularyGroup = new Group();
             
                formularyGroup.setTenantId(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[1]")));
                formularyGroup.setGroupId(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[3]")));
                formularyGroup.setRxccGroupName(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[4]")));
                formularyGroup.setEffectiveDate(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[6]")));
                formularyGroup.setTermDate(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[7]")));
                formularyGroup.setFormularyId(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[8]")));
                formularyGroup.setCreatedBy(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[9]")));
                formularyGroup.setCreatedDateTime(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[10]")));
                formularyGroup.setLastUpdatedBy(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[11]")));
                formularyGroup.setLastUpdatedDateTime(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[12]")));                            
                
                // Employee Group Indicator new added
                String isMemberCommunicationIndicator = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[5]"));
                logger.info("****Employee Group Indicator **** " + isMemberCommunicationIndicator);
                if (isMemberCommunicationIndicator.equalsIgnoreCase("Y")){
                    formularyGroup.setMemberCommunicationInd(true);
                }else {
                    formularyGroup.setMemberCommunicationInd(false);  
                }

                formularyGroup.setTenantName(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[2]")));
                 
                list.add(formularyGroup);
            }
    
            return list;
        }

    /**
     * Click the Formulary Groups icon
     * 
     * @throws ElementNotFoundException
     */
    public void clickFormularyGroupsIcon() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, iconFormularyGroups);
        SeleniumPageHelperAndWaiter.pause(5000);
    }
    
    /**
     * Retrieve all the columns of formulary groups
     * @return list of columns
     */
    public List<String> retrieveColumnHeaders()
    {
        List<String> columnNames = SeleniumPageHelperAndWaiter.retrieveWebElementsText(columnHeaders);      
        logger.info("***ColumnNames**** " + columnNames);
       return columnNames;
    }

    /**
     * method used for search formulary box
     * Search value can be Type value returned from the first row
     * @throws ElementNotFoundException if issue occurs
     */

    public void searchKeyword(String searchValue) throws ElementNotFoundException {
        logger.info("***Pass the Formulary search value**** " + searchValue);
        SeleniumPageHelperAndWaiter.clickWebElement(this, textboxFormularyGroupsSearch);
        logger.info("***Pass the Formulary search value  2**** ");
        SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxFormularyGroupsSearch, searchValue);   
        SeleniumPageHelperAndWaiter.pause(5000);
    }
     
    /**
     * This method will sort a column in certain order, i.e clicks the icon sort to make it asc or desc
     * 
     * @param column
     * @param order
     * @throws ElementNotFoundException
     */
    public void sortColumn(FormularyGroupsColumns column, SortOrder order) throws ElementNotFoundException
    {
        switch (column) {

        case TENANT_ID:
            sortColumn(iconTenantIdSort, order);
            break;
            
        case TENANT_NAME:
            sortColumn(iconTenantNameSort, order);
            break;

        case GROUP_ID:
            sortColumn(iconGroupIdSort, order);
            break;

        case RXCC_GROUP_NAME:
            sortColumn(iconRxccGroupNameSort, order);
            break;
         //new added   
        case EMPLOYEE_GROUP_INDICATOR:
            sortColumn(iconEmployeeGroupIndicatorSort, order);
            break;
            
        case EFFECTIVE_DATE:
            sortColumn(iconEffectiveDateSort, order);
            break;   

        case TERMINATION_DATE:
            sortColumn(iconTerminationDateSort, order);
            break; 

        case FORMULARY_CODE:
            sortColumn(iconFormularyCodeSort, order);
            break; 
            
        case CREATED_USER:
            sortColumn(iconCreatedUserSort, order);
            break;
            
        case CREATE_DATE:
            sortColumn(iconCreateDateSort, order);
            break;
            
        case LAST_MODIFIED_USER:
            sortColumn(iconLastModifiedUserSort, order);
            break;

        case LAST_UPDATED:
            sortColumn(iconLastUpdatedSort, order);
            break;
            
        default:
            break;
        }
        SeleniumPageHelperAndWaiter.pause(500);   // to avoid stale element exception 
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
     * @throws ElementNotFoundException 
     */
    public List<String> retrieveColumnData(FormularyGroupsColumns column)
    {
      switch (column) {  
     
        
        case TENANT_ID:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelTenantId);
            
        case TENANT_NAME:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelTenantName);

        case GROUP_ID:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelGroupId);

        case RXCC_GROUP_NAME:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelRxccGroupName);
         
            //new added
        case EMPLOYEE_GROUP_INDICATOR:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelEmployeeGroupIndicator);
        
        case EFFECTIVE_DATE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelEffectiveDate); 
            
        case TERMINATION_DATE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelTerminationDate); 
            
        case FORMULARY_CODE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelFormularyCode); 

        case CREATED_USER:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelCreatedUser);
            
        case CREATE_DATE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelCreateDate);
            
        case LAST_MODIFIED_USER:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelLastModifieduser);
            
        case LAST_UPDATED:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelLastUpdated);

        default:
            break;
        }
        SeleniumPageHelperAndWaiter.pause(500);   // to avoid stale element exception 
        return null;
    }
    
    /**
     * Retrieve EMPLOYEE_GROUP_INDICATOR 
     * @param column
     * @return
     * @throws ElementNotFoundException 
     */
    public List<Boolean> retrieveEmpGrpIndData(FormularyGroupsColumns column)
    {
      switch (column) {  

        case EMPLOYEE_GROUP_INDICATOR:

            List<String> temp = SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelEmployeeGroupIndicator);
            List<Boolean> memberCommInd = new ArrayList<Boolean>();
            for (String s : temp) {
                memberCommInd.add(s.equals("Y") ? true : false);
            }
            return memberCommInd;
 
        default:
            break;
        }
        SeleniumPageHelperAndWaiter.pause(500);   // to avoid stale element exception 
        return null;
    }
    
    /**
     * Filter for the Formulary Groups
     * Select the filter by passing the filter name
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterFormularyGroups(String filterName) throws ElementNotFoundException {
        if (filterName == "Group Id") {
           SeleniumPageHelperAndWaiter.clickWebElement(driver, iconGroupIdFilter);
        }
        SeleniumPageHelperAndWaiter.pause(1000);
    }
    /**
     * Filter for the Formulary Group
     * Select the filter dropdown
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterDropDownFormularyGroup(String filterName) throws ElementNotFoundException {
        if (filterName == "Group Id") {
           SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownFilterGroupId);
        }
       
        SeleniumPageHelperAndWaiter.pause(1000);
    }

    /**
     * Filter for the Formulary Group
     * Select the filter dropdown Text Box
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterDropDownSelectTextBoxFormularyGroup(String filterName) throws ElementNotFoundException {
        if (filterName == "Group Id") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, textboxGroupIdFilterDropDown);          
            SeleniumPageHelperAndWaiter.pause(1000);
        }
    } 
    
    /**
     * Filter for the Formulary Group
     * Set the field filter textbox WebElement
     *
     * @param filter value to set the textbox
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterDropDownInputTextBoxFormularyGroup(String formularyGroupFilterSearchTerm) throws ElementNotFoundException {
            SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textboxGroupIdFilterDropDown, formularyGroupFilterSearchTerm, 2);      
            SeleniumPageHelperAndWaiter.pause(1000);
     
    }
    
    /**
     * Filter for the Formulary Group
     * 
     * Select the first row from th search result
     * @throws ElementNotFoundException is something goes wrong
     */
    public String clickFirsFormularyGroupFilterResult(String filterValue) throws ElementNotFoundException {

        List<String> listDropDownValues= SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, rowGroupSearchResults);
        int listSize = listDropDownValues.size();
        logger.info("listDropDownValues size :" +listSize);
        logger.info("filterValue  :" +filterValue);

        if (listSize > 0) {
       
            for ( String listValue : listDropDownValues ) {
                
                if (listValue.equalsIgnoreCase(filterValue)) {
                    SeleniumPageHelperAndWaiter.clickWebElement(driver, rowGroupSearchResults.get(0));
                    break;
                }
            }
        }
        int rowCount=SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, rowGroupSearchResults).size();
        String filterRowCount = (rowCount+1) + " Records Found"; 
        SeleniumPageHelperAndWaiter.pause(1000);
        return filterRowCount;
    }
    /**
     * Filter for the Formulary Group
     * Record count WebElement
     *
     * @return no. of records returned
     * @throws ElementNotFoundException if issue occurs
     */
    
    public String filterDropDownRecordCountFormularyGroup() throws ElementNotFoundException {
         return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, this.labelFilterRecordCount); 
        }
    
    /**
     * Filter for the Formulary Group
     * Clear the filter search WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    
    public void filterClearFormularyGroup() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonClear);  
        }
    
    /**
     * Filter for the Formulary Group
     * Select checkbox to for the filter Selection WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    
    public void selectFilterSelectionFormularyGroup() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(driver, selectFilterSelection);
        SeleniumPageHelperAndWaiter.pause(3000);
        }
    
    /**
     * Filter for the Formulary Group
     * close the filter Selection WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    
    public void closeFilterSelectionFormularyGroup() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(driver, closeFilterSelection);
        SeleniumPageHelperAndWaiter.pause(3000);
        }
    
    /**
     * Filter for the Formulary Group
     * Apply the filter search WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    
    public void filterApplyFormularyGroup() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonApply);
        SeleniumPageHelperAndWaiter.pause(3000);
        }
    
}