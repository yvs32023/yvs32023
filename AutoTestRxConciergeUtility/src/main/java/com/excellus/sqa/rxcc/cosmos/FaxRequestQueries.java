/** 

 *  

 * @copyright 2022 Excellus BCBS 

 * All rights reserved. 

 *  

 */ 

package com.excellus.sqa.rxcc.cosmos; 

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.FaxRequest;
/** 
 *  
 *  
 * @author Mahesh Chowdary (mmanchar) 
 * @since 09/29/2022 
 */
public class FaxRequestQueries extends Queries
{

	private static final Logger logger = LoggerFactory.getLogger(FaxRequestQueries.class);

	/**
	 * Retrieve list of FaxRequest 
	 * @return list of FaxRequest 
	 */
	public synchronized static List<FaxRequest> getFaxRequest()
	{
		return executeQuery(BeanNames.LBS_CONTAINER_FAX_REQUEST, String.format(Queries.QUERY_BY_TYPE, "pdf"), FaxRequest.class, 100);
	}


	/**
	 * Retrieve random faxRequest
	 * @return {@link FaxRequest}
	 */
	
	public synchronized static FaxRequest getRandomFaxRequest()
	{
		logger.info("Get random fax");

		return getRandomItem(getFaxRequest());
	}

	/**
	 * Retrieve the FaxRequest by specific id & phoneNumber
	 * 
	 * @author mmanchar
	 * @since 09/29/22
	 *
	 * @param Id of the fax request
	 * @param phoneNumber of the fax request
	 * @return {@link FaxRequest}
	 */
	public synchronized static FaxRequest getFaxRequest(String Id, String phoneNumber)
	{
		// Build the query
		String query = String.format(Queries.QUERY_BY_TYPE, "pdf") + 
				String.format(" and c.id = \"%s\" and c.phoneNumber = \"%s\"", Id, phoneNumber);

		logger.info("Retrieving member note from Cosmos DB");

		String beanName = BeanNames.LBS_CONTAINER_FAX_REQUEST;

		List<FaxRequest> faxrequest = executeQuery(beanName, query, FaxRequest.class, 1);

		if ( faxrequest.size() > 0 )
			return faxrequest.get(0);

		return null;
	}

	/**
	 * Retrieve fax request by intervention id
	 * @param interventionId of the fax request
	 * @return {@link FaxRequest}
	 * @since 01/03/23
	 */
	public synchronized static FaxRequest getFaxRequestByInterventionId(String interventionId)
	{
		List<FaxRequest> faxRequests = getFaxRequestByInterventionIds(interventionId);

		if ( faxRequests.size() > 0 )
			return faxRequests.get(0);

		return null;
	}

	/**
	 * Retrieve fax requests by intervention ids
	 * @param interventionIds of the fax requests
	 * @return list of {@link FaxRequest}
	 * @since 01/03/23
	 */
	public synchronized static List<FaxRequest> getFaxRequestByInterventionIds(String... interventionIds)
	{
		String query = String.format(Queries.QUERY_BY_TYPE, "pdf") + " AND c.interventionId IN (%s)";

		// Insert the ids
		List<String> listIds = Arrays.asList(interventionIds);
		query = String.format(query, ("\"" + String.join("\", \"", listIds) + "\""));

		return executeQuery(BeanNames.LBS_CONTAINER_FAX_REQUEST, query, FaxRequest.class, 100);
	}

	/**
	 * Query 100 FAXREQUEST
	 * 
	 * @return list of {@link FaxRequest}
	 */
	public synchronized static List<FaxRequest> getFaxRequests()
	{
		return executeQuery(BeanNames.LBS_CONTAINER_FAX_REQUEST, String.format(Queries.QUERY_BY_TYPE, "pdf"), FaxRequest.class, 100);
	}

	/**
	 * Retrieve all fax request regardless of type
	 *
	 * @return list of {@link FaxRequest}
	 */
	public synchronized static List<FaxRequest> getAllFaxRequests()
	{
		return executeQuery(BeanNames.LBS_CONTAINER_FAX_REQUEST, Queries.QUERY_ALL, FaxRequest.class, 100);
	}

}

