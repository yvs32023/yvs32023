/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.Tenant;

/**
 * Cosmos DB queries to LBS tenant container
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/15/2022
 */
public class TenantQueries extends Queries
{

	private static final Logger logger = LoggerFactory.getLogger(TenantQueries.class);

	private volatile static List<Tenant> TENANTS;   // Cache the tenants, so we don't have to run multiple queries to Cosmos
	
	private final static String IGNORE_TENANT = "'exp'";  // GC (10/25/22) removed loa and med

	/**
	 * Query all tenants
	 * @return list of {@link Tenant}
	 */
	public synchronized static List<Tenant> getTenants() {

		if ( TENANTS != null && TENANTS.size() > 0)
			return TENANTS;

		// Execute Cosmos DB query
		logger.info("Retrieving tenants from Cosmos DB");
		String query = String.format(QUERY_BY_TYPE,"tenant") + " and c.subscriptionName not in (" + IGNORE_TENANT + ")";
		TENANTS = executeQuery("LBSContainerTenant", query, Tenant.class, 100);

		return TENANTS;

	}
	

	/**
	 * Retrieve a tenant provided adTenantId
	 * @param adTenantId of the tenant
	 * @return {@link Tenant} that correspond to the adTenantId. If not found then it returns null.
	 */
	public synchronized static Tenant getTenant(String adTenantId) {

		if ( TENANTS == null || TENANTS.size() == 0)
			getTenants();

		for ( Tenant tenant : TENANTS )
		{
			if ( tenant.getAdTenantId().equals(adTenantId) )
				return tenant;
		}

		return null;
	}

	/**
	 * Retrieve all tenant's adTenantId
	 * @return list of adTenantId of tenants
	 */
	public synchronized static List<String> getAdTenantIds() {

		if ( TENANTS == null || TENANTS.size() == 0)
			getTenants();

		List<String> adTenantIds = new ArrayList<String>();
		for ( Tenant tenant : TENANTS )
		{
			adTenantIds.add(tenant.getAdTenantId());
		}

		return adTenantIds;
	}

	/**
	 * Retrieve the adTenantId provided the subscriptionName (i.e. EXE, EHP, etc)
	 *
	 * @since 03/01/22
	 * @author gcosmiano
	 * @param subscriptionName of the tenant
	 * @return adTenantId that correspond to subscriptionName
	 */
	public synchronized static String getAdTenantId(String subscriptionName)
	{
		if ( TENANTS == null || TENANTS.size() == 0 ) {
			getTenants();
		}

		for ( Tenant tenant : TENANTS )
		{
			if (StringUtils.equalsIgnoreCase(tenant.getSubscriptionName(), subscriptionName) ) {
				return tenant.getAdTenantId();
			}
		}

		return null;
	}
	
	/**
	 * Retrieve a tenant provided tenantId
	 * @author msharma 
	 * @since 03/24/2022
	 * @param tenantId of the tenant
	 * @return {@link Tenant} that correspond to the tenantId. If not found then it returns null.
	 */
	public synchronized static Tenant getTenantByTenantId(int tenantId) {

		if ( TENANTS == null || TENANTS.size() == 0)
			getTenants();

		for ( Tenant tenant : TENANTS )
		{
			if ( tenant.getTenantId() == (tenantId) )
				return tenant;
		}

		return null;
	}
	
	public static List<Tenant> getTenantsByAccountName(String accountName)
	{
		if ( StringUtils.endsWithIgnoreCase(accountName, "_single") )
		{
			return getTenants().stream()
					.filter(tenant -> StringUtils.equalsIgnoreCase(tenant.getSubscriptionName(), Tenant.Type.EHP.getSubscriptionName()))
					.collect(Collectors.toList());
		}
		else if ( StringUtils.endsWithIgnoreCase(accountName, "_loa") )
		{
			return getTenants().stream()
						   .filter(tenant -> StringUtils.equalsIgnoreCase(tenant.getSubscriptionName(), Tenant.Type.LOA.getSubscriptionName()))
						   .collect(Collectors.toList());
		}
		else if ( StringUtils.endsWithIgnoreCase(accountName, "_med") )
		{
			return getTenants().stream()
						   .filter(tenant -> StringUtils.equalsIgnoreCase(tenant.getSubscriptionName(), Tenant.Type.MED.getSubscriptionName()))
						   .collect(Collectors.toList());
		}

		// Assuming the account has access to all tenants
		return getTenants();
	}

	/**
	 * Retrieve the tenant provided any type of id
	 * @param id can be member id, intervention id, correspondent id, etc
	 * @return {@link Tenant}
	 * @since 01/03/23
	 */
	public static Tenant getTenantById(String id)
	{
		if ( TENANTS == null || TENANTS.size() == 0)
			getTenants();

		return TENANTS.stream()
				.filter(tenant -> StringUtils.containsIgnoreCase(id, tenant.getAdTenantId()))
				.findFirst()
				.orElse(null);
	}
}
