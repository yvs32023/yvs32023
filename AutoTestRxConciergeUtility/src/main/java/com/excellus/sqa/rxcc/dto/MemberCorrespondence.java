/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;

import com.excellus.sqa.rxcc.Utility;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * DTO for Member Correspondence
 * 
 * @author Manish Sharma (msharma)
 * @since 03/01/2022
 */
@JsonPropertyOrder({"memberId","type","correspondenceId","correspondenceType","correspondenceDateTime","correspondenceOutcome"
    ,"interventionId","contactName","contactTitle","contactComment","targetDrug","note"
    ,"pdfUri","storagePath","npi","providerName"})
@JsonInclude(JsonInclude.Include.NON_NULL)  
public class MemberCorrespondence extends CommonItem 
{
    @JsonProperty(required=true)
    private String memberId;

    @JsonProperty(required=true)
    private MemberType type; //enum

    @JsonProperty(required=true)
    private String correspondenceId;

    @JsonProperty(required=true)
    private CorrespondenceType correspondenceType; //enum

    @JsonProperty(required=true)
    private String correspondenceDateTime;

    @JsonProperty(required=true)
    private String correspondenceOutcome;

    private String interventionId;

    private String interventionNoteId;  //Added 11/10/2022 due to regression failure for Unrecognized field

    @JsonProperty(required=true)
    private String contactName;

    private String contactTitle;

    private String contactComment;

    private String targetDrug;

    private String note;

    private String pdfUri;

    private StoragePath storagePath;

    private String npi;

    private String providerName;

    private String documentId;

    /*
     * Validations
     */

    /**
     * Compare two objects
     * 
     * @param MemberCorrespondence
     * @return
     */
    public List<DynamicNode> compare(MemberCorrespondence memberCorrespondence)
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        tests.add(dynamicTest("memberId: [" + memberId + "]", () -> assertEquals(memberId, memberCorrespondence.getMemberId(), getApiInfo(memberCorrespondence))));
        tests.add(dynamicTest("type: [" + type + "]", () -> assertEquals(type, memberCorrespondence.getType(), getApiInfo(memberCorrespondence))));
        tests.add(dynamicTest("correspondenceId: [" + correspondenceId + "]", () -> assertEquals(correspondenceId, memberCorrespondence.getCorrespondenceId(), getApiInfo(memberCorrespondence))));
        tests.add(dynamicTest("correspondenceType: [" + correspondenceType + "]", () -> assertEquals(correspondenceType, memberCorrespondence.getCorrespondenceType(), getApiInfo(memberCorrespondence))));

        // Perform correspondenceDateTime date validation			
        if ( StringUtils.isNotBlank(correspondenceDateTime) && StringUtils.isNotBlank(memberCorrespondence.getCorrespondenceDateTime()) )
        {
            tests.add(compareDates("correspondenceDateTime: [" + correspondenceDateTime + "]", correspondenceDateTime, memberCorrespondence.getCorrespondenceDateTime(),
                    new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, getApiInfo(memberCorrespondence)));
        }
        else
        {
            tests.add(dynamicTest("correspondenceDateTime: [" + correspondenceDateTime + "]", () -> assertEquals(correspondenceDateTime, memberCorrespondence.getCorrespondenceDateTime(), getApiInfo(memberCorrespondence))));
        }

