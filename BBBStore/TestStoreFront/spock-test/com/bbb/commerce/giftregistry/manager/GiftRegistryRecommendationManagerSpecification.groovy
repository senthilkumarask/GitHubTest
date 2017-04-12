package com.bbb.commerce.giftregistry.manager

import java.util.List
import java.util.Map
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO
import com.bbb.commerce.catalog.vo.RegistryCategoryMapVO;
import com.bbb.commerce.catalog.vo.RegistryTypeVO
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.giftregistry.bean.AddItemsBean;
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools
import com.bbb.commerce.giftregistry.tool.RecommendationRegistryProductVO
import com.bbb.commerce.giftregistry.tool.RegistryBulkNotificationVO
import com.bbb.commerce.giftregistry.tool.SortRecommRegistryVO
import com.bbb.commerce.giftregistry.vo.ManageRegItemsResVO
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.email.BBBTemplateEmailSender
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.search.bean.result.BBBDynamicPriceSkuVO
import com.bbb.utils.BBBUtility
import atg.commerce.pricing.priceLists.PriceListManager
import atg.nucleus.naming.ComponentName;
import atg.repository.MutableRepository
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile
import atg.userprofiling.email.TemplateEmailException
import atg.userprofiling.email.TemplateEmailInfo
import atg.userprofiling.email.TemplateEmailInfoImpl
import spock.lang.specification.BBBExtendedSpec;

class GiftRegistryRecommendationManagerSpecification extends BBBExtendedSpec{

	GiftRegistryRecommendationManager manager
	BBBTemplateEmailSender sender =Mock()
	GiftRegistryTools giftTools =Mock()
	BBBSessionBean bean =Mock()
	PriceListManager pman =Mock()
	MutableRepository mRep =Mock()
	Profile profile =Mock()
	BBBCatalogTools cTools =Mock()
	String registryId ="regId"
	String tabId = "tabId"
	String sortOption = "salePrice"
	String eventTypeCode = "eventTypeCode"

	def setup(){
		manager = new GiftRegistryRecommendationManager()
		manager.setGiftRegistryTools(giftTools)
		manager.setEmailSender(sender)
		manager.setPriceListManager(pman)
		manager.setProfilePriceListPropertyName("")
		manager.setGiftRepository(mRep)
		manager.setSessionBean(bean)
		manager.setRegistrantScheduledBulkEmail("bulkEmail")
		manager.setTemplateUrl("templateURL")
		manager.setRegistryURL("registryURL")
		BBBUtility.setCatalogTools(cTools)
		requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
	}

	private setParametrsForSpy(){
		manager =Spy()
		manager.setGiftRegistryTools(giftTools)
		manager.setCatalogTools(cTools)
		manager.extractSiteId() >> "tbs"
	}

	def"getRegistryRecommendationItemsForTab, when recommendationsVO List is empty(sortRecommendations)"(){

		given:
		setParametrsForSpy()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId, tabId) >> []

		//for sortRecommendations inside getRegistryRecommendationItemsForTab
		0*cTools.getRegistryTypes("tbs")
		0*cTools.getCategoryForRegistry(_)
		0*cTools.getBazaarVoiceDetails(_)
		//ends sortRecommendations

		when:
		List<RecommendationRegistryProductVO> List = manager.getRegistryRecommendationItemsForTab(registryId,tabId, null, eventTypeCode,requestMock)

