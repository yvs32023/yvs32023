/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.dto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DTO for LBS Lookup items/documents
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/05/2022
 */
public class Lookup extends Item{

	private static final Logger logger = LoggerFactory.getLogger(Lookup.class);

	private String pkId;
	private LookupType type;
	private Map<String, String> lookup;




	/**
	 * Constructs a Lookup object.
	 */
	public Lookup(){
		this.lookup = new HashMap<String, String>();
	}

	/**
	 * Constructs a Lookup object.
	 * @param lookup The lookup to use.
	 */
	public Lookup(Map<String, String> lookup){
		this.lookup = new HashMap<String, String>();
	}

	/**
	 * Adds a key-value pair to the lookup.
	 * @param key The key to insert.
	 * @param value The value to associate with the key.
	 */
	public void put(String key, String value) {
		lookup.put(key, value);
	}

	/**
	 * Gets the value associated with a given key.
	 * @param key The key to search for.
	 * @return The associated value, or null if the key is not found.
	 */
	public String get(String key) {
		return lookup.get(key);
	}


	/*
	 * Validations
	 */

	/**
	 * Compare two objects
	 *
	 * @param lookup
	 * @return
	 */
	public List<DynamicNode> compare(Lookup lookup) {
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("pkId: [" + this.pkId + "]", () -> assertEquals(pkId, lookup.getPkId(), getApiInfo(lookup))));
		tests.add(dynamicTest("type: [" + this.type + "]", () -> assertEquals(type, lookup.getType(), getApiInfo(lookup))));
		tests.add(dynamicTest("lookup", () -> assertThat(getApiInfo(lookup), this.lookup, is(lookup.getLookup())) ));

		tests.addAll( super.compareLookUpSpecific(lookup) );

		return tests;
	}


	/*
	 * Setter / Getter
	 */

	public String getPkId() {
		return pkId;
	}

	public void setPkId(String pkId) {
		this.pkId = pkId;
	}

	public LookupType getType() {
		return type;
	}

	public void setType(LookupType type) {
		this.type = type;
	}

	public Map<String, String> getLookup() {
		return lookup;
	}

	public void setLookup(Map<String, String> lookup) {
		this.lookup = lookup;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public static void main(String[] args)   {
		String lookupString = "{\n" +
				" \"pkId\": \"lookup\",\n" +
				" \"type\": \"taxonomy\",\n" +
				" \"lookup\": {\n" +
				" \"123\": \"Podiatrist\",\n" +
				" \"207Q00000X\": \"Family Medicine\"\n" +
				" },\n" +
				" \"ver\": \"1.0\",\n" +
				" \"lastUpdated\": \"2021-07-23T04:57:08Z\",\n" +
				" \"lastUpdatedBy\": \"jsmith\",\n" +
				" \"id\": \"taxonomy\"\n" +
				"}";

		String lookupString2 = "{\n" +
				" \"pkId\": \"lookup\",\n" +
				" \"type\": \"taxonomy\",\n" +
				" \"lookup\": {\n" +
				" \"123\": \"Podiatrist\",\n" +
				" \"207Q00000X\": \"Family Medicine\"\n" +
				" },\n" +
				" \"ver\": \"1.0\",\n" +
				" \"lastUpdated\": \"2021-07-23T04:57:08Z\",\n" +
				" \"lastUpdatedBy\": \"jsmith\",\n" +
				" \"id\": \"taxonomy\"\n" +
				"}";

		ObjectMapper mapper = new ObjectMapper();

		try {
			Lookup lookup = mapper.readValue(lookupString, Lookup.class);
			Lookup lookup2 = mapper.readValue(lookupString2, Lookup.class);

			lookup.compare(lookup2);

			logger.info(lookup.toString());
			logger.info(lookup2.toString());
			logger.info( lookup.compare(lookup2).toString() );
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public enum LookupType {
		credentials,
		taxonomy,
		daw,
		provider_status,
		pharmacy_status,
		formulary,
		queueStatusCode,
		validateStatusCode,
		retriggerAfter180Status,
		declineStatusCode,
		retriggerCustomStatus,
		form_status,
		correspondence_outcome,
		correspondence_type,
		correspondence_title,
		interventionActionApproved,
		interventionActionDeclined,
		nofaxprovider_status,
		nofaxintervention_status,
		queue_status_filter;
	}
}

