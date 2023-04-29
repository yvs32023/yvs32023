/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;

import com.excellus.sqa.restapi.steps.ApiPutStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.ProviderQueries;
import com.excellus.sqa.rxcc.dto.Hours;
import com.excellus.sqa.rxcc.dto.OfficeLocation;
import com.excellus.sqa.rxcc.dto.Provider;
import com.excellus.sqa.utilities.DateTimeUtils;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * 
 *  
 * 
 * {
 *   "default" : boolean,
 *  "status" : boolean,
 *  "faxVerified" : boolean,
 *  "address1" : "string",		
 *  "address2" : "string",
 *  "city" : "string",
 *  "state" : "string",
 *  "postalCode" : "string",
 *  "phoneNumber" : "string",
 *  "faxNumber" : "string",
 *  "timeZone" : "string",
 *  "contactName" : "string",
 *  "contactNumber" : "string",
 *  "contactExt" : "string",
 *   "monHours" : {
 *       "closed" : boolean,
 *       "startTime" : "string",
 *       "endTime" : "string"
 *   }
 *   "tueHours" : {
 *       "closed" : boolean,
 *      "startTime" : "string",
 *       "endTime" : "string"
 *   }	
 *   "wedHours" : {
 *      "closed" : boolean,
 *       "startTime" : "string",
 *      "endTime" : "string"
 *   }
 *  "thurHours" : {
 *       "closed" : boolean,
 *      "startTime" : "string",
 *       "endTime" : "string"
 *   }
 *   "friHours" : {
 *       "closed" : boolean,
 *      "startTime" : "string",
 *       "endTime" : "string"
 *   }
 *   "satHours" : {
 *       "closed" : boolean,
 *       "startTime" : "string",
 *       "endTime" : "string"
 *   }
 *   "sunHours" : {
 *       "closed" : boolean,
 *       "startTime" : "string",
 *       "endTime" : "string"
 *   }		
 * }
 * <p><b>Request</b><br/>
 * PUT    https://apim-lbs-rxc-dev-east-001.azure-api.net/api/provider/providers/{providerId}/office-locations/{officeLocationId}
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 04/28/2022
 */
