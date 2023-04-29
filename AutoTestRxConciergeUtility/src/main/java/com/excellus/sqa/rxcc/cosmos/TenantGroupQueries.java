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
import java.util.Random;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.TenantGetGroups;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 05/18/2022
 */
public class TenantGroupQueries  extends Queries
{
	private static final Logger logger = LoggerFactory.getLogger(TenantQueries.class);

	private volatile static Queue<TenantGetGroups> TENANTGETGROUPSEHP; 
	private volatile static Queue<TenantGetGroups> TENANTGETGROUPSEXE; 

	private volatile static List <String> TENANTGETGROUPSFORMULARYCODE;   

	private final static String QUERY_TYPE_GROUP = "SELECT * FROM c Where c.type = 'group'";
	private final static String QUERY_TENANT_GET_GROUPS_FORMULARY_CODE = "SELECT DISTINCT VALUE c.formularyCode FROM c";


	/**
	 * Query  Tenant Group where type = 'group' and added extension of whereClause for EHP Tenant
	 * 
	 * 
	 * @return list of {@link Tenant Group with added extension of whereClause}
	 */
	@SuppressWarnings("unchecked") public synchronized static List<TenantGetGroups> getGroupsEHPTenant(String whereClause) {

		if ( TENANTGETGROUPSEHP != null && TENANTGETGROUPSEHP.size() > 0 )
			return (List<TenantGetGroups>) TENANTGETGROUPSEHP;

		String query = String.format(QUERY_TYPE_GROUP);

		whereClause = StringUtils.isNotBlank(whereClause) ?
				RegExUtils.replaceFirst(whereClause, "where", "") : "";

		query += StringUtils.isNotBlank(whereClause) ? " and " + whereClause.trim() : "";

		List<TenantGetGroups> tenantGetGroups = executeQuery("LBSContainerTenant", query, TenantGetGroups.class, 100); 
		TENANTGETGROUPSEHP = new LinkedList<>(tenantGetGroups);

		return (List<TenantGetGroups>) TENANTGETGROUPSEHP;
	}

	/**
	 * Query  Tenant Group where type = 'group' and added extension of whereClause for EXE Tenant
	 * 
	 * 
	 * @return list of {@link Tenant Group with added extension of whereClause}
	 */
	@SuppressWarnings("unchecked") public synchronized static List<TenantGetGroups> getGroupsEXETenant(String whereClause) {

		if ( TENANTGETGROUPSEXE != null && TENANTGETGROUPSEXE.size() > 0 )
			return (List<TenantGetGroups>) TENANTGETGROUPSEXE;

		String query = String.format(QUERY_TYPE_GROUP);

		whereClause = StringUtils.isNotBlank(whereClause) ?
				RegExUtils.replaceFirst(whereClause, "where", "") : "";

		query += StringUtils.isNotBlank(whereClause) ? " and " + whereClause.trim() : "";

		List<TenantGetGroups> tenantGetGroups = executeQuery("LBSContainerTenant", query, TenantGetGroups.class, 100); 
		TENANTGETGROUPSEXE = new LinkedList<>(tenantGetGroups);

		return (List<TenantGetGroups>) TENANTGETGROUPSEXE;

	}

	/**
	 * Query Tenant Group where type = 'group'
	 * 
	 * @return list of {@link TenantGroup}
	 */
	@SuppressWarnings("unchecked")
	public synchronized static String getTenantGetGroupsFormularyCode(String adTenantId )
	{
		String formularyCode = null;

		if (TENANTGETGROUPSFORMULARYCODE == null || TENANTGETGROUPSFORMULARYCODE.size() == 0  )
		{
			tenantFormularyQuery(" where c.adTenantId = \'" + adTenantId + "\'");
		}

		// Grab random number between 0 to the size of the list minus 1
		Random ran = new Random();

		if  (TENANTGETGROUPSFORMULARYCODE.size() > 1)
		{
			int index = ran.nextInt(TENANTGETGROUPSFORMULARYCODE.size()-1 + 0) + 0;
			formularyCode = TENANTGETGROUPSFORMULARYCODE.get(index);
		}
		else
		{
			formularyCode = TENANTGETGROUPSFORMULARYCODE.get(0);
		}	

		TENANTGETGROUPSFORMULARYCODE.remove(formularyCode);

		return formularyCode;

	}

	/**
	 * Query  Tenant Group where type = 'group' and added extension of whereClause
	 * 
	 * 
	 * @return list of {@link Tenant Group with added extension of whereClause}
	 */
	@SuppressWarnings("unchecked") public synchronized  static void tenantFormularyQuery(String whereClause) {

		logger.info("Retrieving member from Cosmos DB");

		String query = String.format(QUERY_TENANT_GET_GROUPS_FORMULARY_CODE);
		query += StringUtils.isNotBlank(whereClause) ?  whereClause : "";

		TENANTGETGROUPSFORMULARYCODE = executeQuery("LBSContainerTenant", query, String.class);
	}
}
