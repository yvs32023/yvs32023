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

/**
 * PdfLogoAddress is part of group
 * 
 * @author Garrett Cosmiano(gcosmian)
 * @since 09/07/2022
 */
public class PdfLogoAddress extends AbstractJsonDTO<PdfLogoAddress> 
{
	
	private String line1;
	private String line2;
	private String line3;
	private String line4;
	
	/*
	 * Validations
	 */
	
	@Override
	public List<DynamicNode> compare(PdfLogoAddress pdfLogoAddress)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();
		
		tests.add(dynamicTest("line1: [" + line1 + "]", () -> assertEquals(line1, pdfLogoAddress.getLine1(), getApiInfo(pdfLogoAddress))));
		tests.add(dynamicTest("line2: [" + line2 + "]", () -> assertEquals(line2, pdfLogoAddress.getLine2(), getApiInfo(pdfLogoAddress))));
		tests.add(dynamicTest("line3: [" + line3 + "]", () -> assertEquals(line3, pdfLogoAddress.getLine3(), getApiInfo(pdfLogoAddress))));
		tests.add(dynamicTest("line4: [" + line4 + "]", () -> assertEquals(line4, pdfLogoAddress.getLine4(), getApiInfo(pdfLogoAddress))));
		
		return tests;
	}
	
	/*
	 * Helper methods
	 */

	@Override
	public boolean equals(Object obj)
	{
		if ( obj instanceof PdfLogoAddress)
		{
			PdfLogoAddress pdfLogoAddress = (PdfLogoAddress) obj;

			if ( line1.equals(pdfLogoAddress.getLine1()) )
				return true;
		}
		
		return false;
	}


	/*
	 * Setter / Getter
	 */
	
	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getLine3() {
		return line3;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}

	public String getLine4() {
		return line4;
	}

	public void setLine4(String line4) {
		this.line4 = line4;
	}

}

