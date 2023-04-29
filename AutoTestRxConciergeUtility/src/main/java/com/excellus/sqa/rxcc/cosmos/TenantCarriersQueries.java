/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.TenantCarrier;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 09/20/2022
 */
public class TenantCarriersQueries extends Queries
{
	private static final Logger logger = LoggerFactory.getLogger(TenantCarriersQueries.class);

	private volatile static Map<String, List<TenantCarrier>> TENANT_CARRIERS = new HashMap<>();

//	private volatile static List<TenantCarrier> CARRIERS;

//	private volatile static List<TenantCarrier> CARRIERS_EHP; // Cache the TenantCarrier, so we don't have to run multiple queries to Cosmos
//	private volatile static List<TenantCarrier> CARRIERS_EXE; // Cache the TenantCarrier, so we don't have to run multiple queries to Cosmos

	private volatile static String QUERY_BY_TENANT = "SELECT * FROM c WHERE c.type = 'carrier' AND c.adTenantId='%s'";

	/**
	 * Retrieve list of carriers 
	 * @return list of carriers
	 */
	public synchronized static List<TenantCarrier> getCarrier()
	{
		List<TenantCarrier> list = new ArrayList<>();

		for (Tenant tenant : TenantQueries.getTenants() )
		{
			if ( TENANT_CARRIERS.containsKey(tenant.getSubscriptionName()) )
			{
				list.addAll(TENANT_CARRIERS.get(tenant.getSubscriptionName()));
			}
			else
			{
				List<TenantCarrier> carries = executeQuery(BeanNames.LBS_CONTAINER_TENANT,
												String.format(QUERY_BY_TENANT, TenantQueries.getAdTenantId(tenant.getSubscriptionName())),
												TenantCarrier.class, 100);

				TENANT_CARRIERS.put(tenant.getSubscriptionName(), carries);
				list.addAll(carries);
			}
		}

		return list;
	}

	public  synchronized static List<TenantCarrier> getCarrierByTenantSubName(String subscriptionName)
	{
		if ( TENANT_CARRIERS.containsKey(subscriptionName) )
		{
			return TENANT_CARRIERS.get(subscriptionName);
		}

		List<TenantCarrier> carries = executeQuery(BeanNames.LBS_CONTAINER_TENANT,
										String.format(QUERY_BY_TENANT, TenantQueries.getAdTenantId(subscriptionName)),
										TenantCarrier.class, 100);

		TENANT_CARRIERS.put(subscriptionName, carries);
		return carries;
	}



	/**
	 * Retrieve list of EHP carriers 
	 * @return list of carriers
	 */
	public synchronized static List<TenantCarrier> getCarrierEHP()
	{
		return getCarrierByTenantSubName(Tenant.Type.EHP.getSubscriptionName());
	}

	/**
	 * Retrieve list of EXE carriers 
	 * @return list of carriers
	 */
	public synchronized static List<TenantCarrier> getCarrierEXE()
	{
		return getCarrierByTenantSubName(Tenant.Type.EXE.getSubscriptionName());
	}

	/**
	 * Retrieve random carrier
	 * @return {@link TenantCarrier}
	 */
	public synchronized static TenantCarrier getRandomCarrier()
	{
		logger.info("Get random carrier");
		return getRandomItem(getCarrier());
	}

	/**
	 * Retrieve random carrier base on the tenant subscription name
	 * @param subscriptionName tenant's ubscription name
	 * @return random {@link TenantCarrier}
	 */
	public synchronized static TenantCarrier getRandomCarrierByTenantSubName(String subscriptionName)
	{
		logger.info("Get random carrier with tenant subscription name " + subscriptionName);
		return getRandomItem( getCarrierByTenantSubName(subscriptionName) );
	}

	/**
	 * Retrieve random EHP carrier
	 * @return {@link TenantCarrier}
	 */
	public synchronized static TenantCarrier getRandomCarrierEHP()
	{
		logger.info("Get random EHP carrier");
		return getRandomItem(getCarrierEHP());
	}

	/**
	 * Retrieve random EXE carrier
	 * @return {@link TenantCarrier}
	 */
	public synchronized static TenantCarrier getRandomCarrierEXE()
	{
		logger.info("Get random EXE carrier");
		return getRandomItem(getCarrierEXE());
	}


	/**
	 * Retrieve  carrier based on provided parameters
	 * @param adTenantId
	 * @param carrierId
	 * @param benefitHierarchyId
	 *
	 * @author msharma 
	 * @since 09/24/2022
	 * @return {@link TenantCarrier}
	 */
	public synchronized static List<TenantCarrier> getCarriers(String adTenantId, String carrierId, String benefitHierarchyId )
	{
		if ((StringUtils.isNotBlank(carrierId)) && ((StringUtils.isNotBlank(benefitHierarchyId)) && ((StringUtils.isNotBlank(adTenantId)))))	
		{
			String query = String.format(Queries.QUERY_BY_TYPE, "carrier") + 
					String.format(" and c.adTenantId = \"%s\" and c.carrierId = \"%s\" and c.benefitHierarchyId = \"%s\"", adTenantId, carrierId, benefitHierarchyId);

			logger.info("Retrieving tenant carrier from Cosmos DB");

			List<TenantCarrier> tenantCarriers = executeQuery("LBSContainerTenant", query, TenantCarrier.class, 100);

			return tenantCarriers;
		}

		else if ((StringUtils.isNotBlank(benefitHierarchyId))  && ((StringUtils.isNotBlank(adTenantId)) && ((StringUtils.isBlank(carrierId))))) 
		{
			String query = String.format(Queries.QUERY_BY_TYPE, "carrier") + 
					String.format(" and c.adTenantId = \"%s\"and  c.benefitHierarchyId = \"%s\"", adTenantId, benefitHierarchyId);

			logger.info("Retrieving tenant carrier from Cosmos DB");

			List<TenantCarrier> tenantCarriers = executeQuery("LBSContainerTenant", query, TenantCarrier.class, 100);

			return tenantCarriers;
		}

		else if ((StringUtils.isNotBlank(carrierId))&& (StringUtils.isBlank(benefitHierarchyId)) && ((StringUtils.isNotBlank(adTenantId))))
		{
			String query = String.format(Queries.QUERY_BY_TYPE, "carrier") + 
					String.format(" and c.adTenantId = \"%s\" and c.carrierId = \"%s\"", adTenantId, carrierId);

			logger.info("Retrieving tenant carrier from Cosmos DB");

			List<TenantCarrier> tenantCarriers = executeQuery("LBSContainerTenant", query, TenantCarrier.class, 100);

			return tenantCarriers;
		}

		else if ((StringUtils.isBlank(carrierId)) && (StringUtils.isBlank(benefitHierarchyId)) && ((StringUtils.isNotBlank(adTenantId))))
		{
			String query = String.format(Queries.QUERY_BY_TYPE, "carrier") + 
					String.format(" and c.adTenantId = \"%s\"", adTenantId);

			logger.info("Retrieving tenant carrier from Cosmos DB");

			List<TenantCarrier> tenantCarriers = executeQuery("LBSContainerTenant", query, TenantCarrier.class, 100);

			return tenantCarriers;

		}

		else 
		{
			throw new TestConfigurationException(String.format("Invalid Tenant Carrier", adTenantId) );
		}

	}
}
