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

import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.kickstarters.KickStarterVO;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Sample droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestTopSkusDroplet extends BaseTestCase {
    
    	
    	    	
    	 public void testService() throws Exception{
    		 
    		    DynamoHttpServletRequest pRequest = getRequest();
    	    	DynamoHttpServletResponse pResponse = getResponse();
    	    	SKUDetailVO skuDetailVO = null;
    	    	String productId =null;
    	    	
    	    	String siteId = (String) getObject("siteId");
    	    	getRequest().setParameter("siteId", siteId);
    	    	getRequest().setParameter("skuId", "skuId1234");
    	    	
    	    	
    	    	TopSkusDroplet topConsultantDetailsDroplet = (TopSkusDroplet) getObject("TopSkusDroplet");
    	    	topConsultantDetailsDroplet.service(pRequest, pResponse);
    	    	skuDetailVO = (SKUDetailVO) pRequest.getObjectParameter("SKUDetailsVO");
    	    	productId = (String) pRequest.getObjectParameter("productId");
    	    	assertNotNull(skuDetailVO);
    	    	assertNotNull(productId);
    			}  	 
    	 } 
    	  	
    	
    	    

