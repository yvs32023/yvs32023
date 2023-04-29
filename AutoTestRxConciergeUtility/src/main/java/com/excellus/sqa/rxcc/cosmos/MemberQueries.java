/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.Tenant.Type;

/**
 * Cosmos DB queries to EXE/EHP member container where type is 'member'
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 03/02/2022
 */
public class MemberQueries extends Queries
{

    private static final Logger logger = LoggerFactory.getLogger(MemberQueries.class);

    private volatile static List<Member> RANDOM_EXE_MEMBERS;    // contains random EXE member
    private volatile static List<Member> RANDOM_EHP_MEMBERS;    // contains random EHP member
    private volatile static List<Member> RANDOM_LOA_MEMBERS;
    private volatile static List<Member> RANDOM_MED_MEMBERS;

    private volatile static List<Member> RANDOM_EXE_NON_DECEASED_MEMBERS;    // contains random EXE non-deceased member
    private volatile static List<Member> RANDOM_EHP_NON_DECEASED_MEMBERS;    // contains random EHP non-deceased member
    private volatile static List<Member> RANDOM_LOA_NON_DECEASED_MEMBERS;    // contains random LOA non-deceased member
    private volatile static List<Member> RANDOM_MED_NON_DECEASED_MEMBERS;    // contains random MED non-deceased member


    /**
     * Retrieve a random member
     * 
     * @author gcosmian
     * @since 03/28/22
     * 
     * @param type Tenant type
     * @return a random member
     */
    public synchronized static Member getRandomMember(Tenant.Type type)
    {
        switch (type) 
        {
        case EHP:
            return getRandomMemberType(Tenant.Type.EHP.getSubscriptionName());

        case EXE:
            return getRandomMemberType(Tenant.Type.EXE.getSubscriptionName());

        case LOA:
            return getRandomMemberType(Tenant.Type.LOA.getSubscriptionName());

        case MED:
            return getRandomMemberType(Tenant.Type.MED.getSubscriptionName());

        default:
            throw new TestConfigurationException(String.format("Invalid/Unsupported tenant [%s].", type) );
        }
    }


