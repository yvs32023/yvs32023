/**
 * 
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 * 
 */
/**
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import com.excellus.sqa.rxcc.cosmos.MemberInterventionQueries;
import com.excellus.sqa.rxcc.cosmos.Queries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.pages.member.MemberCommandCenterPO;
import com.excellus.sqa.rxcc.pages.member.MemberInterventionsPO;
import com.excellus.sqa.rxcc.steps.member.OpenMemberStep;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.step.IStep.Status;

/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 02/09/2023
 */
/**
 * @author hzia
 *
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@UserRole(role = {"SSO", "RXCC_FULL_MULTI","RXCC_FULL_LOA", "RXCC_FULL_MED"})
@Tag("ALL")
@Tag("MEMBER")
@DisplayName("113906-Display Rule Name and link to rule in Command Center")
public class MemberCommandCenterRuleLinkTest extends RxConciergeUITestBase {    
	
	
	 private static final Logger logger = LoggerFactory.getLogger(MemberCommandCenterTest.class);

	    static PageConfiguration pageConfiguration;
	    static MemberCommandCenterPO commandCenterPO;
	    static MemberInterventionsPO memberInterventionPO;
	    private static List<Tenant> tenants;
	    static String interventionId;
	    static String memberId;
	    static String tenantSubscription;

	    @BeforeAll
	    public static void setupTestData() throws ElementNotFoundException
	    {
	        logger.info("Setup the test data Member Intervention Command Center Rule Name Link");

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

		@TestFactory
	    @Test   
	    @Order(1)
	    @DisplayName("3278-RxCC Command Center - Rule Name Link")
	    public void validateRuleNameLink() throws ElementNotFoundException
	    {
	        SeleniumPageHelperAndWaiter.pause(2000);
	        memberInterventionPO = new MemberInterventionsPO(driverBase.getWebDriver(), pageConfiguration);
	        SeleniumPageHelperAndWaiter.pause(2000);
	        List<MemberIntervention> expectedInterventions = MemberInterventionQueries.getInterventionUIData(tenantSubscription, memberId, interventionId);
	        List<String> expectedintervention = expectedInterventions.stream().map(directory -> directory.getRuleName()).collect(Collectors.toList());
	        String expectedinterventionRuleName= expectedintervention.toString();
	        if (commandCenterPO.isClickableRuleLinkName()==true) {
	        	 commandCenterPO.selectRuleLink();
	        	 SeleniumPageHelperAndWaiter.pause(2000);
	        	 String actualRulename=commandCenterPO.getRuleNameText();
	        	 assertThat(actualRulename, Matchers.containsInAnyOrder(expectedinterventionRuleName.toString()) != null);
	        }
	    }
}

