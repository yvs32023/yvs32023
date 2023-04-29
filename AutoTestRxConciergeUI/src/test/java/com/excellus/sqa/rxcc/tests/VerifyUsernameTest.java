/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.pages.home.MainMenuPO;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;

/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 01/28/2022
 */

@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_READ_MULTI", "RXCC_OPS_MULTI", "RXCC_REPORT_MULTI",
		"RXCC_FULL_SINGLE", "RXCC_READ_SINGLE", "RXCC_OPS_SINGLE", "RXCC_REPORT_SINGLE",
		"RXCC_FULL_LOA", "RXCC_READ_LOA", "RXCC_OPS_LOA", "RXCC_REPORT_LOA",
		"RXCC_FULL_MED", "RXCC_READ_MED", "RXCC_OPS_MED", "RXCC_REPORT_MED"})
@Tag("ALL")
public class VerifyUsernameTest extends RxConciergeUITestBase{

	//This Test logs into the RX CC application with provided user names and asserts that the correct user name shows up under the user menu
	@Test
	public void verifyUsernameTest() throws ElementNotFoundException {
		
		//Page configuration
		PageConfiguration loginPage = (PageConfiguration) BeanLoader.loadBean("homePage");
		MainMenuPO mainMenuPO = new MainMenuPO(driverBase.getWebDriver(), loginPage);
		
		String ActualUsername= mainMenuPO.retrieveUsername();
		String ExpectedUsername= acctName;
		//Assertion of correct user name
		assertEquals(ActualUsername,ExpectedUsername);
		

	}

	
}

