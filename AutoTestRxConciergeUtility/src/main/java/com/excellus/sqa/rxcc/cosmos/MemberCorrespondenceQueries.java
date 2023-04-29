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
import com.excellus.sqa.rxcc.Utility;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.Tenant.Type;

/**
 * Queries for member correspondence
 * 
 * @author Neeru Tagore(ntagore)
 * @since 05/09/2022
 */
public class MemberCorrespondenceQueries extends Queries 
{
	private static final Logger logger = LoggerFactory.getLogger(MemberRxClaimQueries.class);

	// List of members and the number correspondences and intervention it has
	private volatile static List<GenericCount> RANDOM_EHP_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION;
	private volatile static List<GenericCount> RANDOM_EXE_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION;
	private volatile static List<GenericCount> RANDOM_LOA_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION;
	private volatile static List<GenericCount> RANDOM_MED_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION;

	// List of members and the number correspondences it has
	private volatile static List<GenericCount> RANDOM_EHP_MEMBERS_WITH_CORRESPONDENCE;
	private volatile static List<GenericCount> RANDOM_EXE_MEMBERS_WITH_CORRESPONDENCE;
	private volatile static List<GenericCount> RANDOM_LOA_MEMBERS_WITH_CORRESPONDENCE;
	private volatile static List<GenericCount> RANDOM_MED_MEMBERS_WITH_CORRESPONDENCE;

	private volatile static Map<String, List<GenericCount>> MEMBERS_WITH_CORRESPONDENCE_INCLUDE_INTERVENTIONNOTEID = new HashMap<>();

	private static int DEFAULT_NUMBER_OF_CORRESPONDENCE = 15;
	private static int DEFAULT_NUMBER_OF_CORRESPONDENCE_WITH_INTERVENTION = 1;

	/**
	 * Retrieve the member correspondence from ALL tenant based on the subcription name
	 * 
	 * @param subscriptionName of the tenant
	 * @param memberId of the member
	 * @return List of member correspondences
	 */
	public synchronized static List<MemberCorrespondence> getALLTenantMemberCorrespondences(String subscriptionName, String memberId)
	{
		return getMemberCorrespondence(subscriptionName, memberId);
	}

	/**
	 * Retrieve the member correspondence from ALL tenant based on the subcription name
	 * 
	 * @param subscriptionName of the tenant
	 * @param memberId of the member
	 * @param interventionId of the member
	 * @return List of member correspondences
	 */
	public synchronized static List<MemberCorrespondence> getALLTenantMemberCorrespondences(String subscriptionName,String memberId,  String interventionId)
	{
		return getMemberCorrespondenceWithIntervention(subscriptionName, memberId, interventionId );
	}

	/**
	 * Retrieve the random member correspondence from ALL tenant based on the subcription name
	 * 
	 * @param subscriptionName of the tenant
	 * @param memberId of the member
	 * @param interventionId of the member
	 * @return member correspondence
	 */
	public synchronized static MemberCorrespondence getALLTenantMemberCorrespondence(String subscriptionName,String memberId,  String interventionId)
	{
		return getRandomItem (getMemberCorrespondenceWithIntervention(subscriptionName, memberId, interventionId ));
	}



	/**
	 * Retrieve the member correspondence
	 * 
	 * @param subscriptionName for all tenant
	 * @param memberId of the member
	 * @return
	 */
	public synchronized static List<MemberCorrespondence> getMemberCorrespondence(String subscriptionName, String memberId)
	{
		// Build the query
		String query = String.format(Queries.QUERY_BY_TYPE, "correspondence") + 
				String.format(" and c.memberId = \"%s\"", memberId);	// GC (07/08/22) removed sorting cause the indexing has been updated cause exception

		logger.info("Retrieving member correspondence from Cosmos DB");
		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
		List<MemberCorrespondence> memberCorrespondences = executeQuery(beanName, query, MemberCorrespondence.class, 100);

		// GC (07/08/22) sort list by created date 
		memberCorrespondences = memberCorrespondences.stream()
				.sorted(Comparator.comparing(MemberCorrespondence::getCreatedDateTime))
				.collect(Collectors.toList());

		return memberCorrespondences;
	}

