/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.interventionrule;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

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

import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.InterventionRuleQueries;
import com.excellus.sqa.rxcc.dto.InterventionRule;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Headers;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 08/02/2022
 */
@SuppressWarnings("unused")
@Tag("ALL")
@Tag("INTERVENTION")
@Tag("INTERVENTIONRULE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetInterventionRule")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class GetInterventionRuleTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(GetInterventionRuleTest.class);

	@TestFactory
	@DisplayName("26546: GetInterventionRule Happy Path")
	@Order(1)
	public List<DynamicNode> happyPath() throws JsonProcessingException {

		// Cosmos db 
		InterventionRule expected = InterventionRuleQueries.getRandomInterventionRule();

		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(),	INTERVENTION_RULE_GET_ENDPOINT, new Object[] {expected.getId()}, 200, null);
		apiGetStep.run();

		InterventionRule actual = apiGetStep.convertToJsonDTO(InterventionRule.class);

		if (apiGetStep.stepStatus() != IStep.Status.COMPLETED) {
			return apiGetStep.getTestResults();
		}

		apiGetStep.getTestResults().add( dynamicContainer("GetInterventionRule DTO", expected.compare(actual)) );
		return apiGetStep.getTestResults();
	}

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("41356: GetInterventionRule Happy Path with Param Invalid")
	@Order(2)
	public List<DynamicNode> invalidParam() throws JsonProcessingException {

		String id = "invalid";

		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), INTERVENTION_RULE_GET_ENDPOINT,
				new Object[] { id }, 404, HTTP_404_NOT_FOUND);

		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("41359: GetInterventionRule Invalid Auth")
	@Order(3)
	public  List<DynamicNode> invalidAuth() throws JsonProcessingException {

		String id = UUID.randomUUID().toString();

		Headers headers = getHeadersInvalidAuth();
		ApiGetStep apiGetStep = new ApiGetStep(headers, INTERVENTION_RULE_GET_ENDPOINT, new Object[] {id}, 401, HTTP_401_UNAUTHORIZED);

		apiGetStep.run();
		return apiGetStep.getTestResults();

	}
}
