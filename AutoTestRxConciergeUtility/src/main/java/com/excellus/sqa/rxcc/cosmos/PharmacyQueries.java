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
import com.excellus.sqa.rxcc.dto.GenericStringItem;
import com.excellus.sqa.rxcc.dto.Pharmacy;
import com.excellus.sqa.selenium.utilities.SortOrder;

/**
 * Cosmos DB queries to LBS pharmacy container
 * 
 * @author Manish Sharma (msharma)
 * @since 03/09/2022
 */
public class PharmacyQueries extends Queries 
{

	private static final Logger logger = LoggerFactory.getLogger(PharmacyQueries.class);

	private volatile static Queue<Pharmacy> PHARMACIES; // Cache the pharmacy, so we don't have to run multiple queries to Cosmos
	
	private static final String BEAN_NAME = "LBSContainerPharmacy";
				
	/**
	 * Query 100 Pharmacy where type = 'pharmacy'
	 * 
	 * @return list of {@link Pharmacy}
	 */
	@SuppressWarnings("unchecked")
	public synchronized static List<Pharmacy> getPharmacies()
	{

		if (PHARMACIES != null && PHARMACIES.size() > 0)
			return (List<Pharmacy>) PHARMACIES;

		List<Pharmacy> pharmacies = executeQuery("LBSContainerPharmacy", String.format(QUERY_BY_TYPE, "pharmacy"),Pharmacy.class, 100);
		PHARMACIES = new LinkedList<>(pharmacies);

		return (List<Pharmacy>) PHARMACIES;

	}

	/**
	 * Query 100 Pharmacy where type = 'pharmacy' and added extension of whereClause
	 * 
	 * 
	 * @return list of {@link Pharmacy with added extension of whereClause}
	 */
		  @SuppressWarnings("unchecked") public synchronized static List<Pharmacy> getPharmacies(String whereClause) {
		  
		  if ( PHARMACIES != null && PHARMACIES.size() > 0) 
			  return (List<Pharmacy>) PHARMACIES;
		  
		  if ( !StringUtils.startsWithIgnoreCase(whereClause, "where") )
			  throw new TestConfigurationException("Invalid where clause, see https://docs.microsoft.com/en-us/azure/cosmos-db/sql/sql-query-where#arguments");
		  
		  String query = String.format(Queries.QUERY_BY_TYPE, "pharmacy" );
		  
		  whereClause = StringUtils.isNotBlank(whereClause) ?
				  	RegExUtils.replaceFirst(whereClause, "where", "") : "";
		  
		  query += StringUtils.isNotBlank(whereClause) ? " and " + whereClause.trim() : "";
		   
		  List<Pharmacy> pharmacies = executeQuery("LBSContainerPharmacy", query, Pharmacy.class, 100); 
		  PHARMACIES = new LinkedList<>(pharmacies);
		  
		  return (List<Pharmacy>) PHARMACIES;
		  
	}
	
	 /**
	  * Retrieve a random pharmacy from LBS container
	  * 
	  * 
	  * @return a random pharmacy from LBS container
	  */		  
    	public synchronized static Pharmacy getRandomPharmacy() {

		if (PHARMACIES == null || PHARMACIES.size() < 1)
			getPharmacies();

		return PHARMACIES.remove();
	}
	
	 /**
	  * Retrieve a random pharmacy from LBS container on the whereClause
	  * 
	  * 
	  * @return a random pharmacy from LBS container on the whereClause
	  */	

	    public synchronized static Pharmacy getPharmacy(String whereClause) {

		if (PHARMACIES == null || PHARMACIES.size() < 1)
			getPharmacies(whereClause);

		return PHARMACIES.remove();
	}
	    
	/**
	 * Retrieve a provider from LBS container with the given id
	 *
	 * @param id of the pharmacy to retrieve
	 * @return a {@link Pharmacy}
	 */
	public synchronized static Pharmacy getPharmacyById(String id)
	{
		logger.info("Retrieving pharmacy from Cosmos DB");

		return retrieveItem(BEAN_NAME, id, id, Pharmacy.class);
	}

    
    
