package com.bbb.search.droplet;

import static org.junit.Assert.*
import org.junit.Test
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.search.bean.result.SortOptionVO
import com.bbb.search.droplet.BBBDefaultSortOptionsDroplet
import com.bbb.tools.BBBSiteRepositoryTools

import spock.lang.specification.BBBExtendedSpec;

class BBBDefaultSortOptionsDropletSpecification extends BBBExtendedSpec {

	def BBBSiteRepositoryTools siteRepositoryToolsMock = Mock()
	def BBBDefaultSortOptionsDroplet testObj 
	def SortOptionVO sortOptionVOobject
	
	def setup(){
		testObj = new BBBDefaultSortOptionsDroplet(siteRepositoryTools: siteRepositoryToolsMock ) 
	}
	
	def "service: TC to check if  default sort options for the site are set"(){
		
		given:
		siteRepositoryToolsMock.getSortOptionsForSite() >> sortOptionVOobject
		when:
		testObj.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter("sortOptions", sortOptionVOobject)
		1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	def "service: TC to throw BBBSystemException from getting sort options for the site"(){
		
		given:
		testObj=Spy()
		testObj.setSiteRepositoryTools(siteRepositoryToolsMock)
		siteRepositoryToolsMock.getSortOptionsForSite() >> {throw new BBBSystemException("mock BBBSystemException")}
		when:
		testObj.service(requestMock, responseMock)
		then:
		1*testObj.logError(_)
		0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	def "service: TC to throw BBBBusinessException from getting sort options for the site"(){
		
		given:
		testObj=Spy()
		testObj.setSiteRepositoryTools(siteRepositoryToolsMock)
		testObj.setLoggingDebug(true)
		siteRepositoryToolsMock.getSortOptionsForSite() >> {throw new BBBBusinessException("mock BBBBusinessException")}
		when:
		testObj.service(requestMock, responseMock)
		then:
		2*testObj.logError(_,_)
		0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
}
