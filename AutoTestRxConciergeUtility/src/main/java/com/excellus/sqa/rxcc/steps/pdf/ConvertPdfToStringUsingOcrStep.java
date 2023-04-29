/**
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.steps.pdf;

import static com.excellus.sqa.step.IStep.Status.COMPLETED;
import static com.excellus.sqa.step.IStep.Status.IN_PROGRESS;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.excellus.sqa.step.AbstractStep;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

/** 
 * Converts a PDF file to a string using optical character recognition
 * 
 * @author Roland Burbulis (rburbuli)
 * @since 04/19/2023
 */
public class ConvertPdfToStringUsingOcrStep extends AbstractStep
{
	private static final Logger logger = LoggerFactory.getLogger(ConvertPdfToStringUsingOcrStep.class);

	private final static String STEP_NAME = ConvertPdfToStringUsingOcrStep.class.getSimpleName();
	private final static String STEP_DESC = "Convert a PDF file to a string using optical character recognition";

	private File pdfFile;
	
	private String pdfContent;

	public ConvertPdfToStringUsingOcrStep(File pdfFile)
	{
		super(STEP_NAME, STEP_DESC);
		
		this.pdfFile = pdfFile;
	}

	@Override
	public void run()
	{
		super.stepStatus = IN_PROGRESS;
		
		try
		{		
			if(pdfFile != null)
			{
				PDDocument pdfDocument = PDDocument.load(pdfFile);
				
				PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);

				File tessDataDir = ResourceUtils.getFile("classpath:tessdata");

				ITesseract tesseract = new Tesseract();
				
				tesseract.setDatapath(tessDataDir.getAbsolutePath());
				tesseract.setLanguage("eng");
				
				StringBuilder out = new StringBuilder();

				for(int page = 0; page < pdfDocument.getNumberOfPages(); page++) {
					BufferedImage pdfPageImage = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

					// Create a temp image file
					File tempFile = File.createTempFile("tempfile_" + page, ".png");
					
					ImageIO.write(pdfPageImage, "png", tempFile);

					String pageText = tesseract.doOCR(tempFile);
					
					pageText = StringUtils.normalizeSpace(pageText);
					
					out.append(pageText);

					// Delete temp file
					tempFile.delete();
				}

				pdfContent = out.toString();
				
				logger.debug(pdfContent);
			}

			super.stepStatus = COMPLETED;
		}
		catch (Exception e)
		{
			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}

		logger.info(print());
	}

	public String getPdfContent()
	{
		return pdfContent;
	}
}