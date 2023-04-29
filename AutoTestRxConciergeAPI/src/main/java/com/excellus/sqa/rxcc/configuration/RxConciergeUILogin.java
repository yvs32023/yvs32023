/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.configuration;

import java.util.Date;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.Browser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.cosmos.CosmosClient;
import com.excellus.sqa.configuration.TestConfiguration;
import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.junit.AssumeUserRoleCondition;
import com.excellus.sqa.rxcc.pageobject.LoginPO;
import com.excellus.sqa.rxcc.pageobject.LogoutPO;
import com.excellus.sqa.selenium.BrowserSetup;
import com.excellus.sqa.selenium.DriverBase;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.LogInUIStep;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.step.IStep;

/**
 * Login to Rx CC Web application
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/12/2022
 */
public class RxConciergeUILogin extends BrowserSetup
{
	private static final Logger logger = LoggerFactory.getLogger(RxConciergeUILogin.class);

	/*
	 * Account information that is populated from scraping Web UI after logging to Rx CC Web application
	 */
	private static String acctName;
	private static String acctUsername;

	/*
	 * Token information that is populated from scraping Web UI after logging to Rx CC Web application
	 */
	private static String tokenAccess;
	private static String refreshToken;
	private static Date tokenExpiration;


	@Override
	public void login() throws Exception {

		logger.debug("Starting to login");

		// GC (03/06/22) use the token from the VM argument if it is provided
		tokenAccess = System.getProperty("token");
		if (StringUtils.isNotBlank(tokenAccess)) {
			return;
		}

		DriverBase driverBase = BrowserSetup.getDriverBase();

		/*
		 * Login to Rx CC Web application
		 */

		TestConfiguration testConfiguration = BeanLoader.loadBean(AssumeUserRoleCondition.getUserRole(), TestConfiguration.class);

		// Login page
		PageConfiguration loginPage = BeanLoader.loadBean("loginPage", PageConfiguration.class);
		LoginPO loginPO = new LoginPO(driverBase.getWebDriver(), loginPage, driverBase.getDevTools());

		// Login step
		LogInUIStep logInStep = new LogInUIStep(loginPO, testConfiguration);

		// Login to application
		logInStep.run();

		if ( logInStep.stepStatus() != IStep.Status.COMPLETED )
			throw logInStep.getStepException();

		// set application information
		acctName		= loginPO.getAcctName();
		acctUsername	= loginPO.getAcctUsername();
		tokenAccess		= loginPO.getTokenAccess();
		refreshToken	= loginPO.getRefreshToken();
		tokenExpiration = loginPO.getTokenExpiration();

		logger.debug("Login successful");

		// GC (03/11/22) set logout page object
		PageConfiguration logoutPage = BeanLoader.loadBean("homePage", PageConfiguration.class);
		LogoutPO logoutPO = new LogoutPO(driverBase.getWebDriver(), logoutPage);
		super.getDriverBase().setLogoutPage(logoutPO);
	}

	/**
	 * Determines the browser to use base on -DBrowser parameter.
	 * The default is Edge if -DBrowser is not provided.
	 *
	 * @return {@link Browser}
	 */
	@Override
	public Browser getBrowser()
	{
		String browser = System.getProperty("browser");

		// Use 'Browser' property if it is still blank
		if ( StringUtils.isBlank(browser) )
			browser = System.getProperty("Browser");

		if ( StringUtils.isBlank(browser) || StringUtils.equalsIgnoreCase(browser, "Edge") )
		{
			return Browser.EDGE;
		}
		else if ( StringUtils.containsIgnoreCase(browser, "Chrome") )
		{
			return Browser.CHROME;
		}
		else
		{
			throw new TestConfigurationException("Please set JVM arg 'browser' to either Chrome or Edge");
		}
	}

	/**
	 * Callback that is invoked <em>exactly once</em>
	 * after the end of <em>all</em> test containers.
	 * Inherited from {@code CloseableResource}
	 */
	@Override
	public void close() throws Throwable {

		// close the browser
		if ( super.getDriverBase() != null )
		{
			super.getDriverBase().destroyWebDriver();
		}

		// close all Cosmos DB connections
		if ( RxConciergeCosmoConfig.getTokenCredential() != null )
		{
			// GC (11/01/22) Close only those that where opened
			for ( Entry<String, CosmosClient> entry : RxConciergeCosmoConfig.getOpenedCosmosClient().entrySet() )
			{
				String  cosmosClientName = entry.getKey();
				CosmosClient cosmosClient = entry.getValue();

				if ( cosmosClient != null )
				{
					try
					{
						logger.info("Closing Cosmos DB connection for " + cosmosClientName);
						cosmosClient.close();
						logger.info("Successfully closed Cosmos DB connection for " + cosmosClientName);
					}
					catch (Exception e)
					{
						logger.error("Unable to close Cosmos DB connection for "  + cosmosClientName, e);
					}
				}
			}
		}
	}


	/*
	 * Setter / Getter
	 */

	public static String getAcctName() {
		return acctName;
	}

	public static void setAcctName(String acctName) {
		RxConciergeUILogin.acctName = acctName;
	}

	public static String getAcctUsername() {
		return acctUsername;
	}

	public static void setAcctUsername(String acctUsername) {
		RxConciergeUILogin.acctUsername = acctUsername;
	}

	public static String getTokenAccess() {
		return tokenAccess;
	}

	public static void setTokenAccess(String tokenAccess) {
		RxConciergeUILogin.tokenAccess = tokenAccess;
	}

	public static String getRefreshToken() {
		return refreshToken;
	}

	public static void setRefreshToken(String refreshToken) {
		RxConciergeUILogin.refreshToken = refreshToken;
	}

	public static Date getTokenExpiration() {
		return tokenExpiration;
	}

	public static void setTokenExpiration(Date tokenExpiration) {
		RxConciergeUILogin.tokenExpiration = tokenExpiration;
	}

}
