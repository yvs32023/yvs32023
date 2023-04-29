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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.OfficeLocation;
import com.excellus.sqa.rxcc.dto.Provider;
import com.excellus.sqa.rxcc.dto.member.MemberProviderTabColumns;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.IPage;
import com.excellus.sqa.selenium.utilities.SortOrder;

/**
 * Page object that handles Member Details - Provider
 * 
 * @author Husnain Zia (hzia)
 * @since 02/21/2022
 */
public class MemberDetailsProviderPO extends AbstractCommonPage implements IPage  {

    public MemberDetailsProviderPO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
    }

    private static final Logger logger = LoggerFactory.getLogger(MemberDetailsNotesPO.class);
    
    //Declared Variables

    @FindBy(xpath = "//span[1][contains(text(),'Providers')]")
    private WebElement labelProviderTab;

    @FindBy(xpath = "//span[1][contains(text(),'Prescription Claims')]")
    private WebElement labelClaimsTab;

    @FindBy(xpath = "//rxc-member-providers//p[contains(text(), 'Record')]")
    private WebElement textNumberOfRecords;

    @FindBy(xpath = "//rxc-member-providers//input[@placeholder='Search keyword']")
    private WebElement inputProviderSearch;

    // @neerutagore 03/20/23
    private final String inputClickProviderSearch ="//rxc-member-providers//input[@placeholder='Search keyword']";

    @FindBy(xpath = "//rxc-member-providers//button[span='Clear']")
    private WebElement buttonClearFilters;

    @FindBy(xpath = "//rxc-member-providers//button[span='Collapse All']")
    private WebElement buttonCollapseAll;

    @FindBy(xpath = "//div[1]/div/div[1]/button[3]/span")
    private WebElement buttonExpandAll;

    @FindBy(xpath = "//tbody//tr[2]//td[2]")
    private WebElement tableProviderExpanded;	

    @FindBy(xpath = "//tbody")
    private List<WebElement> textNoRecords;

    //newly added
    @FindBy(xpath = "//table[@role='table']//thead/tr/th/span")
    private List<WebElement> columnHeaders;

    //DTO Check
    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/tbody/tr/td[2]")
    private List<WebElement> labelNameNPI;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/tbody/tr/td[3]")
    private List<WebElement> labelPhone;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/tbody/tr/td[4]")
    private List<WebElement> labelFax;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/tbody/tr/td[5]")
    private List<WebElement> labelCity;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/tbody/tr/td[6]")
    private List<WebElement> labelState;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/tbody/tr/td[7]")
    private List<WebElement> labelPostalCode;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/tbody/tr/td[8]")
    private List<WebElement> labelNumberOfLocations;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/tbody/tr/td[9]")
    private List<WebElement> labelTaxonomy;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/tbody/tr/td[10]")
    private List<WebElement> labelProviderStatus;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/tbody/tr/td[./*[contains(@class,'ng-star-inserted')]]")
    private List<WebElement> labelProviderVerified;

    private String attribProviderVerifiedXPath ="//rxc-member-providers/p-table/div/div[2]/table/tbody/tr[%d]/td[./*[contains(@class,'ng-star-inserted') and contains(@style, 'color: green')]]";

    //sortXpath

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Name(NPI)')]/p-sorticon/i")
    private WebElement iconNameNPISort;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Phone')]/p-sorticon/i")
    private WebElement iconPhoneSort;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Fax')]/p-sorticon/i")
    private WebElement iconFaxSort;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'City')]/p-sorticon/i")
    private WebElement iconCitySort;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'State')]/p-sorticon/i")
    private WebElement iconStateSort;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Postal Code')]/p-sorticon/i")
    private WebElement iconPostalCodeSort;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), '# of Locations')]/p-sorticon/i")
    private WebElement iconNumberOfLocationsSort;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Taxonomy')]/p-sorticon/i")
    private WebElement iconTaxonomySort;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Provider Status')]/p-sorticon/i")
    private WebElement iconProviderStatusSort;

    @FindBy(xpath ="//rxc-member-providers/p-table/div/div[2]/table/thead/tr/th/span[contains(text(), 'Provider Verified')]/p-sorticon/i")
    private WebElement iconProviderVerifiedSort;

    @FindBy(xpath = "//rxc-member-providers/p-table/div/div[2]/table/tbody/tr")
    private List<WebElement> rowProviderRecord; 

    
    //Filter Xpaths 03/27/23

    @FindBy(xpath = "//p-columnfilter[@field='npi']")
    private WebElement buttonNPIFilter;

    @FindBy(xpath = "//p-multiselect[@placeholder='Select fullName (NPI)']")
    private WebElement enterNPIFilter;

    @FindBy(xpath = "//p-columnfilter[@field='defaultLocation.phoneNumber']//span")
    private WebElement buttonPhoneNumberFilter;

    @FindBy(xpath = "//p-multiselect[@placeholder='Select phoneNumber']")
    private WebElement enterPhoneNumberFilter;

    @FindBy(xpath = "//p-columnfilter[@field='defaultLocation.faxNumber']")
    private WebElement buttonFaxNumberFilter;

    @FindBy(xpath = "//p-multiselect[@placeholder='Select fax number']")
    private WebElement enterFaxNumberFilter;

    @FindBy(xpath = "//p-columnfilter[@field='defaultLocation.city']")
    private WebElement buttonCityFilter;

    @FindBy(xpath = "//p-multiselect[@placeholder='Select city']")
    private WebElement enterCityFilter;

    @FindBy(xpath = "//p-columnfilter[@field='defaultLocation.state']")
    private WebElement buttonStateFilter;

    @FindBy(xpath = "//p-multiselect[@placeholder='Select state']")
    private WebElement enterStateFilter;

    @FindBy(xpath = "//p-columnfilter[@field='defaultLocation.postalCode']")
    private WebElement buttonPostalCodeFilter;

    @FindBy(xpath = "//p-multiselect[@placeholder='Select Postal Code']")
    private WebElement enterPostalCodeFilter;

    @FindBy(xpath = "//p-columnfilter[@field='officeLocations.length']")
    private WebElement buttonNumberofLocationsFilter;

    @FindBy(xpath = "//p-multiselect[@placeholder='Select number of locations']")
    private WebElement enterNumberOfLocationsFilter;

    @FindBy(xpath = "//p-columnfilter[@field='taxonomyDescr']")
    private WebElement buttonTaxonomyFilter;

    @FindBy(xpath = "//p-multiselect[@placeholder='Select taxonomy']")
    private WebElement enterTaxonomyFilter;

    @FindBy(xpath = "//p-columnfilter[@field='statusInd']")
    private WebElement buttonStatusFilter;

    @FindBy(xpath = "//p-multiselect[@placeholder='Select status']")
    private WebElement enterStatusFilter;

    @FindBy(xpath ="//p-columnfilter[@field='defaultLocation.faxVerified']")
    private WebElement buttonFaxVerifiedFilter;  

    @FindBy(xpath ="//p-columnfilterformelement/div[1]/p-radiobutton/label['Verified']")
    private WebElement radiobuttonFaxVerified;

    @FindBy(xpath ="//p-columnfilterformelement/div[2]/p-radiobutton/label['Not Verified']")
    private WebElement radiobuttonNotFaxVerified;

    @FindBy(xpath ="//body/div//button[contains(., 'Clear')]")
    private WebElement buttonFaxVerifiedClear;

    @FindBy(xpath = "//body/div/div/button[contains(., 'Apply')]")
    private WebElement buttonFaxVerifiedApply;

    @FindBy(xpath = "//a[contains(text(),'Return to Tenant Selection ')]")
    private WebElement labelReturnToTenantSelection;

    private final String filterSearchResultXpath= "//rxc-member-providers/p-table/div/div[2]/table/tbody/tr";
    @FindBy(xpath = filterSearchResultXpath)
    private List<WebElement> listFilterSearchresult;

    @FindBy(xpath = "//p-columnfilterformelement/p-multiselect/*/div/*/div/input[@role='textbox']")
    private WebElement textboxProviderFilterDropDown; 

    private final String providerFilterSearchResultXpath = "//p-columnfilterformelement/p-multiselect/div/div[4]/div[2]/ul/p-multiselectitem";

    @FindBy(xpath = providerFilterSearchResultXpath)
    private List<WebElement> rowProviderSearchResults;

    @FindBy(xpath = "//p-columnfilterformelement/p-multiselect/*/div/*/button[@type='button']")
    private WebElement buttonFilterDropDownExit; 

    @FindBy(xpath = "//p[contains(text(),'Records Found')]")
    private WebElement labelFilterRecordCount; 

    @FindBy(xpath = "//button/span[contains(.,'Clear')]")
    private WebElement buttonClear;

    private final String providerFilterSearchCountXpath = "//rxc-member-providers/p-table/div/div[2]/table/tbody/tr";
    
    @FindBy(xpath = "//button[@type='button']/span[contains(.,'Apply')]")
    private WebElement buttonApply;
    
    @FindBy(xpath ="//div[@role='checkbox']")
    private WebElement filterCheckBox;

    @FindBy(xpath = providerFilterSearchCountXpath)
    private List<WebElement> rowMemberProviderSearchCount;

    @FindBy(xpath = "//label[normalize-space()='Show All']")
    private WebElement radiobuttonShowAll;

    @Override
    public void waitForPageObjectToLoad() {
        logger.debug("Waiting for member search textbox to load");
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, inputProviderSearch);
    }

    /**
     * Click the Provider tab under Member Details
     * 
     * @throws ElementNotFoundException
     */
    public void clickProvider() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, labelProviderTab);
    }

    /**
     * Click the Pharmacy Claims tab under Member Details
     * 
     * @throws ElementNotFoundException
     */
    public void clickClaims() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, labelClaimsTab);
    }

    /**
     * Click the Pharmacy Claims tab under Member Details
     * @return 
     * 
     * @throws ElementNotFoundException
     */
    public List<String> checkNumberOfrecords() throws ElementNotFoundException {
        List<String> numberOfRecords = SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, textNoRecords);
        return numberOfRecords;
    }


    /**
     * Click Expand All under Provider Tab
     * 
     * @throws ElementNotFoundException
     */
    public void clickExpandAll() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, buttonExpandAll, 5);
    }

    /**
     * Click Collapse All under Provider Tab
     * 
     * @throws ElementNotFoundException
     */
    public void clickCollapseAll() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, buttonCollapseAll, 5);
    }  

    /**
     * Check to see if Claims Visible after Provider Expanded
     * @return 
     * @return true if visible
     * @throws ElementNotFoundException
     */
    public boolean checkClaimsVisible() {
        boolean display=false;
        //ignoring exception because if element is not visible then we know if the collapse all button is working
        try {
            boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, tableProviderExpanded, 5);
            if ( displayed )
                display=true;

        }
        catch (Exception e)
        {
        }
        return display;
    }
    /*
     * @ntagore 03/13/2023
     */
    /**
     * Retrieve all the columns of Provider Tab
     * @return list of columns
     */
    public List<String> retrieveColumnHeaders()
    {

        logger.info("***retrieveColumnHeaders**** " + SeleniumPageHelperAndWaiter.retrieveWebElementsText(columnHeaders));
        List<String> columnNames = SeleniumPageHelperAndWaiter.retrieveWebElementsText(columnHeaders);
        return columnNames;
    }

    /**
     * Retrieve the data of the pharmacy directory
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public List<Provider> retrieveProviderInfo() throws ElementNotFoundException
    {
        List<Provider> list = new ArrayList<>();
        logger.info("***Inside retrieveProviderInfo **** " + list);
        int i = 0;

        for ( WebElement row : rowProviderRecord )
        {
            Provider memberProvider = new Provider();
            OfficeLocation officeLocation = new OfficeLocation();

            if (i == 1) break ;
            //Provider LastName, FirstName and NPI
            String setProviderIdName = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[2]"));
            logger.info("***Inside setProviderIdName **** " + setProviderIdName);        
            String  setProviderFirstLastName = StringUtils.substringBeforeLast(setProviderIdName, " (");       
            String setProviderNPI = StringUtils.substringAfterLast(setProviderIdName, "(");
            setProviderNPI = StringUtils.remove(setProviderNPI, ")");
            logger.info("***Inside setProviderFirstLastName **** " + setProviderFirstLastName);


            memberProvider.setFirstName(StringUtils.substringBefore(setProviderFirstLastName, " "));
            memberProvider.setLastName(StringUtils.substringAfter(setProviderFirstLastName, " "));
            memberProvider.setNpi(setProviderNPI.trim());

            officeLocation.setPhoneNumber(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[3]")));

            officeLocation.setFaxNumber(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[4]")));


            officeLocation.setCity(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[5]")));

            officeLocation.setState(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[6]")));
            officeLocation.setPostalCode(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[7]")));
            officeLocation.setId(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[8]")));

            memberProvider.setTaxonomyDescr(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[9]")));
            memberProvider.setStatusInd(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[10]")));

            //Provider Verified
            int rowCount = SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelProviderVerified).size();        
            try {
                for ( int rc=0; rc<rowCount; rc++ )
                {
                    List<WebElement> webElementValues =   driver.findElements( By.xpath( String.format(attribProviderVerifiedXPath, i+1) ) );
                    if (webElementValues.size() == 1 ) {
                        officeLocation.setFaxVerified(true);
                    }
                    else if (webElementValues.size()== 0) {
                        officeLocation.setFaxVerified(false); 
                    }
                    SeleniumPageHelperAndWaiter.pause(100);
                }                
            }
            catch (Exception e) {
            }                  
            memberProvider.setOfficeLocations( Arrays.asList(officeLocation) );
            list.add(memberProvider);
            i++;
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
    public void sortColumn(MemberProviderTabColumns column, SortOrder order) throws ElementNotFoundException
    {
        switch (column) {

        case NAME_NPI:
            sortColumn(iconNameNPISort, order);
            break;

        case PHONE:
            sortColumn(iconPhoneSort, order);
            break;

        case FAX:
            sortColumn(iconFaxSort, order);
            break;

        case CITY:
            sortColumn(iconCitySort, order);
            break;   

        case STATE:
            sortColumn(iconStateSort, order);
            break; 

        case POSTAL_CODE:
            sortColumn(iconPostalCodeSort, order);
            break; 

        case NUMBER_OF_LOCATIONS:
            sortColumn(iconNumberOfLocationsSort, order);
            break;

        case TAXONOMY:
            sortColumn(iconTaxonomySort, order);
            break;

        case PROVIDER_STATUS:
            sortColumn(iconProviderStatusSort, order);
            break;

        case PROVIDER_VERIFIED:
            sortColumn(iconProviderVerifiedSort, order);
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
    public List<String> retrieveColumnData(MemberProviderTabColumns column)
    {
        switch (column) {
        case NAME_NPI:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelNameNPI);

        case PHONE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelPhone);

        case FAX:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelFax);    

        case CITY:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelCity); 

        case STATE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelState); 

        case POSTAL_CODE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelPostalCode); 

        case NUMBER_OF_LOCATIONS:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelNumberOfLocations);

        case TAXONOMY:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelTaxonomy);

        case PROVIDER_STATUS:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelProviderStatus);

        case PROVIDER_VERIFIED:

            int rowCount = SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelProviderVerified).size();
            List<String> columnValues = new ArrayList<>();

            try {
                for ( int i=0; i<rowCount; i++ )
                {
                    List<WebElement> webElementValues =   driver.findElements( By.xpath( String.format(attribProviderVerifiedXPath, i+1) ) );
                    if (webElementValues.size() == 1 ) {
                        columnValues.add("true");
                    }
                    else if (webElementValues.size()== 0) {
                        columnValues.add("false");
                    }
                    SeleniumPageHelperAndWaiter.pause(100);
                }                
            }
            catch (Exception e) {
            }
            return columnValues;

        default:
            break;

        }
        SeleniumPageHelperAndWaiter.pause(500);   // to avoid stale element exception 
        return null;
    }
    /**
     * Search for Provider
     * 
     * @param Provider search information
     * @return Results from search
     * @throws ElementNotFoundException 
     */

    public void providerSearch(String searchValue) throws ElementNotFoundException {
        try {
            logger.info("***Pass the search value**** " + searchValue);
            boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(this, By.xpath(inputClickProviderSearch));
            logger.info("***is displayed **** " + displayed);
            if ( displayed )
                SeleniumPageHelperAndWaiter.clickBy(this, By.xpath(inputClickProviderSearch));
            SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, inputProviderSearch, searchValue);
        }
        catch (Exception e)
        {
            logger.error("Provider NPI Search element not found");
        }
    }

    /**
     * Filter for the Provider
     * Select the filter by passing the filter name
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterProvider(MemberProviderTabColumns filterName) throws ElementNotFoundException {
        switch (filterName) {
        case NAME_NPI:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonNPIFilter);
            break;
        case PHONE:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonPhoneNumberFilter);
            break;
        case FAX:    
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonFaxNumberFilter);
            break;
        case CITY:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonCityFilter);
            break;
        case STATE:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonStateFilter);
            break;
        case POSTAL_CODE:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonPostalCodeFilter);
            break;
        case NUMBER_OF_LOCATIONS: 
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonNumberofLocationsFilter);
            break;
        case TAXONOMY:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonTaxonomyFilter);
            break;        
        case PROVIDER_STATUS:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonStatusFilter);
            break;       
        case PROVIDER_VERIFIED:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonFaxVerifiedFilter);
            break;

        default:
            break;
        }
        SeleniumPageHelperAndWaiter.pause(1000);
    }

    /**
     * Filter for the Provider
     * Select the filter dropdown
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterDropDownProvider(MemberProviderTabColumns column) throws ElementNotFoundException {
        switch (column) {

        case NAME_NPI:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterNPIFilter);
            break;
        case PHONE:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterPhoneNumberFilter);
            break;
        case FAX:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterFaxNumberFilter);   
            break;
        case CITY:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterCityFilter);
            break;
        case STATE:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterStateFilter);
            break;
        case POSTAL_CODE:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterPostalCodeFilter);
            break;
        case NUMBER_OF_LOCATIONS:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterNumberOfLocationsFilter);
            break;
        case TAXONOMY:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterTaxonomyFilter);
            break;
        case PROVIDER_STATUS:
            SeleniumPageHelperAndWaiter.clickWebElement(driver, enterStatusFilter);
            break;

        default:
            break;
        }

        SeleniumPageHelperAndWaiter.pause(1000);
    }

    /**
     * Filter for the provider
     * Set the field filter textbox WebElement
     *
     * @param filter value to set the textbox
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterDropDownInputTextBoxProvider(String providerFilterSearchTerm) throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textboxProviderFilterDropDown, providerFilterSearchTerm, 2);      
        SeleniumPageHelperAndWaiter.pause(1000);

    }
    
    /**
     * Method to input value for provider verified filter
     * @param providerFilterSearchTerm
     * @throws ElementNotFoundException
     */
    public void filterInputSearchFaxVerifiedProvider(String providerFilterSearchTerm) throws ElementNotFoundException {
        if (providerFilterSearchTerm == "true") {
            SeleniumPageHelperAndWaiter.setRadioButton(driver, radiobuttonFaxVerified);
        }else {
            SeleniumPageHelperAndWaiter.setRadioButton(driver, radiobuttonNotFaxVerified);
        }
        SeleniumPageHelperAndWaiter.pause(1000);

    }

    /**
     * Filter for the Provider
     * Select the first row from th search result
     * @throws ElementNotFoundException is something goes wrong
     */
    public String clickFirstProviderFilterResult(String filterValue) throws ElementNotFoundException {

        List<String> listDropDownValues= SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, rowProviderSearchResults);
        int listSize = listDropDownValues.size();
        logger.info("listDropDownValues size :" +listSize);
        logger.info("filterValue  :" +filterValue);

        if (listSize > 0) {

            for ( String listValue : listDropDownValues ) {

                if (listValue.concat(filterValue).contains(filterValue)) {
                    SeleniumPageHelperAndWaiter.clickWebElement(driver, rowProviderSearchResults.get(0));
                    break;
                }
            }
        }
        closeFilter();
        applyFilterSelection();
        int rowCount=SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, rowProviderRecord).size();
        String filterRowCount = (rowCount) + " Records Found"; 
        SeleniumPageHelperAndWaiter.pause(1000);
        return filterRowCount;
    }

    /**
     * Filter for the Provider
     * Record count WebElement
     * @return no. of records returned
     * @throws ElementNotFoundException if issue occurs
     */

    public String filterDropDownRecordCountProvider() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, this.labelFilterRecordCount); 
    }

    /**
     * Filter for the Provider
     * Clear the filter search WebElement
     * @throws ElementNotFoundException if issue occurs
     */

    public void filterClearProvider() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonClear);  
    }
    
    /**
     * select filter checkbox
     * @throws ElementNotFoundException if issue occurs
     */

    public void selectCheckboxFilter() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(driver, filterCheckBox);
        SeleniumPageHelperAndWaiter.pause(1000);
    }
    
    /**
     * Close filter icon
     * @throws ElementNotFoundException if issue occurs
     */

    public void closeFilter() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonFilterDropDownExit);
        SeleniumPageHelperAndWaiter.pause(1000);
    }
    
    /**
     * Apply filter selection
     * @throws ElementNotFoundException if issue occurs
     */

    public void applyFilterSelection() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonApply);  
    }
    
    /* This method will return the phone number or fax number in the normalized form
     * Param number to be normalized
     */
    public String getNormalizePhoneOrFaxNumber(String phoneOrFaxNumber) {
        OfficeLocation officeLocation = new OfficeLocation();
        return officeLocation.getNormalizeNumber(phoneOrFaxNumber);
    }
    
    /**
     * Filter for the Provider Verified 
     * Get the filter search result count
     * @throws ElementNotFoundException is something goes wrong
     */
    public String getProviderVerifiedFilterResult() throws ElementNotFoundException {
        clickCollapseAll(); 
        int rowCount=SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, rowProviderRecord).size();
        return String.valueOf(rowCount)+ " Records Found";
    }

}

