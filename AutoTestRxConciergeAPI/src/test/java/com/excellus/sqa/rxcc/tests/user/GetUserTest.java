/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.user;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.UserQueries;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.User;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Header;
import io.restassured.http.Headers;


/**
 * Get current logged in user by {adTenantId}.
 * 
 * GET https://apim-lbs-rxc-dev-east-001.azure-api.net/api/user/users/me
 * 
 * 
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 05/09/2022
 */
@Tag("ALL")
@Tag("USER")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetUser")
public class GetUserTest extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(GetUserTest.class);
	private String subscriptionName;

	@TestFactory
	@DisplayName("3468: GetUser Happy Path EHP")
	public List<DynamicNode> happyPathEHP() throws JsonProcessingException 
	{
		//Tenant
		subscriptionName = Tenant.Type.EHP.getSubscriptionName();

		//Cosmos DB
		User expected = UserQueries.getEHPUser(RxConciergeUILogin.getAcctName());

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)), USER_GET_ENDPOINT, null, 200, null);
		apiGetStep.run();

		User actual = apiGetStep.convertToJsonDTO(User.class);

		apiGetStep.getTestResults().add( dynamicContainer("User DTO", expected.compare(actual)) );
		return apiGetStep.getTestResults();

	}

	@TestFactory
	@DisplayName("3458: GetUser Invalid Method")
	@Order(2)
	public List<DynamicNode> invalidRestApiPostMethod()
	{
		subscriptionName = Tenant.Type.EHP.getSubscriptionName();

		// API call
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), USER_GET_ENDPOINT, null, null, 404, HTTP_404_RESOURCE_NOT_FOUND);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("3465: GetUser Header Missing")
	@Order(3)
	public List<DynamicNode> missingHeader()
	{		
		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), USER_GET_ENDPOINT, null, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("3464: GetUser Invalid Header Value")
	@Order(4)
	public List<DynamicNode>  incorrectHeaderValue()
	{
		Headers headers = getGenericHeaders(new Header(API_HEADER_NAME, "abc"));
		ApiGetStep apiGetStep = new ApiGetStep(headers, USER_GET_ENDPOINT, null, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("3471: GetUser Invalid Auth")
	@Order(5)
	public List<DynamicNode> invalidAuth()
	{
		subscriptionName = Tenant.Type.EHP.getSubscriptionName();

		Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionName));
		ApiGetStep apiGetStep = new ApiGetStep(headers, USER_GET_ENDPOINT, null, 401, HTTP_401_UNAUTHORIZED);

		apiGetStep.run();

		return apiGetStep.getTestResults();
	}

}


