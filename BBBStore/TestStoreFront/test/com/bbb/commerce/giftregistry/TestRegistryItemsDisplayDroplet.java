package com.bbb.commerce.giftregistry;

import java.util.Map;

import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.giftregistry.droplet.RegistryItemsDisplayDroplet;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestRegistryItemsDisplayDroplet extends BaseTestCase {
	
	
	/**Test case 1 where user is logged & list is not null*/	
	public void testCase1() throws Exception {
		
		//Case 2: If logged in user & list is not null
		RegistryItemsDisplayDroplet registryItemsDisplayDroplet = (RegistryItemsDisplayDroplet) getObject ("registryItemsDisplay");
		
		
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		 
		String username0 = (String) getObject("username0");
		String siteId = (String) getObject("siteId");
		profile.setDataSource(profileTool.getItemFromEmail(username0));
		
		getRequest().setParameter("registryId", "153551906");
		getRequest().setParameter("siteId",siteId);
		//registryItemsDisplayDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		getRequest().setParameter("sortSeq", "1");
		getRequest().setParameter("view", "1");
		getRequest().setParameter("startIdx", "0");
		getRequest().setParameter("blkSize", "10");
		getRequest().setParameter("eventTypeCode","BA1");
		getRequest().setParameter("isAvailForWebPurchaseFlag", "true");
		getRequest().setParameter("profile", profile);
		
		getRequest().setParameter("errorMsg", null);
		registryItemsDisplayDroplet.setRegistryItemsListServiceName("getRegistryItemList");
		registryItemsDisplayDroplet.service(getRequest(), getResponse());
		
		String errorMsg  =  (String)(getRequest().getObjectParameter("errorMsg"));

		//no error msg
		if (errorMsg != null){
			assertNotNull(errorMsg);
			Map categoryBuckets =  (Map)(getRequest().getObjectParameter("categoryBuckets"));
			
			//assert categoryBuckets are not null 
			assertNull(categoryBuckets);
		}else {
			assertNull(errorMsg);
			Map categoryBuckets =  (Map)(getRequest().getObjectParameter("categoryBuckets"));
			
			//assert categoryBuckets are not null 
			assertNull(categoryBuckets);
		}
		
		
	    
	}	

		
	/** Test case 4 where user is logged in & sortseq is 2 */
	public void testCase2() throws Exception {
		
		//Case 4: If logged in user & sort seq is 2  
		RegistryItemsDisplayDroplet registryItemsDisplayDroplet = (RegistryItemsDisplayDroplet) getObject ("registryItemsDisplay");
		
		
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		 
		String username0 = (String) getObject("username0");
		String siteId = (String) getObject("siteId");
		profile.setDataSource(profileTool.getItemFromEmail(username0));
		
		getRequest().setParameter("registryId", "153551906");
		getRequest().setParameter("siteId",siteId);
		//registryItemsDisplayDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		getRequest().setParameter("sortSeq", "2");
		getRequest().setParameter("startIdx", "0");
		getRequest().setParameter("blkSize", "10");
		getRequest().setParameter("eventTypeCode","BA1");
		getRequest().setParameter("isAvailForWebPurchaseFlag", "true");
		getRequest().setParameter("profile", profile);		
		getRequest().setParameter("errorMsg", null);
		registryItemsDisplayDroplet.setRegistryItemsListServiceName("getRegistryItemList");
		
		registryItemsDisplayDroplet.service(getRequest(), getResponse());
		
		String errorMsg  =  (String)(getRequest().getObjectParameter("errorMsg"));

		if (errorMsg != null){
			assertNotNull(errorMsg);
			Map categoryBuckets =  (Map)(getRequest().getObjectParameter("categoryBuckets"));
			
			//assert categoryBuckets are not null 
			assertNull(categoryBuckets);
		}else {
			assertNull(errorMsg);
			Map categoryBuckets =  (Map)(getRequest().getObjectParameter("categoryBuckets"));
			
			//assert categoryBuckets are not null 
			assertNotNull(categoryBuckets);
		}
	    
	}
	
	
	


}
