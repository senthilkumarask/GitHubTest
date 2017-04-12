package com.bbb.commerce.giftregistry.webservices.test;

import java.util.ArrayList;
import java.util.List;

import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.giftregistry.bean.AddItemsBean;
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean;
import com.bbb.commerce.giftregistry.formhandler.GiftRegistryFormHandler;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class WSTestAddItemsToGiftRegistry extends BaseTestCase{
	
	public void testWSService() throws Exception {
		 GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler) getObject("giftRegistryManager");
		GiftRegistryViewBean testGiftRegistryViewBean=new GiftRegistryViewBean();
		testGiftRegistryViewBean.setRegistrySize(1);
		List<AddItemsBean> addList =new ArrayList<AddItemsBean>();
		AddItemsBean pAdditem= new AddItemsBean();
		pAdditem.setQuantity((String)getObject("quantity"));
		pAdditem.setRegistryId((String)getObject("registryId"));
		pAdditem.setSku((String)getObject("skuId"));
		pAdditem.setProductId((String)getObject("productId"));
		addList.add(pAdditem);
		testGiftRegistryViewBean.setAdditem(addList);
		String pSiteId = (String) getObject("siteId");
		giftRegistryFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		String jsonObj = (String) getObject("jsonObj");
		giftRegistryFormHandler.setJasonCollectionObj(jsonObj);
		giftRegistryFormHandler.setAddItemsToRegServiceName("addItemsToRegistry");	

		
		Profile resultProfile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		assertTrue(resultProfile.isTransient());
		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		String username = (String) getObject("username");
	    resultProfile.setDataSource(profileTool.getItemFromEmail(username));
	    boolean errorExists=true;
	    if(null!=profileTool.getItemFromEmail(username))
	    {
		errorExists=giftRegistryFormHandler.handleAddItemToGiftRegistry(getRequest(), getResponse());
		if (errorExists)
		{
			assertTrue(errorExists);
		}
	    }
		assertTrue(errorExists);
		
		
	       
	}
}
