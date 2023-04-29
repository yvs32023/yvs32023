/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.cosmos;


import java.util.Arrays;
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

import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.GenericStringItem;
import com.excellus.sqa.rxcc.dto.Provider;
import com.excellus.sqa.selenium.utilities.SortOrder;


/**
 * Cosmos DB queries to LBS provider container
 * 
 * @author Manish Sharma (msharma)
 * @since 03/07/2022
 */
public class ProviderQueries extends Queries  
{

	private static final Logger logger = LoggerFactory.getLogger(ProviderQueries.class);

	private volatile static Queue<Provider> PROVIDERS;   // Cache the providers, so we don't have to run multiple queries to Cosmos
	private volatile static List<Provider> PROVIDERSS;

	private static final String BEAN_NAME = "LBSContainerProvider";

	/**
	 * Query 100 Provider where type = 'provider'
	 * 
	 * @return list of {@link Provider}
	 */
	@SuppressWarnings("unchecked")
	public synchronized static List<Provider> getProviders() 
	{
		if ( PROVIDERS != null && PROVIDERS.size() > 0)
			return (List<Provider>) PROVIDERS;

		List<Provider> providers = executeQuery(BEAN_NAME, String.format(QUERY_BY_TYPE, "provider"), Provider.class, 100);
		PROVIDERS = new LinkedList<>(providers);

		return (List<Provider>) PROVIDERS;

	}

	/**
	 * Query 100 Provider where type = 'provider' and added extension of whereClause
	 * 
	 * 
	 * @return list of {@link Provider with added extension of whereClause}
	 */
	@SuppressWarnings("unchecked") public synchronized static List<Provider> getProviders(String whereClause) {

		String query = String.format(Queries.QUERY_BY_TYPE, "provider" );

		whereClause = StringUtils.isNotBlank(whereClause) ?
				RegExUtils.replaceFirst(whereClause, "where", "") : "";

		query += StringUtils.isNotBlank(whereClause) ? " and " + whereClause.trim() : "";

		List<Provider> providers = executeQuery("LBSContainerProvider", query, Provider.class, 100); 
		PROVIDERS = new LinkedList<>(providers);

		return (List<Provider>) PROVIDERS;
	}

	/**
	 * Retrieve a random provider from LBS container
	 *
	 * @return a random provider from LBS container
	 */
	public synchronized static Provider getRandomProvider()
	{
		if ( PROVIDERS == null || PROVIDERS.size() < 1) getProviders();
		return PROVIDERS.remove();	   
	}

	/**
	 * Retrieve a random provider from LBS container on the whereClause
	 *
	 * @return a random provider from LBS container whereClause
	 */
	public synchronized static Provider getProvider(String whereClause)
	{
		if ( PROVIDERS == null || PROVIDERS.size() < 1) getProviders(whereClause);
		return PROVIDERS.remove(); 
	}

	/**
	 * Retrieve a specific provider from LBS container on the whereClause
	 *
	 * @return a specific provider from LBS container whereClause
	 */
	public synchronized static Provider getSpecificProvider(String whereClause)
	{
		List<Provider> providers = new LinkedList<>(getProviders(whereClause));
		return providers.get(0); 
	}


	/**
	 * Retrieve a provider from LBS container with the given id
	 *
	 * @param id of the provide to retrieve
	 * @return a {@link Provider}
	 */
	public synchronized static Provider getProviderById(String id)
	{
		logger.info("Retrieving provider from Cosmos DB");
		return retrieveItem(BEAN_NAME, id, id, Provider.class);
	}

	/**
	 * Retrieve providers from LBS container with the given npi
	 * @param npi list of npi
	 * @return list of Providers
	 * 
	 * @author Garrett Cosmiano (gcosmian)
	 * @since 07/11/22
	 */
	public synchronized static List<Provider> getProviderByNpi(String... npi)
	{
		logger.info("Retrieving provider from Cosmos DB with multiple provider npi");

		final String query = String.format("SELECT * FROM c WHERE c.type ='provider' and c.npi in (%s)", 
				"\"" + String.join("\", \"", npi) + "\"");

		return executeQuery(BEAN_NAME, query, Provider.class);
	}

