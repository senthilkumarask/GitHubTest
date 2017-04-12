/**
 * 
 */
package com.bbb.payment.creditcard;

import atg.payment.creditcard.CreditCardInfo;

/**
 * @author alakra
 *
 */
public interface BBBCreditCardInfo extends CreditCardInfo {
	
	/**
	 * @return the nameOnCard
	 */
	public String getNameOnCard();

	/**
	 * @param pNameOnCard the nameOnCard to set
	 */
	public void setNameOnCard(String pNameOnCard);
	
	/**
	 * @return the lastFourDigits
	 */
	public String getLastFourDigits();

	/**
	 * @param lastFourDigits the lastFourDigits to set
	 */
	public void setLastFourDigits(String lastFourDigits);
}
