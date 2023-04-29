/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosPatchItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.PartitionKey;
import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.spring.BeanLoader;

/**
 * List of queries for Cosmos DB
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/05/2022
 */
public class Queries
{
	private static final Logger logger = LoggerFactory.getLogger(Queries.class);

	/*
	 * General
	 */

	protected static int DEFAULT_MAX_BUFFER_ITEM_COUNT = 100;

	public final static String QUERY_ALL = "SELECT * FROM c";
	public final static String QUERY_BY_TYPE = "SELECT * FROM c where c.type = \"%s\"";
	public final static String QUERY_BY_ID = "SELECT * FROM c where c.id = \"%s\"";

	/**
	 * Execute the Cosmos DB query.<br/>
	 * The code below is taken from <a href="https://github.com/Azure-Samples/azure-cosmos-java-sql-api-samples/blob/main/src/main/java/com/azure/cosmos/examples/queries/sync/QueriesQuickstart.java">Azure Cosmos DB queries via Java SQL API</a>
	 *
	 * @author gcosmian
	 * @since 03/14/22
	 * @param beanName for the CosmosContainer, see {@link RxConciergeCosmoConfig}
	 * @param query to be executed
	 * @param clazz the class type to be returned by the query
	 * @param <T> the class type to be returned by the query
	 * @return List of Cosmos DB documents
	 */
	protected synchronized static <T> List<T> executeQuery(String beanName, String query, Class<T> clazz)
	{
		return executeQuery(beanName, query, clazz, 100);
	}

	/**
	 * Execute the Cosmos DB query.<br/>
	 * The code below is taken from <a href="https://github.com/Azure-Samples/azure-cosmos-java-sql-api-samples/blob/main/src/main/java/com/azure/cosmos/examples/queries/sync/QueriesQuickstart.java">Azure Cosmos DB queries via Java SQL API</a>
	 *
	 * @author gcosmian
	 * @since 03/07/22
	 * @param beanName for the CosmosContainer, see {@link RxConciergeCosmoConfig}
	 * @param query to be executed
	 * @param clazz the class type to be returned by the query
	 * @param size of the maximum buffered item of query execution
	 * @param <T> the class type to be returned by the query
	 * @return List of Cosmos DB documents
	 */
	protected synchronized static <T> List<T> executeQuery(String beanName, String query, Class<T> clazz, int size)
	{
		// Retrieve the CosmosContainer
		CosmosContainer container = BeanLoader.loadBean(beanName, CosmosContainer.class);

		// Setup the maximum buffered item count
		if ( size < 1 ) {
			size = DEFAULT_MAX_BUFFER_ITEM_COUNT;
		}

		CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
		queryOptions.setMaxBufferedItemCount(size);

		int pageSize = 100; //No of docs per page
		int currentPageNumber = 1;
		int documentNumber = 0;
		String continuationToken = null;
		double requestCharge = 0.0;

		List<T> list = new LinkedList<T>();

		long start = System.currentTimeMillis();

		do {
			logger.info("Receiving a set of query response pages.");
			logger.info("Continuation Token: " + continuationToken + "\n");

			Iterable<FeedResponse<T>> feedResponseIterator =
					container.queryItems(query, queryOptions, clazz).iterableByPage(continuationToken, pageSize);

			for (FeedResponse<T> page : feedResponseIterator) {

				logger.info(String.format("Current page number: %d", currentPageNumber));

				// Access all of the documents in this result page
				for (T docProps : page.getResults()) {
					list.add(docProps);
					documentNumber++;
				}

				// Accumulate the request charge of this page
				requestCharge += page.getRequestCharge();

				// Page count so far
				logger.info(String.format("Total documents received so far: %d", documentNumber));

				// Request charge so far
				logger.info(String.format("Total request charge so far: %f\n", requestCharge));

				// STOP iterating the page if we have the number of document needed
				if ( documentNumber >= size ) {
					break;
				}

				// Along with page results, get a continuation token
				// which enables the client to "pick up where it left off"
				// in accessing query response pages.
				continuationToken = page.getContinuationToken();

				currentPageNumber++;
			}
		} while (continuationToken != null);

		long end = System.currentTimeMillis();
		logger.info(String.format("Cosmos DB: the query [%s] took %d seconds", query, ((end-start)/1000) ));

		return list;
	}

