package com.bbb.commerce.giftregistry.droplet

import java.util.Map;

import atg.commerce.pricing.priceLists.PriceListException
import atg.commerce.pricing.priceLists.PriceListManager
import atg.multisite.Site
import atg.multisite.SiteContext
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile

import com.bbb.cms.PromoBoxVO
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.RegistryCategoryMapVO
import com.bbb.commerce.catalog.vo.RegistryTypeVO
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.RegistryItemVO
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO
import com.bbb.commerce.giftregistry.vo.RegistryResVO
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.profile.session.BBBSessionBean

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec
/**
 * 
 * @author kmagud
 *
 */
class PrimeGiftRegistryInfoDropletSpecification extends BBBExtendedSpec {
	
	PrimeGiftRegistryInfoDroplet testObj
	GiftRegistryManager giftRegistryManagerMock = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	PriceListManager priceListManagerMock = Mock()
	SiteContext siteContextMock = Mock()
	BBBSessionBean sessionBeanMock = new BBBSessionBean()
	Profile profileMock = Mock()
	Site siteMock = Mock()
	RepositoryItem repositoryItemMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	public static final ParameterName REGISTRY_ID = ParameterName.getParameterName(BBBGiftRegistryConstants.REGISTRY_ID)
	public static final ParameterName SORT_SEQ = ParameterName.getParameterName(BBBGiftRegistryConstants.SORT_SEQ)
	public static final ParameterName VIEW = ParameterName.getParameterName(BBBGiftRegistryConstants.VIEW)
	public static final ParameterName REG_EVENT_TYPE_CODE = ParameterName.getParameterName(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE)
	public static final ParameterName START_INDEX = ParameterName.getParameterName(BBBGiftRegistryConstants.START_INDEX)
	public static final ParameterName BULK_SIZE = ParameterName.getParameterName(BBBGiftRegistryConstants.BULK_SIZE)
	public static final ParameterName IS_GIFT_GIVER = ParameterName.getParameterName(BBBGiftRegistryConstants.IS_GIFT_GIVER)
	public static final ParameterName IS_AVAIL_WEBPUR = ParameterName.getParameterName(BBBGiftRegistryConstants.IS_AVAIL_WEBPUR)
	public static final ParameterName SITE_ID_PARAM= ParameterName.getParameterName("siteId")
	public static final ParameterName PROFILE = ParameterName.getParameterName("profile")
	
	
	def setup(){
		testObj = new PrimeGiftRegistryInfoDroplet(giftRegistryManager:giftRegistryManagerMock,catalogTools:catalogToolsMock,
			priceListManager:priceListManagerMock,siteContext:siteContextMock,topRegMaxCount:"TopRegMaxCount",profilePriceListPropertyName:"priceList")
		
		requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBeanMock
		requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
	}
	
	/////////////////////////////////TCs for service starts////////////////////////////////////////
	//Signature : public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) ////
	
