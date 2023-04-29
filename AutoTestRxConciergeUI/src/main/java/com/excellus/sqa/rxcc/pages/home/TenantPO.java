/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.home;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;
import com.excellus.sqa.selenium.page.IPage;

/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 01/17/2022
 */
@Tag("ALL")
public class TenantPO extends AbstractCommonPage implements IPage
{
	
	private static final Logger logger = LoggerFactory.getLogger(TenantPO.class);
	//Declared Variables
	
	@FindBy(xpath = "//span[1][contains(text(),'Excellus Employee')]")
	private WebElement labelEXECard;
	
	@FindBy(xpath = "//rxc-tenant-grid//div[text()='Excellus Health Plan']")
	private WebElement labelEHPCard;
	
	@FindBy(xpath = "//rxc-tenant-grid//div[text()='LBS Out Of Area']")
	private WebElement labelOOACard;
	
	@FindBy(xpath = "//rxc-tenant-grid//div[text()='Excellus BCBS Medicare']")
	private WebElement labelBCBSCard;
	
	@FindBy(xpath = "//input[@placeholder='Search Tenants']")	// GC (02/22/22) updated base on UI changes
	private WebElement textboxSearchTenant;
	
	@FindBy(xpath = "//rxc-tenant-grid//div[@tabindex='0']/div/div/div[1]")	// GC (02/22/22) updated base on UI changes
	private List<WebElement> labelTenantName;
	
	@FindBy(xpath = "//rxc-tenant-grid//div//span[1][contains(text(),'Phone:')]")	// HZ Tenant phone
	private WebElement labelTenantPhone;
	
	@FindBy(xpath = "//rxc-tenant-grid//div//span[2][contains(text(),'Fax:')]")	// HZ Tenant phone
	private WebElement labelTenantFax;
	
	
	//Page configuration
	public TenantPO(WebDriver driver, PageConfiguration page) 
	{
		super(driver, page);
		waitForPageObjectToLoad();
	}

	@Override
	public void waitForPageObjectToLoad() {
		SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, textboxSearchTenant);
	}

    /**
     * Search for Tenant Selection
     * Select the text by tenant name
     * @throws ElementNotFoundException if issue occurs
     */
    public void tenantSearchSelection(Tenant.Type type) throws ElementNotFoundException {
        if (type == Tenant.Type.EHP) {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, labelEHPCard);
        }else if (type == Tenant.Type.EXE) {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, labelEXECard);
        }else if (type == Tenant.Type.LOA) {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, labelOOACard);
        }else if (type == Tenant.Type.MED) {
            SeleniumPageHelperAndWaiter.clickWebElement(driver, labelBCBSCard);
        }
        SeleniumPageHelperAndWaiter.pause(1000);
    }
    
    
    /**
     * Search for Tenant and return Phone
     * Select the text by tenant name
     * @return 
     * @throws ElementNotFoundException if issue occurs
     */
    public String tenantPhoneSearchSelection(String Tenid) throws ElementNotFoundException {
    	try {
			boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, textboxSearchTenant, 5);
			if ( displayed )
				SeleniumPageHelperAndWaiter.clickWebElement(this, textboxSearchTenant);
			SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxSearchTenant, Tenid);
			return retrieveFirstTenantPhone();
			
			
			
		}
		catch (Exception e)
		{
			logger.error("Tenant Search element not found");
			return null;
			
		}
		
	}

	//Method used to select Excellus Employee Tenant Card
	
	public void selectTenant(WebElement tenantName) {
		try {
			boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, tenantName, 5);
			if ( displayed )
				SeleniumPageHelperAndWaiter.clickWebElement(this, tenantName);
			
			
		}
		catch (Exception e)
		{
			logger.error("Excellus Employee Tenant not found");
			
		}
	
	}


	public String searchTenant(String Tenid) {
		try {
			boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, textboxSearchTenant, 5);
			if ( displayed )
				SeleniumPageHelperAndWaiter.clickWebElement(this, textboxSearchTenant);
			SeleniumPageHelperAndWaiter.enterTextByWebElement(this, textboxSearchTenant, Tenid);
			return retrieveFirstTenant();
			
			
			
		}
		catch (Exception e)
		{
			logger.error("Tenant Search element not found");
			return null;
			
		}
		
	}
	
	public String retrieveFirstTenant() throws ElementNotFoundException {
		List<String> listTenants= retrieveTenants();
		if (listTenants.size()>0) {
			return listTenants.get(0);
		}
		return null;
	}

	public List <String> retrieveTenants() throws ElementNotFoundException{
		return SeleniumPageHelperAndWaiter.retrieveWebElementsText(this, labelTenantName);
	}
	
	//Phone number

	public String retrieveFirstTenantPhone() throws ElementNotFoundException {
		String listTenants= retrieveTenantPhoneNumber();
		return listTenants;

	}

	public String retrieveTenantPhoneNumber() throws ElementNotFoundException{
		return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelTenantPhone);
	}
	
	//Fax number

	public String retrieveFirstTenantFax() throws ElementNotFoundException {
		String listTenants= retrieveTenantFaxNumber();
		return listTenants;

	}

	public String retrieveTenantFaxNumber() throws ElementNotFoundException{
		return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelTenantFax);
	}
}



	


	


