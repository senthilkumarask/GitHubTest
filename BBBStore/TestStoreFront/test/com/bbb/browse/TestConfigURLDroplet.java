package com.bbb.browse;

import java.io.IOException;
import javax.servlet.ServletException;
import com.bbb.commerce.browse.droplet.ConfigURLDroplet;
import com.sapient.common.tests.BaseTestCase;

public class TestConfigURLDroplet extends BaseTestCase{

	public void testConfigURLDroplet() throws  ServletException, IOException
	{
		ConfigURLDroplet configURLDroplet = (ConfigURLDroplet) getObject("configURLDroplet");	
	
		getRequest().setParameter("configType", "ThirdPartyURLs");
		configURLDroplet.service(getRequest(), getResponse());
	
		String cssPath = (String)getRequest().getObjectParameter("cssPath");
		String jsPath = (String)getRequest().getObjectParameter("jsPath");
		String imagePath = (String)getRequest().getObjectParameter("imagePath");
		
		assertNotNull(cssPath);
		assertNotNull(jsPath);
		assertNotNull(imagePath);
	}
}
