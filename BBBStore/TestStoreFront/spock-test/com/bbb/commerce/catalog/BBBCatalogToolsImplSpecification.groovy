package com.bbb.commerce.catalog;

import java.text.SimpleDateFormat
import java.util.Map
import java.util.List;

import org.apache.commons.lang.time.DateUtils

import spock.lang.specification.BBBExtendedSpec
import atg.commerce.order.AuxiliaryData
import atg.commerce.pricing.priceLists.PriceListException
import atg.commerce.pricing.priceLists.PriceListManager
import atg.nucleus.naming.ComponentName
import atg.repository.MutableRepository
import atg.repository.Query
import atg.repository.QueryBuilder
import atg.repository.QueryExpression
import atg.repository.RemovedItemException
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemDescriptor
import atg.repository.RepositoryItemImpl
import atg.repository.RepositoryView
import atg.repository.rql.RqlStatement
import atg.servlet.DynamoHttpServletRequest
import atg.servlet.ServletUtil
import atg.userprofiling.Profile

import com.bbb.account.profile.vo.ProfileEDWInfoVO
import com.bbb.browse.webservice.manager.BBBEximManager
import com.bbb.cache.BBBLocalDynamicPriceSKUCache
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.cms.tools.CmsTools
import com.bbb.commerce.browse.manager.ProductManager
import com.bbb.commerce.browse.vo.RecommendedSkuVO
import com.bbb.commerce.browse.vo.SddZipcodeVO
import com.bbb.commerce.catalog.comparison.vo.CompareProductEntryVO
import com.bbb.commerce.catalog.vo.AttributeVO
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO
import com.bbb.commerce.catalog.vo.CategoryVO
import com.bbb.commerce.catalog.vo.CollectionProductVO
import com.bbb.commerce.catalog.vo.CountryVO
import com.bbb.commerce.catalog.vo.ImageVO
import com.bbb.commerce.catalog.vo.MediaVO
import com.bbb.commerce.catalog.vo.PDPAttributesVO
import com.bbb.commerce.catalog.vo.ProductVO
import com.bbb.commerce.catalog.vo.RecommendedCategoryVO
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.catalog.vo.ShipMethodVO
import com.bbb.commerce.catalog.vo.SiteChatAttributesVO
import com.bbb.commerce.catalog.vo.StateVO
import com.bbb.commerce.catalog.vo.ThresholdVO
import com.bbb.commerce.inventory.BBBInventoryManager
import com.bbb.commerce.inventory.OnlineInventoryManager
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship
import com.bbb.constants.BBBCmsConstants
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBEximConstants
import com.bbb.constants.BBBInternationalShippingConstants
import com.bbb.constants.BBBWebServiceConstants
import com.bbb.constants.TBSConstants
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.ecommerce.order.BBBStoreShippingGroup
import com.bbb.eph.util.EPHLookUpUtil
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.cache.BBBDynamicPriceCacheContainer
import com.bbb.framework.cache.BBBObjectCache
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.profile.session.BBBSessionBean
import com.bbb.repository.RepositoryItemMock
import com.bbb.search.bean.query.SearchQuery
import com.bbb.search.bean.result.BBBDynamicPriceSkuVO
import com.bbb.search.bean.result.BBBDynamicPriceVO
import com.bbb.search.bean.result.CategoryRefinementVO
import com.bbb.search.bean.result.FacetRefinementVO
import com.bbb.search.endeca.EndecaSearchUtil
import com.bbb.search.endeca.constants.BBBEndecaConstants
import com.bbb.search.endeca.tools.EndecaSearchTools
import com.bbb.search.endeca.vo.SearchBoostingAlgorithmVO
import com.bbb.selfservice.vo.BeddingShipAddrVO
import com.bbb.seo.CategorySeoLinkGenerator
import com.bbb.tools.BBBEdwRepositoryTools
import com.bbb.tools.BBBPriceListRepositoryTools
import com.bbb.tools.BBBSchoolRepositoryTools
import com.bbb.tools.BBBShippingRepositoryTools
import com.bbb.tools.BBBSiteRepositoryTools
import com.bbb.tools.BBBStoreRepositoryTools
import com.bbb.tools.GlobalRepositoryTools
import com.bbb.utils.BBBConfigRepoUtils
import com.bbb.utils.BBBUtility


public class BBBCatalogToolsImplSpecification extends BBBExtendedSpec{
	
	 MutableRepository siteRepositoryMock = Mock()
	 MutableRepository catalogRepositoryMock = Mock()
	 BBBCatalogToolsImpl testObjCatalogTools
	 RepositoryItem siteRepositoryItemMock = Mock()
	 RepositoryItem skuRepositoryItemMock = Mock()
	 RepositoryItem shipRepositoryItemMock1 = Mock()
	 RepositoryItem repositoryItemMock = Mock()
	 LblTxtTemplateManager lblTxtTemplateManagerMock = Mock()
	 Map<String, String> bbbSiteToAttributeSiteMap = new HashMap<String, String>()
	 Repository repositoryMock = Mock()
	 RepositoryItemMock repositoryItem = new RepositoryItemMock()
	 RepositoryItem[] repositoryItemMockList = [repositoryItem]
	 RqlStatement rqlStatementMock = Mock()
	 RepositoryView repositoryViewMock = Mock()
	 BBBSessionBean sessionBeanMock = Mock(BBBSessionBean)
	 BBBOrderImpl bbbOrderImplMock = Mock()
	 SddZipcodeVO sddZipcodeVOMock = Mock()
	 BBBCommerceItem bbbCommerceItemMock = Mock()
	 SKUDetailVO skuDetailVOMock = Mock()
	 ShipMethodVO shipMethodVOMock = Mock()
	 Map<String, String> shipGrpFreeShipAttrMap = new HashMap<String, String>()
	 PriceListManager priceListManagerMock = Mock()
	 BBBInventoryManager bbbInventoryManagerMock = Mock()
	 OnlineInventoryManager onlineInventoryManagerMock = Mock()
	 BBBSiteRepositoryTools siteRepositoryToolsMock = Mock()
     BBBEdwRepositoryTools edwRepositoryToolsMock = Mock()
     BBBShippingRepositoryTools shippingRepositoryToolsMock = Mock()
     BBBSchoolRepositoryTools schoolRepositoryToolsMock = Mock()
     BBBStoreRepositoryTools storeRepositoryToolsMock = Mock()
     BBBPriceListRepositoryTools priceListRepositoryToolsMock = Mock()
     GlobalRepositoryTools globalRepositoryToolsMock = Mock()
	 RepositoryItem productRepositoryItemMock = Mock()
	 RepositoryItem priceListRepositoryItemMock = Mock()
	 RepositoryItem priceRepositoryItemMock = Mock()
	 CategoryVO categoryVOMock = Mock()
	 MutableRepository couponRepositoryMock = Mock()
	 RepositoryItem categoryRepositoryItem = Mock()
	 MutableRepository vendorRepository = Mock()
	 QueryBuilder qBuilder  = Mock()
	 QueryExpression qExp =Mock()
	 Query query = Mock()
	 BBBCatalogToolsImpl catalogTools  = Mock()
	 SearchQuery sQueryMock = Mock()
	 EPHLookUpUtil lookupUtilMock = Mock()
	 EndecaSearchUtil eSearchUtil =Mock()
	 EndecaSearchTools eSearchTools = Mock()
	 SearchBoostingAlgorithmVO searchBoostingAlgorithmVO = Mock()
	 Profile pMock = Mock()
	 BBBLocalDynamicPriceSKUCache  priceSKUCacheMock = Mock() 
	 BBBDynamicPriceCacheContainer priceCacheContainer = Mock()
	 BBBDynamicPriceSkuVO priceSkuVO = new BBBDynamicPriceSkuVO()
	 BBBObjectCache ObjectCacheMock =Mock()
	 CategorySeoLinkGenerator seoLinkGen = Mock()
	 CmsTools cmsTool =Mock()
	 BBBEximManager exManager =Mock()
	 BBBHardGoodShippingGroup  hShip = Mock()
	 RepositoryItem recommendationItemMock = Mock()
	 RecommendedCategoryVO recommendedCategoryVO=Mock()
	 Repository managedCatalogRepositoryMock = Mock()
	 List<RepositoryItem> recommendations=Mock()
	 Object propertyValue=Mock()
	 ProductManager productManagerMock=Mock() 
	 Map   siteToAttributeSiteMap=["BedBathUS":"bedbathUSFlag","BuyBuyBaby":"buybuybabyFlag","BedBathCanada":"bedbathcanadaFlag","GS_BedBathUS":"gsBedbathUSFlag","GS_BuyBuyBaby":"gsBuyBuyBabyFlag",
		 "GS_BedBathCanada":"gsbedbathcanadaFlag"]
	
	def setup() {

		testObjCatalogTools = Spy(BBBCatalogToolsImpl)
		def customizationOfferedSiteMap = ["BedBathUS":"customizationOfferedFlag", "TBS_BedBathUS":"customizationOfferedFlag",
			"TBS_BuyBuyBaby":"customizationOfferedFlag", "TBS_BedBathCanada":"customizationOfferedFlag",
			"BuyBuyBaby":"babCustomizationOfferedFlag", "BedBathCanada":"caCustomizationOfferedFlag",
			"GS_BedBathUS":"gsCustomizationOfferedFlag", "GS_BuyBuyBaby":"gsCustomizationOfferedFlag",
			"GS_BedBathCanada":"gsCustomizationOfferedFlag", "TBS_BedBathUS":"customizationOfferedFlag",
			"TBS_BuyBuyBaby":"babCustomizationOfferedFlag", "TBS_BedBathCanada":"caCustomizationOfferedFlag"]

		def shipGrpFreeShipAttrMap = ["3g":"10_1", "2a":"10_2", "1a":"10_3"]
		def pagemap = ["DEFAULT":"00","SEARCH":"01","L2L3":"02","BRAND":"03","COLLEGE":"02"]
		testObjCatalogTools.setDataCenterMap(["DC1":"AN","DC2":"SD","DC3":"UN"])
		testObjCatalogTools.setCustomizationOfferedSiteMap(customizationOfferedSiteMap)
		testObjCatalogTools.setShipGrpFreeShipAttrMap(shipGrpFreeShipAttrMap)
		testObjCatalogTools.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
		testObjCatalogTools.setSddShipMethodId("SDD")
		testObjCatalogTools.setInventoryManager(onlineInventoryManagerMock)
		testObjCatalogTools.setBbbInventoryManager(bbbInventoryManagerMock)
		testObjCatalogTools.setRecommendedSkuRepository(repositoryMock)
		testObjCatalogTools.setBBBSiteToAttributeSiteMap(bbbSiteToAttributeSiteMap)
		BBBConfigRepoUtils.setBbbCatalogTools(testObjCatalogTools)
		testObjCatalogTools.setSiteRepository(siteRepositoryMock)
		testObjCatalogTools.setPriceListManager(priceListManagerMock)
		testObjCatalogTools.setGlobalRepositoryTools(globalRepositoryToolsMock)
		testObjCatalogTools.setPriceListRepositoryTools(priceListRepositoryToolsMock)
		testObjCatalogTools.setStoreRepositoryTools(storeRepositoryToolsMock)
		testObjCatalogTools.setSchoolRepositoryTools(schoolRepositoryToolsMock)
		testObjCatalogTools.setEdwRepositoryTools(edwRepositoryToolsMock)
		testObjCatalogTools.setShippingRepositoryTools(shippingRepositoryToolsMock)
		testObjCatalogTools.setSiteRepositoryTools(siteRepositoryToolsMock)
		testObjCatalogTools.setCatalogRepository(catalogRepositoryMock)
		testObjCatalogTools.setCouponRepository(couponRepositoryMock)
		testObjCatalogTools.setConfigType("ContentCatalogKeys")
		testObjCatalogTools.setCouponRuleSkuQuery("entryCode=?0 and ruleToEvaluate=?1 and siteId=?2 and skuId=?3 ORDER BY sequence SORT ASC")
		testObjCatalogTools.setCouponRuleVendorDeptQuery("entryCode=?0 and ruleToEvaluate=?1 and siteId=?2 and vendorId=?7 and jdaDeptId=?8 ORDER BY sequence SORT ASC")
		testObjCatalogTools.setCouponRuleVendorDeptClassQuery("entryCode=?0 and ruleToEvaluate=?1 and siteId=?2 and (vendorId=?7 OR vendorId=?8) and (jdaDeptId=?4 and (jdaSubDeptId=?8 OR (jdaSubDeptId=?5 AND (jdaClass=?8 OR jdaClass=?6)))) ORDER BY sequence SORT ASC")
		testObjCatalogTools.setGiftCertProductQuery("giftCertProduct=1 and (collection=1 or leadPrd=1)")
		testObjCatalogTools.setVendorRepository(vendorRepository)
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		testObjCatalogTools.setGuidesRepository(repositoryMock)
		testObjCatalogTools.setProductGuideRqlQuery("shopGuideId=?0 and site=?1")
		testObjCatalogTools.setSearchUtil(eSearchUtil)
		testObjCatalogTools.setPageTypeMap(pagemap)
		testObjCatalogTools.setEndecaSearchTools(eSearchTools)
		testObjCatalogTools.setSkuCacheContainer(priceCacheContainer)
		testObjCatalogTools.setProductCacheContainer(priceCacheContainer)
		requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> pMock
		testObjCatalogTools.setLocalDynamicSKUPriceCache(priceSKUCacheMock)
		testObjCatalogTools.setObjectCache(ObjectCacheMock)
		testObjCatalogTools.setCategorySeoLinkGenerator(seoLinkGen)
		testObjCatalogTools.setIntShipSkuRestrictionRepository(couponRepositoryMock)
		testObjCatalogTools.setCmsTools(cmsTool)
		testObjCatalogTools.setEximManager(exManager)
		testObjCatalogTools.setBazaarVoiceRepository(siteRepositoryMock)
		testObjCatalogTools.setBbbManagedCatalogRepository(managedCatalogRepositoryMock)
		testObjCatalogTools.setProductManager(productManagerMock)
		testObjCatalogTools.setCountryName("countryCode = ?0")
		testObjCatalogTools.setPageOrderQuery("pageName = ?0")
		testObjCatalogTools.setPageOrderRepository(siteRepositoryMock)
		testObjCatalogTools.setAttributeInfoRepository(repositoryMock)
		testObjCatalogTools.setZipCodeSaperater(",")
	}

	/////////////////////////////////TCs for getSKUDetails starts////////////////////////////////////////
	//Signature : public final SKUDetailVO  getSKUDetails(final String siteId, final String skuId, final boolean calculateAboveBelowLine, final boolean isMinimal, final boolean includeStoreItems)

	def "getSKUDetails. This TC is a Happy Flow of getSKUDetails method which tests the invocation of setEligibleShipMethods of skuDetailVOMock."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "564564"
		boolean calculateAboveBelowLine = false
		boolean isMinimal = false
		boolean includeStoreItems = true

		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		skuRepositoryItemMock.getRepositoryId() >> "5454564545"
		skuRepositoryItemMock.getPropertyValue("storeSKU") >> true
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuDetailVOMock.isLtlItem() >> true
		testObjCatalogTools.getMinimalSku(skuRepositoryItemMock) >> skuDetailVOMock
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock
		testObjCatalogTools.getLTLEligibleShippingMethods(skuRepositoryItemMock.getRepositoryId(), siteId, testObjCatalogTools.getDefaultLocale()) >> [shipMethodVOMock]

		when:
		testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, isMinimal, includeStoreItems)

		then:
		//checks setEligibleShipMethods is invoked of skuDetailVOMock.
		1 * skuDetailVOMock.setEligibleShipMethods(_)
	}

	def "getSKUDetails with isSkuActive property as false. This TC tests the getSKUDetails method which tests the invocation of setEligibleShipMethods of skuDetailVOMock."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "564564"
		boolean calculateAboveBelowLine = false
		boolean isMinimal = false
		boolean includeStoreItems = true

		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		skuRepositoryItemMock.getRepositoryId() >> "545458"
		skuRepositoryItemMock.getPropertyValue("storeSKU") >> true
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> false
		skuDetailVOMock.isLtlItem() >> true
		testObjCatalogTools.getMinimalSku(skuRepositoryItemMock) >> skuDetailVOMock
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock
		testObjCatalogTools.getLTLEligibleShippingMethods(skuRepositoryItemMock.getRepositoryId(), siteId, testObjCatalogTools.getDefaultLocale()) >> [shipMethodVOMock]

		when:
		testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, isMinimal, includeStoreItems)

		then:
		//checks setEligibleShipMethods is invoked of skuDetailVOMock.
		1 * skuDetailVOMock.setEligibleShipMethods(_)
	}

	def "getSKUDetails with storeSKU property as true and includeStoreItems as false. This TC tests the tests the properties of skuDetailVOMock."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "564564"
		boolean calculateAboveBelowLine = false
		boolean isMinimal = false
		boolean fromCart = true
		boolean includeStoreItems = false

		//Mock methods for SkuAttributeList
		repositoryItemMock.getRepositoryId() >> "SDD"
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
		repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		repositoryItemMock.getPropertyValue("startDate") >> new Date()
		repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
		repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
		repositoryItemMock.getPropertyValue("placeHolder") >> "a"
		skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
		shipRepositoryItemMock1.getRepositoryId() >> "454464"
		skuRepositoryItemMock.getPropertyValue("nonShippableStates") >> [shipRepositoryItemMock1].toSet()
		skuRepositoryItemMock.getPropertyValue("customizationOfferedFlag") >> true
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getRebateKey()) >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "sameDayDelAttributeKeyList") >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getZoomKeys()) >> ["SDD"]

		//Mock methods for SKUDetailVO
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		testObjCatalogTools.updateShippingMessageFlag(skuDetailVOMock, false, 0.0) >> void
		testObjCatalogTools.populateDynamicSKUDeatilInVO(skuDetailVOMock, "US", siteId, fromCart) >> void
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getSiteRepository().getItem(siteId, "siteConfiguration") >> siteRepositoryItemMock

		//Mock methods for getSKUDetails
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		skuRepositoryItemMock.getRepositoryId() >> "564564"
		skuRepositoryItemMock.getPropertyValue("storeSKU") >> true
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY , BBBCmsConstants.SDD_SHOW_ATTRI) >> ["true"]
		1*siteRepositoryToolsMock.isNexusState(*_) >> true
		when:
		def SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, isMinimal, includeStoreItems)

		then:
		//checks getSKUDetailVO is invoked with SKU Details of BBBCatalogToolsImpl.
		skuDetailVO.getSkuId() == "564564"
		skuDetailVO.isHasRebate() == true
		skuDetailVO.isHasSddAttribute() == true
		skuDetailVO.isCustomizationOffered() == false
		skuDetailVO.getNonShippableStates().get(0).getStateCode() == "454464"
	}

	def "getSKUDetails with null sddEligibleOn and isLtlItem as false. This TC tests the invocation of setEligibleShipMethods of skuDetailVOMock."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "564564"
		boolean calculateAboveBelowLine = false
		boolean isMinimal = false
		boolean includeStoreItems = true

		repositoryItemMock.getRepositoryId() >> "SDD"
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> []
		skuRepositoryItemMock.getRepositoryId() >> "5454564545"
		skuRepositoryItemMock.getPropertyValue("storeSKU") >> true
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuDetailVOMock.isLtlItem() >> false
		testObjCatalogTools.getMinimalSku(skuRepositoryItemMock) >> skuDetailVOMock
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock
		testObjCatalogTools.getSiteRepository().getItem(siteId, "siteConfiguration") >> siteRepositoryItemMock
		0*siteRepositoryToolsMock.getShippingMethodsForSku(siteId, skuId, includeStoreItems)
		when:
		testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, isMinimal, includeStoreItems)

		then:
		//checks setEligibleShipMethods is invoked of skuDetailVOMock.
		1 * skuDetailVOMock.setEligibleShipMethods(_)
	}


	def "getSKUDetails with null storeSKU property of skuRepositoryItem. This TC tests the invocation of setEligibleShipMethods of skuDetailVOMock."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "898989"
		boolean calculateAboveBelowLine = false
		boolean isMinimal = false
		boolean fromCart = true
		boolean includeStoreItems = true

		//Mock methods for SKUDetailVO
		repositoryItemMock.getRepositoryId() >> "SDD"
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		lblTxtTemplateManagerMock.getPageLabel("lbl_higher_free_shipping_threshold", testObjCatalogTools.getDefaultLocale(), null, siteId)
		testObjCatalogTools.updateShippingMessageFlag(skuDetailVOMock, false, 0.0) >> void
		testObjCatalogTools.populateDynamicSKUDeatilInVO(skuDetailVOMock, "US", siteId, fromCart) >> void
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getSiteRepository().getItem(siteId, "siteConfiguration") >> siteRepositoryItemMock

		//Mock methods for getSKUDetails
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		skuRepositoryItemMock.getRepositoryId() >> "898989"
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:
		def SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, isMinimal, includeStoreItems)

		then:
		//checks getSKUDetailVO is invoked with SKU Details of BBBCatalogToolsImpl.
		skuDetailVO.getSkuId() == "898989"
	}

	def "getSKUDetails with null skuRepositoryItem. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "564564"
		boolean calculateAboveBelowLine = false
		boolean isMinimal = false
		boolean includeStoreItems = true

		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]

		when:
		testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, isMinimal, includeStoreItems)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSKUDetails with empty SiteId. This TC test that exception is throws when SiteId is null."() {

		given:
		String siteId = ""
		String skuId = "564564"
		boolean calculateAboveBelowLine = false
		boolean isMinimal = false
		boolean includeStoreItems = true

		when:
		testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, isMinimal, includeStoreItems)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSKUDetails with null SiteId. This TC test that exception is throws when SiteId is null."() {

		given:
		String siteId = null
		String skuId = "564564"
		boolean calculateAboveBelowLine = false
		boolean isMinimal = false
		boolean includeStoreItems = true

		when:
		testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, isMinimal, includeStoreItems)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSKUDetails with exception scenarios. This TC test that repository exception is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "564564"
		boolean calculateAboveBelowLine = false
		boolean isMinimal = false
		boolean includeStoreItems = true

		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> {throw new RepositoryException()}

		when:
		testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, isMinimal, includeStoreItems)

		then:
		//checks if BBBBusinessException is thrown.
		BBBSystemException exception = thrown()
	}

	////////////////////////////////////TCs for getSKUDetails ends////////////////////////////////////////

	/////////////////////////////////TCs for getSKUDetails starts////////////////////////////////////////
	//Signature : public final SKUDetailVO  getSKUDetails(final String siteId, final String skuId, final boolean calculateAboveBelowLine,String ... intlUser)

	def "getSKUDetails. This TC is the happy flow of getSKUDetails."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "66555"
		boolean calculateAboveBelowLine = false
		String intlUser = ""
		repositoryItemMock.getRepositoryId() >> "SDD"
		requestMock.getHeader("X-bbb-channel") >> "MobileApp"
		skuRepositoryItemMock.getRepositoryId() >> "66555"
		skuRepositoryItemMock.getPropertyValue("webOffered") >> false
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock, intlUser) >> true
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock
		testObjCatalogTools.getSiteRepository().getItem(siteId, "siteConfiguration") >> siteRepositoryItemMock
		testObjCatalogTools.getNonShippableStatesForSku(siteId, skuId) >> []
		when:

		SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, intlUser)

		then:
		//checks getSKUDetailVO is invoked with SKU Details of BBBCatalogToolsImpl.
		skuDetailVO.getSkuId() == "66555"
	}

	def "getSKUDetails with empty siteId. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = ""
		String skuId = "66555"
		boolean calculateAboveBelowLine = false
		String intlUser = ""

		when:

		testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, intlUser)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSKUDetails with null siteId. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = null
		String skuId = "66555"
		boolean calculateAboveBelowLine = false
		String intlUser = ""

		when:

		testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, intlUser)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSKUDetails with skuRepositoryItem as null. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "66555"
		boolean calculateAboveBelowLine = false
		String intlUser = ""

		when:

		testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, intlUser)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSKUDetails with isSkuActive as false. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "66555"
		boolean calculateAboveBelowLine = false
		String intlUser = ""
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock, intlUser) >> false
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, intlUser)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSKUDetails with repository exception scenarios. This TC test that repository exception is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "66555"
		boolean calculateAboveBelowLine = false
		String intlUser = ""

		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> {throw new RepositoryException()}

		when:
		testObjCatalogTools.getSKUDetails(siteId, skuId, calculateAboveBelowLine, intlUser)

		then:
		//checks if BBBBusinessException is thrown.
		BBBSystemException exception = thrown()
	}

	////////////////////////////////////TCs for getSKUDetails ends////////////////////////////////////////

	/////////////////////////////////TCs for getSKUDetails starts////////////////////////////////////////
	//Signature : public final SKUDetailVO getSKUDetails(final String siteId, final String skuId)///////

	def "getSKUDetails. This TC is the happy flow for getSKUDetails"() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		//Mock methods for SkuAttributeList
		repositoryItemMock.getRepositoryId() >> "SDD"
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
		repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		repositoryItemMock.getPropertyValue("startDate") >> new Date()
		repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
		repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
		skuRepositoryItemMock.getPropertyValue("ltlFlag") >> true
		skuRepositoryItemMock.getRepositoryId() >> skuId
		skuRepositoryItemMock.getPropertyValue("intlRestricted") >> "Y"
		skuRepositoryItemMock.getPropertyValue("isAssemblyOffered") >> true
		skuRepositoryItemMock.getPropertyValue("personalizationType") >> "true"
		skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
		skuRepositoryItemMock.getPropertyValue("customizationOfferedFlag") >> true
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "sameDayDelAttributeKeyList") >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getZoomKeys()) >> ["SDD"]

		//Mock methods for getSKUDetails
		skuRepositoryItemMock.getPropertyValue("intlRestricted") >> "true"
		skuRepositoryItemMock.getPropertyValue("onSale") >> true
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["false"]
		catalogRepositoryMock.getItem(skuId, "sku") >> skuRepositoryItemMock
		2*globalRepositoryToolsMock.isCustomizationOfferedForSKU(skuRepositoryItemMock, siteId) >> true
		when:
		SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		skuDetailVO.getSkuId() == "785854"
		skuDetailVO.isIntlRestricted() == true
		skuDetailVO.isOnSale() == true
		skuDetailVO.isHasRebate() == true
		skuDetailVO.isHasSddAttribute() == true
		skuDetailVO.isLtlItem() == true
		skuDetailVO.isCustomizableRequired() == false
		skuDetailVO.isCustomizationOffered() == true
		skuDetailVO.getPersonalizationType() == "true"
		skuDetailVO.isWebOfferedFlag() == true
		skuDetailVO.isActiveFlag() == true
		skuDetailVO.isDisableFlag() == false
		skuDetailVO.isAssemblyOffered() == true
		1 * testObjCatalogTools.updateShippingMessageFlag(_, false, 0.0)
	}

	def "getSKUDetails with blank siteId. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = ""
		String skuId = "785854"

		when:

		testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSKUDetails with siteId as null. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = null
		String skuId = "785854"

		when:

		testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSKUDetails without skuRepositoryItem. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]

		when:

		testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSKUDetails with CurrentZipcodeVO in SessionBean. This TC tests if SddEligibility & PromoAttId is set."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		//Mock methods for SkuAttributeList
		repositoryItemMock.getRepositoryId() >> "SDD"
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
		repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		repositoryItemMock.getPropertyValue("startDate") >> new Date()
		repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
		repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
		skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
		skuRepositoryItemMock.getPropertyValue("customizationOfferedFlag") >> true
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> ["true"]

		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "sameDayDelAttributeKeyList") >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getZoomKeys()) >> ["SDD"]

		//Mock methods for getSKUDetails

		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock



		when:

		testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks if SddEligibility & PromoAttId is set.
		3 * sessionBeanMock.getCurrentZipcodeVO() >> sddZipcodeVOMock
		1 * sddZipcodeVOMock.isSddEligibility() >> true
		1 * sddZipcodeVOMock.getPromoAttId() >> "color"


	}

	def "getSKUDetails with CurrentZipcodeVO as null. This TC tests if SddEligibility & PromoAttId is set."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		//Mock methods for SkuAttributeList
		repositoryItemMock.getRepositoryId() >> "SDD"
		repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
		skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
		skuRepositoryItemMock.getPropertyValue("customizationOfferedFlag") >> true
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> ["true"]

		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "sameDayDelAttributeKeyList") >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getZoomKeys()) >> ["SDD"]

		//Mock methods for getSKUDetails
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks if SddEligibility & PromoAttId is not set.
		1 * sessionBeanMock.getCurrentZipcodeVO() >> null
		0 * sddZipcodeVOMock.isSddEligibility() >> true
		0 * sddZipcodeVOMock.getPromoAttId() >> "color"
	}

	def "getSKUDetails with SameDayDeliveryFlag as null. This TC tests if SddEligibility & PromoAttId is set."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		//Mock methods for SkuAttributeList
		repositoryItemMock.getRepositoryId() >> "SDD"
		repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
		skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
		skuRepositoryItemMock.getPropertyValue("customizationOfferedFlag") >> true
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> ["true"]

		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "sameDayDelAttributeKeyList") >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getZoomKeys()) >> ["SDD"]

		//Mock methods for getSKUDetails
		sddZipcodeVOMock.isSddEligibility() >> true
		sessionBeanMock.getCurrentZipcodeVO() >> sddZipcodeVOMock
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> [null]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks if SddEligibility & PromoAttId is not set.
		0 * sessionBeanMock.getCurrentZipcodeVO() >> null
		0 * sddZipcodeVOMock.isSddEligibility() >> true
		0 * sddZipcodeVOMock.getPromoAttId() >> "color"
	}

	def "getSKUDetail with empty rebateAttributeKeyList. This TC tests the HasRebate property of SKUDetailVO."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		//Mock methods for SkuAttributeList
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
		repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		repositoryItemMock.getPropertyValue("startDate") >> new Date()
		repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
		repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
		skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getRebateKey()) >> []
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "sameDayDelAttributeKeyList") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getZoomKeys()) >> ["SDD"]

		//Mock methods for SKUDetailVO
		repositoryItemMock.getRepositoryId() >> "SDD"
		skuRepositoryItemMock.getRepositoryId() >> "785854"
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuRepositoryItemMock.getPropertyValue("parentProducts") >> [].toSet()
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:
		def SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks getSKUDetailVO is invoked with HasRebate as false of BBBCatalogToolsImpl.
		skuDetailVO.isHasRebate() == false
	}

	def "getSKUDetail with null rebateAttributeKeyList. This TC tests the HasRebate property of SKUDetailVO."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		//Mock methods for SkuAttributeList
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
		repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		repositoryItemMock.getPropertyValue("startDate") >> new Date()
		repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
		repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
		skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getRebateKey()) >> null
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "sameDayDelAttributeKeyList") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getZoomKeys()) >> ["SDD"]

		//Mock methods for SKUDetailVO
		repositoryItemMock.getRepositoryId() >> "SDD"
		skuRepositoryItemMock.getRepositoryId() >> "785854"
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuRepositoryItemMock.getPropertyValue("parentProducts") >> [].toSet()
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:
		def SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks getSKUDetailVO is invoked with HasRebate as false of BBBCatalogToolsImpl.
		skuDetailVO.isHasRebate() == false
	}

	def "getSKUDetail with itemRebateKey not contained in attributeNameRepoItemMap. This TC tests the HasRebate property of SKUDetailVO."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		//Mock methods for SkuAttributeList
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
		repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		repositoryItemMock.getPropertyValue("startDate") >> new Date()
		repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
		repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
		skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getRebateKey()) >> ["key1"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "sameDayDelAttributeKeyList") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getZoomKeys()) >> ["SDD"]

		//Mock methods for SKUDetailVO
		repositoryItemMock.getRepositoryId() >> "SDD"
		skuRepositoryItemMock.getRepositoryId() >> "785854"
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuRepositoryItemMock.getPropertyValue("parentProducts") >> [].toSet()
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:
		def SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks getSKUDetailVO is invoked with HasRebate as false of BBBCatalogToolsImpl.
		skuDetailVO.isHasRebate() == false
	}

	def "getSKUDetail with empty sddAttributeKeyList. This TC tests the HasSddAttribute property of SKUDetailVO."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		//Mock methods for SkuAttributeList
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
		repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		repositoryItemMock.getPropertyValue("startDate") >> new Date()
		repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
		repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
		skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getRebateKey()) >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "sameDayDelAttributeKeyList") >> []
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getZoomKeys()) >> ["SDD"]

		//Mock methods for SKUDetailVO
		repositoryItemMock.getRepositoryId() >> "SDD"
		skuRepositoryItemMock.getRepositoryId() >> "785854"
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuRepositoryItemMock.getPropertyValue("parentProducts") >> [].toSet()
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:
		def SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks getSKUDetailVO is invoked with HasSddAttribute as false of BBBCatalogToolsImpl.
		skuDetailVO.isHasSddAttribute() == false
	}

	def "getSKUDetail with SDDOn as true. This TC tests the HasRebate property of SKUDetailVO."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		//Mock methods for SkuAttributeList
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
		repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		repositoryItemMock.getPropertyValue("startDate") >> new Date()
		repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
		repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
		skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getRebateKey()) >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "sameDayDelAttributeKeyList") >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getZoomKeys()) >> ["SDD"]

		//Mock methods for SKUDetailVO
		repositoryItemMock.getRepositoryId() >> "SDD"
		skuRepositoryItemMock.getRepositoryId() >> "785854"
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuRepositoryItemMock.getPropertyValue("parentProducts") >> [].toSet()
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]

		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:
		def SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks getSKUDetailVO is invoked with ParentProdId as null of BBBCatalogToolsImpl.

		skuDetailVO.hasSddAttribute == true
	}

	def "getSKUDetail with isCustomizableOn property as false. This TC tests the CustomizationOffered property of SKUDetailVO."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		//Mock methods for SKUDetailVO
		repositoryItemMock.getRepositoryId() >> "SDD"
		skuRepositoryItemMock.getRepositoryId() >> "785854"
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuRepositoryItemMock.getPropertyValue("forceBelowLine") >> true
		skuRepositoryItemMock.getPropertyValue("parentProducts") >> [].toSet()
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:
		def SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks getSKUDetailVO is invoked with isCustomizationOffered as false of BBBCatalogToolsImpl.
		skuDetailVO.isCustomizationOffered() == false
	}

	def "getSKUDetailVwith ShipMsgDisplayFlag as false. This TC tests the SkuBelowLine property of SKUDetailVO."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		//Mock methods for SKUDetailVO
		repositoryItemMock.getRepositoryId() >> "SDD"
		skuRepositoryItemMock.getRepositoryId() >> "785854"
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuRepositoryItemMock.getPropertyValue("forceBelowLine") >> true
		skuRepositoryItemMock.getPropertyValue("parentProducts") >> [].toSet()
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:
		def SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks getSKUDetailVO is invoked with isSkuBelowLine as true of BBBCatalogToolsImpl.
		0 * testObjCatalogTools.updateShippingMessageFlag(_, false, 0.0)
	}

	def "getSKUDetail with empty DynamicPricing as false. This TC tests the SkuBelowLine property of SKUDetailVO."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		//Mock methods for SKUDetailVO
		repositoryItemMock.getRepositoryId() >> "SDD"
		skuRepositoryItemMock.getRepositoryId() >> "785854"
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuRepositoryItemMock.getPropertyValue("forceBelowLine") >> true
		skuRepositoryItemMock.getPropertyValue("parentProducts") >> [].toSet()
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> []
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:
		def SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks getSKUDetailVO is invoked with isSkuBelowLine as true of BBBCatalogToolsImpl.
		0 * testObjCatalogTools.updateShippingMessageFlag(_, false, 0.0)
	}

	def "getSKUDetail with exception scenarios. This TC tests the RepositoryException is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> {throw new RepositoryException()}

		when:
		def SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks exception is thrown in BBBCatalogToolsImpl.
		BBBSystemException exception = thrown()
	}

	def "getSKUDetail with null DynamicPricing. This TC tests the SkuBelowLine property of SKUDetailVO."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		//Mock methods for SKUDetailVO
		repositoryItemMock.getRepositoryId() >> "SDD"
		skuRepositoryItemMock.getRepositoryId() >> "785854"
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuRepositoryItemMock.getPropertyValue("forceBelowLine") >> true
		skuRepositoryItemMock.getPropertyValue("parentProducts") >> [].toSet()
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> null
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:
		def SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks returnCountryFromSession is not invoked of BBBCatalogToolsImpl.
		0 * testObjCatalogTools.returnCountryFromSession()
	}
	def "getSKUDetail with null DynamicPricing. This TC tests the SkuBelowLine property of SKUDetailVO throw system exception."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"

		//Mock methods for SKUDetailVO
		repositoryItemMock.getRepositoryId() >> "SDD"
		skuRepositoryItemMock.getRepositoryId() >> "785854"
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuRepositoryItemMock.getPropertyValue("forceBelowLine") >> true
		skuRepositoryItemMock.getPropertyValue("parentProducts") >> [].toSet()
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> {throw new BBBSystemException("Mock of System Exception")}
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> null
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:
		SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks returnCountryFromSession is not invoked of BBBCatalogToolsImpl.
		0 * testObjCatalogTools.returnCountryFromSession()
	}
	def "getSKUDetail with null DynamicPricing. This TC tests the SkuBelowLine property of SKUDetailVO throw Business exception."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "785854"
		testObjCatalogTools.setLoggingTrace(true)
		//Mock methods for SKUDetailVO
		repositoryItemMock.getRepositoryId() >> "SDD"
		skuRepositoryItemMock.getRepositoryId() >> "785854"
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuRepositoryItemMock.getPropertyValue("forceBelowLine") >> true
		skuRepositoryItemMock.getPropertyValue("parentProducts") >> [].toSet()
		siteRepositoryItemMock.getPropertyValue("applicableShipMethods") >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "shipTo_POBoxOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "isCustomizableOn") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> {throw new BBBBusinessException("Mock of System Exception")}
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "DynamicPricing") >> null
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "enableDynamicRepoCall") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:
		SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, skuId)

		then:
		//checks returnCountryFromSession is not invoked of BBBCatalogToolsImpl.
		0 * testObjCatalogTools.returnCountryFromSession()
	}

	/////////////////////////////////TCs for getSKUDetails ends////////////////////////////////////////

	///////////////////////////////////TCs for getSKUDetails starts//////////////////////////////////////
	//Signature : public  SKUDetailVO getSKUDetails(String siteId, boolean checkSKUActiveFlag, String skuId)

	def "getSKUDetails. Thic TC is the happy flow of getSKUDetails"() {

		given:
		String siteId = "BedBathUS"
		boolean checkSKUActiveFlag = false
		String skuId = "95586"

		skuRepositoryItemMock.getRepositoryId() >> "95586"
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["false"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock
		when:

		SKUDetailVO skuDetailVO = testObjCatalogTools.getSKUDetails(siteId, checkSKUActiveFlag, skuId)

		then:
		skuDetailVO.getSkuId() == "95586"
	}

	def "getSKUDetails with siteId as blank. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = ""
		boolean checkSKUActiveFlag = false
		String skuId = "95586"

		when:

		testObjCatalogTools.getSKUDetails(siteId, checkSKUActiveFlag, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSKUDetails with null as siteId. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = null
		boolean checkSKUActiveFlag = false
		String skuId = "95586"

		when:

		testObjCatalogTools.getSKUDetails(siteId, checkSKUActiveFlag, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSKUDetails with null as skuRepositoryItem. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = "BedBathUS"
		boolean checkSKUActiveFlag = false
		String skuId = "95586"

		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]

		when:

		testObjCatalogTools.getSKUDetails(siteId, checkSKUActiveFlag, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSKUDetail with RepositoryException scenarios. This TC tests the RepositoryException is thrown."() {

		given:
		String siteId = "BedBathUS"
		boolean checkSKUActiveFlag = false
		String skuId = "95586"

		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> {throw new RepositoryException()}

		when:

		testObjCatalogTools.getSKUDetails(siteId, checkSKUActiveFlag, skuId)

		then:
		//checks exception is thrown in BBBCatalogToolsImpl.
		BBBSystemException exception = thrown()
	}

	/////////////////////////////////TCs for getSKUDetails ends////////////////////////////////////////

	///////////////////////////////////TCs for getMinimalSku starts//////////////////////////////////////
	//Signature : public SKUDetailVO getMinimalSku(final RepositoryItem skuRepositoryItem)

	def "getMinimalSku. This TC is the happy flow of getMinimalSku."() {

		given:
		skuRepositoryItemMock.getRepositoryId() >> "55588"
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true

		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]

		when:
		def SKUDetailVO skuDetailVO = testObjCatalogTools.getMinimalSku(skuRepositoryItemMock)

		then:
		//checks getSKUDetailVO is invoked with SKU Details of BBBCatalogToolsImpl.
		skuDetailVO.getSkuId() == "55588"
		skuDetailVO.isActiveFlag() == false
		skuDetailVO.isAssemblyOffered() == false
		skuDetailVO.isStoreSKU() == true
		skuDetailVO.isActiveFlag() == false
	}

	def "getMinimalSku. This TC tests the properties of SKUDetailVO."() {

		given:
		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuRepositoryItemMock.getPropertyValue("ltlFlag") >> true
		skuRepositoryItemMock.getPropertyValue("displayName") >> "true"
		skuRepositoryItemMock.getPropertyValue("webOffered") >> true
		skuRepositoryItemMock.getPropertyValue("disable") >> true
		skuRepositoryItemMock.getPropertyValue("isAssemblyOffered") >> true
		skuRepositoryItemMock.getPropertyValue("color") >> "true"
		skuRepositoryItemMock.getPropertyValue("size") >> "true"
		skuRepositoryItemMock.getPropertyValue("smallImage") >> "/store/images"
		skuRepositoryItemMock.getPropertyValue("mediumImage") >> "/store/images"
		skuRepositoryItemMock.getPropertyValue("upc") >> "true"
		skuRepositoryItemMock.getPropertyValue("bopusExclusion") >> true
		skuRepositoryItemMock.getPropertyValue("personalizationType") >> "true"

		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]

		when:
		def SKUDetailVO skuDetailVO = testObjCatalogTools.getMinimalSku(skuRepositoryItemMock)

		then:
		//checks getSKUDetailVO is invoked with SKU Details of BBBCatalogToolsImpl.
		skuDetailVO.isLtlItem() == true
		skuDetailVO.getDisplayName() == "true"
		skuDetailVO.isWebOfferedFlag() == true
		skuDetailVO.isDisableFlag() == true
		skuDetailVO.isAssemblyOffered() == true
		skuDetailVO.getColor() == "true"
		skuDetailVO.getSize() == "true"
		skuDetailVO.getUpc() == "true"
		skuDetailVO.isBopusExcludedForMinimalSku() == true
		skuDetailVO.getPersonalizationType() == "true"
		1 * testObjCatalogTools.updateShippingMessageFlag(_, false, 0.0)
	}


	def "getMinimalSku with MediumImage as null. This TC tests the property of SKUDetailVO."() {

		given:

		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuRepositoryItemMock.getPropertyValue("mediumImage") >> "/store/images"

		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]

		when:
		SKUDetailVO skuDetailVO = testObjCatalogTools.getMinimalSku(skuRepositoryItemMock)

		then:
		//checks getSKUDetailVO is invoked with SKU Details of BBBCatalogToolsImpl.
		ImageVO skuImages = skuDetailVO.getSkuImages()
		skuImages.getMediumImage().equals("/store/images")
	}

	def "getMinimalSku with ShipMsgDisplayFlag as false. This TC tests the invocation of updateShippingMessageFlag of BBBCatalogToolsImpl."() {

		given:

		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true

		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["false"]

		when:

		testObjCatalogTools.getMinimalSku(skuRepositoryItemMock)

		then:
		//checks updateShippingMessageFlag is not invoked of BBBCatalogToolsImpl.
		0 * testObjCatalogTools.updateShippingMessageFlag(_, false, 0.0)
	}


	def "getMinimalSku with exception scenarios. This TC tests if BBBSystemException is thrown."() {

		given:

		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true

		1*testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> {throw new BBBSystemException("Mockc of System Exception")}

		when:

		SKUDetailVO skuDetailVO = testObjCatalogTools.getMinimalSku(skuRepositoryItemMock)

		then:
		skuDetailVO.isActiveFlag() == false
	}

	///////////////////////////////////TCs for getMinimalSku ends///////////////////////////////////////////////////

	///////////////////////////////////TCs for getMinimalSku starts/////////////////////////////////////////
	//Signature : public  SKUDetailVO getMinimalSku(String skuId) /////////////////////////////////////////

	def "getMinimalSku. This TC is the happy flow for getMinimalSku"() {

		given:
		String skuId = "698556"

		skuRepositoryItemMock.getRepositoryId() >> "698556"
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock
		when:

		def SKUDetailVO skuDetailVO = testObjCatalogTools.getMinimalSku(skuId)

		then:
		//checks getSKUDetailVO is invoked with SKU Details of BBBCatalogToolsImpl.
		skuDetailVO.getSkuId() == "698556"
	}



	def "getMinimalSku with empty skuId. This TC tests if BBBBusinessException is thrown."() {

		given:
		String skuId = ""

		when:

		testObjCatalogTools.getMinimalSku(skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getMinimalSku with skuRepositoryItem as null. This TC tests if BBBBusinessException is thrown."() {

		given:
		String skuId = "698556"

		when:

		testObjCatalogTools.getMinimalSku(skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getMinimalSku with RepositoryException scenarios. This TC tests if BBBSystemException is thrown."() {

		given:
		String skuId = "698556"

		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> {throw new RepositoryException()}

		when:

		def SKUDetailVO skuDetailVO = testObjCatalogTools.getMinimalSku(skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBSystemException exception = thrown()
	}

	///////////////////////////////////TCs for getMinimalSku ends/////////////////////////////////////////

	///////////////////////////////////TCs for getEverLivingSKUDetails starts//////////////////////////////////////
	//Signature : public final SKUDetailVO getEverLivingSKUDetails(final String siteId, final String skuId, final boolean calculateAboveBelowLine)

	def "getEverLivingSKUDetails. This TC is the happy flow of getEverLivingSKUDetails."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "57477"
		boolean calculateAboveBelowLine = false

		//Mock methods for SkuAttributeList
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
		repositoryItemMock.getRepositoryId() >> "57477"
		repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		repositoryItemMock.getPropertyValue("startDate") >> new Date()
		repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
		repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
		skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
		skuRepositoryItemMock.getPropertyValue("customizationOfferedFlag") >> true
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getRebateKey()) >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "sameDayDelAttributeKeyList") >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getZoomKeys()) >> ["SDD"]

		//Mock methods for getEverLivingSKUDetails
		skuRepositoryItemMock.getRepositoryId() >> "57477"
		skuRepositoryItemMock.getPropertyValue("webOffered") >> false
		skuRepositoryItemMock.getPropertyValue("disable") >> true
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SHOW_SDD_ATTRIBUTE") >> ["true"]
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		def SKUDetailVO skuDetailVO = testObjCatalogTools.getEverLivingSKUDetails(siteId, skuId, calculateAboveBelowLine)

		then:
		//checks getSKUDetailVO is invoked with SKU Details of BBBCatalogToolsImpl.
		skuDetailVO.getSkuId() == "57477"
	}

	def "getEverLivingSKUDetails with siteId as null. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = null
		String skuId = "57477"
		boolean calculateAboveBelowLine = false

		when:

		testObjCatalogTools.getEverLivingSKUDetails(siteId, skuId, calculateAboveBelowLine)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getEverLivingSKUDetails with empty siteId. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = ""
		String skuId = "57477"
		boolean calculateAboveBelowLine = false

		when:

		testObjCatalogTools.getEverLivingSKUDetails(siteId, skuId, calculateAboveBelowLine)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getEverLivingSKUDetails with skuRepositoryItem as null. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "57477"
		boolean calculateAboveBelowLine = false

		when:

		testObjCatalogTools.getEverLivingSKUDetails(siteId, skuId, calculateAboveBelowLine)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getEverLivingSKUDetails with repository exception scenarios. This TC test that repository exception is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "57477"
		boolean calculateAboveBelowLine = false

		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> {throw new RepositoryException()}

		when:
		testObjCatalogTools.getEverLivingSKUDetails(siteId, skuId, calculateAboveBelowLine)

		then:
		//checks if BBBBusinessException is thrown.
		BBBSystemException exception = thrown()
	}

	///////////////////////////////////TCs for getEverLivingSKUDetails ends//////////////////////////////////////

	///////////////////////////////////TCs for getRecommendedSKU starts/////////////////////////////////////////
	//Signature : public RecommendedSkuVO  getRecommendedSKU (String siteId, BBBOrderImpl order) //////////////

	def "getRecommendedSKU. This TC is the happy flow of getRecommendedSKU"() {

		given:
		String siteId = "TBS_BedBathUS"
		RepositoryItem salePriceRepositoryItemMock = Mock()
		RepositoryItem listPriceRepositoryItemMock = Mock()

		requestMock.getHeader("X-bbb-channel") >> "MobileApp"
		repositoryItemMock.getPropertyValue("listPrice") >> new Double(5.63)
		repositoryItemMock.getPropertyValue("startDate") >> new Date()
		repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
		repositoryItemMock.getPropertyValue("webOffered") >> false
		repositoryItemMock.getPropertyValue("disable") >> false
		repositoryItemMock.getPropertyValue("description") >> "Latest Product"
		repositoryItem.setProperties("description":"Latest Product")
		repositoryItem.setProperties("recommSku":repositoryItemMock)
		repositoryItem.setRepositoryId("477888")
		testObjCatalogTools.getBbbInventoryManager().getProductAvailability(siteId, repositoryItemMock.getRepositoryId(),"productDisplay",0) >> 0
		bbbCommerceItemMock.getCatalogRefId() >> "cat3435535"
		bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock]
		priceListManagerMock.getPriceList(_,"salePriceList") >> salePriceRepositoryItemMock
		priceListManagerMock.getPriceList(_,"priceList") >> listPriceRepositoryItemMock
		priceListManagerMock.getPrice(salePriceRepositoryItemMock, _,_) >> repositoryItemMock
		priceListManagerMock.getPrice(listPriceRepositoryItemMock, _,_) >> repositoryItemMock
		1*testObjCatalogTools.getRqlQueryStatement(_) >> rqlStatementMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock,repositoryItemMock,repositoryItemMock,repositoryItemMock,repositoryItemMock,repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PROPERTY_NAME_RECOMM_SKU) >> repositoryItemMock
		4*bbbInventoryManagerMock.getProductAvailability( "TBS_BedBathUS", "sku12346",BBBInventoryManager.PRODUCT_DISPLAY,0) >> {throw new BBBBusinessException("Mock Business exception")} >> {throw new BBBSystemException("Mock system exception")} >> 1 >> 0
		repositoryItemMock.getRepositoryId() >> "cat3435535" >> "sku12346"
		1* repositoryMock.getView(BBBCatalogConstants.ITEM_DESCRIPTOR_RECOMMENDED_SKUS) >> repositoryViewMock
		6*globalRepositoryToolsMock.isSkuActive(repositoryItemMock,_) >> false >> true
		1*catalogRepositoryMock.getItem("sku12346",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		Set<RepositoryItem> parentProducts = new HashSet<RepositoryItem>()
		parentProducts.add(repositoryItemMock)
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> parentProducts
		testObjCatalogTools.isProductActive(repositoryItemMock) >> true
		testObjCatalogTools.getSKUDetails(siteId, "sku12346", false) >> skuDetailVOMock
		when:

		RecommendedSkuVO recommendedSkuVO = testObjCatalogTools.getRecommendedSKU(siteId, bbbOrderImplMock)

		then:
		//checks recommendedSkuVO is invoked with SKU Details of BBBCatalogToolsImpl.
		recommendedSkuVO.getRecommSKUVO() == skuDetailVOMock
		recommendedSkuVO.getSalePrice() == 5.63
		recommendedSkuVO.getComment() == "Latest Product"
		recommendedSkuVO.getCartSkuId() == "sku12346"
	}


	def "getRecommendedSKU with exception scenarios. This TC tests BBBSystemException is thrown"() {

		given:
		String siteId = "TBS_BedBathUS"
		RepositoryItem parentProductRepItem = Mock()
		repositoryItemMock.getRepositoryId() >> "441414"
		requestMock.getHeader("X-bbb-channel") >> "MobileApp"
		repositoryItemMock.getPropertyValue("webOffered") >> false
		repositoryItem.setProperties("recommSku":repositoryItemMock)
		rqlStatementMock.executeQuery(repositoryViewMock, _) >>  [repositoryItem,repositoryItem]
		bbbCommerceItemMock.getCatalogRefId() >> "cat3435535"
		bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock]
		bbbOrderImplMock.getOrderSkus() >> ["order1"]
		parentProductRepItem.getPropertyValue("webOffered") >> false
		parentProductRepItem.getRepositoryId() >> "144444"
		skuRepositoryItemMock.getPropertyValue("parentProducts") >> [parentProductRepItem].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getSKUDetails(siteId, repositoryItemMock.getRepositoryId(), false) >> {throw new BBBSystemException("Mock of system exception")}
		testObjCatalogTools.getCatalogRepository().getItem("441414", "sku") >> skuRepositoryItemMock
		1*testObjCatalogTools.getRqlQueryStatement(_) >> rqlStatementMock
		repositoryMock.getView(BBBCatalogConstants.ITEM_DESCRIPTOR_RECOMMENDED_SKUS) >> repositoryViewMock
		globalRepositoryToolsMock.isSkuActive(repositoryItemMock,_) >> true
		bbbInventoryManagerMock.getProductAvailability( "TBS_BedBathUS", "441414",BBBInventoryManager.PRODUCT_DISPLAY,0) >> 0
		when:

		RecommendedSkuVO recommendedSkuVO = testObjCatalogTools.getRecommendedSKU(siteId, bbbOrderImplMock)

		then:
		//checks if BBBSystemException is thrown.
		recommendedSkuVO == null

	}
	def "getRecommendedSKU with exception scenarios. This TC tests Business exception is thrown"() {

		given:
		String siteId = "TBS_BedBathUS"
		RepositoryItem parentProductRepItem = Mock()
		repositoryItemMock.getRepositoryId() >> "441414"
		requestMock.getHeader("X-bbb-channel") >> "MobileApp"
		repositoryItemMock.getPropertyValue("webOffered") >> false
		repositoryItem.setProperties("recommSku":repositoryItemMock)
		rqlStatementMock.executeQuery(repositoryViewMock, _) >>  [repositoryItem,repositoryItem]
		bbbCommerceItemMock.getCatalogRefId() >> "cat3435535"
		bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock]
		bbbOrderImplMock.getOrderSkus() >> ["order1"]
		parentProductRepItem.getPropertyValue("webOffered") >> false
		parentProductRepItem.getRepositoryId() >> "144444"
		skuRepositoryItemMock.getPropertyValue("parentProducts") >> [parentProductRepItem].toSet()
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getSKUDetails(siteId, repositoryItemMock.getRepositoryId(), false) >> {throw new BBBBusinessException("Mock of Business exception")}
		testObjCatalogTools.getCatalogRepository().getItem("441414", "sku") >> skuRepositoryItemMock
		1*testObjCatalogTools.getRqlQueryStatement(_) >> rqlStatementMock
		repositoryMock.getView(BBBCatalogConstants.ITEM_DESCRIPTOR_RECOMMENDED_SKUS) >> repositoryViewMock
		globalRepositoryToolsMock.isSkuActive(repositoryItemMock,_) >> true
		bbbInventoryManagerMock.getProductAvailability( "TBS_BedBathUS", "441414",BBBInventoryManager.PRODUCT_DISPLAY,0) >> 0
		when:

		RecommendedSkuVO recommendedSkuVO = testObjCatalogTools.getRecommendedSKU(siteId, bbbOrderImplMock)

		then:
		//checks if BBBSystemException is thrown.
		recommendedSkuVO == null

	}
	def "getRecommendedSKU with exception scenarios. This TC IllegalArgumentException exception is thrown"() {
		given:
		String siteId = "TBS_BedBathUS"
		RepositoryItem parentProductRepItem = Mock()
		repositoryItemMock.getRepositoryId() >> "441414"
		requestMock.getHeader("X-bbb-channel") >> "MobileApp"
		repositoryItem.setProperties("recommSku":repositoryItemMock)
		rqlStatementMock.executeQuery(repositoryViewMock, _) >>  {throw new IllegalArgumentException()}
		bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock]
		1*testObjCatalogTools.getRqlQueryStatement(_) >> rqlStatementMock
		repositoryMock.getView(BBBCatalogConstants.ITEM_DESCRIPTOR_RECOMMENDED_SKUS) >> repositoryViewMock
		bbbCommerceItemMock.getCatalogRefId() >> "cat3435535"
		when:

		RecommendedSkuVO recommendedSkuVO = testObjCatalogTools.getRecommendedSKU(siteId, bbbOrderImplMock)

		then:
		//checks if BBBSystemException is thrown.
		recommendedSkuVO == null
	}

	def "getRecommendedSKU with exception scenarios. This TC RepositoryException exception is thrown"() {
		given:
		String siteId = "TBS_BedBathUS"
		RepositoryItem parentProductRepItem = Mock()
		repositoryItemMock.getRepositoryId() >> "441414"
		requestMock.getHeader("X-bbb-channel") >> "MobileApp"
		repositoryItem.setProperties("recommSku":repositoryItemMock)
		bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock]
		repositoryMock.getView(BBBCatalogConstants.ITEM_DESCRIPTOR_RECOMMENDED_SKUS) >> {throw new RepositoryException()}
		bbbCommerceItemMock.getCatalogRefId() >> "cat3435535"
		when:

		RecommendedSkuVO recommendedSkuVO = testObjCatalogTools.getRecommendedSKU(siteId, bbbOrderImplMock)

		then:
		//checks if BBBSystemException is thrown.
		recommendedSkuVO == null
	}
	def "getRecommendedSKU with recommItems as null. This TC skus in order is []"() {

		given:
		String siteId = "TBS_BedBathUS"

		repositoryItemMock.getPropertyValue("webOffered") >> false

		bbbOrderImplMock.getCommerceItems() >> []
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]

		when:

		RecommendedSkuVO recommendedSkuVO = testObjCatalogTools.getRecommendedSKU(siteId, bbbOrderImplMock)

		then:
		//checks recommendedSkuVO is invoked with SKU Details of BBBCatalogToolsImpl.
		recommendedSkuVO == null

	}

	///////////////////////////////////TCs for getRecommendedSKU ends/////////////////////////////////////////

	///////////////////////////////////TCs for getSkuThreshold starts/////////////////////////////////////////
	//Signature : public final ThresholdVO getSkuThreshold(final String siteId, final String skuId) ///////////

	def "getSkuThreshold. This TC is happy flow which tests thresholdVO properties in getSkuThreshold."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "487878"

		repositoryItem.setProperties("thresholdAvailable":56666699996)
		repositoryItem.setProperties("thresholdLimited":48866699996)
		testObjCatalogTools.executeRQLQuery(*_) >> repositoryItemMockList
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "EOM_Threshold_Flag") >> ["true"]

		when:

		ThresholdVO thresholdVO = testObjCatalogTools.getSkuThreshold(siteId, skuId)

		then:
		//checks thresholdVO is invoked with SKU Details of BBBCatalogToolsImpl.
		thresholdVO.getThresholdAvailable() == 56666699996
		thresholdVO.getThresholdLimited() == 48866699996
	}

	def "getSkuThreshold with null skuId. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = null

		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "EOM_Threshold_Flag") >> ["true"]

		when:

		testObjCatalogTools.getSkuThreshold(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSkuThreshold with empty skuId. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = ""

		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "EOM_Threshold_Flag") >> ["true"]

		when:

		testObjCatalogTools.getSkuThreshold(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSkuThreshold with null siteId. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = null
		String skuId = "487878"

		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "EOM_Threshold_Flag") >> ["true"]

		when:

		testObjCatalogTools.getSkuThreshold(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSkuThreshold with empty siteId. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = ""
		String skuId = "487878"

		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "EOM_Threshold_Flag") >> ["true"]

		when:

		testObjCatalogTools.getSkuThreshold(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSkuThreshold with EOM_Threshold_Flag as false. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "487878"

		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "EOM_Threshold_Flag") >> ["false"]

		when:

		testObjCatalogTools.getSkuThreshold(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSkuThreshold with null skuRepositoryItemMock. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "487878"

		testObjCatalogTools.executeRQLQuery(*_) >> null
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "EOM_Threshold_Flag") >> ["true"]

		when:

		testObjCatalogTools.getSkuThreshold(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getSkuThreshold with null query . This TC tests for thresholdVO properties in getSkuThreshold."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "487878"
		def RepositoryItemMock jdaRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] jdaRepositoryItemMockList = [jdaRepositoryItem]

		testObjCatalogTools.executeRQLQuery(null,_,_,_) >> null
		jdaRepositoryItem.setProperties("thresholdAvailable":64466699996)
		jdaRepositoryItem.setProperties("thresholdLimited":76666699996)
		testObjCatalogTools.executeRQLQuery("jdaDeptId is null and   jdaSubDeptId is null and  jdaClass is null ",_,_,_) >> jdaRepositoryItemMockList
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "EOM_Threshold_Flag") >> ["true"]

		when:

		ThresholdVO thresholdVO = testObjCatalogTools.getSkuThreshold(siteId, skuId)

		then:
		//checks thresholdVO is invoked with SKU Details of BBBCatalogToolsImpl.
		thresholdVO.getThresholdAvailable() == 64466699996
		thresholdVO.getThresholdLimited() == 76666699996
	}

	def "getSkuThreshold with query length <= 0. This TC tests for thresholdVO properties in getSkuThreshold."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "487878"
		def RepositoryItemMock jdaRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] jdaRepositoryItemMockList = [jdaRepositoryItem]

		testObjCatalogTools.executeRQLQuery(null,_,_,_) >> []
		jdaRepositoryItem.setProperties("thresholdAvailable":64466699996)
		jdaRepositoryItem.setProperties("thresholdLimited":76666699996)
		testObjCatalogTools.executeRQLQuery("jdaDeptId is null and   jdaSubDeptId is null and  jdaClass is null ",_,_,_) >> jdaRepositoryItemMockList
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "EOM_Threshold_Flag") >> ["true"]

		when:

		ThresholdVO thresholdVO = testObjCatalogTools.getSkuThreshold(siteId, skuId)

		then:
		//checks thresholdVO is invoked with SKU Details of BBBCatalogToolsImpl.
		thresholdVO.getThresholdAvailable() == 64466699996
		thresholdVO.getThresholdLimited() == 76666699996
	}

	def "getSkuThreshold with jda queries from repository. This TC tests for thresholdVO properties in getSkuThreshold."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "487878"
		def RepositoryItemMock jdaRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] jdaRepositoryItemMockList = [jdaRepositoryItem]
		def RepositoryItem jdaDeptRepositoryItemMock = Mock()
		def RepositoryItem jdaSubDeptRepositoryItemMock = Mock()

		testObjCatalogTools.executeRQLQuery(null,_,_,_) >> null
		jdaRepositoryItem.setProperties("thresholdAvailable":15586699996)
		jdaRepositoryItem.setProperties("thresholdLimited":69976699996)
		testObjCatalogTools.executeRQLQuery("jdaDeptId = ?0  and  jdaSubDeptId = ?1 and  jdaClass = ?2 ",_,_,_) >> jdaRepositoryItemMockList
		skuRepositoryItemMock.getPropertyValue("jdaDept") >> jdaDeptRepositoryItemMock
		((RepositoryItem)skuRepositoryItemMock.getPropertyValue("jdaDept")).getRepositoryId() >> "1111"
		skuRepositoryItemMock.getPropertyValue("jdaSubDept") >> jdaSubDeptRepositoryItemMock
		((RepositoryItem)skuRepositoryItemMock.getPropertyValue("jdaSubDept")).getRepositoryId() >> "4444"
		skuRepositoryItemMock.getPropertyValue("jdaClass") >> "jdaClass"
		skuRepositoryItemMock.getPropertyValue("jdaClass") >> "7777"
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "EOM_Threshold_Flag") >> ["true"]

		when:

		ThresholdVO thresholdVO = testObjCatalogTools.getSkuThreshold(siteId, skuId)

		then:
		//checks thresholdVO is invoked with SKU Details of BBBCatalogToolsImpl.
		thresholdVO.getThresholdAvailable() == 15586699996
		thresholdVO.getThresholdLimited() == 69976699996
	}

	def "getSkuThreshold with jda(jdaSubDept with '_') queries from repository. This TC tests for thresholdVO properties in getSkuThreshold."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "487878"
		def RepositoryItemMock jdaRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] jdaRepositoryItemMockList = [jdaRepositoryItem]
		def RepositoryItem jdaDeptRepositoryItemMock = Mock()
		def RepositoryItem jdaSubDeptRepositoryItemMock = Mock()

		testObjCatalogTools.executeRQLQuery(null,_,_,_) >> null
		jdaRepositoryItem.setProperties("thresholdAvailable":87786699996)
		jdaRepositoryItem.setProperties("thresholdLimited":55586699996)
		testObjCatalogTools.executeRQLQuery("jdaDeptId = ?0  and  jdaSubDeptId = ?1 and  jdaClass = ?2 ",_,_,_) >> jdaRepositoryItemMockList
		skuRepositoryItemMock.getPropertyValue("jdaDept") >> jdaDeptRepositoryItemMock
		((RepositoryItem)skuRepositoryItemMock.getPropertyValue("jdaDept")).getRepositoryId() >> "1111"
		skuRepositoryItemMock.getPropertyValue("jdaSubDept") >> jdaSubDeptRepositoryItemMock
		((RepositoryItem)skuRepositoryItemMock.getPropertyValue("jdaSubDept")).getRepositoryId() >> "4444_jdaSubDept"
		skuRepositoryItemMock.getPropertyValue("jdaClass") >> "jdaClass"
		skuRepositoryItemMock.getPropertyValue("jdaClass") >> "7777"
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "EOM_Threshold_Flag") >> ["true"]

		when:

		ThresholdVO thresholdVO = testObjCatalogTools.getSkuThreshold(siteId, skuId)

		then:
		//checks thresholdVO is invoked with SKU Details of BBBCatalogToolsImpl.
		thresholdVO.getThresholdAvailable() == 87786699996
		thresholdVO.getThresholdLimited() == 55586699996
	}

	def "getSkuThreshold with jda queries from repository as empty. This TC tests for thresholdVO properties in getSkuThreshold."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "487878"
		RepositoryItemMock jdaRepositoryItem = new RepositoryItemMock()
		RepositoryItem[] jdaRepositoryItemMockList = [jdaRepositoryItem]
		RepositoryItem jdaDeptRepositoryItemMock = Mock()
		RepositoryItem jdaSubDeptRepositoryItemMock = Mock()

		testObjCatalogTools.executeRQLQuery(null,_,_,_) >> null
		jdaRepositoryItem.setProperties("thresholdAvailable":15586699996)
		jdaRepositoryItem.setProperties("thresholdLimited":69976699996)
		testObjCatalogTools.executeRQLQuery("jdaDeptId = ?0  and  jdaSubDeptId = ?1 and  jdaClass = ?2 ",_,_,_) >> []
		skuRepositoryItemMock.getPropertyValue("jdaDept") >> jdaDeptRepositoryItemMock
		((RepositoryItem)skuRepositoryItemMock.getPropertyValue("jdaDept")).getRepositoryId() >> "1111"
		skuRepositoryItemMock.getPropertyValue("jdaSubDept") >> jdaSubDeptRepositoryItemMock
		((RepositoryItem)skuRepositoryItemMock.getPropertyValue("jdaSubDept")).getRepositoryId() >> "4444"
		skuRepositoryItemMock.getPropertyValue("jdaClass") >> "jdaClass"
		skuRepositoryItemMock.getPropertyValue("jdaClass") >> "7777"
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "EOM_Threshold_Flag") >> ["true"]

		when:

		ThresholdVO thresholdVO = testObjCatalogTools.getSkuThreshold(siteId, skuId)

		then:
		thresholdVO == null
	}
	def "getSkuThreshold with jda queries from repository as null. This TC tests for thresholdVO properties in getSkuThreshold."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "487878"
		RepositoryItemMock jdaRepositoryItem = new RepositoryItemMock()
		RepositoryItem[] jdaRepositoryItemMockList = [jdaRepositoryItem]
		RepositoryItem jdaDeptRepositoryItemMock = Mock()
		RepositoryItem jdaSubDeptRepositoryItemMock = Mock()

		testObjCatalogTools.executeRQLQuery(null,_,_,_) >> null
		jdaRepositoryItem.setProperties("thresholdAvailable":15586699996)
		jdaRepositoryItem.setProperties("thresholdLimited":69976699996)
		testObjCatalogTools.executeRQLQuery("jdaDeptId = ?0  and  jdaSubDeptId = ?1 and  jdaClass = ?2 ",_,_,_) >> null
		skuRepositoryItemMock.getPropertyValue("jdaDept") >> jdaDeptRepositoryItemMock
		((RepositoryItem)skuRepositoryItemMock.getPropertyValue("jdaDept")).getRepositoryId() >> "1111"
		skuRepositoryItemMock.getPropertyValue("jdaSubDept") >> jdaSubDeptRepositoryItemMock
		((RepositoryItem)skuRepositoryItemMock.getPropertyValue("jdaSubDept")).getRepositoryId() >> "4444"
		skuRepositoryItemMock.getPropertyValue("jdaClass") >> "jdaClass"
		skuRepositoryItemMock.getPropertyValue("jdaClass") >> "7777"
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "EOM_Threshold_Flag") >> ["true"]

		when:

		ThresholdVO thresholdVO = testObjCatalogTools.getSkuThreshold(siteId, skuId)

		then:
		thresholdVO == null
	}

	def "getSkuThreshold with exception scenarios . This TC tests for thresholdVO properties in getSkuThreshold."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "487878"

		testObjCatalogTools.executeRQLQuery(null,_,_,_) >> null
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> {throw new RepositoryException()}
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "EOM_Threshold_Flag") >> ["true"]

		when:

		ThresholdVO thresholdVO = testObjCatalogTools.getSkuThreshold(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBSystemException exception = thrown()
	}
	def"Gets the threshold vo"(){
		when:
		ThresholdVO thresholdVO = testObjCatalogTools.getThresholdVO(repositoryItem)
		then:
		thresholdVO.getThresholdAvailable() == 0
		thresholdVO.getThresholdLimited() == 0
	}

	///////////////////////////////////TCs for getSkuThreshold ends/////////////////////////////////////////////

	///////////////////////////////////TCs for isSKUBelowLine starts/////////////////////////////////////////
	//Signature : public final boolean isSKUBelowLine(final String siteId, final String skuId) ////////////////

	def "isSKUBelowLine. Thic TC is the happy flow for isSKUBelowLine."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "89969"

		testObjCatalogTools.getInventoryManager().getAltAfs(skuId, siteId) >> 66556699996
		testObjCatalogTools.getInventoryManager().getAfs(skuId, siteId) >> 44886699996
		testObjCatalogTools.getInventoryManager().getIgr(skuId, siteId) >> 22886699996
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		boolean isSKUBelowLine = testObjCatalogTools.isSKUBelowLine(siteId, skuId)

		then:
		//checks isSKUBelowLine is invoked with isSKUBelowLine of BBBCatalogToolsImpl.
		isSKUBelowLine == false
	}

	def "isSKUBelowLine with forceBelowLine as true in skuRepositoryItem. Thic TC tests the value of isSKUBelowLine."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "89969"

		skuRepositoryItemMock.getPropertyValue("forceBelowLine") >> true
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		boolean isSKUBelowLine = testObjCatalogTools.isSKUBelowLine(siteId, skuId)

		then:
		//checks isSKUBelowLine is invoked with isSKUBelowLine of BBBCatalogToolsImpl.
		isSKUBelowLine == true
	}

	def "isSKUBelowLine with BedBathCanada as siteId and ecomFullfillment = 'E'. Thic TC tests the value of isSKUBelowLine."() {

		given:
		String siteId = "BedBathCanada"
		String skuId = "89969"

		skuRepositoryItemMock.getPropertyValue("forceBelowLine") >> false
		skuRepositoryItemMock.getPropertyValue("eComFulfillment") >> "E"
		testObjCatalogTools.getInventoryManager().getAltAfs(skuId, siteId) >> 66556699996
		testObjCatalogTools.getInventoryManager().getAfs(skuId, siteId) >> 44886699996
		testObjCatalogTools.getInventoryManager().getIgr(skuId, siteId) >> 22886699996
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		boolean isSKUBelowLine = testObjCatalogTools.isSKUBelowLine(siteId, skuId)

		then:
		//checks isSKUBelowLine is invoked with isSKUBelowLine of BBBCatalogToolsImpl.
		isSKUBelowLine == false
	}

	def "isSKUBelowLine with BedBathCanada as siteId and without ecomFullfillment. Thic TC tests the value of isSKUBelowLine."() {

		given:
		String siteId = "BedBathCanada"
		String skuId = "89969"

		skuRepositoryItemMock.getPropertyValue("forceBelowLine") >> false
		testObjCatalogTools.getInventoryManager().getAltAfs(skuId, siteId) >> 66556699996
		testObjCatalogTools.getInventoryManager().getAfs(skuId, siteId) >> 44886699996
		testObjCatalogTools.getInventoryManager().getIgr(skuId, siteId) >> 22886699996
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		boolean isSKUBelowLine = testObjCatalogTools.isSKUBelowLine(siteId, skuId)

		then:
		//checks isSKUBelowLine is invoked with isSKUBelowLine of BBBCatalogToolsImpl.
		isSKUBelowLine == false
	}

	def "isSKUBelowLine with BedBathCanada as siteId and ecomFullfillment != 'E'. Thic TC tests the value of isSKUBelowLine."() {

		given:
		String siteId = "BedBathCanada"
		String skuId = "89969"

		skuRepositoryItemMock.getPropertyValue("forceBelowLine") >> false
		skuRepositoryItemMock.getPropertyValue("eComFulfillment") >> "W"
		testObjCatalogTools.getInventoryManager().getAltAfs(skuId, siteId) >> 66556699996
		testObjCatalogTools.getInventoryManager().getAfs(skuId, siteId) >> 44886699996
		testObjCatalogTools.getInventoryManager().getIgr(skuId, siteId) >> 22886699996
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		boolean isSKUBelowLine = testObjCatalogTools.isSKUBelowLine(siteId, skuId)

		then:
		//checks isSKUBelowLine is invoked with isSKUBelowLine of BBBCatalogToolsImpl.
		isSKUBelowLine == false
	}

	def "isSKUBelowLine with BedBathCanada as siteId and ecomFullfillment != 'E' and igr > 0. Thic TC tests the value of isSKUBelowLine."() {

		given:
		String siteId = "BedBathCanada"
		String skuId = "89969"

		skuRepositoryItemMock.getPropertyValue("forceBelowLine") >> false
		skuRepositoryItemMock.getPropertyValue("eComFulfillment") >> "W"
		testObjCatalogTools.getInventoryManager().getAltAfs(skuId, siteId) >> -66556699996
		testObjCatalogTools.getInventoryManager().getAfs(skuId, siteId) >> 44886699996
		testObjCatalogTools.getInventoryManager().getIgr(skuId, siteId) >> 22886699996
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		boolean isSKUBelowLine = testObjCatalogTools.isSKUBelowLine(siteId, skuId)

		then:
		//checks isSKUBelowLine is invoked with isSKUBelowLine of BBBCatalogToolsImpl.
		isSKUBelowLine == false
	}


	def "isSKUBelowLine with BedBathCanada as siteId and ecomFullfillment != 'E' and igr < 0. Thic TC tests the value of isSKUBelowLine."() {

		given:
		String siteId = "BedBathCanada"
		String skuId = "89969"

		skuRepositoryItemMock.getPropertyValue("forceBelowLine") >> false
		skuRepositoryItemMock.getPropertyValue("eComFulfillment") >> "W"
		testObjCatalogTools.getInventoryManager().getAltAfs(skuId, siteId) >> -66556699996
		testObjCatalogTools.getInventoryManager().getAfs(skuId, siteId) >> 44886699996
		testObjCatalogTools.getInventoryManager().getIgr(skuId, siteId) >> -22886699996
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		boolean isSKUBelowLine = testObjCatalogTools.isSKUBelowLine(siteId, skuId)

		then:
		//checks isSKUBelowLine is invoked with isSKUBelowLine of BBBCatalogToolsImpl.
		isSKUBelowLine == true
	}

	def "isSKUBelowLine with BedBathCanada as siteId and igr > 0. Thic TC tests the value of isSKUBelowLine."() {

		given:
		String siteId = "BedBathCanada"
		String skuId = "89969"

		skuRepositoryItemMock.getPropertyValue("forceBelowLine") >> false
		skuRepositoryItemMock.getPropertyValue("eComFulfillment") >> "E"
		testObjCatalogTools.getInventoryManager().getAltAfs(skuId, siteId) >> -66556699996
		testObjCatalogTools.getInventoryManager().getAfs(skuId, siteId) >> 44886699996
		testObjCatalogTools.getInventoryManager().getIgr(skuId, siteId) >> 22886699996
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		boolean isSKUBelowLine = testObjCatalogTools.isSKUBelowLine(siteId, skuId)

		then:
		//checks isSKUBelowLine is invoked with isSKUBelowLine of BBBCatalogToolsImpl.
		isSKUBelowLine == false
	}


	def "isSKUBelowLine with BedBathCanada as siteId and igr < 0. Thic TC tests the value of isSKUBelowLine."() {

		given:
		String siteId = "BedBathCanada"
		String skuId = "89969"

		skuRepositoryItemMock.getPropertyValue("forceBelowLine") >> false
		skuRepositoryItemMock.getPropertyValue("eComFulfillment") >> "E"
		testObjCatalogTools.getInventoryManager().getAltAfs(skuId, siteId) >> -66556699996
		testObjCatalogTools.getInventoryManager().getAfs(skuId, siteId) >> 44886699996
		testObjCatalogTools.getInventoryManager().getIgr(skuId, siteId) >> -22886699996
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		boolean isSKUBelowLine = testObjCatalogTools.isSKUBelowLine(siteId, skuId)

		then:
		//checks isSKUBelowLine is invoked with isSKUBelowLine of BBBCatalogToolsImpl.
		isSKUBelowLine
	}

	def "isSKUBelowLine with igr > 0. Thic TC tests the value of isSKUBelowLine."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "89969"

		testObjCatalogTools.getInventoryManager().getAltAfs(skuId, siteId) >> -66556699996
		testObjCatalogTools.getInventoryManager().getAfs(skuId, siteId) >> 44886699996
		testObjCatalogTools.getInventoryManager().getIgr(skuId, siteId) >> 22886699996
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		boolean isSKUBelowLine = testObjCatalogTools.isSKUBelowLine(siteId, skuId)

		then:
		//checks isSKUBelowLine is invoked with isSKUBelowLine of BBBCatalogToolsImpl.
		isSKUBelowLine == false
	}


	def "isSKUBelowLine with igr < 0. Thic TC tests the value of isSKUBelowLine."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "89969"

		testObjCatalogTools.getInventoryManager().getAltAfs(skuId, siteId) >> -66556699996
		testObjCatalogTools.getInventoryManager().getAfs(skuId, siteId) >> 44886699996
		testObjCatalogTools.getInventoryManager().getIgr(skuId, siteId) >> -22886699996
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		boolean isSKUBelowLine = testObjCatalogTools.isSKUBelowLine(siteId, skuId)

		then:
		//checks isSKUBelowLine is invoked with isSKUBelowLine of BBBCatalogToolsImpl.
		isSKUBelowLine
	}

	def "isSKUBelowLine with null skuRepositoryItemMock. Thic TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "89969"

		when:

		testObjCatalogTools.isSKUBelowLine(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "isSKUBelowLine with exception scenarios. Thic TC tests if RepositoryException is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "89969"

		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> {throw new RepositoryException()}
		when:

		boolean isSKUBelowLine = testObjCatalogTools.isSKUBelowLine(siteId, skuId)

		then:
		//checks if BBBSystemException is thrown.
		BBBSystemException exception = thrown()
	}
	///////////////////////////////////TCs for isSKUBelowLine ends /////////////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for isCustomizationRequiredForSKU starts //////////////////////////////////////////////////
	//Signature : public final boolean isCustomizationRequiredForSKU(final RepositoryItem skuRepositoryItem , final String siteId) //

	def "isCustomizationRequiredForSKU. This TC is the happy flow which tests for isCustomizationRequiredForSKU in isCustomizationRequiredForSKU."() {

		given:
		String siteId = "BedBathUS"

		skuRepositoryItemMock.getPropertyValue("customizationOfferedFlag") >> true
		skuRepositoryItemMock.getPropertyValue("personalizationType") >> "CR"
		skuRepositoryItemMock.getPropertyValue("eligibleCustomizationCodes") >> "05"
		1*globalRepositoryToolsMock.isCustomizationOfferedForSKU(skuRepositoryItemMock, siteId) >> true
		when:

		boolean isCustomizationRequiredForSKU = testObjCatalogTools.isCustomizationRequiredForSKU(skuRepositoryItemMock, siteId)

		then:
		//checks isCustomizationRequiredForSKU is invoked with isCustomizationRequiredForSKU of BBBCatalogToolsImpl.
		isCustomizationRequiredForSKU == true
	}
	def "isCustomizationRequiredForSKU with personalizationType as empty. This TC tests for isCustomizationRequiredForSKUfor in isCustomizationRequiredForSKU"() {

		given:
		String siteId = "BedBathUS"

		skuRepositoryItemMock.getPropertyValue("customizationOfferedFlag") >> true
		skuRepositoryItemMock.getPropertyValue("personalizationType") >> null
		skuRepositoryItemMock.getPropertyValue("eligibleCustomizationCodes") >> "05"
		1*globalRepositoryToolsMock.isCustomizationOfferedForSKU(skuRepositoryItemMock, siteId) >> true
		when:

		boolean isCustomizationRequiredForSKU = testObjCatalogTools.isCustomizationRequiredForSKU(skuRepositoryItemMock, siteId)

		then:
		//checks isCustomizationRequiredForSKU is invoked with isCustomizationRequiredForSKU of BBBCatalogToolsImpl.
		isCustomizationRequiredForSKU == false
	}

	def "isCustomizationRequiredForSKU with personalizationType as PB. This TC tests for isCustomizationRequiredForSKUfor in isCustomizationRequiredForSKU."() {

		given:
		String siteId = "BedBathUS"

		skuRepositoryItemMock.getPropertyValue("customizationOfferedFlag") >> true
		skuRepositoryItemMock.getPropertyValue("personalizationType") >> "PB"
		skuRepositoryItemMock.getPropertyValue("eligibleCustomizationCodes") >> "05"
		1*globalRepositoryToolsMock.isCustomizationOfferedForSKU(skuRepositoryItemMock, siteId) >> true
		when:

		boolean isCustomizationRequiredForSKU = testObjCatalogTools.isCustomizationRequiredForSKU(skuRepositoryItemMock, siteId)

		then:
		//checks isCustomizationRequiredForSKU is invoked with isCustomizationRequiredForSKU of BBBCatalogToolsImpl.
		isCustomizationRequiredForSKU == true
	}
	def "isCustomizationRequiredForSKU with personalizationType as other value. This TC tests for isCustomizationRequiredForSKUfor in isCustomizationRequiredForSKU."() {

		given:
		String siteId = "BedBathUS"

		skuRepositoryItemMock.getPropertyValue("customizationOfferedFlag") >> true
		skuRepositoryItemMock.getPropertyValue("personalizationType") >> "Pq"
		skuRepositoryItemMock.getPropertyValue("eligibleCustomizationCodes") >> "05"
		1*globalRepositoryToolsMock.isCustomizationOfferedForSKU(skuRepositoryItemMock, siteId) >> true
		when:

		boolean isCustomizationRequiredForSKU = testObjCatalogTools.isCustomizationRequiredForSKU(skuRepositoryItemMock, siteId)

		then:
		//checks isCustomizationRequiredForSKU is invoked with isCustomizationRequiredForSKU of BBBCatalogToolsImpl.
		isCustomizationRequiredForSKU == false
	}

	///////////////////////////////////TCs for isCustomizationRequiredForSKU ends /////////////////////////////////////////////////

	///////////////////////////////////TCs for getNonShippableStatesForSku starts //////////////////////////////////////////////////
	//Signature : public final List<StateVO> getNonShippableStatesForSku(final String pSiteId, final String pSkuId)////////////////

	def "getNonShippableStatesForSku. This TC is the happy flow whch tests for stateVOList in getNonShippableStatesForSku."() {

		given:
		String siteId = "BedBathUs"
		String skuId = "855669"

		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		skuRepositoryItemMock.getPropertyValue("nonShippableStates") >> [].toSet()
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		List<StateVO> stateVOList = testObjCatalogTools.getNonShippableStatesForSku(siteId, skuId)

		then:
		//checks getNonShippableStatesForSku is invoked with stateVOList of BBBCatalogToolsImpl.
		stateVOList == null
	}

	def "getNonShippableStatesForSku with isSkuActive as false. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = "BedBathUs"
		String skuId = "855669"

		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> false
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		List<StateVO> stateVOList = testObjCatalogTools.getNonShippableStatesForSku(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getNonShippableStatesForSku with skuRepositoryItem as null. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = "BedBathUs"
		String skuId = "855669"

		when:

		testObjCatalogTools.getNonShippableStatesForSku(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getNonShippableStatesForSku with siteId as null. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = null
		String skuId = "855669"

		when:

		testObjCatalogTools.getNonShippableStatesForSku(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getNonShippableStatesForSku with empty siteId. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = ""
		String skuId = "855669"

		when:

		testObjCatalogTools.getNonShippableStatesForSku(siteId, skuId)

		then:
		//checks if BBBBusinessException is thrown.
		BBBBusinessException exception = thrown()
	}

	def "getNonShippableStatesForSku with exception scenarios. This TC tests if RepositoryException is thrown."() {

		given:
		String siteId = "BedBathUs"
		String skuId = "855669"

		testObjCatalogTools.isSkuActive(skuRepositoryItemMock) >> true
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> {throw new RepositoryException()}

		when:

		testObjCatalogTools.getNonShippableStatesForSku(siteId, skuId)

		then:
		//checks if BBBSystemException is thrown.
		BBBSystemException exception = thrown()
	}
	///////////////////////////////////TCs for getNonShippableStatesForSku ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for isFreeShipping starts //////////////////////////////////////////////////////////////////////////
	//Signature : public final boolean isFreeShipping(final String siteId, final String skuId, final String shippingMethodId) ////////////////

	def "isFreeShipping. This TC is the happy flow of isFreeShipping."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "4787878"
		String shippingMethodId = "3g"

		//Mock methods for SkuAttributeList
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
		repositoryItemMock.getRepositoryId() >> "10_1"
		repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		repositoryItemMock.getPropertyValue("startDate") >> new Date()
		repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
		repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
		skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
		skuRepositoryItemMock.getPropertyValue("customizationOfferedFlag") >> true
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getRebateKey()) >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "sameDayDelAttributeKeyList") >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getZoomKeys()) >> ["SDD"]

		//Mock methods for isFreeShipping
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		when:

		boolean isFreeShipping = testObjCatalogTools.isFreeShipping(siteId, skuId, shippingMethodId)

		then:
		isFreeShipping == true
	}

	def "isFreeShipping. This TC is when applicableAttrIds does not contain freeShipAttr."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "4787878"
		String shippingMethodId = "3g"

		def RepositoryItem skuAttributeRelationSetMock = Mock()
		def RepositoryItem skuAttributeRepositoryItemMock = Mock()
		ServletUtil.setCurrentRequest(null)

		//Mock methods for isFreeShipping
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock

		//Mock methods for SkuAttributeList
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
		repositoryItemMock.getRepositoryId() >> "10_1"
		skuAttributeRepositoryItemMock.getRepositoryId() >> "18_2"
		skuAttributeRelationSetMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		skuAttributeRelationSetMock.getPropertyValue("startDate") >> new Date()
		skuAttributeRelationSetMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
		skuAttributeRelationSetMock.getPropertyValue("skuAttribute") >> skuAttributeRepositoryItemMock
		skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [skuAttributeRelationSetMock].toSet()
		skuRepositoryItemMock.getPropertyValue("customizationOfferedFlag") >> true
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "previewEnabled") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getRebateKey()) >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "sameDayDelAttributeKeyList") >> ["SDD"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", testObjCatalogTools.getZoomKeys()) >> ["SDD"]

		when:

		boolean isFreeShipping = testObjCatalogTools.isFreeShipping(siteId, skuId, shippingMethodId)

		then:
		isFreeShipping == false
	}

	def "isFreeShipping. This TC is when Repository Exception occurs."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "4787878"
		String shippingMethodId = "3g"
		//Mock methods for isFreeShipping
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> {throw new RepositoryException("Mock Repository Exception")}

		when:
		boolean isFreeShipping = testObjCatalogTools.isFreeShipping(siteId, skuId, shippingMethodId)

		then:
		thrown BBBSystemException
	}

	def "isFreeShipping. This TC is when attributeMap is empty."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "4787878"
		String shippingMethodId = "3g"

		//Mock methods for isFreeShipping
		testObjCatalogTools.getCatalogRepository().getItem(skuId, "sku") >> skuRepositoryItemMock
		skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> null

		when:

		boolean isFreeShipping = testObjCatalogTools.isFreeShipping(siteId, skuId, shippingMethodId)

		then:
		isFreeShipping == false
	}

	def "isFreeShipping. This TC is when siteId is not defined"() {

		given:
		String siteId = ""
		String skuId = "4787878"
		String shippingMethodId = "3g"

		when:
		def results = testObjCatalogTools.isFreeShipping(siteId, skuId, shippingMethodId)

		then:
		thrown BBBBusinessException
	}

	def "isFreeShipping. This TC is when skuId is not defined"() {

		given:
		String siteId = "BedBathUS"
		String skuId = ""
		String shippingMethodId = "3g"

		when:
		def results = testObjCatalogTools.isFreeShipping(siteId, skuId, shippingMethodId)

		then:
		thrown BBBBusinessException
	}

	def "isFreeShipping. This TC is when shippingMethodId is not defined"() {

		given:
		String siteId = "BedBathUS"
		String skuId = "45454545"
		String shippingMethodId = ""

		when:
		def results = testObjCatalogTools.isFreeShipping(siteId, skuId, shippingMethodId)

		then:
		thrown BBBBusinessException
	}

	def "isFreeShipping with skuRepositoryItemMock as null. This TC tests if BBBBusinessException is thrown."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "4787878"
		String shippingMethodId = "3g"

		when:

		testObjCatalogTools.isFreeShipping(siteId, skuId, shippingMethodId)

		then:
		BBBBusinessException exception = thrown()
	}

	def "isFreeShipping with shippingMethodId not contained in shipGrpFreeShipAttrMap. This TC tests if logTrace is invoked."() {

		given:
		String siteId = "BedBathUS"
		String skuId = "4787878"
		String shippingMethodId = "5"

		when:

		testObjCatalogTools.isFreeShipping(siteId, skuId, shippingMethodId)

		then:
		1 * testObjCatalogTools.logTrace("No Free ship attr for the shipping id " + shippingMethodId)
	}
	///////////////////////////////////TCs for isFreeShipping ends ///////////////////////////////////////////////////////////////
	///////////////////////////////////TCs for getSkuAttributeList starts //////////////////////////////////////////////////////////////////////////
	//Signature : public final Map<String, RepositoryItem> getSkuAttributeList(final RepositoryItem skuRepositoryItem,final String siteId, final Map<String, RepositoryItem> attributeMap, final String regionPromoAttr, final boolean currentZipEligibility) //

	def "getSkuAttributeList. This TC is the happy flow for getSkuAttributeList"() {

		given:
		String siteId = "BedBathUS"
		String regionPromoAttr = "18_2"

		globalRepositoryToolsMock.setPreviewEnabled(false)
		globalRepositoryToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PREVIEW_ENABLED) >> ["true"]

		Map<String, RepositoryItem> attributeMap = new HashMap<String, RepositoryItem>()
		attributeMap.put("18_2", repositoryItemMock)

		def RepositoryItem skuAttributeRelationSetMock = Mock()
		def RepositoryItem skuAttributeRepositoryItemMock = Mock()

		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [skuAttributeRelationSetMock].toSet()
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		skuAttributeRelationSetMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		skuAttributeRelationSetMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME) >> new Date()
		skuAttributeRelationSetMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuAttributeRelationSetMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> skuAttributeRepositoryItemMock
		testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
		skuAttributeRepositoryItemMock.getRepositoryId() >> "18_2"

		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["true"]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY,BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> ["18_2"]
		skuAttributeRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_ATTRIBUTE_PROPERTY_NAME) >> new Date()
		skuAttributeRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_ATTRIBUTE_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)

		when:

		Map<String, RepositoryItem> results = testObjCatalogTools.getSkuAttributeList(skuRepositoryItemMock, siteId,attributeMap, regionPromoAttr, true)

		then:
		results.size() == 1
	}

	def "getSkuAttributeList. This TC when isAttributeApplicable returns false"() {

		given:
		String siteId = "BedBathUS"
		String regionPromoAttr = "18_2"
		Map<String, RepositoryItem> attributeMap = new HashMap<String, RepositoryItem>()
		attributeMap.put("18_2", repositoryItemMock)

		def RepositoryItem skuAttributeRelationSetMock1 = Mock()
		def RepositoryItem skuAttributeRelationSetMock2 = Mock()
		def RepositoryItem skuAttributeRepositoryItemMock1 = Mock()

		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [skuAttributeRelationSetMock1,skuAttributeRelationSetMock2].toSet()
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")

		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> null
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY,BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> ["18_2"]

		skuAttributeRelationSetMock1.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		skuAttributeRelationSetMock1.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME) >> new Date()
		skuAttributeRelationSetMock1.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuAttributeRelationSetMock1.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> skuAttributeRepositoryItemMock1

		skuAttributeRelationSetMock2.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		skuAttributeRelationSetMock2.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuAttributeRelationSetMock2.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuAttributeRelationSetMock2.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> skuAttributeRepositoryItemMock1

		skuAttributeRepositoryItemMock1.getRepositoryId() >> "18_2"
		skuAttributeRepositoryItemMock1.getPropertyValue(BBBCatalogConstants.START_DATE_ATTRIBUTE_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuAttributeRepositoryItemMock1.getPropertyValue(BBBCatalogConstants.END_DATE_ATTRIBUTE_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)



		when:

		Map<String, RepositoryItem> results = testObjCatalogTools.getSkuAttributeList(skuRepositoryItemMock, siteId,attributeMap, regionPromoAttr, true)

		then:
		results.size() == 1
	}

	def "getSkuAttributeList. This TC when currentZipEligibility is false"() {

		given:
		String siteId = "BedBathUS"
		String regionPromoAttr = "18_2"
		Map<String, RepositoryItem> attributeMap = new HashMap<String, RepositoryItem>()
		attributeMap.put("18_2", repositoryItemMock)

		def RepositoryItem skuAttributeRelationSetMock = Mock()
		def RepositoryItem skuAttributeRepositoryItemMock = Mock()

		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [skuAttributeRelationSetMock].toSet()
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		skuAttributeRelationSetMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		skuAttributeRelationSetMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME) >> new Date()
		skuAttributeRelationSetMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuAttributeRelationSetMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> skuAttributeRepositoryItemMock
		skuAttributeRepositoryItemMock.getRepositoryId() >> "18_2"

		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["true"]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY,BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> ["18_2"]
		skuAttributeRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_ATTRIBUTE_PROPERTY_NAME) >> new Date()
		skuAttributeRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_ATTRIBUTE_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)

		when:

		Map<String, RepositoryItem> results = testObjCatalogTools.getSkuAttributeList(skuRepositoryItemMock, siteId,attributeMap, regionPromoAttr, false)

		then:
		results.size() == 1
	}

	def "getSkuAttributeList. This TC when regionPromoAttr is not defined"() {

		given:
		String siteId = "BedBathUS"
		String regionPromoAttr = ""
		Map<String, RepositoryItem> attributeMap = new HashMap<String, RepositoryItem>()
		attributeMap.put("18_2", repositoryItemMock)

		def RepositoryItem skuAttributeRelationSetMock = Mock()
		def RepositoryItem skuAttributeRepositoryItemMock = Mock()

		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [skuAttributeRelationSetMock].toSet()
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		skuAttributeRelationSetMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		skuAttributeRelationSetMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME) >> new Date()
		skuAttributeRelationSetMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuAttributeRelationSetMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> skuAttributeRepositoryItemMock
		skuAttributeRepositoryItemMock.getRepositoryId() >> "18_2"

		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["true"]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY,BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> ["18_2"]
		skuAttributeRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_ATTRIBUTE_PROPERTY_NAME) >> new Date()
		skuAttributeRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_ATTRIBUTE_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)

		when:

		Map<String, RepositoryItem> results = testObjCatalogTools.getSkuAttributeList(skuRepositoryItemMock, siteId,attributeMap, regionPromoAttr, false)

		then:
		results.size() == 1
	}

	def "getSkuAttributeList. This TC when sddAttributeKeyList is empty in sameDayDeliverEligibility private method"() {

		given:
		String siteId = "BedBathUS"
		String regionPromoAttr = "18_2"
		Map<String, RepositoryItem> attributeMap = new HashMap<String, RepositoryItem>()
		attributeMap.put("18_2", repositoryItemMock)

		def RepositoryItem skuAttributeRelationSetMock1 = Mock()
		def RepositoryItem skuAttributeRepositoryItemMock = Mock()

		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [skuAttributeRelationSetMock1].toSet()
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		skuAttributeRelationSetMock1.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		skuAttributeRelationSetMock1.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME) >> new Date()
		skuAttributeRelationSetMock1.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuAttributeRelationSetMock1.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> skuAttributeRepositoryItemMock
		skuAttributeRepositoryItemMock.getRepositoryId() >> "18_2"

		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["true"]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY,BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> []
		skuAttributeRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_ATTRIBUTE_PROPERTY_NAME) >> new Date()
		skuAttributeRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_ATTRIBUTE_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)

		when:

		Map<String, RepositoryItem> results = testObjCatalogTools.getSkuAttributeList(skuRepositoryItemMock, siteId,attributeMap, regionPromoAttr, false)

		then:
		results.size() == 1
	}

	def "getSkuAttributeList. This TC when checkAttributeIntlApplicability returns true"() {

		given:
		String siteId = "BedBathUS"
		String regionPromoAttr = "18_2"
		Map<String, RepositoryItem> attributeMap = new HashMap<String, RepositoryItem>()
		attributeMap.put("18_2", repositoryItemMock)

		testObjCatalogTools.resolveComponentFromRequest(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
		sessionBeanMock.isInternationalShippingContext() >> true

		def RepositoryItem skuAttributeRelationSetMock = Mock()
		def RepositoryItem skuAttributeRepositoryItemMock = Mock()

		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [skuAttributeRelationSetMock].toSet()
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		skuAttributeRelationSetMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		skuAttributeRelationSetMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME) >> new Date()
		skuAttributeRelationSetMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuAttributeRelationSetMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> skuAttributeRepositoryItemMock
		skuAttributeRepositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG) >> "N"

		when:

		Map<String, RepositoryItem> results = testObjCatalogTools.getSkuAttributeList(skuRepositoryItemMock, siteId,attributeMap, regionPromoAttr, false)

		then:
		results.size() == 1
	}

	def "getSkuAttributeList. This TC when BBBBusiness Exception occurred in sameDayDeliverEligibility private method"() {

		given:
		String siteId = "BedBathUS"
		String regionPromoAttr = "18_2"
		Map<String, RepositoryItem> attributeMap = new HashMap<String, RepositoryItem>()
		attributeMap.put("18_2", repositoryItemMock)

		def RepositoryItem skuAttributeRelationSetMock1 = Mock()
		def RepositoryItem skuAttributeRepositoryItemMock = Mock()

		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [skuAttributeRelationSetMock1].toSet()
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		skuAttributeRelationSetMock1.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		skuAttributeRelationSetMock1.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME) >> new Date()
		skuAttributeRelationSetMock1.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuAttributeRelationSetMock1.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> skuAttributeRepositoryItemMock
		skuAttributeRepositoryItemMock.getRepositoryId() >> "18_2"

		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["true"]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY,BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> {throw new BBBBusinessException("Mock BBBBusinessException")}
		skuAttributeRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_ATTRIBUTE_PROPERTY_NAME) >> new Date()
		skuAttributeRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_ATTRIBUTE_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)

		when:

		Map<String, RepositoryItem> results = testObjCatalogTools.getSkuAttributeList(skuRepositoryItemMock, siteId,attributeMap, regionPromoAttr, false)

		then:
		results.size() == 1
	}

	def "getSkuAttributeList. This TC when BBBSystem Exception occurred in sameDayDeliverEligibility private method"() {

		given:
		String siteId = "BedBathUS"
		String regionPromoAttr = "18_2"
		Map<String, RepositoryItem> attributeMap = new HashMap<String, RepositoryItem>()
		attributeMap.put("18_2", repositoryItemMock)

		def RepositoryItem skuAttributeRelationSetMock1 = Mock()
		def RepositoryItem skuAttributeRepositoryItemMock = Mock()

		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [skuAttributeRelationSetMock1].toSet()
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
		skuAttributeRelationSetMock1.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
		skuAttributeRelationSetMock1.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME) >> new Date()
		skuAttributeRelationSetMock1.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuAttributeRelationSetMock1.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> skuAttributeRepositoryItemMock
		skuAttributeRepositoryItemMock.getRepositoryId() >> "18_2"

		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["true"]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY,BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> {throw new BBBSystemException("Mock BBBSystemException")}
		skuAttributeRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_ATTRIBUTE_PROPERTY_NAME) >> new Date()
		skuAttributeRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_ATTRIBUTE_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)

		when:

		Map<String, RepositoryItem> results = testObjCatalogTools.getSkuAttributeList(skuRepositoryItemMock, siteId,attributeMap, regionPromoAttr, false)

		then:
		results.size() == 1
	}

	def "getSkuAttributeList. This TC when BBBSystem Exception occurred in getSkuAttributeList"() {

		given:
		String siteId = "BedBathUS"
		String regionPromoAttr = "18_2"
		Map<String, RepositoryItem> attributeMap = new HashMap<String, RepositoryItem>()
		attributeMap.put("18_2", repositoryItemMock)

		def RepositoryItem skuAttributeRelationSetMock = Mock()
		def RepositoryItem skuAttributeRepositoryItemMock = Mock()

		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [skuAttributeRelationSetMock].toSet()
		testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
		bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")

		when:

		Map<String, RepositoryItem> results = testObjCatalogTools.getSkuAttributeList(skuRepositoryItemMock, siteId,attributeMap, regionPromoAttr, false)

		then:
		thrown BBBSystemException
	}

	///////////////////////////////////TCs for getSkuAttributeList ends ///////////////////////////////////////////////////////////////


	///////////////////////////////////TCs for checkAttributeIntlApplicability starts ///////////////////////////////////////////////////////////////
	//Signature : public boolean checkAttributeIntlApplicability( RepositoryItem attributeInfo, boolean internationalShippingContext ) //

	def "checkAttributeIntlApplicability. This TC is the happy flow for checkAttributeIntlApplicability"() {

		given:

		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG) >> "X"

		when:

		def skipIntlAttribute = testObjCatalogTools.checkAttributeIntlApplicability(repositoryItemMock, false)

		then:
		skipIntlAttribute == true
	}

	def "checkAttributeIntlApplicability. This TC is when repository item is not defined"() {

		given:


		when:

		def skipIntlAttribute = testObjCatalogTools.checkAttributeIntlApplicability(null, false)

		then:
		skipIntlAttribute == false
	}

	def "checkAttributeIntlApplicability. This TC is when repsoitory returns empty value"() {

		given:
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG) >> ""

		when:

		def skipIntlAttribute = testObjCatalogTools.checkAttributeIntlApplicability(repositoryItemMock, true)

		then:
		skipIntlAttribute == false
	}

	def "checkAttributeIntlApplicability. This TC is when intlFlag returns 'N' for internationalShippingContext is false"() {

		given:
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG) >> "N"

		when:

		def skipIntlAttribute = testObjCatalogTools.checkAttributeIntlApplicability(repositoryItemMock, false)

		then:
		skipIntlAttribute == false
	}


	def "checkAttributeIntlApplicability. This TC is when intlFlag returns 'X' for internationalShippingContext is true"() {

		given:
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG) >> "X"

		when:

		def skipIntlAttribute = testObjCatalogTools.checkAttributeIntlApplicability(repositoryItemMock, true)

		then:
		skipIntlAttribute == false
	}

	///////////////////////////////////TCs for checkAttributeIntlApplicability ends ///////////////////////////////////////////////////////////////


	///////////////////////////////////TCs for resolveComponentFromRequest starts ///////////////////////////////////////////////////////////////
	//Signature : public Object resolveComponentFromRequest(String componentPath) //

	def "resolveComponentFromRequest. This TC is when componentPath is empty"() {

		given:

		when:

		def component = testObjCatalogTools.resolveComponentFromRequest("")

		then:
		component == null
	}

	///////////////////////////////////TCs for resolveComponentFromRequest ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getProductMedia starts ///////////////////////////////////////////////////////////////
	//Signature : public final List<MediaVO> getProductMedia(final String productId, String siteId) //

	def "getProductMedia. This TC is happy flow of getProductMedia with properties are defined in populateMediaVO private method"() {

		given:
		String productId = "23232323"
		String siteId = "TBS_BedBathUS"

		def RepositoryItem prdtMediaItemListMock = Mock()
		def RepositoryItem mediaSitesMock = Mock()

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIA_RELN_PRODUCT_PROPERTY_NAME) >> [prdtMediaItemListMock]

		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.START_DATE_OTHER_MEDIA_PROPERTY_NAME) >> null
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.END_DATE_OTHER_MEDIA_PROPERTY_NAME) >> null

		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.SITES_OTHER_MEDIA_PROPERTY_NAME) >> [mediaSitesMock].toSet()
		mediaSitesMock.getRepositoryId() >> "BedBathUS"
		prdtMediaItemListMock.getRepositoryId() >> "23231"

		// for populateMediaVO Private Method
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.MEDIA_TRANSCRIPT_OTHER_MEDIA_PROPERTY_NAME) >> "Media_transcript"
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.PROVIDER_OTHER_MEDIA_PROPERTY_NAME) >> "provider_id"
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.MEDIA_DESCRIPTION_OTHER_MEDIA_PROPERTY_NAME) >> "media_description"
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.MEDIA_SOURCE_OTHER_MEDIA_PROPERTY_NAME) >> "media_source"
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.COMMENTS_OTHER_MEDIA_PROPERTY_NAME) >> "comments"
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.MEDIA_TYPE_OTHER_MEDIA_PROPERTY_NAME) >> "media_type"

		when:
		List<MediaVO> productMedia = testObjCatalogTools.getProductMedia(productId, siteId)

		then:
		productMedia[0].mediaTranscript == "Media_transcript"
		productMedia[0].providerId == "provider_id"
		productMedia[0].mediaDescription == "media_description"
		productMedia[0].mediaSource == "media_source"
		productMedia[0].comments == "comments"
		productMedia[0].mediaType == "media_type"
	}

	def "getProductMedia. This TC is when properties are not defined in populateMediaVO private method"() {

		given:
		String productId = "23232323"
		String siteId = "BedBathUS"

		def RepositoryItem prdtMediaItemListMock = Mock()
		def RepositoryItem mediaSitesMock = Mock()

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIA_RELN_PRODUCT_PROPERTY_NAME) >> [prdtMediaItemListMock]

		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.START_DATE_OTHER_MEDIA_PROPERTY_NAME) >> null
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.END_DATE_OTHER_MEDIA_PROPERTY_NAME) >> null

		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.SITES_OTHER_MEDIA_PROPERTY_NAME) >> [mediaSitesMock].toSet()
		mediaSitesMock.getRepositoryId() >> "BedBathUS"
		prdtMediaItemListMock.getRepositoryId() >> "23231"

		// for populateMediaVO Private Method
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.MEDIA_TRANSCRIPT_OTHER_MEDIA_PROPERTY_NAME) >> null
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.PROVIDER_OTHER_MEDIA_PROPERTY_NAME) >> null
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.MEDIA_DESCRIPTION_OTHER_MEDIA_PROPERTY_NAME) >> null
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.MEDIA_SOURCE_OTHER_MEDIA_PROPERTY_NAME) >> null
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.COMMENTS_OTHER_MEDIA_PROPERTY_NAME) >> null
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.MEDIA_TYPE_OTHER_MEDIA_PROPERTY_NAME) >> null

		when:

		List<MediaVO> productMedia = testObjCatalogTools.getProductMedia(productId, siteId)

		then:
		productMedia[0].mediaTranscript == null
		productMedia[0].providerId == null
		productMedia[0].mediaDescription == null
		productMedia[0].mediaSource == null
		productMedia[0].comments == null
		productMedia[0].mediaType == null

	}

	def "getProductMedia. This TC is when mediaApplicableOnCurrSite is false"() {

		given:
		String productId = "23232323"
		String siteId = "TBS_BedBathUS"

		def RepositoryItem prdtMediaItemListMock = Mock()
		def RepositoryItem mediaSitesMock = Mock()

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIA_RELN_PRODUCT_PROPERTY_NAME) >> [prdtMediaItemListMock]

		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.START_DATE_OTHER_MEDIA_PROPERTY_NAME) >> null
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.END_DATE_OTHER_MEDIA_PROPERTY_NAME) >> null

		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.SITES_OTHER_MEDIA_PROPERTY_NAME) >> [mediaSitesMock].toSet()
		mediaSitesMock.getRepositoryId() >> "BedBathCanada"
		prdtMediaItemListMock.getRepositoryId() >> "23231"

		when:
		List<MediaVO> productMedia = testObjCatalogTools.getProductMedia(productId, siteId)

		then:
		productMedia[0] == null

	}

	def "getProductMedia. This TC is when isAttributeApplicable returns false"() {

		given:
		String productId = "23232323"
		String siteId = "TBS_BedBathUS"

		def RepositoryItem prdtMediaItemListMock = Mock()
		def RepositoryItem mediaSitesMock = Mock()

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIA_RELN_PRODUCT_PROPERTY_NAME) >> [prdtMediaItemListMock]

		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.START_DATE_OTHER_MEDIA_PROPERTY_NAME) >> new Date()
		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.END_DATE_OTHER_MEDIA_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)

		prdtMediaItemListMock.getPropertyValue(BBBCatalogConstants.SITES_OTHER_MEDIA_PROPERTY_NAME) >> [mediaSitesMock].toSet()
		mediaSitesMock.getRepositoryId() >> "BedBathUS"
		prdtMediaItemListMock.getRepositoryId() >> "23231"

		when:
		List<MediaVO> productMedia = testObjCatalogTools.getProductMedia(productId, siteId)

		then:
		productMedia == []
	}

	def "getProductMedia. This TC is when prdtMediaItemList returns null"() {

		given:
		String productId = "23232323"
		String siteId = "TBS_BedBathUS"

		def RepositoryItem prdtMediaItemListMock = Mock()
		def RepositoryItem mediaSitesMock = Mock()

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIA_RELN_PRODUCT_PROPERTY_NAME) >> null

		when:
		List<MediaVO> productMedia = testObjCatalogTools.getProductMedia(productId, siteId)

		then:
		productMedia == []
	}

	def "getProductMedia. This TC is when prdtMediaItemList returns empty"() {

		given:
		String productId = "23232323"
		String siteId = "TBS_BedBathUS"

		def RepositoryItem prdtMediaItemListMock = Mock()
		def RepositoryItem mediaSitesMock = Mock()

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIA_RELN_PRODUCT_PROPERTY_NAME) >> []

		when:
		List<MediaVO> productMedia = testObjCatalogTools.getProductMedia(productId, siteId)

		then:
		productMedia == []
	}

	def "getProductMedia. This TC is when productRepositoryItem returns null"() {

		given:
		String productId = "23232323"
		String siteId = "TBS_BedBathUS"
		def RepositoryItem prdtMediaItemListMock = Mock()
		def RepositoryItem mediaSitesMock = Mock()
		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null

		when:
		List<MediaVO> productMedia = testObjCatalogTools.getProductMedia(productId, siteId)

		then:
		productMedia == []
	}

	def "getProductMedia. This TC is when productRepositoryItem throws RepositoryException"() {

		given:
			String productId = "23232323"
			String siteId = "TBS_BedBathUS"
			def RepositoryItem prdtMediaItemListMock = Mock()
			def RepositoryItem mediaSitesMock = Mock()
			catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock RepositoryException")}
			
		when:
		testObjCatalogTools.getProductMedia(productId, siteId)

		then:
		thrown BBBSystemException
		1 * testObjCatalogTools.logError("Catalog API Method Name [getProductMedia]: RepositoryException ")
	}

	/*def "getProductMedia. This TC is when ParseException throws"() {
	 given:
	 String productId = "23232323"
	 String siteId = "TBS_BedBathUS"
	 def RepositoryItem prdtMediaItemListMock = Mock()
	 def RepositoryItem mediaSitesMock = Mock()
	 catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
	 repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIA_RELN_PRODUCT_PROPERTY_NAME) >> [prdtMediaItemListMock]
	 def SimpleDateFormat dateFormatMock = new SimpleDateFormat("MM/dd/yyyy")
	 dateFormatMock.parse(dateFormatMock.format(new Date())) >> {throw new ParseException("Mock ParseException")}
	 when:
	 List<MediaVO> productMedia = testObjCatalogTools.getProductMedia(productId, siteId)
	 then:
	 productMedia == []
	 }*/


	///////////////////////////////////TCs for getProductMedia ends ///////////////////////////////////////////////////////////////


	///////////////////////////////////TCs for isProductActive(String productId) starts ///////////////////////////////////////////////////////////////
	//Signature : public boolean isProductActive (String productId) //////////////

	def "isProductActive of string. This TC is the happy flow of isProductActive with one parameter"() {

		given:
		String productId = "232323"
		catalogRepositoryMock.getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productId)

		then:
		booleanResult == true
	}

	def "isProductActive of string. This TC is when productRepositoryItem returns null"() {

		given:
		String productId = "232323"
		catalogRepositoryMock.getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productId)

		then:
		booleanResult == null
		thrown BBBBusinessException
	}

	def "isProductActive of string. This TC is when productRepositoryItem throws repositoryException"() {

		given:
		String productId = "232323"
		catalogRepositoryMock.getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for Repository Exception")}
		when:
		def booleanResult = testObjCatalogTools.isProductActive(productId)

		then:
		booleanResult == null
		thrown BBBSystemException
	}

	///////////////////////////////////TCs for isProductActive(String productId) ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for isProductActive(final RepositoryItem productRepositoryItem) starts ///////////////////////////////////////////////////////////////
	//Signature : public final boolean isProductActive(final RepositoryItem productRepositoryItem) //////////////

	def "isProductActive of RepositoryItem. This TC is the happy flow for isProductActive"() {

		given:
		ServletUtil.setCurrentRequest(null)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) >> new Date()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock)

		then:
		booleanResult == false
	}

	def "isProductActive of RepositoryItem. This TC is when prodDisable is not defined"() {

		given:
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "mobiledev"
		globalRepositoryToolsMock.setPreviewEnabled(false)
		globalRepositoryToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PREVIEW_ENABLED) >> ["true"]
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) >> new Date()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> false
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> null

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock)

		then:
		booleanResult == false
	}

	def "isProductActive of RepositoryItem. This TC is X-bbb-channel returns empty"() {

		given:
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> ""
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) >> new Date()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) >> new Date()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock)

		then:
		booleanResult == false
	}

	def "isProductActive of RepositoryItem. This TC is when weboffered returns true"() {

		given:
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "mobiledev"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock)

		then:
		booleanResult == true
	}

	def "isProductActive of RepositoryItem. This TC is when startdate is not defined"() {

		given:
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "mobiledev"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) >> new Date()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock)

		then:
		booleanResult == false
	}

	def "isProductActive of RepositoryItem. This TC is when preview date is after end date"() {

		given:
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "mobiledev"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) >> new Date()
		def endDate = new GregorianCalendar(2014, Calendar.AUGUST, 3, 23, 59, 45).time
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) >> endDate
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock)

		then:
		booleanResult == false
	}

	def "isProductActive of RepositoryItem. This TC is when preview date is before start date"() {

		given:
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "mobiledev"
		def startDate = new GregorianCalendar(2014, Calendar.AUGUST, 3, 23, 59, 45).time
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) >> new Date()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock)

		then:
		booleanResult == false
	}

	///////////////////////////////////TCs for isProductActive(final RepositoryItem productRepositoryItem) ends ///////////////////////////////////////////////////////////////


	///////////////////////////////////TCs for isProductActive(String productId,String siteId) starts ///////////////////////////////////////////////////////////////
	//Signature : public boolean isProductActive (String productId,String siteId) //////////////

	def "isProductActive of String,String. This TC is the happy flow for isProductActive"() {

		given:
		String productId = "353223"
		String siteId = "BedBathUS"
		catalogRepositoryMock.getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue("siteIds") >> [""].toSet()

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productId, siteId)

		then:
		booleanResult == false
	}

	def "isProductActive of String,String. This TC is when repositoryItem returns null"() {

		given:
		String productId = "353223"
		String siteId = "BedBathUS"
		catalogRepositoryMock.getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productId, siteId)

		then:
		booleanResult == null
		thrown BBBBusinessException
	}

	def "isProductActive of String,String. This TC is when repositoryItem throws exception"() {

		given:
		String productId = "353223"
		String siteId = "BedBathUS"
		catalogRepositoryMock.getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for Repository Exception")}

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productId, siteId)

		then:
		booleanResult == null
		thrown BBBSystemException
	}

	///////////////////////////////////TCs for isProductActive (String productId,String siteId) ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for isProductActive(final RepositoryItem productRepositoryItem, final String siteId) Starts ///////////////////////////////////////////////////////////////
	//Signature : public final boolean isProductActive(final RepositoryItem productRepositoryItem, final String siteId) //////////////

	def "isProductActive of RepositoryItem,String. This TC is the happy flow of isProductActive"() {

		given:
		String siteId = "BedBathUS"
		globalRepositoryToolsMock.setPreviewEnabled(false)
		globalRepositoryToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PREVIEW_ENABLED) >> ["true"]
		productRepositoryItemMock.getPropertyValue("siteIds") >> ["BedBathUS"].toSet()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) >> new Date()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> false
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> true

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isProductActive of RepositoryItem,String. This TC is when properties are not defined"() {

		given:
		String siteId = "BedBathUS"

		productRepositoryItemMock.getPropertyValue("siteIds") >> ["BedBathUS"].toSet()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> null

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isProductActive of RepositoryItem,String. This TC is when preview date is before start date"() {

		given:
		String siteId = "BedBathUS"

		productRepositoryItemMock.getPropertyValue("siteIds") >> ["BedBathUS"].toSet()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) >> null

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isProductActive of RepositoryItem,String. This TC is when preview date is after enddate"() {

		given:
		String siteId = "BedBathUS"

		productRepositoryItemMock.getPropertyValue("siteIds") >> ["BedBathUS"].toSet()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) >> new Date()
		def endDate = new GregorianCalendar(2014, Calendar.AUGUST, 3, 23, 59, 45).time
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) >> endDate

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isProductActive of RepositoryItem,String. This TC is when disable is false"() {

		given:
		String siteId = "BedBathUS"

		productRepositoryItemMock.getPropertyValue("siteIds") >> ["BedBathUS"].toSet()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock, siteId)

		then:
		booleanResult == true
	}

	def "isProductActive of RepositoryItem,String. This TC is when webOffered is false"() {

		given:
		String siteId = "BedBathUS"

		productRepositoryItemMock.getPropertyValue("siteIds") >> ["BedBathUS"].toSet()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) >> new Date()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) >> new Date()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> false
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isProductActive of RepositoryItem,String. This TC is when siteId and assocSites is different"() {

		given:
		String siteId = "BedBathCanada"

		productRepositoryItemMock.getPropertyValue("siteIds") >> ["BedBathUS"].toSet()

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isProductActive of RepositoryItem,String. This TC is when assocSites is not defined"() {

		given:
		String siteId = "BedBathCanada"

		productRepositoryItemMock.getPropertyValue("siteIds") >> null

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isProductActive of RepositoryItem,String. This TC is when assocSites is empty"() {

		given:
		String siteId = "BedBathCanada"

		productRepositoryItemMock.getPropertyValue("siteIds") >> [].toSet()

		when:
		def booleanResult = testObjCatalogTools.isProductActive(productRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	///////////////////////////////////TCs for isProductActive(final RepositoryItem productRepositoryItem, final String siteId) ends ///////////////////////////////////////////////////////////////


	///////////////////////////////////TCs for isEverlivingProduct Starts ///////////////////////////////////////////////////////////////
	//Signature : public final boolean isEverlivingProduct(final String pProductId, String pSiteId) //////////////

	def "isEverlivingProduct. This TC is the happy flow of isEverlivingProduct when siteId is TBS_BedBathUS"() {

		given:
		String siteId = "TBS_BedBathUS"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		def enableDate = new GregorianCalendar(2014, Calendar.AUGUST, 3, 23, 59, 45).time
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> enableDate
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_FOREVER_PDP_FLAG) >> null

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == true
	}

	def "isEverlivingProduct. This TC is disableForeverPDPFlag is true when siteId is TBS_BedBathUS"() {

		given:
		String siteId = "TBS_BedBathUS"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		def enableDate = new GregorianCalendar(2014, Calendar.AUGUST, 3, 23, 59, 45).time
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> enableDate
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_FOREVER_PDP_FLAG) >> true

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == false
	}

	def "isEverlivingProduct. This TC is disable is false when siteId is TBS_BedBathUS"() {

		given:
		String siteId = "TBS_BedBathUS"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> false
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_FOREVER_PDP_FLAG) >> true

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == false
	}

	def "isEverlivingProduct. This TC is validDate is false when siteId is TBS_BedBathUS"() {

		given:
		String siteId = "TBS_BedBathUS"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_FOREVER_PDP_FLAG) >> false

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == false
	}

	def "isEverlivingProduct. This TC is when repositoryItem returns null"() {

		given:
		String siteId = "TBS_BedBathUS"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == null
		thrown BBBBusinessException
	}

	def "isEverlivingProduct. This TC is when repositoryItem throws exception"() {

		given:
		String siteId = "TBS_BedBathUS"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for Repository Exception")}

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == null
		thrown BBBSystemException
	}

	def "isEverlivingProduct. This TC is when siteId is empty"() {

		given:
		String siteId = ""
		String productId = "232323"

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == false
	}

	def "isEverlivingProduct. This TC is properties are not defined when siteId is TBS_BedBathUS"() {

		given:
		String siteId = "TBS_BedBathUS"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_FOREVER_PDP_FLAG) >> null

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == false
	}

	def "isEverlivingProduct. This TC is the happy flow of isEverlivingProduct when siteId is TBS_BuyBuyBaby"() {

		given:
		String siteId = "TBS_BuyBuyBaby"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		def enableDate = new GregorianCalendar(2014, Calendar.AUGUST, 3, 23, 59, 45).time
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> enableDate
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.BAB_DISABLE_FOREVER_PDP_FLAG) >> null

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == true
	}

	def "isEverlivingProduct. This TC is disableForeverPDPFlag is true when siteId is TBS_BuyBuyBaby"() {

		given:
		String siteId = "TBS_BuyBuyBaby"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		def enableDate = new GregorianCalendar(2014, Calendar.AUGUST, 3, 23, 59, 45).time
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.BAB_DISABLE_FOREVER_PDP_FLAG) >> true

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == false
	}

	def "isEverlivingProduct. This TC is disable is false when siteId is TBS_BuyBuyBaby"() {

		given:
		String siteId = "TBS_BuyBuyBaby"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> false
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.BAB_DISABLE_FOREVER_PDP_FLAG) >> true

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == false
	}

	def "isEverlivingProduct. This TC is validDate is false when siteId is TBS_BuyBuyBaby"() {

		given:
		String siteId = "TBS_BuyBuyBaby"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.BAB_DISABLE_FOREVER_PDP_FLAG) >> false

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == false
	}

	def "isEverlivingProduct. This TC is properties are not defined when siteId is TBS_BuyBuyBaby"() {

		given:
		String siteId = "TBS_BuyBuyBaby"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.BAB_DISABLE_FOREVER_PDP_FLAG) >> null

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == false
	}

	def "isEverlivingProduct. This TC is the happy flow of isEverlivingProduct when siteId is TBS_BedBathCanada"() {

		given:
		String siteId = "TBS_BedBathCanada"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		def enableDate = new GregorianCalendar(2014, Calendar.AUGUST, 3, 23, 59, 45).time
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> enableDate
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_DISABLE_FOREVER_PDP_FLAG) >> null

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == true
	}

	def "isEverlivingProduct. This TC is disableForeverPDPFlag is true when siteId is TBS_BedBathCanada"() {

		given:
		String siteId = "TBS_BedBathCanada"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_DISABLE_FOREVER_PDP_FLAG) >> true

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == false
	}

	def "isEverlivingProduct. This TC is disable is false when siteId is TBS_BedBathCanada"() {

		given:
		String siteId = "TBS_BedBathCanada"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> false
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_DISABLE_FOREVER_PDP_FLAG) >> true

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == false
	}

	def "isEverlivingProduct. This TC is validDate is false when siteId is TBS_BedBathCanada"() {

		given:
		String siteId = "TBS_BedBathCanada"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_DISABLE_FOREVER_PDP_FLAG) >> false

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == false
	}

	def "isEverlivingProduct. This TC is properties are not defined when siteId is TBS_BedBathCanada"() {

		given:
		String siteId = "TBS_BedBathCanada"
		String productId = "232323"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ENABLE_DATE_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_DISABLE_FOREVER_PDP_FLAG) >> null

		when:
		def booleanResult = testObjCatalogTools.isEverlivingProduct(productId, siteId)

		then:
		booleanResult == false
	}


	///////////////////////////////////TCs for isEverlivingProduct ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getFirstActiveParentProductForSKU Starts ///////////////////////////////////////////////////////////////
	//Signature : public final String getFirstActiveParentProductForSKU(final String pSkuId) //////////////


	def "getFirstActiveParentProductForSKU. This TC is the happy flow for getFirstActiveParentProductForSKU"() {

		given:
		String skuId = "533232"
		catalogRepositoryMock.getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> [repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >> "23223"

		when:
		def results = testObjCatalogTools.getFirstActiveParentProductForSKU(skuId)

		then:
		results == "23223"
	}

	def "getFirstActiveParentProductForSKU. This TC is when skuId is not defined"() {

		given:
		String skuId = null

		when:
		def results = testObjCatalogTools.getFirstActiveParentProductForSKU(skuId)

		then:
		results == null
		thrown BBBBusinessException
	}

	def "getFirstActiveParentProductForSKU. This TC is when skuId is empty"() {

		given:
		String skuId = ""

		when:
		def results = testObjCatalogTools.getFirstActiveParentProductForSKU(skuId)

		then:
		results == null
		thrown BBBBusinessException
	}

	def "getFirstActiveParentProductForSKU. This TC is when repository throws exception"() {

		given:
		String skuId = "5323323"
		catalogRepositoryMock.getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for RepositoryException")}

		when:
		def results = testObjCatalogTools.getFirstActiveParentProductForSKU(skuId)

		then:
		results == null
		thrown BBBSystemException
	}

	def "getFirstActiveParentProductForSKU. This TC is when repository returns no items"() {

		given:
		String skuId = "5323323"
		catalogRepositoryMock.getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null

		when:
		def results = testObjCatalogTools.getFirstActiveParentProductForSKU(skuId)

		then:
		results == null
		thrown BBBBusinessException
	}

	def "getFirstActiveParentProductForSKU. This TC is when parentProducts property returns no items"() {

		given:
		String skuId = "5323323"
		catalogRepositoryMock.getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> null

		when:
		def results = testObjCatalogTools.getFirstActiveParentProductForSKU(skuId)

		then:
		results == null
		thrown BBBBusinessException
	}

	def "getFirstActiveParentProductForSKU. This TC is when parentProducts property returns empty"() {

		given:
		String skuId = "5323323"
		catalogRepositoryMock.getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> [].toSet()

		when:
		def results = testObjCatalogTools.getFirstActiveParentProductForSKU(skuId)

		then:
		results == null
		thrown BBBBusinessException
	}

	def "getFirstActiveParentProductForSKU. This TC is when parentProducts property returns null repositoryId"() {

		given:
		String skuId = "5323323"
		catalogRepositoryMock.getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> [repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >> null

		when:
		def results = testObjCatalogTools.getFirstActiveParentProductForSKU(skuId)

		then:
		results == null
		thrown BBBBusinessException
	}


	///////////////////////////////////TCs for getFirstActiveParentProductForSKU ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for hasSDDAttribute Starts ///////////////////////////////////////////////////////////////
	//Signature : public final boolean hasSDDAttribute(final String siteId, final String skuId, final String regionPromoAttr, final boolean isZipSDDEligibile) //////////////

	def "hasSDDAttribute. This TC is the happy flow of hasSDDAttribute"() {

		given:
			String siteId = "BedBathUS"
			String skuId = "222522"
			String regionPromoAttr = "12_2"
			
			catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepositoryItemMock
			skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
			bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
			testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
			repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
			repositoryItemMock.getPropertyValue("startDate") >> new Date()
			repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
			repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
			testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
			repositoryItemMock.getRepositoryId() >> "SDD"
			testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> null
			testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY, BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> ["12_2"]
			RepositoryItemDescriptor repItemDescriptorMock = Mock()
			skuRepositoryItemMock.getItemDescriptor() >> repItemDescriptorMock
			repItemDescriptorMock.getItemDescriptorName() >> BBBCatalogConstants.SKU_ITEM_DESCRIPTOR
			Map skuattr = new HashMap()
			skuattr.put("12_2",skuRepositoryItemMock)
			testObjCatalogTools.getSkuAttributeList(*_) >> skuattr
		when:
			def booleanResult = testObjCatalogTools.hasSDDAttribute(siteId, skuRepositoryItemMock, regionPromoAttr, false)
		 
		then:
			booleanResult == true

	}
	
	def "hasSDDAttribute. This TC is the happy flow of hasSDDAttribute when we pass product Item"() {
		
		given:
			String siteId = "BedBathUS"
			String skuId = "222522"
			String regionPromoAttr = "12_2"
			
			catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
			productRepositoryItemMock.getPropertyValue("productAttributeRelns") >> [repositoryItemMock].toSet()
			bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
			testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
			repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
			repositoryItemMock.getPropertyValue("startDate") >> new Date()
			repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
			repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
			testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
			repositoryItemMock.getRepositoryId() >> "12_2"
			testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> ["true"]
			testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY, BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> ["12_2"]
			RepositoryItemDescriptor repItemDescriptorMock = Mock()
			productRepositoryItemMock.getItemDescriptor() >> repItemDescriptorMock
			repItemDescriptorMock.getItemDescriptorName() >> BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR
			Map skuattr = new HashMap()
			testObjCatalogTools.getProductAttributeList(productRepositoryItemMock, siteId, skuattr) >> skuattr
		when:
			def booleanResult = testObjCatalogTools.hasSDDAttribute(siteId, productRepositoryItemMock, regionPromoAttr, false)
		 
		then:
			booleanResult == false

	}

	def "hasSDDAttribute. This TC is when attributeMap and itemSddKey are different"() {

		given:
			String siteId = "BedBathUS"
			String skuId = "222522"
			String regionPromoAttr = "12_2"
			
			catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepositoryItemMock
			skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
			bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
			testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
			repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
			repositoryItemMock.getPropertyValue("startDate") >> new Date()
			repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
			repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
			testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
			repositoryItemMock.getRepositoryId() >> "10_2"
			testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> null
			testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY, BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> ["SDD"]
			RepositoryItemDescriptor repItemDescriptorMock = Mock()
			skuRepositoryItemMock.getItemDescriptor() >> repItemDescriptorMock
			repItemDescriptorMock.getItemDescriptorName() >> BBBCatalogConstants.SKU_ITEM_DESCRIPTOR
		when:
			def booleanResult = testObjCatalogTools.hasSDDAttribute(siteId, skuRepositoryItemMock, regionPromoAttr, false)
		 
		then:
		booleanResult == false

	}

	def "hasSDDAttribute. This TC is when sddAttributeKeyList is empty"() {

		given:
			String siteId = "BedBathUS"
			String skuId = "222522"
			String regionPromoAttr = "12_2"
			
			catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepositoryItemMock
			skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
			bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
			testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
			repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
			repositoryItemMock.getPropertyValue("startDate") >> new Date()
			repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
			repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
			testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
			repositoryItemMock.getRepositoryId() >> "10_2"
			testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> null
			testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY, BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> []
			RepositoryItemDescriptor repItemDescriptorMock = Mock()
			skuRepositoryItemMock.getItemDescriptor() >> repItemDescriptorMock
			repItemDescriptorMock.getItemDescriptorName() >> BBBCatalogConstants.SKU_ITEM_DESCRIPTOR
		when:
			def booleanResult = testObjCatalogTools.hasSDDAttribute(siteId, skuRepositoryItemMock, regionPromoAttr, false)
		 
		then:
		booleanResult == false

	}

	def "hasSDDAttribute. This TC is when sddAttributeKeyList is null"() {

		given:
			String siteId = "BedBathUS"
			String skuId = "222522"
			String regionPromoAttr = "12_2"
			
			catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepositoryItemMock
			skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
			bbbSiteToAttributeSiteMap.put("BedBathUS", "bb")
			testObjCatalogTools.getBBBSiteToAttributeSiteMap() >> bbbSiteToAttributeSiteMap
			repositoryItemMock.getPropertyValue(bbbSiteToAttributeSiteMap.get("BedBathUS")) >> true
			repositoryItemMock.getPropertyValue("startDate") >> new Date()
			repositoryItemMock.getPropertyValue("endDate") >> DateUtils.addMonths(new Date(), 6)
			repositoryItemMock.getPropertyValue("skuAttribute") >> repositoryItemMock
			testObjCatalogTools.checkAttributeIntlApplicability(_) >> false
			repositoryItemMock.getRepositoryId() >> "10_2"
			testObjCatalogTools.getAllValuesForKey("SameDayDeliveryKeys", "SameDayDeliveryFlag") >> null
			testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY, BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> null
			RepositoryItemDescriptor repItemDescriptorMock = Mock()
			skuRepositoryItemMock.getItemDescriptor() >> repItemDescriptorMock
			repItemDescriptorMock.getItemDescriptorName() >> BBBCatalogConstants.SKU_ITEM_DESCRIPTOR
		when:
			def booleanResult = testObjCatalogTools.hasSDDAttribute(siteId, skuRepositoryItemMock, regionPromoAttr, false)
		 
		then:
		booleanResult == false

	}

	def "hasSDDAttribute. This TC is when attributeMap is empty"() {

		given:
			String siteId = "BedBathUS"
			String skuId = "222522"
			String regionPromoAttr = "12_2"
			
			catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepositoryItemMock
			skuRepositoryItemMock.getPropertyValue("skuAttributeRelns") >> [repositoryItemMock].toSet()
			RepositoryItemDescriptor repItemDescriptorMock = Mock()
			skuRepositoryItemMock.getItemDescriptor() >> repItemDescriptorMock
			repItemDescriptorMock.getItemDescriptorName() >> BBBCatalogConstants.SKU_ITEM_DESCRIPTOR
			Map skuattr = new HashMap()
			testObjCatalogTools.getSkuAttributeList(*_) >> skuattr
		when:
			def booleanResult = testObjCatalogTools.hasSDDAttribute(siteId, skuRepositoryItemMock, regionPromoAttr, false)
		 
		then:
		booleanResult == false

	}

	def "hasSDDAttribute. This TC is when siteId is empty"() {
		
		given:
			String siteId = ""
			String skuId = "222522"
			String regionPromoAttr = "12_2"
			RepositoryItemDescriptor repItemDescriptorMock = Mock()
			skuRepositoryItemMock.getItemDescriptor() >> repItemDescriptorMock
			repItemDescriptorMock.getItemDescriptorName() >> BBBCatalogConstants.SKU_ITEM_DESCRIPTOR
		when:
			def booleanResult = testObjCatalogTools.hasSDDAttribute(siteId, skuRepositoryItemMock, regionPromoAttr, false)
		 
		then:
			booleanResult == null
			thrown BBBBusinessException

	}
	
	def "hasSDDAttribute. This TC is when skuItem is null"() {
		
		given:
			String siteId = "BedBathUS"
			String skuId = ""
			String regionPromoAttr = "12_2"
			RepositoryItemDescriptor repItemDescriptorMock = Mock()
			skuRepositoryItemMock.getItemDescriptor() >> repItemDescriptorMock
			repItemDescriptorMock.getItemDescriptorName() >> BBBCatalogConstants.SKU_ITEM_DESCRIPTOR
		when:
			def booleanResult = testObjCatalogTools.hasSDDAttribute(siteId, null, regionPromoAttr, false)
		 
		then:
		booleanResult == null
		thrown BBBBusinessException

	}
	
	def "hasSDDAttribute. This TC is when getItemDescriptor() throws RepositoryException"() {
		
		given:
			String siteId = "BedBathUS"
			String skuId = "232323"
			String regionPromoAttr = "12_2"
			
			catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for Repository Exception")}
			skuRepositoryItemMock.getItemDescriptor() >> {throw new RepositoryException("Repository Exception Mock")}
		when:
			def booleanResult = testObjCatalogTools.hasSDDAttribute(siteId, skuRepositoryItemMock, regionPromoAttr, false)
		 
		then:
		booleanResult == null
		thrown BBBSystemException

	}

	///////////////////////////////////TCs for hasSDDAttribute ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for isFixedPriceShipping Starts ///////////////////////////////////////////////////////////////
	//Signature : public final boolean isFixedPriceShipping(final String siteId, final String skuId) //////////////

	def "isFixedPriceShipping. This TC is the happy flow of isFixedPriceShipping"() {

		given:
		String siteId = "BedBathUS"
		String skuId = "232323"

		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME) >> true

		when:
		def booleanResult = testObjCatalogTools.isFixedPriceShipping(siteId, skuId)

		then:
		booleanResult == true

	}

	def "isFixedPriceShipping. This TC is when giftCert property is not defined"() {

		given:
		String siteId = "BedBathUS"
		String skuId = "232323"

		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME) >> null

		when:
		def booleanResult = testObjCatalogTools.isFixedPriceShipping(siteId, skuId)

		then:
		booleanResult == false

	}

	def "isFixedPriceShipping. This TC is when repositoryItem returns no item"() {

		given:
		String siteId = "BedBathUS"
		String skuId = "232323"

		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null

		when:
		def booleanResult = testObjCatalogTools.isFixedPriceShipping(siteId, skuId)

		then:
		booleanResult == null
		thrown(BBBBusinessException)

	}

	def "isFixedPriceShipping. This TC is when logging error is turned off"() {

		given:
		String siteId = "BedBathUS"
		String skuId = "232323"

		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null
		testObjCatalogTools.isLoggingError() >> false

		when:
		def booleanResult = testObjCatalogTools.isFixedPriceShipping(siteId, skuId)

		then:
		booleanResult == null
		thrown(BBBBusinessException)

	}

	def "isFixedPriceShipping. This TC is when repository throws exception"() {

		given:
		String siteId = "BedBathUS"
		String skuId = "232323"

		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for Repository Exception")}

		when:
		def booleanResult = testObjCatalogTools.isFixedPriceShipping(siteId, skuId)

		then:
		booleanResult == null
		thrown(BBBSystemException)

	}

	///////////////////////////////////TCs for isFixedPriceShipping ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getSKUSurcharge Starts ///////////////////////////////////////////////////////////////
	//Signature : public final double getSKUSurcharge(final String pSiteId, final String pSkuId, final String shippingMethodId) //////////////

	def "getSKUSurcharge. This TC is the happy flow of getSKUSurcharge"() {

		given:
		String siteId = "BedBathUS"
		String skuId = "232323"
		String shippingMethodId = "LWA"

		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		Double shippingSurcharge = new Double(22.2)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SHIPPING_SURCHARGE_SKU_PROPERTY_NAME) >> shippingSurcharge

		when:
		double surcharge = testObjCatalogTools.getSKUSurcharge(siteId, skuId, shippingMethodId)

		then:
		surcharge == 22.2
	}

	def "getSKUSurcharge. This TC is when repositoryItem returns no items"() {

		given:
		String siteId = "BedBathUS"
		String skuId = "232323"
		String shippingMethodId = "LWA"

		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null

		when:
		double surcharge = testObjCatalogTools.getSKUSurcharge(siteId, skuId, shippingMethodId)

		then:
		surcharge == 0.0
		thrown(BBBBusinessException)
	}

	def "getSKUSurcharge. This TC is when repositoryItem throws Exception"() {

		given:
		String siteId = "BedBathUS"
		String skuId = "232323"
		String shippingMethodId = "LWA"

		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for Repository Exception")}

		when:
		double surcharge = testObjCatalogTools.getSKUSurcharge(siteId, skuId, shippingMethodId)

		then:
		surcharge == 0.0
		thrown(BBBSystemException)
	}

	def "getSKUSurcharge. This TC is when shippingSurcharge property is not defined"() {

		given:
		String siteId = "BedBathUS"
		String skuId = "232323"
		String shippingMethodId = "LWA"

		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SHIPPING_SURCHARGE_SKU_PROPERTY_NAME) >> null

		when:
		double surcharge = testObjCatalogTools.getSKUSurcharge(siteId, skuId, shippingMethodId)

		then:
		surcharge == 0.0
	}

	def "getSKUSurcharge. This TC is when loggingDebug is turned off"() {

		given:
		String siteId = "BedBathUS"
		String skuId = "232323"
		String shippingMethodId = "LWA"

		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null
		testObjCatalogTools.isLoggingError() >> false

		when:
		double surcharge = testObjCatalogTools.getSKUSurcharge(siteId, skuId, shippingMethodId)

		then:
		surcharge == 0.0
		thrown(BBBBusinessException)
	}

	///////////////////////////////////TCs for getSKUSurcharge ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getParentProductForSku(final String skuId) Starts ///////////////////////////////////////////////////////////////
	//Signature : public final String getParentProductForSku(final String skuId) //////////////

	def "getParentProductForSku of String. This TC is the happy flow of getParentProductForSku"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> [repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >> "232333"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) >> new Date()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) >> new Date()
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> false


		when:
		def parentProductId = testObjCatalogTools.getParentProductForSku(skuId)

		then:
		parentProductId == "232333"
	}

	def "getParentProductForSku of String. This TC is when skuId is not defined"() {

		given:
		String skuId = null

		when:
		def parentProductId = testObjCatalogTools.getParentProductForSku(skuId)

		then:
		parentProductId == null
		thrown(BBBBusinessException)
	}

	def "getParentProductForSku of Stirng. This TC is when skuId is empty"() {

		given:
		String skuId = ""

		when:
		def parentProductId = testObjCatalogTools.getParentProductForSku(skuId)

		then:
		parentProductId == null
		thrown(BBBBusinessException)
	}

	def "getParentProductForSku of Stirng. This TC is when parentProducts property is not defined"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> null

		when:
		def parentProductId = testObjCatalogTools.getParentProductForSku(skuId)

		then:
		parentProductId == ""
	}

	def "getParentProductForSku of Stirng. This TC is when parentProducts property is empty"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> [].toSet()

		when:
		def parentProductId = testObjCatalogTools.getParentProductForSku(skuId)

		then:
		parentProductId == ""
	}

	def "getParentProductForSku of Stirng. This TC is when isProductActive returns false"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> [repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >> "232333"

		when:
		def parentProductId = testObjCatalogTools.getParentProductForSku(skuId)

		then:
		parentProductId == ""
	}

	///////////////////////////////////TCs for getParentProductForSku(final String skuId) ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getParentProductForSku(final String skuId, final boolean activeParentNotReqd) Starts ///////////////////////////////////////////////////////////////
	//Signature : public final String getParentProductForSku(final String skuId, final boolean activeParentNotReqd) //////////////

	def "getParentProductForSku of Stirng,boolean. This TC is the happy flow of getParentProductForSku"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> [repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >> "232333"
		when:
		def parentProductId = testObjCatalogTools.getParentProductForSku(skuId, false)

		then:
		parentProductId == "232333"
	}

	def "getParentProductForSku of String,boolean. This TC is when skuId is not defined"() {

		given:
		String skuId = null

		when:
		def parentProductId = testObjCatalogTools.getParentProductForSku(skuId, false)

		then:
		parentProductId == null
		thrown(BBBBusinessException)
	}

	def "getParentProductForSku of Stirng,boolean. This TC is when skuId is empty"() {

		given:
		String skuId = ""

		when:
		def parentProductId = testObjCatalogTools.getParentProductForSku(skuId, true)

		then:
		parentProductId == null
		thrown(BBBBusinessException)
	}

	def "getParentProductForSku of Stirng,boolean. This TC is when parentProducts property is not defined"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> null

		when:
		def parentProductId = testObjCatalogTools.getParentProductForSku(skuId, false)

		then:
		parentProductId == ""
	}

	def "getParentProductForSku of Stirng,boolean. This TC is when parentProducts property is empty"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> [].toSet()

		when:
		def parentProductId = testObjCatalogTools.getParentProductForSku(skuId, true)

		then:
		parentProductId == ""
	}

	def "getParentProductForSku of Stirng,boolean. This TC is when repositoryItem returns no item"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null

		when:
		def parentProductId = testObjCatalogTools.getParentProductForSku(skuId, true)

		then:
		parentProductId == null
		thrown(BBBBusinessException)
	}

	def "getParentProductForSku of Stirng,boolean. This TC is when repositoryItem throws exception"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for Repository Exception")}

		when:
		def parentProductId = testObjCatalogTools.getParentProductForSku(skuId, true)

		then:
		parentProductId == null
		thrown(BBBSystemException)
	}

	///////////////////////////////////TCs for getParentProductForSku(final String skuId, final boolean activeParentNotReqd) ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getSkuImages Starts ///////////////////////////////////////////////////////////////
	//Signature : public final ImageVO getSkuImages(final String skuId) //////////////

	def "getSkuImages. This TC is the happy flow of getSkuImages"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME) >> "large Image"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME) >> "small Image"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) >> "swatch Image"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) >> "medium Image"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) >> "thumbnail Image"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ZOOM_INDEX_PATH_PRODUCT_PROPERTY_NAME) >> 10
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ZOOM_IMAGE_IMAGE_PROPERTY_NAME) >> "zoom Image"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ANYWHERE_ZOOM_PRODUCT_PROPERTY_NAME) >> true

		when:
		ImageVO skuImages = testObjCatalogTools.getSkuImages(skuId)

		then:
		skuImages.largeImage == "large Image"
		skuImages.smallImage == "small Image"
		skuImages.swatchImage == "swatch Image"
		skuImages.mediumImage == "medium Image"
		skuImages.thumbnailImage == "thumbnail Image"
		skuImages.zoomImageIndex == 10
		skuImages.zoomImage == "zoom Image"
		skuImages.anywhereZoomAvailable == true
	}

	def "getSkuImages. This TC is when the properties are not defined in repository"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ZOOM_INDEX_PATH_PRODUCT_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ZOOM_IMAGE_IMAGE_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ANYWHERE_ZOOM_PRODUCT_PROPERTY_NAME) >> null

		when:
		ImageVO skuImages = testObjCatalogTools.getSkuImages(skuId)

		then:
		skuImages.largeImage == ""
		skuImages.smallImage == ""
		skuImages.swatchImage == ""
		skuImages.mediumImage == ""
		skuImages.thumbnailImage == ""
		skuImages.zoomImageIndex == 0
		skuImages.zoomImage == ""
		skuImages.anywhereZoomAvailable == false
	}

	def "getSkuImages. This TC is when the skuId is not defined"() {

		given:
		String skuId = null

		when:
		ImageVO skuImages = testObjCatalogTools.getSkuImages(skuId)

		then:
		skuImages == null
		thrown(BBBBusinessException)
	}

	def "getSkuImages. This TC is when the skuId is empty"() {

		given:
		String skuId = ""

		when:
		ImageVO skuImages = testObjCatalogTools.getSkuImages(skuId)

		then:
		skuImages == null
		thrown(BBBBusinessException)
	}

	def "getSkuImages. This TC is when the repositoryItem returns no item"() {

		given:
		String skuId = "2322323"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null

		when:
		ImageVO skuImages = testObjCatalogTools.getSkuImages(skuId)

		then:
		skuImages == null
		thrown(BBBBusinessException)
	}

	def "getSkuImages. This TC is when the repositoryItem throws exception"() {

		given:
		String skuId = "2322323"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for Repository Exception")}

		when:
		ImageVO skuImages = testObjCatalogTools.getSkuImages(skuId)

		then:
		skuImages == null
		thrown(BBBSystemException)
	}


	///////////////////////////////////TCs for getSkuImages ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for isGiftWrapItem Starts ///////////////////////////////////////////////////////////////
	//Signature : public final boolean isGiftWrapItem(final String pSiteId, final String skuId) //////////////

	def "isGiftWrapItem. This TC is the happy flow for isGiftWrapItem"() {

		given:
		String skuId = "2322323"
		String siteId = "BedBathUS"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		testObjCatalogTools.isSkuActive(productRepositoryItemMock) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_ELIGIBLE_SKU_PROPERTY_NAME) >> true

		when:
		def booleanResult = testObjCatalogTools.isGiftWrapItem(siteId, skuId)

		then:
		booleanResult == true
	}

	def "isGiftWrapItem. This TC is when repository Item returns no item"() {

		given:
		String skuId = "2322323"
		String siteId = "BedBathUS"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null

		when:
		def booleanResult = testObjCatalogTools.isGiftWrapItem(siteId, skuId)

		then:
		booleanResult == null
		thrown(BBBBusinessException)
	}

	def "isGiftWrapItem. This TC is when repository Item throws Exception"() {

		given:
		String skuId = "2322323"
		String siteId = "BedBathUS"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for Repository Exception")}

		when:
		def booleanResult = testObjCatalogTools.isGiftWrapItem(siteId, skuId)

		then:
		booleanResult == null
		thrown(BBBSystemException)
	}

	def "isGiftWrapItem. This TC is when isActive method returns false"() {

		given:
		String skuId = "2322323"
		String siteId = "BedBathUS"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		testObjCatalogTools.isSkuActive(productRepositoryItemMock) >> false

		when:
		def booleanResult = testObjCatalogTools.isGiftWrapItem(siteId, skuId)

		then:
		booleanResult == false
	}

	def "isGiftWrapItem. This TC is when giftWrapEligible property is not defined"() {

		given:
		String skuId = "2322323"
		String siteId = "BedBathUS"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		testObjCatalogTools.isSkuActive(productRepositoryItemMock) >> true
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_ELIGIBLE_SKU_PROPERTY_NAME) >> null

		when:
		def booleanResult = testObjCatalogTools.isGiftWrapItem(siteId, skuId)

		then:
		booleanResult == false
	}


	///////////////////////////////////TCs for isGiftWrapItem ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for isSkuActive(final String skuId) Starts ///////////////////////////////////////////////////////////////
	//Signature : public boolean isSkuActive(final String skuId) //////////////

	def "isSkuActive of String. This TC is the happy flow of isSkuActive"() {

		given:
		String skuId = "2322323"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		testObjCatalogTools.isSkuActive(productRepositoryItemMock) >> true

		when:
		def booleanResult = testObjCatalogTools.isSkuActive(skuId)

		then:
		booleanResult == true
	}

	///////////////////////////////////TCs for isSkuActive(final String skuId) ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for isSkuActiveForStore Starts ///////////////////////////////////////////////////////////////
	//Signature : public final boolean isSkuActiveForStore(final RepositoryItem skuRepositoryItem) //////////////

	def "isSkuActiveForStore. This TC is the happy flow of isSkuActiveForStore"() {

		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isSkuActiveForStore(skuRepositoryItemMock)

		then:
		booleanResult == false
	}

	def "isSkuActiveForStore. This TC is when properties are not defined"() {

		given:
		globalRepositoryToolsMock.setPreviewEnabled(false)
		globalRepositoryToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PREVIEW_ENABLED) >> ["true"]
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME) >> null

		when:
		def booleanResult = testObjCatalogTools.isSkuActiveForStore(skuRepositoryItemMock)

		then:
		booleanResult == false
	}

	def "isSkuActiveForStore. This TC is when repository Item returns no item"() {

		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)

		when:
		def booleanResult = testObjCatalogTools.isSkuActiveForStore(null)

		then:
		booleanResult == false
	}

	def "isSkuActiveForStore. This TC is when diable property is false"() {

		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isSkuActiveForStore(skuRepositoryItemMock)

		then:
		booleanResult == true
	}

	def "isSkuActiveForStore. This TC is when preview date is before start date"() {

		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isSkuActiveForStore(skuRepositoryItemMock)

		then:
		booleanResult == false
	}

	def "isSkuActiveForStore. This TC is when preview date is after end date"() {

		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		def endDate = new GregorianCalendar(2014, Calendar.AUGUST, 3, 23, 59, 45).time
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> endDate
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isSkuActiveForStore(skuRepositoryItemMock)

		then:
		booleanResult == false
	}

	def "isSkuActiveForStore. This TC is when startDate property is not defined"() {

		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isSkuActiveForStore(skuRepositoryItemMock)

		then:
		booleanResult == false
	}

	def "isSkuActiveForStore. This TC is when endDate property is not defined"() {

		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUSION_SKU_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isSkuActiveForStore(skuRepositoryItemMock)

		then:
		booleanResult == false
	}

	///////////////////////////////////TCs for isSkuActiveForStore ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for isSkuActive(final RepositoryItem skuRepositoryItem, final String siteId) Starts ///////////////////////////////////////////////////////////////
	//Signature : public final boolean isSkuActive(final RepositoryItem skuRepositoryItem, final String siteId) //////////////

	def "isSkuActive of RepositoryItem,String. This TC is the happy flow of isSkuActive"() {

		given:
		String siteId = "BedBathUS"
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_IDS_SKU_PROPERTY_NAME) >> ["BedBathUS"].toSet()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> false
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true

		when:
		def booleanResult = testObjCatalogTools.isSkuActive(skuRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isSkuActive of RepositoryItem,String. This TC is when the properties are not defined"() {

		given:
		String siteId = "BedBathUS"
		globalRepositoryToolsMock.setPreviewEnabled(false)
		globalRepositoryToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PREVIEW_ENABLED) >> ["true"]
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_IDS_SKU_PROPERTY_NAME) >> ["BedBathUS"].toSet()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> null

		when:
		def booleanResult = testObjCatalogTools.isSkuActive(skuRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isSkuActive of RepositoryItem,String. This TC is when the repository Item returns no item"() {

		given:
		String siteId = "BedBathUS"
		globalRepositoryToolsMock.setPreviewEnabled(true)

		when:
		def booleanResult = testObjCatalogTools.isSkuActive(null, siteId)

		then:
		booleanResult == false
	}

	def "isSkuActive of RepositoryItem,String. This TC is when the assocSites property is not defined"() {

		given:
		String siteId = "BedBathUS"
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_IDS_SKU_PROPERTY_NAME) >> null

		when:
		def booleanResult = testObjCatalogTools.isSkuActive(skuRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isSkuActive of RepositoryItem,String. This TC is when the assocSites property is empty"() {

		given:
		String siteId = "BedBathUS"
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_IDS_SKU_PROPERTY_NAME) >> [].toSet()

		when:
		def booleanResult = testObjCatalogTools.isSkuActive(skuRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isSkuActive of RepositoryItem,String. This TC is when the assocSites property is not equal to siteId"() {

		given:
		String siteId = "BedBathUS"
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_IDS_SKU_PROPERTY_NAME) >> ["BuyBuyBaby"].toSet()

		when:
		def booleanResult = testObjCatalogTools.isSkuActive(skuRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isSkuActive of RepositoryItem,String. This TC is when disable property is false"() {

		given:
		String siteId = "BedBathUS"
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_IDS_SKU_PROPERTY_NAME) >> ["BedBathUS"].toSet()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isSkuActive(skuRepositoryItemMock, siteId)

		then:
		booleanResult == true
	}

	def "isSkuActive of RepositoryItem,String. This TC is when preview date is before start date"() {

		given:
		String siteId = "BedBathUS"
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_IDS_SKU_PROPERTY_NAME) >> ["BedBathUS"].toSet()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isSkuActive(skuRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isSkuActive of RepositoryItem,String. This TC is when preview date is after end date"() {

		given:
		String siteId = "BedBathUS"
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_IDS_SKU_PROPERTY_NAME) >> ["BedBathUS"].toSet()
		def endDate = new GregorianCalendar(2014, Calendar.AUGUST, 3, 23, 59, 45).time
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> endDate
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isSkuActive(skuRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isSkuActive of RepositoryItem,String. This TC is when startDate property is not defined"() {

		given:
		String siteId = "BedBathUS"
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_IDS_SKU_PROPERTY_NAME) >> ["BedBathUS"].toSet()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> false
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isSkuActive(skuRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	def "isSkuActive of RepositoryItem,String. This TC is when endDate property is not defined"() {

		given:
		String siteId = "BedBathUS"
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_IDS_SKU_PROPERTY_NAME) >> ["BedBathUS"].toSet()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> false
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isSkuActive(skuRepositoryItemMock, siteId)

		then:
		booleanResult == false
	}

	///////////////////////////////////TCs for isSkuActive(final RepositoryItem skuRepositoryItem, final String siteId) ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for isCategoryActive Starts ///////////////////////////////////////////////////////////////
	//Signature : public final boolean isCategoryActive(final RepositoryItem categoryRepositoryItem) //////////////

	def "isCategoryActive. This TC is the happy flow of isCategoryActive"() {

		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> true

		when:
		def booleanResult = testObjCatalogTools.isCategoryActive(skuRepositoryItemMock)

		then:
		booleanResult == false
	}

	def "isCategoryActive. This TC is when catdisable property is not defined"() {

		given:
		globalRepositoryToolsMock.setPreviewEnabled(false)
		globalRepositoryToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PREVIEW_ENABLED) >> ["true"]
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> null

		when:
		def booleanResult = testObjCatalogTools.isCategoryActive(skuRepositoryItemMock)

		then:
		booleanResult == false
	}

	def "isCategoryActive. This TC is when preview date is before start date"() {

		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> DateUtils.addMonths(new Date(), 6)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isCategoryActive(skuRepositoryItemMock)

		then:
		booleanResult == false
	}

	def "isCategoryActive. This TC is when preview date is after end date"() {

		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		def endDate = new GregorianCalendar(2014, Calendar.AUGUST, 3, 23, 59, 45).time
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> endDate
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isCategoryActive(skuRepositoryItemMock)

		then:
		booleanResult == false
	}

	def "isCategoryActive. This TC is when end date property is not defined"() {

		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isCategoryActive(skuRepositoryItemMock)

		then:
		booleanResult == true
	}

	def "isCategoryActive. This TC is when start date property is not defined"() {

		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date(addDays(1, "MM/DD/YYYY"))
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> false

		when:
		def booleanResult = testObjCatalogTools.isCategoryActive(skuRepositoryItemMock)

		then:
		booleanResult == true
	}

	///////////////////////////////////TCs for isCategoryActive ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getSalePrice Starts ///////////////////////////////////////////////////////////////
	//Signature : public final Double getSalePrice(final String productId, final String skuId) //////////////

	def "getSalePrice. This TC is the happy flow of getSalePrice"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		ServletUtil.setCurrentUserProfile(skuRepositoryItemMock)
		priceListManagerMock.getPriceList(skuRepositoryItemMock,BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME) >> priceListRepositoryItemMock
		priceListManagerMock.getPrice(priceListRepositoryItemMock,productId, skuId) >> priceRepositoryItemMock
		priceRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> 22.2d

		when:
		Double results = testObjCatalogTools.getSalePrice(productId, skuId)

		then:
		results == 22.2
	}

	def "getSalePrice. This TC is when priceist Repository Item returns no Item"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		ServletUtil.setCurrentUserProfile(skuRepositoryItemMock)
		priceListManagerMock.getPriceList(skuRepositoryItemMock,BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME) >> null

		when:
		Double results = testObjCatalogTools.getSalePrice(productId, skuId)

		then:
		results == 0.0
	}

	def "getSalePrice. This TC is when priceist Repository Item throws Exception"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		ServletUtil.setCurrentUserProfile(skuRepositoryItemMock)
		priceListManagerMock.getPriceList(skuRepositoryItemMock,BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME) >> {throw new PriceListException("Mock for PriceListException")}

		when:
		Double results = testObjCatalogTools.getSalePrice(productId, skuId)

		then:
		results == null
		thrown(BBBSystemException)
	}

	def "getSalePrice. This TC is when price Repository Item returns no item"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		ServletUtil.setCurrentUserProfile(skuRepositoryItemMock)
		priceListManagerMock.getPriceList(skuRepositoryItemMock,BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME) >> priceListRepositoryItemMock
		priceListManagerMock.getPrice(priceListRepositoryItemMock,productId, skuId) >> null

		when:
		Double results = testObjCatalogTools.getSalePrice(productId, skuId)

		then:
		results == 0.0
	}

	def "getSalePrice. This TC is when price Repository Item throws exception"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		ServletUtil.setCurrentUserProfile(skuRepositoryItemMock)
		priceListManagerMock.getPriceList(skuRepositoryItemMock,BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME) >> priceListRepositoryItemMock
		priceListManagerMock.getPrice(priceListRepositoryItemMock,productId, skuId) >> {throw new RemovedItemException("Mock for RemovedItemException")}

		when:
		Double results = testObjCatalogTools.getSalePrice(productId, skuId)

		then:
		results == null
		thrown(BBBSystemException)
	}

	///////////////////////////////////TCs for getSalePrice ends ///////////////////////////////////////////////////////////////


	///////////////////////////////////TCs for getListPrice Starts ///////////////////////////////////////////////////////////////
	//Signature : public final Double getListPrice(final String productId, final String skuId) //////////////

	def "getListPrice. This TC is the happy flow of getListPrice"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		ServletUtil.setCurrentUserProfile(skuRepositoryItemMock)
		priceListManagerMock.getPriceList(skuRepositoryItemMock,BBBCatalogConstants.PRICE_LIST_ITEM_DESCRIPTOR) >> priceListRepositoryItemMock
		priceListManagerMock.getPrice(priceListRepositoryItemMock,productId, skuId) >> priceRepositoryItemMock
		priceRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> 24.2d

		when:
		Double results = testObjCatalogTools.getListPrice(productId, skuId)

		then:
		results == 24.2
	}

	def "getListPrice. This TC is when PriceList repositoryItem returns no item"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		ServletUtil.setCurrentUserProfile(skuRepositoryItemMock)
		priceListManagerMock.getPriceList(skuRepositoryItemMock,BBBCatalogConstants.PRICE_LIST_ITEM_DESCRIPTOR) >> null

		when:
		Double results = testObjCatalogTools.getListPrice(productId, skuId)

		then:
		results == 0.0
	}

	def "getListPrice. This TC is when price repositoryItem returns no item"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		ServletUtil.setCurrentUserProfile(skuRepositoryItemMock)
		priceListManagerMock.getPriceList(skuRepositoryItemMock,BBBCatalogConstants.PRICE_LIST_ITEM_DESCRIPTOR) >> priceListRepositoryItemMock
		priceListManagerMock.getPrice(priceListRepositoryItemMock,productId, skuId) >> null

		when:
		Double results = testObjCatalogTools.getListPrice(productId, skuId)

		then:
		results == 0.0
	}

	def "getListPrice. This TC is when PriceList RepositoryItem throws exception"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		ServletUtil.setCurrentUserProfile(skuRepositoryItemMock)
		priceListManagerMock.getPriceList(skuRepositoryItemMock,BBBCatalogConstants.PRICE_LIST_ITEM_DESCRIPTOR) >> {throw new PriceListException("Mock for PriceListException")}

		when:
		Double results = testObjCatalogTools.getListPrice(productId, skuId)

		then:
		results == null
		thrown(BBBSystemException)
	}

	///////////////////////////////////TCs for getListPrice ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getEffectivePrice Starts ///////////////////////////////////////////////////////////////
	//Signature : public final Double getEffectivePrice(final String productId, final String skuId, final String refNum, final double pPrice) //////////////

	def "getEffectivePrice of String,String,String,double. This TC is the happy flow of getEffectivePrice"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		String refNum = "refId"
		catalogRepositoryMock.getItem(skuId.trim(),BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "someValue"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ON_SALE) >> true
		ServletUtil.setCurrentUserProfile(skuRepositoryItemMock)
		priceListManagerMock.getPriceList(skuRepositoryItemMock,BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME) >> priceListRepositoryItemMock
		priceListManagerMock.getPrice(priceListRepositoryItemMock, productId, skuId) >> priceRepositoryItemMock
		priceRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> 22.2d
		BBBUtility.checkCurrentPriceForSavedItem(true, 0.0, 22.2, "someValue", 15.2, "refId") >> 22.2d

		when:
		Double results = testObjCatalogTools.getEffectivePrice(productId, skuId, refNum , 15.2)

		then:
		results == 22.2
	}

	def "getEffectivePrice of String,String,String,double. This TC is when Repository Item returns no item"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		String refNum = "refId"
		catalogRepositoryMock.getItem(skuId.trim(),BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null

		when:
		Double results = testObjCatalogTools.getEffectivePrice(productId, skuId, refNum , 15.2)

		then:
		results == null
		thrown(BBBBusinessException)
	}

	def "getEffectivePrice of String,String,String,double. This TC is when Repository Item throws Exception"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		String refNum = "refId"
		catalogRepositoryMock.getItem(skuId.trim(),BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for RepositoryException")}

		when:
		Double results = testObjCatalogTools.getEffectivePrice(productId, skuId, refNum , 15.2)

		then:
		results == null
		thrown(BBBSystemException)
	}

	///////////////////////////////////TCs for getListPrice ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getEffectivePrice Starts ///////////////////////////////////////////////////////////////
	//Signature : public final Double getEffectivePrice(final String productId, final String skuId) //////////////

	def "getEffectivePrice of String,String. This TC is the happy flow of getEffectivePrice"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId.trim(),BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ON_SALE) >> true
		ServletUtil.setCurrentUserProfile(skuRepositoryItemMock)
		priceListManagerMock.getPriceList(skuRepositoryItemMock,BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME) >> priceListRepositoryItemMock
		priceListManagerMock.getPrice(priceListRepositoryItemMock, productId, skuId) >> priceRepositoryItemMock
		priceRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> 26.66d

		when:
		Double results = testObjCatalogTools.getEffectivePrice(productId, skuId)

		then:
		results == 26.66
	}

	def "getEffectivePrice of String,String. This TC is when onSale property is false"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId.trim(),BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.ON_SALE) >> false

		when:
		Double results = testObjCatalogTools.getEffectivePrice(productId, skuId)

		then:
		results == 0.0
	}

	def "getEffectivePrice of String,String. This TC is when repositoryItem returns no item"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId.trim(),BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null

		when:
		Double results = testObjCatalogTools.getEffectivePrice(productId, skuId)

		then:
		results == null
		thrown(BBBBusinessException)
	}

	def "getEffectivePrice of String,String. This TC is when repositoryItem throws exception"() {

		given:
		String productId = "31232"
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId.trim(),BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for RepositoryException")}

		when:
		Double results = testObjCatalogTools.getEffectivePrice(productId, skuId)

		then:
		results == null
		thrown(BBBSystemException)
	}

	///////////////////////////////////TCs for getListPrice ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getBccManagedCategoryList Starts ///////////////////////////////////////////////////////////////
	//Signature : public final List<String> getBccManagedCategoryList(int tmpDepth, String categoryId) //////////////

	def "getBccManagedCategoryList. This TC is the happy flow of getBccManagedCategoryList"() {

		given:
		String categoryId = "CAT2323"
		testObjCatalogTools.executeRQLQuery(*_) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.L2_EXCLUSION) >> ["L2Exclusion"]

		when:
		List<String> mFacetList = testObjCatalogTools.getBccManagedCategoryList(1, categoryId)

		then:
		mFacetList[0].size() > 0
	}

	def "getBccManagedCategoryList. This TC is when tmpDepth is 2"() {

		given:
		String categoryId = "CAT2323"
		testObjCatalogTools.executeRQLQuery(*_) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.L3_EXCLUSION) >> ["L3Exclusion"]

		when:
		List<String> mFacetList = testObjCatalogTools.getBccManagedCategoryList(2, categoryId)

		then:
		mFacetList[0].size() > 0
	}

	def "getBccManagedCategoryList. This TC is when tmpDepth is different"() {

		given:
		String categoryId = "CAT2323"
		testObjCatalogTools.executeRQLQuery(*_) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.L2_EXCLUSION) >> null

		when:
		List<String> mFacetList = testObjCatalogTools.getBccManagedCategoryList(3, categoryId)

		then:
		mFacetList[0] == null
	}

	def "getBccManagedCategoryList. This TC is when repository Item returns no item"() {

		given:
		String categoryId = "CAT2323"
		testObjCatalogTools.executeRQLQuery(*_) >> null

		when:
		List<String> mFacetList = testObjCatalogTools.getBccManagedCategoryList(2, categoryId)

		then:
		mFacetList[0] == null
	}

	def "getBccManagedCategoryList. This TC is when repositoryItem[0] returns no item"() {

		given:
		String categoryId = "CAT2323"
		def RepositoryItemMock repositoryItem1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [null]
		testObjCatalogTools.executeRQLQuery(*_) >> repositoryItemList

		when:
		List<String> mFacetList = testObjCatalogTools.getBccManagedCategoryList(2, categoryId)

		then:
		mFacetList[0] == null
	}

	///////////////////////////////////TCs for getBccManagedCategoryList ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getRelatedCategories(final String productId, final String siteId) Starts ///////////////////////////////////////////////////////////////
	//Signature : public List<CategoryVO> getRelatedCategories (final String productId, final String siteId) //////////////

	def "getRelatedCategories of String,String. This TC is the happy flow of getRelatedCategories"() {

		given:
		String productId = "232333"
		String siteId = "BedBathUS"

		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME) >> [repositoryItemMock].toSet()
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.RELATED_CATEGORIES_COUNT) >> ["23"]
		testObjCatalogTools.getActiveParentCategoriesForSite([repositoryItemMock].toSet(), siteId) >> [categoryVOMock]

		when:
		List<CategoryVO> relatedCategories = testObjCatalogTools.getRelatedCategories(productId, siteId)

		then:
		relatedCategories == [categoryVOMock]
	}

	def "getRelatedCategories of String,String. This TC is when productId is passed empty"() {

		given:
		String productId = ""
		String siteId = "BedBathUS"

		when:
		List<CategoryVO> relatedCategories = testObjCatalogTools.getRelatedCategories(productId, siteId)

		then:
		relatedCategories == null
		thrown(BBBBusinessException)
	}

	def "getRelatedCategories of String,String. This TC is when repository Item returns no item"() {

		given:
		String productId = "313131"
		String siteId = "BedBathUS"
		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null

		when:
		List<CategoryVO> relatedCategories = testObjCatalogTools.getRelatedCategories(productId, siteId)

		then:
		relatedCategories == null
		thrown(BBBBusinessException)
	}

	def "getRelatedCategories of String,String. This TC is when repository Item throws exception"() {

		given:
		String productId = "313131"
		String siteId = "BedBathUS"
		catalogRepositoryMock.getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for RepositoryException")}

		when:
		List<CategoryVO> relatedCategories = testObjCatalogTools.getRelatedCategories(productId, siteId)

		then:
		relatedCategories == null
		thrown(BBBSystemException)
	}

	///////////////////////////////////TCs for getRelatedCategories(final String productId, final String siteId) ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getRelatedCategories(final RepositoryItem productRepoItem, final String siteId) Starts ///////////////////////////////////////////////////////////////
	//Signature : public List<CategoryVO> getRelatedCategories (final RepositoryItem productRepoItem, final String siteId) //////////////

	def "getRelatedCategories of RepositoryItem,String. This TC is the happy flow of getRelatedCategories"() {

		given:
		String siteId = "BedBathUS"
		Set<RepositoryItem> parentCategories = new HashSet<RepositoryItem>()
		parentCategories.add(repositoryItemMock)
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME) >> parentCategories
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.RELATED_CATEGORIES_COUNT) >> ["23"]
		testObjCatalogTools.getActiveParentCategoriesForSite(parentCategories, siteId) >> [categoryVOMock]

		when:
		List<CategoryVO> relatedCategories = testObjCatalogTools.getRelatedCategories(productRepositoryItemMock, siteId)

		then:
		relatedCategories == [categoryVOMock]
	}

	def "getRelatedCategories of RepositoryItem,String. This TC is when parentCategories is empty"() {

		given:
		String siteId = "BedBathUS"
		Set<RepositoryItem> parentCategories = new HashSet<RepositoryItem>()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME) >> parentCategories

		when:
		List<CategoryVO> relatedCategories = testObjCatalogTools.getRelatedCategories(productRepositoryItemMock, siteId)

		then:
		relatedCategories == []
	}
	def "getRelatedCategories of RepositoryItem,String. This TC is when parentCategories is null"() {

		given:
		String siteId = "BedBathUS"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME) >> null

		when:
		List<CategoryVO> relatedCategories = testObjCatalogTools.getRelatedCategories(productRepositoryItemMock, siteId)

		then:
		relatedCategories == []
	}

	///////////////////////////////////TCs for getRelatedCategories(final RepositoryItem productRepoItem, final String siteId) ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getSiteTag Starts ///////////////////////////////////////////////////////////////
	//Signature : public final String getSiteTag(final String siteId) //////////////

	def "getSiteTag. This TC is the happy flow of getSiteTag"() {

		given:
		String siteId = "BedBathUS"
		siteRepositoryMock.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepositoryItemMock
		siteRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_TAG_SITE_PROPERTY_NAME) >> "BedBath"

		when:
		def siteTag = testObjCatalogTools.getSiteTag(siteId)

		then:
		siteTag == "BedBath"
	}

	def "getSiteTag. This TC is when repository Item returns no item"() {

		given:
		String siteId = "BedBathUS"
		siteRepositoryMock.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> null

		when:
		def siteTag = testObjCatalogTools.getSiteTag(siteId)

		then:
		siteTag == null
		thrown(BBBBusinessException)
	}

	def "getSiteTag. This TC is when the repository Item throws exception"() {

		given:
		String siteId = "BedBathUS"
		siteRepositoryMock.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for RepositoryException")}

		when:
		def siteTag = testObjCatalogTools.getSiteTag(siteId)

		then:
		siteTag == null
		thrown(BBBSystemException)
	}

	def "getSiteTag. This TC is when the siteTag property is not defined"() {

		given:
		String siteId = "BedBathUS"
		siteRepositoryMock.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepositoryItemMock
		siteRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_TAG_SITE_PROPERTY_NAME) >> null

		when:
		def siteTag = testObjCatalogTools.getSiteTag(siteId)

		then:
		siteTag == ""
	}


	///////////////////////////////////TCs for getSiteTag ends ///////////////////////////////////////////////////////////////


	///////////////////////////////////TCs for getcustomerCareEmailAddress Starts ///////////////////////////////////////////////////////////////
	//Signature : public final String getcustomerCareEmailAddress(final String siteId) //////////////

	def "getcustomerCareEmailAddress. This TC is the happy flow of getcustomerCareEmailAddress"() {

		given:
		String siteId = "BedBathUS"
		siteRepositoryMock.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepositoryItemMock
		siteRepositoryItemMock.getPropertyValue("customerCareEmailAddress") >> "customerservices@bedbath.com"

		when:
		def customerCareEmailAddress = testObjCatalogTools.getcustomerCareEmailAddress(siteId)

		then:
		customerCareEmailAddress == "customerservices@bedbath.com"
	}

	def "getcustomerCareEmailAddress. This TC is when the repositoryItem returns no Item"() {

		given:
		String siteId = "BedBathUS"
		siteRepositoryMock.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> null

		when:
		def customerCareEmailAddress = testObjCatalogTools.getcustomerCareEmailAddress(siteId)

		then:
		customerCareEmailAddress == null
		thrown(BBBSystemException)
	}

	def "getcustomerCareEmailAddress. This TC is when the repositoryItem throws Exception"() {

		given:
		String siteId = "BedBathUS"
		siteRepositoryMock.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for RepositoryException")}

		when:
		def customerCareEmailAddress = testObjCatalogTools.getcustomerCareEmailAddress(siteId)

		then:
		customerCareEmailAddress == null
		thrown(BBBSystemException)
	}

	///////////////////////////////////TCs for getcustomerCareEmailAddress ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getTimeZones Starts ///////////////////////////////////////////////////////////////
	//Signature : public final List<String> getTimeZones(final String siteId) //////////////

	def "getTimeZones. This TC is the happy flow of getTimeZones"() {

		given:
		String siteId = "BedBathUS"
		siteRepositoryMock.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepositoryItemMock
		def RepositoryItemMock repositoryItem1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [repositoryItem1]
		siteRepositoryItemMock.getPropertyValue(BBBCatalogConstants.TIMEZONES_PROPERTY_NAME) >> [repositoryItem1].toSet()
		repositoryItem1.setProperties(["timeZone" : "timeZone"])

		when:
		List<String> timeZoneList = testObjCatalogTools.getTimeZones(siteId)

		then:
		timeZoneList == ["timeZone"]
	}

	def "getTimeZones. This TC is when repositoryItem returns no item"() {

		given:
		String siteId = "BedBathUS"
		siteRepositoryMock.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> null

		when:
		List<String> timeZoneList = testObjCatalogTools.getTimeZones(siteId)

		then:
		timeZoneList == null
		thrown(BBBSystemException)
	}

	def "getTimeZones. This TC is when repositoryItem throws exception"() {

		given:
		String siteId = "BedBathUS"
		siteRepositoryMock.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for RepositoryException")}

		when:
		List<String> timeZoneList = testObjCatalogTools.getTimeZones(siteId)

		then:
		timeZoneList == null
		thrown(BBBSystemException)
	}

	def "getTimeZones. This TC is when the timeZones Property is not defined"() {

		given:
		String siteId = "BedBathUS"
		siteRepositoryMock.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepositoryItemMock
		def RepositoryItemMock repositoryItem1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [repositoryItem1]
		siteRepositoryItemMock.getPropertyValue(BBBCatalogConstants.TIMEZONES_PROPERTY_NAME) >> null

		when:
		List<String> timeZoneList = testObjCatalogTools.getTimeZones(siteId)

		then:
		timeZoneList == null
		thrown(BBBSystemException)
	}
	///////////////////////////////////TCs for getTimeZones ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getMaxInventorySKUForProduct Starts ///////////////////////////////////////////////////////////////
	//Signature : public final String getMaxInventorySKUForProduct(ProductVO productVO, final String siteId) //////////////

	def "getMaxInventorySKUForProduct. This TC is the happy flow for getMaxInventorySKUForProduct"() {

		given:
		String siteId = "BedBathUS"
		def ProductVO productVOMock = Mock()
		productVOMock.getChildSKUs() >> ["223232"]
		catalogRepositoryMock.getItem("223232",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		testObjCatalogTools.isSkuActive(productRepositoryItemMock) >> true
		onlineInventoryManagerMock.getMaxStockSku(["223232"], siteId) >> ["223232"]

		when:
		def maxInventorySkuId = testObjCatalogTools.getMaxInventorySKUForProduct(productVOMock, siteId)

		then:
		maxInventorySkuId != null
	}

	def "getMaxInventorySKUForProduct. This TC is when siteId is not defined in the parameter"() {

		given:
		String siteId = null
		def ProductVO productVOMock = Mock()

		when:
		def maxInventorySkuId = testObjCatalogTools.getMaxInventorySKUForProduct(productVOMock, siteId)

		then:
		maxInventorySkuId == null
	}

	def "getMaxInventorySKUForProduct. This TC is when siteId is empty"() {

		given:
		String siteId = ""
		def ProductVO productVOMock = Mock()

		when:
		def maxInventorySkuId = testObjCatalogTools.getMaxInventorySKUForProduct(productVOMock, siteId)

		then:
		maxInventorySkuId == null
	}

	def "getMaxInventorySKUForProduct. This TC is when childSkus returns no value"() {

		given:
		String siteId = "BedBathUS"
		def ProductVO productVOMock = Mock()
		productVOMock.getChildSKUs() >> null

		when:
		def maxInventorySkuId = testObjCatalogTools.getMaxInventorySKUForProduct(productVOMock, siteId)

		then:
		maxInventorySkuId == null
		thrown(BBBBusinessException)
	}

	def "getMaxInventorySKUForProduct. This TC is when childSkus is empty"() {

		given:
		String siteId = "BedBathUS"
		def ProductVO productVOMock = Mock()
		productVOMock.getChildSKUs() >> []

		when:
		def maxInventorySkuId = testObjCatalogTools.getMaxInventorySKUForProduct(productVOMock, siteId)

		then:
		maxInventorySkuId == null
		thrown(BBBBusinessException)
	}

	def "getMaxInventorySKUForProduct. This TC is when skuIds is empty"() {

		given:
		String siteId = "BedBathUS"
		def ProductVO productVOMock = Mock()
		productVOMock.getChildSKUs() >> ["223232"]
		catalogRepositoryMock.getItem("223232",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		testObjCatalogTools.isSkuActive(productRepositoryItemMock) >> false

		when:
		def maxInventorySkuId = testObjCatalogTools.getMaxInventorySKUForProduct(productVOMock, siteId)

		then:
		maxInventorySkuId == null
		thrown(BBBBusinessException)
	}

	///////////////////////////////////TCs for getMaxInventorySKUForProduct ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for isSKUAvailable Starts ///////////////////////////////////////////////////////////////
	//Signature : public final boolean isSKUAvailable(final String siteId, final String skuId) //////////////

	def "isSKUAvailable. This TC is the happy flow of isSKUAvailable"() {

		given:
		String siteId = "BedBathUS"
		String skuId = "223232"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		testObjCatalogTools.isSkuActive(productRepositoryItemMock) >> {return true}

		when:
		def booleanResult = testObjCatalogTools.isSKUAvailable(siteId, skuId)

		then:
		booleanResult == true
	}

	def "isSKUAvailable. This TC is when siteId is not defined in the parameter"() {

		given:
		String siteId = null
		String skuId = "223232"

		when:
		def booleanResult = testObjCatalogTools.isSKUAvailable(siteId, skuId)

		then:
		booleanResult == null
		thrown(BBBBusinessException)
	}

	def "isSKUAvailable. This TC is when siteId is passed empty in the parameter"() {

		given:
		String siteId = ""
		String skuId = "223232"

		when:
		def booleanResult = testObjCatalogTools.isSKUAvailable(siteId, skuId)

		then:
		booleanResult == null
		thrown(BBBBusinessException)
	}

	def "isSKUAvailable. This TC is when the repositoryItem throws exception"() {

		given:
		String siteId = "BedBathUS"
		String skuId = "223232"
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for RepositoryException")}

		when:
		def booleanResult = testObjCatalogTools.isSKUAvailable(siteId, skuId)

		then:
		booleanResult == null
		thrown(BBBSystemException)
	}

	///////////////////////////////////TCs for isSKUAvailable ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for isSkuExcluded Starts ///////////////////////////////////////////////////////////////
	//Signature : public final boolean isSkuExcluded(final String skuId, final String promotionCode, boolean isAppliedCoupon) //////////////

	def "isSkuExcluded. This TC is the happy flow of isSkuExcluded"() {

		given:
		String skuId = "223232"
		String promotionCode = "2232"
		def RepositoryItem jdaDeptItem = Mock()
		def RepositoryItem jdaSubDeptItem = Mock()
		def RepositoryItemMock repositoryItem1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [repositoryItem1]

		catalogRepositoryMock.getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) >> "vendorId"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> jdaDeptItem
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> jdaSubDeptItem
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) >> "jdaClass"
		jdaDeptItem.getRepositoryId() >> "j2121"
		jdaSubDeptItem.getRepositoryId() >> "js232"
		requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "BedBathUS"
		testObjCatalogTools.setLogCouponDetails(false)
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getCouponRuleSkuQuery(),["2232", "E", "BedBathUS", "223232", "j2121", "js232", "jdaClass", "vendorId", "0"],BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, couponRepositoryMock) >> [repositoryItem1]


		when:
		def booleanResult = testObjCatalogTools.isSkuExcluded(skuId, promotionCode, false)

		then:
		booleanResult == true
	}

	def "isSkuExcluded. This TC is when catalogRepositoryItem returns no item"() {

		given:
		String skuId = "223232"
		String promotionCode = "2232"
		catalogRepositoryMock.getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null

		when:
		def booleanResult = testObjCatalogTools.isSkuExcluded(skuId, promotionCode, true)

		then:
		booleanResult == false
	}

	def "isSkuExcluded. This TC is when catalogRepositoryItem throws exception"() {

		given:
		String skuId = "223232"
		String promotionCode = "2232"
		catalogRepositoryMock.getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for RepositoryException")}

		when:
		def booleanResult = testObjCatalogTools.isSkuExcluded(skuId, promotionCode, true)

		then:
		booleanResult == null
		thrown(BBBSystemException)
	}

	def "isSkuExcluded. This TC is when promotionCode passed empty in the parameter"() {

		given:
		String skuId = "223232"
		String promotionCode = ""

		when:
		def booleanResult = testObjCatalogTools.isSkuExcluded(skuId, promotionCode, true)

		then:
		booleanResult == false
	}

	def "isSkuExcluded. This TC is when skuId passed empty in the parameter"() {

		given:
		String skuId = ""
		String promotionCode = "23232"

		when:
		def booleanResult = testObjCatalogTools.isSkuExcluded(skuId, promotionCode, true)

		then:
		booleanResult == false
	}

	def "isSkuExcluded. This TC is when the request is not defined and passing couponRuleVendorDeptQuery query in repository"() {

		given:
		ServletUtil.setCurrentRequest(null)
		String skuId = "223232"
		String promotionCode = "2232"
		def RepositoryItem jdaDeptItem = Mock()
		def RepositoryItem jdaSubDeptItem = Mock()
		def RepositoryItemMock repositoryItem1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [repositoryItem1]
		catalogRepositoryMock.getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) >> "vendorId"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> jdaDeptItem
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> jdaSubDeptItem
		jdaDeptItem.getRepositoryId() >> "j2121"
		jdaSubDeptItem.getRepositoryId() >> "js232"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) >> "jdaClass"
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.setLogCouponDetails(true)
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getCouponRuleSkuQuery(),["2232", "E", "BedBathUS", "223232", "j2121", "js232", "jdaClass", "vendorId", "0"],BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, couponRepositoryMock) >> null
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getCouponRuleVendorDeptQuery(),["2232", "E", "BedBathUS", "223232", "j2121", "js232", "jdaClass", "vendorId", "0"], BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, couponRepositoryMock) >> [repositoryItem1]

		when:
		def booleanResult = testObjCatalogTools.isSkuExcluded(skuId, promotionCode, true)

		then:
		booleanResult == true
	}
	def "isSkuExcluded. This TC is by passing couponRuleVendorDeptClassQuery query in repository"() {

		given:
		String skuId = "223232"
		String promotionCode = "2232"
		def RepositoryItem jdaDeptItem = Mock()
		def RepositoryItem jdaSubDeptItem = Mock()
		def RepositoryItemMock repositoryItem1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [repositoryItem1]
		catalogRepositoryMock.getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) >> "vendorId"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> jdaDeptItem
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> jdaSubDeptItem
		jdaDeptItem.getRepositoryId() >> "j2121"
		jdaSubDeptItem.getRepositoryId() >> "js232"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) >> "jdaClass"
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.isLogCouponDetails() >> {return true}
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getCouponRuleSkuQuery(),["2232", "E", "BedBathUS", "223232", "j2121", "js232", "jdaClass", "vendorId", "0"],BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, couponRepositoryMock) >> []
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getCouponRuleVendorDeptQuery(),["2232", "E", "BedBathUS", "223232", "j2121", "js232", "jdaClass", "vendorId", "0"], BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, couponRepositoryMock) >> null
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getCouponRuleVendorDeptClassQuery(),["2232", "E", "BedBathUS", "223232", "j2121", "js232", "jdaClass", "vendorId", "0"], BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, couponRepositoryMock) >> [repositoryItem1]

		when:
		def booleanResult = testObjCatalogTools.isSkuExcluded(skuId, promotionCode, false)

		then:
		booleanResult == true
	}
	def "isSkuExcluded. This TC is when couponRuleVendorDeptClassQuery query returns empty item"() {

		given:
		String skuId = "223232"
		String promotionCode = "2232"
		def RepositoryItem jdaDeptItem = Mock()
		def RepositoryItem jdaSubDeptItem = Mock()
		def RepositoryItemMock repositoryItem1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [repositoryItem1]
		catalogRepositoryMock.getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) >> "vendorId"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> jdaDeptItem
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> jdaSubDeptItem
		jdaDeptItem.getRepositoryId() >> "j2121"
		jdaSubDeptItem.getRepositoryId() >> "js232"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) >> "jdaClass"
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.isLogCouponDetails() >> {return false}
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getCouponRuleSkuQuery(),["2232", "E", "BedBathUS", "223232", "j2121", "js232", "jdaClass", "vendorId", "0"],BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, couponRepositoryMock) >> []
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getCouponRuleVendorDeptQuery(),["2232", "E", "BedBathUS", "223232", "j2121", "js232", "jdaClass", "vendorId", "0"], BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, couponRepositoryMock) >> []
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getCouponRuleVendorDeptClassQuery(),["2232", "E", "BedBathUS", "223232", "j2121", "js232", "jdaClass", "vendorId", "0"], BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, couponRepositoryMock) >> []

		when:
		def booleanResult = testObjCatalogTools.isSkuExcluded(skuId, promotionCode, true)

		then:
		booleanResult == false
	}
	def "isSkuExcluded. This TC is when couponRuleVendorDeptClassQuery query returns no item"() {

		given:
		String skuId = "223232"
		String promotionCode = "2232"
		def RepositoryItem jdaDeptItem = Mock()
		def RepositoryItem jdaSubDeptItem = Mock()
		def RepositoryItemMock repositoryItem1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [repositoryItem1]
		catalogRepositoryMock.getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) >> "vendorId"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> null
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >> null
		jdaDeptItem.getRepositoryId() >> "j2121"
		jdaSubDeptItem.getRepositoryId() >> "js232"
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) >> "jdaClass"
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.isLogCouponDetails() >> {return false}
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getCouponRuleSkuQuery(),_,BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, couponRepositoryMock) >> []
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getCouponRuleVendorDeptQuery(),_, BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, couponRepositoryMock) >> []
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getCouponRuleVendorDeptClassQuery(),_, BBBCatalogConstants.COUPON_RULES_ITEM_DESCRIPTOR, couponRepositoryMock) >> null

		when:
		def booleanResult = testObjCatalogTools.isSkuExcluded(skuId, promotionCode, true)

		then:
		booleanResult == false
	}


	///////////////////////////////////TCs for isSkuExcluded ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getThirdPartyTagStatus Starts ///////////////////////////////////////////////////////////////
	//Signature : public String getThirdPartyTagStatus(final String currentSiteId, final BBBCatalogTools catalogTools,final String name) //////////////

	def "getThirdPartyTagStatus. This TC is the happy flow for getThirdPartyTagStatus"() {

		given:
		String currentSiteId = "BedBathUS"
		String name = "en"
		def BBBCatalogTools bbbCatalogToolsMock = Mock()

		bbbCatalogToolsMock.getContentCatalogConfigration(BBBCatalogConstants.BED_BATH_US_SITE_CODE) >> ["BedBathUS"]
		bbbCatalogToolsMock.getContentCatalogConfigration(BBBCatalogConstants.BUY_BUY_BABY_SITE_CODE) >> null
		bbbCatalogToolsMock.getContentCatalogConfigration("en_us") >> ["true"]

		when:
		def tagStatus = testObjCatalogTools.getThirdPartyTagStatus(currentSiteId, bbbCatalogToolsMock, name)

		then:
		tagStatus.toString() == "true"
	}

	def "getThirdPartyTagStatus. This TC is when currentSiteId is BuyBuyBaby"() {

		given:
		String currentSiteId = "BuyBuyBaby"
		String name = "en"
		def BBBCatalogTools bbbCatalogToolsMock = Mock()

		bbbCatalogToolsMock.getContentCatalogConfigration(BBBCatalogConstants.BED_BATH_US_SITE_CODE) >> null
		bbbCatalogToolsMock.getContentCatalogConfigration(BBBCatalogConstants.BUY_BUY_BABY_SITE_CODE) >> ["BuyBuyBaby"]
		bbbCatalogToolsMock.getContentCatalogConfigration("en_baby") >> null

		when:
		def tagStatus = testObjCatalogTools.getThirdPartyTagStatus(currentSiteId, bbbCatalogToolsMock, name)

		then:
		tagStatus.toString() == "true"
	}

	def "getThirdPartyTagStatus. This TC is when currentSiteId is BuyBuyBabyCanada"() {

		given:
		String currentSiteId = "BuyBuyBabyCanada"
		String name = "en"
		def BBBCatalogTools bbbCatalogToolsMock = Mock()

		bbbCatalogToolsMock.getContentCatalogConfigration(BBBCatalogConstants.BED_BATH_US_SITE_CODE) >> []
		bbbCatalogToolsMock.getContentCatalogConfigration(BBBCatalogConstants.BUY_BUY_BABY_SITE_CODE) >> null
		bbbCatalogToolsMock.getContentCatalogConfigration("en_ca") >> []

		when:
		def tagStatus = testObjCatalogTools.getThirdPartyTagStatus(currentSiteId, bbbCatalogToolsMock, name)

		then:
		tagStatus.toString() == "true"
	}

	def "getThirdPartyTagStatus. This TC is when currentSiteId is BuyBuyBabyCanada with tagstatus false"() {

		given:
		String currentSiteId = "BuyBuyBabyCanada"
		String name = "en"
		def BBBCatalogTools bbbCatalogToolsMock = Mock()

		bbbCatalogToolsMock.getContentCatalogConfigration(BBBCatalogConstants.BED_BATH_US_SITE_CODE) >> []
		bbbCatalogToolsMock.getContentCatalogConfigration(BBBCatalogConstants.BUY_BUY_BABY_SITE_CODE) >> []
		bbbCatalogToolsMock.getContentCatalogConfigration("en_ca") >> ["false"]

		when:
		def tagStatus = testObjCatalogTools.getThirdPartyTagStatus(currentSiteId, bbbCatalogToolsMock, name)

		then:
		tagStatus.toString() == "false"
	}

	///////////////////////////////////TCs for getThirdPartyTagStatus ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getContentCatalogConfigration Starts ///////////////////////////////////////////////////////////////
	//Signature :  public final List<String> getContentCatalogConfigration(final String key) //////////////

	def "getContentCatalogConfigration. This TC is the happy flow of getContentCatalogConfigration"() {

		given:
		String key = "bedbathUS"
		testObjCatalogTools.getConfigType() >> "configType"
		testObjCatalogTools.getAllValuesForKey(testObjCatalogTools.getConfigType(), key) >> ["true"]
		when:
		List<String> configrationValue = testObjCatalogTools.getContentCatalogConfigration(key)

		then:
		configrationValue == ["true"]
	}

	def "getContentCatalogConfigration. This TC is when the key is not defined"() {

		given:
		String key = null
		when:
		List<String> configrationValue = testObjCatalogTools.getContentCatalogConfigration(key)

		then:
		configrationValue == []
	}

	def "getContentCatalogConfigration. This TC is when the key is empty"() {

		given:
		String key = ""
		when:
		List<String> configrationValue = testObjCatalogTools.getContentCatalogConfigration(key)

		then:
		configrationValue == []
	}

	///////////////////////////////////TCs for getContentCatalogConfigration ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getRootCollegeIdFrmConfig Starts ///////////////////////////////////////////////////////////////
	//Signature :  public final String getRootCollegeIdFrmConfig(final String pSiteId)  //////////////

	def "getRootCollegeIdFrmConfig. This TC is the happy flow of getRootCollegeIdFrmConfig"() {

		given:
		String siteId = "BedBathUS"
		testObjCatalogTools.getConfigType() >> "configType"
		testObjCatalogTools.getCollegeConfigKey() >> "configkey"
		testObjCatalogTools.getAllValuesForKey(testObjCatalogTools.getConfigType(), testObjCatalogTools.getCollegeConfigKey()) >> ["true"]

		when:
		def collegeList = testObjCatalogTools.getRootCollegeIdFrmConfig(siteId)

		then:
		collegeList == "true"
	}

	def "getRootCollegeIdFrmConfig. This TC is when the siteId parameter is TBS_BedBathUS"() {

		given:
		String siteId = "TBS_BedBathUS"
		testObjCatalogTools.getConfigType() >> "configType"
		testObjCatalogTools.getCollegeConfigKey() >> "configkey"
		testObjCatalogTools.getAllValuesForKey(testObjCatalogTools.getConfigType(), testObjCatalogTools.getCollegeConfigKey()) >> ["true"]

		when:
		def collegeList = testObjCatalogTools.getRootCollegeIdFrmConfig(siteId)

		then:
		collegeList == "true"
	}

	def "getRootCollegeIdFrmConfig. This TC is when the siteId parameter is BuyBuyBaby"() {

		given:
		String siteId = "BuyBuyBaby"
		testObjCatalogTools.getConfigType() >> "configType"
		testObjCatalogTools.getCollegeConfigKeyCanada() >> "configkey"
		testObjCatalogTools.getAllValuesForKey(testObjCatalogTools.getConfigType(), testObjCatalogTools.getCollegeConfigKeyCanada()) >> ["true"]

		when:
		def collegeList = testObjCatalogTools.getRootCollegeIdFrmConfig(siteId)

		then:
		collegeList == "true"
	}

	def "getRootCollegeIdFrmConfig. This TC is when the collegeList is not returned"() {

		given:
		String siteId = "BuyBuyBaby"
		testObjCatalogTools.getConfigType() >> "configType"
		testObjCatalogTools.getCollegeConfigKeyCanada() >> "configkey"
		testObjCatalogTools.getAllValuesForKey(testObjCatalogTools.getConfigType(), testObjCatalogTools.getCollegeConfigKeyCanada()) >> null

		when:
		def collegeList = testObjCatalogTools.getRootCollegeIdFrmConfig(siteId)

		then:
		collegeList == null
		thrown(BBBSystemException)
	}

	def "getRootCollegeIdFrmConfig. This TC is when the collegeList is returned empty"() {

		given:
		String siteId = "BuyBuyBaby"
		testObjCatalogTools.getConfigType() >> "configType"
		testObjCatalogTools.getCollegeConfigKeyCanada() >> "configkey"
		testObjCatalogTools.getAllValuesForKey(testObjCatalogTools.getConfigType(), testObjCatalogTools.getCollegeConfigKeyCanada()) >> []

		when:
		def collegeList = testObjCatalogTools.getRootCollegeIdFrmConfig(siteId)

		then:
		collegeList == null
		thrown(BBBSystemException)
	}

	def "getRootCollegeIdFrmConfig. This TC is when the collegeList thrown exception"() {

		given:
		String siteId = "BuyBuyBaby"
		testObjCatalogTools.getConfigType() >> "configType"
		testObjCatalogTools.getCollegeConfigKeyCanada() >> "configkey"
		testObjCatalogTools.getAllValuesForKey(testObjCatalogTools.getConfigType(), testObjCatalogTools.getCollegeConfigKeyCanada()) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}

		when:
		def collegeList = testObjCatalogTools.getRootCollegeIdFrmConfig(siteId)

		then:
		collegeList == null
		thrown (BBBSystemException)
	}

	///////////////////////////////////TCs for getRootCollegeIdFrmConfig ends ///////////////////////////////////////////////////////////////

	///////////////////////////////////TCs for getParentProductItemForSku Starts ///////////////////////////////////////////////////////////////
	//Signature :  public  RepositoryItem getParentProductItemForSku(final String skuId)  //////////////

	def "getParentProductItemForSku. This TC is the happy flow for getParentProductItemForSku"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId.trim(),	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> [repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >> "32325"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> false

		when:
		RepositoryItem parentProductItem = testObjCatalogTools.getParentProductItemForSku(skuId)

		then:
		parentProductItem == repositoryItemMock
	}

	def "getParentProductItemForSku. This TC is when isProductActive method returns false"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId.trim(),	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> [repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >> "32325"

		when:
		RepositoryItem parentProductItem = testObjCatalogTools.getParentProductItemForSku(skuId)

		then:
		parentProductItem == null
	}

	def "getParentProductItemForSku. This TC is when parentProducts property returns no Item"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId.trim(),	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> null

		when:
		RepositoryItem parentProductItem = testObjCatalogTools.getParentProductItemForSku(skuId)

		then:
		parentProductItem == null
	}

	def "getParentProductItemForSku. This TC is when parentProducts property returns empty"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId.trim(),	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> productRepositoryItemMock
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> [].toSet()

		when:
		RepositoryItem parentProductItem = testObjCatalogTools.getParentProductItemForSku(skuId)

		then:
		parentProductItem == null
	}

	def "getParentProductItemForSku. This TC is when repositoryItem is not defined"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId.trim(),	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null

		when:
		RepositoryItem parentProductItem = testObjCatalogTools.getParentProductItemForSku(skuId)

		then:
		parentProductItem == null
		thrown(BBBBusinessException)
	}

	def "getParentProductItemForSku. This TC is when repositoryItem throws exception"() {

		given:
		String skuId = "232323"
		catalogRepositoryMock.getItem(skuId.trim(),	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for RepositoryException")}

		when:
		RepositoryItem parentProductItem = testObjCatalogTools.getParentProductItemForSku(skuId)

		then:
		parentProductItem == null
		thrown(BBBSystemException)
	}

	def "getParentProductItemForSku. This TC is when skuId is empty"() {

		given:
		String skuId = ""

		when:
		RepositoryItem parentProductItem = testObjCatalogTools.getParentProductItemForSku(skuId)

		then:
		parentProductItem == null
		thrown(BBBBusinessException)
	}

	def "getParentProductItemForSku. This TC is when skuId is not defined"() {

		given:
		String skuId = null

		when:
		RepositoryItem parentProductItem = testObjCatalogTools.getParentProductItemForSku(skuId)

		then:
		parentProductItem == null
		thrown(BBBBusinessException)
	}

	///////////////////////////////////TCs for getParentProductItemForSku ends ///////////////////////////////////////////////////////////////
	///////////////////////////////////TCs for isPhantomCategory starts ///////////////////////////////////////////////////////////////
	def"isPhantomCategory,This Tc for Repository item is  phantomCategory or not"(){
		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> null
		categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> false
		catalogRepositoryMock.getItem("pCategoryId", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> categoryRepositoryItem
		categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY) >> true
		when:
		boolean valid = testObjCatalogTools.isPhantomCategory("pCategoryId")
		then:
		valid
	}
	def"isPhantomCategory,This Tc for Repository item is  phantomCategory is null"(){
		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> null
		categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> false
		catalogRepositoryMock.getItem("pCategoryId", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> categoryRepositoryItem
		categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY) >> null
		when:
		boolean valid = testObjCatalogTools.isPhantomCategory("pCategoryId")
		then:
		valid == false
	}
	def"isPhantomCategory,This Tc for RepositoryException"(){
		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		catalogRepositoryMock.getItem("pCategoryId", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		boolean valid = testObjCatalogTools.isPhantomCategory("pCategoryId")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:Error occured while retrieving  data from repository")
	}
	def"isPhantomCategory,This Tc for  Repository item is null"(){
		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		catalogRepositoryMock.getItem("pCategoryId", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> null
		when:
		boolean valid = testObjCatalogTools.isPhantomCategory("pCategoryId")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1002:1002")
	}
	def"isPhantomCategory,This Tc for Repository item is not a cateogry"(){
		given:
		globalRepositoryToolsMock.setPreviewEnabled(true)
		categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
		catalogRepositoryMock.getItem("pCategoryId", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> categoryRepositoryItem
		categoryRepositoryItem.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY) >> true
		when:
		boolean valid = testObjCatalogTools.isPhantomCategory("pCategoryId")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1002:1002")
	}
	///////////////////////////////////TCs for isPhantomCategory ends ///////////////////////////////////////////////////////////////
	///////////////////////////////////TCs for getClearanceProducts starts ///////////////////////////////////////////////////////////////
	def" Method to return clearance product list vo's for given site id and category id"(){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		CategoryVO categoryVO = Mock()
		CategoryVO categoryVO1 = Mock()
		CategoryVO categoryVO2 = Mock()
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> categoryVO
		categoryVO.getSubCategories() >> [categoryVO1,categoryVO2]
		categoryVO1.getSubCategories() >> null
		categoryVO2.getChildProducts()>> ["prd1234","prd1234"]
		ProductVO pVO = Mock()
		testObjCatalogTools.getProductDetails("BedBathUS", "prd1234", true) >> pVO
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceProducts("BedBathUS", "pCategoryId")
		then:
		productVOList.size() == 2
	}
	def" Method to return clearance product list vo's for given site id and category id throw business exception"(){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> {throw new BBBBusinessException("Mock of Business exception")}
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceProducts("BedBathUS", "pCategoryId")
		then:
		productVOList.size() == 0
	}
	def" Method to return clearance product list vo's for given site id and category id throw system exception"(){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> {throw new BBBSystemException("Mock of system exception")}
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceProducts("BedBathUS", "pCategoryId")
		then:
		productVOList.size() == 0
	}
	def" Method to return clearance product list vo's for given site id and category id,child products is null" (){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		CategoryVO categoryVO = Mock()
		CategoryVO categoryVO1 = Mock()
		CategoryVO categoryVO2 = Mock()
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> categoryVO
		categoryVO.getSubCategories() >> [categoryVO1]
		categoryVO1.getSubCategories() >> null
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceProducts("BedBathUS", "pCategoryId")
		then:
		productVOList.size() == 0
	}
	def" Method to return clearance product list vo's for given site id and category id,subCategory is null" (){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		CategoryVO categoryVO = Mock()
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> categoryVO
		categoryVO.getSubCategories() >> []
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceProducts("BedBathUS", "pCategoryId")
		then:
		productVOList.size() == 0
	}
	def" Method to return clearance product list vo's for given site id and category id,getCategoryDetail is null" (){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> null
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceProducts("BedBathUS", "pCategoryId")
		then:
		productVOList.size() == 0
	}
	def" Method to return clearance product list vo's for given site id and category id single child product vo is null"(){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		CategoryVO categoryVO = Mock()
		CategoryVO categoryVO1 = Mock()
		CategoryVO categoryVO2 = Mock()
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> categoryVO
		categoryVO.getSubCategories() >> [categoryVO1,categoryVO2]
		categoryVO1.getChildProducts() >> ["prd1235"]
		categoryVO2.getChildProducts()>> ["prd1234","prd1234"]
		ProductVO pVO = Mock()
		testObjCatalogTools.getProductDetails("BedBathUS", "prd1234", true) >> null
		testObjCatalogTools.getProductDetails("BedBathUS", "prd1235", true) >> pVO
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceProducts("BedBathUS", "pCategoryId")
		then:
		productVOList.size() == 1
	}
	def" Method to return clearance product list vo's for given site id and category id  getchild products is empty"(){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		CategoryVO categoryVO = Mock()
		CategoryVO categoryVO1 = Mock()
		CategoryVO categoryVO2 = Mock()
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> categoryVO
		categoryVO.getSubCategories() >> [categoryVO1,categoryVO2]
		categoryVO1.getChildProducts() >> []
		categoryVO2.getChildProducts()>> ["prd1234"]
		ProductVO pVO = Mock()
		testObjCatalogTools.getProductDetails("BedBathUS", "prd1234", true) >> {throw new BBBBusinessException("Mock of business exception")}
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceProducts("BedBathUS", "pCategoryId")
		then:
		productVOList.size() == 0
	}
	///////////////////////////////////TCs for getClearanceProducts ends ///////////////////////////////////////////////////////////////
	///////////////////////////////////TCs for getGiftProducts starts ///////////////////////////////////////////////////////////////
	def"The method returns a list of ProductVO for products whose gift cert flag is true if no product is available"(){
		given:
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getGiftCertProductQuery(),
				BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR, testObjCatalogTools.getCatalogRepository()) >>  [repositoryItemMock,repositoryItemMock]
		testObjCatalogTools.isProductActive(repositoryItemMock) >> false >> true
		ProductVO pVO = Mock()
		testObjCatalogTools.getProductDetails("BedBathUS", _) >> pVO
		when:
		List<ProductVO> productvo = testObjCatalogTools.getGiftProducts("BedBathUS")
		then:
		productvo.size() == 1
	}
	def"The method returns a list of ProductVO for products whose gift cert flag is true if no product is available site id is null"(){
		when:
		List<ProductVO> productvo = testObjCatalogTools.getGiftProducts(null)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"The method returns a list of ProductVO for products whose gift cert flag is true if no product is available ,no repository items"(){
		given:
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getGiftCertProductQuery(),
				BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR, testObjCatalogTools.getCatalogRepository()) >>  []
		when:
		List<ProductVO> productvo = testObjCatalogTools.getGiftProducts("BedBathUS")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("3003:3003")
	}
	def"The method returns a list of ProductVO for products whose gift cert flag is true if no product is available , repository items is null"(){
		given:
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getGiftCertProductQuery(),
				BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR, testObjCatalogTools.getCatalogRepository()) >>  null
		when:
		List<ProductVO> productvo = testObjCatalogTools.getGiftProducts("BedBathUS")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("3003:3003")
	}
	def"The method returns a list of ProductVO for products whose gift cert flag is true if no product is available , throw repository exception"(){
		given:
		testObjCatalogTools.executeRQLQuery(testObjCatalogTools.getGiftCertProductQuery(),
				BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR, testObjCatalogTools.getCatalogRepository()) >>  {throw new RepositoryException()}
		when:
		List<ProductVO> productvo = testObjCatalogTools.getGiftProducts("BedBathUS")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	///////////////////////////////////TCs for getGiftProducts ends ///////////////////////////////////////////////////////////////
	///////////////////////////////////TCs for getCompareProductDetail starts ///////////////////////////////////////////////////////////////

	def"This method is used to populate the ProductVO for all types of products and the SKU VO"(){
		given:
		ProductVO pVO = new ProductVO()
		CompareProductEntryVO cpeVO =new  CompareProductEntryVO()
		cpeVO.setProductId("prd1234")
		cpeVO.setSkuId("sku1234")
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getComparisonProductDetails("prd1234", "BedBathUS", cpeVO) >> pVO
		pVO.setLowPrice(9.5)
		pVO.setIntlRestricted(false)
		pVO.setDisabled(false)
		BazaarVoiceProductVO bPvo= Mock()
		pVO.setBvReviews(bPvo)
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> ["true"]
		1*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, _, null,_) >> "9.21"
		1*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, _) >> "txt_freeshipping_product"
		2*catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepositoryItemMock
		SKUDetailVO skuVO = new SKUDetailVO()
		testObjCatalogTools.getSKUDetails("BedBathUS", "sku1234") >> skuVO
		testObjCatalogTools.updateShippingMessageFlag(skuVO, false, 0.0) >> {}
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_CATEGORY_PROPERTY_NAME) >> BBBCatalogConstants.DESCRIPTION_CATEGORY_PROPERTY_NAME
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
		skuVO.setDisplayShipMsg("DisplayShipMsg")
		skuVO.setShipMsgFlag(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> true
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) >> "swatchImage"
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME) >> "color"
		bbbInventoryManagerMock.getProductAvailability("BedBathUS", "sku1234", BBBInventoryManager.PRODUCT_DISPLAY, 0)>>0
		testObjCatalogTools.getComparisonSKUDetails("BedBathUS", "sku1234", false, cpeVO) >> skuVO
		skuVO.setBopusAllowed(true)
		skuVO.setGiftWrapEligible(true)
		1*catalogRepositoryMock.getItem("prd1234",	BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> priceListRepositoryItemMock
		priceListRepositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY) >> BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY
		2*vendorRepository.getView(BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR) >>  repositoryViewMock
		2*repositoryViewMock.getQueryBuilder() >> qBuilder
		2*qBuilder.createComparisonQuery(qExp, qExp, 4) >> query
		2*qBuilder.createPropertyQueryExpression("customizationVendorId") >> qExp
		2*qBuilder.createConstantQueryExpression("vendorId") >> qExp
		when:
		testObjCatalogTools.getCompareProductDetail(cpeVO)
		then:
		cpeVO.isIntlRestricted() == false
		cpeVO.getDisplayShipMsg().equals("DisplayShipMsg")
		cpeVO.isShipMsgFlag()
		cpeVO.getShortDescription().equals(BBBCatalogConstants.DESCRIPTION_CATEGORY_PROPERTY_NAME)
		cpeVO.getMediumImagePath().equals(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)
		cpeVO.getThumbnailImagePath().equals(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME)
		cpeVO.getProductName().equals(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)
		cpeVO.isLtlProduct()
		cpeVO.getColor().get("swatchImage").equals("color")
		cpeVO.isInCartFlag()==false
		cpeVO.isBopusExcluded() == false
		cpeVO.getSkuGiftWrapEligible().equals(BBBCoreConstants.AVAILABLE)
		cpeVO.getVendorInfoVO() != null
		cpeVO.getFreeStandardShipping().equals(BBBCoreConstants.COMPARE_PRODUCT_NO)
		cpeVO.getClearance().equals(BBBCoreConstants.COMPARE_PRODUCT_NO)
		cpeVO.isInStock()

	}
	def"This method is used to populate the ProductVO for all types of products and the SKU VO,product is collection"(){
		given:
		ProductVO pVO = new ProductVO()
		CompareProductEntryVO cpeVO =new  CompareProductEntryVO()
		cpeVO.setProductId("prd1234")
		cpeVO.setSkuId("sku1234")
		AttributeVO atrVO =Mock()
		cpeVO.setAttributesList(["attributi":atrVO,"attribute":atrVO])
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getComparisonProductDetails("prd1234", "BedBathUS", cpeVO) >> pVO
		pVO.setLowPrice(9.5)
		pVO.setIntlRestricted(false)
		pVO.setDisabled(false)
		BazaarVoiceProductVO bPvo= Mock()
		pVO.setBvReviews(bPvo)
		pVO.setCollection(true)
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> ["false"]
		0*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, _, null,_) >> "9.21"
		0*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, _) >> "txt_freeshipping_product"
		1*catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepositoryItemMock
		SKUDetailVO skuVO = new SKUDetailVO()
		testObjCatalogTools.getSKUDetails("BedBathUS", "sku1234") >> skuVO
		testObjCatalogTools.updateShippingMessageFlag(skuVO, false, 0.0) >> {}
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_CATEGORY_PROPERTY_NAME) >> BBBCatalogConstants.DESCRIPTION_CATEGORY_PROPERTY_NAME
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
		skuVO.setDisplayShipMsg("DisplayShipMsg")
		skuVO.setShipMsgFlag(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) >> "swatchImage"
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME) >> "color"
		bbbInventoryManagerMock.getProductAvailability("BedBathUS", "sku1234", BBBInventoryManager.PRODUCT_DISPLAY, 0)>>{throw new BBBSystemException("Mock of system exception")}
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.CLEARANCE_ATTRIBUTE) >>["attr"]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.FREE_STANDARD_SHIPPING)>>["attr"]
		testObjCatalogTools.getComparisonSKUDetails("BedBathUS", "sku1234", false, cpeVO) >> skuVO
		skuVO.setBopusAllowed(true)
		skuVO.setGiftWrapEligible(true)
		1*catalogRepositoryMock.getItem("prd1234",	BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> priceListRepositoryItemMock
		priceListRepositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY) >> BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY
		2*vendorRepository.getView(BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR) >>  repositoryViewMock
		2*repositoryViewMock.getQueryBuilder() >> qBuilder
		2*qBuilder.createComparisonQuery(qExp, qExp, 4) >> query
		2*qBuilder.createPropertyQueryExpression("customizationVendorId") >> qExp
		2*qBuilder.createConstantQueryExpression("vendorId") >> qExp
		when:
		testObjCatalogTools.getCompareProductDetail(cpeVO)
		then:
		cpeVO.isIntlRestricted() == false
		cpeVO.getDisplayShipMsg().equals(null)
		cpeVO.isShipMsgFlag() == false
		cpeVO.getShortDescription().equals(BBBCatalogConstants.DESCRIPTION_CATEGORY_PROPERTY_NAME)
		cpeVO.getMediumImagePath().equals(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)
		cpeVO.getThumbnailImagePath().equals(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME)
		cpeVO.getProductName().equals(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)
		cpeVO.isLtlProduct() == false
		cpeVO.isInCartFlag()==false
		cpeVO.isBopusExcluded() == false
		cpeVO.getSkuGiftWrapEligible().equals(BBBCoreConstants.AVAILABLE)
		cpeVO.getVendorInfoVO() != null
		cpeVO.getFreeStandardShipping().equals(BBBCoreConstants.COMPARE_PRODUCT_NO)
		cpeVO.getClearance().equals(BBBCoreConstants.COMPARE_PRODUCT_NO)
		cpeVO.getAttributesList().size()==2
		cpeVO.isInStock() == false

	}
	def"This method is used to populate the ProductVO for all types of products and the SKU VO,sku details is null"(){
		given:
		ProductVO pVO = new ProductVO()
		CompareProductEntryVO cpeVO =new  CompareProductEntryVO()
		cpeVO.setProductId("prd1234")
		cpeVO.setSkuId("sku1234")
		cpeVO.setMultiSku(true)
		AttributeVO atrVO =Mock()
		cpeVO.setAttributesList(["attributi":atrVO,"attribute":atrVO])
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getComparisonProductDetails("prd1234", "BedBathUS", cpeVO) >> pVO
		pVO.setLowPrice(9.5)
		pVO.setIntlRestricted(false)
		pVO.setDisabled(false)
		BazaarVoiceProductVO bPvo= Mock()
		pVO.setBvReviews(bPvo)
		pVO.setCollection(false)
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> ["true"]
		1*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, _, null,_) >> "9.21"
		1*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, _) >> "txt_freeshipping_product"
		2*catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepositoryItemMock
		SKUDetailVO skuVO = new SKUDetailVO()
		testObjCatalogTools.getSKUDetails("BedBathUS", "sku1234") >> null
		testObjCatalogTools.updateShippingMessageFlag(skuVO, false, 0.0) >> {}
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_CATEGORY_PROPERTY_NAME) >> BBBCatalogConstants.DESCRIPTION_CATEGORY_PROPERTY_NAME
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
		skuVO.setDisplayShipMsg("DisplayShipMsg")
		skuVO.setShipMsgFlag(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) >> "swatchImage"
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME) >> "color"
		bbbInventoryManagerMock.getProductAvailability("BedBathUS", "sku1234", BBBInventoryManager.PRODUCT_DISPLAY, 0)>>{throw new BBBSystemException("Mock of system exception")}
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.CLEARANCE_ATTRIBUTE) >>["attr"]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.FREE_STANDARD_SHIPPING)>>["attr"]
		testObjCatalogTools.getComparisonSKUDetails("BedBathUS", "sku1234", false, cpeVO) >> skuVO
		skuVO.setBopusAllowed(true)
		skuVO.setGiftWrapEligible(true)
		1*catalogRepositoryMock.getItem("prd1234",	BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> priceListRepositoryItemMock
		priceListRepositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY) >> BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY
		2*vendorRepository.getView(BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR) >>  repositoryViewMock
		2*repositoryViewMock.getQueryBuilder() >> qBuilder
		2*qBuilder.createComparisonQuery(qExp, qExp, 4) >> query
		2*qBuilder.createPropertyQueryExpression("customizationVendorId") >> qExp
		2*qBuilder.createConstantQueryExpression("vendorId") >> qExp
		when:
		testObjCatalogTools.getCompareProductDetail(cpeVO)
		then:
		cpeVO.isIntlRestricted() == false
		cpeVO.getDisplayShipMsg().equals(null)
		cpeVO.isShipMsgFlag() == false
		cpeVO.getShortDescription().equals(BBBCatalogConstants.DESCRIPTION_CATEGORY_PROPERTY_NAME)
		cpeVO.getMediumImagePath().equals(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)
		cpeVO.getThumbnailImagePath().equals(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME)
		cpeVO.getProductName().equals(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)
		cpeVO.isLtlProduct() == false
		cpeVO.isInCartFlag()==false
		cpeVO.isBopusExcluded() == false
		cpeVO.getSkuGiftWrapEligible().equals(BBBCoreConstants.AVAILABLE)
		cpeVO.getVendorInfoVO() != null
		cpeVO.getFreeStandardShipping().equals(BBBCoreConstants.COMPARE_PRODUCT_NO)
		cpeVO.getClearance().equals(BBBCoreConstants.COMPARE_PRODUCT_NO)
		cpeVO.getAttributesList().size()==2
		cpeVO.isInStock() == false

	}
	def"This method is used to populate the ProductVO for all types of products and the SKU VO throw exception"(){
		given:
		ProductVO pVO = new ProductVO()
		CompareProductEntryVO cpeVO =new  CompareProductEntryVO()
		cpeVO.setProductId("prd1234")
		cpeVO.setSkuId("sku1234")
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getComparisonProductDetails("prd1234", "BedBathUS", cpeVO) >> pVO
		pVO.setLowPrice(6.2)
		pVO.setHighPrice(10.2)
		pVO.setIntlRestricted(false)
		pVO.setDisabled(false)
		BazaarVoiceProductVO bPvo= Mock()
		pVO.setBvReviews(bPvo)
		cpeVO.setCollection(false)
		BBBConfigRepoUtils.setBbbCatalogTools(catalogTools)
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> {throw new BBBBusinessException("Mock of Business exception")}
		0*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, _, null,_) >> "9.21"
		0*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, _) >> "txt_freeshipping_product"
		2*catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null
		SKUDetailVO skuVO = new SKUDetailVO()
		testObjCatalogTools.getSKUDetails("BedBathUS", "sku1234") >> skuVO
		testObjCatalogTools.updateShippingMessageFlag(skuVO, false, 0.0) >> {}
		skuVO.setDisplayShipMsg("DisplayShipMsg")
		skuVO.setShipMsgFlag(true)
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> true
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) >> "swatchImage"
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME) >> "color"
		1*bbbInventoryManagerMock.getProductAvailability("BedBathUS", "sku1234", BBBInventoryManager.PRODUCT_DISPLAY, 0)>>1
		testObjCatalogTools.getComparisonSKUDetails("BedBathUS", "sku1234", false, cpeVO) >> skuVO
		skuVO.setBopusAllowed(false)
		skuVO.setGiftWrapEligible(false)
		skuVO.setSkuBelowLine(true)
		1*catalogRepositoryMock.getItem("prd1234",	BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> priceListRepositoryItemMock
		priceListRepositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY) >> BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY
		2*vendorRepository.getView(BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR) >>  repositoryViewMock
		2*repositoryViewMock.getQueryBuilder() >> qBuilder
		2*qBuilder.createComparisonQuery(qExp, qExp, 4) >> query
		2*qBuilder.createPropertyQueryExpression("customizationVendorId") >> qExp
		2*qBuilder.createConstantQueryExpression("vendorId") >> qExp
		Map<String,AttributeVO> attrlist =new HashMap<String,AttributeVO>()
		attrlist.put("1",new AttributeVO())
		attrlist.put("3",new AttributeVO())
		cpeVO.setAttributesList(attrlist)
		catalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.CLEARANCE_ATTRIBUTE) >> []
		when:
		testObjCatalogTools.getCompareProductDetail(cpeVO)
		then:
		cpeVO.isIntlRestricted() == false
		cpeVO.getDisplayShipMsg() == null
		cpeVO.isShipMsgFlag() == false
		cpeVO.getShortDescription()==null
		cpeVO.getMediumImagePath()==null
		cpeVO.getThumbnailImagePath()==null
		cpeVO.getProductName()==null
		cpeVO.isLtlProduct() == false
		cpeVO.getColor().isEmpty()
		cpeVO.isInCartFlag()==false
		cpeVO.isBopusExcluded() == false
		cpeVO.getSkuGiftWrapEligible().equals(BBBCoreConstants.NOT_AVAILABLE)
		cpeVO.getVendorInfoVO() != null
		cpeVO.isInStock() == false
		cpeVO.getAttributesList().size()==2
	}
	def"This method is used to populate the ProductVO for all types of products and the SKU VO ,productvo is null"(){
		given:
		ProductVO pVO = new ProductVO()
		CompareProductEntryVO cpeVO =new  CompareProductEntryVO()
		cpeVO.setProductId("prd1234")
		cpeVO.setSkuId("sku1234")
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getComparisonProductDetails("prd1234", "BedBathUS", cpeVO) >> null
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> ["true"]
		when:
		testObjCatalogTools.getCompareProductDetail(cpeVO)
		then:
		Exception ex= thrown()
		ex.getMessage().equals("1004:CompareProductEntryVO is returned as null from BBBCatalogToolsImpl.getProductDetail() method")
	}
	def"This method is used to populate the ProductVO for all types of products and the SKU VO ,throw repository exception"(){
		given:
		ProductVO pVO = new ProductVO()
		CompareProductEntryVO cpeVO =new  CompareProductEntryVO()
		cpeVO.setProductId("prd1234")
		cpeVO.setSkuId("sku1234")
		BazaarVoiceProductVO bPvo= Mock()
		pVO.setBvReviews(bPvo)
		pVO.setDisabled(false)
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getComparisonProductDetails("prd1234", "BedBathUS", cpeVO) >> pVO
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> ["true"]
		1*catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		testObjCatalogTools.getCompareProductDetail(cpeVO)
		then:
		Exception ex= thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"This method is used to populate the ProductVO for all types of products and the SKU VO ,skuid is null"(){
		given:
		ProductVO pVO = new ProductVO()
		CompareProductEntryVO cpeVO =new  CompareProductEntryVO()
		AttributeVO atrVO =Mock()
		cpeVO.setMultiSku(true)
		cpeVO.setAttributesList(["attributi":atrVO,"attribute":atrVO])
		cpeVO.setProductId("prd1234")
		cpeVO.setSkuId(null)
		pVO.setIntlRestricted(true)
		pVO.setDisabled(true)
		ImageVO image =Mock()
		BazaarVoiceProductVO bazeerVO = Mock()
		BBBDynamicPriceVO dynamicPrice =Mock()
		pVO.setDynamicPriceVO(dynamicPrice)
		dynamicPrice.getCountry() >> null
		pVO.setProductImages(image)
		pVO.setName("productName")
		pVO.setCollection(false)
		pVO.setShortDescription("ShortDescription")
		pVO.setLongDescription("LongDescription")
		pVO.setBvReviews(bazeerVO)
		pVO.setPriceRangeDescription("PriceRangeDescription")
		pVO.setSalePriceRangeDescription("SalePriceRangeDescription")
		pVO.setDisplayShipMsg("DisplayShipMsg")
		pVO.setInCartFlag(true)
		pVO.setPriceLabelCode("PriceLabelCode")
		pVO.setShipMsgFlag(true)
		pVO.setDynamicPricingProduct(true)
		pVO.setChildSKUs(["sku1234"])
		priceListRepositoryToolsMock.getSkuIncartFlag("sku1234", false) >> true
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getComparisonProductDetails("prd1234", "BedBathUS", cpeVO) >> pVO
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> ["true"]
		catalogRepositoryMock.getItem("prd1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR)>>repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> [skuRepositoryItemMock,skuRepositoryItemMock,skuRepositoryItemMock]
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) >> null>>BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME) >>null>> BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.CLEARANCE_ATTRIBUTE) >>["attributi"]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.FREE_STANDARD_SHIPPING)>>["attribute"]
		testObjCatalogTools.getComparisonSKUDetails("BedBathUS", "sku1234", false, cpeVO) >> skuDetailVOMock
		bbbInventoryManagerMock.getProductAvailability("BedBathUS", "sku1234", BBBInventoryManager.PRODUCT_DISPLAY, 0) >>{throw new BBBBusinessException("Mock of business exception")}
		vendorRepository.getItem(BBBCatalogConstants.DEFAULT,BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDORS_NAME_PROPERTY) >>BBBCatalogConstants.VENDORS_NAME_PROPERTY
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_JS) >> BBBCatalogConstants.VENDOR_JS
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_JS_MOBILE) >>BBBCatalogConstants.VENDOR_JS_MOBILE
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_API_KEY) >>BBBCatalogConstants.VENDOR_API_KEY
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_API_URL) >>BBBCatalogConstants.VENDOR_API_URL
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_API_VERSION) >> BBBCatalogConstants.VENDOR_API_VERSION
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_BBB_CLIENT_ID)>> BBBCatalogConstants.VENDOR_BBB_CLIENT_ID
		when:
		testObjCatalogTools.getCompareProductDetail(cpeVO)
		then:
		cpeVO != null
		cpeVO.getClearance().equals(BBBCoreConstants.COMPARE_PRODUCT_YES)
		cpeVO.getFreeStandardShipping().equals(BBBCoreConstants.COMPARE_PRODUCT_YES)
		cpeVO.isInStock() == false
		cpeVO.getSkuGiftWrapEligible().equals(BBBCoreConstants.NOT_AVAILABLE)
		cpeVO.isProductActive()
		cpeVO.getMediumImagePath().equals(pVO.getProductImages().getMediumImage())
		cpeVO.getThumbnailImagePath().equals(pVO.getProductImages().getThumbnailImage())
		cpeVO.getProductName().equals(pVO.getName())
		cpeVO.isCollection().equals(pVO.isCollection())
		cpeVO.getShortDescription().equals(pVO.getShortDescription())
		cpeVO.getLongDescription().equals(pVO.getLongDescription())
		cpeVO.getReviews().equals(pVO.getBvReviews())
		cpeVO.getPriceRangeDescription().equals(pVO.getPriceRangeDescription())
		cpeVO.getSalePriceRangeDescription().equals(pVO.getSalePriceRangeDescription())
		cpeVO.getDisplayShipMsg().equals(pVO.getDisplayShipMsg())
		cpeVO.isShipMsgFlag().equals(pVO.isShipMsgFlag())
		cpeVO.getPriceLabelCode().equals(pVO.getPriceLabelCode())
		cpeVO.isDynamicPricingProduct().equals(pVO.isDynamicPricingProduct())
		cpeVO.isInCartFlag()
		cpeVO.getCustomizationCode().equals(BBBCoreConstants.SELECT_OPTIONS);
		cpeVO.getFreeStandardShipping().equals(BBBCoreConstants.COMPARE_PRODUCT_YES);
		cpeVO.getVdcSkuFlag().equals(BBBCoreConstants.SELECT_OPTIONS);
		cpeVO.getSkuGiftWrapEligible().equals(BBBCoreConstants.NOT_AVAILABLE);
		cpeVO.getClearance().equals(BBBCoreConstants.COMPARE_PRODUCT_YES)
		cpeVO.getColor().get(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME).equals(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME)
		cpeVO.getAttributesList().size()==0
		cpeVO.getVendorInfoVO().getClientId().equals(BBBCatalogConstants.VENDOR_BBB_CLIENT_ID)
		cpeVO.getVendorInfoVO().getVendorName().equals(BBBCatalogConstants.VENDORS_NAME_PROPERTY)
		cpeVO.getVendorInfoVO().getApiVersion().equals(BBBCatalogConstants.VENDOR_API_VERSION)
		cpeVO.getVendorInfoVO().getApiKey().equals(BBBCatalogConstants.VENDOR_API_KEY)
		cpeVO.getVendorInfoVO().getApiURL().equals(BBBCatalogConstants.VENDOR_API_URL)
		cpeVO.getVendorInfoVO().getVendorJS().equals(BBBCatalogConstants.VENDOR_JS)
		cpeVO.getVendorInfoVO().getVendorMobileJS().equals(BBBCatalogConstants.VENDOR_JS_MOBILE)

	}
	def"This method is used to populate the ProductVO for all types of products and the SKU VO ,child skus is empty"(){
		given:
		ProductVO pVO = new ProductVO()
		CompareProductEntryVO cpeVO =new  CompareProductEntryVO()
		AttributeVO atrVO =Mock()
		cpeVO.setMultiSku(true)
		cpeVO.setAttributesList(null)
		cpeVO.setProductId("prd1234")
		cpeVO.setSkuId(null)
		pVO.setIntlRestricted(true)
		pVO.setDisabled(true)
		ImageVO image =Mock()
		BazaarVoiceProductVO bazeerVO = Mock()
		BBBDynamicPriceVO dynamicPrice =Mock()
		pVO.setDynamicPriceVO(dynamicPrice)
		dynamicPrice.getCountry() >> null
		pVO.setProductImages(image)
		pVO.setName("productName")
		pVO.setCollection(false)
		pVO.setShortDescription("ShortDescription")
		pVO.setLongDescription("LongDescription")
		pVO.setBvReviews(bazeerVO)
		pVO.setPriceRangeDescription("PriceRangeDescription")
		pVO.setSalePriceRangeDescription("SalePriceRangeDescription")
		pVO.setDisplayShipMsg("DisplayShipMsg")
		pVO.setInCartFlag(true)
		pVO.setPriceLabelCode("PriceLabelCode")
		pVO.setShipMsgFlag(true)
		pVO.setDynamicPricingProduct(true)
		pVO.setChildSKUs(null)
		testObjCatalogTools.getComparisonProductDetails("prd1234", BBBCoreConstants.SITE_BBB, cpeVO) >> pVO
		0*priceListRepositoryToolsMock.getSkuIncartFlag("sku1234", false) >> true
		testObjCatalogTools.getCurrentSiteId() >> BBBCoreConstants.SITE_BBB
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> ["true"]
		catalogRepositoryMock.getItem("prd1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR)>>repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> [skuRepositoryItemMock,skuRepositoryItemMock,skuRepositoryItemMock]
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) >> null>>BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME) >>null>> BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.CLEARANCE_ATTRIBUTE) >>["attributi"]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCoreConstants.FREE_STANDARD_SHIPPING)>>["attribute"]
		testObjCatalogTools.getComparisonSKUDetails(BBBCoreConstants.SITE_BBB, "sku1234", false, cpeVO) >> skuDetailVOMock
		vendorRepository.getItem(BBBCatalogConstants.DEFAULT,BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDORS_NAME_PROPERTY) >>BBBCatalogConstants.VENDORS_NAME_PROPERTY
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_JS) >> BBBCatalogConstants.VENDOR_JS
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_JS_MOBILE) >>BBBCatalogConstants.VENDOR_JS_MOBILE
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_API_KEY) >>BBBCatalogConstants.VENDOR_API_KEY
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_API_URL) >>BBBCatalogConstants.VENDOR_API_URL
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_API_VERSION) >> BBBCatalogConstants.VENDOR_API_VERSION
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_BAB_CLIENT_ID)>> BBBCatalogConstants.VENDOR_BAB_CLIENT_ID
		when:
		testObjCatalogTools.getCompareProductDetail(cpeVO)
		then:
		cpeVO != null
		cpeVO.getClearance().equals(BBBCoreConstants.COMPARE_PRODUCT_NO)
		cpeVO.getFreeStandardShipping().equals(BBBCoreConstants.COMPARE_PRODUCT_NO)
		cpeVO.isInStock() == false
		cpeVO.getSkuGiftWrapEligible().equals(BBBCoreConstants.SELECT_OPTIONS)
		cpeVO.isProductActive()
		cpeVO.getMediumImagePath().equals(pVO.getProductImages().getMediumImage())
		cpeVO.getThumbnailImagePath().equals(pVO.getProductImages().getThumbnailImage())
		cpeVO.getProductName().equals(pVO.getName())
		cpeVO.isCollection().equals(pVO.isCollection())
		cpeVO.getShortDescription().equals(pVO.getShortDescription())
		cpeVO.getLongDescription().equals(pVO.getLongDescription())
		cpeVO.getReviews().equals(pVO.getBvReviews())
		cpeVO.getPriceRangeDescription().equals(pVO.getPriceRangeDescription())
		cpeVO.getSalePriceRangeDescription().equals(pVO.getSalePriceRangeDescription())
		cpeVO.getDisplayShipMsg().equals(pVO.getDisplayShipMsg())
		cpeVO.isShipMsgFlag().equals(pVO.isShipMsgFlag())
		cpeVO.getPriceLabelCode().equals(pVO.getPriceLabelCode())
		cpeVO.isDynamicPricingProduct().equals(pVO.isDynamicPricingProduct())
		cpeVO.isInCartFlag() == false
		cpeVO.getCustomizationCode().equals(BBBCoreConstants.SELECT_OPTIONS);
		cpeVO.getVdcSkuFlag().equals(BBBCoreConstants.SELECT_OPTIONS);
		cpeVO.getSkuGiftWrapEligible().equals(BBBCoreConstants.SELECT_OPTIONS);
		cpeVO.getColor().get(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME).equals(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME)
		cpeVO.getVendorInfoVO().getClientId().equals(BBBCatalogConstants.VENDOR_BAB_CLIENT_ID)
		cpeVO.getVendorInfoVO().getVendorName().equals(BBBCatalogConstants.VENDORS_NAME_PROPERTY)
		cpeVO.getVendorInfoVO().getApiVersion().equals(BBBCatalogConstants.VENDOR_API_VERSION)
		cpeVO.getVendorInfoVO().getApiKey().equals(BBBCatalogConstants.VENDOR_API_KEY)
		cpeVO.getVendorInfoVO().getApiURL().equals(BBBCatalogConstants.VENDOR_API_URL)
		cpeVO.getVendorInfoVO().getVendorJS().equals(BBBCatalogConstants.VENDOR_JS)
		cpeVO.getVendorInfoVO().getVendorMobileJS().equals(BBBCatalogConstants.VENDOR_JS_MOBILE)
	}
	///////////////////////////////////TCs for getCompareProductDetail ends ///////////////////////////////////////////////////////////////
	def"This method gets the bazaar voice key for the current Site from Config Keys repository"(){
		given:
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_BazaarVoiceKey")>> ["BazarVoiceKey"]
		when:
		String value = testObjCatalogTools.getBazaarVoiceKey()
		then:
		value.equals("BazarVoiceKey")
	}
	def"This method gets the bazaar voice key for the current Site from Config Keys repository values from repository is null"(){
		given:
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_BazaarVoiceKey")>> []
		when:
		String value = testObjCatalogTools.getBazaarVoiceKey()
		then:
		value == null
	}
	def"This method gets the bazaar voice key for the current Site from Config Keys repository throw business exception"(){
		given:
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_BazaarVoiceKey")>> {throw new BBBBusinessException("Mock of business exception")}
		when:
		String value = testObjCatalogTools.getBazaarVoiceKey()
		then:
		Exception ex = thrown()
	}
	def"This method gets the bazaar voice key for the current Site from Config Keys repository throw system exception"(){
		given:
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_BazaarVoiceKey")>> {throw new BBBSystemException("Mock of system exception")}
		when:
		String value = testObjCatalogTools.getBazaarVoiceKey()
		then:
		Exception ex = thrown()
	}
	def"Get the Brand Id from Repository"(){
		given:
		testObjCatalogTools.parseRqlStatement("brandName = ?0") >> rqlStatementMock
		catalogRepositoryMock.getView(BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BRANDS_ITEM_ID) >>BBBCatalogConstants.BRANDS_ITEM_ID
		when:
		String value = testObjCatalogTools.getBrandId("brandName")
		then:
		value.equals(BBBCatalogConstants.BRANDS_ITEM_ID)
	}
	def"Get the Brand Id from Repository ,zero items available "(){
		given:
		testObjCatalogTools.parseRqlStatement("brandName = ?0") >> rqlStatementMock
		catalogRepositoryMock.getView(BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> []
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BRANDS_ITEM_ID) >>BBBCatalogConstants.BRANDS_ITEM_ID
		when:
		String value = testObjCatalogTools.getBrandId("brandName")
		then:
		value == null
	}
	def"Get the Brand Id from Repository ,items is null "(){
		given:
		testObjCatalogTools.parseRqlStatement("brandName = ?0") >> rqlStatementMock
		catalogRepositoryMock.getView(BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BRANDS_ITEM_ID) >>BBBCatalogConstants.BRANDS_ITEM_ID
		when:
		String value = testObjCatalogTools.getBrandId("brandName")
		then:
		value == null
	}
	def"Get the Brand Id from Repository ,items is property value is null "(){
		given:
		testObjCatalogTools.parseRqlStatement("brandName = ?0") >> rqlStatementMock
		catalogRepositoryMock.getView(BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BRANDS_ITEM_ID) >> null
		when:
		String value = testObjCatalogTools.getBrandId("brandName")
		then:
		value == null
	}
	def"Get the Brand Id from Repository ,throw RepositoryException "(){
		given:
		testObjCatalogTools.parseRqlStatement("brandName = ?0") >> rqlStatementMock
		catalogRepositoryMock.getView(BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> {throw new RepositoryException()}
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BRANDS_ITEM_ID) >>BBBCatalogConstants.BRANDS_ITEM_ID
		when:
		String value = testObjCatalogTools.getBrandId("brandName")
		then:
		value == null
		1*testObjCatalogTools.logError("Catalog API Method Name [getBrandId]: RepositoryException ", _)
	}
	def"This method is used to fetch the packAndHoldEnd Dtae from SiteRepository"(){
		given:
		siteRepositoryMock.getItem("BedBathUS",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) >> new Date("05/16/2017")
		when:
		String value = testObjCatalogTools.packNHoldEndDate("BedBathUS")
		then:
		value.equals("05/16/2017")
	}
	def"This method is used to fetch the packAndHoldEnd Dtae from SiteRepository , respository item is null"(){
		given:
		siteRepositoryMock.getItem("BedBathUS",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> null
		when:
		String value = testObjCatalogTools.packNHoldEndDate(BBBCoreConstants.SITE_BAB_CA)
		then:
		value == null
	}
	def"This method is used to fetch the packAndHoldEnd Dtae from SiteRepository , respository item is  property value is null"(){
		given:
		siteRepositoryMock.getItem(TBSConstants.SITE_TBS_BAB_CA,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) >> null
		when:
		String value = testObjCatalogTools.packNHoldEndDate(TBSConstants.SITE_TBS_BAB_CA)
		then:
		value == null
	}
	def"Get the Brand name from Repository"(){
		given:
		catalogRepositoryMock.getItem("brandId",BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME) >> BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME
		when:
		String value = testObjCatalogTools.getBrandName("brandId")
		then:
		value.equals(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME)

	}
	def"Get the Brand name from Repository , repository item is null"(){
		given:
		catalogRepositoryMock.getItem("brandId",BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR) >> null
		when:
		String value = testObjCatalogTools.getBrandName("brandId")
		then:
		value == null

	}
	def"Get the Brand name from Repository , repository item is brand property value is null"(){
		given:
		catalogRepositoryMock.getItem("brandId",BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME) >> null
		when:
		String value = testObjCatalogTools.getBrandName("brandId")
		then:
		value == null

	}
	def"Get the Brand name from Repository , throw repository exception"(){
		given:
		catalogRepositoryMock.getItem("brandId",BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock of repository exception")}
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME) >> BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME
		when:
		String value = testObjCatalogTools.getBrandName("brandId")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")

	}
	def"The method gives the expected min and max date string when a customer can expect teh delivery"(){
		given:
		shippingRepositoryToolsMock.getShippingDuration("shippingMethod","siteId") >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME) >> new Date(addDays(0, "mm/dd/yyyy"))
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME) >> 4
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME) >> 3
		1*testObjCatalogTools.getWeekEndDays("siteId") >> [1,2,3,4].toSet()
		1*testObjCatalogTools.getHolidayList("siteId") >> [new Date(addDays(1, "mm/dd/yyyy"))].toSet()
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		SKUDetailVO skuDetailVOMock= Mock()
		testObjCatalogTools.getMinimalSku(repositoryItemMock) >> skuDetailVOMock
		skuDetailVOMock.getOrderToShipSla() >> "7"
		when:
		String value = testObjCatalogTools.getExpectedDeliveryDateForLTLItem("shippingMethod","siteId","skuId",new Date(),true)
		then:
		value != null
	}
	def"The method gives the expected min and max date string when a customer can expect teh delivery site id is TBS_BedBathCanada"(){
		given:
		shippingRepositoryToolsMock.getShippingDuration("shippingMethod","TBS_BedBathCanada") >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME) >> new Date()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME) >> 4
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME) >> 3
		testObjCatalogTools.getWeekEndDays("TBS_BedBathCanada") >> [1,3,4].toSet()
		testObjCatalogTools.getHolidayList("TBS_BedBathCanada") >> [new Date()].toSet()
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		SKUDetailVO skuDetailVOMock= Mock()
		testObjCatalogTools.getMinimalSku(repositoryItemMock) >> skuDetailVOMock
		skuDetailVOMock.getOrderToShipSla() >> "7"
		testObjCatalogTools.isHoliday(_,_)>> false
		when:
		String value = testObjCatalogTools.getExpectedDeliveryDateForLTLItem("shippingMethod","TBS_BedBathCanada","skuId",new Date(),false)
		then:
		value!= null
	}
	def"The method gives the expected min and max date string when a customer can expect teh delivery site id is BedBathCanada"(){
		given:
		shippingRepositoryToolsMock.getShippingDuration("shippingMethod","BedBathCanada") >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME) >> new Date(Calendar.getInstance().getTimeInMillis()+(10 * 60000))
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME) >> 4
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME) >> 3
		testObjCatalogTools.getWeekEndDays("BedBathCanada") >> [1,4].toSet()
		testObjCatalogTools.getHolidayList("BedBathCanada") >> [new Date()].toSet()
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		SKUDetailVO skuDetailVOMock= Mock()
		testObjCatalogTools.getMinimalSku(repositoryItemMock) >> skuDetailVOMock
		skuDetailVOMock.getOrderToShipSla() >> "7"
		testObjCatalogTools.isHoliday(_,_)>> true
		when:
		String value = testObjCatalogTools.getExpectedDeliveryDateForLTLItem("shippingMethod","BedBathCanada","skuId",new Date(),false)
		then:
		value!=null
	}
	def"The method gives the expected min and max date string when a customer can expect teh delivery site id is BedBathCanada cutofftime "(){
		given:
		shippingRepositoryToolsMock.getShippingDuration("shippingMethod","BedBathCanada") >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME) >> new Date()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME) >> 4
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME) >> 3
		testObjCatalogTools.getWeekEndDays("BedBathCanada") >> [1,4].toSet()
		testObjCatalogTools.getHolidayList("BedBathCanada") >> [new Date()].toSet()
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		SKUDetailVO skuDetailVOMock= Mock()
		testObjCatalogTools.getMinimalSku(repositoryItemMock) >> skuDetailVOMock
		skuDetailVOMock.getOrderToShipSla() >> "7"
		testObjCatalogTools.isHoliday(_,_)>> true
		when:
		String value = testObjCatalogTools.getExpectedDeliveryDateForLTLItem("shippingMethod","BedBathCanada","skuId",new Date(),false)
		then:
		value!= null
	}
	def"The method gives the expected min and max date string when a customer can expect teh delivery site id is BedBathCanada throw respository exception"(){
		given:
		shippingRepositoryToolsMock.getShippingDuration("shippingMethod","BedBathCanada") >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME) >> new Date()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME) >> 4
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME) >> 3
		testObjCatalogTools.getWeekEndDays("BedBathCanada") >> [1,3,4].toSet()
		testObjCatalogTools.getHolidayList("BedBathCanada") >> [new Date()].toSet()
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> {throw new RepositoryException("Mock of repository exception")}
		SKUDetailVO skuDetailVOMock= Mock()
		testObjCatalogTools.getMinimalSku(repositoryItemMock) >> skuDetailVOMock
		skuDetailVOMock.getOrderToShipSla() >> "7"
		testObjCatalogTools.isHoliday(_,_)>> true
		when:
		String value = testObjCatalogTools.getExpectedDeliveryDateForLTLItem("shippingMethod","BedBathCanada","skuId",new Date(addDays(1, "mm/dd/yyyy")),false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}

	def"The method gives the expected min and max date string when a customer can expect teh delivery order date is null"(){
		given:
		shippingRepositoryToolsMock.getShippingDuration("shippingMethod","siteId") >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME) >> new Date(addDays(1, "mm/dd/yyy"))
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME) >> 4
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME) >> 3
		testObjCatalogTools.getWeekEndDays("siteId") >> [1,4].toSet()
		testObjCatalogTools.getHolidayList("siteId") >> [new Date(addDays(1, "mm/dd/yyyy"))].toSet()
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		SKUDetailVO skuDetailVOMock= Mock()
		testObjCatalogTools.getMinimalSku(repositoryItemMock) >> skuDetailVOMock
		skuDetailVOMock.getOrderToShipSla() >> null
		when:
		String value = testObjCatalogTools.getExpectedDeliveryDateForLTLItem("shippingMethod","siteId","skuId",null,false)
		then:
		value!= null
	}
	def"The method gives the expected min and max date string when a customer can expect teh delivery cutoff time is null"(){
		given:
		shippingRepositoryToolsMock.getShippingDuration("shippingMethod",BBBCoreConstants.SITE_BAB_CA) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CUT_OFF_TIME_SHIPPING_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME) >> 4
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_DAYS_TO_SHIP_SHIPPING_PROPERTY_NAME) >> 3
		testObjCatalogTools.getWeekEndDays(BBBCoreConstants.SITE_BAB_CA) >> [1,3,4].toSet()
		testObjCatalogTools.getHolidayList(BBBCoreConstants.SITE_BAB_CA) >> [new Date(addDays(1, "mm/dd/yyyy"))].toSet()
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		SKUDetailVO skuDetailVOMock= Mock()
		testObjCatalogTools.getMinimalSku(repositoryItemMock) >> skuDetailVOMock
		skuDetailVOMock.getOrderToShipSla() >> null
		when:
		String value = testObjCatalogTools.getExpectedDeliveryDateForLTLItem("shippingMethod",BBBCoreConstants.SITE_BAB_CA,"skuId",null,false)
		then:
		value!= null
	}
	def"The method gives the expected min and max date string when a customer can expect teh delivery, respository item is null from shipping repository tools"(){
		given:
		1*shippingRepositoryToolsMock.getShippingDuration("shippingMethod","siteId") >> null
		when:
		String value = testObjCatalogTools.getExpectedDeliveryDateForLTLItem("shippingMethod","siteId","skuId",new Date(),false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2004:2004")
	}
	def"The method gives the expected min and max date string when a customer can expect teh delivery .siteid is null"(){
		when:
		String value = testObjCatalogTools.getExpectedDeliveryDateForLTLItem("shippingMethod",null,"skuId",null,true)
		then:
		Exception ex= thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"The method gives the expected min and max date string when a customer can expect teh delivery sku id is null"(){
		when:
		String value = testObjCatalogTools.getExpectedDeliveryDateForLTLItem("shippingMethod","siteId",null,null,true)
		then:
		Exception ex= thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"The method gives the expected min and max date string when a customer can expect teh delivery shipping method is null"(){
		when:
		String value = testObjCatalogTools.getExpectedDeliveryDateForLTLItem(null,"siteId","skuId",null,true)
		then:
		Exception ex= thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"This method is use get the ever ling product from Repository"(){
		given:
		RepositoryItemMock childProdItem = Mock()
		testObjCatalogTools.isEverlivingProduct("pProductId", "pSiteId") >> true
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> [childProdItem,childProdItem,childProdItem]
		childProdItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME) >> childProdItem
		childProdItem.getPropertyValue("giftCertProduct") >> null >> true
		childProdItem.getRepositoryId() >>"rep1234" >> "rep12345"
		testObjCatalogTools.isEverlivingProduct("rep1234", "pSiteId") >> false
		testObjCatalogTools.isEverlivingProduct("rep12345", "pSiteId") >>true >> false
		testObjCatalogTools.isProductActive(childProdItem) >> true >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID) >> BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID
		CollectionProductVO pVOMock = new CollectionProductVO()
		testObjCatalogTools.getCollectionProductVO(repositoryItemMock, _, "pSiteId") >> pVOMock
		repositoryMock.getView(BBBCatalogConstants.GUIDES) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock]
		testObjCatalogTools.parseRqlStatement(_) >> rqlStatementMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GUIDE_ID)>> BBBCatalogConstants.GUIDE_ID
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingMainProductDetails("pSiteId", "pProductId",true, false, false)
		then:
		pVO != null
		pVO.getIsEverLiving()
		pVO.getGiftCertProduct()
		pVO.getShopGuideId().equals(BBBCatalogConstants.GUIDE_ID)
	}
	def"This method is use get the ever ling product from Repository prductguied is null"(){
		given:
		RepositoryItemMock childProdItem = Mock()
		testObjCatalogTools.isEverlivingProduct("pProductId", "pSiteId") >> true
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> [childProdItem,childProdItem,childProdItem]
		childProdItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME) >> childProdItem
		childProdItem.getPropertyValue("giftCertProduct") >> null >> true
		childProdItem.getRepositoryId() >>"rep1234" >> "rep12345"
		testObjCatalogTools.isEverlivingProduct("rep1234", "pSiteId") >> false
		testObjCatalogTools.isEverlivingProduct("rep12345", "pSiteId") >>true >> false
		testObjCatalogTools.isProductActive(childProdItem) >> true >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID) >> null
		CollectionProductVO pVOMock = new CollectionProductVO()
		testObjCatalogTools.getCollectionProductVO(repositoryItemMock, _, "pSiteId") >> pVOMock
		repositoryMock.getView(BBBCatalogConstants.GUIDES) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock]
		testObjCatalogTools.parseRqlStatement(_) >> rqlStatementMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GUIDE_ID)>> BBBCatalogConstants.GUIDE_ID
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingMainProductDetails("pSiteId", "pProductId",true, false, false)
		then:
		pVO != null
		pVO.getIsEverLiving()
		pVO.getGiftCertProduct()
		pVO.getShopGuideId()==null
	}
	def"This method is use get the ever ling product from Repository throw Product is a collection but has no child products"(){
		given:
		RepositoryItemMock childProdItem = Mock()
		testObjCatalogTools.isEverlivingProduct("pProductId", "pSiteId") >> false
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> null
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingMainProductDetails("pSiteId", "pProductId",true, false, false)
		then:
		Exception ex =thrown()
		ex.getMessage().equals("1009:1009")
	}
	def"This method is use get the ever ling product from Repository ,for given product repository item is null"(){
		given:
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingMainProductDetails("pSiteId", "pProductId",true, true, false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1004:1004")
	}
	def"This method is use get the ever ling product from Repository ,throw respository exception"(){
		given:
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingMainProductDetails("pSiteId", "pProductId",true, true, false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"This method is use get the ever ling product from Repository siteis is null"(){
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingMainProductDetails(null, "pProductId",true, true, false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"This method is use get the ever ling product from Repository product is not everliving product"(){
		given:
		testObjCatalogTools.isEverlivingProduct("pProductId", "pSiteId") >> false
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingMainProductDetails("pSiteId", "pProductId",true, true, true)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1005:1005")
	}
	def"This method will get the productVO for ever living pdp page. This is differenet from regular productVO"(){
		given:
		Date preview =new Date()
		RepositoryItemMock childProdItem = Mock()
		testObjCatalogTools.isEverlivingProduct("pProductId", "BedBathUS") >> true
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> "Y"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME) >>BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID) >> BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID
		repositoryMock.getView(BBBCatalogConstants.GUIDES) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> null
		testObjCatalogTools.parseRqlStatement(_) >> rqlStatementMock
		RepositoryItem productAttributeRelns = Mock()
		RepositoryItem productAttributeRelns1 = Mock()
		RepositoryItem productAttributeRelns2 = Mock()
		RepositoryItem  prodAttributeRepositoryItem = Mock()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [productAttributeRelns,productAttributeRelns1,productAttributeRelns2].toSet()
		productAttributeRelns.getPropertyValue("bedbathUSFlag") >> false
		productAttributeRelns1.getPropertyValue("bedbathUSFlag") >> true
		productAttributeRelns2.getPropertyValue("bedbathUSFlag") >> true
		productAttributeRelns1.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> null
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> prodAttributeRepositoryItem
		SddZipcodeVO zipcodeVO= new SddZipcodeVO()
		zipcodeVO.setPromoAttId("promoAttId")
		zipcodeVO.setSddEligibility(true)
		sessionBeanMock.getCurrentZipcodeVO() >> zipcodeVO
		sessionBeanMock.isInternationalShippingContext() >> true
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.START_DATE_PROD_RELATION_PROPERTY_NAME) >> preview
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.END_DATE_PROD_RELATION_PROPERTY_NAME)   >> preview
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_PROD_RELATION_PROPERTY_NAME) >> preview
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_PROD_RELATION_PROPERTY_NAME)   >> preview
		prodAttributeRepositoryItem.getRepositoryId() >> "prdRel1234"
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG) >> 'Y'
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY,BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> []
		BBBConfigRepoUtils.setBbbCatalogTools(catalogTools)
		catalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["true"]
		testObjCatalogTools.setPreviewEnabled(true)
		PreviewAttributes previewAttributes = Mock()
		previewAttributes.getPreviewDate() >> preview
		requestMock.resolveName("/com/bbb/commerce/catalog/PreviewAttributes") >> previewAttributes
		testObjCatalogTools.setBBBSiteToAttributeSiteMap(siteToAttributeSiteMap)
		globalRepositoryToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PREVIEW_ENABLED) >> ["true"]
		testObjCatalogTools.setZoomKeys("prdRel1234")
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "prdRel1234") >> ["prdRel1234"]
		repositoryItemMock.getPropertyValue("giftCertProduct") >> true
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingMainProductDetails("BedBathUS", "pProductId",false, true, false)
		then:
		pVO != null
		pVO.getIsEverLiving()
		pVO.getShopGuideId()==null
		pVO.isIntlRestricted()
		pVO.getPriceRangeDescriptionRepository().equals(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME)
		pVO.getDefaultPriceRangeDescription().equals(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME)
		pVO.isZoomAvailable() == false
		pVO.getGiftCertProduct()
		pVO.isEverLiving
		pVO.getBvReviews().isRatingAvailable() == false
	}
	def"This method will get the productVO for ever living pdp page. Product is not collection and lead "(){
		given:
		Date preview =new Date()
		RepositoryItemMock childProdItem = Mock()
		testObjCatalogTools.isEverlivingProduct("pProductId", "BedBathUS") >> true
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> "Y"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME) >>BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID) >> BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID
		repositoryMock.getView(BBBCatalogConstants.GUIDES) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock]
		testObjCatalogTools.parseRqlStatement(_) >> rqlStatementMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GUIDE_ID)>> BBBCatalogConstants.GUIDE_ID
		RepositoryItem productAttributeRelns = Mock()
		RepositoryItem productAttributeRelns1 = Mock()
		RepositoryItem productAttributeRelns2 = Mock()
		RepositoryItem  prodAttributeRepositoryItem = Mock()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [productAttributeRelns,productAttributeRelns1,productAttributeRelns2].toSet()
		productAttributeRelns.getPropertyValue("bedbathUSFlag") >> false
		productAttributeRelns1.getPropertyValue("bedbathUSFlag") >> true
		productAttributeRelns2.getPropertyValue("bedbathUSFlag") >> true
		productAttributeRelns1.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> null
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> prodAttributeRepositoryItem
		SddZipcodeVO zipcodeVO= new SddZipcodeVO()
		zipcodeVO.setPromoAttId("promoAttId")
		zipcodeVO.setSddEligibility(true)
		sessionBeanMock.getCurrentZipcodeVO() >> zipcodeVO
		sessionBeanMock.isInternationalShippingContext() >> true
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.START_DATE_PROD_RELATION_PROPERTY_NAME) >> preview
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.END_DATE_PROD_RELATION_PROPERTY_NAME)   >> preview
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_PROD_RELATION_PROPERTY_NAME) >> preview
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_PROD_RELATION_PROPERTY_NAME)   >> preview
		prodAttributeRepositoryItem.getRepositoryId() >> "prdRel1234"
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG) >> 'Y'
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY,BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> []
		BBBConfigRepoUtils.setBbbCatalogTools(catalogTools)
		catalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["true"]
		testObjCatalogTools.setPreviewEnabled(true)
		PreviewAttributes previewAttributes = Mock()
		previewAttributes.getPreviewDate() >> preview
		requestMock.resolveName("/com/bbb/commerce/catalog/PreviewAttributes") >> previewAttributes
		testObjCatalogTools.setBBBSiteToAttributeSiteMap(siteToAttributeSiteMap)
		globalRepositoryToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PREVIEW_ENABLED) >> ["true"]
		testObjCatalogTools.setZoomKeys("prdRel1234")
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "prdRel1234") >> ["prdRel1234"]
		repositoryItemMock.getPropertyValue("giftCertProduct") >> true
		productAttributeRelns.getRepositoryId() >> "sku1234"
		productAttributeRelns1.getRepositoryId() >> "sku1234"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> [productAttributeRelns,productAttributeRelns1]
		//testObjCatalogTools.updateProductTabs(_, "siteId", _) >> {}
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingMainProductDetails("siteId", "pProductId",false, false, false)
		then:
		pVO != null
		pVO.getIsEverLiving() == false
		pVO.getShopGuideId().equals("id")
		pVO.isIntlRestricted()
		pVO.isZoomAvailable()
		pVO.getGiftCertProduct()
		pVO.getBvReviews().isRatingAvailable() == false
		pVO.getChildSKUs().size() == 2
		pVO.getChildSKUs().get(0).equals("sku1234")
		pVO.getChildSKUs().get(1).equals("sku1234")
	}
	def"The method returns ProductVO If product is a collection then the rollup types of the childProducts need to come"(){
		given:
		RepositoryItemMock childProdItem = Mock()
		testObjCatalogTools.isEverlivingProduct("pProductId", "siteId") >> true
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> [childProdItem,childProdItem,childProdItem] >> []
		childProdItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME) >> childProdItem
		childProdItem.getPropertyValue("giftCertProduct") >> null >> true
		childProdItem.getRepositoryId() >>"rep1234" >> "rep12345"
		testObjCatalogTools.isEverlivingProduct("rep1234", "siteId") >> false
		testObjCatalogTools.isEverlivingProduct("rep12345", "siteId") >>true >> true >> false
		testObjCatalogTools.isProductActive(childProdItem) >> true >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID) >> null
		CollectionProductVO pVOMock = new CollectionProductVO()
		testObjCatalogTools.getCollectionProductVO(repositoryItemMock, _, "pSiteId") >> pVOMock
		repositoryMock.getView(BBBCatalogConstants.GUIDES) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock]
		testObjCatalogTools.parseRqlStatement(_) >> rqlStatementMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GUIDE_ID)>> BBBCatalogConstants.GUIDE_ID
		ProductVO  pvoMock = new ProductVO()
		testObjCatalogTools.getProductDetails("siteId", _, false) >> pvoMock
		RepositoryItem secondtimerepositoryItemMock =Mock()
		catalogRepositoryMock.getItem("rep12345",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> secondtimerepositoryItemMock
		secondtimerepositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >>  null
		secondtimerepositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >>  true
		secondtimerepositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> []
		CollectionProductVO cPVO = Mock()
		CollectionProductVO cPVO1 = new CollectionProductVO()
		testObjCatalogTools.getCollectionProductVO(secondtimerepositoryItemMock, _, "siteId") >> cPVO
		testObjCatalogTools.getCollectionProductVO(repositoryItemMock, _, "siteId") >> cPVO1
		repositoryMock.getView(BBBCatalogConstants.GUIDES) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [secondtimerepositoryItemMock]
		testObjCatalogTools.parseRqlStatement(_) >> rqlStatementMock
		secondtimerepositoryItemMock.getPropertyValue(BBBCatalogConstants.GUIDE_ID)>> BBBCatalogConstants.GUIDE_ID
		secondtimerepositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID) >>  BBBCatalogConstants.GUIDE_ID
		when:
		ProductVO pVO = testObjCatalogTools.getEverLivingProductDetails("siteId", "pProductId",false, false, false)
		then:
		pVO.getGiftCertProduct()
		pVO.getIsEverLiving()
		1*cPVO.setGiftCertProduct(_)
		1*cPVO.setIsEverLiving(_)
		1*cPVO.setShopGuideId(_)
	}
	def"The method returns ProductVO If product is a collection then the rollup types of the childProducts need to come , child rollup item are not null"(){
		given:
		RepositoryItemMock childProdItem = Mock()
		testObjCatalogTools.isEverlivingProduct("pProductId", "siteId") >> false
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> [childProdItem,childProdItem,childProdItem] >> []
		childProdItem.getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME) >> null
		childProdItem.getPropertyValue(BBBCatalogConstants.COLLECTION_ROLL_UP_PRODUCT_RELATION_PROPERTY_NAME) >> childProdItem
		childProdItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME) >> childProdItem
		childProdItem.getPropertyValue("giftCertProduct") >> null >> true
		childProdItem.getRepositoryId() >>"rep1234" >> "rep12345"
		testObjCatalogTools.isEverlivingProduct("rep1234", "siteId") >> false
		testObjCatalogTools.isEverlivingProduct("rep12345", "siteId") >>true >> true >> false
		testObjCatalogTools.isProductActive(childProdItem) >> true >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID) >> null
		CollectionProductVO pVOMock = new CollectionProductVO()
		testObjCatalogTools.getCollectionProductVO(repositoryItemMock, _, "pSiteId") >> pVOMock
		repositoryMock.getView(BBBCatalogConstants.GUIDES) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock]
		testObjCatalogTools.parseRqlStatement(_) >> rqlStatementMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GUIDE_ID)>> BBBCatalogConstants.GUIDE_ID
		ProductVO  pvoMock = new ProductVO()
		testObjCatalogTools.getProductDetails("siteId", _, false) >> pvoMock
		RepositoryItem secondtimerepositoryItemMock =Mock()
		catalogRepositoryMock.getItem("rep12345",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> secondtimerepositoryItemMock
		secondtimerepositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >>  null
		secondtimerepositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >>  true
		secondtimerepositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> null
		CollectionProductVO cPVO = Mock()
		CollectionProductVO cPVO1 = new CollectionProductVO()
		testObjCatalogTools.getCollectionProductVO(secondtimerepositoryItemMock, _, "siteId") >> cPVO
		testObjCatalogTools.getCollectionProductVO(repositoryItemMock, _, "siteId") >> cPVO1
		repositoryMock.getView(BBBCatalogConstants.GUIDES) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [secondtimerepositoryItemMock]
		testObjCatalogTools.parseRqlStatement(_) >> rqlStatementMock
		secondtimerepositoryItemMock.getPropertyValue(BBBCatalogConstants.GUIDE_ID)>> BBBCatalogConstants.GUIDE_ID
		secondtimerepositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID) >>  BBBCatalogConstants.GUIDE_ID
		when:
		ProductVO pVO = testObjCatalogTools.getEverLivingProductDetails("siteId", "pProductId",false, false, false)
		then:
		pVO.getGiftCertProduct()
		pVO.getIsEverLiving() == false
		1*cPVO.setGiftCertProduct(_)
		1*cPVO.setIsEverLiving(_)
		1*cPVO.setShopGuideId(_)
	}
	def"The method returns ProductVO If product is a collection then the rollup types of the childProducts need to come,for given product repository item is null"(){
		given:
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingProductDetails("pSiteId", "pProductId",true, true, false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1004:1004")
	}
	def"The method returns ProductVO If product is a collection then the rollup types of the childProducts need to come,throw respository exception"(){
		given:
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingProductDetails("pSiteId", "pProductId",true, true, false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"The method returns ProductVO If product is a collection then the rollup types of the childProducts need to come from Repository siteis is null"(){
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingProductDetails(null, "pProductId",true, true, false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"The method returns ProductVO If product is a collection then the rollup types of the childProducts is empty and not lead prodcut"(){
		given:
		testObjCatalogTools.isEverlivingProduct("pProductId", "siteId") >> false
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >>  []
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingProductDetails("siteId", "pProductId",false, false, false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1009:1009")
	}
	def"The method returns ProductVO If product is a collection then the rollup types of the childProducts need to empty product and not everliving product"(){
		given:
		testObjCatalogTools.isEverlivingProduct("pProductId", "pSiteId") >> false
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingProductDetails("pSiteId", "pProductId",true, true, true)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1005:1005")
	}
	def"The method returns ProductVO If product is a collection then the rollup types of the childProducts. Product is not collection and lead "(){
		given:
		Date preview =new Date()
		RepositoryItemMock childProdItem = Mock()
		testObjCatalogTools.isEverlivingProduct("pProductId", "BedBathUS") >> true
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME) >>BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID) >> BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID
		repositoryMock.getView(BBBCatalogConstants.GUIDES) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock]
		testObjCatalogTools.parseRqlStatement(_) >> rqlStatementMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GUIDE_ID)>> BBBCatalogConstants.GUIDE_ID
		RepositoryItem productAttributeRelns = Mock()
		RepositoryItem productAttributeRelns1 = Mock()
		RepositoryItem productAttributeRelns2 = Mock()
		RepositoryItem  prodAttributeRepositoryItem = Mock()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [productAttributeRelns,productAttributeRelns1,productAttributeRelns2].toSet()
		productAttributeRelns.getPropertyValue("bedbathUSFlag") >> true
		productAttributeRelns1.getPropertyValue("bedbathUSFlag") >> true
		productAttributeRelns2.getPropertyValue("bedbathUSFlag") >> true
		productAttributeRelns1.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> prodAttributeRepositoryItem
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> prodAttributeRepositoryItem
		productAttributeRelns.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> prodAttributeRepositoryItem
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG) >> "X" >> "y"
		SddZipcodeVO zipcodeVO= new SddZipcodeVO()
		zipcodeVO.setPromoAttId("promoAttId")
		zipcodeVO.setSddEligibility(true)
		sessionBeanMock.getCurrentZipcodeVO() >> zipcodeVO
		sessionBeanMock.isInternationalShippingContext() >> false
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.START_DATE_PROD_RELATION_PROPERTY_NAME) >> preview
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.END_DATE_PROD_RELATION_PROPERTY_NAME)   >> preview
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_PROD_RELATION_PROPERTY_NAME) >> preview
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_PROD_RELATION_PROPERTY_NAME)   >> preview
		prodAttributeRepositoryItem.getRepositoryId() >> "prdRel1234"
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG) >> null
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY,BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> []
		BBBConfigRepoUtils.setBbbCatalogTools(catalogTools)
		catalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> []
		testObjCatalogTools.setPreviewEnabled(true)
		PreviewAttributes previewAttributes = Mock()
		previewAttributes.getPreviewDate() >> preview
		requestMock.resolveName("/com/bbb/commerce/catalog/PreviewAttributes") >> previewAttributes
		testObjCatalogTools.setBBBSiteToAttributeSiteMap(siteToAttributeSiteMap)
		globalRepositoryToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PREVIEW_ENABLED) >> ["false"]
		testObjCatalogTools.setZoomKeys("prdRel1234")
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "prdRel1234") >> []
		repositoryItemMock.getPropertyValue("giftCertProduct") >> true
		productAttributeRelns.getRepositoryId() >> "sku1234"
		productAttributeRelns1.getRepositoryId() >> "sku1234"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> [productAttributeRelns,productAttributeRelns1]
		//testObjCatalogTools.updateProductTabs(_, "siteId", _) >> {}
		testObjCatalogTools.sameDayDeliverEligibility(prodAttributeRepositoryItem, _, _) >> false >> true
		when:
		ProductVO pVO= testObjCatalogTools.getEverLivingProductDetails("BedBathUS", "pProductId",false, true, false)
		then:
		pVO != null
		pVO.getIsEverLiving() == true
		pVO.getShopGuideId().equals("id")
		pVO.isIntlRestricted() == false
		pVO.isZoomAvailable()==true
		pVO.getGiftCertProduct()
		pVO.getBvReviews().isRatingAvailable() == false
	}

	///Still need to write test cases

	def" Used for Rest API Method to return clearance collections list vo's for given site id and category id "(){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		CategoryVO categoryVO = Mock()
		CategoryVO categoryVO1 = Mock()
		CategoryVO categoryVO2 = Mock()
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> categoryVO
		categoryVO.getSubCategories() >> [categoryVO1,categoryVO2]
		categoryVO1.getSubCategories() >> null
		categoryVO2.getChildProducts()>> ["prd1234","prd1234"]
		CollectionProductVO pVO = Mock()
		pVO.isCollection() >> true
		testObjCatalogTools.getProductDetails("BedBathUS", "prd1234", true) >> pVO
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceCollections("BedBathUS", "pCategoryId")
		then:
		productVOList.size() == 2
	}
	def" Used for Rest API Method to return clearance collections list vo's for given site id and category id throw business exception"(){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> {throw new BBBBusinessException("Mock of Business exception")}
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceCollections("BedBathUS", "pCategoryId")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("8008:8008")
	}
	def"Used for Rest API Method to return clearance collections list vo's for given site id and category id throw system exception"(){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> {throw new BBBSystemException("Mock of system exception")}
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceCollections("BedBathUS", "pCategoryId")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def" Used for Rest API Method to return clearance collections list vo's for given site id and category id,child products is null" (){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		CategoryVO categoryVO = Mock()
		CategoryVO categoryVO1 = Mock()
		CategoryVO categoryVO2 = Mock()
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> categoryVO
		categoryVO.getSubCategories() >> [categoryVO1]
		categoryVO1.getSubCategories() >> null
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceCollections("BedBathUS", "pCategoryId")
		then:
		productVOList.size() == 0
	}
	def"Used for Rest API Method to return clearance collections list vo's for given site id and category id,subCategory is null" (){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		CategoryVO categoryVO = Mock()
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> categoryVO
		categoryVO.getSubCategories() >> []
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceCollections("BedBathUS", "pCategoryId")
		then:
		productVOList.size() == 0
	}
	def" Used for Rest API Method to return clearance collections list vo's for given site id and category id,getCategoryDetail is null" (){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> null
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceCollections("BedBathUS", "pCategoryId")
		then:
		productVOList.size() == 0
	}
	def"Used for Rest API Method to return clearance collections list vo's for given site id and category id single child product vo is null"(){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		CategoryVO categoryVO = Mock()
		CategoryVO categoryVO1 = Mock()
		CategoryVO categoryVO2 = Mock()
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> categoryVO
		categoryVO.getSubCategories() >> [categoryVO1,categoryVO2]
		categoryVO1.getChildProducts() >> ["prd1235"]
		categoryVO2.getChildProducts()>> ["prd1234","prd1234"]
		ProductVO pVO = Mock()
		testObjCatalogTools.getProductDetails("BedBathUS", "prd1234", true) >> null
		testObjCatalogTools.getProductDetails("BedBathUS", "prd1235", true) >> pVO
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceCollections("BedBathUS", "pCategoryId")
		then:
		productVOList.size() == 1
	}
	def" Used for Rest API Method to return clearance collections list vo's for given site id and category id  getchild products is empty"(){
		given:
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		CategoryVO categoryVO = Mock()
		CategoryVO categoryVO1 = Mock()
		CategoryVO categoryVO2 = Mock()
		testObjCatalogTools.getCategoryDetail("BedBathUS", "pCategoryId",true) >> categoryVO
		categoryVO.getSubCategories() >> [categoryVO1,categoryVO2]
		categoryVO1.getChildProducts() >> []
		categoryVO2.getChildProducts()>> ["prd1234"]
		ProductVO pVO = Mock()
		testObjCatalogTools.getProductDetails("BedBathUS", "prd1234", true) >> {throw new BBBBusinessException("Mock of business exception")}
		when:
		List<ProductVO> productVOList  = testObjCatalogTools.getClearanceCollections("BedBathUS", "pCategoryId")
		then:
		productVOList.size() == 0
	}
	def"method to fetch list of product that are available for clearance,Zero product for given site"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUS_clearanceCategories") >> []
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		when:
		List clearanceList = testObjCatalogTools.getClearanceProduct()
		then:
		clearanceList.isEmpty()
	}
	def"method to fetch list of product that are available for clearance,Category item is null"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "BedBathUS_clearanceCategories") >> ["cat1234"]
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "BedBathUS_noClearanceProducts") >> ["3"]
		testObjCatalogTools.setConfigType("ContentCatalogKeys")
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat1234",true) >> null
		when:
		List clearanceList = testObjCatalogTools.getClearanceProduct()
		then:
		clearanceList.size()==0
	}
	def"Method is used to get product details for lazy loading"(){
		given:
		RepositoryItemMock childProdItem = Mock()
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "collection_child_prd_row_count") >> ["4"]
		catalogRepositoryMock.getItem("ProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> [childProdItem,childProdItem]
		childProdItem.getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME) >> null
		childProdItem.getPropertyValue(BBBCatalogConstants.COLLECTION_ROLL_UP_PRODUCT_RELATION_PROPERTY_NAME) >> childProdItem
		childProdItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME) >> childProdItem
		childProdItem.getPropertyValue("giftCertProduct") >> null >> true
		childProdItem.getRepositoryId() >>"rep1234" >> "rep12345"
		testObjCatalogTools.isProductActive(childProdItem) >> true >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID) >> null
		ProductVO pVOMock = new ProductVO()
		testObjCatalogTools.getProductDetails("siteId", "rep1234", false) >> pVOMock
		repositoryMock.getView(BBBCatalogConstants.GUIDES) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock]
		testObjCatalogTools.parseRqlStatement(_) >> rqlStatementMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GUIDE_ID)>> BBBCatalogConstants.GUIDE_ID
		ProductVO  pvoMock = new ProductVO()
		testObjCatalogTools.getProductDetails("siteId", _, false) >> pvoMock
		CollectionProductVO cPVO1 = new CollectionProductVO()
		testObjCatalogTools.getCollectionProductVO(repositoryItemMock, _, "siteId") >> cPVO1
		when:
		ProductVO pVO = testObjCatalogTools.getProductDetailsForLazyLoading("siteId", "ProductId",false,false, 0,false)
		then:
		pVO !=  null
		cPVO1.getGiftCertProduct()
		cPVO1.getIsEverLiving() == null

	}
	def"Method is used to get product details for lazy loading,child product rollup items are null"(){
		given:
		RepositoryItemMock childProdItem = Mock()
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "collection_child_prd_row_count") >> {throw new BBBSystemException("Mock of system exception")}
		catalogRepositoryMock.getItem("ProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> [childProdItem,childProdItem,childProdItem,childProdItem]
		childProdItem.getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE_ROLLUP_PROPERTY_NAME) >> null
		childProdItem.getPropertyValue(BBBCatalogConstants.COLLECTION_ROLL_UP_PRODUCT_RELATION_PROPERTY_NAME) >>null>> childProdItem
		childProdItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME) >> childProdItem
		childProdItem.getPropertyValue("giftCertProduct") >> null >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY) >> BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY
		1*vendorRepository.getView(BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR) >>  repositoryViewMock
		1*repositoryViewMock.getQueryBuilder() >> qBuilder
		1*qBuilder.createComparisonQuery(qExp, qExp, 4) >> query
		1*qBuilder.createPropertyQueryExpression("customizationVendorId") >> qExp
		1*qBuilder.createConstantQueryExpression("vendorId") >> qExp
		childProdItem.getRepositoryId() >>"rep1234" >> "rep12345"
		testObjCatalogTools.isProductActive(childProdItem) >> true
		ProductVO pVOMock = new ProductVO()
		testObjCatalogTools.getProductDetails("siteId", "rep1234", false) >> pVOMock
		AttributeVO atr1 =new  AttributeVO()
		AttributeVO atr2 =new  AttributeVO()
		pVOMock.setAttributesList(["atr1":[atr1],"atr2":null])
		repositoryMock.getView(BBBCatalogConstants.GUIDES) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock]
		testObjCatalogTools.parseRqlStatement(_) >> rqlStatementMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GUIDE_ID)>> BBBCatalogConstants.GUIDE_ID
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID)>> BBBCatalogConstants.GUIDE_ID
		ProductVO  pvoMock = new ProductVO()
		testObjCatalogTools.getProductDetails("siteId", _, false) >> pvoMock
		CollectionProductVO cPVO1 = new CollectionProductVO()
		testObjCatalogTools.getCollectionProductVO(repositoryItemMock, _, "siteId") >> cPVO1
		when:
		ProductVO pVO = testObjCatalogTools.getProductDetailsForLazyLoading("siteId", "ProductId",false,false, 0,false)
		then:
		pVO !=  null
		cPVO1.getGiftCertProduct()
		cPVO1.getIsEverLiving() == null
		cPVO1.getShopGuideId().equals(BBBCatalogConstants.GUIDE_ID)

	}
	def"Method is used to get product details for lazy loading,index is -1"(){
		given:
		RepositoryItemMock childProdItem = Mock()
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "collection_child_prd_row_count") >> {throw new BBBBusinessException("Mock of Business exception")}
		catalogRepositoryMock.getItem("ProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> [childProdItem,childProdItem]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID) >> null
		ProductVO pVOMock = new ProductVO()
		repositoryMock.getView(BBBCatalogConstants.GUIDES) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock]
		testObjCatalogTools.parseRqlStatement(_) >> rqlStatementMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GUIDE_ID)>> BBBCatalogConstants.GUIDE_ID
		ProductVO  pvoMock = new ProductVO()
		testObjCatalogTools.getProductDetails("siteId", _, false) >> pvoMock
		CollectionProductVO cPVO1 = new CollectionProductVO()
		testObjCatalogTools.getCollectionProductVO(repositoryItemMock, _, "siteId") >> cPVO1
		when:
		ProductVO pVO = testObjCatalogTools.getProductDetailsForLazyLoading("siteId", "ProductId",false,false, -1,false)
		then:
		pVO !=  null
		cPVO1.getGiftCertProduct() == false
		cPVO1.getShopGuideId() == null

	}
	def"Method is used to get product details for lazy loading,child product is null"(){
		given:
		RepositoryItemMock childProdItem = Mock()
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "collection_child_prd_row_count") >> {throw new BBBBusinessException("Mock of Business exception")}
		catalogRepositoryMock.getItem("ProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID) >> null
		ProductVO pVOMock = new ProductVO()
		repositoryMock.getView(BBBCatalogConstants.GUIDES) >> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock]
		testObjCatalogTools.parseRqlStatement(_) >> rqlStatementMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GUIDE_ID)>> BBBCatalogConstants.GUIDE_ID
		ProductVO  pvoMock = new ProductVO()
		testObjCatalogTools.getProductDetails("siteId", _, false) >> pvoMock
		CollectionProductVO cPVO1 = new CollectionProductVO()
		testObjCatalogTools.getCollectionProductVO(repositoryItemMock, _, "siteId") >> cPVO1
		when:
		ProductVO pVO = testObjCatalogTools.getProductDetailsForLazyLoading("siteId", "ProductId",false,false, -1,false)
		then:
		pVO !=  null
		cPVO1.getGiftCertProduct() == false
		cPVO1.getShopGuideId() == null

	}
	def"Method is used to get product details for lazy loading,child product is null and not a lead product"(){
		given:
		RepositoryItemMock childProdItem = Mock()
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "collection_child_prd_row_count") >> {throw new BBBBusinessException("Mock of Business exception")}
		catalogRepositoryMock.getItem("ProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> null
		when:
		ProductVO pVO = testObjCatalogTools.getProductDetailsForLazyLoading("siteId", "ProductId",false,false, -1,false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1009:1009")
	}
	def"Method is used to get product details for lazy loading,for given product repository item is null"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "collection_child_prd_row_count") >> ["4"]
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null
		when:
		ProductVO pVO= testObjCatalogTools.getProductDetailsForLazyLoading("pSiteId", "pProductId",true, true,0, false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1004:1004")
	}
	def"Method is used to get product details for lazy loading,throw respository exception"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "collection_child_prd_row_count") >> ["4"]
		catalogRepositoryMock.getItem("pProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		ProductVO pVO= testObjCatalogTools.getProductDetailsForLazyLoading("pSiteId", "pProductId",true, true,0, false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"Method is used to get product details for lazy loading,site id is null"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "collection_child_prd_row_count") >> []
		when:
		ProductVO pVO = testObjCatalogTools.getProductDetailsForLazyLoading(null, "ProductId",false,false, 0,false)
		then:
		Exception ex =thrown()
		ex.getMessage().equals("2006:2006")

	}
	def"Method is used to get product details for lazy loading,Product is not active and exception is their"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "collection_child_prd_row_count") >> []
		catalogRepositoryMock.getItem("ProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.isProductActive(repositoryItemMock) >> false
		when:
		ProductVO pVO = testObjCatalogTools.getProductDetailsForLazyLoading("siteId", "ProductId",false,false, 0,true)
		then:
		Exception ex =thrown()
		ex.getMessage().equals("1005:1005")

	}
	def"Method is used to get product details for lazy loading,Product is not lead and collection"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, "collection_child_prd_row_count") >> []
		catalogRepositoryMock.getItem("ProductId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.isProductActive(repositoryItemMock) >> false
		testObjCatalogTools.getProductVO(repositoryItemMock,"siteId",true,true) >> null
		when:
		ProductVO pVO = testObjCatalogTools.getProductDetailsForLazyLoading("siteId", "ProductId",true,true, 0,false)
		then:
		pVO == null
	}
	def"The method sets the product Tabs for teh product Teh method checks if the list of product tabs associaed"(){
		given:
		ProductVO pVOMock= new ProductVO()
		RepositoryItem sitesrepItem = Mock()
		RepositoryItem sitesrepItem1 = Mock()
		RepositoryItem sitesrepItem2 = Mock()
		RepositoryItem sitesrepItem3 = Mock()
		RepositoryItem sitesrepItem4 = Mock()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_TABS_PRODUCT_PROPERTY_NAME) >> [repositoryItemMock,repositoryItemMock,repositoryItemMock,repositoryItemMock,repositoryItemMock,repositoryItemMock,repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITES_TABS_PROPERTY_NAME) >> null >> [].toSet() >> [sitesrepItem,sitesrepItem1].toSet()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.TAB_NAME_TABS_PROPERTY_NAME) >>null>>null>> "harmonTabName">>null >> BBBCatalogConstants.TAB_NAME_TABS_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.TAB_CONTENT_TABS_PROPERTY_NAME) >>null>>null>>BBBCatalogConstants.TAB_CONTENT_TABS_PROPERTY_NAME >> null >> null >> BBBCatalogConstants.TAB_CONTENT_TABS_PROPERTY_NAME
		sitesrepItem.getRepositoryId() >> "psiteId"
		sitesrepItem1.getRepositoryId() >> "siteId"
		sitesrepItem2.getRepositoryId() >> "siteId"
		sitesrepItem3.getRepositoryId() >> "siteId"
		testObjCatalogTools.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PDP_TAB_NAME_HARMON) >> ["harmonTabName"]
		when:
		ProductVO pVO = testObjCatalogTools.updateProductTabs(productRepositoryItemMock, "siteId", pVOMock)
		then:
		pVO != null
		pVO.getProductTabs().size() ==4
	}
	def"The method sets the product Tabs for teh product Teh method checks if the list of product tabs associaed,throw system exception"(){
		given:
		ProductVO pVOMock= new ProductVO()
		RepositoryItem sitesrepItem = Mock()
		RepositoryItem sitesrepItem1 = Mock()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_TABS_PRODUCT_PROPERTY_NAME) >> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITES_TABS_PROPERTY_NAME)  >> [sitesrepItem1].toSet()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.TAB_NAME_TABS_PROPERTY_NAME) >> "NotharmonTab"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.TAB_CONTENT_TABS_PROPERTY_NAME) >>BBBCatalogConstants.TAB_CONTENT_TABS_PROPERTY_NAME
		sitesrepItem1.getRepositoryId() >> "siteId"
		testObjCatalogTools.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PDP_TAB_NAME_HARMON) >> {throw new BBBSystemException("Mock of system exceptions")}
		when:
		ProductVO pVO = testObjCatalogTools.updateProductTabs(productRepositoryItemMock, "siteId", pVOMock)
		then:
		pVO != null
		pVO.getProductTabs().size() ==1
	}
	def"The method sets the product Tabs for teh product Teh method checks if the list of product tabs associaed,throw business exception"(){
		given:
		ProductVO pVOMock= new ProductVO()
		RepositoryItem sitesrepItem = Mock()
		RepositoryItem sitesrepItem1 = Mock()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_TABS_PRODUCT_PROPERTY_NAME) >> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITES_TABS_PROPERTY_NAME)  >> [sitesrepItem1].toSet()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.TAB_NAME_TABS_PROPERTY_NAME) >> BBBCatalogConstants.TAB_NAME_TABS_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.TAB_CONTENT_TABS_PROPERTY_NAME) >>null
		sitesrepItem1.getRepositoryId() >> "siteId"
		testObjCatalogTools.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PDP_TAB_NAME_HARMON) >> {throw new BBBBusinessException("Mock of system exceptions")}
		when:
		ProductVO pVO = testObjCatalogTools.updateProductTabs(productRepositoryItemMock, "siteId", pVOMock)
		then:
		pVO != null
		pVO.getProductTabs().size() ==1
	}
	def"The method sets the product Tabs for teh product Teh method checks if the list of product tabs associaed,harmontab vlaue from repository is null"(){
		given:
		ProductVO pVOMock= new ProductVO()
		RepositoryItem sitesrepItem = Mock()
		RepositoryItem sitesrepItem1 = Mock()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_TABS_PRODUCT_PROPERTY_NAME) >> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITES_TABS_PROPERTY_NAME)  >> [sitesrepItem1].toSet()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.TAB_NAME_TABS_PROPERTY_NAME) >> BBBCatalogConstants.TAB_NAME_TABS_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.TAB_CONTENT_TABS_PROPERTY_NAME) >>null
		sitesrepItem1.getRepositoryId() >> "siteId"
		testObjCatalogTools.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PDP_TAB_NAME_HARMON) >>[]
		when:
		ProductVO pVO = testObjCatalogTools.updateProductTabs(productRepositoryItemMock, "siteId", pVOMock)
		then:
		pVO != null
		pVO.getProductTabs().size() ==1
	}
	def"The method sets the product Tabs for teh product Teh method checks if the list of product tabs associaed,productTabs is null"(){
		given:
		ProductVO pVOMock= Mock()
		productRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_TABS_PRODUCT_PROPERTY_NAME) >> null
		when:
		ProductVO pVO = testObjCatalogTools.updateProductTabs(productRepositoryItemMock, "siteId", pVOMock)
		then:
		1*pVOMock.setProductTabs(_)
	}
	def"The method sets the product Tabs for teh product Teh method checks if the list of product tabs associaed,RepositoryItem is null "(){
		given:
		ProductVO pVOMock= Mock()
		when:
		ProductVO pVO = testObjCatalogTools.updateProductTabs(null, "siteId", pVOMock)
		then:
		0*pVOMock.setProductTabs(_)
	}
	def" Method will return a omniture value, which is dependent on siteSpect header value"(){
		given:
		requestMock.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO) >> sQueryMock
		eSearchUtil.getEphLookUpUtil() >> lookupUtilMock
		lookupUtilMock.isEpHLookUpEnable() >> true
		sQueryMock.isFromBrandPage() >> false
		sQueryMock.getKeyWord() >> "keyword"
		2*eSearchUtil.getEPHBoostingScheme(sQueryMock) >>  "EPHBoostingScheme"
		requestMock.getParameter(BBBCoreConstants.PAGE_NAME) >> BBBCoreConstants.SEARCH
		requestMock.getSession() >> sessionMock
		sessionMock.getAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE) >> BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE
		sessionMock.getAttribute(BBBCoreConstants.VENDOR_PARAM) >> BBBCoreConstants.VENDOR_PARAM
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.VENDOR_FLAG,BBBCoreConstants.FALSE)>> true
		1*eSearchTools.getSearchBoostingAlgorithmsFromLocalMap(_) >> searchBoostingAlgorithmVO
		searchBoostingAlgorithmVO.getOmnitureEventRequired() >> true
		searchBoostingAlgorithmVO.getOmnitureDescription() >> "OmnitureDescription#qwer#asdf"
		sQueryMock.isEPHFound() >> true
		when:
		String value  = testObjCatalogTools.getOmnitureVariable(requestMock)
		then:
		value.equals("En:OmnitureDescription#qwer#asdf:OmnitureDescription#qwer#asdf:EPHBoostingScheme")
	}
	def" Method will return a omniture value, which is dependent on siteSpect header value,EPHBoostingScheme  is null"(){
		given:
		requestMock.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO) >> sQueryMock
		eSearchUtil.getEphLookUpUtil() >> lookupUtilMock
		lookupUtilMock.isEpHLookUpEnable() >> true
		sQueryMock.isFromBrandPage() >> false
		sQueryMock.getKeyWord() >> "keyword"
		1*eSearchUtil.getEPHBoostingScheme(sQueryMock) >>  null
		requestMock.getParameter(BBBCoreConstants.PAGE_NAME) >> BBBCoreConstants.SEARCH
		requestMock.getSession() >> sessionMock
		sessionMock.getAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE) >> null
		sessionMock.getAttribute(BBBCoreConstants.VENDOR_PARAM) >> BBBCoreConstants.VENDOR_PARAM
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.VENDOR_FLAG,BBBCoreConstants.FALSE)>> true
		when:
		String value  = testObjCatalogTools.getOmnitureVariable(requestMock)
		then:
		value.equals(":00:00:00")
	}
	def" Method will return a omniture value, which is dependent on siteSpect header value,page type is L2L3"(){
		given:
		requestMock.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO) >> sQueryMock
		sQueryMock.isFromBrandPage() >> true
		sQueryMock.getKeyWord() >> "keyword"
		0*eSearchUtil.getEPHBoostingScheme(sQueryMock) >>  null
		requestMock.getParameter(BBBCoreConstants.PAGE_NAME) >> BBBCoreConstants.L2L3
		requestMock.getSession() >> sessionMock
		sessionMock.getAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE) >> null
		sessionMock.getAttribute(BBBCoreConstants.VENDOR_PARAM) >> BBBCoreConstants.VENDOR_PARAM
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.VENDOR_FLAG,BBBCoreConstants.FALSE)>> true
		sQueryMock.isEphApplied() >> true
		when:
		String value  = testObjCatalogTools.getOmnitureVariable(requestMock)
		then:
		value.equals("")
	}
	def" Method will return a omniture value, which is dependent on siteSpect header value,page type is brand"(){
		given:
		requestMock.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO) >> sQueryMock
		sQueryMock.isFromBrandPage() >> true
		sQueryMock.getKeyWord() >> "keyword"
		0*eSearchUtil.getEPHBoostingScheme(sQueryMock) >>  null
		requestMock.getParameter(BBBCoreConstants.PAGE_NAME) >> BBBCoreConstants.BRAND
		requestMock.getSession() >> sessionMock
		sessionMock.getAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE) >> null
		sessionMock.getAttribute(BBBCoreConstants.VENDOR_PARAM) >> null
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.VENDOR_FLAG,BBBCoreConstants.FALSE)>> true
		sQueryMock.isEphApplied() >> true
		when:
		String value  = testObjCatalogTools.getOmnitureVariable(requestMock)
		then:
		value.equals("")
	}
	def" Method will return a omniture value, which is dependent on siteSpect header value,vendor flag is false"(){
		given:
		requestMock.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO) >> null
		eSearchUtil.getEphLookUpUtil() >> lookupUtilMock
		lookupUtilMock.isEpHLookUpEnable() >> true
		sQueryMock.isFromBrandPage() >> false
		sQueryMock.getKeyWord() >> "keyword"
		0*eSearchUtil.getEPHBoostingScheme(sQueryMock) >>  "EPHBoostingScheme"
		requestMock.getParameter(BBBCoreConstants.PAGE_NAME) >> BBBCoreConstants.SEARCH
		requestMock.getSession() >> sessionMock
		sessionMock.getAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE) >> BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE
		sessionMock.getAttribute(BBBCoreConstants.VENDOR_PARAM) >> BBBCoreConstants.VENDOR_PARAM
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.VENDOR_FLAG,BBBCoreConstants.FALSE)>> false
		1*eSearchTools.getSearchBoostingAlgorithmsFromLocalMap(_) >> null
		searchBoostingAlgorithmVO.getOmnitureEventRequired() >> true
		searchBoostingAlgorithmVO.getOmnitureDescription() >> "OmnitureDescription#qwer#asdf"
		sQueryMock.isEPHFound() >> true
		when:
		String value  = testObjCatalogTools.getOmnitureVariable(requestMock)
		then:
		value.equals("control:")
	}
	def" Method will return a omniture value, which is dependent on siteSpect header value,omniture event not Required"(){
		given:
		requestMock.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO) >> sQueryMock
		eSearchUtil.getEphLookUpUtil() >> lookupUtilMock
		lookupUtilMock.isEpHLookUpEnable() >> true
		sQueryMock.isFromBrandPage() >> true
		sQueryMock.getKeyWord() >> "keyword"
		0*eSearchUtil.getEPHBoostingScheme(sQueryMock) >>  "EPHBoostingScheme"
		requestMock.getParameter(BBBCoreConstants.PAGE_NAME) >> BBBCoreConstants.SEARCH
		requestMock.getSession() >> sessionMock
		sessionMock.getAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE) >> BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE
		sessionMock.getAttribute(BBBCoreConstants.VENDOR_PARAM) >> BBBCoreConstants.VENDOR_PARAM
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.VENDOR_FLAG,BBBCoreConstants.FALSE)>> false
		1*eSearchTools.getSearchBoostingAlgorithmsFromLocalMap(_) >> searchBoostingAlgorithmVO
		searchBoostingAlgorithmVO.getOmnitureEventRequired() >> false
		sQueryMock.isEPHFound() >> true
		when:
		String value  = testObjCatalogTools.getOmnitureVariable(requestMock)
		then:
		value.equals("control:")
	}
	def" Method will return a omniture value, which is dependent on siteSpect header value,OmnitureDescription is empty"(){
		given:
		requestMock.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO) >> sQueryMock
		eSearchUtil.getEphLookUpUtil() >> lookupUtilMock
		lookupUtilMock.isEpHLookUpEnable() >> false
		sQueryMock.isFromBrandPage() >> false
		sQueryMock.getKeyWord() >> "keyword"
		0*eSearchUtil.getEPHBoostingScheme(sQueryMock) >>  "EPHBoostingScheme"
		requestMock.getParameter(BBBCoreConstants.PAGE_NAME) >> BBBCoreConstants.SEARCH
		requestMock.getSession() >> sessionMock
		sessionMock.getAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE) >> BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE
		sessionMock.getAttribute(BBBCoreConstants.VENDOR_PARAM) >> BBBCoreConstants.VENDOR_PARAM
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.VENDOR_FLAG,BBBCoreConstants.FALSE)>> false
		1*eSearchTools.getSearchBoostingAlgorithmsFromLocalMap(_) >> searchBoostingAlgorithmVO
		searchBoostingAlgorithmVO.getOmnitureEventRequired() >> true
		sQueryMock.isEPHFound() >> true
		when:
		String value  = testObjCatalogTools.getOmnitureVariable(requestMock)
		then:
		value.equals("control:")
	}
	def" Method will return a omniture value, which is dependent on siteSpect header value,search key word is null"(){
		given:
		requestMock.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO) >> sQueryMock
		eSearchUtil.getEphLookUpUtil() >> lookupUtilMock
		lookupUtilMock.isEpHLookUpEnable() >> true
		sQueryMock.isFromBrandPage() >> false
		sQueryMock.getKeyWord() >> null
		0*eSearchUtil.getEPHBoostingScheme(sQueryMock) >>  "EPHBoostingScheme"
		requestMock.getParameter(BBBCoreConstants.PAGE_NAME) >> BBBCoreConstants.SEARCH
		requestMock.getSession() >> sessionMock
		sessionMock.getAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE) >> BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE
		sessionMock.getAttribute(BBBCoreConstants.VENDOR_PARAM) >> BBBCoreConstants.VENDOR_PARAM
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.VENDOR_FLAG,BBBCoreConstants.FALSE)>> false
		1*eSearchTools.getSearchBoostingAlgorithmsFromLocalMap(_) >> searchBoostingAlgorithmVO
		searchBoostingAlgorithmVO.getOmnitureEventRequired() >> true
		sQueryMock.isEPHFound() >> true
		when:
		String value  = testObjCatalogTools.getOmnitureVariable(requestMock)
		then:
		value.equals("control:")
	}
	def" Method will return a omniture value, which is dependent on siteSpect header value,page type is not search"(){
		given:
		requestMock.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO) >> sQueryMock
		eSearchUtil.getEphLookUpUtil() >> lookupUtilMock
		lookupUtilMock.isEpHLookUpEnable() >> true
		sQueryMock.isFromBrandPage() >> false
		sQueryMock.getKeyWord() >> null
		0*eSearchUtil.getEPHBoostingScheme(sQueryMock) >>  "EPHBoostingScheme"
		requestMock.getParameter(BBBCoreConstants.PAGE_NAME) >>""
		requestMock.getSession() >> sessionMock
		sessionMock.getAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE) >> BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE
		sessionMock.getAttribute(BBBCoreConstants.VENDOR_PARAM) >> BBBCoreConstants.VENDOR_PARAM
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.VENDOR_FLAG,BBBCoreConstants.FALSE)>> false
		1*eSearchTools.getSearchBoostingAlgorithmsFromLocalMap(_) >> searchBoostingAlgorithmVO
		searchBoostingAlgorithmVO.getOmnitureEventRequired() >> true
		sQueryMock.isEPHFound() >> true
		when:
		String value  = testObjCatalogTools.getOmnitureVariable(requestMock)
		then:
		value.equals("control")
	}
	def" Method will return a omniture value, which is dependent on siteSpect header value,ephQueryScheme is not off"(){
		given:
		requestMock.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO) >> sQueryMock
		eSearchUtil.getEphLookUpUtil() >> lookupUtilMock
		lookupUtilMock.isEpHLookUpEnable() >> true
		sQueryMock.isFromBrandPage() >> false
		sQueryMock.getKeyWord() >> "keyword"
		2*eSearchUtil.getEPHBoostingScheme(sQueryMock) >>  "off"
		requestMock.getParameter(BBBCoreConstants.PAGE_NAME) >> BBBCoreConstants.SEARCH
		requestMock.getSession() >> sessionMock
		sessionMock.getAttribute(BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE) >> BBBEndecaConstants.BOOST_ALGORITHM_BOOST_CODE
		sessionMock.getAttribute(BBBCoreConstants.VENDOR_PARAM) >> BBBCoreConstants.VENDOR_PARAM
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.VENDOR_KEYS, BBBCoreConstants.VENDOR_FLAG,BBBCoreConstants.FALSE)>> true
		1*eSearchTools.getSearchBoostingAlgorithmsFromLocalMap(_) >> searchBoostingAlgorithmVO
		searchBoostingAlgorithmVO.getOmnitureEventRequired() >> true
		searchBoostingAlgorithmVO.getOmnitureDescription() >> "OmnitureDescription#qwer#asdf"
		sQueryMock.isEPHFound() >> false
		when:
		String value  = testObjCatalogTools.getOmnitureVariable(requestMock)
		then:
		value.equals("En:OmnitureDescription#qwer#asdf:OmnitureDescription#qwer#asdf:off")
	}
	def"SKU is not active If the calculateAboveBelowLine flag is true then the Above or Below Line logic is calculated for each SKU."()
	{
		given:
		catalogRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>repositoryItemMock
		1*globalRepositoryToolsMock.isSkuActive(repositoryItemMock,[]) >> true
		testObjCatalogTools.getSKUDetailVO(repositoryItemMock, _, true, false) >>new  SKUDetailVO()
		when:
		SKUDetailVO sVO = testObjCatalogTools.getSKUDynamicPriceDetails(["skuId":"sku12345","calculateAboveBelowLine" : true,"formCart":false])
		then:
		sVO != null
	}
	def"SKU is not active If the calculateAboveBelowLine flag is true then the Above or Below Line logic is calculated for each SKU,sku is not active"()
	{
		given:
		catalogRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>repositoryItemMock
		1*globalRepositoryToolsMock.isSkuActive(repositoryItemMock,[]) >> false
		testObjCatalogTools.getSKUDetailVO(repositoryItemMock, _, true, false) >>new  SKUDetailVO()
		when:
		SKUDetailVO sVO = testObjCatalogTools.getSKUDynamicPriceDetails(["skuId":"sku12345","calculateAboveBelowLine" : true,"fromCart":false])
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1003:1003")
	}
	def"SKU is not active If the calculateAboveBelowLine flag is true then the Above or Below Line logic is calculated for each SKU,skuid and site id is null"()
	{
		given:
		0*globalRepositoryToolsMock.isSkuActive(repositoryItemMock,[]) >> true
		testObjCatalogTools.getSKUDetailVO(repositoryItemMock, _, true, false) >>new  SKUDetailVO()
		when:
		SKUDetailVO sVO = testObjCatalogTools.getSKUDynamicPriceDetails([:])
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"SKU is not active If the calculateAboveBelowLine flag is true then the Above or Below Line logic is calculated for each SKU,throw respository exception"()
	{
		given:
		catalogRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>{throw new RepositoryException("Mock of repositoory exception")}
		0*globalRepositoryToolsMock.isSkuActive(repositoryItemMock,[]) >> false
		testObjCatalogTools.getSKUDetailVO(repositoryItemMock, _, true, false) >>new  SKUDetailVO()
		when:
		SKUDetailVO sVO = testObjCatalogTools.getSKUDynamicPriceDetails(["skuId":"sku12345","calculateAboveBelowLine" : true,"fromCart":false])
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"SKU is not active If the calculateAboveBelowLine flag is true then the Above or Below Line logic is calculated for each SKU,sku not available"()
	{
		given:
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>null
		0*globalRepositoryToolsMock.isSkuActive(repositoryItemMock,[]) >> false
		testObjCatalogTools.getSKUDetailVO(repositoryItemMock, _, true, false) >>new  SKUDetailVO()
		when:
		SKUDetailVO sVO = testObjCatalogTools.getSKUDynamicPriceDetails(["siteId":"siteId","calculateAboveBelowLine" : true,"fromCart":false])
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1000:1000")
	}
	def"Store-incart price on kickstarter page Update shipping message flag for incarteligible skus"(){
		given:
		skuDetailVOMock.isLtlItem() >> false
		sessionBeanMock.isInternationalShippingContext() >> false
		1*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, _, null,_) >> "5"
		skuDetailVOMock.getParentProdId() >> "prd1234"
		skuDetailVOMock.getSkuId() >> "sku1234"
		1*priceListRepositoryToolsMock.getIncartPrice("prd1234", "sku1234") >> 6
		1*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, _)>>"shipDisplaymsg"
		when:
		testObjCatalogTools.updateShippingMessageFlag(skuDetailVOMock, true)
		then:
		1*skuDetailVOMock.setDisplayShipMsg("shipDisplaymsg")
		1*skuDetailVOMock.setShipMsgFlag(true)
	}
	def"Store-incart price on kickstarter page Update shipping message flag for incarteligible skus,cartprice is less than Threshhold"(){
		given:
		skuDetailVOMock.isLtlItem() >> false
		sessionBeanMock.isInternationalShippingContext() >> false
		1*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, _, null,_) >> "5"
		skuDetailVOMock.getParentProdId() >> "prd1234"
		skuDetailVOMock.getSkuId() >> "sku1234"
		priceListRepositoryToolsMock.getIncartPrice("prd1234", "sku1234") >> 3
		0*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, _)>>"shipDisplaymsg"
		when:
		testObjCatalogTools.updateShippingMessageFlag(skuDetailVOMock, true)
		then:
		1*skuDetailVOMock.setDisplayShipMsg("")
		1*skuDetailVOMock.setShipMsgFlag(false)
	}
	def"Store-incart price on kickstarter page Update shipping message flag for incarteligible skus,Threshhold is charcters"(){
		given:
		skuDetailVOMock.isLtlItem() >> false
		sessionBeanMock.isInternationalShippingContext() >> false
		1*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, _, null,_) >> "asasasdsa"
		skuDetailVOMock.getParentProdId() >> "prd1234"
		skuDetailVOMock.getSkuId() >> "sku1234"
		1*priceListRepositoryToolsMock.getIncartPrice("prd1234", "sku1234") >> 3
		1*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, _)>>"shipDisplaymsg"
		when:
		testObjCatalogTools.updateShippingMessageFlag(skuDetailVOMock, true)
		then:
		1*skuDetailVOMock.setDisplayShipMsg("shipDisplaymsg")
		1*skuDetailVOMock.setShipMsgFlag(true)
	}
	def"Store-incart price on kickstarter page Update shipping message flag for incarteligible skus,Threshhold"(){
		given:
		skuDetailVOMock.isLtlItem() >> false
		sessionBeanMock.isInternationalShippingContext() >> false
		1*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, _, null,_) >> null
		skuDetailVOMock.getParentProdId() >> "prd1234"
		skuDetailVOMock.getSkuId() >> "sku1234"
		0*priceListRepositoryToolsMock.getIncartPrice("prd1234", "sku1234") >> 3
		0*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, _)>>"shipDisplaymsg"
		when:
		testObjCatalogTools.updateShippingMessageFlag(skuDetailVOMock, true)
		then:
		0*skuDetailVOMock.setDisplayShipMsg("")
		0*skuDetailVOMock.setShipMsgFlag(false)
	}
	def"Store-incart price on kickstarter page Update shipping message flag for incarteligible skus,not eligible in cart"(){
		given:
		skuDetailVOMock.isLtlItem() >> false
		sessionBeanMock.isInternationalShippingContext() >> false
		0*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, _, null,_) >> "5"
		skuDetailVOMock.getParentProdId() >> "prd1234"
		skuDetailVOMock.getSkuId() >> "sku1234"
		0*priceListRepositoryToolsMock.getIncartPrice("prd1234", "sku1234") >> 6
		0*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, _)>>"shipDisplaymsg"
		when:
		testObjCatalogTools.updateShippingMessageFlag(skuDetailVOMock, false)
		then:
		0*skuDetailVOMock.setDisplayShipMsg("shipDisplaymsg")
		0*skuDetailVOMock.setShipMsgFlag(true)
	}
	def"Store-incart price on kickstarter page Update shipping message flag for incarteligible skus,profile is transient"(){
		given:
		skuDetailVOMock.isLtlItem() >> false
		sessionBeanMock.isInternationalShippingContext() >> false
		0*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, _, null,_) >> "5"
		skuDetailVOMock.getParentProdId() >> "prd1234"
		skuDetailVOMock.getSkuId() >> "sku1234"
		0*priceListRepositoryToolsMock.getIncartPrice("prd1234", "sku1234") >> 6
		0*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, _)>>"shipDisplaymsg"
		pMock.isTransient() >> true
		when:
		testObjCatalogTools.updateShippingMessageFlag(skuDetailVOMock, true)
		then:
		0*skuDetailVOMock.setDisplayShipMsg("shipDisplaymsg")
		0*skuDetailVOMock.setShipMsgFlag(true)
	}
	def"Store-incart price on kickstarter page Update shipping message flag for incarteligible skus is ltl item"(){
		given:
		skuDetailVOMock.isLtlItem() >> true
		sessionBeanMock.isInternationalShippingContext() >> false
		when:
		testObjCatalogTools.updateShippingMessageFlag(skuDetailVOMock, false)
		then:
		0*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, _, null,_)
	}
	def"Store-incart price on kickstarter page Update shipping message flag for incarteligible skus,international order"(){
		given:
		skuDetailVOMock.isLtlItem() >> false
		sessionBeanMock.isInternationalShippingContext() >> true
		when:
		testObjCatalogTools.updateShippingMessageFlag(skuDetailVOMock, false)
		then:
		0*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, _, null,_)
	}
	def"Store-incart price on kickstarter page Update shipping message flag for incarteligible skus,current request is null"(){
		given:
		skuDetailVOMock.isLtlItem() >> false
		ServletUtil.setCurrentRequest(null)
		when:
		testObjCatalogTools.updateShippingMessageFlag(skuDetailVOMock, false)
		then:
		0*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD, _, null,_)
	}
	def"New Method to optimze SKUDetailVO"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"US"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "true"
		priceSKUCacheMock.isSkuCacheReady() >> true
		1*priceSKUCacheMock.lookUPSKUItemInCache("sku_sku1234") >> priceSkuVO
		priceSkuVO.setCaPricingLabelCode(null)
		priceSkuVO.setCaIncartFlag(true)
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> BBBCoreConstants.SITE_BAB_CA
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",false)
		then:
		priceSkuVO != null
		priceSkuVO.getPricingLabelCode()== null
		priceSkuVO.getInCartFlag()
		priceSkuVO.isDynamicPriceSKU()
	}
	def"New Method to optimze SKUDetailVO,site id is BuyBuyBaby"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"US"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "true"
		priceSKUCacheMock.isSkuCacheReady() >> true
		1*priceSKUCacheMock.lookUPSKUItemInCache("sku_sku1234") >> priceSkuVO
		priceSkuVO.setBabyPricingLabelCode("BabyPricingLabelCode")
		priceSkuVO.setBabyIncartFlag(true)
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> BBBCoreConstants.SITE_BBB
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",false)
		then:
		priceSkuVO != null
		priceSkuVO.getBabyPricingLabelCode().equals("BabyPricingLabelCode")
		priceSkuVO.isBabyIncartFlag()
		priceSkuVO.isDynamicPriceSKU()
	}
	def"New Method to optimze SKUDetailVO,site id is TBS_BuyBuyBaby"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"US"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "true"
		priceSKUCacheMock.isSkuCacheReady() >> true
		1*priceSKUCacheMock.lookUPSKUItemInCache("sku_sku1234") >> priceSkuVO
		priceSkuVO.setBabyPricingLabelCode(null)
		priceSkuVO.setBabyIncartFlag(true)
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> BBBCoreConstants.SITEBAB_BABY_TBS
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",false)
		then:
		priceSkuVO != null
		priceSkuVO.getBabyPricingLabelCode()== null
		priceSkuVO.isBabyIncartFlag()
		priceSkuVO.isDynamicPriceSKU()
	}
	def"New Method to optimze SKUDetailVO,site id is BedBathUS"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"US"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "true"
		priceSKUCacheMock.isSkuCacheReady() >> true
		1*priceSKUCacheMock.lookUPSKUItemInCache("sku_sku1234") >> priceSkuVO
		priceSkuVO.setBbbPricingLabelCode("BbbPricingLabelCode")
		priceSkuVO.setBbbIncartFlag(true)
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >>"BedBathUS"
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",false)
		then:
		priceSkuVO != null
		priceSkuVO.getBbbPricingLabelCode().equals("BbbPricingLabelCode")
		priceSkuVO.isBbbIncartFlag()
		priceSkuVO.isDynamicPriceSKU()
	}
	def"New Method to optimze SKUDetailVO,site id is TBS_BedBathUS"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"US"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "true"
		priceSKUCacheMock.isSkuCacheReady() >> true
		1*priceSKUCacheMock.lookUPSKUItemInCache("sku_sku1234") >> priceSkuVO
		priceSkuVO.setBbbPricingLabelCode(null)
		priceSkuVO.setBbbIncartFlag(true)
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "TBS_BedBathUS"
		when:
		BBBDynamicPriceSkuVO pBbbIncartFlag= testObjCatalogTools.getDynamicPriceSKUVO("sku1234",false)
		then:
		priceSkuVO != null
		priceSkuVO.getBbbPricingLabelCode()== null
		priceSkuVO.isBbbIncartFlag()
		priceSkuVO.isDynamicPriceSKU()
	}
	def"New Method to optimze SKUDetailVO,country is mexico"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"MX"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "true"
		priceSKUCacheMock.isSkuCacheReady() >> false
		0*priceSKUCacheMock.lookUPSKUItemInCache("sku_sku1234")
		priceSkuVO.setMxPricingLabelCode("CaPricingLabelCode")
		priceSkuVO.setMxIncartFlag(true)
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> BBBInternationalShippingConstants.COUNTRY_MEXICO
		1*priceCacheContainer.get("sku_sku1234") >> priceSkuVO
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",false)
		then:
		priceSkuVO != null
		priceSkuVO.getMxPricingLabelCode().equals("CaPricingLabelCode")
		priceSkuVO.isMxIncartFlag()
		priceSkuVO.isDynamicPriceSKU()
	}
	def"New Method to optimze SKUDetailVO,country is mexico ,label is null"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"MX"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "true"
		priceSKUCacheMock.isSkuCacheReady() >> false
		0*priceSKUCacheMock.lookUPSKUItemInCache("sku_sku1234")
		priceSkuVO.setMxPricingLabelCode(null)
		priceSkuVO.setMxIncartFlag(true)
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> BBBInternationalShippingConstants.COUNTRY_MEXICO
		1*priceCacheContainer.get("sku_sku1234") >> priceSkuVO
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",false)
		then:
		priceSkuVO != null
		priceSkuVO.getMxPricingLabelCode()==null
		priceSkuVO.isMxIncartFlag()
		priceSkuVO.isDynamicPriceSKU()
	}
	def"New Method to optimze SKUDetailVO, site id is TBS_BedBathCanada"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"US"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "false"
		priceSKUCacheMock.isSkuCacheReady() >> true
		0*priceSKUCacheMock.lookUPSKUItemInCache("sku_sku1234") >> priceSkuVO
		priceSkuVO.setCaPricingLabelCode("CaPricingLabelCode")
		priceSkuVO.setCaIncartFlag(true)
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> BBBCoreConstants.SITEBAB_CA_TBS
		testObjCatalogTools.setSkuCacheContainer(null)
		testObjCatalogTools.setDynamicPriceRepository(repositoryMock)
		repositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_PRICE_ITEM)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG_SKU) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE_SKU) >> BBBCatalogConstants.BABY_PRICING_LABEL_CODE_SKU
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG_SKU) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE_SKU) >> BBBCatalogConstants.BBB_PRICING_LABEL_CODE_SKU
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG_SKU)>> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE_SKU)>>BBBCatalogConstants.CA_PRICING_LABEL_CODE_SKU
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG_SKU) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE_SKU) >> BBBCatalogConstants.MX_PRICING_LABEL_CODE_SKU
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",true)
		then:
		priceSkuVO != null
		priceSkuVO.getPricingLabelCode().equals("caPricingLabelCode")
		priceSkuVO.getInCartFlag()
		priceSkuVO.isDynamicPriceSKU()
	}
	def"New Method to optimze SKUDetailVO, site id is TBS_BedBathCanada throw repository exception"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"US"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> null
		priceSKUCacheMock.isSkuCacheReady() >> true
		0*priceSKUCacheMock.lookUPSKUItemInCache("sku_sku1234") >> priceSkuVO
		priceSkuVO.setCaPricingLabelCode("CaPricingLabelCode")
		priceSkuVO.setCaIncartFlag(true)
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> BBBCoreConstants.SITEBAB_CA_TBS
		testObjCatalogTools.setSkuCacheContainer(null)
		testObjCatalogTools.setDynamicPriceRepository(repositoryMock)
		repositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_PRICE_ITEM)>> {throw new RepositoryException()}
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",true)
		then:
		priceSkuVO == null
	}
	def"New Method to optimze SKUDetailVO, sku id not available"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"CA"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "false"
		priceSKUCacheMock.isSkuCacheReady() >> true
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> BBBCoreConstants.SITEBAB_CA_TBS
		testObjCatalogTools.setSkuCacheContainer(null)
		testObjCatalogTools.setDynamicPriceRepository(repositoryMock)
		repositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_PRICE_ITEM)>> null
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",true)
		then:
		priceSkuVO == null
	}
	def"New Method to optimze SKUDetailVO, from cart is true"(){
		given:
		1*globalRepositoryToolsMock.returnCountryFromSession()>>null
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "false"
		priceSKUCacheMock.isSkuCacheReady() >> true
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> BBBCoreConstants.SITEBAB_CA_TBS
		testObjCatalogTools.setSkuCacheContainer(null)
		testObjCatalogTools.setDynamicPriceRepository(repositoryMock)
		repositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_PRICE_ITEM)>> null
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",false)
		then:
		priceSkuVO == null
	}
	def"New Method to optimze SKUDetailVO, country is Canda"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"CA"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "true"
		priceSKUCacheMock.isSkuCacheReady() >> true
		1*priceSKUCacheMock.lookUPSKUItemInCache("sku_sku1234") >> priceSkuVO
		priceSkuVO.setBabyIncartFlag(true)
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> BBBCoreConstants.SITE_BBB
		testObjCatalogTools.setSkuCacheContainer(null)
		testObjCatalogTools.setDynamicPriceRepository(repositoryMock)
		repositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_PRICE_ITEM)>> null
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",false)
		then:
		priceSkuVO != null
		priceSkuVO.isBabyIncartFlag()
		priceSkuVO.getInCartFlag()
	}
	def"New Method to optimze SKUDetailVO, country is Canda and site id is TBS_BuyBuyBaby"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"CA"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "true"
		priceSKUCacheMock.isSkuCacheReady() >> true
		1*priceSKUCacheMock.lookUPSKUItemInCache("sku_sku1234") >> priceSkuVO
		priceSkuVO.setBabyIncartFlag(true)
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "TBS_BuyBuyBaby"
		testObjCatalogTools.setSkuCacheContainer(null)
		testObjCatalogTools.setDynamicPriceRepository(repositoryMock)
		repositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_PRICE_ITEM)>> null
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",false)
		then:
		priceSkuVO != null
		priceSkuVO.isBabyIncartFlag()
		priceSkuVO.getInCartFlag()
	}
	def"New Method to optimze SKUDetailVO, country is Canda and site id is TBS_BedBathUS"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"CA"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "true"
		priceSKUCacheMock.isSkuCacheReady() >> true
		1*priceSKUCacheMock.lookUPSKUItemInCache("sku_sku1234") >> priceSkuVO
		priceSkuVO.setBbbIncartFlag(true)
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "TBS_BedBathUS"
		testObjCatalogTools.setSkuCacheContainer(null)
		testObjCatalogTools.setDynamicPriceRepository(repositoryMock)
		repositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_PRICE_ITEM)>> null
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",false)
		then:
		priceSkuVO != null
		priceSkuVO.isBbbIncartFlag()
		priceSkuVO.getInCartFlag()
	}
	def"New Method to optimze SKUDetailVO, country is Canda and site id is BedBathUS"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"CA"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "true"
		priceSKUCacheMock.isSkuCacheReady() >> true
		1*priceSKUCacheMock.lookUPSKUItemInCache("sku_sku1234") >> priceSkuVO
		priceSkuVO.setBbbIncartFlag(true)
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "BedBathUS"
		testObjCatalogTools.setSkuCacheContainer(null)
		testObjCatalogTools.setDynamicPriceRepository(repositoryMock)
		repositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_PRICE_ITEM)>> null
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",false)
		then:
		priceSkuVO != null
		priceSkuVO.isBbbIncartFlag()
		priceSkuVO.getInCartFlag()
	}
	def"New Method to optimze SKUDetailVO, country is Canda and site id is empty"(){
		given:
		2*globalRepositoryToolsMock.returnCountryFromSession()>>"CA"
		testObjCatalogTools.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_LOCAL_SKU_DYNAMIC_PRICING_KEY, _) >> "true"
		priceSKUCacheMock.isSkuCacheReady() >> true
		1*priceSKUCacheMock.lookUPSKUItemInCache("sku_sku1234") >> priceSkuVO
		priceSkuVO.setBbbIncartFlag(true)
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> ""
		testObjCatalogTools.setSkuCacheContainer(null)
		testObjCatalogTools.setDynamicPriceRepository(repositoryMock)
		repositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_PRICE_ITEM)>> null
		when:
		BBBDynamicPriceSkuVO priceSkuVO = testObjCatalogTools.getDynamicPriceSKUVO("sku1234",false)
		then:
		priceSkuVO != null
		priceSkuVO.getInCartFlag() == false
	}
	def"removePhantomCategory.TC for removePhantomCategory"(){
		given:

		FacetRefinementVO frVOcl1= new FacetRefinementVO()
		FacetRefinementVO frVOcl2= new FacetRefinementVO()
		FacetRefinementVO frVOcl3= new FacetRefinementVO()
		FacetRefinementVO frVOcl4= new FacetRefinementVO()
		FacetRefinementVO frVOcl5= new FacetRefinementVO()
		FacetRefinementVO frVO= new FacetRefinementVO()
		FacetRefinementVO frVO1= new FacetRefinementVO()
		FacetRefinementVO frVO2= new FacetRefinementVO()
		FacetRefinementVO frVO3= new FacetRefinementVO()
		FacetRefinementVO frVO4= new FacetRefinementVO()
		frVOcl1.setFacetsRefinementVOs([frVO1])
		frVOcl2.setFacetsRefinementVOs([frVO2])
		frVOcl3.setFacetsRefinementVOs([frVO3])
		frVOcl4.setFacetsRefinementVOs([frVO4])
		frVOcl5.setFacetsRefinementVOs([frVO4])
		frVO.setCatalogId("cat12345")
		frVO1.setCatalogId("cat12345")
		frVO2.setCatalogId("cat12345")
		frVO3.setCatalogId("cat12345")
		frVO4.setCatalogId("cat12345")
		frVOcl1.setCatalogId("cat123456")
		frVOcl2.setCatalogId("cat123456")
		frVOcl3.setCatalogId("cat123456")
		frVOcl4.setCatalogId("cat123456")
		frVOcl5.setCatalogId("cat123456")
		CategoryVO parentCategoryVOMock = Mock()
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat12345", false) >> null >> categoryVOMock
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat123456", false) >> null >> parentCategoryVOMock
		categoryVOMock.getPhantomCategory() >> null >> false>>false >> true
		categoryVOMock.getCategoryId() >> "cat12345"
		parentCategoryVOMock.getPhantomCategory() >> null >> false>>false >> true
		parentCategoryVOMock.getCategoryId() >> "cat123456"
		testObjCatalogTools.isFirstLevelCategory("cat12345", "BedBathUS") >> true >> false
		testObjCatalogTools.isFirstLevelCategory("cat123456", "BedBathUS") >> true >> false
		List facetRefinementVO = [null,frVO,frVOcl1,frVOcl2,frVOcl3,frVOcl4,frVOcl5]
		when:
		testObjCatalogTools.removePhantomCategory(facetRefinementVO, "BedBathUS")
		then:
		facetRefinementVO.size() ==6
		frVOcl4.getFacetsRefinementVOs().size() == 0
	}
	def"removePhantomCategory.TC for removePhantomCategory throw business exception"(){
		given:

		FacetRefinementVO frVO= new FacetRefinementVO()
		frVO.setCatalogId("cat12345")
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat12345", false) >>  {throw new BBBBusinessException("Mock of business exception")}
		categoryVOMock.getCategoryId() >> "cat12345"
		List facetRefinementVO = [frVO]
		when:
		testObjCatalogTools.removePhantomCategory(facetRefinementVO, "BedBathUS")
		then:
		1*testObjCatalogTools.logError("Error thrown from BBBCatalogTools.removePhantomCategory ",_)
	}
	def"removePhantomCategoryCLP.TC for removePhantomCategoryCLP"(){
		given:
		CategoryRefinementVO frVOcl1= new CategoryRefinementVO()
		CategoryRefinementVO frVOcl2= new CategoryRefinementVO()
		CategoryRefinementVO frVOcl3= new CategoryRefinementVO()
		CategoryRefinementVO frVOcl4= new CategoryRefinementVO()
		CategoryRefinementVO frVOcl5= new CategoryRefinementVO()
		frVOcl1.setQuery("cat123456")
		frVOcl2.setQuery("cat123456")
		frVOcl3.setQuery("cat123456")
		frVOcl4.setQuery("cat123456")
		frVOcl5.setQuery("cat123456")
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat123456", false) >> null >> categoryVOMock
		categoryVOMock.getPhantomCategory() >> null >> false >> false >> true
		categoryVOMock.getCategoryId() >> "cat123456"
		testObjCatalogTools.isFirstLevelCategory("cat123456", "BedBathUS") >> true >> false
		List facetRefinementVO = [null,frVOcl1,frVOcl2,frVOcl3,frVOcl4,frVOcl5]
		when:
		testObjCatalogTools.removePhantomCategoryCLP(facetRefinementVO, "BedBathUS")
		then:
		facetRefinementVO.size() ==5
	}
	def"removePhantomCategoryCLP.TC for removePhantomCategoryCLP throw business exception"(){
		given:
		CategoryRefinementVO frVO= new CategoryRefinementVO()
		frVO.setQuery("cat12345")
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat12345", false) >>  {throw new BBBBusinessException("Mock of business exception")}
		categoryVOMock.getCategoryId() >> "cat12345"
		List facetRefinementVO = [frVO]
		when:
		testObjCatalogTools.removePhantomCategoryCLP(facetRefinementVO, "BedBathUS")
		then:
		1*testObjCatalogTools.logError("Error thrown from BBBCatalogTools.removePhantomCategoryCLP ",_)
	}
	def"The method checks if the category id is a top level category To check this all the parent category ids of the category is added in a list"(){
		given:
		catalogRepositoryMock.getItem("categoryId", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "siteIdRootCategory")>>["rep1234"]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.FIXED_PARENT_CATEGORIES_PROPERTY_NAME) >> [repositoryItemMock,repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >>  "rep1234"
		when:
		boolean valid = testObjCatalogTools.isFirstLevelCategory("categoryId", "siteId")
		then:
		valid
	}
	def"The method checks if the category id is a top level category To check this all the parent category ids of the category is added in a list,parent category item is null"(){
		given:
		catalogRepositoryMock.getItem("categoryId", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "siteIdRootCategory")>>["rep1234"]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.FIXED_PARENT_CATEGORIES_PROPERTY_NAME) >> null
		repositoryItemMock.getRepositoryId() >>  "rep1234"
		when:
		boolean valid = testObjCatalogTools.isFirstLevelCategory("categoryId", "siteId")
		then:
		valid == false
	}
	def"The method checks if the category id is a top level category To check this all the parent category ids of the category is added in a list,parent category item is empty"(){
		given:
		catalogRepositoryMock.getItem("categoryId", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "siteIdRootCategory")>>["rep1234"]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.FIXED_PARENT_CATEGORIES_PROPERTY_NAME) >> [].toSet()
		repositoryItemMock.getRepositoryId() >>  "rep1234"
		when:
		boolean valid = testObjCatalogTools.isFirstLevelCategory("categoryId", "siteId")
		then:
		valid == false
	}
	def"The method checks if the category id is a top level category To check this all the parent category ids of the category is added in a list,parent category item is not fixed category"(){
		given:
		catalogRepositoryMock.getItem("categoryId", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "siteIdRootCategory")>>["rep12345"]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.FIXED_PARENT_CATEGORIES_PROPERTY_NAME) >> [repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >>  "rep1234"
		when:
		boolean valid = testObjCatalogTools.isFirstLevelCategory("categoryId", "siteId")
		then:
		valid == false
	}
	def"The method checks if the category id is a top level category To check this all the parent category ids of the category is added in a list,category id is null"(){
		when:
		boolean valid = testObjCatalogTools.isFirstLevelCategory(null, "siteId")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"The method checks if the category id is a top level category To check this all the parent category ids of the category is added in a list,site id is null"(){
		when:
		boolean valid = testObjCatalogTools.isFirstLevelCategory("categoryId", null)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"The method checks if the category id is a top level category To check this all the parent category ids of the category is added in a list,category item is not available"(){
		given:
		catalogRepositoryMock.getItem("categoryId", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> null
		when:
		boolean valid = testObjCatalogTools.isFirstLevelCategory("categoryId", "siteId")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1001:1001")
	}
	def"The method checks if the category id is a top level category To check this all the parent category ids of the category is added in a list,throw repository exception"(){
		given:
		catalogRepositoryMock.getItem("categoryId", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		boolean valid = testObjCatalogTools.isFirstLevelCategory("categoryId", "siteId")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"getParentProductId ,TC for getParentProductId"(){
		given:
		testObjCatalogTools.getAllValuesForKey("ObjectCacheKeys", BBBCoreConstants.DISABLE_COLLECTION_PARENT_CACHE) >> ["false"]
		testObjCatalogTools.getAllValuesForKey("ObjectCacheKeys", "COLLECTION_CHILD_RELATION_CACHE_NAME") >> ["true"]
		ObjectCacheMock.get("prd1234_BedBathUS","true") >> "pprd1234"
		when:
		String value = testObjCatalogTools.getParentProductId("prd1234", "BedBathUS")
		then:
		value.equals("pprd1234")
	}
	def"getParentProductId ,TC for parent item is cache is disabled"(){
		given:
		testObjCatalogTools.getAllValuesForKey("ObjectCacheKeys", BBBCoreConstants.DISABLE_COLLECTION_PARENT_CACHE) >> ["true"]
		when:
		String value = testObjCatalogTools.getParentProductId("prd1234", "BedBathUS")
		then:
		value == null
	}
	def"getParentProductId ,TC for product id is null"(){
		given:
		testObjCatalogTools.getAllValuesForKey("ObjectCacheKeys", BBBCoreConstants.DISABLE_COLLECTION_PARENT_CACHE) >> ["false"]
		when:
		String value = testObjCatalogTools.getParentProductId(null, "BedBathUS")
		then:
		value == null
	}
	def"This method send message to TIBCO to populate EDW Repository"(){
		given:
		ProfileEDWInfoVO EDWInfoVO = Mock()
		testObjCatalogTools.setDcPrefix("DC1")
		testObjCatalogTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN) >> ["userToken"]
		1*testObjCatalogTools.sendTextMessage(_) >> {}
		expect:
		testObjCatalogTools.submitEDWProfileDataMesssage(EDWInfoVO)
	}
	def"returns dynamic price details for BBBProductVO"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		dPriceVO.setAlreadyPopulated(false)
		BBBDynamicPriceVO inerdPriceVO =new BBBDynamicPriceVO()
		1*globalRepositoryToolsMock.returnCountryFromSession() >>null
		1*priceCacheContainer.get("product_prd1234") >> inerdPriceVO
		inerdPriceVO.setCaListPriceString("CaListPriceString")
		inerdPriceVO.setCaPricingLabelCode("CaPricingLabelCode")
		inerdPriceVO.setCaSalePriceString("CaSalePriceString")
		inerdPriceVO.setCaIncartFlag(true)
		inerdPriceVO.setBabyIncartFlag(true)
		inerdPriceVO.setBabyListPriceString("BabyListPriceString")
		inerdPriceVO.setBabyPricingLabelCode("BabyPricingLabelCode")
		inerdPriceVO.setBabySalePriceString("BabySalePriceString")
		inerdPriceVO.setBbbIncartFlag(true)
		inerdPriceVO.setBbbListPriceString("BbbListPriceString")
		inerdPriceVO.setBbbPricingLabelCode("BbbPricingLabelCode")
		inerdPriceVO.setBbbSalePriceString("BbbSalePriceString")
		inerdPriceVO.setMxIncartFlag(true)
		inerdPriceVO.setMxListPriceString("MxListPriceString")
		inerdPriceVO.setMxPricingLabelCode("MxPricingLabelCode")
		inerdPriceVO.setMxSalePriceString("MxSalePriceString")
		lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,_) >> null
		when:
		testObjCatalogTools.getDynamicPriceDetails(dPriceVO, "prd1234")
		then:
		dPriceVO.getCountry().equals("US")
		dPriceVO.isAlreadyPopulated()
		dPriceVO.isMxIncartFlag()
		dPriceVO.isBabyIncartFlag()
		dPriceVO.isBbbIncartFlag()
		dPriceVO.isCaIncartFlag()
		dPriceVO.isSetFromCache()
		dPriceVO.getBbbSalePriceString().equals("BbbSalePriceString")
		dPriceVO.getBbbListPriceString().equals("BbbListPriceString")
		dPriceVO.getBbbPricingLabelCode().equals("BbbPricingLabelCode")
		dPriceVO.getMxSalePriceString().equals("MxSalePriceString")
		dPriceVO.getMxListPriceString().equals("MxListPriceString")
		dPriceVO.getMxPricingLabelCode().equals("MxPricingLabelCode")
		dPriceVO.getCaSalePriceString().equals("CaSalePriceString")
		dPriceVO.getCaListPriceString().equals("CaListPriceString")
		dPriceVO.getCaPricingLabelCode().equals("CaPricingLabelCode")
		dPriceVO.getBabySalePriceString().equals("BabySalePriceString")
		dPriceVO.getBabyListPriceString().equals("BabyListPriceString")
		dPriceVO.getBabyPricingLabelCode().equals("BabyPricingLabelCode")
	}
	def"returns dynamic price details for BBBProductVO,fetch from repository"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		dPriceVO.setAlreadyPopulated(false)
		BBBDynamicPriceVO inerdPriceVO =new BBBDynamicPriceVO()
		2*globalRepositoryToolsMock.returnCountryFromSession() >>"US"
		0*priceCacheContainer.get("product_prd1234") >> null
		testObjCatalogTools.setProductCacheContainer(null)
		1*priceListRepositoryToolsMock.getDynamicPriceProductItem("prd1234") >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG)>>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_LIST_PRICE_STRING)>>BBBCatalogConstants.BABY_LIST_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_SALE_PRICE_STRING)>>BBBCatalogConstants.BABY_SALE_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE)>>BBBCatalogConstants.BABY_PRICING_LABEL_CODE
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG)>>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_LIST_PRICE_STRING)>>BBBCatalogConstants.BBB_LIST_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE)>>BBBCatalogConstants.BBB_PRICING_LABEL_CODE
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_SALE_PRICE_STRING)>>BBBCatalogConstants.BBB_SALE_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG)>>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_LIST_PRICE_STRING)>>BBBCatalogConstants.CA_LIST_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE)>>BBBCatalogConstants.CA_PRICING_LABEL_CODE
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_SALE_PRICE_STRING)>>BBBCatalogConstants.CA_SALE_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG)>>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_LIST_PRICE_STRING)>>BBBCatalogConstants.MX_LIST_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE)>>BBBCatalogConstants.MX_PRICING_LABEL_CODE
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_SALE_PRICE_STRING)>>BBBCatalogConstants.MX_SALE_PRICE_STRING
		1*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,_) >> BBBCatalogConstants.BBB_LIST_PRICE_STRING
		testObjCatalogTools.setTbdPriceString(["\$.01","\$0.01",BBBCatalogConstants.BBB_LIST_PRICE_STRING])
		1*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,_) >> BBBCatalogConstants.BBB_LIST_PRICE_STRING
		//estObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_REPO_CALL_DYN_PRICE) >>["true"]
		testObjCatalogTools.setEnableDynRepoCallPLP(true)
		when:
		testObjCatalogTools.getDynamicPriceDetails(dPriceVO, "prd1234")
		then:
		dPriceVO.getCountry().equals("US")
		dPriceVO.isAlreadyPopulated()
		dPriceVO.isMxIncartFlag()
		dPriceVO.isBabyIncartFlag()
		dPriceVO.isBbbIncartFlag()
		dPriceVO.isCaIncartFlag()
		dPriceVO.isSetFromCache()
		dPriceVO.getBbbSalePriceString().equals(null)
		dPriceVO.getBbbListPriceString().equals(BBBCatalogConstants.BBB_LIST_PRICE_STRING)
		dPriceVO.getBbbPricingLabelCode().equals(null)
		dPriceVO.getMxSalePriceString().equals(null)
		dPriceVO.getMxListPriceString().equals(BBBCatalogConstants.BBB_LIST_PRICE_STRING)
		dPriceVO.getMxPricingLabelCode().equals(null)
		dPriceVO.getCaSalePriceString().equals(null)
		dPriceVO.getCaListPriceString().equals(BBBCatalogConstants.BBB_LIST_PRICE_STRING)
		dPriceVO.getCaPricingLabelCode().equals(null)
		dPriceVO.getBabySalePriceString().equals(null)
		dPriceVO.getBabyListPriceString().equals(BBBCatalogConstants.BBB_LIST_PRICE_STRING)
		dPriceVO.getBabyPricingLabelCode().equals(null)
	}
	def"returns dynamic price details for BBBProductVO,BBBDynamicPriceVO is empty"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		dPriceVO.setAlreadyPopulated(false)
		BBBDynamicPriceVO inerdPriceVO =new BBBDynamicPriceVO()
		1*globalRepositoryToolsMock.returnCountryFromSession() >>null
		1*priceCacheContainer.get("product_prd1234") >> inerdPriceVO
		1*lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,_) >> "BbbListPriceString"
		testObjCatalogTools.setTbdPriceString(["\$.01","\$0.01"])
		when:
		testObjCatalogTools.getDynamicPriceDetails(dPriceVO, "prd1234")
		then:
		dPriceVO.getCountry().equals("US")
		dPriceVO.isAlreadyPopulated()
		dPriceVO.isMxIncartFlag() == false
		dPriceVO.isBabyIncartFlag() == false
		dPriceVO.isBbbIncartFlag() == false
		dPriceVO.isCaIncartFlag()==  false
		dPriceVO.isSetFromCache()== true
		dPriceVO.getBbbSalePriceString().equals(null)
		dPriceVO.getBbbListPriceString().equals(null)
		dPriceVO.getBbbPricingLabelCode().equals(null)
		dPriceVO.getMxSalePriceString().equals(null)
		dPriceVO.getMxListPriceString().equals(null)
		dPriceVO.getMxPricingLabelCode().equals(null)
		dPriceVO.getCaSalePriceString().equals(null)
		dPriceVO.getCaListPriceString().equals(null)
		dPriceVO.getCaPricingLabelCode().equals(null)
		dPriceVO.getBabySalePriceString().equals(null)
		dPriceVO.getBabyListPriceString().equals(null)
		dPriceVO.getBabyPricingLabelCode().equals(null)
	}
	def"returns dynamic price details for BBBProductVO,product is not available in both cache and repository"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		dPriceVO.setAlreadyPopulated(false)
		BBBDynamicPriceVO inerdPriceVO =new BBBDynamicPriceVO()
		1*globalRepositoryToolsMock.returnCountryFromSession() >>null
		1*priceCacheContainer.get("product_prd1234") >> null
		testObjCatalogTools.setEnableDynRepoCallPLP(true)
		priceListRepositoryToolsMock.getDynamicPriceProductItem("prd1234") >> null
		when:
		testObjCatalogTools.getDynamicPriceDetails(dPriceVO, "prd1234")
		then:
		dPriceVO.getCountry().equals("US")
		dPriceVO.isAlreadyPopulated() == false
		dPriceVO.isMxIncartFlag() == false
		dPriceVO.isBabyIncartFlag() == false
		dPriceVO.isBbbIncartFlag() == false
		dPriceVO.isCaIncartFlag()==  false
		dPriceVO.isSetFromCache()== false
		dPriceVO.getBbbSalePriceString().equals(null)
		dPriceVO.getBbbListPriceString().equals(null)
		dPriceVO.getBbbPricingLabelCode().equals(null)
		dPriceVO.getMxSalePriceString().equals(null)
		dPriceVO.getMxListPriceString().equals(null)
		dPriceVO.getMxPricingLabelCode().equals(null)
		dPriceVO.getCaSalePriceString().equals(null)
		dPriceVO.getCaListPriceString().equals(null)
		dPriceVO.getCaPricingLabelCode().equals(null)
		dPriceVO.getBabySalePriceString().equals(null)
		dPriceVO.getBabyListPriceString().equals(null)
		dPriceVO.getBabyPricingLabelCode().equals(null)
	}
	def"returns dynamic price details for BBBProductVO,product data already populated"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		dPriceVO.setAlreadyPopulated(true)
		BBBDynamicPriceVO inerdPriceVO =new BBBDynamicPriceVO()
		2*globalRepositoryToolsMock.returnCountryFromSession() >> "US"
		1*priceCacheContainer.get("product_prd1234") >> inerdPriceVO
		when:
		testObjCatalogTools.getDynamicPriceDetails(dPriceVO, "prd1234")
		then:
		dPriceVO.getCountry().equals("US")
		dPriceVO.isAlreadyPopulated()
		dPriceVO.isMxIncartFlag() == false
		dPriceVO.isBabyIncartFlag() == false
		dPriceVO.isBbbIncartFlag() == false
		dPriceVO.isCaIncartFlag()==  false
		dPriceVO.isSetFromCache()== true
		dPriceVO.getBbbSalePriceString().equals(null)
		dPriceVO.getBbbListPriceString().equals(null)
		dPriceVO.getBbbPricingLabelCode().equals(null)
		dPriceVO.getMxSalePriceString().equals(null)
		dPriceVO.getMxListPriceString().equals(null)
		dPriceVO.getMxPricingLabelCode().equals(null)
		dPriceVO.getCaSalePriceString().equals(null)
		dPriceVO.getCaListPriceString().equals(null)
		dPriceVO.getCaPricingLabelCode().equals(null)
		dPriceVO.getBabySalePriceString().equals(null)
		dPriceVO.getBabyListPriceString().equals(null)
		dPriceVO.getBabyPricingLabelCode().equals(null)
	}
	def"returns dynamic price details for BBBProductVO,Dynamic call for PLP is disabled"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		dPriceVO.setAlreadyPopulated(true)
		BBBDynamicPriceVO inerdPriceVO =new BBBDynamicPriceVO()
		2*globalRepositoryToolsMock.returnCountryFromSession() >> "US"
		1*priceCacheContainer.get("product_prd1234") >> null
		testObjCatalogTools.setEnableDynRepoCallPLP(false)
		when:
		testObjCatalogTools.getDynamicPriceDetails(dPriceVO, "prd1234")
		then:
		dPriceVO.getCountry().equals("US")
		dPriceVO.isAlreadyPopulated()
		dPriceVO.isMxIncartFlag() == false
		dPriceVO.isBabyIncartFlag() == false
		dPriceVO.isBbbIncartFlag() == false
		dPriceVO.isCaIncartFlag()==  false
		dPriceVO.isSetFromCache()== false
		dPriceVO.getBbbSalePriceString().equals(null)
		dPriceVO.getBbbListPriceString().equals(null)
		dPriceVO.getBbbPricingLabelCode().equals(null)
		dPriceVO.getMxSalePriceString().equals(null)
		dPriceVO.getMxListPriceString().equals(null)
		dPriceVO.getMxPricingLabelCode().equals(null)
		dPriceVO.getCaSalePriceString().equals(null)
		dPriceVO.getCaListPriceString().equals(null)
		dPriceVO.getCaPricingLabelCode().equals(null)
		dPriceVO.getBabySalePriceString().equals(null)
		dPriceVO.getBabyListPriceString().equals(null)
		dPriceVO.getBabyPricingLabelCode().equals(null)
	}
	def"New method to fetch dynamic price description for product"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY) >> ["true"]
		1*globalRepositoryToolsMock.returnCountryFromSession() >> null
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		1*priceCacheContainer.get("product_prd1234") >> dPriceVO
		testObjCatalogTools.getCurrentSiteId() >>  BBBCoreConstants.SITE_BBB
		dPriceVO.setBabyIncartFlag(true)
		1*testObjCatalogTools.updatePriceStringProd("prd1234", _, "US", BBBCoreConstants.SITE_BBB) >> {}
		when:
		BBBDynamicPriceVO priceVO = testObjCatalogTools.getDynamicProdPriceDescription("prd1234")
		then:
		priceVO!= null
		priceVO.getCountry().equals("US")
		priceVO.isInCartFlag()
	}
	def"New method to fetch dynamic price description for product fetch from DB"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY) >> ["true"]
		2*globalRepositoryToolsMock.returnCountryFromSession() >> "MX"
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		1*priceCacheContainer.get("product_prd1234") >> null
		testObjCatalogTools.getCurrentSiteId() >>  BBBCoreConstants.SITE_BAB_US
		dPriceVO.setBabyIncartFlag(true)
		1*testObjCatalogTools.updatePriceStringProd("prd1234", _, "MX", BBBCoreConstants.SITE_BAB_US) >> {throw new BBBBusinessException("Mock of business exception")}
		1*priceListRepositoryToolsMock.getDynamicPriceProductItem("prd1234") >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG)>>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_LIST_PRICE_STRING)>>BBBCatalogConstants.BABY_LIST_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_SALE_PRICE_STRING)>>BBBCatalogConstants.BABY_SALE_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE)>>BBBCatalogConstants.BABY_PRICING_LABEL_CODE
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG)>>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_LIST_PRICE_STRING)>>BBBCatalogConstants.BBB_LIST_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE)>>BBBCatalogConstants.BBB_PRICING_LABEL_CODE
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_SALE_PRICE_STRING)>>BBBCatalogConstants.BBB_SALE_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG)>>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_LIST_PRICE_STRING)>>BBBCatalogConstants.CA_LIST_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE)>>BBBCatalogConstants.CA_PRICING_LABEL_CODE
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_SALE_PRICE_STRING)>>BBBCatalogConstants.CA_SALE_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG)>>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_LIST_PRICE_STRING)>>BBBCatalogConstants.MX_LIST_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE)>>BBBCatalogConstants.MX_PRICING_LABEL_CODE
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_SALE_PRICE_STRING)>>BBBCatalogConstants.MX_SALE_PRICE_STRING
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_REPO_CALL_DYN_PRICE)>>["true"]
		when:
		BBBDynamicPriceVO priceVO = testObjCatalogTools.getDynamicProdPriceDescription("prd1234")
		then:
		priceVO!= null
		priceVO.getCountry().equals("MX")
		priceVO.isMxIncartFlag()
		priceVO.isBabyIncartFlag()
		priceVO.isBbbIncartFlag()
		priceVO.isCaIncartFlag()
		priceVO.getBbbSalePriceString().equals(BBBCatalogConstants.BBB_SALE_PRICE_STRING)
		priceVO.getBbbListPriceString().equals(BBBCatalogConstants.BBB_LIST_PRICE_STRING)
		priceVO.getBbbPricingLabelCode().equals(BBBCatalogConstants.BBB_PRICING_LABEL_CODE)
		priceVO.getMxSalePriceString().equals(BBBCatalogConstants.MX_SALE_PRICE_STRING)
		priceVO.getMxListPriceString().equals(BBBCatalogConstants.MX_LIST_PRICE_STRING)
		priceVO.getMxPricingLabelCode().equals(BBBCatalogConstants.MX_PRICING_LABEL_CODE)
		priceVO.getCaSalePriceString().equals(BBBCatalogConstants.CA_SALE_PRICE_STRING)
		priceVO.getCaListPriceString().equals(BBBCatalogConstants.CA_LIST_PRICE_STRING)
		priceVO.getCaPricingLabelCode().equals(BBBCatalogConstants.CA_PRICING_LABEL_CODE)
		priceVO.getBabySalePriceString().equals(BBBCatalogConstants.BABY_SALE_PRICE_STRING)
		priceVO.getBabyListPriceString().equals(BBBCatalogConstants.BABY_LIST_PRICE_STRING)
		priceVO.getBabyPricingLabelCode().equals(BBBCatalogConstants.BABY_PRICING_LABEL_CODE)
	}
	def"New method to fetch dynamic price description for product fetch from DB item is not available"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY) >> ["true"]
		2*globalRepositoryToolsMock.returnCountryFromSession() >> "MX"
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		1*priceCacheContainer.get("product_prd1234") >> null
		testObjCatalogTools.getCurrentSiteId() >>  BBBCoreConstants.SITE_BAB_US
		dPriceVO.setBabyIncartFlag(true)
		1*priceListRepositoryToolsMock.getDynamicPriceProductItem("prd1234") >> null
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_REPO_CALL_DYN_PRICE)>>["true"]
		when:
		BBBDynamicPriceVO priceVO = testObjCatalogTools.getDynamicProdPriceDescription("prd1234")
		then:
		priceVO == null
	}
	def"New method to fetch dynamic price description for product fetch from DB is diabled"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY) >> ["true"]
		2*globalRepositoryToolsMock.returnCountryFromSession() >> "MX"
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		1*priceCacheContainer.get("product_prd1234") >> null
		testObjCatalogTools.getCurrentSiteId() >>  BBBCoreConstants.SITE_BAB_US
		dPriceVO.setBabyIncartFlag(true)
		0*priceListRepositoryToolsMock.getDynamicPriceProductItem("prd1234") >> null
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_REPO_CALL_DYN_PRICE)>>["false"]
		when:
		BBBDynamicPriceVO priceVO = testObjCatalogTools.getDynamicProdPriceDescription("prd1234")
		then:
		priceVO == null
	}
	def"New method to fetch dynamic price description for product ,site id is tbs_bedbathus"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY) >> ["true"]
		2*globalRepositoryToolsMock.returnCountryFromSession() >> "CA"
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		testObjCatalogTools.setProductCacheContainer(null)
		testObjCatalogTools.getCurrentSiteId() >>  "tbs_bedbathus"
		dPriceVO.setBabyIncartFlag(true)
		0*testObjCatalogTools.updatePriceStringProd("prd1234", _, "MX", BBBCoreConstants.SITE_BAB_US) >> {}
		1*priceListRepositoryToolsMock.getDynamicPriceProductItem("prd1234") >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG)>>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_LIST_PRICE_STRING)>>BBBCatalogConstants.BABY_LIST_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_SALE_PRICE_STRING)>>BBBCatalogConstants.BABY_SALE_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE)>>BBBCatalogConstants.BABY_PRICING_LABEL_CODE
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG)>>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_LIST_PRICE_STRING)>>BBBCatalogConstants.BBB_LIST_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE)>>BBBCatalogConstants.BBB_PRICING_LABEL_CODE
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_SALE_PRICE_STRING)>>BBBCatalogConstants.BBB_SALE_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG)>>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_LIST_PRICE_STRING)>>BBBCatalogConstants.CA_LIST_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE)>>BBBCatalogConstants.CA_PRICING_LABEL_CODE
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_SALE_PRICE_STRING)>>BBBCatalogConstants.CA_SALE_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG)>>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_LIST_PRICE_STRING)>>BBBCatalogConstants.MX_LIST_PRICE_STRING
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE)>>BBBCatalogConstants.MX_PRICING_LABEL_CODE
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_SALE_PRICE_STRING)>>BBBCatalogConstants.MX_SALE_PRICE_STRING
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_REPO_CALL_DYN_PRICE)>>["true"]
		when:
		BBBDynamicPriceVO priceVO = testObjCatalogTools.getDynamicProdPriceDescription("prd1234")
		then:
		priceVO!= null
		priceVO.getCountry().equals("CA")
		priceVO.isMxIncartFlag()
		priceVO.isBabyIncartFlag()
		priceVO.isBbbIncartFlag()
		priceVO.isCaIncartFlag()
		priceVO.getBbbSalePriceString().equals(BBBCatalogConstants.BBB_SALE_PRICE_STRING)
		priceVO.getBbbListPriceString().equals(BBBCatalogConstants.BBB_LIST_PRICE_STRING)
		priceVO.getBbbPricingLabelCode().equals(BBBCatalogConstants.BBB_PRICING_LABEL_CODE)
		priceVO.getMxSalePriceString().equals(BBBCatalogConstants.MX_SALE_PRICE_STRING)
		priceVO.getMxListPriceString().equals(BBBCatalogConstants.MX_LIST_PRICE_STRING)
		priceVO.getMxPricingLabelCode().equals(BBBCatalogConstants.MX_PRICING_LABEL_CODE)
		priceVO.getCaSalePriceString().equals(BBBCatalogConstants.CA_SALE_PRICE_STRING)
		priceVO.getCaListPriceString().equals(BBBCatalogConstants.CA_LIST_PRICE_STRING)
		priceVO.getCaPricingLabelCode().equals(BBBCatalogConstants.CA_PRICING_LABEL_CODE)
		priceVO.getBabySalePriceString().equals(BBBCatalogConstants.BABY_SALE_PRICE_STRING)
		priceVO.getBabyListPriceString().equals(BBBCatalogConstants.BABY_LIST_PRICE_STRING)
		priceVO.getBabyPricingLabelCode().equals(BBBCatalogConstants.BABY_PRICING_LABEL_CODE)
	}
	def"New method to fetch dynamic price description for product,dynamic price is diabled"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY) >> []
		when:
		BBBDynamicPriceVO priceVO = testObjCatalogTools.getDynamicProdPriceDescription("prd1234")
		then:
		priceVO == null
	}
	def"New method to update dynamic price string for product"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		when:
		testObjCatalogTools.updatePriceStringProd("prd1234", dPriceVO, "US",BBBCoreConstants.SITE_BAB_CA)
		then:
		dPriceVO.isDynamicPricingProduct()
		dPriceVO.getSalePriceRangeDescription().equals(null)
		dPriceVO.getPriceRangeDescription().equals(null)
		dPriceVO.isCaIncartFlag() == false
	}
	def"New method to update dynamic price string for product for site id SITEBAB_CA_TBS"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		dPriceVO.setCaListPriceString("CaListPriceString")
		dPriceVO.setCaSalePriceString("CaSalePriceString")
		dPriceVO.setCaPricingLabelCode("CaPricingLabelCode")
		dPriceVO.setCaIncartFlag(true)
		when:
		testObjCatalogTools.updatePriceStringProd("prd1234", dPriceVO, "US",BBBCoreConstants.SITEBAB_CA_TBS)
		then:
		dPriceVO.getCaPricingLabelCode().equals("CaPricingLabelCode")
		dPriceVO.isDynamicPricingProduct()
		dPriceVO.getSalePriceRangeDescription().equals("CaSalePriceString")
		dPriceVO.getPriceRangeDescription().equals("CaListPriceString")
		dPriceVO.isCaIncartFlag()
	}
	def"New method to update dynamic price string for product for site id is SITEBAB_BABY_TBS"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		when:
		testObjCatalogTools.updatePriceStringProd("prd1234", dPriceVO, "US",BBBCoreConstants.SITEBAB_BABY_TBS)
		then:
		dPriceVO.isDynamicPricingProduct()
		dPriceVO.getSalePriceRangeDescription().equals(null)
		dPriceVO.getPriceRangeDescription().equals(null)
		dPriceVO.isCaIncartFlag() == false
	}
	def"New method to update dynamic price string for product for site id SITE_BBB"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		dPriceVO.setBabyListPriceString("CaListPriceString")
		dPriceVO.setBabySalePriceString("CaSalePriceString")
		dPriceVO.setBabyPricingLabelCode("CaPricingLabelCode")
		dPriceVO.setBabyIncartFlag(true)
		when:
		testObjCatalogTools.updatePriceStringProd("prd1234", dPriceVO, "US",BBBCoreConstants.SITE_BBB)
		then:
		dPriceVO.isDynamicPricingProduct()
		dPriceVO.getSalePriceRangeDescription().equals("CaSalePriceString")
		dPriceVO.getPriceRangeDescription().equals("CaListPriceString")
		dPriceVO.isCaIncartFlag()==false
	}
	def"New method to update dynamic price string for product for country MX and vo is empty"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		when:
		testObjCatalogTools.updatePriceStringProd("prd1234", dPriceVO, "MX","")
		then:
		dPriceVO.isDynamicPricingProduct()
		dPriceVO.getSalePriceRangeDescription().equals(null)
		dPriceVO.getPriceRangeDescription().equals(null)
		dPriceVO.isCaIncartFlag()==false
	}
	def"New method to update dynamic price string for product for country US and vo is empty"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		when:
		testObjCatalogTools.updatePriceStringProd("prd1234", dPriceVO, "US","")
		then:
		dPriceVO.isDynamicPricingProduct()
		dPriceVO.getSalePriceRangeDescription().equals(null)
		dPriceVO.getPriceRangeDescription().equals(null)
		dPriceVO.isCaIncartFlag()==false
	}
	def"New method to update dynamic price string for product for country MX"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		dPriceVO.setMxListPriceString("CaListPriceString")
		dPriceVO.setMxSalePriceString("CaSalePriceString")
		dPriceVO.setMxPricingLabelCode("CaPricingLabelCode")
		dPriceVO.setMxIncartFlag(true)
		when:
		testObjCatalogTools.updatePriceStringProd("prd1234", dPriceVO, "MX","")
		then:
		dPriceVO.isDynamicPricingProduct()
		dPriceVO.getSalePriceRangeDescription().equals("CaSalePriceString")
		dPriceVO.getPriceRangeDescription().equals("CaListPriceString")
		dPriceVO.isCaIncartFlag()==false
	}
	def"New method to update dynamic price string for product  country US"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		dPriceVO.setBbbListPriceString("CaListPriceString")
		dPriceVO.setBbbSalePriceString("CaSalePriceString")
		dPriceVO.setBbbPricingLabelCode("CaPricingLabelCode")
		dPriceVO.setBbbIncartFlag(true)
		when:
		testObjCatalogTools.updatePriceStringProd("prd1234", dPriceVO, "US","")
		then:
		dPriceVO.getSalePriceRangeDescription().equals("CaSalePriceString")
		dPriceVO.getPriceRangeDescription().equals("CaListPriceString")
		dPriceVO.isCaIncartFlag()==false
	}
	def"method to update dynamic price string for product"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		ProductVO pVO = Mock()
		when:
		testObjCatalogTools.updatePriceStringProd(pVO, dPriceVO, "US",BBBCoreConstants.SITE_BAB_CA)
		then:
		1*pVO.setPriceRangeDescription(null)
		1*pVO.setSalePriceRangeDescription(null)
		1*pVO.setPriceLabelCode(null)
	}

	def"method to update dynamic price string for product for site id SITEBAB_CA_TBS"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		ProductVO pVO = Mock()
		dPriceVO.setCaListPriceString("CaListPriceString")
		dPriceVO.setCaSalePriceString("CaSalePriceString")
		dPriceVO.setCaPricingLabelCode("CaPricingLabelCode")
		dPriceVO.setCaIncartFlag(true)
		when:
		testObjCatalogTools.updatePriceStringProd(pVO, dPriceVO, "US",BBBCoreConstants.SITEBAB_CA_TBS)
		then:
		1*pVO.setPriceLabelCode("CaPricingLabelCode")
		1*pVO.setSalePriceRangeDescription("CaSalePriceString")
		1*pVO.setPriceRangeDescription("CaListPriceString")
	}
	def"method to update dynamic price string for product for site id is SITEBAB_BABY_TBS"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		ProductVO pVO = Mock()
		when:
		testObjCatalogTools.updatePriceStringProd(pVO, dPriceVO, "US",BBBCoreConstants.SITEBAB_BABY_TBS)
		then:
		1*pVO.setPriceRangeDescription(null)
		1*pVO.setSalePriceRangeDescription(null)
		1*pVO.setPriceLabelCode(null)
	}
	def"method to update dynamic price string for product for site id SITE_BBB"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		ProductVO pVO = Mock()
		dPriceVO.setBabyListPriceString("CaListPriceString")
		dPriceVO.setBabySalePriceString("CaSalePriceString")
		dPriceVO.setBabyPricingLabelCode("CaPricingLabelCode")
		dPriceVO.setBabyIncartFlag(true)
		when:
		testObjCatalogTools.updatePriceStringProd(pVO, dPriceVO, "US",BBBCoreConstants.SITE_BBB)
		then:
		1*pVO.setPriceLabelCode("CaPricingLabelCode")
		1*pVO.setSalePriceRangeDescription("CaSalePriceString")
		1*pVO.setPriceRangeDescription("CaListPriceString")
	}
	def"method to update dynamic price string for product for country MX and vo is empty"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		ProductVO pVO = Mock()
		when:
		testObjCatalogTools.updatePriceStringProd(pVO, dPriceVO, "MX","")
		then:
		1*pVO.setPriceRangeDescription(null)
		1*pVO.setSalePriceRangeDescription(null)
		1*pVO.setPriceLabelCode(null)
	}
	def"method to update dynamic price string for product for country US and vo is empty"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		ProductVO pVO = Mock()
		when:
		testObjCatalogTools.updatePriceStringProd(pVO, dPriceVO, "US","")
		then:
		1*pVO.setPriceRangeDescription(null)
		1*pVO.setSalePriceRangeDescription(null)
	}
	def"method to update dynamic price string for product for country MX"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		ProductVO pVO = Mock()
		dPriceVO.setMxListPriceString("CaListPriceString")
		dPriceVO.setMxSalePriceString("CaSalePriceString")
		dPriceVO.setMxPricingLabelCode("CaPricingLabelCode")
		dPriceVO.setMxIncartFlag(true)
		when:
		testObjCatalogTools.updatePriceStringProd(pVO, dPriceVO, "MX","")
		then:
		1*pVO.setPriceRangeDescription("CaListPriceString")
		1*pVO.setSalePriceRangeDescription("CaSalePriceString")
	}
	def"method to update dynamic price string for product  country US"(){
		given:
		BBBDynamicPriceVO dPriceVO =new BBBDynamicPriceVO()
		ProductVO pVO = Mock()
		dPriceVO.setBbbListPriceString("CaListPriceString")
		dPriceVO.setBbbSalePriceString("CaSalePriceString")
		dPriceVO.setBbbPricingLabelCode("CaPricingLabelCode")
		dPriceVO.setBbbIncartFlag(true)
		when:
		testObjCatalogTools.updatePriceStringProd(pVO, dPriceVO, "US","")
		then:
		1*pVO.setPriceLabelCode("CaPricingLabelCode")
		1*pVO.setSalePriceRangeDescription("CaSalePriceString")
		1*pVO.setPriceRangeDescription("CaListPriceString")
	}
	def"Gets the active parent category ids for site"(){
		given:
		RepositoryItem catrepo= Mock()
		RepositoryItem catrepo1= Mock()
		RepositoryItem catrepo2= Mock()
		RepositoryItem catrepo3= Mock()
		RepositoryItem catrepo4= Mock()
		catrepo2.getRepositoryId() >> "rep1234"
		catrepo3.getRepositoryId() >> "rep1234"
		catrepo.getPropertyValue(BBBCoreConstants.SITE_IDS) >> ["BebBathCanada","TBS_bedBathUS"].toSet()
		catrepo1.getPropertyValue(BBBCoreConstants.SITE_IDS) >> ["BebBathCanada","TBS_bedBathUS","BedBathUS"].toSet()
		catrepo2.getPropertyValue(BBBCoreConstants.SITE_IDS) >> ["BebBathCanada","TBS_bedBathUS","BedBathUS"].toSet()
		catrepo3.getPropertyValue(BBBCoreConstants.SITE_IDS) >> ["BebBathCanada","TBS_bedBathUS","BedBathUS"].toSet()
		catrepo2.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
		catrepo3.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
		testObjCatalogTools.isCategoryActive(catrepo1) >> false
		testObjCatalogTools.isCategoryActive(catrepo2) >> true
		testObjCatalogTools.isCategoryActive(catrepo3) >> true
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.RELATED_CATEGORIES_COUNT) >> ["2"]
		seoLinkGen.formatUrl(_, BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME) >> "formatUrl"
		when:
		List<CategoryVO> categoryVOList =testObjCatalogTools.getActiveParentCategoriesForSite ([catrepo3,catrepo2,catrepo1,catrepo].toSet(), "BedBathUS")
		then:
		categoryVOList.size() == 1
		categoryVOList.get(0).getCategoryName().equals(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)
		categoryVOList.get(0).getCategoryId().equals("rep1234")
		categoryVOList.get(0).getSeoURL().equals("formatUrl")
	}
	def"Gets the active parent category ids for site,maxactivecount is zero"(){
		given:
		RepositoryItem catrepo= Mock()
		catrepo.getPropertyValue(BBBCoreConstants.SITE_IDS) >> ["BebBathCanada","TBS_bedBathUS"].toSet()
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.RELATED_CATEGORIES_COUNT) >> ["0"]
		seoLinkGen.formatUrl(_, BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME) >> "formatUrl"
		when:
		List<CategoryVO> categoryVOList =testObjCatalogTools.getActiveParentCategoriesForSite ([catrepo].toSet(), "BedBathUS")
		then:
		categoryVOList.size() == 0
	}
	def"Gets the active parent category ids for site,maxactivecount is zero,max active count is nulls"(){
		given:
		RepositoryItem catrepo= Mock()
		catrepo.getPropertyValue(BBBCoreConstants.SITE_IDS) >> ["BebBathCanada","TBS_bedBathUS"].toSet()
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.RELATED_CATEGORIES_COUNT) >> [null]
		seoLinkGen.formatUrl(_, BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME) >> "formatUrl"
		when:
		List<CategoryVO> categoryVOList =testObjCatalogTools.getActiveParentCategoriesForSite ([catrepo].toSet(), "BedBathUS")
		then:
		categoryVOList.size() == 0
	}
	def"Gets the active parent category ids for site,maxactivecount is zero,max active count is charcters"(){
		given:
		RepositoryItem catrepo= Mock()
		catrepo.getPropertyValue(BBBCoreConstants.SITE_IDS) >> ["BebBathCanada","TBS_bedBathUS"].toSet()
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.RELATED_CATEGORIES_COUNT) >> ["asadas"]
		seoLinkGen.formatUrl(_, BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME) >> "formatUrl"
		when:
		List<CategoryVO> categoryVOList =testObjCatalogTools.getActiveParentCategoriesForSite ([catrepo].toSet(), "BedBathUS")
		then:
		categoryVOList.size() == 0
	}
	def"Gets the active parent category ids for site,relatedCategoriesCount is null "(){
		given:
		Set<RepositoryItem> parentCategories = new HashSet<RepositoryItem>()
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.RELATED_CATEGORIES_COUNT) >> null
		when:
		List<CategoryVO> categoryVOList =testObjCatalogTools.getActiveParentCategoriesForSite (parentCategories, "BedBathUS")
		then:
		categoryVOList.isEmpty()
	}
	def"This method gets the parent category id for a particular product"(){
		given:
		RepositoryItem catrepo= Mock()
		RepositoryItem catrepo1= Mock()
		RepositoryItem catrepo2= Mock()
		RepositoryItem catrepo3= Mock()
		catrepo.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> null
		catrepo1.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> true
		catrepo2.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> false
		catrepo3.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> false
		catrepo1.getPropertyValue(BBBCoreConstants.SITE_IDS) >> ["BebBathCanada","TBS_bedBathUS"].toSet()
		catrepo2.getPropertyValue(BBBCoreConstants.SITE_IDS) >> ["BebBathCanada","TBS_bedBathUS","BedBathUS"].toSet()
		catrepo3.getPropertyValue(BBBCoreConstants.SITE_IDS) >> ["BebBathCanada","TBS_bedBathUS","BedBathUS"].toSet()
		catrepo3.getRepositoryId() >> "rep1234"
		catrepo2.getRepositoryId() >> "rep1234"
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME) >> [catrepo3,catrepo2,catrepo1,catrepo].toSet()
		when:
		String value = testObjCatalogTools.getParentCategoryIdForProduct("prd1234","BedBathUS")
		then:
		value.equals("rep1234")
	}
	def"This method gets the parent category id for a particular product.parent catagories is null"(){
		given:
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME) >> [].toSet()
		when:
		String value = testObjCatalogTools.getParentCategoryIdForProduct("prd1234","BedBathUS")
		then:
		value.equals("")
	}
	def"This method gets the parent category id for a particular product,when product id is null"(){
		given:
		when:
		String value = testObjCatalogTools.getParentCategoryIdForProduct(null,"BedBathUS")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"This method gets the parent category id for a particular product,repository item is null"(){
		given:
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null
		when:
		String value = testObjCatalogTools.getParentCategoryIdForProduct("prd1234","BedBathUS")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1004:1004")
	}
	def"This method gets the parent category id for a particular product,throws repository exception"(){
		given:
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		String value = testObjCatalogTools.getParentCategoryIdForProduct("prd1234","BedBathUS")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"Gets the vendor configuration js"(){
		given:
		1*testObjCatalogTools.getCatalogRepository().getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) >>  BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_KEYWORDS) >> BBBCatalogConstants.PRODUCT_KEYWORDS
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY) >> BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY
		1*vendorRepository.getView(BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR) >>  repositoryViewMock
		1*repositoryViewMock.getQueryBuilder() >> qBuilder
		1*qBuilder.createComparisonQuery(qExp, qExp, 4) >> query
		1*qBuilder.createPropertyQueryExpression("customizationVendorId") >> qExp
		1*qBuilder.createConstantQueryExpression("vendorId") >> qExp
		RepositoryItem vendorRepo = Mock()
		1*repositoryViewMock.executeQuery(query) >> [vendorRepo]
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDORS_NAME_PROPERTY) >> BBBCatalogConstants.VENDORS_NAME_PROPERTY
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDOR_JS) >> BBBCatalogConstants.VENDOR_JS
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDOR_JS_MOBILE) >> BBBCatalogConstants.VENDOR_JS_MOBILE
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDOR_API_KEY) >> BBBCatalogConstants.VENDOR_API_KEY
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDOR_API_URL) >> BBBCatalogConstants.VENDOR_API_URL
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDOR_API_VERSION) >> BBBCatalogConstants.VENDOR_API_VERSION
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		when:
		Set<String> values = testObjCatalogTools.getVendorConfigurationJS("prd1234", "section", BBBCatalogConstants.PRODUCT_DETAILS, "desktop")
		then:
		values.contains(BBBCatalogConstants.VENDOR_JS)
	}
	def"Gets the vendor configuration js channel as mobile"(){
		given:
		1*testObjCatalogTools.getCatalogRepository().getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) >>  BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_KEYWORDS) >> BBBCatalogConstants.PRODUCT_KEYWORDS
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY) >> BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY
		1*vendorRepository.getView(BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR) >>  repositoryViewMock
		1*repositoryViewMock.getQueryBuilder() >> qBuilder
		1*qBuilder.createComparisonQuery(qExp, qExp, 4) >> query
		1*qBuilder.createPropertyQueryExpression("customizationVendorId") >> qExp
		1*qBuilder.createConstantQueryExpression("vendorId") >> qExp
		RepositoryItem vendorRepo = Mock()
		1*repositoryViewMock.executeQuery(query) >> [vendorRepo]
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDORS_NAME_PROPERTY) >> BBBCatalogConstants.VENDORS_NAME_PROPERTY
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDOR_JS) >> BBBCatalogConstants.VENDOR_JS
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDOR_JS_MOBILE) >> BBBCatalogConstants.VENDOR_JS_MOBILE
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDOR_API_KEY) >> BBBCatalogConstants.VENDOR_API_KEY
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDOR_API_URL) >> BBBCatalogConstants.VENDOR_API_URL
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDOR_API_VERSION) >> BBBCatalogConstants.VENDOR_API_VERSION
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDOR_BBB_CLIENT_ID) >> BBBCatalogConstants.VENDOR_BBB_CLIENT_ID
		testObjCatalogTools.getCurrentSiteId() >> BBBCoreConstants.SITE_BAB_US
		when:
		Set<String> values = testObjCatalogTools.getVendorConfigurationJS("prd1234", "section", BBBCatalogConstants.PRODUCT_DETAILS, "mobile")
		then:
		values.contains(BBBCatalogConstants.VENDOR_JS_MOBILE)
	}
	def"Gets the vendor configuration js channel as mobile and vendor id is null"(){
		given:
		1*testObjCatalogTools.getCatalogRepository().getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) >>  BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_KEYWORDS) >> BBBCatalogConstants.PRODUCT_KEYWORDS
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY) >> null
		1*vendorRepository.getItem(BBBCatalogConstants.DEFAULT,BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR) >> null
		testObjCatalogTools.getCurrentSiteId() >> BBBCoreConstants.SITE_BAB_US
		when:
		Set<String> values = testObjCatalogTools.getVendorConfigurationJS("prd1234", "section", BBBCatalogConstants.PRODUCT_DETAILS, "mobile")
		then:
		!values.contains(BBBCatalogConstants.VENDOR_JS_MOBILE)
	}
	def"Gets the vendor configuration js channel as mobile and product is null"(){
		given:
		RepositoryItem vendorRepo = Mock()
		1*vendorRepository.getItem(BBBCatalogConstants.DEFAULT,BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR) >> vendorRepo
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDOR_JS_MOBILE) >> BBBCatalogConstants.VENDOR_JS_MOBILE
		when:
		Set<String> values = testObjCatalogTools.getVendorConfigurationJS(null, "section", BBBCatalogConstants.PRODUCT_DETAILS, "mobile")
		then:
		values.contains(BBBCatalogConstants.VENDOR_JS_MOBILE)
	}
	def"Gets the vendor configuration js for cart"(){
		given:
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.queryRepositoryItems(_,BBBEximConstants.ALL,_) >> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_JS) >>BBBCatalogConstants.VENDOR_JS
		when:
		Set<String> values = testObjCatalogTools.getVendorConfigurationJS("prd1234", BBBCoreConstants.CART, BBBCoreConstants.CART,  "DESKTOP")
		then:
		values.contains(BBBCatalogConstants.VENDOR_JS)
	}
	def"Gets the vendor configuration js for cart and channel as mobile"(){
		given:
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.queryRepositoryItems(_,BBBEximConstants.ALL,_) >> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_JS_MOBILE) >>BBBCatalogConstants.VENDOR_JS_MOBILE
		when:
		Set<String> values = testObjCatalogTools.getVendorConfigurationJS("prd1234", BBBCoreConstants.CART, BBBCoreConstants.CART,  "Mobile")
		then:
		values.contains(BBBCatalogConstants.VENDOR_JS_MOBILE)
	}
	def"Gets the vendor configuration js for cart and channel as mobile,mobile js is null"(){
		given:
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.queryRepositoryItems(_,BBBEximConstants.ALL,_) >> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_JS_MOBILE) >>null
		when:
		Set<String> values = testObjCatalogTools.getVendorConfigurationJS("prd1234", BBBCoreConstants.CART, BBBCoreConstants.CART,  "Mobile")
		then:
		values.isEmpty()
	}
	def"Gets the vendor configuration js for payment "(){
		given:
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.queryRepositoryItems(_,BBBEximConstants.ALL,_) >> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_JS_MOBILE) >>null
		when:
		Set<String> values = testObjCatalogTools.getVendorConfigurationJS("prd1234", "", BBBCoreConstants.CART,  "Mobile")
		then:
		values ==null
	}
	def"Gets the vendor configuration js for cart item is not available "(){
		given:
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.queryRepositoryItems(_,BBBEximConstants.ALL,_) >>[]
		RepositoryItem vendorRepo = Mock()
		1*vendorRepository.getItem(BBBCatalogConstants.DEFAULT,BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR) >> vendorRepo
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDOR_JS) >> BBBCatalogConstants.VENDOR_JS
		when:
		Set<String> values = testObjCatalogTools.getVendorConfigurationJS("prd1234", "", BBBCoreConstants.WISHLIST, "DESKTOP")
		then:
		values.contains(BBBCatalogConstants.VENDOR_JS)
	}
	def"Gets the vendor configuration js for cart item is null "(){
		given:
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.queryRepositoryItems(_,BBBEximConstants.ALL,_) >> null
		RepositoryItem vendorRepo = Mock()
		1*vendorRepository.getItem(BBBCatalogConstants.DEFAULT,BBBCatalogConstants.VENDOR_CONFIGURATION_DESCRIPTOR) >> {throw new RepositoryException()}
		vendorRepo.getPropertyValue(BBBCatalogConstants.VENDOR_JS) >> BBBCatalogConstants.VENDOR_JS
		when:
		Set<String> values = testObjCatalogTools.getVendorConfigurationJS("prd1234", BBBCatalogConstants.CART_DETAIL, BBBCoreConstants.CART, "DESKTOP")
		then:
		!values.contains(BBBCatalogConstants.VENDOR_JS)
	}
	def"This method returns only SEO Meta Details for product"(){
		given:
		1*testObjCatalogTools.getCatalogRepository().getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) >>  BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_KEYWORDS) >> BBBCatalogConstants.PRODUCT_KEYWORDS
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VENDOR_ID_PRODUCT_PROPERTY) >> null
		when:
		ProductVO vo = testObjCatalogTools.getProductVOMetaDetails("BedBathUS", "prd1234")
		then:
		vo.getName().equals(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)
		vo.getShortDescription().equals(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)
		vo.getPrdKeywords().equals(BBBCatalogConstants.PRODUCT_KEYWORDS)
	}
	def"This method returns only SEO Meta Details for product site id is null"(){
		when:
		ProductVO vo = testObjCatalogTools.getProductVOMetaDetails("", "")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"This method returns only SEO Meta Details for product  , repository item is null"(){
		given:
		catalogRepositoryMock.getItem("prd1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null
		when:
		ProductVO vo = testObjCatalogTools.getProductVOMetaDetails("BedBathUS", "prd1234")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1004:1004")
	}
	def"This method returns only SEO Meta Details for product  , throw repository exception"(){
		given:
		catalogRepositoryMock.getItem("prd1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		ProductVO vo = testObjCatalogTools.getProductVOMetaDetails("BedBathUS", "prd1234")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"Get the VDC Offset message from label and actual offset date"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.VDC_OFFSET_DATE) >>[addDays(0, BBBCoreConstants.US_DATE_FORMAT)]
		catalogRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCoreConstants.SHIPPING_CUT_OFF_OFFSET) >> new Integer(2)
		1*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_VDC_OFFSET_MSG, null,_,"BedBathUS") >> BBBCoreConstants.TXT_VDC_OFFSET_MSG
		when:
		String value = testObjCatalogTools.getActualOffsetMessage("sku12345", "BedBathUS")
		then:
		value.equals(BBBCoreConstants.TXT_VDC_OFFSET_MSG)
	}
	def"Get the VDC Offset message from label and actual offset date throw repository exception"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.VDC_OFFSET_DATE) >>[addDays(0, BBBCoreConstants.US_DATE_FORMAT)]
		catalogRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		0*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_VDC_OFFSET_MSG, null,_,"BedBathUS") >> BBBCoreConstants.TXT_VDC_OFFSET_MSG
		when:
		String value = testObjCatalogTools.getActualOffsetMessage("sku12345", "BedBathUS")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"Get the VDC Offset message from label and actual offset date  repository item is null"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.VDC_OFFSET_DATE) >>[addDays(0, BBBCoreConstants.US_DATE_FORMAT)]
		catalogRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>null
		1*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_VDC_OFFSET_MSG, null,_,"BedBathUS") >> BBBCoreConstants.TXT_VDC_OFFSET_MSG
		when:
		String value = testObjCatalogTools.getActualOffsetMessage("sku12345", "BedBathUS")
		then:
		value.equals(BBBCoreConstants.TXT_VDC_OFFSET_MSG)
	}
	def"Get the VDC Offset message from label and actual offset date  shippingCutoffOffset is null"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.VDC_OFFSET_DATE) >>[addDays(0, BBBCoreConstants.US_DATE_FORMAT)]
		catalogRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCoreConstants.SHIPPING_CUT_OFF_OFFSET) >> null
		1*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_VDC_OFFSET_MSG, null,_,"BedBathUS") >> BBBCoreConstants.TXT_VDC_OFFSET_MSG
		when:
		String value = testObjCatalogTools.getActualOffsetMessage("sku12345", "BedBathUS")
		then:
		value.equals(BBBCoreConstants.TXT_VDC_OFFSET_MSG)
	}

	def"Get the VDC Offset message from label and actual offset date no of days is zero"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.VDC_OFFSET_DATE) >>[addDays(0, BBBCoreConstants.US_DATE_FORMAT)]
		catalogRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCoreConstants.SHIPPING_CUT_OFF_OFFSET) >> new Integer(0)
		1*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_VDC_OFFSET_MSG, null,_,"BedBathUS") >> BBBCoreConstants.TXT_VDC_OFFSET_MSG
		when:
		String value = testObjCatalogTools.getActualOffsetMessage("sku12345", "BedBathUS")
		then:
		value.equals(BBBCoreConstants.TXT_VDC_OFFSET_MSG)
	}
	def"Get the VDC Offset message from label and actual offset date,site id is null"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.VDC_OFFSET_DATE) >>[addDays(0, BBBCoreConstants.US_DATE_FORMAT)]
		catalogRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCoreConstants.SHIPPING_CUT_OFF_OFFSET) >> new Integer(2)
		0*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_VDC_OFFSET_MSG, null,_,"BedBathUS") >> BBBCoreConstants.TXT_VDC_OFFSET_MSG
		when:
		String value = testObjCatalogTools.getActualOffsetMessage("sku12345",null)
		then:
		value.equals("")
	}
	def"Get the VDC Offset message from label and actual offset date,vdcOffsetDate is empty"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.VDC_OFFSET_DATE) >>[null]
		0*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_VDC_OFFSET_MSG, null,_,"BedBathUS") >> BBBCoreConstants.TXT_VDC_OFFSET_MSG
		when:
		String value = testObjCatalogTools.getActualOffsetMessage("sku12345","BedBathUS")
		then:
		value.equals("")
	}
	def"Get the VDC Offset message from label and actual offset date,throw prase exception"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.VDC_OFFSET_DATE) >>["asd"]
		0*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_VDC_OFFSET_MSG, null,_,"BedBathUS") >> BBBCoreConstants.TXT_VDC_OFFSET_MSG
		when:
		String value = testObjCatalogTools.getActualOffsetMessage("sku12345","BedBathUS")
		then:
		value.equals("")
	}
	def"Get the VDC Offset message from label and actual offset date,sku id is null"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.VDC_OFFSET_DATE) >>[addDays(0, BBBCoreConstants.US_DATE_FORMAT)]
		catalogRepositoryMock.getItem("sku12345", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCoreConstants.SHIPPING_CUT_OFF_OFFSET) >> new Integer(2)
		0*lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_VDC_OFFSET_MSG, null,_,"BedBathUS") >> BBBCoreConstants.TXT_VDC_OFFSET_MSG
		when:
		String value = testObjCatalogTools.getActualOffsetMessage(null, "BedBathUS")
		then:
		value.equals("")
	}

	def"Get the VDC message from Label and the VDC Expected Deliverty Time"(){
		given:
		shippingRepositoryToolsMock.getExpectedDeliveryTimeVDC("shippingMethod", "sku1234", true, _, false, false) >> "vdcDelTime"
		testObjCatalogTools.getMinimalSku("sku1234") >> skuDetailVOMock
		skuDetailVOMock.isLtlItem() >> true
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "BedBathUS"
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getSKUDetails("BedBathUS", "sku1234") >>skuDetailVOMock
		skuDetailVOMock.isCustomizationOffered() >> false
		lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_VDC_DEL_LTL_MSG, null, _,"BedBathUS") >> "vdcShipMessage"
		when:
		String value =  testObjCatalogTools.getVDCShipMessage("sku1234", true, "shippingMethod", new Date(), true)
		then:
		value.equals("vdcShipMessage")
	}
	def"Get the VDC message from Label and the VDC Expected Deliverty Time throw exception"(){
		given:
		shippingRepositoryToolsMock.getExpectedDeliveryTimeVDC("shippingMethod", "sku1234", true, _, false, false) >> "vdcDelTime"
		testObjCatalogTools.getMinimalSku("sku1234") >> {throw new BBBBusinessException("Mock of Business exception")}
		skuDetailVOMock.isLtlItem() >> true
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "BedBathUS"
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getSKUDetails("BedBathUS", "sku1234") >>{throw new BBBBusinessException("Mock of Business exception")}
		skuDetailVOMock.isCustomizationOffered() >> false
		lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_VDC_DEL_REST_MSG, null, _,"BedBathUS") >> "vdcShipMessage"
		when:
		String value =  testObjCatalogTools.getVDCShipMessage("sku1234", true, "shippingMethod", new Date(), true)
		then:
		value.equals("vdcShipMessage")
	}
	def"Get the VDC message from Label and the VDC Expected Deliverty Time personalizationOffered is availble"(){
		given:
		shippingRepositoryToolsMock.getExpectedDeliveryTimeVDC("shippingMethod", "sku1234", true, _, false, false) >> "vdcDelTime"
		testObjCatalogTools.getMinimalSku("sku1234") >> skuDetailVOMock
		skuDetailVOMock.isLtlItem() >> false
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "BedBathUS"
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getSKUDetails("BedBathUS", "sku1234") >>skuDetailVOMock
		skuDetailVOMock.isCustomizationOffered() >> true
		lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_VDC_DEL_LTL_MSG, null, _,"BedBathUS") >> "vdcShipMessage"
		when:
		String value =  testObjCatalogTools.getVDCShipMessage("sku1234", true, "shippingMethod", new Date(), true)
		then:
		value.equals("vdcShipMessage")
	}
	def"Get the VDC message from Label and the VDC Expected Deliverty Time call from cart page"(){
		given:
		shippingRepositoryToolsMock.getExpectedDeliveryTimeVDC("shippingMethod", "sku1234", true, _, false, false) >> "vdcDelTime"
		testObjCatalogTools.getMinimalSku("sku1234") >> skuDetailVOMock
		skuDetailVOMock.isLtlItem() >> false
		requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "BedBathUS"
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		testObjCatalogTools.getSKUDetails("BedBathUS", "sku1234") >>null
		lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_VDC_DEL_REST_CART_MSG, null, _,"BedBathUS") >> "vdcShipMessage"
		when:
		String value =  testObjCatalogTools.getVDCShipMessage("sku1234", true, "shippingMethod", new Date(), false)
		then:
		value.equals("vdcShipMessage")
	}
	def"Get the VDC message from Label and the VDC Expected Deliverty Time is null"(){
		given:
		shippingRepositoryToolsMock.getExpectedDeliveryTimeVDC("shippingMethod", "sku1234", true, _, false, false) >> null
		when:
		String value =  testObjCatalogTools.getVDCShipMessage("sku1234", true, "shippingMethod", new Date(), true)
		then:
		value.equals("")
	}
	def"The method takes shipping methood and sku Id and then check the shipping method from the eligible shipping methods of sku"(){
		given:
		ShipMethodVO ship1 = Mock()
		ShipMethodVO ship2 = Mock()
		ShipMethodVO ship3 = Mock()
		ship1.getShipMethodId()>> null
		ship2.getShipMethodId()>> "shipping"
		ship3.getShipMethodId()>> "LW"
		1*siteRepositoryToolsMock.getShippingMethodsForSku("BedBathUS", "SKU1234", false) >> [ship1,ship2,ship3]
		1*catalogRepositoryMock.getItem("SKU1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED) >> true
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["false"]
		when:
		boolean valid  = testObjCatalogTools.isShippingMethodExistsForSku("BedBathUS", "SKU1234", "LW", true)
		then:
		valid
	}
	def"The method takes shipping methood and sku Id and then check the shipping method from the eligible shipping methods of sku,assemble fee not offered for commerceitem"(){
		given:
		ShipMethodVO ship1 = Mock()
		ShipMethodVO ship2 = Mock()
		ShipMethodVO ship3 = Mock()
		ship1.getShipMethodId()>> null
		ship2.getShipMethodId()>> "shipping"
		ship3.getShipMethodId()>> "LW"
		1*siteRepositoryToolsMock.getShippingMethodsForSku("BedBathUS", "SKU1234", false) >> [ship1,ship2,ship3]
		0*catalogRepositoryMock.getItem("SKU1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED) >> true
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["false"]
		when:
		boolean valid  = testObjCatalogTools.isShippingMethodExistsForSku("BedBathUS", "SKU1234", "LW", false)
		then:
		valid == true
	}
	def"The method takes shipping methood and sku Id and then check the shipping method from the eligible shipping methods of sku,assemble fee not offered for sku"(){
		given:
		ShipMethodVO ship3 = Mock()
		ship3.getShipMethodId()>> "LW"
		1*siteRepositoryToolsMock.getShippingMethodsForSku("BedBathUS", "SKU1234", false) >> [ship3]
		1*catalogRepositoryMock.getItem("SKU1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED) >> false
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["false"]
		when:
		boolean valid  = testObjCatalogTools.isShippingMethodExistsForSku("BedBathUS", "SKU1234", "LW", true)
		then:
		valid == false
	}
	def"The method takes shipping methood and sku Id and then check the shipping method from the eligible shipping methods of sku,shipping method is not LW"(){
		given:
		ShipMethodVO ship3 = Mock()
		ship3.getShipMethodId()>> "shippingID"
		1*siteRepositoryToolsMock.getShippingMethodsForSku("BedBathUS", "SKU1234", false) >> [ship3]
		0*catalogRepositoryMock.getItem("SKU1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED) >> false
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["false"]
		when:
		boolean valid  = testObjCatalogTools.isShippingMethodExistsForSku("BedBathUS", "SKU1234", "shippingID", true)
		then:
		valid == true
	}
	def"The method takes shipping methood and sku Id and then check the shipping method from the eligible shipping methods of sku,shipping method not macthing "(){
		given:
		1*siteRepositoryToolsMock.getShippingMethodsForSku("BedBathUS", "SKU1234", false) >> []
		0*catalogRepositoryMock.getItem("SKU1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED) >> true
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> []
		when:
		boolean valid  = testObjCatalogTools.isShippingMethodExistsForSku("BedBathUS", "SKU1234", "LW", true)
		then:
		valid == false
	}
	def"isAssemblyFeeOffered,TC for skuid is offered assemble fee"(){
		given:
		1*catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED) >> true
		when:
		boolean valid = testObjCatalogTools.isAssemblyFeeOffered("sku1234")
		then:
		valid
	}
	def"isAssemblyFeeOffered,TC for skuid is offered assemble fee,sku id is empty"(){
		given:
		1*catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null
		when:
		boolean valid = testObjCatalogTools.isAssemblyFeeOffered("sku1234")
		then:
		Exception ex =thrown()
		ex.getMessage().equals("1000:1000")
	}
	def"isAssemblyFeeOffered,TC for skuid is offered assemble fee, throw reposiotory exception"(){
		given:
		1*catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		boolean valid = testObjCatalogTools.isAssemblyFeeOffered("sku1234")
		then:
		Exception ex =thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"isAssemblyFeeOffered,TC for skuid is offered assemble fee assembly offered property is empty"(){
		given:
		1*catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_ASSEMBLY_FEE_OFFERED) >> null
		when:
		boolean valid = testObjCatalogTools.isAssemblyFeeOffered("sku1234")
		then:
		valid  == false
	}
	def"The method gets the zoom index corresponding to a product id."(){
		given:
		testObjCatalogTools.getParentCategoryForProduct("prd1234", "BedBathUS") >> ["1":categoryVOMock]
		categoryVOMock.getZoomValue() >> 2.25
		when:
		int value = testObjCatalogTools.getZoomIndex("prd1234", "BedBathUS")
		then:
		1*siteRepositoryToolsMock.getBccManagedCategory(categoryVOMock)
		value==2
	}
	def"The method gets the zoom index corresponding to a product id.zoom value less than 1"(){
		given:
		testObjCatalogTools.getParentCategoryForProduct("prd1234", "BedBathUS") >> ["1":categoryVOMock]
		categoryVOMock.getZoomValue() >> 0.25
		when:
		int value = testObjCatalogTools.getZoomIndex("prd1234", "BedBathUS")
		then:
		1*siteRepositoryToolsMock.getBccManagedCategory(categoryVOMock)
		value==1
	}
	def"The method gets the zoom index corresponding to a product id.zoom value greater than 5"(){
		given:
		testObjCatalogTools.getParentCategoryForProduct("prd1234", "BedBathUS") >> ["1":categoryVOMock]
		categoryVOMock.getZoomValue() >> 6.25
		when:
		int value = testObjCatalogTools.getZoomIndex("prd1234", "BedBathUS")
		then:
		1*siteRepositoryToolsMock.getBccManagedCategory(categoryVOMock)
		value==5
	}
	def"The method gets the zoom index corresponding to a product id.noparent category"(){
		given:
		testObjCatalogTools.getParentCategoryForProduct("prd1234", "BedBathUS") >> [:]
		when:
		int value = testObjCatalogTools.getZoomIndex("prd1234", "BedBathUS")
		then:
		value==5
	}
	def"The method gets the zoom index corresponding to a product id.no a float"(){
		given:
		testObjCatalogTools.getParentCategoryForProduct("prd1234", "BedBathUS") >> ["1":categoryVOMock]
		categoryVOMock.getZoomValue()>>"asdf"
		when:
		int value = testObjCatalogTools.getZoomIndex("prd1234", "BedBathUS")
		then:
		value==5
	}
	def"The method gets the zoom index corresponding to a product id.throw business exception"(){
		given:
		testObjCatalogTools.getParentCategoryForProduct("prd1234", "BedBathUS") >>{throw new BBBBusinessException("Mock of businesse exception")}
		when:
		int value = testObjCatalogTools.getZoomIndex("prd1234", "BedBathUS")
		then:
		value==5
	}
	def"The method gets the zoom index corresponding to a product id.throw system exception"(){
		given:
		testObjCatalogTools.getParentCategoryForProduct("prd1234", "BedBathUS") >> {throw new BBBSystemException("Mock of system exception")}
		when:
		int value = testObjCatalogTools.getZoomIndex("prd1234", "BedBathUS")
		then:
		value==5
	}
	def"This method is used to return the value of EComFulfillment property in thSKU repository Item"(){
		given:
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.ECOM_FULFILLMENT_SKU_PROPERTY_NAME) >> "EComFulfillment"
		when:
		String value = testObjCatalogTools.getSkuEComFulfillment("sku1234")
		then:
		value.equals("EComFulfillment")
	}
	def"This method is used to return the value of EComFulfillment property in thSKU repository Item,ecom fullfilment property value is null"(){
		given:
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.ECOM_FULFILLMENT_SKU_PROPERTY_NAME) >> null
		when:
		String value = testObjCatalogTools.getSkuEComFulfillment("sku1234")
		then:
		value.equals(null)
	}
	def"This method is used to return the value of EComFulfillment property in thSKU repository Item,throw repositoryexception"(){
		given:
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		String value = testObjCatalogTools.getSkuEComFulfillment("sku1234")
		then:
		Exception ex =thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"This method is used to return the value of EComFulfillment property in thSKU repository Item,for sku repositoryitem is null"(){
		given:
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null
		when:
		String value = testObjCatalogTools.getSkuEComFulfillment("sku1234")
		then:
		Exception ex =thrown()
		ex.getMessage().equals("1000:1000")
	}
	def"This method is used to get the the ImageVo for the Product"(){
		given:
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME
		when:
		ImageVO imageVO = testObjCatalogTools.getProductImages("prd1234")
		then:
		imageVO.getCollectionThumbnailImage().equals(BBBCatalogConstants.COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME)
	}
	def"This method is used to get the the ImageVo for the Product,collectionThumbnail property is null"(){
		given:
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)>>BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME)>>BBBCatalogConstants.SMALL_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGULAR_IMAGE_IMAGE_PROPERTY_NAME) >>BBBCatalogConstants.REGULAR_IMAGE_IMAGE_PROPERTY_NAME
		when:
		ImageVO imageVO = testObjCatalogTools.getProductImages("prd1234")
		then:
		imageVO!= null
		imageVO.getCollectionThumbnailImage().equals(null)
		imageVO.getMediumImage().equals(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)
		imageVO.getSmallImage().equals(BBBCatalogConstants.REGULAR_IMAGE_IMAGE_PROPERTY_NAME)
		imageVO.getRegularImage().equals(null)
		imageVO.getLargeImage().equals(BBBCatalogConstants.LARGE_IMAGE_IMAGE_PROPERTY_NAME)
	}
	def"This method is used to get the the ImageVo for the Product,repository item is null"(){
		given:
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null
		when:
		ImageVO imageVO = testObjCatalogTools.getProductImages("prd1234")
		then:
		Exception ex =thrown()
		ex.getMessage().equals("1004:1004")
	}
	def"This method is used to get the the ImageVo for the Product, throw repositoryexception"(){
		given:
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		ImageVO imageVO = testObjCatalogTools.getProductImages("prd1234")
		then:
		Exception ex =thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"This method checks for the inclusion eligibility based on Department Id"(){
		when:
		boolean valid = testObjCatalogTools.checkForDeptSubDeptClassInclusion("1", "1", "0", "1", "1", "1")
		then:
		valid
	}
	def"This method checks for the inclusion eligibility based on Department Id ,rule dep id is null"(){
		when:
		boolean valid = testObjCatalogTools.checkForDeptSubDeptClassInclusion("", "1", "0", "1", "1", "1")
		then:
		valid == false
	}
	def"This method checks for the inclusion eligibility based on Department Id,ruledeptid is zero"(){
		when:
		boolean valid = testObjCatalogTools.checkForDeptSubDeptClassInclusion("0", "1", "0", "1", "1", "1")
		then:
		valid==false
	}
	def"This method checks for the inclusion eligibility based on Department Idrule dept id and sku jda dept id do not match"(){
		when:
		boolean valid = testObjCatalogTools.checkForDeptSubDeptClassInclusion("1", "0", "0", "1", "1", "1")
		then:
		valid == false
	}
	def"This method checks for the inclusion eligibility based on Department Idrule sub dept id is empty"(){
		when:
		boolean valid = testObjCatalogTools.checkForDeptSubDeptClassInclusion("1", "1", "", "1", "1", "1")
		then:
		valid == false
	}
	def"This method checks for the inclusion eligibility based on Department Id,rule sub dept id ans skujdasub dept id donot match"(){
		when:
		boolean valid = testObjCatalogTools.checkForDeptSubDeptClassInclusion("1", "1", "1", "0", "1", "1")
		then:
		valid == false
	}
	def"This method checks for the inclusion eligibility based on Department Id,rule class is zero "(){
		when:
		boolean valid = testObjCatalogTools.checkForDeptSubDeptClassInclusion("1", "1", "1", "1", "0", "1")
		then:
		valid
	}
	def"This method checks for the inclusion eligibility based on Department Id,rule class is empty "(){
		when:
		boolean valid = testObjCatalogTools.checkForDeptSubDeptClassInclusion("1", "1", "1", "1", "", "1")
		then:
		valid == false
	}
	def"This method checks for the inclusion eligibility based on Department Id,rule class and skujda class do not match "(){
		when:
		boolean valid = testObjCatalogTools.checkForDeptSubDeptClassInclusion("1", "1", "1", "1", "1", "0")
		then:
		valid == false
	}
	def"This method checks for the inclusion eligibility based on Department Id,rule class and skujda class do  match "(){
		when:
		boolean valid = testObjCatalogTools.checkForDeptSubDeptClassInclusion("1", "1", "1", "1", "1", "1")
		then:
		valid
	}
	def"This method checks for vendor inclusion eligibility for the commerce item"(){
		when:
		boolean valid = testObjCatalogTools.checkForVendorInclusion("vendorId", "vendorId", "1", true)
		then:
		valid
	}
	def"This method checks for vendor inclusion eligibility for the commerce item,rule dept id is zero"(){
		when:
		boolean valid = testObjCatalogTools.checkForVendorInclusion("vendorId", "vendorId", "0", true)
		then:
		valid
	}
	def"This method checks for vendor inclusion eligibility for the commerce item,isDeptSubDeptClassInclusive is false "(){
		when:
		boolean valid = testObjCatalogTools.checkForVendorInclusion("vendorId", "vendorId", "1", false)
		then:
		valid == false
	}
	def"This method checks for vendor inclusion eligibility for the commerce item,vendor is empty "(){
		when:
		boolean valid = testObjCatalogTools.checkForVendorInclusion("", "vendorId", "1", true)
		then:
		valid == false
	}
	def"This method checks for vendor inclusion eligibility for the commerce item,vendor is zero "(){
		when:
		boolean valid = testObjCatalogTools.checkForVendorInclusion("0", "vendorId", "1", true)
		then:
		valid == true
	}
	def"This method checks for vendor inclusion eligibility for the commerce item,vendor id do not match sku vendor id "(){
		when:
		boolean valid = testObjCatalogTools.checkForVendorInclusion("vendor", "vendorId", "1", true)
		then:
		valid == false
	}
	def"The method checks if invite"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCmsConstants.INVITE_FRIENDS_KEY) >> ["true"]
		when:
		boolean valid = testObjCatalogTools.isInviteFriends()
		then:
		valid
	}

	def"The method checks if invite,invite key is empty"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCmsConstants.INVITE_FRIENDS_KEY) >> []
		when:
		boolean valid = testObjCatalogTools.isInviteFriends()
		then:
		valid == false
	}

	def"The method checks if invite,throw business exception"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCmsConstants.INVITE_FRIENDS_KEY) >> {throw new BBBBusinessException("Mock of System exception")}
		when:
		boolean valid = testObjCatalogTools.isInviteFriends()
		then:
		valid == false
	}

	def"The method checks if invite,throw system exception"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCmsConstants.INVITE_FRIENDS_KEY) >> {throw new BBBSystemException("Mock of System exception")}
		when:
		boolean valid = testObjCatalogTools.isInviteFriends()
		then:
		valid == false
	}
	def"The method checks if VDC item restricted for international shipping"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_VDC_RESTRICTED) >> ["true"]
		when:
		boolean valid = testObjCatalogTools.isIntShippingVDCRestricted()
		then:
		valid
	}

	def"The method checks if VDC item restricted for international shipping,CONFIG_KEY_INTERNATIONAL_SHIP_VDC_RESTRICTED key is empty"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_VDC_RESTRICTED) >> []
		when:
		boolean valid = testObjCatalogTools.isIntShippingVDCRestricted()
		then:
		valid == true
	}

	def"The method checks if VDC item restricted for international shipping,throw business exception"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_VDC_RESTRICTED) >> {throw new BBBBusinessException("Mock of System exception")}
		when:
		boolean valid = testObjCatalogTools.isIntShippingVDCRestricted()
		then:
		valid == true
	}

	def"The method checks if VDC item restricted for international shipping,throw system exception"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_VDC_RESTRICTED) >> {throw new BBBSystemException("Mock of System exception")}
		when:
		boolean valid = testObjCatalogTools.isIntShippingVDCRestricted()
		then:
		valid == true
	}
	def"This method checks if the sku is eligible for international shipping or not"(){
		given:
		skuDetailVOMock.getSkuId() >> "sku1234"
		couponRepositoryMock.getItem("sku1234", BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU) >> repositoryItemMock
		repositoryItemMock.getRepositoryId() >> "sku1234"
		when:
		boolean valid = testObjCatalogTools.isSkuRestrictedForIntShip(skuDetailVOMock)
		then:
		valid
	}
	def"This method checks if the sku is eligible for international shipping or not,sku id and repository id are different "(){
		given:
		skuDetailVOMock.getSkuId() >> "sku1234"
		couponRepositoryMock.getItem("sku1234", BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU) >> repositoryItemMock
		repositoryItemMock.getRepositoryId() >> "sku12345"
		testObjCatalogTools.isIntShippingVDCRestricted()>> true
		skuDetailVOMock.isVdcSku() >> true
		when:
		boolean valid = testObjCatalogTools.isSkuRestrictedForIntShip(skuDetailVOMock)
		then:
		valid
	}
	def"This method checks if the sku is eligible for international shipping or not,sku is not aVDC "(){
		given:
		skuDetailVOMock.getSkuId() >> "sku1234"
		couponRepositoryMock.getItem("sku1234", BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU) >> null
		repositoryItemMock.getRepositoryId() >> "sku12345"
		testObjCatalogTools.isIntShippingVDCRestricted()>> true
		skuDetailVOMock.isVdcSku() >> false
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
		when:
		boolean valid = testObjCatalogTools.isSkuRestrictedForIntShip(skuDetailVOMock)
		then:
		valid
	}
	def"This method checks if the sku is eligible for international shipping or not,sku is not LTL "(){
		given:
		skuDetailVOMock.getSkuId() >> "sku1234"
		couponRepositoryMock.getItem("sku1234", BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU) >> null
		repositoryItemMock.getRepositoryId() >> "sku12345"
		testObjCatalogTools.isIntShippingVDCRestricted()>> true
		skuDetailVOMock.isVdcSku() >> false
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> true
		when:
		boolean valid = testObjCatalogTools.isSkuRestrictedForIntShip(skuDetailVOMock)
		then:
		valid
	}
	def"This method checks if the sku is eligible for international shipping or not,sku is not store sku "(){
		given:
		skuDetailVOMock.getSkuId() >> "sku1234"
		couponRepositoryMock.getItem("sku1234", BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU) >> null
		repositoryItemMock.getRepositoryId() >> "sku12345"
		testObjCatalogTools.isIntShippingVDCRestricted()>> true
		skuDetailVOMock.isVdcSku() >> false
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME) >> true
		when:
		boolean valid = testObjCatalogTools.isSkuRestrictedForIntShip(skuDetailVOMock)
		then:
		valid
	}
	def"This method checks if the sku is eligible for international shipping or not,sku is not a giftcret "(){
		given:
		skuDetailVOMock.getSkuId() >> "sku1234"
		couponRepositoryMock.getItem("sku1234", BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU) >> null
		repositoryItemMock.getRepositoryId() >> "sku12345"
		testObjCatalogTools.isIntShippingVDCRestricted()>> false
		skuDetailVOMock.isVdcSku() >> true
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> skuRepositoryItemMock
		skuRepositoryItemMock.getRepositoryId()>>"repId"
		testObjCatalogTools.executeRQLQuery(_, _,BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_JDA_DEPT, testObjCatalogTools.getCatalogRepository()) >> [repositoryItemMock]
		when:
		boolean valid = testObjCatalogTools.isSkuRestrictedForIntShip(skuDetailVOMock)
		then:
		valid == true
	}
	def"This method checks if the sku is eligible for international shipping or not,sku is not a jdaDept "(){
		given:
		skuDetailVOMock.getSkuId() >> "sku1234"
		couponRepositoryMock.getItem("sku1234", BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU) >> null
		repositoryItemMock.getRepositoryId() >> "sku12345"
		testObjCatalogTools.isIntShippingVDCRestricted()>> false
		skuDetailVOMock.isVdcSku() >> true
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> skuRepositoryItemMock
		skuRepositoryItemMock.getRepositoryId()>>"repId"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >>skuRepositoryItemMock
		testObjCatalogTools.executeRQLQuery(_, _,BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_JDA_DEPT, testObjCatalogTools.getCatalogRepository()) >> []
		testObjCatalogTools.executeRQLQuery(_, _,BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_JDA_SUB_DEPT, testObjCatalogTools.getCatalogRepository()) >> [repositoryItemMock]
		when:
		boolean valid = testObjCatalogTools.isSkuRestrictedForIntShip(skuDetailVOMock)
		then:
		valid
	}
	def"This method checks if the sku is eligible for international shipping or not,sku is not a jdaSubDept "(){
		given:
		skuDetailVOMock.getSkuId() >> "sku1234"
		couponRepositoryMock.getItem("sku1234", BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU) >> null
		repositoryItemMock.getRepositoryId() >> "sku12345"
		testObjCatalogTools.isIntShippingVDCRestricted()>> false
		skuDetailVOMock.isVdcSku() >> true
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getRepositoryId()>>"repId"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >>skuRepositoryItemMock
		testObjCatalogTools.executeRQLQuery(_, _,BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_JDA_SUB_DEPT, testObjCatalogTools.getCatalogRepository()) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) >>"repId"
		testObjCatalogTools.executeRQLQuery(_, _,BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_JDA_CLASS, testObjCatalogTools.getCatalogRepository()) >> [repositoryItemMock]
		when:
		boolean valid = testObjCatalogTools.isSkuRestrictedForIntShip(skuDetailVOMock)
		then:
		valid
	}
	def"This method checks if the sku is eligible for international shipping or not,sku is not a restrictedJdaclass "(){
		given:
		skuDetailVOMock.getSkuId() >> "sku1234"
		couponRepositoryMock.getItem("sku1234", BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU) >> null
		repositoryItemMock.getRepositoryId() >> "sku12345"
		testObjCatalogTools.isIntShippingVDCRestricted()>> false
		skuDetailVOMock.isVdcSku() >> true
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getRepositoryId()>>"repId"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >>skuRepositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_CLASS_SKU_PROPERTY_NAME) >>"repId"
		testObjCatalogTools.executeRQLQuery(_, _,BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_JDA_SUB_DEPT, testObjCatalogTools.getCatalogRepository()) >> null
		testObjCatalogTools.executeRQLQuery(_, _,BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_JDA_CLASS, testObjCatalogTools.getCatalogRepository()) >> []
		testObjCatalogTools.getParentProductForSku("sku1234") > "prd1234"
		when:
		boolean valid = testObjCatalogTools.isSkuRestrictedForIntShip(skuDetailVOMock)
		then:
		valid == false
	}
	def"This method checks if the sku is eligible for international shipping or not,sku is  a restrictedBBBBrands "(){
		given:
		skuDetailVOMock.getSkuId() >> "sku1234"
		couponRepositoryMock.getItem("sku1234", BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU) >> null
		repositoryItemMock.getRepositoryId() >> "sku12345"
		testObjCatalogTools.isIntShippingVDCRestricted()>> false
		skuDetailVOMock.isVdcSku() >> true
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getRepositoryId()>>"repId"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >>null
		testObjCatalogTools.getParentProductForSku("sku1234") >> "prd1234"
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_BRANDS) >> skuRepositoryItemMock
		testObjCatalogTools.executeRQLQuery(_, _,BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_BRANDS, testObjCatalogTools.getCatalogRepository()) >> [repositoryItemMock]
		when:
		boolean valid = testObjCatalogTools.isSkuRestrictedForIntShip(skuDetailVOMock)
		then:
		valid
	}
	def"This method checks if the sku is eligible for international shipping or not,sku is  a restrictedBBBBrands id is null "(){
		given:
		skuDetailVOMock.getSkuId() >> "sku1234"
		couponRepositoryMock.getItem("sku1234", BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU) >> null
		repositoryItemMock.getRepositoryId() >> "sku12345"
		testObjCatalogTools.isIntShippingVDCRestricted()>> false
		skuDetailVOMock.isVdcSku() >> true
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getRepositoryId()>>""
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >>null
		testObjCatalogTools.getParentProductForSku("sku1234") >> "prd1234"
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_BRANDS) >> skuRepositoryItemMock
		testObjCatalogTools.executeRQLQuery(_, _,BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_BRANDS, testObjCatalogTools.getCatalogRepository()) >> [repositoryItemMock]
		when:
		boolean valid = testObjCatalogTools.isSkuRestrictedForIntShip(skuDetailVOMock)
		then:
		valid == false
	}
	def"This method checks if the sku is eligible for international shipping or not,sku is  a restrictedBBBBrands is null "(){
		given:
		skuDetailVOMock.getSkuId() >> "sku1234"
		couponRepositoryMock.getItem("sku1234", BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU) >> null
		repositoryItemMock.getRepositoryId() >> "sku12345"
		testObjCatalogTools.isIntShippingVDCRestricted()>> false
		skuDetailVOMock.isVdcSku() >> true
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_STORE_SKU_SKU_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.GIFT_CERT_SKU_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getRepositoryId()>>"repId"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME) >>null
		testObjCatalogTools.getParentProductForSku("sku1234") >> "prd1234"
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_BRANDS) >> null
		when:
		boolean valid = testObjCatalogTools.isSkuRestrictedForIntShip(skuDetailVOMock)
		then:
		valid == false
	}
	def"This method checks if the sku is eligible for international shipping or not,sku repository item is null"(){
		given:
		skuDetailVOMock.getSkuId() >> "sku1234"
		couponRepositoryMock.getItem("sku1234", BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU) >> repositoryItemMock
		repositoryItemMock.getRepositoryId() >> "sku12345"
		testObjCatalogTools.isIntShippingVDCRestricted()>> true
		skuDetailVOMock.isVdcSku() >> false
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null
		when:
		boolean valid = testObjCatalogTools.isSkuRestrictedForIntShip(skuDetailVOMock)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1000:1000")
	}
	def"This method checks if the sku is eligible for international shipping or not,throw repository exception "(){
		given:
		skuDetailVOMock.getSkuId() >> "sku1234"
		couponRepositoryMock.getItem("sku1234", BBBCatalogConstants.ITEM_DESCRIPTOR_RESTRICTED_SKU) >> repositoryItemMock
		repositoryItemMock.getRepositoryId() >> "sku12345"
		testObjCatalogTools.isIntShippingVDCRestricted()>> true
		skuDetailVOMock.isVdcSku() >> false
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		boolean valid = testObjCatalogTools.isSkuRestrictedForIntShip(skuDetailVOMock)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"The method checks if the sku is ltl & vdc,site id is null"(){
		when:
		boolean isltl = testObjCatalogTools.isSkuLtl("", "")
		then:
		isltl == false
	}
	def"The method checks if the sku is ltl & vdc,sku id id is null"(){
		when:
		boolean isltl = testObjCatalogTools.isSkuLtl("BedBathUS", "")
		then:
		isltl == false
	}
	def"getDeliveryCharge ,tc for getting deliver charge"(){
		given:
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_WEIGHT) >>"3"
		1*cmsTool.getDeliveryCharge("BedBathUS", 3, "smethodCode") >> 3.0
		when:
		double value = testObjCatalogTools.getDeliveryCharge("BedBathUS", "sku1234", "smethodCode")
		then:
		value==3.0
	}
	def"getDeliveryCharge ,tc for getting deliver charge sku weigth is null"(){
		given:
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_WEIGHT) >>null
		1*cmsTool.getDeliveryCharge("BedBathUS", null, "smethodCode") >> 3.0
		when:
		double value = testObjCatalogTools.getDeliveryCharge("BedBathUS", "sku1234", "smethodCode")
		then:
		value==3.0
	}
	def"getDeliveryCharge ,tc for getting deliver charge throw repository exception"(){
		given:
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_WEIGHT) >>"3"
		0*cmsTool.getDeliveryCharge("BedBathUS", 3, "smethodCode") >> 3.0
		when:
		double value = testObjCatalogTools.getDeliveryCharge("BedBathUS", "sku1234", "smethodCode")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"getDeliveryCharge ,tc for getting deliver charge  repository item is null"(){
		given:
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_WEIGHT) >>"3"
		0*cmsTool.getDeliveryCharge("BedBathUS", 3, "smethodCode") >> 3.0
		when:
		double value = testObjCatalogTools.getDeliveryCharge("BedBathUS", "sku1234", "smethodCode")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1000:1000")
	}
	def"getDeliveryCharge ,tc for getting deliver charge,skuid is empty"(){
		given:
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_WEIGHT) >>"3"
		0*cmsTool.getDeliveryCharge("BedBathUS", 3, "smethodCode") >> 3.0
		when:
		double value = testObjCatalogTools.getDeliveryCharge("BedBathUS", "", "smethodCode")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"getDeliveryCharge ,tc for getting deliver charge,site id is empty"(){
		given:
		catalogRepositoryMock.getItem("sku1234",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_WEIGHT) >>"3"
		0*cmsTool.getDeliveryCharge("BedBathUS", 3, "smethodCode") >> 3.0
		when:
		double value = testObjCatalogTools.getDeliveryCharge("", "", "smethodCode")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"Calculate assembly charge for site specific SKU"(){
		given:
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.getMinimalSku(repositoryItemMock) >> skuDetailVOMock
		skuDetailVOMock.getAssemblyTime() >> "150"
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.ASSEMBLY_FEE_PER_THIRTY_MINUTES) >>["3"]
		when:
		double value= testObjCatalogTools.getAssemblyCharge("BedBathUS","sku1234")
		then:
		value == 15.0
	}
	def"Calculate assembly charge for site specific SKU,assembly_fee_per_thirty_minutes key value is null"(){
		given:
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.getMinimalSku(repositoryItemMock) >> skuDetailVOMock
		skuDetailVOMock.getAssemblyTime() >> ""
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.ASSEMBLY_FEE_PER_THIRTY_MINUTES) >>null
		when:
		double value= testObjCatalogTools.getAssemblyCharge("BedBathUS","sku1234")
		then:
		value == 0.0
	}
	def"Calculate assembly charge for site specific SKU,assembly_fee_per_thirty_minutes key value is empty"(){
		given:
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.getMinimalSku(repositoryItemMock) >> skuDetailVOMock
		skuDetailVOMock.getAssemblyTime() >> ""
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.ASSEMBLY_FEE_PER_THIRTY_MINUTES) >>[""]
		when:
		double value= testObjCatalogTools.getAssemblyCharge("BedBathUS","sku1234")
		then:
		value == 0.0
	}

	def"Calculate assembly charge for site specific SKU,throw repository exception"(){
		given:
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> {throw new RepositoryException()}
		when:
		double value= testObjCatalogTools.getAssemblyCharge("BedBathUS","sku1234")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"Calculate assembly charge for site specific SKU,site  id is null"(){
		when:
		double value= testObjCatalogTools.getAssemblyCharge("","")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"Calculate assembly charge for site specific SKU,sku id is null"(){
		when:
		double value= testObjCatalogTools.getAssemblyCharge("BedBathUS","")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"Gets the color swatch,product id is null"(){
		when:
		Map colorMap = testObjCatalogTools.getColorSwatch(null, null)
		then:
		colorMap.isEmpty()
	}
	def"Gets the color swatch,product id is product repository item is null"(){
		given:
		1*catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null
		when:
		Map colorMap = testObjCatalogTools.getColorSwatch("pr1234", null)
		then:
		colorMap.isEmpty()
	}
	def"Gets the color swatch,sku repository items is null"(){
		given:
		1*catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		when:
		Map colorMap = testObjCatalogTools.getColorSwatch("pr1234", null)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1000:1000")
	}
	def"Gets the color swatch,sku repository items is throw repository exception"(){
		given:
		1*catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		Map colorMap = testObjCatalogTools.getColorSwatch("pr1234", null)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"Gets the color swatch,product is not collection"(){
		given:
		1*catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> [repositoryItemMock,repositoryItemMock]
		when:
		Map colorMap = testObjCatalogTools.getColorSwatch("pr1234", new ProductVO(collection:true))
		then:
		colorMap.isEmpty()
	}
	def"This catalog method will update the SKU detail VO with the attributes required on the product comparison page if product is a single sku or lead product"(){
		given:
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		1*globalRepositoryToolsMock.isSkuActive(repositoryItemMock, _) >> true
		testObjCatalogTools.isSKUBelowLine("BedBathUs", "sku1234") >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.EMAIL_OUT_OF_STOCK_PRODUCT_PROPERTY_NAME) >>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME) >> true
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["true"]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.VDC_ATTRIBUTES_LIST) >> ["1"]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.LTL_DELIVERY_ATTRIBUTES_LIST) >> ["3"]
		DynamoHttpServletRequest thisrequestMock= Mock()
		ServletUtil.setCurrentRequest(thisrequestMock)
		thisrequestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> null
		Map skuattr = ["2":repositoryItemMock,"1":repositoryItemMock,"3":repositoryItemMock]
		testObjCatalogTools.getSkuAttributeList(repositoryItemMock, "BedBathUs", _, _, false) >> skuattr
		AttributeVO aVO =Mock()
		AttributeVO aVO1 =Mock()
		AttributeVO aVO2 =Mock()
		repositoryItemMock.getRepositoryId() >> "rep1234"
		repositoryItemMock.getPropertyValue("displayDescrip") >> "displayDescrip"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME) >> BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME) >> BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME
		siteRepositoryToolsMock.getSiteLevelAttributes(_, _)>>["1":aVO,"2":aVO1,"3":aVO2]
		CompareProductEntryVO cpvo = new CompareProductEntryVO()
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.IS_CUSTOMIZABLE_ON) >>["true"]
		repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >>  BBBCoreConstants.PERSONALIZATION_CODE_CR
		2*globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BedBathUs") >> true
		when:
		SKUDetailVO skuVO  = testObjCatalogTools.getComparisonSKUDetails("BedBathUs", "sku1234", true, cpvo)
		then:
		skuVO != null
		skuVO.isSkuBelowLine()
		cpvo.isEmailAlertOn()
		cpvo.isCustomizableRequired() == false
		cpvo.isCustomizationOffered()
		cpvo.getPersonalizationType().equalsIgnoreCase(BBBCoreConstants.PERSONALIZATION_CODE_CR)
		cpvo.getVdcSku().size()==1
		cpvo.getLtlAttributesList().size()==1
		cpvo.getAttributesList().size() == 1
		cpvo.getCustomizationCode().equals(null)
	}
	def"This catalog method will update the SKU detail VO with the attributes required on the product comparison page if product is a single sku or lead product,attribute list isempty"(){
		given:
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		1*globalRepositoryToolsMock.isSkuActive(repositoryItemMock, _) >> true
		testObjCatalogTools.isSKUBelowLine("BedBathUs", "sku1234") >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.EMAIL_OUT_OF_STOCK_PRODUCT_PROPERTY_NAME) >>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME) >> true
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> []
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.VDC_ATTRIBUTES_LIST) >> []
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.LTL_DELIVERY_ATTRIBUTES_LIST) >> []
		requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> null
		Map skuattr = ["1":repositoryItemMock,"2":repositoryItemMock,"3":repositoryItemMock]
		testObjCatalogTools.getSkuAttributeList(repositoryItemMock, "BedBathUs", _, _, false) >> skuattr
		AttributeVO aVO =Mock()
		AttributeVO aVO1 =Mock()
		AttributeVO aVO2 =Mock()
		repositoryItemMock.getRepositoryId() >> "rep1234"
		repositoryItemMock.getPropertyValue("displayDescrip") >> "displayDescrip"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME) >> BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME) >> BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME
		siteRepositoryToolsMock.getSiteLevelAttributes(_, _)>>[:]>>["1":aVO,"2":aVO1,"3":aVO2]
		CompareProductEntryVO cpvo = new CompareProductEntryVO()
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.IS_CUSTOMIZABLE_ON) >>["false"]
		repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >>  BBBCoreConstants.PERSONALIZATION_CODE_CR
		0*globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BedBathUs") >> true
		repositoryItemMock.getPropertyValue(BBBCoreConstants.ELIGIBLE_CUSTOMIZATION_CODES) >> "1,2"
		exManager.getEximValueMap() >> ["1":"a","2":null]
		when:
		SKUDetailVO skuVO  = testObjCatalogTools.getComparisonSKUDetails("BedBathUs", "sku1234", false, cpvo)
		then:
		skuVO != null
		skuVO.isSkuBelowLine() == false
		cpvo.isEmailAlertOn()
		cpvo.isCustomizableRequired() == false
		cpvo.isCustomizationOffered() == false
		cpvo.getPersonalizationType()==null
		cpvo.getVdcSku().size()==0
		cpvo.getLtlAttributesList().size()==0
		cpvo.getAttributesList()==null
		cpvo.getCustomizationCodeValues().size() ==1
		cpvo.getCustomizationCode().equals("1,2")
	}
	def"This catalog method will update the SKU detail VO with the attributes required on the product comparison page if product is a single sku or lead product,attribute list is not empty"(){
		given:
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		1*globalRepositoryToolsMock.isSkuActive(repositoryItemMock, _) >> true
		testObjCatalogTools.isSKUBelowLine("BedBathUs", "sku1234") >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.EMAIL_OUT_OF_STOCK_PRODUCT_PROPERTY_NAME) >>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME) >> true
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["true"]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.VDC_ATTRIBUTES_LIST) >> []
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.LTL_DELIVERY_ATTRIBUTES_LIST) >> []
		requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
		sessionBeanMock.getCurrentZipcodeVO() >> null
		Map skuattr = ["1":repositoryItemMock,"2":repositoryItemMock,"3":repositoryItemMock]
		testObjCatalogTools.getSkuAttributeList(repositoryItemMock, "BedBathUs", _, _, false) >> skuattr
		AttributeVO aVO =Mock()
		AttributeVO aVO1 =Mock()
		AttributeVO aVO2 =Mock()
		repositoryItemMock.getRepositoryId() >> "rep1234"
		repositoryItemMock.getPropertyValue("displayDescrip") >> "displayDescrip"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME) >> BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME) >> BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME
		siteRepositoryToolsMock.getSiteLevelAttributes(_, _)>>["1":aVO,"2":aVO1,"3":aVO2]>>[:]
		CompareProductEntryVO cpvo = new CompareProductEntryVO()
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.IS_CUSTOMIZABLE_ON) >>["false"]
		repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >>  BBBCoreConstants.PERSONALIZATION_CODE_CR
		0*globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BedBathUs") >> true
		repositoryItemMock.getPropertyValue(BBBCoreConstants.ELIGIBLE_CUSTOMIZATION_CODES) >> "1,2"
		exManager.getEximValueMap() >> ["1":"a","2":null]
		when:
		SKUDetailVO skuVO  = testObjCatalogTools.getComparisonSKUDetails("BedBathUs", "sku1234", false, cpvo)
		then:
		skuVO != null
		skuVO.isSkuBelowLine() == false
		cpvo.isEmailAlertOn()
		cpvo.isCustomizableRequired() == false
		cpvo.isCustomizationOffered() == false
		cpvo.getPersonalizationType()==null
		cpvo.getVdcSku()==null
		cpvo.getLtlAttributesList()== null
		cpvo.getAttributesList().size()==3
		cpvo.getCustomizationCodeValues().size() ==1
		cpvo.getCustomizationCode().equals("1,2")
	}
	def"This catalog method will update the SKU detail VO with the attributes required on the product comparison page if product is a single sku or lead product,attribute list is not empty,vdcskutype is null in repository item"(){
		given:
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		1*globalRepositoryToolsMock.isSkuActive(repositoryItemMock, _) >> true
		testObjCatalogTools.isSKUBelowLine("BedBathUs", "sku1234") >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.EMAIL_OUT_OF_STOCK_PRODUCT_PROPERTY_NAME) >>true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME) >> null
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["true"]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.VDC_ATTRIBUTES_LIST) >> []
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.LTL_DELIVERY_ATTRIBUTES_LIST) >> []
		requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
		sessionBeanMock.getCurrentZipcodeVO() >> sddZipcodeVOMock
		sddZipcodeVOMock.getPromoAttId()>>"regionPromoAttr"
		sddZipcodeVOMock.isSddEligibility() >> true
		Map skuattr = ["1":repositoryItemMock,"2":repositoryItemMock,"3":repositoryItemMock]
		testObjCatalogTools.getSkuAttributeList(repositoryItemMock, "BedBathUs", _, "regionPromoAttr", true) >> [:]
		AttributeVO aVO =Mock()
		AttributeVO aVO1 =Mock()
		AttributeVO aVO2 =Mock()
		repositoryItemMock.getRepositoryId() >> "rep1234"
		repositoryItemMock.getPropertyValue("displayDescrip") >> "displayDescrip"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME) >> BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME) >> BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME
		siteRepositoryToolsMock.getSiteLevelAttributes(_, _)>>["1":aVO,"2":aVO1,"3":aVO2]>>[:]
		CompareProductEntryVO cpvo = new CompareProductEntryVO()
		testObjCatalogTools.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.IS_CUSTOMIZABLE_ON) >>["false"]
		repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >>  BBBCoreConstants.PERSONALIZATION_CODE_CR
		0*globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BedBathUs") >> true
		repositoryItemMock.getPropertyValue(BBBCoreConstants.ELIGIBLE_CUSTOMIZATION_CODES) >> "1,2"
		exManager.getEximValueMap() >> ["1":"a","2":null]
		when:
		SKUDetailVO skuVO  = testObjCatalogTools.getComparisonSKUDetails("BedBathUs", "sku1234", false, cpvo)
		then:
		skuVO != null
		skuVO.isSkuBelowLine() == false
		cpvo.isEmailAlertOn()
		cpvo.isCustomizableRequired() == false
		cpvo.isCustomizationOffered() == false
		cpvo.getPersonalizationType()==null
		cpvo.getVdcSku()==null
		cpvo.getLtlAttributesList()== null
		cpvo.getAttributesList()==null
		cpvo.getCustomizationCodeValues().size() ==1
		cpvo.getCustomizationCode().equals("1,2")
	}
	def"This catalog method will update the SKU detail VO with the attributes required on the product comparison page if product is a single sku or lead product,sku id is null"(){
		when:
		SKUDetailVO skuVO  = testObjCatalogTools.getComparisonSKUDetails("BedBathUs", "", false, null)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"This catalog method will update the SKU detail VO with the attributes required on the product comparison page if product is a single sku or lead product,site id id is null"(){
		when:
		SKUDetailVO skuVO  = testObjCatalogTools.getComparisonSKUDetails("", "", false, null)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"This catalog method will update the SKU detail VO with the attributes required on the product comparison page if product is a single sku or lead product,sku repository item is null"(){
		given:
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null
		when:
		SKUDetailVO skuVO  = testObjCatalogTools.getComparisonSKUDetails("BedBathUs", "sku1234", false, null)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1000:1000")
	}
	def"This catalog method will update the SKU detail VO with the attributes required on the product comparison page if product is a single sku or lead product,throw repository exception"(){
		given:
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		SKUDetailVO skuVO  = testObjCatalogTools.getComparisonSKUDetails("BedBathUs", "sku1234", false, null)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"This method provides Review Rating of any product"(){
		given:
		siteRepositoryMock.getView(BBBCatalogConstants.BAZAAR_VOICE) >> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >>qBuilder
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.PRODUCT_ID)>>qExp
		qBuilder.createConstantQueryExpression("pProductId")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		repositoryViewMock.executeQuery(query)>> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.EXTERNAL_ID)>>BBBCatalogConstants.EXTERNAL_ID
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.TOTAL_REVIEW_COUNT) >> 3
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.AVERAGE_OVERALL_RATING) >> 3.6f
		when:
		BazaarVoiceProductVO ratingVO =  testObjCatalogTools.getBazaarVoiceDetails("pProductId")
		then:
		ratingVO.isRatingAvailable()
		ratingVO.getAverageOverallRating() == 3.6f
		ratingVO.getTotalReviewCount() == 3
		ratingVO.getExternalId().equals(BBBCatalogConstants.EXTERNAL_ID)
	}
	def"This method provides Review Rating of any product,average total rating property is null"(){
		given:
		siteRepositoryMock.getView(BBBCatalogConstants.BAZAAR_VOICE) >> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >>qBuilder
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.PRODUCT_ID)>>qExp
		qBuilder.createConstantQueryExpression("pProductId")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		repositoryViewMock.executeQuery(query)>> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.EXTERNAL_ID)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.TOTAL_REVIEW_COUNT) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.AVERAGE_OVERALL_RATING) >> null
		when:
		BazaarVoiceProductVO ratingVO =  testObjCatalogTools.getBazaarVoiceDetails("pProductId")
		then:
		ratingVO.isRatingAvailable() == false
	}
	def"This method provides Review Rating of any product throw repository exception"(){
		given:
		siteRepositoryMock.getView(BBBCatalogConstants.BAZAAR_VOICE) >> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >>qBuilder
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.PRODUCT_ID)>>qExp
		qBuilder.createConstantQueryExpression("pProductId")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		repositoryViewMock.executeQuery(query)>> {throw new RepositoryException()}
		when:
		BazaarVoiceProductVO ratingVO =  testObjCatalogTools.getBazaarVoiceDetails("pProductId")
		then:
		ratingVO.isRatingAvailable() == false
	}
	def"This method provides Review Rating of any product,length of repository item is length zero"(){
		given:
		siteRepositoryMock.getView(BBBCatalogConstants.BAZAAR_VOICE) >> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >>qBuilder
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.PRODUCT_ID)>>qExp
		qBuilder.createConstantQueryExpression("pProductId")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		repositoryViewMock.executeQuery(query)>> []
		when:
		BazaarVoiceProductVO ratingVO =  testObjCatalogTools.getBazaarVoiceDetails("pProductId")
		then:
		ratingVO.isRatingAvailable()== false
	}
	def"This method provides Review Rating of any product,length of repository item is null"(){
		given:
		siteRepositoryMock.getView(BBBCatalogConstants.BAZAAR_VOICE) >> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >>qBuilder
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.PRODUCT_ID)>>qExp
		qBuilder.createConstantQueryExpression("pProductId")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		repositoryViewMock.executeQuery(query)>> null
		when:
		BazaarVoiceProductVO ratingVO =  testObjCatalogTools.getBazaarVoiceDetails("pProductId")
		then:
		ratingVO.isRatingAvailable()== false
	}
	def"This method provides Review Rating of any product,length of repository item is contians null value"(){
		given:
		siteRepositoryMock.getView(BBBCatalogConstants.BAZAAR_VOICE) >> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >>qBuilder
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.PRODUCT_ID)>>qExp
		qBuilder.createConstantQueryExpression("pProductId")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		repositoryViewMock.executeQuery(query)>> [null]
		when:
		BazaarVoiceProductVO ratingVO =  testObjCatalogTools.getBazaarVoiceDetails("pProductId")
		then:
		ratingVO.isRatingAvailable()== false
	}
	def"This method provides Review Rating of any product,product id is null"(){
		when:
		BazaarVoiceProductVO ratingVO =  testObjCatalogTools.getBazaarVoiceDetails(null)
		then:
		ratingVO.isRatingAvailable()==false
	}
	def"This is an overloaded method which provides Review Rating of any product based on productid and site id"(){
		given:
		siteRepositoryMock.getView(BBBCatalogConstants.BAZAAR_VOICE) >> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >>qBuilder
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.PRODUCT_ID)>>qExp
		qBuilder.createConstantQueryExpression("pProductId")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.SITE_ID)>>qExp
		qBuilder.createConstantQueryExpression("")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		qBuilder.createAndQuery(query,query) >> query
		repositoryViewMock.executeQuery(query)>> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.EXTERNAL_ID)>>BBBCatalogConstants.EXTERNAL_ID
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.TOTAL_REVIEW_COUNT) >> 3
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.AVERAGE_OVERALL_RATING) >> 3.6f
		testObjCatalogTools.setBazaarVoiceSiteSpecific(true)
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_ID) >>BBBCatalogConstants.SITE_ID
		when:
		BazaarVoiceProductVO ratingVO =  testObjCatalogTools.getBazaarVoiceDetails("pProductId","")
		then:
		ratingVO.isRatingAvailable()
		ratingVO.getAverageOverallRating() == 3.6f
		ratingVO.getTotalReviewCount() == 3
		ratingVO.getExternalId().equals(BBBCatalogConstants.EXTERNAL_ID)
		ratingVO.getSiteId().equals(BBBCatalogConstants.SITE_ID)
	}
	def"This is an overloaded method which provides Review Rating of any product based on productid and site id is tbs_buybuybaby"(){
		given:
		siteRepositoryMock.getView(BBBCatalogConstants.BAZAAR_VOICE) >> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >>qBuilder
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.PRODUCT_ID)>>qExp
		qBuilder.createConstantQueryExpression("pProductId")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.SITE_ID)>>qExp
		qBuilder.createConstantQueryExpression("")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		qBuilder.createAndQuery(query,query) >> query
		repositoryViewMock.executeQuery(query)>> [repositoryItemMock]
		testObjCatalogTools.setBazaarVoiceSiteSpecific(false)
		when:
		BazaarVoiceProductVO ratingVO =  testObjCatalogTools.getBazaarVoiceDetails("pProductId","TBS_BuyBuyBaby")
		then:
		ratingVO.isRatingAvailable()==false
	}
	def"This is an overloaded method which provides Review Rating of any product based on productid and site id is GS_BedBathUS"(){
		given:
		siteRepositoryMock.getView(BBBCatalogConstants.BAZAAR_VOICE) >> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >>qBuilder
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.PRODUCT_ID)>>qExp
		qBuilder.createConstantQueryExpression("pProductId")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.SITE_ID)>>qExp
		qBuilder.createConstantQueryExpression("")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		qBuilder.createAndQuery(query,query) >> query
		repositoryViewMock.executeQuery(query)>> null
		testObjCatalogTools.setBazaarVoiceSiteSpecific(false)
		when:
		BazaarVoiceProductVO ratingVO =  testObjCatalogTools.getBazaarVoiceDetails("pProductId","GS_BuyBuyBaby")
		then:
		ratingVO.isRatingAvailable()==false
	}
	def"This is an overloaded method which provides Review Rating of any product based on productid and site id ,length of repository items is zero"(){
		given:
		siteRepositoryMock.getView(BBBCatalogConstants.BAZAAR_VOICE) >> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >>qBuilder
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.PRODUCT_ID)>>qExp
		qBuilder.createConstantQueryExpression("pProductId")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.SITE_ID)>>qExp
		qBuilder.createConstantQueryExpression("")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		qBuilder.createAndQuery(query,query) >> query
		repositoryViewMock.executeQuery(query)>> []
		testObjCatalogTools.setBazaarVoiceSiteSpecific(false)
		when:
		BazaarVoiceProductVO ratingVO =  testObjCatalogTools.getBazaarVoiceDetails("pProductId","GS_BuyBuyBaby")
		then:
		ratingVO.isRatingAvailable()==false
	}
	def"This is an overloaded method which provides Review Rating of any product based on productid and site id ,throw repository exception"(){
		given:
		siteRepositoryMock.getView(BBBCatalogConstants.BAZAAR_VOICE) >> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >>qBuilder
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.PRODUCT_ID)>>qExp
		qBuilder.createConstantQueryExpression("pProductId")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		qBuilder.createPropertyQueryExpression(BBBCatalogConstants.SITE_ID)>>qExp
		qBuilder.createConstantQueryExpression("")>>qExp
		qBuilder.createComparisonQuery(qExp, qExp,  QueryBuilder.EQUALS) >>query
		qBuilder.createAndQuery(query,query) >> query
		repositoryViewMock.executeQuery(query)>> []
		testObjCatalogTools.setBazaarVoiceSiteSpecific(false)
		when:
		BazaarVoiceProductVO ratingVO =  testObjCatalogTools.getBazaarVoiceDetails("pProductId","GS_BuyBuyBaby")
		then:
		ratingVO.isRatingAvailable()==false
	}
	def"Product Comparison page. This catalog method will update the Product VO with the attributes required on the product comparison page for products."(){
		given:
		Date preview =new Date()
		CompareProductEntryVO cpeo = new CompareProductEntryVO()
		catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.isProductActive(repositoryItemMock)>> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> "n"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)>> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)>> BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME
		testObjCatalogTools.getBazaarVoiceDetails("pr1234", "BedBathUS")>>new BazaarVoiceProductVO()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME) >> "0.0001%L%l%H%h"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME
		testObjCatalogTools.isSkuOnSale("pr1234",_)>> true
		lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,_) >> BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME
		RepositoryItem productAttributeRelns = Mock()
		RepositoryItem productAttributeRelns1 = Mock()
		RepositoryItem productAttributeRelns2 = Mock()
		RepositoryItem  prodAttributeRepositoryItem = Mock()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [productAttributeRelns,productAttributeRelns1,productAttributeRelns2].toSet()
		productAttributeRelns.getPropertyValue("bedbathUSFlag") >> false
		productAttributeRelns1.getPropertyValue("bedbathUSFlag") >> true
		productAttributeRelns2.getPropertyValue("bedbathUSFlag") >> true
		productAttributeRelns1.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> null
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> prodAttributeRepositoryItem
		SddZipcodeVO zipcodeVO= new SddZipcodeVO()
		zipcodeVO.setPromoAttId("promoAttId")
		zipcodeVO.setSddEligibility(true)
		sessionBeanMock.getCurrentZipcodeVO() >> zipcodeVO
		sessionBeanMock.isInternationalShippingContext() >> true
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.START_DATE_PROD_RELATION_PROPERTY_NAME) >> preview
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.END_DATE_PROD_RELATION_PROPERTY_NAME)   >> preview
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_PROD_RELATION_PROPERTY_NAME) >> preview
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_PROD_RELATION_PROPERTY_NAME)   >> preview
		prodAttributeRepositoryItem.getRepositoryId() >> "prdRel1234"
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG) >> 'Y'
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY,BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> []
		BBBConfigRepoUtils.setBbbCatalogTools(catalogTools)
		catalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["true"]
		testObjCatalogTools.setPreviewEnabled(true)
		PreviewAttributes previewAttributes = Mock()
		previewAttributes.getPreviewDate() >> preview
		requestMock.resolveName("/com/bbb/commerce/catalog/PreviewAttributes") >> previewAttributes
		testObjCatalogTools.setBBBSiteToAttributeSiteMap(siteToAttributeSiteMap)
		globalRepositoryToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PREVIEW_ENABLED) >> ["true"]
		testObjCatalogTools.setZoomKeys("prdRel1234")
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "prdRel1234") >> ["prdRel1234"]
		repositoryItemMock.getPropertyValue("giftCertProduct") >> true
		AttributeVO aVO =Mock()
		AttributeVO aVO1 =Mock()
		AttributeVO aVO2 =Mock()
		prodAttributeRepositoryItem.getPropertyValue("displayDescrip") >> "displayDescrip"
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME) >> BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME) >> BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME
		siteRepositoryToolsMock.getSiteLevelAttributes(_, _)>>["1":aVO,"prdRel1234":aVO1,"3":aVO2]
		catalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.LTL_DELIVERY_ATTRIBUTES_LIST) >>["prdRel1234"]
		when:
		ProductVO pVO =  testObjCatalogTools.getComparisonProductDetails("pr1234", "BedBathUS", cpeo)
		then:
		pVO.isLtlProduct()
		pVO.isCollection()
		pVO.getProductImages().getMediumImage().equals(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)
		pVO.getProductImages().getThumbnailImage().equals(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME)
		pVO.getName().equals(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)
		pVO.getLongDescription().equals(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)
		pVO.getShortDescription().equals(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)
		pVO.getDefaultPriceRangeDescription().equals("0.0001%L%l%H%h")
		pVO.getSkuLowPrice().equals(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME)
		pVO.getSkuHighPrice().equals(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME)
		pVO.getHighPrice() == 0.00
		pVO.getLowPrice()==0.00
		pVO.getSalePriceRangeDescriptionRepository().equals("0.0001\$0.00%l%H%h")
		cpeo.getLtlAttributesList().size()==1
		cpeo.getLtlAttributesList().get("prdRel1234").getActionURL().equals(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME)
		cpeo.getLtlAttributesList().get("prdRel1234").getImageURL().equals(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME)
		cpeo.getLtlAttributesList().get("prdRel1234").getAttributeDescrip().equals("displayDescrip")
		cpeo.getAttributesList().size()==2
	}
	def"Product Comparison page. This catalog method will update the Product VO with the attributes required on the product comparison page for products,child products is size greater than one."(){
		given:
		Date preview =new Date()
		CompareProductEntryVO cpeo = new CompareProductEntryVO()
		catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.isProductActive(repositoryItemMock)>> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> "n"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)>> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)>> BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME
		testObjCatalogTools.getBazaarVoiceDetails("pr1234", "BedBathUS")>>new BazaarVoiceProductVO()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME) >> "0.0001%H%h"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME
		testObjCatalogTools.isSkuOnSale("pr1234",_)>> true
		lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,_) >> BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> [skuRepositoryItemMock,skuRepositoryItemMock]
		RepositoryItem productAttributeRelns = Mock()
		RepositoryItem productAttributeRelns1 = Mock()
		RepositoryItem productAttributeRelns2 = Mock()
		RepositoryItem  prodAttributeRepositoryItem = Mock()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [productAttributeRelns,productAttributeRelns1,productAttributeRelns2].toSet()
		productAttributeRelns.getPropertyValue("bedbathUSFlag") >> false
		productAttributeRelns1.getPropertyValue("bedbathUSFlag") >> true
		productAttributeRelns2.getPropertyValue("bedbathUSFlag") >> true
		productAttributeRelns1.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> null
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> prodAttributeRepositoryItem
		SddZipcodeVO zipcodeVO= new SddZipcodeVO()
		zipcodeVO.setPromoAttId("promoAttId")
		zipcodeVO.setSddEligibility(true)
		sessionBeanMock.getCurrentZipcodeVO() >> zipcodeVO
		sessionBeanMock.isInternationalShippingContext() >> true
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.START_DATE_PROD_RELATION_PROPERTY_NAME) >> preview
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.END_DATE_PROD_RELATION_PROPERTY_NAME)   >> preview
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_PROD_RELATION_PROPERTY_NAME) >> preview
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_PROD_RELATION_PROPERTY_NAME)   >> preview
		prodAttributeRepositoryItem.getRepositoryId() >> "prdRel1234"
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG) >> 'Y'
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY,BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> []
		BBBConfigRepoUtils.setBbbCatalogTools(catalogTools)
		catalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["true"]
		testObjCatalogTools.setPreviewEnabled(true)
		PreviewAttributes previewAttributes = Mock()
		previewAttributes.getPreviewDate() >> preview
		requestMock.resolveName("/com/bbb/commerce/catalog/PreviewAttributes") >> previewAttributes
		testObjCatalogTools.setBBBSiteToAttributeSiteMap(siteToAttributeSiteMap)
		globalRepositoryToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PREVIEW_ENABLED) >> ["true"]
		testObjCatalogTools.setZoomKeys("prdRel1234")
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "prdRel1234") >> ["prdRel1234"]
		repositoryItemMock.getPropertyValue("giftCertProduct") >> true
		siteRepositoryToolsMock.getSiteLevelAttributes(_, _)>>[:]
		catalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.LTL_DELIVERY_ATTRIBUTES_LIST) >>["prdRel1234"]
		when:
		ProductVO pVO =  testObjCatalogTools.getComparisonProductDetails("pr1234", "BedBathUS", cpeo)
		then:
		pVO.isLtlProduct()
		pVO.isCollection() == false
		pVO.getProductImages().getMediumImage().equals(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)
		pVO.getProductImages().getThumbnailImage().equals(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME)
		pVO.getName().equals(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)
		pVO.getLongDescription().equals(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)
		pVO.getShortDescription().equals(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)
		pVO.getDefaultPriceRangeDescription().equals("0.0001%H%h")
		pVO.getSkuLowPrice().equals(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME)
		pVO.getSkuHighPrice().equals(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME)
		pVO.getHighPrice() == 0.00
		pVO.getLowPrice()==0.00
		pVO.getSalePriceRangeDescriptionRepository().equals("0.0001\$0.00%h")
		cpeo.isMultiSku()
	}
	def"Product Comparison page. This catalog method will update the Product VO with the attributes required on the product comparison page for products,child products is ltl product."(){
		given:
		Date preview =new Date()
		CompareProductEntryVO cpeo = new CompareProductEntryVO()
		catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.isProductActive(repositoryItemMock)>> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> "n"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)>> null
		testObjCatalogTools.getBazaarVoiceDetails("pr1234", "BedBathUS")>>new BazaarVoiceProductVO()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME) >>null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME
		testObjCatalogTools.isSkuOnSale("pr1234",_)>> false
		lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,_) >> BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> [skuRepositoryItemMock]
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> null
		RepositoryItem productAttributeRelns = Mock()
		RepositoryItem productAttributeRelns1 = Mock()
		RepositoryItem productAttributeRelns2 = Mock()
		RepositoryItem  prodAttributeRepositoryItem = Mock()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [].toSet()
		when:
		ProductVO pVO =  testObjCatalogTools.getComparisonProductDetails("pr1234", "BedBathUS", cpeo)
		then:
		pVO.isLtlProduct()
		pVO.isCollection() == false
		pVO.getProductImages().getMediumImage().equals(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)
		pVO.getProductImages().getThumbnailImage().equals(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME)
		pVO.getName().equals(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)
		pVO.getShortDescription().equals(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)
		pVO.getSkuLowPrice().equals(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME)
		pVO.getSkuHighPrice().equals(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME)
		cpeo.isMultiSku() == false
	}
	def"Product Comparison page. This catalog method will update the Product VO with the attributes required on the product comparison page for products,list and sale price greater than sero ."(){
		given:
		Date preview =new Date()
		CompareProductEntryVO cpeo = new CompareProductEntryVO()
		catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.isProductActive(repositoryItemMock)>> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> "n"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> false
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)>> null
		testObjCatalogTools.getBazaarVoiceDetails("pr1234", "BedBathUS")>>new BazaarVoiceProductVO()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME) >>"0.0001%h"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME
		testObjCatalogTools.isSkuOnSale("pr1234",_)>> true
		testObjCatalogTools.getListPrice(_,_)>> 10.0
		testObjCatalogTools.getSalePrice(_,_)>> 10.0
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "WarrantyPrice")>>["5"]>>["11"]
		lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,_) >> BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> [skuRepositoryItemMock]
		catalogRepositoryMock.getItem(_, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >>skuRepositoryItemMock
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> null
		RepositoryItem productAttributeRelns = Mock()
		RepositoryItem productAttributeRelns1 = Mock()
		RepositoryItem productAttributeRelns2 = Mock()
		RepositoryItem  prodAttributeRepositoryItem = Mock()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [].toSet()
		when:
		ProductVO pVO =  testObjCatalogTools.getComparisonProductDetails("pr1234", "BedBathUS", cpeo)
		then:
		pVO.isLtlProduct() == false
		pVO.isCollection() == false
		pVO.getProductImages().getMediumImage().equals(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)
		pVO.getProductImages().getThumbnailImage().equals(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME)
		pVO.getName().equals(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)
		pVO.getShortDescription().equals(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)
		pVO.getSkuLowPrice().equals(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME)
		pVO.getSkuHighPrice().equals(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME)
		cpeo.isMultiSku() == false
	}
	def"Product Comparison page. This catalog method will update the Product VO with the attributes required on the product comparison page for products,sku low price is zero."(){
		given:
		Date preview =new Date()
		CompareProductEntryVO cpeo = new CompareProductEntryVO()
		catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.isProductActive(repositoryItemMock)>> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> "n"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)>> null
		testObjCatalogTools.getBazaarVoiceDetails("pr1234", "BedBathUS")>>new BazaarVoiceProductVO()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME) >>"0.0001%l"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME
		testObjCatalogTools.isSkuOnSale("pr1234",_)>> true
		testObjCatalogTools.getListPrice(_,_)>> 10.0
		testObjCatalogTools.getSalePrice(_,_)>> 10.0
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "WarrantyPrice")>>["5"]>>["11"]
		lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,_) >> BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> null
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> null
		RepositoryItem productAttributeRelns = Mock()
		RepositoryItem productAttributeRelns1 = Mock()
		RepositoryItem productAttributeRelns2 = Mock()
		RepositoryItem  prodAttributeRepositoryItem = Mock()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [].toSet()
		when:
		ProductVO pVO =  testObjCatalogTools.getComparisonProductDetails("pr1234", "BedBathUS", cpeo)
		then:
		pVO.isLtlProduct()
		pVO.isCollection() == false
		pVO.getProductImages().getMediumImage().equals(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)
		pVO.getProductImages().getThumbnailImage().equals(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME)
		pVO.getName().equals(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)
		pVO.getShortDescription().equals(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)
		pVO.getSkuLowPrice().equals(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME)
		pVO.getSkuHighPrice().equals(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME)
		cpeo.isMultiSku() == false
	}
	def"Product Comparison page. This catalog method will update the Product VO with the attributes required on the product comparison page for products,sku high price is zero ."(){
		given:
		Date preview =new Date()
		CompareProductEntryVO cpeo = new CompareProductEntryVO()
		catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.isProductActive(repositoryItemMock)>> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> "n"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)>> null
		testObjCatalogTools.getBazaarVoiceDetails("pr1234", "BedBathUS")>>new BazaarVoiceProductVO()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME) >>"0.0001%h"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME) >> null
		testObjCatalogTools.isSkuOnSale("pr1234",_)>> false
		testObjCatalogTools.getSalePrice(_,_)>> 0.01
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "WarrantyPrice")>>["5"]>>["11"]
		lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,_) >> BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> [skuRepositoryItemMock]
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> null
		RepositoryItem productAttributeRelns = Mock()
		RepositoryItem productAttributeRelns1 = Mock()
		RepositoryItem productAttributeRelns2 = Mock()
		RepositoryItem  prodAttributeRepositoryItem = Mock()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [].toSet()
		when:
		ProductVO pVO =  testObjCatalogTools.getComparisonProductDetails("pr1234", "BedBathUS", cpeo)
		then:
		pVO.isLtlProduct()
		pVO.isCollection() == false
		pVO.getProductImages().getMediumImage().equals(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)
		pVO.getProductImages().getThumbnailImage().equals(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME)
		pVO.getName().equals(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)
		pVO.getShortDescription().equals(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)
		pVO.getSkuLowPrice().equals(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME)
		cpeo.isMultiSku() == false
	}
	def"Product Comparison page. This catalog method will update the Product VO with the attributes required on the product comparison page for products,compare product as sku id"(){
		given:
		Date preview =new Date()
		CompareProductEntryVO cpeo = new CompareProductEntryVO()
		cpeo.setSkuId("sku1234")
		catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.isProductActive(repositoryItemMock)>> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> "n"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) >> BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)>> BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME
		testObjCatalogTools.getBazaarVoiceDetails("pr1234", "BedBathUS")>>new BazaarVoiceProductVO()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME) >>"0.000%l"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME) >> BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME
		testObjCatalogTools.isSkuOnSale("pr1234",_)>> true
		lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,_) >> BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> [skuRepositoryItemMock]
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> null
		RepositoryItem productAttributeRelns = Mock()
		RepositoryItem productAttributeRelns1 = Mock()
		RepositoryItem productAttributeRelns2 = Mock()
		RepositoryItem  prodAttributeRepositoryItem = Mock()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [].toSet()
		when:
		ProductVO pVO =  testObjCatalogTools.getComparisonProductDetails("pr1234", "BedBathUS", cpeo)
		then:
		pVO.isLtlProduct()
		pVO.isCollection() == false
		pVO.getProductImages().getMediumImage().equals(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME)
		pVO.getProductImages().getThumbnailImage().equals(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME)
		pVO.getName().equals(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)
		pVO.getShortDescription().equals(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)
		pVO.getSkuLowPrice()==null
		pVO.getSkuHighPrice().equals(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME)
		cpeo.isMultiSku() == false
	}
	def"Product Comparison page. This catalog method will update the Product VO with the attributes required on the product comparison page for products.normal product"(){
		given:
		Date preview =new Date()
		CompareProductEntryVO cpeo = new CompareProductEntryVO()
		catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.isProductActive(repositoryItemMock)>> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> ""
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) >>null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) >>null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)>> "0.0001%L%l"
		testObjCatalogTools.getBazaarVoiceDetails("pr1234", "BedBathUS")>>new BazaarVoiceProductVO()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> [skuRepositoryItemMock]
		skuRepositoryItemMock.getRepositoryId() >> "sku1234"
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepositoryItemMock
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> true
		testObjCatalogTools.isSkuOnSale("pr1234",_)>> true
		lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,_) >> BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME
		RepositoryItem productAttributeRelns = Mock()
		RepositoryItem productAttributeRelns1 = Mock()
		RepositoryItem productAttributeRelns2 = Mock()
		RepositoryItem  prodAttributeRepositoryItem = Mock()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >> [productAttributeRelns,productAttributeRelns1,productAttributeRelns2].toSet()
		productAttributeRelns.getPropertyValue("bedbathUSFlag") >> false
		productAttributeRelns1.getPropertyValue("bedbathUSFlag") >> true
		productAttributeRelns2.getPropertyValue("bedbathUSFlag") >> true
		productAttributeRelns1.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> null
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_SKU_RELATION_PROPERTY_NAME) >> prodAttributeRepositoryItem
		SddZipcodeVO zipcodeVO= new SddZipcodeVO()
		zipcodeVO.setPromoAttId("promoAttId")
		zipcodeVO.setSddEligibility(true)
		sessionBeanMock.getCurrentZipcodeVO() >> zipcodeVO
		sessionBeanMock.isInternationalShippingContext() >> true
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.START_DATE_PROD_RELATION_PROPERTY_NAME) >> preview
		productAttributeRelns2.getPropertyValue(BBBCatalogConstants.END_DATE_PROD_RELATION_PROPERTY_NAME)   >> preview
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_PROD_RELATION_PROPERTY_NAME) >> preview
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_PROD_RELATION_PROPERTY_NAME)   >> preview
		prodAttributeRepositoryItem.getRepositoryId() >> "prdRel1234"
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_FLAG) >> 'Y'
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.SDD_KEY,BBBCmsConstants.SDD_ATTRIBUTE_LIST) >> []
		BBBConfigRepoUtils.setBbbCatalogTools(catalogTools)
		catalogTools.getAllValuesForKey(BBBCoreConstants.SAME_DAY_DELIVERY_KEYS, BBBCoreConstants.SAME_DAY_DELIVERY_FLAG) >> ["true"]
		testObjCatalogTools.setPreviewEnabled(true)
		PreviewAttributes previewAttributes = Mock()
		previewAttributes.getPreviewDate() >> preview
		requestMock.resolveName("/com/bbb/commerce/catalog/PreviewAttributes") >> previewAttributes
		testObjCatalogTools.setBBBSiteToAttributeSiteMap(siteToAttributeSiteMap)
		globalRepositoryToolsMock.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.PREVIEW_ENABLED) >> ["true"]
		testObjCatalogTools.setZoomKeys("prdRel1234")
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, "prdRel1234") >> []
		repositoryItemMock.getPropertyValue("giftCertProduct") >> true
		AttributeVO aVO =Mock()
		AttributeVO aVO1 =Mock()
		AttributeVO aVO2 =Mock()
		prodAttributeRepositoryItem.getPropertyValue("displayDescrip") >> "displayDescrip"
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME) >> BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME
		prodAttributeRepositoryItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME) >> BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME
		siteRepositoryToolsMock.getSiteLevelAttributes(_, _)>>["1":aVO,"prdRel1235":aVO1,"3":aVO2]
		catalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.LTL_DELIVERY_ATTRIBUTES_LIST) >>["prdRel1234"]
		when:
		ProductVO pVO =  testObjCatalogTools.getComparisonProductDetails("pr1234", "BedBathUS", cpeo)
		then:
		cpeo.isLtlProduct()
		cpeo.isMultiSku() == false
		pVO.getChildSKUs().get(0).equals("sku1234")
		pVO.isLtlProduct()
		pVO.isCollection() == false
		pVO.getLongDescription().equals("0.0001%L%l")
		cpeo.getAttributesList().size()==3
	}

	def"Product Comparison page. This catalog method will update the Product VO with the attributes required on the product comparison page for products.normal product throw bussiness exception"(){
		given:
		Date preview =new Date()
		CompareProductEntryVO cpeo = new CompareProductEntryVO()
		catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		testObjCatalogTools.isProductActive(repositoryItemMock)>> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> ""
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) >>null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_IMAGE_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME) >>null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)>> "0.0001%L%l"
		testObjCatalogTools.getBazaarVoiceDetails("pr1234", "BedBathUS")>>new BazaarVoiceProductVO()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_LOW_PRICE_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_HIGH_PRICE_PRODUCT_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> [skuRepositoryItemMock]
		skuRepositoryItemMock.getRepositoryId() >> "sku1234"
		catalogRepositoryMock.getItem("sku1234", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) >> true
		testObjCatalogTools.isSkuOnSale("pr1234",_)>> true
		lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.PRICE_IS_TBD, null, null,_) >> BBBCatalogConstants.PRICE_RANGE_PRODUCT_PROPERTY_NAME
		when:
		ProductVO pVO =  testObjCatalogTools.getComparisonProductDetails("pr1234", "BedBathUS", cpeo)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"Product Comparison page. This catalog method will update the Product VO with the attributes required on the product comparison page for products.repository item is null"(){
		given:
		catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null
		when:
		ProductVO pVO =  testObjCatalogTools.getComparisonProductDetails("pr1234", "BedBathUS", null)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1004:1004")
	}
	def"Product Comparison page. This catalog method will update the Product VO with the attributes required on the product comparison page for products.throw repository exception"(){
		given:
		catalogRepositoryMock.getItem("pr1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		ProductVO pVO =  testObjCatalogTools.getComparisonProductDetails("pr1234", "BedBathUS", null)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"Product Comparison page. This catalog method will update the Product VO with the attributes required on the product comparison page for products.product id is null"(){

		when:
		ProductVO pVO =  testObjCatalogTools.getComparisonProductDetails("", "BedBathUS", null)
		then:
		pVO==null
	}
	def"Product Comparison page. This catalog method will update the Product VO with the attributes required on the product comparison page for products.site id is null"(){

		when:
		ProductVO pVO =  testObjCatalogTools.getComparisonProductDetails("pr1234", "", null)
		then:
		pVO==null
	}
	def"The method gets category details in the form of CategoryVO"(){
		given:
		catalogRepositoryMock.getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryViewMock
		repositoryViewMock.getQueryBuilder()>> qBuilder
		qBuilder.createPropertyQueryExpression("id")>>qExp
		qBuilder.createConstantQueryExpression(_)>> qExp
		qBuilder.createComparisonQuery(qExp, qExp, QueryBuilder.EQUALS) >>query
		repositoryViewMock.executeQuery(query)>>[repositoryItemMock]
		testObjCatalogTools.isCategoryActive(repositoryItemMock)>> true
		when:
		RepositoryItem repitem = testObjCatalogTools.getCategoryRepDetail("BedBathUS", "catid")
		then:
		repitem ==repositoryItemMock
	}
	def"The method gets category details in the form of CategoryVO throw repository exception"(){
		given:
		catalogRepositoryMock.getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryViewMock
		repositoryViewMock.getQueryBuilder()>> qBuilder
		qBuilder.createPropertyQueryExpression("id")>>qExp
		qBuilder.createConstantQueryExpression(_)>> qExp
		qBuilder.createComparisonQuery(qExp, qExp, QueryBuilder.EQUALS) >>query
		repositoryViewMock.executeQuery(query)>>{throw new RepositoryException()}
		when:
		RepositoryItem repitem = testObjCatalogTools.getCategoryRepDetail("BedBathUS", "catid")
		then:
		repitem == null
	}
	def"The method gets category details in the form of CategoryVO ,category is disable"(){
		given:
		catalogRepositoryMock.getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryViewMock
		repositoryViewMock.getQueryBuilder()>> qBuilder
		qBuilder.createPropertyQueryExpression("id")>>qExp
		qBuilder.createConstantQueryExpression(_)>> qExp
		qBuilder.createComparisonQuery(qExp, qExp, QueryBuilder.EQUALS) >>query
		repositoryViewMock.executeQuery(query)>>[repositoryItemMock]
		testObjCatalogTools.isCategoryActive(repositoryItemMock)>> false
		when:
		RepositoryItem repitem = testObjCatalogTools.getCategoryRepDetail("BedBathUS", "catid")
		then:
		repitem ==repositoryItemMock
	}
	def"The method gets category details in the form of CategoryVO,repository items is empty"(){
		given:
		catalogRepositoryMock.getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryViewMock
		repositoryViewMock.getQueryBuilder()>> qBuilder
		qBuilder.createPropertyQueryExpression("id")>>qExp
		qBuilder.createConstantQueryExpression(_)>> qExp
		qBuilder.createComparisonQuery(qExp, qExp, QueryBuilder.EQUALS) >>query
		repositoryViewMock.executeQuery(query)>>[]
		when:
		RepositoryItem repitem = testObjCatalogTools.getCategoryRepDetail("BedBathUS", "catid")
		then:
		repitem ==null
	}
	def"The method gets category details in the form of CategoryVO,repository items is null"(){
		given:
		catalogRepositoryMock.getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryViewMock
		repositoryViewMock.getQueryBuilder()>> qBuilder
		qBuilder.createPropertyQueryExpression("id")>>qExp
		qBuilder.createConstantQueryExpression(_)>> qExp
		qBuilder.createComparisonQuery(qExp, qExp, QueryBuilder.EQUALS) >>query
		repositoryViewMock.executeQuery(query)>>null
		when:
		RepositoryItem repitem = testObjCatalogTools.getCategoryRepDetail("BedBathUS", "catid")
		then:
		repitem ==null
	}
	def"This method returns the Category Id to be passed in the Pricing message Vo to be shown for Shop similar items"(){
		given:
		testObjCatalogTools.getPrimaryCategory("prd1234")>> ""
		siteRepositoryToolsMock.getSimilarProductsIgnoreList("BedBathUs") >> [].toSet()
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUs") >> ["1":categoryVOMock]
		categoryVOMock.getCategoryId() >> "cat1234"
		when:
		String value = testObjCatalogTools.getShopSimilarItemCategory("prd1234", "BedBathUs")
		then:
		value.equals("cat1234")
	}
	def"This method returns the Category Id to be passed in the Pricing message Vo to be shown for Shop similar items,primary category is not empty"(){
		given:
		testObjCatalogTools.getPrimaryCategory("prd1234")>> "cat1234"
		siteRepositoryToolsMock.getSimilarProductsIgnoreList("BedBathUs") >> [].toSet()
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUs") >> ["1":categoryVOMock]
		categoryVOMock.getCategoryId() >> "cat1234"
		when:
		String value = testObjCatalogTools.getShopSimilarItemCategory("prd1234", "BedBathUs")
		then:
		value.equals("cat1234")
	}
	def"This method returns the Category Id to be passed in the Pricing message Vo to be shown for Shop similar items,AllParentCategoryForProduct  is  empty"(){
		given:
		testObjCatalogTools.getPrimaryCategory("prd1234")>> ""
		siteRepositoryToolsMock.getSimilarProductsIgnoreList("BedBathUs") >> null
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUs") >> [:]
		categoryVOMock.getCategoryId() >> "cat1234"
		when:
		String value = testObjCatalogTools.getShopSimilarItemCategory("prd1234", "BedBathUs")
		then:
		value.equals(BBBCatalogConstants.CATEGORY_ID_BLANK)
	}
	def"This method returns the Category Id to be passed in the Pricing message Vo to be shown for Shop similar items,SimilarProductsIgnoreList  is   empty"(){
		given:
		testObjCatalogTools.getPrimaryCategory("prd1234")>> null
		siteRepositoryToolsMock.getSimilarProductsIgnoreList("BedBathUs") >> ["PRD1234","prd12345"].toSet()
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUs") >> [:]
		categoryVOMock.getCategoryId() >> "cat1234"
		when:
		String value = testObjCatalogTools.getShopSimilarItemCategory("prd1234", "BedBathUs")
		then:
		value.equals(BBBCatalogConstants.CATEGORY_ID_BLANK)
	}
	def"This method returns the Category Id to be passed in the Pricing message Vo to be shown for Shop similar items,SimilarProductsIgnoreList  is  NOT empty"(){
		given:
		testObjCatalogTools.getPrimaryCategory("prd1234")>> "cat1234"
		siteRepositoryToolsMock.getSimilarProductsIgnoreList("BedBathUs") >> ["cat1234","prd12345"].toSet()
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUs") >> ["1":categoryVOMock,"2":categoryVOMock]
		categoryVOMock.getCategoryId() >> "cat1234">>"cat12345"
		when:
		String value = testObjCatalogTools.getShopSimilarItemCategory("prd1234", "BedBathUs")
		then:
		value.equals("cat12345")
	}
	def"This method returns the Category Id to be passed in the Pricing message Vo to be shown for Shop similar items,primary not contain in similar product list"(){
		given:
		testObjCatalogTools.getPrimaryCategory("prd1234")>> "cat12345"
		siteRepositoryToolsMock.getSimilarProductsIgnoreList("BedBathUs") >> ["cat1234","prd12345"].toSet()
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUs") >> ["1":categoryVOMock,"2":categoryVOMock]
		categoryVOMock.getCategoryId() >> "cat1234">>"cat12345"
		when:
		String value = testObjCatalogTools.getShopSimilarItemCategory("prd1234", "BedBathUs")
		then:
		value.equals("cat12345")
	}
	def"This method returns the Category Id to be passed in the Pricing message Vo to be shown for Shop similar items,productid is null"(){
		when:
		String value = testObjCatalogTools.getShopSimilarItemCategory(null, "BedBathUs")
		then:
		value.equals(BBBCatalogConstants.CATEGORY_ID_BLANK)
	}
	def"This method returns whether the Global Chat On/Off Flag is set for the site Id"(){
		given:
		SiteChatAttributesVO siteVO = Mock()
		siteRepositoryToolsMock.getSiteChatAttributes("BedBathUS") >> siteVO
		siteVO.isOnOffFlag() >> true
		when:
		boolean value = testObjCatalogTools.checkGlobalChat("BedBathUS")
		then:
		value
	}
	def"This method returns whether the Global Chat On/Off Flag is set for the site Id is null"(){
		when:
		boolean value = testObjCatalogTools.checkGlobalChat(null)
		then:
		value==false
	}
	def"This is used on PDP to evaluate Enable chat and Display Ask and Answer Logic"(){
		given:
		SiteChatAttributesVO siteVO = Mock()
		1*siteRepositoryToolsMock.getSiteChatAttributes("BedBathUS") >> siteVO
		siteVO.isOnOffFlag() >> true
		siteVO.isChatFlagPDP() >> true
		siteVO.isChatOverrideFlagPDP() >> true
		siteVO.isDaasFlagPDP() >> true
		siteVO.isDaasOverrideFlagPDP() >> true
		categoryVOMock.getDefaultViewValue()>>"viewValue"
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS") >>["1":categoryVOMock]
		categoryVOMock.getCategoryId() >> "cat1234"
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat1234",false) >> categoryVOMock
		when:
		PDPAttributesVO pdpAttributesVo= testObjCatalogTools.PDPAttributes("prd1234", "cat1234", "", "BedBathUS")
		then:
		pdpAttributesVo!= null
		2*siteRepositoryToolsMock.getBccManagedCategory(categoryVOMock)
		pdpAttributesVo.getDefaultDisplayType().equals("viewValue")
		pdpAttributesVo.getCategoryId().equals("cat1234")
		pdpAttributesVo.isChatEnabled()
		pdpAttributesVo.isAskAndAnswerEnabled()
		pdpAttributesVo.isSiteChatOnOffFlag()
		pdpAttributesVo.isPdpChatOnOffFlag()
		pdpAttributesVo.isPdpChatOverrideFlag()
		pdpAttributesVo.isPdpDaasOnOffFlag()
		pdpAttributesVo.isPdpDaasOverrideFlag()
	}
	def"This is used on PDP to evaluate Enable chat and Display Ask and Answer Logic,pdpChatOverrideFlag and pdpDaasOverrideFlag is false"(){
		given:
		SiteChatAttributesVO siteVO = Mock()
		1*siteRepositoryToolsMock.getSiteChatAttributes("BedBathUS") >> siteVO
		siteVO.isOnOffFlag() >> true
		siteVO.isChatFlagPDP() >> true
		siteVO.isChatOverrideFlagPDP() >> false
		siteVO.isDaasFlagPDP() >> true
		siteVO.isDaasOverrideFlagPDP() >> false
		categoryVOMock.getDefaultViewValue()>>"viewValue"
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS") >>["1":categoryVOMock]
		categoryVOMock.getCategoryId() >> "cat1234"
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat1234",false) >> categoryVOMock
		categoryVOMock.getDisplayAskAndAnswer()>>"unHide"
		when:
		PDPAttributesVO pdpAttributesVo= testObjCatalogTools.PDPAttributes("prd1234", "cat1234", "pPoc", "BedBathUS")
		then:
		pdpAttributesVo!= null
		3*siteRepositoryToolsMock.getBccManagedCategory(categoryVOMock)
		pdpAttributesVo.isChatEnabled()==false
		pdpAttributesVo.isAskAndAnswerEnabled()
		pdpAttributesVo.isSiteChatOnOffFlag()
		pdpAttributesVo.isPdpChatOnOffFlag()
		pdpAttributesVo.isPdpChatOverrideFlag()==false
		pdpAttributesVo.isPdpDaasOnOffFlag()
		pdpAttributesVo.isPdpDaasOverrideFlag()==false
	}
	def"This is used on PDP to evaluate Enable chat and Display Ask and Answer Logic,pdpChatOverrideFlag and pdpDaasOverrideFlag is false,ischat enabled true"(){
		given:
		SiteChatAttributesVO siteVO = Mock()
		1*siteRepositoryToolsMock.getSiteChatAttributes("BedBathUS") >> siteVO
		siteVO.isOnOffFlag() >> true
		siteVO.isChatFlagPDP() >> true
		siteVO.isChatOverrideFlagPDP() >> false
		siteVO.isDaasFlagPDP() >> true
		siteVO.isDaasOverrideFlagPDP() >> false
		categoryVOMock.getDefaultViewValue()>>"viewValue"
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS") >>["1":categoryVOMock]
		categoryVOMock.getCategoryId() >> "cat1234"
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat1234",false) >> categoryVOMock
		categoryVOMock.getDisplayAskAndAnswer()>>""
		categoryVOMock.getIsChatEnabled() >> true
		when:
		PDPAttributesVO pdpAttributesVo= testObjCatalogTools.PDPAttributes("prd1234", "cat1234", "pPoc", "BedBathUS")
		then:
		pdpAttributesVo!= null
		3*siteRepositoryToolsMock.getBccManagedCategory(categoryVOMock)
		pdpAttributesVo.isChatEnabled()==true
		pdpAttributesVo.isAskAndAnswerEnabled() == false
		pdpAttributesVo.isSiteChatOnOffFlag()
		pdpAttributesVo.isPdpChatOnOffFlag()
		pdpAttributesVo.isPdpChatOverrideFlag()==false
		pdpAttributesVo.isPdpDaasOnOffFlag()
		pdpAttributesVo.isPdpDaasOverrideFlag()==false
	}
	def"This is used on PDP to evaluate Enable chat and Display Ask and Answer Logic,pdpChatOverrideFlag and pdpDaasOverrideFlag is falseand poc is  null"(){
		given:
		SiteChatAttributesVO siteVO = Mock()
		1*siteRepositoryToolsMock.getSiteChatAttributes("BedBathUS") >> siteVO
		siteVO.isOnOffFlag() >> true
		siteVO.isChatFlagPDP() >> true
		siteVO.isChatOverrideFlagPDP() >> false
		siteVO.isDaasFlagPDP() >> true
		siteVO.isDaasOverrideFlagPDP() >> false
		categoryVOMock.getDefaultViewValue()>>"viewValue"
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS") >>["1":categoryVOMock]
		categoryVOMock.getCategoryId() >> "cat1234"
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat1234",false) >> categoryVOMock
		categoryVOMock.getDisplayAskAndAnswer()>>""
		categoryVOMock.getIsChatEnabled() >> true
		when:
		PDPAttributesVO pdpAttributesVo= testObjCatalogTools.PDPAttributes("prd1234", "cat1234", "", "BedBathUS")
		then:
		pdpAttributesVo!= null
		1*siteRepositoryToolsMock.getBccManagedCategory(categoryVOMock)
		pdpAttributesVo.isChatEnabled()==true
		pdpAttributesVo.isAskAndAnswerEnabled() == true
		pdpAttributesVo.isSiteChatOnOffFlag()
		pdpAttributesVo.isPdpChatOnOffFlag()
		pdpAttributesVo.isPdpChatOverrideFlag()==false
		pdpAttributesVo.isPdpDaasOnOffFlag()
		pdpAttributesVo.isPdpDaasOverrideFlag()==false
	}
	def"This is used on PDP to evaluate Enable chat and Display Ask and Answer Logic,pdpChatOverrideFlag and pdpDaasOverrideFlag is chatenabled  poc is  null"(){
		given:
		SiteChatAttributesVO siteVO = Mock()
		1*siteRepositoryToolsMock.getSiteChatAttributes("BedBathUS") >> siteVO
		siteVO.isOnOffFlag() >> true
		siteVO.isChatFlagPDP() >> true
		siteVO.isChatOverrideFlagPDP() >> false
		siteVO.isDaasFlagPDP() >> true
		siteVO.isDaasOverrideFlagPDP() >> false
		categoryVOMock.getDefaultViewValue()>>null
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS") >>["1":categoryVOMock]>>[:]
		categoryVOMock.getCategoryId() >> "cat1234"
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat1234",false) >> categoryVOMock
		categoryVOMock.getDisplayAskAndAnswer()>>"Hide"
		categoryVOMock.getIsChatEnabled() >> null
		when:
		PDPAttributesVO pdpAttributesVo= testObjCatalogTools.PDPAttributes("prd1234", "cat1234", "", "BedBathUS")
		then:
		pdpAttributesVo!= null
		2*siteRepositoryToolsMock.getBccManagedCategory(categoryVOMock)
		pdpAttributesVo.isChatEnabled()==false
		pdpAttributesVo.isAskAndAnswerEnabled() == false
		pdpAttributesVo.isSiteChatOnOffFlag()
		pdpAttributesVo.isPdpChatOnOffFlag()
		pdpAttributesVo.isPdpChatOverrideFlag()==false
		pdpAttributesVo.isPdpDaasOnOffFlag()
		pdpAttributesVo.isPdpDaasOverrideFlag()==false
	}
	def"This is used on PDP to evaluate Enable chat and Display Ask and Answer Logic,pdpChatOverrideFlag and pdpDaasOverrideFlag is chatenabled is false and   poc is  null"(){
		given:
		SiteChatAttributesVO siteVO = Mock()
		1*siteRepositoryToolsMock.getSiteChatAttributes("BedBathUS") >> siteVO
		siteVO.isOnOffFlag() >> true
		siteVO.isChatFlagPDP() >> true
		siteVO.isChatOverrideFlagPDP() >> false
		siteVO.isDaasFlagPDP() >> false
		siteVO.isDaasOverrideFlagPDP() >> false
		categoryVOMock.getDefaultViewValue()>>null
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS") >>["1":categoryVOMock]>>[:]
		categoryVOMock.getCategoryId() >> "cat1234"
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat1234",false) >> null
		categoryVOMock.getDisplayAskAndAnswer()>>"Hide"
		categoryVOMock.getIsChatEnabled() >> null
		when:
		PDPAttributesVO pdpAttributesVo= testObjCatalogTools.PDPAttributes("prd1234", "cat1234", "", "BedBathUS")
		then:
		pdpAttributesVo!= null
		1*siteRepositoryToolsMock.getBccManagedCategory(categoryVOMock)
		pdpAttributesVo.isChatEnabled()==false
		pdpAttributesVo.isAskAndAnswerEnabled() == false
		pdpAttributesVo.isSiteChatOnOffFlag()
		pdpAttributesVo.isPdpChatOnOffFlag()
		pdpAttributesVo.isPdpChatOverrideFlag()==false
		pdpAttributesVo.isPdpDaasOnOffFlag()== false
		pdpAttributesVo.isPdpDaasOverrideFlag()==false
	}
	def"This is used on PDP to evaluate Enable chat and Display Ask and Answer Logic,pdpChatOverrideFlag and pdpDaasOverrideFlag is isChatFlagPDP is false and   poc is  null"(){
		given:
		SiteChatAttributesVO siteVO = Mock()
		1*siteRepositoryToolsMock.getSiteChatAttributes("BedBathUS") >> siteVO
		siteVO.isOnOffFlag() >> true
		siteVO.isChatFlagPDP() >> false
		siteVO.isChatOverrideFlagPDP() >> false
		siteVO.isDaasFlagPDP() >> false
		siteVO.isDaasOverrideFlagPDP() >> false
		categoryVOMock.getDefaultViewValue()>>null
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS") >>["1":categoryVOMock]>>[:]
		categoryVOMock.getCategoryId() >> "cat1234"
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat1234",false) >> null
		categoryVOMock.getDisplayAskAndAnswer()>>"Hide"
		categoryVOMock.getIsChatEnabled() >> null
		when:
		PDPAttributesVO pdpAttributesVo= testObjCatalogTools.PDPAttributes("prd1234", "cat1234", "", "BedBathUS")
		then:
		pdpAttributesVo!= null
		1*siteRepositoryToolsMock.getBccManagedCategory(categoryVOMock)
		pdpAttributesVo.isChatEnabled()==false
		pdpAttributesVo.isAskAndAnswerEnabled() == false
		pdpAttributesVo.isSiteChatOnOffFlag()
		pdpAttributesVo.isPdpChatOnOffFlag()==false
		pdpAttributesVo.isPdpChatOverrideFlag()==false
		pdpAttributesVo.isPdpDaasOnOffFlag()== false
		pdpAttributesVo.isPdpDaasOverrideFlag()==false
	}
	def"This is used on PDP to evaluate Enable chat and Display Ask and Answer Logic,pdpChatOverrideFlag and pdpDaasOverrideFlag is isOnOffFlag is false and   poc is  null"(){
		given:
		SiteChatAttributesVO siteVO = Mock()
		1*siteRepositoryToolsMock.getSiteChatAttributes("BedBathUS") >> siteVO
		siteVO.isOnOffFlag() >> false
		siteVO.isChatFlagPDP() >> false
		siteVO.isChatOverrideFlagPDP() >> false
		siteVO.isDaasFlagPDP() >> false
		siteVO.isDaasOverrideFlagPDP() >> false
		categoryVOMock.getDefaultViewValue()>>null
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS") >>{throw new BBBBusinessException("Mock of business exception")}
		categoryVOMock.getCategoryId() >> "cat1234"
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat1234",false) >> categoryVOMock
		categoryVOMock.getDisplayAskAndAnswer()>>"Hide"
		categoryVOMock.getIsChatEnabled() >> null
		when:
		PDPAttributesVO pdpAttributesVo= testObjCatalogTools.PDPAttributes("prd1234", "cat1234", "", "BedBathUS")
		then:
		pdpAttributesVo!= null
		1*siteRepositoryToolsMock.getBccManagedCategory(categoryVOMock)
		pdpAttributesVo.isChatEnabled()==false
		pdpAttributesVo.isAskAndAnswerEnabled() == false
		pdpAttributesVo.isSiteChatOnOffFlag()==false
		pdpAttributesVo.isPdpChatOnOffFlag()==false
		pdpAttributesVo.isPdpChatOverrideFlag()==false
		pdpAttributesVo.isPdpDaasOnOffFlag()== false
		pdpAttributesVo.isPdpDaasOverrideFlag()==false
	}
	def"This is used on PDP to evaluate Enable chat and Display Ask and Answer Logic,throw system exception"(){
		given:
		SiteChatAttributesVO siteVO = Mock()
		1*siteRepositoryToolsMock.getSiteChatAttributes("BedBathUS") >> siteVO
		siteVO.isOnOffFlag() >> false
		siteVO.isChatFlagPDP() >> false
		siteVO.isChatOverrideFlagPDP() >> false
		siteVO.isDaasFlagPDP() >> false
		siteVO.isDaasOverrideFlagPDP() >> false
		categoryVOMock.getDefaultViewValue()>>null
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS") >>{throw new BBBSystemException("Mock of business exception")}
		categoryVOMock.getCategoryId() >> "cat1234"
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat1234",false) >> categoryVOMock
		categoryVOMock.getDisplayAskAndAnswer()>>"Hide"
		categoryVOMock.getIsChatEnabled() >> null
		when:
		PDPAttributesVO pdpAttributesVo= testObjCatalogTools.PDPAttributes("prd1234", "cat1234", "", "BedBathUS")
		then:
		pdpAttributesVo!= null
		1*siteRepositoryToolsMock.getBccManagedCategory(categoryVOMock)
		pdpAttributesVo.isChatEnabled()==false
		pdpAttributesVo.isAskAndAnswerEnabled() == false
		pdpAttributesVo.isSiteChatOnOffFlag()==false
		pdpAttributesVo.isPdpChatOnOffFlag()==false
		pdpAttributesVo.isPdpChatOverrideFlag()==false
		pdpAttributesVo.isPdpDaasOnOffFlag()== false
		pdpAttributesVo.isPdpDaasOverrideFlag()==false
	}
	def"This is used on PDP to evaluate Enable chat and Display Ask and Answer Logic,product id is null"(){
		when:
		PDPAttributesVO pdpAttributesVo= testObjCatalogTools.PDPAttributes("", "cat1234", "", "BedBathUS")
		then:
		pdpAttributesVo!= null
	}
	def"This is used on PDP to evaluate Enable chat and Display Ask and Answer Logic,site id is null"(){
		when:
		PDPAttributesVO pdpAttributesVo= testObjCatalogTools.PDPAttributes("prd1234", "cat1234", "", "")
		then:
		pdpAttributesVo!= null
	}
	def"This is used on PDP to evaluate Enable chat and Display Ask and Answer Logic,ListEnabledInCategory is empty"(){
		given:
		SiteChatAttributesVO siteVO = Mock()
		1*siteRepositoryToolsMock.getSiteChatAttributes("BedBathUS") >> siteVO
		siteVO.isOnOffFlag() >> true
		siteVO.isChatFlagPDP() >> true
		siteVO.isChatOverrideFlagPDP() >> false
		siteVO.isDaasFlagPDP() >> true
		siteVO.isDaasOverrideFlagPDP() >> false
		categoryVOMock.getDefaultViewValue()>>"viewValue"
		testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS") >>[:]
		categoryVOMock.getCategoryId() >> "cat1234"
		testObjCatalogTools.getCategoryDetail("BedBathUS", "cat1234",false) >> categoryVOMock
		categoryVOMock.getDisplayAskAndAnswer()>>""
		categoryVOMock.getIsChatEnabled() >> true
		when:
		PDPAttributesVO pdpAttributesVo= testObjCatalogTools.PDPAttributes("prd1234", "cat1234", "pPoc", "BedBathUS")
		then:
		pdpAttributesVo!= null
		pdpAttributesVo.isChatEnabled()==false
		pdpAttributesVo.isAskAndAnswerEnabled() == false
		pdpAttributesVo.isSiteChatOnOffFlag()
		pdpAttributesVo.isPdpChatOnOffFlag()
		pdpAttributesVo.isPdpChatOverrideFlag()==false
		pdpAttributesVo.isPdpDaasOnOffFlag()
		pdpAttributesVo.isPdpDaasOverrideFlag()==false
	}
	def"This method returns the categoryId based on criteria is empty"(){
		when:
		String value = testObjCatalogTools.chatEnabledInCategory([], "")
		then:
		value.equals("")
	}
	def"This method returns the categoryId based on criteria is first"(){
		when:
		String value = testObjCatalogTools.chatEnabledInCategory([], "first")
		then:
		value.equals("")
	}
	def"This method returns the categoryId based on criteria is second"(){
		when:
		String value = testObjCatalogTools.chatEnabledInCategory([categoryVOMock], "second")
		then:
		value.equals("")
	}
	def"This method returns the categoryId based on criteria is CRITERIA_ANY"(){
		given:
		categoryVOMock.getIsChatEnabled()>>false
		when:
		String value = testObjCatalogTools.chatEnabledInCategory([categoryVOMock], BBBCatalogConstants.CRITERIA_ANY)
		then:
		value.equals("")
	}
	
	def "getCategoryRecommendation. This TC is a Happy Flow of getCategoryRecommendation method which tests the recommendation data populated."() {
		
		given:
		RepositoryItemImpl recommendationItemMock =Mock()
		RepositoryItem item =Mock()
		testObjCatalogTools.setBbbManagedCatalogRepository(managedCatalogRepositoryMock)
		String categoryId="12227"
		String productId="106192"
		managedCatalogRepositoryMock.getItem(categoryId,"categoryDetails") >> recommendationItemMock
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "numOfCatRecommToshow") >> ["4"]
		recommendationItemMock.getPropertyValue("catRecommId") >> [item]
		item.getRepositoryId()>>"DC1100008"
		item.getPropertyValue("catRecommId") >>"DC1100008"
		item.getPropertyValue("recommImageUrl") >>"test"
		item.getPropertyValue("recommLink") >>"test"
		item.getPropertyValue("recommText") >>"test"
		
		when:
		List<RecommendedCategoryVO> list = testObjCatalogTools.getCategoryRecommendation(categoryId,productId)
		 
		then:
		RecommendedCategoryVO catVo =list.get(0)
		catVo.getCatRecommId().equals("DC1100008")
		 
	}
	
	def "getPrimaryCategoryRecommendation. This TC is a Flow of getCategoryRecommendation method for primaryCategoryOfProd  which tests the recommendation data populated."() {
		
		given:
		RepositoryItemImpl recommendationItemMock =Mock()
		RepositoryItem item =Mock()
		testObjCatalogTools.setBbbManagedCatalogRepository(managedCatalogRepositoryMock)
		String categoryId=null
		String productId="106192"
		managedCatalogRepositoryMock.getItem("12227","categoryDetails") >> recommendationItemMock
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "numOfCatRecommToshow") >> ["4"]
		testObjCatalogTools.getPrimaryCategory("106192") >> "12227"
		recommendationItemMock.getPropertyValue("catRecommId") >> [item]
		item.getRepositoryId()>>"DC1100008"
		item.getPropertyValue("catRecommId") >>"DC1100008"
		item.getPropertyValue("recommImageUrl") >>"test"
		item.getPropertyValue("recommLink") >>"test"
		item.getPropertyValue("recommText") >>"test"
		
		when:
		List<RecommendedCategoryVO> list = testObjCatalogTools.getCategoryRecommendation(categoryId,productId)
		 
		then:
		RecommendedCategoryVO catVo =list.get(0)
		catVo.getCatRecommId().equals("DC1100008")
		 
	}
	def "getParentCategoryRecommendation. This TC is a Happy Flow of getCategoryRecommendation method for parentCategoryOfProd  which tests the recommendation data populated."() {
		
		given:
		RepositoryItemImpl recommendationItemMock =Mock()
		RepositoryItem item =Mock()
		testObjCatalogTools.setBbbManagedCatalogRepository(managedCatalogRepositoryMock)
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		String categoryId=null
		String productId="106192"
		managedCatalogRepositoryMock.getItem("12227","categoryDetails") >> recommendationItemMock
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "numOfCatRecommToshow") >> ["4"]
		testObjCatalogTools.getPrimaryCategory("106192") >> null
		testObjCatalogTools.getParentCategoryIdForProduct("106192","BedBathUS") >> "12227"
		recommendationItemMock.getPropertyValue("catRecommId") >> [item]
		item.getRepositoryId()>>"DC1100008"
		item.getPropertyValue("catRecommId") >>"DC1100008"
		item.getPropertyValue("recommImageUrl") >>"test"
		item.getPropertyValue("recommLink") >>"test"
		item.getPropertyValue("recommText") >>"test"
		
		when:
		List<RecommendedCategoryVO> list = testObjCatalogTools.getCategoryRecommendation(categoryId,productId)
		 
		then:
		RecommendedCategoryVO catVo =list.get(0)
		catVo.getCatRecommId().equals("DC1100008")
		 
	}
	
	
	def "getProductDetailsWithSiblings. This TC is a Happy Flow of getProductDetailsWithSiblings method for lead product which tests the Siblings data populated."() {
		
		given:
		String productId="106192"
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		RepositoryItem productRepositoryItem =Mock()
		ProductVO productVO= Mock()
		RepositoryItem item =Mock()
		RepositoryItem childProdItem =Mock()
		RepositoryItem defaultSKU =Mock()
		BazaarVoiceProductVO bvReviews= Mock()
		testObjCatalogTools.getCatalogRepository().getItem(productId, "product") >> productRepositoryItem
		testObjCatalogTools.isProductActive(productRepositoryItem) >> true
		productRepositoryItem.getPropertyValue("leadPrd") >> true
		List<ProductVO> childOrSiblingProductVOList = Mock()
		productRepositoryItem.getPropertyValue("productChildProducts") >> [item]
		item.getPropertyValue("productId") >> childProdItem
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "numOfProdRecommToShow") >> ["12"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["false"]
		testObjCatalogTools.isProductActive(childProdItem) >> true
		childProdItem.getPropertyValue("intlRestricted") >> "false"
		childProdItem.getPropertyValue("ltlFlag") >> false
		testObjCatalogTools.updatePriceDescription(item) >> productVO
		childProdItem.getPropertyValue("childSKUs") >> [defaultSKU]
		childProdItem.getPropertyValue("displayName") >> "testProd"
		childProdItem.getPropertyValue("smallImage") >> "testImage"
		childProdItem.getPropertyValue("mediumImage") >> "mediumImage"
		childProdItem.getPropertyValue("seoUrl") >> "seoUrl"
		childProdItem.getPropertyValue("collection") >> false
		testObjCatalogTools.getBazaarVoiceDetails(childProdItem,"1234","BedBathUS") >> bvReviews
		testObjCatalogTools.updatePriceDescription(childProdItem) >> productVO
		defaultSKU.getPropertyValue("ltlFlag") >> false
		defaultSKU.getRepositoryId >> "1234"
		testObjCatalogTools.isSkuActive(defaultSKU) >> true
		productManagerMock.isPersonalizedSku() >> true

		when:
		List<ProductVO> list = testObjCatalogTools.getProductDetailsWithSiblings(productId)
		 
		then:
		ProductVO prodVO =list.get(0)
		prodVO!=null
		 
	}
	def "getProductDetailsNotActiveProduct. This TC is a Happy Flow of getProductDetailsWithSiblings method for not active product which tests the Siblings data populated."() {
		
		given:
		String productId="106192"
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		RepositoryItem productRepositoryItem =Mock()
	
		testObjCatalogTools.getCatalogRepository().getItem(productId, "product") >> productRepositoryItem
		testObjCatalogTools.isProductActive(productRepositoryItem) >> false
		when:
		List<ProductVO> list = testObjCatalogTools.getProductDetailsWithSiblings(productId)
		 
		then:
		 BBBBusinessException exception = thrown()
		 
	}
	
	def "getProductDetailsForSiblings. This TC is a Happy Flow of getProductDetailsWithSiblings method for sibling products which tests the Siblings data populated."() {
		
		given:
		String productId="106192"
		testObjCatalogTools.getCurrentSiteId() >> "BedBathUS"
		RepositoryItem productRepositoryItem =Mock()
		ProductVO productVO= Mock()
		RepositoryItem item =Mock()
		RepositoryItem r1 =Mock()
		RepositoryItem r2 =Mock()
		RepositoryItem childProdItem =Mock()
		RepositoryItem defaultSKU =Mock()
		BazaarVoiceProductVO bvReviews= Mock()
		RepositoryView productRelView= Mock()
		RepositoryView productView= Mock()
		/*Object[] productRelParams=["test"]*/
		testObjCatalogTools.getCatalogRepository().getItem(productId, "product") >> productRepositoryItem
		testObjCatalogTools.isProductActive(productRepositoryItem) >> true
		productRepositoryItem.getPropertyValue("leadPrd") >> false
		catalogRepositoryMock.getView("bbbPrdReln") >> productRelView
		testObjCatalogTools.setProductRelItemQuery("productId=?0")
		catalogRepositoryMock.getView("product") >> productRelView
		testObjCatalogTools.getParentProductQuery() >> "productId=?0"
		List<ProductVO> childOrSiblingProductVOList = Mock()
		productRepositoryItem.getPropertyValue("productChildProducts") >> [item]
		item.getPropertyValue("productId") >> childProdItem
		item.getRepositoryId() >> "1234"
		childProdItem.getRepositoryId() >> "1234"
		productRepositoryItem.getRepositoryId() >> "1234"
		testObjCatalogTools.getAllValuesForKey("ContentCatalogKeys", "numOfProdRecommToShow") >> ["12"]
		testObjCatalogTools.getAllValuesForKey("FlagDrivenFunctions", "ShipMsgDisplayFlag") >> ["false"]
		testObjCatalogTools.isProductActive(childProdItem) >> true
		childProdItem.getPropertyValue("intlRestricted") >> "false"
		childProdItem.getPropertyValue("ltlFlag") >> false
		testObjCatalogTools.updatePriceDescription(item) >> productVO
		childProdItem.getPropertyValue("childSKUs") >> [defaultSKU]
		childProdItem.getPropertyValue("displayName") >> "testProd"
		childProdItem.getPropertyValue("smallImage") >> "testImage"
		childProdItem.getPropertyValue("mediumImage") >> "mediumImage"
		childProdItem.getPropertyValue("seoUrl") >> "seoUrl"
		childProdItem.getPropertyValue("collection") >> false
		testObjCatalogTools.getBazaarVoiceDetails(childProdItem,"1234","BedBathUS") >> bvReviews
		testObjCatalogTools.updatePriceDescription(childProdItem) >> productVO
		defaultSKU.getPropertyValue("ltlFlag") >> false
		defaultSKU.getRepositoryId() >> "1234"
		testObjCatalogTools.isSkuActive(defaultSKU) >> true
		productManagerMock.isPersonalizedSku() >> true
		testObjCatalogTools.extractDBCall(productRelView, _, _)>> [r1]
		testObjCatalogTools.isProductActive(r1) >> true
		testObjCatalogTools.isProductActive(r2) >> true
		r1.getPropertyValue("productChildProducts") >> [childProdItem]
		childProdItem.getPropertyValue("productId") >> r2
		r2.getRepositoryId() >> "2345"

		when:
		List<ProductVO> list = testObjCatalogTools.getProductDetailsWithSiblings(productId)
		 
		then:
		ProductVO prodVO =list.get(0)
		prodVO!=null
		 
	}
	def"Map of All the Categories associated with the Product on the corresponding Site"(){
		given:
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME) >> [repositoryItemMock,skuRepositoryItemMock].toSet()
		repositoryItemMock.getPropertyValue("siteIds") >> [].toSet()
		skuRepositoryItemMock.getRepositoryId() >> "rep1234"
		skuRepositoryItemMock.getPropertyValue("siteIds") >> ["BedBathUS"].toSet()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)>>BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) >>true
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY)>>null
		when:
		Map cateogryMap = testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS")
		then:
		CategoryVO cVO=((CategoryVO)cateogryMap.get("0"))
		cVO.getCategoryId().equals("rep1234")
		cVO.getCategoryName().equals(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)
		cVO.getIsCollege()
		cVO.getPhantomCategory()== null
	}
	def"Map of All the Categories associated with the Product on the corresponding Site,PHANTOM_CATEGORY is not null"(){
		given:
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME) >> [repositoryItemMock,skuRepositoryItemMock].toSet()
		repositoryItemMock.getPropertyValue("siteIds") >> [].toSet()
		skuRepositoryItemMock.getRepositoryId() >> "rep1234"
		skuRepositoryItemMock.getPropertyValue("siteIds") >> ["BedBathUS"].toSet()
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)>>BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) >>null
		skuRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY)>>true
		when:
		Map cateogryMap = testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS")
		then:
		((CategoryVO)cateogryMap.get("0")).getCategoryId().equals("rep1234")
		((CategoryVO)cateogryMap.get("0")).getCategoryName().equals(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)
		((CategoryVO)cateogryMap.get("0")).getIsCollege()== null
		((CategoryVO)cateogryMap.get("0")).getPhantomCategory()
	}
	def"Map of All the Categories associated with the Product on the corresponding Site,"(){
		given:
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		Map cateogryMap = testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS")
		then:
		cateogryMap.isEmpty()
	}
	def"Map of All the Categories associated with the Product on the corresponding Site,parentCategories is empty"(){
		given:
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME) >> [].toSet()
		when:
		Map cateogryMap = testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS")
		then:
		cateogryMap.isEmpty()
	}
	def"Map of All the Categories associated with the Product on the corresponding Site,parentCategories is null"(){
		given:
		catalogRepositoryMock.getItem("prd1234",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME) >> null
		when:
		Map cateogryMap = testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS")
		then:
		cateogryMap.isEmpty()
	}
	def"Map of All the Categories associated with the Product on the corresponding Site is null"(){
		when:
		Map cateogryMap = testObjCatalogTools.getAllParentCategoryForProduct("", "BedBathUS")
		then:
		cateogryMap.isEmpty()
	}
	def"Map of All the Categories associated with the Product on the corresponding Site repository item is null"(){
		when:
		Map cateogryMap = testObjCatalogTools.getAllParentCategoryForProduct("prd1234", "BedBathUS")
		then:
		cateogryMap.isEmpty()
	}
	def"The values are country code and the country code in the VO"(){
		given:
		testObjCatalogTools.executeRQLQuery(_, _, BBBCatalogConstants.BILLING_ADDRESS_COUNTRY_ITEM_DESCRIPTOR, _) >> [repositoryItemMock,repositoryItemMock,repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COUNTRY_NAME_PROPERTY)>> null>>"OHIO"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COUNTRY_CODE_PROPERTY)>>null>>null>>"OH"
		when:
		List list = testObjCatalogTools.getCountriesVOList("US")
		then:
		list.size()==1
		((CountryVO)list.get(0)).getCountryCode().equals("OH")
		((CountryVO)list.get(0)).getCountryName().equals("OHIO")
	}
	def"The values are country code and the country code in the VO,country code is null"(){
		given:
		testObjCatalogTools.executeRQLQuery(_, _, BBBCatalogConstants.BILLING_ADDRESS_COUNTRY_ITEM_DESCRIPTOR, _) >> []
		when:
		List list = testObjCatalogTools.getCountriesVOList("")
		then:
		list.size()==0
	}
	def"The values are country code and the country code in the VO,rql query returns null"(){
		given:
		testObjCatalogTools.executeRQLQuery(_, _, BBBCatalogConstants.BILLING_ADDRESS_COUNTRY_ITEM_DESCRIPTOR, _) >> null
		when:
		List list = testObjCatalogTools.getCountriesVOList("")
		then:
		list.size()==0
	}
	def"The values are country code and the country code in the VO International Billing "(){
		given:
		testObjCatalogTools.executeRQLQuery(_, _, BBBCatalogConstants.INTL_BILLING_ADDRESS_COUNTRY_ITEM_DESCRIPTOR, _) >> [repositoryItemMock,repositoryItemMock,repositoryItemMock,repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COUNTRY_NAME_PROPERTY)>> null>>"OHIO"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_ENABLED_PROPERTY) >> false >> false >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COUNTRY_CODE_PROPERTY)>>null>>null>>null>>"OH"
		testObjCatalogTools.setLoggingDebug(true)
		when:
		List list = testObjCatalogTools.getIntlCountriesVOList("IN")
		then:
		list.size()==1
		((CountryVO)list.get(0)).getCountryCode().equals("OH")
		((CountryVO)list.get(0)).getCountryName().equals("OHIO")
	}
	def"The values are country code and the country code in the VO International Billing ,country code is null"(){
		given:
		testObjCatalogTools.executeRQLQuery(_, _, BBBCatalogConstants.INTL_BILLING_ADDRESS_COUNTRY_ITEM_DESCRIPTOR, _) >> []
		when:
		List list = testObjCatalogTools.getIntlCountriesVOList("")
		then:
		list.size()==0
	}
	def"The values are country code and the country code in the VOInternational Billing ,rql query returns null"(){
		given:
		testObjCatalogTools.executeRQLQuery(_, _, BBBCatalogConstants.INTL_BILLING_ADDRESS_COUNTRY_ITEM_DESCRIPTOR, _) >> null
		when:
		List list = testObjCatalogTools.getIntlCountriesVOList("")
		then:
		list.size()==0
	}
	def"The value are country names and the keys are country code"(){
		given:
		testObjCatalogTools.executeRQLQuery(_, _, BBBCatalogConstants.BILLING_ADDRESS_COUNTRY_ITEM_DESCRIPTOR, _) >> [repositoryItemMock,repositoryItemMock,repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COUNTRY_NAME_PROPERTY)>> null>>"OHIO"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.COUNTRY_CODE_PROPERTY)>>null>>null>>"OH"
		when:
		Map map = testObjCatalogTools.getCountriesInfoForCreditCard("US")
		then:
		map.size()==1
		map.get("OH").equals("OHIO")
	}
	def"The value are country names and the keys are country code,country code is null"(){
		given:
		testObjCatalogTools.executeRQLQuery(_, _, BBBCatalogConstants.BILLING_ADDRESS_COUNTRY_ITEM_DESCRIPTOR, _) >> []
		when:
		Map map = testObjCatalogTools.getCountriesInfoForCreditCard("")
		then:
		map.size()==0
	}
	def"The value are country names and the keys are country code,rql query returns null"(){
		given:
		testObjCatalogTools.executeRQLQuery(_, _, BBBCatalogConstants.BILLING_ADDRESS_COUNTRY_ITEM_DESCRIPTOR, _) >> null
		when:
		Map map= testObjCatalogTools.getCountriesInfoForCreditCard("")
		then:
		map.size()==0
	}
	def"the details are retrieved from the Repository Cache and the VO is prepared,throw exception"(){
		given:
		catalogRepositoryMock.getItem("Sku1234",  BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException()}
		when:
		testObjCatalogTools.getSKUDetails("BebBathUS", "Sku1234", false, false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")

	}
	def"the details are retrieved from the Repository Cache and the VO is prepared,sku is  active"(){
		given:
		catalogRepositoryMock.getItem("Sku1234",  BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		1*globalRepositoryToolsMock.isSkuActive(repositoryItemMock, _)>> true
		testObjCatalogTools.getSKUDetailVO(repositoryItemMock, "BebBathUS", false) >> skuDetailVOMock
		when:
		SKUDetailVO sku= testObjCatalogTools.getSKUDetails("BebBathUS", "Sku1234", false, true)
		then:
		sku == skuDetailVOMock
	}
	def"the details are retrieved from the Repository Cache and the VO is prepared,addexception is  enable"(){
		given:
		catalogRepositoryMock.getItem("Sku1234",  BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		0*globalRepositoryToolsMock.isSkuActive(repositoryItemMock, _)>> true
		testObjCatalogTools.getSKUDetailVO(repositoryItemMock, "BebBathUS", false) >> skuDetailVOMock
		when:
		SKUDetailVO sku= testObjCatalogTools.getSKUDetails("BebBathUS", "Sku1234", false, false)
		then:
		sku == skuDetailVOMock
	}
	def"the details are retrieved from the Repository Cache and the VO is prepared,sku is not active"(){
		given:
		catalogRepositoryMock.getItem("Sku1234",  BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		globalRepositoryToolsMock.isSkuActive(repositoryItemMock, _)>> false
		when:
		testObjCatalogTools.getSKUDetails("BebBathUS", "Sku1234", false, true)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1003:1003")
	}
	def"If the SKU does not exist in the system then the method will throw BBBBusinessException"(){
		when:
		testObjCatalogTools.getSKUDetails("BebBathUS", "Sku1234", false, false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1000:1000")
	}
	def"If the site id does not exist  method will throw BBBBusinessException"(){
		when:
		testObjCatalogTools.getSKUDetails("", "Sku1234", false, false)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"This method returns a VO containing the category related data"(){
		given:
		testObjCatalogTools.getCurrentSiteId()>> "BedBathUS"
		catalogRepositoryMock.getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >> qBuilder
		qBuilder.createPropertyQueryExpression("id") >> qExp
		qBuilder.createConstantQueryExpression("ca1234") >> qExp
		qBuilder.createComparisonQuery(qExp, qExp,QueryBuilder.EQUALS)>> query
		repositoryViewMock.executeQuery(query) >> [repositoryItemMock]
		testObjCatalogTools.isCategoryActive(repositoryItemMock) >> true
		testObjCatalogTools.getCategoryDetail("BedBathUS", "rep1234",false) >> categoryVOMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_CATEGORIES_PROPERTY_NAME)>>[repositoryItemMock]
		repositoryItemMock.getRepositoryId() >> "rep1234"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY)>> true
		when:
		CategoryVO cVO = testObjCatalogTools.getFullCategoryVO("ca1234")
		then:
		cVO != null
		cVO.getIsCollege()
		cVO.getPhantomCategory()
		cVO.getCategoryImage() == null
		cVO.getCategoryImage() == null
		cVO.getCategoryDisplayType() == null
		cVO.getSubCategories().size() == 1
	}
	def"This method returns a VO containing the category related data,category is not active"(){
		given:
		testObjCatalogTools.getCurrentSiteId()>> "BedBathUS"
		catalogRepositoryMock.getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >> qBuilder
		qBuilder.createPropertyQueryExpression("id") >> qExp
		qBuilder.createConstantQueryExpression("ca1234") >> qExp
		qBuilder.createComparisonQuery(qExp, qExp,QueryBuilder.EQUALS)>> query
		repositoryViewMock.executeQuery(query) >> [repositoryItemMock]
		testObjCatalogTools.isCategoryActive(repositoryItemMock) >> false
		when:
		CategoryVO cVO = testObjCatalogTools.getFullCategoryVO("ca1234")
		then:
		cVO != null
		cVO.isErrorExist()
		cVO.getErrorCode().equals(BBBCatalogErrorCodes.DATA_NOT_FOUND)
		cVO.getErrorMessage().equals(BBBCatalogConstants.INACTIVE_CATEGORY)
	}
	def"This method returns a VO containing the category related data,thriw repository exception"(){
		given:
		testObjCatalogTools.getCurrentSiteId()>> "BedBathUS"
		catalogRepositoryMock.getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> {throw new RepositoryException()}
		when:
		CategoryVO cVO = testObjCatalogTools.getFullCategoryVO("ca1234")
		then:
		cVO != null
		cVO.isErrorExist()
		cVO.getErrorCode().equals(BBBCatalogErrorCodes.NOT_ABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY)
		cVO.getErrorMessage().equals(BBBCatalogConstants.CATEGORY_UNAVAILABLE)
	}
	def"This method returns a VO containing the category related data,data from repository is length is zero"(){
		given:
		testObjCatalogTools.getCurrentSiteId()>> "BedBathUS"
		catalogRepositoryMock.getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >> qBuilder
		qBuilder.createPropertyQueryExpression("id") >> qExp
		qBuilder.createConstantQueryExpression("ca1234") >> qExp
		qBuilder.createComparisonQuery(qExp, qExp,QueryBuilder.EQUALS)>> query
		repositoryViewMock.executeQuery(query) >> []
		when:
		CategoryVO cVO = testObjCatalogTools.getFullCategoryVO("ca1234")
		then:
		cVO != null
		cVO.isErrorExist()
		cVO.getErrorCode().equals(BBBCatalogErrorCodes.DATA_NOT_FOUND)
		cVO.getErrorMessage().equals(BBBCatalogConstants.INACTIVE_CATEGORY)
	}
	def"This method returns a VO containing the category related data,data from repository is null"(){
		given:
		testObjCatalogTools.getCurrentSiteId()>> "BedBathUS"
		catalogRepositoryMock.getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >> qBuilder
		qBuilder.createPropertyQueryExpression("id") >> qExp
		qBuilder.createConstantQueryExpression("ca1234") >> qExp
		qBuilder.createComparisonQuery(qExp, qExp,QueryBuilder.EQUALS)>> query
		repositoryViewMock.executeQuery(query) >> null
		when:
		CategoryVO cVO = testObjCatalogTools.getFullCategoryVO("ca1234")
		then:
		cVO != null
		cVO.isErrorExist()
		cVO.getErrorCode().equals(BBBCatalogErrorCodes.DATA_NOT_FOUND)
		cVO.getErrorMessage().equals(BBBCatalogConstants.INACTIVE_CATEGORY)		
	}
	def"This method returns a VO containing the category related data,category id is empty"(){
		when:
		CategoryVO cVO = testObjCatalogTools.getFullCategoryVO("")
		then:
		cVO != null
		cVO.isErrorExist()
		cVO.getErrorCode().equals(BBBCatalogErrorCodes.INVALID_INPUT_PROVIDED)
		cVO.getErrorMessage().equals(BBBCatalogConstants.INVALID_CATEGORY)
	}
	def"The method gets category details in the form of CategoryVO data"(){
		given:
		catalogRepositoryMock.getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >> qBuilder
		qBuilder.createPropertyQueryExpression("id") >> qExp
		qBuilder.createConstantQueryExpression("ca1234") >> qExp
		qBuilder.createComparisonQuery(qExp, qExp,QueryBuilder.EQUALS)>> query
		repositoryViewMock.executeQuery(query) >> [repositoryItemMock]
		testObjCatalogTools.isCategoryActive(repositoryItemMock) >> true
		testObjCatalogTools.getCategoryDetail("BedBathUS", "rep1234",false) >> categoryVOMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_CATEGORIES_PROPERTY_NAME)>>[]
		repositoryItemMock.getRepositoryId() >> "rep1234"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)>> BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME)>> BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME)>> BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_CATEGORY_PROPERTY_NAME) >>[]
		when:
		CategoryVO cVO = testObjCatalogTools.getCategoryDetail("BedBathUS","ca1234",true)
		then:
		cVO != null
		cVO.getIsCollege()== null
		cVO.getPhantomCategory()==null
		cVO.getCategoryImage() == BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME
		cVO.getCategoryDisplayType() == BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME
		cVO.getSubCategories()==null
		cVO.getCategoryName() == BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
	}
	def"The method gets category details in the form of CategoryVO data,ProductList is not  Required"(){
		given:
		catalogRepositoryMock.getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >> qBuilder
		qBuilder.createPropertyQueryExpression("id") >> qExp
		qBuilder.createConstantQueryExpression("ca1234") >> qExp
		qBuilder.createComparisonQuery(qExp, qExp,QueryBuilder.EQUALS)>> query
		repositoryViewMock.executeQuery(query) >> [repositoryItemMock]
		testObjCatalogTools.isCategoryActive(repositoryItemMock) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_CATEGORIES_PROPERTY_NAME)>>[]
		repositoryItemMock.getRepositoryId() >> "rep1234"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)>> BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME)>> BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME)>> BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_CATEGORY_PROPERTY_NAME) >>[]
		when:
		CategoryVO cVO = testObjCatalogTools.getCategoryDetail("BedBathUS","ca1234",false)
		then:
		cVO != null
		cVO.getIsCollege()== null
		cVO.getPhantomCategory()==null
		cVO.getCategoryImage() == BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME
		cVO.getCategoryDisplayType() == BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME
		cVO.getSubCategories()==null
		cVO.getCategoryName() == BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
	}
	def"The method gets category details in the form of CategoryVO data,throw business exception"(){
	given:
	catalogRepositoryMock.getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryViewMock
	repositoryViewMock.getQueryBuilder() >> qBuilder
	qBuilder.createPropertyQueryExpression("id") >> qExp
	qBuilder.createConstantQueryExpression("ca1234") >> qExp
	qBuilder.createComparisonQuery(qExp, qExp,QueryBuilder.EQUALS)>> query
	repositoryViewMock.executeQuery(query) >> [repositoryItemMock]
	testObjCatalogTools.isCategoryActive(repositoryItemMock) >> true
	testObjCatalogTools.getCategoryDetail("BedBathUS", "rep1234",false) >> {throw new BBBBusinessException("Mock of business exception")}
	repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_CATEGORIES_PROPERTY_NAME)>>[repositoryItemMock]
	repositoryItemMock.getRepositoryId() >> "rep1234"
	repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) >> null
	repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)>> BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
	repositoryItemMock.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME)>> BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME
	repositoryItemMock.getPropertyValue(BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME)>> BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME
	repositoryItemMock.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY)>> null
	repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_CATEGORY_PROPERTY_NAME) >>[]
	when:
	CategoryVO cVO = testObjCatalogTools.getCategoryDetail("BedBathUS","ca1234",false)
	then:
	cVO != null
	cVO.getIsCollege()== null
	cVO.getPhantomCategory()==null
	cVO.getCategoryImage() == BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME
	cVO.getCategoryDisplayType() == BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME
	cVO.getSubCategories()==null
	cVO.getCategoryName() == BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
	}
	def"The method gets category details in the form of CategoryVO data,ProductList is   Required"(){
		given:
		catalogRepositoryMock.getView(BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryViewMock
		repositoryViewMock.getQueryBuilder() >> qBuilder
		qBuilder.createPropertyQueryExpression("id") >> qExp
		qBuilder.createConstantQueryExpression("ca1234") >> qExp
		qBuilder.createComparisonQuery(qExp, qExp,QueryBuilder.EQUALS)>> query
		repositoryViewMock.executeQuery(query) >> [repositoryItemMock]
		testObjCatalogTools.isCategoryActive(repositoryItemMock) >> true
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_CATEGORIES_PROPERTY_NAME)>>[]
		repositoryItemMock.getRepositoryId() >> "rep1234"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) >> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME)>> BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME)>> BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME)>> BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PHANTOM_CATEGORY)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_CATEGORY_PROPERTY_NAME) >>[repositoryItemMock]
		when:
		CategoryVO cVO = testObjCatalogTools.getCategoryDetail("BedBathUS","ca1234",true)
		then:
		cVO != null
		cVO.getIsCollege()== null
		cVO.getPhantomCategory()==null
		cVO.getCategoryImage() == BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME
		cVO.getCategoryDisplayType() == BBBCatalogConstants.NODE_TYPE_CATEGORY_PROPERTY_NAME
		cVO.getSubCategories()==null
		cVO.getCategoryName() == BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME
		cVO.getChildProducts().size()==1
		cVO.getChildProducts().get(0).equals("rep1234")
		}
	def"This method to get parentcatageory from repository"(){
		given:
		catalogRepositoryMock.getItem("prd1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR)>> repositoryItemMock
		catalogRepositoryMock.getItem("cat1234", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRIMARY_PARENT_CATEGORY) >> "cat1234"
		when:
		String  value  = testObjCatalogTools.getPrimaryCategory("prd1234")
		then:
		value.equals("cat1234")
	}
	def"This method to get parentcatageory from repository,parent repository category is null"(){
		given:
		catalogRepositoryMock.getItem("prd1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR)>> repositoryItemMock
		catalogRepositoryMock.getItem("cat1234", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRIMARY_PARENT_CATEGORY) >> "cat1234"
		when:
		String  value  = testObjCatalogTools.getPrimaryCategory("prd1234")
		then:
		value.equals(null)
	}
	def"This method to get parentcatageory from repository,primary category id is empty"(){
		given:
		catalogRepositoryMock.getItem("prd1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR)>> repositoryItemMock
		catalogRepositoryMock.getItem("cat1234", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRIMARY_PARENT_CATEGORY) >> null
		when:
		String  value  = testObjCatalogTools.getPrimaryCategory("prd1234")
		then:
		value.equals(null)
	}
	def"This method to get parentcatageory from repository,  repository  item is null"(){
		given:
		catalogRepositoryMock.getItem("prd1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR)>> null
		when:
		String  value  =  testObjCatalogTools.getPrimaryCategory("prd1234")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1004:1004")
	}
	def"This method to get parentcatageory from repository, throw repository exception"(){
		given:
		catalogRepositoryMock.getItem("prd1234", BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR)>> {throw new RepositoryException()}
		when:
		String  value  = testObjCatalogTools.getPrimaryCategory("prd1234")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"This method to get parentcatageory from repository,product id is null"(){
		when:
		String  value  =  testObjCatalogTools.getPrimaryCategory("")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2006:2006")
	}
	def"This method is used to fetch the tab list based page name"(){
		given:
		testObjCatalogTools.parseRqlStatement(_)>>rqlStatementMock
		siteRepositoryMock.getView(BBBCatalogConstants.PAGE_ORDER_ITEM_DESCRIPTOR)>> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.TABS_LIST_PROPERTY) >> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.TAB_NAME_PROPERTY) >> "tab"
		when:
		List<String> tabList  = testObjCatalogTools.getTabNameList("pageName")
		then:
		tabList.size()==1
		tabList.get(0).equals("tab")
	}
	def"This method is used to fetch the tab list based page name,tab for page is zero"(){
		given:
		testObjCatalogTools.parseRqlStatement(_)>>rqlStatementMock
		siteRepositoryMock.getView(BBBCatalogConstants.PAGE_ORDER_ITEM_DESCRIPTOR)>> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> [repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.TABS_LIST_PROPERTY) >> []
		when:
		List<String> tabList  = testObjCatalogTools.getTabNameList("pageName")
		then:
		tabList.size()==0
	}
	def"This method is used to fetch the tab list based page name,items from repository is empty"(){
		given:
		testObjCatalogTools.parseRqlStatement(_)>>rqlStatementMock
		siteRepositoryMock.getView(BBBCatalogConstants.PAGE_ORDER_ITEM_DESCRIPTOR)>> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >> []
		when:
		List<String> tabList  = testObjCatalogTools.getTabNameList("pageName")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("8960:8960")
	}
	def"This method is used to fetch the tab list based page name,rqlquery is null"(){
		given:
		testObjCatalogTools.setPageOrderQuery(null)
		when:
		List<String> tabList  = testObjCatalogTools.getTabNameList("pageName")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("8960:8960")
	}
	def"This method is used to fetch the tab list based page name,repository is null "(){
		given:
		testObjCatalogTools.setPageOrderRepository(null)
		when:
		List<String> tabList  = testObjCatalogTools.getTabNameList("pageName")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("8960:8960")
	}
	def"This method is used to fech repository item from repositor"(){
		given:
		testObjCatalogTools.parseRqlStatement(_)>>rqlStatementMock
		siteRepositoryMock.getView(BBBCatalogConstants.PAGE_ORDER_ITEM_DESCRIPTOR)>> null
		rqlStatementMock.executeQuery(null, _) >>{throw new RepositoryException()}
		testObjCatalogTools.setLoggingError(true)
		when:
		RepositoryItem[] repItems =  testObjCatalogTools.executeRQLQuery("rqlQuery", null,BBBCatalogConstants.PAGE_ORDER_ITEM_DESCRIPTOR,siteRepositoryMock)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"This method is used to fech repository item from repositor,result is null"(){
		given:
		testObjCatalogTools.parseRqlStatement(_)>>rqlStatementMock
		siteRepositoryMock.getView(BBBCatalogConstants.PAGE_ORDER_ITEM_DESCRIPTOR)>> repositoryViewMock
		rqlStatementMock.executeQuery(repositoryViewMock, _) >>null
		testObjCatalogTools.setLoggingError(true)
		when:
		RepositoryItem[] repItems =  testObjCatalogTools.executeRQLQuery("rqlQuery", null,BBBCatalogConstants.PAGE_ORDER_ITEM_DESCRIPTOR,siteRepositoryMock)
		then:
		repItems == null
	}
	def"This method will fetch the shop guide id for the given product and then check whether any guide id exist for that shop guide id and for the current site"(){
		given:
		repositoryMock.getView(BBBCatalogConstants.GUIDES)>>{throw new RepositoryException()}
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID)>>BBBCatalogConstants.PRODUCT_SHOP_GUIDE_ID
		when:
		String value = testObjCatalogTools.getProductGuideId(repositoryItemMock,"BedBathUS")
		then:
		Exception ex =thrown()
		ex.getMessage().equals("2003:2003")
	}
	def"This method will validate bedding date with syatem date "(){
		given:
		testObjCatalogTools.getCurrentSiteId()>>"BedBathUS"
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.NO_OF_DAYS) >> ["1"]
		when:
		boolean valid = testObjCatalogTools.validateBeddingAttDate("1/26/2017", "1/23/2017")
		then:
		valid == true
	}
	def"This method will validate bedding date with syatem date site BedBathCanada "(){
		given:
		testObjCatalogTools.getCurrentSiteId()>>"BedBathCanada"
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCatalogConstants.NO_OF_DAYS) >> ["1"]
		when:
		boolean valid = testObjCatalogTools.validateBeddingAttDate("26/1/2017", "27/1/2017")
		then:
		valid == false
	}
	def"This method will validate bedding date with syatem date bedddingshipdate is null"(){
		given:
		when:
		boolean valid = testObjCatalogTools.validateBeddingAttDate("", "12/21/2017")
		then:
		valid == false
	}
	def"This method will validate bedding date with syatem date current is null"(){
		given:
		when:
		boolean valid = testObjCatalogTools.validateBeddingAttDate("12/21/2017", "")
		then:
		valid == false
	}
	def"This method will return BeddingShipAddrVO details"(){
		given:
		BeddingShipAddrVO shipVOMock =Mock()
		schoolRepositoryToolsMock.getBeddingShipAddress("collegeIdValue") >> shipVOMock
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.getBeddingShipAddrVO("collegeIdValue")
		then:
		shipVO == shipVOMock
	}
	def"This method will return BeddingShipAddrVO details throw business exception"(){
		given:
		schoolRepositoryToolsMock.getBeddingShipAddress("collegeIdValue") >> {throw new BBBBusinessException("Mock of business exception")}
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.getBeddingShipAddrVO("collegeIdValue")
		then:
		shipVO == null
	}
	def"This method will return BeddingShipAddrVO details throw system exception"(){
		given:
		schoolRepositoryToolsMock.getBeddingShipAddress("collegeIdValue") >> {throw new BBBSystemException("Mock of system exception")}
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.getBeddingShipAddrVO("collegeIdValue")
		then:
		shipVO == null
	}
	def"This method will validate bedding kit attribute and if it present it will get school details"(){
		given:
		BBBShippingGroupCommerceItemRelationship cItemRel = Mock()
		BBBShippingGroupCommerceItemRelationship cItemRel1= Mock()
		BBBCommerceItem cItem =Mock()
		AuxiliaryData data = Mock()
		cItemRel.getCommerceItem() >> cItem
		cItemRel1.getCommerceItem() >> cItem
		cItem.getAuxiliaryData()>> data
		data.getCatalogRef() >>  repositoryItemMock
		hShip.getCommerceItemRelationships() >>[cItemRel1,cItemRel] 
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.BEDDING_KIT_ATTRIBUTE)>>["rep1234"]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >>[repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >> "rep1234"
		repositoryItemMock.getPropertyValue("skuAttribute")>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME) >>null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue("id") >>"rep1234"
		BeddingShipAddrVO shipVOMock =Mock()
		2*schoolRepositoryToolsMock.getBeddingShipAddress("college") >>shipVOMock
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt([hShip], "college")
		then:
		shipVO == shipVOMock
	}
	def"This method will validate bedding kit attribute and if it present it will get school details,date does not match"(){
		given:
		BBBShippingGroupCommerceItemRelationship cItemRel = Mock()
		BBBShippingGroupCommerceItemRelationship cItemRel1= Mock()
		BBBCommerceItem cItem =Mock()
		AuxiliaryData data = Mock()
		cItemRel.getCommerceItem() >> cItem
		cItemRel1.getCommerceItem() >> cItem
		cItem.getAuxiliaryData()>> data
		data.getCatalogRef() >>  repositoryItemMock
		hShip.getCommerceItemRelationships() >>[cItemRel1,cItemRel]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.BEDDING_KIT_ATTRIBUTE)>>["rep1234"]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >>[repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >> "rep1234"
		repositoryItemMock.getPropertyValue("skuAttribute")>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME) >>new Date(addDays(-5, "MM/DD/YYYY"))
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue("id") >>"rep1234"
		BeddingShipAddrVO shipVOMock =Mock()
		0*schoolRepositoryToolsMock.getBeddingShipAddress("college") >>shipVOMock
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt([hShip], "college")
		then:
		shipVO == null
	}
	def"This method will validate bedding kit attribute and if it present it will get school details,attributes does not match"(){
		given:
		BBBShippingGroupCommerceItemRelationship cItemRel = Mock()
		BBBShippingGroupCommerceItemRelationship cItemRel1= Mock()
		BBBCommerceItem cItem =Mock()
		AuxiliaryData data = Mock()
		cItemRel.getCommerceItem() >> cItem
		cItemRel1.getCommerceItem() >> cItem
		cItem.getAuxiliaryData()>> data
		data.getCatalogRef() >>  repositoryItemMock
		hShip.getCommerceItemRelationships() >>[cItemRel1,cItemRel]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.BEDDING_KIT_ATTRIBUTE)>>[]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >>[repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >> "rep1234"
		repositoryItemMock.getPropertyValue("skuAttribute")>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME) >>null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue("id") >>"rep1234"
		BeddingShipAddrVO shipVOMock =Mock()
		0*schoolRepositoryToolsMock.getBeddingShipAddress("college") >>shipVOMock
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt([hShip], "college")
		then:
		shipVO == null
	}
	def"This method will validate bedding kit attribute and if it present it will get school details,throw system exception"(){
		given:
		BBBShippingGroupCommerceItemRelationship cItemRel = Mock()
		BBBShippingGroupCommerceItemRelationship cItemRel1= Mock()
		BBBCommerceItem cItem =Mock()
		AuxiliaryData data = Mock()
		cItemRel.getCommerceItem() >> cItem
		cItemRel1.getCommerceItem() >> cItem
		cItem.getAuxiliaryData()>> data
		data.getCatalogRef() >>  repositoryItemMock
		hShip.getCommerceItemRelationships() >>[cItemRel1,cItemRel]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.BEDDING_KIT_ATTRIBUTE)>>["rep1234"]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >>[repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >> "rep1234"
		repositoryItemMock.getPropertyValue("skuAttribute")>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME) >>null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue("id") >>"rep1234"
		BeddingShipAddrVO shipVOMock =Mock()
		2*schoolRepositoryToolsMock.getBeddingShipAddress("college") >>{throw new BBBSystemException("Mock of system exception")}
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt([hShip], "college")
		then:
		shipVO == null
	}
	def"This method will validate bedding kit attribute and if it present it will get school details, throw business exception"(){
		given:
		BBBShippingGroupCommerceItemRelationship cItemRel = Mock()
		BBBShippingGroupCommerceItemRelationship cItemRel1= Mock()
		BBBCommerceItem cItem =Mock()
		AuxiliaryData data = Mock()
		cItemRel.getCommerceItem() >> cItem
		cItemRel1.getCommerceItem() >> cItem
		cItem.getAuxiliaryData()>> data
		data.getCatalogRef() >>  repositoryItemMock
		hShip.getCommerceItemRelationships() >>[cItemRel1,cItemRel]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.BEDDING_KIT_ATTRIBUTE)>>["rep1234"]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >>[repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >> "rep1234"
		repositoryItemMock.getPropertyValue("skuAttribute")>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_RELATION_PROPERTY_NAME) >>null
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_RELATION_PROPERTY_NAME)>> null
		repositoryItemMock.getPropertyValue("id") >>"rep1234"
		BeddingShipAddrVO shipVOMock =Mock()
		2*schoolRepositoryToolsMock.getBeddingShipAddress("college") >>{throw new BBBBusinessException("Mock of business exception")}
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt([hShip], "college")
		then:
		shipVO == null
	}
	def"This method will validate bedding kit attribute and if it present it will get school details,sku attribute is null"(){
		given:
		BBBShippingGroupCommerceItemRelationship cItemRel = Mock()
		BBBShippingGroupCommerceItemRelationship cItemRel1= Mock()
		BBBCommerceItem cItem =Mock()
		AuxiliaryData data = Mock()
		cItemRel.getCommerceItem() >> cItem
		cItemRel1.getCommerceItem() >> cItem
		cItem.getAuxiliaryData()>> data
		data.getCatalogRef() >>  repositoryItemMock
		hShip.getCommerceItemRelationships() >>[cItemRel1,cItemRel]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.BEDDING_KIT_ATTRIBUTE)>>[""]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >>[repositoryItemMock].toSet()
		repositoryItemMock.getRepositoryId() >> "rep1234"
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt([hShip], "")
		then:
		shipVO == null
	}
	def"This method will validate bedding kit attribute and if it present it will get school details,catalogrerf is null"(){
		given:
		BBBShippingGroupCommerceItemRelationship cItemRel = Mock()
		BBBShippingGroupCommerceItemRelationship cItemRel1= Mock()
		BBBCommerceItem cItem =Mock()
		AuxiliaryData data = Mock()
		cItemRel.getCommerceItem() >> cItem
		cItemRel1.getCommerceItem() >> cItem
		cItem.getAuxiliaryData()>> data
		data.getCatalogRef() >>  null
		hShip.getCommerceItemRelationships() >>[cItemRel1,cItemRel]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.BEDDING_KIT_ATTRIBUTE)>>[""]
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt([hShip], "")
		then:
		shipVO == null
	}
	def"This method will validate bedding kit attribute and if it present it will get school details,repositoryId is null"(){
		given:
		BBBShippingGroupCommerceItemRelationship cItemRel = Mock()
		BBBShippingGroupCommerceItemRelationship cItemRel1= Mock()
		BBBCommerceItem cItem =Mock()
		AuxiliaryData data = Mock()
		cItemRel.getCommerceItem() >> cItem
		cItemRel1.getCommerceItem() >> cItem
		cItem.getAuxiliaryData()>> data
		data.getCatalogRef() >>  repositoryItemMock
		hShip.getCommerceItemRelationships() >>[cItemRel1,cItemRel]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.BEDDING_KIT_ATTRIBUTE)>>[""]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >>[repositoryItemMock].toSet()
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt([hShip], "")
		then:
		shipVO == null
	}
	def"This method will validate bedding kit attribute and if it present it will get school details,sku attribute is empty"(){
		given:
		BBBShippingGroupCommerceItemRelationship cItemRel = Mock()
		BBBShippingGroupCommerceItemRelationship cItemRel1= Mock()
		BBBCommerceItem cItem =Mock()
		AuxiliaryData data = Mock()
		cItemRel.getCommerceItem() >> cItem
		cItemRel1.getCommerceItem() >> cItem
		cItem.getAuxiliaryData()>> data
		data.getCatalogRef() >>  repositoryItemMock
		hShip.getCommerceItemRelationships() >>[cItemRel1,cItemRel]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.BEDDING_KIT_ATTRIBUTE)>>[""]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >>[].toSet()
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt([hShip], "")
		then:
		shipVO == null
	}
	def"This method will validate bedding kit attribute and if it present it will get school details,sku attribute relation is null"(){
		given:
		BBBShippingGroupCommerceItemRelationship cItemRel = Mock()
		BBBShippingGroupCommerceItemRelationship cItemRel1= Mock()
		BBBCommerceItem cItem =Mock()
		AuxiliaryData data = Mock()
		cItemRel.getCommerceItem() >> cItem
		cItemRel1.getCommerceItem() >> cItem
		cItem.getAuxiliaryData()>> data
		data.getCatalogRef() >>  repositoryItemMock
		hShip.getCommerceItemRelationships() >>[cItemRel1,cItemRel]
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.BEDDING_KIT_ATTRIBUTE)>>[""]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SKU_ATTRIBUTE_RELATION_SKU_PROPERTY_NAME) >>null
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt([hShip], "")
		then:
		shipVO == null
	}
	def"This method will validate bedding kit attribute and if it present it will get school details,relation ship list is empty"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,BBBCatalogConstants.BEDDING_KIT_ATTRIBUTE)>>[""]
		hShip.getCommerceItemRelationships() >>[]
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt([hShip], "")
		then:
		shipVO == null
	}
	def"This method will validate bedding kit attribute and if it present it will get school details,shipping group is store group"(){
		given:
		BBBStoreShippingGroup sg = Mock()
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt([sg], "")
		then:
		shipVO == null
	}
	def"This method will validate bedding kit attribute and if it present it will get school details,shipping group null"(){
		given:
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt([null], "")
		then:
		shipVO == null
	}
	def"This method will validate bedding kit attribute and if it present it will get school details,shippingroup list is empty"(){
		given:
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt([], "")
		then:
		shipVO == null
	}
	def"This method will validate bedding kit attribute and if it present it will get school details,shipping group list is null"(){
		given:
		when:
		BeddingShipAddrVO shipVO = testObjCatalogTools.validateBedingKitAtt(null, "")
		then:
		shipVO == null
	}
	def"Gets the sorted college category"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, _)>>["cat1234"]
		CategoryVO cmainVO = new CategoryVO()
		CategoryVO subVO = new CategoryVO()
		CategoryVO subVO1 = new CategoryVO()
		cmainVO.setSubCategories([subVO,subVO1])
		cmainVO.setCategoryId("cat1234")
		subVO.setCategoryId("cat12345")
		subVO1.setCategoryId("cat1234")
		when:
		CategoryVO catVO = testObjCatalogTools.getSortedCollegeCategory(cmainVO)
		then:
		catVO.getSubCategories().size()==2
		((CategoryVO)catVO.getSubCategories().get(0)).getCategoryId().equals("cat1234")
	}
	def"Gets the sorted college category,sub categories is empty"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, _)>>["cat1234"]
		CategoryVO cmainVO = new CategoryVO()
		cmainVO.setSubCategories([])
		cmainVO.setCategoryId("cat1234")
		when:
		CategoryVO catVO = testObjCatalogTools.getSortedCollegeCategory(cmainVO)
		then:
		catVO.getSubCategories().size()==0
	}
	def"Gets the sorted college category,category list is empty"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, _)>>[]
		CategoryVO cmainVO = new CategoryVO()
		cmainVO.setSubCategories([])
		cmainVO.setCategoryId("cat1234")
		when:
		CategoryVO catVO = testObjCatalogTools.getSortedCollegeCategory(cmainVO)
		then:
		catVO.getSubCategories().size()==0
	}
	def"Gets the sorted college category,throw business exception"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, _)>>{throw new BBBBusinessException("Mock of business exception")}
		CategoryVO cmainVO = new CategoryVO()
		when:
		CategoryVO catVO = testObjCatalogTools.getSortedCollegeCategory(cmainVO)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("6006")
	}
	def"Gets the sorted college category,throw System exception"(){
		given:
		testObjCatalogTools.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, _)>>{throw new BBBSystemException("Mock of System exception")}
		CategoryVO cmainVO = new CategoryVO()
		when:
		CategoryVO catVO = testObjCatalogTools.getSortedCollegeCategory(cmainVO)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003")
	}
	def"Gets the brand display flag"(){
		given:
		testObjCatalogTools.executeRQLQuery(_, _,BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR, catalogRepositoryMock)>>[repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_BRAND_PROPERTY_NAME) >> true
		when:
		boolean valid = testObjCatalogTools.getBrandDisplayFlag("brandName")
		then:
		valid
	}
	def"Gets the brand display flag,display brand property is null"(){
		given:
		testObjCatalogTools.executeRQLQuery(_, _,BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR, catalogRepositoryMock)>>[repositoryItemMock]
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_BRAND_PROPERTY_NAME) >> null
		when:
		boolean valid = testObjCatalogTools.getBrandDisplayFlag("brandName")
		then:
		valid == false
	}
	def"Gets the brand display flag,rql query gives zero items"(){
		given:
		testObjCatalogTools.executeRQLQuery(_, _,BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR, catalogRepositoryMock)>>[]
		when:
		boolean valid = testObjCatalogTools.getBrandDisplayFlag("brandName")
		then:
		valid== false
	}
	def"Gets the brand display flag,rql query gives null"(){
		given:
		testObjCatalogTools.executeRQLQuery(_, _,BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR, catalogRepositoryMock)>>null
		when:
		boolean valid = testObjCatalogTools.getBrandDisplayFlag("brandName")
		then:
		valid== false
	}
	def"Gets the brand display flag, brand name is empty"(){
		when:
		boolean valid = testObjCatalogTools.getBrandDisplayFlag("")
		then:
		valid == false
	}
	def"getAttributeInfoRepositoryItems ,TC for get repository items"(){
		given:
		repositoryMock.getItems(_, BBBCatalogConstants.ATTRIBUTE_INFO)>> repositoryItemMockList
		when:
		 RepositoryItem[] items = testObjCatalogTools.getAttributeInfoRepositoryItems("id")
		then:
		items.length==1
	}
	def"getAttributeInfoRepositoryItems ,TC throw repository"(){
		given:
		repositoryMock.getItems(_, BBBCatalogConstants.ATTRIBUTE_INFO)>> {throw new RepositoryException()}
		when:
		 RepositoryItem[] items = testObjCatalogTools.getAttributeInfoRepositoryItems("id")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003")
	}
	def"This method is used to validate sku is eligble for shipping or not"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS) >> [repositoryItemMock].toSet()
		when:
		boolean valid  = testObjCatalogTools.isSkuWithRestrictedShipping("sku1234", "BedBathUs")
		then:
		valid== true
	}
	def"This method is used to validate sku is eligble for shipping or not,site sku regions value is empty"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS) >> [].toSet()
		when:
		boolean valid  = testObjCatalogTools.isSkuWithRestrictedShipping("sku1234", "BedBathUs")
		then:
		valid== false
	}
	def"This method is used to validate sku is eligble for shipping or not,site sku regions value is null"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS) >> null
		when:
		boolean valid  = testObjCatalogTools.isSkuWithRestrictedShipping("sku1234", "BedBathUs")
		then:
		valid== false
	}
	def"This method is used to validate sku is eligble for shipping or not,sku item is not available"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> null
		when:
		boolean valid  = testObjCatalogTools.isSkuWithRestrictedShipping("sku1234", "BedBathUs")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1000")
	}
	def"This method is used to validate sku is eligble for shipping or not,sku item throw repository exception"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> {throw new RepositoryException()}
		when:
		boolean valid  = testObjCatalogTools.isSkuWithRestrictedShipping("sku1234", "BedBathUs")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003")
	}
	def"This method is used to validate sku is eligble for shipping or not,sku id is null"(){
		when:
		boolean valid  = testObjCatalogTools.isSkuWithRestrictedShipping(null, "BedBathUs")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("8007")
	}
	def"This method is used to validate sku is eligble for shipping or not sku details"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS) >> [repositoryItemMock].toSet()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.RESTRICTED_SKU_ZIP_CODES) >> "4320678945,73054"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGION_NAME) >>BBBCatalogConstants.REGION_NAME
		when:
		String valid  = testObjCatalogTools.getRestrictedSkuDetails("sku1234", "43206")
		then:
		valid.equals(BBBCatalogConstants.REGION_NAME)
	}
	def"This method is used to validate sku is eligble for shipping or not sku details,site sku regions value is empty"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS) >> [].toSet()
		when:
		String valid  = testObjCatalogTools.getRestrictedSkuDetails("sku1234", "43206")
		then:
		valid== null
	}
	def"This method is used to validate sku is eligble for shipping or not sku details,site sku regions value is null"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS) >> null
		when:
		String valid  = testObjCatalogTools.getRestrictedSkuDetails("sku1234", "43206")
		then:
		valid== null
	}
	def"This method is used to validate sku is eligble for shipping or not sku details,sku item is not available"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> null
		when:
		String valid  = testObjCatalogTools.getRestrictedSkuDetails("sku1234", "43206")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1000")
	}
	def"This method is used to validate sku is eligble for shipping or not sku details,sku item throw repository exception"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> {throw new RepositoryException()}
		when:
		String valid  = testObjCatalogTools.getRestrictedSkuDetails("sku1234", "43206")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003")
	}
	def"This method is used to validate sku is eligble for shipping or not sku details,sku id is null"(){
		when:
		String valid  = testObjCatalogTools.getRestrictedSkuDetails(null, "43206")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("8007")
	}
	def"This method is used to validate sku is eligble for shipping or not with sku zip code"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS) >> [repositoryItemMock].toSet()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.RESTRICTED_SKU_ZIP_CODES) >> "4320678945,43206"
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGION_NAME) >>BBBCatalogConstants.REGION_NAME
		when:
		boolean valid  = testObjCatalogTools.isShippingZipCodeRestrictedForSku("sku1234","BedBathUS", "43206")
		then:
		valid==true
	}
	def"This method is used to validate sku is eligble for shipping or not with sku zip code,site sku region is null"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS) >> [null,repositoryItemMock].toSet()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.RESTRICTED_SKU_ZIP_CODES) >>null
		when:
		boolean valid  = testObjCatalogTools.isShippingZipCodeRestrictedForSku("sku1234","BedBathUS", "43206")
		then:
		valid==false
	}
	def"This method is used to validate sku is eligble for shipping or not with sku zip code,restricted sku zip codes is empty"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS) >> [repositoryItemMock].toSet()
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.RESTRICTED_SKU_ZIP_CODES) >>""
		when:
		boolean valid  = testObjCatalogTools.isShippingZipCodeRestrictedForSku("sku1234","BedBathUS", "43206")
		then:
		valid==false
	}
	def"This method is used to validate sku is eligble for shipping or not with sku zip code,site sku regions value is empty"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS) >> [].toSet()
		when:
		boolean valid  = testObjCatalogTools.isShippingZipCodeRestrictedForSku("sku1234","BedBathUS", "43206")
		then:
		valid==false
	}
	def"This method is used to validate sku is eligble for shipping or not with sku zip code,site sku regions value is null"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> repositoryItemMock
		repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITE_SKU_REGIONS) >> null
		when:
		boolean valid  = testObjCatalogTools.isShippingZipCodeRestrictedForSku("sku1234","BedBathUS", "43206")
		then:
		valid==false
	}
	def"This method is used to validate sku is eligble for shipping or not with sku zip code,sku id is AssemblyFee"(){
		when:
		boolean valid  = testObjCatalogTools.isShippingZipCodeRestrictedForSku("AssemblyFee","BedBathUS", "43206")
		then:
		valid==false
	}
	def"This method is used to validate sku is eligble for shipping or not with sku zip code,sku id is DeliverySurcharge"(){
		when:
		boolean valid  = testObjCatalogTools.isShippingZipCodeRestrictedForSku("DeliverySurcharge","BedBathUS", "43206")
		then:
		valid==false
	}
	def"This method is used to validate sku is eligble for shipping or not with sku zip code,sku item is not available"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> null
		when:
		boolean valid  = testObjCatalogTools.isShippingZipCodeRestrictedForSku("sku1234","BedBathUS", "43206")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("1000")
	}
	def"This method is used to validate sku is eligble for shipping or not with sku zip code,sku item throw repository exception"(){
		given:
		catalogRepositoryMock.getItem("sku1234",	BBBCatalogConstants.SKU_ITEM_DESCRIPTOR)>> {throw new RepositoryException()}
		when:
		boolean valid  = testObjCatalogTools.isShippingZipCodeRestrictedForSku("sku1234", "BedBathUS","43206")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("2003")
	}
	def"This method is used to validate sku is eligble for shipping or not with sku zip code,sku id is null"(){
		when:
		boolean valid  = testObjCatalogTools.isShippingZipCodeRestrictedForSku(null,"BedBathUS", "43206")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("8007")
	}
	def"This method is used to validate sku is eligble for shipping or not with sku zip code,zip code id is null"(){
		when:
		boolean valid  = testObjCatalogTools.isShippingZipCodeRestrictedForSku("sku1234","BedBathUS", null)
		then:
		Exception ex = thrown()
		ex.getMessage().equals("8007")
	}
	public static String addDays(int days ,String format) {	
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, days);  // number of days to add
		return sdf.format(c.getTime()).toString();
	}
}
