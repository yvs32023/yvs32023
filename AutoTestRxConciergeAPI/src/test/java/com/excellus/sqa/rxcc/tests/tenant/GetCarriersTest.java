/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.tenant;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.RequestLoggingFilter;
import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.TenantCarriersQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.TenantCarrier;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * Get all tenant type carriers for a tenant by {adTenantId}.
 * 
 * GET https://apim-lbs-rxc-dev-east-001.azure-api.net/api/tenant/tenants/{adTenantId}/carriers[?carrierId][&benefitHierarchyId]
 * 
 * Request parameters : adTenantId , carrierId , benefitHierarchyId
 * Required field : adTenantId
 * Optioanl field : carrierId, benefitHierarchyId
 * 
 * @author Manish Sharma (msharma)
 * @since 09/20/2022
 */
@Tag("ALL")
@Tag("TENANT")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetCarriers")
public class GetCarriersTest extends RxConciergeAPITestBaseV2 {
	private static final Logger logger = LoggerFactory.getLogger(GetCarriersTest.class);
	private String subscriptionName;

	@TestFactory
	@DisplayName("54925: (1) Tenant GetCarriers ALL Happy Path")
	@Order(1)
	public List<DynamicNode> happyPathAllParam() throws JsonProcessingException {
		List<DynamicNode> test = new ArrayList<DynamicNode>();
		List<Tenant> tenants = TenantQueries.getTenants();

		for (Tenant tenant : tenants) {
			try {
				subscriptionName = tenant.getSubscriptionName();
				TenantCarrier list = TenantCarriersQueries.getCarrierByTenantSubName(subscriptionName).get(0);

				String adTenantId = list.getAdTenantId();
				String carrierId = list.getCarrierId();
				String benefitHierarchyId = list.getBenefitHierarchyId();

				// Cosmos db
				List<TenantCarrier> expectedTenantCarriers = new ArrayList<TenantCarrier>();
				expectedTenantCarriers = TenantCarriersQueries.getCarriers(adTenantId, carrierId, benefitHierarchyId);

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)),
						TENANT_GET_CARRIERS_ENDPOINT, new Object[] { adTenantId, carrierId, benefitHierarchyId }, 200,
						null);
				apiGetStep.run();

				List<TenantCarrier> actualTenantCarriers = apiGetStep.convertToJsonDTOs(TenantCarrier.class);

				if (apiGetStep.stepStatus() != IStep.Status.COMPLETED) {
					return apiGetStep.getTestResults();
				}

