/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestSampleDroplte.java
 *
 *  DESCRIPTION: Test sample droplet
 *
 *  HISTORY:
 *  Oct 14, 2011  Initial version
*/
package com.bbb.kickstarters.droplet;

import java.util.ArrayList;
import java.util.List;

import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.vo.StoreVO;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Sample droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestKickStarterPaginationDroplet extends BaseTestCase {
    
    	
    	    	
    	 public void btestService() throws Exception{
    		 
    		    DynamoHttpServletRequest pRequest = getRequest();
    	    	DynamoHttpServletResponse pResponse = getResponse();
    	    	String nextId= null;
    	    	String previousId = null;
    	    	
    	    	getRequest().setParameter("kickStarterId", "DC2800004");
    	    	getRequest().setParameter("kickStarterType", "Top Consultant");
    	    	
    	    	KickStarterPaginationDroplet kickStarterPaginationDroplet = (KickStarterPaginationDroplet) getObject("KickStarterPaginationDroplet");
    	    	kickStarterPaginationDroplet.service(pRequest, pResponse);
    	    	nextId = (String) pRequest.getObjectParameter("nextId");
    	    	previousId = (String) pRequest.getObjectParameter("previousId");
    	    	assertNotNull(nextId);
    	    	assertNotNull(previousId);
    			}  	 
    	 } 
    	  	
    	
    	    

