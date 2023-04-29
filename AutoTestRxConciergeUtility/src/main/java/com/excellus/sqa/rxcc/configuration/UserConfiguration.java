/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.configuration;

import java.util.ArrayList;
import java.util.List;

import com.excellus.sqa.configuration.IUserConfiguration;

/**
 * Rx CC account
 * 
 * @author Garrett Cosmiano (gcosmian)
 * @since 01/11/2022
 */
public enum UserConfiguration implements IUserConfiguration 
{

	// Uses SSO which does not require user id, username or password. The values here are placeholder only.
	SSO
		(System.getProperty("user.name"),
			System.getProperty("user.name") + "@excellus.com",
			System.getProperty("user.name")),	
	/*
	Multiple tenant level access
	 */

	RXCC_FULL_MULTI
		("RXCC_FULL_MULTI",
			"rxcc_full_multi@excellus.com",
			"rxcc_full_multi"),

	RXCC_READ_MULTI
			("RXCC_READ_MULTI",
					"rxcc_read_multi@excellus.com",
					"rxcc_read_multi"),

	RXCC_OPS_MULTI
			("RXCC_OPS_MULTI",
					"rxcc_ops_multi@excellus.com",
					"rxcc_ops_multi"),

	RXCC_REPORT_MULTI
			("RXCC_REPORT_MULTI",
					"rxcc_report_multi@excellus.com",
					"rxcc_report_multi"),

	/*
	EHP tenant level access
	 */

	RXCC_FULL_SINGLE
			("RXCC_FULL_SINGLE",
					"rxcc_full_single@excellus.com",
					"rxcc_full_single"),

	RXCC_READ_SINGLE
			("RXCC_READ_SINGLE",
					"rxcc_read_single@excellus.com",
					"rxcc_read_single"),

	RXCC_OPS_SINGLE
			("RXCC_OPS_SINGLE",
					"rxcc_ops_single@excellus.com",
					"rxcc_ops_single"),

	RXCC_REPORT_SINGLE
			("RXCC_REPORT_SINGLE",
					"rxcc_report_single@excellus.com",
					"rxcc_report_single"),

	/*
	LOA tenant level access
	 */

	RXCC_FULL_LOA
			("RXCC_FULL_LOA",
					"rxcc_full_loa@excellus.com",
					"rxcc_full_loa"),

	RXCC_READ_LOA
			("RXCC_READ_LOA",
					"rxcc_read_loa@excellus.com",
					"rxcc_read_loa"),

	RXCC_OPS_LOA
			("RXCC_OPS_LOA",
					"rxcc_ops_loa@excellus.com",
					"rxcc_ops_loa"),

	RXCC_REPORT_LOA
			("RXCC_REPORT_LOA",
					"rxcc_report_loa@excellus.com",
					"rxcc_report_loa"),

	/*
	MED tenant level access
	 */

	RXCC_FULL_MED
			("RXCC_FULL_MED",
					"rxcc_full_med@excellus.com",
					"rxcc_full_med"),

	RXCC_READ_MED
			("RXCC_READ_MED",
					"rxcc_read_med@excellus.com",
					"rxcc_read_med"),

	RXCC_OPS_MED
			("RXCC_OPS_MED",
					"rxcc_ops_med@excellus.com",
					"rxcc_ops_med"),

	RXCC_REPORT_MED
			("RXCC_REPORT_MED",
					"rxcc_report_med@excellus.com",
					"rxcc_report_med"),

	/*
	 * Performance testing
	 */

	CAPTest01 ("CAP Test01", "CAPTest01@excellus.com", "CAPTest01"),
	CAPTest02 ("CAP Test02", "CAPTest02@excellus.com", "CAPTest02"),
	CAPTest03 ("CAP Test03", "CAPTest03@excellus.com", "CAPTest03"),
	CAPTest04 ("CAP Test04", "CAPTest04@excellus.com", "CAPTest04"),
	CAPTest05 ("CAP Test05", "CAPTest05@excellus.com", "CAPTest05"),
	CAPTest06 ("CAP Test06", "CAPTest06@excellus.com", "CAPTest06"),
	CAPTest07 ("CAP Test07", "CAPTest07@excellus.com", "CAPTest07"),
	CAPTest08 ("CAP Test08", "CAPTest08@excellus.com", "CAPTest08"),
	CAPTest09 ("CAP Test09", "CAPTest09@excellus.com", "CAPTest09"),
	CAPTest10 ("CAP Test10", "CAPTest10@excellus.com", "CAPTest10"),
	CAPTest11 ("CAP Test11", "CAPTest11@excellus.com", "CAPTest11"),
	CAPTest12 ("CAP Test12", "CAPTest12@excellus.com", "CAPTest12"),
	CAPTest13 ("CAP Test13", "CAPTest13@excellus.com", "CAPTest13"),
	CAPTest14 ("CAP Test14", "CAPTest14@excellus.com", "CAPTest14"),
	CAPTest15 ("CAP Test15", "CAPTest15@excellus.com", "CAPTest15"),
	CAPTest16 ("CAP Test16", "CAPTest16@excellus.com", "CAPTest16"),
	CAPTest17 ("CAP Test17", "CAPTest17@excellus.com", "CAPTest17"),
	CAPTest18 ("CAP Test18", "CAPTest18@excellus.com", "CAPTest18"),
	CAPTest19 ("CAP Test19", "CAPTest19@excellus.com", "CAPTest19"),
	CAPTest20 ("CAP Test20", "CAPTest20@excellus.com", "CAPTest20"),
	CAPTest21 ("CAP Test21", "CAPTest21@excellus.com", "CAPTest21"),
	CAPTest22 ("CAP Test22", "CAPTest22@excellus.com", "CAPTest22"),
	CAPTest23 ("CAP Test23", "CAPTest23@excellus.com", "CAPTest23"),
	CAPTest24 ("CAP Test24", "CAPTest24@excellus.com", "CAPTest24"),
	CAPTest25 ("CAP Test25", "CAPTest25@excellus.com", "CAPTest25"),

	;
	
	private String userid;		// User ID that is used throughout the test
	private String username;	// Username used to login to Web application
	private String name;		// Name associated with the username, e.g. John Doe
	
	UserConfiguration(String userid, String username, String name)
	{
		this.userid = userid;
		this.username = username;
		this.name = name;
	}

	/**
	 * Retrieve all performance test accounts
	 *
	 * @return list of accounts
	 */
	public static List<UserConfiguration> getPerformanceTestAccts()
	{
		List<UserConfiguration> accts = new ArrayList<UserConfiguration>();

		for ( UserConfiguration acct : UserConfiguration.values() )
		{
			if ( acct.getName().startsWith("CAPTest") )
			{
				accts.add(acct);
			}
		}

		return accts;
	}

	public static UserConfiguration valueOfUserId(String userId)
	{
		return valueOf(userId.toUpperCase());
	}

	@Override
	public String getUserId() {
		return userid;
	}

	@Override
	public String getUsername() 
	{
		return username;
	}

	@Override
	public String getName() {
		return name;
	}

}
