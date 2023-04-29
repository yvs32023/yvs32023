/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.Lookup;

/**
 * Cosmos DB queries to LBS lookup container
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/05/2022
 */
public class LookupQueries extends Queries
{

	private static final Logger logger = LoggerFactory.getLogger(LookupQueries.class);

	private volatile static List<Lookup> LOOKUPS;    // GC (02/11/22) Cache the lookups so we don't have to run multiple queries to Cosmos

	/**
	 * Queries all lookups
	 * @return list of lookups
	 */
	public synchronized static List<Lookup> getLookups() {

		if ( LOOKUPS != null && LOOKUPS.size() > 0 )
			return LOOKUPS;

		// Execute Cosmos DB query
		logger.info("Retrieving lookup from Cosmos DB");
		LOOKUPS = executeQuery("LBSContainerLookup", QUERY_ALL, Lookup.class, 100);

		return LOOKUPS;
	}

	/**
	 * Retrieve a lookup provided a type
	 * @param type of lookup
	 * @return the lookup or null if none found
	 */
	public synchronized static Lookup getLookup(Lookup.LookupType type) {

		if ( LOOKUPS == null || LOOKUPS.size() == 0 )
			getLookups();

		for (Lookup lookup : LOOKUPS )
		{
			if ( lookup.getType() == type )
				return lookup;
		}

		return null;
	}

	/**
	 * Retrieve all lookup types
	 * @return list of {@link com.excellus.sqa.rxcc.dto.Lookup.LookupType}
	 */
	public synchronized static List<Lookup.LookupType> getLookupTypes() {

		if ( LOOKUPS == null || LOOKUPS.size() == 0 )
			getLookups();

		List<Lookup.LookupType> types = new ArrayList<Lookup.LookupType>();
		for (Lookup lookup : LOOKUPS )
		{
			types.add(lookup.getType());
		}

		return types;
	}

}