	/**
	 * Queries the provider for a specific field joint with several filters.
	 * This is meant to be used for FieldSearch.
	 * 
	 * @author Garrett Cosmiano (gcosmian)
	 * @since 04/14/22
	 * @param fieldQuery the field to be queried
	 * @param searchTerm to be used for the query
	 * @param filterStrings to be added into the query. The map should correspond to key as the field and value as the value
	 * @param filterBoolean fields with boolean values
	 * @return list of string that represent the result
	 */
	public synchronized static List<String> fieldSearch(String fieldQuery, String searchTerm, Map<String, List<String>> filterStrings, Map<String, Boolean> filterBoolean)
	{
		final List<String> nonLocationFields = Arrays.asList("npi", "firstName", "lastName", "taxonomyCode", "taxonomyDescr", "statusInd");
		final String theQuery = "SELECT distinct(%1$s.%2$s) as item  FROM provider p JOIN l IN p.officeLocations WHERE p.type=\"provider\" and RegexMatch(UPPER(%1$s.%2$s), \"(?<![\\\\w])%3$s\", \"m\")";

		// set the table to use
		String tableAbbr = nonLocationFields.contains(fieldQuery) ? "p" : "l";

		String query = String.format(theQuery, tableAbbr, fieldQuery, searchTerm.toUpperCase());

		// Add any filters
		if ( filterStrings != null && filterStrings.size() > 0 )
		{
			for ( Entry<String, List<String>> entry : filterStrings.entrySet())
			{
				String tbl = nonLocationFields.contains(entry.getKey()) ? "p" : "l";

				String values = "\"" + String.join("\", \"", entry.getValue()) + "\"";

				query += String.format(" and %s.%s IN (%s)", tbl, entry.getKey(), values);
			}
		}

		// Add any fileters for boolean type
		if ( filterBoolean != null && filterBoolean.size() > 0 )
		{
			for ( Entry<String, Boolean> entry : filterBoolean.entrySet())
			{
				String tbl = nonLocationFields.contains(entry.getKey()) ? "p" : "l";

				query += String.format(" and %s.%s IN (%s)", tbl, entry.getKey(), String.valueOf(entry.getValue()) );
			}
		}

		// Run the query
		List<GenericStringItem> items = executeQuery(BEAN_NAME, query, GenericStringItem.class, 100);

		// return list of string of the result
		return items.stream()
				.map(GenericStringItem::getItem)
				.collect(Collectors.toList());
	}

	/**
	 * Search for providers
	 * 
	 * @param searchTerm term to search the with provider
	 * @return list of providers
	 * 
	 * @author Garrett Cosmiano (gcosmian)
	 * @since 07/11/22
	 */
	public synchronized static List<Provider> providerSearch(String searchTerm)
	{
		return providerSearch(searchTerm, null, null);
	}

	/**
	 * Search for providers
	 * 
	 * @param searchTerm term to search the with provider
	 * @param filters key-value pair that contains additional filters; for non-boolean values
	 * @param faxVerified true or false
	 * @return list of providers
	 * 
	 * @author Garrett Cosmiano (gcosmian)
	 * @since 07/11/22
	 */
	public synchronized static List<Provider> providerSearch(String searchTerm, Map<String, List<String>> filters, Boolean faxVerified)
	{
		return providerSearch(searchTerm, filters, faxVerified, "firstName", SortOrder.ASCENDING);
	}

