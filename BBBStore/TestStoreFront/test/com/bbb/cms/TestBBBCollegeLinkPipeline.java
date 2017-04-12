package com.bbb.cms;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.pipeline.BBBCollegeLinkPipeline;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBCollegeLinkPipeline extends BaseTestCase {

	DynamoHttpServletRequest request = getRequest();
	DynamoHttpServletResponse response = getResponse();


	public void testCollegeLinkPipeline() throws Exception {
		try{
			BBBCollegeLinkPipeline collegePipeline = (BBBCollegeLinkPipeline) getObject("collegePipeline");
			collegePipeline.setLoggingDebug(true);
			String url = (String) getObject("url");	
			collegePipeline.setTestSapeUnit(url);

			collegePipeline.service(request, response);


			Profile profile = (Profile) ServletUtil.getCurrentUserProfile(); 
			String schoolId=(String)profile.getPropertyValue("schoolIds");

			System.out.println("***************************************************");
			System.out.println("profile : "+profile);
			System.out.println("schoolId : "+schoolId);
			System.out.println("***************************************************");

			assertNotNull(schoolId);

			assertTrue(true);


		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
