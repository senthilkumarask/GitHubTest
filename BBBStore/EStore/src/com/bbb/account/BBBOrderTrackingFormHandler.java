/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBOrderTrackingFormHandler.java
 *
 *  DESCRIPTION: BBBOrderTrackingFormHandler performs form handling activities related to order tracking form. 	
 *  HISTORY:
 *  12/07/11 Initial version
 *
 */

package com.bbb.account;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.vo.order.CartItemDetailInfo;
import com.bbb.account.vo.order.OrderDetailInfoReturn;
import com.bbb.browse.BazaarVoiceUtil;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.cart.bean.CommerceItemDisplayVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.common.BBBOrderVO;
import com.bbb.commerce.common.BBBTrackOrderVO;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.framework.webservices.vo.ValidationError;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;
import com.bbb.ecommerce.order.BBBOrderImpl;



public class BBBOrderTrackingFormHandler extends BBBGenericFormHandler {

	public static final String SUCCESS_URL_ATG = "track_order_guest.jsp";
	public static final String SUCCESS_URL_LEGACY = "track_order_guest_legacy.jsp";
	private String mTrackOrderSuccessURL;
	private String mTrackOrderErrorURL;
	private Map<String, String> mErrorMap;
	private BBBOrderTrackingManager mOrderTrackingManager;
	private BBBTrackOrderVO mBBBTrackOrderVO;
	private String mOrderId;
	private String mEmailId;
	private String mEmailIdMD5;
	private LblTxtTemplateManager mLblTxtTemplateManager;
	private String userTokenBVRR;
	private BBBCatalogTools mCatalogTools;
	private String emailMD5HashPrefix;

	/**
	 * @return the emailMD5HashPrefix
	 */
	public String getEmailMD5HashPrefix() {
		return this.emailMD5HashPrefix;
	}

	/**
	 * @param emailMD5HashPrefix the emailMD5HashPrefix to set
	 */
	public void setEmailMD5HashPrefix(String emailMD5HashPrefix) {
		this.emailMD5HashPrefix = emailMD5HashPrefix;
	}

	/**
	 * @return the lblTxtTemplateManager
	 */
	public final LblTxtTemplateManager getLblTxtTemplateManager() {
		return this.mLblTxtTemplateManager;
	}

	/**
	 * @param pLblTxtTemplateManager the lblTxtTemplateManager to set
	 */
	public void setLblTxtTemplateManager(LblTxtTemplateManager pLblTxtTemplateManager) {
		this.mLblTxtTemplateManager = pLblTxtTemplateManager;
	}

	/**
	 * This method performs the validation of email and order Id form fields.
	 * 
	 * UC_Anonymous_Order_Query
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 * @return void
	 */

