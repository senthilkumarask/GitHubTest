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
import atg.userprofiling.Profile;

import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.kickstarters.KickStarterVO;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Sample droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestKickStarterDetailsDroplet extends BaseTestCase {
    
    	
    	    	
    	 public void testService() throws Exception{
    		 
    		    DynamoHttpServletRequest pRequest = getRequest();
    	    	DynamoHttpServletResponse pResponse = getResponse();
    	    	KickStarterVO kickStarterVO = new KickStarterVO();
    	    	Profile profile = (Profile) ServletUtil.getCurrentUserProfile();
    	    	boolean isTransient= profile.isTransient();
    	    	
    	    	String siteId = (String) getObject("siteId");
    	    	getRequest().setParameter("siteId", siteId);
    	    	getRequest().setParameter("consultantId", "DC123456");
    	    	getRequest().setParameter("eventType", "wedding");
    	    	
    	    	
    	    	KickStarterDetailsDroplet kickStarterDetailsDroplet = (KickStarterDetailsDroplet) getObject("KickStarterDetailsDroplet");
    	    	kickStarterDetailsDroplet.service(pRequest, pResponse);
    	    	kickStarterVO = (KickStarterVO) pRequest.getObjectParameter("kickStarterVO");
    	    	assertNotNull(kickStarterVO);
    			}  	 
    	 } 
    	  	
    	
    	    

