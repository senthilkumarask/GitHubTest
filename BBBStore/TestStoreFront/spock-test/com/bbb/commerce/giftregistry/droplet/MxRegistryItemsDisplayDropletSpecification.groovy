package com.bbb.commerce.giftregistry.droplet

import com.bbb.cms.PromoBoxVO
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.RegistryCategoryMapVO
import com.bbb.commerce.catalog.vo.RegistryTypeVO
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.RegistryItemVO
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.constants.BBBGiftRegistryConstants
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException

import atg.commerce.pricing.priceLists.PriceListException
import atg.commerce.pricing.priceLists.PriceListManager
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryItem
import atg.userprofiling.Profile
import spock.lang.specification.BBBExtendedSpec

class MxRegistryItemsDisplayDropletSpecification extends BBBExtendedSpec {

	MxRegistryItemsDisplayDroplet mridDroplet
	GiftRegistryManager grManager = Mock()
	Profile profileMock = Mock()
	BBBCatalogTools cToolsMock = Mock()
	PriceListManager pListManager = Mock()
	
	RegistryItemsListVO regItemListVO = new RegistryItemsListVO() 
	RegistryTypeVO rTypeVo = new RegistryTypeVO ()
	RegistryTypeVO rTypeVo1 = new RegistryTypeVO ()
	
	RegistryItemVO regItemVO = new RegistryItemVO()
	RegistryItemVO regItemVO1 = new RegistryItemVO()
	RegistryItemVO regItemVO2 = new RegistryItemVO()
	
	PromoBoxVO pBoxVO = new PromoBoxVO()
	
	ServiceErrorVO errorVO = new ServiceErrorVO()
	//ServiceErrorVO errorVO = Mock()
	SKUDetailVO sDetailVO = new SKUDetailVO()
	SKUDetailVO sDetailVO1 = new SKUDetailVO()
	SKUDetailVO sDetailVO2 = new SKUDetailVO()
	
	RegistryCategoryMapVO rCategoryMapVO = new RegistryCategoryMapVO()
	
