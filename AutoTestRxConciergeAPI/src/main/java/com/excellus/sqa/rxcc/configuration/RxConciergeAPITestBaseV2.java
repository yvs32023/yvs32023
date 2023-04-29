/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.configuration;

import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.extension.ExtendWith;

import com.excellus.sqa.configuration.IUserConfiguration;
import com.excellus.sqa.configuration.TestConfiguration;
import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.restapi.RequestLoggingFilter;
import com.excellus.sqa.restapi.steps.ApiDeleteStep;
import com.excellus.sqa.restapi.steps.ApiPatchStep;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.restapi.steps.ApiPutStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.junit.AddRoleToDisplayName;
import com.excellus.sqa.rxcc.junit.AssumeUserRole;
import com.excellus.sqa.spring.BeanLoader;
import com.excellus.sqa.spring.SpringConfig;
import com.excellus.sqa.test.TestBase;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * API base test class
 *
 * GC (10/25/22) Updates
 *               - login is now handle in annotation BeforeAll. This will allow the test to be configured to run with different user role.
 *               - move bean names to class {@link BeanNames}. Modified all corresponding classes affected
 * 
 * @author Garrett Cosmiano (gcosmian)
 * @since 01/17/2022
 */
@UserRole(role = {"RXCC_FULL_MULTI"})
@SpringConfig(classes = {RxConciergeBeanConfig.class, RxConciergeCosmoConfig.class})
@ExtendWith(RxConciergeUILogin.class)
@DisplayNameGeneration(AddRoleToDisplayName.class)	// GC (10/24/22) add custom display name with the role
@AssumeUserRole
public class RxConciergeAPITestBaseV2 extends TestBase
{
	
	private static final Logger logger = LoggerFactory.getLogger(RxConciergeAPITestBaseV2.class);

	protected static String API_HEADER_NAME = "X-RXCC-SUB";

	/*
	 * Lookup Endpoints
	 */

	public static final String LOOKUP_GET_ENDPOINT					= "/api/lookup/lookups/{type}";
	public static final String LOOKUPS_GET_ENDPOINT					= "/api/lookup/lookups";


	/*
	 * Providers Endpoints  
	 */
	public static final String PROVIDER_GET_ENDPOINT								= "/api/provider/providers/{providerId}";
	public static final String PROVIDER_PATCH_ENDPOINT								= "/api/provider/providers/{providerId}";
	public static final String PROVIDERS_POST_ENDPOINT								= "/api/provider/providers";
	public static final String PROVIDERS_INSERT_OFFICE_LOCATION_POST_ENDPOINT		= "/api/provider/providers/{providerId}/office-locations";
	public static final String PROVIDERS_REPLACE_OFFICE_LOCATION_POST_ENDPOINT		= "/api/provider/providers/{providerId}/office-locations/{officeLocationId}";
	
	
	/*
	 * Pharmacy Endpoints 
	 */
	public static final String GETPHARMACIES_POST_ENDPOINT			= "/api/pharmacy/pharmacies";
	public static final String PHARMACY_SEARCH_POST_ENDPOINT		= "/api/pharmacy/pharmacy-search";
	public static final String PHARMACY_GET_ENDPOINT				= "/api/pharmacy/pharmacies/{providerId}";
	public static final String PHARMACY_FIELD_SEARCH_POST_ENDPOINT	= "/api/pharmacy/field-search";
	public static final String PHARMACY_PATCH_ENDPOINT				= "/api/pharmacy/pharmacies/{providerId}";
	
	/*
	 * Tenant Endpoints
	 */

	public static final String TENANT_GET_ENDPOINT					= "/api/tenant/tenants/{adTenantId}";
	public static final String TENANTS_GET_ENDPOINT					= "/api/tenant/tenants";/**/
	public static final String TENANT_GET_GROUPS_ENDPOINT			= "/api/tenant/groups/{formularyCode}";
	public static final String TENANT_GET_CARRIERS_ENDPOINT			= "/api/tenant/tenants/{adTenantId}/carriers?carrierId={carrierId}&benefitHierarchyId={benefitHierarchyId}";
	
	/*
	 * Member
	 */
		
	public static final String MEMBER_GET_ENDPOINT					= "/api/member/members/{memberId}";	//	GetMember
	public static final String MEMBER_PATCH_POST_ENDPOINT			= "/api/member/members/{memberId}";	//	PatchMember
	
	public static final String MEMBER_NOTES_GET_ENDPOINT			= "/api/member/members/{memberId}/notes";	// GetNotes
	public static final String MEMBER_NOTE_POST_ENDPOINT			= "/api/member/members/{memberId}/notes";	// InsertNote
	public static final String MEMBER_NOTE_GET_ENDPOINT				= "/api/member/members/{memberId}/notes/{noteId}";	// GetNote
	public static final String MEMBER_NOTE_DELETE_POST_ENDPOINT		= "/api/member/members/{memberId}/notes/{noteId}";	// DeleteNote
	public static final String MEMBER_NOTE_PATCH_POST_ENDPOINT		= "/api/member/members/{memberId}/notes/{noteId}";	// PatchNote
	
