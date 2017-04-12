package com.bbb.personalstore.targeter;

import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.targeting.TargetingArray;

import com.bbb.targeting.CertonaTargeter;
import com.sapient.common.tests.BaseTestCase;

/**
 * This is a Sapunit to test personal Store Certona Targeter, which contains methods to
 * test the negative test cases for Certona Targeter. As the output is dynamic have not yet included the positive test case.
 * 
 * @author rjain40
 * 
 */

public class TestCertonaTargeter extends BaseTestCase {
	
	 public void testProductRecommsWithError1() throws Exception {
		 
		 CertonaTargeter certonaTargeter = (CertonaTargeter) getObject("TestCertonaTargeter");
		 
		 String siteId = (String) getObject("siteId");
		 
		 getRequest().setParameter("errorMsg",null);
		 getRequest().setParameter("siteId", siteId);
		 getRequest().setParameter("userid", "");
		 getRequest().setParameter("sourceId","BBB0000");
		 getRequest().setParameter("trackingId", "");
		 getRequest().setParameter("number", 15);
		 getRequest().setParameter("targeter", certonaTargeter);
		 TargetingArray targetingArray = (TargetingArray)Nucleus.getGlobalNucleus().resolveName("/atg/targeting/TargetingArray");
		 
		 
		 atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
		 atg.servlet.ServletUtil.setCurrentResponse(this.getResponse());
		 final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		 final DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
		 
		 targetingArray.service(request,response);
		 Object[] arr = (Object[])request.getObjectParameter("elements");
		 assertNull(arr);
		 
		 //for next test case not to fail
		 getRequest().setParameter("certonaResponseVO", null);
	 }
	 
	 public void testProductRecommsWithError2() throws Exception {
		 
		 CertonaTargeter certonaTargeter = (CertonaTargeter) getObject("TestCertonaTargeter");
		 
		 String siteId = (String) getObject("siteId");
		 
		 getRequest().setParameter("errorMsg",null);
		 getRequest().setParameter("siteId", siteId);
		 getRequest().setParameter("userid", "");
		 getRequest().setParameter("sourceId","BBB1000");
		 getRequest().setParameter("trackingId", "");
		 getRequest().setParameter("number", 15);
		 getRequest().setParameter("targeter", certonaTargeter);
		 TargetingArray targetingArray = (TargetingArray)Nucleus.getGlobalNucleus().resolveName("/atg/targeting/TargetingArray");
		 
		 
		 atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
		 atg.servlet.ServletUtil.setCurrentResponse(this.getResponse());
		 final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		 final DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
		 
		 targetingArray.service(request,response);
		 Object[] arr = (Object[])request.getObjectParameter("elements");
		 assertNull(arr);
		 
		 //for next test case not to fail
		 getRequest().setParameter("certonaResponseVO", null);
	 }
}
