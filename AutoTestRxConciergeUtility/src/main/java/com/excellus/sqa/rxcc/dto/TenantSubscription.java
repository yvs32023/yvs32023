/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.dto;

/**
 * 
 * 
 * @author Manish Sharma (msharma)
 * @since 05/20/2022
 * @deprecated since 10/31/22. Use {@link Tenant.Type}
 */
@Deprecated
public enum TenantSubscription 
{
	EXE		("exe",		"Excellus Employee",	"5ebc0f4d-a684-4a93-9c36-7de6f5d84833"),
	EHP		("ehp",		"Excellus Health Plan",	"87f4a82d-e2b6-44cc-9fff-828f94cf8ec9");

	
	private String subscriptionName;
	private String tenantName;
	private String adTenantId;

	TenantSubscription(String subscriptionName, String tenantName, String adTenantId) {
		// TODO Auto-generated constructor stub
		
		this.subscriptionName = subscriptionName;
		this.tenantName = tenantName;
		this.adTenantId = adTenantId;
	}
	

	public String getSubscriptionName() {
		return subscriptionName;
	}

	public String getTenantName() {
		return tenantName;
	}

	public String getAdTenantId() {
		return adTenantId;
	}

}
