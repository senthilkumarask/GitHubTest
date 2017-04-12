package com.bbb.rest.framework;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.rest.filtering.RestPropertyCustomizer;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.RestConstants;
import com.bbb.utils.BBBUtility;

/**
 * This REST property customizer is used to transform the credit Card number into masked Number
 * 
 * @author Manuj Gupta
 *  
 * 
 */

public class CreditCardCustomizer  extends BBBGenericService  implements RestPropertyCustomizer{

	public Object getPropertyValue(String pPropertyName, Object pResource) {
	
		logDebug("Entering CreditCardCustomizer.getPropertyValue");
	
		
		try {
			if(RestConstants.CC_NUMBER.equals(pPropertyName)){
				String creditCardNumber = (String) DynamicBeans.getSubPropertyValue(pResource, pPropertyName);
			
			
				logDebug("CreditCardCustomizer::: Masking Credit Card Number");
				
				return BBBUtility.maskCrediCardNumber(creditCardNumber);
			}else if(RestConstants.CC_CVVNUMBER.equals(pPropertyName)){
				String cvvNumber = (String) DynamicBeans.getSubPropertyValue(pResource, pPropertyName);
				
				
				logDebug("CreditCardCustomizer::: Masking CVV Number");
				
				return BBBUtility.maskAllDigits(cvvNumber);
			}
			
		} catch (PropertyNotFoundException e) {
			logError("CreditCardCustomizer:: Property not found: " + pPropertyName, e);
		}
		
		
			logDebug("CreditCardCustomizer::: could not find the property, returning NULL");
		
		return null;

	}

	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		throw new UnsupportedOperationException("Not implemented");
	}

}
