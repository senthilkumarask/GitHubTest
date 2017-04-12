package com.bbb.commerce.checkout.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.account.BBBAddressTools;
import com.bbb.account.api.BBBAddressAPI;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * To populate addresses entered for shipping, profile addresses
 * 
 */
public class TBSBillingAddressDroplet extends BBBBillingAddressDroplet {


	/**
	 * 
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
	    BBBPerformanceMonitor.start("BBBBillingAddressDroplet", "service");
		if (isLoggingDebug()) {
			logDebug("Entry BBBBillingAddressDroplet.service");
		}
		String selectedAddrKey = null;
		BBBAddress billingAddress = null;

		final BBBOrder order = (BBBOrder) pRequest
				.getObjectParameter(BBBCoreConstants.ORDER);

		final Profile profile = (Profile) pRequest
				.getObjectParameter(BBBCoreConstants.PROFILE);

		BBBAddressContainer billingAddrContainer = (BBBAddressContainer) pRequest
				.getObjectParameter(BBBCoreConstants.BILLING_ADDRESS_CONTAINER);

		if (null == order.getBillingAddress()) {
			billingAddrContainer.initialize();
		}

		billingAddrContainer.getAddressMap().clear();
		
		
		
		// get order's billing address if present and make it preselected
		if (null != getCheckoutMgr()) {
			billingAddress = getCheckoutMgr().getBillingAddress(order);
			if (null != billingAddress) {
				if(!StringUtils.isEmpty(billingAddress.getCountry())){
				    try {
					Map<String,String> countryMap = ((BBBCatalogToolsImpl)getCatalogTools()).getCountriesInfoForCreditCard(billingAddress.getCountry());
					if(countryMap != null && countryMap.size() == 1){
					    billingAddress.setCountryName(countryMap.get(billingAddress.getCountry()));
					}
				    } catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "Error getting valid countries"), e);
				    } catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(pRequest, "Error getting valid countries"), e);
				    }
				} else {
				    billingAddress.setCountryName(null);
				}
				billingAddrContainer.addAddressToContainer(billingAddress.getId(),billingAddress);
				String sourceKey = billingAddrContainer.getSourceKey(billingAddress
						.getId());
				if (null != sourceKey) {
					billingAddrContainer.removeAddressFromContainer(sourceKey);
				}
				selectedAddrKey = billingAddress.getId();
			}
		}
		
		// If user is authenticated, get billing addresses from profile and add
		// them to address container only if it is not already order's billing
		// address
		List <BBBAddress> profileAddList = new ArrayList<BBBAddress>();
		BBBAddress defaultProfileAddress =null;
		if (isUserAuthenticated(profile)) {
			try {
				pRequest.setParameter("isUserAuthed", "TRUE");
				pRequest.setAttribute("isUserAuthed", "TRUE");
				if (null != getAddressAPI()) {
					for (BBBAddress address : getAddressAPI()
							.getShippingAddress(profile,
									SiteContextManager.getCurrentSiteId())) {

						// Check if there is any empty address in the profile, then skip that address to add into the container.
						if (null != address && !StringUtils.isEmpty(address.getAddress1())) {

							profileAddList.add(address);
							
							if (null != address
									&& !compareAddressesList(billingAddrContainer.getDuplicate(),address)
									&& (null == billingAddress || !BBBAddressTools.compare((Address)address, (Address)billingAddress))) {
								billingAddrContainer.addAddressToContainer(
										address.getId(), (BBBAddress) address);
							}
						}
						
						/*if(address.isDefault()){
							selectedAddrKey = address.getId();
						}*/
					}
					
					
					// get default profile address 
					defaultProfileAddress = (BBBAddress)(getAddressAPI().getDefaultBillingAddress(profile,
					SiteContextManager.getCurrentSiteId()));
					
					//set deafult address ID as preselected
					if(null != defaultProfileAddress && !BBBAddressTools.isNullAddress((Address)defaultProfileAddress) && StringUtils.isBlank(selectedAddrKey) ){
						//selectedAddrKey = defaultProfileAddress.getId();
						selectedAddrKey = getSelectedKey(profileAddList, defaultProfileAddress, selectedAddrKey);
					}
					
					
				}
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, "Error getting billing address from profile"), e);
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(pRequest, "Error getting billing address from profile"), e);
			}
		}

		// get all shipping addresses for that order and add them to address
		// container only if it is not already order's billing address and make
		// any
		// one address preselected
		if (null != order
				&& null != getShippingGroupManager()
				&& null != getShippingGroupManager()
						.getNonRegistryShippingAddress(order)
				&& !getShippingGroupManager().getNonRegistryShippingAddress(
						order).isEmpty()) {
			List<BBBAddress> list=new ArrayList<BBBAddress>();
			for (BBBAddress address : getShippingGroupManager()
					.getNonRegistryShippingAddress(order)) {
				
				if(StringUtils.isBlank(address.getSource())){

					if (!compareAddressesList(billingAddrContainer.getDuplicate(),address)
							&& !BBBAddressTools.compare((Address)address, (Address)billingAddress)){
						if(!compareAddressesList(profileAddList, address)){

							billingAddrContainer.addAddressToContainer(address.getId(),
									address);
							list.add(address);
							billingAddrContainer.setDuplicate(list);

							if (StringUtils.isBlank(selectedAddrKey)) {
								selectedAddrKey = address.getId();
							}
						}else{
							if (StringUtils.isBlank(selectedAddrKey)) {
								selectedAddrKey = getSelectedKey(profileAddList, address, selectedAddrKey);
							}
						}

					}

				}
			}
		}
		List<String> savedTerritories = new ArrayList<String>();
		if(!billingAddrContainer.getAddressMap().isEmpty()){
			Iterator iterator = billingAddrContainer.getAddressMap().entrySet().iterator();
			do {
				Map.Entry<String, BBBAddress> entry = (Map.Entry<String, BBBAddress>)iterator.next();
				String key = entry.getKey();
			    BBBAddress storedAddress = entry.getValue();
			    if(getCheckoutMgr().getNotSavableStates().contains(storedAddress.getState())){
			    	iterator.remove();
			    }
			} while(iterator.hasNext());			
		}
		
		pRequest.setParameter("selectedAddrKey", selectedAddrKey);
		pRequest.setParameter("addresses", billingAddrContainer.getAddressMap());
		pRequest.serviceParameter("output", pRequest, pResponse);
		BBBPerformanceMonitor.end("BBBBillingAddressDroplet", "service");
	}

	/**
	 * 
	 * @param profile
	 * @return
	 */
	private boolean isUserAuthenticated(Profile profile) {
		if (isLoggingDebug()) {
			logDebug("Entry BBBBillingAddressDroplet.isAuthenticated");
		}
		boolean result = false;
		final Integer securityStatus = (Integer) profile
				.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS);
		if (!profile.isTransient() && null != securityStatus
				&& securityStatus >= 4) {
			result = true;
		}
		return result;
	}
	
	
	/**
	 * This method compares the address with the addresses in the list.
	 * 
	 * @param addressList
	 * 			List of Addresses 
	 * @param address
	 * @return boolean
	 */
	private boolean compareAddressesList(List<BBBAddress> addressList ,BBBAddress address) {
        boolean result = false;
       if(addressList != null && !addressList.isEmpty()){
          for(BBBAddress addItem :addressList){        	  
        	  if(address != null && compareAddress(addItem, address)) {
        		  result = true  ;        		  
        	  } 
          }
		}
        return result;
    }
	
	/**
	 * This method compares two addresses.
	 * 
	 * @param add1
	 * 			Address
	 * @param add2
	 * 			Address
	 * @return boolean
	 */
	
	private boolean compareAddress(BBBAddress add1,BBBAddress add2){
		
		boolean result = false;
		if(null != add2 && null != add1 ) {
  		  if(StringUtils.isEmpty(add2.getOwnerId())) {
  			  add2.setOwnerId(add1.getOwnerId());            
  		  }
  		  if(StringUtils.isEmpty(add1.getAddress2()) && 
  				  StringUtils.isEmpty(add2.getAddress2())) {//profile store null and sg store "" string, normalizing the objects
  			  add1.setAddress2(add2.getAddress2());
  		  }
  		  if(StringUtils.isEmpty(add1.getMiddleName()) && 
  				  StringUtils.isEmpty(add2.getMiddleName())) {//profile store null and sg store "" string, normalizing the objects
  			  add1.setMiddleName(add2.getMiddleName());
  		  }
  		  if(((Address)add1).equals(add2)) {
  			result =  true;
  			
  		  }
  		  
  	  } 
		return result;
	}
	
	/**
	 * This method compares the address with the addresses in the list, if match is found then 
	 * address id is returned as the key.
	 * 
	 * @param addressList
	 * 			List of addresses.
	 * @param address
	 * 			Address to compare with.
	 * @param key
	 * 			pre selected key.
	 * @return Selected key
	 */
	private String getSelectedKey(List<BBBAddress> addressList,
			BBBAddress address, String key) {

		for (BBBAddress profileAddItem : addressList) {
			if (compareAddress(profileAddItem, address)) {
				key = profileAddItem.getId();
			}
		}

		return key;
	}
		 
	
}
