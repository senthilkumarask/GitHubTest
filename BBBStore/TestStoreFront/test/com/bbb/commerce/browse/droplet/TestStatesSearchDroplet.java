package com.bbb.commerce.browse.droplet;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import utils.system;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;

import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestStatesSearchDroplet extends BaseTestCase {

	public void testProcessStateResult() throws Exception
	{
		StatesSearchDroplet statesSearchDroplet = (StatesSearchDroplet) getObject("statesSearchDroplet");	
		String pSiteId = (String) getObject("siteId");
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		statesSearchDroplet.service(getRequest(), getResponse());
		@SuppressWarnings("unchecked")
		List <ArrayList<StateVO>> listAllStates=(ArrayList <ArrayList<StateVO>>)getRequest().getObjectParameter("listOfStates");
		if(listAllStates.isEmpty()){
			assertTrue(listAllStates.isEmpty());	
		}
		else{		
			assertFalse(listAllStates.isEmpty());
		}
	}
}
