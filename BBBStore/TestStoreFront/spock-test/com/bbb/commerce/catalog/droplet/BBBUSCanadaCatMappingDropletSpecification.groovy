package com.bbb.commerce.catalog.droplet

import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.vo.CategoryMappingVo
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter

import spock.lang.specification.BBBExtendedSpec

class BBBUSCanadaCatMappingDropletSpecification extends BBBExtendedSpec {

	BBBUSCanadaCatMappingDroplet droplet
	BBBCatalogToolsImpl catalogTools
	
	def setup(){
		droplet = Spy()
		catalogTools = Mock()
		droplet.setCatalogTools(catalogTools)
	}
	
	def "service method happy path"(){
		given:
			droplet.extractCurrentSiteId() >> "BedBathUS"
			requestMock.getObjectParameter("categoryId") >> "categoryId"
			requestMock.getObjectParameter("postURL") >> "postURL?categoryId"
			CategoryMappingVo categoryMappingVO = new CategoryMappingVo()
			1*catalogTools.getUSCanadaCategoryMapping("categoryId") >> categoryMappingVO
			categoryMappingVO.setCanadaUrl("")
			categoryMappingVO.setCanadaCategoryId("canadaCatId")
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*droplet.logDebug("returnPostURL in BBBUSCanadaCatMappingDroplet : postURL?canadaCatId")
			requestMock.setParameter("returnPostURL", "postURL?canadaCatId")
			1*requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def "service method with canadaCatId empty"(){
		given:
			droplet.extractCurrentSiteId() >> "BedBathUS"
			requestMock.getObjectParameter("categoryId") >> "categoryId"
			requestMock.getObjectParameter("postURL") >> "postURL?categoryId"
			CategoryMappingVo categoryMappingVO = new CategoryMappingVo()
			1*catalogTools.getUSCanadaCategoryMapping("categoryId") >> categoryMappingVO
			categoryMappingVO.setCanadaUrl("")
			categoryMappingVO.setCanadaCategoryId("")
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*requestMock.setParameter("returnPostURL", "")
			1*requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def "service method with canadaURL not empty"(){
		given:
			droplet.extractCurrentSiteId() >> "BedBathUS"
			requestMock.getObjectParameter("categoryId") >> "categoryId"
			requestMock.getObjectParameter("postURL") >> "postURL?categoryId"
			CategoryMappingVo categoryMappingVO = new CategoryMappingVo()
			1*catalogTools.getUSCanadaCategoryMapping("categoryId") >> categoryMappingVO
			categoryMappingVO.setCanadaUrl("canadaURL")
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*droplet.logDebug("returnPostURL in BBBUSCanadaCatMappingDroplet : canadaURL");
			1*requestMock.setParameter("returnPostURL", "canadaURL")
			1*requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def "service method with siteId not equals BedBathUS"(){
		given:
			droplet.extractCurrentSiteId() >> "BedBathCA"
			requestMock.getObjectParameter("categoryId") >> "categoryId"
			requestMock.getObjectParameter("postURL") >> "postURL?categoryId"
			CategoryMappingVo categoryMappingVO = new CategoryMappingVo()
			1*catalogTools.getUSCanadaCategoryMapping("categoryId") >> categoryMappingVO
			categoryMappingVO.setUsUrl("")
			categoryMappingVO.setUsCategoryId("USCatId")
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*droplet.logDebug("returnPostURL in BBBUSCanadaCatMappingDroplet : postURL?USCatId")
			requestMock.setParameter("returnPostURL", "postURL?USCatId")
			1*requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def "service method with empty USCatId"(){
		given:
			droplet.extractCurrentSiteId() >> "BedBathCA"
			requestMock.getObjectParameter("categoryId") >> "categoryId"
			requestMock.getObjectParameter("postURL") >> "postURL?categoryId"
			CategoryMappingVo categoryMappingVO = new CategoryMappingVo()
			1*catalogTools.getUSCanadaCategoryMapping("categoryId") >> categoryMappingVO
			categoryMappingVO.setUsUrl("")
			categoryMappingVO.setUsCategoryId("")
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			requestMock.setParameter("returnPostURL", "")
			1*requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def "service method with not empty UsURL"(){
		given:
			droplet.extractCurrentSiteId() >> "BedBathCA"
			requestMock.getObjectParameter("categoryId") >> "categoryId"
			requestMock.getObjectParameter("postURL") >> "postURL?categoryId"
			CategoryMappingVo categoryMappingVO = new CategoryMappingVo()
			1*catalogTools.getUSCanadaCategoryMapping("categoryId") >> categoryMappingVO
			categoryMappingVO.setUsUrl("UsURL")
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*droplet.logDebug("returnPostURL in BBBUSCanadaCatMappingDroplet : UsURL");
			1*requestMock.setParameter("returnPostURL", "UsURL")
			1*requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def "service method with BBBSystemException thrown"(){
		given:
			droplet.extractCurrentSiteId() >> "BedBathCA"
			requestMock.getObjectParameter("categoryId") >> "categoryId"
			1*catalogTools.getUSCanadaCategoryMapping("categoryId") >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "System Exception from service of BBBUSCanadaCatMappingDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1021),_)
			1*requestMock.setParameter("error", "err_us_canada_category_mapping_system_error")
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def "service method with BBBBusinessException thrown"(){
		given:
			droplet.extractCurrentSiteId() >> "BedBathCA"
			requestMock.getObjectParameter("categoryId") >> "categoryId"
			1*catalogTools.getUSCanadaCategoryMapping("categoryId") >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Business Exception from service of BBBUSCanadaCatMappingDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1020),_)
			1*requestMock.setParameter("error", "err_us_canada_category_mapping_system_error")
			1*requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def "service method with categoryId empty"(){
		given:
			droplet.extractCurrentSiteId() >> "BedBathCA"
			requestMock.getObjectParameter("categoryId") >> ""
			0*catalogTools.getUSCanadaCategoryMapping("categoryId") >> null
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*requestMock.serviceParameter("output", requestMock, responseMock)
	}
}
