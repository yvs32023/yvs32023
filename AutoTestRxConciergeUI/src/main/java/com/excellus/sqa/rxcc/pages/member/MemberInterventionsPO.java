/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.member.MemberInterventionsColumns;
import com.excellus.sqa.rxcc.pages.member.MemberInterventionsPO;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.utilities.SortOrder;

/**
 * 
 * @author Husnain Zia (hzia)
 * @since 11/14/2022
 */
/**
 * @author hzia
 *
 */
public class MemberInterventionsPO extends AbstractCommonPage {

    private static final Logger logger = LoggerFactory.getLogger(MemberInterventionsPO.class);

    @FindBy(xpath = "//p-menubar//a[text()='Rx Clinical Concierge']")
    private WebElement menuHomePage;

  //@FindBy(xpath = "//rxc-member-details/p-tabmenu/div/ul/li[1]")
    @FindBy(xpath ="//span[1][contains(text(),'Interventions')]")//@ntagore 03/20/23
    private WebElement iconMemberInterventions;

    @FindBy(xpath = "//rxc-member-interventions//table/thead/tr/th/span")
    private List<WebElement> columnHeaders;   

    @FindBy(xpath = "//rxc-member-interventions//div/div[2]/span/input[@placeholder='Search keyword']")
    private WebElement textboxMemberInterventionsSearch;

    @FindBy(xpath = "//tbody")
    private List<WebElement> tableMemberInterventions;

    @FindBy(xpath = "//tbody//tr//td[2]")
    private List<WebElement> columnResults;

    @FindBy(xpath = "//rxc-member-details/rxc-member-interventions/p-table//table/tbody/tr")
    private List<WebElement> rowMemberInterventionsRecord; 
    
    // DTO check

    @FindBy(xpath = "//rxc-member-interventions/p-table/div/div[2]//table/tbody/tr/td[ count(//tr/th[contains(./span,'Created Date')]/preceding-sibling::th)+1]")
    private List<WebElement> labelCreatedDate;

    @FindBy(xpath = "//rxc-member-interventions/p-table/div/div[2]//table/tbody/tr/td[ count(//tr/th[contains(./span,'Status Change Date')]/preceding-sibling::th)+1]")
    private List<WebElement> labelStatusChangeDate;

    @FindBy(xpath = "//rxc-member-interventions/p-table/div/div[2]//table/tbody/tr/td[ count(//tr/th[contains(./span,'Status')]/preceding-sibling::th)+1]")
    private List<WebElement> labelStatus;

    @FindBy(xpath = "//rxc-member-interventions/p-table/div/div[2]//table/tbody/tr/td[ count(//tr/th[contains(./span,'Plan Cost')]/preceding-sibling::th)+1]")
    private List<WebElement> labelPlanCost;

    @FindBy(xpath = "//rxc-member-interventions/p-table/div/div[2]//table/tbody/tr/td[ count(//tr/th[contains(./span,'Member Cost')]/preceding-sibling::th)+1]")
    private List<WebElement> labelMemberCost;

    @FindBy(xpath = "//rxc-member-interventions/p-table/div/div[2]//table/tbody/tr/td[ count(//tr/th[contains(./span,'Target Drug')]/preceding-sibling::th)+1]")
    private List<WebElement> labelTargetDrug;

    @FindBy(xpath = "//rxc-member-interventions/p-table/div/div[2]//table/tbody/tr/td[ count(//tr/th[contains(./span,'Provider')]/preceding-sibling::th)+1]")
    private List<WebElement> labelProvider;

    // SortXpaths 
    @FindBy(xpath = "//rxc-member-interventions/p-table/div/div[2]/table/thead/tr/th[1]/span[contains(text(), 'Created Date')]/p-sorticon/i")
    private WebElement iconCreatedDateSort;

    @FindBy(xpath = "//rxc-member-interventions/p-table/div/div[2]/table/thead/tr/th[2]/span[contains(text(), 'Status Change Date')]/p-sorticon/i")
    private WebElement iconProviderStatusChangeDateSort;

