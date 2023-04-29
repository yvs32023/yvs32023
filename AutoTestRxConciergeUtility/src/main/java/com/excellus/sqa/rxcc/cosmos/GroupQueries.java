/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.Group;
import com.excellus.sqa.rxcc.dto.Tenant;

/**
 * Cosmos queries for groups
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 09/07/2022
 */
public class GroupQueries extends Queries 
{

    private static final Logger logger = LoggerFactory.getLogger(GroupQueries.class);

    private volatile static List<Group> GROUPS;
    private volatile static List<Group> GROUPS_EHP; // Cache the Group, so we don't have to run multiple queries to Cosmos
    private volatile static List<Group> GROUPS_EXE; // Cache the Group, so we don't have to run multiple queries to Cosmos

    private volatile static String QUERY_BY_TENANT = "SELECT * FROM c WHERE c.type = 'group' AND c.adTenantId='%s'";
    private static final String BEAN_NAME = "LBSContainerFormulary";
    /**
     * Retrieve list of groups 
     * @return list of groups
     */
    public synchronized static List<Group> getGroup()
    {

        if (GROUPS != null && GROUPS.size() > 0)
            return GROUPS;

        GROUPS = new ArrayList<Group>();
        GROUPS.addAll( getGroupEHP() );
        GROUPS.addAll( getGroupEXE() );

        return GROUPS;
    }

    /**
     * Retrieve list of EHP groups 
     * @return list of groups
     */
    public synchronized static List<Group> getGroupEHP()
    {

        if (GROUPS_EHP != null && GROUPS_EHP.size() > 0)
            return GROUPS_EHP;

        GROUPS_EHP = executeQuery(BeanNames.LBS_CONTAINER_FORMULARY,
                String.format(QUERY_BY_TENANT, TenantQueries.getAdTenantId(Tenant.Type.EHP.getSubscriptionName())),
                Group.class, 100);

        return GROUPS_EHP;
    }

    /**
     * Retrieve list of EXE groups 
     * @return list of groups
     */
    public synchronized static List<Group> getGroupEXE()
    {

        if (GROUPS_EXE != null && GROUPS_EXE.size() > 0)
            return GROUPS_EXE;

        GROUPS_EXE = executeQuery(BeanNames.LBS_CONTAINER_FORMULARY,
                String.format(QUERY_BY_TENANT, TenantQueries.getAdTenantId(Tenant.Type.EXE.getSubscriptionName())),
                Group.class, 100);

        return GROUPS_EXE;
    }

    /**
     * Retrieve random group
     * @return {@link Group}
     */
    public synchronized static Group getRandomGroup()
    {
        logger.info("Get random group");
        return getRandomItem(getGroup());
        //return getRandomItem(GROUPS);
    }

    /**
     * Retrieve random EHP group
     * @return {@link Group}
     */
    public synchronized static Group getRandomGroupEHP()
    {
        logger.info("Get random EHP group");
        return getRandomItem(getGroupEHP());
    }

    /**
     * Retrieve random EXE group
     * @return {@link Group}
     */
    public synchronized static Group getRandomGroupEXE()
    {
        logger.info("Get random EXE group");
        return getRandomItem(getGroupEXE());
    }

    /**
     * Retrieve the formulary group
     * 
     * @param formularyId of the formulary group
     * @return
     * 
     * @author msharma
     * @date 09/14/2022
     */
    public synchronized static List<Group> getGroup(String formularyId)
    {
        // Build the query
        String query = String.format(Queries.QUERY_BY_TYPE, "group") + 
                String.format(" and c.formularyId = \"%s\"", formularyId);


        logger.info("Retrieving formulary from Cosmos DB");
        String beanName = BeanNames.LBS_CONTAINER_FORMULARY;
        List<Group> group = executeQuery(beanName, query, Group.class, 100);

        group.sort(Comparator.comparing(Group::getCreatedDateTime).reversed());

        return group;
    }
    /**
     * Perform group search
     * @ntagore
     * @param searchTerm searchTerm to search for
     * @return list of Groups
     */
    public synchronized static List<Group> search(String searchTerm)
    {
        logger.debug("Perform Group search");
        final String theQuery = "SELECT * FROM c WHERE c.type='group' and \n"        
       + "(RegexMatch(UPPER(c.groupId), \"(?<![\\\\w])%1$s\", \"m\") \n"
                + "   or RegexMatch(UPPER(c.rxccGroupName), \"(?<![\\\\w])%1$s\", \"m\") )\n";
        String query = String.format(theQuery, searchTerm.toUpperCase());
        return executeQuery(BEAN_NAME, query, Group.class);
    }

    /**
     * Perform group search on a given tenant
     * @param searchTerm to search for
     * @param adTenantId associated with the formulary group
     * @return list of {@link Group}
     * @since 10/31/22
     * @author gcosmian
     */
    public synchronized static List<Group> search(String searchTerm, String adTenantId)
    {
        logger.debug("Perform Group search");
        final String theQuery = "SELECT * FROM c WHERE c.type='group' and \n"
                                        + "c.adTenantId = '%1$s' and \n"
                                        + "(RegexMatch(UPPER(c.groupId), \"(?<![\\\\w])%2$s\", \"m\") \n"
                                        + "   or RegexMatch(UPPER(c.rxccGroupName), \"(?<![\\\\w])%2$s\", \"m\") )\n";
        String query = String.format(theQuery, adTenantId, searchTerm.toUpperCase());
        return executeQuery(BEAN_NAME, query, Group.class);
    }

}

