package com.bbb.commerce.service.pricing

import java.util.Collection
import java.util.HashMap
import java.util.Map
import javax.transaction.TransactionManager

import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.droplet.BBBPromotionTools
import com.bbb.commerce.catalog.vo.GiftWrapVO
import com.bbb.commerce.catalog.vo.LTLAssemblyFeeVO
import com.bbb.commerce.catalog.vo.LTLDeliveryChargeVO
import com.bbb.commerce.common.BBBAddressImpl
import atg.commerce.order.InvalidParameterException
import atg.commerce.order.Order;

import com.bbb.commerce.order.BBBCommerceItemManager
import com.bbb.commerce.order.BBBOrderManager
import com.bbb.commerce.order.BBBShippingGroupManager
import com.bbb.commerce.pricing.BBBPricingTools
import com.bbb.constants.BBBCheckoutConstants
import com.bbb.constants.BBBCmsConstants
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.ecommerce.order.BBBOrderTools
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.BBBOrderPriceInfo
import com.bbb.order.bean.BBBShippingPriceInfo
import com.bbb.order.bean.GiftWrapCommerceItem
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem
import com.bbb.utils.BBBUtility
import com.bedbathandbeyond.atg.Address
import com.bedbathandbeyond.atg.Adjustment
import com.bedbathandbeyond.atg.AdjustmentList
import com.bedbathandbeyond.atg.Coupon
import com.bedbathandbeyond.atg.CouponList
import com.bedbathandbeyond.atg.Item
import com.bedbathandbeyond.atg.ItemList
import com.bedbathandbeyond.atg.MessageHeader
import com.bedbathandbeyond.atg.PcInfoType
import com.bedbathandbeyond.atg.PricingRequest
import com.bedbathandbeyond.atg.PricingRequestInfo
import com.bedbathandbeyond.atg.PricingResponse
import com.bedbathandbeyond.atg.PricingResponseInfo
import com.bedbathandbeyond.atg.ShippingList
import com.bedbathandbeyond.atg.Item.Status.Enum
import com.octetstring.vde.backend.Mapper

import atg.commerce.CommerceException
import atg.commerce.claimable.ClaimableTools
import atg.commerce.order.CommerceItem
import atg.commerce.order.CommerceItemNotFoundException
import atg.commerce.order.HardgoodShippingGroup
import atg.commerce.order.OrderTools
import atg.commerce.order.ShippingGroup
import atg.commerce.order.ShippingGroupCommerceItemRelationship
import atg.commerce.pricing.DetailedItemPriceInfo
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.OrderPriceInfo
import atg.commerce.pricing.PricingAdjustment
import atg.commerce.pricing.PricingException
import atg.commerce.pricing.ShippingPriceInfo
import atg.dtm.TransactionDemarcationException
import atg.repository.MutableRepository
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.userprofiling.Profile
import atg.userprofiling.ProfileTools
import spock.lang.specification.BBBExtendedSpec

class BBBPricingWSMapperSpecification extends BBBExtendedSpec{
	
	def BBBOrderManager mOrderManager =Mock()
	def TransactionManager manager =Mock()
	def BBBCatalogToolsImpl cTools =Mock()
	def Repository rep = Mock()
	def BBBShippingGroupManager sgManager =Mock()
	def BBBPricingTools pTools = Mock()
	def ProfileTools proTools =Mock()
	def BBBCommerceItemManager cman =Mock()
	def Repository repository =Mock()
	
	def BBBPricingWSMapper mapper
	
	def setup(){
			
		 mapper = new BBBPricingWSMapper()
		 mapper.setOrderManager(mOrderManager)
		 mapper.setTransactionManager(manager)
		 mapper.setCatalogTools(cTools)
		 mapper.setSiteRepository(rep)
		 mapper.setShippingGroupManager(sgManager)
		 mapper.setPricingTools(pTools)
		 mapper.setProfileTools(proTools)
		 mapper.setCommerceItemManager(cman)
		 mapper.setShippingMethodList([""])
		 mapper.setPromotionRepository(repository)
	}
	
