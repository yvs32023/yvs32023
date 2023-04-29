/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.interventionrule;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

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

import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.cosmos.InterventionRuleQueries;
import com.excellus.sqa.rxcc.dto.InterventionRule;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Headers;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 12/27/2022
 * 
 * PostInterventionRule
 * 
 * Insert a new interventionrule type interventionrule.
 * 
 * Post https://apim-lbs-rxc-dev-east-001.azure-api.net/api/intervention-rule/intervention-rules
 *
 * {
    "ruleName": "string",
    "ruleStatus": integer,
    "averageLengthTherapy": integer,
    "savingsInformationText": "string",
    "ageAndBelow": integer,
    "ageAndGreater": integer,
    "daysSupply": integer,
    "daysSupplySign": "string",
    "costThresholdAmount": number,
    "costThresholdSign": "string",
    "interventionType": "string",
    "runDailyInd": boolean,
    "acceptText": "string",
    "declineText": "string",
    "qualityTitleText": "string",
    "qualityFreeformTargetPrerequisiteInd": integer,
    "qualityFreeFormText": "string",
    "qualityInterventionText": "string",
    "formularyTenantsGroups": [
        {
            "formularyId": "string",
            "formularyCode": "string",
            "formularyDescription": "string",
            "tenantGroups": [
                {
                    "adTenantId": "string",
                    "rxccGroupName": ["string"]
                }
            ]
        }
    ],
    "target": {
        "drugSelectionFilter": {
            "searchBy": "string",
            "search": "string",
            "bgtInd": ["string"],
            "genericDrugName": "string",
            "productName": "string",
            "manufacturer": "string",
            "rxOTC": ["string"],
            "fdaCode": ["string"],
            "dosageForm": ["string"]
        },
        "claimsLookbackInitial": integer,
        "claimsLookbackOngoing": integer,
        "selectedDrugsNDC": ["string"],
        "showOnFax": boolean,
        "static": boolean
    },
    "prerequisites": [
        {
            "condition": "string",
            "drugSelectionFilter": {
                "searchBy": "string",
                "search": "string",
                "bgtInd": ["string"],
                "genericDrugName": "string",
                "productName": "string",
                "manufacturer": "string",
                "rxOTC": ["string"],
                "fdaCode": ["string"],
                "dosageForm": ["string"]
            },
            "claimsLookbackInitial": integer,
            "claimsLookbackOngoing": integer,
            "selectedDrugsNDC": ["string"],
            "showOnFax": boolean,
            "static": boolean
        }
    ],
    "alternatives": [
        {
            "condition": "string",
            "drugSelectionFilter": {
                "searchBy": "string",
                "search": "string",
                "bgtInd": ["string"],
                "genericDrugName": "string",
                "productName": "string",
                "manufacturer": "string",
                "rxOTC": ["string"],
                "fdaCode": ["string"],
                "dosageForm": ["string"]
            },
            "claimsLookbackInitial": integer,
            "claimsLookbackOngoing": integer,
            "selectedDrugsNDC": ["string"],
            "showOnFax": boolean,
            "static": boolean
        }
    ],
    "faxAlternativeText": "string",
    "phoneFollowupInd": boolean,
}
 */
@Tag("ALL")
@Tag("INTERVENTIONRULE")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostInterventionRuleLOA")
@UserRole(role = {"RXCC_FULL_LOA","RXCC_FULL_MULTI"})
public class PostInterventionRuleLoaTest extends InterventionRuleHelper
{

	protected static final Logger logger = LoggerFactory.getLogger(PostInterventionRuleEhpTest.class);

	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("LOA: Happy Path: Post Intervention Rule")
	@Order(1)
	public List<DynamicNode> happyPathPost() throws JsonProcessingException
	{
		// Setup test data
		InterventionRule interventionRule = new InterventionRule();

		interventionRule = getNewLoaInterventionRule();

		//create api body
		String body = createApiBody(interventionRule);

		// calls api request and validation 
		return happyPath(interventionRule,body);
	}


	@TestFactory
	@DisplayName("LOA: Happy Path: Post Intervention Rule Without Target")
	@Order(2)
	public List<DynamicNode> happyPathWithoutTarget() throws JsonProcessingException
	{
		// Setup test data
		InterventionRule interventionRule = new InterventionRule();

		interventionRule = getNewLoaInterventionRule();
		interventionRule.setTarget(null);

		//create api body
		String body = createApiBody(interventionRule);

		// calls api request and validation 
		return happyPath(interventionRule,body);
	}

