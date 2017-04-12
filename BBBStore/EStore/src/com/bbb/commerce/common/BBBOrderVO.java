package com.bbb.commerce.common;

import java.util.List;
import java.util.Map;

import com.bbb.commerce.cart.bean.CommerceItemDisplayVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.order.bean.TrackingInfo;
import com.bbb.rest.checkout.vo.AppliedCouponsVO;

/**
 * This is a custom class for order details.
 * 
 * @author msiddi
 * @version $Revision: #1 $
 */
@SuppressWarnings("rawtypes")
public class BBBOrderVO {

	/*
	 * ===================================================== 
	 * * MEMBER VARIABLES
	 * =====================================================
	 */

	private String affiliate;
	private String schoolCoupon;
	private String schoolId;
	private String cartItemCount;
	private BBBRepositoryContactInfo billingAddress;
	private String orderType;
	private boolean bopusOrderFlag;
	private boolean transientFlag;
	private List<ShippingGroupDisplayVO> shippingGroups;
	@SuppressWarnings({ })
	private List paymentGroups;
	private PriceInfoDisplayVO orderPriceInfoDisplayVO;
	private List<CommerceItemDisplayVO> commerceItemVOList;
	private String orderId;
	private Map<String, String> carrierUrlMap;
	private List<TrackingInfo> trackingInfos;
	private String submittedDate;
	private String orderStatus;
	private String bopusOrderNumber;
	private String onlineOrderNumber;
	private Map<String, RegistrySummaryVO> registryMap;
	private String profileId;
	private AppliedCouponsVO appliedCouponsVO;
	private boolean singleShippingOrder;
	private boolean payPalOrder;
	private boolean orderContainLTLItem; //LTL
	private boolean expressCheckOut;
	private boolean emailSignUp;
	private String orderSubStatus;
	private boolean orderContainIntlRestrictedItem;
	private List<String> bopusListForIS;
	private boolean orderContainsOOSItem;
	private boolean eximWebserviceFailure;
	private boolean orderHasErrorPrsnlizedItem;
	private boolean singleItemCheckoutFlag;
	private String singleItemCheckoutUrl;
	private boolean orderHasPersonlizedItem;
	private boolean errorExist;
	private String errorMsg;
	private boolean evaluateBannerDetails;
	private boolean isRemovePorchService;
	private Map<String,String> porchRestrictedServiceAddress;
	private boolean porchRegistrantAddressRemoved;
	private boolean incartPriceOrder;


	public boolean isErrorExist() {
		return errorExist;
	}

