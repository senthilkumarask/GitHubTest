/**
 * 
 */
package com.bbb.commerce.inventory

import java.util.Map;

import com.bbb.commerce.cart.bean.CommerceItemVO
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.inventory.vo.BBBStoreInventoryVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.profile.session.BBBSessionBean
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.manager.SearchStoreManager
import com.sun.java.xml.ns.j2Ee.String;

import atg.commerce.inventory.InventoryException
import atg.commerce.order.OrderHolder;
import atg.multisite.SiteContextManager
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil
import atg.userprofiling.Profile;
import spock.lang.specification.BBBExtendedSpec


class BBBStoreInventoryManagerImplSpecification extends BBBExtendedSpec {
	
	BBBStoreInventoryManagerImpl storeInvMngrImpl
	BBBCatalogTools mCatalogTools = Mock()
	SiteContextManager siteContextManager = Mock()
	BopusInventoryService mBopusService = Mock()
	SearchStoreManager searchStoreManager = Mock()
	BBBCheckoutManager checkoutManager = Mock()
	BBBStoreInventoryManager storeInventoryManager = Mock()
	
	def setup(){
		storeInvMngrImpl = Spy()
		storeInvMngrImpl.setLoggingDebug(true)
		storeInvMngrImpl.setSiteContextManager(siteContextManager)
		storeInvMngrImpl.setCatalogTools(mCatalogTools)
		storeInvMngrImpl.setBopusService(mBopusService)
		storeInvMngrImpl.setSearchStoreManager(searchStoreManager)
		storeInvMngrImpl.setCheckoutManager(checkoutManager)
		storeInvMngrImpl.setStoreInventoryManager(storeInventoryManager)
	}
	
	def "getInventory(), storeId is passed as null"(){
		BBBStoreInventoryVO vo
		given:
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
		
		when:
			vo = storeInvMngrImpl.getInventory("skuId", null , true)
		
		then:
			BBBBusinessException excep = thrown()
			vo == null
			
	}
	
	def "getInventory(), storeId is passed as empty"(){
		BBBStoreInventoryVO vo
		given:
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
		
		when:
			vo = storeInvMngrImpl.getInventory("skuId", "" , true)
		
		then:
			BBBBusinessException excep = thrown()
			vo == null
	}
	
	
	def "getInventory(), Happy path"(){
		BBBStoreInventoryVO vo
		ThresholdVO skuThresholdVO = Mock()
		Map<String,Integer> map = new HashMap<String,Integer>()
		given:
			map.put("storeId", 100)
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			1*mBopusService.getInventoryForBopusItem(_, _ , _) >> map
		
		when:
			vo = storeInvMngrImpl.getInventory("skuId", "storeId" , true)
		
		then:
			vo!=null
			vo.getStoreInventoryStock() == 100
			vo.getThresholdAvailable() == 0
			vo.getThresholdLimited() == 0
			vo.getStoreId() == "storeId"
			vo.getSkuId() == null
	}
	
	def "getInventory(), Bopus service returned null map"(){
		BBBStoreInventoryVO vo
		ThresholdVO skuThresholdVO = Mock()
		given:
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			1*mBopusService.getInventoryForBopusItem(_, _ , _) >> null
		
		when:
			vo = storeInvMngrImpl.getInventory("skuId", "storeId" , true)
		
		then:
			vo!=null
			vo.getStoreInventoryStock() == 0
			vo.getThresholdAvailable() == 0
			vo.getThresholdLimited() == 0
			vo.getStoreId() == "storeId"
			vo.getSkuId() == null
	}
	
