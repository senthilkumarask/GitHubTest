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
package com.bbb.internationalshipping.scheduler;

import java.io.IOException;

import atg.commerce.CommerceException;
import atg.service.idgen.IdGeneratorException;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.internationalshipping.fulfillment.poservice.BBBFileUtils;
import com.bbb.internationalshipping.fulfillment.scheduler.POProcessScheduler;
import com.sapient.common.tests.BaseTestCase;


public class TestPOProcessScheduler extends BaseTestCase {

	
	/*************  TestCreateDSOrderId  
	 * @throws CommerceException 	
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws CommerceException
	 * @throws IdGeneratorException 
	 */
	public void testSchedule()  {
		String filePath = (String) getObject("filePath");
		String archive = (String) getObject("archive");
		String error = (String) getObject("error");
		POProcessScheduler poProcessScheduler = (POProcessScheduler) getObject("poProcessScheduler");
		BBBFileUtils.moveFile(filePath, BBBFileUtils.getFile(error, "2014-11-04-09-45-06-PO-E4X001000370887.xml.asc"));
		poProcessScheduler.executeDoScheduledTask();
		if(!poProcessScheduler.isDisableService()){
            boolean success=BBBFileUtils.moveFile(filePath, BBBFileUtils.getFile(archive, "2014-11-04-09-45-06-PO-E4X001000370887.xml.asc"));
            assertTrue(success);
		}
		BBBFileUtils.moveFile(error, BBBFileUtils.getFile(filePath, "2014-11-04-09-45-06-PO-E4X001000370887.xml.asc"));
		
	}
	

			
}

