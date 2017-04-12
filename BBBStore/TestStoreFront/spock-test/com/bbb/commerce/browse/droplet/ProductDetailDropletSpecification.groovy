package com.bbb.commerce.browse.droplet;
/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  SearchDropletSpecification.groovy
 *
 *  DESCRIPTION: Test Search droplet
 *
 *  HISTORY:
 *  Aug 5, 2016  Initial version
*/


import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie

import spock.lang.specification.BBBExtendedSpec
import weblogic.wtc.gwt.ServiceParameters;
import atg.multisite.SiteContextManager
import atg.repository.RepositoryItem
import atg.servlet.DynamoHttpServletRequest
import atg.userprofiling.Profile

import com.bbb.browse.webservice.manager.BBBEximManager
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.browse.manager.ProductManager
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.AttributeVO
import com.bbb.commerce.catalog.vo.CollectionProductVO
import com.bbb.commerce.catalog.vo.ImageVO
import com.bbb.commerce.catalog.vo.MediaVO
import com.bbb.commerce.catalog.vo.ProductVO
import com.bbb.commerce.catalog.vo.RollupTypeVO
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.exim.bean.EximSessionBean
import com.bbb.commerce.inventory.BBBInventoryManagerImpl
import com.bbb.commerce.inventory.BBBStoreInventoryManagerImpl
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSavedItemsSessionBean
import com.bbb.profile.session.BBBSessionBean
import com.bbb.repository.RepositoryItemMock
import com.bbb.rest.catalog.vo.KatoriPriceRestVO
import com.bbb.rest.output.BBBCustomTagComponent
import com.bbb.search.bean.result.BBBDynamicPriceSkuVO
import com.bbb.selfservice.manager.SearchStoreManager
import com.bbb.utils.BBBUtility
import com.bbb.wishlist.GiftListVO
import com.bbb.wishlist.manager.BBBGiftlistManager

/**
 *  Test Search droplet
 *
 *  @author Sapient Corporation
 *
 */
public class ProductDetailDropletSpecification extends BBBExtendedSpec {
	
	def ProductManager productManagerMock = Mock()
	def BBBInventoryManagerImpl inventoryManagerMock = Mock()
	def BBBCatalogTools catalogToolsMock = Mock()
	def BBBStoreInventoryManagerImpl storeInventoryManagerMock = Mock()
	def SearchStoreManager searchStoreManagerMock = Mock()
	def BBBEximManager eximManagerMock = Mock()
	def BBBCustomTagComponent customTagComponentMock = Mock()
	def LblTxtTemplateManager lblTxtTemplateManagerMock = Mock()
	def Profile profileMock = Mock(Profile)
	def BBBGiftlistManager giftlistManagerMock = Mock()
	def ProductVO productVOMock = Mock()
	def SKUDetailVO pSKUDetailVOMock = Mock()
	def SiteContextManager siteContextManagerMock = Mock()
	def CollectionProductVO collectionProductVOMock = Mock()
	List<String> ChildSKUSMock = Mock()
	def BBBSessionBean sessionBeanMock = Mock(BBBSessionBean) 
	def BBBSavedItemsSessionBean savedItemsSessionBeanMock = Mock(BBBSavedItemsSessionBean)
	def BBBEximManager bbbeximManagerMock = Mock()
			
	def ProductDetailDroplet prodDetailTestObj 
	def setup() {
		
		prodDetailTestObj = new ProductDetailDroplet(productManager:productManagerMock,mCatalogTools:catalogToolsMock,inventoryManager:inventoryManagerMock, lblTxtTemplateManager:lblTxtTemplateManagerMock
			,storeInventoryManager:storeInventoryManagerMock,searchStoreManager:searchStoreManagerMock,	giftlistManager:giftlistManagerMock,bbbEximManager:bbbeximManagerMock,customTagComponent:customTagComponentMock)
				
		requestMock.resolveName("/atg/userprofiling/Profile") >> profileMock
		requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBeanMock
		requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> savedItemsSessionBeanMock
		
		prodDetailTestObj.isLoggingDebug() >> true
		Cookie[] cookie = new Cookie("removePersonalization", "124545")
		requestMock.getCookies() >> cookie
		List<String> proviedListIdMock = ["1","2","12"]
		prodDetailTestObj.setProviderIdList(proviedListIdMock)
		requestMock.getHeader("X-Akamai-Edgescape") >> "lat=12121wqwq;long=sadasdsa"
	   BBBUtility.setCatalogTools(catalogToolsMock)
	}
	def "product Droplet Performing product Id as null"() {
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1 * requestMock.serviceParameter(prodDetailTestObj.OPARAM_ERROR, requestMock, responseMock)
		}
	
