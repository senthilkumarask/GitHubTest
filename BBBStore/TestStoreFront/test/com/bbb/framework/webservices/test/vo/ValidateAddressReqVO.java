/*
 *
 * File  : ValidateAddressReqVO.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.webservices.test.vo;

import com.bbb.framework.integration.ServiceRequestBase;


/**
 * I am just an example VO. Please ignore me. I am used by AddressManagementServiceMarshaller.
 * @author manohar
 * @version 1.0
 */
public class ValidateAddressReqVO extends ServiceRequestBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The address vo. */
	private AddressVO addressVO;
	
	/** The multiple match ind. */
	private boolean multipleMatchInd;
	
	/** The max matching address to be returned. */
	private int maxMatchingAddressToBeReturned;

	private String serviceName;
	
	/**
	 * 
	 * @param serviceName
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * Very important to implement this method and let the framework know the
	 * web service name that is being implemented
	 */
	
	/**
	 * Very important to implement this method and let the framework know the
	 * whether the web service response needs to be cached
	 */
	@Override
	public Boolean isCacheEnabled() {
		return false;
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Checks if is multiple match ind.
	 * 
	 * @return true, if is multiple match ind
	 */
	public final boolean isMultipleMatchInd() {
		return multipleMatchInd;
	}

	/**
	 * Sets the multiple match ind.
	 * 
	 * @param multipleMatchInd the new multiple match ind
	 */
	public void setMultipleMatchInd(final boolean multipleMatchInd) {
		this.multipleMatchInd = multipleMatchInd;
	}

	/**
	 * Gets the address vo.
	 * 
	 * @return the address vo
	 */
	public final AddressVO getAddressVO() {
		return addressVO;
	}

	/**
	 * Sets the address vo.
	 * 
	 * @param addressVO the new address vo
	 */
	public void setAddressVO(final AddressVO addressVO) {
		this.addressVO = addressVO;
	}

	/**
	 * Gets the max matching address to be returned.
	 * 
	 * @return the max matching address to be returned
	 */
	public final int getMaxMatchingAddressToBeReturned() {
		return maxMatchingAddressToBeReturned;
	}

	/**
	 * Sets the max matching address to be returned.
	 * 
	 * @param maxMatchingAddressToBeReturned the new max matching address to be returned
	 */
	public void setMaxMatchingAddressToBeReturned(
			final int maxMatchingAddressToBeReturned) {
		this.maxMatchingAddressToBeReturned = maxMatchingAddressToBeReturned;
	}

	/*public Collection<Key> getKeys() {

		BBBPerformanceMonitor
		.start("ValidateAddressReqVO-getKeys");
		
		StringBuilder keyString = new StringBuilder(30);
		keyString.append("Address_"); keyString.append(getAddressVO().getAddressLine1());
		keyString.append("City_"); keyString.append(getAddressVO().getCity());
		keyString.append("State_"); keyString.append(getAddressVO().getState());
		keyString.append("Country_"); keyString.append(getAddressVO().getCountry());
		Collection<Key> keys = new HashSet<Key>();
		keys.add(new Key(Key.PRIMARY,keyString.toString()));
		BBBPerformanceMonitor
		.end("ValidateAddressReqVO-getKeys");
		
		return keys;
	}*/
	
	

	
}

