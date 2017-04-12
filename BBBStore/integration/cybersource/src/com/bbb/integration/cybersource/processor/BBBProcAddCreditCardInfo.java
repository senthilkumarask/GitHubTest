package com.bbb.integration.cybersource.processor;

import java.util.List;

import atg.commerce.order.PaymentGroup;
import atg.integrations.cybersourcesoap.cc.CreditCardProcParams;
import atg.integrations.cybersourcesoap.cc.processor.ProcAddCreditCardInfo;
import atg.service.pipeline.PipelineResult;

import com.bbb.commerce.order.BBBCreditCard;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.cybersource.stub.Card;
import com.cybersource.stub.RequestMessage;

/**
 * 
 * Processor sends request to CyberSource and sets reply to params
 * 
 */
public class BBBProcAddCreditCardInfo extends ProcAddCreditCardInfo {

	/**
	 * Adds detailed info to the request if necessary
	 */
	@SuppressWarnings("unchecked")
	protected int runCreditCardProcess(CreditCardProcParams pParams, PipelineResult pResult) throws Exception {
		super.runCreditCardProcess(pParams, pResult);
		
		BBBCreditCard creditCard = null;
		RequestMessage request = pParams.getRequest(); 
	    Card card = request.getCard();
		for(PaymentGroup paymentGroup : (List<PaymentGroup>)pParams.getCreditCardInfo().getOrder().getPaymentGroups()){
			if(paymentGroup instanceof BBBCreditCard){
				creditCard = (BBBCreditCard)paymentGroup;
			    if(creditCard.getNameOnCard() != null) {
			    	card.setFullName(creditCard.getNameOnCard());
			    }
			}
		}	    

	    request.setCard(card);
	    
	    //BSL-628 | Add Online Order number as merchant reference code
	    if (((BBBOrder) pParams.getCreditCardInfo().getOrder())
				.getOnlineBopusItemsStatusInOrder().equalsIgnoreCase(
						BBBCheckoutConstants.BOPUS_ONLY)) {
			request.setMerchantReferenceCode(((BBBOrder) pParams
					.getCreditCardInfo().getOrder()).getBopusOrderNumber());
		} else {
			request.setMerchantReferenceCode(((BBBOrder) pParams
					.getCreditCardInfo().getOrder()).getOnlineOrderNumber());
		}
	    
	    if(isLoggingDebug()){
	    	logDebug("MerchantReferenceCode: " + request.getMerchantReferenceCode());
	    }

		return SUCCESS;
	}
	
	private String mOnlineOrderPrefixKey;
	private String mCartAndCheckOutConfigType;

	/**
	 * @return the mOnlineOrderPrefixKey
	 */
	public String getOnlineOrderPrefixKey() {
		return mOnlineOrderPrefixKey;
	}
	/**
	 * @param mOnlineOrderPrefixKey the mOnlineOrderPrefixKey to set
	 */
	public void setOnlineOrderPrefixKey(String pOnlineOrderPrefixKey) {
		this.mOnlineOrderPrefixKey = pOnlineOrderPrefixKey;
	}
	/**
	 * @return the mCartAndCheckOutConfigType
	 */
	public String getCartAndCheckOutConfigType() {
		return mCartAndCheckOutConfigType;
	}
	/**
	 * @param mCartAndCheckOutConfigType the mCartAndCheckOutConfigType to set
	 */
	public void setCartAndCheckOutConfigType(String pCartAndCheckOutConfigType) {
		this.mCartAndCheckOutConfigType = pCartAndCheckOutConfigType;
	}
}