	def"service method. This TC is the Happy flow of service method"(){
		given:
			sessionBeanMock.setPrimeRegistryCompleted(FALSE)
			requestMock.resolveName("/atg/multisite/SiteContext") >> siteContextMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO(registryId:"222222",eventCode:"code")
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathUS") >> [registrySkinnyVOMock]
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code",registryTypeId:"141414")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"registryIn")
			1 * giftRegistryManagerMock.fetchRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:25522l)
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:35333l,jdaRetailPrice:25d,personalisedCode:"personalisedCode",refNum:"refNum",personlisedPrice:28d)
			RegistryItemVO registryItemVOMock2 = new RegistryItemVO(sku:45444l,jdaRetailPrice:0d,personalisedCode:null)
			RegistryItemVO registryItemVOMock3 = new RegistryItemVO(sku:5555l)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:[registryItemVOMock,registryItemVOMock1,registryItemVOMock2],
				mSkuRegItemVOMap:["sku56655":registryItemVOMock,"sku56655_12":registryItemVOMock3],totEntries:5)
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("222222", false, BBBCoreConstants.VIEW_ALL) >> registryItemsListVOMock
			RegistryCategoryMapVO registryCategoryMapVOMock = new RegistryCategoryMapVO()
			3 * catalogToolsMock.getCategoryForRegistry("141414") >> ["1":registryCategoryMapVOMock]
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("222222", "BedBathUS") >> registryResVOMock 
			
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			2 * catalogToolsMock.getPromoBoxForRegistry("141414") >> promoBoxVOMock
			
			//fliterNotAvliableItem Private Method Coverage
			1 * catalogToolsMock.getParentProductForSku("45444", true)
			1 * catalogToolsMock.getParentProductForSku("35333", true)
			1 * catalogToolsMock.getParentProductForSku("25522", true) >> {throw new Exception("Mock for Exception")}
			
			//getlistRegistryItemVO Private Method Coverage
			2 * catalogToolsMock.getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, testObj.getTopRegMaxCount()) >>> [["2"],[""]]
			
			//checkPrice Private Method Coverage
			3 * catalogToolsMock.getParentProductForSku("35333", true) >>> ["prod12345","prod12345",null]
			3 * catalogToolsMock.getParentProductForSku("45444", true) >>> ["prod2345","prod12345",null]
			1 * catalogToolsMock.getSalePrice("prod2345", "45444") >> 15d
			1 * catalogToolsMock.getListPrice("prod2345", "45444") >> 23d
			
			//withDefaultCategory Private Method Coverage
			requestMock.getParameter(SITE_ID_PARAM) >> "BedBathUS"
			2 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.ENABLE_LTL_REG_FOR_SITE) >> ["true"]
			SKUDetailVO sKUDetailVOMock = new SKUDetailVO(skuId:"sku56655")
			SKUDetailVO sKUDetailVOMock1 = new SKUDetailVO(skuId:"sku212321")
			1 * catalogToolsMock.sortSkubyRegistry(["35333", "45444"], null, "141414", "BedBathUS", BBBCatalogConstants.CATEGORY_SORT_TYPE, null) >> ["firstSku":[sKUDetailVOMock,sKUDetailVOMock1],"secondSku":[]] 
			
			//skuDetailsVO Private Method Coverage
			1 * catalogToolsMock.isSKUBelowLine("BedBathUS", "sku56655") >> FALSE
			1 * catalogToolsMock.isSKUBelowLine("BedBathUS", "sku212321") >> TRUE
			
			//withDefaultPrice Private Method Coverage
			sessionBeanMock.getValues().put("defaultUserCountryCode","NJ")
			1 * catalogToolsMock.getPriceRanges("141414", "NJ") >> ["10","18"]
			SKUDetailVO sKUDetailVOMock2 = new SKUDetailVO(skuId:"sku66666")
			SKUDetailVO sKUDetailVOMock3 = new SKUDetailVO(skuId:"sku77777")
			1 * catalogToolsMock.sortSkubyRegistry(null, [:], "141414", "BedBathUS", null, "NJ") >> ["thirdSku":[sKUDetailVOMock2,sKUDetailVOMock3],"forthSku":[]]
			1 * catalogToolsMock.getParentProductForSku("sku66666") >> "prod8878"
			2 * priceListManagerMock.getPriceList(profileMock, "priceList") >> repositoryItemMock
			1 * priceListManagerMock.getPrice(repositoryItemMock, "prod8878", "sku66666") >> repositoryItemMock
			repositoryItemMock.getPropertyValue("listPrice") >> "23"
			1 * catalogToolsMock.isSKUBelowLine("BedBathUS", "sku66666") >> TRUE
			
			1 * catalogToolsMock.getParentProductForSku("sku77777") >> "prod777997"
			1 * priceListManagerMock.getPrice(repositoryItemMock, "prod777997", "sku77777") >> repositoryItemMock
			1 * catalogToolsMock.isSKUBelowLine("BedBathUS", "sku77777") >> FALSE
			
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.getValues().get("222222_REG_SUMMARY").equals(registryResVOMock)
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
			2 * requestMock.setParameter(BBBGiftRegistryConstants.PROMOBOX, promoBoxVOMock)
			2 * requestMock.setParameter(BBBGiftRegistryConstants.TOTAL_ENTRIES_COUNT, 5)
			testObj.getCertonaListMaxCount().equals(2)
			1 * requestMock.setParameter("skuList", "35333;45444")
			1 * requestMock.setParameter("certonaSkuList", "35333")
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REGISTRY_CATEGORY_MAP_VO, ["1":registryCategoryMapVOMock])
			2 * requestMock.setParameter("emptyOutOfStockListFlag", "false")
			1 * requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST, ["10","18"])
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.getAttribute('sessionBean')
	}
	
	def"service method. This TC is when isPrimeRegistryCompleted is true"(){
		given:
			sessionBeanMock.setPrimeRegistryCompleted(TRUE)
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
	}
	
	def"service method. This TC is when registryItemsListVO is null"(){
		given:
			sessionBeanMock.setPrimeRegistryCompleted(FALSE)
			requestMock.resolveName("/atg/multisite/SiteContext") >> siteContextMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO(registryId:"222222",eventCode:"code")
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathUS") >> [registrySkinnyVOMock]
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code",registryTypeId:"141414")
			1 * giftRegistryManagerMock.fetchRegistryTypes("BedBathUS") >> [registryTypeVOMock]
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("222222", false, BBBCoreConstants.VIEW_ALL) >> null
			RegistryCategoryMapVO registryCategoryMapVOMock = new RegistryCategoryMapVO()
			1 * catalogToolsMock.getCategoryForRegistry("141414") >> ["1":registryCategoryMapVOMock]
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("222222", "BedBathUS") >> registryResVOMock
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.getValues().get("222222_REG_SUMMARY").equals(registryResVOMock)
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}

	def"service method. This TC is when priceRangeList is null in withDefaultPrice Private method"(){
		given:
			sessionBeanMock.setPrimeRegistryCompleted(FALSE)
			requestMock.resolveName("/atg/multisite/SiteContext") >> siteContextMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO(registryId:"222222",eventCode:"code")
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathUS") >> [registrySkinnyVOMock]
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code",registryTypeId:"141414")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"registryIn")
			1 * giftRegistryManagerMock.fetchRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:25522l,personalisedCode:"",jdaRetailPrice:0d)
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:5555l,personlisedPrice:12d)
			RegistryItemVO registryItemVOMock2 = new RegistryItemVO(sku:3333l,personlisedPrice:12d)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:[registryItemVOMock],
				mSkuRegItemVOMap:["sku56655_12":registryItemVOMock1,"sku8585_22":registryItemVOMock2],totEntries:5)
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("222222", false, BBBCoreConstants.VIEW_ALL) >> registryItemsListVOMock
			RegistryCategoryMapVO registryCategoryMapVOMock = new RegistryCategoryMapVO()
			3 * catalogToolsMock.getCategoryForRegistry("141414") >> ["1":registryCategoryMapVOMock]
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("222222", "BedBathUS") >> registryResVOMock
			
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			2 * catalogToolsMock.getPromoBoxForRegistry("141414") >> promoBoxVOMock
			
			//fliterNotAvliableItem Private Method Coverage
			1 * catalogToolsMock.getParentProductForSku("25522", true)
			
			//getlistRegistryItemVO Private Method Coverage
			2 * catalogToolsMock.getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, testObj.getTopRegMaxCount()) >>> [["2"],[""]]
			
			//checkPrice Private Method Coverage
			3 * catalogToolsMock.getParentProductForSku("25522", true) >>> ["prod13456","prod13456",null]
			1 * catalogToolsMock.getSalePrice("prod13456", "25522") >> 0.0d
			1 * catalogToolsMock.getListPrice("prod13456", "25522") >> 23d
			
			//withDefaultCategory Private Method Coverage
			requestMock.getParameter(SITE_ID_PARAM) >> null
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			2 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.ENABLE_LTL_REG_FOR_SITE) >> [""]
			SKUDetailVO sKUDetailVOMock = new SKUDetailVO(skuId:"sku566554")
			1 * catalogToolsMock.sortSkubyRegistry(["25522"], null, "141414", "BedBathUS", BBBCatalogConstants.CATEGORY_SORT_TYPE, null) >> ["1":[sKUDetailVOMock]]
			
			//skuDetailsVO Private Method Coverage
			1 * catalogToolsMock.isSKUBelowLine("BedBathUS", "sku56655") >> FALSE
			1 * catalogToolsMock.isSKUBelowLine('BedBathUS', 'sku566554') >> FALSE
			
			//withDefaultPrice Private Method Coverage
			sessionBeanMock.getValues().put("defaultUserCountryCode","NJ")
			1 * catalogToolsMock.getPriceRanges("141414", "NJ") >> null
			SKUDetailVO sKUDetailVOMock2 = new SKUDetailVO(skuId:"sku56655")
			SKUDetailVO sKUDetailVOMock3 = new SKUDetailVO(skuId:"sku77777")
			SKUDetailVO sKUDetailVOMock4 = new SKUDetailVO(skuId:"sku8585")
			1 * catalogToolsMock.sortSkubyRegistry(null, [:], "141414", "BedBathUS", null, "NJ") >> ['$1_+$':[sKUDetailVOMock2,sKUDetailVOMock3],'10$11-52$72':[sKUDetailVOMock4]]
			
			1 * catalogToolsMock.getParentProductForSku("sku56655") >> "prod8878"
			3 * priceListManagerMock.getPriceList(profileMock, "priceList") >> repositoryItemMock
			1 * priceListManagerMock.getPrice(repositoryItemMock, "prod8878", "sku56655") >> repositoryItemMock
			repositoryItemMock.getPropertyValue("listPrice") >> "23"
			
			1 * catalogToolsMock.getParentProductForSku("sku77777") >> "prod777997"
			1 * priceListManagerMock.getPrice(repositoryItemMock, "prod777997", "sku77777") >> repositoryItemMock
			1 * catalogToolsMock.isSKUBelowLine("BedBathUS", "sku77777") >> TRUE
			
			1 * catalogToolsMock.getParentProductForSku("sku8585") >> "prod882297"
			1 * priceListManagerMock.getPrice(repositoryItemMock, "prod882297", "sku8585") >> repositoryItemMock
			1 * catalogToolsMock.isSKUBelowLine("BedBathUS", "sku8585") >> TRUE
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.getValues().get("222222_REG_SUMMARY").equals(registryResVOMock)
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
			2 * requestMock.setParameter(BBBGiftRegistryConstants.PROMOBOX, promoBoxVOMock)
			2 * requestMock.setParameter(BBBGiftRegistryConstants.TOTAL_ENTRIES_COUNT, 5)
			testObj.getCertonaListMaxCount().equals(2)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REGISTRY_CATEGORY_MAP_VO, ["1":registryCategoryMapVOMock])
			1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
			1 * requestMock.setParameter('errorMsg', 'err_regsearch_biz_exception')
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.getAttribute('sessionBean')
			1 * requestMock.setParameter('priceRangeList', null)
			1 * requestMock.setParameter('certonaSkuList', '25522')
			1 * requestMock.setParameter('emptyOutOfStockListFlag', 'true')
			1 * requestMock.setParameter('skuList', '25522')
	}
	
	def"service method. This TC is when country is US in withDefaultPrice Private method"(){
		given:
			sessionBeanMock.setPrimeRegistryCompleted(FALSE)
			requestMock.resolveName("/atg/multisite/SiteContext") >> siteContextMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO(registryId:"222222",eventCode:"code")
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathUS") >> [registrySkinnyVOMock]
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code",registryTypeId:"141414")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"registryIn")
			1 * giftRegistryManagerMock.fetchRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:25522l,personalisedCode:"",jdaRetailPrice:0d)
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:5555l,personlisedPrice:12d)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:[registryItemVOMock],
				mSkuRegItemVOMap:["sku56655_12":registryItemVOMock1],totEntries:5)
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("222222", false, BBBCoreConstants.VIEW_ALL) >> registryItemsListVOMock
			RegistryCategoryMapVO registryCategoryMapVOMock = new RegistryCategoryMapVO()
			3 * catalogToolsMock.getCategoryForRegistry("141414") >> ["1":registryCategoryMapVOMock]
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("222222", "BedBathUS") >> registryResVOMock
			
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			2 * catalogToolsMock.getPromoBoxForRegistry("141414") >> promoBoxVOMock
			
			//fliterNotAvliableItem Private Method Coverage
			1 * catalogToolsMock.getParentProductForSku("25522", true)
			
			//getlistRegistryItemVO Private Method Coverage
			2 * catalogToolsMock.getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, testObj.getTopRegMaxCount()) >>> [["2"],[""]]
			
			//checkPrice Private Method Coverage
			3 * catalogToolsMock.getParentProductForSku("25522", true) >>> ["prod13456","prod13456",null]
			1 * catalogToolsMock.getSalePrice("prod13456", "25522") >> 0.0d
			1 * catalogToolsMock.getListPrice("prod13456", "25522") >> 23d
			
			//withDefaultCategory Private Method Coverage
			requestMock.getParameter(SITE_ID_PARAM) >> null
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			2 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.ENABLE_LTL_REG_FOR_SITE) >> [""]
			SKUDetailVO sKUDetailVOMock = new SKUDetailVO(skuId:"sku566554")
			1 * catalogToolsMock.sortSkubyRegistry(["25522"], null, "141414", "BedBathUS", BBBCatalogConstants.CATEGORY_SORT_TYPE, null) >> ["1":[sKUDetailVOMock]]
			
			//skuDetailsVO Private Method Coverage
			catalogToolsMock.isSKUBelowLine("BedBathUS", "sku56655") >> FALSE
			1 * catalogToolsMock.isSKUBelowLine('BedBathUS', 'sku566554') >> FALSE
			
			//withDefaultPrice Private Method Coverage
			sessionBeanMock.getValues().put("defaultUserCountryCode","US")
			1 * catalogToolsMock.getPriceRanges("141414", "US") >> ['70$158-52$72',"100-250"]
			SKUDetailVO sKUDetailVOMock2 = new SKUDetailVO(skuId:"sku56655")
			1 * catalogToolsMock.sortSkubyRegistry(null, [:], "141414", "BedBathUS", null, "US") >> ['$20_+$':[sKUDetailVOMock2],
				'70$158-52$72':[sKUDetailVOMock2],'10$11-52$10':[sKUDetailVOMock2],'10$1152$72':[sKUDetailVOMock2]]
			
			3 * catalogToolsMock.getParentProductForSku("sku56655") >> "prod8878"
			priceListManagerMock.getPriceList(profileMock, "priceList") >> repositoryItemMock
			3 * priceListManagerMock.getPrice(repositoryItemMock, "prod8878", "sku56655") >> repositoryItemMock
			3 * repositoryItemMock.getPropertyValue("listPrice") >> "23"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.getValues().get("222222_REG_SUMMARY").equals(registryResVOMock)
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
			testObj.getCertonaListMaxCount().equals(2)
			1 * requestMock.serviceLocalParameter('error', requestMock, responseMock)
			2 * requestMock.setParameter(BBBGiftRegistryConstants.PROMOBOX, promoBoxVOMock)
			2 * requestMock.setParameter(BBBGiftRegistryConstants.TOTAL_ENTRIES_COUNT, 5)
			1 * requestMock.setParameter('emptyOutOfStockListFlag', 'true')
			1 * requestMock.setParameter('priceRangeList', ['70$158-52$72', '100-250'])
			1 * requestMock.setParameter('certonaSkuList', '25522')
			1 * requestMock.setParameter('categoryVOMap', ['1':registryCategoryMapVOMock])
			1 * requestMock.setParameter('skuList', '25522')
			1 * requestMock.setParameter('errorMsg', 'err_regsearch_biz_exception')
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.getAttribute('sessionBean')
		}
	
	def"service method. This TC is when country is MX in withDefaultPrice Private method"(){
		given:
			sessionBeanMock.setPrimeRegistryCompleted(FALSE)
			requestMock.resolveName("/atg/multisite/SiteContext") >> siteContextMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO(registryId:"222222",eventCode:"code")
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathUS") >> [registrySkinnyVOMock]
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code",registryTypeId:"141414")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"registryIn")
			1 * giftRegistryManagerMock.fetchRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:25522l,personalisedCode:"",jdaRetailPrice:0d)
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:5555l,personlisedPrice:12d)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:[registryItemVOMock],
				mSkuRegItemVOMap:["sku56655":registryItemVOMock1],totEntries:5)
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("222222", false, BBBCoreConstants.VIEW_ALL) >> registryItemsListVOMock
			RegistryCategoryMapVO registryCategoryMapVOMock = new RegistryCategoryMapVO()
			3 * catalogToolsMock.getCategoryForRegistry("141414") >> ["1":registryCategoryMapVOMock]
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("222222", "BedBathUS") >> registryResVOMock
			
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			2 * catalogToolsMock.getPromoBoxForRegistry("141414") >> promoBoxVOMock
			
			//fliterNotAvliableItem Private Method Coverage
			1 * catalogToolsMock.getParentProductForSku("25522", true)
			
			//getlistRegistryItemVO Private Method Coverage
			2 * catalogToolsMock.getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, testObj.getTopRegMaxCount()) >>> [["2"],[""]]
			
			//checkPrice Private Method Coverage
			3 * catalogToolsMock.getParentProductForSku("25522", true) >>> ["prod13456","prod13456",null]
			1 * catalogToolsMock.getSalePrice("prod13456", "25522") >> 0.0d
			1 * catalogToolsMock.getListPrice("prod13456", "25522") >> 23d
			
			//withDefaultCategory Private Method Coverage
			requestMock.getParameter(SITE_ID_PARAM) >> null
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			2 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.ENABLE_LTL_REG_FOR_SITE) >> [""]
			SKUDetailVO sKUDetailVOMock = new SKUDetailVO(skuId:"sku566554")
			1 * catalogToolsMock.sortSkubyRegistry(["25522"], null, "141414", "BedBathUS", BBBCatalogConstants.CATEGORY_SORT_TYPE, null) >> ["1":[sKUDetailVOMock]]
			
			//skuDetailsVO Private Method Coverage
			catalogToolsMock.isSKUBelowLine("BedBathUS", "sku56655") >> FALSE
			1 * catalogToolsMock.isSKUBelowLine('BedBathUS', 'sku566554') >> FALSE
			
			//withDefaultPrice Private Method Coverage
			sessionBeanMock.getValues().put("defaultUserCountryCode","MX")
			1 * catalogToolsMock.getPriceRanges("141414", "MX") >> ['70$158-52$72',"100-250"]
			SKUDetailVO sKUDetailVOMock2 = new SKUDetailVO(skuId:"sku56655")
			1 * catalogToolsMock.sortSkubyRegistry(null, [:], "141414", "BedBathUS", null, "MX") >> ['70158-5272':[sKUDetailVOMock2]]
			
			1 * catalogToolsMock.getParentProductForSku("sku56655") >> "prod8878"
			priceListManagerMock.getPriceList(profileMock, "priceList") >> repositoryItemMock
			1 * priceListManagerMock.getPrice(repositoryItemMock, "prod8878", "sku56655") >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue("listPrice") >> "23"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.getValues().get("222222_REG_SUMMARY").equals(registryResVOMock)
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
			1 * requestMock.getAttribute('sessionBean')
			2 * requestMock.setParameter('emptyOutOfStockListFlag', 'true')
			1 * requestMock.setParameter('priceRangeList', ['70$158-52$72', '100-250'])
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1 * requestMock.setParameter('categoryVOMap', ['1':registryCategoryMapVOMock])
			1 * requestMock.setParameter('certonaSkuList', '25522')
			2 * requestMock.setParameter('promoBox',  promoBoxVOMock)
			1 * requestMock.setParameter('skuList', '25522')
			2 * requestMock.setParameter('totEntries', 5)
			
	}
	
	def"service method. This TC is when country is empty in withDefaultPrice Private method"(){
		given:
			sessionBeanMock.setPrimeRegistryCompleted(FALSE)
			requestMock.resolveName("/atg/multisite/SiteContext") >> siteContextMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO(registryId:"222222",eventCode:"code")
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathUS") >> [registrySkinnyVOMock]
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code",registryTypeId:"141414")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"registryIn")
			1 * giftRegistryManagerMock.fetchRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:25522l,personalisedCode:"",jdaRetailPrice:0d)
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:5555l,personlisedPrice:12d)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:[registryItemVOMock],
				mSkuRegItemVOMap:["sku56655":registryItemVOMock1],totEntries:5)
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("222222", false, BBBCoreConstants.VIEW_ALL) >> registryItemsListVOMock
			RegistryCategoryMapVO registryCategoryMapVOMock = new RegistryCategoryMapVO()
			3 * catalogToolsMock.getCategoryForRegistry("141414") >> ["1":registryCategoryMapVOMock]
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("222222", "BedBathUS") >> registryResVOMock
			
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			2 * catalogToolsMock.getPromoBoxForRegistry("141414") >> promoBoxVOMock
			
			//fliterNotAvliableItem Private Method Coverage
			1 * catalogToolsMock.getParentProductForSku("25522", true)
			
			//getlistRegistryItemVO Private Method Coverage
			2 * catalogToolsMock.getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, testObj.getTopRegMaxCount()) >>> [["2"],[""]]
			
			//checkPrice Private Method Coverage
			3 * catalogToolsMock.getParentProductForSku("25522", true) >>> ["prod13456","prod13456",null]
			1 * catalogToolsMock.getSalePrice("prod13456", "25522") >> 0.0d
			1 * catalogToolsMock.getListPrice("prod13456", "25522") >> 23d
			
			//withDefaultCategory Private Method Coverage
			requestMock.getParameter(SITE_ID_PARAM) >> null
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			2 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.ENABLE_LTL_REG_FOR_SITE) >> [""]
			SKUDetailVO sKUDetailVOMock = new SKUDetailVO(skuId:"sku566554")
			1 * catalogToolsMock.sortSkubyRegistry(["25522"], null, "141414", "BedBathUS", BBBCatalogConstants.CATEGORY_SORT_TYPE, null) >> ["1":[sKUDetailVOMock]]
			
			//skuDetailsVO Private Method Coverage
			catalogToolsMock.isSKUBelowLine("BedBathUS", "sku56655") >> FALSE
			1 * catalogToolsMock.isSKUBelowLine('BedBathUS', 'sku566554') >> FALSE
			
			//withDefaultPrice Private Method Coverage
			sessionBeanMock.getValues().put("defaultUserCountryCode","")
			1 * catalogToolsMock.getPriceRanges("141414", "") >> ['70$158-52$72',"100-250"]
			SKUDetailVO sKUDetailVOMock2 = new SKUDetailVO(skuId:"sku56655")
			1 * catalogToolsMock.sortSkubyRegistry(null, [:], "141414", "BedBathUS", null, "") >> ['70158-5272':[sKUDetailVOMock2]]
			
			1 * catalogToolsMock.getParentProductForSku("sku56655") >> "prod8878"
			priceListManagerMock.getPriceList(profileMock, "priceList") >> repositoryItemMock
			1 * priceListManagerMock.getPrice(repositoryItemMock, "prod8878", "sku56655") >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue("listPrice") >> "23"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.getValues().get("222222_REG_SUMMARY").equals(registryResVOMock)
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
			1 * requestMock.getAttribute('sessionBean')
			2 * requestMock.setParameter('emptyOutOfStockListFlag', 'true')
			1 * requestMock.setParameter('priceRangeList', ['70$158-52$72', '100-250'])
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1 * requestMock.setParameter('categoryVOMap', ['1':registryCategoryMapVOMock])
			1 * requestMock.setParameter('certonaSkuList', '25522')
			2 * requestMock.setParameter('promoBox',  promoBoxVOMock)
			1 * requestMock.setParameter('skuList', '25522')
			2 * requestMock.setParameter('totEntries', 5)
			
	}
	
	def"service method. This TC is when RepositoryException thrown in withDefaultPrice Private Method"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setPriceListManager(priceListManagerMock)
			testObj.setProfilePriceListPropertyName("priceList")
			sessionBeanMock.setPrimeRegistryCompleted(FALSE)
			requestMock.resolveName("/atg/multisite/SiteContext") >> siteContextMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO(registryId:"222222",eventCode:"code")
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathUS") >> [registrySkinnyVOMock]
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code",registryTypeId:"141414")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"registryIn")
			1 * giftRegistryManagerMock.fetchRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:25522l,personalisedCode:"",jdaRetailPrice:0d)
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:5555l,personlisedPrice:12d)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:[registryItemVOMock],
				mSkuRegItemVOMap:["sku56655":registryItemVOMock1],totEntries:5)
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("222222", false, BBBCoreConstants.VIEW_ALL) >> registryItemsListVOMock
			RegistryCategoryMapVO registryCategoryMapVOMock = new RegistryCategoryMapVO()
			3 * catalogToolsMock.getCategoryForRegistry("141414") >> ["1":registryCategoryMapVOMock]
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("222222", "BedBathUS") >> registryResVOMock
			
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			2 * catalogToolsMock.getPromoBoxForRegistry("141414") >> promoBoxVOMock
			
			//fliterNotAvliableItem Private Method Coverage
			1 * catalogToolsMock.getParentProductForSku("25522", true)
			
			//getlistRegistryItemVO Private Method Coverage
			2 * catalogToolsMock.getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, testObj.getTopRegMaxCount()) >>> [["2"],[""]]
			
			//checkPrice Private Method Coverage
			3 * catalogToolsMock.getParentProductForSku("25522", true) >>> ["prod13456","prod13456",null]
			1 * catalogToolsMock.getSalePrice("prod13456", "25522") >> 0.0d
			1 * catalogToolsMock.getListPrice("prod13456", "25522") >> 23d
			
			//withDefaultCategory Private Method Coverage
			requestMock.getParameter(SITE_ID_PARAM) >> null
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			2 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.ENABLE_LTL_REG_FOR_SITE) >> [""]
			SKUDetailVO sKUDetailVOMock = new SKUDetailVO(skuId:"sku566554")
			1 * catalogToolsMock.sortSkubyRegistry(["25522"], null, "141414", "BedBathUS", BBBCatalogConstants.CATEGORY_SORT_TYPE, null) >> ["1":[sKUDetailVOMock]]
			
			//skuDetailsVO Private Method Coverage
			catalogToolsMock.isSKUBelowLine("BedBathUS", "sku56655") >> FALSE
			1 * catalogToolsMock.isSKUBelowLine('BedBathUS', 'sku566554') >> FALSE
			
			//withDefaultPrice Private Method Coverage
			sessionBeanMock.getValues().put("defaultUserCountryCode","")
			1 * catalogToolsMock.getPriceRanges("141414", "") >> ['70$158-52$72',"100-250"]
			SKUDetailVO sKUDetailVOMock2 = new SKUDetailVO(skuId:"sku56655")
			1 * catalogToolsMock.sortSkubyRegistry(null, [:], "141414", "BedBathUS", null, "") >> ['70158-5272':[sKUDetailVOMock2]]
			
			1 * catalogToolsMock.getParentProductForSku("sku56655") >> "prod8878"
			priceListManagerMock.getPriceList(profileMock, testObj.getProfilePriceListPropertyName()) >> repositoryItemMock
			1 * priceListManagerMock.getPrice(repositoryItemMock, "prod8878", "sku56655") >> {throw new RepositoryException("Mock for RepositoryException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.getValues().get("222222_REG_SUMMARY").equals(registryResVOMock)
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
			1 * testObj.logError('giftregistry_1080: null|false|RepositoryException from getlistRegistryItemVO() of RegistryItemsDisplayDroplet', _)
			1 * requestMock.setParameter('skuList', '25522')
			1 * requestMock.setParameter('certonaSkuList', '25522')
			1 * requestMock.setParameter('priceRangeList', ['70$158-52$72', '100-250'])
			1 * requestMock.setParameter('categoryVOMap', ['1':registryCategoryMapVOMock])
			2 * requestMock.setParameter('promoBox', promoBoxVOMock)
			1 * requestMock.setParameter('emptyOutOfStockListFlag', 'true')
			1 * requestMock.serviceLocalParameter("output", requestMock, responseMock)
			1 * requestMock.getAttribute('sessionBean')
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			2 * requestMock.setParameter('totEntries', 5)
			
	}
	
	def"service method. This TC is when sKUDetailVO skuId is passed as null in withDefaultPrice Private method"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setPriceListManager(priceListManagerMock)
			testObj.setProfilePriceListPropertyName("priceList")
			sessionBeanMock.setPrimeRegistryCompleted(FALSE)
			requestMock.resolveName("/atg/multisite/SiteContext") >> siteContextMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO(registryId:"222222",eventCode:"code")
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathUS") >> [registrySkinnyVOMock]
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code",registryTypeId:"141414")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"registryIn")
			1 * giftRegistryManagerMock.fetchRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:25522l,personalisedCode:"",jdaRetailPrice:0d)
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:5555l,personlisedPrice:12d)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:[registryItemVOMock],
				mSkuRegItemVOMap:[:],totEntries:5)
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("222222", false, BBBCoreConstants.VIEW_ALL) >> registryItemsListVOMock
			RegistryCategoryMapVO registryCategoryMapVOMock = new RegistryCategoryMapVO()
			3 * catalogToolsMock.getCategoryForRegistry("141414") >> ["1":registryCategoryMapVOMock]
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("222222", "BedBathUS") >> registryResVOMock
			
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			2 * catalogToolsMock.getPromoBoxForRegistry("141414") >> promoBoxVOMock
			
			//fliterNotAvliableItem Private Method Coverage
			1 * catalogToolsMock.getParentProductForSku("25522", true)
			
			//getlistRegistryItemVO Private Method Coverage
			2 * catalogToolsMock.getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, testObj.getTopRegMaxCount()) >>> [["2"],[""]]
			
			//checkPrice Private Method Coverage
			3 * catalogToolsMock.getParentProductForSku("25522", true) >>> ["prod13456","prod13456",null]
			1 * catalogToolsMock.getSalePrice("prod13456", "25522") >> 0.0d
			1 * catalogToolsMock.getListPrice("prod13456", "25522") >> 23d
			
			//withDefaultCategory Private Method Coverage
			requestMock.getParameter(SITE_ID_PARAM) >> null
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			2 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.ENABLE_LTL_REG_FOR_SITE) >> [""]
			SKUDetailVO sKUDetailVOMock = new SKUDetailVO(skuId:"sku566554")
			1 * catalogToolsMock.sortSkubyRegistry(["25522"], null, "141414", "BedBathUS", BBBCatalogConstants.CATEGORY_SORT_TYPE, null) >> ["1":[sKUDetailVOMock]]
			
			//skuDetailsVO Private Method Coverage
			catalogToolsMock.isSKUBelowLine("BedBathUS", "sku56655") >> FALSE
			1 * catalogToolsMock.isSKUBelowLine('BedBathUS', 'sku566554') >> FALSE
			
			//withDefaultPrice Private Method Coverage
			sessionBeanMock.getValues().put("defaultUserCountryCode","")
			1 * catalogToolsMock.getPriceRanges("141414", "") >> ['70$158-52$72',"100-250"]
			SKUDetailVO sKUDetailVOMock2 = new SKUDetailVO(skuId:null)
			1 * catalogToolsMock.sortSkubyRegistry(null, [:], "141414", "BedBathUS", null, "") >> ['70158-5272':[sKUDetailVOMock2]]
			
			1 * catalogToolsMock.getParentProductForSku(null) >> null
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.getValues().get("222222_REG_SUMMARY").equals(registryResVOMock)
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
			1 * testObj.logError('giftregistry_1004: null|false|Exception from service of PrimeGiftRegistryInfoDroplet', _)
			1 * requestMock.setParameter('skuList', '25522')
			1 * requestMock.setParameter('certonaSkuList', '25522')
			1 * requestMock.setParameter('categoryVOMap', ['1':registryCategoryMapVOMock])
			1 * requestMock.setParameter('emptyOutOfStockListFlag', 'true')
			2 * requestMock.setParameter('totEntries', 5)
			1 * requestMock.setParameter('priceRangeList', ['70$158-52$72', '100-250'])
			2 * requestMock.setParameter('promoBox', promoBoxVOMock)
			1 * requestMock.setParameter('errorMsg', 'err_regsearch_biz_exception')
			1 * requestMock.getAttribute('sessionBean')
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	def"service method. This TC is when PriceListException thrown in withDefaultPrice Private Method"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			testObj.setSiteContext(siteContextMock)
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setPriceListManager(priceListManagerMock)
			testObj.setProfilePriceListPropertyName("priceList")
			sessionBeanMock.setPrimeRegistryCompleted(FALSE)
			requestMock.resolveName("/atg/multisite/SiteContext") >> siteContextMock
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> "BedBathUS"
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO(registryId:"222222",eventCode:"code")
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, "BedBathUS") >> [registrySkinnyVOMock]
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code",registryTypeId:"141414")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"registryIn")
			1 * giftRegistryManagerMock.fetchRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:25522l,personalisedCode:"",jdaRetailPrice:0d)
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:5555l,personlisedPrice:12d)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:[registryItemVOMock],
				mSkuRegItemVOMap:[:],totEntries:5)
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("222222", false, BBBCoreConstants.VIEW_ALL) >> registryItemsListVOMock
			RegistryCategoryMapVO registryCategoryMapVOMock = new RegistryCategoryMapVO()
			3 * catalogToolsMock.getCategoryForRegistry("141414") >> ["1":registryCategoryMapVOMock]
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("222222", "BedBathUS") >> registryResVOMock
			
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			2 * catalogToolsMock.getPromoBoxForRegistry("141414") >> promoBoxVOMock
			
			//fliterNotAvliableItem Private Method Coverage
			1 * catalogToolsMock.getParentProductForSku("25522", true)
			
			//getlistRegistryItemVO Private Method Coverage
			2 * catalogToolsMock.getAllValuesForKey(BBBCertonaConstants.FEED_CONFIG_TYPE, testObj.getTopRegMaxCount()) >>> [["2"],[""]]
			
			//checkPrice Private Method Coverage
			3 * catalogToolsMock.getParentProductForSku("25522", true) >>> ["prod13456","prod13456",null]
			1 * catalogToolsMock.getSalePrice("prod13456", "25522") >> 0.0d
			1 * catalogToolsMock.getListPrice("prod13456", "25522") >> 23d
			
			//withDefaultCategory Private Method Coverage
			requestMock.getParameter(SITE_ID_PARAM) >> null
			requestMock.getHeader("X-bbb-site-id") >> "BedBathUS"
			2 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.ENABLE_LTL_REG_FOR_SITE) >> [""]
			SKUDetailVO sKUDetailVOMock = new SKUDetailVO(skuId:"sku566554")
			1 * catalogToolsMock.sortSkubyRegistry(["25522"], null, "141414", "BedBathUS", BBBCatalogConstants.CATEGORY_SORT_TYPE, null) >> ["1":[sKUDetailVOMock]]
			
			//skuDetailsVO Private Method Coverage
			catalogToolsMock.isSKUBelowLine("BedBathUS", "sku56655") >> FALSE
			1 * catalogToolsMock.isSKUBelowLine('BedBathUS', 'sku566554') >> FALSE
			
			//withDefaultPrice Private Method Coverage
			sessionBeanMock.getValues().put("defaultUserCountryCode","")
			1 * catalogToolsMock.getPriceRanges("141414", "") >> ['70$158-52$72',"100-250"]
			SKUDetailVO sKUDetailVOMock2 = new SKUDetailVO(skuId:"sku56655")
			1 * catalogToolsMock.sortSkubyRegistry(null, [:], "141414", "BedBathUS", null, "") >> ['70158-5272':[sKUDetailVOMock2]]
			priceListManagerMock.getPriceList(profileMock, testObj.getProfilePriceListPropertyName()) >> {throw new PriceListException("Mock for PriceListException")}
			1 * catalogToolsMock.getParentProductForSku("sku56655") >> "prod52212"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.getValues().get("222222_REG_SUMMARY").equals(registryResVOMock)
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
			1 * testObj.logError('giftregistry_1081: null|false|PriceListException from withDefaultPrice of RegistryItemsDisplayDroplet ', _)
			1 * testObj.logError('giftregistry_1004: null|false|Exception from service of PrimeGiftRegistryInfoDroplet', _)
			1 * requestMock.setParameter('skuList', '25522')
			1 * requestMock.setParameter('certonaSkuList', '25522')
			1 * requestMock.setParameter('categoryVOMap', ['1':registryCategoryMapVOMock])
			1 * requestMock.setParameter('emptyOutOfStockListFlag', 'true')
			2 * requestMock.setParameter('totEntries', 5)
			1 * requestMock.setParameter('priceRangeList', ['70$158-52$72', '100-250'])
			2 * requestMock.setParameter('promoBox', promoBoxVOMock)
			1 * requestMock.setParameter('errorMsg', 'err_regsearch_biz_exception')
			1 * requestMock.getAttribute('sessionBean')
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	
	////////////////////////////////////TCs for service ends////////////////////////////////////////
	
	/////////////////////////////////TCs for primeGiftRegistryInfo starts////////////////////////////////////////
	//Signature : public boolean primeGiftRegistryInfo() ////
	
	def"primeGiftRegistryInfo. This TC is when listRegistryItemVO is assigned as null and empty"(){
		given:
			String siteId = "BedBathUS"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> siteId
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO(registryId:"222222",eventCode:"code")
			RegistrySkinnyVO registrySkinnyVOMock1 = new RegistrySkinnyVO(registryId:"33333",eventCode:"noCode")
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, siteId) >> [registrySkinnyVOMock,registrySkinnyVOMock1]
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code",registryTypeId:"141414")
			1 * giftRegistryManagerMock.fetchRegistryTypes(siteId) >> [registryTypeVOMock]
			RegistryItemVO registryItemVOMock = new RegistryItemVO()
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:null,mSkuRegItemVOMap:["sku56655":registryItemVOMock])
			RegistryItemsListVO registryItemsListVOMock1 = new RegistryItemsListVO(registryItemList:[],mSkuRegItemVOMap:["sku56655":registryItemVOMock])
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("222222", false, BBBCoreConstants.VIEW_ALL) >> registryItemsListVOMock
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("33333", false, BBBCoreConstants.VIEW_ALL) >> registryItemsListVOMock1
			RegistryCategoryMapVO registryCategoryMapVOMock = new RegistryCategoryMapVO()
			RegistryCategoryMapVO registryCategoryMapVOMock1 = new RegistryCategoryMapVO()
			4 * catalogToolsMock.getCategoryForRegistry("141414") >> ["1":registryCategoryMapVOMock,"2":registryCategoryMapVOMock1]
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("222222", siteId) >> registryResVOMock
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("33333", siteId) >> registryResVOMock
			
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			4 * catalogToolsMock.getPromoBoxForRegistry("141414") >> promoBoxVOMock
			
			//checkWithNull Private Method Coverage
			2 * catalogToolsMock.getPriceRanges("141414", null) >> ["15-25","100-250"]
			sessionBeanMock.getValues().put("defaultUserCountryCode","NJ")
			
		
		when:
			boolean results = testObj.primeGiftRegistryInfo()
		then:
			results == TRUE
			sessionBeanMock.getValues().get("222222_REG_SUMMARY").equals(registryResVOMock)
			sessionBeanMock.getValues().get("33333_REG_SUMMARY").equals(registryResVOMock)
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
			4 * requestMock.setParameter(BBBGiftRegistryConstants.PROMOBOX, promoBoxVOMock)
			4 * requestMock.setParameter(BBBGiftRegistryConstants.TOTAL_ENTRIES_COUNT, 0)
			2 * requestMock.setParameter(BBBGiftRegistryConstants.REGISTRY_CATEGORY_MAP_VO, ["1":registryCategoryMapVOMock,"2":registryCategoryMapVOMock1])
			2 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS, ["1":null, "2":null])
			4 * requestMock.setParameter(BBBGiftRegistryConstants.EMPTY_LIST, "true")
			2 * requestMock.setParameter(BBBGiftRegistryConstants.SORT_SEQUENCE, "1")
			4 * requestMock.serviceParameter("output", requestMock, responseMock)
			2 * requestMock.setParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST, ['15-25', '100-250'])
			2 * requestMock.setParameter(BBBGiftRegistryConstants.SORT_SEQUENCE, "2")
			2 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS, ['15-25':null, '100-250':null])
			2 * requestMock.getAttribute('sessionBean')
			2 * requestMock.setAttribute('sessionBean', sessionBeanMock)
	}
	
	def"primeGiftRegistryInfo. This TC is when registryItemsListVO is null"(){
		given:
			String siteId = "BedBathUS"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> null
			requestMock.getHeader("X-bbb-site-id") >> siteId
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO(registryId:"222222",eventCode:"code")
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, siteId) >> [registrySkinnyVOMock]
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code",registryTypeId:"141414")
			1 * giftRegistryManagerMock.fetchRegistryTypes(siteId) >> [registryTypeVOMock]
			RegistryItemVO registryItemVOMock = new RegistryItemVO()
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("222222", false, BBBCoreConstants.VIEW_ALL) >> null
			RegistryCategoryMapVO registryCategoryMapVOMock = new RegistryCategoryMapVO()
			1 * catalogToolsMock.getCategoryForRegistry("141414") >> ["1":registryCategoryMapVOMock]
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("222222", siteId) >> registryResVOMock
		
		when:
			boolean results = testObj.primeGiftRegistryInfo()
			
		then:
			results == TRUE
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
	}
	
	def"primeGiftRegistryInfo. This TC is when Exception thrown"(){
		given:
			testObj = Spy()
			testObj.setSiteContext(siteContextMock)
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			String siteId = "BedBathUS"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> null
			requestMock.getHeader("X-bbb-site-id") >> siteId
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO()
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, siteId) >> {throw new Exception("Mock for Exception")}

		when:
			boolean results = testObj.primeGiftRegistryInfo()
			
		then:
			results == FALSE
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
			1 * testObj.logError('BBBBusinessException from service of PrimeGiftRegistryInfoDroplet', _)
			1 * testObj.logDebug('Priming of giftregistry data will be done as RegItemWSCall key value is FALSE ')
			1 * testObj.logDebug(' PrimeGiftRegistryInfoDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start')
			1 * testObj.logDebug(' PrimeGiftRegistryInfoDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - ends')
			
	}
	
	def"primeGiftRegistryInfo. This TC is when registryTypeId is empty and country is MX"(){
		given:
			String siteId = "BedBathUS"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> siteId
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO(registryId:"222222",eventCode:"code")
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, siteId) >> [registrySkinnyVOMock]
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"registryCode")
			1 * giftRegistryManagerMock.fetchRegistryTypes(siteId) >> [registryTypeVOMock]
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:5555l)
			RegistryItemVO registryItemVOMock = new RegistryItemVO()
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:[registryItemVOMock1],mSkuRegItemVOMap:["sku56655":registryItemVOMock])
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("222222", false, BBBCoreConstants.VIEW_ALL) >> registryItemsListVOMock
			2 * catalogToolsMock.getCategoryForRegistry(null) >> [:]
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("222222", siteId) >> registryResVOMock
			
			//processItemList Private Method Coverage
			2 * catalogToolsMock.getPromoBoxForRegistry(null) >> null
			
			//checkWithNull Private Method Coverage
			1 * catalogToolsMock.getPriceRanges(null, null) >> null
			1 * catalogToolsMock.getPriceRanges(null, "MX") >> ["15-25"]
			sessionBeanMock.getValues().put("defaultUserCountryCode","MX")
		
		when:
			boolean results = testObj.primeGiftRegistryInfo()
		then:
			results == TRUE
			sessionBeanMock.getValues().get("222222_REG_SUMMARY").equals(registryResVOMock)
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
			2 * requestMock.setParameter(BBBGiftRegistryConstants.TOTAL_ENTRIES_COUNT, 0)
			1 * requestMock.setParameter('categoryVOMap', [:])
			2 * requestMock.setParameter('emptyList', 'true')
			1 * requestMock.getAttribute('sessionBean')
			2 * requestMock.setParameter('promoBox', null)
			2 * requestMock.serviceParameter('output', requestMock, responseMock)
			1 * requestMock.setParameter('sortSequence', '2')
			1 * requestMock.setParameter('sortSequence', '1')
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.setParameter('categoryBuckets', [:])
			1 * requestMock.setParameter('priceRangeList', ['15-25'])
			1 * requestMock.setParameter('categoryBuckets', ['15-25':null])
			
	}
	
	def"primeGiftRegistryInfo. This TC is when registryTypeId is empty and country is US"(){
		given:
			String siteId = "BedBathUS"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> siteId
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO(registryId:"222222",eventCode:"code")
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, siteId) >> [registrySkinnyVOMock]
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code",registryTypeId:"141414")
			1 * giftRegistryManagerMock.fetchRegistryTypes(siteId) >> [registryTypeVOMock]
			RegistryItemVO registryItemVOMock = new RegistryItemVO()
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:null,mSkuRegItemVOMap:["sku56655":registryItemVOMock])
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("222222", false, BBBCoreConstants.VIEW_ALL) >> registryItemsListVOMock
			2 * catalogToolsMock.getCategoryForRegistry("141414") >> [:]
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("222222", siteId) >> registryResVOMock
			
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			2 * catalogToolsMock.getPromoBoxForRegistry("141414") >> promoBoxVOMock
			
			//checkWithNull Private Method Coverage
			1 * catalogToolsMock.getPriceRanges("141414", null) >> ["10","18"]
			sessionBeanMock.getValues().put("defaultUserCountryCode","US")
		
		when:
			boolean results = testObj.primeGiftRegistryInfo()
		then:
			results == TRUE
			sessionBeanMock.getValues().get("222222_REG_SUMMARY").equals(registryResVOMock)
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
			2 * requestMock.setParameter(BBBGiftRegistryConstants.TOTAL_ENTRIES_COUNT, 0)
			1 * requestMock.setParameter('categoryVOMap', [:])
			2 * requestMock.setParameter('emptyList', 'true')
			1 * requestMock.getAttribute('sessionBean')
			2 * requestMock.setParameter('promoBox', promoBoxVOMock)
			2 * requestMock.serviceParameter('output', requestMock, responseMock)
			1 * requestMock.setParameter('sortSequence', '2')
			1 * requestMock.setParameter('sortSequence', '1')
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.setParameter('categoryBuckets', [:])
			1 * requestMock.setParameter('priceRangeList', ['10', '18'])
			1 * requestMock.setParameter('categoryBuckets', ['10':null, '18':null])
			
	}
	
	def"primeGiftRegistryInfo. This TC is when registryTypeId is empty and country is empty"(){
		given:
			String siteId = "BedBathUS"
			siteContextMock.getSite() >> siteMock
			siteMock.getId() >> siteId
			RegistrySkinnyVO registrySkinnyVOMock = new RegistrySkinnyVO(registryId:"222222",eventCode:"code")
			1 * giftRegistryManagerMock.getAcceptableGiftRegistries(profileMock, siteId) >> [registrySkinnyVOMock]
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code",registryTypeId:"141414")
			1 * giftRegistryManagerMock.fetchRegistryTypes(siteId) >> [registryTypeVOMock]
			RegistryItemVO registryItemVOMock = new RegistryItemVO()
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:null,mSkuRegItemVOMap:["sku56655":registryItemVOMock])
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin("222222", false, BBBCoreConstants.VIEW_ALL) >> registryItemsListVOMock
			2 * catalogToolsMock.getCategoryForRegistry("141414") >> [:]
			RegistryResVO registryResVOMock = new RegistryResVO()
			1 * giftRegistryManagerMock.getRegistryInfoFromEcomAdmin("222222", siteId) >> registryResVOMock
			
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			2 * catalogToolsMock.getPromoBoxForRegistry("141414") >> promoBoxVOMock
			
			//checkWithNull Private Method Coverage
			1 * catalogToolsMock.getPriceRanges("141414", null) >> ["10","18"]
			sessionBeanMock.getValues().put("defaultUserCountryCode","")
		
		when:
			boolean results = testObj.primeGiftRegistryInfo()
		then:
			results == TRUE
			sessionBeanMock.getValues().get("222222_REG_SUMMARY").equals(registryResVOMock)
			sessionBeanMock.isPrimeRegistryCompleted().equals(TRUE)
			2 * requestMock.setParameter(BBBGiftRegistryConstants.TOTAL_ENTRIES_COUNT, 0)
			1 * requestMock.setParameter('categoryVOMap', [:])
			2 * requestMock.setParameter('emptyList', 'true')
			1 * requestMock.getAttribute('sessionBean')
			2 * requestMock.setParameter('promoBox', promoBoxVOMock)
			2 * requestMock.serviceParameter('output', requestMock, responseMock)
			1 * requestMock.setParameter('sortSequence', '2')
			1 * requestMock.setParameter('sortSequence', '1')
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.setParameter('categoryBuckets', [:])
			1 * requestMock.setParameter('priceRangeList', ['10', '18'])
			1 * requestMock.setParameter('categoryBuckets', ['10':null, '18':null])
			
	}
	
	////////////////////////////////////TCs for primeGiftRegistryInfo ends////////////////////////////////////////
	
	/////////////////////////////////TCs for getNewKeyRange starts////////////////////////////////////////
	//Signature : public String getNewKeyRange(String value) ////
	
	def"getNewKeyRange. This TC is when i = 0"(){
		given:
			String value = 'sku#$$111?'
		when:
			String results = testObj.getNewKeyRange(value)
		then:
			results == 'sku#$11.001?'
	}
	
	def"getNewKeyRange. This TC is when i = 1"(){
		given:
			String value = 'order@#$$()837893412?'
		when:
			String results = testObj.getNewKeyRange(value)
		then:
			results == 'order@#$$()837893412?'
	}
	
	////////////////////////////////////TCs for getNewKeyRange ends////////////////////////////////////////
	
	
	
	
}
