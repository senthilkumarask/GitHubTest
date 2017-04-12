package com.bbb.rest.framework;


import java.util.Properties;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.droplet.TagConversionException;
import atg.droplet.TagConverterManager;
import atg.rest.filtering.RestPropertyCustomizer;
import atg.servlet.ServletUtil;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;

public class LocalePriceCustomizer extends BBBGenericService  implements RestPropertyCustomizer{

	public Object getPropertyValue(String pPropertyName, Object pResource) {
	
		logDebug("Entering LocalePriceCustomizer for Property value" + pPropertyName);
		Double pricingProperty=null;
		Object value =null;
		String priceValue= BBBCoreConstants.BLANK;
		try {
			Properties prop = new Properties();
		    prop.setProperty("mxprice", "mxprice");
		    prop.setProperty("locale", "en_US");
		    prop.setProperty("formatPrice", "false");
			value = DynamicBeans.getPropertyValue(pResource, pPropertyName);
			 if (value != null && value instanceof String ) {
				 if (!value.toString().isEmpty()) {
					 pricingProperty =  Double.parseDouble(value.toString());
					 priceValue = (TagConverterManager.getTagConverterByName("currency").convertObjectToString(ServletUtil.getCurrentRequest(), pricingProperty, prop)).toString();
				 }
			} else {
				pricingProperty = (Double) value;
				priceValue = (TagConverterManager.getTagConverterByName("currency").convertObjectToString(ServletUtil.getCurrentRequest(), pricingProperty, prop)).toString();
			}
		} catch (PropertyNotFoundException e) {
			logError("LocalePriceCustomizer::could not find the Property Name :" + pPropertyName + "::returning Empty Value with Exception Log trace :"+e);
		} catch (TagConversionException e) {
			logError("LocalePriceCustomizer:: error occured in Conversion of Prices for Property :" + pPropertyName + "::Exception Log Trace :" +e);
		}

		return Double.parseDouble(priceValue);

	}

	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		// Set Property Value
		
	}
}