	protected final void preTrackOrder(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.mErrorMap = new HashMap<String, String>();
		final String orderId = getOrderId();
		final String emailId = getEmailId();
		final String emailIdMD5 = getEmailIdMD5();
		final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		if (BBBUtility.isEmpty(emailId) && BBBUtility.isEmpty(emailIdMD5)) {
			final String error = this.getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERR_TRACKORDER_EMAIL_EMPTY,
					pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);						
			this.mErrorMap.put(BBBCoreConstants.ERROR, error);
			addFormException(new DropletException(error, BBBCoreErrorConstants.ERR_TRACKORDER_EMAIL_EMPTY));
		} else {
			if (!BBBUtility.isValidEmail(emailId) && BBBUtility.isEmpty(emailIdMD5)) {
				final String error = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERR_TRACKORDER_INVALID_EMAIL,
						pRequest.getLocale().getLanguage(),
						errorPlaceHolderMap, null);						
				this.mErrorMap.put(BBBCoreConstants.ERROR, error);
				addFormException(new DropletException(error, BBBCoreErrorConstants.ERR_TRACKORDER_INVALID_EMAIL));
			} else if (!BBBUtility.isValidMD5(emailIdMD5) && BBBUtility.isEmpty(emailId)){
				final String error = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERR_TRACKORDER_INVALID_EMAIL_MD5,
						pRequest.getLocale().getLanguage(),
						errorPlaceHolderMap, null);						
				this.mErrorMap.put(BBBCoreConstants.ERROR, error);
				addFormException(new DropletException(error, BBBCoreErrorConstants.ERR_TRACKORDER_INVALID_EMAIL_MD5));
			}
		}
		if (BBBUtility.isEmpty(orderId)) {	
			final String error = this.getLblTxtTemplateManager().getErrMsg(
					BBBCoreErrorConstants.ERR_TRACKORDER_ORDERID_EMPTY,
					pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);						
			this.mErrorMap.put(BBBCoreConstants.ERROR, error);
			addFormException(new DropletException(error, BBBCoreErrorConstants.ERR_TRACKORDER_ORDERID_EMPTY));
		} else {
			if (!BBBUtility.isValidOrderNumber(orderId.toUpperCase())) {	
				final String error = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.INVALID_ORDERID,
						pRequest.getLocale().getLanguage(),
						errorPlaceHolderMap, null);						
				this.mErrorMap.put(BBBCoreConstants.ERROR, error);
				addFormException(new DropletException(error, BBBCoreErrorConstants.INVALID_ORDERID));
			}
		}
	}


	/**
	 * Performs the following 
	 * 1. Call Manager's method for Order present in ATG with entered email and order Id 
	 * 2. If no ATG order found with entered fields then manager's method to get legacy orders is called  
	 * 
	 * UC_Anonymous_Order_Query
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return void
	 */

	public boolean handleGetOrderTrackingDetails(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException	{

		return this.handleTrackOrder(pRequest, pResponse);
	}


	/**
	 * Performs the following 
	 * 1. Call Manager's method for Order present in ATG with entered email and order Id 
	 * 2. If no ATG order found with entered fields then manager's method to get legacy orders is called  
	 * 
	 * 
	 * Changes were made to this method for R2.2 517B Story
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException 
	 * @throws IOException 
	 * @return void
	 */

	public boolean handleTrackOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		BBBPerformanceMonitor.start("BBBOrderTrackingFormHandler-handleTrackOrder:Start");
		final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
		this.preTrackOrder(pRequest, pResponse);

		final String orderId = getOrderId();
		final String emailId = getEmailId();
		final String emailIdMD5 = getEmailIdMD5();
		logDebug("In handleTrackOrder() Method for orderId : " + orderId);
		// Setting empty email id as order tracking doesn't require email address to be passed in Bazaar Voice UAS.
		//PS-18177. Updated UAS to generate token for BV
		String bazaarVoiceEmailId = "";
				
		if (this.mErrorMap.isEmpty() && !BBBUtility.isEmpty(orderId) && (!BBBUtility.isEmpty(emailId) || !BBBUtility.isEmpty(emailIdMD5))) {
			this.setBBBTrackOrderVO(new BBBTrackOrderVO());
			this.getBBBTrackOrderVO().setLegacyOrderFlag(BBBCoreConstants.RETURN_FALSE);
			final BBBOrderVO bbbOrderVO;
			try {
				// R2.2 - 517C start - If track order is called using email Id then get order details using email ID else get details using emailIdMD5 hash
				//PS-18177. Updated UAS to generate token for BV
				if (!BBBUtility.isEmpty(emailId)) {
					bbbOrderVO = this.mOrderTrackingManager.getATGOrderTrackingDetails(orderId, emailId);
					this.setUserTokenBVRR(BazaarVoiceUtil.createUserTokenBVRR(BazaarVoiceUtil.generateMD5((getEmailMD5HashPrefix()+emailId).toLowerCase()), this.getCatalogTools().getBazaarVoiceKey(),emailId.toLowerCase()));
					this.getBBBTrackOrderVO().setBvToken(this.getUserTokenBVRR());
					pRequest.getSession().setAttribute("userTokenBVRR", this.getUserTokenBVRR());
				} else {
					BBBOrderImpl order = (BBBOrderImpl) getOrderTrackingManager().getOrderObject(orderId);
					bbbOrderVO = this.mOrderTrackingManager.getATGOrderTrackingDetailsWithMd5(orderId, emailIdMD5, order);
					if(order != null && order.getBillingAddress() != null){
						bazaarVoiceEmailId = order.getBillingAddress().getEmail();
					}
					this.setUserTokenBVRR(BazaarVoiceUtil.createUserTokenBVRR(emailIdMD5, this.getCatalogTools().getBazaarVoiceKey(),bazaarVoiceEmailId.toLowerCase()));
					this.getBBBTrackOrderVO().setBvToken(this.getUserTokenBVRR());
					pRequest.getSession().setAttribute("userTokenBVRR", this.getUserTokenBVRR());
				}
				//R2.2 517C end
				if (bbbOrderVO != null) {
					this.getBBBTrackOrderVO().setBbbOrderVO(bbbOrderVO); //Manager's method for Order present in ATG
					if (!(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT).equals(this.getTrackOrderSuccessURL())) {
						this.setTrackOrderSuccessURL(SUCCESS_URL_ATG);
					}
				}else{
					this.getBBBTrackOrderVO().setLegacyOrderFlag(BBBCoreConstants.RETURN_TRUE);
				}
			} catch (BBBBusinessException e) {
				//If no order with the entered order Id is found in ATG
				if ("account_1073:err_no_such_order".equals(e.getMessage())) {
					this.getBBBTrackOrderVO().setLegacyOrderFlag(BBBCoreConstants.RETURN_TRUE);
					
				} else {
					//If the order id entered and email id are not associated
					if ("account_1074:order_email_not_associated".equals(e.getMessage())) {
						final String error = this.getLblTxtTemplateManager().getErrMsg(
								BBBCoreErrorConstants.ERR_EMAIL_ORDER_NOT_ASSOC,
								pRequest.getLocale().getLanguage(),
								errorPlaceHolderMap, null);						
						this.mErrorMap.put(BBBCoreConstants.ERROR, error);
						addFormException(new DropletException(error, BBBCoreErrorConstants.ERR_EMAIL_ORDER_NOT_ASSOC));
					} else {
						logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.TRACKORDER_EXCEPTION
								, BBBCoreErrorConstants.ACCOUNT_ERROR_1129), e);
						final String error = this.getLblTxtTemplateManager().getErrMsg(
								BBBCoreErrorConstants.TRACKORDER_EXCEPTION,
								pRequest.getLocale().getLanguage(),
								errorPlaceHolderMap, null);						
						this.mErrorMap.put(BBBCoreConstants.ERROR, error);
						addFormException(new DropletException(error, BBBCoreErrorConstants.TRACKORDER_EXCEPTION));
					}
				}
			} catch (BBBSystemException e) {
				//In case of no business error but a system (technical)exception
				final String error = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.TRACKORDER_EXCEPTION,
						pRequest.getLocale().getLanguage(),
						errorPlaceHolderMap, null);	
				this.mErrorMap.put(BBBCoreConstants.ERROR, error);
				addFormException(new DropletException(error, BBBCoreErrorConstants.TRACKORDER_EXCEPTION));
				logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.TRACKORDER_EXCEPTION,
						BBBCoreErrorConstants.ACCOUNT_ERROR_1129), e);
			}

			//If no ATG order found with entered fields then manager's method to get legacy orders is called
			if (this.getBBBTrackOrderVO().isLegacyOrderFlag()) {
				try {
					// R2.2 517C start
					logDebug("No ATG order found with entered fields so manager's method to get legacy orders is called");
					if (!BBBUtility.isEmpty(emailId)) {
						this.getBBBTrackOrderVO().setLegacyOrderVO(this.mOrderTrackingManager
								.getLegacyOrderTrackingDetails(orderId, emailId)); //manager's method to get legacy order
						this.setUserTokenBVRR(BazaarVoiceUtil.createUserTokenBVRR(BazaarVoiceUtil.generateMD5((getEmailMD5HashPrefix()+emailId).toLowerCase()), this.getCatalogTools().getBazaarVoiceKey(),emailId.toLowerCase()));
						this.getBBBTrackOrderVO().setBvToken(this.getUserTokenBVRR());
					} else {
						OrderDetailInfoReturn objOrderDetailRes = new OrderDetailInfoReturn();
						objOrderDetailRes = getOrderTrackingManager().getOrderDetailsManager().getLegacyOrderDetail(orderId);
						if(objOrderDetailRes != null && objOrderDetailRes.getBilling() != null ){
							bazaarVoiceEmailId = objOrderDetailRes.getBilling().getEmailAddr();
						}
						this.getBBBTrackOrderVO().setLegacyOrderVO(this.mOrderTrackingManager
								.getLegacyOrderTrackingDetailsWithEmailIdMD5(orderId, emailIdMD5,objOrderDetailRes)); //manager's method to get legacy order
						
						this.setUserTokenBVRR(BazaarVoiceUtil.createUserTokenBVRR(emailIdMD5, this.getCatalogTools().getBazaarVoiceKey(),bazaarVoiceEmailId.toLowerCase()));
						this.getBBBTrackOrderVO().setBvToken(this.getUserTokenBVRR());
					}
					//R2.2 517C end
					if (!(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT).equals(this.getTrackOrderSuccessURL())) {
						this.setTrackOrderSuccessURL(SUCCESS_URL_LEGACY);
					}
					if (this.getBBBTrackOrderVO().getLegacyOrderVO().getStatus().isErrorExists()) {
						final ErrorStatus errStatus = this.getBBBTrackOrderVO().getLegacyOrderVO().getStatus();
						//Business error from webservice 
						if (!BBBUtility.isEmpty(errStatus.getDisplayMessage())) {
							logError(LogMessageFormatter.formatMessage(pRequest, "BBBOrderTrackingFormHandler::handleTrackOrder : Business Error received while fetching Orders from Legacy System\n Error returned from service:"+errStatus.getDisplayMessage(), BBBCoreErrorConstants.ACCOUNT_ERROR_1087));
							final String error = this.getLblTxtTemplateManager().getErrMsg(
									BBBCoreErrorConstants.TRACKORDER_EXCEPTION,
									pRequest.getLocale().getLanguage(),
									errorPlaceHolderMap, null);	
							this.mErrorMap.put(BBBCoreConstants.ERROR, errStatus.getDisplayMessage());
							addFormException(new DropletException(error, BBBCoreErrorConstants.TRACKORDER_EXCEPTION));
						} else if (!BBBUtility.isEmpty(errStatus.getErrorMessage())) {
							//Technical Error
							logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.TRACKORDER_EXCEPTION,
									BBBCoreErrorConstants.ACCOUNT_ERROR_1129));
							final String error = this.getLblTxtTemplateManager().getErrMsg(
									BBBCoreErrorConstants.TRACKORDER_EXCEPTION,
									pRequest.getLocale().getLanguage(),
									errorPlaceHolderMap, null);	
							this.mErrorMap.put(BBBCoreConstants.ERROR, error);
							addFormException(new DropletException(error, BBBCoreErrorConstants.TRACKORDER_EXCEPTION));
						} else if (!errStatus.getValidationErrors().isEmpty()) {
							//Field validation error from webservice
							final Iterator<ValidationError> valErrorIterator = errStatus
									.getValidationErrors().iterator();
							while (valErrorIterator.hasNext()) {
								final ValidationError valError = valErrorIterator.next();
								if (!BBBUtility.isEmpty(valError.getKey()) 
										&& !BBBUtility.isEmpty(valError.getValue())) {
									logError(LogMessageFormatter.formatMessage(pRequest, "BBBOrderTrackingFormHandler::handleTrackOrder : Validation Errors received while fetching Orders from Legacy System\nkey=" + valError.getKey()+" validation Error: "+valError.getValue(), BBBCoreErrorConstants.ACCOUNT_ERROR_1130));
									if (valError.getKey().toLowerCase().contains("email")) {	
										final String error = this.getLblTxtTemplateManager().getErrMsg(
												BBBCoreErrorConstants.ERR_TRACKORDER_INVALID_EMAIL,
												pRequest.getLocale().getLanguage(),
												errorPlaceHolderMap, null);	
										this.mErrorMap.put(BBBCoreConstants.ERROR, error);
										addFormException(new DropletException(error, BBBCoreErrorConstants.ERR_TRACKORDER_INVALID_EMAIL));
									} else {
										if (valError.getKey().toLowerCase().contains("order")) {	
											final String error = this.getLblTxtTemplateManager().getErrMsg(
													BBBCoreErrorConstants.INVALID_ORDERID,
													pRequest.getLocale().getLanguage(),
													errorPlaceHolderMap, null);	
											this.mErrorMap.put(BBBCoreConstants.ERROR, error);
											addFormException(new DropletException(error, BBBCoreErrorConstants.INVALID_ORDERID));
										} else {	
											this.mErrorMap.put(BBBCoreConstants.ERROR, valError.getValue());
										}
									}
								}
							}
						}
					}

				} catch (BBBSystemException e) {
					//Error while calling Web Service
					logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.TRACKORDER_EXCEPTION
							, BBBCoreErrorConstants.ACCOUNT_ERROR_1129), e);
					final String error = this.getLblTxtTemplateManager().getErrMsg(
							BBBCoreErrorConstants.TRACKORDER_EXCEPTION,
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null);	
					this.mErrorMap.put(BBBCoreConstants.ERROR, error);
					addFormException(new DropletException(error, BBBCoreErrorConstants.TRACKORDER_EXCEPTION));
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreErrorConstants.TRACKORDER_EXCEPTION
							, BBBCoreErrorConstants.ACCOUNT_ERROR_1129), e);
					final String error = this.getLblTxtTemplateManager().getErrMsg(
							BBBCoreErrorConstants.TRACKORDER_EXCEPTION,
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null);	
					this.mErrorMap.put(BBBCoreConstants.ERROR, error);
					addFormException(new DropletException(error, BBBCoreErrorConstants.TRACKORDER_EXCEPTION));
				}
			}
		}

		if (this.mErrorMap != null && !this.mErrorMap.isEmpty()) {
			this.mBBBTrackOrderVO = null;
		} else {
			try {
				this.populateActiveProdMap();
			} catch (BBBSystemException e) {
				this.logDebug("System Exeption caught for handleTrackOrder",e);
			} catch (BBBBusinessException e) {
				this.logDebug("Business Exeption caught for handleTrackOrder",e);
			}
		}

		BBBPerformanceMonitor
		.end("BBBOrderTrackingFormHandler-handleTrackOrder");

		return checkFormRedirect(getTrackOrderSuccessURL(), getTrackOrderErrorURL(),
				pRequest, pResponse);
	}

	/**
	 * @return the trackOrderSuccessURL
	 */
	public String getTrackOrderSuccessURL() {
		return this.mTrackOrderSuccessURL;
	}

	/**
	 * @param pTrackOrderSuccessURL the trackOrderSuccessURL to set
	 */
	public void setTrackOrderSuccessURL(String pTrackOrderSuccessURL) {
		this.mTrackOrderSuccessURL = pTrackOrderSuccessURL;
	}

	/**
	 * @return the mTrackOrderErrorURL
	 */
	public String getTrackOrderErrorURL() {
		return this.mTrackOrderErrorURL;
	}

	/**
	 * @param pTrackOrderErrorURL the mTrackOrderErrorURL to set
	 */
	public void setTrackOrderErrorURL(String pTrackOrderErrorURL) {
		this.mTrackOrderErrorURL = pTrackOrderErrorURL;
	}


	/**
	 * @return the mOrderId
	 */
	public String getOrderId() {
		return this.mOrderId;
	}

	/**
	 * @param pOrderId the mOrderId to set
	 */
	public void setOrderId(String pOrderId) {
		this.mOrderId = pOrderId.toUpperCase();
	}

	/**
	 * @return the mOrderTrackingManager
	 */
	public BBBOrderTrackingManager getOrderTrackingManager() {
		return this.mOrderTrackingManager;
	}

	/**
	 * @param pOrderTrackingManager the mOrderTrackingManager to set
	 */
	public void setOrderTrackingManager(BBBOrderTrackingManager pOrderTrackingManager) {
		this.mOrderTrackingManager = pOrderTrackingManager;
	}

	/**
	 * @return the mEmailId
	 */
	public String getEmailId() {
		return this.mEmailId;
	}

	/**
	 * @param pEmailId the mEmailId to set
	 */
	public void setEmailId(String pEmailId) {
		this.mEmailId = pEmailId;
	}

	/**
	 * 
	 * @return the EmailIdMD5
	 */
	public String getEmailIdMD5() {
		return mEmailIdMD5;
	}

	/**
	 * 
	 * @param mEmailIdMD5 the emailIdMD5 hash to set
	 */
	public void setEmailIdMD5(String mEmailIdMD5) {
		this.mEmailIdMD5 = mEmailIdMD5;
	}

	/**
	 * @return the mErrorMap
	 */
	public Map<String, String> getErrorMap() {
		return this.mErrorMap;
	}

	/**
	 * @param pErrorMap the mErrorMap to set
	 */
	public void setErrorMap(Map<String, String> pErrorMap) {
		this.mErrorMap = pErrorMap;
	}

	/**
	 * @return the mTrackOrderInfoVO
	 */
	public BBBTrackOrderVO getBBBTrackOrderVO() {
		return this.mBBBTrackOrderVO;
	}

	/**
	 * @param pBBBTrackOrderVO the mBBBTrackOrderVO to set
	 */
	public void setBBBTrackOrderVO(final BBBTrackOrderVO pBBBTrackOrderVO) {
		this.mBBBTrackOrderVO = pBBBTrackOrderVO;
	}

	/**
	 * This method populates a Map for all the order Items along with a flag denoting if the product is Active.
	 * 
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void populateActiveProdMap() throws BBBSystemException, BBBBusinessException{
		boolean prodActiveFlag;
		this.getBBBTrackOrderVO().setActiveProdMap(new HashMap<String, Boolean>());
		if (this.getBBBTrackOrderVO().isLegacyOrderFlag()) {
			if (this.getBBBTrackOrderVO().getLegacyOrderVO() != null) {
				final List<CartItemDetailInfo> legacyOrderItemList = this.getBBBTrackOrderVO()
						.getLegacyOrderVO().getOrderInfo().getCartDetailInfo().getCartItemDetailList();
				for (CartItemDetailInfo legacyOrderItem : legacyOrderItemList) {
					final String skuId = legacyOrderItem.getSKU();
					final String productId = this.getOrderTrackingManager().getProductId(skuId);
					prodActiveFlag = this.getOrderTrackingManager().isProductActive(productId);
					//setting product Id as it doesn't come from webservice
					legacyOrderItem.setProductId(productId);
					//Creating Map with SKU Id
					this.getBBBTrackOrderVO().getActiveProdMap().put(productId, prodActiveFlag);
				}
								
			}
		} else {
			if (this.getBBBTrackOrderVO().getBbbOrderVO() != null) {
				final List<CommerceItemDisplayVO> orderItemList = this.getBBBTrackOrderVO()
						.getBbbOrderVO().getCommerceItemVOList();
				for (CommerceItemDisplayVO orderItem : orderItemList) {
					final String productId = orderItem.getProductId();
					prodActiveFlag = this.getOrderTrackingManager().isProductActive(productId);

					this.getBBBTrackOrderVO().getActiveProdMap().put(productId, prodActiveFlag);
				}
			}
		}
	}

	
	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param mCatalogTools the mCatalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools mCatalogTools) {
		this.mCatalogTools = mCatalogTools;
	}

	/**
	 * @return the userTokenBVRR
	 */
	public String getUserTokenBVRR() {
		return userTokenBVRR;
	}

	/**
	 * @param userTokenBVRR the userTokenBVRR to set
	 */
	public void setUserTokenBVRR(String userTokenBVRR) {
		this.userTokenBVRR = userTokenBVRR;
	}
}
