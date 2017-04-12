package com.bbb.commerce.giftregistry;

import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.giftregistry.droplet.GiftRegistryFlyoutDroplet;
import com.bbb.commerce.giftregistry.droplet.RegistryInfoDisplayDroplet;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestRegistryInfoDisplayDroplet extends BaseTestCase {

	/** Test case 1 where view is guest and user is transient & no registryId */
	public void testServiceGuest1() throws Exception {
		
		RegistryInfoDisplayDroplet registryInfoDisplayDroplet = (RegistryInfoDisplayDroplet) getObject ("registryInfoDisplay");
		
		//ensure profile is transient
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		profile.setDataSource(null);

		assertTrue(profile.isTransient());
		getRequest().setParameter("registryId", null);
		getRequest().setParameter("errorMsg", null);
		String siteId = (String) getObject("siteId");
		//getRequest().setParameter("siteId",siteId);
		registryInfoDisplayDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		registryInfoDisplayDroplet.service(getRequest(), getResponse());
		
		String errorMsg  =  (String)(getRequest().getObjectParameter("errorMsg"));		
		
		addObjectToAssert("errInvalidReg", errorMsg);
	    
	}
	
	/** Test case 2 where view is guest and user is transient & registryId =10001 */	
	public void testServiceGuest2() throws Exception {
		
		RegistryInfoDisplayDroplet registryInfoDisplayDroplet = (RegistryInfoDisplayDroplet) getObject ("registryInfoDisplay");
		
		//ensure profile is transient
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		profile.setDataSource(null);

		assertTrue(profile.isTransient());
		
		getRequest().setParameter("registryId", "153588756");
		
		getRequest().setParameter("errorMsg", null);
		String siteId = (String) getObject("siteId");
		//getRequest().setParameter("siteId",siteId);
		registryInfoDisplayDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		registryInfoDisplayDroplet.setRegistryInfoServiceName("getRegistryInfo");		
		registryInfoDisplayDroplet.service(getRequest(), getResponse());
		
		String errorMsg  =  (String)(getRequest().getObjectParameter("errorMsg"));

		if(errorMsg ==null ){
			//no error msg
			assertNull(errorMsg);
			
			RegistrySummaryVO registrySummaryVO =  (RegistrySummaryVO)(getRequest().getObjectParameter("registrySummaryVO"));
			
			//assert registrySummaryVO is not null 
			assertNotNull(registrySummaryVO);
			
		}else{
			//no error msg
			assertNotNull(errorMsg);
			
			RegistrySummaryVO registrySummaryVO =  (RegistrySummaryVO)(getRequest().getObjectParameter("registrySummaryVO"));
			
			//assert registrySummaryVO is not null 
			assertNull(registrySummaryVO);			
		}
		
	}

	/** Test case 3 where view is owner and user is transient */
	public void testServiceOwner1() throws Exception {

		RegistryInfoDisplayDroplet registryInfoDisplayDroplet = (RegistryInfoDisplayDroplet) getObject ("registryInfoDisplay");


		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		profile.setDataSource(null);
		
		assertTrue(profile.isTransient());
		
		getRequest().setParameter("registryId", "153588756");
		getRequest().setParameter("displayView", "owner");
		getRequest().setParameter("errorMsg", null);
		String siteId = (String) getObject("siteId");
		registryInfoDisplayDroplet.setRegistryInfoServiceName("getRegistryInfo");
		//getRequest().setParameter("siteId",siteId);
		registryInfoDisplayDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		registryInfoDisplayDroplet.service(getRequest(), getResponse());
		
		String errorMsg  =  (String)(getRequest().getObjectParameter("errorMsg"));		
		
		addObjectToAssert("errInvalidReg", errorMsg);
	}

	/** Test case 4 where view is owner, user is logged &  registryId does not belong to him*/	
	public void testServiceOwner2() throws Exception {
	
   
		RegistryInfoDisplayDroplet registryInfoDisplayDroplet = (RegistryInfoDisplayDroplet) getObject ("registryInfoDisplay");
		
		
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		 
		String username0 = (String) getObject("username0");
		String siteId = (String) getObject("siteId");
		profile.setDataSource(profileTool.getItemFromEmail(username0));
		
		getRequest().setParameter("displayView", "owner");
		getRequest().setParameter("registryId", "153588756");		
		
		getRequest().setParameter("errorMsg", null);
		registryInfoDisplayDroplet.setRegistryInfoServiceName("getRegistryInfo");
		//getRequest().setParameter("siteId",siteId);
		registryInfoDisplayDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		registryInfoDisplayDroplet.service(getRequest(), getResponse());
		
		String errorMsg  =  (String)(getRequest().getObjectParameter("errorMsg"));		
		
		addObjectToAssert("errInvalidReg", errorMsg);

	
	}

	/** Test case 5 where view is owner, user is logged &  registryId belongs to him*/	
	public void testServiceOwner3() throws Exception {
		
 
		GiftRegistryFlyoutDroplet giftRegistryFlyoutDroplet = (GiftRegistryFlyoutDroplet) getRequest().resolveName("/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet");

		Profile resultProfile = (Profile) getRequest().resolveName(
				"/atg/userprofiling/Profile");

		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		
		String username0 = (String) getObject("username0");
		
		resultProfile.setDataSource(profileTool.getItemFromEmail(username0));
		
		getRequest().setParameter("profile", resultProfile);
		
		System.out.println("resultProfile.isTransient(): "
				+ resultProfile.isTransient());
		// assertTrue(resultProfile.isTransient());
		
		String siteId = (String) getObject("siteId");
		
		giftRegistryFlyoutDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		
		giftRegistryFlyoutDroplet.service(getRequest(), getResponse());
		
		
		
		RegistryInfoDisplayDroplet registryInfoDisplayDroplet = (RegistryInfoDisplayDroplet) getObject ("registryInfoDisplay");
		
		
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		//ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		 
		
		
		profile.setDataSource(profileTool.getItemFromEmail(username0));
		
		getRequest().setParameter("displayView", "owner");
		getRequest().setParameter("registryId", "153587050");	
		
		getRequest().setParameter("errorMsg", null);
		
		registryInfoDisplayDroplet.setRegistryInfoServiceName("getRegistryInfo");
		//getRequest().setParameter("siteId",siteId);
		registryInfoDisplayDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		registryInfoDisplayDroplet.service(getRequest(), getResponse());
		
		String errorMsg  =  (String)(getRequest().getObjectParameter("errorMsg"));

		if(errorMsg ==null ){
			//no error msg
			assertNull(errorMsg);
			
			RegistrySummaryVO registrySummaryVO =  (RegistrySummaryVO)(getRequest().getObjectParameter("registrySummaryVO"));
			
			//assert registrySummaryVO is not null 
			assertNotNull(registrySummaryVO);
			
		}else{
			//no error msg
			assertNotNull(errorMsg);
			
			RegistrySummaryVO registrySummaryVO =  (RegistrySummaryVO)(getRequest().getObjectParameter("registrySummaryVO"));
			
			//assert registrySummaryVO is not null 
			if (null==registrySummaryVO)
			{
			assertNull(registrySummaryVO);
			}
			else
			{
				assertNotNull(registrySummaryVO);
			}
		}
	    
	}
	
	/**Test case user is logged , view is guest & not null registryId*/	
	public void testServiceOwner4() throws Exception {
		
   
		RegistryInfoDisplayDroplet registryInfoDisplayDroplet = (RegistryInfoDisplayDroplet) getObject ("registryInfoDisplay");
		
		
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		 
		String username0 = (String) getObject("username0");
		String siteId = (String) getObject("siteId");
		profile.setDataSource(profileTool.getItemFromEmail(username0));
		
		getRequest().setParameter("displayView", "guest");
		getRequest().setParameter("registryId", "153501902");
		
		getRequest().setParameter("errorMsg", null);
		registryInfoDisplayDroplet.setRegistryInfoServiceName("getRegistryInfo");
		//getRequest().setParameter("siteId",siteId);
		registryInfoDisplayDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		registryInfoDisplayDroplet.service(getRequest(), getResponse());
		
		String errorMsg  =  (String)(getRequest().getObjectParameter("errorMsg"));
		
		if(errorMsg ==null ){
			//no error msg
			assertNull(errorMsg);
			
			RegistrySummaryVO registrySummaryVO =  (RegistrySummaryVO)(getRequest().getObjectParameter("registrySummaryVO"));
			
			//assert registrySummaryVO is not null 
			assertNotNull(registrySummaryVO);
			
		}else{
			//no error msg
			assertNotNull(errorMsg);
			
			RegistrySummaryVO registrySummaryVO =  (RegistrySummaryVO)(getRequest().getObjectParameter("registrySummaryVO"));
			
			//assert registrySummaryVO is not null 
			if (null==registrySummaryVO)
			{
			assertNull(registrySummaryVO);
			}
			else
			{
				assertNotNull(registrySummaryVO);
			}
				
		}
		
		//make sure use gets loged out
		profile.setDataSource(null);
	    
	}	


}
