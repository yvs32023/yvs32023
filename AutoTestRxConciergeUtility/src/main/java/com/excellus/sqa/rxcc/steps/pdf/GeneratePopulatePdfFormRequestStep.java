/**
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.steps.pdf;

import static com.excellus.sqa.step.IStep.Status.COMPLETED;
import static com.excellus.sqa.step.IStep.Status.IN_PROGRESS;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.cosmos.FaxTemplateQueries;
import com.excellus.sqa.rxcc.cosmos.InterventionRuleQueries;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionQueries;
import com.excellus.sqa.rxcc.cosmos.Queries;
import com.excellus.sqa.rxcc.cosmos.TenantCarriersQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.ContactInformation;
import com.excellus.sqa.rxcc.dto.FaxTemplate;
import com.excellus.sqa.rxcc.dto.InterventionRule;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.TenantCarrier;
import com.excellus.sqa.step.AbstractStep;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/** 
 * Generates the request body for a POST to ../api/pdf/populate/form API.
 * 
 * @author Roland Burbulis (rburbuli)
 * @since 04/13/2023
 */
public class GeneratePopulatePdfFormRequestStep extends AbstractStep
{
	private static final Logger logger = LoggerFactory.getLogger(GeneratePopulatePdfFormRequestStep.class);

	private final static String STEP_NAME = GeneratePopulatePdfFormRequestStep.class.getSimpleName();
	private final static String STEP_DESC = "Generate request body for a POST to ../api/pdf/populate/form API";

	private final String tenantSubscriptionName;
	private final boolean includeTargetDrug;
	private final boolean includePrerequisiteDrugs;
	private final boolean includeQualityFreeFormText;
	private final boolean includeQualityTitleText;

	private JsonObject requestBody;

	public GeneratePopulatePdfFormRequestStep(
		String tenantSubscriptionName,
		boolean includeTargetDrug,
		boolean includePrerequisiteDrugs,
		boolean includeQualityFreeFormText,
		boolean includeQualityTitleText)
	{
		super(STEP_NAME, STEP_DESC);
		
		this.tenantSubscriptionName = tenantSubscriptionName;
		this.includeTargetDrug = includeTargetDrug;
		this.includePrerequisiteDrugs = includePrerequisiteDrugs;
		this.includeQualityFreeFormText = includeQualityFreeFormText;
		this.includeQualityTitleText = includeQualityTitleText;
	}

