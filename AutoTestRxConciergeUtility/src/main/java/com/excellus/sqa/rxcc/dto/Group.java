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

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;

import com.excellus.sqa.rxcc.Utility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Base on the group schema https://dev.azure.com/ExcellusBCBS/EHP/_git/rxcc-schemas?path=/schemas/formulary.group.schema.json
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 09/07/2022
 */
@JsonPropertyOrder({ "formularyId","type","adTenantId","tenantId","groupId","rxccGroupName","effectiveDate","termDate","memberCommunicationInd",
	"pdfLogoUri","pdfLogoAddress","pbmGroupName","pbmGroupSize","benefitId","benefitDescription","fundingArrangementId","fundingArrangementDescription"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Group extends CommonItem
{
	@JsonProperty(required=true)
	private String  formularyId;

	@JsonProperty(required=true)
	private String  type;

	@JsonProperty(required=true)
	private String  adTenantId;

	@JsonProperty(required=true)
	private String  tenantId;

	@JsonProperty(required=true)
	private String  groupId;

	@JsonProperty(required=true)
	private String  rxccGroupName;

	@JsonProperty(required=true)
	private String  effectiveDate;

	@JsonProperty(required=true)
	private String  termDate;

	@JsonProperty(required=true)
	private boolean  memberCommunicationInd;

	private String  pdfLogoUri;

	private PdfLogoAddress  pdfLogoAddress;

	private String  pbmGroupName;

	private String  pbmGroupSize;

	private String  benefitId;

	private String  benefitDescription;

	private String  fundingArrangementId;

	private String  fundingArrangementDescription;
	
    @JsonProperty(required=true)
    private String tenantName;  // ntagore 03/06/23

	/*
	 * Validations
	 */

   
	public List<DynamicNode> compare(Group group) 
	{

		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("formularyId: [" + formularyId + "]", () -> assertEquals(formularyId, group.getFormularyId(), getApiInfo(group))));
		tests.add(dynamicTest("type: [" + type + "]", () -> assertEquals(type, group.getType(), getApiInfo(group))));
		tests.add(dynamicTest("adTenantId: [" + adTenantId + "]", () -> assertEquals(adTenantId, group.getAdTenantId(), getApiInfo(group))));
		tests.add(dynamicTest("tenantId: [" + tenantId + "]", () -> assertEquals(tenantId, group.getTenantId(), getApiInfo(group))));
		tests.add(dynamicTest("groupId: [" + groupId + "]", () -> assertEquals(groupId, group.getGroupId(), getApiInfo(group))));
		tests.add(dynamicTest("rxccGroupName: [" + rxccGroupName + "]", () -> assertEquals(rxccGroupName, group.getRxccGroupName(), getApiInfo(group))));
		tests.add(dynamicTest("effectiveDate: [" + effectiveDate + "]", () -> assertEquals(effectiveDate, group.getEffectiveDate(), getApiInfo(group))));
		tests.add(dynamicTest("termDate: [" + termDate + "]", () -> assertEquals(termDate, group.getTermDate(), getApiInfo(group))));
		tests.add(dynamicTest("memberCommunicationInd: [" + memberCommunicationInd + "]", () -> assertEquals(memberCommunicationInd, group.isMemberCommunicationInd(), getApiInfo(group))));
		tests.add(dynamicTest("pdfLogoUri: [" + pdfLogoUri + "]", () -> assertEquals(pdfLogoUri, group.getPdfLogoUri(), getApiInfo(group))));
		
		//performs object null validation
		if (group.getPdfLogoAddress() != null)
		{
			tests.add(dynamicContainer("PdfLogoAddress", pdfLogoAddress.compare(group.getPdfLogoAddress())));
		}
		else
		{
			tests.add(dynamicTest("pdfLogoAddress: [" + pdfLogoAddress + "]", () -> assertEquals(pdfLogoAddress, group.getPdfLogoAddress(), getApiInfo(group))));	
		}
		
		
		tests.add(dynamicTest("pbmGroupName: [" + pbmGroupName + "]", () -> assertEquals(pbmGroupName, group.getPbmGroupName(), getApiInfo(group))));
		tests.add(dynamicTest("pbmGroupSize: [" + pbmGroupSize + "]", () -> assertEquals(pbmGroupSize, group.getPbmGroupSize(), getApiInfo(group))));
		tests.add(dynamicTest("benefitId: [" + benefitId + "]", () -> assertEquals(benefitId, group.getBenefitId(), getApiInfo(group))));
		tests.add(dynamicTest("benefitDescription: [" + benefitDescription + "]", () -> assertEquals(benefitDescription, group.getBenefitDescription(), getApiInfo(group))));
		tests.add(dynamicTest("fundingArrangementId: [" + fundingArrangementId + "]", () -> assertEquals(fundingArrangementId, group.getFundingArrangementId(), getApiInfo(group))));
		tests.add(dynamicTest("fundingArrangementDescription: [" + fundingArrangementDescription + "]", () -> assertEquals(fundingArrangementDescription, group.getFundingArrangementDescription(), getApiInfo(group))));


		tests.addAll( super.compare(group) );

		return tests;	

	}

	/*
     * UI Validations
     * @author ntagore
     * @since 09/27/2022
     */

    public List<DynamicNode> compareUI(Group group) 
    {

        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        tests.add(dynamicTest("Formulary Code: [" + formularyId + "]", () -> assertEquals(formularyId, group.getFormularyId())));
        tests.add(dynamicTest("Created User: [" + createdBy + "]", () -> assertEquals(createdBy, group.getCreatedBy())));
        tests.add(dynamicTest("Create Date: [" + createdDateTime + "]", 
                () -> assertEquals(Utility.convertCosmosDateToUI(createdDateTime, "MM/dd/yyyy"), group.getCreatedDateTime())));
        tests.add(dynamicTest("Tenant ID: [" + tenantId + "]", () -> assertEquals(tenantId, group.getTenantId())));
        tests.add(dynamicTest("Group Id: [" + groupId + "]", () -> assertEquals(groupId, group.getGroupId())));
        tests.add(dynamicTest("Rxcc Group Name: [" + rxccGroupName + "]", () -> assertEquals(rxccGroupName, group.getRxccGroupName())));
        
        //Perform custom validation of Effective Date
        if ( StringUtils.isNotBlank(effectiveDate) && StringUtils.isNotBlank(group.getEffectiveDate()) )
            
        {

          String dateYear = effectiveDate.substring(0, 4);
          String dateMonth = effectiveDate.substring(4, 6);
          String dateDay = effectiveDate.substring(6, 8);
          
          String expectedEffectiveDate = dateMonth+"/"+dateDay+"/"+dateYear; 
          
          tests.add(dynamicTest("Effective Date : [" + effectiveDate + "]",
                  () -> assertEquals(expectedEffectiveDate, group.getEffectiveDate())));
        }
        else
        {
            tests.add(dynamicTest("Effective Date: [" + effectiveDate + "]", 
                    () -> assertEquals(effectiveDate, group.getEffectiveDate())));
        } 
        
        //Perform custom validation of termination date        
        if ( StringUtils.isNotBlank(termDate) && StringUtils.isNotBlank(group.getTermDate()) )
            
        {

          String dateYear = termDate.substring(0, 4);
          String dateMonth = termDate.substring(4, 6);
          String dateDay = termDate.substring(6, 8);
          
          String expectedTermDate = dateMonth+"/"+dateDay+"/"+dateYear;     
          
          tests.add(dynamicTest("Termination Date : [" + termDate + "]",
                  () -> assertEquals(expectedTermDate, group.getTermDate())));
        }
        else
        {
            tests.add(dynamicTest("Effective Date: [" + effectiveDate + "]", 
                    () -> assertEquals(termDate, group.getTermDate())));
        } 
        
        tests.add(dynamicTest("Last Modified user: [" + lastUpdatedBy + "]", () -> assertEquals(lastUpdatedBy, group.getLastUpdatedBy())));
        tests.add(dynamicTest("Last Updated: [" + lastUpdatedDateTime + "]", 
                () -> assertEquals(Utility.convertCosmosDateToUI(lastUpdatedDateTime, "MM/dd/yyyy"), group.getLastUpdatedDateTime())));  
        
        
        //new added
        tests.add(dynamicTest("memberCommunicationInd: [" + memberCommunicationInd + "]", () -> assertEquals(memberCommunicationInd, group.isMemberCommunicationInd())));
        
       return tests;   

    }

	/*
	 * Helper methods
	 */

	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof Group)
		{
			Group group = (Group) obj;

			if ( id.equals(group.getId()) )
				return true;
		}

		return false;
	}

	/*
	 * Setter / Getter
	 */

	public String getFormularyId() {
		return formularyId;
	}

	public void setFormularyId(String formularyId) {
		this.formularyId = formularyId;
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

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getRxccGroupName() {
		return rxccGroupName;
	}

	public void setRxccGroupName(String rxccGroupName) {
		this.rxccGroupName = rxccGroupName;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
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

	public PdfLogoAddress getPdfLogoAddress() {
		return pdfLogoAddress;
	}

	public void setPdfLogoAddress(PdfLogoAddress pdfLogoAddress) {
		this.pdfLogoAddress = pdfLogoAddress;
	}

	public String getPbmGroupName() {
		return pbmGroupName;
	}

	public void setPbmGroupName(String pbmGroupName) {
		this.pbmGroupName = pbmGroupName;
	}

	public String getPbmGroupSize() {
		return pbmGroupSize;
	}

	public void setPbmGroupSize(String pbmGroupSize) {
		this.pbmGroupSize = pbmGroupSize;
	}

	public String getBenefitId() {
		return benefitId;
	}

	public void setBenefitId(String benefitId) {
		this.benefitId = benefitId;
	}

	public String getBenefitDescription() {
		return benefitDescription;
	}

	public void setBenefitDescription(String benefitDescription) {
		this.benefitDescription = benefitDescription;
	}

	public String getFundingArrangementId() {
		return fundingArrangementId;
	}

	public void setFundingArrangementId(String fundingArrangementId) {
		this.fundingArrangementId = fundingArrangementId;
	}

	public String getFundingArrangementDescription() {
		return fundingArrangementDescription;
	}

	public void setFundingArrangementDescription(String fundingArrangementDescription) {
		this.fundingArrangementDescription = fundingArrangementDescription;
	}
	
	// ntagore 03/06/23
    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    
    public enum Type
    {
        EHP     (1, "ehp",  "Excellus Health Plan"),
        EXE     (2, "exe",  "Excellus Employee"),
        LOA     (3, "loa",  "LBS Out Of Area"),
        MED     (4, "med",  "Excellus BCBS Medicare");

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

        public static final Type valueOfEnum(String valueOf)
        {
            if (valueOf.equalsIgnoreCase("1"))
            {
                return EHP;
            }
            
            else if (valueOf.equalsIgnoreCase("2"))
            {
                return EXE;
            }

            else if (valueOf.equalsIgnoreCase("3"))
            {
                return LOA;
            }

            else if (valueOf.equalsIgnoreCase("4"))
            {
                return MED;
            }

            else
            {
                return null;
            }
        }
        
        @Override
        public String toString() {
            return this.name;
        }
    }

}

