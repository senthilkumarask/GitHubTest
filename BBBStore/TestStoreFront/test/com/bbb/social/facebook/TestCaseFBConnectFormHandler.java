package com.bbb.social.facebook;

import java.util.HashSet;
import java.util.Set;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.framework.BBBSiteContext;
import com.bbb.social.facebook.formhandler.FBConnectFormHandler;
import com.bbb.social.facebook.vo.SchoolVO;
import com.bbb.social.facebook.vo.UserVO;
import com.sapient.common.tests.BaseTestCase;


/**
 * @author jsidhu
 *
 */
public class TestCaseFBConnectFormHandler extends BaseTestCase {

	 
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	/*public void testhandleValidateAndExtendAccout() throws Exception{
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		BBBProfileFormHandler bbbProfileForm = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		bbbProfileForm.setExpireSessionOnLogout(false);
		bbbProfileForm.getErrorMap().clear();
		bbbProfileForm.getFormExceptions().clear();
		bbbProfileForm.setFormErrorVal(false);

		if(!bbbProfileForm.getProfile().isTransient()){
			bbbProfileForm.handleLogout(pRequest, pResponse);
		}
		
		System.out.println("TestCaseFBConnectFormHandler.testhandleValidateAndExtendAccout.getFormExceptions()" + bbbProfileForm.getFormExceptions());
		assertTrue(bbbProfileForm.getProfile().isTransient());
		assertTrue(bbbProfileForm.getFormExceptions().size() == 0);
		
		//FBConnectFormHandler fbConnectForm = (FBConnectFormHandler) getObject("fbConnectForm");
		bbbProfileForm.setSiteContext(BBBSiteContext.getBBBSiteContext((String)getObject("siteId")));
		bbbProfileForm.setLoginSuccessURL("/account/login.jsp");
		bbbProfileForm.setLoginErrorURL("/account/extend_account.jsp");
		UserVO fbUserProfile = new UserVO();
		
		fbUserProfile.setUserName((String)getObject("fbusername"));
		fbUserProfile.setFirstName((String)getObject("fbfname"));
		fbUserProfile.setEmail((String)getObject("fbemail"));
		fbUserProfile.setGender((String)getObject("gender"));
		fbUserProfile.setLastName((String)getObject("fblname"));
		fbUserProfile.setName((String)getObject("fbname"));
		fbUserProfile.setMiddleName((String)getObject("fbmname"));
		bbbProfileForm.getValue().put("login", (String)getObject("fbemail"));
		bbbProfileForm.getValue().put("password",(String)getObject("password"));
		pRequest.setParameter("emailAddress",(String)getObject("fbemail") );
		bbbProfileForm.setEmailAddress((String)getObject("fbemail"));
		Set<SchoolVO> schools = new HashSet<SchoolVO>();
		SchoolVO school = new SchoolVO();

		school.setName("School Name");
		school.setSchoolID("School ID");

		schools.add(school);
		fbUserProfile.setSchools(schools);
		pRequest.getSession().setAttribute(FBConstants.FB_BASIC_INFO, fbUserProfile);
		ServletUtil.setCurrentRequest(pRequest);
		bbbProfileForm.getErrorMap().clear();
		bbbProfileForm.getFormExceptions().clear();
		bbbProfileForm.setFormErrorVal(false);
		System.out.println("bbbProfileFormHandler.getFormExceptions()" + bbbProfileForm.getFormExceptions());
		pRequest.setParameter("userCheckingOut","true");
		BBBProfileTools tools = (BBBProfileTools) bbbProfileForm.getProfileTools();
        tools.setMergeOrders(false);
		bbbProfileForm.handleValidateAndExtendAccount(pRequest, pResponse);
		
		//To get the current bbb profile and verify the extend account
		RepositoryItem bbbUserProfile = bbbProfileForm.getProfileTools().getItemFromEmail((String)getObject("fbemail"));
		//RepositoryItem bbbUserProfile = bbbProfileForm.getProfile().getDataSource();
		Map<String, Object> bbbUserCurrSiteMap = (Map<String, Object>) bbbUserProfile.getPropertyValue(bbbProfileForm.getPmgr().getUserSiteItemsPropertyName());
		System.out.println("\nBefore assertTrue(bbbUserCurrSiteMap.containsKey((String)getObject(siteId)))");
		assertTrue(bbbUserCurrSiteMap.containsKey((String)getObject("siteId")));
		//Verify the login flow
		System.out.println("\nBefore if(!bbbUserProfile.isTransient())");
		if(!bbbUserProfile.isTransient())
		{
			//To Get the Facebook profile && Verify the linking of FBprofile with Current Site
			RepositoryItem fbUserRepositoryItem = bbbProfileForm.getFacebookProfileTool().getFacebookUserProfile(fbUserProfile.getUserName());
			RepositoryItem bbbUserProfileforFB = (RepositoryItem) fbUserRepositoryItem.getPropertyValue(bbbProfileForm.getFacebookProfileTool().getPropertyManager().getBBBProfilePropertyName());
			Map<String, RepositoryItem> siteAssoc = (Map<String, RepositoryItem>) bbbUserProfileforFB.getPropertyValue(bbbProfileForm.getPmgr().getUserSiteItemsPropertyName());
			assertTrue(siteAssoc.containsKey((String)getObject("siteId")));		
			bbbProfileForm.handleLogout(getRequest(), getResponse());
		}
		bbbProfileForm.getErrorMap().clear();
		bbbProfileForm.getFormExceptions().clear();
		bbbProfileForm.setFormErrorVal(false);
	}
	*/
	/*public void testhandleExtendAccount() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		BBBProfileFormHandler bbbProfileForm = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		bbbProfileForm.setExpireSessionOnLogout(false);
		bbbProfileForm.getErrorMap().clear();
		bbbProfileForm.getFormExceptions().clear();
		bbbProfileForm.setFormErrorVal(false);
		
		if(!bbbProfileForm.getProfile().isTransient()){
			bbbProfileForm.handleLogout(getRequest(), getResponse());
		}
		System.out.println("TestCaseFBConnectFormHandler.testhandleExtendAccount.getFormExceptions()" + bbbProfileForm.getFormExceptions());
		assertTrue(bbbProfileForm.getProfile().isTransient());
		assertTrue(bbbProfileForm.getFormExceptions().size() == 0);
		
		bbbProfileForm.setLoginSuccessURL("/account/login.jsp");
		bbbProfileForm.setLoginErrorURL("/account/fc_enabled_sister_site.jsp");
		bbbProfileForm.setSiteContext(BBBSiteContext.getBBBSiteContext((String)getObject("siteId")));
		UserVO fbUserProfile = new UserVO();
		fbUserProfile.setUserName((String)getObject("fbusername"));
		fbUserProfile.setFirstName((String)getObject("fbfname"));
		fbUserProfile.setEmail((String)getObject("fbemail"));
		fbUserProfile.setGender((String)getObject("gender"));
		fbUserProfile.setLastName((String)getObject("fblname"));
		fbUserProfile.setName((String)getObject("fbname"));
		fbUserProfile.setMiddleName((String)getObject("fbmname"));
		
		bbbProfileForm.getValue().put("login", (String)getObject("fbemail"));
		//bbbProfileForm.getValue().put("password",(String)getObject("password"));
		Set<SchoolVO> schools = new HashSet<SchoolVO>();
		SchoolVO school = new SchoolVO();

		school.setName("School Name");
		school.setSchoolID("School ID");

		schools.add(school);
		fbUserProfile.setSchools(schools);
		pRequest.getSession().setAttribute(FBConstants.FB_BASIC_INFO, fbUserProfile);
		ServletUtil.setCurrentRequest(pRequest);
		pRequest.setParameter("userCheckingOut","true");
		BBBProfileTools tools = (BBBProfileTools) bbbProfileForm.getProfileTools();
        tools.setMergeOrders(false);
		bbbProfileForm.handleExtendAccount(pRequest, pResponse);
		
		//To get the current bbb profile and verify the extend account
		RepositoryItem bbbUserProfile = bbbProfileForm.getProfile().getDataSource();
		Map<String, Object> bbbUserCurrSiteMap = (Map<String, Object>) bbbUserProfile.getPropertyValue(bbbProfileForm.getPmgr().getUserSiteItemsPropertyName());
		assertTrue(bbbUserCurrSiteMap.containsKey((String)getObject("siteId")));
		//Verify the login flow
		assertTrue(!bbbUserProfile.isTransient());
		//To Get the Facebook profile && Verify the linking of FBprofile with Current Site
		
		RepositoryItem fbUserRepositoryItem = bbbProfileForm.getFacebookProfileTool().getFacebookUserProfile(fbUserProfile.getUserName());
		RepositoryItem bbbUserProfileforFB = (RepositoryItem) fbUserRepositoryItem.getPropertyValue(bbbProfileForm.getFacebookProfileTool().getPropertyManager().getBBBProfilePropertyName());
		Map<String, RepositoryItem> siteAssoc = (Map<String, RepositoryItem>) bbbUserProfileforFB.getPropertyValue(bbbProfileForm.getPmgr().getUserSiteItemsPropertyName());
		assertTrue(siteAssoc.containsKey((String)getObject("siteId")));		
		bbbProfileForm.handleLogout(getRequest(), getResponse());
		bbbProfileForm.getErrorMap().clear();
		bbbProfileForm.getFormExceptions().clear();
		bbbProfileForm.setFormErrorVal(false);

		//assertTrue(fbConnectForm.getBbbProfileFormHandler().getProfile().isTransient());
	}	
	*/
	public void testBbbProfileNotExist() throws Exception{
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		FBConnectFormHandler fbConnectForm = (FBConnectFormHandler) getObject("fbConnectForm");
		fbConnectForm.setSiteContext(BBBSiteContext.getBBBSiteContext((String)getObject("siteId")));
		fbConnectForm.setSuccessURL("/account/login.jsp");
		fbConnectForm.setErrorURL("/account/extend_account.jsp");
		UserVO fbUserProfile = new UserVO();		
		fbUserProfile.setUserName((String)getObject("fbusername"));
		fbUserProfile.setFirstName((String)getObject("fbfname"));
		fbUserProfile.setEmail((String)getObject("fbemail"));
		fbUserProfile.setGender((String)getObject("gender"));
		fbUserProfile.setLastName((String)getObject("fblname"));
		fbUserProfile.setName((String)getObject("fbname"));
		fbUserProfile.setMiddleName((String)getObject("fbmname"));		
		fbConnectForm.setEmailAddress((String)getObject("fbemail"));
		Set<SchoolVO> schools = new HashSet<SchoolVO>();
		SchoolVO school = new SchoolVO();

		school.setName("School Name");
		school.setSchoolID("School ID");

		schools.add(school);
		fbUserProfile.setSchools(schools);
		pRequest.getSession().setAttribute(FBConstants.FB_BASIC_INFO, fbUserProfile);
		ServletUtil.setCurrentRequest(pRequest);
		fbConnectForm.handleBbbProfileExist(pRequest, pResponse);
		System.out.println("-------testBbbProfileNotExist---------"+fbConnectForm.getEvent());
		assertTrue(FBConstants.EVENT_PROFILE_NOT_FOUND.equals(fbConnectForm.getEvent()));		
	}
	