	public static final String MEMBER_RX_CLAIMS_GET_ENDPOINT		= "/api/member/members/{memberId}/rxclaims/?adjudicationDateStart={adjudicationDateStart}";	// GetRxClaims
	public static final String MEMBER_RX_CLAIM_GET_ENDPOINT			= "/api/member/members/{memberId}/rxclaims/{rxClaimId}/?adjudicationDateStart={adjudicationDateStart}";	// GetRxClaim
	
	public static final String MEMBER_CORRESPONDENCE_DELETE_ENDPOINT		= "api/member/members/{memberId}/interventions/{interventionId}/correspondences/{id}";	// DeleteCorrespondence
	public static final String MEMBER_CORRESPONDENCE_GET_ENDPOINT			= "/api/member/members/{memberId}/interventions/{interventionId}/correspondences/{id}";	// GetCorrespondence
	public static final String MEMBER_CORRESPONDENCES_GET_ENDPOINT			= "/api/member/members/{memberId}/interventions/{interventionId}/correspondences";	// GetCorrespondences
	public static final String MEMBER_ALL_CORRESPONDENCES_GET_ENDPOINT		= "/api/member/members/{memberId}/correspondences";	// GetAllCorrespondences
	public static final String MEMBER_CORRESPONDENCE_PATCH_ENDPOINT			= "/api/member/members/{memberId}/interventions/{interventionId}/correspondences/{id}";	// PatchCorrespondence
	public static final String MEMBER_CORRESPONDENCE_POST_ENDPOINT			= "api/member/members/{memberId}/interventions/{interventionId}/correspondences";	// PostCorrespondence
	
	
	public static final String MEMBER_LEGACY_CORRESPONDENCE_GET_ENDPOINT		= "/api/member/members/{memberId}/legacy-correspondences/{id}";	// GetLegacyCorrespondence
	public static final String MEMBER_LEGACY_CORRESPONDENCES_GET_ENDPOINT		= "/api/member/members/{memberId}/legacy-correspondences";	// GetLegacyCorrespondences
	
	public static final String MEMBER_LEGACY_INTERVENTION_GET_ENDPOINT		= "/api/member/members/{memberId}/legacy-interventions/{id}";	// GetLegacyIntervention
	public static final String MEMBER_LEGACY_INTERVENTIONS_GET_ENDPOINT		= "/api/member/members/{memberId}/legacy-interventions";	// GetLegacyInterventions
	
	public static final String MEMBER_SIMULATIONS_GET_ENDPOINT				= "/api/member/simulations?ruleId={ruleId}&simulationRunNumber={simulationRunNumber}";	// GetSimulations
	
	public static final String MEMBER_INTERVENTION_GET_ENDPOINT				= "/api/member/members/{memberId}/interventions/{id}";	// GetIntervention
	public static final String MEMBER_INTERVENTIONS_GET_ENDPOINT			= "/api/member/members/{memberId}/interventions";	// GetInterventions
	public static final String MEMBER_INTERVENTION_SEARCH_ENDPOINT			= "/api/member/intervention-search";	// PostInterventionSearch
	
	public static final String MEMBER_INTERVENTION_NOTE_GET_ENDPOINT		= "api/member/members/{memberId}/interventions/{interventionId}/intervention-notes/{interventionNoteId}";	//GetInterventionNote
	public static final String MEMBER_INTERVENTION_NOTES_GET_ENDPOINT		= "api/member/members/{memberId}/interventions/{interventionId}/intervention-notes";	// GetInterventionNotes
	public static final String MEMBER_INTERVENTION_NOTE_POST_ENDPOINT		= "api/member/members/{memberId}/interventions/{interventionId}/intervention-notes";	// PostInterventionNote
	public static final String MEMBER_INTERVENTION_NOTE_PATCH_ENDPOINT		= "api/member/members/{memberId}/interventions/{interventionId}/intervention-notes/{interventionNoteId}";	// PatchInterventionNote
	
	/*
	 * Search Endpoints
	 */

	public static final String MEMBER_SEARCH_POST_ENDPOINT			= "/api/search/member-search/{adTenantId}";
	public static final String MEMBER_SEARCHES_POST_ENDPOINT		= "/api/search/member-search";
	public static final String PROVIDER_SEARCH_POST_ENDPOINT		= "/api/provider/provider-search";
	public static final String PROVIDER_FIELD_SEARCH_POST_ENDPOINT	= "/api/provider/field-search";
	
	/*
	 * Ndc Endpoints
	 */
	public static final String NDC_SEARCH_POST_ENDPOINT				= "/api/ndc/ndc-search";
	public static final String NDC_FIELD_SEARCH_POST_ENDPOINT		= "/api/ndc/field-search";
	
	/*
	 * User Endpoints
	 */
	public static final String USER_GET_ENDPOINT					= "/api/user/users/me"; //	GetUser
	public static final String USER_PATCH_ENDPOINT					= "/api/user/users/me"; //	PatchUser
	
	/*
	 * Intervention Batch Endpoints
	 */
	public static final String INTERVENTIONBATCH_GET_ENDPOINT		= "/api/intervention-batch/{batchId}"; //	GetInterventionBatch
	
