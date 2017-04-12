/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  OrderTrackingResponseVO.java
 *
 *  DESCRIPTION: OrderTrackingResponseVO : Response VO for order tracking web service  
 *  HISTORY:
 *  12/07/11 Initial version
 *
 */
package com.bbb.account.vo;

import java.util.Date;
import java.util.List;

import com.bbb.framework.integration.ServiceResponseBase;
import com.bbb.framework.webservices.vo.ErrorStatus;

public class OrderTrackingResponseVO extends ServiceResponseBase{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ErrorStatus mErrorStatus;
	private List<TrackingLinkVO> mTrackingLinks;
	private String mOrderNumber;
	private Date mOrderDate;
	private String mCustomerFirstName;
	private String mCustomerLastName;
	private String mOrderStatus;
	private String mStatusDate;
	private int mTotalTrackingLinks;
	/**
	 * @return the mCustomerFirstName
	 */
	public String getCustomerFirstName() {
		return mCustomerFirstName;
	}
	/**
	 * @param pCustomerFirstName the mCustomerFirstName to set
	 */
	public void setCustomerFirstName(String pCustomerFirstName) {
		this.mCustomerFirstName = pCustomerFirstName;
	}
	/**
	 * @return the mCustomerLastName
	 */
	public String getCustomerLastName() {
		return mCustomerLastName;
	}
	/**
	 * @param pCustomerLastName the mCustomerLastName to set
	 */
	public void setCustomerLastName(String pCustomerLastName) {
		this.mCustomerLastName = pCustomerLastName;
	}
	/**
	 * @return the mOrderStatus
	 */
	public String getOrderStatus() {
		return mOrderStatus;
	}
	/**
	 * @param pOrderStatus the mOrderStatus to set
	 */
	public void setOrderStatus(String pOrderStatus) {
		this.mOrderStatus = pOrderStatus;
	}
	/**
	 * @return the mStatusDate
	 */
	public String getStatusDate() {
		return mStatusDate;
	}
	/**
	 * @param pStatusDate the mStatusDate to set
	 */
	public void setStatusDate(String pStatusDate) {
		this.mStatusDate = pStatusDate;
	}
	/**
	 * @return the mTotalTrackingLinks
	 */
	public int getTotalTrackingLinks() {
		return mTotalTrackingLinks;
	}
	/**
	 * @param pTotalTrackingLinks the mTotalTrackingLinks to set
	 */
	public void setTotalTrackingLinks(int pTotalTrackingLinks) {
		this.mTotalTrackingLinks = pTotalTrackingLinks;
	}
	/**
	 * @return the mTrackingLinks
	 */
	public List<TrackingLinkVO> getTrackingLinks() {
		return mTrackingLinks;
	}
	/**
	 * @param pTrackingLinks the mTrackingLinks to set
	 */
	public void setTrackingLinks(List<TrackingLinkVO> pTrackingLinks) {
		this.mTrackingLinks = pTrackingLinks;
	}
	/**
	 * @return the mOrderNumber
	 */
	public String getOrderNumber() {
		return mOrderNumber;
	}
	/**
	 * @param pOrderNumber the mOrderNumber to set
	 */
	public void setOrderNumber(String pOrderNumber) {
		this.mOrderNumber = pOrderNumber;
	}
	/**
	 * @return the mOrderDate
	 */
	public Date getOrderDate() {
		return new Date(this.mOrderDate.getTime());
	}
	/**
	 * @param pOrderDate the mOrderDate to set
	 */
	public void setOrderDate(final Date pOrderDate) {
		
		this.mOrderDate = new Date(pOrderDate.getTime());
	}
	/**
	 * @return the mErrorStatus
	 */
	public ErrorStatus getErrorStatus() {
		return mErrorStatus;
	}
	/**
	 * @param pErrorStatus the mErrorStatus to set
	 */
	public void setErrorStatus(ErrorStatus pErrorStatus) {
		this.mErrorStatus = pErrorStatus;
	}
	
	
}
