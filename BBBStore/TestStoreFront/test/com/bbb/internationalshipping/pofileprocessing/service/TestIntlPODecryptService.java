/*
 *  Copyright 2014, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  TestInternationalCheckoutManager.java
 *
 *  DESCRIPTION: Test InternationalCheckoutManager
 *
 *  HISTORY:
 *  Oct 14, 2011 Initial version
 */
package com.bbb.internationalshipping.pofileprocessing.service;

import java.io.File;

import com.bbb.exception.BBBBusinessException;
import com.bbb.internationalshipping.fulfillment.poservice.IntlPODecryptService;
import com.sapient.common.tests.BaseTestCase;


public class TestIntlPODecryptService extends BaseTestCase {

	
	
	public void testUnmarshalPOFile() throws BBBBusinessException 
	{
		IntlPODecryptService intlPoDecrypt= (IntlPODecryptService) getObject("intlPoDecrypt");
		String filePath = (String) getObject("filePath");
		System.out.println("filePath  "+filePath);
		File file= new File(filePath);
		System.out.println("file  "+file);
		System.out.print("Decrypted File : "+intlPoDecrypt.runtimePOFileDecryption(file));
				
			assertNotNull(intlPoDecrypt.runtimePOFileDecryption(file));
		
	}
	
	
}

