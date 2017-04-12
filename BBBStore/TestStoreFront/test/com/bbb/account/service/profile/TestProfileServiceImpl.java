package com.bbb.account.service.profile;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.sapient.common.tests.BaseTestCase;

public class TestProfileServiceImpl extends BaseTestCase {

	public void testCreateUser() {

		String email = (String) getObject("email");
		String firstName = (String) getObject("firstName");
		String lastName = (String) getObject("lastName");
		String password = (String) getObject("password");
		String confirmPassword = (String) getObject("confirmPassword");
		String siteId = (String) getObject("siteId");
		String phone = (String) getObject("phone");
		String mobile = (String) getObject("mobile");
		boolean optIn = (Boolean) getObject("optIn");

		ProfileServiceImpl profileServiceImpl = new ProfileServiceImpl();
		com.bedbathandbeyond.atg.ProfileRequestVODocument profileRequestVODocument = com.bedbathandbeyond.atg.ProfileRequestVODocument.Factory
				.newInstance();
		com.bedbathandbeyond.atg.ProfileRequestVO profileRequestVO = com.bedbathandbeyond.atg.ProfileRequestVO.Factory
				.newInstance();
		profileRequestVO.setAppId("1");
		profileRequestVO.setConfirmPassword(confirmPassword);
		profileRequestVO.setPassword(password);
		profileRequestVO.setEmail(email);
		profileRequestVO.setEmailOptIn(optIn);
		profileRequestVO.setFirstName(firstName);
		profileRequestVO.setLastName(lastName);
		profileRequestVO.setMobileNumber(mobile);
		profileRequestVO.setPhoneNumber(phone);
		profileRequestVO.setSiteId(siteId);

		profileRequestVODocument.setProfileRequestVO(profileRequestVO);
		DynamoHttpServletRequest pRequest = getRequest();
		ServletUtil.setCurrentRequest(pRequest);
		com.bedbathandbeyond.atg.ProfileResponseVODocument profileResponseVODocument = profileServiceImpl
				.createUser(profileRequestVODocument);
		assertNotNull(profileResponseVODocument);
	}

	public void testForgotPassword() {

		String email = (String) getObject("email");
		String siteId = (String) getObject("siteId");

		ProfileServiceImpl profileServiceImpl = new ProfileServiceImpl();
		com.bedbathandbeyond.atg.ForgotPasswordRequestVODocument forgotPasswordRequestVODocument = com.bedbathandbeyond.atg.ForgotPasswordRequestVODocument.Factory
				.newInstance();
		com.bedbathandbeyond.atg.ForgotPasswordRequestVO forgotPasswordRequestVO = com.bedbathandbeyond.atg.ForgotPasswordRequestVO.Factory
				.newInstance();

		forgotPasswordRequestVO.setEmail(email);
		forgotPasswordRequestVO.setSiteId(siteId);
		forgotPasswordRequestVO.setAppId("1");

		forgotPasswordRequestVODocument
				.setForgotPasswordRequestVO(forgotPasswordRequestVO);
		DynamoHttpServletRequest pRequest = getRequest();
		ServletUtil.setCurrentRequest(pRequest);

		com.bedbathandbeyond.atg.ForgotPasswordResponseVODocument forgotPasswordResponseVODocument = profileServiceImpl
				.forgotPassword(forgotPasswordRequestVODocument);
		assertNotNull(forgotPasswordResponseVODocument);

	}

