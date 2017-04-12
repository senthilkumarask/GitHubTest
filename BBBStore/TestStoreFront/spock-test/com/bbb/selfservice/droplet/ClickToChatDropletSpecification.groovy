package com.bbb.selfservice.droplet

import java.text.SimpleDateFormat;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.SiteChatAttributesVO
import com.bbb.exception.BBBSystemException
import com.bbb.selfservice.common.SelfServiceUtil;

import atg.multisite.Site
import atg.multisite.SiteContext;
import spock.lang.specification.BBBExtendedSpec;;

class ClickToChatDropletSpecification extends BBBExtendedSpec{

	
	ClickToChatDroplet droplet
	BBBCatalogTools cTools =Mock()
	SiteContext siteContext =Mock()
	
	def setup(){
		droplet = new ClickToChatDroplet()
		droplet.setCatalogTools(cTools)
		droplet.setSiteContext(siteContext)
	}
	
	def"service , check if Chat is enabled for the Site."(){
		
		given:
		java.util.Date d = new java.util.Date()
		SiteChatAttributesVO siteChatAttributesVO = new SiteChatAttributesVO()
		siteChatAttributesVO.setOnOffFlag(true)
		siteChatAttributesVO.setWeekDayOpenTime(d)
		siteChatAttributesVO.setWeekDayCloseTime(d)
		siteChatAttributesVO.setWeekEndCloseTime(d)
		siteChatAttributesVO.setWeekEndOpenTime(d)
		
		Site site =Mock()
		siteContext.getSite() >> site
		site.getId() >> "siteId"
		1*cTools.getSiteChatAttributes("siteId") >> siteChatAttributesVO
		
		when:
		droplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("chatURL", siteChatAttributesVO.getChatURL());
		1*requestMock.setParameter("onOffFlag", siteChatAttributesVO.isOnOffFlag());
		1*requestMock.setParameter("weekDayOpenTime",new SimpleDateFormat("HH:mm").format(siteChatAttributesVO.getWeekDayOpenTime()));
		1*requestMock.setParameter("weekDayCloseTime",new SimpleDateFormat("HH:mm").format(siteChatAttributesVO.getWeekDayCloseTime()));
		1*requestMock.setParameter("weekEndOpenTime",new SimpleDateFormat("HH:mm").format(siteChatAttributesVO.getWeekEndOpenTime()));
		1*requestMock.setParameter("weekEndCloseTime",new SimpleDateFormat("HH:mm").format(siteChatAttributesVO.getWeekEndCloseTime()));
		1*requestMock.setParameter("chatOpenFlag", false)
		1*requestMock.serviceLocalParameter("output", requestMock, responseMock)	
	}
	
	def"service , check if Chat is disabled for the Site."(){
		
		given:
		java.util.Date d = new java.util.Date()
		SiteChatAttributesVO siteChatAttributesVO = new SiteChatAttributesVO()
		siteChatAttributesVO.setOnOffFlag(false)
		siteChatAttributesVO.setWeekDayOpenTime(d)
		siteChatAttributesVO.setWeekDayCloseTime(d)
		siteChatAttributesVO.setWeekEndCloseTime(d)
		siteChatAttributesVO.setWeekEndOpenTime(d)
		
		Site site =Mock()
		siteContext.getSite() >> site
		site.getId() >> "siteId"
		1*cTools.getSiteChatAttributes("siteId") >> siteChatAttributesVO
		
		when:
		droplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("chatURL", siteChatAttributesVO.getChatURL());
		1*requestMock.setParameter("onOffFlag", siteChatAttributesVO.isOnOffFlag());
		1*requestMock.setParameter("weekDayOpenTime",_);
		1*requestMock.setParameter("weekDayCloseTime",new SimpleDateFormat("HH:mm").format(siteChatAttributesVO.getWeekDayCloseTime()));
		1*requestMock.setParameter("weekEndOpenTime",new SimpleDateFormat("HH:mm").format(siteChatAttributesVO.getWeekEndOpenTime()));
		1*requestMock.setParameter("weekEndCloseTime",new SimpleDateFormat("HH:mm").format(siteChatAttributesVO.getWeekEndCloseTime()));
		1*requestMock.setParameter("chatOpenFlag", true)
		1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"service, check if exception is thrown"(){
		
		given:
		Site site =Mock()
		siteContext.getSite() >> site
		site.getId() >> "siteId"
		
		1*cTools.getSiteChatAttributes("siteId") >> {throw new BBBSystemException("")}
		
		when:
		droplet.service(requestMock, responseMock)
		
		then:
		1*requestMock.setParameter("systemerror", "err_chat_system_error")
		1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
}
