/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicNode;
import org.junit.platform.commons.util.StringUtils;

import com.excellus.sqa.rxcc.Utility;
import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DTO for Member Rxclaim
 * 
 * @author Manish Sharma (msharma)
 * @since 03/01/2022
 */
public class MemberRxclaim extends Item {

    private String memberId;
    private String type;
    private String upi;
    private String groupId;
    private String groupName;
    private String subscriberId;
    private String dependentCode;
    private String transactionId;
    private String transactionIdOrg;
    private String claimStatusCode;
    private String claimStatusDescr;
    private String dateService;
    private String adjudicationDate;
    private String rxBenefitId;
    private String rxBenefitDescr;  //4/7/2022 : MS modified from rxBenefitIdDescr to rxBenefitId
    private String serviceProviderId;
    private String serviceProviderName;
    private String prescriberId;
    private String prescriberFirstName;
    private String prescriberLastName;
    private String rxNumber;
    private String ndc;
    private String gpi;
    private String drugName;
    private String dosageForm;
    private int quantityDispensed;
    private int quantityPrescribed;
    private int quantityIntendedDispense;
    private String productStrength;
    private String strengthUOM;
    private int strengthMetric;
    private int daySupply;
    private String mailOrderInd;
    private String formularyStatus;
    private String formularyStatusDescr;
    private String tierInd;
    private String genericInd;
    private String drugType;
    private String drugTypeDescr;
    private String dateRxWritten;
    private String daw;
    private String dawDescr;
    private double grossAmountDue;
    private double netAmountDue;
    private double amountCopay;
    private double amountPeriodicDeductible;
    private double amountCoInsur;
    private double dispensingFeePaid;
    private double amountSalesTax;
    private double ingredientCostPaid;
    private double memberAmountDue;  //MS (12/21/2022) added as schema  has been updated to add this new property
    private double totalAmountPaid;
    private String therapeuticClassCode;
    private int fillNumber;
    private int numberRefillAuth;
    private String reportGroup1;
    private String reportGroup2;
    private String reportGroup3;
    private int batchNumber; 	//MS (04/07/2022) added

    private String tenantName;
    private int tenantId;		//MS (04/07/2022) added


    private String dateCreated;  //MS 04/07/2022 - Only in Exe

    /*
     * Validations
     */

    /**
     * Compare two objects
     * 
     * @param MemberRxclaim
     * @return
     */

