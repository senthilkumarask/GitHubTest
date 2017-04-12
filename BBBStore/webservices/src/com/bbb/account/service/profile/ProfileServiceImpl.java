/**
 * ProfileServiceImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:22:40 CEST)
 */
package com.bbb.account.service.profile;

import java.util.Map;

import atg.nucleus.Nucleus;
import atg.servlet.ServletUtil;

import com.bbb.account.webservices.BBBProfileServices;
import com.bbb.account.webservices.vo.BillingAddressRequestVO;
import com.bbb.account.webservices.vo.ForgotPasswordRequestVO;
import com.bbb.account.webservices.vo.LinkCoRegistrantRequestVO;
import com.bbb.account.webservices.vo.LinkProfileRequestVO;
import com.bbb.account.webservices.vo.ProfileInfoRequestVO;
import com.bbb.account.webservices.vo.ProfileInfoResponseVO;
import com.bbb.account.webservices.vo.ProfileRequestVO;
import com.bbb.framework.webservices.BBBDozerBeanProvider;
import com.bbb.utils.BBBUtility;
import com.bbb.account.service.profile.BBBGetProfileInfoWebService;
import com.bbb.account.service.profile.ProfileBasicVO;
import com.bbb.account.service.profile.RequestVO;
import com.bedbathandbeyond.atg.Address;
import com.bedbathandbeyond.atg.BillingAddressResponseVODocument;
import com.bedbathandbeyond.atg.ErrorList;
import com.bedbathandbeyond.atg.ForgotPasswordResponseVODocument;
import com.bedbathandbeyond.atg.LinkCoRegistrantResponseVODocument;
import com.bedbathandbeyond.atg.LinkProfileResponseVODocument;
import com.bedbathandbeyond.atg.LoginResponseVODocument;
import com.bedbathandbeyond.atg.ProfileInfoResponseVODocument;
import com.bedbathandbeyond.atg.ProfileResponseVODocument;
import com.bedbathandbeyond.atg.ResponseVODocument;

/**
 * ProfileServiceImpl java skeleton for the axisService
 */
public class ProfileServiceImpl implements ProfileService {

	/**
	 * Auto generated method signature
	 * 
	 * @param profileInfoRequestVO0
	 * @return profileInfoResponseVO1
	 */
	final String DOZER_BEAN_PROVIDER = "/com/bbb/framework/webservices/BBBDozerBeanProvider";
	final String PROFILE_SERVICES = "/com/bbb/account/webservices/BBBProfileServices";
	final String HARTE_HANKS_SERVICES = "/com/bbb/webservices/BBBGetProfileInfoWebService";

