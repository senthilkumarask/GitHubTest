package com.bbb.account;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllAccountTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for Account/Self service/Social classes");
		//$JUnit-BEGIN$
		
		//profile
		suite.addTestSuite(TestBBBPreferences.class);
		suite.addTestSuite(TestBBBProfileFormHandler.class);
		suite.addTestSuite(TestBBBAddressAPI.class);
		
		
		//suite.addTestSuite(TestStoreGiftlistFormHandler.class);
		suite.addTestSuite(com.bbb.selfservice.droplet.TestSearchStoreFormHandler.class);
		suite.addTestSuite(TestBBBStoreSessionDroplet.class);
		//suite.addTestSuite(TestBBBReclaimLegacyAccount.class);
		
		suite.addTestSuite(TestBBBForgotPasswordFormHandler.class);
		suite.addTestSuite(com.bbb.selfservice.manager.TestP2PDirectionsFormHandler.class);
		
		suite.addTestSuite(TestBBBProfileTools.class);
		suite.addTestSuite(TestBBBSharedProfile.class);
		suite.addTestSuite(TestBBBFavStore.class);
		
		suite.addTestSuite(TestValidateOldAccount.class);
		suite.addTestSuite(TestBBBOrderTrackingManager.class);
		suite.addTestSuite(TestLegacyOrderDetailsDroplet.class);
		suite.addTestSuite(com.bbb.integration.order.TestGetOrderHistoryDroplet.class);
		suite.addTestSuite(com.bbb.integration.order.TestGetOrderHistoryManager.class);
		suite.addTestSuite(com.bbb.account.TestBBBOrderTrackingFormHandler.class);
		
//		//self service
		suite.addTestSuite(com.bbb.selfservice.common.TestSelfServiceUtil.class);
		suite.addTestSuite(com.bbb.selfservice.contactus.TestSelfServiceDroplet.class);
		suite.addTestSuite(com.bbb.selfservice.droplet.TestClickToChatDroplet.class);
		suite.addTestSuite(TestBBBProfileFormHandlerBV.class);
		suite.addTestSuite(com.bbb.selfservice.droplet.TestGetCouponsDroplet.class);
		suite.addTestSuite(com.bbb.account.TestBBBCouponManager.class);
		suite.addTestSuite(com.bbb.selfservice.droplet.TestCanadaStoreLocatorDroplet.class);
		suite.addTestSuite(com.bbb.selfservice.survey.TestSurveyDroplet.class);
		suite.addTestSuite(com.bbb.selfservice.droplet.TestStoreLocatorDroplet.class);
		suite.addTestSuite(com.bbb.selfservice.droplet.TestSearchInStoreDroplet.class);	
		suite.addTestSuite(com.bbb.selfservice.droplet.TestListPriceSalePriceDroplet.class);	
		suite.addTestSuite(com.bbb.selfservice.droplet.TestMultipleAddressSearchStoreDroplet.class);
		suite.addTestSuite(com.bbb.selfservice.manager.TestSearchStoreManager.class);
		suite.addTestSuite(com.bbb.certona.marshaller.TestCertonaProfileFeedMarshaller.class);
		suite.addTestSuite(com.bbb.account.webservices.TestBBBProfileServices.class);
		suite.addTestSuite(com.bbb.account.droplet.TestBBBConfigKeysDroplet.class);
		suite.addTestSuite(com.bbb.account.droplet.TestBBBEncryptionDroplet.class);
		suite.addTestSuite(com.bbb.account.TestBBBDesEncryptionTools.class);
		suite.addTestSuite(com.bbb.account.droplet.TestChangeAncorTagURLDroplet.class);
		
		//profile sync with gift registry
		suite.addTestSuite(com.bbb.account.TestProfileSync.class);
		
		suite.addTestSuite(com.bbb.account.profile.TestFeedFileParser.class);
		suite.addTestSuite(com.bbb.account.profile.TestProfileFeedTools.class);
		//suite.addTestSuite(com.bbb.selfservice.formhandler.TestSubscriptionFormHandler.class);
		suite.addTestSuite(com.bbb.social.facebook.TestCaseFBConnectFormHandler.class);
		suite.addTestSuite(com.bbb.social.facebook.TestFBUserSiteAssocDroplet.class);
		suite.addTestSuite(com.bbb.social.facebook.api.TestFBResponseParser.class);
		
		//New Sapeunit fixed By Rajesh Saini
		suite.addTestSuite(com.bbb.account.service.profile.TestProfileServiceImpl.class);

		suite.addTestSuite(com.bbb.selfservice.formhandler.TestBridalBookFormHandler.class);
		suite.addTestSuite(com.bbb.selfservice.formhandler.TestBabyBookFormHandler.class);
		suite.addTestSuite(com.bbb.selfservice.formhandler.TestSurveyFormHandler.class);

		/*Commented Temporarily
		 	suite.addTestSuite(com.bbb.selfservice.contactus.TestContactUsFormHandler.class);
		*/
		suite.addTestSuite(com.bbb.framework.integration.TestServiceResponseBase.class);
	    suite.addTestSuite(com.bbb.session.TestBBBMultiSiteComponentSessionmanager.class);
		suite.addTestSuite(com.bbb.account.droplet.TestBBBURLEncodingDroplet.class);
		suite.addTestSuite(com.bbb.account.order.droplet.TestMapToArrayDefaultFirst.class);
		suite.addTestSuite(com.bbb.account.order.droplet.TestOrderSummaryDetails.class);
		suite.addTestSuite(com.bbb.account.order.droplet.TestTrackingInfoDroplet.class);
		//suite.addTestSuite(com.bbb.selfservice.formhandler.TestHWRegistrationFormHandler.class);
		suite.addTestSuite(com.bbb.utils.TestBBBLogBuildNumber.class);
		suite.addTestSuite(com.bbb.selfservice.droplet.TestGetCollegeProductDroplet.class);
		suite.addTestSuite(com.bbb.selfservice.tools.TestMapQuestService.class);
		suite.addTestSuite(com.bbb.framework.encryption.TestEncryption.class);
		
		//New Sape unit for Store repository cache invalidation scheduler.
		suite.addTestSuite(com.bbb.store.scheduler.TestInvalidateStoreRepositoryCacheScheduler.class);
		
		//New sape unit for Redirect droplet.
		suite.addTestSuite(com.bbb.browse.TestRedirectDroplet.class);
		
		//new sape units added for product comparison page. R2.2 178-A4
		suite.addTestSuite(com.bbb.commerce.browse.droplet.compare.TestBBBProductComparisonDroplet.class);
		suite.addTestSuite(com.bbb.commerce.catalog.comparison.TestBBBCompareProductHandler.class);
		//$JUnit-END$
		
//		Kick Starter JUNITS
	/*	suite.addTestSuite(com.bbb.kickstarters.droplet.TestKickStarterDetailsDroplet.class);
		suite.addTestSuite(com.bbb.kickstarters.droplet.TestKickStarterPaginationDroplet.class);
		suite.addTestSuite(com.bbb.kickstarters.droplet.TestShopTheLookDroplet.class);
		suite.addTestSuite(com.bbb.kickstarters.droplet.TestTopConsultantsDroplet.class);
		suite.addTestSuite(com.bbb.kickstarters.droplet.TestTopSkusDroplet.class);
		
		suite.addTestSuite(com.bbb.kickstarters.manager.TestKickStarterManager.class);
		suite.addTestSuite(com.bbb.kickstarters.manager.TestKickStarterManager1.class);
		
		suite.addTestSuite(com.bbb.kickstarters.tools.TestKickStarterTools.class);*/
		
		return suite;
	}

}
