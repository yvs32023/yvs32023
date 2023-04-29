/**
 * 
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.InterventionBatch;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 03/03/2023
 */
public class InterventionBatchQueries extends Queries{

	private static final Logger logger = LoggerFactory.getLogger(InterventionBatchQueries.class);

	private volatile static Map<String, List<GenericCount>> INTERVENTIONBATCH_BY_BATCHID= new HashMap<>();


	/**
	 * Retrieve random distinct interventionBatchId  based on  subscriptionName 
	 * @param subscriptionName of the tenant
	 * @return interventionBatchId
	 * @author msharma 03/03/23
	 */
	public synchronized static String getRandomRecordsWithInterventionBatchByBatchId(String subscriptionName)
	{
		logger.info("Retrieving random interventionBatchId with interventionBatch");

		String key = subscriptionName;
		if ( !INTERVENTIONBATCH_BY_BATCHID.containsKey(key) )
		{
			getRecordsWithInterventionBatchByBatchId(subscriptionName);
		}

		GenericCount genericCount = getRandomItem( INTERVENTIONBATCH_BY_BATCHID.get(key) );
		return (genericCount != null) ? genericCount.getId() : null;
	}

	/**
	 * Returns a list of {@link GenericCount} objects for  distinct interventionBatchId
	 * 
	 * @param subscriptionName The name of the subscription for which to retrieve members with interventions.
	 * @return A list of {@link GenericCount} objects representing the distinct interventionBatchId
	 * 
	 * @see GenericCount
	 * @author msharma 03/03/23
	 */
	public synchronized static List<GenericCount> getRecordsWithInterventionBatchByBatchId(String subscriptionName)
	{
		final Logger logger = LoggerFactory.getLogger(InterventionBatchQueries.class);

		// Check if the list of members with interventions has already been retrieved and cached
		if ( INTERVENTIONBATCH_BY_BATCHID.containsKey(subscriptionName) )
		{
			// If so, return the cached list
			logger.debug("Returning cached list of interventionbatch for subscription {}", subscriptionName);
			return INTERVENTIONBATCH_BY_BATCHID.get(subscriptionName);
		}

		// If not, create a new query to retrieve the list of distinct interventionBatchId
		final String query = "SELECT distinct c.interventionBatchId as id FROM c where c.type = 'interventionbatch'";

		// Retrieve the bean name for the specified subscription
		String beanName = RxConciergeCosmoConfig.getInterventionBatchContainerBeanName(subscriptionName);

		// Execute the query to retrieve the list of distinct interventionBatchId
		List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);

		String key = subscriptionName;
		// Cache the list of distinct interventionBatchId
		INTERVENTIONBATCH_BY_BATCHID.put(key, list);

		logger.debug("Returning list of members with interventions for subscription {}", subscriptionName);
		// Return the list of distinct interventionBatchId
		return list;
	}


	/**
	 * Retrieves all the interventionbatch given a batch id
	 *
	 * @param subscriptionName the member belongs to
	 * @param batchId to retrieve interventionbatch
	 * @return list of {@link InterventionBatch}
	 *	 * @author msharma 03/03/23
	 */
	public synchronized static List<InterventionBatch> getInterventionBatch(String subscriptionName, String batchId)
	{
		String query = String.format(Queries.QUERY_BY_TYPE, "interventionbatch") +
				String.format(" AND c.interventionBatchId = \"%s\"", batchId) ;
		String beanName = RxConciergeCosmoConfig.getInterventionBatchContainerBeanName(subscriptionName);
		List<InterventionBatch> list = executeQuery(beanName, query, InterventionBatch.class, 100);

		return list;
	}

}
