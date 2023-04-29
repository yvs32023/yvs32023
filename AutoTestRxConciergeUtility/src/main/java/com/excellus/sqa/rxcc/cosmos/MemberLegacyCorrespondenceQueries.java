/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.Tenant.Type;

/**
 * 
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 09/06/2022
 */
public class MemberLegacyCorrespondenceQueries extends Queries 
{

    private static final Logger logger = LoggerFactory.getLogger(MemberLegacyCorrespondenceQueries.class);

    private volatile static List<GenericCount> RANDOM_EHP_MEMBERS_WITH_LEGACY_CORRESPONDENCE;
    private volatile static List<GenericCount> RANDOM_EXE_MEMBERS_WITH_LEGACY_CORRESPONDENCE;	
    private volatile static List<GenericCount> RANDOM_LOA_MEMBERS_WITH_LEGACY_CORRESPONDENCE;
    private volatile static List<GenericCount> RANDOM_MED_MEMBERS_WITH_LEGACY_CORRESPONDENCE;	

    private volatile static Map<String, List<GenericCount>> MEMBERS_WITH_LEGACY_CORRESPONDENCE_LIST = new HashMap<>();

    /**
     * Retrieve random member id with legacy correspondence
     * @param tenantType {@link Tenant}
     * @return member id
     */
    public synchronized static String getRandomMemberWithLegacyCorrespondence(Tenant tenant)
    {
        return getRandomMemberWithLegacyCorrespondence(tenant.getSubscriptionName());
    }

    /**
     * Retrieve random member id with legacy correspondence
     * @param tenantType {@link Tenant.Type}
     * @return member id
     */
    public synchronized static String getRandomMemberWithLegacyCorrespondence(Tenant.Type tenantType)
    {
        return getRandomMemberWithLegacyCorrespondence(tenantType.getSubscriptionName());
    }

