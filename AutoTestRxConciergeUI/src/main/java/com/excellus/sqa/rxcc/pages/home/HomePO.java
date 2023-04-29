/**
 * 
 * @copyright 2021 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.home;

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
 * 
 * 
 * @author Garrett Cosmiano (gcosmian)
 * @since 12/17/2021
 */
public class HomePO extends AbstractCommonPage implements ILogoutPage
{
	
	@FindBy(tagName = "rxc-user-menu")
	private WebElement iconUserMenu;
	
	@FindBy(xpath = "//rxc-user-menu//a/span[.='Logout']")
	private WebElement menuLogout;
	
	private String buttonEmailXpath = "//small[.='%s']";
	
	@FindBy(xpath = "//h2[contains(text(),'Global Search')]")
	private WebElement labelGlobalSearch;
	
	@FindBy(xpath = "//rxc-user-menu//p-menu//div//ul//li[1]//span")
	private WebElement labelUsername;
	
	@FindBy(xpath = "//p-menubar//div//p-menubarsub//ul//li[1]//a//span[1]")
	private WebElement iconProviderDir;
	
	@FindBy(xpath = "//p-menubar//div//p-menubarsub//ul//li[2]//a//span[1]")
	private WebElement iconInterventionTemp;
	
	@FindBy(xpath = "//p-menubar//div//p-menubarsub//ul//li[3]//a//span[1]")
	private WebElement iconPharmacyDir;
	
	private String emailAddress;
	private static final Logger logger = LoggerFactory.getLogger(HomePO.class);
	
	public HomePO(WebDriver driver, PageConfiguration page) 
	{
		super(driver, page);
		//waitForPageObjectToLoad();
	}

	public HomePO(WebDriver driver, PageConfiguration page, String emailAddress) 
	{
		super(driver, page);
		this.emailAddress = emailAddress;
		//waitForPageObjectToLoad();
	}

	@Override
	public void waitForPageObjectToLoad() 
	{
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, labelGlobalSearch);
	}
	
	public String retrieveUsername() throws ElementNotFoundException{
		SeleniumPageHelperAndWaiter.clickWebElement(this, iconUserMenu);
		String Username= SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelUsername);
		return Username;
	}
	
	public void clickIconProv() {
		
		try {
			boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, iconProviderDir, 5);
			if ( displayed )
				SeleniumPageHelperAndWaiter.clickWebElement(this, iconProviderDir);	
		}
		catch (Exception e)
		{
			logger.error("Provider icon not found on home page");
			
		}
	
	}
	
	public void clickIconIntervention() {
		
		try {
			boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, iconInterventionTemp, 5);
			if ( displayed )
				SeleniumPageHelperAndWaiter.clickWebElement(this, iconInterventionTemp);
		}
		catch (Exception e)
		{
			logger.error("Intervention Template icon not found on home page");
			
		}
	
	}

	public void clickIconPhar() {
	
		try {
			boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, iconPharmacyDir, 5);
			if ( displayed )
			SeleniumPageHelperAndWaiter.clickWebElement(this, iconPharmacyDir);
		}
		catch (Exception e)
		{
			logger.error("Pharmacy icon not found on home page");
		
		}

	}

	@Override
	public void logout() throws ElementNotFoundException 
	{
		SeleniumPageHelperAndWaiter.clickWebElement(this, iconUserMenu);
		SeleniumPageHelperAndWaiter.clickWebElement(this, menuLogout);
		
		By username = By.xpath( String.format(buttonEmailXpath, emailAddress) );
		SeleniumPageHelperAndWaiter.clickBy(this, username);
		
		// wait for the login page to load
		com.excellus.sqa.rxcc.pageobject.LoginPO loginPO = new com.excellus.sqa.rxcc.pageobject.LoginPO(driver, pageConfiguration, null);
		loginPO.waitForPageObjectToLoad();
	}
	
}
