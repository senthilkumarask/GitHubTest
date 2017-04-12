package com.bbb.commerce.order;

import java.io.Serializable;

import com.bbb.ecommerce.order.BBBOrderImpl;

public class PayAtRegisterBeanInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * mOrder property to hold BBBOrderImpl
	 */
	private BBBOrderImpl mOrder;
	/**
	 * mAmount property to hold Amount
	 */
	private double mAmount;
	/**
	 * mPaymentGroupId property to hold PaymentGroupId
	 */
	private String mPaymentGroupId;
	/**
	 * mSiteID property to hold SiteID
	 */
	private String mSiteID;
	
	
	/**
	 * @return the order
	 */
	public BBBOrderImpl getOrder() {
		return mOrder;
	}
	/**
	 * @param pOrder the order to set
	 */
	public void setOrder(BBBOrderImpl pOrder) {
		mOrder = pOrder;
	}
	/**
	 * @return the amount
	 */
	public double getAmount() {
		return mAmount;
	}
	/**
	 * @return the paymentGroupId
	 */
	public String getPaymentGroupId() {
		return mPaymentGroupId;
	}
	/**
	 * @param pAmount the amount to set
	 */
	public void setAmount(double pAmount) {
		mAmount = pAmount;
	}
	/**
	 * @param pPaymentGroupId the paymentGroupId to set
	 */
	public void setPaymentGroupId(String pPaymentGroupId) {
		mPaymentGroupId = pPaymentGroupId;
	}
	/**
	 * @return the siteID
	 */
	public String getSiteID() {
		return mSiteID;
	}
	/**
	 * @param pSiteID the siteID to set
	 */
	public void setSiteID(String pSiteID) {
		mSiteID = pSiteID;
	}
	
}
