/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.pages.member;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.PhoneNumber;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.page.AbstractCommonPage;

/**
 * Page object that handles member demographics
 * @author Neeru Tagore(ntagore)
 * @since 02/18/2022
 */

public class MemberDemographicPO extends AbstractCommonPage {

    private static final Logger logger = LoggerFactory.getLogger(MemberDemographicPO.class);

    private final String sectionMemberDemographicXpath = "//*[@data-testid='member-details-demographics-accordion']";

    @FindBy(xpath = sectionMemberDemographicXpath)
    private WebElement sectionMemberDemographic;

    @FindBy(xpath = sectionMemberDemographicXpath + "//a[text()='Member Demographics']")
    private WebElement headerName;

    @FindBy(xpath = "//*[@role='region']/div/div/span[1]")
    private WebElement labelMemberName;

    @FindBy(xpath = sectionMemberDemographicXpath + "//dt[text()='Subscriber ID:']/following-sibling::dd[1]")
    private WebElement labelSubscriberIdDependentCode;

    @FindBy(xpath = "//div[@role='region']//div//form//p//span")
    private WebElement labelTermed;

    @FindBy(xpath = "//dt[text()='Unique ID:']/following-sibling::dd[1]")
    private WebElement labelUniqueId;

    @FindBy(xpath = "//dt[text()='DOB:']/following-sibling::dd/time")
    private WebElement labelDob;

    @FindBy(xpath = "//dt[text()='Gender:']/following-sibling::dd[1]")
    private WebElement labelGender;

    private final String labelAddress1Xpath = "//*/p-accordiontab[1]/div/div[2]/div/form/address/dl[1]/dd";
    @FindBy(xpath = labelAddress1Xpath)
    private WebElement labelAddress1;

    @FindBy(xpath = "//*/dt[contains(text(), 'Address')]/following-sibling::dd[1]/span[1]")
    private WebElement labelAddress2;

    @FindBy(xpath = "//*/dt[contains(text(), 'Address')]/following-sibling::dd[1]/span[2]")
    private WebElement labelAddress3;

    @FindBy(xpath = "//dt[text()='City:']/following-sibling::dd[1]")
    private WebElement labelCity;

    @FindBy(xpath = "//dt[text()='State:']/following-sibling::dd[1]")
    private WebElement labelState;

    @FindBy(xpath = "//dt[text()='Postal Code:']/following-sibling::dd[1]")
    private WebElement labelPostalCode;

    @FindBy(xpath = "//*/dt[contains(text(), 'Phone')]/following-sibling::dd[1]")
    private List<WebElement> labelPhoneNumbers;

    @FindBy(xpath = "//*/dt[contains(text(), 'Phone')]/following-sibling::dd[1]/p[1]")
    private WebElement homePhoneNumber;

    @FindBy(xpath = "//*/dt[contains(text(), 'Phone')]/following-sibling::dd[1]/p[2]")
    private WebElement cellPhoneNumber;

    @FindBy(xpath = "//dt[text()='Email:']/following-sibling::dd[1]")
    private WebElement labelEmail;

    @FindBy(xpath = "//dt[text()='Opted into Communication:']/following-sibling::dd[1]/span[1]")
    private WebElement labelOptedOutOfCommunication;

    @FindBy(xpath = "//*/dt[contains(text(), 'Correspondence Type')]/../dd[2]/span")
    private WebElement labelCorrespondenceType;

    @FindBy(xpath = "//p-accordiontab[1]//div[1]//div[1]//a[1]//span[1]")
    private WebElement expandCollapseMemberDemographic;

 //   @FindBy(xpath = "//body//rxc-app//rxc-member-details-panel//span//span[1]")
    @FindBy(xpath ="//button[@data-testid='member-details-edit-button']")  //updated xpath NT@ 03/20/23
    private WebElement iconMemberDetailEdit;

    @FindBy(xpath = "//body//rxc-app//rxc-member-details-panel//form//span[2]")
    private WebElement labelDeceased;

    @FindBy(xpath = "//body//rxc-app//form//p//span[2]")
    private WebElement valueDeceased;

    @FindBy(xpath = "//p-checkbox[@formcontrolname='optIntoCommunication']//div[2]")
    private WebElement checkboxOptIntoCommunication;