	def List sDetailVolist = [sDetailVO]
	def List regItemList = [regItemVO, regItemVO1,regItemVO2]
	def Map<String, RegistryItemVO> itemVOMap =["rvo" : regItemVO1]
	def setup(){
		 mridDroplet = new MxRegistryItemsDisplayDroplet(giftRegistryManager : grManager, catalogTools : cToolsMock,registryItemsListServiceName : "ritem", topRegMaxCount : "2", priceListManager : pListManager, profilePriceListPropertyName: "priceList")
	}
	def"service.TC to  Fetch Mx Registry Item List "(){
		given:
		requestMock.getParameter( ParameterName.getParameterName("registryId" ) ) >> "regId"
		requestMock.getParameter( ParameterName.getParameterName("sortSeq") ) >> null
		requestMock.getParameter( ParameterName.getParameterName("view") ) >> "12"
		requestMock.getParameter( ParameterName.getParameterName("eventTypeCode") ) >> "etCode"
		requestMock.getParameter( ParameterName.getParameterName("startIdx") ) >> "0"

		requestMock.getParameter( ParameterName.getParameterName("blkSize") ) >> "6"
		requestMock.getParameter( ParameterName.getParameterName("isGiftGiver") ) >> "true"
		requestMock.getParameter( ParameterName.getParameterName("isAvailForWebPurchaseFlag") ) >> "true"
		requestMock.getParameter("isMxGiftGiver") >> true
		requestMock.getParameter( ParameterName.getParameterName("siteId") ) >> "siteId"
		
		requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		profileMock.getRepositoryId() >> "rId"
		2*cToolsMock.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["wsdKey"]
		
		1*grManager.fetchRegistryItems({RegistrySearchVO vo->vo.blkSize==6 && vo.siteId=='5' && vo.userToken=='wsdKey' && vo.serviceName=='ritem' && vo.registryId=='regId' && vo.view==12 && vo.startIdx==0 && vo.giftGiver==true && vo.availForWebPurchaseFlag == true}) >> regItemListVO
		1*cToolsMock.getRegistryTypes("siteId") >> [rTypeVo, rTypeVo1]
		//Start :check registry type
		rTypeVo.setRegistryCode("etCode")
		rTypeVo1.setRegistryCode("etCode1")
		
		rTypeVo.setRegistryTypeId("rTypeId")
		//End : Check Registry type
		
		//Start : processItemList
		regItemListVO.setServiceErrorVO(errorVO) 
		errorVO.setErrorExists(true) 
		errorVO.setErrorDisplayMessage("displayMessage")  
		errorVO.setErrorId(900) 
		1*cToolsMock.getPromoBoxForRegistry("rTypeId") >> pBoxVO
		regItemListVO.setRegistryItemList(regItemList)
		
		regItemVO.setSku(2) 
		regItemVO1.setSku(3) 
		regItemVO2.setSku(4)
		1*cToolsMock.getParentProductForSku("2", true)
		1*cToolsMock.getParentProductForSku("4", true)
		1*cToolsMock.getParentProductForSku("3", true) >> {throw new Exception("exception")}
		
		regItemListVO.setSkuRegItemVOMap(itemVOMap)
		
		//getlistRegistryItemVO
		1*cToolsMock.getAllValuesForKey("CertonaKeys", _) >> ["3"]
		//checkPrice
		1*cToolsMock.getParentProductForSku("2", true) >> "pid"
		1*cToolsMock.getParentProductForSku("4", true) >> "pid"
		regItemVO.setJdaRetailPrice(2)
		regItemVO2.setJdaRetailPrice(0)
		
		1*cToolsMock.getSalePrice("pid","4") >> null
		1*cToolsMock.getListPrice("pid","4") >> null
	
		//withDefaultCategory
		1*cToolsMock.sortSkubyRegistry(_, null,"rTypeId", _,"category",null) >> ["sku1" : sDetailVolist]
		//start : skuDetailsVO
		sDetailVO.setSkuId("rvo")
		1*cToolsMock.isSKUBelowLine(_, _) >> true
		//end skuDetailsVO
		//end withDefaultCategory
		//end checkPrice
		cToolsMock.getCategoryForRegistry("rTypeId") >> ["mapVO":rCategoryMapVO,"sku1":rCategoryMapVO]
		//end getlistRegistryItemVO
		//end : processItemList
		
		when:
		mridDroplet.service(requestMock, responseMock)
		then:
		0*grManager.isRegistryOwnedByProfile("rId", "regId", "siteId")
		1*requestMock.setParameter("errorMsg", "err_gift_reg_fatal_error")
		1*requestMock.serviceParameter("error", requestMock, responseMock)
		1*requestMock.setParameter("promoBox", pBoxVO)
		1*requestMock.setParameter("skuList", '2')
		1*requestMock.setParameter("certonaSkuList", '2')
		1*requestMock.setParameter("categoryVOMap",_)
		regItemVO1.getsKUDetailVO() != null
		1*requestMock.setParameter("emptyOutOfStockListFlag",_)
		1*requestMock.setParameter("categoryBuckets",_)
		1*requestMock.setParameter('notInStockCategoryBuckets', ['mapVO':null, 'sku1':null])
		1 * requestMock.setParameter('inStockCategoryBuckets', _)
		//1 * requestMock.setParameter("emptyOutOfStockListFlag",_)
		1 * requestMock.setParameter('sortSequence', '1')
		1 * requestMock.setParameter('count', 2)
		1 * requestMock.setParameter('totEntries', 0)

	}
	
