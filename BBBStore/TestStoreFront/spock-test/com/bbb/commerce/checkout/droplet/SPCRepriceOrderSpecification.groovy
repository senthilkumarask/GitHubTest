package com.bbb.commerce.checkout.droplet

import atg.service.pipeline.PipelineManager
import atg.service.pipeline.RunProcessException
import atg.userprofiling.Profile
import com.bbb.commerce.order.BBBOrderManager
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderImpl

import spock.lang.specification.BBBExtendedSpec

class SPCRepriceOrderSpecification extends BBBExtendedSpec {
	
	def SPCRepriceOrder spcrorderDropletOb
	def BBBOrderImpl orderMock = Mock()
	def Profile profileMock = Mock()
	def BBBOrderManager orderManagerMock = Mock()
	def PipelineManager pipelineManagerMock = Mock()
	
	def setup(){
		spcrorderDropletOb = new SPCRepriceOrder(orderManager:orderManagerMock)
	} 
	
	def"service. TC to reprice the order"(){
		given:
		requestMock.getObjectParameter( "order") >> orderMock
		requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		orderManagerMock.getPipelineManager() >> pipelineManagerMock
		when:
		spcrorderDropletOb.service(requestMock, responseMock)
		
		then:
		1*pipelineManagerMock.runProcess("repriceOrder", _)
		1*requestMock.serviceParameter("output", requestMock, _)
	}

	def"service. TC when it throws RunProcessException while repricing the order  "(){
		given:
		requestMock.getObjectParameter( "order") >> orderMock
		requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		1*orderManagerMock.getPipelineManager() >> {throw new RunProcessException("exception") }
		expect:
		spcrorderDropletOb.service(requestMock, responseMock)
		
	}
	
}
