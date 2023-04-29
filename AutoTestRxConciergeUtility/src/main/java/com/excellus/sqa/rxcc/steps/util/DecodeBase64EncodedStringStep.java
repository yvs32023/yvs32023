/**
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.steps.util;

import static com.excellus.sqa.step.IStep.Status.COMPLETED;
import static com.excellus.sqa.step.IStep.Status.IN_PROGRESS;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.step.AbstractStep;

/** 
 * Decodes a base 64 encoded string
 * 
 * @author Roland Burbulis (rburbuli)
 * @since 04/19/2023
 */
public class DecodeBase64EncodedStringStep extends AbstractStep
{
	private static final Logger logger = LoggerFactory.getLogger(DecodeBase64EncodedStringStep.class);

	private final static String STEP_NAME = DecodeBase64EncodedStringStep.class.getSimpleName();
	private final static String STEP_DESC = "Decode a base 64 encoded string";

	private String base64EncodedString;
	
	private byte[] decodedBytes;

	public DecodeBase64EncodedStringStep(String base64EncodedString)
	{
		super(STEP_NAME, STEP_DESC);
		
		this.base64EncodedString = base64EncodedString;
	}

	@Override
	public void run()
	{
		super.stepStatus = IN_PROGRESS;
		
		try
		{		
			decodedBytes = Base64.decodeBase64(base64EncodedString);

			super.stepStatus = COMPLETED;
		}
		catch (Exception e)
		{
			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}

		logger.info(print());
	}

	public byte[] getDecodedBytes()
	{
		return decodedBytes;
	}
}