package com.bbb.commerce.giftregistry.formhandler;

import atg.nucleus.Nucleus;
import atg.repository.MutableRepository;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.PropertyManager;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.framework.BBBSiteContext;
import com.bbb.profile.session.BBBSessionBean;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFormHandlerAaddAll1 extends BaseTestCase {
	public void testAddAllFromKickStarter() throws Exception {
		 
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryFormHandler");	 
		 String pJasonCollectionObj = (String) getObject("jasonCollectionObj");
		 String site = "BedBathUS";
		 String sessionBeanPath = (String) getObject("sessionBean");		 
		 String registryId = (String) getObject("registryId");
		 String eventType = (String) getObject("eventType");
		 String kickStarterId = (String) getObject("kickStarterId");
		 String addAllActions = (String)getObject("addAllAction");
		 String login = (String)getObject("login");
		 BBBSessionBean bbbSessionBean = (BBBSessionBean) getRequest().resolveName(sessionBeanPath);
		 bbbSessionBean.setRegistryId(registryId);
		 bbbSessionBean.setEventType(eventType);
		 bbbSessionBean.setEventType(kickStarterId);		 
		 
		 
		 giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(site));		 
		 giftRegistryFormHandler.setJasonCollectionObj(pJasonCollectionObj);		
		 giftRegistryFormHandler.setKickStarterAddAllAction(addAllActions);		 
		 BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		 RepositoryItem repoItem = bbbProfileFormHandler.getProfileTools().getItemFromEmail(login);	
		 
		 Profile resultProfile = bbbProfileFormHandler.getProfile();		 
		 ProfileTools profTools =  bbbProfileFormHandler.getProfileTools();
		 PropertyManager lManager = bbbProfileFormHandler.getProfile().getProfileTools().getPropertyManager();
		 resultProfile.setPropertyValue(lManager.getSecurityStatusPropertyName(),lManager.getSecurityStatusLogin());
		 resultProfile.setDataSource(repoItem);
		 profTools.setSecurityStatus(resultProfile, lManager.getSecurityStatusSecureLogin());
		DynamoHttpServletRequest dyRequest =  ServletUtil.getCurrentRequest();
		getRequest().setParameter("siteId", site);	
 	    giftRegistryFormHandler.setProfile(resultProfile);
		boolean isTrue =  giftRegistryFormHandler.handleAddItemToGiftRegistry(getRequest(),getResponse());
		assertTrue(isTrue);
		  }
	
}