    @FindBy(xpath = "//rxc-member-interventions/p-table/div/div[2]/table/thead/tr/th[3]/span[contains(text(), 'Status')]/p-sorticon/i")
    private WebElement iconProviderStatusSort;

    @FindBy(xpath = "//rxc-member-interventions/p-table/div/div[2]/table/thead/tr/th[4]/span[contains(text(), 'Plan Cost')]/p-sorticon/i")
    private WebElement iconPlanCostSort;

    @FindBy(xpath = "//rxc-member-interventions/p-table/div/div[2]/table/thead/tr/th[5]/span[contains(text(), 'Member Cost')]/p-sorticon/i")
    private WebElement iconMemberCostSort;
   
    @FindBy(xpath = "//rxc-member-interventions/p-table/div/div[2]/table/thead/tr/th[6]/span[contains(text(), 'Target Drug')]/p-sorticon/i")
    private WebElement iconTargetDrugSort;

    @FindBy(xpath = "//rxc-member-interventions/p-table/div/div[2]/table/thead/tr/th[7]/span[contains(text(), 'Provider')]/p-sorticon/i")
    private WebElement iconProviderSort;

    //Filter Xpaths
    
    @FindBy(xpath = "//th/span[contains(text(),'Created Date')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonCreatedDateFilter;
    
    @FindBy(xpath = "//div[2]/div[1]/div/p-columnfilterformelement/p-calendar/span/input")
    private WebElement enterCreatedDateFilter;

    @FindBy(xpath = "//th/span[contains(text(),'Status Change Date')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonProviderStatusChangeDateFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter Status Change Date']")
    private WebElement enterStatusChangeDateFilter;

    @FindBy(xpath = "//th/span[contains(text(),'Status')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonProviderStatusFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter Status']")
    private WebElement enterStatusFilter;

    @FindBy(xpath = "//th/span[contains(text(),'Plan Cost')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonPlanCostFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter Plan Cost']")
    private WebElement enterPlanCostFilter;

    @FindBy(xpath = "//th/span[contains(text(),'Member Cost')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonMemberCostFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter Member Cost']")
    private WebElement enterMemberCostFilter;

    @FindBy(xpath = "//th/span[contains(text(),'Target Drug')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonTargetDrugFilter;
    
    @FindBy(xpath = "//div[2]/div[1]/div/p-columnfilterformelement/p-multiselect/div/div[3]/span")
    private WebElement buttonTargetDrugFilterDropdown;
    
    @FindBy(xpath = "//html/body/div[2]/div[1]/div/p-columnfilterformelement/p-multiselect/div/div[4]/div[1]/div[2]/input")
    private WebElement enterTargetDrugFilter;

    @FindBy(xpath = "//th/span[contains(text(),'Provider')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonProviderFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter Provider']")
    private WebElement enterProviderFilter;

    @FindBy(xpath = "//button[.='Reset']")
    private WebElement buttonReset;

    @FindBy(xpath = "//a[contains(text(),'Return to Tenant Selection ')]")
    private WebElement labelReturnToTenantSelection;
   
    private final String filterSearchResultXpath= "/html/body/rxc-app/rxc-member-interventions/main/div/p-table/div/div[2]/table/tbody/tr";
    @FindBy(xpath = filterSearchResultXpath)
    private List<WebElement> listFilterSearchresult;
 
    @FindBy(xpath = "//rxc-member-details//rxc-member-interventions//p-table//div//div[1]//div//p")
    private WebElement labelRecordFound;
    
    @FindBy(xpath = "//button[.='Clear']")
    private WebElement selectedFilter;
    
    @FindBy(xpath = "//p-columnfilterformelement/div/p-autocomplete/span/div/ul/li[1]") 
    private WebElement filterList;
    
    @FindBy(xpath = "//p-columnfilterformelement/div/p-autocomplete/span/ul/li/input[@role='searchbox']")
    private WebElement inputFilterSearch;
    
    @FindBy(xpath = "//html/body/div[2]/div[1]/div/p-columnfilterformelement/p-calendar/span/input")
    private WebElement inputFilterSearchCreatedDate;
    
    @FindBy(xpath = "//button[.='Apply']")
    private WebElement buttonApply;
    
    @FindBy(xpath = "//p-columnfilterformelement/p-multiselect/div/div[3]/span")
    private WebElement dropdownTargetDrug;
    
    @FindBy(xpath = "//p-columnfilterformelement/p-multiselect/div/div[4]/div[1]/div[2]/input")
    private WebElement inputTargetDrugFilterSearch;
    
    @FindBy(xpath = "//p-columnfilterformelement/p-multiselect/div/div[4]/div[2]/ul/p-multiselectitem/li/div/div")
    private WebElement checkboxTargetDrugFilterSearch;
    
    @FindBy(xpath = "//p-columnfilterformelement/p-multiselect/div/div[4]/div[1]/button/span[1]")
    private WebElement filterTargetDrugx;
    
    //added @ntagore
    
    @FindBy(xpath = "//rxc-member-details/rxc-member-interventions/p-table/div/div[2]/table/tbody/tr")
    private WebElement selectFirstRow;
    
    public MemberInterventionsPO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
    }

    @Override
    public void waitForPageObjectToLoad() {
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, textboxMemberInterventionsSearch);
    }


