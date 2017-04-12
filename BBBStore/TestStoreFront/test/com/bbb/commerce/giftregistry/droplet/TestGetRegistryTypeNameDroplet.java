package com.bbb.commerce.giftregistry.droplet;

import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGetRegistryTypeNameDroplet extends BaseTestCase {

	public void testService() throws Exception {
		GetRegistryTypeNameDroplet registryTypeNameDroplet = (GetRegistryTypeNameDroplet) getObject ("getRegistryTypeNameDroplet");
		
		//registryTypeNameDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
		getRequest().setParameter("siteId","BedBathUS");
		getRequest().setParameter("registryTypeCode","BA1");
		registryTypeNameDroplet.service(getRequest(), getResponse());
		
	    String registryType = (String)(getRequest().getObjectParameter("registryTypeName"));

	    assertNotNull(registryType);
		
	}
}
