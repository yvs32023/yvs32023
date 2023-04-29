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
public class InterventionFormQueries extends Queries 
{

	private static final Logger logger = LoggerFactory.getLogger(InterventionFormQueries.class);

	private volatile static Queue<InterventionRule> INTERVENTIONFORM; // Cache the intervention, so we don't have to run multiple queries to Cosmos

	private static final String BEAN_NAME = "LBSContainerInterventionForm";
	
	
	/**
	 * Query 100 INTERVENTIONFORM 
	 * 
	 * @return list of {@link InterventionRule}
	 */
	@SuppressWarnings("unchecked")
	public synchronized static List<InterventionRule> getInterventionForms()
	{

		if (INTERVENTIONFORM != null && INTERVENTIONFORM.size() > 0)
			return (List<InterventionRule>) INTERVENTIONFORM;

		List<InterventionRule> interventionForm = executeQuery(BeanNames.LBS_CONTAINER_INTERVENTION_FORM, QUERY_ALL, InterventionRule.class, 100);
		INTERVENTIONFORM = new LinkedList<>(interventionForm);

		return (List<InterventionRule>) INTERVENTIONFORM;

	}
	
	/**
	 * Query 100 INTERVENTIONFORM and added extension of whereClause
	 * 
	 * 
	 * @return list of {@link InterventionRule} with added extension of whereClause}
	 */
	@SuppressWarnings("unchecked") public synchronized static List<InterventionRule> getInterventionForms(String whereClause) {

		if ( INTERVENTIONFORM != null && INTERVENTIONFORM.size() > 0) 
			return (List<InterventionRule>) INTERVENTIONFORM;

		if ( !StringUtils.startsWithIgnoreCase(whereClause, "where") )
			throw new TestConfigurationException("Invalid where clause, see https://docs.microsoft.com/en-us/azure/cosmos-db/sql/sql-query-where#arguments");

		String query = String.format(Queries.QUERY_ALL);

		whereClause = StringUtils.isNotBlank(whereClause) ?
				RegExUtils.replaceFirst(whereClause, "where", "") : "";

		query += StringUtils.isNotBlank(whereClause) ? " and " + whereClause.trim() : "";

		List<InterventionRule> interventionForm = executeQuery("LBSContainerInterventionForm", query, InterventionRule.class, 100); 
		INTERVENTIONFORM = new LinkedList<>(interventionForm);

		return (List<InterventionRule>) INTERVENTIONFORM;

	}

	/**
	 * Retrieve a random interventionForm from LBS container
	 * 
	 * 
	 * @return a random interventionForm from LBS container
	 */		  
	public synchronized static InterventionRule getRandomInterventionForm() {

		if (INTERVENTIONFORM == null || INTERVENTIONFORM.size() < 1)
			getInterventionForms();

		return INTERVENTIONFORM.remove();
	}

	/**
	 * Retrieve a random interventionForm from LBS container on the whereClause
	 * 
	 * 
	 * @return a random interventionForm from LBS container on the whereClause
	 */	

	public synchronized static InterventionRule getRandomInterventionForm(String whereClause) {

		if (INTERVENTIONFORM == null || INTERVENTIONFORM.size() < 1)
			getInterventionForms(whereClause);

		return INTERVENTIONFORM.remove();
	}
	
}
