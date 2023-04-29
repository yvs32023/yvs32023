/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import com.excellus.sqa.jsonlocatorhelper.JsonHelper;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.ProviderQueries;
import com.excellus.sqa.rxcc.dto.Hours;
import com.excellus.sqa.rxcc.dto.OfficeLocation;
import com.excellus.sqa.rxcc.dto.Provider;
import com.excellus.sqa.selenium.SeleniumPageHelperAndWaiter;
import com.excellus.sqa.utilities.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;


/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 05/12/2022
 */
@Tag("ALL")
@Tag("PROVIDER")
@Tag("SEARCH")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PostOfficeLocationInsertFollowedPostProviderSearch")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class InsertOfficeLocationFollowedProviderSearchTest extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(InsertOfficeLocationFollowedProviderSearchTest.class);

	static Provider originalProvider;
	static Provider testData;

	@AfterEach
	public void restoreOriginalOfficeLocation()
	{
		if ( originalProvider != null )
		{
			ProviderQueries.replaceProvider(originalProvider);
			originalProvider = null;
		}

		testData = null;
	}

	@TestFactory
	@DisplayName("7439: Prov_InsertOfficeLocation Happy Path : ALL Properties  followed by Provider Search(With filters on lastName,city and state) ")
	@Order(1)
	public List<DynamicNode> happyPathInsertOfficeLocation() throws JsonProcessingException
	{
		List<DynamicNode> result = new ArrayList<>();

		// Create test data
		createTestData();

		/*
		 * Insert new office location
		 */

		// Setup test data
		int officeLocationId = testData.getOfficeLocations().size() + 1;

		OfficeLocation newLocation = new OfficeLocation();
		newLocation.setId(String.valueOf(officeLocationId));
		newLocation.setDefaultVal(true);	// GC (04/04/23) set as default so that the Cognisant is updated with this default office
		newLocation.setStatus(true);
		newLocation.setAddress1("Something about address 1");
		newLocation.setAddress2("Something about address 2");
		//newLocation.setAddress3("Something about address 3");
		newLocation.setCity("CityZZZZ");
		newLocation.setState("MA");
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
		JSONObject body = createApiBody(newLocation);

		// Call API - insert new office as default
		result.add(happyPathInsert(body));

		SeleniumPageHelperAndWaiter.pause(20000);	// Allow the Cognisant search indexer to performed

		/*
		 * Perform provider search to make sure the Cognisant search indexer is updated after new office was inserted
		 */

		//  Use Provider.first as query
		JSONObject searchBody = new JSONObject();
		searchBody.put("query", testData.getLastName().toUpperCase());

		//Use filter fields (upper case) in Provider.lastname 
		JSONArray lastNames = new JSONArray();
		lastNames.put(testData.getLastName());

		//Use filter fields (upper case) in Provider.city
		JSONArray cities = new JSONArray();
		cities.put(testData.getOfficeLocations().get(testData.getOfficeLocations().size() - 1).getCity());

		//Use filter fields (upper case) in Provider.city
		JSONArray state = new JSONArray();
		state.put(testData.getOfficeLocations().get((testData.getOfficeLocations().size() - 1)).getState());

		JSONObject filter = new JSONObject();
		filter.put("lastNames", lastNames);
		filter.put("city", cities);
		filter.put("state", state);

		searchBody.put("filter", filter);

		// Call API
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_SEARCH_POST_ENDPOINT, searchBody.toString(), null, 200, null);
		apiPostStep.run();

		// Validate
		result.add(dynamicContainer("Search provider (Cognisant search indexer)", SearchProviderTest.validateSearchResult(apiPostStep, testData.getLastName())));

		return result;
	}

	@TestFactory
	@DisplayName("7439: Prov_InsertOfficeLocation Happy Path : Required Properties  followed by Provider Search(With filters on faxNumber and phoneNumber) ")
	@Order(2)
	public List<DynamicNode> happyPathInsertOfficeLocationRequired() throws JsonProcessingException
	{
		List<DynamicNode> result = new ArrayList<>();

		// Create test data
		createTestData();

		/*
		 * Insert new office location
		 */

		// Setup test data
		int officeLocationId = testData.getOfficeLocations().size() + 1;

		OfficeLocation newLocation = new OfficeLocation();
		newLocation.setId(String.valueOf(officeLocationId));
		newLocation.setDefaultVal(true);	// GC (04/04/23) set as default so that the Cognisant is updated with this default office
		newLocation.setAddress1("Something about address 1");
		newLocation.setPhoneNumber("2345676787");
		newLocation.setFaxNumber("9878767876");
		newLocation.setMonHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setTueHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setWedHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setThurHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setFriHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSatHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSunHours(new Hours(false,"09:00:00","20:00:00"));

		testData.getOfficeLocations().add(newLocation);

		// Create API body
		JSONObject body = createApiBody(newLocation);

		result.add( happyPathInsert(body) );

		SeleniumPageHelperAndWaiter.pause(20000);	// Allow the Cognisant search indexer to performed

		/*
		 * Perform provider search to make sure the Cognisant search indexer is updated after new office was inserted
		 */

		//  Use Provider.first as query
		JSONObject searchBody = new JSONObject();
		searchBody.put("query", testData.getLastName().toUpperCase());

		//Use filter fields (upper case) in Provider.faxNumber
		JSONArray faxNumber = new JSONArray();
		faxNumber.put(testData.getOfficeLocations().get(testData.getOfficeLocations().size() - 1).getFaxNumber());

		//Use filter fields (upper case) in Provider.phoneNumber
		JSONArray phoneNumber = new JSONArray();
		phoneNumber.put(testData.getOfficeLocations().get((testData.getOfficeLocations().size() - 1)).getPhoneNumber());

		JSONObject filter = new JSONObject();
		filter.put("faxNumber", faxNumber);
		filter.put("phoneNumber", phoneNumber);

		searchBody.put("filter", filter);

		// Call API
		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_SEARCH_POST_ENDPOINT, searchBody.toString(), null, 200, null);
		apiPostStep.run();

		// validate
		result.add(dynamicContainer("Search provider (Cognisant search indexer)", SearchProviderTest.validateSearchResult(apiPostStep, testData.getLastName())));

		return result;
	}

	@TestFactory
	@DisplayName("7439: Prov_InsertOfficeLocation Happy Path : Required Properties followed by Provider Search(With filters on faxNumber,city,state and phoneNumber)")
	@Order(3)
	public List<DynamicNode> withBodyFirstNameWithAscSort() throws JsonProcessingException
	{

		List<DynamicNode> result = new ArrayList<>();

		// Create test data
		createTestData();

		/*
		 * Insert new office location
		 */

		// Setup test data
		int officeLocationId = testData.getOfficeLocations().size() + 1;

		OfficeLocation newLocation = new OfficeLocation();
		newLocation.setId(String.valueOf(officeLocationId));
		newLocation.setDefaultVal(true);	// GC (04/04/23) set as default so that the Cognisant is updated with this default office
		newLocation.setStatus(true);
		newLocation.setAddress1("Something about address 1");
		newLocation.setCity("City");
		newLocation.setState("VA");
		newLocation.setPhoneNumber("2345676787");
		newLocation.setFaxNumber("9878767876");
		newLocation.setMonHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setTueHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setWedHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setThurHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setFriHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSatHours(new Hours(false,"09:00:00","20:00:00"));
		newLocation.setSunHours(new Hours(false,"09:00:00","20:00:00"));

		testData.getOfficeLocations().add(newLocation);

		// Create API body
		JSONObject body = createApiBody(newLocation);

		result.add( happyPathInsert(body));

		logger.debug("Patch response: " + result);

		SeleniumPageHelperAndWaiter.pause(20000);	// Allow the Cognisant search indexer to performed

		/*
		 * Perform provider search to make sure the Cognisant search indexer is updated after new office was inserted
		 */

		//Use Provider.first as query
		JSONObject searchBody = new JSONObject();
		searchBody.put("query", testData.getLastName().toUpperCase());

		//Use filter fields (upper case) in Provider.faxNumber
		JSONArray faxNumber = new JSONArray();
		faxNumber.put(testData.getOfficeLocations().get(testData.getOfficeLocations().size() - 1).getFaxNumber());

		//Use filter fields (upper case) in Provider.phoneNumber
		JSONArray phoneNumber = new JSONArray();
		phoneNumber.put(testData.getOfficeLocations().get((testData.getOfficeLocations().size() - 1)).getPhoneNumber());

		//Use filter fields (upper case) in Provider.city
		JSONArray cities = new JSONArray();
		cities.put(testData.getOfficeLocations().get(testData.getOfficeLocations().size() - 1).getCity());


		//Use filter fields (upper case) in Provider.city
		JSONArray state = new JSONArray();
		state.put(testData.getOfficeLocations().get((testData.getOfficeLocations().size() - 1)).getState());

		JSONObject filter = new JSONObject();
		filter.put("faxNumber", faxNumber);
		filter.put("phoneNumber", phoneNumber);
		filter.put("city", cities);
		filter.put("state", state);

		searchBody.put("filter", filter);

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDER_SEARCH_POST_ENDPOINT, searchBody.toString(), null, 200, null);
		apiPostStep.run();

		// validate
		result.add(dynamicContainer("Search provider (Cognisant search indexer)", SearchProviderTest.validateSearchResult(apiPostStep, testData.getLastName())));

		return result;
	}

	/**
	 * Submit API request to insert new office location and validate the results
	 * 
	 * @param apiBody API request body
	 * @return return {@link DynamicContainer} of tests
	 */
	private DynamicContainer happyPathInsert(JSONObject apiBody)
	{
		final String testName = "Insert new office";

		ApiPostStep apiPostStep = new ApiPostStep(getGenericHeaders(), PROVIDERS_INSERT_OFFICE_LOCATION_POST_ENDPOINT, apiBody.toString(), new Object[]{testData.getId()}, 201, null);
		apiPostStep.run();

		List<DynamicNode> result = new ArrayList<>(apiPostStep.getTestResults());

		if ( apiPostStep.getResponseStatusCode() != 201 )
		{
			return dynamicContainer(testName, result);
		}
		else
		{
			String newAddress = JsonHelper.getJsonValueString(apiBody, "address1");
			// update existing office set as current default to non-default since we added a new office as the default
			testData.getOfficeLocations().forEach(
					office -> {
						if (office.isDefaultVal() && !StringUtils.equals(office.getAddress1(), newAddress))
						{
							office.setDefaultVal(false);
						}
					});
		}

		Provider actual = ProviderQueries.getProviderById(testData.getId());

		logger.debug("API response: " + actual.toString());

		// Validate the Cosmos data after the API patch
		result.add(dynamicContainer("Cosmos db", testData.compare( actual )));

		return dynamicContainer(testName, result);
	}

	/**
	 * Create API body
	 * 
	 *  @param officeLocation to be updated
	 *  @return JsonObject that represent the params
	 */
	private JSONObject createApiBody(OfficeLocation officeLocation)
	{
		JSONObject requestBody = new JSONObject();

		requestBody.put("default", officeLocation.isDefaultVal());

		requestBody.put("status", officeLocation.isStatus());

		requestBody.put("address1", officeLocation.getAddress1());

		if ( StringUtils.isNotBlank(officeLocation.getAddress2()) )
			requestBody.put("address2", officeLocation.getAddress2());

		//Out of scope for now 4/14/2022 
		//if ( StringUtils.isNotBlank(officeLocation.getAddress3()) ) 
		//requestBody.addProperty("address3", officeLocation.getAddress3());

		if ( StringUtils.isNotBlank(officeLocation.getCity()) )
			requestBody.put("city", officeLocation.getCity());

		if ( StringUtils.isNotBlank(officeLocation.getState()))
			requestBody.put("state", officeLocation.getState());

		if ( StringUtils.isNotBlank(officeLocation.getPostalCode()) )
			requestBody.put("postalCode", officeLocation.getPostalCode());

		if ( StringUtils.isNotBlank(officeLocation.getTimeZone()) )
			requestBody.put("timeZone", officeLocation.getTimeZone());

		if ( StringUtils.isNotBlank(officeLocation.getPhoneNumber()) )
			requestBody.put("phoneNumber", officeLocation.getPhoneNumber());

		if ( StringUtils.isNotBlank( officeLocation.getFaxNumber()) )
			requestBody.put("faxNumber", officeLocation.getFaxNumber());

		if ( StringUtils.isNotBlank( officeLocation.getContactName()) )
			requestBody.put("contactName", officeLocation.getContactName());

		if ( StringUtils.isNotBlank(officeLocation.getContactNumber()) )
			requestBody.put("contactNumber", officeLocation.getContactNumber());

		if ( StringUtils.isNotBlank(officeLocation.getContactExt()) )
			requestBody.put("contactExt", officeLocation.getContactExt());

		requestBody.put("faxVerified", officeLocation.isFaxVerified());

		// Adding Mon Hours
		JSONObject jsonMonHours = new JSONObject();

		if ( StringUtils.isNotBlank(officeLocation.getMonHours().getStartTime()) )

			jsonMonHours.put("startTime", officeLocation.getMonHours().getStartTime());

		if ( StringUtils.isNotBlank(officeLocation.getMonHours().getEndTime()))
			jsonMonHours.put("endTime", officeLocation.getMonHours().getEndTime());

		jsonMonHours.put("closed", officeLocation.getMonHours().isClosed());

		requestBody.put("MonHours", jsonMonHours);

		// Adding Tue Hours
		JSONObject jsonTueHours = new JSONObject();

		if ( StringUtils.isNotBlank(officeLocation.getTueHours().getStartTime()) )
			jsonTueHours.put("startTime",officeLocation.getTueHours().getStartTime());

		if ( StringUtils.isNotBlank(officeLocation.getTueHours().getEndTime()))
			jsonTueHours.put("endTime",officeLocation.getTueHours().getEndTime());

		jsonTueHours.put("closed", officeLocation.getTueHours().isClosed());

		requestBody.put("TueHours", jsonTueHours);

		// Adding Wed Hours
		JSONObject jsonWedHours = new JSONObject();

		if ( StringUtils.isNotBlank(officeLocation.getWedHours().getStartTime()) )
			jsonWedHours.put("startTime",officeLocation.getWedHours().getStartTime());

		if ( StringUtils.isNotBlank(officeLocation.getWedHours().getEndTime()) )
			jsonWedHours.put("endTime",officeLocation.getWedHours().getEndTime());

		jsonWedHours.put("closed", officeLocation.getWedHours().isClosed());

		requestBody.put("WedHours", jsonWedHours);

		// Adding Thur Hours
		JSONObject jsonThurHours = new JSONObject();

		jsonThurHours.put("closed", officeLocation.getThurHours().isClosed());

		if ( StringUtils.isNotBlank(officeLocation.getThurHours().getStartTime()) )
			jsonThurHours.put("startTime",officeLocation.getThurHours().getStartTime());

		if ( StringUtils.isNotBlank(officeLocation.getWedHours().getEndTime()) )
			jsonThurHours.put("endTime",officeLocation.getThurHours().getEndTime());


		requestBody.put("ThurHours", jsonThurHours);

		// Adding Fri Hours
		JSONObject jsonFriHours = new JSONObject();

		if ( StringUtils.isNotBlank(officeLocation.getFriHours().getStartTime()) )
			jsonFriHours.put("startTime",officeLocation.getFriHours().getStartTime());

		if ( StringUtils.isNotBlank(officeLocation.getFriHours().getEndTime()))
			jsonFriHours.put("endTime",officeLocation.getFriHours().getEndTime());

		jsonFriHours.put("closed", officeLocation.getFriHours().isClosed());

		requestBody.put("FriHours", jsonFriHours);

		// Adding Sat Hours
		JSONObject jsonSatHours = new JSONObject();

		if ( StringUtils.isNotBlank(officeLocation.getSatHours().getStartTime()) )
			jsonSatHours.put("startTime",officeLocation.getSatHours().getStartTime());

		if ( StringUtils.isNotBlank(officeLocation.getSatHours().getEndTime()))
			jsonSatHours.put("endTime",officeLocation.getSatHours().getEndTime());

		jsonSatHours.put("closed", officeLocation.getSatHours().isClosed());

		requestBody.put("SatHours", jsonSatHours);

		// Adding Sun Hours
		JSONObject jsonSunHours = new JSONObject();

		if ( StringUtils.isNotBlank(officeLocation.getSunHours().getStartTime()) )
			jsonSunHours.put("startTime",officeLocation.getSunHours().getStartTime());

		if ( StringUtils.isNotBlank(officeLocation.getSunHours().getEndTime()))
			jsonSunHours.put("endTime",officeLocation.getSunHours().getEndTime());

		jsonSunHours.put("closed", officeLocation.getSunHours().isClosed());

		requestBody.put("SunHours", jsonSunHours);

		return requestBody;
	}

	/**
	 * Create a test data to use
	 * @throws JsonProcessingException if exception occurs
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
