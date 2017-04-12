package com.bbb.commerce.browse.droplet

import java.util.List;
import java.util.Map

import org.bouncycastle.crypto.tls.PSKTlsClient;

import com.bbb.browse.webservice.manager.BBBEximManager
import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.browse.manager.ProductManager
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.ProductVO
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.exim.bean.EximSessionBean;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.BBBStoreInventoryManager
import com.bbb.commerce.order.BBBShippingGroupManager
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSavedItemsSessionBean
import com.bbb.profile.session.BBBSessionBean
import com.bbb.rest.catalog.vo.KatoriPriceRestVO;
import com.bbb.selfservice.manager.SearchStoreManager
import com.bbb.wishlist.GiftListVO;

import atg.commerce.gifts.GiftlistManager;
import atg.commerce.inventory.InventoryException
import atg.repository.RepositoryItem
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import spock.lang.specification.BBBExtendedSpec;

class SKURollUpListDropletSpecification extends BBBExtendedSpec {

	def ProductManager productManager = Mock()
	def BBBInventoryManager inventoryManager = Mock()
	def BBBStoreInventoryManager storeInventoryManager = Mock()
	def SearchStoreManager searchStoreManager = Mock()
	def GiftlistManager giftlistManager = Mock()
	def RepositoryItem mProfile = Mock()
	def BBBShippingGroupManager mShippingGroupManager = Mock()
	def BBBCatalogTools mCatalogTools = Mock()
	def BBBEximManager eximManager = Mock()
	
	SKURollUpListDroplet droplet = new SKURollUpListDroplet()
	
	def setup(){
		droplet.setProductManager(productManager)
		droplet.setInventoryManager(inventoryManager)
		droplet.setStoreInventoryManager(storeInventoryManager)
		droplet.setSearchStoreManager(searchStoreManager)
		droplet.setGiftlistManager(giftlistManager)
		droplet.setProfile(mProfile)
		droplet.setShippingGroupManager(mShippingGroupManager)
		droplet.setCatalogTools(mCatalogTools)
		droplet.setEximManager(eximManager)
	}
	
