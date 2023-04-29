/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.member;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.IPage;

/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 03/05/2022
 */
public class MemberDetailsPharmacyPO extends AbstractCommonPage implements IPage {

	public MemberDetailsPharmacyPO(WebDriver driver, PageConfiguration page) {
		super(driver, page);
	}

	private static final Logger logger = LoggerFactory.getLogger(MemberDetailsNotesPO.class);
	
	//Declared Variables
	
	@FindBy(xpath = "//span[1][contains(text(),'Pharmacies')]")
	private WebElement labelPharmacyTab;
	
	@FindBy(xpath = "//span[1][contains(text(),'Prescription Claims')]")
	private WebElement labelClaimsTab;
	
	@FindBy(xpath = "//rxc-member-pharmacies-table[1]/p-table[1]/div[1]/div[1]/div[1]/p")
	private WebElement textNumberOfRecords;
	
//	private final String retrievePharmacyRecord ="//rxc-member-pharmacies-table[1]/p-table[1]/div[1]/div[1]/div[1]/p";
	
	@FindBy(xpath = "//rxc-member-Pharmacies-table//p-table//span//input")
	private WebElement inputPharmacySearch;
	
//	private final String textboxClickPharmacySearch = "//rxc-member-Pharmacies-table//p-table//span//input";
	
	@FindBy(xpath = "//div//div[1]//button[1]")
	private WebElement buttonClearFilters;

	@FindBy(xpath = "//rxc-member-Pharmacies-table//div[1]//button[2]//span")
	private WebElement buttonCollapseAll;

	@FindBy(xpath = "//rxc-member-Pharmacies-table//div[1]//button[3]//span")
	private WebElement buttonExpandAll;

	@FindBy(xpath = "//td[contains(text(),'Fax: ')]")
	private WebElement tablePharmacyExpanded;	
	
	@FindBy(xpath = "//tbody")
	private List<WebElement> textNoRecords;

	
	@Override
	public void waitForPageObjectToLoad() {
		logger.debug("Waiting for member search textbox to load");
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, inputPharmacySearch);
	}
	
	/**
	 * Click the Pharmacy tab under Member Details
	 * 
	 * @throws ElementNotFoundException
	 */
	public void clickPharmacy() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.clickWebElement(this, labelPharmacyTab);
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
	 * Click Expand All under Pharmacy Tab
	 * 
	 * @throws ElementNotFoundException
	 */
	public void clickExpandAll() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, buttonExpandAll, 5);
	}
	
	/**
	 * Click Collapse All under Pharmacy Tab
	 * 
	 * @throws ElementNotFoundException
	 */
	public void clickCollapseAll() throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, buttonCollapseAll, 5);
	}
	
	/**
	 * Search for Pharmacy
	 * 
	 * @param Pharmacy search information
	 * @return Results from search
	 * @throws ElementNotFoundException 
	 */
	public String searchPharmacy(String Pharmacy) throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.clickWebElement(driver, inputPharmacySearch);
		SeleniumPageHelperAndWaiter.enterTextByWebElement(driver, inputPharmacySearch, Pharmacy);
		return retrieveSearchResult();
	}

	/**
	 * Retrieve search results for Pharmacy Search
	 * @return list of search results
	 * 
	 * @throws ElementNotFoundException
	 */

	public String retrieveSearchResult() throws ElementNotFoundException {
		List<String> listSearch= retrieveSearchresults();
		if (listSearch.size()>0) {
			return listSearch.get(0);
		}
		return null;
	}
	/**
	 * Retrieve text from pharmacy search result table
	 * @return WebElement text
	 * @throws ElementNotFoundException
	 */
	public List <String> retrieveSearchresults() throws ElementNotFoundException{
		return SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, textNoRecords);
	}
	
	/**
	 * Check to see if Claims Visible after Pharmacy Expanded
	 * @return 
	 * @return true if visible
	 * @throws ElementNotFoundException
	 */
	public boolean checkClaimsVisible() {
		boolean display=false;
		try {
			boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, tablePharmacyExpanded, 5);
			if ( displayed )
				display=true;
			
		}
		catch (Exception e)
		{
		}
		return display;
	}
}


