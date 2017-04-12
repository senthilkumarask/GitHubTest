package com.bbb.commerce.order.purchase

import javax.transaction.Transaction
import javax.transaction.TransactionManager

import spock.lang.specification.BBBExtendedSpec
import atg.adapter.gsa.GSARepository
import atg.commerce.CommerceException
import atg.commerce.claimable.ClaimableManager
import atg.commerce.claimable.ClaimableTools
import atg.commerce.gifts.GiftlistManager
import atg.commerce.order.CommerceItem
import atg.commerce.order.CreditCard
import atg.commerce.order.OrderHolder
import atg.commerce.order.ShippingGroupCommerceItemRelationship
import atg.commerce.order.ShippingGroupRelationshipContainerImpl
import atg.commerce.order.purchase.AddCommerceItemInfo
import atg.commerce.order.purchase.CommerceItemShippingInfoContainer
import atg.commerce.order.purchase.ShippingGroupMapContainer
import atg.commerce.pricing.OrderPriceInfo
import atg.commerce.promotion.PromotionException
import atg.commerce.promotion.PromotionTools
import atg.commerce.util.RepeatingRequestMonitor
import atg.dtm.TransactionDemarcationException
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.service.pipeline.PipelineManager
import atg.service.pipeline.RunProcessException
import atg.userprofiling.Profile

import com.bbb.account.api.BBBAddressAPI
import com.bbb.account.api.BBBAddressVO
import com.bbb.browse.webservice.manager.BBBEximManager
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.catalog.vo.ShipMethodVO
import com.bbb.commerce.catalog.vo.ThresholdVO
import com.bbb.commerce.checkout.BBBVerifiedByVisaConstants
import com.bbb.commerce.checkout.manager.BBBCheckoutManager
import com.bbb.commerce.checkout.util.BBBCouponUtil
import com.bbb.commerce.common.BBBStoreInventoryContainer
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.AddressVO
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.commerce.giftregistry.vo.RegistryTypes
import com.bbb.commerce.inventory.BBBInventoryManager
import com.bbb.commerce.inventory.BBBInventoryManagerImpl
import com.bbb.commerce.inventory.BopusInventoryService
import com.bbb.commerce.order.BBBCommerceItemManager
import com.bbb.commerce.order.BBBGiftCard
import com.bbb.commerce.order.BBBOrderManager
import com.bbb.commerce.order.BBBPaymentGroupManager
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship
import com.bbb.commerce.order.BBBShippingGroupManager
import com.bbb.commerce.order.paypal.BBBPayPalServiceManager
import com.bbb.commerce.order.paypal.BBBPayPalSessionBean
import com.bbb.commerce.order.purchase.CheckoutProgressStates.DEFAULT_STATES
import com.bbb.commerce.pricing.BBBPricingManager
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants
import com.bbb.constants.BBBGiftRegistryConstants
import com.bbb.constants.BBBPayPalConstants
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.ecommerce.order.BBBStoreShippingGroup
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.cache.CoherenceCacheContainer
import com.bbb.framework.webservices.vo.ErrorStatus
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.NonMerchandiseCommerceItem
import com.bbb.paypal.BBBSetExpressCheckoutResVO
import com.bbb.profile.session.BBBSavedItemsSessionBean
import com.bbb.profile.session.BBBSessionBean
import com.bbb.repository.RepositoryItemMock
import com.bbb.utils.CommonConfiguration
import com.bbb.vo.wishlist.WishListVO
import com.bbb.wishlist.BBBWishlistManager
import com.bbb.wishlist.GiftListVO

class BBBCartFormhandlerSpecification extends BBBExtendedSpec {

	BBBCartFormhandler cartObj
	OrderHolder orderHolderMock = Mock()
	BBBOrderImpl orderMock = Mock()
	BBBOrderManager orderManagerMock = Mock()
	CommonConfiguration cConfig = Mock()
	BopusInventoryService biServiceMock = Mock()
	LblTxtTemplateManager errorMSGMock = Mock()
	BBBCatalogToolsImpl cToolsMock = Mock()
	BBBInventoryManagerImpl inManagerMock = Mock()
	CoherenceCacheContainer cacheMock = Mock()
	GSARepository repMock = Mock()
	GiftlistManager glManagerMock =Mock()
	MutableRepositoryItem repItemMock = Mock()
	Profile profileMock = Mock()
	BBBCommerceItemManager cManagerMock =Mock()
	BBBSavedItemsSessionBean sItemSessionMock =Mock()
	RepeatingRequestMonitor reqMonitorMock = Mock()
	TransactionManager trMock = Mock()
	BBBWishlistManager wishLisManger =Mock()
	BBBPricingManager priceManager = Mock()
	BBBPurchaseProcessHelper ppHelper =Mock()
	BBBPayPalServiceManager paypalManager  = Mock()
	CheckoutProgressStates pState = Mock()
	BBBShippingGroupManager sGroupManager = Mock()
	BBBSessionBean bSessionbean = new BBBSessionBean()
	BBBCheckoutManager cManager =Mock()
	BBBAddressAPI addressAPI = Mock()
	BBBHardGoodShippingGroup hShip = Mock()
	BBBStoreShippingGroup sShip = Mock()
	PipelineManager plManager = Mock()
	BBBCouponUtil cUtilMock  =Mock()
	PromotionTools pTools = Mock()
	ClaimableManager clManager = Mock()
	ClaimableTools clTools = Mock()
	BBBPaymentGroupManager pgMAnager = Mock()
	GiftRegistryManager gfManager = Mock()
	BBBEximManager eximanager = Mock()
	BBBStoreInventoryContainer siContainer  = Mock()
	ShippingGroupRelationshipContainerImpl relContainer = Mock()
	CommerceItemShippingInfoContainer citemShipContainer = Mock()
	ShippingGroupMapContainer shipGrpMap =Mock()
	
	def setup(){
		cartObj = new BBBCartFormhandler()
		cartObj.setShoppingCart(orderHolderMock)
		orderHolderMock.getCurrent() >> orderMock
		cartObj.setCommonConfiguration(cConfig)
		cartObj.setOrderManager(orderManagerMock)
		cConfig.isLogDebugEnableOnCartFormHandler() >> true
		cartObj.setBopusInventoryService(biServiceMock)
		cartObj.setLblTxtTemplateManager(errorMSGMock)
		cartObj.setCatalogUtil(cToolsMock)
		cartObj.setInventoryManager(inManagerMock)
		cartObj.setCacheContainer(cacheMock)
		cartObj.setLocalStoreRepository(repMock)
		cartObj.setProfile(profileMock)
		cartObj.setUserProfile(profileMock)
		cartObj.setGiftlistManager(glManagerMock)
		cartObj.setCommerceItemManager(cManagerMock)
		cartObj.setSavedItemsSessionBean(sItemSessionMock)
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		cartObj.setWishlistManager(wishLisManger)
		cartObj.setTransactionManager(trMock)
		cartObj.setPricingManager(priceManager)
		cartObj.setMsgHandler(errorMSGMock)
		cartObj.setPurchaseProcessHelper(ppHelper)
		requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> bSessionbean
		cartObj.setPaypalServiceManager(paypalManager)
		cartObj.setCheckoutManager(cManager)
		cartObj.setCheckoutState(pState)
		cartObj.setShippingGroupManager(sGroupManager)
		cartObj.setPipelineManager(plManager)
		cartObj.setCouponUtil(cUtilMock)
		cartObj.setPromoTools(pTools)
		cartObj.setClaimableManager(clManager)
		cartObj.setPaymentGroupManager(pgMAnager)
		cartObj.setRegistryManager(gfManager)
		cartObj.setEximManager(eximanager)
		cartObj.setStoreInventoryContainer(siContainer)
		cartObj.setCommerceItemShippingInfoContainer(citemShipContainer)
		cartObj.setShippingGroupMapContainer(shipGrpMap)
	}
	def "Handle method to edit Personalized item"(){
		given:
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB
		cartObj.setCommerceItemId("ci1234")
		cartObj.setReferenceNumber("ref1234")
		cartObj.setEditItemInOrderFormsSuccessURL("cartObj")
		cartObj.setEditItemInOrderFormsSuccessURL("cartObj")
		CommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		BBBCommerceItem cItem3 = Mock()
		BBBCommerceItem cItem4 = Mock()
		cItem1.getId() >> "ci12345"
		cItem3.getId() >> "ci1234"
		cItem4.getId() >> "ci1234"
		cItem3.getReferenceNumber() >> "ref1235"
		cItem4.getReferenceNumber() >> "ref1234"
		orderMock.getCommerceItems() >> [cItem,cItem1,cItem3,cItem4]
		when:
		cartObj.handleEditPersonalisedItem(requestMock, responseMock)
		then:
		cartObj.getEditItemInOrderFormsErrorURL().equals("")
		cartObj.getEditItemInOrderFormsSuccessURL().equals("")
		cartObj.getErrorMap().get(BBBCoreConstants.ERR_EDIT_INVALID_REF_NUM).equals(BBBCoreConstants.ERR_EDIT_INVALID_REF_NUM_MSG)
		1*cItem4.setEximPricingReq(true)
		1*orderManagerMock.updateOrder(orderMock)
		1*ppHelper.runRepricingProcess(*_)
	}
	def "Handle method to edit Personalized item throw RunProcessException"(){
		given:
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEAPP
		cartObj.setCommerceItemId("ci1234")
		cartObj.setReferenceNumber("ref1234")
		cartObj.setEditItemInOrderFormsSuccessURL("cartObj")
		cartObj.setEditItemInOrderFormsSuccessURL("cartObj")
		BBBCommerceItem cItem = Mock()
		cItem.getId() >> "ci1234"
		cItem.getReferenceNumber() >> "ref1234"
		orderMock.getCommerceItems() >> [cItem]
		1*ppHelper.runRepricingProcess(*_) >> {throw new RunProcessException()}
		when:
		cartObj.handleEditPersonalisedItem(requestMock, responseMock)
		then:
		cartObj.getEditItemInOrderFormsErrorURL().equals("")
		cartObj.getEditItemInOrderFormsSuccessURL().equals("")
		1*cItem.setEximPricingReq(true)
		1*cItem.setEximErrorExists(true)
		1*orderManagerMock.updateOrder(orderMock)
	}
	def "Handle method to edit Personalized item commerce list is empty"(){
		given:
		cartObj.setEditItemInOrderFormsSuccessURL("cartObj")
		cartObj.setEditItemInOrderFormsErrorURL("cartObj")
		orderMock.getCommerceItems() >> null
		when:
		cartObj.handleEditPersonalisedItem(requestMock, responseMock)
		then:
		cartObj.getEditItemInOrderFormsErrorURL().equals("cartObj")
		cartObj.getEditItemInOrderFormsSuccessURL().equals("cartObj")
		0*orderManagerMock.updateOrder(orderMock)
	}
	def "handleReserveNow method validates if the the inventory is zero."(){
		given:
		cartObj.setJsonResultString("{\"addItemResults\":[{\"storeId\":\"st1234\",\"skuId\":\"sku12345\",\"qty\":\"1\"},{\"storeId\":\"st1235\",\"skuId\":\"sku12346\",\"qty\":\"2\"}]}")
		ThresholdVO thresholdVO = new ThresholdVO()
		thresholdVO.setThresholdLimited(3)
		1*cToolsMock.getSkuThreshold(_,"sku12346") >> thresholdVO
		1 *biServiceMock.getInventoryForBopusItem(_, _, false) >> ["st1235":10]
		1*inManagerMock.getInventoryStatus(10, 2, thresholdVO, "st1235") >> 0
		when:
		boolean valid = cartObj.handleReserveNow(requestMock, responseMock, "favStoreState")
		then:
		valid
		0 * errorMSGMock.getErrMsg(BBBCoreErrorConstants.ERROR_FROM_DOM,BBBCoreConstants.DEFAULT_LOCALE, null)
	}
	def "handleReserveNow method validates if the the inventory is zero limited qunantity."(){
		given:
		cartObj.setJsonResultString("{\"addItemResults\":[{\"storeId\":\"st1234\",\"skuId\":\"sku12345\",\"qty\":\"1\"},{\"storeId\":\"st1235\",\"skuId\":\"sku12346\",\"qty\":\"2\"}]}")
		ThresholdVO thresholdVO = new ThresholdVO()
		thresholdVO.setThresholdLimited(3)
		1*cToolsMock.getSkuThreshold(_,"sku12346") >> thresholdVO
		1 *biServiceMock.getInventoryForBopusItem(_, _, false) >> ["st1235":10]
		1*inManagerMock.getInventoryStatus(10, 2, thresholdVO, "st1235") >> 2
		when:
		boolean valid = cartObj.handleReserveNow(requestMock, responseMock, "favStoreState")
		then:
		valid
		0 * errorMSGMock.getErrMsg(BBBCoreErrorConstants.ERROR_FROM_DOM,BBBCoreConstants.DEFAULT_LOCALE, null)
	}
	def "handleReserveNow method validates if the the inventory is zero insufficent quantity."(){
		given:
		cartObj.setJsonResultString("{\"addItemResults\":[{\"storeId\":\"st1235\",\"skuId\":\"sku12346\",\"qty\":\"3\"}]}")
		ThresholdVO thresholdVO = new ThresholdVO()
		thresholdVO.setThresholdLimited(3)
		1 * cToolsMock.getSkuThreshold(_,"sku12346") >> thresholdVO
		1 * biServiceMock.getInventoryForBopusItem(_, _, false) >> ["st1235":2]
		1 * inManagerMock.getInventoryStatus(2, 3, thresholdVO, "st1235") >> 0
		1 * errorMSGMock.getErrMsg(BBBCoreErrorConstants.ERROR_INSUFFICIENT_MEMORY,BBBCoreConstants.DEFAULT_LOCALE, null) >> BBBCoreErrorConstants.ERROR_INSUFFICIENT_MEMORY
		when:
		boolean valid = cartObj.handleReserveNow(requestMock, responseMock, "favStoreState")
		then:
		valid == false
	}
	def "handleReserveNow method validates if the the inventory is zero quanity from service for corresponding store is zero."(){
		given:
		cartObj.setJsonResultString("{\"addItemResults\":[{\"storeId\":\"st1235\",\"skuId\":\"sku12346\",\"qty\":\"3\"}]}")
		1 * cToolsMock.getSkuThreshold(_,"sku12346") >> null
		1 * biServiceMock.getInventoryForBopusItem(_, _, false) >> ["st1235":0]
		1 * inManagerMock.getInventoryStatus(0, 3, _, "st1235") >> 0
		1 * errorMSGMock.getErrMsg(BBBCoreErrorConstants.ERROR_EMPTY_SKU_THRESHOLD,BBBCoreConstants.DEFAULT_LOCALE, null) >> BBBCoreErrorConstants.ERROR_EMPTY_SKU_THRESHOLD
		when:
		boolean valid = cartObj.handleReserveNow(requestMock, responseMock, "favStoreState")
		then:
		valid == false
	}
	def "handleReserveNow method validates if the the inventory is zero quanity from service and thershold both are same."(){
		given:
		cartObj.setJsonResultString("{\"addItemResults\":[{\"storeId\":\"st1235\",\"skuId\":\"sku12346\",\"qty\":\"3\"}]}")
		ThresholdVO thresholdVO = new ThresholdVO()
		thresholdVO.setThresholdLimited(10)
		1 * cToolsMock.getSkuThreshold(_,"sku12346") >> thresholdVO
		1 * biServiceMock.getInventoryForBopusItem(_, _, false) >> ["st1235":10]
		1 * inManagerMock.getInventoryStatus(10, 3, _, "st1235") >> 0
		1 * errorMSGMock.getErrMsg(BBBCoreErrorConstants.ERROR_INVENTORY_NOT_AVAIL,BBBCoreErrorConstants.ERROR_INVENTORY_NOT_AVAIL, null) >> BBBCoreErrorConstants.ERROR_INVENTORY_NOT_AVAIL
		1*cacheMock.get("st1235-sku12346","localstore-near-local-store-inv") >> new Object()
		1*repMock.getItemForUpdate("st1235:sku12346","storeLocalInventory") >> repItemMock
		when:
		boolean valid = cartObj.handleReserveNow(requestMock, responseMock, "favStoreState")
		then:
		valid == false
		1*cacheMock.remove(_, "localstore-near-local-store-inv")
		1*cacheMock.put(_, _, "localstore-near-local-store-inv")
		1*repMock.updateItem(repItemMock)
		1*repItemMock.setPropertyValue("stockLevel",_)
	}
	def "handleReserveNow method validates if the the inventory is zero quanity inventory status as unavailable."(){
		given:
		cartObj.setJsonResultString("{\"addItemResults\":[{\"storeId\":\"st1235\",\"skuId\":\"sku12346\",\"qty\":\"3\"}]}")
		ThresholdVO thresholdVO = new ThresholdVO()
		thresholdVO.setThresholdLimited(5)
		1 * cToolsMock.getSkuThreshold(_,"sku12346") >> thresholdVO
		1 * biServiceMock.getInventoryForBopusItem(_, _, false) >> ["st1235":10]
		1 * inManagerMock.getInventoryStatus(10, 3, _, "st1235") >> 1
		1 * errorMSGMock.getErrMsg(BBBCoreErrorConstants.ERROR_INSUFFICIENT_MEMORY,BBBCoreConstants.DEFAULT_LOCALE, null) >> BBBCoreErrorConstants.ERROR_INSUFFICIENT_MEMORY
		1*cacheMock.get("st1235-sku12346","localstore-near-local-store-inv") >> null
		1*repMock.getItemForUpdate("st1235:sku12346","storeLocalInventory") >> null
		when:
		boolean valid = cartObj.handleReserveNow(requestMock, responseMock, "favStoreState")
		then:
		valid == false
		0*cacheMock.remove(_, "localstore-near-local-store-inv")
		0*cacheMock.put(_, _, "localstore-near-local-store-inv")
		0*repMock.updateItem(repItemMock)
		0*repItemMock.setPropertyValue("stockLevel",_)
	}
	def "handleReserveNow method validates if the the inventory is zero quanity inventory status as unavailable and quantity is one."(){
		given:
		cartObj.setJsonResultString("{\"addItemResults\":[{\"storeId\":\"st1235\",\"skuId\":\"sku12346\"}]}")
		ThresholdVO thresholdVO = new ThresholdVO()
		thresholdVO.setThresholdLimited(5)
		1 * cToolsMock.getSkuThreshold(_,"sku12346") >> thresholdVO
		1 * biServiceMock.getInventoryForBopusItem(_, _, false) >> ["st1235":10]
		1 * inManagerMock.getInventoryStatus(10,_, _, "st1235") >> 1
		1 * errorMSGMock.getErrMsg(BBBCoreErrorConstants.ERROR_INVENTORY_NOT_AVAIL,BBBCoreErrorConstants.ERROR_INVENTORY_NOT_AVAIL, null) >> BBBCoreErrorConstants.ERROR_INVENTORY_NOT_AVAIL
		1*cacheMock.get("st1235-sku12346","localstore-near-local-store-inv") >> null
		1*repMock.getItemForUpdate("st1235:sku12346","storeLocalInventory") >> {throw new RepositoryException("Mock of repositoryexception")}
		when:
		boolean valid = cartObj.handleReserveNow(requestMock, responseMock, "favStoreState")
		then:
		valid == false
		0*cacheMock.remove(_, "localstore-near-local-store-inv")
		0*cacheMock.put(_, _, "localstore-near-local-store-inv")
		0*repMock.updateItem(repItemMock)
		0*repItemMock.setPropertyValue("stockLevel",_)
	}
	def "handleReserveNow method validates if the the inventory is zero emapty amp value retrun from service."(){
		given:
		cartObj.setJsonResultString("{\"addItemResults\":[{\"storeId\":\"st1234\",\"skuId\":\"sku12345\",\"qty\":\"1\"}]}")
		1 * errorMSGMock.getErrMsg(BBBCoreErrorConstants.ERROR_FROM_DOM,BBBCoreConstants.DEFAULT_LOCALE, null) >> BBBCoreErrorConstants.ERROR_FROM_DOM
		when:
		cartObj.handleReserveNow(requestMock, responseMock, "favStoreState")
		then:
		1 * biServiceMock.getInventoryForBopusItem(_, _, false)
	}
	def "handleReserveNow method validates if the the inventory is zero favstorestate is empty."(){
		given:
		cartObj.setJsonResultString("{\"addItemResults1\":[{\"storeId\":\"st1234\",\"skuId\":\"sku12345\",\"qty\":\"1\"},{\"storeId\":\"st1235\",\"skuId\":\"sku12346\",\"qty\":\"2\"}]}")
		ThresholdVO thresholdVO = new ThresholdVO()
		thresholdVO.setThresholdLimited(3)
		0*cToolsMock.getSkuThreshold(_,"sku12346") >> thresholdVO
		0 *biServiceMock.getInventoryForBopusItem(_, _, false) >> ["st1235":10]
		0*inManagerMock.getInventoryStatus(10, 2, thresholdVO, "st1235") >> 0
		when:
		boolean valid = cartObj.handleReserveNow(requestMock, responseMock, null)
		then:
		valid == false
		0 * errorMSGMock.getErrMsg(BBBCoreErrorConstants.ERROR_FROM_DOM,BBBCoreConstants.DEFAULT_LOCALE, null)
	}
	def "handleReserveNow method validates if the the inventory is zero favstorestate is empty store id not available in map."(){
		given:
		cartObj.setJsonResultString("{\"addItemResults\":[{\"storeId\":\"st1235\",\"skuId\":\"sku12346\",\"qty\":null}]}")
		ThresholdVO thresholdVO = new ThresholdVO()
		thresholdVO.setThresholdLimited(3)
		0*cToolsMock.getSkuThreshold(_,"sku12346") >> thresholdVO
		1 *biServiceMock.getInventoryForBopusItem(_, _, false) >> ["st12351":10]
		0*inManagerMock.getInventoryStatus(10, 2, thresholdVO, "st1235") >> 0
		when:
		boolean valid = cartObj.handleReserveNow(requestMock, responseMock, "favStoreState")
		then:
		valid == false
		0 * errorMSGMock.getErrMsg(BBBCoreErrorConstants.ERROR_FROM_DOM,BBBCoreConstants.DEFAULT_LOCALE, null)
	}
	def "handleReserveNow method validates if the the inventory is zero  throws system exception."(){
		given:
		cartObj.setJsonResultString("{\"addItemResults\":[{\"storeId\":\"st1235\",\"skuId\":\"sku12346\",\"qty\":null}]}")
		ThresholdVO thresholdVO = new ThresholdVO()
		thresholdVO.setThresholdLimited(3)
		1 *biServiceMock.getInventoryForBopusItem(_, _, false) >> {throw new BBBSystemException("Mock of System exception")}
		1 * errorMSGMock.getErrMsg(BBBCoreErrorConstants.ERROR_WHILE_DOM_CALL,BBBCoreErrorConstants.ERROR_WHILE_DOM_CALL, null) >> BBBCoreErrorConstants.ERROR_WHILE_DOM_CALL
		when:
		boolean valid = cartObj.handleReserveNow(requestMock, responseMock, "favStoreState")
		then:
		valid == false
		0 * errorMSGMock.getErrMsg(BBBCoreErrorConstants.ERROR_FROM_DOM,BBBCoreConstants.DEFAULT_LOCALE, null)
		0*cToolsMock.getSkuThreshold(_,"sku12346")
		0*inManagerMock.getInventoryStatus(10, 2, thresholdVO, "st1235")
	}
	def "handleReserveNow method validates if the the inventory is zero  throws Business exception."(){
		given:
		cartObj.setJsonResultString("{\"addItemResults\":[{\"storeId\":\"st1235\",\"skuId\":\"sku12346\",\"qty\":null}]}")
		ThresholdVO thresholdVO = new ThresholdVO()
		thresholdVO.setThresholdLimited(3)
		1 *biServiceMock.getInventoryForBopusItem(_, _, false) >> {throw new BBBBusinessException("Mock of Business exception")}
		when:
		boolean valid = cartObj.handleReserveNow(requestMock, responseMock, "favStoreState")
		then:
		valid == false
		0*inManagerMock.getInventoryStatus(10, 2, thresholdVO, "st1235") 
		0*cToolsMock.getSkuThreshold(_,"sku12346")
	}
	def "This method is used to add item to order from gift registry"(){
		given:
		cartObj= Spy()
		spyObjIntilization(cartObj)
		cartObj.setJsonResultString("{\"addItemResults\":[{\"prodId\":\"pr1234\",\"bts\":\"false\"}]}")
		cartObj.isBTSProduct(_) >> true
		cartObj.handleAddItemToOrder(requestMock, responseMock) >> {}
		when:
		cartObj.handleAddRegistryItemToOrder(requestMock, responseMock)
		then:
		cartObj.getJsonResultString().contains("true")
	}
	def "This method is used to add item to order from gift registry dose not contain additemresults"(){
		given:
		cartObj= Spy()
		spyObjIntilization(cartObj)
		cartObj.setJsonResultString("{\"addItemResults1\":[{\"prodId\":\"pr1234\",\"bts\":\"false\"}]}")
		cartObj.isBTSProduct(_) >> false
		cartObj.handleAddItemToOrder(requestMock, responseMock) >> {}
		when:
		cartObj.handleAddRegistryItemToOrder(requestMock, responseMock)
		then:
		cartObj.getJsonResultString().equals("{\"addItemResults\":[{\"prodId\":\"pr1234\",\"bts\":\"true\"}]}") == false
	}
	def "This method is used to add item to order from gift registry throw Exception"(){
		given:
		cartObj= Spy()
		spyObjIntilization(cartObj)
		cartObj.setJsonResultString("{\"addItemResults\":[{\"prodId\":\"pr1234\",\"bts\":\"false\"}]}")
		cartObj.isBTSProduct(_) >> {throw new Exception()}
		cartObj.handleAddItemToOrder(requestMock, responseMock) >> {}
		when:
		cartObj.handleAddRegistryItemToOrder(requestMock, responseMock)
		then:
		cartObj.getJsonResultString().equals("{\"addItemResults\":[{\"prodId\":\"pr1234\",\"bts\":\"true\"}]}") == false
	}
	def "This method is used to add item to order from wish list"(){
		given:
		cartObj= Spy()
		spyObjIntilization(cartObj)
		requestMock.getHeader(_) >> BBBCoreConstants.MOBILEWEB
		cartObj.handleAddItemToOrder(requestMock, responseMock) >> {true}
		when:
		boolean valid = cartObj.handleMoveWishListItemToOrderForms(requestMock, responseMock)
		then:
		1*requestMock.setAttribute(BBBCoreConstants.SEND_SFL_COOKIE, Boolean.TRUE)
		cartObj.isFromWishlist()
		valid
	}
	def "This method is used to add item to order from wish list channel as mobile app"(){
		given:
		cartObj= Spy()
		spyObjIntilization(cartObj)
		requestMock.getHeader(_) >> BBBCoreConstants.MOBILEAPP
		cartObj.handleAddItemToOrder(requestMock, responseMock) >> {true}
		when:
		boolean valid = cartObj.handleMoveWishListItemToOrder(requestMock, responseMock)
		then:
		1*requestMock.setAttribute(BBBCoreConstants.SEND_SFL_COOKIE, Boolean.TRUE)
		cartObj.isFromWishlist()
		valid
	}
	def "This method is used to add item to order from wish list channel as desktop"(){
		given:
		cartObj= Spy()
		spyObjIntilization(cartObj)
		requestMock.getHeader(_) >> "desktop"
		cartObj.handleAddItemToOrder(requestMock, responseMock) >> {true}
		when:
		boolean valid = cartObj.handleMoveWishListItemToOrder(requestMock, responseMock)
		then:
		0*requestMock.setAttribute(BBBCoreConstants.SEND_SFL_COOKIE, Boolean.TRUE)
		cartObj.isFromWishlist()
		valid
	}
	def "this method is used to undo item moved to cart for registered user"(){
		given:
		RepositoryItem repItem = Mock()
		BBBCommerceItem cItem  = Mock()
		orderMock.getMovedCommerceItem() >> cItem
		profileMock.getPropertyValue("wishlist") >> repItem
		repItem.getRepositoryId() >> "ws1234"
		1*glManagerMock.getGiftitem("ws1234") >> repItem
		1*glManagerMock.getGiftlistItemQuantityDesired("ws1234") >> 2l
		cartObj.setWishlistItemId("ws1234")
		when:
		boolean valid  = cartObj.handleUndoCartItemMove(requestMock, responseMock)
		then:
		cartObj.getWishListId().equals("ws1234")
		0*glManagerMock.removeGiftlist(_, "ws1234")
		1*cManagerMock.addItemToOrder(orderMock, cItem)
		1*glManagerMock.setGiftlistItemQuantityDesired("ws1234", _, 2)
		valid
	}
	
