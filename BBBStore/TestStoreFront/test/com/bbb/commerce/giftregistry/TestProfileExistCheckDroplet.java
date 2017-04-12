package com.bbb.commerce.giftregistry;

import com.bbb.commerce.giftregistry.droplet.ProfileExistCheckDroplet;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestProfileExistCheckDroplet extends BaseTestCase {

	public void testService() throws Exception {
		ProfileExistCheckDroplet profileExistCheckDroplet = (ProfileExistCheckDroplet) getObject("profileExistCheckDroplet");
		
		
		String emailId = (String) getObject("emailId1");
		String siteId = (String) getObject("siteId");
		
		//profileExistCheckDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		getRequest().setParameter("siteId","BedBathUS");

		getRequest().setParameter("emailId", emailId);
		profileExistCheckDroplet.service(getRequest(), getResponse());
		boolean doesCoRegistrantExist = Boolean.parseBoolean(getRequest().getParameter("doesCoRegistrantExist"));
		assertFalse(doesCoRegistrantExist);

		emailId = (String) getObject("emailId2");
		getRequest().setParameter("emailId", emailId);
		profileExistCheckDroplet.service(getRequest(), getResponse());
		String status=getRequest().getParameter("doesCoRegistrantExist");
		if (status.equalsIgnoreCase("nonSister"))
		{
			doesCoRegistrantExist=true;
		}
		else{
		doesCoRegistrantExist = Boolean.parseBoolean(getRequest().getParameter("doesCoRegistrantExist"));
		}
		assertTrue(doesCoRegistrantExist);

	}

}