    /**
     * Retrieve the data of the member intervention
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public List<MemberIntervention> retrieveMemberInterventionsInfo() throws ElementNotFoundException
    {
        List<MemberIntervention> list = new ArrayList<>();
        logger.info("***Inside retrieveMemberInterventionsInfo **** " + list);

        for ( WebElement row : rowMemberInterventionsRecord )
        {
            MemberIntervention memberIntervention = new MemberIntervention();
            
            memberIntervention.setCreatedDateTime(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[1]")));
            memberIntervention.setQueueStatusChangeDateTime(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[2]")));          
            memberIntervention.setQueueStatusCode(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[3]")));
            memberIntervention.setTargetProductName(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[6]")));
            memberIntervention.setOverrideProviderName(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[7]")));

            list.add(memberIntervention);
   
        }

        return list;
    }
    
    
    /**
     * Retrieve all the columns from member interventions
     * @return list of columns
     */
    public List<String> retrieveColumnHeaders()
    {
        logger.info("***retrieveColumnHeaders**** " + SeleniumPageHelperAndWaiter.retrieveWebElementsText(columnHeaders));
        List<String> columnNames = SeleniumPageHelperAndWaiter.retrieveWebElementsText(columnHeaders);
        return columnNames;
    }

    /**
     * method used for member interventions search box
     * Search value can be Type value returned from the first row
     * @throws ElementNotFoundException if issue occurs
     */

