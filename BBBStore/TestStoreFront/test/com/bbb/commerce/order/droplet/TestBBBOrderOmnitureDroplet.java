package com.bbb.commerce.order.droplet;

import java.util.HashMap;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.order.vo.OrderOmnitureVO;
import com.bbb.commerce.service.pricing.BBBPricingWSMapper;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bedbathandbeyond.atg.PricingRequestDocument;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBOrderOmnitureDroplet extends BaseTestCase {
	
	
	public void testService() throws Exception {
		DynamoHttpServletRequest pRequest =  getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		BBBOrderOmnitureDroplet orderOmnitureDroplet = (BBBOrderOmnitureDroplet)getObject("bbbOrderOmnitureDroplet");		
		
		PricingRequestDocument wsRequest;
        BBBOrderImpl order = null;
		
		String siteId = (String)getObject("siteId");
		wsRequest = com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse((String) getObject("xmlAsString"));
        
        
		
		Profile profile = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");
		order = (BBBOrderImpl) ((BBBPricingWSMapper) getObject("bbbPriceObject")).transformRequestToOrder(wsRequest.getPricingRequest(), profile, new HashMap<String, Object>());
		order.setSiteId(siteId);
		
		
		
		OrderOmnitureVO omnitureVO = null;
		pRequest.setParameter("order", order);
		try{
			orderOmnitureDroplet.service(pRequest, pResponse);
			omnitureVO = (OrderOmnitureVO)pRequest.getObjectParameter(BBBCoreConstants.OMNITURE_VO);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		if(omnitureVO != null){
			assertNotNull(omnitureVO.getProducts());
			assertEquals("Order Id should be the Purchase ID ", order.getId(), omnitureVO.getPurchaseID());
		}
		
		
	}
}
