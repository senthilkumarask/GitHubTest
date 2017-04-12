/**
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 *
 * Reproduction or use of this file without explicit 
 * written consent is prohibited.
 *
 * Created by: syadav
 *
 * Created on: 26-December-2011
 * --------------------------------------------------------------------------------
 */
package com.bbb.account.vo;

//import java.util.Date;
import java.io.Serializable;
import java.util.List;
import com.bbb.account.vo.RedemptionCodeVO;

public class CouponListVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mEntryCd;
	private String mDescription;
	private String mCouponsDescription;
	private String mCouponsImageUrl;
	private String mCouponsExclusions;
	private boolean disqualify = false;
	private String promoItem;
	private String mPromoId;
	private String expiryDate;
	private String uniqueCouponCd;
	private String redemptionChannel;
	private int expiryCount;
	private String issueDate;
	private String effectiveDate;
	private String lastRedemptionDate;
	private String redemptionCount;
	private String redemptionLimit;
	private List<RedemptionCodeVO> redemptionCodesVO;
	private String headerText;
	private String headerImageURL;
	private String details;
	private String footer;
	private String couponExclusionsMessage;
	private boolean preloaded=false;
	private String displayExpiryDate;
	
	private String couponType;
	
	
	public boolean isPreloaded() {
		return preloaded;
	}

	public void setPreloaded(boolean preloaded) {
		this.preloaded = preloaded;
	}

	public List<RedemptionCodeVO> getRedemptionCodesVO() {
		return redemptionCodesVO;
	}

	public void setRedemptionCodesVO(List<RedemptionCodeVO> redemptionCodesVO) {
		this.redemptionCodesVO = redemptionCodesVO;
	}
	
	public String getmEntryCd() {
		return mEntryCd;
	}

	public void setmEntryCd(String mEntryCd) {
		this.mEntryCd = mEntryCd;
	}

	public String getmDescription() {
		return mDescription;
	}

	public void setmDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public String getmCouponsDescription() {
		return mCouponsDescription;
	}

	public void setmCouponsDescription(String mCouponsDescription) {
		this.mCouponsDescription = mCouponsDescription;
	}

	public String getmCouponsImageUrl() {
		return mCouponsImageUrl;
	}

	public void setmCouponsImageUrl(String mCouponsImageUrl) {
		this.mCouponsImageUrl = mCouponsImageUrl;
	}

	public String getmCouponsExclusions() {
		return mCouponsExclusions;
	}

	public void setmCouponsExclusions(String mCouponsExclusions) {
		this.mCouponsExclusions = mCouponsExclusions;
	}

	public String getmPromoId() {
		return mPromoId;
	}

	public void setmPromoId(String mPromoId) {
		this.mPromoId = mPromoId;
	}

	public String getRedemptionChannel() {
		return redemptionChannel;
	}

	public void setRedemptionChannel(String redemptionChannel) {
		this.redemptionChannel = redemptionChannel;
	}

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getLastRedemptionDate() {
		return lastRedemptionDate;
	}

	public void setLastRedemptionDate(String lastRedemptionDate) {
		this.lastRedemptionDate = lastRedemptionDate;
	}

	public String getRedemptionCount() {
		return redemptionCount;
	}

	public void setRedemptionCount(String redemptionCount) {
		this.redemptionCount = redemptionCount;
	}

	public String getRedemptionLimit() {
		return redemptionLimit;
	}

	public void setRedemptionLimit(String redemptionLimit) {
		this.redemptionLimit = redemptionLimit;
	}


	public String getHeaderText() {
		return headerText;
	}

	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	public String getHeaderImageURL() {
		return headerImageURL;
	}

	public void setHeaderImageURL(String headerImageURL) {
		this.headerImageURL = headerImageURL;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public String getCouponExclusionsMessage() {
		return couponExclusionsMessage;
	}

	public void setCouponExclusionsMessage(String couponExclusionsMessage) {
		this.couponExclusionsMessage = couponExclusionsMessage;
	}

	public int getExpiryCount() {
		return expiryCount;
	}

	public void setExpiryCount(int expiryCount) {
		this.expiryCount = expiryCount;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	
	


	

	public String getUniqueCouponCd() {
		return uniqueCouponCd;
	}

	public void setUniqueCouponCd(String uniqueCouponCd) {
		this.uniqueCouponCd = uniqueCouponCd;
	}


	/**
	 * returns the Entry cd
	 * @return
	 */
	public String getEntryCd() {
		return mEntryCd;
	}
	
	/**
	 * sets the Entry Cd
	 * @param mEntryCd
	 */
	public void setEntryCd(String pEntryCd) {
		this.mEntryCd = pEntryCd;
	}
	
	/**
	 * returns the description
	 * @return
	 */
	public String getDescription() {
		return mDescription;
	}
	
	/**
	 * sets the description for the coupon
	 * @param pDescription
	 */
	public void setDescription(String pDescription) {
		this.mDescription = pDescription;
	}

	/**
	 * @return the couponsDescription
	 */
	public String getCouponsDescription() {
		return mCouponsDescription;
	}

	/**
	 * @return the couponsImageUrl
	 */
	public String getCouponsImageUrl() {
		return mCouponsImageUrl;
	}

	/**
	 * @return the couponsExclusions
	 */
	public String getCouponsExclusions() {
		return mCouponsExclusions;
	}

	/**
	 * @param pCouponsDescription the couponsDescription to set
	 */
	public void setCouponsDescription(String pCouponsDescription) {
		mCouponsDescription = pCouponsDescription;
	}

	/**
	 * @param pCouponsImageUrl the couponsImageUrl to set
	 */
	public void setCouponsImageUrl(String pCouponsImageUrl) {
		mCouponsImageUrl = pCouponsImageUrl;
	}

	/**
	 * @param pCouponsExclusions the couponsExclusions to set
	 */
	public void setCouponsExclusions(String pCouponsExclusions) {
		mCouponsExclusions = pCouponsExclusions;
	}

	public void setDisqualify(boolean pB) {
		this.disqualify = pB;
		
	}

	/**
	 * @return the disqualify
	 */
	public boolean getDisqualify() {
		return disqualify;
	}

	/**
	 * @return the promoItem
	 */
	public String getPromoItem() {
		return promoItem;
	}

	/**
	 * @param promoItem the promoItem to set
	 */
	public void setPromoItem(String promoItem) {
		this.promoItem = promoItem;
	}

	/**
	 * @return the mPromoId
	 */

	public String getPromoId() {
		return mPromoId;
	}

	/**
	 * @param mPromoId
	 * the mPromoId to set
	 */

	public void setPromoId(String pPromoId) {
		this.mPromoId = pPromoId;
	}

	/**
	 * @return the couponType
	 */
	public String getCouponType() {
		return couponType;
	}

	/**
	 * @param couponType the couponType to set
	 */
	public void setCouponType(String couponType) {
		this.couponType = couponType;
	}

	public String getDisplayExpiryDate() {
		return displayExpiryDate;
	}

	public void setDisplayExpiryDate(String displayExpiryDate) {
		this.displayExpiryDate = displayExpiryDate;
	}

	@Override
	public String toString() {
		return "CouponListVo [mEntryCd=" + mEntryCd + ", mDescription="
				+ mDescription + ", mCouponsDescription=" + mCouponsDescription
				+ ", mCouponsImageUrl=" + mCouponsImageUrl
				+ ", mCouponsExclusions=" + mCouponsExclusions
				+ ", disqualify=" + disqualify + ", promoItem=" + promoItem
				+ ", mPromoId=" + mPromoId + ", expiryDate=" + expiryDate
				+ ", uniqueCouponCd=" + uniqueCouponCd + ", redemptionChannel="
				+ redemptionChannel + ", expiryCount=" + expiryCount
				+ ", issueDate=" + issueDate + ", effectiveDate="
				+ effectiveDate + ", lastRedemptionDate=" + lastRedemptionDate
				+ ", redemptionCount=" + redemptionCount + ", redemptionLimit="
				+ redemptionLimit + ", redemptionCodesVO=" + redemptionCodesVO
				+ ", headerText=" + headerText + ", headerImageURL="
				+ headerImageURL + ", details=" + details + ", footer="
				+ footer + ", couponExclusionsMessage="
				+ couponExclusionsMessage + ", preloaded=" + preloaded
				+ ", couponType=" + couponType + ", displayExpiryDate=" + displayExpiryDate + "]";
	}
	
	
}