	public void testGetBillingAddress() {

		String email = (String) getObject("email");
		String securityToken = (String) getObject("securityToken");
		String siteId = (String) getObject("siteId");
		String profileId = (String) getObject("profileId");

		ProfileServiceImpl profileServiceImpl = new ProfileServiceImpl();
		com.bedbathandbeyond.atg.BillingAddressRequestVODocument billingAddressRequestVODocument = com.bedbathandbeyond.atg.BillingAddressRequestVODocument.Factory
				.newInstance();
		com.bedbathandbeyond.atg.BillingAddressRequestVO biAddressRequestVO = com.bedbathandbeyond.atg.BillingAddressRequestVO.Factory
				.newInstance();

		biAddressRequestVO.setEmail(email);
		biAddressRequestVO.setProfileId(profileId);
		biAddressRequestVO.setSecurityToken(securityToken);
		biAddressRequestVO.setSiteId(siteId);
		biAddressRequestVO.setAppId("1");

		billingAddressRequestVODocument
				.setBillingAddressRequestVO(biAddressRequestVO);

		DynamoHttpServletRequest pRequest = getRequest();
		ServletUtil.setCurrentRequest(pRequest);

		com.bedbathandbeyond.atg.BillingAddressResponseVODocument billingAddressResponseVODocument = profileServiceImpl
				.getBillingAddress(billingAddressRequestVODocument);
		assertNotNull(billingAddressResponseVODocument);

	}

	public void testGetProfileInfo() {

		String email = (String) getObject("email");
		String password = (String) getObject("password");
		String siteId = (String) getObject("siteId");
		boolean autoExtendProfile = (Boolean) getObject("autoExtendProfile");
		String profileId = (String) getObject("profileId");

		ProfileServiceImpl profileServiceImpl = new ProfileServiceImpl();
		com.bedbathandbeyond.atg.ProfileInfoRequestVODocument profileInfoRequestVODocument = com.bedbathandbeyond.atg.ProfileInfoRequestVODocument.Factory
				.newInstance();
		com.bedbathandbeyond.atg.ProfileInfoRequestVO profileInfoRequestVO = com.bedbathandbeyond.atg.ProfileInfoRequestVO.Factory
				.newInstance();

		profileInfoRequestVO.setEmail(email);
		profileInfoRequestVO.setPassword(password);
		profileInfoRequestVO.setAutoExtendProfile(autoExtendProfile);
		profileInfoRequestVO.setProfileId(profileId);
		profileInfoRequestVO.setSiteId(siteId);
		profileInfoRequestVO.setAppId("1");

		profileInfoRequestVODocument
				.setProfileInfoRequestVO(profileInfoRequestVO);

		DynamoHttpServletRequest pRequest = getRequest();
		ServletUtil.setCurrentRequest(pRequest);

		com.bedbathandbeyond.atg.ProfileInfoResponseVODocument profileInfoResponseVODocument = profileServiceImpl
				.getProfileInfo(profileInfoRequestVODocument);
		assertNotNull(profileInfoResponseVODocument);

	}

	public void testGetResponseVO() {

		String requestType = (String) getObject("requestType");
		String source = (String) getObject("source");
		String ipAddress = (String) getObject("ipAddress");
		String siteId = (String) getObject("siteId");
		String token = (String) (String) getObject("securityToken");

		ProfileServiceImpl profileServiceImpl = new ProfileServiceImpl();
		com.bedbathandbeyond.atg.RequestVODocument requestVODocument = com.bedbathandbeyond.atg.RequestVODocument.Factory
				.newInstance();
		com.bedbathandbeyond.atg.RequestVO requestVO = com.bedbathandbeyond.atg.RequestVO.Factory
				.newInstance();
		requestVO.setRequestType(1);
		requestVO.setSource(source);
		requestVO.setIpAddress(ipAddress);
		requestVO.setSiteId(siteId);
		requestVO.setToken(token);

		DynamoHttpServletRequest pRequest = getRequest();
		ServletUtil.setCurrentRequest(pRequest);

		com.bedbathandbeyond.atg.ResponseVODocument responseVODocument = profileServiceImpl
				.getResponseVO(requestVODocument);
		assertNotNull(responseVODocument);

	}

