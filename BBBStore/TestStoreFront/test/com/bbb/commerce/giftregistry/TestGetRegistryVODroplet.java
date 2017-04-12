package com.bbb.commerce.giftregistry;

import com.bbb.commerce.giftregistry.droplet.GetRegistryVODroplet;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestGetRegistryVODroplet extends BaseTestCase {

	public void testService() throws Exception {
		GetRegistryVODroplet getRegistryVODroplet = (GetRegistryVODroplet) getObject("getRegistryVODroplet");
		String registryId = (String) getObject("registryId2");
		/*getRequest().setParameter("registryId", registryId);
		
		getRegistryVODroplet.service(getRequest(), getResponse());
		String errorMessage = getRequest().getParameter(
				GetRegistryVODroplet.OUTPUT_ERROR_MSG);
		addObjectToAssert("errorMessage", errorMessage);*/

		registryId = (String) getObject("registryId1");
		getRequest().setParameter("registryId", "153587758");
		String siteId = (String) getObject("siteId");
		//getRequest().setParameter("siteId", siteId);
		//getRegistryVODroplet.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
		getRequest().setParameter("siteId","BedBathUS");
		getRegistryVODroplet.service(getRequest(), getResponse());
		RegistryVO registryVO = ((RegistryVO) getRequest().getObjectParameter(
				GetRegistryVODroplet.OUTPUT_REGISTRYVO));
		addObjectToAssert("registryId", registryVO.getRegistryId());
		
	}

}
