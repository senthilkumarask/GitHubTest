package com.bbb.selfservice.droplet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.common.StoreDetailsWrapper;
import com.sapient.common.tests.BaseTestCase;

public class TestMultipleAddressSearchStoreDroplet extends BaseTestCase {

	public void testMultilpleAddressSearchStore() throws BBBBusinessException,
			BBBSystemException, ServletException, IOException {
		
		
	
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		String pSiteId = (String) getObject("siteId");
        SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
        try {
          siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        } catch (SiteContextException siteContextException) {
          throw new BBBSystemException("Exception" + siteContextException);
        }    
        pRequest.setParameter("searchString",
				"40 Fulton Street,2nd Floor,NY,10038");
		pRequest.setParameter("searchType",
				SelfServiceConstants.ADDRESS_BASED_STORE_SEARCH);
		SearchStoreDroplet searchStoreDroplet = (SearchStoreDroplet) getObject("SearchStore");
		String pSearchBasedOn = (String) getObject("searchBasedOn");
		String pRadius = (String) getObject("radius");
		pRequest.setParameter("searchBasedOn",pSearchBasedOn);
		pRequest.setParameter("radius",pRadius);
	    	searchStoreDroplet.service(pRequest, pResponse);
  			StoreDetailsWrapper objStoreDetailsWrapper =(StoreDetailsWrapper) pRequest.getObjectParameter("StoreDetailsWrapper");
  			if(null != objStoreDetailsWrapper.getStoreDetails())
  			{
  				assertTrue(objStoreDetailsWrapper != null
  						&& objStoreDetailsWrapper.getStoreDetails().size() > 0);
  			}
  			else if(null != objStoreDetailsWrapper.getStoreAddressSuggestion())
  			{
  				assertTrue(objStoreDetailsWrapper != null
  						&& objStoreDetailsWrapper.getStoreAddressSuggestion().size() > 0);
  			}
		

	}

}
