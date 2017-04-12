/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestGetCouponsDroplet.java
 *
 *  DESCRIPTION: Test Get Coupons Droplet
 *
 *  HISTORY:
 *  Author : Sandeep Yadav
 * December 30, 2011  Initial version
 */
package com.bbb.selfservice.droplet;

import java.util.ArrayList;
import java.util.List;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.BBBGetCouponsManager;
import com.bbb.account.GetCouponsDroplet;
import com.bbb.account.vo.CouponListVo;
import com.bbb.account.vo.CouponResponseVo;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.BBBSiteContext;
import com.bbb.utils.BBBUtility;
import com.sapient.common.tests.BaseTestCase;

public class TestGetCouponsDroplet extends BaseTestCase {

	public void testService() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();

		String pSiteId = (String) getObject("siteId");
		String pEmailAddress = (String) getObject("emailAddress");
		String pMobileNumber = (String) getObject("mobileNumber");
		String invalidEmailAddress = (String) getObject("invalidEmailAddress");
		
	
		boolean isCouponExist = false;

		pRequest.setParameter(BBBCoreConstants.EMAIL_ADDR_PARAM_NAME, pEmailAddress);
		pRequest.setParameter(BBBCoreConstants.MOBILE_NUMBER_PARAM_NAME, pMobileNumber);

		GetCouponsDroplet couponsDroplet = (GetCouponsDroplet) getObject("getCouponsDroplet");
		BBBGetCouponsManager couponsManager = (BBBGetCouponsManager) getObject("getCouponsManager");

		//couponsManager.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		couponsDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));

		couponsDroplet.getSiteContext();
		couponsDroplet.getMediaItmKey();
		couponsDroplet.getCatalogTools();

		//String siteId = couponsManager.getSiteContext().getSite().getId();

		//System.out.println("siteId : " + siteId);

		couponsDroplet.service(pRequest, pResponse);

		@SuppressWarnings("unchecked")
		List<CouponListVo> onlineCouponList = (ArrayList<CouponListVo>) getRequest().getObjectParameter("onlineCouponList");
		@SuppressWarnings("unchecked")
		List<CouponListVo> useAnywhereCouponList = (ArrayList<CouponListVo>) getRequest().getObjectParameter("useAnywhereCouponList");
		int couponCount = (Integer) getRequest().getObjectParameter("couponCount");
		
		if (!BBBUtility.isListEmpty(onlineCouponList) || !BBBUtility.isListEmpty(useAnywhereCouponList)) {
			isCouponExist = true;
			assertTrue(isCouponExist);
			assertTrue("Some problem occured while coupon retrieval", couponCount > 0);
		}
		
		pRequest.setParameter(BBBCoreConstants.EMAIL_ADDR_PARAM_NAME, invalidEmailAddress);
		couponsDroplet.service(pRequest, pResponse);
		String error = (String)getRequest().getObjectParameter("systemerror");

		if(error!=null){
			isCouponExist = false;
			assertFalse(isCouponExist);
		}
		couponsDroplet.setLoggingDebug(true);
		couponsDroplet.getCouponsManager();
		couponsDroplet.getTermConditionItmKey();
		CouponResponseVo vo = new CouponResponseVo();
		vo.setCouponList(vo.getCouponList());
		vo.setErrorStatus(null);
		vo.getErrorStatus();
		new CouponResponseVo().setCouponList(null);
	}

}
