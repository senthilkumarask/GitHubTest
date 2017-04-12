package com.bbb.cms.droplet;


import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;



import com.bbb.cms.RecommendationLandingPageTemplateVO;
import com.sapient.common.tests.BaseTestCase;

public class TestRegistrantLandingPageTemplateDroplet extends BaseTestCase{
public void testRegistrantLandingPageTemplateDroplet() throws Exception {
	DynamoHttpServletRequest pRequest = getRequest();
	DynamoHttpServletResponse pResponse = getResponse();
		RecommendationLandingPageTemplateDroplet objRecommendationLandingPageTemplateDroplet = (RecommendationLandingPageTemplateDroplet) getObject("registrantLandingPageTemplateDroplet");
    	String siteId= (String) this.getObject("siteId");
    	pRequest.setParameter("siteId", siteId);
    	String channel= (String) getObject("channel");
    	pRequest.setParameter("channel", channel);
    	String registryType= (String) getObject("registryType");
    	pRequest.setParameter("registryType", registryType);
    	String registryURL= "/store/_includes/modals/inviteFriendsRegistry.jsp?eventType=Wedding&registryId=203521845";
    	pRequest.setParameter("registryURL", registryURL);    	
    	objRecommendationLandingPageTemplateDroplet.service(pRequest, pResponse);
    	RecommendationLandingPageTemplateVO registrantLandingPageTemplateVO =  (RecommendationLandingPageTemplateVO)pRequest.getObjectParameter("RecommendationLandingPageTemplateVO");
		System.out.println(registrantLandingPageTemplateVO.toString());
    	assertNotNull(registrantLandingPageTemplateVO);
    	assertNotNull(registrantLandingPageTemplateVO.getPromoBox());
    	assertNotNull(registrantLandingPageTemplateVO.getPromoBoxList());
		
    }
}
