package com.bbb.commerce.checkout.util;

import java.util.HashMap;
import java.util.Map;

import atg.commerce.order.Order;
import atg.repository.RepositoryItem;
import atg.userprofiling.Profile;

import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.sapient.common.tests.BaseTestCase;

public class TestSchoolPromotion extends BaseTestCase {

	public void testSchoolPromotion() throws Exception {
		BBBCouponUtil couponUtil = (BBBCouponUtil) getObject("BBBCouponUtil");
		String promoCode = (String) getObject("promoCode");
		String schoolPromotionID = (String) getObject("schoolPromotionID");
		
		Boolean result = null;
		result = couponUtil.compareClaimCode(promoCode, schoolPromotionID);
		assertNotNull("schoolPromo fails", result);
		
		
		BBBCartFormhandler formHandler = (BBBCartFormhandler) getObject("bbbCartFormHandler");
		Order order = formHandler.getOrder();
		Profile profile = (Profile)formHandler.getProfile();
		
		profile.setPropertyValue("schoolPromotions", (String)getObject("schoolPromotionID"));
		profile.setPropertyValue("schoolIds", (String)getObject("schoolId"));
		
		Map<String, RepositoryItem> promotions = new HashMap<String, RepositoryItem>();
		
		promotions = couponUtil.applySchoolPromotion(promotions, profile, order);
		
		assertTrue("School promotions is not applied : ", promotions.size() >0);
		
		assertNotNull("schoolId in BBBOrderImpl is null : ", ((BBBOrderImpl) order).getSchoolId());
		
		assertTrue("Profile schoolId is  not set in BBBOrderImpl : ", ((String)getObject("schoolId")).equals(((BBBOrderImpl) order).getSchoolId()));
	}
	
}
