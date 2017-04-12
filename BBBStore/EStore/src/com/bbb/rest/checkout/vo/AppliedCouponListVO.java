package com.bbb.rest.checkout.vo;
import java.io.Serializable;
import java.util.List;

import com.bbb.account.vo.RedemptionCodeVO;


/**
 * VO to get all credit card information
 * @author sku134
 *
 */
public class AppliedCouponListVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean selected;
	private String index;
	private String couponId;
	private String displayName;
	private String description;
	private String typeDetail;
	private String mCouponsExclusions;
	private String expiryDate;
	private String uniqueCouponCd;
	private String couponType;
	private int expiryCount;
	private String couponsImageUrl;
	private String redemptionChannel;
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
	private String promoId;
	private String displayExpiryDate;
	
	

	public String getPromoId() {
		return promoId;
	}

	public void setPromoId(String promoId) {
		this.promoId = promoId;
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
	public List<RedemptionCodeVO> getRedemptionCodesVO() {
		return redemptionCodesVO;
	}
	public void setRedemptionCodesVO(List<RedemptionCodeVO> redemptionCodesVO) {
		this.redemptionCodesVO = redemptionCodesVO;
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

	public String getCouponsImageUrl() {
		return couponsImageUrl;
	}

	public void setCouponsImageUrl(String couponsImageUrl) {
		this.couponsImageUrl = couponsImageUrl;
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
	
	public String getmCouponsExclusions() {
		return mCouponsExclusions;
	}
	public void setmCouponsExclusions(String mCouponsExclusions) {
		this.mCouponsExclusions = mCouponsExclusions;
	}

	public String getUniqueCouponCd() {
		return uniqueCouponCd;
	}
	public void setUniqueCouponCd(String uniqueCouponCd) {
		this.uniqueCouponCd = uniqueCouponCd;
	}
	public String getCouponType() {
		return couponType;
	}
	public void setCouponType(String couponType) {
		this.couponType = couponType;
	}
	
	
	public String getTypeDetail() {
		return typeDetail;
	}
	public void setTypeDetail(String typeDetail) {
		this.typeDetail = typeDetail;
	}

	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getCouponId() {
		return couponId;
	}
	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @param pCouponsExclusions the couponsExclusions to set
	 */
	public void setCouponsExclusions(String pCouponsExclusions) {
		mCouponsExclusions = pCouponsExclusions;
	}
	/**
	 * @return the couponsExclusions
	 */
	public String getCouponsExclusions() {
		return mCouponsExclusions;
	}

	public String getDisplayExpiryDate() {
		return displayExpiryDate;
	}

	public void setDisplayExpiryDate(String displayExpiryDate) {
		this.displayExpiryDate = displayExpiryDate;
	}
	
	
}