	def "this method is used to undo item moved to cart for registered user throw commerce exception"(){
		given:
		RepositoryItem repItem = Mock()
		BBBCommerceItem cItem  = Mock()
		orderMock.getMovedCommerceItem() >> cItem
		profileMock.getPropertyValue("wishlist") >> repItem
		repItem.getRepositoryId() >> "ws1234"
		1*glManagerMock.getGiftitem("ws1234") >> repItem
		1*glManagerMock.getGiftlistItemQuantityDesired("ws1234") >> 0
		1*cManagerMock.addItemToOrder(orderMock, cItem) >> {throw new CommerceException("Mock of commerce exception")}
		cartObj.setWishlistItemId("ws1234")
		when:
		boolean valid  = cartObj.handleUndoCartItemMove(requestMock, responseMock)
		then:
		0*glManagerMock.setGiftlistItemQuantityDesired("ws1234", _, 0)
		cartObj.getWishListId().equals("ws1234")
		1*glManagerMock.removeItemFromGiftlist(_, "ws1234")
		valid
	}
	def "this method is used to undo item moved to cart for registered user quantity from wish list is negative"(){
		given:
		RepositoryItem repItem = Mock()
		BBBCommerceItem cItem  = Mock()
		orderMock.getMovedCommerceItem() >> cItem
		profileMock.getPropertyValue("wishlist") >> repItem
		repItem.getRepositoryId() >> "ws1234"
		1*glManagerMock.getGiftitem("ws1234") >> repItem
		1*glManagerMock.getGiftlistItemQuantityDesired("ws1234") >> -1
		cartObj.setWishlistItemId("ws1234")
		when:
		boolean valid  = cartObj.handleUndoCartItemMove(requestMock, responseMock)
		then:
		1*cManagerMock.addItemToOrder(orderMock, cItem)
		cartObj.setWishlistItemId("ws1234")
		0*glManagerMock.setGiftlistItemQuantityDesired("ws1234", _, 0)
		cartObj.getWishListId().equals("ws1234")
		1*glManagerMock.removeItemFromGiftlist(_, "ws1234")
		valid
	}
	def "this method is used to undo item moved to cart for registered user repository itme from wish list is null"(){
		given:
		RepositoryItem repItem = Mock()
		BBBCommerceItem cItem  = Mock()
		orderMock.getMovedCommerceItem() >> cItem
		profileMock.getPropertyValue("wishlist") >> repItem
		repItem.getRepositoryId() >> "ws1234"
		1*glManagerMock.getGiftitem("ws1234") >> null
		1*glManagerMock.getGiftlistItemQuantityDesired("ws1234") >> -1
		cartObj.setWishlistItemId("ws1234")
		when:
		boolean valid  = cartObj.handleUndoCartItemMove(requestMock, responseMock)
		then:
		1*cManagerMock.addItemToOrder(orderMock, cItem)
		cartObj.setWishlistItemId("ws1234")
		0*glManagerMock.setGiftlistItemQuantityDesired("ws1234", _, 0)
		cartObj.getWishListId().equals("ws1234")
		0*glManagerMock.removeItemFromGiftlist(_, "ws1234")
		valid
	}
	
