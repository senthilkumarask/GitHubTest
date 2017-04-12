/**
 * 
 */
package com.bbb.integration.cybersource.creditcard;

import atg.payment.creditcard.CreditCardStatus;

/**
 * @author alakra
 *
 */
public interface BBBCreditCardStatus extends CreditCardStatus {
	
	public String getAuthorizationCode();
	
	public void setAuthorizationCode(String pAuthCode);
	
	public String getAuthResponseRecord();
	
	public void setAuthResponseRecord(String pAuthResposeRecord);

}
