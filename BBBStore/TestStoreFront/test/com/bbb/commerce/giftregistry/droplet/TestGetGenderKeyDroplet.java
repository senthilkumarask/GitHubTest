package com.bbb.commerce.giftregistry.droplet;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.sapient.common.tests.BaseTestCase;

public class TestGetGenderKeyDroplet extends BaseTestCase {

	public void testService() throws Exception {
		GetGenderKeyDroplet genderKeyDroplet = (GetGenderKeyDroplet) getObject("genderKeyDroplet");
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		String genderKey = (String) getObject("genderKey");
		String inverseflag = (String) getObject("inverseflag");
		
		pRequest.setParameter("genderKey", genderKey);
		pRequest.setParameter("inverseflag", inverseflag);
		genderKeyDroplet.service(pRequest, pResponse);
		assertEquals(pRequest.getParameter("genderCode"),"It's a boy!");
	}
	public void testServiceInvalid() throws Exception {
		GetGenderKeyDroplet genderKeyDroplet = (GetGenderKeyDroplet) getObject("genderKeyDroplet");
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		String genderKey = (String) getObject("genderKey");
		
		pRequest.setParameter("genderKey", genderKey);
		pRequest.setParameter("inverseflag", null);
		genderKeyDroplet.service(pRequest, pResponse);
		assertEquals(pRequest.getParameter("genderCode"),"B");
	}
	public void testServiceInvalid1() throws Exception {
		GetGenderKeyDroplet genderKeyDroplet = (GetGenderKeyDroplet) getObject("genderKeyDroplet");
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		pRequest.setParameter("genderKey", null);
		pRequest.setParameter("inverseflag", null);
		genderKeyDroplet.service(pRequest, pResponse);
		assertEquals(pRequest.getParameter("errorMsg"),"genderKy is null");
	}
	

}
