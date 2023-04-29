/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.configuration;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Browser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.cosmos.CosmosClient;
import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.selenium.BrowserSetup;

/**
 * Start Rx CC Web application
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/24/2022
 */
public class RxConciergeUIBrowser extends BrowserSetup
{
	
	private static final Logger logger = LoggerFactory.getLogger(RxConciergeUIBrowser.class);

	@Override
	public void login() throws Exception 
	{
		// Login will be done else where
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
		
		// close the browser
		try {
			super.close();
		}
		catch (Exception e) {

			logger.error("Unable to properly close the browser --> ", e.getMessage());

			if ( e instanceof WebDriverException || ExceptionUtils.getRootCause(e) instanceof WebDriverException )
			{
				logger.error("Trying it one more time using driver.quit() ...");
				try {
					super.getDriverBase().getWebDriver().quit();	// try it one more time
				}
				catch (Exception e1) {
					logger.error("Still can't close the browser --> ", e1.getMessage());
				}
			}
		}
	}

}