	def "getInventory(), catalogTools returned ThresholdVO as null"(){
		BBBStoreInventoryVO vo
		Map<String,Integer> map = new HashMap<String,Integer>()
		given:
			map.put("storeId", 100)
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			1*mCatalogTools.getSkuThreshold(_,_) >> null
			1*mBopusService.getInventoryForBopusItem(_, _ , _) >> map
		
		when:
			vo = storeInvMngrImpl.getInventory("skuId", "storeId" , true)
		
		then:
			vo!=null
			vo.getStoreInventoryStock() == 100
			vo.getThresholdAvailable() == 0
			vo.getThresholdLimited() == 0
			vo.getStoreId() == "storeId"
			vo.getSkuId() == null
	}
	
	def "getInventory(), Bopus service returned empty map"(){
		BBBStoreInventoryVO vo
		ThresholdVO skuThresholdVO = Mock()
		Map<String,Integer> map = new HashMap<String,Integer>()
		given:
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			1*mBopusService.getInventoryForBopusItem(_, _ , _) >> map
		
		when:
			vo = storeInvMngrImpl.getInventory("skuId", "storeId" , true)
		
		then:
			vo!=null
			vo.getStoreInventoryStock() == 0
			vo.getThresholdAvailable() == 0
			vo.getThresholdLimited() == 0
			vo.getStoreId() == "storeId"
			vo.getSkuId() == null
	}
	
	def "getInventory(), Bopus service returned map with null value for key"(){
		BBBStoreInventoryVO vo
		ThresholdVO skuThresholdVO = Mock()
		Map<String,Integer> map = new HashMap<String,Integer>()
		given:
			map.put("storeId", null)
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			1*mBopusService.getInventoryForBopusItem(_, _ , _) >> map
		
		when:
			vo = storeInvMngrImpl.getInventory("skuId", "storeId" , true)
		
		then:
			vo!=null
			vo.getStoreInventoryStock() == 0
			vo.getThresholdAvailable() == 0
			vo.getThresholdLimited() == 0
			vo.getStoreId() == "storeId"
			vo.getSkuId() == null
	}
	
	def "getInventory(), catalogTools thrown BBBSystemException"(){
		BBBStoreInventoryVO vo
		given:
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			1*mCatalogTools.getSkuThreshold(_,_) >> {throw new BBBSystemException("Mock of system exception")}
		
		when:
			vo = storeInvMngrImpl.getInventory("skuId", "storeId" , true)
		
		then:
			InventoryException excep = thrown()
			excep.getMessage().equals(null)
			vo == null
	}
	
	def "getInventory(), catalogTools thrown BBBBusinessException"(){
		BBBStoreInventoryVO vo
		given:
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			1*mCatalogTools.getSkuThreshold(_,_) >> {throw new BBBBusinessException("")}
		
		when:
			vo = storeInvMngrImpl.getInventory("skuId", "storeId" , true)
		
		then:
			InventoryException excep = thrown()
			vo == null
	}
	
	def "getInventory(), setting storeInventories as empty list"(){
		BBBStoreInventoryVO vo
		given:
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			storeInvMngrImpl.getInventory(_,_,_,_) >> []
		
		when:
			vo = storeInvMngrImpl.getInventory("skuId", "storeId" , true)
		
		then:
			BBBBusinessException excep = thrown()
			excep.getMessage().equals("empty_storeId:Store ID entered is empty")
			vo == null
		
	}
	
	def "getFavStoreInventory(), Happy path"(){
		Map<String,Integer> map = new HashMap<String,Integer>()
		ThresholdVO skuThresholdVO = Mock()

		given:
			map.put("storeId", 100)
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			1*mBopusService.getInventoryForBopusItem(_, _ , _) >> map
			
		when:
			map = storeInvMngrImpl.getFavStoreInventory("skuId","siteId","storeId",true)
			
		then:
			map!=null
			map.get("skuId")!=null
			map.get("skuId") == 0
			
	}
	
