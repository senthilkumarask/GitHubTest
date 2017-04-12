package com.bbb.commerce;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllCartTests {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllCartTests.class.getName());
        //$JUnit-BEGIN$
        suite.addTestSuite(com.bbb.commerce.checkout.formhandler.TestBBBBillingFormhandler.class);
        suite.addTestSuite(com.bbb.commerce.checkout.formhandler.TestBBBPaymentGroupFormHandler.class);
        suite.addTestSuite(com.bbb.payment.giftcard.TestBBBGiftCardFormHandler.class);
        suite.addTestSuite(com.bbb.commerce.order.purchase.TestBBBCartFormHandler.class);
        suite.addTestSuite(com.bbb.commerce.order.scheduler.TestBBBPurgeOrderScheduler.class);
        suite.addTestSuite(com.bbb.commerce.order.TestBBBCommerceItemManager.class);
        suite.addTestSuite(com.bbb.commerce.order.TestBBBOrderManager.class);
        suite.addTestSuite(com.bbb.commerce.order.TestBBBShippingGroupManager.class);
        
        suite.addTestSuite(com.bbb.commerce.payment.processor.TestGiftCardAuthorization.class);
        
        suite.addTestSuite(com.bbb.commerce.order.purchase.TestBBBPurchaseProcessHelper.class);
        suite.addTestSuite(com.bbb.framework.integration.TestServiceResponseBase.class);
        suite.addTestSuite(com.bbb.session.TestBBBMultiSiteComponentSessionmanager.class);
        
        //New Sapeunit added by Rajesh
        suite.addTestSuite(com.bbb.commerce.order.scheduler.TestBBBTibcoOrderMessageSchedulerJob.class);
        suite.addTestSuite(com.bbb.commerce.order.droplet.TestDisplayExpressCheckoutDroplet.class);
        
        
        //$JUnit-END$
        return suite;
    }

}
