/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBOrderTrackingManager.java
 *
 *  DESCRIPTION: BBBOrderTrackingManager contains APIs to fetch ATG Orders and Legacy orders from Webservice. 	
 *  HISTORY:
 *  12/07/2011 Initial version
 *
 */
package com.bbb.account;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import com.bbb.account.order.manager.OrderDetailsManager;
import com.bbb.account.vo.OrderTrackingRequestVO;
import com.bbb.account.vo.OrderTrackingResponseVO;
import com.bbb.account.vo.order.OrderDetailInfoReturn;
import com.bbb.browse.BazaarVoiceUtil;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.common.BBBOrderVO;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * @author Lokesh Duseja
 * 
 */
public class BBBOrderTrackingManager extends BBBGenericService {

	public static final String ORDER_EMAIL_NOT_ASSOCIATED = "order_email_not_associated";
	public static final String ORDER_NOT_FOUND = "No Order found with order id:";
	public static final String CONTACT_INFO_NOT_FOUND = "No contact info for the order.";


	private SiteContext mSiteContext;
	private String mServiceName;
	private BBBCatalogTools mCatalogTools;
	private OrderDetailsManager orderDetailsManager;
	private BBBOrderTools mOrderTools;
	public static final String BILLING_ADDRESS = "billingAddress";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String EMAIL = "email";
	public static final String TRACKING_ID = "trackingId";
	public static final String CARRIER_CODE = "carrierCode";
	public static final String SHIPMENT_TRACKING = "shipmentTracking";
	private String mShippingCarriers;
	private String emailMD5HashPrefix;

	public SiteContext getSiteContext() {
		return this.mSiteContext;
	}

	/**
	 * @param mSiteContext
	 *            the mSiteContext to set
	 */
	public void setSiteContext(SiteContext pSiteContext) {
		this.mSiteContext = pSiteContext;
	}

	/** @return the orderDetailsManager */
	public final OrderDetailsManager getOrderDetailsManager() {
		return this.orderDetailsManager;
	}

	/** @param orderDetailsManager the orderDetailsManager to set */
	public final void setOrderDetailsManager(final OrderDetailsManager orderDetailsManager) {
		this.orderDetailsManager = orderDetailsManager;
	}

	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param mCatalogTools
	 *            the mCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
	/**
	 * @return the emailMD5HashPrefix
	 */
	public String getEmailMD5HashPrefix() {
		return emailMD5HashPrefix;
	}

	/**
	 * @param emailMD5HashPrefix the emailMD5HashPrefix to set
	 */
	public void setEmailMD5HashPrefix(String emailMD5HashPrefix) {
		this.emailMD5HashPrefix = emailMD5HashPrefix;
	}

	/**
	 * This method gets ATG orders associated with email and order Id
	 * It was modified in R2.2 for the story 517B
	 * 
	 * UC_Anonymous_Order_Query
	 * 
	 * @param pOrderId
	 * @param pEmailId
	 * @return OrderTrackingResponseVO
	 */

