package com.bbb.tools

import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBConfigToolsImpl;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.cache.BBBDynamicPriceCacheContainer
import com.bbb.framework.goldengate.DCPrefixIdGenerator;
import com.bbb.framework.performance.logger.PerformanceLogger;
import com.bbb.repository.RepositoryItemMock;
import com.bbb.search.bean.result.BBBDynamicPriceSkuVO

import atg.repository.Repository;
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemImpl
import atg.repository.RepositoryView
import atg.servlet.ServletUtil
import atg.commerce.pricing.priceLists.PriceListException
import atg.commerce.pricing.priceLists.PriceListManager
import atg.repository.MutableRepository;
import atg.servlet.DynamoHttpServletRequest;
import spock.lang.Specification;

class BBBPriceListRepositoryToolsSpecification extends Specification {
	
	def BBBPriceListRepositoryTools testObj
	def MutableRepository priceListRepositoryMock = Mock()
	def Repository dynamicPriceRepositoryMock = Mock()
	def PriceListManager priceListManagerMock = Mock()
	def DynamoHttpServletRequest requestMock = Mock()
	def SKUDetailVO skuDetailVOMock = Mock()
	def PerformanceLogger perfMock = Mock()
	def RepositoryItemMock repositoryItemMock = Mock()
	def RepositoryItemMock priceRepositoryItemMock = Mock()
	def BBBDynamicPriceCacheContainer skuCacheContainerMock = Mock()
	def GlobalRepositoryTools globalRepositoryToolsMock = Mock()
	def BBBConfigToolsImpl configToolsImplMock = Mock()
	def BBBDynamicPriceSkuVO bbbDynamicPriceSkuVOMock = Mock()
	
	
	
	def setup() {
		
		testObj = new BBBPriceListRepositoryTools(priceListRepository:priceListRepositoryMock,
		dynamicPriceRepository:dynamicPriceRepositoryMock,priceListManager:priceListManagerMock,skuCacheContainer:skuCacheContainerMock,
		globalRepositoryTools:globalRepositoryToolsMock)
		
		ServletUtil.setCurrentRequest(requestMock)
		requestMock.getContextPath() >> "/store"
		requestMock.resolveName("/com/bbb/framework/performance/logger/PerformanceLogger") >> perfMock
		perfMock.isEnableCustomComponentsMonitoring() >> false
		
	}
	
	/* getIncartPrice - Test Cases START */
	
	def "getIncartPrice when siteId is different" () {
		given:
			
			testObj = Spy()
			
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setPriceListRepository(priceListRepositoryMock)
			testObj.setPriceListManager(priceListManagerMock)
			
			testObj.getCurrentSiteId() >> "BedBath"
			globalRepositoryToolsMock.returnCountryFromSession() >> null
			
			Map<String,String> inCartPriceListIdValue = new HashMap<String,String>()
			inCartPriceListIdValue.put("BedBathUSInCartPriceList", "PL1234")
			
			testObj.getConfigValueByconfigType(*_) >> inCartPriceListIdValue 
			
			priceListRepositoryMock.getItem("PL1234",BBBInternationalShippingConstants.PROPERTY_PRICELIST) >> repositoryItemMock
			 
			priceListManagerMock.getPrice(repositoryItemMock, "212121", "362263") >> priceRepositoryItemMock
			
			Double listPrice = new Double(32.22f)
			priceRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> listPrice 
			
		when:
			def results = testObj.getIncartPrice("212121", "362263")
			
		then:
		results == 32.220001220703125
	}
	