	public void setErrorExist(boolean errorExist) {
		this.errorExist = errorExist;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean isOrderHasPersonlizedItem() {
		return orderHasPersonlizedItem;
	}

	public void setOrderHasPersonlizedItem(boolean orderHasPersonlizedItem) {
		this.orderHasPersonlizedItem = orderHasPersonlizedItem;
	}
	/**
	 * @return the orderContainsOOSItem
	 */
	public boolean isOrderContainsOOSItem() {
		return orderContainsOOSItem;
	}

	/**
	 * @param orderContainsOOSItem the orderContainsOOSItem to set
	 */
	public void setOrderContainsOOSItem(boolean orderContainsOOSItem) {
		this.orderContainsOOSItem = orderContainsOOSItem;
	}

	public List<String> getBopusListForIS() {
		return bopusListForIS;
	}

	public void setBopusListForIS(List<String> bopusListForIS) {
		this.bopusListForIS = bopusListForIS;
	}

	public String getOrderSubStatus() {
		return orderSubStatus;
	}

	public void setOrderSubStatus(String orderSubStatus) {
		this.orderSubStatus = orderSubStatus;
	}
	/**
	 * @return the payPalOrder
	 */
	public boolean isPayPalOrder() {
		return payPalOrder;
	}

	/**
	 * @param payPalOrder the payPalOrder to set
	 */
	public void setPayPalOrder(boolean payPalOrder) {
		this.payPalOrder = payPalOrder;
	}

	public AppliedCouponsVO getAppliedCouponsVO() {
		return this.appliedCouponsVO;
	}

	public void setAppliedCouponsVO(AppliedCouponsVO appliedCouponsVO) {
		this.appliedCouponsVO = appliedCouponsVO;
	}

	// variable for prop 65
	
	Map<String, Map> prop65DetailMap;
	
	private boolean isPackAndHoldFlag;


	public boolean isPackAndHoldFlag() {
		return this.isPackAndHoldFlag;
	}

	public void setPackAndHoldFlag(boolean isPackAndHoldFlag) {
		this.isPackAndHoldFlag = isPackAndHoldFlag;
	}

	/**
	 * @return the registryMap
	 */
	public Map<String, RegistrySummaryVO> getRegistryMap() {
		return this.registryMap;
	}

	/**
	 * @param registryMap the registryMap to set
	 */
	public void setRegistryMap(Map<String, RegistrySummaryVO> registryMap) {
		this.registryMap = registryMap;
	}
	

	/*
	 * ===================================================== 
	 * * GETTERS and * SETTERS 
	 * =====================================================
	 */
	
	
	public String getBopusOrderNumber() {
		return this.bopusOrderNumber;
	}

	public void setBopusOrderNumber(String bopusOrderNumber) {
		this.bopusOrderNumber = bopusOrderNumber;
	}

	public String getOnlineOrderNumber() {
		return this.onlineOrderNumber;
	}

	public void setOnlineOrderNumber(String onlineOrderNumber) {
		this.onlineOrderNumber = onlineOrderNumber;
	}

	public String getSubmittedDate() {
		return this.submittedDate;
	}

	public void setSubmittedDate(String submittedDate) {
		this.submittedDate = submittedDate;
	}

	public String getOrderStatus() {
		return this.orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Map<String, String> getCarrierUrlMap() {
		return this.carrierUrlMap;
	}

	public void setCarrierUrlMap(Map<String, String> carrierUrlMap) {
		this.carrierUrlMap = carrierUrlMap;
	}

	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public List<CommerceItemDisplayVO> getCommerceItemVOList() {
		return this.commerceItemVOList;
	}

	public void setCommerceItemVOList(List<CommerceItemDisplayVO> commerceItemVOList) {
		this.commerceItemVOList = commerceItemVOList;
	}

	public String getAffiliate() {
		return this.affiliate;
	}

	public void setAffiliate(String affiliate) {
		this.affiliate = affiliate;
	}

	public String getSchoolCoupon() {
		return this.schoolCoupon;
	}

	public void setSchoolCoupon(String schoolCoupon) {
		this.schoolCoupon = schoolCoupon;
	}

	public String getSchoolId() {
		return this.schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}

	public BBBRepositoryContactInfo getBillingAddress() {
		return this.billingAddress;
	}

	public void setBillingAddress(BBBRepositoryContactInfo billingAddress) {
		this.billingAddress = billingAddress;
	}

	public String getOrderType() {
		return this.orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public boolean isBopusOrderFlag() {
		return this.bopusOrderFlag;
	}

	public void setBopusOrderFlag(boolean bopusOrderFlag) {
		this.bopusOrderFlag = bopusOrderFlag;
	}

	
	public void setTransientFlag(boolean transientFlag) {
		this.transientFlag = transientFlag;
	}

	public boolean isTransientFlag() {
		return this.transientFlag;
	}

	
	public void setShippingGroups(List<ShippingGroupDisplayVO> shippingGroups) {
		this.shippingGroups = shippingGroups;
	}

	public List<ShippingGroupDisplayVO> getShippingGroups() {
		return this.shippingGroups;
	}

	public void setPaymentGroups(List paymentGroups) {
		this.paymentGroups = paymentGroups;
	}

	public List getPaymentGroups() {
		return this.paymentGroups;
	}


	public void setOrderPriceInfoDisplayVO(PriceInfoDisplayVO orderPriceInfoDisplayVO) {
		this.orderPriceInfoDisplayVO = orderPriceInfoDisplayVO;
	}

	public PriceInfoDisplayVO getOrderPriceInfoDisplayVO() {
		return this.orderPriceInfoDisplayVO;
	}

	public void setCartItemCount(String cartItemCount) {
		this.cartItemCount = cartItemCount;
	}

	public String getCartItemCount() {
		return this.cartItemCount;
	}

	public void setTrackingInfos(List<TrackingInfo> trackingInfos) {
		this.trackingInfos = trackingInfos;
	}

	public List<TrackingInfo> getTrackingInfos() {
		return this.trackingInfos;
	}

	public Map<String, Map> getProp65DetailMap() {
		return this.prop65DetailMap;
	}

	public void setProp65DetailMap(Map<String, Map> prop65DetailMap) {
		this.prop65DetailMap = prop65DetailMap;
	}

	public String getProfileId() {
		return this.profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	/**
	 * @return the singleShippingOrder
	 */
	public boolean isSingleShippingOrder() {
		return singleShippingOrder;
	}

	/**
	 * @param singleShippingOrder the singleShippingOrder to set
	 */
	public void setSingleShippingOrder(boolean singleShippingOrder) {
		this.singleShippingOrder = singleShippingOrder;
	}

	/**
	 * Return true if order contain LTL item
	 * @return
	 */
	public boolean isOrderContainLTLItem() {
		return orderContainLTLItem;
}

	/**
	 * Set true if Order having LTL item
	 * @param orderContainLTLItem
	 */
	public void setOrderContainLTLItem(boolean orderContainLTLItem) {
		this.orderContainLTLItem = orderContainLTLItem;
	}

	public boolean isExpressCheckOut() {
		return expressCheckOut;
	}

	public void setExpressCheckOut(boolean expressCheckOut) {
		this.expressCheckOut = expressCheckOut;
	}

	/**
	 * @return the emailSignUp
	 */
	public boolean isEmailSignUp() {
		return emailSignUp;
	}

	/**
	 * @param emailSignUp the emailSignUp to set
	 */
	public void setEmailSignUp(boolean emailSignUp) {
		this.emailSignUp = emailSignUp;
	}

	public boolean isOrderContainIntlRestrictedItem() {
		return orderContainIntlRestrictedItem;
	}

	public void setOrderContainIntlRestrictedItem(
			boolean orderContainIntlRestrictedItem) {
		this.orderContainIntlRestrictedItem = orderContainIntlRestrictedItem;
	}

	/**
	 * @return the eximWebserviceFailure
	 */
	public boolean isEximWebserviceFailure() {
		return eximWebserviceFailure;
	}

	/**
	 * @param eximWebserviceFailure the eximWebserviceFailure to set
	 */
	public void setEximWebserviceFailure(boolean eximWebserviceFailure) {
		this.eximWebserviceFailure = eximWebserviceFailure;
	}

	/**
	 * @return the orderHasErrorPrsnlizedItem
	 */
	public boolean isOrderHasErrorPrsnlizedItem() {
		return orderHasErrorPrsnlizedItem;
	}

	/**
	 * @param orderHasErrorPrsnlizedItem the orderHasErrorPrsnlizedItem to set
	 */
	public void setOrderHasErrorPrsnlizedItem(boolean orderHasErrorPrsnlizedItem) {
		this.orderHasErrorPrsnlizedItem = orderHasErrorPrsnlizedItem;
	}

	public boolean isSingleItemCheckoutFlag() {
		return singleItemCheckoutFlag;
	}

	public void setSingleItemCheckoutFlag(boolean singleItemCheckoutFlag) {
		this.singleItemCheckoutFlag = singleItemCheckoutFlag;
	}

	public String getSingleItemCheckoutUrl() {
		return singleItemCheckoutUrl;
	}

	public void setSingleItemCheckoutUrl(String singleItemCheckoutUrl) {
		this.singleItemCheckoutUrl = singleItemCheckoutUrl;
	}


	/**
	 * @return the porchRestrictedServiceAddress
	 */
	public Map<String, String> getPorchRestrictedServiceAddress() {
		return porchRestrictedServiceAddress;
	}

	/**
	 * @param porchRestrictedServiceAddress the porchRestrictedServiceAddress to set
	 */
	public void setPorchRestrictedServiceAddress(Map<String, String> porchRestrictedServiceAddress) {
		this.porchRestrictedServiceAddress = porchRestrictedServiceAddress;
	}

	/**
	 * @return the isRemovePorchService
	 */
	public boolean isRemovePorchService() {
		return isRemovePorchService;
	}

	/**
	 * @param isRemovePorchService the isRemovePorchService to set
	 */
	public void setRemovePorchService(boolean isRemovePorchService) {
		this.isRemovePorchService = isRemovePorchService;
	}

	/**
	 * @return the porchRegistrantAddressRemoved
	 */
	public boolean isPorchRegistrantAddressRemoved() {
		return porchRegistrantAddressRemoved;
	}

	/**
	 * @param porchRegistrantAddressRemoved the porchRegistrantAddressRemoved to set
	 */
	public void setPorchRegistrantAddressRemoved(boolean porchRegistrantAddressRemoved) {
		this.porchRegistrantAddressRemoved = porchRegistrantAddressRemoved;
	}

	public boolean isIncartPriceOrder() {
		return incartPriceOrder;
	}
	

	public void setIncartPriceOrder(boolean incartPriceOrder) {
		this.incartPriceOrder = incartPriceOrder;
	}
	

}
