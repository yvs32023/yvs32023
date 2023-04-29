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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.User;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 05/09/2022
 */
public class UserQueries extends Queries
{
	private static final Logger logger = LoggerFactory.getLogger(UserQueries.class);


	private volatile static Queue<User> RANDOM_EXE_USER;    // contains random EXE member
	private volatile static Queue<User> RANDOM_EHP_USER;    // contains random EHP member

	/**
	 * Retrieve a random EXE user
	 *
	 * @return a random EXE user
	 */
	public synchronized static User getEXEUser(String accntName)
	{
		if ( (RANDOM_EXE_USER != null && RANDOM_EXE_USER.size() > 0) )
		{
			return RANDOM_EXE_USER.remove();
		}

		RANDOM_EXE_USER = getRandomUsers(BeanNames.EXE_CONTAINER_USER, " where c.name = \'" + accntName + "\'" , DEFAULT_MAX_BUFFER_ITEM_COUNT);
		return RANDOM_EXE_USER.remove();
	}

	/**
	 * Retrieve a random EHP user
	 *
	 * @return a random EHP user
	 */
	public synchronized static User getEHPUser(String accntName )
	{
		if ( (RANDOM_EHP_USER != null && RANDOM_EHP_USER.size() > 0) )
		{
			return RANDOM_EHP_USER.remove();
		}

		RANDOM_EHP_USER = getRandomUsers(BeanNames.EHP_CONTAINER_USER, " where c.name = \'" + accntName + "\'" , DEFAULT_MAX_BUFFER_ITEM_COUNT);
		return RANDOM_EHP_USER.remove();
	}



	/**
	 * Perform query to the user
	 * @param beanName of the user defined in {@link com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig}
	 * @param size of the user to retrieve
	 * @return list of users
	 */
	private synchronized static Queue<User> getRandomUsers(String beanName, String whereClause, int size)
	{
		
		String query = String.format(Queries.QUERY_ALL);
		query += StringUtils.isNotBlank(whereClause) ?  whereClause : "";


		List<User> userList = executeQuery(beanName, query, User.class, size);
		Queue<User> queue = new LinkedList<>(userList);

		return queue;
	}
	
	/**
	 * Retrieve the user
	 * 
	 * @author gcosmian
	 * @since 09/29/22
	 * @param subscriptionName either exe or ehp
	 * @param oid of the user
	 * @param  id associated with the user
	 * @return
	 */
	public synchronized static User getUser(String subscriptionName, String id, String oid)
	{
		// Build the query
		String query = String.format(Queries.QUERY_ALL) + 
				String.format(" where c.id = \"%s\" and c.oid = \"%s\"", id, oid);

		logger.info("Retrieving user from Cosmos DB");
		String beanName = RxConciergeCosmoConfig.getUserContainerBeanName(subscriptionName);
		List<User> users = executeQuery(beanName, query, User.class, 1);

		if ( users != null && users.size() > 0 )
			return users.get(0);

		return null;
	}
	
	/**
	 * Insert user into EXE member container
	 * @param user {@link User} to be inserted into Cosmos DB
	 */
	public synchronized static void insertExeUser(User user)
	{
		insertUser(Tenant.Type.EXE.getSubscriptionName(), user);
	}

	/**
	 * Insert user into EHP member container
	 * @param user {@link User} to be inserted into Cosmos DB
	 */
	public synchronized static void insertEhpUser(User user)
	{
		insertUser(Tenant.Type.EHP.getSubscriptionName(), user);
	}

	/**
	 * Insert user into user container
	 * 
	 * @param subscriptionName either exe or ehp
	 * @param user to be inserted
	 * 
	 * @author msharma 
	 * @date 09/29/2022
	 * 
	 */
	public synchronized static void insertUser(String subscriptionName,  User user)
	{
		logger.info("Inserting user into Cosmos DB with itemId {} partition key {}", user.getId(), user.getOid());

		String beanName = RxConciergeCosmoConfig.getUserContainerBeanName(subscriptionName);

		insertItem(beanName, user.getId(), user.getOid(), user);
	}
	
	/**
	 * Delete user from EXE member container
	 * @param oid to be deleted
	 * @param id associated with the user to be deleted
	 */
	public synchronized static void deleteExeUser(String id, String oid)
	{
		deleteUser(Tenant.Type.EXE.getSubscriptionName(), id, oid);
	}

	/**
	 * Delete member note from EXE member container
	 * @param oid to be deleted
	 * @param id associated with the user to be deleted
	 */
	public synchronized static void deleteEhpUser(String id, String oid)
	{
		deleteUser(Tenant.Type.EHP.getSubscriptionName(), id, oid);
	}

	/**
	 * Delete user from container
	 * 
	 * @param oid to be deleted
	 * @param subscriptionName either exe or ehp
	 * @param id associated with the user to be deleted
	 * 
	 * @author msharma
	 * @date 09/29/2022
	 */
	public synchronized static void deleteUser(String subscriptionName,String id, String oid)
	{
		logger.info("Deleting user from Cosmos DB with itemId {} partition key {}", id, oid);

		String beanName = RxConciergeCosmoConfig.getUserContainerBeanName(subscriptionName);

		deleteItem(beanName, id, oid, User.class);
	}
	
}
