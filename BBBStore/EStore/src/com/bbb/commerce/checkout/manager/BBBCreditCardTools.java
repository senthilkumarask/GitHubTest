package com.bbb.commerce.checkout.manager;

import java.util.HashMap;
import java.util.Map;

import atg.payment.creditcard.ExtendableCreditCardTools;

import com.bbb.utils.BBBUtility;

public class BBBCreditCardTools extends ExtendableCreditCardTools {

	
	@SuppressWarnings("rawtypes")
    private Map cardCVVLengthsMap = new HashMap();
		
	public int validateSecurityCode (String cvv, String type) {
		
		if (BBBUtility.isNotEmpty(cvv) && BBBUtility.isNotEmpty(type)) {
			String requiredCVVLength = (String) getCardCVVLengthsMap().get(type);
			int enteredCVVLengh = cvv.length();

			if (Integer.parseInt(requiredCVVLength) != enteredCVVLengh) {
				return 15;
			} else {
				return 0;
			}
		}else {
			return 15;
		}
		
	}
	
	/*public int verifyCreditCard (CreditCardInfo creditCardInfo) {
		int success = super.verifyCreditCard(creditCardInfo);
		if (success == 0) {
			success = validateSecurityCode(((BasicBBBCreditCardInfo)creditCardInfo).
					getCardVerificationNumber(), ((BasicBBBCreditCardInfo)creditCardInfo).getCreditCardType());
			return success;
		} else {
			return success;
		}
	}*/
	
	// This method not required since we OOTB is working for us and new error message will come from CMS.
    /*public String getStatusCodeMessage(int pCode) {
    	
    	if (super.getStatusCodeMessage(pCode, null) != null) {
    		return getStatusCodeMessage(pCode, null);
    	} else {
    		return new String ("Please enter valid CVV number");
    	}
        
    }*/

	/**
	 * @return the cardCVVLengthsMap
	 */
	@SuppressWarnings("rawtypes")
    public Map getCardCVVLengthsMap() {
		return cardCVVLengthsMap;
	}

	/**
	 * @param cardCVVLengthsMap the cardCVVLengthsMap to set
	 */
	@SuppressWarnings("rawtypes")
    public void setCardCVVLengthsMap(Map cardCVVLengthsMap) {
		this.cardCVVLengthsMap = cardCVVLengthsMap;
	}
	
	
}