	public void testBbbProfileExist() throws Exception{
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		FBConnectFormHandler fbConnectForm = (FBConnectFormHandler) getObject("fbConnectForm");
		fbConnectForm.setSiteContext(BBBSiteContext.getBBBSiteContext((String)getObject("siteId")));
		fbConnectForm.setSuccessURL("/account/login.jsp");
		fbConnectForm.setErrorURL("/account/extend_account.jsp");
		UserVO fbUserProfile = new UserVO();		
		fbUserProfile.setUserName((String)getObject("fbusername"));
		fbUserProfile.setFirstName((String)getObject("fbfname"));
		fbUserProfile.setEmail((String)getObject("fbemail"));
		fbUserProfile.setGender((String)getObject("gender"));
		fbUserProfile.setLastName((String)getObject("fblname"));
		fbUserProfile.setName((String)getObject("fbname"));
		fbUserProfile.setMiddleName((String)getObject("fbmname"));		
		fbConnectForm.setEmailAddress((String)getObject("fbemail"));
		Set<SchoolVO> schools = new HashSet<SchoolVO>();
		SchoolVO school = new SchoolVO();

		school.setName("School Name");
		school.setSchoolID("School ID");

		schools.add(school);
		fbUserProfile.setSchools(schools);
		pRequest.getSession().setAttribute(FBConstants.FB_BASIC_INFO, fbUserProfile);
		ServletUtil.setCurrentRequest(pRequest);
		fbConnectForm.handleBbbProfileExist(pRequest, pResponse);
		System.out.println("-------testBbbProfileExist---------"+fbConnectForm.getEvent());
		assertTrue(!FBConstants.EVENT_PROFILE_NOT_FOUND.equals(fbConnectForm.getEvent()));		
	}
	