	def "getIncartPrice when Repository Exception occurs" () {
		given:
			
			testObj = Spy()
			
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setPriceListRepository(priceListRepositoryMock)
			testObj.setPriceListManager(priceListManagerMock)
			
			testObj.getCurrentSiteId() >> "BedBath"
			globalRepositoryToolsMock.returnCountryFromSession() >> null
			
			Map<String,String> inCartPriceListIdValue = new HashMap<String,String>()
			inCartPriceListIdValue.put("BedBathUSInCartPriceList", "PL1234")
			
			testObj.getConfigValueByconfigType(*_) >> inCartPriceListIdValue
			
			priceListRepositoryMock.getItem("PL1234",BBBInternationalShippingConstants.PROPERTY_PRICELIST) >> {throw new RepositoryException("Mock Repository Exception")}
			 
			priceListManagerMock.getPrice(repositoryItemMock, "212121", "362263") >> priceRepositoryItemMock
			
			Double listPrice = new Double(32.22f)
			priceRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> listPrice
			
		when:
			def results = testObj.getIncartPrice("212121", "362263")
			
		then:
		results == 0.0
	}
	
	def "getIncartPrice when PriceListException occurs" () {
		given:
			
			testObj = Spy()
			
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setPriceListRepository(priceListRepositoryMock)
			testObj.setPriceListManager(priceListManagerMock)
			
			testObj.getCurrentSiteId() >> "BedBath"
			globalRepositoryToolsMock.returnCountryFromSession() >> null
			
			Map<String,String> inCartPriceListIdValue = new HashMap<String,String>()
			inCartPriceListIdValue.put("BedBathUSInCartPriceList", "PL1234")
			
			testObj.getConfigValueByconfigType(*_) >> inCartPriceListIdValue
			
			priceListRepositoryMock.getItem("PL1234",BBBInternationalShippingConstants.PROPERTY_PRICELIST) >> repositoryItemMock
			 
			priceListManagerMock.getPrice(repositoryItemMock, "212121", "362263") >> {throw new PriceListException("Mock PriceListException")}
			
			Double listPrice = new Double(32.22f)
			priceRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> listPrice
			
		when:
			def results = testObj.getIncartPrice("212121", "362263")
			
		then:
		results == 0.0
	}
	
	def "getIncartPrice when BBBBusinessException occurs" () {
		given:
			
			testObj = Spy()
			
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setPriceListRepository(priceListRepositoryMock)
			testObj.setPriceListManager(priceListManagerMock)
			
			testObj.getCurrentSiteId() >> "BedBath"
			globalRepositoryToolsMock.returnCountryFromSession() >> null
			
			Map<String,String> inCartPriceListIdValue = new HashMap<String,String>()
			inCartPriceListIdValue.put("BedBathUSInCartPriceList", "PL1234")
			
			testObj.getConfigValueByconfigType(*_) >> {throw new BBBBusinessException("Mock BBBBusinessException")}
			
			priceListRepositoryMock.getItem("PL1234",BBBInternationalShippingConstants.PROPERTY_PRICELIST) >> repositoryItemMock
			 
			priceListManagerMock.getPrice(repositoryItemMock, "212121", "362263") >> priceRepositoryItemMock
			
			Double listPrice = new Double(32.22f)
			priceRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> listPrice
			
		when:
			def results = testObj.getIncartPrice("212121", "362263")
			
		then:
		results == 0.0
	}
	
	def "getIncartPrice when BBBSystemException occurs" () {
		given:
			
			testObj = Spy()
			
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setPriceListRepository(priceListRepositoryMock)
			testObj.setPriceListManager(priceListManagerMock)
			
			testObj.getCurrentSiteId() >> "BedBath"
			globalRepositoryToolsMock.returnCountryFromSession() >> null
			
			Map<String,String> inCartPriceListIdValue = new HashMap<String,String>()
			inCartPriceListIdValue.put("BedBathUSInCartPriceList", "PL1234")
			
			testObj.getConfigValueByconfigType(*_) >> {throw new BBBSystemException("Mock BBBSystemException")}
			
			priceListRepositoryMock.getItem("PL1234",BBBInternationalShippingConstants.PROPERTY_PRICELIST) >> repositoryItemMock
			 
			priceListManagerMock.getPrice(repositoryItemMock, "212121", "362263") >> priceRepositoryItemMock
			
			Double listPrice = new Double(32.22f)
			priceRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> listPrice
			
		when:
			def results = testObj.getIncartPrice("212121", "362263")
			
		then:
		results == 0.0
	}
	
