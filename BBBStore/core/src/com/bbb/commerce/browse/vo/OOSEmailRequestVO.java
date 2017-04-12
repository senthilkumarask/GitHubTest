/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE: OOSEmailRequestVO.java
 *
 *  
 *  HISTORY:
 *  Rajesh Saini: Added New variables and there setter and getter
 *  
 *
 */
package com.bbb.commerce.browse.vo;

import javax.xml.datatype.XMLGregorianCalendar;

import com.bbb.framework.integration.ServiceRequestBase;

public class OOSEmailRequestVO extends ServiceRequestBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3675504005014591834L;

	private String serviceName = "oosTibcoMessage";

	@Override
	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(final String serviceName) {
		this.serviceName = serviceName;
	}

	private String skuId;

	private String productId;

	private String productName;

	private String emailAddr;

	private String custName;

	private String userIp;

	private transient XMLGregorianCalendar requestedDT;

	private transient XMLGregorianCalendar inStockNotifyDT;

	private transient XMLGregorianCalendar notice1DT;

	private transient XMLGregorianCalendar notice2DT;

	private transient XMLGregorianCalendar unsubscribeDT;

	private transient XMLGregorianCalendar finalNoticeDT;

	private String siteFlag;

	public String getSkuId() {
		return skuId;
	}

	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getEmailAddr() {
		return emailAddr;
	}

	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public XMLGregorianCalendar getRequestedDT() {
		return requestedDT;
	}

	public void setRequestedDT(XMLGregorianCalendar requestedDT) {
		this.requestedDT = requestedDT;
	}

	public XMLGregorianCalendar getInStockNotifyDT() {
		return inStockNotifyDT;
	}

	public void setInStockNotifyDT(XMLGregorianCalendar inStockNotifyDT) {
		this.inStockNotifyDT = inStockNotifyDT;
	}

	public XMLGregorianCalendar getNotice1DT() {
		return notice1DT;
	}

	public void setNotice1DT(XMLGregorianCalendar notice1dt) {
		notice1DT = notice1dt;
	}

	public XMLGregorianCalendar getNotice2DT() {
		return notice2DT;
	}

	public void setNotice2DT(XMLGregorianCalendar notice2dt) {
		notice2DT = notice2dt;
	}

	public XMLGregorianCalendar getUnsubscribeDT() {
		return unsubscribeDT;
	}

	public void setUnsubscribeDT(XMLGregorianCalendar unsubscribeDT) {
		this.unsubscribeDT = unsubscribeDT;
	}

	public XMLGregorianCalendar getFinalNoticeDT() {
		return finalNoticeDT;
	}

	public void setFinalNoticeDT(XMLGregorianCalendar finalNoticeDT) {
		this.finalNoticeDT = finalNoticeDT;
	}

	public String getSiteFlag() {
		return siteFlag;
	}

	public void setSiteFlag(String siteFlag) {
		this.siteFlag = siteFlag;
	}

}