@Tag("ALL")
@Tag("PROVIDER")
@Tag("PROVIDER_REPLACEOFFICELOCATION")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PutProviderOfficeLocationReplace")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class ReplaceOfficeLocationTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(ReplaceOfficeLocationTest.class);

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
	@Tag("ALL")
	@Tag("PROVIDER")
	@TestFactory
	@DisplayName("5880: Prov_ReplaceOfficeLocation Happy Path : ALL Properties")
	@Order(1)
	public List<DynamicNode> happyPathReplaceOfficeLocation() throws Exception
	{
		// Create test data

		createTestData();

		// Setup test data
		int index = testData.getOfficeLocations().size() - 1;

		OfficeLocation newLocation = getNewRegularOfficeLocation(index);
		OfficeLocation officeLocation = testData.getOfficeLocations().get(index);

		testData.getOfficeLocations().remove(officeLocation);
		testData.getOfficeLocations().add(newLocation);

		for ( OfficeLocation office : testData.getOfficeLocations() )
		{
			if ( !office.getId().equals(newLocation.getId()) && office.isDefaultVal() ) {
				office.setDefaultVal(false);
				break;
			}
		}


		// Create API body
		JsonObject body = createApiBody(newLocation);

		return happyPathReplace(body.toString(), newLocation.getId());
	}

	@TestFactory
	@DisplayName("5880:: Prov_ReplaceOfficeLocation Happy Path : Required Properties")
	@Order(2)
	public List<DynamicNode> happyPathReplaceOfficeLocationRequired() throws Exception
	{
		// Create test data
		createTestData();

		// Setup test data
		int index = testData.getOfficeLocations().size() - 1;

		OfficeLocation newLocation = new OfficeLocation();
		OfficeLocation officeLocation = testData.getOfficeLocations().get(index);

		newLocation.setId(officeLocation.getId());
		newLocation.setDefaultVal(true);
		newLocation.setAddress1("Something !@#$%^&* address 1");
		newLocation.setMonHours(new Hours(true,"07:79:00","16:79:00"));
		newLocation.setTueHours(new Hours(false,"\"\"","\"\""));
		newLocation.setWedHours(new Hours(false,"\"\"","\"\""));
		newLocation.setThurHours(new Hours(false,"\"\"","\"\""));
		newLocation.setFriHours(new Hours(false,"\"\"","\"\""));
		newLocation.setSatHours(new Hours(false,"\"\"","\"\""));
		newLocation.setSunHours(new Hours(false,null,null));

		testData.getOfficeLocations().remove(officeLocation);
		testData.getOfficeLocations().add(newLocation);

		for ( OfficeLocation office : testData.getOfficeLocations() )
		{
			if ( !office.getId().equals(newLocation.getId()) && office.isDefaultVal() ) {
				office.setDefaultVal(false);
				break;
			}
		}

		// Create API body
		JsonObject body = createApiBody(newLocation);

		return happyPathReplace(body.toString(), newLocation.getId());
	}

	@TestFactory
	@DisplayName("16962 - 17034: Prov_ReplaceOfficeLocation Happy Path : Required Properties")
	@Order(3)
	public List<DynamicNode> happyPathReplaceOfficeLocationRequiredExtended() throws Exception
	{
		// Create test data
		createTestData();

		// Setup test data
		int index = testData.getOfficeLocations().size() - 1;

		OfficeLocation newLocation = new OfficeLocation();
		OfficeLocation officeLocation = testData.getOfficeLocations().get(index);

		newLocation.setId(officeLocation.getId());
		newLocation.setDefaultVal(true);
		newLocation.setAddress1("Something Testing  address 1");
		newLocation.setMonHours(new Hours(false,"07:79:00","16:79:00"));
		newLocation.setTueHours(new Hours(false,"07:79:00","16:79:00 ")); //end with space
		newLocation.setWedHours(new Hours(false,"07:79:00","16:79:00"));
		newLocation.setThurHours(new Hours(false,"07:79:00","16:79:00"));
		newLocation.setFriHours(new Hours(false,"07:79:00","16:79:00"));
		newLocation.setSatHours(new Hours(false,"07:79:00","16:79:00"));
		newLocation.setSunHours(new Hours(false,"07:79:00","16:79:00"));

		testData.getOfficeLocations().remove(officeLocation);
		testData.getOfficeLocations().add(newLocation);

		for ( OfficeLocation office : testData.getOfficeLocations() )
		{
			if ( !office.getId().equals(newLocation.getId()) && office.isDefaultVal() ) {
				office.setDefaultVal(false);
				break;
			}
		}

		// Create API body
		JsonObject body = createApiBody(newLocation);

		return happyPathReplace(body.toString(), newLocation.getId());
	}


	@TestFactory
	@DisplayName("5890, 16943,16944, 16954: Prov_ReplaceOfficeLocation With Body Spl Char")
	@Order(4)
	public List<DynamicNode> happyPathInsertSpecialChars() throws Exception
	{
		// Create test data
		createTestData();

		int index = testData.getOfficeLocations().size() - 1;

		OfficeLocation newLocation = getNewOfficeLocation(index);
		OfficeLocation officeLocation = testData.getOfficeLocations().get(index);

		testData.getOfficeLocations().remove(officeLocation);
		testData.getOfficeLocations().add(newLocation);

		for ( OfficeLocation office : testData.getOfficeLocations() )
		{
			if ( !office.getId().equals(newLocation.getId()) && office.isDefaultVal() ) {
				office.setDefaultVal(false);
				break;
			}
		}

		logger.debug("original office location: " + officeLocation);
		logger.debug("Updated office location: " + newLocation);

		// Create API body
		JsonObject body = createApiBody(newLocation);

		return happyPathReplace(body.toString(), newLocation.getId());

	}

	/*
	 * Negative test cases
	 */
	@TestFactory
	@DisplayName("5882: Prov_ReplaceOfficeLocation Invalid Parm")
	@Order(5)
	public List<DynamicNode> invalidParam()
	{
		String  providerId = "3333333333444";
		String  officeLocationId = "1";

		// Setup test data
		int index = testData.getOfficeLocations().size() - 1;

		OfficeLocation newLocation = getNewRegularOfficeLocation(index);
		OfficeLocation officeLocation = testData.getOfficeLocations().get(index);

		testData.getOfficeLocations().remove(officeLocation);
		testData.getOfficeLocations().add(newLocation);

		for ( OfficeLocation office : testData.getOfficeLocations() )
		{
			if ( !office.getId().equals(newLocation.getId()) && office.isDefaultVal() ) {
				office.setDefaultVal(false);
				break;
			}
		}

		// Create API body
		JsonObject body = createApiBody(newLocation);

		ApiPutStep apiPutStep = new ApiPutStep(getGenericHeaders(),
										PROVIDERS_REPLACE_OFFICE_LOCATION_POST_ENDPOINT,
										body.toString(),
										new Object[] {providerId, officeLocationId},
										404,
										HTTP_404_NOT_FOUND);
		apiPutStep.run();

		// validate the API message
		final String EXPECTED_MSG = "Entity with the specified id does not exist in the system.";
		String actualMsg = apiPutStep.getResponse().then().extract().asString();
		apiPutStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertTrue(actualMsg.contains(EXPECTED_MSG))));

		return apiPutStep.getTestResults();
	}


	@TestFactory
	@DisplayName("5894: Prov_ReplaceOfficeLocation Invalid Auth")
	@Order(6)
	public List<DynamicNode>  invalidToken()
	{
		String  providerId = "3333333333444";
		String  officeLocationId = "1";


		// Setup test data
		int index = testData.getOfficeLocations().size() - 1;

		OfficeLocation newLocation = getNewRegularOfficeLocation(index);
		OfficeLocation officeLocation = testData.getOfficeLocations().get(index);

		testData.getOfficeLocations().remove(officeLocation);
		testData.getOfficeLocations().add(newLocation);

		for ( OfficeLocation office : testData.getOfficeLocations() )
		{
			if ( !office.getId().equals(newLocation.getId()) && office.isDefaultVal() ) {
				office.setDefaultVal(false);
				break;
			}
		}


		// Create API body
		JsonObject body = createApiBody(newLocation);

		ApiPutStep apiPutStep = new ApiPutStep(getHeadersInvalidAuth(),
										PROVIDERS_REPLACE_OFFICE_LOCATION_POST_ENDPOINT,
										body.toString(),
										new Object[] {providerId, officeLocationId},
										401,
										HTTP_401_UNAUTHORIZED);
		apiPutStep.run();

		// validate the API json response "message"
		String actualMsg = apiPutStep.getResponse().then().extract().jsonPath().get("message");
		apiPutStep.getTestResults().add(dynamicTest("API response - message [" + UNAUTHORIZED_MSG + "]", () -> assertEquals(UNAUTHORIZED_MSG, actualMsg)));

		return apiPutStep.getTestResults();
	}


	@TestFactory
	@DisplayName("16940: Prov_ReplaceOfficeLocation With Body (invalid field in the body)")
	@Order(7)
	public List<DynamicNode>  invalidBodyField()
	{
		String whereClauseExtension = "where ARRAY_LENGTH(c.officeLocations) > 0";
		Provider provider = ProviderQueries.getProvider(whereClauseExtension);

		// Create API body
		String apiBody = "{ \"invalidField\": \"Invalid field\" }";

		ApiPutStep apiPutStep = new ApiPutStep(getGenericHeaders(),
										PROVIDERS_REPLACE_OFFICE_LOCATION_POST_ENDPOINT,
										apiBody,
										new Object[] {provider.getId(), provider.getOfficeLocations().get(0).getId()},
										400,
										HTTP_400_BAD_REQUEST);
		apiPutStep.run();

		// validate the API message
		final String EXPECTED_MSG = "\"'address1' can't be null or a blank string!\"";
		String actualMsg = apiPutStep.getResponse().then().extract().asString();
		apiPutStep.getTestResults().add(dynamicTest("API message [" + EXPECTED_MSG + "]", () -> assertEquals(EXPECTED_MSG, actualMsg)));

		return apiPutStep.getTestResults();
	}



	//Helper Method With Special Character

	public  OfficeLocation  getNewOfficeLocation(int index) {

		OfficeLocation newLocation = new OfficeLocation();
		OfficeLocation officeLocation = testData.getOfficeLocations().get(index);

		newLocation.setId(officeLocation.getId());

		newLocation.setDefaultVal(true);
		newLocation.setStatus(true);
		newLocation.setAddress1("Something !@#$%^&* address JG");
		newLocation.setAddress2("Something about !@#$%^&* JG");
		//newLocation.setAddress3("!@#$%^&* about address 3");
		newLocation.setCity("!@#$%^&*JG");
		newLocation.setState("!@#$%^&*JG");
		newLocation.setPostalCode("02895");
		newLocation.setTimeZone("#JG");
		newLocation.setPhoneNumber("2345676787");
		newLocation.setFaxNumber("9878767876");
		newLocation.setContactName("M!@#$%^&* Sharma G");
		newLocation.setContactExt("@#$234JG");
		newLocation.setFaxVerified(false);
		newLocation.setMonHours(new Hours(false,"\"\"","\"\""));
		newLocation.setTueHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setWedHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setThurHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setFriHours(new Hours(true,"09:00:00","20:00:00"));
		newLocation.setSatHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSunHours(new Hours(false,"09:00:00","20:00:00"));

		return newLocation;
	}

	//Helper Method Without Special Character

	private  OfficeLocation  getNewRegularOfficeLocation(int index) {

		OfficeLocation newLocation = new OfficeLocation();
		OfficeLocation officeLocation = testData.getOfficeLocations().get(index);

		newLocation.setId(officeLocation.getId());

		newLocation.setDefaultVal(true);
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
		newLocation.setFaxVerified(true);
		newLocation.setMonHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setTueHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setWedHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setThurHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setFriHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSatHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSunHours(new Hours(false,"09:00:00","20:00:00"));

		return newLocation;
	}


	/**
	 * Submit API request and validate the results
	 * 
	 * @param apiBody API request body
	 * @return list of {@link DynamicNode} of test
	 */
	private List<DynamicNode> happyPathReplace(String apiBody, String officeLocationId)
	{
		// Call API
		ApiPutStep apiPutStep = new ApiPutStep(getGenericHeaders(),
										PROVIDERS_REPLACE_OFFICE_LOCATION_POST_ENDPOINT,
										apiBody,
										new Object[] {testData.getId(), officeLocationId},
										201,
										HTTP_201_CONTENT_CREATED);
		apiPutStep.run();

		if ( apiPutStep.getResponseStatusCode() != 201 )
		{
			return apiPutStep.getTestResults();
		}

		// Get the member from the Cosmos
		Provider actual = ProviderQueries.getProviderById(testData.getId());

		logger.info("API response: " + actual.toString());

		// Validate the Cosmos data after the API patch
		apiPutStep.getTestResults().add(dynamicContainer("Cosmos db", testData.compare( actual )));

		return apiPutStep.getTestResults();
	}

	/**
	 * Create API body
	 * 
	 * 
	 *  @param officeLocation {@link OfficeLocation} that will be used to generate API request
	 *  @return JsonObject that represent the params
	 */
	private JsonObject createApiBody(OfficeLocation officeLocation)
	{
		JsonObject requestBody = new JsonObject();

		//requestBody.addProperty("id", officeLocation.getId());

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
	 * @throws JsonProcessingException if exception occurs
	 */
	private void createTestData() throws JsonProcessingException
	{
		// Get random provider with atleast one officeLocation
		String whereClauseExtension = "where ARRAY_LENGTH(c.officeLocations) > 0";
		originalProvider = ProviderQueries.getProvider(whereClauseExtension);

		logger.info("Provider: " + originalProvider.toString());

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
