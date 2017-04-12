package com.bbb.commerce.browse.droplet

import java.util.List;
import java.util.Map;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.utils.BBBUtility
import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.browse.manager.ProductManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.AttributeVO
import com.bbb.commerce.catalog.vo.CollectionProductVO
import com.bbb.commerce.catalog.vo.ImageVO
import com.bbb.commerce.catalog.vo.MediaVO;
import com.bbb.commerce.catalog.vo.ProductVO
import com.bbb.commerce.catalog.vo.RollupTypeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.catalog.vo.TabVO
import com.bbb.commerce.inventory.BBBInventoryManager
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.search.bean.result.BBBDynamicPriceVO

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class EverLivingDetailsDropletSpecification extends BBBExtendedSpec {
	
	EverLivingDetailsDroplet testObj
	ProductManager productManagerMock = Mock()
	BBBInventoryManager inventoryManagerMock = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	private static final String SIZE = "SIZE";
	private static final String IS_MAIN_PRODUCT = "isMainProduct";
	public static final String SKUDETAILVO = "pSKUDetailVO";
	public static final String MEDIAVO = "mediaVO";
	public static final String PRODUCTVO = "productVO";
	public static final String COLLECTIONVO = "collectionVO";
	public static final String DEFAULTCHILDSKU = "pDefaultChildSku";
	public static final String FIRSTATTRIBUTSVOLIST = "pFirstAttributsVOList";
	public static final String FIRSTCHILDSKU = "pFirstChildSKU";
	public static final String PRODUCTTAB = "productTabs";
	public static final String CATEGORYID = "categoryId";
	public static final String PLACEHOLDER = "PDP";
	public static final String PRODUCT_ID_PARAMETER = "id";
	public final static String OPARAM_OUTPUT="output";
	public final static String OPARAM_ERROR="error";
	public final static String TABLOOKUP="tabLookUp";
	public final static String SKUID="skuId";
	public final static String REGISTRYID="registryId";
	public final static String INSTOCK="inStock";
	private static final String LINK_STRING = "linkStringNonRecproduct";
	private static final String RKGCOLLECTION_PROD_IDS = "rkgCollectionProdIds";
	
	def setup(){
		testObj = new EverLivingDetailsDroplet(inventoryManager:inventoryManagerMock,productManager:productManagerMock,providerID:"12")
		BBBUtility.setCatalogTools(catalogToolsMock)
	}
	
	/////////////////////////////////TCs for service starts////////////////////////////////////////
	//Signature : public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) ////
	
	def"service method.This TC is Happy flow when productVO's isCollection is false"(){
		given:
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prod12345"
			requestMock.getParameter(SKUID) >> "sku12345"
			requestMock.getObjectParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(REGISTRYID) >> "22323212"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			
			ImageVO imageVOMock = new ImageVO()
			TabVO tabVOMock = new TabVO()
			TabVO tabVOMock1 = new TabVO()
			RollupTypeVO rollupTypeVOMock2 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock3 = new RollupTypeVO()
			ProductVO productVOMock = new ProductVO(collection:FALSE,productId:"prod12345",childSKUs:["sku12345","sku56789"],productImages:imageVOMock,
				productTabs:[tabVOMock,tabVOMock1],rollupAttributes:["SIZE":[rollupTypeVOMock2],"NONE":[rollupTypeVOMock3]]) 
			
			1 * productManagerMock.getEverLivingMainProductDetails("BedBathUS", "prod12345") >> productVOMock
			MediaVO mediaVOMock = new MediaVO(providerId:"6") 
			MediaVO mediaVOMock1 = new MediaVO(providerId:"12")
			1 * productManagerMock.getMediaDetails("prod12345", "BedBathUS") >> [mediaVOMock,mediaVOMock1]
			requestMock.getParameter("color") >> "green"
			requestMock.getParameter("checkInventory") >> TRUE
			1 * inventoryManagerMock.getEverLivingProductAvailability("BedBathUS", "sku12345", BBBInventoryManager.PRODUCT_DISPLAY) >> 0
			AttributeVO attributeVOMock = new AttributeVO()
			ImageVO imageVOMock1 = new ImageVO()
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuBelowLine:TRUE,displayName:"Dinnerware Set",description:"Like the finest writing parchment",
				skuAttributes:["PDP":[attributeVOMock]],skuImages:imageVOMock1,zoomAvailable:TRUE,color:"green",skuId:"sku12345",size:"42",upc:"Mikasa") 
			1 * productManagerMock.getEverLivingSKUDetails("BedBathUS","sku12345", TRUE) >> skuDetailVOMock
			1 * requestMock.getSession().getAttribute(BBBCoreConstants.USER_TOKEN_BVRR) >> "userToken"
			
			//getColorSwatchDetail Public method Coverage
			RollupTypeVO rollupTypeVOMock = new RollupTypeVO(firstRollUp:"red")
			RollupTypeVO rollupTypeVOMock1 = new RollupTypeVO(firstRollUp:"green",largeImagePath:"/largeImage/url")
			1 * productManagerMock.getEverLivingRollupDetails("prod12345", "green", "color", "size") >> [rollupTypeVOMock,rollupTypeVOMock1]
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("color", "green")
			1 * requestMock.setParameter("selected", "sku12345")
			1 * requestMock.setParameter("size", "42")
			1 * requestMock.setParameter("upc", "Mikasa")
			1 * requestMock.setParameter("finish", "green")
			1 * requestMock.setParameter(TABLOOKUP, true)
			1 * requestMock.setParameter(PRODUCTTAB, [tabVOMock,tabVOMock1])
			1 * requestMock.setParameter(INSTOCK, FALSE)
			1 * requestMock.setParameter(SKUDETAILVO, skuDetailVOMock)
			1 * requestMock.setParameter(PRODUCTVO, productVOMock)
			1 * requestMock.setParameter(FIRSTATTRIBUTSVOLIST, [attributeVOMock])
			1 * requestMock.setParameter(FIRSTCHILDSKU, "sku12345")
			1 * requestMock.setParameter(BBBCoreConstants.USER_TOKEN_BVRR, "userToken")
			1 * requestMock.setParameter(LINK_STRING, _)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock, responseMock)
	}
	
	def"service method.This TC is when productVO's isCollection is true and it covers createChildProducts Public method"(){
		given:
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prod12345"
			requestMock.getParameter(SKUID) >> "sku12345"
			requestMock.getObjectParameter(IS_MAIN_PRODUCT) >> null
			requestMock.getParameter(REGISTRYID) >> "22323212"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			
			TabVO tabVOMock = new TabVO()
			TabVO tabVOMock1 = new TabVO()
			RollupTypeVO rollupTypeVOMock = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock1 = new RollupTypeVO(rollupAttribute:"rollupAtt1")
			RollupTypeVO rollupTypeVOMock2 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock3 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock4 = new RollupTypeVO(rollupAttribute:"rollupAtt4")
			RollupTypeVO rollupTypeVOMock10 = new RollupTypeVO(rollupAttribute:"rollupAtt10")
			RollupTypeVO rollupTypeVOMock5 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock6 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock7 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock8 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock9 = new RollupTypeVO()
			ImageVO imageVOMock = new ImageVO()
			BBBDynamicPriceVO bbbDynamicPriceVOMock = Mock()
			
			ProductVO productVOMock = new ProductVO(rollupAttributes:["COLOR":[rollupTypeVOMock],"2":[rollupTypeVOMock1]],prdRelationRollup:["COLOR":[rollupTypeVOMock2]],
				name:"DiningTable",productId:"prod22222",priceRangeDescription:"priceRangeDesc",productImages:imageVOMock,dynamicPriceVO:bbbDynamicPriceVOMock,childSKUs:["sku7894"])
			
			ProductVO productVOMock1 = new ProductVO(rollupAttributes:["FINISH":[rollupTypeVOMock3],"3":[rollupTypeVOMock4],"4":[rollupTypeVOMock10]],prdRelationRollup:["FINISH":[rollupTypeVOMock5]],
				name:"DiningPlates",productId:"prod33333",priceRangeDescription:"priceRangeDesc",productImages:imageVOMock,dynamicPriceVO:bbbDynamicPriceVOMock,childSKUs:["sku7894"])
			
			ProductVO productVOMock2 = new ProductVO(rollupAttributes:["SIZE":[rollupTypeVOMock6],"5":null],prdRelationRollup:["SIZE":[rollupTypeVOMock8],"TWO":[rollupTypeVOMock8],"NEXT":[rollupTypeVOMock8]],
				productId:"prod44444",name:"DiningChairs")
			
			CollectionProductVO collectionProductVOMock = new CollectionProductVO(collection:TRUE,childSKUs:["sku12345"],productTabs:[tabVOMock,tabVOMock1],
				childProducts:[productVOMock,productVOMock1,productVOMock2])
			
			1 * productManagerMock.getEverLivingProductDetails("BedBathUS", "prod12345") >> collectionProductVOMock
			MediaVO mediaVOMock = new MediaVO(providerId:"6")
			1 * productManagerMock.getMediaDetails("prod12345", "BedBathUS") >> [mediaVOMock]
			requestMock.getParameter("color") >> "green"
			requestMock.getParameter("checkInventory") >> FALSE
			ImageVO imageVOMock1 = new ImageVO()
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuBelowLine:TRUE,displayName:"Dinnerware Set",description:"Like the finest writing parchment",
				skuAttributes:null,skuImages:imageVOMock1,zoomAvailable:TRUE,color:"red",skuId:"sku12345",size:"42",upc:"Mikasa")
			1 * productManagerMock.getEverLivingSKUDetails("BedBathUS","sku12345", TRUE) >> skuDetailVOMock
			1 * requestMock.getSession().getAttribute(BBBCoreConstants.USER_TOKEN_BVRR) >> "userToken"
			
			//createChildProducts Public Method Coverage
			RollupTypeVO rollupTypeVOChild = new RollupTypeVO()
			RollupTypeVO rollupTypeVOChild1= new RollupTypeVO()
			productManagerMock.getEverLivingRollupDetails("prod22222","rollupAtt1","COLOR", null) >> [rollupTypeVOChild,rollupTypeVOChild1]
			productManagerMock.getEverLivingRollupDetails("prod33333","rollupAtt4","FINISH", null) >> null
			productManagerMock.getEverLivingRollupDetails("prod33333","rollupAtt10","FINISH", null) >> []
			
			//getColorSwatchDetail Public method Coverage
			RollupTypeVO rollupTypeVOMock11 = new RollupTypeVO(firstRollUp:"red")
			RollupTypeVO rollupTypeVOMock12 = new RollupTypeVO(firstRollUp:"green",thumbnailImagePath:"/thumbnailImagePath/url")
			1 * productManagerMock.getEverLivingRollupDetails("prod22222", "green", "color", "size") >> [rollupTypeVOMock11,rollupTypeVOMock12]
			productManagerMock.getEverLivingRollupDetails("prod33333", "green", "color", "size") >>> [null,[]]
			productManagerMock.getEverLivingRollupDetails("prod33333", "green", "finish", "size") >>> [[],[]]
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			
			1 * requestMock.setParameter(COLLECTIONVO, collectionProductVOMock)
			1 * requestMock.setParameter(TABLOOKUP, false)
			1 * requestMock.setParameter(RKGCOLLECTION_PROD_IDS, _)
			1 * requestMock.setParameter(MEDIAVO, mediaVOMock)
			1 * requestMock.setParameter("size", "42")
			1 * requestMock.setParameter("upc", "Mikasa")
			1 * requestMock.setParameter('color', 'red')
			1 * requestMock.setParameter('finish', 'red')
			1 * requestMock.setParameter(PRODUCTTAB, [tabVOMock,tabVOMock1])
			1 * requestMock.setParameter(INSTOCK, FALSE)
			1 * requestMock.setParameter(SKUDETAILVO, skuDetailVOMock)
			1 * requestMock.setParameter(PRODUCTVO, collectionProductVOMock)
			1 * requestMock.setParameter(FIRSTATTRIBUTSVOLIST, null)
			1 * requestMock.setParameter(FIRSTCHILDSKU, "sku12345")
			1 * requestMock.setParameter(BBBCoreConstants.USER_TOKEN_BVRR, "userToken")
			1 * requestMock.setParameter(LINK_STRING, _)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock, responseMock)
	}
	
	def"service method.This TC is when productId and skuId is null"(){
		given:
			testObj = Spy()
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> null
			requestMock.getParameter(SKUID) >> "sku12345"
			requestMock.getObjectParameter(IS_MAIN_PRODUCT) >> null
			requestMock.getParameter(REGISTRYID) >> "22323212"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> null
			requestMock.getParameter("color") >> "green"
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.serviceParameter(OPARAM_ERROR, requestMock, responseMock)
			1 * testObj.logDebug('pProductId is NULL')
	}
	
	def"service method.This TC is when color is empty and childSKUs are null"(){
		given:
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prod12345"
			requestMock.getParameter(SKUID) >> "sku12345"
			requestMock.getObjectParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(REGISTRYID) >> "22323212"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			
			RollupTypeVO rollupTypeVOMock2 = new RollupTypeVO()
			ProductVO productVOMock = new ProductVO(collection:FALSE,productId:null,childSKUs:null,
				productTabs:null,rollupAttributes:["SIZE":[rollupTypeVOMock2]])
			
			1 * productManagerMock.getEverLivingMainProductDetails("BedBathUS", "prod12345") >> productVOMock
			1 * productManagerMock.getMediaDetails(_, "BedBathUS") >> null
			requestMock.getParameter("color") >> ""
			1 * requestMock.getSession().getAttribute(BBBCoreConstants.USER_TOKEN_BVRR) >> "userToken"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('userTokenBVRR', 'userToken')
			1 * requestMock.setParameter('productVO', productVOMock)
			1 * requestMock.setParameter('tabLookUp', true)
			1 * requestMock.setParameter('linkStringNonRecproduct', _)
			1 * requestMock.setParameter('inStock', true)
			1 * requestMock.setParameter('pFirstAttributsVOList', null)
			1 * requestMock.setParameter('productTabs', null)
			1 * requestMock.setParameter('pSKUDetailVO', null)
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
	}
	
	def"service method.This TC is when skuId is null"(){
		given:
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prod12345"
			requestMock.getParameter(SKUID) >> null
			requestMock.getObjectParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(REGISTRYID) >> "22323212"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
	
			ProductVO productVOMock = new ProductVO(collection:FALSE,productId:null,childSKUs:["sku12345","sku56789"],
				productTabs:null,rollupAttributes:null)
			
			1 * productManagerMock.getEverLivingMainProductDetails("BedBathUS", "prod12345") >> productVOMock
			1 * productManagerMock.getMediaDetails(_, "BedBathUS") >> []
			requestMock.getParameter("color") >> ""
			1 * requestMock.getSession().getAttribute(BBBCoreConstants.USER_TOKEN_BVRR) >> "userToken"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('userTokenBVRR', 'userToken')
			1 * requestMock.setParameter('pFirstChildSKU', 'sku12345')
			1 * requestMock.setParameter('pFirstAttributsVOList', null)
			1 * requestMock.setParameter('productTabs', null)
			1 * requestMock.setParameter('inStock', true)
			1 * requestMock.setParameter('linkStringNonRecproduct', _)
			1 * requestMock.setParameter('pSKUDetailVO', null)
			1 * requestMock.setParameter('productVO', productVOMock)
			1 * requestMock.setParameter('tabLookUp', true)
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
	}
	
	def"service method.This TC is when childSKUs does not contain skuId and exception thrown in createChildProducts "(){
		given:
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prod12345"
			requestMock.getParameter(SKUID) >> "sku52324"
			requestMock.getObjectParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(REGISTRYID) >> "22323212"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			requestMock.getParameter("color") >> ""
			productManagerMock.getMediaDetails(_, "BedBathUS") >> []
			
			RollupTypeVO rollupTypeVOMock = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock1 = new RollupTypeVO(rollupAttribute:"rollupAtt1")
			RollupTypeVO rollupTypeVOMock2 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock3 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock4 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock5 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock6 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock7 = new RollupTypeVO()
			ImageVO imageVOMock = new ImageVO()
			BBBDynamicPriceVO bbbDynamicPriceVOMock = Mock()
			
			ProductVO productVOMock = new ProductVO(rollupAttributes:["COLOR":[rollupTypeVOMock]],prdRelationRollup:["COLOR":[rollupTypeVOMock1]],productId:"prod22222")
			
			ProductVO productVOMock1 = new ProductVO(rollupAttributes:["COLOR":[rollupTypeVOMock]],prdRelationRollup:null,productId:"prod33333")
			
			ProductVO productVOMock2 = new ProductVO(rollupAttributes:null,prdRelationRollup:["COLOR":[rollupTypeVOMock1]],productId:"prod44444")
			
			ProductVO productVOMock3 = new ProductVO(rollupAttributes:["COLOR":[rollupTypeVOMock]],prdRelationRollup:["COLOR":[rollupTypeVOMock1],"SECOND":[rollupTypeVOMock1]],productId:"prod55555")
			
			ProductVO productVOMock4 = new ProductVO(rollupAttributes:["NOCOLOR":[rollupTypeVOMock]],prdRelationRollup:["COLOR":[rollupTypeVOMock1]],productId:"prod66666")
			
			ProductVO productVOMock5 = new ProductVO(rollupAttributes:["NOCOLOR":[rollupTypeVOMock]],prdRelationRollup:["FINISH":[rollupTypeVOMock1]],productId:"prod77777")
			
			ProductVO productVOMock6 = new ProductVO(rollupAttributes:["NOCOLOR":[rollupTypeVOMock]],prdRelationRollup:["SIZE":[rollupTypeVOMock1]],productId:"prod88888")
			
			ProductVO productVOMock7 = new ProductVO(rollupAttributes:["SIZE":[rollupTypeVOMock2],"5":[rollupTypeVOMock4]],prdRelationRollup:["SIZE":[rollupTypeVOMock3]],
				,name:"DiningChairs",productId:"prod99999",priceRangeDescription:"priceRangeDesc",productImages:imageVOMock,dynamicPriceVO:bbbDynamicPriceVOMock)
			
			ProductVO productVOMock8 = new ProductVO(rollupAttributes:["SIZE":[rollupTypeVOMock5],"5":[rollupTypeVOMock6]],prdRelationRollup:["SIZE":[rollupTypeVOMock7]],
				,name:"DiningChairs",productId:"prod00000",priceRangeDescription:"priceRangeDesc",productImages:imageVOMock,dynamicPriceVO:bbbDynamicPriceVOMock)
			
			CollectionProductVO collectionProductVOMock = new CollectionProductVO(collection:TRUE,childSKUs:["sku12345","sku56789"],productTabs:null,
				childProducts:[productVOMock,productVOMock1,productVOMock2,productVOMock3,productVOMock4,productVOMock5,productVOMock6,productVOMock7,productVOMock8],productId:"prod11111",leadSKU:FALSE)

			productManagerMock.getEverLivingMainProductDetails("BedBathUS", "prod12345") >> collectionProductVOMock
			1 * requestMock.getSession().getAttribute(BBBCoreConstants.USER_TOKEN_BVRR) >> "userToken"
			
			//getColorSwatchDetail Public method Coverage
			1 * productManagerMock.getEverLivingRollupDetails("prod99999", null, SIZE, null) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			1 * productManagerMock.getEverLivingRollupDetails("prod00000", null, SIZE, null) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
			1 * requestMock.setParameter('linkStringNonRecproduct', _)
			1 * requestMock.setParameter('productTabs', null)
			1 * requestMock.setParameter('rkgCollectionProdIds', _)
			1 * requestMock.setParameter('inStock', true)
			1 * requestMock.setParameter('pFirstAttributsVOList', null)
			1 * requestMock.setParameter('collectionVO',  collectionProductVOMock)
			1 * requestMock.setParameter('productVO', collectionProductVOMock)
			1 * requestMock.setParameter('tabLookUp', false)
			1 * requestMock.setParameter('pFirstChildSKU', 'sku12345')
			1 * requestMock.setParameter('userTokenBVRR', 'userToken')
			1 * requestMock.setParameter('pSKUDetailVO', null)
			
	}
	
	def"service method.This TC is when pSKUDetailVO is null"(){
		given:
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prod12345"
			requestMock.getParameter(SKUID) >> "sku12345"
			requestMock.getObjectParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(REGISTRYID) >> ""
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			
			ImageVO imageVOMock = new ImageVO()
			RollupTypeVO rollupTypeVOMock2 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock3 = new RollupTypeVO()
			ProductVO productVOMock = new ProductVO(collection:FALSE,productId:"prod12345",childSKUs:["sku12345","sku56789"],productImages:imageVOMock,
				productTabs:null,rollupAttributes:["SIZE":[rollupTypeVOMock2],"NONE":[rollupTypeVOMock3]])
			
			1 * productManagerMock.getEverLivingMainProductDetails("BedBathUS", "prod12345") >> productVOMock
			1 * productManagerMock.getMediaDetails("prod12345", "BedBathUS") >> null
			requestMock.getParameter("color") >> "green"
			requestMock.getParameter("checkInventory") >> TRUE
			1 * inventoryManagerMock.getEverLivingProductAvailability("BedBathUS", "sku12345", BBBInventoryManager.PRODUCT_DISPLAY) >> 1
			1 * productManagerMock.getEverLivingSKUDetails("BedBathUS","sku12345", TRUE) >> null
			1 * requestMock.getSession().getAttribute(BBBCoreConstants.USER_TOKEN_BVRR) >> "userToken"
			
			//getColorSwatchDetail Public method Coverage
			1 * productManagerMock.getEverLivingRollupDetails("prod12345", "green", "color", "size") >> []
			1 * productManagerMock.getEverLivingRollupDetails("prod12345", "green", "finish", "size") >> []
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('pFirstAttributsVOList', null)
			1 * requestMock.setParameter('linkStringNonRecproduct', _)
			1 * requestMock.setParameter('inStock', false)
			1 * requestMock.setParameter('pSKUDetailVO', null)
			1 * requestMock.setParameter('pFirstChildSKU', 'sku12345')
			1 * requestMock.setParameter('productTabs', null)
			1 * requestMock.setParameter('tabLookUp', true)
			1 * requestMock.setParameter('productVO', productVOMock)
			1 * requestMock.setParameter('userTokenBVRR', 'userToken')
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
	}
	
	def"service method.This TC is when childProducts is empty"(){
		given:
			testObj = Spy()
			testObj.setProductManager(productManagerMock)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prod12345"
			requestMock.getParameter(SKUID) >> "sku52324"
			requestMock.getObjectParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(REGISTRYID) >> "22323212"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			requestMock.getParameter("color") >> "green"
			productManagerMock.getMediaDetails(_, "BedBathUS") >> []
			
			CollectionProductVO collectionProductVOMock = new CollectionProductVO(collection:TRUE,childSKUs:["sku12345","sku56789"],productTabs:null,
				childProducts:[],productId:"prod11111",leadSKU:null)
			
			1 * testObj.createChildProducts(collectionProductVOMock, _) >> collectionProductVOMock
			1 * testObj.createChildProductsSkuLevel(collectionProductVOMock, _) >> collectionProductVOMock
			productManagerMock.getEverLivingMainProductDetails("BedBathUS", "prod12345") >> collectionProductVOMock
			1 * requestMock.getSession().getAttribute(BBBCoreConstants.USER_TOKEN_BVRR) >> "userToken"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
			1 * requestMock.setParameter('productTabs', null)
			1 * requestMock.setParameter('inStock', true)
			1 * requestMock.setParameter('pFirstChildSKU', 'sku12345')
			1 * requestMock.setParameter('pFirstAttributsVOList', null)
			1 * testObj.logDebug('pSiteId[BedBathUS]')
			1 * testObj.logDebug('pProductId[prod12345]')
			1 * testObj.logDebug('LeadSKU[null]')
			1 * requestMock.setParameter('tabLookUp', false)
			1 * requestMock.setParameter('pSKUDetailVO', null)
			1 * requestMock.setParameter('productVO',collectionProductVOMock)
	}
	
	
	def"service method.This TC is when leadSKU is true"(){
		given:
			testObj = Spy()
			testObj.setProductManager(productManagerMock)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prod12345"
			requestMock.getParameter(SKUID) >> "sku52324"
			requestMock.getObjectParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(REGISTRYID) >> "22323212"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			requestMock.getParameter("color") >> ""
			productManagerMock.getMediaDetails(_, "BedBathUS") >> []
			
			RollupTypeVO rollupTypeVOMock = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock1 = new RollupTypeVO()
			
			CollectionProductVO collectionProductVOMock = new CollectionProductVO(collection:TRUE,childSKUs:["sku12345","sku56789"],productTabs:null,
				childProducts:[],productId:"prod11111",leadSKU:TRUE,rollupAttributes:null)
			
			1 * testObj.createChildProducts(collectionProductVOMock, _) >> collectionProductVOMock
			1 * testObj.createChildProductsSkuLevel(collectionProductVOMock, _) >> collectionProductVOMock
			productManagerMock.getEverLivingMainProductDetails("BedBathUS", "prod12345") >> collectionProductVOMock
			1 * requestMock.getSession().getAttribute(BBBCoreConstants.USER_TOKEN_BVRR) >> "userToken"
			
			//getColorSwatchDetail Public method Coverage
			RollupTypeVO rollupTypeVOMock3 = new RollupTypeVO(firstRollUp:"red")
			1 * productManagerMock.getEverLivingRollupDetails("prod11111", "", "color", "size") >> [rollupTypeVOMock3]
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.serviceParameter('output', requestMock, responseMock)
			1 * requestMock.setParameter('productTabs', null)
			1 * requestMock.setParameter('userTokenBVRR', 'userToken')
			1 * requestMock.setParameter('pSKUDetailVO', null)
			1 * requestMock.setParameter('inStock', true)
			1 * requestMock.setParameter('tabLookUp', true)
			1 * requestMock.setParameter('rkgCollectionProdIds', _)
			1 * requestMock.setParameter('linkStringNonRecproduct', _)
			1 * requestMock.setParameter('pFirstAttributsVOList', null)
			1 * requestMock.setParameter('pFirstChildSKU', 'sku12345')
			1 * requestMock.setParameter('productVO',  collectionProductVOMock)
			2 * testObj.logDebug('LeadSKU[true]')
	}
	
	def"service method.This TC is when skuBelowLine is false"(){
		given:
			testObj = Spy()
			testObj.setProductManager(productManagerMock)
			testObj.setInventoryManager(inventoryManagerMock)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prod12345"
			requestMock.getParameter(SKUID) >> "sku12345"
			requestMock.getObjectParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(REGISTRYID) >> "22323212"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			
			ImageVO imageVOMock = new ImageVO()
			RollupTypeVO rollupTypeVOMock3 = new RollupTypeVO()
			CollectionProductVO collectionProductVOMock = new CollectionProductVO(collection:TRUE,productId:"prod12345",childSKUs:["sku12345","sku56789"],productImages:imageVOMock,
				productTabs:null,rollupAttributes:["1":[rollupTypeVOMock3]],childProducts:[])
			
			1 * productManagerMock.getEverLivingMainProductDetails("BedBathUS", "prod12345") >> collectionProductVOMock
			1 * testObj.createChildProducts(collectionProductVOMock, _) >> collectionProductVOMock
			1 * testObj.createChildProductsSkuLevel(collectionProductVOMock, _) >> collectionProductVOMock
			1 * productManagerMock.getMediaDetails("prod12345", "BedBathUS") >> null
			requestMock.getParameter("color") >> null
			requestMock.getParameter("checkInventory") >> TRUE
			1 * inventoryManagerMock.getEverLivingProductAvailability("BedBathUS", "sku12345", BBBInventoryManager.PRODUCT_DISPLAY) >> 0
			AttributeVO attributeVOMock = new AttributeVO()
			ImageVO imageVOMock1 = new ImageVO()
			SKUDetailVO skuDetailVOMock = new SKUDetailVO(skuBelowLine:FALSE,displayName:"Dinnerware Set",description:"Like the finest writing parchment",
				skuAttributes:["PDP":[attributeVOMock]],skuImages:imageVOMock1,zoomAvailable:TRUE,color:"green",skuId:"sku12345",size:"42",upc:"Mikasa")
			1 * productManagerMock.getEverLivingSKUDetails("BedBathUS","sku12345", TRUE) >> skuDetailVOMock
			1 * requestMock.getSession().getAttribute(BBBCoreConstants.USER_TOKEN_BVRR) >> "userToken"
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter('pFirstChildSKU', 'sku12345')
			1 * requestMock.setParameter('linkStringNonRecproduct', _)
	}
	
	def"service method.This TC is when BBBBusinessException thrown in service method"(){
		given:
			testObj = Spy()
			testObj.setProductManager(productManagerMock)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prod12345"
			requestMock.getParameter(SKUID) >> "sku12345"
			requestMock.getObjectParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(REGISTRYID) >> "22323212"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			
			ImageVO imageVOMock = new ImageVO()
			ProductVO productVOMock = new ProductVO(collection:FALSE,productId:"prod12345",childSKUs:["sku12345","sku56789"])
			
			1 * productManagerMock.getEverLivingMainProductDetails("BedBathUS", "prod12345") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError('browse_1032: Business Exception from service of ProductDetailDroplet for productId=prod12345 |skuId=sku12345 |SiteId=BedBathUS', _)
	}
	
	def"service method.This TC is when BBBSystemException thrown in service method"(){
		given:
			testObj = Spy()
			testObj.setProductManager(productManagerMock)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prod12345"
			requestMock.getParameter(SKUID) >> "sku12345"
			requestMock.getObjectParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(IS_MAIN_PRODUCT) >> TRUE
			requestMock.getParameter(REGISTRYID) >> "22323212"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "BedBathUS"
			
			ImageVO imageVOMock = new ImageVO()
			ProductVO productVOMock = new ProductVO(collection:FALSE,productId:"prod12345",childSKUs:["sku12345","sku56789"])
			
			1 * productManagerMock.getEverLivingMainProductDetails("BedBathUS", "prod12345") >> {throw new BBBSystemException("Mock for BBBSystemException")}
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * testObj.logError('browse_1033: System Exception from service of ProductDetailDroplet for productId=prod12345 |skuId=sku12345 |SiteId=BedBathUS', _)
	}
	
	
	/////////////////////////////////TCs for service ends////////////////////////////////////////
	
	/////////////////////////////////TCs for createChildProductsSkuLevel starts////////////////////////////////////////
	//Signature : public CollectionProductVO createChildProductsSkuLevel(CollectionProductVO collectionProductVO, String siteId) ////
	
	def"createChildProductsSkuLevel. This TC is the Happy flow of createChildProductsSkuLevel"(){
		given:
			String siteId = "BedBathUS"
			RollupTypeVO rollupTypeVOMock = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock1 = new RollupTypeVO(rollupAttribute:"rollupAtt")
			RollupTypeVO rollupTypeVOMock2 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock3 = new RollupTypeVO(rollupAttribute:"rollupAtt3")
			RollupTypeVO rollupTypeVOMock4 = new RollupTypeVO(rollupAttribute:"rollupAtt4")
			BBBDynamicPriceVO bbbDynamicPriceVOMock = Mock()
			ImageVO imageVOMock = new ImageVO()
			ProductVO productVOMock = new ProductVO(prdRelationRollup:["NONE":[rollupTypeVOMock]],rollupAttributes:["ONE":[rollupTypeVOMock1,rollupTypeVOMock2]],name:"DiningChair",
				productId:"prod11111",priceRangeDescription:"priceRangeDesc",dynamicPriceVO:bbbDynamicPriceVOMock,productImages:imageVOMock)
			ProductVO productVOMock1 = new ProductVO(prdRelationRollup:["NONE":[rollupTypeVOMock]],rollupAttributes:["TWO":[rollupTypeVOMock3],"THREE":[rollupTypeVOMock4]],name:"DiningTable",
				productId:"prod22222",priceRangeDescription:"priceRange",dynamicPriceVO:bbbDynamicPriceVOMock,productImages:imageVOMock)
			ProductVO productVOMock2 = new ProductVO(prdRelationRollup:["NONE":[rollupTypeVOMock]],rollupAttributes:[:],name:"DiningPlates",
				productId:"prod22222",priceRangeDescription:"Description",dynamicPriceVO:bbbDynamicPriceVOMock,productImages:imageVOMock)
			CollectionProductVO collectionProductVOMock = new CollectionProductVO(childProducts:[productVOMock,productVOMock1,productVOMock2])
			2 * productManagerMock.getSKUId(siteId,"prod11111", _) >>> ["sku12345",""]
			RollupTypeVO rollupValuesMock = new RollupTypeVO(rollupAttribute:"rollupAtt5")
			RollupTypeVO rollupValuesMock1 = new RollupTypeVO(rollupAttribute:"rollupAtt6")
			1 * productManagerMock.getEverLivingRollupDetails("prod22222", "rollupAtt3", "TWO","THREE") >> [rollupValuesMock,rollupValuesMock1]  
			2 * productManagerMock.getSKUId(siteId, "prod22222", _) >>> ["sku34567",""]
			
			
		when:
			CollectionProductVO collectionProductVO= testObj.createChildProductsSkuLevel(collectionProductVOMock, siteId)
		then:
			collectionProductVO.getChildProducts().size() == 4
			collectionProductVO.getChildProducts().get(0).getName().equals("DiningChair - rollupAtt")
			collectionProductVO.getChildProducts().get(1).getName().equals("DiningChair - null")
			collectionProductVO.getChildProducts().get(2).getName().equals("DiningTable - rollupAtt3 - rollupAtt5")
			collectionProductVO.getChildProducts().get(3).getName().equals("DiningTable - rollupAtt3 - rollupAtt6")
			collectionProductVO.getChildProducts().get(0).getChildSKUs().equals(["sku12345"])
			collectionProductVO.getChildProducts().get(0).getProductId().equals("prod11111")
			collectionProductVO.getChildProducts().get(0).getProductImages().equals(imageVOMock)
			
	}
	
	def"createChildProductsSkuLevel. This TC is when getChildProducts() is null"(){
		given:
			String siteId = "BedBathUS"
			CollectionProductVO collectionProductVOMock = new CollectionProductVO(childProducts:null)
			
		when:
			CollectionProductVO collectionProductVO = testObj.createChildProductsSkuLevel(collectionProductVOMock, siteId)
		then:
			collectionProductVO.getChildProducts() == []
	}
	
	def"createChildProductsSkuLevel. This TC is when BBBBusinessException and BBBSystemException thrown in createChildProductsSkuLevel method"(){
		given:
			testObj = Spy()
			testObj.setProductManager(productManagerMock)
			String siteId = "BedBathUS"
			RollupTypeVO rollupTypeVOMock = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock1 = new RollupTypeVO(rollupAttribute:"rollupAtt")
			RollupTypeVO rollupTypeVOMock2 = new RollupTypeVO()
			RollupTypeVO rollupTypeVOMock3 = new RollupTypeVO(rollupAttribute:"rollupAtt3")
			RollupTypeVO rollupTypeVOMock4 = new RollupTypeVO()
			
			BBBDynamicPriceVO bbbDynamicPriceVOMock = Mock()
			ImageVO imageVOMock = new ImageVO()
			ProductVO productVOMock = new ProductVO(prdRelationRollup:["NONE":[rollupTypeVOMock]],rollupAttributes:["ONE":[rollupTypeVOMock1,rollupTypeVOMock2]],name:"DiningChair",
				productId:"prod11111",priceRangeDescription:"priceRangeDesc",dynamicPriceVO:bbbDynamicPriceVOMock,productImages:imageVOMock)
			
			ProductVO productVOMock1 = new ProductVO(prdRelationRollup:["OTHER":[rollupTypeVOMock]],rollupAttributes:["TWO":[rollupTypeVOMock3]],name:"DiningTable",
				productId:"prod22222")
			
			ProductVO productVOMock2 = new ProductVO(prdRelationRollup:["NONE":[rollupTypeVOMock]],rollupAttributes:["NONE":[rollupTypeVOMock3]],name:"DiningPlates",
				productId:"prod33333")
			
			ProductVO productVOMock3 = new ProductVO(prdRelationRollup:["NONE":[rollupTypeVOMock]],rollupAttributes:null,name:"Dining",
				productId:"prod44444")
			
			ProductVO productVOMock4 = new ProductVO(prdRelationRollup:["NONE":[rollupTypeVOMock]],rollupAttributes:["FOUR":[rollupTypeVOMock3],"FIVE":[rollupTypeVOMock4]],name:"Beds",
				productId:"prod55555")
			
			ProductVO productVOMock5 = new ProductVO(prdRelationRollup:["NONE":[rollupTypeVOMock]],rollupAttributes:["SIX":[rollupTypeVOMock3],"SEVEN":[rollupTypeVOMock4]],name:"Pillows",
				productId:"prod66666")
			
			CollectionProductVO collectionProductVOMock = new CollectionProductVO(childProducts:[productVOMock,productVOMock1,productVOMock2,productVOMock3,productVOMock4,productVOMock5])
			productManagerMock.getSKUId(siteId,"prod11111", _) >> {throw new BBBBusinessException("Mock for BBBBusinessException")} >> {throw new BBBSystemException("Mock for BBBSystemException")}
			1 * productManagerMock.getEverLivingRollupDetails("prod55555", "rollupAtt3", "FOUR","FIVE") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			1 * productManagerMock.getEverLivingRollupDetails("prod66666", "rollupAtt3", "SIX","SEVEN") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			
		when:
			CollectionProductVO collectionProductVO = testObj.createChildProductsSkuLevel(collectionProductVOMock, siteId)
		then:
			collectionProductVO.getChildProducts().size() == 5
			collectionProductVO.getChildProducts().get(0).getName().equals("DiningChair - rollupAtt")
			collectionProductVO.getChildProducts().get(1).getName().equals("DiningChair - null")
			collectionProductVO.getChildProducts().get(2).getName().equals("DiningTable")
			collectionProductVO.getChildProducts().get(3).getName().equals("DiningPlates")
			collectionProductVO.getChildProducts().get(4).getName().equals("Dining")
			1 * testObj.logError('browse_1032 Business Exception [productRollUp is not one] from createChildProductsSkuLevel of ProductDetailDroplet ', _)
			1 * testObj.logError('browse_1033 System Exception from createChildProductsSkuLevel of ProductDetailDroplet ', _)
			1 * testObj.logError('browse_1033 System Exception [productRollUp is not one] from createChildProductsSkuLevel of ProductDetailDroplet ', _)
			1 * testObj.logError('browse_1032 Business Exception from createChildProductsSkuLevel of ProductDetailDroplet ', _)			
	}
	
	/////////////////////////////////TCs for createChildProductsSkuLevel ends ////////////////////////////////////////
	
	/////////////////////////////////TCs for createChildProducts starts////////////////////////////////////////
	//Signature : public CollectionProductVO createChildProducts(CollectionProductVO collectionProductVO, String siteId) ////
	
	def"createChildProducts. This TC is when CollectionProductVO parameter passed as null"(){
		given:
			String siteId = "BedBathUS"
		when:
			CollectionProductVO results = testObj.createChildProducts(null, siteId)
		then:
			results == null
	}
	
	def"createChildProducts. This TC is when ChildProducts is null"(){
		given:
			CollectionProductVO collectionProductVOMock = new CollectionProductVO(childProducts:null)
			String siteId = "BedBathUS"
		when:
			CollectionProductVO results = testObj.createChildProducts(collectionProductVOMock, siteId)
		then:
			results == collectionProductVOMock
	}
	
	def "createChildProducts. This TC is when prdRelationRollup and productRollUp contains Key as FINISH" (){
		given:
			CollectionProductVO collectionProductVO = new CollectionProductVO()
			List<ProductVO> productVOListMock = new ArrayList<ProductVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
			RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
			colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
			prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
			Map<String,List<RollupTypeVO>> prdRelationUpMock = ["FINISH":prdRelationRollupTypeVOFinsh,"COLOR":prdRelationRollupTypeVOColor]
			Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["FINISH":prdRelationRollupTypeVOFinish]
			ProductVO productMock = new ProductVO()
			productMock.setName("TestProduct")
			productMock.setPrdRelationRollup(prdRelationRollupMock)
			productMock.setRollupAttributes(prdRelationUpMock)
			productVOListMock.add(productMock)
			List listChildSku = new ArrayList<String>()
				listChildSku.add("124545")
				collectionProductVO.setProductId("10124545")
				collectionProductVO.setChildSKUs(listChildSku)
				collectionProductVO.setCollection(true)
				collectionProductVO.setChildProducts(productVOListMock)
				productManagerMock.getRollupDetails(_, _, _, _) >> prdRelationRollupTypeVOColor
		when:
			testObj.createChildProducts(collectionProductVO, "BedBathUS")
		then:
			collectionProductVO.getChildProducts().size() == 1
			collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct - RollupAttribute")
		}
	
	/////////////////////////////////TCs for createChildProducts ends ////////////////////////////////////////
	
	/////////////////////////////////TCs for setChildProductsAttributes starts////////////////////////////////////////
	//Signature : public ProductVO setChildProductsAttributes(ProductVO collectionChildProduct, ProductVO product,Map<String,List<RollupTypeVO>> populateRollUp, String name) ////
	
	def"setChildProductsAttributes. This TC is when name parameter is passed as null"(){
		given:
			ProductVO collectionChildProduct = new ProductVO()
			ImageVO imageVOMock = new ImageVO()
			ProductVO productMock = new ProductVO(name:"DiningChair",priceRangeDescription:"priceRange",childSKUs:["SKU12345"],productId:"prod12345",productImages:imageVOMock)
			List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
			Map<String,List<RollupTypeVO>> prdRelationUpMock = ["FINISH":prdRelationRollupTypeVOFinsh,"COLOR":prdRelationRollupTypeVOColor]
			Map<String,List<RollupTypeVO>> populateRollUp = ["SIZE":prdRelationUpMock]
		when:	
			ProductVO ProductVOResults = testObj.setChildProductsAttributes(collectionChildProduct, productMock, populateRollUp, null)
		then:
			ProductVOResults.getName().equals("DiningChair")
			ProductVOResults.getChildSKUs().equals(["SKU12345"])
			ProductVOResults.getProductId().equals("prod12345")
			ProductVOResults.getProductImages().equals(imageVOMock)
			ProductVOResults.getPriceRangeDescription().equals("priceRange")
			ProductVOResults.getRollupAttributes().equals(populateRollUp)
	}
	
	/////////////////////////////////TCs for setChildProductsAttributes ends ////////////////////////////////////////
	
	/////////////////////////////////TCs for getColorSwatchDetail starts////////////////////////////////////////
	//Signature : public void getColorSwatchDetail(ProductVO productVO,String color, boolean isCollectionFlag) throws BBBBusinessException,BBBSystemException ////
	
	
	def "getColorSwatchDetail. This TC is when isCollectionFlag is true"(){
		given:
			
			RollupTypeVO RollupTypeVOMock2 = new RollupTypeVO()
			ImageVO imageVOMock = new ImageVO() 
			ProductVO productVO = new ProductVO(productId:"4567127",rollupAttributes:["SIZE":[RollupTypeVOMock2]],productImages:imageVOMock)
			RollupTypeVO RollupTypeVOMock = new RollupTypeVO(firstRollUp:"red")
			RollupTypeVO RollupTypeVOMock1 = new RollupTypeVO(firstRollUp:"blue",thumbnailImagePath:"/thumbnailPath/images")
			List<RollupTypeVO> rollUPList = [RollupTypeVOMock,RollupTypeVOMock1]
			
			1 * productManagerMock.getEverLivingRollupDetails(productVO.getProductId(), "blue", "color", "size") >> rollUPList
			
		when:
			testObj.getColorSwatchDetail(productVO, "blue", true)
		then:
			productVO.getProductImages().getThumbnailImage().equals("/thumbnailPath/images")
			productVO.getProductImages().getLargeImage().equals(null)
	}
	
	def "getColorSwatchDetail. This TC is when isCollectionFlag is false"(){
		given:
			
			RollupTypeVO RollupTypeVOMock2 = new RollupTypeVO()
			ImageVO imageVOMock = new ImageVO()
			ProductVO productVO = new ProductVO(productId:"4567127",rollupAttributes:["SIZE":[RollupTypeVOMock2]],productImages:imageVOMock)
			RollupTypeVO RollupTypeVOMock = new RollupTypeVO(firstRollUp:"red")
			RollupTypeVO RollupTypeVOMock1 = new RollupTypeVO(firstRollUp:"blue",thumbnailImagePath:"/thumbnailPath/images",largeImagePath:"/largeImage/images")
			List<RollupTypeVO> rollUPList = [RollupTypeVOMock,RollupTypeVOMock1]
			
			1 * productManagerMock.getEverLivingRollupDetails(productVO.getProductId(), "blue", "color", "size") >> null
			1 * productManagerMock.getEverLivingRollupDetails(productVO.getProductId(), "blue", "finish", "size") >> rollUPList
			
		when:
			testObj.getColorSwatchDetail(productVO, "blue", false)
		then:
			productVO.getProductImages().getThumbnailImage().equals(null)
			productVO.getProductImages().getLargeImage().equals("/largeImage/images")
	}
	
	/////////////////////////////////TCs for getColorSwatchDetail ends ////////////////////////////////////////

}
