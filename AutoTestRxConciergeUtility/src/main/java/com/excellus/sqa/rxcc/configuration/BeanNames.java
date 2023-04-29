/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.configuration;

/**
 * Static names of beans
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 09/06/2022
 */
public class BeanNames{

	// URL
	public final static String API_URL	= "apiUrl";


	// Web configuration
	public final static String CHROME_WEB_APP	= "chromeWebApp";
	public final static String EDGE_WEB_APP		= "edgeWebApp";


	// Web pages

	public final static String LOGIN_PAGE			= "loginPage";
	public final static String HOME_PAGE			= "homePage";
	public final static String TENANT_PAGE			= "tenantPage";
	public final static String MEMBER_PAGE			= "memberPage";
	public final static String PROVIDER_PAGE		= "providerPage";
	public final static String INTERVENTION_PAGE	= "interventionPage";
	public final static String PHARMACIES_PAGE		= "pharmaciesPage";


	// Cosmos

	public final static String LBS_COSMOS_CLIENT				= "LBSCosmosClient";
	public final static String LBS_COSMOS_DB					= "LBSCosmosDB";
	public static final String LBS_CONTAINER_LOOKUP				= "LBSContainerLookup";
	public static final String LBS_CONTAINER_PHARMACY			= "LBSContainerPharmacy";
	public static final String LBS_CONTAINER_PROVIDER			= "LBSContainerProvider";
	public static final String LBS_CONTAINER_TENANT				= "LBSContainerTenant";
	public static final String LBS_CONTAINER_NDC				= "LBSContainerNdc";
	public static final String LBS_CONTAINER_INTERVENTION_FORM	= "LBSContainerInterventionForm";
	public static final String LBS_CONTAINER_INTERVENTION_RULE	= "LBSContainerInterventionRule";
	public static final String LBS_CONTAINER_FORMULARY			= "LBSContainerFormulary";
	public static final String LBS_CONTAINER_FAX_TEMPLATE		= "LBSContainerFaxTemplate";
	public static final String LBS_CONTAINER_FAX_REQUEST		= "LBSContainerFaxRequest";

	public final static String EHP_COSMOS_CLIENT				= "EHPCosmosClient";
	public final static String EHP_COSMOS_DB					= "EHPCosmosDB";
	public static final String EHP_CONTAINER_MEMBER				= "EHPContainerMember";
	public static final String EHP_CONTAINER_USER				= "EHPContainerUser";
	public static final String EHP_CONTAINER_INTERVENTIONBATCH	= "EHPContainerInterventionBatch";

	public final static String EXE_COSMOS_CLIENT				= "EXECosmosClient";
	public final static String EXE_COSMOS_DB					= "EXECosmosDB";
	public static final String EXE_CONTAINER_MEMBER				= "EXEContainerMember";
	public static final String EXE_CONTAINER_USER				= "EXEContainerUser";
	public static final String EXE_CONTAINER_INTERVENTIONBATCH	= "EXEContainerInterventionBatch";

	public final static String LOA_COSMOS_CLIENT				= "LOACosmosClient";
	public final static String LOA_COSMOS_DB					= "LOACosmosDB";
	public static final String LOA_CONTAINER_MEMBER				= "LOAContainerMember";
	public static final String LOA_CONTAINER_USER				= "LOAContainerUser";
	public static final String LOA_CONTAINER_INTERVENTIONBATCH	= "LOAContainerInterventionBatch";

	public final static String MED_COSMOS_CLIENT				= "MEDCosmosClient";
	public final static String MED_COSMOS_DB					= "MEDCosmosDB";
	public static final String MED_CONTAINER_MEMBER				= "MEDContainerMember";
	public static final String MED_CONTAINER_USER				= "MEDContainerUser";
	public static final String MED_CONTAINER_INTERVENTIONBATCH	= "MEDContainerInterventionBatch";


	// Credentials

	public final static String SSO				= "SSO";

	public final static String RXCC_FULL_MULTI		= "RXCC_FULL_MULTI";
	public final static String RXCC_READ_MULTI		= "RXCC_READ_MULTI";
	public final static String RXCC_OPS_MULTI		= "RXCC_OPS_MULTI";
	public final static String RXCC_REPORT_MULTI	= "RXCC_REPORT_MULTI";

	public final static String RXCC_FULL_SINGLE		= "RXCC_FULL_SINGLE";
	public final static String RXCC_READ_SINGLE		= "RXCC_READ_SINGLE";
	public final static String RXCC_OPS_SINGLE		= "RXCC_OPS_SINGLE";
	public final static String RXCC_REPORT_SINGLE	= "RXCC_REPORT_SINGLE";

	public final static String RXCC_FULL_LOA	= "RXCC_FULL_LOA";
	public final static String RXCC_READ_LOA	= "RXCC_READ_LOA";
	public final static String RXCC_OPS_LOA		= "RXCC_OPS_LOA";
	public final static String RXCC_REPORT_LOA	= "RXCC_REPORT_LOA";

	public final static String RXCC_FULL_MED	= "RXCC_FULL_MED";
	public final static String RXCC_READ_MED	= "RXCC_READ_MED";
	public final static String RXCC_OPS_MED		= "RXCC_OPS_MED";
	public final static String RXCC_REPORT_MED	= "RXCC_REPORT_MED";
}

