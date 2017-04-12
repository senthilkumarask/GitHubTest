package com.bbb.selfservice.droplet

import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.exception.BBBSystemException
import com.bbb.selfservice.manager.ScheduleAppointmentManager
import spock.lang.specification.BBBExtendedSpec

class ScheduleAppointmentDropletSpecification extends BBBExtendedSpec {
	 ScheduleAppointmentDroplet saDroplet
	 ScheduleAppointmentManager saManager = Mock()
	 BBBCatalogToolsImpl cTools = Mock()
	def setup(){
		saDroplet = new ScheduleAppointmentDroplet(scheduleAppointmentManager : saManager, catalogTools : cTools )
	}
	def"service. TC to check the ScheduleAppointment "(){
		given:
             setRequestParameters()			
			1*saManager.canScheduleAppointmentForSiteId("usBed") >> true
			
			1*saManager.isApponitmentTypeValidForStore("fStore1", "appoint1") >> true
		
		when:
			saDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("isScheduleAppointment", true)
			1*requestMock.setParameter("errorOnModal", false)
			1*requestMock.setParameter("directSkedgeMe", true)
			1*requestMock.serviceParameter("output", requestMock, responseMock)

	}
	
	def"service. TC for BBBSystemException while validating  appointment type    "(){
		given:
             setRequestParameters()			
			1*saManager.canScheduleAppointmentForSiteId("usBed") >> true
			
			1*saManager.isApponitmentTypeValidForStore("fStore1", "appoint1") >> {throw new BBBSystemException("exception")}
		
		when:
			saDroplet.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter("isScheduleAppointment", true)
			1*requestMock.setParameter("errorOnModal", true)
			1*requestMock.setParameter("directSkedgeMe", false)
			1*requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"service. TC for check for validation of store type  "(){
		given:
			requestMock.getLocalParameter("favouriteStoreId") >> "fStore1"
			requestMock.getLocalParameter("appointmentType") >> ""
			requestMock.getLocalParameter("siteId") >> "usBed"
			
			1*saManager.canScheduleAppointmentForSiteId("usBed") >> true
			
		
		when:
			saDroplet.service(requestMock, responseMock)
		then:
		    0*saManager.isApponitmentTypeValidForStore("fStore1", _)
			1*requestMock.setParameter("isScheduleAppointment", true)
			1*requestMock.setParameter("errorOnModal", false)
			1*requestMock.setParameter("directSkedgeMe", false)
			1*requestMock.serviceParameter("output", requestMock, responseMock)
	}

	
	def"service. TC when ScheduleAppointment is false  "(){
		given:
             setRequestParameters()			
			1*saManager.canScheduleAppointmentForSiteId("usBed") >> false
			
		
		when:
			saDroplet.service(requestMock, responseMock)
		then:
			0*saManager.isApponitmentTypeValidForStore("fStore1", _)
			1*requestMock.setParameter("isScheduleAppointment", false)
			1*requestMock.setParameter("errorOnModal", false)
			1*requestMock.setParameter("directSkedgeMe", false)
			1*requestMock.serviceParameter("output", requestMock, responseMock)
	}

	private setRequestParameters(){
		requestMock.getLocalParameter("favouriteStoreId") >> "fStore1"
		requestMock.getLocalParameter("appointmentType") >> "appoint1"
		requestMock.getLocalParameter("siteId") >> "usBed"

	}
	
	/************************************** isFavouriteStoreIdValid *************************/
	def "isFavouriteStoreIdValid. Tc When appointmentType is null" (){
		given:
			String favouriteStoreId = "fvs1"
			String appointmentType = null
		when:
		 boolean value = saDroplet.isFavouriteStoreIdValid(favouriteStoreId, appointmentType)
		then:
		 value == false
	}
	
	def "isFavouriteStoreIdValid. Tc When favouriteStoreId is empty" (){
		given:
			String favouriteStoreId = ""
			String appointmentType = "aptype"
		when:
		 boolean value = saDroplet.isFavouriteStoreIdValid(favouriteStoreId, appointmentType)
		then:
		 value == false
	}
	
	def "isFavouriteStoreIdValid. Tc When favouriteStoreId is null" (){
		given:
			String favouriteStoreId = null
			String appointmentType = "aptype"
		when:
		 boolean value = saDroplet.isFavouriteStoreIdValid(favouriteStoreId, appointmentType)
		then:
		 value == false
	}
	
	
	/*********************************************** isAppointmentValid *********************************/
	
	def "isAppointmentValid. Tcto validate the appointmentType" (){
		given:
			String appointmentType = "aptype"
		when:
		 boolean value = saDroplet.isAppointmentValid(appointmentType)
		then:
		 value == true
	}
	
	def "isAppointmentValid. Tc when appointmentType is empty" (){
		given:
			String appointmentType = ""
		when:
		 boolean value = saDroplet.isAppointmentValid(appointmentType)
		then:
		 value == false
	}
	
	def "isAppointmentValid. Tc when appointmentType is null" (){
		given:
			String appointmentType = null
		when:
		 boolean value = saDroplet.isAppointmentValid(appointmentType)
		then:
		 value == false
	}
}
