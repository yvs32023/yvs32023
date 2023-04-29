/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.member;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.MemberRxclaim;
import com.excellus.sqa.rxcc.dto.member.PrescriptionClaimColumns;
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
 * @since 02/24/2022
 */

public class MemberPrescriptionClaimPO extends AbstractCommonPage implements IPage {

    private static final Logger logger = LoggerFactory.getLogger(MemberPrescriptionClaimPO.class);

    @FindBy(xpath = "//html/body/rxc-app/rxc-members/rxc-tenant-content/main/section[2]/rxc-member-details/p-tabmenu/div/ul/li[2]/a/span[1]")   //"//*[@role='tab']/span[text()='Prescription Claims'] "   ///html/body/rxc-app/rxc-members/rxc-tenant-content/main/section[2]/rxc-member-details/p-tabmenu/div/ul/li[2]/a/span[1]
    private WebElement headerPrescriptionClaims;

    @FindBy(xpath = "//p-sorticon[@field='adjudicationDate']")
    private WebElement iconDateSort;

    @FindBy(xpath = "//p-sorticon[@field='drugName']")
    private WebElement iconDrugNameSort;

    @FindBy(xpath = "//p-sorticon[@field='productStrength']")
    private WebElement iconProductStrengthSort;

    @FindBy(xpath = "//p-sorticon[@field='dosageForm']")
    private WebElement iconDosageFormSort;

    @FindBy(xpath = "//p-sorticon[@field='providerFullName']")
    private WebElement iconProviderFullNameSort;

    @FindBy(xpath = "//p-sorticon[@field='pharmacyName']")
    private WebElement iconPharmacyNameSort;

    @FindBy(xpath = "//p-sorticon[@field='quantityDispensed']")
    private WebElement iconQuantityPrescribedSort;

    @FindBy(xpath = "//p-sorticon[@field='daySupply']")
    private WebElement iconDaySupplySort;

    @FindBy(xpath = "//p-sorticon[@field='claimStatusDescr']")
    private WebElement iconClaimStatusDescrSort;

    @FindBy(xpath = "//p-sorticon[@field='transactionIdOrg']")
    private WebElement icontransactionIdOrgSort;

    //Below xpaths are for the Prescription Claims

    //Drug Name
    @FindBy(xpath = "//p-columnfilter[@field='drugName']")
    private WebElement iconFilterDrugName;

    @FindBy(xpath = "//p-columnfilterformelement/*/div[.='Select Drugs']")
    private WebElement dropdownFilterDrugName;

    //Drug Strength
    @FindBy(xpath = "//p-columnfilter[@field='productStrength']")
    private WebElement iconFilterStrength;

    @FindBy(xpath = "//p-columnfilterformelement/*/div[.='Select Strengths']")
    private WebElement dropdownFilterStrength;

    //@ntagore  1/24/2023
    //Dosage Form
    @FindBy(xpath = "//p-columnfilter[@field='dosageForm']")
    private WebElement iconFilterDosageForm;

    @FindBy(xpath = "//p-columnfilterformelement/p-multiselect[@placeholder='Select Dosage Forms']")
    private WebElement dropdownFilterDosageForm;

    //Provider Name
    @FindBy(xpath = "//p-columnfilter[@field='providerFullName']")
    private WebElement iconFilterProviderName;

    @FindBy(xpath = "//p-columnfilterformelement/*/div[.='Select Providers']")
    private WebElement dropdownFilterProviderName;

    //Pharmacy Name
    @FindBy(xpath = "//p-columnfilter[@field='pharmacyName']")
    private WebElement iconFilterPharmacyName;

    @FindBy(xpath = "//p-columnfilterformelement/*/div[.='Select Pharmacies']")
    private WebElement dropdownFilterPharmacyName;

    //Quantity
    @FindBy(xpath = "//p-columnfilter[@field='quantityDispensed']")
    private WebElement iconFilterQuantity;

    @FindBy(xpath = "//p-columnfilterformelement/*/div[.='Select Quantities']")
    private WebElement dropdownFilterQuantity;