				for (TenantCarrier expected : expectedTenantCarriers) {
					boolean found = false;
					for (TenantCarrier actual : actualTenantCarriers) {
						if (expected.equals(actual)) {
							found = true;
							apiGetStep.getTestResults().add(dynamicContainer(
									"adTenantId id [" + expected.getAdTenantId() + "]", expected.compare(actual)));
							break;
						}
					}

					if (!found) {
						apiGetStep.getTestResults().add(dynamicTest("adTenantId id [" + expected.getAdTenantId() + "]",
								fail("Unable to find the carrier from API response")));
					}
				}

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults()));
			} catch (Exception e) {
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add(dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),
						() -> fail(apiInfo + "\n" + e.getMessage(), e)));
			}

			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("54925: (2) Tenant GetCarriers Partial ALL Happy Path(Includes adTenantId & carrierId only)")
	@Order(2)
	public List<DynamicNode> happyPathPartialParam() throws JsonProcessingException {
		List<DynamicNode> test = new ArrayList<DynamicNode>();
		List<Tenant> tenants = TenantQueries.getTenants();

		for (Tenant tenant : tenants) {
			try {
				subscriptionName = tenant.getSubscriptionName();
				TenantCarrier list = TenantCarriersQueries.getCarrierByTenantSubName(subscriptionName).get(0);

				// Empty for benefitHierarchyId as this is non-required param query
				String adTenantId = list.getAdTenantId();
				String carrierId = list.getCarrierId();
				String benefitHierarchyId = "";

				// Cosmos db
				List<TenantCarrier> expectedTenantCarriers = new ArrayList<TenantCarrier>();
				expectedTenantCarriers = TenantCarriersQueries.getCarriers(adTenantId, carrierId, benefitHierarchyId);

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)),
						TENANT_GET_CARRIERS_ENDPOINT, new Object[] { adTenantId, carrierId, benefitHierarchyId }, 200,
						null);
				apiGetStep.run();

				List<TenantCarrier> actualTenantCarriers = apiGetStep.convertToJsonDTOs(TenantCarrier.class);

				if (apiGetStep.stepStatus() != IStep.Status.COMPLETED) {
					return apiGetStep.getTestResults();
				}

				for (TenantCarrier expected : expectedTenantCarriers) {
					boolean found = false;
					for (TenantCarrier actual : actualTenantCarriers) {
						if (expected.equals(actual)) {
							found = true;
							apiGetStep.getTestResults().add(dynamicContainer(
									"adTenantId id [" + expected.getAdTenantId() + "]", expected.compare(actual)));
							break;
						}
					}

					if (!found) {
						apiGetStep.getTestResults().add(dynamicTest("adTenantId id [" + expected.getAdTenantId() + "]",
								fail("Unable to find the carrier from API response")));
					}
				}

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults()));
			} catch (Exception e) {
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add(dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),
						() -> fail(apiInfo + "\n" + e.getMessage(), e)));
			}
			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("54925: (3) Tenant GetCarriers Partial Parameter ALL Happy Path(Includes required parameter adTenantId only)")
	@Order(3)
	public List<DynamicNode> happyPathPartialParamEHP() throws JsonProcessingException {
		List<DynamicNode> test = new ArrayList<DynamicNode>();
		List<Tenant> tenants = TenantQueries.getTenants();

		for (Tenant tenant : tenants) {
			try {
				subscriptionName = tenant.getSubscriptionName();
				TenantCarrier list = TenantCarriersQueries.getCarrierByTenantSubName(subscriptionName).get(0);

				// Only required request parameter has value
				String adTenantId = list.getAdTenantId();
				String carrierId = "";
				String benefitHierarchyId = "";

				// Cosmos db
				List<TenantCarrier> expectedTenantCarriers = new ArrayList<TenantCarrier>();
				expectedTenantCarriers = TenantCarriersQueries.getCarriers(adTenantId, carrierId, benefitHierarchyId);

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)),
						TENANT_GET_CARRIERS_ENDPOINT, new Object[] { adTenantId, carrierId, benefitHierarchyId }, 200,
						null);
				apiGetStep.run();

				List<TenantCarrier> actualTenantCarriers = apiGetStep.convertToJsonDTOs(TenantCarrier.class);

				if (apiGetStep.stepStatus() != IStep.Status.COMPLETED) {
					return apiGetStep.getTestResults();
				}

				for (TenantCarrier expected : expectedTenantCarriers) {
					boolean found = false;
					for (TenantCarrier actual : actualTenantCarriers) {
						if (expected.equals(actual)) {
							found = true;
							apiGetStep.getTestResults().add(dynamicContainer(
									"adTenantId id [" + expected.getAdTenantId() + "]", expected.compare(actual)));
							break;
						}
					}
					if (!found) {
						apiGetStep.getTestResults().add(dynamicTest("adTenantId id [" + expected.getAdTenantId() + "]",
								fail("Unable to find the carrier from API response")));
					}
				}

				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults()));
			} catch (Exception e) {
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add(dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),
						() -> fail(apiInfo + "\n" + e.getMessage(), e)));
			}
			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}

	@TestFactory
	@DisplayName("54925: (4) Tenant GetCarriers Partial Parameter ALL Happy Path(Includes adTenantId & benefitHierarchyId only)")
	@Order(4)
	public List<DynamicNode> happyPathPartialParamEXE() throws JsonProcessingException {
		List<DynamicNode> test = new ArrayList<DynamicNode>();
		List<Tenant> tenants = TenantQueries.getTenants();

		for (Tenant tenant : tenants) {
			try {
				subscriptionName = tenant.getSubscriptionName();
				TenantCarrier list = TenantCarriersQueries.getCarrierByTenantSubName(subscriptionName).get(0);

				// Empty for carrierId as this is non-required param query
				String adTenantId = list.getAdTenantId();
				String carrierId = "";
				String benefitHierarchyId = list.getBenefitHierarchyId();

				// Cosmos db
				List<TenantCarrier> expectedTenantCarriers = new ArrayList<TenantCarrier>();
				expectedTenantCarriers = TenantCarriersQueries.getCarriers(adTenantId, carrierId, benefitHierarchyId);

				// API call
				ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(new Header(API_HEADER_NAME, subscriptionName)),
						TENANT_GET_CARRIERS_ENDPOINT, new Object[] { adTenantId, carrierId, benefitHierarchyId }, 200,
						null);
				apiGetStep.run();

				List<TenantCarrier> actualTenantCarriers = apiGetStep.convertToJsonDTOs(TenantCarrier.class);

				if (apiGetStep.stepStatus() != IStep.Status.COMPLETED) {
					return apiGetStep.getTestResults();
				}

				for (TenantCarrier expected : expectedTenantCarriers) {
					boolean found = false;
					for (TenantCarrier actual : actualTenantCarriers) {
						if (expected.equals(actual)) {
							found = true;
							apiGetStep.getTestResults().add(dynamicContainer(
									"adTenantId id [" + expected.getAdTenantId() + "]", expected.compare(actual)));
							break;
						}
					}

					if (!found) {
						apiGetStep.getTestResults().add(dynamicTest("adTenantId id [" + expected.getAdTenantId() + "]",
								fail("Unable to find the carrier from API response")));
					}
				}
				test.add(dynamicContainer(subscriptionName.toUpperCase(), apiGetStep.getTestResults()));
			} catch (Exception e) {
				// Capture any test using testApiValidationResults
				String apiInfo = RequestLoggingFilter.getApiInfo();
				test.add(dynamicTest("Subscription Name :" + subscriptionName.toUpperCase(),
						() -> fail(apiInfo + "\n" + e.getMessage(), e)));
			}

			resetApiInfo(); // Reset the API information and test validation results
		}
		return test;
	}

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("54929: Tenant GetCarriers  Invalid Auth")
	@Order(5)
	public List<DynamicNode> invalidAuth() throws JsonProcessingException {

		String adTenantId = UUID.randomUUID().toString();
		String carrierId = UUID.randomUUID().toString();
		String benefitHierarchyId = UUID.randomUUID().toString();

		Headers headers = getHeadersInvalidAuth(new Header(API_HEADER_NAME, subscriptionName));
		ApiGetStep apiGetStep = new ApiGetStep(headers, TENANT_GET_CARRIERS_ENDPOINT,
				new Object[] { adTenantId, carrierId, benefitHierarchyId }, 401, HTTP_401_UNAUTHORIZED);

		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("54927: Tenant GetCarriers Invalid EndPoint")
	@Order(6)
	public List<DynamicNode> invalidEndPoint() throws JsonProcessingException {

		// invalid endpoint
		String CARRIERS_INVALID_GET_ENDPOINT = "\"/api/tenant/tenants/{adTenantId}/carriers?carrierId={carrierId}&benefitHierarchyId={benefitHierarchyId}\"";

		String adTenantId = UUID.randomUUID().toString();
		String carrierId = UUID.randomUUID().toString();
		String benefitHierarchyId = UUID.randomUUID().toString();

		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), CARRIERS_INVALID_GET_ENDPOINT,
				new Object[] { adTenantId, carrierId, benefitHierarchyId }, 404, HTTP_404_RESOURCE_NOT_FOUND);

		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("54926: Tenant GetCarriers Happy Path with Param Invalid")
	@Order(7)
	public List<DynamicNode> invalidParam() throws JsonProcessingException {

		String adTenantId = "invalid";
		String carrierId = "invalid";
		String benefitHierarchyId = "invalid";

		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), TENANT_GET_CARRIERS_ENDPOINT,
				new Object[] { adTenantId, carrierId, benefitHierarchyId }, 400, HTTP_400_BAD_REQUEST);

		apiGetStep.run();

		// validate the API message
		final String EXPECTED_MSG = "\"adTenantId " + adTenantId + " not found!\"";
		String actualMsg = apiGetStep.getResponse().then().extract().asString();
		apiGetStep.getTestResults().add(
				dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("54931: Tenant GetCarriers Required field check (adTenantId required)")
	@Order(8)
	public List<DynamicNode> requiredFieldCheck() throws JsonProcessingException {

		String adTenantId = "";
		String carrierId = "";
		String benefitHierarchyId = "";

		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), TENANT_GET_CARRIERS_ENDPOINT,
				new Object[] { adTenantId, carrierId, benefitHierarchyId }, 404, HTTP_404_NOT_FOUND);

		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("25784: Tenant GetCarriers Invalid Method")
	@Order(9)
	public List<DynamicNode> invalidMethod() {
		String adTenantId = "";
		String carrierId = "";
		String benefitHierarchyId = "";

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), TENANT_GET_CARRIERS_ENDPOINT, null,
				new Object[] { adTenantId, carrierId, benefitHierarchyId }, 404, HTTP_404_RESOURCE_NOT_FOUND);

		apiPostStep.run();
		return apiPostStep.getTestResults();
	}
}