	/*
	 * Intervention Form Endpoints 
	 */
	public static final String INTERVENTION_FORM_GET_ENDPOINT			= "api/intervention-form/intervention-forms/{id}"; //	GetInterventionForm
	public static final String INTERVENTION_FORM_VERSIONS_GET_ENDPOINT	= "api/intervention-form/intervention-forms/{formId}/versions"; //	GetInterventionFormVersions

	/*
	 * Intervention Rule Endpoints 
	 */
	public static final String INTERVENTION_RULE_GET_ENDPOINT			= "api/intervention-rule/intervention-rules/{id}"; //	GetInterventionRule
	public static final String INTERVENTION_RULE_POST_ENDPOINT			= "api/intervention-rule/intervention-rules"; //	PostInterventionRule
	
	/*
	 * FaxRequest Endpoints
	 */

	public static final String FAX_REQUEST_GET_ENDPOINT				= "/api/fax-request/fax-requests/{faxRequestId}/phone-numbers/{phoneNumber}"; //	GetFaxRequest
	public static final String FAX_REQUESTS_GET_ENDPOINT			= "/api/fax-request/fax-requests"; //	GetFaxRequests
	public static final String FAX_REQUESTS_POST_ENDPOINT			= "/api/fax-request/fax-requests";
	
	/*
	 * Formulary Endpoints
	 */

	public static final String FORMULARY_GET_ENDPOINT				= "/api/formulary/formularies/{formularyId}"; //	GetFormulary
	public static final String FORMULARIES_GET_ENDPOINT				= "/api/formulary/formularies";//	GetFormularies
	public static final String FORMULARY_INSERT_POST_ENDPOINT		= "/api/formulary/formularies";//	InsertFormulary
	public static final String FORMULARY_PATCH_ENDPOINT				= "/api/formulary/formularies/{formularyId}"; //	PatchFormulary
	public static final String GROUP_GET_ENDPOINT				    = "/api/formulary/formularies/{formularyId}/groups/{id}"; //	GetGroup
	public static final String GROUPS_GET_ENDPOINT			        = "/api/formulary/formularies/{formularyId}/groups";//	GetGroups
	
	/*
	 * Fax Template Endpoints
	 */

	public static final String FAX_TEMPLATE_GET_ENDPOINT				= "/api/fax-template/fax-templates/{id}"; //	GetFaxTemplate
	public static final String FAX_TEMPLATES_GET_ENDPOINT				= "/api/fax-template/fax-templates";//	GetFaxTemplates
	
	/*
	 * ExtraFax Endpoints
	 */

	public static final String SERVER_STATUSES_GET_ENDPOINT				= "/api/extrafax/server_statuses"; //	GetServerStatuses
	
	/*
	 * PDF Endpoints
	 */
	public static final String POPULATE_PDF_FORM_POST_ENDPOINT 			= "/api/pdf/populate/form";  //Generates PDF document for Preview Fax and Send Fax
	
	/*
	 * HTTP Messages
	 */
	public static final String HTTP_200_OK					= "HTTP/1.1 200 OK";
	public static final String HTTP_201_CONTENT_CREATED		= "HTTP/1.1 201 Created";
	public static final String HTTP_204_NO_CONTENT			= "HTTP/1.1 204 No Content";
	public static final String HTTP_400_BAD_REQUEST			= "HTTP/1.1 400 Bad Request";
	public static final String HTTP_401_UNAUTHORIZED		= "HTTP/1.1 401 Unauthorized";
	public static final String HTTP_404_NOT_FOUND           = "HTTP/1.1 404 Not Found";
	public static final String HTTP_404_RESOURCE_NOT_FOUND  = "HTTP/1.1 404 Resource Not Found";
	public static final String HTTP_500_INTERNAL_SERVER_ERR = "HTTP/1.1 500 Internal Server Error";

	/*
	 * Common messages
	 */
	
	public static final String UNAUTHORIZED_MSG		= "APIM - All APIs - Invalid Token";

	@BeforeAll
	public static void restAssuredAPIUrl()
	{
		// set API URL
		RestAssured.baseURI  = (String) BeanLoader.loadBean(BeanNames.API_URL);
		
		// Set logging
		RestAssured.filters(RequestLoggingFilter.with(LogDetail.URI, LogDetail.METHOD, LogDetail.HEADERS, LogDetail.PARAMS, LogDetail.BODY));
	}

	/**
	 * Reset the API information before running the test
	 * @author Garrett Cosmiano
	 * @since 10/04/22
	 */
	@BeforeEach
	public void resetApiInfo()
	{
		RequestLoggingFilter.resetApiInfo();
	}

	/*
	 * REST GET - valid request
	 */

	/**
	 * REST API GET
	 * 
	 * @param <T> Class type
	 * @param headers (required) REST API header
	 * @param endpoint (required) REST API URL
	 * @param params (optional) URL parameters
	 * @param responseJsonDTO (required) Class type to convert the Response body
	 * @param expectedResponseStatuCode (optional) expected status code
	 * @param expectedResponseStatusLine (optional) expected status line
	 * @return JSON objects of class type provided
	 * @deprecated Use {@link }
	 */
	@Deprecated
	protected <T> T restGet(Headers headers, String endpoint, Object[] params, Class<T> responseJsonDTO, Integer expectedResponseStatuCode, String expectedResponseStatusLine)
	{
		Response response = restGet(headers, endpoint, params);
		
		return extractJsonDTO(response, responseJsonDTO, expectedResponseStatuCode, expectedResponseStatusLine);
	}
	
