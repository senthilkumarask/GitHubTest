package com.bbb.commerce.giftregistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.simplifyRegistry.RegistryInputVO;
import com.bbb.simplifyRegistry.RegistryInputsByTypeVO;
import com.bbb.simplifyRegistry.droplet.SimpleRegFieldsDroplet;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

import atg.multisite.SiteContextManager;

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
public class TestSimpleRegFieldsDroplet extends BaseTestCase {
	
	/**
	 * Testing RegistryTypes
	 * @throws Exception
	 */
	
	public static final String UNIVERSITY="University";
	public static final String COLLEGE_UNIVERSITY="College/University"; 
	
	public void testSimpleRegFieldsDropletService() throws Exception {
		SimpleRegFieldsDroplet testSimpleRegFieldsDroplet = (SimpleRegFieldsDroplet) getObject("testSimpleRegFieldsDroplet");
		String siteId=(String) getObject("siteId");
		getRequest().setParameter("siteId",siteId);
		String eventType=(String) getObject("eventType");
		
		if((BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId)||BBBCoreConstants.SITEBAB_CA_TBS.equalsIgnoreCase(siteId)) && UNIVERSITY.equalsIgnoreCase(eventType) ){
			eventType=COLLEGE_UNIVERSITY;
		}
		getRequest().setParameter("eventType",eventType);
		
		RegistryInputsByTypeVO registryInputsByTypeVO = new RegistryInputsByTypeVO();
		List<RegistryInputVO> registryInputList = (List) new ArrayList<RegistryInputVO>();
		
		testSimpleRegFieldsDroplet.service(getRequest(), getResponse());
		Map<String, Map<String, String>> inputListMap=(Map<String, Map<String, String>>)getRequest().getObjectParameter("inputListMap");
		registryInputsByTypeVO=(RegistryInputsByTypeVO)getRequest().getObjectParameter("registryInputsByTypeVO");
		 registryInputList=(List<RegistryInputVO>) getRequest().getObjectParameter("registryInputList");		
		String   actualValueAfterProcess="";
        	if (registryInputsByTypeVO != null)
        	{
        		actualValueAfterProcess	=registryInputsByTypeVO.getEventType();
        	}
        addObjectToAssert("registryType", actualValueAfterProcess);
    }
}