	def"service.TC to  Fetch Mx Registry when sku registry map is empty gets from sortSkubyRegistry() method and error code is 901 "(){
		given:
		def List sDetailVolist = []
		def List regItemList = [regItemVO, regItemVO1]
		def Map<String, RegistryItemVO> itemVOMap =["rvo" : regItemVO1]
	
		
		requestMock.getParameter( ParameterName.getParameterName("registryId" ) ) >> "regId"
		requestMock.getParameter( ParameterName.getParameterName("sortSeq") ) >> "1"
		requestMock.getParameter( ParameterName.getParameterName("view") ) >> null
		requestMock.getParameter( ParameterName.getParameterName("eventTypeCode") ) >> "etCode"
		requestMock.getParameter( ParameterName.getParameterName("startIdx") ) >> "0"

		requestMock.getParameter( ParameterName.getParameterName("blkSize") ) >> "6"
		requestMock.getParameter( ParameterName.getParameterName("isGiftGiver") ) >> "false"
		requestMock.getParameter( ParameterName.getParameterName("isAvailForWebPurchaseFlag") ) >> "true"
		requestMock.getParameter("isMxGiftGiver") >> false
		requestMock.getParameter( ParameterName.getParameterName("siteId") ) >> "siteId"
		
		requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		profileMock.getRepositoryId() >> "rId"
		cToolsMock.getAllValuesForKey("WSDLSiteFlags", "siteId") >> ["true"]
		
		1*cToolsMock.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> []
		
		1*grManager.fetchRegistryItems(_) >> regItemListVO
		1*cToolsMock.getRegistryTypes("siteId") >> [rTypeVo, rTypeVo1]
		//Start :check registry type
		rTypeVo.setRegistryCode("etCode")
		rTypeVo1.setRegistryCode("etCode1")
		
		rTypeVo.setRegistryTypeId("rTypeId")
		//End : Check Registry type
		
		//Start : processItemList
		regItemListVO.setServiceErrorVO(errorVO)
		errorVO.setErrorExists(true)
		errorVO.setErrorDisplayMessage("displayMessage")
		errorVO.setErrorId(901)
		1*cToolsMock.getPromoBoxForRegistry("rTypeId") >> pBoxVO
		regItemListVO.setRegistryItemList(regItemList)
		
		regItemVO.setSku(2)
		regItemVO1.setSku(3)
	//	regItemVO2.setSku(4)
		1*cToolsMock.getParentProductForSku("2", true)
		//1*cToolsMock.getParentProductForSku("4", true)
		1*cToolsMock.getParentProductForSku("3", true) 
		
		regItemListVO.setSkuRegItemVOMap(itemVOMap)
		
		//getlistRegistryItemVO
		1*cToolsMock.getAllValuesForKey("CertonaKeys", _) >> ["2"]
		//checkPrice
		1*cToolsMock.getParentProductForSku("2", true) >> "pid"
		1*cToolsMock.getParentProductForSku("3", true) >> null
		regItemVO.setJdaRetailPrice(0)
//		regItemVO2.setJdaRetailPrice(0)
		
		1*cToolsMock.getSalePrice("pid","2") >> 0
		1*cToolsMock.getListPrice("pid","2") >> "5"
	
		//withDefaultCategory
		1*cToolsMock.sortSkubyRegistry(_, null,"rTypeId", _,"category",null) >> ["sku1" : sDetailVolist]
		//start : skuDetailsVO
		sDetailVO.setSkuId("rvo")
		//1*cToolsMock.isSKUBelowLine(_, _) >> true
		//end skuDetailsVO
		//end withDefaultCategory
		//end checkPrice
		cToolsMock.getCategoryForRegistry("rTypeId") >> ["mapVO":rCategoryMapVO,"sku1":rCategoryMapVO]
		//end getlistRegistryItemVO
		//end : processItemList
		
		when:
		mridDroplet.service(requestMock, responseMock)
		then:
		1*grManager.isRegistryOwnedByProfile("rId", "regId", "siteId")
		1*requestMock.setParameter("errorMsg", "err_gift_reg_siteflag_usertoken_error")
		1*requestMock.serviceParameter("error", requestMock, responseMock)
		1*requestMock.setParameter("promoBox", pBoxVO)
		1*requestMock.setParameter("skuList", '2')
		1*requestMock.setParameter("certonaSkuList", '2')
		1*requestMock.setParameter("categoryVOMap",_)
		1*requestMock.setParameter("emptyOutOfStockListFlag",_)
		1*requestMock.setParameter("categoryBuckets",_)
		1*requestMock.setParameter('notInStockCategoryBuckets', ['mapVO':null, 'sku1':null])
		1 * requestMock.setParameter('inStockCategoryBuckets', _)
		1 * requestMock.setParameter('sortSequence', '1')
		1 * requestMock.setParameter('count', 2)
		1 * requestMock.setParameter('totEntries', 0)

	}
	