	public void testCoRegistrant() {

		String coRegEmail = "test3@example.com";
		String eventDate = "12345";
		String eventType = "Baby";
		String regId = "153588307";
		String siteId = "BuyBuyBaby";

		ProfileServiceImpl profileServiceImpl = new ProfileServiceImpl();
		com.bedbathandbeyond.atg.LinkCoRegistrantRequestVODocument linkCoRegistrantRequestVODocument = com.bedbathandbeyond.atg.LinkCoRegistrantRequestVODocument.Factory
				.newInstance();

		com.bedbathandbeyond.atg.LinkCoRegistrantsRequestVO linkCoRegistrantsRequestVO = com.bedbathandbeyond.atg.LinkCoRegistrantsRequestVO.Factory
				.newInstance();

		linkCoRegistrantsRequestVO.setAppId("1");
		linkCoRegistrantsRequestVO.setCoRegEmail(coRegEmail);
		linkCoRegistrantsRequestVO.setEventDate(eventDate);
		linkCoRegistrantsRequestVO.setEventType(eventType);
		linkCoRegistrantsRequestVO.setRegId(regId);
		linkCoRegistrantsRequestVO.setSiteId(siteId);

		linkCoRegistrantRequestVODocument
				.setLinkCoRegistrantRequestVO(linkCoRegistrantsRequestVO);

		DynamoHttpServletRequest pRequest = getRequest();
		ServletUtil.setCurrentRequest(pRequest);

		com.bedbathandbeyond.atg.LinkCoRegistrantResponseVODocument linkCoRegistrantResponseVODocument = profileServiceImpl
				.linkCoRegistrant(linkCoRegistrantRequestVODocument);
		assertNotNull(linkCoRegistrantResponseVODocument);

	}

	public void testlinkProfileAndRegistry() {

		String profileId = "130002";
		String coRegEmail = "test3@example.com";
		String eventDate = "12345";
		String eventType = "Baby";
		String regId = "153588307";
		String siteId = "BuyBuyBaby";

		ProfileServiceImpl profileServiceImpl = new ProfileServiceImpl();
		com.bedbathandbeyond.atg.LinkProfileRequestVODocument linkProfileRequestVODocument = com.bedbathandbeyond.atg.LinkProfileRequestVODocument.Factory
				.newInstance();

		com.bedbathandbeyond.atg.LinkProfileAndRegistryRequestVO linkProfileAndRegistryRequestVO = com.bedbathandbeyond.atg.LinkProfileAndRegistryRequestVO.Factory
				.newInstance();

		linkProfileAndRegistryRequestVO.setEventDate(eventDate);
		linkProfileAndRegistryRequestVO.setEventType(eventType);
		linkProfileAndRegistryRequestVO.setProfileId(profileId);
		linkProfileAndRegistryRequestVO.setRegId(regId);
		linkProfileAndRegistryRequestVO.setSiteId(siteId);
		linkProfileAndRegistryRequestVO.setCoRegEmail(coRegEmail);

		linkProfileRequestVODocument
				.setLinkProfileRequestVO(linkProfileAndRegistryRequestVO);

		DynamoHttpServletRequest pRequest = getRequest();
		ServletUtil.setCurrentRequest(pRequest);

		com.bedbathandbeyond.atg.LinkProfileResponseVODocument linkProfileResponseVODocument = profileServiceImpl
				.linkProfileAndRegistry(linkProfileRequestVODocument);

		assertNotNull(linkProfileResponseVODocument);

	}

	public void testLogin() {
		String email = (String) getObject("email");
		String password = (String) getObject("password");
		String siteId = (String) getObject("siteId");

		ProfileServiceImpl profileServiceImpl = new ProfileServiceImpl();
		com.bedbathandbeyond.atg.LoginRequestVODocument loginRequestVODocument = com.bedbathandbeyond.atg.LoginRequestVODocument.Factory
				.newInstance();
		com.bedbathandbeyond.atg.LoginRequestVO loginRequestVO = com.bedbathandbeyond.atg.LoginRequestVO.Factory
				.newInstance();

		loginRequestVO.setLoginId(email);
		loginRequestVO.setPassword(password);
		loginRequestVO.setSiteId(siteId);

		loginRequestVODocument.setLoginRequestVO(loginRequestVO);
		DynamoHttpServletRequest pRequest = getRequest();
		ServletUtil.setCurrentRequest(pRequest);
		profileServiceImpl.login(loginRequestVODocument);
		com.bedbathandbeyond.atg.LoginResponseVODocument loginResponseVODocument = profileServiceImpl
				.login(loginRequestVODocument);
		assertNotNull(loginResponseVODocument);
	}

}
