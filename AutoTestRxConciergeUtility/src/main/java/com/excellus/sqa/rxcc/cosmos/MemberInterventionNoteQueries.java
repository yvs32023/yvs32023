/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.MemberInterventionNote;
import com.excellus.sqa.rxcc.dto.Tenant;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 10/12/2022
 */
public class MemberInterventionNoteQueries extends Queries
{

	private static final Logger logger = LoggerFactory.getLogger(MemberInterventionNoteQueries.class);
 
	private volatile static Map<String, List<GenericCount>> RANDOM_MEMBERS_WITH_INTERVENTION_NOTE = new HashMap<>();

	/**
	 * Retrieve random member id with  intervention note
	 * @param tenantType {@link Tenant}
	 * @return member id
	 */
	public synchronized static String getRandomMemberWithMemberInterventionNote(Tenant tenant)
	{
		return getRandomMemberWithMemberInterventionNote(tenant.getSubscriptionName());
	}

	/**
	 * Retrieve random member id with  intervention note
	 * @param tenantType {@link Tenant.Type}
	 * @return member id
	 */
	public synchronized static String getRandomMemberWithMemberInterventionNote(Tenant.Type tenantType)
	{
		return getRandomMemberWithMemberInterventionNote(tenantType.getSubscriptionName());
	}


	/**
	 * Returns a randomly selected member ID with at least one intervention note for the given subscription.
	 * If the member ID has not been retrieved before, this method retrieves the latest intervention notes
	 * for the subscription and stores the member counts in a map for future use.
	 *
	 * @param subscriptionName the subscription name to retrieve a member ID for
	 * @return a randomly selected member ID with at least one intervention note, or null if no member ID is found
	 * @msharma 02/20/23
	 */
	public synchronized static String getRandomMemberWithMemberInterventionNote(String subscriptionName)
	{
		logger.info("Retrieving random member id with intervention note");
		
		String key = subscriptionName;
		if ( !RANDOM_MEMBERS_WITH_INTERVENTION_NOTE.containsKey(key) )
		{
			getMemberWithInterventionNote(subscriptionName);
		}

		GenericCount genericCount = getRandomItem( RANDOM_MEMBERS_WITH_INTERVENTION_NOTE.get(key) );
		return (genericCount != null) ? genericCount.getId() : null;
	}



	/**
	 * Retrieves a list of member IDs with at least one intervention note for the given subscription and
	 * stores the list in a map for future use. The method executes a Cosmos DB query to count the number
	 * of intervention notes for each member and returns a list of `GenericCount` objects that includes
	 * the member ID and the number of intervention notes.
	 *
	 * @param subscriptionName the subscription to retrieve member IDs for
	 * @return a list of `GenericCount` objects that includes member IDs and the number of intervention notes
	 * @msharma 02/20/23
	 */
	public synchronized static List<GenericCount> getMemberWithInterventionNote(String subscriptionName)
	{
		final String query = "SELECT * FROM ( "
				+ "    SELECT r.memberId as id, r.num "
				+ "    FROM ( "
				+ "        SELECT c.memberId, count(1) as num FROM c WHERE c.type = \"interventionnote\" GROUP BY c.memberId "
				+ "        ) as r "
				+ ") as a "
				+ "WHERE a.num > 0";

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

		List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);
		
		String key = subscriptionName;
		RANDOM_MEMBERS_WITH_INTERVENTION_NOTE.put(key, list);