	def"service.TC to  Fetch Mx Registry when sortSeq is price sort sequence and error code is 902 "(){
		given:
		def List sDetailVolist = [sDetailVO,sDetailVO1,sDetailVO2]
		//def List regItemList = [regItemVO, regItemVO1]
		def List regItemList = [regItemVO]
		def Map<String, RegistryItemVO> itemVOMap =["rvo" : regItemVO1]
		def Map<String, List> mxSkuReg =["rvo" : sDetailVolist]
	    RepositoryItem priceList = Mock()
		RepositoryItem price = Mock()
		
		requestMock.getParameter( ParameterName.getParameterName("registryId" ) ) >> "regId"
		requestMock.getParameter( ParameterName.getParameterName("sortSeq") ) >> "2"
		requestMock.getParameter( ParameterName.getParameterName("view") ) >> null
		requestMock.getParameter( ParameterName.getParameterName("eventTypeCode") ) >> "etCode"
		requestMock.getParameter( ParameterName.getParameterName("startIdx") ) >> "0"

		requestMock.getParameter( ParameterName.getParameterName("blkSize") ) >> "6"
		requestMock.getParameter( ParameterName.getParameterName("isGiftGiver") ) >> "false"
		requestMock.getParameter( ParameterName.getParameterName("isAvailForWebPurchaseFlag") ) >> "true"
		requestMock.getParameter("isMxGiftGiver") >> false
		requestMock.getParameter( ParameterName.getParameterName("siteId") ) >> "siteId"
		
		requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		requestMock.getParameter("mxConversionValue") >> "mxValue"
		
		profileMock.getRepositoryId() >> "rId"
		cToolsMock.getAllValuesForKey("WSDLSiteFlags", "siteId") >> []
		
		1*cToolsMock.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> []
		
		1*grManager.fetchRegistryItems(_) >> regItemListVO
		1*cToolsMock.getRegistryTypes("siteId") >> [rTypeVo, rTypeVo1]
		//Start :check registry type
		rTypeVo.setRegistryCode("etCode")
		rTypeVo1.setRegistryCode("etCode1")
		
		rTypeVo.setRegistryTypeId("rTypeId")
		//End : Check Registry type
		
		//Start : processItemList
		regItemListVO.setServiceErrorVO(errorVO)
		errorVO.setErrorExists(true)
		errorVO.setErrorDisplayMessage("displayMessage")
		errorVO.setErrorId(902)
		1*cToolsMock.getPromoBoxForRegistry("rTypeId") >> pBoxVO
		regItemListVO.setRegistryItemList(regItemList)
		
		regItemVO.setSku(2)
		//regItemVO1.setSku(3)
	//	regItemVO2.setSku(4)
		1*cToolsMock.getParentProductForSku("2", true)
		1*cToolsMock.getParentProductForSku("2", true) >> null
		
		regItemListVO.setSkuRegItemVOMap(itemVOMap)
		
		//getlistRegistryItemVO
		1*cToolsMock.getAllValuesForKey("CertonaKeys", _) >> [""]
		
		cToolsMock.sortMxSkubyRegistry(null, _, "rTypeId", "siteId", null, "mxValue") >> ["rvo" : sDetailVolist]
		sDetailVO.setSkuId("rvo")
		
		sDetailVO1.setSkuId(null)
		sDetailVO2.setSkuId("rvo1")
		1*cToolsMock.getParentProductForSku("rvo") >> "pId"
		1*cToolsMock.getParentProductForSku("rvo1") >> "pId1"
		

		cToolsMock.getMxPriceRanges("rTypeId") >> ["rvo","not"]
		
		
		
		sDetailVO.setSkuId("rvo")
		//1*pListManager.getPrice(priceList, "pId", "rvo") 
		1*pListManager.getPrice(_, "pId1", "rvo1") >> {throw new PriceListException("exception")}
		regItemVO1.setJdaRetailPrice(20)
		
		1*cToolsMock.isSKUBelowLine("siteId", "rvo") >> true
		
		1*cToolsMock.getCategoryForRegistry("rTypeId") >> ["mapVO":rCategoryMapVO,"sku1":rCategoryMapVO]
		
		when:
		mridDroplet.service(requestMock, responseMock)
		then:
		1*grManager.isRegistryOwnedByProfile("rId", "regId", "siteId")
		1*requestMock.setParameter("errorMsg", "err_gift_reg_invalid_input_format")
		1*requestMock.serviceParameter("error", requestMock, responseMock)
		1*requestMock.setParameter("promoBox", pBoxVO)
		0*requestMock.setParameter("certonaSkuList", '2')
		1*requestMock.setParameter("emptyOutOfStockListFlag",_)
		1*requestMock.setParameter("categoryBuckets",_)
		1 * requestMock.setParameter('inStockCategoryBuckets', _)
		1 * requestMock.setParameter('totEntries', 0)
		
		1 * requestMock.setParameter("priceRangeList", ["rvo","not"])
		
		1 * requestMock.setParameter('sortSequence', '2')
		1 * requestMock.setParameter('count', 1)
		1 * requestMock.setParameter('notInStockCategoryBuckets', ['rvo':null, 'not':null])
		
		regItemVO1.getPrice() == "20.0"
		regItemVO1.getsKUDetailVO() == sDetailVO
		regItemVO1.getIsBelowLineItem() 

	}
	
