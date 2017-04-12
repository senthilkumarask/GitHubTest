/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestSearchDroplet.java
 *
 *  DESCRIPTION: Test Search droplet
 *
 *  HISTORY:
 *  Nov 7, 2011  Initial version
*/
package com.bbb.search.handler;

import java.io.IOException;
import java.lang.Exception;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.query.SortCriteria;
import com.bbb.search.bean.result.BBBProductList;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Search droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestNavigationSearchFormHandler extends BaseTestCase {
    public void testService() throws ServletException,IOException {
    	NavigationSearchFormHandler handler = (NavigationSearchFormHandler) getObject("navigationSearchFormHandler");
        handler.setSearchTerm("Sheets");
    	handler.handleSearch(getRequest(), getResponse());
    	assertTrue(handler.getFormExceptions().size() == 0);
    }
}
