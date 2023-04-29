/**
 * @copyright 2023 Excellus BCBS
 * All rights reserved.
 */
package com.excellus.sqa.rxcc.steps.io;

import static com.excellus.sqa.step.IStep.Status.COMPLETED;
import static com.excellus.sqa.step.IStep.Status.IN_PROGRESS;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.step.AbstractStep;

/** 
 * Writes byte array to the file system
 * 
 * @author Roland Burbulis (rburbuli)
 * @since 04/19/2023
 */
public class WriteByteArrayToFileSystemStep extends AbstractStep
{
	private static final Logger logger = LoggerFactory.getLogger(WriteByteArrayToFileSystemStep.class);

	private final static String STEP_NAME = WriteByteArrayToFileSystemStep.class.getSimpleName();
	private final static String STEP_DESC = "Write byte array to the file system";

	private Path directory;
	private final String fileName;
	private final byte[] fileAsByteArray;

	private File file;

	public WriteByteArrayToFileSystemStep(Path directory, String fileName, byte[] fileAsByteArray)
	{
		super(STEP_NAME, STEP_DESC);
		
		this.directory = directory;
		this.fileName = fileName;
		this.fileAsByteArray = fileAsByteArray;
	}

	@Override
	public void run()
	{
		super.stepStatus = IN_PROGRESS;
		
		try
		{		
			if(!Files.exists(directory)) {
				Files.createDirectories(directory);
			}

			Path directoryAndFileName = directory.resolve(fileName);
			
			file = directoryAndFileName.toFile();
			
			FileOutputStream fos = new FileOutputStream(file);
			
			fos.write(fileAsByteArray);
			
			fos.close();

			super.stepStatus = COMPLETED;
		}
		catch (Exception e)
		{
			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}

		logger.info(print());
	}

	public File getFile()
	{
		return file;
	}
}