		return list;
	}



	/**
	 * Retrieves all the  intervention note given a member id
	 * 
	 * @param subscriptionName the member belongs to
	 * @param memberId to retrieve  intervention note
	 * @return list of {@link MemberInterventionNote}
	 */
	public synchronized static List<MemberInterventionNote> getInterventionNote(String subscriptionName, String memberId)
	{
		String query = String.format(Queries.QUERY_BY_TYPE, "interventionnote") + 
				String.format(" and c.memberId = \"%s\"", memberId);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
		List<MemberInterventionNote> list = executeQuery(beanName, query, MemberInterventionNote.class, 100);

		return list;
	}

	/**
	 * Retrieves the  intervention note given memberId, intervention id, note id
	 * @msharma 02/05/23
	 * @param memberId, intervention id, note id
	 * @return
	 */

	public synchronized  static MemberInterventionNote getSpecificInterventionNote(String memberId, String interventionId, String id)
	{
		// Build the query
		String query = String.format(Queries.QUERY_BY_TYPE, "interventionnote") + 
				String.format(" and c.memberId = \"%s\" and c.interventionId = \"%s\" and c.id = \"%s\"", memberId, interventionId, id);


		Tenant tenant = TenantQueries.getTenants().stream()
				.filter( theTenant -> StringUtils.startsWith(interventionId, theTenant.getAdTenantId()))
				.findFirst()
				.orElseGet(null);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(tenant.getSubscriptionName());
		List<MemberInterventionNote> list = executeQuery(beanName, query, MemberInterventionNote.class);

		if ( list != null && list.size() > 0 )
		{
			return list.get(0);
		}

		return null;
	}


	/**
	 * Retrieves the  intervention note given  intervention id
	 * @msharma 01/26/23
	 * @param interventionId
	 * @return
	 */

	public synchronized  static MemberInterventionNote getInterventionNote(String interventionId)
	{
		String query = String.format(Queries.QUERY_BY_TYPE, "interventionnote") +
				String.format(" and c.interventionId = \"%s\"", interventionId);

		Tenant tenant = TenantQueries.getTenants().stream()
				.filter( theTenant -> StringUtils.startsWith(interventionId, theTenant.getAdTenantId()))
				.findFirst()
				.orElseGet(null);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(tenant.getSubscriptionName());
		List<MemberInterventionNote> list = executeQuery(beanName, query, MemberInterventionNote.class);

		list = list.stream()  // MS (01/29/23) sort list by created date 
				.sorted(Comparator.comparing(MemberInterventionNote::getCreatedDateTime).reversed())
				.collect(Collectors.toList());

		list.stream()
		.max(Comparator.comparing(MemberInterventionNote::getCreatedDateTime)).orElse(null);

		if ( list != null && list.size() > 0 )
		{
			return list.get(0);
		}

		return null;
	}


	/**
	 * Retrieves the  intervention note given a member id and intervention id
	 * @ntagore 01/18/23
	 * @param subscriptionName the member belongs to
	 * @param memberId to retrieve  intervention note
	 * @return list of {@link MemberInterventionNote}
	 */
	public synchronized static List<MemberInterventionNote> getInterventionNoteById(String subscriptionName, String memberId, String interventionId)
	{
		String query = "SELECT * "
				+ "from c WHERE c.type = \"interventionnote\" "
				+  String.format(" and c.memberId = \"%s\"", memberId)
				+  String.format(" and c.interventionId = \"%s\"", interventionId);

		logger.info("***interventionNote Query ******  "+query);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
		List<MemberInterventionNote> list = executeQuery(beanName, query, MemberInterventionNote.class, 100);

		return list;
	}


	/**
	 * Delete InterventionNote from container
	 * @param subscriptionName either exe, ehp, loa or med
	 * @param interventionNoteId to be deleted
	 * @param memberId associated with the interventionNoteId to be deleted
	 * @author mmanchar
	 * @date 25/01/2023
	 */
	public synchronized static void deleteInterventionNote(String subscriptionName, String interventionNoteId, String memberId )
	{
		logger.info("Deleting interventionNote from Cosmos DB with itemId {} partition key {}", interventionNoteId, memberId);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

		deleteItem(beanName, interventionNoteId,  memberId, MemberInterventionNote.class);
	}


	/**
	 * Delete InterventionNote from container
	 * @param interventionNoteId to be deleted
	 * @param memberId associated with the interventionNoteId to be deleted
	 * @author msharma
	 * @date 02/02/2023
	 */
	public synchronized static void deleteInterventionNote( String interventionNoteId, String memberId )
	{
		logger.info("Deleting interventionNote from Cosmos DB with itemId {} partition key {}", interventionNoteId, memberId);
		logger.debug("Fetching tenant details for memberId {}", memberId);

		Tenant tenant = TenantQueries.getTenants().stream()
				.filter( theTenant -> StringUtils.startsWith(memberId, theTenant.getAdTenantId()))
				.findFirst()
				.orElseGet(null);

		String subscriptionName = tenant.getSubscriptionName();
		logger.debug("Fetched subscriptionName {} for tenant {}", subscriptionName, tenant);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
		logger.debug("Fetched beanName {} for subscriptionName {}", beanName, subscriptionName);

		deleteItem(beanName, interventionNoteId,  memberId, MemberInterventionNote.class);
		logger.info("Deleted interventionNote from Cosmos DB with itemId {} partition key {}", interventionNoteId, memberId);
	}
	
	/**
	 * Insert InterventionNote from container
	 * @param interventionNoteId to be deleted
	 * @param memberId associated with the interventionNoteId to be deleted
	 * @author msharma
	 * @date 02/15/2023
	 */
	public synchronized static void insertInterventionNote( MemberInterventionNote interventionNote)
	{
		logger.info("Inserting interventionNote into Cosmos DB with itemId {} partition key {}", interventionNote.getId(), interventionNote.getMemberId());
		logger.debug("Fetching tenant details for memberId {}", interventionNote.getMemberId());

		Tenant tenant = TenantQueries.getTenants().stream()
				.filter( theTenant -> StringUtils.startsWith(interventionNote.getMemberId(), theTenant.getAdTenantId()))
				.findFirst()
				.orElseGet(null);

		String subscriptionName = tenant.getSubscriptionName();
		logger.debug("Fetched subscriptionName {} for tenant {}", subscriptionName, tenant);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
		logger.debug("Fetched beanName {} for subscriptionName {}", beanName, subscriptionName);

		insertItem(beanName, interventionNote.getId(),  interventionNote.getMemberId(), interventionNote);
		logger.info("Inserted interventionNote from Cosmos DB with itemId {} partition key {}", interventionNote.getId(), interventionNote.getMemberId());
	}
	

	public synchronized static List<MemberInterventionNote> getMemberInterventionNotesLatestCreatedBy(String subscriptionName, String createdBy) 
	{
	    logger.info("Retrieving intervention notes for user: {}", createdBy);

	    String query = String.format("SELECT TOP 1 * FROM c WHERE c.type = 'interventionnote' AND  LENGTH(c.interventionId) > 100 AND c.createdBy = '%s' ORDER BY c.lastUpdatedDateTime DESC", createdBy);

	    String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
	    logger.debug("Executing query: {}", query);
	    List<MemberInterventionNote> memberInterventionNotes = executeQuery(beanName, query, MemberInterventionNote.class, 100);
	    logger.debug("Query returned {} intervention notes", memberInterventionNotes.size());

	    return memberInterventionNotes;
	}

	/**

	Retrieves a list of {@link MemberInterventionNote}s that match the provided where clause, from the Cosmos DB container
	associated with the given subscription name.
	@param subscriptionName the name of the subscription associated with the Cosmos DB container to query
	@param whereClause a SQL WHERE clause, in string format, specifying the filter criteria for the query
	@return a list of {@link MemberInterventionNote}s that match the specified criteria, retrieved from the Cosmos DB container
	@throws TestConfigurationException if the provided where clause is invalid, as specified in the Azure Cosmos DB documentation
    @author msharma
	@date 02/23/2023
	*/
	public static List<MemberInterventionNote> getMemberInterventionNotes(String subscriptionName, String whereClause) {
	    if (!StringUtils.startsWithIgnoreCase(whereClause, "where")) {
	        throw new TestConfigurationException("Invalid WHERE clause: " + whereClause);
	    }

	    String query = String.format(Queries.QUERY_BY_TYPE, "interventionnote");
	    whereClause = StringUtils.trim(StringUtils.removeStartIgnoreCase(whereClause, "where"));

	    if (StringUtils.isNotBlank(whereClause)) {
	        query += " AND " + whereClause;
	    }

	    String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
	    logger.debug("Executing query: {}", query);
	    List<MemberInterventionNote> memberInterventionNotes = executeQuery(beanName, query, MemberInterventionNote.class, 100);
	    logger.debug("Query returned {} intervention notes", memberInterventionNotes.size());

	    return memberInterventionNotes;
	}

}
