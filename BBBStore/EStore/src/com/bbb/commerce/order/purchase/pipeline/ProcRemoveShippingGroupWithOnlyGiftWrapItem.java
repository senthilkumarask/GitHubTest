package com.bbb.commerce.order.purchase.pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.processor.SavedProperties;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.ecommerce.order.BBBOrderImpl;

public class ProcRemoveShippingGroupWithOnlyGiftWrapItem extends SavedProperties implements PipelineProcessor {

	/**
	 * constant of the BBBOrderManager
	 */	
	private BBBOrderManager orderManager;
	
	/**
	 * constant of the BBBCommerceItemManager
	 */		
	private BBBCommerceItemManager	commerceItemManager;	
	
	
	/** This is the 2 pipeline link in the update order pipeline. It is responsible to 
	 *  remove the shiping group if it contains only the gift wrap item.
	 *  
	 * @param	 Object, PipelineResult
	 * 
	 * @return	int
	 */
	@SuppressWarnings("unchecked")
    public int runProcess(Object params, PipelineResult pipelineResult) 
			throws java.lang.Exception {

		if (isLoggingDebug()){
			logDebug("Entered  ProcRemoveShippingGroupWithOnlyGiftWrapItem" + params);
		}
		
		Map<String, Object> requestInputMap = (Map<String, Object>) params;
		
		BBBOrderImpl order = (BBBOrderImpl) requestInputMap.get(PipelineConstants.ORDER);
		
		if (order != null && !order.getStateAsString().equalsIgnoreCase("Incomplete")) {
			
			if (isLoggingDebug()){
				logDebug("Order is not in Incomplete state ");
			}
			return SUCCESS;
		} else if (order != null){
			//First remove items which does not have SG relatonships from Order
			removeItemsWithNoSGRel(order);
			
			HashMap<String, List<String>> map = new HashMap<String, List<String>>();
			List<ShippingGroup> shippingGroups = order.getShippingGroups();
			List<String> giftWrapIdList;
			for (ShippingGroup shippingGroup : shippingGroups) {
				if(isLoggingDebug()){
					logDebug("-----------------------------------------------------------------------");
					logDebug("" + shippingGroup);
					logDebug("-----------------------------------------------------------------------");
				}
				boolean foundNonGiftWrapItem = false;
				boolean foundGiftWrapItem = false;
				giftWrapIdList = new ArrayList<String>();
				
				if(shippingGroup instanceof HardgoodShippingGroup) {
					
					List<CommerceItemRelationship> commerceItemRelationshipList = shippingGroup.getCommerceItemRelationships();
					for (CommerceItemRelationship commerceItemRelationship : commerceItemRelationshipList) {
						
						if (commerceItemRelationship != null) {
							if(isLoggingDebug()){
								logDebug("CommerceItemClassType: " + commerceItemRelationship.getCommerceItem().getCommerceItemClassType());
							}
							
							if (commerceItemRelationship.getCommerceItem().getCommerceItemClassType().equalsIgnoreCase("giftWrapCommerceItem")) {
								foundGiftWrapItem = true;
								giftWrapIdList.add(commerceItemRelationship.getCommerceItem().getId());								
							} else {
								foundNonGiftWrapItem = true;								
							}							
						}
					}
					
					if (foundGiftWrapItem && !foundNonGiftWrapItem) {
						map.put(shippingGroup.getId(), giftWrapIdList);
						if (isLoggingDebug()){
							logDebug("Shipping Group Removed");
						}						
					}
					//Remove if more than one giftWrapId items in SG
					if(giftWrapIdList.size() > 1){
						Iterator<String> giftWrapIdListIterator = giftWrapIdList.iterator();
						giftWrapIdListIterator.remove();
						while(giftWrapIdListIterator.hasNext()){
							getCommerceItemManager().removeItemFromOrder(order, giftWrapIdListIterator.next());
							giftWrapIdListIterator.remove();
						}
					}				
					
				} else {
					continue;
				}
			}
			
			removeShippingGroupFromList(order, map);
			
		}
		return SUCCESS;
	}

	/**
	 * @param order
	 * @throws CommerceException
	 */
	private void removeItemsWithNoSGRel(BBBOrderImpl order)
			throws CommerceException {
		@SuppressWarnings("unchecked")
		List<CommerceItem> commItemList = order.getCommerceItems();
		List<String> removalItemIdList = new ArrayList<String>();
		for (CommerceItem commerceItem : commItemList) {
			if(commerceItem.getShippingGroupRelationshipCount() >0){
				continue;
			}else{
				removalItemIdList.add(commerceItem.getId());				
			}
		}
		
		for (String itemId : removalItemIdList) {
			getCommerceItemManager().removeItemFromOrder(order, itemId);
		}
	}

	private void removeShippingGroupFromList(BBBOrderImpl order, Map<String, List<String>> map) throws CommerceException {
		
		if (isLoggingDebug()){
		   logDebug("removeShippingGroupFromList called for order -: " + order );
		   logDebug("Shipping group removal map: "+ map );
		}	
		
		String shippingGroupId; 
		List<String> giftWrapIdList ;
		
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {

		    if (isLoggingDebug()){
				   logDebug("Key = " + entry.getKey() + ", Value = " + entry.getValue());
				}
		    
		    shippingGroupId = entry.getKey();
		    giftWrapIdList = entry.getValue();
		    
		    if (order != null && shippingGroupId!=null && !giftWrapIdList.isEmpty()) {
		    	try{
		    		
		    		for (String id : giftWrapIdList) {
		    			getCommerceItemManager().removeItemFromOrder(order, id);
		    			getShippingGroupManager().removeShippingGroupFromOrder(order, shippingGroupId);
		    			
		    		}
		    	}catch(CommerceException ce){
		    	    if(isLoggingError()) {
		    	        logError("error removing giftwrap item from order",ce);
		    	    }
		    	}
		    }		
		}
		
		
		
	}
	
	/**
	 * @return the commerceItemManager
	 */
	public BBBCommerceItemManager getCommerceItemManager() {
		return commerceItemManager;
	}

	/**
	 * @param commerceItemManager the commerceItemManager to set
	 */
	public void setCommerceItemManager(BBBCommerceItemManager commerceItemManager) {
		this.commerceItemManager = commerceItemManager;
	}

	/**
     *Overriden method of PipelineProcessor which indicated the return code for run process method.
     *
     * @return Int: Array of int .
     */
    public int[] getRetCodes() {
        int[] retn = {SUCCESS};
        return retn;
    }
    
	/**
	 * @return the orderManager
	 */
	public BBBOrderManager getOrderManager() {
		return orderManager;
	}

	/**
	 * @param orderManager the orderManager to set
	 */
	public void setOrderManager(BBBOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	private BBBShippingGroupManager shippingGroupManager; 


	/**
	 * @return the shippingGroupManager
	 */
	public BBBShippingGroupManager getShippingGroupManager() {
		return shippingGroupManager;
	}

	/**
	 * @param shippingGroupManager the shippingGroupManager to set
	 */
	public void setShippingGroupManager(BBBShippingGroupManager shippingGroupManager) {
		this.shippingGroupManager = shippingGroupManager;
	}

    

	/**
     * Final Static variable for success.
     */
	private static final int SUCCESS = 1;
	
}
