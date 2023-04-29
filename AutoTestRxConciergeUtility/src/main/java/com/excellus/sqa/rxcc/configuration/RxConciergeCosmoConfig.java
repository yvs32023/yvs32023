/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.configuration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.azure.core.credential.TokenCredential;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.identity.AzureCliCredential;
import com.azure.identity.AzureCliCredentialBuilder;
import com.azure.identity.ChainedTokenCredentialBuilder;
import com.azure.identity.EnvironmentCredential;
import com.azure.identity.EnvironmentCredentialBuilder;
import com.azure.identity.InteractiveBrowserCredential;
import com.azure.identity.InteractiveBrowserCredentialBuilder;
import com.azure.identity.ManagedIdentityCredential;
import com.azure.identity.ManagedIdentityCredentialBuilder;
import com.azure.identity.VisualStudioCodeCredential;
import com.azure.identity.VisualStudioCodeCredentialBuilder;
import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.dto.Tenant;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for Cosmos DB
 * 
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/03/2022
 */
@Lazy(true)
@Configuration
@PropertySources({
	@PropertySource("classpath:properties/rxCC-${environment}.properties")
})
public class RxConciergeCosmoConfig 
{
	private static final Logger logger = LoggerFactory.getLogger(RxConciergeCosmoConfig.class);
	
	public static final String COSMOS_DATE_FORMAT	= "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'";
	public static final String COSMOS_TIMEZONE		= "GMT";

	static TokenCredential credential = null;
	
	static CosmosClient lbsCosmosClient;    // Lifetime Benefit Solutions (shared resources)
	
	static CosmosClient exeCosmosClient;    // Excellus Employees (EXE)
	
	static CosmosClient ehpCosmosClient;    // Excellus Health Plan (EHP)

	static CosmosClient loaCosmosClient;	// LBS Out of Area (LOA)

	static CosmosClient medCosmosClient;	// Medicare (MED)

	static Map<String, CosmosClient> openedCosmosClient = new HashMap<>();	// GC (11/0/22)

	/*
	 * -----------------------------------------------
	 *                  Properties
	 * -----------------------------------------------
	 */
	
	@Value("${cosmos.date.format}")
	private String cosmosDateFormat;
	
	@Value("${cosmos.timezone}")
	private String cosmosTimezone;
	
	@Value("${cosmos.lbs.endpoint}")
	private String lbsEndpoint;
	
	@Value("${cosmos.lbs.database}")
	private String lbsDatabase;
	
	@Value("${cosmos.lbs.container.lookup}")
	private String lbsContainerLookup;
	
	@Value("${cosmos.lbs.container.pharmacy}")
	private String lbsContainerPharmacy;

	@Value("${cosmos.lbs.container.provider}")
	private String lbsContainerProvider;
	
	@Value("${cosmos.lbs.container.tenant}")
	private String lbsContainerTenant;
	
	@Value("${cosmos.lbs.container.ndc}")
	private String lbsContainerNdc;
	
	@Value("${cosmos.lbs.container.interventionform}")
	private String lbsContainerInterventionForm;
	
	@Value("${cosmos.lbs.container.interventionrule}")
	private String lbsContainerInterventionRule;
	
	@Value("${cosmos.lbs.container.formulary}")
	private String lbsContainerFormulary;
	
	@Value("${cosmos.lbs.container.faxtemplate}")
	private String lbsContainerFaxTemplate;
	
	@Value("${cosmos.lbs.container.faxrequest}")
	private String lbsContainerFaxRequest;
			
	@Value("${cosmos.exe.endpoint}")
	private String exeEndpoint;
	
	@Value("${cosmos.exe.database}")
	private String exeDatabase;
	
	@Value("${cosmos.exe.container.interventionbatch}")
	private String exeContainerInterventionBatch;
	
	@Value("${cosmos.exe.container.member}")
	private String exeContainerMember;
	
	@Value("${cosmos.exe.container.user}")
	private String exeContainerUser;
	
	@Value("${cosmos.ehp.endpoint}")
	private String ehpEndpoint;
	
	@Value("${cosmos.ehp.database}")
	private String ehpDatabase;
	
