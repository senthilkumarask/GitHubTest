/**
 * 
 */
package com.bbb.commerce.checkout.tools;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.SimpleOrderManager;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.checkout.BBBVerifiedByVisaConstants;
import com.bbb.commerce.checkout.vbv.vo.BBBVerifiedByVisaVO;
import com.bbb.commerce.common.BBBVBVSessionBean;
import com.bbb.commerce.order.paypal.BBBPayPalServiceManager;
import com.bbb.commerce.order.paypal.PayPalAddressVerification;
import com.bbb.commerce.order.purchase.CheckoutProgressStates;
import com.bbb.commerce.vo.PayPalInputVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.paypal.BBBAddressPPVO;
import com.bbb.paypal.BBBGetExpressCheckoutDetailsResVO;
import com.bbb.paypal.vo.PayPalAddressVerifyVO;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;
import com.cardinalcommerce.client.CentinelRequest;
import com.cardinalcommerce.client.CentinelResponse;

/**
 * @author ngup50
 *
 */
public class BBBCheckoutTools extends BBBGenericService{
	
	private BBBCatalogToolsImpl catalogTools;
	private BBBPayPalServiceManager paypalServiceManager;
	private PayPalAddressVerification payPalAddressVerification;
	private LblTxtTemplateManager lblTxtTemplateManager;
	 
	 
	public PayPalAddressVerification getPayPalAddressVerification() {
		return this.payPalAddressVerification;
	}

