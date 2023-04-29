/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.OfficeLocation;
import com.excellus.sqa.rxcc.dto.Provider;
import com.excellus.sqa.rxcc.dto.provider.ProviderDirectoryColumns;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.utilities.SortOrder;
/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 03/31/2022
 * @author ntagore 02/13/2023 Refactor the code
 */
public class ProviderDirectoryPO extends AbstractCommonPage {

    private static final Logger logger = LoggerFactory.getLogger(ProviderDirectoryPO.class);

    @FindBy(xpath = "//p-menubar//a[text()='Rx Clinical Concierge']")
    private WebElement menuHomePage;

    @FindBy(xpath = "//div[@class='flex items-center gap-2 ng-star-inserted']/*[3]//*[name()='svg']")  //NT updated Xpath (09/30/2022)
    private WebElement iconProviderDir;
    
    @FindBy(xpath = "//rxc-provider-header-row/following-sibling::tr/th/span")
    private List<WebElement> columnHeaders;   
  
    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table/div/div[1]/rxc-table-header/span/input[@placeholder='Search Providers']")
    private WebElement textboxProviderDirectorySearch;

    @FindBy(xpath = "//tbody")
    private List<WebElement> tableProviderDir;

    @FindBy(xpath = "//tbody//tr//td[2]")
    private List<WebElement> columnResults;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table/div/div[2]/table/tbody/tr")
    private List<WebElement> rowProviderDirectoryRecord; 
    
