/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.pageobject;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.ILogoutPage;

/**
 * Page object for logging out of Web Application
 * 
 * GC (03/09/22) moved this class from AutoTestRxConciergeUI module to AutoTestRxConciergeUtility module
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/21/2022
 */
public class LogoutPO extends AbstractCommonPage implements ILogoutPage
{

	private static final Logger logger = LoggerFactory.getLogger(LogoutPO.class);

	@FindBy(tagName = "rxc-user-menu")
	private WebElement iconUserMenu;

	@FindBy(xpath = "//rxc-user-menu//a/span[.='Logout']")
	private WebElement menuLogout;
	
	@FindBy(xpath = "//rxc-user-menu//p-menu//div//ul//li[1]//span")
	private WebElement labelUsername;
	
	@FindBy(xpath = "//div[@role='list']//img")
	private WebElement imgAccount;
	
	private String buttonEmailXpath = "//small[.='%s']";
	
	private String buttonNameXpath = "//div[.='%s']";

	private String emailAddress;
	
	@FindBy(xpath = "//p-toastitem//div[.='Search Error Occurred']/following-sibling::div[.='Whoops! An error occurred while performing your search.']")
	private WebElement alertWebApp;
	
	@FindBy(xpath = "//p-toastitem//div[@role='alert']/button")
	private WebElement buttonAlertExit;

	/**
	 * Constructor
	 *
	 * @param driver WebDriver for PageObject
	 * @param page   PageConfiguration for the UI page
	 */
	public LogoutPO(WebDriver driver, PageConfiguration page) {
		super(driver, page);
		waitForPageObjectToLoad();
	}

	/**
	 * Constructor with the email address to use for logging out of Web application
	 *
	 * @param driver WebDriver for PageObject
	 * @param page   PageConfiguration for the UI page
	 */
	public LogoutPO(WebDriver driver, PageConfiguration page, String emailAddress) {
		super(driver, page);
		this.emailAddress = emailAddress;
		waitForPageObjectToLoad();
	}

	@Override
	public void waitForPageObjectToLoad() {
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, iconUserMenu);
	}

	/**
	 * Retrieve the user name which will be used to logout if email isn't provided
	 * @return the user name
	 * @throws ElementNotFoundException is there are any issues
	 */
	public String retrieveUsername() throws ElementNotFoundException
	{
		SeleniumPageHelperAndWaiter.clickWebElement(this, iconUserMenu);
		String Username= SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelUsername);
		return Username;
	}
	
	/**
	 * Logout from the Web Application
	 */
	@Override
	public void logout() throws ElementNotFoundException
	{
		logger.debug("Loging out from RX UI web application");
		
		/*
		 * GC (04/01/22) Handle occasion when an alert is displayed that is blocking the account icon
		 */
		
		try {
			if ( SeleniumPageHelperAndWaiter.isWebElementVisible(driver, alertWebApp, 1) )
			{
				SeleniumPageHelperAndWaiter.clickWebElement(this, buttonAlertExit);
				SeleniumPageHelperAndWaiter.pause(200);	// Handles intermittent 'interrupted element exception'
			}
		}
		catch (Exception e) {
			// do nothing
		}

		
		SeleniumPageHelperAndWaiter.clickWebElement(this, iconUserMenu);
		SeleniumPageHelperAndWaiter.clickWebElement(this, menuLogout);

		By accountLocator = null;
		
		// Determine which to use to logout, either email address or username
		if ( StringUtils.isNotBlank(emailAddress) )
		{
			accountLocator = By.xpath( String.format(buttonEmailXpath, emailAddress) );
		}
		else
		{
			String username = this.retrieveUsername();
			accountLocator = By.xpath( String.format(buttonNameXpath, username) );
		}
		
		// Use the account locator to logout if it is present
		if ( SeleniumPageHelperAndWaiter.isLocatorPresence(this, accountLocator, 2) )
		{
			SeleniumPageHelperAndWaiter.clickBy(this, accountLocator);
		}
		// Use the first account image to logout
		else
		{
			SeleniumPageHelperAndWaiter.clickWebElement(this, imgAccount);
		}
		
		// Wait for the logout to complete within 3 seconds
		SeleniumPageHelperAndWaiter.pause(3000);
		logger.debug("Successfully out from RX UI web application");
	}

	/*
	 * Getter
	 */
	
	public WebElement getIconUserMenu() {
		return iconUserMenu;
	}

	public WebElement getMenuLogout() {
		return menuLogout;
	}

	public WebElement getLabelUsername() {
		return labelUsername;
	}
	
	
}