	def"service.TC to  Fetch Mx Registry when sortSeq is price sort sequence and error display message is empty "(){
		given:
			def List sDetailVolist = [sDetailVO]
			//def List regItemList = [regItemVO, regItemVO1]
			def List regItemList = [regItemVO]
			def Map<String, RegistryItemVO> itemVOMap =["rvo" : regItemVO1]
			def Map<String, List> mxSkuReg =["rvo" : sDetailVolist]
			RepositoryItem priceList = Mock()
			RepositoryItem price = Mock()
			
			requestMock.getParameter( ParameterName.getParameterName("registryId" ) ) >> "regId"
			requestMock.getParameter( ParameterName.getParameterName("sortSeq") ) >> "2"
			requestMock.getParameter( ParameterName.getParameterName("view") ) >> null
			requestMock.getParameter( ParameterName.getParameterName("eventTypeCode") ) >> "etCode"
			requestMock.getParameter( ParameterName.getParameterName("startIdx") ) >> "0"
	
			requestMock.getParameter( ParameterName.getParameterName("blkSize") ) >> "6"
			requestMock.getParameter( ParameterName.getParameterName("isGiftGiver") ) >> "false"
			requestMock.getParameter( ParameterName.getParameterName("isAvailForWebPurchaseFlag") ) >> "true"
			requestMock.getParameter("isMxGiftGiver") >> false
			requestMock.getParameter( ParameterName.getParameterName("siteId") ) >> "siteId"
			
			requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
			requestMock.getParameter("mxConversionValue") >> "mxValue"
			
			profileMock.getRepositoryId() >> "rId"
			cToolsMock.getAllValuesForKey("WSDLSiteFlags", "siteId") >> []
			
			1*cToolsMock.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> []
			
			1*grManager.fetchRegistryItems(_) >> regItemListVO
			1*cToolsMock.getRegistryTypes("siteId") >> [rTypeVo, rTypeVo1]
			//Start :check registry type
			rTypeVo.setRegistryCode("etCode")
			rTypeVo1.setRegistryCode("etCode1")
			
			rTypeVo.setRegistryTypeId("rTypeId")
			//End : Check Registry type
			
			//Start : processItemList
			regItemListVO.setServiceErrorVO(errorVO)
			errorVO.setErrorExists(true)
			errorVO.setErrorDisplayMessage("")
			errorVO.setErrorId(902)
			1*cToolsMock.getPromoBoxForRegistry("rTypeId") >> pBoxVO
			regItemListVO.setRegistryItemList(regItemList)
			
			regItemVO.setSku(2)
			
			1*cToolsMock.getParentProductForSku("2", true)
			1*cToolsMock.getParentProductForSku("2", true) >> null
			
			regItemListVO.setSkuRegItemVOMap(itemVOMap)
			
	
			1*cToolsMock.getAllValuesForKey("CertonaKeys", _) >> [""]
			
			cToolsMock.sortMxSkubyRegistry(null, _, "rTypeId", "siteId", null, "mxValue") >> ["rvo" : sDetailVolist]
			sDetailVO.setSkuId(null)
			
			
	
			1*cToolsMock.getMxPriceRanges("rTypeId") >> ["rvo","not"]
			
			
			
			1*cToolsMock.getCategoryForRegistry("rTypeId") >> ["mapVO":rCategoryMapVO,"sku1":rCategoryMapVO]
		
		when:
			mridDroplet.service(requestMock, responseMock)
		then:
			1*grManager.isRegistryOwnedByProfile("rId", "regId", "siteId")
			0*requestMock.setParameter("errorMsg", "err_gift_reg_invalid_input_format")
			0*requestMock.serviceParameter("error", requestMock, responseMock)
			1*requestMock.setParameter("promoBox", pBoxVO)
			0*requestMock.setParameter("certonaSkuList", '2')
			1*requestMock.setParameter("emptyOutOfStockListFlag",_)
			1*requestMock.setParameter("categoryBuckets",_)
			1 * requestMock.setParameter('inStockCategoryBuckets', _)
			1 * requestMock.setParameter('totEntries', 0)
			
			1 * requestMock.setParameter("priceRangeList", ["rvo","not"])
			
			1 * requestMock.setParameter('sortSequence', '2')
			1 * requestMock.setParameter('count', 1)
			1 * requestMock.setParameter('notInStockCategoryBuckets', ['rvo':null, 'not':null])
			0*cToolsMock.getParentProductForSku("rvo")
			0*cToolsMock.isSKUBelowLine("siteId", "rvo")

	}
	