    // DTO check

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'NPI')]/preceding-sibling::th)+1]")
    private List<WebElement> labelNPI;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'First Name')]/preceding-sibling::th)+1]")
    private List<WebElement> labelFirstName;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Last Name')]/preceding-sibling::th)+1]")
    private List<WebElement> labelLastName;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Taxonomy Description')]/preceding-sibling::th)+1]")
    private List<WebElement> labelTaxonomyDescription;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Phone Number')]/preceding-sibling::th)+1]")
    private List<WebElement> labelPhoneNumber;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Fax Number')]/preceding-sibling::th)+1]")
    private List<WebElement> labelFaxNumber;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'City')]/preceding-sibling::th)+1]")
    private List<WebElement> labelCity;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'State')]/preceding-sibling::th)+1]")
    private List<WebElement> labelState;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'# of Locations')]/preceding-sibling::th)+1]")
    private List<WebElement> labelNumberOfLocations;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Status')]/preceding-sibling::th)+1]")
    private List<WebElement> labelStatus;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/tbody/tr/td[ count(//tr/th[contains(./span,'Fax Verified')]/preceding-sibling::th)+1]")
    private List<WebElement> labelFaxVerified;

    // SortXpaths 

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/thead/tr/th/span[contains(text(), 'NPI')]/p-sorticon/i")
    private WebElement iconNPISort;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/thead/tr/th/span[contains(text(), 'First Name')]/p-sorticon/i")
    private WebElement iconProviderFirstNameSort;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/thead/tr/th/span[contains(text(), 'Last Name')]/p-sorticon/i")
    private WebElement iconProviderLastNameSort;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/thead/tr/th/span[contains(text(), 'Taxonomy Description')]/p-sorticon/i")
    private WebElement iconTaxonomySort;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/thead/tr/th/span[contains(text(), 'Phone Number')]/p-sorticon/i")
    private WebElement iconPhoneNumSort;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/thead/tr/th/span[contains(text(), 'Fax Number')]/p-sorticon/i")
    private WebElement iconFaxNumSort;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/thead/tr/th/span[contains(text(), 'City')]/p-sorticon/i")
    private WebElement iconCitySort;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/thead/tr/th/span[contains(text(), 'State')]/p-sorticon/i")
    private WebElement iconStateSort;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/thead/tr/th/span[contains(text(), '# of Locations')]/p-sorticon/i")
    private WebElement iconLocationsSort;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/thead/tr/th/span[contains(text(), 'Status')]/p-sorticon/i")
    private WebElement iconStatusSort;

    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table//table/thead/tr/th/span[contains(text(), 'Fax Verified')]/p-sorticon/i")
    private WebElement iconFaxVerifiedSort;

    //Filter Xpaths

    @FindBy(xpath = "//th/span[contains(text(),'NPI')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonNPIFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter NPI']")
    private WebElement enterNPIFilter;

    @FindBy(xpath = "//th/span[contains(text(),'First Name')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonProviderFirstNameFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter First Name']")
    private WebElement enterFirstNameFilter;

    @FindBy(xpath = "//th/span[contains(text(),'Last Name')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonProviderLastNameFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter Last Name']")
    private WebElement enterLastNameFilter;

    @FindBy(xpath = "//th/span[contains(text(),'Taxonomy Description')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonTaxDecFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter Taxonomy Description']")
    private WebElement enterTaxDecFilter;

    @FindBy(xpath = "//th/span[contains(text(),'Phone Number')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonPhoneNumberFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter Phone Number']")
    private WebElement enterPhoneNumberFilter;

    @FindBy(xpath = "//th/span[contains(text(),'Fax Number')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonFaxNumberFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter Fax Number']")
    private WebElement enterFaxNumberFilter;

    @FindBy(xpath = "//th/span[contains(text(),'City')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonCityFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter City']")
    private WebElement enterCityFilter;

    @FindBy(xpath = "//th/span[contains(text(),'State')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonStateFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter State']")
    private WebElement enterStateFilter;

    @FindBy(xpath = "//th/span[contains(text(),'# of Locations')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonNumberofLocationsFilter;
    
    @FindBy(xpath = "//input[@placeholder='Show All']")
    private WebElement enterNumberOfLocationsFilter;

    @FindBy(xpath = "//th/span[contains(text(),'Status')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonStatusFilter;
    
    @FindBy(xpath = "//input[@placeholder='Enter Status']")
    private WebElement enterStatusFilter;
    
    @FindBy(xpath ="//th/span[contains(text(),'Fax Verified')]/following-sibling::p-columnfilter/div/button")
    private WebElement buttonFaxVerifiedFilter;  
    
  
    @FindBy(xpath ="//p-columnfilterformelement/div/div[1]/p-radiobutton/label[\"Verified\"]")
    private WebElement radiobuttonFaxVerified;
    
    @FindBy(xpath ="//p-columnfilterformelement/div/div[2]/p-radiobutton/label[\"Not Verified\"]")
    private WebElement radiobuttonNotFaxVerified;
    
    @FindBy(xpath ="//p-columnfilterformelement/div/button[@label='Clear']")
    private WebElement radiobuttonClear;

    @FindBy(xpath = "//p-columnfilterformelement/div/button[@type='button']")
    private WebElement buttonNumLocationReset;

    @FindBy(xpath = "//a[contains(text(),'Return to Tenant Selection ')]")
    private WebElement labelReturnToTenantSelection;
   
    private final String filterSearchResultXpath= "/html/body/rxc-app/rxc-provider-directory/main/div/p-table/div/div[2]/table/tbody/tr";
    @FindBy(xpath = filterSearchResultXpath)
    private List<WebElement> listFilterSearchresult;
    
    @FindBy(xpath = "//rxc-provider-directory/main/div/p-table/div/div[1]/rxc-table-header/div/span")
    private WebElement labelRecordFound;
    
    @FindBy(xpath = "//p-columnfilterformelement//li/span[contains(@class, 'p-autocomplete-token-icon')]")  
    private WebElement selectedFilter;
    
    @FindBy(xpath = "//p-columnfilterformelement/div/p-autocomplete/span/div/ul/li[1]") 
    private WebElement filterList;
    
    @FindBy(xpath = "//p-columnfilterformelement/div/p-autocomplete/span/ul/li/input[@role='searchbox']")
    private WebElement inputFilterSearch;

    @FindBy(xpath = "//p-columnfilterformelement/div/p-inputnumber/span/input[@placeholder='Show All']")
    private WebElement inputFilterSearchNumLocations;
    
    
    public ProviderDirectoryPO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
    }

    @Override
    public void waitForPageObjectToLoad() {
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, textboxProviderDirectorySearch);
    }


    /**
     * Retrieve the data of the pharmacy directory
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public List<Provider> retrieveProviderDirectoryInfo() throws ElementNotFoundException
    {
        List<Provider> list = new ArrayList<>();
        logger.info("***Inside retrieveProviderDirectoryInfo **** " + list);

        for ( WebElement row : rowProviderDirectoryRecord )
        {
            Provider providerDirectory = new Provider();
            OfficeLocation officeLocation = new OfficeLocation();
            
            providerDirectory.setNpi(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[1]")));
            providerDirectory.setFirstName(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[2]")));
            providerDirectory.setLastName(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[3]")));
            providerDirectory.setTaxonomyDescr(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[4]")));
            providerDirectory.setStatusInd(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[10]")));
            officeLocation.setPhoneNumber(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[5]")));
            officeLocation.setFaxNumber(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[6]")));
            officeLocation.setCity(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[7]")).toUpperCase());
            officeLocation.setState(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[8]")));
            officeLocation.setId(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[9]")));
            
            /*
             * Changes to fix the regression test failure 11/16/22
             * @ntagore
             */
            String isFaxVerified = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[11]"));
            logger.info("****Fax Verified**** " + isFaxVerified);
            if (isFaxVerified.equalsIgnoreCase("Y")){
                officeLocation.setFaxVerified(true);
            }else {
                officeLocation.setFaxVerified(false);  
            }
            providerDirectory.setOfficeLocations( Arrays.asList(officeLocation) );

            list.add(providerDirectory);
   
        }

        return list;
    }
    
    
    /**
     * Retrieve all the columns from provider directory
     * @return list of columns
     */
    public List<String> retrieveColumnHeaders()
    {
        logger.info("***retrieveColumnHeaders**** " + SeleniumPageHelperAndWaiter.retrieveWebElementsText(columnHeaders));
        List<String> columnNames = SeleniumPageHelperAndWaiter.retrieveWebElementsText(columnHeaders);
        return columnNames;
    }

    /**
     * method used for provider directory search box
     * Search value can be Type value returned from the first row
     * @throws ElementNotFoundException if issue occurs
     */

    public void providerDirectorySearch(String searchValue) throws ElementNotFoundException {

        logger.info("***Pass the search value**** " + searchValue);
        SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxProviderDirectorySearch, searchValue);   
        SeleniumPageHelperAndWaiter.pause(1000);
    }

    /**
     * This method will sort a column in certain order, i.e clicks the icon sort to make it asc or desc
     * 
     * @param column
     * @param order
     * @throws ElementNotFoundException
     */
    public void sortColumn(ProviderDirectoryColumns column, SortOrder order) throws ElementNotFoundException
    {
        switch (column) {

        case NPI:
            sortColumn(iconNPISort, order);
            break;

        case FIRST_NAME:
            sortColumn(iconProviderFirstNameSort, order);
            break;

        case LAST_NAME:
            sortColumn(iconProviderLastNameSort, order);
            break;
            
        case TAXONOMY_DESCRIPTION:
            sortColumn(iconTaxonomySort, order);
            break;   

        case PHONE_NUMBER:
            sortColumn(iconPhoneNumSort, order);
            break; 

        case FAX_NUMBER:
            sortColumn(iconFaxNumSort, order);
            break; 
            
        case CITY:
            sortColumn(iconCitySort, order);
            break;

        case STATE:
            sortColumn(iconStateSort, order);
            break;
            
        case NUMBER_OF_LOCATIONS:
            sortColumn(iconLocationsSort, order);
            break;

        case STATUS:
            sortColumn(iconStatusSort, order);
            break;
            
        case FAX_VERIFIED:
            sortColumn(iconFaxVerifiedSort, order);
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
    public List<String> retrieveColumnData(ProviderDirectoryColumns column)
    {
        switch (column) {
        case NPI:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelNPI);

        case FIRST_NAME:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelFirstName);

        case LAST_NAME:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelLastName);    

        case TAXONOMY_DESCRIPTION:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelTaxonomyDescription); 
            
        case PHONE_NUMBER:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelPhoneNumber); 
            
        case FAX_NUMBER:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelFaxNumber); 

        case CITY:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelCity); 

        case STATE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelState);
            
        case NUMBER_OF_LOCATIONS:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelNumberOfLocations);

        case STATUS:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelStatus);

        case FAX_VERIFIED:
            List<String> columnValues = null;
            columnValues = SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelFaxVerified);
            for (int i = 0; i < columnValues.size(); i++){
                if (columnValues.get(i).equals("Y")) {
                    columnValues.set(i, "true");
                }
                else if (columnValues.get(i).equals("N")) {
                    columnValues.set(i, "false");
                }
            }
            return columnValues;

        default:
            break;
        }
        SeleniumPageHelperAndWaiter.pause(500);   // to avoid stale element exception 
        return null;
    }

    /**
     * Filter for the provider Directory
     * Select the filter by passing the filter name
     * @throws ElementNotFoundException if issue occurs
     */
    public void iconFilterProviderDirectory(ProviderDirectoryColumns filterName) throws ElementNotFoundException {
        switch (filterName) {
        
        case NPI:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonNPIFilter);
            break;
        case FIRST_NAME: 
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonProviderFirstNameFilter);
            break;
        case LAST_NAME:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonProviderLastNameFilter);
            break;
        case TAXONOMY_DESCRIPTION:    
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonTaxDecFilter);
            break;
        case PHONE_NUMBER:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonPhoneNumberFilter);
            break;
        case FAX_NUMBER:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonFaxNumberFilter);
            break;
        case CITY:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonCityFilter);
            break;
        case STATE:
           SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonStateFilter);
           break;
        case STATUS:
           SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonStatusFilter);
           break;
        case NUMBER_OF_LOCATIONS:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonNumberofLocationsFilter );
            break;
         case FAX_VERIFIED:
             SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonFaxVerifiedFilter );
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
    public void clickTextBoxFilterProviderDirectory(ProviderDirectoryColumns column) throws ElementNotFoundException {
        
        switch (column) {
        case NPI:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterNPIFilter);
            break;
        case FIRST_NAME:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterFirstNameFilter);
            break;
        case LAST_NAME:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterLastNameFilter);
            break;
        case TAXONOMY_DESCRIPTION: 
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterTaxDecFilter);
            break;
        case PHONE_NUMBER:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterPhoneNumberFilter);
            break;
        case FAX_NUMBER:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterFaxNumberFilter); 
            break;
        case CITY:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterCityFilter);
            break;
        case STATE:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterStateFilter);
            break;
        case STATUS:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterStatusFilter);
            break;
        case NUMBER_OF_LOCATIONS:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterNumberOfLocationsFilter);
            break;
            
        default:
            break;
        }    

        SeleniumPageHelperAndWaiter.pause(500);
    }

    /**
     * Search for the provider directory columns
     * Set the field filter textbox WebElement
     *
     * @param filter value to set the textbox
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterInputSearchBoxProviderDirectory(String providerDirectoryFilterSearchTerm) throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, inputFilterSearch, providerDirectoryFilterSearchTerm, 2);      
        SeleniumPageHelperAndWaiter.pause(1000);

    }
    
    /**
     * Search for the provider directory Number of Location column
     * Set the field filter textbox WebElement
     *
     * @param filter value to set the textbox
     * @throws ElementNotFoundException if issue occurs
     */ 
    public void filterInputSearchNumLocationsProviderDirectory(String providerDirectoryFilterSearchTerm) throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, inputFilterSearchNumLocations, providerDirectoryFilterSearchTerm, 2);      
        SeleniumPageHelperAndWaiter.pause(1000);

    }
    
    //method to input value for fax verified
    public void filterInputSearchFaxVerifiedProviderDirectory(String providerDirectoryFilterSearchTerm) throws ElementNotFoundException {
   //     SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, inputFilterSearchNumLocations, providerDirectoryFilterSearchTerm, 2);   
        if (providerDirectoryFilterSearchTerm == "true") {
            SeleniumPageHelperAndWaiter.setRadioButton(driver, radiobuttonFaxVerified);
        }else {
            SeleniumPageHelperAndWaiter.setRadioButton(driver, radiobuttonNotFaxVerified);
        }
        SeleniumPageHelperAndWaiter.pause(1000);

    }
    
    
    /**
     * Filter the provider directory columns
     * 
     * @throws ElementNotFoundException if something goes wrong
     *
     */
    
    public String selectProviderDirectoryFilterResult(String filterValue) throws ElementNotFoundException {

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
    public String filterProviderDirectoryRecordCount() throws ElementNotFoundException {
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
     * select Number of Location filter value 
     * @throws ElementNotFoundException if issue occurs
     */
    public void selectFilterListNumLocations() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(driver, enterNumberOfLocationsFilter);
  }
    
    /**
     * select 'x' to reset the filter search webElement
     *
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterClearProviderDirectory() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, selectedFilter);
        SeleniumPageHelperAndWaiter.pause(500);
    }
    /**
     * select reset the filter Number Location webElement
     *
     * @throws ElementNotFoundException if issue occurs
     */
    
    public void filterClearNumLocations() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, buttonNumLocationReset);
        SeleniumPageHelperAndWaiter.pause(500);
    }
    
    public void filterClearFaxVerified() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, radiobuttonClear);
        SeleniumPageHelperAndWaiter.pause(500);
    }
      
    public boolean isFilterIconDisplayed(String filterName) throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.isWebElementVisible(driver, selectedFilter, 3); 
    }

    public void clickProviderDirectorySearch() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, textboxProviderDirectorySearch);  
    }
    

    /**
     * Click the Provider Directory icon
     * 
     * @throws ElementNotFoundException
     */
    public void clickProviderDirectory() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, iconProviderDir);
    }
    
    /* This method will return the phone number or fax number in the normalized form
     * Param number to be normalized
     */
    public String getNormalizeNumber(String number) {
        OfficeLocation officeLocation = new OfficeLocation();
        return officeLocation.getNormalizeNumber(number);
    }

}

