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
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

/**
 *  Test Sample droplet
 *
 *  @author Sapient Corporation
 *
 */
public class TestClickToChatDroplet extends BaseTestCase {
    
    public void testService() throws Exception {
    	
    	DynamoHttpServletRequest pRequest = getRequest();
    	DynamoHttpServletResponse pResponse = getResponse();
    	
		String pSiteId = (String) getObject("siteId");
		ClickToChatDroplet c2cDroplet = (ClickToChatDroplet) getObject("clickToChatDroplet");
		c2cDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		c2cDroplet.service(pRequest, pResponse);

		addObjectToAssert("chatURL", pRequest.getParameter("chatURL"));
		addObjectToAssert("onOffFlag", pRequest.getParameter("onOffFlag"));
		addObjectToAssert("weekDayOpenTime", pRequest.getParameter("weekDayOpenTime"));
		addObjectToAssert("weekDayCloseTime", pRequest.getParameter("weekDayCloseTime"));
		addObjectToAssert("weekEndOpenTime", pRequest.getParameter("weekEndOpenTime"));
		addObjectToAssert("weekEndCloseTime", pRequest.getParameter("weekEndCloseTime"));
		
    }
    
}
