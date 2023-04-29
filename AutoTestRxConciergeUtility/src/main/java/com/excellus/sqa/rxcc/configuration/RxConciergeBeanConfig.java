/**
 * 
 * @copyright 2021 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.configuration;

import java.io.FileNotFoundException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import com.excellus.sqa.configuration.IUserConfiguration;
import com.excellus.sqa.configuration.TestConfiguration;
import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.selenium.configuration.ChromeConfiguration;
import com.excellus.sqa.selenium.configuration.EdgeConfiguration;
import com.excellus.sqa.selenium.configuration.PageConfiguration;

/**
 * Configuration for Rx Clinical Concierge
 * 
 * @author Garrett Cosmiano (gcosmian)
 * @since 12/09/2021
 */
@Lazy(true)
@Configuration
@PropertySources({
	@PropertySource("classpath:properties/rxCC-${environment}.properties"),
	@PropertySource("classpath:properties/pageConfiguration-${environment}.properties")
})
public class RxConciergeBeanConfig 
{
	@Autowired
	private Environment env;
	
	private static Environment environment;

	/*
	 * -----------------------------------------------
	 *                  Properties
	 * -----------------------------------------------
	 */
	
	@Value("${url.home}")
	private String urlHome;
	
	@Value("${chrome.driver.dir}")
	private String chromeDriverRelativeFilePath;
	
	@Value("${chrome.extensions}")
	private String chromeExtensions;
	
	@Value("${edge.driver.dir}")
	private String edgeDriverRelativeFilePath;
	
	@Value("${url.api}")
	private String apiUrl;

	/* Users */

	/* Multiple tenant level access */

	@Value("${full.multi.user}")
	private IUserConfiguration userFullMulti;
	
	@Value("${full.multi.pwd}")
	private String passwordFullMulti;

	@Value("${read.multi.user}")
	private IUserConfiguration userReadMulti;

	@Value("${read.multi.pwd}")
	private String passwordReadMulti;

	@Value("${ops.multi.user}")
	private IUserConfiguration userOpsMulti;

	@Value("${ops.multi.pwd}")
	private String passwordOpsMulti;

	@Value("${report.multi.user}")
	private IUserConfiguration userReportMulti;

	@Value("${report.multi.pwd}")
	private String passwordReportMulti;

	/* EHP tenant level access */

	@Value("${full.single.user}")
	private IUserConfiguration userFullSingle;

	@Value("${full.single.pwd}")
	private String passwordFullSingle;

	@Value("${read.single.user}")
	private IUserConfiguration userReadSingle;

	@Value("${read.single.pwd}")
	private String passwordReadSingle;

	@Value("${ops.single.user}")
	private IUserConfiguration userOpsSingle;

	@Value("${ops.single.pwd}")
	private String passwordOpsSingle;

	@Value("${report.single.user}")
	private IUserConfiguration userReportSingle;

	@Value("${report.single.pwd}")
	private String passwordReportSingle;

	/* LOA tenant level access */

	@Value("${full.loa.user}")
	private IUserConfiguration userFullLoa;

	@Value("${full.loa.pwd}")
	private String passwordFullLoa;

	@Value("${read.loa.user}")
	private IUserConfiguration userReadLoa;

	@Value("${read.loa.pwd}")
	private String passwordReadLoa;

	@Value("${ops.loa.user}")
	private IUserConfiguration userOpsLoa;

	@Value("${ops.loa.pwd}")
	private String passwordOpsLoa;

	@Value("${report.loa.user}")
	private IUserConfiguration userReportLoa;

	@Value("${report.loa.pwd}")
	private String passwordReportLoa;

	/* MED tenant level access */

	@Value("${full.med.user}")
	private IUserConfiguration userFullMed;

	@Value("${full.med.pwd}")
	private String passwordFullMed;

	@Value("${read.med.user}")
	private IUserConfiguration userReadMed;

	@Value("${read.med.pwd}")
	private String passwordReadMed;

	@Value("${ops.med.user}")
	private IUserConfiguration userOpsMed;

	@Value("${ops.med.pwd}")
	private String passwordOpsMed;

	@Value("${report.med.user}")
	private IUserConfiguration userReportMed;

	@Value("${report.med.pwd}")
	private String passwordReportMed;


	/* Page configuration */
	
	@Value("${home.pagename}")
	private String homePage;
	
	@Value("${home.element.timeout}")
	private int homePageElementTimeout;
	