	/**
	 * REST API GET
	 * 
	 * @param <T> Class type
	 * @param headers (required) REST API header
	 * @param endpoint (required) REST API URL
	 * @param params (optional) URL parameters
	 * @param responseJsonDTO (required) Class type to convert the Response body
	 * @param expectedResponseStatuCode (optional) expected status code
	 * @param expectedResponseStatusLine (optional) expected status line
	 * @return List of JSON objects of class type provided
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected <T> List<T> restGets(Headers headers, String endpoint, Object[] params, Class<T> responseJsonDTO, Integer expectedResponseStatuCode, String expectedResponseStatusLine)
	{
		Response response = restGet(headers, endpoint, params);
		
		return extractJsonDTOs(response, responseJsonDTO, expectedResponseStatuCode, expectedResponseStatusLine);
	}
	
	/**
	 * REST API GET
	 * 
	 * @param headers (required) REST API header
	 * @param endpoint (required) REST API URL
	 * @param params (optional) URL parameters
	 * @return {@link Response}
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	public static Response restGet(Headers headers, String endpoint, Object[] params)
	{
		Response response;
		
		if (  params != null && params.length > 0 )
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.get(endpoint, params);
		}
		else
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.get(endpoint);
		}
			
		return response;
	}

	
	/*
	 * REST POST - valid request
	 */
	
	/**
	 * REST API POST
	 * 
	 * @param <T> Class type
	 * @param headers (required) REST API header
	 * @param endpoint (required) REST API URL
	 * @param requestBody (optional) REST API request body
	 * @param params (optional) URL parameters
	 * @param responseJsonDTO (required) Class type to convert the Response body
	 * @param expectedResponseStatuCode (optional) expected status code
	 * @param expectedResponseStatusLine (optional) expected status line
	 * @return JSON object of class type provided
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected <T> T restPost(Headers headers, String endpoint, String requestBody, Object[] params, Class<T> responseJsonDTO, Integer expectedResponseStatuCode, String expectedResponseStatusLine)
	{
		Response response = restPost(headers, endpoint, requestBody, params);
		
		return extractJsonDTO(response, responseJsonDTO, expectedResponseStatuCode, expectedResponseStatusLine);
	}

	/**
	 * REST API POST
	 * 
	 * @param <T> Class type
	 * @param headers (required) REST API header
	 * @param endpoint (required) REST API URL
	 * @param requestBody (optional) REST API request body
	 * @param params (optional) URL parameters
	 * @param responseJsonDTO (required) Class type to convert the Response body
	 * @param expectedResponseStatuCode (optional) expected status code
	 * @param expectedResponseStatusLine (optional) expected status line
	 * @return List of JSON objects of class type provided
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected <T> List<T> restPosts(Headers headers, String endpoint, String requestBody, Object[] params, Class<T> responseJsonDTO, Integer expectedResponseStatuCode, String expectedResponseStatusLine)
	{
		Response response = restPost(headers, endpoint, requestBody, params);
		
		return extractJsonDTOs(response, responseJsonDTO, expectedResponseStatuCode, expectedResponseStatusLine);
	}
	
	/**
	 * REST API POST
	 * 
	 * @param headers (required) REST API header
	 * @param endpoint (required) REST API URL
	 * @param requestBody (optional) REST API request body
	 * @param params (optional) URL parameters
	 * @return RestAssured API response
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	public static Response restPost(Headers headers, String endpoint, String requestBody, Object[] params)
	{
		Response response;
		
		if ( StringUtils.isNotBlank(requestBody) && (params == null || params.length == 0) )
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.and()
							.body(requestBody)
							.post(endpoint);
		}
		else if ( StringUtils.isNotBlank(requestBody) && params != null && params.length > 0 )
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.and()
							.body(requestBody)
							.post(endpoint, params);
		}
		else if ( StringUtils.isBlank(requestBody) && params != null && params.length > 0 )
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.post(endpoint, params);
		}
		
		else
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.post(endpoint);
		}
		
		return response;
	}
	
	
	/*
	 * REST PATCH - valid request
	 */

	/**
	 * REST API PATCH
	 * 
	 * @param <T> Class type
	 * @param headers (required) REST API header
	 * @param endpoint (required) REST API URL
	 * @param requestBody (optional) REST API request body
	 * @param params (optional) URL parameters
	 * @param responseJsonDTO (required) Class type to convert the Response body
	 * @param expectedResponseStatuCode (optional) expected status code
	 * @param expectedResponseStatusLine (optional) expected status line
	 * @return JSON object
	 * @deprecated Use {@link ApiPatchStep}
	 */
	@Deprecated
	protected <T> T restPatch(Headers headers, String endpoint, String requestBody, Object[] params, Class<T> responseJsonDTO, Integer expectedResponseStatuCode, String expectedResponseStatusLine)
	{
		Response response = restPatch(headers, endpoint, requestBody, params);
		
		return extractJsonDTO(response, responseJsonDTO, expectedResponseStatuCode, expectedResponseStatusLine);
	}
	
