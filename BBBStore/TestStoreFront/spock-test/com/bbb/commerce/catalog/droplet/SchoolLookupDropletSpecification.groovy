package com.bbb.commerce.catalog.droplet

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCoreConstants
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.vo.SchoolVO;

import spock.lang.specification.BBBExtendedSpec

class SchoolLookupDropletSpecification extends BBBExtendedSpec {

	SchoolLookupDroplet droplet
	BBBCatalogTools catalogTools
	
	def setup(){
		droplet = Spy()
		catalogTools = Mock()
		droplet.setCatalogTools(catalogTools)
	}
	
	def "service method happy path"(){
		
		given:
			requestMock.getLocalParameter(BBBCoreConstants.SCHOOL_ID) >> "schoolId"
			SchoolVO schoolVO = Mock()
			1*catalogTools.getSchoolDetailsById("schoolId") >> schoolVO
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*droplet.logDebug("starting method SchoolLookupDroplet.service")
			1*droplet.logDebug("Input Paramter SchoolId :schoolId")
			1*requestMock.setParameter(BBBCoreConstants.SCHOOL_VO, schoolVO)
			1*requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock,responseMock)
	}
	
	def "service method with schoolId null and BBBSystemException thrown"(){
		
		given:
			requestMock.getLocalParameter(BBBCoreConstants.SCHOOL_ID) >> null
			1*catalogTools.getSchoolDetailsById(null) >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*droplet.logDebug("starting method SchoolLookupDroplet.service")
			1*droplet.logDebug("School Id is Null")
			1*requestMock.serviceParameter(BBBCoreConstants.EMPTY, requestMock,responseMock)
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "SchoolLookupDroplet.service() | RepositoryException ","catalog_1068"), _)
	}
	
	def "service method with schoolId null and BBBBusinessException thrown"(){
		
		given:
			requestMock.getLocalParameter(BBBCoreConstants.SCHOOL_ID) >> null
			1*catalogTools.getSchoolDetailsById(null) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*droplet.logDebug("starting method SchoolLookupDroplet.service")
			1*droplet.logDebug("School Id is Null")
			1*requestMock.serviceParameter(BBBCoreConstants.EMPTY, requestMock,responseMock)
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "SchoolLookupDroplet.service() | RepositoryException ","catalog_1069"), _)
	}
}