	def "getIncartPrice when country is Mexico" () {
		given:
			
			testObj = Spy()
			
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setPriceListRepository(priceListRepositoryMock)
			testObj.setPriceListManager(priceListManagerMock)
			
			testObj.getCurrentSiteId() >> "BedBath"
			globalRepositoryToolsMock.returnCountryFromSession() >> "MX"
			
			Map<String,String> inCartPriceListIdValue = new HashMap<String,String>()
			inCartPriceListIdValue.put("MexInCartPriceList", "PL4567")
			
			testObj.getConfigValueByconfigType(*_) >> inCartPriceListIdValue
			
			priceListRepositoryMock.getItem("PL4567",BBBInternationalShippingConstants.PROPERTY_PRICELIST) >> repositoryItemMock
			 
			priceListManagerMock.getPrice(repositoryItemMock, "212121", "362263") >> priceRepositoryItemMock
			
			Double listPrice = new Double(32.22f)
			priceRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> listPrice
			
		when:
			def results = testObj.getIncartPrice("212121", "362263")
			
		then:
		results == 32.220001220703125
	}
	
	def "getIncartPrice when siteID is BedBathCanada" () {
		given:
			
			testObj = Spy()
			
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setPriceListRepository(priceListRepositoryMock)
			testObj.setPriceListManager(priceListManagerMock)
			
			testObj.getCurrentSiteId() >> "BedBathCanada"
			globalRepositoryToolsMock.returnCountryFromSession() >> "MX"
			
			Map<String,String> inCartPriceListIdValue = new HashMap<String,String>()
			inCartPriceListIdValue.put("BedBathCanadaInCartPriceList", "PL6789")
			
			testObj.getConfigValueByconfigType(*_) >> inCartPriceListIdValue
			
			priceListRepositoryMock.getItem("PL6789",BBBInternationalShippingConstants.PROPERTY_PRICELIST) >> repositoryItemMock
			 
			priceListManagerMock.getPrice(repositoryItemMock, "212121", "362263") >> priceRepositoryItemMock
			
			Double listPrice = new Double(32.22f)
			priceRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> listPrice
			
		when:
			def results = testObj.getIncartPrice("212121", "362263")
			
		then:
		results == 32.220001220703125
	}
	
	def "getIncartPrice when siteID is TBS_BedBathCanada" () {
		given:
			
			testObj = Spy()
			
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setPriceListRepository(priceListRepositoryMock)
			testObj.setPriceListManager(priceListManagerMock)
			
			testObj.getCurrentSiteId() >> "TBS_BedBathCanada"
			globalRepositoryToolsMock.returnCountryFromSession() >> "MX"
			
			Map<String,String> inCartPriceListIdValue = new HashMap<String,String>()
			inCartPriceListIdValue.put("BedBathCanadaInCartPriceList", "PL6789")
			
			testObj.getConfigValueByconfigType(*_) >> inCartPriceListIdValue
			
			priceListRepositoryMock.getItem("PL6789",BBBInternationalShippingConstants.PROPERTY_PRICELIST) >> repositoryItemMock
			 
			priceListManagerMock.getPrice(repositoryItemMock, "212121", "362263") >> priceRepositoryItemMock
			
			Double listPrice = new Double(32.22f)
			priceRepositoryItemMock.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME) >> listPrice
			
		when:
			def results = testObj.getIncartPrice("212121", "362263")
			
