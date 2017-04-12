/**
 * @author mamish
 * @version Id: //com.bbb.commerce.order/TestBBBOrderManager.java.TestBBBOrderManager $$
 * @updated $DateTime: Nov 18, 2011 12:46:29 PM
 */
package com.bbb.account;

import com.sapient.common.tests.BaseTestCase;

/**
 * @author akhaju
 * 
 */
public class TestBBBDesEncryptionTools extends BaseTestCase {

	public void testEncrypt() throws Exception {

		BBBDesEncryptionTools encryptionTools = (BBBDesEncryptionTools)getObject("encryptionTools");
		String inputStr = (String)getObject("inputString");
		assertNull(encryptionTools.encrypt(null));
		assertNotNull(encryptionTools.encrypt(inputStr));
		assertNotSame(inputStr, encryptionTools.encrypt(inputStr));
	}
	public void testDecrypt() throws Exception {

		BBBDesEncryptionTools encryptionTools = (BBBDesEncryptionTools)getObject("encryptionTools");
		String inputStr = (String)getObject("inputString");
		assertNull(encryptionTools.decrypt(null));
		String encStr = encryptionTools.encrypt(inputStr);
		assertNotNull(encryptionTools.decrypt(encStr));
		assertEquals(inputStr, encryptionTools.decrypt(encStr));
	}
}
