package com.bbb.cms.droplet;

import com.bbb.cms.LandingTemplateVO;
import com.sapient.common.tests.BaseTestCase;

public class TestCircularLandingDroplet extends BaseTestCase {
	@SuppressWarnings("unchecked")
	public void testCircular() throws Exception {
    	
		CircularLandingDroplet circularLandingDroplet = (CircularLandingDroplet) getObject("CircularLandingDroplet");
    	String siteId= (String) getObject("siteId");
    	String pageName= (String) getObject("pageName");
    	getRequest().setParameter("siteId", siteId);
    	getRequest().setParameter("pageName", pageName);
    	
    	circularLandingDroplet.service(getRequest(), getResponse());
    	LandingTemplateVO landingTemplateVO =  (LandingTemplateVO) getRequest().getObjectParameter("LandingTemplateVO");
    	assertNotNull(landingTemplateVO);
    	assertNotNull(landingTemplateVO.getCircularListings());
		
    }
	public void testCircularNULL() throws Exception {
    	
		CircularLandingDroplet circularLandingDroplet = (CircularLandingDroplet) getObject("CircularLandingDroplet");
    	String siteId= (String) getObject("siteId");
    	String pageName= (String) getObject("pageName");
    	getRequest().setParameter("siteId", siteId);
    	getRequest().setParameter("pageName", pageName);
    	circularLandingDroplet.service(getRequest(), getResponse());
    	LandingTemplateVO landingTemplateVO =  (LandingTemplateVO) getRequest().getObjectParameter("LandingTemplateVO");
    	assertNotNull(landingTemplateVO);
    	assertNull(landingTemplateVO.getCircularListings());
		
    }
}
