/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;

import com.excellus.sqa.restapi.steps.ApiPutStep;
import com.excellus.sqa.restapi.steps.IApiStep;
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
 * @since 05/13/2022
 */
@Tag("ALL")
@Tag("PROVIDER")
@Tag("SEARCH")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("PutOfficeLocationReplaceFollowedPostProviderSearch")
@UserRole(role = {"RXCC_FULL_SINGLE"})
public class ReplaceOfficeLocationFollowedProviderSearchTest extends RxConciergeAPITestBaseV2
{

	private static final Logger logger = LoggerFactory.getLogger(ReplaceOfficeLocationFollowedProviderSearchTest.class);

	private final String TEST_NAME_REPLACE_OFFICE = "Replace Office Location";

	static Provider originalProvider;
	static Provider testData;

	@AfterEach
	public void resetoriginalOfficeLocation()
	{
		if ( originalProvider != null )
		{
			ProviderQueries.replaceProvider(originalProvider);
			originalProvider = null;
		}

		testData = null;
	}

	@TestFactory
	@DisplayName("5880: Prov_ReplaceOfficeLocation Happy Path : ALL Properties  followed by Provider Search(With filters on last Name, city and state)")
	@Order(1)
	public List<DynamicNode> happyPathReplaceOfficeLocation() throws Exception
	{

		List<DynamicNode> result = new ArrayList<>();

		// Create test data
		createTestData();

		// Setup test data
		int index = testData.getOfficeLocations().size() - 1;

		/*
		 * Replace office location
		 */

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

		result.add(dynamicContainer(TEST_NAME_REPLACE_OFFICE, happyPathReplace(body.toString(), newLocation.getId())));

		SeleniumPageHelperAndWaiter.pause(20000);	// Allow the Cognisant search indexer to performed

		/*
		 * Perform provider search to make sure the Cognisant search indexer is updated after replacing an office location
		 */

		//  Use Provider.first as query
		JsonObject searchBody = new JsonObject();
		searchBody.addProperty("query", testData.getFirstName().toUpperCase());

		//Use filter fields (upper case) in Provider.lastname 
		JsonArray lastName = new JsonArray();
		lastName.add(testData.getLastName());

		//Use filter fields (upper case) in Provider.city
		JsonArray cities = new JsonArray();
		cities.add(testData.getOfficeLocations().get(testData.getOfficeLocations().size() - 1).getCity());

		//Use filter fields (upper case) in Provider.city
		JsonArray state = new JsonArray();
		state.add(testData.getOfficeLocations().get((testData.getOfficeLocations().size() - 1)).getState());

		JsonObject filter = new JsonObject();
		filter.add("lastName", lastName);
		filter.add("city", cities);
		filter.add("state", state);

		searchBody.add("filter", filter);

		// Call API
		IApiStep apiStep = SearchProviderTest.providerSearch(searchBody);

		// Validate
		result.add(dynamicContainer("SearchProviderResponse validation", SearchProviderTest.validateSearchResult(apiStep, testData.getFirstName())));

		return result;
	}

	@TestFactory
	@DisplayName("5880:: Prov_ReplaceOfficeLocation Happy Path : Required Properties followed by Provider Search(With filters on faxNumber and phoneNumber)")
	@Order(2)
	public List<DynamicNode> happyPathReplaceOfficeLocationRequired() throws Exception
	{

		List<DynamicNode> result = new ArrayList<>();

		// Create test data
		createTestData();

		// Setup test data
		int index = testData.getOfficeLocations().size() - 1;

		OfficeLocation newLocation = new OfficeLocation();
		OfficeLocation officeLocation = testData.getOfficeLocations().get(index);

		newLocation.setId(officeLocation.getId());
		newLocation.setDefaultVal(true);
		newLocation.setAddress1("Something !@#$%^&* address 1");
		newLocation.setPhoneNumber("2345676787");
		newLocation.setFaxNumber("9878767876");
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

		result.add(dynamicContainer(TEST_NAME_REPLACE_OFFICE, happyPathReplace(body.toString(), newLocation.getId())));

		SeleniumPageHelperAndWaiter.pause(20000);	// Allow the Cognisant search indexer to performed

		/*
		 * Perform provider search to make sure the Cognisant search indexer is updated after replacing an office location
		 */

		//  Use Provider.first as query
		JsonObject searchBody = new JsonObject();
		searchBody.addProperty("query", testData.getLastName().toUpperCase());

		//Use filter fields (upper case) in Provider.faxNumber
		JsonArray faxNumber = new JsonArray();
		faxNumber.add(testData.getOfficeLocations().get(testData.getOfficeLocations().size() - 1).getFaxNumber());

		//Use filter fields (upper case) in Provider.phoneNumber
		JsonArray phoneNumber = new JsonArray();
		phoneNumber.add(testData.getOfficeLocations().get((testData.getOfficeLocations().size() - 1)).getPhoneNumber());

		JsonObject filter = new JsonObject();
		filter.add("faxNumber", faxNumber);
		filter.add("phoneNumber", phoneNumber);

		searchBody.add("filter", filter);

		// Call API
		IApiStep apiStep = SearchProviderTest.providerSearch(searchBody);

		// Validate
		result.add(dynamicContainer("SearchProviderResponse validation", SearchProviderTest.validateSearchResult(apiStep, testData.getFirstName())));

		return result;


	}

