package com.bbb.selfservice.droplet

import java.util.List;

import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.selfservice.manager.SurveyManager

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class StateDropletSpecification extends BBBExtendedSpec {

	StateDroplet testObj
	SurveyManager surveyManagerMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	def setup(){
		testObj = new StateDroplet(surveyManager:surveyManagerMock)
	}
	
	def"service method. This TC is the happy flow of service method"(){
		given:
			1 * requestMock.getParameter("NoShowUSTerr") >> "AF,ASF" 
			1 * requestMock.getParameter("showMilitaryStates") >> TRUE
			StateVO stateVOMock = new StateVO()
			StateVO stateVOMock1 = new StateVO()
			1 * surveyManagerMock.getUserLocation(_,TRUE,"AF,ASF") >> [stateVOMock,stateVOMock1]
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("location", [stateVOMock,stateVOMock1])
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service method. This TC is when showMilitaryStates is false and BBBBusinessException thrown in service method"(){
		given:
			testObj = Spy()
			testObj.setSurveyManager(surveyManagerMock)
			requestMock.getParameter("NoShowUSTerr") >> "AF,ASF"
			requestMock.getParameter("showMilitaryStates") >> FALSE
			1 * surveyManagerMock.getUserLocation(_,FALSE,"AF,ASF") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logDebug("StateDroplet.service() method started")
			1 * testObj.logDebug('StateDroplet.service() method ends')
			1 * testObj.logError('account_1210: BBBBusinessException in StateDroplet', _)
	}
	
	def"service method. This TC is when BBBSystemException thrown in service method"(){
		given:
			testObj = Spy()
			testObj.setSurveyManager(surveyManagerMock)
			requestMock.getParameter("NoShowUSTerr") >> "AF,ASF"
			requestMock.getParameter("showMilitaryStates") >> FALSE
			1 * surveyManagerMock.getUserLocation(_,FALSE,"AF,ASF") >> {throw new BBBSystemException("Mock for BBBSystemException")}
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logDebug("StateDroplet.service() method started")
			1 * testObj.logDebug('StateDroplet.service() method ends')
			1 * testObj.logError('account_1211: BBBSystemException in StateDroplet', _)
	}
}

