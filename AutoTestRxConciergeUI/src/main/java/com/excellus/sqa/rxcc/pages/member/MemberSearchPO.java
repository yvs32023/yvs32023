/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
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

import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;

/**
 * Handles member search
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/21/2022
 */
public class MemberSearchPO extends AbstractCommonPage {

	private static final Logger logger = LoggerFactory.getLogger(MemberSearchPO.class);

	private final String GLOBAL_SEARCH_PLACEHOLDER  = "Search by name, id, or date of birth...";

	@FindBy(xpath = "//input[@role='searchbox']")
	private WebElement textboxMemberSearch;

	private final String memberSearchResultXpath = "//rxc-search-result";	// GC (02/22/22) changed xpath base on UI changes

	private By memberSearchResult = By.xpath(memberSearchResultXpath);

	@FindBy(xpath = memberSearchResultXpath)
	private List<WebElement> rowMemberSearchResults;

	// These represent the fields in the search result
	private By labelName            = By.xpath(".//p[@data-testid='nameElement']");
	private By labelSubscriberId    = By.xpath(".//p[@data-testid='subscriberElement']");
	private By labelDOB             = By.xpath(".//p[@data-testid='dateOfBirthElement']");
	private By labelTenant          = By.xpath(".//p[@data-testid='tenantNameElement']");
	private By labelGroup           = By.xpath(".//p[@data-testid='groupNameElement']");

	/**
	 * Constructor
	 *
	 * @param driver WebDriver for PageObject
	 * @param page   PageConfiguration for the UI page
	 */
	public MemberSearchPO(WebDriver driver, PageConfiguration page) {
		super(driver, page);
		waitForPageObjectToLoad();
	}

	@Override
	public void waitForPageObjectToLoad() {
		logger.debug("Waiting for member search textbox to load");
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, textboxMemberSearch);
	}

	/**
	 * Set the field MemberSearch textbox WebElement
	 *
	 * @param memberSearchTerm memberSearchTerm to set the textbox
	 * @throws ElementNotFoundException if issue occurs
	 */
	public void setMemberSearch(String memberSearchTerm) throws ElementNotFoundException {
		SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxMemberSearch, memberSearchTerm);
	}

	/**
	 * Retrieve the placeholder of the search textbox
	 *
	 * @return the value of placeholder attribute of textboxMemberSearch
	 * @throws ElementNotFoundException
	 */
	public String retrievePlaceholder() throws ElementNotFoundException {
		return SeleniumPageHelperAndWaiter.retrieveAttribute(this, textboxMemberSearch, "placeholder");
	}

	/**
	 * Click the member from the search result provided the {@link Member}
	 * @param member
	 * @throws ElementNotFoundException
	 */
	public void clickMemberResult(Member member) throws ElementNotFoundException
	{
		clickMemberResult(member.getFirstLastName(), member.getSubscriberId(), member.getDateBirth(), member.getTenantName(), member.getGroupName());
	}

	/**
	 * Click on the first member from the search result that matches first/last name, subscriber id, DOB,
	 * group name and if tenant name if not null
	 *
	 * @param firstLastName of the member
	 * @param subscriberId of the member
	 * @param dob of the member
	 * @param tenantName the member belongs to
	 * @param groupName of the member
	 * @throws ElementNotFoundException is something goes wrong
	 */
	public void clickMemberResult(String firstLastName, String subscriberId, String dob, String tenantName, String groupName) throws ElementNotFoundException {

		SeleniumPageHelperAndWaiter.waitForNumberOfElementsToBeMoreThan(this, memberSearchResult, 0, 5);

		for ( WebElement row : this.rowMemberSearchResults)
		{
			String theFirstLastName = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, this.labelName);
			String theSubscriberId =  SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, this.labelSubscriberId);
			String theDOB = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, this.labelDOB);
			String theTenantName = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, this.labelTenant);
			String theGroupName = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, this.labelGroup);

			// Match the member to click
			if (StringUtils.equals(firstLastName, theFirstLastName) && StringUtils.equals(subscriberId, theSubscriberId) &&
				StringUtils.equals(dob, theDOB) && StringUtils.equals(groupName, theGroupName) )
			{
				if ( (StringUtils.isNotBlank(tenantName) && StringUtils.equals(tenantName, theTenantName)) || // tenant is not defined in JSON schema
						     StringUtils.isBlank(tenantName)) {

					SeleniumPageHelperAndWaiter.clickWebElement(this, row);
					new MemberDemographicPO(driver, pageConfiguration);

					return;
				}
			}
		}

		throw new ElementNotFoundException(String.format("The member [%s; %s; %s; %s; %s] was not found in the search result",
				firstLastName, subscriberId, dob, tenantName, groupName));
	}
	
	/**
	 * Click the first member from the search result
	 * 
	 * @author gcosmian
	 * @since 02/22/22
	 * @throws ElementNotFoundException is something goes wrong
	 */
	public void clickFirstMemberResult() throws ElementNotFoundException
	{
		SeleniumPageHelperAndWaiter.waitForNumberOfElementsToBeMoreThan(this, memberSearchResult, 0, 5);
		
		SeleniumPageHelperAndWaiter.clickWebElement(this, this.rowMemberSearchResults.get(0));
		new MemberDemographicPO(driver, pageConfiguration);
	}
	

	/**
	 * Retrieve the member search result
	 *
	 * @return
	 * @throws ElementNotFoundException
	 */
	public List<Member> retrieveMemberSearchResult() throws ElementNotFoundException {

		SeleniumPageHelperAndWaiter.waitForNumberOfElementsToBeMoreThan(this, memberSearchResult, 0, 5);

		List<Member> members = new ArrayList<Member>();

		String searchPlaceholder = this.retrievePlaceholder();  // get the search placeholder to determine if the search is performed as global

		boolean globalSearch = false;
		if ( StringUtils.equalsIgnoreCase(GLOBAL_SEARCH_PLACEHOLDER, searchPlaceholder) ) {
			globalSearch = true;
		}

		for ( WebElement row : this.rowMemberSearchResults)
		{
			Member member = new Member();
			member.setFirstLastName( SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, this.labelName) );
			member.setSubscriberId( SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, this.labelSubscriberId) );
			member.setDateBirth( SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, this.labelDOB) );
			member.setGroupName( SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, this.labelGroup) );

			if ( globalSearch ) {   // grab the tenant name if global search was performed
				member.setTenantName( SeleniumPageHelperAndWaiter.retrieveWebElementText(this, row, this.labelTenant) );
			}

			members.add(member);
		}

		return members;
	}

}