	public void testCheckFBConnect() throws Exception{
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		FBConnectFormHandler fbConnectForm = (FBConnectFormHandler) getObject("fbConnectForm");
		fbConnectForm.setSiteContext(BBBSiteContext.getBBBSiteContext((String)getObject("siteId")));
		fbConnectForm.setSuccessURL("/account/login.jsp");
		fbConnectForm.setErrorURL("/account/extend_account.jsp");
		UserVO fbUserProfile = new UserVO();		
		fbUserProfile.setUserName((String)getObject("fbusername"));
		fbUserProfile.setFirstName((String)getObject("fbfname"));
		fbUserProfile.setEmail((String)getObject("fbemail"));
		fbUserProfile.setGender((String)getObject("gender"));
		fbUserProfile.setLastName((String)getObject("fblname"));
		fbUserProfile.setName((String)getObject("fbname"));
		fbUserProfile.setMiddleName((String)getObject("fbmname"));		
		fbConnectForm.setEmailAddress((String)getObject("fbemail"));
		Set<SchoolVO> schools = new HashSet<SchoolVO>();
		SchoolVO school = new SchoolVO();

		school.setName("School Name");
		school.setSchoolID("School ID");

		schools.add(school);
		fbUserProfile.setSchools(schools);
		pRequest.getSession().setAttribute(FBConstants.FB_BASIC_INFO, fbUserProfile);
		ServletUtil.setCurrentRequest(pRequest);
		fbConnectForm.handleCheckFBConnect(pRequest, pResponse);
		System.out.println("-------testCheckFBConnect---------"+fbConnectForm.getEvent());
		assertTrue(fbConnectForm.getEvent()!=null);		
	}	
	/*public void testFBUnlinking() throws Exception{
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBProfileFormHandler bbbProfileForm = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		bbbProfileForm.setExpireSessionOnLogout(false);
		FBConnectFormHandler fbConnectForm = (FBConnectFormHandler) getObject("fbConnectForm");
		fbConnectForm.setSiteContext(BBBSiteContext.getBBBSiteContext((String)getObject("siteId")));
		fbConnectForm.setSuccessURL("/account/login.jsp");
		fbConnectForm.setErrorURL("/account/extend_account.jsp");
		UserVO fbUserProfile = new UserVO();		
		fbUserProfile.setUserName((String)getObject("fbusername"));
		fbUserProfile.setFirstName((String)getObject("fbfname"));
		fbUserProfile.setEmail((String)getObject("fbemail"));
		fbUserProfile.setGender((String)getObject("gender"));
		fbUserProfile.setLastName((String)getObject("fblname"));
		fbUserProfile.setName((String)getObject("fbname"));
		fbUserProfile.setMiddleName((String)getObject("fbmname"));		
		fbConnectForm.setEmailAddress((String)getObject("fbemail"));
		Set<SchoolVO> schools = new HashSet<SchoolVO>();
		SchoolVO school = new SchoolVO();

		school.setName("School Name");
		school.setSchoolID("School ID");

		schools.add(school);
		fbUserProfile.setSchools(schools);
		pRequest.getSession().setAttribute(FBConstants.FB_BASIC_INFO, fbUserProfile);
		ServletUtil.setCurrentRequest(pRequest);
		bbbProfileForm.getErrorMap().clear();
		bbbProfileForm.getFormExceptions().clear();
		bbbProfileForm.setFormErrorVal(false);
		if(!bbbProfileForm.getProfile().isTransient()){
			bbbProfileForm.handleLogout(getRequest(), getResponse());
		}
		System.out.println("TestCaseFBConnectFormHandler.testFBUnlinking.getFormExceptions()" + bbbProfileForm.getFormExceptions());
		assertTrue(bbbProfileForm.getProfile().isTransient());
		assertTrue(bbbProfileForm.getFormExceptions().size() == 0);
		
		bbbProfileForm.getValue().put("login", (String)getObject("fbemail"));
		bbbProfileForm.getValue().put("password", (String)getObject("password"));
		atg.servlet.ServletUtil.setCurrentRequest(pRequest);
		pRequest.setParameter("userCheckingOut","true");
		BBBProfileTools tools = (BBBProfileTools) bbbProfileForm.getProfileTools();
		tools.setMergeOrders(false);
		boolean isLogin = bbbProfileForm.handleLogin(getRequest(), getResponse());
		assertFalse(bbbProfileForm.getProfile().isTransient());
		
		fbConnectForm.handleUnLinking(pRequest, pResponse);
		System.out.println("-------testCheckFBConnect---------"+fbConnectForm.getEvent());
		assertNotNull(fbConnectForm.getEvent());
		if(!bbbProfileForm.getProfile().isTransient()){
			bbbProfileForm.handleLogout(getRequest(), getResponse());
		}
		bbbProfileForm.getErrorMap().clear();
		bbbProfileForm.getFormExceptions().clear();
		bbbProfileForm.setFormErrorVal(false);
	}
*/
	 
