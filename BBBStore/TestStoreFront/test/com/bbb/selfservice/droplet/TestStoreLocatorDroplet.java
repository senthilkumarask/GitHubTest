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
package com.bbb.selfservice.droplet;
import java.util.ArrayList;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.common.StoreDetailsWrapper;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Sample droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestStoreLocatorDroplet extends BaseTestCase {
    
    	
    	    	
    	 public void testStoreDetailsByStoreID() throws Exception{
    		    DynamoHttpServletRequest pRequest = getRequest();
    	    	DynamoHttpServletResponse pResponse = getResponse();
    	    	SearchStoreDroplet searchStoreDroplet = (SearchStoreDroplet) getObject("SearchStore");
    	    	String pSiteId = (String) getObject("siteId");
    	        SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
    	        try {
    	          siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
    	        } catch (SiteContextException siteContextException) {
    	          throw new BBBSystemException("Exception" + siteContextException);
    	        }    	    	
    	    	String pStoreId = (String) getObject("storeId");
    	    	pRequest.setParameter("storeId",pStoreId);
    			searchStoreDroplet.service(pRequest, pResponse);
    		    StoreDetails storedetails =(StoreDetails) pRequest.getObjectParameter("StoreDetails");
    		   assertNotNull(storedetails);
    	
    		    //Next Test starts here 
      		  	String pSearchString = (String) getObject("searchString");
      			String pSearchType = (String) getObject("searchType");
      			String pSearchBasedOn = (String) getObject("searchBasedOn");
      	    	pRequest.setParameter("searchString",pSearchString);
      	    	pRequest.setParameter("searchType",pSearchType);
      	    	pRequest.setParameter("searchBasedOn",pSearchBasedOn);
      	    	pRequest.setParameter("storeId","");
      	    	searchStoreDroplet.service(pRequest, pResponse);
      			StoreDetailsWrapper storeDetailsWrapper =(StoreDetailsWrapper) pRequest.getObjectParameter("StoreDetailsWrapper");
      			assertNotNull(storeDetailsWrapper);
      			ArrayList<StoreDetails> storedetails2 = (ArrayList<StoreDetails>) storeDetailsWrapper.getStoreDetails();
      			assertTrue(storedetails2.size()>0);
      	    	 searchStoreDroplet.getRadius();
      	    	 searchStoreDroplet.getStaticMapZoom();
      	    	 searchStoreDroplet.getStaticMapSize();
      	    	 searchStoreDroplet.getStaticMapKey();
      	    	 
      	    	 searchStoreDroplet.setMapQuestTable(null);
      	    	 searchStoreDroplet.getMapQuestTable();
      			}


    	 
    	 
    	 } 
    	  	
    	
    	    

