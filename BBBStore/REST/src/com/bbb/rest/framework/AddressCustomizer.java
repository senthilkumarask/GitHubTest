package com.bbb.rest.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.rest.filtering.RestPropertyCustomizer;
import atg.userprofiling.Profile;

import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.common.BBBGenericService;
import com.bbb.utils.BBBUtility;


public class AddressCustomizer  extends BBBGenericService  implements RestPropertyCustomizer{

	@SuppressWarnings({ "unchecked" })
	public Object getPropertyValue(String pPropertyName, Object pResource) {
	
		
		
		logDebug("Entering AddressCustomizer.getPropertyValue");
		
		
		List<String> POBoxAddressList = new ArrayList<String>();
		
		try {
			if(pResource instanceof Profile)
			{
				
					Map<String,RepositoryItem> secondaryAddressMap = (Map<String,RepositoryItem>)((Profile) pResource).getPropertyValue("secondaryAddresses");
					for(String address : secondaryAddressMap.keySet())
					{
						RepositoryItem item  = (RepositoryItem)secondaryAddressMap.get(address);
						BBBRepositoryContactInfo contactInfo = new BBBRepositoryContactInfo((MutableRepositoryItem) item);
						if(!BBBUtility.isNonPOBoxAddress(contactInfo.getAddress1(), contactInfo.getAddress2())){
							POBoxAddressList.add(contactInfo.getId());
						}
					}
			}
			
			
				logDebug("AddressCustomizer::: value="+POBoxAddressList);
			
			
		} catch (Exception e) {
			logError(e.getMessage(),e);
		}

		return POBoxAddressList;

	}

	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		throw new UnsupportedOperationException("Not implemented");
	}

}
