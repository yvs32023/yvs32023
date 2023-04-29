/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.MemberNote;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.Tenant.Type;



/**
 * Queries for member notes
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/14/2022
 */
public class MemberNoteQueries extends Queries 
{

    private static final Logger logger = LoggerFactory.getLogger(MemberNoteQueries.class);

    private volatile static List<GenericCount> RANDOM_EHP_MEMBERS_WITH_NOTE;
    private volatile static List<GenericCount> RANDOM_EXE_MEMBERS_WITH_NOTE;  
    private volatile static List<GenericCount> RANDOM_LOA_MEMBERS_WITH_NOTE;
    private volatile static List<GenericCount> RANDOM_MED_MEMBERS_WITH_NOTE;  


    /**
     * Retrieve the member note
     * 
     * @author gcosmian
     * @since 03/28/22
     * @param memberId of the member
     * @return
     */
    public synchronized static List<MemberNote> getMemberNote(String memberId)
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

        return getMemberNote(tenant.getSubscriptionName(), memberId);
    }

    /**
     * Retrieve the member note
     * 
     * @param subscriptionName either exe or ehp
     * @param memberId of the member
     * @return
     */
    public synchronized static List<MemberNote> getMemberNote(String subscriptionName, String memberId)
    {
        // Build the query
        String query = String.format(Queries.QUERY_BY_TYPE, "note") + 
                String.format(" and c.memberId = \"%s\"", memberId);

        logger.info("Retrieving member note from Cosmos DB");
        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
        List<MemberNote> memberNotes = executeQuery(beanName, query, MemberNote.class, 100);

        memberNotes.sort(Comparator.comparing(MemberNote::getCreateDateTime).reversed());

        return memberNotes;
    }



    /**
     * Retrieve the member note
     * 
     * @author gcosmian
     * @since 03/16/22
     * @param subscriptionName either exe or ehp
     * @param noteId of the member note
     * @param memberId of the member
     * @return
     */
    public synchronized static MemberNote getMemberNote(String subscriptionName, String noteId, String memberId)
    {
        // Build the query
        String query = String.format(Queries.QUERY_BY_TYPE, "note") + 
                String.format(" and c.id = \"%s\" and c.memberId = \"%s\"", noteId, memberId);

        logger.info("Retrieving member note from Cosmos DB");
        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
        List<MemberNote> memberNotes = executeQuery(beanName, query, MemberNote.class, 1);

        if ( memberNotes != null && memberNotes.size() > 0 )
            return memberNotes.get(0);

        return null;
    }

    /**
     * Insert member note into member container
     * 
     * @param subscriptionName either exe or ehp
     * @param note to be inserted
     */
    public synchronized static void insertMemberNote(String subscriptionName, MemberNote note)
    {
        logger.info("Inserting member note into Cosmos DB with itemId {} partition key {}", note.getId(), note.getMemberId());

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        insertItem(beanName, note.getId(), note.getMemberId(), note);
    }

    /**
     * Delete member note from EXE member container
     * 
     * @author gcosmian
     * @since 03/28/22
     * @param noteId to be deleted
     * @param memberId associated with the note to be deleted
     */
    public synchronized static void deleteMemberNote(String noteId, String memberId)
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

        deleteMemberNote(tenant.getSubscriptionName(), noteId, memberId);
    }


    /**
     * Delete member note from container
     * @param subscriptionName either exe or ehp
     * @param noteId to be deleted
     * @param memberId associated with the note to be deleted
     */
    public synchronized static void deleteMemberNote(String subscriptionName, String noteId, String memberId)
    {
        logger.info("Deleting member note from Cosmos DB with itemId {} partition key {}", noteId, memberId);

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        deleteItem(beanName, noteId, memberId, MemberNote.class);
    }


    /**
     * Retrieve random member id with note
     * @param tenantType {@link Tenant}
     * @return member id
     */
    public synchronized static String getRandomMemberWithNote(Tenant tenant)
    {
        return getRandomMemberWithNote(tenant.getSubscriptionName());
    }

    /**
     * Retrieve random member id with legacy intervention
     * @param tenantType {@link Tenant.Type}
     * @return member id
     */
    public synchronized static String getRandomMemberWithNote(Tenant.Type tenantType)
    {
        return getRandomMemberWithNote(tenantType.getSubscriptionName());
    }

    /**
     * Retrieve random member id with note
     * @param subscriptionName tenant subscription name
     * @return member id
     */
    public synchronized static String getRandomMemberWithNote(String subscriptionName)
    {
        logger.info("Retrieving random member id with note");

        if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EHP.getSubscriptionName()) )
        {
            if ( RANDOM_EHP_MEMBERS_WITH_NOTE == null || RANDOM_EHP_MEMBERS_WITH_NOTE.size() < 1 )
            {
                getMemberWithNote(subscriptionName);
            }

            GenericCount genericCount = getRandomItem(RANDOM_EHP_MEMBERS_WITH_NOTE);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EXE.getSubscriptionName()) )
        {
            if ( RANDOM_EXE_MEMBERS_WITH_NOTE == null || RANDOM_EXE_MEMBERS_WITH_NOTE.size() < 1 )
            {
                getMemberWithNote(subscriptionName);
            }

            GenericCount genericCount = getRandomItem(RANDOM_EXE_MEMBERS_WITH_NOTE);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.LOA.getSubscriptionName()) )
        {
            if ( RANDOM_LOA_MEMBERS_WITH_NOTE == null || RANDOM_LOA_MEMBERS_WITH_NOTE.size() < 1 )
            {
                getMemberWithNote(subscriptionName);
            }

            GenericCount genericCount = getRandomItem(RANDOM_LOA_MEMBERS_WITH_NOTE);
            return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.MED.getSubscriptionName()) )
        {
            if ( RANDOM_MED_MEMBERS_WITH_NOTE == null || RANDOM_MED_MEMBERS_WITH_NOTE.size() < 1 )
            {
                getMemberWithNote(subscriptionName);
            }

            GenericCount genericCount = getRandomItem(RANDOM_MED_MEMBERS_WITH_NOTE);
            return (genericCount != null) ? genericCount.getId() : null;

        }

        throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
    }

    /**
     * Get list of member ids with at least 1 or more legacy intervention
     * @param subscriptionName the members belongs to
     * @return list of member ids with number of legacy intervention
     */
    public synchronized static List<GenericCount> getMemberWithNote(String subscriptionName)
    {
        final String query = "SELECT * FROM ( "
                + "    SELECT r.memberId as id, r.num "
                + "    FROM ( "
                + "        SELECT c.memberId, count(1) as num FROM c WHERE c.type = \"note\" GROUP BY c.memberId "
                + "        ) as r "
                + ") as a "
                + "WHERE a.num > 0";

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);

        if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()) )
        {
            RANDOM_EXE_MEMBERS_WITH_NOTE = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()) )
        {
            RANDOM_EHP_MEMBERS_WITH_NOTE = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()) )
        {
            RANDOM_LOA_MEMBERS_WITH_NOTE = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()) )
        {
            RANDOM_MED_MEMBERS_WITH_NOTE = list;
        }
        else
        {
            throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
        }

        return list;
    }	

    /**
     * Get list of member ids with at note length less than 40 for UI testing
     * @author ntagore 02/01/23
     * @param String subscriptionName the member belong to
     *  @param memberId to retrieve note 
     * @return list of member ids with note length less than 40
     */
    public synchronized static String getMemberWithNoteSizeLimit(String subscriptionName)
    {
        // Build the query      
        final String query = "SELECT r.member as memberId FROM ( "
                + "    SELECT c.memberId AS member,COUNT(LENGTH(c.note) <= 40 ? 1 : undefined) AS NOTE_LESS, "
                + "    COUNT(LENGTH(c.note) > 40 ? 1 : undefined) AS NOTE_MORE  FROM c WHERE c.type = 'note' and c.alert = true "
                + "    GROUP BY c.memberId) as r "
                + "    WHERE r.NOTE_MORE = 0 ";

        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

        List<MemberNote> list = executeQuery(beanName, query, MemberNote.class, 100);
        String memberList ="";

        if (list != null && list.size() > 0) {

            for (MemberNote note : list) {
                memberList = memberList +  note.getMemberId() + "\",\"";
            }
        }else {

            throw new TestConfigurationException( String.format("Member id with Note length less than 40 not found") );
        }
        memberList = StringUtils.removeEnd(memberList, "\",\"");


        return memberList;
    }

}

