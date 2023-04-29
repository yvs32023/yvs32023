/**
 * 
 * @copyright 2022 Excellus BCBS
 * All rights reserved.
 * 
 */
/**
 * 
 */
package com.excellus.sqa.rxcc.steps.member;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.cosmos.implementation.apachecommons.lang.StringUtils;
import com.excellus.sqa.rxcc.cosmos.MemberQueries;
import com.excellus.sqa.rxcc.cosmos.MemberRxClaimQueries;
import com.excellus.sqa.rxcc.cosmos.PharmacyQueries;
import com.excellus.sqa.rxcc.cosmos.ProviderQueries;
import com.excellus.sqa.rxcc.dto.Member;
import com.excellus.sqa.rxcc.dto.MemberRxclaim;
import com.excellus.sqa.rxcc.dto.Pharmacy;
import com.excellus.sqa.rxcc.dto.Provider;
import com.excellus.sqa.rxcc.dto.Tenant;
import com.excellus.sqa.step.AbstractStep;

/**
 * 
 * @author Husnain Zia (hzia)
 * @since 05/26/2022
 */
/**
 * @author hzia
 *
 */
public class GetMemberRXClaimStep extends AbstractStep {
	private static final Logger logger = LoggerFactory.getLogger(GetMemberRXClaimStep.class);

	private final static String STEP_NAME = "GetMemberRXClaim";
	private final static String STEP_DESC = "Get member RX Claims";

	private String memberId;
	private Member member;
	private final String subscriptionName;
    private final String memberIdSmallNote;
	private List<MemberRxclaim> memberClaims;
	private List<Provider> providersList = new ArrayList<>();
	private List<Pharmacy> pharmacysList = new ArrayList<>();

	/**
	 * Constructor
	 * 
	 * @param subscriptionName
	 */
	public GetMemberRXClaimStep(String subscriptionName, String memberIdSmallNote)
	{
		super(STEP_NAME, STEP_DESC);
		this.subscriptionName=subscriptionName;
		this.memberIdSmallNote=memberIdSmallNote;
		memberClaims = new ArrayList<MemberRxclaim>();
	}

	@Override
	public void run() 
	{
		super.stepStatus = Status.IN_PROGRESS;
		logger.info(super.print());

		try
		{
		    memberId=memberIdSmallNote;
			member=MemberQueries.getMember(subscriptionName, memberId);
			if (subscriptionName.equalsIgnoreCase(Tenant.Type.EHP.getSubscriptionName()))
			{
				memberClaims = MemberRxClaimQueries.getALLTenantMemberRxclaim(subscriptionName, memberId);
			}
			else if (subscriptionName.equalsIgnoreCase(Tenant.Type.EXE.getSubscriptionName()))
			{
				memberClaims = MemberRxClaimQueries.getALLTenantMemberRxclaim(subscriptionName, memberId);
			}

			for (MemberRxclaim claim:memberClaims) {
				providersList.add(ProviderQueries.getProviderById(claim.getPrescriberId()));
				pharmacysList.add(PharmacyQueries.getPharmacyById(claim.getServiceProviderId()));
			}

			super.stepStatus = Status.COMPLETED;
		}
		catch (Exception e)
		{
			super.stepException = e;
			super.stepStatus = Status.ERROR;
		}

		logger.info(print());
	}

	/**
	 * Find the member note where the id or note contains the identifier
	 * @param identifier to find from note id or note itself
	 * @return member note if found otherwise returns null
	 */
	public MemberRxclaim getMemberRxclaim(String identifier)
	{
		for ( MemberRxclaim claim : memberClaims )
		{
			if ( StringUtils.containsIgnoreCase(claim.getId(), identifier) ||
					StringUtils.containsIgnoreCase(claim.getMemberId(), identifier) ) {
				return claim;
			}
		}

		return null;
	}

	public List<MemberRxclaim> getMemberClaims() {
		return memberClaims;
	}

	/**
	 * @return the providersList
	 */
	public List<Provider> getProvidersList() {
		return providersList;
	}

	/**
	 * @return the pharmacysList
	 */
	public List<Pharmacy> getPharmacysList() {
		return pharmacysList;
	}


}

