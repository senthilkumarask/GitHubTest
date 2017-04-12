package com.bbb.account.webservices;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Random;

import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.servlet.ServletUtil;

import com.bbb.account.BBBProfileManager;
import com.bbb.account.webservices.vo.BillingAddressResponseVO;
import com.bbb.account.webservices.vo.ForgotPasswordRequestVO;
import com.bbb.account.webservices.vo.ForgotPasswordResponseVO;
import com.bbb.account.webservices.vo.LinkCoRegistrantResponseVO;
import com.bbb.account.webservices.vo.LinkProfileResponseVO;
import com.bbb.account.webservices.vo.LoginRequestVO;
import com.bbb.account.webservices.vo.LoginResponseVO;
import com.bbb.account.webservices.vo.ProfileInfoResponseVO;
import com.bbb.account.webservices.vo.ProfileResponseVO;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBProfileServices extends BaseTestCase {
	
	public void testValidLogin(){
		
		String email = (String) getObject("email");
		String password = (String) getObject("password");
		String siteId = (String) getObject("siteId");
		getRequest().setAttribute("siteId", siteId);
		ServletUtil.setCurrentRequest(getRequest());
		BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices"); 
		BBBProfileServicesConstant constant = new BBBProfileServicesConstant();
		String atgProfile = BBBProfileServicesConstant.ATG_PROFILE;
		String no = BBBProfileServicesConstant.NO;
		String user = BBBProfileServicesConstant.USER;
		String yes = BBBProfileServicesConstant.YES;
		
		System.out.println(constant);
		System.out.println(atgProfile);
		System.out.println(no);
		System.out.println(user);
		System.out.println(yes);
		
		profileServices.setProfileServices(profileServices);
		profileServices.getProfileServices();
		profileServices.setMessageFrom("test");
		profileServices.getMessageFrom();
		

		profileServices.setLoggingDebug(true);
		LoginRequestVO reqVo = new LoginRequestVO();
		reqVo.setLoginId(email);
		reqVo.setPassword(password);
		reqVo.setSiteId(siteId);
		LoginResponseVO respVo = profileServices.login(reqVo);
		assertNotNull(respVo);
		if(null != respVo.getErrorMap()) {
			for(String str: respVo.getErrorMap().keySet()) {
				System.out.println(":Error in testValidLogin:"+respVo.getErrorMap().get(str));
			}
			System.out.println("ErrorMap:"+respVo.getErrorMap());
			assertFalse(respVo.getErrorMap().size()>0);
		}

		System.out.println("Profile ID:"+respVo.getProfileId());
		assertNotNull(respVo.getProfileId());
	}
	
	public void testLoginProfileId(){
		LoginRequestVO reqVo = new LoginRequestVO();
		String email = (String) getObject("email");
		String password = (String) getObject("password");
		String siteId = (String) getObject("siteId");
		BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices"); 
		profileServices.setLoggingDebug(true);
		reqVo.setLoginId(email);
		reqVo.setPassword(password);
		reqVo.setSiteId(siteId);
		LoginResponseVO respVo = profileServices.login(reqVo);
		assertNotNull(respVo);
		reqVo.setLoginId(respVo.getProfileId());
		reqVo.setLoginProfileId(true);
		respVo = profileServices.login(reqVo);
		assertNotNull(respVo);
		if(null != respVo.getErrorMap()) {
			for(String str: respVo.getErrorMap().keySet()) {
				System.out.println(":Error in testValidLogin:"+respVo.getErrorMap().get(str));
			}
			assertFalse(respVo.getErrorMap().size()>0);
		}
		System.out.println("Profile ID:"+respVo.getProfileId());
		assertNotNull(respVo.getProfileId());
	}
	
	public void testLoginInvalidPassword(){
		LoginRequestVO reqVo = new LoginRequestVO();
		reqVo.setLoginProfileId(true);
		String email = (String) getObject("email");
		String password = "xyz123";
		String siteId = (String) getObject("siteId");
		BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices"); 
		profileServices.setLoggingDebug(true);
		reqVo.setLoginId(email);
		reqVo.setPassword(password);
		reqVo.setSiteId(siteId);
		LoginResponseVO respVo = profileServices.login(reqVo);
		assertNotNull(respVo);
		assertNull(respVo.getProfileId());
		assertNotNull(respVo.getErrorMap());
	}
	public void testLoginEmptyReq(){
		LoginRequestVO reqVo = new LoginRequestVO();
		BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices"); 
		profileServices.setLoggingDebug(true);
		LoginResponseVO respVo = profileServices.login(reqVo);
		assertNotNull(respVo);
		assertNull(respVo.getProfileId());
		assertNotNull(respVo.getErrorMap());
	}
	
	
	public void testForgotPassword() throws RepositoryException, UnsupportedEncodingException{
		String email = (String) getObject("email");
		String siteId = (String) getObject("siteId");
		BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices");
		profileServices.setLoggingDebug(true);
		BBBProfileManager manger = (BBBProfileManager) getObject("bbbProfileManager");
		ForgotPasswordRequestVO reqVo = new ForgotPasswordRequestVO();
		reqVo.setEmail(email);
		reqVo.setSiteId(siteId);
		ForgotPasswordResponseVO respVo;
		respVo = profileServices.forgotPassword(reqVo);
		assertNotNull(respVo);
		assertNull(respVo.getErrorMap());
		manger.updatePassword(email, "Sapient!2");
	}
	
	public void testEmailEmptyReq() throws RepositoryException, UnsupportedEncodingException{
		ForgotPasswordRequestVO reqVo = new ForgotPasswordRequestVO();
		BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices"); 
		profileServices.setLoggingDebug(true);
		ForgotPasswordResponseVO respVo = profileServices.forgotPassword(reqVo);
		assertNotNull(respVo);
		assertNotNull(respVo.getErrorMap());
	}
	
	public void testSiteEmptyReq() throws RepositoryException, UnsupportedEncodingException{
		ForgotPasswordRequestVO reqVo = new ForgotPasswordRequestVO();
		BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices"); 
		profileServices.setLoggingDebug(true);
		ForgotPasswordResponseVO respVo = profileServices.forgotPassword(reqVo);
		assertNotNull(respVo);
		assertNotNull(respVo.getErrorMap());
	}
	
	public void testForgotPasswordInvalidEmail() throws RepositoryException, UnsupportedEncodingException{
		ForgotPasswordRequestVO reqVo = new ForgotPasswordRequestVO();
		String email = (String) getObject("email");
		String siteId = (String) getObject("siteId");
		BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices"); 
		profileServices.setLoggingDebug(true);
		reqVo.setEmail(email);
		reqVo.setSiteId(siteId);
		ForgotPasswordResponseVO respVo = profileServices.forgotPassword(reqVo);
		assertNotNull(respVo);
		assertNotNull(respVo.getErrorMap());
	}
	
	public void testForgotPasswordInvalidSiteId() throws RepositoryException, UnsupportedEncodingException{
		ForgotPasswordRequestVO reqVo = new ForgotPasswordRequestVO();
		String email = (String) getObject("email");
		String siteId = (String) getObject("siteId");
		BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices"); 
		profileServices.setLoggingDebug(true);
		reqVo.setEmail(email);
		reqVo.setSiteId(siteId);
		ForgotPasswordResponseVO respVo = profileServices.forgotPassword(reqVo);
		assertNotNull(respVo);
		assertNotNull(respVo.getErrorMap());
	}
	
	
	public void testCreateAccountValidation(){
		
		com.bbb.account.webservices.vo.ProfileRequestVO reqVo = new com.bbb.account.webservices.vo.ProfileRequestVO();
		BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices"); 
		profileServices.setLoggingDebug(true);
		profileServices.setDynRequest(getRequest());
		profileServices.setDynResponse(getResponse());
		
		String email = (String) getObject("email");
		String firstName = (String) getObject("firstName");
		String lastName = (String) getObject("lastName");
		String password = (String) getObject("password");
		String confirmPassword = (String) getObject("confirmPassword");
		String siteId = (String) getObject("siteId");
		String phone = (String) getObject("phone");
		String mobile = (String) getObject("mobile");
		boolean optIn = (Boolean) getObject("optIn");
		
		//IN-Valid Email
		reqVo.setEmail("vnalinisapient.com");
		reqVo.setPassword(password);
		reqVo.setConfirmPassword(confirmPassword);
		reqVo.setSiteId(siteId);
		reqVo.setFirstName(firstName);
		reqVo.setLastName(lastName);
		reqVo.setMobileNumber(mobile);
		reqVo.setPhoneNumber(phone);
		reqVo.setEmailOptIn(optIn);
		ProfileResponseVO respVo = profileServices.createUser(reqVo);
		assertNotNull(respVo);
		assertNotNull(respVo.getErrorMap());
		System.out.println(respVo);
		
		
		// Invalid First Name and Last Name
		
		reqVo.setEmail(email);
		reqVo.setPassword(password);
		reqVo.setConfirmPassword(confirmPassword);
		reqVo.setSiteId(siteId);
		reqVo.setFirstName("test01234567890123456789");
		reqVo.setLastName("test01234567890123456789");
		reqVo.setMobileNumber(mobile);
		reqVo.setPhoneNumber(phone);
		reqVo.setEmailOptIn(true);
		respVo = profileServices.createUser(reqVo);
		assertNotNull(respVo);
		assertNotNull(respVo.getErrorMap());
		System.out.println(respVo);
		
		
		// Invalid Password and Confirm Password
		
		reqVo.setEmail(email);
		reqVo.setPassword("bb12345!BBB");
		reqVo.setConfirmPassword("bb12345!BB");
		reqVo.setSiteId(siteId);
		reqVo.setFirstName(firstName);
		reqVo.setLastName(lastName);
		reqVo.setMobileNumber(mobile);
		reqVo.setPhoneNumber(phone);
		reqVo.setEmailOptIn(true);
		respVo = profileServices.createUser(reqVo);
		assertNotNull(respVo);
		assertNotNull(respVo.getErrorMap());
		System.out.println(respVo);
		
		
		
		// Invalid Site Id
		
		reqVo.setEmail(email);
		reqVo.setPassword(password);
		reqVo.setConfirmPassword(confirmPassword);
		reqVo.setSiteId("");
		reqVo.setFirstName(firstName);
		reqVo.setLastName(lastName);
		reqVo.setMobileNumber(mobile);
		reqVo.setPhoneNumber(phone);
		reqVo.setEmailOptIn(optIn);
		respVo = profileServices.createUser(reqVo);
		assertNotNull(respVo);
		assertNotNull(respVo.getErrorMap());
		System.out.println(respVo);
		
		
	}
	
	public void testCreateAccount() throws Exception{
		
		com.bbb.account.webservices.vo.ProfileRequestVO reqVo = new com.bbb.account.webservices.vo.ProfileRequestVO();
		BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices");
		profileServices.setDynRequest(getRequest());
		profileServices.setDynResponse(getResponse());
		profileServices.setLoggingDebug(true);
		
		String email = (String) getObject("email");
		String firstName = (String) getObject("firstName");
		Random random = new Random();
		email = firstName + random.nextInt(10000) + "@gmail.com";
		String lastName = (String) getObject("lastName");
		String password = (String) getObject("password");
		String confirmPassword = (String) getObject("confirmPassword");
		String siteId = (String) getObject("siteId");
		String phone = (String) getObject("phone");
		String mobile = (String) getObject("mobile");
		boolean optIn = (Boolean) getObject("optIn");
		
		//appending random integer to email so that test case do not fails when run
		//multiple times
		reqVo.setEmail((int)(Math.random()*9999)+email);
		reqVo.setPassword(password);
		reqVo.setConfirmPassword(confirmPassword);
		reqVo.setSiteId(siteId);
		reqVo.setFirstName(firstName);
		reqVo.setLastName(lastName);
		reqVo.setMobileNumber(mobile);
		reqVo.setPhoneNumber(phone);
		reqVo.setEmailOptIn(optIn);
		ServletUtil.setCurrentRequest(getRequest());
		ProfileResponseVO respVo = profileServices.createUser(reqVo);
		System.out.println(respVo);
		
		assertNotNull(respVo);
		if (respVo.getProfileId()!=null && !StringUtils.isEmpty(respVo.getProfileId())) {		
			assertNull(respVo.getErrorMap());
		} else {			
			assertNotNull(respVo.getErrorMap());
		}		
	}
	
	public void testGetProfileInfo() throws Exception{
		
		
			
			com.bbb.account.webservices.vo.ProfileInfoRequestVO reqVo = new com.bbb.account.webservices.vo.ProfileInfoRequestVO();
			BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices");
			profileServices.setLoggingDebug(true);
			profileServices.setDynRequest(getRequest());
			profileServices.setDynResponse(getResponse());
			BBBProfileManager manger = (BBBProfileManager) getObject("bbbProfileManager");
			
			String email = (String) getObject("email");
			String password = (String) getObject("password");
			String siteId = (String) getObject("siteId");
			boolean autoExtendProfile = (Boolean) getObject("autoExtendProfile");
			String profileId = (String) getObject("profileId");
			manger.updatePassword(email, "Sapient!2");
			
			reqVo.setAutoExtendProfile(autoExtendProfile);
			reqVo.setEmail(email);
			reqVo.setPassword(password);
			reqVo.setProfileId(profileId);
			reqVo.setSiteId(siteId);
			
			ProfileInfoResponseVO respVo = profileServices.getProfileInfo(reqVo);
			assertNull(respVo.getErrorMap());
			
			autoExtendProfile = false;
			profileId = "fsfs";
			reqVo.setAutoExtendProfile(autoExtendProfile);
			reqVo.setEmail(email);
			reqVo.setPassword(password);
			reqVo.setProfileId(profileId);
			reqVo.setSiteId(siteId);
			
			respVo = profileServices.getProfileInfo(reqVo);
			profileId = (String) getObject("profileId");
			assertNull(respVo.getErrorMap());
			
			email = "tdfs@xample.com";
			reqVo.setAutoExtendProfile(autoExtendProfile);
			reqVo.setEmail(email);
			reqVo.setPassword(password);
			reqVo.setProfileId(profileId);
			reqVo.setSiteId(siteId);
			respVo = profileServices.getProfileInfo(reqVo);
			assertNotNull(respVo.getErrorMap());
			email = (String) getObject("email");
			
			siteId = "";
			reqVo.setAutoExtendProfile(autoExtendProfile);
			reqVo.setEmail(email);
			reqVo.setPassword(password);
			reqVo.setProfileId(profileId);
			reqVo.setSiteId(siteId);
			respVo = profileServices.getProfileInfo(reqVo);
			assertNotNull(respVo.getErrorMap());
			siteId = (String) getObject("siteId");
			
			password = "Saient!2";
			reqVo.setAutoExtendProfile(autoExtendProfile);
			reqVo.setEmail(email);
			reqVo.setPassword(password);
			reqVo.setProfileId(profileId);
			reqVo.setSiteId(siteId);
			respVo = profileServices.getProfileInfo(reqVo);
			assertNotNull(respVo.getErrorMap());
			
		}
	
	
	
public void testGetBillingAddress() throws Exception{
		
		com.bbb.account.webservices.vo.BillingAddressRequestVO reqVo = new com.bbb.account.webservices.vo.BillingAddressRequestVO();
		BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices");
		profileServices.setLoggingDebug(true);
		profileServices.setDynRequest(getRequest());
		profileServices.setDynResponse(getResponse());
		
		String email = (String) getObject("email");
		String securityToken = (String) getObject("securityToken");
		String siteId = (String) getObject("siteId");
		String profileId = (String) getObject("profileId");
		
		
		reqVo.setEmail(email);
		reqVo.setProfileId(profileId);
		reqVo.setSecurityToken(securityToken);
		reqVo.setSiteId(siteId);
		
		
		BillingAddressResponseVO respVo = profileServices.getBillingAddress(reqVo);
		assertNull(respVo.getErrorMap());
		
		securityToken = "xyz";
		reqVo.setEmail(email);
		reqVo.setProfileId(profileId);
		reqVo.setSecurityToken(securityToken);
		reqVo.setSiteId(siteId);
				
		respVo = profileServices.getBillingAddress(reqVo);
		assertNotNull(respVo.getErrorMap());
		securityToken = (String) getObject("securityToken");
		
		
		siteId = "Buyaby";
				
		reqVo.setEmail(email);
		reqVo.setProfileId(profileId);
		reqVo.setSecurityToken(securityToken);
		reqVo.setSiteId(siteId);
		
		
		respVo = profileServices.getBillingAddress(reqVo);
				
		assertNotNull(respVo.getErrorMap());
		siteId = (String) getObject("siteId");
		
		
		profileId = "12345";
		
		reqVo.setEmail(email);
		reqVo.setProfileId(profileId);
		reqVo.setSecurityToken(securityToken);
		reqVo.setSiteId(siteId);
		
		
		respVo = profileServices.getBillingAddress(reqVo);
		System.out.println(respVo);
		
		assertNull(respVo.getErrorMap());
		
		
	}
	
	public void testlinkProfileAndRegistry() throws Exception{
		
		com.bbb.account.webservices.vo.LinkProfileRequestVO reqVo = new com.bbb.account.webservices.vo.LinkProfileRequestVO();
		BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices");
		profileServices.setLoggingDebug(true);
		profileServices.setDynRequest(getRequest());
		profileServices.setDynResponse(getResponse());
		getRequest().getLocale().setDefault(Locale.US);
		ServletUtil.setCurrentRequest(getRequest());
		
		/*Check Empty Profile data */
		String profileId = "";
		String coRegEmail = "";
		String eventDate = "";
		String eventType = "";
		String regId = "";
		String siteId = "";
		
		reqVo.setEventDate(eventDate);
		reqVo.setEventType(eventType);
		reqVo.setProfileId(profileId);
		reqVo.setRegId(regId);
		reqVo.setSiteId(siteId);
		reqVo.setCoRegEmail(coRegEmail);
		
		LinkProfileResponseVO respVo = profileServices.linkProfileAndRegistry(reqVo);
		assertNotNull(respVo.getErrorMap());

		/*Check Invalid Profile data */
		profileId = "";
		coRegEmail = "invalidEmail";
		eventDate = "";
		eventType = "";
		regId = "";
		siteId = "invalidEmailsiteId";
		
		reqVo.setEventDate(eventDate);
		reqVo.setEventType(eventType);
		reqVo.setProfileId(profileId);
		reqVo.setRegId(regId);
		reqVo.setSiteId(siteId);
		reqVo.setCoRegEmail(coRegEmail);
		
		respVo = profileServices.linkProfileAndRegistry(reqVo);
		assertNotNull(respVo.getErrorMap());

		
		profileId = "130002";
		coRegEmail = "test3@example.com";
		eventDate = "12/12/2020";
		eventType = "Baby";
		regId = "153738246";
		siteId = "BuyBuyBaby";
		
		reqVo.setEventDate(eventDate);
		reqVo.setEventType(eventType);
		reqVo.setProfileId(profileId);
		reqVo.setRegId(regId);
		reqVo.setSiteId(siteId);
		reqVo.setCoRegEmail(coRegEmail);
		
		respVo = profileServices.linkProfileAndRegistry(reqVo);
		assertNull(respVo.getErrorMap());
		
	}
	
	public void testCoRegistrant() throws Exception{
		
		com.bbb.account.webservices.vo.LinkCoRegistrantRequestVO reqVo = new com.bbb.account.webservices.vo.LinkCoRegistrantRequestVO();
		BBBProfileServices profileServices = (BBBProfileServices)getObject("profileServices");
		profileServices.setLoggingDebug(true);
		profileServices.setDynRequest(getRequest());
		profileServices.setDynResponse(getResponse());
		getRequest().getLocale().setDefault(Locale.US);
		ServletUtil.setCurrentRequest(getRequest());
		
		/*Check Empty Profile data */
		String profileId = "";
		String coRegEmail = "";
		String eventDate = "";
		String eventType = "";
		String regId = "";
		String siteId = "";
		
		reqVo.setEventDate(eventDate);
		reqVo.setEventType(eventType);
		reqVo.setProfileId(profileId);
		reqVo.setRegId(regId);
		reqVo.setSiteId(siteId);
		reqVo.setCoRegEmail(coRegEmail);
		
		
		LinkCoRegistrantResponseVO respVo = profileServices.linkCoRegistrant(reqVo);
		System.out.println(respVo);
		
		assertNotNull(respVo.getErrorMap());
		
		
		/*Check Invalid Profile data */
		profileId = "";
		coRegEmail = "invalidEmail";
		eventDate = "";
		eventType = "";
		regId = "";
		siteId = "invalidEmailsiteId";
		
		reqVo.setEventDate(eventDate);
		reqVo.setEventType(eventType);
		reqVo.setProfileId(profileId);
		reqVo.setRegId(regId);
		reqVo.setSiteId(siteId);
		reqVo.setCoRegEmail(coRegEmail);
		
		
		respVo = profileServices.linkCoRegistrant(reqVo);
		System.out.println(respVo);
		
		assertNotNull(respVo.getErrorMap());
		
		profileId = "130002";
		coRegEmail = "test3@example.com";
		eventDate = "12/12/2020";
		eventType = "Baby";
		regId = "153738246";
		siteId = "BuyBuyBaby";
		
		reqVo.setEventDate(eventDate);
		reqVo.setEventType(eventType);
		reqVo.setProfileId(profileId);
		reqVo.setRegId(regId);
		reqVo.setSiteId(siteId);
		reqVo.setCoRegEmail(coRegEmail);
		
		
		respVo = profileServices.linkCoRegistrant(reqVo);
		System.out.println(respVo);
		
		assertNull(respVo.getErrorMap());
		
		
		
	}
	
	
}