	/**
	 * Search for providers
	 * 
	 * @param searchTerm term to search the with provider
	 * @param filters key-value pair that contains additional filters; for non-boolean values
	 * @param faxVerified true or false
	 * @param sortField field to sort
	 * @param sortDirection ascending or descending
	 * @return list of providers
	 * 
	 * @author Garrett Cosmiano (gcosmian)
	 * @since 07/11/22
	 */
	public synchronized static List<Provider> providerSearch(String searchTerm, Map<String, List<String>> filters, Boolean faxVerified, String sortField, SortOrder sortDirection)
	{
		final List<String> nonLocationFields = Arrays.asList("npi", "firstName", "lastName", "taxonomyCode", "taxonomyDescr", "statusInd");

		final String theQuery = "SELECT distinct(p.npi) as id \n"
				+ "FROM provider p JOIN l IN p.officeLocations \n"
				+ "WHERE p.type=\"provider\" and \n"
				+ "   (RegexMatch(UPPER(p.npi), \"(?<![\\\\w])%1$s\") or \n"
				+ "    RegexMatch(UPPER(p.firstName), \"(?<![\\\\w])%1$s\") or \n"
				+ "    RegexMatch(UPPER(p.lastName), \"(?<![\\\\w])%1$s\") or \n"
				+ "    RegexMatch(UPPER(p.taxonomyDescr), \"(?<![\\\\w])%1$s\") or \n"
				+ "    RegexMatch(UPPER(p.statusInd), \"(?<![\\\\w])%1$s\") or \n"
				+ "    RegexMatch(UPPER(l.phoneNumber), \"(?<![\\\\w])%1$s\") or \n"
				+ "    RegexMatch(UPPER(l.faxNumber), \"(?<![\\\\w])%1$s\") or \n"
				+ "    RegexMatch(UPPER(l.city), \"(?<![\\\\w])%1$s\") or \n"
				+ "    RegexMatch(UPPER(l.state), \"(?<![\\\\w])%1$s\") \n"
				+ "    ) \n";

		String query = String.format(theQuery, searchTerm.toUpperCase());

		// Add any filters
		if ( filters != null && filters.size() > 0 )
		{
			for ( Entry<String, List<String>> entry : filters.entrySet())
			{
				String tbl = nonLocationFields.contains(entry.getKey()) ? "p" : "l";

				String values = "\"" + String.join("\", \"", entry.getValue()) + "\"";

				query += String.format(" and %s.%s IN (%s)", tbl, entry.getKey(), values);
			}
		}

		// Add fax verified filter
		if ( faxVerified != null )
		{
			query += String.format(" and l.faxVerifie = %s", faxVerified ? "true" : "false");
		}

		// Sort field - by default use storeName field
		if ( StringUtils.isBlank(sortField) )
		{
			sortField = "firstName";
		}

		// Sort direction - by default use ascending
		if ( sortDirection == null )
		{
			sortDirection = SortOrder.ASCENDING;
		}

		// Add sort
		query += String.format( "ORDER BY %1$s.%2$s %3$s", 
				nonLocationFields.contains(sortField) ? "p" : "l", 
						sortField, 
						(sortDirection == SortOrder.ASCENDING ? "ASC" : "DESC") ) ;

		List<GenericCount> ids = executeQuery(BEAN_NAME, query, GenericCount.class);

		// Retrieve providers given the ids 
		List<String> npis = ids.stream().map(id -> id.getId()).collect(Collectors.toList());
		return getProviderByNpi(npis.toArray(new String[0]));
	}


	/**
	 * Insert provider into Cosmos DB
	 * 
	 * @param provider to be inserted
	 */
	public synchronized static void insertProvider(Provider provider)
	{
		logger.info("Inserting provider into Cosmos DB with itemId {} partition key {}", provider.getId(), provider.getId());

		insertItem(BEAN_NAME, provider.getId(), provider.getId(), provider);
	}

	/**
	 * Delete provider
	 * 
	 * @param providerId to be deleted
	 */
	public synchronized static void deleteProvider(String providerId)
	{
		logger.info("Deleting provider from Cosmos DB with itemId {} partition key {}", providerId, providerId);

		deleteItem(BEAN_NAME, providerId, providerId, Provider.class);
	}

	/**
	 * 
	 * @param provider {@link Provider} that will be replaced
	 */
	public static void replaceProvider(Provider provider) 
	{
		// Call Cosmos DB patch/update
		replaceItem(BEAN_NAME, provider.getId(), provider.getId(), provider, Provider.class);

	}
}
