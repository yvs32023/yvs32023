/**
 * 
 * @copyright 2021 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pageobject;

import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.DevToolsException;
import org.openqa.selenium.devtools.v110.network.Network;
import org.openqa.selenium.devtools.v110.network.model.RequestId;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.ILoginPage;
import com.excellus.sqa.utilities.Decryptor;

/**
 * Handles the login page
 * 
 * @author Garrett Cosmiano (gcosmian)
 * @since 12/17/2021
 */
public class LoginPO extends AbstractCommonPage implements ILoginPage 
{
	
	private static final Logger logger = LoggerFactory.getLogger(LoginPO.class);
	
	private String acctName;
	private String acctUsername;
	
	private String tokenAccess;
	private String refreshToken;
	private Date tokenExpiration;
	
	private final DevTools devTools;

	@FindBy(xpath = "//*[@data-testid='login-button']")
	private WebElement buttonLogin;
	
	@FindBy(id = "loginHeader")
	private WebElement headerPickAnAccount;
	
	@FindBy(id = "otherTileText")
	private WebElement buttonUseAnotherAccount;
	
	@FindBy(xpath = "//input[@type='email']")
	private WebElement textboxUsername;
	
	@FindBy(name = "passwd")
	private WebElement textboxPassword;
	
	@FindBy(xpath = "//input[@type='submit' and @value='Next']")
	private WebElement buttonNext;
	
	@FindBy(xpath = "//input[@type='submit' and @value='Sign in']")
	private WebElement buttonSignIn;
	
	@FindBy(xpath = "//a[text()='Sign out and sign in with a different account']")
	private WebElement linkSignOutSignIn;
	
	@FindBy(xpath = "//a[text()='Switch Microsoft Edge profiles']")
	private WebElement linkSwitchProfile;
	
	/**
	 * Constructor
	 * 
	 * @param driver {@link WebDriver}
	 * @param page {@link PageConfiguration}
	 * @param devTools {@link DevTools}
	 */
	public LoginPO(WebDriver driver, PageConfiguration page, DevTools devTools) 
	{
		super(driver, page);
		this.devTools = devTools;
	}

