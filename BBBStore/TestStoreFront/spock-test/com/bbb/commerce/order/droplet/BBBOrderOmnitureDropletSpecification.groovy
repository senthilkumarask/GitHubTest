package com.bbb.commerce.order.droplet

import java.io.IOException;
import java.util.HashMap;
import java.util.List
import java.util.Map;

import javax.servlet.ServletException

import org.apache.commons.lang.StringUtils;

import atg.commerce.order.CreditCard
import atg.commerce.order.OrderHolder;

import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools
import com.bbb.commerce.catalog.vo.SiteVO;
import com.bbb.commerce.common.BBBRepositoryContactInfo
import com.bbb.commerce.order.TBSShippingInfo;
import com.bbb.commerce.order.vo.OrderOmnitureVO;
import com.bbb.commerce.pricing.BBBPricingTools
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.bbb.order.bean.GiftWrapCommerceItem
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.TBSCommerceItem
import com.bbb.order.bean.TBSItemInfo
import com.bbb.utils.BBBUtility;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext.ExpectedTypeRootLoader;

import atg.commerce.order.AuxiliaryData
import atg.commerce.order.CommerceItem
import atg.commerce.order.CommerceItemNotFoundException
import atg.commerce.order.HardgoodShippingGroup
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.OrderImpl
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PropertyNameConstants;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupImpl
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.PricingAdjustment
import atg.commerce.pricing.ShippingPriceInfo
import atg.commerce.pricing.TaxPriceInfo;
import atg.commerce.pricing.UnitPriceBean;
import atg.commerce.promotion.PromotionTools
import atg.core.util.Address
import atg.core.util.Range;
import atg.nucleus.naming.ComponentName;
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemDescriptor
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile
import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;

/**
 * 
 * @author Velmurugan Moorthy
 * 
 * This class is to unit test the BBBOrderOmnitureDroplet
 *
 *
 */

/*
 * Change required in Java class are listed below 
 * 
 *  orderMock.isExpressCheckOut() - removed final from this method - OrderImpl
 *   
 */

class BBBOrderOmnitureDropletSpecification extends BBBExtendedSpec {

	private BBBOrderOmnitureDroplet orderOmnitureDropletSpy
	private BBBCatalogTools catalogToolsMock
	private BBBPricingTools pricingToolsMock
	private BBBPromotionTools promotionToolsMock
	private BBBEximManager eximPricingManagerMock
	
	private static final DISPLAY_NAME = "displayName"
	
	def setup() {
		
		orderOmnitureDropletSpy = Spy()
		
		catalogToolsMock = Mock()
		pricingToolsMock = Mock()
		eximPricingManagerMock = Mock()
		promotionToolsMock = Mock()
		
		orderOmnitureDropletSpy.setLoggingDebug(true)
		orderOmnitureDropletSpy.setCatalogTools(catalogToolsMock)
		orderOmnitureDropletSpy.setPricingTools(pricingToolsMock)
		orderOmnitureDropletSpy.setPromotionTools(promotionToolsMock)
		orderOmnitureDropletSpy.setBbbEximPricingManager(eximPricingManagerMock)
			
	}
	
  /*================================================
   *											   *  
   * Service - Test cases - STARTS   			   * 
   * 											   * 
   * 											   * 
   * Method Signature :    						   * 
   * 											   * 
   * public void service(				           * 
   *  final DynamoHttpServletRequest pRequest,     * 
   *  final DynamoHttpServletResponse pResponse    * 
   *  )										       * 									
   *  throws ServletException,	IOException	       * 
   *================================================
   */
	

	/*
	 * Negative(alternate) branches covered : 
	 * 
	 * 
	 * appendProdAndEvents() -
	 *  
	 * #439 - Commerce item is not of type BBBCommerceItem - !(commerceItem instanceof BBBCommerceItem)
	 * #449 - Sale price is less than 0 - (salePrice > 0)
	 * #585 - cisiRel.getShippingGroup() instanceof BBBStoreShippingGroup - true & false branch
	 * 
	 * createPromoStr() 
	 * 
	 * #314 - !firstItem - true
	 * #323 - !firstOrderItem - true
	 * #321 - Promo is neither ITEM_PROMO not ORDER_PROMO - currentPromo.getItemDescriptor().getItemDescriptorName().equalsIgnoreCase(BBBCoreConstants.ORDER_DISCOUNT)
	 * #312 - currentPromo.getItemDescriptor() - triggers - RepositoryException covered
	 * #503 - BBBUtility.isNotEmpty(assemblyItemId) - both branches covered
	 * #505 - (assemblyFeeCommerceItem != null) - assemblyFeeCommerceItem - null - scenario
	 * #507 - (assemblyFee!=null && assemblyFee > 0) - aseemblyFee is 0 
	 * 
	 *  
	 */

   def "service - Order omniture details retrieved successfully (happy flow)" () {
	   
	   given : 
	   
	   def totalAdjustment = 10.00
	   def shippingSavings = 5.00
	   def rawShippingTotal = 5.00
	   def salePrice1 = 350.00
	   def salePrice2 = 15.00
	   def salePrice3 = 0.00
	   def listPrice1 = 330.00
	   def listPrice2 = 18.00
	   def listPrice3 = 19.00
	   def unitPrice1 = 16.00
	   def unitPrice2 = 17.00
	   def assemblyItemAmount = 20.00
	   def deliveryItemAmount = 21.00
	   def taxPriceAmount = 22.00
	   def priceBeanQty1 = 3
	   def priceBeanQty2 = 4
	   def shipGrpRelQty1 = 1
	   def shipGrpRelQty2 = 2
	   def orderRelationShipCount = 2
	   def productId1 = "prod01"
	   //def productId2 = "prod02"
	   def productId3 = "prod03"
	   //def productId4 = "prod04"
	   def onlineOrderNumber = "order01"
	   def state = "NY"
	   def postalCode = "54321"
	   def couponRepoId1 = "coupon01"
	   def couponRepoId2 = "coupon02"
	   def couponRepoId3 = "coupon03"
	   def couponRepoId6 = "coupon06"
	   def couponRepoId7 = "coupon07"
	   def couponRepoId8 = "coupon08"
	   def couponRepoId9 = "coupon09"
	   def couponRepoId10 = "coupon10"
	   def creditCardType = "VISA"
	   def paymentMethod1 = "onlinePayment"
	   def paymentMethod2 = "netbanking"
	   def assemblyItemId3 = "assemblyItem01"
	   //def assemblyItemId4 = "assemblyItem02"
	   def deliveryItemId3 = "deliveryItem01"
	   def shippingMethod1 = "OneDayShipping"
	   def shipMethodDesc1 = "Delivery in one day"
	   def commIteRefNumber1 = "ciRef01"
	   def ciPersonalization1 = "Logan"
	   def promoDisplayName1 = "50OFF"
	   def promoDisplayName4 = "15OFF"
	   def siteId = "BBB_US"
	   def siteName = "BedBathAndBeyond"
	   def countryName = "US"
	   
	   BBBOrderImpl orderMock = Mock()
	   Profile profileMock = Mock()
	   
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel1 = Mock()
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel2 = Mock()
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel3 = Mock()
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel4 = Mock()
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel5 = Mock()
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel6 = Mock()
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel7 = Mock()
	   
	   BBBCommerceItem commerceItem1 = Mock()
	   CommerceItem commerceItem2 = Mock()
	   BBBCommerceItem commerceItem3 = Mock()
	   //BBBCommerceItem commerceItem4 = Mock()
	   LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
	   LTLAssemblyFeeCommerceItem assemblyFeeCommerceItem1 = Mock()
	   LTLAssemblyFeeCommerceItem assemblyFeeCommerceItem2 = Mock()
	   
	   TaxPriceInfo taxPriceInfo = Mock()
	   ItemPriceInfo itemPriceInfo1 = Mock()
	   //ItemPriceInfo itemPriceInfo2 = null
	   ItemPriceInfo itemPriceInfo3 = Mock()
	  //ItemPriceInfo itemPriceInfo4 = null
	   ItemPriceInfo assemblyItemPriceInfo1 = Mock()
	   ItemPriceInfo deliveryItemPriceInfo1 = Mock()
	   
	   RepositoryItem billingAddressItemMock = Mock()
	   
	   PriceInfoVO orderPriceInfoMock = Mock()
	   
	   BBBRepositoryContactInfo billingAddressMock = Mock()
	   
	   PricingAdjustment itemPricingAdjustment1 = Mock()
	   PricingAdjustment itemPricingAdjustment2 = Mock()
	   
	   RepositoryItem itemPricingModel1 = Mock()
	   RepositoryItem itemPricingModel2 = Mock()
	   RepositoryItem currentPromo1 = Mock()
	   RepositoryItem currentPromo2 = Mock()
	   RepositoryItem currentPromo3 = null
	   RepositoryItem currentPromo4 = Mock()
	   RepositoryItem currentPromo5 = Mock()
	   RepositoryItem currentPromo6 = Mock()
	   RepositoryItem currentPromo7 = Mock()
	   RepositoryItem currentPromo8 = Mock()
	   RepositoryItem currentPromo9 = Mock()
	   RepositoryItem currentPromo10 = Mock()
	   
	   RepositoryItem couponItem1 = Mock()
	   RepositoryItem couponItem2 = Mock()
	   RepositoryItem couponItem3 = Mock()
	   RepositoryItem couponItem6 = Mock()
	   RepositoryItem couponItem7 = Mock()
	   RepositoryItem couponItem8 = Mock()
	   RepositoryItem couponItem9 = Mock()
	   RepositoryItem couponItem10 = Mock()
	   
	   RepositoryItem shippingMethodItem1 = Mock()
	   RepositoryItem [] currentPromos1 = [currentPromo1,currentPromo2]
	   RepositoryItem [] currentPromos2 = [currentPromo3,currentPromo4]
	   RepositoryItem [] currentPromos5 = [currentPromo5]
	   RepositoryItem [] currentPromos6 = [currentPromo6]
	   RepositoryItem [] currentPromos7 = [currentPromo7]
	   RepositoryItem [] currentPromos8 = [currentPromo8]
	   RepositoryItem [] currentPromos9 = [currentPromo9]
	   RepositoryItem [] currentPromos10 = [currentPromo10]
	   RepositoryItemDescriptor itemPromoItemDesc1 = Mock()
	   RepositoryItemDescriptor itemPromoItemDesc6 = Mock()
	   RepositoryItemDescriptor itemPromoItemDesc7 = Mock()
	   RepositoryItemDescriptor itemPromoItemDesc8 = Mock()
	   RepositoryItemDescriptor itemPromoItemDesc9 = Mock()
	   RepositoryItemDescriptor itemPromoItemDesc10 = Mock()
	   
	   AuxiliaryData auxiliaryDataMock1 = Mock()
	   //AuxiliaryData auxiliaryDataMock2 = Mock()
	   AuxiliaryData auxiliaryDataMock3 = Mock()
	   //AuxiliaryData auxiliaryDataMock4 = Mock()
	   
	   BBBHardGoodShippingGroup hardGoodShipGroupMock1 = Mock()
	   BBBStoreShippingGroup storeShippingGroupMock = Mock()
	   ShippingGroupImpl shippingGroup = Mock()
	   
	   PaymentGroup paymentGroup1 = Mock()
	   CreditCard creditCardPaymentGroup = Mock()
	   
	   UnitPriceBean priceBean1 = Mock()
	   UnitPriceBean priceBean2 = Mock()
	   
	   Range range1 = Mock()
	   Range range2 = Mock()
	   
	   List<PricingAdjustment> itemPricingAdjustments = new ArrayList<>()
	   List<ShippingGroupCommerceItemRelationship> shipGrpCommItmRelList = new ArrayList<>()
	   List<ShippingGroup> shippingGroups = new ArrayList<>()
	   List<CommerceItem> commerceItems = new ArrayList<>()
	   List<RepositoryItem>  couponRepositoryItems = new ArrayList<>()
	   List<PaymentGroup> paymentGroups = new ArrayList<>()
	   List<UnitPriceBean> priceBeans = new ArrayList<>()
	   HashMap<String,String> eximMap = new HashMap<>()
	   SiteVO siteVO = new SiteVO()
	   
	   auxiliaryDataMock1.getProductId() >> productId1
	   //auxiliaryDataMock2.getProductId() >> productId2
	   auxiliaryDataMock3.getProductId() >> productId3
	   //auxiliaryDataMock4.getProductId() >> productId4
	   
	  (1.._) * itemPricingModel1.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> "" >> ""
	   
	   itemPricingAdjustments.add(itemPricingAdjustment1)
	   itemPricingAdjustments.add(itemPricingAdjustment2)
	   
	   couponItem1.getRepositoryId() >> couponRepoId1
	   couponItem2.getRepositoryId() >> couponRepoId2
	   couponItem3.getRepositoryId()>> couponRepoId3
	   couponItem6.getRepositoryId()>> couponRepoId6
	   couponItem7.getRepositoryId()>> couponRepoId7
	   couponItem8.getRepositoryId()>> couponRepoId8
	   couponItem9.getRepositoryId()>> couponRepoId9
	   couponItem10.getRepositoryId()>> couponRepoId10
	   
	   couponRepositoryItems.addAll([couponItem1,couponItem2,couponItem3,couponItem6,couponItem7,couponItem8,couponItem9,couponItem10])
	   /*couponRepositoryItems.add(couponItem1)
	   couponRepositoryItems.add(couponItem2)*/
	   
	   (1.._) * itemPriceInfo1.getAdjustments() >> itemPricingAdjustments
	   (1.._) * itemPriceInfo1.getSalePrice() >> salePrice1
	   (1.._) * itemPriceInfo1.getListPrice() >> listPrice1
	   
	   /*itemPriceInfo2.getAdjustments() >> itemPricingAdjustments
	   itemPriceInfo2.getSalePrice() >> salePrice2
	   itemPriceInfo2.getListPrice() >> listPrice2*/
	   
	   itemPriceInfo3.getAdjustments() >> itemPricingAdjustments
	   itemPriceInfo3.getSalePrice() >> salePrice3
	   itemPriceInfo3.getListPrice() >> listPrice3
	   
	   (1.._) * itemPricingAdjustment1.getPricingModel() >> itemPricingModel1
	   (1.._) * itemPricingAdjustment1.getTotalAdjustment() >> totalAdjustment
	   
	   itemPricingAdjustment2.getTotalAdjustment() >> totalAdjustment
	   itemPricingAdjustment2.getPricingModel() >> itemPricingModel2
	   
	   commerceItem1.getPriceInfo() >> itemPriceInfo1
	   commerceItem1.getAuxiliaryData() >> auxiliaryDataMock1
	   (1.._) * commerceItem1.getReferenceNumber() >> commIteRefNumber1
	   (1.._) * commerceItem1.isBuyOffAssociatedItem() >> true
	   (1.._) * commerceItem1.getPersonalizationOptions() >> ciPersonalization1
	   /*commerceItem2.getPriceInfo() >> itemPriceInfo2
	   commerceItem2.getAuxiliaryData() >> auxiliaryDataMock2*/
	   
	   commerceItem3.getPriceInfo() >> itemPriceInfo3
	   commerceItem3.getAuxiliaryData() >> auxiliaryDataMock3
	   (1.._) * commerceItem3.isLtlItem() >> true
	   (1.._) * commerceItem3.getDeliveryItemId() >> deliveryItemId3
	   //Unable to iterate 3 times - third value is not working
	   //(1.._) * commerceItem3.getAssemblyItemId() >> assemblyItemId3 >> assemblyItemId3+"02" >> ""
	   //(1.._) * commerceItem3.getAssemblyItemId() >> assemblyItemId3 >> assemblyItemId3+"02"
	   (1.._) * commerceItem3.getAssemblyItemId() >>> [assemblyItemId3,assemblyItemId3,assemblyItemId3,assemblyItemId3,""]
	   (1.._) * taxPriceInfo.getAmount() >> taxPriceAmount
	   (1.._) * pricingToolsMock.round(taxPriceAmount, 2) >> taxPriceAmount
	   
	   (1.._) * orderMock.getCommerceItem(deliveryItemId3) >> deliveryCommerceItem
	   //(1.._) * orderMock.getCommerceItem(assemblyItemId3) >> assemblyFeeCommerceItem1 >> assemblyFeeCommerceItem1 >> null
	   (1.._) * orderMock.getCommerceItem(assemblyItemId3) >>>[assemblyFeeCommerceItem1,assemblyFeeCommerceItem1,assemblyFeeCommerceItem1,null,assemblyFeeCommerceItem1]
	   (1.._) * orderMock.getTaxPriceInfo() >> taxPriceInfo
	   (1.._) * assemblyFeeCommerceItem1.getPriceInfo() >> assemblyItemPriceInfo1 >> assemblyItemPriceInfo1 >> assemblyItemPriceInfo1
	   (1.._) * assemblyItemPriceInfo1.getAmount() >> assemblyItemAmount >> 0 >> null// null can't be covered as amount is the instance variable '0.00' will be default value
	   (1.._) * deliveryCommerceItem.getPriceInfo() >> deliveryItemPriceInfo1
	   (1.._) * deliveryItemPriceInfo1.getAmount() >> deliveryItemAmount
	   /*commerceItem4.getPriceInfo() >> itemPriceInfo4
	   commerceItem4.getAuxiliaryData() >> auxiliaryDataMock1*/
	   //getProductId()
	   
	   (1.._) * paymentGroup1.getOrderRelationshipCount() >> orderRelationShipCount
	   (1.._) * paymentGroup1.getPaymentMethod() >> paymentMethod1
	   
	   creditCardPaymentGroup.getCreditCardType() >> creditCardType
	   creditCardPaymentGroup.getOrderRelationshipCount() >> orderRelationShipCount
	   creditCardPaymentGroup.getPaymentMethod() >> paymentMethod2
	   
	   priceBean1.getUnitPrice() >> unitPrice1
	   priceBean1.getQuantity() >> priceBeanQty1
	   
	   priceBean2.getUnitPrice() >> unitPrice2
	   priceBean2.getQuantity() >> priceBeanQty2
	   
	   (1.._) * shippingMethodItem1.getPropertyValue("shipMethodDescription") >> shipMethodDesc1
	   
	   (1.._) * hardGoodShipGroupMock1.getShippingMethod() >> shippingMethod1 >> shippingMethod1
	   (1.._) * hardGoodShipGroupMock1.getShippingAddress() >> billingAddressMock
	   
	   /*(1.._) * hardGoodShipGroupMock2.getShippingMethod() >> shippingMethod1
	   (1.._) * hardGoodShipGroupMock2.getShippingAddress() >> null*/
	   
	   (1.._) * catalogToolsMock.getShippingMethod(shippingMethod1) >> shippingMethodItem1
	   //commerceItems.add([commerceItem1,commerceItem2,commerceItem3,commerceItem4])
	   
	   commerceItems.add([commerceItem1,commerceItem2,commerceItem3])
	   
	   shippingGroups.add(hardGoodShipGroupMock1)
	   
	   paymentGroups.addAll([paymentGroup1,creditCardPaymentGroup])
	   
	   /*paymentGroups.add(paymentGroup1)	   
	   paymentGroups.add(creditCardPaymentGroup)*/
	   
	   (1.._) * shipGrpCommItemRel1.getCommerceItem() >> commerceItem1
	   (1.._) * shipGrpCommItemRel1.getRange() >> range1
	   (1.._) * shipGrpCommItemRel1.getQuantity() >> shipGrpRelQty1
	   (1.._) * shipGrpCommItemRel1.getShippingGroup() >> hardGoodShipGroupMock1 >> hardGoodShipGroupMock1
	   
	   shipGrpCommItemRel2.getCommerceItem() >> commerceItem2
	   shipGrpCommItemRel2.getRange() >> range2
	   shipGrpCommItemRel2.getQuantity() >> shipGrpRelQty2
	   
	   shipGrpCommItemRel3.getCommerceItem() >> commerceItem3
	   shipGrpCommItemRel3.getRange() >> range2
	   shipGrpCommItemRel3.getQuantity() >> shipGrpRelQty2
	   shipGrpCommItemRel3.getShippingGroup() >> storeShippingGroupMock
	   
	   shipGrpCommItemRel4.getCommerceItem() >> commerceItem3
	   shipGrpCommItemRel4.getRange() >> range2
	   shipGrpCommItemRel4.getQuantity() >> shipGrpRelQty2
	   shipGrpCommItemRel4.getShippingGroup() >> shippingGroup
	   
	   shipGrpCommItemRel5.getCommerceItem() >> commerceItem3
	   shipGrpCommItemRel5.getRange() >> range2
	   shipGrpCommItemRel5.getQuantity() >> shipGrpRelQty2
	   shipGrpCommItemRel5.getShippingGroup() >> shippingGroup
	   
	   shipGrpCommItemRel6.getCommerceItem() >> commerceItem3
	   shipGrpCommItemRel6.getRange() >> range2
	   shipGrpCommItemRel6.getQuantity() >> shipGrpRelQty2
	   shipGrpCommItemRel6.getShippingGroup() >> shippingGroup
	   
	   shipGrpCommItemRel7.getCommerceItem() >> commerceItem3
	   shipGrpCommItemRel7.getRange() >> range2
	   shipGrpCommItemRel7.getQuantity() >> shipGrpRelQty2
	   shipGrpCommItemRel7.getShippingGroup() >> shippingGroup
	   
	    shipGrpCommItmRelList.addAll([shipGrpCommItemRel1,shipGrpCommItemRel2,shipGrpCommItemRel3,shipGrpCommItemRel4,shipGrpCommItemRel5,shipGrpCommItemRel6,shipGrpCommItemRel7])
		//shipGrpCommItmRelList.addAll([shipGrpCommItemRel1,shipGrpCommItemRel2,shipGrpCommItemRel3])
		priceBeans.addAll([priceBean1,priceBean2])
	   
		(1.._) * billingAddressMock.getState() >> state
		(1.._) * billingAddressMock.getPostalCode() >> postalCode
		
		(1.._) * profileMock.isTransient() >> true
		
	    1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER)  >> orderMock
	    1 * requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
		1 * eximPricingManagerMock.getEximValueMap() >> eximMap
		
