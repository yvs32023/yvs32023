/**
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.tests.pdf;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.restapi.RequestLoggingFilter;
import com.excellus.sqa.restapi.steps.ApiPostStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;
import com.excellus.sqa.rxcc.cosmos.TenantQueries;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.rxcc.steps.fax.ValidateFaxPdfContentStep;
import com.excellus.sqa.rxcc.steps.io.WriteByteArrayToFileSystemStep;
import com.excellus.sqa.rxcc.steps.pdf.ConvertPdfToStringUsingOcrStep;
import com.excellus.sqa.rxcc.steps.pdf.GeneratePopulatePdfFormRequestStep;
import com.excellus.sqa.rxcc.steps.pdf.GetPdfsPathStep;
import com.excellus.sqa.rxcc.steps.util.DecodeBase64EncodedStringStep;
import com.excellus.sqa.step.IStep;
import com.google.gson.JsonObject;

import io.restassured.http.Headers;

/** 
 * Generates PDF document for Preview Fax and Send Fax
 * 
 * POST https://apim-lbs-rxc-dev-east-001.azure-api.net/api/pdf/populate/form
 * 
 * @author Roland Burbulis (rburbuli)
 * @since 04/11/2023
 */
@Tag("ALL")
@Tag("PDF")
@DisplayName("PostPopulatePdfForm")
public class PostPopulatePdfFormTests extends RxConciergeAPITestBaseV2 
{
	private static final Logger logger = LoggerFactory.getLogger(PostPopulatePdfFormTests.class);

	@TestFactory
	@DisplayName("39631:  POST to ../api/pdf/populate/form API - no targetDrug, prerequisiteDrugs, qualityFreeFormText, or qualityTitleText")
	public List<DynamicNode> postPopulatePdfFormNoTargetDrugPrerequisiteDrugsQualityFreeFormTextOrQualityTitleTextTest()
	{
		return createTest(false, false, false, false);
	}
	
	@TestFactory
	@DisplayName("183538:  POST to ../api/pdf/populate/form API - with prerequisiteDrugs")
	public List<DynamicNode> postPopulatePdfFormWithPrerequisiteDrugsTest()
	{
		return createTest(false, true, false, false);
	}
	
	@TestFactory
	@DisplayName("183539:  POST to ../api/pdf/populate/form API - with qualityFreeFormText")
	public List<DynamicNode> postPopulatePdfFormWithQualityFreeFormTextTest()
	{
		return createTest(false, false, true, false);
	}
	
	@TestFactory
	@DisplayName("183540:  POST to ../api/pdf/populate/form API - with qualityTitleText")
	public List<DynamicNode> postPopulatePdfFormWithQualityTitleTextTest()
	{
		return createTest(false, false, false, true);
	}
	
	@TestFactory
	@DisplayName("183541:  POST to ../api/pdf/populate/form API - with prerequisiteDrugs and qualityTitleText")
	public List<DynamicNode> postPopulatePdfFormWithPrerequisiteDrugsAndQualityTitleTextTest()
	{
		return createTest(false, true, false, true);
	}
	
	@TestFactory
	@DisplayName("183542:  POST to ../api/pdf/populate/form API - with prerequisiteDrugs, qualityFreeFormText, and qualityTitleText")
	public List<DynamicNode> postPopulatePdfFormWithPrerequisiteDrugsQualityFreeFormTextAndQualityTitleTextTest()
	{
		return createTest(false, true, true, true);
	}
	
	@TestFactory
	@DisplayName("183543:  POST to ../api/pdf/populate/form API - with targetDrug, prerequisiteDrugs, qualityFreeFormText, and qualityTitleText")
	public List<DynamicNode> postPopulatePdfFormWithTargetDrugPrerequisiteDrugsQualityFreeFormTextAndQualityTitleTextTest()
	{
		return createTest(true, true, true, true);
	}
	
	@TestFactory
	@DisplayName("183547:  POST to ../api/pdf/populate/form API - with prerequisiteDrugs and qualityFreeFormText")
	public List<DynamicNode> postPopulatePdfFormWithPrerequisiteDrugsAndQualityFreeFormTextTest()
	{
		return createTest(false, true, true, false);
	}
	
	public List<DynamicNode> createTest(
			boolean includeTargetDrug,
			boolean includePrerequisiteDrugs,
			boolean includeQualityFreeFormText,
			boolean includeQualityTitleText)
	{
		// create a new list to store dynamic nodes
		List<DynamicNode> test = new ArrayList<>();

		// get list of tenants from cosmos
		List<Tenant> tenants = TenantQueries.getTenants();

		String tenantSubscriptionName = null;
		
		// iterate through the tenants
		for(Tenant tenant : tenants) 
		{
			try 
			{
				// get subscription name
				tenantSubscriptionName = tenant.getSubscriptionName();
				
				GeneratePopulatePdfFormRequestStep generatePopulatePdfFormRequestStep = new GeneratePopulatePdfFormRequestStep(
						tenantSubscriptionName,
						includeTargetDrug,
						includePrerequisiteDrugs,
						includeQualityFreeFormText,
						includeQualityTitleText);
				
				generatePopulatePdfFormRequestStep.run();
				
				JsonObject requestBody = generatePopulatePdfFormRequestStep.getRequestBody();

				test.add(dynamicContainer("Tenant Subscription Name:  " + tenantSubscriptionName.toUpperCase(), doTest(requestBody, tenantSubscriptionName)));
			}
			catch(Exception e)
			{
				String apiInfo = RequestLoggingFilter.getApiInfo();
				
				test.add(dynamicTest("Tenant Subscription Name:  " + tenantSubscriptionName.toUpperCase(), () -> fail(apiInfo + "\n" + e.getMessage(), e)));
			}
			
			// Reset the API information and test validation results
			resetApiInfo();
		}
		
		return test;
	}
	