	public com.bedbathandbeyond.atg.ProfileInfoResponseVODocument getProfileInfo(com.bedbathandbeyond.atg.ProfileInfoRequestVODocument profileInfoRequestVO0) {

		BBBProfileServices ws = (BBBProfileServices) ServletUtil.getCurrentRequest().resolveName(PROFILE_SERVICES);
		ProfileInfoRequestVO profileRequest = new ProfileInfoRequestVO();
		com.bedbathandbeyond.atg.ProfileInfoResponseVODocument profileInfoResponseVODocument = ProfileInfoResponseVODocument.Factory.newInstance();
		
		/* Create response */
		com.bedbathandbeyond.atg.ProfileInfoResponseVO profileInfoResponseVO = com.bedbathandbeyond.atg.ProfileInfoResponseVO.Factory.newInstance();
		ErrorList errorList = ErrorList.Factory.newInstance();

		try {
			/* Create request */
			profileRequest.setEmail(profileInfoRequestVO0.getProfileInfoRequestVO().getEmail());
			profileRequest.setPassword(profileInfoRequestVO0.getProfileInfoRequestVO().getPassword());
			profileRequest.setAutoExtendProfile(profileInfoRequestVO0.getProfileInfoRequestVO().getAutoExtendProfile());
			profileRequest.setProfileId(profileInfoRequestVO0.getProfileInfoRequestVO().getProfileId());
			profileRequest.setSiteId(profileInfoRequestVO0.getProfileInfoRequestVO().getSiteId());
			profileRequest.setAppId(profileInfoRequestVO0.getProfileInfoRequestVO().getAppId());
			
			/* Invoke service */
			ProfileInfoResponseVO profileResponse = ws.getProfileInfo(profileRequest);
			
			
			if (!profileResponse.isError()) {
				profileInfoResponseVO.setFirstName(profileResponse.getFirstName());
				profileInfoResponseVO.setProfileId(profileResponse.getProfileId());
				profileInfoResponseVO.setLastName(profileResponse.getLastName());
				profileInfoResponseVO.setEmail(profileResponse.getEmail());
				if (profileResponse.getPhone() != null) {
					profileInfoResponseVO.setPhone(profileResponse.getPhone());
				}
				if (profileResponse.getMobile() != null) {
					profileInfoResponseVO.setMobile(profileResponse.getMobile());
				}
				profileInfoResponseVO.setAutoExtend(profileResponse.isAutoExtend());
				if (profileResponse.getDefaultBillingAddress() != null && profileResponse.getDefaultBillingAddress().getAddress1() != null) {
					com.bedbathandbeyond.atg.Address address1 = Address.Factory.newInstance();
					address1.setAddressLine1(profileResponse.getDefaultBillingAddress().getAddress1());
					address1.setFirstNm(profileResponse.getDefaultBillingAddress().getFirstName());
					address1.setLastNm(profileResponse.getDefaultBillingAddress().getLastName());
					profileInfoResponseVO.setDefaultBillingAddress(address1);
					if (BBBUtility.isNotEmpty(profileResponse.getDefaultBillingAddress().getAddress1())) {
						profileInfoResponseVO.getDefaultBillingAddress().setAddressLine1(profileResponse.getDefaultBillingAddress().getAddress1());
						profileInfoResponseVO.getDefaultBillingAddress().setAddressLine2(profileResponse.getDefaultBillingAddress().getAddress2());
						profileInfoResponseVO.getDefaultBillingAddress().setCity(profileResponse.getDefaultBillingAddress().getCity());
						profileInfoResponseVO.getDefaultBillingAddress().setState(profileResponse.getDefaultBillingAddress().getState());
						profileInfoResponseVO.getDefaultBillingAddress().setZipCode(profileResponse.getDefaultBillingAddress().getPostalCode());
						profileInfoResponseVO.getDefaultBillingAddress().setCountry(profileResponse.getDefaultBillingAddress().getCountry());
					}
				} else {
					profileInfoResponseVO.setDefaultBillingAddress(null);
				}
				if (profileResponse.getDefaultShippingAddress() != null && profileResponse.getDefaultShippingAddress().getAddress1() != null) {

					com.bedbathandbeyond.atg.Address address1 = Address.Factory.newInstance();
					address1.setAddressLine1(profileResponse.getDefaultShippingAddress().getAddress1());
					address1.setFirstNm(profileResponse.getDefaultShippingAddress().getFirstName());
					address1.setLastNm(profileResponse.getDefaultShippingAddress().getLastName());
					profileInfoResponseVO.setDefaultShippingAddress(address1);
					if (BBBUtility.isNotEmpty(profileResponse.getDefaultShippingAddress().getAddress1())) {
						profileInfoResponseVO.getDefaultShippingAddress().setAddressLine1(profileResponse.getDefaultShippingAddress().getAddress1());
						profileInfoResponseVO.getDefaultShippingAddress().setAddressLine2(profileResponse.getDefaultShippingAddress().getAddress2());
						profileInfoResponseVO.getDefaultShippingAddress().setCity(profileResponse.getDefaultShippingAddress().getCity());
						profileInfoResponseVO.getDefaultShippingAddress().setState(profileResponse.getDefaultShippingAddress().getState());
						profileInfoResponseVO.getDefaultShippingAddress().setZipCode(profileResponse.getDefaultShippingAddress().getPostalCode());
						profileInfoResponseVO.getDefaultShippingAddress().setCountry(profileResponse.getDefaultShippingAddress().getCountry());
					}
				} else {
					profileInfoResponseVO.setDefaultShippingAddress(null);
				}
			} else {
				profileInfoResponseVO.setError(profileResponse.isError());
			}

			Map<String, String> errorMap = profileResponse.getErrorMap();
			if (errorMap != null) {

				for (String errorKey : errorMap.keySet()) {
					com.bedbathandbeyond.atg.Error error = errorList.addNewException(); // com.bedbathandbeyond.atg.Error.Factory.newInstance();
					error.setCode(errorKey);
					error.setDescription(errorMap.get(errorKey));
				}

				if (errorList.sizeOfExceptionArray() > 0) {
					profileInfoResponseVO.setExceptions(errorList);
				}
			}
			profileInfoResponseVODocument.setProfileInfoResponseVO(profileInfoResponseVO);
		} catch (Throwable e) { //NOPMD: We dont want to throw 500 error anytime
			com.bedbathandbeyond.atg.Error error = errorList.addNewException();
			error.setCode("err_ws_call");
			error.setDescription(e.getMessage());
			if (errorList.sizeOfExceptionArray() > 0) {
				profileInfoResponseVO.setExceptions(errorList);
			}
			profileInfoResponseVODocument.setProfileInfoResponseVO(profileInfoResponseVO);
			if (ws.isLoggingError()) {
				ws.logError("Webservice error while performing Profile opt", e);
			}
			if (ws.isLoggingError()) {
				ws.logError("Webservice error while performing Profile opt", e);
			}
		}
		return profileInfoResponseVODocument;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param profileRequestVO2
	 * @return profileResponseVO3
	 */

	public com.bedbathandbeyond.atg.ProfileResponseVODocument createUser(com.bedbathandbeyond.atg.ProfileRequestVODocument profileRequestVO2) {

		BBBDozerBeanProvider dozerBean = (BBBDozerBeanProvider) Nucleus.getGlobalNucleus().resolveName(DOZER_BEAN_PROVIDER);
		BBBProfileServices ws = (BBBProfileServices) ServletUtil.getCurrentRequest().resolveName(PROFILE_SERVICES);
		/* Create request */
		ProfileRequestVO profileRequest = new ProfileRequestVO();
		com.bedbathandbeyond.atg.ProfileResponseVODocument profileResponseVODocument = ProfileResponseVODocument.Factory.newInstance();
		
		/* Create response */
		com.bedbathandbeyond.atg.ProfileResponseVO profileInfoResponseVO = com.bedbathandbeyond.atg.ProfileResponseVO.Factory.newInstance();
		ErrorList errorList = ErrorList.Factory.newInstance();
		try {
			
			/* Un-Marshall request */
			profileRequest.setAppId(profileRequestVO2.getProfileRequestVO().getAppId());
			profileRequest.setConfirmPassword(profileRequestVO2.getProfileRequestVO().getConfirmPassword());
			profileRequest.setPassword(profileRequestVO2.getProfileRequestVO().getPassword());
			profileRequest.setEmail(profileRequestVO2.getProfileRequestVO().getEmail());
			profileRequest.setEmailOptIn(profileRequestVO2.getProfileRequestVO().getEmailOptIn());
			profileRequest.setFirstName(profileRequestVO2.getProfileRequestVO().getFirstName());
			profileRequest.setLastName(profileRequestVO2.getProfileRequestVO().getLastName());
			profileRequest.setMobileNumber(profileRequestVO2.getProfileRequestVO().getMobileNumber());
			profileRequest.setPhoneNumber(profileRequestVO2.getProfileRequestVO().getPhoneNumber());
			profileRequest.setSiteId(profileRequestVO2.getProfileRequestVO().getSiteId());
			profileRequest.setAutoExtendProfile(profileRequestVO2.getProfileRequestVO().getAutoExtendProfile());

			/* Invoke service */
			com.bbb.account.webservices.vo.ProfileResponseVO profileResponse = ws.createUser(profileRequest);
			/* Marshall response */
			profileInfoResponseVO.setProfileId(profileResponse.getProfileId());
			profileInfoResponseVO.setError(profileResponse.isError());
			Map<String, String> errorMap = profileResponse.getErrorMap();
			if (errorMap != null) {
				for (String errorKey : errorMap.keySet()) {
					com.bedbathandbeyond.atg.Error error = errorList.addNewException(); // com.bedbathandbeyond.atg.Error.Factory.newInstance();
					error.setCode(errorKey);
					error.setDescription(errorMap.get(errorKey));
				}

				if (errorList.sizeOfExceptionArray() > 0) {
					profileInfoResponseVO.setExceptions(errorList);
				}
			}
			profileResponseVODocument.setProfileResponseVO(profileInfoResponseVO);
		} catch (Throwable e) {//NOPMD: We dont want to throw 500 error anytime
			com.bedbathandbeyond.atg.Error error = errorList.addNewException();
			error.setCode("err_ws_call");
			error.setDescription(e.getMessage());
			if (errorList.sizeOfExceptionArray() > 0) {
				profileInfoResponseVO.setExceptions(errorList);
			}
			profileResponseVODocument.setProfileResponseVO(profileInfoResponseVO);
			if (ws.isLoggingError()) {
				ws.logError("Webservice error while performing Profile opt", e);
			}
		}
		return profileResponseVODocument;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param linkCoRegistrantRequestVO4
	 * @return linkCoRegistrantResponseVO5
	 */

	public com.bedbathandbeyond.atg.LinkCoRegistrantResponseVODocument linkCoRegistrant(com.bedbathandbeyond.atg.LinkCoRegistrantRequestVODocument linkCoRegistrantRequestVO4) {
		BBBDozerBeanProvider dozerBean = (BBBDozerBeanProvider) Nucleus.getGlobalNucleus().resolveName(DOZER_BEAN_PROVIDER);
		BBBProfileServices ws = (BBBProfileServices) ServletUtil.getCurrentRequest().resolveName(PROFILE_SERVICES);
		LinkCoRegistrantRequestVO profileRequest = new LinkCoRegistrantRequestVO();
		com.bedbathandbeyond.atg.LinkCoRegistrantResponseVODocument linkCoRegistrantResponseVODocument = LinkCoRegistrantResponseVODocument.Factory.newInstance();
		/* Create response */
		com.bedbathandbeyond.atg.LinkCoRegistrantsResponseVO linkCoRegistrantsResponseVO = com.bedbathandbeyond.atg.LinkCoRegistrantsResponseVO.Factory.newInstance();
		ErrorList errorList = ErrorList.Factory.newInstance();
		try {

			// dozerBean.getDozerMapper().map(linkCoRegistrantRequestVO4.getLinkCoRegistrantRequestVO(),
			// profileRequest);
			profileRequest.setAppId(linkCoRegistrantRequestVO4.getLinkCoRegistrantRequestVO().getAppId());
			profileRequest.setCoRegEmail(linkCoRegistrantRequestVO4.getLinkCoRegistrantRequestVO().getCoRegEmail());
			profileRequest.setEventDate(linkCoRegistrantRequestVO4.getLinkCoRegistrantRequestVO().getEventDate());
			profileRequest.setEventType(linkCoRegistrantRequestVO4.getLinkCoRegistrantRequestVO().getEventType());
			profileRequest.setRegId(linkCoRegistrantRequestVO4.getLinkCoRegistrantRequestVO().getRegId());
			profileRequest.setSiteId(linkCoRegistrantRequestVO4.getLinkCoRegistrantRequestVO().getSiteId());

			/* Invoke service */
			com.bbb.account.webservices.vo.LinkCoRegistrantResponseVO linkCoRegistrantResponseVO = ws.linkCoRegistrant(profileRequest);
			// dozerBean.getDozerMapper().map(linkCoRegistrantResponseVO,
			// linkCoRegistrantsResponseVO);

			linkCoRegistrantsResponseVO.setCoRegProfileId(linkCoRegistrantResponseVO.getCoRegProfileId());
			Map<String, String> errorMap = linkCoRegistrantResponseVO.getErrorMap();
			if (errorMap != null) {
				for (String errorKey : errorMap.keySet()) {
					com.bedbathandbeyond.atg.Error error = errorList.addNewException(); // com.bedbathandbeyond.atg.Error.Factory.newInstance();
					error.setCode(errorKey);
					error.setDescription(errorMap.get(errorKey));
				}

				if (errorList.sizeOfExceptionArray() > 0) {
					linkCoRegistrantsResponseVO.setExceptions(errorList);
				}
			}
			linkCoRegistrantResponseVODocument.setLinkCoRegistrantResponseVO(linkCoRegistrantsResponseVO);

		} catch (Throwable e) {//NOPMD: We dont want to throw 500 error anytime
			com.bedbathandbeyond.atg.Error error = errorList.addNewException();
			error.setCode("err_ws_call");
			error.setDescription(e.getMessage());
			if (errorList.sizeOfExceptionArray() > 0) {
				linkCoRegistrantsResponseVO.setExceptions(errorList);
			}
			linkCoRegistrantResponseVODocument.setLinkCoRegistrantResponseVO(linkCoRegistrantsResponseVO);
			if (ws.isLoggingError()) {
				ws.logError("Webservice error while performing Profile opt", e);
			}
		}
		return linkCoRegistrantResponseVODocument;

	}

	/**
	 * Auto generated method signature
	 * 
	 * @param loginRequestVO6
	 * @return loginResponseVO7
	 */

	public com.bedbathandbeyond.atg.LoginResponseVODocument login(com.bedbathandbeyond.atg.LoginRequestVODocument loginRequestVO6) {

		BBBDozerBeanProvider dozerBean = (BBBDozerBeanProvider) Nucleus.getGlobalNucleus().resolveName(DOZER_BEAN_PROVIDER);
		BBBProfileServices ws = (BBBProfileServices) ServletUtil.getCurrentRequest().resolveName(PROFILE_SERVICES);
		/* Create request */
		com.bbb.account.webservices.vo.LoginRequestVO profileRequest = new com.bbb.account.webservices.vo.LoginRequestVO();
		LoginResponseVODocument loginResponseVODocument = LoginResponseVODocument.Factory.newInstance();
		
		/* Create response */
		com.bedbathandbeyond.atg.LoginResponseVO loginResponse = com.bedbathandbeyond.atg.LoginResponseVO.Factory.newInstance();
		
		ErrorList errorList = ErrorList.Factory.newInstance();
		try {
			// Un-Marshall the request
			profileRequest.setAutoExtendProfile(loginRequestVO6.getLoginRequestVO().getAutoExtendProfile());
			profileRequest.setLoginId(loginRequestVO6.getLoginRequestVO().getLoginId());
			profileRequest.setLoginProfileId(loginRequestVO6.getLoginRequestVO().getLoginProfileId());
			profileRequest.setPassword(loginRequestVO6.getLoginRequestVO().getPassword());
			profileRequest.setSiteId(loginRequestVO6.getLoginRequestVO().getSiteId());
			profileRequest.setAppId(loginRequestVO6.getLoginRequestVO().getAppId());
			/*
			 * dozerBean.getDozerMapper().map(loginRequestVO6.getLoginRequestVO()
			 * , profileRequest);
			 */
			/* dozerBean.getDozerMapper().map(loginResponseVO, loginResponse); */
			/* Invoke service */
			com.bbb.account.webservices.vo.LoginResponseVO loginResponseVO = ws.login(profileRequest);
		
			// Marshall the response
			loginResponse.setError(loginResponseVO.isError());
			loginResponse.setProfileAutoExtended(loginResponseVO.isProfileAutoExtended());
			loginResponse.setProfileId(loginResponseVO.getProfileId());
			Map<String, String> errorMap = loginResponseVO.getErrorMap();
			if (errorMap != null) {
				for (String errorKey : errorMap.keySet()) {
					com.bedbathandbeyond.atg.Error error = errorList.addNewException();
					error.setCode(errorKey);
					error.setDescription(errorMap.get(errorKey));
				}

				if (errorList.sizeOfExceptionArray() > 0) {
					loginResponse.setExceptions(errorList);
				}
			}
			loginResponseVODocument.setLoginResponseVO(loginResponse);
		} catch (Throwable e) {//NOPMD: We dont want to throw 500 error anytime

			com.bedbathandbeyond.atg.Error error = errorList.addNewException();
			error.setCode("err_ws_call");
			error.setDescription(e.getMessage());
			if (errorList.sizeOfExceptionArray() > 0) {
				loginResponse.setExceptions(errorList);
			}
			loginResponseVODocument.setLoginResponseVO(loginResponse);
			if (ws.isLoggingError()) {
				ws.logError("Webservice error while performing Profile opt", e);
			}
		}
		return loginResponseVODocument;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param billingAddressRequestVO8
	 * @return billingAddressResponseVO9
	 */

	public com.bedbathandbeyond.atg.BillingAddressResponseVODocument getBillingAddress(com.bedbathandbeyond.atg.BillingAddressRequestVODocument billingAddressRequestVO8) {
		BBBDozerBeanProvider dozerBean = (BBBDozerBeanProvider) Nucleus.getGlobalNucleus().resolveName(DOZER_BEAN_PROVIDER);
		BBBProfileServices ws = (BBBProfileServices) ServletUtil.getCurrentRequest().resolveName(PROFILE_SERVICES);
		BillingAddressRequestVO profileRequest = new BillingAddressRequestVO();
		com.bedbathandbeyond.atg.BillingAddressResponseVODocument billingAddressResponseVODocument = BillingAddressResponseVODocument.Factory.newInstance();
		
		/* Create response */
		com.bedbathandbeyond.atg.BillingAddressResponseVO billingAddressWSResponse = com.bedbathandbeyond.atg.BillingAddressResponseVO.Factory.newInstance();
		ErrorList errorList = ErrorList.Factory.newInstance();
		try {
			/* Create request */
			// dozerBean.getDozerMapper().map(billingAddressRequestVO8.getBillingAddressRequestVO(),
			// profileRequest);
			// Request Mapping
			profileRequest.setEmail(billingAddressRequestVO8.getBillingAddressRequestVO().getEmail());
			profileRequest.setProfileId(billingAddressRequestVO8.getBillingAddressRequestVO().getProfileId());
			profileRequest.setSecurityToken(billingAddressRequestVO8.getBillingAddressRequestVO().getSecurityToken());
			profileRequest.setSiteId(billingAddressRequestVO8.getBillingAddressRequestVO().getSiteId());
			profileRequest.setAppId(billingAddressRequestVO8.getBillingAddressRequestVO().getAppId());
			/* Invoke service */
			com.bbb.account.webservices.vo.BillingAddressResponseVO billingAddressResposeVO = ws.getBillingAddress(profileRequest);
			// dozerBean.getDozerMapper().map(billingAddressResposeVO,
			// billingAddressWSResponse);

			// Response Mapping

			if (billingAddressResposeVO.getDefaultBillingAddress() != null && BBBUtility.isNotEmpty(billingAddressResposeVO.getDefaultBillingAddress().getAddress1())) {
				com.bedbathandbeyond.atg.Address address1 = Address.Factory.newInstance();
				address1.setFirstNm(billingAddressResposeVO.getDefaultBillingAddress().getFirstName());
				address1.setLastNm(billingAddressResposeVO.getDefaultBillingAddress().getLastName());
				address1.setAddressLine1(billingAddressResposeVO.getDefaultBillingAddress().getAddress1());
				address1.setAddressLine2(billingAddressResposeVO.getDefaultBillingAddress().getAddress2());
				address1.setCity(billingAddressResposeVO.getDefaultBillingAddress().getCity());
				address1.setState(billingAddressResposeVO.getDefaultBillingAddress().getState());
				address1.setZipCode(billingAddressResposeVO.getDefaultBillingAddress().getPostalCode());
				address1.setCountry(billingAddressResposeVO.getDefaultBillingAddress().getCountry());
				billingAddressWSResponse.setDefaultBillingAddress(address1);
				
			} else {
				billingAddressWSResponse.setDefaultBillingAddress(null);
			}

			Map<String, String> errorMap = billingAddressResposeVO.getErrorMap();
			if (errorMap != null) {
				for (String errorKey : errorMap.keySet()) {
					com.bedbathandbeyond.atg.Error error = errorList.addNewException(); // com.bedbathandbeyond.atg.Error.Factory.newInstance();
					error.setCode(errorKey);
					error.setDescription(errorMap.get(errorKey));
				}

				if (errorList.sizeOfExceptionArray() > 0) {
					billingAddressWSResponse.setExceptions(errorList);
				}
			} else {
				billingAddressWSResponse.setEmail(billingAddressResposeVO.getEmail());
				billingAddressWSResponse.setProfileId(billingAddressResposeVO.getProfileId());
			}

			billingAddressResponseVODocument.setBillingAddressResponseVO(billingAddressWSResponse);

		} catch (Throwable e) {//NOPMD: We dont want to throw 500 error anytime

			com.bedbathandbeyond.atg.Error error = errorList.addNewException();
			error.setCode("err_ws_call");
			error.setDescription(e.getMessage());
			if (errorList.sizeOfExceptionArray() > 0) {
				billingAddressWSResponse.setExceptions(errorList);
			}
			billingAddressResponseVODocument.setBillingAddressResponseVO(billingAddressWSResponse);
			if (ws.isLoggingError()) {
				ws.logError("Webservice error while performing Profile opt", e);
			}
		}
		return billingAddressResponseVODocument;

	}

	/**
	 * Auto generated method signature
	 * 
	 * @param requestVO8
	 * @return responseVO9
	 */

	public com.bedbathandbeyond.atg.ResponseVODocument getResponseVO(com.bedbathandbeyond.atg.RequestVODocument requestVO8) {
		// TODO : fill this with the necessary business logic
		BBBDozerBeanProvider dozerBean = (BBBDozerBeanProvider) Nucleus.getGlobalNucleus().resolveName(DOZER_BEAN_PROVIDER);
		BBBGetProfileInfoWebService ws = (BBBGetProfileInfoWebService) ServletUtil.getCurrentRequest().resolveName(HARTE_HANKS_SERVICES);
		RequestVO profileRequest = new RequestVO();
		com.bedbathandbeyond.atg.ResponseVODocument responseVODocument = ResponseVODocument.Factory.newInstance();
		com.bedbathandbeyond.atg.ResponseVO wSResponseVO = com.bedbathandbeyond.atg.ResponseVO.Factory.newInstance();
		try {
			/* Create request */
			// dozerBean.getDozerMapper().map(requestVO8.getRequestVO(),
			// profileRequest);

			profileRequest.setRequestType(requestVO8.getRequestVO().getRequestType());
			profileRequest.setSource(requestVO8.getRequestVO().getSource());
			profileRequest.setIpAddress(requestVO8.getRequestVO().getIpAddress());
			profileRequest.setSiteId(requestVO8.getRequestVO().getSiteId());
			profileRequest.setSource(requestVO8.getRequestVO().getSource());
			profileRequest.setToken(requestVO8.getRequestVO().getToken());
			/* Invoke service */
			com.bbb.account.service.profile.ResponseVO resposeVO = ws.getResponseVO(profileRequest);
			/* Create response */
			
			// dozerBean.getDozerMapper().map(resposeVO, wSResponseVO);
			wSResponseVO.setAuthorzied(resposeVO.isAuthorzied());
			wSResponseVO.setMessage(resposeVO.getMessage());
			wSResponseVO.setResponseType(resposeVO.getResponseType());

			ProfileBasicVO profileBasicVO = resposeVO.getProfileBasicVO();
			com.bedbathandbeyond.atg.ProfileBasicVO wsProfileBasicVO = com.bedbathandbeyond.atg.ProfileBasicVO.Factory.newInstance();

			if (profileBasicVO != null) {
				if (profileBasicVO.getAddressLine1() != null)
					wsProfileBasicVO.setAddressLine1(profileBasicVO.getAddressLine1());
				if (profileBasicVO.getAddressLine2() != null)
					wsProfileBasicVO.setAddressLine2(profileBasicVO.getAddressLine2());
				if (profileBasicVO.getFirstName() != null)
					wsProfileBasicVO.setFirstName(profileBasicVO.getFirstName());
				if (profileBasicVO.getLastName() != null)
					wsProfileBasicVO.setLastName(profileBasicVO.getLastName());
				if (profileBasicVO.getCity() != null)
					wsProfileBasicVO.setCity(profileBasicVO.getCity());
				if (profileBasicVO.getState() != null)
					wsProfileBasicVO.setState(profileBasicVO.getState());
				if (profileBasicVO.getMobile() != null)
					wsProfileBasicVO.setMobile(profileBasicVO.getMobile());
				if (profileBasicVO.getPhoneNum() != null)
					wsProfileBasicVO.setPhoneNum(profileBasicVO.getPhoneNum());
				if (profileBasicVO.getEmail() != null)
					wsProfileBasicVO.setEmail(profileBasicVO.getEmail());
				if (profileBasicVO.getZipcode() != null)
					wsProfileBasicVO.setZipcode(profileBasicVO.getZipcode());
				if (profileBasicVO.getProfileId() != null)
					wsProfileBasicVO.setProfileId(profileBasicVO.getProfileId());
				if (profileBasicVO.getCountry() != null)
					wsProfileBasicVO.setCountry(profileBasicVO.getCountry());
				
				wsProfileBasicVO.setEmailOptIn(profileBasicVO.isEmailOptIn());
				
			}
			wSResponseVO.setProfileBasicVO(wsProfileBasicVO);
			responseVODocument.setResponseVO(wSResponseVO);

		} catch (Throwable e) {//NOPMD: We dont want to throw 500 error anytime
			wSResponseVO.setMessage(e.getMessage());
			responseVODocument.setResponseVO(wSResponseVO);
			if (ws.isLoggingError()) {
				ws.logError("Webservice error while performing Profile opt", e);
			}
		}
		return responseVODocument;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param linkProfileRequestVO10
	 * @return linkProfileResponseVO11
	 */

	public com.bedbathandbeyond.atg.LinkProfileResponseVODocument linkProfileAndRegistry(com.bedbathandbeyond.atg.LinkProfileRequestVODocument linkProfileRequestVO10) {
		BBBDozerBeanProvider dozerBean = (BBBDozerBeanProvider) Nucleus.getGlobalNucleus().resolveName(DOZER_BEAN_PROVIDER);
		BBBProfileServices ws = (BBBProfileServices) ServletUtil.getCurrentRequest().resolveName(PROFILE_SERVICES);
		LinkProfileRequestVO profileRequest = new LinkProfileRequestVO();
		com.bedbathandbeyond.atg.LinkProfileResponseVODocument linkProfileResponseVODocument = LinkProfileResponseVODocument.Factory.newInstance();
		/* Create response */
		com.bedbathandbeyond.atg.LinkProfileAndRegistryResponseVO linkProfileAndRegistryResponseVO = com.bedbathandbeyond.atg.LinkProfileAndRegistryResponseVO.Factory
				.newInstance();
		ErrorList errorList = ErrorList.Factory.newInstance();
		try {

			// dozerBean.getDozerMapper().map(linkProfileRequestVO10.getLinkProfileRequestVO(),
			// profileRequest);
			profileRequest.setAppId(linkProfileRequestVO10.getLinkProfileRequestVO().getAppId());
			profileRequest.setCoRegEmail(linkProfileRequestVO10.getLinkProfileRequestVO().getCoRegEmail());
			profileRequest.setEventDate(linkProfileRequestVO10.getLinkProfileRequestVO().getEventDate());
			profileRequest.setEventType(linkProfileRequestVO10.getLinkProfileRequestVO().getEventType());
			profileRequest.setProfileId(linkProfileRequestVO10.getLinkProfileRequestVO().getProfileId());
			profileRequest.setRegId(linkProfileRequestVO10.getLinkProfileRequestVO().getRegId());
			profileRequest.setSiteId(linkProfileRequestVO10.getLinkProfileRequestVO().getSiteId());

			/* Invoke service */
			com.bbb.account.webservices.vo.LinkProfileResponseVO linkProfileResponseVO = ws.linkProfileAndRegistry(profileRequest);
			// dozerBean.getDozerMapper().map(linkProfileResponseVO,
			// linkProfileAndRegistryResponseVO);
			linkProfileAndRegistryResponseVO.setCoRegProfileId(linkProfileResponseVO.getCoRegProfileId());
			linkProfileAndRegistryResponseVO.setProfileId(linkProfileResponseVO.getProfileId());
			Map<String, String> errorMap = linkProfileResponseVO.getErrorMap();
			if (errorMap != null) {
				for (String errorKey : errorMap.keySet()) {
					com.bedbathandbeyond.atg.Error error = errorList.addNewException(); // com.bedbathandbeyond.atg.Error.Factory.newInstance();
					error.setCode(errorKey);
					error.setDescription(errorMap.get(errorKey));
				}

				if (errorList.sizeOfExceptionArray() > 0) {
					linkProfileAndRegistryResponseVO.setExceptions(errorList);
				}
			}
			linkProfileResponseVODocument.setLinkProfileResponseVO(linkProfileAndRegistryResponseVO);

		} catch (Throwable e) {//NOPMD: We dont want to throw 500 error anytime
			com.bedbathandbeyond.atg.Error error = errorList.addNewException();
			error.setCode("err_ws_call");
			error.setDescription(e.getMessage());
			if (errorList.sizeOfExceptionArray() > 0) {
				linkProfileAndRegistryResponseVO.setExceptions(errorList);
			}
			linkProfileResponseVODocument.setLinkProfileResponseVO(linkProfileAndRegistryResponseVO);
			if (ws.isLoggingError()) {
				ws.logError("Webservice error while performing Profile opt", e);
			}
		}
		return linkProfileResponseVODocument;
	}

	/**
	 * Auto generated method signature
	 * 
	 * @param forgotPasswordRequestVO12
	 * @return forgotPasswordResponseVO13
	 */

	public com.bedbathandbeyond.atg.ForgotPasswordResponseVODocument forgotPassword(com.bedbathandbeyond.atg.ForgotPasswordRequestVODocument forgotPasswordRequestVO12) {

		BBBDozerBeanProvider dozerBean = (BBBDozerBeanProvider) Nucleus.getGlobalNucleus().resolveName(DOZER_BEAN_PROVIDER);
		BBBProfileServices ws = (BBBProfileServices) ServletUtil.getCurrentRequest().resolveName(PROFILE_SERVICES);
		ForgotPasswordRequestVO profileRequest = new ForgotPasswordRequestVO();
		com.bedbathandbeyond.atg.ForgotPasswordResponseVODocument forgotPasswordResponseVODocument = ForgotPasswordResponseVODocument.Factory.newInstance();
		/* Create response */
		com.bedbathandbeyond.atg.ForgotPasswordResponseVO forgotPasswordResponse = com.bedbathandbeyond.atg.ForgotPasswordResponseVO.Factory.newInstance();
		ErrorList errorList = ErrorList.Factory.newInstance();
		try {
			/* Create request */

			// dozerBean.getDozerMapper().map(forgotPasswordRequestVO12.getForgotPasswordRequestVO(),
			// profileRequest);
			profileRequest.setEmail(forgotPasswordRequestVO12.getForgotPasswordRequestVO().getEmail());
			profileRequest.setSiteId(forgotPasswordRequestVO12.getForgotPasswordRequestVO().getSiteId());
			profileRequest.setAppId(forgotPasswordRequestVO12.getForgotPasswordRequestVO().getAppId());
			// dozerBean.getDozerMapper().map(forgotPasswordResponseVO,
			// forgotPasswordResponse);
			/* Invoke service */
			com.bbb.account.webservices.vo.ForgotPasswordResponseVO forgotPasswordResponseVO = ws.forgotPassword(profileRequest);
			
			forgotPasswordResponse.setProfileId(forgotPasswordResponseVO.getProfileId());
			forgotPasswordResponse.setResetSuccess(forgotPasswordResponseVO.isResetSuccess());
			forgotPasswordResponse.setError(forgotPasswordResponseVO.isError());
			Map<String, String> errorMap = forgotPasswordResponseVO.getErrorMap();
			if (errorMap != null) {
				for (String errorKey : errorMap.keySet()) {
					com.bedbathandbeyond.atg.Error error = errorList.addNewException(); // com.bedbathandbeyond.atg.Error.Factory.newInstance();
					error.setCode(errorKey);
					error.setDescription(errorMap.get(errorKey));
				}

				if (errorList.sizeOfExceptionArray() > 0) {
					forgotPasswordResponse.setExceptions(errorList);
				}
			}
			forgotPasswordResponseVODocument.setForgotPasswordResponseVO(forgotPasswordResponse);

		} catch (Throwable e) {//NOPMD: We dont want to throw 500 error anytime
			com.bedbathandbeyond.atg.Error error = errorList.addNewException();
			error.setCode("err_ws_call");
			error.setDescription(e.getMessage());
			if (errorList.sizeOfExceptionArray() > 0) {
				forgotPasswordResponse.setExceptions(errorList);
			}
			forgotPasswordResponseVODocument.setForgotPasswordResponseVO(forgotPasswordResponse);
			if (ws.isLoggingError()) {
				ws.logError("Webservice error while performing Profile opt", e);
			}
		}
		return forgotPasswordResponseVODocument;
	}

}
