/**
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.dto;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Get Outbound Fax
 * <a href="https://apim-lbs-rxc-dev-east-001.developer.azure-api.net/api-details#api=extrafax&operation=get-outbound-fax">get-outbound-fax</a>
 *
 * @author Garrett Cosmiano (gcosmian)
 * @since 12/30/2022
 */
public class OutboundFaxStatus
{
	private static final Logger logger = LoggerFactory.getLogger(OutboundFaxStatus.class);

	final String apiXmlResponse;
	Integer id;
	String jobStatus;
	String fax;
	String recipientStatus;
	Integer jobCompleted;
	String startTime;
	String endTime;
	Double transTime;
	String port;
	Integer pages;
	String billingCode;
	Double charge;
	Integer retryCount;
	String retryDetails;

	public OutboundFaxStatus(String apiXmlResponse)
	{
		this.apiXmlResponse = apiXmlResponse;

		if ( StringUtils.isBlank(apiXmlResponse)) {
			return;
		}

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(apiXmlResponse.getBytes()));
			XPath xPath =  XPathFactory.newInstance().newXPath();

			String temp = xPath.compile("//out_job/id").evaluate(document);
			this.id = StringUtils.isNotBlank(temp) ? Integer.parseInt(temp) : null;

			this.jobStatus = xPath.compile("//out_job/status").evaluate(document);

			temp = xPath.compile("//out_job/job_completed").evaluate(document);
			this.jobCompleted = StringUtils.isNotBlank(temp) ? Integer.parseInt(temp) : null;

			this.fax = xPath.compile("//recipient/address").evaluate(document);
			this.recipientStatus = xPath.compile("//recipient/status").evaluate(document);
			this.startTime = xPath.compile("//sent_log/start_time").evaluate(document);
			this.endTime = xPath.compile("//sent_log/end_time").evaluate(document);

			temp = xPath.compile("//sent_log/trans_time").evaluate(document);
			this.transTime = StringUtils.isNotBlank(temp) ? Double.parseDouble(temp) : null;

			this.port = xPath.compile("//sent_log/port").evaluate(document);

			temp = xPath.compile("//sent_log/pages").evaluate(document);
			this.pages = StringUtils.isNotBlank(temp) ? Integer.parseInt(temp) : null;

			this.billingCode = xPath.compile("//sent_log/billing_code").evaluate(document);

			temp = xPath.compile("//sent_log/charge").evaluate(document);
			this.charge = StringUtils.isNotBlank(temp) ? Double.parseDouble(temp) : null;

			temp = xPath.compile("//sent_log/retry_count").evaluate(document);
			this.retryCount = StringUtils.isNotBlank(temp) ? Integer.parseInt(temp) : null;

			this.retryDetails = xPath.compile("//sent_log/retry_details").evaluate(document);
		}
		catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException e)
		{
			logger.error("Unable to parse the API xml response", e);
		}
	}

	@Override
	public String toString()
	{
		return "OutboundFax{" +
					   "id=" + id +
					   ", jobStatus='" + jobStatus + '\'' +
					   ", fax='" + fax + '\'' +
					   ", recipientStatus='" + recipientStatus + '\'' +
					   ", jobCompleted=" + jobCompleted +
					   ", startTime='" + startTime + '\'' +
					   ", endTime='" + endTime + '\'' +
					   ", transTime=" + transTime +
					   ", port='" + port + '\'' +
					   ", pages=" + pages +
					   ", billingCode='" + billingCode + '\'' +
					   ", charge=" + charge +
					   ", retryCount=" + retryCount +
					   ", retryDetails='" + retryDetails + '\'' +
					   '}';
	}

	/*
	 * Setter / Getter
	 */

	public String getApiXmlResponse()
	{
		return apiXmlResponse;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getJobStatus()
	{
		return jobStatus;
	}

	public void setJobStatus(String jobStatus)
	{
		this.jobStatus = jobStatus;
	}

	public String getFax()
	{
		return fax;
	}

	public void setFax(String fax)
	{
		this.fax = fax;
	}

	public String getRecipientStatus()
	{
		return recipientStatus;
	}

	public void setRecipientStatus(String recipientStatus)
	{
		this.recipientStatus = recipientStatus;
	}

	public Integer getJobCompleted()
	{
		return jobCompleted;
	}

	public void setJobCompleted(Integer jobCompleted)
	{
		this.jobCompleted = jobCompleted;
	}

	public String getStartTime()
	{
		return startTime;
	}

	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}

	public String getEndTime()
	{
		return endTime;
	}

	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}

	public Double getTransTime()
	{
		return transTime;
	}

	public void setTransTime(Double transTime)
	{
		this.transTime = transTime;
	}

	public String getPort()
	{
		return port;
	}

	public void setPort(String port)
	{
		this.port = port;
	}

	public Integer getPages()
	{
		return pages;
	}

	public void setPages(Integer pages)
	{
		this.pages = pages;
	}

	public String getBillingCode()
	{
		return billingCode;
	}

	public void setBillingCode(String billingCode)
	{
		this.billingCode = billingCode;
	}

	public Double getCharge()
	{
		return charge;
	}

	public void setCharge(Double charge)
	{
		this.charge = charge;
	}

	public Integer getRetryCount()
	{
		return retryCount;
	}

	public void setRetryCount(Integer retryCount)
	{
		this.retryCount = retryCount;
	}

	public String getRetryDetails()
	{
		return retryDetails;
	}

	public void setRetryDetails(String retryDetails)
	{
		this.retryDetails = retryDetails;
	}
}
