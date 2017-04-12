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
public class TestShopTheLookDroplet extends BaseTestCase {
    
    	
    	    	
    	 public void testService() throws Exception{
    		 
    		    DynamoHttpServletRequest pRequest = getRequest();
    	    	DynamoHttpServletResponse pResponse = getResponse();
    	    	ServletUtil.setCurrentRequest(pRequest);
    	    	List<RepositoryItem> kickStarterDataItems =new ArrayList<RepositoryItem>();
    	    	boolean isTransient = true;
    	    	
    	    	String siteId = (String) getObject("site_id");
    	    	getRequest().setParameter("site_id", siteId);
    	    	getRequest().setParameter("registryType", "wedding");
    	    	getRequest().setParameter("isTransient", isTransient);
    	    	
    	    	ShopTheLookDroplet shopTheLookDroplet = (ShopTheLookDroplet) getObject("ShopTheLookDroplet");
    	    	shopTheLookDroplet.service(pRequest, pResponse);
    	    	kickStarterDataItems = (List<RepositoryItem>) pRequest.getObjectParameter("kickStarterDataItemsList");
    	    	assertNotNull(kickStarterDataItems);
    	    	assertTrue(kickStarterDataItems.size()>0);
    			}  	 
    	 } 
    	  	
    	
    	    

