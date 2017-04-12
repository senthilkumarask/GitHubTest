/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  AllTests.java
 *
 *  DESCRIPTION: Test suite for all packages
 *
 *  HISTORY:
 *  Oct 14, 2011  Initial version
*/
package com;

import org.apache.log4j.xml.DOMConfigurator;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

    public static Test suite() {
        //DOMConfigurator.configure("log4j.xml");
        TestSuite suite = new TestSuite("Test for com");
        //$JUnit-BEGIN$
           suite.addTest(com.bbb.commerce.catalog.TestPIMFeed.suite());
      //     suite.addTest(com.bbb.cms.CMSTests.suite());
        //$JUnit-END$
        return suite;
    }

}
