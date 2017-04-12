/**
 * 
 */
package com.bbb.commerce.payment;

import org.apache.commons.lang.StringUtils;

import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.RelationshipNotFoundException;
import atg.commerce.payment.PaymentManager;

import com.bbb.commerce.order.PayAtRegisterProcessorImpl;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.payment.giftcard.GiftCardProcessor;

/**
 * @author vagra4
 *
 */
public class BBBPaymentManager extends PaymentManager {
	
	private static final double ONE_DOLLAR_AMOUNT = 1.0;
	
	private GiftCardProcessor mGiftCardProcessor;
	private PayPalProcessorImpl payPalProcessor;
	
	/**
	 * mPayAtRegisterProcessor property to hold PayAtRegisterProcessor object
	 */
	private PayAtRegisterProcessorImpl mPayAtRegisterProcessor;

	/**
	 * @return the payPalProcessor
	 */
	public PayPalProcessorImpl getPayPalProcessor() {
		return payPalProcessor;
	}

	/**
	 * @param payPalProcessor the payPalProcessor to set
	 */
	public void setPayPalProcessor(PayPalProcessorImpl payPalProcessor) {
		this.payPalProcessor = payPalProcessor;
	}

	/**
	 * @return the mGiftCardProcessor
	 */
	public GiftCardProcessor getGiftCardProcessor() {
		return mGiftCardProcessor;
	}

	/**
	 * @param pGiftCardProcessor the mGiftCardProcessor to set
	 */
	public void setGiftCardProcessor(GiftCardProcessor pGiftCardProcessor) {
		mGiftCardProcessor = pGiftCardProcessor;
	}
	
	/**
	 * @return the payAtRegisterProcessor
	 */
	public PayAtRegisterProcessorImpl getPayAtRegisterProcessor() {
		return mPayAtRegisterProcessor;
	}

	/**
	 * @param pPayAtRegisterProcessor the payAtRegisterProcessor to set
	 */
	public void setPayAtRegisterProcessor(
			PayAtRegisterProcessorImpl pPayAtRegisterProcessor) {
		mPayAtRegisterProcessor = pPayAtRegisterProcessor;
	}
	
	public void authorize(Order pOrder, PaymentGroup pPaymentGroup) throws CommerceException {
		BBBOrder bbbOrder = (BBBOrder) pOrder;
		double paymentGroupAmount = pPaymentGroup.getAmount();
		if(pPaymentGroup instanceof CreditCard
		        && StringUtils.equals(bbbOrder.getOnlineBopusItemsStatusInOrder(), BBBCheckoutConstants.BOPUS_ONLY)){
				if(isLoggingDebug()){
					logDebug("Setting the Credit card group [" + pPaymentGroup.getId() + "] amount [" + pPaymentGroup.getAmount() + "] to $1 AUTH for pure BOPUS order [" + bbbOrder.getId() + "]");
				}
				paymentGroupAmount = ONE_DOLLAR_AMOUNT;
		}
		authorize(bbbOrder, pPaymentGroup, paymentGroupAmount);		
	}
	
	public boolean isAmountTooHigh(double pAmountSoFar, double pNewAmount, PaymentGroup pPaymentGroup) {
		double amount = pAmountSoFar + pNewAmount;
		double pgAmount = pPaymentGroup.getAmount();
		amount = getPricingTools().round(amount);
		
		/*Check if this is pure BOPUS order (i.e. requested authorization amount = $1.0)*/
		try {
			BBBOrder bbbOrder = (BBBOrder)pPaymentGroup.getOrderRelationship().getOrder();
			if(StringUtils.equals(bbbOrder.getOnlineBopusItemsStatusInOrder(), BBBCheckoutConstants.BOPUS_ONLY) && (Double.compare(pNewAmount, ONE_DOLLAR_AMOUNT) == BBBCoreConstants.ZERO)){
				return false;
			}
		} catch (RelationshipNotFoundException e) {
			if(isLoggingError()){
				logError("Could not find relation between payment group & order", e);
			}
		} catch (InvalidParameterException e) {
			if(isLoggingError()){
				logError("Invalid parameter while retrieving relation between payment group & order", e);
			}
		}
		return amount > pgAmount;
	}
}
