/**
 * 
 */
package com.bbb.framework.crypto;

import java.sql.Connection;

import atg.nucleus.ServiceException;

import com.bbb.rest.framework.BaseTestCase;

/**
 * @author ksandh
 *
 */
public class TestKeyGeneratorService extends BaseTestCase {

	public TestKeyGeneratorService(String name) {
		super(name);
	}

	/**
	 * Test method for {@link com.bbb.framework.crypto.KeyGeneratorService#generateKey()}.
	 */
	public void testGenerateKey() {
		String encryptionKey = KeyGeneratorService.generateKey();
		assertNotNull(encryptionKey);
	}

	/**
	 * Test method for {@link com.bbb.framework.crypto.KeyGeneratorService#getConnection()}.
	 * @throws ServiceException 
	 */
	public void testGetConnection() throws ServiceException {
		Connection connection = KeyGeneratorService.getConnection((String)getObject("propertiesFileLocation"));
		assertNotNull(connection);
	}
}
