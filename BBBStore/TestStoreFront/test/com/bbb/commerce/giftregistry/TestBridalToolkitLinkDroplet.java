package com.bbb.commerce.giftregistry;

import java.util.List;

import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.giftregistry.droplet.BridalToolkitLinkDroplet;
import com.bbb.commerce.giftregistry.vo.BridalRegistryVO;
import com.sapient.common.tests.BaseTestCase;

public class TestBridalToolkitLinkDroplet extends BaseTestCase {

	public void testService() throws Exception {
		
		BridalToolkitLinkDroplet bridalToolkitLinkDroplet = (BridalToolkitLinkDroplet) getObject ("bridalToolkitRegistry");
		
		Profile resultProfile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		
		assertTrue(resultProfile.isTransient());
		
		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		String siteId = (String) getObject("siteId");
		 String username0 = (String) getObject("username0");
	    resultProfile.setDataSource(profileTool.getItemFromEmail(username0));
	    getRequest().setParameter("profile", resultProfile);
	    getRequest().setParameter("siteId","BedBathUS");
	    ServletUtil.setCurrentRequest(getRequest());
	    bridalToolkitLinkDroplet.service(getRequest(), getResponse());
	    List<BridalRegistryVO> list2=(List<BridalRegistryVO>)getRequest().getObjectParameter("bridalRegistryVOList");
	    
	    int  excpectedSizeOfList0=0;
	    if(null!=list2)
	    {
	    	excpectedSizeOfList0=list2.size();
	    }
	    addObjectToAssert("size0", excpectedSizeOfList0);
	    
	    
	   

	}

}
