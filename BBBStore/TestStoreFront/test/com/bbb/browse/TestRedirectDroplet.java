package com.bbb.browse;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.commerce.browse.droplet.BBBRedirectDroplet;
import com.sapient.common.tests.BaseTestCase;

public class TestRedirectDroplet extends BaseTestCase{

	public void testService() throws  ServletException, IOException
	{
		BBBRedirectDroplet redirectDroplet = (BBBRedirectDroplet) getObject("redirectDroplet");	
		String url = (String)getObject("urlToRedirect");
		getRequest().setParameter("url",url);
		redirectDroplet.service(getRequest(), getResponse());
		assertEquals(301, getResponse().getStatus());;
	}	
}
