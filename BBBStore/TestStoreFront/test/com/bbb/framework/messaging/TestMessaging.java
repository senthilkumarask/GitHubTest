package com.bbb.framework.messaging;

import java.util.List;

import javax.naming.InitialContext;

import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.messaging.ValidateMsgReqVO;
import com.bbb.framework.webservices.test.vo.AddressVO;
import com.bbb.framework.webservices.test.vo.ValidateAddressReqVO;
import com.bbb.framework.webservices.test.vo.ValidateAddressResVO;
import com.sapient.common.tests.BaseTestCase;

public class TestMessaging extends BaseTestCase{
	
	public void testMessaging() {
		ValidateMsgReqVO voReq = new ValidateMsgReqVO();
		voReq.setTestMessage((String)getObject("message"));
		voReq.setServiceName((String)getObject("service"));
		try{
			ServiceHandlerUtil.send(voReq);
		}catch(Exception e){
			e.printStackTrace();
			addObjectToAssert("message_received", "could not send message");
		}
		addObjectToAssert("message_received", "This is the test message");
	}
}
