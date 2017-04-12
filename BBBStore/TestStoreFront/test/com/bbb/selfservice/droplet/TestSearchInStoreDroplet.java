package com.bbb.selfservice.droplet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreDetailsWrapper;
import com.sapient.common.tests.BaseTestCase;

public class TestSearchInStoreDroplet extends BaseTestCase {

	public void testSearchInStore() throws BBBBusinessException,
			BBBSystemException, ServletException, IOException {
	
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		pRequest.setParameter("searchBasedOn",
				(String) getObject("searchBasedOn"));
		pRequest.setParameter(
				"searchString",
				",RecordId,N,Address,City,State,Country,Phone,Lat,Lng,postal,hours&pageSize=10&radius=20&origin=99501");
		pRequest.setParameter("searchType",
				SelfServiceConstants.ZIPCODE_BASED_STORE_SEARCH);
		pRequest.setParameter("productId", (String) getObject("productId"));
		pRequest.setParameter("orderedQty", (String) getObject("orderedQty"));
		
		SearchInStoreDroplet bopuseDroplet = (SearchInStoreDroplet) getObject("SearchInStoreDroplet");
		pRequest.setParameter("siteId", (String) getObject("siteId"));
		bopuseDroplet.service(pRequest, pResponse);
		if(getRequest().getObjectParameter("StoreDetailsWrapper")!=null && getRequest().getObjectParameter("productAvailable")!=null)
		{
			StoreDetailsWrapper objStoreDetailsWrapper = (StoreDetailsWrapper) getRequest()
					.getObjectParameter("StoreDetailsWrapper");
			Map<String, Integer> productStatusMap = (Map<String, Integer>) getRequest()
					.getObjectParameter("productAvailable");
	
			// assertFalse(orderList==null);
			assertTrue(objStoreDetailsWrapper != null
					&& objStoreDetailsWrapper.getStoreDetails().size() > 0);
			assertTrue(productStatusMap != null && productStatusMap.size() > 0);
		}

		/*
		 * assertTrue("ERRORS in Logging out ", bbbProfileFormHandler
		 * .getFormExceptions().size() == 0);
		 */
	}

}
