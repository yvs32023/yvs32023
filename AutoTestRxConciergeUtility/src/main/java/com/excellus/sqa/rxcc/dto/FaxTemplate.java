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
 * Base on the fax template schema https://dev.azure.com/ExcellusBCBS/EHP/_git/rxcc-schemas?path=/schemas/faxtemplate.faxtemplate.schema.json
 * 
 * @author Manish Sharma(msharma)
 * @since 09/26/2022
 */
@JsonPropertyOrder({ "faxTemplateId","type","formName","pdfFormName","status","providerHeader","memberHeader","ecrBlock1","ecrBlock2",
	"ecrBlock3","acceptCheckBox","acceptText","declineCheckBox","declineText","ecrClosure","ecrFooter"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FaxTemplate extends CommonItem
{
	
	@JsonProperty(required=true)
	private String  faxTemplateId;
	
	private Type  type; //const
	
	@JsonProperty(required=true)
	private String  formName;
	
	private String  pdfFormName;
	
	@JsonProperty(required=true)
	private Status  status; //enum
	
	private String  providerHeader;
	
	private String  memberHeader;
	
	private String  ecrBlock1;
	
	private String  ecrBlock2;
	
	private String  ecrBlock3;
	
	private boolean  acceptCheckbox; //need to refactor(property name) later on schema issue 
	
	@JsonProperty(required=true)
	private String  acceptText;
	
	private boolean  declineCheckbox; //need to refactor(property name) later on schema issue 
	
	@JsonProperty(required=true)
	private String  declineText;
	
	private String  ecrClosure;
	
	private String  ecrFooter;
	
	/*
	 * Validations
	 */

	public List<DynamicNode> compare(FaxTemplate faxTemplate) 
	{

		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("faxTemplateId: [" + faxTemplateId + "]", () -> assertEquals(faxTemplateId, faxTemplate.getFaxTemplateId(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("type: [" + type + "]", () -> assertEquals(type, faxTemplate.getType(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("formName: [" + formName + "]", () -> assertEquals(formName, faxTemplate.getFormName(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("pdfFormName: [" + pdfFormName + "]", () -> assertEquals(pdfFormName, faxTemplate.getPdfFormName(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("status: [" + status + "]", () -> assertEquals(status, faxTemplate.getStatus(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("providerHeader: [" + providerHeader + "]", () -> assertEquals(providerHeader, faxTemplate.getProviderHeader(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("memberHeader: [" + memberHeader + "]", () -> assertEquals(memberHeader, faxTemplate.getMemberHeader(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("ecrBlock1: [" + ecrBlock1 + "]", () -> assertEquals(ecrBlock1, faxTemplate.getEcrBlock1(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("ecrBlock2: [" + ecrBlock2 + "]", () -> assertEquals(ecrBlock2, faxTemplate.getEcrBlock2(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("ecrBlock3: [" + ecrBlock3 + "]", () -> assertEquals(ecrBlock3, faxTemplate.getEcrBlock3(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("acceptCheckbox: [" + acceptCheckbox + "]", () -> assertEquals(acceptCheckbox, faxTemplate.isAcceptCheckbox(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("acceptText: [" + acceptText + "]", () -> assertEquals(acceptText, faxTemplate.getAcceptText(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("declineCheckbox: [" + declineCheckbox + "]", () -> assertEquals(declineCheckbox, faxTemplate.isDeclineCheckbox(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("declineText: [" + declineText + "]", () -> assertEquals(declineText, faxTemplate.getDeclineText(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("ecrClosure: [" + ecrClosure + "]", () -> assertEquals(ecrClosure, faxTemplate.getEcrClosure(), getApiInfo(faxTemplate))));
		tests.add(dynamicTest("ecrFooter: [" + ecrFooter + "]", () -> assertEquals(ecrFooter, faxTemplate.getEcrFooter(), getApiInfo(faxTemplate))));
	
		tests.addAll( super.compare(faxTemplate) );

		return tests;	

	}
	
	/*
	 * Helper methods
	 */
	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof FaxTemplate)
		{
			FaxTemplate faxTemplate = (FaxTemplate) obj;

			if ( id.equals(faxTemplate.getId()) )
				return true;
		}

		return false;
	}
	
	
	/*
	 * Setter / Getter
	 */
	public String getFaxTemplateId() {
		return faxTemplateId;
	}

	public void setFaxTemplateId(String faxTemplateId) {
		this.faxTemplateId = faxTemplateId;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getPdfFormName() {
		return pdfFormName;
	}

	public void setPdfFormName(String pdfFormName) {
		this.pdfFormName = pdfFormName;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getProviderHeader() {
		return providerHeader;
	}

	public void setProviderHeader(String providerHeader) {
		this.providerHeader = providerHeader;
	}

	public String getMemberHeader() {
		return memberHeader;
	}

	public void setMemberHeader(String memberHeader) {
		this.memberHeader = memberHeader;
	}

	public String getEcrBlock1() {
		return ecrBlock1;
	}

	public void setEcrBlock1(String ecrBlock1) {
		this.ecrBlock1 = ecrBlock1;
	}

	public String getEcrBlock2() {
		return ecrBlock2;
	}

	public void setEcrBlock2(String ecrBlock2) {
		this.ecrBlock2 = ecrBlock2;
	}

	public String getEcrBlock3() {
		return ecrBlock3;
	}

	public void setEcrBlock3(String ecrBlock3) {
		this.ecrBlock3 = ecrBlock3;
	}

	public boolean isAcceptCheckbox() {
		return acceptCheckbox;
	}

	public void setAcceptCheckbox(boolean acceptCheckbox) {
		this.acceptCheckbox = acceptCheckbox;
	}

	public String getAcceptText() {
		return acceptText;
	}

	public void setAcceptText(String acceptText) {
		this.acceptText = acceptText;
	}

	public boolean isDeclineCheckbox() {
		return declineCheckbox;
	}

	public void setDeclineCheckbox(boolean declineCheckbox) {
		this.declineCheckbox = declineCheckbox;
	}

	public String getDeclineText() {
		return declineText;
	}

	public void setDeclineText(String declineText) {
		this.declineText = declineText;
	}

	public String getEcrClosure() {
		return ecrClosure;
	}

	public void setEcrClosure(String ecrClosure) {
		this.ecrClosure = ecrClosure;
	}

	public String getEcrFooter() {
		return ecrFooter;
	}

	public void setEcrFooter(String ecrFooter) {
		this.ecrFooter = ecrFooter;
	}

	/*
	 * Enum / Constant
	 */
	public enum Type 
	{
		faxtemplate;
	}
	
	public enum Status 
	{
		Active,
		Inactive;
	}

}
