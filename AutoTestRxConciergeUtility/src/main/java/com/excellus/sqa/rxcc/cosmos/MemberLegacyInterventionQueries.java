/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.MemberLegacyIntervention;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.Tenant.Type;

/**
 * 
 * 
 * @author Neeru Tagore (ntagore)
 * @since 09/09/2022
 */
public class MemberLegacyInterventionQueries  extends Queries
{

    private static final Logger logger = LoggerFactory.getLogger(MemberLegacyInterventionQueries.class);

    private volatile static List<GenericCount> RANDOM_EHP_MEMBERS_WITH_LEGACY_INTERVENTION;
    private volatile static List<GenericCount> RANDOM_EXE_MEMBERS_WITH_LEGACY_INTERVENTION; 
    private volatile static List<GenericCount> RANDOM_LOA_MEMBERS_WITH_LEGACY_INTERVENTION;
    private volatile static List<GenericCount> RANDOM_MED_MEMBERS_WITH_LEGACY_INTERVENTION;

    /**
     * Retrieve random member id with legacy intervention
     * @param tenantType {@link Tenant}
     * @return member id
     */
    public synchronized static String getRandomMemberWithLegacyIntervention(Tenant tenant)
    {
        return getRandomMemberWithLegacyIntervention(tenant.getSubscriptionName());
    }

    /**
     * Retrieve random member id with legacy intervention
     * @param tenantType {@link Tenant.Type}
     * @return member id
     */
    public synchronized static String getRandomMemberWithLegacyIntervention(Tenant.Type tenantType)
    {
        return getRandomMemberWithLegacyIntervention(tenantType.getSubscriptionName());
    }

