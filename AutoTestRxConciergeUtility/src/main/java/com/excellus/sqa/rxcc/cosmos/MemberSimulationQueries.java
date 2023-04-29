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
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.Tenant.Type;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 10/06/2022
 */
public class MemberSimulationQueries extends Queries
{
    
    private static final Logger logger = LoggerFactory.getLogger(MemberSimulationQueries.class);

    private volatile static List<GenericCount> RANDOM_EHP_MEMBERS_WITH_SIMULATION;
    private volatile static List<GenericCount> RANDOM_EXE_MEMBERS_WITH_SIMULATION;   
    private volatile static List<GenericCount> RANDOM_LOA_MEMBERS_WITH_SIMULATION;
    private volatile static List<GenericCount> RANDOM_MED_MEMBERS_WITH_SIMULATION;  

    /**
     * Retrieve random member id with Simulation
     * @param tenantType {@link Tenant}
     * @return member id
     */
    public synchronized static String getRandomMemberWithSimulation(Tenant tenant)
    {
        return getRandomMemberWithSimulation(tenant.getSubscriptionName());
    }
    
    /**
     * Retrieve random member id with  Simulation
     * @param tenantType {@link Tenant.Type}
     * @return member id
     */
    public synchronized static String getRandomMemberWithSimulation(Tenant.Type tenantType)
    {
        return getRandomMemberWithSimulation(tenantType.getSubscriptionName());
    }
    
    /**
     * Retrieve random member id with Simulation
     * @param subscriptionName tenant subscription name
     * @return member id
     */
    public synchronized static String getRandomMemberWithSimulation(String subscriptionName)
    {
        logger.info("Retrieving random member id with simulation");
        
        if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EHP.getSubscriptionName()) )
        {
            if ( RANDOM_EHP_MEMBERS_WITH_SIMULATION == null || RANDOM_EHP_MEMBERS_WITH_SIMULATION.size() < 1 )
            {
                getMemberWithSimulation(subscriptionName);
            }
        
			GenericCount genericCount = getRandomItem(RANDOM_EHP_MEMBERS_WITH_SIMULATION);
			return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EXE.getSubscriptionName()) )
        {
            if ( RANDOM_EXE_MEMBERS_WITH_SIMULATION == null || RANDOM_EXE_MEMBERS_WITH_SIMULATION.size() < 1 )
            {
                getMemberWithSimulation(subscriptionName);
            }
            
        	GenericCount genericCount = getRandomItem(RANDOM_EXE_MEMBERS_WITH_SIMULATION);
			return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.LOA.getSubscriptionName()) )
        {
            if ( RANDOM_LOA_MEMBERS_WITH_SIMULATION == null || RANDOM_LOA_MEMBERS_WITH_SIMULATION.size() < 1 )
            {
                getMemberWithSimulation(subscriptionName);
            }
            
        	GenericCount genericCount = getRandomItem(RANDOM_LOA_MEMBERS_WITH_SIMULATION);
			return (genericCount != null) ? genericCount.getId() : null;
        }
        else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.MED.getSubscriptionName()) )
        {
            if ( RANDOM_MED_MEMBERS_WITH_SIMULATION == null || RANDOM_MED_MEMBERS_WITH_SIMULATION.size() < 1 )
            {
                getMemberWithSimulation(subscriptionName);
            }
            
        	GenericCount genericCount = getRandomItem(RANDOM_MED_MEMBERS_WITH_SIMULATION);
			return (genericCount != null) ? genericCount.getId() : null;
        }
                
        throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
    }
    
    
    /**
     * Get list of member ids with at least 1 or more simulation
     * @param subscriptionName the members belongs to
     * @return list of member ids with number of simulation
     */
    public synchronized static List<GenericCount> getMemberWithSimulation(String subscriptionName)
    {
        final String query = "SELECT * FROM ( "
                + "    SELECT r.memberId as id, r.num "
                + "    FROM ( "
                + "        SELECT c.memberId, count(1) as num FROM c WHERE c.type = \"simulation\" GROUP BY c.memberId "
                + "        ) as r "
                + ") as a "
                + "WHERE a.num > 0";
        
        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
        
        List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);
        
        if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()) )
        {
        	RANDOM_EXE_MEMBERS_WITH_SIMULATION = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()) )
        {
        	RANDOM_EHP_MEMBERS_WITH_SIMULATION = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()) )
        {
        	RANDOM_LOA_MEMBERS_WITH_SIMULATION = list;
        }
        else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()) )
        {
        	RANDOM_MED_MEMBERS_WITH_SIMULATION = list;
        }
        else
        {
            throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
        }
        
        return list;
    }
    
    
    /**
     * Retrieves all the simulation given a member id
     * 
     * @param subscriptionName the member belongs to
     * @param memberId to retrieve simulation
     * @return list of {@link MemberIntervention}
     */
    public synchronized static List<MemberIntervention> getSimulations(String subscriptionName, String memberId)
    {
        String query = String.format(Queries.QUERY_BY_TYPE, "simulation") + 
                String.format(" and c.memberId = \"%s\"", memberId);
        
        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
        List<MemberIntervention> list = executeQuery(beanName, query, MemberIntervention.class, 100);
        
        return list;
    }
    
    /**
     * Retrieves all the simulation given ruleId & simulationRunNumber
     * 
     * @param subscriptionName the member belongs to
     * @param ruleId to retrieve simulation
     * @param simulationRunNumber to retrieve simulation
     * 
	 * @author msharma 
	 * @since 10/10/2022
     * @return list of {@link MemberIntervention}
     */
    public synchronized static List<MemberIntervention> getSimulations(String subscriptionName, String ruleId, Integer simulationRunNumber)
    {
        String query = String.format(Queries.QUERY_BY_TYPE, "simulation") + 
                String.format(" and c.ruleId = \"%s\"  and c.simulationRunNumber = %s", ruleId, simulationRunNumber);
        
        String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
        List<MemberIntervention> memberSimulations = executeQuery(beanName, query, MemberIntervention.class, 100);
        
        return memberSimulations;
    }


}

