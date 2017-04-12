package com.bbb.selfservice.common;
//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Jaswinder Sidhu
//
//Created on: 03-November-2011
//--------------------------------------------------------------------------------

import java.io.Serializable;
import java.util.List;

public class StoreDetailsWrapper implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mCurrentPage;
	private int mTotalPageCount;
	private String mPageKey;
	// private String mDistanceUnit;
	private List<StoreDetails> mStoreDetails;
	private List<StoreAddressSuggestion> mStoreAddressSuggestion;
    private String babyCanadaFlag;
	private String labelForMobileCanada;
	private int babyCanadaStoreCount;
	private int bedBathCanadaStoreCount;
	private String radius;
	private String inventoryNotAvailable;
	/*private Map<String,Integer> productStatusMap;*/
	private String errorInViewAllStores;
	private String errorInViewFavStores;
	private String errorIneligibleStates;
	private String favStoreId;	
	private String emptyInput;
	private String emptyDOMResponse;
	
	/**
	 * @param pCurrentPage
	 * @param pTotalPageCount
	 * @param pPageKey
	 * @param pStoreDetails
	 */
	public StoreDetailsWrapper(int pCurrentPage, int pTotalPageCount,
			String pPageKey, List<StoreDetails> pStoreDetails) {
		super();
		mCurrentPage = pCurrentPage;
		mTotalPageCount = pTotalPageCount;
		mPageKey = pPageKey;
		mStoreDetails = pStoreDetails;
	}
	
	public StoreDetailsWrapper(List<StoreAddressSuggestion> pStoreAddressSuggestion) {
		super();
		mStoreAddressSuggestion = pStoreAddressSuggestion;
	}

	/**
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return mCurrentPage;
	}

	/**
	 * @param pCurrentPage
	 *            the currentPage to set
	 */
	public void setCurrentPage(int pCurrentPage) {
		mCurrentPage = pCurrentPage;
	}

	/**
	 * @return the totalPageCount
	 */
	public int getTotalPageCount() {
		return mTotalPageCount;
	}

	/**
	 * @param pTotalPageCount
	 *            the totalPageCount to set
	 */
	public void setTotalPageCount(int pTotalPageCount) {
		mTotalPageCount = pTotalPageCount;
	}

	/**
	 * @return the pageKey
	 */
	public String getPageKey() {
		return mPageKey;
	}

	/**
	 * @param pPageKey
	 *            the pageKey to set
	 */
	public void setPageKey(String pPageKey) {
		mPageKey = pPageKey;
	}

	/**
	 * @return the storeDetails
	 */
	public List<StoreDetails> getStoreDetails() {
		return mStoreDetails;
	}

	/**
	 * @param pStoreDetails
	 *            the storeDetails to set
	 */
	public void setStoreDetails(List<StoreDetails> pStoreDetails) {
		mStoreDetails = pStoreDetails;
	}

	/**
	 * @return the mStoreAddressSuggestion
	 */
	public List<StoreAddressSuggestion> getStoreAddressSuggestion() {
		return mStoreAddressSuggestion;
	}

	/**
	 * @param mStoreAddressSuggestion the mStoreAddressSuggestion to set
	 */
	public void setStoreAddressSuggestion(
			List<StoreAddressSuggestion> pStoreAddressSuggestion) {
		this.mStoreAddressSuggestion = pStoreAddressSuggestion;
	}

	public String getErrorInViewFavStores() {
		return errorInViewFavStores;
	}

	public void setErrorInViewFavStores(String errorInViewFavStores) {
		this.errorInViewFavStores = errorInViewFavStores;
	}

	public String getErrorIneligibleStates() {
		return errorIneligibleStates;
	}

	public void setErrorIneligibleStates(String errorIneligibleStates) {
		this.errorIneligibleStates = errorIneligibleStates;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder strBuild = new StringBuilder();
		strBuild.append("Current Page-").append(getCurrentPage()).append("--TotalPages-").append(getTotalPageCount()).append("--PageKey-").append(getPageKey()).append("--StoreRecords-").append(getStoreDetails()==null?"null":getStoreDetails().toString());
		return strBuild.toString();
	}

	public String getBabyCanadaFlag() {
		return babyCanadaFlag;
	}
	
	public void setBabyCanadaFlag(String babyCanadaFlag) {
		this.babyCanadaFlag = babyCanadaFlag;
	}

	public String getLabelForMobileCanada() {
		return labelForMobileCanada;
	}

	public void setLabelForMobileCanada(String labelForMobileCanada) {
		this.labelForMobileCanada = labelForMobileCanada;
	}
	
	public int getBabyCanadaStoreCount() {
		return babyCanadaStoreCount;
	}

	public void setBabyCanadaStoreCount(int babyCanadaStoreCount) {
		this.babyCanadaStoreCount = babyCanadaStoreCount;
	}

	public int getBedBathCanadaStoreCount() {
		return bedBathCanadaStoreCount;
	}

	public void setBedBathCanadaStoreCount(int bedBathCanadaStoreCount) {
		this.bedBathCanadaStoreCount = bedBathCanadaStoreCount;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public String getInventoryNotAvailable() {
		return inventoryNotAvailable;
	}

	public void setInventoryNotAvailable(String inventoryNotAvailable) {
		this.inventoryNotAvailable = inventoryNotAvailable;
	}

	public String getErrorInViewAllStores() {
		return errorInViewAllStores;
	}

	public void setErrorInViewAllStores(String errorInViewAllStores) {
		this.errorInViewAllStores = errorInViewAllStores;
	}

	public String getFavStoreId() {
		return favStoreId;
	}

	public void setFavStoreId(String favStoreId) {
		this.favStoreId = favStoreId;
	}

	public String getEmptyInput() {
		return emptyInput;
	}

	public void setEmptyInput(String emptyInput) {
		this.emptyInput = emptyInput;
	}
	public String getEmptyDOMResponse() {
		return emptyDOMResponse;
	}
	public void setEmptyDOMResponse(String emptyDOMResponse) {
		this.emptyDOMResponse = emptyDOMResponse;
	}
	
	

}
