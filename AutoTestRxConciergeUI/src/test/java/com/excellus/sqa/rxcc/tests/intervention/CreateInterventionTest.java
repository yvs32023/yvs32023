/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.intervention;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.InterventionRuleQueries;
import com.excellus.sqa.rxcc.dto.InterventionRule;
import com.excellus.sqa.rxcc.pages.interventionrule.CreateInterventionPO;
import com.excellus.sqa.rxcc.pages.interventionrule.InterventionRuleLibraryPO;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;

/**
 * @author Neeru Tagore (ntagore)
 * @since 10/06/2022
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_FULL_SINGLE","RXCC_FULL_LOA","RXCC_FULL_MED"})
@Tag("ALL")
@Tag("INTERVENTION")
@Tag("CREATE_INTERVENTION")
public class CreateInterventionTest extends RxConciergeUITestBase {
    private static final Logger logger = LoggerFactory.getLogger(CreateInterventionTest.class);
    static CreateInterventionPO createInterventionPO;
    static InterventionRuleLibraryPO interventionRuleLibraryPO;
    static PageConfiguration pageConfiguration;

    static List<InterventionRule> expectedInterventionRule;
    static List<InterventionRule> actualInterventionRule;
     
    private static String interventionRuleId;
    private static String interventionId;
    
    final String INTERVENTION_RULE_NAME = "AutoIntervention";

    @BeforeAll
    public static void setup() throws ElementNotFoundException

    {
        pageConfiguration = (PageConfiguration) BeanLoader.loadBean("memberPage");
        interventionRuleLibraryPO = new InterventionRuleLibraryPO(driverBase.getWebDriver(), pageConfiguration);

        interventionRuleLibraryPO.clickRuleLibrary();
        createInterventionPO = new CreateInterventionPO(driverBase.getWebDriver(), pageConfiguration);
        
    }  
    @Test
    @Order(1)
    @DisplayName("88879: RxCC Intervention-Create Intervention")
    public void createIntervention() throws ElementNotFoundException{
        
        
        //  step 1
        createInterventionPO.clickAddNewInterventionRuleLibrary();
        SeleniumPageHelperAndWaiter.pause(500);
        createInterventionPO.selectInterventionType();
        SeleniumPageHelperAndWaiter.pause(500);
        createInterventionPO.clickAddTarget();

        // step 2 - Search by GPI
        createInterventionPO.enterGPI("12405010001810");
        SeleniumPageHelperAndWaiter.pause(5000);
        createInterventionPO.clickSave();
        createInterventionPO.clickRightIcon();

        // Step 3 - Skip PreRequisite
        createInterventionPO.clickRightIcon();

        //Step 4 - Select Alternative Drug
        createInterventionPO.clickAddAlternative();
        createInterventionPO.enterGPI("12405010000330");
        SeleniumPageHelperAndWaiter.pause(5000);
        createInterventionPO.clickSave();
        SeleniumPageHelperAndWaiter.pause(500);
        createInterventionPO.clickIconFilterCriteria();
        SeleniumPageHelperAndWaiter.pause(1000);

        // Copy Product Name
        createInterventionPO.clickCopyProductName();
        createInterventionPO.enterTextFaxAlternative("Acyclovir Oral Tablet 800 MG");
        createInterventionPO.clickRightIcon();
        SeleniumPageHelperAndWaiter.pause(500);
        //Skip Select Criteria
        createInterventionPO.clickRightIcon();

        // Step 5- Add Formulary
        createInterventionPO.clickAddFormulary();
        SeleniumPageHelperAndWaiter.pause(500);
        createInterventionPO.clickAddFormularyData();
        
        //go to next page
        createInterventionPO.clickRightIcon();

        //Step 6 -Intervention Type
        createInterventionPO.filterInterventionType();

        createInterventionPO.enterRuleName(INTERVENTION_RULE_NAME);
        createInterventionPO.clickRightIcon();

        //Skip Quality Fax Form

        createInterventionPO.clickRightIcon();
        //Step 7 - Save
        
        createInterventionPO.clickReviewSave();
        
        createInterventionPO.closeIcon();
        
        //Step 8 - Select Interventio Rule Library to validate the added new intervention
        interventionRuleLibraryPO.clickRuleLibrary();

   }
    
    //Hus Zia test case  PBI #113911
    @Test
    @Order(3)
    @DisplayName("26816: RxCC Intervention Wizard - Fax Form - Preview Fax (Cost Savings)")
    public void createInterventionFax() throws ElementNotFoundException, IOException{
        
        //  step 1
        createInterventionPO.clickAddNewInterventionRuleLibrary();
        SeleniumPageHelperAndWaiter.pause(500);
        createInterventionPO.selectInterventionType();
        SeleniumPageHelperAndWaiter.pause(500);
        createInterventionPO.clickAddTarget();

        // step 2 - Search by GPI
        createInterventionPO.enterGPI("12405010001810");
        SeleniumPageHelperAndWaiter.pause(5000);
        createInterventionPO.clickSave();
        createInterventionPO.checkShowOnFax();
        createInterventionPO.clickRightIcon();

        // Step 3 - Skip PreRequisite
        createInterventionPO.clickAddPreReq();
        createInterventionPO.enterGPI("12405010102030");
        SeleniumPageHelperAndWaiter.pause(5000);
        createInterventionPO.clickSave();
        createInterventionPO.checkShowOnFax();
        createInterventionPO.clickRightIcon();

        //Step 4 - Select Alternative Drug
        createInterventionPO.clickAddAlternative();
        createInterventionPO.enterGPI("12405010000330");
        SeleniumPageHelperAndWaiter.pause(5000);
        createInterventionPO.clickSave();
        SeleniumPageHelperAndWaiter.pause(500);
        createInterventionPO.clickIconFilterCriteria();
        SeleniumPageHelperAndWaiter.pause(1000);

        // Copy Product Name
        createInterventionPO.clickCopyProductName();
        String faxAltText = "Acyclovir Oral Tablet 800 MG";
        createInterventionPO.enterTextFaxAlternative(faxAltText);
        createInterventionPO.enterAverLenOfTherapy("5");
        createInterventionPO.clickRightIcon();
        SeleniumPageHelperAndWaiter.pause(500);
        //Skip Select Criteria
        createInterventionPO.clickRightIcon();

        // Step 5- Add Formulary
        createInterventionPO.clickAddFormulary();
        SeleniumPageHelperAndWaiter.pause(500);
        createInterventionPO.clickAddFormularyData();
        
        //go to next page
        createInterventionPO.clickRightIcon();

        //Step 6 -Intervention Type
        createInterventionPO.filterInterventionTypeFax();

        createInterventionPO.enterRuleName(INTERVENTION_RULE_NAME);
        createInterventionPO.clickRightIcon();
        
        //Quality Fax Form
        String savingsText = "This is a test for Savings Information";
        createInterventionPO.enterSavingsInformation(savingsText);
        createInterventionPO.clickPreviewFax();
        createInterventionPO.retrieveEmdededPDFData();
        
        String pdfUrl = createInterventionPO.retrieveEmdededPDFData();
        try {

        	String pdfContent=getPdfContent(pdfUrl);

        	assertTrue(pdfContent.contains("We are continually working with our members and their providers to improve affordable access to prescription drugs. \r\n"
        			+ "After conducting a review, a potential medication cost-savings opportunity was identified for one of your patients and we \r\n"
        			+ "would appreciate your review to help evaluate if switching to a cost-saving medication is appropriate for your patient.\r\n"
        			+ "If you have received this communication in error or would like to opt out call the number listed above."));
        	assertTrue(pdfContent.contains("If Accepting, please send a new prescription to the patient's pharmacy and cancel the patient's current \r\n"
        			+ "prescription. To avoid patient confusion, we ask you to communicate medication change(s) to your \r\n"
        			+ "patient."));
        	assertTrue(pdfContent.contains("If Declining, please provide rationale below:"));
        	assertTrue(pdfContent.contains("Excellus Health Plan\r\n"
        			+ "Rochester NY\r\n"
        			+ "5857579999"));
        	assertTrue(pdfContent.contains("Kev Hince\r\n"
        			+ "02/19/1997\r\n"
        			+ "202394248"));
        	assertTrue(pdfContent.contains("Please consider changing to one of the alternatives listed below for this patient, if clinically appropriate:"));
        	assertTrue(pdfContent.contains("Choosing a lower-cost/suggested product reduces overall healthcare cost, maintains clinical efficacy and may reduce \r\n"
        			+ "patient out-of-pocket costs."));
        	assertTrue(pdfContent.contains("If you have questions and would like to speak to a member of our staff, please call 1-888-905-0968."));
        	assertTrue(pdfContent.contains("Once you have reached a decision (above), please fax this document to 1-585-327-3509."));
        	assertTrue(pdfContent.contains(savingsText));
        	assertTrue(pdfContent.contains(faxAltText));
        }
        	catch (IOException e) {

        		e.printStackTrace();
        		}
     	
        SeleniumPageHelperAndWaiter.pause(3000);
        createInterventionPO.clickClosePDF();
    }
    
    @TestFactory
    @Order(2)
    @DisplayName("Search by Rule Name- for Newly added Intervention Rule")
    public List<DynamicNode> interventionRuleSearch() throws ElementNotFoundException
    {

        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        String searchTerm = INTERVENTION_RULE_NAME; 
        
        String whereClauseExtension ="where c.ruleName =\"" + INTERVENTION_RULE_NAME + "\"";
              
        expectedInterventionRule = InterventionRuleQueries.getInterventionRules(whereClauseExtension); // list of  the record based on the where clause
        
        interventionRuleId = expectedInterventionRule.get(0).getRuleId();
        logger.info("***interventionRuleId**** 1 ****" + interventionRuleId);
        interventionId = expectedInterventionRule.get(0).getId();
        logger.info("***interventionId**** 1 ****" + interventionId);
        
        Map<String, List<String>> filters = new HashMap<String, List<String>>();
        filters.put("Rule Name", Arrays.asList(INTERVENTION_RULE_NAME));
        
        logger.info("***Search result expected value**** " + expectedInterventionRule);

        //UI 
        interventionRuleLibraryPO.RuleLibrarySearch(searchTerm.toUpperCase());
        SeleniumPageHelperAndWaiter.pause(2000);
        actualInterventionRule = interventionRuleLibraryPO.retrieveRuleLibraryInfo();
        
        logger.info("***Search result actual value**** " + actualInterventionRule);

        for ( InterventionRule expected : expectedInterventionRule )
        { 
            boolean found = false; 
            for ( InterventionRule actual : actualInterventionRule )
            {
                if ( expected.getRuleName().equals(actual.getRuleName()) )
                {
                    tests.add(dynamicContainer("Rule Id " + expected.getRuleId(), expected.compareUI(actual)));
                    found = true;
                    
                    break;
                }
            }

            if ( !found )
            {
                tests.add(dynamicTest("Rule Id " + expected.getRuleId() + " is missing from UI search result", 
                        () -> fail("InterventionRule [" + expected.toString() + "] not found in UI search result")));
            }
        }
        return tests;
    }
    
    
    public String getPdfContent(String dataValue) throws IOException {
    	final String BEGINNING = "data:application/pdf;base64,";
			// Convert 64bit data to PDF file
		byte[] decoder = Base64.decodeBase64(StringUtils.substringAfter(dataValue, BEGINNING));
    	InputStream targetStream = new ByteArrayInputStream(decoder);
       // InputStream is=pdfURL.openStream();
    	BufferedInputStream bis=new BufferedInputStream(targetStream);
    	PDDocument doc=PDDocument.load(bis);
    	int pages=doc.getNumberOfPages();
    	System.out.println("The total number of pages "+pages);
    	PDFTextStripper strip=new PDFTextStripper();
    	strip.setStartPage(1);
    	strip.setEndPage(2);
    	String stripText=strip.getText(doc);
    	System.out.println(stripText);
    	targetStream.close();
    	bis.close();
    	doc.close();
    	return stripText;
    }

    @AfterAll
    public static void cleanUp()
    {
        // TODO need to delete the index on intervention     

        // Work around since the delete of interversion leaves orphan record in the cogn search intervention
        given()
            .relaxedHTTPSValidation()
            .headers( getGenericHeaders() )
            .and()
            .delete("api/intervention-rule/intervention-rules/{id}", interventionId)
            .then()
            .assertThat().statusCode(204);
    }   
	
}
