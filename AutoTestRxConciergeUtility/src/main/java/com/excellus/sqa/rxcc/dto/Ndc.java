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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.JsonObject;

/**
 * DTO for NDC
 * 
 * JSON schema is not defined as of yet - 3/7/2022
 * 
 * @author Manish Sharma (msharma)
 * @since 03/02/2022
 */
@JsonPropertyOrder({ "awpCurrEffDate", "awpCurrUnitPrice", 
	"brandNameCode", "deaCode", 
	"dosageForm", "gpi2Group", 
	"gpi2GroupCode", "gpi4Class", "gpi4ClassCode", "gpi6Subclass", "gpi6SubclassCode", "gpi8BaseName", 
	"gpi8BaseNameCode",  
	"gpi10GenericName", "gpi10GenericNameCode", "gpi12GenericNameDoseForm", "gpi12GenericNameDoseFormCode", "gpi14Name", "gpi14Unformatted", 
	"inactiveDate", "inactiveFlag",  
	"innerPack", "labeler", "marketingCategory", "ms", 
	"ms",  
	"packageDescr",
	"productName", "qty", "routeAdmin", "size", "teeCode", "usualDose", "usualDuration",  
	"wacCurrentEffectiveDate", "wacCurrentUnitPrice",
	"repackCode", "rxOTC", "ahfsClass", "appNumber", "awpCurrPackagePrice",  
	"gpi0CategoryRange", "packageSum", "wacCurrPackagePrice"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ndc extends Item 
{
	@JsonProperty(required=true)
	private String awpCurrEffDate;

	@JsonProperty(required=true)	
	private Integer awpCurrUnitPrice; 


	@JsonProperty(required=true)
	private String brandNameCode;

	@JsonProperty(required=true)
	private String deaCode;

	@JsonProperty(required=true)
	private String dosageForm;

	@JsonProperty(required=true)
	private String gpi2Group;

	@JsonProperty(required=true)
	private String gpi2GroupCode;

	@JsonProperty(required=true)
	private String gpi4Class;

	@JsonProperty(required=true)
	private String gpi4ClassCode;

	@JsonProperty(required=true)
	private String gpi6Subclass; 

	@JsonProperty(required=true)
	private String gpi6SubclassCode;

	@JsonProperty(required=true)
	private String gpi8BaseName;

	@JsonProperty(required=true)
	private String gpi8BaseNameCode;

	@JsonProperty(required=true)
	private String gpi10GenericName;

	@JsonProperty(required=true)
	private String gpi10GenericNameCode;

	@JsonProperty(required=true)
	private String gpi12GenericNameDoseForm;

	@JsonProperty(required=true)
	private String gpi12GenericNameDoseFormCode;

	@JsonProperty(required=true)
	private String gpi14Name; 

	@JsonProperty(required=true)
	private String gpi14Unformatted;

	@JsonProperty(required=true)
	private String inactiveDate;

	@JsonProperty(required=true)
	private String inactiveFlag;

	@JsonProperty(required=true)
	private String innerPack;

	@JsonProperty(required=true)
	private String labeler;

	@JsonProperty(required=true)
	private String marketingCategory;

	@JsonProperty(required=true)
	private String ms;

	@JsonProperty(required=true)
	private String ndc; 

	@JsonProperty(required=true)
	private String packageDescr;

	@JsonProperty(required=true)
	private String productName;

	@JsonProperty(required=true)
	private String qty;

	@JsonProperty(required=true)
	private String routeAdmin;

	@JsonProperty(required=true)
	private int size;

	@JsonProperty(required=true)
	private String teeCode;

	@JsonProperty(required=true)
	private int usualDose;

	@JsonProperty(required=true)
	private int usualDuration;

	@JsonProperty(required=true)
	private String wacCurrentEffectiveDate;

	@JsonProperty(required=true)
	private String wacCurrentUnitPrice;

	@JsonProperty(required=true)
	private String repackCode;

	@JsonProperty(required=true)
	private String rxOTC;

	@JsonProperty(required=true)
	private String ahfsClass;

	@JsonProperty(required=true)
	private String appNumber;

	@JsonProperty(required=true)
	private int awpCurrPackagePrice;

	@JsonProperty(required=true)
	private String gpi0CategoryRange;

	@JsonProperty(required=true)
	private String packageSum;

	@JsonProperty(required=true)
	private int wacCurrPackagePrice;



	/*
	 * Validations
	 */

	/**
	 * Compare ndc properties defined in the schema
	 * 
	 * @param ndc {@link Ndc} to compare with
	 * @return test results of comparison
	 */
	public List<DynamicNode> compare(Ndc ndcs)
	{
		List<DynamicNode> tests = new ArrayList<DynamicNode>();

		tests.add(dynamicTest("awpCurrEffDate [" + awpCurrEffDate + "]", () -> assertEquals(awpCurrEffDate, ndcs.getAwpCurrEffDate(), getApiInfo(ndcs))));

		tests.add(dynamicTest("awpCurrUnitPrice [" + awpCurrUnitPrice + "]", () -> assertEquals(awpCurrUnitPrice, ndcs.getAwpCurrUnitPrice(), getApiInfo(ndcs))));

		tests.add(dynamicTest("brandNameCode [" + brandNameCode + "]", () -> assertEquals(brandNameCode, ndcs.getBrandNameCode(), getApiInfo(ndcs))));
		tests.add(dynamicTest("deaCode [" + deaCode + "]", () -> assertEquals(deaCode, ndcs.getDeaCode(), getApiInfo(ndcs))));
		tests.add(dynamicTest("dosageForm [" + dosageForm + "]", () -> assertEquals(dosageForm, ndcs.getDosageForm(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi2Group [" + gpi2Group + "]", () -> assertEquals(gpi2Group, ndcs.getGpi2Group(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi2GroupCode [" + gpi2GroupCode + "]", () -> assertEquals(gpi2GroupCode, ndcs.getGpi2GroupCode(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi4Class [" + gpi4Class + "]", () -> assertEquals(gpi4Class, ndcs.getGpi4Class(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi4ClassCode [" + gpi4ClassCode + "]", () -> assertEquals(gpi4ClassCode, ndcs.getGpi4ClassCode(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi6Subclass [" + gpi6Subclass + "]", () -> assertEquals(gpi6Subclass, ndcs.getGpi6Subclass(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi6SubclassCode [" + gpi6SubclassCode + "]", () -> assertEquals(gpi6SubclassCode, ndcs.getGpi6SubclassCode(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi8BaseName [" + gpi8BaseName + "]", () -> assertEquals(gpi8BaseName, ndcs.getGpi8BaseName(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi8BaseNameCode [" + gpi8BaseNameCode + "]", () -> assertEquals(gpi8BaseNameCode, ndcs.getGpi8BaseNameCode(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi10GenericName [" + gpi10GenericName + "]", () -> assertEquals(gpi10GenericName, ndcs.getGpi10GenericName(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi10GenericNameCode [" + gpi10GenericNameCode + "]", () -> assertEquals(gpi10GenericNameCode, ndcs.getGpi10GenericNameCode(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi12GenericNameDoseForm [" + gpi12GenericNameDoseForm + "]", () -> assertEquals(gpi12GenericNameDoseForm, ndcs.getGpi12GenericNameDoseForm(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi12GenericNameDoseFormCode [" + gpi12GenericNameDoseFormCode + "]", () -> assertEquals(gpi12GenericNameDoseFormCode, ndcs.getGpi12GenericNameDoseFormCode(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi14Name [" + gpi14Name + "]", () -> assertEquals(gpi14Name, ndcs.getGpi14Name(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi14Unformatted [" + gpi14Unformatted + "]", () -> assertEquals(gpi14Unformatted, ndcs.getGpi14Unformatted(), getApiInfo(ndcs))));
		tests.add(dynamicTest("inactiveDate [" + inactiveDate + "]", () -> assertEquals(inactiveDate, ndcs.getInactiveDate(), getApiInfo(ndcs))));
		tests.add(dynamicTest("inactiveFlag [" + inactiveFlag + "]", () -> assertEquals(inactiveFlag, ndcs.getInactiveFlag(), getApiInfo(ndcs))));
		tests.add(dynamicTest("innerPack [" + innerPack + "]", () -> assertEquals(innerPack, ndcs.getInnerPack(), getApiInfo(ndcs))));
		tests.add(dynamicTest("labeler [" + labeler + "]", () -> assertEquals(labeler, ndcs.getLabeler(), getApiInfo(ndcs))));
		tests.add(dynamicTest("marketingCategory [" + marketingCategory + "]", () -> assertEquals(marketingCategory, ndcs.getMarketingCategory(), getApiInfo(ndcs))));
		tests.add(dynamicTest("ms [" + ms + "]", () -> assertEquals(ms, ndcs.getMs(), getApiInfo(ndcs))));
		tests.add(dynamicTest("ndc [" + ndc + "]", () -> assertEquals(ndc, ndcs.getNdc(), getApiInfo(ndcs))));
		tests.add(dynamicTest("packageDescr [" + packageDescr + "]", () -> assertEquals(packageDescr, ndcs.getPackageDescr(), getApiInfo(ndcs))));
		tests.add(dynamicTest("productName [" + productName + "]", () -> assertEquals(productName, ndcs.getProductName(), getApiInfo(ndcs))));
		tests.add(dynamicTest("qty [" + qty + "]", () -> assertEquals(qty, ndcs.getQty(), getApiInfo(ndcs))));
		tests.add(dynamicTest("routeAdmin  [" + routeAdmin  + "]", () -> assertEquals(routeAdmin , ndcs.getRouteAdmin(), getApiInfo(ndcs))));
		tests.add(dynamicTest("size [" + size + "]", () -> assertEquals(size, ndcs.getSize(), getApiInfo(ndcs))));
		tests.add(dynamicTest("teeCode [" + teeCode + "]", () -> assertEquals(teeCode, ndcs.getTeeCode(), getApiInfo(ndcs))));
		tests.add(dynamicTest("usualDose [" + usualDose + "]", () -> assertEquals(usualDose, ndcs.getUsualDose(), getApiInfo(ndcs))));
		tests.add(dynamicTest("usualDuration [" + usualDuration + "]", () -> assertEquals(usualDuration, ndcs.getUsualDuration(), getApiInfo(ndcs))));
		tests.add(dynamicTest("wacCurrentEffectiveDate [" + wacCurrentEffectiveDate + "]", () -> assertEquals(wacCurrentEffectiveDate, ndcs.getWacCurrentEffectiveDate(), getApiInfo(ndcs))));
		tests.add(dynamicTest("wacCurrentUnitPrice [" + wacCurrentUnitPrice + "]", () -> assertEquals(wacCurrentUnitPrice, ndcs.getWacCurrentUnitPrice(), getApiInfo(ndcs))));
		tests.add(dynamicTest("repackCode [" + repackCode + "]", () -> assertEquals(repackCode, ndcs.getRepackCode(), getApiInfo(ndcs))));
		tests.add(dynamicTest("rxOTC [" + rxOTC + "]", () -> assertEquals(rxOTC, ndcs.getRxOTC(), getApiInfo(ndcs))));
		tests.add(dynamicTest("ahfsClass [" + ahfsClass + "]", () -> assertEquals(ahfsClass, ndcs.getAhfsClass(), getApiInfo(ndcs))));
		tests.add(dynamicTest("appNumber [" + appNumber + "]", () -> assertEquals(appNumber, ndcs.getAppNumber(), getApiInfo(ndcs))));
		tests.add(dynamicTest("awpCurrPackagePrice [" + awpCurrPackagePrice + "]", () -> assertEquals(awpCurrPackagePrice, ndcs.getAwpCurrPackagePrice(), getApiInfo(ndcs))));
		tests.add(dynamicTest("gpi0CategoryRange [" + gpi0CategoryRange + "]", () -> assertEquals(gpi0CategoryRange, ndcs.getGpi0CategoryRange(), getApiInfo(ndcs))));
		tests.add(dynamicTest("packageSum [" + packageSum + "]", () -> assertEquals(packageSum, ndcs.getPackageSum(), getApiInfo(ndcs))));
		tests.add(dynamicTest("wacCurrPackagePrice [" + wacCurrPackagePrice + "]", () -> assertEquals(wacCurrPackagePrice, ndcs.getWacCurrPackagePrice(), getApiInfo(ndcs))));

		tests.addAll( super.compare(ndcs) );

		return tests;
	}


	/*
	 * Helper methods
	 */

	public JsonObject convertToNdcSearch(List<NdcField> fieldSelection)
	{
		JsonObject drugs = new JsonObject();


		for ( NdcField field : fieldSelection )
		{
			{
				switch (field) {
				case labeler:
				{
					drugs.addProperty(field.toString(),this.getLabeler());
				}
				break;

				case marketingCategory:
				{
					drugs.addProperty(field.toString(),this.getMarketingCategory());
				}
				break;

				case brandNameCode:
				{
					drugs.addProperty(field.toString(),this.getBrandNameCode());
				}
				break;

				case productName:
				{
					drugs.addProperty(field.toString(),this.getProductName());
				}
				break;

				case ndc:
				{
					drugs.addProperty(field.toString(),this.getNdc());
				}
				break;

				case awpCurrUnitPrice:
				{
					drugs.addProperty(field.toString(),this.getAwpCurrUnitPrice());
				}
				break;

				case wacCurrentUnitPrice:
				{
					drugs.addProperty(field.toString(),this.getWacCurrentUnitPrice());
				}
				break;

				case gpi14Unformatted:
				{
					drugs.addProperty(field.toString(),this.getGpi14Unformatted());
				}
				break;

				case gpi14Name:
				{
					drugs.addProperty(field.toString(),this.getGpi14Name());
				}
				break;

				case gpi12GenericNameDoseForm:
				{
					drugs.addProperty(field.toString(),this.getGpi12GenericNameDoseForm());
				}
				break;

				case gpi12GenericNameDoseFormCode:
				{
					drugs.addProperty(field.toString(),this.getGpi12GenericNameDoseFormCode());
				}
				break;

				case gpi10GenericNameCode:
				{
					drugs.addProperty(field.toString(),this.getGpi10GenericNameCode());
				}
				break;

				case gpi10GenericName:
				{
					drugs.addProperty(field.toString(),this.getGpi10GenericName());
				}
				break;

				case gpi8BaseNameCode:
				{
					drugs.addProperty(field.toString(),this.getGpi8BaseNameCode());
				}
				break;

				case gpi8BaseName:
				{
					drugs.addProperty(field.toString(),this.getGpi8BaseName());
				}
				break;

				case gpi6SubclassCode:
				{
					drugs.addProperty(field.toString(),this.getGpi6SubclassCode());
				}
				break;

				case gpi6Subclass:
				{
					drugs.addProperty(field.toString(),this.getGpi6Subclass());
				}
				break;


				case gpi4ClassCode:
				{
					drugs.addProperty(field.toString(),this.getGpi4ClassCode());
				}
				break;

				case gpi4Class:
				{
					drugs.addProperty(field.toString(),this.getGpi4Class());
				}
				break;

				case gpi2GroupCode:
				{
					drugs.addProperty(field.toString(),this.getGpi2GroupCode());
				}
				break;

				case gpi2Group:
				{
					drugs.addProperty(field.toString(),this.getGpi2Group());
				}
				break;

				case gpi0CategoryRange:
				{
					drugs.addProperty(field.toString(),this.getGpi0CategoryRange());
				}
				break;


				default:
					break;
				}

			}
		}

		return drugs;
	}

	/*
	 * Setter / Getter
	 */
	public String getAwpCurrEffDate() {
		return awpCurrEffDate;
	}

	public void setAwpCurrEffDate(String awpCurrEffDate) {
		this.awpCurrEffDate = awpCurrEffDate;
	}

	public Integer getAwpCurrUnitPrice() {
		return awpCurrUnitPrice;
	}

	public void setAwpCurrUnitPrice(float awpCurrUnitPrice) {
		this.awpCurrUnitPrice = (int)awpCurrUnitPrice;
	}

	public String getBrandNameCode() {
		return brandNameCode;
	}

	public void setBrandNameCode(String brandNameCode) {
		this.brandNameCode = brandNameCode;
	}

	public String getDeaCode() {
		return deaCode;
	}

	public void setDeaCode(String deaCode) {
		this.deaCode = deaCode;
	}

	public String getDosageForm() {
		return dosageForm;
	}

	public void setDosageForm(String dosageForm) {
		this.dosageForm = dosageForm;
	}

	public String getGpi2Group() {
		return gpi2Group;
	}

	public void setGpi2Group(String gpi2Group) {
		this.gpi2Group = gpi2Group;
	}

	public String getGpi2GroupCode() {
		return gpi2GroupCode;
	}

	public void setGpi2GroupCode(String gpi2GroupCode) {
		this.gpi2GroupCode = gpi2GroupCode;
	}

	public String getGpi4Class() {
		return gpi4Class;
	}

	public void setGpi4Class(String gpi4Class) {
		this.gpi4Class = gpi4Class;
	}

	public String getGpi4ClassCode() {
		return gpi4ClassCode;
	}

	public void setGpi4ClassCode(String gpi4ClassCode) {
		this.gpi4ClassCode = gpi4ClassCode;
	}

	public String getGpi6Subclass() {
		return gpi6Subclass;
	}

	public void setGpi6Subclass(String gpi6Subclass) {
		this.gpi6Subclass = gpi6Subclass;
	}

	public String getGpi6SubclassCode() {
		return gpi6SubclassCode;
	}

	public void setGpi6SubclassCode(String gpi6SubclassCode) {
		this.gpi6SubclassCode = gpi6SubclassCode;
	}

	public String getGpi8BaseName() {
		return gpi8BaseName;
	}

	public void setGpi8BaseName(String gpi8BaseName) {
		this.gpi8BaseName = gpi8BaseName;
	}

	public String getGpi8BaseNameCode() {
		return gpi8BaseNameCode;
	}

	public void setGpi8BaseNameCode(String gpi8BaseNameCode) {
		this.gpi8BaseNameCode = gpi8BaseNameCode;
	}

	public String getGpi10GenericName() {
		return gpi10GenericName;
	}

	public void setGpi10GenericName(String gpi10GenericName) {
		this.gpi10GenericName = gpi10GenericName;
	}

	public String getGpi10GenericNameCode() {
		return gpi10GenericNameCode;
	}

	public void setGpi10GenericNameCode(String gpi10GenericNameCode) {
		this.gpi10GenericNameCode = gpi10GenericNameCode;
	}

	public String getGpi12GenericNameDoseForm() {
		return gpi12GenericNameDoseForm;
	}

	public void setGpi12GenericNameDoseForm(String gpi12GenericNameDoseForm) {
		this.gpi12GenericNameDoseForm = gpi12GenericNameDoseForm;
	}

	public String getGpi12GenericNameDoseFormCode() {
		return gpi12GenericNameDoseFormCode;
	}

	public void setGpi12GenericNameDoseFormCode(String gpi12GenericNameDoseFormCode) {
		this.gpi12GenericNameDoseFormCode = gpi12GenericNameDoseFormCode;
	}

	public String getGpi14Name() {
		return gpi14Name;
	}

	public void setGpi14Name(String gpi14Name) {
		this.gpi14Name = gpi14Name;
	}

	public String getGpi14Unformatted() {
		return gpi14Unformatted;
	}

	public void setGpi14Unformatted(String gpi14Unformatted) {
		this.gpi14Unformatted = gpi14Unformatted;
	}

	public String getInactiveDate() {
		return inactiveDate;
	}

	public void setInactiveDate(String inactiveDate) {
		this.inactiveDate = inactiveDate;
	}

	public String getInactiveFlag() {
		return inactiveFlag;
	}

	public void setInactiveFlag(String inactiveFlag) {
		this.inactiveFlag = inactiveFlag;
	}

	public String getInnerPack() {
		return innerPack;
	}

	public void setInnerPack(String innerPack) {
		this.innerPack = innerPack;
	}

	public String getLabeler() {
		return labeler;
	}

	public void setLabeler(String labeler) {
		this.labeler = labeler;
	}

	public String getMarketingCategory() {
		return marketingCategory;
	}

	public void setMarketingCategory(String marketingCategory) {
		this.marketingCategory = marketingCategory;
	}

	public String getMs() {
		return ms;
	}

	public void setMs(String ms) {
		this.ms = ms;
	}

	public String getNdc() {
		return ndc;
	}

	public void setNdc(String ndc) {
		this.ndc = ndc;
	}

	public String getPackageDescr() {
		return packageDescr;
	}

	public void setPackageDescr(String packageDescr) {
		this.packageDescr = packageDescr;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getRouteAdmin() {
		return routeAdmin;
	}

	public void setRouteAdmin(String routeAdmin) {
		this.routeAdmin = routeAdmin;
	}

	public int getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = (int)size;
	}

	public String getTeeCode() {
		return teeCode;
	}

	public void setTeeCode(String teeCode) {
		this.teeCode = teeCode;
	}

	public int getUsualDose() {
		return usualDose;
	}

	public void setUsualDose(float usualDose) {
		this.usualDose = (int)usualDose;
	}

	public int getUsualDuration() {
		return usualDuration;
	}

	public void setUsualDuration(float usualDuration) {
		this.usualDuration = (int)usualDuration;
	}

	public String getWacCurrentEffectiveDate() {
		return wacCurrentEffectiveDate;
	}

	public void setWacCurrentEffectiveDate(String wacCurrentEffectiveDate) {
		this.wacCurrentEffectiveDate = wacCurrentEffectiveDate;
	}

	public String getWacCurrentUnitPrice() {
		return wacCurrentUnitPrice;
	}

	public void setWacCurrentUnitPrice(String wacCurrentUnitPrice) {
		this.wacCurrentUnitPrice = wacCurrentUnitPrice;
	}

	public String getRepackCode() {
		return repackCode;
	}

	public void setRepackCode(String repackCode) {
		this.repackCode = repackCode;
	}

	public String getRxOTC() {
		return rxOTC;
	}

	public void setRxOTC(String rxOTC) {
		this.rxOTC = rxOTC;
	}

	public String getAhfsClass() {
		return ahfsClass;
	}

	public void setAhfsClass(String ahfsClass) {
		this.ahfsClass = ahfsClass;
	}

	public String getAppNumber() {
		return appNumber;
	}

	public void setAppNumber(String appNumber) {
		this.appNumber = appNumber;
	}

	public int getAwpCurrPackagePrice() {
		return awpCurrPackagePrice;
	}

	public void setAwpCurrPackagePrice(float awpCurrPackagePrice) {
		this.awpCurrPackagePrice = (int)awpCurrPackagePrice;
	}

	public String getGpi0CategoryRange() {
		return gpi0CategoryRange;
	}

	public void setGpi0CategoryRange(String gpi0CategoryRange) {
		this.gpi0CategoryRange = gpi0CategoryRange;
	}

	public String getPackageSum() {
		return packageSum;
	}

	public void setPackageSum(String packageSum) {
		this.packageSum = packageSum;
	}

	public int getWacCurrPackagePrice() {
		return wacCurrPackagePrice;
	}

	public void setWacCurrPackagePrice(float wacCurrPackagePrice) {
		this.wacCurrPackagePrice = (int)wacCurrPackagePrice;
	}

	public enum NdcField {
		labeler, marketingCategory, brandNameCode,productName,ndc,awpCurrUnitPrice,wacCurrentUnitPrice,gpi14Unformatted,gpi14Name,
		gpi12GenericNameDoseForm,gpi12GenericNameDoseFormCode,gpi10GenericNameCode,gpi10GenericName,gpi8BaseNameCode,
		gpi8BaseName,gpi6SubclassCode,gpi6Subclass,gpi4ClassCode,gpi4Class,gpi2GroupCode,gpi2Group,gpi0CategoryRange;

		@Override
		public String toString() {
			return name();
		}
	}

}