	@Value("${cosmos.ehp.container.interventionbatch}")
	private String ehpContainerInterventionBatch;
	
	@Value("${cosmos.ehp.container.member}")
	private String ehpContainerMember;
	
	@Value("${cosmos.ehp.container.user}")
	private String ehpContainerUser;

	@Value("${cosmos.loa.endpoint}")
	private String loaEndpoint;

	@Value("${cosmos.loa.database}")
	private String loaDatabase;

	@Value("${cosmos.loa.container.interventionbatch}")
	private String loaContainerInterventionBatch;
	
	@Value("${cosmos.loa.container.member}")
	private String loaContainerMember;

	@Value("${cosmos.loa.container.user}")
	private String loaContainerUser;

	@Value("${cosmos.med.endpoint}")
	private String medEndpoint;

	@Value("${cosmos.med.database}")
	private String medDatabase;
	
	@Value("${cosmos.med.container.interventionbatch}")
	private String medContainerInterventionBatch;

	@Value("${cosmos.med.container.member}")
	private String medContainerMember;

	@Value("${cosmos.med.container.user}")
	private String medContainerUser;

	/*
	 * -----------------------------------------------
	 *                   Beans
	 * -----------------------------------------------
	 */

	/**
	 * Create CosmosClient for LBS
	 * @return CosmosClient that represent LBS
	 */
	@Bean(name = BeanNames.LBS_COSMOS_CLIENT)
	public CosmosClient getLBSCosmosClient()
	{
		if ( lbsCosmosClient == null )
		{
			setupCredential();

			lbsCosmosClient = new CosmosClientBuilder()
		                .credential(credential)
		                .endpoint(lbsEndpoint)
		                .gatewayMode()
		                .buildClient();

			openedCosmosClient.put(BeanNames.LBS_COSMOS_CLIENT, lbsCosmosClient);
		}
		
		 return lbsCosmosClient;
	}

	/**
	 * Create CosmosDatabase from LBS CosmosClient
	 * @return CosmosDatabase that represent LBS database
	 */
	@Bean(name = BeanNames.LBS_COSMOS_DB)
	public CosmosDatabase getLBSDatabase()
	{
		return getLBSCosmosClient().getDatabase(lbsDatabase);
	}

	/**
	 * Create CosmosContainer for Lookup from LBS CosmosDatabase
	 * @return CosmosContainer that represent LBS lookup container
	 */
	@Bean(name = BeanNames.LBS_CONTAINER_LOOKUP)
	public CosmosContainer getLBSContainerLookup()
	{
		return getLBSDatabase().getContainer(lbsContainerLookup);
	}

	/**
	 * Create CosmosContainer for Pharmacy from LBS CosmosDatabase
	 * @return CosmosContainer that represent LBS pharmacy container
	 */
	@Bean(name = BeanNames.LBS_CONTAINER_PHARMACY)
	public CosmosContainer getLBSContainerPharmacy()
	{
		return getLBSDatabase().getContainer(lbsContainerPharmacy);
	}

	/**
	 * Create CosmosContainer for Provider from LBS CosmosDatabase
	 * @return CosmosContainer that represent LBS provider container
	 */
	@Bean(name = BeanNames.LBS_CONTAINER_PROVIDER)
	public CosmosContainer getLBSContainerProvider()
	{
		return getLBSDatabase().getContainer(lbsContainerProvider);
	}

	/**
	 * Create CosmosContainer for Tenant from LBS CosmosDatabase
	 * @return CosmosContainer that represent LBS tenant container
	 */
	@Bean(name = BeanNames.LBS_CONTAINER_TENANT)
	public CosmosContainer getLBSContainerTenant()
	{
		return getLBSDatabase().getContainer(lbsContainerTenant);
	}
	
	@Bean(name = BeanNames.LBS_CONTAINER_NDC)
	public CosmosContainer getLBSContainerNDC()
	{
		return getLBSDatabase().getContainer(lbsContainerNdc);
	}
	
