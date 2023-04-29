/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.pharmacy;




import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.PharmacyQueries;
import com.excellus.sqa.rxcc.dto.Pharmacy;
import com.excellus.sqa.rxcc.pages.pharmacy.PharmacyDemographicPO;
import com.excellus.sqa.rxcc.steps.pharmacy.OpenPharmacyStep;
import com.excellus.sqa.rxcc.steps.pharmacy.RetrievePharmacyDemographicStep;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.step.IStep.Status;

/**
 * 
 *
 * @author Neeru Tagore (ntagore)
 * @since 03/23/2022
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_FULL_SINGLE"})
@Tag("ALL")
@Tag("PHARMACY_DIRECTORY") 
public class PharmacyDemographicTest extends RxConciergeUITestBase
{
    
    @TestFactory  
    @Test
    @DisplayName ("5779-UI Display Pharmacy Demographics") 
    public List<DynamicNode> pharmacyDemographic() throws ElementNotFoundException {

    	List<DynamicNode> tests = new ArrayList<DynamicNode>();
    	
        //Create expected pharmacy DTO

    	// STEP 1 - get random Pharmacy
        String whereClauseExtension = "where IS_DEFINED (c.address2) and NOT IS_NULL(c.address2) and IS_DEFINED(c.faxNumber) and NOT IS_NULL(c.faxNumber)";
        Pharmacy expectedPharmacy = PharmacyQueries.getPharmacy(whereClauseExtension);
        
        // STEP 2 - open the pharmacy demographic
        OpenPharmacyStep openPharmacyStep = new OpenPharmacyStep(driverBase.getWebDriver(), expectedPharmacy.getNpi());
        openPharmacyStep.run();
        tests.addAll(openPharmacyStep.getTestResults());
        
        if ( openPharmacyStep.stepStatus() != Status.COMPLETED )
        	return tests;

        // STEP 3 - Retrieve pharmacy demographic information
        RetrievePharmacyDemographicStep retrievePharmacyStep = new RetrievePharmacyDemographicStep(driverBase.getWebDriver());
        retrievePharmacyStep.run();
        tests.addAll(retrievePharmacyStep.getTestResults());
        
        if ( retrievePharmacyStep.stepStatus() != Status.COMPLETED )
        	return tests;
        
        // STEP 4 - Validate pharmacy data
        Pharmacy actualPharmacy = retrievePharmacyStep.getPharmacy();
        tests.addAll( expectedPharmacy.compareUI(actualPharmacy) );
        
        // STEP 5 - Collapse/Expand
        PharmacyDemographicPO pharmacyDemographicPO = new PharmacyDemographicPO(driverBase.getWebDriver(), BeanLoader.loadBean("pharmaciesPage", PageConfiguration.class)); 
        pharmacyDemographicPO.isCollapsedPharmacyInformation();
        SeleniumPageHelperAndWaiter.pause(2000);
        pharmacyDemographicPO.isExpandedPharmacyInformation();

        return tests;
    }
   
    
}
