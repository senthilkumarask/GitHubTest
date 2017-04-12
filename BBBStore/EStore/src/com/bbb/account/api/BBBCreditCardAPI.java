package com.bbb.account.api;

import java.util.List;

import atg.userprofiling.Profile;


import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public interface BBBCreditCardAPI {

	public BasicBBBCreditCardInfo getDefaultCreditCard (Profile profile, String siteId) 
			throws BBBSystemException, BBBBusinessException;
	
	public List getUserCreditCardWallet (Profile profile, String siteId)			
			throws BBBSystemException, BBBBusinessException;
	
	public BasicBBBCreditCardInfo addNewCreditCard (Profile profile, BasicBBBCreditCardInfo cardInfo, String siteId)
			throws BBBSystemException, BBBBusinessException;
	
}
