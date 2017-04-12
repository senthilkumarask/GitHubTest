package com.bbb.search.bean.result;

import java.io.Serializable;

public class BBBDynamicPriceSkuVO implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean bbbIncartFlag;
	private String bbbPricingLabelCode;
	private boolean babyIncartFlag;
	private String babyPricingLabelCode;
	private boolean caIncartFlag;
	private String caPricingLabelCode;
	private boolean mxIncartFlag;
	private String mxPricingLabelCode;
	
	//Below are set by siteId logic
	private boolean inCartFlag;
	private String pricingLabelCode;
	public boolean getInCartFlag() {
		return inCartFlag;
	}
	public void setInCartFlag(boolean incartFlag) {
		this.inCartFlag = incartFlag;
	}
	public String getPricingLabelCode() {
		return pricingLabelCode;
	}
	public void setPricingLabelCode(String pricingLabelCode) {
		this.pricingLabelCode = pricingLabelCode;
	}
	public boolean isDynamicPriceSKU() {
		return dynamicPriceSKU;
	}
	public void setDynamicPriceSKU(boolean dynamicPriceSKU) {
		this.dynamicPriceSKU = dynamicPriceSKU;
	}
	private boolean dynamicPriceSKU;
	
	public boolean isBbbIncartFlag() {
		return bbbIncartFlag;
	}
	public void setBbbIncartFlag(boolean bbbIncartFlag) {
		this.bbbIncartFlag = bbbIncartFlag;
	}
	public String getBbbPricingLabelCode() {
		return bbbPricingLabelCode;
	}
	public void setBbbPricingLabelCode(String bbbPricingLabelCode) {
		this.bbbPricingLabelCode = bbbPricingLabelCode;
	}
	public boolean isBabyIncartFlag() {
		return babyIncartFlag;
	}
	public void setBabyIncartFlag(boolean babyIncartFlag) {
		this.babyIncartFlag = babyIncartFlag;
	}
	public String getBabyPricingLabelCode() {
		return babyPricingLabelCode;
	}
	public void setBabyPricingLabelCode(String babyPricingLabelCode) {
		this.babyPricingLabelCode = babyPricingLabelCode;
	}
	public boolean isCaIncartFlag() {
		return caIncartFlag;
	}
	public void setCaIncartFlag(boolean caIncartFlag) {
		this.caIncartFlag = caIncartFlag;
	}
	public String getCaPricingLabelCode() {
		return caPricingLabelCode;
	}
	public void setCaPricingLabelCode(String caPricingLabelCode) {
		this.caPricingLabelCode = caPricingLabelCode;
	}
	public boolean isMxIncartFlag() {
		return mxIncartFlag;
	}
	public void setMxIncartFlag(boolean mxIncartFlag) {
		this.mxIncartFlag = mxIncartFlag;
	}
	public String getMxPricingLabelCode() {
		return mxPricingLabelCode;
	}
	public void setMxPricingLabelCode(String mxPricingLabelCode) {
		this.mxPricingLabelCode = mxPricingLabelCode;
	}
}
