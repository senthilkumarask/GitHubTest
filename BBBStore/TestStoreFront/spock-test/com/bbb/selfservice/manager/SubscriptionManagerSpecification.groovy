package com.bbb.selfservice.manager

import com.bbb.selfservice.tibco.vo.SubscriptionVO;

import spock.lang.specification.BBBExtendedSpec

class SubscriptionManagerSpecification extends BBBExtendedSpec {

	SubscriptionManager mngr = Spy()
	
	def "requestInfoTIBCO method"(){
		
		given:
			SubscriptionVO pSubscriptionVO = new SubscriptionVO()
			mngr.send(pSubscriptionVO) >> null
		
		when:
			mngr.requestInfoTIBCO(pSubscriptionVO)
			
		then:
			1*mngr.logDebug("SubscriptionManager.requestInfoTIBCO() method started")
			1*mngr.logDebug("SubscriptionManager.requestInfoTIBCO() method ends")
	}
}
