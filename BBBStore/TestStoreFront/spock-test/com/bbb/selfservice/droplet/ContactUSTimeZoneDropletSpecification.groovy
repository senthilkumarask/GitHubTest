package com.bbb.selfservice.droplet

import atg.multisite.Site
import atg.multisite.SiteContext
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.selfservice.manager.ContactUsManager
import spock.lang.specification.BBBExtendedSpec

class ContactUSTimeZoneDropletSpecification extends BBBExtendedSpec {
	
	def ContactUSTimeZoneDroplet cutzDroplet
	def SiteContext sContextMock = Mock()
	def ContactUsManager cManager = Mock()
	def Site siteMock = Mock()
	
	def setup(){
		cutzDroplet = new ContactUSTimeZoneDroplet(siteContext : sContextMock, contactUsManager : cManager)
	}
	
	def "service.TC to get the timezone type" (){
		given:
			1*sContextMock.getSite() >> siteMock
			siteMock.getId() >> "usBed"
			1*cManager.getTimeZones("usBed") >> ["GST+5:30", "GST+0"]
		when:
			cutzDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("timeZoneTypes", ["GST+5:30", "GST+0"])
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service.TC for BBBSystemException" (){
		given:
			1*sContextMock.getSite() >> siteMock
			siteMock.getId() >> "usBed"
			1*cManager.getTimeZones("usBed") >> {throw new BBBSystemException("exception")}
		when:
			cutzDroplet.service(requestMock, responseMock)
		then:
			0*requestMock.setParameter("timeZoneTypes", _)
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def "service.TC for BBBBusinessException" (){
		given:
			1*sContextMock.getSite() >> siteMock
			siteMock.getId() >> "usBed"
			1*cManager.getTimeZones("usBed") >> {throw new BBBBusinessException("exception")}
		when:
			cutzDroplet.service(requestMock, responseMock)
		then:
			0*requestMock.setParameter("timeZoneTypes", _)
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}

}
