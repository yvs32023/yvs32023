/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.cosmos;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.Utility;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.dto.GenericCount;
import com.excellus.sqa.rxcc.dto.MemberCorrespondence;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.Tenant;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 10/06/2022
 */
public class MemberInterventionQueries extends Queries
{

	private static final Logger logger = LoggerFactory.getLogger(MemberInterventionQueries.class);

	private volatile static Map<String, List<GenericCount>> MEMBERS_WITH_INTERVENTION = new HashMap<>();
	private volatile static Map<String, List<GenericCount>> MEMBERS_WITH_INTERVENTION_TYPE = new HashMap<>();
	private volatile static Map<String, List<GenericCount>> MEMBERS_WITH_INTERVENTION_EXCLUDE_QUEUESTATUSCODE = new HashMap<>();
	private volatile static Map<String, List<GenericCount>> MEMBERS_WITH_INTERVENTION_INCLUDE_QUEUESTATUSCODE = new HashMap<>();
	private volatile static Map<String, List<GenericCount>> MEMBERS_WITH_INTERVENTION_BY_RULETYPE= new HashMap<>();

	//	private volatile static List<GenericCount> RANDOM_EHP_MEMBERS_WITH_INTERVENTION_TYPE;
	//	private volatile static List<GenericCount> RANDOM_EXE_MEMBERS_WITH_INTERVENTION_TYPE;
	//	private volatile static List<GenericCount> RANDOM_LOA_MEMBERS_WITH_INTERVENTION_TYPE;
	//	private volatile static List<GenericCount> RANDOM_MED_MEMBERS_WITH_INTERVENTION_TYPE;


	/**
	 * Retrieve random member id with member intervention
	 * @param tenant {@link Tenant}
	 * @return member id
	 */
	public synchronized static String getRandomMemberWithIntervention(Tenant tenant)
	{
		return getRandomMemberWithIntervention(tenant.getSubscriptionName());
	}

	/**
	 * Retrieve random member id with member intervention
	 * @param tenantType {@link Tenant.Type}
	 * @return member id
	 */
	public synchronized static String getRandomMemberWithIntervention(Tenant.Type tenantType)
	{
		return getRandomMemberWithIntervention(tenantType.getSubscriptionName());
	}

	/**
	 * Retrieve random member id with member intervention
	 * @param subscriptionName tenant subscription name
	 * @return member id
	 */
	public synchronized static String getRandomMemberWithIntervention(String subscriptionName)
	{
		logger.info("Retrieving random member id with intervention");

		return getRandomItem(getMemberWithIntervention(subscriptionName)).getId();
	}


	/**
	 * Get list of member ids with at least 1 or more member intervention
	 * @param subscriptionName the members belongs to
	 * @return list of member ids with number of intervention
	 */
	public synchronized static List<GenericCount> getMemberWithIntervention(String subscriptionName)
	{
		if ( MEMBERS_WITH_INTERVENTION.containsKey(subscriptionName) )
		{
			return MEMBERS_WITH_INTERVENTION.get(subscriptionName);
		}

		final String query = "SELECT * FROM ( "
				+ "    SELECT r.memberId as id, r.num "
				+ "    FROM ( "
				+ "        SELECT c.memberId, count(1) as num FROM c WHERE c.type = \"intervention\" "
				+ " GROUP BY c.memberId "
				+ "        ) as r "
				+ ") as a "
				+ "WHERE a.num > 0";

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

		List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);
		MEMBERS_WITH_INTERVENTION.put(subscriptionName, list);

