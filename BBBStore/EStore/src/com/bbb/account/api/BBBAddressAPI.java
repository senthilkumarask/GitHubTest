package com.bbb.account.api;

import java.util.List;

import atg.core.util.Address;
import atg.repository.RepositoryItem;
import atg.userprofiling.Profile;


import com.bbb.commerce.common.BBBAddress;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.paypal.BBBAddressPPVO;

public interface BBBAddressAPI {

	public BBBAddressVO getDefaultShippingAddress (Profile profile, String siteId) 
			throws BBBSystemException, BBBBusinessException;
	
	public BBBAddressVO getDefaultBillingAddress (Profile profile, String siteId)
			throws BBBSystemException, BBBBusinessException;
	
	public  List<BBBAddressVO> getShippingAddress (Profile profile, String siteId)
			throws BBBSystemException, BBBBusinessException;
	
	public List<BBBAddressVO> getBillingAddress (Profile profile, String siteId)
			throws BBBSystemException, BBBBusinessException;
		
	public BBBAddressVO addNewShippingAddress (Profile profile, BBBAddressVO addressVO, String siteId) 
			throws BBBSystemException, BBBBusinessException;
	
	public BBBAddress addNewBillingAddress(Profile profile, BBBAddress pAddress,
			String siteId, boolean isDefaultBillingAddress) throws BBBSystemException, BBBBusinessException;
	
	public BBBAddress addNewShippingAddress (Profile profile, BBBAddress pAddress, String siteId, boolean isDefaultShippingAddress, boolean isDefaultBillingAddress) 
			throws BBBSystemException, BBBBusinessException;
	
	public BBBAddress addNewShippingAddress(Profile profile, BBBAddressVO address, String siteId, boolean isDefaultShippingAddress, boolean isDefaultBillingAddress)
			throws BBBSystemException, BBBBusinessException;
	
	public void updateAddressToProfile(Profile profile, BBBAddressVO addressVO, String addressId, String siteId) 
			throws BBBSystemException;
	
	public  BBBAddressVO fetchAddress(Profile profile, String siteId, String addressRepositoryId)
			throws BBBSystemException, BBBBusinessException;
	
	public void copyBBBAddress(RepositoryItem defaultShippingAddress,BBBAddressVO addressVO);
	
	public void copyToBillAddress(RepositoryItem defaultShippingAddress,Address address);
	public void copyBBBAddressVO(BBBAddressVO addressVO,Address address);

	public BBBAddress addNewPayPalShippingAddress(Profile profile,BBBAddressPPVO addressVO, String siteId, boolean isDefaultShippingAddress, boolean isDefaultBillingAddress) throws BBBSystemException,
	 BBBBusinessException;
}
