/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
/**
 * 
 */
package com.excellus.sqa.rxcc.pages.interventionrule;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.InterventionRule;
import com.excellus.sqa.rxcc.dto.InterventionRule.RuleStatus;
import com.excellus.sqa.rxcc.dto.interventionrules.InterventionRulesColumns;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.utilities.SortOrder;

/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 08/22/2022
 */
/**
 * @author hzia
 *
 */
public class InterventionRuleLibraryPO extends AbstractCommonPage {

    private static final Logger logger = LoggerFactory.getLogger(InterventionRuleLibraryPO.class);

    @FindBy(xpath = "//p-menubar//a[text()='Rx Clinical Concierge']")
    private WebElement menuHomePage;
    @FindBy(xpath = "/html/body/rxc-app/ng-component/rxc-header/header/p-menubar/div/div[1]/div/svg-icon[4]")
   // @FindBy(xpath = "//rxc-header/header/p-menubar/div/p-menubarsub/ul[@role='menubar']/li/a[@href='/Intervention-Rule-Library']")
    private WebElement iconInterventionRuleLib;

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table/div/div[2]/table/thead/tr//span[(text())]")
    private List<WebElement> columnHeaders;   
    
    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table/div/div[1]/rxc-table-header/div[3]/span/input[@placeholder='Search Rules']")
    private WebElement textboxRuleLibrarySearch;

    @FindBy(xpath = "//tbody")
    private List<WebElement> tableInterventionRuleLib;

    @FindBy(xpath = "//tbody//tr//td[2]")
    private List<WebElement> columnResults;

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table/div/div[2]/table/tbody/tr")
    private List<WebElement> rowRuleLibraryRecord; 
    
    // DTO check

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Rule Name')]/preceding-sibling::th)+1]")
    private List<WebElement> labelRuleName;

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Create Date')]/preceding-sibling::th)+1]")
    private List<WebElement> labelCreateDate;

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Modify Date')]/preceding-sibling::th)+1]")
    private List<WebElement> labelModifyDate;

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Status')]/preceding-sibling::th)+1]")
    private List<WebElement> labelStatus;

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Formularies Assigned')]/preceding-sibling::th)+1]")
    private List<WebElement> labelFormulariesAssigned;

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Tenants Assigned')]/preceding-sibling::th)+1]")
    private List<WebElement> labelTenantsAssigned;

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Run Daily')]/preceding-sibling::th)+1]")
    private List<WebElement> labelRunDaily;


    // SortXpaths 

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table//table/thead/tr/th/span[contains(text(), 'Rule Name')]/p-sorticon/i")
    private WebElement iconRuleNameSort;

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table//table/thead/tr/th/span[contains(text(), 'Create Date')]/p-sorticon/i")
    private WebElement iconCreateDateSort;

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table//table/thead/tr/th/span[contains(text(), 'Modify Date')]/p-sorticon/i")
    private WebElement iconModifyDateSort;

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table//table/thead/tr/th/span[contains(text(), 'Status')]/p-sorticon/i")
    private WebElement iconStatusSort;

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table//table/thead/tr/th/span[contains(text(), 'Formularies Assigned')]/p-sorticon/i")
    private WebElement iconFormulariesAssignedSort;

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table//table/thead/tr/th/span[contains(text(), 'Tenants Assigned')]/p-sorticon/i")
    private WebElement iconTenantsAssignedSort;

    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table//table/thead/tr/th/span[contains(text(), 'Run Daily')]/p-sorticon/i")
    private WebElement iconRunDailySort;


    //Filter Xpaths

    @FindBy(xpath = "//th/span[contains(text(),'Rule Name')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonRuleNameFilter;
    
    @FindBy(xpath = "//div/p-columnfilterformelement/p-autocomplete/span/ul/li/input")
    private WebElement enterRuleNameFilter;

    @FindBy(xpath = "//th/span[contains(text(),'Status')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonStatusFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter Status']")
    private WebElement enterStatusFilter;

    @FindBy(xpath = "//th/span[contains(text(),'City')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonRunDailyFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter City']")
    private WebElement enterRunDailyFilter;

    @FindBy(xpath = "//rxc-table-header/div[1]/button[1]/span[1]")
    private WebElement buttonReset;

    @FindBy(xpath = "//a[contains(text(),'Return to Tenant Selection ')]")
    private WebElement labelReturnToTenantSelection;
   
    private final String filterSearchResultXpath= "/html/body/rxc-app/rxc-intervention-rule-library/main/div/p-table/div/div[2]/table/tbody/tr";
    @FindBy(xpath = filterSearchResultXpath)
    private List<WebElement> listFilterSearchresult;
    
    @FindBy(xpath = "//rxc-intervention-rule-library/main/div/p-table/div/div[1]/rxc-table-header/div/span")
    private WebElement labelRecordFound;
    
    @FindBy(xpath = "//tbody/tr/td[\"No intervention rules found\"]")
    private WebElement noInterventionRulesFound;
    
    @FindBy(xpath = "//p-columnfilterformelement//li/span[contains(@class, 'p-autocomplete-token-icon')]")  
    private WebElement selectedFilter;
   
    @FindBy(xpath = "//p-columnfilterformelement/p-autocomplete/span/div/ul/li[1]") 
    private WebElement filterList;
    
    @FindBy(xpath = "//div/p-columnfilterformelement/p-autocomplete/span/ul/li/input")
    private WebElement inputFilterSearch;

    public InterventionRuleLibraryPO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
    }

