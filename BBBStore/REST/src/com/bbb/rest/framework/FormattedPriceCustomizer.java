package com.bbb.rest.framework;

import java.util.Properties;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.droplet.BBBCurrencyTagConvertor;
import atg.droplet.TagConversionException;
import atg.rest.filtering.RestPropertyCustomizer;
import atg.servlet.ServletUtil;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;

public class FormattedPriceCustomizer extends BBBGenericService implements
		RestPropertyCustomizer {

	private FormattedPricePropertyManager pricePropertyManager;
	
	public FormattedPricePropertyManager getPricePropertyManager() {
		return this.pricePropertyManager;
	}

	public void setPricePropertyManager(FormattedPricePropertyManager pricePropertyManager) {
		this.pricePropertyManager = pricePropertyManager;
	}

	@Override
	public Object getPropertyValue(String pPropertyName, Object pResource) {
		logDebug("Entering FormattedPriceCustomizer for Property value" + pPropertyName);
		Double pricingProperty=null;
		Object value = null;
		String priceValue= BBBCoreConstants.BLANK;
		if (this.getPricePropertyManager() == null) {
			FormattedPricePropertyManager propertyManager = (FormattedPricePropertyManager) ServletUtil.getCurrentRequest().resolveName("com/bbb/rest/framework/FormattedPricePropertyManager");
			this.setPricePropertyManager(propertyManager);
		}
		
		try {
			Properties prop = new Properties();
		    prop.setProperty("mxprice", "mxprice");
		    prop.setProperty("locale", "en_US");
		    BBBCurrencyTagConvertor objConverter = new BBBCurrencyTagConvertor();
		    String requiredPropertyName = BBBCoreConstants.BLANK;
		    requiredPropertyName = this.getPricePropertyManager().getPricesPropertyName(pPropertyName);
		    value = DynamicBeans.getPropertyValue(pResource, requiredPropertyName);
		    if (value != null && value instanceof String && !value.toString().isEmpty()) {
		    	pricingProperty = Double.valueOf((String) value);
			} else if (value != null & value instanceof Double) {
				pricingProperty = (Double) value;
			}
		    
		    if(pricingProperty != null){
		    	priceValue = objConverter.convertObjectToString(ServletUtil.getCurrentRequest(), pricingProperty, prop).toString();
		    }else{
		    	priceValue = "";
		    }
		} catch (PropertyNotFoundException e) {
	       	logError("FormattedPriceCustomizer::could not find the Property Name :" + pPropertyName + "::returning Empty Value with Exception Log trace :"+e);
		} catch (TagConversionException e) {
			logError("FormattedPriceCustomizer::error occured in Conversion of Prices for Property :" + pPropertyName + "::Exception Log Trace :" +e);
		}
		
		return priceValue.toString();
	}

	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		// Set Property value

	}

}