	def "product Droplet Performing with productId  with  inventory check with out child products."() {
		given:
		 //mocking methods
		Map<String, EximSessionBean> personalizedSkus = new HashMap<String, EximSessionBean>()
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("colorInQueryParamsTBS") >> false
		requestMock.getAttribute("personalizedSku") >> "personalizedSku"
		requestMock.getParameter("color") >> ""
		requestMock.getParameter("skuId") >> "675984"
		productVOMock.getChildSKUs() >> ["124545","675984"]
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("removePersonalization") >> true
		productVOMock.isCollection() >> false
		Map<String ,List<AttributeVO>> arrtibutemap = ["PDP":null]
		productVOMock.getAttributesList() >> arrtibutemap
		Map<String ,List<AttributeVO>> rollupAttributes = ["NONE":null]
		productVOMock.getRollupAttributes() >> rollupAttributes
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		productManagerMock.getProductDetails(_, _) >> productVOMock
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		sessionBeanMock.getPersonalizedSkus() >> personalizedSkus
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1* requestMock.serviceParameter(prodDetailTestObj.OPARAM_OUTPUT, requestMock, responseMock)
		}
	def "product Droplet Performing with productId  and inventory check throws product no longer available exception."() {
		given:
		 //mocking methods
		Map<String, EximSessionBean> personalizedSkus = new HashMap<String, EximSessionBean>()
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("colorInQueryParamsTBS") >> false
		requestMock.getAttribute("personalizedSku") >> "personalizedSku"
		requestMock.getParameter("color") >> "blue"
		requestMock.getParameter("skuId") >> "675984"
		productVOMock.getChildSKUs() >> ["124545","675984"]
		requestMock.getParameter("removePersonalization") >> true
		productVOMock.isCollection() >> false
		Map<String ,List<AttributeVO>> arrtibutemap = ["PDP":null]
		productVOMock.getAttributesList() >> arrtibutemap
		Map<String ,List<AttributeVO>> rollupAttributes = ["SIZE":null]
		productVOMock.getRollupAttributes() >> rollupAttributes
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		productManagerMock.getProductDetails(_, _) >> {throw new BBBBusinessException(
                                    BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY,
                                    BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY) }
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		sessionBeanMock.getPersonalizedSkus() >> personalizedSkus
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1* requestMock.serviceParameter(prodDetailTestObj.OPARAM_ERROR, requestMock, responseMock)
		}
	def "product Droplet Performing with productId  and inventory check throws product not available in repository exception."() {
		given:
		 //mocking methods
		Map<String, EximSessionBean> personalizedSkus = new HashMap<String, EximSessionBean>()
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("colorInQueryParamsTBS") >> false
		requestMock.getAttribute("personalizedSku") >> "personalizedSku"
		requestMock.getParameter("color") >> "blue"
		requestMock.getParameter("skuId") >> "675984"
		productVOMock.getChildSKUs() >> ["124545","675984"]
		requestMock.getParameter("removePersonalization") >> true
		productVOMock.isCollection() >> false
		Map<String ,List<AttributeVO>> arrtibutemap = ["PDP":null]
		productVOMock.getAttributesList() >> arrtibutemap
		Map<String ,List<AttributeVO>> rollupAttributes = ["NONE":null]
		productVOMock.getRollupAttributes() >> rollupAttributes
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		productManagerMock.getProductDetails(_, _) >> {throw new BBBBusinessException(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
																						BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY) }
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		sessionBeanMock.getPersonalizedSkus() >> personalizedSkus
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1* requestMock.serviceParameter(prodDetailTestObj.OPARAM_ERROR, requestMock, responseMock)
		}
	def "product Droplet Performing with productId  and inventory check throws child products not present for collection item."() {
		given:
		 //mocking methods
		Map<String, EximSessionBean> personalizedSkus = new HashMap<String, EximSessionBean>()
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("colorInQueryParamsTBS") >> false
		requestMock.getAttribute("personalizedSku") >> "personalizedSku"
		requestMock.getParameter("color") >> "blue"
		requestMock.getParameter("skuId") >> "675984"
		productVOMock.getChildSKUs() >> ["124545","675984"]
		requestMock.getParameter("removePersonalization") >> true
		productVOMock.isCollection() >> false
		Map<String ,List<AttributeVO>> arrtibutemap = ["PDP":null]
		productVOMock.getAttributesList() >> arrtibutemap
		Map<String ,List<AttributeVO>> rollupAttributes = ["NONE":null]
		productVOMock.getRollupAttributes() >> rollupAttributes
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		productManagerMock.getProductDetails(_, _) >> {throw new BBBBusinessException(
                                    BBBCatalogErrorCodes.CHILD_PRODUCTS_NOT_PRESENT_FOR_COLLECTION_PRODUCT,
                                    BBBCatalogErrorCodes.CHILD_PRODUCTS_NOT_PRESENT_FOR_COLLECTION_PRODUCT) }
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		sessionBeanMock.getPersonalizedSkus() >> personalizedSkus
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1* requestMock.serviceParameter(prodDetailTestObj.OPARAM_ERROR, requestMock, responseMock)
		}
	def "product Droplet Performing with productId  and inventory check throws unable fecth data from repository."() {
		given:
		 //mocking methods
		Map<String, EximSessionBean> personalizedSkus = new HashMap<String, EximSessionBean>()
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("colorInQueryParamsTBS") >> false
		requestMock.getAttribute("personalizedSku") >> "personalizedSku"
		requestMock.getParameter("color") >> "blue"
		requestMock.getParameter("skuId") >> "675984"
		productVOMock.getChildSKUs() >> ["124545","675984"]
		requestMock.getParameter("removePersonalization") >> true
		productVOMock.isCollection() >> false
		Map<String ,List<AttributeVO>> arrtibutemap = ["PDP":null]
		productVOMock.getAttributesList() >> arrtibutemap
		Map<String ,List<AttributeVO>> rollupAttributes = ["NONE":null]
		productVOMock.getRollupAttributes() >> rollupAttributes
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		productManagerMock.getProductDetails(_, _) >> {throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION) }
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		sessionBeanMock.getPersonalizedSkus() >> personalizedSkus
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1* requestMock.serviceParameter(prodDetailTestObj.OPARAM_ERROR, requestMock, responseMock)
		}
	
	def "product Droplet Performing with productId  with out  inventory check and not a collection item."() {
		given:
		 //mocking methods
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("poc") >> "poc"
		requestMock.getParameter("colorInQueryParamsTBS") >> true
		requestMock.getObjectParameter("colorInQueryParamsTBS") >> true
		requestMock.getAttribute("personalizedSku") >> "personalizedSku"
		requestMock.getParameter("color") >> "blue"
		requestMock.getParameter("checkInventory") >> false
		requestMock.getParameter("showIncartPrice")>> false
		productVOMock.getChildSKUs() >> ["124545"]
		productVOMock.isCollection() >> false
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		Map<String ,List<AttributeVO>> rollupAttributes = ["SIZE":null]
		productVOMock.getRollupAttributes() >> rollupAttributes
		productManagerMock.getProductDetails(_, _) >> productVOMock
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		List<MediaVO> mediaVOListMock = new ArrayList<MediaVO>()
		MediaVO mediaVOMock = new MediaVO()
		mediaVOMock.setProviderId("13")
		mediaVOListMock.add(mediaVOMock)
		productManagerMock.getMediaDetails(_,_) >>  mediaVOListMock
		EximSessionBean eximSessionBeanMock = new EximSessionBean()
		sessionBeanMock.getPersonalizedSkus() >> ["124545":eximSessionBeanMock]
		catalogToolsMock.getZoomIndex(_,_)>>124
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1 * requestMock.setParameter('zoomQuery', 'hei=49600&wid=49600&qlt=50,1')
		}
	def "product Droplet Performing with productId  with out  inventory check and collection item"() {
		given:
		 //mocking methods
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("colorInQueryParamsTBS") >> false
		requestMock.getAttribute("personalizedSku") >> "personalizedSku"
		//requestMock.getParameter("color") >> "blue"
		requestMock.getParameter("checkInventory") >> true
		requestMock.getParameter("registryId") >> "asasas121"
		requestMock.getObjectParameter("isMainProduct") >> true
		requestMock.getParameter("isMainProduct") >> true
		requestMock.getObjectParameter("isDefaultSku") >> true
		requestMock.getParameter("isDefaultSku") >> true
		collectionProductVOMock.getChildSKUs() >> ["124547","675984"]
		collectionProductVOMock.isCollection() >> true
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		productManagerMock.getMainProductDetails(_, _) >> collectionProductVOMock
		pSKUDetailVOMock.getSkuId() >> "124547"
		pSKUDetailVOMock.isVdcSku() >> true
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		List<MediaVO> mediaVOListMock = new ArrayList<MediaVO>()
		MediaVO mediaVOMock = new MediaVO()
		mediaVOMock.setProviderId("12")
		mediaVOListMock.add(mediaVOMock)
		productManagerMock.getMediaDetails(_,_) >>  mediaVOListMock
		List<ProductVO> productListMock = new ArrayList<ProductVO>()
		ProductVO prMock1 = new ProductVO()
		prMock1.setProductId("product1")
		ProductVO prMock2 = new ProductVO()
		prMock2.setProductId("product2")
		productListMock.add(prMock1)
		productListMock.add(prMock2)
		collectionProductVOMock.getChildProducts() >> productListMock 
		inventoryManagerMock.getProductAvailability(_, _, _, 0) >> 1
		EximSessionBean eximSessionBeanMock = new EximSessionBean()
		sessionBeanMock.getPersonalizedSkus() >> ["124547":eximSessionBeanMock]
		KatoriPriceRestVO katoriPriceRestVOMock= new KatoriPriceRestVO()
		bbbeximManagerMock.getPriceByRefKatori(_, _, _, _, _, false, _, _) >>katoriPriceRestVOMock
		def BBBDynamicPriceSkuVO dynamicSKUPriceVOMock = Mock()
		pSKUDetailVOMock.getDynamicSKUPriceVO() >> dynamicSKUPriceVOMock
		dynamicSKUPriceVOMock.inCartFlag() >> true
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		catalogToolsMock.getActualOffsetDate(_,_) >> "Offsetmessage"
		catalogToolsMock.getZoomIndex(_,_)>>124
		collectionProductVOMock.getLeadSKU() >> false
		
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1 * requestMock.setParameter('zoomQuery', 'hei=49600&wid=49600&qlt=50,1')
		1 * requestMock.setParameter('inStock', false)
		1 * requestMock.setParameter("offsetDateVDC","Offsetmessage")
		}
	def "product Droplet Performing with productId  with  inventory check with empty roll up attributes."() {
		given:
		 //mocking methods
		Map<String, EximSessionBean> personalizedSkus = new HashMap<String, EximSessionBean>()
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("colorInQueryParamsTBS") >> false
		requestMock.getAttribute("personalizedSku") >> "personalizedSku"
		requestMock.getParameter("color") >> "blue"
		requestMock.getParameter("skuId") >> "675984"
		productVOMock.getChildSKUs() >> ["124545","675984"]
		requestMock.getParameter("siteId") >> "BedBathUS"
		requestMock.getParameter("removePersonalization") >> true
		productVOMock.isCollection() >> false
		Map<String ,List<AttributeVO>> arrtibutemap = ["PDP":null]
		productVOMock.getAttributesList() >> arrtibutemap
		Map<String ,List<AttributeVO>> rollupAttributes = null
		productVOMock.getRollupAttributes() >> rollupAttributes
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		productManagerMock.getProductDetails(_, _) >> productVOMock
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		sessionBeanMock.getPersonalizedSkus() >> personalizedSkus
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1* requestMock.serviceParameter(prodDetailTestObj.OPARAM_OUTPUT, requestMock, responseMock)
		}
	def "product Droplet Performing with productId  with out  inventory check and without child skus"() {
		given:
		 //mocking methods
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("colorInQueryParamsTBS") >> false
		requestMock.getAttribute("personalizedSku") >> "personalizedSku"
		//requestMock.getParameter("color") >> "blue"
		requestMock.getParameter("checkInventory") >> true
		requestMock.getParameter("registryId") >> "asasas121"
		requestMock.getObjectParameter("isMainProduct") >> true
		requestMock.getParameter("isMainProduct") >> true
		requestMock.getObjectParameter("isDefaultSku") >> true
		requestMock.getParameter("isDefaultSku") >> true
		//collectionProductVOMock.getChildSKUs() >> ["124547","675984"]
		collectionProductVOMock.isCollection() >> true
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		productManagerMock.getMainProductDetails(_, _) >> collectionProductVOMock
		pSKUDetailVOMock.getSkuId() >> "124547"
		pSKUDetailVOMock.isVdcSku() >> true
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		List<MediaVO> mediaVOListMock = new ArrayList<MediaVO>()
		productManagerMock.getMediaDetails(_,_) >>  mediaVOListMock
		List<ProductVO> productListMock = new ArrayList<ProductVO>()
		ProductVO prMock1 = new ProductVO()
		prMock1.setProductId("product1")
		ProductVO prMock2 = new ProductVO()
		prMock2.setProductId("product2")
		productListMock.add(prMock1)
		productListMock.add(prMock2)
		collectionProductVOMock.getChildProducts() >> productListMock
		inventoryManagerMock.getProductAvailability(_, _, _, 0) >> 1
		EximSessionBean eximSessionBeanMock = new EximSessionBean()
		sessionBeanMock.getPersonalizedSkus() >> ["124547":eximSessionBeanMock]
		KatoriPriceRestVO katoriPriceRestVOMock= new KatoriPriceRestVO()
		bbbeximManagerMock.getPriceByRefKatori(_, _, _, _, _, false, _, _) >>katoriPriceRestVOMock
		def BBBDynamicPriceSkuVO dynamicSKUPriceVOMock = Mock()
		pSKUDetailVOMock.getDynamicSKUPriceVO() >> dynamicSKUPriceVOMock
		dynamicSKUPriceVOMock.inCartFlag() >> true
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		catalogToolsMock.getActualOffsetDate(_,_) >> "Offsetmessage"
		collectionProductVOMock.getLeadSKU() >> true
		
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1 * requestMock.setParameter('tabLookUp', true)
		}
	def "product Droplet Performing with productId  with out  inventory check and skuid does not macth in childskus "() {
		given:
		 //mocking methods
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("colorInQueryParamsTBS") >> false
		requestMock.getAttribute("personalizedSku") >> "personalizedSku"
		requestMock.getParameter("skuId") >> "12454"
		requestMock.getParameter("checkInventory") >> true
		requestMock.getParameter("registryId") >> "asasas121"
		requestMock.getObjectParameter("isMainProduct") >> true
		requestMock.getParameter("isMainProduct") >> true
		requestMock.getObjectParameter("isDefaultSku") >> true
		requestMock.getParameter("isDefaultSku") >> true
		collectionProductVOMock.getChildSKUs() >> ["124547","675984"]
		collectionProductVOMock.isCollection() >> true
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		productManagerMock.getMainProductDetails(_, _) >> collectionProductVOMock
		pSKUDetailVOMock.getSkuId() >> "12454"
		pSKUDetailVOMock.isVdcSku() >> true
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		List<MediaVO> mediaVOListMock = new ArrayList<MediaVO>()
		productManagerMock.getMediaDetails(_,_) >>  mediaVOListMock
		List<ProductVO> productListMock = new ArrayList<ProductVO>()
		ProductVO prMock1 = new ProductVO()
		prMock1.setProductId("product1")
		ProductVO prMock2 = new ProductVO()
		prMock2.setProductId("product2")
		productListMock.add(prMock1)
		productListMock.add(prMock2)
		collectionProductVOMock.getChildProducts() >> productListMock
		inventoryManagerMock.getProductAvailability(_, _, _, 0) >> 1
		EximSessionBean eximSessionBeanMock = new EximSessionBean()
		sessionBeanMock.getPersonalizedSkus() >> ["124547":eximSessionBeanMock]
		KatoriPriceRestVO katoriPriceRestVOMock= new KatoriPriceRestVO()
		bbbeximManagerMock.getPriceByRefKatori(_, _, _, _, _, false, _, _) >>katoriPriceRestVOMock
		def BBBDynamicPriceSkuVO dynamicSKUPriceVOMock = Mock()
		pSKUDetailVOMock.getDynamicSKUPriceVO() >> dynamicSKUPriceVOMock
		dynamicSKUPriceVOMock.inCartFlag() >> true
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		catalogToolsMock.getActualOffsetDate(_,_) >> "Offsetmessage"
		collectionProductVOMock.getLeadSKU() >> true
		
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1 * requestMock.setParameter('tabLookUp', true)
		}
	def "product Droplet Performing with productId  thrwos system excpetion for sku setting ActualOffsetDat "() {
		given:
			Map<String, EximSessionBean> personalizedSkus = new HashMap<String, EximSessionBean>()
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("color") >> "blue"
		requestMock.getParameter("checkInventory") >> false
		requestMock.getParameter("isMainProduct") >> false
		collectionProductVOMock.getChildSKUs() >> ["12454534","6894754"]
		collectionProductVOMock.isCollection() >> true
		requestMock.getParameter("removePersonalization") >> false
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		productManagerMock.getProductDetails(_, _) >> collectionProductVOMock
		requestMock.getParameter("registryId") >> "registryId"
		pSKUDetailVOMock.isSkuBelowLine() >> true
		pSKUDetailVOMock.getColor() >> "blue"
		pSKUDetailVOMock.getSkuId() >> "124545"
		pSKUDetailVOMock.isVdcSku() >> true
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		sessionBeanMock.getPersonalizedSkus() >> personalizedSkus
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		List<MediaVO> mediaVOListMock = new ArrayList<MediaVO>()
		MediaVO mediaVOMock = new MediaVO()
		mediaVOMock.setProviderId("12")
		mediaVOListMock.add(mediaVOMock)
		productManagerMock.getMediaDetails(_,_) >>  mediaVOListMock
		List<ProductVO> productListMock = new ArrayList<ProductVO>()
		ProductVO prMock1 = new ProductVO()
		prMock1.setProductId("product1")
		ProductVO prMock2 = new ProductVO()
		prMock2.setProductId("product2")
		productListMock.add(prMock1)
		productListMock.add(prMock2)
		collectionProductVOMock.getChildProducts() >> productListMock
		catalogToolsMock.getActualOffsetDate(_,_) >> {throw new BBBSystemException()}
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1 * requestMock.serviceParameter("output",requestMock,responseMock)
		}
	def "product Droplet Performing with productId  for sku not an VDC item "() {
		given:
			Map<String, EximSessionBean> personalizedSkus = new HashMap<String, EximSessionBean>()
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("color") >> "blue"
		requestMock.getParameter("checkInventory") >> false
		requestMock.getParameter("isMainProduct") >> true
		requestMock.getObjectParameter("isMainProduct") >> true
		collectionProductVOMock.getChildSKUs() >> ["124545"]
		collectionProductVOMock.isCollection() >> true
		requestMock.getParameter("removePersonalization") >> false
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		productManagerMock.getMainProductDetails(_, _) >> collectionProductVOMock
		requestMock.getParameter("registryId") >> "registryId"
		pSKUDetailVOMock.isSkuBelowLine() >> true
		pSKUDetailVOMock.getColor() >> "blue"
		pSKUDetailVOMock.getSkuId() >> "124545"
		pSKUDetailVOMock.isVdcSku() >> false
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		sessionBeanMock.getPersonalizedSkus() >> personalizedSkus
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		List<MediaVO> mediaVOListMock = new ArrayList<MediaVO>()
		MediaVO mediaVOMock = new MediaVO()
		mediaVOMock.setProviderId("12")
		mediaVOListMock.add(mediaVOMock)
		productManagerMock.getMediaDetails(_,_) >>  mediaVOListMock
		List<ProductVO> productListMock = new ArrayList<ProductVO>()
		collectionProductVOMock.getChildProducts() >> productListMock
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1 * requestMock.serviceParameter("output",requestMock,responseMock)
		}
	def "product Droplet Performing with productId  thrwos Business excpetion for sku setting ActualOffsetDat "() {
		given:
			Map<String, EximSessionBean> personalizedSkus = new HashMap<String, EximSessionBean>()
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("color") >> "blue"
		requestMock.getParameter("checkInventory") >> false
		requestMock.getParameter("isMainProduct") >> false
		collectionProductVOMock.getChildSKUs() >> ["124545"]
		collectionProductVOMock.isCollection() >> true
		requestMock.getParameter("removePersonalization") >> false
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		productManagerMock.getProductDetails(_, _) >> collectionProductVOMock
		requestMock.getParameter("registryId") >> "registryId"
		pSKUDetailVOMock.isSkuBelowLine() >> true
		pSKUDetailVOMock.getColor() >> "blue"
		pSKUDetailVOMock.getSkuId() >> "124545"
		pSKUDetailVOMock.isVdcSku() >> true
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		sessionBeanMock.getPersonalizedSkus() >> personalizedSkus
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		List<MediaVO> mediaVOListMock = new ArrayList<MediaVO>()
		MediaVO mediaVOMock = new MediaVO()
		mediaVOMock.setProviderId("12")
		mediaVOListMock.add(mediaVOMock)
		productManagerMock.getMediaDetails(_,_) >>  mediaVOListMock
		List<ProductVO> productListMock = new ArrayList<ProductVO>()
		ProductVO prMock1 = new ProductVO()
		prMock1.setProductId("product1")
		ProductVO prMock2 = new ProductVO()
		prMock2.setProductId("product2")
		productListMock.add(prMock1)
		productListMock.add(prMock2)
		collectionProductVOMock.getChildProducts() >> productListMock
		catalogToolsMock.getActualOffsetDate(_,_) >> {throw new BBBBusinessException("Mock Business Exception")}
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1 * requestMock.serviceParameter("output",requestMock,responseMock)
		}
	def "product Droplet Performing with productId  with  out  inventory check item out of stock, and collection item is not lead sku"() {
		given:
		 //mocking methods
		Map<String, EximSessionBean> personalizedSkus = new HashMap<String, EximSessionBean>()
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("colorInQueryParamsTBS") >> false
		requestMock.getAttribute("personalizedSku") >> "personalizedSku"
		requestMock.getParameter("color") >> "blue"
		requestMock.getParameter("checkInventory") >> false
		requestMock.getObjectParameter("isMainProduct") >> true
		requestMock.getParameter("isMainProduct") >> false
		requestMock.getObjectParameter("isDefaultSku") >> true
		requestMock.getParameter("isDefaultSku") >> true
		collectionProductVOMock.getChildSKUs() >> ["124545","675984"]
		collectionProductVOMock.isCollection() >> true
		requestMock.getParameter("removePersonalization") >> false
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		productManagerMock.getProductDetails(_, _) >> collectionProductVOMock
		requestMock.getParameter("registryId") >> "registryId"
		pSKUDetailVOMock.isSkuBelowLine() >> true
		pSKUDetailVOMock.getColor() >> "blue"
		pSKUDetailVOMock.getSkuId() >> "124545"
		pSKUDetailVOMock.isVdcSku() >> true
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		sessionBeanMock.getPersonalizedSkus() >> personalizedSkus
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		List<MediaVO> mediaVOListMock = new ArrayList<MediaVO>()
		MediaVO mediaVOMock = new MediaVO()
		mediaVOMock.setProviderId("12")
		mediaVOListMock.add(mediaVOMock)
		productManagerMock.getMediaDetails(_,_) >>  mediaVOListMock
		List<ProductVO> productListMock = new ArrayList<ProductVO>()
		ProductVO prMock1 = new ProductVO()
		prMock1.setProductId("product1")
		ProductVO prMock2 = new ProductVO()
		prMock2.setProductId("product2")
		productListMock.add(prMock1)
		productListMock.add(prMock2)
		collectionProductVOMock.getChildProducts() >> productListMock
		catalogToolsMock.getActualOffsetDate(_,_) >> ""
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1* requestMock.setParameter("selected", "124545");
		}
	def "product Droplet Performing with productId  with out  inventory check and collection item is lead sku"() {
		given:
		 //mocking methods
		final StringBuilder builder = new StringBuilder('3267083;product1;product2; ')
		Map<String, EximSessionBean> personalizedSkus = new HashMap<String, EximSessionBean>()
		RepositoryItemMock profileitemMock = new RepositoryItemMock()
		profileitemMock.setRepositoryId("1234");
		List itemsMock = new ArrayList<RepositoryItem>();
		requestMock.getParameter("id") >> "3267083"
		requestMock.getParameter("colorInQueryParamsTBS") >> false
		requestMock.getAttribute("personalizedSku") >> "personalizedSku"
		requestMock.getParameter("color") >> "blue"
		requestMock.getParameter("checkInventory") >> false
		requestMock.getObjectParameter("isMainProduct") >> true
		requestMock.getParameter("isMainProduct") >> false
		requestMock.getObjectParameter("isDefaultSku") >> true
		requestMock.getParameter("isDefaultSku") >> true
		requestMock.getParameter("showIncartPrice") >> true
		collectionProductVOMock.getChildSKUs() >> ["124545","675984"]
		collectionProductVOMock.isCollection() >> true
		collectionProductVOMock.getLeadSKU() >> true
		// requestMock.getParameter("registryId") >> ""
		siteContextManagerMock.getCurrentSiteId() >> "BedBathUS"
		productManagerMock.getProductDetails(_, _) >> collectionProductVOMock
		productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
		prodDetailTestObj.getPersonalizedSkuFromSession(_, _) >> null
		sessionBeanMock.getPersonalizedSkus() >> personalizedSkus
		prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
		giftlistManagerMock.getGiftlistItems(_) >> itemsMock
		prodDetailTestObj.isItemInWishlist(_,_,_,_,_) >> false;
		profileMock.getPropertyValue(_) >> profileitemMock
		List<RollupTypeVO> rollUpMock = new ArrayList<RollupTypeVO>()
		productManagerMock.getRollupDetails(_, _,_, _) >> rollUpMock
		List<MediaVO> mediaVOListMock = new ArrayList<MediaVO>()
		MediaVO mediaVOMock = new MediaVO()
		mediaVOMock.setProviderId("12")
		mediaVOListMock.add(mediaVOMock)
		productManagerMock.getMediaDetails(_,_) >>  mediaVOListMock
		List<ProductVO> productListMock = new ArrayList<ProductVO>()
		ProductVO prMock1 = new ProductVO()
		prMock1.setProductId("product1")
		ProductVO prMock2 = new ProductVO()
		prMock2.setProductId("product2")
		productListMock.add(prMock1)
		productListMock.add(prMock2)
		collectionProductVOMock.getChildProducts() >> productListMock
		def BBBDynamicPriceSkuVO dynamicSKUPriceVOMock = Mock()
		pSKUDetailVOMock.getDynamicSKUPriceVO() >> dynamicSKUPriceVOMock
		dynamicSKUPriceVOMock.inCartFlag() >> true
		productManagerMock.getSKUDetails("BedBathUs",_, _) >> pSKUDetailVOMock
		when:
		prodDetailTestObj.service(requestMock,responseMock)//calling the test methods
		then:
		1 * requestMock.setParameter('tabLookUp', true)
		1 * requestMock.setParameter("linkStringNonRecproduct", "3267083;product1;product2;")
		1 * requestMock.setParameter("rkgCollectionProdIds", "product1,product2")
		}
	
			def " Produc Droplet Performing  method for unregistered customer with save item in sessionBean "(){
			given:
		   //mocking methods
		   profileMock.isTransient() >> true
		   expect:"Returning false for products"
		   prodDetailTestObj.isItemInWishlist("4055126",false,requestMock,"","") == false
		  }
			def " Produc Droplet Performing  method for unregistered customer with  sku id not matching "(){
				given:
			   //mocking methods
			   List<GiftListVO> giftListMock = new ArrayList<GiftListVO>()
			   GiftListVO giftListVOMock = new GiftListVO()
			   giftListVOMock.setSkuID("4055126")
			   giftListVOMock.setReferenceNumber("123456")
			   giftListMock.add(giftListVOMock)
			   savedItemsSessionBeanMock.getSaveItems(true) >> giftListMock
			   profileMock.isTransient() >> true
			   expect:"Returning true for normal products"
			   prodDetailTestObj.isItemInWishlist("405512",false,requestMock,"","") == false
			  }
			def " Produc Droplet Performing  method for unregistered customer with  not sku id "(){
				given:
			   //mocking methods
			   List<GiftListVO> giftListMock = new ArrayList<GiftListVO>()
			   GiftListVO giftListVOMock = new GiftListVO()
			   giftListVOMock.setSkuID("4055126")
			   giftListVOMock.setReferenceNumber("123456")
			   giftListMock.add(giftListVOMock)
			   savedItemsSessionBeanMock.getSaveItems(true) >> giftListMock
			   profileMock.isTransient() >> true
			   expect:"Returning true for normal products"
			   prodDetailTestObj.isItemInWishlist(null,false,requestMock,"","") == false
			  }
			def " Produc Droplet Performing  method for unregistered customer with  empty sku id "(){
				given:
			   //mocking methods
			   List<GiftListVO> giftListMock = new ArrayList<GiftListVO>()
			   GiftListVO giftListVOMock = new GiftListVO()
			   giftListVOMock.setSkuID("4055126")
			   giftListVOMock.setReferenceNumber("123456")
			   giftListMock.add(giftListVOMock)
			   savedItemsSessionBeanMock.getSaveItems(true) >> giftListMock
			   profileMock.isTransient() >> true
			   expect:"Returning true for normal products"
			   prodDetailTestObj.isItemInWishlist("",false,requestMock,"","") == false
			  }
			def " Produc Droplet Performing  method for unregistered customer non LTLItem and dose not have personalizationType "(){
			given:
		   //mocking methods
		   List<GiftListVO> giftListMock = new ArrayList<GiftListVO>()
		   GiftListVO giftListVOMock = new GiftListVO()
		   giftListVOMock.setSkuID("4055126")
		   giftListVOMock.setReferenceNumber("123456")
		   giftListMock.add(giftListVOMock)
		   savedItemsSessionBeanMock.getSaveItems(true) >> giftListMock
		   profileMock.isTransient() >> true
		   expect:"Returning true for normal products"
		   prodDetailTestObj.isItemInWishlist("4055126",false,requestMock,"","") == true
		  }
			def " Produc Droplet Performing  method for unregistered customer non LTLItem having personalized type as null "(){
				given:
			   //mocking methods
			   List<GiftListVO> giftListMock = new ArrayList<GiftListVO>()
			   GiftListVO giftListVOMock = new GiftListVO()
			   giftListVOMock.setSkuID("4055126")
			   giftListMock.add(giftListVOMock)
			   savedItemsSessionBeanMock.getSaveItems(true) >> giftListMock
			   profileMock.isTransient() >> true
			   expect:"Returning true for normal products"
			   prodDetailTestObj.isItemInWishlist("4055126",false,requestMock,null,"") == true
			  }
			def " Produc Droplet Performing  method for unregistered customer non LTLItem having personalizedSku as empty "(){
				given:
			   //mocking methods
			   List<GiftListVO> giftListMock = new ArrayList<GiftListVO>()
			   GiftListVO giftListVOMock = new GiftListVO()
			   giftListVOMock.setSkuID("4055126")
			   giftListMock.add(giftListVOMock)
			   savedItemsSessionBeanMock.getSaveItems(true) >> giftListMock
			   profileMock.isTransient() >> true
			   expect:"Returning true for normal products"
			   prodDetailTestObj.isItemInWishlist("4055126",false,requestMock,null,"") == true
			  }
		def " Produc Droplet Performing  method for unregistered customer non LTLItem  "(){
			given:
		   //mocking methods
		   
		   List<GiftListVO> giftListMock = new ArrayList<GiftListVO>()
		   GiftListVO giftListVOMock = new GiftListVO()
		   giftListVOMock.setSkuID("4055126")
		   giftListVOMock.setReferenceNumber("123456")
		   giftListMock.add(giftListVOMock)
		   savedItemsSessionBeanMock.getSaveItems(true) >> giftListMock
		   profileMock.isTransient() >> true
		   expect:"Returning true for normal products"
		   prodDetailTestObj.isItemInWishlist("4055126",false,requestMock,"","") == true
		  }
		def " Produc Droplet Performing  method for unregistered customer an LTLItem user already done personalization  "(){
			given:
		   //mocking methods
		   requestMock.getAttribute("personalizedSku") >> "3267083"
		   List<GiftListVO> giftListMock = new ArrayList<GiftListVO>()
		   GiftListVO giftListVOMock = new GiftListVO()
		   giftListVOMock.setSkuID("4055126")
		   giftListVOMock.setReferenceNumber("123456")
		   giftListMock.add(giftListVOMock)
		   savedItemsSessionBeanMock.getSaveItems(true) >> giftListMock
		   profileMock.isTransient() >> true
		   expect:"Returning true for In progress personalized product"
		   prodDetailTestObj.isItemInWishlist("4055126",true,requestMock,"","") == true
		  }
		def " Produc Droplet Performing  method for unregistered customer an LTLItem with personalization   "(){
			given:
		   //mocking methods
		   List<GiftListVO> giftListMock = new ArrayList<GiftListVO>()
		   GiftListVO giftListVOMock = new GiftListVO()
		   giftListVOMock.setSkuID("4055126")
		   giftListMock.add(giftListVOMock)
		   savedItemsSessionBeanMock.getSaveItems(true) >> giftListMock
		   profileMock.isTransient() >> true
		   expect:"Returning true item does not exist in gift list"
		   prodDetailTestObj.isItemInWishlist("4055126",true,requestMock,"personalizationType","") == true
		  }
		def " Produc Droplet Performing  method for unregistered customer an LTLItem with personalization and sku is availble in giftlist   "(){
			given:
		   //mocking methods
		   List itemsMock = new ArrayList<RepositoryItem>();
		   List<GiftListVO> giftListMock = new ArrayList<GiftListVO>()
		   GiftListVO giftListVOMock = new GiftListVO()
		   giftListVOMock.setSkuID("4055126")
		   giftListVOMock.setReferenceNumber("123456")
		   
		   giftListMock.add(giftListVOMock)
		   savedItemsSessionBeanMock.getSaveItems(true) >> giftListMock
		   profileMock.isTransient() >> true
		   expect:"Returning true item does not personalization"
		   prodDetailTestObj.isItemInWishlist("4055126",true,requestMock,"personalizationType","") == false
		  }
		def " Produc Droplet Performing  method for unregistered customer an LTLItem  sku is availble in giftlist and having shipping method  "(){
			given:
		   //mocking methods
			requestMock.getAttribute("personalizedSku") >> "personalizedSku"
		   List itemsMock = new ArrayList<RepositoryItem>();
		   List<GiftListVO> giftListMock = new ArrayList<GiftListVO>()
		   GiftListVO giftListVOMock = new GiftListVO()
		   giftListVOMock.setSkuID("4055126")
		   giftListVOMock.setLtlShipMethod("Standard")
		   giftListMock.add(giftListVOMock)
		   savedItemsSessionBeanMock.getSaveItems(true) >> giftListMock
		   profileMock.isTransient() >> true
		   expect:
		   prodDetailTestObj.isItemInWishlist("4055126",true,requestMock,"","Standard") == true
		  }
		def " Produc Droplet Performing  method for unregistered customer an LTLItem  sku is availble in giftlist and with out shipping method  "(){
			given:
		   //mocking methods
		   List itemsMock = new ArrayList<RepositoryItem>();
		   List<GiftListVO> giftListMock = new ArrayList<GiftListVO>()
		   GiftListVO giftListVOMock = new GiftListVO()
		   giftListVOMock.setSkuID("4055126")
		   giftListVOMock.setLtlShipMethod("Standard")
		   giftListMock.add(giftListVOMock)
		   savedItemsSessionBeanMock.getSaveItems(true) >> giftListMock
		   profileMock.isTransient() >> true
		   expect:
		   prodDetailTestObj.isItemInWishlist("4055126",true,requestMock,"","") == false
		  }
		def " Produc Droplet Performing  method for registered customer with out sku id "(){
			given:
		   //mocking methods
			RepositoryItemMock profileItemMock = new RepositoryItemMock()
			profileItemMock.setRepositoryId("1234");
			profileMock.getPropertyValue(_) >> profileItemMock;
			List itemsMockList = new ArrayList<RepositoryItem>();
			RepositoryItemMock itemMock = new RepositoryItemMock()
			HashMap dataMap = ["catalogRefId":"40552126"]
			itemMock.setProperties(dataMap)
			itemsMockList.add(itemMock)
			giftlistManagerMock.getGiftlistItems(_) >> itemsMockList
			expect:"Returning true for normal products"
			prodDetailTestObj.isItemInWishlist(null,false,requestMock,"","") == false
		   
		  }
		def " Produc Droplet Performing  method for registered customer with out having wishlist in profile "(){
			given:
		   //mocking methods
			RepositoryItemMock itemMock = new RepositoryItemMock()
			itemMock.setRepositoryId("1234");
			profileMock.getPropertyValue(_) >> itemMock;
		   expect:"Returning false for products"
		   prodDetailTestObj.isItemInWishlist("4055126",false,requestMock,"","") == false
		   
		  }
		def " Produc Droplet Performing  method for registered customer non LTLItem  different sku ids"(){
			given:
			RepositoryItemMock profileItemMock = new RepositoryItemMock()
			profileItemMock.setRepositoryId("1234");
			profileMock.getPropertyValue(_) >> profileItemMock;
			List itemsMockList = new ArrayList<RepositoryItem>();
			RepositoryItemMock itemMock = new RepositoryItemMock()
			HashMap dataMap = ["catalogRefId":"40552126"]
			itemMock.setProperties(dataMap)
			itemsMockList.add(itemMock)
			giftlistManagerMock.getGiftlistItems(_) >> itemsMockList
			expect:"Returning true for normal products"
			prodDetailTestObj.isItemInWishlist("4055126",false,requestMock,"","") == false
		  }
		def " Produc Droplet Performing  method for registered customer non LTLItem  "(){
			given:
			RepositoryItemMock profileItemMock = new RepositoryItemMock()
			profileItemMock.setRepositoryId("1234");
			profileMock.getPropertyValue(_) >> profileItemMock;
			List itemsMockList = new ArrayList<RepositoryItem>();
			RepositoryItemMock itemMock = new RepositoryItemMock()
			HashMap dataMap = ["catalogRefId":"4055126"]
			itemMock.setProperties(dataMap)
			itemsMockList.add(itemMock)
			giftlistManagerMock.getGiftlistItems(_) >> itemsMockList
			expect:"Returning true for normal products"
			prodDetailTestObj.isItemInWishlist("4055126",false,requestMock,"","") == true
		  }
		def " Produc Droplet Performing  method for registered customer an LTLItem with personalization and user already done personalization  "(){
			given:
		   //mocking methods
		    RepositoryItemMock profileItemMock = new RepositoryItemMock()
			profileItemMock.setRepositoryId("1234");
			profileMock.getPropertyValue(_) >> profileItemMock;
			List itemsMockList = new ArrayList<RepositoryItem>();
			requestMock.getAttribute("personalizedSku") >> "3267083"
			RepositoryItemMock itemMock = new RepositoryItemMock()
			HashMap dataMap = ["referenceNumber":"sds876","catalogRefId":"4055126"]
			itemMock.setProperties(dataMap)
			itemsMockList.add(itemMock)
			giftlistManagerMock.getGiftlistItems(_) >> itemsMockList
		    expect:"Returning true for In progress personalized product"
		    prodDetailTestObj.isItemInWishlist("4055126",true,requestMock,"","") == true
		  }
		def " Produc Droplet Performing  method for registered customer an LTLItem with  personalization  "(){
			given:
		   //mocking methods
		    RepositoryItemMock profileItemMock = new RepositoryItemMock()
			profileItemMock.setRepositoryId("1234");
			profileMock.getPropertyValue(_) >> profileItemMock;
			List itemsMockList = new ArrayList<RepositoryItem>();
			RepositoryItemMock itemMock = new RepositoryItemMock()
			HashMap dataMap = ["catalogRefId":"4055126"]
			itemMock.setProperties(dataMap)
			itemsMockList.add(itemMock)
			requestMock.getParameter("itemPersonalized") >> true
			giftlistManagerMock.getGiftlistItems(_) >> itemsMockList
		   expect:"Returning true for In progress personalized product"
		   prodDetailTestObj.isItemInWishlist("4055126",true,requestMock,"personalizationType","") == false
		  }
		def " Produc Droplet Performing  method for registered customer an LTLItem without  personalization  "(){
			given:
		   //mocking methods
			RepositoryItemMock profileItemMock = new RepositoryItemMock()
			profileItemMock.setRepositoryId("1234");
			profileMock.getPropertyValue(_) >> profileItemMock;
			List itemsMockList = new ArrayList<RepositoryItem>();
			RepositoryItemMock itemMock = new RepositoryItemMock()
			HashMap dataMap = ["catalogRefId":"4055126"]
			itemMock.setProperties(dataMap)
			itemsMockList.add(itemMock)
			giftlistManagerMock.getGiftlistItems(_) >> itemsMockList
		   expect:"Returning true for In progress personalized product"
		   prodDetailTestObj.isItemInWishlist("4055126",true,requestMock,"personalizationType","") == true
		  }
		def " Produc Droplet Performing  method for registered customer an LTLItem without  personalization and sku does not mach "(){
			given:
		   //mocking methods
			RepositoryItemMock profileItemMock = new RepositoryItemMock()
			profileItemMock.setRepositoryId("1234");
			profileMock.getPropertyValue(_) >> profileItemMock;
			List itemsMockList = new ArrayList<RepositoryItem>();
			RepositoryItemMock itemMock = new RepositoryItemMock()
			HashMap dataMap = ["referenceNumber":"sds876","catalogRefId":"4055126"]
			itemMock.setProperties(dataMap)
			itemsMockList.add(itemMock)
			requestMock.getParameter("itemPersonalized") >> false
			giftlistManagerMock.getGiftlistItems(_) >> itemsMockList
		   expect:"Returning true for In progress personalized product"
		   prodDetailTestObj.isItemInWishlist("4055126",true,requestMock,"personalizationType","") == false
		  }
		def " Produc Droplet Performing  method for registered customer an LTLItem  sku is availble in giftlist and having shipping method  "(){
			given:
		    RepositoryItemMock profileItemMock = new RepositoryItemMock()
			profileItemMock.setRepositoryId("1234");
			profileMock.getPropertyValue(_) >> profileItemMock;
			List itemsMockList = new ArrayList<RepositoryItem>();
			RepositoryItemMock itemMock = new RepositoryItemMock()
			HashMap dataMap = ["ltlShipMethod":"Standard","catalogRefId":"4055126"]
			itemMock.setProperties(dataMap)
			itemsMockList.add(itemMock)
			giftlistManagerMock.getGiftlistItems(_) >> itemsMockList
		  	expect:"Returning true item does not exist in gift list"
		   prodDetailTestObj.isItemInWishlist("4055126",true,requestMock,"","Standard") == true
		  }
		def " Produc Droplet Performing  method for registered customer an LTLItem  sku is availble in giftlist and with out shipping method  "(){
			given:
		    RepositoryItemMock profileItemMock = new RepositoryItemMock()
			profileItemMock.setRepositoryId("1234");
			profileMock.getPropertyValue(_) >> profileItemMock;
			List itemsMockList = new ArrayList<RepositoryItem>();
			requestMock.getAttribute("personalizedSku") >> "3267083"
			RepositoryItemMock itemMock = new RepositoryItemMock()
			HashMap dataMap = ["catalogRefId":"4055126"]
			itemMock.setProperties(dataMap)
			itemsMockList.add(itemMock)
			giftlistManagerMock.getGiftlistItems(_) >> itemsMockList
		  	expect:"Returning true item does not exist in gift list"
		   prodDetailTestObj.isItemInWishlist("4055126",true,requestMock,"","Standard") == false
		  }
		def " Produc Droplet Performing  fetching sku rollup details based on color and finish  "(){
			given:
			ProductVO productDetailsMock = Mock()
			productDetailsMock.getProductId() >> "4560124"
			RollupTypeVO rollupTypeVOMock = new RollupTypeVO()
			rollupTypeVOMock.setFirstRollUp("blue")
			List<RollupTypeVO> rollUpList = [rollupTypeVOMock]
			List<RollupTypeVO> rollUpEmptyList = new ArrayList<RollupTypeVO>()
			productManagerMock.getRollupDetails("4560124","blue","color","finish") >> rollUpEmptyList
			productManagerMock.getProductStatus("bedBathUS","4560124") >> true
			productManagerMock.getEverLivingSKUId("bedBathUS", "4560124", _) >> "104560124"
			expect:"Returning sku id "
			prodDetailTestObj.getSkuIDForColor(productDetailsMock,"blue","bedBathUS","4560124") == null
		  }
		def " Produc Droplet Performing  fetching sku rollup details based on different color and finish  "(){
			given:
			ProductVO productDetailsMock = Mock()
			productDetailsMock.getProductId() >> "4560124"
			RollupTypeVO rollupTypeVOMock = new RollupTypeVO()
			rollupTypeVOMock.setFirstRollUp("green")
			List<RollupTypeVO> rollUpList = [rollupTypeVOMock]
			productManagerMock.getRollupDetails("4560124","blue","color","size") >> rollUpList
			productManagerMock.getProductStatus("bedBathUS","4560124") >> true
			productManagerMock.getEverLivingSKUId("bedBathUS", "4560124", _) >> "104560124"
			expect:"Returning sku id "
			prodDetailTestObj.getSkuIDForColor(productDetailsMock,"blue","bedBathUS","4560124") == null
		  }
		def " Produc Droplet Performing  fetching sku rollup details based on color and size  and not an everLiving product"(){
			given:
			ProductVO productDetailsMock = Mock()
			productDetailsMock.getProductId() >> "4560124"
			RollupTypeVO rollupTypeVOMock = new RollupTypeVO()
			rollupTypeVOMock.setFirstRollUp("blue")
			List<RollupTypeVO> rollUpList = [rollupTypeVOMock]
			productManagerMock.getRollupDetails("4560124","blue","color","size") >> rollUpList
			productManagerMock.getProductStatus("bedBathUS","4560124") >> false
			productManagerMock.getSKUId("bedBathUS", "4560124", _) >> "104560124"
			expect:"Returning sku id "
		    prodDetailTestObj.getSkuIDForColor(productDetailsMock,"blue","bedBathUS","4560124").equalsIgnoreCase("104560124") == true
		  }
		def " Produc Droplet Performing  fetching sku rollup details based on color and size  and  everLiving product"(){
			given:
			ProductVO productDetailsMock = Mock()
			productDetailsMock.getProductId() >> "4560124"
			RollupTypeVO rollupTypeVOMock = new RollupTypeVO()
			rollupTypeVOMock.setFirstRollUp("blue")
			List<RollupTypeVO> rollUpList = [rollupTypeVOMock]
			productManagerMock.getRollupDetails("4560124","blue","color","size") >> rollUpList
			productManagerMock.getProductStatus("bedBathUS","4560124") >> true
			productManagerMock.getEverLivingSKUId("bedBathUS", "4560124", _) >> "104560124"
			expect:"Returning sku id "
			prodDetailTestObj.getSkuIDForColor(productDetailsMock,"blue","bedBathUS","4560124").equalsIgnoreCase("104560124") == true
		  }
		
		def "product Droplet Performing method explodes the Child Products of a collection based on the rollup attributes of the collection and child products with color and siZE." (){
			given:
			 //mocking methods
			
			CollectionProductVO collectionProductVO = new CollectionProductVO()
			List<ProductVO> productVOListMock = new ArrayList<ProductVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
			RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
			colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
			prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
			Map<String,List<RollupTypeVO>> prdRelationUpMock = ["SIZE":prdRelationRollupTypeVOFinsh,"COLOR":prdRelationRollupTypeVOColor]
			Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["SIZE":prdRelationRollupTypeVOFinish]
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
			prodDetailTestObj.createChildProducts(collectionProductVO, "BedBathUS")
			then:
			collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct - RollupAttribute") == true
			}
		def "product Droplet Performing method explodes the Child Products of a collection based on the different rollup attributes and rekations ship." (){
			given:
			 //mocking methods
			
			CollectionProductVO collectionProductVO = new CollectionProductVO()
			List<ProductVO> productVOListMock = new ArrayList<ProductVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
			RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
			colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
			prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
			Map<String,List<RollupTypeVO>> prdRelationUpMock = ["COLOR":prdRelationRollupTypeVOColor]
			Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["COLOR":prdRelationRollupTypeVOFinish]
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
			prodDetailTestObj.createChildProducts(collectionProductVO, "BedBathUS")
			then:
			collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct - RollupAttribute") == false
			}
		def "product Droplet Performing method explodes the Child Products of a collection based on with out color attribute" (){
			given:
			 //mocking methods
			
			CollectionProductVO collectionProductVO = new CollectionProductVO()
			List<ProductVO> productVOListMock = new ArrayList<ProductVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
			RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
			colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
			prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
			Map<String,List<RollupTypeVO>> prdRelationUpMock = ["size":prdRelationRollupTypeVOColor,"FINISH":prdRelationRollupTypeVOColor]
			Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["COLOR":prdRelationRollupTypeVOFinish]
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
			prodDetailTestObj.createChildProducts(collectionProductVO, "BedBathUS")
			then:
			collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct - RollupAttribute") == false
			}
		def "product Droplet Performing method explodes the Child Products of a collection based on with  color attribute" (){
			given:
			 //mocking methods
			
			CollectionProductVO collectionProductVO = new CollectionProductVO()
			List<ProductVO> productVOListMock = new ArrayList<ProductVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
			RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
			colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
			prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
			Map<String,List<RollupTypeVO>> prdRelationUpMock = ["COLOR":prdRelationRollupTypeVOColor,"FINISH":prdRelationRollupTypeVOColor]
			Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["SIZE":prdRelationRollupTypeVOFinish]
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
			prodDetailTestObj.createChildProducts(collectionProductVO, "BedBathUS")
			then:
			collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct - RollupAttribute") == false
			}
		def "product Droplet Performing method explodes the Child Products as null." (){
			given:
			 //mocking methods
			
			CollectionProductVO collectionProductVO = new CollectionProductVO()
			when:
			prodDetailTestObj.createChildProducts(collectionProductVO, "BedBathUS")
			then:
			collectionProductVO.getChildProducts() == null
			}
		def "product Droplet Performing method explodes the productVO as null." (){
			given:
			 //mocking methods
			
			CollectionProductVO collectionProductVO = null
			when:
			prodDetailTestObj.createChildProducts(collectionProductVO, "BedBathUS")
			then:
			collectionProductVO == null
			}
		def "product Droplet Performing method explodes the Child Products of a collection based on the rollup attributes of the collection trows Bussiness Exception." (){
			given:
			 //mocking methods
			
			List<ProductVO> ProductVOListMock = new ArrayList<ProductVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
			RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
			prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
			Map<String,List<RollupTypeVO>> prdRelationUpMock = ["SIZE":prdRelationRollupTypeVOFinsh,"COLOR":prdRelationRollupTypeVOColor]
			Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["SIZE":prdRelationRollupTypeVOFinish]
			ProductVO ProductVOMock = new ProductVO()
			ProductVOMock.setName("TestProduct")
			ProductVOMock.setPrdRelationRollup(prdRelationRollupMock)
			ProductVOMock.setRollupAttributes(prdRelationUpMock)
			ProductVOListMock.add(ProductVOMock)
			collectionProductVOMock.getChildSKUs() >> ["124545"]
			collectionProductVOMock.isCollection() >> true
			collectionProductVOMock.getChildProducts() >> ProductVOListMock
			productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
			prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
			productManagerMock.getRollupDetails(_, _, _, _) >> {throw new BBBBusinessException("Mock Business Exception")}
			productManagerMock.getImmediateParentCategoryForProduct(_,_) >> "ImmediateParentCategory"
			expect:
			prodDetailTestObj.createChildProducts(collectionProductVOMock, "BedBathUS")
			}
		def "product Droplet Performing method explodes the Child Products of a collection based on the rollup attributes of the collection throws system excpetion" (){
			given:
			 //mocking methods
			
			List<ProductVO> ProductVOListMock = new ArrayList<ProductVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
			RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
			prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
			Map<String,List<RollupTypeVO>> prdRelationUpMock = ["SIZE":prdRelationRollupTypeVOFinsh,"COLOR":prdRelationRollupTypeVOColor]
			Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["SIZE":prdRelationRollupTypeVOFinish]
			ProductVO ProductVOMock = new ProductVO()
			ProductVOMock.setName("TestProduct")
			ProductVOMock.setPrdRelationRollup(prdRelationRollupMock)
			ProductVOMock.setRollupAttributes(prdRelationUpMock)
			ProductVOListMock.add(ProductVOMock)
			collectionProductVOMock.getChildSKUs() >> ["124545"]
			collectionProductVOMock.isCollection() >> true
			collectionProductVOMock.getChildProducts() >> ProductVOListMock
			productManagerMock.getSKUDetails(_,_,_) >> pSKUDetailVOMock
			prodDetailTestObj.getGiftlistManager() >> giftlistManagerMock
			productManagerMock.getRollupDetails(_, _, _, _) >> {throw new BBBSystemException("Mock System Exception")}
			productManagerMock.getImmediateParentCategoryForProduct(_,_) >> "ImmediateParentCategory"
			expect:
			prodDetailTestObj.createChildProducts(collectionProductVOMock, "BedBathUS")
			}
		def "product Droplet Performing method explodes the Child Products of a collection based on the rollup attributes of the collection and child products with Finish and Size ." (){
			given:
			 //mocking methods
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
			prodDetailTestObj.createChildProducts(collectionProductVO, "BedBathUS")
			then:
			collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct - RollupAttribute") == true
			}
		def "product Droplet Performing method explodes the Child Products of a collection based on the rollup   and releation ship  same  attributes." (){
			given:
			 //mocking methods
			CollectionProductVO collectionProductVO = new CollectionProductVO()
			List<ProductVO> productVOListMock = new ArrayList<ProductVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
			RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
			colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
			prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
			Map<String,List<RollupTypeVO>> prdRelationUpMock = ["FINISH":prdRelationRollupTypeVOFinsh,"COLOR":prdRelationRollupTypeVOColor]
			Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["FINISH":prdRelationRollupTypeVOFinish,"COLOR":prdRelationRollupTypeVOColor]
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
			prodDetailTestObj.createChildProducts(collectionProductVO, "BedBathUS")
			then:
			collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct") == true
			}
		def "product Droplet Performing method explodes the Child Products of a collection based on the rollup   and releation ship  different  attributes." (){
			given:
			 //mocking methods
			CollectionProductVO collectionProductVO = new CollectionProductVO()
			List<ProductVO> productVOListMock = new ArrayList<ProductVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
			RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
			colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
			prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
			Map<String,List<RollupTypeVO>> prdRelationUpMock = ["FINISH":prdRelationRollupTypeVOFinsh,"COLOR":prdRelationRollupTypeVOColor]
			Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["FINISH":prdRelationRollupTypeVOFinish,"COLOR":prdRelationRollupTypeVOColor]
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
			prodDetailTestObj.createChildProducts(collectionProductVO, "BedBathUS")
			then:
			collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct") == true
			}
		def "product Droplet Performing method explodes the Child Products of a collection based on the rollup attributes of the collection less than one sku details ." (){
			given:
			 //mocking methods
			CollectionProductVO collectionProductVO = new CollectionProductVO()
			List<ProductVO> productVOListMock = new ArrayList<ProductVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
			RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
			colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
			prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
			Map<String,List<RollupTypeVO>> prdRelationUpMock = ["FINISH":prdRelationRollupTypeVOFinsh,"COLOR":prdRelationRollupTypeVOColor]
			Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["FINISH":prdRelationRollupTypeVOFinish,"COLOR":prdRelationRollupTypeVOColor]
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
			prodDetailTestObj.createChildProducts(collectionProductVO, "BedBathUS")
			then:
			collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct") == true
			}
		def "product Droplet Performing method explodes the Child Products of a collection based on the rollup attributes with out RollupAttributes ." (){
			given:
			 //mocking methods
			CollectionProductVO collectionProductVO = new CollectionProductVO()
			List<ProductVO> productVOListMock = new ArrayList<ProductVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
			RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
			prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
			Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["FINISH":prdRelationRollupTypeVOFinish,"COLOR":prdRelationRollupTypeVOColor]
			ProductVO productMock = new ProductVO()
			productMock.setName("TestProduct")
			productMock.setPrdRelationRollup(prdRelationRollupMock)
			productMock.setRollupAttributes(null)
			productVOListMock.add(productMock)
			List listChildSku = new ArrayList<String>()
				listChildSku.add("124545")
				collectionProductVO.setProductId("10124545")
				collectionProductVO.setChildSKUs(listChildSku)
				collectionProductVO.setCollection(true)
				collectionProductVO.setChildProducts(productVOListMock)
				productManagerMock.getRollupDetails(_, _, _, _) >> prdRelationRollupTypeVOColor
			when:
			prodDetailTestObj.createChildProducts(collectionProductVO, "BedBathUS")
			then:
			collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct") == true
			}
		def "product Droplet Performing method explodes the Child Products of a collection based on the rollup attributes with out  RelationRollup Attributes ." (){
			given:
			 //mocking methods
			CollectionProductVO collectionProductVO = new CollectionProductVO()
			List<ProductVO> productVOListMock = new ArrayList<ProductVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
			RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
			prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
			Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["FINISH":prdRelationRollupTypeVOFinish,"COLOR":prdRelationRollupTypeVOColor]
			ProductVO productMock = new ProductVO()
			productMock.setName("TestProduct")
			productMock.setPrdRelationRollup(null)
			productMock.setRollupAttributes(prdRelationRollupMock)
			productVOListMock.add(productMock)
			List listChildSku = new ArrayList<String>()
				listChildSku.add("124545")
				collectionProductVO.setProductId("10124545")
				collectionProductVO.setChildSKUs(listChildSku)
				collectionProductVO.setCollection(true)
				collectionProductVO.setChildProducts(productVOListMock)
				productManagerMock.getRollupDetails(_, _, _, _) >> prdRelationRollupTypeVOColor
			when:
			prodDetailTestObj.createChildProducts(collectionProductVO, "BedBathUS")
			then:
			collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct") == true
			}
			def "product Droplet Performing method explodes Child Products of a collection to SKU level based on the rollup attributes of the child products more than one." (){
			given:
			 //mocking methods
			CollectionProductVO collectionProductVO = new CollectionProductVO()
			List<ProductVO> ProductVOListMock = new ArrayList<ProductVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
			List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
			RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
			colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
			prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
			Map<String,List<RollupTypeVO>> prdRelationUpMock = ["FINISH":prdRelationRollupTypeVOColor,"COLOR":prdRelationRollupTypeVOColor]
			Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["NONE":prdRelationRollupTypeVOFinish,"FINISH":prdRelationRollupTypeVOColor]
			ProductVO ProductVOMock = new ProductVO()
			ProductVOMock.setName("TestProduct")
			ProductVOMock.setPrdRelationRollup(prdRelationRollupMock)
			ProductVOMock.setRollupAttributes(prdRelationUpMock)
			ProductVOListMock.add(ProductVOMock)
			List listChildSku = new ArrayList<String>()
				listChildSku.add("124545")
				collectionProductVO.setProductId("10124545")
				collectionProductVO.setChildSKUs(listChildSku)
				collectionProductVO.setCollection(true)
				collectionProductVO.setChildProducts(ProductVOListMock)
			productManagerMock.getRollupDetails(_, _, _, _) >> prdRelationRollupTypeVOColor
			when:
				prodDetailTestObj.createChildProductsSkuLevel(collectionProductVO, "BedBathUS")
			then:
				collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct - RollupAttribute - RollupAttribute") == true
			}
			def "product Droplet Performing method explodes Child Products of a collection to SKU level based on the rollup attributes as empty of the child products more than one." (){
				given:
				 //mocking methods
				CollectionProductVO collectionProductVO = new CollectionProductVO()
				List<ProductVO> ProductVOListMock = new ArrayList<ProductVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
				RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
				colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
				prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
				Map<String,List<RollupTypeVO>> prdRelationUpMock =new HashMap<String,List<RollupTypeVO>>()
				Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["NONE":prdRelationRollupTypeVOFinish,"FINISH":prdRelationRollupTypeVOColor]
				ProductVO ProductVOMock = new ProductVO()
				ProductVOMock.setName("TestProduct")
				ProductVOMock.setPrdRelationRollup(prdRelationRollupMock)
				ProductVOMock.setRollupAttributes(prdRelationUpMock)
				ProductVOListMock.add(ProductVOMock)
				List listChildSku = new ArrayList<String>()
					listChildSku.add("124545")
					collectionProductVO.setProductId("10124545")
					collectionProductVO.setChildSKUs(listChildSku)
					collectionProductVO.setCollection(true)
					collectionProductVO.setChildProducts(ProductVOListMock)
				productManagerMock.getRollupDetails(_, _, _, _) >> prdRelationRollupTypeVOColor
				when:
					prodDetailTestObj.createChildProductsSkuLevel(collectionProductVO, "BedBathUS")
				then:
					collectionProductVO.getChildProducts().size() == 0
				}
			def "product Droplet Performing method explodes does not haver Child Products " (){
				given:
				collectionProductVOMock.isCollection() >> true
				expect:
				prodDetailTestObj.createChildProductsSkuLevel(collectionProductVOMock, "BedBathUS")
				}
			def "product Droplet Performing method explodes Child Products of a collection to SKU level based on the rollup attributes thows system exception" (){
				given:
				 //mocking methods
				List<ProductVO> ProductVOListMock = new ArrayList<ProductVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
				RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
				prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
				Map<String,List<RollupTypeVO>> prdRelationUpMock = ["FINISH":prdRelationRollupTypeVOColor,"COLOR":prdRelationRollupTypeVOColor]
				Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["NONE":prdRelationRollupTypeVOFinish,"FINISH":prdRelationRollupTypeVOColor]
				ProductVO ProductVOMock = new ProductVO()
				ProductVOMock.setName("TestProduct")
				ProductVOMock.setPrdRelationRollup(prdRelationRollupMock)
				ProductVOMock.setRollupAttributes(prdRelationUpMock)
				ProductVOListMock.add(ProductVOMock)
				collectionProductVOMock.getChildSKUs() >> ["124545"]
				collectionProductVOMock.isCollection() >> true
				collectionProductVOMock.getChildProducts() >> ProductVOListMock
				productManagerMock.getRollupDetails(_, _, _, _) >> {throw new BBBSystemException("Mock System Exception")}
				expect:
				prodDetailTestObj.createChildProductsSkuLevel(collectionProductVOMock, "BedBathUS")
				}
			
			def "product Droplet Performing method explodes Child Products of a collection to SKU level based on the rollup attributes thows Business exception" (){
				given:
				 //mocking methods
				
				List<ProductVO> ProductVOListMock = new ArrayList<ProductVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
				RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
				prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
				Map<String,List<RollupTypeVO>> prdRelationUpMock = ["FINISH":prdRelationRollupTypeVOColor,"COLOR":prdRelationRollupTypeVOColor]
				Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["NONE":prdRelationRollupTypeVOFinish,"FINISH":prdRelationRollupTypeVOColor]
				ProductVO ProductVOMock = new ProductVO()
				ProductVOMock.setName("TestProduct")
				ProductVOMock.setPrdRelationRollup(prdRelationRollupMock)
				ProductVOMock.setRollupAttributes(prdRelationUpMock)
				ProductVOListMock.add(ProductVOMock)
				collectionProductVOMock.getChildSKUs() >> ["124545"]
				collectionProductVOMock.isCollection() >> true
				collectionProductVOMock.getChildProducts() >> ProductVOListMock
				productManagerMock.getRollupDetails(_, _, _, _) >> {throw new BBBBusinessException("Mock Business Exception")}
				expect:
				prodDetailTestObj.createChildProductsSkuLevel(collectionProductVOMock, "BedBathUS")
				}
			
			def "product Droplet Performing method explodes Child Products of a collection to SKU level based on the rollup attributes  child product." (){
				given:
				 //mocking methods
				CollectionProductVO collectionProductVO = new CollectionProductVO()
				List<ProductVO> ProductVOListMock = new ArrayList<ProductVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
				RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
				colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
				prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
				Map<String,List<RollupTypeVO>> prdRelationUpMock = ["FINISH":prdRelationRollupTypeVOColor,]
				Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["NONE":prdRelationRollupTypeVOFinish,"FINISH":prdRelationRollupTypeVOColor]
				ProductVO ProductVOMock = new ProductVO()
				ProductVOMock.setName("TestProduct")
				ProductVOMock.setPrdRelationRollup(prdRelationRollupMock)
				ProductVOMock.setRollupAttributes(prdRelationUpMock)
				ProductVOListMock.add(ProductVOMock)
				List listChildSku = new ArrayList<String>()
				listChildSku.add("124545")
				collectionProductVO.setProductId("10124545")
				collectionProductVO.setChildSKUs(listChildSku)
				collectionProductVO.setCollection(true)
				collectionProductVO.setChildProducts(ProductVOListMock)
				productManagerMock.getSKUId("BedBathUS", collectionProductVO.getProductId(), _) >> "124545"
				when:
				collectionProductVO = prodDetailTestObj.createChildProductsSkuLevel(collectionProductVO, "BedBathUS")
				then:
				collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct - RollupAttribute") == true
				
			}
			def "product Droplet Performing method explodes Child Products of a collection to SKU level based on the  rollup attributes  not set child product." (){
				given:
				 //mocking methods
				CollectionProductVO collectionProductVO = new CollectionProductVO()
				List<ProductVO> ProductVOListMock = new ArrayList<ProductVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
				RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
				colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
				prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
				Map<String,List<RollupTypeVO>> prdRelationUpMock = ["FINISH":prdRelationRollupTypeVOColor,]
				Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["Color":prdRelationRollupTypeVOFinish,"FINISH":prdRelationRollupTypeVOColor]
				ProductVO ProductVOMock = new ProductVO()
				ProductVOMock.setName("TestProduct")
				ProductVOMock.setPrdRelationRollup(prdRelationRollupMock)
				ProductVOMock.setRollupAttributes(prdRelationUpMock)
				ProductVOListMock.add(ProductVOMock)
				List listChildSku = new ArrayList<String>()
				listChildSku.add("124545")
				collectionProductVO.setProductId("10124545")
				collectionProductVO.setChildSKUs(listChildSku)
				collectionProductVO.setCollection(true)
				collectionProductVO.setChildProducts(ProductVOListMock)
				productManagerMock.getSKUId("BedBathUS", collectionProductVO.getProductId(), _) >> "124545"
				when:
				collectionProductVO = prodDetailTestObj.createChildProductsSkuLevel(collectionProductVO, "BedBathUS")
				then:
				collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct") == true
				
			}
			def "product Droplet Performing method explodes Child Products of a collection to SKU level based on the  relation ship attributes  not set child product." (){
				given:
				 //mocking methods
				CollectionProductVO collectionProductVO = new CollectionProductVO()
				List<ProductVO> ProductVOListMock = new ArrayList<ProductVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
				RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
				colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
				prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
				Map<String,List<RollupTypeVO>> prdRelationUpMock = ["FINISH":prdRelationRollupTypeVOColor,"NONE":prdRelationRollupTypeVOColor]
				Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["NONE":prdRelationRollupTypeVOFinish,"FINISH":prdRelationRollupTypeVOColor]
				ProductVO ProductVOMock = new ProductVO()
				ProductVOMock.setName("TestProduct")
				ProductVOMock.setPrdRelationRollup(prdRelationRollupMock)
				ProductVOMock.setRollupAttributes(prdRelationUpMock)
				ProductVOListMock.add(ProductVOMock)
				List listChildSku = new ArrayList<String>()
				listChildSku.add("124545")
				collectionProductVO.setProductId("10124545")
				collectionProductVO.setChildSKUs(listChildSku)
				collectionProductVO.setCollection(true)
				collectionProductVO.setChildProducts(ProductVOListMock)
				productManagerMock.getSKUId("BedBathUS", collectionProductVO.getProductId(), _) >> "124545"
				when:
				collectionProductVO = prodDetailTestObj.createChildProductsSkuLevel(collectionProductVO, "BedBathUS")
				then:
				collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct") == true
				
			}
			def "product Droplet Performing method explodes Child Products of a collection to SKU level based on the  rollup attributes and RelationRollup not set ." (){
				given:
				 //mocking methods
				CollectionProductVO collectionProductVO = new CollectionProductVO()
				List<ProductVO> ProductVOListMock = new ArrayList<ProductVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
				RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
				colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
				prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
				Map<String,List<RollupTypeVO>> prdRelationUpMock = ["FINISH":prdRelationRollupTypeVOColor,]
				ProductVO ProductVOMock = new ProductVO()
				ProductVOMock.setName("TestProduct")
				ProductVOMock.setPrdRelationRollup(null)
				ProductVOMock.setRollupAttributes(prdRelationUpMock)
				ProductVOListMock.add(ProductVOMock)
				List listChildSku = new ArrayList<String>()
				listChildSku.add("124545")
				collectionProductVO.setProductId("10124545")
				collectionProductVO.setChildSKUs(listChildSku)
				collectionProductVO.setCollection(true)
				collectionProductVO.setChildProducts(ProductVOListMock)
				productManagerMock.getSKUId("BedBathUS", collectionProductVO.getProductId(), _) >> "124545"
				when:
				collectionProductVO = prodDetailTestObj.createChildProductsSkuLevel(collectionProductVO, "BedBathUS")
				then:
				collectionProductVO.getChildProducts().size() == 1 && collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct") == true
				
			}
			def "product Droplet Performing method explodes Child Products of a collection to SKU level based on the  RollupAttributes  not set child product." (){
				given:
				 //mocking methods
				CollectionProductVO collectionProductVO = new CollectionProductVO()
				List<ProductVO> ProductVOListMock = new ArrayList<ProductVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
				RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
				colorRollupTypeVOMock.setRollupAttribute("RollupAttribute")
				prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
				Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["Color":prdRelationRollupTypeVOFinish,"FINISH":prdRelationRollupTypeVOColor]
				ProductVO ProductVOMock = new ProductVO()
				ProductVOMock.setName("TestProduct")
				ProductVOMock.setPrdRelationRollup(prdRelationRollupMock)
				ProductVOMock.setRollupAttributes(null)
				ProductVOListMock.add(ProductVOMock)
				List listChildSku = new ArrayList<String>()
				listChildSku.add("124545")
				collectionProductVO.setProductId("10124545")
				collectionProductVO.setChildSKUs(listChildSku)
				collectionProductVO.setCollection(true)
				collectionProductVO.setChildProducts(ProductVOListMock)
				productManagerMock.getSKUId("BedBathUS", collectionProductVO.getProductId(), _) >> "124545"
				when:
				collectionProductVO = prodDetailTestObj.createChildProductsSkuLevel(collectionProductVO, "BedBathUS")
				then:
				collectionProductVO.getChildProducts().size() == 1 &&	collectionProductVO.getChildProducts().get(0).getName().equals("TestProduct") == true
			}
			def "product Droplet Performing method explodes Child Products of a collection to SKU level based on the rollup attributes  child product thows Business exception." (){
				given:
				 //mocking methods
				
				CollectionProductVO collectionProductVO = new CollectionProductVO()
				List<ProductVO> productVOListMock = new ArrayList<ProductVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
				RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
				prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
				Map<String,List<RollupTypeVO>> prdRelationUpMock = ["FINISH":prdRelationRollupTypeVOColor,]
				Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["NONE":prdRelationRollupTypeVOFinish,"FINISH":prdRelationRollupTypeVOColor]
				ProductVO productMock = new ProductVO()
				productMock.setName("TestProduct")
				productMock.setPrdRelationRollup(prdRelationRollupMock)
				productMock.setRollupAttributes(prdRelationUpMock)
				productVOListMock.add(productMock)
				List listChildSku = new ArrayList<String>()
				listChildSku.add("124545")
				collectionProductVO.setChildSKUs(listChildSku)
				collectionProductVO.setCollection(true)
				collectionProductVO.setChildProducts(productVOListMock)
				productManagerMock.getSKUId(_,_,_) >> {throw new BBBBusinessException("Mock Business Exception")}
				expect:
				prodDetailTestObj.createChildProductsSkuLevel(collectionProductVO, "BedBathUS")
				}
			def "product Droplet Performing method explodes Child Products of a collection to SKU level based on the rollup attributes  child product thows system exception." (){
				given:
				 //mocking methods
				CollectionProductVO collectionProductVO = new CollectionProductVO()
				List<ProductVO> productVOListMock = new ArrayList<ProductVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinish=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOFinsh=new ArrayList<RollupTypeVO>()
				List<RollupTypeVO> prdRelationRollupTypeVOColor=new ArrayList<RollupTypeVO>()
				RollupTypeVO colorRollupTypeVOMock = new RollupTypeVO()
				prdRelationRollupTypeVOColor.add(colorRollupTypeVOMock)
				Map<String,List<RollupTypeVO>> prdRelationUpMock = ["FINISH":prdRelationRollupTypeVOColor,]
				Map<String,List<RollupTypeVO>> prdRelationRollupMock = ["NONE":prdRelationRollupTypeVOFinish,"FINISH":prdRelationRollupTypeVOColor]
				ProductVO productMock = new ProductVO()
				productMock.setName("TestProduct")
				productMock.setPrdRelationRollup(prdRelationRollupMock)
				productMock.setRollupAttributes(prdRelationUpMock)
				productVOListMock.add(productMock)
				List listChildSku = new ArrayList<String>()
				listChildSku.add("124545")
				collectionProductVO.setChildSKUs(listChildSku)
				collectionProductVO.setCollection(true)
				collectionProductVO.setChildProducts(productVOListMock)
				productManagerMock.getSKUId(_,_,_) >> {throw new BBBSystemException("Mock system Exception")}
				expect:
				prodDetailTestObj.createChildProductsSkuLevel(collectionProductVO, "BedBathUS")
				}
			def "product Droplet Performing  method will replace size list it rollup attribute map and large/thumnail image to product vo  if sku is leading sku."(){
				given:
				ProductVO productVO = new ProductVO()
				productVO.setProductId("4567127")
				List<RollupTypeVO> rollUPList = new ArrayList<RollupTypeVO>()
				RollupTypeVO rollupTypeVOMock = new RollupTypeVO()
				rollupTypeVOMock.setFirstRollUp("blue")
				rollupTypeVOMock.setLargeImagePath("LARGEImagePath")
				rollUPList.add(rollupTypeVOMock)
				List<RollupTypeVO> rollSizeUPList = new ArrayList<RollupTypeVO>()
				Map<String , List<RollupTypeVO>> rollUpAttributesMap = ["SIZE":rollSizeUPList]
				ImageVO imageVOMock = new ImageVO()
				productVO.setProductImages(imageVOMock)
				productVO.setRollupAttributes(rollUpAttributesMap)
				productManagerMock.getRollupDetails(_,_, _, _) >> rollUPList
				when:
				prodDetailTestObj.getColorSwatchDetail(productVO, "blue", true)
				then:
				productVO.getProductImages().getThumbnailImage().equalsIgnoreCase("LARGEImagePath?\$229\$")  == true
			}
			def "product Droplet Performing  method will replace size list it rollup attribute map differnt color and large/thumnail image to product vo  if sku is leading sku."(){
				given:
				ProductVO productVO = new ProductVO()
				productVO.setProductId("4567127")
				List<RollupTypeVO> rollUPList = new ArrayList<RollupTypeVO>()
				RollupTypeVO rollupTypeVOMock = new RollupTypeVO()
				rollupTypeVOMock.setFirstRollUp("green")
				rollupTypeVOMock.setLargeImagePath("LARGEImagePath")
				rollUPList.add(rollupTypeVOMock)
				List<RollupTypeVO> rollSizeUPList = new ArrayList<RollupTypeVO>()
				Map<String , List<RollupTypeVO>> rollUpAttributesMap = ["SIZE":rollSizeUPList]
				ImageVO imageVOMock = new ImageVO()
				productVO.setProductImages(imageVOMock)
				productVO.setRollupAttributes(rollUpAttributesMap)
				productManagerMock.getRollupDetails(_,_, _, _) >> rollUPList
				when:
				prodDetailTestObj.getColorSwatchDetail(productVO, "blue", true)
				then:
				productVO.getProductImages().getThumbnailImage() == null
			}
			def "product Droplet Performing  method will replace size list it rollup attribute map and large/thumnail image to product vo  if sku is not leading sku."(){
				given:
				ProductVO productVO = new ProductVO()
				productVO.setProductId("4567127")
				List<RollupTypeVO> rollUPList = new ArrayList<RollupTypeVO>()
				RollupTypeVO rollupTypeVOMock = new RollupTypeVO()
				rollupTypeVOMock.setFirstRollUp("blue")
				rollupTypeVOMock.setLargeImagePath("LARGEImagePath")
				rollUPList.add(rollupTypeVOMock)
				List<RollupTypeVO> rollSizeUPList = new ArrayList<RollupTypeVO>()
				Map<String , List<RollupTypeVO>> rollUpAttributesMap = ["SIZE":rollSizeUPList]
				ImageVO imageVOMock = new ImageVO()
				productVO.setProductImages(imageVOMock)
				productVO.setRollupAttributes(rollUpAttributesMap)
				productManagerMock.getRollupDetails("4567127","blue", "color","size") >> null
				productManagerMock.getRollupDetails("4567127","blue", "finish","size") >> rollUPList
				when:
				prodDetailTestObj.getColorSwatchDetail(productVO, "blue", false)
				then:
				productVO.getProductImages().getThumbnailImage().equalsIgnoreCase("LARGEImagePath?\$229\$")  &&	productVO.getProductImages().getLargeImage().equalsIgnoreCase("LARGEImagePath")  == true
			}
			
			def "product Droplet Performing  method will replace size list it rollup attribute map differnt color and large/thumnail image to product vo  if sku is not leading sku."(){
				given:
				ProductVO productVO = new ProductVO()
				productVO.setProductId("4567127")
				List<RollupTypeVO> rollUPList = new ArrayList<RollupTypeVO>()
				RollupTypeVO rollupTypeVOMock = new RollupTypeVO()
				rollupTypeVOMock.setFirstRollUp("green")
				rollupTypeVOMock.setLargeImagePath("LARGEImagePath")
				rollUPList.add(rollupTypeVOMock)
				List<RollupTypeVO> rollSizeUPList = new ArrayList<RollupTypeVO>()
				Map<String , List<RollupTypeVO>> rollUpAttributesMap = ["SIZE":rollSizeUPList]
				ImageVO imageVOMock = new ImageVO()
				productVO.setProductImages(imageVOMock)
				productVO.setRollupAttributes(rollUpAttributesMap)
				productManagerMock.getRollupDetails(_,_, _, _) >> rollUPList
				when:
				prodDetailTestObj.getColorSwatchDetail(productVO, "blue", false)
				then:
				productVO.getProductImages().getThumbnailImage() == null
			}
			def "product detail Drplet will perform the setting map value to product rollupattributes"(){
				given:
				ProductVO collectionChildProduct = new ProductVO()
				ProductVO productVO = new ProductVO()
				productVO.setChildSKUs(["12345"])
				productVO.setName("Name")
				Map<String,List<RollupTypeVO>> rollup = ["Map":null]
				when:				
				prodDetailTestObj.setChildProductsAttributes(collectionChildProduct,productVO,rollup,null)
				then:
				collectionChildProduct.getChildSKUs().size() == 1 &&  collectionChildProduct.getName().equals("Name")
			}
}