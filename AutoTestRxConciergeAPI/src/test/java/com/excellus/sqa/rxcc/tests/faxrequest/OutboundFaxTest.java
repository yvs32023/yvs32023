/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.tests.faxrequest;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.roles.UserRole;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.configuration.RxConciergeUILogin;
import com.excellus.sqa.rxcc.cosmos.FaxRequestQueries;
import com.excellus.sqa.rxcc.cosmos.MemberInterventionQueries;
import com.excellus.sqa.rxcc.cosmos.Queries;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.FaxRequest;
import com.excellus.sqa.rxcc.dto.MemberIntervention;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.steps.fax.GeneratePdfFaxRequestStep;
import com.excellus.sqa.rxcc.steps.fax.RetrievePdfFaxStep;
import com.excellus.sqa.rxcc.steps.fax.ValidateFaxPdfContentStep;
import com.excellus.sqa.rxcc.steps.io.WriteByteArrayToFileSystemStep;
import com.excellus.sqa.rxcc.steps.pdf.ConvertPdfToStringUsingOcrStep;
import com.excellus.sqa.rxcc.steps.pdf.GetPdfsPathStep;
import com.excellus.sqa.rxcc.workflows.fax.ProcessSentFaxWorkflow;
import com.excellus.sqa.rxcc.workflows.fax.SendExtrafaxWorkflow;
import com.excellus.sqa.step.IStep;
import com.excellus.sqa.workflow.IWorkflow.Mode;
import com.google.gson.JsonObject;

import io.restassured.http.Header;
import io.restassured.http.Headers;

/**
 * Perform outbound fax testing
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 11/28/2022
 */
@Tag("ALL")
@Tag("FAXREQUEST")
@Tag("OUTBOUND")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("OutboundFaxRequest")
@UserRole(role = {"RXCC_FULL_SINGLE", "RXCC_FULL_SINGLE", "RXCC_FULL_LOA", "RXCC_FULL_MED"})
public class OutboundFaxTest extends RxConciergeAPITestBaseV2
{
	private static final Logger logger = LoggerFactory.getLogger(OutboundFaxTest.class);

	private static List<Tenant> tenants;

	private static Map<String, JsonObject> memberFaxRequest = new HashMap<>();
	private static Map<String, JsonObject> memberFaxRequestSuccessful = new HashMap<>();
	private static Map<String, Integer> jobQueued = new HashMap<>();
	private static List<String> interventionIds = new ArrayList<>();
	private static List<FaxRequest> faxRequests = new ArrayList<>();

	private static Map<String, Integer> originalNpiCounts;

	private static boolean continueTest = true;


	@BeforeAll
	public static void setupTestData()
	{
		logger.info("Setup the test data for fax");
		Map<String, MemberIntervention> memberIntervention = new HashMap<>();

		tenants = TenantQueries.getTenantsByAccountName(RxConciergeUILogin.getAcctName());

		for ( Tenant tenant : tenants )
		{
			String memberId = MemberInterventionQueries.getRandomMemberWithIntervention(tenant.getSubscriptionName());
			if (StringUtils.isNotBlank(memberId))
			{
				// get random intervention
				List<MemberIntervention> interventions = MemberInterventionQueries.getIntervention(tenant.getSubscriptionName(), memberId)
																 .stream()
																 .filter(intervention -> intervention.getQueueStatusCode().equals("6") // Get only queue status 6 (Generated)
																		 && intervention.getPrerequisiteProductName() != null)
																 .collect(Collectors.toList());
				
				memberIntervention.put(memberId, Queries.getRandomItem(interventions));

				/*
				 * build Fax request
				 */

				GeneratePdfFaxRequestStep generatePdfFaxRequestStep = new GeneratePdfFaxRequestStep(memberIntervention.get(memberId), true);
				generatePdfFaxRequestStep.run();

				memberFaxRequest.put(memberId, generatePdfFaxRequestStep.getRequestBody());
			}
		}

		// retrieve fax request
		List<FaxRequest> faxRequests = FaxRequestQueries.getAllFaxRequests();
		FaxRequest faxRequestMain = faxRequests.stream()
											.filter( faxRequest -> StringUtils.equalsIgnoreCase(faxRequest.getPhoneNumber(), "0000000000"))
											.findFirst()
											.orElse(null);

		if (faxRequestMain != null )
			originalNpiCounts = faxRequestMain.getNpiCount();
	}

