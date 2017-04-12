package com.bbb.selfservice.droplet

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.PDPAttributesVO;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.selfservice.manager.CanadaStoreLocatorManager;

import spock.lang.specification.BBBExtendedSpec;

class ChatAskAndAnswerDropletSpecification extends BBBExtendedSpec{

	ChatAskAndAnswerDroplet droplet
	BBBCatalogTools cTools =Mock()
	
	def setup(){
		droplet = Spy()
		droplet.setCatalogTools(cTools)
	}
	
	def"service, access all data related to Chat and Ask and Answer for Product Details page."(){
		
		given:
		2*requestMock.getParameter("productId") >> "productId"
		1*requestMock.getParameter("categoryId") >> "cId"
		1*requestMock.getParameter("poc") >> "poc"
		
		droplet.extractSiteID() >> "siteId"
		PDPAttributesVO pdpAttributesVo = new PDPAttributesVO()
		cTools.PDPAttributes("productId", "cId", "poc", "siteId") >> pdpAttributesVo
		
		when:
		droplet.service(requestMock,responseMock)
		
		then:
		1*requestMock.setParameter("PDPAttributesVo", pdpAttributesVo)
		1*requestMock.serviceParameter("output", requestMock, responseMock)
		1*droplet.logDebug("Added Values in the PDP Attributes Vo")
		
	}
	
	def"service, when product id is empty "(){
		
		given:
		1*requestMock.getParameter("productId") >> ""
		0*requestMock.getParameter("categoryId") >> "cId"
		0*requestMock.getParameter("poc") >> "poc"
		
		droplet.extractSiteID() >> "c"
		
		when:
		droplet.service(requestMock,responseMock)
		
		then:
		0*requestMock.setParameter("PDPAttributesVo", null);
		0*requestMock.serviceParameter("output", requestMock, responseMock)
		1*droplet.logDebug("Exiting ChatAskAndAnswerDroplet.service()")
		
	}
	
	def"service , when BBBSystemException is thrown "(){
		
		given:
		2*requestMock.getParameter("productId") >> "productId"
		1*requestMock.getParameter("categoryId") >> "cId"
		1*requestMock.getParameter("poc") >> "poc"
		
		droplet.extractSiteID() >> "siteId"
		PDPAttributesVO pdpAttributesVo = new PDPAttributesVO()
		cTools.PDPAttributes("productId", "cId", "poc", "siteId") >> {throw new BBBSystemException("")}
		
		when:
		droplet.service(requestMock,responseMock)
		
		then:
		0*requestMock.setParameter("PDPAttributesVo", pdpAttributesVo);
		1*requestMock.serviceParameter("error", requestMock, responseMock)
		0*droplet.logDebug("Added Values in the PDP Attributes Vo")
		
	}
	
	def"service , when BBBBusinessException is thrown"(){
		
		given:
		2*requestMock.getParameter("productId") >> "productId"
		1*requestMock.getParameter("categoryId") >> "cId"
		1*requestMock.getParameter("poc") >> "poc"
		
		droplet.extractSiteID() >> "siteId"
		PDPAttributesVO pdpAttributesVo = new PDPAttributesVO()
		cTools.PDPAttributes("productId", "cId", "poc", "siteId") >> {throw new BBBBusinessException("")}
		
		when:
		droplet.service(requestMock,responseMock)
		
		then:
		0*requestMock.setParameter("PDPAttributesVo", pdpAttributesVo);
		1*requestMock.serviceParameter("error", requestMock, responseMock)
		0*droplet.logDebug("Added Values in the PDP Attributes Vo")
		
	}
}
