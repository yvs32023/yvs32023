/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicNode;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 05/18/2022
 */
@JsonPropertyOrder({ "id",
	"type",
	"tenantId",
	"adTenantId",
	"groupId",
	"rxccGroupName","effectiveDate","termDate","memberCommunicationInd","pdfLogoUri","pdfLogoAddress"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantGetGroups extends Item
{

	@JsonProperty(required=true)
	private String formularyCode;

	@JsonProperty(required=true)
	private String type;

	@JsonProperty(required=true)
	private String adTenantId;

	@JsonProperty(required=true)
	private int tenantId;

	@JsonProperty(required=true)
	private String groupId;

	@JsonProperty(required=true)
	private String groupName;

	@JsonProperty(required=true)
	private String effDate;

	@JsonProperty(required=true)
	private String termDate;

	@JsonProperty(required=true)
	private boolean memberCommunicationInd;

	@JsonProperty(required=true)
	private String pdfLogoUri;

	private String pdfLogoAddress;

	
	/*
	 * Validations
	 */

	/**
	 * Compare two objects
	 * 
	 * @param tenantgetgroups
	 * @return
	 */
	public List<DynamicNode> compare(TenantGetGroups tenantgetgroups)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("formularyCode: [" + formularyCode + "]", () -> assertEquals(formularyCode, tenantgetgroups.getFormularyCode(), getApiInfo(tenantgetgroups))));
		tests.add(dynamicTest("type: [" + type + "]", () -> assertEquals(type, tenantgetgroups.getType(), getApiInfo(tenantgetgroups))));
		tests.add(dynamicTest("adTenantId: [" + adTenantId + "]", () -> assertEquals(adTenantId, tenantgetgroups.getAdTenantId(), getApiInfo(tenantgetgroups))));
		tests.add(dynamicTest("tenantId: [" + tenantId + "]", () -> assertEquals(tenantId, tenantgetgroups.getTenantId(), getApiInfo(tenantgetgroups))));
		tests.add(dynamicTest("groupId: [" + groupId + "]", () -> assertEquals(groupId, tenantgetgroups.getGroupId(), getApiInfo(tenantgetgroups))));
		tests.add(dynamicTest("rxccGroupName: [" + groupName + "]", () -> assertEquals(groupName, tenantgetgroups.getRxccGroupName(), getApiInfo(tenantgetgroups))));
		tests.add(dynamicTest("effectiveDate: [" + effDate + "]", () -> assertEquals(effDate, tenantgetgroups.getEffectiveDate(), getApiInfo(tenantgetgroups))));
		tests.add(dynamicTest("termDate: [" + termDate + "]", () -> assertEquals(termDate, tenantgetgroups.getTermDate(), getApiInfo(tenantgetgroups))));
		tests.add(dynamicTest("memberCommunicationInd: [" + memberCommunicationInd + "]", () -> assertEquals(memberCommunicationInd, tenantgetgroups.isMemberCommunicationInd(), getApiInfo(tenantgetgroups))));
		tests.add(dynamicTest("pdfLogoUri: [" + pdfLogoUri + "]", () -> assertEquals(pdfLogoUri, tenantgetgroups.getPdfLogoUri(), getApiInfo(tenantgetgroups))));
		tests.add(dynamicTest("pdfLogoAddress: [" + pdfLogoAddress + "]", () -> assertEquals(pdfLogoAddress, tenantgetgroups.getPdfLogoAddress(), getApiInfo(tenantgetgroups))));

		tests.addAll( super.compare(tenantgetgroups) );

		return tests;
	}


	/*
	 * Helper methods
	 */

	@Override
	public boolean equals(Object obj)
	{
					
		if ( obj instanceof TenantGetGroups)
		{
			TenantGetGroups tenantgetgroups = (TenantGetGroups) obj;

			if ( formularyCode.equals(tenantgetgroups.getFormularyCode()) && type.equals(tenantgetgroups.getType()) && adTenantId.equals(tenantgetgroups.getAdTenantId()) 
					&& tenantId == (tenantgetgroups.getTenantId()) && groupId.equals(tenantgetgroups.getGroupId()) && groupName.equals(tenantgetgroups.getRxccGroupName()) && effDate.equals(tenantgetgroups.getEffectiveDate())
							&& termDate.equals(tenantgetgroups.getTermDate()) && memberCommunicationInd == (tenantgetgroups.isMemberCommunicationInd()) )

				return true;
		}

		return false;
	}


	/*
	 * Setter / Getter
	 */
	
	public String getFormularyCode() {
		return formularyCode;
	}

	public void setFormularyCode(String formularyCode) {
		this.formularyCode = formularyCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getRxccGroupName() {
		return groupName;
	}

	public void setRxccGroupName(String rxccGroupName) {
		this.groupName = rxccGroupName;
	}

	public String getEffectiveDate() {
		return effDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effDate = effectiveDate;
	}

	public String getTermDate() {
		return termDate;
	}

	public void setTermDate(String termDate) {
		this.termDate = termDate;
	}

	public boolean isMemberCommunicationInd() {
		return memberCommunicationInd;
	}

	public void setMemberCommunicationInd(boolean memberCommunicationInd) {
		this.memberCommunicationInd = memberCommunicationInd;
	}

	public String getPdfLogoUri() {
		return pdfLogoUri;
	}

	public void setPdfLogoUri(String pdfLogoUri) {
		this.pdfLogoUri = pdfLogoUri;
	}

	public String getPdfLogoAddress() {
		return pdfLogoAddress;
	}

	public void setPdfLogoAddress(String pdfLogoAddress) {
		this.pdfLogoAddress = pdfLogoAddress;
	}


}
