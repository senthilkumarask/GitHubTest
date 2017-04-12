/**
 * @author mamish
 * @version Id: //com.bbb.commerce.order/TestBBBOrderManager.java.TestBBBOrderManager $$
 * @updated $DateTime: Nov 18, 2011 12:46:29 PM
 */
package com.bbb.account.droplet;

import java.util.Map;

import atg.nucleus.Nucleus;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.droplet.BBBConfigKeysDroplet;
import com.bbb.account.droplet.BBBEncryptionDroplet;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author akhaju
 * 
 */
public class TestBBBEncryptionDroplet extends BaseTestCase {

	public void testEncrypt() throws Exception {

		DynamoHttpServletRequest req = getRequest();
		DynamoHttpServletResponse res = getResponse();
		BBBEncryptionDroplet droplet = (BBBEncryptionDroplet)Nucleus.getGlobalNucleus().resolveName("/com/bbb/account/droplet/BBBEncryptionDroplet");
		String inputStr = (String)getObject("inputString");
		req.setParameter("inputvalue",inputStr );
		req.setParameter("operation","encrypt" );
		droplet.service(req, res);
		assertNotNull(req.getParameter("outputvalue"));
	}
	public void testDecrypt() throws Exception {

		DynamoHttpServletRequest req = getRequest();
		DynamoHttpServletResponse res = getResponse();
		BBBEncryptionDroplet droplet = (BBBEncryptionDroplet)Nucleus.getGlobalNucleus().resolveName("/com/bbb/account/droplet/BBBEncryptionDroplet");
		String inputStr = (String)getObject("inputString");
		req.setParameter("inputvalue",inputStr );
		req.setParameter("operation","encrypt" );
		droplet.service(req, res);
		String encValue=req.getParameter("outputvalue");
		
		req.setParameter("inputvalue",encValue );
		req.setParameter("operation","decrypt" );
		droplet.service(req, res);
		assertNotNull(req.getParameter("outputvalue"));
		assertEquals(inputStr,req.getParameter("outputvalue"));
	}
	public void testInvalid() throws Exception {

		DynamoHttpServletRequest req = getRequest();
		DynamoHttpServletResponse res = getResponse();
		BBBEncryptionDroplet droplet = (BBBEncryptionDroplet)Nucleus.getGlobalNucleus().resolveName("/com/bbb/account/droplet/BBBEncryptionDroplet");
		String inputStr = (String)getObject("inputString");
		req.setParameter("inputvalue",inputStr );
		req.setParameter("operation","invalid" );
		droplet.service(req, res);
		assertNotNull(req.getParameter("outputvalue"));
	}

}
