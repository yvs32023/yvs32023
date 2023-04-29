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
import org.junit.platform.commons.util.StringUtils;

import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * DTO for provider
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/03/2022
 */
@JsonPropertyOrder({ "npi",
    "type",
    "statusInd",
    "firstName",
    "lastName",
    "credential",
    "taxonomyCode",
    "taxonomyDescr",
	"officeLocations"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Provider extends Item {

    @JsonProperty(required=true)
    private String npi;
    
	@JsonProperty(required=true)
    private String type;

    @JsonProperty(required=true)
    private String statusInd;

    @JsonProperty(required=true)
    private String firstName;
    
    //private String middleInitial;	// GC (03/31/22)

    @JsonProperty(required=true)
    private String lastName;

    private String credential;

    @JsonProperty(required=true)
    private String taxonomyCode;

    @JsonProperty(required=true)
    private String taxonomyDescr;

    @JsonProperty(required=true)
    private List<OfficeLocation> officeLocations;

	/*
	 * Validations
	 */

    /**
	 * Compare two objects
	 * 
	 * @param provider {@link Provider} to be compared to this instance
	 * @return list of {@link DynamicNode} of test
	 */
	public List<DynamicNode> compare(Provider provider)
	{
		List<DynamicNode> tests = new ArrayList<>();
		
		
		
		tests.add(dynamicTest("npi: [" + npi + "]", () -> assertEquals(npi, provider.getNpi(), getApiInfo(provider))));
		tests.add(dynamicTest("type: [" + type + "]", () -> assertEquals(type, provider.getType(), getApiInfo(provider))));
		tests.add(dynamicTest("statusInd: [" + statusInd + "]", () -> assertEquals(statusInd, provider.getStatusInd(), getApiInfo(provider))));
		tests.add(dynamicTest("firstName: [" + firstName + "]", () -> assertEquals(firstName, provider.getFirstName(), getApiInfo(provider))));
		tests.add(dynamicTest("lastName: [" + lastName + "]", () -> assertEquals(lastName, provider.getLastName(), getApiInfo(provider))));
		tests.add(dynamicTest("credential: [" + credential + "]", () -> assertEquals(credential, provider.getCredential(), getApiInfo(provider))));
		tests.add(dynamicTest("taxonomyCode: [" + taxonomyCode + "]", () -> assertEquals(taxonomyCode, provider.getTaxonomyCode(), getApiInfo(provider))));
		tests.add(dynamicTest("taxonomyDescr: [" + taxonomyDescr + "]", () -> assertEquals(taxonomyDescr, provider.getTaxonomyDescr(), getApiInfo(provider))));
		tests.add(dynamicTest("officeLocations (count): [" + officeLocations.size() + "]", () -> assertEquals(officeLocations.size(), provider.getOfficeLocations().size(), getApiInfo(provider))));
		
		/*
		 * Validate each of the office locations
		 */
		
		for ( OfficeLocation expected : officeLocations )
		{
			boolean found = false;
			for ( OfficeLocation actual : provider.getOfficeLocations() )
			{
				if ( expected.equals(actual) )
				{
					found = true;
					
					tests.add(dynamicContainer("Office Location [" + id + "]", expected.compare(actual)) );	// compare the office location
					
					break;
				}
			}
			
			if ( !found ) {
				tests.add(dynamicTest("officeLocation not found", () -> fail("The office location with ID " + id + " is missing \n" + getApiInfo(provider))));
			}
		}
		
		// Perform custom validation on the item
		
		if ( StringUtils.isNotBlank(lastUpdated) && StringUtils.isNotBlank(provider.getLastUpdated()) )
		{
			tests.add(compareDates("lastUpdated: [" + lastUpdated + "]", lastUpdated, provider.getLastUpdated(),
					new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, getApiInfo(provider)));
		}
		else
		{
			tests.add(dynamicTest("lastUpdated: [" + lastUpdated + "]", () -> assertEquals(lastUpdated, provider.getLastUpdated(), getApiInfo(provider))));
		}
		
		tests.add(dynamicTest("lastUpdatedBy: [" + lastUpdatedBy + "]", () -> assertEquals(lastUpdatedBy, provider.getLastUpdatedBy(), getApiInfo(provider))));
		tests.add(dynamicTest("ver: [" + ver + "]", () -> assertEquals(ver, provider.getVer(), getApiInfo(provider))));
		tests.add(dynamicTest("id [" + id + "]", () -> assertEquals(id, provider.getId(), getApiInfo(provider))));
		
		return tests;
	}
	
    /**
     * Compare UI objects for Provider Directory
     * 
     * @param provider {@link Provider} to be compared with this instance
     * @return list of {@link DynamicNode} of test
     */
    public List<DynamicNode> compareUI(Provider provider)
    {
        List<DynamicNode> tests = new ArrayList<>();

        tests.add(dynamicTest("npi: [" + npi + "]", () -> assertEquals(npi, provider.getNpi())));
        tests.add(dynamicTest("firstName: [" + firstName + "]", () -> assertEquals(firstName, provider.getFirstName().toUpperCase())));
        tests.add(dynamicTest("lastName: [" + lastName + "]", () -> assertEquals(lastName, provider.getLastName().toUpperCase())));
        tests.add(dynamicTest("taxonomyDescr: [" + taxonomyDescr + "]", () -> assertEquals(taxonomyDescr, provider.getTaxonomyDescr())));
        tests.add(dynamicTest("statusInd: [" + statusInd + "]", () -> assertEquals(statusInd, provider.getStatusInd())));
    //    tests.add(dynamicTest("officeLocations (count): [" + officeLocations.size() + "]", () -> assertEquals(officeLocations.size(), provider.getOfficeLocations().size(), getApiInfo(provider))));


        /*
         * Custom validation of office locations
         */
        OfficeLocation expectedDefaultLocation = officeLocations.stream()
                .filter(office -> office.isDefaultVal() != null && office.isDefaultVal())
                .findAny()
                .orElse(null);

        OfficeLocation actualDefaultLocation = provider.getOfficeLocations().stream()
                .filter(office -> office.isDefaultVal() != null && office.isDefaultVal())
                .findAny()
                .orElse(provider.getOfficeLocations().size() > 0 ? provider.getOfficeLocations().get(0) : null);

        if ( expectedDefaultLocation == null && actualDefaultLocation != null )
        {
            tests.add(dynamicTest("Provider from cosmos did not have default office location", 
                    () -> fail("No default office location found from Cosmos but UI has -- " + actualDefaultLocation)));
        }
        else if ( expectedDefaultLocation != null && actualDefaultLocation == null )
        {
            tests.add(dynamicTest("Provider from UI did not have default office location", 
                    () -> fail("No default office location found from UI but Cosmos has -- " + expectedDefaultLocation)));
        }
        else if ( expectedDefaultLocation == null && actualDefaultLocation == null )
        {
            tests.add(dynamicTest(String.format("Provider [npi %s] from both Cosmos and UI did not have default office location", provider.getId()), 
                    () -> fail("No default office location found from either UI or Cosmos")));
        }
        else
        {
            tests.add(dynamicContainer("Office Location [default]", expectedDefaultLocation.compareUI(actualDefaultLocation)) );  // compare the office location 

        }
        return tests; 
    }
    
    
    /**
     * Compare UI objects for Member Provider Page
     * @author ntagore 03/14/23
     * @param memberProvider {@link Provider} to be compared with this instance
     * @return list of {@link DynamicNode} of test
     */
    public List<DynamicNode> compareMemberProviderUI(Provider memberProvider)
    {
        List<DynamicNode> tests = new ArrayList<>();

        tests.add(dynamicTest("npi: [" + npi + "]", () -> assertEquals(npi, memberProvider.getNpi())));      
        tests.add(dynamicTest("Provider First LastName: [" + firstName + " " + lastName + "]", () -> assertEquals(firstName.toUpperCase()+" "+lastName.toUpperCase(), memberProvider.getFirstName().toUpperCase() +" "+memberProvider.getLastName().toUpperCase())));
        tests.add(dynamicTest("taxonomyDescr: [" + taxonomyDescr + "]", () -> assertEquals(taxonomyDescr, memberProvider.getTaxonomyDescr())));
        tests.add(dynamicTest("statusInd: [" + statusInd + "]", () -> assertEquals(statusInd, memberProvider.getStatusInd())));

        /*
         * Custom validation of office locations
         */
        OfficeLocation expectedDefaultLocation = officeLocations.stream()
                .filter(office -> office.isDefaultVal() != null && office.isDefaultVal())
                .findAny()
                .orElse(null);

        OfficeLocation actualDefaultLocation = memberProvider.getOfficeLocations().stream()
                .filter(office -> office.isDefaultVal() != null && office.isDefaultVal())
                .findAny()
                .orElse(memberProvider.getOfficeLocations().size() > 0 ? memberProvider.getOfficeLocations().get(0) : null);

        if ( expectedDefaultLocation == null && actualDefaultLocation != null )
        {
            tests.add(dynamicTest("Provider from cosmos did not have default office location", 
                    () -> fail("No default office location found from Cosmos but UI has -- " + actualDefaultLocation)));
        }
        else if ( expectedDefaultLocation != null && actualDefaultLocation == null )
        {
            tests.add(dynamicTest("Provider from UI did not have default office location", 
                    () -> fail("No default office location found from UI but Cosmos has -- " + expectedDefaultLocation)));
        }
        else if ( expectedDefaultLocation == null && actualDefaultLocation == null )
        {
            tests.add(dynamicTest(String.format("Provider [npi %s] from both Cosmos and UI did not have default office location", memberProvider.getId()), 
                    () -> fail("No default office location found from either UI or Cosmos")));
        }
        else
        {
            tests.add(dynamicContainer("Office Location [default]", expectedDefaultLocation.compareUI(actualDefaultLocation)) );  // compare the office location 

        }
        return tests; 
    }


    /*
     * Helper methods
     */

    @Override
    public boolean equals(Object obj)
    {
        if ( obj instanceof Provider)
        {
            Provider provider = (Provider) obj;

			return npi.equals(provider.getNpi());
        }

        return false;
    }



    /*
     * Setter / Getter
     */

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatusInd() {
        return statusInd;
    }

    public void setStatusInd(String statusInd) {
        this.statusInd = statusInd;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public String getTaxonomyCode() {
        return taxonomyCode;
    }

    public void setTaxonomyCode(String taxonomyCode) {
        this.taxonomyCode = taxonomyCode;
    }

    public String getTaxonomyDescr() {
        return taxonomyDescr;
    }

    public void setTaxonomyDescr(String taxonomyDescr) {
        this.taxonomyDescr = taxonomyDescr;
    }

    public List<OfficeLocation> getOfficeLocations() {
        return officeLocations;
    }

    public void setOfficeLocations(List<OfficeLocation> officeLocations) {
        this.officeLocations = officeLocations;
    }
    
    /**
     * @author ntagore 02/17/2023
     * This method is used to get the default office location
     * @return OfficeLocation
     */
	@JsonIgnore		// GC (04/05/23) re-added after it was removed
    public OfficeLocation getDefaultOfficeLocation()
    {
        if ( this.officeLocations != null && this.officeLocations.size() > 0 )
        {
            for ( OfficeLocation location : this.officeLocations )
            {
                if ( location.isDefaultVal() ) {
                    return location;
                }
            }
        }
        
        return null;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}



