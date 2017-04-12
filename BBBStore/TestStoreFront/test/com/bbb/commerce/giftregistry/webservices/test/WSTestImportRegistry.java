package com.bbb.commerce.giftregistry.webservices.test;

import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.giftregistry.formhandler.GiftRegistryFormHandler;
import com.bbb.commerce.giftregistry.vo.RegistryTypes;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class WSTestImportRegistry extends BaseTestCase{
	
	public void testImportWSService() throws Exception {
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryManager");
		 String username = (String) getObject("username");
		 Profile resultProfile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		 ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		 resultProfile.setDataSource(profileTool.getItemFromEmail(username));
		 giftRegistryFormHandler.setRegistryId((String)getObject("registryId"));
		 giftRegistryFormHandler.setRegistryPassword((String)getObject("password"));
		 //getRequest().setParameter("siteId",((String)getObject("siteId")));
		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(((String)getObject("siteId"))));
		 giftRegistryFormHandler.setImportEventDate((String)getObject("eventDate"));
		 giftRegistryFormHandler.setImportEventType((String)getObject("eventType"));
		 giftRegistryFormHandler.setImportRegServiceName("importRegistry");
		 ServletUtil.setCurrentRequest(getRequest());
		 boolean isNoError=giftRegistryFormHandler.handleImportRegistry( getRequest(),  getResponse());
		 RegistryVO registryVO =giftRegistryFormHandler.getRegistryVO();
		 if( registryVO!= null) {
			 registryVO.setRegistryId("1212120");
			 registryVO.getEvent().setEventDate("01/01/2012");
			 registryVO.getEvent().setShowerDate("01/01/2012");
			 RegistryTypes registryType = new RegistryTypes();
			 registryType.setRegistryTypeName("BA1");
			 registryVO.setRegistryType( registryType);
			 //setRegistryType("BA1");
			 giftRegistryFormHandler.getGiftRegistryManager().linkRegistry(registryVO, true);
		 }
		 addObjectToAssert("isNoError", isNoError);
		 assertNull(giftRegistryFormHandler.getImportURL());
	       
	}
	public void testImportWSServiceError() throws Exception {
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryManager");
		 String username = (String) getObject("username");
		 Profile resultProfile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		 ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		 resultProfile.setDataSource(profileTool.getItemFromEmail(username));
		 giftRegistryFormHandler.setRegistryId((String)getObject("registryId"));
		 giftRegistryFormHandler.setRegistryPassword((String)getObject("password"));
		 //getRequest().setParameter("siteId",((String)getObject("siteId")));
		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(((String)getObject("siteId"))));
		 giftRegistryFormHandler.setImportEventDate((String)getObject("eventDate"));
		 giftRegistryFormHandler.setImportEventType((String)getObject("eventType"));
		 giftRegistryFormHandler.setImportRegServiceName("importRegistry");
		 ServletUtil.setCurrentRequest(getRequest());
		 boolean isNoError=giftRegistryFormHandler.handleImportRegistry( getRequest(),  getResponse());
		 giftRegistryFormHandler.getSessionBean().setImportRegistryRedirectUrl(null);
		 addObjectToAssert("isNoError", isNoError);
	       
	}
}
