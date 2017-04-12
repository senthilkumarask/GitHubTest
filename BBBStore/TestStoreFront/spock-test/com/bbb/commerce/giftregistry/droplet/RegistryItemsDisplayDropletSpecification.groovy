package com.bbb.commerce.giftregistry.droplet

import java.util.Collections;
import java.util.List;
import java.util.Map;

import atg.commerce.pricing.priceLists.PriceListManager
import atg.multisite.SiteContextManager;
import atg.nucleus.naming.ParameterName;
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile

import com.bbb.browse.webservice.manager.BBBEximManager
import com.bbb.cms.PromoBoxVO
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.RegistryCategoryMapVO;
import com.bbb.commerce.catalog.vo.RegistryTypeVO
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.checklist.manager.CheckListManager
import com.bbb.commerce.checklist.tools.CheckListTools
import com.bbb.commerce.checklist.vo.CategoryVO
import com.bbb.commerce.checklist.vo.CheckListVO
import com.bbb.commerce.checklist.vo.MyItemCategoryVO
import com.bbb.commerce.checklist.vo.MyItemVO
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools
import com.bbb.commerce.giftregistry.vo.RegistryItemVO
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBConfigRepoUtils;

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class RegistryItemsDisplayDropletSpecification extends BBBExtendedSpec {
	
	RegistryItemsDisplayDroplet testObj
	GiftRegistryManager giftRegistryManagerMock = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	PriceListManager priceListManagerMock = Mock()
	BBBEximManager eximManagerMock = Mock()
	CheckListManager checkListManagerMock = Mock()
	Profile profileMock = Mock()
	BBBSessionBean sessionBeanMock = new BBBSessionBean()
	RepositoryItem repositoryItemMock = Mock()
	CheckListTools checkListToolsMock = Mock()
	GiftRegistryTools giftRegistryToolsMock = Mock()

	private static final boolean TRUE = true
	private static final boolean FALSE = false
	private static final String SITE_ID = "siteId";
	private static final String EVENT_DATE = "eventDate";
	public static final ParameterName REGISTRY_ID = ParameterName.getParameterName( BBBGiftRegistryConstants.REGISTRY_ID );
	public static final ParameterName C2_ID = ParameterName.getParameterName(BBBGiftRegistryConstants.C2_ID)
	public static final ParameterName C1_ID = ParameterName.getParameterName(BBBGiftRegistryConstants.C1_ID)
	public static final ParameterName C3_ID = ParameterName.getParameterName(BBBGiftRegistryConstants.C3_ID)
	public static final ParameterName SORT_SEQ = ParameterName.getParameterName(BBBGiftRegistryConstants.SORT_SEQ)
	public static final ParameterName VIEW = ParameterName.getParameterName( BBBGiftRegistryConstants.VIEW )
	public static final ParameterName REG_EVENT_TYPE_CODE = ParameterName.getParameterName(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE)
	public static final ParameterName EVENT_TYPE = ParameterName.getParameterName(BBBGiftRegistryConstants.EVENT_TYPE)
	public static final ParameterName START_INDEX = ParameterName.getParameterName(BBBGiftRegistryConstants.START_INDEX)
	public static final ParameterName BULK_SIZE = ParameterName.getParameterName(BBBGiftRegistryConstants.BULK_SIZE)
	public static final ParameterName IS_GIFT_GIVER = ParameterName.getParameterName(BBBGiftRegistryConstants.IS_GIFT_GIVER)
	public static final ParameterName IS_AVAIL_WEBPUR = ParameterName.getParameterName(BBBGiftRegistryConstants.IS_AVAIL_WEBPUR)
	public static final ParameterName SITE_ID_PARAM = ParameterName.getParameterName(SITE_ID)
	public static final ParameterName EVENT_DATE_PARAM = ParameterName.getParameterName(EVENT_DATE)
	public static final ParameterName PROFILE = ParameterName.getParameterName("profile")
	public static final ParameterName IS_MY_ITEMS_CHECKLIST = ParameterName.getParameterName(BBBGiftRegistryConstants.IS_MY_ITEMS_CHECKLIST)
	public static final ParameterName IS_SECOND_CALL = ParameterName.getParameterName(BBBGiftRegistryConstants.IS_SECOND_CALL)
	public static final ParameterName FROM_CHECKLIST = ParameterName.getParameterName( BBBGiftRegistryConstants.FROM_CHECKLIST)

	
	def setup(){
		testObj = new RegistryItemsDisplayDroplet(giftRegistryManager:giftRegistryManagerMock,catalogTools:catalogToolsMock,priceListManager:priceListManagerMock,
			eximManager:eximManagerMock,checkListManager:checkListManagerMock,registryItemsListServiceName:"getRegistryItemList2",topRegMaxCount:"TopRegMaxCount",certonaListMaxCount:2,
			profilePriceListPropertyName:"priceList")
		
		requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profileMock
		requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
		BBBConfigRepoUtils.setBbbCatalogTools(catalogToolsMock)
		checkListManagerMock.getCheckListTools() >> checkListToolsMock
		giftRegistryManagerMock.getGiftRegistryTools() >> giftRegistryToolsMock
	}
	
	/////////////////////////////////TCs for service starts////////////////////////////////////////
	//Signature : public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ////
	
	def"service method. This TC is when isMyItemsCheckList is true and sortSeq is 1"(){
		given:
		
			String siteId = "BedBathUS"; String isSecondCall = "FALSE";	String registryId = "23236562";	String c2id = "c2Id"; String c3id = "c3Id";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = FALSE; Boolean isMxGiftGiver = TRUE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = null; String view = null; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver, 
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12452","23236562","BedBathUS")
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> ["true"]
			2 * catalogToolsMock.getAllValuesForKey("WSDLKeys", "WebServiceUserToken") >> ["true"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistryItemVO registryItemVOMock = new RegistryItemVO()
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO()
			RegistryItemVO registryItemVOMock2 = new RegistryItemVO()
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,registryItemList: [registryItemVOMock,registryItemVOMock1],
				mSkuRegItemVOMap:["1":registryItemVOMock2],totEntries:5)
			
			1 * giftRegistryManagerMock.fetchRegistryItems(_) >> registryItemsListVOMock
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"151515")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> ["true"]
			
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO() 
			1 * catalogToolsMock.getPromoBoxForRegistry("151515") >> promoBoxVOMock
			
			//getlistRegistryItemVO Private Method Coverage 
			1 * catalogToolsMock.getAllValuesForKey("CertonaKeys", testObj.getTopRegMaxCount()) >> ["2"]
			
			//setMyItemsForEPHNodes Public method Coverage
			requestMock.getParameter(EVENT_TYPE) >> "Wedding"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> ["true"]
			1 * checkListManagerMock.getMyItemVO("23236562", "Wedding") >> null
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			sessionBeanMock.getValues().get("registryItems").equals(registryItemsListVOMock)
			1 * requestMock.setParameter('categoryBuckets', [:])
			1 * requestMock.setParameter('totEntries', 5)
			1 * requestMock.setParameter('sortSequence', '1')
			1 * requestMock.setParameter('count', 2)
			1 * requestMock.setParameter('inStockCategoryBuckets', [:])
			1 * requestMock.setParameter('notInStockCategoryBuckets', [:])
			1 * requestMock.setParameter('promoBox',  promoBoxVOMock)
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
		
	}
	
	def"service method. This TC is when isMyItemsCheckList, isMxGiftGiver are false and isGiftGiver is true"(){
		given:
		
			String siteId = "BedBathUS"; String isSecondCall = "";	String registryId = "23236562";	String c2id = "c2Id"; String c3id = "c3Id";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = TRUE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = 1; String view = 1; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = FALSE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> ["true"]
			2 * catalogToolsMock.getAllValuesForKey("WSDLSiteFlags", siteId) >> ["BedBathUS"]
			1 * catalogToolsMock.getAllValuesForKey("WSDLKeys", "WebServiceUserToken") >> null
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RepositoryItem repositoryItemMock3 = Mock()
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuRepositoryItem:repositoryItemMock)
			SKUDetailVO skuDetailVOMock1 = new SKUDetailVO(skuRepositoryItem:repositoryItemMock3)
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:8585l,qtyRequested:1,sKUDetailVO:skuDetailVOMock,qtyPurchased:1)
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:9595l,qtyRequested:2,sKUDetailVO:skuDetailVOMock,qtyPurchased:3)
			RegistryItemVO registryItemVOMock4 = new RegistryItemVO(sku:1515l,qtyRequested:3,sKUDetailVO:null,qtyPurchased:2)
			RegistryItemVO registryItemVOMock5 = new RegistryItemVO(sku:2525l,qtyRequested:4,sKUDetailVO:skuDetailVOMock1,qtyPurchased:2)
			RegistryItemVO registryItemVOMock2 = new RegistryItemVO(sku:2352l,personalisedCode:"percode",refNum:"-1",ltlDeliveryServices:"",itemType:null)
			RegistryItemVO registryItemVOMock3 = new RegistryItemVO(sku:3521l,personalisedCode:"",refNum:"1",ltlDeliveryServices:"",itemType:"LT")
			
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,
				registryItemList:[registryItemVOMock,registryItemVOMock1,registryItemVOMock4,registryItemVOMock5],
				mSkuRegItemVOMap:["1":registryItemVOMock2,"2":registryItemVOMock3],totEntries:5)
			1 * giftRegistryManagerMock.fetchRegistryItems(_) >> registryItemsListVOMock
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> [""]
			RepositoryItem repositoryItemMock1 = Mock()
			repositoryItemMock1.getRepositoryId() >> "id12345"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >>> [[].toSet(),null]
			1 * catalogToolsMock.getListPrice(null,"8585") >> 0d
			1 * catalogToolsMock.getSalePrice(null,"8585") >> 0d
			1 * catalogToolsMock.getListPrice(null,"9595") >> 0d
			1 * catalogToolsMock.getSalePrice(null,"9595") >> 0d
			1 * catalogToolsMock.getListPrice("id12345","2525") >> 50d
			1 * catalogToolsMock.getSalePrice("id12345","2525") >> 20d
			repositoryItemMock3.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >> [repositoryItemMock1].toSet()
			
			//setLTLAttributesInRegItem Public Method Coverage
			1 * eximManagerMock.getPersonalizedOptionsDisplayCode("percode") >> "displayCode"
			
			//processItemList Private Method Coverage
			1 * catalogToolsMock.getPromoBoxForRegistry("") >> null
			1 * checkListManagerMock.showC1CategoryOnRlp(c1id, registryId, eventTypeCode) >> "NotShow"
			requestMock.getParameter(FROM_CHECKLIST) >> "true"
			MyItemCategoryVO myItemCategoryVOMock = new MyItemCategoryVO()
			MyItemVO myItemVOMock = new MyItemVO(categoryListVO:[myItemCategoryVOMock])
			1 * checkListManagerMock.getMyItemVO(registryId, eventTypeCode) >> myItemVOMock
			
			//fliterNotAvliableItem Public Method Coverage
			1 * catalogToolsMock.getParentProductForSku("8585", true)
			1 * catalogToolsMock.getParentProductForSku("9595", true)
			
			//calculateQty Private Method Coverage
			CategoryVO categoryVOMock = new CategoryVO(categoryId:"c1Id",childCategoryVO: null)
			CategoryVO categoryVOMock1 = new CategoryVO(categoryId:"cid",childCategoryVO:[])
			CategoryVO categoryVOMock4 = new CategoryVO(categoryId:"c2Id")
			CategoryVO categoryVOMock3 = new CategoryVO(childCategoryVO:[categoryVOMock4],categoryId:"c2Id")
			CategoryVO categoryVOMock2 = new CategoryVO(categoryId:"c1Id",childCategoryVO:[categoryVOMock3])
			CategoryVO categoryVOMock7 = new CategoryVO(categoryId:"c3Id",addedQuantity:5,suggestedQuantity:2)
			CategoryVO categoryVOMock6 = new CategoryVO(childCategoryVO:[categoryVOMock7],categoryId:"c2Id")
			CategoryVO categoryVOMock5 = new CategoryVO(categoryId:"c1Id",childCategoryVO:[categoryVOMock6])
			CheckListVO checkListVOMock = new CheckListVO(categoryListVO:[categoryVOMock,categoryVOMock1,categoryVOMock2,categoryVOMock5])
			sessionBeanMock.setChecklistVO(checkListVOMock)
			
			//checkWithNull Private Method Coverage
			RegistryCategoryMapVO registryCategoryMapVOMock = new RegistryCategoryMapVO()
			RegistryCategoryMapVO registryCategoryMapVOMock1 = new RegistryCategoryMapVO()
			1 * catalogToolsMock.getCategoryForRegistry("") >> ["first": registryCategoryMapVOMock,"second":registryCategoryMapVOMock1]
			
			//enableBuyOffStartBrowsing Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "buyoff_start_browsing_key") >> ["true"]
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey("buyoffItemPurchasedLimit") >> "4"
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey("buyoffItemPurchasedPercentageLimit") >> "8"
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			registryItemVOMock2.getPersonalizationOptionsDisplay().equals("displayCode")
			registryItemVOMock2.getRefNum().equals("")
			1 * requestMock.setParameter('totEntries', 0)
			1 * requestMock.setParameter('addedCount', 5)
			1 * requestMock.setParameter('emptyList', 'true')
			1 * requestMock.setParameter('showStartBrowsing', true)
			1 * requestMock.setParameter('omniProductList', ';null;;;event22=1|event23=0.0;eVar30=8585;null;;;event22=2|event23=0.0;eVar30=9595;id12345;;;event22=4|event23=80.0;eVar30=2525')
			1 * requestMock.setParameter('sortSequence', '1')
			1 * requestMock.setParameter('categoryBuckets', ['first':null, 'second':null])
			1 * requestMock.setParameter('qtyof', '5 of 2')
			1 * requestMock.setParameter('categoryVOMap', ["first": registryCategoryMapVOMock,"second":registryCategoryMapVOMock1])
			1 * requestMock.setParameter('other', true)
			1 * requestMock.setParameter('promoBox', null)
			1 * requestMock.serviceParameter('output', requestMock , responseMock)
			1 * catalogToolsMock.getParentProductForSku('2525', true)
			1 * catalogToolsMock.getParentProductForSku('1515', true)
			
	}
	
	def"service method. This TC is when isSecondCall is true and isGiftGiver is false"(){
		given:
			requestMock.getParameter(IS_SECOND_CALL) >> TRUE
			
			//setRegItemsSecondCall Public Method Coverage
			requestMock.getParameter(EVENT_DATE_PARAM) >> "12/12/2016"
			requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> "cat12354"
			requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			requestMock.getParameter(C1_ID) >> ""
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(parentProdId:null)
			SKUDetailVO skuDetailVOMock1 = new SKUDetailVO(parentProdId:"prod23585")
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sKUDetailVO:skuDetailVOMock,sku:2222l,jdaRetailPrice:20d,personalisedCode:null,ltlDeliveryServices:"")
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sKUDetailVO:null,sku:1111l)
			RegistryItemVO registryItemVOMock2 = new RegistryItemVO(sKUDetailVO:skuDetailVOMock1,sku:3333l,jdaRetailPrice:0d,personalisedCode:"",ltlDeliveryServices:"")
			sessionBeanMock.getValues().put("registryItemsAll",[registryItemVOMock,registryItemVOMock1,registryItemVOMock2])
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> ["true"]
			MyItemCategoryVO myItemCategoryVOMock = new MyItemCategoryVO(categoryId:"cat8988")
			MyItemCategoryVO myItemCategoryVOMock1 = new MyItemCategoryVO(categoryId:"cat12354")
			MyItemCategoryVO myItemCategoryVOMock2 = new MyItemCategoryVO(categoryId:"cat5225",childCategoryVO:[])
			sessionBeanMock.getValues().put("ephCategoryBuckets",[myItemCategoryVOMock,myItemCategoryVOMock1,myItemCategoryVOMock2])
			RegistryItemVO registryItemVOMock3 = new RegistryItemVO()
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:[registryItemVOMock3])
			sessionBeanMock.getValues().put("registryItems",registryItemsListVOMock)
			
			//setCertonaSkuList Public Method Coverage
			requestMock.getParameter(IS_GIFT_GIVER) >> FALSE
			
			//checkPrice Private Method Coverage
			1 * catalogToolsMock.getParentProductForSku("1111", true) >> null
			1 * catalogToolsMock.getParentProductForSku("2222", true) >> "prod11111"
			1 * catalogToolsMock.getParentProductForSku("3333", true) >> "prod22222"
			1 * catalogToolsMock.getSkuIncartFlag("2222") >> FALSE
			
			1 * catalogToolsMock.getSkuIncartFlag("3333") >> TRUE
			1 * catalogToolsMock.getIncartPrice("prod22222", "3333") >> 15d
			1 * catalogToolsMock.getSalePrice("prod22222","3333") >> 20d
			1 * catalogToolsMock.getListPrice("prod22222","3333") >> 28d
						
		when:
			testObj.service(requestMock, responseMock)
		then:
			sessionBeanMock.getValues().get("ephCategoryBuckets").equals(null)
			sessionBeanMock.getValues().get("registryItems").equals(null)
			sessionBeanMock.getValues().get("registryItemsAll").equals(null)
			1 * requestMock.setParameter('skuList', '2222;3333')
			1 * requestMock.setParameter('ephCategoryBuckets', [myItemCategoryVOMock2])
			1 * requestMock.setParameter('itemList', 'prod23585')
			1 * requestMock.setParameter('certonaSkuList', '2222')
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
	}
	
	def"service method. This TC is when regItemsWSCall is null,isMyItemsCheckList is false and errorId is 900 in processItemList Private Method"(){
		given:
			this.spyMethod()
			
			String siteId = "BedBathUS"; String isSecondCall = "FALSE";	String registryId = "23236562";	String c2id = "c2Id"; String c3id = "c3Id";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = FALSE; Boolean isMxGiftGiver = TRUE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = null; String view = null; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = FALSE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12452","23236562","BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> null
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"errorMessage",errorId:900)
			RegistryItemVO registryItemListMock = new RegistryItemVO(sku:8585l,personalisedCode:"code1",refNum:"refnum1",personlisedPrice:25d)
			RegistryItemVO registryItemListMock1 = new RegistryItemVO(sku:9595l,personalisedCode:"",ltlDeliveryServices:"LTLDeliveries")
			RegistryItemVO registryItemListMock2 = new RegistryItemVO(sku:2222l,personalisedCode:"",ltlDeliveryServices:"LTL2",itemType:"LTL")
			RegistryItemVO mSkuRegItemVOMapMock = new RegistryItemVO(sku:1111l,personalisedCode:"percode",refNum:"",ltlDeliveryServices:"ltlDelivery",assemblySelected:"N",itemType:"LTLShipping")
			RegistryItemVO mSkuRegItemVOMapMock1 = new RegistryItemVO(sku:2222l,personalisedCode:"percode1",refNum:"",ltlDeliveryServices:"LWA",assemblySelected:"N",itemType:"LTL",qtyPurchased:0)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,registryItemList: [registryItemListMock,registryItemListMock1,registryItemListMock2],
				mSkuRegItemVOMap:["sku12345_4":mSkuRegItemVOMapMock,"sku3535":mSkuRegItemVOMapMock1],totEntries:5)
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver, isMyItemsCheckList, "1") >> registryItemsListVOMock
			
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"151515")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> null
			
			//setLTLAttributesInRegItem Public Method Coverage
			1 * eximManagerMock.getPersonalizedOptionsDisplayCode("percode") >> "displayCode"
			1 * catalogToolsMock.getDeliveryCharge(_, "1111", "ltlDelivery") >> 10d 
			1 * catalogToolsMock.isShippingMethodExistsForSku(siteId, "1111", "ltlDelivery", false) >> TRUE
			1 * catalogToolsMock.getShippingMethod("ltlDelivery") >> repositoryItemMock
			2 * repositoryItemMock.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION) >> "LTLShipping"
			
			1 * eximManagerMock.getPersonalizedOptionsDisplayCode("percode1") >> "displayCode1"
			1 * catalogToolsMock.getDeliveryCharge(_, "2222", "LWA") >> 20d
			1 * catalogToolsMock.isShippingMethodExistsForSku(siteId, "2222", "LW", true) >> FALSE
			1 * catalogToolsMock.getAssemblyCharge(_, "2222") >> 22d
			1 * catalogToolsMock.getShippingMethod("LWA") >> repositoryItemMock
			1 * repositoryItemMock.getRepositoryId() >> "sp23456"
			
				
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			1 * catalogToolsMock.getPromoBoxForRegistry("151515") >> promoBoxVOMock
			
			//fliterNotAvliableItem Public Method Coverage
			1 * catalogToolsMock.getParentProductForSku("8585", true)
			1 * catalogToolsMock.getParentProductForSku("9595", true)
			1 * catalogToolsMock.getParentProductForSku("2222", true)
			
			//checkPrice Private Method Coverage
			1 * catalogToolsMock.getParentProductForSku("8585", true) >> "prod11111"
			1 * catalogToolsMock.getSkuIncartFlag("8585") >> FALSE
			1 * catalogToolsMock.getSalePrice("prod11111","8585") >> 20d
			1 * catalogToolsMock.getListPrice("prod11111","8585") >> 28d
			
			1 * catalogToolsMock.getParentProductForSku("9595", true) >> "prod22222"
			1 * catalogToolsMock.getSkuIncartFlag("9595") >> TRUE
			1 * catalogToolsMock.getIncartPrice("prod22222", "9595") >> 2d
			1 * catalogToolsMock.getSalePrice("prod22222","9595") >> 20d
			1 * catalogToolsMock.getListPrice("prod22222","9595") >> 28d
			
			1 * catalogToolsMock.getParentProductForSku("2222", true) >> "prod33333"
			1 * catalogToolsMock.getSkuIncartFlag("2222") >> FALSE
			1 * catalogToolsMock.getSalePrice("prod33333","2222") >> 20d
			1 * catalogToolsMock.getListPrice("prod33333","2222") >> 28d
			
			//getlistRegistryItemVO Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("CertonaKeys", testObj.getTopRegMaxCount()) >> [""]
			RegistryCategoryMapVO registryCategoryMapVOMock = new RegistryCategoryMapVO()
			1 * catalogToolsMock.getCategoryForRegistry("151515") >> ["1":registryCategoryMapVOMock]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> ["true"]
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuId:"sku3535")
			SKUDetailVO skuDetailVOMock1 = new SKUDetailVO(skuId:"sku4545")
			SKUDetailVO skuDetailVOMock2 = new SKUDetailVO(skuId:"sku12345")
			1 * catalogToolsMock.sortSkubyRegistry(["8585", "9595", "2222"], null,"151515", siteId, BBBCatalogConstants.CATEGORY_SORT_TYPE,null) >> ["sku1":[skuDetailVOMock,skuDetailVOMock1],"sku2":[],"sku3":[skuDetailVOMock2]]
			
			//skuDetailsVO Private Method Coverage
			1 * catalogToolsMock.isSKUBelowLine(siteId,"sku3535") >> TRUE
			1 * catalogToolsMock.isSKUBelowLine(siteId,"sku4545") >> FALSE
			1 * catalogToolsMock.isSKUBelowLine(siteId,"sku12345") >> TRUE
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			mSkuRegItemVOMapMock.getPersonalizationOptionsDisplay().equals("displayCode")
			mSkuRegItemVOMapMock.getDeliverySurcharge().equals(10d)
			mSkuRegItemVOMapMock.isShipMethodUnsupported().equals(FALSE)
			mSkuRegItemVOMapMock.getLtlShipMethodDesc().equals("LTLShipping")
			mSkuRegItemVOMapMock.getLtlDeliveryServices().equals("ltlDelivery")
			mSkuRegItemVOMapMock1.getPersonalizationOptionsDisplay().equals("displayCode1")
			mSkuRegItemVOMapMock1.getDeliverySurcharge().equals(20d)
			mSkuRegItemVOMapMock1.isShipMethodUnsupported().equals(TRUE)
			mSkuRegItemVOMapMock1.getLtlShipMethodDesc().equals("LTLShipping With Assembly")
			mSkuRegItemVOMapMock1.getLtlDeliveryServices().equals("sp23456A")
			mSkuRegItemVOMapMock1.isDSLUpdateable().equals(TRUE)
			1 * testObj.logError('giftregistry_1011: p12452|false|Fatal error from service of RegistriesItemDisplayDroplet : Error Id is:900')
			1 * testObj.logError('Mock for BBBBusinessException', _)
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
			1 * requestMock.serviceParameter('error', requestMock, responseMock)
			1 * requestMock.setParameter('errorMsg', 'err_gift_reg_fatal_error')
			1 * requestMock.setParameter('categoryVOMap', ['1':registryCategoryMapVOMock])
			1 * requestMock.setParameter('certonaSkuList', '8585;9595;2222')
			1 * requestMock.setParameter('promoBox', promoBoxVOMock)
			1 * requestMock.setParameter('count', 3)
			1 * requestMock.setParameter('notInStockCategoryBuckets', ['1':null])
			1 * requestMock.setParameter('totEntries', 5)
			1 * requestMock.setParameter('categoryBuckets', ['1':null])
			1 * requestMock.setParameter('emptyOutOfStockListFlag', 'false')
			1 * requestMock.setParameter('sortSequence', '1')
			1 * requestMock.setParameter('inStockCategoryBuckets', ['1':null])
			1 * requestMock.setParameter('skuList', '8585;9595;2222')
			1 * testObj.logDebug(' RegistryItemsDisplayDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - ends')
	}
	
	def"service method. This TC is when isGiftGiver is true and errorId is 901 in processItemList Private Method"(){
		given:
			this.spyMethod()
			
			String siteId = "BedBathUS"; String isSecondCall = "FALSE";	String registryId = "23236562";	String c2id = "c2Id"; String c3id = "c3Id";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = TRUE; Boolean isMxGiftGiver = TRUE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = null; String view = null; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = FALSE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> [""]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"errorMessage",errorId:901)
			RegistryItemVO registryItemListMock = new RegistryItemVO(sku:8585l,personalisedCode:"",refNum:"refnum1",ltlDeliveryServices:"LTLDeliveries1")
			RegistryItemVO registryItemListMock1 = new RegistryItemVO(sku:9595l,personalisedCode:"",ltlDeliveryServices:"")
			RegistryItemVO registryItemListMock2 = new RegistryItemVO(sku:2222l,personalisedCode:"",ltlDeliveryServices:"LTL2",itemType:"LTL")
			RegistryItemVO mSkuRegItemVOMapMock = new RegistryItemVO(sku:0l,personalisedCode:"percode",refNum:"",ltlDeliveryServices:"ltlDelivery",assemblySelected:"",itemType:"LTLShipping")
			RegistryItemVO mSkuRegItemVOMapMock1 = new RegistryItemVO(sku:2222l,personalisedCode:"percode1",refNum:"",ltlDeliveryServices:"LWA",assemblySelected:"Y",itemType:"LTL",qtyPurchased:2)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,registryItemList: [registryItemListMock,registryItemListMock1,registryItemListMock2],
				mSkuRegItemVOMap:["sku12345_4":mSkuRegItemVOMapMock,"sku3535":mSkuRegItemVOMapMock1],totEntries:5)
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver, isMyItemsCheckList, "1") >> registryItemsListVOMock
			
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"151515")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> ["true"]
			
			//setLTLAttributesInRegItem Public Method Coverage
			1 * eximManagerMock.getPersonalizedOptionsDisplayCode("percode") >> "displayCode"
			1 * giftRegistryManagerMock.getNotifyRegistrantMsgType("", "12/12/2016") >> "displayMessageType"
			1 * catalogToolsMock.getDeliveryCharge(_, "", "ltlDelivery") >> 10d
			1 * catalogToolsMock.isShippingMethodExistsForSku(siteId, "", "ltlDelivery", false) >> FALSE
			1 * catalogToolsMock.getShippingMethod("ltlDelivery") >> repositoryItemMock
			2 * repositoryItemMock.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION) >> "LTLShipping"
			
			1 * eximManagerMock.getPersonalizedOptionsDisplayCode("percode1") >> "displayCode1"
			1 * giftRegistryManagerMock.getNotifyRegistrantMsgType("2222", "12/12/2016") >> "displayMessageType1"
			1 * catalogToolsMock.getDeliveryCharge(_, "2222", "LWA") >> 20d
			1 * catalogToolsMock.isShippingMethodExistsForSku(siteId, "2222", "LW", true) >> TRUE
			1 * catalogToolsMock.getAssemblyCharge(_, "2222") >> 22d
			1 * catalogToolsMock.getShippingMethod("LWA") >> repositoryItemMock
			1 * repositoryItemMock.getRepositoryId() >> "sp23456"
			
				
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			1 * catalogToolsMock.getPromoBoxForRegistry("151515") >> promoBoxVOMock
			
			//fliterNotAvliableItem Public Method Coverage
			1 * catalogToolsMock.getParentProductForSku("8585", true)
			1 * catalogToolsMock.getParentProductForSku("9595", true)
			1 * catalogToolsMock.getParentProductForSku("2222", true) >> {throw new Exception("Mock for Exception")}
			
			//checkPrice Private Method Coverage
			1 * catalogToolsMock.getParentProductForSku("8585", true) >> "prod11111"
			1 * catalogToolsMock.getSkuIncartFlag("8585") >> TRUE
			1 * catalogToolsMock.getIncartPrice("prod11111", "8585") >> 5d
			1 * catalogToolsMock.getSalePrice("prod11111","8585") >> 0.0d
			1 * catalogToolsMock.getListPrice("prod11111","8585") >> 28d
			
			1 * catalogToolsMock.getParentProductForSku("9595", true) >> "prod22222"
			1 * catalogToolsMock.getSkuIncartFlag("9595") >> TRUE
			1 * catalogToolsMock.getIncartPrice("prod22222", "9595") >> 2d
			1 * catalogToolsMock.getSalePrice("prod22222","9595") >> 0.0d
			1 * catalogToolsMock.getListPrice("prod22222","9595") >> 28d
			
			//getlistRegistryItemVO Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("CertonaKeys", testObj.getTopRegMaxCount()) >> [""]
			RegistryCategoryMapVO registryCategoryMapVOMock = new RegistryCategoryMapVO()
			1 * catalogToolsMock.getCategoryForRegistry("151515") >> ["1":registryCategoryMapVOMock]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> [""]
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuId:"sku3535")
			1 * catalogToolsMock.sortSkubyRegistry(["8585", "9595"], null,"151515", siteId, BBBCatalogConstants.CATEGORY_SORT_TYPE,null) >> ["1":[skuDetailVOMock]]
			
			//skuDetailsVO Private Method Coverage
			1 * catalogToolsMock.isSKUBelowLine(siteId,"sku3535") >> TRUE
			
			//enableBuyOffStartBrowsing Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "buyoff_start_browsing_key") >> ["true"]
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey("buyoffItemPurchasedLimit") >> ""
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey("buyoffItemPurchasedPercentageLimit") >> "6"
			
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			mSkuRegItemVOMapMock.getPersonalizationOptionsDisplay().equals("displayCode")
			mSkuRegItemVOMapMock.getDisplayNotifyRegistrantMsg().equals("displayMessageType")
			mSkuRegItemVOMapMock.getDeliverySurcharge().equals(10d)
			mSkuRegItemVOMapMock.isShipMethodUnsupported().equals(TRUE)
			mSkuRegItemVOMapMock.getLtlShipMethodDesc().equals("LTLShipping")
			mSkuRegItemVOMapMock.getLtlDeliveryServices().equals("ltlDelivery")
			mSkuRegItemVOMapMock1.getPersonalizationOptionsDisplay().equals("displayCode1")
			mSkuRegItemVOMapMock1.getDisplayNotifyRegistrantMsg().equals("displayMessageType1")
			mSkuRegItemVOMapMock1.getDeliverySurcharge().equals(20d)
			mSkuRegItemVOMapMock1.isShipMethodUnsupported().equals(FALSE)
			mSkuRegItemVOMapMock1.getLtlShipMethodDesc().equals("LTLShipping With Assembly")
			mSkuRegItemVOMapMock1.getLtlDeliveryServices().equals("sp23456A")
			1 * requestMock.setParameter('promoBox', promoBoxVOMock)
			1 * testObj.logError('giftregistry_1002: p12452|false|Either user token or site flag invalid from service of RegistryItemsDisplayDroplet : Error Id is:901')
			1 * requestMock.setParameter('showStartBrowsing', true)
			1 * requestMock.setParameter('sortSequence', '1')
			1 * requestMock.setParameter('categoryVOMap', ['1':registryCategoryMapVOMock])
			1 * requestMock.setParameter('omniProductList', '')
			1 * requestMock.setParameter('errorMsg', 'err_gift_reg_siteflag_usertoken_error')
			1 * requestMock.setParameter('totEntries', 5)
			1 * requestMock.setParameter('skuList', '8585;9595')
			1 * requestMock.setParameter('notInStockCategoryBuckets', ['1':[mSkuRegItemVOMapMock1]])
			1 * requestMock.setParameter('count', 2)
			1 * requestMock.setParameter('inStockCategoryBuckets', ['1':null])
			1 * requestMock.setParameter('emptyOutOfStockListFlag', 'false')
			1 * requestMock.setParameter('certonaSkuList', '8585;9595')
			1 * requestMock.setParameter('categoryBuckets', ['1':[mSkuRegItemVOMapMock1]])
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
			1 * requestMock.serviceParameter('error', requestMock, responseMock)
		
	}
	
	def"service method. This TC is when sortSeq is 2 and errorId is 902 and includes withDefaultPrice Private method"(){
		given:
			this.spyMethod()
			
			String siteId = "BedBathUS"; String isSecondCall = "FALSE";	String registryId = "23236562";	String c2id = "c2Id"; String c3id = "c3Id";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = FALSE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = "2"; String view = null; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12452","23236562","BedBathUS") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> ["true"]
			1 * catalogToolsMock.getAllValuesForKey("WSDLSiteFlags", siteId) >> null
			1 * catalogToolsMock.getAllValuesForKey("WSDLKeys", "WebServiceUserToken") >> null
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"errorMessage",errorId:902)
			RegistryItemVO registryItemListMock = new RegistryItemVO(sku:8585l,personalisedCode:"code1")
			RegistryItemVO mSkuRegItemVOMapMock = new RegistryItemVO(sku:1111l,personalisedCode:"percode",personlisedPrice:35d,ltlDeliveryServices:"LTLDeliveries1",refNum:"-1",assemblySelected:"N")
			RegistryItemVO mSkuRegItemVOMapMock1 = new RegistryItemVO(sku:2222l,personalisedCode:"percode1",personlisedPrice:20d)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,registryItemList: [registryItemListMock],
				mSkuRegItemVOMap:["sku12345_4":mSkuRegItemVOMapMock,"sku4545":mSkuRegItemVOMapMock1],totEntries:5)

			1 * giftRegistryManagerMock.fetchRegistryItems(_) >> registryItemsListVOMock
			
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"151515")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> null
				
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			1 * catalogToolsMock.getPromoBoxForRegistry("151515") >> promoBoxVOMock
			
			//getlistRegistryItemVO Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("CertonaKeys", testObj.getTopRegMaxCount()) >> [""]
			
			//withDefaultPrice Private Method Coverage
			sessionBeanMock.getValues().put("defaultUserCountryCode", "NJ")
			1 * catalogToolsMock.getPriceRanges("151515","NJ") >> ["15-25","100-250"]
			requestMock.getObjectParameter(PROFILE) >> profileMock
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuId:"sku3535")
			SKUDetailVO skuDetailVOMock1 = new SKUDetailVO(skuId:"sku4545")
			SKUDetailVO skuDetailVOMock2 = new SKUDetailVO(skuId:"sku12345")
			1 * catalogToolsMock.sortSkubyRegistry(null, [:], "151515", "BedBathUS", null,"NJ") >> ["sku1":[skuDetailVOMock,skuDetailVOMock1],"sku2":[],"sku3":[skuDetailVOMock2]] 
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> ["true"]
			
			RepositoryItem repositoryItemMock1 = Mock()
			1 * catalogToolsMock.getParentProductForSku("sku3535") >> "prod11111"
			1 * priceListManagerMock.getPriceList(profileMock, testObj.getProfilePriceListPropertyName()) >> repositoryItemMock
			1 * priceListManagerMock.getPrice(repositoryItemMock, "prod11111", "sku3535") >> repositoryItemMock1
			3 * repositoryItemMock1.getPropertyValue("listPrice") >> 51d
			1 * catalogToolsMock.isSKUBelowLine(siteId, "sku3535") >> FALSE
			
			1 * catalogToolsMock.getParentProductForSku("sku4545") >> "prod22222"
			1 * priceListManagerMock.getPriceList(profileMock, testObj.getProfilePriceListPropertyName()) >> repositoryItemMock
			1 * priceListManagerMock.getPrice(repositoryItemMock, "prod22222", "sku4545") >> repositoryItemMock1
			1 * catalogToolsMock.isSKUBelowLine(siteId, "sku4545") >> TRUE
			
			1 * catalogToolsMock.getParentProductForSku("sku12345") >> "prod33333"
			1 * priceListManagerMock.getPriceList(profileMock, testObj.getProfilePriceListPropertyName()) >> repositoryItemMock
			1 * priceListManagerMock.getPrice(repositoryItemMock, "prod33333", "sku12345") >> repositoryItemMock1
			1 * catalogToolsMock.isSKUBelowLine(siteId, "sku12345") >> TRUE
			
			//updateRegforLTL Private Method Coverage
			2 * catalogToolsMock.getDeliveryCharge(_,"1111", "LTLDeliveries1") >> 31d
			2 * catalogToolsMock.isShippingMethodExistsForSku(_, "1111", "LTLDeliveries1", false) >> TRUE
			2 * catalogToolsMock.getShippingMethod("LTLDeliveries1") >> repositoryItemMock
			2 * repositoryItemMock.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION) >> "shipMethodDescription"
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			sessionBeanMock.getValues().get("registryItems").equals(registryItemsListVOMock)
			mSkuRegItemVOMapMock.getRefNum().equals("")
			mSkuRegItemVOMapMock.getDeliverySurcharge().equals(31d)
			mSkuRegItemVOMapMock.isShipMethodUnsupported().equals(FALSE)
			mSkuRegItemVOMapMock.getLtlShipMethodDesc().equals("shipMethodDescription")
			1 * testObj.logError('giftregistry_1049: p12452|false|GiftRegistry input fields format error from processItemList() of RegistryItemsDisplayDroplet | webservice error code=902')
			1 * testObj.logError('Mock for BBBSystemException', _)
			1 * requestMock.setParameter('promoBox', promoBoxVOMock)
			1 * requestMock.setParameter('priceRangeList', ['15-25', '100-250'])
			1 * requestMock.setParameter('notInStockCategoryBuckets', _)
			1 * requestMock.setParameter('categoryBuckets', _)
			1 * requestMock.setParameter('count', 1)
			1 * requestMock.setParameter('inStockCategoryBuckets', _)
			1 * requestMock.setParameter('totEntries', 5)
			1 * requestMock.setParameter('sortSequence', '2')
			1 * requestMock.setParameter('emptyOutOfStockListFlag', 'false')
			1 * requestMock.setParameter('errorMsg', 'err_gift_reg_invalid_input_format')
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
			1 * requestMock.serviceParameter('error', requestMock, responseMock)
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.getAttribute('sessionBean')
			
	}
	

	def"service method. This TC is when sortSeq is 2 and errorId is 202 and country is empty"(){
		given:
			String siteId = "BedBathUS"; String isSecondCall = "FALSE";	String registryId = "23236562";	String c2id = "c2Id"; String c3id = "c3Id";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = FALSE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = "2"; String view = null; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12452","23236562","BedBathUS")
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> ["true"]
			1 * catalogToolsMock.getAllValuesForKey("WSDLSiteFlags", siteId) >> null
			1 * catalogToolsMock.getAllValuesForKey("WSDLKeys", "WebServiceUserToken") >> null
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorDisplayMessage:"",errorId:202)
			RegistryItemVO registryItemListMock = new RegistryItemVO(sku:8585l,personalisedCode:"code1")
			RegistryItemVO mSkuRegItemVOMapMock = new RegistryItemVO(sku:1111l,personalisedCode:"percode",personlisedPrice:15d,ltlDeliveryServices:null,refNum:"-1",
				assemblySelected:"N")
			RegistryItemVO mSkuRegItemVOMapMock1 = new RegistryItemVO(sku:2222l,personalisedCode:"percode1",personlisedPrice:20d,ltlDeliveryServices:null)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,registryItemList: [registryItemListMock],
				mSkuRegItemVOMap:["sku12345_4":mSkuRegItemVOMapMock,"sku4545":mSkuRegItemVOMapMock1],totEntries:5)

			1 * giftRegistryManagerMock.fetchRegistryItems(_) >> registryItemsListVOMock
			
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"151515")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> null
				
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			1 * catalogToolsMock.getPromoBoxForRegistry("151515") >> promoBoxVOMock
			
			//getlistRegistryItemVO Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("CertonaKeys", testObj.getTopRegMaxCount()) >> [""]
			
			//withDefaultPrice Private Method Coverage
			sessionBeanMock.getValues().put("defaultUserCountryCode", "")
			1 * catalogToolsMock.getPriceRanges("151515","") >> ["15-25","100-250"]
			requestMock.getObjectParameter(PROFILE) >> profileMock
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuId:"sku12345")
			SKUDetailVO skuDetailVOMock1 = new SKUDetailVO(skuId:"sku12345")
			SKUDetailVO skuDetailVOMock2 = new SKUDetailVO(skuId:"sku12345")
			SKUDetailVO skuDetailVOMock3 = new SKUDetailVO(skuId:"sku12345")
			1 * catalogToolsMock.sortSkubyRegistry(null, [:], "151515", "BedBathUS", null,"") >> ['$20_+$':[skuDetailVOMock],'70$208-52$72':[skuDetailVOMock1],
				'70$118-52$12':[skuDetailVOMock2],'$12_+$':[skuDetailVOMock3]]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> [""]
			
			RepositoryItem repositoryItemMock1 = Mock()
			4 * catalogToolsMock.getParentProductForSku("sku12345") >> "prod33333"
			4 * priceListManagerMock.getPriceList(profileMock, testObj.getProfilePriceListPropertyName()) >> repositoryItemMock
			4 * priceListManagerMock.getPrice(repositoryItemMock, "prod33333", "sku12345") >> repositoryItemMock1
			4 * catalogToolsMock.isSKUBelowLine(siteId, "sku12345") >> FALSE
			4 * repositoryItemMock1.getPropertyValue("listPrice") >> 51d
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			sessionBeanMock.getValues().get("registryItems").equals(registryItemsListVOMock)
			1 * requestMock.setParameter('count', 1)
			1 * requestMock.setParameter('inStockCategoryBuckets', _ )
			1 * requestMock.setParameter('totEntries', 5)
			1 * requestMock.setParameter('promoBox', promoBoxVOMock)
			1 * requestMock.setParameter('notInStockCategoryBuckets', _)
			1 * requestMock.setParameter('priceRangeList', ['15-25', '100-250'])
			1 * requestMock.setParameter('emptyOutOfStockListFlag', 'true')
			1 * requestMock.setParameter('sortSequence', '2')
			1 * requestMock.setParameter('categoryBuckets', _)
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.getAttribute('sessionBean')
			1 * requestMock.serviceParameter('output', requestMock,responseMock)
			
	}
	
	def"service method. This TC is when sortSeq is 2 and country is NJ in checkWithNull Private Method"(){
		given:
		
			String siteId = "BedBathUS"; String isSecondCall = "";	String registryId = "23236562";	String c2id = "c2Id"; String c3id = "";
			String c1id = ""; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = FALSE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = 2; String view = 1; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> ["false"]
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile('p12452', '23236562', 'BedBathUS')
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,
				registryItemList:[],mSkuRegItemVOMap:[:],totEntries:5)
			
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin(registryId, isGiftGiver, isMyItemsCheckList, view) >> registryItemsListVOMock
			
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"c12345")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> [""]
			
			//processItemList Private Method Coverage
			1 * catalogToolsMock.getPromoBoxForRegistry('c12345')
			1 * checkListManagerMock.showC1CategoryOnRlp(c1id, registryId, eventTypeCode) >> "show"
			requestMock.getParameter(FROM_CHECKLIST) >> "true"
			MyItemCategoryVO myItemCategoryVOMock = new MyItemCategoryVO(categoryId:"cat89895")
			MyItemCategoryVO myItemCategoryVOMock1 = new MyItemCategoryVO(categoryId:"cat99895")
			MyItemVO myItemVOMock = new MyItemVO(categoryListVO:[myItemCategoryVOMock,myItemCategoryVOMock1])
			1 * checkListManagerMock.getMyItemVO(registryId, eventTypeCode) >> myItemVOMock
			1 * checkListManagerMock.showC1CategoryOnRlp("cat89895", registryId, eventTypeCode) >> "notShow"
			1 * checkListManagerMock.showC1CategoryOnRlp("cat99895", registryId, eventTypeCode) >> "show"
			
			//calculateQty Private Method Coverage
			CategoryVO categoryVOMock4 = new CategoryVO(categoryId:"c2Id")
			CategoryVO categoryVOMock3 = new CategoryVO(childCategoryVO:[categoryVOMock4],categoryId:"c2Id",addedQuantity:5,suggestedQuantity:2)
			CategoryVO categoryVOMock2 = new CategoryVO(categoryId:"",childCategoryVO:[categoryVOMock3])
			CheckListVO checkListVOMock = new CheckListVO(categoryListVO:[categoryVOMock2])
			sessionBeanMock.setChecklistVO(checkListVOMock)
			
			//checkWithNull Private Method Coverage
			1 * catalogToolsMock.getPriceRanges("c12345",null) >> ['70-52',"100-250"]
			sessionBeanMock.getValues().put("defaultUserCountryCode","NJ")
			
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter('totEntries', 0)
			1 * requestMock.setParameter('priceRangeList', ['70-52', '100-250'])
			1 * requestMock.setParameter('addedCount', 5)
			1 * requestMock.setParameter('qtyof', '5 of 2')
			1 * requestMock.setParameter('emptyList', 'true')
			1 * requestMock.setParameter('categoryBuckets', ['100-250':null, '70-52':null])
			1 * requestMock.setParameter('promoBox', null)
			1 * requestMock.setParameter('sortSequence', '2')
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.getAttribute('sessionBean')
	}
	
	def"service method. This TC is when sortSeq is 2 and country is MX in checkWithNull Private Method"(){
		given:
		
			String siteId = "BedBathUS"; String isSecondCall = "";	String registryId = "23236562";	String c2id = ""; String c3id = "";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = FALSE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = 2; String view = 1; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> ["false"]
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile('p12452', '23236562', 'BedBathUS')
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,
				registryItemList:[],mSkuRegItemVOMap:[:],totEntries:5)
			
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin(registryId, isGiftGiver, isMyItemsCheckList, view) >> registryItemsListVOMock
			
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"c12345")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> [""]
			
			//processItemList Private Method Coverage
			1 * catalogToolsMock.getPromoBoxForRegistry('c12345')
			1 * checkListManagerMock.showC1CategoryOnRlp(c1id, registryId, eventTypeCode) >> "show"
			requestMock.getParameter(FROM_CHECKLIST) >> "true"
			MyItemCategoryVO myItemCategoryVOMock = new MyItemCategoryVO(categoryId:"cat89895")
			MyItemCategoryVO myItemCategoryVOMock1 = new MyItemCategoryVO(categoryId:"cat99895")
			MyItemVO myItemVOMock = new MyItemVO(categoryListVO:[myItemCategoryVOMock,myItemCategoryVOMock1])
			1 * checkListManagerMock.getMyItemVO(registryId, eventTypeCode) >> myItemVOMock
			
			//calculateQty Private Method Coverage
			CategoryVO categoryVOMock3 = new CategoryVO(childCategoryVO:[],categoryId:"c2I",addedQuantity:5,suggestedQuantity:2)
			CategoryVO categoryVOMock2 = new CategoryVO(categoryId:"c1Id",childCategoryVO:[categoryVOMock3])
			CheckListVO checkListVOMock = new CheckListVO(categoryListVO:[categoryVOMock2])
			sessionBeanMock.setChecklistVO(checkListVOMock)
			
			//checkWithNull Private Method Coverage
			1 * catalogToolsMock.getPriceRanges("c12345",null) >> ['70-52',"100-250"]
			sessionBeanMock.getValues().put("defaultUserCountryCode","MX")
			1 * catalogToolsMock.getPriceRanges("c12345","MX") >> ["1","2"]
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter('totEntries', 0)
			1 * requestMock.setParameter('emptyList', 'true')
			1 * requestMock.setParameter('promoBox', null)
			1 * requestMock.setParameter('priceRangeList', ['1', '2'])
			1 * requestMock.setParameter('qtyof', null)
			1 * requestMock.setParameter('ephCategoryBuckets', [])
			1 * requestMock.setParameter('sortSequence', '2')
			1 * requestMock.setParameter('categoryBuckets', ['2':null, '1':null])
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
			1 * requestMock.setAttribute('sessionBean',sessionBeanMock)
			1 * requestMock.getAttribute('sessionBean')
	}
	
	def"service method. This TC is when sortSeq is 2 and country is empty in checkWithNull Private Method"(){
		given:
		
			String siteId = "BedBathUS"; String isSecondCall = "";	String registryId = "23236562";	String c2id = ""; String c3id = "";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = TRUE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = 2; String view = 1; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> ["false"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,
				registryItemList:[],mSkuRegItemVOMap:[:],totEntries:5)
			
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin(registryId, isGiftGiver, isMyItemsCheckList, view) >> registryItemsListVOMock
			
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"c12345")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> [""]
			
			//processItemList Private Method Coverage
			1 * catalogToolsMock.getPromoBoxForRegistry('c12345')
			1 * checkListManagerMock.showC1CategoryOnRlp(c1id, registryId, eventTypeCode) >> "show"
			requestMock.getParameter(FROM_CHECKLIST) >> "true"
			MyItemVO myItemVOMock = new MyItemVO(categoryListVO:[])
			1 * checkListManagerMock.getMyItemVO(registryId, eventTypeCode) >> myItemVOMock
			
			//calculateQty Private Method Coverage
			CategoryVO categoryVOMock3 = new CategoryVO(childCategoryVO:[],categoryId:"c2I",addedQuantity:5,suggestedQuantity:2)
			CategoryVO categoryVOMock2 = new CategoryVO(categoryId:"",childCategoryVO:[categoryVOMock3])
			CheckListVO checkListVOMock = new CheckListVO(categoryListVO:[categoryVOMock2])
			sessionBeanMock.setChecklistVO(checkListVOMock)
			
			//checkWithNull Private Method Coverage
			1 * catalogToolsMock.getPriceRanges("c12345",null) >> ['70-52',"100-250"]
			sessionBeanMock.getValues().put("defaultUserCountryCode","")
			
			//enableBuyOffStartBrowsing Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "buyoff_start_browsing_key") >> null
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter('totEntries', 0)
			1 * requestMock.setParameter('showStartBrowsing', false)
			1 * requestMock.setParameter('qtyof', null)
			1 * requestMock.setParameter('categoryBuckets', ['100-250':null, '70-52':null])
			1 * requestMock.setParameter('sortSequence', '2')
			1 * requestMock.setParameter('omniProductList', '')
			1 * requestMock.setParameter('emptyList', 'true')
			1 * requestMock.setParameter('promoBox', null)
			1 * requestMock.setParameter('priceRangeList', ['70-52', '100-250'])
			1 * requestMock.getAttribute('sessionBean')
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.serviceParameter('output', requestMock,responseMock)
	}
	
	def"service method. This TC is when sortSeq is 2 and country is US in checkWithNull Private Method"(){
		given:
		
			String siteId = "BedBathUS"; String isSecondCall = "";	String registryId = "23236562";	String c2id = ""; String c3id = "";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = TRUE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = 2; String view = 1; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> ["false"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistryItemVO registryItemListMock = new RegistryItemVO(sku:8585l,personalisedCode:"code1",qtyRequested:4,qtyPurchased:1)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,
				registryItemList:[registryItemListMock],mSkuRegItemVOMap:[:],totEntries:5)
			
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin(registryId, isGiftGiver, isMyItemsCheckList, view) >> registryItemsListVOMock
			
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> [""]
			
			//processItemList Private Method Coverage
			1 * catalogToolsMock.getPromoBoxForRegistry('')
			1 * checkListManagerMock.showC1CategoryOnRlp(c1id, registryId, eventTypeCode) >> "show"
			requestMock.getParameter(FROM_CHECKLIST) >> "true"
			1 * checkListManagerMock.getMyItemVO(registryId, eventTypeCode) >> null
			
			//calculateQty Private Method Coverage
			CategoryVO categoryVOMock3 = new CategoryVO(childCategoryVO:[],categoryId:"c2I",addedQuantity:5,suggestedQuantity:2)
			CategoryVO categoryVOMock2 = new CategoryVO(categoryId:"",childCategoryVO:[categoryVOMock3])
			CheckListVO checkListVOMock = new CheckListVO(categoryListVO:[categoryVOMock2])
			sessionBeanMock.setChecklistVO(checkListVOMock)
			
			//checkWithNull Private Method Coverage
			1 * catalogToolsMock.getPriceRanges("",null) >> ['70-52',"100-250"]
			sessionBeanMock.getValues().put("defaultUserCountryCode","US")
			
			//enableBuyOffStartBrowsing Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "buyoff_start_browsing_key") >> ["true"]
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey("buyoffItemPurchasedLimit") >> "0"
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey("buyoffItemPurchasedPercentageLimit") >> "8"
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter('totEntries', 0)
			1 * requestMock.setParameter('sortSequence', '2')
			1 * requestMock.setParameter('showStartBrowsing', false)
			1 * requestMock.setParameter('emptyList', 'true')
			1 * requestMock.setParameter('promoBox', null)
			1 * requestMock.setParameter('priceRangeList', ['70-52', '100-250'])
			1 * requestMock.setParameter('omniProductList', '')
			1 * requestMock.setParameter('qtyof', null)
			1 * requestMock.setParameter('categoryBuckets', ['100-250':null, '70-52':null])
			1 * requestMock.getAttribute('sessionBean')
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.serviceParameter('output', requestMock,responseMock)
	}

	def"service method. This TC is when sortSeq is 2 and priceRangeList is null in checkWithNull Private Method"(){
		given:
		
			String siteId = "BedBathUS"; String isSecondCall = "";	String registryId = "23236562";	String c2id = ""; String c3id = "";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = TRUE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = 2; String view = 1; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> ["false"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistryItemVO registryItemListMock = new RegistryItemVO(sku:8585l,personalisedCode:"code1",qtyRequested:4,qtyPurchased:1)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,
				registryItemList:[registryItemListMock],mSkuRegItemVOMap:[:],totEntries:5)
			
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin(registryId, isGiftGiver, isMyItemsCheckList, view) >> registryItemsListVOMock
			
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> [""]
			
			//processItemList Private Method Coverage
			1 * catalogToolsMock.getPromoBoxForRegistry('')
			1 * checkListManagerMock.showC1CategoryOnRlp(c1id, registryId, eventTypeCode) >> "show"
			requestMock.getParameter(FROM_CHECKLIST) >> ""
			
			//calculateQty Private Method Coverage
			CategoryVO categoryVOMock3 = new CategoryVO(childCategoryVO:[],categoryId:"c2I",addedQuantity:5,suggestedQuantity:2)
			CategoryVO categoryVOMock2 = new CategoryVO(categoryId:"",childCategoryVO:[categoryVOMock3])
			CheckListVO checkListVOMock = new CheckListVO(categoryListVO:[categoryVOMock2])
			sessionBeanMock.setChecklistVO(checkListVOMock)
			
			//checkWithNull Private Method Coverage
			1 * catalogToolsMock.getPriceRanges("",null) >> null
			sessionBeanMock.getValues().put("defaultUserCountryCode","NJ")
			
			//enableBuyOffStartBrowsing Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "buyoff_start_browsing_key") >> ["true"]
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey("buyoffItemPurchasedLimit") >> "0"
			1 * giftRegistryManagerMock.getGiftRegistryConfigurationByKey("buyoffItemPurchasedPercentageLimit") >> "200"
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter('totEntries', 0)
			1 * requestMock.setParameter('sortSequence', '2')
			1 * requestMock.setParameter('categoryBuckets', [:])
			1 * requestMock.setParameter('showStartBrowsing', true)
			1 * requestMock.setParameter('priceRangeList', null)
			1 * requestMock.setParameter('emptyList', 'true')
			1 * requestMock.setParameter('promoBox', null)
			1 * requestMock.setParameter('omniProductList', '')
			1 * requestMock.setParameter('qtyof', null)
			1 * requestMock.getAttribute('sessionBean')
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.serviceParameter('output', requestMock,responseMock)
	}
	
	def"service method. This TC is when sortSeq is 2 and buyoff_start_browsing_key is empty in enableBuyOffStartBrowsing Private Method"(){
		given:
		
			String siteId = "BedBathUS"; String isSecondCall = "";	String registryId = "23236562";	String c2id = ""; String c3id = "";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = TRUE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = 2; String view = 1; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> ["false"]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistryItemVO registryItemListMock = new RegistryItemVO(sku:8585l,personalisedCode:"code1",qtyRequested:4,qtyPurchased:1)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,
				registryItemList:[registryItemListMock],mSkuRegItemVOMap:[:],totEntries:5)
			
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin(registryId, isGiftGiver, isMyItemsCheckList, view) >> registryItemsListVOMock
			
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> [""]
			
			//processItemList Private Method Coverage
			1 * catalogToolsMock.getPromoBoxForRegistry('')
			1 * checkListManagerMock.showC1CategoryOnRlp(c1id, registryId, eventTypeCode) >> "show"
			requestMock.getParameter(FROM_CHECKLIST) >> "false"
			
			//calculateQty Private Method Coverage
			CategoryVO categoryVOMock3 = new CategoryVO(childCategoryVO:[],categoryId:"c2I",addedQuantity:5,suggestedQuantity:2)
			CategoryVO categoryVOMock2 = new CategoryVO(categoryId:"",childCategoryVO:[categoryVOMock3])
			CheckListVO checkListVOMock = new CheckListVO(categoryListVO:[categoryVOMock2])
			sessionBeanMock.setChecklistVO(checkListVOMock)
			
			//checkWithNull Private Method Coverage
			1 * catalogToolsMock.getPriceRanges("",null) >> ['70-52',"100-250"]
			sessionBeanMock.getValues().put("defaultUserCountryCode","MX")
			
			//enableBuyOffStartBrowsing Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "buyoff_start_browsing_key") >> [""]
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.setParameter('totEntries', 0)
			1 * requestMock.setParameter('sortSequence', '2')
			1 * requestMock.setParameter('categoryBuckets', [:])
			1 * requestMock.setParameter('showStartBrowsing', false)
			1 * requestMock.setParameter('priceRangeList', null)
			1 * catalogToolsMock.getPriceRanges('', 'MX')
			1 * requestMock.setParameter('emptyList', 'true')
			1 * requestMock.setParameter('promoBox', null)
			1 * requestMock.setParameter('omniProductList', '')
			1 * requestMock.setParameter('qtyof', null)
			1 * requestMock.getAttribute('sessionBean')
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.serviceParameter('output', requestMock,responseMock)
	}
	
	def"service method. This TC is when isMxGiftGiver is true and checking fetchRegistryItems is executed with correct values"(){
		given:
		
			String siteId = "BedBathUS"; String isSecondCall = "";	String registryId = "23236562";	String c2id = ""; String c3id = "";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = TRUE; Boolean isMxGiftGiver = TRUE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = 2; String view = 1; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> ["true"]
			2 * catalogToolsMock.getAllValuesForKey("WSDLKeys", "WebServiceUserToken") >> ["true"]
			
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> [""]

		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * giftRegistryManagerMock.fetchRegistryItems({ RegistrySearchVO registrySearchVO -> registrySearchVO.siteId == "5" && registrySearchVO.userToken == "true" &&
				registrySearchVO.serviceName == "getRegistryItemList2" && registrySearchVO.registryId == registryId && registrySearchVO.view == 1 && 
				registrySearchVO.startIdx == 0 && registrySearchVO.blkSize == 12 &&registrySearchVO.giftGiver == TRUE && registrySearchVO.availForWebPurchaseFlag == TRUE})
			sessionBeanMock.getValues().get("registryItems").equals(null)
	}
	
	def"service method. This TC is when isMxGiftGiver is false and checking fetchRegistryItems is executed with correct values"(){
		given:
		
			String siteId = "BedBathUS"; String isSecondCall = "";	String registryId = "23236562";	String c2id = ""; String c3id = "";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = TRUE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = 2; String view = 1; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> ["true"]
			2 * catalogToolsMock.getAllValuesForKey("WSDLSiteFlags", siteId) >> ["BedBathUS"]
			2 * catalogToolsMock.getAllValuesForKey("WSDLKeys", "WebServiceUserToken") >> ["true"]
			
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> [""]

		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * giftRegistryManagerMock.fetchRegistryItems({ RegistrySearchVO registrySearchVO -> registrySearchVO.siteId == "BedBathUS" && registrySearchVO.userToken == "true" &&
				registrySearchVO.serviceName == "getRegistryItemList2" && registrySearchVO.registryId == registryId && registrySearchVO.view == 1 &&
				registrySearchVO.startIdx == 0 && registrySearchVO.blkSize == 12 &&registrySearchVO.giftGiver == TRUE && registrySearchVO.availForWebPurchaseFlag == TRUE})
			sessionBeanMock.getValues().get("registryItems").equals(null)
	}
	
	def"service method. This TC is when sortSeq is 2 and country is MX in withDefaultPrice Private Method"(){
		given:
			String siteId = "BedBathUS"; String isSecondCall = "FALSE";	String registryId = "23236562";	String c2id = "c2Id"; String c3id = "c3Id";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = FALSE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = "2"; String view = null; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12452","23236562","BedBathUS") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> [""]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistryItemVO registryItemListMock = new RegistryItemVO(sku:8585l,personalisedCode:"code1")
			RegistryItemVO mSkuRegItemVOMapMock = new RegistryItemVO(sku:1111l,personalisedCode:"percode",personlisedPrice:9d,ltlDeliveryServices:null,refNum:"-1",assemblySelected:"N")
			RegistryItemVO mSkuRegItemVOMapMock1 = new RegistryItemVO(sku:2222l,personalisedCode:"percode1",personlisedPrice:20d,ltlDeliveryServices:"LTLDA",refNum:"",assemblySelected:"Y")
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,registryItemList: [registryItemListMock],
				mSkuRegItemVOMap:["sku12345_4":mSkuRegItemVOMapMock,"sku4545":mSkuRegItemVOMapMock1],totEntries:5)

			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin(registryId, isGiftGiver, isMyItemsCheckList, "1") >> registryItemsListVOMock
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"151515")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> null
				
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			1 * catalogToolsMock.getPromoBoxForRegistry("151515") >> promoBoxVOMock
			
			//getlistRegistryItemVO Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("CertonaKeys", testObj.getTopRegMaxCount()) >> [""]
			
			//withDefaultPrice Private Method Coverage
			sessionBeanMock.getValues().put("defaultUserCountryCode", "MX")
			1 * catalogToolsMock.getPriceRanges("151515","MX") >> ['10$11-52$10',"100-250"]
			requestMock.getObjectParameter(PROFILE) >> profileMock
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuId:"sku12345")
			SKUDetailVO skuDetailVOMock1 = new SKUDetailVO(skuId:"sku4545")
			SKUDetailVO skuDetailVOMock2 = new SKUDetailVO(skuId:"sku12345")
			1 * catalogToolsMock.sortSkubyRegistry(null, [:], "151515", "BedBathUS", null,"MX") >> ["MXN":[skuDetailVOMock],"M_X":[skuDetailVOMock1],"":[skuDetailVOMock1],'10$11-52$10':[skuDetailVOMock2]]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> ["true"]
			
			RepositoryItem repositoryItemMock1 = Mock()
			4 * repositoryItemMock1.getPropertyValue('listPrice') >> "25"
			2 * catalogToolsMock.getParentProductForSku("sku12345") >> "prod33333"
			2 * priceListManagerMock.getPriceList(profileMock, testObj.getProfilePriceListPropertyName()) >> repositoryItemMock
			2 * priceListManagerMock.getPrice(repositoryItemMock, "prod33333", "sku12345") >> repositoryItemMock1
			2 * catalogToolsMock.isSKUBelowLine(siteId, "sku12345") >> FALSE
			
			2 * catalogToolsMock.getParentProductForSku("sku4545") >> "prod22222"
			2 * priceListManagerMock.getPriceList(profileMock, testObj.getProfilePriceListPropertyName()) >> repositoryItemMock
			2 * priceListManagerMock.getPrice(repositoryItemMock, "prod22222", "sku4545") >> repositoryItemMock1
			2 * catalogToolsMock.isSKUBelowLine(siteId, "sku4545") >> FALSE
			
			
			//updateRegforLTL Private Method Coverage
			RepositoryItem repositoryItemMock2 = Mock()
			2 * catalogToolsMock.getDeliveryCharge(_,"2222", "LTLDA") >> 31d
			4 * catalogToolsMock.isShippingMethodExistsForSku(_, BBBCoreConstants.BLANK+"2222",BBBCoreConstants.LW , true) >>> [TRUE,FALSE]
			4 * catalogToolsMock.getAssemblyCharge(_, "2222") >> 22d
			2 * catalogToolsMock.getShippingMethod("LTLDA") >> repositoryItemMock
			2 * repositoryItemMock.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION) >> "shipMethodDescription"
			2 * repositoryItemMock.getRepositoryId() >> "LW"
			
			2 * catalogToolsMock.getDeliveryCharge(_,"2222", "LWA") >> 32d
			2 * catalogToolsMock.getShippingMethod("LWA") >> repositoryItemMock2
			2 * repositoryItemMock2.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION) >> "shipMethodDescription"
			2 * repositoryItemMock2.getRepositoryId() >> "LTLD"
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			sessionBeanMock.getValues().get("registryItems").equals(registryItemsListVOMock)
			mSkuRegItemVOMapMock1.getRefNum().equals("")
			mSkuRegItemVOMapMock1.getDeliverySurcharge().equals(32d)
			mSkuRegItemVOMapMock1.isShipMethodUnsupported().equals(TRUE)
			mSkuRegItemVOMapMock1.getLtlShipMethodDesc().equals("shipMethodDescription With Assembly")
			mSkuRegItemVOMapMock1.getAssemblyFees().equals(22d)
			mSkuRegItemVOMapMock1.getLtlDeliveryServices().equals("LTLDA")
			1 * requestMock.setParameter('promoBox',  promoBoxVOMock)
			1 * requestMock.setParameter('inStockCategoryBuckets', _)
			1 * requestMock.setParameter('categoryBuckets', _)
			1 * requestMock.setParameter('count', 1)
			1 * requestMock.setParameter('priceRangeList', ['10$11-52$10', '100-250'])
			1 * requestMock.setParameter('emptyOutOfStockListFlag', 'true')
			1 * requestMock.setParameter('notInStockCategoryBuckets', ['MXN':null, 'M_X':null, '':null, '10$11-52$10':null, '100-250':null])
			1 * requestMock.setParameter('sortSequence', '2')
			1 * requestMock.setParameter('totEntries', 5)
			1 * requestMock.serviceParameter('output', requestMock,responseMock)
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.getAttribute('sessionBean')
			
	}
	
	def"service method. This TC is when sortSeq is 3 and BBBSystemException thrown in service method"(){
		given:
			this.spyMethod()
			
			String siteId = "BedBathUS"; String isSecondCall = "FALSE";	String registryId = "23236562";	String c2id = "c2Id"; String c3id = "c3Id";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = TRUE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = "3"; String view = null; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> [""]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuRepositoryItem:repositoryItemMock)
			RegistryItemVO registryItemListMock = new RegistryItemVO(sku:8585l,sKUDetailVO:skuDetailVOMock)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,registryItemList: [registryItemListMock],
				mSkuRegItemVOMap:[:],totEntries:5)
			
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME) >>> [[].toSet(),null]
			1 * catalogToolsMock.getListPrice(null,"8585") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin(registryId, isGiftGiver, isMyItemsCheckList, "1") >> registryItemsListVOMock
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"151515")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> null
				
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			1 * catalogToolsMock.getPromoBoxForRegistry("151515") >> promoBoxVOMock
			
			//getlistRegistryItemVO Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("CertonaKeys", testObj.getTopRegMaxCount()) >> [""]
						
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			sessionBeanMock.getValues().get("registryItems").equals(registryItemsListVOMock)
			1 * testObj.logError('BBBSystemException in serviceMethod of registry : 23236562 Exception is : com.bbb.exception.BBBSystemException: Mock for BBBSystemException')
			2 * requestMock.serviceParameter('error', requestMock, responseMock)
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
			1 * requestMock.setParameter('notInStockCategoryBuckets', [:])
			1 * requestMock.setParameter('inStockCategoryBuckets', [:])
			1 * requestMock.setParameter('totEntries', 5)
			1 * testObj.logDebug('pRegistryId[23236562]')
			1 * requestMock.setParameter('promoBox',  promoBoxVOMock)
			1 * requestMock.setParameter('sortSequence', '3')
			1 * requestMock.setParameter('categoryBuckets', _)
			1 * requestMock.setParameter('count', 1)
			
	}
	
	def"service method. This TC is when registryItemsListVO is null and BBBBusinessException thrown in service method"(){
		given:
			this.spyMethod()
			
			String siteId = "BedBathUS"; String isSecondCall = "FALSE";	String registryId = "23236562";	String c2id = "c2Id"; String c3id = "c3Id";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = TRUE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = "3"; String view = null; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> [""]
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin(registryId, isGiftGiver, isMyItemsCheckList, "1") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
						
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.serviceParameter('error', requestMock, responseMock)
			1 * testObj.logError('BBBBusinessException in serviceMethod of registry : 23236562 Exception is : com.bbb.exception.BBBBusinessException: Mock for BBBBusinessException')
			
	}
	
	def"service method. This TC is when registryItemsListVO has value and BBBBusinessException thrown in service method"(){
		given:
			this.spyMethod()
			
			String siteId = "BedBathUS"; String isSecondCall = "FALSE";	String registryId = "23236562";	String c2id = "c2Id"; String c3id = "c3Id";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = TRUE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = "3"; String view = null; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> [""]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:TRUE,errorId:200)
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuRepositoryItem:repositoryItemMock)
			RegistryItemVO registryItemListMock = new RegistryItemVO(sku:8585l,sKUDetailVO:skuDetailVOMock)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,registryItemList: [registryItemListMock],
				mSkuRegItemVOMap:[:],totEntries:5)
			
			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin(registryId, isGiftGiver, isMyItemsCheckList, "1") >> registryItemsListVOMock
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
						
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			1 * requestMock.serviceParameter('error', requestMock, responseMock)
			1 * testObj.logError('giftregistry_1079: p12452|false|BBBBusinessException from service of RegistriesItemDisplayDroplet : Error Id is:200', _)
			
	}
	
	def"service method. This TC is when sortSeq is 2 and country is US and RepositoryException thrown in withDefaultPrice Private Method"(){
		given:
			this.spyMethod()
			
			String siteId = "BedBathUS"; String isSecondCall = "FALSE";	String registryId = "23236562";	String c2id = "c2Id"; String c3id = "c3Id";
			String c1id = "c1Id"; int startIdx = 0;	int blkSize = 12; Boolean isGiftGiver = FALSE; Boolean isMxGiftGiver = FALSE; Boolean isAvailForWebPurchaseFlag = TRUE;
			String sortSeq = "2"; String view = null; String eventTypeCode = "c12345";	String regEventDate = "12/12/2016";	Boolean isMyItemsCheckList = TRUE;
		
			this.getParametersForService(requestMock, isSecondCall, registryId, c2id, c3id, c1id, sortSeq, view, eventTypeCode, startIdx, blkSize, isGiftGiver, isMxGiftGiver,
				isAvailForWebPurchaseFlag, siteId, regEventDate, isMyItemsCheckList)
			
			profileMock.getRepositoryId() >> "p12452"
			1 * giftRegistryManagerMock.isRegistryOwnedByProfile("p12452","23236562","BedBathUS")
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "RegItemsWSCall") >> [""]
			ServiceErrorVO serviceErrorVOMock = new ServiceErrorVO(errorExists:FALSE)
			RegistryItemVO registryItemListMock = new RegistryItemVO(sku:8585l,personalisedCode:"code1")
			RegistryItemVO mSkuRegItemVOMapMock = new RegistryItemVO(sku:1111l,personalisedCode:"percode",personlisedPrice:9d,ltlDeliveryServices:null,refNum:"-1",assemblySelected:"N")
			RegistryItemVO mSkuRegItemVOMapMock1 = new RegistryItemVO(sku:2222l,personalisedCode:"percode1",personlisedPrice:20d,ltlDeliveryServices:"LWA",refNum:"1",assemblySelected:"")
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(serviceErrorVO:serviceErrorVOMock,registryItemList: [registryItemListMock],
				mSkuRegItemVOMap:["sku12345_4":mSkuRegItemVOMapMock,"sku4545":mSkuRegItemVOMapMock1],totEntries:5)

			1 * giftRegistryManagerMock.fetchRegistryItemsFromEcomAdmin(registryId, isGiftGiver, isMyItemsCheckList, "1") >> registryItemsListVOMock
			RegistryTypeVO registryTypeVOMock = new RegistryTypeVO(registryCode:"code")
			RegistryTypeVO registryTypeVOMock1 = new RegistryTypeVO(registryCode:"c12345",registryTypeId:"151515")
			1 * catalogToolsMock.getRegistryTypes("BedBathUS") >> [registryTypeVOMock,registryTypeVOMock1]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> null
				
			//processItemList Private Method Coverage
			PromoBoxVO promoBoxVOMock = new PromoBoxVO()
			1 * catalogToolsMock.getPromoBoxForRegistry("151515") >> promoBoxVOMock
			
			//getlistRegistryItemVO Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("CertonaKeys", testObj.getTopRegMaxCount()) >> [""]
			
			//withDefaultPrice Private Method Coverage
			sessionBeanMock.getValues().put("defaultUserCountryCode", "US")
			1 * catalogToolsMock.getPriceRanges("151515","US") >> ['10$11-52$10',"100-250"]
			requestMock.getObjectParameter(PROFILE) >> profileMock
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuId:"sku12345")
			SKUDetailVO skuDetailVOMock1 = new SKUDetailVO(skuId:"sku4545")
			SKUDetailVO skuDetailVOMock2 = new SKUDetailVO(skuId:"sku25525")
			1 * catalogToolsMock.sortSkubyRegistry(null, [:], "151515", "BedBathUS", null,"US") >> ["M+XN":[skuDetailVOMock],'10$11-52$':[skuDetailVOMock],"":[skuDetailVOMock1],"sku":[skuDetailVOMock2]]
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> ["true"]
			
			RepositoryItem repositoryItemMock1 = Mock()
			3 * repositoryItemMock1.getPropertyValue('listPrice') >> "25"
			2 * catalogToolsMock.getParentProductForSku("sku12345") >> "prod33333"
			2 * priceListManagerMock.getPriceList(profileMock, testObj.getProfilePriceListPropertyName()) >> repositoryItemMock
			2 * priceListManagerMock.getPrice(repositoryItemMock, "prod33333", "sku12345") >> repositoryItemMock1
			2 * catalogToolsMock.isSKUBelowLine(siteId, "sku12345") >> FALSE
			
			1 * catalogToolsMock.getParentProductForSku("sku4545") >> "prod22222"
			1 * priceListManagerMock.getPriceList(profileMock, testObj.getProfilePriceListPropertyName()) >> repositoryItemMock
			1 * priceListManagerMock.getPrice(repositoryItemMock, "prod22222", "sku4545") >> repositoryItemMock1
			1 * catalogToolsMock.isSKUBelowLine(siteId, "sku4545") >> FALSE
			
			1 * catalogToolsMock.isShippingMethodExistsForSku(_, "2222", "LTLDA", false) >> FALSE
			
			1 * catalogToolsMock.getParentProductForSku("sku25525") >> "prod33333"
			1 * priceListManagerMock.getPriceList(profileMock, testObj.getProfilePriceListPropertyName()) >> repositoryItemMock
			1 * priceListManagerMock.getPrice(repositoryItemMock, "prod33333", "sku25525") >> {throw new RepositoryException("Mock for RepositoryException")}
			
			
			//updateRegforLTL Private Method Coverage
			RepositoryItem repositoryItemMock2 = Mock()
			1 * catalogToolsMock.getDeliveryCharge(_,"2222", "LTLDA") >> 31d
			1 * catalogToolsMock.isShippingMethodExistsForSku(_, BBBCoreConstants.BLANK+"2222",BBBCoreConstants.LW , true) >> TRUE
			1 * catalogToolsMock.getAssemblyCharge(_, "2222") >> 22d
			1 * catalogToolsMock.getShippingMethod("LTLDA") >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION) >> "shipMethodDescription"
			
			1 * catalogToolsMock.getDeliveryCharge(_,"2222", "LWA") >> 32d
			1 * catalogToolsMock.getShippingMethod("LWA") >> repositoryItemMock2
			1 * repositoryItemMock2.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION) >> "shipMethodDescription"
			1 * repositoryItemMock2.getRepositoryId() >> "LTLD"
			
		when:
			testObj.service(requestMock, responseMock)
		
		then:
			sessionBeanMock.getValues().get("registryItems").equals(registryItemsListVOMock)
			mSkuRegItemVOMapMock1.getRefNum().equals("1")
			mSkuRegItemVOMapMock1.getDeliverySurcharge().equals(31d)
			mSkuRegItemVOMapMock1.isShipMethodUnsupported().equals(TRUE)
			mSkuRegItemVOMapMock1.getLtlShipMethodDesc().equals("shipMethodDescription")
			mSkuRegItemVOMapMock1.getAssemblyFees().equals(22d)
			mSkuRegItemVOMapMock1.getLtlDeliveryServices().equals("LTLDA")
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
			1 * requestMock.setParameter('inStockCategoryBuckets', _)
			1 * requestMock.setAttribute('sessionBean', sessionBeanMock)
			1 * requestMock.setParameter('sortSequence', '2')
			1 * testObj.logError('giftregistry_1080: p12452|false|RepositoryException from getlistRegistryItemVO() of RegistryItemsDisplayDroplet', _)
			1 * requestMock.setParameter('count', 1)
			1 * requestMock.setParameter('categoryBuckets', _)
			1 * requestMock.setParameter('notInStockCategoryBuckets', ['M+XN':null, '10$11-52$':null, '':null])
			1 * requestMock.setParameter('totEntries', 5)
			1 * requestMock.getAttribute('sessionBean')
			1 * requestMock.setParameter('priceRangeList', ['10$11-52$10', '100-250'])
			1 * requestMock.setParameter('promoBox', promoBoxVOMock)
	}

	private spyMethod() {
		testObj = Spy()
		testObj.setGiftRegistryManager(giftRegistryManagerMock)
		testObj.setCatalogTools(catalogToolsMock)
		testObj.setCheckListManager(checkListManagerMock)
		testObj.setEximManager(eximManagerMock)
		testObj.setPriceListManager(priceListManagerMock)
		testObj.setTopRegMaxCount("TopRegMaxCount")
		testObj.setRegistryItemsListServiceName("getRegistryItemList2")
		testObj.setCertonaListMaxCount(4)
		testObj.setProfilePriceListPropertyName("priceList")
	}

	private getParametersForService(atg.servlet.DynamoHttpServletRequest requestMock, String isSecondCall, String registryId, String c2id, String c3id, String c1id, String sortSeq, String view, String eventTypeCode, int startIdx, int blkSize, Boolean isGiftGiver, Boolean isMxGiftGiver, Boolean isAvailForWebPurchaseFlag, String siteId, String regEventDate, Boolean isMyItemsCheckList) {
		requestMock.getParameter(IS_SECOND_CALL) >> isSecondCall
		requestMock.getParameter(REGISTRY_ID) >> registryId
		requestMock.getParameter(C2_ID) >> c2id
		requestMock.getParameter(C3_ID) >> c3id
		requestMock.getParameter(C1_ID) >> c1id
		requestMock.getParameter(SORT_SEQ) >> sortSeq
		requestMock.getParameter(VIEW) >> view
		requestMock.getParameter(REG_EVENT_TYPE_CODE) >> eventTypeCode
		requestMock.getParameter(START_INDEX) >> startIdx
		requestMock.getParameter(BULK_SIZE) >> blkSize
		requestMock.getParameter(IS_GIFT_GIVER) >> isGiftGiver
		requestMock.getParameter("isMxGiftGiver") >> isMxGiftGiver
		requestMock.getParameter(IS_AVAIL_WEBPUR) >> isAvailForWebPurchaseFlag
		requestMock.getParameter(SITE_ID_PARAM) >> siteId
		requestMock.getParameter(EVENT_DATE_PARAM) >> regEventDate
		requestMock.getParameter(IS_MY_ITEMS_CHECKLIST) >> isMyItemsCheckList
	}
	
	////////////////////////////////////TCs for service ends////////////////////////////////////////
	
	/////////////////////////////////TCs for setMyItemsForEPHNodes starts////////////////////////////////////////
	/* Signature : public void setMyItemsForEPHNodes(final DynamoHttpServletRequest pRequest, List<RegistryItemVO> listRegistryItemVO
							,String c2id,String c3id,String c1id,Map<String, RegistryItemVO> mSkuRegItemVOMap) */
	
	def"setMyItemsForEPHNodes. This TC is when cid's are empty and isGiftGiver is false"(){
		given:
			String c2id = ""
			String c3id = ""
			String c1id = ""
			RegistryItemVO registryItemVOMock = new RegistryItemVO()
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO()
			List<RegistryItemVO> listRegistryItemVO = [registryItemVOMock,registryItemVOMock1]
			RegistryItemVO registryItemVOMock2 = new RegistryItemVO()
			RegistryItemVO registryItemVOMock3 = new RegistryItemVO()
			Map<String, RegistryItemVO> mSkuRegItemVOMap = ["one":registryItemVOMock2,"two":registryItemVOMock3]
			
			requestMock.getParameter(EVENT_TYPE) >> "Wedding"
			requestMock.getParameter(REGISTRY_ID) >> "23231211"
			requestMock.getParameter(SITE_ID_PARAM) >> "BedBathUS"
			requestMock.getParameter(IS_GIFT_GIVER) >> FALSE
			requestMock.getParameter(EVENT_DATE_PARAM) >> "regEventDate"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> ["true"]
			MyItemCategoryVO myItemCategoryVOMock = new MyItemCategoryVO(categoryId:"cat12345")
			MyItemCategoryVO myItemCategoryVOMock1 = new MyItemCategoryVO(categoryId:"cat23456",registryItemsCount:0)
			MyItemCategoryVO myItemCategoryVOMock2 = new MyItemCategoryVO(categoryId:"cat34567",registryItemsCount:12,childCategoryVO:[])
			MyItemVO myItemVOMock = new MyItemVO(categoryListVO:[myItemCategoryVOMock,myItemCategoryVOMock1,myItemCategoryVOMock2])
			1 * checkListManagerMock.getMyItemVO("23231211", "Wedding") >> myItemVOMock
			
			1 * checkListManagerMock.showC1CategoryOnRlp("cat12345", "23231211", "Wedding") >> "notShow"
			1 * checkListManagerMock.showC1CategoryOnRlp("cat23456", "23231211", "Wedding") >> "show"
			1 * checkListManagerMock.showC1CategoryOnRlp("cat34567", "23231211", "Wedding") >> "show"
			
			RegistryItemVO registryItemVOMock4 =new RegistryItemVO()
			RegistryItemVO registryItemVOMock5 =new RegistryItemVO()
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:[registryItemVOMock4,registryItemVOMock5])
			sessionBeanMock.getValues().put("registryItems", registryItemsListVOMock)
			1 * checkListToolsMock.setEPHCategoryBuckets([registryItemVOMock,registryItemVOMock1], [myItemCategoryVOMock1,myItemCategoryVOMock2])
			
		when:
			testObj.setMyItemsForEPHNodes(requestMock, listRegistryItemVO , c2id, c3id, c1id, mSkuRegItemVOMap)
		then:
			1 * requestMock.setParameter('expandedCategory', 'cat34567')
			1 * requestMock.setParameter('ephCategoryBuckets', [myItemCategoryVOMock1,myItemCategoryVOMock2])
			sessionBeanMock.getValues().get("ephCategoryBuckets").equals([myItemCategoryVOMock1,myItemCategoryVOMock2])
			sessionBeanMock.getValues().get("registryItemsAll").equals([])
		
	}
	
	def"setMyItemsForEPHNodes. This TC is when c3id is empty and isGiftGiver is false"(){
		given:
			String c2id = "c2id"
			String c3id = ""
			String c1id = "c1id"
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:25512l,itemType:"LTL")
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:25512l,itemType:"LTL")
			List<RegistryItemVO> listRegistryItemVO = [registryItemVOMock,registryItemVOMock1]
			RegistryItemVO registryItemVOMock2 = new RegistryItemVO()
			RegistryItemVO registryItemVOMock3 = new RegistryItemVO()
			Map<String, RegistryItemVO> mSkuRegItemVOMap = ["one":registryItemVOMock2,"two":registryItemVOMock3]
			
			requestMock.getParameter(EVENT_TYPE) >> "Wedding"
			requestMock.getParameter(REGISTRY_ID) >> "23231211"
			requestMock.getParameter(SITE_ID_PARAM) >> "TBS"
			requestMock.getParameter(IS_GIFT_GIVER) >> FALSE
			requestMock.getParameter(EVENT_DATE_PARAM) >> "regEventDate"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> [""]
			MyItemCategoryVO myItemCategoryVOMock = new MyItemCategoryVO(categoryId:"cat12345")
			MyItemVO myItemVOMock = new MyItemVO(categoryListVO:[myItemCategoryVOMock])
			1 * checkListManagerMock.getMyItemVO("23231211", "Wedding") >> myItemVOMock
			
			RegistryItemVO registryItemVOMock4 =new RegistryItemVO(sku:25512l,personalisedCode:"",refNum:"-1",ltlDeliveryServices:"",itemType:"LTL")
			RegistryItemVO registryItemVOMock5 =new RegistryItemVO(sku:25545l,personalisedCode:"",refNum:"",ltlDeliveryServices:"",itemType:null)
			RegistryItemVO registryItemVOMock6 =new RegistryItemVO(sku:33222l)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:[registryItemVOMock4,registryItemVOMock5,registryItemVOMock6])
			sessionBeanMock.getValues().put("registryItems", registryItemsListVOMock)
			1 * checkListToolsMock.getSkuCategoriesListMap([registryItemVOMock4,registryItemVOMock5,registryItemVOMock6]) >> ["1":["sku1","sku2"]] 
			1 * checkListToolsMock.isSkuInCategory(["1":["sku1","sku2"]], "25512", c2id) >> TRUE
			1 * checkListToolsMock.isSkuInCategory(["1":["sku1","sku2"]], "25545", c2id) >> TRUE
			1 * checkListToolsMock.isSkuInCategory(["1":["sku1","sku2"]], "33222", c2id) >> FALSE
			1 * checkListManagerMock.showC1CategoryOnRlp(c1id, "23231211", "Wedding") >> "notShow"
			sessionBeanMock.getValues().put("checklistVO", null)
			
			//fliterNotAvliableItem Public Method Coverage
			1 * catalogToolsMock.getParentProductForSku("25512", true)
			1 * catalogToolsMock.getParentProductForSku("25545", true)
			
			//populateRegItemAttributes Public Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> ["true"]
			2 * giftRegistryToolsMock.setPriceInRegItem(_)
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuId:"sku23121")
			1 * giftRegistryToolsMock.getSKUDetailsWithProductId("TBS", "25512", _) >> skuDetailVOMock
			1 * giftRegistryToolsMock.getSKUDetailsWithProductId("TBS", "25545", _) >> skuDetailVOMock
			
			//setBelowLineAttribute Public method Coverage
			2 * catalogToolsMock.isSKUBelowLine("TBS", "sku23121") >>> [TRUE,FALSE]
			
		when:
			testObj.setMyItemsForEPHNodes(requestMock, listRegistryItemVO , c2id, c3id, c1id, mSkuRegItemVOMap)
		then:
			1 * requestMock.setParameter('qtyof', null)
			1 * requestMock.setParameter('other', true)
			1 * requestMock.setParameter('registryItemsAll', [registryItemVOMock4,registryItemVOMock5])
			sessionBeanMock.getValues().get("registryItemsAll").equals([registryItemVOMock4,registryItemVOMock5])
			registryItemVOMock4.getsKUDetailVO().equals(skuDetailVOMock)
			registryItemVOMock4.getRefNum().equals("")
			registryItemVOMock4.getIsBelowLineItem().equals("true")
			registryItemVOMock5.getsKUDetailVO().equals(skuDetailVOMock)
			registryItemVOMock5.getIsBelowLineItem().equals("false")
			
	}
	
	def"setMyItemsForEPHNodes. This TC is when isSkuInCategory is false and isGiftGiver is false"(){
		given:
			String c2id = ""
			String c3id = "c3id"
			String c1id = "c1id"
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:25512l,itemType:"LTL")
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:25512l,itemType:"LTL")
			List<RegistryItemVO> listRegistryItemVO = [registryItemVOMock,registryItemVOMock1]
			RegistryItemVO registryItemVOMock2 = new RegistryItemVO()
			RegistryItemVO registryItemVOMock3 = new RegistryItemVO()
			Map<String, RegistryItemVO> mSkuRegItemVOMap = ["one":registryItemVOMock2,"two":registryItemVOMock3]
			
			requestMock.getParameter(EVENT_TYPE) >> "Wedding"
			requestMock.getParameter(REGISTRY_ID) >> "23231211"
			requestMock.getParameter(SITE_ID_PARAM) >> "TBS"
			requestMock.getParameter(IS_GIFT_GIVER) >> FALSE
			requestMock.getParameter(EVENT_DATE_PARAM) >> "regEventDate"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> [""]
			MyItemCategoryVO myItemCategoryVOMock = new MyItemCategoryVO(categoryId:"cat12345")
			MyItemVO myItemVOMock = new MyItemVO(categoryListVO:[myItemCategoryVOMock])
			1 * checkListManagerMock.getMyItemVO("23231211", "Wedding") >> myItemVOMock
			
			RegistryItemVO registryItemVOMock4 =new RegistryItemVO(sku:25512l)
			RegistryItemVO registryItemVOMock5 =new RegistryItemVO(sku:25545l)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:[registryItemVOMock4,registryItemVOMock5])
			sessionBeanMock.getValues().put("registryItems", registryItemsListVOMock)
			1 * checkListToolsMock.getSkuCategoriesListMap([registryItemVOMock4,registryItemVOMock5]) >> ["1":["sku1","sku2"]]
			1 * checkListToolsMock.isSkuInCategory(["1":["sku1","sku2"]], "25512", c3id) >> FALSE
			1 * checkListToolsMock.isSkuInCategory(["1":["sku1","sku2"]], "25545", c3id) >> FALSE
			
			//calculateQty Private Method Coverage
			CheckListVO checkListVOMock = new CheckListVO(categoryListVO:null)
			sessionBeanMock.setChecklistVO(checkListVOMock)
			
			//populateRegItemAttributes Public Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> null
			
		when:
			testObj.setMyItemsForEPHNodes(requestMock, listRegistryItemVO , c2id, c3id, c1id, mSkuRegItemVOMap)
		then:
			1 * requestMock.setParameter('qtyof', null)
			1 * requestMock.setParameter('registryItemsAll', [])
			sessionBeanMock.getValues().get("registryItemsAll").equals([])
			
	}
	
	def"setMyItemsForEPHNodes. This TC is when isSkuInCategory is true and isGiftGiver is false"(){
		given:
			String c2id = ""
			String c3id = "c3id"
			String c1id = "c1id"
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:25512l,itemType:"LTL")
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:26232l,itemType:"LTL")
			List<RegistryItemVO> listRegistryItemVO = [registryItemVOMock,registryItemVOMock1]
			RegistryItemVO registryItemVOMock2 = new RegistryItemVO()
			RegistryItemVO registryItemVOMock3 = new RegistryItemVO()
			Map<String, RegistryItemVO> mSkuRegItemVOMap = ["one":registryItemVOMock2,"two":registryItemVOMock3]
			
			requestMock.getParameter(EVENT_TYPE) >> "Wedding"
			requestMock.getParameter(REGISTRY_ID) >> "23231211"
			requestMock.getParameter(SITE_ID_PARAM) >> "TBS"
			requestMock.getParameter(IS_GIFT_GIVER) >> FALSE
			requestMock.getParameter(EVENT_DATE_PARAM) >> "regEventDate"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> [""]
			MyItemCategoryVO myItemCategoryVOMock = new MyItemCategoryVO(categoryId:"cat12345")
			MyItemVO myItemVOMock = new MyItemVO(categoryListVO:[myItemCategoryVOMock])
			1 * checkListManagerMock.getMyItemVO("23231211", "Wedding") >> myItemVOMock
			
			RegistryItemVO registryItemVOMock4 =new RegistryItemVO(sku:25512l,personalisedCode:"",refNum:"-1",ltlDeliveryServices:"",itemType:"LTL",qtyPurchased:0)
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:[registryItemVOMock4])
			sessionBeanMock.getValues().put("registryItems", registryItemsListVOMock)
			1 * checkListToolsMock.getSkuCategoriesListMap([registryItemVOMock4]) >> ["1":["sku1","sku2"]]
			1 * checkListToolsMock.isSkuInCategory(["1":["sku1","sku2"]], "25512", c3id) >> TRUE
			1 * checkListManagerMock.showC1CategoryOnRlp(c1id, "23231211", "Wedding") >> "show"
			sessionBeanMock.setChecklistVO(null)
			
			//fliterNotAvliableItem Public Method Coverage
			1 * catalogToolsMock.getParentProductForSku("25512", true)
			//1 * catalogToolsMock.getParentProductForSku("25545", true)
			
			//populateRegItemAttributes Public Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> [""]
			1 * giftRegistryToolsMock.setPriceInRegItem(_)
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuId:"sku23121")
			1 * giftRegistryToolsMock.getSKUDetailsWithProductId("TBS", "25512", _) >> skuDetailVOMock
			
			//calculateQty Private Method Coverage
			CheckListVO checkListVOMock = new CheckListVO(categoryListVO:null)
			sessionBeanMock.setChecklistVO(checkListVOMock)
			
			//setBelowLineAttribute Public method Coverage
			1 * catalogToolsMock.isSKUBelowLine("TBS", "sku23121") >> TRUE
			
		when:
			testObj.setMyItemsForEPHNodes(requestMock, listRegistryItemVO , c2id, c3id, c1id, mSkuRegItemVOMap)
		then:
			1 * requestMock.setParameter('qtyof', null)
			1 * requestMock.setParameter('registryItemsAll', [registryItemVOMock4])
			sessionBeanMock.getValues().get("registryItemsAll").equals([registryItemVOMock4])
			registryItemVOMock4.getsKUDetailVO().equals(skuDetailVOMock)
			registryItemVOMock4.getRefNum().equals("")
			registryItemVOMock4.isDSLUpdateable().equals(TRUE)
			registryItemVOMock4.getIsBelowLineItem().equals("true")		
	}
	

	def"setMyItemsForEPHNodes. This TC is when registryItemsListVO is null"(){
		given:
			String c2id = ""
			String c3id = "c3id"
			String c1id = "c1id"
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:25512l,itemType:"LTL")
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:25512l,itemType:"LTL")
			List<RegistryItemVO> listRegistryItemVO = [registryItemVOMock,registryItemVOMock1]
			RegistryItemVO registryItemVOMock2 = new RegistryItemVO()
			RegistryItemVO registryItemVOMock3 = new RegistryItemVO()
			Map<String, RegistryItemVO> mSkuRegItemVOMap = ["one":registryItemVOMock2,"two":registryItemVOMock3]
			
			requestMock.getParameter(EVENT_TYPE) >> "Wedding"
			requestMock.getParameter(REGISTRY_ID) >> "23231211"
			requestMock.getParameter(SITE_ID_PARAM) >> "TBS"
			requestMock.getParameter(IS_GIFT_GIVER) >> FALSE
			requestMock.getParameter(EVENT_DATE_PARAM) >> "regEventDate"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> [""]
			MyItemCategoryVO myItemCategoryVOMock = new MyItemCategoryVO(categoryId:"cat12345")
			MyItemVO myItemVOMock = new MyItemVO(categoryListVO:[myItemCategoryVOMock])
			1 * checkListManagerMock.getMyItemVO("23231211", "Wedding") >> myItemVOMock
			sessionBeanMock.getValues().put("registryItems", null)
			
			//calculateQty Private Method Coverage
			sessionBeanMock.setChecklistVO(null)
			
			//populateRegItemAttributes Public Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> null
			
		when:
			testObj.setMyItemsForEPHNodes(requestMock, listRegistryItemVO , c2id, c3id, c1id, mSkuRegItemVOMap)
		then:
			1 * requestMock.setParameter('qtyof', null)
			1 * requestMock.setParameter('registryItemsAll', null)
			sessionBeanMock.getValues().get("registryItemsAll").equals(null)
			
	}
	
	def"setMyItemsForEPHNodes. This TC is when isGiftGiver is true"(){
		given:
			String c2id = ""
			String c3id = ""
			String c1id = "c1Id"
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:25512l)
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:5225l)
			List<RegistryItemVO> listRegistryItemVO = [registryItemVOMock,registryItemVOMock1]
			RegistryItemVO registryItemVOMock2 = new RegistryItemVO()
			RegistryItemVO registryItemVOMock3 = new RegistryItemVO()
			Map<String, RegistryItemVO> mSkuRegItemVOMap = ["one":registryItemVOMock2,"two":registryItemVOMock3]
			
			requestMock.getParameter(EVENT_TYPE) >> "Wedding"
			requestMock.getParameter(REGISTRY_ID) >> "23231211"
			requestMock.getParameter(SITE_ID_PARAM) >> "BedBathUS"
			requestMock.getParameter(IS_GIFT_GIVER) >> TRUE
			requestMock.getParameter(EVENT_DATE_PARAM) >> "regEventDate"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> ["true"]
			MyItemCategoryVO myItemCategoryVOMock = new MyItemCategoryVO(categoryId:"cat12345")
			MyItemCategoryVO myItemCategoryVOMock1 = new MyItemCategoryVO(categoryId:"cat245321")
			MyItemCategoryVO myItemCategoryVOMock2 = new MyItemCategoryVO(categoryId:"cat34564",registryItemsCount:0)
			MyItemCategoryVO myItemCategoryVOMock3 = new MyItemCategoryVO(categoryId:"cat44567",registryItemsCount:12,childCategoryVO:[])
			
			MyItemVO myItemVOMock = new MyItemVO(categoryListVO:[myItemCategoryVOMock,myItemCategoryVOMock1])
			
			MyItemVO myItemVOMock1 = new MyItemVO(categoryListVO:[myItemCategoryVOMock])
			
			MyItemVO myItemVOMock2 = new MyItemVO(categoryListVO:[myItemCategoryVOMock])
			
			3 * checkListManagerMock.getMyItemVO("23231211", "Wedding") >>> [myItemVOMock,myItemVOMock1,myItemVOMock2]
			
			1 * checkListManagerMock.showC1CategoryOnRlp("cat12345", "23231211", "Wedding") >> "notShow"
			1 * checkListManagerMock.showC1CategoryOnRlp("cat245321", "23231211", "Wedding") >> "show"
			1 * checkListToolsMock.setGiftGiverEPHBuckets([registryItemVOMock], [])
			1 * checkListToolsMock.setGiftGiverEPHBuckets([registryItemVOMock1], [])
			
			//fliterNotAvliableItem Public Method Coverage
			1 * catalogToolsMock.getParentProductForSku("25512", true)
			1 * catalogToolsMock.getParentProductForSku("5225", true)
			
			//populateRegItemAttributes Public Method Coverage
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "notifyRegistrantFlag") >> [""]
			
			//populateRegItemAttributes
			2 * giftRegistryToolsMock.setPriceInRegItem(_)
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuId:"sku23121")
			1 * giftRegistryToolsMock.getSKUDetailsWithProductId("BedBathUS", "25512", _) >> skuDetailVOMock
			1 * giftRegistryToolsMock.getSKUDetailsWithProductId("BedBathUS", "5225", _) >> skuDetailVOMock
			
			//setBelowLineAttribute Public method Coverage
			2 * catalogToolsMock.isSKUBelowLine("BedBathUS", "sku23121") >>> [TRUE,FALSE]
		
			
		when:
			testObj.setMyItemsForEPHNodes(requestMock, listRegistryItemVO , c2id, c3id, c1id, mSkuRegItemVOMap)
		then:
			1 * requestMock.setParameter(BBBGiftRegistryConstants.IN_STOCK_EPH_BUCKETS, [])
			1 * requestMock.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_EPH_BUCKETS, [])
		
	}
	
	def"setMyItemsForEPHNodes. This TC is when c1Categories is empty"(){
		given:
			String c2id = ""
			String c3id = ""
			String c1id = "c1Id"
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:25512l)
			List<RegistryItemVO> listRegistryItemVO = [registryItemVOMock]
			RegistryItemVO registryItemVOMock2 = new RegistryItemVO()
			Map<String, RegistryItemVO> mSkuRegItemVOMap = ["one":registryItemVOMock2]
			
			requestMock.getParameter(EVENT_TYPE) >> "Wedding"
			requestMock.getParameter(REGISTRY_ID) >> "23231211"
			requestMock.getParameter(SITE_ID_PARAM) >> "BedBathUS"
			requestMock.getParameter(IS_GIFT_GIVER) >> TRUE
			requestMock.getParameter(EVENT_DATE_PARAM) >> "regEventDate"
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> ["true"]
			MyItemVO myItemVOMock = new MyItemVO(categoryListVO:[])
			1 * checkListManagerMock.getMyItemVO("23231211", "Wedding") >>> [myItemVOMock]	
			
		expect:
			testObj.setMyItemsForEPHNodes(requestMock, listRegistryItemVO , c2id, c3id, c1id, mSkuRegItemVOMap)
	
	}
	
	////////////////////////////////////TCs for setMyItemsForEPHNodes ends////////////////////////////////////////
	
	/////////////////////////////////TCs for setRegItemsSecondCall starts////////////////////////////////////////
	////// Signature : public void setRegItemsSecondCall(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) /////

	def"setRegItemsSecondCall. This TC is when c1id has value and registryItemsAll is null"(){
		given:
			requestMock.getParameter(EVENT_DATE_PARAM) >> "01/01/2017"
			requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> "cat12345"
			requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			requestMock.getParameter(C1_ID) >> "c1Id"
			sessionBeanMock.getValues().put("registryItemsAll",null)
		
		when:
			testObj.setRegItemsSecondCall(requestMock, responseMock)
		then:
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"setRegItemsSecondCall. This TC is when c1id, registryItemsAll are empty and registryItemsList is null"(){
		given:
			requestMock.getParameter(EVENT_DATE_PARAM) >> "01/01/2017"
			requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> "cat12345"
			requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			requestMock.getParameter(C1_ID) >> ""
			RegistryItemVO registryItemVOMock = new RegistryItemVO()
			sessionBeanMock.getValues().put("registryItemsAll",[])
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> [""]
			MyItemCategoryVO myItemCategoryVOMock = new MyItemCategoryVO(categoryId:"cat8988")
			MyItemCategoryVO myItemCategoryVOMock1 = new MyItemCategoryVO(categoryId:"cat12354")
			sessionBeanMock.getValues().put("ephCategoryBuckets",[myItemCategoryVOMock,myItemCategoryVOMock1])
			RegistryItemsListVO registryItemsListVOMock = new RegistryItemsListVO(registryItemList:null)
			sessionBeanMock.getValues().put("registryItems",registryItemsListVOMock)
		
		when:
			testObj.setRegItemsSecondCall(requestMock, responseMock)
		then:
			sessionBeanMock.getValues().get("ephCategoryBuckets").equals(null)
			sessionBeanMock.getValues().get("registryItems").equals(null)
			sessionBeanMock.getValues().get("registryItemsAll").equals(null)
			1 * requestMock.setParameter('ephCategoryBuckets', [])
			1 * requestMock.serviceParameter("output", requestMock, responseMock)
	}
	
	def"setRegItemsSecondCall. This TC is when ephCategoryBuckets is empty and BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			requestMock.getParameter(EVENT_DATE_PARAM) >> "01/01/2017"
			requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> "cat12345"
			requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			requestMock.getParameter(C1_ID) >> ""
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sKUDetailVO:null)
			sessionBeanMock.getValues().put("registryItemsAll",[registryItemVOMock])
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> [""]
			sessionBeanMock.getValues().put("ephCategoryBuckets",[])
			
			testObj.setCertonaSkuList(requestMock, null, [], null, _, 0, null) >> {throw new BBBSystemException("Mock for BBBSystemException")}
		
		when:
			testObj.setRegItemsSecondCall(requestMock, responseMock)
		then:
			sessionBeanMock.getValues().get("ephCategoryBuckets").equals(null)
			sessionBeanMock.getValues().get("registryItemsAll").equals([registryItemVOMock])
			1 * requestMock.setParameter('itemList', '')
			1 * testObj.logError('BBBSystemException in setRegItemsSecondCall method of RegistryItemsDisplayDroplet ', _)
	}
	
	def"setRegItemsSecondCall. This TC is when ephCategoryBuckets is null and BBBBusinessException thrown"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			requestMock.getParameter(EVENT_DATE_PARAM) >> "01/01/2017"
			requestMock.getParameter(BBBCoreConstants.PARAM_CATEGORY_ID) >> "cat12345"
			requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "BedBathUS"
			requestMock.getParameter(C1_ID) >> ""
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sKUDetailVO:null)
			sessionBeanMock.getValues().put("registryItemsAll",[registryItemVOMock])
			1 * catalogToolsMock.getAllValuesForKey("FlagDrivenFunctions", "enableLTLRegForSite") >> [""]
			sessionBeanMock.getValues().put("ephCategoryBuckets",null)
			
			testObj.setCertonaSkuList(requestMock, null, [], null, _, 0, null) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		
		when:
			testObj.setRegItemsSecondCall(requestMock, responseMock)
		then:
			sessionBeanMock.getValues().get("ephCategoryBuckets").equals(null)
			sessionBeanMock.getValues().get("registryItemsAll").equals([registryItemVOMock])
			1 * requestMock.setParameter('itemList', '')
			1 * testObj.logError('BBBBusinessException in setRegItemsSecondCall method of RegistryItemsDisplayDroplet ', _)
	}
	
	////////////////////////////////////TCs for setRegItemsSecondCall ends////////////////////////////////////////
	
	
	/////////////////////////////////TCs for setRegistryItemDetailsInC1 starts////////////////////////////////////////
	/* Signature : public void setRegistryItemDetailsInC1(MyItemCategoryVO categoryVO, String siteId, boolean enableLTLRegForSite, String regEventDate, 
								List<RegistryItemVO> registryItemsList,	List<RegistryItemVO> registryItemsAll) */
	

	def"setRegistryItemDetailsInC1. This TC is the Happy flow of setRegistryItemDetailsInC1 method"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			String siteId = "BedBathUS"
			Boolean enableLTLRegForSite = TRUE
			String regEventDate = "01/01/2016"
			RegistryItemVO registryItemVOMock = new RegistryItemVO()
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO()
			RegistryItemVO registryItemVOMock2 = new RegistryItemVO()
			RegistryItemVO registryItemVOMock3 = new RegistryItemVO()
			List<RegistryItemVO> registryItemsAllMock = [registryItemVOMock2,registryItemVOMock3]
			
			RegistryItemVO c2RegItemsMock = new RegistryItemVO()
			RegistryItemVO c2RegItemsMock1 = new RegistryItemVO()
			RegistryItemVO c3RegItemsMock = new RegistryItemVO()
			RegistryItemVO c3RegItemsMock1 = new RegistryItemVO()
			MyItemCategoryVO c3CategoriesListMock = new MyItemCategoryVO(registryItems:[c3RegItemsMock,c3RegItemsMock1])
			MyItemCategoryVO c3CategoriesListMock1 = new MyItemCategoryVO(registryItems:[c3RegItemsMock,c3RegItemsMock1])
			MyItemCategoryVO myItemCategoryVOMock1 = new MyItemCategoryVO(registryItems:[c2RegItemsMock,c2RegItemsMock1],categoryId:"NotAll",childCategoryVO:[])
			MyItemCategoryVO myItemCategoryVOMock2 = new MyItemCategoryVO(registryItems:[c2RegItemsMock,c2RegItemsMock1],categoryId:"All",childCategoryVO:[c3CategoriesListMock,c3CategoriesListMock1])
			RegistryItemVO c1RegItemsMock = new RegistryItemVO()
			RegistryItemVO c1RegItemsMock1 = new RegistryItemVO()
			MyItemCategoryVO myItemCategoryVOMock = new MyItemCategoryVO(childCategoryVO:[myItemCategoryVOMock1,myItemCategoryVOMock2],categoryId:"other",registryItems:[c1RegItemsMock,c1RegItemsMock1])
			
			5 * testObj.populateRegItemAttributes(*_) >> null
		
		when:
			testObj.setRegistryItemDetailsInC1(myItemCategoryVOMock, SITE_ID, enableLTLRegForSite, EVENT_DATE, [registryItemVOMock,registryItemVOMock1], registryItemsAllMock)
		then:
			myItemCategoryVOMock.getRegistryItemsCount().equals(2)
			myItemCategoryVOMock1.getRegistryItemsCount().equals(2)
			myItemCategoryVOMock2.getRegistryItemsCount().equals(6)
			registryItemsAllMock.size().equals(10)
			
	}
	
	def"setRegistryItemDetailsInC1. This TC is when registryItemsAll is passed as null"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			String siteId = "BedBathUS"
			Boolean enableLTLRegForSite = TRUE
			String regEventDate = "01/01/2016"
			RegistryItemVO registryItemVOMock = new RegistryItemVO()
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO()
			
			List<RegistryItemVO> registryItemsAllMock = null
			
			RegistryItemVO c2RegItemsMock = new RegistryItemVO()
			RegistryItemVO c2RegItemsMock1 = new RegistryItemVO()
			RegistryItemVO c3RegItemsMock = new RegistryItemVO()
			RegistryItemVO c3RegItemsMock1 = new RegistryItemVO()
			MyItemCategoryVO c3CategoriesListMock = new MyItemCategoryVO(registryItems:[c3RegItemsMock,c3RegItemsMock1])
			MyItemCategoryVO c3CategoriesListMock1 = new MyItemCategoryVO(registryItems:[c3RegItemsMock,c3RegItemsMock1])
			MyItemCategoryVO myItemCategoryVOMock1 = new MyItemCategoryVO(registryItems:[c2RegItemsMock,c2RegItemsMock1],categoryId:"NotAll",childCategoryVO:null)
			MyItemCategoryVO myItemCategoryVOMock2 = new MyItemCategoryVO(registryItems:[c2RegItemsMock,c2RegItemsMock1],categoryId:"All",childCategoryVO:[c3CategoriesListMock,c3CategoriesListMock1])
			RegistryItemVO c1RegItemsMock = new RegistryItemVO()
			RegistryItemVO c1RegItemsMock1 = new RegistryItemVO()
			MyItemCategoryVO myItemCategoryVOMock = new MyItemCategoryVO(childCategoryVO:[myItemCategoryVOMock1,myItemCategoryVOMock2],categoryId:"other",registryItems:[c1RegItemsMock,c1RegItemsMock1])
			
			5 * testObj.populateRegItemAttributes(*_) >> null
		
		when:
			testObj.setRegistryItemDetailsInC1(myItemCategoryVOMock, SITE_ID, enableLTLRegForSite, EVENT_DATE, [registryItemVOMock,registryItemVOMock1], registryItemsAllMock)
		then:
			myItemCategoryVOMock.getRegistryItemsCount().equals(2)
			myItemCategoryVOMock1.getRegistryItemsCount().equals(2)
			myItemCategoryVOMock2.getRegistryItemsCount().equals(6)
			registryItemsAllMock == null
			
	}
	
	def"setRegistryItemDetailsInC1. This TC is when c2CategoriesList is null"(){
		given:
			String siteId = "BedBathUS"
			Boolean enableLTLRegForSite = TRUE
			String regEventDate = "01/01/2016"
			MyItemCategoryVO myItemCategoryVOMock = new MyItemCategoryVO(childCategoryVO:null,categoryId:"all")
		
		expect:
			testObj.setRegistryItemDetailsInC1(myItemCategoryVOMock, SITE_ID, enableLTLRegForSite, EVENT_DATE, [], [])
			
	}
	
	////////////////////////////////////TCs for setRegistryItemDetailsInC1 ends////////////////////////////////////////
	
	def"setFirstEPHBucket. This TC is when c1CategoriesOnRLP is passed as empty"(){
		given:
		
		expect:
			testObj.setFirstEPHBucket(requestMock, "BedBathUS", FALSE, [], [], [])
	}
	
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
