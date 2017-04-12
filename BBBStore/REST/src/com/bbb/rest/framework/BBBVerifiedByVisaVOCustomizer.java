package com.bbb.rest.framework;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.rest.filtering.RestPropertyCustomizer;

import com.bbb.commerce.checkout.vbv.vo.BBBVerifiedByVisaVO;
import com.bbb.commerce.common.BBBVBVSessionBean;
import com.bbb.common.BBBGenericService;

/**
 * This REST property customizer is used to send vbvSessionBean.getbBBVerifiedByVisaVO() properties.
 * 
 * @author Nikhil Gupta
 *  
 * 
 */
public class BBBVerifiedByVisaVOCustomizer  extends BBBGenericService  implements RestPropertyCustomizer{

	public Object getPropertyValue(String pPropertyName, Object pResource) {
	
		
	 logDebug("Entering BBBVerifiedByVisaVOCustomizer.getPropertyValue");
	
		
		BBBVBVSessionBean vbvSessionBean = null;
		//Map siteItems = null;
		BBBVerifiedByVisaVO item = null;
		
		try {
			
			vbvSessionBean = (BBBVBVSessionBean) DynamicBeans.getSubPropertyValue(pResource, pPropertyName);
			
			item = (BBBVerifiedByVisaVO) vbvSessionBean.getbBBVerifiedByVisaVO();
			
		
		 logDebug("BBBVerifiedByVisaVOCustomizer::: value="+item);
		
			
		} catch (PropertyNotFoundException e) {
			logError(e.getMessage(),e);
		}

		return item;

	}

	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		throw new UnsupportedOperationException("Not implemented");
	}

}
