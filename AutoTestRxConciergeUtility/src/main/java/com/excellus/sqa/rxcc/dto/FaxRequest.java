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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DynamicNode;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * DTO for Fax Request
 * 
 * @author Mahesh Chowdary (mmanchar)
 * @since 02/28/2022
 */
@JsonPropertyOrder({ "phoneNumber", "type", "npiCount", "faxNumber", "npi", "memberId", "interventionId", "faxJobId", "override",
	"pdfUri", "documentId", "createdBy", "createdDateTime", "lastUpdatedBy", "lastUpdatedDateTime", "version",
"id" })
public class FaxRequest extends CommonItem {

	@JsonProperty(required=true)
	private String phoneNumber;

	@JsonProperty(required=true)
	private Type type;  //const

	@JsonProperty(required=true)
	private String faxNumber;

	@JsonProperty(required=true)
	private String npi;

	@JsonProperty(required=true)
	private String memberId;

	@JsonProperty(required=true)
	private String interventionId;

	private String faxJobId;

	@JsonProperty(required=true)
	private boolean override;

	private int retryCount;

	@JsonProperty(required=true)
	private String pdfUri;

	@JsonProperty(required=true)
	private String documentId;

	private Map<String, Integer> npiCount;	// GC (11/30/22)


	/*
	 * Validations
	 * 
	 */


	/**
	 * Compare faxrequest properties defined in the schema
	 * 
	 * @param faxRequest  {@link FaxRequest} to compare with
	 * @return test results of comparison
	 */
	public List<DynamicNode> compare(FaxRequest faxRequest) 
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("phoneNumber: [" + phoneNumber + "]",
				() -> assertEquals(phoneNumber, faxRequest.getPhoneNumber(), getApiInfo(faxRequest))));
		tests.add(dynamicTest("type: [" + type + "]",
				() -> assertEquals(type, faxRequest.getType(), getApiInfo(faxRequest))));
		tests.add(dynamicTest("faxNumber: [" + faxNumber + "]",
				() -> assertEquals(faxNumber, faxRequest.getFaxNumber(), getApiInfo(faxRequest))));
		tests.add(dynamicTest("npi: [" + npi + "]",
				() -> assertEquals(npi, faxRequest.getNpi(), getApiInfo(faxRequest))));
		tests.add(dynamicTest("memberId: [" + memberId + "]",
				() -> assertEquals(memberId, faxRequest.getMemberId(), getApiInfo(faxRequest))));
		tests.add(dynamicTest("interventionId: [" + interventionId + "]",
				() -> assertEquals(interventionId, faxRequest.getInterventionId(), getApiInfo(faxRequest))));
		tests.add(dynamicTest("faxJobId: [" + faxJobId + "]",
				() -> assertEquals(faxJobId, faxRequest.getFaxJobId(), getApiInfo(faxRequest))));
		tests.add(dynamicTest("override: [" + override + "]",
				() -> assertEquals(override, faxRequest.isOverride(), getApiInfo(faxRequest))));
		tests.add(dynamicTest("retryCount: [" + retryCount + "]",
				() -> assertEquals(retryCount, faxRequest.getRetryCount(), getApiInfo(faxRequest))));
		tests.add(dynamicTest("pdfUri: [" + pdfUri + "]",
				() -> assertEquals(pdfUri, faxRequest.getPdfUri(), getApiInfo(faxRequest))));
		tests.add(dynamicTest("documentId: [" + documentId + "]",
				() -> assertEquals(documentId, faxRequest.getDocumentId(), getApiInfo(faxRequest))));

		tests.addAll(super.compare(faxRequest));

		return tests;
	}

	/*
	 * Helper methods
	 */

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FaxRequest) {
			FaxRequest faxRequest = (FaxRequest) obj;

			if (id.equals(faxRequest.getId()))
				return true;
		}

		return false;
	}

	/*
	 * Setter / Getter
	 */

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getNpi() {
		return npi;
	}

	public void setNpi(String npi) {
		this.npi = npi;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getInterventionId() {
		return interventionId;
	}

	public void setInterventionId(String interventionId) {
		this.interventionId = interventionId;
	}

	public String getFaxJobId() {
		return faxJobId;
	}

	public void setFaxJobId(String faxJobId) {
		this.faxJobId = faxJobId;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public String getPdfUri() {
		return pdfUri;
	}

	public void setPdfUri(String pdfUri) {
		this.pdfUri = pdfUri;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public Map<String, Integer> getNpiCount()
	{
		return npiCount;
	}

	public void setNpiCount(Map<String, Float> npiCount)
	{
		this.npiCount = new HashMap<String, Integer>();

		for ( Map.Entry<String, Float> entry : npiCount.entrySet() )
		{
			this.npiCount.put(entry.getKey(), entry.getValue().intValue());
		}
	}

	public enum Type {
		pdf,
		count;
	}

}
