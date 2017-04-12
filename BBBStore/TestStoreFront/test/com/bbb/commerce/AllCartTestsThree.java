package com.bbb.commerce;





import junit.framework.Test;
import junit.framework.TestSuite;

public class AllCartTestsThree {

    public static Test suite() {
        TestSuite suite = new TestSuite(AllCartTestsThree.class.getName());
        //$JUnit-BEGIN$   

        suite.addTestSuite(com.bbb.commerce.order.TestCybersourceIntegration.class);
        suite.addTestSuite(com.bbb.commerce.service.pricing.TestBBBPricingWebService.class);
        suite.addTestSuite(com.bbb.commerce.checkout.droplet.TestBBBCreditCardDisplayDroplet.class);
        suite.addTestSuite(com.bbb.commerce.checkout.droplet.TestBBBCheckoutProductDetailDroplet.class);
        suite.addTestSuite(com.bbb.commerce.checkout.droplet.TestGetApplicableShippingMethodsDroplet.class);
        suite.addTestSuite(com.bbb.commerce.checkout.droplet.TestGetApplicableLTLShippingMethodsDroplet.class);
        suite.addTestSuite(com.bbb.commerce.checkout.droplet.TestBBBBillingAddressDroplet.class);
        suite.addTestSuite(com.bbb.commerce.checkout.TestOrderCheckout.class);
        
        // new test suite added for committing the order by PAYPAL
        suite.addTestSuite(com.bbb.commerce.checkout.formhandler.TestBBBCommitOrderFormHandler.class);
        
        //$JUnit-END$
        return suite;
    }

}
