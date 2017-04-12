package com.bbb.browse;



import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.droplet.EverLivingPDPDroplet;
import com.bbb.exception.BBBSystemException;
import com.sapient.common.tests.BaseTestCase;

public class TestEverLivingPDPDroplet extends BaseTestCase{

	public void testEverLivingPDPDropletProduct() throws  ServletException, IOException, BBBSystemException
	{
		EverLivingPDPDroplet everLivingPDPDroplet = (EverLivingPDPDroplet) getObject("everLivingDropletProduct");	
		 DynamoHttpServletRequest pRequest = getRequest();
	     DynamoHttpServletResponse pResponse = getResponse();
	
	     pRequest.setParameter("id","prod60026");
		String pSiteId = (String) getObject("siteId");
		pRequest.setParameter("siteId",pSiteId);
		atg.servlet.ServletUtil.setCurrentRequest(pRequest);
		atg.servlet.ServletUtil.setCurrentResponse(pResponse);
		everLivingPDPDroplet.service(getRequest(), getResponse());
	
		Boolean everLivingProduct = (Boolean)getRequest().getObjectParameter("everLivingProduct");
		
		assertNotNull(everLivingProduct);
	}
}