    /**
     * Retrieve random member id with legacy correspondence
     * @param subscriptionName tenant subscription name
     * @return member id
     */
    public synchronized static String getRandomMemberWithLegacyCorrespondence(String subscriptionName)
    {
        logger.info("Retrieving random member id with legacy correspondence");

        if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EHP.getSubscriptionName()) )
        {
            if ( RANDOM_EHP_MEMBERS_WITH_LEGACY_CORRESPONDENCE == null || RANDOM_EHP_MEMBERS_WITH_LEGACY_CORRESPONDENCE.size() < 1 )
            {
                getMemberWithLegacyCorrespondence(subscriptionName);
            }
            GenericCount genericCount = getRandomItem(RANDOM_EHP_MEMBERS_WITH_LEGACY_CORRESPONDENCE);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EXE.getSubscriptionName()) )
        {
            if ( RANDOM_EXE_MEMBERS_WITH_LEGACY_CORRESPONDENCE == null || RANDOM_EXE_MEMBERS_WITH_LEGACY_CORRESPONDENCE.size() < 1 )
            {
                getMemberWithLegacyCorrespondence(subscriptionName);
            }
            GenericCount genericCount = getRandomItem(RANDOM_EXE_MEMBERS_WITH_LEGACY_CORRESPONDENCE);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.LOA.getSubscriptionName()) )
        {
            if ( RANDOM_LOA_MEMBERS_WITH_LEGACY_CORRESPONDENCE == null || RANDOM_LOA_MEMBERS_WITH_LEGACY_CORRESPONDENCE.size() < 1 )
            {
                getMemberWithLegacyCorrespondence(subscriptionName);
            }
            GenericCount genericCount = getRandomItem(RANDOM_LOA_MEMBERS_WITH_LEGACY_CORRESPONDENCE);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.MED.getSubscriptionName()) )
        {
            if ( RANDOM_MED_MEMBERS_WITH_LEGACY_CORRESPONDENCE == null || RANDOM_MED_MEMBERS_WITH_LEGACY_CORRESPONDENCE.size() < 1 )
            {
                getMemberWithLegacyCorrespondence(subscriptionName);
            }
            GenericCount genericCount = getRandomItem(RANDOM_MED_MEMBERS_WITH_LEGACY_CORRESPONDENCE);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
    }


    /**
     * Get list of member ids with at least 5 or more legacy correspondence
     * @param subscriptionName the members belongs to
     * @return list of member ids with number of legacy correspondence
     */
    public synchronized static List<GenericCount> getMemberWithLegacyCorrespondence(String subscriptionName)
    {
        final String query = "SELECT * FROM ( "
                + "    SELECT r.memberId as id, r.num "
                + "    FROM ( "
                + "        SELECT c.memberId, count(1) as num FROM c WHERE c.type = \"legacycorrespondence\" GROUP BY c.memberId "
                + "        ) as r "
                + ") as a "
                + "WHERE a.num > 4";

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);

        if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()) )
        {
            RANDOM_EXE_MEMBERS_WITH_LEGACY_CORRESPONDENCE = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()) )
        {
            RANDOM_EHP_MEMBERS_WITH_LEGACY_CORRESPONDENCE = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()) )
        {
            RANDOM_LOA_MEMBERS_WITH_LEGACY_CORRESPONDENCE = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()) )
        {
            RANDOM_MED_MEMBERS_WITH_LEGACY_CORRESPONDENCE = list;
        }
        else
        {
            throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
        }

        return list;
    }


    /**
     * Retrieves all the legacy correspondence given a member id
     * 
     * @param subscriptionName the member belongs to
     * @param memberId to retrieve legacy correspondence
     * @return list of {@link MemberCorrespondence}
     */
    public synchronized static List<MemberCorrespondence> getLegacyCorrespondence(String subscriptionName, String memberId)
    {
        String query = String.format(Queries.QUERY_BY_TYPE, "legacycorrespondence") + 
                String.format(" and c.memberId = \"%s\"", memberId);

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
        List<MemberCorrespondence> list = executeQuery(beanName, query, MemberCorrespondence.class, 100);

        return list;
    }
    /**
     * This method is used to get the random member name with Legacy Correspondence UI
     * @author ntagore 02/16/2023
     * @param tenantType  Type of the Tenant
     * @param memberNoteList list of members name
     * @return returns a random member name with Legacy Correspondence UI
     */

    public synchronized static String getRandomMemberWithLegacyCorrespondenceUI(Tenant.Type tenantType, String memberNoteList)
    {
        return getRandomMemberWithLegacyCorrespondenceUI(tenantType.getSubscriptionName(), memberNoteList);
    }

    /**
     * Get random member id with legacy correspondence.
     * @author ntagore 02/16/2023
     * @param subscriptionName the subscriptionName
     * @param memberNoteList the memberNoteList
     * @return member id
     */
    public synchronized static String getRandomMemberWithLegacyCorrespondenceUI(String subscriptionName, String memberNoteList)
    {

        logger.info("Retrieving random member id with legacy correspondence");

        String key = subscriptionName;
        if ( !MEMBERS_WITH_LEGACY_CORRESPONDENCE_LIST.containsKey(key) )
        {
            getMemberWithLegacyCorrespondenceUI(subscriptionName,memberNoteList);
        }

        GenericCount genericCount = getRandomItem( MEMBERS_WITH_LEGACY_CORRESPONDENCE_LIST.get(key) );
        return (genericCount != null) ? genericCount.getId() : null;
    }

    /**
     * This method is used to get Member with Legacy Correspondence UI.
     * @author ntagore 02/16/2023
     * @param subscriptionName subscription name of the tenant
     * @param memberNoteList list of members
     * @return List of {@link GenericCount}
     */
    public synchronized static List<GenericCount> getMemberWithLegacyCorrespondenceUI(String subscriptionName, String memberNoteList)
    {
        final String query = "SELECT * FROM ( "
                + "    SELECT r.memberId as id, r.num "
                + "    FROM ( "
                + "        SELECT c.memberId, count(1) as num FROM c WHERE c.type = \"legacycorrespondence\" "
                +      String.format(" and c.memberId in ( \"%s\" ) ", memberNoteList )
                + "GROUP BY c.memberId "
                + "        ) as r "
                + ") as a "
                + "WHERE a.num > 4";

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);
        String key = subscriptionName;
        MEMBERS_WITH_LEGACY_CORRESPONDENCE_LIST.put(key, list);

        return list;
    } 

    /**
     * @author ntagore
     * @date 02/16/2023
     * Retrieve a random  member with legacy Correspondence
     * @param memberId
     * @param subscriptionname
     * @return a random  member
     */
    public synchronized static MemberCorrespondence getRandomMemberWithLegacyCorrespondenceandNote(String memberId, String subscriptionName)
    {

        // Build the query     
        final String query = String.format(Queries.QUERY_BY_TYPE, "legacycorrespondence") 
                + String.format(" and c.memberId in ( \"%s\" ) ", memberId );  

        logger.info("******Query******"  + query);

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        List<MemberCorrespondence> list = executeQuery(beanName, query, MemberCorrespondence.class, 100);

        logger.info("***RxClaim member List ******  "+list);
        if ( list == null )
        {
            throw new TestConfigurationException( String.format("Member id with Note length less than 40 not found") );
        }

        return getRandomItem(list);
    }

}

