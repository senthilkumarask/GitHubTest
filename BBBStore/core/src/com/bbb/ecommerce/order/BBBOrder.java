package com.bbb.ecommerce.order;

import java.util.Date;
import java.util.List;
import java.util.Map;

import atg.commerce.order.Order;
import atg.repository.MutableRepositoryItem;

import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.TrackingInfo;

/**
 * 
 * @author manohar
 * @story UC_checkout_billing
 * @created 12/2/2011
 */
public interface BBBOrder extends Order{
	
	public static enum OrderType{ONLINE, HYBRID, BOPUS};
	
	public Map getAvailabilityMap();

	public void setAvailabilityMap(final Map availabilityMap);
	
	public BBBCommerceItem getMovedCommerceItem();
	
	public void setMovedCommerceItem(BBBCommerceItem movedCommerceItem);
	
	public boolean isMessageShownOnCart();
	
	public void setMessageShownOnCart(boolean isMessageShownOnCart);	

	public Map getCouponMap();

    public void setCouponMap(final Map registryMap);
	
	public Map getRegistryMap();

	public void setRegistryMap(final Map registryMap);
	
	public String getUserIP();
	
	public void setUserIP(String pUserIP);
	
	public String getOrderXML();
	
	public void setOrderXML(String pOrderXML);	

	public BBBRepositoryContactInfo getBillingAddress();
	
	public void setBillingAddress(BBBRepositoryContactInfo address);
	
	public BBBRepositoryContactInfo getShippingAddress();
	
	public String getSchoolId();
	
	public void setSchoolId(String pSchoolId);
	
	public String getSchoolCoupon();
	
	public void setSchoolCoupon(String pSchoolCoupon);
	
	
	public List<TrackingInfo> getTrackingInfos();	
	
	public boolean isBopusOrder();
	
	public OrderType getOrderType();	
	
	public String getOnlineBopusItemsStatusInOrder();
	
	public void setAffiliate(String affiliate);
	
	public String getAffiliate();
	
	public String getOnlineOrderNumber();
	
	public void setOnlineOrderNumber(String onlineOrderNumber);
	
	public String getBopusOrderNumber();
	
	public void setBopusOrderNumber(String bopusOrderNumber);

	public boolean isTaxCalculationFailure();
	
	public void setCavv(String cavv);
    
    public String getCavv();
    
    public void setEci(String eci);
    
    public String getEci();
    
    public void setXid(String xid);
    
    public String getXid();
    
    public void setErrorNo(String errorNo);
    
    public String getErrorNo();
    
    public void setErrorDesc(String errorDesc);
    
    public String getErrorDesc();
    
    public void setTransactionId(String transactionId);
    
    public String getTransactionId();
    
    public void setVbvOrderId(String vbvOrderId);
    
    public String getVbvOrderId();
    
    public void setEnrolled(String enrolled);
    
    public String getEnrolled();
    
    public void setSignatureVerification(String signatureVerification);
    
    public String getSignatureVerification();
    
    public void setPAResStatus(String pAResStatus);
    
    public String getPAResStatus();
    
    public void setCommerceIndicator(String commerceIndicator);
    
    public String getCommerceIndicator();

    public String getSubStatus();
    
    public void setSubStatus(String substatus);
    
	/**
	 * If shipping method is common across all hard good shipping groups then
	 * this method returns that shipping method name otherwise returns null.
	 * 
	 * @param pOrder
	 * @return
	 */
	public String isCommonShippingMethodAmongHGS();
	/**
	 * This methods returns all Map<HardGroupShippingGroupID,ShippingMethodName>.
	 * 
	 * @param pOrder
	 * @return
	 */

	public Map<String, String> getAllHGShippingMethodsFromOrder();

	public void setToken(String token);
	
	public Date getTimeStamp();

	public void setTimeStamp(Date currentDate);
	
	public void setInternationalOrderId(String orderId);
	public void setInternationalState(String orderState);
	public void setInternationalCurrencyCode(String currencyCode);
	public void setInternationalCountryCode(String countryCode);
	public void setInternationalOrderDate(Date submittedDate);
	public Date getInternationalOrderDate();
	public String getInternationalOrderId();
	
	public boolean isEximWebserviceFailure();
	
	public void setEximWebserviceFailure(boolean eximWebserviceFailure);	
	public boolean isTBSApprovalRequired();
	public void setTBSApprovalRequired(boolean isRequired);
	public boolean isTBSOrder();
	public void setTBSOrder(boolean isTBS);
	public String getTBSAssociateID();
	public void setTBSAssociateID(String associateID);
	public String getTBSApproverID();
	public void setTBSApproverID(String approverID);
	public String getTbsStoreNo();
	public void setTbsStoreNo(String pTbsStoreNo);
	public void setSalesOS(String salesOS);
	public String getSalesOS();
	
	public boolean isAutoWaiveFlag();
	public void setAutoWaiveFlag(boolean autoWaiveFlag);
	
	public void setAutoWaiveClassification(String classification);
	public String getAutoWaiveClassification();
	
	/**
	 * @return the anonymousOrderTaxItem
	 */
	public Map<String, MutableRepositoryItem> getAnonymousOrderTaxItem();

	/**
	 * @param anonymousOrderTaxItem
	 *            the anonymousOrderTaxItem to set
	 */
	public void setAnonymousOrderTaxItem(Map<String, MutableRepositoryItem> anonymousOrderTaxItem);
	
	public Map getTaxOverrideMap();
	public void setTaxOverrideMap(Map taxOverrideMap);
	
	public String getEmailAddress();
	public void setEmailAddress(String pEmailAddress);
	public void setOSUpdateListener(boolean isOSUpdateListener);
	public boolean isOSUpdateListener();
}
