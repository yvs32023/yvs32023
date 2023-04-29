/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.pharmacy;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.Pharmacy;
import com.excellus.sqa.rxcc.dto.pharmacy.PharmacyDirectoryColumns;
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
 * 
 * @since 06/02/2022
 */
    
    public class PharmacyDirectoryPO extends AbstractCommonPage implements IPage {
	
	private static final Logger logger = LoggerFactory.getLogger(PharmacyDirectoryPO.class);

	@FindBy(xpath = "//p-menubar//a[text()='Rx Clinical Concierge']")
	private WebElement menuHomePage;
	
	@FindBy(xpath = "//div[@class='flex items-center gap-2 ng-star-inserted']/*[4]//*[name()='svg']")	// nt (09/30/22) corrected xpath
	private WebElement iconPharmacyDir;
	
	@FindBy(xpath = "//rxc-pharmacy-header-row/following-sibling::tr/th/span")
	private List<WebElement> columnHeaders;
	
	@FindBy(xpath = "//rxc-pharmacies//input[@placeholder='Search Pharmacies']")
    private WebElement textboxPharmacyDirectorySearch;

	@FindBy(xpath = "//tbody")
	private List<WebElement> tablePharmacyDir;
	
    private final String selectFirstPharmacyXpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/tbody/tr[1]/td[1]";
    
    @FindBy(xpath = selectFirstPharmacyXpath)
    private WebElement selectFirstPharmacy;
    
    //Sort Xpath
	
	@FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/thead/tr/th[1]/span/p-sorticon/i")
	private WebElement iconNPISort;

	@FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/thead/tr/th[2]/span/p-sorticon/i")
	private WebElement iconPharmacyNameSort;
	
	@FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/thead/tr/th[3]/span/p-sorticon/i")
	private WebElement iconTaxonomySort;
	
	@FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/thead/tr/th[4]/span/p-sorticon/i")
	private WebElement iconPhoneNumSort;
	
	@FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/thead/tr/th[5]/span/p-sorticon/i")
	private WebElement iconCitySort;
	
	@FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/thead/tr/th[6]/span/p-sorticon/i")
	private WebElement iconStateSort;
	
	@FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/thead/tr/th[7]/span/p-sorticon/i")
	private WebElement iconStatusSort;
	
    // DTO check

    @FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/tbody/tr/td[1]")
    private List<WebElement> labelPharmacyNPI;

    @FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/tbody/tr/td[2]")
    private List<WebElement> labelPharmacyName;

    @FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/tbody/tr/td[3]")
    private List<WebElement> labelPharmacyTaxonomyDescription;

    @FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/tbody/tr/td[4]")
    private List<WebElement> labelPhoneNumber;

    @FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/tbody/tr/td[5]")
    private List<WebElement> labelPharmacyCity;

    @FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/tbody/tr/td[6]")
    private List<WebElement> labelPharmacyState;

    @FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/tbody/tr/td[7]")
    private List<WebElement> labelPharmacyStatus;
    
    @FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[2]/table/tbody/tr")	// GC (06/23/22) corrected xpath
    private List<WebElement> rowPharmacyDirectoryRecord; 
    
    // Filter Xpath
	
	@FindBy(xpath = "//th/span[contains(text(),'NPI')]/following-sibling::p-columnfilter/div/button")
	private WebElement iconNPIFilter;
	
	@FindBy(xpath = "//input[@placeholder='Enter NPI']")
	private WebElement enterNPIFilter;
	
	@FindBy(xpath = "//th/span[contains(text(),'Pharmacy Name')]/following-sibling::p-columnfilter/div/button")
	private WebElement iconPharmacyNameFilter;
	
	@FindBy(xpath = "//input[@placeholder='Enter Pharmacy Name']")
    private WebElement enterPharmacyNameFilter;
    
	@FindBy(xpath = "//th/span[contains(text(),'Taxonomy Description')]/following-sibling::p-columnfilter/div/button")
	private WebElement iconTaxDecFilter;
	
	@FindBy(xpath = "//input[@placeholder='Enter Taxonomy Description']")
	private WebElement enterTaxonomyDescFilter;
	
	@FindBy(xpath = "//th/span[contains(text(),'Phone Number')]/following-sibling::p-columnfilter/div/button")
	private WebElement iconPhoneNumberFilter;
	
    @FindBy(xpath = "//input[@placeholder='Enter Phone Number']")
    private WebElement enterPhoneNumberFilter;	
	
	@FindBy(xpath = "//th/span[contains(text(),'City')]/following-sibling::p-columnfilter/div/button")
	private WebElement iconCityFilter;
	
    @FindBy(xpath = "//input[@placeholder='Enter City']")
    private WebElement enterCityFilter;	
	
	@FindBy(xpath = "//th/span[contains(text(),'State')]/following-sibling::p-columnfilter/div/button")
	private WebElement iconStateFilter;
	
    @FindBy(xpath = "//input[@placeholder='Enter State']")
    private WebElement enterStateFilter;	
	
	@FindBy(xpath = "//th/span[contains(text(),'Status')]/following-sibling::p-columnfilter/div/button")
	private WebElement iconStatusFilter;
	
    @FindBy(xpath = "//input[@placeholder='Enter Status']")
    private WebElement enterStatusFilter;	
	
	@FindBy(xpath = "//p-columnfilterformelement/div/p-autocomplete/span/ul/li/input[@role='searchbox']")
	private WebElement inputFilterSearch;
	
	private final String pharmacyDirectoryFilterSearchResultXpath= "/html/body/rxc-app/rxc-pharmacies/main/div/p-table/div/div[2]/table/tbody/tr";
	@FindBy(xpath = pharmacyDirectoryFilterSearchResultXpath)
	private List<WebElement> listFilterSearchresult;
	
	@FindBy(xpath = "//rxc-pharmacies/main/div/p-table/div/div[1]/rxc-table-header/div/span")
	private WebElement labelRecordFound;
	
	@FindBy(xpath = "//p-columnfilterformelement//li/span[contains(@class, 'p-autocomplete-token-icon')]")	// GC (06/23/22) corrected/simplified xpath
	private WebElement selectedFilter;
	
	@FindBy(xpath = "//p-columnfilterformelement/div/p-autocomplete/span/div/ul/li[1]")
	private WebElement filterList;
	
	public PharmacyDirectoryPO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
    }
    
    @Override
    public void waitForPageObjectToLoad() {
       SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, textboxPharmacyDirectorySearch);
    }
    
    /**
     * Retrieve the data of the pharmacy directory
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public List<Pharmacy> retrievePharmacyDirectoryInfo() throws ElementNotFoundException
    {
        List<Pharmacy> list = new ArrayList<>();
        logger.info("***Inside retrievePharmacyDirectoryInfo **** " + list);

        for ( WebElement row : rowPharmacyDirectoryRecord )
        {
            Pharmacy pharmacyDirectory = new Pharmacy();

            // GC (06/23/22) corrected relative xpath for each columns
            pharmacyDirectory.setNpi(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[1]")));
            pharmacyDirectory.setStoreName(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[2]")));
            pharmacyDirectory.setTaxonomyDescr(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[3]")));
            pharmacyDirectory.setPhoneNumber(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[4]")));
            pharmacyDirectory.setCity(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[5]")));
            pharmacyDirectory.setState(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[6]")));
            pharmacyDirectory.setStatusInd(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath("./td[7]")));

            list.add(pharmacyDirectory);
        }

        return list;
    }
    
    /**
     * Retrieve all the columns from pharmacy directory
     * @return list of columns
     */
    public List<String> retrieveColumnHeaders()
    {
    	return SeleniumPageHelperAndWaiter.retrieveWebElementsText(columnHeaders);
    }
    
    /**
     * method used for pharmacy directory search box
     * Search value can be Type value returned from the first row
     * @throws ElementNotFoundException if issue occurs
     */
    public void pharmacyDirectorySearch(String searchValue) throws ElementNotFoundException 
    {
        logger.info("***Pass the search value**** " + searchValue);
        SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxPharmacyDirectorySearch, searchValue);	// GC (06/23/22) simplified logic
        SeleniumPageHelperAndWaiter.pause(1000);
    }
        
    /**
     * This method will sort a column in certain order, i.e clicks the icon sort to make it asc or desc
     * 
     * @param column
     * @param order
     * @throws ElementNotFoundException
     */
    public void sortColumn(PharmacyDirectoryColumns column, SortOrder order) throws ElementNotFoundException
    {
        switch (column) {

        case NPI:
            sortColumn(iconNPISort, order);
            break;

        case PHARMACY_NAME:
            sortColumn(iconPharmacyNameSort, order);
            break;

        case TAXONOMY_DESCRIPTION:
            sortColumn(iconTaxonomySort, order);
            break;   

        case PHONE_NUMBER:
            sortColumn(iconPhoneNumSort, order);
            break; 

        case CITY:
            sortColumn(iconCitySort, order);
            break;

        case STATE:
            sortColumn(iconStateSort, order);
            break;

        case STATUS:
            sortColumn(iconStatusSort, order);
            break;    

        default:
            break;
        }
        SeleniumPageHelperAndWaiter.pause(1000);   // to avoid stale element exception 
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
    public List<String> retrieveColumnData(PharmacyDirectoryColumns column)
    {
        switch (column) {
        case NPI:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelPharmacyNPI);

        case PHARMACY_NAME:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelPharmacyName);

        case TAXONOMY_DESCRIPTION:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelPharmacyTaxonomyDescription);    

        case PHONE_NUMBER:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelPhoneNumber); 

        case CITY:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelPharmacyCity); 

        case STATE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelPharmacyState);

        case STATUS:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelPharmacyStatus); 

        default:
            break;
        }
        SeleniumPageHelperAndWaiter.pause(500);   // to avoid stale element exception 
        return null;
    }
	
	
    /**
     * Filter for the Global Pharmacy Directory
     * Select the filter by passing the filter name
     * @param enum PharmacyDirectoryColumns column
     * @throws ElementNotFoundException if issue occurs
     */
    public void iconFilterPharmacyDirectory(PharmacyDirectoryColumns filterName) throws ElementNotFoundException {
    	
    	// GC (06/23/22) modified to use enum PharmacyDirectoryColumns
    	
    	switch (filterName) {
		
    	case NPI:
    		SeleniumPageHelperAndWaiter.clickWebElement(driver, iconNPIFilter);
			break;
			
    	 case PHARMACY_NAME:
    		 SeleniumPageHelperAndWaiter.clickWebElement(driver, iconPharmacyNameFilter);
    		 break;

         case TAXONOMY_DESCRIPTION:
        	 SeleniumPageHelperAndWaiter.clickWebElement(driver, iconTaxDecFilter);
        	 break;

         case PHONE_NUMBER:
        	 SeleniumPageHelperAndWaiter.clickWebElement(driver, iconPhoneNumberFilter);
        	 break;

         case CITY:
        	 SeleniumPageHelperAndWaiter.clickWebElement(driver, iconCityFilter);
        	 break;

         case STATE:
        	 SeleniumPageHelperAndWaiter.clickWebElement(driver, iconStateFilter);
        	 break;

         case STATUS:
        	 SeleniumPageHelperAndWaiter.clickWebElement(driver, iconStatusFilter);
        	 break;

		default:
			break;
		}

    	SeleniumPageHelperAndWaiter.pause(500);
    }
    
    
    
    /**
     * Select the filter column to enter the filter value
     * @param enum PharmacyDirectoryColumns column
     * @throws ElementNotFoundException if issue occurs
     */
    public void clickTextBoxFilterPharmacyDirectory(PharmacyDirectoryColumns column) throws ElementNotFoundException {
    	
    	// GC (06/23/22) modified to use enum PharmacyDirectoryColumns
    	
    	switch (column) {
		
    	case NPI:
			SeleniumPageHelperAndWaiter.clickWebElement(driver, enterNPIFilter);
			break;
			
    	 case PHARMACY_NAME:
    		 SeleniumPageHelperAndWaiter.clickWebElement(driver, enterPharmacyNameFilter);
    		 break;

         case TAXONOMY_DESCRIPTION:
        	 SeleniumPageHelperAndWaiter.clickWebElement(driver, enterTaxonomyDescFilter);
        	 break;

         case PHONE_NUMBER:
        	 SeleniumPageHelperAndWaiter.clickWebElement(driver, enterPhoneNumberFilter);
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

		default:
			break;
		}
    	
        SeleniumPageHelperAndWaiter.pause(500);
    }
	
    /**
     * Search for the pharmacy directory columns
     * Set the field filter textbox WebElement
     *
     * @param filter value to set the textbox
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterInputSearchBoxPharmacyDirectory(String pharmacyDirectoryFilterSearchTerm) throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, inputFilterSearch, pharmacyDirectoryFilterSearchTerm, 2);      
        SeleniumPageHelperAndWaiter.pause(1000);

    }
	
    /**
     * Filter the Pharmacy directory columns
     * 
     * Select the first row from the search result
     * @throws ElementNotFoundException if something goes wrong
     * @deprecated GC (06/23/22) this is not used and the logic doesn't make sense
     * Need to look at this function
     */
    @Deprecated
    public String clickFirsPharmacyDirectoryColumnFilterResult(String filterValue) throws ElementNotFoundException {

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
    public String filterPharmacyDirectoryRecordCount() throws ElementNotFoundException {
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
    public void filterClearPharmacyDirectory() throws ElementNotFoundException {
    	// GC (06/23/22) simplified logic
    	SeleniumPageHelperAndWaiter.clickWebElement(this, selectedFilter);
    	SeleniumPageHelperAndWaiter.pause(500);	// give time for the UI to reload the pharmacy table
    }
    
    
    public boolean isFilterIconDisplayed(String filterName) throws ElementNotFoundException {
    	// GC (06/23/22) simplified logic
        return SeleniumPageHelperAndWaiter.isWebElementVisible(driver, selectedFilter, 3); 
    }

	/**
	 * Click the Pharmacy Directory icon
	 * 
	 * @throws ElementNotFoundException
	 */
	public void clickPharmacyDirectory() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.clickWebElement(this, iconPharmacyDir);
		waitForPageObjectToLoad();
	}
		
	public void clickPharmacyDirectorySearch() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.clickWebElement(this, textboxPharmacyDirectorySearch);	// GC (06/23/22) using WebElement instead of By locator
	}
	
}

