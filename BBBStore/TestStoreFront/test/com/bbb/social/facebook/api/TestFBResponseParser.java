package com.bbb.social.facebook.api;

import com.bbb.exception.BBBBusinessException;
import com.bbb.social.facebook.vo.UserVO;
import com.sapient.common.tests.BaseTestCase;

public class TestFBResponseParser extends BaseTestCase {
	
	public void testParseFacebookBasicInfo() throws Exception {
		
		FBResponseParserImpl fbResponseParser = (FBResponseParserImpl)getObject("fbResponseParser");
		String fbBasicInfoJson = (String) getObject("fbBasicInfoJson");
		UserVO userVO = fbResponseParser.parseFacebookBasicInfo(fbBasicInfoJson);
		assertNotNull(userVO);
		
	}
	
	public void testParseFacebookErrorInfo() {
		
		boolean error = false;
		FBResponseParserImpl fbResponseParser = (FBResponseParserImpl)getObject("fbResponseParser");
		String fbBasicInfoJson = (String) getObject("fbBasicInfoJson");
		
		try{
			UserVO userVO = fbResponseParser.parseFacebookBasicInfo(fbBasicInfoJson);
		}catch(BBBBusinessException e){
			error = true;
		}
		assertTrue(error);
	}
	
}	

