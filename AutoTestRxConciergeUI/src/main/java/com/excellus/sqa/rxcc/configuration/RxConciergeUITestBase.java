/**
 * 
 * @copyright 2021 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.configuration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.IUserConfiguration;
import com.excellus.sqa.configuration.TestConfiguration;
import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.pageobject.LoginPO;
import com.excellus.sqa.rxcc.pageobject.LogoutPO;
import com.excellus.sqa.rxcc.pages.home.MainMenuPO;
import com.excellus.sqa.selenium.BrowserSetup;
import com.excellus.sqa.selenium.DriverBase;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.selenium.step.LogInUIStep;
import com.excellus.sqa.selenium.step.LogOffUIStep;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.spring.SpringConfig;
import com.excellus.sqa.step.IStep;
import com.excellus.sqa.step.IStep.Status;
import com.excellus.sqa.test.TestBase;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * Main test containing
 * - BeforeAll
 *      starts browser
 *      login to Rx CC UI
 *      Scrape account and token information
 * - AfterAll:
 *      Logout from Rx CC UI
 *      Closes browser then disconnect from APEX DB.
 * 
 * @author Garrett Cosmiano (gcosmian)
 * @since 12/17/2021
 */
@ExtendWith(RxConciergeUIBrowser.class)
@SpringConfig(classes = {RxConciergeBeanConfig.class, RxConciergeCosmoConfig.class})
public class RxConciergeUITestBase extends TestBase
{
	
	private static final Logger logger = LoggerFactory.getLogger(RxConciergeUITestBase.class);

	protected static DriverBase driverBase;
	
	private static List<TestConfiguration> testConfigurations = null;
	
	protected static boolean loginSuccessful = false;

	/*
	 * Account information that is populated from scraping Web UI after logging to Rx CC Web application
	 */
	protected static String acctName;
	protected static String acctUsername;

	/*
	 * Token information that is populated from scraping Web UI after logging to Rx CC Web application
	 */
	protected static String tokenAccess;
	protected static String refreshToken;
	protected static Date tokenExpiration;
	
	protected static TestConfiguration testConfiguration;
	
	@BeforeAll
	public static void login() throws Exception
	{
		// no need to login if we are still log on
		if ( loginSuccessful )
			return;
		
		if ( driverBase == null )
			driverBase = BrowserSetup.getDriverBase();
		
		/*
		 * For logging purposes
		 * 
		
		EdgeConfiguration edgeConfig = BeanLoader.loadBean(EdgeConfiguration.class);
		logger.info("Edge driver exists: " + edgeConfig.isWebDriverExists());
		logger.info("Edge driver: " + edgeConfig.getDriverAbsoluteFilePath());
		
		ChromeConfiguration chromeConfig = BeanLoader.loadBean(ChromeConfiguration.class);
		logger.info("Chrome driver exists: " + chromeConfig.isWebDriverExists());
		logger.info("Chrome driver: " + chromeConfig.getDriverAbsoluteFilePath());
		 * 
		 */
		
		/*
		 * Login to Rx CC Web application
		 */
		
		TestConfiguration testConfiguration = getTestConfiguration();

		// Login page
		PageConfiguration loginPage = BeanLoader.loadBean(BeanNames.LOGIN_PAGE, PageConfiguration.class);
		LoginPO loginPO = new LoginPO(driverBase.getWebDriver(), loginPage, driverBase.getDevTools());

		// Login step
		LogInUIStep logInStep = null;

		// Logout of the application if current user is different from test user
		if ( StringUtils.isNotBlank(acctUsername) && !StringUtils.equals(acctUsername, getTestConfiguration().getUserName()) )
		{
			PageConfiguration mainMenuPage = (PageConfiguration) BeanLoader.loadBean(BeanNames.HOME_PAGE);
			MainMenuPO mainMenuPO = new MainMenuPO(driverBase.getWebDriver(), mainMenuPage, acctUsername);  // GC (02/21/22) use MainMenuPO instead of HomePO

			LogOffUIStep logOffStep = new LogOffUIStep(mainMenuPO, false);
			logOffStep.run();

			if ( logOffStep.getStepException() != null && logOffStep.stepStatus() != Status.COMPLETED )
			{
				((AbstractUIStep) logOffStep).snagIt();
				throw logOffStep.getStepException();
			}

			logInStep = new LogInUIStep(loginPO, testConfiguration);
		}
		// Everything else
		else
		{
			logInStep = new LogInUIStep(loginPO, testConfiguration);
		}
		
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
		
		loginSuccessful = true;
		logger.debug("Login successful");
		
		// set API URL
        RestAssured.baseURI  = (String) BeanLoader.loadBean(BeanNames.API_URL);
	}
	
	@AfterAll
	public static void tearDown()
	{
		// set logout page object
		PageConfiguration logoutPage = BeanLoader.loadBean(BeanNames.HOME_PAGE, PageConfiguration.class);
		LogoutPO logoutPO = new LogoutPO(driverBase.getWebDriver(), logoutPage, 
								StringUtils.isNotBlank(acctUsername) ? acctUsername : testConfiguration.getUserConfig().getUsername()); // GC (06/13/22)
		
		// Logout of the Web application
		LogOffUIStep logOffStep = new LogOffUIStep(logoutPO, false);
		logOffStep.run();
		
		if ( logOffStep.stepStatus() != Status.COMPLETED )
		{
			logger.error("Unable to logout", logOffStep.getStepException());
		}
		
		acctName = null;
		acctUsername = null;
		
		tokenAccess = null;
		refreshToken = null;
		tokenExpiration = null;
		
		testConfigurations = null;
		
		loginSuccessful = false;
	}
	
	/**
	 * Retrieve all the test configurations to be used for a test method
	 * 
	 * @return list of {@link TestConfiguration}
	 */
	public static List<TestConfiguration> getTestConfigurations()
	{
		testConfigurations = new ArrayList<TestConfiguration>();
		for ( String userRole : testExecutionUserList )
		{
			TestConfiguration config = (TestConfiguration) BeanLoader.loadBean(userRole);
			testConfigurations.add(config);
		}
		
		return testConfigurations;
	}
	
	/**
	 * Retrieve the the first test configuration
	 * 
	 * @return
	 */
	public static TestConfiguration getTestConfiguration()
	{
		List<TestConfiguration> configs = getTestConfigurations();
		
		if ( configs.size() > 0 )
			return configs.get(0);
		
		throw new TestConfigurationException("There are no test configuration setup");
	}
	
	@Override
	public List<IUserConfiguration> convertUserToUserConfiguration()
	{
		return null;
	}
	
	
	 /**
     * Retrieve generic and common headers with invalid Bearer authorization
     * 
     * @return {@link Headers}
     */
    protected static Headers getGenericHeaders()
    {
        Header authorization = new Header("Authorization", "Bearer " + tokenAccess);
        Header contentType = new Header("Content-Type", ContentType.JSON.toString());
        Header accept = new Header("Accept", ContentType.JSON.toString());
        
        return new Headers(authorization, contentType, accept);
    }

}
