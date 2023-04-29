/**
 * 
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.tests.extrafax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.excellus.sqa.restapi.steps.ApiGetStep;
import com.excellus.sqa.rxcc.configuration.RxConciergeAPITestBaseV2;

/**
 * Server Statuses
 * APIM Proxy to ExtraFax APIs used exclusively by Fax API
 *  
 * 
 * GET https://apim-lbs-rxc-dev-east-001.azure-api.net/api/extrafax/server_statuses
 *  
 *  
 * @author Manish Sharma (msharma)
 * @since 01/03/2023
 */
@Tag("ALL")
@Tag("EXTRAFAX")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("GetServerStatuses")
public class GetServerStatusesTest extends RxConciergeAPITestBaseV2
{
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(GetServerStatusesTest.class);

	@TestFactory
	@DisplayName("153030: GetServerStatuses Happy Path")
	@Order(1)
	public List<DynamicNode> happyPath() throws ParserConfigurationException, SAXException, IOException 
	{

		// API call
		ApiGetStep apiGetStep = new ApiGetStep(getGenericHeaders(), SERVER_STATUSES_GET_ENDPOINT, new Object[] {}, 200, null);
		apiGetStep.run();

		//Get the response body as a String.
		String xmlString = apiGetStep.getResponse().then().extract().asString();

		//Creating DocumentBuilder objects which will parse the XML String.
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		//Parsing the XML String.
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new StringReader(xmlString)));

		//Get the list of elements in the XML document.
		NodeList nodeList = document.getElementsByTagName("is_available");

		apiGetStep.getTestResults().add(dynamicTest("Server Status out_ports is_available validation",
				() -> assertEquals("1", nodeList.item(0).getTextContent())));


		return apiGetStep.getTestResults();
	}
}
