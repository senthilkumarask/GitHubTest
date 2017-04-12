package com.bbb.framework.integration;

import com.bbb.framework.integration.exception.ServiceExceptionConstants.ServiceErrorType;
import com.bbb.framework.integration.exception.ServiceExceptionConstants.ServiceResponseError;
import com.bbb.framework.integration.vo.ResponseErrorVO;
import com.sapient.common.tests.BaseTestCase;

public class TestServiceResponseBase extends BaseTestCase {

	
	public void testServiceResponseBase() throws Exception {
	
		ServiceResponseBase serviceResponseBase = new ServiceResponseBase();
		
		ResponseErrorVO responseErrorVO = new ResponseErrorVO();
		responseErrorVO.setActor("testActor");
		responseErrorVO.setErrorMsg("testError");
		responseErrorVO.setTransId("12345");
		responseErrorVO.setErrorCode(ServiceResponseError.DATA_NOT_FOUND);
		responseErrorVO.setErrorType(ServiceErrorType.DATA);
		responseErrorVO.setErrorFields(new String[]{"errorFiedl1","errorFiedl2","errorField3","errorField4"});
		
		
		serviceResponseBase.setError(responseErrorVO);
		serviceResponseBase.getError();
	}
	
}
