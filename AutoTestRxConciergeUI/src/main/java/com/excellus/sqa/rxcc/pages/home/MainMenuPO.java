/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.pages.home;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.MainMenu;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.ILogoutPage;

/**
 * Main menu of the Web application
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/21/2022
 */
public class MainMenuPO extends AbstractCommonPage implements ILogoutPage
{

    private static final Logger logger = LoggerFactory.getLogger(MainMenuPO.class);

    @FindBy(xpath = "//p-menubar//a[text()='Rx Clinical Concierge']")
    private WebElement menuHomePage;

    final private String MENU_XPATH = "//rxc-header/header/p-menubar/div/div/div/svg-icon[%s]"; 

    @FindBy(xpath = "//rxc-header/header/p-menubar/div/div/div/svg-icon")
    private List<WebElement> menuItems;

    @FindBy(xpath = "//div[@class='p-tooltip-text']")
    private WebElement tooltip;

    @FindBy(tagName = "rxc-user-menu")
    private WebElement iconUserMenu;

    private final String LABEL_USERNAME_XPATH = "//rxc-user-menu//p-menu//div//ul//li[1]//span";

    @FindBy(xpath = LABEL_USERNAME_XPATH)
    private WebElement labelUsername;

    @FindBy(xpath = "//rxc-user-menu//a/span[.='Logout']")
    private WebElement menuLogout;

    @FindBy(xpath = "//rxc-search//p-autocomplete//span//input")
    private WebElement inputFuzzySearchBar;

    private String buttonEmailXpath = "//small[.='%s']";

    private String emailAddress;

    @FindBy(xpath = "//a[@href='/tenants']/strong")
    private WebElement labelCurrentTenant;

