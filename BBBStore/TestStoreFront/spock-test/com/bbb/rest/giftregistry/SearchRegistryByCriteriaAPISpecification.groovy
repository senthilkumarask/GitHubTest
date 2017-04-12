package com.bbb.rest.giftregistry

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean
import com.bbb.commerce.giftregistry.droplet.GiftRegistryPaginationDroplet
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.constants.BBBGiftRegistryConstants
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import javax.servlet.ServletException

import atg.multisite.Site
import atg.multisite.SiteContext
import spock.lang.specification.BBBExtendedSpec

class SearchRegistryByCriteriaAPISpecification extends BBBExtendedSpec {

	SearchRegistryByCriteriaAPI srbycAPI
	SiteContext sContext = Mock()
	Site site = Mock()
	BBBCatalogTools cTools = Mock()
	GiftRegistryPaginationDroplet grPaginDroplet = Mock()
	
	GiftRegSessionBean grsessionBean = new GiftRegSessionBean()
	RegistrySummaryVO rSummeryVO = Mock()
	
	def setup(){
		srbycAPI = new SearchRegistryByCriteriaAPI(siteContext : sContext, registrySearchServiceName : "rSearch", catalogTools : cTools, giftRegistryPaginationDroplet : grPaginDroplet)
	}
	
	def"searchRegistryByCriteria. TC to get Registry information"(){
		given:
			Map<String, String> inputMap = ["pageNo":"2","perPage":"3","sortPassString":"sr", "firstName" : "HARRY", "lastName":"johen"]
			List rsVO = [rSummeryVO]
			sContext.getSite() >> site
			site.getId() >> "usBed"
			2*cTools.getAllValuesForKey("WSDLSiteFlags", "usBed") >> ["us"]
			1*cTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken1"]
			
			
			requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> grsessionBean
			requestMock.getObjectParameter("registrySummaryResultList") >> rsVO
			requestMock.getObjectParameter("errorMsg") >> null
			
		when:
			List value = srbycAPI.searchRegistryByCriteria(inputMap)
		then:
			1*grPaginDroplet.service(requestMock, responseMock)
			1*requestMock.setParameter("pageNo", "2")
			1*requestMock.setParameter("perPage", "3")
			1*requestMock.setParameter("sortPassString", "sr")
			1*requestMock.setParameter("siteId", "usBed")
			RegistrySearchVO rSearchVO = grsessionBean.getRequestVO()
			rSearchVO.getFirstName() == "HARRY"
			rSearchVO.getLastName() == "johen"
			rSearchVO.getServiceName() == "rSearch"
			rSearchVO.getSiteId() == "us"
			rSearchVO.getUserToken() == "tocken1"
			!rSearchVO.isReturnLeagacyRegistries()
			rSearchVO.getGiftGiver()
			value.get(0) == rSummeryVO
	} 
	
	def"searchRegistryByCriteria. when first name is empty and grPaginationDroplet having error"(){
		given:
			Map<String, String> inputMap = ["pageNo":"2","perPage":"3","sortPassString":"sr", "firstName" : "", "registryId":"rId"]
			List rsVO = [rSummeryVO]
			sContext.getSite() >> site
			site.getId() >> "usBed"
			1*cTools.getAllValuesForKey("WSDLSiteFlags", "usBed") >> null
			1*cTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken1"]
			
			
			requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> grsessionBean
			requestMock.getObjectParameter("registrySummaryResultList") >> rsVO
			requestMock.getObjectParameter("errorMsg") >> "error"
			
		when:
			List value = srbycAPI.searchRegistryByCriteria(inputMap)
		then:
			BBBSystemException exception = thrown()
			1*grPaginDroplet.service(requestMock, responseMock)
			1*requestMock.setParameter("pageNo", "2")
			1*requestMock.setParameter("perPage", "3")
			1*requestMock.setParameter("sortPassString", "sr")
			1*requestMock.setParameter("siteId", "usBed")
			RegistrySearchVO rSearchVO = grsessionBean.getRequestVO()
			rSearchVO.getRegistryId() == "rId"
			rSearchVO.getServiceName() == "rSearch"
	}
	
