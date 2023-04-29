/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.junit;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 10/25/2022
 */
public class AddRoleToDisplayName extends DisplayNameGenerator.Standard
{
	private static final Logger logger = LoggerFactory.getLogger(AddRoleToDisplayName.class);

	@Override
	public String generateDisplayNameForClass(Class<?> testClass)
	{
		String role = System.getProperty("TestUserRoleFilter");
		role = StringUtils.isBlank(role) || StringUtils.contains(role, ",") ? "" : " (" +  role + ")";

		// else use test class name then append the role
		return super.generateDisplayNameForClass(testClass) + role;
	}
}