	def "getFavStoreInventory(), Threshold available is greater than 7"(){
		Map<String,Integer> map = new HashMap<String,Integer>()
		ThresholdVO skuThresholdVO = Mock()
		given:
			map.put("storeId", 100)
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			skuThresholdVO.getThresholdAvailable() >> 100
			1*mBopusService.getInventoryForBopusItem(_, _ , _) >> map
		when:
			map = storeInvMngrImpl.getFavStoreInventory("skuId","siteId","storeId",true)
			
			
		then:
			map!=null
			map.get("skuId")!=null
			map.get("skuId") == 2
	}
	
	def "getFavStoreInventory(), Threshold available is equal to 7"(){
		Map<String,Integer> map = new HashMap<String,Integer>()
		ThresholdVO skuThresholdVO = Mock()
		given:
			map.put("storeId", 100)
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			skuThresholdVO.getThresholdAvailable() >> 7
			1*mBopusService.getInventoryForBopusItem(_, _ , _) >> map
		when:
			map = storeInvMngrImpl.getFavStoreInventory("skuId","siteId","storeId",true)
			
			
		then:
			map!=null
			map.get("skuId")!=null
			map.get("skuId") == 0
	}
	
	def "getFavStoreInventory(), Threshold limited is greater than 7"(){
		Map<String,Integer> map = new HashMap<String,Integer>()
		ThresholdVO skuThresholdVO = Mock()
		given:
			map.put("storeId", 7)
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			skuThresholdVO.getThresholdAvailable() >> 8
			skuThresholdVO.getThresholdLimited() >> 8
			1*mBopusService.getInventoryForBopusItem(_, _ , _) >> map
		when:
			map = storeInvMngrImpl.getFavStoreInventory("skuId","siteId","storeId",true)
			
			
		then:
			map!=null
			map.get("skuId")!=null
			map.get("skuId") == 1
	}
	
	def "getFavStoreInventory(), Threshold available is 0 and storeInventoryStock is 0"(){
		Map<String,Integer> map = new HashMap<String,Integer>()
		ThresholdVO skuThresholdVO = Mock()
		given:
			map.put("storeId", 0)
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			skuThresholdVO.getThresholdAvailable() >> 0
			1*mBopusService.getInventoryForBopusItem(_, _ , _) >> map
		when:
			map = storeInvMngrImpl.getFavStoreInventory("skuId","siteId","storeId",true)
			
		then:
			map!=null
			map.get("skuId")!=null
			map.get("skuId") == 1
	}
	
	def "setThresholdBySku(), Happy path"(){
		Map<String,BBBStoreInventoryVO> map = new HashMap<String,BBBStoreInventoryVO>()
		Map<String,Integer> inventoryBySkuMap = new HashMap<String,Integer>()
		ThresholdVO skuThresholdVO = Mock()

		given:
			inventoryBySkuMap.put("skuId", 10)
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			
		when:
			map = storeInvMngrImpl.setThresholdBySku("siteId",["skuId"],"storeId",inventoryBySkuMap)
			
		then:
			map.size() == 1
			map.get("skuId").getStoreInventoryStock() == 10
			map.get("skuId").getThresholdAvailable() == 0
			map.get("skuId").getThresholdLimited() == 0
			
	}
	
	def "setThresholdBySku(), skuIds passed is null"(){
		Map<String,BBBStoreInventoryVO> map = new HashMap<String,BBBStoreInventoryVO>()
		Map<String,Integer> inventoryBySkuMap = new HashMap<String,Integer>()

		given:
			inventoryBySkuMap.put("skuId", 10)
			
		when:
			map = storeInvMngrImpl.setThresholdBySku("siteId",null,"storeId",inventoryBySkuMap)
			
		then:
			map.size() == 0
	}
	
	def "setThresholdBySku(), inventoryBySkuMap is passed as null"(){
		Map<String,BBBStoreInventoryVO> map = new HashMap<String,BBBStoreInventoryVO>()
		Map<String,Integer> inventoryBySkuMap = new HashMap<String,Integer>()
		ThresholdVO skuThresholdVO = Mock()

		given:
			inventoryBySkuMap.put("skuId", 10)
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			
		when:
			map = storeInvMngrImpl.setThresholdBySku("siteId",["skuId"],"storeId",null)
			
		then:
			map.size() == 1
			map.get("skuId").getStoreInventoryStock() == 0
			map.get("skuId").getThresholdAvailable() == 0
			map.get("skuId").getThresholdLimited() == 0
	}
	
