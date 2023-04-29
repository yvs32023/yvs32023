/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.pages.member.MemberSearchPO;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;

/**
 * 
 * 
 * @author Husnain Zia (hzia)
 * @since 02/07/2022
 */
@UserRole(role = {"SSO", "RXCC_FULL_MULTI"})
@Tag("ALL")
@Tag("MEMBER")
@Tag("MEMBER_SEARCH")
@Tag("SEARCH")
public class MemberSearchTest extends RxConciergeUITestBase {
	
	@ParameterizedTest
	@ValueSource(strings={"201986534 0"})
	public void searchMember(String MemberId) throws InterruptedException, ElementNotFoundException 
	{
		PageConfiguration memberPage = (PageConfiguration) BeanLoader.loadBean("memberPage");
		MemberSearchPO memberSearchPO = new MemberSearchPO(driverBase.getWebDriver(), memberPage);
		memberSearchPO.setMemberSearch(MemberId);
	}

}

