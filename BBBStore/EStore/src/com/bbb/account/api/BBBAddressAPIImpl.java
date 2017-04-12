package com.bbb.account.api;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryException;
import atg.userprofiling.Profile;
import atg.userprofiling.address.AddressTools;

import com.bbb.account.BBBAddressObject;
import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.paypal.BBBAddressPPVO;
import com.bbb.utils.BBBUtility;

public class BBBAddressAPIImpl extends BBBGenericService implements BBBAddressAPI {

	public BBBProfileTools mProfileTools;
	
	/**
	 * @return the mProfileTools
	 */
	public BBBProfileTools getProfileTools() {
		return mProfileTools;
	}

	/**
	 * @param mProfileTools the mProfileTools to set
	 */
	public void setProfileTools(BBBProfileTools mProfileTools) {
		this.mProfileTools = mProfileTools;     
	}

	@Override
	public BBBAddressVO getDefaultShippingAddress(Profile profile, String siteId)
			throws BBBSystemException, BBBBusinessException {
		
		BBBAddressVO addressVO = new BBBAddressVO();
		
			logDebug("BBBAddressAPIImpl | getDefaultShippingAddress method starts");
		
		RepositoryItem defaultShippingAddress = getProfileTools().getDefaultShippingAddress(profile);
		if(defaultShippingAddress != null){
			copyBBBAddress(defaultShippingAddress, addressVO);
//			try {
//	            AddressTools.copyAddress(defaultShippingAddress, addressVO);
//	        } catch (IntrospectionException e) {
//	            logError("BBBAddressAPIImpl | getShippingAddress exception ",e);
//	            throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1278,"EXCEPTION WHILE COPY ADDRESS",e);
//	        }
		
		}
		addressVO.setSource(BBBAddressAPIConstants.SOURCE);
		addressVO.setIdentifier(addressVO.getSource()+addressVO.getId());
		
		return addressVO;
	}
		