	/**
	 * Retrieve random member id with member correspondence
	 * @param subscriptionName tenant subscription name
	 * @return member id
	 */
	public synchronized static String getRandomMembersWithCorrespondenceMoreThanX(String subscriptionName)
	{
		logger.info("Retrieving random member id with correspondence");

		if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EHP.getSubscriptionName()) )
		{
			if ( RANDOM_EHP_MEMBERS_WITH_CORRESPONDENCE == null || RANDOM_EHP_MEMBERS_WITH_CORRESPONDENCE.size() < 1 )
			{
				getMembersWithCorrespondenceMoreThanX(subscriptionName,  DEFAULT_NUMBER_OF_CORRESPONDENCE);
			}

			GenericCount genericCount = getRandomItem(RANDOM_EHP_MEMBERS_WITH_CORRESPONDENCE);
			return (genericCount != null) ? genericCount.getId() : null;
		}
		else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EXE.getSubscriptionName()) )
		{
			if ( RANDOM_EXE_MEMBERS_WITH_CORRESPONDENCE == null || RANDOM_EXE_MEMBERS_WITH_CORRESPONDENCE.size() < 1 )
			{
				getMembersWithCorrespondenceMoreThanX(subscriptionName,  DEFAULT_NUMBER_OF_CORRESPONDENCE);
			}

			GenericCount genericCount = getRandomItem(RANDOM_EXE_MEMBERS_WITH_CORRESPONDENCE);
			return (genericCount != null) ? genericCount.getId() : null;
		}
		else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.LOA.getSubscriptionName()) )
		{
			if ( RANDOM_LOA_MEMBERS_WITH_CORRESPONDENCE == null || RANDOM_LOA_MEMBERS_WITH_CORRESPONDENCE.size() < 1 )
			{
				getMembersWithCorrespondenceMoreThanX(subscriptionName,  DEFAULT_NUMBER_OF_CORRESPONDENCE);
			}

			GenericCount genericCount = getRandomItem(RANDOM_LOA_MEMBERS_WITH_CORRESPONDENCE);
			return (genericCount != null) ? genericCount.getId() : null;
		}
		else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.MED.getSubscriptionName()) )
		{
			if ( RANDOM_MED_MEMBERS_WITH_CORRESPONDENCE == null || RANDOM_MED_MEMBERS_WITH_CORRESPONDENCE.size() < 1 )
			{
				getMembersWithCorrespondenceMoreThanX(subscriptionName,  DEFAULT_NUMBER_OF_CORRESPONDENCE);
			}

			GenericCount genericCount = getRandomItem(RANDOM_MED_MEMBERS_WITH_CORRESPONDENCE);
			return (genericCount != null) ? genericCount.getId() : null;
		}

		throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
	}


	/**
	 * Retrieves members that has more than X number of correspondence 
	 * 
	 * @author Garrett Cosmiano (gcosmian)
	 * @since 05/18/22
	 * 
	 * @param subscriptionName
	 * @param numOfCorrespondence
	 * @return list of members with corresponding counts of correspondence
	 */
	public synchronized static List<GenericCount> getMembersWithCorrespondenceMoreThanX(String subscriptionName, int numOfCorrespondence)
	{
		logger.info("Retrieving from Cosmos DB a member with correspondence that is more than " + numOfCorrespondence);

		/*
		 * Retrieve members with at least X number of correspondence
		 */
		String query = "SELECT * from (SELECT r.memberId as id,  r.num "
				+ "FROM (SELECT c.memberId, count(1) as num FROM c WHERE c.type = \"correspondence\" GROUP BY c.memberId) as r "
				+ "WHERE r.num > " + numOfCorrespondence +")";

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

		List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, 100);

		if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()) )
		{
			RANDOM_EXE_MEMBERS_WITH_CORRESPONDENCE = list;
		}
		else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()) )
		{
			RANDOM_EHP_MEMBERS_WITH_CORRESPONDENCE = list;
		}
		else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()) )
		{
			RANDOM_LOA_MEMBERS_WITH_CORRESPONDENCE = list;
		}
		else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()) )
		{
			RANDOM_MED_MEMBERS_WITH_CORRESPONDENCE = list;
		}
		else
		{
			throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
		}
		return list;
	}

	/**
	 * Retrieve the member correspondence
	 * 
	 * @param subscriptionName for all tenant
	 * @param memberId of the member
	 * @param interventionId of the member
	 * @return
	 */
	public synchronized static List<MemberCorrespondence> getMemberCorrespondenceWithIntervention(String subscriptionName, String memberId, String interventionId )
	{
		// Build the query
		String query = String.format(Queries.QUERY_BY_TYPE, "correspondence") + 
				String.format(" and c.memberId = \"%s\"", memberId)  + 
				String.format(" and c.interventionId = \"%s\"", interventionId);	// GC (07/08/22) removed sorting cause the indexing has been updated cause exception

		logger.info("Retrieving member correspondence from Cosmos DB");
		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
		List<MemberCorrespondence> memberCorrespondences = executeQuery(beanName, query, MemberCorrespondence.class, 100);

		// GC (07/08/22) sort list by created date 
		memberCorrespondences = memberCorrespondences.stream()
				.sorted(Comparator.comparing(MemberCorrespondence::getCreatedDateTime))
				.collect(Collectors.toList());

		return memberCorrespondences;
	}

	/**
	 * Retrieve random member id which has intervention associated with member correspondence
	 * @param subscriptionName tenant subscription name
	 * @return member id
	 */
	public synchronized static String getRandomMembersWithCorrespondenceAndInterventionMoreThanX(String subscriptionName)
	{
		logger.info("Retrieving random member id with correspondence");

		if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EHP.getSubscriptionName()) )
		{
			if ( RANDOM_EHP_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION == null || RANDOM_EHP_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION.size() < 1 )
			{
				getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionName,  DEFAULT_NUMBER_OF_CORRESPONDENCE_WITH_INTERVENTION);
			}
			return getRandomItem(RANDOM_EHP_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION).getId();
		}
		else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EXE.getSubscriptionName()) )
		{
			if ( RANDOM_EXE_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION == null || RANDOM_EXE_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION.size() < 1 )
			{
				getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionName,  DEFAULT_NUMBER_OF_CORRESPONDENCE_WITH_INTERVENTION);
			}
			return getRandomItem(RANDOM_EXE_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION).getId();
		}
		else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.LOA.getSubscriptionName()) )
		{
			if ( RANDOM_LOA_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION == null || RANDOM_LOA_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION.size() < 1 )
			{
				getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionName,  DEFAULT_NUMBER_OF_CORRESPONDENCE_WITH_INTERVENTION);
			}
			return getRandomItem(RANDOM_LOA_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION).getId();
		}
		else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.MED.getSubscriptionName()) )
		{
			if ( RANDOM_MED_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION == null || RANDOM_MED_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION.size() < 1 )
			{
				getMembersWithCorrespondenceAndInterventionMoreThanX(subscriptionName,  DEFAULT_NUMBER_OF_CORRESPONDENCE_WITH_INTERVENTION);
			}
			return getRandomItem(RANDOM_MED_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION).getId();
		}
		throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
	}

	/**
	 * Retrieves members that has more than X number of both correspondence and intervention id
	 * 
	 * @author msharma
	 * @since 08/16/22
	 * 
	 * @param subscriptionName
	 * @param numOfCorrespondenceandIntervention
	 * @return list of members with corresponding counts of correspondence and intervention id
	 */
	public synchronized static List<GenericCount> getMembersWithCorrespondenceAndInterventionMoreThanX(String subscriptionName, int numOfCorrespondenceandIntervention)
	{
		logger.info("Retrieving from Cosmos DB a member with correspondence and intervention that is more than " + numOfCorrespondenceandIntervention);

		if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()) &&
				RANDOM_EXE_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION != null && RANDOM_EXE_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION.size() >= numOfCorrespondenceandIntervention )
		{
			return RANDOM_EXE_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION;
		}
		else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()) &&
				RANDOM_EHP_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION != null && RANDOM_EHP_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION.size() >= numOfCorrespondenceandIntervention )
		{
			return RANDOM_EHP_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION;
		}
		else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()) &&
				RANDOM_LOA_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION != null && RANDOM_LOA_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION.size() >= numOfCorrespondenceandIntervention )
		{
			return RANDOM_LOA_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION;
		}
		else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()) &&
				RANDOM_MED_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION != null && RANDOM_MED_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION.size() >= numOfCorrespondenceandIntervention )
		{
			return RANDOM_MED_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION;
		}

		/*
		 * Retrieve members with at least X number of correspondence
		 */

		String query = "SELECT * from (SELECT r.interventionId as intId, r.memberId as id,  r.num "
				+ "FROM (SELECT c.interventionId,c.memberId, count(1) as num FROM c WHERE c.type = \"correspondence\" and LENGTH(c.interventionId) > 100 GROUP BY c.interventionId, c.memberId) as r "
				+ "WHERE r.num > " + numOfCorrespondenceandIntervention +")";

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

		List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, 100);

		if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()) )
		{
			RANDOM_EXE_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION = list;
		}
		else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()) )
		{
			RANDOM_EHP_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION = list;
		}
		else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()) )
		{
			RANDOM_LOA_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION = list;
		}
		else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()) )
		{
			RANDOM_MED_MEMBERS_WITH_CORRESPONDENCE_AND_INTERVENTION = list;
		}
		else
		{
			throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe or ehp", subscriptionName) );
		}

		return list;
	}

	/**
	 * Retrieve the member correspondence
	 * 
	 * @author msharma
	 * @since 08/23/22
	 * @param subscriptionName for all tenant
	 * @param memberId of the member
	 * @param interventionId of the member correspondence
	 * @param id of the member correspondence
	 * @return
	 */
	public synchronized static MemberCorrespondence getMemberCorrespondence(String subscriptionName, String memberId, String interventionId, String id )
	{
		// Build the query
		String query = String.format(Queries.QUERY_BY_TYPE, "correspondence") + 
				String.format(" and c.memberId = \"%s\" and c.interventionId = \"%s\" and c.id = \"%s\"", memberId, interventionId, id);

		logger.info("Retrieving member correspondence from Cosmos DB");
		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
		List<MemberCorrespondence> memberCorrespondences = executeQuery(beanName, query, MemberCorrespondence.class, 1);

		if ( memberCorrespondences != null && memberCorrespondences.size() > 0 )
			return memberCorrespondences.get(0);

		return null;
	}

	/**
	 * Insert member correspondence into member container
	 * 
	 * @param subscriptionName either exe or ehp
	 * @param correspondence to be inserted
	 */
	public synchronized static void insertMemberCorrespondence(String subscriptionName, MemberCorrespondence correspondence)
	{
		logger.info("Inserting member correspondence into Cosmos DB with itemId {} partition key {}", correspondence.getId(), correspondence.getMemberId());

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

		insertItem(beanName, correspondence.getId(), correspondence.getMemberId(), correspondence);
	}


	/**
	 * Delete member correspondence from container
	 * @param subscriptionName either exe or ehp
	 * @param correspondenceId to be deleted
	 * @param memberId associated with the note to be deleted
	 */
	public synchronized static void deleteMemberCorrespondence(String subscriptionName, String correspondenceId, String memberId)
	{
		logger.info("Deleting member correspondence from Cosmos DB with itemId {} partition key {}", correspondenceId, memberId);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

		deleteItem(beanName, correspondenceId, memberId, MemberCorrespondence.class);
	}

	/**
	 * Retrieves the  member correspondence with  given  intervention id
	 * @msharma 01/26/23
	 * @param interventionId
	 * @return
	 */

	public synchronized  static MemberCorrespondence getCorrespondenceWithInterventionId(String interventionId)
	{
		String query = String.format(Queries.QUERY_BY_TYPE, "correspondence") +
				String.format(" and c.interventionId = \"%s\"", interventionId);

		Tenant tenant = TenantQueries.getTenants().stream()
				.filter( theTenant -> StringUtils.startsWith(interventionId, theTenant.getAdTenantId()))
				.findFirst()
				.orElseGet(null);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(tenant.getSubscriptionName());
		List<MemberCorrespondence> list = executeQuery(beanName, query, MemberCorrespondence.class);

		list = list.stream()  // MS (01/29/23) sort list by created date 
				.sorted(Comparator.comparing(MemberCorrespondence::getCreatedDateTime).reversed())
				.collect(Collectors.toList());

		list.stream() //fetch by latest in the container comapring the create Date Time
		.max(Comparator.comparing(MemberCorrespondence::getCreatedDateTime)).orElse(null);

		if ( list != null && list.size() > 0 )
		{
			return list.get(0);
		}

		return null;
	}


	/**
	 * Retrieves members that has Inbound and Outbound fax Provider number of correspondences  
	 * 
	 * @author Neeru Tagore (ntagore)
	 * @since 12/22/22
	 * 
	 * @param subscriptionName
	 * @return list of members with corresponding counts of correspondence
	 */
	public synchronized static MemberCorrespondence getMembersWithCorrespondenceType(String subscriptionName )	    
	{
		// Build the query
		String query = 
				"SELECT * FROM c "
						+ "WHERE c.type = 'correspondence' "
						+ "AND c.correspondenceOutcome = 'Inbound Fax' "
						+ "AND c.correspondenceType in ('Outbound Fax Provider', 'Inbound Fax Provider') "
						+ "AND c.createdBy ='System API' "
						+ String.format(" AND c.createdDateTime > \'%s\' ", Utility.getFirstOfLastMonth() )
						+ "ORDER by c.createdDateTime DESC ";

		logger.info("Retrieving member correspondenceType from Cosmos DB");

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

		List<MemberCorrespondence> memberCorrespondences = executeQuery(beanName, query, MemberCorrespondence.class, 10);

		if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName())||( memberCorrespondences != null && memberCorrespondences.size() > 0 ) )
		{
			return memberCorrespondences.get(0);
		}
		else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName())||( memberCorrespondences != null && memberCorrespondences.size() > 0 ))
		{
			return memberCorrespondences.get(0);
		}
		else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName())||( memberCorrespondences != null && memberCorrespondences.size() > 0 ) )
		{
			return memberCorrespondences.get(0);
		}
		else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName())||( memberCorrespondences != null && memberCorrespondences.size() > 0 ) )
		{
			return memberCorrespondences.get(0);
		}
		else
		{
			throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe or ehp", subscriptionName) );
		}

	}

	/**
	 * Retrieves the  intervention correspondence given a member id and intervention id
	 * @ntagore 01/19/23
	 * @param subscriptionName the member belongs to
	 * @param memberId to retrieve  intervention note
	 * @return list of {@link MemberCorrespondence}
	 */
	public synchronized static List<MemberCorrespondence> getInterventionCorrespondence(String subscriptionName, String memberId, String interventionId)
	{       
		String query = "SELECT * "
				+ "from c WHERE c.type = \"correspondence\" "
				+  String.format(" and c.memberId = \"%s\"", memberId)
				+  String.format(" and c.interventionId = \"%s\"", interventionId);

		logger.info("***interventionCorespondence Query ******  "+query);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
		List<MemberCorrespondence> list = executeQuery(beanName, query, MemberCorrespondence.class, 100);
		logger.info("***interventionCorespondence List ******  "+list);
		return list;
	}

	/**
	 * Returns a list of members with correspondence excluding certain createdBy records.
	 * If the results for the same subscription name have been previously retrieved,
	 * it returns the cached results.
	 * 
	 * @param subscriptionName The name of the subscription.
	 * @return A list of {@link GenericCount} objects representing the members with correspondence excluding the specified createdBy 
	 * @author msharma 02/27/23
	 */
	public synchronized static List<GenericCount> getMembersWithCorrespondenceIncludeInterventionNoteId(String subscriptionName)
	{
		final Logger logger = LoggerFactory.getLogger(MemberCorrespondenceQueries.class);

		if ( MEMBERS_WITH_CORRESPONDENCE_INCLUDE_INTERVENTIONNOTEID.containsKey(subscriptionName) )
		{
			logger.debug("Returning cached value for subscriptionName: {}", subscriptionName);
			return MEMBERS_WITH_CORRESPONDENCE_INCLUDE_INTERVENTIONNOTEID.get(subscriptionName);
		}

		final String query = "SELECT * FROM ( "
				+ " SELECT  r.interventionId as intId, r.interventionNoteId as typeId, r.memberId as id,  r.num "
				+ " FROM ( "
				+ " SELECT c.interventionId,c.interventionNoteId, c.memberId, count(1) as num "
				+ " FROM c "
				+ " WHERE c.type = \"correspondence\" and  IS_DEFINED( c.interventionNoteId)\r\n and NOT (c.createdBy = 'System API')"
				+ " GROUP BY c.interventionId, c.interventionNoteId, c.memberId"
				+ " ) as r "
				+ " WHERE r.num > 0)";


		logger.debug("Executing query: {}", query);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

		List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);
		MEMBERS_WITH_CORRESPONDENCE_INCLUDE_INTERVENTIONNOTEID.put(subscriptionName, list);

		logger.debug("Query executed successfully, result size: {}", list.size());

		return list;

	}


}
