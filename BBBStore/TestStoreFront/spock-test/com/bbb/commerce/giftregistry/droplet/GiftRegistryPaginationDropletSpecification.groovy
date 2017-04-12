package com.bbb.commerce.giftregistry.droplet

import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.RegSearchResVO;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import spock.lang.specification.BBBExtendedSpec

class GiftRegistryPaginationDropletSpecification extends BBBExtendedSpec {

	GiftRegistryPaginationDroplet droplet
	GiftRegistryManager mGiftRegistryManager
	
	def setup(){
		droplet = new GiftRegistryPaginationDroplet()
		mGiftRegistryManager = Mock()
		droplet.setGiftRegistryManager(mGiftRegistryManager)
	}
	
	def "service method happy path"(){
		
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.SORT_PASS_STRING) >> "id"
			requestMock.getParameter(BBBGiftRegistryConstants.SORTING_ORDER) >> "DESC"
			GiftRegSessionBean registrySessionBean = Mock()
			requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> registrySessionBean
			requestMock.getParameter("siteId") >> "siteId"
			requestMock.getParameter("mx_registry_pagination") >> "mxregistry"
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			registrySessionBean.getRequestVO() >> registrySearchVO
			requestMock.getLocalParameter(BBBGiftRegistryConstants.PAGE_NO) >> "1"
			requestMock.getLocalParameter(BBBGiftRegistryConstants.PER_PAGE) >> "10"
			requestMock.getLocalParameter("registryId") >> "regId"
			RegSearchResVO searchResponseVO = Mock()
			RegistrySummaryVO regSummaryVO = Mock()
			mGiftRegistryManager.searchRegistries(_,_) >> searchResponseVO
			searchResponseVO.getTotEntries() >> 20
			searchResponseVO.getListRegistrySummaryVO() >> [regSummaryVO]
			
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter(BBBGiftRegistryConstants.TOTAL_RESULT_COUNT,20)
			1*requestMock.setParameter(BBBGiftRegistryConstants.REGiSTRY_SEARCH_LIST,_)
			1*requestMock.serviceParameter("output", requestMock,responseMock)
	}
	
	def "service method with pageNo, perPage, registryId,sortOrder,sortPassString,searchResponseVO as null"(){
		
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.SORT_PASS_STRING) >> null
			requestMock.getParameter(BBBGiftRegistryConstants.SORTING_ORDER) >> null
			GiftRegSessionBean registrySessionBean = Mock()
			requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> registrySessionBean
			requestMock.getParameter("siteId") >> "siteId"
			requestMock.getParameter("mx_registry_pagination") >> "mxregistry"
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			registrySessionBean.getRequestVO() >> registrySearchVO
			requestMock.getLocalParameter(BBBGiftRegistryConstants.PAGE_NO) >> null
			requestMock.getLocalParameter(BBBGiftRegistryConstants.PER_PAGE) >> null
			requestMock.getLocalParameter("registryId") >> null
			RegistrySummaryVO regSummaryVO = Mock()
			mGiftRegistryManager.searchRegistries(_,_) >> null
			
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			0*requestMock.setParameter(BBBGiftRegistryConstants.TOTAL_RESULT_COUNT,20)
			0*requestMock.setParameter(BBBGiftRegistryConstants.REGiSTRY_SEARCH_LIST,_)
			1*requestMock.serviceParameter("empty", requestMock,responseMock)
	}
	
	def "service method with pageNo, perPage, registryId,sortOrder,sortPassString,searchResponseVO as null and regPagination not equals mxregistry and SocketTimeoutException thrown"(){
		
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.SORT_PASS_STRING) >> null
			requestMock.getParameter(BBBGiftRegistryConstants.SORTING_ORDER) >> null
			GiftRegSessionBean registrySessionBean = Mock()
			requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> registrySessionBean
			requestMock.getParameter("siteId") >> "siteId"
			requestMock.getParameter("mx_registry_pagination") >> "mxregistry1"
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			registrySessionBean.getRequestVO() >> registrySearchVO
			requestMock.getLocalParameter(BBBGiftRegistryConstants.PAGE_NO) >> null
			requestMock.getLocalParameter(BBBGiftRegistryConstants.PER_PAGE) >> null
			requestMock.getLocalParameter("registryId") >> null
			RegistrySummaryVO regSummaryVO = Mock()
			mGiftRegistryManager.searchRegistries(_,_) >> {throw new SocketTimeoutException("SocketTimeoutException is thrown")}
			
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("errorMsg","err_regsearch_biz_exception")
			1*requestMock.serviceLocalParameter("error", requestMock,responseMock)
	}
	
	def "service method with pageNo, perPage, registryId,sortOrder,sortPassString,searchResponseVO as null and regPagination not equals mxregistry and BBBSystemException thrown"(){
		
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.SORT_PASS_STRING) >> null
			requestMock.getParameter(BBBGiftRegistryConstants.SORTING_ORDER) >> null
			GiftRegSessionBean registrySessionBean = Mock()
			requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> registrySessionBean
			requestMock.getParameter("siteId") >> "siteId"
			requestMock.getParameter("mx_registry_pagination") >> "mxregistry1"
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			registrySessionBean.getRequestVO() >> registrySearchVO
			requestMock.getLocalParameter(BBBGiftRegistryConstants.PAGE_NO) >> null
			requestMock.getLocalParameter(BBBGiftRegistryConstants.PER_PAGE) >> null
			requestMock.getLocalParameter("registryId") >> null
			RegistrySummaryVO regSummaryVO = Mock()
			mGiftRegistryManager.searchRegistries(_,_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
			
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("errorMsg","err_regsearch_sys_exception")
			1*requestMock.serviceLocalParameter("error", requestMock,responseMock)
	}
	
	def "service method with pageNo, perPage, registryId,sortOrder,sortPassString,searchResponseVO as null and regPagination not equals mxregistry and BBBBusinessException thrown"(){
		
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.SORT_PASS_STRING) >> null
			requestMock.getParameter(BBBGiftRegistryConstants.SORTING_ORDER) >> null
			GiftRegSessionBean registrySessionBean = Mock()
			requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> registrySessionBean
			requestMock.getParameter("siteId") >> "siteId"
			requestMock.getParameter("mx_registry_pagination") >> "mxregistry1"
			RegistrySearchVO registrySearchVO = new RegistrySearchVO()
			registrySessionBean.getRequestVO() >> registrySearchVO
			requestMock.getLocalParameter(BBBGiftRegistryConstants.PAGE_NO) >> null
			requestMock.getLocalParameter(BBBGiftRegistryConstants.PER_PAGE) >> null
			requestMock.getLocalParameter("registryId") >> null
			RegistrySummaryVO regSummaryVO = Mock()
			mGiftRegistryManager.searchRegistries(_,_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
			
		
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("errorMsg","err_regsearch_biz_exception")
			1*requestMock.serviceLocalParameter("error", requestMock,responseMock)
	}
	
	def "service method with regisytrySearchVO is null"(){
		given:
			GiftRegSessionBean registrySessionBean	= Mock()
			requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> registrySessionBean
			registrySessionBean.getRequestVO() >> null
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*requestMock.setParameter("errorMsg","err_regsearch_biz_exception");
			1*requestMock.serviceParameter("error", requestMock, responseMock)
	}
	
	def "service method with mx_registry as null"(){
		given:
			GiftRegSessionBean registrySessionBean	= Mock()
			requestMock.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean") >> registrySessionBean
			RegistrySearchVO registrySearchVO = Mock()
			registrySessionBean.getRequestVO() >> registrySearchVO
			requestMock.getParameter("mx_registry_pagination") >> null
			
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*requestMock.serviceParameter("empty", requestMock, responseMock)
	}
}
