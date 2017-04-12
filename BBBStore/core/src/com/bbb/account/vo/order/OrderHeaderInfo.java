package com.bbb.account.vo.order;

import java.io.Serializable;

/**
 * OrderHeaderInfo.java
 *
 * This file is part of OrderDetails webservice response
 * 
 */


public class OrderHeaderInfo  implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int cybsAuthAttempts;

    private long mbrID;

    private double shipSurchargeTotalAmt;

    private double shipAmt;

    private double giftPkgAmt;

    private double taxAmt;
    
    private double stateTax;

    private double countyTax;

    private String countyTaxLbl;

    private double subTotalAmt;

    private double totalAmt;

    private String originCd;

    private String cybsTaxFlag;

    private String orderDt;

    private String couponCd;

    private String shipMethodCD;

    private String shipMethod;

    private String promoEmailFlag;

    private String partnerCD;

    private String shippingPromoCD;

    private String giftPkgPromoCD;

    private String orderStatus;

    private String statusDt;

    private String emailAddr;

    private String taxMessage;

	/**
	 * @return the cybsAuthAttempts
	 */
	public int getCybsAuthAttempts() {
		return cybsAuthAttempts;
	}

	/**
	 * @param cybsAuthAttempts the cybsAuthAttempts to set
	 */
	public void setCybsAuthAttempts(int cybsAuthAttempts) {
		this.cybsAuthAttempts = cybsAuthAttempts;
	}

	/**
	 * @return the mbrID
	 */
	public long getMbrID() {
		return mbrID;
	}

	/**
	 * @param mbrID the mbrID to set
	 */
	public void setMbrID(long mbrID) {
		this.mbrID = mbrID;
	}

	/**
	 * @return the shipSurchargeTotalAmt
	 */
	public double getShipSurchargeTotalAmt() {
		return shipSurchargeTotalAmt;
	}

	/**
	 * @param shipSurchargeTotalAmt the shipSurchargeTotalAmt to set
	 */
	public void setShipSurchargeTotalAmt(double shipSurchargeTotalAmt) {
		this.shipSurchargeTotalAmt = shipSurchargeTotalAmt;
	}

	/**
	 * @return the shipAmt
	 */
	public double getShipAmt() {
		return shipAmt;
	}

	/**
	 * @param shipAmt the shipAmt to set
	 */
	public void setShipAmt(double shipAmt) {
		this.shipAmt = shipAmt;
	}

	/**
	 * @return the giftPkgAmt
	 */
	public double getGiftPkgAmt() {
		return giftPkgAmt;
	}

	/**
	 * @param giftPkgAmt the giftPkgAmt to set
	 */
	public void setGiftPkgAmt(double giftPkgAmt) {
		this.giftPkgAmt = giftPkgAmt;
	}

	/**
	 * @return the taxAmt
	 */
	public double getTaxAmt() {
		return taxAmt;
	}

	/**
	 * @param taxAmt the taxAmt to set
	 */
	public void setTaxAmt(double taxAmt) {
		this.taxAmt = taxAmt;
	}
	
	/**
	 * @return the stateTax
	 */
	public double getStateTax() {
		return stateTax;
	}

	/**
	 * @param stateTax the stateTax to set
	 */
	public void setStateTax(double stateTax) {
		this.stateTax = stateTax;
	}

	/**
	 * @return the countyTax
	 */
	public double getCountyTax() {
		return countyTax;
	}

	/**
	 * @param countyTax the countyTax to set
	 */
	public void setCountyTax(double countyTax) {
		this.countyTax = countyTax;
	}

	/**
	 * @return the countyTaxLbl
	 */
	public String getCountyTaxLbl() {
		return countyTaxLbl;
	}

	/**
	 * @param countyTaxLbl the countyTaxLbl to set
	 */
	public void setCountyTaxLbl(String countyTaxLbl) {
		this.countyTaxLbl = countyTaxLbl;
	}

	/**
	 * @return the subTotalAmt
	 */
	public double getSubTotalAmt() {
		return subTotalAmt;
	}

	/**
	 * @param subTotalAmt the subTotalAmt to set
	 */
	public void setSubTotalAmt(double subTotalAmt) {
		this.subTotalAmt = subTotalAmt;
	}

	/**
	 * @return the totalAmt
	 */
	public double getTotalAmt() {
		return totalAmt;
	}

	/**
	 * @param totalAmt the totalAmt to set
	 */
	public void setTotalAmt(double totalAmt) {
		this.totalAmt = totalAmt;
	}

	/**
	 * @return the originCd
	 */
	public String getOriginCd() {
		return originCd;
	}

	/**
	 * @param originCd the originCd to set
	 */
	public void setOriginCd(String originCd) {
		this.originCd = originCd;
	}

	/**
	 * @return the cybsTaxFlag
	 */
	public String getCybsTaxFlag() {
		return cybsTaxFlag;
	}

	/**
	 * @param cybsTaxFlag the cybsTaxFlag to set
	 */
	public void setCybsTaxFlag(String cybsTaxFlag) {
		this.cybsTaxFlag = cybsTaxFlag;
	}

	/**
	 * @return the orderDt
	 */
	public String getOrderDt() {
		return orderDt;
	}

	/**
	 * @param orderDt the orderDt to set
	 */
	public void setOrderDt(String orderDt) {
		this.orderDt = orderDt;
	}

	/**
	 * @return the couponCd
	 */
	public String getCouponCd() {
		return couponCd;
	}

	/**
	 * @param couponCd the couponCd to set
	 */
	public void setCouponCd(String couponCd) {
		this.couponCd = couponCd;
	}

	/**
	 * @return the shipMethodCD
	 */
	public String getShipMethodCD() {
		return shipMethodCD;
	}

	/**
	 * @param shipMethodCD the shipMethodCD to set
	 */
	public void setShipMethodCD(String shipMethodCD) {
		this.shipMethodCD = shipMethodCD;
	}

	/**
	 * @return the shipMethod
	 */
	public String getShipMethod() {
		return shipMethod;
	}

	/**
	 * @param shipMethod the shipMethod to set
	 */
	public void setShipMethod(String shipMethod) {
		this.shipMethod = shipMethod;
	}

	/**
	 * @return the promoEmailFlag
	 */
	public String getPromoEmailFlag() {
		return promoEmailFlag;
	}

	/**
	 * @param promoEmailFlag the promoEmailFlag to set
	 */
	public void setPromoEmailFlag(String promoEmailFlag) {
		this.promoEmailFlag = promoEmailFlag;
	}

	/**
	 * @return the partnerCD
	 */
	public String getPartnerCD() {
		return partnerCD;
	}

	/**
	 * @param partnerCD the partnerCD to set
	 */
	public void setPartnerCD(String partnerCD) {
		this.partnerCD = partnerCD;
	}

	/**
	 * @return the shippingPromoCD
	 */
	public String getShippingPromoCD() {
		return shippingPromoCD;
	}

	/**
	 * @param shippingPromoCD the shippingPromoCD to set
	 */
	public void setShippingPromoCD(String shippingPromoCD) {
		this.shippingPromoCD = shippingPromoCD;
	}

	/**
	 * @return the giftPkgPromoCD
	 */
	public String getGiftPkgPromoCD() {
		return giftPkgPromoCD;
	}

	/**
	 * @param giftPkgPromoCD the giftPkgPromoCD to set
	 */
	public void setGiftPkgPromoCD(String giftPkgPromoCD) {
		this.giftPkgPromoCD = giftPkgPromoCD;
	}

	/**
	 * @return the orderStatus
	 */
	public String getOrderStatus() {
		return orderStatus;
	}

	/**
	 * @param orderStatus the orderStatus to set
	 */
	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	/**
	 * @return the statusDt
	 */
	public String getStatusDt() {
		return statusDt;
	}

	/**
	 * @param statusDt the statusDt to set
	 */
	public void setStatusDt(String statusDt) {
		this.statusDt = statusDt;
	}

	/**
	 * @return the emailAddr
	 */
	public String getEmailAddr() {
		return emailAddr;
	}

	/**
	 * @param emailAddr the emailAddr to set
	 */
	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

	/**
	 * @return the taxMessage
	 */
	public String getTaxMessage() {
		return taxMessage;
	}

	/**
	 * @param taxMessage the taxMessage to set
	 */
	public void setTaxMessage(String taxMessage) {
		this.taxMessage = taxMessage;
	}

}