	@TestFactory
	@DisplayName("Send fax request")
	@Order(1)
	public List<DynamicNode> sendFaxRequest()
	{
		List<DynamicNode> tests = new LinkedList<>();

		for ( Map.Entry<String, JsonObject> faxRequest : memberFaxRequest.entrySet() )
		{
			try
			{
				Tenant tenant = TenantQueries.getTenantById(faxRequest.getKey());
				Headers headers = getGenericHeaders(new Header("X-RXCC-SUB", tenant.getSubscriptionName()));

				/*
				 * Send fax request
				 */

				ApiPostStep apiPostStep = new ApiPostStep(headers, FAX_REQUESTS_POST_ENDPOINT, faxRequest.getValue().toString(), null, 200, HTTP_200_OK);
				apiPostStep.run();

				tests.add(dynamicContainer(tenant.getSubscriptionName().toUpperCase() + " - member id: " + faxRequest.getKey(), apiPostStep.getTestResults()));

				if ( apiPostStep.stepStatus() == IStep.Status.COMPLETED && apiPostStep.getResponseStatusCode() == 200 ) {
					memberFaxRequestSuccessful.put(faxRequest.getKey(), faxRequest.getValue());
					jobQueued.put(faxRequest.getKey(), null);

					JsonObject request = faxRequest.getValue();
					interventionIds.add(request.get("interventionId").getAsString());
				}
			}
			catch (Exception e)
			{
				tests.add(dynamicTest(faxRequest.getKey(), () -> fail(e)));
			}
		}

		if ( memberFaxRequestSuccessful.size() < 1 ) {
			continueTest = false;	// no sense of continuing the test if no fax request was successful
		}

		return tests;
	}
	
	@TestFactory
	@DisplayName("Send fax")
	@Order(2)
	public List<DynamicNode> sendFax()
	{
		assumeTrue(continueTest && memberFaxRequestSuccessful.size() > 0 );
		
		List<DynamicNode> tests = new LinkedList<>();
		
		for ( Map.Entry<String, JsonObject> entry : memberFaxRequestSuccessful.entrySet() )
		{
			List<DynamicNode> result = new LinkedList<>();
			
			String interventionId = entry.getValue().get("interventionId").getAsString();
			SendExtrafaxWorkflow sendExtrafaxWorkflow = new SendExtrafaxWorkflow(Mode.TEST, RxConciergeUILogin.getTokenAccess(), entry.getValue(), interventionId, RxConciergeUILogin.getAcctName());
			sendExtrafaxWorkflow.run();
			
			result.addAll(sendExtrafaxWorkflow.workflowStepResults());
			result.addAll(sendExtrafaxWorkflow.workflowTestResults());
			tests.add(dynamicContainer("memberId: " + entry.getKey(), result));
			
			faxRequests.add(sendExtrafaxWorkflow.getFaxRequest());
		}
		
		return tests;
	}
	
	@TestFactory
	@DisplayName("Process sent fax")
	@Order(3)
	public List<DynamicNode> processSentFax()
	{
		assumeTrue(continueTest && memberFaxRequestSuccessful.size() > 0 );
		
		List<DynamicNode> tests = new LinkedList<>();
		
		for ( Map.Entry<String, JsonObject> entry : memberFaxRequestSuccessful.entrySet() )
		{
			List<DynamicNode> result = new LinkedList<>();
			
			String interventionId = entry.getValue().get("interventionId").getAsString();
			ProcessSentFaxWorkflow processSentFaxWorkflow = new ProcessSentFaxWorkflow(Mode.TEST, RxConciergeUILogin.getTokenAccess(), interventionId, entry.getValue(), originalNpiCounts);
			processSentFaxWorkflow.run();
			
			result.addAll(processSentFaxWorkflow.workflowStepResults());
			result.addAll(processSentFaxWorkflow.workflowTestResults());
			tests.add(dynamicContainer("memberId: " + entry.getKey(), result));
		}
		
		return tests;
	}


