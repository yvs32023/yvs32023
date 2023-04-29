/**
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.steps.pdf;

import static com.excellus.sqa.step.IStep.Status.COMPLETED;
import static com.excellus.sqa.step.IStep.Status.IN_PROGRESS;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.step.AbstractStep;

/** 
 * Gets the path where the PDFs are stored
 * 
 * @author Roland Burbulis (rburbuli)
 * @since 04/19/2023
 */
public class GetPdfsPathStep extends AbstractStep
{
	private static final Logger logger = LoggerFactory.getLogger(GetPdfsPathStep.class);

	private final static String STEP_NAME = GetPdfsPathStep.class.getSimpleName();
	private final static String STEP_DESC = "Get the path where the PDFs are stored";

	private Path pdfsPath;

	public GetPdfsPathStep()
	{
		super(STEP_NAME, STEP_DESC);
	}

	@Override
	public void run()
	{
		super.stepStatus = IN_PROGRESS;
		
		try
		{		
			String customPdfDir = (System.getProperty("reportDir") != null) ? System.getProperty("pdfPath") : System.getenv("pdfPath");
			
			customPdfDir = StringUtils.isNotBlank(customPdfDir) ? customPdfDir : "pdfFaxes";
			
			pdfsPath = Paths.get("target", customPdfDir);

			super.stepStatus = COMPLETED;
		}
		catch (Exception e)
		{
			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}

		logger.info(print());
	}

	public Path getPdfsPath()
	{
		return pdfsPath;
	}
}