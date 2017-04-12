package com.bbb.commerce;



import junit.framework.Test;
import junit.framework.TestSuite;

public class AllCartTestsTwo {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllCartTestsTwo.class.getName());
        //$JUnit-BEGIN$        
        suite.addTestSuite(com.bbb.commerce.order.formhandler.TestBBBShippingGroupFormhandler.class);
        suite.addTestSuite(com.bbb.commerce.order.purchase.TestBBBMultiShippingAddressDroplet.class);

        suite.addTestSuite(com.bbb.commerce.order.purchase.TestBBBListStatesDroplet.class);
        
        suite.addTestSuite(com.bbb.commerce.order.manager.WSTestPromotionLookupManager.class);
        suite.addTestSuite(com.bbb.valuelink.TestValueLinkGiftCardProcessor.class);
        
        suite.addTestSuite(com.bbb.commerce.checkout.util.TestSchoolPromotion.class);
        suite.addTestSuite(com.bbb.commerce.order.droplet.TestGiftWrapCheckDroplet.class);
        suite.addTestSuite(com.bbb.commerce.order.droplet.TestGiftWrapGreetingsDroplet.class); 
        suite.addTestSuite(com.bbb.commerce.order.purchase.TestBBBShippingFormhandler.class);
        
        suite.addTestSuite(com.bbb.payment.droplet.TestBBBPaymentGroupDroplet.class);
        
//		Pal Pal Junits
        suite.addTestSuite(com.bbb.commerce.droplet.TestPayPalDroplet.class);
        
        //$JUnit-END$
        return suite;
    }

}