	@Bean(name = BeanNames.LBS_CONTAINER_INTERVENTION_FORM)
	public CosmosContainer getLBSContainerInterventionForm()
	{
		return getLBSDatabase().getContainer(lbsContainerInterventionForm);
	}
	
	@Bean(name = BeanNames.LBS_CONTAINER_INTERVENTION_RULE)
	public CosmosContainer getLBSContainerInterventionRule()
	{
		return getLBSDatabase().getContainer(lbsContainerInterventionRule);
	}
	
	@Bean(name = BeanNames.LBS_CONTAINER_FORMULARY)
	public CosmosContainer getLBSContainerFormulary()
	{
		return getLBSDatabase().getContainer(lbsContainerFormulary);
	}
	
	@Bean(name = BeanNames.LBS_CONTAINER_FAX_TEMPLATE)
	public CosmosContainer getLBSContainerFaxTemplate()
	{
		return getLBSDatabase().getContainer(lbsContainerFaxTemplate);
	}
	
	@Bean(name = BeanNames.LBS_CONTAINER_FAX_REQUEST)
	public CosmosContainer getLBSContainerFaxRequest()
	{
		return getLBSDatabase().getContainer(lbsContainerFaxRequest);
	}


	/**
	 * Create CosmosClient for EXE
	 * @return CosmosClient that represent EXE
	 */
	@Bean(name = BeanNames.EXE_COSMOS_CLIENT)
	public CosmosClient getEXECosmosClient()
	{
		if ( exeCosmosClient == null )
		{
			setupCredential();

			exeCosmosClient = new CosmosClientBuilder()
		                .credential(credential)
		                .endpoint(exeEndpoint)
		                .gatewayMode()
		                .buildClient();

			openedCosmosClient.put(BeanNames.EXE_COSMOS_CLIENT, exeCosmosClient);
		}
		
		 return exeCosmosClient;
	}

	/**
	 * Create CosmosDatabase from EXE CosmosClient
	 * @return CosmosDatabase that represent EXE database
	 */
	@Bean(name = BeanNames.EXE_COSMOS_DB)
	public CosmosDatabase getEXEDatabase()
	{
		return getEXECosmosClient().getDatabase(exeDatabase);
	}
	
	/**
	 * Create CosmosContainer for InterventionBatch from EXE CosmosDatabase
	 * @return CosmosContainer that represent EXE interventionbatch container
	 */
	@Bean(name = BeanNames.EXE_CONTAINER_INTERVENTIONBATCH)
	public CosmosContainer getEXEContainerInterventionBatch()
	{
		return getEXEDatabase().getContainer(exeContainerInterventionBatch);
	}

	/**
	 * Create CosmosContainer for Member from EXE CosmosDatabase
	 * @return CosmosContainer that represent EXE member container
	 */
	@Bean(name = BeanNames.EXE_CONTAINER_MEMBER)
	public CosmosContainer getEXEContainerMember()
	{
		return getEXEDatabase().getContainer(exeContainerMember);
	}

	/**
	 * Create CosmosContainer for User from EXE CosmosDatabase
	 * @return CosmosContainer that represent EXE user container
	 */
	@Bean(name = BeanNames.EXE_CONTAINER_USER)
	public CosmosContainer getEXEContainerUser()
	{
		return getEXEDatabase().getContainer(exeContainerUser);
	}

	/**
	 * Create CosmosClient for EHP
	 * @return CosmosClient that represent EHP
	 */
	@Bean(name = BeanNames.EHP_COSMOS_CLIENT)
	public CosmosClient getEHPCosmosClient()
	{
		if ( ehpCosmosClient == null )
		{
			setupCredential();

			ehpCosmosClient = new CosmosClientBuilder()
		                .credential(credential)
		                .endpoint(ehpEndpoint)
		                .gatewayMode()
		                .buildClient();

			openedCosmosClient.put(BeanNames.EHP_COSMOS_CLIENT, ehpCosmosClient);
		}
		
		 return ehpCosmosClient;
	}

	/**
	 * Create CosmosDatabase from EHP CosmosClient
	 * @return CosmosDatabase that represent EHP database
	 */
	@Bean(name = BeanNames.EHP_COSMOS_DB)
	public CosmosDatabase getEHPDatabase()
	{
		return getEHPCosmosClient().getDatabase(ehpDatabase);
	}
	