	/**
	 * Insert an item into Cosmos DB container
	 * 
	 * @param beanName that corresponds to the Cosmos DB container
	 * @param itemId of the item to be inserted
	 * @param partitionKey of the container
	 * @param item to be inserted into Cosmos DB container
	 * @param <T> the class type to be inserted into Cosmos DB container
	 */
	protected synchronized static <T> void insertItem(String beanName, String itemId, String partitionKey, T item)
	{
		// Retrieve the CosmosContainer
		CosmosContainer container = BeanLoader.loadBean(beanName, CosmosContainer.class);

		logger.info("Inserting into Cosmos Container {} (beanName) with itemId {} partition key {}", beanName, itemId, partitionKey);
		logger.info("Inserting item {}", item.toString());

		CosmosItemResponse<T> response = container.createItem(item, new PartitionKey(partitionKey), new CosmosItemRequestOptions());
		logger.info("Status code for creating item: {}", response.getStatusCode());
		logger.info("Done creating");
	}

	/**
	 * Delete an item from Cosmos DB container
	 * 
	 * @param beanName that corresponds to the Cosmos DB container
	 * @param itemId of the item to be inserted
	 * @param partitionKey of the container
	 * @param clazz type to be deleted from Cosmos DB container
	 * @param <T> the class type to be deleted from Cosmos DB container
	 */
	protected synchronized static <T> void deleteItem(String beanName, String itemId, String partitionKey, Class<T> clazz)
	{
		// Retrieve the CosmosContainer
		CosmosContainer container = BeanLoader.loadBean(beanName, CosmosContainer.class);

		CosmosItemResponse<T> item = container.readItem(itemId, new PartitionKey(partitionKey), clazz);

		if ( item != null )
		{
			logger.info("Deleting from Cosmos Container {} (beanName) with itemId {} partition key {}", beanName, itemId, partitionKey);

			CosmosItemResponse<Object>  response = container.deleteItem(itemId, new PartitionKey(partitionKey), new CosmosItemRequestOptions());
			logger.info("Done deleting. Status code for item delete: {}", response.getStatusCode());
		}
		else
		{
			logger.warn("Nothing to delete. The item id {} and partition key {} does not exists", itemId, partitionKey);
		}
	}

	/**
	 * Patch/Update an existing item from Cosmos DB container
	 * 
	 * @author gcosmian
	 * @since 03/22/22
	 * @param <T> the class type to be deleted from Cosmos DB container
	 * @param beanName that corresponds to the Cosmos DB container
	 * @param itemId of the item to be updated
	 * @param partitionKey of the container
	 * @param item to replace it with
	 * @param clazz type to be deleted from Cosmos DB container
	 */
	protected synchronized static <T> void replaceItem(String beanName, String itemId, String partitionKey, T item, Class<T> clazz)
	{
		// Retrieve the CosmosContainer
		CosmosContainer container = BeanLoader.loadBean(beanName, CosmosContainer.class);

		CosmosItemResponse<T> theItem = container.readItem(itemId, new PartitionKey(partitionKey), clazz);

		if ( theItem != null )
		{
			CosmosPatchItemRequestOptions options = new CosmosPatchItemRequestOptions();

			logger.info("Replace) existing item with itemId {} and partition key {} from Cosmos Container {} (beanName)", itemId, partitionKey, beanName);

			container.replaceItem(item, itemId, new PartitionKey(partitionKey), options);

			logger.info("Item has been replaced for id {}", itemId);
		}
		else
		{
			logger.warn("Nothing to replace. The item id {} and partition key {} does not exists", itemId, partitionKey);
		}

	}

	/**
	 * Retrieve an item from Cosmos container
	 * 
	 * @author gcosmian
	 * @since 03/21/22
	 * @param <T> the class type to be retrieved from Cosmos DB container
	 * @param beanName that corresponds to the Cosmos DB container
	 * @param itemId of the item to be retrieved
	 * @param partitionKey of the container
	 * @param clazz type to be retrieved from Cosmos DB container
	 * @return an instance of <T> that represent the item
	 */
	protected synchronized static <T> T retrieveItem(String beanName, String itemId, String partitionKey, Class<T> clazz)
	{
		// Retrieve the CosmosContainer
		CosmosContainer container = BeanLoader.loadBean(beanName, CosmosContainer.class);

		CosmosItemResponse<T> item = container.readItem(itemId, new PartitionKey(partitionKey), clazz);

		if ( item != null )
		{
			return item.getItem();
		}

		return null;
	}

	/**
	 * Retrieve random item from a list
	 * @param <T> Generic type
	 * @param items list of items
	 * @return a random item
	 * 
	 * @author Garrett Cosmiano (gcosmian)
	 * @since 09/07/22
	 */
	public synchronized static <T> T getRandomItem(List<T> items)
	{
		
		
		if (items == null ||items.size() == 0 )
		{
			return null;
		}

		else if (items.size() > 0)
		{
			int min = 0;
			int max = items.size()-1;
			int index = (int)(Math.random()*(max-min+1)+min);

			T item = items.get(index);
			items.remove(index);

			return item;
		}

		else 
		{
			throw new TestConfigurationException(String.format("Invalid items", items) );
		}
	}

}