    //Day Supply
    @FindBy(xpath = "//p-columnfilter[@field='daySupply']")
    private WebElement iconFilterDaySupply;

    @FindBy(xpath = "//p-columnfilterformelement/*/div[.='Select Supplies']")
    private WebElement dropdownFilterDaySupply;

    //Statuses
    @FindBy(xpath = "//p-columnfilter[@field='claimStatusCode']")
    private WebElement iconFilterStatus;

    //Status Value
    @FindBy(xpath = "//p-multiselectitem/li")
    private List<WebElement> iconFilterStatusResult;

    @FindBy(xpath = "//p-columnfilterformelement/*/div[contains(.,'Select Statuses')]")
    private WebElement dropdownFilterStatus;

    //Claim ID
    @FindBy(xpath = "//p-columnfilter[@field='transactionIdOrg']")
    private WebElement iconFilterClaimId;

    @FindBy(xpath = "//p-columnfilterformelement/*/div[.='Select Claim IDs']")
    private WebElement dropdownFilterClaimId;

    //Common webElements for filter

    @FindBy(xpath = "//p-columnfilterformelement/p-multiselect/*/div/*/div/input[@role='textbox']")
    private WebElement textboxMemberClaimsFilterDropDown; 

    private final String memberClaimsFilterSearchResultXpath = "//p-columnfilterformelement/p-multiselect/div/div[4]/div[2]/ul/p-multiselectitem";

    @FindBy(xpath = memberClaimsFilterSearchResultXpath)
    private List<WebElement> rowMemberClaimsSearchResults;

    @FindBy(xpath = "//p-columnfilterformelement/p-multiselect/*/div/*/button[@type='button']")
    private WebElement buttonFilterDropDownExit; 

    @FindBy(xpath = "//rxc-member-rxclaims-table[1]/p-table[1]/div[1]/div[1]/div[1]/p[contains(text(),'Records Found')]")
    private WebElement labelFilterRecordCount; 

    @FindBy(xpath = "//rxc-member-rxclaims-table[1]/p-table[1]/div[1]/div[1]/div[1]/div[1]/button[1]/span[contains(.,'Clear')]")
    private WebElement buttonClear;  

    private final String memberClaimsFilterSearchCountXpath = "//rxc-member-rxclaims-table/p-table/div/div[2]/table/tbody/tr";

    @FindBy(xpath = memberClaimsFilterSearchCountXpath)
    private List<WebElement> rowMemberClaimsSearchCount;

    //DAW WebElement
    private final String labelMemberClaimsDAWXPath = "//rxc-member-rxclaims-table/p-table/div/div[2]/table/tbody/tr[2]/td[5]/span"; 

    @FindBy(xpath = labelMemberClaimsDAWXPath)
    private WebElement labelMemberClaimsDAW;

    private final String buttonMemberClaimsExpand = "//rxc-member-rxclaims-table/p-table/div/div[2]/table/tbody/tr[1]/td[1]/button";


    @FindBy(xpath = "//rxc-member-rxclaims-table//tr")
    private List<WebElement> labelMemberClaimRecord; 


    private final String retrieveClaimLabelRecord ="//rxc-member-rxclaims-table[1]/p-table[1]/div[1]/div[1]/div[1]/p";
    /*
     * added 
     * ntagore 01/09/23
     */

    @FindBy(xpath = "//rxc-member-rxclaims-table//input[@placeholder='Search keyword']")
    private WebElement textboxClaimSearch;

    private final String textboxClickClaimSearch ="//rxc-member-rxclaims-table//input[@placeholder='Search keyword']";

    //DTO Check

    @FindBy(xpath = "//rxc-member-rxclaims-table/p-table/div/div[2]/table/tbody/tr/td[2]")
    private List<WebElement> labelDate;

    @FindBy(xpath = "//rxc-member-rxclaims-table/p-table/div/div[2]/table/tbody/tr/td[3]")
    private List<WebElement> labelDrugName;

    @FindBy(xpath = "//rxc-member-rxclaims-table/p-table/div/div[2]/table/tbody/tr/td[4]")
    private List<WebElement> labelDrugStrength;

