/*
 *
 * File  : ValidateAddressResVO.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.webservices.test.vo;

import java.util.List;

import com.bbb.framework.integration.ServiceResponseBase;

/**
 * I am just an example VO. Plesae ignore me. I am used by AddressManagementServiceUnMarshaller.
 * @author manohar
 * @version 1.0
 */
public class ValidateAddressResVO extends ServiceResponseBase {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	

	/** The address v os. */
	private AddressVO[] addressVOs;
	
	/**
	 * Gets the address v os.
	 * 
	 * @return the address v os
	 */
	public final AddressVO[] getAddressVOs() {
		return addressVOs;
	}

	/**
	 * Sets the address v os.
	 * 
	 * @param addressVOs the new address v os
	 */
	public void setAddressVOs(final AddressVO[] addressVOs) {
		this.addressVOs = addressVOs;
	}

	/** The address vo list. */
	private List<AddressVO> addressVOList;

	/**
	 * Gets the address vo list.
	 * 
	 * @return the address vo list
	 */
	public final List<AddressVO> getAddressVOList() {
		return addressVOList;
	}

	/**
	 * Sets the address vo list.
	 * 
	 * @param addressVOList the new address vo list
	 */
	public void setAddressVOList(final List<AddressVO> addressVOList) {
		this.addressVOList = addressVOList;
	}
	
}