package com.bbb.commerce.order;

import java.util.ArrayList;
import java.util.List;

import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.core.util.Address;

import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;

public class TBSShippingGroupManager extends BBBShippingGroupManager {

	/**
	 * This method is update to avoid the creation of the new shipping group, in case of only one shipping group for multishipping group.
	 */
	public BBBHardGoodShippingGroup getSGForIdAndMethod(final Order pOrder, final BBBAddressContainer pContainer,
			final String pSourceId, final String pShippingMethod) throws CommerceException{

		BBBHardGoodShippingGroup bbbHGSG = null;
		
		//set the values to the default shipping group
		for (final Object obj : pOrder.getShippingGroups()) {
			if(obj instanceof BBBHardGoodShippingGroup){
				bbbHGSG = (BBBHardGoodShippingGroup)obj;
				final Address pAddress = (Address)pContainer.getAddressFromContainer(pSourceId);
				bbbHGSG.setShippingAddress(pAddress);
				bbbHGSG.setShippingMethod(pShippingMethod);
				bbbHGSG.setSourceId(pSourceId);
				if( BBBCheckoutConstants.REGISTRY_SOURCE.equalsIgnoreCase( ((BBBAddress)pAddress).getSource()) ){
					bbbHGSG.setRegistryId(((BBBAddress)pAddress).getId());
					bbbHGSG.setRegistryInfo(getRegistryInfo(pOrder, ((BBBAddress)pAddress).getId()));
				}
			}
		}
		return bbbHGSG;
	}
}
