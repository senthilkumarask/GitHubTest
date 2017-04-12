package com.bbb.selfservice.survey;

import java.util.List;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;

import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.selfservice.droplet.StateDroplet;
import com.sapient.common.tests.BaseTestCase;

public class TestSurveyDroplet extends BaseTestCase{
	
	public void testService() throws Exception
	{
		StateDroplet surveyLocationDroplet  = (StateDroplet) getObject("surveyDroplet");
		
		surveyLocationDroplet.setLoggingDebug(true);
		String pSiteId = (String)  getObject("siteId");
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		surveyLocationDroplet.service(getRequest(), getResponse());
		List<StateVO> states =(List<StateVO>) getRequest().getObjectParameter("location");
		assertTrue(states != null);
//		assertFalse(states == null);
//		
//	    String locationCode = 	(String)  getObject("locationCode");
//	    String locationName = 	(String)  getObject("locationName");
//	    Boolean locationNexusState = 	(Boolean)  getObject("locationNexusState");
//	    
//	    String locationInValue = (String)  getObject("locationInvalid");
//		
//	    StateVO stateVO = new StateVO();
//	    
//	    stateVO.setStateCode(locationCode);
//	    stateVO.setStateName(locationName);
//	    stateVO.setNexusState(locationNexusState);
//	    
//        StateVO stateInVO = new StateVO();
//	    
//        stateInVO.setStateCode(locationCode);
//        stateInVO.setStateName(locationInValue);
//        stateInVO.setNexusState(locationNexusState);
//	    
//	    if(states !=null){
//		 assertTrue(states.contains(stateVO));
//		 assertFalse(states.contains(stateInVO));
//		}
	}
	
	

}
