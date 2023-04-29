/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.opentest4j.TestAbortedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.MemberCorrespondenceQueries;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionNoteQueries;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionQueries;
import com.excellus.sqa.rxcc.cosmos.Queries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.FaxRequest;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.MemberInterventionNote;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.pages.member.MemberCommandCenterPO;
import com.excellus.sqa.rxcc.pages.member.MemberCorrespondencePO;
import com.excellus.sqa.rxcc.pages.member.MemberInterventionsPO;
import com.excellus.sqa.rxcc.steps.member.OpenMemberStep;
import com.excellus.sqa.rxcc.workflows.fax.ProcessSentFaxWorkflow;
import com.excellus.sqa.rxcc.workflows.fax.SendExtrafaxWorkflow;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.step.IStep.Status;
import com.excellus.sqa.workflow.IWorkflow.Mode;

/**
 * @author Neeru Tagore (ntagore)
 * @since 12/29/2022
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI","RXCC_FULL_LOA", "RXCC_FULL_MED"})
@Tag("ALL")
@Tag("MEMBER")
@DisplayName("74625-Generation and Receipt of Fax")
public class MemberCommandCenterTest extends RxConciergeUITestBase {

    private static final Logger logger = LoggerFactory.getLogger(MemberCommandCenterTest.class);

    static PageConfiguration pageConfiguration;
    static MemberCommandCenterPO commandCenterPO;
    static MemberInterventionsPO memberInterventionPO;
    private static List<Tenant> tenants;
    private static List<MemberIntervention> expectedInterventions;
    private static List<MemberIntervention> actualInterventions;
    static String interventionId;
    static String memberId;
    static String tenantSubscription;
    static List<MemberInterventionNote> expectedmemberInterventionNote;
    static List<MemberInterventionNote> actualmemberInterventionNote;
    static List<MemberCorrespondence> expectedInterventionCorrespondence;
    static List<MemberCorrespondence> actualInterventionCorrespondence;
    private static List<FaxRequest> faxRequests = new ArrayList<>();
    static MemberCorrespondencePO memberCorrespondencePO;
    static List<MemberCorrespondence> expectedCorrespondence;
    static List<MemberCorrespondence> actualCorrespondence;

    @BeforeAll
    public static void setupTestData() throws ElementNotFoundException
    {
        logger.info("Setup the test data for fax");

        Map<String, MemberIntervention> memberIntervention = new HashMap<>();

        tenants = TenantQueries.getTenantsByAccountName(acctName);

        for ( Tenant tenant : tenants )
        {
            memberId = MemberInterventionQueries.getRandomMemberWithInterventionUI(tenant.getSubscriptionName());
            tenantSubscription = tenant.getSubscriptionName();
            if (StringUtils.isNotBlank(memberId))
            {
                List<MemberIntervention> interventions = MemberInterventionQueries.getInterventionUI(tenantSubscription, memberId)
                        .stream()
                        .filter(intervention -> intervention.getQueueStatusCode().equals("6")) // Get only queue status 6 (Generated)
                        .collect(Collectors.toList());

                memberIntervention.put(memberId, Queries.getRandomItem(interventions));
                interventionId =  memberIntervention.get(memberId).getId();
                memberId =  memberIntervention.get(memberId).getMemberId();

                logger.info("********Member Id   " + memberId);
                logger.info("********Inter Id  " + interventionId);

                OpenMemberStep step = new OpenMemberStep(driverBase.getWebDriver(), memberId);
                step.run();
                if ( step.stepStatus() != Status.COMPLETED ) {
                    throw new TestAbortedException("Unable to open the member id [" + memberId + "]", step.getStepException());
                } 
                break;
            }
        }
        pageConfiguration = BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class);
        memberInterventionPO = new MemberInterventionsPO(driverBase.getWebDriver(), pageConfiguration);
        memberInterventionPO.selectIntervention();
        SeleniumPageHelperAndWaiter.pause(2000);  
    }

    @BeforeEach
    public void instantiateCommandCenterPO() throws ElementNotFoundException{

        commandCenterPO = new MemberCommandCenterPO(driverBase.getWebDriver(), pageConfiguration);
    }

    @Test
    @Order(1)
    @DisplayName("22412-Fax Queued")
    public void faxRequest() throws ElementNotFoundException
    {
        // Step 1 -Select Preview Fax and Send Fax
        SeleniumPageHelperAndWaiter.pause(2000);
        commandCenterPO.previewFax();
        SeleniumPageHelperAndWaiter.pause(5000);
        commandCenterPO.sendFax();
        SeleniumPageHelperAndWaiter.pause(5000);
    }

    @TestFactory
    @Test
    @Order(2)
    @DisplayName("22412-Fax Data validation ")
    public List<DynamicNode> validateFaxQueuedStatus() throws ElementNotFoundException
    {
        SeleniumPageHelperAndWaiter.pause(5000);
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        List<MemberInterventionNote> expectedmemberInterventionNote = MemberInterventionNoteQueries.getInterventionNoteById(tenantSubscription, memberId, interventionId); 
        actualmemberInterventionNote = commandCenterPO.retrieveInterventionNote();

        for ( MemberInterventionNote expected : expectedmemberInterventionNote )
        { 
            boolean found = false; 
            for ( MemberInterventionNote actual : actualmemberInterventionNote )

            {
                if ( expected.equalsByNonId(actual) )
                {
                    tests.add(dynamicContainer("Intervention Status History data validation " + expected.getNewStatusDescription(), expected.compareMemberInterventionNoteUI(actual)));
                    found = true;
                    break;
                }
            }

            if ( !found )
            {
                tests.add(dynamicTest("Intervention Status History data validation " + expected.getNewStatusDescription() + " is missing from UI search result", 
                        () -> fail("Intervention Staus [" + expected.toString() + "] not found in UI search result")));
            }
        }
        return tests;
    }
    @TestFactory
    @DisplayName("22412-Send Fax")
    @Order(3)
    public List<DynamicNode> sendFax()
    {  
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        SendExtrafaxWorkflow sendExtrafaxWorkflow = new SendExtrafaxWorkflow(Mode.RUN, tokenAccess, null, interventionId, acctName);
        sendExtrafaxWorkflow.run();

        tests.addAll(sendExtrafaxWorkflow.workflowStepResults());
        tests.addAll(sendExtrafaxWorkflow.workflowTestResults());

        faxRequests.add(sendExtrafaxWorkflow.getFaxRequest());
        return tests;
    }
    @TestFactory
    @DisplayName("22412-Send Fax validation")
    @Order(4)
    public List<DynamicNode> processSentFax() throws ElementNotFoundException
    {       
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        ProcessSentFaxWorkflow processSentFaxWorkflow = new ProcessSentFaxWorkflow(Mode.RUN, tokenAccess, interventionId, null, null);
        processSentFaxWorkflow.run();

        tests.addAll(processSentFaxWorkflow.workflowStepResults());
        tests.addAll(processSentFaxWorkflow.workflowTestResults());
        SeleniumPageHelperAndWaiter.refreshWebPage(commandCenterPO); //Page refresh required to get Fax Successful on UI
        SeleniumPageHelperAndWaiter.pause(3000);
        return tests;
    }

    @TestFactory
    @Test   
    @Order(5)
    @DisplayName("22412-Intervention Correspondence validation ")
    public List<DynamicNode> validateInterventionCorrespondence() throws ElementNotFoundException
    {
        SeleniumPageHelperAndWaiter.pause(1000);
        List<MemberCorrespondence> expectedInterventionCorrespondence = MemberCorrespondenceQueries.getInterventionCorrespondence(tenantSubscription, memberId, interventionId); 
        actualInterventionCorrespondence = commandCenterPO.retrieveInterventionCorrespondenceInfo();

        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        for ( MemberCorrespondence expected : expectedInterventionCorrespondence )
        { 
            boolean found = false; 
            for ( MemberCorrespondence actual : actualInterventionCorrespondence )

            {
                if ( expected.equalsByNonId(actual) )
                {
                    tests.add(dynamicContainer("Intervention Correspondence data validation " + expected.getCorrespondenceType(), expected.compareInterventionCorrespondenceUI(actual)));
                    found = true;
                    break;
                }
            }

            if ( !found )
            {
                tests.add(dynamicTest("Intervention Correspondence data validation " + expected.getCorrespondenceType() + " is missing from UI search result", 
                        () -> fail("Intervention Correspondence [" + expected.toString() + "] not found in UI search result")));
            }
        }
        return tests;
    }

    @TestFactory
    @Test   
    @Order(6)
    @DisplayName("22412-Correspondence Tab validation ")
    public List<DynamicNode> validateCorrespondenceTab() throws ElementNotFoundException
    {
        SeleniumPageHelperAndWaiter.pause(2000);
        memberCorrespondencePO = new MemberCorrespondencePO(driverBase.getWebDriver(), pageConfiguration);
        memberCorrespondencePO.clickCorrespondence();
        SeleniumPageHelperAndWaiter.pause(2000);
        List<MemberCorrespondence> expectedCorrespondence = MemberCorrespondenceQueries.getInterventionCorrespondence(tenantSubscription, memberId, interventionId); 
        actualCorrespondence = memberCorrespondencePO.retrieveCorrespondenceTypeInfo();

        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        for ( MemberCorrespondence expected : expectedCorrespondence )
        { 
            boolean found = false; 
            for ( MemberCorrespondence actual : actualCorrespondence )

            {
                if ( expected.equalsByNonId(actual) )
                {
                    tests.add(dynamicContainer("Correspondence Data validation " + expected.getCorrespondenceType(), expected.compareCorrespondenceTypeUI(actual)));
                    found = true;
                    break;
                }
            }

            if ( !found )
            {
                tests.add(dynamicTest("Correspondence Data validation " + expected.getCorrespondenceType() + " is missing from UI search result", 
                        () -> fail("Correspondence [" + expected.toString() + "] not found in UI search result")));
            }
        }
        commandCenterPO.selectCommandCenter();
        return tests;

    }

    @TestFactory
    @Test   
    @Order(7)
    @DisplayName("22412-Member Intervention Tab validation ")
    public List<DynamicNode> validateMemberInterventionTab() throws ElementNotFoundException
    {
        SeleniumPageHelperAndWaiter.pause(2000);
        memberInterventionPO = new MemberInterventionsPO(driverBase.getWebDriver(), pageConfiguration);
        memberInterventionPO.clickMemberInterventions();
        SeleniumPageHelperAndWaiter.pause(2000);
        List<MemberIntervention> expectedInterventions = MemberInterventionQueries.getInterventionUIData(tenantSubscription, memberId, interventionId); 
        actualInterventions = memberInterventionPO.retrievInterventionsFaxInfo();

        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        for ( MemberIntervention expected : expectedInterventions )
        { 
            boolean found = false; 
            for ( MemberIntervention actual : actualInterventions )

            {
                if ( expected.equalsByNonIds(actual) )
                {
                    tests.add(dynamicContainer("Intervention Data validation " + expected.getOriginalProviderName(), expected.compareInterventionsFaxUI(actual)));
                    found = true;
                    break;
                }
            }

            if ( !found )
            {
                tests.add(dynamicTest("Intervention Data validation " + expected.getOriginalProviderName() + " is missing from UI search result", 
                        () -> fail("Intervention [" + expected.toString() + "] not found in UI search result")));
            }
        }
        return tests;
    }
}