	public BBBOrderVO getATGOrderDetails(final String pOrderId) throws BBBSystemException, BBBBusinessException {

		BBBOrderVO bbbOrderVO = null;
		BBBOrderImpl order = null;

		final String currentSiteId = SiteContextManager.getCurrentSiteId();
		order = (BBBOrderImpl) getOrderObject(pOrderId);

		if (currentSiteId.equals(order.getSiteId())) {
			final Date orderDate = order.getSubmittedDate();
			if (orderDate == null) {
				logDebug("Submitted Date for the order is null for the order:"
						+ pOrderId);
				throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1073, BBBCoreErrorConstants.ERR_NO_SUCH_ORDER);
			}
			// Fetch all the details for the Order
			bbbOrderVO = setBBBOrderVoDetails(order);
		}
		return bbbOrderVO;
	}
	
	
	/**
	 * This method gets ATG orders associated with email and order Id
	 * It was modified in R2.2 for the story 517B
	 * 
	 * UC_Anonymous_Order_Query
	 * 
	 * @param pOrderId
	 * @param pEmailId
	 * @return OrderTrackingResponseVO
	 */

	public BBBOrderVO getATGOrderTrackingDetails(final String pOrderId,
			final String pEmailId) throws BBBSystemException, BBBBusinessException {

		BBBOrderVO bbbOrderVO = null;
		BBBOrderImpl order = null;

		final String currentSiteId = SiteContextManager.getCurrentSiteId();
		order = (BBBOrderImpl) getOrderObject(pOrderId);
		
		String siteId = order.getSiteId();
		String sisterSiteId = null;
		if(siteId.startsWith(TBSConstants.TBS_PREFIX) ) {
			sisterSiteId = siteId.substring(TBSConstants.FOUR);
		}
		else {
			sisterSiteId = TBSConstants.TBS_SITE_PREFIX + siteId;
		}

		if (currentSiteId.equalsIgnoreCase(order.getSiteId()) || currentSiteId.equalsIgnoreCase(sisterSiteId)) {
			final Date orderDate = order.getSubmittedDate();
			if (orderDate == null) {
				logError("Submitted Date for the order is null for the order:"
						+ pOrderId);
				throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1073, BBBCoreErrorConstants.ERR_NO_SUCH_ORDER);
			}

			// Check Email from Billing address
			final BBBRepositoryContactInfo contactInfo = order.getBillingAddress();
			if (contactInfo != null) {
				if (!pEmailId.toLowerCase().equals(
						contactInfo.getEmail().toLowerCase())) {
					throw new BBBBusinessException(
							BBBCoreErrorConstants.ACCOUNT_ERROR_1074,
							ORDER_EMAIL_NOT_ASSOCIATED);
				}
			} else {
				logError("Contact Info (Billing Address) for order Id "+ pOrderId +"is null");
				throw new BBBBusinessException(
						BBBCoreErrorConstants.TRACKORDER_EXCEPTION,
						CONTACT_INFO_NOT_FOUND);
			}

			// Fetch all the details for the Order
			bbbOrderVO = setBBBOrderVoDetails(order);
		}
		return bbbOrderVO;
	}

	/**
	 * This method gets ATG orders associated with emailIDMD5 hash and order Id
	 * It was added in R2.2 for the story 517C
	 * @param pOrderId
	 * @param pEmailIdMD5
	 * @param order 
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public BBBOrderVO getATGOrderTrackingDetailsWithMd5(final String pOrderId, final String pEmailIdMD5, BBBOrderImpl order) throws BBBSystemException, BBBBusinessException {

		BBBOrderVO bbbOrderVO = null;

		final String currentSiteId = SiteContextManager.getCurrentSiteId();
		
		//BBBSL-11092 | Adding TBS site id check
		String siteId = order.getSiteId();
		String sisterSiteId = null;
		if(siteId.startsWith(TBSConstants.TBS_PREFIX) ) {
			sisterSiteId = siteId.substring(TBSConstants.FOUR);
			logDebug("sisterSiteId::"+sisterSiteId);
		}
		else {
			sisterSiteId = TBSConstants.TBS_SITE_PREFIX + siteId;
			logDebug("sisterSiteId::"+sisterSiteId);
		}

		if (currentSiteId.equals(order.getSiteId())  || currentSiteId.equalsIgnoreCase(sisterSiteId)) {
			final Date orderDate = order.getSubmittedDate();
			if (orderDate == null) {
				logDebug("Submitted Date for the order is null for the order:"
						+ pOrderId);
				throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1073, BBBCoreErrorConstants.ERR_NO_SUCH_ORDER);
			}

			// Check Email from Billing address
			final BBBRepositoryContactInfo contactInfo = order.getBillingAddress();
			if (contactInfo != null) {
			
					logDebug("ATG email id -- "+contactInfo.getEmail());
					logDebug("ATG email id for comparison -- "+getEmailMD5HashPrefix()+contactInfo.getEmail());
				
				if (!pEmailIdMD5.equals(BazaarVoiceUtil.generateMD5((getEmailMD5HashPrefix()+contactInfo.getEmail()).toLowerCase()))) {
					throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1074, ORDER_EMAIL_NOT_ASSOCIATED);
				}
			} else {
				throw new BBBBusinessException(
						BBBCoreErrorConstants.TRACKORDER_EXCEPTION,
						CONTACT_INFO_NOT_FOUND);
			}

			// Fetch all the details for the Order
			bbbOrderVO = setBBBOrderVoDetails(order);
		}
		return bbbOrderVO;
	}

	
	private BBBOrderVO setBBBOrderVoDetails(BBBOrderImpl order) throws BBBSystemException, BBBBusinessException {
		BBBOrderVO bbbOrderVO = null;
		try {
			bbbOrderVO = this.getOrderDetailsManager().getOrderDetailsVO(order, false);
			// Removing Secure Information
			bbbOrderVO.setBillingAddress(new BBBRepositoryContactInfo());
			bbbOrderVO.setPaymentGroups(null);
			bbbOrderVO.setAppliedCouponsVO(null);
			bbbOrderVO.setAffiliate(null);
			bbbOrderVO.setProfileId(null);
			
			Map<String, RegistrySummaryVO> registryMap = new HashMap<String, RegistrySummaryVO>();
			for (Map.Entry<String, RegistrySummaryVO> entry : bbbOrderVO.getRegistryMap().entrySet()) {
			    String key = entry.getKey();
			    RegistrySummaryVO value = entry.getValue();
			    
			    // Removing Secure Information
			    RegistrySummaryVO newValue = new RegistrySummaryVO();
			    newValue.setCoRegistrantFirstName(value.getCoRegistrantFirstName());
			    newValue.setCoRegistrantLastName(value.getCoRegistrantLastName());
			    newValue.setCoRegistrantFullName(value.getCoRegistrantFullName());

			    newValue.setPrimaryRegistrantFirstName(value.getPrimaryRegistrantFirstName());
			    newValue.setPrimaryRegistrantLastName(value.getPrimaryRegistrantLastName());
			    newValue.setPrimaryRegistrantFullName(value.getPrimaryRegistrantFullName());
			    
			    newValue.setRegistryId(value.getRegistryId());
			    newValue.setRegistryInfo(value.getRegistryInfo());
			    newValue.setRegistryType(value.getRegistryType());
			    
			    registryMap.put(key, newValue);
			}
			bbbOrderVO.setRegistryMap(registryMap);
			
			
		} catch (CommerceException e) {
			this.logDebug("Commerce Exception while getting current order details", e);
		}
		return bbbOrderVO;
	}

	/**
	 * This method gets legacy orders from Web Service with email and order Id
 	 * It was modified in R2.2 for the story 517B
 	 * 
	 * 
	 * @param pOrderId
	 * @param pEmailId
	 * @return OrderTrackingResponseVO
	 */

	public OrderDetailInfoReturn getLegacyOrderTrackingDetails(
			final String pOrderId, final String pEmailId) throws BBBSystemException,
			BBBBusinessException {

		OrderDetailInfoReturn objOrderDetailRes = new OrderDetailInfoReturn();
		if (!BBBUtility.isEmpty(pOrderId) && !BBBUtility.isEmpty(pEmailId)) {
			//Check Legacy || Setting request VO for Web Service
			final OrderTrackingRequestVO requestVO = new OrderTrackingRequestVO();
			final String siteId = this.mSiteContext.getSite().getId();

			if (this.getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId) != null
					&& this.getCatalogTools().getAllValuesForKey(
							BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId)
							.size() > BBBCoreConstants.ZERO) {
				requestVO.setSiteFlag(this.getCatalogTools().getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(
								BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1070,
						"Site Flag for webservices is not set for siteId: "
								+ siteId);
			}

			if (this.getCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY,
					BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) != null
					&& this.getCatalogTools().getAllValuesForKey(
							BBBWebServiceConstants.TXT_WSDLKEY,
							BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).size() > BBBCoreConstants.ZERO) {
				requestVO.setUserToken(this.getCatalogTools().getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY,
						BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(
								BBBCoreConstants.ZERO));
			} else {
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1071,
						"Webservices Token for webservices is not set");
			}

			requestVO.setOrderNumber(pOrderId);
			requestVO.setEmailAddress(pEmailId);
			requestVO.setServiceName(getServiceName());

			logDebug("Going to invoke webservice for Order Tracking");
			logDebug("siteFlag: " + requestVO.getSiteFlag());
			logDebug("userToken: " + requestVO.getUserToken());
			logDebug("emailAddress: " + pEmailId);
			logDebug("OrderNumber: " + pOrderId);
			logDebug("ServiceName: " + getServiceName());
			OrderTrackingResponseVO responseVO = null;
			// Invoke the service handler which will call webservice
			responseVO = (OrderTrackingResponseVO) ServiceHandlerUtil
					.invoke(requestVO);
			if(!responseVO.getErrorStatus().isErrorExists()){
				// Get Legacy orders
				objOrderDetailRes = this.getOrderDetailsManager().getLegacyOrderDetail(pOrderId);
				if (null != objOrderDetailRes && objOrderDetailRes.getOrderInfo() != null) {
					String dateFormat = BBBCoreConstants.DATE_FORMAT;

					if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)){
						dateFormat = BBBCoreConstants.CA_DATE_FORMAT;
					}
					String orderStatus = objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().getOrderStatus();
					try {
						objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().setOrderStatus(BBBUtility.getTrackingOrderStatus(orderStatus, objOrderDetailRes,dateFormat));
					} catch (ParseException parseException) {
						logError("ParseException While invoking getTrackingOrderStatus in BBBUtility "+"siteId is " + siteId,parseException);
					}
					
					final String currentSiteId = SiteContextManager.getCurrentSiteId();
					String orderDt = objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().getOrderDt();
					//final String state = objOrderDetailRes.getShipping().getAddress().getState();
					Date orderDate = null;

					if (BBBUtility.isNotEmpty(orderDt)) {
						DateFormat formatter;
						try {
							formatter = new SimpleDateFormat(BBBCoreConstants.US_DATE_FORMAT);
							orderDate = (Date) formatter.parse(orderDt);
						} catch (ParseException e) {
							orderDate = null;
						}
					}