    /**
     * Retrieve a random member based on subscriptionName
     *
     * @return a random  member based on subscriptionName
     */
    public synchronized static Member getRandomMemberType(String subscriptionName)
    {
        if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EXE.getSubscriptionName())) 
        {
            if ( (RANDOM_EXE_MEMBERS != null && RANDOM_EXE_MEMBERS.size() > 0) )
            {
                return getRandomItem(RANDOM_EXE_MEMBERS);
            }

            RANDOM_EXE_MEMBERS = getRandomMembers(BeanNames.EXE_CONTAINER_MEMBER, "where not IS_DEFINED(c.medBenefitId) and not IS_DEFINED(c.medBenefitDescr)", DEFAULT_MAX_BUFFER_ITEM_COUNT);
            return getRandomItem(RANDOM_EXE_MEMBERS);
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EHP.getSubscriptionName())) 
        {
            if ( (RANDOM_EHP_MEMBERS != null && RANDOM_EHP_MEMBERS.size() > 0) )
            {
                return getRandomItem(RANDOM_EHP_MEMBERS);
            }

            RANDOM_EHP_MEMBERS = getRandomMembers(BeanNames.EHP_CONTAINER_MEMBER, null, DEFAULT_MAX_BUFFER_ITEM_COUNT);
            return getRandomItem(RANDOM_EHP_MEMBERS);
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.LOA.getSubscriptionName())) 
        {
            if ( (RANDOM_LOA_MEMBERS != null && RANDOM_LOA_MEMBERS.size() > 0) )
            {
                return getRandomItem(RANDOM_LOA_MEMBERS);
            }

            RANDOM_LOA_MEMBERS = getRandomMembers(BeanNames.LOA_CONTAINER_MEMBER, null, DEFAULT_MAX_BUFFER_ITEM_COUNT);
            return getRandomItem(RANDOM_LOA_MEMBERS);
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.MED.getSubscriptionName())) 
        {
            if ( (RANDOM_MED_MEMBERS != null && RANDOM_MED_MEMBERS.size() > 0) )
            {
                return getRandomItem(RANDOM_MED_MEMBERS);
            }

            RANDOM_MED_MEMBERS = getRandomMembers(BeanNames.MED_CONTAINER_MEMBER, null, DEFAULT_MAX_BUFFER_ITEM_COUNT);
            return getRandomItem(RANDOM_MED_MEMBERS);
        }

        throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
    }


    /**
     * Retrieve a random member with provided query(whereClause extension)
     *
     * @return a random  member based with provided query(whereClause extension)
     */
    public synchronized static List<Member> getSpecificMember(String subscriptionName, String whereClause, int size)
    {
        if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EXE.getSubscriptionName())) 
        {
            if ( StringUtils.isBlank(whereClause) ) {
                throw new TestConfigurationException("The whereClause cannot be null or empty");
            }

            return getRandomMembers(BeanNames.EXE_CONTAINER_MEMBER, whereClause, size);
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EHP.getSubscriptionName())) 
        {
            if ( StringUtils.isBlank(whereClause) ) {
                throw new TestConfigurationException("The whereClause cannot be null or empty");
            }

            return getRandomMembers(BeanNames.EHP_CONTAINER_MEMBER, whereClause, size);
        }

        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.LOA.getSubscriptionName())) 
        {
            if ( StringUtils.isBlank(whereClause) ) {
                throw new TestConfigurationException("The whereClause cannot be null or empty");
            }

            return getRandomMembers(BeanNames.LOA_CONTAINER_MEMBER, whereClause, size);
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.MED.getSubscriptionName())) 
        {
            if ( StringUtils.isBlank(whereClause) ) {
                throw new TestConfigurationException("The whereClause cannot be null or empty");
            }

            return getRandomMembers(BeanNames.MED_CONTAINER_MEMBER, whereClause, size);
        }

        throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
    }


    /**
     * Perform query to the member tenant
     * @param beanName of the tenant defined in {@link com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig}
     * @param size of the members to retrieve
     * @return list of members
     */
    private synchronized static List<Member> getRandomMembers(String beanName, String whereClause, int size)
    {
        // Setup the where clause
        whereClause = StringUtils.isNotBlank(whereClause) ?
                RegExUtils.replaceFirst(whereClause, "where", "") : "";

        String query = String.format(Queries.QUERY_BY_TYPE, "member");
        query += StringUtils.isNotBlank(whereClause) ? " and " + whereClause.trim() : "";

        logger.info("Retrieving member from Cosmos DB");
        List<Member> memberList = executeQuery(beanName, query, Member.class, size);

        return memberList;
    }


    /**
     * Retrieve a random non-deceased member
     *
     * @author gcosmian
     * @since 03/21/22
     * @param subscriptionId of the tenant, either exe, ehp, loa or med
     * @return a random  member
     */
    public synchronized static Member getRandomNonDeceasedMember(String subscriptionId)
    {
        if ( StringUtils.equalsIgnoreCase(subscriptionId, Tenant.Type.EXE.getSubscriptionName()) )
        {
            if ( (RANDOM_EXE_NON_DECEASED_MEMBERS != null && RANDOM_EXE_NON_DECEASED_MEMBERS.size() > 0) )
            {
                return getRandomItem(RANDOM_EXE_NON_DECEASED_MEMBERS);
            }

            RANDOM_EXE_NON_DECEASED_MEMBERS = getRandomMembers(BeanNames.EXE_CONTAINER_MEMBER, "where c.deceased = false", 50);
            return getRandomItem(RANDOM_EXE_NON_DECEASED_MEMBERS);
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionId, Tenant.Type.EHP.getSubscriptionName()) )
        {
            if ( (RANDOM_EHP_NON_DECEASED_MEMBERS != null && RANDOM_EHP_NON_DECEASED_MEMBERS.size() > 0) )
            {
                return getRandomItem(RANDOM_EHP_NON_DECEASED_MEMBERS);
            }

            RANDOM_EHP_NON_DECEASED_MEMBERS = getRandomMembers(BeanNames.EHP_CONTAINER_MEMBER, "where c.deceased = false", 50);
            return getRandomItem(RANDOM_EHP_NON_DECEASED_MEMBERS);
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionId, Tenant.Type.LOA.getSubscriptionName()) )
        {
            if ( (RANDOM_LOA_NON_DECEASED_MEMBERS != null && RANDOM_LOA_NON_DECEASED_MEMBERS.size() > 0) )
            {
                return getRandomItem(RANDOM_LOA_NON_DECEASED_MEMBERS);
            }

            RANDOM_LOA_NON_DECEASED_MEMBERS = getRandomMembers(BeanNames.LOA_CONTAINER_MEMBER, "where c.deceased = false", 50);
            return getRandomItem(RANDOM_LOA_NON_DECEASED_MEMBERS);
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionId, Tenant.Type.MED.getSubscriptionName()) )
        {
            if ( (RANDOM_MED_NON_DECEASED_MEMBERS != null && RANDOM_MED_NON_DECEASED_MEMBERS.size() > 0) )
            {
                return getRandomItem(RANDOM_MED_NON_DECEASED_MEMBERS);
            }

            RANDOM_MED_NON_DECEASED_MEMBERS = getRandomMembers(BeanNames.MED_CONTAINER_MEMBER, "where c.deceased = false", 50);
            return getRandomItem(RANDOM_MED_NON_DECEASED_MEMBERS);
        }
        throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe or ehp", subscriptionId) );
    }

    /**
     * Replace an existing member
     * 
     * @author gcosmian
     * @since 03/21/22
     * @param subscriptionId of the tenant, either exe, ehp, loa or med
     * @param member {@link Member} to be updated
     * @param isDeceased the value to update the field 'deceased'
     * @param isOptInComm the value to update the field 'optinComm'
     * @param correspondentType the value to update the field 'correspondenceType'
     */
    public synchronized static void replaceMember(String subscriptionId, Member member, Boolean isDeceased, Boolean isOptInComm, String correspondentType)
    {
        // Do nothing if all param is null
        if ( member == null && isDeceased == null && isOptInComm == null && correspondentType == null )
            return;

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionId);

        /*
         * Build the fields to be updated
         */

        Map<String, Object> patchSet = new HashMap<String, Object>();

        if ( isDeceased != null )
            patchSet.put("/deceased", isDeceased);

        if ( isOptInComm != null )
            patchSet.put("/optinComm", isOptInComm);

        if ( StringUtils.isNotBlank(correspondentType) )
            patchSet.put("/correspondenceType", correspondentType);

        // Call Cosmos DB patch/update
        replaceItem(beanName, member.getId(), member.getId(), member, Member.class);
    }

    /**
     * Get an existing member
     * 
     * @author gcosmian
     * @since 03/21/22
     * @param subscriptionId of the tenant, either exe, ehp, loa or med
     * @param memberId of the member to get
     * @return {@link Member} that correspond to the memberId
     */
    public synchronized static Member getMember(String subscriptionId, String memberId)
    {
        // Do nothing if member is not provided
        if ( StringUtils.isBlank(memberId) )
            return null;

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionId);

        // Query Cosmos DB
        return retrieveItem(beanName, memberId, memberId, Member.class);
    }

    /**
     * Get an existing member
     *
     * @author gcosmian
     * @since 01/05/23
     * @param memberId of the member to get
     * @return {@link Member} that correspond to the memberId
     */
    public synchronized static Member getMemberById(String memberId)
    {
        // Do nothing if member is not provided
        if ( StringUtils.isBlank(memberId) )
            return null;

        Tenant tenant = TenantQueries.getTenantById(memberId);
        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(tenant.getSubscriptionName());

        // Query Cosmos DB
        return retrieveItem(beanName, memberId, memberId, Member.class);
    }

    /**
     * Insert member into member container
     * 
     * @since 04/05/22
     * @author gcosmian
     * @param subscriptionName either either exe, ehp, loa or med
     * @param member to insert
     */
    public static void insertMember(String subscriptionName, Member member) {
        logger.info("Inserting member into Cosmos DB with itemId {} partition key {}", member.getId(), member.getMemberId());

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        insertItem(beanName, member.getId(), member.getMemberId(), member);
    }

    /**
     * Delete member
     * 
     * @since 04/05/22
     * @author gcosmian
     * @param subscriptionName either exe, ehp, loa or med
     * @param id to be deleted
     * @param memberId to be deleted
     */
    public static void deleteMember(String subscriptionName, String id, String memberId) 
    {
        logger.info("Deleting member from Cosmos DB with itemId {} partition key {}", id, memberId);

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        deleteItem(beanName, id, memberId, Member.class);
    }


    /**
     * @author ntagore
     * 02/15/2023
     * This method is used to get the random non deceased member from the container.
     * @param memberId   MemberId for which the details are to be fetched
     * @param subscriptionName   SubscriptionName for which the details are to be fetched
     * Returns the Member details
     */
    public synchronized static Member getRandomNonDeceasedMemberUI(String memberId, String subscriptionName)
    {

        // Build the query      
        final String query = String.format(Queries.QUERY_BY_TYPE, "member") 
                + String.format(" and c.memberId in ( \"%s\" ) ", memberId );  

        logger.info("******Query******"  + query);

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        List<Member> list = executeQuery(beanName, query, Member.class, 100);

        logger.info("*** member List ******  "+list);
        if ( list == null )
        {
            throw new TestConfigurationException( String.format("Member id with Note length less than 40 not found") );
        }

        return getRandomItem(list);
    }

}
