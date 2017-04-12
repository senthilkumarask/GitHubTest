package com.bbb.framework.messaging;

import com.bbb.framework.integration.ServiceRequestBase;

public class ValidateMsgReqVO extends ServiceRequestBase{
	
	String serviceName;
	
	String testMessage;
	
	public String getTestMessage() {
		return testMessage;
	}


	public void setTestMessage(String testMessage) {
		this.testMessage = testMessage;
	}


	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}
	
}
