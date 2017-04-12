package com.bbb.search.bean.result;

import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;

public class TBSProduct extends BBBProduct{

	private static final long serialVersionUID = 1L;
	
	private String mNextProductId;
	
	private String mPreviousProductId;
	
	private String shortDescription;
	private String longDescription;
	private String mSkuId;
	private boolean ltlProduct;
	private boolean cmokirschFlag;
	
	private BazaarVoiceProductVO bvReviews;

	/**
	 * @return the shortDescription
	 */
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * @param shortDescription the shortDescription to set
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	/**
	 * @return the longDescription
	 */
	public String getLongDescription() {
		return longDescription;
	}

	/**
	 * @param longDescription the longDescription to set
	 */
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	/**
	 * @return the nextProductId
	 */
	public String getNextProductId() {
		return mNextProductId;
	}

	/**
	 * @return the previousProductId
	 */
	public String getPreviousProductId() {
		return mPreviousProductId;
	}

	/**
	 * @param pNextProductId the nextProductId to set
	 */
	public void setNextProductId(String pNextProductId) {
		mNextProductId = pNextProductId;
	}

	/**
	 * @param pPreviousProductId the previousProductId to set
	 */
	public void setPreviousProductId(String pPreviousProductId) {
		mPreviousProductId = pPreviousProductId;
	}

	/**
	 * @return the skuId
	 */
	public String getSkuId() {
		return mSkuId;
	}

	/**
	 * @param pSkuId the skuId to set
	 */
	public void setSkuId(String pSkuId) {
		mSkuId = pSkuId;
	}

	/**
	 * @return the ltlProduct
	 */
	public boolean isLtlProduct() {
		return ltlProduct;
	}

	/**
	 * @param pLtlProduct the ltlProduct to set
	 */
	public void setLtlProduct(boolean pLtlProduct) {
		ltlProduct = pLtlProduct;
	}

	public boolean isCmokirschFlag() {
		return cmokirschFlag;
	}

	public void setCmokirschFlag(boolean cmokirschFlag) {
		this.cmokirschFlag = cmokirschFlag;
	}
	
	/**
	 * @return the bvReviews
	 */
	public BazaarVoiceProductVO getBvReviews() {
		return bvReviews;
	}

	/**
	 * @param bvReviews the bvReviews to set
	 */
	public void setBvReviews(BazaarVoiceProductVO bvReviews) {
		this.bvReviews = bvReviews;
	}
}