	def"service.TC to  Fetch Mx Registry when sortSeq is not in ( price sort sequence and cart sort sequence)  "(){
		given:
			def List sDetailVolist = [sDetailVO]
			//def List regItemList = [regItemVO, regItemVO1]
			def List regItemList = [regItemVO]
			def Map<String, RegistryItemVO> itemVOMap =["rvo" : regItemVO1]
			def Map<String, List> mxSkuReg =["rvo" : sDetailVolist]
			RepositoryItem priceList = Mock()
			RepositoryItem price = Mock()
			
			requestMock.getParameter( ParameterName.getParameterName("registryId" ) ) >> "regId"
			requestMock.getParameter( ParameterName.getParameterName("sortSeq") ) >> "3"
			requestMock.getParameter( ParameterName.getParameterName("view") ) >> null
			requestMock.getParameter( ParameterName.getParameterName("eventTypeCode") ) >> "etCode"
			requestMock.getParameter( ParameterName.getParameterName("startIdx") ) >> "0"
	
			requestMock.getParameter( ParameterName.getParameterName("blkSize") ) >> "6"
			requestMock.getParameter( ParameterName.getParameterName("isGiftGiver") ) >> "false"
			requestMock.getParameter( ParameterName.getParameterName("isAvailForWebPurchaseFlag") ) >> "true"
			requestMock.getParameter("isMxGiftGiver") >> false
			requestMock.getParameter( ParameterName.getParameterName("siteId") ) >> "siteId"
			
			requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
			requestMock.getParameter("mxConversionValue") >> "mxValue"
			
			profileMock.getRepositoryId() >> "rId"
			cToolsMock.getAllValuesForKey("WSDLSiteFlags", "siteId") >> []
			
			1*cToolsMock.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> []
			
			1*grManager.fetchRegistryItems(_) >> regItemListVO
			1*cToolsMock.getRegistryTypes("siteId") >> [rTypeVo, rTypeVo1]
			//Start :check registry type
			rTypeVo.setRegistryCode("etCode")
			rTypeVo1.setRegistryCode("etCode1")
			
			rTypeVo.setRegistryTypeId("rTypeId")
			
			//Start : processItemList
			regItemListVO.setServiceErrorVO(errorVO)
			errorVO.setErrorExists(false)
			1*cToolsMock.getPromoBoxForRegistry("rTypeId") >> pBoxVO
			regItemListVO.setRegistryItemList(regItemList)
			
			regItemVO.setSku(2)
			
			1*cToolsMock.getParentProductForSku("2", true)
			1*cToolsMock.getParentProductForSku("2", true) >> null
			
			regItemListVO.setSkuRegItemVOMap(itemVOMap)
			
	
			1*cToolsMock.getAllValuesForKey("CertonaKeys", _) >> [""]
			
			
			
			
			1*cToolsMock.getCategoryForRegistry("rTypeId") >> ["mapVO":rCategoryMapVO,"sku1":rCategoryMapVO]
		
		when:
			mridDroplet.service(requestMock, responseMock)
		then:
			1*grManager.isRegistryOwnedByProfile("rId", "regId", "siteId")
			0*requestMock.setParameter("errorMsg", "err_gift_reg_invalid_input_format")
			1*requestMock.serviceParameter("error", requestMock, responseMock)
			1*requestMock.setParameter("promoBox", pBoxVO)
			0*requestMock.setParameter("certonaSkuList", '2')
			1*requestMock.setParameter("categoryBuckets",_)
			1 * requestMock.setParameter('inStockCategoryBuckets', _)
			1 * requestMock.setParameter('totEntries', 0)
			1 * requestMock.setParameter('sortSequence', '3')
			1 * requestMock.setParameter('count', 1)
			1 * requestMock.setParameter('notInStockCategoryBuckets', [:])
			0*cToolsMock.getParentProductForSku("rvo")
			0*cToolsMock.isSKUBelowLine("siteId", "rvo")

	}
	
	
	/*********************************** list registry item vo is empty*/
	
	def"service.TC to  Fetch Mx Registry when list registry item vo is empty   "(){
		given:
			def List regItemList = []
			
			String rType = "rTypeId"
			requestMock.getParameter( ParameterName.getParameterName("sortSeq") ) >> "2"
			commonMethodForCheckWithNull(rType,regItemList)			
			1*cToolsMock.getPriceRanges("rTypeId", null) >> ["range"]
			
					
		when:
			mridDroplet.service(requestMock, responseMock)
		then:
			1*grManager.isRegistryOwnedByProfile("rId", "regId", "siteId")
			0*requestMock.setParameter("errorMsg", "err_gift_reg_invalid_input_format")
			1*requestMock.setParameter("promoBox", pBoxVO)
			0*requestMock.setParameter("certonaSkuList", '2')
			0*requestMock.setParameter("categoryBuckets",_)
			0 * requestMock.setParameter('inStockCategoryBuckets', _)
			0 * requestMock.setParameter('notInStockCategoryBuckets', [:])
			0*cToolsMock.getParentProductForSku("rvo")
			0*cToolsMock.isSKUBelowLine("siteId", "rvo")
			
			1 * requestMock.setParameter("priceRangeList",["range"])
			1 * requestMock.setParameter("categoryBuckets",['range': null])
			1 * requestMock.setParameter("emptyList","true")
			1 * requestMock.setParameter('sortSequence', '2')
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
			

	}
	