	/**
	 * Create CosmosContainer for InterventionBatch from EHP CosmosDatabase
	 * @return CosmosContainer that represent EHP interventionbatch container
	 */
	@Bean(name = BeanNames.EHP_CONTAINER_INTERVENTIONBATCH)
	public CosmosContainer getEHPContainerInterventionBatch()
	{
		return getEHPDatabase().getContainer(ehpContainerInterventionBatch);
	}

	/**
	 * Create CosmosContainer for Member from EHP CosmosDatabase
	 * @return CosmosContainer that represent EHP member container
	 */
	@Bean(name = BeanNames.EHP_CONTAINER_MEMBER)
	public CosmosContainer getEHPContainerMember()
	{
		return getEHPDatabase().getContainer(ehpContainerMember);
	}

	/**
	 * Create CosmosContainer for User from EHP CosmosDatabase
	 * @return CosmosContainer that represent EHP user container
	 */
	@Bean(name = BeanNames.EHP_CONTAINER_USER)
	public CosmosContainer getEHPContainerUser()
	{
		return getEHPDatabase().getContainer(ehpContainerUser);
	}

	/**
	 * Create CosmosClient for LOA
	 * @return CosmosClient that represent LOA
	 */
	@Bean(name = BeanNames.LOA_COSMOS_CLIENT)
	public CosmosClient getLOACosmosClient()
	{
		if ( loaCosmosClient == null )
		{
			setupCredential();

			loaCosmosClient = new CosmosClientBuilder()
									  .credential(credential)
									  .endpoint(loaEndpoint)
									  .gatewayMode()
									  .buildClient();

			openedCosmosClient.put(BeanNames.LOA_COSMOS_CLIENT, loaCosmosClient);
		}

		return loaCosmosClient;
	}

	/**
	 * Create CosmosDatabase from LOA CosmosClient
	 * @return CosmosDatabase that represent LOA database
	 */
	@Bean(name = BeanNames.LOA_COSMOS_DB)
	public CosmosDatabase getLOADatabase()
	{
		return getLOACosmosClient().getDatabase(loaDatabase);
	}
	
	/**
	 * Create CosmosContainer for InterventionBatch from LOA CosmosDatabase
	 * @return CosmosContainer that represent LOA interventionbatch container
	 */
	@Bean(name = BeanNames.LOA_CONTAINER_INTERVENTIONBATCH)
	public CosmosContainer getLOAContainerInterventionBatch()
	{
		return getLOADatabase().getContainer(loaContainerInterventionBatch);
	}

	/**
	 * Create CosmosContainer for Member from LOA CosmosDatabase
	 * @return CosmosContainer that represent LOA member container
	 */
	@Bean(name = BeanNames.LOA_CONTAINER_MEMBER)
	public CosmosContainer getLOAContainerMember()
	{
		return getLOADatabase().getContainer(loaContainerMember);
	}

	/**
	 * Create CosmosContainer for User from LOA CosmosDatabase
	 * @return CosmosContainer that represent LOA user container
	 */
	@Bean(name = BeanNames.LOA_CONTAINER_USER)
	public CosmosContainer getLOAContainerUser()
	{
		return getLOADatabase().getContainer(loaContainerUser);
	}

	/**
	 * Create CosmosClient for MED
	 * @return CosmosClient that represent MED
	 */
	@Bean(name = BeanNames.MED_COSMOS_CLIENT)
	public CosmosClient getMEDCosmosClient()
	{
		if ( medCosmosClient == null )
		{
			setupCredential();

			medCosmosClient = new CosmosClientBuilder()
									  .credential(credential)
									  .endpoint(medEndpoint)
									  .gatewayMode()
									  .buildClient();

			openedCosmosClient.put(BeanNames.MED_COSMOS_CLIENT, medCosmosClient);
		}

		return medCosmosClient;
	}