	public void setPayPalAddressVerification(
			PayPalAddressVerification payPalAddressVerification) {
		this.payPalAddressVerification = payPalAddressVerification;
	}

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return this.lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}
	
	public BBBPayPalServiceManager getPaypalServiceManager() {
		return this.paypalServiceManager;
	}

	public void setPaypalServiceManager(BBBPayPalServiceManager paypalServiceManager) {
		this.paypalServiceManager = paypalServiceManager;
	}

	/**
	 * This method is used to make Lookup thin client call
	 * Response parameters like Enrolled, ACSUrl, Payload, etc of this call are set in BBBVerifiedByVisaVO and this vo is set in session bean.
	 * @param centinelRequest
	 * @param cardVerNumber is CVV number
	 * @param bbbOrder is Order object
	 * @return bBBVerifiedByVisaVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public final BBBVerifiedByVisaVO vbvCentinelSendLookupRequest(CentinelRequest centinelRequest, String cardVerNumber, BBBOrder bbbOrder, BBBVBVSessionBean vbvSessionBean) throws BBBSystemException, BBBBusinessException{

		logDebug("Start vbvCentinelSendLookupRequest method of BBBCheckoutTools for Order Id: " + bbbOrder.getId());
		CentinelResponse centinelResponse = null;
	    String originOfTraffic = BBBUtility.getOriginOfTraffic();
	    Date today = new Date();
		Timestamp dateTimeStamp=new Timestamp(today.getTime());
		String time = dateTimeStamp.toString();
		int connectTimeout = Integer.parseInt(getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ConnectTimeout).get(0));
		int readTimeout = Integer.parseInt(getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ReadTimeout).get(0));
		BBBVerifiedByVisaVO bBBVerifiedByVisaVO = null;
		
		if(null != centinelRequest){
			String regex = "\\<" + BBBVerifiedByVisaConstants.CardNumber + "\\>(.)*\\<\\/" + BBBVerifiedByVisaConstants.CardNumber + "\\>";
			String requestForLogging = centinelRequest.getFormattedRequest();
			if(BBBUtility.isNotEmpty(requestForLogging)){
				requestForLogging  = requestForLogging.replaceAll(regex, BBBCoreConstants.BLANK);
				logDebug("Centinal Look up Request -- " + requestForLogging);
			}
			long start = System.currentTimeMillis();
			centinelResponse = centinelRequest.sendHTTP(getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionURL).get(0), connectTimeout, readTimeout);
			long end = System.currentTimeMillis();
			logInfo("Total time taken to execute the Centinel lookup service=" + (end - start));
		}
		
		if(null != centinelResponse){
			String regexPayload = "\\<" + BBBVerifiedByVisaConstants.Payload + "\\>(.)*\\<\\/" + BBBVerifiedByVisaConstants.Payload + "\\>";
			String responseForLogging = centinelResponse.getUnparsedResponse();
			if(BBBUtility.isNotEmpty(responseForLogging)){
				responseForLogging = responseForLogging.replaceAll(regexPayload, BBBCoreConstants.BLANK);
				logDebug("Centinal Look up Response -- " + responseForLogging);
				this.logPersistedInfo(BBBVerifiedByVisaConstants.CENTINAL_LOOKUP, bbbOrder.getId(), null, null, null, bbbOrder.getSiteId(), originOfTraffic, null, time, BBBVerifiedByVisaConstants.CENTINAL_LOOKUP, centinelResponse.getUnparsedResponse().length() > 4000 ? centinelResponse.getUnparsedResponse().substring(0, 4000): centinelResponse.getUnparsedResponse());
			}
			bBBVerifiedByVisaVO = vbvSessionBean.getbBBVerifiedByVisaVO();
			bBBVerifiedByVisaVO.setLookupErrorNo(centinelResponse.getValue(BBBVerifiedByVisaConstants.ErrorNo));
			bBBVerifiedByVisaVO.setLookupErrorDesc(centinelResponse.getValue(BBBVerifiedByVisaConstants.ErrorDesc));
			bBBVerifiedByVisaVO.setTransactionId(centinelResponse.getValue(BBBVerifiedByVisaConstants.TransactionId));
			bBBVerifiedByVisaVO.setOrderId(centinelResponse.getValue(BBBVerifiedByVisaConstants.OrderId));
			bBBVerifiedByVisaVO.setEnrolled(centinelResponse.getValue(BBBVerifiedByVisaConstants.Enrolled));
			bBBVerifiedByVisaVO.setLookupEciFlag(centinelResponse.getValue(BBBVerifiedByVisaConstants.EciFlag));
			bBBVerifiedByVisaVO.setaCSUrl(centinelResponse.getValue(BBBVerifiedByVisaConstants.ACSUrl));
			bBBVerifiedByVisaVO.setPayload(centinelResponse.getValue(BBBVerifiedByVisaConstants.Payload));
			bBBVerifiedByVisaVO.setLookupResponse(centinelResponse.getFormattedResponse());
			bBBVerifiedByVisaVO.setLookupRequest(null);
			bBBVerifiedByVisaVO.setCardVerNumber(cardVerNumber);
			bBBVerifiedByVisaVO.setTransactionType(getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionType).get(0));
			vbvSessionBean.setbBBVerifiedByVisaVO(bBBVerifiedByVisaVO);
			logInfo("In Lookup call for OrderID : " + bbbOrder.getId() + " " + BBBVerifiedByVisaConstants.ErrorNo + " is : "+ centinelResponse.getValue(BBBVerifiedByVisaConstants.ErrorNo)
					+ ", " + BBBVerifiedByVisaConstants.ErrorDesc + " is : " + centinelResponse.getValue(BBBVerifiedByVisaConstants.ErrorDesc)
					+ ", " + BBBVerifiedByVisaConstants.Enrolled + " is : " + centinelResponse.getValue(BBBVerifiedByVisaConstants.Enrolled)
					+ ", " + BBBVerifiedByVisaConstants.EciFlag + " is : " + centinelResponse.getValue(BBBVerifiedByVisaConstants.EciFlag));
		}
		
		logDebug("End vbvCentinelSendLookupRequest method of BBBCheckoutTools for Order Id: " + bbbOrder.getId());
		return bBBVerifiedByVisaVO;
	}
	
	/**
	 * This method is called to update order accordingly with response attributes from lookup thin client call
	 * @param bbbOrder object of BBBOrder
	 * @param bBBVerifiedByVisaVO
	 * @param errorExists to check if there is any error in lookup call
	 * @param orderManager to call update order
	 * @return errorExists true/false
	 * @throws CommerceException is case of update order fail
	 */
	public final boolean vbvUpdateLookupOrderAttributes(BBBOrder bbbOrder, BBBVerifiedByVisaVO bBBVerifiedByVisaVO, boolean errorExists, SimpleOrderManager orderManager) throws CommerceException{
		
		logDebug("Start vbvUpdateLookupOrderAttributes method of BBBCheckoutTools for Order Id: " + bbbOrder.getId());
		synchronized (bbbOrder) {
        	bbbOrder.setErrorNo(bBBVerifiedByVisaVO.getLookupErrorNo());
        	bbbOrder.setErrorDesc(bBBVerifiedByVisaVO.getLookupErrorDesc());
        	bbbOrder.setTransactionId(bBBVerifiedByVisaVO.getTransactionId());
        	bbbOrder.setVbvOrderId(bBBVerifiedByVisaVO.getOrderId());
        	bbbOrder.setEnrolled(bBBVerifiedByVisaVO.getEnrolled());
        	bbbOrder.setEci(bBBVerifiedByVisaVO.getLookupEciFlag());
        	String errorNo = bBBVerifiedByVisaVO.getLookupErrorNo();
			String enrolled = bBBVerifiedByVisaVO.getEnrolled();
			if (BBBVerifiedByVisaConstants.ERROR_NO_0.equals(errorNo) && BBBVerifiedByVisaConstants.EnrolledForVBV_Yes.equals(enrolled) ) {
				errorExists=false;
			} else{
				if(bBBVerifiedByVisaVO.getCentinel_PIType().equalsIgnoreCase(BBBVerifiedByVisaConstants.VISA)){
					bBBVerifiedByVisaVO.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
					bbbOrder.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
					logDebug("VBV attempted");
				}
			}
			orderManager.updateOrder(bbbOrder);
		}
		logDebug("errorExists in lookup call for Order Id: " + errorExists);
		logDebug("End vbvUpdateLookupOrderAttributes method of BBBCheckoutTools for Order Id: " + bbbOrder.getId());
		return errorExists;
	}
	
	/**This method calls sendHTTP method to make thin client call for authentication. Response of this call is set in BBBVerifiedByVisaVO and this vo is set in session bean.
	 * @param centinelRequest
	 * @param bbbOrder
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public final BBBVerifiedByVisaVO vbvSendAuthenticateRequest(CentinelRequest centinelRequest, BBBOrder bbbOrder, BBBVBVSessionBean vbvSessionBean) throws BBBSystemException, BBBBusinessException{

		logDebug("Start vbvSendAuthenticateRequest method of BBBCheckoutTools for Order Id: " + bbbOrder.getId());
		CentinelResponse centinelResponse = null;
	    String originOfTraffic = BBBUtility.getOriginOfTraffic();
	    Date today = new Date();
		Timestamp dateTimeStamp=new Timestamp(today.getTime());
		String time = dateTimeStamp.toString();
		int connectTimeout = Integer.parseInt(getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ConnectTimeout).get(0));
		int readTimeout = Integer.parseInt(getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.ReadTimeout).get(0));
		if(null != centinelRequest){
			String regexCardNumber = "\\<" + BBBVerifiedByVisaConstants.PAResPayload + "\\>(.)*\\<\\/" + BBBVerifiedByVisaConstants.PAResPayload + "\\>";
			String regexTransPwd = "\\<" + BBBVerifiedByVisaConstants.TransactionPwd + "\\>(.)*\\<\\/" + BBBVerifiedByVisaConstants.TransactionPwd + "\\>";
			String requestForLogging = centinelRequest.getFormattedRequest();
			if(BBBUtility.isNotEmpty(requestForLogging)){
				requestForLogging  = requestForLogging.replaceAll(regexCardNumber, BBBCoreConstants.BLANK);
				requestForLogging  = requestForLogging.replaceAll(regexTransPwd, BBBCoreConstants.BLANK);
				logDebug("Centinal Authorization API request -- " + requestForLogging);
			}
			long start = System.currentTimeMillis();
			centinelResponse = centinelRequest.sendHTTP(getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.VBVTransactionURL).get(0), connectTimeout, readTimeout);
			long end = System.currentTimeMillis();
			logInfo("Total time taken to execute the Centinel Authenticate service=" + (end - start));
		}
		
		if(null!=centinelResponse && BBBUtility.isNotEmpty(centinelResponse.getUnparsedResponse())) {
			logDebug("Centinal Authorization API response -- "+centinelResponse.getUnparsedResponse());
			this.logPersistedInfo(BBBVerifiedByVisaConstants.CENTINAL_AUTHENTICATE, bbbOrder.getId(), null, null, null, bbbOrder.getSiteId(), originOfTraffic, null, time, BBBVerifiedByVisaConstants.CENTINAL_AUTHENTICATE, centinelResponse.getUnparsedResponse().length() > 4000 ? centinelResponse.getUnparsedResponse().substring(0, 4000) : centinelResponse.getUnparsedResponse());
		}
		BBBVerifiedByVisaVO bBBVerifiedByVisaVO = vbvSessionBean.getbBBVerifiedByVisaVO();
		if(null != centinelRequest){
			bBBVerifiedByVisaVO.setAuthenticateRequest(centinelRequest.getFormattedRequest());
		}
		if(centinelResponse != null) {
			bBBVerifiedByVisaVO.setAuthenticateResponse(centinelResponse.getFormattedResponse());
			bBBVerifiedByVisaVO.setpAResStatus(centinelResponse.getValue(BBBVerifiedByVisaConstants.PAResStatus));
			bBBVerifiedByVisaVO.setSignatureVerification(centinelResponse.getValue(BBBVerifiedByVisaConstants.SignatureVerification));
			bBBVerifiedByVisaVO.setAuthenticationEciFlag(centinelResponse.getValue(BBBVerifiedByVisaConstants.EciFlag));
			bBBVerifiedByVisaVO.setXid(centinelResponse.getValue(BBBVerifiedByVisaConstants.Xid));
			bBBVerifiedByVisaVO.setCavv(centinelResponse.getValue(BBBVerifiedByVisaConstants.Cavv));
			bBBVerifiedByVisaVO.setAuthenticationErrorNo(centinelResponse.getValue(BBBVerifiedByVisaConstants.ErrorNo));
			bBBVerifiedByVisaVO.setAuthenticationErrorDesc(centinelResponse.getValue(BBBVerifiedByVisaConstants.ErrorDesc));
			
			logInfo("In Authenticate call for OrderID : " + bbbOrder.getId() + ", " + BBBVerifiedByVisaConstants.PAResStatus  + " is : " + centinelResponse.getValue(BBBVerifiedByVisaConstants.PAResStatus)
					+ ", " + BBBVerifiedByVisaConstants.SignatureVerification +  " is : " + centinelResponse.getValue(BBBVerifiedByVisaConstants.SignatureVerification)
					+ ", " + BBBVerifiedByVisaConstants.EciFlag +  " is : " + centinelResponse.getValue(BBBVerifiedByVisaConstants.EciFlag)
					+ ", " + BBBVerifiedByVisaConstants.Xid +  " is : " + centinelResponse.getValue(BBBVerifiedByVisaConstants.Xid)
					+ ", " + BBBVerifiedByVisaConstants.Cavv +  " is : " + centinelResponse.getValue(BBBVerifiedByVisaConstants.Cavv)
					+ ", " + BBBVerifiedByVisaConstants.ErrorNo +  " is : " + centinelResponse.getValue(BBBVerifiedByVisaConstants.ErrorNo)
					+ ", " + BBBVerifiedByVisaConstants.ErrorDesc +  " is : " + centinelResponse.getValue(BBBVerifiedByVisaConstants.ErrorDesc));	
		}
		
		logDebug("End vbvSendAuthenticateRequest method of BBBCheckoutTools for Order Id: " + bbbOrder.getId());
		centinelRequest = null;
        centinelResponse = null;
		return bBBVerifiedByVisaVO;
	}
	
	/**
	 * This method is called to update order accordingly with response attributes from authenticate thin client call
	 * and throw exceptions as per business logic for further processing of order.
	 * @param bbbOrder object of BBBOrder
	 * @param bBBVerifiedByVisaVO
	 * @param messageHandler for getting messages from error repository
	 * @param orderManager for calling update order
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws CommerceException
	 */
	public final void vbvUpdateAuthOrderAttributes(BBBOrder bbbOrder, BBBVerifiedByVisaVO bBBVerifiedByVisaVO, LblTxtTemplateManager messageHandler, SimpleOrderManager orderManager) throws BBBSystemException, BBBBusinessException, CommerceException{
		
		logDebug("Start vbvUpdateAuthOrderAttributes method of BBBCheckoutTools for Order Id: " + bbbOrder.getId());
		synchronized (bbbOrder) {
			try{
				bbbOrder.setPAResStatus(bBBVerifiedByVisaVO.getpAResStatus());
				bbbOrder.setSignatureVerification(bBBVerifiedByVisaVO.getSignatureVerification());
				bbbOrder.setEci(bBBVerifiedByVisaVO.getAuthenticationEciFlag());
				bbbOrder.setXid(bBBVerifiedByVisaVO.getXid());
				bbbOrder.setCavv(bBBVerifiedByVisaVO.getCavv());
				bbbOrder.setErrorNo(bBBVerifiedByVisaVO.getAuthenticationErrorNo());
				bbbOrder.setErrorDesc(bBBVerifiedByVisaVO.getAuthenticationErrorDesc());
				String paresStatus = bBBVerifiedByVisaVO.getpAResStatus();
				String sigVerStatus = bBBVerifiedByVisaVO.getSignatureVerification();
				String errorNo = bBBVerifiedByVisaVO.getAuthenticationErrorNo();
				String enrollmentStatus=bBBVerifiedByVisaVO.getEnrolled();
		    	//if no error exists (i.e. errorNo is 0 or errorNo is 1140) and signature verification is Y and PaRes status is Y or A then proceed with committing the order. else redirect to payment page with failure.
		    	if ((BBBVerifiedByVisaConstants.ERROR_NO_0.equals(errorNo)||BBBVerifiedByVisaConstants.ERROR_NO_1140.equals(errorNo)) && BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes.equals(sigVerStatus) && BBBVerifiedByVisaConstants.PaResStatus_Y.equals(paresStatus)) {
			    	bBBVerifiedByVisaVO.setMessage(getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess).get(0));
			    	if(bBBVerifiedByVisaVO.getCentinel_PIType().equalsIgnoreCase(BBBVerifiedByVisaConstants.VISA)){
						bBBVerifiedByVisaVO.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV);
						bbbOrder.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV);
						logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV);
					}
		    	} else if ((BBBVerifiedByVisaConstants.ERROR_NO_0.equals(errorNo)||BBBVerifiedByVisaConstants.ERROR_NO_1140.equals(errorNo)) && BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes.equals(sigVerStatus) && BBBVerifiedByVisaConstants.PaResStatus_A.equals(paresStatus)) {
			    	bBBVerifiedByVisaVO.setMessage(getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess).get(0));
			    	if(bBBVerifiedByVisaVO.getCentinel_PIType().equalsIgnoreCase(BBBVerifiedByVisaConstants.VISA)){
						bBBVerifiedByVisaVO.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
						bbbOrder.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
						logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
					}
		    	} else if ((BBBVerifiedByVisaConstants.ERROR_NO_0.equals(errorNo)||BBBVerifiedByVisaConstants.ERROR_NO_1140.equals(errorNo)) && BBBUtility.isEmpty(paresStatus) && BBBUtility.isEmpty(sigVerStatus) && BBBVerifiedByVisaConstants.PaResStatus_N.equalsIgnoreCase(enrollmentStatus)) {
			    	bBBVerifiedByVisaVO.setMessage(getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.AuthenticateSuccess).get(0));
			    	if(bBBVerifiedByVisaVO.getCentinel_PIType().equalsIgnoreCase(BBBVerifiedByVisaConstants.VISA)){
						bBBVerifiedByVisaVO.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
						bbbOrder.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
						logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
					}
		    	} else if (BBBVerifiedByVisaConstants.PaResStatus_U.equalsIgnoreCase(paresStatus) && BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes.equalsIgnoreCase(sigVerStatus) && (BBBVerifiedByVisaConstants.ERROR_NO_0.equals(errorNo)||BBBVerifiedByVisaConstants.ERROR_NO_1140.equals(errorNo))) {
			    	bBBVerifiedByVisaVO.setMessage(getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.NonAuthenticatedSuccess).get(0));
			    	if(bBBVerifiedByVisaVO.getCentinel_PIType().equalsIgnoreCase(BBBVerifiedByVisaConstants.VISA)){
						bBBVerifiedByVisaVO.setCommerceIndicator(BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR);
						bbbOrder.setCommerceIndicator(BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR);
						logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.EMPTY_COMMERCE_INDICATOR);
					}
		    	} else if (BBBVerifiedByVisaConstants.PaResStatus_N.equalsIgnoreCase(paresStatus) && BBBVerifiedByVisaConstants.SignatureVerificationStatus_Yes.equalsIgnoreCase(sigVerStatus) && (BBBVerifiedByVisaConstants.ERROR_NO_0.equals(errorNo)||BBBVerifiedByVisaConstants.ERROR_NO_1140.equals(errorNo))) {
		    		bBBVerifiedByVisaVO.setMessage(messageHandler.getErrMsg(BBBVerifiedByVisaConstants.AuthenticateFailure, BBBCoreConstants.DEFAULT_LOCALE, null));
		    		throw new BBBBusinessException(BBBVerifiedByVisaConstants.AuthenticateFailure, messageHandler.getErrMsg(BBBVerifiedByVisaConstants.AuthenticateFailure, BBBCoreConstants.DEFAULT_LOCALE, null));
		    	}else if(BBBUtility.isEmpty(paresStatus) && BBBUtility.isEmpty(sigVerStatus) && BBBVerifiedByVisaConstants.AuthenticateTimeout_1051.equalsIgnoreCase(errorNo)||BBBVerifiedByVisaConstants.AuthenticateTimeout_7040.equalsIgnoreCase(errorNo)){
		    		bBBVerifiedByVisaVO.setMessage(getCatalogTools().getAllValuesForKey(BBBVerifiedByVisaConstants.THIRD_PARTY_URL, BBBVerifiedByVisaConstants.NonAuthenticatedSuccess).get(0));
			    	if(bBBVerifiedByVisaVO.getCentinel_PIType().equalsIgnoreCase(BBBVerifiedByVisaConstants.VISA)){
						bBBVerifiedByVisaVO.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
						bbbOrder.setCommerceIndicator(BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
						logDebug("Setting VISA comerce indicator with " + BBBVerifiedByVisaConstants.VBV_ATTEMPTED);
					}
		    	}
		    	else{
		    		bBBVerifiedByVisaVO.setMessage(messageHandler.getErrMsg(BBBVerifiedByVisaConstants.AuthenticateFailure, BBBCoreConstants.DEFAULT_LOCALE, null));
		    		throw new BBBBusinessException(BBBVerifiedByVisaConstants.AuthenticateFailure, messageHandler.getErrMsg(BBBVerifiedByVisaConstants.AuthenticateFailure, BBBCoreConstants.DEFAULT_LOCALE, null));
		    	}
			
			}
			finally{
				orderManager.updateOrder(bbbOrder);
				logDebug("End vbvUpdateAuthOrderAttributes method of BBBCheckoutTools for Order Id: " + bbbOrder.getId());
			}
		}
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogToolsImpl getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogToolsImpl catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * This method validates whether paypal token exist in order or not
	 * 
	 * @param token
	 * @param order
	 * @return true if token exist else false
	 */
	public boolean validatePayPalToken(String token, BBBOrderImpl order, Profile profile){
		
		boolean validToken = true;
		 if(StringUtils.isEmpty(token) || BBBCoreConstants.NULL_VALUE.equalsIgnoreCase(token)){
			logDebug("BBBCheckoutTools.validatePayPalToken():: No Token Available");
			logDebug("BBBCheckoutTools.validatePayPalToken():: Call Set Express Webservice");
			validToken = false;
			try {
				getPaypalServiceManager().removePayPalPaymentGroup(order, profile);
			} catch (BBBSystemException e) {
				logError("BBBCheckoutTools.validatePayPalToken():: System Exception while removing PayPal Details from Order: " + e);
			}
			
		}
		return validToken;
	}
	
	/**
	 * This method returns redirect url if paypal service failed i.e. cart or payment page
	 * 
	 * @return url of cart or payment page
	 */
	public String getPayPalRedirectUrl(CheckoutProgressStates checkoutState){
		
		String redirectUrl = null;
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		  BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);

		if(checkoutState.getCurrentLevel() != null && checkoutState.getCurrentLevel().equalsIgnoreCase(BBBCoreConstants.CART)){
			redirectUrl = checkoutState.getCheckoutFailureURLs().get(checkoutState.getCurrentLevel());
		  }else if(checkoutState.getCurrentLevel() != null && checkoutState.getCurrentLevel().equalsIgnoreCase(BBBCoreConstants.SP_REVIEW)){
			  redirectUrl = checkoutState.getCheckoutFailureURLs().get(BBBPayPalConstants.SP_PAYPAL);
		  }
		else{
			redirectUrl = checkoutState.getCheckoutFailureURLs().get(BBBPayPalConstants.PAYMENT);
		}
		return redirectUrl;
	}
	
	/**
	 * This method returns whether call is from cart or not
	 * 
	 * @return true if from cart else false
	 */
	public boolean isPayPalCallFromCart(String currentLevel){
		if(currentLevel != null && currentLevel.equalsIgnoreCase(BBBCoreConstants.CART)){
			return true;
		 }
		return false;
	}
	
	/**
	 * @param pRequest
	 * @param pResponse
	 * @param voResp
	 * @param order
	 * @param addressVerifyVO
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public PayPalAddressVerifyVO validateShipping(BBBGetExpressCheckoutDetailsResVO voResp, BBBOrderImpl order, PayPalAddressVerifyVO addressVerifyVO, PayPalInputVO paypalInput) throws ServletException, IOException {
		logDebug("BBBCheckoutTools.validateShipping():: Start");
		
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		boolean result = false;
		//boolean ispayPalShipAddValidated = payPalSessionBean.isPayPalShipAddValidated();
		boolean validateOrderAddress = paypalInput.getPaypalSessionBean().isValidateOrderAddress();
		boolean addressExistInOrder = this.paypalServiceManager.addressInOrder(order);
		
		//Set Redirect Url and redirect state to Preview Page by default and override it with respective to the situations
		if(paypalInput.isSpcSession()){
			addressVerifyVO.setRedirectUrl(this.payPalAddressVerification.getAddressVerifyRedirectUrl().get(BBBPayPalConstants.SP_REVIEW));
			addressVerifyVO.setRedirectState(BBBPayPalConstants.SP_REVIEW);
		}else{		
		addressVerifyVO.setRedirectUrl(this.payPalAddressVerification.getAddressVerifyRedirectUrl().get(BBBPayPalConstants.REVIEW));
		addressVerifyVO.setRedirectState(BBBPayPalConstants.REVIEW);
		}
		
		String qasVerified = (String) request.getObjectParameter(BBBPayPalConstants.QAS_VERIFIED);
		logDebug("qasVerified : " + qasVerified);
		/*This block will execute when qas is not verified yet. i.e. validating only International and PO address*/
		if(qasVerified.equalsIgnoreCase(BBBCoreConstants.FALSE)){
			//verify international and PO Address before QAS validation
			result = this.getPayPalAddressVerification().validateInternationalAndPOAddress(voResp, addressVerifyVO);
		}
		
		/*This block will execute when qas is already verified. i.e. validating shipping restrictions and coupons*/
		else{
			/*
			 * Below attribute act as an indicator to refresh coupon in case of
			 * user does paypal checkout for the first time in a session.
			 */
			boolean refreshCoupon = Boolean.valueOf((String) request.getSession().getAttribute(
					BBBCoreConstants.REFRESH_COUPON));
			/*This block will execute when address is already present in order. In that case we dont need to validate
			 * International, PO and QAS*/
			if(validateOrderAddress || addressExistInOrder){
				
				boolean relationExist = getPaypalServiceManager().ShippingCommerceRelationshipExist(order);
				if(!relationExist){
					List<String> addressErrorList = new ArrayList<String>();
					addressVerifyVO.setSuccess(false);
					addressVerifyVO.setRedirectUrl(this.getPayPalAddressVerification().getAddressVerifyRedirectUrl().get(BBBPayPalConstants.SHIPPING));
					addressVerifyVO.setRedirectState(BBBPayPalConstants.SHIPPING_SINGLE);
					this.logError("Commerce item have no Shipping Address, so redirecting user to shipping page");
					addressErrorList.add(this.getLblTxtTemplateManager().getErrMsg(BBBPayPalConstants.ERR_PP_ADDRESS_EMPTY, "EN", null));
					addressVerifyVO.setAddressErrorList(addressErrorList);
				}
				else{
					logDebug("Address exists in order");
					String currentState = paypalInput.getCheckoutState().getCurrentLevel();
					/*This block will execute when user clicks continue with paypal i.e. 
					 * cart may be modified after adding shipping address in order*/
					if(currentState.equalsIgnoreCase(BBBCoreConstants.CART)){
						logDebug("Validating shipping restrictions for address in order as cart may be modified");
						result = this.getPayPalAddressVerification().validateShippingAddressInOrder(order, addressVerifyVO);
					
						//Call coupon web service if user is transient
						if(result && paypalInput.getProfile().isTransient()){
							logDebug("No error while validating shipping address so validating coupons");
							//Invoke coupon web service 
							/*
							 * We are calling validateCoupons method only if
							 * there is fresh calls to paypal for a guest user
							 * session.
							 */
							logDebug("Coupon Validation Started");
							if (refreshCoupon && !BBBUtility.siteIsTbs(SiteContextManager.getCurrentSiteId())
									&& getPaypalServiceManager().validateCoupons(order, paypalInput.getProfile())) {
								addressVerifyVO.setRedirectUrl(this.getPayPalAddressVerification()
										.getAddressVerifyRedirectUrl().get(BBBPayPalConstants.COUPONS));
								addressVerifyVO.setRedirectState(BBBPayPalConstants.COUPONS);
							} else if (!refreshCoupon && !BBBUtility.siteIsTbs(SiteContextManager.getCurrentSiteId())
									&& null != order.getCouponMap() && !order.getCouponMap().isEmpty()) {
								addressVerifyVO.setRedirectUrl(this.getPayPalAddressVerification()
										.getAddressVerifyRedirectUrl().get(BBBPayPalConstants.COUPONS));
								addressVerifyVO.setRedirectState(BBBPayPalConstants.COUPONS);
							}
							/*Resetting REFRESH_COUPON attribute after 1st calls.*/
							ServletUtil.getCurrentRequest().getSession().setAttribute(BBBCoreConstants.REFRESH_COUPON, null);
						}
					}
						
					/*This block will execute when user clicks Next from Shipping Page(Redirected to shipping page when there was any error validating paypal shipping address) 
					 * and we will validate only coupons in that case*/
					else if(paypalInput.getProfile().isTransient()){
						logDebug("Shipping Address is already validated i.e. when clicks NEXT from shipping page so validating coupons");
						//Invoke coupon web service 
						/*
						 * We are calling validateCoupons method only if there
						 * is fresh calls to paypal for a guest user session.
						 */
						if (refreshCoupon && !BBBUtility.siteIsTbs(SiteContextManager.getCurrentSiteId())
								&& getPaypalServiceManager().validateCoupons(order, paypalInput.getProfile())) {
							addressVerifyVO.setRedirectUrl(this.getPayPalAddressVerification()
									.getAddressVerifyRedirectUrl().get(BBBPayPalConstants.COUPONS));
							addressVerifyVO.setRedirectState(BBBPayPalConstants.COUPONS);
						} else if (!refreshCoupon && !BBBUtility.siteIsTbs(SiteContextManager.getCurrentSiteId())
								&& null != order.getCouponMap() && !order.getCouponMap().isEmpty()) {
							addressVerifyVO.setRedirectUrl(this.getPayPalAddressVerification()
									.getAddressVerifyRedirectUrl().get(BBBPayPalConstants.COUPONS));
							addressVerifyVO.setRedirectState(BBBPayPalConstants.COUPONS);
						} else {
							String currentLevel = paypalInput.getCheckoutState().getCurrentLevel();
							addressVerifyVO.setRedirectUrl(paypalInput.getCheckoutState().getCheckoutFailureURLs()
									.get(currentLevel));
							addressVerifyVO.setRedirectState(currentLevel);
						}
						/*Resetting REFRESH_COUPON attribute after 1st calls.*/
						ServletUtil.getCurrentRequest().getSession().setAttribute(BBBCoreConstants.REFRESH_COUPON, null);
					}
				}
			}
			
			/*This block will execute when qas is verified already and there is no address in order*/
			else {
				logDebug("Address does not exist in order");
				logDebug("Shipping Restrictions Validation Started");
				boolean isSuggestedAddress = populateQasSuggestedAddress(voResp, request);
				logDebug("Qas address: " + isSuggestedAddress);
				//verify shipping restrictions after QAS verification
				result = this.getPayPalAddressVerification().validatePayPalShippingAddress(voResp, order, addressVerifyVO, paypalInput);
				
				//Call coupon webservice if user is transient
				if(result && paypalInput.getProfile().isTransient()){
					//Invoke coupon web service 
					logDebug("Coupon Validation Started");
					/*
					 * We are calling validateCoupons method only if there is
					 * fresh calls to paypal for a guest user session.
					 */
					if (refreshCoupon && !BBBUtility.siteIsTbs(SiteContextManager.getCurrentSiteId())
							&& getPaypalServiceManager().validateCoupons(order, paypalInput.getProfile())) {
						addressVerifyVO.setRedirectUrl(this.getPayPalAddressVerification()
								.getAddressVerifyRedirectUrl().get(BBBPayPalConstants.COUPONS));
						addressVerifyVO.setRedirectState(BBBPayPalConstants.COUPONS);
					} else if (!refreshCoupon && !BBBUtility.siteIsTbs(SiteContextManager.getCurrentSiteId())
							&& null != order.getCouponMap() && !order.getCouponMap().isEmpty()) {
						addressVerifyVO.setRedirectUrl(this.getPayPalAddressVerification()
								.getAddressVerifyRedirectUrl().get(BBBPayPalConstants.COUPONS));
						addressVerifyVO.setRedirectState(BBBPayPalConstants.COUPONS);
					}
					/*Resetting REFRESH_COUPON attribute after 1st calls.*/
					ServletUtil.getCurrentRequest().getSession().setAttribute(BBBCoreConstants.REFRESH_COUPON, null);
				}
			}
		}
		//Populate PayPalSession Bean with address, error List.
		paypalInput.getPaypalSessionBean().setAddress(addressVerifyVO.getAddress());
		paypalInput.getPaypalSessionBean().setErrorList(addressVerifyVO.getAddressErrorList());
		paypalInput.getPaypalSessionBean().setInternationalOrPOError(addressVerifyVO.isInternationalOrPOError());
		paypalInput.getPaypalSessionBean().setInternationalError(addressVerifyVO.isInternationalError());
		logDebug("BBBCheckoutTools.validateShipping():: Ends");
		
		return addressVerifyVO;
	}
	
	
	/**
	 * This method populates updated address entries in shipping address with
	 * QAS suggested address
	 * 
	 * @param voResp
	 * @param pRequest
	 * @return true if qas provided any address otherwise false
	 */
	private boolean populateQasSuggestedAddress(BBBGetExpressCheckoutDetailsResVO voResp, final DynamoHttpServletRequest pRequest){
		
		logDebug("PayPalDroplet.populateQasSuggestedAddress():: Start");
		String address1 = (String) pRequest.getObjectParameter(BBBPayPalConstants.ADDRESS1);
		boolean qasAddress = false;
		if(BBBUtility.isEmpty(address1)){
			logDebug("PayPalDroplet.populateQasSuggestedAddress():: Qas does not provided any address");
			qasAddress = false;
		}
		else{
			logDebug("PayPalDroplet.populateQasSuggestedAddress():: Qas provided an address");
			String address2 = (String) pRequest.getObjectParameter(BBBPayPalConstants.ADDRESS2);
			String cityName = (String) pRequest.getObjectParameter(BBBPayPalConstants.CITY_NAME);
			String stateName = (String) pRequest.getObjectParameter(BBBPayPalConstants.STATE_NAME);
			String zipCode = (String) pRequest.getObjectParameter(BBBPayPalConstants.ZIP_CODE);
			BBBAddressPPVO shippingAddress = voResp.getShippingAddress();
			if(shippingAddress != null){
				qasAddress = true;
				shippingAddress.setAddress1(address1);
				shippingAddress.setAddress2(address2);
				shippingAddress.setCity(cityName);
				shippingAddress.setState(stateName);
				shippingAddress.setPostalCode(zipCode);
			}
		}
		logDebug("PayPalDroplet.populateQasSuggestedAddress():: End");
		return qasAddress;
	}
	
}
