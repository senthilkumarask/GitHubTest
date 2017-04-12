/**
 * @author akhaju
 * @version Id: //com.bbb.commerce.order/TestBBBOrderManager.java.TestBBBOrderManager $$
 * @updated $DateTime: Nov 18, 2011 12:46:29 PM
 */

 
 
 
 
 
package com.bbb.account.droplet;

import java.util.Map;

import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.droplet.BBBConfigKeysDroplet;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author manish
 *
 */
public class TestBBBConfigKeysDroplet   extends BaseTestCase{
	
	public void testConfigKeysDroplet() throws Exception {		
		
		
		DynamoHttpServletRequest req = getRequest();
		BBBConfigKeysDroplet droplet = (BBBConfigKeysDroplet)Nucleus.getGlobalNucleus().resolveName("/com/bbb/account/droplet/BBBConfigKeysDroplet");
		DynamoHttpServletResponse res = getResponse();
		String configKey = (String)getObject("configKey");
		req.setParameter("configKey",configKey);
		droplet.service(req, res);
		Object obj = req.getObjectParameter("configMap");		
		assertNotNull(obj);
		assertTrue(obj instanceof Map);
		assertTrue(((Map)obj).size()>0);
	}
}
