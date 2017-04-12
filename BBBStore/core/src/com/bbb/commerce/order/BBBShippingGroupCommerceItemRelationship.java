package com.bbb.commerce.order;

import atg.commerce.order.PropertyNameConstants;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;

public class BBBShippingGroupCommerceItemRelationship extends ShippingGroupCommerceItemRelationship {
	
    /**
	 * default version ID
	 */
	private static final long serialVersionUID = 1L;

	public double getAmount(){
    	return ((Double) getPropertyValue(PropertyNameConstants.AMOUNT)).doubleValue();
    }

    public void setAmount(double d){
    	setPropertyValue(PropertyNameConstants.AMOUNT, d);
    }


}
