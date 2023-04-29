/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;

import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.ProviderQueries;
import com.excellus.sqa.rxcc.dto.Hours;
import com.excellus.sqa.rxcc.dto.OfficeLocation;
import com.excellus.sqa.rxcc.dto.Provider;
import com.excellus.sqa.rxcc.tests.member.MemberInsertNoteTest;
import com.excellus.sqa.utilities.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * POST https://apim-lbs-rxc-dev-east-001.azure-api.net/api/provider/providers/{providerId}/office-locations
 * Request Parameter : providerId
 *
 * Insert a new Office Location for a Provider by {providerId}. {providerId} maps to npi in Provider Container.
 * 'guid' for the new Office Location automatically gets generated by the API.
 *
 * @author Manish Sharma (msharma)
 * @since 04/05/2022
 */
@Tag("ALL")
@Tag("PROVIDER")
@Tag("PROVIDE_OFFICELOCATION")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostProviderOfficeLocationInsert")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class InsertOfficeLocationTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(MemberInsertNoteTest.class);

	static Provider originalProvider;
	static Provider testData;

	@AfterEach
	public void resetoriginalOfficeLocation()
	{
		if ( originalProvider != null )
		{
			ProviderQueries.replaceProvider(originalProvider);
		}
	}

	@TestFactory
	@DisplayName("5864: Prov_InsertOfficeLocation Happy Path : ALL Properties")
	@Order(1)
	public List<DynamicNode> happyPathInsertOfficeLocation() throws Exception
	{
		// Create test data
		createTestData();

		// Setup test data
		int officeLocationId = testData.getOfficeLocations().size() + 1;

		OfficeLocation newLocation = new OfficeLocation();
		newLocation.setId(String.valueOf(officeLocationId));
		newLocation.setDefaultVal(false);
		newLocation.setStatus(true);
		newLocation.setAddress1("Something about address 1");
		newLocation.setAddress2("Something about address 2");
		//newLocation.setAddress3("Something about address 3");
		newLocation.setCity("City");
		newLocation.setState("RI");
		newLocation.setPostalCode("02895");
		newLocation.setTimeZone("EST");
		newLocation.setPhoneNumber("2345676787");
		newLocation.setFaxNumber("9878767876");
		newLocation.setContactName("Manish Sharma");
		newLocation.setContactNumber("5256589856");
		newLocation.setContactExt("234");
		newLocation.setFaxVerified(false);
		newLocation.setMonHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setTueHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setWedHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setThurHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setFriHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSatHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSunHours(new Hours(false,"09:00:00","20:00:00"));

		testData.getOfficeLocations().add(newLocation);

		// Create API body
		JsonObject body = createApiBody(newLocation);

		return happyPathInsert(body.toString());
	}

	@TestFactory
	@DisplayName("5874: Prov_InsertOfficeLocation With Body Spl Char")
	@Order(2)
	public List<DynamicNode> happyPathInsertSpecialChars() throws Exception
	{
		// Create test data
		createTestData();

		// Setup test data
		int officeLocationId = testData.getOfficeLocations().size() + 1;

		OfficeLocation newLocation = new OfficeLocation();
		newLocation.setId(String.valueOf(officeLocationId));
		newLocation.setDefaultVal(false);
		newLocation.setStatus(true);
		newLocation.setAddress1("Something !@#$%^&* address 1");
		newLocation.setAddress2("Something about !@#$%^&* 2");
		//newLocation.setAddress3("!@#$%^&* about address 3");
		newLocation.setCity("!@#$%^&*");
		newLocation.setState("!@#$%^&*");
		newLocation.setPostalCode("02895");
		newLocation.setTimeZone("#");
		newLocation.setPhoneNumber("2345676787");
		newLocation.setFaxNumber("9878767876");
		newLocation.setContactName("Manish Sharma");
		newLocation.setContactNumber("5256589856");
		newLocation.setContactExt("234");
		newLocation.setFaxVerified(false);
		newLocation.setMonHours(new Hours(false,"\"\"","\"\""));
		newLocation.setTueHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setWedHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setThurHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setFriHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSatHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSunHours(new Hours(false,"09:00:00","20:00:00"));

		testData.getOfficeLocations().add(newLocation);

		// Create API body
		JsonObject body = createApiBody(newLocation);

		return happyPathInsert(body.toString());
	}

	/*
	 * Negative test cases
	 */

	@TestFactory
	@DisplayName("5877: Prov_InsertOfficeLocation Invalid Auth")
	@Order(3)
	public List<DynamicNode>  invalidToken()
	{
		// Get provider
		Provider provider = ProviderQueries.getRandomProvider();

		OfficeLocation newLocation = new OfficeLocation();

		newLocation.setDefaultVal(false);
		newLocation.setStatus(true);
		newLocation.setAddress1("Something about address 1");
		newLocation.setAddress2("Something about address 2");
		//newLocation.setAddress3("Something about address 3");
		newLocation.setMonHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setTueHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setWedHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setThurHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setFriHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSatHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSunHours(new Hours(false,"09:00:00","20:00:00"));

		// Create API body
		JsonObject body = createApiBody(newLocation);

		ApiPostStep apiPostStep = new ApiPostStep(getHeadersInvalidAuth(),
										PROVIDERS_INSERT_OFFICE_LOCATION_POST_ENDPOINT,
										body.toString(),
										new Object[] {provider.getId()},
										401,
										HTTP_401_UNAUTHORIZED);
		apiPostStep.run();

		List<DynamicNode> testResults = new ArrayList<>(apiPostStep.getTestResults());

		Response response = apiPostStep.getResponse();

		// validate the API json response "message"
		String actualMsg = response.then().extract().jsonPath().get("message");
		testResults.add(dynamicTest("API response - message [" + UNAUTHORIZED_MSG + "]", () -> assertEquals(UNAUTHORIZED_MSG, actualMsg)));

		return testResults;
	}

	@TestFactory
	@DisplayName("5866: Prov_InsertOfficeLocation Invalid Parm")
	@Order(4)
	public List<DynamicNode> invalidParam()
	{
		String providerId = "998876543210";

		// Create API body
		OfficeLocation newLocation = new OfficeLocation();

		newLocation.setDefaultVal(false);
		newLocation.setStatus(true);
		newLocation.setAddress1("Something about address 1");
		newLocation.setAddress2("Something about address 2");
		//newLocation.setAddress3("Something about address 3");
		newLocation.setMonHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setTueHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setWedHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setThurHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setFriHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSatHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSunHours(new Hours(false,"09:00:00","20:00:00"));

		// Create API body
		JsonObject body = createApiBody(newLocation);

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(),
									PROVIDERS_INSERT_OFFICE_LOCATION_POST_ENDPOINT,
									body.toString(),
									new Object[] {providerId},
									404,
									HTTP_404_NOT_FOUND);
		apiPostStep.run();

		List<DynamicNode> testResults = new ArrayList<>(apiPostStep.getTestResults());

		// validate the API message
		final String EXPECTED_MSG = "Entity with the specified id does not exist in the system.";
		Response response = apiPostStep.getResponse();
		String actualMsg = response.then().extract().asString();
		testResults.add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

		return testResults;
	}

	@TestFactory
	@DisplayName("5863: Prov_InsertOfficeLocation With Body(invalid field in the body)")
	@Order(5)
	public List<DynamicNode> invalidBodyField()
	{
		Provider provider = ProviderQueries.getRandomProvider();

		// Create API body
		String apiBody = "{ \"invalidField\": \"Invalid field\" }";

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(),
									PROVIDERS_INSERT_OFFICE_LOCATION_POST_ENDPOINT,
									apiBody,
									new Object[]{testData.getId()},
									400,
									HTTP_400_BAD_REQUEST);
		apiPostStep.run();

		List<DynamicNode> testResults = new ArrayList<>(apiPostStep.getTestResults());

		// validate the API message
		Response response = apiPostStep.getResponse();
		final String EXPECTED_MSG = "\"'address1' can't be null or a blank string!\"";
		String actualMsg = response.then().extract().asString();
		testResults.add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertEquals(EXPECTED_MSG, actualMsg)));

		return testResults;
	}

	/**
	 * Submit API request and validate the results
	 * 
	 * @param apiBody API request body
	 * @return list of {@link DynamicNode} of test
	 */
	private List<DynamicNode> happyPathInsert(String apiBody)
	{
		// Call API
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(),
										PROVIDERS_INSERT_OFFICE_LOCATION_POST_ENDPOINT,
										apiBody,
										new Object[]{testData.getId()},
										201,
										HTTP_201_CONTENT_CREATED);
		apiPostStep.run();

		List<DynamicNode> testResults = new ArrayList<>(apiPostStep.getTestResults());

		if ( apiPostStep.getResponseStatusCode() != 201 )
			return testResults;

		// Get the member from the Cosmos
		Provider actual = ProviderQueries.getProviderById(testData.getId());

		logger.info("API response: " + actual.toString());

		// Validate the Cosmos data after the API patch
		testResults.add(dynamicContainer("Cosmos db", testData.compare( actual )));

		return testResults;
	}

	/**
	 * Create API body
	 * 
	 *  @param officeLocation {@link OfficeLocation} that will be used to create API request body
	 *  @return JsonObject that represent the params
	 */
	private JsonObject createApiBody(OfficeLocation officeLocation)
	{
		JsonObject requestBody = new JsonObject();

		requestBody.addProperty("default", officeLocation.isDefaultVal());

		requestBody.addProperty("status", officeLocation.isStatus());

		requestBody.addProperty("address1", officeLocation.getAddress1());

		if ( StringUtils.isNotBlank(officeLocation.getAddress2()) )
			requestBody.addProperty("address2", officeLocation.getAddress2());

		//Out of scope for now 4/14/2022 
		//if ( StringUtils.isNotBlank(officeLocation.getAddress3()) ) 
		//requestBody.addProperty("address3", officeLocation.getAddress3());

		if ( StringUtils.isNotBlank(officeLocation.getCity()) )
			requestBody.addProperty("city", officeLocation.getCity());

		if ( StringUtils.isNotBlank(officeLocation.getState()))
			requestBody.addProperty("state", officeLocation.getState());

		if ( StringUtils.isNotBlank(officeLocation.getPostalCode()) )
			requestBody.addProperty("postalCode", officeLocation.getPostalCode());

		if ( StringUtils.isNotBlank(officeLocation.getTimeZone()) )
			requestBody.addProperty("timeZone", officeLocation.getTimeZone());

		if ( StringUtils.isNotBlank(officeLocation.getPhoneNumber()) )
			requestBody.addProperty("phoneNumber", officeLocation.getPhoneNumber());

		if ( StringUtils.isNotBlank( officeLocation.getFaxNumber()) )
			requestBody.addProperty("faxNumber", officeLocation.getFaxNumber());

		if ( StringUtils.isNotBlank( officeLocation.getContactName()) )
			requestBody.addProperty("contactName", officeLocation.getContactName());

		if ( StringUtils.isNotBlank(officeLocation.getContactNumber()) )
			requestBody.addProperty("contactNumber", officeLocation.getContactNumber());

		if ( StringUtils.isNotBlank(officeLocation.getContactExt()) )
			requestBody.addProperty("contactExt", officeLocation.getContactExt());

		requestBody.addProperty("faxVerified", officeLocation.isFaxVerified());

		// Adding Mon Hours
		JsonObject jsonMonHours = new JsonObject();

		if ( StringUtils.isNotBlank(officeLocation.getMonHours().getStartTime()) )

			jsonMonHours.addProperty("startTime", officeLocation.getMonHours().getStartTime());

		if ( StringUtils.isNotBlank(officeLocation.getMonHours().getEndTime()))
			jsonMonHours.addProperty("endTime", officeLocation.getMonHours().getEndTime());

		jsonMonHours.addProperty("closed", officeLocation.getMonHours().isClosed());

		requestBody.add("MonHours", jsonMonHours);

		// Adding Tue Hours
		JsonObject jsonTueHours = new JsonObject();

		if ( StringUtils.isNotBlank(officeLocation.getTueHours().getStartTime()) )
			jsonTueHours.addProperty("startTime",officeLocation.getTueHours().getStartTime());

		if ( StringUtils.isNotBlank(officeLocation.getTueHours().getEndTime()))
			jsonTueHours.addProperty("endTime",officeLocation.getTueHours().getEndTime());

		jsonTueHours.addProperty("closed", officeLocation.getTueHours().isClosed());

		requestBody.add("TueHours", jsonTueHours);

		// Adding Wed Hours
		JsonObject jsonWedHours = new JsonObject();

		if ( StringUtils.isNotBlank(officeLocation.getWedHours().getStartTime()) )
			jsonWedHours.addProperty("startTime",officeLocation.getWedHours().getStartTime());

		if ( StringUtils.isNotBlank(officeLocation.getWedHours().getEndTime()) )
			jsonWedHours.addProperty("endTime",officeLocation.getWedHours().getEndTime());

		jsonWedHours.addProperty("closed", officeLocation.getWedHours().isClosed());

		requestBody.add("WedHours", jsonWedHours);

		// Adding Thur Hours
		JsonObject jsonThurHours = new JsonObject();

		jsonThurHours.addProperty("closed", officeLocation.getThurHours().isClosed());

		if ( StringUtils.isNotBlank(officeLocation.getThurHours().getStartTime()) )
			jsonThurHours.addProperty("startTime",officeLocation.getThurHours().getStartTime());

		if ( StringUtils.isNotBlank(officeLocation.getWedHours().getEndTime()) )
			jsonThurHours.addProperty("endTime",officeLocation.getThurHours().getEndTime());


		requestBody.add("ThurHours", jsonThurHours);

		// Adding Fri Hours
		JsonObject jsonFriHours = new JsonObject();

		if ( StringUtils.isNotBlank(officeLocation.getFriHours().getStartTime()) )
			jsonFriHours.addProperty("startTime",officeLocation.getFriHours().getStartTime());

		if ( StringUtils.isNotBlank(officeLocation.getFriHours().getEndTime()))
			jsonFriHours.addProperty("endTime",officeLocation.getFriHours().getEndTime());

		jsonFriHours.addProperty("closed", officeLocation.getFriHours().isClosed());

		requestBody.add("FriHours", jsonFriHours);

		// Adding Sat Hours
		JsonObject jsonSatHours = new JsonObject();

		if ( StringUtils.isNotBlank(officeLocation.getSatHours().getStartTime()) )
			jsonSatHours.addProperty("startTime",officeLocation.getSatHours().getStartTime());

		if ( StringUtils.isNotBlank(officeLocation.getSatHours().getEndTime()))
			jsonSatHours.addProperty("endTime",officeLocation.getSatHours().getEndTime());

		jsonSatHours.addProperty("closed", officeLocation.getSatHours().isClosed());

		requestBody.add("SatHours", jsonSatHours);

		// Adding Sun Hours
		JsonObject jsonSunHours = new JsonObject();

		if ( StringUtils.isNotBlank(officeLocation.getSunHours().getStartTime()) )
			jsonSunHours.addProperty("startTime",officeLocation.getSunHours().getStartTime());

		if ( StringUtils.isNotBlank(officeLocation.getSunHours().getEndTime()))
			jsonSunHours.addProperty("endTime",officeLocation.getSunHours().getEndTime());

		jsonSunHours.addProperty("closed", officeLocation.getSunHours().isClosed());

		requestBody.add("SunHours", jsonSunHours);

		return requestBody;
	}

	/**
	 * Create a test data to use
	 * @throws JsonProcessingException is exception occurs
	 */
	private void createTestData() throws JsonProcessingException
	{
		// Get random provider
		originalProvider = ProviderQueries.getRandomProvider();

		// Create a deep copy and update id
		testData = originalProvider.deepCopy(Provider.class);
		testData.setLastUpdatedBy(RxConciergeUILogin.getAcctName());
		
		try {
			testData.setLastUpdated(DateTimeUtils.generateTimeStamp(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT, RxConciergeCosmoConfig.COSMOS_TIMEZONE));
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}
	}

}