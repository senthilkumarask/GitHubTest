package com.bbb.account;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.ServletUtil;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBProfileFormHandlerBV extends BaseTestCase {

	public void testCheckUserTokenBVRR() throws Exception {
		BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		String pSiteId = (String) getObject("siteId");
		
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		getRequest().getSession().setAttribute( BBBCoreConstants.BV_WRITE_REVIEW_URL, "http://del2l160BBB/store/account/login.jsp?BVDoLogin=yes");
		getRequest().getSession().setAttribute(BBBCoreConstants.RETURN_PAGE, "http://reviews.bedbathandbeyond.com");
		Object redirectURL = (String) "http://del2l160BBB/store/account/login.jsp";
		ServletUtil.setCurrentRequest(getRequest());
		bbbProfileFormHandler.checkUserTokenBVRR(getRequest(), redirectURL, false, "test1@example.com");
		assertNotNull(bbbProfileFormHandler.getLoginSuccessURL());
		getRequest().getSession().setAttribute( BBBCoreConstants.BV_WRITE_REVIEW_URL, "http://del2l160BBB/store/account/login.jsp?BVDoLogin=yes");
		getRequest().getSession().setAttribute(BBBCoreConstants.RETURN_PAGE, "http://reviews.bedbathandbeyond.com");
		Object checkString = bbbProfileFormHandler.checkUserTokenBVRR( getRequest(), redirectURL, true, "test1@example.com");
		assertNull(checkString);
	}
}

