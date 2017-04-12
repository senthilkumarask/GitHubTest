package com.bbb.rest.framework;

import java.util.Properties;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.droplet.BBBDefaultCurrencyTagConverter;
import atg.droplet.TagConversionException;
import atg.droplet.TagConverterManager;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.rest.filtering.RestPropertyCustomizer;
import atg.servlet.ServletUtil;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.utils.BBBUtility;


/**
 * This class is having methods used for Order history display i n USD price
 * 
 */
public class USDPriceCustomizer  implements
RestPropertyCustomizer {
	private static final ApplicationLogging LOG = ClassLoggingFactory.getFactory().getLoggerForClass(BBBUtility.class);
	private final String USDPROPERTYMANAGER="com/bbb/rest/framework/USDPricePropertyManager";
	private USDPricePropertyManager usdPricePropertyManager;
	public USDPricePropertyManager getUsdPricePropertyManager() {
		return usdPricePropertyManager;
	}

	public void setUsdPricePropertyManager(
			USDPricePropertyManager usdPricePropertyManager) {
		this.usdPricePropertyManager = usdPricePropertyManager;
	}

	@Override
	public Object getPropertyValue(String pPropertyName, Object pResource) {
		if(LOG.isLoggingDebug()){LOG.logDebug("Entering USDPriceCustomizer for Property value" + pPropertyName);}
		Double pricingProperty=null;
		Object value = null;
		String priceValue= BBBCoreConstants.BLANK;
		  BBBDefaultCurrencyTagConverter objConverter = new BBBDefaultCurrencyTagConverter();
		if (this.getUsdPricePropertyManager() == null) {
			USDPricePropertyManager propertyManager = (USDPricePropertyManager) ServletUtil.getCurrentRequest().resolveName(USDPROPERTYMANAGER);
			this.setUsdPricePropertyManager(propertyManager);
		}
		try {
			Properties prop = new Properties();
		    String requiredPropertyName = BBBCoreConstants.BLANK;
		    requiredPropertyName = this.getUsdPricePropertyManager().getPricesPropertyName(pPropertyName);
		    value = DynamicBeans.getPropertyValue(pResource, requiredPropertyName);
		    if (value!= null && value instanceof String && !value.toString().isEmpty()) {
		    	pricingProperty = Double.valueOf((String) value);
			} else if (value != null & value instanceof Double) {
				pricingProperty = (Double) value;
			}
		    priceValue= TagConverterManager
			.getTagConverterByName("defaultCurrency")
			.convertObjectToString(
					ServletUtil.getCurrentRequest(), pricingProperty,
					prop).toString();
		   // priceValue= objConverter.convertObjectToString(ServletUtil.getCurrentRequest(), pricingProperty, prop).toString();
		} catch (PropertyNotFoundException e) {
			LOG.logError("USDPriceCustomizer::could not find the Property Name :" + pPropertyName + "::returning Empty Value with Exception Log trace :"+e);
		} catch (TagConversionException e) {
			LOG.logError("USDPriceCustomizer::could not find the Property Name :" + pPropertyName + "::returning Empty Value with Exception Log trace :"+e);
		}
		 return priceValue;
	}

	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

}
