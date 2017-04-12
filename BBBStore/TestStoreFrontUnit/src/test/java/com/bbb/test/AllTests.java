package com.bbb.test;

import java.util.ResourceBundle;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

import com.bbb.commerce.checkout.tools.TestBBBCheckoutToolsUnit;
import com.bbb.commerce.giftregistry.formhandler.TestGiftRegistryFormHandler;
import com.bbb.commerce.giftregistry.manager.TestBridalRegistriesUnit;
import com.bbb.commerce.giftregistry.manager.TestCoRegistrantLinkUnit;
import com.bbb.commerce.giftregistry.manager.TestGiftRegistryManagerUnit;
import com.bbb.commerce.giftregistry.manager.TestGiftRegistryRecommendationManager;
import com.bbb.commerce.giftregistry.manager.TestKGiftRegistryManager;
import com.bbb.commerce.giftregistry.tool.TestGiftRegistryToolsUnit;
import com.bbb.commerce.order.purchase.TestBBBCartFormHandler;
import com.bbb.rest.TestBBBGSConfigurationManager;
import com.bbb.rest.TestHeartbeatService;
import com.bbb.search.droplet.TestSearchDropletUnit;
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for Store front MOCK JUNITS");
		
		suite.addTest(new JUnit4TestAdapter(TestBBBCheckoutToolsUnit.class));
		suite.addTest(new JUnit4TestAdapter(TestGiftRegistryFormHandler.class));
		suite.addTest(new JUnit4TestAdapter(TestBridalRegistriesUnit.class));
		suite.addTest(new JUnit4TestAdapter(TestCoRegistrantLinkUnit.class));
		suite.addTest(new JUnit4TestAdapter(TestGiftRegistryManagerUnit.class));
		suite.addTest(new JUnit4TestAdapter(TestGiftRegistryRecommendationManager.class));
		suite.addTest(new JUnit4TestAdapter(TestKGiftRegistryManager.class));
		suite.addTest(new JUnit4TestAdapter(TestGiftRegistryToolsUnit.class));
		suite.addTest(new JUnit4TestAdapter(TestSearchDropletUnit.class));
		suite.addTest(new JUnit4TestAdapter(TestHeartbeatService.class));
		suite.addTest(new JUnit4TestAdapter(TestBBBCartFormHandler.class));	
		suite.addTest(new JUnit4TestAdapter(TestBBBGSConfigurationManager.class));
		return suite;
	}

}