    public List<DynamicNode> compare(MemberRxclaim memberRxclaim)
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        tests.add(dynamicTest("memberId: [" + memberId + "]", () -> assertEquals(memberId, memberRxclaim.getMemberId(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("type: [" + type + "]", () -> assertEquals(type, memberRxclaim.getType(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("upi: [" + upi + "]", () -> assertEquals(upi, memberRxclaim.getUpi(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("groupId: [" + groupId + "]", () -> assertEquals(groupId, memberRxclaim.getGroupId(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("groupName: [" + groupName + "]", () -> assertEquals(groupName, memberRxclaim.getGroupName(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("subscriberId: [" + subscriberId + "]", () -> assertEquals(subscriberId, memberRxclaim.getSubscriberId(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("dependentCode: [" + dependentCode + "]", () -> assertEquals(dependentCode, memberRxclaim.getDependentCode(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("transactionId: [" + transactionId + "]", () -> assertEquals(transactionId, memberRxclaim.getTransactionId(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("transactionIdOrg: [" + transactionIdOrg + "]", () -> assertEquals(transactionIdOrg, memberRxclaim.getTransactionIdOrg(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("claimStatusCode: [" + claimStatusCode + "]", () -> assertEquals(claimStatusCode, memberRxclaim.getClaimStatusCode(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("claimStatusDescr: [" + claimStatusDescr + "]", () -> assertEquals(claimStatusDescr, memberRxclaim.getClaimStatusDescr(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("dateService: [" + dateService + "]", () -> assertEquals(dateService, memberRxclaim.getDateService(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("adjudicationDate: [" + adjudicationDate + "]", () -> assertEquals(adjudicationDate, memberRxclaim.getAdjudicationDate(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("rxBenefitId: [" + rxBenefitId + "]", () -> assertEquals(rxBenefitId, memberRxclaim.getRxBenefitId(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("rxBenefitDescr: [" + rxBenefitDescr + "]", () -> assertEquals(rxBenefitDescr, memberRxclaim.getRxBenefitDescr(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("serviceProviderId: [" + serviceProviderId + "]", () -> assertEquals(serviceProviderId, memberRxclaim.getServiceProviderId(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("serviceProviderName: [" + serviceProviderName + "]", () -> assertEquals(serviceProviderName, memberRxclaim.getServiceProviderName(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("prescriberId: [" + prescriberId + "]", () -> assertEquals(prescriberId, memberRxclaim.getPrescriberId(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("prescriberFirstName: [" + prescriberFirstName + "]", () -> assertEquals(prescriberFirstName, memberRxclaim.getPrescriberFirstName(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("prescriberLastName: [" + prescriberLastName + "]", () -> assertEquals(prescriberLastName, memberRxclaim.getPrescriberLastName(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("rxNumber: [" + rxNumber + "]", () -> assertEquals(rxNumber, memberRxclaim.getRxNumber(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("ndc: [" + ndc + "]", () -> assertEquals(ndc, memberRxclaim.getNdc(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("gpi: [" + gpi + "]", () -> assertEquals(gpi, memberRxclaim.getGpi(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("drugName: [" + drugName + "]", () -> assertEquals(drugName, memberRxclaim.getDrugName(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("dosageForm: [" + dosageForm + "]", () -> assertEquals(dosageForm, memberRxclaim.getDosageForm(), "Confirmed Bug #17134:" + getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("quantityDispensed: [" + quantityDispensed + "]", () -> assertEquals(quantityDispensed, memberRxclaim.getQuantityDispensed(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("quantityPrescribed: [" + quantityPrescribed + "]", () -> assertEquals(quantityPrescribed, memberRxclaim.getQuantityPrescribed(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("quantityIntendedDispense: [" + quantityIntendedDispense + "]", () -> assertEquals(quantityIntendedDispense, memberRxclaim.getQuantityIntendedDispense(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("productStrength: [" + productStrength + "]", () -> assertEquals(productStrength, memberRxclaim.getProductStrength(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("strengthUOM: [" + strengthUOM + "]", () -> assertEquals(strengthUOM, memberRxclaim.getStrengthUOM(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("strengthMetric: [" + strengthMetric + "]", () -> assertEquals(strengthMetric, memberRxclaim.getStrengthMetric(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("daySupply: [" + daySupply + "]", () -> assertEquals(daySupply, memberRxclaim.getDaySupply(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("mailOrderInd: [" + mailOrderInd + "]", () -> assertEquals(mailOrderInd, memberRxclaim.getMailOrderInd(), "Confirmed Bug #23278: " + getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("formularyStatus: [" + formularyStatus + "]", () -> assertEquals(formularyStatus, memberRxclaim.getFormularyStatus(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("formularyStatusDescr: [" + formularyStatusDescr + "]", () -> assertEquals(formularyStatusDescr, memberRxclaim.getFormularyStatusDescr(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("tierInd: [" + tierInd + "]", () -> assertEquals(tierInd, memberRxclaim.getTierInd(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("genericInd: [" + genericInd + "]", () -> assertEquals(genericInd, memberRxclaim.getGenericInd(), "Confirmed Bug #23278: " + getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("drugType: [" + drugType + "]", () -> assertEquals(drugType, memberRxclaim.getDrugType(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("drugTypeDescr: [" + drugTypeDescr + "]", () -> assertEquals(drugTypeDescr, memberRxclaim.getDrugTypeDescr(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("dateRxWritten: [" + dateRxWritten + "]", () -> assertEquals(dateRxWritten, memberRxclaim.getDateRxWritten(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("daw: [" + daw + "]", () -> assertEquals(daw, memberRxclaim.getDaw(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("dawDescr: [" + dawDescr + "]", () -> assertEquals(dawDescr, memberRxclaim.getDawDescr(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("grossAmountDue: [" + grossAmountDue + "]", () -> assertEquals(grossAmountDue, memberRxclaim.getGrossAmountDue(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("netAmountDue: [" + netAmountDue + "]", () -> assertEquals(netAmountDue, memberRxclaim.getNetAmountDue(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("amountCopay: [" + amountCopay + "]", () -> assertEquals(amountCopay, memberRxclaim.getAmountCopay(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("amountPeriodicDeductible: [" + amountPeriodicDeductible + "]", () -> assertEquals(amountPeriodicDeductible, memberRxclaim.getAmountPeriodicDeductible(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("amountCoInsur: [" + amountCoInsur + "]", () -> assertEquals(amountCoInsur, memberRxclaim.getAmountCoInsur(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("dispensingFeePaid: [" + dispensingFeePaid + "]", () -> assertEquals(dispensingFeePaid, memberRxclaim.getDispensingFeePaid(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("amountSalesTax: [" + amountSalesTax + "]", () -> assertEquals(amountSalesTax, memberRxclaim.getAmountSalesTax(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("ingredientCostPaid: [" + ingredientCostPaid + "]", () -> assertEquals(ingredientCostPaid, memberRxclaim.getIngredientCostPaid(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("memberAmountDue: [" + memberAmountDue + "]", () -> assertEquals(memberAmountDue, memberRxclaim.getMemberAmountDue(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("totalAmountPaid: [" + totalAmountPaid + "]", () -> assertEquals(totalAmountPaid, memberRxclaim.getTotalAmountPaid(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("therapeuticClassCode: [" + therapeuticClassCode + "]", () -> assertEquals(therapeuticClassCode, memberRxclaim.getTherapeuticClassCode(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("fillNumber: [" + fillNumber + "]", () -> assertEquals(fillNumber, memberRxclaim.getFillNumber(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("numberRefillAuth: [" + numberRefillAuth + "]", () -> assertEquals(numberRefillAuth, memberRxclaim.getNumberRefillAuth(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("reportGroup1: [" + reportGroup1 + "]", () -> assertEquals(reportGroup1, memberRxclaim.getReportGroup1(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("reportGroup2: [" + reportGroup2 + "]", () -> assertEquals(reportGroup2, memberRxclaim.getReportGroup2(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("reportGroup3: [" + reportGroup3 + "]", () -> assertEquals(reportGroup3, memberRxclaim.getReportGroup3(), getApiInfo(memberRxclaim)))); 
        tests.add(dynamicTest("batchNumber [" + batchNumber + "]", () -> assertEquals(batchNumber, memberRxclaim.getBatchNumber(), getApiInfo(memberRxclaim))));




        // Perform custom validation on the item

        if ( StringUtils.isNotBlank(lastUpdated) && StringUtils.isNotBlank(memberRxclaim.getLastUpdated()) )
        {
            tests.add(compareDates("lastUpdated: [" + lastUpdated + "]", lastUpdated, memberRxclaim.getLastUpdated(),
                    new String[]{ RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", "") }, getApiInfo(memberRxclaim)));
        }
        else
        {
            tests.add(dynamicTest("lastUpdated: [" + lastUpdated + "]", () -> assertEquals(lastUpdated, memberRxclaim.getLastUpdated(), getApiInfo(memberRxclaim))));
        }

        tests.add(dynamicTest("lastUpdatedBy: [" + lastUpdatedBy + "]", () -> assertEquals(lastUpdatedBy, memberRxclaim.getLastUpdatedBy(), getApiInfo(memberRxclaim))));
        tests.add(dynamicTest("ver: [" + ver + "]", () -> assertEquals(ver, memberRxclaim.getVer(), getApiInfo(memberRxclaim))));

        // Validate id value if provided
        if ( StringUtils.isNotBlank(id) )
        {
            tests.add(dynamicTest("id [" + id + "]", () -> assertEquals(id, memberRxclaim.getId(), getApiInfo(memberRxclaim))));
        }
        // validate the actual member id is not null
        else
        {
            tests.add(dynamicTest("id (not null)", () -> assertNotNull(memberRxclaim.getId(), "id must not null: " + getApiInfo(memberRxclaim))));
        }

        //tests.addAll( super.compare(memberRxclaim) );

        return tests;
    }

    /**
     * Compare UI objects
     * added @author ntagore 01/10/23
     * @param MemberRxclaim
     * @return
     */

    public List<DynamicNode> compareUI(MemberRxclaim memberRxclaim)
    {
        List<DynamicNode> tests = new ArrayList<DynamicNode>();

        // Perform adjudication date validation        
        if ( StringUtils.isNotBlank(adjudicationDate) && StringUtils.isNotBlank(memberRxclaim.getAdjudicationDate()) )
        {
            String expectedDate = Utility.convertCosmosDateToUI(adjudicationDate, "MM/dd/yyyy");
            tests.add(dynamicTest("Date: [" + adjudicationDate + "]", 
                    () -> assertEquals(expectedDate, memberRxclaim.getAdjudicationDate()) ));
        }
        else
        {
            tests.add(dynamicTest("Date: [" + adjudicationDate + "]", 
                    () -> assertEquals(adjudicationDate, memberRxclaim.getAdjudicationDate())));
        }     

        tests.add(dynamicTest("Drug Name: [" + drugName + "]", () -> assertEquals(drugName.toUpperCase(), memberRxclaim.getDrugName().toUpperCase())));
        tests.add(dynamicTest("Strength: [" + productStrength + "]", () -> assertEquals(productStrength, memberRxclaim.getProductStrength())));
        tests.add(dynamicTest("Dosage Form: [" + dosageForm + "]", () -> assertEquals(dosageForm, memberRxclaim.getDosageForm())));
        tests.add(dynamicTest("Provider NPI: [" + prescriberId + "]", () -> assertEquals(prescriberId, memberRxclaim.getPrescriberId())));
        tests.add(dynamicTest("Provider First LastName: [" + prescriberFirstName + " " + prescriberLastName + "]", () -> assertEquals(prescriberFirstName.toUpperCase()+" "+prescriberLastName.toUpperCase(), memberRxclaim.getPrescriberFirstName().toUpperCase() +" "+memberRxclaim.getPrescriberLastName().toUpperCase())));
        tests.add(dynamicTest("Pharmacy NPI: [" + serviceProviderId + "]", () -> assertEquals(serviceProviderId, memberRxclaim.getServiceProviderId()))); 
        tests.add(dynamicTest("PharmacyName: [" + serviceProviderName + "]", () -> assertEquals(serviceProviderName.toUpperCase(), memberRxclaim.getServiceProviderName().toUpperCase())));
        tests.add(dynamicTest("Quantity: [" + quantityDispensed + "]", () -> assertEquals(quantityDispensed, memberRxclaim.getQuantityDispensed())));
        tests.add(dynamicTest("DaySupply: [" + daySupply + "]", () -> assertEquals(daySupply, memberRxclaim.getDaySupply())));
        tests.add(dynamicTest("Status: [" + claimStatusDescr + "]", () -> assertEquals(claimStatusDescr, memberRxclaim.getClaimStatusDescr()))); 
        tests.add(dynamicTest("claim Id: [" + transactionIdOrg + "]", () -> assertEquals(transactionIdOrg, memberRxclaim.getTransactionIdOrg()))); 


        return tests;
    }


    /*
     * Helper methods
     */

    /**
     * Equals is defined if the following are met
     * - rxclaim id are the same
     *
     * @return true if it mets the criteria
     */
    @Override
    public boolean equals(Object obj)
    {
        if ( obj instanceof MemberRxclaim )
        {
            MemberRxclaim memberRxclaim = (MemberRxclaim) obj;

            if ( id.equals(memberRxclaim.getId())  )
                return true;
        }


        return false;
    }


    /*
     * Setter and getter
     */

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpi() {
        return upi;
    }

    public void setUpi(String upi) {
        this.upi = upi;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getDependentCode() {
        return dependentCode;
    }

    public void setDependentCode(String dependentCode) {
        this.dependentCode = dependentCode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionIdOrg() {
        return transactionIdOrg;
    }

    public void setTransactionIdOrg(String transactionIdOrg) {
        this.transactionIdOrg = transactionIdOrg;
    }

    public String getClaimStatusCode() {
        return claimStatusCode;
    }

    public void setClaimStatusCode(String claimStatusCode) {
        this.claimStatusCode = claimStatusCode;
    }

    public String getClaimStatusDescr() {
        return claimStatusDescr;
    }

    public void setClaimStatusDescr(String claimStatusDescr) {
        this.claimStatusDescr = claimStatusDescr;
    }

    public String getDateService() {
        return dateService;
    }

    public void setDateService(String dateService) {
        this.dateService = dateService;
    }

    public String getAdjudicationDate() {
        return adjudicationDate;
    }

    public void setAdjudicationDate(String adjudicationDate) {
        this.adjudicationDate = adjudicationDate;
    }

    public String getRxBenefitId() {
        return rxBenefitId;
    }

    public void setRxBenefitId(String rxBenefitId) {
        this.rxBenefitId = rxBenefitId;
    }

    public String getRxBenefitDescr() {
        return rxBenefitDescr;
    }

    public void setRxBenefitDescr(String rxBenefitDescr) {
        this.rxBenefitDescr = rxBenefitDescr;
    }

    public String getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(String serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public String getServiceProviderName() {
        return serviceProviderName;
    }

    public void setServiceProviderName(String serviceProviderName) {
        this.serviceProviderName = serviceProviderName;
    }

    public String getPrescriberId() {
        return prescriberId;
    }

    public void setPrescriberId(String prescriberId) {
        this.prescriberId = prescriberId;
    }

    public String getPrescriberFirstName() {
        return prescriberFirstName;
    }

    public void setPrescriberFirstName(String prescriberFirstName) {
        this.prescriberFirstName = prescriberFirstName;
    }

    public String getPrescriberLastName() {
        return prescriberLastName;
    }

    public void setPrescriberLastName(String prescriberLastName) {
        this.prescriberLastName = prescriberLastName;
    }

    public String getRxNumber() {
        return rxNumber;
    }

    public void setRxNumber(String rxNumber) {
        this.rxNumber = rxNumber;
    }

    public String getNdc() {
        return ndc;
    }

    public void setNdc(String ndc) {
        this.ndc = ndc;
    }

    public String getGpi() {
        return gpi;
    }

    public void setGpi(String gpi) {
        this.gpi = gpi;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public int getQuantityDispensed() {
        return quantityDispensed;
    }

    public void setQuantityDispensed(int quantityDispensed) {
        this.quantityDispensed = quantityDispensed;
    }

    public int getQuantityPrescribed() {
        return quantityPrescribed;
    }

    public void setQuantityPrescribed(int quantityPrescribed) {
        this.quantityPrescribed = quantityPrescribed;
    }

    public int getQuantityIntendedDispense() {
        return quantityIntendedDispense;
    }

    public void setQuantityIntendedDispense(int quantityIntendedDispense) {
        this.quantityIntendedDispense = quantityIntendedDispense;
    }

    public String getProductStrength() {
        return productStrength;
    }

    public void setProductStrength(String productStrength) {
        this.productStrength = productStrength;
    }

    public String getStrengthUOM() {
        return strengthUOM;
    }

    public void setStrengthUOM(String strengthUOM) {
        this.strengthUOM = strengthUOM;
    }

    public int getStrengthMetric() {
        return strengthMetric;
    }

    public void setStrengthMetric(int strengthMetric) {
        this.strengthMetric = strengthMetric;
    }

    public int getDaySupply() {
        return daySupply;
    }

    public void setDaySupply(int daySupply) {
        this.daySupply = daySupply;
    }

    public String getMailOrderInd() {
        return mailOrderInd;
    }

    public void setMailOrderInd(String mailOrderInd) {
        this.mailOrderInd = mailOrderInd;
    }

    public String getFormularyStatus() {
        return formularyStatus;
    }

    public void setFormularyStatus(String formularyStatus) {
        this.formularyStatus = formularyStatus;
    }

    public String getFormularyStatusDescr() {
        return formularyStatusDescr;
    }

    public void setFormularyStatusDescr(String formularyStatusDescr) {
        this.formularyStatusDescr = formularyStatusDescr;
    }

    public String getTierInd() {
        return tierInd;
    }

    public void setTierInd(String tierInd) {
        this.tierInd = tierInd;
    }

    public String getGenericInd() {
        return genericInd;
    }

    public void setGenericInd(String genericInd) {
        this.genericInd = genericInd;
    }

    public String getDrugType() {
        return drugType;
    }

    public void setDrugType(String drugType) {
        this.drugType = drugType;
    }

    public String getDrugTypeDescr() {
        return drugTypeDescr;
    }

    public void setDrugTypeDescr(String drugTypeDescr) {
        this.drugTypeDescr = drugTypeDescr;
    }

    public String getDateRxWritten() {
        return dateRxWritten;
    }

    public void setDateRxWritten(String dateRxWritten) {
        this.dateRxWritten = dateRxWritten;
    }

    public String getDaw() {
        return daw;
    }

    public void setDaw(String daw) {
        this.daw = daw;
    }

    public String getDawDescr() {
        return dawDescr;
    }

    public void setDawDescr(String dawDescr) {
        this.dawDescr = dawDescr;
    }

    public double getGrossAmountDue() {
        return grossAmountDue;
    }

    public void setGrossAmountDue(double grossAmountDue) {
        this.grossAmountDue = grossAmountDue;
    }

    public double getNetAmountDue() {
        return netAmountDue;
    }

    public void setNetAmountDue(double netAmountDue) {
        this.netAmountDue = netAmountDue;
    }

    public double getAmountCopay() {
        return amountCopay;
    }

    public void setAmountCopay(double amountCopay) {
        this.amountCopay = amountCopay;
    }


    public double getAmountPeriodicDeductible() {
        return amountPeriodicDeductible;
    }

    public void setAmountPeriodicDeductible(double amountPeriodicDeductible) {
        this.amountPeriodicDeductible = amountPeriodicDeductible;
    }

    public double getAmountCoInsur() {
        return amountCoInsur;
    }

    public void setAmountCoInsur(double amountCoInsur) {
        this.amountCoInsur = amountCoInsur;
    }

    public double getDispensingFeePaid() {
        return dispensingFeePaid;
    }

    public void setDispensingFeePaid(double dispensingFeePaid) {
        this.dispensingFeePaid = dispensingFeePaid;
    }

    public double getAmountSalesTax() {
        return amountSalesTax;
    }

    public void setAmountSalesTax(double amountSalesTax) {
        this.amountSalesTax = amountSalesTax;
    }

    public double getIngredientCostPaid() {
        return ingredientCostPaid;
    }

    public void setIngredientCostPaid(double ingredientCostPaid) {
        this.ingredientCostPaid = ingredientCostPaid;
    }

    public double getMemberAmountDue() {
        return memberAmountDue;
    }

    public void setMemberAmountDue(double memberAmountDue) {
        this.memberAmountDue = memberAmountDue;
    }

    public double getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(double totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public String getTherapeuticClassCode() {
        return therapeuticClassCode;
    }

    public void setTherapeuticClassCode(String therapeuticClassCode) {
        this.therapeuticClassCode = therapeuticClassCode;
    }

    public int getFillNumber() {
        return fillNumber;
    }

    public void setFillNumber(int fillNumber) {
        this.fillNumber = fillNumber;
    }

    public int getNumberRefillAuth() {
        return numberRefillAuth;
    }

    public void setNumberRefillAuth(int numberRefillAuth) {
        this.numberRefillAuth = numberRefillAuth;
    }

    public String getReportGroup1() {
        return reportGroup1;
    }

    public void setReportGroup1(String reportGroup1) {
        this.reportGroup1 = reportGroup1;
    }

    public String getReportGroup2() {
        return reportGroup2;
    }

    public void setReportGroup2(String reportGroup2) {
        this.reportGroup2 = reportGroup2;
    }

    public String getReportGroup3() {
        return reportGroup3;
    }

    public void setReportGroup3(String reportGroup3) {
        this.reportGroup3 = reportGroup3;
    }

    public int getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(float batchNumber) {
        this.batchNumber = (int)batchNumber;
    }

    @JsonIgnore
    public int getTenantId() {

        String[] ids = memberId.split("_");

        try {
            return Integer.valueOf(ids[0]).intValue();
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setTenantId(Object tenantId) {
        try {
            this.tenantId = (int)tenantId;
        }
        catch (Exception e) {
            // do nothing
        }


    }

    @JsonIgnore
    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    @JsonIgnore
    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

}
