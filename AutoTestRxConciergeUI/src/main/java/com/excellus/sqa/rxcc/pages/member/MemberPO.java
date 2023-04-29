/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.member;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.member.MemberTabMenu;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.spring.BeanLoader;

/**
 * Handles the member page functionalities below
 * - hide member demographics
 * - select menu
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 09/06/2022
 */
public class MemberPO extends AbstractCommonPage 
{
	
	private static final Logger logger = LoggerFactory.getLogger(MemberPO.class);
	
	@FindBy(xpath = "//rxc-members")
	private WebElement sectionMember;
	
	@FindBy(xpath = "//rxc-members//aside/i")
	private WebElement iconHideMemberDemographics;
	
	private String memberTabMenuXpath = "//rxc-members//rxc-member-details/p-tabmenu//span[text()='%s']";
	
	/**
	 * Constructor
	 * @param driver {@link WebDriver}
	 */
	public MemberPO(WebDriver driver) {
		super(driver, BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
		waitForPageObjectToLoad();
	}
	
	
	/**
	 * Constructor
	 * @param driver {@link WebDriver}
	 * @param page {@link PageConfiguration}
	 */
	public MemberPO(WebDriver driver, PageConfiguration page) {
		super(driver, page);
		waitForPageObjectToLoad();
	}

	@Override
	public void waitForPageObjectToLoad() {
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, sectionMember);
	}
	
	/**
	 * Hides or expose Member Demographics
	 * @param hide set to true to hide Member Demographics otherwise false
	 * @throws ElementNotFoundException 
	 */
	public void hideMemberDemographics(boolean hide) throws ElementNotFoundException
	{
		String attr = SeleniumPageHelperAndWaiter.retrieveAttribute(this, iconHideMemberDemographics, "class");
		
		if ( ( hide && !StringUtils.endsWithIgnoreCase(attr, "rotate-180 transform") ) ||	// hide Member Demographics
				( !hide && StringUtils.endsWithIgnoreCase(attr, "rotate-180 transform") ) )	// expose Member Demographics
		{
			SeleniumPageHelperAndWaiter.clickWebElement(this, iconHideMemberDemographics);
			SeleniumPageHelperAndWaiter.pause(200);
		}
	}

	/**
	 * Determine if the member tab menu is present
	 * 
	 * @param tabMenu {@link MemberTabMenu}
	 * @return true if the tab menu is present and visible
	 * @throws ElementNotFoundException if issues occurs
	 */
	public boolean isMemberTabMenuPresent(MemberTabMenu tabMenu) throws ElementNotFoundException
	{
		By menu = By.xpath( String.format(memberTabMenuXpath, tabMenu.toString()) );
		
		return SeleniumPageHelperAndWaiter.isWebElementVisible(this, menu);
	}
	
	/**
	 * Click the member menu tab.
	 * 
	 * NOTE: This will throw runtime exception if the menu does not exists
	 * 
	 * @param tabMenu {@link MemberTabMenu}
	 */
	public void clickTabMenu(MemberTabMenu tabMenu)
	{
		logger.info("Selecting tab menu " + tabMenu.toString());
		By menu = By.xpath( String.format(memberTabMenuXpath, tabMenu.toString()) );
		SeleniumPageHelperAndWaiter.clickBy(this , menu);
		SeleniumPageHelperAndWaiter.pause(500);
	}
}

