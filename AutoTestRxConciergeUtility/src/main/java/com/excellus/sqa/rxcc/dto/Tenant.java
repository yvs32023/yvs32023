package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicNode;

/**
 * DTO for LBS Tenant items/documents
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 02/05/2022
 */
public class Tenant extends CommonItem {
	
	private int tenantId;
	private String type;
	private String tenantName;
	private String tenantDescription;
	private String subscriptionName;
	private String adTenantId;
	private String city;
	private String state;
	private String phoneNumber;
	private String faxNumber;
	private String imageUrl;
	private boolean status;
	private boolean legacyInd;
	private String  pdfLogoUri;
	private PdfLogoAddress  pdfLogoAddress;
		
	/*
	 * Validations
	 */

	/**
	 * Compare two objects
	 * 
	 * @param tenant
	 * @return
	 */
	public List<DynamicNode> compare(Tenant tenant)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		tests.add(dynamicTest("tenantId: [" + tenantId + "]", () -> assertEquals(tenantId, tenant.getTenantId(), getApiInfo(tenant))));
		tests.add(dynamicTest("tenantName: [" + tenantName + "]", () -> assertEquals(tenantName, tenant.getTenantName(), getApiInfo(tenant))));
		tests.add(dynamicTest("tenantDescription: [" + tenantDescription + "]", () -> assertEquals(tenantDescription, tenant.getTenantDescription(), getApiInfo(tenant))));
		tests.add(dynamicTest("subscriptionName: [" + subscriptionName + "]", () -> assertEquals(subscriptionName, tenant.getSubscriptionName(), getApiInfo(tenant))));
		tests.add(dynamicTest("adTenantId: [" + adTenantId + "]", () -> assertEquals(adTenantId, tenant.getAdTenantId(), getApiInfo(tenant))));
		tests.add(dynamicTest("city: [" + city + "]", () -> assertEquals(city, tenant.getCity(), getApiInfo(tenant))));
		tests.add(dynamicTest("state: [" + state + "]", () -> assertEquals(state, tenant.getState(), getApiInfo(tenant))));
		tests.add(dynamicTest("phoneNumber: [" + phoneNumber + "]", () -> assertEquals(phoneNumber, tenant.getPhoneNumber(), getApiInfo(tenant))));
		tests.add(dynamicTest("faxNumber: [" + faxNumber + "]", () -> assertEquals(faxNumber, tenant.getFaxNumber(), getApiInfo(tenant))));
		tests.add(dynamicTest("imageUrl: [" + imageUrl + "]", () -> assertEquals(imageUrl, tenant.getImageUrl(), getApiInfo(tenant))));
		tests.add(dynamicTest("status: [" + status + "]", () -> assertEquals(status, tenant.isStatus(), getApiInfo(tenant))));
		tests.add(dynamicTest("pdfLogoUri: [" + pdfLogoUri + "]", () -> assertEquals(pdfLogoUri, tenant.getPdfLogoUri(), getApiInfo(tenant))));
		tests.add(dynamicTest("pdfLogoAddress: [" + pdfLogoAddress + "]", () -> assertEquals(pdfLogoAddress, tenant.getPdfLogoAddress(), getApiInfo(tenant))));
		
		tests.addAll( super.compare(tenant) );
		
		return tests;
	}
	

	/*
	 * Helper methods
	 */

	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof Tenant)
		{
			Tenant tenant = (Tenant) obj;

			if ( adTenantId.equals(tenant.getAdTenantId()) )
				return true;
		}
		
		return false;
	}


	/*
	 * Setter / Getter
	 */

	public int getTenantId() {
		return tenantId;
	}

	public void setTenantId(int tenantId) {
		this.tenantId = tenantId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getTenantDescription() {
		return tenantDescription;
	}

	public void setTenantDescription(String tenantDescription) {
		this.tenantDescription = tenantDescription;
	}

	public String getSubscriptionName() {
		return subscriptionName;
	}

	public void setSubscriptionName(String subscriptionName) {
		this.subscriptionName = subscriptionName;
	}

	public String getAdTenantId() {
		return adTenantId;
	}

	public void setAdTenantId(String adTenantId) {
		this.adTenantId = adTenantId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
	
	public boolean isLegacyInd() {
		return legacyInd;
	}

	public void setLegacyInd(boolean legacyInd) {
		this.legacyInd = legacyInd;
	}
	
	public String getPdfLogoUri() {
		return pdfLogoUri;
	}

	public void setPdfLogoUri(String pdfLogoUri) {
		this.pdfLogoUri = pdfLogoUri;
	}

	public PdfLogoAddress getPdfLogoAddress() {
		return pdfLogoAddress;
	}

	public void setPdfLogoAddress(PdfLogoAddress pdfLogoAddress) {
		this.pdfLogoAddress = pdfLogoAddress;
	}

	/**
	 * Defines the known tenants
	 * 
	 * @author gcosmian
	 * @since 03/28/22
	 */
	public enum Type
	{
		EHP		(1, "ehp",	"Excellus Health Plan"),
		EXE		(2,	"exe",	"Excellus Employee"),
		LOA		(2,	"loa",	"LBS Out Of Area"),
		MED		(2,	"med",	"Excellus BCBS Medicare");
		
		private final int tenantId;
		private final String subscriptionName;
		private final String name;
		
		private Type(int tenantId, String subscriptionName, String name)
		{
			this.tenantId = tenantId;
			this.subscriptionName = subscriptionName;
			this.name = name;
		}
		
		public int getTenantId() {
			return this.tenantId;
		}
		
		public String getSubscriptionName() {
			return this.subscriptionName;
		}
		
		public String getName() {
			return this.name;
		}
		
		@Override
		public String toString() {
			return this.tenantId + " - " + this.subscriptionName + " - " + this.name;
		}
	}
	
}
