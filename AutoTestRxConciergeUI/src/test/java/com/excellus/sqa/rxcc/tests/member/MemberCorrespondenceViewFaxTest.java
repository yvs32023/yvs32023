/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opentest4j.TestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.MemberCorrespondenceQueries;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.pages.member.MemberCorrespondenceViewFaxPO;
import com.excellus.sqa.rxcc.steps.member.OpenMemberStep;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.step.IStep.Status;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 12/16/2022
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI","RXCC_FULL_LOA", "RXCC_FULL_MED"})
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_CORRESPONDENCE_VIEW_FAX")
@DisplayName("115627-System Generated Correspondence Event")
public class MemberCorrespondenceViewFaxTest extends RxConciergeUITestBase {
    private static final Logger logger = LoggerFactory.getLogger(MemberCorrespondenceViewFaxTest.class);

    static PageConfiguration pageConfiguration;
    
    static MemberCorrespondenceViewFaxPO memberCorrespondenceViewFaxPO;

    static String memberId;
    
    static MemberCorrespondence memberWithFaxCorrespondence;

    @BeforeAll
    public static void setup() throws ElementNotFoundException{
        
        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
            memberWithFaxCorrespondence = MemberCorrespondenceQueries.getMembersWithCorrespondenceType(Tenant.Type.EHP.getSubscriptionName());}
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) {
            memberWithFaxCorrespondence = MemberCorrespondenceQueries.getMembersWithCorrespondenceType(Tenant.Type.MED.getSubscriptionName());}
        else {
            memberWithFaxCorrespondence = MemberCorrespondenceQueries.getMembersWithCorrespondenceType(Tenant.Type.LOA.getSubscriptionName());}
        
        memberId = memberWithFaxCorrespondence.getMemberId();
        logger.info("*******memberId************"+memberId);
        
        OpenMemberStep step = new OpenMemberStep(driverBase.getWebDriver(), memberId);
        step.run();

        logger.info("*******memberId************"+memberId);

        if ( step.stepStatus() != Status.COMPLETED ) {
            throw new TestAbortedException("Unable to open the member id [" + memberId + "]", step.getStepException());
        }
        pageConfiguration = BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class);
        memberCorrespondenceViewFaxPO = new MemberCorrespondenceViewFaxPO(driverBase.getWebDriver(), pageConfiguration);
        
    }
    
    @BeforeEach
    public void clickHeaderCorrespondence() throws ElementNotFoundException{

        memberCorrespondenceViewFaxPO.clickCorrespondence();
    }
    @Test
    @Order(1)
    @DisplayName ("9983-View Member Inbound Provider Fax")
    public void viewInboundFaxProvider() throws ElementNotFoundException{
        memberCorrespondenceViewFaxPO.clickInboundFaxProvider();
        SeleniumPageHelperAndWaiter.pause(2000);
        boolean isPDFPresent = memberCorrespondenceViewFaxPO.isPdfPresent();
        memberCorrespondenceViewFaxPO.buttonCancel();
        assertEquals(isPDFPresent, true);
    }

    @Test
    @Order(2)
    @DisplayName ("9983- View Member Outbound Provider Fax")
    public void viewOutboundFaxProvider() throws ElementNotFoundException{
        memberCorrespondenceViewFaxPO.clickOutboundFaxProvider();
        SeleniumPageHelperAndWaiter.pause(2000);
        boolean isPDFPresent = memberCorrespondenceViewFaxPO.isPdfPresent();
        memberCorrespondenceViewFaxPO.buttonCancel();
        assertEquals(isPDFPresent, true);
    
    }  
    
}