	def "setThresholdBySku(), inventoryBySkuMap is passed as empty "(){
		Map<String,BBBStoreInventoryVO> map = new HashMap<String,BBBStoreInventoryVO>()
		Map<String,Integer> inventoryBySkuMap = new HashMap<String,Integer>()
		ThresholdVO skuThresholdVO = Mock()

		given:
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			
		when:
			map = storeInvMngrImpl.setThresholdBySku("siteId",["skuId"],"storeId",inventoryBySkuMap)
			
		then:
			map.size() == 1
			map.get("skuId").getStoreInventoryStock() == 0
			map.get("skuId").getThresholdAvailable() == 0
			map.get("skuId").getThresholdLimited() == 0
	}
	
	def "setThresholdBySku(), inventoryBySkuMap has null key's value "(){
		Map<String,BBBStoreInventoryVO> map = new HashMap<String,BBBStoreInventoryVO>()
		Map<String,Integer> inventoryBySkuMap = new HashMap<String,Integer>()
		ThresholdVO skuThresholdVO = Mock()

		given:
			inventoryBySkuMap.put("skuId", null)
			1*mCatalogTools.getSkuThreshold(_,_) >> skuThresholdVO
			
		when:
			map = storeInvMngrImpl.setThresholdBySku("siteId",["skuId"],"storeId",inventoryBySkuMap)
			
		then:
			map.size() == 1
			map.get("skuId").getStoreInventoryStock() == 0
			map.get("skuId").getThresholdAvailable() == 0
			map.get("skuId").getThresholdLimited() == 0
	}
	
	def "setThresholdBySku(), catalogTools returns null skuThresholdVO "(){
		Map<String,BBBStoreInventoryVO> map = new HashMap<String,BBBStoreInventoryVO>()
		Map<String,Integer> inventoryBySkuMap = new HashMap<String,Integer>()

		given:
			inventoryBySkuMap.put("skuId", 10)
			1*mCatalogTools.getSkuThreshold(_,_) >> null
			
		when:
			map = storeInvMngrImpl.setThresholdBySku("siteId",["skuId"],"storeId",inventoryBySkuMap)
			
		then:
			map.size() == 1
			map.get("skuId").getStoreInventoryStock() == 10
			map.get("skuId").getThresholdAvailable() == 0
			map.get("skuId").getThresholdLimited() == 0
	}
	
	def "checkCommItemBopusEligible(), vo passed is null"(){
		//CommerceItemVO vo = Mock()
		boolean flag
		when:
			flag = storeInvMngrImpl.checkCommItemBopusEligible(null) 
		
		then:
			flag == false
	}
	
	def "checkCommItemBopusEligible(), vo passed with LTL flag set as true"(){
		given:
			CommerceItemVO vo = Mock()
			SKUDetailVO skuDetailVO = Mock()
			boolean flag
			vo.getSkuDetailVO()>> skuDetailVO
			skuDetailVO.isLtlItem() >> true
		when:
			flag = storeInvMngrImpl.checkCommItemBopusEligible(vo)
		
		then:
			flag == false
	}
	
	def "checkCommItemBopusEligible(), vo passed  with reference number with value"(){
		CommerceItemVO vo = Mock()
		BBBCommerceItem item = Mock()
		SKUDetailVO skuDetailVO = Mock()
		boolean flag
		
		given:
			vo.getSkuDetailVO()>> skuDetailVO
			skuDetailVO.isLtlItem() >> false
			vo.getBBBCommerceItem() >> item
			item.getReferenceNumber() >> "refNUm"
		when:
			flag = storeInvMngrImpl.checkCommItemBopusEligible(vo)
		
		then:
			flag == false
	}
	