		(1.._) * orderPriceInfoMock.getShippingSavings() >> shippingSavings 
		(1.._) * orderPriceInfoMock.getRawShippingTotal() >> rawShippingTotal
		2 * pricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceInfoMock
		(1.._) * pricingToolsMock.round(shippingSavings) >> shippingSavings
		(1.._) * pricingToolsMock.round(shippingSavings,_) >> shippingSavings
		
		itemPromoItemDesc1.getItemDescriptorName() >> BBBCoreConstants.ITEM_DISCOUNT
		itemPromoItemDesc6.getItemDescriptorName() >> BBBCoreConstants.ITEM_DISCOUNT
		itemPromoItemDesc7.getItemDescriptorName() >> BBBCoreConstants.ORDER_DISCOUNT
		itemPromoItemDesc8.getItemDescriptorName() >> "Other discounts"
		
		(1.._) * currentPromo1.getItemDescriptor() >> itemPromoItemDesc1
		(1.._) * currentPromo1.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName1

		/*currentPromo2.getItemDescriptor() >> itemPromoItemDesc2
		currentPromo1.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName4*/
		
		currentPromo5.getItemDescriptor() >> itemPromoItemDesc7
		currentPromo5.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName4
		
		currentPromo6.getItemDescriptor() >> itemPromoItemDesc6
		currentPromo6.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName4
		
		currentPromo7.getItemDescriptor() >> itemPromoItemDesc7
		currentPromo7.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName4
		
		currentPromo8.getItemDescriptor() >> itemPromoItemDesc8
		currentPromo8.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName4
		
		//currentPromo9.getItemDescriptor() >> itemPromoItemDesc9
		currentPromo9.getItemDescriptor() >> {throw new RepositoryException("")}
		//currentPromo10.getItemDescriptor() >> {throw new BBBSystemException("")}
		currentPromo9.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName4
		
		promotionToolsMock.getCouponListFromOrder(orderMock) >> couponRepositoryItems
		
		1 * catalogToolsMock.getPromotions(couponRepoId1) >> currentPromos1
		 catalogToolsMock.getPromotions(couponRepoId2) >> currentPromos2
		 catalogToolsMock.getPromotions(couponRepoId3) >> currentPromos5
		 catalogToolsMock.getPromotions(couponRepoId6) >> currentPromos6
		 catalogToolsMock.getPromotions(couponRepoId7) >> currentPromos7
		 catalogToolsMock.getPromotions(couponRepoId8) >> currentPromos8
		 catalogToolsMock.getPromotions(couponRepoId9) >> currentPromos9
		 (1.._) * catalogToolsMock.getSiteDetailFromSiteId(siteId) >> siteVO
		 
		 siteVO.setSiteName(siteName) 
		 siteVO.setCountryCode(countryName) 
		 
		(1.._) * orderMock.getCommerceItems() >> commerceItems
		(1.._) * orderMock.isExpressCheckOut() >> true // isExpresscheckout() is final method - remove final
		(1.._) * orderMock.getSiteId() >> siteId
		(1.._) * orderMock.getOnlineOrderNumber() >> onlineOrderNumber
		(1.._) * orderMock.getPaymentGroups() >> paymentGroups
		(1.._) * orderMock.getPaymentGroupCount() >> paymentGroups.size()
		(3.._) * orderMock.getBillingAddress() >> billingAddressMock
		(2.._) * orderMock.getShippingGroups() >> shippingGroups
		//(2.._) * orderMock.getShippingGroups() >> [hardGoodShipGroupMock1,hardGoodShipGroupMock1]
	    (2.._) * hardGoodShipGroupMock1.getCommerceItemRelationships() >> shipGrpCommItmRelList
		
		pricingToolsMock.generatePriceBeans(itemPriceInfo1.getCurrentPriceDetailsForRange(range1)) >> priceBeans

		1 * requestMock.setParameter(BBBCoreConstants.OMNITURE_VO, _)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		1 * orderOmnitureDropletSpy.logError("Error occured while fetching coupons in omniture",_)
	 // when :
	   expect :
	   
	   orderOmnitureDropletSpy.service(requestMock, responseMock)
	   