    @Override
    public void waitForPageObjectToLoad() {
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, textboxRuleLibrarySearch);
    }


    /**
     * Retrieve the data of the new added intervention rule library
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     * @ntagore 10/31/2022
     */
    public List<InterventionRule> retrieveRuleLibraryInfo() throws ElementNotFoundException
    {
        List<InterventionRule> list = new ArrayList<>();
        logger.info("***Inside retrieveRuleLibraryInfo **** " + list);

        for ( WebElement row : rowRuleLibraryRecord )
        {
            InterventionRule RuleLibrary = new InterventionRule();
            RuleLibrary.setRuleName(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[2]")));
            RuleLibrary.setCreatedDateTime(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[3]")));
            RuleLibrary.setLastUpdatedDateTime(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[4]")));
            RuleLibrary.setRuleStatus(RuleStatus.valueOfEnum(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[5]"))).getRuleStatusIndex());   
            RuleLibrary.setNumberFormularies(Integer.parseInt(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[6]"))));
            RuleLibrary.setNumberTenants(Integer.parseInt(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[7]"))));
            RuleLibrary.setRunDailyInd(true);
            list.add(RuleLibrary);
        }
        return list;
    }
    
    
    /**
     * Retrieve all the columns from Rule Library
     * @return list of columns
     */
    public List<String> retrieveColumnHeaders()
    {
        logger.info("***retrieveColumnHeaders**** " + SeleniumPageHelperAndWaiter.retrieveWebElementsText(columnHeaders));
        List<String> columnNames = new ArrayList<String>(SeleniumPageHelperAndWaiter.retrieveWebElementsText(columnHeaders));
       return columnNames.stream().filter(item -> StringUtils.isNotBlank(item)).collect(Collectors.toList());
 
        
    }

    /**
     * method used for Rule Library search box
     * Search value can be Type value returned from the first row
     * @throws ElementNotFoundException if issue occurs
     */

    public void RuleLibrarySearch(String searchValue) throws ElementNotFoundException {

        logger.info("***Pass the search value**** " + searchValue);
        SeleniumPageHelperAndWaiter.clickWebElement(this, textboxRuleLibrarySearch);
        SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxRuleLibrarySearch, searchValue);   
        SeleniumPageHelperAndWaiter.pause(1000);
    }

    /**
     * This method will sort a column in certain order, i.e clicks the icon sort to make it asc or desc
     * 
     * @param column
     * @param order
     * @throws ElementNotFoundException
     */
    public void sortColumn(InterventionRulesColumns column, SortOrder order) throws ElementNotFoundException
    {
        switch (column) {

        case RULE_NAME:
            sortColumn(iconRuleNameSort, order);
            break;

        case CREATE_DATE:
            sortColumn(iconCreateDateSort, order);
            break;

        case MODIFY_DATE:
            sortColumn(iconModifyDateSort, order);
            break;
            
        case STATUS:
            sortColumn(iconStatusSort, order);
            break;   

        case FORMULARIES_ASSIGNED:
            sortColumn(iconFormulariesAssignedSort, order);
            break; 

        case TENANTS_ASSIGNED:
            sortColumn(iconTenantsAssignedSort, order);
            break; 
            
        case RUN_DAILY:
            sortColumn(iconRunDailySort, order);
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
     */
    public List<String> retrieveColumnData(InterventionRulesColumns column)
    {
        switch (column) {
        case RULE_NAME:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelRuleName);

        case CREATE_DATE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelCreateDate);

        case MODIFY_DATE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelModifyDate);    

        case STATUS:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelStatus); 
            
        case FORMULARIES_ASSIGNED:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelFormulariesAssigned); 
            
        case TENANTS_ASSIGNED:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelTenantsAssigned); 

        case RUN_DAILY:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelRunDaily); 

        default:
            break;
        }
        SeleniumPageHelperAndWaiter.pause(500);   // to avoid stale element exception 
        return null;
    }

    /**
     * Filter for the Rule Library
     * Select the filter by passing the filter name
     * @throws ElementNotFoundException if issue occurs
     */
    public void iconFilterRuleLibrary(InterventionRulesColumns filterName) throws ElementNotFoundException {
        switch (filterName) {
        
        case RULE_NAME:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonRuleNameFilter);
            break;
        case STATUS:    
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonStatusFilter);
            break;
        case RUN_DAILY:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonRunDailyFilter);
            break;
        default:
            break;
       }    
        SeleniumPageHelperAndWaiter.pause(500);
    }
   
    /**
     * Select the filter column to enter the filter value
     * @throws ElementNotFoundException if issue occurs
     */
    public void clickTextBoxFilterRuleLibrary(InterventionRulesColumns column) throws ElementNotFoundException {
        
        switch (column) {
        case RULE_NAME:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterRuleNameFilter);
            break;
        case STATUS: 
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterStatusFilter);
            break;
        case RUN_DAILY:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterRunDailyFilter);
            break;
            
        default:
            break;
        }    

        SeleniumPageHelperAndWaiter.pause(500);
    }

    /**
     * Search for the Rule Library columns
     * Set the field filter textbox WebElement
     *
     * @param filter value to set the textbox
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterInputSearchBoxRuleLibrary(String RuleLibraryFilterSearchTerm) throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, inputFilterSearch, RuleLibraryFilterSearchTerm, 2);      
        SeleniumPageHelperAndWaiter.pause(3000);

    }
    
    /**
     * Filter the Rule Library columns
     * 
     * @throws ElementNotFoundException if something goes wrong
     *
     */
    
    public String selectRuleLibraryFilterResult(String filterValue) throws ElementNotFoundException {

        List<String> listDropDownValues= SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, listFilterSearchresult);
        int listSize = listDropDownValues.size();
        logger.info("listDropDownValues size :" +listSize);
        logger.info("filterValue  :" +filterValue);

        if (listSize > 0) {

            for ( String listValue : listDropDownValues ) {

                if (listValue.equalsIgnoreCase(filterValue)) {
                    SeleniumPageHelperAndWaiter.clickWebElement(driver, listFilterSearchresult.get(0));
                    break;
                }
            }
        }
        SeleniumPageHelperAndWaiter.pause(1000);
        int rowCount=SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, listFilterSearchresult).size();
        String filterRowCount = rowCount + " Records Found"; 
        return filterRowCount;
    }
    
    /**
     * Record count WebElement
     *
     * @return no. of records returned
     * @throws ElementNotFoundException if issue occurs
     */
    public String filterRuleLibraryRecordCount() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, this.labelRecordFound); 
    }

    /**
     * select filter value from list
     * @throws ElementNotFoundException if issue occurs
     */
    public void selectFilterList() throws ElementNotFoundException {
          SeleniumPageHelperAndWaiter.clickWebElement(driver, filterList);
    }
    
    /**
     * select 'x' to reset the filter search webElement
     *
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterClearRuleLibrary() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, selectedFilter);
        SeleniumPageHelperAndWaiter.pause(500);
    }
    /**
     * select reset webElement
     *
     * @throws ElementNotFoundException if issue occurs
     */
    public void buttonReset() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonReset);
        SeleniumPageHelperAndWaiter.pause(500);
    }
    
    public boolean isFilterIconDisplayed(String filterName) throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.isWebElementVisible(driver, selectedFilter, 3); 
    }

    public void clickRuleLibrarySearch() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, textboxRuleLibrarySearch);  
    }
    

    /**
     * Click the Rule Library icon
     * 
     * @throws ElementNotFoundException
     */
    public void clickRuleLibrary() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, iconInterventionRuleLib);
    }
    
    
    /**
     * Return result label
     * 
     * @throws ElementNotFoundException
     */
    public String labelRowCount() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelRecordFound);
    }
    
    /**
     * Return no intervention record found 
     * for negative test case
     * @author ntagore 02/27/2023
     * @throws ElementNotFoundException
     */
    public String labelNoInterventionFound() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, noInterventionRulesFound);
    }
    
    /**
     * This method will return the row count of the filter result.
     * for negative test case
     * @author ntagore 02/27/2023
     * @return rowCount
     * @throws ElementNotFoundException If filter search result is not found
     */
    public int getFilterResultRowCount() throws ElementNotFoundException {
        int rowCount=  SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, listFilterSearchresult).size();
        return rowCount;
    }
    
    
    
}

