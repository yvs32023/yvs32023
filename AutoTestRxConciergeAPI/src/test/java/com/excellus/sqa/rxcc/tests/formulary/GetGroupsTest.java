/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.formulary;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
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

import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.GroupQueries;
import com.excellus.sqa.rxcc.dto.Group;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * https://apim-lbs-rxc-dev-east-001.azure-api.net/api/formulary/formularies/{formularyId}/groups
 *
 * Request Parameters : formularyId 
 * 
 * @author Manish Sharma (msharma)
 * @since 09/13/2022
 */
@Tag("ALL")
@Tag("FORMULARY")
@Tag("GROUP")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetGroups")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class GetGroupsTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(GetGroupsTest.class);

	@TestFactory
	@DisplayName("42940: Formulary GetGroups ALL Happy Path")
	@Order(1)
	public List<DynamicNode> happyPath() throws JsonProcessingException {

		Group list = GroupQueries.getRandomGroup();
		String formularyId = list.getFormularyId();

		// Querying Cosmos db for the list of the group based on formularyId
		List<Group> expectedGroups = GroupQueries.getGroup(formularyId);

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), GROUPS_GET_ENDPOINT, new Object[] {formularyId}, 200, null);
		apiGetStep.run();

		List<Group> actualGroups = apiGetStep.convertToJsonDTOs(Group.class);

		if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED ) {
			return apiGetStep.getTestResults();
		}

		for ( Group expected : expectedGroups)
		{
			boolean found = false;
			for (Group actual : actualGroups ) 
			{
				if ( expected.equals(actual) )
				{
					found = true;
					apiGetStep.getTestResults().add(dynamicContainer("Group for Formulary id [" + expected.getFormularyId() + "]", expected.compare(actual)));
					break;
				}
			}

			if ( !found )
			{
				apiGetStep.getTestResults().add(dynamicTest("Group for Formulary id [" + expected.getFormularyId() + "]", fail("Unable to find the formulary from API response")));
			}
		}
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("42940: Formulary GetGroups EHP Happy Path")
	@Order(2)
	public List<DynamicNode> happyPathEHP() throws JsonProcessingException {

		Group list = GroupQueries.getRandomGroupEHP();
		String formularyId = list.getFormularyId();

		// Querying Cosmos db for the list of the group based on formularyId
		List<Group> expectedGroups = GroupQueries.getGroup(formularyId);

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), GROUPS_GET_ENDPOINT, new Object[] {formularyId}, 200, null);
		apiGetStep.run();

		List<Group> actualGroups = apiGetStep.convertToJsonDTOs(Group.class);

		if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED ) {
			return apiGetStep.getTestResults();
		}

		for ( Group expected : expectedGroups)
		{
			boolean found = false;
			for (Group actual : actualGroups ) 
			{
				if ( expected.equals(actual) )
				{
					found = true;
					apiGetStep.getTestResults().add(dynamicContainer("Group for Formulary id [" + expected.getFormularyId() + "]", expected.compare(actual)));
					break;
				}
			}

			if ( !found )
			{
				apiGetStep.getTestResults().add(dynamicTest("Group for Formulary id [" + expected.getFormularyId() + "]", fail("Unable to find the formulary from API response")));
			}
		}
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("42940: Formulary GetGroups EXE Happy Path")
	@Order(3)
	public List<DynamicNode> happyPathEXE() throws JsonProcessingException {

		Group list = GroupQueries.getRandomGroupEXE();
		String formularyId = list.getFormularyId();

		// Querying Cosmos db for the list of the group based on formularyId
		List<Group> expectedGroups = GroupQueries.getGroup(formularyId);

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), GROUPS_GET_ENDPOINT, new Object[] {formularyId}, 200, null);
		apiGetStep.run();

		List<Group> actualGroups = apiGetStep.convertToJsonDTOs(Group.class);

		if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED ) {
			return apiGetStep.getTestResults();
		}

		for ( Group expected : expectedGroups)
		{
			boolean found = false;
			for (Group actual : actualGroups ) 
			{
				if ( expected.equals(actual) )
				{
					found = true;
					apiGetStep.getTestResults().add(dynamicContainer("Group for Formulary id [" + expected.getFormularyId() + "]", expected.compare(actual)));
					break;
				}
			}

			if ( !found )
			{
				apiGetStep.getTestResults().add(dynamicTest("Group for Formulary id [" + expected.getFormularyId() + "]", fail("Unable to find the formulary from API response")));
			}
		}
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("54910: Formulary GetGroups  Happy Path with Param Invalid")
	@Order(4)
	public List<DynamicNode> invalidParam() throws JsonProcessingException {

		String formularyId = "invalid";

		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), GROUPS_GET_ENDPOINT,
				new Object[]{formularyId}, 200, HTTP_200_OK);

		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("54913: Formulary GetGroups Invalid Auth")
	@Order(5)
	public List<DynamicNode> invalidAuth()
	{
		//Query Cosmos with one random formulary group
		Group expected = GroupQueries.getRandomGroup();

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getHeadersInvalidAuth(), GROUPS_GET_ENDPOINT,
				new Object[]{expected.getFormularyId()}, 401, HTTP_401_UNAUTHORIZED);

		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("54916: Formulary GetGroups Invalid Method")
	@Order(6)
	public List<DynamicNode> invalidRestApiPostMethod() throws JsonProcessingException {

		String formularyId = "invalid";

		// API call
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), GROUPS_GET_ENDPOINT, null,
				new Object[]{formularyId}, 500, HTTP_500_INTERNAL_SERVER_ERR);

		apiPostStep.run();
		return apiPostStep.getTestResults();
	}
}
