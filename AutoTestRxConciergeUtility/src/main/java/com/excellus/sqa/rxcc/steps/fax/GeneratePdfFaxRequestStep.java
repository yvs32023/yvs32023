/**
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.steps.fax;

import static com.excellus.sqa.step.IStep.Status.COMPLETED;
import static com.excellus.sqa.step.IStep.Status.IN_PROGRESS;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.cosmos.FaxTemplateQueries;
import com.excellus.sqa.rxcc.cosmos.InterventionRuleQueries;
import com.excellus.sqa.rxcc.cosmos.MemberQueries;
import com.excellus.sqa.rxcc.cosmos.TenantCarriersQueries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.ContactInformation;
import com.excellus.sqa.rxcc.dto.FaxTemplate;
import com.excellus.sqa.rxcc.dto.InterventionRule;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.TenantCarrier;
import com.excellus.sqa.step.AbstractStep;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Generates pdf fax request
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 01/04/2023
 */
public class GeneratePdfFaxRequestStep extends AbstractStep
{
	private static final Logger logger = LoggerFactory.getLogger(GeneratePdfFaxRequestStep.class);

	private final static String STEP_NAME = GeneratePdfFaxRequestStep.class.getSimpleName();
	private final static String STEP_DESC = "Generate PDF Fax request";

	private final MemberIntervention memberIntervention;
	private final Boolean useMemberAddress;
	private JsonObject requestBody;

	public GeneratePdfFaxRequestStep(MemberIntervention memberIntervention)
	{
		super(STEP_NAME, STEP_DESC);
		this.memberIntervention = memberIntervention;
		this.useMemberAddress = false;
	}

	public GeneratePdfFaxRequestStep(MemberIntervention memberIntervention, boolean useMemberAddress)
	{
		super(STEP_NAME, STEP_DESC);
		this.memberIntervention = memberIntervention;
		this.useMemberAddress = useMemberAddress;
	}

