/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.MemberRxclaim;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.Tenant.Type;

/**
 * Queries for member rxclaims
 * 
 * @author Manish Sharma (msharma)
 * @since 04/07/2022
 */
@SuppressWarnings("unused")
public class MemberRxClaimQueries extends Queries 
{
    private static final Logger logger = LoggerFactory.getLogger(MemberRxClaimQueries.class);

    private volatile static List<MemberRxclaim> RANDOM_EHP_MEMBERS_RXCLAIM;    // contains random EHP member
    private volatile static List<MemberRxclaim> RANDOM_EXE_MEMBERS_RXCLAIM;    // contains random EXE member
    private volatile static List<MemberRxclaim> RANDOM_LOA_MEMBERS_RXCLAIM;    // contains random LOA member
    private volatile static List<MemberRxclaim> RANDOM_MED_MEMBERS_RXCLAIM;    // contains random MED member

    private volatile static List<MemberRxclaim> RANDOM_EHP_MEMBERS_RXCLAIM_ADJUDICATION_DATE;    // contains random EHP member with given adjudicationDateStart
    private volatile static List<MemberRxclaim> RANDOM_EXE_MEMBERS_RXCLAIM_ADJUDICATION_DATE;    // contains random EXE member with given adjudicationDateStart
    private volatile static List<MemberRxclaim> RANDOM_LOA_MEMBERS_RXCLAIM_ADJUDICATION_DATE;    // contains random EHP member with given adjudicationDateStart
    private volatile static List<MemberRxclaim> RANDOM_MED_MEMBERS_RXCLAIM_ADJUDICATION_DATE;    // contains random EXE member with given adjudicationDateStart

    // List of members and the number correspondences it has
    private volatile static List<GenericCount> RANDOM_EHP_MEMBERS_WITH_RXCLAIM;
    private volatile static List<GenericCount> RANDOM_EXE_MEMBERS_WITH_RXCLAIM;
    private volatile static List<GenericCount> RANDOM_LOA_MEMBERS_WITH_RXCLAIM;
    private volatile static List<GenericCount> RANDOM_MED_MEMBERS_WITH_RXCLAIM;

    /**
     * Retrieve random member id with RxClaim
     * @param tenantType {@link Tenant}
     * @return member id
     */
    public synchronized static String getRandomMemberWithRxClaim(Tenant tenant)
    {
        return getRandomMemberWithRxClaim(tenant.getSubscriptionName());
    }

    /**
     * Retrieve random member id with  RxClaim
     * @param tenantType {@link Tenant.Type}
     * @return member id
     */
    public synchronized static String getRandomMemberWithRxClaim(Tenant.Type tenantType)
    {
        return getRandomMemberWithRxClaim(tenantType.getSubscriptionName());
    }


    /**
     * Retrieve the member note
     * 
     * @author msharma
     * @since 04/07/2022
     * @param memberId of the member
     * @return
     */
    public synchronized static List<MemberRxclaim> getMemberRxclaim(String memberId)
    {
        // GC (04/05/22) schema change - member id starts now with adTenantId
        String[] ids = memberId.split("_");

        Tenant tenant = null;

        try {
            int id = Integer.valueOf(ids[0]).intValue();
            tenant = TenantQueries.getTenantByTenantId(id);
        }
        catch (NumberFormatException e) {
            tenant = TenantQueries.getTenant(ids[0]);
        }

        if ( tenant == null )
        {
            throw new TestConfigurationException( String.format("Member id [%s] starts with unknown tenant id", memberId) );
        }

        return getMemberRxclaim(tenant.getSubscriptionName(), memberId);
    }



    /**
     * Retrieve the list of member rxclaim from ALL tenant based on the subcription name
     * 
     * @param subscriptionName of the tenant
     * @param memberId of the member
     * @return List of member notes
     */
    public synchronized static List<MemberRxclaim> getALLTenantMemberRxclaim(String subscriptionName , String memberId)
    {
        return getMemberRxclaim(subscriptionName, memberId);
    }

    /**
     * Retrieve the member rxclaim from ALL tenant based on the subcription name
     * 
     * @param subscriptionName of the tenant
     * @param memberId of the member
     * @param memberId of the member
     * @return List of member notes
     */
    public synchronized static MemberRxclaim getALLTenantMemberRxclaim(String subscriptionName, String rxclaimId, String memberId)
    {
        return getMemberRxclaim( subscriptionName, rxclaimId,  memberId);
    }


