package com.bbb.internationalshipping;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.bbb.internationalshipping.droplet.TestInternationalShippingCheckoutDroplet;
import com.bbb.internationalshipping.droplet.TestInternationalShippingContextSetterDroplet;
import com.bbb.internationalshipping.formHandler.TestInternationalShipFormHandler;
import com.bbb.internationalshipping.integration.poFileProcessing.TestIntlPOFileUnMarshaller;
import com.bbb.internationalshipping.manager.TestInternationalCheckoutManager;
import com.bbb.internationalshipping.manager.TestInternationalOrderConfirmationManager;
import com.bbb.internationalshipping.order.TestBBBInternationalOrder;
import com.bbb.internationalshipping.pofileprocessing.service.TestIntlPODecryptService;
import com.bbb.internationalshipping.scheduler.TestPOProcessScheduler;
import com.bbb.internationalshipping.utils.TestBuildContext;
import com.bbb.internationalshipping.utils.TestInternationalOrderXmlRepoTools;

public class AllInternationalShippingTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for Account/Self service/Social classes");
		//$JUnit-BEGIN$
		suite.addTestSuite(TestInternationalShippingCheckoutDroplet.class);
		suite.addTestSuite(TestInternationalShippingContextSetterDroplet.class);
		suite.addTestSuite(TestInternationalShipFormHandler.class);
		suite.addTestSuite(TestInternationalCheckoutManager.class);
		suite.addTestSuite(TestBBBInternationalOrder.class);
		suite.addTestSuite(TestBuildContext.class);
		suite.addTestSuite(TestInternationalOrderXmlRepoTools.class);
		suite.addTestSuite(TestIntlPODecryptService.class);
		suite.addTestSuite(TestPOProcessScheduler.class);
		suite.addTestSuite(TestInternationalOrderConfirmationManager.class);
		suite.addTestSuite(TestIntlPOFileUnMarshaller.class);
 		//$JUnit-END$
		return suite;
	}

}
