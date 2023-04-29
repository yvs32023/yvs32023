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

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.IPage;
import com.excellus.sqa.spring.BeanLoader;

/**
 * Page object that handles member legacy correspondence
 * 
 * @author  Garrett Cosmiano
 * @since 09/01/2022
 */
public class MemberLegacyCorrespondencePO extends AbstractCommonPage implements IPage {

	private static final Logger logger = LoggerFactory.getLogger(MemberLegacyCorrespondencePO.class);
	
	@FindBy(xpath = "//span[normalize-space()='Legacy Correspondence']")
	private WebElement headerLegacyCorrespondence;
	
	final String SECTION_PAGE_XPATH = "//rxc-member-legacy-correspondence";
	
	@FindBy(xpath = SECTION_PAGE_XPATH)
	private WebElement sectionLegacyCorrespondence;
	
	@FindBy(xpath = SECTION_PAGE_XPATH + "//p[contains(text(), 'Records')]")
	private WebElement labelRecords;
	
	@FindBy(xpath = SECTION_PAGE_XPATH + "//table//thead/tr/th")
	private List<WebElement> columnNames;
	
	@FindBy(xpath = SECTION_PAGE_XPATH + "//table//tbody/tr")
	private List<WebElement> rowRecords;
	
	private String rowRecordXpath = SECTION_PAGE_XPATH + "//table//tbody/tr[%d]";
	
	@FindBy(xpath = SECTION_PAGE_XPATH + "//table//tbody/tr/td/div")
	private WebElement labelContactComment;
	
	private final String COLUMN_INDEX_CALL_DATE_TIME	= "count(//tr/th[contains(.,'Call Date Time')]/preceding-sibling::th)+1";
	private final String COLUMN_INDEX_CALL_TYPE			= "count(//tr/th[contains(.,'Call Type')]/preceding-sibling::th)+1";
	private final String COLUMN_INDEX_CALL_OUTCOME		= "count(//tr/th[contains(.,'Call Outcome')]/preceding-sibling::th)+1";
	private final String COLUMN_INDEX_POINT_OF_CONTACT	= "count(//tr/th[contains(.,'Point of Contact')]/preceding-sibling::th)+1";
	private final String COLUMN_INDEX_CREATED_BY		= "count(//tr/th[contains(.,'Created By')]/preceding-sibling::th)+1";

	private By buttonExpand			= By.xpath(  "./td/button/span[contains(@class,'p-button-icon')]");
	private By labelCallDateTime	= By.xpath(  "./td[ " + COLUMN_INDEX_CALL_DATE_TIME + " ]");
	private By labelCallType		= By.xpath(  "./td[ " + COLUMN_INDEX_CALL_TYPE + " ]");
	private By labelCallOutcome		= By.xpath(  "./td[ " + COLUMN_INDEX_CALL_OUTCOME + " ]");
	private By labelPointOfContact	= By.xpath(  "./td[ " + COLUMN_INDEX_POINT_OF_CONTACT + " ]");
	private By labelCreatedBy		= By.xpath(  "./td[ " + COLUMN_INDEX_CREATED_BY + " ]");

	
	public MemberLegacyCorrespondencePO(WebDriver driver) {
		super(driver, BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
	}

	/**
	 * Constructor
	 *
	 * @param driver WebDriver for PageObject
	 * @param page   PageConfiguration for the UI page
	 */
	public MemberLegacyCorrespondencePO(WebDriver driver, PageConfiguration page) {
		super(driver, page);
	}

	@Override
	public void waitForPageObjectToLoad() {
		logger.debug("Waiting for member legacy correspondence to load");
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, sectionLegacyCorrespondence);
	}
	
	/**
	 * Retrieves the number of records displayed in the label
	 * @return number of records
	 * @throws ElementNotFoundException
	 */
	public int retrieveNumOfRecrods() throws ElementNotFoundException
	{
		String temp = SeleniumPageHelperAndWaiter.retrieveTextFromTextbox(this, labelRecords);
		temp = StringUtils.replace(temp, "Records Found", "").trim();
		
		try {
			return Integer.valueOf(temp);
		}
		catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * Retrieve all the legacy correspondence
	 * @return list of {@link MemberCorrespondence}
	 * @throws ElementNotFoundException if there are issues
	 */
	public List<MemberCorrespondence> retrieveLegacyCorrespondence() throws ElementNotFoundException
	{
		logger.debug("Retrieving legacy correspondences");
		
		List<MemberCorrespondence> records = new ArrayList<>();
		
		int numRows = rowRecords.size();
		
		for ( int index = 0; index < numRows;  index++ )
		{
			records.add( retrieveCorrespondence(index) );
		}
		
		return records;
	}
	
	/**
	 * Retrieves the correspondence base on the index of the row
	 * @param index first row starts with index 0 
	 * @return {@link MemberCorrespondence}
	 * @throws ElementNotFoundException if there are issues
	 */
	public MemberCorrespondence retrieveCorrespondence(int index) throws ElementNotFoundException
	{
		logger.info("Retrieving legacy correspondence data");
		
		WebElement row = driver.findElement( By.xpath( String.format(rowRecordXpath, index+1) ) );
		MemberCorrespondence record = new MemberCorrespondence();
		
		String temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, labelCallDateTime);
		record.setCorrespondenceDateTime(temp);
		
		temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, labelCallType);
		record.setCorrespondenceType(temp);
		
		temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, labelCallOutcome);
		record.setCorrespondenceOutcome(temp);
		
		temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, labelPointOfContact);
		record.setContactName(temp);
		
		temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, labelCreatedBy);
		record.setCreatedBy(temp);
		
		expandCorrespondence(row.findElement(buttonExpand), true);
		
		temp = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, driver.findElement( By.xpath( String.format(rowRecordXpath, index+2) ) ));
		record.setContactComment(temp);
		
		expandCorrespondence(row.findElement(buttonExpand), false);
		
		return record;
	}
	
	/**
	 * Expand/collapse a record to display contact comment
	 * @param iconExpand WebElement that represent the icon for expanding/collapsing
	 * @param expand true to expand it otherwise false
	 * @throws ElementNotFoundException if there are issues
	 */
	private void expandCorrespondence(WebElement iconExpand, boolean expand) throws ElementNotFoundException
	{
		logger.info("Expanding row");
		String attr = SeleniumPageHelperAndWaiter.retrieveAttribute(this, iconExpand, "class");
		
		if ( expand && StringUtils.endsWithIgnoreCase(attr, "-right") )
		{
			SeleniumPageHelperAndWaiter.clickWebElement(this, iconExpand);
			SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, labelContactComment);
		}
		else if ( !expand && StringUtils.endsWithIgnoreCase(attr, "-down") )
		{
			SeleniumPageHelperAndWaiter.clickWebElement(this, iconExpand);
			SeleniumPageHelperAndWaiter.pause(500);
		}
	}
	
	/**
	 * Retrieve all the columns
	 * 
	 * @return list of columns
	 * @throws ElementNotFoundException
	 */
	public List<String> retrieveColumns() throws ElementNotFoundException
	{
		List<String> columns = SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, columnNames);
		columns.remove(0);	// do not include the first entry which is blank
		return columns;
	}

}