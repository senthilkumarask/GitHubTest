package com.bbb.social;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllSocialTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for Social classes");
		//facebook
		suite.addTestSuite(com.bbb.social.facebook.TestCaseFBConnectFormHandler.class);
		suite.addTestSuite(com.bbb.social.facebook.TestFBUserSiteAssocDroplet.class);
		suite.addTestSuite(com.bbb.social.facebook.api.TestFBResponseParser.class);
		return suite;
	}

}