		public void copyBBBAddress(RepositoryItem defaultShippingAddress, BBBAddressVO addressVO){
		if(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_FIRST_NAME)!=null) {
			addressVO.setFirstName(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_FIRST_NAME).toString());
		}
		if(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_LAST_NAME)!=null) {
			addressVO.setLastName(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_LAST_NAME).toString());
		}
		if(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_COMPANY_NAME)!=null) {
			addressVO.setCompanyName(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_COMPANY_NAME).toString());
		}
		
		if(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS1)!=null) {
			addressVO.setAddress1(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS1).toString());
		}
		if(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS2)!=null) {
			addressVO.setAddress2(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS2).toString());
		}
		if(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS3)!=null) {
			addressVO.setAddress3(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS3).toString());
		}
		
		if(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_CITY)!=null) {
			addressVO.setCity(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_CITY).toString());
		}
		if(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_STATE)!=null) {
			addressVO.setState(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_STATE).toString());
		}
		if(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_POSTAL_CODE)!=null) {
			addressVO.setPostalCode(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_POSTAL_CODE).toString());
		}
		if(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_COUNTRY)!=null) {
			addressVO.setCountry(defaultShippingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_COUNTRY).toString());
		}
		//changes for QAS
		if(defaultShippingAddress.getPropertyValue("poBoxAddress")!=null) {
			addressVO.setPoBoxAddress((Boolean)defaultShippingAddress.getPropertyValue("poBoxAddress"));
		}
		if(defaultShippingAddress.getPropertyValue("qasValidated")!=null) {
			addressVO.setQasValidated((Boolean)defaultShippingAddress.getPropertyValue("qasValidated"));
		}
		addressVO.setId(defaultShippingAddress.getRepositoryId());
		
		logDebug("BBBAddressAPIImpl | getDefaultShippingAddress method ends"+addressVO);
		}
		
		public void copyBBBAddressVO(BBBAddressVO  addressVO, Address address){
			
			if(addressVO.getFirstName()!=null) {
				address.setFirstName(addressVO.getFirstName());
			}
			if(addressVO.getLastName()!=null) {
				address.setLastName(addressVO.getLastName());
			}		
			if(addressVO.getAddress1()!=null) {
				address.setAddress1(addressVO.getAddress1());
			}
			if(addressVO.getAddress2()!=null) {
				address.setAddress2(addressVO.getAddress2());
			}
			if(addressVO.getAddress3()!=null) {
				address.setAddress3(addressVO.getAddress3());
			}
			if(addressVO.getCity()!=null) {
				address.setCity(addressVO.getCity());
			}
			if(addressVO.getState()!=null) {
				address.setState(addressVO.getState());
			}
			if(addressVO.getPostalCode()!=null) {
				address.setPostalCode(addressVO.getPostalCode());
			}
			if(addressVO.getCountry()!=null) {
				address.setCountry(addressVO.getCountry());
			}
			
				logDebug("BBBAddressAPIImpl | getDefaultShippingAddress method ends"+addressVO);
			}
		
		
		
		public void copyToBillAddress(RepositoryItem fromAddress, Address addressVO){
			if(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_FIRST_NAME)!=null) {
				addressVO.setFirstName(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_FIRST_NAME).toString());
			}
			if(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_LAST_NAME)!=null) {
				addressVO.setLastName(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_LAST_NAME).toString());
			}			
			if(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS1)!=null) {
				addressVO.setAddress1(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS1).toString());
			}
			if(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS2)!=null) {
				addressVO.setAddress2(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS2).toString());
			}
			if(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS3)!=null) {
				addressVO.setAddress3(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS3).toString());
			}
			if(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_CITY)!=null) {
				addressVO.setCity(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_CITY).toString());
			}
			if(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_STATE)!=null) {
				addressVO.setState(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_STATE).toString());
			}
			if(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_POSTAL_CODE)!=null) {
				addressVO.setPostalCode(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_POSTAL_CODE).toString());
			}
			if(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_COUNTRY)!=null) {
				addressVO.setCountry(fromAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_COUNTRY).toString());
			}
			
				logDebug("BBBAddressAPIImpl | getDefaultShippingAddress method ends"+addressVO);
			}


	@Override
	public BBBAddressVO getDefaultBillingAddress(Profile profile, String siteId)
			throws BBBSystemException, BBBBusinessException {
		
		logDebug("BBBAddressAPIImpl | getDefaultBillingAddress method starts");
		RepositoryItem defaultBillingAddress = getProfileTools().getDefaultBillingAddress(profile);
		
		if (defaultBillingAddress == null) {
			return null;
		}
			BBBAddressVO addressVO = new BBBAddressVO();
			if(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_FIRST_NAME)!=null) {
				addressVO.setFirstName(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_FIRST_NAME).toString());
			}
			if(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_LAST_NAME)!=null) {
				addressVO.setLastName(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_LAST_NAME).toString());
			}
			if(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_COMPANY_NAME)!=null) {
				addressVO.setCompanyName(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_COMPANY_NAME).toString());
			}
			
			if(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS1)!=null) {
				addressVO.setAddress1(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS1).toString());
			}
			if(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS2)!=null) {
				addressVO.setAddress2(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS2).toString());
			}
			if(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS3)!=null) {
				addressVO.setAddress3(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS3).toString());
			}
			if(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_CITY)!=null) {
				addressVO.setCity(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_CITY).toString());
			}
			if(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_STATE)!=null) {
				addressVO.setState(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_STATE).toString());
			}
			if(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_COUNTRY)!=null) {
				addressVO.setCountry(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_COUNTRY).toString());
			}
			if(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_POSTAL_CODE)!=null) {
				addressVO.setPostalCode(defaultBillingAddress.getPropertyValue(BBBAddressAPIConstants.PPTY_POSTAL_CODE).toString());
			}			
		
		
		
		
		
		logDebug("BBBAddressAPIImpl | getDefaultBillingAddress method ends"+addressVO);
		addressVO.setSource(BBBAddressAPIConstants.SOURCE);
		return addressVO;
		
	}

	@Override
	public  List<BBBAddressVO> getShippingAddress(Profile profile, String siteId)
			throws BBBSystemException, BBBBusinessException {
		
			logDebug("BBBAddressAPIImpl | getShippingAddress method starts");
		List<BBBAddressVO> addressList = new ArrayList<BBBAddressVO>();
		BBBAddressVO addressVO;
		Iterator it = getProfileTools().getAllShippingAddresses(profile).iterator();
		while(it.hasNext()){

			addressVO = new BBBAddressVO();
			RepositoryItem repos =(RepositoryItem)it.next();
				copyBBBAddress(repos, addressVO);
//			try {
//				AddressTools.copyAddress(repos, addressVO);
//			} catch (IntrospectionException e) {
//				logError("BBBAddressAPIImpl | getShippingAddress exception ",e);
//				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1278,"EXCEPTION WHILE COPY ADDRESS",e);
//			}
			/*if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_FIRST_NAME)!=null){
				addressVO.setFirstName(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_FIRST_NAME).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_LAST_NAME)!=null){
				addressVO.setLastName(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_LAST_NAME).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_COMPANY_NAME)!=null){
				addressVO.setCompanyName(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_COMPANY_NAME).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS1)!=null){
				addressVO.setAddress1(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS1).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS2)!=null){
				addressVO.setAddress2(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS2).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_STATE)!=null){
				addressVO.setState(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_STATE).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_COUNTRY)!=null){
				addressVO.setCountry(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_COUNTRY).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_POSTAL_CODE)!=null){
				addressVO.setPostalCode(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_POSTAL_CODE).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_CITY)!=null){
				addressVO.setCity(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_CITY).toString());
			}*/
			addressVO.setSource(BBBAddressAPIConstants.SOURCE);
			addressVO.setSourceIdentifier(addressVO.getId());
			addressVO.setId((String)repos.getPropertyValue("id"));
			addressVO.setIdentifier(addressVO.getSource()+addressVO.getId());
			addressList.add(addressVO);
		}
		
			logDebug("BBBAddressAPIImpl | getShippingAddress method ends"+addressList);
		return addressList;
	}

	@Override
	public List<BBBAddressVO> getBillingAddress(Profile profile, String siteId)
			throws BBBSystemException, BBBBusinessException {
		
			logDebug("BBBAddressAPIImpl | getBillingAddress method starts");
		
		List<BBBAddressVO> addressList = new ArrayList<BBBAddressVO>();
		BBBAddressVO addressVO;
		Iterator it = getProfileTools().getAllBillingAddresses(profile).iterator();
		while(it.hasNext()){

			addressVO = new BBBAddressVO();
			RepositoryItem repos =(RepositoryItem)it.next();

				copyBBBAddress(repos, addressVO);
				//AddressTools.copyAddress(repos, addressVO);
			
/*
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_FIRST_NAME)!=null){
				addressVO.setFirstName(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_FIRST_NAME).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_LAST_NAME)!=null){
				addressVO.setLastName(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_LAST_NAME).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_COMPANY_NAME)!=null){
				addressVO.setCompanyName(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_COMPANY_NAME).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS1)!=null){
				addressVO.setAddress1(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS1).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS2)!=null){
				addressVO.setAddress2(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_ADDRESS2).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_STATE)!=null){
				addressVO.setState(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_STATE).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_COUNTRY)!=null){
				addressVO.setCountry(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_COUNTRY).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_POSTAL_CODE)!=null){
				addressVO.setPostalCode(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_POSTAL_CODE).toString());
			}
			if((repos).getPropertyValue(BBBAddressAPIConstants.PPTY_CITY)!=null){
				addressVO.setCity(repos.getPropertyValue(BBBAddressAPIConstants.PPTY_CITY).toString());
			}*/
			addressVO.setSource(BBBAddressAPIConstants.SOURCE);
			addressList.add(addressVO);
		}
		
			logDebug("BBBAddressAPIImpl | getBillingAddress method ends"+addressList);
		return addressList;
	}
	
	@Override
	public BBBAddressVO addNewShippingAddress(Profile profile,
			BBBAddressVO addressVO, String siteId) throws BBBSystemException,
			BBBBusinessException {
		logDebug("BBBAddressAPIImpl | addNewShippingAddress method starts");
		if (profile != null && addressVO != null 
				&& !BBBUtility.isEmpty(addressVO.getAddress1()) 
				&& !BBBUtility.isEmpty(addressVO.getPostalCode()) 
				&& !BBBUtility.isEmpty(addressVO.getCity()) 
				&& !BBBUtility.isEmpty(addressVO.getState()) 
				&& siteId != null) {

			BBBAddressVO rtnAddressVO = new BBBAddressVO();
			String id = null;
			BBBAddressObject addressObject = new BBBAddressObject();

			addressObject.setFirstName(addressVO.getFirstName());
			addressObject.setLastName(addressVO.getLastName());
			addressObject.setAddress1(addressVO.getAddress1());
			addressObject.setAddress2(addressVO.getAddress2());
			addressObject.setAddress3(addressVO.getAddress3());
			addressObject.setCompanyName(addressVO.getCompanyName());
			addressObject.setCity(addressVO.getCity());
			addressObject.setState(addressVO.getState());
			addressObject.setCountry(addressVO.getCountry());
			addressObject.setPostalCode(addressVO.getPostalCode());
			addressObject.setPoBoxAddress(addressVO.isPoBoxAddress());
			addressObject.setQasValidated(addressVO.isQasValidated());
			// Sample Test Data
			/*
			 * addressObject.setFirstName("shipFnamenal1027");
			 * addressObject.setLastName("shipLnamenal1027");
			 * addressObject.setAddress1("shipAdrl1027");
			 * addressObject.setAddress2("shipAdr21027");
			 * 
			 * addressObject.setCity("shipCity1027");
			 * addressObject.setState("shipState1027");
			 * addressObject.setCountry("shipCountry1027");
			 * addressObject.setPostalCode("12345");
			 */

			try {
				String nickName = getProfileTools().getUniqueShippingAddressNickname(addressObject, profile, null);
				id = getProfileTools().createProfileRepositorySecondaryAddress(profile, nickName, addressObject);
				// creditCardNickName=getProfileTools().getProfileAddressById(id).getPropertyValue("creditCardNickName").toString();
				getProfileTools().setDefaultShippingAddress(profile, nickName);
			} catch (Exception e) {
				logError("addNewShippingAddress", e);
			}
			rtnAddressVO.setId(id);

			rtnAddressVO.setSource(BBBAddressAPIConstants.SOURCE);
			rtnAddressVO.setIdentifier(rtnAddressVO.getSource() + rtnAddressVO.getId());
			logDebug("BBBAddressAPIImpl | addNewShippingAddress method ends" + rtnAddressVO.getId());
			return rtnAddressVO;
		} else {
			logInfo("profile=" + profile + " addressVO=" + addressVO + " siteId=" + siteId);
			logDebug("BBBAddressAPIImpl | addNewShippingAddress method ends");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1279,"EXCEPTION WHILE ADDNEWSHIPPINGADDRESS");
		}
	}

	@Override
	public BBBAddress addNewBillingAddress(Profile profile, BBBAddress pAddress, String siteId, boolean isDefaultBillingAddress) throws BBBSystemException, BBBBusinessException {

		logDebug("BBBAddressAPIImpl | addNewBillingAddress method starts");
		if (profile != null && pAddress != null 
				&& !BBBUtility.isEmpty(pAddress.getAddress1()) 
				&& !BBBUtility.isEmpty(pAddress.getPostalCode()) 
				&& !BBBUtility.isEmpty(pAddress.getCity()) 
				&& !BBBUtility.isEmpty(pAddress.getState()) 
				&& siteId != null) {
			BBBAddress rtnAddressVO = new BBBAddressVO();
			String id = null;

			BBBAddressObject addressObject = new BBBAddressObject();
			addressObject.setFirstName(pAddress.getFirstName());
			addressObject.setLastName(pAddress.getLastName());
			addressObject.setAddress1(pAddress.getAddress1());
			addressObject.setAddress2(pAddress.getAddress2());
			addressObject.setAddress3(pAddress.getAddress3());
			addressObject.setCompanyName(pAddress.getCompanyName());
			addressObject.setCity(pAddress.getCity());
			addressObject.setState(pAddress.getState());
			addressObject.setCountry(pAddress.getCountry());
			addressObject.setPostalCode(pAddress.getPostalCode());

			// Sample Test Data
			/*
			 * addressObject.setFirstName("bilFnamenal1027");
			 * addressObject.setLastName("bilLnamenal1027");
			 * addressObject.setAddress1("bilAdrl1027");
			 * addressObject.setAddress2("bilAdr21027");
			 * 
			 * addressObject.setCity("bilCity1027");
			 * addressObject.setState("bilState1027");
			 * addressObject.setCountry("bilCountry1027");
			 * addressObject.setPostalCode("12345");
			 */
			try {
				String nickName = getProfileTools().getUniqueShippingAddressNickname(addressObject, profile, null);
				id = getProfileTools().createProfileRepositorySecondaryAddress(profile, nickName, addressObject);
				if (isDefaultBillingAddress){
					getProfileTools().setDefaultBillingAddress(profile, nickName);
				}
			} catch (Exception e) {
				logError("addNewShippingAddress", e);
			}
			rtnAddressVO.setId(id);
			logDebug("BBBAddressAPIImpl | addNewBillingAddress method ends" + rtnAddressVO.getId());
			rtnAddressVO.setSource(BBBAddressAPIConstants.SOURCE);
			return rtnAddressVO;
		} else {
			logInfo("profile=" + profile + " pAddress=" + pAddress + " siteId=" + siteId);
			logDebug("BBBAddressAPIImpl | addNewBillingAddress method ends");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1278,"EXCEPTION WHILE ADDNEWBILLINGADDRESS");
		}
	}
	
	public BBBAddress addNewShippingAddress(Profile profile,
			BBBAddress address, String siteId, boolean isDefaultShippingAddress, boolean isDefaultBillingAddress) throws BBBSystemException,
			BBBBusinessException {
		if (profile != null && address != null
				&& !BBBUtility.isEmpty(address.getAddress1()) 
				&& !BBBUtility.isEmpty(address.getPostalCode()) 
				&& !BBBUtility.isEmpty(address.getCity()) 
				&& !BBBUtility.isEmpty(address.getState()) 
				&& siteId != null) {

			logDebug("BBBAddressAPIImpl | addNewShippingAddress method starts");

			BBBAddressVO rtnAddressVO = new BBBAddressVO();

			String id = null;
			BBBAddressObject addressObject = new BBBAddressObject();

			addressObject.setFirstName(address.getFirstName());
			addressObject.setLastName(address.getLastName());
			addressObject.setAddress1(address.getAddress1());
			addressObject.setAddress2(address.getAddress2());
			addressObject.setAddress3(address.getAddress3());
			addressObject.setCompanyName(address.getCompanyName());
			addressObject.setCity(address.getCity());
			addressObject.setState(address.getState());
			addressObject.setCountry(address.getCountry());
			addressObject.setPostalCode(address.getPostalCode());
			addressObject.setPoBoxAddress(address.isPoBoxAddress());
			addressObject.setQasValidated(address.isQasValidated());
			try {
				String nickName = getProfileTools().getUniqueShippingAddressNickname(addressObject, profile, null);
				id = getProfileTools().createProfileRepositorySecondaryAddress(profile, nickName, addressObject);
				
				getProfileTools().setDefaultShippingAddress(profile, nickName);
				if (isDefaultBillingAddress) {
					getProfileTools().setDefaultBillingAddress(profile, nickName);
				}
				if (isDefaultShippingAddress) {
					getProfileTools().setDefaultShippingAddress(profile, nickName);
				}

			} catch (Exception e) {
				logError("addNewShippingAddress", e);
			}
			rtnAddressVO.setId(id);

			logDebug("BBBAddressAPIImpl | addNewShippingAddress method ends" + rtnAddressVO.getId());
			rtnAddressVO.setSource(BBBAddressAPIConstants.SOURCE);
			rtnAddressVO.setIdentifier(rtnAddressVO.getSource() + rtnAddressVO.getId());

			return rtnAddressVO;
		} else {
			logInfo("profile=" + profile + " address=" + address + " siteId=" + siteId);
			logDebug("BBBAddressAPIImpl | addNewBillingAddress method ends");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1279,"EXCEPTION WHILE ADDNEWBILLINGADDRESS");
		}
	}

	public BBBAddress addNewShippingAddress(Profile profile,
			BBBAddressVO addressVO, String siteId, boolean isDefaultShippingAddress, boolean isDefaultBillingAddress) throws BBBSystemException,
 BBBBusinessException {
		if (profile != null && addressVO != null 	
				&& !BBBUtility.isEmpty(addressVO.getAddress1()) 
				&& !BBBUtility.isEmpty(addressVO.getPostalCode()) 
				&& !BBBUtility.isEmpty(addressVO.getCity()) 
				&& !BBBUtility.isEmpty(addressVO.getState()) 
				&& siteId != null) {
			logDebug("BBBAddressAPIImpl | addNewShippingAddress method starts");

			BBBAddressVO rtnAddressVO = new BBBAddressVO();

			String id = null;
			BBBAddressObject addressObject = new BBBAddressObject();

			//BBBSL-1976. Adding trimmed address values to merge shipping group if difference is due to trailing spaces.
			addressObject.setFirstName(trimValue(addressVO.getFirstName()));
			addressObject.setLastName(trimValue(addressVO.getLastName()));
			addressObject.setAddress1(trimValue(addressVO.getAddress1()));
			addressObject.setAddress2(trimValue(addressVO.getAddress2()));
			addressObject.setAddress3(trimValue(addressVO.getAddress3()));
			addressObject.setCompanyName(trimValue(addressVO.getCompanyName()));
			addressObject.setCity(trimValue(addressVO.getCity()));
			addressObject.setState(trimValue(addressVO.getState()));
			addressObject.setCountry(addressVO.getCountry());
			addressObject.setPostalCode(addressVO.getPostalCode());
			addressObject.setPoBoxAddress(addressVO.isPoBoxAddress());
			addressObject.setQasValidated(addressVO.isQasValidated());
			addressObject.setEmail(addressVO.getEmail());
			addressObject.setPhoneNumber(addressVO.getPhoneNumber());    
			try {
				String nickName = getProfileTools().getUniqueShippingAddressNickname(addressObject, profile, null);
				id = getProfileTools().createProfileRepositorySecondaryAddress(profile, nickName, addressObject);
				//updates for QAS
				//getProfileTools().updateQasDetails(profile, nickName,(BBBAddressObject) addressObject);
				if (isDefaultShippingAddress) {
					getProfileTools().setDefaultShippingAddress(profile, nickName);
				}

				if (isDefaultBillingAddress) {
					getProfileTools().setDefaultBillingAddress(profile, nickName);
				}

			} catch (Exception e) {
				logError("addNewShippingAddress", e);
			}
			rtnAddressVO.setId(id);

			logDebug("BBBAddressAPIImpl | addNewShippingAddress method ends" + rtnAddressVO.getId());
			rtnAddressVO.setSource(BBBAddressAPIConstants.SOURCE);
			rtnAddressVO.setIdentifier(rtnAddressVO.getSource() + rtnAddressVO.getId());

			return rtnAddressVO;
		} else {
			logInfo("profile=" + profile + " addressVO=" + addressVO + " siteId=" + siteId);
			logDebug("BBBAddressAPIImpl | addNewBillingAddress method ends");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1279,"EXCEPTION WHILE ADDNEWBILLINGADDRESS");
		}
	}
		
	
	public void updateAddressToProfile(Profile profile, BBBAddressVO addressVO, 
			String addressId, String siteId) throws BBBSystemException {	
		
		if (addressVO!=null && addressId!=null) {
			
			logDebug("BBBAddressAPIImpl | updateAddressToProfile method starts");
		
			final BBBAddressObject addressObject = new BBBAddressObject();
			
			if (addressVO.getFirstName()!=null && !BBBUtility.isEmpty(addressVO.getFirstName()))
				addressObject.setFirstName(trimValue(addressVO.getFirstName()));
			if (addressVO.getLastName()!=null && !BBBUtility.isEmpty(addressVO.getLastName()))
				addressObject.setLastName(trimValue(addressVO.getLastName()));
			if (addressVO.getAddress1()!=null && !BBBUtility.isEmpty(addressVO.getAddress1()))
				addressObject.setAddress1(trimValue(addressVO.getAddress1()));
			if (addressVO.getAddress2()!=null && !BBBUtility.isEmpty(addressVO.getAddress2()))
				addressObject.setAddress2(trimValue(addressVO.getAddress2()));
			if (addressVO.getAddress3()!=null && !BBBUtility.isEmpty(addressVO.getAddress3()))
				addressObject.setAddress3(trimValue(addressVO.getAddress3()));
			if (addressVO.getCompanyName()!=null && !BBBUtility.isEmpty(addressVO.getCompanyName()))
				addressObject.setCompanyName(trimValue(addressVO.getCompanyName()));
			if (addressVO.getCity()!=null && !BBBUtility.isEmpty(addressVO.getCity()))
				addressObject.setCity(trimValue(addressVO.getCity()));
			if (addressVO.getState()!=null && !BBBUtility.isEmpty(addressVO.getState()))
				addressObject.setState(trimValue(addressVO.getState()));
			if (addressVO.getCountry()!=null && !BBBUtility.isEmpty(addressVO.getCountry()))
				addressObject.setCountry(addressVO.getCountry());
			if (addressVO.getPostalCode()!=null && !BBBUtility.isEmpty(addressVO.getPostalCode()))
				addressObject.setPostalCode(addressVO.getPostalCode());
			addressObject.setPoBoxAddress(addressVO.isPoBoxAddress());
			addressObject.setQasValidated(addressVO.isQasValidated());
		
			// Get address repository item to be updated
			RepositoryItem oldAddress = getProfileTools().getProfileAddressById(addressId);
		
			try {
				// Update address repository item
				getProfileTools().updateProfileRepositoryAddress(oldAddress, addressObject);
			}
			catch (RepositoryException repositoryExc) {
				logError("profile=" + profile + " addressVO=" + addressVO + " siteId=" + siteId,repositoryExc);
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1145,repositoryExc.getMessage());
			}
			
			logDebug("BBBAddressAPIImpl | updateAddressToProfile method ends");
			
		}else {
			logInfo("profile=" + profile + " addressVO=" + addressVO + " siteId=" + siteId);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1279,"EXCEPTION WHILE UPDATEPROFILEADDRESS");
		}
		
	}
	
	
	//BBBSL-1976. Adding trimmed address values to merge shipping group if difference is due to trailing spaces.
	/**
	 * This method checks for the address value and if not null then trim the
	 * value and return the trimmed value.
	 * @param addressValue
	 * @return
	 */
	private String trimValue(String addressValue){
		return (addressValue!=null ? addressValue.trim() : addressValue);
	}
	
	public  BBBAddressVO fetchAddress(Profile profile, String siteId, String addressRepositoryId)
			throws BBBSystemException, BBBBusinessException {
		
			logDebug("BBBAddressAPIImpl | getShippingAddress method starts");
		
		if(addressRepositoryId ==null)
			return null;
		
		BBBAddressVO addressVO = null;
		
		Map addresses = (Map)profile.getPropertyValue("secondaryAddresses");
		
		if(addresses!=null){
			Iterator it = addresses.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry entry = (Map.Entry) it.next();
				
				RepositoryItem addressItem =(RepositoryItem)entry.getValue();
				
				if( addressRepositoryId.equalsIgnoreCase(addressItem.getRepositoryId())){
					
					addressVO = new BBBAddressVO();
				
					copyBBBAddress(addressItem, addressVO);
					
					addressVO.setSource(BBBAddressAPIConstants.SOURCE);
					addressVO.setId((String)addressItem.getPropertyValue("id"));
					addressVO.setIdentifier(addressVO.getSource()+addressVO.getId());
					logDebug("BBBAddressAPIImpl | fetchAddress method ends "+addressRepositoryId);
					break;
				}
			}
		}
		
		return addressVO;
	}
	
	
	public BBBAddress addNewPayPalShippingAddress(Profile profile,BBBAddressPPVO addressVO, String siteId, boolean isDefaultShippingAddress, boolean isDefaultBillingAddress) throws BBBSystemException,
 BBBBusinessException {
		if (profile != null && addressVO != null 	
				&& !BBBUtility.isEmpty(addressVO.getAddress1()) 
				&& !BBBUtility.isEmpty(addressVO.getPostalCode()) 
				&& !BBBUtility.isEmpty(addressVO.getCity()) 
				&& !BBBUtility.isEmpty(addressVO.getState()) 
				&& siteId != null) {
			logDebug("BBBAddressAPIImpl | addNewShippingAddress method starts");

			BBBAddressVO rtnAddressVO = new BBBAddressVO();

			String id = null;
			BBBAddressObject addressObject = new BBBAddressObject();

			addressObject.setFirstName(addressVO.getFirstName());
			addressObject.setLastName(addressVO.getLastName());
			addressObject.setAddress1(addressVO.getAddress1());
			addressObject.setAddress2(addressVO.getAddress2());
			addressObject.setAddress3(addressVO.getAddress3());
			addressObject.setCompanyName(addressVO.getCompanyName());
			addressObject.setCity(addressVO.getCity());
			addressObject.setState(addressVO.getState());
			addressObject.setCountry(addressVO.getCountry());
			addressObject.setPostalCode(addressVO.getPostalCode());
			

			try {
				String nickName = getProfileTools().getUniqueShippingAddressNickname(addressObject, profile, null);
				id = getProfileTools().createProfileRepositorySecondaryAddress(profile, nickName, addressObject);
				if (isDefaultShippingAddress) {
					getProfileTools().setDefaultShippingAddress(profile, nickName);
				}

				if (isDefaultBillingAddress) {
					getProfileTools().setDefaultBillingAddress(profile, nickName);
				}

			} catch (Exception e) {
				logError("addNewShippingAddress", e);
			}
			rtnAddressVO.setId(id);

			logDebug("BBBAddressAPIImpl | addNewShippingAddress method ends" + rtnAddressVO.getId());
			rtnAddressVO.setSource(BBBAddressAPIConstants.SOURCE);
			rtnAddressVO.setIdentifier(rtnAddressVO.getSource() + rtnAddressVO.getId());

			return rtnAddressVO;
		} else {
			logInfo("profile=" + profile + " addressVO=" + addressVO + " siteId=" + siteId);
			logDebug("BBBAddressAPIImpl | addNewBillingAddress method ends");
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1279,"EXCEPTION WHILE ADDNEWBILLINGADDRESS");
		}
	}
	
}