        tests.add(dynamicTest("interventionId: [" + interventionId + "]", () -> assertEquals(interventionId, memberCorrespondence.getInterventionId(), getApiInfo(memberCorrespondence))));
        tests.add(dynamicTest("interventionNoteId: [" + interventionNoteId + "]", () -> assertEquals(interventionNoteId, memberCorrespondence.getInterventionNoteId(), getApiInfo(memberCorrespondence))));
        tests.add(dynamicTest("contactName: [" + contactName + "]", () -> assertEquals(contactName, memberCorrespondence.getContactName(), getApiInfo(memberCorrespondence))));
        tests.add(dynamicTest("contactTitle: [" + contactTitle + "]", () -> assertEquals(contactTitle, memberCorrespondence.getContactTitle(), getApiInfo(memberCorrespondence))));
        tests.add(dynamicTest("contactComment: [" + contactComment + "]", () -> assertEquals(contactComment, memberCorrespondence.getContactComment(), getApiInfo(memberCorrespondence))));
        tests.add(dynamicTest("correspondenceOutcome: [" + correspondenceOutcome + "]", () -> assertEquals(correspondenceOutcome, memberCorrespondence.getCorrespondenceOutcome(), getApiInfo(memberCorrespondence))));
        tests.add(dynamicTest("targetDrug: [" + targetDrug + "]", () -> assertEquals(targetDrug, memberCorrespondence.getTargetDrug(), getApiInfo(memberCorrespondence))));		
        tests.add(dynamicTest("note: [" + note + "]", () -> assertEquals(note, memberCorrespondence.getNote(), getApiInfo(memberCorrespondence))));
        tests.add(dynamicTest("pdfUri: [" + pdfUri + "]", () -> assertEquals(pdfUri, memberCorrespondence.getPdfUri(), getApiInfo(memberCorrespondence))));

        //performs object null validation
        if (memberCorrespondence.getStoragePath() != null)
        {
            tests.add(dynamicContainer("StoragePath", storagePath.compare(memberCorrespondence.getStoragePath())));
        }
        else
        {
            tests.add(dynamicTest("storagePath: [" + storagePath + "]", () -> assertEquals(storagePath, memberCorrespondence.getStoragePath(), getApiInfo(memberCorrespondence))));	
        }

        tests.add(dynamicTest("npi: [" + npi + "]", () -> assertEquals(npi, memberCorrespondence.getNpi(), getApiInfo(memberCorrespondence))));
        tests.add(dynamicTest("providerName: [" + providerName + "]", () -> assertEquals(providerName, memberCorrespondence.getProviderName(), getApiInfo(memberCorrespondence))));
        tests.add(dynamicTest("documentId: [" + documentId + "]", () -> assertEquals(documentId, memberCorrespondence.getDocumentId(), getApiInfo(memberCorrespondence))));
        tests.add(dynamicTest("createdBy [" + createdBy + "]", () -> assertEquals(createdBy, memberCorrespondence.getCreatedBy(), getApiInfo(memberCorrespondence))));

        // Perform date validation			
        if ( StringUtils.isNotBlank(createdDateTime) && StringUtils.isNotBlank(memberCorrespondence.getCreatedDateTime()) )
        {
            tests.add(compareDates("createdDateTime: [" + createdDateTime + "]", createdDateTime, memberCorrespondence.getCreatedDateTime(),
                    new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, getApiInfo(memberCorrespondence)));
        }
        else
        {
            tests.add(dynamicTest("createdDateTime: [" + createdDateTime + "]", () -> assertEquals(createdDateTime, memberCorrespondence.getCreatedDateTime(), getApiInfo(memberCorrespondence))));
        }

        tests.add(dynamicTest("lastUpdatedBy [" + lastUpdatedBy + "]", () -> assertEquals(lastUpdatedBy, memberCorrespondence.getLastUpdatedBy(), getApiInfo(memberCorrespondence))));

        // Perform date validation	
        if ( StringUtils.isNotBlank(lastUpdatedDateTime) && StringUtils.isNotBlank(memberCorrespondence.getLastUpdatedDateTime()) )
        {
            tests.add(compareDates("lastUpdatedDateTime: [" + lastUpdatedDateTime + "]", lastUpdatedDateTime, memberCorrespondence.getLastUpdatedDateTime(),
                    new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, getApiInfo(memberCorrespondence)));
        }
        else
        {
            tests.add(dynamicTest("lastUpdatedDateTime [" + lastUpdatedDateTime + "]", () -> assertEquals(lastUpdatedDateTime, memberCorrespondence.getLastUpdatedDateTime(), getApiInfo(memberCorrespondence))));
        }

