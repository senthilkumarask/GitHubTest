package com.bbb.rest.framework;

import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBGiftRegistryConstants;

import atg.repository.RepositoryItem;
import atg.rest.filtering.RestPropertyCustomizer;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * This REST property customizer is used to transform the order object into REST BBBOrderVO
 * 
 * @author Gaurav Bisht
 * @version $Change: 698235 $$DateTime: 2012/04/20 06:41:21 $$Author: jsiddaga $
 * 
 * 
 */

public class RegistrySummaryVOPropertyCustomizer extends BBBGenericService  implements RestPropertyCustomizer{

	public static String CLASS_VERSION = "$Id: //hosting-blueprint/MobileCommerce/version/10.1.1/server/MobileCommerce/src/atg/rest/filtering/customizers/RepriceCartPropertyCustomizer.java#1 $$Change: 698235 $";

	public Object getPropertyValue(String pPropertyName, Object pResource) {	
		Object returnValue = null;	
		logDebug("Entering RegistrySummaryVOPropertyCustomizer.getPropertyValue");				
		if(pResource instanceof RegistrySummaryVO ){
			RegistrySummaryVO vo = (RegistrySummaryVO)pResource;
			if(vo != null && vo.isRegistryOwnedByProfile()){
				if ("shippingAddress".equals(pPropertyName)){
					if(isRecognizedUser()){
						returnValue = new AddressVO();			
					}else{
						returnValue = vo.getShippingAddress();
					}
				}else if("futureShippingAddress".equals(pPropertyName)){
					if(isRecognizedUser()){
						returnValue = new AddressVO();			
					}else{
						returnValue = vo.getFutureShippingAddress();
					}
				}else if("futureShippingDate".equals(pPropertyName)){
					returnValue = vo.getFutureShippingDate();
				}else if("registrantEmail".equals(pPropertyName)){
					if(isRecognizedUser()){
						returnValue = BBBGiftRegistryConstants.MASKED;			
					}else{
						returnValue = vo.getRegistrantEmail();
					}
				}else if("bridalToolkitToken".equals(pPropertyName)){
					returnValue = vo.getBridalToolkitToken();
				}else if("pwsurl".equals(pPropertyName)){
					returnValue = vo.getPwsurl();
				}else if("regToken".equals(pPropertyName)){
					returnValue = vo.getRegToken();
				}else if("primaryRegistrantEmail".equals(pPropertyName)){
					returnValue = vo.getPrimaryRegistrantEmail();
				}else if("primaryRegistrantPrimaryPhoneNum".equals(pPropertyName)){
					returnValue = vo.getPrimaryRegistrantPrimaryPhoneNum();
				}else if("coRegistrantEmail".equals(pPropertyName)){
					if(isRecognizedUser()){
						returnValue = BBBGiftRegistryConstants.MASKED;			
					}else{
						returnValue = vo.getCoRegistrantEmail();
					}
				}else if("ownerProfileID".equals(pPropertyName)){
					returnValue = vo.getOwnerProfileID();
				}else if("coRegistrantMaidenName".equals(pPropertyName)){
					if(isRecognizedUser()){
						returnValue = null;			
					}else{
						returnValue = vo.getCoRegistrantMaidenName();
					}
				}else if("coRegistrantFullName".equals(pPropertyName)){
					if(isRecognizedUser()){
						returnValue = null;			
					}else{
						returnValue = vo.getCoRegistrantFullName();
					}
				}else if("registryInfo".equals(pPropertyName)){
					if(isRecognizedUser()){
						returnValue = null;			
					}else{
						returnValue = vo.getRegistryInfo();
					}
				}
				
			}else{
				if("shippingAddress".equals(pPropertyName)){
					returnValue = new AddressVO();
				} else if("futureShippingAddress".equals(pPropertyName)){
					returnValue = new AddressVO();
				}else if ("coRegistrantFullName".equals(pPropertyName)){
					returnValue = vo.getCoRegistrantFullName();
				}else if ("coRegistrantMaidenName".equals(pPropertyName)){
					returnValue = vo.getCoRegistrantMaidenName();
				}else{
					returnValue = BBBGiftRegistryConstants.MASKED;
				}
			}
		}
			
		
		logDebug("Exiting RegistrySummaryVOPropertyCustomizer.getPropertyValue");
		

		return returnValue;

	}
	
	/**
	 * We getting info for user is in recognize state or logged in state
	 * @return
	 */
	private boolean isRecognizedUser(){
		DynamoHttpServletRequest currentRequest = ServletUtil.getCurrentRequest();
		RepositoryItem profile = ServletUtil.getCurrentUserProfile();
		if(null != currentRequest && null != profile) {
			BBBProfileTools profileTools = (BBBProfileTools) currentRequest.resolveName("/atg/userprofiling/ProfileTools");
			return profileTools.isRecognizedUser(currentRequest, profile);
		}
		return false;
	}
	
	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		throw new UnsupportedOperationException("Not implemented");
	}
}
