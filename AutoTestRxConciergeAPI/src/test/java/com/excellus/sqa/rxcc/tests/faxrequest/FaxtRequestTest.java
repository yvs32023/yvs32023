/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.tests.faxrequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.dto.FaxTemplate;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.dto.TenantCarrier;
import com.excellus.sqa.utilities.DateTimeUtils;
import com.google.gson.JsonObject;

/**
 * This contains all helper methods for testing fax requests
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 12/01/2022
 * @deprecated Moved these into step classes.
 */
@Deprecated
public class FaxtRequestTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(FaxtRequestTest.class);

	protected static JsonObject buildJsonBody(MemberIntervention memberIntervention, FaxTemplate faxTemplate, Tenant tenant, TenantCarrier tenantCarrier)
	{
		JsonObject requestBody = new JsonObject();

		requestBody.addProperty("type", "pdf");
		requestBody.addProperty("phoneNumber", memberIntervention.getOriginalProviderContact().getPhoneNumber());
		requestBody.addProperty("faxNumber", memberIntervention.getOriginalProviderContact().getFaxNumber());
		requestBody.addProperty("npi", memberIntervention.getOriginalNpi());
		requestBody.addProperty("override", "false");
		requestBody.addProperty("tenantSubscriptionName", tenant.getSubscriptionName());
		requestBody.addProperty("tenantCarrierId", tenantCarrier.getCarrierId());

		if ( memberIntervention.getPdfLogoAddress() != null )
		{
			requestBody.addProperty("tenantLine1", StringUtils.isNotBlank(memberIntervention.getPdfLogoAddress().getLine1()) ?
														   memberIntervention.getPdfLogoAddress().getLine1() : tenantCarrier.getPdfLogoAddress().getLine1());
			requestBody.addProperty("tenantLine2", StringUtils.isNotBlank(memberIntervention.getPdfLogoAddress().getLine2()) ?
														   memberIntervention.getPdfLogoAddress().getLine2() : tenantCarrier.getPdfLogoAddress().getLine2());
			requestBody.addProperty("tenantLine3", StringUtils.isNotBlank(memberIntervention.getPdfLogoAddress().getLine3()) ?
														   memberIntervention.getPdfLogoAddress().getLine3() : tenantCarrier.getPdfLogoAddress().getLine3());
		}
		else
		{
			requestBody.addProperty("tenantLine1", tenantCarrier.getPdfLogoAddress().getLine1());
			requestBody.addProperty("tenantLine2", tenantCarrier.getPdfLogoAddress().getLine2());
			requestBody.addProperty("tenantLine3", tenantCarrier.getPdfLogoAddress().getLine3());
		}

		try {
			requestBody.addProperty("date", DateTimeUtils.generateTimeStamp("mm/dd/yyyy"));
		}
		catch(Exception e) {
            logger.error("An unexpected error is caught while generating timestamp", e);
		}
		
		requestBody.addProperty("providerHeader", faxTemplate.getProviderHeader());

		if ( memberIntervention.isProviderOverride() )
		{
			requestBody.addProperty("providerLine1", memberIntervention.getOverrideProviderName());
			requestBody.addProperty("providerLine2", "1023368818");	// TODO not certain where this data should come from

			if ( memberIntervention.getOverrideProviderContact() != null )
			{
				requestBody.addProperty("providerLine3", memberIntervention.getOverrideProviderContact().getAddress1());

				String temp = StringUtils.isNotBlank(memberIntervention.getOverrideProviderContact().getCity()) ?
									  memberIntervention.getOverrideProviderContact().getCity() + ", " : "";
				temp += StringUtils.isNotBlank(memberIntervention.getOverrideProviderContact().getState()) ?
								memberIntervention.getOverrideProviderContact().getState() + " " : "";
				temp += StringUtils.isNotBlank(memberIntervention.getOverrideProviderContact().getPostalCode()) ?
								memberIntervention.getOverrideProviderContact().getPostalCode() : "";
				requestBody.addProperty("providerLine4",  temp);
			}
		}
		else
		{
			requestBody.addProperty("providerLine1", memberIntervention.getOriginalProviderName());
			requestBody.addProperty("providerLine2", "1023368818");	// TODO not certain where this data should come from

			requestBody.addProperty("providerLine3", memberIntervention.getOriginalProviderContact().getAddress1());

			String temp = StringUtils.isNotBlank(memberIntervention.getOriginalProviderContact().getCity()) ?
								  memberIntervention.getOriginalProviderContact().getCity() + ", " : "";
			temp += StringUtils.isNotBlank(memberIntervention.getOriginalProviderContact().getState()) ?
							memberIntervention.getOriginalProviderContact().getState() + " " : "";
			temp += StringUtils.isNotBlank(memberIntervention.getOriginalProviderContact().getPostalCode()) ?
							memberIntervention.getOriginalProviderContact().getPostalCode() : "";
			requestBody.addProperty("providerLine4",  temp);
		}

		requestBody.addProperty("memberHeader", faxTemplate.getMemberHeader());
		requestBody.addProperty("memberLine1", memberIntervention.getMemberName());
		requestBody.addProperty("memberLine2", memberIntervention.getMemberDateBirth());
		requestBody.addProperty("memberLine3", memberIntervention.getMemberAddress().getLine1());

		String temp = StringUtils.isNotBlank(memberIntervention.getMemberAddress().getLine2()) ?
							  memberIntervention.getMemberAddress().getLine2() + ", " : "";
		temp += StringUtils.isNotBlank(memberIntervention.getMemberAddress().getLine3()) ?
						memberIntervention.getMemberAddress().getLine3() + " " : "";
		temp += StringUtils.isNotBlank(memberIntervention.getMemberAddress().getLine4()) ?
						memberIntervention.getMemberAddress().getLine4() : "";

		requestBody.addProperty("memberLine4", temp);

		requestBody.addProperty("memberId", memberIntervention.getMemberId());
		requestBody.addProperty("interventionId", memberIntervention.getId());

		requestBody.addProperty("acceptText", StringUtils.isNotBlank(memberIntervention.getAcceptText()) ?
													  memberIntervention.getAcceptText() : faxTemplate.getAcceptText());

		requestBody.addProperty("declineText", StringUtils.isNotBlank(memberIntervention.getDeclineText()) ?
													   memberIntervention.getDeclineText() : faxTemplate.getDeclineText());

		requestBody.addProperty("targetDrug", memberIntervention.getTargetProductName());

		requestBody.addProperty("qualityInterventionText", StringUtils.isNotBlank(memberIntervention.getQualityInterventionText()) ?
																   memberIntervention.getQualityInterventionText() : QUALITY_INTERVENTION_TEXT);

		if ( StringUtils.isNotBlank(faxTemplate.getEcrBlock1()) ) {
			requestBody.addProperty("ecrBlock1", faxTemplate.getEcrBlock1());
		}

		if ( StringUtils.isNotBlank(faxTemplate.getEcrBlock2()) ) {
			requestBody.addProperty("ecrBlock2", faxTemplate.getEcrBlock2());
		}

		if ( StringUtils.isNotBlank(faxTemplate.getEcrBlock3()) ) {
			requestBody.addProperty("ecrBlock3", faxTemplate.getEcrBlock3());
		}

		if ( StringUtils.isNotBlank(faxTemplate.getEcrClosure()) ) {
			requestBody.addProperty("ecrClosure", faxTemplate.getEcrClosure());
		}

		if ( StringUtils.isNotBlank(faxTemplate.getEcrFooter()) ) {
			requestBody.addProperty("ecrFooter", faxTemplate.getEcrFooter());
		}

		if ( StringUtils.containsIgnoreCase(faxTemplate.getFormName(), "Cost Savings"))
		{
			requestBody.addProperty("averageCostSavings", AVERAGE_COST_SAVINGS);
		}

		requestBody.addProperty("pdfFormName", faxTemplate.getPdfFormName());

		logger.info("Request JSON body: " + requestBody);

		return requestBody;
	}


	/*
	 * Test methods
	 */



	private final static String ENDPOINT_OUTBOUND_TIMER = "https://faw-lbs-rxc-tst-outbound-east-001.azurewebsites.net/admin/functions/OutboundTimer";

	private final static String OUTBOUND_X_FUNCTION_KEY = "57EWTYwbBE1f3RBP_F20O21zZkyv_oPG7E0hFkUDvDTlAzFu96-yOg==";

	private final static String INBOUND_TIMER_ENDPOINT = "https://faw-lbs-rxc-tst-outbound-east-001.azurewebsites.net/admin/functions/OutboundTimer";

	private final static String INBOUND_X_FUNCTION_KEY = "57EWTYwbBE1f3RBP_F20O21zZkyv_oPG7E0hFkUDvDTlAzFu96-yOg==";

	protected final static String ECR_BLOCK_1 = "We are continually working with our members and their employers to improve health outcomes, " +
													  "leading to more affordable access to healthcare. After conducting a review, a potential opportunity " +
													  "was identified for one of your patients and we would appreciate your review to help evaluate if this " +
													  "possible overall health improvement opportunity is appropriate for your patient.";

	protected final static String TARGET_DRUG = "Gap in Care";

	protected final static String QUALITY_INTERVENTION_TEXT = "It is important to take care of the patient, to be followed by the doctor, but it is a time of " +
																	"great pain and suffering. For to come to the smallest detail, no one should practice any " +
																	"kind of work unless he derives some benefit from it. Do not be angry with the pain in the " +
																	"reprimand in the pleasure he wants to be a hair from the pain in the hope that there is no " +
																	"breeding. Unless they are blinded by lust, they do not come out; they are in fault who abandon " +
																	"their duties and soften their hearts, that is toil.";
	protected final static String ECR_BLOCK_2 = "Please consider changing to one of the alternatives listed below for this patient, if clinically appropriate: ";

	protected final static String CURRENT_MEDICATION_HEADER = "Current Medication(s)";

	protected final static String CURRENT_MEDICATION_TEXT = "Ibuprofin - NSAID";

	protected final static String ALTERNATIVE_HEADER = "Preferred Alternative(s)";

	protected final static String ALTERNATIVE_TEXT = "Recommend Acetaminophen to reduce risk to kidneys";

	protected final static String ECR_BLOCK_3 = "Choosing a lower cost/preferred product reduces total drug spend, maintains clinical efficiency, and may reduce " +
													  "patient out-of-pocket costs.";

	protected final static String AVERAGE_COST_SAVINGS = "Average Cost Savings $37.50 for a 90 day supply";

	protected final static String ACCEPT_TEXT = "If Accepting: Please send a new prescription for the preferred product to the patient's pharmacy and cancel the " +
													  "patient's current prescription.";

	protected final static String DECLINE_TEXT = "If Declining, please provide rationale below: ";

	protected final static String ECR_CLOSURE = "\"Once you have reached a decision (above), please fax this document to 1 800 555 1234.\n\n" +
													  "If you have questions and would like to speak to a member of our staff, please call 1-888-905-0968.\n\n" +
													  "Thank you for your time and consideration.";

	protected final static String ECR_FOOTER = "If you have received this communication in error or would like to opt out call the number listed above.";

}
