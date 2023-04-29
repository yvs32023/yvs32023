/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DynamicNode;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 08/23/2022
 */
@JsonPropertyOrder({"containerName","fileName"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoragePath extends AbstractJsonDTO<StoragePath>
{

	private String containerName;

	private String fileName;	

	/**
	 * Default constructor
	 */
	public StoragePath() {
	}

	/**
	 * Constructor
	 * @param containerName
	 * @param fileName
	 */
	public StoragePath(String containerName,String fileName) 
	{
		this.containerName = containerName;
		this.fileName = fileName;
	}


	/*
	 * Validations
	 */

	/**
	 * Compare two objects
	 * 
	 * @param storagePath
	 * @return
	 */
	public List<DynamicNode> compare(StoragePath storagePath) 
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("containerName: [" + containerName + "]", () -> assertEquals(containerName, storagePath.getContainerName(), getApiInfo(storagePath))));
		tests.add(dynamicTest("fileName: [" + fileName + "]", () -> assertEquals(fileName, storagePath.getFileName(), getApiInfo(storagePath))));

		return tests;
	}

	/*
	 * Helper methods
	 */

	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof StoragePath)
		{
			StoragePath storagePath = (StoragePath) obj;

			if ( fileName.equals(storagePath.getFileName()) )
				return true;
		}

		return false;
	}

	/*
	 * Setter / Getter
	 */

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
