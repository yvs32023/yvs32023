/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;

/**
 * 
 * 
 * @author Garrett Cosmiano (gcosmian)
 * @since 01/11/2022
 */
@UserRole(role = {"SSO", "RXCC_FULL_MULTI", "RXCC_READ_MULTI", "RXCC_OPS_MULTI", "RXCC_REPORT_MULTI",
		"RXCC_FULL_SINGLE", "RXCC_READ_SINGLE", "RXCC_OPS_SINGLE", "RXCC_REPORT_SINGLE",
		"RXCC_FULL_LOA", "RXCC_READ_LOA", "RXCC_OPS_LOA", "RXCC_REPORT_LOA",
		"RXCC_FULL_MED", "RXCC_READ_MED", "RXCC_OPS_MED", "RXCC_REPORT_MED"})
@Tag("ALL")
public class LoginTest extends RxConciergeUITestBase {
	
	@Test
	public void loginSuccessful()
	{
		assertTrue(StringUtils.isNotBlank(tokenAccess));
		assertTrue(StringUtils.isNotBlank(refreshToken));
	}

}