	def"searchRegistryByCriteria. when registryId is empty and grPaginationDroplet throws IOException"(){
		given:
			Map<String, String> inputMap = ["pageNo":"2","perPage":"3","sortPassString":"sr", "firstName" : null, "registryId":"", "email" : "hs@g"]
			List rsVO = [rSummeryVO]
			sContext.getSite() >> site
			site.getId() >> "usBed"
			1*cTools.getAllValuesForKey("WSDLSiteFlags", "usBed") >> null
			1*cTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken1"]
			
			
			requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> grsessionBean
			1*grPaginDroplet.service(requestMock, responseMock)	>> {throw new IOException("exception")}		
		when:
			List value = srbycAPI.searchRegistryByCriteria(inputMap)
		then:
			BBBSystemException exception = thrown()
			
			1*requestMock.setParameter("pageNo", "2")
			1*requestMock.setParameter("perPage", "3")
			1*requestMock.setParameter("sortPassString", "sr")
			1*requestMock.setParameter("siteId", "usBed")
			RegistrySearchVO rSearchVO = grsessionBean.getRequestVO()
			rSearchVO.getEmail() == "hs@g"
			rSearchVO.getServiceName() == "rSearch"
	}
	
	def"searchRegistryByCriteria. when email ,firstName is empty and registryId is null "(){
		given:
			Map<String, String> inputMap = ["pageNo":"2","perPage":"3","sortPassString":"sr", "firstName" : null, "registryId":null, "email" : ""]
			List rsVO = [rSummeryVO]
			sContext.getSite() >> site
			site.getId() >> "usBed"
	when:
			List value = srbycAPI.searchRegistryByCriteria(inputMap)
		then:
			BBBBusinessException exception = thrown()
			
			0*grPaginDroplet.service(requestMock, responseMock)
			value == null
			0*cTools.getAllValuesForKey("WSDLSiteFlags", _)
	}
	
	def"searchRegistryByCriteria. when email is null "(){
		given:
			Map<String, String> inputMap = ["pageNo":"2","perPage":"3","sortPassString":"sr", "firstName" : null, "registryId":null, "email" : null]
			List rsVO = [rSummeryVO]
			sContext.getSite() >> site
			site.getId() >> "usBed"
	when:
			List value = srbycAPI.searchRegistryByCriteria(inputMap)
		then:
			BBBBusinessException exception = thrown()
			
			0*grPaginDroplet.service(requestMock, responseMock)
			value == null
			0*cTools.getAllValuesForKey("WSDLSiteFlags", _)
	}
	
	def"searchRegistryByCriteria. when registryId is empty and grPaginationDroplet throws ServletException"(){
		given:
			Map<String, String> inputMap = ["pageNo":"2","perPage":"3","sortPassString":"sr", "firstName" : null, "registryId":"", "email" : "hs@g"]
			List rsVO = [rSummeryVO]
			sContext.getSite() >> site
			site.getId() >> "usBed"
			1*cTools.getAllValuesForKey("WSDLSiteFlags", "usBed") >> null
			1*cTools.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["tocken1"]
			
			
			requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> grsessionBean
			1*grPaginDroplet.service(requestMock, responseMock)	>> {throw new ServletException("exception")}
		when:
			List value = srbycAPI.searchRegistryByCriteria(inputMap)
		then:
			BBBSystemException exception = thrown()
			
			1*requestMock.setParameter("pageNo", "2")
			1*requestMock.setParameter("perPage", "3")
			1*requestMock.setParameter("sortPassString", "sr")
			1*requestMock.setParameter("siteId", "usBed")
			RegistrySearchVO rSearchVO = grsessionBean.getRequestVO()
			rSearchVO.getEmail() == "hs@g"
			rSearchVO.getServiceName() == "rSearch"
	}
}
