/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.context.annotation.Description;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeBeanConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.dto.MainMenu;
import com.excellus.sqa.rxcc.pages.home.MainMenuPO;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;

/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 02/03/2022
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_FULL_SINGLE", "RXCC_FULL_LOA", "RXCC_FULL_MED"})
@Tag("ALL")
public class AppHeadersTest extends RxConciergeUITestBase {
	//Declared Variables
	String baseURL= RxConciergeBeanConfig.envConfig().getProperty("url.home") + "/";	// GC (02/22/22) use configuration rather than hard coded the value
	String ProviderURL="providers";
	String InterventionURL="intervention-rules";
	String PharmacyURL="pharmacies";
	String InterventionQueueURL ="interventions";
	String FormularyMaintenanceURL="formulary-maintenance";
	String SimulationsURL="simulations";
	String FaxDiscrepanciesURL="fax-discrepancies";
	
	//Page configuration
	PageConfiguration loginPage = (PageConfiguration) BeanLoader.loadBean("homePage");
	MainMenuPO home= new MainMenuPO(driverBase.getWebDriver(), loginPage);
	
	//This test clicks on the Provider icon on the homepage to navigate to the correct Provider URL
	@Test
	@Order(1)
	@Description("3875 - RxCC Application Header - UI Tenant Screen - Provider, Intervention and Pharmacy Links")
	public void testProvider() throws InterruptedException, ElementNotFoundException {

	    home.selectMenu(MainMenu.PROVIDER_DIRECTORY);
		Thread.sleep(2000);
		String actualURL=driverBase.getWebDriver().getCurrentUrl();
		assertEquals((baseURL+ProviderURL),actualURL);
	}
	
	//This test clicks on the Intervention icon on the homepage to navigate to the correct Intervention URL
	@Test
	@Order(2)
	@Description("3875 - RxCC Application Header - UI Tenant Screen - Provider, Intervention and Pharmacy Links")
	public void testIntervention() throws InterruptedException, ElementNotFoundException {

	    home.selectMenu(MainMenu.INTERVENTION_RULE_LIBRARY);
		Thread.sleep(2000);
		String actualURL=driverBase.getWebDriver().getCurrentUrl();
		assertEquals((baseURL+InterventionURL),actualURL);
	}
	
	//This test clicks on the Pharmacy icon on the homepage to navigate to the correct Pharmacy URL
	@Test
	@Order(3)
	@Description("3875 - RxCC Application Header - UI Tenant Screen - Provider, Intervention and Pharmacy Links")
	public void testPharmacy() throws InterruptedException, ElementNotFoundException {

	    home.selectMenu(MainMenu.PHARMACY_DIRECTORY);
		Thread.sleep(2000);
		String actualURL=driverBase.getWebDriver().getCurrentUrl();
		assertEquals((baseURL+PharmacyURL),actualURL);
	}

	//This test clicks on the FormularyMaintenance icon on the homepage to navigate to the correct FormularyMaintenance URL
    @Test
    @Order(4)
    @Description("3875 - RxCC Application Header - UI Tenant Screen - Provider, Intervention and Pharmacy Links")
    public void testInterventionQueue() throws InterruptedException, ElementNotFoundException {
        
        home.selectMenu(MainMenu.INTERVENTION_QUEUE);
        Thread.sleep(2000);
        String actualURL=driverBase.getWebDriver().getCurrentUrl();
        assertEquals((baseURL+InterventionQueueURL),actualURL);
    }
	
	//This test clicks on the FormularyMaintenance icon on the homepage to navigate to the correct FormularyMaintenance URL
	@Test
	@Order(5)
	@Description("3875 - RxCC Application Header - UI Tenant Screen - Provider, Intervention and Pharmacy Links")
	public void testFormularyMaintenance() throws InterruptedException, ElementNotFoundException {
	  
	    home.selectMenu(MainMenu.FORMULARY_MAINTENANCE);
		Thread.sleep(2000);
		String actualURL=driverBase.getWebDriver().getCurrentUrl();
		assertEquals((baseURL+FormularyMaintenanceURL),actualURL);
	}
	
	//This test clicks on the Simulations icon on the homepage to navigate to the correct Simulations URL
	@Test
	@Order(6)
	@Description("3875 - RxCC Application Header - UI Tenant Screen - Provider, Intervention and Pharmacy Links")
	public void testSimulations() throws InterruptedException, ElementNotFoundException {
	    
	    home.selectMenu(MainMenu.SIMULATION_QUEUE);
		Thread.sleep(2000);
		String actualURL=driverBase.getWebDriver().getCurrentUrl();
		assertEquals((baseURL+SimulationsURL),actualURL);
	}
	
	//This test clicks on the Fax-Discrepancies icon on the homepage to navigate to the correct Fax-Discrepancies URL
	@Test
	@Order(7)
	@Description("3875 - RxCC Application Header - UI Tenant Screen - Provider, Intervention and Pharmacy Links")
	public void testFaxDiscrepancies() throws InterruptedException, ElementNotFoundException {
	    
	    home.selectMenu(MainMenu.FAX_DISCREPANCY_QUEUE);
		Thread.sleep(2000);
		String actualURL=driverBase.getWebDriver().getCurrentUrl();
		assertEquals((baseURL+FaxDiscrepanciesURL),actualURL);
	}

}

