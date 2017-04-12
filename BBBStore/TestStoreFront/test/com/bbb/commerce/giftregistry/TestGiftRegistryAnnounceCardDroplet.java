package com.bbb.commerce.giftregistry;

import java.util.List;

import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.giftregistry.droplet.GiftRegistryAnnouncementCardDroplet;
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryAnnounceCardDroplet extends BaseTestCase {

	public void testService() throws Exception {
		
		GiftRegistryAnnouncementCardDroplet announceCardDroplet = (GiftRegistryAnnouncementCardDroplet) getObject ("giftRegistryAnnounceCard");
		
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		
		profile.setDataSource(null);
		//ensure profile is transient
		
		assertTrue("profile is not transient",profile.isTransient());
		
		getRequest().setParameter("profile", profile);
		String siteId = (String) getObject("siteId");
		getRequest().setParameter("siteId", siteId);
		
		//Case 1: If user is transient
		announceCardDroplet.service(getRequest(), getResponse());
		//assert registries list is null
		assertNull("For transient profile regstries are not null",getRequest().getObjectParameter("registries"));


		profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		
		//Case 2: If logged in user with zero registry
		String username0 = (String) getObject("username0");
		
		profile.setDataSource(profileTool.getItemFromEmail(username0));
		//assert user ls logged in
		//assertFalse(profile.isTransient());
		
		//getRequest().setParameter("siteId", siteId);
		getRequest().setParameter("siteId", siteId);
		getRequest().setParameter("profile", profile);
		
		announceCardDroplet.service(getRequest(), getResponse());
		List<RegistrySkinnyVO> list = (List<RegistrySkinnyVO>)(getRequest().getObjectParameter("registries"));
		//List<RegistrySkinnyVO> list =  announceCardDroplet.getGiftRegistryManager().fetchUsersBabyRegistries(profile, siteId );
	    assertNull(list);
	    //addObjectToAssert( "size0", excpectedSizeOfList0);
	    profile.setDataSource(null);
		assertTrue("profile1 is not transient",profile.isTransient());
		

		//Case 3: If logged in user with one registry	    
	    profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
	   	String username1 = (String) getObject("username1");
	    profile.setDataSource(profileTool.getItemFromEmail(username1));

	    //getRequest().setParameter("siteId", siteId);
	    getRequest().setParameter("siteId", siteId);
	    getRequest().setParameter("profile", profile);
	    announceCardDroplet.service(getRequest(), getResponse());
	    //List<RegistrySkinnyVO> list1 =  announceCardDroplet.getGiftRegistryManager().fetchUsersBabyRegistries(profile, siteId);
	    List<RegistrySkinnyVO> list1 =  (List<RegistrySkinnyVO>)(getRequest().getObjectParameter("registries"));
		//assert user ls logged in
		//assertFalse(profile.isTransient());
	    int  excpectedSizeOfList1=1;
	    if (null!=list1)
	    {
		excpectedSizeOfList1=list1.size();
		
	    }
	    
	    addObjectToAssert("size1", excpectedSizeOfList1);
	    assertNotNull(announceCardDroplet.getGiftRegistryManager());
   	   
		
	    
	    profile.setDataSource(null);
	    
		assertTrue("profile2 is now transient",profile.isTransient());
		
		//Case 4: If logged in user with multiple registries (more than 1)
		profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
	    String username2 = (String) getObject("username2");
	    profile.setDataSource(profileTool.getItemFromEmail(username2));
	    
	   // getRequest().setParameter("siteId", siteId);
	    
	    getRequest().setParameter("siteId", "BedBathUS");
	    getRequest().setParameter("profile", profile);
	    announceCardDroplet.service(getRequest(), getResponse());
	    List<RegistrySkinnyVO> list2 =  (List<RegistrySkinnyVO>)(getRequest().getObjectParameter("registries"));
	    //List<RegistrySkinnyVO> list2 =  announceCardDroplet.getGiftRegistryManager().fetchUsersBabyRegistries(profile, siteId);
		//assert user ls logged in
	    int  excpectedSizeOfList2=1;
	    if (null!=list2)
	    {
	    	excpectedSizeOfList2=list2.size();
		
	    }
	    addObjectToAssert("size2", excpectedSizeOfList2);
	    profile.setDataSource(null);
		assertTrue("profile3 is now transient",profile.isTransient());

	    
	}

}
