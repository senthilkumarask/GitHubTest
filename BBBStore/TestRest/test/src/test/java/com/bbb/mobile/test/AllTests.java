package com.bbb.mobile.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.bbb.mobile.controller.TestGiftRegistryController;
import com.bbb.mobile.controller.TestRecommenderLandingPageController;
import com.bbb.mobile.controller.TestRegistrantEmptyLandingPageController;
import com.bbb.mobile.controller.TestRegistryRecommController;


public class AllTests extends TestSuite {

	public static void main (String[] args) {
		junit.textui.TestRunner.run (suite());
	}
	public static Test suite() {

		TestSuite suite= new TestSuite();
		suite.addTest(new TestGiftRegistryController("testUserLoggedIn"));
		suite.addTest(new TestGiftRegistryController("testUserNotLoggedIn"));
		suite.addTest(new TestGiftRegistryController("testAddRegistrySummaryVORegistryIdExists"));
		suite.addTest(new TestGiftRegistryController("testAddRegistrySummaryVORegistryIdDontExists"));
		suite.addTest(new TestGiftRegistryController("testAddRegSummaryVOListEmpty"));
		suite.addTest(new TestRecommenderLandingPageController("testInvalidTokenUserLoggedIn"));
		suite.addTest(new TestRecommenderLandingPageController("testValidTokenUserNotLoggedIn"));
		suite.addTest(new TestRegistrantEmptyLandingPageController("testRegistrantEmptyPage"));
		suite.addTest(new TestRegistryRecommController("testDisplayRecommendations_setup"));
		suite.addTest(new TestRegistryRecommController("testDisplayPendingRecommendations"));
		suite.addTest(new TestRegistryRecommController("testSetSeoUrlForEachRecom"));
		suite.addTest(new TestRegistryRecommController("testDisplayRecommendersTab"));
		suite.addTest(new TestRegistryRecommController("testDisplayRecommenders_setup"));
		suite.addTest(new TestRegistryRecommController("testDisplayDeclinedRecommendations"));
		suite.addTest(new TestRegistryRecommController("testDisplayAcceptedRecommendations"));
		
		return suite;
	}

}
