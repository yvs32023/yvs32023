/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.GenericStringItem;
import com.excellus.sqa.rxcc.dto.Ndc;


/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 05/23/2022
 */
public class NdcQueries extends Queries
{

	private static final Logger logger = LoggerFactory.getLogger(NdcQueries.class);

	private volatile static Queue<Ndc> NDCS; // Cache the ndc, so we don't have to run multiple queries to Cosmos

	private static final String BEAN_NAME = "LBSContainerNdc";

	/**
	 * Query 100 NDC 
	 * 
	 * @return list of {@link Ndc}
	 */
	@SuppressWarnings("unchecked")
	public synchronized static List<Ndc> getNdcs()
	{

		if (NDCS != null && NDCS.size() > 0)
			return (List<Ndc>) NDCS;

		List<Ndc> ndcs = executeQuery(BeanNames.LBS_CONTAINER_NDC, QUERY_ALL, Ndc.class, 100);
		NDCS = new LinkedList<>(ndcs);

		return (List<Ndc>) NDCS;

	}

	/**
	 * Query 100 Ndc and added extension of whereClause
	 * 
	 * 
	 * @return list of {@link Ndc with added extension of whereClause}
	 */
	@SuppressWarnings("unchecked") public synchronized static List<Ndc> getNdcs(String whereClause) {

		if ( NDCS != null && NDCS.size() > 0) 
			return (List<Ndc>) NDCS;

		if ( !StringUtils.startsWithIgnoreCase(whereClause, "where") )
			throw new TestConfigurationException("Invalid where clause, see https://docs.microsoft.com/en-us/azure/cosmos-db/sql/sql-query-where#arguments");

		String query = String.format(Queries.QUERY_ALL);

		whereClause = StringUtils.isNotBlank(whereClause) ?
				RegExUtils.replaceFirst(whereClause, "where", "") : "";

		query += StringUtils.isNotBlank(whereClause) ? " and " + whereClause.trim() : "";

		List<Ndc> ndcs = executeQuery("LBSContainerNdc", query, Ndc.class, 100); 
		NDCS = new LinkedList<>(ndcs);

		return (List<Ndc>) NDCS;

	}

	/**
	 * Retrieve a random ndc from LBS container
	 * 
	 * 
	 * @return a random ndc from LBS container
	 */		  
	public synchronized static Ndc getRandomNdc() {

		if (NDCS == null || NDCS.size() < 1)
			getNdcs();

		return NDCS.remove();
	}

	/**
	 * Retrieve a random ndc from LBS container on the whereClause
	 * 
	 * 
	 * @return a random ndc from LBS container on the whereClause
	 */	

	public synchronized static Ndc getRandomNdc(String whereClause) {

		if (NDCS == null || NDCS.size() < 1)
			getNdcs(whereClause);

		return NDCS.remove();
	}

	/**
	 * Queries the ndc for a specific field joint with several filters.
	 * This is meant to be used for Search query.
	 * 
	 * @author Garrett Cosmiano (gcosmian)
	 * @since 05/31/22
	 * @param searchBy to be used for the query
	 * @param search to be add into the query.
	 * @param filterListStrings
	 * @param filterStrings
	 * @return list of string that represent the result
	 */
	public synchronized static List<Ndc> fieldSelections(String searchBy, String search, Map<String, List<String>> filterListStrings, Map<String, String> filterStrings)

	{
		List<Ndc> items;

		String theQuery = "SELECT * FROM c Where RegexMatch(UPPER(%1$s), \"(?<![\\\\w])%2$s\", \"m\")";

		theQuery = String.format(theQuery,"c." + searchBy,search);

		// Add any filters for list string
		if ( filterListStrings != null && filterListStrings.size() > 0 )
		{
			for ( Entry<String, List<String>> entry : filterListStrings.entrySet())
			{
				String values = "";

				for (String s : entry.getValue() )
				{
					if ( StringUtils.isNotBlank(values) ) {
						values += ", ";
					}

					values += "\"" + s + "\"";
				}

				theQuery += String.format(" and c.%s IN (%s)", entry.getKey(), values);
			}
		}


		// Add any filters for string
		if ( filterStrings != null && filterStrings.size() > 0 )
		{
			for ( Entry<String, String> entry : filterStrings.entrySet())
			{

				theQuery += String.format(" and c.%s IN (\"%s\")", entry.getKey(), String.valueOf(entry.getValue()));
			}
		}

		// Run the query
		items = executeQuery(BeanNames.LBS_CONTAINER_NDC, theQuery, Ndc.class, 200);

		return items;
	}
	/**
	 * Queries the provider for a specific field joint with several filters.
	 * This is meant to be used for FieldSearch NDC.
	 * 
	 * @author Manish Sharma
	 * @since 06/05/22
	 * @param searchTerm to be used for the query
	 * @param fieldQuery the field to be queried
	 * @param filterListStrings
	 * @param filterStrings
	 * @return list of string that represent the result
	 */
	public synchronized static List<String> fieldSearch(String searchTerm, String fieldQuery,  Map<String, List<String>> filterListStrings, Map<String, String> filterStrings)

	{

		String theQuery = "SELECT distinct(c.%1$s) as item FROM c Where RegexMatch(UPPER(c.%1$s), \"(?<![\\\\w])%2$s\", \"m\")";

		String query = String.format(theQuery, fieldQuery,searchTerm);


		// Add any filters for list string
		if ( filterListStrings != null && filterListStrings.size() > 0 )
		{
			for ( Entry<String, List<String>> entry : filterListStrings.entrySet())
			{
				String values = "";

				for (String s : entry.getValue() )
				{
					if ( StringUtils.isNotBlank(values) ) {
						values += ", ";
					}

					values += "\"" + s + "\"";
				}

				query += String.format(" and c.%s IN (%s)", entry.getKey(), values);
			}
		}


		// Add any filters for string
		if ( filterStrings != null && filterStrings.size() > 0 )
		{
			for ( Entry<String, String> entry : filterStrings.entrySet())
			{

				query += String.format(" and c.%s IN (\"%s\")", entry.getKey(), String.valueOf(entry.getValue()));
			}
		}

		// Run the query
		List<GenericStringItem> items = executeQuery(BEAN_NAME, query, GenericStringItem.class, 100);

		// return list of string of the result
		return items.stream()
				.map(GenericStringItem::getItem)
				.collect(Collectors.toList());
	}

}