    @FindBy(xpath = "//*/p-accordiontab[@data-testid='member-details-demographics-accordion']/div/div[@role='region']/div/form/dl/dd[1]/span[1]")
    private WebElement checkboxOptIntoCommunicationValue;

    @FindBy(xpath = "//*[@id=\\\"p-accordiontab-0-content\\\"]/div/form/dl[2]/dd[2]/p-dropdown/div/div[2]/span")
    private WebElement clickEditCorrespondenceType;

    @FindBy(xpath = "//rxc-member-details-panel//button[2]//span[1]")
    private WebElement buttonSave;

    @FindBy(xpath = "//*[@id=\"p-accordiontab-0-content\"]/div/p/span/p/button[1]")
    private WebElement buttonCancel;

    @FindBy(xpath = "//*/form/*/dt[contains(text(), 'Correspondence Type')]/../dd[2]")
    private WebElement retrieveSubscriberCorrespondenceType;

    @FindBy(xpath = "//dt[text()='RxCC Group Name:']/following-sibling::dd[1]")
    private WebElement labelRxccGroupName;

    @FindBy(xpath = "//dt[text()='RxCC Group ID:']/following-sibling::dd[1]")
    private WebElement labelRxccGroupId;

    @FindBy(xpath = "//dt[text()='Reporting Group 1:']/following-sibling::dd[1]")
    private WebElement labelReportingGroup1;

    @FindBy(xpath = "//dt[text()='Reporting Group 2:']/following-sibling::dd[1]")
    private WebElement labelReportingGroup2;

    @FindBy(xpath = "//dt[text()='Reporting Group 3:']/following-sibling::dd[1]")
    private WebElement labelReportingGroup3;

    @FindBy(xpath = "//dt[text()='Funding Arrangement:']/following-sibling::dd[1]")
    private WebElement labelFundingArrangement;

    @FindBy(xpath = "//p-accordiontab[@data-testid='member-details-groupInfo-accordion']//span[1]")
    private WebElement expandCollapseGroupInfoPanel;

    @FindBy(xpath = "//*/form/*/dt[contains(text(), 'Correspondence Type')]/../dd[2]")
    private WebElement selectSubscriberCorrespondenceType;

    private By buttonNoteAlertExit = By.xpath("//p-toastitem//button[contains(@class, 'p-toast-icon-close')]");

    private String dropdownCorrespondentTypeXpath = "//*/p-dropdownitem/li/span[contains(text( ), '%s')]";

    @FindBy(xpath = "//dt[text()='RX Product Description:']/following-sibling::dd[1]")
    private WebElement labelRxProductDescription;

    @FindBy(xpath = "//dt[text()='Formulary:']/following-sibling::dd[1]")
    private WebElement labelFormulary;

    @FindBy(xpath = "//dt[text()='Carrier ID Description:']/following-sibling::dd[1]")
    private WebElement labelCarrierIdDescription;

    @FindBy(xpath = "//p-accordiontab[@data-testid='member-details-planInfo-accordion']//span[1]")
    private WebElement expandCollapsePlanInfoPanel;

    /**
     * Constructor
     *
     * @param driver WebDriver for PageObject
     * @param page   PageConfiguration for the UI page
     */

    public MemberDemographicPO(WebDriver driver, PageConfiguration page) {
        super(driver, page);
        waitForPageObjectToLoad();
    }