	public void testFBConstants() throws Exception{
		
		assertEquals(FBConstants.EVENT_SUFFIX_FB_CONNECTED,"_WITH_FB_CONNECT");
		assertEquals(FBConstants.EVENT_PROFILE_NOT_FOUND,"PROFILE_NOT_FOUND");
		assertEquals(FBConstants.EVENT_BBB_PROFILE_EXIST_IN_SAME_SITE,"BBB_PROFILE_EXIST_IN_SAME_SITE");
		assertEquals(FBConstants.EVENT_BBB_PROFILE_EXIST_IN_SAME_SITE_GROUP,"BBB_PROFILE_EXIST_IN_SAME_SITE_GROUP");
		assertEquals(FBConstants.EVENT_BBB_PROFILE_EXIST_ON_DIFFERENT_SITE_GROUP,"BBB_PROFILE_EXIST_ON_DIFFERENT_SITE_GROUP");
		assertEquals(FBConstants.ERROR_FETCH_FACEBOOK_PROFILE,"error_fetch_fb_profile");
		assertEquals(FBConstants.EMPTY_STRING,"");
		assertEquals(FBConstants.EVENT_FB_PROFILE_FOUND_NOT_LINKED_WITH_BBB_PROFILE,"FB_PROFILE_FOUND_NOT_LINKED_WITH_BBB_PROFILE");
		assertEquals(FBConstants.FB_OPERATION_REMOVE,"FB_OPERATION_REMOVE");
		assertEquals(FBConstants.FB_OPERATION_CREATE,"FB_OPERATION_CREATE");
		assertEquals(FBConstants.FB_OPERATION_UPDATE,"FB_OPERATION_UPDATE");
		assertEquals(FBConstants.SECURITY_STATUS,"securityStatus");
		assertEquals(FBConstants.PARSE_FB_BASIC_INFO,"PARSE_FB_BASIC_INFO");
		assertEquals(FBConstants.FB_BASIC_INFO,"FB_BASIC_INFO");
		assertEquals(FBConstants.FB_ERROR_PARSE_BASIC_INFO,"Error occurred while fetching Facebook information");
		assertEquals(FBConstants.FB_HEADER_PARAM,"BBB-ajax-redirect-url");
		assertEquals(FBConstants.PROFILE_LOOKUP_BASED_ON_FBACCOUNTID,"username");
		assertEquals(FBConstants.PROFILE_LOOKUP_BASED_ON_FBEMAILID,"emailid");
		assertEquals(FBConstants.FB_UNLINK_SUCCESS,"unlinked");
		assertEquals(FBConstants.FB_UNLINK_ERROR,"unlinkingfailed");
		assertEquals(FBConstants.FB_PAGE_ACCOUNT_OVERVIEW,"myAccountOverview");
		assertEquals(FBConstants.ERROR_FB_UNEXPECTED,"error_fb_unexpected_error");
		assertEquals(FBConstants.PAGE_SECTION_LOGIN_REG,"loginRegistration");
		assertEquals(FBConstants.EVENT_BBB_PROFILE_EXIST_IN_SAME_SITE_WITH_FB_CONNECT,"BBB_PROFILE_EXIST_IN_SAME_SITE_WITH_FB_CONNECT");
		assertEquals(FBConstants.ERROR_FB_ALREADY_LINKED,"error_fb_already_link");
		assertEquals(FBConstants.ERROR_MESSAGE,"error");
		assertEquals(FBConstants.EVENT_MIGRATED_USER,"EVENT_MIGRATED_USER");
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
