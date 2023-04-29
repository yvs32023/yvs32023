/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excellus.sqa.rxcc.configuration.RxConciergeCosmoConfig;
import com.excellus.sqa.utilities.Utilities;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * 
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 03/14/2022
 */
public class Utility extends Utilities
{
    private static final Logger logger = LoggerFactory.getLogger(Utility.class);

    /**
     * Converts Cosmos date into UI date
     * 
     * @param cosmosDate taken from Cosmos
     * @param format (optional) default format is "MM/dd/yyyy" if not provided
     * @return date base on EDT/EST time zone which what the UI is based on
     */
    public static String convertCosmosDateToUI(String cosmosDate, String format) 
    {
        if ( StringUtils.isBlank(cosmosDate)) {
            return cosmosDate;
        }
        if ( StringUtils.isBlank(format)) {
            format = "MM/dd/yyyy";  // Default format for UI
        }
        try
        {
            logger.debug("Cosmos date: " + cosmosDate);
            DateFormat gmt = new SimpleDateFormat(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT.replaceAll("\\.S*", ""));
            gmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = gmt.parse(cosmosDate);  
            DateFormat est = new SimpleDateFormat(format);
            return est.format(date);
        } 
        catch (Exception e) 
        {
            try
            {
                DateFormat gmt = new SimpleDateFormat(RxConciergeCosmoConfig.COSMOS_DATE_FORMAT);
                gmt.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date date = gmt.parse(cosmosDate);  
                DateFormat est = new SimpleDateFormat(format);
                return est.format(date);
            }
            catch (Exception e1)
            {
                logger.error("An unexpected error is caught while generating timestampe", e);
                return cosmosDate;
            }
        }
    }

    /**
     * Normalize the phone/fax to US format
     * @param phoneNumber or fax number to normalize
     * @return normalized phone/fax number base on US format
     * @since 11/04/22
     * @author gcosmian
     */
    public static String normalizePhoneNumber(String phoneNumber) {
        if ( StringUtils.isBlank(phoneNumber) )
            return phoneNumber;

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try
        {
            Phonenumber.PhoneNumber thePhoneNumber = phoneUtil.parse(phoneNumber, "US");
            return phoneUtil.format(thePhoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        }
        catch (NumberParseException e)
        {
            return phoneNumber;
        }
    }

    /**
     * Normalize the phone/fax base on the format provided
     * @param phoneNumber to be formatted
     * @param format {@link PhoneNumberUtil.PhoneNumberFormat}
     * @return formatted phone number
     * @since 01/04/23
     */
    public static String normalizePhoneNumber(String phoneNumber, PhoneNumberUtil.PhoneNumberFormat format) {
        if ( StringUtils.isBlank(phoneNumber) )
            return phoneNumber;

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try
        {
            Phonenumber.PhoneNumber thePhoneNumber = phoneUtil.parse(phoneNumber, "US");
            return phoneUtil.format(thePhoneNumber, format);
        }
        catch (NumberParseException e)
        {
            return phoneNumber;
        }
    }

    /**
     * Method to pick the first date of the Month with correspondence type
     * @return current month start from 1st  in the format "YYYY-mm-dd"
     * @since 12/23/22
     * @author neeru tagore
     */
    public static String getFirstOfMonth() {

        LocalDate todaydate = LocalDate.now();
        return todaydate.withDayOfMonth(1).toString(); 

    }

    /**
     * Method to pick the first date of the last Month with correspondence type
     * @return previous month start from 1st  in the format "YYYY-mm-dd"
     * @since 12/23/22
     * @author neeru tagore
     */
    public static String getFirstOfLastMonth() {

        LocalDate todaydate = LocalDate.now().minusMonths(1);
        return todaydate.withDayOfMonth(1).toString(); 

    }
}