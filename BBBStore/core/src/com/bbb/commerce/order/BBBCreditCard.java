/**
 * 
 */
package com.bbb.commerce.order;

import atg.commerce.order.CreditCard;
import atg.repository.RemovedItemException;

import com.bbb.payment.creditcard.BBBCreditCardInfo;



/**
 * @author alakra
 *
 */
public class BBBCreditCard extends CreditCard implements BBBCreditCardInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7422251861510704231L;
	
	private static final String NAME_ON_CARD = "nameOnCard";
	
	private static final String LAST_FOUR_DIGITS = "lastFourDigits";

	/**
	 * @return the lastFourDigits
	 */
	public String getLastFourDigits() {
		return (String) getPropertyValue(LAST_FOUR_DIGITS);
	}

	/**
	 * @param lastFourDigits the lastFourDigits to set
	 */
	public void setLastFourDigits(String lastFourDigits) {
		setPropertyValue(LAST_FOUR_DIGITS, lastFourDigits);
	}

	/**
	 * @return the nameOnCard
	 */
	public final String getNameOnCard() {
		return (String) getPropertyValue(NAME_ON_CARD);
	}

	/**
	 * @param pNameOnCard the nameOnCard to set
	 */
	public final void setNameOnCard(String pNameOnCard) {
		setPropertyValue(NAME_ON_CARD, pNameOnCard);
	}
	
   public String toString()
   {
     StringBuffer sb = new StringBuffer("PaymentGroup[");
     try {
       String id = getId();
       sb.append("id:").append(id).append("; ");
       String paymentMethod = getPaymentMethod();
       sb.append("paymentMethod:").append(paymentMethod).append("; ");
       sb.append("amount:").append(getAmount()).append("; ");
       sb.append("amountAuthorized:").append(getAmountAuthorized()).append("; ");
       sb.append("amountCredited:").append(getAmountCredited()).append("; ");
       sb.append("amountDebited:").append(getAmountDebited()).append("; ");
       sb.append("pNameOnCard:").append(getNameOnCard()).append("; ");
       sb.append("LastFourDigits:").append(getLastFourDigits()).append("; ");
       String state = getStateAsString();
       sb.append("state:").append(state);
     }
     catch (RemovedItemException exc) {
       sb.append("removed");
     }
     sb.append("]");
 
     return sb.toString();
   }
}