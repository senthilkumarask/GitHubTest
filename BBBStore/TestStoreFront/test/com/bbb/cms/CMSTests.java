/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  AllTests.java
 *
 *  DESCRIPTION: Test suite for the package
 *
 *  HISTORY:
 *  Oct 14, 2011  Initial version
*/
package com.bbb.cms;



import junit.framework.Test;
import junit.framework.TestSuite;

import com.bbb.certona.marshaller.TestCertonaCatalogFeedMarshaller;
import com.bbb.certona.marshaller.TestCertonaInventoryFeedMarshaller;
import com.bbb.certona.utils.TestCertonaCatalogFeedTools;
import com.bbb.cms.droplet.TestBBBGetRootCategoryDroplet;
import com.bbb.cms.droplet.TestBridalShowDetailDroplet;
import com.bbb.cms.droplet.TestBridalShowStateDroplet;
import com.bbb.cms.droplet.TestCircularLandingDroplet;
import com.bbb.cms.droplet.TestCmsNavDroplet;
import com.bbb.cms.droplet.TestCollegeCategoryDroplet;
import com.bbb.cms.droplet.TestCollegeCollectionsDroplet;
import com.bbb.cms.droplet.TestGuidesLongDescDroplet;
import com.bbb.cms.droplet.TestGuidesTemplateDroplet;
import com.bbb.cms.droplet.TestGuidesVideosDroplet;
import com.bbb.cms.droplet.TestHeaderWhatsNewDroplet;
import com.bbb.cms.droplet.TestHomePageProductsDroplet;
import com.bbb.cms.droplet.TestHomePageTemplateDroplet;
import com.bbb.cms.droplet.TestLandingTemplateDroplet;
import com.bbb.cms.droplet.TestNextCollegeCollectionDroplet;
import com.bbb.cms.droplet.TestPaginationDroplet;
import com.bbb.cms.droplet.TestRecommenderLandingPageTemplateDroplet;
import com.bbb.cms.droplet.TestRegistrantLandingPageTemplateDroplet;
import com.bbb.cms.droplet.TestRegistryTemplateDroplet;
import com.bbb.cms.droplet.TestSchoolLookupDroplet;
import com.bbb.cms.droplet.TestStaticTemplateDroplet;
import com.bbb.cms.email.TestEmailAPageFormHandler;
import com.bbb.cms.manager.TestEmailTemplateManager;
import com.bbb.cms.manager.TestGuidesManager;
import com.bbb.cms.manager.TestHomePageTemplateManager;
import com.bbb.cms.manager.TestLandingManager;
import com.bbb.cms.manager.TestLblTxtManager;
import com.bbb.cms.manager.TestRegistryManager;
import com.bbb.cms.manager.TestStaticTemplateManager;
import com.bbb.cms.tools.TestCmsTools;
import com.bbb.commerce.browse.droplet.TestCollegeSearchDroplet;
import com.bbb.commerce.browse.droplet.TestProductStatusDroplet;
import com.bbb.commerce.browse.droplet.TestStatesSearchDroplet;
import com.bbb.commerce.catalog.TestBBBCatalogTools;
import com.bbb.commerce.catalog.droplet.TestPromotionLookupDroplet;
import com.bbb.commerce.catalog.formhandler.TestBBBPreviewDateFormHandler;
import com.bbb.commerce.checkout.droplet.TestBBBSkuPropDetailsDroplet;
import com.bbb.commerce.checkout.droplet.TestIsProductSKUShippingDroplet;
import com.bbb.commerce.inventory.InventoryTestCase;
import com.bbb.commerce.order.feeds.TestOrderStatusUpdateManager;
import com.bbb.common.manager.TestShippingMethodManager;
import com.bbb.integration.BazaarVoice.TestBazaarVoiceSchedulerJob;
import com.bbb.selfservice.droplet.TestScheduleAppointmentDroplet;

public class CMSTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.bbb.cms.Bridalshow");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestBridalShow.class);
        suite.addTestSuite(TestLblTxtManager.class);
        suite.addTestSuite(TestStaticTemplateManager.class);
        suite.addTestSuite(TestBBBCatalogTools.class);
        suite.addTestSuite(TestLandingManager.class);
        suite.addTestSuite(TestGuidesManager.class);
		suite.addTestSuite(TestHomePageTemplateManager.class);
		suite.addTestSuite(InventoryTestCase.class);
        suite.addTestSuite(TestCertonaCatalogFeedMarshaller.class);
        suite.addTestSuite(TestCertonaCatalogFeedTools.class);
        suite.addTestSuite(TestShippingMethodManager.class);
//        suite.addTestSuite(com.bbb.integration.BazaarVoice.TestBazaarVoiceManager.class);
//        suite.addTestSuite(com.bbb.integration.BazaarVoice.TestBazaarVoiceUnmarshal.class);
        suite.addTestSuite(TestCollegeCategoryDroplet.class);
        suite.addTestSuite(TestBBBCollegeLinkPipeline.class);

        suite.addTestSuite(TestCollegeCollectionsDroplet.class);
        suite.addTestSuite(TestRegistryManager.class);
        suite.addTestSuite(TestOrderStatusUpdateManager.class);
        suite.addTestSuite(TestNextCollegeCollectionDroplet.class);
        suite.addTestSuite(TestCertonaInventoryFeedMarshaller.class);
		suite.addTestSuite(TestEmailTemplateManager.class);
		suite.addTestSuite(TestStaticTemplateDroplet.class);
		suite.addTestSuite(TestGuidesVideosDroplet.class);
		suite.addTestSuite(TestGuidesLongDescDroplet.class);
		suite.addTestSuite(TestHeaderWhatsNewDroplet.class);
		suite.addTestSuite(TestRegistryTemplateDroplet.class);
		suite.addTestSuite(TestPromotionLookupDroplet.class);
		suite.addTestSuite(TestBridalShowDetailDroplet.class);
		suite.addTestSuite(TestBridalShowStateDroplet.class);
		suite.addTestSuite(TestCmsNavDroplet.class);
		suite.addTestSuite(TestGuidesTemplateDroplet.class);
		suite.addTestSuite(TestHomePageTemplateDroplet.class);
		suite.addTestSuite(TestLandingTemplateDroplet.class);
		suite.addTestSuite(TestPaginationDroplet.class);
		suite.addTestSuite(TestCmsTools.class);
		suite.addTestSuite(TestEmailAPageFormHandler.class);
		suite.addTestSuite(TestBBBPreviewDateFormHandler.class);
		suite.addTestSuite(TestSchoolLookupDroplet.class);
		suite.addTestSuite(TestHomePageProductsDroplet.class);
		suite.addTestSuite(TestCollegeSearchDroplet.class);
		suite.addTestSuite(TestStatesSearchDroplet.class);
		suite.addTestSuite(TestCircularLandingDroplet.class);
		suite.addTestSuite(TestIsProductSKUShippingDroplet.class);

		//New Sapeunit by Rajesh Saini
		suite.addTestSuite(TestBBBGetRootCategoryDroplet.class);
		// Temporarily reverting as its failing all transactions
		suite.addTestSuite(TestBBBSkuPropDetailsDroplet.class);
		suite.addTestSuite(TestBazaarVoiceSchedulerJob.class);
		suite.addTestSuite(TestProductStatusDroplet.class);

		suite.addTestSuite(TestRecommenderLandingPageTemplateDroplet.class);
		suite.addTestSuite(TestRegistrantLandingPageTemplateDroplet.class);
		suite.addTestSuite(TestScheduleAppointmentDroplet.class);
        //$JUnit-END$
        return suite;
    }

}

