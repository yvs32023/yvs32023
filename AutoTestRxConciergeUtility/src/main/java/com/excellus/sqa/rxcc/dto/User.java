/** 

 *  

 * @copyright 2022 Excellus BCBS 

 * All rights reserved. 

 *  

 */ 


package com.excellus.sqa.rxcc.dto; 

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicNode;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/** 

 *  DTO for LBS User

 *  

 * @author Ponnani Ananthanarayanan (panantha) 

 * @since 02/28/2022 

 */
@JsonPropertyOrder({ "oid",
    "name",
    "preferredUsername",
    "preferred_username",
    "lastSearch",
    "savedSearches"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User extends Item {

	@JsonProperty(required=true)
	private String oid;
	
	@JsonProperty(required=true)
	private String name;
	
	@JsonProperty(required=true)
	private String preferredUsername;
	
	private String preferred_username; //temporaryfix due to bug on 
	
	@JsonProperty(required=true)
	private String lastSearch;
	
	@JsonProperty(required=true)
	private List<SavedSearch> savedSearches;




	/*
	 * Validations
	 */

	/**
	 * Compare two objects
	 * 
	 * @param user
	 * @return
	 */
	public List<DynamicNode> compare(User user)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();


		tests.add(dynamicTest("oid: [" + oid + "]", () -> assertEquals(oid, user.getOid(), getApiInfo(user))));
		tests.add(dynamicTest("name: [" + name + "]", () -> assertEquals(name, user.getName(), getApiInfo(user))));
		tests.add(dynamicTest("preferredUsername: [" + preferredUsername + "]", () -> assertEquals(preferredUsername, user.getPreferredUsername(), getApiInfo(user))));
		tests.add(dynamicTest("preferredUsername: [" + preferred_username + "]", () -> assertEquals(preferred_username, user.getPreferred_username(), getApiInfo(user))));
		tests.add(dynamicTest("lastSearch: [" + lastSearch + "]", () -> assertEquals(lastSearch, user.getLastSearch(), getApiInfo(user))));
		tests.add(dynamicTest("savedSearches (count): [" + savedSearches.size() + "]", () -> assertEquals(savedSearches.size(), user.getSavedSearches().size(), getApiInfo(user))));

		/*
		 * Validate each of the saved Searches
		 */

		for ( SavedSearch expected : savedSearches )
		{
			boolean found = false;
			for ( SavedSearch actual : user.getSavedSearches())
			{
				if ( expected.equals(actual) )
				{
					found = true;

					tests.add(dynamicContainer("Saved Searches [" + oid + "]", expected.compare(actual)) );	// compare the office location

					break;
				}
			}

			if ( !found ) {
				tests.add(dynamicTest("Saved Searches not found", () -> fail("The Saved Searches with ID " + oid + " is missing")));
			}
		}

		tests.addAll( super.compare(user) );

		return tests;
	}


	/*
	 * Helper methods
	 */

	@Override 
	public boolean equals(Object obj)
	{
		if ( obj instanceof User )
		{
			User user = (User) obj;

			if (this.getOid() != user.getOid()) {
				return false;
			}	
			return true;
		}

		return false;
	}




	/*
	 * Setter / Getter
	 */

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPreferred_username() {
		return preferred_username;
	}

	public void setPreferred_username(String preferred_username) {
		this.preferred_username = preferred_username;
	}

	public String getPreferredUsername() {
		return preferredUsername;
	}

	public void setPreferredUsername(String preferredUsername) {
		this.preferredUsername = preferredUsername;
	}

	public String getLastSearch() {
		return lastSearch;
	}

	public void setLastSearch(String lastSearch) {
		this.lastSearch = lastSearch;
	}

	public List<SavedSearch> getSavedSearches() {
		return savedSearches;
	}

	public void setSavedSearches(List<SavedSearch> savedSearches) {
		this.savedSearches = savedSearches;
	}

}