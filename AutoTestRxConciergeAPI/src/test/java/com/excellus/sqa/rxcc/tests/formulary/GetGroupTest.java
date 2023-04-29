/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.formulary;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.GroupQueries;
import com.excellus.sqa.rxcc.dto.Group;
import com.excellus.sqa.step.IStep;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.restassured.http.Headers;

/**
 * Cosmos DB | Database id: rxcc-shared | Container id: formulary
 * Get formulary type group for {formularyId} by {id}
 * 
 * GET https://apim-lbs-rxc-dev-east-001.azure-api.net/api/formulary/formularies/{formularyId}/groups/{id}
 * 
 * Request Parameters : formularyId & id
 * 
 * @author Manish Sharma (msharma)
 * @since 09/13/2022
 */
@Tag("ALL")
@Tag("FORMULARY")
@Tag("GROUP")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetGroup")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class GetGroupTest extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(GetGroupTest.class);

	/*
	 * Positive test cases
	 */
	@TestFactory
	@DisplayName("42939: Formulary GetGroup ALL Happy Path")
	@Order(1)
	public List<DynamicNode> happyPathALL() throws JsonProcessingException {

		//Query Cosmos db for list of formulary groups
		List<Group> list = GroupQueries.getGroup();
		Group expected = list.get(0);

		// API call and validation
		return getApiCall(expected);
	}

	@TestFactory
	@DisplayName("42939: Formulary GetGroup EHP Happy Path")
	@Order(1)
	public List<DynamicNode> happyPathEHP() throws JsonProcessingException {

		//Query Cosmos db for list of formulary groups
		List<Group> list = GroupQueries.getGroupEHP();
		Group expected = list.get(0);

		// GC (10/26/22) perform test on user role that does not have tenant level access on EHP
		if ( !StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_multi") &&
				!StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_single"))
		{
			ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), GROUP_GET_ENDPOINT, new Object[] {expected.getFormularyId(), expected.getId()}, 400, HTTP_400_BAD_REQUEST);
			apiGetStep.run();

			return apiGetStep.getTestResults();
		}

		// API call
		return getApiCall(expected);
	}

	@TestFactory
	@DisplayName("42939: Formulary GetGroup EXE Happy Path")
	@Order(1)
	public List<DynamicNode> happyPathEXE() throws JsonProcessingException {

		//Query Cosmos db for list of formulary groups
		List<Group> list = GroupQueries.getGroupEXE();
		Group expected = list.get(0);

		// GC (10/26/22) perform test on user role that does not have tenant level access on EXE
		if ( !StringUtils.endsWithIgnoreCase(RxConciergeUILogin.getAcctName(), "_multi") )
		{
			ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), GROUP_GET_ENDPOINT, new Object[] {expected.getFormularyId(), expected.getId()}, 400, HTTP_400_BAD_REQUEST);
			apiGetStep.run();

			return apiGetStep.getTestResults();
		}

		// API call
		return getApiCall(expected);
	}

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("54905: Formulary GetGroup  Invalid Auth")
	@Order(2)
	public List<DynamicNode> invalidAuth()
	{
		//Query Cosmos db for list of formulary groups
		List<Group> list = GroupQueries.getGroup();
		Group expected = list.get(0);

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getHeadersInvalidAuth(), GROUP_GET_ENDPOINT,
				new Object[]{expected.getFormularyId(),expected.getId()}, 401, HTTP_401_UNAUTHORIZED);

		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("54902: Formulary GetGroup  Happy Path with Param Invalid")
	@Order(3)
	public List<DynamicNode> invalidParam() throws JsonProcessingException {

		String formularyId = "invalid";
		String id = "invalid";
	
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), GROUP_GET_ENDPOINT,
				new Object[]{formularyId, id}, 404, HTTP_404_NOT_FOUND);

		apiGetStep.run();
		return apiGetStep.getTestResults();
	}

	@TestFactory
	@DisplayName("54908: Formulary GetGroup Invalid Method")
	@Order(4)
	public List<DynamicNode> invalidRestApiPostMethod()	{
		
		//Query Cosmos db for list of formulary groups
		List<Group> list = GroupQueries.getGroup();
		Group expected = list.get(0);

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), GROUP_GET_ENDPOINT, null,
				new Object[]{expected.getFormularyId(), expected.getId()}, 404, HTTP_404_RESOURCE_NOT_FOUND);

		apiPostStep.run();
		return apiPostStep.getTestResults();
	}

	/**
	 * Call the API to get group
	 *
	 * @param expected formulary group
	 * @return {@link Group}
	 */
	private List<DynamicNode> getApiCall(Group expected) {

		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), GROUP_GET_ENDPOINT, new Object[] {expected.getFormularyId(), expected.getId()}, 200, null);
		apiGetStep.run();

		Group actual = apiGetStep.convertToJsonDTO(Group.class);

		if ( apiGetStep.stepStatus() != IStep.Status.COMPLETED ) {
			return apiGetStep.getTestResults();
		}

		apiGetStep.getTestResults().add(dynamicContainer("Group validation", expected.compare(actual)));

		return apiGetStep.getTestResults();
	}
}
