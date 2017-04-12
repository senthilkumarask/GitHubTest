package com.bbb.commerce.giftregistry;

import java.util.List;

import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.giftregistry.droplet.AddItemToGiftRegistryDroplet;
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.framework.BBBSiteContext;
import com.bbb.profile.session.BBBSessionBean;
import com.sapient.common.tests.BaseTestCase;

public class TestAddItemToGiftRegistryDroplet extends BaseTestCase {

	public void testService() throws Exception {
		
        AddItemToGiftRegistryDroplet addItemToGiftRegistryDroplet = (AddItemToGiftRegistryDroplet) getObject ("addItemToGiftRegistry");
		
		Profile resultProfile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		
		assertTrue(resultProfile.isTransient());
		
		
		
		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		String siteId = (String) getObject("siteId");
		/*String username2 = (String) getObject("username1");
	    resultProfile.setDataSource(profileTool.getItemFromEmail(username2));
	    getRequest().setParameter("siteId",null);
	    addItemToGiftRegistryDroplet.service(getRequest(), getResponse());
	    String errorMessage = getRequest().getParameter(
				GiftRegistryTypesDroplet.OUTPUT_ERROR_MSG);
		addObjectToAssert("errorMessage", errorMessage);
		String siteId = (String) getObject("siteId");
		String username1 = (String) getObject("username1");
		
	    resultProfile.setDataSource(profileTool.getItemFromEmail(username1));
	    getRequest().setParameter("siteId",siteId);
	    addItemToGiftRegistryDroplet.service(getRequest(), getResponse());
	    List<RegistrySkinnyVO> list=(List<RegistrySkinnyVO>)getRequest().getObjectParameter("registrySkinnyVOList");
	    int excpectedSizeOfList1=list.size();
	    addObjectToAssert("size1", excpectedSizeOfList1);*/
	   
	    String username0 = (String) getObject("username0");
	    resultProfile.setDataSource(profileTool.getItemFromEmail(username0));
	    //getRequest().setParameter("siteId",siteId);
	    //addItemToGiftRegistryDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
	    getRequest().setParameter("siteId","BedBathUS");
	    addItemToGiftRegistryDroplet.service(getRequest(), getResponse());
	    final BBBSessionBean sessionBean = (BBBSessionBean) getRequest()
				.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
	    List<RegistrySkinnyVO> list2=(List<RegistrySkinnyVO>)sessionBean.getValues().get("registrySkinnyVOList");
	    
	    int  excpectedSizeOfList0=0;
	    if(null!=list2)
	    {
	    	excpectedSizeOfList0=list2.size();
	    }
	    		
	    		
	    addObjectToAssert("size0", excpectedSizeOfList0);
	    
	    
	   

	}

}