	/**
	 * Create CosmosDatabase from MED CosmosClient
	 * @return CosmosDatabase that represent MED database
	 */
	@Bean(name = BeanNames.MED_COSMOS_DB)
	public CosmosDatabase getMEDDatabase()
	{
		return getMEDCosmosClient().getDatabase(medDatabase);
	}

	/**
	 * Create CosmosContainer for InterventionBatch from MED CosmosDatabase
	 * @return CosmosContainer that represent MED interventionbatch container
	 */
	@Bean(name = BeanNames.MED_CONTAINER_INTERVENTIONBATCH)
	public CosmosContainer getMEDContainerInterventionBatch()
	{
		return getMEDDatabase().getContainer(medContainerInterventionBatch);
	}
	
	/**
	 * Create CosmosContainer for Member from MED CosmosDatabase
	 * @return CosmosContainer that represent MED member container
	 */
	@Bean(name = BeanNames.MED_CONTAINER_MEMBER)
	public CosmosContainer getMEDContainerMember()
	{
		return getMEDDatabase().getContainer(medContainerMember);
	}

	/**
	 * Create CosmosContainer for User from MED CosmosDatabase
	 * @return CosmosContainer that represent MED user container
	 */
	@Bean(name = BeanNames.MED_CONTAINER_USER)
	public CosmosContainer getMEDContainerUser()
	{
		return getMEDDatabase().getContainer(medContainerUser);
	}

	/*
	 * Helper method
	 */
	
	/**
	 * Setup the Azure Cloud credential
	 */
	private void setupCredential()
	{
		if ( credential == null ) {

			// GC (06/13/22) use these system properties to determine if running in Azure pipeline
			final String userDir = System.getProperty("user.dir");
			final String userHome = System.getProperty("user.home");

			logger.info("User dir: " + userDir);
			logger.info("User home: " + userHome);

			/*
			 * GC (02/25/22) Updated to switch between DefaultAzureCredential or ChainedTokenCredential,
			 *               base on where the test is executed
			 */

			if ( (StringUtils.containsIgnoreCase(userDir, "Agents") ||
					StringUtils.containsIgnoreCase(userDir, "pool")) &&
					StringUtils.containsIgnoreCase(userHome, "ServiceProfiles") )
			{
				/*
				 * Use this if running test in Azure pipeline
				 */
				logger.info("Estabilishing TokenCredential in Azure pipeline");
				
				/*
				 * GC (03/28/2023) for some reason, pipeline started to fail because it tried to use Managed Identity which isn't setup.
				 * It was throwing an error 
				 * 	  ManagedIdentityCredential authentication failed. Error Details: C:\ProgramData\AzureConnectedMachineAgent\Tokens\e9fb1dd2-0eb1-4383-ac15-581b0cc7fd04.key]
				 * 
				 * As a solution, we are changing the chained credential builder to start with Azure CLI first followed by environment and managed identity.
				 */
				// credential = new DefaultAzureCredentialBuilder().build();
				
				EnvironmentCredential environmentCredential = new EnvironmentCredentialBuilder().build();
				ManagedIdentityCredential managedIdentityCredential = new ManagedIdentityCredentialBuilder().build();
				AzureCliCredential azureCliCredential = new AzureCliCredentialBuilder().build();
			
				credential = new ChainedTokenCredentialBuilder()
						.addLast(azureCliCredential)
						.addLast(environmentCredential)
						.addLast(managedIdentityCredential)
						.build();
				
				if ( credential == null )
				{
					logger.error("TokenCredential is null!");
					throw new TestConfigurationException("Unable to setup TokenCredential in Azure pipeline");
				}
				
				logger.info("Successfully created TokenCredential in Azure pipeline!");
			}
			else
			{
				logger.info("Estabilishing TokenCredential in NON-Azure pipeline");

				// Use this if NOT running in Azure pipeline
				EnvironmentCredential environmentCredential = new EnvironmentCredentialBuilder().build();
				ManagedIdentityCredential managedIdentityCredential = new ManagedIdentityCredentialBuilder().build();
				AzureCliCredential azureCliCredential = new AzureCliCredentialBuilder().build();
				VisualStudioCodeCredential visualStudioCodeCredential = new VisualStudioCodeCredentialBuilder().build();
				InteractiveBrowserCredential interactiveBrowserCredential = new InteractiveBrowserCredentialBuilder().build();

				credential = new ChainedTokenCredentialBuilder()
						.addLast(environmentCredential)
						.addLast(managedIdentityCredential)
						.addLast(azureCliCredential)
						.addLast(visualStudioCodeCredential)
						.addLast(interactiveBrowserCredential)
						.build();
			}
		}
	}
	