	@Override
	public void waitForPageObjectToLoad()
	{
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, buttonLogin);	
	}
	
	/**
	 * Login to Rx CC Web Application
	 * 
	 * @param emailAddress that will be used to login
	 * @param password associated with the email address
	 */
	@Override
	public void login(String emailAddress, String password) throws Exception 
	{
		// GC (06/13/22) wait for the login page to load completely
		waitForPageObjectToLoad();
		SeleniumPageHelperAndWaiter.clickWebElement(this, buttonLogin);
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, headerPickAnAccount);
		
		/*
		 * Add listener using DevTools to collect application information
		 */
		getAppInfo();

		// SSO
		if ( StringUtils.isBlank(password) )
		{
			By account = By.xpath( String.format("//*[@data-test-id='%s']/div", emailAddress) );
			if ( SeleniumPageHelperAndWaiter.isWebElementVisible(this, account) )
			{
				SeleniumPageHelperAndWaiter.clickBy(this, account);
			}
		}
		// Non-SSO
		else
		{
			// Click the existing account
			By account = By.xpath( String.format("//*[@data-test-id='%s']/div", emailAddress) );
			if ( SeleniumPageHelperAndWaiter.isLocatorPresence(this, account, 1) )
			{
				logger.info("Using the existing account " + emailAddress);
				SeleniumPageHelperAndWaiter.clickBy(this, account);
			}
			// Enter the username account
			else
			{
				logger.info("Username " + emailAddress + " not found.");

				try
				{
					boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, linkSignOutSignIn, 5);
					
					if ( displayed )
						SeleniumPageHelperAndWaiter.clickWebElement(this, linkSignOutSignIn);
					else
						logger.info("Not found 'Sign out and sign in with a different account'");
				}
				catch (Exception e)
				{
					// do nothing
				}
				
				try
				{
					boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, buttonUseAnotherAccount, 5);
					
					if ( displayed )
						SeleniumPageHelperAndWaiter.clickWebElement(this, buttonUseAnotherAccount);
					else
						logger.info("Not found 'Use another account'");
				}
				catch (Exception e)
				{
					// do nothing
				}
				
				try
				{
					boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, linkSwitchProfile, 5);
					
					if ( displayed )
					{
						SeleniumPageHelperAndWaiter.clickWebElement(this, linkSwitchProfile);
						SeleniumPageHelperAndWaiter.pause(2000);
						
						logger.info("Current URL: " + driver.getCurrentUrl());
						SeleniumPageHelperAndWaiter.refreshWebPage(this);
						
						driver.get("https://tst.rxcc.excellus.com");
						SeleniumPageHelperAndWaiter.pause(2000);
						
						SeleniumPageHelperAndWaiter.takesScreenshot(driver, Paths.get("target", "errorscreen"), "swtichEdgeProfile");
					}
				}
				catch (Exception e)
				{
					// do nothing
				}

				logger.info("Entering email address " + emailAddress);
				
				// Username
				try
				{
					SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxUsername, emailAddress);
					
					if ( SeleniumPageHelperAndWaiter.isWebElementVisible(driver, buttonNext, this.getElementTimeout())  )
						SeleniumPageHelperAndWaiter.clickWebElement(this, buttonNext);
				}
				catch (Exception e) {
					logger.error("Unable to enter the username", e);
					throw e;
				}
			}


			// Password
			if ( StringUtils.isNotBlank(password) )
			{
				logger.info("Entering password");

				try
				{
					if ( SeleniumPageHelperAndWaiter.isWebElementVisible(driver, textboxPassword, this.getElementTimeout()) )
					{
						SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxPassword, password, true);   // GC (06/13/22) change it to mask password in logging						
						SeleniumPageHelperAndWaiter.clickWebElement(this, buttonSignIn);
					}
				}
				catch (Exception e) {
					logger.warn("Password not entered, it is possible it us using SSO", e);
					throw e;
				}
			}
		}
		
		// More Details
		try
		{
			By moreDetails = By.xpath("//a[text()='More details']");
			if ( SeleniumPageHelperAndWaiter.isLocatorPresence(this, moreDetails, 1) )
			{
				logger.info("Error logging in, dialog box contains 'More details' link!!!");
				
				SeleniumPageHelperAndWaiter.pause(1000);
				SeleniumPageHelperAndWaiter.takesScreenshot(driver, Paths.get("target", "errorscreen"), "moreDetailsError");
				
				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("window.scrollBy(0,document.body.scrollHeight)", "");
				
				throw new TestConfigurationException("Unable to login, see screenshot moreDetailsError_*.png");
			}
		}
		catch (Exception e) {
			logger.warn("Unable to click 'More details'", e);
			throw e;
		}
	
		// Wait for 3 seconds after logging into Web Application
		SeleniumPageHelperAndWaiter.pause(3000);
		
		logger.info("Login successful, waiting to retrieve bearer token");

		/*
		 * Wait for the application information
		 */
		long duration = 10000;
		long start = System.currentTimeMillis();
		while ( StringUtils.isBlank(acctName) || StringUtils.isBlank(acctUsername) || 
				StringUtils.isBlank(tokenAccess) || StringUtils.isBlank(refreshToken) )
		{
			if ( (System.currentTimeMillis() - start) < duration )
			{
				SeleniumPageHelperAndWaiter.pause(1000);
			}
			else
			{
				throw new TestConfigurationException("Unable to retrieve the application information (token, username, etc)");
			}
		}
	}

	
	/**
	 * Login to Rx CC Web Application
	 * 
	 * @param emailAddress that will be used to login
	 * @param password associated with the email address
	 * @param secreteKey to use to decrypt the provided encrypted password
	 */
	@Override
	public void login(String emailAddress, String password, String secreteKey) throws Exception 
	{
		if ( StringUtils.isNotBlank(password) && StringUtils.isNotBlank(secreteKey) )
			login(emailAddress, Decryptor.decrypt(password, secreteKey));
		else
			login(emailAddress, null);
	}

	/**
	 * Retrieves information about the application
	 */
	private void getAppInfo()
	{
		devTools.createSession();
		
		final RequestId[] requestIds = new RequestId[1];
		devTools.send(Network.enable(Optional.of(100000000), Optional.empty(), Optional.empty()));

		logger.info("Enabled network listener");
		
		devTools.addListener(Network.responseReceived(), responseReceived -> {
			
			String url = responseReceived.getResponse().getUrl();
			if ( StringUtils.isBlank(tokenAccess) && url.contains("oauth2/v2.0/token") && responseReceived.getResponse().getStatus() == 200 ) {

				requestIds[0] = responseReceived.getRequestId();
				
				try
				{
					String responseBody = devTools.send(Network.getResponseBody(requestIds[0])).getBody();

					// retrieve tokens and other information
					if ( responseBody.contains("Bearer") )
					{
						logger.info("URL: " + responseReceived.getResponse().getUrl());
						logger.debug("Response Body: " + responseBody);
						
						JSONObject responseObject = new JSONObject(responseBody);
						
						String theTokenAccess = responseObject.getString("access_token");
						String theRefreshToken = responseObject.getString("refresh_token");

						if  ( StringUtils.isBlank(tokenAccess) )
						{
							logger.info("Setting access_token for the first time");
							tokenAccess = theTokenAccess;
							refreshToken = theRefreshToken;
						}
						else if ( !StringUtils.equals(tokenAccess, theTokenAccess) )
						{
							logger.info("Refreshing access_token");
							tokenAccess = theTokenAccess;
							refreshToken = theRefreshToken;
						}
						else
						{
							logger.info("Tokens did not changed");
						}
						
						logger.debug("access_token: " +  tokenAccess);
						logger.debug("refresh_token: " +  refreshToken);
						
						/*
						 * Retrieve name, username and expiration date
						 */
						
						String[] split_string = tokenAccess.split("\\.");
				        String tokenBody = split_string[1];
				        
				        Base64 base64Url = new Base64(true);
				        String tokenBodyDecoded = new String(base64Url.decode(tokenBody));
				        JSONObject jsonBody = new JSONObject(tokenBodyDecoded);
				        
				        this.acctName = jsonBody.getString("name");
				        this.acctUsername = jsonBody.getString("preferred_username");
				        this.tokenExpiration = new Date(jsonBody.getLong("exp") * 1000);
					}
				}
				catch (DevToolsException e)
				{
					logger.warn("Something went wrong processing network response while retrieving application token\n" + e.getMessage());
				}
            }
        });
	}
	
	/*
	 * Setter / Getter
	 */

	public String getAcctName() {
		return this.acctName;
	}

	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}

	public String getAcctUsername() {
		return this.acctUsername;
	}

	public void setAcctUsername(String acctUsername) {
		this.acctUsername = acctUsername;
	}

	public String getTokenAccess() {
		return this.tokenAccess;
	}

	public void setTokenAccess(String tokenAccess) {
		this.tokenAccess = tokenAccess;
	}

	public String getRefreshToken() {
		return this.refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Date getTokenExpiration() {
		return tokenExpiration;
	}

	public void setTokenExpiration(Date tokenExpiration) {
		this.tokenExpiration = tokenExpiration;
	}
	
	
}