		return list;
	}
	/**
	 * Retrieves all the member intervention given a member id
	 *
	 * @param subscriptionName the member belongs to
	 * @param memberId to retrieve intervention
	 * @return list of {@link MemberIntervention}
	 */
	public synchronized static List<MemberIntervention> getIntervention(String subscriptionName, String memberId)
	{
		String query = String.format(Queries.QUERY_BY_TYPE, "intervention") +
				String.format(" AND c.memberId = \"%s\"", memberId) ;
		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
		List<MemberIntervention> list = executeQuery(beanName, query, MemberIntervention.class, 100);

		return list;
	}

	public synchronized  static MemberIntervention getIntervention(String interventionId)
	{
		String query = String.format(Queries.QUERY_BY_TYPE, "intervention") +
				String.format(" and c.id = \"%s\"", interventionId);

		Tenant tenant = TenantQueries.getTenants().stream()
				.filter( theTenant -> StringUtils.startsWith(interventionId, theTenant.getAdTenantId()))
				.findFirst()
				.orElseGet(null);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(tenant.getSubscriptionName());
		List<MemberIntervention> list = executeQuery(beanName, query, MemberIntervention.class);

		list = list.stream()  // MS (01/29/23) sort list by created date 
				.sorted(Comparator.comparing(MemberIntervention::getCreatedDateTime))
				.collect(Collectors.toList());


		if ( list != null && list.size() > 0 )
		{
			return list.get(0);
		}

		return null;
	}

	/**
	 * Retrieves all the member intervention given a member id, targetInterventionQueueType & subscriptionName
	 *
	 * @param subscriptionName the member belongs to
	 * @param memberId to retrieve intervention
	 * @return list of {@link MemberIntervention}
	 */
	public synchronized static List<MemberIntervention> getInterventionByType(String subscriptionName, String memberId, String targetInterventionQueueType)
	{
		String query = String.format(Queries.QUERY_BY_TYPE, "intervention") +
				String.format(" and c.memberId = \"%s\"", memberId)	+
				String.format(" and c.targetInterventionQueueType = \"%s\"", targetInterventionQueueType);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
		List<MemberIntervention> list = executeQuery(beanName, query, MemberIntervention.class, 100);

		return list;
	}


	/**
	 * Retrieve member interventions from specific container with the given id
	 * @param id list of id
	 * @return list of MemberInterventions
	 *
	 * @author Manish Sharma (msharma)
	 * @since 11/29/22
	 */
	public synchronized static List<MemberIntervention> getInterventionById(String subscriptionName,String... id)
	{
		logger.info("Retrieving member intervention from Cosmos DB with multiple intervention id");

		final String query = String.format("SELECT * FROM c WHERE c.type ='intervention' and c.id in (%s)",
				"\"" + String.join("\", \"", id) + "\"");

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
		return executeQuery(beanName, query, MemberIntervention.class);
	}



	/**
	 * Retrieve random member id with member intervention based on  subscriptionName & type , optional field providerOverride
	 * @param subscriptionName of the tenant
	 * @param type of intervention queue
	 * @aram providerOverride true or false
	 * @return member id
	 */
	public synchronized static String getRandomMemberWithInterventionBasedOnTargetInterventionQueueType(String subscriptionName, String type, String providerOverride)
	{
		logger.info("Retrieving random member id with intervention");

		String key = subscriptionName + "_" + type + (StringUtils.isNotBlank(providerOverride) ? "_" + providerOverride : "");
		if ( !MEMBERS_WITH_INTERVENTION_TYPE.containsKey(key) )
		{
			getMemberWithInterventionBasedOnTargetInterventionQueueType(subscriptionName, type, providerOverride);
		}

		GenericCount genericCount = getRandomItem( MEMBERS_WITH_INTERVENTION_TYPE.get(key) );
		return (genericCount != null) ? genericCount.getId() : null;

		//		if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EHP.getSubscriptionName()) )
		//		{
		//			getMemberWithInterventionBasedOnTargetInterventionQueueType(subscriptionName, type, providerOverride);
		//
		//			GenericCount genericCount = getRandomItem(RANDOM_EHP_MEMBERS_WITH_INTERVENTION_TYPE);
		//			return (genericCount != null) ? genericCount.getId() : null;
		//		}
		//		else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.EXE.getSubscriptionName()) )
		//		{
		//			getMemberWithInterventionBasedOnTargetInterventionQueueType(subscriptionName, type, providerOverride);
		//
		//			GenericCount genericCount = getRandomItem(RANDOM_EXE_MEMBERS_WITH_INTERVENTION_TYPE);
		//			return (genericCount != null) ? genericCount.getId() : null;
		//		}
		//		else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.LOA.getSubscriptionName()) )
		//		{
		//			getMemberWithInterventionBasedOnTargetInterventionQueueType(subscriptionName, type, providerOverride);
		//
		//			GenericCount genericCount = getRandomItem(RANDOM_LOA_MEMBERS_WITH_INTERVENTION_TYPE);
		//			return (genericCount != null) ? genericCount.getId() : null;
		//		}
		//		else if ( StringUtils.equalsIgnoreCase(subscriptionName, Type.MED.getSubscriptionName()) )
		//		{
		//			getMemberWithInterventionBasedOnTargetInterventionQueueType(subscriptionName , type, providerOverride);
		//
		//			GenericCount genericCount = getRandomItem(RANDOM_MED_MEMBERS_WITH_INTERVENTION_TYPE);
		//			return (genericCount != null) ? genericCount.getId() : null;
		//		}
		//
		//		throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
	}


	/**
	 * Get list of member ids with at least 1 or more member intervention which for particular targetInterventionQueueType
	 * @param subscriptionName the members belongs to , targetInterventionQueueType , targetInterventionQueueType(optional)
	 * @return list of member ids with number of intervention
	 */
	public synchronized static List<GenericCount> getMemberWithInterventionBasedOnTargetInterventionQueueType(String subscriptionName, String type, String providerOverride)
	{

		if ((StringUtils.isNotBlank(subscriptionName)) && ((StringUtils.isNotBlank(type)) && ((StringUtils.isNotBlank(providerOverride) && providerOverride != null))))
		{
			final String query = "SELECT * FROM ( "
					+ "    SELECT r.memberId as id, r.num "
					+ "    FROM ( "
					+ "    SELECT c.memberId, count(1) as num FROM c WHERE c.type = \"intervention\" and c.targetInterventionQueueType = \'" + type + "\' and c.providerOverride = " + providerOverride + " and c.queueStatusCode in (\"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\", \"11\", \"12\", \"13\", \"14\", \"15\", \"16\", \"17\", \"18\", \"19\", \"20\", \"21\") GROUP BY c.memberId "
					+ "        ) as r "
					+ ") as a "
					+ "WHERE a.num > 0";

			String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

			List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);

			String key = subscriptionName + "_" + type + "_" + providerOverride;
			MEMBERS_WITH_INTERVENTION_TYPE.put(key, list);


			//			if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()) )
			//			{
			//				RANDOM_EXE_MEMBERS_WITH_INTERVENTION_TYPE = new LinkedList<>(list);
			//
			//			}
			//			else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()) )
			//			{
			//				RANDOM_EHP_MEMBERS_WITH_INTERVENTION_TYPE = new LinkedList<>(list);
			//			}
			//			else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()) )
			//			{
			//				RANDOM_LOA_MEMBERS_WITH_INTERVENTION_TYPE = new LinkedList<>(list);
			//			}
			//			else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()) )
			//			{
			//				RANDOM_MED_MEMBERS_WITH_INTERVENTION_TYPE = new LinkedList<>(list);
			//			}
			//			else
			//			{
			//				throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
			//			}

			return list;
		}


		else if ((StringUtils.isNotBlank(subscriptionName)) && ((StringUtils.isNotBlank(type)) && ((StringUtils.isBlank(providerOverride) || providerOverride == null ))))
		{
			final String query = "SELECT * FROM ( "
					+ "    SELECT r.memberId as id, r.num "
					+ "    FROM ( "
					+ "      SELECT c.memberId, count(1) as num FROM c WHERE c.type = \"intervention\" and c.targetInterventionQueueType = \'" + type + "\'  and c.queueStatusCode in (\"1\", \"2\", \"3\", \"4\", \"5\", \"6\", \"7\", \"8\", \"9\", \"10\", \"11\", \"12\", \"13\", \"14\", \"15\", \"16\", \"17\", \"18\", \"19\", \"20\", \"21\") GROUP BY c.memberId"
					+ "        ) as r "
					+ ") as a "
					+ "WHERE a.num > 0";

			String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

			List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);

			String key = subscriptionName + "_" + type;
			MEMBERS_WITH_INTERVENTION_TYPE.put(key, list);

			//			if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()) )
			//			{
			//				RANDOM_EXE_MEMBERS_WITH_INTERVENTION_TYPE = new LinkedList<>(list);
			//			}
			//			else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()) )
			//			{
			//				RANDOM_EHP_MEMBERS_WITH_INTERVENTION_TYPE = new LinkedList<>(list);
			//			}
			//			else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName()) )
			//			{
			//				RANDOM_LOA_MEMBERS_WITH_INTERVENTION_TYPE = new LinkedList<>(list);
			//			}
			//			else if ( subscriptionName.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName()) )
			//			{
			//				RANDOM_MED_MEMBERS_WITH_INTERVENTION_TYPE = new LinkedList<>(list);
			//			}
			//			else
			//			{
			//				throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe, ehp, loa or med", subscriptionName) );
			//			}

			return list;
		}

		return null;
	}


	/**
	 * Retrieve random member id with member intervention for UI Testing
	 * @param subscriptionName tenant subscription name
	 * @return member id
	 * @author ntagore 01/23/23
	 */
	public synchronized static String getRandomMemberWithInterventionUI(String subscriptionName)
	{
		logger.info("Retrieving random member id with intervention");

		return getRandomItem(getMemberWithInterventionUI(subscriptionName)).getId();
	}

	/**
	 * Get list of member ids with at least 1 or more member intervention for UI Testing
	 * @param subscriptionName the members belongs to
	 * @return list of member ids with number of intervention
	 * @author ntagore 01/23/23
	 */
	public synchronized static List<GenericCount> getMemberWithInterventionUI(String subscriptionName)
	{
		if ( MEMBERS_WITH_INTERVENTION.containsKey(subscriptionName) )
		{
			return MEMBERS_WITH_INTERVENTION.get(subscriptionName);
		}

		final String query = "SELECT * FROM ( "
				+ "    SELECT r.memberId as id, r.num "
				+ "    FROM ( "
				+ "        SELECT c.memberId, count(1) as num FROM c WHERE c.type = \"intervention\" "
				+ String.format(" AND c.createdDateTime > \'%s\' ", Utility.getFirstOfLastMonth() )  //(@ntagore)added this to bring member with latest interventions
				+ " AND c.originalOfficeFaxVerified = true "
				+ " GROUP BY c.memberId "
				+ "        ) as r "
				+ ") as a "
				+ "WHERE a.num > 0";

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

		List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);
		MEMBERS_WITH_INTERVENTION.put(subscriptionName, list);

		return list;
	}
	/**
	 * Retrieves all the member intervention given a member id for UI testing
	 * @ntagore 01/23/23
	 * @param subscriptionName the member belongs to
	 * @param memberId to retrieve intervention
	 * @return list of {@link MemberIntervention}
	 */
	public synchronized static List<MemberIntervention> getInterventionUI(String subscriptionName, String memberId)
	{
		String query = String.format(Queries.QUERY_BY_TYPE, "intervention") +
				String.format(" AND c.createdDateTime > \'%s\' ", Utility.getFirstOfLastMonth() ) +  //(@ntagore)added this to bring latest interventions
				String.format(" AND c.memberId = \"%s\"", memberId) +
				" AND c.originalOfficeFaxVerified = true " ;//(@ntagore) fiter iterventions with provider fax verified status is true. This will make sure Fax Send is enabled.

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
		List<MemberIntervention> list = executeQuery(beanName, query, MemberIntervention.class, 100);

		return list;
	}


	/**
	 * Retrieves the  member intervention given a member id and intervention id
	 * @ntagore 01/20/23
	 * @param subscriptionName the member belongs to
	 * @param memberId to retrieve  intervention note
	 * @return list of {@link MemberCorrespondence}
	 */
	public synchronized static List<MemberIntervention> getInterventionUIData(String subscriptionName, String memberId, String id)
	{
		String query = "SELECT * "
				+ "from c WHERE c.type = \'intervention\' "
				+  String.format(" and c.memberId = \'%s\'", memberId)
				+  String.format(" and c.id = \'%s\'", id);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);
		List<MemberIntervention> list = executeQuery(beanName, query, MemberIntervention.class, 100);
		return list;
	}



	/**
	 * Returns a list of {@link GenericCount} objects for members with certain queue status codes for their interventions.
	 * 
	 * @param subscriptionName The name of the subscription for which to retrieve members with interventions.
	 * @param queueStatusCode The queue status codes to search for.
	 * @return A list of {@link GenericCount} objects representing the members with the specified queue status codes for their interventions.
	 * 
	 * @see GenericCount
	 * @author msharma 02/07/23
	 */
	public synchronized static List<GenericCount> getMembersWithInterventionCertainQueueStatusCode(String subscriptionName,String... queueStatusCode)
	{
		final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

		// Check if the list of members with interventions has already been retrieved and cached
		if ( MEMBERS_WITH_INTERVENTION_INCLUDE_QUEUESTATUSCODE.containsKey(subscriptionName) )
		{
			// If so, return the cached list
			logger.debug("Returning cached list of members with interventions for subscription {}", subscriptionName);
			return MEMBERS_WITH_INTERVENTION_INCLUDE_QUEUESTATUSCODE.get(subscriptionName);
		}

		// If not, create a new query to retrieve the list of members with interventions
		final String query = String.format("SELECT * FROM (\n" + 
				" SELECT r.memberId as id,  r.id as intId, r.num\n" + 
				" FROM (\n" + 
				"   SELECT c.memberId, c.id ,count(1) as num\n" + 
				"   FROM c\n" + 
				"   WHERE c.type = 'intervention' and c.queueStatusCode  in ('%s')\n" + 
				"   GROUP BY c.memberId, c.id\n" + 
				" ) as r\n" + 
				") as a\n" + 
				"WHERE a.num > 0", 
				String.join("', '", queueStatusCode));

		// Retrieve the bean name for the specified subscription
		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

		// Execute the query to retrieve the list of members with interventions
		List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);

		// Cache the list of members with interventions
		MEMBERS_WITH_INTERVENTION_INCLUDE_QUEUESTATUSCODE.put(subscriptionName, list);

		logger.debug("Returning list of members with interventions for subscription {}", subscriptionName);
		// Return the list of members with interventions
		return list;
	}


	/**
	 * Returns a list of members with interventions excluding certain queue status codes.
	 * If the results for the same subscription name have been previously retrieved,
	 * it returns the cached results.
	 * 
	 * @param subscriptionName The name of the subscription.
	 * @param queueStatusCode The queue status codes to exclude.
	 * @return A list of {@link GenericCount} objects representing the members with interventions excluding the specified queue status codes.
	 * @author msharma 02/07/23
	 */
	public synchronized static List<GenericCount> getMembersWithInterventionsExcludingQueueStatusCode(String subscriptionName,String... queueStatusCode)
	{
		final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

		if ( MEMBERS_WITH_INTERVENTION_EXCLUDE_QUEUESTATUSCODE.containsKey(subscriptionName) )
		{
			logger.debug("Returning cached value for subscriptionName: {}", subscriptionName);
			return MEMBERS_WITH_INTERVENTION_EXCLUDE_QUEUESTATUSCODE.get(subscriptionName);
		}

		final String query = String.format("SELECT * FROM (\n" + 
				" SELECT r.memberId as id,  r.id as intId, r.num\n" + 
				" FROM (\n" + 
				"   SELECT c.memberId, c.id ,count(1) as num\n" + 
				"   FROM c\n" + 
				"   WHERE c.type = 'intervention' and c.queueStatusCode not in (%s)\n" + 
				"   GROUP BY c.memberId, c.id\n" + 
				" ) as r\n" + 
				") as a\n" + 
				"WHERE a.num > 0", 
				String.join(", ", queueStatusCode));

		logger.debug("Executing query: {}", query);

		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

		List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);
		MEMBERS_WITH_INTERVENTION_EXCLUDE_QUEUESTATUSCODE.put(subscriptionName, list);

		logger.debug("Query executed successfully, result size: {}", list.size());

		return list;
	}

	/**
	 * Retrieve random member id with member intervention based on  subscriptionName & ruleType 
	 * @param subscriptionName of the tenant
	 * @param ruleType of intervention queue
	 * @return member id
	 * @author msharma 03/03/23
	 */
	public synchronized static String getRandomMembersWithInterventionByRuleType(String subscriptionName, String ruleType)
	{
		logger.info("Retrieving random member id with intervention");

		String key = subscriptionName + "_" + ruleType;
		if ( !MEMBERS_WITH_INTERVENTION_BY_RULETYPE.containsKey(key) )
		{
			getMembersWithInterventionByRuleType(subscriptionName, ruleType);
		}

		GenericCount genericCount = getRandomItem( MEMBERS_WITH_INTERVENTION_BY_RULETYPE.get(key) );
		return (genericCount != null) ? genericCount.getId() : null;

	}


	/**
	 * Returns a list of {@link GenericCount} objects for members with certain ruleType for their interventions.
	 * 
	 * @param subscriptionName The name of the subscription for which to retrieve members with interventions.
	 * @param ruleType The ruleType to search for.
	 * @return A list of {@link GenericCount} objects representing the members with the specified queue status codes for their interventions.
	 * 
	 * @see GenericCount
	 * @author msharma 03/03/23
	 */
	public synchronized static List<GenericCount> getMembersWithInterventionByRuleType(String subscriptionName,String ruleType)
	{
		final Logger logger = LoggerFactory.getLogger(MemberInterventionQueries.class);

		// Check if the list of members with interventions has already been retrieved and cached
		if ( MEMBERS_WITH_INTERVENTION_BY_RULETYPE.containsKey(subscriptionName) )
		{
			// If so, return the cached list
			logger.debug("Returning cached list of members with interventions for subscription {}", subscriptionName);
			return MEMBERS_WITH_INTERVENTION_BY_RULETYPE.get(subscriptionName);
		}

		// If not, create a new query to retrieve the list of members with interventions
		final String query = "SELECT * FROM ( "
				+ "    SELECT r.memberId as id, r.num "
				+ "    FROM ( "
				+ String.format( "SELECT c.memberId, count(1) as num FROM c WHERE c.type = \"intervention\" AND c.ruleType =  \'%s\'", ruleType) 
				+ " GROUP BY c.memberId "
				+ "        ) as r "
				+ ") as a "
				+ "WHERE a.num > 0";

		// Retrieve the bean name for the specified subscription
		String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

		// Execute the query to retrieve the list of members with interventions
		List<GenericCount> list = executeQuery(beanName, query, GenericCount.class, DEFAULT_MAX_BUFFER_ITEM_COUNT);

		String key = subscriptionName + "_" + ruleType;
		// Cache the list of members with interventions
		MEMBERS_WITH_INTERVENTION_BY_RULETYPE.put(key, list);

		logger.debug("Returning list of members with interventions for subscription {}", subscriptionName);
		// Return the list of members with interventions
		return list;
	}
	
	 // vijaya sekhar - New method to query member intervention base on where clause fields
	  public static List<MemberIntervention> searchInterventionsQueue(String subscriptionName,
	      String whereClause) {
	    final String query = "SELECT * FROM c WHERE " + whereClause;

	    String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

	    return executeQuery(beanName, query, MemberIntervention.class);

	  }


	  // vijaya sekhar - New method to query member intervention base on query
	  public static List<MemberIntervention> searchInterventionsByQuery(String subscriptionName,
	      String query) {


	    String beanName = RxConciergeCosmoConfig.getMemberContainerBeanName(subscriptionName);

	    return executeQuery(beanName, query, MemberIntervention.class);

	  }

}