	def "checkCommItemBopusEligible(), vo passed  with reference number as empty"(){
		CommerceItemVO vo = Mock()
		BBBCommerceItem item = Mock()
		SKUDetailVO skuDetailVO = Mock()
		boolean flag
		
		given:
			vo.getSkuDetailVO()>> skuDetailVO
			skuDetailVO.isLtlItem() >> false
			vo.getBBBCommerceItem() >> item
			item.getReferenceNumber() >> ""
		when:
			flag = storeInvMngrImpl.checkCommItemBopusEligible(vo)
		
		then:
			flag == true
	}
	
	def "checkCommItemBopusEligible(), vo passed  with BopusAllowed flag as true"(){
		CommerceItemVO vo = Mock()
		BBBCommerceItem item = Mock()
		SKUDetailVO skuDetailVO = Mock()
		boolean flag
		
		given:
			vo.getSkuDetailVO()>> skuDetailVO
			skuDetailVO.isLtlItem() >> false
			vo.getBBBCommerceItem() >> item
			item.getReferenceNumber() >> ""
			skuDetailVO.isBopusAllowed() >> true
		when:
			flag = storeInvMngrImpl.checkCommItemBopusEligible(vo)
		
		then:
			flag == false
	}
	
	def "checkCommItemBopusEligible(), vo passed  with BopusAllowed flag as false"(){
		CommerceItemVO vo = Mock()
		BBBCommerceItem item = Mock()
		SKUDetailVO skuDetailVO = Mock()
		boolean flag
		
		given:
			vo.getSkuDetailVO()>> skuDetailVO
			skuDetailVO.isLtlItem() >> false
			vo.getBBBCommerceItem() >> item
			item.getReferenceNumber() >> ""
			skuDetailVO.isBopusAllowed() >> false
		when:
			flag = storeInvMngrImpl.checkCommItemBopusEligible(vo)
		
		then:
			flag == true
	}
	
	def "getRestFavStoreInvMultiSkus(), favStoreId passed null"(){
		Profile profile = Mock()
		BBBSessionBean sessionBean = Mock()
		OrderHolder cart = Mock()
		BBBOrder order = Mock()
		given:
			requestMock.resolveName("/atg/userprofiling/Profile") >> profile
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> cart
			cart.getCurrent() >> order
			
		when:
			Map<String,Object>	map = storeInvMngrImpl.getRestFavStoreInvMultiSkus()
		
		then:
			map == null
	}
	
	def "getRestFavStoreInvMultiSkus(), profile is not transient"(){
		Profile profile = Mock()
		BBBSessionBean sessionBean = Mock()
		OrderHolder cart = Mock()
		BBBOrder order = Mock()
		given:
			requestMock.resolveName("/atg/userprofiling/Profile") >> profile
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> cart
			cart.getCurrent() >> order
			profile.isTransient() >> true
			
		when:
			Map<String,Object>	map = storeInvMngrImpl.getRestFavStoreInvMultiSkus()
		
		then:
			map == null
	}
	
	def "getRestFavStoreInvMultiSkus(), sessionBean's isInternationalShippingContext is true"(){
		Profile profile = Mock()
		BBBSessionBean sessionBean = Mock()
		OrderHolder cart = Mock()
		BBBOrder order = Mock()
		given:
			requestMock.resolveName("/atg/userprofiling/Profile") >> profile
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> cart
			cart.getCurrent() >> order
			sessionBean.isInternationalShippingContext() >> true
			
		when:
			Map<String,Object>	map = storeInvMngrImpl.getRestFavStoreInvMultiSkus()
		
		then:
			map == null
	}
	
