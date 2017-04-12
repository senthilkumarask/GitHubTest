package com.bbb.search.bean.result;

import java.io.Serializable;

public class BBBDynamicPriceVO implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String bbbListPriceString;
	private String bbbSalePriceString;
	private boolean bbbIncartFlag;
	private String bbbPricingLabelCode;
	private String babyListPriceString;
	private String babySalePriceString;
	private boolean babyIncartFlag;
	private String babyPricingLabelCode;
	private String caListPriceString;
	private String caSalePriceString;
	private boolean caIncartFlag;
	private String caPricingLabelCode;
	private String mxListPriceString;
	private String mxSalePriceString;
	private boolean mxIncartFlag;
	private String mxPricingLabelCode;
	private boolean alreadyPopulated;
	private boolean dynamicProdEligible;
	private String siteId;
	
	//Below are set by siteId specific values
	private boolean inCartFlag;
	private boolean dynamicPricingProduct;
	private String priceLabelCode;
	private String priceRangeDescription;
	private String salePriceRangeDescription;
	
	private String country;
	private boolean setFromCache;
	
	public boolean isSetFromCache() {
		return setFromCache;
	}
	public void setSetFromCache(boolean setFromCache) {
		this.setFromCache = setFromCache;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public boolean isAlreadyPopulated() {
		return alreadyPopulated;
	}
	public void setAlreadyPopulated(boolean alreadyPopulated) {
		this.alreadyPopulated = alreadyPopulated;
	}
	public String getBbbListPriceString() {
		return bbbListPriceString;
	}
	public void setBbbListPriceString(String bbbListPriceString) {
		this.bbbListPriceString = bbbListPriceString;
	}
	public String getBbbSalePriceString() {
		return bbbSalePriceString;
	}
	public void setBbbSalePriceString(String bbbSalePriceString) {
		this.bbbSalePriceString = bbbSalePriceString;
	}
	
	public String getBbbPricingLabelCode() {
		return bbbPricingLabelCode;
	}
	public void setBbbPricingLabelCode(String bbbPricingLabelCode) {
		this.bbbPricingLabelCode = bbbPricingLabelCode;
	}
	public String getBabyListPriceString() {
		return babyListPriceString;
	}
	public void setBabyListPriceString(String babyListPriceString) {
		this.babyListPriceString = babyListPriceString;
	}
	public String getBabySalePriceString() {
		return babySalePriceString;
	}
	public void setBabySalePriceString(String babySalePriceString) {
		this.babySalePriceString = babySalePriceString;
	}
	
	public String getBabyPricingLabelCode() {
		return babyPricingLabelCode;
	}
	public void setBabyPricingLabelCode(String babyPricingLabelCode) {
		this.babyPricingLabelCode = babyPricingLabelCode;
	}
	public String getCaListPriceString() {
		return caListPriceString;
	}
	public void setCaListPriceString(String caListPriceString) {
		this.caListPriceString = caListPriceString;
	}
	public String getCaSalePriceString() {
		return caSalePriceString;
	}
	public void setCaSalePriceString(String caSalePriceString) {
		this.caSalePriceString = caSalePriceString;
	}
	
	public String getCaPricingLabelCode() {
		return caPricingLabelCode;
	}
	public void setCaPricingLabelCode(String caPricingLabelCode) {
		this.caPricingLabelCode = caPricingLabelCode;
	}
	public String getMxListPriceString() {
		return mxListPriceString;
	}
	public void setMxListPriceString(String mxListPriceString) {
		this.mxListPriceString = mxListPriceString;
	}
	public String getMxSalePriceString() {
		return mxSalePriceString;
	}
	public void setMxSalePriceString(String mxSalePriceString) {
		this.mxSalePriceString = mxSalePriceString;
	}
	
	public String getMxPricingLabelCode() {
		return mxPricingLabelCode;
	}
	public void setMxPricingLabelCode(String mxPricingLabelCode) {
		this.mxPricingLabelCode = mxPricingLabelCode;
	}
	/**
	
	/**
	 * @return the siteId
	 */
	public final String getSiteId() {
		return siteId;
	}
	/**
	 * @param siteId the siteId to set
	 */
	public final void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	/**
	 * @return the bbbIncartFlag
	 */
	public final boolean isBbbIncartFlag() {
		return bbbIncartFlag;
	}
	/**
	 * @param bbbIncartFlag the bbbIncartFlag to set
	 */
	public final void setBbbIncartFlag(boolean bbbIncartFlag) {
		this.bbbIncartFlag = bbbIncartFlag;
	}
	/**
	 * @return the babyIncartFlag
	 */
	public final boolean isBabyIncartFlag() {
		return babyIncartFlag;
	}
	/**
	 * @param babyIncartFlag the babyIncartFlag to set
	 */
	public final void setBabyIncartFlag(boolean babyIncartFlag) {
		this.babyIncartFlag = babyIncartFlag;
	}
	/**
	 * @return the caIncartFlag
	 */
	public final boolean isCaIncartFlag() {
		return caIncartFlag;
	}
	/**
	 * @param caIncartFlag the caIncartFlag to set
	 */
	public final void setCaIncartFlag(boolean caIncartFlag) {
		this.caIncartFlag = caIncartFlag;
	}
	/**
	 * @return the mxIncartFlag
	 */
	public final boolean isMxIncartFlag() {
		return mxIncartFlag;
	}
	/**
	 * @param mxIncartFlag the mxIncartFlag to set
	 */
	public final void setMxIncartFlag(boolean mxIncartFlag) {
		this.mxIncartFlag = mxIncartFlag;
	}
	/**
	 * @return the dynamicProdEligible
	 */
	public final boolean isDynamicProdEligible() {
		return dynamicProdEligible;
	}
	/**
	 * @param dynamicProdEligible the dynamicProdEligible to set
	 */
	public final void setDynamicProdEligible(boolean dynamicProdEligible) {
		this.dynamicProdEligible = dynamicProdEligible;
	}
	public boolean isInCartFlag() {
		return inCartFlag;
	}
	public void setInCartFlag(boolean inCartFlag) {
		this.inCartFlag = inCartFlag;
	}
	public boolean isDynamicPricingProduct() {
		return dynamicPricingProduct;
	}
	public void setDynamicPricingProduct(boolean dynamicPricingProduct) {
		this.dynamicPricingProduct = dynamicPricingProduct;
	}
	public String getPriceLabelCode() {
		return priceLabelCode;
	}
	public void setPriceLabelCode(String priceLabelCode) {
		this.priceLabelCode = priceLabelCode;
	}
	public String getPriceRangeDescription() {
		return priceRangeDescription;
	}
	public void setPriceRangeDescription(String priceRangeDescription) {
		this.priceRangeDescription = priceRangeDescription;
	}
	public String getSalePriceRangeDescription() {
		return salePriceRangeDescription;
	}
	public void setSalePriceRangeDescription(String salePriceRangeDescription) {
		this.salePriceRangeDescription = salePriceRangeDescription;
	}
	@Override
	public String toString() {
		return "BBBDynamicPriceVO [bbbListPriceString=" + bbbListPriceString
				+ ", bbbSalePriceString=" + bbbSalePriceString
				+ ", bbbIncartFlag=" + bbbIncartFlag + ", bbbPricingLabelCode="
				+ bbbPricingLabelCode + ", babyListPriceString="
				+ babyListPriceString + ", babySalePriceString="
				+ babySalePriceString + ", babyIncartFlag=" + babyIncartFlag
				+ ", babyPricingLabelCode=" + babyPricingLabelCode
				+ ", caListPriceString=" + caListPriceString
				+ ", caSalePriceString=" + caSalePriceString
				+ ", caIncartFlag=" + caIncartFlag + ", caPricingLabelCode="
				+ caPricingLabelCode + ", mxListPriceString="
				+ mxListPriceString + ", mxSalePriceString="
				+ mxSalePriceString + ", mxIncartFlag=" + mxIncartFlag
				+ ", mxPricingLabelCode=" + mxPricingLabelCode
				+ ", alreadyPopulated=" + alreadyPopulated
				+ ", dynamicProdEligible=" + dynamicProdEligible + ", siteId="
				+ siteId + ", inCartFlag=" + inCartFlag
				+ ", dynamicPricingProduct=" + dynamicPricingProduct
				+ ", priceLabelCode=" + priceLabelCode
				+ ", priceRangeDescription=" + priceRangeDescription
				+ ", salePriceRangeDescription=" + salePriceRangeDescription
				+ "]";
	}


}
