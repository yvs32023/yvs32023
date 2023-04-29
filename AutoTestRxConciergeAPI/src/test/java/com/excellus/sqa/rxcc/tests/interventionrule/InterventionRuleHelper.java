/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.interventionrule;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.FaxTemplateQueries;
import com.excellus.sqa.rxcc.cosmos.InterventionRuleQueries;
import com.excellus.sqa.rxcc.dto.Alternatives;
import com.excellus.sqa.rxcc.dto.DrugSelectionFilter;
import com.excellus.sqa.rxcc.dto.DrugSelectionFilter.BgtInd;
import com.excellus.sqa.rxcc.dto.DrugSelectionFilter.RxOTC;
import com.excellus.sqa.rxcc.dto.DrugSelectionFilter.SearchBy;
import com.excellus.sqa.rxcc.dto.FaxTemplate;
import com.excellus.sqa.rxcc.dto.FormularyTenantsGroups;
import com.excellus.sqa.rxcc.dto.InterventionRule;
import com.excellus.sqa.rxcc.dto.InterventionRule.QualityFreeformTargetPrerequisiteInd;
import com.excellus.sqa.rxcc.dto.InterventionRule.RuleStatus;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.Prerequisites;
import com.excellus.sqa.rxcc.dto.Prerequisites.Condition;
import com.excellus.sqa.rxcc.dto.Target;
import com.excellus.sqa.rxcc.dto.TenantGroups;
import com.excellus.sqa.step.IStep;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import io.restassured.http.Headers;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 12/13/2022
 */

public class InterventionRuleHelper extends RxConciergeAPITestBaseV2
{
	protected static final Logger logger = LoggerFactory.getLogger(InterventionRuleHelper.class);

	protected String createdDateTime;
	protected InterventionRule toBeDeleted;

