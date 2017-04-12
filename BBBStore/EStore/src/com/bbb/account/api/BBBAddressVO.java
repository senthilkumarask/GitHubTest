package com.bbb.account.api;

import com.bbb.commerce.common.BBBAddressImpl;

public class BBBAddressVO extends BBBAddressImpl implements Comparable<BBBAddressVO>{

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 6235841294398967547L;

	@Override
	public int compareTo(BBBAddressVO arg0) {
		int compareResult = 0;
		
		if(null!= super.getIdentifier() && null!= arg0 ){
			compareResult = super.getIdentifier().compareTo( arg0.getIdentifier());
		}
		return compareResult;
		
	}
}
