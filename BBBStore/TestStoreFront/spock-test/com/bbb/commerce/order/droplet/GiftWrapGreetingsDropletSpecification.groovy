package com.bbb.commerce.order.droplet

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import spock.lang.specification.BBBExtendedSpec;

class GiftWrapGreetingsDropletSpecification extends BBBExtendedSpec {
   def GiftWrapGreetingsDroplet gwgDroplet 
   def BBBCatalogTools caToolsMock = Mock()
   def setup(){
	   gwgDroplet = new GiftWrapGreetingsDroplet(BBBCatalogTools:caToolsMock)
   }
   def"service. TC check gift wrap message when site id is not null" (){
	   given:
	   String siteId = "USBed"
	   requestMock.getObjectParameter( "siteId") >> siteId
	   1*caToolsMock.getCommonGreetings(siteId) >> ["gif123": "the toy is for u"]
	   
	   when:
	   gwgDroplet.service(requestMock, responseMock)
	   
	   then:
	   1*requestMock.setParameter("giftWrapMessages",["gif123": "the toy is for u"])
	   1*requestMock.serviceLocalParameter("output",requestMock, responseMock)
	   0*requestMock.serviceLocalParameter("empty",requestMock, responseMock)
	   

   }
   
   def"service. TC check gift wrap message when site id is  null" (){
	   given:
	   String siteId = "USBed"
	   requestMock.getObjectParameter( "siteId") >> null
	   
	   when:
	   gwgDroplet.service(requestMock, responseMock)
	   
	   then:
	   0*requestMock.serviceLocalParameter("output",requestMock, responseMock)
	   1*requestMock.serviceParameter('empty',requestMock, responseMock)
	   

   }
   
   def"service. TC for BBBBusinessException" (){
	   given:
	   String siteId = "USBed"
	   requestMock.getObjectParameter( "siteId") >> siteId
	   1*caToolsMock.getCommonGreetings(siteId) >> {throw new BBBBusinessException("exception")}
	   
	   when:
	   gwgDroplet.service(requestMock, responseMock)
	   
	   then:
	   0*requestMock.setParameter("giftWrapMessages",_)
	   0*requestMock.serviceLocalParameter("output",requestMock, responseMock)
	   

   }
   
   def"service. TC for BBBSystemException" (){
	   given:
	   String siteId = "USBed"
	   requestMock.getObjectParameter( "siteId") >> siteId
	   1*caToolsMock.getCommonGreetings(siteId) >> {throw new BBBSystemException("exception")}
	   
	   when:
	   gwgDroplet.service(requestMock, responseMock)
	   
	   then:
	   0*requestMock.setParameter("giftWrapMessages",_)
	   0*requestMock.serviceLocalParameter("output",requestMock, responseMock)
   }
   
   /********************************************************* getCommonGreeting********************************************************/
   def"getCommonGreeting. TC to get gift wrap message"(){
	   given:
	   gwgDroplet = Spy()
	   1*gwgDroplet.service(requestMock, responseMock) >> {}
	   1* requestMock.getObjectParameter("giftWrapMessages") >> ["gif123": "the toy is for u"]
	   
	   when:
	   Map<String, String> map = gwgDroplet.getCommonGreeting(new HashMap())
	   then:
	   1*requestMock.setParameter("siteId", _)
	   map.get("gif123") == "the toy is for u"
   }
   
   def"getCommonGreeting. TC to get gift wrap message Map is empty "(){
	   given:
	   gwgDroplet = Spy()
	   gwgDroplet.service(requestMock, responseMock) >> {}
	  1* requestMock.getObjectParameter("giftWrapMessages") >> [:]
	   
	   when:
	   Map<String, String> map = gwgDroplet.getCommonGreeting(new HashMap())
	   then:
	   1*requestMock.setParameter("siteId", _)
	   BBBSystemException exception =  thrown() 
   }
   
   def"getCommonGreeting. TC to get gift wrap message Map is null "(){
	   given:
	   gwgDroplet = Spy()
	   gwgDroplet.service(requestMock, responseMock) >> {}
	   1* requestMock.getObjectParameter("giftWrapMessages") >> null
	   
	   when:
	   gwgDroplet.getCommonGreeting(new HashMap())
	   then:
	   1*requestMock.setParameter("siteId", _)
	   BBBSystemException exception =  thrown()
   }
   
   def"getCommonGreeting. TC for IOException "(){
	   given:
	   gwgDroplet = Spy()
	   gwgDroplet.service(requestMock, responseMock) >> {throw new IOException("exception")}
	   
	   when:
	  gwgDroplet.getCommonGreeting(new HashMap())
	   then:
	   1*requestMock.setParameter("siteId", _)
	   BBBSystemException exception =  thrown()
   }
   
}
