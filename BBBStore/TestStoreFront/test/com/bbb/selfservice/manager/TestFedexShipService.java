package com.bbb.selfservice.manager;

import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestFedexShipService extends BaseTestCase {

	public void testLabel() throws Exception {
		
		ServletUtil.setCurrentRequest(this.getRequest());
		FedexShipService FShipService = (FedexShipService) getObject("testFedexShipService");		
		//easyFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
		BBBCatalogTools bbCatalogTools = (BBBCatalogTools) getObject("catalogTools");		
		
		String siteId = "BedBathUS";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}		
		final Map<String , String> pMap = new HashedMap();		
		pMap.put(BBBCoreConstants.FIRST_NAME.toLowerCase(),"xxx");
		pMap.put(BBBCoreConstants.LAST_NAME.toLowerCase(), "yyy");
		pMap.put(BBBCoreConstants.CC_COMPANY, "bed bath and beyond");
		pMap.put(BBBCoreConstants.CC_ADDRESS1, "31 parker rd");
		pMap.put(BBBCoreConstants.SHIP_FROM_CITY.toLowerCase(), "edison");
		pMap.put(BBBCoreConstants.CC_STATE, "nj");
		pMap.put(BBBCoreConstants.CC_POSTAL_CODE.toLowerCase(), "08820");		
		pMap.put(BBBCoreConstants.PHONE , "7323586987");
		pMap.put(BBBCoreConstants.EMAIL_ADDRESS, "test1@test.com");
		pMap.put(BBBCoreConstants.NUM_BOXES, String.valueOf(1));
		pMap.put(BBBCoreConstants.WEIGHT1, String.valueOf(BBBCoreConstants.TEN));
		pMap.put(BBBCoreConstants.WEIGHT2, BBBCoreConstants.BLANK);
		pMap.put(BBBCoreConstants.WEIGHT3, BBBCoreConstants.BLANK);
		pMap.put(BBBCoreConstants.SHIPTORMA, "BBB12345678");		
		Map<String , Object> finalResult = FShipService.getLabel(pMap, bbCatalogTools, siteContextManager.getCurrentSiteContext());
		if(finalResult!=null){
			assertTrue(true);
		}else{
			assertTrue(false);
		}
	}
	public void  testSendEmailLabel() throws Exception {

		
		ServletUtil.setCurrentRequest(this.getRequest());
		FedexShipService FShipService = (FedexShipService) getObject("testFedexShipService");		
		//easyFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
		BBBCatalogTools bbCatalogTools = (BBBCatalogTools) getObject("catalogTools");		
		
		String siteId = "BedBathUS";
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}		
		final Map<String , String> pMap = new HashedMap();		
		pMap.put(BBBCoreConstants.FIRST_NAME.toLowerCase(),"xxx");
		pMap.put(BBBCoreConstants.LAST_NAME.toLowerCase(), "yyy");
		pMap.put(BBBCoreConstants.CC_COMPANY, "bed bath and beyond");
		pMap.put(BBBCoreConstants.CC_ADDRESS1, "31 parker rd");
		pMap.put(BBBCoreConstants.SHIP_FROM_CITY.toLowerCase(), "edison");
		pMap.put(BBBCoreConstants.CC_STATE, "nj");
		pMap.put(BBBCoreConstants.CC_POSTAL_CODE.toLowerCase(), "08820");		
		pMap.put(BBBCoreConstants.PHONE , "7323586987");
		pMap.put(BBBCoreConstants.EMAIL_ADDRESS, "test1@test.com");
		pMap.put(BBBCoreConstants.NUM_BOXES, String.valueOf(1));
		pMap.put(BBBCoreConstants.WEIGHT1, String.valueOf(BBBCoreConstants.TEN));
		pMap.put(BBBCoreConstants.WEIGHT2, BBBCoreConstants.BLANK);
		pMap.put(BBBCoreConstants.WEIGHT3, BBBCoreConstants.BLANK);
		pMap.put(BBBCoreConstants.SHIPTORMA, "BBB12345678");		
		Map<String , Object> finalResult = FShipService.sendEmailLabel(pMap, bbCatalogTools, siteContextManager.getCurrentSiteContext());
		if(finalResult!=null){
			assertTrue(true);
		}else{
			assertTrue(false);
		}
	
		
	}
}
