/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.FaxTemplate;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 09/27/2022
 */
public class FaxTemplateQueries extends Queries
{
	private static final Logger logger = LoggerFactory.getLogger(FaxTemplateQueries.class);

	private volatile static List<FaxTemplate> FAXTEMPLATE;

	
	/**
	 * Retrieve list of faxtemplates 
	 * @return list of faxtemplates 
	 */
	public synchronized static List<FaxTemplate> getFaxTemplates()
	{
		if (FAXTEMPLATE != null && FAXTEMPLATE.size() > 0)
			return FAXTEMPLATE;
 
		FAXTEMPLATE = executeQuery(BeanNames.LBS_CONTAINER_FAX_TEMPLATE, String.format(Queries.QUERY_BY_TYPE, "faxtemplate"), FaxTemplate.class, 100);
				
		return FAXTEMPLATE;
	}
	
	/**
	 * Retrieve random faxtemplates
	 * @return {@link FaxTemplate}
	 */
	public synchronized static FaxTemplate getRandomFaxTemplate()
	{
		logger.info("Get random faxtemplate");
		return getRandomItem(getFaxTemplates());
	}

	/**
	 * REtrieve faxtemplates
	 * @param id of the fax template
	 * @return {@link FaxTemplate}
	 */
	public synchronized static FaxTemplate getFaxTemplateById(String id)
	{
		List<FaxTemplate> list = getFaxTemplates();

		return list.stream()
				.filter(fax -> StringUtils.equalsIgnoreCase(fax.getId(), id))
				.findFirst()
				.orElse(null);
	}
}