	@Override
	public void run()
	{
		super.stepStatus = IN_PROGRESS;

		try
		{
			Tenant tenant = TenantQueries.getTenantById(memberIntervention.getMemberId());
			InterventionRule interventionRule = InterventionRuleQueries.getInterventionRuleByRuleId(memberIntervention.getRuleId());
			FaxTemplate faxTemplate = FaxTemplateQueries.getFaxTemplateById(interventionRule.getInterventionType());
			TenantCarrier tenantCarrier = TenantCarriersQueries.getRandomCarrierByTenantSubName(tenant.getSubscriptionName());

			/*
			 * Build the PDF Fax base on https://apim-lbs-rxc-dev-east-001.developer.azure-api.net/api-details#api=faxrequest&operation=post-createfaxrequest
			 */

			requestBody = new JsonObject();

			/* Logo name and address */

			if ( memberIntervention.getPdfLogoAddress() != null )
			{
				requestBody.addProperty("tenantLine1", memberIntervention.getPdfLogoAddress().getLine1());
				requestBody.addProperty("tenantLine2", memberIntervention.getPdfLogoAddress().getLine2());
				requestBody.addProperty("tenantLine3", memberIntervention.getPdfLogoAddress().getLine3());
			}
			else if ( tenantCarrier.getPdfLogoAddress() != null )
			{
				requestBody.addProperty("tenantLine1", tenantCarrier.getPdfLogoAddress().getLine1());
				requestBody.addProperty("tenantLine2", tenantCarrier.getPdfLogoAddress().getLine2());
				requestBody.addProperty("tenantLine3", tenantCarrier.getPdfLogoAddress().getLine3());
			}
			else if ( tenant.getPdfLogoAddress() != null )
			{
				requestBody.addProperty("tenantLine1", tenant.getPdfLogoAddress().getLine1());
				requestBody.addProperty("tenantLine2", tenant.getPdfLogoAddress().getLine2());
				requestBody.addProperty("tenantLine3", tenant.getPdfLogoAddress().getLine3());
			}
			
			JsonArray prerequisiteDrugs = new JsonArray();
			
			memberIntervention.getPrerequisiteProductName().forEach(prerequisiteDrugs::add);
		
			requestBody.add("prerequisiteDrugs", prerequisiteDrugs);
			requestBody.addProperty("qualityFreeFormText", memberIntervention.getQualityFreeFormText());
			requestBody.addProperty("qualityTitleText", interventionRule.getQualityTitleText());

			requestBody.addProperty("date", DateTimeUtils.generateTimeStamp("MM/dd/yyyy", "GMT"));	// Date

			/* Provider */

			requestBody.addProperty("providerHeader", faxTemplate.getProviderHeader());

			if ( memberIntervention.isProviderOverride() )
			{
				requestBody.addProperty("providerLine1", memberIntervention.getOverrideProviderName());
				buildProviderContact(requestBody, memberIntervention.getOverrideProviderContact());
			}
			else
			{
				requestBody.addProperty("providerLine1", memberIntervention.getOriginalProviderName());
				buildProviderContact(requestBody, memberIntervention.getOriginalProviderContact());
			}

			/* Member */

			requestBody.addProperty("interventionId", memberIntervention.getId());

			requestBody.addProperty("memberHeader", faxTemplate.getMemberHeader());
			requestBody.addProperty("memberId", memberIntervention.getMemberId());
			requestBody.addProperty("memberLine1", memberIntervention.getMemberName());
			requestBody.addProperty("memberLine2",
					"DOB: " + DateTimeUtils.normalizeDate(memberIntervention.getMemberDateBirth(), "yyyyMMdd", "MM/dd/yyyy"));

			if ( this.useMemberAddress )
			{
				requestBody.addProperty("memberLine3", memberIntervention.getMemberAddress().getLine1());

				String temp = StringUtils.isNotBlank(memberIntervention.getMemberAddress().getLine2()) ?
									  memberIntervention.getMemberAddress().getLine2() + ", " : "";
				temp += StringUtils.isNotBlank(memberIntervention.getMemberAddress().getLine3()) ?
								memberIntervention.getMemberAddress().getLine3() + " " : "";
				temp += StringUtils.isNotBlank(memberIntervention.getMemberAddress().getLine4()) ?
								memberIntervention.getMemberAddress().getLine4() : "";

				requestBody.addProperty("memberLine4", temp);
			}
			else	// Use member subscription
			{
				Member member = MemberQueries.getMemberById(memberIntervention.getMemberId());
				requestBody.addProperty("memberLine3", "Subscriber ID: " + member.getSubscriberId());
			}

			/* ECR 1 & 2 blocks */

			if ( StringUtils.isNotBlank(faxTemplate.getEcrBlock1()) ) {
				requestBody.addProperty("ecrBlock1", faxTemplate.getEcrBlock1());
			}

			if ( StringUtils.isNotBlank(faxTemplate.getEcrBlock2()) ) {
				requestBody.addProperty("ecrBlock2", faxTemplate.getEcrBlock2());
			}

			/* Target drug */

			requestBody.addProperty("currentMedicationHeader", "Current Medication(s)");
			requestBody.addProperty("currentMedicationText", memberIntervention.getTargetProductName());

			/* Alternative drug */

			requestBody.addProperty("alternativeHeader", "Suggested Alternative(s)");
			requestBody.addProperty("alternativeText", memberIntervention.getFaxAlternativeText());

			/* ECR 3 block */

			if ( StringUtils.isNotBlank(faxTemplate.getEcrBlock3()) ) {
				requestBody.addProperty("ecrBlock3", faxTemplate.getEcrBlock3());
			}

			/* Savings */

			if ( StringUtils.isNotBlank(interventionRule.getSavingsInformationText()) )
			{
				requestBody.addProperty("averageCostSavings", interventionRule.getSavingsInformationText());
			}

			/* Accept / Decline */

			requestBody.addProperty("acceptText", StringUtils.isNotBlank(memberIntervention.getAcceptText()) ?
														  memberIntervention.getAcceptText() : faxTemplate.getAcceptText());

			requestBody.addProperty("declineText", StringUtils.isNotBlank(memberIntervention.getDeclineText()) ?
														   memberIntervention.getDeclineText() : faxTemplate.getDeclineText());

			/* Closure */

			if ( StringUtils.isNotBlank(faxTemplate.getEcrClosure()) ) {
				String str = faxTemplate.getEcrClosure();
				str = StringUtils.replace(str, "1-585-327-3509", tenant.getPhoneNumber());
				str = StringUtils.replace(str, "1-888-905-0968", tenant.getFaxNumber());
				requestBody.addProperty("ecrClosure", str);
			}

			/* Footnote */

			if ( StringUtils.isNotBlank(faxTemplate.getEcrFooter()) ) {
				requestBody.addProperty("ecrFooter", faxTemplate.getEcrFooter());
			}

			/* Miscellaneous information */

			requestBody.addProperty("npi", memberIntervention.getOriginalNpi());
			requestBody.addProperty("pdfFormName", faxTemplate.getPdfFormName());
			requestBody.addProperty("phoneNumber", memberIntervention.getOriginalProviderContact().getPhoneNumber());
			requestBody.addProperty("targetDrug", memberIntervention.getTargetProductName());
			requestBody.addProperty("tenantCarrierId", tenantCarrier.getCarrierId());
			requestBody.addProperty("tenantSubscriptionName", tenant.getSubscriptionName());
			requestBody.addProperty("type", "pdf");

			logger.info("Request JSON body: " + requestBody);

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

		String temp = StringUtils.isNotBlank(contactInformation.getCity()) ? contactInformation.getCity() + ", " : "";
		temp += StringUtils.isNotBlank(contactInformation.getState()) ? contactInformation.getState() + " " : "";
		temp += StringUtils.isNotBlank(contactInformation.getPostalCode()) ? contactInformation.getPostalCode() : "";

		requestBody.addProperty("providerLine3",  temp);

		//requestBody.addProperty("faxNumber", contactInformation.getFaxNumber()); //Changing this to static value for testing 
		requestBody.addProperty("faxNumber", "5853273508");
	}

	public JsonObject getRequestBody()
	{
		return requestBody;
	}

}