        tests.add(dynamicTest("version [" + version + "]", () -> assertEquals(version, memberCorrespondence.getVersion(), getApiInfo(memberCorrespondence))));

        // Validate id value if provided
        if ( StringUtils.isNotBlank(id) )
        {
            tests.add(dynamicTest("id [" + id + "]", () -> assertEquals(id, memberCorrespondence.getId(), getApiInfo(memberCorrespondence))));
        }
        // validate the actual member id is not null
        else
        {
            tests.add(dynamicTest("id (not null)", () -> assertNotNull(memberCorrespondence.getId(), "id must not null: " + getApiInfo(memberCorrespondence))));
        }

        tests.addAll( super.compare(memberCorrespondence) );

        return tests;
    }

    /**
     * Compare UI two objects
     * 
     * @param MemberCorrespondence
     * @return
     * @author neerutagore
     */
    public List<DynamicNode> compareCorrespondenceUI(MemberCorrespondence memberCorrespondence)
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        tests.add(dynamicTest("correspondenceType: [" + correspondenceType + "]", () -> assertEquals(correspondenceType, memberCorrespondence.getCorrespondenceType())));
        //tests.add(dynamicTest("interventionId: [" + interventionId + "]", () -> assertEquals(interventionId, memberCorrespondence.getInterventionId())));

        // cosmos data inconsistencey with space trimming and making the upper cases
        tests.add(dynamicTest("contactName: [" + contactName + "]", () -> assertEquals(contactName.trim().toUpperCase(), memberCorrespondence.getContactName().toUpperCase())));  
        tests.add(dynamicTest("contactTitle: [" + contactTitle + "]", () -> assertEquals(contactTitle, memberCorrespondence.getContactTitle())));

        tests.add(dynamicTest("correspondenceOutcome: [" + correspondenceOutcome + "]", () -> assertEquals(correspondenceOutcome, memberCorrespondence.getCorrespondenceOutcome())));

        tests.add(dynamicTest("targetDrug: [" + targetDrug + "]", () -> assertEquals(targetDrug, memberCorrespondence.getTargetDrug())));       
        tests.add(dynamicTest("createdBy: [" + createdBy + "]", () -> assertEquals(createdBy, memberCorrespondence.getCreatedBy())));

        tests.add(dynamicTest("createdDateTime: [" + createdDateTime + "]", 
                () -> assertEquals(Utility.convertCosmosDateToUI(createdDateTime,  "MM/dd/yyyy"),memberCorrespondence.getCreatedDateTime())));              

        tests.add(dynamicTest("lastUpdatedDateTime: [" + lastUpdatedDateTime + "]", 
                () -> assertEquals(Utility.convertCosmosDateToUI(lastUpdatedDateTime,  "MM/dd/yyyy"),memberCorrespondence.getLastUpdatedDateTime())));

        tests.add(dynamicTest("providerName: [" + providerName + "]", () -> assertEquals(providerName.replace("  ", " "), memberCorrespondence.getProviderName())));

        return tests;
    }

    /**
     * Compares legacy correspondence as displayed in the UI
     * @param memberCorrespondence
     * @return list of {@link DynamicNode}
     * @author Garrett Cosmiano (gcosmian)
     * @since 09/06/22
     */
    public List<DynamicNode> compareLegacyCorrespondenceUI(MemberCorrespondence memberCorrespondence)
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        // Perform correspondenceDateTime date validation
        if ( StringUtils.isNotBlank(correspondenceDateTime) && StringUtils.isNotBlank(memberCorrespondence.getCorrespondenceDateTime()) )
        {
            String expectedDate = Utility.convertCosmosDateToUI(correspondenceDateTime, "MM/dd/yyyy HH:mm");
            tests.add(dynamicTest("Call Date Time: [" + correspondenceDateTime + "]", 
                    () -> assertEquals(expectedDate, memberCorrespondence.getCorrespondenceDateTime()) ));
        }
        else
        {
            tests.add(dynamicTest("Call Date Time: [" + correspondenceDateTime + "]", 
                    () -> assertEquals(correspondenceDateTime, memberCorrespondence.getCorrespondenceDateTime())));
        }

        tests.add(dynamicTest("Call Type: [" + correspondenceType + "]", () -> assertEquals(correspondenceType, memberCorrespondence.getCorrespondenceType())));

        tests.add(dynamicTest("Call Outcome: [" + correspondenceOutcome + "]", () -> assertEquals(correspondenceOutcome, memberCorrespondence.getCorrespondenceOutcome())));

        tests.add(dynamicTest("Point of Contact: [" + contactName + "]", () -> assertEquals(contactName.trim().toUpperCase(), memberCorrespondence.getContactName().toUpperCase())));  

        tests.add(dynamicTest("Created By: [" + createdBy + "]", () -> assertEquals(createdBy, memberCorrespondence.getCreatedBy())));

        tests.add(dynamicTest("Contact Comment: [" + contactComment + "]", 
                () -> assertEquals(contactComment.replaceAll("\\s+", " "), memberCorrespondence.getContactComment().replaceAll("\\s+", " ") )));

        return tests;
    }

    /**
     * Compare UI two objects
     * 
     * @param MemberCorrespondence
     * @return
     * @author neerutagore 01/18/2023
     * This need to be kept separate because date format is different for Intervention Correspondence on Command Center 
     */
    public List<DynamicNode> compareInterventionCorrespondenceUI(MemberCorrespondence memberCorrespondence)
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        tests.add(dynamicTest("correspondenceType: [" + correspondenceType + "]", () -> assertEquals(correspondenceType, memberCorrespondence.getCorrespondenceType())));
        tests.add(dynamicTest("correspondenceOutcome: [" + correspondenceOutcome + "]", () -> assertEquals(correspondenceOutcome, memberCorrespondence.getCorrespondenceOutcome())));
        tests.add(dynamicTest("contactName: [" + contactName + "]", () -> assertEquals(contactName, memberCorrespondence.getContactName())));  
        tests.add(dynamicTest("contactTitle: [" + contactTitle + "]", () -> assertEquals(contactTitle, memberCorrespondence.getContactTitle())));
        tests.add(dynamicTest("createdBy: [" + createdBy + "]", () -> assertEquals(createdBy, memberCorrespondence.getCreatedBy())));

        tests.add(dynamicTest("modifiedBy: [" + lastUpdatedBy + "]", () -> assertEquals(lastUpdatedBy, memberCorrespondence.getLastUpdatedBy()))); // added to fix regression failure 02/23/23
        // Perform InterventionCorrespondenceDateTime validation

        if ( StringUtils.isNotBlank(createdDateTime) && StringUtils.isNotBlank(memberCorrespondence.getCreatedDateTime()) )
        {
            String expectedDate = Utility.convertCosmosDateToUI(createdDateTime, "MM/dd/yyyy");
            tests.add(dynamicTest("Created Date: [" + createdDateTime + "]", 
                    () -> assertEquals(expectedDate, memberCorrespondence.getCreatedDateTime()) ));
        }
        else
        {
            tests.add(dynamicTest("Created Date: [" + createdDateTime + "]", 
                    () -> assertEquals(createdDateTime, memberCorrespondence.getCreatedDateTime())));
        }

        // Perform Intervention Correspondence updated datetime validation
        if ( StringUtils.isNotBlank(lastUpdatedDateTime) && StringUtils.isNotBlank(memberCorrespondence.getLastUpdatedDateTime()) )
        {
            String expectedDate = Utility.convertCosmosDateToUI(lastUpdatedDateTime, "MM/dd/yyyy");
            tests.add(dynamicTest("Modified Date: [" + lastUpdatedDateTime + "]", 
                    () -> assertEquals(expectedDate, memberCorrespondence.getLastUpdatedDateTime()) ));
        }
        else
        {
            tests.add(dynamicTest("Modified Date: [" + lastUpdatedDateTime + "]", 
                    () -> assertEquals(lastUpdatedDateTime, memberCorrespondence.getLastUpdatedDateTime())));
        }
        tests.add(dynamicTest("providerName: [" + providerName + "]", () -> assertEquals(providerName.replace("  ", " ").toLowerCase(), memberCorrespondence.getProviderName().toLowerCase())));

        return tests;
    }
    /**
     * Compare UI two objects
     * 
     * @param MemberCorrespondence
     * @return
     * @author neerutagore 01/19/2023
     * This needs to be kept separate because of data inconsistent from regular correspondence tab testing
     */
    public List<DynamicNode> compareCorrespondenceTypeUI(MemberCorrespondence memberCorrespondence)
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        tests.add(dynamicTest("correspondenceType: [" + correspondenceType + "]", () -> assertEquals(correspondenceType, memberCorrespondence.getCorrespondenceType())));
        tests.add(dynamicTest("correspondenceOutcome: [" + correspondenceOutcome + "]", () -> assertEquals(correspondenceOutcome, memberCorrespondence.getCorrespondenceOutcome())));
        tests.add(dynamicTest("contactName: [" + contactName + "]", () -> assertEquals(contactName, memberCorrespondence.getContactName())));  
        tests.add(dynamicTest("contactTitle: [" + contactTitle + "]", () -> assertEquals(contactTitle, memberCorrespondence.getContactTitle())));
        tests.add(dynamicTest("targetDrug: [" + targetDrug + "]", () -> assertEquals(targetDrug, memberCorrespondence.getTargetDrug())));       
        tests.add(dynamicTest("createdBy: [" + createdBy + "]", () -> assertEquals(createdBy, memberCorrespondence.getCreatedBy())));
        tests.add(dynamicTest("createdDateTime: [" + createdDateTime + "]", 
                () -> assertEquals(Utility.convertCosmosDateToUI(createdDateTime,  "MM/dd/yyyy"),memberCorrespondence.getCreatedDateTime())));              

        tests.add(dynamicTest("lastUpdatedDateTime: [" + lastUpdatedDateTime + "]", 
                () -> assertEquals(Utility.convertCosmosDateToUI(lastUpdatedDateTime,  "MM/dd/yyyy"),memberCorrespondence.getLastUpdatedDateTime())));

        tests.add(dynamicTest("providerName: [" + providerName + "]", () -> assertEquals(providerName.replace("  ", " "), memberCorrespondence.getProviderName())));

        return tests;
    }


    /*
     * Helper methods
     */

    /**
     * Equals is defined if the following are met
     * - correspondence id are the same
     *
     * @return true if it mets the criteria
     */
    @Override
    public boolean equals(Object obj)
    {
        if ( obj instanceof MemberCorrespondence )
        {
            MemberCorrespondence memberCorrespondence = (MemberCorrespondence) obj;

            if ( id.equals(memberCorrespondence.getId()) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method evaluate if the provided correspondence matches these fields
     * - type
     * - outcome
     * - contact name
     * - created by
     * 
     * This is meant to be used for UI validation
     * 
     * @param theCorrespondence {@link MemberCorrespondence}   
     * @return true if the fields matches otherwise false
     */
    public boolean equalsByNonId(MemberCorrespondence theCorrespondence)
    {
        if (  (correspondenceType == theCorrespondence.getCorrespondenceType()) &&
                StringUtils.equalsIgnoreCase(correspondenceOutcome, theCorrespondence.getCorrespondenceOutcome()) &&
                StringUtils.equalsIgnoreCase(contactName, theCorrespondence.getContactName()) &&
                StringUtils.equalsIgnoreCase(createdBy, theCorrespondence.getCreatedBy())) {
            return true;
        }

        return false;
    }


    /*
     * Setter / Getter
     */

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public MemberType getType() {
        return type;
    }

    public void setType(MemberType type) {
        this.type = type;
    }	

    public String getCorrespondenceId() {
        return correspondenceId;
    }

    public void setCorrespondenceId(String correspondenceId) {
        this.correspondenceId = correspondenceId;
    }

    public CorrespondenceType getCorrespondenceType() {
        return correspondenceType;
    }

    public void setCorrespondenceType(CorrespondenceType correspondenceType) {
        this.correspondenceType = correspondenceType;
    }

    public void setCorrespondenceType(String correspondenceType) 
    {
        this.correspondenceType = CorrespondenceType.valueOfEnum(correspondenceType);
    }

    public String getCorrespondenceDateTime() {
        return correspondenceDateTime;
    }

    public void setCorrespondenceDateTime(String correspondenceDateTime) {
        this.correspondenceDateTime = correspondenceDateTime;
    }

    public String getCorrespondenceOutcome() {
        return correspondenceOutcome;
    }

    public void setCorrespondenceOutcome(String correspondenceOutcome) {
        this.correspondenceOutcome = correspondenceOutcome;
    }

    public String getInterventionId() {
        return interventionId;
    }

    public void setInterventionId(String interventionId) {
        this.interventionId = interventionId;
    }

    public String getInterventionNoteId() {
        return interventionNoteId;
    }

    public void setInterventionNoteId(String interventionNoteId) {
        this.interventionNoteId = interventionNoteId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactTitle() {
        return contactTitle;
    }

    public void setContactTitle(String contactTitle) {
        this.contactTitle = contactTitle;
    }

    public String getContactComment() {
        return contactComment;
    }

    public void setContactComment(String contactComment) {
        this.contactComment = contactComment;
    }

    public String getTargetDrug() {
        return targetDrug;
    }

    public void setTargetDrug(String targetDrug) {
        this.targetDrug = targetDrug;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPdfUri() {
        return pdfUri;
    }

    public void setPdfUri(String pdfUri) {
        this.pdfUri = pdfUri;
    }

    public StoragePath getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(StoragePath storagePath) {
        this.storagePath = storagePath;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId; 

    }

    public enum MemberType {
        correspondence,
        legacycorrespondence;
    }


    public enum CorrespondenceType {

        INBOUND_CALL         	(0, "Inbound Call"),
        OUTBOUND_CALL       	(1, "Outbound Call"),
        INBOUND_FAX_PROVIDER    (2, "Inbound Fax Provider"),
        OUTBOUND_FAX_PROVIDER   (3, "Outbound Fax Provider");

        private int correspondenceTypeIndex;
        private String item;

        private CorrespondenceType(int correspondenceType, String item)
        {
            this.correspondenceTypeIndex = correspondenceType; 
            this.item = item;


        }

        public int getCorrespondenceTypeIndex()
        {
            return this.correspondenceTypeIndex;
        }

        public  String getEnumValue()
        {
            return item;
        }



        public static final CorrespondenceType valueOfEnum(String valueOf)
        {
            if (valueOf.equalsIgnoreCase("Inbound Call"))
            {
                return INBOUND_CALL;
            }

            else if (valueOf.equalsIgnoreCase("Outbound Call"))
            {
                return OUTBOUND_CALL;
            }

            else if (valueOf.equalsIgnoreCase("Inbound Fax Provider"))
            {
                return INBOUND_FAX_PROVIDER;
            }

            else if (valueOf.equalsIgnoreCase("Outbound Fax Provider"))
            {
                return OUTBOUND_FAX_PROVIDER;
            }

            else
            {
                return null;
            }
        }


        @Override
        public String toString() {
            return item;
        }
    }

}
