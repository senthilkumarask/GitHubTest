package com.bbb.commerce.giftregistry;
import java.util.List;

import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.giftregistry.droplet.GiftRegistryTypesDroplet;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

/**
 * This class provides test case for handling methods for operating on the
 * current customer's gift registries. It can be used to create new gift
 * registries, edit gift registries and add items to the gift registry during
 * the browse and/or shopping process. Gift registry management is the core
 * functionality of this form handler. It controls creating, updating and item
 * management to all of the customer's gift registries. This includes creating,
 * updating, publishing, and deleting gift registries as well as adding items to
 * the registries. This functionality is invoked via the various handleXXX
 * methods of the form handler.
 * 
 * @author sku134
 * 
 */
public class TestGiftRegistryTypesDroplet extends BaseTestCase {
	
	/**
	 * Testing RegistryTypes
	 * @throws Exception
	 */
	public void testRegistryTypeDropletService() throws Exception {
		GiftRegistryTypesDroplet testGiftRegistryTypesDroplet = (GiftRegistryTypesDroplet) getObject("testGiftRegistryTypesDroplet");
		String siteId=(String) getObject("siteId");
		//getRequest().setParameter("siteId",siteId);
		//testGiftRegistryTypesDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		getRequest().setParameter("siteId",siteId);
		testGiftRegistryTypesDroplet.service(getRequest(), getResponse());
		List<RegistryTypeVO> registryTypesList=(List<RegistryTypeVO>)getRequest().getObjectParameter("registryTypes");
        String   actualValueAfterProcess="";
        for (RegistryTypeVO iterateRegistry:registryTypesList){
        	if (iterateRegistry.getRegistryName().equalsIgnoreCase("Birthday"))
        	{
        		actualValueAfterProcess	=iterateRegistry.getRegistryName();
        	}
        }
        addObjectToAssert("registryType", actualValueAfterProcess);
        
        
        String siteId1=(String) getObject("siteId1");
		//getRequest().setParameter("siteId",siteId1);
        //testGiftRegistryTypesDroplet.setSiteContext(BBBSiteContext.getBBBSiteContext(siteId1));
        getRequest().setParameter("siteId",siteId1);
		testGiftRegistryTypesDroplet.service(getRequest(), getResponse());
		List<RegistryTypeVO> registryTypesList1=(List<RegistryTypeVO>)getRequest().getObjectParameter("registryTypes");
		
		String errorMessage = getRequest().getParameter(
				GiftRegistryTypesDroplet.OUTPUT_ERROR_MSG);
		addObjectToAssert("errorMessage", errorMessage);
    }
}
