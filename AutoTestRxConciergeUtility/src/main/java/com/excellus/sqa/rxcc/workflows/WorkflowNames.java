/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
package com.excellus.sqa.rxcc.workflows;

import com.excellus.sqa.workflow.IEnumWorkflow;

/**
 * 
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 01/06/2023
 */
public enum WorkflowNames implements IEnumWorkflow 
{
    MEMBER_INTERVENTIONS,
    MEMBER_CLAIMS,
    MEMBER_PROVIDER,
    MEMBER_PHARMACY,
    MEMBER_CORRESPONDENCE,
    MEMBER_NOTES,
    MEMBER,
    PROVIDER,
    PHARMACY,
    INTERVENTION,
	
	SEND_FAX,
	PROCESS_SENT_FAX;
	
	@Override
	public String workflow() {
		return name();
	}
}