		then:
		results == 32.220001220703125
	}
	
	def "getIncartPrice when inCartPriceListId is empty" () {
		given:
			
			testObj = Spy()
			
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setPriceListRepository(priceListRepositoryMock)
			testObj.setPriceListManager(priceListManagerMock)
			
			testObj.getCurrentSiteId() >> "TBS_BedBathCanada"
			globalRepositoryToolsMock.returnCountryFromSession() >> "MX"
			
			Map<String,String> inCartPriceListIdValue = new HashMap<String,String>()
			inCartPriceListIdValue.put("BedBathCanadaInCartPriceList", "")
			
			testObj.getConfigValueByconfigType(*_) >> inCartPriceListIdValue
			
		when:
			def results = testObj.getIncartPrice("212121", "362263")
			
		then:
		results == 0.0
	}
	
	def "getIncartPrice when priceListRepository returns null" () {
		given:
			
			testObj = Spy()
			
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setPriceListRepository(priceListRepositoryMock)
			testObj.setPriceListManager(priceListManagerMock)
			
			testObj.getCurrentSiteId() >> "TBS_BedBathCanada"
			globalRepositoryToolsMock.returnCountryFromSession() >> "MX"
			
			Map<String,String> inCartPriceListIdValue = new HashMap<String,String>()
			inCartPriceListIdValue.put("BedBathCanadaInCartPriceList", "PL6789")
			
			testObj.getConfigValueByconfigType(*_) >> inCartPriceListIdValue
			
			priceListRepositoryMock.getItem("PL6789",BBBInternationalShippingConstants.PROPERTY_PRICELIST) >> null
			
		when:
			def results = testObj.getIncartPrice("212121", "362263")
			
		then:
		results == 0.0
	}
	
	def "getIncartPrice when PriceListManagerRepository returns null" () {
		given:
			
			testObj = Spy()
			
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setPriceListRepository(priceListRepositoryMock)
			testObj.setPriceListManager(priceListManagerMock)
			
			testObj.getCurrentSiteId() >> "TBS_BedBathCanada"
			globalRepositoryToolsMock.returnCountryFromSession() >> "MX"
			
			Map<String,String> inCartPriceListIdValue = new HashMap<String,String>()
			inCartPriceListIdValue.put("BedBathCanadaInCartPriceList", "PL6789")
			
			testObj.getConfigValueByconfigType(*_) >> inCartPriceListIdValue
			
			priceListRepositoryMock.getItem("PL6789",BBBInternationalShippingConstants.PROPERTY_PRICELIST) >> repositoryItemMock
			 
			priceListManagerMock.getPrice(repositoryItemMock, "212121", "362263") >> null
			
		when:
			def results = testObj.getIncartPrice("212121", "362263")
			
		then:
		results == 0.0
	}
	
	/* getIncartPrice - Test Cases ENDS */
	
	/* getDynamicPriceProductItem - Test Cases START */
	
	def "getDynamicPriceProductItem by passing product Id" (){
		given:
		
			dynamicPriceRepositoryMock.getItem("P2552",BBBCatalogConstants.PRODUCT_PRICE_ITEM) >> repositoryItemMock
		when:
			def RepositoryItem results = testObj.getDynamicPriceProductItem("P2552")
		then:
			results == repositoryItemMock
	}
	
	def "getDynamicPriceProductItem when RepositoryException occurs" (){
		given:
		
			dynamicPriceRepositoryMock.getItem("P2552",BBBCatalogConstants.PRODUCT_PRICE_ITEM) >> {throw new RepositoryException("Mock Repository Exception")}
		when:
			def RepositoryItem results = testObj.getDynamicPriceProductItem("P2552")
		then:
			results == null
	}
	
	/* getDynamicPriceProductItem - Test Cases ENDS */
	
	/* getSkuIncartFlag - Test Cases START */
	
	def "getSkuIncartFlag by passing parameters and enableDynamicPricing is false" (){
		given:
		
			testObj = Spy()
			
			testObj.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY, "false") >> "false"
		
		when:
			def results = testObj.getSkuIncartFlag("256332",false)
			
		then:
			results == false
	}
	
	def "getSkuIncartFlag by passing parameters and enableDynamicPricing is true" (){
		given:
		
			testObj = Spy()
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.getCurrentSiteId() >> "BedBathCanada"
			
			testObj.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY, "false") >> "true"
			skuCacheContainerMock.get("sku_" + "256332") >> null
			dynamicPriceRepositoryMock.getItem("256332", BBBCatalogConstants.SKU_PRICE_ITEM) >> repositoryItemMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE_SKU) >> "pricingLabelBABY"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE_SKU) >> "pricingLabelBBB"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE_SKU) >> "pricingLabelCA"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE_SKU) >> "pricingLabelMX"
			
			globalRepositoryToolsMock.returnCountryFromSession() >> "MX"
		
		when:
			def results = testObj.getSkuIncartFlag("256332",true)
			
		then:
			results == true
	}
	
	def "getSkuIncartFlag when skuCacheContainer is null and item gets null" (){
		given:
		
			testObj = Spy()
			testObj.setSkuCacheContainer(null)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.getCurrentSiteId() >> "BedBathCanada"
			
			testObj.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY, "false") >> "true"
			dynamicPriceRepositoryMock.getItem("256332", BBBCatalogConstants.SKU_PRICE_ITEM) >> null
			
		
		when:
			def results = testObj.getSkuIncartFlag("256332",true)
			
		then:
			results == false
	}
	
	def "getSkuIncartFlag when Repository Exception occurs" (){
		given:
		
			testObj = Spy()
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.getCurrentSiteId() >> "BedBathCanada"
			
			testObj.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY, "false") >> "true"
			skuCacheContainerMock.get("sku_" + "256332") >> null
			dynamicPriceRepositoryMock.getItem("256332", BBBCatalogConstants.SKU_PRICE_ITEM) >> {throw new RepositoryException("Mock Repository Exception")}
			
		
		when:
			def results = testObj.getSkuIncartFlag("256332",true)
			
		then:
			results == false
	}
	
	def "getSkuIncartFlag when enableDynamicPricingStr is null" (){
		given:
		
			testObj = Spy()
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			
			testObj.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY, "false") >> null
		
		when:
			def results = testObj.getSkuIncartFlag("256332",true)
			
		then:
			results == false
	}
	
	def "getSkuIncartFlag when returnCountryFromSession is null" (){
		given:
		
			testObj = Spy()
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.getCurrentSiteId() >> "BuyBuyBaby"
			
			testObj.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY, "false") >> "true"
			skuCacheContainerMock.get("sku_" + "256332") >> bbbDynamicPriceSkuVOMock
			dynamicPriceRepositoryMock.getItem("256332", BBBCatalogConstants.SKU_PRICE_ITEM) >> repositoryItemMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE_SKU) >> "pricingLabelBABY"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE_SKU) >> "pricingLabelBBB"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE_SKU) >> "pricingLabelCA"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE_SKU) >> "pricingLabelMX"
			
			globalRepositoryToolsMock.returnCountryFromSession() >> null
		
		when:
			def results = testObj.getSkuIncartFlag("256332",false)
			
		then:
			results == false
	}
	
	def "getSkuIncartFlag when fetchFromDB is false and skuDynamicPriceVo is null" (){
		given:
		
			testObj = Spy()
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.getCurrentSiteId() >> "BedBathUS"
			
			testObj.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY, "false") >> "true"
			skuCacheContainerMock.get("sku_" + "256332") >> null
			dynamicPriceRepositoryMock.getItem("256332", BBBCatalogConstants.SKU_PRICE_ITEM) >> repositoryItemMock
		
		when:
			def results = testObj.getSkuIncartFlag("256332",false)
			
		then:
			results == false
	}
	
	def "getSkuIncartFlag when country is MX" (){
		given:
		
			testObj = Spy()
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.getCurrentSiteId() >> "BedBathUS"
			
			testObj.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY, "false") >> "true"
			skuCacheContainerMock.get("sku_" + "256332") >> bbbDynamicPriceSkuVOMock
			dynamicPriceRepositoryMock.getItem("256332", BBBCatalogConstants.SKU_PRICE_ITEM) >> repositoryItemMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE_SKU) >> "pricingLabelBABY"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE_SKU) >> "pricingLabelBBB"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE_SKU) >> "pricingLabelCA"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE_SKU) >> "pricingLabelMX"
			
			globalRepositoryToolsMock.returnCountryFromSession() >> "MX"
		
		when:
			def results = testObj.getSkuIncartFlag("256332",false)
			
		then:
			results == false
	}
	
	def "getSkuIncartFlag when siteId is BedBathUS" (){
		given:
		
			testObj = Spy()
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.getCurrentSiteId() >> "BedBathUS"
			
			testObj.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY, "false") >> "true"
			skuCacheContainerMock.get("sku_" + "256332") >> null
			dynamicPriceRepositoryMock.getItem("256332", BBBCatalogConstants.SKU_PRICE_ITEM) >> repositoryItemMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE_SKU) >> "pricingLabelBABY"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE_SKU) >> "pricingLabelBBB"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE_SKU) >> "pricingLabelCA"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE_SKU) >> "pricingLabelMX"
			
			globalRepositoryToolsMock.returnCountryFromSession() >> "IND"
		
		when:
			def results = testObj.getSkuIncartFlag("256332",true)
			
		then:
			results == true
	}
	
	def "getSkuIncartFlag when siteId and country is different" (){
		given:
		
			testObj = Spy()
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.getCurrentSiteId() >> "BathCanada"
			
			testObj.getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_INCART_AND_DYNAMIC_PRICING_KEY, "false") >> "true"
			skuCacheContainerMock.get("sku_" + "256332") >> null
			dynamicPriceRepositoryMock.getItem("256332", BBBCatalogConstants.SKU_PRICE_ITEM) >> repositoryItemMock
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE_SKU) >> "pricingLabelBABY"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE_SKU) >> "pricingLabelBBB"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE_SKU) >> "pricingLabelCA"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE_SKU) >> "pricingLabelMX"
			
			globalRepositoryToolsMock.returnCountryFromSession() >> "IND"
		
		when:
			def results = testObj.getSkuIncartFlag("256332",true)
			
		then:
			results == false
	}

	
	
	/* getSkuIncartFlag - Test Cases ENDS */
	
	/* populateDynamicSKUDeatilInVO - Test Cases START */
	
	def "populateDynamicSKUDeatilInVO when passing parameters" (){
		given:
		
			testObj = Spy()
			
			
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522") 
			
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			
			skuCacheContainerMock.get("sku_" + "2522") >> bbbDynamicPriceSkuVOMock
			
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "US", "BedBathCanada", false)
			
		then:
			results == null
	}
	
	def "populateDynamicSKUDeatilInVO when passing fromcart is true" (){
		given:
		
			testObj = Spy()
			
			
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522")
			
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			testObj.enableRepoCallforDynPrice() >> true
			dynamicPriceRepositoryMock.getItem("2522", BBBCatalogConstants.SKU_PRICE_ITEM) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE_SKU) >> "pricingLabelBABY"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE_SKU) >> "pricingLabelBBB"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE_SKU) >> "pricingLabelCA"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE_SKU) >> "pricingLabelMX"
			
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "MX", "BedBathUS", true)
			
		then:
			results == null
	}
	
	def "populateDynamicSKUDeatilInVO when Repsoitory Exception occurs" (){
		given:
		
			testObj = Spy()
			
			
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522")
			
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			testObj.enableRepoCallforDynPrice() >> true
			dynamicPriceRepositoryMock.getItem("2522", BBBCatalogConstants.SKU_PRICE_ITEM) >> {throw new RepositoryException("Mock Repository Exception")}
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BABY_PRICING_LABEL_CODE_SKU) >> "pricingLabelBABY"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_PRICING_LABEL_CODE_SKU) >> "pricingLabelBBB"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CA_PRICING_LABEL_CODE_SKU) >> "pricingLabelCA"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_IN_CART_FLAG_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MX_PRICING_LABEL_CODE_SKU) >> "pricingLabelMX"
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "MX", "BedBathUS", true)
			
		then:
			results == null
	}
	
	def "populateDynamicSKUDeatilInVO when passing country as IND" (){
		given:
		
			testObj = Spy()
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522")
			skuDetailVOMock.getDynamicSKUPriceVO() >> bbbDynamicPriceSkuVOMock
			skuCacheContainerMock.get("sku_" + "2522") >> bbbDynamicPriceSkuVOMock

			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "IND", "BuyBuyBaby", false)
			
		then:
			results == null
	}
	
	def "populateDynamicSKUDeatilInVO when passing siteId as TBS_BuyBuyBaby and country as IND" (){
		given:
		
			testObj = Spy()
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522")
			skuDetailVOMock.getDynamicSKUPriceVO() >> bbbDynamicPriceSkuVOMock
			
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			
			skuCacheContainerMock.get("sku_" + "2522") >> bbbDynamicPriceSkuVOMock
			
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "IND", "TBS_BuyBuyBaby", false)
			
		then:
			results == null
	}
	
	def "populateDynamicSKUDeatilInVO when passing siteId as BedBathUS" (){
		given:
		
			testObj = Spy()
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522")
			skuDetailVOMock.getDynamicSKUPriceVO() >> bbbDynamicPriceSkuVOMock
			
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			
			skuCacheContainerMock.get("sku_" + "2522") >> bbbDynamicPriceSkuVOMock
			
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "IND", "BedBathUS", false)
			
		then:
			results == null
	}
	
	def "populateDynamicSKUDeatilInVO when passing siteId as TBS_BedBathUS" (){
		given:
		
			testObj = Spy()
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522")
			skuDetailVOMock.getDynamicSKUPriceVO() >> bbbDynamicPriceSkuVOMock
			
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			
			skuCacheContainerMock.get("sku_" + "2522") >> bbbDynamicPriceSkuVOMock
			
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "IND", "TBS_BedBathUS", false)
			
		then:
			results == null
	}
	
	def "populateDynamicSKUDeatilInVO when passing siteId as TBS_BedBath" (){
		given:
		
			testObj = Spy()
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522")
			skuDetailVOMock.getDynamicSKUPriceVO() >> bbbDynamicPriceSkuVOMock
			
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			
			skuCacheContainerMock.get("sku_" + "2522") >> bbbDynamicPriceSkuVOMock
			
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "IND", "TBS_BedBath", false)
			
		then:
			results == null
	}
	
	def "populateDynamicSKUDeatilInVO when passing skuDynamicPriceVo as Null" (){
		given:
		
			testObj = Spy()
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522")
			testObj.enableRepoCallforDynPrice() >> false
			
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			
			skuCacheContainerMock.get("sku_" + "2522") >> null
			
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "IND", "TBS_BedBath", true)
			
		then:
			results == null
	}
	
	def "populateDynamicSKUDeatilInVO when passing dynamicSkuItem as Null" (){
		given:
		
			testObj = Spy()
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522")
			testObj.enableRepoCallforDynPrice() >> true
			
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			dynamicPriceRepositoryMock.getItem("2522", BBBCatalogConstants.SKU_PRICE_ITEM) >> null
			
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "IND", "TBS_BedBath", true)
			
		then:
			results == null
	}
	
	def "populateDynamicSKUDeatilInVO when passing parameters country as MX" (){
		given:
		
			testObj = Spy()
			
			
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522")
			
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			testObj.enableRepoCallforDynPrice() >> false
			
			skuCacheContainerMock.get("sku_" + "2522") >> bbbDynamicPriceSkuVOMock
			
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			bbbDynamicPriceSkuVOMock.getMxPricingLabelCode() >> null
			
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "MX", "BedBath", true)
			
		then:
			results == null
	}
	
	def "populateDynamicSKUDeatilInVO when passing parameters siteId as BuyBuyBaby" (){
		given:
		
			testObj = Spy()
			
			
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522")
			
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			testObj.enableRepoCallforDynPrice() >> false
			
			skuCacheContainerMock.get("sku_" + "2522") >> bbbDynamicPriceSkuVOMock
			
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			bbbDynamicPriceSkuVOMock.getMxPricingLabelCode() >> null
			bbbDynamicPriceSkuVOMock.getBabyPricingLabelCode() >> "5232"
			
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "US", "BuyBuyBaby", true)
			
		then:
			results == null
	}
	
	def "populateDynamicSKUDeatilInVO when passing parameters siteId as TBS_BuyBuyBaby" (){
		given:
		
			testObj = Spy()
			
			
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522")
			
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			testObj.enableRepoCallforDynPrice() >> false
			
			skuCacheContainerMock.get("sku_" + "2522") >> bbbDynamicPriceSkuVOMock
			
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			bbbDynamicPriceSkuVOMock.getMxPricingLabelCode() >> null
			bbbDynamicPriceSkuVOMock.getBabyPricingLabelCode() >> null
			
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "US", "TBS_BuyBuyBaby", true)
			
		then:
			results == null
	}
	
	def "populateDynamicSKUDeatilInVO when passing parameters siteId as invalid" (){
		given:
		
			testObj = Spy()
			
			
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522")
			
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			testObj.enableRepoCallforDynPrice() >> false
			
			skuCacheContainerMock.get("sku_" + "2522") >> bbbDynamicPriceSkuVOMock
			
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			bbbDynamicPriceSkuVOMock.getMxPricingLabelCode() >> null
			bbbDynamicPriceSkuVOMock.getBbbPricingLabelCode() >> "2332"
			
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "US", "TBS_BuyBu", true)
			
		then:
			results == null
	}
	
	def "populateDynamicSKUDeatilInVO when fromcart parameter is false" (){
		given:
		
			testObj = Spy()
			skuDetailVOMock.getSkuId() >> "2522"
			skuDetailVOMock.setSkuId("2522")
			testObj.enableRepoCallforDynPrice() >> true
			
			testObj.setSkuCacheContainer(skuCacheContainerMock)
			testObj.setDynamicPriceRepository(dynamicPriceRepositoryMock)
			
			skuCacheContainerMock.get("sku_" + "2522") >> null
			
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> "1234"
			
			
		when:
		
			def results = testObj.populateDynamicSKUDeatilInVO(skuDetailVOMock, "IND", "TBS_BedBath", false)
			
		then:
			results == null
	}
	
	/* populateDynamicSKUDeatilInVO - Test Cases ENDS */
	
	/* updateSkuPriceFlags - Test Cases START */
	
	def "updateSkuPriceFlags when CaPricingLabelCode is null" (){
		given:
			bbbDynamicPriceSkuVOMock.getCaPricingLabelCode() >> null
			
		when:
		
			def results = testObj.updateSkuPriceFlags(skuDetailVOMock, bbbDynamicPriceSkuVOMock, "US", "TBS_BedBathCanada")
			
		then:
			results == null
	}
	
	def "updateSkuPriceFlags when BbbPricingLabelCode is null" (){
		given:
			bbbDynamicPriceSkuVOMock.getBbbPricingLabelCode() >> null
			
		when:
		
			def results = testObj.updateSkuPriceFlags(skuDetailVOMock, bbbDynamicPriceSkuVOMock, "IND", "BedBath")
			
		then:
			results == null
	}
	
	/* updateSkuPriceFlags - Test Cases ENDS */


	
	
	

}