	/**
	 * REST API PATCH
	 * 
	 * @param headers (required) REST API header
	 * @param endpoint (required) REST API URL
	 * @param requestBody (optional) REST API request body
	 * @param params (optional) URL parameters
	 * @return RestAssured API response
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected Response restPatch(Headers headers, String endpoint, String requestBody, Object[] params)
	{
		Response response;
		
		if ( StringUtils.isNotBlank(requestBody) && (params == null || params.length == 0) )
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.and()
							.body(requestBody)
							.patch(endpoint);
		}
		else if ( StringUtils.isNotBlank(requestBody) && params != null && params.length > 0 )
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.and()
							.body(requestBody)
							.patch(endpoint, params);
		}
		else if ( StringUtils.isBlank(requestBody) && params != null && params.length > 0 )
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.patch(endpoint, params);
		}
		else
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.patch(endpoint);
		}
		
		return response;
	}
	
	
	/*
	 * REST DELETE - valid request
	 */
	
	/**
	 * REST API DELETE
	 * 
	 * @param headers (required) REST API header
	 * @param endpoint (required) REST API URL
	 * @param params (optional) URL parameters
	 * @param expectedStatuCode (optional) expected status code
	 * @return RestAssured response
	 * @deprecated Use {@link ApiDeleteStep}
	 */
	@Deprecated
	protected Response restDelete(Headers headers, String endpoint, Object[] params, Integer expectedStatuCode)
	{
		Response response;
		
		if ( params != null && params.length > 0 )
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.and()
							.delete(endpoint, params);
		}
		else
		{
			response = 	given()
					.relaxedHTTPSValidation()
					.headers(headers)
					.and()
					.delete(endpoint);
		}

		if ( expectedStatuCode != null ) {
			assertThat(RequestLoggingFilter.getApiInfo(), response.statusCode(), is(expectedStatuCode) );
		}
		