	@Value("${home.element.retries}")
	private int homePageElementRetry;
	
	@Value("${rxc.login.pagename}")
	private String loginPage;
	
	@Value("${rxc.login.element.timeout}")
	private int loginPageElementTimeout;
	
	@Value("${rxc.login.element.retries}")
	private int loginPageElementRetry;
	
	@Value("${tenant.pagename}")
	private String tenantPage;
	
	@Value("${tenant.element.timeout}")
	private int tenantPageElementTimeout;
	
	@Value("${tenant.element.retries}")
	private int tenantPageElementRetry;
	
	@Value("${member.pagename}")
	private String memberPage;
	
	@Value("${member.element.timeout}")
	private int memberPageElementTimeout;
	
	@Value("${member.element.retries}")
	private int memberPageElementRetry;
	
	@Value("${provider.pagename}")
	private String providerPage;
	
	@Value("${provider.element.timeout}")
	private int providerPageElementTimeout;
	
	@Value("${provider.element.retries}")
	private int providerPageElementRetry;
	
	@Value("${intervention.pagename}")
	private String interventionPage;
	
	@Value("${intervention.element.timeout}")
	private int interventionPageElementTimeout;
	
	@Value("${provider.element.retries}")
	private int interventionPageElementRetry;
	
	@Value("${pharmacies.pagename}")
	private String pharmaciesPage;
	
	@Value("${pharmacies.element.timeout}")
	private int pharmaciesPageElementTimeout;
	
	@Value("${pharmacies.element.retries}")
	private int pharmaciesPageElementRetry;
	
	
	/**
	 * Setup the environment configuration to be accessible
	 * 
	 * @author gcosmian
	 * @since 02/22/22
	 */
	@PostConstruct
	public void init()
	{
		environment = env;
	}
	
	/*
	 * -----------------------------------------------
	 *                   Beans
	 * -----------------------------------------------
	 */

	@Bean(name = BeanNames.CHROME_WEB_APP)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public ChromeConfiguration rxWebAppChrome() throws FileNotFoundException 
	{
		// GC (04/18/22) Add extension to chrome
		ChromeConfiguration chromeConfig = new ChromeConfiguration(urlHome, chromeDriverRelativeFilePath);
		
		if ( StringUtils.isNoneBlank(chromeExtensions) )
		{
			chromeConfig.setExtensions( Arrays.asList(chromeExtensions) );	// set chrome extension
		}
		else
		{
			throw new TestConfigurationException("Chrome browser requires an extension 'Windows 10 Application'!");
		}
		
		return chromeConfig;
	}
	
	@Bean(name = BeanNames.EDGE_WEB_APP)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public EdgeConfiguration rxWebAppEdge() throws FileNotFoundException 
	{
		return new EdgeConfiguration(urlHome, edgeDriverRelativeFilePath);
	}
	
	@Bean(name = BeanNames.API_URL)
	public String getApiUrl()
	{
		return apiUrl;
	}
	
	/*
	 * UI Page configuration
	 */

	/**
	 * Create PageConfiguration for Rx CC Home page
	 * @return PageConfiguration
	 */
	@Bean(name = BeanNames.HOME_PAGE)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PageConfiguration homePage() 
	{
		return new PageConfiguration(homePage, homePageElementTimeout, homePageElementRetry);
	}

	/**
	 * Create PageConfiguration for Rx CC Login page
	 * @return PageConfiguration
	 */
	@Bean(name = BeanNames.LOGIN_PAGE)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PageConfiguration loginPage() 
	{
		return new PageConfiguration(loginPage, loginPageElementTimeout, loginPageElementRetry);
	}
	
	/**
	 * Create PageConfiguration for Rx CC Tenant
	 * @return PageConfiguration
	 */
	@Bean(name = BeanNames.TENANT_PAGE)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PageConfiguration tenantPage() 
	{
		return new PageConfiguration(tenantPage, tenantPageElementTimeout, tenantPageElementRetry);
	}
	
	/**
	 * Create PageConfiguration for Rx CC member
	 * @return PageConfiguration
	 */
	@Bean(name = BeanNames.MEMBER_PAGE)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PageConfiguration memberPage() 
	{
		return new PageConfiguration(memberPage, memberPageElementTimeout, memberPageElementRetry);
	}
	
	/**
	 * Create PageConfiguration for Rx CC provider
	 * @return PageConfiguration
	 */
	@Bean(name = BeanNames.PROVIDER_PAGE)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PageConfiguration providerPage() 
	{
		return new PageConfiguration(providerPage, providerPageElementTimeout, providerPageElementRetry);
	}
	
