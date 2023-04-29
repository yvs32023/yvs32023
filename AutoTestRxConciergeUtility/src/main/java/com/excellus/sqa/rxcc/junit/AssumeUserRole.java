/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * This will be used to determine a test class/method will be run base on the user role
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 10/25/2022
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(AssumeUserRoleCondition.class)
public @interface AssumeUserRole
{

}
