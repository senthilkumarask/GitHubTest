package com.bbb.commerce.common;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import atg.core.util.Address;

import com.bbb.account.vo.order.ShipmentTrackingInfoVO;

/**
 * This is a custom class for shipping group details.
 * 
 * @author msiddi
 * @version $Revision: #1 $
 */

public class ShippingGroupDisplayVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* MEMBER VARIABLES */

	// private boolean getGiftWrapInd;
	private boolean containsGiftWrap;

	private boolean containsGiftWrapMessage;
	private String giftWrapMessage;
	private String packAndHoldDate;
	private Map<String, String> ecoFeeItemMap;
	private String shippingGroupType;
	private String storeId;
	private String shippingMethod;
	private String shippingMethodDescription;
	private Address shippingAddress;
	private String id;
	private String commerceItemCount;
	private String phoneNumber;
	private String email;
	private String commerceItemRelationshipCount;
	private List<ShippingGrpCIRelationshipVO> commerceItemRelationshipVOList;

	private PriceInfoDisplayVO shippingPriceInfoDisplayVO;
	private List<ShipmentTrackingInfoVO> trackingInfoVOList;
	private String expectedDeliveryDate;
	private String sourceId;
	private String ltlEmail;
	private String alternatePhoneNumber;
	private String mobilePhoneNumber;
	
	/*
	 * ===================================================== * GETTERS and *
	 * SETTERS * =====================================================
	 */


	public String getMobilePhoneNumber() {
		return mobilePhoneNumber;
	}

	public void setMobilePhoneNumber(String mobilePhoneNumber) {
		this.mobilePhoneNumber = mobilePhoneNumber;
	}

	public String getAlternatePhoneNumber() {
		return alternatePhoneNumber;
	}

	public void setAlternatePhoneNumber(String alternatePhoneNumber) {
		this.alternatePhoneNumber = alternatePhoneNumber;
	}

	public String getLtlEmail() {
		return ltlEmail;
	}

	public void setLtlEmail(String ltlEmail) {
		this.ltlEmail = ltlEmail;
	}

	

	

	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public List<ShipmentTrackingInfoVO> getTrackingInfoVOList() {
		return trackingInfoVOList;
	}

	public void setTrackingInfoVOList(List<ShipmentTrackingInfoVO> trackingInfoVOList) {
		this.trackingInfoVOList = trackingInfoVOList;
	}

	public void setExpectedDeliveryDate(String expectedDeliveryDate) {
		this.expectedDeliveryDate = expectedDeliveryDate;
	}

	public String getExpectedDeliveryDate() {
		return expectedDeliveryDate;
	}

	/*
	 * public boolean isGetGiftWrapInd() { return getGiftWrapInd; }
	 * 
	 * public void setGetGiftWrapInd(boolean getGiftWrapInd) {
	 * this.getGiftWrapInd = getGiftWrapInd; }
	 */

	public void setContainsGiftWrap(boolean containsGiftWrap) {
		this.containsGiftWrap = containsGiftWrap;
	}

	public boolean isContainsGiftWrap() {
		return containsGiftWrap;
	}

	public boolean isContainsGiftWrapMessage() {
		return containsGiftWrapMessage;
	}

	public void setContainsGiftWrapMessage(boolean containsGiftWrapMessage) {
		this.containsGiftWrapMessage = containsGiftWrapMessage;
	}

	public String getGiftWrapMessage() {
		return giftWrapMessage;
	}

	public void setGiftWrapMessage(String giftWrapMessage) {
		this.giftWrapMessage = giftWrapMessage;
	}

	public Map<String, String> getEcoFeeItemMap() {
		return ecoFeeItemMap;
	}

	public void setEcoFeeItemMap(Map<String, String> ecoFeeItemMap) {
		this.ecoFeeItemMap = ecoFeeItemMap;
	}

	public String getShippingMethod() {
		return shippingMethod;
	}

	public void setShippingMethod(String shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	/**
	 * @return the shippingMethodDescription
	 */
	public String getShippingMethodDescription() {
		return shippingMethodDescription;
	}

	/**
	 * @param shippingMethodDescription the shippingMethodDescription to set
	 */
	public void setShippingMethodDescription(String shippingMethodDescription) {
		this.shippingMethodDescription = shippingMethodDescription;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCommerceItemCount() {
		return commerceItemCount;
	}

	public void setCommerceItemCount(String commerceItemCount) {
		this.commerceItemCount = commerceItemCount;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCommerceItemRelationshipCount() {
		return commerceItemRelationshipCount;
	}

	public void setCommerceItemRelationshipCount(String commerceItemRelationshipCount) {
		this.commerceItemRelationshipCount = commerceItemRelationshipCount;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setPackAndHoldDate(String packAndHoldDate) {
		this.packAndHoldDate = packAndHoldDate;
	}

	public String getPackAndHoldDate() {
		return packAndHoldDate;
	}

	public void setShippingGroupType(String shippingGroupType) {
		this.shippingGroupType = shippingGroupType;
	}

	public String getShippingGroupType() {
		return shippingGroupType;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Address getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingPriceInfoDisplayVO(PriceInfoDisplayVO shippingPriceInfoDisplayVO) {
		this.shippingPriceInfoDisplayVO = shippingPriceInfoDisplayVO;
	}

	public PriceInfoDisplayVO getShippingPriceInfoDisplayVO() {
		return shippingPriceInfoDisplayVO;
	}

	public void setCommerceItemRelationshipVOList(List<ShippingGrpCIRelationshipVO> commerceItemRelationshipVOList) {
		this.commerceItemRelationshipVOList = commerceItemRelationshipVOList;
	}

	public List<ShippingGrpCIRelationshipVO> getCommerceItemRelationshipVOList() {
		return commerceItemRelationshipVOList;
	}

}
