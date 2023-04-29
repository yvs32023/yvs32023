/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.steps.member;


import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.configuration.RxConciergeBeanConfig;
import com.excellus.sqa.rxcc.pages.member.MemberDemographicPO;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.selenium.step.AbstractUIStep;
import com.excellus.sqa.spring.BeanLoader;

/**
 * Open member demographic web page
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/28/2022
 */
public class OpenMemberStep extends AbstractUIStep {

    private static final Logger logger = LoggerFactory.getLogger(OpenMemberStep.class);
    
    private final static String STEP_NAME = "OpenMemberDemographic";
    private final static String STEP_DESC = "Open member (%s) demographic";
    
    private String adTenantId;
    private String memberId;
    
    /**
     * Open the member demographic web page given the adTenantId and memberId
     * @param driver {@link WebDriver}
     * @param adTenantId the member belongs to
     * @param memberId to open
     */
    public OpenMemberStep(WebDriver driver, String adTenantId, String memberId) 
    {
        super(STEP_NAME, String.format(STEP_DESC + " from tenant %s", memberId, adTenantId), driver, BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
        this.adTenantId = adTenantId;
        this.memberId = memberId;
        
        
    }
    
    /**
     * Open the member demographic web page given the member id
     * 
     * @param driver {@link WebDriver}
     * @param memberId to open
     */
    public OpenMemberStep(WebDriver driver, String memberId) 
    {
        super(STEP_NAME, String.format(STEP_DESC, memberId), driver, BeanLoader.loadBean(BeanNames.MEMBER_PAGE, PageConfiguration.class));
        this.memberId = memberId;
    }
    
    /**
     * Open the member demographic web page given the member id
     * 
     * @param driver {@link WebDriver}
     * @param memberId to open
     */
    public OpenMemberStep(WebDriver driver, PageConfiguration pageConfiguration, String memberId) 
    {
        super(STEP_NAME, String.format(STEP_DESC, memberId), driver, pageConfiguration);
        this.memberId = memberId;
        
    }

    @Override
    public void run() 
    {
        super.stepStatus = Status.IN_PROGRESS;
        logger.info(super.print());
        
        try
        {
            if ( StringUtils.isBlank(adTenantId) )
            {
                /*
                 * Get adTenantId
                 */
                
                // GC (09/07/22) use GetTenantStep to retrieve tenant
                GetTenantStep getTenantStep = new GetTenantStep(null, memberId);
                getTenantStep.run();
                adTenantId = getTenantStep.getTenant().getAdTenantId();
                
                if ( StringUtils.isBlank(adTenantId) )
                {
                    throw new TestConfigurationException( String.format("Member id [%s] starts with unknown tenant id", memberId) );
                }
            }
            
            
            // https://tst.rxcc.excellus.com/tenants/{adTenantId}/members/{memberId}
            String url = RxConciergeBeanConfig.envConfig().getProperty("url.home");
            driver.get(url + "/tenants/" + adTenantId + "/members/" + memberId);
            
            // Wait for the page to load
            MemberDemographicPO memberDemographicPO = new MemberDemographicPO(super.driver, super.pageConfiguration);
            memberDemographicPO.waitForNoteAlertDisappear();  // close the alert
            super.stepStatus = Status.COMPLETED;
        }
        catch (Exception e)
        {
            super.stepException = e;
            super.stepStatus = Status.ERROR;
        }
        
        logger.info(print());
    }
}