	def"service.TC to  Fetch Mx Registry when list registry item vo is empty  and sort seq is not price sort  "(){
		given:
			String rType = ""
			def List regItemList = []
			requestMock.getParameter( ParameterName.getParameterName("sortSeq") ) >> "3"
			commonMethodForCheckWithNull(rType,regItemList)
			cToolsMock.getCategoryForRegistry("") >> ["cat1" : rCategoryMapVO, "cat2" : rCategoryMapVO]
			
					
		when:
			mridDroplet.service(requestMock, responseMock)
		then:
			1*grManager.isRegistryOwnedByProfile("rId", "regId", "siteId")
			1 * requestMock.setParameter("categoryVOMap", ["cat1" : rCategoryMapVO, "cat2" : rCategoryMapVO])
            1 * requestMock.setParameter("categoryBuckets", ["cat1" : null, "cat2" : null])		
			1 * requestMock.setParameter("emptyList","true")
			1 * requestMock.setParameter('sortSequence', '3')
			0 * cToolsMock.getPriceRanges("rTypeId", null)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
			

	}
	
	def"service.TC to  Fetch Mx Registry when list registry item vo is null   "(){
		given:
			String rType = ""
			def List regItemList = null
			requestMock.getParameter( ParameterName.getParameterName("sortSeq") ) >> "3"
			commonMethodForCheckWithNull(rType,regItemList)
			cToolsMock.getCategoryForRegistry("") >> ["cat1" : rCategoryMapVO, "cat2" : rCategoryMapVO]
			
					
		when:
			mridDroplet.service(requestMock, responseMock)
		then:
			1*grManager.isRegistryOwnedByProfile("rId", "regId", "siteId")
			1 * requestMock.setParameter("categoryVOMap", ["cat1" : rCategoryMapVO, "cat2" : rCategoryMapVO])
			1 * requestMock.setParameter("categoryBuckets", ["cat1" : null, "cat2" : null])
			1 * requestMock.setParameter("emptyList","true")
			1 * requestMock.setParameter('sortSequence', '3')
			0 * cToolsMock.getPriceRanges("rTypeId", null)
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
			

	}
	
	private commonMethodForCheckWithNull(String rType, List regItemList){
		
		requestMock.getParameter( ParameterName.getParameterName("registryId" ) ) >> "regId"
		requestMock.getParameter( ParameterName.getParameterName("view") ) >> null
		requestMock.getParameter( ParameterName.getParameterName("eventTypeCode") ) >> "etCode"
		requestMock.getParameter( ParameterName.getParameterName("startIdx") ) >> "0"

		requestMock.getParameter( ParameterName.getParameterName("blkSize") ) >> "6"
		requestMock.getParameter( ParameterName.getParameterName("isGiftGiver") ) >> "false"
		requestMock.getParameter( ParameterName.getParameterName("isAvailForWebPurchaseFlag") ) >> "true"
		requestMock.getParameter("isMxGiftGiver") >> false
		requestMock.getParameter( ParameterName.getParameterName("siteId") ) >> "siteId"
		
		requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		requestMock.getParameter("mxConversionValue") >> "mxValue"
		
		profileMock.getRepositoryId() >> "rId"
		cToolsMock.getAllValuesForKey("WSDLSiteFlags", "siteId") >> []
		
		1*cToolsMock.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> []
		
		1*grManager.fetchRegistryItems(_) >> regItemListVO
		1*cToolsMock.getRegistryTypes("siteId") >> [rTypeVo, rTypeVo1]
		//Start :check registry type
		rTypeVo.setRegistryCode("etCode")
		rTypeVo1.setRegistryCode("etCode1")
		rTypeVo.setRegistryTypeId(rType)
		
		
		//Start : processItemList
		regItemListVO.setServiceErrorVO(errorVO)
		errorVO.setErrorExists(false)
		1*cToolsMock.getPromoBoxForRegistry(rType) >> pBoxVO
		regItemListVO.setRegistryItemList(regItemList)

	}
	