	@TestFactory
	@DisplayName("5880:: Prov_ReplaceOfficeLocation Happy Path : Required Properties followed by Provider Search(With filters on FaxNumber, City, State and PhoneNumber)")
	@Order(3)
	public List<DynamicNode> happyPathReplaceOfficeLocationSort() throws Exception
	{

		List<DynamicNode> result = new ArrayList<>();

		// Create test data
		createTestData();

		// Setup test data
		int index = testData.getOfficeLocations().size() - 1;

		OfficeLocation newLocation = new OfficeLocation();
		OfficeLocation officeLocation = testData.getOfficeLocations().get(index);

		newLocation.setId(officeLocation.getId());
		newLocation.setDefaultVal(true);
		newLocation.setAddress1("Something !@#$%^&* address 1");
		newLocation.setCity("City");
		newLocation.setState("VA");
		newLocation.setPhoneNumber("2345676787");
		newLocation.setFaxNumber("9878767876");
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

		result.add(dynamicContainer(TEST_NAME_REPLACE_OFFICE, happyPathReplace(body.toString(), newLocation.getId())));

		SeleniumPageHelperAndWaiter.pause(20000);	// Allow the Cognisant search indexer to performed

		/*
		 * Perform provider search to make sure the Cognisant search indexer is updated after replacing an office location
		 */

		//  Use Provider.first as query
		JsonObject searchBody = new JsonObject();
		searchBody.addProperty("query", testData.getLastName().toUpperCase());

		//Use filter fields (upper case) in Provider.faxNumber
		JsonArray faxNumber = new JsonArray();
		faxNumber.add(testData.getOfficeLocations().get(testData.getOfficeLocations().size() - 1).getFaxNumber());

		//Use filter fields (upper case) in Provider.phoneNumber
		JsonArray phoneNumber = new JsonArray();
		phoneNumber.add(testData.getOfficeLocations().get((testData.getOfficeLocations().size() - 1)).getPhoneNumber());

		//Use filter fields (upper case) in Provider.city
		JsonArray cities = new JsonArray();
		cities.add(testData.getOfficeLocations().get(testData.getOfficeLocations().size() - 1).getCity());


		//Use filter fields (upper case) in Provider.city
		JsonArray state = new JsonArray();
		state.add(testData.getOfficeLocations().get((testData.getOfficeLocations().size() - 1)).getState());

		JsonObject filter = new JsonObject();
		filter.add("faxNumber", faxNumber);
		filter.add("phoneNumber", phoneNumber);
		filter.add("city", cities);
		filter.add("state", state);


		searchBody.add("filter", filter);

		// Call API
		IApiStep apiStep = SearchProviderTest.providerSearch(searchBody);

		// Validate
		result.add(dynamicContainer("SearchProviderResponse validation", SearchProviderTest.validateSearchResult(apiStep, testData.getFirstName())));

		return result;
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
	 * @param officeLocationId that will be replaced
	 * @return list of {@link DynamicNode} of tests
	 */
	private List<DynamicNode> happyPathReplace(String apiBody, String officeLocationId)
	{
		// Call API
		ApiPutStep apiPutStep = new ApiPutStep(getGenericHeaders(), PROVIDERS_REPLACE_OFFICE_LOCATION_POST_ENDPOINT, apiBody, new Object[] {testData.getId(), officeLocationId}, 201, HTTP_201_CONTENT_CREATED);
		apiPutStep.run();

		List<DynamicNode> testResults = new ArrayList<>(apiPutStep.getTestResults());

		if ( apiPutStep.getResponseStatusCode() != 201 )
		{
			return testResults;
		}

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
	 * 
	 *  @param officeLocation {@link OfficeLocation} to be used to create API request
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