	/**
	 * Create PageConfiguration for Rx CC intervention
	 * @return PageConfiguration
	 */
	@Bean(name = BeanNames.INTERVENTION_PAGE)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PageConfiguration interventionPage() 
	{
		return new PageConfiguration(interventionPage, interventionPageElementTimeout, interventionPageElementRetry);
	}
	
	/**
	 * Create PageConfiguration for Rx CC pharmacy
	 * @return PageConfiguration
	 */
	@Bean(name = BeanNames.PHARMACIES_PAGE)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PageConfiguration pharmaciesPage() 
	{
		return new PageConfiguration(pharmaciesPage, pharmaciesPageElementTimeout, pharmaciesPageElementRetry);
	}
	
	/*
	 * Credentials
	 */

	/**
	 * Test configuration SSO credential
	 * @return TestConfiguration
	 */
	@Bean(name = BeanNames.SSO)
	public TestConfiguration userSSO() 
	{
		System.setProperty("SysKey", "dummy,SecretKey");
		TestConfiguration testConfiguration = new TestConfiguration(UserConfiguration.SSO, "dummyPassword");
		return testConfiguration;
	}

	/**
	 * Test configuration RXCC_FULL_MULTI credential
	 * @return TestConfiguration
	 */
	@Bean(name = BeanNames.RXCC_FULL_MULTI)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCFullMulti() 
	{
		return new TestConfiguration(userFullMulti, passwordFullMulti);
	}

	@Bean(name = BeanNames.RXCC_READ_MULTI)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCReadMulti()
	{
		return new TestConfiguration(userReadMulti, passwordReadMulti);
	}

	@Bean(name = BeanNames.RXCC_OPS_MULTI)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCOpsMulti()
	{
		return new TestConfiguration(userOpsMulti, passwordOpsMulti);
	}

	@Bean(name = BeanNames.RXCC_REPORT_MULTI)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCReportMulti()
	{
		return new TestConfiguration(userReportMulti, passwordReportMulti);
	}

	@Bean(name = BeanNames.RXCC_FULL_SINGLE)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCFullSingle()
	{
		return new TestConfiguration(userFullSingle, passwordFullSingle);
	}

	@Bean(name = BeanNames.RXCC_READ_SINGLE)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCReadSingle()
	{
		return new TestConfiguration(userReadSingle, passwordReadSingle);
	}

	@Bean(name = BeanNames.RXCC_OPS_SINGLE)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCOpsSingle()
	{
		return new TestConfiguration(userOpsSingle, passwordOpsSingle);
	}

	@Bean(name = BeanNames.RXCC_REPORT_SINGLE)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCReportSingle()
	{
		return new TestConfiguration(userReportSingle, passwordReportSingle);
	}

	@Bean(name = BeanNames.RXCC_FULL_LOA)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCFullLoa()
	{
		return new TestConfiguration(userFullLoa, passwordFullLoa);
	}

	@Bean(name = BeanNames.RXCC_READ_LOA)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCReadLoa()
	{
		return new TestConfiguration(userReadLoa, passwordReadLoa);
	}

	@Bean(name = BeanNames.RXCC_OPS_LOA)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCOpsLoa()
	{
		return new TestConfiguration(userOpsLoa, passwordOpsLoa);
	}

	@Bean(name = BeanNames.RXCC_REPORT_LOA)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCReportLoa()
	{
		return new TestConfiguration(userReportLoa, passwordReportLoa);
	}

	@Bean(name = BeanNames.RXCC_FULL_MED)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCFullMed()
	{
		return new TestConfiguration(userFullMed, passwordFullMed);
	}

	@Bean(name = BeanNames.RXCC_READ_MED)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCReadMed()
	{
		return new TestConfiguration(userReadMed, passwordReadMed);
	}

	@Bean(name = BeanNames.RXCC_OPS_MED)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCOpsMed()
	{
		return new TestConfiguration(userOpsMed, passwordOpsMed);
	}

	@Bean(name = BeanNames.RXCC_REPORT_MED)
	@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
	public TestConfiguration userRxCCReportMed()
	{
		return new TestConfiguration(userReportMed, passwordReportMed);
	}

	/**
	 * Return the environment configuration
	 * 
	 * @author gcosmian
	 * @since 02/22/22
	 * @return
	 */
	public static Environment envConfig()
	{
		return environment;
	}
}