	/**
	 * Retrieve token credential
	 * 
	 * @return {@link TokenCredential}
	 */
	public static TokenCredential getTokenCredential()
	{
		return credential;
	}
	
	/**
	 * Retrieve the tenant container bean name
	 * 
	 * @author gcosmian
	 * @since 03/21/22
	 * @param subscriptionName  of the tenant, either exe or ehp
	 * @return bean name corresponding to the subscription id
	 */
	public static String getMemberContainerBeanName(String subscriptionName)
	{
		if ( StringUtils.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName(), subscriptionName) )
		{
			return BeanNames.EXE_CONTAINER_MEMBER;
		}
		else if ( StringUtils.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName(), subscriptionName) )
		{
			return BeanNames.EHP_CONTAINER_MEMBER;
		}
		else if ( StringUtils.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName(), subscriptionName) )
		{
			return BeanNames.LOA_CONTAINER_MEMBER;
		}
		else if ( StringUtils.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName(), subscriptionName) )
		{
			return BeanNames.MED_CONTAINER_MEMBER;
		}
		else
		{
			throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe or ehp", subscriptionName) );
		}
	}
	
	/**
	 * Retrieve the user container bean name
	 * 
	 * @author msharma
	 * @since 10/13/22
	 * @param subscriptionName  of the user, either exe or ehp
	 * @return bean name corresponding to the subscription id
	 */
	public static String getUserContainerBeanName(String subscriptionName)
	{
		if ( StringUtils.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName(), subscriptionName) )
		{
			return BeanNames.EXE_CONTAINER_USER;
		}
		else if ( StringUtils.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName(), subscriptionName) )
		{
			return BeanNames.EHP_CONTAINER_USER;
		}
		else if ( StringUtils.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName(), subscriptionName) )
		{
			return BeanNames.LOA_CONTAINER_USER;
		}
		else if ( StringUtils.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName(), subscriptionName) )
		{
			return BeanNames.MED_CONTAINER_USER;
		}
		else
		{
			throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe or ehp", subscriptionName) );
		}
	}
	
	/**
	 * Retrieve the interventionBath container bean name
	 * 
	 * @author msharma
	 * @since 03/03/23
	 * @param subscriptionName  of the user, either exe, ehp, loa or med
	 * @return bean name corresponding to the subscription id
	 */
	public static String getInterventionBatchContainerBeanName(String subscriptionName)
	{
		if ( StringUtils.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName(), subscriptionName) )
		{
			return BeanNames.EXE_CONTAINER_INTERVENTIONBATCH;
		}
		else if ( StringUtils.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName(), subscriptionName) )
		{
			return BeanNames.EHP_CONTAINER_INTERVENTIONBATCH;
		}
		else if ( StringUtils.equalsIgnoreCase(Tenant.Type.LOA.getSubscriptionName(), subscriptionName) )
		{
			return BeanNames.LOA_CONTAINER_INTERVENTIONBATCH;
		}
		else if ( StringUtils.equalsIgnoreCase(Tenant.Type.MED.getSubscriptionName(), subscriptionName) )
		{
			return BeanNames.MED_CONTAINER_INTERVENTIONBATCH;
		}
		else
		{
			throw new TestConfigurationException(String.format("Invalid tenant subscription id [%s]. Use either exe,ehp,loa or med", subscriptionName) );
		}
	}


	/**
	 * Retrieve all Cosmos clients that were opened
	 * @return list of {@link CosmosClient}
	 */
	public static Map<String, CosmosClient> getOpenedCosmosClient()
	{
		return openedCosmosClient;
	}
}