    @Override
    public void waitForPageObjectToLoad() {
        logger.debug("Waiting for member demographic to load");
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, sectionMemberDemographic);
        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, headerName);
    }

    /**
     * Retrieve the text of the member demographic WebElement
     * 
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */

    public Member retrieveMemberInfo() throws ElementNotFoundException {
        Member member = new Member();
        String temp = retrieveSubscriberIdDependentCode();

        member.setFirstLastName(retrieveMemberName());
        member.setSubscriberId(StringUtils.substringBefore(temp, " "));
        member.setDependentCode(StringUtils.substringAfter(temp, " "));
        member.setUpi(retrieveSubscriberUniqueId());
        member.setDateBirth(retrieveSubscriberDob());
        member.setGender(retrieveSubscriberGender());
        member.setAddress1(retrieveSubscriberAddress1());
        member.setAddress2(retrieveSubscriberAddress2());
        member.setAddress3(retrieveSubscriberAddress3());
        member.setCity(retrieveSubscriberCity());
        member.setState(retrieveSubscriberState());
        member.setPostalCode(retrieveSubscriberPostalCode());
        member.setPhoneNumbers(retrievePhoneNumbers());
        member.setEmail(retrieveSubscriberEmail());
        member.setOptinComm(retrieveSubscriberOptedOutOfCommunication());
        member.setCorrespondenceType(retrieveSubscriberCorrespondenceType());

        String temp2 = retrieveGroupInfoFundingDetails();
        String fundingID = StringUtils.substringBefore(temp2, "-");
        String fundingDescr = StringUtils.substringAfter(temp2, "-");

        member.setGroupName(retrieveSubscriberGroupName());
        member.setGroupId(retrieveSubscriberGroupId());
        member.setReportGroup1(retrieveSubscriberReportingGroup1());
        member.setReportGroup2(retrieveSubscriberReportingGroup2());
        member.setReportGroup3(retrieveSubscriberReportingGroup3());
        member.setFundingArrangementId(StringUtils.trim(fundingID));
        member.setFundingArrangementDescr(StringUtils.trim(fundingDescr));

        String temp3 = retrieveFormularyInfo();
        String formularyCode = StringUtils.substringBefore(temp3, ",");
        String formularyDescription = StringUtils.substringAfter(temp3, ",");

        member.setVendorFormularyCode(StringUtils.trim(formularyCode));
        member.setVendorFormularyDescr(StringUtils.trim(formularyDescription));

        member.setRxBenefitDescr(retrieveRxProductDescription());
        member.setCarrierName(formularyDescription);

        return member;
    }

    public String retrieveMemberName() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelMemberName);
    }

    public String retrieveSubscriberUniqueId() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelUniqueId);
    }

    public String retrieveSubscriberDob() throws ElementNotFoundException {
        String dob = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelDob);

        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");

        try {
            Date date = formatter.parse(dob);
            return formatter2.format(date).toString();
        } catch (ParseException e) {
            return dob;
        }
    }

    public String retrieveSubscriberGender() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelGender);
    }

    public String retrieveSubscriberAddress1() throws ElementNotFoundException {

        String address = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelAddress1);
        String address_1 = address.split("\n")[0];
        return address_1;
    }

    public String retrieveSubscriberAddress2() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelAddress2);
    }

    public String retrieveSubscriberAddress3() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelAddress3);
    }

    public String retrieveSubscriberCity() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelCity);
    }

    public String retrieveSubscriberState() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelState);
    }

    public String retrieveSubscriberPostalCode() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelPostalCode);
    }

    public String retrieveSubscriberEmail() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelEmail);
    }

    public boolean retrieveSubscriberOptedOutOfCommunication() throws ElementNotFoundException {

        String value = SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelOptedOutOfCommunication);

        if (value.equalsIgnoreCase("Yes"))
            return true;
        else {
            return false;
        }

    }

    public String retrieveSubscriberGroupName() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelRxccGroupName);
    }

    public String retrieveSubscriberGroupId() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelRxccGroupId);
    }

    public String retrieveSubscriberReportingGroup1() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelReportingGroup1);
    }

    public String retrieveSubscriberReportingGroup2() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelReportingGroup2);
    }

    public String retrieveSubscriberReportingGroup3() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelReportingGroup3);
    }

    public String retrieveGroupInfoFundingDetails() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelFundingArrangement);
    }

    /**
     * Retrieve the text of the field SubscriberPhoneList label WebElement
     * 
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */

    public List<PhoneNumber> retrievePhoneNumbers() throws ElementNotFoundException {

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
        
        String  phoneHome =  SeleniumPageHelperAndWaiter.retrieveWebElementText(this, homePhoneNumber);  
        
        if ( StringUtils.isNotBlank(phoneHome) && StringUtils.containsIgnoreCase(phoneHome, "Home") )
        {
            PhoneNumber phoneNumber = new PhoneNumber();
            
            String[] phoneSplit1 = phoneHome.split(" ");
            String phoneType1 = phoneSplit1[0];
            String phoneNum1 = phoneSplit1[1];
            
            phoneNumber.setType(phoneType1);
            phoneNumber.setNumber(phoneNum1);
            
            phoneNumbers.add(phoneNumber);
        }
        
        
        String  phoneCell =  SeleniumPageHelperAndWaiter.retrieveWebElementText(this, cellPhoneNumber);
        
        if ( StringUtils.isNotBlank(phoneCell) && StringUtils.containsIgnoreCase(phoneCell, "Cell") )
        {
            PhoneNumber phoneNumber = new PhoneNumber();
            
            String[] phoneSplit1 = phoneCell.split(" ");
            String phoneType1 = phoneSplit1[0];
            String phoneNum1 = phoneSplit1[1];
            
            phoneNumber.setType(phoneType1);
            phoneNumber.setNumber(phoneNum1);
            
            phoneNumbers.add(phoneNumber);
        }
 
        return phoneNumbers;
    }   
    
    /**
     * Retrieve the text of the field SubscriberIdDependentCode label WebElement
     * 
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public String retrieveSubscriberIdDependentCode() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelSubscriberIdDependentCode);
    }

    /**
     * Retrieve the text of the label Rx Product Description WebElement
     * 
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public String retrieveRxProductDescription() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelRxProductDescription);
    }

    /**
     * Retrieve the text of the label Formulary WebElement
     * 
     * @return String that represents the text of the label WebElement
     * @throws ElementNotFoundException if issue occurs
     */
    public String retrieveFormularyInfo() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, labelFormulary);
    }

    /**
     * Expand or collapse the member demographic
     * 
     * @param expand the member demographic if true. Otherwise collapse it if it is
     *               false
     * @throws ElementNotFoundException if there is an issue
     */
    public void expandCollapseMemberDemographic(boolean expand) throws ElementNotFoundException {
        final String COLLAPSED = "-right";
        final String EXPANDED = "-down";

        String classAttr = SeleniumPageHelperAndWaiter.retrieveAttribute(this, expandCollapseMemberDemographic,
                "class");

        if (expand && StringUtils.endsWithIgnoreCase(classAttr, COLLAPSED)) {
            SeleniumPageHelperAndWaiter.clickWebElement(this, expandCollapseMemberDemographic);
            SeleniumPageHelperAndWaiter.pause(500);
        } else if (!expand && StringUtils.endsWithIgnoreCase(classAttr, EXPANDED)) {
            SeleniumPageHelperAndWaiter.clickWebElement(this, expandCollapseMemberDemographic);
            SeleniumPageHelperAndWaiter.pause(500);
        }
    }

    /**
     * Check if the Member Demographic section is expanded or collapsed
     * 
     * @return true if expanded otherwise return false
     * @throws ElementNotFoundException
     */
    public boolean isExpandCollapseMemberDemographic() throws ElementNotFoundException {
        final String EXPANDED = "-down";

        String classAttr = SeleniumPageHelperAndWaiter.retrieveAttribute(this, expandCollapseMemberDemographic,
                "class");

        if (StringUtils.endsWithIgnoreCase(classAttr, EXPANDED)) {
            return true;
        }

        return false;
    }

    /**
     * Expand or collapse the member Group Info
     * 
     * @param expand the member Group Info if true. Otherwise collapse it if it is
     *               false
     * @throws ElementNotFoundException if there is an issue
     */
    public void expandCollapseMemberGroupInfo(boolean expand) throws ElementNotFoundException {
        final String COLLAPSED = "-right";
        final String EXPANDED = "-down";

        String classAttr = SeleniumPageHelperAndWaiter.retrieveAttribute(this, expandCollapseGroupInfoPanel, "class");

        if (expand && StringUtils.endsWithIgnoreCase(classAttr, COLLAPSED)) {
            SeleniumPageHelperAndWaiter.clickWebElement(this, expandCollapseGroupInfoPanel);
            SeleniumPageHelperAndWaiter.pause(5000);
        } else if (!expand && StringUtils.endsWithIgnoreCase(classAttr, EXPANDED)) {
            SeleniumPageHelperAndWaiter.clickWebElement(this, expandCollapseGroupInfoPanel);
            SeleniumPageHelperAndWaiter.pause(1000);
        }
    }

    /**
     * Check if the Member Group Info section is expanded or collapsed
     * 
     * @return true if expanded otherwise return false
     * @throws ElementNotFoundException
     */
     
    public boolean isExpandCollapseMemberGroupInfo() throws ElementNotFoundException {
        final String EXPANDED = "-down";

        String classAttr = SeleniumPageHelperAndWaiter.retrieveAttribute(this, expandCollapseGroupInfoPanel, "class");

        if (StringUtils.endsWithIgnoreCase(classAttr, EXPANDED)) {
            return true;
        }

        return false;
    }

    /**
     * Expand or collapse the member Plan Info section
     * 
     * @param expand the member Plan Info if true. Otherwise collapse it if it is
     *               false
     * @throws ElementNotFoundException if there is an issue
     */
    public void expandCollapseMemberPlanInfo(boolean expand) throws ElementNotFoundException {
        final String COLLAPSED = "-right";
        final String EXPANDED = "-down";

        String classAttr = SeleniumPageHelperAndWaiter.retrieveAttribute(this, expandCollapsePlanInfoPanel, "class");

        if (expand && StringUtils.endsWithIgnoreCase(classAttr, COLLAPSED)) {
            SeleniumPageHelperAndWaiter.clickWebElement(this, expandCollapsePlanInfoPanel);
            SeleniumPageHelperAndWaiter.pause(3000);
        } else if (!expand && StringUtils.endsWithIgnoreCase(classAttr, EXPANDED)) {
            SeleniumPageHelperAndWaiter.clickWebElement(this, expandCollapsePlanInfoPanel);
            SeleniumPageHelperAndWaiter.pause(1000);
        }
    }

    /**
     * Check if the Member Plan Info section is expanded or collapsed
     * 
     * @return true if expanded otherwise return false
     * @throws ElementNotFoundException
     */
    public boolean isExpandCollapseMemberPlanInfo() throws ElementNotFoundException {
        final String EXPANDED = "-down";

        String classAttr = SeleniumPageHelperAndWaiter.retrieveAttribute(this, expandCollapsePlanInfoPanel, "class");

        if (StringUtils.endsWithIgnoreCase(classAttr, EXPANDED)) {
            return true;
        }

        return false;
    }

    /**
     * Determine if the Termed label is displayed in the UI
     * 
     * @throws ElementNotFoundException if issue occurs
     */

    public boolean isTermedLabelDisplayed() throws ElementNotFoundException {
        try {
            return SeleniumPageHelperAndWaiter.isWebElementVisible(driver, labelTermed, 2);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Determine if the Member edit icon WebElement is displayed in the UI
     * 
     * @throws ElementNotFoundException if issue occurs
     */

    public void clickMemberEditIcon() {
        try {
            boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, iconMemberDetailEdit, 2);
            if (displayed)
                SeleniumPageHelperAndWaiter.clickWebElement(this, iconMemberDetailEdit);

        } catch (Exception e) {
            logger.error("Member edit icon failed");

        }
    }

    /**
     * Retrieve the current setting for the Deceased checkbox WebElement
     * 
     * @return true if the checkbox is enabled otherwise return false
     */
    public boolean isLabelDeceasedDisplayed() throws ElementNotFoundException {
        try {
            return SeleniumPageHelperAndWaiter.isWebElementVisible(driver, valueDeceased, 1);
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Set the Deceased checkbox WebElement
     * 
     * @param status true to enable the checkbox otherwise false to disable checkbox
     * @throws ElementNotFoundException if issue occurs
     */

    public void selectDeceased() throws ElementNotFoundException {
        try {
            boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, labelDeceased, 1);
            if (displayed)
                SeleniumPageHelperAndWaiter.setCheckbox(this, labelDeceased, displayed);

        } catch (Exception e) {
            logger.error("Deceased checkbox failed");

        }
    }

    /**
     * Click the save WebElement field
     * 
     * @throws ElementNotFoundException if issue occurs
     */

    public void clickSave() {
        try {
            boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, buttonSave, 2);
            if (displayed)
                SeleniumPageHelperAndWaiter.waitForWebElementThenClick(driver, buttonSave, 2);

        } catch (Exception e) {
            logger.error("Save failed");

        }
    }

    /**
     * Set the OptOutOfCommunications checkbox WebElement
     * 
     * @param status true to enable the checkbox otherwise false to disable checkbox
     * @throws ElementNotFoundException if issue occurs
     */
    public void selectOptOutOfCommunications() throws ElementNotFoundException {
        try {
            boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, checkboxOptIntoCommunication,
                    2);
            if (displayed)
                SeleniumPageHelperAndWaiter.setCheckbox(this, checkboxOptIntoCommunication, displayed);
        } catch (Exception e) {
            logger.error("Opted into Communication failed");

        }
    }

    /**
     * Retrieve the current setting for the OptOutOfCommunications checkbox
     * WebElement
     * 
     * @return true if the checkbox is enabled otherwise return false
     */

    public String valueofOptOutofCommunication() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, checkboxOptIntoCommunicationValue);

    }

    public boolean isOptOutofCommunicationChecked() throws ElementNotFoundException {
        SeleniumPageHelperAndWaiter.pause(2000);
        return SeleniumPageHelperAndWaiter.isCheckboxEnable(driver, checkboxOptIntoCommunication);
    }

    /**
     * Retrieve the current setting for the Correspondence type WebElement
     */

    public String retrieveSubscriberCorrespondenceType() throws ElementNotFoundException {
        return SeleniumPageHelperAndWaiter.retrieveWebElementText(this, retrieveSubscriberCorrespondenceType);
    }

    /**
     * Select the Correspondence Type WebElement
     * 
     * @param option this is the option that the correspondent need to be change
     *               with
     * @throws ElementNotFoundException if issue occurs
     */

    public void selectCorrespondenceType(String currentOption) throws ElementNotFoundException {

        // check if the Page is in Edit mode

        boolean displayed = SeleniumPageHelperAndWaiter.isWebElementVisible(driver, buttonCancel, 2);
        // go to edit mode
        if (displayed == false) {
            clickMemberEditIcon();
        }

        SeleniumPageHelperAndWaiter.clickWebElement(driver, selectSubscriberCorrespondenceType);

        By correspondentSelection = null;

        if (currentOption.equalsIgnoreCase("Letter")) {
            // if Letter then change to Email
            correspondentSelection = By.xpath(String.format(dropdownCorrespondentTypeXpath, "Email"));
        } else {
            // if Email then changeto Letter
            correspondentSelection = By.xpath(String.format(dropdownCorrespondentTypeXpath, "Letter"));
        }

        SeleniumPageHelperAndWaiter.waitForVisibilityOfWebElement(this, correspondentSelection);

        SeleniumPageHelperAndWaiter.clickBy(this, correspondentSelection);

        SeleniumPageHelperAndWaiter.waitForInvisibleOfWebElement(driver, correspondentSelection, 2);

        clickSave();
    }

    /**
     * Clicks the button WebElement NoteAlertExit
     * Updated GC (05/06/22) - just wait for the alert to disappear instead of clicking it.
     * 
     * @author gcosmian
     * @since 03/29/22
     */
