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
package com.bbb.commerce.catalog;





import junit.framework.Test;
import junit.framework.TestSuite;

public class TestPIMFeed {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test Cases for PIM Feed");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestBBBCatalogImportManager.class);
        suite.addTestSuite(TestBBBProdProdAssociation.class);
       
        //$JUnit-END$
        return suite;
    }

}