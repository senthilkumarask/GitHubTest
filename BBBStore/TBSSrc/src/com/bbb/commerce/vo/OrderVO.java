package com.bbb.commerce.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class OrderVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * mOrderNumber property
	 */
	private String mOrderNumber;
	/**
	 * mSubmittedDate property
	 */
	private String mSubmittedDate;
	/**
	 * mFirstName property
	 */
	private String mFirstName;
	/**
	 * mLastName property
	 */
	private String mLastName;
	/**
	 * mEmailId property
	 */
	private String mEmailId;
	/**
	 * mOrderAmount property
	 */
	private double mOrderAmount;
	/**
	 * mOrderStatus property
	 */
	private String mOrderStatus;
	/**
	 * mStore property
	 */
	private String mStore;
	/**
	 * mAssociate property
	 */
	private String mAssociate;
	/**
	 * mOnlineOrderNumber property
	 */
	private String mOnlineOrderNumber;
	
	private Map<String, ShipVO>  shipMap = new HashMap<String, ShipVO>();
	
	/**
	 * @return the orderNumber
	 */
	public String getOrderNumber() {
		return mOrderNumber;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return mFirstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return mLastName;
	}
	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return mEmailId;
	}
	/**
	 * @return the orderAmount
	 */
	public double getOrderAmount() {
		return mOrderAmount;
	}
	/**
	 * @return the orderStatus
	 */
	public String getOrderStatus() {
		return mOrderStatus;
	}
	/**
	 * @param pOrderNumber the orderNumber to set
	 */
	public void setOrderNumber(String pOrderNumber) {
		mOrderNumber = pOrderNumber;
	}
	/**
	 * @param pFirstName the firstName to set
	 */
	public void setFirstName(String pFirstName) {
		mFirstName = pFirstName;
	}
	/**
	 * @param pLastName the lastName to set
	 */
	public void setLastName(String pLastName) {
		mLastName = pLastName;
	}
	/**
	 * @param pEmailId the emailId to set
	 */
	public void setEmailId(String pEmailId) {
		mEmailId = pEmailId;
	}
	/**
	 * @param pOrderAmount the orderAmount to set
	 */
	public void setOrderAmount(double pOrderAmount) {
		mOrderAmount = pOrderAmount;
	}
	/**
	 * @param pOrderStatus the orderStatus to set
	 */
	public void setOrderStatus(String pOrderStatus) {
		mOrderStatus = pOrderStatus;
	}
	/**
	 * @return the store
	 */
	public String getStore() {
		return mStore;
	}
	/**
	 * @return the associate
	 */
	public String getAssociate() {
		return mAssociate;
	}
	/**
	 * @param pStore the store to set
	 */
	public void setStore(String pStore) {
		mStore = pStore;
	}
	/**
	 * @param pAssociate the associate to set
	 */
	public void setAssociate(String pAssociate) {
		mAssociate = pAssociate;
	}
	/**
	 * @return the submittedDate
	 */
	public String getSubmittedDate() {
		return mSubmittedDate;
	}
	/**
	 * @param pSubmittedDate the submittedDate to set
	 */
	public void setSubmittedDate(String pSubmittedDate) {
		mSubmittedDate = pSubmittedDate;
	}
	/**
	 * @return the onlineOrderNumber
	 */
	public String getOnlineOrderNumber() {
		return mOnlineOrderNumber;
	}
	/**
	 * @param pOnlineOrderNumber the onlineOrderNumber to set
	 */
	public void setOnlineOrderNumber(String pOnlineOrderNumber) {
		mOnlineOrderNumber = pOnlineOrderNumber;
	}
	/**
	 * @return the shipMap
	 */
	public Map<String, ShipVO> getShipMap() {
		return shipMap;
	}
	/**
	 * @param pShipMap the shipMap to set
	 */
	public void setShipMap(Map<String, ShipVO> pShipMap) {
		shipMap = pShipMap;
	}
	
}