	@Override
	public void run()
	{
		super.stepStatus = IN_PROGRESS;

		try
		{
			String memberId = MemberInterventionQueries.getRandomMemberWithIntervention(tenantSubscriptionName);
			
			List<MemberIntervention> memberInterventions = MemberInterventionQueries.getIntervention(tenantSubscriptionName, memberId);
			
			//TODO:  Eventually we would want to do this but we don't have interventions for all members for all
			//tenants that have prerequisiteProductName
			/*if(includePrerequisiteDrugs)
			{
				memberInterventions = memberInterventions
						.stream()
						.filter(intervention -> intervention.getPrerequisiteProductName() != null)
						.collect(Collectors.toList());
			}*/
			
			// pick random member intervention
			MemberIntervention memberIntervention = Queries.getRandomItem(memberInterventions);
			
			Tenant tenant = TenantQueries.getTenantById(memberIntervention.getMemberId());
			
			TenantCarrier tenantCarrier = TenantCarriersQueries.getRandomCarrierByTenantSubName(tenant.getSubscriptionName());
			
			InterventionRule interventionRule = InterventionRuleQueries.getInterventionRuleByRuleId(memberIntervention.getRuleId());
			
			FaxTemplate faxTemplate = FaxTemplateQueries.getFaxTemplateById(interventionRule.getInterventionType());
			
			requestBody = new JsonObject();
			
			requestBody.addProperty("type", "pdf");
			requestBody.addProperty("phoneNumber", memberIntervention.getOriginalProviderContact().getPhoneNumber()); 
			requestBody.addProperty("faxNumber", "5853273508");  //Static for testing
			requestBody.addProperty("tenantSubscriptionName", tenantSubscriptionName);
			
			if(tenantCarrier != null)
			{
				requestBody.addProperty("tenantCarrierId", tenantCarrier.getCarrierId());
			}
			
			if(memberIntervention.getPdfLogoAddress() != null)
			{
				requestBody.addProperty("tenantLine1", memberIntervention.getPdfLogoAddress().getLine1());
				requestBody.addProperty("tenantLine2", memberIntervention.getPdfLogoAddress().getLine2());
				requestBody.addProperty("tenantLine3", memberIntervention.getPdfLogoAddress().getLine3());
			}
			else if(tenantCarrier != null && tenantCarrier.getPdfLogoAddress() != null)
			{
				requestBody.addProperty("tenantLine1", tenantCarrier.getPdfLogoAddress().getLine1());
				requestBody.addProperty("tenantLine2", tenantCarrier.getPdfLogoAddress().getLine2());
				requestBody.addProperty("tenantLine3", tenantCarrier.getPdfLogoAddress().getLine3());
			}
			else if(tenant.getPdfLogoAddress() != null)
			{
				requestBody.addProperty("tenantLine1", tenant.getPdfLogoAddress().getLine1());
				requestBody.addProperty("tenantLine2", tenant.getPdfLogoAddress().getLine2());
				requestBody.addProperty("tenantLine3", tenant.getPdfLogoAddress().getLine3());
			}
			
			requestBody.addProperty("providerHeader", faxTemplate.getProviderHeader());
			requestBody.addProperty("providerLine1", memberIntervention.isProviderOverride()
				? memberIntervention.getOverrideProviderName()
				: memberIntervention.getOriginalProviderName());
			
			buildProviderContact(requestBody, memberIntervention.isProviderOverride()
				? memberIntervention.getOverrideProviderContact()
				: memberIntervention.getOriginalProviderContact());
			
			requestBody.addProperty("memberHeader", faxTemplate.getMemberHeader());
			requestBody.addProperty("memberLine1", memberIntervention.getMemberName());
			requestBody.addProperty("memberLine2", "DOB: " + DateTimeUtils.normalizeDate(memberIntervention.getMemberDateBirth(), "yyyyMMdd", "MM/dd/yyyy"));
			requestBody.addProperty("memberLine3", memberIntervention.getMemberAddress().getLine1());

			String memberLine4 = StringUtils.isNotBlank(memberIntervention.getMemberAddress().getLine2())
									? memberIntervention.getMemberAddress().getLine2() + ", "
									: "";
			
			memberLine4 += StringUtils.isNotBlank(memberIntervention.getMemberAddress().getLine3())
								? memberIntervention.getMemberAddress().getLine3() + " "
								: "";
			memberLine4 += StringUtils.isNotBlank(memberIntervention.getMemberAddress().getLine4())
								? memberIntervention.getMemberAddress().getLine4()
								: "";

			requestBody.addProperty("memberLine4", memberLine4);

			if(StringUtils.isNotBlank(faxTemplate.getEcrBlock1()))
			{
				requestBody.addProperty("ecrBlock1", faxTemplate.getEcrBlock1());
			}
			
			if(includeTargetDrug)
			{
				requestBody.addProperty("targetDrug", memberIntervention.getTargetProductName() != null
					? memberIntervention.getTargetProductName()
					: "Some target drug");
			}
			
			if(includePrerequisiteDrugs)
			{
				JsonArray prerequisiteDrugs = new JsonArray();
				
				if(memberIntervention.getPrerequisiteProductName() != null)
				{
					memberIntervention.getPrerequisiteProductName().forEach(prerequisiteDrugs::add);
				}
				else 
				{
					prerequisiteDrugs.add("Some prerequisite drug");
				}
			
				requestBody.add("prerequisiteDrugs", prerequisiteDrugs);
			}
			
			if(includeQualityFreeFormText)
			{
				requestBody.addProperty("qualityFreeFormText", memberIntervention.getQualityFreeFormText() != null
					? memberIntervention.getQualityFreeFormText()
					: "Some quality free form text");			
			}

			if(includeQualityTitleText)
			{
				requestBody.addProperty("qualityTitleText", interventionRule.getQualityTitleText() != null
					? interventionRule.getQualityTitleText()
					: "Some quality title text");	
			}
		
			requestBody.addProperty("qualityInterventionText", memberIntervention.getQualityInterventionText());
			
			if(StringUtils.isNotBlank(faxTemplate.getEcrBlock2()))
			{
				requestBody.addProperty("ecrBlock2", faxTemplate.getEcrBlock2());
			}
			
			requestBody.addProperty("currentMedicationHeader", "Current Medication(s)");
			requestBody.addProperty("currentMedicationText", memberIntervention.getTargetProductName());
			requestBody.addProperty("alternativeHeader", "Suggested Alternative(s)");
			requestBody.addProperty("alternativeText", memberIntervention.getFaxAlternativeText());
			
			if(StringUtils.isNotBlank(faxTemplate.getEcrBlock3()))
			{
				requestBody.addProperty("ecrBlock3", faxTemplate.getEcrBlock3());
			}
			
			if(StringUtils.isNotBlank(interventionRule.getSavingsInformationText()))
			{
				requestBody.addProperty("averageCostSavings", interventionRule.getSavingsInformationText());
			}
			
			requestBody.addProperty("acceptText", StringUtils.isNotBlank(memberIntervention.getAcceptText())
				? memberIntervention.getAcceptText()
				: faxTemplate.getAcceptText());

			requestBody.addProperty("declineText", StringUtils.isNotBlank(memberIntervention.getDeclineText())
				? memberIntervention.getDeclineText()
				: faxTemplate.getDeclineText());

			if(StringUtils.isNotBlank(faxTemplate.getEcrClosure()))
			{
				String ecrClosure = faxTemplate.getEcrClosure();
				
				ecrClosure = StringUtils.replace(ecrClosure, "1-585-327-3509", tenant.getPhoneNumber());
				ecrClosure = StringUtils.replace(ecrClosure, "1-888-905-0968", tenant.getFaxNumber());
				
				requestBody.addProperty("ecrClosure", ecrClosure);
			}
			
			requestBody.addProperty("memberId", memberIntervention.getMemberId());
			requestBody.addProperty("interventionId", memberIntervention.getId());
			
			if(StringUtils.isNotBlank(faxTemplate.getEcrFooter()))
			{
				requestBody.addProperty("ecrFooter", faxTemplate.getEcrFooter());
			}
			
			requestBody.addProperty("pdfFormName", faxTemplate.getPdfFormName());	

			logger.info("Request body for the POST to ../api/pdf/populate/form API:  " + requestBody);

			super.stepStatus = COMPLETED;
		}
		catch (Exception e)
		{
			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}

		logger.info(print());
	}
	
	private void buildProviderContact(JsonObject requestBody, ContactInformation contactInformation)
	{
		requestBody.addProperty("providerLine2", contactInformation.getAddress1());

		String providerLine3 = StringUtils.isNotBlank(contactInformation.getCity()) ? contactInformation.getCity() + ", " : "";
		
		providerLine3 += StringUtils.isNotBlank(contactInformation.getState()) ? contactInformation.getState() + " " : "";
		providerLine3 += StringUtils.isNotBlank(contactInformation.getPostalCode()) ? contactInformation.getPostalCode() : "";

		requestBody.addProperty("providerLine3", providerLine3);
	}

	public JsonObject getRequestBody()
	{
		return requestBody;
	}
}