	def "getRestFavStoreInvMultiSkus(), Happy path"(){
		Profile profile = Mock()
		BBBSessionBean sessionBean = Mock()
		OrderHolder cart = Mock()
		BBBOrder order = Mock()
		CommerceItemVO vo = Mock()
		SKUDetailVO skuDetailVO = Mock()
		BBBCommerceItem item = Mock()
		StoreDetails storeDetails = Mock()
		Map<String, Integer> favStoreInventory = new HashMap<String,Integer>()
		Map<String, BBBStoreInventoryVO> storeInventories = new HashMap<String, BBBStoreInventoryVO>()
		given:
			requestMock.resolveName("/atg/userprofiling/Profile") >> profile
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> cart
			cart.getCurrent() >> order
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			searchStoreManager.fetchFavoriteStoreId(_,_) >> "favStoreId"
			checkoutManager.getCartItemVOList(_) >> [vo]
			vo.getSkuDetailVO() >> skuDetailVO
			skuDetailVO.getSkuId() >> "skuId"
			skuDetailVO.isLtlItem() >> false
			vo.getBBBCommerceItem() >> item
			item.getReferenceNumber() >> ""
			skuDetailVO.isBopusAllowed() >> false
			item.getId() >> "id"
			searchStoreManager.fetchFavStoreDetails(_,_) >> storeDetails
			1*mBopusService.getMultiSkusStoreInventory(_,_) >> favStoreInventory
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEAPP
			
		when:
			Map<String,Object>	map = storeInvMngrImpl.getRestFavStoreInvMultiSkus()
		
		then:
			map.get("favStoreStockStatus") != null
			map.get("favStoreStockStatus").getAt("id") == 1
			map.get("storeDetails") != null
	}
	
	def "getRestFavStoreInvMultiSkus(), LTL flag set as true "(){
		Profile profile = Mock()
		BBBSessionBean sessionBean = Mock()
		OrderHolder cart = Mock()
		BBBOrder order = Mock()
		CommerceItemVO vo = Mock()
		SKUDetailVO skuDetailVO = Mock()
		BBBCommerceItem item = Mock()
		StoreDetails storeDetails = Mock()
		Map<String, Integer> favStoreInventory = new HashMap<String,Integer>()
		Map<String, BBBStoreInventoryVO> storeInventories = new HashMap<String, BBBStoreInventoryVO>()
		given:
			requestMock.resolveName("/atg/userprofiling/Profile") >> profile
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> cart
			cart.getCurrent() >> order
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			searchStoreManager.fetchFavoriteStoreId(_,_) >> "favStoreId"
			checkoutManager.getCartItemVOList(_) >> [vo]
			vo.getSkuDetailVO() >> skuDetailVO
			skuDetailVO.getSkuId() >> "skuId"
			skuDetailVO.isLtlItem() >> true
			item.getId() >> "id"
			searchStoreManager.fetchFavStoreDetails(_,_) >> storeDetails
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEAPP
			
		when:
			Map<String,Object>	map = storeInvMngrImpl.getRestFavStoreInvMultiSkus()
		
		then:
			map == null
	}
	
	def "getRestFavStoreInvMultiSkus(), fetchFavStoreDetails method returned null "(){
		Profile profile = Mock()
		BBBSessionBean sessionBean = Mock()
		OrderHolder cart = Mock()
		BBBOrder order = Mock()
		CommerceItemVO vo = Mock()
		SKUDetailVO skuDetailVO = Mock()
		BBBCommerceItem item = Mock()

		given:
			requestMock.resolveName("/atg/userprofiling/Profile") >> profile
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> cart
			cart.getCurrent() >> order
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			searchStoreManager.fetchFavoriteStoreId(_,_) >> "favStoreId"
			checkoutManager.getCartItemVOList(_) >> [vo]
			vo.getSkuDetailVO() >> skuDetailVO
			vo.getSkuDetailVO() >> skuDetailVO
			skuDetailVO.getSkuId() >> "skuId"
			skuDetailVO.isLtlItem() >> false
			vo.getBBBCommerceItem() >> item
			item.getReferenceNumber() >> ""
			skuDetailVO.isBopusAllowed() >> false
			item.getId() >> "id"
			searchStoreManager.fetchFavStoreDetails(_,_) >> null
			
		when:
			Map<String,Object>	map = storeInvMngrImpl.getRestFavStoreInvMultiSkus()
		
		then:
			map == null
	}
	