	@TestFactory
	@DisplayName("LOA: Happy Path: Post Intervention Rule Without Prerequisite")
	@Order(3)
	public List<DynamicNode> happyPathWithoutPrerequisite () throws JsonProcessingException
	{
		// Setup test data
		InterventionRule interventionRule = new InterventionRule();

		interventionRule = getNewLoaInterventionRule();
		interventionRule.setPrerequisites(null);

		//create api body
		String body = createApiBody(interventionRule);

		// calls api request and validation 
		return happyPath(interventionRule,body);
	}

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("LOA: Negative: Post Intervention Rule Duplicate Rule Name")
	@Order(4)
	public List<DynamicNode> ruleNameAlreadyExists() throws JsonProcessingException
	{
		//fetching the random record from cosmos db for intervention rule
		InterventionRule expected = InterventionRuleQueries.getRandomInterventionRule();

		// Setup test data
		InterventionRule interventionRule = new InterventionRule();

		interventionRule = getNewLoaInterventionRule();

		//overriding ruleName for test data after fetching random record from cosmos db
		interventionRule.setRuleName(expected.getRuleName());

		//create api body
		String body = createApiBody(interventionRule);

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), INTERVENTION_RULE_POST_ENDPOINT, body,new Object[]{}, 400, HTTP_400_BAD_REQUEST);
		apiPostStep.run();

		// validate the API message
		final String EXPECTED_MSG = "\"ruleName already exists!\"";
		String actualMsg = apiPostStep.getResponse().then().extract().asString();
		apiPostStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]",
				() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())));

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("LOA: Negative: Post Intervention Rule Either Target or Prerequisites  must be provided")
	@Order(5)
	public List<DynamicNode> targetPrerequisitesBothDontExists() throws JsonProcessingException
	{
		// Setup test data
		InterventionRule interventionRule = new InterventionRule();
		interventionRule = getNewLoaInterventionRule();

		interventionRule.setTarget(null);
		interventionRule.setPrerequisites(null);

		//create api body
		String body = createApiBody(interventionRule);

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), INTERVENTION_RULE_POST_ENDPOINT, body,new Object[]{}, 400, HTTP_400_BAD_REQUEST);
		apiPostStep.run();
		
		if ( apiPostStep.getResponseStatusCode() == 201 )
		{
			InterventionRule actualAPIResponse = apiPostStep.convertToJsonDTO(InterventionRule.class);
			toBeDeleted = actualAPIResponse;
		}

		// validate the API message
		final String EXPECTED_MSG = "\"If target is not provided then prerequisites must be provided!\"";
		String actualMsg = apiPostStep.getResponse().then().extract().asString();
		apiPostStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]",
				() -> assertTrue(actualMsg.contains(EXPECTED_MSG), apiPostStep.getApiInfo())));

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("LOA: Negative: Post Intervention Rule Alternatives  must be provided")
	@Order(6)
	public List<DynamicNode> AlternativesDontExists() throws JsonProcessingException
	{
		// Setup test data
		InterventionRule interventionRule = new InterventionRule();

		interventionRule = getNewLoaInterventionRule();
		interventionRule.setAlternatives(null);

		//create api body
		String body = createApiBody(interventionRule);

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), INTERVENTION_RULE_POST_ENDPOINT, body,new Object[]{}, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

	@TestFactory
	@DisplayName("LOA: Negative: Post Intervention Rule FormularyTenantsGroups  must be provided")
	@Order(7)
	public List<DynamicNode> FormularyTenantsGroupsDontExists() throws JsonProcessingException
	{
		// Setup test data
		InterventionRule interventionRule = new InterventionRule();

		interventionRule = getNewLoaInterventionRule();
		interventionRule.setFormularyTenantsGroups(null);

		//create api body
		String body = createApiBody(interventionRule);

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), INTERVENTION_RULE_POST_ENDPOINT, body,new Object[]{}, 500, HTTP_500_INTERNAL_SERVER_ERR);
		apiPostStep.run();
		
		if ( apiPostStep.getResponseStatusCode() == 201 )
		{
			InterventionRule actualAPIResponse = apiPostStep.convertToJsonDTO(InterventionRule.class);
			toBeDeleted = actualAPIResponse;
		}

		return apiPostStep.getTestResults();
	}


	@TestFactory
	@DisplayName("LOA: Negative: Post Intervention Rule Invalid Auth")
	@Order(8)
	public List<DynamicNode> invalidToken() throws JsonProcessingException
	{
		// Setup test data
		InterventionRule interventionRule = new InterventionRule();

		interventionRule = getNewLoaInterventionRule();
		interventionRule.setFormularyTenantsGroups(null);

		//create api body
		String body = createApiBody(interventionRule);

		//invalidHeader
		Headers headers = getHeadersInvalidAuth();

		ApiPostStep apiPostStep = new ApiPostStep(headers, INTERVENTION_RULE_POST_ENDPOINT, body,new Object[]{}, 401, HTTP_401_UNAUTHORIZED);
		apiPostStep.run();

		return apiPostStep.getTestResults();
	}

}