    @FindBy(xpath = "//rxc-member-rxclaims-table/p-table/div/div[2]/table/tbody/tr/td[5]")
    private List<WebElement> labelDosageForm;

    @FindBy(xpath = "//rxc-member-rxclaims-table/p-table/div/div[2]/table/tbody/tr/td[6]")
    private List<WebElement> labelProviderNPI;

    @FindBy(xpath = "//rxc-member-rxclaims-table/p-table/div/div[2]/table/tbody/tr/td[7]")
    private List<WebElement> labelPharmacyNPI;

    @FindBy(xpath = "//rxc-member-rxclaims-table/p-table/div/div[2]/table/tbody/tr/td[8]")
    private List<WebElement> labelQuantity;

    @FindBy(xpath = "//rxc-member-rxclaims-table/p-table/div/div[2]/table/tbody/tr/td[9]")
    private List<WebElement> labelDaySupply;

    @FindBy(xpath = "//rxc-member-rxclaims-table/p-table/div/div[2]/table/tbody/tr/td[10]")
    private List<WebElement> labelStatus;

    @FindBy(xpath = "//rxc-member-rxclaims-table/p-table/div/div[2]/table/tbody/tr/td[11]")
    private List<WebElement> labelClaimId;
    
    private String searchColumnValue = "//rxc-member-rxclaims-table/p-table/div/div[2]/table/tbody/tr/td[%d]";

    final String SECTION_PAGE_XPATH = "//rxc-member-rxclaims-table";

    @FindBy(xpath = SECTION_PAGE_XPATH + "//table//tbody/tr")
    private List<WebElement> rowMemberClaimRecord;

    @FindBy(xpath = "//rxc-member-rxclaims-table//table//thead/tr/th")
    private List<WebElement> columnNames;

    //Added line for new method
    static List<MemberRxclaim> memberRxClaimsSearch;
    List<MemberRxclaim> expectedClaims;

    /**
     * Constructor
     *
     * @param driver WebDriver for PageObject
     * @param page   PageConfiguration for the UI page
     */


