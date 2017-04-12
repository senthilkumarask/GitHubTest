package com.bbb.commerce.giftregistry.droplet

import java.util.HashMap;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.RegistryTypes
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.commerce.giftregistry.vo.ShippingVO
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;

import spock.lang.specification.BBBExtendedSpec

class GetRegistryVODropletSpecification extends BBBExtendedSpec {
	
	GetRegistryVODroplet droplet
	BBBCatalogTools catalogTools
	GiftRegistryManager registryManager
	
	def setup(){
		droplet = new GetRegistryVODroplet()
		catalogTools = Mock()
		registryManager = Mock()
		droplet.setCatalogTools(catalogTools)
		droplet.setGiftRegistryManager(registryManager)
	}
	
	def "service method happy path"(){
		given:
		BBBSessionBean sessionBean = Mock()
		RegistryVO registryVO = new RegistryVO()
		ShippingVO shippingVO = new ShippingVO()
		requestMock.getParameter("registryId") >> "regId"
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("isRegTypeNameReq") >> true
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		1*registryManager.getRegistryDetailInfo(_,_) >> registryVO
		registryVO.setShipping(shippingVO)
		shippingVO.setFutureShippingDate("20170126")
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("registryVO", registryVO)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			registryVO.getOptInWeddingOrBump() == "false"
			1*requestMock.setParameter("registryTypeName", null)
	}
	
	def "service method with isRegistryTypeNameRequired null"(){
		given:
		BBBSessionBean sessionBean = Mock()
		RegistryVO registryVO = new RegistryVO()
		ShippingVO shippingVO = new ShippingVO()
		Map<String, RegistryVO> registryVOs = new HashMap<String,RegistryVO>()
		requestMock.getParameter("registryId") >> "regId"
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getRegistryVOs() >> registryVOs
		1*registryManager.getRegistryDetailInfo(_,_) >> registryVO
		registryVO.setAffiliateOptIn("Y")
		registryVO.setShipping(shippingVO)
		shippingVO.setFutureShippingDate("20170126")
		sessionBean.getRegistryTypesEvent() >> "event"
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("registryVO", registryVO)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			registryVO.getOptInWeddingOrBump() == "true"
			0*requestMock.setParameter("registryTypeName", null)
	}
	