	def"transformRequestToOrder, sets the bbbOrder object"(){
		
		given:
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		
		Map<String, Object> pParameters = new HashMap()
		
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> "LW"
		1*cTools.getShippingMethod("LW") >> {}
		
		header.getSiteId() >> siteId
		1*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		
		1*bbbOrder.getPriceInfo() >>  atgOrderPriceInfo
		2*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> "TBS"
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "true")
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populateTBSShippingPriceInRequest in populateshipping groups
		def GiftWrapCommerceItem giftWrapItem =Mock()
		def ItemPriceInfo gInfo =Mock()
		
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "LW") >>hgGroup
		group.getShippingPrice() >> priceInfo
		priceInfo.getShippingAmount() >> 200.0
		
		1*hgGroup.getPriceInfo() >> hgInfo
		priceInfo.getShippingRawAmount() >> 50.0
		priceInfo.getSurcharge() >> 100.0
		priceInfo.getGiftWrapFee() >> 150.0
		
		1*hgGroup.getGiftWrapCommerceItem() >> giftWrapItem
		giftWrapItem.getPriceInfo() >> gInfo
		hgGroup.getId() >> "hgId"
		group.getShippingGroupId() >> "gId"
		
		//for populateItemInfo inside populateShippingGroups 
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def LTLDeliveryChargeCommerceItem cItem1 =Mock()
		def LTLAssemblyFeeCommerceItem cItem2 =Mock()
		def CommerceItem cItem3 =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		LTLDeliveryChargeVO vo = new LTLDeliveryChargeVO()
		LTLAssemblyFeeVO ltlVo = new LTLAssemblyFeeVO()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
	
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		cItem2.getId() >>"cItem2Id"
		cItem1.getId() >>"cItem1Id"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >>  skuItem
		1*skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT) >>productItems
		1*orderTool.getDefaultCommerceItemType() >>"defaultType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "sku", 10.longValue(),"repositoryId","defaultType") >> cItem
		
		1*cTools.isSkuLtl(bbbOrder.getSiteId(), "sku") >> true
		1*cTools.getLTLDeliveryChargeSkuDetails("tbs") >> vo
		vo.setLtlDeliveryChargeSkuId("ltlId")
		vo.setLtlDeliveryChargeProductId("ltlProId")
		1*orderTool.getLtlDeliveryChargeCommerceItemType() >> "chargeType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "ltlId", 10.longValue(),"ltlProId", "chargeType") >> cItem1
		
		item.getIsAssemblyRequested() >> true
		1*cTools.getLTLAssemblyFeeSkuDetails("tbs") >> ltlVo
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "assemblyId", 10.longValue(),"assemblyProId","assemblyType") >> cItem2
		ltlVo.setLtlAssemblySkuId("assemblyId")
		ltlVo.setLtlAssemblyProductId("assemblyProId")
		1*orderTool.getLtlAssemblyFeeCommerceItemType() >> "assemblyType"
		
		1*sgManager.getShippingGroupCommerceItemRelationship(bbbOrder, "cItemId","hgId") >> rel
		1*group.getGiftWrapRequired() >> true
		1*cTools.getWrapSkuDetails("tbs") >> giftVo
		giftVo.setWrapSkuId("wrapId")
		giftVo.setWrapProductId("wrapProId")
		1*orderTool.getGiftWrapCommerceItemType() >> "wrapType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "wrapId", 1, "wrapProId", "wrapType") >> cItem3
		cItem3.getPriceInfo() >> c3Info
		//end populateItemInfo
		
		pTools.getDefaultLocale() >> locale
		//end populateshippinggroups
		
		//isPartiallyShipped starts
		final Enum SHIPPED = Enum.forString("SHIPPED")
		item.getStatus() >> SHIPPED
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == bbbOrder
		1*bbbOrder.setSiteId(siteId.toString())
		1*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*bbbOrder.setPriceInfo(atgOrderPriceInfo)
		pParameters.get("ORDER_TYPE") == "TBS"
		
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO) 
		infoMap.get(hgGroup.getId()) == hgInfo
		
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		1*hgInfo.setFinalShipping(priceInfo.getShippingAmount().doubleValue())
		1*hgInfo.setRawShipping(priceInfo.getShippingRawAmount().doubleValue())
		1*hgInfo.setFinalSurcharge(priceInfo.getSurcharge().doubleValue())
		1*gInfo.setAmount(priceInfo.getGiftWrapFee().doubleValue())
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		
		// for populateItemInfo inside populateShippingGroups 
		1*cItem.setLtlItem(true)
		1*cItem1.setLtlCommerceItemRelation("cItemId")
		1*cItem2.setLtlCommerceItemRelation("cItemId")
		1*cItem.setAssemblyItemId("cItem2Id")
		1*cItem.setDeliveryItemId("cItem1Id")
		//1*hgGroup.setGiftWrapInd(_)
		1*mOrderManager.updateOrder(bbbOrder)
		1*c3Info.setAmount(0.0)
		1*c3Info.setAmountIsFinal(true)
		
		//isPartiallyShipped
		pParameters.get(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED) == true
	}
	
	def"transformRequestToOrder, sets the bbbOrder object when order type is not TBS and shippingGroup.getShippingPrice is not null"(){
		
		given:
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		
		Map<String, Object> pParameters = new HashMap()
		
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> "LW"
		1*cTools.getShippingMethod("LW") >> {}
		
		header.getSiteId() >> null >> siteId
		0*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		header.getCurrencyCode() >> currency
		1*bbbOrder.getPriceInfo() >>  null
		0*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> "identifier"
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, null)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, null)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "true")
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populateTBSShippingPriceInRequest in populateshipping groups
		def GiftWrapCommerceItem giftWrapItem =Mock()
		def ItemPriceInfo gInfo =Mock()
		
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "LW") >>hgGroup
		group.getShippingPrice() >> priceInfo
		priceInfo.getShippingAmount() >> 200.0
		
		1*hgGroup.getPriceInfo() >> hgInfo
		priceInfo.getShippingRawAmount() >>0.0
		priceInfo.getSurcharge() >> 0.0
		
		1*hgGroup.getGiftWrapCommerceItem() >> null
		giftWrapItem.getPriceInfo() >> gInfo
		hgGroup.getId() >> "hgId"
		group.getShippingGroupId() >> "gId"
			
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def LTLDeliveryChargeCommerceItem cItem1 =Mock()
		def LTLAssemblyFeeCommerceItem cItem2 =Mock()
		def CommerceItem cItem3 =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		LTLDeliveryChargeVO vo = new LTLDeliveryChargeVO()
		LTLAssemblyFeeVO ltlVo = new LTLAssemblyFeeVO()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
	
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		cItem2.getId() >>"cItem2Id"
		cItem1.getId() >>"cItem1Id"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >> skuItem
		1*skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT) >>productItems
		1*orderTool.getDefaultCommerceItemType() >>"defaultType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "sku", 10.longValue(),"repositoryId","defaultType") >> cItem
		
		1*cTools.isSkuLtl(bbbOrder.getSiteId(), "sku") >> true
		1*cTools.getLTLDeliveryChargeSkuDetails("tbs") >> vo
		vo.setLtlDeliveryChargeSkuId("ltlId")
		vo.setLtlDeliveryChargeProductId("ltlProId")
		1*orderTool.getLtlDeliveryChargeCommerceItemType() >> "chargeType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "ltlId", 10.longValue(),"ltlProId", "chargeType") >> cItem1
		
		item.getIsAssemblyRequested() >> true
		1*cTools.getLTLAssemblyFeeSkuDetails("tbs") >> null
		1*sgManager.getShippingGroupCommerceItemRelationship(bbbOrder, "cItemId","hgId") >> rel
		1*group.getGiftWrapRequired() >> true
		1*cTools.getWrapSkuDetails("tbs") >> giftVo
		giftVo.setWrapSkuId("wrapId")
		giftVo.setWrapProductId("wrapProId")
		1*orderTool.getGiftWrapCommerceItemType() >> "wrapType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "wrapId", 1, "wrapProId", "wrapType") >> cItem3
		cItem3.getPriceInfo() >> c3Info
		//end populateItemInfo
		
		pTools.getDefaultLocale() >> locale
		//end populateshippinggroups
		
		//isPartiallyShipped starts
		final Enum SHIPPED = Enum.forString("SHIPPED")
		item.getStatus() >> SHIPPED
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == bbbOrder
		0*bbbOrder.setSiteId(siteId.toString())
		0*atgOrderPriceInfo.setCurrencyCode(String.valueOf(header.getCurrencyCode()))
		1*bbbOrder.setPriceInfo(_)
		pParameters.get("ORDER_TYPE") == "identifier"
		
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.get(hgGroup.getId()) == hgInfo
		
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		1*hgInfo.setFinalShipping(priceInfo.getShippingAmount().doubleValue())
		0*hgInfo.setRawShipping(priceInfo.getShippingRawAmount().doubleValue())
		0*hgInfo.setFinalSurcharge(priceInfo.getSurcharge().doubleValue())
		0*gInfo.setAmount(_)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		
		// for populateItemInfo inside populateShippingGroups
		1*cItem.setLtlItem(true)
		1*cItem1.setLtlCommerceItemRelation("cItemId")
		0*cItem2.setLtlCommerceItemRelation("cItemId")
		0*cItem.setAssemblyItemId("cItem2Id")
		1*cItem.setDeliveryItemId("cItem1Id")
		1*mOrderManager.updateOrder(bbbOrder)
		0*c3Info.setAmount(0.0)
		0*c3Info.setAmountIsFinal(true)
		
		//isPartiallyShipped
		pParameters.get(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED) == true
	}
	
	def"transformRequestToOrder,throws CommerceException in populateshippinggroups"(){ //
		
		given:
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		Map<String, Object> pParameters = new HashMap()
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> "LW"
		1*cTools.getShippingMethod("LW") >> {}
		
		header.getSiteId() >> null >> siteId
		0*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		header.getCurrencyCode() >> currency
		1*bbbOrder.getPriceInfo() >>  null
		0*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> "identifier"
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, null)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, null)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "true")
		
		//for populate address method inside populateshippinggroups
		def Address pAddress =Mock()
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "LW") >>{throw new CommerceException()}
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		0*bbbOrder.setSiteId(siteId.toString())
		0*atgOrderPriceInfo.setCurrencyCode(String.valueOf(header.getCurrencyCode()))
		1*bbbOrder.setPriceInfo(_)
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.get(_) == null
		pParameters.get("ORDER_TYPE") == "identifier"
		BBBSystemException e = thrown()
	}
		
	
	def"transformRequestToOrder, sets the bbbOrder object when order type is empty,shipping method in not LW(populateShippingGroups) and shippingRawAmount, Surcharge is zero in populateItemInfo"(){ 
		
		given:
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		
		Map<String, Object> pParameters = new HashMap()
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> "method"
		1*cTools.getShippingMethod("method") >> {}
		
		header.getSiteId() >> null >> siteId
		0*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		
		1*bbbOrder.getPriceInfo() >> atgOrderPriceInfo
		1*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> ""
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "true")
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populateTBSShippingPriceInRequest in populateShippingGroups
		def GiftWrapCommerceItem giftWrapItem =Mock()
		def ItemPriceInfo gInfo =Mock()
		
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "method") >>hgGroup
		group.getShippingPrice() >> priceInfo
		priceInfo.getShippingAmount() >> 200.0
		
		1*hgGroup.getPriceInfo() >> hgInfo
		priceInfo.getShippingRawAmount() >>0.0
		priceInfo.getSurcharge() >> 0.0
		/*priceInfo.getGiftWrapFee() >> 150.0*/
		
		1*hgGroup.getGiftWrapCommerceItem() >> null
		giftWrapItem.getPriceInfo() >> gInfo
		hgGroup.getId() >> "hgId"
		group.getShippingGroupId() >> "gId"
		
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def LTLDeliveryChargeCommerceItem cItem1 =Mock()
		def LTLAssemblyFeeCommerceItem cItem2 =Mock()
		def CommerceItem cItem3 =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		LTLDeliveryChargeVO vo = new LTLDeliveryChargeVO()
		LTLAssemblyFeeVO ltlVo = new LTLAssemblyFeeVO()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
	
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		cItem2.getId() >>"cItem2Id"
		cItem1.getId() >>"cItem1Id"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >> skuItem
		1*skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT) >>productItems
		1*orderTool.getDefaultCommerceItemType() >>"defaultType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "sku", 10.longValue(),"repositoryId","defaultType") >> cItem
		
		1*cTools.isSkuLtl(bbbOrder.getSiteId(), "sku") >> true
		1*cTools.getLTLDeliveryChargeSkuDetails("tbs") >> vo
		vo.setLtlDeliveryChargeSkuId("ltlId")
		vo.setLtlDeliveryChargeProductId("ltlProId")
		1*orderTool.getLtlDeliveryChargeCommerceItemType() >> "chargeType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "ltlId", 10.longValue(),"ltlProId", "chargeType") >> cItem1
		
		item.getIsAssemblyRequested() >> true
		0*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "assemblyId", 10.longValue(),"assemblyProId","assemblyType") >> null
		0*ltlVo.setLtlAssemblySkuId("assemblyId")
		0*ltlVo.setLtlAssemblyProductId("assemblyProId")
		0*orderTool.getLtlAssemblyFeeCommerceItemType() >> "assemblyType"
		
		1*sgManager.getShippingGroupCommerceItemRelationship(bbbOrder, "cItemId","hgId") >> rel
		1*group.getGiftWrapRequired() >> true
		1*cTools.getWrapSkuDetails("tbs") >> giftVo
		giftVo.setWrapSkuId("wrapId")
		giftVo.setWrapProductId("wrapProId")
		1*orderTool.getGiftWrapCommerceItemType() >> "wrapType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "wrapId", 1, "wrapProId", "wrapType") >> cItem3
		cItem3.getPriceInfo() >> c3Info
		//end populateItemInfo
		
		pTools.getDefaultLocale() >> locale
		//end populateshippinggroups
		
		//isPartiallyShipped starts
		final Enum SHIPPED = Enum.forString("SHIPPED")
		item.getStatus() >> SHIPPED

		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == bbbOrder
		0*bbbOrder.setSiteId(siteId.toString())
		0*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*bbbOrder.setPriceInfo(_)
		pParameters.get("ORDER_TYPE") == ""
		
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.get(hgGroup.getId()) == hgInfo
		
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		1*hgInfo.setFinalShipping(priceInfo.getShippingAmount().doubleValue())
		0*hgInfo.setRawShipping(priceInfo.getShippingRawAmount().doubleValue())
		0*hgInfo.setFinalSurcharge(priceInfo.getSurcharge().doubleValue())
		0*gInfo.setAmount(_)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		
		// for populateItemInfo inside populateShippingGroups
		1*cItem.setLtlItem(true)
		1*cItem1.setLtlCommerceItemRelation("cItemId")
		0*cItem2.setLtlCommerceItemRelation("cItemId")
		0*cItem.setAssemblyItemId("cItem2Id")
		1*cItem.setDeliveryItemId("cItem1Id")
		//1*hgGroup.setGiftWrapInd(_)
		1*mOrderManager.updateOrder(bbbOrder)
		0*c3Info.setAmount(0.0)
		0*c3Info.setAmountIsFinal(true)
		
		//isPartiallyShipped
		pParameters.get(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED) == true
	}
	
	def"transformRequestToOrder, sets the bbbOrder object when order type and is empty,ISAssemblyRequested is false in populateItemInfo"(){ 
		
		given:
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		
		Map<String, Object> pParameters = new HashMap()
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> "LW"
		1*cTools.getShippingMethod("LW") >> {}
		
		header.getSiteId() >> siteId
		1*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		
		1*bbbOrder.getPriceInfo() >> atgOrderPriceInfo
		2*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> ""
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "true")
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
			
		//for populateZeroTBSShippingPriceInRequest in populateshipping groups
		def GiftWrapCommerceItem giftWrapItem =Mock()
		def ItemPriceInfo gInfo =Mock()
		
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "LW") >>hgGroup
		group.getShippingPrice() >> null
		1*hgGroup.getPriceInfo() >> hgInfo
		
		1*hgGroup.getGiftWrapCommerceItem() >> giftWrapItem
		giftWrapItem.getPriceInfo() >> gInfo
		hgGroup.getId() >> "hgId"
		group.getShippingGroupId() >> "gId"
		
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def LTLDeliveryChargeCommerceItem cItem1 =Mock()
		def LTLAssemblyFeeCommerceItem cItem2 =Mock()
		def CommerceItem cItem3 =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		LTLDeliveryChargeVO vo = new LTLDeliveryChargeVO()
		LTLAssemblyFeeVO ltlVo = new LTLAssemblyFeeVO()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		cItem2.getId() >>"cItem2Id"
		cItem1.getId() >>"cItem1Id"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >> skuItem
		1*skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT) >>productItems
		1*orderTool.getDefaultCommerceItemType() >>"defaultType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "sku", 10.longValue(),"repositoryId","defaultType") >> cItem
		
		1*cTools.isSkuLtl(bbbOrder.getSiteId(), "sku") >> true
		1*cTools.getLTLDeliveryChargeSkuDetails("tbs") >> vo
		vo.setLtlDeliveryChargeSkuId("ltlId")
		vo.setLtlDeliveryChargeProductId("ltlProId")
		1*orderTool.getLtlDeliveryChargeCommerceItemType() >> "chargeType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "ltlId", 10.longValue(),"ltlProId", "chargeType") >> cItem1
		
		item.getIsAssemblyRequested() >> false
		0*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "assemblyId", 10.longValue(),"assemblyProId","assemblyType") >> null
		0*ltlVo.setLtlAssemblySkuId("assemblyId")
		0*ltlVo.setLtlAssemblyProductId("assemblyProId")
		0*orderTool.getLtlAssemblyFeeCommerceItemType() >> "assemblyType"
		
		1*sgManager.getShippingGroupCommerceItemRelationship(bbbOrder, "cItemId","hgId") >> rel
		1*group.getGiftWrapRequired() >> true
		1*cTools.getWrapSkuDetails("tbs") >> giftVo
		giftVo.setWrapSkuId("wrapId")
		giftVo.setWrapProductId("wrapProId")
		1*orderTool.getGiftWrapCommerceItemType() >> "wrapType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "wrapId", 1, "wrapProId", "wrapType") >> null
		cItem3.getPriceInfo() >> c3Info
		//end populateItemInfo
		
		pTools.getDefaultLocale() >> locale
		//end populateshippinggroups
		
		//isPartiallyShipped starts
		final Enum SHIPPED = Enum.forString("SHIPPED")
		item.getStatus() >> SHIPPED
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == bbbOrder
		1*bbbOrder.setSiteId(siteId.toString())
		1*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*bbbOrder.setPriceInfo(atgOrderPriceInfo)
		pParameters.get("ORDER_TYPE") == ""
		
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.get(hgGroup.getId()) == hgInfo
		
		//for populateZeroTBSShippingPriceInRequest inside populateShipping groups
		1*hgInfo.setFinalShipping(0.00)
		1*hgInfo.setRawShipping(0.00)
		1*hgInfo.setFinalSurcharge(0.00)
		1*gInfo.setAmount(0.0)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		
		// for populateItemInfo inside populateShippingGroups
		1*cItem.setLtlItem(true)
		1*cItem1.setLtlCommerceItemRelation("cItemId")
		0*cItem2.setLtlCommerceItemRelation("cItemId")
		0*cItem.setAssemblyItemId("cItem2Id")
		1*cItem.setDeliveryItemId("cItem1Id")
		1*mOrderManager.updateOrder(bbbOrder)
		0*c3Info.setAmount(0.0)
		0*c3Info.setAmountIsFinal(true)
		
		//isPartiallyShipped
		pParameters.get(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED) == true
	}
	
	def"transformRequestToOrder, sets the bbbOrder object when order type and  sku is not ltl in populateItemInfo"(){
		
		given:
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		
		Map<String, Object> pParameters = new HashMap()
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> "LW"
		1*cTools.getShippingMethod("LW") >> {}
		
		header.getSiteId() >> siteId
		1*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		
		1*bbbOrder.getPriceInfo() >> atgOrderPriceInfo
		2*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> ""
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "true")
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populatezeroTBSShippingPriceInRequest in populateshipping groups
		def GiftWrapCommerceItem giftWrapItem =Mock()
		def ItemPriceInfo gInfo =Mock()

		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "LW") >>hgGroup
		group.getShippingPrice() >> null
		1*hgGroup.getPriceInfo() >> hgInfo
		1*hgGroup.getGiftWrapCommerceItem() >> null
		giftWrapItem.getPriceInfo() >> gInfo
		hgGroup.getId() >> "hgId"
		group.getShippingGroupId() >> "gId"
		
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def LTLDeliveryChargeCommerceItem cItem1 =Mock()
		def LTLAssemblyFeeCommerceItem cItem2 =Mock()
		def CommerceItem cItem3 =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		LTLDeliveryChargeVO vo = new LTLDeliveryChargeVO()
		LTLAssemblyFeeVO ltlVo = new LTLAssemblyFeeVO()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		cItem2.getId() >>"cItem2Id"
		cItem1.getId() >>"cItem1Id"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >> skuItem
		1*skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT) >>productItems
		1*orderTool.getDefaultCommerceItemType() >>"defaultType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "sku", 10.longValue(),"repositoryId","defaultType") >> cItem
		
		1*cTools.isSkuLtl(bbbOrder.getSiteId(), "sku") >> false
		1*sgManager.getShippingGroupCommerceItemRelationship(bbbOrder, "cItemId","hgId") >> rel
		1*group.getGiftWrapRequired() >> false
		//end populateItemInfo
		
		pTools.getDefaultLocale() >> locale
		//end populateshippinggroups
		
		//isPartiallyShipped starts
		final Enum SHIPPED = Enum.forString("SHIPPED")
		item.getStatus() >> SHIPPED

		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == bbbOrder
		1*bbbOrder.setSiteId(siteId.toString())
		1*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*bbbOrder.setPriceInfo(atgOrderPriceInfo)
		pParameters.get("ORDER_TYPE") == ""
		
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.get(hgGroup.getId()) == hgInfo
		
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		1*hgInfo.setRawShipping(0.0)
		1*hgInfo.setFinalShipping(0.0)
		1*hgInfo.setFinalSurcharge(0.00)
		0*gInfo.setAmount(_)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		
		// for populateItemInfo inside populateShippingGroups
		0*cItem.setLtlItem(true)
		0*cItem1.setLtlCommerceItemRelation("cItemId")
		0*cItem.setDeliveryItemId("cItem1Id")
		1*mOrderManager.updateOrder(bbbOrder)
		0*c3Info.setAmount(0.0)
		0*c3Info.setAmountIsFinal(true)
		
		//isPartiallyShipped
		pParameters.get(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED) == true
	}
	
	
	def"transformRequestToOrder, sets the bbbOrder object when order type, sku is ltl and shippingMethod is empty in populateItemInfo"(){ 
		
		given:
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		
		Map<String, Object> pParameters = new HashMap()
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> ""
		1*cTools.getShippingMethod("") >> {}
		
		header.getSiteId() >> siteId
		1*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		
		1*bbbOrder.getPriceInfo() >> atgOrderPriceInfo
		2*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> ""
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "true")
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populateTBSShippingPriceInRequest in populateshipping groups
		def GiftWrapCommerceItem giftWrapItem =Mock()
		def ItemPriceInfo gInfo =Mock()
		
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "") >>hgGroup
		group.getShippingPrice() >> priceInfo
		priceInfo.getShippingAmount() >> 200.0
		
		1*hgGroup.getPriceInfo() >> hgInfo
		priceInfo.getShippingRawAmount() >>0.0
		priceInfo.getSurcharge() >> 0.0
		1*hgGroup.getGiftWrapCommerceItem() >> null
		giftWrapItem.getPriceInfo() >> gInfo
		hgGroup.getId() >> "hgId"
		group.getShippingGroupId() >> "gId"
		
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def LTLDeliveryChargeCommerceItem cItem1 =Mock()
		def LTLAssemblyFeeCommerceItem cItem2 =Mock()
		def CommerceItem cItem3 =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		LTLDeliveryChargeVO vo = new LTLDeliveryChargeVO()
		LTLAssemblyFeeVO ltlVo = new LTLAssemblyFeeVO()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		cItem2.getId() >>"cItem2Id"
		cItem1.getId() >>"cItem1Id"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >> skuItem
		1*skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT) >>productItems
		1*orderTool.getDefaultCommerceItemType() >>"defaultType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "sku", 10.longValue(),"repositoryId","defaultType") >> cItem
		
		1*cTools.isSkuLtl(bbbOrder.getSiteId(), "sku") >> true
		1*sgManager.getShippingGroupCommerceItemRelationship(bbbOrder, "cItemId","hgId") >> rel
		1*group.getGiftWrapRequired() >> true
		1*cTools.getWrapSkuDetails("tbs") >> giftVo
		giftVo.setWrapSkuId("wrapId")
		giftVo.setWrapProductId("wrapProId")
		1*orderTool.getGiftWrapCommerceItemType() >> "wrapType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "wrapId", 1, "wrapProId", "wrapType") >> cItem3
		cItem3.getPriceInfo() >> c3Info
		//end populateItemInfo
		
		pTools.getDefaultLocale() >> locale
		//end populateshippinggroups
		
		//isPartiallyShipped starts
		final Enum SHIPPED = Enum.forString("SHIPPED")
		item.getStatus() >> SHIPPED
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == bbbOrder
		1*bbbOrder.setSiteId(siteId.toString())
		1*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*bbbOrder.setPriceInfo(atgOrderPriceInfo)
		pParameters.get("ORDER_TYPE") == ""
		
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.get(hgGroup.getId()) == hgInfo
		
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		1*hgInfo.setFinalShipping(priceInfo.getShippingAmount().doubleValue())
		0*hgInfo.setRawShipping(priceInfo.getShippingRawAmount().doubleValue())
		0*hgInfo.setFinalSurcharge(priceInfo.getSurcharge().doubleValue())
		0*gInfo.setAmount(_)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		
		// for populateItemInfo inside populateShippingGroups
		0*cItem.setLtlItem(true)
		0*cItem1.setLtlCommerceItemRelation("cItemId")
		0*cItem.setDeliveryItemId("cItem1Id")
		1*mOrderManager.updateOrder(bbbOrder)
		0*c3Info.setAmount(0.0)
		0*c3Info.setAmountIsFinal(true)
		
		//isPartiallyShipped
		pParameters.get(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED) == true
	}
	
	def"transformRequestToOrder, sets the bbbOrder object when order type, sku is ltl and webservice order is not from tbs in  populateShippingGroups"(){ 
		
		given:
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		Map<String, Object> pParameters = new HashMap()
		
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> ""
		1*cTools.getShippingMethod("") >> {}
		header.getSiteId() >> siteId
		1*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		1*bbbOrder.getPriceInfo() >> atgOrderPriceInfo
		2*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> ""
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, null)
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populateTBSShippingPriceInRequest in populateshipping groups	
		def ItemPriceInfo gInfo =Mock()
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "") >>hgGroup
		group.getShippingGroupId() >> "gId"
		
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def LTLDeliveryChargeCommerceItem cItem1 =Mock()
		def LTLAssemblyFeeCommerceItem cItem2 =Mock()
		def CommerceItem cItem3 =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		LTLDeliveryChargeVO vo = new LTLDeliveryChargeVO()
		LTLAssemblyFeeVO ltlVo = new LTLAssemblyFeeVO()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		cItem2.getId() >>"cItem2Id"
		cItem1.getId() >>"cItem1Id"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >> skuItem
		1*skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT) >>productItems
		1*orderTool.getDefaultCommerceItemType() >>"defaultType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "sku", 10.longValue(),"repositoryId","defaultType") >> cItem
		
		1*cTools.isSkuLtl(bbbOrder.getSiteId(), "sku") >> true
		sgManager.getShippingGroupCommerceItemRelationship(bbbOrder, "cItemId","hgId") >> rel
		1*group.getGiftWrapRequired() >> true
		1*cTools.getWrapSkuDetails("tbs") >> giftVo
		giftVo.setWrapSkuId("wrapId")
		giftVo.setWrapProductId("wrapProId")
		1*orderTool.getGiftWrapCommerceItemType() >> "wrapType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "wrapId", 1, "wrapProId", "wrapType") >> cItem3
		cItem3.getPriceInfo() >> c3Info
		//end populateItemInfo
		
		pTools.getDefaultLocale() >> locale
		//end populateshippinggroups
		
		//isPartiallyShipped starts
		final Enum PROCESS = Enum.forString("PROCESS")
		item.getStatus() >> PROCESS
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == bbbOrder
		1*bbbOrder.setSiteId(siteId.toString())
		1*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*bbbOrder.setPriceInfo(atgOrderPriceInfo)
		pParameters.get("ORDER_TYPE") == ""
		
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.isEmpty() == true
		
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		0*hgInfo.setFinalShipping(_)
		0*hgInfo.setRawShipping(_)
		0*hgInfo.setFinalSurcharge(_)
		0*gInfo.setAmount(_)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		
		// for populateItemInfo inside populateShippingGroups
		0*cItem.setLtlItem(true)
		0*cItem1.setLtlCommerceItemRelation("cItemId")
		0*cItem.setDeliveryItemId("cItem1Id")
		1*mOrderManager.updateOrder(bbbOrder)
		0*c3Info.setAmount(0.0)
		0*c3Info.setAmountIsFinal(true)
		
		//isPartiallyShipped
		pParameters.get(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED) == false
	}
	
	def"transformRequestToOrder, sets the bbbOrder object when order type, sku is ltl and webservice order is not from tbs"(){ //
		
		given:
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		Map<String, Object> pParameters = new HashMap()
		
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> ""
		1*cTools.getShippingMethod("") >> {}
		header.getSiteId() >> siteId
		1*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		1*bbbOrder.getPriceInfo() >> atgOrderPriceInfo
		2*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> ""
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populateTBSShippingPriceInRequest in populateshipping groups
		def ItemPriceInfo gInfo =Mock()
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "") >>hgGroup
		group.getShippingGroupId() >> "gId"
		
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def LTLDeliveryChargeCommerceItem cItem1 =Mock()
		def LTLAssemblyFeeCommerceItem cItem2 =Mock()
		def CommerceItem cItem3 =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		LTLDeliveryChargeVO vo = new LTLDeliveryChargeVO()
		LTLAssemblyFeeVO ltlVo = new LTLAssemblyFeeVO()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		cItem2.getId() >>"cItem2Id"
		cItem1.getId() >>"cItem1Id"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >> skuItem
		1*skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT) >>productItems
		1*orderTool.getDefaultCommerceItemType() >>"defaultType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "sku", 10.longValue(),"repositoryId","defaultType") >> cItem
		
		1*cTools.isSkuLtl(bbbOrder.getSiteId(), "sku") >> true
		sgManager.getShippingGroupCommerceItemRelationship(bbbOrder, "cItemId","hgId") >> rel
		1*group.getGiftWrapRequired() >> true
		1*cTools.getWrapSkuDetails("tbs") >> giftVo
		giftVo.setWrapSkuId("wrapId")
		giftVo.setWrapProductId("wrapProId")
		1*orderTool.getGiftWrapCommerceItemType() >> "wrapType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "wrapId", 1, "wrapProId", "wrapType") >> cItem3
		cItem3.getPriceInfo() >> c3Info
		//end populateItemInfo
		
		pTools.getDefaultLocale() >> locale
		//end populateshippinggroups
		
		//isPartiallyShipped starts
		final Enum PROCESS = Enum.forString("PROCESS")
		item.getStatus() >> PROCESS
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == bbbOrder
		1*bbbOrder.setSiteId(siteId.toString())
		1*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*bbbOrder.setPriceInfo(atgOrderPriceInfo)
		pParameters.get("ORDER_TYPE") == ""
		
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.isEmpty() == true
		
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		0*hgInfo.setFinalShipping(_)
		0*hgInfo.setRawShipping(_)
		0*hgInfo.setFinalSurcharge(_)
		0*gInfo.setAmount(_)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		
		// for populateItemInfo inside populateShippingGroups
		0*cItem.setLtlItem(true)
		0*cItem1.setLtlCommerceItemRelation("cItemId")
		0*cItem.setDeliveryItemId("cItem1Id")
		1*mOrderManager.updateOrder(bbbOrder)
		0*c3Info.setAmount(0.0)
		0*c3Info.setAmountIsFinal(true)
		
		//isPartiallyShipped
		pParameters.get(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED) == false
	}
	
	def"transformRequestToOrder, when BBBBusinessException is thrown in  populateAddressInfo"(){ 
		
		given:
		mapper = Spy()
		mapper.setOrderManager(mOrderManager)
		mapper.setTransactionManager(manager)
		mapper.setCatalogTools(cTools)
		mapper.setSiteRepository(rep)
		mapper.setShippingGroupManager(sgManager)
		mapper.setPricingTools(pTools)
		
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		Map<String, Object> pParameters = new HashMap()
		
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> ""
		1*cTools.getShippingMethod("") >> {}
		header.getSiteId() >> siteId
		1*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		1*bbbOrder.getPriceInfo() >> atgOrderPriceInfo
		2*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> ""
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populateTBSShippingPriceInRequest in populateshipping groups
		def ItemPriceInfo gInfo =Mock()
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "") >>hgGroup
		group.getShippingGroupId() >> "gId"
		
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def LTLDeliveryChargeCommerceItem cItem1 =Mock()
		def LTLAssemblyFeeCommerceItem cItem2 =Mock()
		def CommerceItem cItem3 =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		LTLDeliveryChargeVO vo = new LTLDeliveryChargeVO()
		LTLAssemblyFeeVO ltlVo = new LTLAssemblyFeeVO()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		cItem2.getId() >>"cItem2Id"
		cItem1.getId() >>"cItem1Id"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >> null
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == null
		1*bbbOrder.setSiteId(siteId.toString())
		1*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*bbbOrder.setPriceInfo(atgOrderPriceInfo)
		pParameters.get("ORDER_TYPE") == ""
		
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.isEmpty() == true
		
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		0*hgInfo.setFinalShipping(_)
		0*hgInfo.setRawShipping(_)
		0*hgInfo.setFinalSurcharge(_)
		0*gInfo.setAmount(_)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		// for populateItemInfo inside populateShippingGroups
		BBBSystemException e = thrown()
		1*mapper.logError("Error while retrieving product details for item [sku]",_)
	}
	
	def"transformRequestToOrder, when productItems Map is null"(){
		
		given:
		mapper = Spy()
		mapper.setOrderManager(mOrderManager)
		mapper.setTransactionManager(manager)
		mapper.setCatalogTools(cTools)
		mapper.setSiteRepository(rep)
		mapper.setShippingGroupManager(sgManager)
		mapper.setPricingTools(pTools)
		
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		Map<String, Object> pParameters = new HashMap()
		
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> ""
		1*cTools.getShippingMethod("") >> {}
		header.getSiteId() >> siteId
		1*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		1*bbbOrder.getPriceInfo() >> atgOrderPriceInfo
		2*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> ""
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populateTBSShippingPriceInRequest in populateshipping groups
		def ItemPriceInfo gInfo =Mock()
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "") >>hgGroup
		group.getShippingGroupId() >> "gId"
		
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def LTLDeliveryChargeCommerceItem cItem1 =Mock()
		def LTLAssemblyFeeCommerceItem cItem2 =Mock()
		def CommerceItem cItem3 =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		LTLDeliveryChargeVO vo = new LTLDeliveryChargeVO()
		LTLAssemblyFeeVO ltlVo = new LTLAssemblyFeeVO()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		cItem2.getId() >>"cItem2Id"
		cItem1.getId() >>"cItem1Id"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >> skuItem
		
		1*skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT) >>null
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == null
		1*bbbOrder.setSiteId(siteId.toString())
		1*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*bbbOrder.setPriceInfo(atgOrderPriceInfo)
		pParameters.get("ORDER_TYPE") == ""
		
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.isEmpty() == true
		
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		0*hgInfo.setFinalShipping(_)
		0*hgInfo.setRawShipping(_)
		0*hgInfo.setFinalSurcharge(_)
		0*gInfo.setAmount(_)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		// for populateItemInfo inside populateShippingGroups
		BBBSystemException e = thrown()
		1*mapper.logError("Error while retrieving product details for item [sku]",_)
	}
	
	def"transformRequestToOrder, when productItems Map is empty"(){
		
		given:
		mapper = Spy()
		mapper.setOrderManager(mOrderManager)
		mapper.setTransactionManager(manager)
		mapper.setCatalogTools(cTools)
		mapper.setSiteRepository(rep)
		mapper.setShippingGroupManager(sgManager)
		mapper.setPricingTools(pTools)
		
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		Map<String, Object> pParameters = new HashMap()
		
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> ""
		1*cTools.getShippingMethod("") >> {}
		header.getSiteId() >> siteId
		1*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		1*bbbOrder.getPriceInfo() >> atgOrderPriceInfo
		2*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> ""
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populateTBSShippingPriceInRequest in populateshipping groups
		def ItemPriceInfo gInfo =Mock()
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "") >>hgGroup
		group.getShippingGroupId() >> "gId"
		
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def LTLDeliveryChargeCommerceItem cItem1 =Mock()
		def LTLAssemblyFeeCommerceItem cItem2 =Mock()
		def CommerceItem cItem3 =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		LTLDeliveryChargeVO vo = new LTLDeliveryChargeVO()
		LTLAssemblyFeeVO ltlVo = new LTLAssemblyFeeVO()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		cItem2.getId() >>"cItem2Id"
		cItem1.getId() >>"cItem1Id"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >> skuItem
		1*skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT) >>productItems
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == null
		1*bbbOrder.setSiteId(siteId.toString())
		1*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*bbbOrder.setPriceInfo(atgOrderPriceInfo)
		pParameters.get("ORDER_TYPE") == ""
		
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.isEmpty() == true
		
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		0*hgInfo.setFinalShipping(_)
		0*hgInfo.setRawShipping(_)
		0*hgInfo.setFinalSurcharge(_)
		0*gInfo.setAmount(_)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		
		// for populateItemInfo inside populateShippingGroups
		BBBSystemException e = thrown()
		1*mapper.logError("Error while retrieving product details for item [sku]",_)	
	}
	
	def"transformRequestToOrder, when Commerce Exception is thrown in populateItemInfo"(){
		
		given:
		mapper = Spy()
		mapper.setOrderManager(mOrderManager)
		mapper.setTransactionManager(manager)
		mapper.setCatalogTools(cTools)
		mapper.setSiteRepository(rep)
		mapper.setShippingGroupManager(sgManager)
		mapper.setPricingTools(pTools)
		
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		Map<String, Object> pParameters = new HashMap()
		
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> ""
		1*cTools.getShippingMethod("") >> {}
		header.getSiteId() >> siteId
		1*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		1*bbbOrder.getPriceInfo() >> atgOrderPriceInfo
		2*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> ""
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populateTBSShippingPriceInRequest in populateshipping groups
		def ItemPriceInfo gInfo =Mock()
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "") >>hgGroup
		group.getShippingGroupId() >> "gId"
		
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def LTLDeliveryChargeCommerceItem cItem1 =Mock()
		def LTLAssemblyFeeCommerceItem cItem2 =Mock()
		def CommerceItem cItem3 =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		LTLDeliveryChargeVO vo = new LTLDeliveryChargeVO()
		LTLAssemblyFeeVO ltlVo = new LTLAssemblyFeeVO()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		cItem2.getId() >>"cItem2Id"
		cItem1.getId() >>"cItem1Id"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >> skuItem
		1*skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT) >>productItems
		
		1*orderTool.getDefaultCommerceItemType() >>"defaultType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "sku", 10.longValue(),"repositoryId","defaultType") >> null
		
		1*cTools.isSkuLtl(bbbOrder.getSiteId(), "sku") >> false
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == null
		1*bbbOrder.setSiteId(siteId.toString())
		1*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*bbbOrder.setPriceInfo(atgOrderPriceInfo)
		pParameters.get("ORDER_TYPE") == ""
		
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.isEmpty() == true
		
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		0*hgInfo.setFinalShipping(_)
		0*hgInfo.setRawShipping(_)
		0*hgInfo.setFinalSurcharge(_)
		0*gInfo.setAmount(_)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		
		// for populateItemInfo inside populateShippingGroups
		BBBSystemException e = thrown()
		1*mapper.logError("Error while adding commerce item [sku] to shipping group",_)
	}
	
	def"transformRequestToOrder, when Repository Exception is thrown in populateItemInfo"(){
		
		given:
		mapper = Spy()
		mapper.setOrderManager(mOrderManager)
		mapper.setTransactionManager(manager)
		mapper.setCatalogTools(cTools)
		mapper.setSiteRepository(rep)
		mapper.setShippingGroupManager(sgManager)
		mapper.setPricingTools(pTools)
		
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		Map<String, Object> pParameters = new HashMap()
		
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> ""
		1*cTools.getShippingMethod("") >> {}
		header.getSiteId() >> siteId
		1*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		1*bbbOrder.getPriceInfo() >> atgOrderPriceInfo
		2*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> ""
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populateTBSShippingPriceInRequest in populateshipping groups
		def ItemPriceInfo gInfo =Mock()
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "") >>hgGroup
		group.getShippingGroupId() >> "gId"
		
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def LTLDeliveryChargeCommerceItem cItem1 =Mock()
		def LTLAssemblyFeeCommerceItem cItem2 =Mock()
		def CommerceItem cItem3 =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		LTLDeliveryChargeVO vo = new LTLDeliveryChargeVO()
		LTLAssemblyFeeVO ltlVo = new LTLAssemblyFeeVO()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		cItem2.getId() >>"cItem2Id"
		cItem1.getId() >>"cItem1Id"
		1*cTools.getCatalogRepository() >> {throw new RepositoryException()}
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == null
		1*bbbOrder.setSiteId(siteId.toString())
		1*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*bbbOrder.setPriceInfo(atgOrderPriceInfo)
		pParameters.get("ORDER_TYPE") == ""
		
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.isEmpty() == true
		
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		0*hgInfo.setFinalShipping(_)
		0*hgInfo.setRawShipping(_)
		0*hgInfo.setFinalSurcharge(_)
		0*gInfo.setAmount(_)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		
		// for populateItemInfo inside populateShippingGroups
		BBBSystemException e = thrown()
		1*mapper.logError("Error while retrieving product details for item [sku]",_)
	}
	
	def"transformRequestToOrder, when BBBBusinessException is thrown while retreiving giftwrapVO in populateItemInfo"(){
		
		given:
		mapper = Spy()
		mapper.setOrderManager(mOrderManager)
		mapper.setTransactionManager(manager)
		mapper.setCatalogTools(cTools)
		mapper.setSiteRepository(rep)
		mapper.setShippingGroupManager(sgManager)
		mapper.setPricingTools(pTools)
		
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		Map<String, Object> pParameters = new HashMap()
		
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> ""
		1*cTools.getShippingMethod("") >> {}
		header.getSiteId() >> siteId
		1*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		1*bbbOrder.getPriceInfo() >> atgOrderPriceInfo
		2*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> ""
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populateTBSShippingPriceInRequest in populateshipping groups
		def ItemPriceInfo gInfo =Mock()
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "") >>hgGroup
		group.getShippingGroupId() >> "gId"
		hgGroup.getId() >> "hgId"
		
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >> skuItem
		1*skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT) >>productItems
		
		1*orderTool.getDefaultCommerceItemType() >>"defaultType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "sku", 10.longValue(),"repositoryId","defaultType") >> cItem
		1*cTools.isSkuLtl(bbbOrder.getSiteId(), "sku") >> false
		1*sgManager.getShippingGroupCommerceItemRelationship(bbbOrder, "cItemId","hgId") >> rel
		1*group.getGiftWrapRequired() >> true
		1*cTools.getWrapSkuDetails("tbs") >> {throw new BBBBusinessException("")}
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == null
		1*bbbOrder.setSiteId(siteId.toString())
		1*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*bbbOrder.setPriceInfo(atgOrderPriceInfo)
		pParameters.get("ORDER_TYPE") == ""
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.isEmpty() == true
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		0*hgInfo.setFinalShipping(_)
		0*hgInfo.setRawShipping(_)
		0*hgInfo.setFinalSurcharge(_)
		0*gInfo.setAmount(_)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		// for populateItemInfo inside populateShippingGroups
		BBBSystemException e = thrown()
		1*mapper.logError("Error while retrieving gift wrap item for site [tbs]",_)
	}
	
	def"transformRequestToOrder, when CommerceException is thrown while retreiving giftwrapVO in populateItemInfo"(){
		
		given:
		mapper = Spy()
		mapper.setOrderManager(mOrderManager)
		mapper.setTransactionManager(manager)
		mapper.setCatalogTools(cTools)
		mapper.setSiteRepository(rep)
		mapper.setShippingGroupManager(sgManager)
		mapper.setPricingTools(pTools)
		
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		Map<String, Object> pParameters = new HashMap()
		
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//transformordertorequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		def RepositoryItem site = Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> ""
		1*cTools.getShippingMethod("") >> {}
		header.getSiteId() >> siteId
		1*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> site
		1*bbbOrder.getPriceInfo() >> atgOrderPriceInfo
		2*site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY) >> "currency"
		1*header.getOrderIdentifier() >> ""
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, "false")
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populateTBSShippingPriceInRequest in populateshipping groups
		def ItemPriceInfo gInfo =Mock()
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "") >>hgGroup
		group.getShippingGroupId() >> "gId"
		hgGroup.getId() >> "hgId"
		
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >> skuItem
		1*skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT) >>productItems
		
		1*orderTool.getDefaultCommerceItemType() >>"defaultType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "sku", 10.longValue(),"repositoryId","defaultType") >> cItem
		1*cTools.isSkuLtl(bbbOrder.getSiteId(), "sku") >> false
		1*sgManager.getShippingGroupCommerceItemRelationship(bbbOrder, "cItemId","hgId") >> rel
		1*group.getGiftWrapRequired() >> true
		1*cTools.getWrapSkuDetails("tbs") >> giftVo
		giftVo.setWrapSkuId("wrapId")
		giftVo.setWrapProductId("wrapProId")
		1*orderTool.getGiftWrapCommerceItemType() >> "wrapType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "wrapId", 1, "wrapProId", "wrapType") >> {throw new CommerceException("")}
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == null
		1*bbbOrder.setSiteId(siteId.toString())
		1*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*bbbOrder.setPriceInfo(atgOrderPriceInfo)
		pParameters.get("ORDER_TYPE") == ""
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.isEmpty() == true
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		0*hgInfo.setFinalShipping(_)
		0*hgInfo.setRawShipping(_)
		0*hgInfo.setFinalSurcharge(_)
		0*gInfo.setAmount(_)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		// for populateItemInfo inside populateShippingGroups
		BBBSystemException e = thrown()
		1*mapper.logError("Error while adding gift wrap to shipping group [hgId]",_)
	}
	
	def"transformRequestToOrder, when transaction demarcation exception is thrown in createOrder"(){
		
		given:
		mapper =Spy()
		mapper.setOrderManager(mOrderManager)
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		Map<String, Object> pParameters = new HashMap()
		
		1*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		mapper.setTransactionManager(_) >> {throw new TransactionDemarcationException("")}
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		1*mapper.logError("TransactionDemarcationException in transformRequestToOrder() in BBBPricingWSMapper class. ", _)
	}
	
	def"transformRequestToOrder, when BBBBusinessException is thrown in validatePricingRequest"(){
		
		given:
		mapper =Spy()
		mapper.setOrderManager(mOrderManager)
		mapper.setTransactionManager(manager)
		mapper.setCatalogTools(cTools)
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		Map<String, Object> pParameters = new HashMap()
		
		wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//validaterequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> ""
		1*cTools.getShippingMethod("") >> {throw new BBBBusinessException("")}
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		BBBBusinessException e = thrown()
		order == null
	}
	
	def"transformRequestToOrder, when RepositoryException is thrown in PopulateOrderInfo"(){
		
		given:
		mapper = Spy()
		mapper.setOrderManager(mOrderManager)
		mapper.setTransactionManager(manager)
		mapper.setCatalogTools(cTools)
		mapper.setSiteRepository(rep)
		mapper.setShippingGroupManager(sgManager)
		mapper.setPricingTools(pTools)
		
		def PricingRequest wsRequest = Mock()
		def MessageHeader header =Mock()
		def Profile pProfile =Mock()
		Map<String, Object> pParameters = new HashMap()
		
		2*wsRequest.getHeader() >> header
		1*header.getOrderId() >>"Id"
		1*pProfile.getRepositoryId() >>"rId"
		
		//create order
		def BBBOrderTools orderTool = Mock()
		def BBBOrder bbbOrder = Mock()
		mOrderManager.getOrderTools() >> orderTool
		1*orderTool.getDefaultOrderType() >>"orderType"
		1*mOrderManager.createOrder("rId","PWSId", "orderType") >> bbbOrder
		
		//validaterequest
		def com.bedbathandbeyond.atg.ShippingList list =Mock()
		def com.bedbathandbeyond.atg.ShippingGroup group =Mock()
		def PricingRequestInfo pricingRequest =Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		wsRequest.getRequest() >> pricingRequest
		pricingRequest.getShippingGroups() >>list
		list.getShippingGroupArray() >> [group]
		group.getShippingMethod() >> ""
		1*cTools.getShippingMethod("") >> {}
		
		//for populateOrderInfo
		def RepositoryItem site =Mock()
		def OrderPriceInfo atgOrderPriceInfo =Mock()
		header.getSiteId() >> siteId
		1*rep.getItem(siteId.toString(), BBBCheckoutConstants.SITE_CONFIGURATION_ITEM) >> {throw new RepositoryException("")}
		bbbOrder.getPriceInfo() >> atgOrderPriceInfo
		header.getCurrencyCode() >> currency
		1*header.getOrderIdentifier() >> ""
		
		//for populateShippingGroups inside transform order to request
		Map<String,String> shippingGroupRelationMap = new HashMap()
		Map<ShippingGroupCommerceItemRelationship, Item> itemInfoMap = new HashMap()
		
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION, shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM, itemInfoMap)
		pParameters.put(BBBCheckoutConstants.TBS_PRICING_WEBSERVICE_ORDER, null)
		
		def BBBHardGoodShippingGroup hgGroup =Mock()
		def com.bedbathandbeyond.atg.ShippingPriceInfo priceInfo =Mock()
		def BBBShippingPriceInfo hgInfo =Mock()
		def Locale locale = new Locale("en_US")
		
		//for populate address method inside populate shipping groups
		def Address pAddress =Mock()
		group.getShippingAddress() >> pAddress
		1*pAddress.getAddressLine1() >>"addd1"
		1*pAddress.getAddressLine2() >> "add2"
		1*pAddress.getCity() >> "city"
		1*pAddress.getState() >>"state"
		1*pAddress.getZipCode() >>"zipcode"
		1*pAddress.getCountry() >> "country"
		1*pAddress.getDayPhone() >> "dayPhone"
		1*pAddress.getEvePhone() >>"evePhone"
		1*pAddress.getEmail() >>"email"
		//end populate address
		
		//for populateTBSShippingPriceInRequest in populateshipping groups
		def ItemPriceInfo gInfo =Mock()
		1*sgManager.createHardGoodShippingGroup(bbbOrder, _, "") >>hgGroup
		group.getShippingGroupId() >> "gId"
		
		//for populateItemInfo inside populateShippingGroups
		def ItemList shipmentItems = Mock()
		def Item item =Mock()
		def MutableRepository mRep =Mock()
		def RepositoryItem skuItem =Mock()
		def RepositoryItem rItem =Mock()
		def BBBCommerceItem cItem =Mock()
		def LTLDeliveryChargeCommerceItem cItem1 =Mock()
		def LTLAssemblyFeeCommerceItem cItem2 =Mock()
		def CommerceItem cItem3 =Mock()
		def ItemPriceInfo c3Info =Mock()
		def ShippingGroupCommerceItemRelationship rel =Mock()
		LTLDeliveryChargeVO vo = new LTLDeliveryChargeVO()
		LTLAssemblyFeeVO ltlVo = new LTLAssemblyFeeVO()
		GiftWrapVO giftVo = new GiftWrapVO()
		Set<RepositoryItem> productItems = new HashSet()
		productItems.add(rItem)
		
		rItem.getRepositoryId() >> "repositoryId"
		bbbOrder.getSiteId() >> "tbs"
		group.getItemList() >> shipmentItems
		shipmentItems.getItemArray() >> [item]
		item.getSku() >> "sku"
		item.getQuantity() >>10
		cItem.getId() >> "cItemId"
		cItem2.getId() >>"cItem2Id"
		cItem1.getId() >>"cItem1Id"
		
		1*cTools.getCatalogRepository() >> mRep
		mRep.getItem("sku", BBBCheckoutConstants.PROPERTY_SKU) >> skuItem
		1*skuItem.getPropertyValue(BBBCheckoutConstants.PROPERTY_PARENT_PRODUCT) >>productItems
		1*orderTool.getDefaultCommerceItemType() >>"defaultType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "sku", 10.longValue(),"repositoryId","defaultType") >> cItem
		
		1*cTools.isSkuLtl(bbbOrder.getSiteId(), "sku") >> true
		sgManager.getShippingGroupCommerceItemRelationship(bbbOrder, "cItemId","hgId") >> rel
		1*group.getGiftWrapRequired() >> true
		1*cTools.getWrapSkuDetails("tbs") >> giftVo
		giftVo.setWrapSkuId("wrapId")
		giftVo.setWrapProductId("wrapProId")
		1*orderTool.getGiftWrapCommerceItemType() >> "wrapType"
		1*mOrderManager.addAsSeparateItemToShippingGroup(bbbOrder, hgGroup, "wrapId", 1, "wrapProId", "wrapType") >> cItem3
		cItem3.getPriceInfo() >> c3Info
		//end populateItemInfo
		
		pTools.getDefaultLocale() >> locale
		//end populateshippinggroups
		
		//isPartiallyShipped starts
		final Enum PROCESS = Enum.forString("PROCESS")
		item.getStatus() >> PROCESS
		
		when:
		BBBOrder order =mapper.transformRequestToOrder(wsRequest,pProfile,pParameters)
		
		then:
		order == bbbOrder
		1*bbbOrder.setSiteId(siteId.toString())
		0*atgOrderPriceInfo.setCurrencyCode((String) site.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY))
		1*mapper.logDebug("Error occured while populating OrderInfo", _)
		1*bbbOrder.setPriceInfo(atgOrderPriceInfo)
		pParameters.get("ORDER_TYPE") == ""
		
		//for populateShipping groups inside transformordertoreq
		1*bbbOrder.removeAllShippingGroups()
		Map<String, ShippingPriceInfo> infoMap =pParameters.get(BBBCheckoutConstants.PARAM_SGCI_PRICE_INFO)
		infoMap.isEmpty() == true
		
		//for populateTBSShippingPriceInRequest inside populateShipping groups
		0*hgInfo.setFinalShipping(_)
		0*hgInfo.setRawShipping(_)
		0*hgInfo.setFinalSurcharge(_)
		0*gInfo.setAmount(_)
		Map<String,String> map =pParameters.get(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION)
		map.get(hgGroup.getId()) =="gId"
		
		// for populateItemInfo inside populateShippingGroups
		0*cItem.setLtlItem(true)
		0*cItem1.setLtlCommerceItemRelation("cItemId")
		0*cItem.setDeliveryItemId("cItem1Id")
		1*mOrderManager.updateOrder(bbbOrder)
		0*c3Info.setAmount(0.0)
		0*c3Info.setAmountIsFinal(true)
		
		//isPartiallyShipped
		pParameters.get(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED) == false	
	}
	
	
	def"populatePromotions,collects all promotion requested by coupon/promotionid, and add these promotion to active promotions of profile"(){
		
		given:
		def BBBPricingTools pTools =Mock()
		
		mapper =Spy()
		mapper.setPricingTools(pTools)
		mapper.setCatalogTools(cTools)
		
		def BBBOrder pBbbOrder =Mock()
		def PricingRequest pricingRequest =Mock()
		def PricingRequestInfo info =Mock()
		def Profile profile =Mock()
		def MessageHeader header =Mock()
		def CouponList coupons =Mock()
		def RepositoryItem rItem =Mock()
		def BBBPromotionTools proTools =Mock()
		def Coupon coupon =Mock()
		def Coupon coupon1 =Mock()
		def Coupon coupon2 =Mock()
		def RepositoryItem r1 =Mock()
		def RepositoryItem r2 =Mock()
		def RepositoryItem r3 =Mock()
		def RepositoryItem dataSource =Mock()
		def ClaimableTools claimableTools = Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
		def RepositoryItem promotionStatus =Mock()
		def RepositoryItem promotionStatus1 =Mock()
		def RepositoryItem promotionStatus2 =Mock()
		pricingRequest.getRequest() >> info
		
		int i =0
		int j =5
		int k =9
		int uses = 10
		
		mapper.isPartiallyShipped(info) >> false
		
		List<RepositoryItem> list = new ArrayList()
		list.add(rItem)
		
		1*pricingRequest.getHeader() >> header
		1*header.getCoupons() >> coupons
		pTools.getPromotionTools() >> proTools
		proTools.getActivePromotionsProperty() >> "property"
		1*profile.getPropertyValue("property") >> list
		
		Coupon.Type.Enum ATG_PROMOTION = Coupon.Type.Enum.forString("ATG_PROMOTION")
		Coupon.Type.Enum BBB_COUPON = Coupon.Type.Enum.forString("BBB_COUPON")
		
		coupons.getCouponArray() >> [coupon, coupon1, coupon2]
		coupon.getIdentifier() >>"c"
		coupon.getType() >> BBB_COUPON
		coupon1.getIdentifier() >>"c1"
		coupon1.getType() >>ATG_PROMOTION
		coupon2.getIdentifier() >>"c2"
		coupon2.getType() >>ATG_PROMOTION
		
		1*pTools.getPromotion(coupon.getIdentifier(),String.valueOf(coupon.getType())) >> [r1]
		r1.getPropertyValue("type") >> i
		
		1*pTools.getPromotion(coupon1.getIdentifier(),String.valueOf(coupon1.getType())) >> [r2]
		r2.getPropertyValue("type") >> j
		
		1*pTools.getPromotion(coupon2.getIdentifier(),String.valueOf(coupon2.getType())) >> [r3]
		r3.getPropertyValue("type") >> k
		
		1*cTools.getClaimableTools() >> claimableTools
		1*claimableTools.getClaimableItem("c") >> item
		1*proTools.getUsesProperty() >> "Uproperty"
		
		r1.getPropertyValue("Uproperty") >> uses
		profile.getDataSource() >> dataSource
		proTools.createPromotionStatus(profile.getDataSource(), r1, uses, item)>> promotionStatus

		when:
		Map<String, Collection<RepositoryItem>> map = mapper.populatePromotions( pBbbOrder, pricingRequest,  profile) 
		
		then:
		list.contains(promotionStatus) == true
		1*profile.setPropertyValue(proTools.getActivePromotionsProperty(),list)
		map.get(BBBCheckoutConstants.ITEM_PROMOTIONS) == [r1]
		map.get(BBBCheckoutConstants.SHIPPING_PROMOTIONS) == [r2]
		map.get(BBBCheckoutConstants.ORDER_PROMOTIONS) == [r3]
		0*mapper.logDebug("Promotion with Id : " + _+ " is not available in Promotion Repository");
	}
	
	
	def"populatePromotions,when promotion array is null"(){
		
		given:
		
		def BBBPricingTools pTools =Mock()
		def BBBOrder pBbbOrder =Mock()
		def PricingRequest pricingRequest =Mock()
		def PricingRequestInfo info =Mock()
		def Profile profile =Mock()
		def MessageHeader header =Mock()
		def CouponList coupons =Mock()
		def RepositoryItem rItem =Mock()
		def BBBPromotionTools proTools =Mock()
		def Coupon coupon =Mock()
		def RepositoryItem r1 =Mock()
		
		mapper =Spy()
		mapper.setPricingTools(pTools)
		mapper.setCatalogTools(cTools)
		pricingRequest.getRequest() >> info
		
		int i =0
		int uses = 10
		mapper.isPartiallyShipped(info) >> false
		List<RepositoryItem> list = new ArrayList()
		list.add(rItem)
		
		1*pricingRequest.getHeader() >> header
		1*header.getCoupons() >> coupons
		pTools.getPromotionTools() >> proTools
		proTools.getActivePromotionsProperty() >> "property"
		1*profile.getPropertyValue("property") >> list
		
		Coupon.Type.Enum ATG_PROMOTION = Coupon.Type.Enum.forString("ATG_PROMOTION")
		Coupon.Type.Enum BBB_COUPON = Coupon.Type.Enum.forString("BBB_COUPON")
		
		coupons.getCouponArray() >> [coupon]
		coupon.getIdentifier() >>"c"
		coupon.getType() >> BBB_COUPON
		
		1*pTools.getPromotion(coupon.getIdentifier(),String.valueOf(coupon.getType())) >>null

		when:
		Map<String, Collection<RepositoryItem>> map = mapper.populatePromotions( pBbbOrder, pricingRequest,  profile)
		
		then:
		0*profile.setPropertyValue(proTools.getActivePromotionsProperty(),list)
		map ==null
		1*mapper.logDebug("Promotion with Id : " + coupon.getIdentifier()+ " is not available in Promotion Repository")
		BBBBusinessException e = thrown()
		
	}
	
	def"populatePromotions,when promotion array is empty"(){
		
		given:
		
		def BBBPricingTools pTools =Mock()
		
		mapper =Spy()
		mapper.setPricingTools(pTools)
		mapper.setCatalogTools(cTools)
		
		def BBBOrder pBbbOrder =Mock()
		def PricingRequest pricingRequest =Mock()
		def PricingRequestInfo info =Mock()
		def Profile profile =Mock()
		def MessageHeader header =Mock()
		def CouponList coupons =Mock()
		def RepositoryItem rItem =Mock()
		def BBBPromotionTools proTools =Mock()
		def Coupon coupon =Mock()
		def RepositoryItem r1 =Mock()
		pricingRequest.getRequest() >> info
		
		int i =0
		int uses = 10
		mapper.isPartiallyShipped(info) >> false
	
		List<RepositoryItem> list = new ArrayList()
		list.add(rItem)
		1*pricingRequest.getHeader() >> header
		1*header.getCoupons() >> coupons
		pTools.getPromotionTools() >> proTools
		proTools.getActivePromotionsProperty() >> "property"
		1*profile.getPropertyValue("property") >> list
		
		Coupon.Type.Enum ATG_PROMOTION = Coupon.Type.Enum.forString("ATG_PROMOTION")
		Coupon.Type.Enum BBB_COUPON = Coupon.Type.Enum.forString("BBB_COUPON")
		
		coupons.getCouponArray() >> [coupon]
		coupon.getIdentifier() >>"c"
		coupon.getType() >> BBB_COUPON
		1*pTools.getPromotion(coupon.getIdentifier(),String.valueOf(coupon.getType())) >> []

		when:
		Map<String, Collection<RepositoryItem>> map = mapper.populatePromotions( pBbbOrder, pricingRequest,  profile)
		
		then:
		0*profile.setPropertyValue(proTools.getActivePromotionsProperty(),list)
		map ==null
		1*mapper.logDebug("Promotion with Id : " + coupon.getIdentifier()+ " is not available in Promotion Repository")
		BBBBusinessException e = thrown()
		
	}
	
	def"populatePromotions,when promotion statuses is null"(){
		
		given:
		def BBBPricingTools pTools =Mock()
		mapper =Spy()
		mapper.setPricingTools(pTools)
		mapper.setCatalogTools(cTools)
		
		def BBBOrder pBbbOrder =Mock()
		def PricingRequest pricingRequest =Mock()
		def PricingRequestInfo info =Mock()
		def Profile profile =Mock()
		def MessageHeader header =Mock()
		def CouponList coupons =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem dataSource =Mock()
		def ClaimableTools claimableTools =Mock()
		def RepositoryItem	promotionStatus = Mock()
		def BBBPromotionTools proTools =Mock()
		def Coupon coupon =Mock()
		def RepositoryItem r1 =Mock()
		pricingRequest.getRequest() >> info
		
		int i =0
		int uses = 10
		mapper.isPartiallyShipped(info) >> true
		
		1*pricingRequest.getHeader() >> header
		1*header.getCoupons() >> coupons
		pTools.getPromotionTools() >> proTools
		proTools.getActivePromotionsProperty() >> "property"
		1*profile.getPropertyValue("property") >> null
		
		final Coupon.Type.Enum ATG_PROMOTION = Coupon.Type.Enum.forString("ATG_PROMOTION")
		final Coupon.Type.Enum BBB_COUPON = Coupon.Type.Enum.forString("BBB_COUPON")
		
		coupons.getCouponArray() >> [coupon]
		coupon.getIdentifier() >>"c"
		coupon.getType() >> BBB_COUPON
		
		1*pTools.getPromotion(coupon.getIdentifier(),String.valueOf(coupon.getType())) >> [r1]
		r1.getPropertyValue("type") >> i
	
		1*cTools.getClaimableTools() >> claimableTools
		1*claimableTools.getClaimableItem("c") >> item
		proTools.getUsesProperty() >> "Uproperty"
		r1.getPropertyValue("Uproperty") >> uses
		
		profile.getDataSource() >> dataSource
		proTools.createPromotionStatus(profile.getDataSource(), r1, uses, item)>> promotionStatus

		when:
		Map<String, Collection<RepositoryItem>> map = mapper.populatePromotions( pBbbOrder, pricingRequest,  profile)
		
		then:
		1*profile.setPropertyValue(proTools.getActivePromotionsProperty(),_)
		1*mapper.vlogDebug("BBBPricingWSMapper.populatePromotions: There is no active promotions in profile so creating new promotion status list")
		map.get(BBBCheckoutConstants.ITEM_PROMOTIONS)  == []
		map.get(BBBCheckoutConstants.SHIPPING_PROMOTIONS) == []
		map.get(BBBCheckoutConstants.ORDER_PROMOTIONS) == []
	}
	
	def"populatePromotions,when coupons list is null"(){
		
		given:
		def BBBPricingTools pTools =Mock()
		mapper =Spy()
		mapper.setPricingTools(pTools)
		mapper.setCatalogTools(cTools)
		
		def BBBOrder pBbbOrder =Mock()
		def PricingRequest pricingRequest =Mock()
		def PricingRequestInfo info =Mock()
		def Profile profile =Mock()
		def MessageHeader header =Mock()
		def CouponList coupons =Mock()
		def BBBPromotionTools proTools =Mock()
		def RepositoryItem r1 =Mock()
		pricingRequest.getRequest() >> info
		
		int i =0
		mapper.isPartiallyShipped(info) >> true
		
		pricingRequest.getHeader() >> header
		header.getCoupons() >> null
		pTools.getPromotionTools() >> proTools
		proTools.getActivePromotionsProperty() >> "property"
		profile.getPropertyValue("property") >> null
		
		when:
		Map<String, Collection<RepositoryItem>> map = mapper.populatePromotions( pBbbOrder, pricingRequest,  profile)
		
		then:
		map.get(BBBCheckoutConstants.ITEM_PROMOTIONS)  == []
		map.get(BBBCheckoutConstants.SHIPPING_PROMOTIONS) == []
		map.get(BBBCheckoutConstants.ORDER_PROMOTIONS) == []
	}
	
	def"populatePromotions,when RepositoryException is thrown"(){
		
		given:
		def BBBPricingTools pTools =Mock()
		mapper =Spy()
		mapper.setPricingTools(pTools)
		mapper.setCatalogTools(cTools)
		
		def BBBOrder pBbbOrder =Mock()
		def PricingRequest pricingRequest =Mock()
		def PricingRequestInfo info =Mock()
		def Profile profile =Mock()
		def MessageHeader header =Mock()
		def CouponList coupons =Mock()
		def RepositoryItem rItem =Mock()
		def BBBPromotionTools proTools =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem dataSource =Mock()
		def ClaimableTools claimableTools =Mock()
		def RepositoryItem	promotionStatus = Mock()
		def Coupon coupon =Mock()
		def RepositoryItem r1 =Mock()
		pricingRequest.getRequest() >> info
		
		int i =9
		int uses = 10
		mapper.isPartiallyShipped(info) >> true
		
		pricingRequest.getHeader() >> header
		header.getCoupons() >> coupons
		pTools.getPromotionTools() >> proTools
		proTools.getActivePromotionsProperty() >> "property"
		profile.getPropertyValue("property") >> null
		
		final Coupon.Type.Enum ATG_PROMOTION = Coupon.Type.Enum.forString("ATG_PROMOTION")
		final Coupon.Type.Enum BBB_COUPON = Coupon.Type.Enum.forString("BBB_COUPON")
		
		coupons.getCouponArray() >> [coupon]
		coupon.getIdentifier() >>"c"
		coupon.getType() >> BBB_COUPON
		pTools.getPromotion(coupon.getIdentifier(),String.valueOf(coupon.getType())) >> [r1]
		r1.getPropertyValue("type") >> i
	
		cTools.getClaimableTools() >> claimableTools
		claimableTools.getClaimableItem("c") >> item
		proTools.getUsesProperty() >> "Uproperty"
		r1.getPropertyValue("Uproperty") >> uses
		
		profile.getDataSource() >> {throw new RepositoryException("")}
		
		when:
		Map<String, Collection<RepositoryItem>> map = mapper.populatePromotions(pBbbOrder,pricingRequest,profile)
		
		then:
		map == null
		1*mapper.vlogError("BBBPricingWSMapper.populatePromotions: Repository exception occured while creating new promotion status or getting coupon/promotion")
		BBBBusinessException e = thrown()
	}
	
	def "transformOrderToResponse, sets the value to response when getDeliverySurcharge, getDeliverySurchargePerQty,getDeliverySurchargeSaving,getDeliverySurchargeSku,getAssemblyFee, getAssemblyFeeSku is null in populateItemListInResponse"(){
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		3*requestHeader.getOrderIdentifier() >> "TBS"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		2*requestHeader.getCurrencyCode() >> currency
		2*requestHeader.getCallingAppCode() >> "appCode"
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> oldOrder
		1*oldOrder.getInternationalOrderId() >> "intOrderId"
		1*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		2*bbbOrderPriceInfo.getTotal() >> 50.0
		pTools1.round(50.0,2) >> 50.0 
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 600.0
		pTools1.round(600.0, 2) >> 600.0
		2*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 200.0
		pTools1.round(200.0, 2) >> 200.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> ["500.0"]
		pTools1.round(500.0, 2) >> 500.0
		pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		2*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 30.0
		pTools1.round(30.0, 2) >> 30.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> true
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		2*add.getAddress2() >> "add2"
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def ItemPriceInfo ecoInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def atg.core.util.Range range =Mock()
		def LTLAssemblyFeeCommerceItem ltlItem = Mock()
		def LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,requestItem)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		2*cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det]
		2*cItem.isLtlItem() >> true
		2*cItem.getDeliveryItemId() >> "cdId"
		2*cItem.getAssemblyItemId() >>  "caId"
		1*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> ltlItem
		1*pOrder.getCommerceItem(cItem.getDeliveryItemId()) >> deliveryCommerceItem
		1*deliveryCommerceItem.getPriceInfo() >> deliveryInfo
		deliveryInfo.getAmount() >> 300.0
		det.getQuantity() >> 10.0
		pTools1.round(10.0, 2) == 10.0
		
		pOrder.getSiteId() >> "siteId"
		pTools1.calculateItemSurchargeInSG(pOrder.getSiteId(), rel)  >> 90.0
		pTools1.round(90.0, 2) >> 90.0
		1*requestItem.getOriginalLineNumber() >> "2"
		1*requestItem.getStatus() >> status
		1*requestItem.getIsPcInd() >> true
		1*requestItem.getPcInfo() >> pcinf
		1*requestItem.getLineNumber() >> "5"
		1*cItem.getCatalogRefId() >> "skuid"
		2*cItem.getRegistryId() >> "registryId"
		
		//for populateItemAdjustmentListInResponse in populateItemListInResponse
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def RepositoryItem promotionItem = Mock()
		def RepositoryItem coupon = Mock()
		
		pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED , true)
		det.getAdjustments() >>  [adj]
		
		adj.getPricingModel() >> promotionItem
		1*promotionItem.getRepositoryId() >> "repId"
		adj.getCoupon() >> coupon
		1*coupon.getRepositoryId() >>"crepid"
		adj.getAdjustment() >> 60.0
		pTools1.round(60.0,2) >> 60.0
		1*promotionItem.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >>"type"
		1*promotionItem.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display"
		//end populateItemAdjustments
		
		1*info1.getListPrice() >> 90.0
		pTools1.round(90.0,2) >> 90.0
		1*det.getDetailedUnitPrice() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		2*cItem.getId() >>"cID"
		Map<String, String> ecoFeeMap = new HashMap()
		ecoFeeMap.put(cItem.getId(),"ecoFeeItemId")
		1*g1.getEcoFeeItemMap() >> ecoFeeMap
		1*pOrder.getCommerceItem("ecoFeeItemId") >>ecoFeeitem
		2*ecoFeeitem.getPriceInfo() >>ecoInfo
		1*ecoInfo.getListPrice() >> 120.0
		pTools1.round(120.0,2) >> 120.0
		1*ecoFeeitem.getCatalogRefId() >> "ecoSkuId"
		
		1*requestItem.getDeliverySurcharge() >> null
		pTools1.round(50.0,2) >> 50.0
		1*requestItem.getDeliverySurchargePerQty() >> null
		pTools1.round(10.0,2) >> 10.0
		1*requestItem.getDeliverySurchargeSaving() >> null
		pTools1.round(20.0,2) >> 20.0
		1*requestItem.getDeliverySurchargeSku() >> null
		1*requestItem.getAssemblyFee() >> null
		pTools1.round(200.0,2) >> 200.0
		1*requestItem.getAssemblyFeePerQty() >>null
		pTools1.round(60.0,2) >> 60.0
		1*requestItem.getAssemblyFeeSku() >> null
		//ends populateItemListInResponse
		
		//for populateShippingPriceInResponse in populateShippingGroupsInResponse
		def GiftWrapCommerceItem giftWrap =Mock()
		def ItemPriceInfo giftInfo =Mock()
		1*bbbShippingPriceInfo1.getFinalShipping() >> 60.0
		pTools1.round(60.0,2)  >> 60.0
		2*bbbShippingPriceInfo1.getRawShipping() >> 80.0
		pTools1.round(80.0,2)  >> 80.0
		1*g1.getGiftWrapCommerceItem() >> giftWrap
		giftWrap.getPriceInfo() >>giftInfo
		1*giftInfo.getAmount() >> 250.0
		pTools1.round(250.0,2)  >> 250.0
		
		//for populateShippingAdjustmentListInResponse in populateShippingPriceInResponse
		def PricingAdjustment bbbAdjustment =Mock()
		def RepositoryItem bbbrep =Mock()
		def RepositoryItem bbbcoupon = Mock()
		
		bbbShippingPriceInfo1.getAdjustments() >> [bbbAdjustment]
		bbbAdjustment.getPricingModel() >> bbbrep
		bbbrep.getRepositoryId() >> "bbbrepId"
		bbbAdjustment.getAdjustment() >> 350.0
		pTools1.round(350.0, 2) >> 350.0
		bbbAdjustment.getCoupon() >> bbbcoupon
		bbbcoupon.getRepositoryId() >> "bcrepId"
		bbbrep.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >> "type1"
		bbbrep.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display1"
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == "TBS"
		head.getCallingAppCode() == "appCode"
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == 50.0
		orderPriceInfo.getTotalDeliverySurcharge() == 600.0
		orderPriceInfo.getTotalAssemblyFee() == 200.0
		orderPriceInfo.getDeliveryThreshold() ==500.0
		orderPriceInfo.getTotalDeliverySurchargeSaving() == 100.0
		orderPriceInfo.getTotalShipping() == 20.0
		orderPriceInfo.getTotalSurcharge() == 30.0
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()  
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup =group.get(0)
		shippingGroup.getShippingGroupId() == "1"
		shippingGroup.getShippingMethod() == "g1Method"
		
		//for populateAddressInResponse
		com.bedbathandbeyond.atg.Address address=shippingGroup.getShippingAddress() 
		address.getAddressLine1() =="add1"
		address.getCity() == "city"
		address.getState() == "state"
		address.getZipCode() == "postalCode"
		address.getCountry() == "country"
		
		//for populateItemListInResponse
		com.bedbathandbeyond.atg.ItemList itemList =shippingGroup.getItemList()
		List<com.bedbathandbeyond.atg.Item> itemArray = itemList.getItemArray()
		com.bedbathandbeyond.atg.Item item = itemArray.get(0)
		item.getOriginalLineNumber() == "2"
		item.getStatus() != null
		item.getIsPcInd() == true
		item.getPcInfo() != null
		item.getLineNumber()== "5"
		item.getSku() == "skuid"
		item.getQuantity() == 10
		item.getSurcharge() == 90.0
		item.getRegistryId() == "registryId"
		item.getPrice() == 90.0
		item.getFinalPrice() == 0.0
		item.getEcoFee() == 120.0
		item.getEcoFeeSku() =="ecoSkuId"
		item.getDeliverySurcharge() == null
		item.getDeliverySurchargePerQty() == null
		item.getDeliverySurchargeSaving() ==null
		item.getDeliverySurchargeSku() == null
		item.getAssemblyFee() == null
		item.getAssemblyFeePerQty() ==null
		item.getAssemblyFeeSku() == null
		
		//for populateItemAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList adjList = item.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> adjArray = adjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment adjustment= adjArray.get(0)
		adjustment.getAtgPromotionId() == "repId"
		adjustment.getCouponCode() == "crepid"
		adjustment.getDiscountAmount() == 60.0
		adjustment.getPromotionType() == "type"
		adjustment.getPromotionDescription() == "display"
		
		//for populateShippingPriceInResponse
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = shippingGroup.getShippingPrice()
		shippingPriceInfo.getShippingAmount() == 60.0
		shippingPriceInfo.getShippingRawAmount() == 80.0
		shippingPriceInfo.getSurcharge() == 30.0
		shippingPriceInfo.getGiftWrapFee() == 250.0
		
		//for populateShippingAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList priceAdjList = shippingPriceInfo.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> priceAdjArray = priceAdjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment priceAdjustment =priceAdjArray.get(0)
		priceAdjustment.getAtgPromotionId() == "bbbrepId"
		priceAdjustment.getCouponCode() == "bcrepId"
		priceAdjustment.getDiscountAmount() == 350.0
		priceAdjustment.getPromotionType() == "type1"
		priceAdjustment.getPromotionDescription() == "display1"
	}
	
	def "transformOrderToResponse, sets the value to response"(){
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		3*requestHeader.getOrderIdentifier() >> "TBS"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		2*requestHeader.getCurrencyCode() >> currency
		2*requestHeader.getCallingAppCode() >> "appCode"
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> oldOrder
		1*oldOrder.getInternationalOrderId() >> "intOrderId"
		1*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		2*bbbOrderPriceInfo.getTotal() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 600.0
		pTools1.round(600.0, 2) >> 600.0
		2*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 200.0
		pTools1.round(200.0, 2) >> 200.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> ["500.0"]
		pTools1.round(500.0, 2) >> 500.0
		pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		2*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 30.0
		pTools1.round(30.0, 2) >> 30.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> true
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		2*add.getAddress2() >> "add2"
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def ItemPriceInfo ecoInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def atg.core.util.Range range =Mock()
		def LTLAssemblyFeeCommerceItem ltlItem = Mock()
		def LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,requestItem)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		2*cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det]
		2*cItem.isLtlItem() >> true
		2*cItem.getDeliveryItemId() >> "cdId"
		2*cItem.getAssemblyItemId() >>  "caId"
		1*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> ltlItem
		1*pOrder.getCommerceItem(cItem.getDeliveryItemId()) >> deliveryCommerceItem
		1*deliveryCommerceItem.getPriceInfo() >> deliveryInfo
		deliveryInfo.getAmount() >> 300.0
		det.getQuantity() >> 10.0
		pTools1.round(10.0, 2) == 10.0
		
		pOrder.getSiteId() >> "siteId"
		pTools1.calculateItemSurchargeInSG(pOrder.getSiteId(), rel)  >> 90.0
		pTools1.round(90.0, 2) >> 90.0
		1*requestItem.getOriginalLineNumber() >> "2"
		1*requestItem.getStatus() >> status
		1*requestItem.getIsPcInd() >> true
		1*requestItem.getPcInfo() >> pcinf
		1*requestItem.getLineNumber() >> "5"
		1*cItem.getCatalogRefId() >> "skuid"
		2*cItem.getRegistryId() >> "registryId"
		
		//for populateItemAdjustmentListInResponse in populateItemListInResponse
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def RepositoryItem promotionItem = Mock()
		def RepositoryItem coupon = Mock()
		
		pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED , true)
		det.getAdjustments() >>  [adj]
		
		adj.getPricingModel() >> promotionItem
		1*promotionItem.getRepositoryId() >> "repId"
		adj.getCoupon() >> coupon
		1*coupon.getRepositoryId() >>"crepid"
		adj.getAdjustment() >> 60.0
		pTools1.round(60.0,2) >> 60.0
		1*promotionItem.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >>"type"
		1*promotionItem.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display"
		//end populateItemAdjustments
		
		1*info1.getListPrice() >> 90.0
		pTools1.round(90.0,2) >> 90.0
		1*det.getDetailedUnitPrice() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		2*cItem.getId() >>"cID"
		Map<String, String> ecoFeeMap = new HashMap()
		ecoFeeMap.put(cItem.getId(),"ecoFeeItemId")
		1*g1.getEcoFeeItemMap() >> ecoFeeMap
		1*pOrder.getCommerceItem("ecoFeeItemId") >>ecoFeeitem
		2*ecoFeeitem.getPriceInfo() >>ecoInfo
		1*ecoInfo.getListPrice() >> 120.0
		pTools1.round(120.0,2) >> 120.0
		1*ecoFeeitem.getCatalogRefId() >> "ecoSkuId"
		
		2*requestItem.getDeliverySurcharge() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		2*requestItem.getDeliverySurchargePerQty() >> 10.0
		pTools1.round(10.0,2) >> 10.0
		2*requestItem.getDeliverySurchargeSaving() >> 20.0
		pTools1.round(20.0,2) >> 20.0
		2*requestItem.getDeliverySurchargeSku() >> "surchargeSku"
		2*requestItem.getAssemblyFee() >> 200.0
		pTools1.round(200.0,2) >> 200.0
		2*requestItem.getAssemblyFeePerQty() >> 60.0
		pTools1.round(60.0,2) >> 60.0
		2*requestItem.getAssemblyFeeSku() >> "assemblySku"
		//ends populateItemListInResponse
		
		//for populateShippingPriceInResponse in populateShippingGroupsInResponse
		def GiftWrapCommerceItem giftWrap =Mock()
		def ItemPriceInfo giftInfo =Mock()
		1*bbbShippingPriceInfo1.getFinalShipping() >> 60.0
		pTools1.round(60.0,2)  >> 60.0
		2*bbbShippingPriceInfo1.getRawShipping() >> 80.0
		pTools1.round(80.0,2)  >> 80.0
		1*g1.getGiftWrapCommerceItem() >> giftWrap
		giftWrap.getPriceInfo() >>giftInfo
		1*giftInfo.getAmount() >> 250.0
		pTools1.round(250.0,2)  >> 250.0
		
		//for populateShippingAdjustmentListInResponse in populateShippingPriceInResponse
		def PricingAdjustment bbbAdjustment =Mock()
		def RepositoryItem bbbrep =Mock()
		def RepositoryItem bbbcoupon = Mock()
		
		bbbShippingPriceInfo1.getAdjustments() >> [bbbAdjustment]
		bbbAdjustment.getPricingModel() >> bbbrep
		bbbrep.getRepositoryId() >> "bbbrepId"
		bbbAdjustment.getAdjustment() >> 350.0
		pTools1.round(350.0, 2) >> 350.0
		bbbAdjustment.getCoupon() >> bbbcoupon
		bbbcoupon.getRepositoryId() >> "bcrepId"
		bbbrep.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >> "type1"
		bbbrep.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display1"
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == "TBS"
		head.getCallingAppCode() == "appCode"
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == 50.0
		orderPriceInfo.getTotalDeliverySurcharge() == 600.0
		orderPriceInfo.getTotalAssemblyFee() == 200.0
		orderPriceInfo.getDeliveryThreshold() ==500.0
		orderPriceInfo.getTotalDeliverySurchargeSaving() == 100.0
		orderPriceInfo.getTotalShipping() == 20.0
		orderPriceInfo.getTotalSurcharge() == 30.0
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup =group.get(0)
		shippingGroup.getShippingGroupId() == "1"
		shippingGroup.getShippingMethod() == "g1Method"
		
		//for populateAddressInResponse
		com.bedbathandbeyond.atg.Address address=shippingGroup.getShippingAddress()
		address.getAddressLine1() =="add1"
		address.getCity() == "city"
		address.getState() == "state"
		address.getZipCode() == "postalCode"
		address.getCountry() == "country"
		
		//for populateItemListInResponse
		com.bedbathandbeyond.atg.ItemList itemList =shippingGroup.getItemList()
		List<com.bedbathandbeyond.atg.Item> itemArray = itemList.getItemArray()
		com.bedbathandbeyond.atg.Item item = itemArray.get(0)
		item.getOriginalLineNumber() == "2"
		item.getStatus() != null
		item.getIsPcInd() == true
		item.getPcInfo() != null
		item.getLineNumber()== "5"
		item.getSku() == "skuid"
		item.getQuantity() == 10
		item.getSurcharge() == 90.0
		item.getRegistryId() == "registryId"
		item.getPrice() == 90.0
		item.getFinalPrice() == 0.0
		item.getEcoFee() == 120.0
		item.getEcoFeeSku() =="ecoSkuId"
		item.getDeliverySurcharge() == 50.0
		item.getDeliverySurchargePerQty() == 10.0
		item.getDeliverySurchargeSaving() ==20.0
		item.getDeliverySurchargeSku() == "surchargeSku"
		item.getAssemblyFee() == 200.0
		item.getAssemblyFeePerQty() ==60.0
		item.getAssemblyFeeSku() == "assemblySku"
		
		//for populateItemAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList adjList = item.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> adjArray = adjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment adjustment= adjArray.get(0)
		adjustment.getAtgPromotionId() == "repId"
		adjustment.getCouponCode() == "crepid"
		adjustment.getDiscountAmount() == 60.0
		adjustment.getPromotionType() == "type"
		adjustment.getPromotionDescription() == "display"
		
		//for populateShippingPriceInResponse
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = shippingGroup.getShippingPrice()
		shippingPriceInfo.getShippingAmount() == 60.0
		shippingPriceInfo.getShippingRawAmount() == 80.0
		shippingPriceInfo.getSurcharge() == 30.0
		shippingPriceInfo.getGiftWrapFee() == 250.0
		
		//for populateShippingAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList priceAdjList = shippingPriceInfo.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> priceAdjArray = priceAdjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment priceAdjustment =priceAdjArray.get(0)
		priceAdjustment.getAtgPromotionId() == "bbbrepId"
		priceAdjustment.getCouponCode() == "bcrepId"
		priceAdjustment.getDiscountAmount() == 350.0
		priceAdjustment.getPromotionType() == "type1"
		priceAdjustment.getPromotionDescription() == "display1"
	}
	
	def "transformOrderToResponse, currency code and callingAppCode is null(populateResponseHeader), old order is null(populatePricingResponse) , requestItem is null in populateItemListInResponse"(){
		
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		1*requestHeader.getOrderIdentifier() >> ""
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		1*requestHeader.getCurrencyCode() >> null
		1*requestHeader.getCallingAppCode() >> null
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> null
		0*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		1*bbbOrderPriceInfo.getTotal() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		1*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> ["0.0"]
		pTools1.round(0.0, 2) >> 0.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		2*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_ITEM_LINE_NUMBER,1)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> false
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		1*add.getAddress2() >> ""
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def ItemPriceInfo ecoInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def atg.core.util.Range range =Mock()
		def LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,null)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		2*cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det]
		2*cItem.isLtlItem() >> true
		2*cItem.getDeliveryItemId() >> "cdId"
		2*cItem.getAssemblyItemId() >>  ""
		0*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> null
		1*pOrder.getCommerceItem(cItem.getDeliveryItemId()) >> deliveryCommerceItem
		deliveryCommerceItem.getPriceInfo() >> deliveryInfo
		deliveryInfo.getAmount() >> 300.0
		det.getQuantity() >> 10.0
		pTools1.round(10.0, 2) == 10.0
		
		pOrder.getSiteId() >> "siteId"
		pTools1.calculateItemSurchargeInSG(pOrder.getSiteId(), rel)  >> 90.0
		pTools1.round(90.0, 2) >> 90.0
		1*cItem.getCatalogRefId() >> "skuid"
		1*cItem.getRegistryId() >> ""
		
		//for populateItemAdjustmentListInResponse in populateItemListInResponse
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def RepositoryItem promotionItem = Mock()
		def RepositoryItem coupon = Mock()
		
		pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED , false)
		det.getAdjustments() >>  []
		0*coupon.getRepositoryId() >>"crepid"
		0*promotionItem.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >>"type"
		0*promotionItem.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display"
		//end populateItemAdjustments
		
		1*info1.getListPrice() >> 90.0
		pTools1.round(90.0,2) >> 90.0
		1*det.getDetailedUnitPrice() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		2*cItem.getId() >>"cID"
		Map<String, String> ecoFeeMap = new HashMap()
		ecoFeeMap.put(cItem.getId(),"ecoFeeItemId")
		1*g1.getEcoFeeItemMap() >> ecoFeeMap
		1*pOrder.getCommerceItem("ecoFeeItemId") >>ecoFeeitem
		1*ecoFeeitem.getPriceInfo() >>null
		//ends populateItemListInResponse
		
		//for populateLTLItemInfo
		cItem.getQuantity() >> 15
		pTools1.round(20.0) >> 20.0
		pTools1.round(120.0,2)  >> 120.0
		pTools1.round(-120.0,2)  >> -120.0
		pTools1.round(180.0,2)  >> 180.0
		deliveryCommerceItem.getCatalogRefId() >>"delRefId"
		pTools1.round(-180.0,2)  >> -180.0
		//ends populateLTLItemInfo	
		
		//for populateShippingPriceInResponse in populateShippingGroupsInResponse
		def GiftWrapCommerceItem giftWrap =Mock()
		def ItemPriceInfo giftInfo =Mock()
		1*bbbShippingPriceInfo1.getFinalShipping() >> 60.0
		pTools1.round(60.0,2)  >> 60.0
		2*bbbShippingPriceInfo1.getRawShipping() >> 80.0
		pTools1.round(80.0,2)  >> 80.0
		1*g1.getGiftWrapCommerceItem() >> giftWrap
		giftWrap.getPriceInfo() >>giftInfo
		1*giftInfo.getAmount() >> 250.0
		pTools1.round(250.0,2)  >> 250.0
		
		//for populateShippingAdjustmentListInResponse in populateShippingPriceInResponse
		def PricingAdjustment bbbAdjustment =Mock()
		def RepositoryItem bbbrep =Mock()
		def RepositoryItem bbbcoupon = Mock()
		
		bbbShippingPriceInfo1.getAdjustments() >> [bbbAdjustment]
		bbbAdjustment.getPricingModel() >> bbbrep
		bbbrep.getRepositoryId() >> "bbbrepId"
		bbbAdjustment.getAdjustment() >> 350.0
		pTools1.round(350.0, 2) >> 350.0
		bbbAdjustment.getCoupon() >> bbbcoupon
		bbbcoupon.getRepositoryId() >> "bcrepId"
		bbbrep.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >> "type1"
		bbbrep.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display1"
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == null
		head.getCallingAppCode() == null
		head.getCurrencyCode() == null
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
		0*mapper.vlogDebug("International Order updating shipping prices")
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == null
		orderPriceInfo.getTotalDeliverySurcharge() == null
		orderPriceInfo.getTotalAssemblyFee() == null
		orderPriceInfo.getDeliveryThreshold() ==null
		orderPriceInfo.getTotalDeliverySurchargeSaving() == null
		orderPriceInfo.getTotalShipping() == 20.0
		orderPriceInfo.getTotalSurcharge() == null
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup =group.get(0)
		shippingGroup.getShippingGroupId() == "1"
		shippingGroup.getShippingMethod() == "g1Method"
		
		//for populateAddressInResponse
		com.bedbathandbeyond.atg.Address address=shippingGroup.getShippingAddress()
		address.getAddressLine1() =="add1"
		address.getAddressLine2() == null
		address.getCity() == "city"
		address.getState() == "state"
		address.getZipCode() == "postalCode"
		address.getCountry() == "country"
		
		//for populateItemListInResponse
		com.bedbathandbeyond.atg.ItemList itemList =shippingGroup.getItemList()
		List<com.bedbathandbeyond.atg.Item> itemArray = itemList.getItemArray()
		com.bedbathandbeyond.atg.Item item = itemArray.get(1)
		item.getOriginalLineNumber() == "1"
		item.getStatus() == null
		item.getIsPcInd() == false
		item.getPcInfo() == null
		item.getLineNumber()== ""
		item.getSku() == "skuid"
		item.getQuantity() == 9
		item.getSurcharge() == 90.0
		item.getRegistryId() == null
		item.getPrice() == 90.0
		item.getFinalPrice() == 0.0
		item.getEcoFee() == null
		item.getEcoFeeSku() ==null
		item.getDeliverySurcharge() == 180.0
		item.getDeliverySurchargePerQty() == 20
		item.getDeliverySurchargeSaving() == -180.0
		item.getDeliverySurchargeSku() == "delRefId"
		item.getAssemblyFee() == null
		item.getAssemblyFeePerQty() ==null
		item.getAssemblyFeeSku() == null
		
		//populateLTLItemInfo
		item.getIsLTLSku() == true
		com.bedbathandbeyond.atg.Item item1 = itemArray.get(0)
		item1.getIsLTLSku() == true
		item1.getDeliverySurchargePerQty() == 120.0
		item1.getDeliverySurcharge() == 120.0
		item1.getDeliverySurchargeSaving() == -120.0
		item1.getOriginalLineNumber() == "1"
		item1.getStatus() == null
		item1.getLineNumber() == "1"
		item1.getSku() == "skuid"
		item1.getQuantity() == 1
		item1.getPrice() == 90.0
		item1.getFinalPrice() == 0.0
		
		//for populateItemAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList adjList = item.getAdjustmentList()
		adjList == null
		
		//for populateShippingPriceInResponse
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = shippingGroup.getShippingPrice()
		shippingPriceInfo.getShippingAmount() == 60.0
		shippingPriceInfo.getShippingRawAmount() == 80.0
		shippingPriceInfo.getSurcharge() == null
		shippingPriceInfo.getGiftWrapFee() == 250.0
		
		//for populateShippingAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList priceAdjList = shippingPriceInfo.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> priceAdjArray = priceAdjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment priceAdjustment =priceAdjArray.get(0)
		priceAdjustment.getAtgPromotionId() == "bbbrepId"
		priceAdjustment.getCouponCode() == "bcrepId"
		priceAdjustment.getDiscountAmount() == 350.0
		priceAdjustment.getPromotionType() == "type1"
		priceAdjustment.getPromotionDescription() == "display1"
	}
	
	def "transformOrderToResponse, when populateLTLItemInfo is called with assemblyItemId not empty"(){
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		2*requestHeader.getOrderIdentifier() >> "ABC"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		1*requestHeader.getCurrencyCode() >> null
		1*requestHeader.getCallingAppCode() >> null
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> oldOrder
		oldOrder.getInternationalOrderId() >> ""
		0*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		1*bbbOrderPriceInfo.getTotal() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		1*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> {throw new BBBSystemException("")}
		pTools1.round(0.0, 2) >> 0.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		2*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_ITEM_LINE_NUMBER,1)
		mapper.isPartiallyShipped(pPricingRequestInfo) >> false
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		1*add.getAddress2() >> ""
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def ItemPriceInfo ecoInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def atg.core.util.Range range =Mock()
		def LTLAssemblyFeeCommerceItem ltlItem = Mock()
		def LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,null)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		2*cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det]
		2*cItem.isLtlItem() >> true
		2*cItem.getDeliveryItemId() >> "cdId"
		2*cItem.getAssemblyItemId() >>  "caId"
		1*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> ltlItem
		1*pOrder.getCommerceItem(cItem.getDeliveryItemId()) >> deliveryCommerceItem
		deliveryCommerceItem.getPriceInfo() >> deliveryInfo
		deliveryInfo.getAmount() >> 300.0
		det.getQuantity() >> 10.0
		pTools1.round(10.0, 2) == 10.0
		
		pOrder.getSiteId() >> "siteId"
		pTools1.calculateItemSurchargeInSG(pOrder.getSiteId(), rel)  >> 90.0
		pTools1.round(90.0, 2) >> 90.0
		cItem.getCatalogRefId() >> "skuid"
		1*cItem.getRegistryId() >> ""
		
		//for populateItemAdjustmentListInResponse in populateItemListInResponse
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def RepositoryItem promotionItem = Mock()
		def RepositoryItem coupon = Mock()
		
		pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED , true)
		det.getAdjustments() >>  [adj]
		
		adj.getPricingModel() >> promotionItem
		1*promotionItem.getRepositoryId() >> "repId"
		adj.getCoupon() >> null
		adj.getAdjustment() >> 60.0
		pTools1.round(60.0,2) >> 60.0
		1*promotionItem.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >>"type"
		1*promotionItem.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display"
		//end populateItemAdjustments
		
		1*info1.getListPrice() >> 90.0
		pTools1.round(90.0,2) >> 90.0
		1*det.getDetailedUnitPrice() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		2*cItem.getId() >>"cID"
		Map<String, String> ecoFeeMap = new HashMap()
		ecoFeeMap.put(cItem.getId(),"ecoFee")
		1*g1.getEcoFeeItemMap() >> ecoFeeMap
		1*pOrder.getCommerceItem("ecoFee") >> null
		//ends populateItemListInResponse
		
		//for populateLTLItemInfo
		
		def ItemPriceInfo ltlInfo =Mock()
		cItem.getQuantity() >> 15
		pTools1.round(20.0) >> 20.0
		pTools1.round(120.0,2)  >> 120.0
		pTools1.round(-120.0,2)  >> -120.0
		pTools1.round(180.0,2)  >> 180.0
		deliveryCommerceItem.getCatalogRefId() >>"delRefId"
		pTools1.round(-180.0,2)  >> -180.0
		
		ltlItem.getPriceInfo() >> ltlInfo
		ltlInfo.getListPrice() >> 50.0
		pTools1.round(50.0,2)  >> 50.0
		ltlItem.getCatalogRefId() >>"ltlId"
		pTools1.round(450.0,2)  >> 450.0
		//ends populateLTLItemInfo
		
		//for populateShippingPriceInResponse in populateShippingGroupsInResponse
		def GiftWrapCommerceItem giftWrap =Mock()
		def ItemPriceInfo giftInfo =Mock()
		1*bbbShippingPriceInfo1.getFinalShipping() >> 60.0
		pTools1.round(60.0,2)  >> 60.0
		1*bbbShippingPriceInfo1.getRawShipping() >> 0.0
		pTools1.round(0.0,2)  >> 0.0
		1*g1.getGiftWrapCommerceItem() >> null
		
		//for populateShippingAdjustmentListInResponse in populateShippingPriceInResponse
		def PricingAdjustment bbbAdjustment =Mock()
		def RepositoryItem bbbrep =Mock()
		def RepositoryItem bbbcoupon = Mock()
		
		bbbShippingPriceInfo1.getAdjustments() >> [bbbAdjustment]
		bbbAdjustment.getPricingModel() >> bbbrep
		bbbrep.getRepositoryId() >> "bbbrepId"
		bbbAdjustment.getAdjustment() >> 350.0
		pTools1.round(350.0, 2) >> 350.0
		bbbAdjustment.getCoupon() >> bbbcoupon
		bbbcoupon.getRepositoryId() >> "bcrepId"
		bbbrep.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >> "type1"
		bbbrep.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display1"
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == null
		head.getCallingAppCode() == null
		head.getCurrencyCode() == null
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
		0*mapper.vlogDebug("International Order updating shipping prices")
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == null
		orderPriceInfo.getTotalDeliverySurcharge() == null
		orderPriceInfo.getTotalAssemblyFee() == null
		orderPriceInfo.getDeliveryThreshold() ==null
		orderPriceInfo.getTotalDeliverySurchargeSaving() == null
		orderPriceInfo.getTotalShipping() == 20.0
		orderPriceInfo.getTotalSurcharge() == null
		1*mapper.logError("Error getting thresholdAmount from config keys", _)
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup =group.get(0)
		shippingGroup.getShippingGroupId() == "1"
		shippingGroup.getShippingMethod() == "g1Method"
		
		//for populateAddressInResponse
		com.bedbathandbeyond.atg.Address address=shippingGroup.getShippingAddress()
		address.getAddressLine1() =="add1"
		address.getAddressLine2() == null
		address.getCity() == "city"
		address.getState() == "state"
		address.getZipCode() == "postalCode"
		address.getCountry() == "country"
		
		//for populateItemListInResponse
		com.bedbathandbeyond.atg.ItemList itemList =shippingGroup.getItemList()
		List<com.bedbathandbeyond.atg.Item> itemArray = itemList.getItemArray()
		com.bedbathandbeyond.atg.Item item = itemArray.get(1)
		item.getOriginalLineNumber() == "1"
		item.getStatus() == null
		item.getIsPcInd() == false
		item.getPcInfo() == null
		item.getLineNumber()== ""
		item.getSku() == "skuid"
		item.getQuantity() == 9
		item.getSurcharge() == 90.0
		item.getRegistryId() == null
		item.getPrice() == 90.0
		item.getFinalPrice() == 0.0
		item.getEcoFee() == null
		item.getEcoFeeSku() ==null
		item.getDeliverySurcharge() == 180.0
		item.getDeliverySurchargePerQty() == 20
		item.getDeliverySurchargeSaving() == -180.0
		item.getDeliverySurchargeSku() == "delRefId"
		item.getAssemblyFee() == 450.0
		item.getAssemblyFeePerQty() ==50.0
		item.getAssemblyFeeSku() == "ltlId"
		item.getIsAssemblyRequested() == true
		
		//populateLTLItemInfo
		item.getIsLTLSku() == true
		com.bedbathandbeyond.atg.Item item1 = itemArray.get(0)
		item1.getIsLTLSku() == true
		item1.getDeliverySurchargePerQty() == 120.0
		item1.getDeliverySurcharge() == 120.0
		item1.getDeliverySurchargeSaving() == -120.0
		item1.getOriginalLineNumber() == "1"
		item1.getStatus() == null
		item1.getLineNumber() == "1"
		item1.getSku() == "skuid"
		item1.getQuantity() == 1
		item1.getPrice() == 90.0
		item1.getFinalPrice() == 0.0
		item1.getIsAssemblyRequested()== true
		item1.getAssemblyFeePerQty() ==50.0
		item1.getAssemblyFee() ==50.0
		item1.getAssemblyFeeSku() == "ltlId"
		
		//for populateItemAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList adjList = item.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> adjArray = adjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment adjustment= adjArray.get(0)
		adjustment.getAtgPromotionId() == "repId"
		adjustment.getCouponCode() == null
		adjustment.getDiscountAmount() == 60.0
		adjustment.getPromotionType() == "type"
		adjustment.getPromotionDescription() == "display"
		
		//for populateShippingPriceInResponse
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = shippingGroup.getShippingPrice()
		shippingPriceInfo.getShippingAmount() == 60.0
		shippingPriceInfo.getShippingRawAmount() == null
		shippingPriceInfo.getSurcharge() == null
		shippingPriceInfo.getGiftWrapFee() == null
		
		//for populateShippingAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList priceAdjList = shippingPriceInfo.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> priceAdjArray = priceAdjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment priceAdjustment =priceAdjArray.get(0)
		priceAdjustment.getAtgPromotionId() == "bbbrepId"
		priceAdjustment.getCouponCode() == "bcrepId"
		priceAdjustment.getDiscountAmount() == 350.0
		priceAdjustment.getPromotionType() == "type1"
		priceAdjustment.getPromotionDescription() == "display1"
	}
	
	def "transformOrderToResponse, when populateLTLItemInfo is called with assemblyItemId not empty and delChargeForPriceBean is equal to deliveryChargeForCommItem"(){ // do not change
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		2*requestHeader.getOrderIdentifier() >> "ABC"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		1*requestHeader.getCurrencyCode() >> null
		1*requestHeader.getCallingAppCode() >> null
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> {throw new CommerceException("")}
		/*oldOrder.getInternationalOrderId() >> "ABC"*/
		0*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		1*bbbOrderPriceInfo.getTotal() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		1*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> {throw new BBBBusinessException("")}
		pTools1.round(0.0, 2) >> 0.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		2*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_ITEM_LINE_NUMBER,1)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> false
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		1*add.getAddress2() >> ""
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def ItemPriceInfo ecoInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def atg.core.util.Range range =Mock()
		def LTLAssemblyFeeCommerceItem ltlItem = Mock()
		def LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,null)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		2*cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det]
		2*cItem.isLtlItem() >> true
		2*cItem.getDeliveryItemId() >> "cdId"
		2*cItem.getAssemblyItemId() >>  "caId"
		1*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> ltlItem
		1*pOrder.getCommerceItem(cItem.getDeliveryItemId()) >> deliveryCommerceItem
		deliveryCommerceItem.getPriceInfo() >> deliveryInfo
		deliveryInfo.getAmount() >> 300.0
		det.getQuantity() >> 10.0
		pTools1.round(10.0, 2) == 10.0
		
		pOrder.getSiteId() >> "siteId"
		pTools1.calculateItemSurchargeInSG(pOrder.getSiteId(), rel)  >> 90.0
		1*cItem.getCatalogRefId() >> "skuid"
		1*cItem.getRegistryId() >> ""
		
		//for populateItemAdjustmentListInResponse in populateItemListInResponse
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def RepositoryItem promotionItem = Mock()
		def RepositoryItem coupon = Mock()
		
		pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED , true)
		det.getAdjustments() >>  [adj]
		
		adj.getPricingModel() >> promotionItem
		1*promotionItem.getRepositoryId() >> "repId"
		adj.getCoupon() >> null
	/*	1*coupon.getRepositoryId() >>"crepid"*/
		adj.getAdjustment() >> 60.0
		pTools1.round(60.0,2) >> 60.0
		1*promotionItem.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >>"type"
		1*promotionItem.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display"
		//end populateItemAdjustments
		
		1*info1.getListPrice() >> 90.0
		pTools1.round(90.0,2) >> 90.0
		1*det.getDetailedUnitPrice() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		1*cItem.getId() >>"cID"
		Map<String, String> ecoFeeMap = new HashMap()
		ecoFeeMap.put(cItem.getId(),"")
		//ends populateItemListInResponse
		
		//for populateLTLItemInfo
		def ItemPriceInfo ltlInfo =Mock()
		cItem.getQuantity() >> 10.0
		pTools1.round(30.0) >> 30.0
		deliveryInfo.getRawTotalPrice() >> 500.0
		pTools1.round(20.0)  >> 20.0
		pTools1.round(30.0,2)  >> 30.0
		pTools1.round(300.0,2)  >> 300.0
		pTools1.round(200.0,2)  >> 200.0
		deliveryCommerceItem.getCatalogRefId() >>"delRefId"
		ltlItem.getPriceInfo() >> ltlInfo
		ltlInfo.getListPrice() >> 50.0
		ltlItem.getCatalogRefId() >> "ltlItem"
		pTools1.round(50.0,2)  >> 50.0
		pTools1.round(500.0,2)  >> 500.0
		//ends populateLTLItemInfo
		
		//for populateShippingPriceInResponse in populateShippingGroupsInResponse
		def GiftWrapCommerceItem giftWrap =Mock()
		def ItemPriceInfo giftInfo =Mock()
		1*bbbShippingPriceInfo1.getFinalShipping() >> 60.0
		pTools1.round(60.0,2)  >> 60.0
		1*bbbShippingPriceInfo1.getRawShipping() >> 0.0
		pTools1.round(0.0,2)  >> 0.0
		1*g1.getGiftWrapCommerceItem() >> null
		
		//for populateShippingAdjustmentListInResponse in populateShippingPriceInResponse
		def PricingAdjustment bbbAdjustment =Mock()
		def RepositoryItem bbbrep =Mock()
		def RepositoryItem bbbcoupon = Mock()
		
		bbbShippingPriceInfo1.getAdjustments() >> [bbbAdjustment]
		bbbAdjustment.getPricingModel() >> bbbrep
		bbbrep.getRepositoryId() >> "bbbrepId"
		bbbAdjustment.getAdjustment() >> 350.0
		pTools1.round(350.0, 2) >> 350.0
		bbbAdjustment.getCoupon() >> bbbcoupon
		bbbcoupon.getRepositoryId() >> "bcrepId"
		bbbrep.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >> "type1"
		bbbrep.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display1"
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == null
		head.getCallingAppCode() == null
		head.getCurrencyCode() == null
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
		0*mapper.vlogDebug("International Order updating shipping prices")
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == null
		orderPriceInfo.getTotalDeliverySurcharge() == null
		orderPriceInfo.getTotalAssemblyFee() == null
		orderPriceInfo.getDeliveryThreshold() ==null
		orderPriceInfo.getTotalDeliverySurchargeSaving() == null
		orderPriceInfo.getTotalShipping() == 20.0
		orderPriceInfo.getTotalSurcharge() == null
		1*mapper.logError("Error getting thresholdAmount from config keys", _)
		1*mapper.logError("Error while loading order atg.commerce.CommerceException: ")
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup =group.get(0)
		shippingGroup.getShippingGroupId() == "1"
		shippingGroup.getShippingMethod() == "g1Method"
		
		//for populateAddressInResponse
		com.bedbathandbeyond.atg.Address address=shippingGroup.getShippingAddress()
		address.getAddressLine1() =="add1"
		address.getAddressLine2() == null
		address.getCity() == "city"
		address.getState() == "state"
		address.getZipCode() == "postalCode"
		address.getCountry() == "country"
		
		//for populateItemListInResponse
		com.bedbathandbeyond.atg.ItemList itemList =shippingGroup.getItemList()
		List<com.bedbathandbeyond.atg.Item> itemArray = itemList.getItemArray()
		com.bedbathandbeyond.atg.Item item = itemArray.get(0)
		item.getOriginalLineNumber() == "1"
		item.getStatus() == null
		item.getIsPcInd() == false
		item.getPcInfo() == null
		item.getLineNumber()== "1"
		item.getSku() == "skuid"
		item.getQuantity() == 10
		item.getSurcharge() == 90.0
		item.getRegistryId() == null
		item.getPrice() == 90.0
		item.getFinalPrice() == 0.0
		item.getEcoFee() == null
		item.getEcoFeeSku() ==null
		item.getDeliverySurcharge() == 300.0
		item.getDeliverySurchargePerQty() == 30.0
		item.getDeliverySurchargeSaving() == 200.0
		item.getDeliverySurchargeSku() == "delRefId"
		item.getAssemblyFee() == 500.0
		item.getAssemblyFeePerQty() ==50.0
		item.getAssemblyFeeSku() == "ltlItem"
		item.getIsAssemblyRequested() == true
		
		//for populateItemAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList adjList = item.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> adjArray = adjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment adjustment= adjArray.get(0)
		adjustment.getAtgPromotionId() == "repId"
		adjustment.getCouponCode() == null
		adjustment.getDiscountAmount() == 60.0
		adjustment.getPromotionType() == "type"
		adjustment.getPromotionDescription() == "display"
		
		//for populateShippingPriceInResponse
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = shippingGroup.getShippingPrice()
		shippingPriceInfo.getShippingAmount() == 60.0
		shippingPriceInfo.getShippingRawAmount() == null
		shippingPriceInfo.getSurcharge() == null
		shippingPriceInfo.getGiftWrapFee() == null
		
		//for populateShippingAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList priceAdjList = shippingPriceInfo.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> priceAdjArray = priceAdjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment priceAdjustment =priceAdjArray.get(0)
		priceAdjustment.getAtgPromotionId() == "bbbrepId"
		priceAdjustment.getCouponCode() == "bcrepId"
		priceAdjustment.getDiscountAmount() == 350.0
		priceAdjustment.getPromotionType() == "type1"
		priceAdjustment.getPromotionDescription() == "display1"
	}
	
	def "transformOrderToResponse, when populateLTLItemInfo is called with assemblyItemId empty and delChargeForPriceBean is equal to deliveryChargeForCommItem in populateLTLItemInfo"(){
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		2*requestHeader.getOrderIdentifier() >> "ABC"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		1*requestHeader.getCurrencyCode() >> null
		1*requestHeader.getCallingAppCode() >> null
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> {throw new RepositoryException("")}
		0*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		1*bbbOrderPriceInfo.getTotal() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		1*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> {throw new BBBBusinessException("")}
		pTools1.round(0.0, 2) >> 0.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		2*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_ITEM_LINE_NUMBER,1)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> false
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		1*add.getAddress2() >> ""
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def ItemPriceInfo ecoInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def atg.core.util.Range range =Mock()
		def LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,null)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		2*cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det]
		2*cItem.isLtlItem() >> true
		2*cItem.getDeliveryItemId() >> "cdId"
		1*cItem.getAssemblyItemId() >>  ""
		1*pOrder.getCommerceItem(cItem.getDeliveryItemId()) >> deliveryCommerceItem
		deliveryCommerceItem.getPriceInfo() >> deliveryInfo
		deliveryInfo.getAmount() >> 300.0
		det.getQuantity() >> 10.0
		pTools1.round(10.0, 2) == 10.0
		
		pOrder.getSiteId() >> "siteId"
		pTools1.calculateItemSurchargeInSG(pOrder.getSiteId(), rel)  >> 90.0
		pTools1.round(90.0, 2) >> 90.0
		1*cItem.getCatalogRefId() >> "skuid"
		1*cItem.getRegistryId() >> ""
		
		//for populateItemAdjustmentListInResponse in populateItemListInResponse
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def RepositoryItem promotionItem = Mock()
		def RepositoryItem coupon = Mock()
		
		pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED , true)
		det.getAdjustments() >>  [adj]
		
		adj.getPricingModel() >> promotionItem
		1*promotionItem.getRepositoryId() >> "repId"
		adj.getCoupon() >> null
		adj.getAdjustment() >> 60.0
		pTools1.round(60.0,2) >> 60.0
		1*promotionItem.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >>"type"
		1*promotionItem.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display"
		//end populateItemAdjustments
		
		1*info1.getListPrice() >> 90.0
		pTools1.round(90.0,2) >> 90.0
		1*det.getDetailedUnitPrice() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		1*cItem.getId() >>"cID"
		Map<String, String> ecoFeeMap = new HashMap()
		ecoFeeMap.put(cItem.getId(),"")
		//ends populateItemListInResponse
		
		//for populateLTLItemInfo
		def ItemPriceInfo ltlInfo =Mock()
		cItem.getQuantity() >> 10.0
		pTools1.round(30.0) >> 30.0
		deliveryInfo.getRawTotalPrice() >> 500.0
		pTools1.round(20.0)  >> 20.0
		pTools1.round(30.0,2)  >> 30.0
		pTools1.round(300.0,2)  >> 300.0
		pTools1.round(200.0,2)  >> 200.0
		deliveryCommerceItem.getCatalogRefId() >>"delRefId"
		//ends populateLTLItemInfo
		
		//for populateShippingPriceInResponse in populateShippingGroupsInResponse
		def GiftWrapCommerceItem giftWrap =Mock()
		def ItemPriceInfo giftInfo =Mock()
		1*bbbShippingPriceInfo1.getFinalShipping() >> 60.0
		pTools1.round(60.0,2)  >> 60.0
		1*bbbShippingPriceInfo1.getRawShipping() >> 0.0
		pTools1.round(0.0,2)  >> 0.0
		1*g1.getGiftWrapCommerceItem() >> null
		
		//for populateShippingAdjustmentListInResponse in populateShippingPriceInResponse
		def PricingAdjustment bbbAdjustment =Mock()
		def RepositoryItem bbbrep =Mock()
		def RepositoryItem bbbcoupon = Mock()
		
		bbbShippingPriceInfo1.getAdjustments() >> [bbbAdjustment]
		bbbAdjustment.getPricingModel() >> bbbrep
		bbbrep.getRepositoryId() >> "bbbrepId"
		bbbAdjustment.getAdjustment() >> 350.0
		pTools1.round(350.0, 2) >> 350.0
		bbbAdjustment.getCoupon() >> bbbcoupon
		bbbcoupon.getRepositoryId() >> "bcrepId"
		bbbrep.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >> "type1"
		bbbrep.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display1"
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == null
		head.getCallingAppCode() == null
		head.getCurrencyCode() == null
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
		0*mapper.vlogDebug("International Order updating shipping prices")
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == null
		orderPriceInfo.getTotalDeliverySurcharge() == null
		orderPriceInfo.getTotalAssemblyFee() == null
		orderPriceInfo.getDeliveryThreshold() ==null
		orderPriceInfo.getTotalDeliverySurchargeSaving() == null
		orderPriceInfo.getTotalShipping() == 20.0
		orderPriceInfo.getTotalSurcharge() == null
		1*mapper.logError("Error while getting Repository item  atg.repository.RepositoryException: ");
		1*mapper.logError("Error getting thresholdAmount from config keys", _)
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup =group.get(0)
		shippingGroup.getShippingGroupId() == "1"
		shippingGroup.getShippingMethod() == "g1Method"
		
		//for populateAddressInResponse
		com.bedbathandbeyond.atg.Address address=shippingGroup.getShippingAddress()
		address.getAddressLine1() =="add1"
		address.getAddressLine2() == null
		address.getCity() == "city"
		address.getState() == "state"
		address.getZipCode() == "postalCode"
		address.getCountry() == "country"
		
		//for populateItemListInResponse
		com.bedbathandbeyond.atg.ItemList itemList =shippingGroup.getItemList()
		List<com.bedbathandbeyond.atg.Item> itemArray = itemList.getItemArray()
		com.bedbathandbeyond.atg.Item item = itemArray.get(0)
		item.getOriginalLineNumber() == "1"
		item.getStatus() == null
		item.getIsPcInd() == false
		item.getPcInfo() == null
		item.getLineNumber()== "1"
		item.getSku() == "skuid"
		item.getQuantity() == 10
		item.getSurcharge() == 90.0
		item.getRegistryId() == null
		item.getPrice() == 90.0
		item.getFinalPrice() == 0.0
		item.getEcoFee() == null
		item.getEcoFeeSku() ==null
		item.getDeliverySurcharge() == 300.0
		item.getDeliverySurchargePerQty() == 30.0
		item.getDeliverySurchargeSaving() == 200.0
		item.getDeliverySurchargeSku() == "delRefId"
		item.getAssemblyFee() == null
		item.getAssemblyFeePerQty() ==null
		item.getAssemblyFeeSku() == null
		item.getIsAssemblyRequested() == false
		
		//for populateItemAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList adjList = item.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> adjArray = adjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment adjustment= adjArray.get(0)
		adjustment.getAtgPromotionId() == "repId"
		adjustment.getCouponCode() == null
		adjustment.getDiscountAmount() == 60.0
		adjustment.getPromotionType() == "type"
		adjustment.getPromotionDescription() == "display"
		
		//for populateShippingPriceInResponse
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = shippingGroup.getShippingPrice()
		shippingPriceInfo.getShippingAmount() == 60.0
		shippingPriceInfo.getShippingRawAmount() == null
		shippingPriceInfo.getSurcharge() == null
		shippingPriceInfo.getGiftWrapFee() == null
		
		//for populateShippingAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList priceAdjList = shippingPriceInfo.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> priceAdjArray = priceAdjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment priceAdjustment =priceAdjArray.get(0)
		priceAdjustment.getAtgPromotionId() == "bbbrepId"
		priceAdjustment.getCouponCode() == "bcrepId"
		priceAdjustment.getDiscountAmount() == 350.0
		priceAdjustment.getPromotionType() == "type1"
		priceAdjustment.getPromotionDescription() == "display1"
	}
	
	
	def "transformOrderToResponse, when LTL commerceItem is null, CommerceItemNotFound Exception is thrown in populateItemListInResponse"(){ 
		
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		3*requestHeader.getOrderIdentifier() >> "TBS"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		2*requestHeader.getCurrencyCode() >> currency
		2*requestHeader.getCallingAppCode() >> "appCode"
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> oldOrder
		1*oldOrder.getInternationalOrderId() >> "intOrderId"
		1*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		2*bbbOrderPriceInfo.getTotal() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 600.0
		pTools1.round(600.0, 2) >> 600.0
		2*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 200.0
		pTools1.round(200.0, 2) >> 200.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> ["500.0"]
		pTools1.round(500.0, 2) >> 500.0
		pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		1*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 30.0
		pTools1.round(30.0, 2) >> 30.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> true
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		2*add.getAddress2() >> "add2"
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def atg.core.util.Range range =Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,requestItem)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		1*cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det]
		1*cItem.isLtlItem() >> true
		1*cItem.getDeliveryItemId() >> "cdId"
		2*cItem.getAssemblyItemId() >>  "caId"
		1*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> {throw new CommerceItemNotFoundException("")}
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		resp ==null
		BBBSystemException e = thrown()
	}
	
	def "transformOrderToResponse, when LTL commerceItem is null, InvalidParameterException is thrown in populateItemListInResponse"(){
		
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		3*requestHeader.getOrderIdentifier() >> "TBS"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		2*requestHeader.getCurrencyCode() >> currency
		2*requestHeader.getCallingAppCode() >> "appCode"
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> oldOrder
		1*oldOrder.getInternationalOrderId() >> "intOrderId"
		1*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		2*bbbOrderPriceInfo.getTotal() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 600.0
		pTools1.round(600.0, 2) >> 600.0
		2*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 200.0
		pTools1.round(200.0, 2) >> 200.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> ["500.0"]
		pTools1.round(500.0, 2) >> 500.0
		pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		1*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 30.0
		pTools1.round(30.0, 2) >> 30.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> true
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		2*add.getAddress2() >> "add2"
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def atg.core.util.Range range =Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,requestItem)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		1*cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det]
		1*cItem.isLtlItem() >> true
		1*cItem.getDeliveryItemId() >> "cdId"
		2*cItem.getAssemblyItemId() >>  "caId"
		1*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> {throw new InvalidParameterException("")}
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		resp ==null
		BBBSystemException e = thrown()
	}
	
	
	def "transformOrderToResponse, checks if adjustments list is null, sets PromotionAppliedFlag as false in isPromotionApplied"(){ 
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		3*requestHeader.getOrderIdentifier() >> "TBS"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		2*requestHeader.getCurrencyCode() >> currency
		2*requestHeader.getCallingAppCode() >> "appCode"
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> oldOrder
		1*oldOrder.getInternationalOrderId() >> "intOrderId"
		1*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		2*bbbOrderPriceInfo.getTotal() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 600.0
		pTools1.round(600.0, 2) >> 600.0
		2*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 200.0
		pTools1.round(200.0, 2) >> 200.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> ["500.0"]
		pTools1.round(500.0, 2) >> 500.0
		pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		2*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 30.0
		pTools1.round(30.0, 2) >> 30.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> true
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		2*add.getAddress2() >> "add2"
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def ItemPriceInfo ecoInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def atg.core.util.Range range =Mock()
		def LTLAssemblyFeeCommerceItem ltlItem = Mock()
		def LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,requestItem)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		2*cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det]
		2*cItem.isLtlItem() >> true
		2*cItem.getDeliveryItemId() >> "cdId"
		2*cItem.getAssemblyItemId() >>  "caId"
		1*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> ltlItem
		1*pOrder.getCommerceItem(cItem.getDeliveryItemId()) >> deliveryCommerceItem
		1*deliveryCommerceItem.getPriceInfo() >> deliveryInfo
		deliveryInfo.getAmount() >> 300.0
		det.getQuantity() >> 10.0
		pTools1.round(10.0, 2) == 10.0
		
		pOrder.getSiteId() >> "siteId"
		pTools1.calculateItemSurchargeInSG(pOrder.getSiteId(), rel)  >> 90.0
		pTools1.round(90.0, 2) >> 90.0
		1*requestItem.getOriginalLineNumber() >> "2"
		1*requestItem.getStatus() >> status
		1*requestItem.getIsPcInd() >> true
		1*requestItem.getPcInfo() >> pcinf
		1*requestItem.getLineNumber() >> "5"
		cItem.getCatalogRefId() >> "skuid"
		2*cItem.getRegistryId() >> "registryId"
		
		//for populateItemAdjustmentListInResponse in populateItemListInResponse
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def RepositoryItem promotionItem = Mock()
		def RepositoryItem coupon = Mock()
		def AdjustmentList adjlst =Mock()
		def Adjustment adjust =  Mock()
		def Adjustment adjust1 =  Mock()
		
		pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED , true)
		/*det.getAdjustments() >>  [adj]*/
		det.getAdjustments() >>  null
		
		requestItem.getSku() >> "skuid"
		requestItem.getAdjustmentList() >> adjlst
		adjlst.sizeOfAdjustmentArray() >>1
		adjlst.getAdjustmentArray() >> [adjust,adjust1]
		
		adjust.getAtgPromotionId() >> "atgPromotion"
		adjust.getCouponCode() >> "cc"
		adjust.getDiscountAmount() >> 30.0
		adjust.getPromotionType() >> "Ptype"
		adjust.getPromotionDescription() >> "Pdescription"
		
		adjust1.getAtgPromotionId() >> null
		adjust1.getCouponCode() >> null
		adjust1.getDiscountAmount() >> 40.0
		adjust1.getPromotionType() >> "Ptype1"
		adjust1.getPromotionDescription() >> "Pdescription1"
		//end populateItemAdjustments
		
		1*info1.getListPrice() >> 90.0
		pTools1.round(90.0,2) >> 90.0
		1*det.getDetailedUnitPrice() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		2*cItem.getId() >>"cID"
		Map<String, String> ecoFeeMap = new HashMap()
		ecoFeeMap.put(cItem.getId(),"ecoFeeItemId")
		1*g1.getEcoFeeItemMap() >> ecoFeeMap
		1*pOrder.getCommerceItem("ecoFeeItemId") >>ecoFeeitem
		2*ecoFeeitem.getPriceInfo() >>ecoInfo
		1*ecoInfo.getListPrice() >> 120.0
		pTools1.round(120.0,2) >> 120.0
		1*ecoFeeitem.getCatalogRefId() >> "ecoSkuId"
		
		2*requestItem.getDeliverySurcharge() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		2*requestItem.getDeliverySurchargePerQty() >> 10.0
		pTools1.round(10.0,2) >> 10.0
		2*requestItem.getDeliverySurchargeSaving() >> 20.0
		pTools1.round(20.0,2) >> 20.0
		2*requestItem.getDeliverySurchargeSku() >> "surchargeSku"
		2*requestItem.getAssemblyFee() >> 200.0
		pTools1.round(200.0,2) >> 200.0
		2*requestItem.getAssemblyFeePerQty() >> 60.0
		pTools1.round(60.0,2) >> 60.0
		2*requestItem.getAssemblyFeeSku() >> "assemblySku"
		//ends populateItemListInResponse
		
		//for populateShippingPriceInResponse in populateShippingGroupsInResponse
		def GiftWrapCommerceItem giftWrap =Mock()
		def ItemPriceInfo giftInfo =Mock()
		1*bbbShippingPriceInfo1.getFinalShipping() >> 60.0
		pTools1.round(60.0,2)  >> 60.0
		2*bbbShippingPriceInfo1.getRawShipping() >> 80.0
		pTools1.round(80.0,2)  >> 80.0
		1*g1.getGiftWrapCommerceItem() >> giftWrap
		giftWrap.getPriceInfo() >>giftInfo
		1*giftInfo.getAmount() >> 250.0
		pTools1.round(250.0,2)  >> 250.0
		
		//for populateShippingAdjustmentListInResponse in populateShippingPriceInResponse
		def PricingAdjustment bbbAdjustment =Mock()
		def RepositoryItem bbbrep =Mock()
		def RepositoryItem bbbcoupon = Mock()
		
		bbbShippingPriceInfo1.getAdjustments() >> [bbbAdjustment]
		bbbAdjustment.getPricingModel() >> bbbrep
		bbbrep.getRepositoryId() >> "bbbrepId"
		bbbAdjustment.getAdjustment() >> 350.0
		pTools1.round(350.0, 2) >> 350.0
		bbbAdjustment.getCoupon() >> bbbcoupon
		bbbcoupon.getRepositoryId() >> "bcrepId"
		bbbrep.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >> "type1"
		bbbrep.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display1"
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == "TBS"
		head.getCallingAppCode() == "appCode"
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == 50.0
		orderPriceInfo.getTotalDeliverySurcharge() == 600.0
		orderPriceInfo.getTotalAssemblyFee() == 200.0
		orderPriceInfo.getDeliveryThreshold() ==500.0
		orderPriceInfo.getTotalDeliverySurchargeSaving() == 100.0
		orderPriceInfo.getTotalShipping() == 20.0
		orderPriceInfo.getTotalSurcharge() == 30.0
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup =group.get(0)
		shippingGroup.getShippingGroupId() == "1"
		shippingGroup.getShippingMethod() == "g1Method"
		
		//for populateAddressInResponse
		com.bedbathandbeyond.atg.Address address=shippingGroup.getShippingAddress()
		address.getAddressLine1() =="add1"
		address.getCity() == "city"
		address.getState() == "state"
		address.getZipCode() == "postalCode"
		address.getCountry() == "country"
		
		//for populateItemListInResponse
		com.bedbathandbeyond.atg.ItemList itemList =shippingGroup.getItemList()
		List<com.bedbathandbeyond.atg.Item> itemArray = itemList.getItemArray()
		com.bedbathandbeyond.atg.Item item = itemArray.get(0)
		item.getOriginalLineNumber() == "2"
		item.getStatus() != null
		item.getIsPcInd() == true
		item.getPcInfo() != null
		item.getLineNumber()== "5"
		item.getSku() == "skuid"
		item.getQuantity() == 10
		item.getSurcharge() == 90.0
		item.getRegistryId() == "registryId"
		item.getPrice() == 90.0
		item.getFinalPrice() == 0.0
		item.getEcoFee() == 120.0
		item.getEcoFeeSku() =="ecoSkuId"
		item.getDeliverySurcharge() == 50.0
		item.getDeliverySurchargePerQty() == 10.0
		item.getDeliverySurchargeSaving() ==20.0
		item.getDeliverySurchargeSku() == "surchargeSku"
		item.getAssemblyFee() == 200.0
		item.getAssemblyFeePerQty() ==60.0
		item.getAssemblyFeeSku() == "assemblySku"
		
		//for populateItemAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList adjList = item.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> adjArray = adjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment adjustment= adjArray.get(0)
		adjustment.getAtgPromotionId() == "atgPromotion"
		adjustment.getCouponCode() == "cc"
		adjustment.getDiscountAmount() == 30.0
		adjustment.getPromotionType() == "Ptype"
		adjustment.getPromotionDescription() == "Pdescription"
		
		com.bedbathandbeyond.atg.Adjustment adjustment1= adjArray.get(1)
		adjustment1.getAtgPromotionId() == null
		adjustment1.getCouponCode() == null
		adjustment1.getDiscountAmount() == 40.0
		adjustment1.getPromotionType() == "Ptype1"
		adjustment1.getPromotionDescription() == "Pdescription1"
		
		//for populateShippingPriceInResponse
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = shippingGroup.getShippingPrice()
		shippingPriceInfo.getShippingAmount() == 60.0
		shippingPriceInfo.getShippingRawAmount() == 80.0
		shippingPriceInfo.getSurcharge() == 30.0
		shippingPriceInfo.getGiftWrapFee() == 250.0
		
		//for populateShippingAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList priceAdjList = shippingPriceInfo.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> priceAdjArray = priceAdjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment priceAdjustment =priceAdjArray.get(0)
		priceAdjustment.getAtgPromotionId() == "bbbrepId"
		priceAdjustment.getCouponCode() == "bcrepId"
		priceAdjustment.getDiscountAmount() == 350.0
		priceAdjustment.getPromotionType() == "type1"
		priceAdjustment.getPromotionDescription() == "display1"
	}
	
	def "transformOrderToResponse, checks if adjustments list is null, and sku id of commerecItem is not equal to skuid recieved from response in populateItemAdjustmentListInResponse"(){ 
		
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		3*requestHeader.getOrderIdentifier() >> "TBS"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		2*requestHeader.getCurrencyCode() >> currency
		2*requestHeader.getCallingAppCode() >> "appCode"
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> oldOrder
		1*oldOrder.getInternationalOrderId() >> "intOrderId"
		1*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		2*bbbOrderPriceInfo.getTotal() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 600.0
		pTools1.round(600.0, 2) >> 600.0
		2*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 200.0
		pTools1.round(200.0, 2) >> 200.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> ["500.0"]
		pTools1.round(500.0, 2) >> 500.0
		pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		2*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 30.0
		pTools1.round(30.0, 2) >> 30.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> true
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		2*add.getAddress2() >> "add2"
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def ItemPriceInfo ecoInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def atg.core.util.Range range =Mock()
		def LTLAssemblyFeeCommerceItem ltlItem = Mock()
		def LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,requestItem)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		2*cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det]
		2*cItem.isLtlItem() >> true
		2*cItem.getDeliveryItemId() >> "cdId"
		2*cItem.getAssemblyItemId() >>  "caId"
		1*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> ltlItem
		1*pOrder.getCommerceItem(cItem.getDeliveryItemId()) >> deliveryCommerceItem
		1*deliveryCommerceItem.getPriceInfo() >> deliveryInfo
		deliveryInfo.getAmount() >> 300.0
		det.getQuantity() >> 10.0
		pTools1.round(10.0, 2) == 10.0
		
		pOrder.getSiteId() >> "siteId"
		pTools1.calculateItemSurchargeInSG(pOrder.getSiteId(), rel)  >> 90.0
		pTools1.round(90.0, 2) >> 90.0
		1*requestItem.getOriginalLineNumber() >> "2"
		1*requestItem.getStatus() >> status
		1*requestItem.getIsPcInd() >> true
		1*requestItem.getPcInfo() >> pcinf
		1*requestItem.getLineNumber() >> "5"
		cItem.getCatalogRefId() >> "skuid"
		2*cItem.getRegistryId() >> "registryId"
		
		//for populateItemAdjustmentListInResponse in populateItemListInResponse
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def RepositoryItem promotionItem = Mock()
		def RepositoryItem coupon = Mock()
		def AdjustmentList adjlst =Mock()
		def Adjustment adjust =  Mock()
		def Adjustment adjust1 =  Mock()
		
		pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED , true)
		det.getAdjustments() >>  null
		requestItem.getSku() >> "sku"
		//end populateItemAdjustments
		
		1*info1.getListPrice() >> 90.0
		pTools1.round(90.0,2) >> 90.0
		1*det.getDetailedUnitPrice() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		2*cItem.getId() >>"cID"
		Map<String, String> ecoFeeMap = new HashMap()
		ecoFeeMap.put(cItem.getId(),"ecoFeeItemId")
		1*g1.getEcoFeeItemMap() >> ecoFeeMap
		1*pOrder.getCommerceItem("ecoFeeItemId") >>ecoFeeitem
		2*ecoFeeitem.getPriceInfo() >>ecoInfo
		1*ecoInfo.getListPrice() >> 120.0
		pTools1.round(120.0,2) >> 120.0
		1*ecoFeeitem.getCatalogRefId() >> "ecoSkuId"
		
		2*requestItem.getDeliverySurcharge() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		2*requestItem.getDeliverySurchargePerQty() >> 10.0
		pTools1.round(10.0,2) >> 10.0
		2*requestItem.getDeliverySurchargeSaving() >> 20.0
		pTools1.round(20.0,2) >> 20.0
		2*requestItem.getDeliverySurchargeSku() >> "surchargeSku"
		2*requestItem.getAssemblyFee() >> 200.0
		pTools1.round(200.0,2) >> 200.0
		2*requestItem.getAssemblyFeePerQty() >> 60.0
		pTools1.round(60.0,2) >> 60.0
		2*requestItem.getAssemblyFeeSku() >> "assemblySku"
		//ends populateItemListInResponse
		
		//for populateShippingPriceInResponse in populateShippingGroupsInResponse
		def GiftWrapCommerceItem giftWrap =Mock()
		def ItemPriceInfo giftInfo =Mock()
		1*bbbShippingPriceInfo1.getFinalShipping() >> 60.0
		pTools1.round(60.0,2)  >> 60.0
		2*bbbShippingPriceInfo1.getRawShipping() >> 80.0
		pTools1.round(80.0,2)  >> 80.0
		1*g1.getGiftWrapCommerceItem() >> giftWrap
		giftWrap.getPriceInfo() >>giftInfo
		1*giftInfo.getAmount() >> 250.0
		pTools1.round(250.0,2)  >> 250.0
		
		//for populateShippingAdjustmentListInResponse in populateShippingPriceInResponse
		def PricingAdjustment bbbAdjustment =Mock()
		def RepositoryItem bbbrep =Mock()
		def RepositoryItem bbbcoupon = Mock()
		
		bbbShippingPriceInfo1.getAdjustments() >> [bbbAdjustment]
		bbbAdjustment.getPricingModel() >> bbbrep
		bbbrep.getRepositoryId() >> "bbbrepId"
		bbbAdjustment.getAdjustment() >> 350.0
		pTools1.round(350.0, 2) >> 350.0
		bbbAdjustment.getCoupon() >> bbbcoupon
		bbbcoupon.getRepositoryId() >> "bcrepId"
		bbbrep.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >> "type1"
		bbbrep.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display1"
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == "TBS"
		head.getCallingAppCode() == "appCode"
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == 50.0
		orderPriceInfo.getTotalDeliverySurcharge() == 600.0
		orderPriceInfo.getTotalAssemblyFee() == 200.0
		orderPriceInfo.getDeliveryThreshold() ==500.0
		orderPriceInfo.getTotalDeliverySurchargeSaving() == 100.0
		orderPriceInfo.getTotalShipping() == 20.0
		orderPriceInfo.getTotalSurcharge() == 30.0
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup =group.get(0)
		shippingGroup.getShippingGroupId() == "1"
		shippingGroup.getShippingMethod() == "g1Method"
		
		//for populateAddressInResponse
		com.bedbathandbeyond.atg.Address address=shippingGroup.getShippingAddress()
		address.getAddressLine1() =="add1"
		address.getCity() == "city"
		address.getState() == "state"
		address.getZipCode() == "postalCode"
		address.getCountry() == "country"
		
		//for populateItemListInResponse
		com.bedbathandbeyond.atg.ItemList itemList =shippingGroup.getItemList()
		List<com.bedbathandbeyond.atg.Item> itemArray = itemList.getItemArray()
		com.bedbathandbeyond.atg.Item item = itemArray.get(0)
		item.getOriginalLineNumber() == "2"
		item.getStatus() != null
		item.getIsPcInd() == true
		item.getPcInfo() != null
		item.getLineNumber()== "5"
		item.getSku() == "skuid"
		item.getQuantity() == 10
		item.getSurcharge() == 90.0
		item.getRegistryId() == "registryId"
		item.getPrice() == 90.0
		item.getFinalPrice() == 0.0
		item.getEcoFee() == 120.0
		item.getEcoFeeSku() =="ecoSkuId"
		item.getDeliverySurcharge() == 50.0
		item.getDeliverySurchargePerQty() == 10.0
		item.getDeliverySurchargeSaving() ==20.0
		item.getDeliverySurchargeSku() == "surchargeSku"
		item.getAssemblyFee() == 200.0
		item.getAssemblyFeePerQty() ==60.0
		item.getAssemblyFeeSku() == "assemblySku"
		
		//for populateItemAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList adjList = item.getAdjustmentList()
		adjList == null
				
		//for populateShippingPriceInResponse
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = shippingGroup.getShippingPrice()
		shippingPriceInfo.getShippingAmount() == 60.0
		shippingPriceInfo.getShippingRawAmount() == 80.0
		shippingPriceInfo.getSurcharge() == 30.0
		shippingPriceInfo.getGiftWrapFee() == 250.0
		
		//for populateShippingAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList priceAdjList = shippingPriceInfo.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> priceAdjArray = priceAdjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment priceAdjustment =priceAdjArray.get(0)
		priceAdjustment.getAtgPromotionId() == "bbbrepId"
		priceAdjustment.getCouponCode() == "bcrepId"
		priceAdjustment.getDiscountAmount() == 350.0
		priceAdjustment.getPromotionType() == "type1"
		priceAdjustment.getPromotionDescription() == "display1"
	}
	
	def "transformOrderToResponse, checks if adjustments list is null in populateItemAdjustmentListInResponse"(){ 
		
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		3*requestHeader.getOrderIdentifier() >> "TBS"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		2*requestHeader.getCurrencyCode() >> currency
		2*requestHeader.getCallingAppCode() >> "appCode"
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> oldOrder
		1*oldOrder.getInternationalOrderId() >> "intOrderId"
		1*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		2*bbbOrderPriceInfo.getTotal() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 600.0
		pTools1.round(600.0, 2) >> 600.0
		2*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 200.0
		pTools1.round(200.0, 2) >> 200.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> ["500.0"]
		pTools1.round(500.0, 2) >> 500.0
		pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		2*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 30.0
		pTools1.round(30.0, 2) >> 30.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> true
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		2*add.getAddress2() >> "add2"
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def ItemPriceInfo ecoInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def atg.core.util.Range range =Mock()
		def LTLAssemblyFeeCommerceItem ltlItem = Mock()
		def LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,requestItem)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		2*cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det]
		2*cItem.isLtlItem() >> true
		2*cItem.getDeliveryItemId() >> "cdId"
		2*cItem.getAssemblyItemId() >>  "caId"
		1*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> ltlItem
		1*pOrder.getCommerceItem(cItem.getDeliveryItemId()) >> deliveryCommerceItem
		1*deliveryCommerceItem.getPriceInfo() >> deliveryInfo
		deliveryInfo.getAmount() >> 300.0
		det.getQuantity() >> 10.0
		pTools1.round(10.0, 2) == 10.0
		
		pOrder.getSiteId() >> "siteId"
		pTools1.calculateItemSurchargeInSG(pOrder.getSiteId(), rel)  >> 90.0
		pTools1.round(90.0, 2) >> 90.0
		1*requestItem.getOriginalLineNumber() >> "2"
		1*requestItem.getStatus() >> status
		1*requestItem.getIsPcInd() >> true
		1*requestItem.getPcInfo() >> pcinf
		1*requestItem.getLineNumber() >> "5"
		cItem.getCatalogRefId() >> "skuid"
		2*cItem.getRegistryId() >> "registryId"
		
		//for populateItemAdjustmentListInResponse in populateItemListInResponse
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def RepositoryItem promotionItem = Mock()
		def RepositoryItem coupon = Mock()
		def AdjustmentList adjlst =Mock()
		def Adjustment adjust =  Mock()
		def Adjustment adjust1 =  Mock()
		
		pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED , true)
		/*det.getAdjustments() >>  [adj]*/
		det.getAdjustments() >>  null
		requestItem.getSku() >> "skuid"
		requestItem.getAdjustmentList() >> null
		//end populateItemAdjustments
		
		1*info1.getListPrice() >> 90.0
		pTools1.round(90.0,2) >> 90.0
		1*det.getDetailedUnitPrice() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		2*cItem.getId() >>"cID"
		Map<String, String> ecoFeeMap = new HashMap()
		ecoFeeMap.put(cItem.getId(),"ecoFeeItemId")
		1*g1.getEcoFeeItemMap() >> ecoFeeMap
		1*pOrder.getCommerceItem("ecoFeeItemId") >>ecoFeeitem
		2*ecoFeeitem.getPriceInfo() >>ecoInfo
		1*ecoInfo.getListPrice() >> 120.0
		pTools1.round(120.0,2) >> 120.0
		1*ecoFeeitem.getCatalogRefId() >> "ecoSkuId"
		
		2*requestItem.getDeliverySurcharge() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		2*requestItem.getDeliverySurchargePerQty() >> 10.0
		pTools1.round(10.0,2) >> 10.0
		2*requestItem.getDeliverySurchargeSaving() >> 20.0
		pTools1.round(20.0,2) >> 20.0
		2*requestItem.getDeliverySurchargeSku() >> "surchargeSku"
		2*requestItem.getAssemblyFee() >> 200.0
		pTools1.round(200.0,2) >> 200.0
		2*requestItem.getAssemblyFeePerQty() >> 60.0
		pTools1.round(60.0,2) >> 60.0
		2*requestItem.getAssemblyFeeSku() >> "assemblySku"
		//ends populateItemListInResponse
		
		//for populateShippingPriceInResponse in populateShippingGroupsInResponse
		def GiftWrapCommerceItem giftWrap =Mock()
		def ItemPriceInfo giftInfo =Mock()
		1*bbbShippingPriceInfo1.getFinalShipping() >> 60.0
		pTools1.round(60.0,2)  >> 60.0
		2*bbbShippingPriceInfo1.getRawShipping() >> 80.0
		pTools1.round(80.0,2)  >> 80.0
		1*g1.getGiftWrapCommerceItem() >> giftWrap
		giftWrap.getPriceInfo() >>giftInfo
		1*giftInfo.getAmount() >> 250.0
		pTools1.round(250.0,2)  >> 250.0
		
		//for populateShippingAdjustmentListInResponse in populateShippingPriceInResponse
		def PricingAdjustment bbbAdjustment =Mock()
		def RepositoryItem bbbrep =Mock()
		def RepositoryItem bbbcoupon = Mock()
		
		bbbShippingPriceInfo1.getAdjustments() >> [bbbAdjustment]
		bbbAdjustment.getPricingModel() >> bbbrep
		bbbrep.getRepositoryId() >> "bbbrepId"
		bbbAdjustment.getAdjustment() >> 350.0
		pTools1.round(350.0, 2) >> 350.0
		bbbAdjustment.getCoupon() >> bbbcoupon
		bbbcoupon.getRepositoryId() >> "bcrepId"
		bbbrep.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >> "type1"
		bbbrep.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display1"
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == "TBS"
		head.getCallingAppCode() == "appCode"
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
	//	1*mapper.vlogDebug("International Order updating shipping prices")
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == 50.0
		orderPriceInfo.getTotalDeliverySurcharge() == 600.0
		orderPriceInfo.getTotalAssemblyFee() == 200.0
		orderPriceInfo.getDeliveryThreshold() ==500.0
		orderPriceInfo.getTotalDeliverySurchargeSaving() == 100.0
		orderPriceInfo.getTotalShipping() == 20.0
		orderPriceInfo.getTotalSurcharge() == 30.0
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup =group.get(0)
		shippingGroup.getShippingGroupId() == "1"
		shippingGroup.getShippingMethod() == "g1Method"
		
		//for populateAddressInResponse
		com.bedbathandbeyond.atg.Address address=shippingGroup.getShippingAddress()
		address.getAddressLine1() =="add1"
		address.getCity() == "city"
		address.getState() == "state"
		address.getZipCode() == "postalCode"
		address.getCountry() == "country"
		
		//for populateItemListInResponse
		com.bedbathandbeyond.atg.ItemList itemList =shippingGroup.getItemList()
		List<com.bedbathandbeyond.atg.Item> itemArray = itemList.getItemArray()
		com.bedbathandbeyond.atg.Item item = itemArray.get(0)
		item.getOriginalLineNumber() == "2"
		item.getStatus() != null
		item.getIsPcInd() == true
		item.getPcInfo() != null
		item.getLineNumber()== "5"
		item.getSku() == "skuid"
		item.getQuantity() == 10
		item.getSurcharge() == 90.0
		item.getRegistryId() == "registryId"
		item.getPrice() == 90.0
		item.getFinalPrice() == 0.0
		item.getEcoFee() == 120.0
		item.getEcoFeeSku() =="ecoSkuId"
		item.getDeliverySurcharge() == 50.0
		item.getDeliverySurchargePerQty() == 10.0
		item.getDeliverySurchargeSaving() ==20.0
		item.getDeliverySurchargeSku() == "surchargeSku"
		item.getAssemblyFee() == 200.0
		item.getAssemblyFeePerQty() ==60.0
		item.getAssemblyFeeSku() == "assemblySku"
		
		//for populateItemAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList adjList = item.getAdjustmentList()
		adjList == null
		
		//for populateShippingPriceInResponse
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = shippingGroup.getShippingPrice()
		shippingPriceInfo.getShippingAmount() == 60.0
		shippingPriceInfo.getShippingRawAmount() == 80.0
		shippingPriceInfo.getSurcharge() == 30.0
		shippingPriceInfo.getGiftWrapFee() == 250.0
		
		//for populateShippingAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList priceAdjList = shippingPriceInfo.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> priceAdjArray = priceAdjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment priceAdjustment =priceAdjArray.get(0)
		priceAdjustment.getAtgPromotionId() == "bbbrepId"
		priceAdjustment.getCouponCode() == "bcrepId"
		priceAdjustment.getDiscountAmount() == 350.0
		priceAdjustment.getPromotionType() == "type1"
		priceAdjustment.getPromotionDescription() == "display1"
	}
	
	
	def "transformOrderToResponse, when CommerceItemNotFoundException is thrown while getting ecoFeeCommerceItem in populateItemListInResponse"(){ 
		
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		3*requestHeader.getOrderIdentifier() >> "TBS"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		2*requestHeader.getCurrencyCode() >> currency
		2*requestHeader.getCallingAppCode() >> "appCode"
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> oldOrder
		1*oldOrder.getInternationalOrderId() >> "intOrderId"
		1*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		2*bbbOrderPriceInfo.getTotal() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 600.0
		pTools1.round(600.0, 2) >> 600.0
		2*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 200.0
		pTools1.round(200.0, 2) >> 200.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> ["500.0"]
		pTools1.round(500.0, 2) >> 500.0
		pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		1*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 30.0
		pTools1.round(30.0, 2) >> 30.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> true
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		2*add.getAddress2() >> "add2"
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def ItemPriceInfo ecoInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def atg.core.util.Range range =Mock()
		def LTLAssemblyFeeCommerceItem ltlItem = Mock()
		def LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,requestItem)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		2*cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det]
		1*cItem.isLtlItem() >> true
		2*cItem.getDeliveryItemId() >> "cdId"
		2*cItem.getAssemblyItemId() >>  "caId"
		1*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> ltlItem
		1*pOrder.getCommerceItem(cItem.getDeliveryItemId()) >> deliveryCommerceItem
		1*deliveryCommerceItem.getPriceInfo() >> deliveryInfo
		deliveryInfo.getAmount() >> 300.0
		det.getQuantity() >> 10.0
		pTools1.round(10.0, 2) == 10.0
		
		pOrder.getSiteId() >> "siteId"
		pTools1.calculateItemSurchargeInSG(pOrder.getSiteId(), rel)  >> 90.0
		pTools1.round(90.0, 2) >> 90.0
		1*requestItem.getOriginalLineNumber() >> "2"
		1*requestItem.getStatus() >> status
		1*requestItem.getIsPcInd() >> true
		1*requestItem.getPcInfo() >> pcinf
		1*requestItem.getLineNumber() >> "5"
		cItem.getCatalogRefId() >> "skuid"
		2*cItem.getRegistryId() >> "registryId"
		
		//for populateItemAdjustmentListInResponse in populateItemListInResponse
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def RepositoryItem promotionItem = Mock()
		def RepositoryItem coupon = Mock()
		def AdjustmentList adjlst =Mock()
		def Adjustment adjust =  Mock()
		def Adjustment adjust1 =  Mock()
		
		pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED , true)
		/*det.getAdjustments() >>  [adj]*/
		det.getAdjustments() >>  null
		requestItem.getSku() >> "skuid"
		requestItem.getAdjustmentList() >> null
		//end populateItemAdjustments
		
		1*info1.getListPrice() >> 90.0
		pTools1.round(90.0,2) >> 90.0
		1*det.getDetailedUnitPrice() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		2*cItem.getId() >>"cID"
		Map<String, String> ecoFeeMap = new HashMap()
		ecoFeeMap.put(cItem.getId(),"ecoFeeItemId")
		1*g1.getEcoFeeItemMap() >> ecoFeeMap
		1*pOrder.getCommerceItem("ecoFeeItemId") >>{throw new CommerceItemNotFoundException("")}
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		resp == null
		BBBSystemException e = thrown()
	}
	
	def "transformOrderToResponse, when InvalidParameterException is thrown while getting ecoFeeCommerceItem in populateItemListInResponse"(){
		
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		3*requestHeader.getOrderIdentifier() >> "TBS"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		2*requestHeader.getCurrencyCode() >> currency
		2*requestHeader.getCallingAppCode() >> "appCode"
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> oldOrder
		1*oldOrder.getInternationalOrderId() >> "intOrderId"
		1*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		2*bbbOrderPriceInfo.getTotal() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 600.0
		pTools1.round(600.0, 2) >> 600.0
		2*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 200.0
		pTools1.round(200.0, 2) >> 200.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> ["500.0"]
		pTools1.round(500.0, 2) >> 500.0
		pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		1*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 30.0
		pTools1.round(30.0, 2) >> 30.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> true
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		2*add.getAddress2() >> "add2"
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def ItemPriceInfo ecoInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def atg.core.util.Range range =Mock()
		def LTLAssemblyFeeCommerceItem ltlItem = Mock()
		def LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,requestItem)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		2*cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det]
		1*cItem.isLtlItem() >> true
		2*cItem.getDeliveryItemId() >> "cdId"
		2*cItem.getAssemblyItemId() >>  "caId"
		1*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> ltlItem
		1*pOrder.getCommerceItem(cItem.getDeliveryItemId()) >> deliveryCommerceItem
		1*deliveryCommerceItem.getPriceInfo() >> deliveryInfo
		deliveryInfo.getAmount() >> 300.0
		det.getQuantity() >> 10.0
		pTools1.round(10.0, 2) == 10.0
		
		pOrder.getSiteId() >> "siteId"
		pTools1.calculateItemSurchargeInSG(pOrder.getSiteId(), rel)  >> 90.0
		pTools1.round(90.0, 2) >> 90.0
		1*requestItem.getOriginalLineNumber() >> "2"
		1*requestItem.getStatus() >> status
		1*requestItem.getIsPcInd() >> true
		1*requestItem.getPcInfo() >> pcinf
		1*requestItem.getLineNumber() >> "5"
		cItem.getCatalogRefId() >> "skuid"
		2*cItem.getRegistryId() >> "registryId"
		
		//for populateItemAdjustmentListInResponse in populateItemListInResponse
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def RepositoryItem promotionItem = Mock()
		def RepositoryItem coupon = Mock()
		def AdjustmentList adjlst =Mock()
		def Adjustment adjust =  Mock()
		def Adjustment adjust1 =  Mock()
		
		pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED , true)
		/*det.getAdjustments() >>  [adj]*/
		det.getAdjustments() >>  null
		requestItem.getSku() >> "skuid"
		requestItem.getAdjustmentList() >> null
		//end populateItemAdjustments
		
		1*info1.getListPrice() >> 90.0
		pTools1.round(90.0,2) >> 90.0
		1*det.getDetailedUnitPrice() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		2*cItem.getId() >>"cID"
		Map<String, String> ecoFeeMap = new HashMap()
		ecoFeeMap.put(cItem.getId(),"ecoFeeItemId")
		1*g1.getEcoFeeItemMap() >> ecoFeeMap
		1*pOrder.getCommerceItem("ecoFeeItemId") >>{throw new InvalidParameterException("")}
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		resp == null
		BBBSystemException e = thrown()
	}
	
	def "transformOrderToResponse, checks if adjustments list is null, and size of adjustment array is zero in populateItemAdjustmentListInResponse"(){ 
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		3*requestHeader.getOrderIdentifier() >> "TBS"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		2*requestHeader.getCurrencyCode() >> currency
		2*requestHeader.getCallingAppCode() >> "appCode"
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> oldOrder
		1*oldOrder.getInternationalOrderId() >> "intOrderId"
		1*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		2*bbbOrderPriceInfo.getTotal() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 600.0
		pTools1.round(600.0, 2) >> 600.0
		2*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 200.0
		pTools1.round(200.0, 2) >> 200.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> ["500.0"]
		pTools1.round(500.0, 2) >> 500.0
		pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		2*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 30.0
		pTools1.round(30.0, 2) >> 30.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> true
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		2*add.getAddress2() >> "add2"
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def ItemPriceInfo ecoInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def atg.core.util.Range range =Mock()
		def LTLAssemblyFeeCommerceItem ltlItem = Mock()
		def LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,requestItem)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		2*cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det]
		2*cItem.isLtlItem() >> true
		2*cItem.getDeliveryItemId() >> "cdId"
		2*cItem.getAssemblyItemId() >>  "caId"
		1*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> ltlItem
		1*pOrder.getCommerceItem(cItem.getDeliveryItemId()) >> deliveryCommerceItem
		1*deliveryCommerceItem.getPriceInfo() >> deliveryInfo
		deliveryInfo.getAmount() >> 300.0
		det.getQuantity() >> 10.0
		pTools1.round(10.0, 2) == 10.0
		
		pOrder.getSiteId() >> "siteId"
		pTools1.calculateItemSurchargeInSG(pOrder.getSiteId(), rel)  >> 90.0
		pTools1.round(90.0, 2) >> 90.0
		1*requestItem.getOriginalLineNumber() >> "2"
		1*requestItem.getStatus() >> status
		1*requestItem.getIsPcInd() >> true
		1*requestItem.getPcInfo() >> pcinf
		1*requestItem.getLineNumber() >> "5"
		cItem.getCatalogRefId() >> "skuid"
		2*cItem.getRegistryId() >> "registryId"
		
		//for populateItemAdjustmentListInResponse in populateItemListInResponse
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def RepositoryItem promotionItem = Mock()
		def RepositoryItem coupon = Mock()
		def AdjustmentList adjlst =Mock()
		def Adjustment adjust =  Mock()
		def Adjustment adjust1 =  Mock()
		
		pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED , true)
		/*det.getAdjustments() >>  [adj]*/
		det.getAdjustments() >>  null
		requestItem.getSku() >> "skuid"
		requestItem.getAdjustmentList() >>adjlst
		adjlst.sizeOfAdjustmentArray() >>0
		//end populateItemAdjustments
		
		1*info1.getListPrice() >> 90.0
		pTools1.round(90.0,2) >> 90.0
		1*det.getDetailedUnitPrice() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		2*cItem.getId() >>"cID"
		Map<String, String> ecoFeeMap = new HashMap()
		ecoFeeMap.put(cItem.getId(),"")
		1*g1.getEcoFeeItemMap() >> ecoFeeMap
		
		2*requestItem.getDeliverySurcharge() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		2*requestItem.getDeliverySurchargePerQty() >> 10.0
		pTools1.round(10.0,2) >> 10.0
		2*requestItem.getDeliverySurchargeSaving() >> 20.0
		pTools1.round(20.0,2) >> 20.0
		2*requestItem.getDeliverySurchargeSku() >> "surchargeSku"
		2*requestItem.getAssemblyFee() >> 200.0
		pTools1.round(200.0,2) >> 200.0
		2*requestItem.getAssemblyFeePerQty() >> 60.0
		pTools1.round(60.0,2) >> 60.0
		2*requestItem.getAssemblyFeeSku() >> "assemblySku"
		//ends populateItemListInResponse
		
		//for populateShippingPriceInResponse in populateShippingGroupsInResponse
		def GiftWrapCommerceItem giftWrap =Mock()
		def ItemPriceInfo giftInfo =Mock()
		1*bbbShippingPriceInfo1.getFinalShipping() >> 60.0
		pTools1.round(60.0,2)  >> 60.0
		2*bbbShippingPriceInfo1.getRawShipping() >> 80.0
		pTools1.round(80.0,2)  >> 80.0
		1*g1.getGiftWrapCommerceItem() >> giftWrap
		giftWrap.getPriceInfo() >>giftInfo
		1*giftInfo.getAmount() >> 250.0
		pTools1.round(250.0,2)  >> 250.0
		
		//for populateShippingAdjustmentListInResponse in populateShippingPriceInResponse
		def PricingAdjustment bbbAdjustment =Mock()
		def RepositoryItem bbbrep =Mock()
		def RepositoryItem bbbcoupon = Mock()
		
		bbbShippingPriceInfo1.getAdjustments() >> [bbbAdjustment]
		bbbAdjustment.getPricingModel() >> bbbrep
		bbbrep.getRepositoryId() >> "bbbrepId"
		bbbAdjustment.getAdjustment() >> 350.0
		pTools1.round(350.0, 2) >> 350.0
		bbbAdjustment.getCoupon() >> bbbcoupon
		bbbcoupon.getRepositoryId() >> "bcrepId"
		bbbrep.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >> "type1"
		bbbrep.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display1"
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == "TBS"
		head.getCallingAppCode() == "appCode"
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == 50.0
		orderPriceInfo.getTotalDeliverySurcharge() == 600.0
		orderPriceInfo.getTotalAssemblyFee() == 200.0
		orderPriceInfo.getDeliveryThreshold() ==500.0
		orderPriceInfo.getTotalDeliverySurchargeSaving() == 100.0
		orderPriceInfo.getTotalShipping() == 20.0
		orderPriceInfo.getTotalSurcharge() == 30.0
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup =group.get(0)
		shippingGroup.getShippingGroupId() == "1"
		shippingGroup.getShippingMethod() == "g1Method"
		
		//for populateAddressInResponse
		com.bedbathandbeyond.atg.Address address=shippingGroup.getShippingAddress()
		address.getAddressLine1() =="add1"
		address.getCity() == "city"
		address.getState() == "state"
		address.getZipCode() == "postalCode"
		address.getCountry() == "country"
		
		//for populateItemListInResponse
		com.bedbathandbeyond.atg.ItemList itemList =shippingGroup.getItemList()
		List<com.bedbathandbeyond.atg.Item> itemArray = itemList.getItemArray()
		com.bedbathandbeyond.atg.Item item = itemArray.get(0)
		item.getOriginalLineNumber() == "2"
		item.getStatus() != null
		item.getIsPcInd() == true
		item.getPcInfo() != null
		item.getLineNumber()== "5"
		item.getSku() == "skuid"
		item.getQuantity() == 10
		item.getSurcharge() == 90.0
		item.getRegistryId() == "registryId"
		item.getPrice() == 90.0
		item.getFinalPrice() == 0.0
		item.getEcoFee() == null
		item.getEcoFeeSku() == null
		item.getDeliverySurcharge() == 50.0
		item.getDeliverySurchargePerQty() == 10.0
		item.getDeliverySurchargeSaving() ==20.0
		item.getDeliverySurchargeSku() == "surchargeSku"
		item.getAssemblyFee() == 200.0
		item.getAssemblyFeePerQty() ==60.0
		item.getAssemblyFeeSku() == "assemblySku"
		
		//for populateItemAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList adjList = item.getAdjustmentList()
		adjList == null
		
		//for populateShippingPriceInResponse
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = shippingGroup.getShippingPrice()
		shippingPriceInfo.getShippingAmount() == 60.0
		shippingPriceInfo.getShippingRawAmount() == 80.0
		shippingPriceInfo.getSurcharge() == 30.0
		shippingPriceInfo.getGiftWrapFee() == 250.0
		
		//for populateShippingAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList priceAdjList = shippingPriceInfo.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> priceAdjArray = priceAdjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment priceAdjustment =priceAdjArray.get(0)
		priceAdjustment.getAtgPromotionId() == "bbbrepId"
		priceAdjustment.getCouponCode() == "bcrepId"
		priceAdjustment.getDiscountAmount() == 350.0
		priceAdjustment.getPromotionType() == "type1"
		priceAdjustment.getPromotionDescription() == "display1"
	}
	
	
	def "transformOrderToResponse, when populateLTLItemInforForPromotion is called with assemblyItemId not empty "(){ 
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		/*com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)*/
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		2*requestHeader.getOrderIdentifier() >> "ABC"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		1*requestHeader.getCurrencyCode() >> null
		1*requestHeader.getCallingAppCode() >> null
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> {throw new CommerceException("")}
		/*oldOrder.getInternationalOrderId() >> "ABC"*/
		0*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		1*bbbOrderPriceInfo.getTotal() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		1*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> {throw new BBBBusinessException("")}
		pTools1.round(0.0, 2) >> 0.0
		//pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		2*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_ITEM_LINE_NUMBER,1)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> false
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		1*add.getAddress2() >> ""
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def ItemPriceInfo ecoInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def DetailedItemPriceInfo det1 = Mock()
		def atg.core.util.Range range =Mock()
		def LTLAssemblyFeeCommerceItem ltlItem = Mock()
		def LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,requestItem)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det, det1]
		cItem.isLtlItem() >> true
		cItem.getDeliveryItemId() >> "cdId"
		cItem.getAssemblyItemId() >>  "caId"
		1*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> ltlItem
		1*pOrder.getCommerceItem(cItem.getDeliveryItemId()) >> deliveryCommerceItem
		deliveryCommerceItem.getPriceInfo() >> deliveryInfo
		deliveryInfo.getAmount() >> 300.0
		det.getQuantity() >> 10.0
		det1.getQuantity() >> 10.0
		pTools1.round(10.0, 2) == 10.0
		
		pOrder.getSiteId() >> "siteId"
		pTools1.calculateItemSurchargeInSG(pOrder.getSiteId(), rel)  >> 90.0
		pTools1.round(90.0, 2) >> 90.0
		requestItem.getOriginalLineNumber() >> "2"
		requestItem.getStatus() >> status
		requestItem.getIsPcInd() >> true
		requestItem.getPcInfo() >> pcinf
		requestItem.getLineNumber() >> "5"
		cItem.getCatalogRefId() >> "skuid"
		cItem.getRegistryId() >> "registryId"
		
		//for populateItemAdjustmentListInResponse in populateItemListInResponse
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def RepositoryItem promotionItem = Mock()
		def RepositoryItem coupon = Mock()
		
		pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED , true)
		det.getAdjustments() >>  [adj]
		det1.getAdjustments() >>  null
		requestItem.getSku() >> "sku"
		
		adj.getPricingModel() >> promotionItem
		1*promotionItem.getRepositoryId() >> "repId"
		adj.getCoupon() >> null
		adj.getAdjustment() >> 60.0
		pTools1.round(60.0,2) >> 60.0
		1*promotionItem.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >>"type"
		1*promotionItem.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display"
		//end populateItemAdjustments
		
		info1.getListPrice() >> 90.0
		pTools1.round(90.0,2) >> 90.0
		1*det.getDetailedUnitPrice() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		1*cItem.getId() >>"cID"
		Map<String, String> ecoFeeMap = new HashMap()
		ecoFeeMap.put(cItem.getId(),"")
	
		
		//for populateLTLItemInforForPromotion
		def ItemPriceInfo ltlInfo =Mock()
		cItem.getQuantity() >> 10.0
		pTools1.round(30.0) >> 30.0
		pTools1.round(300.0) >> 300.0
		deliveryCommerceItem.getCatalogRefId() >>"delRefId"
		deliveryInfo.getRawTotalPrice() >> 500.0
		pTools1.round(200.0,2)  >> 200.0
		ltlItem.getPriceInfo() >> ltlInfo
		ltlInfo.getListPrice() >> 50.0
		pTools1.round(50.0,2)  >> 50.0
		pTools1.round(500.0,2)  >> 500.0
		ltlItem.getCatalogRefId() >> "ltlItem"
		
		//for populateShippingPriceInResponse in populateShippingGroupsInResponse
		def GiftWrapCommerceItem giftWrap =Mock()
		def ItemPriceInfo giftInfo =Mock()
		1*bbbShippingPriceInfo1.getFinalShipping() >> 60.0
		pTools1.round(60.0,2)  >> 60.0
		1*bbbShippingPriceInfo1.getRawShipping() >> 0.0
		pTools1.round(0.0,2)  >> 0.0
		1*g1.getGiftWrapCommerceItem() >> null
		
		//for populateShippingAdjustmentListInResponse in populateShippingPriceInResponse
		def PricingAdjustment bbbAdjustment =Mock()
		def RepositoryItem bbbrep =Mock()
		def RepositoryItem bbbcoupon = Mock()
		
		bbbShippingPriceInfo1.getAdjustments() >> [bbbAdjustment]
		bbbAdjustment.getPricingModel() >> bbbrep
		bbbrep.getRepositoryId() >> "bbbrepId"
		bbbAdjustment.getAdjustment() >> 350.0
		pTools1.round(350.0, 2) >> 350.0
		bbbAdjustment.getCoupon() >> bbbcoupon
		bbbcoupon.getRepositoryId() >> "bcrepId"
		bbbrep.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >> "type1"
		bbbrep.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display1"
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == null
		head.getCallingAppCode() == null
		head.getCurrencyCode() == null
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
		0*mapper.vlogDebug("International Order updating shipping prices")
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == null
		orderPriceInfo.getTotalDeliverySurcharge() == null
		orderPriceInfo.getTotalAssemblyFee() == null
		orderPriceInfo.getDeliveryThreshold() ==null
		orderPriceInfo.getTotalDeliverySurchargeSaving() == null
		orderPriceInfo.getTotalShipping() == 20.0
		orderPriceInfo.getTotalSurcharge() == null
		1*mapper.logError("Error getting thresholdAmount from config keys", _)
		1*mapper.logError("Error while loading order atg.commerce.CommerceException: ")
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup =group.get(0)
		shippingGroup.getShippingGroupId() == "1"
		shippingGroup.getShippingMethod() == "g1Method"
		
		//for populateAddressInResponse
		com.bedbathandbeyond.atg.Address address=shippingGroup.getShippingAddress()
		address.getAddressLine1() =="add1"
		address.getAddressLine2() == null
		address.getCity() == "city"
		address.getState() == "state"
		address.getZipCode() == "postalCode"
		address.getCountry() == "country"
		
		//for populateItemListInResponse
		com.bedbathandbeyond.atg.ItemList itemList =shippingGroup.getItemList()
		List<com.bedbathandbeyond.atg.Item> itemArray = itemList.getItemArray()
		com.bedbathandbeyond.atg.Item item = itemArray.get(0)
		item.getOriginalLineNumber() == "2"
		item.getStatus() != null
		item.getIsPcInd() == true
		item.getPcInfo() != null
		item.getLineNumber()== "5"
		item.getSku() == "skuid"
		item.getQuantity() == 10
		item.getSurcharge() == 90.0
		item.getRegistryId() == "registryId"
		item.getPrice() == 90.0
		item.getFinalPrice() == 0.0
		item.getEcoFee() == null
		item.getEcoFeeSku() ==null
		item.getDeliverySurcharge() == 0.0
		item.getDeliverySurchargePerQty() == 0.0
		item.getDeliverySurchargeSaving() == 200.0
		item.getDeliverySurchargeSku() == "delRefId"
		item.getAssemblyFee() == 500.0
		item.getAssemblyFeePerQty() ==50.0
		item.getAssemblyFeeSku() == "ltlItem"
		item.getIsAssemblyRequested() == true
		
		//populateLTLItemInforForPromotion
		item.getIsLTLSku() == true
		com.bedbathandbeyond.atg.Item item1 = itemArray.get(1)
		item1.getIsLTLSku() == true
		item1.getDeliverySurchargePerQty() == 0.0
		item1.getDeliverySurcharge() == 0.0
		item1.getDeliverySurchargeSaving() == 500.0
		item1.getOriginalLineNumber() == "2"
		item1.getStatus() != null
		item1.getLineNumber() == ""
		item1.getSku() == "skuid"
		item1.getQuantity() == 10
		item1.getPrice() == 90.0
		item1.getFinalPrice() == 0.0
		item1.getIsAssemblyRequested()== true
		item1.getAssemblyFeePerQty() ==50.0
		item1.getAssemblyFee() ==500.0
		item1.getAssemblyFeeSku() == "ltlItem"
		
		//for populateItemAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList adjList = item.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> adjArray = adjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment adjustment= adjArray.get(0)
		adjustment.getAtgPromotionId() == "repId"
		adjustment.getCouponCode() == null
		adjustment.getDiscountAmount() == 60.0
		adjustment.getPromotionType() == "type"
		adjustment.getPromotionDescription() == "display"
		
		//for populateShippingPriceInResponse
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = shippingGroup.getShippingPrice()
		shippingPriceInfo.getShippingAmount() == 60.0
		shippingPriceInfo.getShippingRawAmount() == null
		shippingPriceInfo.getSurcharge() == null
		shippingPriceInfo.getGiftWrapFee() == null
		
		//for populateShippingAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList priceAdjList = shippingPriceInfo.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> priceAdjArray = priceAdjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment priceAdjustment =priceAdjArray.get(0)
		priceAdjustment.getAtgPromotionId() == "bbbrepId"
		priceAdjustment.getCouponCode() == "bcrepId"
		priceAdjustment.getDiscountAmount() == 350.0
		priceAdjustment.getPromotionType() == "type1"
		priceAdjustment.getPromotionDescription() == "display1"
	}
	
	
	def "transformOrderToResponse, when populateLTLItemInforForPromotion is called with assemblyItemId  empty"(){
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		/*com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)*/
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		2*requestHeader.getOrderIdentifier() >> "ABC"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		1*requestHeader.getCurrencyCode() >> null
		1*requestHeader.getCallingAppCode() >> null
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> {throw new CommerceException("")}
		/*oldOrder.getInternationalOrderId() >> "ABC"*/
		0*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		1*bbbOrderPriceInfo.getTotal() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		1*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> {throw new BBBBusinessException("")}
		pTools1.round(0.0, 2) >> 0.0
		//pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		2*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 0.0
		pTools1.round(0.0, 2) >> 0.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		pParameters.put(BBBCheckoutConstants.PARAM_ITEM_LINE_NUMBER,1)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> false
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		1*add.getAddress2() >> ""
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def ShippingGroupCommerceItemRelationship rel1 = Mock()
		def BBBCommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def ItemPriceInfo info1 = Mock()
		def ItemPriceInfo deliveryInfo = Mock()
		def ItemPriceInfo ecoInfo = Mock()
		def DetailedItemPriceInfo det = Mock()
		def DetailedItemPriceInfo det1 = Mock()
		def atg.core.util.Range range =Mock()
		def LTLAssemblyFeeCommerceItem ltlItem = Mock()
		def LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,requestItem)
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,sgciRelationMap)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel,rel1]
		2*rel.getCommerceItem() >>cItem
		cItem.getPriceInfo() >> info1
		2*rel.getRange() >> range
		1*info1.getCurrentPriceDetailsForRange(rel.getRange()) >>[det, det1]
		cItem.isLtlItem() >> true
		cItem.getDeliveryItemId() >> "cdId"
		cItem.getAssemblyItemId() >>  ""
		0*pOrder.getCommerceItem(cItem.getAssemblyItemId()) >> ltlItem
		1*pOrder.getCommerceItem(cItem.getDeliveryItemId()) >> deliveryCommerceItem
		deliveryCommerceItem.getPriceInfo() >> deliveryInfo
		deliveryInfo.getAmount() >> 300.0
		det.getQuantity() >> 10.0
		det1.getQuantity() >> 10.0
		pTools1.round(10.0, 2) == 10.0
		
		pOrder.getSiteId() >> "siteId"
		pTools1.calculateItemSurchargeInSG(pOrder.getSiteId(), rel)  >> 90.0
		pTools1.round(90.0, 2) >> 90.0
		requestItem.getOriginalLineNumber() >> "2"
		requestItem.getStatus() >> status
		requestItem.getIsPcInd() >> true
		requestItem.getPcInfo() >> pcinf
		requestItem.getLineNumber() >> "5"
		cItem.getCatalogRefId() >> "skuid"
		cItem.getRegistryId() >> "registryId"
		
		//for populateItemAdjustmentListInResponse in populateItemListInResponse
		def PricingAdjustment adj =Mock()
		def PricingAdjustment adj1 =Mock()
		def RepositoryItem promotionItem = Mock()
		def RepositoryItem coupon = Mock()
		
		pParameters.put(BBBCheckoutConstants.IS_PARTIALLY_SHIPPED , true)
		det.getAdjustments() >>  [adj]
		det1.getAdjustments() >>  null
		requestItem.getSku() >> "sku"
		
		adj.getPricingModel() >> promotionItem
		1*promotionItem.getRepositoryId() >> "repId"
		adj.getCoupon() >> null
		adj.getAdjustment() >> 60.0
		pTools1.round(60.0,2) >> 60.0
		1*promotionItem.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >>"type"
		1*promotionItem.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display"
		//end populateItemAdjustments
		
		info1.getListPrice() >> 90.0
		pTools1.round(90.0,2) >> 90.0
		1*det.getDetailedUnitPrice() >> 0.0
		pTools1.round(0.0,2) >> 0.0
		1*cItem.getId() >>"cID"
		Map<String, String> ecoFeeMap = new HashMap()
		ecoFeeMap.put(cItem.getId(),"")
	
		
		//for populateLTLItemInforForPromotion
		def ItemPriceInfo ltlInfo =Mock()
		cItem.getQuantity() >> 10.0
		pTools1.round(30.0) >> 30.0
		pTools1.round(300.0) >> 300.0
		deliveryCommerceItem.getCatalogRefId() >>"delRefId"
		deliveryInfo.getRawTotalPrice() >> 500.0
		pTools1.round(200.0,2)  >> 200.0
		pTools1.round(50.0,2)  >> 50.0
		pTools1.round(500.0,2)  >> 500.0
		// end  populateLTLItemInforForPromotion
		
		//for populateShippingPriceInResponse in populateShippingGroupsInResponse
		def GiftWrapCommerceItem giftWrap =Mock()
		def ItemPriceInfo giftInfo =Mock()
		1*bbbShippingPriceInfo1.getFinalShipping() >> 60.0
		pTools1.round(60.0,2)  >> 60.0
		1*bbbShippingPriceInfo1.getRawShipping() >> 0.0
		pTools1.round(0.0,2)  >> 0.0
		1*g1.getGiftWrapCommerceItem() >> null
		
		//for populateShippingAdjustmentListInResponse in populateShippingPriceInResponse
		def PricingAdjustment bbbAdjustment =Mock()
		def RepositoryItem bbbrep =Mock()
		def RepositoryItem bbbcoupon = Mock()
		
		bbbShippingPriceInfo1.getAdjustments() >> [bbbAdjustment]
		bbbAdjustment.getPricingModel() >> bbbrep
		bbbrep.getRepositoryId() >> "bbbrepId"
		bbbAdjustment.getAdjustment() >> 350.0
		pTools1.round(350.0, 2) >> 350.0
		bbbAdjustment.getCoupon() >> bbbcoupon
		bbbcoupon.getRepositoryId() >> "bcrepId"
		bbbrep.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >> "type1"
		bbbrep.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display1"
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == null
		head.getCallingAppCode() == null
		head.getCurrencyCode() == null
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
		0*mapper.vlogDebug("International Order updating shipping prices")
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == null
		orderPriceInfo.getTotalDeliverySurcharge() == null
		orderPriceInfo.getTotalAssemblyFee() == null
		orderPriceInfo.getDeliveryThreshold() ==null
		orderPriceInfo.getTotalDeliverySurchargeSaving() == null
		orderPriceInfo.getTotalShipping() == 20.0
		orderPriceInfo.getTotalSurcharge() == null
		1*mapper.logError("Error getting thresholdAmount from config keys", _)
		1*mapper.logError("Error while loading order atg.commerce.CommerceException: ")
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup =group.get(0)
		shippingGroup.getShippingGroupId() == "1"
		shippingGroup.getShippingMethod() == "g1Method"
		
		//for populateAddressInResponse
		com.bedbathandbeyond.atg.Address address=shippingGroup.getShippingAddress()
		address.getAddressLine1() =="add1"
		address.getAddressLine2() == null
		address.getCity() == "city"
		address.getState() == "state"
		address.getZipCode() == "postalCode"
		address.getCountry() == "country"
		
		//for populateItemListInResponse
		com.bedbathandbeyond.atg.ItemList itemList =shippingGroup.getItemList()
		List<com.bedbathandbeyond.atg.Item> itemArray = itemList.getItemArray()
		com.bedbathandbeyond.atg.Item item = itemArray.get(0)
		item.getOriginalLineNumber() == "2"
		item.getStatus() != null
		item.getIsPcInd() == true
		item.getPcInfo() != null
		item.getLineNumber()== "5"
		item.getSku() == "skuid"
		item.getQuantity() == 10
		item.getSurcharge() == 90.0
		item.getRegistryId() == "registryId"
		item.getPrice() == 90.0
		item.getFinalPrice() == 0.0
		item.getEcoFee() == null
		item.getEcoFeeSku() ==null
		item.getDeliverySurcharge() == 0.0
		item.getDeliverySurchargePerQty() == 0.0
		item.getDeliverySurchargeSaving() == 200.0
		item.getDeliverySurchargeSku() == "delRefId"
		item.getAssemblyFee() == null
		item.getAssemblyFeePerQty() ==null
		item.getAssemblyFeeSku() == null
		item.getIsAssemblyRequested() == false
		
		//populateLTLItemInforForPromotion
		item.getIsLTLSku() == true
		com.bedbathandbeyond.atg.Item item1 = itemArray.get(1)
		item1.getIsLTLSku() == true
		item1.getDeliverySurchargePerQty() == 0.0
		item1.getDeliverySurcharge() == 0.0
		item1.getDeliverySurchargeSaving() == 500.0
		item1.getOriginalLineNumber() == "2"
		item1.getStatus() != null
		item1.getLineNumber() == ""
		item1.getSku() == "skuid"
		item1.getQuantity() == 10
		item1.getPrice() == 90.0
		item1.getFinalPrice() == 0.0
		item1.getIsAssemblyRequested()== false
		item1.getAssemblyFeePerQty() ==null
		item1.getAssemblyFee() ==null
		item1.getAssemblyFeeSku() == null
		
		//for populateItemAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList adjList = item.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> adjArray = adjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment adjustment= adjArray.get(0)
		adjustment.getAtgPromotionId() == "repId"
		adjustment.getCouponCode() == null
		adjustment.getDiscountAmount() == 60.0
		adjustment.getPromotionType() == "type"
		adjustment.getPromotionDescription() == "display"
		
		//for populateShippingPriceInResponse
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = shippingGroup.getShippingPrice()
		shippingPriceInfo.getShippingAmount() == 60.0
		shippingPriceInfo.getShippingRawAmount() == null
		shippingPriceInfo.getSurcharge() == null
		shippingPriceInfo.getGiftWrapFee() == null
		
		//for populateShippingAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList priceAdjList = shippingPriceInfo.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> priceAdjArray = priceAdjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment priceAdjustment =priceAdjArray.get(0)
		priceAdjustment.getAtgPromotionId() == "bbbrepId"
		priceAdjustment.getCouponCode() == "bcrepId"
		priceAdjustment.getDiscountAmount() == 350.0
		priceAdjustment.getPromotionType() == "type1"
		priceAdjustment.getPromotionDescription() == "display1"
	}
	
	def "transformOrderToResponse, returns item list as null if CommerceItem is not an instance of BBBCommerceItem in populateItemListInResponse"(){
		
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		3*requestHeader.getOrderIdentifier() >> "TBS"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		2*requestHeader.getCurrencyCode() >> currency
		2*requestHeader.getCallingAppCode() >> "appCode"
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> oldOrder
		1*oldOrder.getInternationalOrderId() >> "intOrderId"
		1*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		2*bbbOrderPriceInfo.getTotal() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 600.0
		pTools1.round(600.0, 2) >> 600.0
		2*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 200.0
		pTools1.round(200.0, 2) >> 200.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> ["500.0"]
		pTools1.round(500.0, 2) >> 500.0
		pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		2*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 30.0
		pTools1.round(30.0, 2) >> 30.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> true
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		2*add.getAddress2() >> "add2"
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def atg.core.util.Range range =Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		/*Map<ShippingGroupCommerceItemRelationship, Item> sgciRelationMap = new HashMap()
		sgciRelationMap.put(rel,requestItem)*/
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,null)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		
		1*g1.getCommerceItemRelationships() >> [rel]
		1*rel.getCommerceItem() >>cItem
		//ends populateItemListInResponse
		
		//for populateShippingPriceInResponse in populateShippingGroupsInResponse
		def GiftWrapCommerceItem giftWrap =Mock()
		def ItemPriceInfo giftInfo =Mock()
		1*bbbShippingPriceInfo1.getFinalShipping() >> 60.0
		pTools1.round(60.0,2)  >> 60.0
		2*bbbShippingPriceInfo1.getRawShipping() >> 80.0
		pTools1.round(80.0,2)  >> 80.0
		1*g1.getGiftWrapCommerceItem() >> giftWrap
		giftWrap.getPriceInfo() >>giftInfo
		1*giftInfo.getAmount() >> 250.0
		pTools1.round(250.0,2)  >> 250.0
		
		//for populateShippingAdjustmentListInResponse in populateShippingPriceInResponse
		def PricingAdjustment bbbAdjustment =Mock()
		def RepositoryItem bbbrep =Mock()
		def RepositoryItem bbbcoupon = Mock()
		
		bbbShippingPriceInfo1.getAdjustments() >> [bbbAdjustment]
		bbbAdjustment.getPricingModel() >> bbbrep
		bbbrep.getRepositoryId() >> "bbbrepId"
		bbbAdjustment.getAdjustment() >> 350.0
		pTools1.round(350.0, 2) >> 350.0
		bbbAdjustment.getCoupon() >> bbbcoupon
		bbbcoupon.getRepositoryId() >> "bcrepId"
		bbbrep.getPropertyValue(BBBCheckoutConstants.TYPE_DETAILS) >> "type1"
		bbbrep.getPropertyValue(BBBCheckoutConstants.DISPLAY_NAME) >> "display1"
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == "TBS"
		head.getCallingAppCode() == "appCode"
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == 50.0
		orderPriceInfo.getTotalDeliverySurcharge() == 600.0
		orderPriceInfo.getTotalAssemblyFee() == 200.0
		orderPriceInfo.getDeliveryThreshold() ==500.0
		orderPriceInfo.getTotalDeliverySurchargeSaving() == 100.0
		orderPriceInfo.getTotalShipping() == 20.0
		orderPriceInfo.getTotalSurcharge() == 30.0
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup =group.get(0)
		shippingGroup.getShippingGroupId() == "1"
		shippingGroup.getShippingMethod() == "g1Method"
		
		//for populateAddressInResponse
		com.bedbathandbeyond.atg.Address address=shippingGroup.getShippingAddress()
		address.getAddressLine1() =="add1"
		address.getCity() == "city"
		address.getState() == "state"
		address.getZipCode() == "postalCode"
		address.getCountry() == "country"
		
		//for populateItemListInResponse
		com.bedbathandbeyond.atg.ItemList itemList =shippingGroup.getItemList()
		List<com.bedbathandbeyond.atg.Item> itemArray = itemList.getItemArray()
		itemArray.isEmpty() == true
		
		//for populateShippingPriceInResponse
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = shippingGroup.getShippingPrice()
		shippingPriceInfo.getShippingAmount() == 60.0
		shippingPriceInfo.getShippingRawAmount() == 80.0
		shippingPriceInfo.getSurcharge() == 30.0
		shippingPriceInfo.getGiftWrapFee() == 250.0
		
		//for populateShippingAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList priceAdjList = shippingPriceInfo.getAdjustmentList()
		List<com.bedbathandbeyond.atg.Adjustment> priceAdjArray = priceAdjList.getAdjustmentArray()
		com.bedbathandbeyond.atg.Adjustment priceAdjustment =priceAdjArray.get(0)
		priceAdjustment.getAtgPromotionId() == "bbbrepId"
		priceAdjustment.getCouponCode() == "bcrepId"
		priceAdjustment.getDiscountAmount() == 350.0
		priceAdjustment.getPromotionType() == "type1"
		priceAdjustment.getPromotionDescription() == "display1"
	}
	
	def "transformOrderToResponse, returns AdjustmentList as null if PricingModel is null in populateShippingAdjustmentListInResponse()"(){
		
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		3*requestHeader.getOrderIdentifier() >> "TBS"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		2*requestHeader.getCurrencyCode() >> currency
		2*requestHeader.getCallingAppCode() >> "appCode"
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> oldOrder
		1*oldOrder.getInternationalOrderId() >> "intOrderId"
		1*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def BBBHardGoodShippingGroup g1 =Mock()
		def ShippingGroup g2 =Mock()
		def BBBHardGoodShippingGroup g3 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		2*bbbOrderPriceInfo.getTotal() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 600.0
		pTools1.round(600.0, 2) >> 600.0
		2*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 200.0
		pTools1.round(200.0, 2) >> 200.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> ["500.0"]
		pTools1.round(500.0, 2) >> 500.0
		pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1,g2]
		2*g1.getPriceInfo() >> bbbShippingPriceInfo1
		1*bbbShippingPriceInfo1.getAmount() >> 20.0
		pTools1.round(20.0, 2) >> 20.0
		bbbShippingPriceInfo1.getFinalSurcharge() >> 30.0
		pTools1.round(30.0, 2) >> 30.0
		//ends populateOrderPriceInResponse in PopulatePricingResponse
		
		//for  populateShippingGroupsInResponse in PopulatePricingResponse
		def atg.core.util.Address add =Mock()
		Map<String,String> shippingGroupRelationMap = new HashMap()
		2*g1.getId() >> "g1Id"
		shippingGroupRelationMap.put(g1.getId(), "1")
		pParameters.put(BBBCheckoutConstants.PARAM_SHIPPING_GROUP_ID_RELATION,shippingGroupRelationMap)
		
		mapper.isPartiallyShipped(pPricingRequestInfo) >> true
		1*g1.getShippingMethod() >>"g1Method"
		1*g1.getShippingAddress() >>add
		
		//for populateaddressinresponse in  populateShippingGroupsInResponse
		1*add.getAddress1() >> "add1"
		2*add.getAddress2() >> "add2"
		1*add.getCity() >> "city"
		1*add.getState() >> "state"
		1*add.getPostalCode() >>"postalCode"
		1*add.getCountry() >>"country"
		//ends populateaddressinresponse
		
		//for populateItemListInResponse in populateShippingGroupsInResponse
		def ShippingGroupCommerceItemRelationship rel =Mock()
		def CommerceItem cItem =Mock()
		def Item requestItem =Mock()
		def atg.core.util.Range range =Mock()
		def PcInfoType pcinf = PcInfoType.Factory.newInstance()
		def CommerceItem ecoFeeitem =Mock()
		pParameters.put(BBBCheckoutConstants.PARAM_SGCI_RELATION_TO_ITEM,null)
		Item.Status.Enum status =  new Item.Status.Enum("SHIPPED",1)
		1*g1.getCommerceItemRelationships() >> [rel]
		1*rel.getCommerceItem() >>cItem
		//ends populateItemListInResponse
		
		//for populateShippingPriceInResponse in populateShippingGroupsInResponse
		def GiftWrapCommerceItem giftWrap =Mock()
		def ItemPriceInfo giftInfo =Mock()
		1*bbbShippingPriceInfo1.getFinalShipping() >> 60.0
		pTools1.round(60.0,2)  >> 60.0
		2*bbbShippingPriceInfo1.getRawShipping() >> 80.0
		pTools1.round(80.0,2)  >> 80.0
		1*g1.getGiftWrapCommerceItem() >> giftWrap
		giftWrap.getPriceInfo() >>giftInfo
		1*giftInfo.getAmount() >> 250.0
		pTools1.round(250.0,2)  >> 250.0
		
		//for populateShippingAdjustmentListInResponse in populateShippingPriceInResponse
		def PricingAdjustment bbbAdjustment =Mock()
		def RepositoryItem bbbrep =Mock()
		def RepositoryItem bbbcoupon = Mock()
		bbbShippingPriceInfo1.getAdjustments() >> [bbbAdjustment]
		bbbAdjustment.getPricingModel() >> null
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == "TBS"
		head.getCallingAppCode() == "appCode"
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == 50.0
		orderPriceInfo.getTotalDeliverySurcharge() == 600.0
		orderPriceInfo.getTotalAssemblyFee() == 200.0
		orderPriceInfo.getDeliveryThreshold() ==500.0
		orderPriceInfo.getTotalDeliverySurchargeSaving() == 100.0
		orderPriceInfo.getTotalShipping() == 20.0
		orderPriceInfo.getTotalSurcharge() == 30.0
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()
		com.bedbathandbeyond.atg.ShippingGroup shippingGroup =group.get(0)
		shippingGroup.getShippingGroupId() == "1"
		shippingGroup.getShippingMethod() == "g1Method"
		
		//for populateAddressInResponse
		com.bedbathandbeyond.atg.Address address=shippingGroup.getShippingAddress()
		address.getAddressLine1() =="add1"
		address.getCity() == "city"
		address.getState() == "state"
		address.getZipCode() == "postalCode"
		address.getCountry() == "country"
		
		//for populateItemListInResponse
		com.bedbathandbeyond.atg.ItemList itemList =shippingGroup.getItemList()
		List<com.bedbathandbeyond.atg.Item> itemArray = itemList.getItemArray()
		itemArray.isEmpty() == true
		
		//for populateShippingPriceInResponse
		com.bedbathandbeyond.atg.ShippingPriceInfo shippingPriceInfo = shippingGroup.getShippingPrice()
		shippingPriceInfo.getShippingAmount() == 60.0
		shippingPriceInfo.getShippingRawAmount() == 80.0
		shippingPriceInfo.getSurcharge() == 30.0
		shippingPriceInfo.getGiftWrapFee() == 250.0
		
		//for populateShippingAdjustmentListInResponse
		com.bedbathandbeyond.atg.AdjustmentList priceAdjList = shippingPriceInfo.getAdjustmentList()
		priceAdjList == null
	}
	
	
	def "transformOrderToResponse, returns shippinglist as null if ShippingGroup is not an instance of BBBHardGoodShippingGroup in populateShippingGroupsInResponse"(){
		
		given:
		mapper = Spy()
		def BBBPricingTools pTools1 =Mock()
		mapper.setPricingTools(pTools1)
		def BBBOrder pOrder =Mock()
		def PricingRequest pWSRequest =Mock()
		Map<String, Object> pParameters =new HashMap()
		
		//for populateResponseHeader
		def MessageHeader requestHeader = Mock()
		com.bedbathandbeyond.atg.Site.Enum siteId = new com.bedbathandbeyond.atg.Site.Enum("BedBathUS",1)
		com.bedbathandbeyond.atg.Currency.Enum currency = new com.bedbathandbeyond.atg.Currency.Enum("USD",1)
		pWSRequest.getHeader() >> requestHeader
		1*requestHeader.getOrderId() >> "orderId"
		3*requestHeader.getOrderIdentifier() >> "TBS"
		1*requestHeader.getSiteId() >> siteId
		1*requestHeader.getOrderDate() >> Calendar.getInstance()
		2*requestHeader.getCurrencyCode() >> currency
		2*requestHeader.getCallingAppCode() >> "appCode"
		//ends populateResponseHeader
		
		//for populatePricingResponse
		def PricingRequestInfo pPricingRequestInfo =Mock()
		def BBBOrderTools tools =Mock()
		def BBBOrder oldOrder =Mock()
		mapper.setOrderTools(tools)
		1*pOrder.getId() >> "pOrderId"
		1*pWSRequest.getRequest() >> pPricingRequestInfo
		1*tools.getOrderFromOnlineOrBopusOrderNumber(_) >> oldOrder
		1*oldOrder.getInternationalOrderId() >> "intOrderId"
		1*mapper.updateISShippingCharges(pOrder) >> {}
		
		//for populateOrderPriceInResponse in PopulatePricingResponse
		def BBBOrderPriceInfo bbbOrderPriceInfo =Mock()
		def BBBCatalogToolsImpl impl =Mock()
		def HardgoodShippingGroup g1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo1 =Mock()
		def BBBShippingPriceInfo bbbShippingPriceInfo3 =Mock()
		
		pOrder.getPriceInfo() >> bbbOrderPriceInfo
		2*bbbOrderPriceInfo.getTotal() >> 50.0
		pTools1.round(50.0,2) >> 50.0
		bbbOrderPriceInfo.getDeliverySurchargeTotal() >> 600.0
		pTools1.round(600.0, 2) >> 600.0
		2*bbbOrderPriceInfo.getAssemblyFeeTotal() >> 200.0
		pTools1.round(200.0, 2) >> 200.0
		mapper.setCatalogTools(impl)
		1*impl.getAllValuesForKey(BBBCmsConstants.LTL_CONFIG_KEY_TYPE_NAME, BBBCmsConstants.THRESHOLD_DELIVERY_AMOUNT) >> ["500.0"]
		pTools1.round(500.0, 2) >> 500.0
		pTools1.round(100.0, 2) >> 100.0
		
		pOrder.getShippingGroups() >> [g1]
		//end populateShippingAdjustmentListInResponse
		//end populateShippingPriceInResponse
		//end PricingResponseInfo
		
		when:
		PricingResponse resp= mapper.transformOrderToResponse(pOrder, pWSRequest, pParameters)
		
		then:
		//for populateresponse header
		MessageHeader head = resp.getHeader()
		head.getOrderIdentifier() == "TBS"
		head.getCallingAppCode() == "appCode"
		head.getSiteId() != null
		head.getOrderDate() != null
		head.getTimestamp() != null
		
		//for populatePricingResponse and populateOrderPriceInResponse
	//	1*mapper.vlogDebug("International Order updating shipping prices")
		PricingResponseInfo respInfo =resp.getResponse()
		com.bedbathandbeyond.atg.OrderPriceInfo orderPriceInfo = respInfo.getOrderPrice()
		orderPriceInfo.getOrderTotal() == 50.0
		orderPriceInfo.getTotalDeliverySurcharge() == 600.0
		orderPriceInfo.getTotalAssemblyFee() == 200.0
		orderPriceInfo.getDeliveryThreshold() ==500.0
		
		//for populateShippingGroupsInResponse
		com.bedbathandbeyond.atg.ShippingList shipList = respInfo.getShippingGroups()
		List <com.bedbathandbeyond.atg.ShippingGroup> group = shipList.getShippingGroupArray()
		group.isEmpty() == true
	}
	
	
	def"updateISShippingCharges, when shipping method is Shipping_Charge_Without_Promotion and ashipping list contains true"(){
		
		given:
		mapper = Spy()
		Order order = Mock()
		HardgoodShippingGroup hd =Mock()
		BBBShippingPriceInfo priceInfo =Mock()
		PricingAdjustment adj =Mock()
		BBBPricingTools pTools =Mock()
		BBBOrderPriceInfo pinfo = Mock()
		
		mapper.setPricingTools(pTools)
		mapper.setCatalogTools(cTools)
		
		1*mapper.getShippingMethodList() >> ["Shipping_Charge_Without_Promotion"]
		1*cTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, "Shipping_Charge_Without_Promotion")  >> ["true"]
		
		1*order.getShippingGroups() >> [hd]
		1*hd.getPriceInfo() >> priceInfo
		
		List<PricingAdjustment> list = new ArrayList()
		list.add(adj)
		1*priceInfo.getAdjustments() >> list
		
		2*order.getSiteId() >> "site"
		2*hd.getShippingMethod() >> "ship1"
		
		1*pTools.calculateShippingCost(order.getSiteId(), hd.getShippingMethod(), hd) >> 100.0
		1*order.getPriceInfo() >> pinfo
		
		when: 
		mapper.updateISShippingCharges(order)
		
		then:
		list.contains(adj) == false
		1*priceInfo.setDiscounted(false)
		1*priceInfo.setAmountIsFinal(true)
		1*priceInfo.setAmount(100.0)
		1*priceInfo.setFinalShipping(100.0)
		1*pinfo.setShipping(100.0)		
		0*mapper.logDebug(" using all shipping promotion for user as flag set is Shipping_Charge_With_Promotion ")
		0*mapper.logDebug(" applying zero shipping charge for user as flag set is Shipping_Charge_Zero ");
	}
	
	def"updateISShippingCharges, when shipping method is Shipping_Charge_Zero and shipping list contains true"(){
		
		given:
		mapper = Spy()
		Order order = Mock()
		HardgoodShippingGroup hd =Mock()
		BBBShippingPriceInfo priceInfo =Mock()
		PricingAdjustment adj =Mock()
		BBBPricingTools pTools =Mock()
		BBBOrderPriceInfo pinfo = Mock()
		
		mapper.setPricingTools(pTools)
		mapper.setCatalogTools(cTools)
	
		1*mapper.getShippingMethodList() >> ["Shipping_Charge_Zero"]
		1*cTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, "Shipping_Charge_Zero")  >> ["true"]
		
		1*order.getShippingGroups() >> [hd]
		1*hd.getPriceInfo() >> priceInfo
		
		List<PricingAdjustment> list = new ArrayList()
		list.add(adj)
		1*priceInfo.getAdjustments() >> list
		
		1*order.getPriceInfo() >> pinfo
		
		when:
		mapper.updateISShippingCharges(order)
		
		then:
		list.contains(adj) == false
		1*priceInfo.setDiscounted(false)
		1*priceInfo.setAmountIsFinal(true)
		1*priceInfo.setAmount(0.0)
		1*priceInfo.setRawShipping(0.00);
		1*priceInfo.setFinalShipping(0.0)
		1*priceInfo.setFinalSurcharge(0.00)
		1*pinfo.setShipping(0.0)
		0*mapper.logDebug(" using all shipping promotion for user as flag set is Shipping_Charge_With_Promotion ");
	}
	
	def"updateISShippingCharges, when shipping method is Shipping_Charge_With_Promotion and shipping list contains true"(){
		
		given:
		mapper = Spy()
		Order order = Mock()
		HardgoodShippingGroup hd =Mock()
		BBBShippingPriceInfo priceInfo =Mock()
		PricingAdjustment adj =Mock()
		BBBPricingTools pTools =Mock()
		BBBOrderPriceInfo pinfo = Mock()
		
		mapper.setPricingTools(pTools)
		mapper.setCatalogTools(cTools)
		
		1*mapper.getShippingMethodList() >> ["Shipping_Charge_With_Promotion"]
		1*cTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, "Shipping_Charge_With_Promotion")  >> ["true"]
		
		when:
		mapper.updateISShippingCharges(order)
		
		then:
		1*mapper.logDebug(" using all shipping promotion for user as flag set is Shipping_Charge_With_Promotion ")
	}
	
	def"updateISShippingCharges, when shipping list contains false"(){
		
		given:
		mapper = Spy()
		Order order = Mock()
		HardgoodShippingGroup hd =Mock()
		BBBShippingPriceInfo priceInfo =Mock()
		PricingAdjustment adj =Mock()
		BBBPricingTools pTools =Mock()
		BBBOrderPriceInfo pinfo = Mock()
		
		mapper.setPricingTools(pTools)
		mapper.setCatalogTools(cTools)
		
		1*mapper.getShippingMethodList() >> ["Shipping_Charge_Without_Promotion","Shipping_Charge_With_Promotion" ,"Shipping_Charge_Zero"]
		1*cTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, "Shipping_Charge_Without_Promotion")  >> ["false"]
		1*cTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, "Shipping_Charge_With_Promotion")  >> ["false"]
		1*cTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, "Shipping_Charge_Zero")  >> ["false"]
		
		when:
		mapper.updateISShippingCharges(order)
		
		then:
		0*priceInfo.setDiscounted(false)
		0*priceInfo.setAmountIsFinal(true)
		0*priceInfo.setAmount(100.0)
		0*priceInfo.setFinalShipping(100.0)
		0*pinfo.setShipping(100.0)
	}
	
	def"updateISShippingCharges, when shipping method is normal and shipping list contains true"(){
		
		given:
		mapper = Spy()
		Order order = Mock()
		HardgoodShippingGroup hd =Mock()
		BBBShippingPriceInfo priceInfo =Mock()
		PricingAdjustment adj =Mock()
		BBBPricingTools pTools =Mock()
		BBBOrderPriceInfo pinfo = Mock()
		
		mapper.setPricingTools(pTools)
		mapper.setCatalogTools(cTools)
		
		1*mapper.getShippingMethodList() >> ["normal"]
		1*cTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, "normal")  >> ["true"]
		
		when:
		mapper.updateISShippingCharges(order)
		
		then:
		0*priceInfo.setDiscounted(false)
		0*priceInfo.setAmountIsFinal(true)
		0*priceInfo.setAmount(100.0)
		0*priceInfo.setFinalShipping(100.0)
		0*pinfo.setShipping(100.0)
	}
	
	def"updateISShippingCharges, when BBBSystemException and BBBBusinessException is thrown"(){
		
		given:
		mapper = Spy()
		Order order = Mock()
		HardgoodShippingGroup hd =Mock()
		BBBShippingPriceInfo priceInfo =Mock()
		PricingAdjustment adj =Mock()
		BBBPricingTools pTools =Mock()
		BBBOrderPriceInfo pinfo = Mock()
		
		mapper.setPricingTools(pTools)
		mapper.setCatalogTools(cTools)
		
		1*mapper.getShippingMethodList() >> ["normal", "express"]
		1*cTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, "normal")  >> {throw new BBBSystemException("")}
		1*cTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, "express")  >> {throw new BBBBusinessException("")}
		
		when:
		mapper.updateISShippingCharges(order)
		
		then:
		2*mapper.logError("Error getting INTERNATIONAL_SHIPPING from config keys", _)
	}
	
	def"updateISShippingCharges, when shippingList is emtpy"(){
		
		given:
		mapper = Spy()
		Order order = Mock()
		BBBShippingPriceInfo priceInfo =Mock()
		PricingAdjustment adj =Mock()
		BBBOrderPriceInfo pinfo = Mock()
	
		mapper.setCatalogTools(cTools)
		
		1*mapper.getShippingMethodList() >> ["normal"]
		1*cTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, "normal")  >> []
		
		when:
		mapper.updateISShippingCharges(order)
		
		then:
		0*priceInfo.setDiscounted(false)
		0*priceInfo.setAmountIsFinal(true)
		0*priceInfo.setAmount(100.0)
		0*priceInfo.setFinalShipping(100.0)
		0*pinfo.setShipping(100.0)
	}
	
	
	def"updateISShippingCharges, when shipping method is Shipping_Charge_Without_Promotion and PricingException is thrown"(){
		
		given:
		mapper = Spy()
		Order order = Mock()
		HardgoodShippingGroup hd =Mock()
		BBBShippingPriceInfo priceInfo =Mock()
		PricingAdjustment adj =Mock()
		BBBPricingTools pTools =Mock()
		BBBOrderPriceInfo pinfo = Mock()
		
		mapper.setPricingTools(pTools)
		mapper.setCatalogTools(cTools)
		
		mapper.getShippingMethodList() >> ["Shipping_Charge_Without_Promotion"]
		cTools.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, "Shipping_Charge_Without_Promotion")  >> ["true"]
		
		1*order.getShippingGroups() >> [hd]
		1*hd.getPriceInfo() >> priceInfo
		
		List<PricingAdjustment> list = new ArrayList()
		list.add(adj)
		1*priceInfo.getAdjustments() >> list
		
		2*order.getSiteId() >> "site"
		2*hd.getShippingMethod() >> "ship1"
		
		1*pTools.calculateShippingCost(order.getSiteId(), hd.getShippingMethod(), hd) >> {throw new PricingException("")}
		
		when:
		mapper.updateISShippingCharges(order)
		
		then:
		list.contains(adj) == false
		1*priceInfo.setDiscounted(false)
		1*priceInfo.setAmountIsFinal(true)
		0*priceInfo.setAmount(100.0)
		0*priceInfo.setFinalShipping(100.0)
		0*pinfo.setShipping(100.0)
		1*mapper.logError("Exception While calculating Shipping Price For International Orders", _)
	}	
}