	/**
	 * Setup the created date/time
	 */
	@BeforeEach
	public void dataSetup()
	{	
		try {
			createdDateTime = DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE);
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}
	}

	/**
	 *  Delete any member correspondence that was created with the test
	 */
	@AfterEach
	public void deleteMemberCorrespondenceFromCosmos()
	{

		if ( toBeDeleted != null ) 
		{
			InterventionRuleQueries.deleteInterventionRule( toBeDeleted.getId(),toBeDeleted.getRuleId());
		}

		toBeDeleted = null;

	}

	//Creating Generic Test Data for ALL Tenants
	public InterventionRule genericInterventionRule(List<FormularyTenantsGroups> formularyTenantsGroups)
	{

		// prerequisitesMap
		List<Prerequisites> prerequisitesList = new ArrayList<>();

		DrugSelectionFilter drugSelectionFilter = new DrugSelectionFilter (SearchBy.gpi, "01100010", Arrays.asList(BgtInd.B, BgtInd.G, BgtInd.T), "Penicillin G Pot in Dextrose Solution", "", "SANDOZ", Arrays.asList(RxOTC.O), Arrays.asList("2987654321"), Arrays.asList("abc123", "123ABC"));
		Prerequisites prerequisites = new Prerequisites(Condition.any, drugSelectionFilter, 365, 10, Arrays.asList("00049052084", "00049042010"), null, true);
		prerequisitesList.add(prerequisites);


		// targetMap
		DrugSelectionFilter drugSelectionFilter1 = new DrugSelectionFilter(	SearchBy.gpi,"01100010", Arrays.asList(BgtInd.G),	"Penicillin G Pot in Dextrose Solution", null,
				"SANDOZ", 	Arrays.asList(RxOTC.R), Arrays.asList("1234567890", "0987654321"),	Arrays.asList("Tablet", "Tablet", "Tablet", "Tablet"));

		Target targetObj = new Target(drugSelectionFilter1,	365, 10, Arrays.asList("0009052084", "00049042010"), null, false);

		logger.info("Target" + targetObj);

		//alternativesMap
		List<Alternatives> alternativeList = new ArrayList<>();

		DrugSelectionFilter drugSelectionFilterObject = new DrugSelectionFilter(SearchBy.gpi, "01100010", Arrays.asList(BgtInd.B, BgtInd.G),
				"Penicillin G Pot in Dextrose Solution", "Penicillin G Potassium Injection Solution Reconstituted 5000000 UNIT", "SANDOZ", Arrays.asList(RxOTC.R, RxOTC.O),
				Arrays.asList("1987654321"),Arrays.asList("Tablet", "Tablet", "Tablet", "Tablet"));

		Alternatives alternativeObj = new Alternatives(drugSelectionFilterObject, 365, 10, Arrays.asList("00049052084", "00049042010", "70860012620"), null,
				null, true);

		alternativeList.add(alternativeObj);

		InterventionRule interventionRule = new InterventionRule();

		interventionRule.setRuleName("Test ANTIHYPERLIPIDEMICS HMG CoA Reductase Inhibitors Brand Crestor1");
		interventionRule.setRuleStatus(RuleStatus.valueOfEnum("Active").getRuleStatusIndex());
		interventionRule.setAverageLengthTherapy(12);
		interventionRule.setSavingsInformationText("This will save over $100.");
		interventionRule.setAgeAndBelow(5);
		interventionRule.setAgeAndGreater(21);
		interventionRule.setDaysSupply(10);
		interventionRule.setDaysSupplySign(">=");
		interventionRule.setCostThresholdAmount((float) 123.5);
		interventionRule.setCostThresholdSign(">");

		// Cosmos db to get random fax template, populating setInterventionType, setAcceptText, setDeclineText
		FaxTemplate randomFaxTemplate = FaxTemplateQueries.getRandomFaxTemplate();

		interventionRule.setInterventionType(randomFaxTemplate.getId());
		interventionRule.setRunDailyInd(true);
		interventionRule.setAcceptText(randomFaxTemplate.getAcceptText());
		interventionRule.setDeclineText(randomFaxTemplate.getDeclineText());
		interventionRule.setQualityTitleText( "Gap in Care:");
		interventionRule.setQualityFreeformTargetPrerequisiteInd(QualityFreeformTargetPrerequisiteInd.FREE_FORM.getQualityFreeformTargetPrerequisiteIndIndex());
		interventionRule.setQualityFreeFormText("This is a better choice.");
		interventionRule.setQualityInterventionText("This is custom intervention text that is displayed on the fax.");
		interventionRule.setFormularyTenantsGroups(formularyTenantsGroups);
		interventionRule.setTarget(targetObj);
		interventionRule.setPrerequisites(prerequisitesList);
		interventionRule.setAlternatives(alternativeList);
		interventionRule.setFaxAlternativeText("API Automation Testing");
		interventionRule.setPhoneFollowupInd(true);

		return interventionRule;
	}

	//Creating Test Data for EHP Tenants
	protected  InterventionRule  getNewEhpInterventionRule( ) 
	{

		//create test data : formularyTenantsGroups
		List<TenantGroups> tenantGroupsList = new ArrayList<>();
		TenantGroups tenantGroups1 = new TenantGroups("5ebc0f4d-a684-4a93-9c36-7de6f5d84833", Arrays.asList("Lifetime Healthcare Companies"));
		TenantGroups tenantGroups2 = new TenantGroups("87f4a82d-e2b6-44cc-9fff-828f94cf8ec9", Arrays.asList( "Essex County", "Seneca Foods", "Gatehouse Media","IBEW Local 86","Gleason"));
		tenantGroupsList.add(tenantGroups1);
		tenantGroupsList.add(tenantGroups2);
		FormularyTenantsGroups formularyTenantsGroups1 = new FormularyTenantsGroups("102","102","Excellus Commercial 3 Tier Closed", tenantGroupsList);

		List<TenantGroups> tenantGroupsList1 = new ArrayList<>();
		TenantGroups tenantGroups3 = new TenantGroups("b89aec2f-fcec-4674-820b-dfa057e9507e", Arrays.asList("Medicare Direct Pay", "Medicare Group"));
		tenantGroupsList1.add(tenantGroups3);
		FormularyTenantsGroups formularyTenantsGroups2 = new FormularyTenantsGroups("104","104","Excellus Medicare", tenantGroupsList1);

		List<FormularyTenantsGroups> formularyTenantsGroups = Arrays.asList(formularyTenantsGroups1,formularyTenantsGroups2);


		//create test data interventionRule : mapping above data for  formularyTenantsGroups,prerequisitesMap,targetMap,alternativesMap 
		InterventionRule interventionRule = new InterventionRule();
		interventionRule = genericInterventionRule( formularyTenantsGroups);

		return interventionRule;

	}

	//Creating Test Data for LOA Tenants using genericInterventionRule helper method 
	protected  InterventionRule  getNewLoaInterventionRule( ) 
	{

		//create test data : formularyTenantsGroups
		List<TenantGroups> tenantGroupsList = new ArrayList<>();

		TenantGroups tenantGroups1 = new TenantGroups("2f926508-ef7c-4226-a3e7-a3fc6f048b5d", Arrays.asList("RITT Industries Holdings Inc", "SYRACUSE GLASS COMPANY", "CAMERON MANUFACTURING"));
		tenantGroupsList.add(tenantGroups1);

		FormularyTenantsGroups formularyTenantsGroups1 = new FormularyTenantsGroups("103","103","103", tenantGroupsList);
		List<FormularyTenantsGroups> formularyTenantsGroups = Arrays.asList(formularyTenantsGroups1);


		//create test data interventionRule : mapping above data for  formularyTenantsGroups,prerequisitesMap,targetMap,alternativesMap 
		InterventionRule interventionRule = new InterventionRule();
		interventionRule = genericInterventionRule( formularyTenantsGroups);

		return interventionRule;
	}

	//Creating Test Data for EXE Tenants
	protected  InterventionRule  getNewExeInterventionRule() 
	{

		//create test data : formularyTenantsGroups
		List<TenantGroups> tenantGroupsList = new ArrayList<>();
		TenantGroups tenantGroups1 = new TenantGroups("5ebc0f4d-a684-4a93-9c36-7de6f5d84833", Arrays.asList("Lifetime Healthcare Companies"));
		TenantGroups tenantGroups2 = new TenantGroups("87f4a82d-e2b6-44cc-9fff-828f94cf8ec9", Arrays.asList( "Essex County", "Seneca Foods", "Gatehouse Media","IBEW Local 86","Gleason"));
		tenantGroupsList.add(tenantGroups1);
		tenantGroupsList.add(tenantGroups2);
		FormularyTenantsGroups formularyTenantsGroups1 = new FormularyTenantsGroups("102","102","Excellus Commercial 3 Tier Closed", tenantGroupsList);

		List<TenantGroups> tenantGroupsList1 = new ArrayList<>();
		TenantGroups tenantGroups3 = new TenantGroups("b89aec2f-fcec-4674-820b-dfa057e9507e", Arrays.asList("Medicare Direct Pay", "Medicare Group"));
		tenantGroupsList1.add(tenantGroups3);
		FormularyTenantsGroups formularyTenantsGroups2 = new FormularyTenantsGroups("104","104","Excellus Medicare", tenantGroupsList1);

		List<TenantGroups> tenantGroupsList2 = new ArrayList<>();
		TenantGroups tenantGroups4 = new TenantGroups("2f926508-ef7c-4226-a3e7-a3fc6f048b5d", Arrays.asList("AFVC"));
		tenantGroupsList2.add(tenantGroups4);
		FormularyTenantsGroups formularyTenantsGroups3 = new FormularyTenantsGroups("103","103","ESI National Preferred Formulary", tenantGroupsList2);

		List<FormularyTenantsGroups> formularyTenantsGroups = Arrays.asList(formularyTenantsGroups1,formularyTenantsGroups2,formularyTenantsGroups3);


		//create test data interventionRule : mapping above data for  formularyTenantsGroups,prerequisitesMap,targetMap,alternativesMap 
		InterventionRule interventionRule = new InterventionRule();
		interventionRule = genericInterventionRule( formularyTenantsGroups);

		return interventionRule;
	}


	/**
	 * Create API body
	 *
	 * @param interventionRule  {@link InterventionRule}
	 * @return {@link ObjectMapper}
	 */
	protected String createApiBody(InterventionRule interventionRule) throws JsonProcessingException
	{
		Map<String, Object> map = new LinkedHashMap<String, Object>();

		if ( interventionRule.getRuleName() != null )
			map.put("ruleName", interventionRule.getRuleName());	

		if (interventionRule.getRuleStatus() != null )
			map.put("ruleStatus", interventionRule.getRuleStatus());	

		if ( interventionRule.getAverageLengthTherapy() != null )
			map.put("averageLengthTherapy", interventionRule.getAverageLengthTherapy());	

		if ( interventionRule.getSavingsInformationText() != null )
			map.put("savingsInformationText", interventionRule.getSavingsInformationText());	

		if ( interventionRule.getAgeAndBelow() != null )
			map.put("ageAndBelow", interventionRule.getAgeAndBelow());

		if ( interventionRule.getAgeAndGreater() != null )
			map.put("ageAndGreater", interventionRule.getAgeAndGreater());

		if ( interventionRule.getDaysSupply() != null )
			map.put("daysSupply", interventionRule.getDaysSupply());

		if ( interventionRule.getDaysSupplySign() != null )
			map.put("daysSupplySign", interventionRule.getDaysSupplySign());

		if ( interventionRule.getCostThresholdAmount() != null )
			map.put("costThresholdAmount", interventionRule.getCostThresholdAmount());

		if ( interventionRule.getCostThresholdSign() != null )
			map.put("costThresholdSign", interventionRule.getCostThresholdSign());

		if ( interventionRule.getInterventionType() != null )
			map.put("interventionType", interventionRule.getInterventionType());

		if ( interventionRule.getRunDailyInd() != null )
			map.put("runDailyInd", interventionRule.getRunDailyInd());

		if ( interventionRule.getAcceptText() != null )
			map.put("acceptText", interventionRule.getAcceptText());

		if ( interventionRule.getDeclineText() != null )
			map.put("declineText", interventionRule.getDeclineText());

		if ( interventionRule.getQualityTitleText() != null )
			map.put("qualityTitleText", interventionRule.getQualityTitleText());

		if ( interventionRule.getQualityFreeformTargetPrerequisiteInd() != null )
			map.put("qualityFreeformTargetPrerequisiteInd", interventionRule.getQualityFreeformTargetPrerequisiteInd());

		if ( interventionRule.getQualityFreeFormText() != null )
			map.put("qualityFreeFormText", interventionRule.getQualityFreeFormText());

		if ( interventionRule.getQualityInterventionText() != null )
			map.put("qualityInterventionText", interventionRule.getQualityInterventionText());

		//formularyTenantsGroups
		if ( interventionRule.getFormularyTenantsGroups() != null )
		{
			map.put("formularyTenantsGroups", interventionRule.getFormularyTenantsGroups());
		}

		//target
		if ( interventionRule.getTarget() != null )
		{
			map.put("target", interventionRule.getTarget());
		}

		//alternatives
		if ( interventionRule.getAlternatives() != null )
		{
			map.put("alternatives", interventionRule.getAlternatives());
		}

		//prerequisites
		if ( interventionRule.getPrerequisites() != null )
		{
			map.put("prerequisites", interventionRule.getPrerequisites());
		}

		// faxAlternatives
		if ( interventionRule.getFaxAlternatives() != null )
		{
			map.put("faxAlternatives", interventionRule.getFaxAlternatives());
		}

		if ( interventionRule.getFaxAlternativeText() != null )
			map.put("faxAlternativeText", interventionRule.getFaxAlternativeText());

		if ( interventionRule.getPhoneFollowupInd() != null )
			map.put("phoneFollowupInd", interventionRule.getPhoneFollowupInd());



		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			logger.error("Unable to covert JSON object to string", e);
			return "Unable to covert JSON object to string --> " + e.getMessage();
		}
	}


	/**
	 * Create the expected {@link InterventionRule}
	 * 
	 * @param interventionRule expected {@link InterventionRule}
	 * @param actualAPIResponse actual {@link InterventionRule} api response
	 * @param createdDateTime of the correspondence
	 * @param apiBody {@link ObjectMapper} of the API body
	 * @return {@link InterventionRule}
	 */
	protected InterventionRule createExpectedInterventionRule(InterventionRule interventionRule, InterventionRule actualAPIResponse, String createdDateTime, String apiBody)
	{


		//Get list of  distinct adTenantIds
		Set<String> distinctAdTenantIdSet = new HashSet<>();
		for (FormularyTenantsGroups formularyTenantsGroups : interventionRule.getFormularyTenantsGroups()) {
			for (TenantGroups tenantGroups : formularyTenantsGroups.getTenantGroups()) {
				if(!distinctAdTenantIdSet.contains(tenantGroups.getAdTenantId())) { 
					distinctAdTenantIdSet.add(tenantGroups.getAdTenantId()); 
				} 
			} 
		} 

		//Get list of all formulary Id
		List<String> distinctFormulariesSet = new ArrayList<>();
		for (FormularyTenantsGroups formularyTenantsGroups : interventionRule.getFormularyTenantsGroups())
		{
			distinctFormulariesSet.add(formularyTenantsGroups.getFormularyId());
		}


		Gson gson = new Gson();
		InterventionRule expectedInterventionRule = gson.fromJson(apiBody, InterventionRule.class);

		expectedInterventionRule.setRuleId(actualAPIResponse.getRuleId());
		expectedInterventionRule.setId(actualAPIResponse.getId());
		expectedInterventionRule.setType("interventionrule");
		expectedInterventionRule.setRuleVersion(1);
		expectedInterventionRule.setNumberTenants(distinctAdTenantIdSet.size());
		expectedInterventionRule.setNumberFormularies(distinctFormulariesSet.size());
		expectedInterventionRule.setCreatedBy(RxConciergeUILogin.getAcctName());
		expectedInterventionRule.setCreatedDateTime(createdDateTime);
		expectedInterventionRule.setLastUpdatedBy(RxConciergeUILogin.getAcctName());
		expectedInterventionRule.setLastUpdatedDateTime(createdDateTime);
		expectedInterventionRule.setVersion("1");

		return expectedInterventionRule;
	}

	/**
	 * Call the API to insert member intervention
	 * 
	 * @param body for the API call
	 * @return {@link MemberIntervention}
	 */
	protected ApiPostStep insertInterventionRuleAPI(String body)
	{
		logger.debug("Starting API call");

		Headers headers = getGenericHeaders();

		ApiPostStep apiPostStep = new ApiPostStep(headers, INTERVENTION_RULE_POST_ENDPOINT, body, new Object[] {},201, null);
		apiPostStep.run();

		return apiPostStep;
	}


	/*
	 * Helper methods
	 */

	/**
	 * Generic test for happy path
	 * @param interventionRule {@link InterventionRule}
	 * @param apiBody {@link ObjectMapper}
	 * @return test validation result
	 */
	protected List<DynamicNode> happyPath( InterventionRule interventionRule, String apiBody)
	{
		List<DynamicNode> test = new ArrayList<DynamicNode>();


		// Call API
		ApiPostStep apiPostStep = insertInterventionRuleAPI(apiBody);
		InterventionRule actualAPIResponse = apiPostStep.convertToJsonDTO(InterventionRule.class);
		if ( apiPostStep.stepStatus() != IStep.Status.COMPLETED )
		{
			return apiPostStep.getTestResults();
		}
		else 
		{
			test.addAll(apiPostStep.getTestResults());
		}

		toBeDeleted = actualAPIResponse;	// set this to be deleted after the test completes

		// Create expected member corr  -fetched id from API response as it is autogenerated
		InterventionRule expected = createExpectedInterventionRule( interventionRule, actualAPIResponse, createdDateTime, apiBody);

		/*
		 * Validations
		 */

		// API
		test.add(dynamicContainer("API response", expected.compare(actualAPIResponse)));

		// Query Cosmos - Get the member correspondence from the cosmos
		InterventionRule actual = InterventionRuleQueries.getInterventionRuleById( actualAPIResponse.getId(),actualAPIResponse.getRuleId());

		test.add(dynamicContainer("Cosmos DB", expected.compare(actual)));

		return test;
	}

}
