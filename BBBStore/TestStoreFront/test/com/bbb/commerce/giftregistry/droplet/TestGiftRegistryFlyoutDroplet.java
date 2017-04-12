package com.bbb.commerce.giftregistry.droplet;

import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGiftRegistryFlyoutDroplet extends BaseTestCase {

	public void testService() throws Exception {
		GiftRegistryFlyoutDroplet giftRegistryFlyoutDroplet = (GiftRegistryFlyoutDroplet) getObject("giftRegistryFlyoutDroplet");

		Profile resultProfile = (Profile) getRequest().resolveName(
				"/atg/userprofiling/Profile");

		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
		
		resultProfile.setDataSource(profileTool.getItemFromEmail("skumar1@sapient.com"));
		
		getRequest().setParameter("profile", resultProfile);
		
		System.out.println("resultProfile.isTransient(): "
				+ resultProfile.isTransient());
		// assertTrue(resultProfile.isTransient());
		
		giftRegistryFlyoutDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
		
		giftRegistryFlyoutDroplet.service(getRequest(), getResponse());

		
		System.out.println("getRequest().getParameter(userStatus): "
				+ getRequest().getParameter("userStatus"));
		
		final String USER_NOT_LOGGED_IN = "1";
		
		String userStatus = getRequest().getParameter("userStatus");
		
		assertFalse(USER_NOT_LOGGED_IN.equalsIgnoreCase(userStatus));
		
	}
} 