	/*
	 * Validate PDF
	 */
	@TestFactory
	@DisplayName("Validate PDF")
	@Order(4)
	public List<DynamicNode> validatePDF()
	{
		List<DynamicNode> tests = new ArrayList<>();
		for (Map.Entry<String, JsonObject> entry : memberFaxRequestSuccessful.entrySet())
		{
			JsonObject request = entry.getValue();
			logger.info("Fax request:\n" + request.toString());

			String phoneNumber = request.get("phoneNumber").getAsString();

			FaxRequest theFaxRequest = faxRequests.stream()
											   .filter( faxRequest -> StringUtils.equalsIgnoreCase(faxRequest.getPhoneNumber(), phoneNumber))
											   .findFirst()
											   .orElse(null);

			if(theFaxRequest != null)
			{
				List<DynamicNode> pdfTests = new ArrayList<>();

				RetrievePdfFaxStep retrievePdfFaxStep = new RetrievePdfFaxStep(RxConciergeUILogin.getTokenAccess(), theFaxRequest.getPdfUri());
				
				retrievePdfFaxStep.run();
				
				pdfTests.addAll(retrievePdfFaxStep.getTestResults());
				
				if(retrievePdfFaxStep.stepStatus() == IStep.Status.COMPLETED)
				{				
					byte[] pdfAsByteArray = retrievePdfFaxStep.getPdfAsByteArray();
					
					GetPdfsPathStep getPdfsPathStep = new GetPdfsPathStep();
					
					getPdfsPathStep.run();
					
					pdfTests.addAll(getPdfsPathStep.getTestResults());
					
					if(getPdfsPathStep.stepStatus() == IStep.Status.COMPLETED)
					{
						Path pdfsDirectory = getPdfsPathStep.getPdfsPath();
						
						String pdfFileName = StringUtils.substringAfterLast(theFaxRequest.getPdfUri(), "/");
						
						WriteByteArrayToFileSystemStep writeByteArrayToFileSystemStep = new WriteByteArrayToFileSystemStep(pdfsDirectory, pdfFileName, pdfAsByteArray);
								
						writeByteArrayToFileSystemStep.run();
						
						pdfTests.addAll(writeByteArrayToFileSystemStep.getTestResults());
						
						if(writeByteArrayToFileSystemStep.stepStatus() == IStep.Status.COMPLETED)
						{						
							File pdfFile = writeByteArrayToFileSystemStep.getFile();
							
							ConvertPdfToStringUsingOcrStep convertPdfToStringUsingOcrStep = new ConvertPdfToStringUsingOcrStep(pdfFile);
							
							convertPdfToStringUsingOcrStep.run();

							pdfTests.addAll(convertPdfToStringUsingOcrStep.getTestResults());
							
							if(convertPdfToStringUsingOcrStep.stepStatus() == IStep.Status.COMPLETED)
							{
								String pdfContent = convertPdfToStringUsingOcrStep.getPdfContent();
								
								logger.info("Fax PDF content:\n" + pdfContent);
								
								ValidateFaxPdfContentStep validateFaxPdfContentStep = new ValidateFaxPdfContentStep(request, pdfContent);
								
								validateFaxPdfContentStep.run();
								
								pdfTests.addAll(validateFaxPdfContentStep.getTestResults());
							}
						}
					}
				}

				tests.add(dynamicContainer(entry.getKey(), pdfTests));
			}
			else
			{
				tests.add(dynamicTest(entry.getKey(),
						() -> fail("No entry created in the 'faxrequest' container with phone number " + request.get("phoneNumber").getAsString())));
			}
		}

		return tests;
	}
}
