/**
 *
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 *
 */
package com.excellus.sqa.rxcc.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DynamicNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.excellus.sqa.DTO.AbstractApiJsonDTO;
import com.excellus.sqa.jsonschemavalidator.SchemaValidator;
import com.excellus.sqa.jsonschemavalidator.ValidationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An abstract class that handles gathering of API information if available
 *
 * @author Garrett Cosmiano(gcosmian)
 * @param <JsonDTO> generic class that extends AbstractApiJsonDTO
 * @since 07/11/2022
 */
public abstract class AbstractJsonDTO<JsonDTO extends AbstractJsonDTO<?>> extends AbstractApiJsonDTO<JsonDTO>
{
	private static final Logger logger = LoggerFactory.getLogger(AbstractJsonDTO.class);

	final String SCHEMA_TEST_NAME = "Cosmos data validation against json schema (%s)";

	@Override
	public <T> List<T> mapSqlDataJsonArrayToDtoObject(Class<T> klazz, JSONArray ja) throws NotImplementedException {
		throw new NotImplementedException("This method is not implemented");
	}

	@Override
	public <T> T mapSqlJsonObjectToDtoObject(Class<T> klazz, JSONObject jo) throws NotImplementedException {
		throw new NotImplementedException("This method is not implemented");
	}

	/**
	 * Set the JSON schema file for DTO
	 * @param schemaClasspathFilename filename of the schema and it should be prefix with classpath; e.g.  classpath:member.note.schema.json
	 * @return {@link File} object of the JSON schema if it is found otherwise it returns null.
	 * @since 04/18/23
	 */
	protected File setJsonSchemaFile(String schemaClasspathFilename)
	{
		if ( StringUtils.isNotEmpty(schemaClasspathFilename) )
		{
			try
			{
				return ResourceUtils.getFile(schemaClasspathFilename);
			}
			catch (IOException e)
			{
				logger.error("Unable to find/read the schema", e);
			}
		}

		return null;
	}

	/**
	 * Validate this instance with json schema
	 * @param jsonSchemaFile schema file that will be used to validate this instance
	 * @return schema validation test
	 * @since 04/07/23
	 */
	public DynamicNode schemaValidation(File jsonSchemaFile)
	{
		if ( jsonSchemaFile == null )
		{
			return dynamicTest(StringUtils.substringBefore(SCHEMA_TEST_NAME," ("),
					() -> fail("No JSON schema provided for validation"));
		}

		final String schemaTestName = String.format(SCHEMA_TEST_NAME, jsonSchemaFile.getName());

		ObjectMapper objectMapper = new ObjectMapper();
		try
		{
			String jsonStr = objectMapper.writeValueAsString(this);

			ValidationResult validationResult = SchemaValidator.validateJsonVsSchema(jsonStr, jsonSchemaFile);
			if (validationResult.isPass()) {
				return dynamicTest(schemaTestName, () -> assertTrue(true));
			}
			else {
				return dynamicTest(schemaTestName, () -> fail("schema violations:\n" + validationResult.getDetails()));
			}
		}
		catch (JsonProcessingException e)
		{
			return dynamicTest(schemaTestName, () -> fail(e));
		}
	}
}