    /**
	 * Queries the pharmacy for a specific field joint with several filters.
	 * This is meant to be used for FieldSearch.
	 * 
	 * @author Garrett Cosmiano (gcosmian)
	 * @since 04/14/22
	 * @param fieldQuery the field to be queried
	 * @param searchTerm to be used for the query
	 * @param filters to be add into the query.  
	 * @return list of string that represent the result
	 */
	public synchronized static List<String> fieldSearch(String fieldQuery, String searchTerm, Map<String, List<String>> filters)
	{
	
		final String theQuery = "SELECT distinct(p.%1$s) as item FROM pharmacy p WHERE p.type=\"pharmacy\" and RegexMatch(UPPER(p.%1$s), \"(?<![\\\\w])%2$s\", \"m\")";
		

        String query = String.format(theQuery, fieldQuery, searchTerm.toUpperCase());
        
        // Add any filters
        if ( filters != null && filters.size() > 0 )
        {
            for ( Entry<String, List<String>> entry : filters.entrySet())
            {
                String values = "\"" + String.join("\", \"", entry.getValue()) + "\"";
                query += String.format(" and p.%s IN (%s)", entry.getKey(), values);
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
	 * Perform pharmacy search
	 * 
	 * @param searchTerm to search for
	 * @param filters by fields
	 * @return list of pharmacies
	 * 
	 * @author Garrett Cosmiano (gcosmian)
	 * @since 07/08/22
	 * 
	 */
	public synchronized static List<Pharmacy> search(String searchTerm, Map<String, List<String>> filters)
	{
		return search(searchTerm, filters, null, null);
	}
	
	/**
	 * Perform pharmacy search
	 * 
	 * @param searchTerm to search for
	 * @param filters by fields
	 * @param sortField field
	 * @param sortDirection sort order
	 * 
	 * @return list of pharmacies
	 * 
	 * @author Garrett Cosmiano (gcosmian)
	 * @since 07/08/22
	 */
	public synchronized static List<Pharmacy> search(String searchTerm, Map<String, List<String>> filters, String sortField, SortOrder sortDirection)
	{
		final String theQuery = "SELECT * FROM c \n"
				+ "WHERE (RegexMatch(UPPER(c.npi), \"(?<![\\\\w])%1$s\", \"m\") \n"
				+ "   or RegexMatch(UPPER(c.storeName), \"(?<![\\\\w])%1$s\", \"m\")\n"
				+ "   or RegexMatch(UPPER(c.taxonomyDescr), \"(?<![\\\\w])%1$s\", \"m\")\n"
				+ "   or RegexMatch(UPPER(c.phoneNumber), \"(?<![\\\\w])%1$s\", \"m\")\n"
				+ "   or RegexMatch(UPPER(c.city), \"(?<![\\\\w])%1$s\", \"m\")\n"
				+ "   or RegexMatch(UPPER(c.state), \"(?<![\\\\w])%1$s\", \"m\")\n"
				+ "   or RegexMatch(UPPER(c.statusInd), \"(?<![\\\\w])%1$s\", \"m\"))\n";
		
		String query = String.format(theQuery, searchTerm.toUpperCase());
		
		// Add any filters
		if ( filters != null && filters.size() > 0 )
		{
			for ( Entry<String, List<String>> entry : filters.entrySet())
			{
				String values = "\"" + String.join("\", \"", entry.getValue()) + "\"";
				query += String.format(" and c.%s IN (%s)\n", entry.getKey(), values);
			}
		}
		
		// Sort field - by default use storeName field
		if ( StringUtils.isBlank(sortField) )
		{
			sortField = "storeName";
		}
		
		// Sort direction - by default use ascending
		if ( sortDirection == null )
		{
			sortDirection = SortOrder.ASCENDING;
		}
		
		// Add sort
		query += String.format( "ORDER BY c.%s %s", sortField, (sortDirection == SortOrder.ASCENDING ? "ASC" : "DESC") ) ;
		
		return executeQuery(BEAN_NAME, query, Pharmacy.class);
	}

}