    /**
     * Retrieve random member id with legacy intervention
     * @param subscriptionName tenant subscription name
     * @return member id
     */
    public synchronized static String getRandomMemberWithLegacyIntervention(String subscriptionName)
    {
        logger.info("Retrieving random member id with legacy intervention");

        if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EHP.getSubscriptionName()) )
        {
            if ( RANDOM_EHP_MEMBERS_WITH_LEGACY_INTERVENTION == null || RANDOM_EHP_MEMBERS_WITH_LEGACY_INTERVENTION.size() < 1 )
            {
                getMemberWithLegacyIntervention(subscriptionName);
            }

            GenericCount genericCount = getRandomItem(RANDOM_EHP_MEMBERS_WITH_LEGACY_INTERVENTION);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EXE.getSubscriptionName()) )
        {
            if ( RANDOM_EXE_MEMBERS_WITH_LEGACY_INTERVENTION == null || RANDOM_EXE_MEMBERS_WITH_LEGACY_INTERVENTION.size() < 1 )
            {
                getMemberWithLegacyIntervention(subscriptionName);
            }

            GenericCount genericCount = getRandomItem(RANDOM_EXE_MEMBERS_WITH_LEGACY_INTERVENTION);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.LOA.getSubscriptionName()) )
        {
            if ( RANDOM_LOA_MEMBERS_WITH_LEGACY_INTERVENTION == null || RANDOM_LOA_MEMBERS_WITH_LEGACY_INTERVENTION.size() < 1 )
            {
                getMemberWithLegacyIntervention(subscriptionName);
            }

            GenericCount genericCount = getRandomItem(RANDOM_LOA_MEMBERS_WITH_LEGACY_INTERVENTION);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.MED.getSubscriptionName()) )
        {
            if ( RANDOM_MED_MEMBERS_WITH_LEGACY_INTERVENTION == null || RANDOM_MED_MEMBERS_WITH_LEGACY_INTERVENTION.size() < 1 )
            {
                getMemberWithLegacyIntervention(subscriptionName);
            }

            GenericCount genericCount = getRandomItem(RANDOM_MED_MEMBERS_WITH_LEGACY_INTERVENTION);
            return (genericCount != null) ? genericCount.getId() : null;
        }

        throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
    }


    /**
     * Get list of member ids with at least 1 or more legacy intervention
     * @param subscriptionName the members belongs to
     * @return list of member ids with number of legacy intervention
     */
    public synchronized static List<GenericCount> getMemberWithLegacyIntervention(String subscriptionName)
    {
        final String query = "SELECT * FROM ( "
                + "    SELECT r.memberId as id, r.num "
                + "    FROM ( "
                + "        SELECT c.memberId, count(1) as num FROM c WHERE c.type = \"legacyintervention\" GROUP BY c.memberId "
                + "        ) as r "
                + ") as a "
                + "WHERE a.num > 0";

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);

        if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()) )
        {
            RANDOM_EXE_MEMBERS_WITH_LEGACY_INTERVENTION = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()) )
        {
            RANDOM_EHP_MEMBERS_WITH_LEGACY_INTERVENTION = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()) )
        {
            RANDOM_LOA_MEMBERS_WITH_LEGACY_INTERVENTION = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()) )
        {
            RANDOM_MED_MEMBERS_WITH_LEGACY_INTERVENTION = list;
        }
        else
        {
            throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
        }

        return list;
    }


    /**
     * Retrieves all the legacy intervention given a member id
     * 
     * @param subscriptionName the member belongs to
     * @param memberId to retrieve legacy intervention
     * @return list of {@link MemberLegacyIntervention}
     */
    public synchronized static List<MemberLegacyIntervention> getLegacyIntervention(String subscriptionName, String memberId)
    {
        String query = String.format(Queries.QUERY_BY_TYPE, "legacyintervention") + 
                String.format(" and c.memberId = \"%s\"", memberId);

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
        List<MemberLegacyIntervention> list = executeQuery(beanName, query, MemberLegacyIntervention.class, 100);

        return list;
    }
    /**
     * This method is used to get the random member name with Legacy Intervention UI
     * @author ntagore 02/17/2023
     * @param tenantType  Type of the Tenant
     * @param memberNoteList list of members name
     * @return returns a random member name with Legacy Intervention UI
     */

    public synchronized static String getRandomMemberWithLegacyInterventionUI(Tenant.Type tenantType,String memberNoteList)
    {
        return getRandomMemberWithLegacyInterventionUI(tenantType.getSubscriptionName(), memberNoteList);
    }

    /**
     * Get random member id with legacy intervention.
     * @author ntagore 02/17/2023
     * @param subscriptionName the subscriptionName
     * @param memberNoteList the memberNoteList
     * @return member id
     */
    public synchronized static String getRandomMemberWithLegacyInterventionUI(String subscriptionName, String memberNoteList)
    {
        logger.info("Retrieving random member id with legacy intervention");

        if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EHP.getSubscriptionName()) )
        {
            if ( RANDOM_EHP_MEMBERS_WITH_LEGACY_INTERVENTION == null || RANDOM_EHP_MEMBERS_WITH_LEGACY_INTERVENTION.size() < 1 )
            {
                getMemberWithLegacyInterventionUI(subscriptionName, memberNoteList);
            }

            GenericCount genericCount = getRandomItem(RANDOM_EHP_MEMBERS_WITH_LEGACY_INTERVENTION);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EXE.getSubscriptionName()) )
        {
            if ( RANDOM_EXE_MEMBERS_WITH_LEGACY_INTERVENTION == null || RANDOM_EXE_MEMBERS_WITH_LEGACY_INTERVENTION.size() < 1 )
            {
                getMemberWithLegacyInterventionUI(subscriptionName,memberNoteList);
            }

            GenericCount genericCount = getRandomItem(RANDOM_EXE_MEMBERS_WITH_LEGACY_INTERVENTION);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.LOA.getSubscriptionName()) )
        {
            if ( RANDOM_LOA_MEMBERS_WITH_LEGACY_INTERVENTION == null || RANDOM_LOA_MEMBERS_WITH_LEGACY_INTERVENTION.size() < 1 )
            {
                getMemberWithLegacyInterventionUI(subscriptionName,memberNoteList);
            }

            GenericCount genericCount = getRandomItem(RANDOM_LOA_MEMBERS_WITH_LEGACY_INTERVENTION);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.MED.getSubscriptionName()) )
        {
            if ( RANDOM_MED_MEMBERS_WITH_LEGACY_INTERVENTION == null || RANDOM_MED_MEMBERS_WITH_LEGACY_INTERVENTION.size() < 1 )
            {
                getMemberWithLegacyInterventionUI(subscriptionName,memberNoteList);
            }

            GenericCount genericCount = getRandomItem(RANDOM_MED_MEMBERS_WITH_LEGACY_INTERVENTION);
            return (genericCount != null) ? genericCount.getId() : null;
        }

        throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
    }

    /**
     * This method is used to get Member with Legacy Intervention UI.
     * @author ntagore 02/17/2023
     * @param subscriptionName subscription name of the tenant
     * @param memberNoteList list of members
     * @return List of {@link GenericCount}
     */

    public synchronized static List<GenericCount> getMemberWithLegacyInterventionUI(String subscriptionName, String memberNoteList)
    {
        final String query = "SELECT * FROM ( "
                + "    SELECT r.memberId as id, r.num "
                + "    FROM ( "
                + "        SELECT c.memberId, count(1) as num FROM c WHERE c.type = \"legacyintervention\" "
                +      String.format(" and c.memberId in ( \"%s\" ) ", memberNoteList )
                + "GROUP BY c.memberId "
                + "        ) as r "
                + ") as a "
                + "WHERE a.num > 0";

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);

        if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()) )
        {
            RANDOM_EXE_MEMBERS_WITH_LEGACY_INTERVENTION = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()) )
        {
            RANDOM_EHP_MEMBERS_WITH_LEGACY_INTERVENTION = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()) )
        {
            RANDOM_LOA_MEMBERS_WITH_LEGACY_INTERVENTION = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()) )
        {
            RANDOM_MED_MEMBERS_WITH_LEGACY_INTERVENTION = list;
        }
        else
        {
            throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
        }

        return list;
    }

    /**
     * @author ntagore
     * @date 02/17/2023
     * Retrieve a random  member with legacy intervention
     * @param memberId
     * @param subscriptionname
     * @return a random  member
     */

    public synchronized static MemberLegacyIntervention getLegacyInterventionWithNoteUI( String memberId, String subscriptionName)
    {
       final String query = String.format(Queries.QUERY_BY_TYPE, "legacyintervention")
                + String.format(" and c.memberId in ( \"%s\" ) ", memberId );

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
        
        List<MemberLegacyIntervention> list = executeQuery(beanName, query, MemberLegacyIntervention.class, 100);
        if ( list == null )
        {
            throw new TestConfigurationException( String.format("Member id with Note length less than 40 not found") );
        }

        return getRandomItem(list);
    }

}
