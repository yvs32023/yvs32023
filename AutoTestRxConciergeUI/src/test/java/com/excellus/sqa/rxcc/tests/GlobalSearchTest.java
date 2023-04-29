/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeUITestBase;
import com.excellus.sqa.rxcc.cosmos.MemberQueries;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.pages.member.MemberSearchPO;
import com.excellus.sqa.selenium.ElementNotFoundException;
import com.excellus.sqa.selenium.configuration.PageConfiguration;
import com.excellus.sqa.spring.BeanLoader;

/**Notes:
 * Each of the below test case perform global search by FN, LN, DOB, MemberID.
 * Each test case validate the Member name, ID, DOB, Tenant, Group
 * Selecting the search result will display member detail page 
 * 
 * GC (02/21/22) modified to use DTO
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 02/04/2022
 * @updated on 02142022
 */
@UserRole(role = {"SSO", "RXCC_FULL_MULTI","RXCC_FULL_LOA","RXCC_FULL_MED"})
@Tag("ALL")
@Tag("SEARCH")
@Tag("GLOBAL_SEARCH")
public class GlobalSearchTest extends RxConciergeUITestBase
{
    private static PageConfiguration pageConfiguration;
    private static MemberSearchPO memberSearchPO;

    @BeforeAll
    public static void setup()
    {
        pageConfiguration = (PageConfiguration) BeanLoader.loadBean("homePage");
        memberSearchPO = new MemberSearchPO(driverBase.getWebDriver(), pageConfiguration);

        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) 
        { memberEhp= MemberQueries.getRandomMember(Tenant.Type.EHP); memberExe=MemberQueries.getRandomMember(Tenant.Type.EXE); }
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_LOA")) 
        {memberLoa= MemberQueries.getRandomMember(Tenant.Type.LOA); }
        else if  (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) 
        {memberMed= MemberQueries.getRandomMember(Tenant.Type.MED); }
    }
    @AfterEach
    public void loadAndRefreshPage() throws ElementNotFoundException {
        memberSearchPO.getWebDriver().navigate().refresh();
        memberSearchPO.waitForPageObjectToLoad();

        memberEhp = null;
        memberExe = null;
        memberLoa = null;
        memberMed = null;

        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) 
        { memberEhp=MemberQueries.getRandomMember(Tenant.Type.EHP); memberExe= MemberQueries.getRandomMember(Tenant.Type.EXE); }
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_LOA")) 
        { memberLoa = MemberQueries.getRandomMember(Tenant.Type.LOA); }
        else if  (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) 
        {memberMed= MemberQueries.getRandomMember(Tenant.Type.MED); }
    }

    @ParameterizedTest
    @MethodSource("getFirstName")
    @DisplayName("3723: RxCC Global Search Results UI_Member First Name")
    public void searchFirstName(String firstName) throws ElementNotFoundException
    {

        // Grab the search result
        List<Member> memberSearchResults = performMemberGlobalSearch(firstName);

        // validate that all members matches the first name
        for ( Member member : memberSearchResults )
        {
            String actual = member.getFirstLastName();
            assertTrue(StringUtils.containsIgnoreCase(actual, firstName), "The member name [" + actual + "] did not contain first " + firstName);
        }
    }

    static Stream<String> getFirstName()
    {
        //        return Stream.of(memberEhp.getFirstName(), memberExe.getFirstName(), memberLoa.getFirstName(), memberMed.getFirstName());
        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
            return Stream.of(memberEhp.getFirstName(), memberExe.getFirstName());
        }else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) { 
            return Stream.of(memberMed.getFirstName());    
        }else {
            return Stream.of(memberLoa.getFirstName()); }
    }

    private static Member memberEhp;
    private static  Member memberExe;
    private static  Member memberLoa;
    private static  Member memberMed;


    @ParameterizedTest
    @MethodSource("getLastName")
    @DisplayName("3724: RxCC Global Search Results UI_Member Last Name")
    public void searchLastName(String lastName) throws ElementNotFoundException {

        // Grab the search result
        List<Member> memberSearchResults = performMemberGlobalSearch(lastName);

        // validate that all members matches the last name
        for ( Member member : memberSearchResults )
        {
            String actual = member.getFirstLastName();
            assertTrue(StringUtils.containsIgnoreCase(actual, lastName), "The member name [" + actual + "] did not contain last name " + lastName);
        }
    }

    static Stream<String> getLastName()
    {
        // return Stream.of(memberEhp.getLastName(), memberExe.getLastName(), memberLoa.getLastName(), memberMed.getLastName());

        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
            return Stream.of(memberEhp.getLastName(), memberExe.getLastName());
        }else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) { 
            return Stream.of(memberMed.getLastName());    
        }else {
            return Stream.of(memberLoa.getLastName()); }
    }

    @ParameterizedTest
    @MethodSource("getDateBirth")
    @DisplayName("3725: RxCC Global Search Results UI_Member DOB")
    public void globalSearchDob(String dob) throws ElementNotFoundException
    {
        // Grab the search result
        List<Member> memberSearchResults = performMemberGlobalSearch(dob);
        dob = formatDOB(dob);

        // validate that all members matches the DOB
        for ( Member member : memberSearchResults )
        {
            assertTrue(StringUtils.isNotBlank(member.getDateBirth()), "The member DOB is either null or empty");           
            assertTrue(StringUtils.equals(dob, member.getDateBirth()), "The member DOB did not match");
        }

    }

    static Stream<String> getDateBirth()
    {
        //return Stream.of(memberEhp.getDateBirth(), memberExe.getDateBirth(), memberLoa.getDateBirth(), memberMed.getDateBirth()); 

        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
            return Stream.of(memberEhp.getDateBirth(), memberExe.getDateBirth());
        }else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) { 
            return Stream.of(memberMed.getDateBirth());    
        }else {
            return Stream.of(memberLoa.getDateBirth()); }
    }


    @ParameterizedTest
    @MethodSource("getSubscriberId")
    @DisplayName("3722: RxCC Global Search Results UI Subscriber ID")
    public void globalSearchSubscriberId(String searchVal) throws ElementNotFoundException
    {
        // Grab the search result
        List<Member> memberSearchResults = performMemberGlobalSearch(searchVal);

        // validate that all members matches the subscriber id
        for ( Member member : memberSearchResults )
        {
            assertTrue(StringUtils.isNotBlank(member.getSubscriberId()), "The member subscriber id is either null or empty");
            assertTrue(StringUtils.equals(searchVal, member.getSubscriberId()), "The member subscriber id did not match");
        }
    }

    static Stream<String> getSubscriberId()
    {
        // return Stream.of(memberEhp.getSubscriberId(), memberExe.getSubscriberId(), memberLoa.getSubscriberId(), memberMed.getSubscriberId()); 
        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) { 
            return Stream.of(memberEhp.getSubscriberId(), memberExe.getSubscriberId());
        }else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) { 
            return Stream.of(memberMed.getSubscriberId());    
        }else {
            return Stream.of(memberLoa.getSubscriberId()); }
    }

    @Test
    @DisplayName("3726: RxCC Global Search Results UI_Select Member")
    public void selectMember() throws ElementNotFoundException
    {
        Member searchMember = new Member();
        if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MULTI")) 
        { 
            searchMember.setFirstName( memberEhp.getFirstName() );
            searchMember.setLastName( memberEhp.getLastName());
            searchMember.setMemberId(memberEhp.getMemberId());
            searchMember.setSubscriberId(memberEhp.getSubscriberId());
            searchMember.setDateBirth(formatDOB(memberEhp.getDateBirth()));
            searchMember.setGroupName(memberEhp.getGroupName());
            searchMember.setTenantName(memberEhp.getTenantName()); }

        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_LOA")) 
        { 
            searchMember.setFirstName( memberLoa.getFirstName() );
            searchMember.setLastName( memberLoa.getLastName());
            searchMember.setMemberId(memberLoa.getMemberId());
            searchMember.setSubscriberId(memberLoa.getSubscriberId());
            searchMember.setDateBirth(formatDOB(memberLoa.getDateBirth()));
            searchMember.setGroupName(memberLoa.getGroupName());
            searchMember.setTenantName(memberLoa.getTenantName()); }
        
        else if (StringUtils.equalsIgnoreCase(acctName, "RXCC_FULL_MED")) 
        { 
            searchMember.setFirstName( memberMed.getFirstName() );
            searchMember.setLastName( memberMed.getLastName());
            searchMember.setMemberId(memberMed.getMemberId());
            searchMember.setSubscriberId(memberMed.getSubscriberId());
            searchMember.setDateBirth(formatDOB(memberMed.getDateBirth()));
            searchMember.setGroupName(memberMed.getGroupName());
            searchMember.setTenantName(memberMed.getTenantName()); }

        // Perform a search
        memberSearchPO.setMemberSearch(searchMember.getFirstName() + " " + searchMember.getLastName() + " " + searchMember.getDateBirth());

        // click on a member that matches the searchMember object
        memberSearchPO.clickMemberResult(searchMember);

        // Validate the url ends with the member id
        String url = memberSearchPO.getWebDriver().getCurrentUrl();
        url = StringUtils.substringBetween(url, "members/", "/");

        assertEquals(searchMember.getMemberId(), url, "Unexpected member displayed");
    }
    /**
     * Perform global search and return the results
     *
     * @param searchTerm to search for
     * @return list of {@link Member} from the serach result
     * @throws ElementNotFoundException is there an issue
     */
    private List<Member> performMemberGlobalSearch(String searchTerm) throws ElementNotFoundException {

        // enter search criteria
        memberSearchPO.setMemberSearch(searchTerm);

        // Grab the search result
        List<Member> memberSearchResults = memberSearchPO.retrieveMemberSearchResult();
        return memberSearchResults;
    }

    public String formatDOB(String dob) {

        DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        DateFormat formatter2 = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date date = formatter.parse(dob);
            dob = formatter2.format(date).toString();
        } catch (ParseException e) {

        }
        return dob;
    }

}