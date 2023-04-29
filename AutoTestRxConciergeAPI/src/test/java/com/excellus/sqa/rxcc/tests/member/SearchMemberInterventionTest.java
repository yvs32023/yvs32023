/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.member;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.cosmos.implementation.apachecommons.lang.StringUtils;
import com.excellus.sqa.restapi.RequestLoggingFilter;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.MemberIntervention.TargetInterventionQueueType;
import com.excellus.sqa.rxcc.dto.MemberInterventionSearchResponse;
import com.excellus.sqa.rxcc.dto.SearchMemberIntervention;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * 
 *  Body used :
 *  
 * {
 *   "query": "6",
 *   "targetInterventionQueueType": "RPH",
 *   "sortField": "memberCost",
 *   "sortDirection": "asc",
 *   "size": "200"
 * }
 *    
 * @author Manish Sharma (msharma)
 * @since 10/13/2022
 */
@Tag("ALL")
@Tag("MEMBER")
@Tag("INTERVENTION")
@Tag("MEMBERINTERVENTION")
@Tag("INTERVENTIONSEARCH")
@Tag("SEARCH")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostMemberInterventionSearch")
public class SearchMemberInterventionTest extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(SearchMemberInterventionTest.class);
	private String subscriptionId; 

	private final String GENERIC_BODY;

	public SearchMemberInterventionTest()
	{
		JsonObject requestBody = new JsonObject();
		requestBody.addProperty("query", "Blue");
		requestBody.addProperty("targetInterventionQueueType", "RPH");

		GENERIC_BODY = requestBody.toString();
	}


	@TestFactory
	@DisplayName("34471/126078: Member PostInterventionSearch Happy Path Search Query by Member Name,  No Sort, No Direction & No size")
	@Order(1)
	public List<DynamicNode> withBodyOnlySearcherm() throws JsonProcessingException
	{

		List<DynamicNode> test = new ArrayList<>();
		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					List<DynamicNode> testType = new ArrayList<>();

					//grabs ranmdom memberId that has defined targetInterventionQueueType enum value
					String memberId = "";
					memberId = MemberInterventionQueries.getRandomMemberWithInterventionBasedOnTargetInterventionQueueType(subscriptionId,type.toString(), null);

					//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
					if (StringUtils.isEmpty(memberId ))
					{
						test.add( dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(),  
								Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
						continue;
					}

					//Query Cosmos for fetching random list of member interventions for provided memberId
					List<MemberIntervention> expectedMemberInterventions = MemberInterventionQueries.getInterventionByType(subscriptionId, memberId, type.getEnumValue());

					//fields for api body
					String searchTerm = expectedMemberInterventions.get(0).getMemberName().replace("-", "");
					String targetInterventionQueueType = type.getEnumValue();
					String subscriptionName = subscriptionId;

					// Create API body
					JsonObject body = createApiBody(searchTerm, targetInterventionQueueType, null, null,  0);

					List<DynamicNode> testAllResults = new ArrayList<>();

					// Call API
					ApiPostStep apiPostStep = searchMemberIntervention(subscriptionName, body, 200);
					MemberInterventionSearchResponse searchResult = apiPostStep.convertToJsonDTO(MemberInterventionSearchResponse.class);
					if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
					{
						return apiPostStep.getTestResults();
					}

					//collecting id of member intervention from api response in the stream  (limiting to 50)
					List<String> memberIdListFromApiResponse = searchResult.getInterventions().stream().map(list -> list.getId()).limit(50)
							.collect(Collectors.toList());


					//fetching list of member intervention from cosmos db by collecting list of id from api response 
					List<MemberIntervention> listOfMemberInterventionsFromCosmos = MemberInterventionQueries.getInterventionById(subscriptionName,memberIdListFromApiResponse.toArray(new String[0]));

					//mapping listOfMemberInterventionsFromCosmos into mappedSearchMemberIntervention
					List<SearchMemberIntervention> mappedSearchMemberIntervention = new ArrayList<>();

					for ( MemberIntervention memberIntervention : listOfMemberInterventionsFromCosmos ) 
					{
						SearchMemberIntervention mappedInterventionSearch = createMappedInterventionSearch( memberIntervention ); 
						mappedSearchMemberIntervention.add(mappedInterventionSearch);
					}

					//validation
					testAllResults = validateSearchResult(searchResult, searchTerm, targetInterventionQueueType, mappedSearchMemberIntervention);
					testAllResults.add(dynamicContainer("API Response", apiPostStep.getTestResults()));
					testType.add(dynamicContainer(" Validation : Subscription Name / Target Intervention Queue Type [" + subscriptionName.toUpperCase() + " / " + type.getEnumValue() + "]" ,testAllResults));
					test.add(dynamicContainer(subscriptionName.toUpperCase() + " / " + type.getEnumValue() , testType));
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}


	@TestFactory
	@DisplayName("126079 : Member PostInterventionSearch Happy Path Search Query by Provider Name, No Sort, No Direction & No size")
	@Order(2)
	public List<DynamicNode> withBodyOnlyOriginalProviderName() throws JsonProcessingException
	{

		List<DynamicNode> test = new ArrayList<>();
		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					List<DynamicNode> testType = new ArrayList<>();

					//grabs ranmdom memberId that has defined targetInterventionQueueType enum value
					String memberId = "";
					memberId = MemberInterventionQueries.getRandomMemberWithInterventionBasedOnTargetInterventionQueueType(subscriptionId,type.toString(),"false");

					//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
					if (StringUtils.isEmpty(memberId ))
					{
						test.add( dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(),  
								Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
						continue;
					}

					//Query Cosmos for fetching random list of member interventions for provided memberId
					List<MemberIntervention> expectedMemberInterventions = MemberInterventionQueries.getInterventionByType(subscriptionId, memberId, type.getEnumValue());

					//fields for api body
					String searchTerm = expectedMemberInterventions.get(0).getOriginalProviderName();
					String targetInterventionQueueType = type.getEnumValue();
					String subscriptionName = subscriptionId;

					// Create API body
					JsonObject body = createApiBody(searchTerm, targetInterventionQueueType, null, null,  0);

					List<DynamicNode> testAllResults = new ArrayList<>();

					// Call API
					ApiPostStep apiPostStep = searchMemberIntervention(subscriptionName, body, 200);
					MemberInterventionSearchResponse searchResult = apiPostStep.convertToJsonDTO(MemberInterventionSearchResponse.class);
					if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
					{
						return apiPostStep.getTestResults();
					}

					//collecting id of member intervention from api response in the stream  (limiting to 50)
					List<String> memberIdListFromApiResponse = searchResult.getInterventions().stream().map(list -> list.getId()).limit(50)
							.collect(Collectors.toList());


					//fetching list of member intervention from cosmos db by collecting list of id from api response 
					List<MemberIntervention> listOfMemberInterventionsFromCosmos = MemberInterventionQueries.getInterventionById(subscriptionName,memberIdListFromApiResponse.toArray(new String[0]));

					//mapping listOfMemberInterventionsFromCosmos into mappedSearchMemberIntervention
					List<SearchMemberIntervention> mappedSearchMemberIntervention = new ArrayList<>();

					for ( MemberIntervention memberIntervention : listOfMemberInterventionsFromCosmos ) 
					{
						SearchMemberIntervention mappedInterventionSearch = createMappedInterventionSearch( memberIntervention ); 
						mappedSearchMemberIntervention.add(mappedInterventionSearch);
					}

					//validation
					testAllResults = validateSearchResult(searchResult, searchTerm, targetInterventionQueueType, mappedSearchMemberIntervention);
					testAllResults.add(dynamicContainer("API Response", apiPostStep.getTestResults()));
					testType.add(dynamicContainer(" Validation : Subscription Name / Target Intervention Queue Type [" + subscriptionName.toUpperCase() + " / " + type.getEnumValue() + "]" ,testAllResults));
					test.add(dynamicContainer(subscriptionName.toUpperCase() + " / " + type.getEnumValue() , testType));
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("126080 : Member PostInterventionSearch Happy Path Search Query by lastUpdatedBy, SortField  providerName, Sort Direction desc")
	@Order(3)
	public List<DynamicNode> withBodyHappyPath() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();
		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					List<DynamicNode> testType = new ArrayList<>();

					//grabs ranmdom memberId that has defined targetInterventionQueueType enum value
					String memberId = "";
					memberId = MemberInterventionQueries.getRandomMemberWithInterventionBasedOnTargetInterventionQueueType(subscriptionId,type.toString(), null);

					//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
					if (StringUtils.isEmpty(memberId ))
					{
						test.add( dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(),  
								Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
						continue;
					}

					//Query Cosmos for fetching random list of member interventions for provided memberId
					List<MemberIntervention> expectedMemberInterventions = MemberInterventionQueries.getInterventionByType(subscriptionId, memberId, type.getEnumValue());

					//fields for api body
					String searchTerm = expectedMemberInterventions.get(0).getLastUpdatedBy();
					String targetInterventionQueueType = type.getEnumValue();
					String subscriptionName = subscriptionId;

					// Create API body
					JsonObject body = createApiBody(searchTerm, targetInterventionQueueType, "providerName", "desc",  200);

					List<DynamicNode> testAllResults = new ArrayList<>();

					// Call API
					ApiPostStep apiPostStep = searchMemberIntervention(subscriptionName, body, 200);
					MemberInterventionSearchResponse searchResult = apiPostStep.convertToJsonDTO(MemberInterventionSearchResponse.class);
					if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
					{
						return apiPostStep.getTestResults();
					}

					//collecting id of member intervention from api response in the stream  (limiting to 50)
					List<String> memberIdListFromApiResponse = searchResult.getInterventions().stream().map(list -> list.getId()).limit(50)
							.collect(Collectors.toList());

					//list of plan cost for sortField Validation (limiting to 50)
					List<String> providerNameFromApiResponseSort = searchResult.getInterventions().stream().map(list -> list.getProviderName()).limit(50)
							.collect(Collectors.toList());

					//fetching list of member intervention from cosmos db by collecting list of id from api response 
					List<MemberIntervention> listOfMemberInterventionsFromCosmos = MemberInterventionQueries.getInterventionById(subscriptionName,memberIdListFromApiResponse.toArray(new String[0]));

					//mapping listOfMemberInterventionsFromCosmos into mappedSearchMemberIntervention
					List<SearchMemberIntervention> mappedSearchMemberIntervention = new ArrayList<>();

					for ( MemberIntervention memberIntervention : listOfMemberInterventionsFromCosmos ) 
					{
						SearchMemberIntervention mappedInterventionSearch = createMappedInterventionSearch( memberIntervention ); 
						mappedSearchMemberIntervention.add(mappedInterventionSearch);
					}

					//list of plan cost for sortField Validation
					List<String> providerNameFromCosmosResponseSort = mappedSearchMemberIntervention.stream().map(list -> list.getProviderName())
							.collect(Collectors.toList());

					providerNameFromCosmosResponseSort.sort(Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER.reversed()));

					//Sort validation
					testType.add(dynamicTest("Assertion" , () -> assertThat(searchResult.getApiInfo(),providerNameFromApiResponseSort, contains(providerNameFromCosmosResponseSort.toArray(new String[providerNameFromCosmosResponseSort.size()])))));

					//Search Result validation
					testAllResults = validateSearchResult(searchResult, searchTerm, targetInterventionQueueType, mappedSearchMemberIntervention);
					testAllResults.add(dynamicContainer("API Response", apiPostStep.getTestResults()));
					testType.add(dynamicContainer(" Validation : Subscription Name / Target Intervention Queue Type [" + subscriptionName.toUpperCase() + " / " + type.getEnumValue() + "]" ,testAllResults));
					test.add(dynamicContainer(subscriptionName.toUpperCase() + " / " + type.getEnumValue() , testType));
				}
			}

			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("126081: Member PostInterventionSearch Happy Path Search Query by Rxcc Group Name, SortField  providerNpi, Sort Direction asc")
	@Order(4)
	public List<DynamicNode> withBodyHappyPathRxccGroupName() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();
		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					List<DynamicNode> testType = new ArrayList<>();

					//grabs ranmdom memberId that has defined targetInterventionQueueType enum value
					String memberId = "";
					memberId = MemberInterventionQueries.getRandomMemberWithInterventionBasedOnTargetInterventionQueueType(subscriptionId,type.toString(), null);

					//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
					if (StringUtils.isEmpty(memberId ))
					{
						test.add( dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(),  
								Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
						continue;
					}

					//Query Cosmos for fetching random list of member interventions for provided memberId
					List<MemberIntervention> expectedMemberInterventions = MemberInterventionQueries.getInterventionByType(subscriptionId, memberId, type.getEnumValue());

					//fields for api body
					String searchTerm = expectedMemberInterventions.get(0).getRxccGroupName().substring(0, 4);
					searchTerm = searchTerm.replaceAll("[^a-zA-Z0-9]", " ");
					String targetInterventionQueueType = type.getEnumValue();
					String subscriptionName = subscriptionId;

					// Create API body
					JsonObject body = createApiBody(searchTerm, targetInterventionQueueType, "providerNpi", "asc",  200);

					List<DynamicNode> testAllResults = new ArrayList<>();

					// Call API
					ApiPostStep apiPostStep = searchMemberIntervention(subscriptionName, body, 200);
					MemberInterventionSearchResponse searchResult = apiPostStep.convertToJsonDTO(MemberInterventionSearchResponse.class);
					if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
					{
						return apiPostStep.getTestResults();
					}

					//collecting id of member intervention from api response in the stream  (limiting to 50)
					List<String> memberIdListFromApiResponse = searchResult.getInterventions().stream().map(list -> list.getId()).limit(50)
							.collect(Collectors.toList());

					//list of plan cost for sortField Validation (limiting to 50)
					List<String> memberDateBirthFromApiResponseSort = searchResult.getInterventions().stream().map(list -> list.getProviderNpi()).limit(50)
							.collect(Collectors.toList());

					//fetching list of member intervention from cosmos db by collecting list of id from api response 
					List<MemberIntervention> listOfMemberInterventionsFromCosmos = MemberInterventionQueries.getInterventionById(subscriptionName,memberIdListFromApiResponse.toArray(new String[0]));

					//mapping listOfMemberInterventionsFromCosmos into mappedSearchMemberIntervention
					List<SearchMemberIntervention> mappedSearchMemberIntervention = new ArrayList<>();

					for ( MemberIntervention memberIntervention : listOfMemberInterventionsFromCosmos ) 
					{
						SearchMemberIntervention mappedInterventionSearch = createMappedInterventionSearch( memberIntervention ); 
						mappedSearchMemberIntervention.add(mappedInterventionSearch);
					}

					//list of plan cost for sortField Validation
					List<String> providerNpiFromCosmosResponseSort = mappedSearchMemberIntervention.stream().map(list -> list.getProviderNpi())
							.collect(Collectors.toList());

					//Checking api result sorting order to external sorting
					providerNpiFromCosmosResponseSort.sort(String.CASE_INSENSITIVE_ORDER);

					//Sort validation
					testType.add(dynamicTest("Assertion" , 	() -> assertThat(searchResult.getApiInfo(),memberDateBirthFromApiResponseSort, contains(providerNpiFromCosmosResponseSort.toArray(new String[providerNpiFromCosmosResponseSort.size()])))));

					//Search Result Validation
					testAllResults.add(dynamicContainer("API Response", apiPostStep.getTestResults()));
					testAllResults = validateSearchResult(searchResult, searchTerm, targetInterventionQueueType, mappedSearchMemberIntervention);
					testType.add(dynamicContainer(" Validation : Subscription Name / Target Intervention Queue Type [" + subscriptionName.toUpperCase() + " / " + type.getEnumValue() + "]" ,testAllResults));
					test.add(dynamicContainer(subscriptionName.toUpperCase() + " / " + type.getEnumValue() , testType));
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}


	@TestFactory
	@DisplayName("126082: Member PostInterventionSearch Happy Path Search Query by Original Npi, SortField  planCost, Sort Direction desc")
	@Order(5)
	public List<DynamicNode> withBodyHappyPathOriginalNpi() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();
		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					List<DynamicNode> testType = new ArrayList<>();

					//grabs ranmdom memberId that has defined targetInterventionQueueType enum value
					String memberId = "";
					memberId = MemberInterventionQueries.getRandomMemberWithInterventionBasedOnTargetInterventionQueueType(subscriptionId,type.toString(),"false");

					//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
					if (StringUtils.isEmpty(memberId ))
					{
						test.add( dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(),  
								Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
						continue;
					}

					//Query Cosmos for fetching random list of member interventions for provided memberId
					List<MemberIntervention> expectedMemberInterventions = MemberInterventionQueries.getInterventionByType(subscriptionId, memberId, type.getEnumValue());

					//fields for api body
					String searchTerm = expectedMemberInterventions.get(0).getOriginalNpi();
					String targetInterventionQueueType = type.getEnumValue();
					String subscriptionName = subscriptionId;

					// Create API body
					JsonObject body = createApiBody(searchTerm, targetInterventionQueueType, "planCost", "desc",  200);

					List<DynamicNode> testAllResults = new ArrayList<>();

					// Call API
					ApiPostStep apiPostStep = searchMemberIntervention(subscriptionName, body, 200);
					MemberInterventionSearchResponse searchResult = apiPostStep.convertToJsonDTO(MemberInterventionSearchResponse.class);
					if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
					{
						return apiPostStep.getTestResults();
					}

					//collecting id of member intervention from api response in the stream  (limiting to 50)
					List<String> memberIdListFromApiResponse = searchResult.getInterventions().stream().map(list -> list.getId()).limit(50)
							.collect(Collectors.toList());

					//list of plan cost for sortField Validation (limiting to 50)
					List<Double> planCostFromApiResponseSort = searchResult.getInterventions().stream().map(list -> list.getPlanCost()).limit(50)
							.collect(Collectors.toList());

					//fetching list of member intervention from cosmos db by collecting list of id from api response 
					List<MemberIntervention> listOfMemberInterventionsFromCosmos = MemberInterventionQueries.getInterventionById(subscriptionName,memberIdListFromApiResponse.toArray(new String[0]));

					//mapping listOfMemberInterventionsFromCosmos into mappedSearchMemberIntervention
					List<SearchMemberIntervention> mappedSearchMemberIntervention = new ArrayList<>();

					for ( MemberIntervention memberIntervention : listOfMemberInterventionsFromCosmos ) 
					{
						SearchMemberIntervention mappedInterventionSearch = createMappedInterventionSearch( memberIntervention ); 
						mappedSearchMemberIntervention.add(mappedInterventionSearch);
					}

					//list of plan cost for sortField Validation
					List<Double> plancostFromCosmosResponseSort = mappedSearchMemberIntervention.stream().map(list -> list.getPlanCost())
							.collect(Collectors.toList());

					//Checking api result sorting order to external sorting
					Collections.sort(plancostFromCosmosResponseSort, Collections.reverseOrder()); 

					//Sort Validation
					testType.add(dynamicTest("Assertion" , 
							() -> assertThat(searchResult.getApiInfo(),planCostFromApiResponseSort, contains(plancostFromCosmosResponseSort.toArray(new Double[plancostFromCosmosResponseSort.size()])))));

					//Search Result Validation
					testAllResults.add(dynamicContainer("API Response", apiPostStep.getTestResults()));
					testAllResults = validateSearchResult(searchResult, searchTerm, targetInterventionQueueType, mappedSearchMemberIntervention);
					testType.add(dynamicContainer(" Validation : Subscription Name / Target Intervention Queue Type [" + subscriptionName.toUpperCase() + " / " + type.getEnumValue() + "]" ,testAllResults));
					test.add(dynamicContainer(subscriptionName.toUpperCase() + " / " + type.getEnumValue() , testType));
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("126083: Member PostInterventionSearch Happy Path Search Query by Target Product Name, SortField  memberDateBirth, Sort Direction desc")
	@Order(6)
	public List<DynamicNode> withBodyHappyPathProviderName() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();
		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					List<DynamicNode> testType = new ArrayList<>();

					//grabs ranmdom memberId that has defined targetInterventionQueueType enum value
					String memberId = "";
					memberId = MemberInterventionQueries.getRandomMemberWithInterventionBasedOnTargetInterventionQueueType(subscriptionId,type.toString(),null);

					//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
					if (StringUtils.isEmpty(memberId ))
					{
						test.add( dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(),   
								Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
						continue;
					}

					//Query Cosmos for fetching random list of member interventions for provided memberId
					List<MemberIntervention> expectedMemberInterventions = MemberInterventionQueries.getInterventionByType(subscriptionId, memberId, type.getEnumValue());

					//fields for api body
					String searchTerm = expectedMemberInterventions.get(0).getTargetProductName().substring(0, 10);
					searchTerm = searchTerm.replaceAll("[^a-zA-Z0-9]", " ");
					String targetInterventionQueueType = type.getEnumValue();
					String subscriptionName = subscriptionId;

					// Create API body
					JsonObject body = createApiBody(searchTerm, targetInterventionQueueType, "memberDateBirth", "desc",  200);

					List<DynamicNode> testAllResults = new ArrayList<>();

					// Call API
					ApiPostStep apiPostStep = searchMemberIntervention(subscriptionName, body, 200);
					MemberInterventionSearchResponse searchResult = apiPostStep.convertToJsonDTO(MemberInterventionSearchResponse.class);
					if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
					{
						return apiPostStep.getTestResults();
					}

					//collecting id of member intervention from api response in the stream  (limiting to 50)
					List<String> memberIdListFromApiResponse = searchResult.getInterventions().stream().map(list -> list.getId()).limit(50)
							.collect(Collectors.toList());

					//list of plan cost for sortField Validation (limiting to 50)
					List<String> memberDateBirthFromApiResponseSort = searchResult.getInterventions().stream().map(list -> list.getMemberDateBirth()).limit(50)
							.collect(Collectors.toList());

					//fetching list of member intervention from cosmos db by collecting list of id from api response 
					List<MemberIntervention> listOfMemberInterventionsFromCosmos = MemberInterventionQueries.getInterventionById(subscriptionName,memberIdListFromApiResponse.toArray(new String[0]));

					//mapping listOfMemberInterventionsFromCosmos into mappedSearchMemberIntervention
					List<SearchMemberIntervention> mappedSearchMemberIntervention = new ArrayList<>();

					for ( MemberIntervention memberIntervention : listOfMemberInterventionsFromCosmos ) 
					{
						SearchMemberIntervention mappedInterventionSearch = createMappedInterventionSearch( memberIntervention ); 
						mappedSearchMemberIntervention.add(mappedInterventionSearch);
					}

					//list of plan cost for sortField Validation
					List<String> memberDateBirthFromCosmosResponseSort = mappedSearchMemberIntervention.stream().map(list -> list.getMemberDateBirth())
							.collect(Collectors.toList());

					//Checking api result sorting order to external sorting
					memberDateBirthFromCosmosResponseSort.sort(String.CASE_INSENSITIVE_ORDER.reversed());

					//Collections.sort(plancostFromCosmosResponseSort);
					//Collections.sort(plancostFromCosmosResponseSort, Collections.reverseOrder()); 

					//Sort Validation
					testType.add(dynamicTest("Assertion" , 
							() -> assertThat(searchResult.getApiInfo(),memberDateBirthFromApiResponseSort, contains(memberDateBirthFromCosmosResponseSort.toArray(new String[memberDateBirthFromCosmosResponseSort.size()])))));

					//Search Result Validation
					testAllResults.add(dynamicContainer("API Response", apiPostStep.getTestResults()));
					testAllResults = validateSearchResult(searchResult, searchTerm, targetInterventionQueueType, mappedSearchMemberIntervention);
					testType.add(dynamicContainer(" Validation : Subscription Name / Target Intervention Queue Type [" + subscriptionName.toUpperCase() + " / " + type.getEnumValue() + "]" ,testAllResults));
					test.add(dynamicContainer(subscriptionName.toUpperCase() + " / " + type.getEnumValue() , testType));
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("126084: Member PostInterventionSearch Happy Path Search Query by queueStatusCode, SortField  memberCost, Sort Direction asc")
	@Order(7)
	public List<DynamicNode> withBodyHappyPathMemberCost() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();
		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					List<DynamicNode> testType = new ArrayList<>();

					//grabs ranmdom memberId that has defined targetInterventionQueueType enum value
					String memberId = "";
					memberId = MemberInterventionQueries.getRandomMemberWithInterventionBasedOnTargetInterventionQueueType(subscriptionId,type.toString(),null);

					//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
					if (StringUtils.isEmpty(memberId ))
					{
						test.add( dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(),   
								Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
						continue;
					}

					//Query Cosmos for fetching random list of member interventions for provided memberId
					List<MemberIntervention> expectedMemberInterventions = MemberInterventionQueries.getInterventionByType(subscriptionId, memberId, type.getEnumValue());

					//fields for api body
					String searchTerm = expectedMemberInterventions.get(0).getQueueStatusCode();
					String targetInterventionQueueType = type.getEnumValue();
					String subscriptionName = subscriptionId;

					// Create API body
					JsonObject body = createApiBody(searchTerm.toString(), targetInterventionQueueType, "memberCost", "asc",  200);

					List<DynamicNode> testAllResults = new ArrayList<>();

					// Call API
					ApiPostStep apiPostStep = searchMemberIntervention(subscriptionName, body, 200);
					MemberInterventionSearchResponse searchResult = apiPostStep.convertToJsonDTO(MemberInterventionSearchResponse.class);
					if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
					{
						return apiPostStep.getTestResults();
					}

					//collecting id of member intervention from api response in the stream  (limiting to 50)
					List<String> memberIdListFromApiResponse = searchResult.getInterventions().stream().map(list -> list.getId()).limit(50)
							.collect(Collectors.toList());

					//list of plan cost for sortField Validation (limiting to 50)
					List<Double> planCostFromApiResponseSort = searchResult.getInterventions().stream().map(list -> list.getMemberCost()).limit(50)
							.collect(Collectors.toList());

					//fetching list of member intervention from cosmos db by collecting list of id from api response 
					List<MemberIntervention> listOfMemberInterventionsFromCosmos = MemberInterventionQueries.getInterventionById(subscriptionName,memberIdListFromApiResponse.toArray(new String[0]));

					//mapping listOfMemberInterventionsFromCosmos into mappedSearchMemberIntervention
					List<SearchMemberIntervention> mappedSearchMemberIntervention = new ArrayList<>();

					for ( MemberIntervention memberIntervention : listOfMemberInterventionsFromCosmos ) 
					{
						SearchMemberIntervention mappedInterventionSearch = createMappedInterventionSearch( memberIntervention ); 
						mappedSearchMemberIntervention.add(mappedInterventionSearch);
					}

					//list of plan cost for sortField Validation
					List<Double> memberCostFromCosmosResponseSort = mappedSearchMemberIntervention.stream().map(list -> list.getMemberCost())
							.collect(Collectors.toList());

					Collections.sort(memberCostFromCosmosResponseSort);

					//Sort Validation
					testType.add(dynamicTest("Assertion" , 
							() -> assertThat(searchResult.getApiInfo(),planCostFromApiResponseSort, contains(memberCostFromCosmosResponseSort.toArray(new Double[memberCostFromCosmosResponseSort.size()])))));

					//Search Result Validation
					testAllResults.add(dynamicContainer("API Response", apiPostStep.getTestResults()));
					testAllResults = validateSearchResult(searchResult, searchTerm.toString(), targetInterventionQueueType, mappedSearchMemberIntervention);
					testType.add(dynamicContainer(" Validation : Subscription Name / Target Intervention Queue Type [" + subscriptionName.toUpperCase() + " / " + type.getEnumValue() + "]" ,testAllResults));
					test.add(dynamicContainer(subscriptionName.toUpperCase() + " / " + type.getEnumValue() , testType));
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("126085: Member PostInterventionSearch Happy Path Search Query by Member Id,  No Sort, No Direction & No size")
	@Order(8)
	public List<DynamicNode> withBodymemberID() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();
		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					List<DynamicNode> testType = new ArrayList<>();

					//grabs ranmdom memberId that has defined targetInterventionQueueType enum value
					String memberId = "";
					memberId = MemberInterventionQueries.getRandomMemberWithInterventionBasedOnTargetInterventionQueueType(subscriptionId,type.toString(), null);

					//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
					if (StringUtils.isEmpty(memberId ))
					{
						test.add( dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(),   
								Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
						continue;
					}

					//Query Cosmos for fetching random list of member interventions for provided memberId
					List<MemberIntervention> expectedMemberInterventions = MemberInterventionQueries.getInterventionByType(subscriptionId, memberId, type.getEnumValue());

					//fields for api body
					String searchTerm = expectedMemberInterventions.get(0).getMemberId().substring(0,1);
					String targetInterventionQueueType = type.getEnumValue();
					String subscriptionName = subscriptionId;

					// Create API body
					JsonObject body = createApiBody(searchTerm, targetInterventionQueueType, null, null,  0);

					List<DynamicNode> testAllResults = new ArrayList<>();

					// Call API
					ApiPostStep apiPostStep = searchMemberIntervention(subscriptionName, body, 200);
					MemberInterventionSearchResponse searchResult = apiPostStep.convertToJsonDTO(MemberInterventionSearchResponse.class);
					if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
					{
						return apiPostStep.getTestResults();
					}

					//collecting id of member intervention from api response in the stream  (limiting to 50)
					List<String> memberIdListFromApiResponse = searchResult.getInterventions().stream().map(list -> list.getId()).limit(50)
							.collect(Collectors.toList());


					//fetching list of member intervention from cosmos db by collecting list of id from api response 
					List<MemberIntervention> listOfMemberInterventionsFromCosmos = MemberInterventionQueries.getInterventionById(subscriptionName,memberIdListFromApiResponse.toArray(new String[0]));

					//mapping listOfMemberInterventionsFromCosmos into mappedSearchMemberIntervention
					List<SearchMemberIntervention> mappedSearchMemberIntervention = new ArrayList<>();

					for ( MemberIntervention memberIntervention : listOfMemberInterventionsFromCosmos ) 
					{
						SearchMemberIntervention mappedInterventionSearch = createMappedInterventionSearch( memberIntervention ); 
						mappedSearchMemberIntervention.add(mappedInterventionSearch);
					}

					//Search Result Validation
					testAllResults = validateSearchResult(searchResult, searchTerm, targetInterventionQueueType, mappedSearchMemberIntervention);
					testAllResults.add(dynamicContainer("API Response", apiPostStep.getTestResults()));
					testType.add(dynamicContainer(" Validation : Subscription Name / Target Intervention Queue Type [" + subscriptionName.toUpperCase() + " / " + type.getEnumValue() + "]" ,testAllResults));
					test.add(dynamicContainer(subscriptionName.toUpperCase() + " / " + type.getEnumValue() , testType));
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("126086: Member PostInterventionSearch Happy Path Search Query by Queue Status Change Dat Time first letter,  No Sort, No Direction & No size")
	@Order(9)
	public List<DynamicNode> withBodyQueueStatusCode() throws JsonProcessingException
	{
		List<DynamicNode> test = new ArrayList<>();
		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					List<DynamicNode> testType = new ArrayList<>();

					//grabs random memberId that has defined targetInterventionQueueType enum value
					String memberId = "";
					memberId = MemberInterventionQueries.getRandomMemberWithInterventionBasedOnTargetInterventionQueueType(subscriptionId,type.toString(), null);

					//if memberId is not found, throw the "NO TEST DATA FOUND"and skip the test
					if (StringUtils.isEmpty(memberId ))
					{
						test.add( dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(),  
								Arrays.asList(dynamicTest("NO TEST DATA FOUND", () -> assertTrue(true)))));
						continue;
					}

					//Query Cosmos for fetching random list of member interventions for provided memberId
					List<MemberIntervention> expectedMemberInterventions = MemberInterventionQueries.getInterventionByType(subscriptionId, memberId, type.getEnumValue());

					//fields for api body
					String searchTerm = expectedMemberInterventions.get(0).getQueueStatusChangeDateTime().substring(0,10).replace("-", " ");
					String targetInterventionQueueType = type.getEnumValue();
					String subscriptionName = subscriptionId;

					// Create API body
					JsonObject body = createApiBody(searchTerm, targetInterventionQueueType, null, null,  0);

					List<DynamicNode> testAllResults = new ArrayList<>();

					// Call API
					ApiPostStep apiPostStep = searchMemberIntervention(subscriptionName, body, 200);
					MemberInterventionSearchResponse searchResult = apiPostStep.convertToJsonDTO(MemberInterventionSearchResponse.class);
					if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
					{
						return apiPostStep.getTestResults();
					}

					//collecting id of member intervention from api response in the stream  (limiting to 50)
					List<String> memberIdListFromApiResponse = searchResult.getInterventions().stream().map(list -> list.getId()).limit(50)
							.collect(Collectors.toList());


					//fetching list of member intervention from cosmos db by collecting list of id from api response 
					List<MemberIntervention> listOfMemberInterventionsFromCosmos = MemberInterventionQueries.getInterventionById(subscriptionName,memberIdListFromApiResponse.toArray(new String[0]));

					//mapping listOfMemberInterventionsFromCosmos into mappedSearchMemberIntervention
					List<SearchMemberIntervention> mappedSearchMemberIntervention = new ArrayList<>();

					for ( MemberIntervention memberIntervention : listOfMemberInterventionsFromCosmos ) 
					{
						SearchMemberIntervention mappedInterventionSearch = createMappedInterventionSearch( memberIntervention ); 
						mappedSearchMemberIntervention.add(mappedInterventionSearch);
					}

					//Search Result Validation
					testAllResults = validateSearchResult(searchResult, searchTerm, targetInterventionQueueType, mappedSearchMemberIntervention);
					testAllResults.add(dynamicContainer("API Response", apiPostStep.getTestResults()));
					testType.add(dynamicContainer(" Validation : Subscription Name / Target Intervention Queue Type [" + subscriptionName.toUpperCase() + " / " + type.getEnumValue() + "]" ,testAllResults));
					test.add(dynamicContainer(subscriptionName.toUpperCase() + " / " + type.getEnumValue() , testType));
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	/**
	 * Setup the test data. Map Cosmos Db search result into SearchMemberIntervention
	 * @param memberIntervention
	 */
	private SearchMemberIntervention createMappedInterventionSearch(MemberIntervention memberIntervention )
	{
		//logic for retrieving provider information based on the business rule 
		String providerNpi  = memberIntervention.isProviderOverride() == true ? memberIntervention.getOverrideNpi() : memberIntervention.getOriginalNpi();
		String providerName = memberIntervention.isProviderOverride() == true ?  memberIntervention.getOverrideProviderName() : memberIntervention.getOriginalProviderName();
		String providerPhoneNumber = memberIntervention.isProviderOverride() == true ? (memberIntervention.getOverrideProviderContact() != null ?
				memberIntervention.getOverrideProviderContact().getPhoneNumber():null) : (memberIntervention.getOriginalProviderContact() != null ? memberIntervention.getOriginalProviderContact().getPhoneNumber(): null);

		SearchMemberIntervention mappedInterventionSearch = new SearchMemberIntervention();

		mappedInterventionSearch.setId(memberIntervention.getId());
		mappedInterventionSearch.setQueueStatusChangeDateTime(memberIntervention.getQueueStatusChangeDateTime());
		mappedInterventionSearch.setQueueStatusCode(memberIntervention.getQueueStatusCode());
		mappedInterventionSearch.setRxccGroupName(memberIntervention.getRxccGroupName());
		mappedInterventionSearch.setPlanCost(memberIntervention.getPlanCost());
		mappedInterventionSearch.setMemberCost(memberIntervention.getMemberCost());
		mappedInterventionSearch.setMemberId(memberIntervention.getMemberId());
		mappedInterventionSearch.setMemberName(memberIntervention.getMemberName());
		mappedInterventionSearch.setMemberDateBirth(memberIntervention.getMemberDateBirth());
		mappedInterventionSearch.setAssignedTo(memberIntervention.getAssignedTo());
		mappedInterventionSearch.setTargetProductName(memberIntervention.getTargetProductName());
		mappedInterventionSearch.setLastUpdatedBy(memberIntervention.getLastUpdatedBy());
		mappedInterventionSearch.setProviderNpi( providerNpi);
		mappedInterventionSearch.setProviderName(providerName );
		mappedInterventionSearch.setProviderPhoneNumber( providerPhoneNumber);
		mappedInterventionSearch.setTargetInterventionQueueType(memberIntervention.getTargetInterventionQueueType());

		return mappedInterventionSearch;
	}

	/**
	 * Create API body
	 * 
	 * @param searchTerm, targetInterventionQueueType, sortField, sortDirection, size
	 * @return
	 */
	private JsonObject createApiBody(String searchTerm, String targetInterventionQueueType, String sortField, String sortDirection, int size)

	{
		JsonObject body = new JsonObject();

		if ( StringUtils.isNotEmpty(searchTerm) )
			body.addProperty("query", searchTerm);

		if ( StringUtils.isNotEmpty(targetInterventionQueueType) )
			body.addProperty("targetInterventionQueueType", targetInterventionQueueType);

		if ( StringUtils.isNotEmpty(sortField) )
			body.addProperty("sortField", sortField);

		if ( StringUtils.isNotEmpty(sortDirection) )
			body.addProperty("sortDirection", sortDirection);

		if ( size > 0 )
			body.addProperty("size", String.valueOf(size));

		return body;
	}


	/**
	 * Perform the API call
	 *
	 * 
	 * @param JsonObject to be search with
	 * @param size of s
	 * @return
	 */
	private ApiPostStep  searchMemberIntervention(String subscriptionName ,JsonObject body, int size) 
	{
		logger.debug("Starting API call");

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName));

		ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_INTERVENTION_SEARCH_ENDPOINT, body.toString(),null, 200, null);

		apiPostStep.run();
		return apiPostStep;

	}

	/**
	 * Validate the search result
	 *
	 * @param searchResult the API response that contains list of intervention
	 * @param searchTerm search terms
	 * @return
	 */
	private List<DynamicNode> validateSearchResult(MemberInterventionSearchResponse searchResult, String searchTerm, String targetInterventionQueueType, List<SearchMemberIntervention> listOfMemberInterventionsFromCosmos)
	{
		List<DynamicNode> test = new ArrayList<DynamicNode>();

		test.add(dynamicTest("Total Count [" + searchResult.getCount() + "]", () -> assertTrue(searchResult.getCount() > 0, "Expecting API return at least 1 or more intervention")));

		test.add(dynamicTest("Validate search term [" + searchTerm + "] in intervention information",
				() -> {
					assertAll("Intervention name",
							() -> {
								for (SearchMemberIntervention memberInterventionFinal : listOfMemberInterventionsFromCosmos )
								{
									assertTrue(memberInterventionFinal.interventionContains(searchTerm),
											memberInterventionFinal.getSearchFieldValues() + " did not contain the search term [" + searchTerm + "]");
								}
							});
				}));

		test.add(dynamicTest("Validate Target Intervention Queue Type [" + targetInterventionQueueType + "] in intervention information",
				() -> {
					assertAll("Intervention name",
							() -> {
								for (SearchMemberIntervention memberInterventionSearch : searchResult.getInterventions() )
								{
									assertTrue(memberInterventionSearch.getTargetInterventionQueueType() == TargetInterventionQueueType.valueOf(targetInterventionQueueType) ,
											memberInterventionSearch.getTargetInterventionQueueTypeFieldValues() + " did not contain the targetInterventionQueueType term [" + targetInterventionQueueType + "]");
								}
							});
				}));

		return test;
	}


	/*
	 * Negative test cases
	 */

	@TestFactory
	@DisplayName("126087: Negative : Member PostInterventionSearch Invalid Auth")
	@Order(10)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException 
	{
		List<DynamicNode> testAllResults = new ArrayList<DynamicNode>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			subscriptionId = tenant.getSubscriptionName();

			ApiPostStep apiPostStep = new ApiPostStep(getHeadersInvalidAuth(), MEMBER_INTERVENTION_SEARCH_ENDPOINT, GENERIC_BODY,
					null, 401, HTTP_401_UNAUTHORIZED);
			apiPostStep.run();

			testAllResults.add(dynamicContainer(subscriptionId.toUpperCase(), apiPostStep.getTestResults())); // add all step test result

			resetApiInfo(); // reset API info and test validation results
		}
		return testAllResults;
	}


	@TestFactory
	@DisplayName("126088: Negative : Member PostInterventionSearch with missing partial Body (missing  targetInterventionQueueType)")
	@Order(11)
	public List<DynamicNode> bodyMissingValueTargetInterventionQueueType()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					//fields for api body
					String searchTerm = "6";

					// Create API body
					JsonObject body = createApiBody(searchTerm, null, null, null,  0);

					Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

					ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_INTERVENTION_SEARCH_ENDPOINT, body.toString(),
							new Object[]{},	400, HTTP_400_BAD_REQUEST);
					apiPostStep.run();

					// validate the API message
					final String EXPECTED_MSG = "\'targetInterventionQueueType\' is invalid!";

					String actualMsg = apiPostStep.getResponse().then().extract().asString();
					apiPostStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
							() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())) );

					test.add(dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(), apiPostStep.getTestResults())); // add all step test result
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("126089: Negative : Member PostInterventionSearch with missing partial Body (missing  query)")
	@Order(12)
	public List<DynamicNode> bodyMissingValueQuery()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					//fields for api body
					String targetInterventionQueueType = type.getEnumValue();

					// Create API body
					JsonObject body = createApiBody(null, targetInterventionQueueType, null, null,  200);

					Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

					ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_INTERVENTION_SEARCH_ENDPOINT, body.toString(),
							new Object[]{},	400, HTTP_400_BAD_REQUEST);
					apiPostStep.run();

					// validate the API message
					final String EXPECTED_MSG = "\'query\' is required!";

					String actualMsg = apiPostStep.getResponse().then().extract().asString();
					apiPostStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
							() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())) );

					test.add(dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(), apiPostStep.getTestResults())); // add all step test result
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}


	@TestFactory
	@DisplayName("126090: Negative : Member PostInterventionSearch with missing only sort direction")
	@Order(13)
	public List<DynamicNode> bodyMissingValueSortDirection()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					//fields for api body
					String searchTerm = "6";
					String targetInterventionQueueType = type.getEnumValue();

					// Create API body
					JsonObject body = createApiBody(searchTerm, targetInterventionQueueType, "memberName", null,  0);

					Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

					ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_INTERVENTION_SEARCH_ENDPOINT, body.toString(),
							new Object[]{},	400, HTTP_400_BAD_REQUEST);
					apiPostStep.run();

					// validate the API message
					final String EXPECTED_MSG = "\'sortField\' and \'sortDirection\' are both required for sorting!";

					String actualMsg = apiPostStep.getResponse().then().extract().asString();
					apiPostStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
							() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())) );

					test.add(dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(), apiPostStep.getTestResults())); // add all step test result
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}


	@TestFactory
	@DisplayName("126091: Negative : Member PostInterventionSearch with missing only sort field")
	@Order(14)
	public List<DynamicNode> bodyMissingValueSortField()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					//fields for api body
					//fields for api body
					String searchTerm = "6";
					String targetInterventionQueueType = type.getEnumValue();

					// Create API body
					JsonObject body = createApiBody(searchTerm, targetInterventionQueueType, null, "asc",  0);

					Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

					ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_INTERVENTION_SEARCH_ENDPOINT, body.toString(),
							new Object[]{},	400, HTTP_400_BAD_REQUEST);
					apiPostStep.run();

					// validate the API message
					final String EXPECTED_MSG = "\'sortField\' and \'sortDirection\' are both required for sorting!";

					String actualMsg = apiPostStep.getResponse().then().extract().asString();
					apiPostStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
							() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())) );

					test.add(dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(), apiPostStep.getTestResults())); // add all step test result
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("126092: Negative : Member PostInterventionSearch with invalid targetInterventionQueueType")
	@Order(15)
	public List<DynamicNode> bodyInvalidValueTargetInterventionQueueType()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//fields for api body
				String searchTerm = "6";

				// targetInterventionQueueType can be only RPH or OC
				String invalidTargetInterventionQueueType = "ZZZ";

				// Create API body
				JsonObject body = createApiBody(searchTerm, invalidTargetInterventionQueueType, null, null,  0);

				Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

				ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_INTERVENTION_SEARCH_ENDPOINT, body.toString(),
						new Object[]{},	400, HTTP_400_BAD_REQUEST);
				apiPostStep.run();

				// validate the API message
				final String EXPECTED_MSG = "\'targetInterventionQueueType\' is invalid!";

				String actualMsg = apiPostStep.getResponse().then().extract().asString();
				apiPostStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
						() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())) );

				test.add(dynamicContainer(subscriptionId.toUpperCase() , apiPostStep.getTestResults())); // add all step test result
			}

			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("126093: Negative : Member PostInterventionSearch with invalid sortField")
	@Order(16)
	public List<DynamicNode> bodyInvalidValueSortField()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					//fields for api body
					String searchTerm = "6";
					String targetInterventionQueueType = type.getEnumValue();

					//invalid sortField
					String invalidSortField = "invalid";

					// Create API body
					JsonObject body = createApiBody(searchTerm, targetInterventionQueueType, invalidSortField, "asc", 200);

					Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

					ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_INTERVENTION_SEARCH_ENDPOINT, body.toString(),
							new Object[]{},	400, HTTP_400_BAD_REQUEST);
					apiPostStep.run();

					// validate the API message
					final String EXPECTED_MSG = "\'sortField\' is invalid!";

					String actualMsg = apiPostStep.getResponse().then().extract().asString();
					apiPostStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
							() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())) );

					test.add(dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(), apiPostStep.getTestResults())); // add all step test result
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("126094: Negative : Member PostInterventionSearch with invalid sortDirection")
	@Order(17)
	public List<DynamicNode> bodyInvalidValueSortDirection()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					///fields for api body
					String searchTerm = "6";
					String targetInterventionQueueType = type.getEnumValue();

					//invalid sortDirection
					String invalidSortDirection = "ascDesc";

					// Create API body
					JsonObject body = createApiBody(searchTerm, targetInterventionQueueType, "memberName", invalidSortDirection, 200);

					Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

					ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_INTERVENTION_SEARCH_ENDPOINT, body.toString(),
							new Object[]{},	400, HTTP_400_BAD_REQUEST);
					apiPostStep.run();

					// validate the API message
					final String EXPECTED_MSG = "\'sortDirection\' is invalid!";

					String actualMsg = apiPostStep.getResponse().then().extract().asString();
					apiPostStep.getTestResults().add( dynamicTest("API message [" + EXPECTED_MSG + "]",
							() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())) );

					test.add(dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(), apiPostStep.getTestResults())); // add all step test result
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("126095: Negative : Member PostInterventionSearch with invalid url")
	@Order(18)
	public List<DynamicNode> validBodyInvalidUrl()
	{
		List<DynamicNode> test = new ArrayList<>();

		List<Tenant> tenants = TenantQueries.getTenants();

		for(Tenant tenant : tenants)
		{
			try
			{
				//grabs all the tenants 
				subscriptionId = tenant.getSubscriptionName();

				//grabs all the targetInterventionQueueType enum value
				for(TargetInterventionQueueType type : TargetInterventionQueueType.values())
				{
					///fields for api body
					String searchTerm = "6";
					String targetInterventionQueueType = type.getEnumValue();

					// Create API body
					JsonObject body = createApiBody(searchTerm, targetInterventionQueueType,null, null, 0);

					Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, subscriptionId));

					ApiPostStep apiPostStep = new ApiPostStep(headers, "/api/member/interventionsearch", body.toString(),
							new Object[]{},	404, HTTP_404_RESOURCE_NOT_FOUND);
					apiPostStep.run();

					test.add(dynamicContainer(subscriptionId.toUpperCase() + " / " + type.getEnumValue(), apiPostStep.getTestResults())); // add all step test result
				}
			}
			catch (Exception e)
			{
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add( dynamicTest("Subscription Name :" + subscriptionId.toUpperCase(),
						() -> fail( apiInfo + "\n" + e.getMessage(), e)) );
			}
			resetApiInfo();  // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("126096: Negative : Member PostInterventionSearch Valid Parm With Incorrect Header Value")
	@Order(19)
	public List<DynamicNode> incorrectHeaderValue()
	{
		//fields for api body
		String searchTerm = "6";
		String targetInterventionQueueType = "RPH";

		// Create API body
		JsonObject body = createApiBody(searchTerm, targetInterventionQueueType,null, null, 0);

		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, "abc"));

		//call API
		ApiPostStep apiPostStep = new ApiPostStep(headers, MEMBER_INTERVENTION_SEARCH_ENDPOINT, body.toString(),
				new Object[]{},	500, HTTP_500_INTERNAL_SERVER_ERR);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("126097: Negative : Member PostInterventionSearch Header Missing")
	@Order(20)
	public List<DynamicNode> missingHeader()
	{
		//fields for api body
		String searchTerm = "6";
		String targetInterventionQueueType = "RPH";

		// Create API body
		JsonObject body = createApiBody(searchTerm, targetInterventionQueueType,null, null, 0);

		//call API
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), MEMBER_INTERVENTION_SEARCH_ENDPOINT, body.toString(),
				new Object[]{},	500, HTTP_500_INTERNAL_SERVER_ERR);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

}