	def "getRestFavStoreInvMultiSkus(), empty channel passed "(){
		Profile profile = Mock()
		BBBSessionBean sessionBean = Mock()
		OrderHolder cart = Mock()
		BBBOrder order = Mock()
		CommerceItemVO vo = Mock()
		SKUDetailVO skuDetailVO = Mock()
		BBBCommerceItem item = Mock()
		StoreDetails storeDetails = Mock()
		Map<String, Integer> favStoreInventory = new HashMap<String,Integer>()
		Map<String, BBBStoreInventoryVO> storeInventories = new HashMap<String, BBBStoreInventoryVO>()
		given:
			requestMock.resolveName("/atg/userprofiling/Profile") >> profile
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> cart
			cart.getCurrent() >> order
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			searchStoreManager.fetchFavoriteStoreId(_,_) >> "favStoreId"
			checkoutManager.getCartItemVOList(_) >> [vo]
			vo.getSkuDetailVO() >> skuDetailVO
			skuDetailVO.getSkuId() >> "skuId"
			skuDetailVO.isLtlItem() >> false
			vo.getBBBCommerceItem() >> item
			item.getReferenceNumber() >> ""
			skuDetailVO.isBopusAllowed() >> false
			item.getId() >> "id"
			searchStoreManager.fetchFavStoreDetails(_,_) >> storeDetails
			1*mBopusService.getMultiSkusStoreInventory(_,_) >> favStoreInventory
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> ""
			
			
		when:
			Map<String,Object>	map = storeInvMngrImpl.getRestFavStoreInvMultiSkus()
		
		then:
			map == null
	}
	
	def "getRestFavStoreInvMultiSkus(), channel passed as MOB "(){
		Profile profile = Mock()
		BBBSessionBean sessionBean = Mock()
		OrderHolder cart = Mock()
		BBBOrder order = Mock()
		CommerceItemVO vo = Mock()
		SKUDetailVO skuDetailVO = Mock()
		BBBCommerceItem item = Mock()
		StoreDetails storeDetails = Mock()
		Map<String, Integer> favStoreInventory = new HashMap<String,Integer>()
		Map<String, BBBStoreInventoryVO> storeInventories = new HashMap<String, BBBStoreInventoryVO>()
		given:
			requestMock.resolveName("/atg/userprofiling/Profile") >> profile
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> cart
			cart.getCurrent() >> order
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			searchStoreManager.fetchFavoriteStoreId(_,_) >> "favStoreId"
			checkoutManager.getCartItemVOList(_) >> [vo]
			vo.getSkuDetailVO() >> skuDetailVO
			skuDetailVO.getSkuId() >> "skuId"
			skuDetailVO.isLtlItem() >> false
			vo.getBBBCommerceItem() >> item
			item.getReferenceNumber() >> ""
			skuDetailVO.isBopusAllowed() >> false
			item.getId() >> "id"
			searchStoreManager.fetchFavStoreDetails(_,_) >> storeDetails
			1*mBopusService.getMultiSkusStoreInventory(_,_) >> favStoreInventory
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MOB"
			
			
		when:
			Map<String,Object>	map = storeInvMngrImpl.getRestFavStoreInvMultiSkus()
		
		then:
			map == null
	}
	
