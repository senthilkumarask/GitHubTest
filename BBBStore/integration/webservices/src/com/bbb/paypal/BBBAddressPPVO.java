package com.bbb.paypal;

import com.bbb.commerce.common.BBBAddressImpl;

public class BBBAddressPPVO extends BBBAddressImpl implements Comparable<BBBAddressPPVO>{

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 6235841294398967547L;

	private boolean fromPayPal;
	
	private String addressStatus;
	
	public BBBAddressPPVO(){
		//default constructor
	}

	@Override
	public int compareTo(BBBAddressPPVO arg0) {
		int compareResult = 0;
		
		if(null!= super.getIdentifier() && null!= arg0 ){
			compareResult = super.getIdentifier().compareTo( arg0.getIdentifier());
		}
		return compareResult;
		
	}
	
	
	/**
	 * @return the addressStatus
	 */
	public String getAddressStatus() {
		return addressStatus;
	}

	/**
	 * @param addressStatus the addressStatus to set
	 */
	public void setAddressStatus(String addressStatus) {
		this.addressStatus = addressStatus;
	}

	/**
	 * @return
	 */
	public boolean isFromPayPal() {
		return this.fromPayPal;
	}

	/**
	 * @param fromPayPal
	 */
	public void setFromPayPal(boolean fromPayPal) {
		this.fromPayPal = fromPayPal;
	}
	
}
