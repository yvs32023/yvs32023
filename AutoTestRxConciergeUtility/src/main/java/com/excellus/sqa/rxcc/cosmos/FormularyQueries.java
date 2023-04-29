/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.cosmos;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.configuration.TestConfigurationException;
import com.excellus.sqa.rxcc.configuration.BeanNames;
import com.excellus.sqa.rxcc.dto.Formulary;
import com.excellus.sqa.rxcc.dto.GenericStringItem;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 06/20/2022
 */
public class FormularyQueries extends Queries 
{

    private static final Logger logger = LoggerFactory.getLogger(FormularyQueries.class);

    private volatile static Queue<Formulary> FORMULARY; // Cache the intervention, so we don't have to run multiple queries to Cosmos

    private static final String BEAN_NAME = "LBSContainerFormulary";


    /**
     * Query 100 FORMULARY
     * 
     * @return list of {@link FORMULARY}
     */
    @SuppressWarnings("unchecked")
    public synchronized static List<Formulary> getFormularies()
    {

        if (FORMULARY != null && FORMULARY.size() > 0)
            return (List<Formulary>) FORMULARY;

        List<Formulary> formulary = executeQuery(BeanNames.LBS_CONTAINER_FORMULARY, QUERY_ALL, Formulary.class, 100);
        FORMULARY = new LinkedList<>(formulary);

        return (List<Formulary>) FORMULARY;

    }

    /**
     * Query 100 FORMULARY and added extension of whereClause
     * 
     * 
     * @return list of {@link FORMULARY with added extension of whereClause}
     */
    @SuppressWarnings("unchecked") public synchronized static List<Formulary> getFormularies(String whereClause) {

        if ( FORMULARY != null && FORMULARY.size() > 0) 
            return (List<Formulary>) FORMULARY;

        if ( !StringUtils.startsWithIgnoreCase(whereClause, "where") )
            throw new TestConfigurationException("Invalid where clause, see https://docs.microsoft.com/en-us/azure/cosmos-db/sql/sql-query-where#arguments");

        String query = String.format(Queries.QUERY_ALL);

        whereClause = StringUtils.isNotBlank(whereClause) ?
                RegExUtils.replaceFirst(whereClause, "where", "") : "";

        query += StringUtils.isNotBlank(whereClause) ? " and " + whereClause.trim() : "";

        List<Formulary> formulary = executeQuery("LBSContainerFormulary", query, Formulary.class, 100); 
        FORMULARY = new LinkedList<>(formulary);

        return (List<Formulary>) FORMULARY;

    }

    /**
     * Retrieve a random formulary from LBS container
     * 
     * 
     * @return a random formulary from LBS container
     */       
    public synchronized static Formulary getRandomFormulary() {

        if (FORMULARY == null || FORMULARY.size() < 1)
            getFormularies();

        return FORMULARY.remove();
    }

    /**
     * Retrieve a random formulary from LBS container on the whereClause
     * 
     * 
     * @return a random formulary from LBS container on the whereClause
     */ 

    public synchronized static Formulary getRandomFormulary(String whereClause) {

        if (FORMULARY == null || FORMULARY.size() < 1)
            getFormularies(whereClause);

        return FORMULARY.remove();
    }

    /**
     * Retrieve the formulary
     * 
     * @param formularyId of the formulary
     * @return
     * 
     * @author msharma
     * @date 08/01/2022
     */
    public synchronized static List<Formulary> getFormulary(String formularyId)
    {
        // Build the query
        String query = String.format(Queries.QUERY_ALL) + 
                String.format(" where c.formularyId = \"%s\"", formularyId);


        logger.info("Retrieving formulary from Cosmos DB");
        String beanName = BeanNames.LBS_CONTAINER_FORMULARY;
        List<Formulary> formulary = executeQuery(beanName, query, Formulary.class, 100);

        formulary.sort(Comparator.comparing(Formulary::getCreatedDateTime).reversed());

        return formulary;
    }

    /**
     * Retrieve the formulary by specific id & formulary id
     * 
     * @author msharma
     * @since 08/07/22
     * 
     * @param formularyId of the member
     * @return
     */
    public synchronized static Formulary getFormulary(String Id, String formularyId)
    {
        // Build the query
        String query = String.format(Queries.QUERY_BY_TYPE, "formulary") + 
                String.format(" and c.id = \"%s\" and c.formularyId = \"%s\"", Id, formularyId);

        logger.info("Retrieving member note from Cosmos DB");
        String beanName = BeanNames.LBS_CONTAINER_FORMULARY;
        List<Formulary> formularies = executeQuery(beanName, query, Formulary.class, 1);

        if ( formularies != null && formularies.size() > 0 )
            return formularies.get(0);

        return null;
    }


    /**
     * Retrieve max formularyId from LBS container 
     * @return list of formularyId
     * 
     * @author msharma
     * @date 08/03/2022
     */
    public synchronized static List<String> getMaxFormularyId()
    {
        logger.info("Retrieving max formularyId from Cosmos DB");

        final String query = String.format("SELECT max(c.formularyId) as item FROM c where c.type = \"formulary\"");

        // Run the query
        List<GenericStringItem> items = executeQuery(BEAN_NAME, query, GenericStringItem.class, 100);

        // return list of string of the result
        return items.stream()
                .map(GenericStringItem::getItem)
                .collect(Collectors.toList());
    }


    /**
     * Insert formulary into formulary container
     * 
     * @param formularyId to be inserted
     * 
     * @author msharma 
     * @date 08/07/2022
     * 
     */
    public synchronized static void insertFormulary( Formulary formulary)
    {
        logger.info("Inserting member note into Cosmos DB with itemId {} partition key {}", formulary.getId(), formulary.getFormularyId());

        String beanName = "LBSContainerFormulary";

        insertItem(beanName, formulary.getId(), formulary.getFormularyId(), formulary);
    }

    /**
     * Delete formulary from container
     * @param formularyId to be deleted
     * 
     * @author msharma
     * @date 08/01/2022
     */
    public synchronized static void deleteFormulary(String id, String formularyId)
    {
        logger.info("Deleting formulary from Cosmos DB with itemId {} partition key {}", id, formularyId);

        String beanName = "LBSContainerFormulary";

        deleteItem(beanName, id, formularyId, Formulary.class);
    }

    /**
     * Perform formulary search
     * @ntagore 11/03/22
     * @param searchTerm searchTerm to search for
     * @return list of Formulary
     */
    public synchronized static List<Formulary> search(String searchTerm)
    {
        logger.debug("Perform formulary search");
        final String theQuery = "SELECT * FROM c WHERE c.type='formulary' and \n"        
                + "(RegexMatch(UPPER(c.formularyCode), \"(?<![\\\\w])%1$s\", \"m\") \n"
                + "   or RegexMatch(UPPER(c.formularyDescription), \"(?<![\\\\w])%1$s\", \"m\") )\n";
        String query = String.format(theQuery, searchTerm.toUpperCase());
        return executeQuery(BEAN_NAME, query, Formulary.class);
    }

}