package com.bbb.framework.encryption;

import java.security.InvalidAlgorithmParameterException;

import com.bbb.framework.crypto.AESEncryptor;
import com.bbb.framework.crypto.EncryptorException;

import junit.framework.TestCase;

public class TestEncryption extends TestCase {
	
	public TestEncryption() {
		super ("testEncryptDecrypt");
	}
	public void testEncryptDecrypt() {
		try {
			AESEncryptor aesEncrypter = new AESEncryptor("Dz95mH1tOySrMlGLhUaICQ==");
			String testData = "Hello";
			String encryptedVal = aesEncrypter.encrypt(testData);
			System.out.println(encryptedVal);
			assertNotSame(testData, encryptedVal);
			String decryptedVal = aesEncrypter.decrypt(encryptedVal);
			System.out.println(decryptedVal);
			assertEquals(testData, decryptedVal);
		} catch (EncryptorException e) {
			assert(false);
		} 
		
		
		try {
			System.out.println("check for Exception");
			throw new EncryptorException();
		} catch (EncryptorException e) {
			assert(true);
		}

		try {
			System.out.println("check for Exception single ");
			throw new EncryptorException("Exception Message");
		} catch (EncryptorException e) {
			assert(true);
		}
		
		try {
			System.out.println("check for Exception throwable");
			throw new EncryptorException(new Throwable());
		} catch (EncryptorException e) {
			assert(true);

		}
		try {
			System.out.println("check for Exception Message with throwable");
			throw new EncryptorException("Exception Message",new Throwable());
		} catch (EncryptorException e) {
			assert(true);

		}

	}

}
