/**
 * 
 */
package com.bbb.rest.framework;

import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.RegistrantVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.commerce.giftregistry.vo.ShippingVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.utils.BBBUtility;

import atg.repository.RepositoryItem;
import atg.rest.filtering.RestPropertyCustomizer;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * 
 * This REST property customizer is used for the defect GFT-1244  & JANSEVTN-72   
 * @author purusr
 *
 */
public class RegistryVOPropertyCustomizer extends BBBGenericService  implements RestPropertyCustomizer{
	@Override
	public Object getPropertyValue(String pPropertyName, Object pResource) {
		Object returnValue = null;	
		logDebug("Entering RegistryVOPropertyCustomizer.getPropertyValue");				
		if(pResource instanceof RegistrantVO ){
			RegistrantVO vo = (RegistrantVO)pResource;
				if ("email".equals(pPropertyName)){
					if(isRecognizedUser()){
						returnValue = BBBGiftRegistryConstants.MASKED;
				} else {
					returnValue = vo.getEmail();
				}
				} else if("cellPhone".equals(pPropertyName)){
					if(isRecognizedUser()){
						returnValue = BBBUtility.maskAllDigits(vo.getCellPhone());
				} else {
					returnValue = vo.getCellPhone();
				}
				} else if("contactAddress".equals(pPropertyName)){
					if(isRecognizedUser()){
						returnValue = new AddressVO();
				} else {
					returnValue = vo.getContactAddress();
				}
				}else if("primaryPhone".equals(pPropertyName)){
					if(isRecognizedUser()){
						returnValue = BBBUtility.maskAllDigits(vo.getCellPhone());
				} else {
					returnValue = vo.getPrimaryPhone();
				}
				}
		}
		if(pResource instanceof RegistryVO ){
			RegistryVO vo = (RegistryVO)pResource;
				if("userAddressList".equals(pPropertyName)){
					if(isRecognizedUser()){
						returnValue = null;
					}else {
					returnValue = vo.getUserAddressList();
					}
				}
		}
		if(pResource instanceof ShippingVO ){
			ShippingVO vo = (ShippingVO)pResource;
				if("shippingAddress".equals(pPropertyName)){
					if(isRecognizedUser()){
						returnValue = new AddressVO();
					}else {
					returnValue = vo.getShippingAddress();
					}
				}else if("futureshippingAddress".equals(pPropertyName)){
					if(isRecognizedUser()){
						returnValue = new AddressVO();
					}else {
					returnValue = vo.getFutureshippingAddress();
					}
				}
		}
			
			
		logDebug("Exiting RegistryVOPropertyCustomizer.getPropertyValue");
		
		return returnValue;
	}

	
	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		throw new UnsupportedOperationException("Not implemented");
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
}