    /**
     * Constructor
     *
     * @param driver WebDriver for PageObject
     * @param page   PageConfiguration for the UI page
     */
    public MainMenuPO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
        waitForPageObjectToLoad();
    }

    /**
     * Constructor with the email address to use for logging out of Web application
     *
     * @param driver WebDriver for PageObject
     * @param page   PageConfiguration for the UI page
     */
    public MainMenuPO(WebDriver driver, PageConfiguration page, String emailAddress) {
        super(driver, page);
        this.emailAddress = emailAddress;
        waitForPageObjectToLoad();
    }

    @Override
    public void waitForPageObjectToLoad() {
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, menuHomePage);
    }

    public void selectMenu(MainMenu item) throws ElementNotFoundException
    {
        By menuLocator = null;

        /*
         * Get the By locator for the menu
         */
        switch (item) {
        case PROVIDER_DIRECTORY:
            menuLocator = By.xpath( String.format(MENU_XPATH, item.getMenuIndex()) );
            break;

        case INTERVENTION_RULE_LIBRARY:
            menuLocator = By.xpath( String.format(MENU_XPATH, item.getMenuIndex()) );
            break;

        case PHARMACY_DIRECTORY:
            menuLocator = By.xpath( String.format(MENU_XPATH, item.getMenuIndex()) );
            break;

        case INTERVENTION_QUEUE:
            menuLocator = By.xpath( String.format(MENU_XPATH, item.getMenuIndex()) );
            break;

        case FORMULARY_MAINTENANCE:
            menuLocator = By.xpath( String.format(MENU_XPATH, item.getMenuIndex()) );
            break;

        case SIMULATION_QUEUE:
            menuLocator = By.xpath( String.format(MENU_XPATH, item.getMenuIndex()) );
            break;

        case FAX_DISCREPANCY_QUEUE:
            menuLocator = By.xpath( String.format(MENU_XPATH, item.getMenuIndex()) );
            break;

        default:
            break;
        }

        /*
         * Hover on the menu and wait for the tool tip
         */
        WebElement menu = driver.findElement(menuLocator);
        SeleniumPageHelperAndWaiter.hoverOnElement(this, menu);
        SeleniumPageHelperAndWaiter.pause(1000);
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, tooltip);

        /*
         * Get the tool tip text
         */

        String tooltipText = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, tooltip);
        if ( StringUtils.equalsIgnoreCase(item.getToolTip(), tooltipText) )
        {
            SeleniumPageHelperAndWaiter.clickBy(this, menuLocator);
        }
        /*
         * Loop through the menu items from the UI
         */
        else
        {
            logger.warn("Menu index for " + item + " is not correct or the tooltip has changed");

            boolean foundMenuItem = false;
            for ( WebElement menuItem : menuItems )
            {
                SeleniumPageHelperAndWaiter.hoverOnElement(this, menuItem);
                SeleniumPageHelperAndWaiter.pause(100);
                SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, tooltip);

                tooltipText = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, tooltip);
                if ( StringUtils.equalsIgnoreCase(item.getToolTip(), tooltipText) )
                {
                    SeleniumPageHelperAndWaiter.clickBy(this, menuLocator);
                    foundMenuItem = true;
                    break;
                }
            }

            if ( !foundMenuItem )
            {
                throw new ElementNotFoundException("Menu item " + item + " was not found in the UI");
            }
        }
    }
    /*
     * This Method will be used to validate the Main Menu icon
     * for Member Pharmacy and Provider Tab tests
     * Common Layout Shell
     */

    public boolean iconValidation(MainMenu item) throws ElementNotFoundException
    {
        By menuLocator = null;

        /*
         * Get the By locator for the menu
         */
        switch (item) {
        case PROVIDER_DIRECTORY:
            menuLocator = By.xpath( String.format(MENU_XPATH, item.getMenuIndex()) );
            break;

        case INTERVENTION_RULE_LIBRARY:
            menuLocator = By.xpath( String.format(MENU_XPATH, item.getMenuIndex()) );
            break;

        case PHARMACY_DIRECTORY:
            menuLocator = By.xpath( String.format(MENU_XPATH, item.getMenuIndex()) );
            break;

        case INTERVENTION_QUEUE:
            menuLocator = By.xpath( String.format(MENU_XPATH, item.getMenuIndex()) );
            break;

        case FORMULARY_MAINTENANCE:
            menuLocator = By.xpath( String.format(MENU_XPATH, item.getMenuIndex()) );
            break;

        case SIMULATION_QUEUE:
            menuLocator = By.xpath( String.format(MENU_XPATH, item.getMenuIndex()) );
            break;

        case FAX_DISCREPANCY_QUEUE:
            menuLocator = By.xpath( String.format(MENU_XPATH, item.getMenuIndex()) );
            break;

        default:
            break;
        }

        boolean isIconVisible = SeleniumPageHelperAndWaiter.isWebElementVisible(this, menuLocator);
        return isIconVisible;
    }

    public String retrieveUsername() throws ElementNotFoundException{
        SeleniumPageHelperAndWaiter.clickWebElement(this, iconUserMenu);
        String Username= SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelUsername);

        // GC (03/25/22) click the user icon again so the menu disappear
        SeleniumPageHelperAndWaiter.clickWebElement(this, iconUserMenu);
        SeleniumPageHelperAndWaiter.waitForInvisibleOfWebElement(driver, By.xpath(LABEL_USERNAME_XPATH), 1);

        return Username;
    }

    /**
     * Clicks the menu WebElement HomePage
     * 
     * @author gcosmian
     * @since 02/22/22
     * @throws ElementNotFoundException if issue occurs
     */
    public void clickHomePage() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, menuHomePage);

        // GC (03/30/22) Since TenantPO is in the home page, use this to wait for the page to load
        new TenantPO(driver, pageConfiguration);
    }

    /**
     * Retrieve the text of the field CurrentTenant label WebElement
     * 
     * @author gcosmian
     * @since 02/22/22
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public String retrieveCurrentTenant() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelCurrentTenant);
    }

    public boolean validateLogoutExist( ) throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.clickWebElement(this, iconUserMenu);
        boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, menuLogout, 5);
        if (displayed)
            return true;
        else {
            return false;
        }
    }

    public boolean validateFuzzySearchExist( ) throws ElementNotFoundException {
        boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, inputFuzzySearchBar, 5);
        if (displayed)
            return true;
        else {
            return false;
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
