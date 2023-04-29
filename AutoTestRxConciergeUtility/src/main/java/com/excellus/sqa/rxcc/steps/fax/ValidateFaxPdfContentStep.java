/**
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.steps.fax;

import static com.excellus.sqa.step.IStep.Status.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.step.AbstractStep;
import com.google.gson.Gson;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * Validate the PDF fax content
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 01/03/2023
 */
public class ValidateFaxPdfContentStep extends AbstractStep
{
	private static final Logger logger = LoggerFactory.getLogger(ValidateFaxPdfContentStep.class);

	private final static String STEP_NAME = ValidateFaxPdfContentStep.class.getSimpleName();
	private final static String STEP_DESC = "Validate the PDF fax content";

	private final JsonObject jsonFaxRequestBody;
	private final String pdfContent;


	/**
	 * Constructor
	 * @param jsonFaxRequestBody {@link JsonObject}
	 */
	public ValidateFaxPdfContentStep(JsonObject jsonFaxRequestBody, String pdfContent)
	{
		super(STEP_NAME, STEP_DESC);
		this.jsonFaxRequestBody = jsonFaxRequestBody;
		this.pdfContent = pdfContent;
	}

	@Override
	public void run()
	{
		super.stepStatus = IN_PROGRESS;

		try
		{
			String pdfLogoAddressLine1 = retrieveStringFromJson("tenantLine1");
			validateField("Tenant address line 1", pdfLogoAddressLine1, -1);

			String pdfLogoAddressLine2 = retrieveStringFromJson("tenantLine2");
			validateAddress("Tenant address line 2", pdfLogoAddressLine2);

			String pdfLogoAddressLine3 = retrieveStringFromJson("tenantLine3");
			validateAddress("Tenant address line 3", pdfLogoAddressLine3);
			
			List<String> prerequisiteDrugs = retrieveStringListFromJson("prerequisiteDrugs");
			
			if(prerequisiteDrugs != null)
			{
				IntStream.range(0, prerequisiteDrugs.size())
					.forEach(prerequisiteDrugIndex -> {
						validateField("Pre-Requisite Drug " + prerequisiteDrugIndex, prerequisiteDrugs.get(prerequisiteDrugIndex), -1);
					});
			}
			
			String qualityFreeFormText = retrieveStringFromJson("qualityFreeFormText");
			
			if(StringUtils.isNotBlank(qualityFreeFormText))
			{
				validateField("Quality Free Form Text", qualityFreeFormText, -1);
			}
			
			String qualityTitleText = retrieveStringFromJson("qualityTitleText");
			
			if(StringUtils.isNotBlank(qualityTitleText))
			{
				validateField("Quality Title Text", qualityTitleText, -1);
			}
			
			String date = retrieveStringFromJson("date");
			
			if(StringUtils.isNotBlank(date))
			{
				validateField("Date", date, -1);
			}

			String providerLine1 = retrieveStringFromJson("providerLine1");
			validateField("Provider line 1", providerLine1, -1);

			String providerLine2 = retrieveStringFromJson("providerLine2");
			validateAddress("Provider line 2", providerLine2);

			String providerLine3 = retrieveStringFromJson("providerLine3");
			if (StringUtils.isNotBlank(providerLine3))
			{
				validateAddress("Provider line 3", providerLine3);
			}

			String providerLine4 =  retrieveStringFromJson("providerLine4");
			if (StringUtils.isNotBlank(providerLine4))
			{
				validateAddress("Provider line 4", providerLine4);
			}

			String memberLine1 = retrieveStringFromJson("memberLine1");
			validateField("Member line 1", memberLine1, -1);

			String memberLine2 = retrieveStringFromJson("memberLine2");
			validateField("Member line 2", memberLine2, -1);

			String memberLine3 = retrieveStringFromJson("memberLine3");
			validateAddress("Member line 3", memberLine3);

			String memberLine4 = retrieveStringFromJson("memberLine4");
			if ( StringUtils.isNotBlank(memberLine4) )
			{
				validateAddress("Member line 4", memberLine4);
			}

			String memberId = retrieveStringFromJson("memberId");
			validateMemberId(memberId);

			String acceptText = retrieveStringFromJson("acceptText");
			validateField("Accept Text", acceptText, 110);

			String declineText = retrieveStringFromJson("declineText");
			validateField("Decline Text", declineText, 110);

			String targetDrug = retrieveStringFromJson("targetDrug");
			
			if(StringUtils.isNotBlank(targetDrug))
			{
				validateField("Target Drug", targetDrug, 50);
			}

			String alternativeText = retrieveStringFromJson("alternativeText");
			validateField("Alternative Drug", alternativeText, 50);

			String ecrBlock1 = retrieveStringFromJson("ecrBlock1");
			if ( StringUtils.isNotBlank(ecrBlock1))
			{
				validateField("ecrBlock1", ecrBlock1, -1);
			}

			String ecrBlock2 = retrieveStringFromJson("ecrBlock2");
			if ( StringUtils.isNotBlank(ecrBlock2))
			{
				validateField("ecrBlock2", ecrBlock2, -1);
			}

			String ecrBlock3 = retrieveStringFromJson("ecrBlock3");
			if ( StringUtils.isNotBlank(ecrBlock3))
			{
				validateField("ecrBlock3", ecrBlock3, -1);
			}

			String ecrClosure = retrieveStringFromJson("ecrClosure");
			if ( StringUtils.isNotBlank(ecrClosure))
			{
				validateField("ecrClosure", ecrClosure, -1);
			}

			String ecrFooter = retrieveStringFromJson("ecrFooter");
			if ( StringUtils.isNotBlank(ecrFooter))
			{
				validateField("ecrFooter", ecrFooter, -1);
			}

			String averageCostSavings = retrieveStringFromJson("averageCostSavings");
			if ( StringUtils.isNotBlank(averageCostSavings))
			{
				validateField("averageCostSavings", averageCostSavings, -1);
			}
		}
		catch (Exception e)
		{
			stepTestResults.add(dynamicTest(stepName, () -> fail(e)));
		}

		super.stepStatus = Status.COMPLETED;
		logger.info(print());
	}