    public MemberPrescriptionClaimPO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
        waitForPageObjectToLoad();
    }

    @Override
    public void waitForPageObjectToLoad() {
        logger.debug("Waiting for member detail page to load");

    }

    /**
     * select the Member Prescription Claim Tab
     * @return 
     * @throws ElementNotFoundException if issue occurs
     */

    public void clickheaderPrescriptionClaims() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, headerPrescriptionClaims);

    }
    /**
     * Sort for the Member Prescription claim
     * @throws ElementNotFoundException if issue occurs
     */
    public void memberClaimsSort() {
        try {
            boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, headerPrescriptionClaims, 5);
            if ( displayed )

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconDateSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconDateSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconDrugNameSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconDrugNameSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconProductStrengthSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconProductStrengthSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconProviderFullNameSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconProviderFullNameSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconPharmacyNameSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconPharmacyNameSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconQuantityPrescribedSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconQuantityPrescribedSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconDaySupplySort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconDaySupplySort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconClaimStatusDescrSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconClaimStatusDescrSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, icontransactionIdOrgSort);

            SeleniumPageHelperAndWaiter.clickWebElement(driver, icontransactionIdOrgSort);

        }
        catch (Exception e)
        {
            logger.error("Member Claims element not found");

        }
    }

    /**
     * This method will sort a column in certain order, i.e clicks the icon sort to make it asc or desc
     * 
     * @param column
     * @param order
     * @throws ElementNotFoundException
     */
    public void sortColumn(PrescriptionClaimColumns column, SortOrder order) throws ElementNotFoundException
    {
        switch (column) {

        case DATE:
            sortColumn(iconDateSort, order);
            break;

        case DRUG_NAME:
            sortColumn(iconDrugNameSort, order);
            break;

        case STRENGTH:
            sortColumn(iconProductStrengthSort, order);
            break;   

        case DOSAGE_FORM:
            sortColumn(iconDosageFormSort, order);
            break; 

        case PROVIDER_NPI:
            sortColumn(iconProviderFullNameSort, order);
            break;

        case PHARMACY_NPI:
            sortColumn(iconPharmacyNameSort, order);
            break;

        case QTY:
            sortColumn(iconQuantityPrescribedSort, order);
            break;

        case DS:
            sortColumn(iconDaySupplySort, order);
            break;

        case STATUS:
            sortColumn(iconClaimStatusDescrSort, order);
            break;

        case CLAIM_ID:
            sortColumn(icontransactionIdOrgSort, order);
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
    public List<String> retrieveColumnData(PrescriptionClaimColumns column)
    {
        switch (column) {
        case DATE:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelDate);

        case DRUG_NAME:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelDrugName);

        case STRENGTH:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelDrugStrength);    

        case DOSAGE_FORM:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelDosageForm); 


        case PROVIDER_NPI:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelProviderNPI); 

        case PHARMACY_NPI:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelPharmacyNPI);

        case QTY:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelQuantity);

        case DS:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelDaySupply); 

        case STATUS:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelStatus); 

        case CLAIM_ID:
            return SeleniumPageHelperAndWaiter.retrieveWebElementsText(labelClaimId);

        default:
            break;
        }

        return null;
    }

    /**
     * Retrieve the number of the Member records
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public int numberOfClaimRecords() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.pause(2000);
        int numberOfRecords = SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, labelMemberClaimRecord).size();

        return numberOfRecords;
    }

    /**
     * Retrieve the member claim record
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public String retrieveMemberClaimRecord() throws ElementNotFoundException {

        return (driver.findElement(By.xpath(retrieveClaimLabelRecord)).getText());


    }

    /**
     * Filter for the Member Prescription claim
     * Select the filter by passing the filter name
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterPrescriptionClaims(String filterName) throws ElementNotFoundException {
        if (filterName == "drugName") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconFilterDrugName);
        }else if (filterName == "strength") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconFilterStrength);
        }else if (filterName == "dosageForm") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconFilterDosageForm);  // added filter for Dosage Form @ntagore 01/24/23
        }else if (filterName == "providerName") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconFilterProviderName);
        }else if (filterName == "pharmacyName") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconFilterPharmacyName);
        }else if (filterName == "quantity") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconFilterQuantity);
        }else if (filterName == "daySupply") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconFilterDaySupply);
        }else if (filterName == "status") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconFilterStatus);
        }else if (filterName == "claimId") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, iconFilterClaimId);
        }
        SeleniumPageHelperAndWaiter.pause(1000);
    }

    /**
     * Filter for the Member Prescription claim
     * Select the filter dropdown
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterDropDownPrescriptionClaims(String filterName) throws ElementNotFoundException {
        if (filterName == "drugName") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownFilterDrugName);
        }else if (filterName == "strength") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownFilterStrength);
        }else if (filterName == "dosageForm") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownFilterDosageForm);  // added filter for Dosage Form @ntagore 01/24/23
        }else if (filterName == "providerName") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownFilterProviderName);
        }else if (filterName == "pharmacyName") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownFilterPharmacyName);
        }else if (filterName == "quantity") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownFilterQuantity);
        }else if (filterName == "daySupply") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownFilterDaySupply);
        }else if (filterName == "status") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownFilterStatus);
        }else if (filterName == "claimId") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, dropdownFilterClaimId);
        }    

        SeleniumPageHelperAndWaiter.pause(1000);
    }


    /**
     * Filter for the Member Prescription claim
     * Select the filter dropdown Text Box
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterDropDownSelectTextBoxPrescriptionClaims(String filterName) throws ElementNotFoundException {
        if (filterName == "drugName") {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, textboxMemberClaimsFilterDropDown);          
            SeleniumPageHelperAndWaiter.pause(1000);
        }
    } 

    /**
     * Filter for the Member Prescription claim
     * Set the field filter textbox WebElement
     *
     * @param filter value to set the textbox
     * @throws ElementNotFoundException if issue occurs
     */
    public void filterDropDownInputTextBoxPrescriptionClaims(String precriptionClaimFilterSearchTerm) throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, textboxMemberClaimsFilterDropDown, precriptionClaimFilterSearchTerm, 2);      
        SeleniumPageHelperAndWaiter.pause(1000);

    }

    /**
     * Filter for the Member Prescription claim
     * 
     * Select the first row from th search result
     * @throws ElementNotFoundException is something goes wrong
     */
    public String clickFirsPrescriptionClaimFilterResult(String filterValue) throws ElementNotFoundException {

        List<String> listDropDownValues= SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, rowMemberClaimsSearchResults);
        int listSize = listDropDownValues.size();
        logger.info("listDropDownValues size :" +listSize);
        logger.info("filterValue  :" +filterValue);

        if (listSize > 0) {

            for ( String listValue : listDropDownValues ) {

                if (listValue.equalsIgnoreCase(filterValue)) {
                    SeleniumPageHelperAndWaiter.clickWebElement(driver, rowMemberClaimsSearchResults.get(0));
                    break;
                }

            }

        }
        int rowCount=SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, rowMemberClaimsSearchCount).size();
        String filterRowCount = (rowCount) + " Records Found"; 
        SeleniumPageHelperAndWaiter.pause(1000);
        return filterRowCount;
    }
    /**
     * Filter for the Member Prescription claim
     * Record count WebElement
     *
     * @return no. of records returned
     * @throws ElementNotFoundException if issue occurs
     */

    public String filterDropDownRecordCountPrescriptionClaims() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, this.labelFilterRecordCount); 
    }


    /**
     * Filter for the Member Prescription claim
     * Clear the filter search WebElement
     *
     * 
     * @throws ElementNotFoundException if issue occurs
     */

    public void filterClearPrescriptionClaims() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(driver, buttonClear);  
    }

    /**
     * select the member Claim expand button
     * 
     */
    public void expandClaimIcon() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.pause(500);
        SeleniumPageHelperAndWaiter.clickBy(this, By.xpath(buttonMemberClaimsExpand));    
        SeleniumPageHelperAndWaiter.pause(1000);
    }


    public String memberClaimDAW() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.pause(500);
        SeleniumPageHelperAndWaiter.hoverOnElement(this, labelMemberClaimsDAW);
        SeleniumPageHelperAndWaiter.pause(500);
        return SeleniumPageHelperAndWaiter.retrieveAttribute(this, labelMemberClaimsDAW, "title");
    }



    /**
     * new added method
     * @ntagore 01/09/2023
     * method used for claim search box
     * Search value can be Type value returned from the first row
     * @throws ElementNotFoundException if issue occurs
     */

    public void claimSearch(String searchValue) throws ElementNotFoundException {
        try {
            logger.info("***Pass the search value**** " + searchValue);
            boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(this, By.xpath(textboxClickClaimSearch));
            logger.info("***is displayed **** " + displayed);
            if ( displayed )
                SeleniumPageHelperAndWaiter.clickBy(this, By.xpath(textboxClickClaimSearch));
            SeleniumPageHelperAndWaiter.pause(2000);
            SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxClaimSearch, searchValue);
            SeleniumPageHelperAndWaiter.pause(4000);
        }
        catch (Exception e)
        {
            logger.error("Claim Search element not found");

        }

    }

    /**
     * @author ntagore 01/30/23
     * Retrieve the text of the member claim
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public List<MemberRxclaim> retrieveMemberClaimInfo() throws ElementNotFoundException
    {
        List<MemberRxclaim> list = new ArrayList<>();
        logger.info("***Inside retrieveMemberClaimInfo **** " + list);

        for ( WebElement row : rowMemberClaimRecord )
        {
            MemberRxclaim memberClaim = new MemberRxclaim();
            //adjudication date

            String adjudicationDate = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[2]"));
            if ( StringUtils.isNotBlank(adjudicationDate) ){
                String dateYear = adjudicationDate.substring(6, 10);
                String dateMonth = adjudicationDate.substring(0, 2);
                String dateDay = adjudicationDate.substring(3, 5);

                adjudicationDate = dateMonth+"/"+dateDay+"/"+dateYear; 
               // adjudicationDate = dateYear+dateMonth+dateDay;
            }
            memberClaim.setAdjudicationDate(adjudicationDate);
            logger.info("***Inside setAdjudication Date **** " + SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[2]")));

            //DrugName        
            memberClaim.setDrugName(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[3]")));
            logger.info("***Inside setDrugName **** " + SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[3]")));

            //Strength
            memberClaim.setProductStrength(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[4]")));

            //DosageForm
            memberClaim.setDosageForm(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[5]")));

            //Provider LastName, FirstName and NPI
            String setProviderIdName = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[6]"));
            logger.info("***Inside setProviderIdName **** " + setProviderIdName);        
            String  setProviderFirstLastName = StringUtils.substringBeforeLast(setProviderIdName, "\n");        
            String setProviderNPI = StringUtils.substringAfterLast(setProviderIdName, "\n");
            logger.info("***Inside setProviderFirstLastName **** " + setProviderFirstLastName);
            memberClaim.setPrescriberFirstName(StringUtils.substringBefore(setProviderFirstLastName, " "));
            memberClaim.setPrescriberLastName(StringUtils.substringAfter(setProviderFirstLastName, " "));
            memberClaim.setPrescriberId(StringUtils.substringBetween(setProviderNPI, "(", ")"));     

            //Pharmacy FirstName, NPI
            String setPharmacyIdName = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[7]"));
            String setPharmacyName = StringUtils.substringBeforeLast(setPharmacyIdName, "\n");
            logger.info("***Inside setPharmacyName **** " + setPharmacyName);
            String setPharmacyNPI = StringUtils.substringAfterLast(setPharmacyIdName, " ");
            logger.info("***Inside setPharmacyNPI **** " + setPharmacyNPI);
            memberClaim.setServiceProviderName(setPharmacyName);
            memberClaim.setServiceProviderId(StringUtils.substringBetween(setPharmacyNPI, "(", ")" ));

            // Quantity
            memberClaim.setQuantityDispensed(Integer.parseInt(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[8]"))));
            //DaySupply
            memberClaim.setDaySupply(Integer.parseInt(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[9]"))));

            // Status
            memberClaim.setClaimStatusDescr(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[10]")));
            //Claim ID
            memberClaim.setTransactionIdOrg(SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, By.xpath(".//td[11]")));
            list.add(memberClaim);

        }
        return list;
    }

    /**
     * Retrieve all the columns
     * @return list of columns
     * @throws ElementNotFoundException
     */
    public List<String> retrieveColumns() throws ElementNotFoundException
    {
        List<String> columns = SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, columnNames);
        columns.remove(0);  // do not include the first entry which is blank
        return columns;
    }
    
    /**
     * Method to filter/search data by column name and column value. This is called in searchRXClaimsColumns
     * @return filtered data based on the criteria
     * @throws ElementNotFoundException
     */
   
    public List<DynamicNode> providerClaimSearchType(List<MemberRxclaim> memberRxClaimsSearch, String searchColumn, String searchValue) throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        // Cosmos
        SeleniumPageHelperAndWaiter.pause(3000);
        if (searchColumn.equalsIgnoreCase("dosage")){
            expectedClaims = memberRxClaimsSearch.stream()
                    .filter(claims -> claims.getDosageForm().equalsIgnoreCase(searchValue))
                    .collect(Collectors.toList());
        }else if (searchColumn.equalsIgnoreCase("drugName")) {      
            expectedClaims = memberRxClaimsSearch.stream()
                    .filter(claims -> claims.getDrugName().toLowerCase().contains(searchValue.toLowerCase()))
                    .collect(Collectors.toList());
        }else if (searchColumn.equalsIgnoreCase("providerName")) {      
            expectedClaims = memberRxClaimsSearch.stream()
                    .filter(claims -> claims.getPrescriberFirstName().toLowerCase().toLowerCase().contains(searchValue.toLowerCase()))
                    .collect(Collectors.toList());
        }else if (searchColumn.equalsIgnoreCase("pharmacyName")) {      
            expectedClaims = memberRxClaimsSearch.stream()
                    .filter(claims -> claims.getServiceProviderName().toLowerCase().contains(searchValue.toLowerCase()))
                    .collect(Collectors.toList());
        }else if (searchColumn.equalsIgnoreCase("quantity")) {      
            expectedClaims = memberRxClaimsSearch.stream()
                    .filter(claims -> (claims.getQuantityDispensed() == Integer.parseInt(searchValue)))
                    .collect(Collectors.toList());
        }else if (searchColumn.equalsIgnoreCase("daySupply")) {      
            expectedClaims = memberRxClaimsSearch.stream()
                    .filter(claims -> (claims.getDaySupply() == Integer.parseInt(searchValue)))
                    .collect(Collectors.toList());
        }else if (searchColumn.equalsIgnoreCase("strength")) {      
            expectedClaims = memberRxClaimsSearch.stream()
                    .filter(claims -> (claims.getProductStrength().contentEquals(searchValue)))
                    .collect(Collectors.toList());
        }else if (searchColumn.equalsIgnoreCase("date")) {      
            expectedClaims = memberRxClaimsSearch.stream()
                    .filter(claims -> (claims.getAdjudicationDate().contentEquals(searchValue)))
                    .collect(Collectors.toList());
        }else if (searchColumn.equalsIgnoreCase("claimId")) {      
            expectedClaims = memberRxClaimsSearch.stream()
                    .filter(claims -> (claims.getTransactionIdOrg().contentEquals(searchValue)))
                    .collect(Collectors.toList());
        }
        logger.info(" ******************* expectedClaims************" + expectedClaims);
        // UI
        claimSearch(searchValue);
        SeleniumPageHelperAndWaiter.pause(3000);
        List<MemberRxclaim> actualClaims = retrieveMemberClaimInfo();
        logger.info(" ******************* actualClaims************" + actualClaims);
        SeleniumPageHelperAndWaiter.pause(3000);
        for ( MemberRxclaim expected : expectedClaims )
        { 
            @SuppressWarnings("unused")
            boolean found = false; 
            for ( MemberRxclaim actual : actualClaims )
            {
                {
                   // if ( expected.getTransactionIdOrg().equals(actual.getTransactionIdOrg()))
                        if ( expected.getTransactionIdOrg().equals(actual.getTransactionIdOrg())
                                && expected.getClaimStatusDescr().equals(actual.getClaimStatusDescr()))
                    {
                        tests.addAll(expected.compareUI(actual));
                        found = true;
                        break;
                    }
                }
            }
        }
        return tests;
    }    
    
    /**
     * Retrieve and retrun column value of the first row of prescription claim
     * Paremeter Position is the column position in the UI
     * @throws ElementNotFoundException
     */
    
    public String getSearchValueByColumn(int position) throws ElementNotFoundException {
        
        String columnValue = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, By.xpath( String.format(searchColumnValue, position )),"column");
        
        if (position==6){ 
            String  setProviderFirstLastName = StringUtils.substringBeforeLast(columnValue, "\n");
            String  SetProviderFirstName = StringUtils.substringBefore(setProviderFirstLastName, " ");
            columnValue=SetProviderFirstName;
        }else if (position==7){ 
            String setPharmacyName = StringUtils.substringBeforeLast(columnValue, "\n"); 
            columnValue=setPharmacyName;            
        }
        
        return columnValue;
                
    }
    
    /**
     * Common method usd to search by column value
     * Parametes passed are orginal(retrieved in before all)  MemberRXclaim list, search by column name and search value
     * @throws ElementNotFoundException
     */

    public List<DynamicNode> searchRXClaimsColumns(List<MemberRxclaim> memberRxClaimsSearch, String searchColumn, String searchValue) throws ElementNotFoundException
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        tests = providerClaimSearchType(memberRxClaimsSearch, searchColumn, searchValue);
        return tests;
    }  


}       