//    public void waitForNoteAlertDisappear() {
//
//        try {
//            // GC (05/06/22) wait until there are no more alerts before proceeding
//            int alertCount = driver.findElements(buttonNoteAlertExit).size();
//            logger.info("Number of note alerts: " + alertCount);
//        	SeleniumPageHelperAndWaiter.waitForInvisibleOfWebElement(driver, buttonNoteAlertExit, getElementTimeout());
//            SeleniumPageHelperAndWaiter.pause(500);
//            logger.info("No more alerts");
//            
//        } catch (Exception e) {
//            // do nothing - it is possible the alert no longer exist or it never existed
//        }
//    }
    
    /**
     * Clicks the WebElement NoteAlert button to close
     * @author ntagore
     * @09/24/22
     */  
    
    public void waitForNoteAlertDisappear() throws ElementNotFoundException {

        try {         
            MemberDemographicPO memberDemographicPO = new MemberDemographicPO(super.driver, super.pageConfiguration);
            @SuppressWarnings("unused")
            boolean isLocatorPresent = SeleniumPageHelperAndWaiter.isLocatorPresence(memberDemographicPO, buttonNoteAlertExit, 10);
            List<WebElement> alertWebElements = driver.findElements(buttonNoteAlertExit);
            logger.info("Number of note alert elements: " + alertWebElements.size());
            int alertCount = driver.findElements(buttonNoteAlertExit).size();
            logger.info("Number of note alerts: " + alertCount);
            for ( WebElement alert : alertWebElements )
            {
                SeleniumPageHelperAndWaiter.clickWebElement(driver, alert);
            }            
            logger.info("No more alerts");
            
        } catch (Exception e) {
        }


}
}