	/**
	 * Generic test for POST to ../api/pdf/populate/form API.  The response from the API is decoded and saved
	 * as PDF file.  The contents of the PDF file are then extracted using OCR and validated against the
	 * request body.
	 * 
	 * @param requestBody - request body for the POST to ../api/pdf/populate/form API
	 * 
	 * @return test validation result
	 */
	private List<DynamicNode> doTest(JsonObject requestBody, String tenantSubscriptionName)
	{
		List<DynamicNode> test = new ArrayList<DynamicNode>();

		ApiPostStep apiPostStep = doPost(requestBody);
		
		if(apiPostStep.stepStatus() != IStep.Status.COMPLETED)
		{
			return apiPostStep.getTestResults();
		}
		else {
			test.addAll(apiPostStep.getTestResults());
			
			String pdfAsBase64EncodedString = apiPostStep.getResponse().asString();
			
			DecodeBase64EncodedStringStep decodeBase64EncodedStringStep = new DecodeBase64EncodedStringStep(pdfAsBase64EncodedString);
			
			decodeBase64EncodedStringStep.run();
			
			if(decodeBase64EncodedStringStep.stepStatus() != IStep.Status.COMPLETED)
			{
				return decodeBase64EncodedStringStep.getTestResults();
			}
			else
			{
				test.addAll(decodeBase64EncodedStringStep.getTestResults());
				
				byte[] pdfAsByteArray = decodeBase64EncodedStringStep.getDecodedBytes();
				
				GetPdfsPathStep getPdfsPathStep = new GetPdfsPathStep();
				
				getPdfsPathStep.run();
				
				if(getPdfsPathStep.stepStatus() != IStep.Status.COMPLETED)
				{
					return getPdfsPathStep.getTestResults();
				}
				else
				{
					test.addAll(getPdfsPathStep.getTestResults());
					
					Path pdfsDirectory = getPdfsPathStep.getPdfsPath();
					
					String memberId = requestBody.get("memberId").getAsString();
					String interventionId = requestBody.get("interventionId").getAsString();
					
					String pdfFileName = memberId + "_" + interventionId + ".pdf";
					
					WriteByteArrayToFileSystemStep writeByteArrayToFileSystemStep = new WriteByteArrayToFileSystemStep(pdfsDirectory, pdfFileName, pdfAsByteArray);
							
					writeByteArrayToFileSystemStep.run();
					
					if(writeByteArrayToFileSystemStep.stepStatus() != IStep.Status.COMPLETED)
					{
						return writeByteArrayToFileSystemStep.getTestResults();
					}
					else
					{
						test.addAll(writeByteArrayToFileSystemStep.getTestResults());
						
						File pdfFile = writeByteArrayToFileSystemStep.getFile();
						
						ConvertPdfToStringUsingOcrStep convertPdfToStringUsingOcrStep = new ConvertPdfToStringUsingOcrStep(pdfFile);
						
						convertPdfToStringUsingOcrStep.run();
						
						if(convertPdfToStringUsingOcrStep.stepStatus() != IStep.Status.COMPLETED)
						{
							return convertPdfToStringUsingOcrStep.getTestResults();
						}
						else
						{
							test.addAll(convertPdfToStringUsingOcrStep.getTestResults());
							
							String pdfContent = convertPdfToStringUsingOcrStep.getPdfContent();
							
							ValidateFaxPdfContentStep validateFaxPdfContentStep = new ValidateFaxPdfContentStep(requestBody, pdfContent);
							
							validateFaxPdfContentStep.run();
							
							if(validateFaxPdfContentStep.stepStatus() != IStep.Status.COMPLETED)
							{
								return validateFaxPdfContentStep.getTestResults();
							}
							else
							{
								test.addAll(validateFaxPdfContentStep.getTestResults());
							}
						}
					}
				}
			}
		}

		return test;
	}
	
	/**
	 * This method makes a POST to ../api/pdf/populate/form API and validates that the response status code is 200
	 * and that the response status line is HTTP/1.1 200 OK.
	 * 
	 * @param requestBody - the request body for a POST to ../api/pdf/populate/form API
	 * 
	 * @return ApiPostStep object representing the POST to ../api/pdf/populate/form API
	 */
	private ApiPostStep doPost(JsonObject requestBody)
	{
		logger.info("Starting POST to ../api/pdf/populate/form API");

		Headers headers = getGenericHeaders();

		ApiPostStep apiPostStep = new ApiPostStep(headers, POPULATE_PDF_FORM_POST_ENDPOINT, requestBody.toString(), new Object[]{}, 200, HTTP_200_OK);
		apiPostStep.run();

		logger.debug("POST to ../api/pdf/populate/form API completed successfully");
		
		return apiPostStep;
	}
}