	def "service method, Happy Path"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			def Profile profile = Mock()
			def SKUDetailVO skuDetailVO = Mock()
			def KatoriPriceRestVO price = Mock()
			def RepositoryItem rItem1 = Mock()
			def RepositoryItem rItem2 = Mock()
			def RepositoryItem wishList = Mock()
			EximSessionBean bean = new EximSessionBean()
			Map<String, EximSessionBean> getPersonalizedSkus = new HashMap<String,EximSessionBean>()
			List items = new ArrayList()
			
			
			getPersonalizedSkus.put("skuId", bean)
			items.add(rItem1)
			items.add(rItem2)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("prodColor") >> "color"
			requestMock.getParameter("prodSize") >> "size"
			requestMock.getParameter("prodFinish") >> "finish"
			requestMock.getParameter("sopt") >> "sopt"
			requestMock.getParameter("registryId") >> "registryId"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			droplet.currentSiteId() >> "siteId"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			1*productManager.getProductStatus(_,_) >> true
			1*productManager.getEverLivingSKUId(_,_,_) >> "skuId"
			1*productManager.getEverLivingSKUDetails(_,_,_) >> skuDetailVO
			1*inventoryManager.getEverLivingProductAvailability(_,_,_) >> 1
			skuDetailVO.setLtlItem(true)
			skuDetailVO.setInCartFlag(true)
			profile.isTransient() >> false
			sessionBean.isInternationalShippingContext() >> false
			droplet.resolveSessionBean(_) >> sessionBean
			sessionBean.getPersonalizedSkus() >> getPersonalizedSkus
			1*eximManager.getPriceByRefKatori(_, _, _, _, _, _, _, _) >> price
			1*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "perSku"
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			6*requestMock.setParameter(_,_)
			1*requestMock.serviceParameter(_, _, _)
	}
	
	def "service method, prodColor is empty"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			def Profile profile = Mock()
			def SKUDetailVO skuDetailVO = Mock()
			def KatoriPriceRestVO price = Mock()
			def RepositoryItem rItem1 = Mock()
			def RepositoryItem rItem2 = Mock()
			def RepositoryItem wishList = Mock()
			EximSessionBean bean = new EximSessionBean()
			Map<String, EximSessionBean> getPersonalizedSkus = new HashMap<String,EximSessionBean>()
			List items = new ArrayList()
			
			items.add(rItem1)
			items.add(rItem2)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("prodColor") >> ""
			requestMock.getParameter("prodSize") >> "size"
			requestMock.getParameter("prodFinish") >> "finish"
			requestMock.getParameter("sopt") >> "sopt"
			requestMock.getParameter("registryId") >> ""
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			1*productManager.getProductStatus(_,_) >> true
			1*productManager.getEverLivingSKUId(_,_,_) >> "skuId"
			1*productManager.getEverLivingSKUDetails(_,_,_) >> skuDetailVO
			1*inventoryManager.getEverLivingProductAvailability(_,_,_) >> 0
			skuDetailVO.isLtlItem() >> true
			skuDetailVO.setInCartFlag(true)
			profile.isTransient() >> false
			sessionBean.isInternationalShippingContext() >> false
			droplet.resolveSessionBean(_) >> sessionBean
			sessionBean.getPersonalizedSkus() >> getPersonalizedSkus
			0*eximManager.getPriceByRefKatori(_, _, _, _, _, _, _, _) >> price
			1*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "perSku"
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			6*requestMock.setParameter(_,_)
			1*requestMock.serviceParameter(_, _, _)
	}
	
	def "service method, prodColor is null and prodFinish is not null"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			def Profile profile = Mock()
			def KatoriPriceRestVO price = Mock()
			def SKUDetailVO skuDetailVO = Mock()
			def RepositoryItem rItem1 = Mock()
			def RepositoryItem rItem2 = Mock()
			def RepositoryItem wishList = Mock()
			def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
			GiftListVO giftListVO1 = new GiftListVO()
			ShipMethodVO shipMethodVo1 = new ShipMethodVO()
			ShipMethodVO shipMethodVo2 = new ShipMethodVO()
			EximSessionBean bean = new EximSessionBean()
			Map<String, EximSessionBean> getPersonalizedSkus = new HashMap<String,EximSessionBean>()
			List items = new ArrayList()
			
			items.add(rItem1)
			items.add(rItem2)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("prodColor") >> "null"
			requestMock.getParameter("prodSize") >> "size"
			requestMock.getParameter("prodFinish") >> "finish"
			requestMock.getParameter("sopt") >> "sopt"
			requestMock.getParameter("registryId") >> "null"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			1*productManager.getProductStatus(_,_) >> true
			1*productManager.getEverLivingSKUId(_,_,_) >> "skuId"
			1*productManager.getEverLivingSKUDetails(_,_,_) >> skuDetailVO
			1*inventoryManager.getEverLivingProductAvailability(_,_,_) >> 0
			skuDetailVO.isLtlItem() >> true
			profile.isTransient() >> true
			sessionBean.isInternationalShippingContext() >> false
			droplet.resolveSessionBean(_) >> sessionBean
			sessionBean.getPersonalizedSkus() >> getPersonalizedSkus
			0*eximManager.getPriceByRefKatori(_, _, _, _, _, _, _, _) >> price
			0*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "perSku"
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			ServletUtil.getCurrentRequest() >> requestMock
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> [giftListVO1]
			giftListVO1.setSkuID("skuId")
			giftListVO1.setReferenceNumber("refNum")
			giftListVO1.setLtlShipMethod("pSopt")
			skuDetailVO.getEligibleShipMethods() >> [shipMethodVo1,shipMethodVo2]
			shipMethodVo1.setShipMethodId("pSopt")
			shipMethodVo2.setShipMethodId("pSopt1")
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			6*requestMock.setParameter(_,_)
			1*requestMock.serviceParameter(_, _, _)
	}
	
	def "service method, prodSize is empty and skuId is null"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			def Profile profile = Mock()

			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("prodColor") >> "color"
			requestMock.getParameter("prodSize") >> ""
			requestMock.getParameter("prodFinish") >> "finish"
			requestMock.getParameter("sopt") >> "sopt"
			requestMock.getParameter("registryId") >> "null"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			1*productManager.getProductStatus(_,_) >> true
			1*productManager.getEverLivingSKUId(_,_,_) >> null
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			2*requestMock.setParameter(_,_)
			1*requestMock.serviceParameter(_, _, _)
	}
	
	def "service method, everliving flag is false"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			def Profile profile = Mock()
			def SKUDetailVO skuDetailVO = Mock()
			Map<String,String> map = new HashMap<String,String>()
			ProductVO productVO = Mock()
			def KatoriPriceRestVO price = Mock()
			def RepositoryItem rItem1 = Mock()
			def RepositoryItem rItem2 = Mock()
			def RepositoryItem wishList = Mock()
			def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
			GiftListVO giftListVO1 = new GiftListVO()
			ShipMethodVO shipMethodVo1 = new ShipMethodVO()
			ShipMethodVO shipMethodVo2 = new ShipMethodVO()
			EximSessionBean bean = new EximSessionBean()
			Map<String, EximSessionBean> getPersonalizedSkus = new HashMap<String,EximSessionBean>()
			List items = new ArrayList()
			
			items.add(rItem1)
			items.add(rItem2)
			getPersonalizedSkus.put("skuId", bean)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("prodColor") >> "color"
			requestMock.getParameter("prodSize") >> ""
			requestMock.getParameter("prodFinish") >> "finish"
			requestMock.getParameter("sopt") >> "sopt"
			requestMock.getParameter("registryId") >> "null"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			1*productManager.getProductStatus(_,_) >> false
			1*productManager.getSKUId(_,_,_) >> "skuId"
			1*productManager.getSKUDetails(_,_,_) >> skuDetailVO
			skuDetailVO.isVdcSku() >> true
			skuDetailVO.isLtlItem() >> false
			skuDetailVO.isBopusAllowed() >> false
			skuDetailVO.isCustomizableRequired() >> false
			map.put(BBBCoreConstants.VDC_OFFSET_FLAG, "true")
			1*mCatalogTools.getConfigValueByconfigType(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS) >> map
			1*mCatalogTools.getActualOffsetDate(_, _) >> "offSetDate"
			1*productManager.getProductDetails(_, _) >> productVO
			profile.isTransient() >> true
			
			sessionBean.isInternationalShippingContext() >> false
			0*searchStoreManager.fetchFavoriteStoreId(_,_) >> "favStoreId"
			0*droplet.resolveSessionBean(_) >> sessionBean
			sessionBean.getPersonalizedSkus() >> getPersonalizedSkus
			1*eximManager.getPriceByRefKatori(_, _, _, _, _, _, _, _) >> price
			0*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "perSku"
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			
			ServletUtil.getCurrentRequest() >> requestMock
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> [giftListVO1]
			giftListVO1.setSkuID("skuId")
			giftListVO1.setLtlShipMethod("pSopt")
			skuDetailVO.getPersonalizationType() >> "type"
			skuDetailVO.getEligibleShipMethods() >> [shipMethodVo1,shipMethodVo2]
			shipMethodVo1.setShipMethodId("pSopt")
			shipMethodVo2.setShipMethodId("pSopt1")
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			6*requestMock.setParameter(_,_)
			1*requestMock.serviceParameter(_, _, _)
	}
	
	def "service method, prodSize is null"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			def Profile profile = Mock()
			def SKUDetailVO skuDetailVO = Mock()
			Map<String,String> map = new HashMap<String,String>()
			ProductVO productVO = Mock()
			def KatoriPriceRestVO price = Mock()
			def RepositoryItem rItem1 = Mock()
			def RepositoryItem rItem2 = Mock()
			def RepositoryItem wishList = Mock()
			def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
			GiftListVO giftListVO1 = new GiftListVO()
			ShipMethodVO shipMethodVo1 = new ShipMethodVO()
			ShipMethodVO shipMethodVo2 = new ShipMethodVO()
			EximSessionBean bean = new EximSessionBean()
			Map<String, EximSessionBean> getPersonalizedSkus = new HashMap<String,EximSessionBean>()
			List items = new ArrayList()
			
			items.add(rItem1)
			items.add(rItem2)
			getPersonalizedSkus.put("skuId1", bean)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("prodColor") >> "color"
			requestMock.getParameter("prodSize") >> "null"
			requestMock.getParameter("prodFinish") >> "finish"
			requestMock.getParameter("sopt") >> "sopt"
			requestMock.getParameter("registryId") >> "regId"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			1*productManager.getProductStatus(_,_) >> false
			1*productManager.getSKUId(_,_,_) >> "skuId"
			1*productManager.getSKUDetails(_,_,_) >> skuDetailVO
			skuDetailVO.isVdcSku() >> true
			skuDetailVO.isLtlItem() >> false
			skuDetailVO.isBopusAllowed() >> false
			skuDetailVO.isCustomizableRequired() >> false
			skuDetailVO.getPersonalizationType() >> "type"
			map.put(BBBCoreConstants.VDC_OFFSET_FLAG, "true")
			1*mCatalogTools.getConfigValueByconfigType(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS) >> map
			1*mCatalogTools.getActualOffsetDate(_, _) >> ""
			1*inventoryManager.getProductAvailability(_, _, _, _) >> 1
			1*productManager.getProductDetails(_, _) >> productVO
			profile.isTransient() >> true
			
			sessionBean.isInternationalShippingContext() >> false
			0*searchStoreManager.fetchFavoriteStoreId(_,_) >> "favStoreId"
			droplet.resolveSessionBean(_) >> sessionBean
			sessionBean.getPersonalizedSkus() >> getPersonalizedSkus
			0*eximManager.getPriceByRefKatori(_, _, _, _, _, _, _, _) >> price
			0*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "perSku"
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			
			ServletUtil.getCurrentRequest() >> requestMock
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> [giftListVO1]
			giftListVO1.setSkuID("skuId")
			//giftListVO1.setReferenceNumber("refNum")
			giftListVO1.setLtlShipMethod("pSopt")
			skuDetailVO.getPersonalizationType() >> "type"
			skuDetailVO.getEligibleShipMethods() >> [shipMethodVo1,shipMethodVo2]
			shipMethodVo1.setShipMethodId("pSopt")
			shipMethodVo2.setShipMethodId("pSopt1")
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			5*requestMock.setParameter(_,_)
			1*requestMock.serviceParameter(_, _, _)
	}
	
	def "service method, prodFinish is null"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			def Profile profile = Mock()
			def SKUDetailVO skuDetailVO = Mock()
			Map<String,String> map = new HashMap<String,String>()
			ProductVO productVO = Mock()
			def KatoriPriceRestVO price = Mock()
			def RepositoryItem rItem1 = Mock()
			def RepositoryItem rItem2 = Mock()
			def RepositoryItem wishList = Mock()
			def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
			GiftListVO giftListVO1 = new GiftListVO()
			GiftListVO giftListVO2 = new GiftListVO()
			ShipMethodVO shipMethodVo1 = new ShipMethodVO()
			ShipMethodVO shipMethodVo2 = new ShipMethodVO()
			EximSessionBean bean = new EximSessionBean()
			Map<String, EximSessionBean> getPersonalizedSkus = new HashMap<String,EximSessionBean>()
			List items = new ArrayList()
			
			items.add(rItem1)
			items.add(rItem2)
			getPersonalizedSkus.put("skuId1", bean)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("prodColor") >> "color"
			requestMock.getParameter("prodSize") >> "null"
			requestMock.getParameter("prodFinish") >> "null"
			requestMock.getParameter("sopt") >> "sopt"
			requestMock.getParameter("registryId") >> ""
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			1*productManager.getProductStatus(_,_) >> false
			1*productManager.getSKUId(_,_,_) >> "skuId"
			1*productManager.getSKUDetails(_,_,_) >> skuDetailVO
			skuDetailVO.isVdcSku() >> true
			skuDetailVO.isLtlItem() >> false
			skuDetailVO.isBopusAllowed() >> false
			skuDetailVO.isCustomizableRequired() >> false
			skuDetailVO.getPersonalizationType() >> "type"
			map.put(BBBCoreConstants.VDC_OFFSET_FLAG, "false")
			1*mCatalogTools.getConfigValueByconfigType(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS) >> map
			0*mCatalogTools.getActualOffsetDate(_, _) >> ""
			1*inventoryManager.getProductAvailability(_, _, _, _) >> 1
			1*productManager.getProductDetails(_, _) >> productVO
			profile.isTransient() >> true
			
			sessionBean.isInternationalShippingContext() >> false
			0*searchStoreManager.fetchFavoriteStoreId(_,_) >> "favStoreId"
			0*droplet.resolveSessionBean(_) >> sessionBean
			sessionBean.getPersonalizedSkus() >> getPersonalizedSkus
			0*eximManager.getPriceByRefKatori(_, _, _, _, _, _, _, _) >> price
			0*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "perSku"
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			
			ServletUtil.getCurrentRequest() >> requestMock
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> [giftListVO1,giftListVO2]
			giftListVO1.setSkuID("skuId")
			giftListVO2.setSkuID("skuId1")
			giftListVO1.setReferenceNumber("refNum")
			giftListVO1.setLtlShipMethod("pSopt")
			skuDetailVO.getPersonalizationType() >> "type"
			skuDetailVO.getEligibleShipMethods() >> [shipMethodVo1,shipMethodVo2]
			shipMethodVo1.setShipMethodId("pSopt")
			shipMethodVo2.setShipMethodId("pSopt1")
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			5*requestMock.setParameter(_,_)
			1*requestMock.serviceParameter(_, _, _)
	}
	
	def "service method, reference Number is not null"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			def Profile profile = Mock()
			def SKUDetailVO skuDetailVO = Mock()
			Map<String,String> map = new HashMap<String,String>()
			ProductVO productVO = Mock()
			def KatoriPriceRestVO price = Mock()
			def RepositoryItem rItem1 = Mock()
			def RepositoryItem rItem2 = Mock()
			def RepositoryItem wishList = Mock()
			def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
			GiftListVO giftListVO1 = new GiftListVO()
			ShipMethodVO shipMethodVo1 = new ShipMethodVO()
			ShipMethodVO shipMethodVo2 = new ShipMethodVO()
			EximSessionBean bean = new EximSessionBean()
			Map<String, EximSessionBean> getPersonalizedSkus = new HashMap<String,EximSessionBean>()
			List items = new ArrayList()
			
			items.add(rItem1)
			items.add(rItem2)
			getPersonalizedSkus.put("skuId", bean)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("prodColor") >> "null"
			requestMock.getParameter("prodSize") >> "null"
			requestMock.getParameter("prodFinish") >> "finish"
			requestMock.getParameter("sopt") >> "sopt"
			requestMock.getParameter("registryId") >> "regId"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			1*productManager.getProductStatus(_,_) >> false
			1*productManager.getSKUId(_,_,_) >> "skuId"
			1*productManager.getSKUDetails(_,_,_) >> skuDetailVO
			skuDetailVO.isVdcSku() >> true
			skuDetailVO.isLtlItem() >> false
			skuDetailVO.isBopusAllowed() >> false
			skuDetailVO.isCustomizableRequired() >> false
			skuDetailVO.getPersonalizationType() >> "type"
			map.put(BBBCoreConstants.VDC_OFFSET_FLAG, "")
			1*mCatalogTools.getConfigValueByconfigType(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS) >> map
			0*mCatalogTools.getActualOffsetDate(_, _) >> ""
			1*inventoryManager.getProductAvailability(_, _, _, _) >> 1
			1*productManager.getProductDetails(_, _) >> productVO
			profile.isTransient() >> true
			
			sessionBean.isInternationalShippingContext() >> false
			0*searchStoreManager.fetchFavoriteStoreId(_,_) >> "favStoreId"
			0*droplet.resolveSessionBean(_) >> sessionBean
			sessionBean.getPersonalizedSkus() >> getPersonalizedSkus
			1*eximManager.getPriceByRefKatori(_, _, _, _, _, _, _, _) >> price
			0*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "perSku"
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			
			ServletUtil.getCurrentRequest() >> requestMock
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> [giftListVO1]
			giftListVO1.setSkuID("skuId")
			giftListVO1.setReferenceNumber("refNum")
			giftListVO1.setLtlShipMethod("sopt")
			skuDetailVO.getPersonalizationType() >> "type"
			skuDetailVO.getEligibleShipMethods() >> [shipMethodVo1,shipMethodVo2]
			shipMethodVo1.setShipMethodId("pSopt")
			shipMethodVo2.setShipMethodId("pSopt1")
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			5*requestMock.setParameter(_,_)
			1*requestMock.serviceParameter(_, _, _)
	}
	
	def "service method, prodColor, prodSize, sopt is empty"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			def Profile profile = Mock()
			def KatoriPriceRestVO price = Mock()
			def SKUDetailVO skuDetailVO = Mock()
			def RepositoryItem rItem1 = Mock()
			def RepositoryItem rItem2 = Mock()
			def RepositoryItem wishList = Mock()
			def ProductVO productVO = Mock()
			def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
			GiftListVO giftListVO1 = new GiftListVO()
			ShipMethodVO shipMethodVo1 = new ShipMethodVO()
			ShipMethodVO shipMethodVo2 = new ShipMethodVO()
			EximSessionBean bean = new EximSessionBean()
			Map<String, EximSessionBean> getPersonalizedSkus = new HashMap<String,EximSessionBean>()
			List items = new ArrayList()
			
			items.add(rItem1)
			items.add(rItem2)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("prodColor") >> ""
			requestMock.getParameter("prodSize") >> ""
			requestMock.getParameter("prodFinish") >> "finish"
			requestMock.getParameter("sopt") >> ""
			requestMock.getParameter("registryId") >> "null"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			1*productManager.getProductStatus(_,_) >> false
			1*productManager.getSKUId(_,_,_) >> "skuId"
			1*productManager.getSKUDetails(_,_,_) >> skuDetailVO
			inventoryManager.getEverLivingProductAvailability(_,_,_) >> 0
			skuDetailVO.isLtlItem() >> true
			skuDetailVO.isVdcSku() >> false
			profile.isTransient() >> true
			sessionBean.isInternationalShippingContext() >> false
			droplet.resolveSessionBean(_) >> sessionBean
			sessionBean.getPersonalizedSkus() >> getPersonalizedSkus
			0*eximManager.getPriceByRefKatori(_, _, _, _, _, _, _, _) >> price
			0*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "perSku"
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			ServletUtil.getCurrentRequest() >> requestMock
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> [giftListVO1]
			giftListVO1.setSkuID("skuId")
			giftListVO1.setReferenceNumber("refNum")
			giftListVO1.setLtlShipMethod("pSopt")
			skuDetailVO.getEligibleShipMethods() >> [shipMethodVo1,shipMethodVo2]
			shipMethodVo1.setShipMethodId("pSopt")
			shipMethodVo2.setShipMethodId("pSopt1")
			1*productManager.getProductDetails(_,_) >> productVO
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			6*requestMock.setParameter(_,_)
			1*requestMock.serviceParameter(_, _, _)
	}
	
	def "service method, skuId is null"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			def Profile profile = Mock()
			def KatoriPriceRestVO price = Mock()
			def SKUDetailVO skuDetailVO = Mock()
			def RepositoryItem rItem1 = Mock()
			def RepositoryItem rItem2 = Mock()
			def RepositoryItem wishList = Mock()
			def ProductVO productVO = Mock()
			def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
			EximSessionBean bean = new EximSessionBean()
			Map<String, EximSessionBean> getPersonalizedSkus = new HashMap<String,EximSessionBean>()
			List items = new ArrayList()
			
			items.add(rItem1)
			items.add(rItem2)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("prodColor") >> ""
			requestMock.getParameter("prodSize") >> ""
			requestMock.getParameter("prodFinish") >> "finish"
			requestMock.getParameter("sopt") >> ""
			requestMock.getParameter("registryId") >> "null"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			1*productManager.getProductStatus(_,_) >> false
			1*productManager.getSKUId(_,_,_) >> null
			0*productManager.getSKUDetails(_,_,_) >> skuDetailVO
			0*inventoryManager.getEverLivingProductAvailability(_,_,_) >> 0
			skuDetailVO.isLtlItem() >> true
			skuDetailVO.isVdcSku() >> false
			profile.isTransient() >> true
			sessionBean.isInternationalShippingContext() >> false
			droplet.resolveSessionBean(_) >> sessionBean
			sessionBean.getPersonalizedSkus() >> getPersonalizedSkus
			0*eximManager.getPriceByRefKatori(_, _, _, _, _, _, _, _) >> price
			0*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "perSku"
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			ServletUtil.getCurrentRequest() >> requestMock
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> null
			1*productManager.getProductDetails(_,_) >> productVO
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			2*requestMock.setParameter(_,_)
			1*requestMock.serviceParameter(_, _, _)
	}
	
	def "service method, BBBSystemException is thrown"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			def Profile profile = Mock()
			def KatoriPriceRestVO price = Mock()
			def SKUDetailVO skuDetailVO = Mock()
			def RepositoryItem rItem1 = Mock()
			def RepositoryItem rItem2 = Mock()
			def RepositoryItem wishList = Mock()
			def ProductVO productVO = Mock()
			def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
			EximSessionBean bean = new EximSessionBean()
			Map<String, EximSessionBean> getPersonalizedSkus = new HashMap<String,EximSessionBean>()
			List items = new ArrayList()
			
			items.add(rItem1)
			items.add(rItem2)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> null
			requestMock.getParameter("prodColor") >> ""
			requestMock.getParameter("prodSize") >> ""
			requestMock.getParameter("prodFinish") >> "finish"
			requestMock.getParameter("sopt") >> ""
			requestMock.getParameter("registryId") >> "null"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			1*productManager.getProductStatus(_,_) >> false
			0*productManager.getSKUId(_,_,_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
			0*productManager.getSKUDetails(_,_,_) >> skuDetailVO
			0*inventoryManager.getEverLivingProductAvailability(_,_,_) >> 0
			skuDetailVO.isLtlItem() >> true
			skuDetailVO.isVdcSku() >> false
			profile.isTransient() >> true
			sessionBean.isInternationalShippingContext() >> false
			droplet.resolveSessionBean(_) >> sessionBean
			sessionBean.getPersonalizedSkus() >> getPersonalizedSkus
			0*eximManager.getPriceByRefKatori(_, _, _, _, _, _, _, _) >> price
			0*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "perSku"
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			ServletUtil.getCurrentRequest() >> requestMock
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> null
			1*productManager.getProductDetails(_,_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*requestMock.serviceParameter(_, _, _)
	}
	
	def "service method, BBBBusinessException is thrown"(){
		
		given:
			def BBBSessionBean sessionBean = Mock()
			def Profile profile = Mock()
			def KatoriPriceRestVO price = Mock()
			def SKUDetailVO skuDetailVO = Mock()
			def RepositoryItem rItem1 = Mock()
			def RepositoryItem rItem2 = Mock()
			def RepositoryItem wishList = Mock()
			def ProductVO productVO = Mock()
			def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
			EximSessionBean bean = new EximSessionBean()
			Map<String, EximSessionBean> getPersonalizedSkus = new HashMap<String,EximSessionBean>()
			List items = new ArrayList()
			
			items.add(rItem1)
			items.add(rItem2)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("prodColor") >> ""
			requestMock.getParameter("prodSize") >> ""
			requestMock.getParameter("prodFinish") >> null
			requestMock.getParameter("sopt") >> ""
			requestMock.getParameter("registryId") >> "null"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			1*productManager.getProductStatus(_,_) >> false
			0*productManager.getSKUId(_,_,_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
			0*productManager.getSKUDetails(_,_,_) >> skuDetailVO
			inventoryManager.getEverLivingProductAvailability(_,_,_) >> 0
			skuDetailVO.isLtlItem() >> true
			skuDetailVO.isVdcSku() >> false
			profile.isTransient() >> true
			sessionBean.isInternationalShippingContext() >> false
			droplet.resolveSessionBean(_) >> sessionBean
			sessionBean.getPersonalizedSkus() >> getPersonalizedSkus
			0*eximManager.getPriceByRefKatori(_, _, _, _, _, _, _, _) >> price
			0*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "perSku"
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			ServletUtil.getCurrentRequest() >> requestMock
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> null
			1*productManager.getProductDetails(_,_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			1*requestMock.serviceParameter(_, _, _)
	}
	
	def "checkSkuBopusEligible method, isInternationalShippingContext is true"(){
		
		given:
			def SKUDetailVO pSKUDetailVO = Mock()
			def BBBSessionBean sessionBean = Mock()
			sessionBean.isInternationalShippingContext() >> true
		
		when:
			boolean flag = droplet.checkSkuBopusEligible(pSKUDetailVO,sessionBean)
		
		then:
			flag == false
		
	}
	
	def "checkSkuBopusEligible method, isInternationalShippingContext is false and isBopusAllowed is true"(){
		
		given:
			def SKUDetailVO pSKUDetailVO = Mock()
			def BBBSessionBean sessionBean = Mock()
			sessionBean.isInternationalShippingContext() >> false
			pSKUDetailVO.isLtlItem() > false
			pSKUDetailVO.isBopusAllowed() >> true
		
		when:
			boolean flag = droplet.checkSkuBopusEligible(pSKUDetailVO,sessionBean)
		
		then:
			flag == false
		
	}
	
	def "checkSkuBopusEligible method, isInternationalShippingContext is false and isBopusAllowed is false and isCustomizableRequired is true"(){
		
		given:
			def SKUDetailVO pSKUDetailVO = Mock()
			def BBBSessionBean sessionBean = Mock()
			sessionBean.isInternationalShippingContext() >> false
			pSKUDetailVO.isLtlItem() > false
			pSKUDetailVO.isBopusAllowed() >> false
			pSKUDetailVO.isCustomizableRequired() >> true
		
		when:
			boolean flag = droplet.checkSkuBopusEligible(pSKUDetailVO,sessionBean)
		
		then:
			flag == false
		
	}
	
	def "service method, skuId is null and prdId is empty and isTransient is false"(){
		
		given:
			droplet = Spy()
			droplet.setProductManager(productManager)
			droplet.setSearchStoreManager(searchStoreManager)
			droplet.setStoreInventoryManager(storeInventoryManager)
			def BBBSessionBean sessionBean = Mock()
			def Profile profile = Mock()
			def KatoriPriceRestVO price = Mock()
			def SKUDetailVO skuDetailVO = Mock()
			def RepositoryItem rItem1 = Mock()
			def RepositoryItem rItem2 = Mock()
			def RepositoryItem wishList = Mock()
			def ProductVO productVO = Mock()
			def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
			EximSessionBean bean = new EximSessionBean()
			Map<String, EximSessionBean> getPersonalizedSkus = new HashMap<String,EximSessionBean>()
			List items = new ArrayList()
			
			items.add(rItem1)
			items.add(rItem2)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> ""
			requestMock.getParameter("prodColor") >> ""
			requestMock.getParameter("prodSize") >> ""
			requestMock.getParameter("prodFinish") >> "finish"
			requestMock.getParameter("sopt") >> ""
			requestMock.getParameter("registryId") >> "null"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			1*productManager.getProductStatus(_,_) >> false
			1*productManager.getSKUId(_,_,_) >> null
			0*productManager.getSKUDetails(_,_,_) >> skuDetailVO
			0*inventoryManager.getEverLivingProductAvailability(_,_,_) >> 0
			skuDetailVO.isLtlItem() >> true
			skuDetailVO.isVdcSku() >> false
			profile.isTransient() >> false
			sessionBean.isInternationalShippingContext() >> false
			droplet.checkSkuBopusEligible(_, _) >> true
			droplet.resolveSessionBean(_) >> sessionBean
			sessionBean.getPersonalizedSkus() >> getPersonalizedSkus
			0*eximManager.getPriceByRefKatori(_, _, _, _, _, _, _, _) >> price
			0*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "perSku"
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			ServletUtil.getCurrentRequest() >> requestMock
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> null
			1*productManager.getProductDetails(_,_) >> productVO
			1*searchStoreManager.fetchFavoriteStoreId(_,_) >> "favStoreId"
			1*storeInventoryManager.getFavStoreInventory(_,_,_,false) >> new HashMap<String,Integer>()
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			3*requestMock.setParameter(_,_)
			1*requestMock.serviceParameter(_, _, _)
	}
	
	def "service method, skuId is null and prdId is empty and isTransient is false and inventoryException is thrown"(){
		
		given:
			droplet = Spy()
			droplet.setProductManager(productManager)
			droplet.setSearchStoreManager(searchStoreManager)
			droplet.setStoreInventoryManager(storeInventoryManager)
			def BBBSessionBean sessionBean = Mock()
			def Profile profile = Mock()
			def KatoriPriceRestVO price = Mock()
			def SKUDetailVO skuDetailVO = Mock()
			def RepositoryItem rItem1 = Mock()
			def RepositoryItem rItem2 = Mock()
			def RepositoryItem wishList = Mock()
			def ProductVO productVO = Mock()
			def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
			EximSessionBean bean = new EximSessionBean()
			Map<String, EximSessionBean> getPersonalizedSkus = new HashMap<String,EximSessionBean>()
			List items = new ArrayList()
			
			items.add(rItem1)
			items.add(rItem2)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> ""
			requestMock.getParameter("prodColor") >> ""
			requestMock.getParameter("prodSize") >> ""
			requestMock.getParameter("prodFinish") >> "finish"
			requestMock.getParameter("sopt") >> ""
			requestMock.getParameter("registryId") >> "null"
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			1*productManager.getProductStatus(_,_) >> false
			1*productManager.getSKUId(_,_,_) >> null
			0*productManager.getSKUDetails(_,_,_) >> skuDetailVO
			0*inventoryManager.getEverLivingProductAvailability(_,_,_) >> 0
			skuDetailVO.isLtlItem() >> true
			skuDetailVO.isVdcSku() >> false
			profile.isTransient() >> false
			sessionBean.isInternationalShippingContext() >> false
			droplet.checkSkuBopusEligible(_, _) >> true
			droplet.resolveSessionBean(_) >> sessionBean
			sessionBean.getPersonalizedSkus() >> getPersonalizedSkus
			0*eximManager.getPriceByRefKatori(_, _, _, _, _, _, _, _) >> price
			0*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "perSku"
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			ServletUtil.getCurrentRequest() >> requestMock
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> null
			1*productManager.getProductDetails(_,_) >> productVO
			1*searchStoreManager.fetchFavoriteStoreId(_,_) >> "favStoreId"
			1*storeInventoryManager.getFavStoreInventory(_,_,_,false) >> {throw new InventoryException()}
		
		when:
			droplet.service(requestMock,responseMock)
		
		then:
			2*requestMock.setParameter(_,_)
			1*requestMock.serviceParameter(_, _, _)
	}
	
	def "isItemInWishlist method, skuId is empty"(){
		
		def SKUDetailVO pSKUDetailVO = Mock()
		def Profile profile = Mock()
		def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
		GiftListVO giftListVO1 = new GiftListVO()
		
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> true
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> [giftListVO1]
			pSKUDetailVO.getEligibleShipMethods() >> null
			
		when:
			boolean flag = droplet.isItemInWishlist("",pSKUDetailVO,requestMock,null,"")
		
		then:
			flag == false
			1*requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_)
		
	}
	
	def "isItemInWishlist method, skuId is not empty"(){
		
		def SKUDetailVO pSKUDetailVO = Mock()
		def Profile profile = Mock()
		def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
		GiftListVO giftListVO1 = new GiftListVO()
		
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> true
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> [giftListVO1]
			giftListVO1.setSkuID("skuID1")
			pSKUDetailVO.getEligibleShipMethods() >> null
			
		when:
			boolean flag = droplet.isItemInWishlist("skuId",pSKUDetailVO,requestMock,null,"")
		
		then:
			flag == false
			1*requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_)
	}
	
	def "isItemInWishlist method, giftListVO is null"(){
		
		def SKUDetailVO pSKUDetailVO = Mock()
		def Profile profile = Mock()
		def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
		
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> true
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> null
			pSKUDetailVO.getEligibleShipMethods() >> null
			
		when:
			boolean flag = droplet.isItemInWishlist("skuId",pSKUDetailVO,requestMock,null,"")
		
		then:
			flag == false
			1*requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_)
	}
	
	def "isItemInWishlist method, isTransient is false and skuId is empty and refNum is null"(){
		
		def SKUDetailVO pSKUDetailVO = Mock()
		def Profile profile = Mock()
		def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
		def RepositoryItem rItem1 = Mock()
		def RepositoryItem wishList = Mock()
		List items = new ArrayList()
		
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> false
			items.add(rItem1)
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			1*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> ""
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> null
			pSKUDetailVO.getPersonalizationType() >> "type"
			
		when:
			boolean flag = droplet.isItemInWishlist("",pSKUDetailVO,requestMock,null,"")
		
		then:
			flag == true
			0*requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_)
	}
	
	def "isItemInWishlist method, isTransient is false and skuId mismatch"(){
		
		def SKUDetailVO pSKUDetailVO = Mock()
		def Profile profile = Mock()
		def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
		def RepositoryItem rItem1 = Mock()
		def RepositoryItem rItem2 = Mock()
		def RepositoryItem wishList = Mock()
		List items = new ArrayList()
		
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> false
			items.add(rItem1)
			items.add(rItem2)
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			1*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> ""
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			pSKUDetailVO.getPersonalizationType() >> "type"
			
		when:
			boolean flag = droplet.isItemInWishlist("",pSKUDetailVO,requestMock,null,"")
		
		then:
			flag == false
			0*requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_)
	}
	
	def "isItemInWishlist method, isTransient is false and skuId is empty and personalizationType is empty"(){
		
		def SKUDetailVO pSKUDetailVO = Mock()
		def Profile profile = Mock()
		def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
		def RepositoryItem rItem1 = Mock()
		def RepositoryItem wishList = Mock()
		List items = new ArrayList()
		
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> false
			items.add(rItem1)
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			1*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> ""
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> null
			pSKUDetailVO.getPersonalizationType() >> ""
			
		when:
			boolean flag = droplet.isItemInWishlist("",pSKUDetailVO,requestMock,null,"")
		
		then:
			flag == true
			0*requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_)
	}
	
	def "isItemInWishlist method, isTransient is false and skuId is empty and personalizationType is empty and isLTL flag is true"(){
		
		def SKUDetailVO pSKUDetailVO = Mock()
		def Profile profile = Mock()
		def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
		def RepositoryItem rItem1 = Mock()
		ShipMethodVO shipMethodVo1 = new ShipMethodVO()
		ShipMethodVO shipMethodVo2 = new ShipMethodVO()
		def RepositoryItem wishList = Mock()
		List items = new ArrayList()
		
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> false
			items.add(rItem1)
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			1*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> ""
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> null
			rItem1.getPropertyValue(BBBCatalogConstants.LTL_SHIP_METHOD) >> "pSopt"
			pSKUDetailVO.getPersonalizationType() >> ""
			pSKUDetailVO.isLtlItem() >> true
			pSKUDetailVO.getEligibleShipMethods() >> [shipMethodVo1,shipMethodVo2]
			shipMethodVo1.setShipMethodId("pSopt")
			shipMethodVo2.setShipMethodId("pSopt1")
			
		when:
			boolean flag = droplet.isItemInWishlist("",pSKUDetailVO,requestMock,null,"")
		
		then:
			flag == false
			1*requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_)
	}
	
	def "isItemInWishlist method, PERSONALIZED_SKU is not null"(){
		
		def SKUDetailVO pSKUDetailVO = Mock()
		def Profile profile = Mock()
		def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
		def RepositoryItem rItem1 = Mock()
		def RepositoryItem rItem2 = Mock()
		def RepositoryItem wishList = Mock()
		List items = new ArrayList()
		
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "pSku"
			profile.isTransient() >> false
			items.add(rItem1)
			items.add(rItem2)
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			1*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> ""
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> null
			rItem2.getPropertyValue(BBBCoreConstants.SKU_ID) >> ""
			rItem2.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> "refNum"
			pSKUDetailVO.getPersonalizationType() >> ""
			pSKUDetailVO.isLtlItem() >> true
			pSKUDetailVO.getEligibleShipMethods() >> []
			
		when:
			boolean flag = droplet.isItemInWishlist("",pSKUDetailVO,requestMock,null,"")
		
		then:
			flag == true
			1*requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_)
	}
	
	def "isItemInWishlist method, skuId mismatch"(){
		
		def SKUDetailVO pSKUDetailVO = Mock()
		def Profile profile = Mock()
		def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
		def RepositoryItem rItem1 = Mock()
		List items = new ArrayList()
		def RepositoryItem wishList = Mock()
		
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "pSku"
			profile.isTransient() >> false
			items.add(rItem1)
			1*giftlistManager.getGiftlistItems(_) >> items
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> "skuId"
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> null
			
		when:
			boolean flag = droplet.isItemInWishlist("",pSKUDetailVO,requestMock,null,"")
		
		then:
			flag == false
			1*requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_)
	}
	
	def "isItemInWishlist method, items is empty"(){
		
		def SKUDetailVO pSKUDetailVO = Mock()
		def Profile profile = Mock()
		def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
		def RepositoryItem wishList = Mock()
		
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "pSku"
			profile.isTransient() >> false
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			1*giftlistManager.getGiftlistItems(_) >> []
			
		when:
			boolean flag = droplet.isItemInWishlist("",pSKUDetailVO,requestMock,null,"")
		
		then:
			flag == false
			1*requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_)
	}
	
	def "isItemInWishlist method, items is null"(){
		
		def SKUDetailVO pSKUDetailVO = Mock()
		def Profile profile = Mock()
		def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
		def RepositoryItem wishList = Mock()
		
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			requestMock.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) >> "pSku"
			profile.isTransient() >> false
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			1*giftlistManager.getGiftlistItems(_) >> null
			
		when:
			boolean flag = droplet.isItemInWishlist("",pSKUDetailVO,requestMock,null,"")
		
		then:
			flag == false
			1*requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_)
	}
	
	def "isItemInWishlist method, skuDetailVO is null"(){
		
		def Profile profile = Mock()
		def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
		def RepositoryItem rItem1 = Mock()
		def RepositoryItem wishList = Mock()
		List items = new ArrayList()
		
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> false
			items.add(rItem1)
			profile.getPropertyValue(BBBCoreConstants.WISH_LIST) >> wishList
			wishList.getRepositoryId() >> "repoId"
			1*giftlistManager.getGiftlistItems(_) >> items
			rItem1.getPropertyValue(BBBCoreConstants.SKU_ID) >> ""
			rItem1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) >> null
			
		when:
			boolean flag = droplet.isItemInWishlist("",null,requestMock,null,"")
		
		then:
			flag == false
			0*requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_)
	}
	
	def "isItemInWishlist method, skuDetailVO is null and isTransient is true"(){
		
		def Profile profile = Mock()
		def BBBSavedItemsSessionBean bbbSavedItemsSessionBean = Mock()
		EximSessionBean bean = new EximSessionBean()
		def RepositoryItem rItem1 = Mock()
		def RepositoryItem wishList = Mock()
		GiftListVO giftListVO1 = new GiftListVO()
		List items = new ArrayList()
		
		given:
			requestMock.resolveName(BBBCoreConstants.ATG_PROFILE) >> profile
			profile.isTransient() >> true
			ServletUtil.getCurrentRequest() >> requestMock
			requestMock.resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean") >> bbbSavedItemsSessionBean
			bbbSavedItemsSessionBean.getSaveItems(true) >> [giftListVO1]
			giftListVO1.setSkuID("skuId")
			giftListVO1.getReferenceNumber() >> null
			
		when:
			boolean flag = droplet.isItemInWishlist("skuId",null,requestMock,bean,"")
		
		then:
			flag == false
			0*requestMock.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,_)
	}
}
