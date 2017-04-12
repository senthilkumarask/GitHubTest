/**
 *
 */
package com.bbb.commerce.payment;

import com.bbb.commerce.order.PaypalBeanInfo;
import com.bbb.commerce.order.PaypalStatus;
import com.bbb.commerce.order.PaypalStatusImpl;
import com.bbb.commerce.order.paypal.BBBPayPalServiceManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.payment.giftcard.PayPalProcessor;
import com.bbb.paypal.BBBDoAuthorizationResVO;
import com.bbb.paypal.BBBDoExpressCheckoutPaymentResVO;


/**
 * This PayPalProcessorImpl makes DoExpress and DoAuth paypal call and set error codes if any
 * 
 * @author ssh108
 */
public class PayPalProcessorImpl extends BBBGenericService implements PayPalProcessor {

	//This is to test error scenarios for paypal. If this is enabled order will not be placed
	boolean isErrorTestingEnabled;
	private BBBPayPalServiceManager paypalServiceManager;
	
	/**
	 * @return the paypalServiceManager
	 */
	public BBBPayPalServiceManager getPaypalServiceManager() {
		return paypalServiceManager;
	}

	/**
	 * @param paypalServiceManager the paypalServiceManager to set
	 */
	public void setPaypalServiceManager(BBBPayPalServiceManager paypalServiceManager) {
		this.paypalServiceManager = paypalServiceManager;
	}

	/**
	 * @return the isErrorTestingEnabled
	 */
	public boolean getIsErrorTestingEnabled() {
		return this.isErrorTestingEnabled;
	}

	/**
	 * @param isErrorTestingEnabled the isErrorTestingEnabled to set
	 */
	public void setIsErrorTestingEnabled(boolean isErrorTestingEnabled) {
		this.isErrorTestingEnabled = isErrorTestingEnabled;
	}

	/**
	 * This method makes doExpress and DoAuth paypal call and set error codes if any
	 * 
	 * @param pPaypalBeanInfo
	 * @return
	 */

	
	@Override
	public final PaypalStatus authorize(final PaypalBeanInfo pPaypalBeanInfo) {
		this.logDebug("Starting method PayPalProcessorImpl.authorize, payPalBeanInfo: " + pPaypalBeanInfo);

		// checking if errorTestEnabled then return error
		if (getIsErrorTestingEnabled()) {
			this.logError("PayPalProcessorImpl.getIsErrorTestingEnabled() | Testing is enabled order will not be Placed: ");
			return new PaypalStatusImpl(this.getNextTransactionId(), false, BBBCheckoutConstants.BLANK, pPaypalBeanInfo);
		}

		// Do Express Service call
		BBBDoExpressCheckoutPaymentResVO res = null;
		try {
			res = getPaypalServiceManager().doExpressCheckout(pPaypalBeanInfo.getOrder());
		} catch (BBBSystemException e) {
			this.logError("PayPalProcessorImpl.authorize() :: System Exception while calling doExpressCheckout Webservice " + e);
			return new PaypalStatusImpl(this.getNextTransactionId(), false, BBBCoreErrorConstants.PAYPAL_WEBSERVICE_CALL_ERROR, pPaypalBeanInfo);
		} catch (BBBBusinessException e) {
			this.logError("PayPalProcessorImpl.authorize() :: Business Exception while calling doExpressCheckout Webservice " + e);
			return new PaypalStatusImpl(this.getNextTransactionId(), false, BBBCoreErrorConstants.PAYPAL_WEBSERVICE_CALL_ERROR, pPaypalBeanInfo);
		}

		if (res != null && !res.getErrorStatus().isErrorExists()) {
			this.logInfo("DoExpress call is success, calling doAuth:: Transaction id: " + res.getTransactionId()
					+ " Protection Flag: " + res.getProtectionElig() + " OrderTimeStamp: " + res.getOrderTimestamp());
			pPaypalBeanInfo.setPaypalOrder(res.getTransactionId());
			// pPaypalBeanInfo.setProtectionEligibility(res.getProtectionElig());
			pPaypalBeanInfo.setOrderTimestamp(res.getOrderTimestamp());
			// Do Authorization Service call
			BBBDoAuthorizationResVO authResVO;
			try {
				authResVO = getPaypalServiceManager().doAuthorization(res.getTransactionId(), pPaypalBeanInfo.getOrder());
				if (authResVO != null && !authResVO.isWebServiceError() && !res.getErrorStatus().isErrorExists()) {
					this.logInfo("doAuth call is success :: CorrelationID: " + authResVO.getCorrelationID() + " TransactionID: " + 
							authResVO.getTransactionId() + " AuthTimeStamp: " + authResVO.getAuthTimeStamp());
					pPaypalBeanInfo.setCorrelationId(authResVO.getCorrelationID());
					pPaypalBeanInfo.setTransId(authResVO.getTransactionId());
					pPaypalBeanInfo.setAuthTimeStamp(authResVO.getAuthTimeStamp());
					pPaypalBeanInfo.setProtectionEligibility(authResVO.getProtectionEligibility());
					return new PaypalStatusImpl(this.getNextTransactionId(), true, BBBCheckoutConstants.BLANK, pPaypalBeanInfo);
				}
				else if(authResVO != null){
					this.logError("PayPalProcessorImpl.authorize() :: PayPal Authorization Fail: Error ID: " + authResVO.getErrorStatus().getErrorId() + "Error Message: " + authResVO.getErrorStatus().getDisplayMessage());
					return new PaypalStatusImpl(this.getNextTransactionId(), false, Integer.toString(authResVO.getErrorStatus().getErrorId()), pPaypalBeanInfo);
				}
			} catch (BBBSystemException e) {
				this.logError("PayPalProcessorImpl.authorize() :: System Exception while calling doAuthorization Webservice " + e);
				return new PaypalStatusImpl(this.getNextTransactionId(), false, BBBCoreErrorConstants.PAYPAL_WEBSERVICE_CALL_ERROR, pPaypalBeanInfo);
			} catch (BBBBusinessException e) {
				this.logError("PayPalProcessorImpl.authorize() :: Business Exception while calling doAuthorization Webservice " + e);
				return new PaypalStatusImpl(this.getNextTransactionId(), false, BBBCoreErrorConstants.PAYPAL_WEBSERVICE_CALL_ERROR, pPaypalBeanInfo);
			}
		}
		else if(res != null){
			this.logError("PayPalProcessorImpl.authorize() :: PayPal Do Express Fail: Error ID: " + res.getErrorStatus().getErrorId() + "Error Message: " + res.getErrorStatus().getDisplayMessage());
			return new PaypalStatusImpl(this.getNextTransactionId(), false, Integer.toString(res.getErrorStatus().getErrorId()), pPaypalBeanInfo);
		} else {
            this.logError("PayPalProcessorImpl.authorize() :: PayPal Do Express response  is null");
            return new PaypalStatusImpl(this.getNextTransactionId(), false, BBBCoreErrorConstants.PAYPAL_WEBSERVICE_CALL_ERROR, pPaypalBeanInfo);
		}
		return new PaypalStatusImpl(this.getNextTransactionId(), true, BBBCheckoutConstants.BLANK, pPaypalBeanInfo);
	}

	/**
	 * This method returns timestamp of transaction.
	 * 
	 * @return
	 */
	@SuppressWarnings("static-method")
	private String getNextTransactionId() {
		return String.valueOf(System.currentTimeMillis());
	}

}
