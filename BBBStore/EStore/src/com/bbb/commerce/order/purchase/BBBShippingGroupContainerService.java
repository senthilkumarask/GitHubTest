/**
 * @author jpadhi
 * @version Id: //com.bbb.commerce.order.purchase/BBBShippingGroupContainerService.java.BBBShippingGroupContainerService $$
 * @updated $DateTime: Jan 2, 2012 3:47:43 PM
 */
package com.bbb.commerce.order.purchase;

import java.util.Collection;
import java.util.Date;

import com.bbb.commerce.common.BBBAddress;

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.ShippingGroupContainerService;
import atg.core.util.Address;

/**
 * @author jpadhi
 *
 */
public class BBBShippingGroupContainerService extends
		ShippingGroupContainerService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see atg.commerce.order.purchase.ShippingGroupContainerService#getNewShippingGroupName(atg.commerce.order.ShippingGroup, java.util.Collection)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String getNewShippingGroupName(ShippingGroup pShippingGroup,
			Collection pShippingGroupMatchers) {
		
		if(pShippingGroup instanceof HardgoodShippingGroup){
			Address addr = ((HardgoodShippingGroup) pShippingGroup).getShippingAddress();
			if(addr != null && addr instanceof BBBAddress){
				return   ((BBBAddress)addr).getIdentifier();
			}else{				
				return String.valueOf(new Date().getTime()); 
			}			
		}else{
			return pShippingGroup.getId();
		}		
	}
	
	/* (non-Javadoc)
	 * @see atg.commerce.order.purchase.ShippingGroupContainerService#getShippingGroupName(atg.commerce.order.ShippingGroup, java.util.Collection)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String getShippingGroupName(ShippingGroup pShippingGroup,
			Collection pShippingGroupMatchers) {
		
		if(pShippingGroup instanceof HardgoodShippingGroup){
			Address addr = ((HardgoodShippingGroup) pShippingGroup).getShippingAddress();
			if(addr != null && addr instanceof BBBAddress){
				return   ((BBBAddress)addr).getIdentifier();
			}else{				
				return String.valueOf(new Date().getTime()); 
			}
		}else{
			return pShippingGroup.getId();
		}
	}

}
