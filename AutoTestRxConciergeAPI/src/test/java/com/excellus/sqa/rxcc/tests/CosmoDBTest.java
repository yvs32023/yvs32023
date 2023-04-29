/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.MemberQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.Tenant;

/**
 *
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 01/17/2022
 */
@Tag("DUMMY")
public class CosmoDBTest extends RxConciergeAPITestBaseV2 {

	private static final Logger logger = LoggerFactory.getLogger(CosmoDBTest.class);

    @Test
    public void queryTest()
    {
    	List<Tenant> tenants = TenantQueries.getTenants();
    	
    	assertTrue(tenants.size() > 0, "Unable to get any tenant");
    	
    	tenants.forEach(tenant -> logger.info(tenant.toString()));
    }
    
    
    @Test
    public void queryMember()
    {
    	Member member = MemberQueries.getRandomMember(Tenant.Type.EHP);
    	assertNotNull(member, "Unable to get random member");
    	logger.info(member.toString());
    }
    
}