     /* then : 
	   
	   1 * orderOmnitureDropletSpy.logError("BBBOrderOmnitureDroplet.appendProdAndEvents(). Null pointer exception occured while getting price info.")*/
	   //thrown NullPointerException
   }

   /*
    * Negative branches covered : 
    * 
    * #241 - if (paymentGroup.getOrderRelationshipCount() > 0) - orderRelationships - empty
    * #305 - if(currentPromos != null && currentPromos.length >0) - currentPromo is null, empty - branches
    * #507 - if(assemblyFee!=null && assemblyFee > 0) - assemblyFee - null branch can't be covered as default value is 0.00
    * #548 - hgsg.getShippingAddress() != null
    * #703 - if(getPricingTools().round(orderPriceInfo.getShippingSavings())>0) - ShippingSavings is 0
    */

   //TODO - #525 - if(((BBBCommerceItem)commerceItem).isLtlItem() && evar35.length() > 0) - isLtlItem() - false
      
   def "service - Alternative branches in appendProdAndEvents" () {
	   
	   given :
	   
	   def totalAdjustment = 10.00
	   //def shippingSavings = 5.00
	   def shippingSavings = 0.00
	   def rawShippingTotal = 5.00
	   def salePrice1 = 350.00
	   def salePrice2 = 15.00
	   def salePrice3 = 0.00
	   def listPrice1 = 330.00
	   def listPrice2 = 18.00
	   def listPrice3 = 19.00
	   def unitPrice1 = 16.00
	   def unitPrice2 = 17.00
	   def assemblyItemAmount = 20.00
	   def deliveryItemAmount = 21.00
	   def taxPriceAmount = 22.00
	   def priceBeanQty1 = 3
	   def priceBeanQty2 = 4
	   def shipGrpRelQty1 = 1
	   def shipGrpRelQty2 = 2
	   def orderRelationShipCount = 2
	   def productId1 = "prod01"
	   def productId3 = "prod03"
	   def onlineOrderNumber = "order01"
	   def state = "NY"
	   def postalCode = "54321"
	   def couponRepoId1 = "coupon01"
	   def couponRepoId2 = "coupon02"
	   def couponRepoId3 = "coupon03"
	   def couponRepoId6 = "coupon06"
	   def couponRepoId7 = "coupon07"
	   def couponRepoId8 = "coupon08"
	   def couponRepoId9 = "coupon09"
	   def couponRepoId10 = "coupon10"
	   def creditCardType = "VISA"
	   def paymentMethod1 = "onlinePayment"
	   def paymentMethod2 = "netbanking"
	   def assemblyItemId3 = "assemblyItem01"
	   def deliveryItemId3 = "deliveryItem01"
	   def shippingMethod1 = "OneDayShipping"
	   def shipMethodDesc1 = "Delivery in one day"
	   def commIteRefNumber1 = "ciRef01"
	   def ciPersonalization1 = "Logan"
	   def promoDisplayName1 = "50OFF"
	   def promoDisplayName4 = "15OFF"
	   def siteId = "BBB_US"
	   def siteName = "BedBathAndBeyond"
	   def countryName = "US"
	   
	   BBBOrderImpl orderMock = Mock()
	   Profile profileMock = Mock()
	   
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel1 = Mock()
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel2 = Mock()
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel3 = Mock()
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel4 = Mock()
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel5 = Mock()
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel6 = Mock()
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel7 = Mock()
	   
	   BBBCommerceItem commerceItem1 = Mock()
	   CommerceItem commerceItem2 = Mock()
	   BBBCommerceItem commerceItem3 = Mock()
	   //BBBCommerceItem commerceItem4 = Mock()
	   LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
	   LTLAssemblyFeeCommerceItem assemblyFeeCommerceItem1 = Mock()
	   LTLAssemblyFeeCommerceItem assemblyFeeCommerceItem2 = Mock()
	   
	   TaxPriceInfo taxPriceInfo = Mock()
	   ItemPriceInfo itemPriceInfo1 = Mock()
	   //ItemPriceInfo itemPriceInfo2 = null
	   ItemPriceInfo itemPriceInfo3 = Mock()
	  //ItemPriceInfo itemPriceInfo4 = null
	   ItemPriceInfo assemblyItemPriceInfo1 = Mock()
	   ItemPriceInfo deliveryItemPriceInfo1 = Mock()
	   
	   RepositoryItem billingAddressItemMock = Mock()
	   
	   PriceInfoVO orderPriceInfoMock = Mock()
	   
	   BBBRepositoryContactInfo billingAddressMock = Mock()
	   
	   PricingAdjustment itemPricingAdjustment1 = Mock()
	   PricingAdjustment itemPricingAdjustment2 = Mock()
	   
	   RepositoryItem itemPricingModel1 = Mock()
	   RepositoryItem itemPricingModel2 = Mock()
	   RepositoryItem currentPromo1 = Mock()
	   RepositoryItem currentPromo2 = Mock()
	   RepositoryItem currentPromo3 = null
	   RepositoryItem currentPromo4 = Mock()
	   RepositoryItem currentPromo5 = Mock()
	   RepositoryItem currentPromo6 = Mock()
	   RepositoryItem currentPromo7 = Mock()
	   RepositoryItem currentPromo8 = Mock()
	   RepositoryItem currentPromo9 = Mock()
	   RepositoryItem currentPromo10 = Mock()
	   
	   RepositoryItem couponItem1 = Mock()
	   RepositoryItem couponItem2 = Mock()
	   RepositoryItem couponItem3 = Mock()
	   RepositoryItem couponItem6 = Mock()
	   RepositoryItem couponItem7 = Mock()
	   RepositoryItem couponItem8 = Mock()
	   RepositoryItem couponItem9 = Mock()
	   RepositoryItem couponItem10 = Mock()
	   
	   RepositoryItem shippingMethodItem1 = Mock()
	   RepositoryItem [] currentPromos1 = [currentPromo1,currentPromo2]
	   RepositoryItem [] currentPromos2 = [currentPromo3,currentPromo4]
	   RepositoryItem [] currentPromos5 = [currentPromo5]
	   RepositoryItem [] currentPromos6 = [currentPromo6]
	   RepositoryItem [] currentPromos7 = [currentPromo7]
	   RepositoryItem [] currentPromos8 = [currentPromo8]
	   RepositoryItem [] currentPromos9 = [currentPromo9]
	   RepositoryItem [] currentPromos10 = [currentPromo10]
	   RepositoryItemDescriptor itemPromoItemDesc1 = Mock()
	   RepositoryItemDescriptor itemPromoItemDesc6 = Mock()
	   RepositoryItemDescriptor itemPromoItemDesc7 = Mock()
	   RepositoryItemDescriptor itemPromoItemDesc8 = Mock()
	   RepositoryItemDescriptor itemPromoItemDesc9 = Mock()
	   RepositoryItemDescriptor itemPromoItemDesc10 = Mock()
	   
	   AuxiliaryData auxiliaryDataMock1 = Mock()
	   //AuxiliaryData auxiliaryDataMock2 = Mock()
	   AuxiliaryData auxiliaryDataMock3 = Mock()
	   //AuxiliaryData auxiliaryDataMock4 = Mock()
	   
	   BBBHardGoodShippingGroup hardGoodShipGroupMock1 = Mock()
	   BBBHardGoodShippingGroup hardGoodShipGroupMock2 = Mock()
	   BBBStoreShippingGroup storeShippingGroupMock = Mock()
	   ShippingGroupImpl shippingGroup = Mock()
	   
	   PaymentGroup paymentGroup1 = Mock()
	   CreditCard creditCardPaymentGroup = Mock()
	   
	   UnitPriceBean priceBean1 = Mock()
	   UnitPriceBean priceBean2 = Mock()
	   
	   Range range1 = Mock()
	   Range range2 = Mock()
	   
	   List<PricingAdjustment> itemPricingAdjustments = new ArrayList<>()
	   List<ShippingGroupCommerceItemRelationship> shipGrpCommItmRelList = new ArrayList<>()
	   List<ShippingGroup> shippingGroups = new ArrayList<>()
	   List<CommerceItem> commerceItems = new ArrayList<>()
	   List<RepositoryItem>  couponRepositoryItems = new ArrayList<>()
	   List<PaymentGroup> paymentGroups = new ArrayList<>()
	   List<UnitPriceBean> priceBeans = new ArrayList<>()
	   HashMap<String,String> eximMap = new HashMap<>()
	   SiteVO siteVO = new SiteVO()
	   
	   auxiliaryDataMock1.getProductId() >> productId1
	   //auxiliaryDataMock2.getProductId() >> productId2
	   auxiliaryDataMock3.getProductId() >> productId3
	   //auxiliaryDataMock4.getProductId() >> productId4
	   
	  (1.._) * itemPricingModel1.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> "" >> ""
	   
	   itemPricingAdjustments.add(itemPricingAdjustment1)
	   itemPricingAdjustments.add(itemPricingAdjustment2)
	   
	   couponItem1.getRepositoryId() >> couponRepoId1
	   couponItem2.getRepositoryId() >> couponRepoId2
	   couponItem3.getRepositoryId()>> couponRepoId3
	   couponItem6.getRepositoryId()>> couponRepoId6
	   couponItem7.getRepositoryId()>> couponRepoId7
	   couponItem8.getRepositoryId()>> couponRepoId8
	   couponItem9.getRepositoryId()>> couponRepoId9
	   couponItem10.getRepositoryId()>> couponRepoId10
	   
	   couponRepositoryItems.addAll([couponItem1,couponItem2,couponItem3,couponItem6,couponItem7,couponItem8,couponItem9,couponItem10])

	   (1.._) * itemPriceInfo1.getAdjustments() >> itemPricingAdjustments
	   (1.._) * itemPriceInfo1.getSalePrice() >> salePrice1
	   (1.._) * itemPriceInfo1.getListPrice() >> listPrice1
	   
	   itemPriceInfo3.getAdjustments() >> itemPricingAdjustments
	   itemPriceInfo3.getSalePrice() >> salePrice3
	   itemPriceInfo3.getListPrice() >> listPrice3
	   
	   (1.._) * itemPricingAdjustment1.getPricingModel() >> itemPricingModel1
	   (1.._) * itemPricingAdjustment1.getTotalAdjustment() >> totalAdjustment
	   
	   itemPricingAdjustment2.getTotalAdjustment() >> totalAdjustment
	   itemPricingAdjustment2.getPricingModel() >> itemPricingModel2
	   
	   commerceItem1.getPriceInfo() >> itemPriceInfo1
	   commerceItem1.getAuxiliaryData() >> auxiliaryDataMock1
	   //(1.._) * commerceItem1.getReferenceNumber() >> commIteRefNumber1
	   (1.._) * commerceItem1.isBuyOffAssociatedItem() >> true
	   //(1.._) * commerceItem1.getPersonalizationOptions() >> ciPersonalization1
	   /*commerceItem2.getPriceInfo() >> itemPriceInfo2
	   commerceItem2.getAuxiliaryData() >> auxiliaryDataMock2*/
	   
	   commerceItem3.getPriceInfo() >> itemPriceInfo3
	   commerceItem3.getAuxiliaryData() >> auxiliaryDataMock3
	   commerceItem3.isLtlItem() >> false
	   commerceItem3.getDeliveryItemId() >> deliveryItemId3
	   //(1.._) * commerceItem3.getAssemblyItemId() >> assemblyItemId3 >> assemblyItemId3+"02" >> ""
	   //(1.._) * commerceItem3.getAssemblyItemId() >> assemblyItemId3 >> assemblyItemId3+"02"
	   commerceItem3.getAssemblyItemId() >>> [assemblyItemId3,assemblyItemId3,assemblyItemId3,assemblyItemId3,""]
	   taxPriceInfo.getAmount() >> taxPriceAmount
	   pricingToolsMock.round(taxPriceAmount, 2) >> taxPriceAmount
	   
	   orderMock.getCommerceItem(deliveryItemId3) >> deliveryCommerceItem
	   //(1.._) * orderMock.getCommerceItem(assemblyItemId3) >> assemblyFeeCommerceItem1 >> assemblyFeeCommerceItem1 >> null
	   orderMock.getCommerceItem(assemblyItemId3) >>>[assemblyFeeCommerceItem1,assemblyFeeCommerceItem1,assemblyFeeCommerceItem1,null,assemblyFeeCommerceItem1]
	   orderMock.getTaxPriceInfo() >> taxPriceInfo
	   assemblyFeeCommerceItem1.getPriceInfo() >> assemblyItemPriceInfo1 >> assemblyItemPriceInfo1 >> assemblyItemPriceInfo1
	   assemblyItemPriceInfo1.getAmount() >> assemblyItemAmount >> 0 >> null// null can't be covered as amount is the instance variable '0.00' will be default value
	   deliveryCommerceItem.getPriceInfo() >> deliveryItemPriceInfo1
	   deliveryItemPriceInfo1.getAmount() >> deliveryItemAmount
	   /*commerceItem4.getPriceInfo() >> itemPriceInfo4
	   commerceItem4.getAuxiliaryData() >> auxiliaryDataMock1*/
	   //getProductId()
	   
	   paymentGroup1.getOrderRelationshipCount() >> 0
	   paymentGroup1.getPaymentMethod() >> paymentMethod1
	   
	   creditCardPaymentGroup.getCreditCardType() >> creditCardType
	   creditCardPaymentGroup.getOrderRelationshipCount() >> orderRelationShipCount
	   creditCardPaymentGroup.getPaymentMethod() >> paymentMethod2
	   
	   priceBean1.getUnitPrice() >> unitPrice1
	   priceBean1.getQuantity() >> priceBeanQty1
	   
	   priceBean2.getUnitPrice() >> unitPrice2
	   priceBean2.getQuantity() >> priceBeanQty2
	   
	   (1.._) * shippingMethodItem1.getPropertyValue("shipMethodDescription") >> shipMethodDesc1
	   
	   //(1.._) * hardGoodShipGroupMock1.getShippingMethod() >> shippingMethod1 >> shippingMethod1
	   //(1.._) * hardGoodShipGroupMock1.getShippingAddress() >> billingAddressMock
	   
	   (1.._) * hardGoodShipGroupMock2.getShippingMethod() >> shippingMethod1  >> shippingMethod1 >> ""
	   (1.._) * hardGoodShipGroupMock2.getShippingAddress() >> billingAddressMock >> billingAddressMock >> billingAddressMock  >> null
	   
	   (1.._) * catalogToolsMock.getShippingMethod(shippingMethod1) >> shippingMethodItem1
	   catalogToolsMock.getShippingMethod("") >> shippingMethodItem1
	   //commerceItems.add([commerceItem1,commerceItem2,commerceItem3,commerceItem4])
	   
	   commerceItems.add([commerceItem1,commerceItem2,commerceItem3])
	   
	   shippingGroups.addAll([hardGoodShipGroupMock1,hardGoodShipGroupMock2])
	   
	   paymentGroups.addAll([paymentGroup1,creditCardPaymentGroup])
	   
	   /*paymentGroups.add(paymentGroup1)
	   paymentGroups.add(creditCardPaymentGroup)*/
	   
	   (1.._) * shipGrpCommItemRel1.getCommerceItem() >> commerceItem1
	   (1.._) * shipGrpCommItemRel1.getRange() >> range1
	   (1.._) * shipGrpCommItemRel1.getQuantity() >> shipGrpRelQty1
	   (1.._) * shipGrpCommItemRel1.getShippingGroup() >> hardGoodShipGroupMock1 >> hardGoodShipGroupMock2
	   
	   shipGrpCommItemRel2.getCommerceItem() >> commerceItem2
	   shipGrpCommItemRel2.getRange() >> range2
	   shipGrpCommItemRel2.getQuantity() >> shipGrpRelQty2
	   
	   shipGrpCommItemRel3.getCommerceItem() >> commerceItem3
	   shipGrpCommItemRel3.getRange() >> range2
	   shipGrpCommItemRel3.getQuantity() >> shipGrpRelQty2
	   shipGrpCommItemRel3.getShippingGroup() >> storeShippingGroupMock >> hardGoodShipGroupMock2 
	   
	   shipGrpCommItemRel4.getCommerceItem() >> commerceItem3
	   shipGrpCommItemRel4.getRange() >> range2
	   shipGrpCommItemRel4.getQuantity() >> shipGrpRelQty2
	   shipGrpCommItemRel4.getShippingGroup() >> shippingGroup
	   
	   shipGrpCommItemRel5.getCommerceItem() >> commerceItem3
	   shipGrpCommItemRel5.getRange() >> range2
	   shipGrpCommItemRel5.getQuantity() >> shipGrpRelQty2
	   shipGrpCommItemRel5.getShippingGroup() >> shippingGroup
	   
	   shipGrpCommItemRel6.getCommerceItem() >> commerceItem3
	   shipGrpCommItemRel6.getRange() >> range2
	   shipGrpCommItemRel6.getQuantity() >> shipGrpRelQty2
	   shipGrpCommItemRel6.getShippingGroup() >> shippingGroup
	   
	   shipGrpCommItemRel7.getCommerceItem() >> commerceItem3
	   shipGrpCommItemRel7.getRange() >> range2
	   shipGrpCommItemRel7.getQuantity() >> shipGrpRelQty2
	   shipGrpCommItemRel7.getShippingGroup() >> shippingGroup
	   
		shipGrpCommItmRelList.addAll([shipGrpCommItemRel1,shipGrpCommItemRel2,shipGrpCommItemRel3,shipGrpCommItemRel4,shipGrpCommItemRel5,shipGrpCommItemRel6,shipGrpCommItemRel7])
		//shipGrpCommItmRelList.addAll([shipGrpCommItemRel1,shipGrpCommItemRel2,shipGrpCommItemRel3])
		priceBeans.addAll([priceBean1,priceBean2])
	   
		(1.._) * billingAddressMock.getState() >> state
		(1.._) * billingAddressMock.getPostalCode() >> postalCode
		
		(1.._) * profileMock.isTransient() >> true
		
		1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER)  >> orderMock
		1 * requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
		1 * eximPricingManagerMock.getEximValueMap() >> eximMap
		
		(1.._) * orderPriceInfoMock.getShippingSavings() >> shippingSavings
		(1.._) * orderPriceInfoMock.getRawShippingTotal() >> rawShippingTotal
		2 * pricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceInfoMock
		pricingToolsMock.round(shippingSavings) >> shippingSavings
		pricingToolsMock.round(shippingSavings,_) >> shippingSavings
		
		itemPromoItemDesc1.getItemDescriptorName() >> BBBCoreConstants.ITEM_DISCOUNT
		itemPromoItemDesc6.getItemDescriptorName() >> BBBCoreConstants.ITEM_DISCOUNT
		itemPromoItemDesc7.getItemDescriptorName() >> BBBCoreConstants.ORDER_DISCOUNT
		itemPromoItemDesc8.getItemDescriptorName() >> "Other discounts"
		
		/*(1.._) * currentPromo1.getItemDescriptor() >> itemPromoItemDesc1
		(1.._) * currentPromo1.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName1*/

		/*currentPromo2.getItemDescriptor() >> itemPromoItemDesc2
		currentPromo1.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName4*/
		
		currentPromo5.getItemDescriptor() >> itemPromoItemDesc7
		currentPromo5.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName4
		
		currentPromo6.getItemDescriptor() >> itemPromoItemDesc6
		currentPromo6.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName4
		
		currentPromo7.getItemDescriptor() >> itemPromoItemDesc7
		currentPromo7.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName4
		
		currentPromo8.getItemDescriptor() >> itemPromoItemDesc8
		currentPromo8.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName4
		
		//currentPromo9.getItemDescriptor() >> itemPromoItemDesc9
		currentPromo9.getItemDescriptor() >> {throw new RepositoryException("")}
		//currentPromo10.getItemDescriptor() >> {throw new BBBSystemException("")}
		currentPromo9.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName4
		
		promotionToolsMock.getCouponListFromOrder(orderMock) >> couponRepositoryItems
		
		1 * catalogToolsMock.getPromotions(couponRepoId1) >> []
		 catalogToolsMock.getPromotions(couponRepoId2) >> null
		 catalogToolsMock.getPromotions(couponRepoId3) >> currentPromos5
		 catalogToolsMock.getPromotions(couponRepoId6) >> currentPromos6
		 catalogToolsMock.getPromotions(couponRepoId7) >> currentPromos7
		 catalogToolsMock.getPromotions(couponRepoId8) >> currentPromos8
		 catalogToolsMock.getPromotions(couponRepoId9) >> currentPromos9
		 (1.._) * catalogToolsMock.getSiteDetailFromSiteId(siteId) >> siteVO
		 
		 siteVO.setSiteName(siteName)
		 siteVO.setCountryCode(countryName)
		 
		(1.._) * orderMock.getCommerceItems() >> commerceItems
		(1.._) * orderMock.isExpressCheckOut() >> true // isExpresscheckout() is final method - remove final
		(1.._) * orderMock.getSiteId() >> siteId
		(1.._) * orderMock.getOnlineOrderNumber() >> onlineOrderNumber
		(1.._) * orderMock.getPaymentGroups() >> paymentGroups
		(1.._) * orderMock.getPaymentGroupCount() >> paymentGroups.size()
		(3.._) * orderMock.getBillingAddress() >> billingAddressMock
		(2.._) * orderMock.getShippingGroups() >> shippingGroups
		//(2.._) * orderMock.getShippingGroups() >> [hardGoodShipGroupMock1,hardGoodShipGroupMock1]
		(2.._) * hardGoodShipGroupMock1.getCommerceItemRelationships() >> shipGrpCommItmRelList
		
		(2.._) * hardGoodShipGroupMock2.getCommerceItemRelationships() >> shipGrpCommItmRelList
		pricingToolsMock.generatePriceBeans(itemPriceInfo1.getCurrentPriceDetailsForRange(range1)) >> priceBeans

		1 * requestMock.setParameter(BBBCoreConstants.OMNITURE_VO, _)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
		1 * orderOmnitureDropletSpy.logError("Error occured while fetching coupons in omniture",_)
	 // when :
	   expect :
	   
	   orderOmnitureDropletSpy.service(requestMock, responseMock)
	   
	 /* then :
	   
	   1 * orderOmnitureDropletSpy.logError("BBBOrderOmnitureDroplet.appendProdAndEvents(). Null pointer exception occured while getting price info.")*/
	   //thrown NullPointerException
   }
   
   
   /*
    * Alternate branches covered : 
    * 
    * #552 - StringUtils.isNotBlank(hgsg.getShippingMethod())
    * #578 - null !=bbbCommItem.getReferenceNumber()  - is empty
    * #558 - StringUtils.isNotBlank(shippingMethodDescription) - shipMethodDesc is blank
    * #649 - itemPriceAdj.getPricingModel() != null - pricing Model is null
    * #708 - else if(getPricingTools().round(orderPriceInfo.getShippingSavings())>0) - savings is zero    
    */
   
   //TODO - (5.16 pm) 24.11.2016 -
   //TODO - #677 - StringUtils.isNotBlank(promos) && null != orderPriceInfo - orderPriceInfo - null // Scenario not possible as 
   //- code uses variable without null ptr chk. Exception occurs @ #708
   //TODO - #703 -  if(getPricingTools().round(orderPriceInfo.getShippingSavings())>0) - 
   
   def "service - appendProdAndEvents -> Shipping method is invalid/empty in shippingGroup " () {
	   
	   given :
	   
	   def totalAdjustment = 10.00
	   def shippingSavings = 5.00
	   //def shippingSavings = 0
	   def rawShippingTotal = 5.00
	   def salePrice1 = 350.00
	   def salePrice2 = 15.00
	   def salePrice3 = 0.00
	   def listPrice1 = 330.00
	   def listPrice2 = 18.00
	   def listPrice3 = 19.00
	   def unitPrice1 = 16.00
	   def unitPrice2 = 17.00
	   def assemblyItemAmount = 20.00
	   def deliveryItemAmount = 21.00
	   def taxPriceAmount = 22.00
	   def priceBeanQty1 = 3
	   def priceBeanQty2 = 4
	   def shipGrpRelQty1 = 1
	   def orderRelationShipCount = 2
	   def productId1 = "prod01"
	   def onlineOrderNumber = "order01"
	   def state = "NY"
	   def postalCode = "54321"
	   def couponRepoId1 = "coupon01"
	   def creditCardType = "VISA"
	   def paymentMethod1 = "onlinePayment"
	   def assemblyItemId3 = "assemblyItem01"
	   def deliveryItemId3 = "deliveryItem01"
	   def shippingMethod1 = "OneDayShipping"
	   def commIteRefNumber1 = "ciRef01"
	   //def ciPersonalization1 = "Logan"
	   def promoDisplayName1 = "50OFF"
	   def siteId = "BBB_US"
	   def siteName = "BedBathAndBeyond"
	   def countryName = "US"
	   def shipMethodDesc1 = "Delivery in one day"
	   
	   BBBOrderImpl orderMock = Mock()
	   Profile profileMock = Mock()
	   
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel1 = Mock()
	   
	   BBBCommerceItem commerceItem1 = Mock()
	   //BBBCommerceItem commerceItem4 = Mock()
	   LTLDeliveryChargeCommerceItem deliveryCommerceItem = Mock()
	   
	   TaxPriceInfo taxPriceInfo = Mock()
	   ItemPriceInfo itemPriceInfo1 = Mock()
	   ItemPriceInfo assemblyItemPriceInfo1 = Mock()
	   ItemPriceInfo deliveryItemPriceInfo1 = Mock()
	   
	   LTLAssemblyFeeCommerceItem assemblyFeeCommerceItem1 = Mock()
	   RepositoryItem billingAddressItemMock = Mock()
	   
	   PriceInfoVO orderPriceInfoMock = Mock()
	   
	   BBBRepositoryContactInfo billingAddressMock = Mock()
	   
	   PricingAdjustment itemPricingAdjustment1 = Mock()
	   
	   RepositoryItem itemPricingModel1 = Mock()
	   
	   RepositoryItem currentPromo1 = Mock()
	   
	   RepositoryItem couponItem1 = Mock()
	   
	   RepositoryItem shippingMethodItem1 = Mock()
	   RepositoryItem [] currentPromos1 = [currentPromo1]
	   
	   RepositoryItemDescriptor itemPromoItemDesc1 = Mock()
	   
	   AuxiliaryData auxiliaryDataMock1 = Mock()
	   
	   BBBHardGoodShippingGroup hardGoodShipGroupMock1 = Mock()
	   BBBHardGoodShippingGroup hardGoodShipGroupMock2 = Mock()
	   
	   BBBStoreShippingGroup storeShippingGroupMock = Mock()
	   ShippingGroupImpl shippingGroup = Mock()
	   
	   PaymentGroup paymentGroup1 = Mock()
	   CreditCard creditCardPaymentGroup = Mock()
	   
	   UnitPriceBean priceBean1 = Mock()
	   
	   Range range1 = Mock()
	   
	   List<PricingAdjustment> itemPricingAdjustments = new ArrayList<>()
	   List<ShippingGroupCommerceItemRelationship> shipGrpCommItmRelList = new ArrayList<>()
	   List<ShippingGroup> shippingGroups = new ArrayList<>()
	   List<CommerceItem> commerceItems = new ArrayList<>()
	   List<RepositoryItem>  couponRepositoryItems = new ArrayList<>()
	   List<PaymentGroup> paymentGroups = new ArrayList<>()
	   List<UnitPriceBean> priceBeans = new ArrayList<>()
	   HashMap<String,String> eximMap = new HashMap<>()
	   SiteVO siteVO = new SiteVO()
	   
	   auxiliaryDataMock1.getProductId() >> productId1
	   
	  (1.._) * itemPricingModel1.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> "" >> ""
	   
	   itemPricingAdjustments.add(itemPricingAdjustment1)
	   
	   couponItem1.getRepositoryId() >> couponRepoId1
	   
	   couponRepositoryItems.addAll([couponItem1])
	   
	   //(1.._) * itemPriceInfo1.getAdjustments() >> itemPricingAdjustments >> itemPricingAdjustments
	   (1.._) * itemPriceInfo1.getAdjustments() >> itemPricingAdjustments >> itemPricingAdjustments  >> []
	   (1.._) * itemPriceInfo1.getSalePrice() >> salePrice1 >> salePrice1
	   (1.._) * itemPriceInfo1.getListPrice() >> listPrice1 >> listPrice1
	   
	   (1.._) * itemPricingAdjustment1.getPricingModel() >> itemPricingModel1   >> itemPricingModel1  >> itemPricingModel1 >> itemPricingModel1 >> null
	   (1.._) * itemPricingAdjustment1.getTotalAdjustment() >> totalAdjustment
	   
	   commerceItem1.getPriceInfo() >> itemPriceInfo1
	   commerceItem1.getAuxiliaryData() >> auxiliaryDataMock1
	   (1.._) * commerceItem1.getReferenceNumber() >> ""
	   (1.._) * commerceItem1.isBuyOffAssociatedItem() >> true
	   (1.._) * taxPriceInfo.getAmount() >> taxPriceAmount
	   (1.._) * pricingToolsMock.round(taxPriceAmount, 2) >> taxPriceAmount
	   
	   (1.._) * orderMock.getTaxPriceInfo() >> taxPriceInfo
	   
	   (1.._) * paymentGroup1.getOrderRelationshipCount() >> orderRelationShipCount
	   (1.._) * paymentGroup1.getPaymentMethod() >> paymentMethod1
	   
	   creditCardPaymentGroup.getCreditCardType() >> creditCardType
	   creditCardPaymentGroup.getOrderRelationshipCount() >> orderRelationShipCount
	   creditCardPaymentGroup.getPaymentMethod() >> paymentMethod1
	   
	   priceBean1.getUnitPrice() >> unitPrice1
	   priceBean1.getQuantity() >> priceBeanQty1

	   hardGoodShipGroupMock1.getShippingMethod() >> ""
	   hardGoodShipGroupMock1.getShippingAddress() >> billingAddressMock >> billingAddressMock
	   
	   (1.._) * hardGoodShipGroupMock2.getShippingMethod()  >> shippingMethod1 >> shippingMethod1 >> ""
	   (1.._) * hardGoodShipGroupMock2.getShippingAddress() >> billingAddressMock >> billingAddressMock
	   
	   catalogToolsMock.getShippingMethod(shippingMethod1) >> shippingMethodItem1
	   
	   commerceItems.add([commerceItem1])
	   
	   shippingGroups.addAll([hardGoodShipGroupMock1])
	   
	   paymentGroups.addAll([paymentGroup1])
	   
	   (1.._) * shipGrpCommItemRel1.getCommerceItem() >> commerceItem1
	   (1.._) * shipGrpCommItemRel1.getRange() >> range1
	   (1.._) * shipGrpCommItemRel1.getQuantity() >> shipGrpRelQty1
	   (1.._) * shipGrpCommItemRel1.getShippingGroup() >> hardGoodShipGroupMock1 >> hardGoodShipGroupMock2
	   
		shipGrpCommItmRelList.addAll([shipGrpCommItemRel1])
		priceBeans.addAll([priceBean1])
	   
		(1.._) * billingAddressMock.getState() >> state
		(1.._) * billingAddressMock.getPostalCode() >> postalCode
		
		(1.._) * profileMock.isTransient() >> true
		
		1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER)  >> orderMock
		1 * requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
		1 * eximPricingManagerMock.getEximValueMap() >> eximMap
		
		(1.._) * orderPriceInfoMock.getShippingSavings() >> shippingSavings
		(1.._) * orderPriceInfoMock.getRawShippingTotal() >> rawShippingTotal
		2 * pricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceInfoMock
		pricingToolsMock.round(shippingSavings) >> shippingSavings
		pricingToolsMock.round(shippingSavings,_) >> shippingSavings

		itemPromoItemDesc1.getItemDescriptorName() >> BBBCoreConstants.ITEM_DISCOUNT
		
		(1.._) * currentPromo1.getItemDescriptor() >> itemPromoItemDesc1
		(1.._) * currentPromo1.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName1

		promotionToolsMock.getCouponListFromOrder(orderMock) >> couponRepositoryItems
		
		1 * catalogToolsMock.getPromotions(couponRepoId1) >> currentPromos1
		(1.._) * catalogToolsMock.getSiteDetailFromSiteId(siteId) >> siteVO
		 
		 siteVO.setSiteName(siteName)
		 siteVO.setCountryCode(countryName)
		 
		(1.._) * orderMock.getCommerceItems() >> commerceItems
		(1.._) * orderMock.isExpressCheckOut() >> true // isExpresscheckout() is final method - remove final
		(1.._) * orderMock.getSiteId() >> siteId
		(1.._) * orderMock.getOnlineOrderNumber() >> onlineOrderNumber
		(1.._) * orderMock.getPaymentGroups() >> paymentGroups
		(1.._) * orderMock.getPaymentGroupCount() >> paymentGroups.size()
		(3.._) * orderMock.getBillingAddress() >> billingAddressMock
		(2.._) * orderMock.getShippingGroups() >>> [[hardGoodShipGroupMock1,hardGoodShipGroupMock2]]
		
		(2.._) * hardGoodShipGroupMock1.getCommerceItemRelationships() >>> [[shipGrpCommItemRel1]]
		hardGoodShipGroupMock2.getCommerceItemRelationships() >>> [[shipGrpCommItemRel1]]
		
		pricingToolsMock.generatePriceBeans(itemPriceInfo1.getCurrentPriceDetailsForRange(range1)) >> priceBeans

		1 * requestMock.setParameter(BBBCoreConstants.OMNITURE_VO, _)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
	   expect :
	   
	   orderOmnitureDropletSpy.service(requestMock, responseMock)
	   
   }
   
   /*
    * Alternate branches :
    *  
    * #525 - if(((BBBCommerceItem)commerceItem).isLtlItem() && evar35.length() > 0) - evar35 is empty 
    *  
    */
   
   def "service - No pricing adjustments" () {
	   
	   given :
	   
	   def totalAdjustment = 10.00
	   def shippingSavings = 5.00
	   //def shippingSavings = 0
	   def rawShippingTotal = 5.00
	   def salePrice1 = 350.00
	   def salePrice2 = 15.00
	   def salePrice3 = 0.00
	   def listPrice1 = 330.00
	   def listPrice2 = 18.00
	   def listPrice3 = 19.00
	   def unitPrice1 = 16.00
	   def unitPrice2 = 17.00
	   def assemblyItemAmount = 20.00
	   def deliveryItemAmount = 21.00
	   def taxPriceAmount = 22.00
	   def priceBeanQty1 = 3
	   def priceBeanQty2 = 4
	   def shipGrpRelQty1 = 1
	   def orderRelationShipCount = 2
	   def productId1 = "prod01"
	   def onlineOrderNumber = "order01"
	   def state = "NY"
	   def postalCode = "54321"
	   def couponRepoId1 = "coupon01"
	   def couponRepoId2 = "coupon02"
	   def creditCardType = "VISA"
	   def paymentMethod1 = "onlinePayment"
	   def assemblyItemId3 = "assemblyItem01"
	   def deliveryItemId3 = "deliveryItem01"
	   def shippingMethod1 = "OneDayShipping"
	   def commIteRefNumber1 = "ciRef01"
	   //def ciPersonalization1 = "Logan"
	   def promoDisplayName1 = "50OFF"
	   def promoDisplayName2 = "60OFF"
	   def siteId = "BBB_US"
	   def siteName = "BedBathAndBeyond"
	   def countryName = "US"
	   def shipMethodDesc1 = "Delivery in one day"
	   
	   BBBOrderImpl orderMock = Mock()
	   Profile profileMock = Mock()
	   
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel1 = Mock()
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel2 = Mock()
	   BBBCommerceItem commerceItem1 = Mock()
	   BBBCommerceItem commerceItem2 = Mock()
	   //BBBCommerceItem commerceItem4 = Mock()
	   LTLDeliveryChargeCommerceItem deliveryCommerceItem1 = Mock()
	   LTLDeliveryChargeCommerceItem deliveryCommerceItem2 = Mock()
	   
	   TaxPriceInfo taxPriceInfo = Mock()
	   ItemPriceInfo itemPriceInfo1 = Mock()
	   ItemPriceInfo assemblyItemPriceInfo1 = Mock()
	   ItemPriceInfo deliveryItemPriceInfo1 = Mock()
	   
	   LTLAssemblyFeeCommerceItem assemblyFeeCommerceItem1 = Mock()
	   RepositoryItem billingAddressItemMock = Mock()
	   
	   PriceInfoVO orderPriceInfoMock = Mock()
	   
	   BBBRepositoryContactInfo billingAddressMock = Mock()
	   
	   PricingAdjustment itemPricingAdjustment1 = Mock()
	   
	   RepositoryItem itemPricingModel1 = Mock()
	   
	   RepositoryItem currentPromo1 = Mock()
	   RepositoryItem currentPromo2 = Mock()
	   
	   RepositoryItem couponItem1 = Mock()
	   RepositoryItem couponItem2 = Mock()
	   
	   RepositoryItem shippingMethodItem1 = Mock()
	   RepositoryItem [] currentPromos1 = [currentPromo1]
	   RepositoryItem [] currentPromos2 = [currentPromo2]
	   RepositoryItemDescriptor itemPromoItemDesc1 = Mock()
	   RepositoryItemDescriptor itemPromoItemDesc2 = Mock()
	   
	   AuxiliaryData auxiliaryDataMock1 = Mock()
	   
	   BBBHardGoodShippingGroup hardGoodShipGroupMock1 = Mock()
	   BBBHardGoodShippingGroup hardGoodShipGroupMock2 = Mock()
	   
	   BBBStoreShippingGroup storeShippingGroupMock = Mock()
	   ShippingGroupImpl shippingGroup = Mock()
	   
	   PaymentGroup paymentGroup1 = Mock()
	   CreditCard creditCardPaymentGroup = Mock()
	   
	   UnitPriceBean priceBean1 = Mock()
	   
	   Range range1 = Mock()
	   
	   List<PricingAdjustment> itemPricingAdjustments = new ArrayList<>()
	   List<ShippingGroupCommerceItemRelationship> shipGrpCommItmRelList = new ArrayList<>()
	   List<ShippingGroup> shippingGroups = new ArrayList<>()
	   List<CommerceItem> commerceItems = new ArrayList<>()
	   List<RepositoryItem>  couponRepositoryItems = new ArrayList<>()
	   List<PaymentGroup> paymentGroups = new ArrayList<>()
	   List<UnitPriceBean> priceBeans = new ArrayList<>()
	   HashMap<String,String> eximMap = new HashMap<>()
	   SiteVO siteVO = new SiteVO()
	   
	   auxiliaryDataMock1.getProductId() >> productId1
	   
	  itemPricingModel1.getPropertyValue(BBBCoreConstants.DISPLAY_NAME) >> "" >> ""
	   
	   itemPricingAdjustments.add(itemPricingAdjustment1)
	   
	   couponItem1.getRepositoryId() >> couponRepoId1
	   couponItem2.getRepositoryId() >> couponRepoId2
	   
	   couponRepositoryItems.addAll([couponItem1,couponItem2])
	   
	   //(1.._) * itemPriceInfo1.getAdjustments() >> []
	   (1.._) * itemPriceInfo1.getAdjustments() >> itemPricingAdjustments >> itemPricingAdjustments
	   (1.._) * itemPriceInfo1.getSalePrice() >> salePrice1 >> salePrice1
	   (1.._) * itemPriceInfo1.getListPrice() >> listPrice1 >> listPrice1
	   
	   (1.._) * itemPricingAdjustment1.getPricingModel() >> itemPricingModel1   >> itemPricingModel1  >> itemPricingModel1 >> itemPricingModel1 >> null
	   (1.._) * itemPricingAdjustment1.getTotalAdjustment() >> totalAdjustment
	   def deliveryItemId1 = "delItem01"
	   commerceItem1.getPriceInfo() >> itemPriceInfo1
	   commerceItem1.getAuxiliaryData() >> auxiliaryDataMock1
	   (1.._) * commerceItem1.getReferenceNumber() >> ""
	   (1.._) * commerceItem1.isBuyOffAssociatedItem() >> true
	   commerceItem1.getDeliveryItemId() >>deliveryItemId1
	   commerceItem1.isLtlItem() >> true
	   
	   commerceItem2.getPriceInfo() >> itemPriceInfo1
	   commerceItem2.getAuxiliaryData() >> auxiliaryDataMock1
	   commerceItem2.getReferenceNumber() >> ""
	   commerceItem2.isBuyOffAssociatedItem() >> true
	   commerceItem2.isLtlItem() >> true
	   
	   itemPriceInfo1.getAmount() >> 15.00
	   
	   deliveryCommerceItem2.getPriceInfo() >> itemPriceInfo1
	   
	   (1.._) * taxPriceInfo.getAmount() >> taxPriceAmount
	   (1.._) * pricingToolsMock.round(taxPriceAmount, 2) >> taxPriceAmount
	   
	   (1.._) * orderMock.getTaxPriceInfo() >> taxPriceInfo
	   
	   (1.._) * paymentGroup1.getOrderRelationshipCount() >> orderRelationShipCount
	   (1.._) * paymentGroup1.getPaymentMethod() >> paymentMethod1
	   
	   creditCardPaymentGroup.getCreditCardType() >> creditCardType
	   creditCardPaymentGroup.getOrderRelationshipCount() >> orderRelationShipCount
	   creditCardPaymentGroup.getPaymentMethod() >> paymentMethod1
	   
	   priceBean1.getUnitPrice() >> unitPrice1
	   priceBean1.getQuantity() >> priceBeanQty1

	   hardGoodShipGroupMock1.getShippingMethod() >> ""
	   hardGoodShipGroupMock1.getShippingAddress() >> billingAddressMock >> billingAddressMock
	   
	   (1.._) * hardGoodShipGroupMock2.getShippingMethod()  >> shippingMethod1 >> shippingMethod1 >> ""
	   (1.._) * hardGoodShipGroupMock2.getShippingAddress() >> billingAddressMock >> billingAddressMock
	   
	   catalogToolsMock.getShippingMethod(shippingMethod1) >> shippingMethodItem1
	   
	   commerceItems.add([commerceItem1,commerceItem2])
	   
	   shippingGroups.addAll([hardGoodShipGroupMock1])
	   
	   paymentGroups.addAll([paymentGroup1])
	   
	   (1.._) * shipGrpCommItemRel1.getCommerceItem() >> commerceItem1
	   (1.._) * shipGrpCommItemRel1.getRange() >> range1
	   (1.._) * shipGrpCommItemRel1.getQuantity() >> shipGrpRelQty1
	   (1.._) * shipGrpCommItemRel1.getShippingGroup() >> hardGoodShipGroupMock1 >> hardGoodShipGroupMock2
	   
	   shipGrpCommItemRel2.getCommerceItem() >> deliveryCommerceItem2
	   shipGrpCommItemRel2.getRange() >> range1
	   shipGrpCommItemRel2.getQuantity() >> shipGrpRelQty1
	   shipGrpCommItemRel2.getShippingGroup() >> hardGoodShipGroupMock1 >> hardGoodShipGroupMock2
	   
	   
		shipGrpCommItmRelList.addAll([shipGrpCommItemRel1,shipGrpCommItemRel2])
		priceBeans.addAll([priceBean1])
	   
		(1.._) * billingAddressMock.getState() >> state
		(1.._) * billingAddressMock.getPostalCode() >> postalCode
		
		(1.._) * profileMock.isTransient() >> true
		
		1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER)  >> orderMock
		1 * requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
		1 * eximPricingManagerMock.getEximValueMap() >> eximMap
		
		(1.._) * orderPriceInfoMock.getShippingSavings() >> shippingSavings
		(1.._) * orderPriceInfoMock.getRawShippingTotal() >> rawShippingTotal
		2 * pricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceInfoMock
		pricingToolsMock.round(shippingSavings) >> shippingSavings
		pricingToolsMock.round(shippingSavings,_) >> shippingSavings

		itemPromoItemDesc1.getItemDescriptorName() >> BBBCoreConstants.ITEM_DISCOUNT
		itemPromoItemDesc2.getItemDescriptorName() >> BBBCoreConstants.ORDER_DISCOUNT
		
		(1.._) * currentPromo1.getItemDescriptor() >> itemPromoItemDesc1
		(1.._) * currentPromo1.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName1

		(1.._) * currentPromo2.getItemDescriptor() >> itemPromoItemDesc2
		(1.._) * currentPromo2.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME) >> promoDisplayName2
		
		promotionToolsMock.getCouponListFromOrder(orderMock) >> couponRepositoryItems
		
		1 * catalogToolsMock.getPromotions(couponRepoId1) >> currentPromos1
		1 * catalogToolsMock.getPromotions(couponRepoId2) >> currentPromos2
		(1.._) * catalogToolsMock.getSiteDetailFromSiteId(siteId) >> siteVO
		

		 siteVO.setSiteName(siteName)
		 siteVO.setCountryCode(countryName)
		 
		(1.._) * orderMock.getCommerceItems() >> commerceItems
		(1.._) * orderMock.isExpressCheckOut() >> true // isExpresscheckout() is final method - remove final
		(1.._) * orderMock.getSiteId() >> siteId
		(1.._) * orderMock.getOnlineOrderNumber() >> onlineOrderNumber
		(1.._) * orderMock.getPaymentGroups() >> paymentGroups
		(1.._) * orderMock.getPaymentGroupCount() >> paymentGroups.size()
		(3.._) * orderMock.getBillingAddress() >> billingAddressMock
		(2.._) * orderMock.getShippingGroups() >>> [[hardGoodShipGroupMock1,hardGoodShipGroupMock2]]
		orderMock.getCommerceItem(deliveryItemId1) >> deliveryCommerceItem2
		(2.._) * hardGoodShipGroupMock1.getCommerceItemRelationships() >>> [[shipGrpCommItemRel1,shipGrpCommItemRel2]]
		hardGoodShipGroupMock2.getCommerceItemRelationships() >>> [[shipGrpCommItemRel1]]
		
		pricingToolsMock.generatePriceBeans(itemPriceInfo1.getCurrentPriceDetailsForRange(range1)) >> priceBeans

		1 * requestMock.setParameter(BBBCoreConstants.OMNITURE_VO, _)
		1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
		
	   expect :
	   
	   orderOmnitureDropletSpy.service(requestMock, responseMock)
	   
   }
   
   /*
	* Negative(alternate) branches covered :
	*
	* appendProdEvents() -
	* #446 - cisiRel.getCommerceItem().getPriceInfo() != null - ItemPriceInfo is null
	*
	*/
   
  def "service - Price details in commerce item is invalid/corrupted (null)" () {
	  
	  given :
	  
	  def productId = "prod04"
	  BBBOrderImpl orderMock = Mock()
	  Profile profileMock = Mock()
	  ShippingGroupCommerceItemRelationship shipGrpCommItemRel = Mock()
	  BBBCommerceItem commerceItem = Mock()
	  ItemPriceInfo itemPriceInfo = null
	  
	  AuxiliaryData auxiliaryDataMock = Mock()
	  BBBHardGoodShippingGroup hardGoodShipGroupMock = Mock()
	  
	  List<ShippingGroupCommerceItemRelationship> shipGrpCommItmRelList = new ArrayList<>()
	  List<ShippingGroup> shippingGroups = new ArrayList<>()
	  List<CommerceItem> commerceItems = new ArrayList<>()
	  HashMap<String,String> eximMap = new HashMap<>()
	  
	  auxiliaryDataMock.getProductId() >> productId
	  
	  commerceItem.getPriceInfo() >> itemPriceInfo
	  commerceItem.getAuxiliaryData() >> auxiliaryDataMock
	  
	  commerceItems.add([commerceItem])
	  shippingGroups.add(hardGoodShipGroupMock)
	  
	  shipGrpCommItemRel.getCommerceItem() >> commerceItem
	  
	   shipGrpCommItmRelList.addAll([shipGrpCommItemRel])
	   
	   
	   1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER)  >> orderMock
	   1 * requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
	   1 * eximPricingManagerMock.getEximValueMap() >> eximMap
	   1 * orderMock.getCommerceItems() >> commerceItems
	   (1.._) * orderMock.getShippingGroups() >> shippingGroups
	   (1.._) * hardGoodShipGroupMock.getCommerceItemRelationships() >> shipGrpCommItmRelList
	   
	 when :
	  
	  orderOmnitureDropletSpy.service(requestMock, responseMock)
	  
	 then :
	  
	  1 * orderOmnitureDropletSpy.logError("BBBOrderOmnitureDroplet.appendProdAndEvents(). Null pointer exception occured while getting price info.")
	  thrown NullPointerException
  }
   
  /*
   * Negative(alternate) branches covered :
   *
   *
   * appendProdEvents() -
   *
   * #562 - BBBBusinessException covered - getCatalogTools().getShippingMethod(hgsg.getShippingMethod())
   * #193 - RepositoryException - triggered from - (#304) getCatalogTools().getPromotions(repositoryItem.getRepositoryId())
   * 
   */
  def "service - Unable to retrieve shipping method (BBBBusinessException)" () {
	  
	  given :
	  
	  def productId = "prod04"
	  //def couponRepoId1 = "coupon01"
	  PaymentGroup paymentGroup1 = Mock()
	  //List<PaymentGroup> paymentGroups = new ArrayList<>()
	  
	  BBBOrderImpl orderMock = Mock()
	  Profile profileMock = Mock()
	  ShippingGroupCommerceItemRelationship shipGrpCommItemRel = Mock()
	  BBBCommerceItem commerceItem = Mock()
	  ItemPriceInfo itemPriceInfo = Mock()
	  //RepositoryItem couponItem1 = Mock()
	  BBBRepositoryContactInfo billingAddressMock = Mock()
	  
	  AuxiliaryData auxiliaryDataMock = Mock()
	  HardgoodShippingGroup hardGoodShipGroupMock = Mock()
	  PriceInfoVO orderPriceInfoMock = Mock()
	  //List<RepositoryItem>  couponRepositoryItems = new ArrayList<>()
	  List<ShippingGroupCommerceItemRelationship> shipGrpCommItmRelList = new ArrayList<>()
	  List<ShippingGroup> shippingGroups = new ArrayList<>()
	  List<CommerceItem> commerceItems = new ArrayList<>()
	  HashMap<String,String> eximMap = new HashMap<>()
	  
	  auxiliaryDataMock.getProductId() >> productId
	  	  
	  (1.._) * orderPriceInfoMock.getShippingSavings() >> 10.00
	  (1.._) * orderPriceInfoMock.getRawShippingTotal() >> 30.00
	  
	  (1.._) * billingAddressMock.getState() >> "NY"
	  (1.._) * billingAddressMock.getPostalCode() >> "12345"
	  
	  (1.._) * pricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceInfoMock
	  promotionToolsMock.getCouponListFromOrder(orderMock) >> {throw new RepositoryException("")}
	  
	  (1.._) * catalogToolsMock.getShippingMethod(_) >> {throw new BBBBusinessException("")}
	  //1 * catalogToolsMock.getPromotions(couponRepoId1) >> {throw new RepositoryException("")}
	  itemPriceInfo.getAdjustments() >> new ArrayList<>()
	  
	  commerceItem.getPriceInfo() >> itemPriceInfo
	  commerceItem.getAuxiliaryData() >> auxiliaryDataMock
	  
	  commerceItems.add([commerceItem])
	  shippingGroups.add(hardGoodShipGroupMock)
	  
	  shipGrpCommItemRel.getCommerceItem() >> commerceItem
	  shipGrpCommItemRel.getShippingGroup() >> hardGoodShipGroupMock
	  shipGrpCommItmRelList.addAll([shipGrpCommItemRel])
	
	   //(1.._) * paymentGroup1.getOrderRelationshipCount() >> 2
	   //(1.._) * paymentGroup1.getPaymentMethod() >> "online"
	   
	  // paymentGroups.addAll([paymentGroup1])

	   	
	   1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER)  >> orderMock
	   1 * requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
	   1 * eximPricingManagerMock.getEximValueMap() >> eximMap
	   1 * orderMock.getCommerceItems() >> commerceItems
	   //(1.._) * orderMock.getPaymentGroups() >> paymentGroups
	   (1.._) * orderMock.getShippingGroups() >> shippingGroups
	   (1.._) * hardGoodShipGroupMock.getCommerceItemRelationships() >> shipGrpCommItmRelList
	   (1.._) * hardGoodShipGroupMock.getShippingAddress() >> billingAddressMock
	   (1.._) * hardGoodShipGroupMock.getShippingMethod() >> "OneDay"
	   pricingToolsMock.generatePriceBeans(_) >> new ArrayList<>()
	   
	 when :
	  
	  orderOmnitureDropletSpy.service(requestMock, responseMock)
	  
	 then :

	  1 * orderOmnitureDropletSpy.logError('Business Error while re trieving shipping method for [OneDay]', _)
	  1 * orderOmnitureDropletSpy.logError("RepositoryException occourred from Repository API call")
  }
   
  def "service - Unable to retrieve shipping method (BBBBusinessException) | loggingError disabled" () {
	  
	  given :
	  
	  def productId = "prod04"
	  //def couponRepoId1 = "coupon01"
	  PaymentGroup paymentGroup1 = Mock()
	  //List<PaymentGroup> paymentGroups = new ArrayList<>()
	  
	  BBBOrderImpl orderMock = Mock()
	  Profile profileMock = Mock()
	  ShippingGroupCommerceItemRelationship shipGrpCommItemRel = Mock()
	  BBBCommerceItem commerceItem = Mock()
	  ItemPriceInfo itemPriceInfo = Mock()
	  //RepositoryItem couponItem1 = Mock()
	  BBBRepositoryContactInfo billingAddressMock = Mock()
	  
	  AuxiliaryData auxiliaryDataMock = Mock()
	  HardgoodShippingGroup hardGoodShipGroupMock = Mock()
	  PriceInfoVO orderPriceInfoMock = Mock()
	  //List<RepositoryItem>  couponRepositoryItems = new ArrayList<>()
	  List<ShippingGroupCommerceItemRelationship> shipGrpCommItmRelList = new ArrayList<>()
	  List<ShippingGroup> shippingGroups = new ArrayList<>()
	  List<CommerceItem> commerceItems = new ArrayList<>()
	  HashMap<String,String> eximMap = new HashMap<>()
	  
	  orderOmnitureDropletSpy.setLoggingError(false)
	  
	  auxiliaryDataMock.getProductId() >> productId
			
	  (1.._) * orderPriceInfoMock.getShippingSavings() >> 10.00
	  (1.._) * orderPriceInfoMock.getRawShippingTotal() >> 30.00
	  
	  (1.._) * billingAddressMock.getState() >> "NY"
	  (1.._) * billingAddressMock.getPostalCode() >> "12345"
	  
	  (1.._) * pricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceInfoMock
	  promotionToolsMock.getCouponListFromOrder(orderMock) >> {throw new RepositoryException("")}
	  
	  (1.._) * catalogToolsMock.getShippingMethod(_) >> {throw new BBBBusinessException("")}
	  //1 * catalogToolsMock.getPromotions(couponRepoId1) >> {throw new RepositoryException("")}
	  itemPriceInfo.getAdjustments() >> new ArrayList<>()
	  
	  commerceItem.getPriceInfo() >> itemPriceInfo
	  commerceItem.getAuxiliaryData() >> auxiliaryDataMock
	  
	  commerceItems.add([commerceItem])
	  shippingGroups.add(hardGoodShipGroupMock)
	  
	  shipGrpCommItemRel.getCommerceItem() >> commerceItem
	  shipGrpCommItemRel.getShippingGroup() >> hardGoodShipGroupMock
	  shipGrpCommItmRelList.addAll([shipGrpCommItemRel])
	
	   //(1.._) * paymentGroup1.getOrderRelationshipCount() >> 2
	   //(1.._) * paymentGroup1.getPaymentMethod() >> "online"
	   
	  // paymentGroups.addAll([paymentGroup1])

		   
	   1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER)  >> orderMock
	   1 * requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
	   1 * eximPricingManagerMock.getEximValueMap() >> eximMap
	   1 * orderMock.getCommerceItems() >> commerceItems
	   //(1.._) * orderMock.getPaymentGroups() >> paymentGroups
	   (1.._) * orderMock.getShippingGroups() >> shippingGroups
	   (1.._) * hardGoodShipGroupMock.getCommerceItemRelationships() >> shipGrpCommItmRelList
	   (1.._) * hardGoodShipGroupMock.getShippingAddress() >> billingAddressMock
	   (1.._) * hardGoodShipGroupMock.getShippingMethod() >> "OneDay"
	   pricingToolsMock.generatePriceBeans(_) >> new ArrayList<>()
	   
	 when :
	  
	  orderOmnitureDropletSpy.service(requestMock, responseMock)
	  
	 then :

	  //0 * orderOmnitureDropletSpy.logError('Business Error while re trieving shipping method for [OneDay]', _)
	  0 * orderOmnitureDropletSpy.logError("RepositoryException occourred from Repository API call")
  }

  /*
   * Negative(alternate) branches covered :
   *
   *
   * appendProdEvents() -
   *
   * #562 - BBBBusinessException covered - getCatalogTools().getShippingMethod(hgsg.getShippingMethod())
   * 
   * #127 - CommerceItemNotFoundException - ((BBBOrder) order).getCommerceItems()
   *
   */
  
  def "service - Unable to retrieve commerce items (CommerceItemNotFoundException)" () {
	  
	  given :
	  
	   BBBOrderImpl orderMock = Mock()
	   Profile profileMock = Mock()
	   1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER)  >> orderMock
	   1 * orderMock.getCommerceItems() >> {throw new CommerceItemNotFoundException("")}

	  when :
	  
	  orderOmnitureDropletSpy.service(requestMock, responseMock)
	  
	 then :
  	
	  1 * orderOmnitureDropletSpy.logError("CommerceItemNotFoundException occourred from Repository API call")
	  1 * requestMock.setParameter(BBBCoreConstants.ERROR,"Error occourred in finding commerecitem details");
      1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_OPARAM, requestMock, responseMock)
  }
  
  def "service - Unable to retrieve commerce items (CommerceItemNotFoundException) | logError disabled" () {
	  
	  given :
	  
	   BBBOrderImpl orderMock = Mock()
	   Profile profileMock = Mock()
	   orderOmnitureDropletSpy.setLoggingError(false)
	   1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER)  >> orderMock
	   1 * orderMock.getCommerceItems() >> {throw new CommerceItemNotFoundException("")}

	  when :
	  
	  orderOmnitureDropletSpy.service(requestMock, responseMock)
	  
	 then :
	  
	  0 * orderOmnitureDropletSpy.logError("CommerceItemNotFoundException occourred from Repository API call")
	  1 * requestMock.setParameter(BBBCoreConstants.ERROR,"Error occourred in finding commerecitem details");
	  1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_OPARAM, requestMock, responseMock)
  }
  
   def "service - Exception while getting commerce items - InvalidParameterException" () {
	  
	  given :
	  
	   BBBOrderImpl orderMock = Mock()
	   Profile profileMock = Mock()
	   1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER)  >> orderMock
	   1 * orderMock.getCommerceItems() >> {throw new InvalidParameterException("")}

	  when :
	  
	  orderOmnitureDropletSpy.service(requestMock, responseMock)
	  
	 then :
	  
	  1 * orderOmnitureDropletSpy.logError("InvalidParameterException occourred from Repository API call")
	  1 * requestMock.setParameter(BBBCoreConstants.ERROR,"Error occourred in finding commerecitem details");
	  1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_OPARAM, requestMock, responseMock)
   }
   
   def "service - Exception while getting commerce items - InvalidParameterException | logError disabled" () {
	   
	   given :
	   
		BBBOrderImpl orderMock = Mock()
		Profile profileMock = Mock()
		orderOmnitureDropletSpy.setLoggingError(false)
		1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER)  >> orderMock
		1 * orderMock.getCommerceItems() >> {throw new InvalidParameterException("")}
 
	   when :
	   
	   orderOmnitureDropletSpy.service(requestMock, responseMock)
	   
	  then :
	   
	   0 * orderOmnitureDropletSpy.logError("InvalidParameterException occourred from Repository API call")
	   1 * requestMock.setParameter(BBBCoreConstants.ERROR,"Error occourred in finding commerecitem details");
	   1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_OPARAM, requestMock, responseMock)
	}

   def "service - Exception while getting commerce items - BBBSystemException | logError disabled" () {
	  
	  given :
	  
	   BBBOrderImpl orderMock = Mock()
	   Profile profileMock = Mock()
	   orderOmnitureDropletSpy.setLoggingError(false)
	   1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER)  >> orderMock
	   1 * orderMock.getCommerceItems() >> {throw new BBBSystemException("")}

	  when :
	  
	  orderOmnitureDropletSpy.service(requestMock, responseMock)
	  
	 then :
	  
	  0 * orderOmnitureDropletSpy.logError("BBBBusinessException occourred from Catalog API call")
	  1 * requestMock.setParameter(BBBCoreConstants.ERROR,"Error occourred in finding order details")
	  1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_OPARAM, requestMock, responseMock)
	  1 * requestMock.setParameter(BBBCoreConstants.OMNITURE_VO, _);
	  1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock);
  }
  
  def "service - Exception while getting commerce items - BBBSystemException" () {
	  
	  given :
	  
	   BBBOrderImpl orderMock = Mock()
	   Profile profileMock = Mock()
	   1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER)  >> orderMock
	   1 * orderMock.getCommerceItems() >> {throw new BBBSystemException("")}

	  when :
	  
	  orderOmnitureDropletSpy.service(requestMock, responseMock)
	  
	 then :
	  
	  1 * orderOmnitureDropletSpy.logError("BBBBusinessException occourred from Catalog API call")
	  1 * requestMock.setParameter(BBBCoreConstants.ERROR,"Error occourred in finding order details")
	  1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_OPARAM, requestMock, responseMock)
	  1 * requestMock.setParameter(BBBCoreConstants.OMNITURE_VO, _);
	  1 * requestMock.serviceParameter(BBBCoreConstants.OPARAM, requestMock, responseMock);
  }
  
  /*
   * Alternate (negative) branches covered : 
   * #564 - BBBSystemException - triggered by appendProdAndEvents() - #555 - getCatalogTools().getShippingMethod(hgsg.getShippingMethod())
   * #337 - BBBSystemException - triggered by - createPromoStr() #303 - getCatalogTools().getPromotions(repositoryItem.getRepositoryId())
   */
  
  //TODO - to cover the following branch - unable to cover.
  //#193 - RepositoryException - triggered from - (#304) getCatalogTools().getPromotions(repositoryItem.getRepositoryId())
  //@Ignore
  def "service - Exception while retrieving coupons" () {
	  
	  given :
	  
	   def productId1 = "prod01"
	   def shippingMethod =  "OneDay"
	   
	   BBBOrderImpl orderMock = Mock()
	   Profile profileMock = Mock()
	   HardgoodShippingGroup hardGoodShipGroupMock = Mock()
	   ShippingGroupCommerceItemRelationship shipGrpCommItemRel = Mock()
	   BBBCommerceItem commerceItem = Mock()
	   ItemPriceInfo itemPriceInfo = Mock()
	   PriceInfoVO orderPriceInfoMock = Mock()
	   AuxiliaryData auxiliaryDataMock = Mock()
	   RepositoryItem shippingMethodItem = Mock()
	   Address shippingAddressMock = Mock() 
	   
	   List<ShippingGroup> shippingGroups = new ArrayList<>()
	   List<ShippingGroupCommerceItemRelationship> shipGrpCommItmRelList = new ArrayList<>()
	   //List<RepositoryItem>  couponRepositoryItem = new ArrayList<>()
	   
	   itemPriceInfo.getAdjustments() >> new ArrayList<>()
	   auxiliaryDataMock.getProductId() >> productId1
	   
	   commerceItem.getPriceInfo() >> itemPriceInfo
	   commerceItem.getAuxiliaryData() >> auxiliaryDataMock
	   
	   shipGrpCommItemRel.getCommerceItem() >> commerceItem
	   shipGrpCommItemRel.getShippingGroup() >> hardGoodShipGroupMock
	   hardGoodShipGroupMock.getShippingMethod() >> shippingMethod
	   hardGoodShipGroupMock.getShippingAddress() >> shippingAddressMock
	   catalogToolsMock.getShippingMethod(shippingMethod) >> {throw new BBBSystemException("")}
	   
	  // shippingMethodItem.getPropertyValue("shipMethodDescription") >> {throw new RepositoryException("")}
	   
	   hardGoodShipGroupMock.getCommerceItemRelationships() >> shipGrpCommItmRelList
	   
	   shipGrpCommItmRelList.addAll([shipGrpCommItemRel])
	   
	   shippingGroups.addAll([hardGoodShipGroupMock])
	   //couponRepositoryItem
	   
	   List<RepositoryItem>  couponRepositoryItems = new ArrayList<>()
	   RepositoryItem couponItem1 = Mock()
	   //RepositoryItem currentPromo1 = Mock()
	   //RepositoryItem [] currentPromos1 = [currentPromo1]
	   
	   couponItem1.getRepositoryId() >> "coupon01"
	   couponRepositoryItems.addAll([couponItem1])
	   
	   promotionToolsMock.getCouponListFromOrder(orderMock) >> couponRepositoryItems
	   
	   (1.._) * catalogToolsMock.getPromotions("coupon01") >> {throw new BBBSystemException("")}
	   
	   (1.._) * requestMock.getObjectParameter(BBBCoreConstants.ORDER)  >> orderMock
	   (1.._) * requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
	   pricingToolsMock.generatePriceBeans(_) >> []
	   (1.._) * pricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceInfoMock
	   (1.._) * orderMock.getShippingGroups() >> shippingGroups
	   (1.._) * orderMock.getPaymentGroups() >> []
	   (1.._) * orderMock.getCommerceItems() >> []
	   
	   profileMock.isTransient() >> false

	  when :
	  
	  orderOmnitureDropletSpy.service(requestMock, responseMock)
	  
	 then :
	  //true
	  //1 * orderOmnitureDropletSpy.logError("System Error while retrieving shipping method for [");
	  1 * orderOmnitureDropletSpy.logError("System Error while retrieving shipping method for [OneDay]", _)
	  1 * orderOmnitureDropletSpy.logError("BBBSystem Exception occured while fetching coupons in omniture",_)
	  /*1 * orderOmnitureDropletSpy.logError("CommerceItemNotFoundException occourred from Repository API call")
	  1 * requestMock.setParameter(BBBCoreConstants.ERROR,"Error occourred in finding commerecitem details");
	  1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_OPARAM, requestMock, responseMock)*/
  }
  
  
  /*
   * Alternate (negative branches covered : 
   * 
   * BBBBusinessException - triggered by - (#239) - (BBBOrder) order).getPaymentGroups() - in updateOmnitureVO()
   * BBBBusinessException - #564 - appendProdAndEvents() - triggered by - getCatalogTools().getShippingMethod(hgsg.getShippingMethod())
   * BBBBusinessException - #303 - triggered by createPromoStr() - getCatalogTools().getPromotions()
   * #472 - itemPriceAdj.getPricingModel() != null
   * #787 - shippingGroup instanceof BBBStoreShippingGroup - both branches covered (StoreShippingGroup & non-store shipppingGroup)
   * #729 - null != ((TBSCommerceItem) commerceItem).getTBSItemInfo() - TBSItemInfo - null
   * #730 -  ((TBSCommerceItem) commerceItem).getTBSItemInfo().isPriceOveride() - priceOverride - false
   * #735 - pricingAdjustment.getAdjustmentDescription().equalsIgnoreCase(TBSConstants.PRICE_OVERRIDE_DESC) - both branches covered
   * #741 - (commerceItem instanceof LTLAssemblyFeeCommerceItem && null != ((LTLAssemblyFeeCommerceItem) commerceItem).getTBSItemInfo()  - both branches covered 
   * #742 - ((LTLAssemblyFeeCommerceItem) commerceItem).getTBSItemInfo().isPriceOveride()) - both branches covered
   * #753 - (commerceItem instanceof LTLDeliveryChargeCommerceItem && null != ((LTLDeliveryChargeCommerceItem) commerceItem).getTBSItemInfo() && 
   * ((LTLDeliveryChargeCommerceItem) commerceItem).getTBSItemInfo().isPriceOveride()) - all branches
   * #766 - (commerceItem instanceof GiftWrapCommerceItem && (null != ((GiftWrapCommerceItem) commerceItem).getTBSItemInfo()) &&
   * 		((GiftWrapCommerceItem) commerceItem).getTBSItemInfo().isPriceOveride()) - all branches					
   * #747 - pricingAdjustment.getAdjustmentDescription().equalsIgnoreCase(TBSConstants.PRICE_OVERRIDE_DESC)				
   * #734 - for (PricingAdjustment pricingAdjustment : adjustments) - (TBSCommerceItem) adjustments is empty
   * #746 - for (PricingAdjustment pricingAdjustment : adjustments) - (LTLAssemblyFeeCommerceItem)adjustments is empty
   * #758 - for (PricingAdjustment pricingAdjustment : adjustments) - (LTLDeliveryChargeCommerceItem) adjustments is empty
   * #770 - for (PricingAdjustment pricingAdjustment : adjustments) - (GiftWrapCommerceItem) adjustments is empty
   * #784 - if (shippingPriceInfo != null) - shippingPriceInfo is null
   * #790 & #800 #812 shipInfo != null && shipInfo.isShipPriceOverride() - tbsShipInfo - null
   * #795 & 817 - pricingAdjustment.getPricingModel() == null  - ShippingPricing model is not null
   * 
   */
  
  //TODO -  
  // #312 - currentPromo.getItemDescriptor() - triggers - BBBSystemException
  //TODO - 5.18 PM (25.11.2016) -   #805 - if(taxPriceInfo != null) // can't be done as it's causing null ptr exception
     
     def "service -  retrieveing Omniture details for TBS site"() {
		 
		 given :
		 
		 def couponRepoId1 = "coupon01"
		 
		 BBBOrderImpl orderMock = Mock()
		 Profile profileMock = Mock() 
		 PriceInfoVO orderPriceInfoVO = new PriceInfoVO()
		 BBBCommerceItem commerceItem1 = Mock()
		 
		 TBSCommerceItem tbsCommerceItem1 = Mock()
		 TBSCommerceItem tbsCommerceItem2 = Mock()
		 TBSCommerceItem tbsCommerceItem3 = Mock()
		 TBSCommerceItem tbsCommerceItem4 = Mock()
		 
		 LTLAssemblyFeeCommerceItem ltlAssemblyFeeCommItem1 = Mock()
		 LTLAssemblyFeeCommerceItem ltlAssemblyFeeCommItem2 = Mock()
		 LTLAssemblyFeeCommerceItem ltlAssemblyFeeCommItem3 = Mock()
		 LTLAssemblyFeeCommerceItem ltlAssemblyFeeCommItem4 = Mock()
		 
		 LTLDeliveryChargeCommerceItem ltlDeliveryChargeCommItem1 = Mock()
		 LTLDeliveryChargeCommerceItem ltlDeliveryChargeCommItem2 = Mock()
		 LTLDeliveryChargeCommerceItem ltlDeliveryChargeCommItem3 = Mock()
		 LTLDeliveryChargeCommerceItem ltlDeliveryChargeCommItem4 = Mock()
		 
		 GiftWrapCommerceItem giftWrapCommerceItem1 = Mock()
		 GiftWrapCommerceItem giftWrapCommerceItem2 = Mock()
		 GiftWrapCommerceItem giftWrapCommerceItem3 = Mock()
		 GiftWrapCommerceItem giftWrapCommerceItem4 = Mock()
		 
		 TBSItemInfo  tbsItemInfo1 = Mock()
		 TBSItemInfo  ltlAssmblyItemInfo1 = Mock()
		 TBSItemInfo ltlDeliveryItemInfo1 = Mock()
		 TBSItemInfo giftWrapItemInfo1 = Mock()
		 
		 Address shippingAddressMock = Mock()
		 BBBHardGoodShippingGroup shippingGroupMock1 = Mock()
		 BBBStoreShippingGroup shippingGroupMock2 = Mock()
		 BBBStoreShippingGroup shippingGroupMock3 = Mock()
		 ShippingGroup shippingGroupMock4 = Mock()
		 ShippingGroup shippingGroupMock5 = Mock()
		 
		 RepositoryItem shippingMethodItem1 = Mock()
		 
		 List<ShippingGroup> shippingGroups = new ArrayList<>()
		 
		 ItemPriceInfo tbsPriceInfo1 = Mock()
		 ItemPriceInfo ltlAssemblyItemPriceInfo1 = Mock() 
		 ItemPriceInfo ltlDeliveryItemPriceInfo1 = Mock()
		 ItemPriceInfo giftWrapItemPriceInfo1 = Mock()
		 
		 //ShippingPriceInfo shipPriceInfo1 = Mock()
		 ShippingGroupCommerceItemRelationship shipGrpCommItemRel = Mock()
		 
		 PricingAdjustment pricingAdjustment1 = Mock()
		 PricingAdjustment pricingAdjustment2 = Mock()
		 PricingAdjustment pricingAdjustment3 = Mock()
		 
		 PricingAdjustment shippingPriceAdjustment1 = Mock()
		 PricingAdjustment shippingPriceAdjustment2 = Mock()
		 PricingAdjustment shippingPriceAdjustment3 = Mock()
		 
		 RepositoryItem shippingPricingModel3 = Mock()
		 
		 ItemPriceInfo itemPriceInfo1 = Mock()
		 
		 AuxiliaryData auxiliaryDataMock1 = Mock()
		 
		 RepositoryItem couponItem1 = Mock()
		 
		 List<PricingAdjustment> shippingPriceAdjustments = new ArrayList<>()
		 List<PricingAdjustment> itemPricingAdjustments = new ArrayList<>()
		 List<CommerceItem> commerceItems = new ArrayList<>()
		 List<ShippingGroupCommerceItemRelationship> shipGrpCommItmRelList = new ArrayList<>()
		 List<RepositoryItem>  couponRepositoryItems = new ArrayList<>()
		 
		 SiteVO siteVO = new SiteVO()
		 
		 def siteID = "BBB_TBS"
		 def shipGroupId = "SG01"
		 
		 TBSShippingInfo tbsShipInfoMock1 = Mock()
		 TBSShippingInfo tbsShipInfoMock2 = Mock()
		 TBSShippingInfo tbsShipInfoMock3 = null
		 
		 BBBShippingPriceInfo shippingPriceInfo1 = Mock()
		 BBBShippingPriceInfo shippingPriceInfo2 = Mock()
		 BBBShippingPriceInfo shippingPriceInfo3 = Mock()
		 
		 //pricingAdjustment1.getAdjustmentDescription() >> "" >> TBSConstants.PRICE_OVERRIDE_DESC >> "" >> TBSConstants.PRICE_OVERRIDE_DESC >> "" >> TBSConstants.PRICE_OVERRIDE_DESC  >> "" >> TBSConstants.PRICE_OVERRIDE_DESC >> "Test string"
		 pricingAdjustment1.getAdjustmentDescription() >> "" >> TBSConstants.PRICE_OVERRIDE_DESC >> "" >> TBSConstants.PRICE_OVERRIDE_DESC >> "" >> TBSConstants.PRICE_OVERRIDE_DESC >> "" >> TBSConstants.PRICE_OVERRIDE_DESC >> 
		 pricingAdjustment2.getAdjustmentDescription() >> ""  >> TBSConstants.TBS_GIFT_WRAP_ADJUSTMENT
		 pricingAdjustment3.getPricingModel() >> null
		 
		 orderPriceInfoVO.setShippingSavings(15.00)
		 orderPriceInfoVO.setTotalShippingAmount(30.00)
		 
		 tbsShipInfoMock1.isTaxOverride() >> true
		 tbsShipInfoMock1.getTaxReason() >> "Tax for shipping expensive products"
		 tbsShipInfoMock1.isSurchargeOverride() >> true
		 tbsShipInfoMock1.getSurchargeReason() >> "Glass objects handled"
		 
		 /*tbsShipInfoMock3.isTaxOverride() >> null
		 tbsShipInfoMock3.getTaxReason() >> "Tax for shipping expensive products"
		 tbsShipInfoMock3.isSurchargeOverride() >> true
		 tbsShipInfoMock3.getSurchargeReason() >> "Glass objects handled"*/
		 
		 
		 1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER) >> orderMock
		 1 * requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
		 //shippingGroupMock1.getCommerceItemRelationships() >> []
		 
		 (1.._) * shippingPriceInfo1.getAdjustments() >> shippingPriceAdjustments
		 
		 (1.._) * shippingGroupMock1.getPriceInfo() >> shippingPriceInfo1
		 (1.._) * shippingGroupMock1.getTbsShipInfo() >> tbsShipInfoMock1
		 (1.._) * shippingGroupMock1.getId() >> shipGroupId
		 (1.._) * shippingGroupMock1.getShippingAddress() >> shippingAddressMock
		 (1.._) * shippingGroupMock1.getShippingMethod() >> "OneDay"
		 
		 shippingGroupMock2.getPriceInfo() >> shippingPriceInfo2
		 shippingGroupMock2.getTbsShipInfo() >> tbsShipInfoMock2
		 shippingGroupMock2.getId() >> shipGroupId
		 //(1.._) * shippingGroupMock2.getShippingAddress() >> shippingAddressMock
		 shippingGroupMock2.getShippingMethod() >> "OneDay"
		 shippingGroupMock2.getCommerceItemRelationships() >> []
		 
		 shippingGroupMock3.getPriceInfo() >> shippingPriceInfo3
		 shippingGroupMock3.getTbsShipInfo() >> tbsShipInfoMock3
		 shippingGroupMock3.getId() >> shipGroupId
		 shippingGroupMock3.getShippingMethod() >> "OneDay"
		 shippingGroupMock3.getCommerceItemRelationships() >> []
		 
		 shippingGroupMock4.getPriceInfo() >> shippingPriceInfo3
		 shippingGroupMock4.getId() >> shipGroupId
		 shippingGroupMock4.getShippingMethod() >> "OneDay"
		 shippingGroupMock4.getCommerceItemRelationships() >> []
		 
		 shippingGroupMock5.getPriceInfo() >> null
		 shippingGroupMock5.getId() >> shipGroupId
		 shippingGroupMock5.getShippingMethod() >> "OneDay"
		 shippingGroupMock5.getCommerceItemRelationships() >> []
		 
		 shippingGroups.addAll([shippingGroupMock1,shippingGroupMock2,shippingGroupMock3,shippingGroupMock4,shippingGroupMock5])
		 
		 (1.._) * pricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceInfoVO
		 
		 (1.._) * pricingToolsMock.round(_, _) >> 15.00
		 
		 itemPricingAdjustments.addAll([pricingAdjustment1,pricingAdjustment1,pricingAdjustment2, pricingAdjustment3])
		 
		 (1.._) * tbsPriceInfo1.getAdjustments() >> itemPricingAdjustments >> []
		 ltlAssemblyItemPriceInfo1.getAdjustments() >> itemPricingAdjustments >> []
		 ltlDeliveryItemPriceInfo1.getAdjustments() >> itemPricingAdjustments >> []
		 giftWrapItemPriceInfo1.getAdjustments() >> itemPricingAdjustments >> []
		 
		 (1.._) * tbsItemInfo1.isPriceOveride() >> true >> false >> true 
		 (1.._) * tbsItemInfo1.getOverideReason() >> "SKU is different for TBS"
		 
		 ltlAssmblyItemInfo1.isPriceOveride() >> true >> false >> true
		 ltlAssmblyItemInfo1.getOverideReason() >> "SKU is different for TBS"
		 
		 ltlDeliveryItemInfo1.isPriceOveride() >> true >> false >> true
		 ltlDeliveryItemInfo1.getOverideReason() >> "SKU is different for TBS"
		 
		 giftWrapItemInfo1.isPriceOveride() >> true >> false >> true  
		 giftWrapItemInfo1.getOverideReason() >> "SKU is different for TBS"
		 
		 tbsShipInfoMock1.isShipPriceOverride() >> true
		 tbsShipInfoMock1.getShipPriceReason() >> "SKU item is different"
		 
		 /*1 * shippingGroupMock1.getPriceInfo() >> shippingPriceInfo
		 1 * shippingGroupMock1.getTbsShipInfo() >> tbsShipInfoMock1
		 1 * shippingGroupMock1.getId() >> shipGroupId
		 1 * shippingGroupMock1.getShippingAddress() >> shippingAddressMock
		 1 * shippingGroupMock1.getShippingMethod() >> "OneDay"*/
		 catalogToolsMock.getShippingMethod("OneDay") >> {throw new BBBBusinessException("")}
		 shippingPriceAdjustment1.getAdjustmentDescription() >> "Shipping Override"
		 shippingPriceAdjustment2.getAdjustmentDescription() >> "Surcharge Override"
		 shippingPriceAdjustment3.getPricingModel() >> shippingPricingModel3
		 shippingPriceAdjustments.addAll([shippingPriceAdjustment1,shippingPriceAdjustment2,shippingPriceAdjustment3])
		 
		 /*shippingPriceInfo1.getAdjustments() >> shippingPriceAdjustments*/
		 
		 (1.._) * tbsCommerceItem1.getTBSItemInfo() >> tbsItemInfo1
		 (1.._) * tbsCommerceItem1.getPriceInfo() >> tbsPriceInfo1
		 
		 		  tbsCommerceItem2.getTBSItemInfo() >> null
				  tbsCommerceItem2.getPriceInfo() >> tbsPriceInfo1
				  
				  tbsCommerceItem3.getTBSItemInfo() >> tbsItemInfo1
				  tbsCommerceItem3.getPriceInfo() >> tbsPriceInfo1
				  
				  tbsCommerceItem4.getTBSItemInfo() >> tbsItemInfo1
				  tbsCommerceItem4.getPriceInfo() >> tbsPriceInfo1
				  
		 
		 ltlAssemblyFeeCommItem1.getTBSItemInfo() >> ltlAssmblyItemInfo1
		 ltlAssemblyFeeCommItem1.getPriceInfo() >> ltlAssemblyItemPriceInfo1
		 
		 ltlAssemblyFeeCommItem2.getTBSItemInfo() >> null
		 ltlAssemblyFeeCommItem2.getPriceInfo() >> ltlAssemblyItemPriceInfo1
		 
		 ltlAssemblyFeeCommItem3.getTBSItemInfo() >> ltlAssmblyItemInfo1
		 ltlAssemblyFeeCommItem3.getPriceInfo() >> ltlAssemblyItemPriceInfo1
		 
		 ltlAssemblyFeeCommItem4.getTBSItemInfo() >> ltlAssmblyItemInfo1
		 ltlAssemblyFeeCommItem4.getPriceInfo() >> ltlAssemblyItemPriceInfo1
		 
		 ltlDeliveryChargeCommItem1.getTBSItemInfo() >> ltlDeliveryItemInfo1
		 ltlDeliveryChargeCommItem1.getPriceInfo() >> ltlDeliveryItemPriceInfo1
		 
		 ltlDeliveryChargeCommItem2.getTBSItemInfo() >> null
		 ltlDeliveryChargeCommItem2.getPriceInfo() >> ltlDeliveryItemPriceInfo1
		 
		 ltlDeliveryChargeCommItem3.getTBSItemInfo() >> ltlDeliveryItemInfo1
		 ltlDeliveryChargeCommItem3.getPriceInfo() >> ltlDeliveryItemPriceInfo1
		 
		 ltlDeliveryChargeCommItem4.getTBSItemInfo() >> ltlDeliveryItemInfo1
		 ltlDeliveryChargeCommItem4.getPriceInfo() >> ltlDeliveryItemPriceInfo1
		 
		 giftWrapCommerceItem1.getTBSItemInfo() >> giftWrapItemInfo1
		 giftWrapCommerceItem1.getPriceInfo() >> giftWrapItemPriceInfo1
		 
		 giftWrapCommerceItem2.getTBSItemInfo() >> null
		 giftWrapCommerceItem2.getPriceInfo() >> giftWrapItemPriceInfo1
		 
		 giftWrapCommerceItem3.getTBSItemInfo() >> giftWrapItemInfo1
		 giftWrapCommerceItem3.getPriceInfo() >> giftWrapItemPriceInfo1
		 
		 giftWrapCommerceItem4.getTBSItemInfo() >> giftWrapItemInfo1
		 giftWrapCommerceItem4.getPriceInfo() >> giftWrapItemPriceInfo1
		 
		 commerceItems.addAll([tbsCommerceItem1,tbsCommerceItem2,tbsCommerceItem3,tbsCommerceItem4,ltlAssemblyFeeCommItem1,ltlAssemblyFeeCommItem2,ltlAssemblyFeeCommItem3,ltlAssemblyFeeCommItem4,ltlDeliveryChargeCommItem1,ltlDeliveryChargeCommItem2,ltlDeliveryChargeCommItem3,ltlDeliveryChargeCommItem4,giftWrapCommerceItem1,giftWrapCommerceItem2,giftWrapCommerceItem3,giftWrapCommerceItem4])
		 
		 Map<String, TaxPriceInfo> taxInfos1 = new HashMap<>()
		 Map<String, TaxPriceInfo> taxInfos2 = new HashMap<>()
		 TaxPriceInfo  taxPriceInfo1 = Mock()
		 
		 taxPriceInfo1.getCityTax() >> 3.00
		 taxPriceInfo1.getCountryTax() >> 4.00
		 taxPriceInfo1.getCountyTax() >> 4.00
		 taxPriceInfo1.getStateTax() >> 5.00
		 
		 taxPriceInfo1.getShippingItemsTaxPriceInfos() >> taxInfos1 >> taxInfos2
		 taxInfos1.put(shipGroupId, taxPriceInfo1)
		 taxInfos1.put("SG10", taxPriceInfo1)
		 
		 (1.._) * orderMock.getCommerceItems() >> commerceItems
		 //orderMock.getShippingGroups() >> [] >> shippingGroups // To skip the execution (major part) of the method appendProducts()
		 orderMock.getShippingGroups() >> shippingGroups // To skip the execution (major part) of the method appendProducts()
		 shipGrpCommItemRel.getCommerceItem() >> commerceItem1
		 auxiliaryDataMock1.getProductId() >> "prod01"
		 
		 List<PricingAdjustment> commPricingAdjustments = new ArrayList<>()
		 
		 //itemPriceInfo1.getAdjustments() 
		 
		 commerceItem1.getAuxiliaryData() >> auxiliaryDataMock1		 
		 commerceItem1.getCatalogRefId() >> "sku01"
		 commerceItem1.getPriceInfo() >> itemPriceInfo1
		 
		 shipGrpCommItemRel.getQuantity() >> 2
		 shipGrpCommItemRel.getShippingGroup() >> shippingGroupMock1
		 
		 //itemPriceInfo1.getAdjustments() >> []
		 itemPriceInfo1.getAdjustments() >> itemPricingAdjustments
		 
		 shipGrpCommItmRelList.addAll([shipGrpCommItemRel])
		 shippingGroupMock1.getCommerceItemRelationships() >> shipGrpCommItmRelList
		 orderMock.getSiteId() >> siteID
		 orderMock.getTaxPriceInfo() >> taxPriceInfo1
		 
		 couponItem1.getRepositoryId() >> couponRepoId1
		 couponRepositoryItems.addAll([couponItem1])
		 RepositoryItem currentPromo1 = Mock()
		 RepositoryItem [] currentPromos1 = [currentPromo1]
		 
		 //currentPromo1.getItemDescriptor() >> {throw new BBBBusinessException("")}
		 
		 (1.._) * promotionToolsMock.getCouponListFromOrder(orderMock) >> couponRepositoryItems
		 (1.._) * catalogToolsMock.getPromotions(couponRepoId1) >> {throw new BBBBusinessException("")}
		 //1 * orderMock.getPaymentGroups() >> [] 
		 1 * orderMock.getPaymentGroups() >> {throw new BBBBusinessException("")}
		 //catalogToolsMock.getSiteDetailFromSiteId(siteID) >> siteVO
		 
		 when : 
		 
		 orderOmnitureDropletSpy.service(requestMock, responseMock)
		 
		 then : 
		 
		 1 * orderOmnitureDropletSpy.logError("BBBBusinessException occourred from Catalog API call")
		 1 * orderOmnitureDropletSpy.logError('Business Error while re trieving shipping method for [OneDay]', _)
		 1 * requestMock.setParameter(BBBCoreConstants.ERROR,"Error occourred in finding order details")
	     1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_OPARAM, requestMock, responseMock)
	 }   
   
	 /*
	  * Alternative(negative) branches covered : 
	  * 
	  * #780 - if (!((BBBOrder) order).getShippingGroups().isEmpty()) - No shipping groups in order
	  * #726 - if (((BBBOrder) order).getCommerceItems().size() > 0) - No commerce items in order 
	  * #178 - if (isLoggingError()) - loggingError disabled
	  */
	 
	 def "service - No commerce item & shipping group in order for TBS site"() {
		 
		 given :
		 
		 def couponRepoId1 = "coupon01"
		 
		 BBBOrderImpl orderMock = Mock()
		 
		 Profile profileMock = Mock()
		 
		 PriceInfoVO orderPriceInfoVO = new PriceInfoVO()
		 
		 BBBCommerceItem commerceItem1 = Mock()
		 
		 TBSCommerceItem tbsCommerceItem1 = Mock()
		 
		 BBBHardGoodShippingGroup shippingGroupMock1 = Mock()
		 
		 RepositoryItem shippingMethodItem1 = Mock()
		 
		 List<ShippingGroup> shippingGroups = new ArrayList<>()
		 
		 ShippingGroupCommerceItemRelationship shipGrpCommItemRel = Mock()
		 
		 PricingAdjustment pricingAdjustment1 = Mock()
		 
		 PricingAdjustment shippingPriceAdjustment1 = Mock()
		 
		 RepositoryItem shippingPricingModel3 = Mock()
		 
		 ItemPriceInfo itemPriceInfo1 = Mock()
		 
		 AuxiliaryData auxiliaryDataMock1 = Mock()
		 
		 RepositoryItem couponItem1 = Mock()
		 
		 TaxPriceInfo  taxPriceInfo1 = Mock()
		 
		 List<PricingAdjustment> shippingPriceAdjustments = new ArrayList<>()
		 List<PricingAdjustment> itemPricingAdjustments = new ArrayList<>()
		 List<CommerceItem> commerceItems = new ArrayList<>()
		 List<ShippingGroupCommerceItemRelationship> shipGrpCommItmRelList = new ArrayList<>()
		 List<RepositoryItem>  couponRepositoryItems = new ArrayList<>()
		 List<PricingAdjustment> commPricingAdjustments = new ArrayList<>()
		 
		 SiteVO siteVO = new SiteVO()
		 
		 Map<String, TaxPriceInfo> taxInfos1 = new HashMap<>()
		 Map<String, TaxPriceInfo> taxInfos2 = new HashMap<>()
		 
		 def siteID = "BBB_TBS"
		 def shipGroupId = "SG01"
		 
		 BBBShippingPriceInfo shippingPriceInfo1 = Mock()
		 
		 orderOmnitureDropletSpy.setLoggingError(false)
		 pricingAdjustment1.getAdjustmentDescription() >> "" >> TBSConstants.PRICE_OVERRIDE_DESC >> "" >> TBSConstants.PRICE_OVERRIDE_DESC >> "" >> TBSConstants.PRICE_OVERRIDE_DESC >> "" >> TBSConstants.PRICE_OVERRIDE_DESC >>
		 
		 orderPriceInfoVO.setShippingSavings(15.00)
		 orderPriceInfoVO.setTotalShippingAmount(30.00)
		 
		 1 * requestMock.getObjectParameter(BBBCoreConstants.ORDER) >> orderMock
		 1 * requestMock.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE)) >> profileMock
		 
		 shippingGroups.addAll([shippingGroupMock1])
		 
		 (1.._) * pricingToolsMock.getOrderPriceInfo(orderMock) >> orderPriceInfoVO
		 
		 (1.._) * pricingToolsMock.round(_, _) >> 15.00
		 
		 itemPricingAdjustments.addAll([pricingAdjustment1,pricingAdjustment1])
		 
		 catalogToolsMock.getShippingMethod("OneDay") >> {throw new BBBBusinessException("")}
		 shippingPriceAdjustment1.getAdjustmentDescription() >> "Shipping Override"
		 shippingPriceAdjustments.addAll([shippingPriceAdjustment1])
		 
		 taxPriceInfo1.getCityTax() >> 3.00
		 taxPriceInfo1.getCountryTax() >> 4.00
		 taxPriceInfo1.getCountyTax() >> 4.00
		 taxPriceInfo1.getStateTax() >> 5.00
		 
		 taxPriceInfo1.getShippingItemsTaxPriceInfos() >> taxInfos1 >> taxInfos2
		 taxInfos1.put(shipGroupId, taxPriceInfo1)
		 taxInfos1.put("SG10", taxPriceInfo1)
		 
		 (1.._) * orderMock.getCommerceItems() >> []
		 (1.._) * orderMock.getShippingGroups() >> []
		 shipGrpCommItemRel.getCommerceItem() >> commerceItem1
		 auxiliaryDataMock1.getProductId() >> "prod01"
		 
		 commerceItem1.getAuxiliaryData() >> auxiliaryDataMock1
		 commerceItem1.getCatalogRefId() >> "sku01"
		 commerceItem1.getPriceInfo() >> itemPriceInfo1
		 
		 shipGrpCommItemRel.getQuantity() >> 2
		 shipGrpCommItemRel.getShippingGroup() >> shippingGroupMock1
		 
		 itemPriceInfo1.getAdjustments() >> itemPricingAdjustments
		 
		 shipGrpCommItmRelList.addAll([shipGrpCommItemRel])
		 shippingGroupMock1.getCommerceItemRelationships() >> shipGrpCommItmRelList
		 orderMock.getSiteId() >> siteID
		 orderMock.getTaxPriceInfo() >> taxPriceInfo1
		 
		 couponItem1.getRepositoryId() >> couponRepoId1
		 couponRepositoryItems.addAll([couponItem1])
		 RepositoryItem currentPromo1 = Mock()
		 RepositoryItem [] currentPromos1 = [currentPromo1]
		 
		 (1.._) * promotionToolsMock.getCouponListFromOrder(orderMock) >> couponRepositoryItems
		 (1.._) * catalogToolsMock.getPromotions(couponRepoId1) >> {throw new BBBBusinessException("")}
		 1 * orderMock.getPaymentGroups() >> {throw new BBBBusinessException("")}
		 
		 when :
		 
		 orderOmnitureDropletSpy.service(requestMock, responseMock)
		 
		 then :
		
		 1 * requestMock.serviceParameter(BBBCoreConstants.ERROR_OPARAM, requestMock, responseMock)
		 0 * orderOmnitureDropletSpy.logError("BBBBusinessException occourred from Catalog API call")
	 }
	 
	 
   /*================================
    * 							    * 
	* Service - Test cases - ENDS   * 
	*							    * 
	* ===============================
	*/

  /*================================================
   *											   *
   * getOmnitureVO - Test cases - STARTS   		   *
   * 											   *
   * 											   *
   * Method Signature :    						   *
   * 											   *
   * public OrderOmnitureVO getOmnitureVO() 	   *
   * throws BBBSystemException,	   				   *
   *        BBBBusinessException				   *						       
   *================================================
   */
  
  def "getOmnitureVO - Omniture details retrieved successfully (happy flow)" () {
	  
	  given : 
	  
	  OrderOmnitureVO orderOmnitureVo = new OrderOmnitureVO()
	  OrderOmnitureVO resultOrderOmnitureVO
	  OrderHolder cartMock = Mock()
	  BBBOrderImpl lastOrderMock = Mock()
	  
	  def purchaseId = "order01"
	  def state = "NY"
	  
	  orderOmnitureVo.setState(state)
	  orderOmnitureVo.setPurchaseID(purchaseId)
	  
	  1 * requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> cartMock
	  1 * requestMock.getObjectParameter(BBBCoreConstants.OMNITURE_VO) >> orderOmnitureVo
	  
	  1 * cartMock.getLast() >> lastOrderMock
	  
	  1 * requestMock.setParameter(BBBCoreConstants.ORDER,lastOrderMock)
	  
	  when :
	   
	  resultOrderOmnitureVO = orderOmnitureDropletSpy.getOmnitureVO()
	  
	  then : 
	  
	  resultOrderOmnitureVO.getState().equals(state)
	  resultOrderOmnitureVO.getPurchaseID().equals(purchaseId)
  }
  
  
  def "getOmnitureVO - ServletException while getting Order Omniture details" () {
	  
	  given :
	  
	  OrderHolder cartMock = Mock()
	  def exceptionMessage = "ServletException"
	  1 * requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> cartMock
	  1 * requestMock.getObjectParameter(BBBCoreConstants.OMNITURE_VO) >> {throw new ServletException(exceptionMessage) }
	  
	  1 * requestMock.setParameter(BBBCoreConstants.ORDER,_)
	  
	  when :
	   
	  orderOmnitureDropletSpy.getOmnitureVO()
	  
	  then :
	  
	  thrown BBBSystemException
	  1 * orderOmnitureDropletSpy.logError('Method: BBBOrderOmnitureDroplet.BBBOrderOmnitureDroplet, Servlet Exception while getting order Omniture VO javax.servlet.ServletException: ServletException', null)
  }
  
  def "getOmnitureVO - IOException while getting Order Omniture details" () {
	  
	  given :
	  
	  OrderHolder cartMock = Mock()
	  def exceptionMessage = "IOException"
	  1 * requestMock.resolveName(BBBCoreConstants.SHOPPING_CART_PATH) >> cartMock
	  1 * requestMock.getObjectParameter(BBBCoreConstants.OMNITURE_VO) >> {throw new IOException(exceptionMessage) }
	  
	  1 * requestMock.setParameter(BBBCoreConstants.ORDER,_)
	  
	  when :
	   
	  orderOmnitureDropletSpy.getOmnitureVO()
	  
	  then :
	  
	  thrown BBBSystemException
	  1 * orderOmnitureDropletSpy.logError('Method: BBBOrderOmnitureDroplet.BBBOrderOmnitureDroplet, IO Exception while getting order Omniture VO java.io.IOException: IOException')
  }
  
  
  /*================================================
   *											   *
   * getOmnitureVO - Test cases - ENDS   		   *
   *											   * 
   *================================================
   */
  
     
   
	
}