		return response;
	}
	
	/**
	 * REST API PUT
	 * 
	 * @param headers (required) REST API header
	 * @param endpoint (required) REST API URL
	 * @param body (optional) URL parameters
	 * @param params (optional) URL parameters
	 * @param expectedStatuCode (optional) expected status code
	 * @return RestAssured response
	 * @deprecated Use {@link ApiPutStep}
	 */
	@Deprecated
	protected Response restPut(Headers headers, String endpoint, String body, Object[] params, Integer expectedStatuCode)
	{
		Response response;
		
		
		if ( StringUtils.isNotBlank(body) && params != null && params.length > 0 )
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.and()
							.body(body)
							.put(endpoint, params);
		}
		else if ( StringUtils.isNotBlank(body) && (params == null || params.length == 0) )
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.and()
							.body(body)
							.put(endpoint);
		}
		else if ( StringUtils.isBlank(body) && params != null && params.length > 0 )
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.and()
							.put(endpoint, params);
		}
		else
		{
			response = 	given()
							.relaxedHTTPSValidation()
							.headers(headers)
							.and()
							.put(endpoint);
		}

		if ( expectedStatuCode != null ) {
			assertThat(RequestLoggingFilter.getApiInfo(), response.statusCode(), is(expectedStatuCode) );
		}
		
		return response;
	}
	
	/*
	 * JSON helper methods
	 */
	
	/**
	 * Extract JSON object from the response.
	 * Also provide validation for API response status code and line
	 * 
	 *  @param <T>  Class type
	 * @param response (required) RestAssured API response
	 * @param responseJsonDTO (required) Class type to convert the Response body
	 * @param expectedStatuCode (optional) expected status code
	 * @return JSON objects of class type provided. Returns null if the response body is empty.
	 * @deprecated Use API steps to convert/extract JSON DTO from API response
	 */
	@Deprecated
	public static <T> T extractJsonDTO(Response response, Class<T> responseJsonDTO, Integer expectedStatuCode, String expectedStatusLine)
	{
		String apiInfo = RequestLoggingFilter.getApiInfo();

		if ( expectedStatuCode != null ) {
			assertThat(apiInfo, response.statusCode(), is(expectedStatuCode) );
		}
		
		if ( StringUtils.isNotBlank(expectedStatusLine) ) {
			assertThat(apiInfo, response.statusLine(), is(expectedStatusLine) );
		}

		if ( response.getBody() != null && StringUtils.isNotBlank(response.getBody().asString()) ) {
			logger.debug(response.getBody().asString());
			return response.getBody().as(responseJsonDTO);
		}

		return null;
	}
	
	/**
	 * Extract JSON object from the response.
	 * Also provide validation for API response status code and line
	 *  
	 * @param <T>  Class type
	 * @param response (required) RestAssured API response
	 * @param responseJsonDTO (required) Class type to convert the Response body
	 * @param expectedStatuCode (optional) expected status code
	 * @return List of JSON objects of class type provided. Returns null if the response body is empty.
	 * @deprecated Use API steps to convert/extract JSON DTO from API response
	 */
	@Deprecated
	protected <T> List<T> extractJsonDTOs(Response response, Class<T> responseJsonDTO, Integer expectedStatuCode, String expectedStatusLine)
	{
		String apiInfo = RequestLoggingFilter.getApiInfo();

		if ( expectedStatuCode != null ) {
			assertThat(apiInfo, response.statusCode(), is(expectedStatuCode) );
		}
		
		if ( StringUtils.isNotBlank(expectedStatusLine) ) {
			assertThat(apiInfo, response.statusLine(), is(expectedStatusLine) );
		}
		
		if ( response.getBody() != null && StringUtils.isNotBlank(response.getBody().asString()) ) {
			logger.debug(response.getBody().asString());
			return response.then().extract().jsonPath().getList(".", responseJsonDTO);
		}

		return null;
	}
	
	
	/*
	 * REST GET - invalid request
	 */
	
	/**
	 * REST API GET - invalid path parameter values.
	 * Expected to get HTTP 404 (not found)
	 *
	 * @param path The path to send the request to.
	 * @param pathParams The path parameters.
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	public void invalidPathParamValueRestGet(String path, Object... pathParams)
	{
		given()
			.relaxedHTTPSValidation()
			.headers( getGenericHeaders() )
			.when()
				.get(path, pathParams)
			.then()
				.assertThat().statusCode(404)
				.assertThat().statusLine(HTTP_404_NOT_FOUND);
	}


	/**
	 * REST API GET - invalid token.
	 * Expected to get HTTP 401 response code (Unauthorized)
	 *
	 * @param path The path to send the request to.
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected void invalidTokenRestGet(String path)
	{
		given()
			.relaxedHTTPSValidation()
			.headers( getHeadersInvalidAuth() )
			.when()
				.get(path)
			.then()
				.assertThat().statusCode(401)
				.assertThat().statusLine(HTTP_401_UNAUTHORIZED);
	}


	/**
	 * REST API GET - invalid token.
	 * Expected to get HTTP 401 response code (Unauthorized)
	 *
	 * @param path The path to send the request to.
	 * @param pathParams The path parameters.
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected void invalidTokenRestGet(String path, Object... pathParams)
	{
		given()
				.relaxedHTTPSValidation()
				.headers( getHeadersInvalidAuth() )
				.when()
				.get(path, pathParams)
				.then()
				.assertThat().statusCode(401)
				.assertThat().statusLine(HTTP_401_UNAUTHORIZED);
	}


	/**
	 * REST API GET - use this for invalid method REST API call.
	 * Expected to get HTTP 404 response code (resource not found)
	 *
	 * @param path The path to send the request to.
	 * @param body the body to be sent as part of the POST
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected void invalidRestApiGetMethod(String path, String body, Object... pathParams)
	{
		given()
				.relaxedHTTPSValidation()
				.headers( getGenericHeaders() )
				.and()
				.body(body)
				.get(path, pathParams)
				.then()
				.assertThat().statusCode(404)
				.assertThat().statusLine(HTTP_404_RESOURCE_NOT_FOUND);
	}
	

	/**
	 * REST API GET - uses invalid method
	 * @param path The path to send the request to.
	 * @param theHeaders header to use
	 * @param statusCode expected status code
	 * @param statusLine expected status message
	 * @param body to be sent with the request
	 * @param pathParams parameters to be sent with the request
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected void invalidRestApiGetMethod(String path, Headers theHeaders, int statusCode, String statusLine, String body, Object... pathParams)
	{
		given()
				.relaxedHTTPSValidation()
				.headers( getGenericHeaders(theHeaders) )
				.and()
				.body(body)
				.get(path, pathParams)
				.then()
				.assertThat().statusCode(statusCode)
				.assertThat().statusLine(statusLine);
	}
	

	/**
	 * REST API GET - use this for invalid method REST API call.
	 * 
	 * @author msharma
	 * @since 03/25/2022
	 * 
	 * @param path The path to send the request to.
	 * @param pathParams The path parameters.
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected void invalidRestApiGetMethodWithoutBody(String path, Headers theHeaders, int statusCode, String statusLine,  Object... pathParams)
	{
		given()
				.relaxedHTTPSValidation()
				.headers( getGenericHeaders(theHeaders) )
				.and()
				.get(path, pathParams)
				.then()
				.assertThat().statusCode(statusCode)
				.assertThat().statusLine(statusLine);
	}
	

	/**
	 * REST API GET - required body missing
	 * @param path to send the request to
	 * @param statusCode expected status code
	 * @param statusLine expected status message
	 * @param pathParams parameters to be sent with the request
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected void invalidRestApiGetMethodWithoutBody(String path,  int statusCode, String statusLine,  Object... pathParams)
	{
		given()
				.relaxedHTTPSValidation()
				.headers( getGenericHeaders() )
				.and()
				.get(path, pathParams)
				.then()
				.assertThat().statusCode(statusCode)
				.assertThat().statusLine(statusLine);
	}
	
	
	/**
	 * @author msharma
	 * @since 08/17/2022
	 * 
	 * REST API GET - required header & body missing
	 * @param path to send the request to
	 * @param statusCode expected status code
	 * @param statusLine expected status message
	 * @param pathParams parameters to be sent with the request
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected void invalidRestApiGetMethodWithoutBodyAndHeader(String path,  int statusCode, String statusLine,  Object... pathParams)
	{
		given()
				.relaxedHTTPSValidation()
				.and()
				.get(path, pathParams)
				.then()
				.assertThat().statusCode(statusCode)
				.assertThat().statusLine(statusLine);
	}


	/**
	 * REST API GET - invalid token.
	 * Expected to get HTTP 401 response code (Unauthorized)
	 *
	 * @param path The path to send the request to.
	 * @param theHeaders of the API GET
	 * @param pathParams The path parameters.
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected void invalidTokenRestGet(String path, Headers theHeaders, Object... pathParams)
	{
		given()
				.relaxedHTTPSValidation()
				.headers( getHeadersInvalidAuth(theHeaders) )
				.and()
				.get(path, pathParams)
				.then()
				.assertThat().statusCode(401)
				.assertThat().statusLine(HTTP_401_UNAUTHORIZED);
	}
	


	/*
	 * REST POST - invalid request
	 */


	/**
	 * REST API POST - use this for invalid method REST API call.
	 * Expected to get HTTP 404 response code (resource not found)
	 *
	 * @param path The path to send the request to.
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected void invalidRestApiPostMethod(String path)
	{
		given()
			.relaxedHTTPSValidation()
			.headers( getGenericHeaders() )
			.when()
				.post(path)
			.then()
				.assertThat().statusCode(404)
				.assertThat().statusLine(HTTP_404_RESOURCE_NOT_FOUND);
	}


	/**
	 * REST API POST - use this for invalid method REST API.
	 * Expected to get HTTP 404 response code (resource not found)
	 *
	 * @param path The path to send the request to.
	 * @param pathParams The path parameters.
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected void invalidRestApiPostMethod(String path, Object... pathParams)
	{
		given()
				.relaxedHTTPSValidation()
				.headers( getGenericHeaders() )
				.and()
				.post(path, pathParams)
				.then()
				.assertThat().statusCode(404)
				.assertThat().statusLine(HTTP_404_RESOURCE_NOT_FOUND);
	}


	/**
	 * REST API POST - invalid token.
	 * Expected to get HTTP 401 response code (Unauthorized)
	 *
	 * @param path The path to send the request to.
	 * @param body the body to be sent as part of the POST
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected void invalidTokenRestPost(String path, String body)
	{
		given()
				.relaxedHTTPSValidation()
				.headers( getHeadersInvalidAuth() )
				.and()
				.body(body)
				.post(path)
				.then()
				.assertThat().statusCode(401)
				.assertThat().statusLine(HTTP_401_UNAUTHORIZED);
	}


	/**
	 * REST API POST - invalid token.
	 * Expected to get HTTP 401 response code (Unauthorized)
	 *
	 * @param path The path to send the request to.
	 * @param body the body to be sent as part of the POST
	 * @param pathParams The path parameters.
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected void invalidTokenRestPost(String path, String body, Object... pathParams)
	{
		given()
				.relaxedHTTPSValidation()
				.headers( getHeadersInvalidAuth() )
				.and()
				.body(body)
				.post(path, pathParams)
				.then()
				.assertThat().statusCode(401)
				.assertThat().statusLine(HTTP_401_UNAUTHORIZED);
	}
	

	/**
	 * REST API POST - invalid token.
	 * Expected to get HTTP 401 response code (Unauthorized)
	 *
	 * @param path The path to send the request to.
	 * @param theHeaders of the API POST
	 * @param body the body to be sent as part of the POST
	 * @param pathParams The path parameters.
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	protected void invalidTokenRestPost(String path, Headers theHeaders, String body, Object... pathParams)
	{
		given()
				.relaxedHTTPSValidation()
				.headers( getHeadersInvalidAuth(theHeaders) )
				.and()
				.body(body)
				.post(path, pathParams)
				.then()
				.assertThat().statusCode(401)
				.assertThat().statusLine(HTTP_401_UNAUTHORIZED);
	}


	/**
	 * REST API POST - use this when validating invalid path param or invalid method
	 * Expected to get HTTP 404 (not found)
	 *
	 * @author msharma
	 * @since 03/28/22
	 * @param path The path to send the request to.
	 * @param theHeaders of the API
	 * @param statusCode expected to be returned
	 * @param statusLine expected to be returned
	 * @param pathParams The path parameters.
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	public void invalidRestPost(String path, Headers theHeaders,  int statusCode, String statusLine, Object... pathParams)
	{
		given()
				.relaxedHTTPSValidation()
				.headers( getGenericHeaders(theHeaders) )
				.and()
				.post(path, pathParams)
				.then()
				.assertThat().statusCode(statusCode)
				.assertThat().statusLine(statusLine);
	}
	

	/**
	 * REST API POST - use this when validating invalid path param or post body
	 * Expected to get HTTP 404 (not found)
	 *
	 * @param path The path to send the request to.
	 * @param body the body to be sent as part of the POST
	 * @param pathParams The path parameters.
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	public void invalidRestPost(String path, String body, int statusCode, String statusLine, Object... pathParams)
	{
		given()
				.relaxedHTTPSValidation()
				.headers( getGenericHeaders() )
				.and()
				.body(body)
				.post(path, pathParams)
				.then()
				.assertThat().statusCode(statusCode)
				.assertThat().statusLine(statusLine);
	}


	/**
	 * REST API POST - use this when validating invalid path param or post body
	 * Expected to get HTTP 404 (not found)
	 *
	 * @author gcosmian
	 * @since 03/15/22
	 * @param path The path to send the request to.
	 * @param body the body to be sent as part of the POST
	 * @param pathParams The path parameters.
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	public void invalidRestPost(String path, Headers theHeaders, String body, int statusCode, String statusLine, Object... pathParams)
	{
		given()
				.relaxedHTTPSValidation()
				.headers( getGenericHeaders(theHeaders) )
				.and()
				.body(body)
				.post(path, pathParams)
				.then()
				.assertThat().statusCode(statusCode)
				.assertThat().statusLine(statusLine);
	}


	/**
	 * REST API POST - use this when validating invalid post body without pathParams
	 * 
	 * @author msharma
	 * @since 04/15/2022
	 *
	 * @param path The path to send the request to.
	 * @param body the body to be sent as part of the POST
	 * @deprecated Use {@link ApiPostStep}
	 */
	@Deprecated
	public void invalidRestPost(String path,int statusCode, String statusLine, String body)
	{
		given()
				.relaxedHTTPSValidation()
				.headers( getGenericHeaders() )
				.and()
				.body(body)
				.post(path)
				.then()
				.assertThat().statusCode(statusCode)
				.assertThat().statusLine(statusLine);
	}
	

	
	/*
	 * API headers
	 */
	

	/**
	 * Retrieve generic and common headers which includes Bearer authorization
	 * 
	 * @return {@link Headers}
	 */
	public static Headers getGenericHeaders()
	{
		Header authorization = new Header("Authorization", "Bearer " + RxConciergeUILogin.getTokenAccess());
		Header contentType = new Header("Content-Type", ContentType.JSON.toString());
		Header accept = new Header("Accept", ContentType.JSON.toString());
		
		return new Headers(authorization, contentType, accept);
	}


	/**
	 * Retrieve generic and common headers plus additional param headers
	 * 
	 * @param theHeaders additional headers to be added on top of the generic headers
	 * @return {@link Headers}
	 */
	protected Headers getGenericHeaders(Header... theHeaders)
	{
		Headers headers = new Headers(Arrays.asList(theHeaders));
		
		return getGenericHeaders(headers);
	}
	

	/**
	 * Retrieve generic and common headers plus additional param headers
	 * 
	 * @param theHeaders additional headers to be added on top of the generic headers
	 * @return {@link Headers}
	 */
	protected Headers getGenericHeaders(Headers theHeaders)
	{
		LinkedList<Header> headerList =  new LinkedList<>();	// list of all headers

		// Generic headers
		for ( Header header : getGenericHeaders() )
		{
			headerList.add(header);
		}
		
		// Additional headers
		for ( Header header : theHeaders )
		{
			headerList.add(header);
		}
		
		return new Headers(headerList);
	}
	

	/*
	 * API headers with invalid authorization
	 */
	

	/**
	 * Retrieve generic and common headers with invalid Bearer authorization
	 * 
	 * @return {@link Headers}
	 */
	protected Headers getHeadersInvalidAuth()
	{
		Header authorization = new Header("Authorization", "Bearer " + RxConciergeUILogin.getTokenAccess() + "invalid");
		Header contentType = new Header("Content-Type", ContentType.JSON.toString());
		Header accept = new Header("Accept", ContentType.JSON.toString());
		
		return new Headers(authorization, contentType, accept);
	}
	

	/**
	 * Retrieve generic and common headers with invalid Bearer authorization plus additional param headers
	 * 
	 * @param theHeaders additional headers to be added on top of the generic headers
	 * @return {@link Headers}
	 */
	protected Headers getHeadersInvalidAuth(Header... theHeaders)
	{
		Headers headers = new Headers(Arrays.asList(theHeaders));
		return getHeadersInvalidAuth(headers);
	}
	

	/**
	 * Retrieve generic and common headers with invalid Bearer authorization plus additional param headers
	 * 
	 * @param theHeaders additional headers to be added on top of the generic headers
	 * @return {@link Headers}
	 */
	protected Headers getHeadersInvalidAuth(Headers theHeaders)
	{
		LinkedList<Header> headerList = new LinkedList<>();	// list of all headers
		
		// Generic headers
		for ( Header header : getHeadersInvalidAuth() )
		{
			headerList.add(header);
		}
		
		// Additional headers
		for ( Header header : theHeaders ) 
		{
			headerList.add(header);
		}
		
		return new Headers(headerList);
	}

	public static TestConfiguration getTestConfiguration()
	{
		if ( testExecutionUserList == null || testExecutionUserList.size() == 0 )
		{
			throw new TestConfigurationException("The VM arg TestUserRoleFilter is required. Please update VM arg TestUserRoleFilter with single account");
		}
		else if ( testExecutionUserList.size() > 1 )
		{
			throw new TestConfigurationException("The test suite can only be run with a single account. Please update the VM arg TestUserRoleFilter with single account");
		}

		return BeanLoader.loadBean(testExecutionUserList.get(0), TestConfiguration.class);
	}

	@Override
	public List<IUserConfiguration> convertUserToUserConfiguration()
	{
		return null;	// not implemented
	}
	
}