    /**
     * Retrieve the member rxclaim
     * 
     * @param subscriptionName either exe or ehp
     * @param memberId of the member
     * @return
     */
    public synchronized static List<MemberRxclaim> getMemberRxclaim(String subscriptionName, String memberId)
    {
        // Build the query
        String query = String.format(Queries.QUERY_BY_TYPE, "rxclaim") + 
                String.format(" and c.memberId = \"%s\"", memberId);


        logger.info("Retrieving member rxclaims from Cosmos DB");
        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
        List<MemberRxclaim> memberRxclaims = executeQuery(beanName, query, MemberRxclaim.class, 100);

        return memberRxclaims;
    }

    /**
     * Retrieve the member rxclaim
     * 
     * @author msharma
     * @since 04/07/22
     * @param subscriptionName either exe or ehp
     * @param rxclaimId of the member rxclaim
     * @param memberId of the member
     * @return
     */
    public synchronized static MemberRxclaim getMemberRxclaim(String subscriptionName, String rxclaimId, String memberId)
    {
        // Build the query
        String query = String.format(Queries.QUERY_BY_TYPE, "rxclaim") + 
                String.format(" and c.id = \"%s\" and c.memberId = \"%s\"", rxclaimId, memberId);

        logger.info("Retrieving member note from Cosmos DB");
        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
        List<MemberRxclaim> memberRxclaims = executeQuery(beanName, query, MemberRxclaim.class, 1);

        if ( memberRxclaims != null && memberRxclaims.size() > 0 )
            return getRandomItem(memberRxclaims);

        return null;
    }

    /**
     * Perform query to the member rxclaim
     * @param beanName of the member defined in {@link com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig}
     * @param size of the members to retrieve
     * @return list of members
     */
    private synchronized static MemberRxclaim getRandomMembersWithRxclaim(String subscriptionName, String whereClause, int size)
    {
        // Setup the where clause
        whereClause = StringUtils.isNotBlank(whereClause) ?
                RegExUtils.replaceFirst(whereClause, "where", "") : "";

        String query = String.format(Queries.QUERY_BY_TYPE, "rxclaim");
        query += StringUtils.isNotBlank(whereClause) ? " and " + whereClause.trim() : "";

        logger.info("Retrieving member from Cosmos DB");
        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
        List<MemberRxclaim> memberList = executeQuery(beanName, query, MemberRxclaim.class, size);

        if ( memberList != null && memberList.size() > 0 )
            return getRandomItem(memberList);

        return null;
    }