		then:
		List.isEmpty() == true
		1*requestMock.setParameter("tabId", tabId)
		0*requestMock.setParameter("categoryBucketsForRecommendation",[:])
		0*requestMock.setParameter("groupByFlag", _);
		0*requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST,null)
	}

	def"getRegistryRecommendationItemsForTab, sorts the tab when sortOption is null(sortRecommendations)"(){

		given:
		setParametrsForSpy()
		RecommendationRegistryProductVO vo1 = Mock()
		RecommendationRegistryProductVO vo2 = Mock()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId, tabId) >> [vo1,vo2]

		//for sortRecommendations inside getRegistryRecommendationItemsForTab
		1*vo1.getRecommendedDate() >> new Date()
		1*vo2.getRecommendedDate() >> new Date()
		0*cTools.getRegistryTypes("tbs")
		0*cTools.getCategoryForRegistry(_)
		//ends sortRecommendations

		when:
		List<RecommendationRegistryProductVO> List = manager.getRegistryRecommendationItemsForTab(registryId,tabId, null, eventTypeCode,requestMock)

		then:
		List.size() ==2
		1*requestMock.setParameter("tabId", tabId)
		1*requestMock.setParameter("categoryBucketsForRecommendation",[:])
		1*requestMock.setParameter("groupByFlag", "date");
		1*requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST,null)
	}

	def"getRegistryRecommendationItemsForTab, sorts the recommendation tab based on sale price(sortRecommendations), priceSorting method is called with country as US and productId as null(checkPrice)"(){

		given:
		setParametrsForSpy()
		RecommendationRegistryProductVO vo1 = Mock()
		RecommendationRegistryProductVO vo2 = Mock()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId, tabId) >> [vo1,vo2]

		//for sortRecommendations inside getRegistryRecommendationItemsForTab
		//for checkRegistryType  inside sortRecommendations
		RegistryTypeVO rVo1 = new RegistryTypeVO()
		RegistryTypeVO rVo2 = new RegistryTypeVO()
		1*cTools.getRegistryTypes("tbs") >> [rVo1,rVo2]
		rVo1.setRegistryCode("Code")
		rVo2.setRegistryCode("eventTypeCode")
		rVo2.setRegistryTypeId("typeId")
		// end checkRegistryType

		Map<String, RegistryCategoryMapVO> getCategoryForRegistry = new HashMap()
		1*cTools.getCategoryForRegistry("typeId") >> getCategoryForRegistry

		//for priceSorting inside sortRecommendations
		BBBSessionBean sessionBean = Mock()
		SKUDetailVO dvo1 = new SKUDetailVO()
		4*vo1.getSkuId() >> "id1"
		4*vo2.getSkuId() >> "id2"

		//for checkPrice inside priceSorting
		1*cTools.getParentProductForSku("id1", true) >> null
		//ends checkPrice

		2*requestMock.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBean
		Map<String,String> sessionMap = new HashMap()
		2*sessionBean.getValues() >> sessionMap
		sessionMap.put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT, "US")
		1*cTools.getPriceRanges("typeId","US") >> ["str1"]
		1*manager.getNewKeyRange("str1") >> "4+5"

		Map<String, List<SKUDetailVO>> categoryMap = new HashMap()
		categoryMap.put("1+2", [dvo1])
		1*cTools.sortSkubyRegistry(null, _, "typeId", "tbs",null, "US") >> categoryMap
		dvo1.setSkuId("id1")
		//ends priceSorting
		//ends sortRecommendations

		BazaarVoiceProductVO bvo = new BazaarVoiceProductVO()
		1*vo1.getProductId() >> "prodId"
		1*cTools.getBazaarVoiceDetails("prodId") >> bvo
		//ends getRegistryRecommendationItemsForTab

		when:
		List<RecommendationRegistryProductVO> voList = manager.getRegistryRecommendationItemsForTab(registryId,tabId, sortOption, eventTypeCode,requestMock)

		then:
		voList == [vo1,vo2]
		1*requestMock.setParameter("tabId", tabId)
		1*requestMock.setParameter("categoryBucketsForRecommendation",["1+2":[vo1] ,"4+5":null]);
		1*requestMock.setParameter("groupByFlag", "price");
		1*requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST,_)
		1*vo1.setBvProductVO(bvo)
		1*vo1.setPrice("null")
	}

	def"getRegistryRecommendationItemsForTab, sorts the recommendation tab based on sale price(sortRecommendations), priceSorting method is called with country as Mexico, productId, salePrice and ListPrice are not null(checkPrice)"(){

		given:
		setParametrsForSpy()
		RecommendationRegistryProductVO vo1 = Mock()
		RecommendationRegistryProductVO vo2 = Mock()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId, tabId) >> [vo1,vo2]

		//for sortRecommendations inside getRegistryRecommendationItemsForTab
		//for checkRegistryType  inside sortRecommendations
		RegistryTypeVO rVo1 = new RegistryTypeVO()
		RegistryTypeVO rVo2 = new RegistryTypeVO()
		1*cTools.getRegistryTypes("tbs") >> [rVo1,rVo2]
		rVo1.setRegistryCode("Code")
		rVo2.setRegistryCode("eventTypeCode")
		rVo2.setRegistryTypeId("typeId")
		// end checkRegistryType

		Map<String, RegistryCategoryMapVO> getCategoryForRegistry = new HashMap()
		1*cTools.getCategoryForRegistry("typeId") >> getCategoryForRegistry

		//for priceSorting inside sortRecommendations
		BBBSessionBean sessionBean = Mock()
		SKUDetailVO dvo1 = new SKUDetailVO()
		6*vo1.getSkuId() >> "id1"
		8*vo2.getSkuId() >> "id2"

		//for checkPrice inside priceSorting
		SKUDetailVO sdvo = new SKUDetailVO()
		SKUDetailVO sdvo1 = new SKUDetailVO()
		BBBDynamicPriceSkuVO dynvo = new BBBDynamicPriceSkuVO()
		BBBDynamicPriceSkuVO dynvo1 = new BBBDynamicPriceSkuVO()

		1*cTools.getParentProductForSku("id1", true) >> "productId"
		3*vo1.getJdaRetailPrice() >> 50
		vo1.getsKUDetailVO() >> sdvo
		sdvo.setDynPriceInfoAlreadyFetched(false)
		1*cTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY) >>["true"]
		1*cTools.getDynamicPriceSKUVO(_, _) >> dynvo
		dynvo.setInCartFlag(false)

		1*cTools.getParentProductForSku("id2", true) >> "productId2"
		vo2.getJdaRetailPrice() >> 0.0
		1*cTools.getSalePrice("productId2", "id2") >> 100.0
		1*cTools.getListPrice("productId2", "id2") >> 200.0
		1*vo2.getSkuIncartPrice() >> 500.0
		vo2.getsKUDetailVO() >> sdvo1
		sdvo1.setDynPriceInfoAlreadyFetched(false)
		1*cTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY) >>["true"]
		1*cTools.getDynamicPriceSKUVO(_, _) >> dynvo1
		dynvo1.setInCartFlag(true)
		//ends checkPrice

		2*requestMock.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBean
		Map<String,String> sessionMap = new HashMap()
		2*sessionBean.getValues() >> sessionMap
		sessionMap.put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT, "MX")
		1*cTools.getPriceRanges("typeId","MX") >> ["s.t.r.1"]
		0*manager.getNewKeyRange("s.t.r.1")

		Map<String, List<SKUDetailVO>> categoryMap = new HashMap()
		categoryMap.put("1+2", [dvo1])
		1*cTools.sortSkubyRegistry(null, _, "typeId", "tbs",null, "MX") >> categoryMap
		dvo1.setSkuId("id1")
		//ends priceSorting
		//ends sortRecommendations

		BazaarVoiceProductVO bvo = new BazaarVoiceProductVO()
		1*vo1.getProductId() >> "prodId"
		1*cTools.getBazaarVoiceDetails("prodId") >> bvo
		//ends getRegistryRecommendationItemsForTab

		when:
		List<RecommendationRegistryProductVO> voList = manager.getRegistryRecommendationItemsForTab(registryId,tabId, sortOption, eventTypeCode,requestMock)

		then:
		voList == [vo1,vo2]
		1*requestMock.setParameter("tabId", tabId)
		1*requestMock.setParameter("categoryBucketsForRecommendation",["1+2":[vo1] ,"s.t.r.1":null]);
		1*requestMock.setParameter("groupByFlag", "price");
		1*requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST,_)
		1*vo1.setBvProductVO(bvo)
		1*vo1.setPrice("50.0")
	}

	def"getRegistryRecommendationItemsForTab, sorts the recommendation tab on sale price(sortRecommendations),priceSorting method is called with priceRangeList null, sale and ListPrice not null(checkPrice)"(){

		given:
		setParametrsForSpy()
		RecommendationRegistryProductVO vo1 = Mock()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId, tabId) >> [vo1]

		//for sortRecommendations inside getRegistryRecommendationItemsForTab
		//for checkRegistryType  inside sortRecommendations
		RegistryTypeVO rVo1 = new RegistryTypeVO()
		1*cTools.getRegistryTypes("tbs") >> [rVo1]
		rVo1.setRegistryCode("eventTypeCode")
		rVo1.setRegistryTypeId("typeId")
		// end checkRegistryType

		Map<String, RegistryCategoryMapVO> getCategoryForRegistry = new HashMap()
		1*cTools.getCategoryForRegistry("typeId") >> getCategoryForRegistry

		//for priceSorting inside sortRecommendations
		BBBSessionBean sessionBean = Mock()
		SKUDetailVO dvo1 = new SKUDetailVO()
		6*vo1.getSkuId() >> "id1"

		//for checkPrice inside priceSorting
		SKUDetailVO sdvo = new SKUDetailVO()
		BBBDynamicPriceSkuVO dynvo = new BBBDynamicPriceSkuVO()
		1*cTools.getParentProductForSku("id1", true) >> "productId"
		3*vo1.getJdaRetailPrice() >> 100.0
		vo1.getsKUDetailVO() >> sdvo
		sdvo.setDynPriceInfoAlreadyFetched(false)
		1*cTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY) >>["true"]
		1*cTools.getDynamicPriceSKUVO(_, _) >> dynvo
		dynvo.setInCartFlag(false)
		//ends checkPrice

		2*requestMock.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBean
		Map<String,String> sessionMap = new HashMap()
		2*sessionBean.getValues() >> sessionMap
		sessionMap.put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT, "IND")
		1*cTools.getPriceRanges("typeId","IND") >> null

		Map<String, List<SKUDetailVO>> categoryMap = new HashMap()
		categoryMap.put("1+2", [dvo1])
		1*cTools.sortSkubyRegistry(null, _, "typeId", "tbs",null, "IND") >> categoryMap
		dvo1.setSkuId("id1")
		1*manager.getNewKeyRange("1+2")
		//ends priceSorting
		//ends sortRecommendations

		BazaarVoiceProductVO bvo = new BazaarVoiceProductVO()
		1*vo1.getProductId() >> "prodId"
		1*cTools.getBazaarVoiceDetails("prodId") >> bvo
		//ends getRegistryRecommendationItemsForTab

		when:
		List<RecommendationRegistryProductVO> voList = manager.getRegistryRecommendationItemsForTab(registryId,tabId, sortOption, eventTypeCode,requestMock)

		then:
		voList == [vo1]
		1*requestMock.setParameter("tabId", tabId)
		1*requestMock.setParameter("categoryBucketsForRecommendation",["1+2":[vo1]]);
		1*requestMock.setParameter("groupByFlag", "price");
		1*requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST,_)
		1*vo1.setBvProductVO(bvo)
		1*vo1.setPrice("100.0")
	}

	def"getRegistryRecommendationItemsForTab, sorts the tab based on sale price(sortRecommendations), priceSorting method is called with country empty, salePrice and ListPrice are null(checkPrice)"(){

		given:
		setParametrsForSpy()
		RecommendationRegistryProductVO vo1 = Mock()
		RecommendationRegistryProductVO vo2 = Mock()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId, tabId) >> [vo1,vo2]

		//for sortRecommendations inside getRegistryRecommendationItemsForTab
		//for checkRegistryType  inside sortRecommendations
		RegistryTypeVO rVo1 = new RegistryTypeVO()
		RegistryTypeVO rVo2 = new RegistryTypeVO()
		1*cTools.getRegistryTypes("tbs") >> [rVo1,rVo2]
		rVo1.setRegistryCode("Code")
		rVo2.setRegistryCode("eventTypeCode")
		rVo2.setRegistryTypeId("typeId")
		// end checkRegistryType

		Map<String, RegistryCategoryMapVO> getCategoryForRegistry = new HashMap()
		1*cTools.getCategoryForRegistry("typeId") >> getCategoryForRegistry

		//for priceSorting inside sortRecommendations
		BBBSessionBean sessionBean = Mock()
		SKUDetailVO dvo1 = new SKUDetailVO()
		6*vo1.getSkuId() >> "id1"
		8*vo2.getSkuId() >> "id2"

		//for checkPrice inside priceSorting
		SKUDetailVO sdvo = new SKUDetailVO()
		SKUDetailVO sdvo1 = new SKUDetailVO()
		BBBDynamicPriceSkuVO dynvo = new BBBDynamicPriceSkuVO()
		BBBDynamicPriceSkuVO dynvo1 = new BBBDynamicPriceSkuVO()

		1*cTools.getParentProductForSku("id1", true) >> "productId"
		1*vo1.getJdaRetailPrice() >> 0.0
		1*cTools.getSalePrice("productId", "id1") >> null
		1*cTools.getListPrice("productId", "id1") >> null
		vo1.getsKUDetailVO() >> sdvo
		sdvo.setDynPriceInfoAlreadyFetched(false)
		1*cTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY) >>["true"]
		1*cTools.getDynamicPriceSKUVO(_, _) >> dynvo
		dynvo.setInCartFlag(false)

		1*cTools.getParentProductForSku("id2", true) >> "productId2"
		vo2.getJdaRetailPrice() >> 0.0
		1*cTools.getSalePrice("productId2", "id2") >> 0.0
		1*cTools.getListPrice("productId2", "id2") >> 200.0
		1*vo2.getSkuIncartPrice() >> 500.0
		vo2.getsKUDetailVO() >> sdvo
		//ends checkPrice

		2*requestMock.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBean
		Map<String,String> sessionMap = new HashMap()
		2*sessionBean.getValues() >> sessionMap
		sessionMap.put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT, "")
		1*cTools.getPriceRanges("typeId","") >> ["1+2"]
		0*manager.getNewKeyRange("1+2")

		Map<String, List<SKUDetailVO>> categoryMap = new HashMap()
		categoryMap.put("1+2", [dvo1])
		1*cTools.sortSkubyRegistry(null, _, "typeId", "tbs",null, "") >> categoryMap
		dvo1.setSkuId("id1")
		//ends priceSorting
		//ends sortRecommendations

		BazaarVoiceProductVO bvo = new BazaarVoiceProductVO()
		1*vo1.getProductId() >> "prodId"
		1*cTools.getBazaarVoiceDetails("prodId") >> bvo
		//ends getRegistryRecommendationItemsForTab

		when:
		List<RecommendationRegistryProductVO> voList = manager.getRegistryRecommendationItemsForTab(registryId,tabId, sortOption, eventTypeCode,requestMock)

		then:
		voList == [vo1,vo2]
		1*requestMock.setParameter("tabId", tabId)
		1*requestMock.setParameter("categoryBucketsForRecommendation",["1+2":[vo1]]);
		1*requestMock.setParameter("groupByFlag", "price");
		1*requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST,_)
		1*vo1.setBvProductVO(bvo)
		1*vo1.setPrice("null")
	}

	def"getRegistryRecommendationItemsForTab, sorts the recommendation tab based on ListPrice(sortRecommendations), priceSorting method is called with country as US and productId as null(checkPrice)"(){

		given:
		setParametrsForSpy()
		String sortOption = "listPrice"
		RecommendationRegistryProductVO vo1 = Mock()
		RecommendationRegistryProductVO vo2 = Mock()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId, tabId) >> [vo1,vo2]

		//for sortRecommendations inside getRegistryRecommendationItemsForTab
		1*vo1.getRecommendedDate() >> new Date()
		1*vo2.getRecommendedDate() >> new Date()

		//for checkRegistryType  inside sortRecommendations
		RegistryTypeVO rVo2 = new RegistryTypeVO()
		1*cTools.getRegistryTypes("tbs") >> [rVo2]
		rVo2.setRegistryCode("eventTypeCode")
		rVo2.setRegistryTypeId("typeId")
		// end checkRegistryType

		Map<String, RegistryCategoryMapVO> getCategoryForRegistry = new HashMap()
		1*cTools.getCategoryForRegistry("typeId") >> getCategoryForRegistry

		//for priceSorting inside sortRecommendations
		BBBSessionBean sessionBean = Mock()
		SKUDetailVO dvo1 = new SKUDetailVO()
		4*vo1.getSkuId() >> "id1"
		4*vo2.getSkuId() >> "id2"

		//for checkPrice inside priceSorting
		1*cTools.getParentProductForSku("id1", true) >> null
		//ends checkPrice

		2*requestMock.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBean
		Map<String,String> sessionMap = new HashMap()
		2*sessionBean.getValues() >> sessionMap
		sessionMap.put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT, "US")
		1*cTools.getPriceRanges("typeId","US") >> ["str1"]
		1*manager.getNewKeyRange("str1") >> "4+5"

		Map<String, List<SKUDetailVO>> categoryMap = new HashMap()
		categoryMap.put("1+2", [dvo1])
		1*cTools.sortSkubyRegistry(null, _, "typeId", "tbs",null, "US") >> categoryMap
		dvo1.setSkuId("id1")
		//ends priceSorting
		//ends sortRecommendations

		BazaarVoiceProductVO bvo = new BazaarVoiceProductVO()
		1*vo1.getProductId() >> "prodId"
		1*cTools.getBazaarVoiceDetails("prodId") >> bvo
		//ends getRegistryRecommendationItemsForTab

		when:
		List<RecommendationRegistryProductVO> voList = manager.getRegistryRecommendationItemsForTab(registryId,tabId, sortOption, eventTypeCode,requestMock)

		then:
		voList == [vo1,vo2]
		1*requestMock.setParameter("tabId", tabId)
		1*requestMock.setParameter("categoryBucketsForRecommendation",["1+2":[vo1] ,"4+5":null]);
		1*requestMock.setParameter("groupByFlag", "price");
		1*requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST,_)
		1*vo1.setBvProductVO(bvo)
		1*vo1.setPrice("null")
	}


	def"getRegistryRecommendationItemsForTab, sorts the tab based on Date(sortRecommendations) when tabId is ACCEPTED_TAB"(){

		given:
		setParametrsForSpy()
		String sortOption ="date"
		String tabId =BBBGiftRegistryConstants.ACCEPTED_TAB
		RecommendationRegistryProductVO vo1 = new RecommendationRegistryProductVO()
		RecommendationRegistryProductVO vo2 = new RecommendationRegistryProductVO()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId, tabId) >> [vo1,vo2]

		//for sortRecommendations inside getRegistryRecommendationItemsForTab
		1*cTools.getRegistryTypes("tbs") >> []

		0*cTools.getCategoryForRegistry("")
		vo1.setRecenltyModifiedDate(new Date())
		vo2.setRecenltyModifiedDate(new Date())
		vo1.setFirstName("vo1")
		vo2.setFirstName("vo2")
		//ends sortRecommendations
		//ends getRegistryRecommendationItemsForTab

		when:
		List<RecommendationRegistryProductVO> voList = manager.getRegistryRecommendationItemsForTab(registryId,tabId, sortOption, eventTypeCode,requestMock)

		then:
		voList.size() ==2
		1*requestMock.setParameter("tabId", tabId)
		1*requestMock.setParameter("categoryBucketsForRecommendation",[:])
		1*requestMock.setParameter("groupByFlag", "date");
		1*requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST,null)
		0*vo1.setBvProductVO(_)
		0*vo1.setPrice(_)
	}

	def"getRegistryRecommendationItemsForTab, sorts the tab based on Date(sortRecommendations) when tabID is DECLINED_TAB"(){

		given:
		setParametrsForSpy()
		String sortOption ="date"
		String tabId =BBBGiftRegistryConstants.DECLINED_TAB
		RecommendationRegistryProductVO vo1 = new RecommendationRegistryProductVO()
		RecommendationRegistryProductVO vo2 = new RecommendationRegistryProductVO()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId, tabId) >> [vo1,vo2]

		//for sortRecommendations inside getRegistryRecommendationItemsForTab
		1*cTools.getRegistryTypes("tbs") >> []

		0*cTools.getCategoryForRegistry("")
		vo1.setRecenltyModifiedDate(new Date())
		vo2.setRecenltyModifiedDate(new Date())
		vo1.setFirstName("vo1")
		vo2.setFirstName("vo2")
		//ends sortRecommendations
		//ends getRegistryRecommendationItemsForTab

		when:
		List<RecommendationRegistryProductVO> List = manager.getRegistryRecommendationItemsForTab(registryId,tabId, sortOption, eventTypeCode,requestMock)

		then:
		List.size() ==2
		1*requestMock.setParameter("tabId", tabId)
		1*requestMock.setParameter("categoryBucketsForRecommendation",[:])
		1*requestMock.setParameter("groupByFlag", "date");
		1*requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST,null)
		0*vo1.setBvProductVO(_)
		0*vo1.setPrice(_)
	}

	def"getRegistryRecommendationItemsForTab, sorts the tab based on Date(sortRecommendations) when tabID is neither Accepted nor declined Tab"(){

		given:
		setParametrsForSpy()
		String sortOption ="date"
		RecommendationRegistryProductVO vo1 = new RecommendationRegistryProductVO()
		RecommendationRegistryProductVO vo2 = new RecommendationRegistryProductVO()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId, tabId) >> [vo1,vo2]

		//for sortRecommendations inside getRegistryRecommendationItemsForTab
		1*cTools.getRegistryTypes("tbs") >> []
		0*cTools.getCategoryForRegistry("")
		vo1.setRecommendedDate(new Date())
		vo2.setRecommendedDate(new Date())
		vo1.setFirstName("vo1")
		vo2.setFirstName("vo2")
		//ends sortRecommendations
		//ends getRegistryRecommendationItemsForTab

		when:
		List<RecommendationRegistryProductVO> vList = manager.getRegistryRecommendationItemsForTab(registryId,tabId, sortOption, eventTypeCode,requestMock)

		then:
		vList.size() == 2
		1*requestMock.setParameter("tabId", tabId)
		1*requestMock.setParameter("categoryBucketsForRecommendation",[:])
		1*requestMock.setParameter("groupByFlag", "date");
		1*requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST,null)
		0*vo1.setBvProductVO(_)
		0*vo1.setPrice(_)
	}

	def"getRegistryRecommendationItemsForTab, sorts the tab based on CATEGORY(sortRecommendations) when jdaCategory in not empty(jdaCategorySorting)"(){

		given:
		setParametrsForSpy()
		String sortOption ="category"
		RecommendationRegistryProductVO vo1 = Mock()
		RecommendationRegistryProductVO vo2 = Mock()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId, tabId) >> [vo1,vo2]

		//for sortRecommendations inside getRegistryRecommendationItemsForTab
		// for jdaCategorySorting inside sortRecommendations
		SKUDetailVO dvo1 = new SKUDetailVO()
		RegistryCategoryMapVO mapVo = new RegistryCategoryMapVO()
		RegistryCategoryMapVO mapVo1 = new RegistryCategoryMapVO()
		RegistryCategoryMapVO mapVo2 = new RegistryCategoryMapVO()

		1*cTools.getRegistryTypes("tbs") >> []
		Map<String, RegistryCategoryMapVO> getCategoryForRegistry = new HashMap()
		1*cTools.getCategoryForRegistry("") >> getCategoryForRegistry
		manager.getSkuFromRecommendations([vo1,vo2]) >> ["id1","id2"]

		Map<String, List<SKUDetailVO>> categoryMap = new HashMap()
		categoryMap.put("1", [dvo1])
		1*cTools.sortSkubyRegistry(["id1","id2"], _, "", "tbs",BBBCatalogConstants.CATEGORY_SORT_TYPE, null) >> categoryMap

		1*vo1.getSkuId() >> "id1"
		1*vo2.getSkuId() >> "id2"
		dvo1.setSkuId("id2")

		getCategoryForRegistry.put("1",mapVo)
		getCategoryForRegistry.put("5",mapVo1)
		getCategoryForRegistry.put("6",mapVo2)
		mapVo.setRecommendedCatFlag(true)
		mapVo1.setRecommendedCatFlag(true)
		mapVo2.setRecommendedCatFlag(false)
		// ends jdaCategorySorting inside sortRecommendations
		//ends sortRecommendations

		when:
		List<RecommendationRegistryProductVO> List = manager.getRegistryRecommendationItemsForTab(registryId,tabId, sortOption, eventTypeCode,requestMock)

		then:
		List.size() ==2
		1*requestMock.setParameter("tabId", tabId)
		1*requestMock.setParameter("categoryBucketsForRecommendation",["1":[vo2],"5":null])
		1*requestMock.setParameter("groupByFlag", "category");
		1*requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST,null)
		1*vo2.setJdaCategory("1")
	}

	def"getRegistryRecommendationItemsForTab, sorts the tab based on CATEGORY(sortRecommendations) when jdaCategory in empty(jdaCategorySorting)"(){

		given:
		setParametrsForSpy()
		String sortOption ="category"
		RecommendationRegistryProductVO vo1 = Mock()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId, tabId) >> [vo1]

		//for sortRecommendations inside getRegistryRecommendationItemsForTab
		//for jdaCategorySorting inside sortRecommendations
		SKUDetailVO dvo1 = new SKUDetailVO()
		RegistryCategoryMapVO mapVo = new RegistryCategoryMapVO()

		1*cTools.getRegistryTypes("tbs") >> []
		Map<String, RegistryCategoryMapVO> getCategoryForRegistry = new HashMap()
		1*cTools.getCategoryForRegistry("") >> getCategoryForRegistry
		manager.getSkuFromRecommendations([vo1]) >> ["id1","id2"]

		Map<String, List<SKUDetailVO>> categoryMap = new HashMap()
		categoryMap.put("1", [dvo1])
		1*cTools.sortSkubyRegistry(["id1","id2"], _, "", "tbs",BBBCatalogConstants.CATEGORY_SORT_TYPE, null) >> categoryMap
		1*vo1.getSkuId() >> "id2"
		dvo1.setSkuId("id2")
		1*vo1.getJdaCategory() >> "jdaCat"
		getCategoryForRegistry.put("1",mapVo)
		mapVo.setRecommendedCatFlag(true)
		//ends jdaCategorySorting inside sortRecommendations
		//ends sortRecommendations

		when:
		List<RecommendationRegistryProductVO> List = manager.getRegistryRecommendationItemsForTab(registryId,tabId, sortOption, eventTypeCode,requestMock)

		then:
		List.size() ==1
		1*requestMock.setParameter("tabId", tabId)
		1*requestMock.setParameter("categoryBucketsForRecommendation",["1":[]])
		1*requestMock.setParameter("groupByFlag", "category");
		1*requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST,null)
		0*vo1.setJdaCategory("")
	}

	def"getRegistryRecommendationItemsForTab, sorts the tab (sortRecommendations) and calls getRecommender method"(){

		given:
		setParametrsForSpy()
		String sortOption =""
		RecommendationRegistryProductVO vo1 = Mock()
		RecommendationRegistryProductVO vo2 = Mock()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId, tabId) >> [vo1,vo2]

		//for sortRecommendations inside getRegistryRecommendationItemsForTab
		1*cTools.getRegistryTypes("tbs") >> []
		1*vo1.getFname_profileId() >> "fPro"
		1*vo2.getFname_profileId() >> "fPro1"

		1*vo1.getRecommenderProfileId() >> "proId"
		1*vo1.getFirstName() >> "fName"
		1*vo1.getLastName() >> "lName"
		1*vo2.getRecommenderProfileId() >> "proId"
		1*vo2.getFirstName() >> "fName"
		1*vo2.getLastName() >> "lName"
		//ends jdaCategorySorting inside sortRecommendations
		//ends sortRecommendations

		when:
		List<RecommendationRegistryProductVO> List = manager.getRegistryRecommendationItemsForTab(registryId,tabId, sortOption, eventTypeCode,requestMock)

		then:
		List.size() ==2
		1*requestMock.setParameter("tabId", tabId)
		1*requestMock.setParameter("categoryBucketsForRecommendation",["proId:fName lName":[vo1],"proId:fName lName" : [vo1,vo2]])
		1*requestMock.setParameter("groupByFlag", "recommender");
		1*requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST,null)
	}

	def"getRegistryRecommItemsForMobileRecommenderTab, gives list of recomendations for reccomendation tab of mobile"(){

		given:
		setParametrsForSpy()
		RecommendationRegistryProductVO vo1 = Mock()
		RecommendationRegistryProductVO vo2 = Mock()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId,BBBGiftRegistryConstants.RECOMMENDER_TAB_ID) >> [vo1,vo2]
		1*manager.sortRecommendations("", "", [vo1,vo2], requestMock, _, BBBGiftRegistryConstants.RECOMMENDER_TAB_ID) >> {}

		when:
		List<RecommendationRegistryProductVO> list =manager.getRegistryRecommItemsForMobileRecommenderTab(registryId)

		then:
		list.contains(vo1)== true
		list.contains(vo2)== true
	}

	def"getRegistryRecommItemsForMobile, called for recommendation tab for mobile."(){

		given:
		setParametrsForSpy()
		RecommendationRegistryProductVO vo1 = Mock()
		RecommendationRegistryProductVO vo2 = Mock()
		1*giftTools.getRegistryRecommendationItemsForTab(registryId,tabId) >> [vo1,vo2]
		1*manager.sortRecommendations("", eventTypeCode, [vo1,vo2], requestMock, _, tabId) >> {["1" : [vo1]]}

		when:
		SortRecommRegistryVO sortVo =manager.getRegistryRecommItemsForMobile(registryId,"",eventTypeCode,tabId)

		then:
		sortVo.getCategoryBucketsForRecommendation() == ["1":[vo1]]
	}

	def"createRegistryRecommendationProductService, wrapper method for createRegistryRecommendationProduct method when registry id is empty"(){

		given:
		String skuId = "skuId"
		String comment = "comment"
		Long quantity = 10
		1*requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >>  profile
		1*profile.getRepositoryId() >> "repId"

		when:
		manager.createRegistryRecommendationProductService("",skuId,comment,quantity)

		then:
		0*giftTools.createRegistryRecommendationsItem(_)
	}

	def"createRegistryRecommendationProductService, wrapper method for createRegistryRecommendationProduct method when skuId  is empty"(){

		given:
		String skuId = ""
		String comment = "comment"
		Long quantity = 10
		1*requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >>  profile
		1*profile.getRepositoryId() >> "repId"

		when:
		manager.createRegistryRecommendationProductService(registryId,skuId,comment,quantity)

		then:
		0*giftTools.createRegistryRecommendationsItem(_)
	}

	def"createRegistryRecommendationProductService, wrapper method for createRegistryRecommendationProduct method with quantity as zero"(){

		given:
		String skuId = "skuId"
		String comment = "comment"
		Long quantity = 0
		1*requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >>  profile
		1*profile.getRepositoryId() >> "repId"

		when:
		manager.createRegistryRecommendationProductService(registryId,skuId,comment,quantity)

		then:
		0*giftTools.createRegistryRecommendationsItem(_)
	}

	def"createRegistryRecommendationProductService, wrapper method for createRegistryRecommendationProduct method"(){

		given:
		String skuId = "skuId"
		String comment = "comment"
		Long quantity = 10
		String recommenderProfileId = "recommenderProfileId"
		1*requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >>  profile
		1*profile.getRepositoryId() >> "repId"

		when:
		manager.createRegistryRecommendationProductService(registryId,skuId,comment,quantity)

		then:
		1*giftTools.createRegistryRecommendationsItem(_)
	}

	def"sendRegistrantScheduledBulkEmail, when eMail is sent"(){

		given:
		TemplateEmailInfoImpl emailInfo =Mock()
		RegistryBulkNotificationVO registryBulkNotificationVO = new RegistryBulkNotificationVO()

		registryBulkNotificationVO.setProfileId("proId");
		registryBulkNotificationVO.setSiteId("tbs");
		registryBulkNotificationVO.setRegistrantEmail("registrantEmail")
		registryBulkNotificationVO.setEventType("eventType")
		registryBulkNotificationVO.setRecomCount(10)
		registryBulkNotificationVO.setRegistFirstName("fName")
		registryBulkNotificationVO.setRegistLastName("lName")
		1*requestMock.resolveName("/com/bbb/commerce/giftregistry/email/GiftRegistryEmailInfoImpl") >> emailInfo

		when:
		boolean flag = manager.sendRegistrantScheduledBulkEmail(registryBulkNotificationVO)

		then:
		flag == true
		1*emailInfo.setMessageTo("registrantEmail")
	}

	def"sendRegistrantScheduledBulkEmail, when TemplateEmailException is thrown while sending eMail"(){

		given:
		manager =Spy()
		TemplateEmailInfoImpl emailInfo =Mock()
		RegistryBulkNotificationVO registryBulkNotificationVO = new RegistryBulkNotificationVO()
		manager.setGiftRegistryTools(giftTools)
		manager.setEmailSender(sender)
		manager.setRegistrantScheduledBulkEmail("bulkEmail")
		manager.setTemplateUrl("templateURL")
		manager.setRegistryURL("registryURL")

		registryBulkNotificationVO.setProfileId("proId");
		registryBulkNotificationVO.setSiteId("tbs");
		registryBulkNotificationVO.setRegistrantEmail("registrantEmail")
		registryBulkNotificationVO.setEventType("eventType")
		registryBulkNotificationVO.setRecomCount(10)
		registryBulkNotificationVO.setRegistFirstName("fName")
		registryBulkNotificationVO.setRegistLastName("lName")
		1*requestMock.resolveName("/com/bbb/commerce/giftregistry/email/GiftRegistryEmailInfoImpl") >> emailInfo
		1*sender.sendEmailMessage(emailInfo, _, true, false) >>{throw new TemplateEmailException()}

		when:
		boolean flag = manager.sendRegistrantScheduledBulkEmail(registryBulkNotificationVO)

		then:
		flag == false
		1*emailInfo.setMessageTo("registrantEmail")
		1*manager.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10123+ " BBBBusinessException of "+ "sendRegistrantScheduledBulkEmail from GiftRegistryRecommendationManager",_)
	}

	def"sendRegistrantScheduledBulkEmail, when RepositoryException is thrown while sending eMail"(){

		given:
		manager =Spy()
		TemplateEmailInfoImpl emailInfo =Mock()
		RegistryBulkNotificationVO registryBulkNotificationVO = new RegistryBulkNotificationVO()
		manager.setGiftRegistryTools(giftTools)
		manager.setEmailSender(sender)
		manager.setRegistrantScheduledBulkEmail("bulkEmail")
		manager.setTemplateUrl("templateURL")
		manager.setRegistryURL("registryURL")

		registryBulkNotificationVO.setProfileId("proId");
		registryBulkNotificationVO.setSiteId("tbs");
		registryBulkNotificationVO.setRegistrantEmail("registrantEmail")
		registryBulkNotificationVO.setEventType("eventType")
		registryBulkNotificationVO.setRecomCount(10)
		registryBulkNotificationVO.setRegistFirstName("fName")
		registryBulkNotificationVO.setRegistLastName("lName")
		1*requestMock.resolveName("/com/bbb/commerce/giftregistry/email/GiftRegistryEmailInfoImpl") >> emailInfo
		1*sender.sendEmailMessage(emailInfo, _, true, false) >>{throw new RepositoryException()}

		when:
		boolean flag = manager.sendRegistrantScheduledBulkEmail(registryBulkNotificationVO)

		then:
		flag == true
		1*emailInfo.setMessageTo("registrantEmail")
		1*manager.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10123+ " BBBBusinessException of "+ "sendRegistrantScheduledBulkEmail from GiftRegistryRecommendationManager",_)
	}

	def"acceptRecommendation, adds item to gift registry from pending & declined recommendations tab"(){

		given:
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		Boolean fromDeclinedTab = true
		AddItemsBean b1 = new AddItemsBean()
		MutableRepositoryItem pendingItems =Mock()

		giftRegistryViewBean.setTotQuantity(20)
		giftRegistryViewBean.setRepositoryId("repId")
		giftRegistryViewBean.setAdditem([b1])
		b1.setLtlDeliveryServices(BBBCoreConstants.LWA)
		1*mRep.getItemForUpdate("repId",BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS) >> pendingItems

		when:
		boolean flag =manager.acceptRecommendation(giftRegistryViewBean, fromDeclinedTab)

		then:
		flag == true
		1*requestMock.setParameter(BBBGiftRegistryConstants.IS_DECLINED,giftRegistryViewBean.getIsDeclined())
		1*pendingItems.setPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 20)
		1*pendingItems.setPropertyValue(BBBGiftRegistryConstants.LTL_SHIP_METHOD,BBBCoreConstants.LW)
		1*pendingItems.setPropertyValue(BBBGiftRegistryConstants.ASEEMBLY_SELECTED,BBBCoreConstants.TRUE)
		1*requestMock.setParameter(BBBGiftRegistryConstants.IS_FROM_DECLINED_TAB,giftRegistryViewBean.getIsFromDeclinedTab())
		1*pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 0)
		1*mRep.updateItem(pendingItems)
	}

	def"acceptRecommendation, when ltlShipMethod is not LWA, fromDeclinedTab is false, ASEEMBLY_SELECTED is null "(){

		given:
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		Boolean fromDeclinedTab = false
		AddItemsBean b1 = new AddItemsBean()
		MutableRepositoryItem pendingItems =Mock()

		giftRegistryViewBean.setTotQuantity(20)
		giftRegistryViewBean.setRepositoryId("repId")
		giftRegistryViewBean.setAdditem([b1])
		b1.setLtlDeliveryServices("")
		1*mRep.getItemForUpdate("repId",BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS) >> pendingItems

		when:
		boolean flag =manager.acceptRecommendation(giftRegistryViewBean, fromDeclinedTab)

		then:
		flag == true
		1*requestMock.setParameter(BBBGiftRegistryConstants.IS_DECLINED,giftRegistryViewBean.getIsDeclined())
		1*pendingItems.setPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 20)
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.LTL_SHIP_METHOD,"")
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.ASEEMBLY_SELECTED,null)
		0*requestMock.setParameter(BBBGiftRegistryConstants.IS_FROM_DECLINED_TAB,giftRegistryViewBean.getIsFromDeclinedTab())
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 0)
		1*requestMock.setParameter(BBBGiftRegistryConstants.IS_FROM_PENDING_TAB,giftRegistryViewBean.getIsFromPendingTab())
		1*mRep.updateItem(pendingItems)
	}

	def"acceptRecommendation, when getAdditem list ie null , pendingItems is null"(){

		given:
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		Boolean fromDeclinedTab = false
		MutableRepositoryItem pendingItems =Mock()

		giftRegistryViewBean.setTotQuantity(20)
		giftRegistryViewBean.setRepositoryId("repId")
		giftRegistryViewBean.setAdditem(null)
		1*mRep.getItemForUpdate("repId",BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS) >> null

		when:
		boolean flag =manager.acceptRecommendation(giftRegistryViewBean, fromDeclinedTab)

		then:
		flag == false
		1*requestMock.setParameter(BBBGiftRegistryConstants.IS_DECLINED,giftRegistryViewBean.getIsDeclined())
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 20)
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.LTL_SHIP_METHOD,"")
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.ASEEMBLY_SELECTED,null)
		0*requestMock.setParameter(BBBGiftRegistryConstants.IS_FROM_DECLINED_TAB,giftRegistryViewBean.getIsFromDeclinedTab())
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 0)
		0*requestMock.setParameter(BBBGiftRegistryConstants.IS_FROM_PENDING_TAB,giftRegistryViewBean.getIsFromPendingTab())
		0*mRep.updateItem(pendingItems)
	}

	def"acceptRecommendation, when getAdditem list contains null , RepositoryException is thrown"(){

		given:
		manager =Spy()
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		Boolean fromDeclinedTab = false
		MutableRepository mRep =Mock()
		MutableRepositoryItem pendingItems =Mock()

		manager.setGiftRepository(mRep)
		giftRegistryViewBean.setTotQuantity(20)
		giftRegistryViewBean.setRepositoryId("repId")
		giftRegistryViewBean.setAdditem([null])
		1*mRep.getItemForUpdate("repId",BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS) >> {throw new RepositoryException("")}

		when:
		boolean flag =manager.acceptRecommendation(giftRegistryViewBean, fromDeclinedTab)

		then:
		flag == true
		1*requestMock.setParameter(BBBGiftRegistryConstants.IS_DECLINED,giftRegistryViewBean.getIsDeclined())
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 20)
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.LTL_SHIP_METHOD,"")
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.ASEEMBLY_SELECTED,null)
		0*requestMock.setParameter(BBBGiftRegistryConstants.IS_FROM_DECLINED_TAB,giftRegistryViewBean.getIsFromDeclinedTab())
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 0)
		0*requestMock.setParameter(BBBGiftRegistryConstants.IS_FROM_PENDING_TAB,giftRegistryViewBean.getIsFromPendingTab())
		0*mRep.updateItem(pendingItems)
		1*manager.logError("Error while updating quantity", _)
	}

	def"declinePendingItems, declines the pendingItems"(){

		given:
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		MutableRepositoryItem pendingItems =Mock()
		giftRegistryViewBean.setRepositoryId("repId")
		1*mRep.getItemForUpdate("repId",BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS) >> pendingItems

		when:
		boolean flag =manager.declinePendingItems(giftRegistryViewBean)

		then:
		flag == true
		1*requestMock.setParameter(BBBGiftRegistryConstants.IS_FROM_PENDING_TAB,giftRegistryViewBean.getIsFromPendingTab())
		1*requestMock.setParameter(BBBGiftRegistryConstants.IS_DECLINED,giftRegistryViewBean.getIsDeclined())
		1*pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 1)
		1*mRep.updateItem(pendingItems)
	}

	def"declinePendingItems, when pendingItems is null"(){

		given:
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		MutableRepositoryItem pendingItems =Mock()
		giftRegistryViewBean.setRepositoryId("repId")
		1*mRep.getItemForUpdate("repId",BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS) >> null

		when:
		boolean flag =manager.declinePendingItems(giftRegistryViewBean)

		then:
		flag == false
		1*requestMock.setParameter(BBBGiftRegistryConstants.IS_FROM_PENDING_TAB,giftRegistryViewBean.getIsFromPendingTab())
		1*requestMock.setParameter(BBBGiftRegistryConstants.IS_DECLINED,giftRegistryViewBean.getIsDeclined())
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 1)
		0*mRep.updateItem(pendingItems)
	}

	def"declinePendingItems, when RepositoryException is thrown"(){

		given:
		manager = Spy()
		MutableRepository mRep =Mock()
		manager.setGiftRepository(mRep)
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		MutableRepositoryItem pendingItems =Mock()
		giftRegistryViewBean.setRepositoryId("repId")
		1*mRep.getItemForUpdate("repId",BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS) >> {throw new RepositoryException("")}

		when:
		boolean flag =manager.declinePendingItems(giftRegistryViewBean)

		then:
		flag == true
		1*requestMock.setParameter(BBBGiftRegistryConstants.IS_FROM_PENDING_TAB,giftRegistryViewBean.getIsFromPendingTab())
		1*requestMock.setParameter(BBBGiftRegistryConstants.IS_DECLINED,giftRegistryViewBean.getIsDeclined())
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 1)
		0*mRep.updateItem(pendingItems)
		1*manager.logError("error while updating declined quantity", _)
	}

	def"persistToggleRecommenderStatus, when request is to BLOCK user "(){

		given:
		String registryId ="regId"
		String recommenderProfileId ="proId"
		String requestedFlag =BBBGiftRegistryConstants.BLOCK_USER
		1*giftTools.persistChangeInBlockStatus(registryId, recommenderProfileId, 0) >> true

		when:
		boolean flag =manager.persistToggleRecommenderStatus(registryId,recommenderProfileId,requestedFlag)

		then:
		flag == true
	}

	def"persistToggleRecommenderStatus, when request is to UNBLOCK user "(){

		given:
		String registryId ="regId"
		String recommenderProfileId ="proId"
		String requestedFlag =BBBGiftRegistryConstants.UNBLOCK_USER
		1*giftTools.persistChangeInBlockStatus(registryId, recommenderProfileId, 1) >> true

		when:
		boolean flag =manager.persistToggleRecommenderStatus(registryId,recommenderProfileId,requestedFlag)

		then:
		flag == true
	}

	def"persistToggleRecommenderStatus, when request is not valid "(){

		given:
		setParametrsForSpy()
		String registryId ="regId"
		String recommenderProfileId ="proId"
		String requestedFlag = "illegalRequest"
		0*giftTools.persistChangeInBlockStatus(registryId, recommenderProfileId, 1)

		when:
		boolean flag =manager.persistToggleRecommenderStatus(registryId,recommenderProfileId,requestedFlag)

		then:
		flag == false
		1*manager.logError("Illegal flag value received for the requestedFlag. Value received is "+ requestedFlag)
	}

	def"persistToggleRecommenderStatus, when registryId is empty"(){

		given:
		String registryId =""
		String recommenderProfileId ="proId"
		String requestedFlag = "illegalRequest"
		0*giftTools.persistChangeInBlockStatus(registryId, recommenderProfileId, _)

		when:
		boolean flag =manager.persistToggleRecommenderStatus(registryId,recommenderProfileId,requestedFlag)

		then:
		flag == false
		BBBSystemException e = thrown()
		e.getMessage().equals("Invalid Parameter Values have been passed. They are registryId:- "
				+ registryId + " , recommenderProfileId:-"
				+ recommenderProfileId + "requestedFlag:- "
				+ requestedFlag)
	}

	def"persistToggleRecommenderStatus, when recommenderProfileId is empty"(){

		given:
		String registryId ="regId"
		String recommenderProfileId =""
		String requestedFlag = "illegalRequest"
		0*giftTools.persistChangeInBlockStatus(registryId, recommenderProfileId, _)

		when:
		boolean flag =manager.persistToggleRecommenderStatus(registryId,recommenderProfileId,requestedFlag)

		then:
		flag == false
		BBBSystemException e = thrown()
		e.getMessage().equals("Invalid Parameter Values have been passed. They are registryId:- "
				+ registryId + " , recommenderProfileId:-"
				+ recommenderProfileId + "requestedFlag:- "
				+ requestedFlag)
	}

	def"persistToggleRecommenderStatus, when requestedFlag is empty"(){

		given:
		String registryId ="regId"
		String recommenderProfileId ="proId"
		String requestedFlag = ""
		0*giftTools.persistChangeInBlockStatus(registryId, recommenderProfileId, _)

		when:
		boolean flag =manager.persistToggleRecommenderStatus(registryId,recommenderProfileId,requestedFlag)

		then:
		flag == false
		BBBSystemException e = thrown()
		e.getMessage().equals("Invalid Parameter Values have been passed. They are registryId:- "
				+ registryId + " , recommenderProfileId:-"
				+ recommenderProfileId + "requestedFlag:- "
				+ requestedFlag)
	}

	def"setEmailOptInChange, changes option to receive scheduled email by registrant from recommender tab"(){

		given:
		String registryId ="regId"
		String emailOptionValue = "1"
		MutableRepositoryItem registryRecommendationItem =Mock()
		1*mRep.getItemForUpdate(registryId,BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS) >> registryRecommendationItem

		when:
		boolean flag =	manager.setEmailOptInChange(registryId,emailOptionValue)

		then:
		flag == true
		1*registryRecommendationItem.setPropertyValue(BBBGiftRegistryConstants.EMAIL_OPTION, 1)
		1*mRep.updateItem(registryRecommendationItem)
	}

	def"setEmailOptInChange, when RepositoryException is thrown"(){

		given:
		manager =Spy()
		String registryId ="regId"
		String emailOptionValue = "1"
		MutableRepository mRep =Mock()
		manager.setGiftRepository(mRep)
		MutableRepositoryItem registryRecommendationItem =Mock()
		1*mRep.getItemForUpdate(registryId,BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS) >> {throw new RepositoryException()}

		when:
		boolean flag =	manager.setEmailOptInChange(registryId,emailOptionValue)

		then:
		1*manager.logError("Error while setting value of emailOption", _)
		flag == false
		0*registryRecommendationItem.setPropertyValue(BBBGiftRegistryConstants.EMAIL_OPTION, 1)
		0*mRep.updateItem(registryRecommendationItem)
	}

	def"setEmailOptInChange, when emailOptionValue is less than -1"(){

		given:
		String registryId ="regId"
		String emailOptionValue = "-2"
		MutableRepositoryItem registryRecommendationItem =Mock()
		0*mRep.getItemForUpdate(registryId,BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS) >> registryRecommendationItem

		when:
		boolean flag =	manager.setEmailOptInChange(registryId,emailOptionValue)

		then:
		flag == false
		0*registryRecommendationItem.setPropertyValue(BBBGiftRegistryConstants.EMAIL_OPTION, 1)
		0*mRep.updateItem(registryRecommendationItem)
	}

	def"setEmailOptInChange, when emailOptionValue is greater than 2"(){

		given:
		String registryId ="regId"
		String emailOptionValue = "5"
		MutableRepositoryItem registryRecommendationItem =Mock()
		0*mRep.getItemForUpdate(registryId,BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS) >> registryRecommendationItem

		when:
		boolean flag =	manager.setEmailOptInChange(registryId,emailOptionValue)

		then:
		flag == false
		0*registryRecommendationItem.setPropertyValue(BBBGiftRegistryConstants.EMAIL_OPTION, 1)
		0*mRep.updateItem(registryRecommendationItem)
	}

	def"getEmailOptInValue,  when registryRecommendationItem, emailOption are not null"(){

		given:
		String registryId ="regId"
		MutableRepositoryItem registryRecommendationItem =Mock()
		1*mRep.getItem(registryId,BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS) >> registryRecommendationItem
		2*registryRecommendationItem.getPropertyValue(BBBGiftRegistryConstants.EMAIL_OPTION) >> 5

		when:
		int value =	manager.getEmailOptInValue(registryId)

		then:
		value == 5
	}

	def"getEmailOptInValue, when registryRecommendationItem is not null, emailOption is null"(){

		given:
		String registryId ="regId"
		MutableRepositoryItem registryRecommendationItem =Mock()
		1*mRep.getItem(registryId,BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS) >> registryRecommendationItem
		1*registryRecommendationItem.getPropertyValue(BBBGiftRegistryConstants.EMAIL_OPTION) >> null

		when:
		int value =	manager.getEmailOptInValue(registryId)

		then:
		value == -1
	}

	def"getEmailOptInValue, when registryRecommendationItem is null, emailOption is not null"(){

		given:
		String registryId ="regId"
		MutableRepositoryItem registryRecommendationItem =Mock()
		1*mRep.getItem(registryId,BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS) >> null
		0*registryRecommendationItem.getPropertyValue(BBBGiftRegistryConstants.EMAIL_OPTION)

		when:
		int value =	manager.getEmailOptInValue(registryId)

		then:
		value == -1
	}

	def"getEmailOptInValue, when RepositoryException is thrown"(){

		given:
		manager =Spy()
		MutableRepository mRep =Mock()
		MutableRepositoryItem registryRecommendationItem =Mock()
		String registryId ="regId"
		manager.setGiftRepository(mRep)

		1*mRep.getItem(registryId,BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS) >> {throw new RepositoryException("")}
		0*registryRecommendationItem.getPropertyValue(BBBGiftRegistryConstants.EMAIL_OPTION) >> 5

		when:
		int value =	manager.getEmailOptInValue(registryId)

		then:
		value == -1
		1*manager.logError("Error while fetching value of emailOption", _)
	}

	def"performUndo, when undoFrom equals moveToPending,pendingItems is not null(revertToPending)"(){

		given:
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		MutableRepositoryItem pendingItems =Mock()
		String undoFrom ="moveToPending"

		giftRegistryViewBean.setRegistryId("regId")
		1*mRep.getItemForUpdate("regId",BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS) >> pendingItems
		0*giftTools.removeUpdateGiftRegistryItem(giftRegistryViewBean)

		when:
		boolean flag = manager.performUndo(giftRegistryViewBean, undoFrom)

		then:
		flag == true
		1*pendingItems.setPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 0);
		1*pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 0);
		1*mRep.updateItem(pendingItems)
	}

	def"performUndo, when undoFrom equals moveToPending,pendingItems is null(revertToPending) and isErrorExists in false"(){

		given:
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		MutableRepositoryItem pendingItems =Mock()
		ManageRegItemsResVO resultVO =new ManageRegItemsResVO()
		ServiceErrorVO sVO = new ServiceErrorVO()
		String undoFrom ="moveToPending"

		giftRegistryViewBean.setRegistryId("regId")
		1*mRep.getItemForUpdate("regId",BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS) >> null

		resultVO.setServiceErrorVO(sVO)
		sVO.setErrorExists(false)
		1*giftTools.removeUpdateGiftRegistryItem(giftRegistryViewBean) >>resultVO

		when:
		boolean flag = manager.performUndo(giftRegistryViewBean, undoFrom)

		then:
		flag == true
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 0);
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 0);
		0*mRep.updateItem(pendingItems)
	}

	def"performUndo, when undoFrom equals moveToPending,pendingItems is null(revertToPending) and isErrorExists in true"(){

		given:
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		MutableRepositoryItem pendingItems =Mock()
		ManageRegItemsResVO resultVO =new ManageRegItemsResVO()
		ServiceErrorVO sVO = new ServiceErrorVO()
		String undoFrom ="moveToPending"

		giftRegistryViewBean.setRegistryId("regId")
		1*mRep.getItemForUpdate("regId",BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS) >> null

		resultVO.setServiceErrorVO(sVO)
		sVO.setErrorExists(true)
		1*giftTools.removeUpdateGiftRegistryItem(giftRegistryViewBean) >>resultVO

		when:
		boolean flag = manager.performUndo(giftRegistryViewBean, undoFrom)

		then:
		flag == false
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 0);
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 0);
		0*mRep.updateItem(pendingItems)
	}

	def"performUndo, when undoFrom equals moveToDeclined,pendingItems is not null(revertToPending)"(){

		given:
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		MutableRepositoryItem pendingItems =Mock()
		String undoFrom ="moveToDeclined"

		giftRegistryViewBean.setRegistryId("regId")
		1*mRep.getItemForUpdate("regId",BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS) >> pendingItems
		0*giftTools.removeUpdateGiftRegistryItem(giftRegistryViewBean)

		when:
		boolean flag = manager.performUndo(giftRegistryViewBean, undoFrom)

		then:
		flag == true
		1*pendingItems.setPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 0);
		1*pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 0);
		1*mRep.updateItem(pendingItems)
	}

	def"performUndo, when undoFrom equals moveToDeclined,pendingItems is null(revertToPending) and isErrorExists in false"(){

		given:
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		MutableRepositoryItem pendingItems =Mock()
		ManageRegItemsResVO resultVO =new ManageRegItemsResVO()
		ServiceErrorVO sVO = new ServiceErrorVO()
		String undoFrom ="moveToDeclined"

		giftRegistryViewBean.setRegistryId("regId")
		1*mRep.getItemForUpdate("regId",BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS) >> null

		resultVO.setServiceErrorVO(sVO)
		sVO.setErrorExists(false)
		1*giftTools.removeUpdateGiftRegistryItem(giftRegistryViewBean) >>resultVO

		when:
		boolean flag = manager.performUndo(giftRegistryViewBean, undoFrom)

		then:
		flag == true
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 0);
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 0);
		0*mRep.updateItem(pendingItems)
	}

	def"performUndo, when undoFrom equals NA and RepositoryException is thrown"(){

		given:
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		MutableRepositoryItem pendingItems =Mock()
		ManageRegItemsResVO resultVO =new ManageRegItemsResVO()
		String undoFrom ="NA"
		1*giftTools.removeUpdateGiftRegistryItem(giftRegistryViewBean) >> {throw new RepositoryException()}

		when:
		boolean flag = manager.performUndo(giftRegistryViewBean, undoFrom)

		then:
		flag == false
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 0);
		0*pendingItems.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 0);
		0*mRep.updateItem(pendingItems)
	}

	def"getRowIdAfterAddition, when addedItemsList is not null"(){

		given:
		manager =Spy()
		RepositoryView view =Mock()
		RepositoryItem add =Mock()
		RepositoryItem add1 =Mock()
		MutableRepository mRep =Mock()
		AddItemsBean addedItemsBean =new AddItemsBean()
		manager.setGiftRepository(mRep)

		1*mRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >>view
		addedItemsBean.setSku("sku")
		addedItemsBean.setRegistryId("regID")
		1*manager.extractDbCall(view, _, _) >> [add, add1]
		1*add.getPropertyValue("ROWID") >> "rowID"

		when:
		String row = manager.getRowIdAfterAddition(addedItemsBean)

		then:
		row =="rowID"
	}

	def"getRowIdAfterAddition, when size of addedItemsList is 1"(){

		given:
		manager =Spy()
		RepositoryView view =Mock()
		RepositoryItem add =Mock()
		MutableRepository mRep =Mock()
		AddItemsBean addedItemsBean =new AddItemsBean()
		manager.setGiftRepository(mRep)

		1*mRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >>view
		addedItemsBean.setSku("sku")
		addedItemsBean.setRegistryId("regID")
		1*manager.extractDbCall(view, _, _) >> [add]
		0*add.getPropertyValue("ROWID")

		when:
		String row = manager.getRowIdAfterAddition(addedItemsBean)

		then:
		1*manager.logError("Unable to detect added product")
		row ==null
	}

	def"getRowIdAfterAddition, when addedItemsList is null"(){

		given:
		manager =Spy()
		RepositoryView view =Mock()
		RepositoryItem add =Mock()
		MutableRepository mRep =Mock()
		AddItemsBean addedItemsBean =new AddItemsBean()
		manager.setGiftRepository(mRep)

		1*mRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >>view
		addedItemsBean.setSku("sku")
		addedItemsBean.setRegistryId("regID")
		1*manager.extractDbCall(view, _, _) >> null
		0*add.getPropertyValue("ROWID")

		when:
		String row = manager.getRowIdAfterAddition(addedItemsBean)

		then:
		1*manager.logError("Unable to detect added product")
		row ==null
	}
}