	/*
	 * Helper methods
	 */

	/**
	 * Retrieve the value of the json field
	 * @param field to be extracted from Json object
	 * @return value of the field. Returns NULL if the field is not present.
	 */
	private String retrieveStringFromJson(String field)
	{
		try
		{
			return jsonFaxRequestBody.get(field).getAsString();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * Retrieve a list of strings for the specified Json array field from the Json fax request body.
	 * 
	 * @param field - Json array field to be retrieved as list of strings from the Json fax request body
	 * @return list of strings for the specified Json array field within the Json fax request body or
	 * 		   null if the field is not present in the Json fax request body
	 */
	private List<String> retrieveStringListFromJson(String field)
	{
		try
		{		
            return new Gson().fromJson(jsonFaxRequestBody.getAsJsonArray(field), new TypeToken<List<String>>(){}.getType());
		}
		catch(Exception e)
		{
			return null;
		}
	}

	/**
	 * The OCR reader does not read properly the member id, i.e. replacing underscore (_) with a space.
	 * Thus, this will handle the validation by spliting the member id with underscore.
	 * @param memberId to be validated
	 */
	private void validateMemberId(String memberId)
	{
		String tenantId = StringUtils.substringBefore(memberId, "_");
		int start = pdfContent.indexOf(tenantId);
		String temp = StringUtils.substring(pdfContent, start, start + 100);

		boolean found = true;
		for ( String str : StringUtils.split(memberId, "_") )
		{
			if ( StringUtils.isNotBlank(str) && !StringUtils.contains(temp, str) )
			{
				found = false;
				break;
			}
		}

		if ( found )  {
			stepTestResults.add(dynamicTest("Member id [" + memberId + "]", () -> assertTrue(true)));
		}
		else {
			stepTestResults.add(dynamicTest("Member id [" + memberId + "]", () -> fail("Not found in PDF")));
		}
	}

	/**
	 * The OCR reader does not read well the address line.
	 * This method will handle the different options OCR might read the address line.
	 *
	 * @param testName to be displayed in the test report
	 * @param address to be validated
	 */
	private void validateAddress(String testName, String address)
	{
		if ( StringUtils.contains(pdfContent, address) )
		{
			stepTestResults.add(dynamicTest(testName + " [" + address + "]", () -> assertTrue(true)));
			return;
		}

		if ( StringUtils.isNotBlank(address) )
		{
			String opt2 = StringUtils.normalizeSpace(address.replaceAll(",", ", "));	// normalize space after comma
			String opt3 = opt2.replaceAll(", ", ",");	// remove all spaces after comma

			if ( !StringUtils.contains(pdfContent, opt2) && !StringUtils.contains(pdfContent, opt3) ) {
				stepTestResults.add(dynamicTest(testName + " [" + address + "]", () -> fail("Not found in PDF")));
			}
			else {
				stepTestResults.add(dynamicTest(testName + " [" + address + "]", () -> assertTrue(true)));
			}
		}
	}

	/**
	 * Validate the data is found in the PDF content
	 * @param testName to be displayed in the test report
	 * @param expected to be validated
	 * @param maxLength if data need to be split into maximum length prior validation.
	 *                  This primarily used for data that are in the table which gets split with
	 *                  line break because the column limit/size.
	 *                  If all data is to be validated without splitting it then set it to -1.
	 */
	private void validateField(String testName, String expected, int maxLength)
	{
		String[] expectedData = StringUtils.split(expected, "\n");

		boolean found = true;
		for ( String data : expectedData )
		{
			for ( String s : splitByLimit(data, maxLength) )
			{
				if ( StringUtils.isNotBlank(s) && !StringUtils.contains(pdfContent, s) )
				{
					found = false;
					break;
				}
			}
		}

		if ( found )  {
			stepTestResults.add(dynamicTest(testName + " [" + expected + "]", () -> assertTrue(true)));
		}
		else {
			stepTestResults.add(dynamicTest(testName + " [" + expected + "]", () -> fail("Not found in PDF")));
		}
	}

	/**
	 * Split the string base on the length limit.
	 * This is primarily used for data within table which are split with line break.
	 * @param data to split
	 * @param maxLength of the string length
	 * @return an array of string
	 */
	private String[] splitByLimit(String data, int maxLength)
	{
		if ( maxLength < 1 )
		{
			return new String[]{data};
		}

		List<String> result = new ArrayList<>();

		String[] strs = StringUtils.split(data," ");

		StringBuffer sb = new StringBuffer();
		for ( String str : strs )
		{
			// initial entry
			if ( sb.toString().length() < 1 ) {
				sb.append(str);
				continue;
			}

			String temp = sb + " " + str;
			if (  temp.length() <= maxLength )
			{
				sb.append(" " + str);
			}
			else
			{
				result.add(sb.toString());

				sb = new StringBuffer();
				sb.append(str);
			}
		}

		result.add(sb.toString());
		return result.toArray(new String[0]);
	}

}
