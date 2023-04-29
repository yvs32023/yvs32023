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

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DynamicNode;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Base on the legacyintervention schema https://dev.azure.com/ExcellusBCBS/EHP/_git/rxcc-schemas?path=/schemas/member.legacyintervention.schema.json
 * 
 * @author Manish Sharma (msharma)
 * @since 09/09/2022
 */
@JsonPropertyOrder({"memberId","ruleName","interventionType","type","legacyStatusCode","targetProductName"
    ,"assignedTo","interventionStatusNote","recommendationDate","conversionDate","outcome", "createdBy", "lastUpdatedBy"})
@JsonInclude(JsonInclude.Include.NON_NULL)

public class MemberLegacyIntervention extends CommonItem
{

    @JsonProperty(required=true)
    private String memberId;
    
    @JsonProperty(required=true)
    private String ruleName;
    
    @JsonProperty(required=true)
    private String interventionType;
    
    @JsonProperty(required=true)
    private Type type;   //const
    
    @JsonProperty(required=true)
    private LegacyStatusCode legacyStatusCode; //enum
    
    @JsonProperty(required=true)
    private String targetProductName;
    
    @JsonProperty(required=true)
    private String assignedTo;
    
    @JsonProperty(required=true)
    private String interventionStatusNote;
    
    @JsonProperty(required=true)
    private String recommendationDate;
    
    @JsonProperty(required=true)
    private String conversionDate;
    
    @JsonProperty(required=true)
    private String outcome;

    
    /*
     * Validations
     */

    /**
     * Compare two objects
     * 
     * @param MemberLegacyIntervention
     * @return
     * @author ntagore
     */
    public List<DynamicNode> compare(MemberLegacyIntervention memberLegacyIntervention)
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
        
        tests.add(dynamicTest("memberId: [" + memberId + "]", () -> assertEquals(memberId, memberLegacyIntervention.getMemberId(), getApiInfo(memberLegacyIntervention))));
        tests.add(dynamicTest("ruleName: [" + ruleName + "]", () -> assertEquals(ruleName, memberLegacyIntervention.getRuleName(), getApiInfo(memberLegacyIntervention))));
        tests.add(dynamicTest("interventionType: [" + interventionType + "]", () -> assertEquals(interventionType, memberLegacyIntervention.getInterventionType(), getApiInfo(memberLegacyIntervention))));
        tests.add(dynamicTest("type: [" + type + "]", () -> assertEquals(type, memberLegacyIntervention.getType(), getApiInfo(memberLegacyIntervention))));
        tests.add(dynamicTest("legacyStatusCode: [" + legacyStatusCode + "]", () -> assertEquals(legacyStatusCode, memberLegacyIntervention.getLegacyStatusCode(), getApiInfo(memberLegacyIntervention))));
        tests.add(dynamicTest("targetProductName: [" + targetProductName + "]", () -> assertEquals(targetProductName, memberLegacyIntervention.getTargetProductName(), getApiInfo(memberLegacyIntervention))));
        tests.add(dynamicTest("assignedTo: [" + assignedTo + "]", () -> assertEquals(assignedTo, memberLegacyIntervention.getAssignedTo(), getApiInfo(memberLegacyIntervention))));
        tests.add(dynamicTest("interventionStatusNote: [" + interventionStatusNote + "]", () -> assertEquals(interventionStatusNote, memberLegacyIntervention.getInterventionStatusNote(), getApiInfo(memberLegacyIntervention))));
        
        // Perform recommendationDate date validation           
        if ( StringUtils.isNotBlank(recommendationDate) && StringUtils.isNotBlank(memberLegacyIntervention.getRecommendationDate()) )
        {
            tests.add(compareDates("recommendationDate: [" + recommendationDate + "]", recommendationDate, memberLegacyIntervention.getRecommendationDate(),
                    new String[]{ "yyyy-MM-dd'T'HH:mm:ss","yyyy-MM-dd" }, getApiInfo(memberLegacyIntervention)));
        }
        else
        {
            tests.add(dynamicTest("recommendationDate: [" + recommendationDate + "]", () -> assertEquals(recommendationDate, memberLegacyIntervention.getRecommendationDate(), getApiInfo(memberLegacyIntervention))));
        }
        
        // Perform conversionDate date validation           
        if ( StringUtils.isNotBlank(conversionDate) && StringUtils.isNotBlank(memberLegacyIntervention.getRecommendationDate()) )
        {
            tests.add(compareDates("conversionDate: [" + conversionDate + "]", conversionDate, memberLegacyIntervention.getConversionDate(),
                    new String[]{ "yyyy-MM-dd'T'HH:mm:ss","yyyy-MM-dd"}, getApiInfo(memberLegacyIntervention)));
        }
        else
        {
            tests.add(dynamicTest("conversionDate: [" + conversionDate + "]", () -> assertEquals(conversionDate, memberLegacyIntervention.getConversionDate(), getApiInfo(memberLegacyIntervention))));
        }
    
        tests.add(dynamicTest("outcome: [" + outcome + "]", () -> assertEquals(outcome, memberLegacyIntervention.getOutcome(), getApiInfo(memberLegacyIntervention))));
        
        tests.addAll(super.compare(memberLegacyIntervention));

