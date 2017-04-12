package com.bbb.ecommerce.pricing;

import java.beans.IntrospectionException;
import java.util.List;

import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.OrderTools;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingException;
import atg.core.util.Address;
import atg.payment.tax.TaxRequestInfo;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.userprofiling.address.AddressTools;

public class TBSTaxProcessorTaxCalculator extends BBBTaxProcessorTaxCalculator {
	
	private OrderTools orderTools;

	/**
	 * Property to hold shippingAddressClassName.
	 */
	private String shippingAddressClassName;
	/**
	 * Property to hold TBSSearchStoreManager reference.
	 */
	private TBSSearchStoreManager mSearchStoreManager;
	
	public OrderTools getOrderTools() {
		return orderTools;
	}

	public void setOrderTools(OrderTools orderTools) {
		this.orderTools = orderTools;
	}
	
	public String getShippingAddressClassName() {
		return shippingAddressClassName;
	}

	public void setShippingAddressClassName(String shippingAddressClassName) {
		this.shippingAddressClassName = shippingAddressClassName;
	}
	/**
	 * @return the mSearchStoreManager
	 */
	public TBSSearchStoreManager getSearchStoreManager() {
		return mSearchStoreManager;
	}

	/**
	 * @param pSearchStoreManagerp the mSearchStoreManager to set
	 */
	public void setSearchStoreManager(TBSSearchStoreManager pSearchStoreManagerp) {
		mSearchStoreManager = pSearchStoreManagerp;
	}

	/**
     * This method is overridden to set the dummy HardGoodShippngGroup for the CMO orders to get the Tax.
     */
	  @SuppressWarnings("unchecked")
	protected Address determineShippingAddress(ShippingGroup pGroup) {
		  ShippingGroup shipGroup = null;
	    if (pGroup instanceof HardgoodShippingGroup) {
	      return ((HardgoodShippingGroup) pGroup).getShippingAddress();
	    } if (pGroup instanceof BBBStoreShippingGroup) {
	    	boolean cmoflag = false;
	    	RepositoryItem currentStoreItem = null;
	    	try {
	    		List<CommerceItemRelationship> shipCitemRelations = ((BBBStoreShippingGroup)pGroup).getCommerceItemRelationships();
	    		
	    		for (CommerceItemRelationship cItemRelationship : shipCitemRelations) {
	    			CommerceItem citem = cItemRelationship.getCommerceItem();
	    			if(citem instanceof TBSCommerceItem && ((TBSCommerceItem)citem).isCMO()){
	    				cmoflag = true;
	    				break;
	    			}
				}
	    		if(cmoflag){
	    			Address storeAddr = null;
	    			currentStoreItem = getSearchStoreManager().getStoreRepository().getItem(((BBBStoreShippingGroup)pGroup).getStoreId(), TBSConstants.STORE);
	    			try {
						storeAddr = (Address) Class.forName(getShippingAddressClassName()).newInstance();
						if(storeAddr != null && currentStoreItem != null){
							storeAddr.setAddress1((String) currentStoreItem.getPropertyValue("address"));
							storeAddr.setCity((String) currentStoreItem.getPropertyValue("city"));
							storeAddr.setState((String) currentStoreItem.getPropertyValue("state"));
							storeAddr.setPostalCode((String) currentStoreItem.getPropertyValue("postalCode"));
							storeAddr.setCountry(((String) currentStoreItem.getPropertyValue("countryCode")).substring(0, 2));
						}
					} catch (ClassNotFoundException e) {
						
					} catch (InstantiationException e) {
						
					} catch (IllegalAccessException e) {
						
					} 
	    			shipGroup = getOrderTools().createShippingGroup(getOrderTools().getDefaultShippingGroupType());
	    			if (shipGroup instanceof HardgoodShippingGroup) {
	    				AddressTools.copyAddress(storeAddr, ((HardgoodShippingGroup) shipGroup).getShippingAddress());
	    				return ((HardgoodShippingGroup) shipGroup).getShippingAddress();
	    			}
	    		}
	    	} catch(CommerceException e){
	    		
	    	} catch(RepositoryException e){
	    		
	    	}  catch(IntrospectionException e){
	    		
	    	}
	    }
	    return null;
	  }
	  
	  
	@Override
	protected boolean verifyShippingAddress(TaxRequestInfo pTRI) throws PricingException {
		List<CommerceItem> commerceItems = pTRI.getOrder().getCommerceItems();
		boolean cmo = false;
		for (CommerceItem item : commerceItems) {
			if (item instanceof TBSCommerceItem) {
				TBSCommerceItem tbsCommerceItem = (TBSCommerceItem) item;
				if (tbsCommerceItem.isCMO()) {
					cmo = true;
				}
			}
		}

		if (cmo) {
			return true;
		} else {
			return super.verifyShippingAddress(pTRI);
		}
	}
	  
}