	def"service.TC to  Fetch Mx Registry when registryItemsListVO is null"(){
		given:
			requestMock.getParameter( ParameterName.getParameterName("registryId" ) ) >> "regId"
			requestMock.getParameter( ParameterName.getParameterName("sortSeq") ) >> null
			requestMock.getParameter( ParameterName.getParameterName("view") ) >> "12"
			requestMock.getParameter( ParameterName.getParameterName("eventTypeCode") ) >> "etCode"
			requestMock.getParameter( ParameterName.getParameterName("startIdx") ) >> "0"
	
			requestMock.getParameter( ParameterName.getParameterName("blkSize") ) >> "6"
			requestMock.getParameter( ParameterName.getParameterName("isGiftGiver") ) >> "true"
			requestMock.getParameter( ParameterName.getParameterName("isAvailForWebPurchaseFlag") ) >> "true"
			requestMock.getParameter("isMxGiftGiver") >> true
			requestMock.getParameter( ParameterName.getParameterName("siteId") ) >> "siteId"
			
			requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
			profileMock.getRepositoryId() >> "rId"
			2*cToolsMock.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["wsdKey"]
			
			1*grManager.fetchRegistryItems(_) >> null
			1*cToolsMock.getRegistryTypes("siteId") >> [rTypeVo]
			//Start :check registry type
			rTypeVo.setRegistryCode("etCode")
			
			rTypeVo.setRegistryTypeId("rTypeId")

		when:
			mridDroplet.service(requestMock, responseMock)
		then:
			0*requestMock.setParameter("errorMsg", "err_gift_reg_invalid_input_format")
			0 * requestMock.setParameter('inStockCategoryBuckets', _)
			0 * requestMock.setParameter('notInStockCategoryBuckets', _)
			0*cToolsMock.isSKUBelowLine("siteId", _)
			0*grManager.isRegistryOwnedByProfile("rId", "regId", "siteId")

	}
	
	def"service.TC for BBBBusinessException"(){
		given:
	        commonMethodForExceptionScenario()	
			1*cToolsMock.getRegistryTypes("siteId") >> {throw new BBBBusinessException("exception") }

		when:
			mridDroplet.service(requestMock, responseMock)
		then:
			0*requestMock.setParameter("errorMsg", "err_gift_reg_invalid_input_format")
			0 * requestMock.setParameter('inStockCategoryBuckets', _)
			0 * requestMock.setParameter('notInStockCategoryBuckets', _)
			0*cToolsMock.isSKUBelowLine("siteId", _)
			0*grManager.isRegistryOwnedByProfile("rId", "regId", "siteId")
			1 * requestMock.serviceParameter("error", requestMock, responseMock)

	}
	
	def"service.TC for BBBSystemException"(){
		given:
			commonMethodForExceptionScenario()
			1*cToolsMock.getRegistryTypes("siteId") >> {throw new BBBSystemException("exception") }

		when:
			mridDroplet.service(requestMock, responseMock)
		then:
			0*requestMock.setParameter("errorMsg", "err_gift_reg_invalid_input_format")
			0 * requestMock.setParameter('inStockCategoryBuckets', _)
			0 * requestMock.setParameter('notInStockCategoryBuckets', _)
			0*cToolsMock.isSKUBelowLine("siteId", _)
			0*grManager.isRegistryOwnedByProfile("rId", "regId", "siteId")
			1 * requestMock.serviceParameter("error", requestMock, responseMock)

	}
	
	private commonMethodForExceptionScenario(){
		requestMock.getParameter( ParameterName.getParameterName("registryId" ) ) >> "regId"
		requestMock.getParameter( ParameterName.getParameterName("sortSeq") ) >> null
		requestMock.getParameter( ParameterName.getParameterName("view") ) >> "12"
		requestMock.getParameter( ParameterName.getParameterName("eventTypeCode") ) >> "etCode"
		requestMock.getParameter( ParameterName.getParameterName("startIdx") ) >> "0"

		requestMock.getParameter( ParameterName.getParameterName("blkSize") ) >> "6"
		requestMock.getParameter( ParameterName.getParameterName("isGiftGiver") ) >> "true"
		requestMock.getParameter( ParameterName.getParameterName("isAvailForWebPurchaseFlag") ) >> "true"
		requestMock.getParameter("isMxGiftGiver") >> true
		requestMock.getParameter( ParameterName.getParameterName("siteId") ) >> "siteId"
		
		requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		profileMock.getRepositoryId() >> "rId"
		2*cToolsMock.getAllValuesForKey("WSDLKeys","WebServiceUserToken") >> ["wsdKey"]
		1*grManager.fetchRegistryItems(_) >> regItemListVO
	}
	
}