	def "this method is used to undo item moved to cart for transient user"(){
		given:
		RepositoryItem repItem = Mock()
		BBBCommerceItem cItem  = Mock()
		orderMock.getMovedCommerceItem() >> cItem
		GiftListVO giftListVO = new GiftListVO()
		List list = [giftListVO]
		1*sItemSessionMock.getGiftListVO() >> list
		giftListVO.setWishListItemId("asas")
		giftListVO.setQuantity(0)
		profileMock.isTransient() >> true
		when:
		boolean valid  = cartObj.handleUndoCartItemMove(requestMock, responseMock)
		then:
		list.size() == 1
		valid
	}
	def "this method is used to undo item moved to cart for registered user throws repositopry exception"(){
		given:
		RepositoryItem repItem = Mock()
		BBBCommerceItem cItem  = Mock()
		orderMock.getMovedCommerceItem() >> cItem
		profileMock.getPropertyValue("wishlist") >> repItem
		repItem.getRepositoryId() >> "ws1234"
		1*glManagerMock.getGiftitem("ws1234") >> repItem
		1*glManagerMock.getGiftlistItemQuantityDesired("ws1234") >> 2l
		cartObj.setWishlistItemId("ws1234")
		1*glManagerMock.setGiftlistItemQuantityDesired("ws1234", _, 2) >> {throw new RepositoryException("Mock of Repository exception")}
		when:
		boolean valid  = cartObj.handleUndoCartItemMove(requestMock, responseMock)
		then:
		cartObj.getWishListId().equals("ws1234")
		0*glManagerMock.removeGiftlist(_, "ws1234")
		0*cManagerMock.addItemToOrder(orderMock, cItem)
		valid
	}
	def "handleMoveAllWishListItemsToOrder method to move all wishlist items to cart and remove them from wishlist"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		Transaction trans = Mock()
		trMock.getTransaction() >> trans
		WishListVO wListVO =new WishListVO()
		GiftListVO gListVO = new GiftListVO()
		gListVO.setProdID("pr1234")
		gListVO.setSkuID("sku1234")
		gListVO.setQuantity(1L)
		gListVO.setWishListItemId("w1234")
		wListVO.setWishListItems([gListVO,gListVO])
		1*wishLisManger.getWishListItems() >> wListVO
		cartObj.setSiteId("BedBathUS")
		SKUDetailVO skuDetailVO = new  SKUDetailVO()
		2*cToolsMock.getSKUDetails("BedBathUS", "sku1234",, false, true, true) >> skuDetailVO
		cartObj.validateInventory("BedBathUS", _, orderMock, requestMock, responseMock) >> {}
		orderMock.getCommerceItemCount() >> 0
		2*priceManager.getListPriceBySite(_, "pr1234", "sku1234") >> 1.2
		requestMock.getHeader(BBBCoreConstants.ORIGIN_OF_TRAFFIC) >> BBBCoreConstants.MOBILEWEB
		RepositoryItemMock itemMock = new RepositoryItemMock()
		profileMock.getPropertyValue("wishlist") >> itemMock
		itemMock.setRepositoryId("123456")
		cartObj.adddItemToOrderSuperCall(requestMock, responseMock)  >> {}
		when:
		cartObj.handleMoveAllWishListItemsToOrder(requestMock, responseMock)
		then:
		1*orderMock.setOriginOfOrder(BBBCoreConstants.CHANNEL_MOBILE_WEB)
		cartObj.getAddItemCount() == 2
		cartObj.getWishListItemIdsToRemove().size() ==2
		cartObj.getWishListItemIdsToRemove().containsAll(["w1234","w1234"])
		cartObj.getMoveAllItemFailureResult().isEmpty()
		cartObj.getWishListId().equals("123456")
		2*glManagerMock.removeItemFromGiftlist(_,_)
	}
	def "handleMoveAllWishListItemsToOrder method to move all wishlist items to cart and remove them from wishlist is null"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		Transaction trans = Mock()
		trMock.getTransaction() >> trans
		1*wishLisManger.getWishListItems() >> null
		cartObj.setSiteId("BedBathUS")
		cartObj.adddItemToOrderSuperCall(requestMock, responseMock)  >> {}
		when:
		cartObj.handleMoveAllWishListItemsToOrder(requestMock, responseMock)
		cartObj.setUserLocale(new Locale("EN"))
		then:
		0*orderMock.setOriginOfOrder(BBBCoreConstants.CHANNEL_MOBILE_WEB)
		cartObj.getAddItemCount() == 0
		cartObj.getWishListItemIdsToRemove() == null
		cartObj.getWishListId().equals("123456") == false
		0*glManagerMock.removeItemFromGiftlist(_,_)
	}
	def "handleMoveAllWishListItemsToOrder method to move all wishlist items to cart and remove them from gift lis is empty is null"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		Transaction trans = Mock()
		trMock.getTransaction() >> trans
		WishListVO wListVO =new WishListVO()
		1*wishLisManger.getWishListItems() >> wListVO
		cartObj.setSiteId("BedBathUS")
		cartObj.adddItemToOrderSuperCall(requestMock, responseMock)  >> {}
		when:
		cartObj.handleMoveAllWishListItemsToOrder(requestMock, responseMock)
		cartObj.setUserLocale(new Locale("EN"))
		then:
		0*orderMock.setOriginOfOrder(BBBCoreConstants.CHANNEL_MOBILE_WEB)
		cartObj.getAddItemCount() == 0
		cartObj.getWishListItemIdsToRemove() == null
		cartObj.getWishListId().equals("123456") == false
		0*glManagerMock.removeItemFromGiftlist(_,_)
	}
	def "handleMoveAllWishListItemsToOrder method to move all wishlist items to cart and remove them from wishlist exceptins after additemtoorder method"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		Transaction trans = Mock()
		trMock.getTransaction() >> trans
		cartObj.setSiteId("BedBathUS")
		cartObj.adddItemToOrderSuperCall(requestMock, responseMock)  >> {}
		cartObj.preMoveAllWishListItemsToOrder(requestMock, responseMock) >> 1
		cartObj.getFormError() >> true
		when:
		cartObj.handleMoveAllWishListItemsToOrder(requestMock, responseMock)
		cartObj.setUserLocale(new Locale("EN"))
		then:
		0*orderMock.setOriginOfOrder(BBBCoreConstants.CHANNEL_MOBILE_WEB)
		cartObj.getAddItemCount() == 0
		cartObj.getWishListItemIdsToRemove() == null
		cartObj.getWishListId().equals("123456") == false
		0*glManagerMock.removeItemFromGiftlist(_,_)
		0*wishLisManger.getWishListItems()
	}
	def "handleMoveAllWishListItemsToOrder method to move all wishlist items to cart and remove them from wishlist throw commerce exception and repository exception"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		Transaction trans = Mock()
		trMock.getTransaction() >> trans
		WishListVO wListVO =new WishListVO()
		GiftListVO gListVO = new GiftListVO()
		gListVO.setProdID("pr1234")
		gListVO.setSkuID("sku1234")
		gListVO.setQuantity(1L)
		gListVO.setWishListItemId("w1234")
		wListVO.setWishListItems([gListVO,gListVO])
		1*wishLisManger.getWishListItems() >> wListVO
		cartObj.setSiteId("BedBathUS")
		SKUDetailVO skuDetailVO = new  SKUDetailVO()
		2*cToolsMock.getSKUDetails("BedBathUS", "sku1234",, false, true, true) >> skuDetailVO
		cartObj.validateInventory("BedBathUS", _, orderMock, requestMock, responseMock) >> {}
		orderMock.getCommerceItemCount() >> 0
		2*priceManager.getListPriceBySite(_, "pr1234", "sku1234") >> 1.2
		requestMock.getHeader(BBBCoreConstants.ORIGIN_OF_TRAFFIC) >> BBBCoreConstants.MOBILEAPP
		RepositoryItemMock itemMock = new RepositoryItemMock()
		profileMock.getPropertyValue("wishlist") >> itemMock
		itemMock.setRepositoryId("123456")
		cartObj.adddItemToOrderSuperCall(requestMock, responseMock)  >> {}
		2*glManagerMock.removeItemFromGiftlist(_,_) >> {throw new CommerceException()} >> {throw new RepositoryException()}
		cartObj.getJsonResultString() >> "result"
		when:
		cartObj.handleMoveAllWishListItemsToOrder(requestMock, responseMock)
		then:
		1*orderMock.setOriginOfOrder(BBBCoreConstants.CHANNEL_MOBILE_APP)
		cartObj.getAddItemCount() == 2
		cartObj.getWishListItemIdsToRemove().size() ==2
		cartObj.getWishListItemIdsToRemove().containsAll(["w1234","w1234"])
		cartObj.getMoveAllItemFailureResult().isEmpty()
		cartObj.getWishListId().equals("123456")
	}
	def "handleMoveAllWishListItemsToOrder method to move all wishlist items to cart and remove them from wishlist making null in savesessionbean for guest profile"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		cartObj.setRepeatingRequestMonitor(null)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		Transaction trans = Mock()
		cartObj.ensureTransaction() >> trans
		WishListVO wListVO =new WishListVO()
		GiftListVO gListVO = new GiftListVO()
		gListVO.setProdID("pr1234")
		gListVO.setSkuID("sku1234")
		gListVO.setQuantity(1L)
		gListVO.setWishListItemId("w1234")
		wListVO.setWishListItems([gListVO])
		1*wishLisManger.getWishListItems() >> wListVO
		cartObj.setSiteId("BedBathUS")
		SKUDetailVO skuDetailVO = new  SKUDetailVO()
		1*cToolsMock.getSKUDetails("BedBathUS", "sku1234",, false, true, true) >> skuDetailVO
		cartObj.validateInventory("BedBathUS", _, orderMock, requestMock, responseMock) >> {}
		orderMock.getCommerceItemCount() >> 1
		1*priceManager.getListPriceBySite(_, "pr1234", "sku1234") >> 1.2
		requestMock.getHeader(BBBCoreConstants.ORIGIN_OF_TRAFFIC) >> BBBCoreConstants.MOBILEWEB
		RepositoryItemMock itemMock = new RepositoryItemMock()
		profileMock.getPropertyValue("wishlist") >> itemMock
		itemMock.setRepositoryId("123456")
		profileMock.isTransient() >> true
		cartObj.adddItemToOrderSuperCall(requestMock, responseMock)  >> {}
		5*cartObj.getFormError() >> false >> false >> false >> false >> true
		cartObj.setJsonResultString("result")
		cartObj.getUserLocale() >> new Locale("en")
		when:
		cartObj.handleMoveAllWishListItemsToOrder(requestMock, responseMock)
		then:
		0*orderMock.setOriginOfOrder(BBBCoreConstants.CHANNEL_MOBILE_WEB)
		cartObj.getAddItemCount() == 1
		cartObj.getWishListItemIdsToRemove().size() ==1
		cartObj.getWishListItemIdsToRemove().containsAll(["w1234"])
		cartObj.getMoveAllItemFailureResult().isEmpty()
		sItemSessionMock.getGiftListVO() == null
	}
	def "handleMoveAllWishListItemsToOrder method to move all wishlist items to cart and remove them from wishlist multiple request"(){
		given:
		reqMonitorMock.isUniqueRequestEntry(_)>> false
		when:
		boolean valid=cartObj.handleMoveAllWishListItemsToOrder(requestMock, responseMock)
		then:
		valid == false
		
	}
	def "opulateMoveAllWishListItemsInfo method validates SKU info Inventory List price sku details id is null"(){
		given:
		WishListVO wListVO =new WishListVO()
		GiftListVO gListVO = new GiftListVO()
		gListVO.setProdID("pr1234")
		gListVO.setSkuID("sku1234")
		gListVO.setQuantity(1L)
		gListVO.setWishListItemId("w1234")
		AddCommerceItemInfo[] newArray = new AddCommerceItemInfo[1]
		0*priceManager.getListPriceBySite(*_) >> 0.0
		1*errorMSGMock.getErrMsg("err_cart_invalidSkuId", "EN", null) >> "err_cart_invalidSkuId"
		when:
		int sucesscount = cartObj.populateMoveAllWishListItemsInfo([gListVO], newArray, requestMock, responseMock)
		then:
		sucesscount == 0
		cartObj.getWishListItemIdsToRemove().size() == 0
		cartObj.getMoveAllItemFailureResult().size() == 1
	}
	def "opulateMoveAllWishListItemsInfo method validates SKU info Inventory List price sku id is null"(){
		given:
		WishListVO wListVO =new WishListVO()
		GiftListVO gListVO = new GiftListVO()
		gListVO.setProdID("pr1234")
		gListVO.setSkuID(null)
		gListVO.setQuantity(1L)
		gListVO.setWishListItemId("w1234")
		AddCommerceItemInfo[] newArray = new AddCommerceItemInfo[1]
		0*priceManager.getListPriceBySite(*_) >> 0.0
		1*errorMSGMock.getErrMsg("err_cart_nullSkuId", "EN", null) >> "err_cart_nullSkuId"
		when:
		int sucesscount = cartObj.populateMoveAllWishListItemsInfo([gListVO], newArray, requestMock, responseMock)
		then:
		sucesscount == 0
		cartObj.getWishListItemIdsToRemove().size() == 0
		cartObj.getMoveAllItemFailureResult().size() == 1
	}
	def "opulateMoveAllWishListItemsInfo method validates SKU info Inventory List price throw new system exception"(){
		given:
		WishListVO wListVO =new WishListVO()
		GiftListVO gListVO = new GiftListVO()
		gListVO.setProdID("pr1234")
		gListVO.setSkuID("sku1234")
		gListVO.setQuantity(1L)
		gListVO.setWishListItemId("w1234")
		AddCommerceItemInfo[] newArray = new AddCommerceItemInfo[1]
		0*priceManager.getListPriceBySite(*_) >> 0.0
		1*cToolsMock.getSKUDetails(_, "sku1234", false, true, true) >> {throw new BBBSystemException("Mock of system exception")}
		when:
		int sucesscount = cartObj.populateMoveAllWishListItemsInfo([gListVO], newArray, requestMock, responseMock)
		then:
		sucesscount == 0
		cartObj.getWishListItemIdsToRemove().size() == 0
		cartObj.getMoveAllItemFailureResult().size() == 1
	}
	def "opulateMoveAllWishListItemsInfo method validates SKU info Inventory List price throw new Business exception"(){
		given:
		WishListVO wListVO =new WishListVO()
		GiftListVO gListVO = new GiftListVO()
		gListVO.setProdID("pr1234")
		gListVO.setSkuID("sku1234")
		gListVO.setQuantity(1L)
		gListVO.setWishListItemId("w1234")
		AddCommerceItemInfo[] newArray = new AddCommerceItemInfo[1]
		0*priceManager.getListPriceBySite(*_) >> 0.0
		cartObj.setFromPipelineFlag(true)
		1*cToolsMock.getSKUDetails(_, "sku1234", false, true, true) >> {throw new BBBBusinessException("Mock of system exception")}
		when:
		int sucesscount = cartObj.populateMoveAllWishListItemsInfo([gListVO], newArray, requestMock, responseMock)
		then:
		sucesscount == 0
		cartObj.getWishListItemIdsToRemove().size() == 0
		cartObj.getMoveAllItemFailureResult().size() == 1
	}
	def "opulateMoveAllWishListItemsInfo method validates SKU info Inventory List price form error in inventory call"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		WishListVO wListVO =new WishListVO()
		GiftListVO gListVO = new GiftListVO()
		gListVO.setProdID("pr1234")
		gListVO.setSkuID("sku1234")
		gListVO.setQuantity(1L)
		gListVO.setWishListItemId("w1234")
		AddCommerceItemInfo[] newArray = new AddCommerceItemInfo[1]
		SKUDetailVO skuDetailVO = new  SKUDetailVO()
		0*priceManager.getListPriceBySite(*_) >> 0.0
		1*cToolsMock.getSKUDetails(_, "sku1234", false, true, true) >> skuDetailVO
		cartObj.validateInventory(*_)>>{cartObj.getFormError() >> true >> false}
		when:
		int sucesscount = cartObj.populateMoveAllWishListItemsInfo([gListVO], newArray, requestMock, responseMock)
		then:
		sucesscount == 1
		cartObj.getWishListItemIdsToRemove().size() == 1
		cartObj.getMoveAllItemFailureResult().size() == 0
	}
	def "opulateMoveAllWishListItemsInfo method validates SKU info Inventory List price Price is not specified for the sku"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		WishListVO wListVO =new WishListVO()
		GiftListVO gListVO = new GiftListVO()
		gListVO.setProdID("pr1234")
		gListVO.setSkuID("sku1234")
		gListVO.setQuantity(1L)
		gListVO.setWishListItemId("w1234")
		AddCommerceItemInfo[] newArray = new AddCommerceItemInfo[1]
		SKUDetailVO skuDetailVO = new  SKUDetailVO()
		1*priceManager.getListPriceBySite(*_) >> 0.0
		1*cToolsMock.getSKUDetails(_, "sku1234", false, true, true) >> skuDetailVO
		cartObj.validateInventory(*_)>>{}
		when:
		int sucesscount = cartObj.populateMoveAllWishListItemsInfo([gListVO], newArray, requestMock, responseMock)
		then:
		sucesscount == 0
		cartObj.getWishListItemIdsToRemove().size() == 0
		cartObj.getMoveAllItemFailureResult().size() == 1
		cartObj.getMoveAllItemFailureResult().get("sku1234").equals(BBBCoreErrorConstants.ERR_SKU_PRICE_NOT_FOUND+"Price is not specified for the sku")
	}
	def "opulateMoveAllWishListItemsInfo method validates SKU info Inventory List price Price is not specified for the sku throw  System Exception"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		WishListVO wListVO =new WishListVO()
		GiftListVO gListVO = new GiftListVO()
		gListVO.setProdID("pr1234")
		gListVO.setSkuID("sku1234")
		gListVO.setQuantity(1L)
		gListVO.setWishListItemId("w1234")
		AddCommerceItemInfo[] newArray = new AddCommerceItemInfo[1]
		SKUDetailVO skuDetailVO = new  SKUDetailVO()
		1*priceManager.getListPriceBySite(*_) >> {throw new BBBSystemException("Mock of System Exception")}
		1*cToolsMock.getSKUDetails(_, "sku1234", false, true, true) >> skuDetailVO
		cartObj.validateInventory(*_)>>{}
		when:
		int sucesscount = cartObj.populateMoveAllWishListItemsInfo([gListVO], newArray, requestMock, responseMock)
		then:
		sucesscount == 0
		cartObj.getWishListItemIdsToRemove().size() == 0
		cartObj.getMoveAllItemFailureResult().size() == 1
		cartObj.getMoveAllItemFailureResult().get("sku1234").equals(BBBCoreErrorConstants.ERR_SKU_PRICE_NOT_FOUND+"Price is not specified for the sku")
	}
	def "This method is to validate inventory of sku id"(){
		given:
		AddCommerceItemInfo item = Mock()
		1*ppHelper.getRollupQuantities(*_) >> 1
		Dictionary<String, String> dic = new Hashtable<String, String>()
		dic.put("registryId","registryId")
		dic.put("storeId","st1234")
		item.getValue() >> dic
		item.getCatalogRefId() >>  "sku1234"
		1*ppHelper.checkCachedInventory(_, "sku1234", "st1234", orderMock, 1, BBBInventoryManager.ADD_ITEM_FROM_REG, _,BBBInventoryManager.AVAILABLE) >> 1
		
		when:
		cartObj.validateInventory("BedBathUS", item, orderMock, requestMock, responseMock)
		then:
		cartObj.getFormError() == true
	}
	def "This method is to validate inventory of sku id limited stock from not registry"(){
		given:
		AddCommerceItemInfo item = Mock()
		1*ppHelper.getRollupQuantities(*_) >> 1
		Dictionary<String, String> dic = new Hashtable<String, String>()
		dic.put("storeId","st1234")
		item.getValue() >> dic
		item.getCatalogRefId() >>  "sku1234"
		1*ppHelper.checkCachedInventory(_, "sku1234", "st1234", orderMock, 1, BBBInventoryManager.ADD_ITEM, _,BBBInventoryManager.AVAILABLE) >> 2
		1*siContainer.getStoreIdInventoryMap() >> [:]
		when:
		cartObj.validateInventory("BedBathUS", item, orderMock, requestMock, responseMock)
		then:
		cartObj.getFormError() == false
		0*cacheMock.put("st1234" + BBBCoreConstants.HYPHEN + "sku1234", 1 , BBBCoreConstants.CACHE_STORE_INV)
	}
	def "This method is to validate inventory of sku id limited stock from not registry StoreIdInventoryMap i not empty"(){
		given:
		AddCommerceItemInfo item = Mock()
		1*ppHelper.getRollupQuantities(*_) >> 1
		Dictionary<String, String> dic = new Hashtable<String, String>()
		dic.put("storeId","st1234")
		item.getValue() >> dic
		item.getCatalogRefId() >>  "sku1234"
		1*ppHelper.checkCachedInventory(_, "sku1234", "st1234", orderMock, 1, BBBInventoryManager.ADD_ITEM, _,BBBInventoryManager.AVAILABLE) >> 2
		2*siContainer.getStoreIdInventoryMap() >> ["st1234|sku1234":2]
		when:
		cartObj.validateInventory("BedBathUS", item, orderMock, requestMock, responseMock)
		then:
		cartObj.getFormError() == false
		1*cacheMock.put("st1234-sku1234", 1 , BBBCoreConstants.CACHE_STORE_INV)
	}
	def "This method is to validate inventory of sku id out of stock from not registry"(){
		given:
		AddCommerceItemInfo item = Mock()
		1*ppHelper.getRollupQuantities(*_) >> 1
		Dictionary<String, String> dic = new Hashtable<String, String>()
		dic.put("storeId","st1234")
		item.getValue() >> dic
		item.getCatalogRefId() >>  "sku1234"
		1*ppHelper.checkCachedInventory(_, "sku1234", "st1234", orderMock, 1, BBBInventoryManager.ADD_ITEM, _,BBBInventoryManager.AVAILABLE) >> {throw new CommerceException()}
		when:
		cartObj.validateInventory("BedBathUS", item, orderMock, requestMock, responseMock)
		then:
		cartObj.getFormError() == false
	}
	def "This method is to validate inventory of sku id request from mobile app"(){
		given:
		AddCommerceItemInfo item = Mock()
		1*ppHelper.getRollupQuantities(*_) >> 1
		Dictionary<String, String> dic = new Hashtable<String, String>()
		dic.put("storeId","st1234")
		item.getValue() >> dic
		item.getCatalogRefId() >>  "sku1234"
		1*ppHelper.checkCachedInventory(_, "sku1234", "st1234", orderMock, 1, BBBInventoryManager.ADD_ITEM, _,BBBInventoryManager.AVAILABLE) >> 1
		requestMock.getHeader(BBBCoreConstants.CHANNEL)>>BBBCoreConstants.MOBILEWEB
		when:
		cartObj.validateInventory("BedBathUS", item, orderMock, requestMock, responseMock)
		then:
		cartObj.getFormError() == true
	}
	def "This method is to validate inventory of sku id request from mobile app and pipeline is true"(){
		given:
		AddCommerceItemInfo item = Mock()
		1*ppHelper.getRollupQuantities(*_) >> 1
		Dictionary<String, String> dic = new Hashtable<String, String>()
		dic.put("storeId","st1234")
		item.getValue() >> dic
		item.getCatalogRefId() >>  "sku1234"
		1*ppHelper.checkCachedInventory(_, "sku1234", "st1234", orderMock, 1, BBBInventoryManager.ADD_ITEM, _,BBBInventoryManager.AVAILABLE) >> 1
		cartObj.setFromPipelineFlag(true)
		requestMock.getHeader(BBBCoreConstants.CHANNEL)>>BBBCoreConstants.MOBILEWEB
		when:
		cartObj.validateInventory("BedBathUS", item, orderMock, requestMock, responseMock)
		then:
		cartObj.getFormError() == false
	}
	def "Wrapper method for Rest for express Checkout"(){
		given:
		inManagerMock.checkUncachedInventory(orderMock)
		BBBPayPalSessionBean paypalBean = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalBean)
		1*paypalManager.isTokenExpired(paypalBean, orderMock) >> true
		orderMock.isPayPalOrder() >> true
		orderMock.getToken() >> "tk1234"
		orderMock.getAvailabilityMap() >> ["availble":0]
		ShipMethodVO shipVO =new ShipMethodVO()
		ShipMethodVO shipVO1 =new ShipMethodVO()
		shipVO.setShipMethodId("sg12345")
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		CommerceItem cItem2 = Mock()
		cItem1.getCatalogRefId() >> "sku1234"
		cItem1.isLtlItem() >> true
		orderMock.getCommerceItems() >> [cItem2,cItem,cItem1]
		1*cToolsMock.getLTLEligibleShippingMethods("sku1234",_, _) >> [shipVO1,shipVO]
		1*pState.spcEligible(orderMock, _)>>true
		orderMock.getShippingGroupCount() >> 1
		when:
		boolean valid = cartObj.handleCheckoutCart(requestMock, responseMock)
		then:
		valid
		1*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		1*orderManagerMock.updateOrder(orderMock)
		1*orderMock.setExpressCheckOut(false)	
		1*cItem1.setHighestshipMethod("sg12345")
		bSessionbean.isSinglePageCheckout() == true
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_CHECKOUT_SINGLE.toString())
		}
	def "Wrapper method for Rest for express Checkout order token id"(){
		given:
		inManagerMock.checkUncachedInventory(orderMock)
		BBBPayPalSessionBean paypalBean = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalBean)
		1*paypalManager.isTokenExpired(paypalBean, orderMock) >> true
		orderMock.isPayPalOrder() >> true
		orderMock.getToken() >> null
		orderMock.getAvailabilityMap() >> ["availble":0]
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		CommerceItem cItem2 = Mock()
		cItem1.getCatalogRefId() >> "sku1234"
		cItem1.isLtlItem() >> true
		orderMock.getCommerceItems() >> [cItem2,cItem,cItem1]
		1*cToolsMock.getLTLEligibleShippingMethods("sku1234",_, _) >> []
		orderMock.getShippingGroupCount() >> 1
		1*pState.spcEligible(orderMock, false) >> false
		when:
		boolean valid = cartObj.handleCheckoutCart(requestMock, responseMock)
		then:
		valid
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		1*orderManagerMock.updateOrder(orderMock)
		1*orderMock.setExpressCheckOut(false)
		0*cItem1.setHighestshipMethod("sg12345")
		bSessionbean.isSinglePageCheckout() == false
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SHIPPING_SINGLE.toString())
		}
	def "Wrapper method for Rest for express Checkout order token id for guest customer"(){
		given:
		pState.getFailureURL() >> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
		inManagerMock.checkUncachedInventory(orderMock)
		BBBPayPalSessionBean paypalBean = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalBean)
		1*paypalManager.isTokenExpired(paypalBean, orderMock) >> false
		orderMock.isPayPalOrder() >> false
		orderMock.getToken() >> null
		orderMock.getAvailabilityMap() >> ["availble":0]
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		CommerceItem cItem2 = Mock()
		cItem1.getCatalogRefId() >> "sku1234"
		cItem1.isLtlItem() >> true
		orderMock.getCommerceItems() >> null
		0*cToolsMock.getLTLEligibleShippingMethods("sku1234",_, _) >> []
		orderMock.getShippingGroupCount() >> 1
		1*pState.spcEligible(orderMock, _)>> false
		profileMock.isTransient() >> true
		1*orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException()}
		when:
		boolean valid = cartObj.handleCheckoutCart(requestMock, responseMock)
		then:
		valid
		1*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		1*orderMock.setExpressCheckOut(false)
		0*cItem1.setHighestshipMethod("sg12345")
		bSessionbean.isSinglePageCheckout() == false
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.GUEST.toString())
		}
	def "Wrapper method for Rest for express Checkout order token id for guest customer with multi shipping"(){
		given:
		pState.getFailureURL() >> "at_rest"
		inManagerMock.checkUncachedInventory(orderMock)
		BBBPayPalSessionBean paypalBean = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalBean)
		1*paypalManager.isTokenExpired(paypalBean, orderMock) >> false
		orderMock.isPayPalOrder() >> false
		orderMock.getToken() >> null
		orderMock.getAvailabilityMap() >> ["availble":0]
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		CommerceItem cItem2 = Mock()
		cItem1.getCatalogRefId() >> "sku1234"
		cItem1.isLtlItem() >> true
		orderMock.getCommerceItems() >> null
		0*cToolsMock.getLTLEligibleShippingMethods("sku1234",_, _) >> []
		orderMock.getShippingGroupCount() >> 2
		1*pState.spcEligible(orderMock, _) >> false
		profileMock.isTransient() >> true
		1*orderManagerMock.updateOrder(orderMock) 
		sessionMock.getAttribute(BBBCoreConstants.RECGONIZED_GUEST_USER_FLOW) >> "true"
		when:
		boolean valid = cartObj.handleCheckoutCart(requestMock, responseMock)
		then:
		valid == false
		1*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		1*orderMock.setExpressCheckOut(false)
		0*cItem1.setHighestshipMethod("sg12345")
		bSessionbean.isSinglePageCheckout() == false
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SHIPPING_MULTIPLE.toString())
		}
	def"wrapper method for Rest for express Checkout"(){
		given:
		reqMonitorMock.isUniqueRequestEntry(_) >> false
		pState.getFailureURL() >> "at_rest"
		inManagerMock.checkUncachedInventory(orderMock)
		BBBPayPalSessionBean paypalBean = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalBean)
		1*paypalManager.isTokenExpired(paypalBean, orderMock) >> false
		orderMock.isPayPalOrder() >> false
		orderMock.getToken() >> null
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> null
		orderMock.getShippingGroupCount() >> 2
		profileMock.isTransient() >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.handleExpCheckout(requestMock, responseMock) >> true
		when:
		boolean valid = cartObj.handleExpressCheckoutForOrder(requestMock, responseMock)
		then:
		valid == false
		1*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		1*orderMock.setExpressCheckOut(true)
		bSessionbean.isSinglePageCheckout() == false
	}
	def"wrapper method for Rest for express Checkout ineventory not available"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		pState.getFailureURL() >> "at_rest"
		inManagerMock.checkUncachedInventory(orderMock)
		BBBPayPalSessionBean paypalBean = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalBean)
		1*paypalManager.isTokenExpired(paypalBean, orderMock) >> false
		orderMock.isPayPalOrder() >> false
		orderMock.getToken() >> null
		orderMock.getAvailabilityMap() >> ["availble":1]
		orderMock.getCommerceItems() >> null
		orderMock.getShippingGroupCount() >> 2
		profileMock.isTransient() >> true
		0*orderManagerMock.updateOrder(orderMock)
		cartObj.handleExpCheckout(requestMock, responseMock) >> true
		when:
		boolean valid = cartObj.handleExpressCheckoutForOrder(requestMock, responseMock)
		then:
		valid == false
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
		0*sGroupManager.removeEmptyShippingGroups(orderMock)
		0*orderMock.setExpressCheckOut(true)
		bSessionbean.isSinglePageCheckout() == false
	}
	def"wrapper method for Rest for express Checkout  paypal order"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		pState.getFailureURL() >> "at_rest"
		inManagerMock.checkUncachedInventory(orderMock)
		BBBPayPalSessionBean paypalBean = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalBean)
		1*paypalManager.isTokenExpired(paypalBean, orderMock) >> false
		orderMock.isPayPalOrder() >> true
		cartObj.handleCheckoutWithPaypal(requestMock, responseMock) >> true
		when:
		boolean valid = cartObj.handleExpressCheckoutForOrder(requestMock, responseMock)
		then:
		valid 
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
	}
	def"wrapper method for Rest for express Checkout  paypal order and singlr page checkout"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		pState.getFailureURL() >> "at_rest"
		inManagerMock.checkUncachedInventory(orderMock)
		BBBPayPalSessionBean paypalBean = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalBean)
		1*paypalManager.isTokenExpired(paypalBean, orderMock) >> false
		orderMock.isPayPalOrder() >> true
		cartObj.handleCheckoutSPWithPaypal(requestMock, responseMock) >> true
		bSessionbean.setSinglePageCheckout(true)
		when:
		boolean valid = cartObj.handleExpressCheckoutForOrder(requestMock, responseMock)
		then:
		valid
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
	}
	def"This method is used for handleExpCheckout"(){
		given:
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI 
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON)>>["true"]
		1*cManager.canItemShipToAddress(_, [], addressvo)>> true
		orderMock.getShippingGroups() >> [sShip,hShip]
		ShippingGroupCommerceItemRelationship shipRel = new ShippingGroupCommerceItemRelationship()
		ShippingGroupCommerceItemRelationship shipRel1 = new ShippingGroupCommerceItemRelationship()
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		cItem1.getCatalogRefId() >> "sku12345"
		shipRel.setCommerceItem(cItem)
		shipRel1.setCommerceItem(cItem1)
		hShip.getCommerceItemRelationships() >> [shipRel,shipRel1]
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku1234", _, _) >> false
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku12345", _, _) >> false
		1*cManager.canItemShipByMethod(_, _, "sd1234") >> true
		profileMock.getPropertyValue(BBBCoreConstants.PHONENUMBER)>>"123455667"
		cartObj.setRepriceOrderChainId("runRepricingProcess")
		cartObj.setSiteId("BedBathUS")
		cartObj.setSinglePageCheckout(true)
		orderMock.getShippingGroupCount() >> 1
		1*pState.spcEligible(orderMock, _) >> true
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		1*cManager.ensureShippingGroups(orderMock, _, profileMock)
		1*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		1*orderManagerMock.updateOrder(orderMock)
		bSessionbean.isSinglePageCheckout()
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"This method is used for handleExpCheckout site is TBS_BedBathUS"(){
		given:
		pState.getFailureURL() >> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON)>>["true"]
		1*cManager.canItemShipToAddress(_, [], addressvo)>> true
		orderMock.getShippingGroups() >> [sShip,hShip]
		ShippingGroupCommerceItemRelationship shipRel = new ShippingGroupCommerceItemRelationship()
		ShippingGroupCommerceItemRelationship shipRel1 = new ShippingGroupCommerceItemRelationship()
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		cItem1.getCatalogRefId() >> "sku12345"
		shipRel.setCommerceItem(cItem)
		shipRel1.setCommerceItem(cItem1)
		hShip.getCommerceItemRelationships() >> [shipRel,shipRel1]
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku1234", _, _) >> false
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku12345", _, _) >> false
		1*cManager.canItemShipByMethod(_, _, "sd1234") >> true
		profileMock.getPropertyValue(BBBCoreConstants.PHONENUMBER)>>"123455667"
		cartObj.setRepriceOrderChainId("runRepricingProcess")
		cartObj.setSiteId("TBS_BedBathUS")
		0*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SinglePageCheckoutOn") >> ["true"]
		0*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "SPC_consult_sitespect") >> ["true"]
		cartObj.setSinglePageCheckout(true)
		orderMock.getShippingGroupCount() >> 1
		0*cManager.orderContainsLTLItem(orderMock) >> false
		0*cManager.displaySingleShipping(orderMock, false) >> true
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		1*cManager.ensureShippingGroups(orderMock, _, profileMock)
		1*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		1*orderManagerMock.updateOrder(orderMock)
		bSessionbean.isSinglePageCheckout() == false
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString())
	}
	def"This method is used for handleExpCheckout site is BedBathCA"(){
		given:
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON)>>["true"]
		1*cManager.canItemShipToAddress(_, [], addressvo)>> true
		orderMock.getShippingGroups() >> [sShip,hShip]
		ShippingGroupCommerceItemRelationship shipRel = new ShippingGroupCommerceItemRelationship()
		ShippingGroupCommerceItemRelationship shipRel1 = new ShippingGroupCommerceItemRelationship()
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		cItem1.getCatalogRefId() >> "sku12345"
		shipRel.setCommerceItem(cItem)
		shipRel1.setCommerceItem(cItem1)
		hShip.getCommerceItemRelationships() >> [shipRel,shipRel1]
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku1234", _, _) >> false
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku12345", _, _) >> false
		1*cManager.canItemShipByMethod(_, _, "sd1234") >> true
		profileMock.getPropertyValue(BBBCoreConstants.PHONENUMBER)>>"123455667"
		cartObj.setRepriceOrderChainId("runRepricingProcess")
		cartObj.setSiteId("BedBathCA")
		cartObj.setSinglePageCheckout(true)
		orderMock.getShippingGroupCount() >> 1
		1*pState.spcEligible(orderMock, _) >> false
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		1*cManager.ensureShippingGroups(orderMock, _, profileMock)
		1*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		1*orderManagerMock.updateOrder(orderMock)
		bSessionbean.isSinglePageCheckout() == false
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString())
	}
	def"This method is used for handleExpCheckout throw exception when getting default address"(){
		given:
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> {throw new Exception()}
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		0*cManager.ensureShippingGroups(orderMock, _, profileMock)
		0*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		0*orderManagerMock.updateOrder(orderMock)
		bSessionbean.isSinglePageCheckout() == false
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"This method is used for handleExpCheckout throw exception when getting default shipping method"(){
		given:
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		1*cToolsMock.getDefaultShippingMethod(_) >> {throw new Exception()}
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		0*cManager.ensureShippingGroups(orderMock, _, profileMock)
		0*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		0*orderManagerMock.updateOrder(orderMock)
		bSessionbean.isSinglePageCheckout() == false
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"This method is used for handleExpCheckout throw exception beacuse of inventory not availalbe"(){
		given:
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":1]
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		0*cManager.ensureShippingGroups(orderMock, _, profileMock)
		0*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		0*orderManagerMock.updateOrder(orderMock)
		bSessionbean.isSinglePageCheckout() == false
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"This method is used for handleExpCheckout throw exception ship to po box "(){
		given:
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON)>>{throw new BBBBusinessException("Mock of Business exceptio")}
		1*cManager.canItemShipToAddress(_, [], addressvo)>> false
		orderMock.getShippingGroups() >> [sShip,hShip]
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		0*cManager.ensureShippingGroups(orderMock, _, profileMock)
		0*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		0*orderManagerMock.updateOrder(orderMock)
		bSessionbean.isSinglePageCheckout() == false
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"This method is used for handleExpCheckout throw exception ship to non po box "(){
		given:
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON)>>{throw new BBBSystemException("Mock of System exceptio")}
		1*cManager.canItemShipToAddress(_, [], addressvo)>> false
		orderMock.getShippingGroups() >> [sShip,hShip]
		addressvo.setIsNonPOBoxAddress(true)
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		0*cManager.ensureShippingGroups(orderMock, _, profileMock)
		0*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		0*orderManagerMock.updateOrder(orderMock)
		bSessionbean.isSinglePageCheckout() == false
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"This method is used for handleExpCheckout ship to po box ad form exception"(){
		given:
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON)>>["false"]
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		0*cManager.ensureShippingGroups(orderMock, _, profileMock)
		0*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		0*orderManagerMock.updateOrder(orderMock)
		bSessionbean.isSinglePageCheckout() == false
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"This method is used for handleExpCheckout throw exception order contain restricted sku"(){
		given:
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON)>>["true"]
		1*cManager.canItemShipToAddress(_, [], addressvo)>> true
		addressvo.setIsNonPOBoxAddress(true)
		orderMock.getShippingGroups() >> [sShip,hShip]
		ShippingGroupCommerceItemRelationship shipRel = new ShippingGroupCommerceItemRelationship()
		ShippingGroupCommerceItemRelationship shipRel1 = new ShippingGroupCommerceItemRelationship()
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		cItem1.getCatalogRefId() >> "sku12345"
		shipRel.setCommerceItem(cItem)
		shipRel1.setCommerceItem(cItem1)
		hShip.getCommerceItemRelationships() >> [shipRel,shipRel1]
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku1234", _, _) >> {throw new BBBBusinessException("Mock of business exception")}
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku12345", _, _) >> true
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		0*cManager.ensureShippingGroups(orderMock, _, profileMock)
		0*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		0*orderManagerMock.updateOrder(orderMock)
		bSessionbean.isSinglePageCheckout() == false
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"This method is used for handleExpCheckout throw exception item is not elgible for selected method"(){
		given:
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON)>>["true"]
		1*cManager.canItemShipToAddress(_, [], addressvo)>> true
		addressvo.setIsNonPOBoxAddress(true)
		orderMock.getShippingGroups() >> [sShip,hShip]
		ShippingGroupCommerceItemRelationship shipRel = new ShippingGroupCommerceItemRelationship()
		ShippingGroupCommerceItemRelationship shipRel1 = new ShippingGroupCommerceItemRelationship()
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		cItem1.getCatalogRefId() >> "sku12345"
		shipRel.setCommerceItem(cItem)
		shipRel1.setCommerceItem(cItem1)
		hShip.getCommerceItemRelationships() >> [shipRel,shipRel1]
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku1234", _, _) >> {throw new BBBBusinessException("Mock of business exception")}
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku12345", _, _) >> false
		1*cManager.canItemShipByMethod(_, [], "sd1234") >> false
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		0*cManager.ensureShippingGroups(orderMock, _, profileMock)
		0*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		0*orderManagerMock.updateOrder(orderMock)
		bSessionbean.isSinglePageCheckout() == false
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"This method is used for handleExpCheckout throw exception mobile and phone number in profile is null"(){
		given:
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON)>>["true"]
		1*cManager.canItemShipToAddress(_, [], addressvo)>> true
		addressvo.setIsNonPOBoxAddress(true)
		orderMock.getShippingGroups() >> [sShip,hShip]
		ShippingGroupCommerceItemRelationship shipRel = new ShippingGroupCommerceItemRelationship()
		ShippingGroupCommerceItemRelationship shipRel1 = new ShippingGroupCommerceItemRelationship()
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		cItem1.getCatalogRefId() >> "sku12345"
		shipRel.setCommerceItem(cItem)
		shipRel1.setCommerceItem(cItem1)
		hShip.getCommerceItemRelationships() >> [shipRel,shipRel1]
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku1234", _, _) >> {throw new BBBBusinessException("Mock of business exception")}
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku12345", _, _) >> false
		1*cManager.canItemShipByMethod(_, [], "sd1234") >> true
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		0*cManager.ensureShippingGroups(orderMock, _, profileMock)
		0*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		0*orderManagerMock.updateOrder(orderMock)
		bSessionbean.isSinglePageCheckout() == false
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"This method is used for handleExpCheckout throw exception in expresscheckout"(){
		given:
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON)>>["false"]
		addressvo.setAddress1("1076 parsons ave")
		1*cManager.canItemShipToAddress(_, [], addressvo)>> true
		addressvo.setIsNonPOBoxAddress(true)
		orderMock.getShippingGroups() >> [sShip,hShip]
		ShippingGroupCommerceItemRelationship shipRel = new ShippingGroupCommerceItemRelationship()
		ShippingGroupCommerceItemRelationship shipRel1 = new ShippingGroupCommerceItemRelationship()
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		cItem1.getCatalogRefId() >> "sku12345"
		shipRel.setCommerceItem(cItem)
		shipRel1.setCommerceItem(cItem1)
		hShip.getCommerceItemRelationships() >> [shipRel,shipRel1]
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku1234", _, _) >> false
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku12345", _, _) >> false
		1*cManager.canItemShipByMethod(_, [], "sd1234") >> true
		profileMock.getPropertyValue(BBBCoreConstants.PHONENUMBER)>>"123455667"
		profileMock.getPropertyValue(BBBCoreConstants.MOBILENUMBER)>>"123455667"
		1*cManager.ensureShippingGroups(orderMock, _, profileMock) >> {throw new Exception()}
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		0*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		0*orderManagerMock.updateOrder(orderMock)
		bSessionbean.isSinglePageCheckout() == false
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"This method is used for handleExpCheckout throw exception in expresscheckout for payment group"(){
		given:
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON)>>["false"]
		addressvo.setAddress1("1076 parsons ave")
		1*cManager.canItemShipToAddress(_, [], addressvo)>> true
		addressvo.setIsNonPOBoxAddress(true)
		orderMock.getShippingGroups() >> [sShip,hShip]
		ShippingGroupCommerceItemRelationship shipRel = new ShippingGroupCommerceItemRelationship()
		ShippingGroupCommerceItemRelationship shipRel1 = new ShippingGroupCommerceItemRelationship()
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		cItem1.getCatalogRefId() >> "sku12345"
		shipRel.setCommerceItem(cItem)
		shipRel1.setCommerceItem(cItem1)
		hShip.getCommerceItemRelationships() >> [shipRel,shipRel1]
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku1234", _, _) >> false
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku12345", _, _) >> false
		1*cManager.canItemShipByMethod(_, [], "sd1234") >> true
		profileMock.getPropertyValue(BBBCoreConstants.MOBILENUMBER)>>"123455667"
		1*cManager.ensurePaymentGroups(orderMock, _, profileMock) >> {throw new Exception()}
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		1*cManager.ensureShippingGroups(orderMock, _, profileMock)
		0*orderManagerMock.updateOrder(orderMock)
		bSessionbean.isSinglePageCheckout() == false
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"This method is used for handleExpCheckout form error after update order"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON)>>["false"]
		addressvo.setAddress1("1076 parsons ave")
		1*cManager.canItemShipToAddress(_, [], addressvo)>> true
		addressvo.setIsNonPOBoxAddress(true)
		orderMock.getShippingGroups() >> [sShip,hShip]
		ShippingGroupCommerceItemRelationship shipRel = new ShippingGroupCommerceItemRelationship()
		ShippingGroupCommerceItemRelationship shipRel1 = new ShippingGroupCommerceItemRelationship()
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		cItem1.getCatalogRefId() >> "sku12345"
		shipRel.setCommerceItem(cItem)
		shipRel1.setCommerceItem(cItem1)
		hShip.getCommerceItemRelationships() >> [shipRel,shipRel1]
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku1234", _, _) >> false
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku12345", _, _) >> false
		1*cManager.canItemShipByMethod(_, [], "sd1234") >> true
		profileMock.getPropertyValue(BBBCoreConstants.MOBILENUMBER)>>"123455667"
		1*orderManagerMock.updateOrder(orderMock) >> {cartObj.getFormError() >> true}
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		1*cManager.ensureShippingGroups(orderMock, _, profileMock)
		1*cManager.ensurePaymentGroups(orderMock, _, profileMock) 
		bSessionbean.isSinglePageCheckout() == false
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"This method is used for handleExpCheckout throw RunProcessException"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON)>>["false"]
		addressvo.setAddress1("1076 parsons ave")
		1*cManager.canItemShipToAddress(_, [], addressvo)>> true
		addressvo.setIsNonPOBoxAddress(true)
		orderMock.getShippingGroups() >> [sShip,hShip]
		ShippingGroupCommerceItemRelationship shipRel = new ShippingGroupCommerceItemRelationship()
		ShippingGroupCommerceItemRelationship shipRel1 = new ShippingGroupCommerceItemRelationship()
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		cItem1.getCatalogRefId() >> "sku12345"
		shipRel.setCommerceItem(cItem)
		shipRel1.setCommerceItem(cItem1)
		hShip.getCommerceItemRelationships() >> [shipRel,shipRel1]
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku1234", _, _) >> false
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku12345", _, _) >> false
		1*cManager.canItemShipByMethod(_, [], "sd1234") >> true
		profileMock.getPropertyValue(BBBCoreConstants.MOBILENUMBER)>>"123455667"
		1*orderManagerMock.updateOrder(orderMock) >> {throw new RunProcessException("Mock of RunProcessException")}
		1*errorMSGMock.getErrMsg("ERR_CART_GENERIC_ERROR_TRY_LATER","EN", null) >> "ERR_CART_GENERIC_ERROR_TRY_LATER"
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		1*cManager.ensureShippingGroups(orderMock, _, profileMock)
		1*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		bSessionbean.isSinglePageCheckout() == false
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"This method is used for handleExpCheckout throw CommerceException"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		cartObj.setRepeatingRequestMonitor(null)
		Transaction trans =Mock()
		cartObj.ensureTransaction() >> trans
		pState.getFailureURL() >> "atg"
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		reqMonitorMock.isUniqueRequestEntry(_) >> true
		cManager.getProfileAddressTool() >> addressAPI
		BBBAddressVO addressvo = new BBBAddressVO()
		1*addressAPI.getDefaultShippingAddress(profileMock,_) >> addressvo
		ShipMethodVO shipvo = new ShipMethodVO()
		shipvo.setShipMethodId("sd1234")
		1*cToolsMock.getDefaultShippingMethod(_) >> shipvo
		orderMock.getAvailabilityMap() >> ["availble":0]
		orderMock.getCommerceItems() >> []
		1*cToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.SHIPTO_POBOXON)>>["false"]
		addressvo.setAddress1("1076 parsons ave")
		1*cManager.canItemShipToAddress(_, [], addressvo)>> true
		addressvo.setIsNonPOBoxAddress(true)
		orderMock.getShippingGroups() >> [sShip,hShip]
		ShippingGroupCommerceItemRelationship shipRel = new ShippingGroupCommerceItemRelationship()
		ShippingGroupCommerceItemRelationship shipRel1 = new ShippingGroupCommerceItemRelationship()
		BBBCommerceItem cItem = Mock()
		BBBCommerceItem cItem1 = Mock()
		cItem.getCatalogRefId() >> "sku1234"
		cItem1.getCatalogRefId() >> "sku12345"
		shipRel.setCommerceItem(cItem)
		shipRel1.setCommerceItem(cItem1)
		hShip.getCommerceItemRelationships() >> [shipRel,shipRel1]
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku1234", _, _) >> {throw new BBBSystemException("Mock of BBBSystemException")}
		1*cToolsMock.isShippingZipCodeRestrictedForSku("sku12345", _, _) >> false
		1*cManager.canItemShipByMethod(_, [], "sd1234") >> true
		profileMock.getPropertyValue(BBBCoreConstants.MOBILENUMBER)>>"123455667"
		1*orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException("Mock of CommerceException")}
		1*errorMSGMock.getErrMsg("ERR_CART_GENERIC_ERROR_TRY_LATER","EN", null) >> "ERR_CART_GENERIC_ERROR_TRY_LATER"
		when:
		cartObj.handleExpCheckout(requestMock, responseMock)
		then:
		1*cManager.ensureShippingGroups(orderMock, _, profileMock)
		1*cManager.ensurePaymentGroups(orderMock, _, profileMock)
		bSessionbean.isSinglePageCheckout() == false
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SP_REVIEW.toString())
	}
	def"Wrapper method for Rest for ApplyCoupons"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		cartObj.handleApplyCoupons(requestMock, responseMock) >> true
		cartObj.setSinglePageCheckoutEnabled(false)
		cartObj.handleMoveToPayment(requestMock, responseMock) >> true
		cartObj.setCouponErrorList([:])
		when:
		boolean vaild = cartObj.handleApplyCouponsToOrder(requestMock, responseMock)
		then:
		vaild
	}
	def"Wrapper method for Rest for ApplyCoupons apply coupon false"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		cartObj.handleApplyCoupons(requestMock, responseMock) >> false
		cartObj.setSinglePageCheckoutEnabled(false)
		cartObj.handleMoveToPayment(requestMock, responseMock) >> true
		Map<String,String> map = ["Errorcode":"ErrorCodevalue","Errorcode1":"ErrorCodevalue1"]
		1*errorMSGMock.getErrMsg("ErrorCodevalue", _,	null, null) >> "ErrorCodevaluenew"
		1*errorMSGMock.getErrMsg("ErrorCodevalue1", _,	null, null) >> "ErrorCodevaluenew"
		cartObj.setCouponErrorList(map)
		requestMock.getLocale() >> new Locale("US_EN")
		when:
		boolean vaild = cartObj.handleApplyCouponsToOrder(requestMock, responseMock)
		then:
		vaild == false
		Map<String,String>  map1 = cartObj.getCouponErrorList()
		map1.get("Errorcode").equals("ErrorCodevaluenew")
		map1.get("Errorcode1").equals("ErrorCodevaluenew")
	}
	def"Wrapper method for Rest for ApplyCoupons  SPC is enabled"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		cartObj.handleApplyCoupons(requestMock, responseMock) >> true
		cartObj.setSinglePageCheckoutEnabled(true)
		cartObj.handleMoveToPayment(requestMock, responseMock) >> true
		cartObj.setCouponErrorList([:])
		when:
		boolean vaild = cartObj.handleApplyCouponsToOrder(requestMock, responseMock)
		then:
		vaild
	}
	def"This method is to apply coupons"(){
		given:
		cartObj.setCouponPage("COUPONS")
		orderMock.getSchoolCoupon() >> "school"
		cUtilMock.compareClaimCode("school", _) >> true
		requestMock.getParameter(BBBCoreConstants.CART) >> BBBCoreConstants.CART
		RepositoryItemMock itemMock = new RepositoryItemMock()
		RepositoryItemMock itemMock1 = new RepositoryItemMock()
		Map<String ,RepositoryItem> map= ["Code":itemMock,"schoolPromo":itemMock1]
		orderMock.getCouponMap() >> map
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >> [itemMock,itemMock1]
		CommerceItem item =Mock()
		BBBCommerceItem item1 =Mock()
		BBBCommerceItem item2 =Mock()
		NonMerchandiseCommerceItem item3 =Mock()
		item1.getStoreId() >> "12345"
		4*cToolsMock.isGiftCardItem(_, "sku1234") >> true >>false  >> true >>false
		1*cToolsMock.isSkuExcluded("sku1234", "Code", false) >> true
		item2.getCatalogRefId() >> "sku1234"  
		orderMock.getCommerceItems() >> [item1,item2,item3,item2,item]
		cartObj.setCouponClaimCode("schoolPromo")
		clManager.getClaimableTools() >>  clTools
		profileMock.getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) >> ["Code","schoolPromo"]
		when:
		cartObj.handleApplyCoupons(requestMock, responseMock)
		then:
		cartObj.getAppliedCouponMap().isEmpty()
		cartObj.getCouponClaimCode().equals("schoolPromo")
		1*pState.setCurrentLevel(DEFAULT_STATES.CART.toString())
		1*orderMock.setPropertyValue("schoolCoupon",null)
		2*pTools.checkPromotionGrant(*_)
		2*pTools.removePromotion(profileMock,_, false)
		1 * profileMock.setPropertyValue('availablePromotionsList',_)
		1 * clTools.getClaimableItem('schoolPromo')
		1*clTools.getClaimableItem("Code")
		1 * cToolsMock.isSkuExcluded(null, 'Code', false)
		
	}
	def"This method is to apply coupons throw business exception from while checking item qualifier falg"(){
		given:
		pState.getFailureURL() >> "atg"
		orderMock.getSchoolCoupon() >> "school"
		cUtilMock.compareClaimCode("school", _) >> true
		RepositoryItemMock itemMock = new RepositoryItemMock()
		RepositoryItemMock itemMock1 = new RepositoryItemMock()
		Map<String ,RepositoryItem> map= ["Code":itemMock,"schoolPromo":itemMock1]
		orderMock.getCouponMap() >> map
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >> [itemMock,itemMock1]
		CommerceItem item =Mock()
		BBBCommerceItem item1 =Mock()
		BBBCommerceItem item2 =Mock()
		NonMerchandiseCommerceItem item3 =Mock()
		item1.getStoreId() >> "12345"
		1*cToolsMock.isGiftCardItem(_, "sku1234") >>{throw new BBBBusinessException("Mock of bisuness exception")}
		0*cToolsMock.isSkuExcluded("sku1234", "Code", false) >> true
		item2.getCatalogRefId() >> "sku1234"
		orderMock.getCommerceItems() >> [item1,item2,item3,item2,item]
		cartObj.setCouponClaimCode("schoolPromo")
		clManager.getClaimableTools() >>  clTools
		profileMock.getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) >> ["Code","schoolPromo"]
		cartObj.setCouponList(["Errorcode":"ErrorCodevalue","Errorcode1":"Code"])
		requestMock.getLocale() >> new Locale("en_US")
		//requestMock.getParameter(BBBCoreConstants.CART) >> "cart"
		cToolsMock.isLogCouponDetails() >> true
		1*orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException("mock of commerce exception")}
		when:
		cartObj.handleApplyCoupons(requestMock, responseMock)
		then:
		cartObj.getAppliedCouponMap().isEmpty()
		cartObj.getCouponClaimCode().equals("schoolPromo")
		1*orderMock.setPropertyValue("schoolCoupon",null)
		0*pTools.checkPromotionGrant(*_)
		2*pTools.removePromotion(profileMock,_, false)
		1 * profileMock.setPropertyValue('availablePromotionsList',_)
		0 * clTools.getClaimableItem('schoolPromo')
		0*clTools.getClaimableItem("Code")
		0 * cToolsMock.isSkuExcluded(null, 'Code', false)
		1 * errorMSGMock.getErrMsg('ERR_CART_GENERIC_ERROR_TRY_LATER', 'EN', null)
	}
	def"This method is to apply coupons throw PromotionException exception from while checking item qualifier falg"(){
		given:
		requestMock.getParameter(BBBCoreConstants.CART) >> BBBCoreConstants.CART
		pState.getFailureURL() >> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
		cartObj.setCouponPage("SP_COUPONS")
		orderMock.getSchoolCoupon() >> "school"
		cUtilMock.compareClaimCode("school", _) >> true
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setProperties(["displayName":"displayName"])
		RepositoryItemMock itemMock1 = new RepositoryItemMock()
		Map<String ,RepositoryItem> map= ["Code":itemMock,"schoolPromo1":itemMock1]
		orderMock.getCouponMap() >> map
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >> [itemMock,itemMock1]
		CommerceItem item =Mock()
		BBBCommerceItem item1 =Mock()
		BBBCommerceItem item2 =Mock()
		NonMerchandiseCommerceItem item3 =Mock()
		item1.getStoreId() >> "12345"
		0*cToolsMock.isGiftCardItem(_, "sku1234")
		0*cToolsMock.isSkuExcluded("sku1234", "Code", false)
		item2.getCatalogRefId() >> "sku1234"
		orderMock.getCommerceItems() >> []
		cartObj.setCouponClaimCode("schoolPromo")
		clManager.getClaimableTools() >>  clTools
		profileMock.getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) >> []
		cartObj.setCouponList(["Errorcode":"schoolPromo","Errorcode1":"Code"])
		requestMock.getLocale() >> new Locale("en_US")
		//requestMock.getParameter(BBBCoreConstants.CART) >> "cart"
		cToolsMock.isLogCouponDetails() >> false
		1*cUtilMock.compareClaimCode(*_)>> true
		1*pTools.checkPromotionGrant(profileMock, _) >> true
		1*orderManagerMock.updateOrder(orderMock) >> {throw new RunProcessException("mock of RunProcess exception")}
		1 *  pTools.grantPromotion(profileMock, _, null, null, _) >> {throw new PromotionException("mock of Promotion exception")}
		when:
		cartObj.handleApplyCoupons(requestMock, responseMock)
		then:
		cartObj.getAppliedCouponMap().isEmpty() 
		cartObj.getCouponClaimCode().equals("schoolPromo")
		1*orderMock.setPropertyValue("schoolCoupon",null)
		1*orderMock.setPropertyValue("schoolCoupon","schoolPromo")
		2*pTools.removePromotion(profileMock,_, false)
		1 * profileMock.setPropertyValue('availablePromotionsList',_)
		0 * clTools.getClaimableItem('schoolPromo')
		1 * clTools.getClaimableItem("Code")
		0 * cToolsMock.isSkuExcluded(null, 'Code', false)
		2 * errorMSGMock.getErrMsg('ERR_CART_GENERIC_ERROR_TRY_LATER', 'EN', null)
		cartObj.getCouponErrorList().size() == 2
		cartObj.getCouponErrorList().get("error").equals("ERR_CART_GENERIC_ERROR_TRY_LATER")
		cartObj.getCouponErrorList().get("Code").equals("err_coupon_grant_error")
	}
	def"This method is to apply coupons throw System exception from while checking item qualifier falg"(){
		given:
		pState.getFailureURL() >> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
		cartObj.setCouponPage("COUPONasS")
		orderMock.getSchoolCoupon() >> "school"
		cUtilMock.compareClaimCode("school", _) >> true
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setProperties(["displayName":"displayName"])
		RepositoryItemMock itemMock1 = new RepositoryItemMock()
		Map<String ,RepositoryItem> map= ["Code":itemMock,"schoolPromo":itemMock1]
		orderMock.getCouponMap() >> map
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >> [itemMock,itemMock1]
		CommerceItem item =Mock()
		BBBCommerceItem item1 =Mock()
		BBBCommerceItem item2 =Mock()
		NonMerchandiseCommerceItem item3 =Mock()
		item1.getStoreId() >> "12345"
		3*cToolsMock.isGiftCardItem(_, "sku1234") >>true>> false>>{throw new BBBSystemException("Mock of System exception")}
		1*cToolsMock.isSkuExcluded("sku1234", "Code", false) >> true
		item2.getCatalogRefId() >> "sku1234"
		orderMock.getCommerceItems() >> [item1,item2,item3,item2,item]
		cartObj.setCouponClaimCode("schoolPromo")
		clManager.getClaimableTools() >>  clTools
		profileMock.getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) >> ["Code1","schoolPromo"]
		cartObj.setCouponList(["Errorcode":"schoolPromo","Errorcode1":"Code"])
		requestMock.getLocale() >> new Locale("en_US")
		//requestMock.getParameter(BBBCoreConstants.CART) >> "cart"
		cToolsMock.isLogCouponDetails() >> false
		1*cUtilMock.compareClaimCode(*_)>> true
		1*pTools.checkPromotionGrant(profileMock, _) >> true
		1*orderManagerMock.updateOrder(orderMock) >> {throw new RepositoryException("mock of Repository exception")}
		when:
		cartObj.handleApplyCoupons(requestMock, responseMock)
		then:
		cartObj.getAppliedCouponMap().size() ==1 
		cartObj.getAppliedCouponMap().get("Code").equals("displayName")
		cartObj.getCouponClaimCode().equals("schoolPromo")
		1*orderMock.setPropertyValue("schoolCoupon",null)
		2*pTools.removePromotion(profileMock,_, false)
		1 * profileMock.setPropertyValue('availablePromotionsList',_)
		0 * clTools.getClaimableItem('schoolPromo')
		1 * clTools.getClaimableItem("Code")
		1 * cToolsMock.isSkuExcluded(null, 'Code', false)
		3 * errorMSGMock.getErrMsg('ERR_CART_GENERIC_ERROR_TRY_LATER', 'EN', null)
		1 *  pTools.grantPromotion(profileMock, _, null, null, _)
		cartObj.getCouponErrorList().size() == 3
		cartObj.getCouponErrorList().get("schoolPromo").equals("err_coupon_bopus_error")
		cartObj.getCouponErrorList().get("error").equals("ERR_CART_GENERIC_ERROR_TRY_LATER")
		cartObj.getCouponErrorList().get("Code").equals("err_coupon_grant_error")
		cartObj.getJsonCouponErrors().equals('[{"ccode":"Code","cerror":"null"},{"ccode":"schoolPromo","cerror":"null"}]')
	}
	def"This method is to apply coupons throw TransactionDemarcation on update order"(){
		given:
		requestMock.getParameter(BBBCoreConstants.CART) >> BBBCoreConstants.CART
		pState.getFailureURL() >> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
		cartObj.setCouponPage("COUPONasS")
		orderMock.getSchoolCoupon() >> "school"
		cUtilMock.compareClaimCode("school", _) >> true
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setProperties(["displayName":"displayName"])
		RepositoryItemMock itemMock1 = new RepositoryItemMock()
		Map<String ,RepositoryItem> map= ["Code":itemMock,"schoolPromo":itemMock1]
		orderMock.getCouponMap() >> map
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >> [itemMock,itemMock1]
		BBBCommerceItem item2 =Mock()
		item2.getStoreId() >> "12345"
		item2.getCatalogRefId() >> "sku1234"
		orderMock.getCommerceItems() >> [item2]
		cartObj.setCouponClaimCode("schoolPromo")
		clManager.getClaimableTools() >>  clTools
		profileMock.getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) >> ["Code1","schoolPromo"]
		cartObj.setCouponList(["Errorcode":"schoolPromo","Errorcode1":"Code"])
		requestMock.getLocale() >> new Locale("en_US")
		//requestMock.getParameter(BBBCoreConstants.CART) >> "cart"
		cToolsMock.isLogCouponDetails() >> false
		1*cUtilMock.compareClaimCode(*_)>> true
		0*pTools.checkPromotionGrant(profileMock, _) >> true
		1*orderManagerMock.updateOrder(orderMock) >> {throw new TransactionDemarcationException("mock of TransactionDemarcation exception")}
		when:
		cartObj.handleApplyCoupons(requestMock, responseMock)
		then:
		cartObj.getAppliedCouponMap().size() ==0
		cartObj.getCouponClaimCode().equals("schoolPromo")
		1*orderMock.setPropertyValue("schoolCoupon",null)
		2*pTools.removePromotion(profileMock,_, false)
		1 * profileMock.setPropertyValue('availablePromotionsList',_)
		0 * clTools.getClaimableItem('schoolPromo')
		0 * clTools.getClaimableItem("Code")
		0 * cToolsMock.isSkuExcluded(null, 'Code', false)
		2 * errorMSGMock.getErrMsg('ERR_CART_GENERIC_ERROR_TRY_LATER', 'EN', null)
		0 *  pTools.grantPromotion(profileMock, _, null, null, _)
		cartObj.getCouponErrorList().size() == 3
		cartObj.getCouponErrorList().get("schoolPromo").equals("err_coupon_bopus_error")
		cartObj.getCouponErrorList().get("error").equals("ERR_PROMOTION_ON_APPLY")
		cartObj.getCouponErrorList().get("Code").equals("err_coupon_bopus_error")
		cartObj.getJsonCouponErrors().equals('[{"ccode":"Code","cerror":"null"},{"ccode":"schoolPromo","cerror":"null"}]')
	}
	def"This method is to apply coupons form error after pre method"(){
		given:
		pState.getFailureURL() >> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
		cartObj.setCouponPage("COUPONasS")
		orderMock.getSchoolCoupon() >>null
		cartObj.setCouponList(["Errorcode":"schoolPromo","Errorcode1":"Code"])
		when:
		cartObj.handleApplyCoupons(requestMock, responseMock)
		then:
		2*errorMSGMock.getErrMsg("err_schoolPromoCode_BLANK","EN", null)
		}
	def"This method is to apply coupons form error after pre method coupon unmatch"(){
		given:
		pState.getFailureURL() >> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
		cartObj.setCouponPage("COUPONasS")
		cartObj.setCouponClaimCode("schoolPromotions")
		cartObj.setCouponList(["Errorcode":"schoolPromo","Errorcode1":"Code"])
		cartObj.setTransactionManager(null)
		1*cUtilMock.compareClaimCode("schoolPromotions", _) >> false
		when:
		cartObj.handleApplyCoupons(requestMock, responseMock)
		then:
		2*errorMSGMock.getErrMsg("err_schoolPromoCode_UNMATCH","EN", null)
		}
	def"This method is to apply coupons ,coupon not avialbe in order map"(){
		given:
		requestMock.getParameter(BBBCoreConstants.CART) >> BBBCoreConstants.CART
		pState.getFailureURL() >> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
		cartObj.setCouponPage("SP_COUPONS")
		orderMock.getSchoolCoupon() >> "school"
		cUtilMock.compareClaimCode("school", _) >> true
		RepositoryItemMock itemMock = new RepositoryItemMock()
		itemMock.setProperties(["displayName":"displayName"])
		RepositoryItemMock itemMock1 = new RepositoryItemMock()
		Map<String ,RepositoryItem> map= ["Code1":null,"schoolPromo":itemMock1]
		orderMock.getCouponMap() >> map
		profileMock.getPropertyValue(BBBCoreConstants.AVAILABLE_PROMOTIONS_LIST) >> [itemMock,itemMock1]
		CommerceItem item =Mock()
		BBBCommerceItem item1 =Mock()
		BBBCommerceItem item2 =Mock()
		NonMerchandiseCommerceItem item3 =Mock()
		item1.getStoreId() >> "12345"
		item2.getCatalogRefId() >> "sku1234"
		orderMock.getCommerceItems() >> []
		cartObj.setCouponClaimCode("schoolPromo")
		clManager.getClaimableTools() >>  clTools
		profileMock.getPropertyValue(BBBCoreConstants.SELECTED_PROMOTIONS_LIST) >> []
		cartObj.setCouponList(["Errorcode":"schoolPromo","Errorcode1":"Code1"])
		requestMock.getLocale() >> new Locale("en_US")
		//requestMock.getParameter(BBBCoreConstants.CART) >> "cart"
		cToolsMock.isLogCouponDetails() >> false
		1*cUtilMock.compareClaimCode(*_)>> true
		1*pTools.checkPromotionGrant(profileMock, _) >> false
		1*orderManagerMock.updateOrder(orderMock) 
		0 *  pTools.grantPromotion(profileMock, _, null, null, _)
		when:
		cartObj.handleApplyCoupons(requestMock, responseMock)
		then:
		cartObj.getAppliedCouponMap().isEmpty() 
		cartObj.getCouponClaimCode().equals("schoolPromo")
		1*orderMock.setPropertyValue("schoolCoupon",null)
		1*orderMock.setPropertyValue("schoolCoupon","schoolPromo")
		2*pTools.removePromotion(profileMock,_, false)
		1 * profileMock.setPropertyValue('availablePromotionsList',_)
		1 * clTools.getClaimableItem('schoolPromo')
		0 * clTools.getClaimableItem("Code")
		0 * cToolsMock.isSkuExcluded(null, 'Code', false)
		2 * errorMSGMock.getErrMsg('ERR_CART_GENERIC_ERROR_TRY_LATER', 'EN', null)
		cartObj.getCouponErrorList().size() == 3
		cartObj.getCouponErrorList().get("error").equals("ERR_PROMOTION_ON_APPLY")
		cartObj.getCouponErrorList().get("Code1").equals("err_coupon_grant_error")
		cartObj.getCouponErrorList().get("schoolPromo").equals("err_coupon_grant_error")
	}
	def"Just takes user to the next page in the checkout flow"(){
		given:
		requestMock.getParameter(BBBCoreConstants.CART) >> BBBCoreConstants.CART
		pState.getFailureURL() >> "atg"
		orderMock.isPayPalOrder() >> true
		BBBPayPalSessionBean paypalBean = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalBean)
		when:
		cartObj.handleMoveToPayment(requestMock, responseMock)
		then:
		1*orderManagerMock.updateOrder(orderMock)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString())
	}
	def"Just takes user to the next page in the checkout flow non paypal order"(){
		given:
		requestMock.getParameter(BBBCoreConstants.CART) >> BBBCoreConstants.CART
		pState.getFailureURL() >> null
		orderMock.isPayPalOrder() >> false
		BBBPayPalSessionBean paypalBean = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalBean)
		cartObj.setRepriceOrderChainId("RepriceOrderChainId")
		when:
		cartObj.handleMoveToPayment(requestMock, responseMock)
		then:
		1*orderManagerMock.updateOrder(orderMock)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.PAYMENT.toString())
	}
	def"Just takes user to the next page in the checkout flow non paypal order and having"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		cartObj.getFormError() >> true
		Transaction trans = Mock()
		cartObj.ensureTransaction() >> trans
		requestMock.getParameter(BBBCoreConstants.CART) >> BBBCoreConstants.CART
		pState.getFailureURL() >> null
		orderMock.isPayPalOrder() >> true
		BBBPayPalSessionBean paypalBean = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalBean)
		cartObj.setRepriceOrderChainId("RepriceOrderChainId")
		when:
		cartObj.handleMoveToPayment(requestMock, responseMock)
		then:
		1*orderManagerMock.updateOrder(orderMock)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.PAYMENT.toString())
	}
	def"Just takes user to the next page in the checkout flow from session bean"(){
		given:
		requestMock.getParameter(BBBCoreConstants.CART) >> BBBCoreConstants.CART
		pState.getFailureURL() >> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
		orderMock.isPayPalOrder() >> true
		BBBPayPalSessionBean paypalBean = new BBBPayPalSessionBean()
		paypalBean.setFromPayPalPreview(true)
		cartObj.setPayPalSessionBean(paypalBean)
		when:
		cartObj.handleMoveToPayment(requestMock, responseMock)
		then:
		1*orderManagerMock.updateOrder(orderMock)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.PAYMENT.toString())
	}
	def"Just takes user to the next page in the checkout flow throws commerce exception"(){
		given:
		1*orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException()}
		when:
		cartObj.handleMoveToPayment(requestMock, responseMock)
		then:
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.PAYMENT.toString())
		}
	def"Just takes user to the next page in the checkout flow throws RunProcessException exception"(){
		given:
		1*orderManagerMock.updateOrder(orderMock) >> {throw new RunProcessException("mock of RunProcessException")}
		when:
		cartObj.handleMoveToPayment(requestMock, responseMock)
		then:
		1*errorMSGMock.getErrMsg(cartObj.GENERIC_ERROR_TRY_LATER, "EN",null)
		}
	def"Wrapper method for Rest for handlecheckoutwithPaypal"(){
		given:
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 10
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.getPaymentGroups() >> [cc,gfCard,gfCard]
		orderMock.getToken() >> "tk1234"
		1*pgMAnager.removePaymentGroupFromOrder(orderMock, "gc12345") >> {throw new CommerceException("Mock of Commerce Exception")}
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException("Mock of CommerceException")}
		cartObj.setFromCart(true)
		1*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> "tk1234"
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION"]
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == false 
		1*pgMAnager.removePaymentGroupFromOrder(orderMock, "GC1234")
		1*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
		cartObj.getPaypalToken().equals("tk1234")
		cartObj.getMoveToPurchaseInfoSuccessURL().equals("paypalridreturltk1234")
		cartObj.getPayPalSucessURL().equals("paypalridreturltk1234")
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal gift card amount and order total are not equal"(){
		given:
		cartObj.setMobileCancelURL("MobileCancelURL")
		pState.getFailureURL()>> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEAPP
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 7
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> true
		orderMock.getPaymentGroups() >> [cc,gfCard,gfCard]
		orderMock.getToken() >> "tk1234"
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException("Mock of CommerceException")}
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorExists(false)
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == true
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString())
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal site id is bedbathus"(){
		given:
		cartObj.setMobileCancelURL(null)
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 7
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> true
		orderMock.getPaymentGroups() >> [cc,gfCard,gfCard]
		orderMock.getToken() >> "tk1234"
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		4*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null)>> BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == true
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SHIPPING_SINGLE.toString())
		paypalsession.getErrorList().size() == 1
		paypalsession.getErrorList().get(0).equals(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR)
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal site id is bedbathus error id is 10737"(){
		given:
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 7
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> true
		orderMock.getPaymentGroups() >> [cc,gfCard,gfCard]
		orderMock.getToken() >> "tk1234"
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(10737)
		errorStatue.setErrorExists(true)
		3*errorMSGMock.getErrMsg("err_cart_paypal_set_express_service", "EN", null)>> "err_cart_paypal_set_express_service"
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == false
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		paypalsession.getErrorList() == null
		cartObj.getErrorMap().size() == 1
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal site id is bedbathus call from desktop"(){
		given:
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 7
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> false
		orderMock.getPaymentGroups() >> [cc,gfCard,gfCard]
		orderMock.getToken() >> null
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		4*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null)>> BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		cartObj.setUserLocale(new Locale("en_US"))
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == false
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SHIPPING_SINGLE.toString())
		paypalsession.getErrorList().size() == 1
		paypalsession.getErrorList().get(0).equals(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR)
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal site id is bedbathus call from desktop failuer url is null"(){
		given:
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> null
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 7
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> false
		orderMock.getPaymentGroups() >> [cc,gfCard,gfCard]
		orderMock.getToken() >> null
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		4*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null)>> BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		cartObj.setUserLocale(new Locale("en_US"))
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == true
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SHIPPING_SINGLE.toString())
		paypalsession.getErrorList().size() == 1
		paypalsession.getErrorList().get(0).equals(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR)
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal throw business exception"(){
		given:
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> null
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 7
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> false
		orderMock.getPaymentGroups() >> [cc,gfCard,gfCard]
		orderMock.getToken() >> null
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> {throw new BBBBusinessException("mock of business exception")}
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		3*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null)>> BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		cartObj.setUserLocale(new Locale("en_US"))
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == false
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		paypalsession.getErrorList() == null
		cartObj.getErrorMap().size() == 1
		cartObj.getErrorMap().get(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE).equals(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE)
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal throw System exception"(){
		given:
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> null
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 7
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> false
		orderMock.getPaymentGroups() >> [cc,gfCard,gfCard]
		orderMock.getToken() >> null
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> {throw new BBBSystemException("mock of business exception")}
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		3*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null)>> BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		cartObj.setUserLocale(new Locale("en_US"))
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == false
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		paypalsession.getErrorList() == null
		cartObj.getErrorMap().size() == 1
		cartObj.getErrorMap().get(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE).equals(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE)
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal paypal response is null"(){
		given:
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> null
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 7
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> false
		orderMock.getPaymentGroups() >> [cc,gfCard,gfCard]
		orderMock.getToken() >> null
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> null
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		0*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null)>> BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		cartObj.setUserLocale(new Locale("en_US"))
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == true
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		paypalsession.getErrorList() == null
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal uncached inventory is available"(){
		given:
		cartObj.setRepeatingRequestMonitor(null)
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 10
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> false
		orderMock.getPaymentGroups() >> []
		orderMock.getToken() >> null
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(true)
		1*inManagerMock.checkUncachedInventory(orderMock) >>  true
		orderMock.isBopusOrder() >> false
		0*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		0*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		0*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null)>> BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		cartObj.setUserLocale(new Locale("en_US"))
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == false
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal  mutilple request"(){
		given:
		1*reqMonitorMock.isUniqueRequestEntry(_) >> false
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 10
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> false
		orderMock.getPaymentGroups() >> []
		orderMock.getToken() >> null
		0*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(true)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  true
		orderMock.isBopusOrder() >> false
		0*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		0*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		0*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null)>> BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		cartObj.setUserLocale(new Locale("en_US"))
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == true
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		0*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		0*sGroupManager.removeEmptyShippingGroups(orderMock)
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal token not expried"(){
		given:
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MOBILEWEB"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 10
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> false
		orderMock.getPaymentGroups() >> []
		orderMock.getToken() >> null
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(true)
		1*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> false
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == true
		paypalsession.getGetExpCheckoutResponse()== null
		paypalsession.isValidateOrderAddress()
		cartObj.isPayPalTokenNotExpired()
		2*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal token not expried from mobileApp "(){
		given:
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "mobileApp"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 10
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> true
		orderMock.getPaymentGroups() >> []
		orderMock.getToken() >> null
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(true)
		1*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> false
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == true
		paypalsession.getGetExpCheckoutResponse()== null
		paypalsession.isValidateOrderAddress() 
		cartObj.isPayPalTokenNotExpired()
		2*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal token not expried from Desktop "(){
		given:
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "Desktop"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 10
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "Desktop"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> true
		orderMock.getPaymentGroups() >> []
		orderMock.getToken() >> null
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(true)
		1*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> false
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		pState.getCheckoutSuccessURLs() >> ["REVIEW":"REVIEW","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == false
		paypalsession.getGetExpCheckoutResponse()== null
		paypalsession.isValidateOrderAddress()
		cartObj.isPayPalTokenNotExpired() == false
		cartObj.getPayPalSucessURL().equals("nullVALIDATION")
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal token not expried not form cart"(){
		given:
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 10
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> false
		orderMock.getPaymentGroups() >> []
		orderMock.getToken() >> null
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> false
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == true
		paypalsession.getGetExpCheckoutResponse()== null
		paypalsession.isValidateOrderAddress() == false
		cartObj.isPayPalTokenNotExpired()
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal token not expried not form cart and from desktop"(){
		given:
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "Desktop"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 10
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> false
		orderMock.getPaymentGroups() >> []
		orderMock.getToken() >> null
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> false
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		pState.getCheckoutFailureURLs() >> ["REVIEW":"REVIEW","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == false
		paypalsession.getGetExpCheckoutResponse()== null
		paypalsession.isValidateOrderAddress() == false
		cartObj.isPayPalTokenNotExpired() == false
		cartObj.getPayPalSucessURL().equals("nullREVIEW?token=null")
	}
	
	def"Wrapper method for Rest for handlecheckoutwithPaypal token not expried not form cart for mobileApp"(){
		given:
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 10
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> false
		orderMock.getPaymentGroups() >> []
		orderMock.getToken() >> null
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> false
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == true
		paypalsession.getGetExpCheckoutResponse()== null
		paypalsession.isValidateOrderAddress() == false
		cartObj.isPayPalTokenNotExpired()
	}
	def"Wrapper method for Rest for handlecheckoutwithPaypal order is bopus item"(){
		given:
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 10
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> false
		orderMock.getPaymentGroups() >> []
		orderMock.getToken() >> null
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> true
		0*paypalManager.isTokenExpired(_, orderMock) >> false
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		when:
		boolean status  =  cartObj.handleCheckoutWithPaypalRest(requestMock, responseMock)
		then:
		status == true
		paypalsession.getGetExpCheckoutResponse()== null
		paypalsession.isValidateOrderAddress() == false
		cartObj.isPayPalTokenNotExpired() == false
	}
	def"Wrapper method for Rest for handleCheckoutSPWithPaypal token not expried not form cart for mobileApp"(){
		given:
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
		orderMock.isPayPalOrder()>> false
		orderMock.getToken() >> null
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> false
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		when:
		boolean status  =  cartObj.handleCheckoutSPWithPaypal(requestMock, responseMock)
		then:
		status == true
		paypalsession.getGetExpCheckoutResponse()== null
		paypalsession.isValidateOrderAddress() == false
		cartObj.isPayPalTokenNotExpired()
	}
	def"Wrapper method for Rest for handleCheckoutSPWithPaypal order is bopus item"(){
		given:
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
		orderMock.isPayPalOrder()>> false
		orderMock.getToken() >> null
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> true
		0*paypalManager.isTokenExpired(_, orderMock) >> false
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		when:
		boolean status  =  cartObj.handleCheckoutSPWithPaypal(requestMock, responseMock)
		then:
		status == true
		paypalsession.getGetExpCheckoutResponse()== null
		paypalsession.isValidateOrderAddress() == false
		cartObj.isPayPalTokenNotExpired() == false
	}
	def"Wrapper method for Rest for handleCheckoutSPWithPaypal"(){
		given:
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB
		orderMock.getToken() >> "tk1234"
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException("Mock of CommerceException")}
		cartObj.setFromCart(true)
		1*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> "tk1234"
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION"]
		when:
		boolean status  =  cartObj.handleCheckoutSPWithPaypal(requestMock, responseMock)
		then:
		status == false
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
		cartObj.getPaypalToken().equals("tk1234")
		cartObj.getMoveToPurchaseInfoSuccessURL().equals("paypalridreturltk1234")
		cartObj.getPayPalSucessURL().equals("paypalridreturltk1234")
	}
	def"Wrapper method for Rest for handleCheckoutSPWithPaypal gift card amount and order total are not equal"(){
		given:
		cartObj.setMobileCancelURL("MobileCancelURL")
		pState.getFailureURL()>> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEAPP
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 7
		orderMock.isPayPalOrder()>> true
		orderMock.getToken() >> "tk1234"
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException("Mock of CommerceException")}
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorExists(false)
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		when:
		boolean status  =  cartObj.handleCheckoutSPWithPaypal(requestMock, responseMock)
		then:
		status == true
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
	}
	def"Wrapper method for Rest for handleCheckoutSPWithPaypal site id is bedbathus"(){
		given:
		cartObj.setMobileCancelURL(null)
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> BBBCoreConstants.ATG_REST_IGNORE_REDIRECT
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 7
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> true
		orderMock.getPaymentGroups() >> [cc,gfCard,gfCard]
		orderMock.getToken() >> "tk1234"
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		4*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null)>> BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		when:
		boolean status  =  cartObj.handleCheckoutSPWithPaypal(requestMock, responseMock)
		then:
		status == true
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		paypalsession.getErrorList().size() == 1
		paypalsession.getErrorList().get(0).equals(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR)
	}
	def"Wrapper method for Rest for handleCheckoutSPWithPaypal site id is bedbathus error id is 10737"(){
		given:
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		orderMock.isPayPalOrder()>> true
		orderMock.getToken() >> "tk1234"
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(10737)
		errorStatue.setErrorExists(true)
		3*errorMSGMock.getErrMsg("err_cart_paypal_set_express_service", "EN", null)>> "err_cart_paypal_set_express_service"
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		when:
		boolean status  =  cartObj.handleCheckoutSPWithPaypal(requestMock, responseMock)
		then:
		status == false
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		paypalsession.getErrorList() == null
		cartObj.getErrorMap().size() == 1
	}
	def"Wrapper method for Rest for handleCheckoutSPWithPaypal site id is bedbathus call from desktop"(){
		given:
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		orderMock.isPayPalOrder()>> false
		orderMock.getToken() >> null
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		4*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null)>> BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		cartObj.setUserLocale(new Locale("en_US"))
		when:
		boolean status  =  cartObj.handleCheckoutSPWithPaypal(requestMock, responseMock)
		then:
		status == false
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		paypalsession.getErrorList().size() == 1
		paypalsession.getErrorList().get(0).equals(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR)
	}
	def"Wrapper method for Rest for handleCheckoutSPWithPaypal site id is bedbathus call from desktop failuer url is null"(){
		given:
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> null
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		orderMock.isPayPalOrder()>> false
		orderMock.getToken() >> null
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		4*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null)>> BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		cartObj.setUserLocale(new Locale("en_US"))
		when:
		boolean status  =  cartObj.handleCheckoutSPWithPaypal(requestMock, responseMock)
		then:
		status == true
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		paypalsession.getErrorList().size() == 1
		paypalsession.getErrorList().get(0).equals(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR)
	}
	def"Wrapper method for Rest for handleCheckoutSPWithPaypal throw business exception"(){
		given:
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> null
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 7
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> false
		orderMock.getPaymentGroups() >> [cc,gfCard,gfCard]
		orderMock.getToken() >> null
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> {throw new BBBBusinessException("mock of business exception")}
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		3*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null)>> BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		cartObj.setUserLocale(new Locale("en_US"))
		when:
		boolean status  =  cartObj.handleCheckoutSPWithPaypal(requestMock, responseMock)
		then:
		status == false
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		paypalsession.getErrorList() == null
		cartObj.getErrorMap().size() == 1
		cartObj.getErrorMap().get(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE).equals(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE)
	}
	def"Wrapper method for Rest for handleCheckoutSPWithPaypal throw System exception"(){
		given:
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> null
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		pgMAnager.getGiftCardTotalAmount(orderMock) >> 7
		OrderPriceInfo oPriceInfo = Mock()
		oPriceInfo.getTotal() >> 10
		BBBGiftCard gfCard = Mock()
		orderMock.getPriceInfo() >> oPriceInfo
		gfCard.getId() >> "GC1234" >>"GC1234">> "gc12345">> "gc12345"
		CreditCard cc = Mock()
		orderMock.isPayPalOrder()>> false
		orderMock.getPaymentGroups() >> [cc,gfCard,gfCard]
		orderMock.getToken() >> null
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> {throw new BBBSystemException("mock of business exception")}
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		3*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null)>> BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		cartObj.setUserLocale(new Locale("en_US"))
		when:
		boolean status  =  cartObj.handleCheckoutSPWithPaypal(requestMock, responseMock)
		then:
		status == false
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		paypalsession.getErrorList() == null
		cartObj.getErrorMap().size() == 1
		cartObj.getErrorMap().get(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE).equals(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE)
	}
	def"Wrapper method for Rest for handleCheckoutSPWithPaypal paypal response is null"(){
		given:
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> null
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		orderMock.isPayPalOrder()>> false
		orderMock.getToken() >> null
		1*reqMonitorMock.isUniqueRequestEntry(_) >> true
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(false)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  false
		orderMock.isBopusOrder() >> false
		1*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		1*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> null
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		0*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE, "EN", null)>> BBBPayPalConstants.ERR_CART_PAYPAL_SET_EXPRESS_SERVICE
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		cartObj.setUserLocale(new Locale("en_US"))
		when:
		boolean status  =  cartObj.handleCheckoutSPWithPaypal(requestMock, responseMock)
		then:
		status == true
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		paypalsession.getErrorList() == null
	}
	def"Wrapper method for Rest for handleCheckoutSPWithPaypal uncached inventory is available"(){
		given:
		cartObj.setRepeatingRequestMonitor(null)
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "bedbathus"
		orderMock.isPayPalOrder()>> false
		orderMock.getToken() >> null
		1*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(true)
		1*inManagerMock.checkUncachedInventory(orderMock) >>  true
		orderMock.isBopusOrder() >> false
		0*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		0*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		0*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null)>> BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		cartObj.setUserLocale(new Locale("en_US"))
		when:
		boolean status  =  cartObj.handleCheckoutSPWithPaypal(requestMock, responseMock)
		then:
		status == false
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		1*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		1*sGroupManager.removeEmptyShippingGroups(orderMock)
		1*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
	}
	def"Wrapper method for Rest for handleCheckoutSPWithPaypal  mutilple request"(){
		given:
		1*reqMonitorMock.isUniqueRequestEntry(_) >> false
		cartObj.setMobileCancelURL("MobileCancelURL")
		cartObj.setMobileRedirectURL("MobileRedirectURL")
		pState.getFailureURL()>> "ATG_REST_IGNORE"
		orderMock.isPayPalOrder()>> false
		orderMock.getToken() >> null
		0*orderManagerMock.updateOrder(orderMock)
		cartObj.setFromCart(true)
		0*inManagerMock.checkUncachedInventory(orderMock) >>  true
		orderMock.isBopusOrder() >> false
		0*paypalManager.isTokenExpired(_, orderMock) >> true
		requestMock.getHeader(BBBCoreConstants.HOST) >> BBBCoreConstants.HOST
		requestMock.getScheme() >> "Scheme"
		BBBSetExpressCheckoutResVO resvo = Mock()
		0*paypalManager.doSetExpressCheckOut(orderMock, _, _, _, profileMock) >> resvo
		resvo.getToken() >> null
		ErrorStatus errorStatue = new ErrorStatus()
		resvo.getErrorStatus() >> errorStatue
		errorStatue.setErrorId(BBBPayPalConstants.PAYPAL_SHIPPING_ERROR_ID)
		errorStatue.setErrorExists(true)
		0*errorMSGMock.getErrMsg(BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR, "EN", null)>> BBBPayPalConstants.ERR_PAYPAL_SET_EXPRESS_SHIPPING_ERROR
		paypalManager.getPayPalRedirectURL() >> "paypalridreturl"
		pState.getCheckoutFailureURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","PAYMENT":"PAYMENT"]
		pState.getCheckoutSuccessURLs() >> ["CART":"CART","VALIDATION":"VALIDATION","INTERMEDIATE_PAYPAL":"INTERMEDIATE_PAYPAL"]
		BBBPayPalSessionBean paypalsession = new BBBPayPalSessionBean()
		cartObj.setPayPalSessionBean(paypalsession)
		cartObj.setUserLocale(new Locale("en_US"))
		when:
		boolean status  =  cartObj.handleCheckoutSPWithPaypal(requestMock, responseMock)
		then:
		status == true
		0*paypalManager.removePayPalPaymentGroup(orderMock, profileMock)
		0*sessionMock.removeAttribute(BBBVerifiedByVisaConstants.PAYMENT_PROGRESS)
		0*sGroupManager.removeEmptyShippingGroups(orderMock)
		0*pState.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString())
	}
	def"Method to associate registry context with cart for mobile"(){
		given:
		cartObj.setBuyOffAssociationSkuId("ci12346")
		cartObj.setRegistryId("reg1234")
		bSessionbean.getRegistrySummaryVO() >> null
		RegistrySummaryVO regVO = new RegistrySummaryVO()
		RegistryTypes rTypes = new RegistryTypes()
		regVO.setRegistryType(rTypes)
		rTypes.setRegistryTypeDesc("RegistryTypeDesc")
		regVO.setPrimaryRegistrantFirstName("PrimaryRegistrantFirstName")
		regVO.setPrimaryRegistrantLastName("PrimaryRegistrantLastName")
		regVO.setCoRegistrantFirstName("CoRegistrantFirstName")
		regVO.setCoRegistrantLastName("CoRegistrantFirstName")
		AddressVO avo =new AddressVO()
		regVO.setShippingAddress(avo)
		2*gfManager.getRegistryInfo("reg1234", _) >> regVO
		cToolsMock.getDefaultCountryForSite(_) >> "US"
		BBBCommerceItem item = Mock()
		BBBCommerceItem item1 = Mock()
		BBBCommerceItem item2 = Mock()
		orderMock.getCommerceItems() >> [item,item1,item1,item1,item1,item2]
		item.getCatalogRefId() >> "sk1234"
		item1.getCatalogRefId() >> "sk12345"
		item2.getCatalogRefId() >> "sk12345"
		item.getId() >> "ci1234"
		item1.getId() >> "ci12345"
		item2.getId() >> "ci12346"
		item.getQuantity() >> 1
		item1.getQuantity() >> 1
		item2.getQuantity() >> 1
		item2.getRegistryId() >> "reg1234"
		item1.getRegistryId() >> null >> "reg12345"
		item1.getStoreId() >> "1234" >> null
		item2.getStoreId() >>null>> "1234">> null
		orderMock.getCommerceItem("ci12346") >> item2
		item2.getReferenceNumber() >> "ref1234"
		item2.getFullImagePath() >> "FullImagePath"
		reqMonitorMock.isUniqueRequestEntry(_)>> true
		Map regMap = [:]
		orderMock.getRegistryMap() >> regMap
		cartObj.setBuyOffDuplicateItemFlag(false)
		when:
		boolean errorfalg = cartObj.handleAssociateContext(requestMock, responseMock)
		then:
		errorfalg
		avo.getCountry().equals("US")
		4*requestMock.setParameter("ci12345", 1)
		1*requestMock.setParameter("ci12346", 1)
		1*requestMock.setParameter("ci1234", 1)
		cartObj.setImageURL("FullImagePath")
		cartObj.isBuyOffDuplicateItemFlag()
		/*1*eximanager.setModerateImageURL(item2)
		item2.setBuyOffAssociatedItem(true)
		item2.setRegistryId("reg1234")
		item2.setRegistryInfo("asas")
		item2.setBuyOffPrimaryRegFirstName(regVO.getPrimaryRegistrantFirstName())
		item2.setBuyOffCoRegFirstName(regVO.getCoRegistrantFirstName())
		item2.setBuyOffRegistryEventType(regVO.getRegistryType().getRegistryTypeDesc())*/
		1*orderManagerMock.updateOrder(orderMock)
		regMap.containsKey("reg1234")
	}
	def"Method to associate registry context with cart for mobile ltlcommerce item"(){
		given:
		cartObj.setBuyOffAssociationSkuId("ci12346")
		cartObj.setRegistryId("reg1234")
		bSessionbean.getRegistrySummaryVO() >> null
		RegistrySummaryVO regVO = new RegistrySummaryVO()
		RegistryTypes rTypes = new RegistryTypes()
		regVO.setRegistryType(rTypes)
		rTypes.setRegistryTypeDesc("RegistryTypeDesc")
		regVO.setPrimaryRegistrantFirstName("PrimaryRegistrantFirstName")
		regVO.setPrimaryRegistrantLastName("PrimaryRegistrantLastName")
		regVO.setCoRegistrantFirstName("CoRegistrantFirstName")
		regVO.setCoRegistrantLastName("CoRegistrantFirstName")
		AddressVO avo =new AddressVO()
		regVO.setShippingAddress(avo)
		2*gfManager.getRegistryInfo("reg1234", _) >> regVO
		cToolsMock.getDefaultCountryForSite(_) >> "US"
		BBBCommerceItem item = Mock()
		BBBCommerceItem item1 = Mock()
		BBBCommerceItem item2 = Mock()
		orderMock.getCommerceItems() >> [item,item1,item1,item1,item1,item2]
		item.getCatalogRefId() >> "sk1234"
		item1.getCatalogRefId() >> "sk12346" >> "sk12345"
		item2.getCatalogRefId() >> "sk12345"
		item.getId() >> "ci1234"
		item1.getId() >> "ci12345"
		item2.getId() >> "ci12346"
		item.getQuantity() >> 1
		item1.getQuantity() >> 1
		item2.getQuantity() >> 1
		item2.getRegistryId() >> "reg1234"
		item1.getRegistryId() >> null >> "reg12345"
		item1.getStoreId() >> "1234" >> null
		item2.getStoreId() >>null>> "1234">> null
		orderMock.getCommerceItem("ci12346") >> item2
		item2.getReferenceNumber() >> "ref1234"
		item2.getFullImagePath() >> "FullImagePath"
		reqMonitorMock.isUniqueRequestEntry(_)>> true
		Map regMap = [:]
		orderMock.getRegistryMap() >> regMap
		cartObj.setBuyOffDuplicateItemFlag(false)
		item.isLtlItem() >> false
		item1.isLtlItem() >> true
		item2.isLtlItem() >> true
		item1.getLtlShipMethod() >> null >> "ltlship">> "checkShipMethod"
		item2.getLtlShipMethod() >> "checkShipMethod"
		ShipMethodVO shipVO = new ShipMethodVO()
		1*cToolsMock.getLTLEligibleShippingMethods(*_) >> [shipVO]
		shipVO.setShipMethodId("checkShipMethod")
		when:
		boolean errorfalg = cartObj.handleAssociateContext(requestMock, responseMock)
		then:
		errorfalg
		avo.getCountry().equals("US")
		4*requestMock.setParameter("ci12345", 1)
		1*requestMock.setParameter("ci12346", 1)
		1*requestMock.setParameter("ci1234", 1)
		cartObj.setImageURL("FullImagePath")
		1*orderManagerMock.updateOrder(orderMock)
		regMap.containsKey("reg1234")
	}
	
	def"Method to associate registry context with cart for mobile ltlcommerce item  "(){
		given:
		cartObj.setBuyOffAssociationSkuId("ci12346")
		cartObj.setRegistryId("reg1234")
		bSessionbean.getRegistrySummaryVO() >> null
		RegistrySummaryVO regVO = new RegistrySummaryVO()
		RegistryTypes rTypes = new RegistryTypes()
		regVO.setRegistryType(rTypes)
		rTypes.setRegistryTypeDesc("RegistryTypeDesc")
		regVO.setPrimaryRegistrantFirstName("PrimaryRegistrantFirstName")
		regVO.setPrimaryRegistrantLastName("PrimaryRegistrantLastName")
		regVO.setCoRegistrantFirstName("CoRegistrantFirstName")
		regVO.setCoRegistrantLastName("CoRegistrantFirstName")
		AddressVO avo =new AddressVO()
		regVO.setShippingAddress(avo)
		2*gfManager.getRegistryInfo("reg1234", _) >> regVO
		bSessionbean.getBuyoffStartBrowsingSummaryVO() >> regVO
		cToolsMock.getDefaultCountryForSite(_) >> "US"
		CommerceItem item = Mock()
		BBBCommerceItem item1 = Mock()
		BBBCommerceItem item2 = Mock()
		orderMock.getCommerceItems() >> [item,item1,item1,item1,item1,item2]
		item.getCatalogRefId() >> "sk1234"
		item1.getCatalogRefId() >> "sk12346" >> "sk12345"
		item2.getCatalogRefId() >> "sk12345"
		item.getId() >> "ci1234"
		item1.getId() >> "ci12345"
		item2.getId() >> "ci12346"
		item.getQuantity() >> 1
		item1.getQuantity() >> 1
		item2.getQuantity() >> 1
		item2.getRegistryId() >> "reg1234"
		item1.getRegistryId() >> null >> "reg12345"
		item1.getStoreId() >> "1234" >> null
		item2.getStoreId() >>null>> "1234">> null
		orderMock.getCommerceItem("ci12346") >> item2
		item2.getWhiteGloveAssembly() >> "true"
		item2.getReferenceNumber() >> "ref1234"
		item2.getFullImagePath() >> "FullImagePath"
		reqMonitorMock.isUniqueRequestEntry(_)>> true
		Map regMap = [:]
		orderMock.getRegistryMap() >> regMap
		cartObj.setBuyOffDuplicateItemFlag(false)
		item1.isLtlItem() >> true
		item2.isLtlItem() >> true
		item1.getLtlShipMethod() >> null >> "ltlship">> "checkShipMethod"
		item2.getLtlShipMethod() >> "checkShipMethod"
		ShipMethodVO shipVO = new ShipMethodVO()
		shipVO.setShipMethodId("LWA")
		1*cToolsMock.getLTLEligibleShippingMethods(*_) >> [shipVO]
		item2.getShippingGroupRelationships() >>[]
		item2.getShippingGroupRelationshipContainer() >> relContainer
		BBBShippingGroupCommerceItemRelationship shipGrpRel =Mock()
		relContainer.getShippingGroupRelationships() >> [shipGrpRel]
		shipGrpRel.getShippingGroup() >> hShip
		cManagerMock.addLTLAssemblyFeeSku(orderMock, hShip, _, item2) >> "asb1234"
		1 *	shipGrpMap.getDefaultShippingGroupName() >> "hardgoodShippingGroup"
		when:
		boolean errorfalg = cartObj.handleAssociateContext(requestMock, responseMock)
		then:
		errorfalg
		avo.getCountry().equals("US")
		4*requestMock.setParameter("ci12345", 1)
		1*requestMock.setParameter("ci12346", 1)
		cartObj.setImageURL("FullImagePath")
		1*orderManagerMock.updateOrder(orderMock)
		regMap.containsKey("reg1234")
		item2.setAssemblyItemId("asb1234")
		1*citemShipContainer.addCommerceItemShippingInfo("asb1234", _)
		hShip.setShippingMethod("LW")
		item2.setLtlShipMethod("LW")
	}
	
	def"Method to associate registry context with cart for mobile ltlcommerce item store id is not null "(){
		given:
		cartObj.setBuyOffAssociationSkuId("ci12346")
		cartObj.setRegistryId("reg1234")
		bSessionbean.getRegistrySummaryVO() >> null
		RegistrySummaryVO regVO = new RegistrySummaryVO()
		RegistryTypes rTypes = new RegistryTypes()
		regVO.setRegistryType(rTypes)
		rTypes.setRegistryTypeDesc("RegistryTypeDesc")
		regVO.setPrimaryRegistrantFirstName("PrimaryRegistrantFirstName")
		regVO.setPrimaryRegistrantLastName("PrimaryRegistrantLastName")
		regVO.setCoRegistrantFirstName("CoRegistrantFirstName")
		regVO.setCoRegistrantLastName("CoRegistrantFirstName")
		AddressVO avo =new AddressVO()
		regVO.setShippingAddress(avo)
		2*gfManager.getRegistryInfo("reg1234", _) >> regVO
		bSessionbean.getBuyoffStartBrowsingSummaryVO() >> regVO
		cToolsMock.getDefaultCountryForSite(_) >> "US"
		BBBCommerceItem item1 = Mock()
		BBBCommerceItem item2 = Mock()
		orderMock.getCommerceItems() >> [item1,item1,item1,item2]
		item1.getCatalogRefId() >> "sk12346" >> "sk12345"
		item2.getCatalogRefId() >> "sk12345"
		item1.getId() >> "ci12345"
		item2.getId() >> "ci12346"
		item1.getQuantity() >> 1
		item2.getQuantity() >> 1
		item2.getRegistryId() >> "reg1234"
		item1.getRegistryId() >>null>>  "reg12345"
		item1.getStoreId() >>null>> "12345" >> "1234" 
		item2.getStoreId() >> "1234"
		orderMock.getCommerceItem("ci12346") >> item2
		item2.getWhiteGloveAssembly() >> "true"
		item2.getReferenceNumber() >> "ref1234"
		item2.getFullImagePath() >> "FullImagePath"
		reqMonitorMock.isUniqueRequestEntry(_)>> true
		Map regMap = [:]
		orderMock.getRegistryMap() >> regMap
		cartObj.setBuyOffDuplicateItemFlag(false)
		item1.isLtlItem() >> false
		item2.isLtlItem() >> false
		item1.getLtlShipMethod() >> null >> "ltlship">> "checkShipMethod"
		item2.getLtlShipMethod() >> "checkShipMethod"
		ShipMethodVO shipVO = new ShipMethodVO()
		shipVO.setShipMethodId("LWA")
		0*cToolsMock.getLTLEligibleShippingMethods(*_) >> [shipVO]
		item2.getShippingGroupRelationships() >>[]
		item2.getShippingGroupRelationshipContainer() >> relContainer
		BBBShippingGroupCommerceItemRelationship shipGrpRel =Mock()
		relContainer.getShippingGroupRelationships() >> [shipGrpRel]
		shipGrpRel.getShippingGroup() >> hShip
		cManagerMock.addLTLAssemblyFeeSku(orderMock, hShip, _, item2) >> "asb1234"
		when:
		boolean errorfalg = cartObj.handleAssociateContext(requestMock, responseMock)
		then:
		errorfalg
		avo.getCountry().equals("US")
		3*requestMock.setParameter("ci12345", 1)
		1*requestMock.setParameter("ci12346", 1)
		cartObj.setImageURL("FullImagePath")
		1*orderManagerMock.updateOrder(orderMock)
		regMap.containsKey("reg1234")
		requestMock.setParameter("ci12346", 2)
		requestMock.setParameter("ci12346", 0)
		cartObj.isBuyOffDuplicateItemFlag()
	}
	def"Method to associate registry context with cart for mobile ltlcommerce item is whiteGloveAssembly "(){
		given:
		cartObj.setBuyOffAssociationSkuId("ci12346")
		cartObj.setRegistryId("reg1234")
		bSessionbean.getRegistrySummaryVO() >> null
		RegistrySummaryVO regVO = new RegistrySummaryVO()
		RegistryTypes rTypes = new RegistryTypes()
		regVO.setRegistryType(rTypes)
		rTypes.setRegistryTypeDesc("RegistryTypeDesc")
		regVO.setPrimaryRegistrantFirstName("PrimaryRegistrantFirstName")
		regVO.setPrimaryRegistrantLastName("PrimaryRegistrantLastName")
		regVO.setCoRegistrantFirstName("CoRegistrantFirstName")
		regVO.setCoRegistrantLastName("CoRegistrantFirstName")
		AddressVO avo =new AddressVO()
		regVO.setShippingAddress(avo)
		2*gfManager.getRegistryInfo("reg1234", _) >> regVO
		bSessionbean.getBuyoffStartBrowsingSummaryVO() >> regVO
		cToolsMock.getDefaultCountryForSite(_) >> "US"
		BBBCommerceItem item1 = Mock()
		BBBCommerceItem item2 = Mock()
		orderMock.getCommerceItems() >> [item1,item1,item1,item2]
		item1.getCatalogRefId() >> "sk12346" >> "sk12345"
		item2.getCatalogRefId() >> "sk12345"
		item1.getId() >> "ci12345"
		item2.getId() >> "ci12346"
		item1.getQuantity() >> 1
		item2.getQuantity() >> 1
		item2.getRegistryId() >> "reg1234"
		item1.getRegistryId() >>null>>"reg1234">> "reg12345"
		item1.getStoreId() >>null>> "12345" >> "1234"
		item2.getStoreId() >> "1234"
		orderMock.getCommerceItem("ci12346") >> item2
		item2.getWhiteGloveAssembly() >> "true"
		item2.getReferenceNumber() >> "ref1234"
		item2.getFullImagePath() >> "FullImagePath"
		reqMonitorMock.isUniqueRequestEntry(_)>> true
		Map regMap = [:]
		orderMock.getRegistryMap() >> regMap
		cartObj.setBuyOffDuplicateItemFlag(false)
		item1.isLtlItem() >> true
		item2.isLtlItem() >> true
		item1.getLtlShipMethod() >> "LW"
		item2.getLtlShipMethod() >> "LW"
		ShipMethodVO shipVO = new ShipMethodVO()
		shipVO.setShipMethodId("LWA")
		1*cToolsMock.getLTLEligibleShippingMethods(*_) >> [shipVO]
		item2.getShippingGroupRelationships() >>[]
		item2.getShippingGroupRelationshipContainer() >> relContainer
		BBBShippingGroupCommerceItemRelationship shipGrpRel =Mock()
		relContainer.getShippingGroupRelationships() >> [shipGrpRel]
		shipGrpRel.getShippingGroup() >> hShip
		cManagerMock.addLTLAssemblyFeeSku(orderMock, hShip, _, item2) >> "asb1234"
		item2.getWhiteGloveAssembly() >> "true"
		when:
		boolean errorfalg = cartObj.handleAssociateContext(requestMock, responseMock)
		then:
		errorfalg
		avo.getCountry().equals("US")
		3*requestMock.setParameter("ci12345", 1)
		1*requestMock.setParameter("ci12346", 1)
		cartObj.setImageURL("FullImagePath")
		1*orderManagerMock.updateOrder(orderMock)
		regMap.containsKey("reg1234")
		requestMock.setParameter("ci12346", 2)
		requestMock.setParameter("ci12346", 0)
		cartObj.isBuyOffDuplicateItemFlag()
	}
	
	def"Validates if the input RegistryId is valid or not "(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		1*gfManager.getRegistryInfo("rg1234", "BedBathUS") >> null
		when:
		cartObj.validateRegistryInfo("rg1234", "BedBathUS", requestMock, responseMock)
		then:
		cartObj.getFormError()
		1*cartObj.logError('cart_1016: registryId is invalid', null)
	}
	def"Validates if the input RegistryId is valid or not throw Business exception  "(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		1*gfManager.getRegistryInfo("rg1234", "BedBathUS") >> {throw new BBBBusinessException("Mock of business exception")}
		when:
		cartObj.validateRegistryInfo("rg1234", "BedBathUS", requestMock, responseMock)
		then:
		cartObj.getFormError()
		2*cartObj.logError('cart_1016: registryId is invalid', null)
	}
	def"Validates if the input RegistryId is valid or not throw System exception  "(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		1*gfManager.getRegistryInfo("rg1234", "BedBathUS") >> {throw new BBBSystemException("Mock of business exception")}
		when:
		cartObj.validateRegistryInfo("rg1234", "BedBathUS", requestMock, responseMock)
		then:
		cartObj.getFormError()
		2*cartObj.logError('cart_1016: registryId is invalid', null)
	}
	def"Validates if the input RegistryId is valid or not for register id is null"(){
		when:
		cartObj.validateRegistryInfo(null, "BedBathUS", requestMock, responseMock)
		then:
		cartObj.getFormError()
		1 * errorMSGMock.getErrMsg('err_cart_invalidRegistryId', 'EN', null)
	}
	def"Validates if the input RegistryId is valid or not throw business exception while getting country name"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		RegistrySummaryVO regVO = new RegistrySummaryVO()
		1*gfManager.getRegistryInfo("rg1234", "BedBathUS") >> regVO
		1*cToolsMock.getDefaultCountryForSite("BedBathUS") >> {throw new BBBBusinessException("Mock of business exception")}
		Map regMap = [:]
		orderMock.getRegistryMap() >> regMap
		when:
		cartObj.validateRegistryInfo("rg1234", "BedBathUS", requestMock, responseMock)
		then:
		cartObj.getFormError()
		regMap.containsKey("rg1234")
	}
	def"Validates if the input RegistryId is valid or not throw system exception while getting country name"(){
		given:
		cartObj = Spy()
		spyObjIntilization(cartObj)
		cartObj.setBuyOffFlag(true)
		RegistrySummaryVO regVO = new RegistrySummaryVO()
		bSessionbean.setBuyoffStartBrowsingSummaryVO(regVO)
		1*cToolsMock.getDefaultCountryForSite("BedBathUS") >> {throw new BBBSystemException("Mock of system exception")}
		Map regMap = [:]
		orderMock.getRegistryMap() >> regMap
		when:
		cartObj.validateRegistryInfo("rg1234", "BedBathUS", requestMock, responseMock)
		then:
		cartObj.getFormError()
		regMap.containsKey("rg1234")
	}
	def"This method is to validate item contian in registry"(){
		given:
		BBBCommerceItem item = Mock()
		BBBCommerceItem item1 = Mock()
		item.getReferenceNumber() >> ""
		item.getCatalogRefId() >> "sku1234"
		item.isBuyOffAssociatedItem() >> true
		item1.getCatalogRefId() >>  "sku1234"
		item1.getReferenceNumber() >> ""
		item.getRegistryId() >> "reg1234"
		when:
		boolean valid =  cartObj.isItemExistInRegistryContext(item, item1, "reg1234")
		then:
		valid
	}
	def"This method is to validate item contian in registry,ReferenceNumber  are not null"(){
		given:
		BBBCommerceItem item = Mock()
		BBBCommerceItem item1 = Mock()
		item.getReferenceNumber() >> "refnum"
		item.getCatalogRefId() >> "sku1234"
		item.isBuyOffAssociatedItem() >> true
		item1.getCatalogRefId() >>  "sku1234"
		item1.getReferenceNumber() >> "refnum"
		item.getRegistryId() >> "reg1234"
		when:
		boolean valid =  cartObj.isItemExistInRegistryContext(item, item1, "reg1234")
		then:
		valid
	}
	def"This method is to validate item contian in registry,ReferenceNumber  did  not macth"(){
		given:
		BBBCommerceItem item = Mock()
		BBBCommerceItem item1 = Mock()
		item.getReferenceNumber() >> "refnum"
		item.getCatalogRefId() >> "sku1234"
		item.isBuyOffAssociatedItem() >> true
		item1.getCatalogRefId() >>  "sku1234"
		item1.getReferenceNumber() >> "refnumber"
		item.getRegistryId() >> "reg1234"
		when:
		boolean valid =  cartObj.isItemExistInRegistryContext(item, item1, "reg1234")
		then:
		valid == false
	}
	def"This method is to validate item contian in registry isBuyOffAssociatedItem is false"(){
		given:
		BBBCommerceItem item = Mock()
		BBBCommerceItem item1 = Mock()
		item.isBuyOffAssociatedItem() >> false
		item1.getCatalogRefId() >>  "sku1234"
		item1.getReferenceNumber() >> "refnum"
		when:
		boolean valid =  cartObj.isItemExistInRegistryContext(item, item1, "reg1234")
		then:
		valid == false
	}
	def"This method is to validate item contian in registry sku id are differnet"(){
		given:
		BBBCommerceItem item = Mock()
		BBBCommerceItem item1 = Mock()
		item.getReferenceNumber() >> ""
		item.isBuyOffAssociatedItem() >> true
		item1.getCatalogRefId() >>  "sku1234"
		item1.getReferenceNumber() >> "refnum"
		item.getCatalogRefId() >> "sku1235"
		when:
		boolean valid =  cartObj.isItemExistInRegistryContext(item, item1, "reg1234")
		then:
		valid == false
	}
	def"This method is to validate item contian in registry ,registry id is null"(){
		given:
		BBBCommerceItem item = Mock()
		BBBCommerceItem item1 = Mock()
		item.getReferenceNumber() >> ""
		item.isBuyOffAssociatedItem() >> true
		item1.getCatalogRefId() >>  "sku1234"
		item1.getReferenceNumber() >> "refnum"
		item.getCatalogRefId() >> "sku1234"
		when:
		boolean valid =  cartObj.isItemExistInRegistryContext(item, item1, "reg1234")
		then:
		valid == false
	}
	def"This method is to validate item contian in registry ,registry ids are differnet"(){
		given:
		BBBCommerceItem item = Mock()
		BBBCommerceItem item1 = Mock()
		item.getReferenceNumber() >> ""
		item.isBuyOffAssociatedItem() >> true
		item1.getCatalogRefId() >>  "sku1234"
		item1.getReferenceNumber() >> "refnum"
		item.getCatalogRefId() >> "sku1234"
		item.getRegistryId() >> "reg12345"
		when:
		boolean valid =  cartObj.isItemExistInRegistryContext(item, item1, "reg1234")
		then:
		valid == false
	}
	def"This method is to validate item contian in registry ,ReferenceNumber is null for first item"(){
		given:
		BBBCommerceItem item = Mock()
		BBBCommerceItem item1 = Mock()
		item.getReferenceNumber() >> ""
		item.isBuyOffAssociatedItem() >> true
		item1.getCatalogRefId() >>  "sku1234"
		item1.getReferenceNumber() >> "refnum"
		item.getCatalogRefId() >> "sku1234"
		item.getRegistryId() >> "reg1234"
		when:
		boolean valid =  cartObj.isItemExistInRegistryContext(item, item1, "reg1234")
		then:
		valid == false
	}
	def"This method is to validate item contian in registry ,ReferenceNumber is null for second item"(){
		given:
		BBBCommerceItem item = Mock()
		BBBCommerceItem item1 = Mock()
		item.getReferenceNumber() >> "refnum"
		item.isBuyOffAssociatedItem() >> true
		item1.getCatalogRefId() >>  "sku1234"
		item1.getReferenceNumber() >> ""
		item.getCatalogRefId() >> "sku1234"
		item.getRegistryId() >> "reg1234"
		when:
		boolean valid =  cartObj.isItemExistInRegistryContext(item, item1, "reg1234")
		then:
		valid == false
	}
	def"TC for handleRepriceOrder"(){
		given:
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB
		1*pgMAnager.checkGiftCard(orderMock) >> true
		when:
		boolean valid =  cartObj.handleRepriceOrder(requestMock, responseMock)
		then:
		valid
		1*requestMock.setParameter(BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY, BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY_REQ)
		1*pgMAnager.processPaymentGroupStatusOnLoad(orderMock)
	}
	def"TC for handleRepriceOrder , order does not contain giftcard"(){
		given:
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEAPP
		1*pgMAnager.checkGiftCard(orderMock) >> false
		when:
		boolean valid =  cartObj.handleRepriceOrder(requestMock, responseMock)
		then:
		valid
		1*requestMock.setParameter(BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY, BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY_REQ)
		0*pgMAnager.processPaymentGroupStatusOnLoad(orderMock)
	}
	def"TC for handleRepriceOrder throw commerceexception"(){
		given:
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "Desktop"
		1*pgMAnager.checkGiftCard(orderMock) >> true
		1*pgMAnager.processPaymentGroupStatusOnLoad(orderMock) >> {throw new CommerceException("Mock of Commerce exception")}
		when:
		boolean valid =  cartObj.handleRepriceOrder(requestMock, responseMock)
		then:
		valid
		0*requestMock.setParameter(BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY, BBBCoreConstants.ITEM_LEVEL_EXP_DELIVERY_REQ)
	}
	void spyObjIntilization(BBBCartFormhandler cartObj){
		cartObj.setShoppingCart(orderHolderMock)
		orderHolderMock.getCurrent() >> orderMock
		cartObj.setCommonConfiguration(cConfig)
		cartObj.setOrderManager(orderManagerMock)
		cConfig.isLogDebugEnableOnCartFormHandler() >> true
		cartObj.setBopusInventoryService(biServiceMock)
		cartObj.setLblTxtTemplateManager(errorMSGMock)
		cartObj.setCatalogUtil(cToolsMock)
		cartObj.setInventoryManager(inManagerMock)
		cartObj.setCacheContainer(cacheMock)
		cartObj.setLocalStoreRepository(repMock)
		cartObj.setProfile(profileMock)
		cartObj.setGiftlistManager(glManagerMock)
		cartObj.setCommerceItemManager(cManagerMock)
		cartObj.setSavedItemsSessionBean(sItemSessionMock)
		cartObj.setRepeatingRequestMonitor(reqMonitorMock)
		cartObj.setWishlistManager(wishLisManger)
		cartObj.setTransactionManager(trMock)
		cartObj.setPricingManager(priceManager)
		cartObj.setMsgHandler(errorMSGMock)
		cartObj.setPurchaseProcessHelper(ppHelper)
		requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> bSessionbean
		cartObj.setPaypalServiceManager(paypalManager)
		cartObj.setCheckoutManager(cManager)
		cartObj.setCheckoutState(pState)
		cartObj.setShippingGroupManager(sGroupManager)
		cartObj.setPipelineManager(plManager)
		cartObj.setCouponUtil(cUtilMock)
		cartObj.setPromoTools(pTools)
		cartObj.setClaimableManager(clManager)
		cartObj.setStoreInventoryContainer(siContainer)
		cartObj.setRegistryManager(gfManager)
	}
}
