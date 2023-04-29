/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.InterventionRule;



/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 06/13/2022
 */
public class InterventionRuleQueries extends Queries 
{

	private static final Logger logger = LoggerFactory.getLogger(InterventionRuleQueries.class);

	private volatile static Queue<InterventionRule> INTERVENTIONRULE; // Cache the intervention, so we don't have to run multiple queries to Cosmos
	
	private volatile static List<InterventionRule> INTERVENTIONRULES; // added for UI validation to avoid use of cache 12/14/22 @ntagore
	
	private static final String BEAN_NAME =  BeanNames.LBS_CONTAINER_INTERVENTION_RULE;
	
	
	/**
	 * Query 100 INTERVENTIONRULE
	 * 
	 * @return list of {@link InterventionRule}
	 */
	@SuppressWarnings("unchecked")
	public synchronized static List<InterventionRule> getInterventionRules()
	{

		if (INTERVENTIONRULE != null && INTERVENTIONRULE.size() > 0)
			return (List<InterventionRule>) INTERVENTIONRULE;

		List<InterventionRule> interventionRule = executeQuery(BeanNames.LBS_CONTAINER_INTERVENTION_RULE, String.format(Queries.QUERY_BY_TYPE, "interventionrule"), InterventionRule.class, 100);
		INTERVENTIONRULE = new LinkedList<>(interventionRule);

		return (List<InterventionRule>) INTERVENTIONRULE;

	}
	
	/**
	 * Query 100 INTERVENTIONRULE and added extension of whereClause
	 * 
	 * 
	 * @return list of {@link InterventionRule with added extension of whereClause}
	 */
	@SuppressWarnings("unchecked") public synchronized static List<InterventionRule> getInterventionRules(String whereClause) {

		if ( INTERVENTIONRULE != null && INTERVENTIONRULE.size() > 0) 
			return (List<InterventionRule>) INTERVENTIONRULE;

		if ( !StringUtils.startsWithIgnoreCase(whereClause, "where") )
			throw new TestConfigurationException("Invalid where clause, see https://docs.microsoft.com/en-us/azure/cosmos-db/sql/sql-query-where#arguments");

        String query = String.format(Queries.QUERY_BY_TYPE, "interventionrule");

		whereClause = StringUtils.isNotBlank(whereClause) ?
				RegExUtils.replaceFirst(whereClause, "where", "") : "";

		query += StringUtils.isNotBlank(whereClause) ? " and " + whereClause.trim() : "";

		List<InterventionRule> interventionRule = executeQuery(BeanNames.LBS_CONTAINER_INTERVENTION_RULE, query, InterventionRule.class, 100); 
		INTERVENTIONRULE = new LinkedList<>(interventionRule);

		return (List<InterventionRule>) INTERVENTIONRULE;

	}
	
	/**
     * added another method for UI validation to avoid cache
     * 
     * @ntagore 12/14/22
     * @return list of {@link InterventionRule with added extension of whereClause}
     */
    public synchronized static List<InterventionRule> getInterventionRulesUI(String whereClause) {

        if ( INTERVENTIONRULES != null && INTERVENTIONRULES.size() > 0) 
            return (List<InterventionRule>) INTERVENTIONRULES;

        if ( !StringUtils.startsWithIgnoreCase(whereClause, "where") )
            throw new TestConfigurationException("Invalid where clause, see https://docs.microsoft.com/en-us/azure/cosmos-db/sql/sql-query-where#arguments");

        String query = String.format(Queries.QUERY_BY_TYPE, "interventionrule");

        whereClause = StringUtils.isNotBlank(whereClause) ?
                RegExUtils.replaceFirst(whereClause, "where", "") : "";

        query += StringUtils.isNotBlank(whereClause) ? " and " + whereClause.trim() : "";

        logger.info("********* query ************" + query);

        List<InterventionRule> interventionRule = executeQuery(BeanNames.LBS_CONTAINER_INTERVENTION_RULE, query, InterventionRule.class, 100); 
        INTERVENTIONRULES = new LinkedList<>(interventionRule);

        return (List<InterventionRule>) INTERVENTIONRULES;
    }

	/**
	 * Retrieve a random interventionRule from LBS container
	 * 
	 * 
	 * @return a random interventionRule from LBS container
	 */		  
	public synchronized static InterventionRule getRandomInterventionRule() {

		if (INTERVENTIONRULE == null || INTERVENTIONRULE.size() < 1)
			getInterventionRules();

		return INTERVENTIONRULE.remove();
	}

	/**
	 * Retrieve a random interventionRule from LBS container on the whereClause
	 * 
	 * 
	 * @return a random interventionRule from LBS container on the whereClause
	 */
	public synchronized static InterventionRule getRandomInterventionRule(String whereClause) {

		if (INTERVENTIONRULE == null || INTERVENTIONRULE.size() < 1)
			getInterventionRules(whereClause);

		return INTERVENTIONRULE.remove();
	}

	/**
	 * Retrieve intervention rule base on the rule id
	 * @param ruleId of the intervention rule
	 * @return {@link InterventionRule}
	 */
	public synchronized static InterventionRule getInterventionRuleByRuleId(String ruleId)
	{
		String query = String.format(Queries.QUERY_BY_TYPE, "interventionrule") + " AND c.ruleId = \"%s\"";
		query = String.format(query, ruleId);

		List<InterventionRule> list = executeQuery(BeanNames.LBS_CONTAINER_INTERVENTION_RULE, query, InterventionRule.class);
		if ( list.size()>0 ) {
			return list.get(0);
		}

		return null;
	}
	
	/**
	 * Retrieve a interventionRule from LBS container with the given id
	 *
	 * @param id of the provide to retrieve
	 * @return a {@link InterventionRule}
	 */
	public synchronized static InterventionRule getInterventionRuleById(String id, String ruleId)
	{
		logger.info("Retrieving intervention rule from with itemId {} partition key {}", id, ruleId);
		return retrieveItem(BEAN_NAME, id, ruleId, InterventionRule.class);
	}
	
	/**
	 * Delete interventionRule
	 * 
	 * @param id
	 */
	public synchronized static void deleteInterventionRule(String id, String ruleId)
	{
		logger.info("Deleting intervention rule from Cosmos DB with itemId {} partition key {}", id, ruleId);

		deleteItem(BEAN_NAME, id, ruleId, InterventionRule.class);
	}
}
