package com.bbb.commerce.order.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import atg.commerce.order.OrderManager;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.userprofiling.Profile;

import com.bbb.account.BBBGetCouponsManager;
import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class WSTestPromotionLookupManager extends BaseTestCase {
    public void testGetPromotions() {
        PromotionLookupManager testObject = (PromotionLookupManager) getObject("bbbPromotionLookupManager");
        Profile profile = (Profile) getRequest().resolveName(BBBCoreConstants.ATG_PROFILE);
        OrderManager orderMan = (OrderManager) getObject("ordermanager");
        List profileAvailable;
        
        Map<String, RepositoryItem> populateValidPromotions = new HashMap<String, RepositoryItem>();
        try {
            BBBOrderImpl order = (BBBOrderImpl) orderMan.createOrder(profile.getRepositoryId());
            BBBRepositoryContactInfo address = ((BBBOrderTools) orderMan.getOrderTools()).createBillingAddress();
            address.setEmail("parag.doshi@bedbath.com");
            address.setPhoneNumber("9099099090");
            order.setBillingAddress(address);
            String site = (String) getObject("siteId");
            BBBGetCouponsManager couponManager = testObject.getCouponManager();
            
            SiteContextManager siteContextManager= (SiteContextManager)  getRequest().resolveName("/atg/multisite/SiteContextManager");
            siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(site));
            
            populateValidPromotions = testObject.populateValidPromotions(profile, order, site, true );
            assertTrue("no promotion returned ", populateValidPromotions.size() > 0);
            order.setCouponMap(populateValidPromotions);
            BBBCartFormhandler cartHandlerObject = (BBBCartFormhandler) getObject("bbbCartFormHandler");
            cartHandlerObject.setOrder(order);
            cartHandlerObject.setProfile(profile);
            Map couponMap = cartHandlerObject.getCouponList();
            String key = populateValidPromotions.keySet().iterator().next();
            couponMap.put(key , key);
            cartHandlerObject.setCouponList((HashMap<String, String>) couponMap);
            cartHandlerObject.resetFormExceptions();
            cartHandlerObject.handleApplyCoupons(getRequest(), getResponse());
            
           
            
            
        } catch (Exception e) {
        } 
        profileAvailable = (List) profile.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST);
        assertTrue("no promotion returned ", profileAvailable.size() > 0);
        // assertTrue("Available promotions are not equal to total promotions", profileAvailable.size() == populateValidPromotions.size());
        for (Iterator iterator = profileAvailable.iterator(); iterator
                .hasNext();) {//remove already granted promotion
            RepositoryItem promotion = (RepositoryItem) iterator.next();
            testObject.getPromotionTools().removePromotion((MutableRepositoryItem) profile, promotion, false);
        }
    }
    
    
}
