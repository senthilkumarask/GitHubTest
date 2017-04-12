package com.bbb.commerce.payment.processor;

import atg.commerce.CommerceException;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.commerce.payment.processor.ProcProcessPaymentGroup;
import atg.payment.PaymentStatus;

import com.bbb.commerce.order.PaypalBeanInfo;
import com.bbb.commerce.payment.BBBPaymentManager;

/**
 * Pipeline Processor for Gift card.
 * 
 * @author vagra4
 * 
 */
public class ProcProcessPaypal extends ProcProcessPaymentGroup {

	public ProcProcessPaypal() {
		//constructor
	}

	/**
	 * This method redeems a gift card. Before redeem this method validates gift
	 * card balance.
	 * 
	 * @param giftCertificateInfo
	 * @return
	 */
	@Override
	public PaymentStatus authorizePaymentGroup(PaymentManagerPipelineArgs pParams) throws CommerceException {

		if (isLoggingDebug()) {
			logDebug("Starting method ProcProcessPayPal.authorizePaymentGroup");
		}
		PaypalBeanInfo payPalInfo = (PaypalBeanInfo) pParams.getPaymentInfo();
		
		if (isLoggingDebug()) {
			logDebug("Exiting method ProcProcessGiftCard.authorizePaymentGroup");
		}
		
		
		return ((BBBPaymentManager) pParams.getPaymentManager())
				.getPayPalProcessor().authorize(payPalInfo);
	}

	/**
	 * Method to debit a payment group.
	 */
	@Override
	public PaymentStatus debitPaymentGroup(PaymentManagerPipelineArgs pParams)
			throws CommerceException {
		/**
		 * This method is not required for gift card.
		 */
		return null;
	}

	/**
	 * Method to credit a payment group.
	 */
	@Override
	public PaymentStatus creditPaymentGroup(PaymentManagerPipelineArgs pParams)
			throws CommerceException {
		/**
		 * This method is not required for gift card.
		 */
		return null;
	}

}