    /**
     * Retrieve random member id with rxclaim
     * @param subscriptionName tenant subscription name
     * @return member id
     */
    public synchronized static String getRandomMemberWithRxClaim(String subscriptionName)
    {
        logger.info("Retrieving random member id with rxclaim");

        if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EHP.getSubscriptionName()) )
        {
            if ( RANDOM_EHP_MEMBERS_WITH_RXCLAIM == null || RANDOM_EHP_MEMBERS_WITH_RXCLAIM.size() < 1 )
            {
                getRandomMembersWithRxClaimMoreThanX(subscriptionName,15);
            }

            GenericCount genericCount = getRandomItem(RANDOM_EHP_MEMBERS_WITH_RXCLAIM);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EXE.getSubscriptionName()) )
        {
            if ( RANDOM_EXE_MEMBERS_WITH_RXCLAIM == null || RANDOM_EXE_MEMBERS_WITH_RXCLAIM.size() < 1 )
            {
                getRandomMembersWithRxClaimMoreThanX(subscriptionName,15);
            }

            GenericCount genericCount = getRandomItem(RANDOM_EXE_MEMBERS_WITH_RXCLAIM);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.LOA.getSubscriptionName()) )
        {
            if ( RANDOM_LOA_MEMBERS_WITH_RXCLAIM == null || RANDOM_LOA_MEMBERS_WITH_RXCLAIM.size() < 1 )
            {
                getRandomMembersWithRxClaimMoreThanX(subscriptionName,15);
            }

            GenericCount genericCount = getRandomItem(RANDOM_LOA_MEMBERS_WITH_RXCLAIM);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.MED.getSubscriptionName()) )
        {
            if ( RANDOM_MED_MEMBERS_WITH_RXCLAIM == null || RANDOM_MED_MEMBERS_WITH_RXCLAIM.size() < 1 )
            {
                getRandomMembersWithRxClaimMoreThanX(subscriptionName,15);
            }

            GenericCount genericCount = getRandomItem(RANDOM_MED_MEMBERS_WITH_RXCLAIM);
            return (genericCount != null) ? genericCount.getId() : null;
        }

        throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
    }


    /**
     * Retrieves members that has more than X number of rxclaim 
     * 
     * @author Garrett Cosmiano (gcosmian)
     * @since 05/18/22
     * 
     * @param subscriptionName
     * @param numOfRxClaim
     * @return list of members with corresponding counts of rxclaim
     */
    public synchronized static List<GenericCount> getRandomMembersWithRxClaimMoreThanX(String subscriptionName, int numOfRxClaim)
    {
        logger.info("Retrieving from Cosmos DB a member with rxclaim that is more than " + numOfRxClaim);


        if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()) &&
                RANDOM_EXE_MEMBERS_WITH_RXCLAIM != null && RANDOM_EXE_MEMBERS_WITH_RXCLAIM.size() >= numOfRxClaim )
        {
            return RANDOM_EXE_MEMBERS_WITH_RXCLAIM;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()) &&
                RANDOM_EHP_MEMBERS_WITH_RXCLAIM != null && RANDOM_EHP_MEMBERS_WITH_RXCLAIM.size() >= numOfRxClaim )
        {
            return RANDOM_EHP_MEMBERS_WITH_RXCLAIM;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()) &&
                RANDOM_LOA_MEMBERS_WITH_RXCLAIM != null && RANDOM_LOA_MEMBERS_WITH_RXCLAIM.size() >= numOfRxClaim )
        {
            return RANDOM_LOA_MEMBERS_WITH_RXCLAIM;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()) &&
                RANDOM_MED_MEMBERS_WITH_RXCLAIM != null && RANDOM_MED_MEMBERS_WITH_RXCLAIM.size() >= numOfRxClaim )
        {
            return RANDOM_MED_MEMBERS_WITH_RXCLAIM;
        }

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        /*
         * Retrieve members with at least X number of rxclaim
         */

        String query = "SELECT * from (SELECT r.memberId as id,  r.num "
                + "FROM (SELECT c.memberId, count(1) as num FROM c WHERE c.type = \"rxclaim\" GROUP BY c.memberId) as r "
                + "WHERE r.num > " + numOfRxClaim +")";

        List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, 100);

        if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()) )
        {
            RANDOM_EXE_MEMBERS_WITH_RXCLAIM = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()) )
        {
            RANDOM_EHP_MEMBERS_WITH_RXCLAIM = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()) )
        {
            RANDOM_LOA_MEMBERS_WITH_RXCLAIM = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()) )
        {
            RANDOM_MED_MEMBERS_WITH_RXCLAIM = list;
        }
        else
        {
            throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe or ehp", subscriptionName) );
        }

        return list;
    }

    /**
     * @author msharma
     * @date 04/25/2022
     * Retrieve a random  member with Rxclaim which has the rxClaimId within 18 months ago from today if not submitted.
     *
     * @return a random  member with Rxclaim which has the rxClaimId within 18 months ago from today if not submitted.
     */
    public synchronized static MemberRxclaim getRandomMemberWithRxclaimWithAdjDateStart(String subscriptionName)
    {

        String months18ago = getDefault18MonthsAgo ();

        // Build the query	    
        final String query = String.format(Queries.QUERY_BY_TYPE, "rxclaim") + 
                String.format(" and c.adjudicationDate >= \"%s\" ", months18ago);        

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        List<MemberRxclaim> list = executeQuery(beanName, query, MemberRxclaim.class, 100);
        if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()) )
        {
            RANDOM_EXE_MEMBERS_RXCLAIM = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()) )
        {
            RANDOM_EHP_MEMBERS_RXCLAIM = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()) )
        {
            RANDOM_LOA_MEMBERS_RXCLAIM = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()) )
        {
            RANDOM_MED_MEMBERS_RXCLAIM = list;
        }
        else
        {
            throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe or ehp", subscriptionName) );
        }

        return getRandomItem(list);
    }


    /**
     * @author msharma
     * @date 04/25/2022
     * Retrieve a random  member with Rxclaim which has the rxClaimId within 18 months ago from today if not submitted.
     *
     * @return a random  member with Rxclaim which has the rxClaimId within 18 months ago from today if not submitted.
     */
    public synchronized static MemberRxclaim getRandomMemberWithRxclaimWithAdjDateStart(String subscriptionName,String givenmonthsago)
    {

        // Setup the where clause
        final String query = String.format(Queries.QUERY_BY_TYPE, "rxclaim") + 
                String.format(" and c.adjudicationDate >= \"%s\"", givenmonthsago);

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        List<MemberRxclaim> list = executeQuery(beanName, query, MemberRxclaim.class, 100);
        if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()) )
        {
            RANDOM_EXE_MEMBERS_RXCLAIM_ADJUDICATION_DATE = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()) )
        {
            RANDOM_EHP_MEMBERS_RXCLAIM_ADJUDICATION_DATE = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()) )
        {
            RANDOM_LOA_MEMBERS_RXCLAIM_ADJUDICATION_DATE = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()) )
        {
            RANDOM_MED_MEMBERS_RXCLAIM_ADJUDICATION_DATE = list;
        }
        else
        {
            throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe or ehp", subscriptionName) );
        }

        return getRandomItem(list);
    }



    //Returns dates, Defaults to 18 months ago from today if not submitted.
    public static String getDefault18MonthsAgo() 
    {
        Calendar calender = Calendar.getInstance();
        calender.setTimeZone(TimeZone.getTimeZone("GMT"));
        calender.add(Calendar.MONTH, -18);


        String months18ago = "" + calender.get(Calendar.YEAR) + 
                StringUtils.leftPad("" + (calender.get(Calendar.MONTH) + 1), 2, '0') +  StringUtils.leftPad("" + (calender.get(Calendar.DAY_OF_MONTH) + 1), 2, '0');

        return months18ago;
    };


    /*
     * Default/Pre-Defined Months
     */

    //Returns dates, Pre-defined date provided in the test class
    public static final String getGivenMonthsAgo(int months) 
    {
        Calendar calender = Calendar.getInstance();
        calender.setTimeZone(TimeZone.getTimeZone("GMT"));
        calender.add(Calendar.MONTH, -1*months);


        String givenmonthsago = "" + calender.get(Calendar.YEAR) + 
                StringUtils.leftPad("" + (calender.get(Calendar.MONTH) + 1), 2, '0') +  StringUtils.leftPad("" + (calender.get(Calendar.DAY_OF_MONTH) + 1), 2, '0');

        return givenmonthsago;
    };


    /**
     * @author msharma
     * 
     * @param adjDateString that's retrived form cosmos db
     * @param adjudicationDateStart  that's for Query parameter.
     * 
     * @return list of members when query param logic is met.
     */
    //Future Update : create static method and keep it in dto : two paramater "cosmos db", variable to pass the url ; if empty use the esisting one
    public static boolean compareDate(String adjDateString, String adjudicationDateStart) {
        // TODO Auto-generated method stub

        String months18ago = getDefault18MonthsAgo();

        //Initializing variables
        int dateInt = 0;
        int dateAdj = 0;

        if ( StringUtils.isBlank(adjudicationDateStart) ) 
        {
            //Defaults to 18 months ago from today 
            dateInt = Integer.parseInt(months18ago);
            dateAdj = Integer.parseInt(adjDateString);
        }
        else		
        {
            //Pre-defined date provided in the test class
            dateInt = Integer.parseInt(adjudicationDateStart);
            dateAdj = Integer.parseInt(adjDateString);
        }

        if ( dateAdj >= dateInt )
        {
            return true;
        }
        return false;
    }

    /**
     * @author ntagore
     * @date 01/26/2023
     * Retrieve a random  member with Rxclaim which has the rxClaimId within 18 months ago from today.
     * @param subscriptionName the member belongs to
     * @param memberId to retrieve  member claim
     */
    public synchronized static List<MemberRxclaim> getRxClaimMemberUI(String subscriptionName, String memberId)
    {
        // Build the query      
        final String query = String.format(Queries.QUERY_BY_TYPE, "rxclaim") 
                +  String.format(" and c.memberId = \"%s\"", memberId);
        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
        logger.info("******Query******"  + query);

        List<MemberRxclaim> list = executeQuery(beanName, query, MemberRxclaim.class, 100);

        return list;
    }

    /**
     * @author ntagore
     * @date 02/02/2023
     * Retrieve a random  member with Rxclaim which has the rxClaimId within 18 months ago from today if not submitted.
     * and also note length less than 40
     * @return a random  member
     */
    public synchronized static MemberRxclaim getRandomMemberWithRxclaimWithNoteAndAdjDateStart(String memberId, String subscriptionName)
    {

        // Build the query      
        final String query = String.format(Queries.QUERY_BY_TYPE, "rxclaim") 
                + String.format(" and c.memberId in ( \"%s\" ) ", memberId );  

        logger.info("******Query******"  + query);

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        List<MemberRxclaim> list = executeQuery(beanName, query, MemberRxclaim.class, 100);

        logger.info("***RxClaim member List ******  "+list);
        if ( list == null )
        {
            throw new TestConfigurationException( String.format("Member id with Note length less than 40 not found") );
        }

        return getRandomItem(list);
    }


}