//					final String shippingMethod = objOrderDetailRes.getOrderInfo()
//							.getOrderHeaderInfo().getShipMethodCD();
//					if (BBBUtility.isNotEmpty(shippingMethod)) {
//						String shipMethodDescription = null;
//						final RepositoryItem shippingMethodRepsoitoryItem = this.mCatalogTools
//								.getShippingMethod(shippingMethod);
//						if (shippingMethodRepsoitoryItem != null && shippingMethodRepsoitoryItem
//								.getPropertyValue(BBBCatalogConstants
//										.SHIPPING_METHOD_DESCRIPTION_SHIPPING_PROPERTY_NAME) != null) {
//							shipMethodDescription = (String) shippingMethodRepsoitoryItem.getPropertyValue(
//									BBBCatalogConstants.SHIPPING_METHOD_DESCRIPTION_SHIPPING_PROPERTY_NAME);
//						}
//						objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().setShipMethod(shipMethodDescription);
//					}
//					if (BBBUtility.isEmpty(objOrderDetailRes.getShipping().getDeliveryDt())) {
//						final String expectedDeliveryDate = this.mCatalogTools
//								.getExpectedDeliveryDate(shippingMethod, state, currentSiteId, orderDate);
//						objOrderDetailRes.getShipping().setDeliveryDt(expectedDeliveryDate);
//					}
					if (currentSiteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
						if (BBBUtility.isNotEmpty(orderDt)) {
							orderDt = BBBUtility.convertDateToAppFormat(orderDt);
							objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().setOrderDt(orderDt);
						}
					}
					//To Do Removing Secure Information
					objOrderDetailRes.setBilling(null);
					objOrderDetailRes.setPayments(null);
				}
			} else {
				objOrderDetailRes.setStatus(responseVO.getErrorStatus());
			}
		} else {
			objOrderDetailRes = null;
		}
		return objOrderDetailRes;	
	}

	/**
	 * This method gets legacy orders from Web Service with email and order Id
 	 * It was added in R2.2 for the story 517C
	 * @param pOrderId
	 * @param pEmailIdMD5 the MD5 hash of the email Id to which the order is associated
	 * @param objOrderDetailRes 
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public OrderDetailInfoReturn getLegacyOrderTrackingDetailsWithEmailIdMD5(
			final String pOrderId, final String pEmailIdMD5, OrderDetailInfoReturn objOrderDetailRes) throws BBBSystemException,
			BBBBusinessException {

		if (!BBBUtility.isEmpty(pOrderId) && !BBBUtility.isEmpty(pEmailIdMD5)) {
			String emailId = null;
			// Get Legacy orders
			if (null != objOrderDetailRes && objOrderDetailRes.getOrderInfo() != null) {
				final String currentSiteId = SiteContextManager.getCurrentSiteId();
				String orderDt = objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().getOrderDt();
				final String state = objOrderDetailRes.getShipping().getAddress().getState();
				Date orderDate = null;
				emailId = objOrderDetailRes.getBilling().getEmailAddr();
				logDebug("Legacy email id -- "+emailId);
				logDebug("Legacy email id for comparision -- "+(getEmailMD5HashPrefix()+emailId).toLowerCase());
				
				if (!pEmailIdMD5.equals(BazaarVoiceUtil.generateMD5((getEmailMD5HashPrefix()+emailId).toLowerCase()))) {
					throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1074, ORDER_EMAIL_NOT_ASSOCIATED);
				}
				if (BBBUtility.isNotEmpty(orderDt)) {
					DateFormat formatter;
					try {
						formatter = new SimpleDateFormat(BBBCoreConstants.US_DATE_FORMAT);
						orderDate = (Date) formatter.parse(orderDt);
					} catch (ParseException e) {
						orderDate = null;
					}
				}
				final String shippingMethod = objOrderDetailRes.getOrderInfo()
						.getOrderHeaderInfo().getShipMethodCD();
				if (BBBUtility.isNotEmpty(shippingMethod)) {
					String shipMethodDescription = null;
					final RepositoryItem shippingMethodRepsoitoryItem = this.mCatalogTools
							.getShippingMethod(shippingMethod);
					if (shippingMethodRepsoitoryItem != null && shippingMethodRepsoitoryItem
							.getPropertyValue(BBBCatalogConstants
									.SHIPPING_METHOD_DESCRIPTION_SHIPPING_PROPERTY_NAME) != null) {
						shipMethodDescription = (String) shippingMethodRepsoitoryItem.getPropertyValue(
								BBBCatalogConstants.SHIPPING_METHOD_DESCRIPTION_SHIPPING_PROPERTY_NAME);
					}
					objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().setShipMethod(shipMethodDescription);
				}
				if (BBBUtility.isEmpty(objOrderDetailRes.getShipping().getDeliveryDt())) {
					final String expectedDeliveryDate = this.mCatalogTools
							.getExpectedDeliveryDate(shippingMethod, state, currentSiteId, orderDate,false);
					objOrderDetailRes.getShipping().setDeliveryDt(expectedDeliveryDate);
				}
				//set valid tracking Order Status for Legacy Orders.
				final String siteId = this.mSiteContext.getSite().getId();
				String dateFormat = BBBCoreConstants.DATE_FORMAT;
				if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)){
					dateFormat = BBBCoreConstants.CA_DATE_FORMAT;
				}
				String orderStatus = objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().getOrderStatus();
				try {
					objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().setOrderStatus(BBBUtility.getTrackingOrderStatus(orderStatus, objOrderDetailRes,dateFormat));
				} catch (ParseException parseException) {
					logError("ParseException While invoking getTrackingOrderStatus in BBBUtility "+"siteId is " + siteId,parseException);
				}
				
				if(currentSiteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || currentSiteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)){
					if (BBBUtility.isNotEmpty(orderDt)) {
						orderDt = BBBUtility.convertDateToAppFormat(orderDt);
						objOrderDetailRes.getOrderInfo().getOrderHeaderInfo().setOrderDt(orderDt);
					}
				}
				//To Do Removing Secure Information
				objOrderDetailRes.setBilling(null);
				objOrderDetailRes.setPayments(null);
			}

		} else {
			objOrderDetailRes = null;
		}
		return objOrderDetailRes;	
	}

	/**
	 * @return the mServiceName
	 */
	public final String getServiceName() {
		return this.mServiceName;
	}

	/**
	 * @param pServiceName
	 *            the mServiceName to set
	 */
	public final void setServiceName(final String pServiceName) {
		this.mServiceName = pServiceName;
	}

	/**
	 * @param pOrderId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */

	public final Order getOrderObject(final String pOrderId) throws BBBBusinessException, BBBSystemException {

		logDebug((new StringBuilder()).append("orderId = ").append(pOrderId).toString());
		Order order = null;
		try {

			order = getOrderTools().getOrderFromOnlineOrBopusOrderNumber(
					pOrderId);

		} catch (CommerceException e) {
			logError(ORDER_NOT_FOUND + pOrderId,e);
			throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1073, BBBCoreErrorConstants.ERR_NO_SUCH_ORDER);
		} catch (RepositoryException e) {
			logError(
					LogMessageFormatter.formatMessage(
							null,
							"RepositoryException got from method getOrderTools()"
									+ ".getOrderFromOnlineOrBopusOrderNumber(pOrderId). Order Id: "
									+ pOrderId,
									BBBCoreErrorConstants.ACCOUNT_ERROR_1133), e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1072,
					"SystemException got from method getOrderTools()"
							+ ".getOrderFromOnlineOrBopusOrderNumber(pOrderId). Order Id: "
							+ pOrderId, e);
		}

		if (order == null) {
			logDebug(ORDER_NOT_FOUND + pOrderId);
			throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1073, BBBCoreErrorConstants.ERR_NO_SUCH_ORDER);
		}

		return order;

	}

	/**
	 * @return the mOrderTools
	 */
	public final BBBOrderTools getOrderTools() {
		return this.mOrderTools;
	}

	/**
	 * @param mOrderTools
	 *            the mOrderTools to set
	 */
	public final void setOrderTools(BBBOrderTools orderTools) {
		this.mOrderTools = orderTools;
	}

	/**
	 * @return the mShippingCarriers
	 */
	public final String getShippingCarriers() {
		return this.mShippingCarriers;
	}

	/**
	 * @param pShippingCarriers
	 *            the mShippingCarriers to set
	 */
	public final void setShippingCarriers(final String pShippingCarriers) {
		this.mShippingCarriers = pShippingCarriers;
	}

	/**
	 * This method is used to find if the productId passed is Active
	 * It is used to help the Mobile site in displaying the review button
	 * 
	 * @param pProductId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public final boolean isProductActive(final String pProductId) throws BBBSystemException, BBBBusinessException {
		return this.mCatalogTools.isProductActive(pProductId);
	}

	/**
	 * This method returns the productId for the corresponding skuId
	 * It is used by the Mobile Site to get the productId by providing the skuId.
	 * 
	 * @param pSkuId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public String getProductId(final String pSkuId) throws BBBSystemException, BBBBusinessException {
		return this.mCatalogTools.getParentProductForSku(pSkuId, false);
	}
}