        return tests;
        
    } 
    
    /**
     * Compare two UI objects
     * 
     * @param MemberLegacyIntervention
     * @return
     */
    public List<DynamicNode> compareLegacyInterventionUI(MemberLegacyIntervention memberLegacyIntervention)
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();
               
        tests.add(dynamicTest("Drug Focus: [" + targetProductName + "]", () -> assertEquals(targetProductName, memberLegacyIntervention.getTargetProductName())));
        tests.add(dynamicTest("Outcome Documented By: [" + lastUpdatedBy + "]", () -> assertEquals(lastUpdatedBy, memberLegacyIntervention.getLastUpdatedBy())));
        tests.add(dynamicTest("Intervention Made By: [" + createdBy + "]", () -> assertEquals(createdBy, memberLegacyIntervention.getCreatedBy())));
        tests.add(dynamicTest("Intervention Name: [" + ruleName + "]", () -> assertEquals(ruleName, memberLegacyIntervention.getRuleName())));
        tests.add(dynamicTest("Reply: [" + assignedTo + "]", () -> assertEquals(assignedTo, memberLegacyIntervention.getAssignedTo())));
        tests.add(dynamicTest("(Outcome Notes): [" + outcome + "]", () -> assertEquals(outcome, memberLegacyIntervention.getOutcome())));   
        
        //    Perform recommendationDate date validation    
               
      if ( StringUtils.isNotBlank(recommendationDate) && StringUtils.isNotBlank(memberLegacyIntervention.getRecommendationDate()) )
          
        {
          String dateYear = recommendationDate.substring(0, 4);
          String dateMonth = recommendationDate.substring(5, 7);
          String dateDay = recommendationDate.substring(8, 10);
          
          String expectedDate = dateMonth+"/"+dateDay+"/"+dateYear;        
          
           tests.add(dynamicTest("Intervention Date: [" + recommendationDate + "]",
                   () -> assertEquals(expectedDate, memberLegacyIntervention.getRecommendationDate()) ));
        }
        else
        {
            tests.add(dynamicTest("Intervention Date: [" + recommendationDate + "]", 
                    () -> assertEquals(recommendationDate, memberLegacyIntervention.getRecommendationDate())));
        }
        
        // Perform conversionDate date validation           
        if ( StringUtils.isNotBlank(conversionDate) && StringUtils.isNotBlank(memberLegacyIntervention.getRecommendationDate()) )
        {
            
            String dateYear = conversionDate.substring(0, 4);
            String dateMonth = conversionDate.substring(5, 7);
            String dateDay = conversionDate.substring(8, 10);
            
            String expectedDate = dateMonth+"/"+dateDay+"/"+dateYear;
            
            tests.add(dynamicTest("Conversion Date: [" + conversionDate + "]",
                    () -> assertEquals(expectedDate, memberLegacyIntervention.getConversionDate()) ));
        }
        else
        {
            tests.add(dynamicTest("Conversion Date: [" + conversionDate + "]", () -> assertEquals(conversionDate, memberLegacyIntervention.getConversionDate())));
        }         
       
        return tests;
        
    }
    
    /*
     * Helper methods
     */

    /**
     * Equals is defined if the following are met
     * - legacyintervention id are the same
     *
     * @return true if it mets the criteria
     */
    @Override
    public boolean equals(Object obj)
    {
        if ( obj instanceof MemberLegacyIntervention )
        {
            MemberLegacyIntervention memberLegacyIntervention = (MemberLegacyIntervention) obj;

            if ( id.equals(memberLegacyIntervention.getId()) ) {
                return true;
            }
        }

        return false;
    }
    
    public boolean equalsUI(Object obj)
    {
        if ( obj instanceof MemberLegacyIntervention )
        {
            MemberLegacyIntervention memberLegacyIntervention = (MemberLegacyIntervention) obj;

            if (  //StringUtils.equals(this.targetProductName, memberLegacyIntervention.getTargetProductName()) &&
                    StringUtils.equals(this.ruleName, memberLegacyIntervention.getRuleName()) &&
                    StringUtils.equals(this.assignedTo, memberLegacyIntervention.getAssignedTo()) )
                 //   StringUtils.equals(this.createdBy, memberLegacyIntervention.getCreatedBy())) 
            {
                return true;
            }
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

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getInterventionType() {
        return interventionType;
    }

    public void setInterventionType(String interventionType) {
        this.interventionType = interventionType;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public LegacyStatusCode getLegacyStatusCode() {
        return legacyStatusCode;
    }

    public void setLegacyStatusCode(LegacyStatusCode legacyStatusCode) {
        this.legacyStatusCode = legacyStatusCode;
    }

    public String getTargetProductName() {
        return targetProductName;
    }

    public void setTargetProductName(String targetProductName) {
        this.targetProductName = targetProductName;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getInterventionStatusNote() {
        return interventionStatusNote;
    }

    public void setInterventionStatusNote(String interventionStatusNote) {
        this.interventionStatusNote = interventionStatusNote;
    }

    public String getRecommendationDate() {
        return recommendationDate;
    }

    public void setRecommendationDate(String recommendationDate) {
        this.recommendationDate = recommendationDate;
    }

    public String getConversionDate() {
        return conversionDate;
    }

    public void setConversionDate(String conversionDate) {
        this.conversionDate = conversionDate;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    
    
    public enum Type 
    {
        legacyintervention;
    }
    
    public enum LegacyStatusCode 
    {
        Pending,
        Completed;
    }

}