	def "service method with isRegistryTypeNameRequired not null"(){
		given:
		droplet = Spy()
		droplet.setCatalogTools(catalogTools)
		droplet.setGiftRegistryManager(registryManager)
		BBBSessionBean sessionBean = Mock()
		RegistryVO registryVO = new RegistryVO()
		ShippingVO shippingVO = new ShippingVO()
		RegistryTypes type = Mock()
		Map<String, RegistryVO> registryVOs = new HashMap<String,RegistryVO>()
		requestMock.getParameter("registryId") >> "regId"
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("isRegTypeNameReq") >> true
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getRegistryVOs() >> registryVOs
		1*registryManager.getRegistryDetailInfo(_,_) >> registryVO
		registryVO.setAffiliateOptIn("N")
		registryVO.setShipping(shippingVO)
		shippingVO.setFutureShippingDate("2017")
		sessionBean.getRegistryTypesEvent() >> "event"
		registryVO.setRegistryType(type)
		type.getRegistryTypeName() >> "type"
		1*catalogTools.getRegistryTypeName(_,_) >> "typeName"
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("registryVO", registryVO)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Date parsing Exception from formatDate of GetRegistryVODroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1072),_);
	}
	
	def "service method with SessionBean null"(){
		given:
		droplet = Spy()
		droplet.setCatalogTools(catalogTools)
		droplet.setGiftRegistryManager(registryManager)
		RegistryVO registryVO = new RegistryVO()
		RegistryTypes type = Mock()
		requestMock.getParameter("registryId") >> "regId"
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("isRegTypeNameReq") >> true
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> null
		1*registryManager.getRegistryDetailInfo(_,_) >> registryVO
		registryVO.setRegistryType(type)
		type.getRegistryTypeName() >> "type"
		1*catalogTools.getRegistryTypeName(_,_) >> {throw new BBBSystemException("SystemException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("registryVO", registryVO)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			registryVO.getOptInWeddingOrBump() == "false"
			1*requestMock.setParameter("registryTypeName", "")
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Get Registry name by registy code BBBSystemException from service of GetRegistryVODroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1071),_);

	}
	
	def "service method with srcDate as empty"(){
		given:
		droplet = Spy()
		droplet.setCatalogTools(catalogTools)
		droplet.setGiftRegistryManager(registryManager)
		BBBSessionBean sessionBean = Mock()
		RegistryVO registryVO = new RegistryVO()
		ShippingVO shippingVO = new ShippingVO()
		RegistryTypes type = Mock()
		requestMock.getParameter("registryId") >> null
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("isRegTypeNameReq") >> true
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		1*registryManager.getRegistryDetailInfo(_,_) >> registryVO
		registryVO.setShipping(shippingVO)
		shippingVO.setFutureShippingDate("")
		registryVO.setRegistryType(type)
		type.getRegistryTypeName() >> "type"
		1*catalogTools.getRegistryTypeName(_,_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("registryVO", registryVO)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			registryVO.getOptInWeddingOrBump() == "false"
			1*requestMock.setParameter("registryTypeName", "")
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Get Registry name by registy code BBBBusinessException from service of GetRegistryVODroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1070),_);

	}
	
	def "service method with registryVO not null"(){
		given:
		droplet = Spy()
		BBBSessionBean sessionBean = Mock()
		RegistryVO registryVO = new RegistryVO()
		Map<String, RegistryVO> registryVOs = new HashMap<String,RegistryVO>()
		registryVOs.put("regId", registryVO)
		requestMock.getParameter("registryId") >> "regId"
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("isRegTypeNameReq") >> true
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getRegistryVOs() >> registryVOs
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("registryVO", registryVO)
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			0*requestMock.setParameter("registryTypeName", "")
	}
	
	def "service method with registryVO  null and BBBBusiness exception is thrown"(){
		given:
		droplet = Spy()
		droplet.setGiftRegistryManager(registryManager)
		BBBSessionBean sessionBean = Mock()
		Map<String, RegistryVO> registryVOs = new HashMap<String,RegistryVO>()
		registryVOs.put("regId", null)
		requestMock.getParameter("registryId") >> "regId"
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("isRegTypeNameReq") >> true
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getRegistryVOs() >> registryVOs
		1*registryManager.getRegistryDetailInfo(_,_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			0*requestMock.setParameter("registryVO", _)
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			0*requestMock.setParameter("registryTypeName", "")
			1*requestMock.setParameter("errorMsg","BBBBusinessException is thrown");
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Business Exception from service of GetRegistryVODroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1073),_);
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock);
	}
	
	def "service method with registryVO  null and BBBSystemException is thrown"(){
		given:
		droplet = Spy()
		droplet.setGiftRegistryManager(registryManager)
		BBBSessionBean sessionBean = Mock()
		Map<String, RegistryVO> registryVOs = new HashMap<String,RegistryVO>()
		registryVOs.put("regId", null)
		requestMock.getParameter("registryId") >> "regId"
		requestMock.getParameter("siteId") >> "BedBathCanada"
		requestMock.getParameter("isRegTypeNameReq") >> true
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getRegistryVOs() >> registryVOs
		1*registryManager.getRegistryDetailInfo(_,_) >> {throw new BBBSystemException("SystemException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			0*requestMock.setParameter("registryVO", _)
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
			0*requestMock.setParameter("registryTypeName", "")
			1*requestMock.setParameter("errorMsg","SystemException is thrown");
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "System Exception from service of GetRegistryVODroplet ",BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1074),_);
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock);
	}
	
	def "format date method with srcDate zero"(){
		
		when:
			String formatDate = droplet.formatDate("BBBCanada",BBBCoreConstants.STRING_ZERO,null,requestMock)
		
		then:
			formatDate == null
	}
	
	def "format date method with srcDate not zero"(){
		
		when:
			String formatDate = droplet.formatDate("BBBCanada","20200509","yyyyMMdd",requestMock)
		
		then:
			formatDate == "05/09/2020"
	}
	
}
