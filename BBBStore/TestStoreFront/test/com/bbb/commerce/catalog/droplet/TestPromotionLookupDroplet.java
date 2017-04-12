package com.bbb.commerce.catalog.droplet;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.selfservice.vo.SchoolVO;
import com.sapient.common.tests.BaseTestCase;

/**
 * To test TestPromotionLookupDroplet service method.
 * 
 * @author skuma7
 */

public class TestPromotionLookupDroplet extends BaseTestCase {

	private static final String DROPLET = "bbbPromotionLookupDroplet";
	private static final String PROMOTION_ID = "promotionId";
	private static final String SITE_ID = "siteId";

	/**
	 * To test the service method of SchoolLookupDroplet 
	 * 
	 * @throws Exception
	 */
	public void testServiceForPromotionLookupDroplet() throws Exception{

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		PromotionLookupDroplet promotionLookupDroplet = (PromotionLookupDroplet) getObject(DROPLET);	
		String promotionId = (String) getObject(PROMOTION_ID);
		String siteId = (String) getObject(SITE_ID);
		
		// Setting request parameters
		pRequest.setParameter(BBBCoreConstants.PROMOTION_ID, promotionId);
		pRequest.setParameter(BBBCoreConstants.SITE_ID, siteId);

		// Calling droplet service method		
		promotionLookupDroplet.service(pRequest, pResponse);		
		String promotionDetails = (String)pRequest.getObjectParameter(BBBCoreConstants.PROMTION_DETAILS);	
		
		assertNotNull(promotionDetails);
			
	
	}	
}