package com.bbb.commerce.payment.processor;

import atg.commerce.CommerceException;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.commerce.payment.processor.ProcProcessPaymentGroup;
import atg.payment.PaymentStatus;

import com.bbb.commerce.payment.BBBPaymentManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.payment.giftcard.GenericGiftCardInfo;


/**
 * Pipeline Processor for Gift card.
 * 
 * @author vagra4
 * 
 */
public class ProcProcessGiftCard extends ProcProcessPaymentGroup {

	public ProcProcessGiftCard() {
		//constructor
	}

	/**
	 * This method redeems a gift card. Before redeem this method validates gift
	 * card balance.
	 * 
	 * @param giftCertificateInfo
	 * @return
	 */
	public PaymentStatus authorizePaymentGroup(
			PaymentManagerPipelineArgs pParams) throws CommerceException {

		if (isLoggingDebug()) {
			logDebug("Starting method ProcProcessGiftCard.authorizePaymentGroup");
		}
		BBBOrder order =(BBBOrder) pParams.getOrder();
		
		
		GenericGiftCardInfo gci = (GenericGiftCardInfo) pParams
				.getPaymentInfo();
		

		if (isLoggingDebug()) {
			logDebug("Exiting method ProcProcessGiftCard.authorizePaymentGroup");
		}
		
		if((order.getSubStatus().equalsIgnoreCase(BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_RESTORE_INVENTORY)|| order.getSubStatus().equalsIgnoreCase(BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_IGNORE_INVENTORY) )){
			return ((BBBPaymentManager) pParams.getPaymentManager())
			.getGiftCardProcessor().authorizeDummyOrderGiftCard(gci);	
		}
		return ((BBBPaymentManager) pParams.getPaymentManager())
				.getGiftCardProcessor().authorize(gci);
	}

	/**
	 * Method to debit a payment group.
	 */
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
	public PaymentStatus creditPaymentGroup(PaymentManagerPipelineArgs pParams)
			throws CommerceException {
		/**
		 * This method is not required for gift card.
		 */
		return null;
	}

}
