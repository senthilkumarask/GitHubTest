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

import java.util.List;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Sample droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestCanadaStoreLocatorDroplet extends BaseTestCase {
    
    	
    	    	
    	 public void testService() throws Exception{
    		 
    		    DynamoHttpServletRequest pRequest = getRequest();
    	    	DynamoHttpServletResponse pResponse = getResponse();
    	    	
    	    	CanadaStoreLocatorDroplet canadaStoreLocatorDroplet = (CanadaStoreLocatorDroplet) getObject("canadaStoreLocatorDroplet");
    	    	
    	    	
    	    	canadaStoreLocatorDroplet.service(pRequest, pResponse);
    	    	List<StoreVO> canadaStoreLocatorVOs = (List<StoreVO>) pRequest.getObjectParameter("canadaStoreLocator");
    	    	assertNotNull(canadaStoreLocatorVOs);
    	    	assertTrue(canadaStoreLocatorVOs.size()>0);
    	    	
    		   
    			}

    	 
    	 } 
    	  	
    	
    	    