    public void memberInterventionsSearch(String searchValue) throws ElementNotFoundException {

        logger.info("***Pass the search value**** " + searchValue);
        SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxMemberInterventionsSearch, searchValue);   
        SeleniumPageHelperAndWaiter.pause(1000);
    }

    /**
     * This method will sort a column in certain order, i.e clicks the icon sort to make it asc or desc
     * 
     * @param column
     * @param order
     * @throws ElementNotFoundException
     */
    public void sortColumn(MemberInterventionsColumns column, SortOrder order) throws ElementNotFoundException
    {
        switch (column) {

        case CREATED_DATE:
            sortColumn(iconCreatedDateSort, order);
            break;

        case STATUS_CHANGE_DATE:
            sortColumn(iconProviderStatusChangeDateSort, order);
            break;

        case STATUS:
            sortColumn(iconProviderStatusSort, order);
            break;
            
        case PLAN_COST:
            sortColumn(iconPlanCostSort, order);
            break;   

        case MEMBER_COST:
            sortColumn(iconMemberCostSort, order);
            break; 

        case TARGET_DRUG:
            sortColumn(iconTargetDrugSort, order);
            break; 
            
        case PROVIDER:
            sortColumn(iconProviderSort, order);
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
    public List<String> retrieveColumnData(MemberInterventionsColumns column)
    {
        switch (column) {
        case CREATED_DATE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelCreatedDate);

        case STATUS_CHANGE_DATE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelStatusChangeDate);

        case STATUS:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelStatus);    

        case PLAN_COST:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelPlanCost); 
            
        case MEMBER_COST:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelMemberCost); 
            
        case TARGET_DRUG:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelTargetDrug); 

        case PROVIDER:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelProvider); 

        default:
            break;
        }
        SeleniumPageHelperAndWaiter.pause(500);   // to avoid stale element exception 
        return null;
    }

    /**
     * Filter for the member interventions
     * Select the filter by passing the filter name
     * @throws ElementNotFoundException if issue occurs
     */
    public void iconFilterMemberInterventions(MemberInterventionsColumns filterName) throws ElementNotFoundException {
        switch (filterName) {
        
        case CREATED_DATE:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonCreatedDateFilter);
            break;
        case STATUS_CHANGE_DATE: 
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonProviderStatusChangeDateFilter);
            break;
        case STATUS:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonProviderStatusFilter);
            break;
        case PLAN_COST:    
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonPlanCostFilter);
            break;
        case MEMBER_COST:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonMemberCostFilter);
            break;
        case TARGET_DRUG:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonTargetDrugFilter);
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonTargetDrugFilterDropdown);
            break;
        case PROVIDER:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonProviderFilter);
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
    public void clickTextBoxFilterMemberInterventions(MemberInterventionsColumns column) throws ElementNotFoundException {
        
        switch (column) {
        case CREATED_DATE:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterCreatedDateFilter);
            break;
        case STATUS_CHANGE_DATE:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterStatusChangeDateFilter);
            break;
        case STATUS:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterStatusFilter);
            break;
        case PLAN_COST: 
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterPlanCostFilter);
            break;
        case MEMBER_COST:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterMemberCostFilter);
            break;
        case TARGET_DRUG:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterTargetDrugFilter); 
            break;
        case PROVIDER:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterProviderFilter);
            break;
            
        default:
            break;
        }    

        SeleniumPageHelperAndWaiter.pause(500);
    }

    /**
     * Search for the member interventions columns
     * Set the field filter textbox WebElement
     *
     * @param filter value to set the textbox
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterInputSearchBoxMemberInterventionsCreatedDate(String memberInterventionsFilterSearchTerm) throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, inputFilterSearchCreatedDate, memberInterventionsFilterSearchTerm, 2);      
        SeleniumPageHelperAndWaiter.pause(1000);

    }
    
    /**
     * Search for the member interventions columns
     * Set the field filter textbox WebElement
     *
     * @param filter value to set the textbox
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterInputSearchBoxMemberInterventions(String memberInterventionsFilterSearchTerm) throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, inputFilterSearch, memberInterventionsFilterSearchTerm, 2);      
        SeleniumPageHelperAndWaiter.pause(1000);

    }
    
    /**
     * Filter the member interventions columns
     * 
     * @throws ElementNotFoundException if something goes wrong
     *
     */
    
    public String selectMemberInterventionsFilterResult(String filterValue) throws ElementNotFoundException {

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
    public String filterMemberInterventionsRecordCount() throws ElementNotFoundException {
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
     * select Target Drug dropdown value from list
     * @throws ElementNotFoundException if issue occurs
     */
    public void selectTargetDrugDropDown(String filterValue) throws ElementNotFoundException {
          SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownTargetDrug);
          SeleniumPageHelperAndWaiter.clickWebElement(driver, inputTargetDrugFilterSearch);
          SeleniumPageHelperAndWaiter.pause(1000);
          SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, inputTargetDrugFilterSearch, filterValue);
          SeleniumPageHelperAndWaiter.pause(1000);
          SeleniumPageHelperAndWaiter.clickWebElement(driver, checkboxTargetDrugFilterSearch);
          SeleniumPageHelperAndWaiter.clickWebElement(driver, filterTargetDrugx);
    }
    
    /**
     * select provider dropdown value from list
     * @throws ElementNotFoundException if issue occurs
     */
    public void selectProviderDropDown(String filterValue) throws ElementNotFoundException {
          SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownTargetDrug);
          SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, inputTargetDrugFilterSearch, filterValue);
          SeleniumPageHelperAndWaiter.clickWebElement(driver, checkboxTargetDrugFilterSearch);
          SeleniumPageHelperAndWaiter.clickWebElement(driver, filterTargetDrugx);
    }
    
    /**
     * click Apply
     * @throws ElementNotFoundException if issue occurs
     */
    public void clickApply() throws ElementNotFoundException {
          SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonApply);
    }
    /**
     * click Enter in Created Date Filter
     * @throws ElementNotFoundException if issue occurs
     */
    public void clickEnterCreatedDateFilter() throws ElementNotFoundException {
    	driver.findElement(By.xpath("/html/body/div[2]/div[1]/div/p-columnfilterformelement/p-calendar/span/input")).sendKeys(Keys.ENTER);
    }
    

    /**
     * select 'x' to reset the filter search webElement
     *
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterClearMemberInterventions() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, selectedFilter);
        SeleniumPageHelperAndWaiter.pause(500);
    }
    
    
    public boolean isFilterIconDisplayed(String filterName) throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.isWebElementVisible(driver, selectedFilter, 3); 
    }

    public void clickMemberInterventionsSearch() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, textboxMemberInterventionsSearch);  
    }
    

    /**
     * Click the Member Interventions icon
     * 
     * @throws ElementNotFoundException
     */
    public void clickMemberInterventions() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, iconMemberInterventions);
    }
    
    /**
     * added @ntagore
     * select the first member row
     * @return 
     * @throws ElementNotFoundException if issue occurs
     */

    public void selectIntervention() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, selectFirstRow);    

    }
    
    /**
     * Retrieve the data of the member intervention to validate successful fax
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     * @ntagore 01/23/23 
     */
    public List<MemberIntervention> retrievInterventionsFaxInfo() throws ElementNotFoundException
    {
        List<MemberIntervention> list = new ArrayList<>();
        logger.info("***Inside retrieveMemberInterventionsInfo **** " + list);

        for ( WebElement row : rowMemberInterventionsRecord )
        {
            MemberIntervention memberIntervention = new MemberIntervention();            
         
            // Perform UI CreatedDate validation
            String createdDateTime = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[1]"));
            if ( StringUtils.isNotBlank(createdDateTime) ){
                String dateYear = createdDateTime.substring(6, 10);
                String dateMonth = createdDateTime.substring(0, 2);
                String dateDay = createdDateTime.substring(3, 5);

                createdDateTime = dateMonth+"/"+dateDay+"/"+dateYear; 
            }
            memberIntervention.setCreatedDateTime(createdDateTime);
       
            // Perform UI StatusChangeDateTime validation             
           
            String statusChangeDateTime = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[2]"));
            if ( StringUtils.isNotBlank(statusChangeDateTime) ){
                String dateYear = statusChangeDateTime.substring(6, 10);
                String dateMonth = statusChangeDateTime.substring(0, 2);
                String dateDay = statusChangeDateTime.substring(3, 5);

                statusChangeDateTime = dateMonth+"/"+dateDay+"/"+dateYear; 
            }
            memberIntervention.setQueueStatusChangeDateTime(statusChangeDateTime); 
                 
            memberIntervention.setQueueStatusCode(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[3]")).replace("Fax Successful", "11"));
   
            String setPlanCost = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[4]")).replace(",", "").substring(1);
            Double doubleSetPlanCost = Double.valueOf(setPlanCost);
            memberIntervention.setPlanCost(doubleSetPlanCost);
            
            String setMemberCost = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[5]")).replace(",", "").substring(1);
            Double doubleSetMemberCost = Double.valueOf(setMemberCost);
            memberIntervention.setMemberCost(doubleSetMemberCost);

            memberIntervention.setTargetProductName(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[6]")));
            memberIntervention.setOriginalProviderName(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[7]")));

            list.add(memberIntervention);
   
        }

        return list;
    }

}
