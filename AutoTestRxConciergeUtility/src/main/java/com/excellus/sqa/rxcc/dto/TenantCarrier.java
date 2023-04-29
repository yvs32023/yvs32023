/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicNode;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 09/20/2022
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantCarrier extends CommonItem
{
	@JsonProperty(required=true)
	private String adTenantId;

	@JsonProperty(required=true)
	private int tenantId;

	@JsonProperty(required=true)
	private String type;

	@JsonProperty(required=true)
	private String carrierId;

	@JsonProperty(required=true)
	private String carrierIdDescription;

	@JsonProperty(required=true)
	private String benefitHierarchyId;

	@JsonProperty(required=true)
	private String benefitHierarchyIdDescription;

	@JsonProperty(required=true)
	private String  pdfLogoUri;

	private PdfLogoAddress  pdfLogoAddress;

	/*
	 * Validations
	 */
	/**
	 * Compare two objects
	 * 
	 * @param tenantCarrier
	 * @return
	 */
	public List<DynamicNode> compare(TenantCarrier tenantCarrier)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		tests.add(dynamicTest("adTenantId: [" + adTenantId + "]", () -> assertEquals(adTenantId, tenantCarrier.getAdTenantId(), getApiInfo(tenantCarrier))));
		tests.add(dynamicTest("tenantId: [" + tenantId + "]", () -> assertEquals(tenantId, tenantCarrier.getTenantId(), getApiInfo(tenantCarrier))));
		tests.add(dynamicTest("type: [" + type + "]", () -> assertEquals(type, tenantCarrier.getType(), getApiInfo(tenantCarrier))));
		tests.add(dynamicTest("carrierId: [" + carrierId + "]", () -> assertEquals(carrierId, tenantCarrier.getCarrierId(), getApiInfo(tenantCarrier))));
		tests.add(dynamicTest("carrierIdDescription: [" + carrierIdDescription + "]", () -> assertEquals(carrierIdDescription, tenantCarrier.getCarrierIdDescription(), getApiInfo(tenantCarrier))));
		tests.add(dynamicTest("benefitHierarchyId: [" + benefitHierarchyId + "]", () -> assertEquals(benefitHierarchyId, tenantCarrier.getBenefitHierarchyId(), getApiInfo(tenantCarrier))));
		tests.add(dynamicTest("benefitHierarchyIdDescription: [" + benefitHierarchyIdDescription + "]", () -> assertEquals(benefitHierarchyIdDescription, tenantCarrier.getBenefitHierarchyIdDescription(), getApiInfo(tenantCarrier))));
		tests.add(dynamicTest("pdfLogoUri: [" + pdfLogoUri + "]", () -> assertEquals(pdfLogoUri, tenantCarrier.getPdfLogoUri(), getApiInfo(tenantCarrier))));
		
		//performs object null validation
		if (tenantCarrier.getPdfLogoAddress() != null)
		{
			tests.add(dynamicContainer("PdfLogoAddress", pdfLogoAddress.compare(tenantCarrier.getPdfLogoAddress())));
		}
		else
		{
			tests.add(dynamicTest("pdfLogoAddress: [" + pdfLogoAddress + "]", () -> assertEquals(pdfLogoAddress, tenantCarrier.getPdfLogoAddress(), getApiInfo(tenantCarrier))));	
		}
		
		
		tests.addAll( super.compare(tenantCarrier) );
		
		return tests;
	}

	/*
	 * Helper methods
	 */

	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof TenantCarrier)
		{
			TenantCarrier tenantCarrier = (TenantCarrier) obj;

			if ( benefitHierarchyId.equals(tenantCarrier.getBenefitHierarchyId()) )
				return true;
		}

		return false;
	}

	/*
	 * Setter / Getter
	 */
	public String getAdTenantId() {
		return adTenantId;
	}


	public void setAdTenantId(String adTenantId) {
		this.adTenantId = adTenantId;
	}


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


	public String getCarrierId() {
		return carrierId;
	}


	public void setCarrierId(String carrierId) {
		this.carrierId = carrierId;
	}


	public String getCarrierIdDescription() {
		return carrierIdDescription;
	}


	public void setCarrierIdDescription(String carrierIdDescription) {
		this.carrierIdDescription = carrierIdDescription;
	}


	public String getBenefitHierarchyId() {
		return benefitHierarchyId;
	}


	public void setBenefitHierarchyId(String benefitHierarchyId) {
		this.benefitHierarchyId = benefitHierarchyId;
	}


	public String getBenefitHierarchyIdDescription() {
		return benefitHierarchyIdDescription;
	}


	public void setBenefitHierarchyIdDescription(String benefitHierarchyIdDescription) {
		this.benefitHierarchyIdDescription = benefitHierarchyIdDescription;
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

}