	def "getRestFavStoreInvMultiSkus(), Happy path with channel as MOBILEAPP "(){
		Profile profile = Mock()
		BBBSessionBean sessionBean = Mock()
		OrderHolder cart = Mock()
		BBBOrder order = Mock()
		CommerceItemVO vo = Mock()
		CommerceItemVO vo1 = Mock()
		SKUDetailVO skuDetailVO = Mock()
		SKUDetailVO skuDetailVO1 = Mock()
		BBBCommerceItem item = Mock()
		BBBCommerceItem item1 = Mock()
		StoreDetails storeDetails = Mock()
		Map<String, Integer> favStoreInventory = new HashMap<String,Integer>()
		Map<String, BBBStoreInventoryVO> storeInventories = new HashMap<String, BBBStoreInventoryVO>()
		given:
			requestMock.resolveName("/atg/userprofiling/Profile") >> profile
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> cart
			cart.getCurrent() >> order
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			searchStoreManager.fetchFavoriteStoreId(_,_) >> "favStoreId"
			checkoutManager.getCartItemVOList(_) >> [vo,vo1]
			vo.getSkuDetailVO() >> skuDetailVO
			vo1.getSkuDetailVO() >> skuDetailVO1
			skuDetailVO.getSkuId() >> "skuId"
			skuDetailVO.isLtlItem() >> false
			skuDetailVO1.getSkuId() >> "skuId"
			skuDetailVO1.isLtlItem() >> true
			vo.getBBBCommerceItem() >> item
			vo1.getBBBCommerceItem() >> item1
			item.getReferenceNumber() >> ""
			item1.getReferenceNumber() >> ""
			skuDetailVO.isBopusAllowed() >> false
			skuDetailVO1.isBopusAllowed() >> false
			item.getId() >> "id"
			item1.getId() >> "id1"
			searchStoreManager.fetchFavStoreDetails(_,_) >> storeDetails
			1*mBopusService.getMultiSkusStoreInventory(_,_) >> favStoreInventory
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEAPP
			
			
		when:
			Map<String,Object>	map = storeInvMngrImpl.getRestFavStoreInvMultiSkus()
		
		then:
			map.get("favStoreStockStatus") != null
			map.get("favStoreStockStatus").getAt("id") == 1
			map.get("storeDetails") != null
	}
	
	def "getRestFavStoreInvMultiSkus(), MobileWeb as channel "(){
		Profile profile = Mock()
		BBBSessionBean sessionBean = Mock()
		OrderHolder cart = Mock()
		BBBOrder order = Mock()
		CommerceItemVO vo = Mock()
		SKUDetailVO skuDetailVO = Mock()
		BBBCommerceItem item = Mock()
		StoreDetails storeDetails = Mock()
		Map<String, Integer> favStoreInventory = new HashMap<String,Integer>()
		Map<String, BBBStoreInventoryVO> storeInventories = new HashMap<String, BBBStoreInventoryVO>()
		given:
			requestMock.resolveName("/atg/userprofiling/Profile") >> profile
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> sessionBean
			requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> cart
			cart.getCurrent() >> order
			storeInvMngrImpl.extractCurrentSiteId() >> "siteId"
			searchStoreManager.fetchFavoriteStoreId(_,_) >> "favStoreId"
			checkoutManager.getCartItemVOList(_) >> [vo]
			vo.getSkuDetailVO() >> skuDetailVO
			skuDetailVO.getSkuId() >> "skuId"
			skuDetailVO.isLtlItem() >> false
			vo.getBBBCommerceItem() >> item
			item.getReferenceNumber() >> ""
			skuDetailVO.isBopusAllowed() >> false
			item.getId() >> "id"
			searchStoreManager.fetchFavStoreDetails(_,_) >> storeDetails
			1*mBopusService.getMultiSkusStoreInventory(_,_) >> favStoreInventory
			requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB
			
			
		when:
			Map<String,Object>	map = storeInvMngrImpl.getRestFavStoreInvMultiSkus()
		
		then:
			map.get("favStoreStockStatus") != null
			map.get("favStoreStockStatus").getAt("id") == 1
			map.get("storeDetails") != null
	}
}
