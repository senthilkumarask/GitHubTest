package com.bbb.commerce.giftregistry.droplet

import java.text.ParseException
import java.util.Calendar;
import java.util.Map;

import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import spock.lang.specification.BBBExtendedSpec

class DateCalculationDropletSpecification extends BBBExtendedSpec {

	DateCalculationDroplet droplet
	GiftRegistryManager mGiftRegistryManager = Mock()
	
	def setup(){
		droplet =Spy()
		droplet.setGiftRegistryManager(mGiftRegistryManager)
	}
	
	def "service method happy path event date and current date as same"(){
		
		given:
			requestMock.getParameter("eventDate") >> "15/08/1947"
			requestMock.getParameter("convertDateFlag") >> "true"
			droplet.getCurrentSiteId() >> BBBCoreConstants.SITE_BAB_CA
			Date date = new Date()
			1*mGiftRegistryManager.compareDate("08/15/1947") >> date
			
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("daysToGo", 0)
			1*requestMock.setParameter("daysCheck", "true")
			1*requestMock.setParameter("daysToNextCeleb", 0)
			1*requestMock.setParameter("check", "true")
	}
	
	def "service method with event date < than current date "(){
		
		given:
			requestMock.getParameter("eventDate") >> "15/08/1947"
			requestMock.getParameter("convertDateFlag") >> "true"
			droplet.getCurrentSiteId() >> TBSConstants.SITE_TBS_BAB_CA
			Date date = new Date("08/15/1947")
			mGiftRegistryManager.compareDate("08/15/1947") >> date
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("check", "false")
			1*requestMock.setParameter("daysCheck", "false")
	}
	
	def "service method with event date > than current date "(){
		
		given:
			requestMock.getParameter("eventDate") >> "15/08/2020"
			requestMock.getParameter("convertDateFlag") >> "true"
			droplet.getCurrentSiteId() >> BBBCoreConstants.SITE_BAB_CA
			Date date = new Date("08/15/2020")
			mGiftRegistryManager.compareDate("08/15/2020") >> date
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter("check", "true")
			1*requestMock.setParameter("daysCheck", "true")
	}
	
	def "service method with convertDateFlag as false"(){
		
		given:
			requestMock.getParameter("eventDate") >> "15/08/2020"
			requestMock.getParameter("convertDateFlag") >> "false"
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.serviceLocalParameter("error", requestMock,responseMock)
	}
	
	def "service method with curent site id empty"(){
		
		given:
			requestMock.getParameter("eventDate") >> "15/08/2020"
			requestMock.getParameter("convertDateFlag") >> "true"
			droplet.getCurrentSiteId() >> ""
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.serviceLocalParameter("error", requestMock,responseMock)
	}
	
	def "service method with resultMap as null"(){
		
		given:
			requestMock.getParameter("eventDate") >> "15/08/2020"
			requestMock.getParameter("convertDateFlag") >> "true"
			droplet.getCurrentSiteId() >> BBBCoreConstants.SITE_BAB_CA
			Date date = new Date("08/15/2020")
			mGiftRegistryManager.compareDate("08/15/2020") >> date
			droplet.calculateDaysParameters(_,_) >> null
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.serviceLocalParameter("output", requestMock,responseMock)
	}
	
	def "service method throwing BBBBusinessException"(){
		
		given:
			requestMock.getParameter("eventDate") >> "15/08/2020"
			requestMock.getParameter("convertDateFlag") >> "true"
			droplet.getCurrentSiteId() >> BBBCoreConstants.SITE_BAB_CA
			mGiftRegistryManager.compareDate("08/15/2020") >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Date Calculation BBBBusinessException from service of DateCalculationDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1065),_);
			1*requestMock.setParameter("errorMsg", _);
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def "service method throwing BBBSystemException"(){
		
		given:
			requestMock.getParameter("eventDate") >> "15/08/2020"
			requestMock.getParameter("convertDateFlag") >> "true"
			droplet.getCurrentSiteId() >> BBBCoreConstants.SITE_BAB_CA
			mGiftRegistryManager.compareDate("08/15/2020") >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Date Calculation BBBSystemException from service of DateCalculationDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1066),_);
			1*requestMock.setParameter("errorMsg", _);
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def "service method throwing ParseException"(){
		
		given:
			requestMock.getParameter("eventDate") >> "15/08/2020"
			requestMock.getParameter("convertDateFlag") >> "true"
			droplet.getCurrentSiteId() >> BBBCoreConstants.SITE_BAB_CA
			1*mGiftRegistryManager.compareDate("08/15/2020") >> {throw new ParseException("ParseException is thrown",1067)}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Date Calculation ParseException from service of DateCalculationDroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1067),_);
			1*requestMock.setParameter("errorMsg", _);
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def "calculateDaysParameters method with BBBBusinessException thrown and event as null"(){
		
		given:
			Date date = new Date()
			Calendar calDate = Calendar.getInstance()
			calDate.setTime(date)
		
		when:
			Map<String, Object> map = droplet.calculateDaysParameters(null, calDate)
		
		then:
			BBBBusinessException excep = thrown()
			excep.getMessage().equals("Unable to Calculate difference in Days. The values are eventDate :- null  and currentDate:- "+calDate.toString())
	}
	
	def "calculateDaysParameters method with BBBBusinessException thrown and calDate as null"(){
		
		given:
			Date date = new Date()
			Calendar event = Calendar.getInstance()
			event.setTime(date)
		
		when:
			Map<String, Object> map = droplet.calculateDaysParameters(event, null)
		
		then:
			BBBBusinessException excep = thrown()
			excep.getMessage().equals("Unable to Calculate difference in Days. The values are eventDate :- "+event.toString()+"  and currentDate:- null")
	}
	
	def "daysDifference method with same first and second date"(){
		
		given:
			Date date1 = new Date("08/20/1988")
			Date date2 = new Date("08/20/1988")
			Calendar firstDate = Calendar.getInstance()
			firstDate.setTime(date1)
			Calendar secondDate = Calendar.getInstance()
			secondDate.setTime(date2)
		
		when:
			long diff = droplet.daysDifference(firstDate, secondDate)
		
		then:
			diff == -1
	}
	
	def "daysToGo method with event month > than current month"(){
		
		given:
			Date date1 = new Date("09/20/1777")
			Date date2 = new Date("08/20/1777")
			Calendar event = Calendar.getInstance()
			event.setTime(date1)
			Calendar current = Calendar.getInstance()
			current.setTime(date2)
		
		when:
			long daysToGo = droplet.daysToGo(event, current)
		
		then:
			daysToGo == 31
	}
	
	def "daysToGo method with event month = current month and event day < than current day"(){
		
		given:
			Date date1 = new Date("08/18/1777")
			Date date2 = new Date("08/20/1777")
			Calendar event = Calendar.getInstance()
			event.setTime(date1)
			Calendar current = Calendar.getInstance()
			current.setTime(date2)
		
		when:
			long daysToGo = droplet.daysToGo(event, current)
		
		then:
			daysToGo == 363
	}
	
	def "daysToGo method with event month = current month and event day > than current day"(){
		
		given:
			Date date1 = new Date("08/28/1777")
			Date date2 = new Date("08/20/1777")
			Calendar event = Calendar.getInstance()
			event.setTime(date1)
			Calendar current = Calendar.getInstance()
			current.setTime(date2)
		
		when:
			long daysToGo = droplet.daysToGo(event, current)
		
		then:
			daysToGo == 8
	}
}
