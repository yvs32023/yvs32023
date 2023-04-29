/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.roles.UserRole;

/**
 *
 * AssumeUserRoleCondition evaluates if the test class or method should be executed based on the supplied user role (VM arg TestUserRoleFilter)
 * matches the test class/method annotation {@link UserRole}. This is NOT applicable to user role where TestUserRoleFilter 'ALL'
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 10/25/2022
 */
public class AssumeUserRoleCondition implements ExecutionCondition
{
	private static final Logger logger = LoggerFactory.getLogger(AssumeUserRoleCondition.class);
	private static String userRole;

	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context)
	{
		String theRole = System.getProperty("TestUserRoleFilter");

		if (StringUtils.isBlank(theRole)) {
			return ConditionEvaluationResult.enabled("Test class can run since no role was provided in VM arg TestUserRoleFilter");
		}

		Class<?> clazz = context.getTestClass().get();

		logger.info("Check if " + clazz.getName() + " can be run with the " + theRole);

		List<String> roles = new ArrayList<String>();

		while ( clazz != null)
		{
			if ( clazz.getDeclaredAnnotation(UserRole.class) == null )
			{
				clazz = clazz.getSuperclass();
				continue;
			}

			roles.addAll(Arrays.asList(clazz.getDeclaredAnnotation(UserRole.class).role()));
			clazz = clazz.getSuperclass();
		}

		roles.replaceAll(String::toUpperCase);

		userRole = roles.stream()
									 .filter(role -> StringUtils.equalsIgnoreCase(role, theRole))
									 .findFirst()
									 .orElse(null);

		if ( StringUtils.isNotBlank(userRole) )
		{
			return ConditionEvaluationResult.enabled("Test can run with the role " + theRole);
		}

		return ConditionEvaluationResult.disabled("Test is only configured to run with " +
														  Arrays.toString(roles.toArray()) + " and not with " + theRole);
	}

	public static void setUserRole(String userRole)
	{
		AssumeUserRoleCondition.userRole = userRole;
	}

	public static String getUserRole()
	{
		return userRole;
	}
}
