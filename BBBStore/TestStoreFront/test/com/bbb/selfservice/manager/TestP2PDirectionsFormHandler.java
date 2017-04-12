package com.bbb.selfservice.manager;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.selfservice.common.RouteDetails;
import com.bbb.selfservice.formhandler.P2PDirectionsFormHandler;
import com.sapient.common.tests.BaseTestCase;

public class TestP2PDirectionsFormHandler extends BaseTestCase{
	public void testP2PDirectionsFormHandler()throws Exception{
		
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			String pSiteId = (String)getObject("siteId");
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		ServletUtil.setCurrentRequest(pRequest);

		P2PDirectionsFormHandler p2PDirectionFormHandler = (P2PDirectionsFormHandler) getObject("p2PDirectionsFormHandler");
		p2PDirectionFormHandler.setPostalCode((String)getObject("postalCode"));
		p2PDirectionFormHandler.setStreet((String)getObject("street"));
		p2PDirectionFormHandler.setCity((String)getObject("city"));
		p2PDirectionFormHandler.setState((String)getObject("state"));
		p2PDirectionFormHandler.setRouteStartPoint((String)getObject("routeStartPoint"));
		p2PDirectionFormHandler.setRouteEndPoint((String)getObject("routeEndPoint"));
		p2PDirectionFormHandler.setDestPostalCode((String)getObject("destPostalCode"));
		p2PDirectionFormHandler.setDestStreet((String)getObject("destStreet"));
		p2PDirectionFormHandler.setDestCity((String)getObject("destCity"));
		p2PDirectionFormHandler.setDestState((String)getObject("destState"));
		p2PDirectionFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
		p2PDirectionFormHandler.getFormExceptions().clear();
		assertTrue("should give error in mapquest service calling", p2PDirectionFormHandler.handleP2PDirections(pRequest, pResponse));
		p2PDirectionFormHandler.getFormExceptions().clear();
		assertNotNull("mP2PRouteDetails should not be null", p2PDirectionFormHandler.getP2PRouteDetails());
		p2PDirectionFormHandler.getFormExceptions().clear();
		p2PDirectionFormHandler.setHighways(true);
		p2PDirectionFormHandler.setSeasonalRoad(true);
		p2PDirectionFormHandler.setTollRoad(true);
		assertTrue("should give error in mapquest service calling", p2PDirectionFormHandler.handleExtraParam(pRequest, pResponse));
		p2PDirectionFormHandler.getFormExceptions().clear();
		
		p2PDirectionFormHandler.setPostalCode("d");
		assertTrue("should give error in mapquest service calling", p2PDirectionFormHandler.handleP2PDirections(pRequest, pResponse));
		assertTrue("error map" , p2PDirectionFormHandler.getFormError());
		p2PDirectionFormHandler.getFormExceptions().clear();
		
		p2PDirectionFormHandler.setPostalCode(null);
		assertTrue("should give error in mapquest service calling", p2PDirectionFormHandler.handleP2PDirections(pRequest, pResponse));
		p2PDirectionFormHandler.getFormExceptions().clear();
		
		p2PDirectionFormHandler.setState(null);
		p2PDirectionFormHandler.setPostalCode(null);
		assertTrue("should give error in mapquest service calling", p2PDirectionFormHandler.handleP2PDirections(pRequest, pResponse));
		assertTrue("error map" , p2PDirectionFormHandler.getFormError());
		p2PDirectionFormHandler.getFormExceptions().clear();
		
	}

}
