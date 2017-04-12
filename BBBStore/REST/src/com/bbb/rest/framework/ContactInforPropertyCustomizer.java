package com.bbb.rest.framework;

import atg.rest.filtering.RestPropertyCustomizer;

import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.utils.BBBUtility;

public class ContactInforPropertyCustomizer implements RestPropertyCustomizer{
	public Object getPropertyValue(String pPropertyName, Object pResource) {
		String returnValue="";
		if(pResource instanceof BBBRepositoryContactInfo){
			BBBRepositoryContactInfo repositoryCotactInfo=(BBBRepositoryContactInfo)pResource;
			
			if(BBBUtility.isNotEmpty(repositoryCotactInfo.getRegistryId())||(repositoryCotactInfo.getSource()!=null && repositoryCotactInfo.getSource().equalsIgnoreCase("registry"))){
				if(pPropertyName.equals("repositoryItem")){
					return null;
				}
				return "masked";
			}			
			if(pPropertyName.equals("firstName"))
				returnValue=repositoryCotactInfo.getFirstName();
			else if(pPropertyName.equals("lastName"))
				returnValue=repositoryCotactInfo.getLastName();
			else if(pPropertyName.equals("email"))
				returnValue=repositoryCotactInfo.getEmail();
			else if(pPropertyName.equals("address1"))
				returnValue=repositoryCotactInfo.getAddress1();
			else if(pPropertyName.equals("address2"))
				returnValue=repositoryCotactInfo.getAddress2();
			else if(pPropertyName.equals("city"))
				returnValue=repositoryCotactInfo.getCity();
		}
		return returnValue;		

	}

	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		throw new UnsupportedOperationException("Not implemented");
	}

}
