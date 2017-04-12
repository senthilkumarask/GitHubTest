package com.bbb.selfservice.manager;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.selfservice.common.RouteDetails;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.sapient.common.tests.BaseTestCase;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class TestSearchStoreManager extends BaseTestCase{
	public void testGetStoreDirections()throws Exception{
		
		SearchStoreManager searchStoreManager=
			(SearchStoreManager)getObject("SearchStoreManager");
		String startAdress=(String)getObject("startAddress");
		String endAdress=(String)getObject("endAddress");
		String pSiteId = (String) getObject("siteId");
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		RouteDetails routeDetails=searchStoreManager.getStoreDirections(startAdress,
				endAdress, null, false, false, false);
		assertNotNull(routeDetails);
		assertNotNull(routeDetails.getTotalDistance());
		System.out.println(routeDetails+"*"+routeDetails.getTotalDistance());
		
	}
	
public void testModifyLatLngCookie()throws Exception{
		
		SearchStoreManager searchStoreManager=
			(SearchStoreManager)getObject("SearchStoreManager");
		String storeIdFromURL=(String)getObject("storeIdFromURL");
		String pSiteId = (String) getObject("siteId");
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		String latLng=searchStoreManager.modifyLatLngCookie(storeIdFromURL,pSiteId);
		assertNotNull(latLng);
		System.out.println("latLngValue is :"+latLng);
		
	}

public void testGetFavStoreByStoreIdForPLP()throws Exception{
	
	DynamoHttpServletRequest req = getRequest();
	DynamoHttpServletResponse res = getResponse();
	SearchStoreManager searchStoreManager=
			(SearchStoreManager)getObject("SearchStoreManager");
	String favStoreID = (String)getObject("favoriteStoreID");
	String pSiteId = (String) getObject("siteId");
	SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
	try {
		siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
	} catch (SiteContextException siteContextException) {
		throw new BBBSystemException("Exception" + siteContextException);
	}
	searchStoreManager.getFavStoreByStoreIdForPLP(pSiteId,req,res,favStoreID);
	assertNotNull(req.getParameter(BBBCoreConstants.FAVORITE_STORE_DETAILS));
}

}
