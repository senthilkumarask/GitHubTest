package com.bbb.rest.selfservice;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.bbb.selfservice.common.StoreAddressSuggestion;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.common.StoreDetailsWrapper;

public class SearchInStoreVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, Integer> mProductStatusMap;
	private StoreDetailsWrapper mObjStoreDetails;
	private StoreDetails mStoreDetail;
	private List<StoreAddressSuggestion> mStoreAddressSuggestion;
	private boolean errorExist = false;
    private String errorCode = null;
    private String errorMessage = null;

	/**
	 * @return the storeDetail
	 */
	public StoreDetails getStoreDetail() {
		return mStoreDetail;
	}
	/**
	 * @param pStoreDetail the storeDetail to set
	 */
	public void setStoreDetail(StoreDetails pStoreDetail) {
		mStoreDetail = pStoreDetail;
	}
	/**
	 * @return the productStatusMap
	 */
	public Map<String, Integer> getProductStatusMap() {
		return mProductStatusMap;
	}
	/**
	 * @param pProductStatusMap the productStatusMap to set
	 */
	public void setProductStatusMap(Map<String, Integer> pProductStatusMap) {
		mProductStatusMap = pProductStatusMap;
	}
	/**
	 * @return the objStoreDetails
	 */
	public StoreDetailsWrapper getObjStoreDetails() {
		return mObjStoreDetails;
	}
	/**
	 * @param pObjStoreDetails the objStoreDetails to set
	 */
	public void setObjStoreDetails(StoreDetailsWrapper pObjStoreDetails) {
		mObjStoreDetails = pObjStoreDetails;
	}
	public List<StoreAddressSuggestion> getStoreAddressSuggestion() {
		return mStoreAddressSuggestion;
	}
	public void setStoreAddressSuggestion(
			List<StoreAddressSuggestion> mStoreAddressSuggestion) {
		this.mStoreAddressSuggestion = mStoreAddressSuggestion;
	}
	/**
	 * @return the errorExist
	 */
	public boolean isErrorExist() {
		return errorExist;
	}
	/**
	 * @param errorExist the errorExist to set
	 */
	public void setErrorExist(boolean errorExist) {
		this.errorExist = errorExist;
	}
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
