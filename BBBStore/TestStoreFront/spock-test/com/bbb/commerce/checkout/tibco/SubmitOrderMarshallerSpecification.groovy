package com.bbb.commerce.checkout.tibco


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.TransactionManager
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.validation.Schema
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils
import org.xml.sax.SAXException

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec
import atg.commerce.CommerceException
import atg.commerce.order.AuxiliaryData;
import atg.commerce.order.CommerceItem
import atg.commerce.order.CommerceItemNotFoundException
import atg.commerce.order.CommerceItemRelationship
import atg.commerce.order.CreditCard
import atg.commerce.order.InvalidParameterException
import atg.commerce.order.OrderTools
import atg.commerce.order.PaymentGroup
import atg.commerce.order.PaymentGroupRelationshipContainer
import atg.commerce.order.PropertyNameConstants;
import atg.commerce.order.RepositoryContactInfo
import atg.commerce.order.ShippingGroup
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.DetailedItemPriceInfo
import atg.commerce.pricing.ItemPriceInfo
import atg.commerce.pricing.OrderPriceInfo
import atg.commerce.pricing.PricingAdjustment
import atg.commerce.pricing.TaxPriceInfo
import atg.commerce.profile.CommerceProfileTools
import atg.core.util.Address
import atg.dtm.TransactionDemarcationException;
import atg.repository.MutableRepository
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemDescriptor

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.VendorInfoVO
import com.bbb.commerce.checkout.tibco.SubmitOrderMarshaller.TAX_TYPE;
import com.bbb.commerce.common.BBBRepositoryContactInfo
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBCreditCard
import com.bbb.commerce.order.BBBGiftCard
import com.bbb.commerce.order.BBBOrderManager
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;
import com.bbb.commerce.order.Paypal
import com.bbb.commerce.order.PaypalStatus
import com.bbb.commerce.order.TBSShippingInfo
import com.bbb.commerce.pricing.BBBPricingTools
import com.bbb.commerce.pricing.bean.PriceInfoVO
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.ecommerce.order.BBBOrderImpl
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.ecommerce.order.BBBShippingGroup
import com.bbb.ecommerce.order.BBBStoreShippingGroup
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.crypto.AESEncryptorComponent
import com.bbb.framework.crypto.EncryptorException
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.jaxb.submitorder.AdjustmentListType
import com.bbb.framework.jaxb.submitorder.AdjustmentType
import com.bbb.framework.jaxb.submitorder.AssemblyFeeType
import com.bbb.framework.jaxb.submitorder.EcoFeeType
import com.bbb.framework.jaxb.submitorder.ItemPriceInfoType
import com.bbb.framework.jaxb.submitorder.ItemType
import com.bbb.framework.jaxb.submitorder.ObjectFactory
import com.bbb.framework.jaxb.submitorder.OrderInfoType
import com.bbb.framework.jaxb.submitorder.OrderType
import com.bbb.framework.jaxb.submitorder.OrderTypes;
import com.bbb.framework.jaxb.submitorder.Orders
import com.bbb.framework.jaxb.submitorder.PaymentTypes;
import com.bbb.framework.jaxb.submitorder.PcInfoType
import com.bbb.framework.jaxb.submitorder.ShipSurchargeType
import com.bbb.framework.jaxb.submitorder.TaxInfoType
import com.bbb.integration.cybersource.creditcard.CreditCardStatus
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.BBBDetailedItemPriceInfo
import com.bbb.order.bean.BBBOrderPriceInfo
import com.bbb.order.bean.BBBShippingPriceInfo
import com.bbb.order.bean.GiftWrapCommerceItem
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem
import com.bbb.order.bean.NonMerchandiseCommerceItem
import com.bbb.order.bean.TBSCommerceItem
import com.bbb.order.bean.TBSItemInfo
import com.bbb.order.bean.TrackingInfo
import com.bbb.payment.giftcard.GiftCardStatus
import com.bbb.selfservice.common.StoreDetails
import com.bbb.selfservice.manager.SearchStoreManager
import com.bbb.utils.CommonConfiguration
import com.itextpdf.awt.geom.CubicCurve2D.Double;
import com.itextpdf.awt.geom.CubicCurve2D.Double;
import com.bbb.commerce.checkout.tibco.SubmitOrderMarshaller.TAX_TYPE;
/**
 * 
 * @author kmagud
 *
 */
class SubmitOrderMarshallerSpecification extends BBBExtendedSpec {

	SubmitOrderMarshaller testObj
	BBBOrder bbbOrderMock = Mock()
	OrderTools orderToolsMock = Mock()
	BBBOrderImpl bbbOrderImplMock = Mock()
	BBBPricingTools pricingToolsMock = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	RepositoryItem repositoryItemMock = Mock()
	BBBOrderManager bbbOrderManagerMock = Mock()
	CommonConfiguration commonConfigurationMock = Mock()
	TaxPriceInfo taxPriceInfoMock = Mock()
	SearchStoreManager searchStoreManagerMock = Mock()
	AESEncryptorComponent aesEncryptorComponentMock = Mock()
	SubmitOrderVO submitOrderVOMock = Mock()
	TransactionManager transactionManagerMock = Mock()
	Orders ordersMock = Mock()
	ShippingGroup shippingGroupMock = Mock()
	TBSShippingInfo tBSShippingInfoMock = Mock()
	BBBHardGoodShippingGroup bbbHardGoodShippingGroupMock = Mock()
	RepositoryItemDescriptor repositoryItemDescriptorMock = Mock()
	MutableRepositoryItem mutableRepositoryItemMock = Mock()
	RepositoryItem userSiteItemMock = Mock()
	OrderPriceInfo orderPriceInfoMock = Mock()
	BBBOrderPriceInfo bbbOrderPriceInfoMock = Mock()
	BBBCommerceItem bbbCommerceItemMock = Mock()
	TBSCommerceItem tbsCommerceItemMock = Mock()
	BBBRepositoryContactInfo bbbRepositoryContactInfoMock = Mock()
	CommerceProfileTools commerceProfileToolsMock = Mock()
	BBBShippingPriceInfo bbbShippingPriceInfoMock = Mock()
	LTLDeliveryChargeCommerceItem ltlDeliveryChargeCommerceItemMock = Mock()
	BBBDetailedItemPriceInfo bbbDetailedItemPriceInfoMock = Mock()
	BBBShippingGroupCommerceItemRelationship bbbShippingGroupCommerceItemRelationshipMock = Mock()
	GiftWrapCommerceItem giftWrapCommerceItemMock = Mock()
	ItemPriceInfo itemPriceInfoMock = Mock()
	BBBCommerceItemManager bbbCommerceItemManagerMock = Mock()
	PriceInfoVO priceInfoVOMock = Mock()
	TrackingInfo trackingInfoMock = Mock()
	StoreDetails storeDetailsMock = Mock()
	SKUDetailVO skuDetailVOMock = Mock()
	NonMerchandiseCommerceItem nonMerchandiseCommerceItemMock = Mock()
	ItemType itemTypeMock = Mock()
	PricingAdjustment pricingAdjustmentMock = Mock()
	RepositoryItem adjustmentPricingModel = Mock()
	RepositoryItem couponRepositoryItem = Mock()
	ItemPriceInfoType itemPriceInfoTypeMock = Mock()
	ObjectFactory objectFactoryMock = Mock()
	ShippingGroupCommerceItemRelationship shippingGroupCommerceItemRelationshipMock = Mock()
	PcInfoType pcInfoTypeMock = Mock()
	ShipMethodVO shipMethodVOMock = Mock()
	Address addressMock = Mock()
	AuxiliaryData auxiliaryDataMock = Mock()
	TaxInfoType taxInfoTypeMock = Mock()
	AdjustmentListType adjustmentListTypeMock = Mock()
	AdjustmentType adjustmentTypeMock = Mock()
	CommerceItem assemblyCommerceItemMock = Mock()
	AssemblyFeeType assemblyFeeTypeMock = Mock()
	RepositoryItem dsLineItemRepositoryItemMock = Mock()
	CommerceItem deliveryCommerceItemMock = Mock()
	ShipSurchargeType shipSurchargeTypeMock = Mock()
	EcoFeeType ecoFeeTypeMock = Mock()
	CommerceItem ecoFeeCommerceItemMock = Mock()
	BBBStoreShippingGroup bbbStoreShippingGroupMock = Mock()
	TBSItemInfo tbsItemInfoMock = Mock()
	LTLAssemblyFeeCommerceItem ltlAssemblyFeeCommerceItemMock = Mock()
	BBBOrderTools bbbOrderToolsMock = Mock()
	BBBCreditCard bbbCreditCardMock = Mock()
	BBBGiftCard bbbGiftCardMock = Mock()
	CreditCardStatus creditCardStatusMock = Mock()
	PaypalStatus paypalStatusMock = Mock()
	Paypal paypalMock = Mock()
	VendorInfoVO vendorInfoVOMock = Mock()
	GiftCardStatus giftCardStatusMock = Mock()
	ItemPriceInfo itemPriceInformationMock = Mock()
	RepositoryItem skuAttrRepositoryItemMock = Mock()
	RepositoryItem skuAttributeMock = Mock()
	
	Map<TAX_TYPE, BigDecimal> taxMapMock = new HashMap<TAX_TYPE, BigDecimal>()
	Map<String, TaxPriceInfo> itemTaxInfoMock = new HashMap<String, TaxPriceInfo>()
	Map<String, String> tBSSiteIdMap = ["TBS_BedBathUS":"TBSBedBathUS","TBS_BuyBuyBaby":"TBSBedBathBaby","TBS_BedBathCanada":"TBSBedBathCanada"]
	String territories = "AS,GU,FM,MP,PR,PW,MH,VI"
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	Set<RepositoryItem> skuAttrRelationMock = new HashSet<RepositoryItem>()
	Map<String, RepositoryItem> userSiteItems = new HashMap<String, RepositoryItem>()

	def setup(){
		
		testObj = new SubmitOrderMarshaller(catalogTools:catalogToolsMock, orderManager:bbbOrderManagerMock, pricingTools:pricingToolsMock, commonConfiguration:commonConfigurationMock,
			encryptorTools:aesEncryptorComponentMock, storeManager:searchStoreManagerMock,TBSSiteIdMap:tBSSiteIdMap,territories:territories)
		
	}
	
	////////////////////////////////////////TestCases for isInternationalOrder --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public boolean isInternationalOrder(String saleChannel) ///////////
	

	def "isInternationalOrder. when sales channel is D_BFREE"(){
		given:
			String salesChannel = "D_BFREE"

		when:
			boolean isInternationalOrder = testObj.isInternationalOrder(salesChannel)

		then:
			isInternationalOrder == true
	}

	def "isInternationalOrder. when sales channel is A_BFREE"(){
		given:
			String salesChannel = "A_BFREE"

		when:
			boolean isInternationalOrder = testObj.isInternationalOrder(salesChannel)

		then:
			isInternationalOrder == true
	}

	def "isInternationalOrder. when sales channel is M_BFREE"(){
		given:
			String salesChannel = "M_BFREE"

		when:
			boolean isInternationalOrder = testObj.isInternationalOrder(salesChannel)

		then:
			isInternationalOrder == true
	}

	def "isInternationalOrder. when channel is unlisted"(){
		given:
			String salesChannel = StringUtils.EMPTY

		when:
			boolean isInternationalOrder = testObj.isInternationalOrder(salesChannel)
		
		then:
			isInternationalOrder == false
	}
	
	////////////////////////////////////////TestCases for isInternationalOrder --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for isInternationalSurchargeOn --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public boolean isInternationalSurchargeOn() ///////////

	def "isInternationalSurchargeOn. when International Surcharge is ON"(){
		given:
			boolean isIntShipSurchargeOn = false
			1 * catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
						BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_SURCHARGE_SWITCH) >> ["true"]

		when:
			isIntShipSurchargeOn = testObj.isInternationalSurchargeOn()

		then:
			isIntShipSurchargeOn == true
	}

	def "isInternationalSurchargeOn. when Shipping Charge value is NULL"(){
		given:
			boolean isIntShipSurchargeOn = false
			1 * catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
						BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_SURCHARGE_SWITCH) >> null

		when:
			isIntShipSurchargeOn = testObj.isInternationalSurchargeOn()

		then:
			isIntShipSurchargeOn == false
	}

	def "isInternationalSurchargeOn. when Shipping Charge value is EMPTY"(){
		given:
			boolean isIntShipSurchargeOn = false
			1 * catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_SURCHARGE_SWITCH) >> []

		when:
			isIntShipSurchargeOn = testObj.isInternationalSurchargeOn()

		then:
			isIntShipSurchargeOn == false
	}

	def "isInternationalSurchargeOn. when International Surcharge determnination throws BBBSystemException"(){
		given:
			boolean isIntShipSurchargeOn = false
			1*catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_SURCHARGE_SWITCH) >> {throw new BBBSystemException("")}

		when:
			isIntShipSurchargeOn = testObj.isInternationalSurchargeOn()

		then:
			isIntShipSurchargeOn == false
	}

	def "isInternationalSurchargeOn. when International Surcharge determnination throws BBBBusinessException"(){
		given:
			boolean isIntShipSurchargeOn = false
			1*catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_SURCHARGE_SWITCH) >> {throw new BBBBusinessException("")}

		when:
			isIntShipSurchargeOn = testObj.isInternationalSurchargeOn()

		then:
			isIntShipSurchargeOn == false
	}
	
	////////////////////////////////////////TestCases for isInternationalSurchargeOn --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for createTaxPriceInfo --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public Map<TAX_TYPE, BigDecimal> createTaxPriceInfo(final Map<TAX_TYPE, BigDecimal> taxMap, final Map<String, TaxPriceInfo> itemTaxPriceInfos, String commerceItemId) ///////////
	
	def "createTaxPriceInfo. This TC is the happy flow of createTaxPriceInfo"(){
		given:
			String commerceItemId = "SKU12345"
			taxPriceInfoMock.getCityTax() >> 11.2d
			taxPriceInfoMock.getCountyTax() >> 22.2d
			taxPriceInfoMock.getDistrictTax() >> 33.3d
			taxPriceInfoMock.getStateTax() >> 44.4d
			taxPriceInfoMock.getAmount() >> 55.5d
			itemTaxInfoMock.put("SKU12345", taxPriceInfoMock)
			Map<TAX_TYPE, BigDecimal> taxMapResults = new HashMap<TAX_TYPE, BigDecimal>()
		when:
			taxMapResults = testObj.createTaxPriceInfo(taxMapMock, itemTaxInfoMock, commerceItemId)

		then:
			taxMapResults.get(TAX_TYPE.CITY).equals(11.2)
			taxMapResults.get(TAX_TYPE.COUNTY).equals(22.2)
			taxMapResults.get(TAX_TYPE.DISTRICT).equals(33.3)
			taxMapResults.get(TAX_TYPE.AMOUNT).equals(55.5)
			taxMapResults.get(TAX_TYPE.STATE).equals(44.4)
	}

	def "createTaxPriceInfo. This TC is when itemTaxPriceInfo returns NULL"(){
		given:
			String commerceItemId = "SKU12345"
			itemTaxInfoMock.get(commerceItemId) >> null
			Map<TAX_TYPE, BigDecimal> taxMapResults = new HashMap<TAX_TYPE, BigDecimal>()
		when:
			taxMapResults = testObj.createTaxPriceInfo(taxMapMock, itemTaxInfoMock, commerceItemId)

		then:
			taxMapResults == [:]
	}
		
	////////////////////////////////////////TestCases for createTaxPriceInfo --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for marshall --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public String marshall(final ServiceRequestIF pReqVO) ///////////
	
	def "marshall. This TC is the happy flow of marshall"(){
		given:
			marshallSpy()
			bbbOrderManagerMock.isLoggingDebug() >> TRUE
			submitOrderVOMock.setOrder(bbbOrderImplMock)
			bbbOrderImplMock.getId() >> "BBB12345"
			testObj.getOrderAsXML(bbbOrderImplMock) >> "submitOrderXml"
			commonConfigurationMock.setPersistOrderXML(TRUE)
			bbbOrderManagerMock.getOrderTools() >> orderToolsMock
			orderToolsMock.getTransactionManager() >> transactionManagerMock
		
		when:
			def results = testObj.marshall(submitOrderVOMock)

		then:
			results == "submitOrderXml"
			1 * bbbOrderManagerMock.logDebug('END: Submit Order Marshaller')
			1 * testObj.marshall(submitOrderVOMock)
			1 * bbbOrderManagerMock.logDebug('Marshalling order [BBB12345]')
			1 * bbbOrderManagerMock.logDebug('Marshalling complete for order [BBB12345]')
			1 * bbbOrderManagerMock.logDebug('START: Submit Order Marshaller')
			1 * bbbOrderManagerMock.updateOrderXML(bbbOrderImplMock, 'submitOrderXml')
			1 * transactionManagerMock.begin()
			1 * bbbOrderManagerMock.updateOrder(bbbOrderImplMock)
	}

	def "marshall. This TC is when TransactionDemarcationException thrown in marshall"(){
		given:
			marshallSpy()
			bbbOrderManagerMock.isLoggingDebug() >> TRUE
			bbbOrderImplMock.getId() >> "BBB12345"
			testObj.getOrderAsXML(bbbOrderImplMock) >> "submitOrderXml"
			commonConfigurationMock.setPersistOrderXML(TRUE)
			bbbOrderManagerMock.getOrderTools() >> orderToolsMock
			orderToolsMock.getTransactionManager() >> transactionManagerMock
		
		when:
			def results = testObj.marshall(submitOrderVOMock)

		then:
			results == "submitOrderXml"
			1 * transactionManagerMock.begin() >> {throw new TransactionDemarcationException("Mock for TransactionDemarcationException")}
			1 * bbbOrderManagerMock.logError('checkout_1016: Transaction demarcation failure while p updating Order XML', _)
			1 * bbbOrderManagerMock.logDebug('Marshalling order [BBB12345]')
			1 * bbbOrderManagerMock.logDebug('START: Submit Order Marshaller')
			1 * bbbOrderManagerMock.logDebug('END: Submit Order Marshaller')
	}
	
	def "marshall. This TC is when OrderManager isLoggingDebug is false"(){
		given:
			marshallSpy()
			bbbOrderManagerMock.isLoggingDebug() >> FALSE
			bbbOrderImplMock.getId() >> "BBB12345"
			testObj.getOrderAsXML(bbbOrderImplMock) >> "submitOrderXml"
			commonConfigurationMock.setPersistOrderXML(TRUE)
			bbbOrderManagerMock.getOrderTools() >> orderToolsMock
			orderToolsMock.getTransactionManager() >> transactionManagerMock
		
		when:
			def results = testObj.marshall(submitOrderVOMock)

		then:
			results == "submitOrderXml"
			0 * bbbOrderManagerMock.logDebug('END: Submit Order Marshaller')
			1 * testObj.marshall(submitOrderVOMock)
			0 * bbbOrderManagerMock.logDebug('Marshalling order [BBB12345]')
			0 * bbbOrderManagerMock.logDebug('Marshalling complete for order [BBB12345]')
			0 * bbbOrderManagerMock.logDebug('START: Submit Order Marshaller')
			1 * bbbOrderManagerMock.updateOrderXML(bbbOrderImplMock, 'submitOrderXml')
			1 * transactionManagerMock.begin()
			1 * bbbOrderManagerMock.updateOrder(bbbOrderImplMock)
	}
	
	def "marshall. This TC is when submitOrderXml is empty"(){
		given:
			marshallSpy()
			bbbOrderManagerMock.isLoggingDebug() >> TRUE
			bbbOrderImplMock.getId() >> "BBB12345"
			testObj.getOrderAsXML(bbbOrderImplMock) >> ""
			commonConfigurationMock.setPersistOrderXML(TRUE)
		
		when:
			def results = testObj.marshall(submitOrderVOMock)

		then:
			results == ""
			1 * bbbOrderManagerMock.logDebug('Marshalling complete for order [BBB12345]')
			1 * bbbOrderManagerMock.logDebug('Marshalling order [BBB12345]')
			1 * testObj.marshall(submitOrderVOMock)
			1 * bbbOrderManagerMock.logDebug('END: Submit Order Marshaller')
			1 * bbbOrderManagerMock.logDebug('START: Submit Order Marshaller')
			0 * bbbOrderManagerMock.updateOrderXML(bbbOrderImplMock, 'submitOrderXml')
			0 * bbbOrderManagerMock.updateOrder(bbbOrderImplMock)
			
	}
	
	def "marshall. This TC is when isPersistOrderXML is false"(){
		given:
			marshallSpy()
			bbbOrderManagerMock.isLoggingDebug() >> TRUE
			bbbOrderImplMock.getId() >> "BBB12345"
			testObj.getOrderAsXML(bbbOrderImplMock) >> "submitOrderXml"
			commonConfigurationMock.setPersistOrderXML(FALSE)
		
		when:
			def results = testObj.marshall(submitOrderVOMock)

		then:
			results == "submitOrderXml"
			1 * bbbOrderManagerMock.logDebug('Marshalling complete for order [BBB12345]')
			1 * bbbOrderManagerMock.logDebug('Marshalling order [BBB12345]')
			1 * bbbOrderManagerMock.logDebug('START: Submit Order Marshaller')
			1 * bbbOrderManagerMock.logDebug('END: Submit Order Marshaller')		
	}
	
	def "marshall. This TC is when CommerceException thrown in updateOrder"(){
		given:
			marshallSpy()
			bbbOrderManagerMock.isLoggingDebug() >> TRUE
			bbbOrderImplMock.getId() >> "BBB12345"
			testObj.getOrderAsXML(bbbOrderImplMock) >> "submitOrderXml"
			commonConfigurationMock.setPersistOrderXML(TRUE)
			bbbOrderManagerMock.getOrderTools() >> orderToolsMock
			orderToolsMock.getTransactionManager() >> transactionManagerMock
			bbbOrderManagerMock.updateOrder(bbbOrderImplMock) >> {throw new CommerceException("Mock for CommerceException")}
		
		when:
			def results = testObj.marshall(submitOrderVOMock)

		then:
			results == "submitOrderXml"
			1 * bbbOrderManagerMock.logDebug('END: Submit Order Marshaller')
			1 * bbbOrderManagerMock.logDebug('Marshalling order [BBB12345]')
			1 * bbbOrderManagerMock.logDebug('START: Submit Order Marshaller')
			1 * bbbOrderManagerMock.logError('checkout_1016: Transaction failure while  updating Order XML. Rolling back the transaction. Will retry again')
			1 * bbbOrderManagerMock.updateOrderXML(bbbOrderImplMock, 'submitOrderXml')
			1 * bbbOrderManagerMock.logError('checkout_1003: Commerce exception while updating Order XML', _)
			1 * testObj.marshall(submitOrderVOMock)
			1 * transactionManagerMock.begin()
	}
	
	def "marshall. This TC is when the input parameter is not defined"(){
		given:
			marshallSpy()
			bbbOrderManagerMock.isLoggingDebug() >> TRUE
		
		when:
			def results = testObj.marshall(null)

		then:
			results == null
			1 * bbbOrderManagerMock.logDebug('END: Submit Order Marshaller')
			1 * bbbOrderManagerMock.logDebug('START: Submit Order Marshaller')		
	}
	
	def "marshall. This TC is when the order in pReqVO is null"(){
		given:
			marshallSpy()
			bbbOrderManagerMock.isLoggingDebug() >> TRUE
			submitOrderVOMock.setOrder(null)
		
		when:
			def results = testObj.marshall(submitOrderVOMock)

		then:
			results == null
			1 * bbbOrderManagerMock.logDebug('END: Submit Order Marshaller')
			1 * bbbOrderManagerMock.logDebug('START: Submit Order Marshaller')
			
	}
	
	private marshallSpy() {
		testObj = Spy()
		testObj.setOrderManager(bbbOrderManagerMock)
		testObj.setCommonConfiguration(commonConfigurationMock)
		submitOrderVOMock.setOrder(bbbOrderImplMock)
	}
	
	////////////////////////////////////////TestCases for marshall --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for getOrderAsXML --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public String getOrderAsXML(final BBBOrder bbbOrder) ///////////
	
	def "getOrderAsXML. This TC is the happy flow of getOrderAsXML"(){
		given:
			testObj = Spy()
			testObj.transformOrder(bbbOrderImplMock) >> ordersMock
			testObj.marshallOrders(ordersMock) >> "orderXML"
		
		when:
			def results = testObj.getOrderAsXML(bbbOrderImplMock)

		then:
			results == "orderXML"
	}
	
	def "getOrderAsXML. This TC is when JAXBException throws"(){
		given:
			testObj = Spy()
			testObj.transformOrder(bbbOrderImplMock) >> ordersMock
			testObj.marshallOrders(ordersMock) >> {throw new JAXBException("Mock for JAXBException")}
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX12345"
		
		when:
			def results = testObj.getOrderAsXML(bbbOrderImplMock)

		then:
			results == null
			thrown BBBSystemException
			1 * testObj.logError('Error marshaling order object for order XXX12345javax.xml.bind.JAXBException: Mock for JAXBException')
	}
	
	def "getOrderAsXML. This TC is when SAXException throws"(){
		given:
			testObj = Spy()
			testObj.transformOrder(bbbOrderImplMock) >> ordersMock
			testObj.marshallOrders(ordersMock) >> {throw new SAXException("Mock for SAXException")}
		
		when:
			def results = testObj.getOrderAsXML(bbbOrderImplMock)

		then:
			results == null
			thrown BBBSystemException
			1 * testObj.logError('Error parsing the XML against schema for order nullorg.xml.sax.SAXException: Mock for SAXException')
			1 * bbbOrderImplMock.getOnlineOrderNumber()
	}
	
	
	////////////////////////////////////////TestCases for getOrderAsXML --> ENDS//////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////TestCases for transformOrder --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public Orders transformOrder(final BBBOrder bbbOrder) ///////////
	
	def "transformOrder. This TC is the Happy flow of transformOrder with all Private methods"(){
		given:
			this.spyForTransforOrder()
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock,ltlDeliveryChargeCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> taxPriceInfoMock
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock,bbbHardGoodShippingGroupMock]
			bbbOrderImplMock.getShippingGroupCount() >> 1
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			1 * commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "XXX12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> "gs12345"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new GregorianCalendar(2016, Calendar.AUGUST, 3, 23, 59, 45).time
			bbbOrderImplMock.getPriceInfo() >> bbbOrderPriceInfoMock
			bbbOrderPriceInfoMock.getCurrencyCode() >> "US"
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> TRUE
			tBSShippingInfoMock.getTaxExemptId() >> "TaxExemptId"
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			
			//getMemberID Private Method Coverage
			userSiteItems.put("TBS_BedBathUS", userSiteItemMock)
			repositoryItemMock.getPropertyValue("userSiteItems") >> userSiteItems
			userSiteItemMock.getPropertyValue("memberId") >> "78945"
			bbbOrderImplMock.getSiteId() >> "TBS_BedBathUS"
			
			//populatePriceInfo Private Method Coverage
			bbbOrderPriceInfoMock.getTotal() >> 55.99d
			pricingToolsMock.round(55.99,2) >> 56
			bbbHardGoodShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			bbbShippingPriceInfoMock.getAmount() >> 40d
			pricingToolsMock.round(40.0,2) >> 40
			testObj.isInternationalSurchargeOn() >> TRUE
			bbbShippingPriceInfoMock.getFinalSurcharge() >> 20d
			pricingToolsMock.round(20.0,2) >> 20
			BBBShippingGroupCommerceItemRelationship bbbShippingGroupCommerceItemRelationshipMock1 = Mock()
			bbbHardGoodShippingGroupMock.getCommerceItemRelationships() >> [bbbShippingGroupCommerceItemRelationshipMock,bbbShippingGroupCommerceItemRelationshipMock1]
			bbbHardGoodShippingGroupMock.getGiftWrapCommerceItem() >> giftWrapCommerceItemMock
			giftWrapCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			ltlDeliveryChargeCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getAmount() >> 2.2d
			pricingToolsMock.round(2.2,2) >> 2
			
			//populateTaxInfo Private method Coverage
			taxPriceInfoMock.getCityTax() >> 3.2d
			taxPriceInfoMock.getCountyTax() >> 4.2d
			taxPriceInfoMock.getDistrictTax() >> 2.1d
			taxPriceInfoMock.getStateTax() >> 1.1d
			taxPriceInfoMock.getAmount() >> 5.2d
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateAddress Private Method Coverage
			testObj.setTerritories(territories)
			bbbRepositoryContactInfoMock.getFirstName() >> "John"
			bbbRepositoryContactInfoMock.getMiddleName() >> "Abraham"
			bbbRepositoryContactInfoMock.getLastName() >> "Kennedy"
			bbbRepositoryContactInfoMock.getAddress1() >> "Nagal Nagar"
			bbbRepositoryContactInfoMock.getAddress2() >> "1st Street"
			bbbRepositoryContactInfoMock.getCity() >> "Jercy"
			bbbRepositoryContactInfoMock.getPostalCode() >> "70001"
			bbbRepositoryContactInfoMock.getCountry() >> "GB"
			bbbRepositoryContactInfoMock.getState() >> "MP"
			bbbRepositoryContactInfoMock.getCompanyName() >> "john Associates"
			bbbRepositoryContactInfoMock.getEmail() >> "john@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			bbbRepositoryContactInfoMock.getAlternatePhoneNumber() >> "8989898989"
			
			//populateShipping Private method Coverage
			HashMap subtotalPriceInfosMock = ["hg123456": orderPriceInfoMock] 
			bbbOrderPriceInfoMock.getShippingItemsSubtotalPriceInfos() >> subtotalPriceInfosMock
			tbsCommerceItemMock.isCMO() >> TRUE
			bbbHardGoodShippingGroupMock.getId() >> "hg123456"
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getShipOnDate() >> DateUtils.addDays(new Date(), 10)
			bbbHardGoodShippingGroupMock.getRegistryId() >> ""
			HashMap shippingSpecialInstructions = ["IS_COMFIRMATION_ASKED":"true","CONFIRMATION_EMAIL_ID":"johnpaul@gmail.com"]
			bbbHardGoodShippingGroupMock.getSpecialInstructions() >> shippingSpecialInstructions
			bbbHardGoodShippingGroupMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbShippingPriceInfoMock.getRawShipping() >> 21.99d
			bbbShippingPriceInfoMock.getFinalShipping() >> 25.99d
			bbbShippingPriceInfoMock.getSurcharge() >> 12.99d
			bbbShippingPriceInfoMock.getFinalSurcharge() >> 20.99d
			tBSShippingInfoMock.isShipPriceOverride() >> TRUE
			bbbShippingGroupCommerceItemRelationshipMock1.getCommerceItem() >> bbbCommerceItemMock
			bbbCommerceItemMock.isLtlItem() >> TRUE
			bbbCommerceItemMock.getId() >> "c123456"
			bbbOrderManagerMock.getCommerceItemManager() >> bbbCommerceItemManagerMock
			bbbCommerceItemManagerMock.getLTLItemPriceInfo("c123456",_, bbbOrderImplMock) >> priceInfoVOMock
			priceInfoVOMock.getDeliverySurchargeProrated() >> 99.99d
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd12345"
			orderPriceInfoMock.getAmount() >> 50.99d
			
			//getShippingGroupLevelItemTaxPriceInfos Private Method Coverage
			TaxPriceInfo taxPriceInfoMock1 = Mock()
			HashMap shippingItemTaxPriceMock = ["hg123456": taxPriceInfoMock,"shippingTaxPriceInfo":taxPriceInfoMock1,"c123456":taxPriceInfoMock]
			taxPriceInfoMock.getShippingItemsTaxPriceInfos() >> shippingItemTaxPriceMock
			
			//populateGiftInfo Private Method Coverage
			bbbHardGoodShippingGroupMock.containsGiftWrap() >> TRUE
			bbbHardGoodShippingGroupMock.getGiftWrapMessage() >> ""
			giftWrapCommerceItemMock.getCatalogRefId() >> "SKU12345"
			
			//populateStoreInfoType Private Method Coverage
			1 * searchStoreManagerMock.getStoreType("TBS_BedBathUS") >> "Online Store"
			1 * searchStoreManagerMock.searchStoreById("sdd12345",bbbOrderImplMock.getSiteId(), "Online Store") >> storeDetailsMock
			storeDetailsMock.getStoreDescription() >> "Fine Tabletop"
			storeDetailsMock.getStoreName() >> "Harvey"
			storeDetailsMock.getAddress() >> "Tremont Street"
			storeDetailsMock.getCity() >> "Boston"
			storeDetailsMock.getState() >> "Massachusetts"
			storeDetailsMock.getPostalCode() >> "02120"
			storeDetailsMock.getCountry() >> "US"
			storeDetailsMock.getSatStoreTimings() >> "9:00am - 9:30pm"
			storeDetailsMock.getSunStoreTimings() >> "9:00am - 7:00pm"
			storeDetailsMock.getWeekdaysStoreTimings() >> "9:00am - 9:30pm"
			storeDetailsMock.getStorePhone() >> "5044546930"
			
			//populateItems Private Method Coverage
			ItemPriceInfo itemPriceInfoMock1 = Mock()
			bbbCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			BBBDetailedItemPriceInfo bbbDetailedItemPriceInfoMock1 = Mock()
			1 * itemPriceInfoMock.getCurrentPriceDetailsForRange(_) >> [bbbDetailedItemPriceInfoMock,bbbDetailedItemPriceInfoMock1]
			1 * pricingToolsMock.calculateItemSurchargeInSG(bbbOrderImplMock.getSiteId(), bbbShippingGroupCommerceItemRelationshipMock1) >> 87.99d
			bbbDetailedItemPriceInfoMock.getQuantity() >> 2
			bbbDetailedItemPriceInfoMock.getAmount() >> 80.99d
			pricingToolsMock.round(_) >> 80.99d
			bbbCommerceItemMock.getDeliveryItemId() >> "de12345"
			bbbCommerceItemMock.getAssemblyItemId() >> "as12345"
			bbbCommerceItemMock.getCatalogRefId() >> "SKU123456"
			1 * catalogToolsMock.getSKUDetails(bbbOrderImplMock.getSiteId(), bbbCommerceItemMock.getCatalogRefId(), false, true, true) >> skuDetailVOMock
			bbbOrderImplMock.getCommerceItem("de12345") >> nonMerchandiseCommerceItemMock
			nonMerchandiseCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			bbbCommerceItemMock.getQuantity() >> 2
			ItemType itemTypeMock = new ItemType()
			testObj.createItemInfo(*_) >> itemTypeMock
			
			//populateAdjustmentList Private method Coverage
			PricingAdjustment pricingAdjustmentMock1 = Mock()
			bbbShippingPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock,pricingAdjustmentMock1]
			pricingAdjustmentMock.getPricingModel() >> adjustmentPricingModel
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Override"
			pricingAdjustmentMock1.getPricingModel() >> null
			pricingAdjustmentMock1.getAdjustmentDescription() >> "TBS Price Override"
			adjustmentPricingModel.getRepositoryId() >> "pa12345"
			pricingAdjustmentMock.getAdjustment() >> 10d
			pricingToolsMock.round(10.0,2) >> 10
			adjustmentPricingModel.getPropertyValue("typeDetail") >> "adjustmentType"
			adjustmentPricingModel.getPropertyValue("displayName") >> "adjustmentDesc"
			pricingAdjustmentMock1.getAdjustment() >> 23.2d
			pricingToolsMock.round(23.2,2) >> 24
			tBSShippingInfoMock.getShipPriceValue() >> 22.99d
			tBSShippingInfoMock.getShipPriceReason() >> "Heavy Item"
			tBSShippingInfoMock.getSurchargeValue() >> 12.99d
			tBSShippingInfoMock.getSurchargeReason() >> "Heavy ship Item"
			
			//fillCouponDetails Private Method Coverage
			pricingAdjustmentMock.getCoupon() >> couponRepositoryItem
			couponRepositoryItem.getRepositoryId() >> "co12345"
			
			//isTaxCalculationFailure method bcoz it is final method
			HashMap orderSpecialInstructions = ["CYBERSOURCE_TAX_FAILURE":"false"]
			bbbOrderImplMock.getSpecialInstructions() >> orderSpecialInstructions

		when:
			 Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

		then:
			ordersResults != null
			1 * testObj.logDebug('SubmitOrderMarshaller:populateStoreInfoType starts')
			1 * testObj.logDebug('SubmitOrderMarshaller:populateStoreInfoType ends')
			1 * testObj.logDebug('Delivery Surcharge for LTL shippingmethod SDD :99.99')
			1 * testObj.logDebug('Delivery Surcharge for LTL order XXX :2.2')
			1 * testObj.createTaxPriceInfo([:], ['hg123456':taxPriceInfoMock, 'shippingTaxPriceInfo':taxPriceInfoMock1, 'c123456':taxPriceInfoMock], 'as12345')
			1 * testObj.logDebug('storeType is: Online Store')
			2 * testObj.createTaxPriceInfo([:], ['hg123456':taxPriceInfoMock, 'shippingTaxPriceInfo':taxPriceInfoMock1, 'c123456':taxPriceInfoMock], 'de12345')
			ordersResults.getOrder().get(0).getOrderInfo().getAtgOrderId() == "XXX12345"
			ordersResults.getOrder().get(0).getOrderInfo().getOrderNumber() == "XXX"
			ordersResults.getOrder().get(0).getOrderInfo().getOrderType() == OrderTypes.DELIVERY
			ordersResults.getOrder().get(0).getOrderInfo().getTbsStoreNo() == "23456"
			ordersResults.getOrder().get(0).getOrderInfo().getCartCreate() == "GSOrder"
			ordersResults.getOrder().get(0).getOrderInfo().getCartFinal() == "GSOrder"
			ordersResults.getOrder().get(0).getOrderInfo().getSalesOS() == null
			ordersResults.getOrder().get(0).getOrderInfo().getAtgProfileId() == "p12345"
			ordersResults.getOrder().get(0).getOrderInfo().getIpAddress() == "128.168.1.256"
			ordersResults.getOrder().get(0).getOrderInfo().getAffiliate() == "affiliateId"
			ordersResults.getOrder().get(0).getOrderInfo().getMbrNum() == "78945"
			ordersResults.getOrder().get(0).getOrderInfo().getSiteId() == "TBSBedBathUS"
			ordersResults.getOrder().get(0).getOrderInfo().getCurrencyCode() == "US"
			ordersResults.getOrder().get(0).getOrderInfo().getSchoolPromo().getSchoolID() == "sch123456"
			ordersResults.getOrder().get(0).getOrderInfo().getSchoolPromo().getPromoCode() == "SCHCOUPON"
			ordersResults.getOrder().get(0).getOrderInfo().getTaxExemptId() == "TaxExemptId"
			ordersResults.getOrder().get(0).getOrderInfo().isEmailSignUp() == TRUE
			ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getDeviceFingerprint() == "fingerPrint"
			ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getApproverId() == "TBS_LEAD"
			ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getStoreAssociateId() == "TBSAssociateId"
			ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getOrderIdentifier() == "TBS"
			ordersResults.getOrder().get(0).getOrderPriceInfo().getOrderTotal() == 56
			ordersResults.getOrder().get(0).getOrderPriceInfo().getTotalShipping() == 40
			ordersResults.getOrder().get(0).getOrderPriceInfo().getTotalSurcharge() == 20
			ordersResults.getOrder().get(0).getOrderPriceInfo().getTotalGiftWrapCost() == 2
			ordersResults.getOrder().get(0).getOrderPriceInfo().getTotalDeliveryCharge() == 2
			ordersResults.getOrder().get(0).getOrderTaxInfo() == null
			ordersResults.getOrder().get(0).getBillingAddress().getFirstName() == "John"
			ordersResults.getOrder().get(0).getBillingAddress().getMiddleName() == "Abraham"
			ordersResults.getOrder().get(0).getBillingAddress().getLastName() == "Kennedy"
			ordersResults.getOrder().get(0).getBillingAddress().getAddressLine1() == "Nagal Nagar"
			ordersResults.getOrder().get(0).getBillingAddress().getAddressLine2() == "1st Street"
			ordersResults.getOrder().get(0).getBillingAddress().getCity() == "Jercy"
			ordersResults.getOrder().get(0).getBillingAddress().getZipCode() == "70001"
			ordersResults.getOrder().get(0).getBillingAddress().getCountryCode() == "MP"
			ordersResults.getOrder().get(0).getBillingAddress().getState() == "UK"
			ordersResults.getOrder().get(0).getBillingAddress().getCompanyName() == "john Associates"
			ordersResults.getOrder().get(0).getBillingAddress().getEmailAddress() == "john@gmail.com"
			ordersResults.getOrder().get(0).getBillingAddress().getHomePhoneNumber() == "9898989898"
			ordersResults.getOrder().get(0).getBillingAddress().getWorkPhoneNumber() == "8989898989"
			
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getAtgShippingGroupId() == "hg123456"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingMethod() == "SDD"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getRegistryId() == null
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getFirstName() == "John"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getMiddleName() == "Abraham"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getLastName() == "Kennedy"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getAddressLine1() == "Nagal Nagar"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getAddressLine2() == "1st Street"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getCity() == "Jercy"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getZipCode() == "70001"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getCountryCode() == "MP"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getCompanyName() == "john Associates"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getEmailAddress() == "johnpaul@gmail.com"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getHomePhoneNumber() == "9898989898"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getWorkPhoneNumber() == "8989898989"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().isTaxCalculationFailure() == null
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getGiftInfo().getMessage() == null
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getGiftInfo().getCharge() == 2.0
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getGiftInfo().getSkuId() == "SKU12345"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingRawAmount() == 0.0
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingAmount() == 0.0
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getSurcharge() == 0.0
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getFinalSurcharge() == 20.0
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(1).getShipFeeOverride() == 22.99
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(1).getShippingFeeOverrideReasonCode() == "Heavy Item"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(1).getItemSurchargeOverride() == 12.99
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(1).getItemSurchargeOverrideReasonCode() == "Heavy ship Item"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(0).getDiscountAmount() == 10
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(0).getAdjustmentType() == "adjustmentType"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(0).getAdjustmentDesc() == "adjustmentDesc"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(1).getDiscountAmount() == 24
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(1).getAtgPromotionId() == "PUBpromo999999"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(1).getAdjustmentDesc() == "Override"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(1).getAdjustmentType() == "Override"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreId() == "sdd12345"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getStoreDescription() == "Fine Tabletop"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getStoreName() == "Harvey"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getAddress() == "Tremont Street"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getCity() == "Boston"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getState() == "Massachusetts"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getZipCode() == "02120"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getCountry() == "US"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getSatStoreTimings() == "9:00am - 9:30pm"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getSunStoreTimings() == "9:00am - 7:00pm"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getWeekdaysStoreTimings() == "9:00am - 9:30pm"
			ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getStorePhoneNumber() == "5044546930"
			
	}
	
	def "transformOrder. This TC is when orderType is ONLINE in OrderInfo object populated in populateOrderInfo"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 0
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			1 * commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> null
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >> null
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "A_BFREE"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getOrderInfo().getAtgOrderId() == "BBB12345"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderNumber() == "XXX"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderType() == OrderTypes.DELIVERY
		   ordersResults.getOrder().get(0).getOrderInfo().getTbsStoreNo() == "23456"
		   ordersResults.getOrder().get(0).getOrderInfo().getCartCreate() == "Origin"
		   ordersResults.getOrder().get(0).getOrderInfo().getCartFinal() == "A_BFREE"
		   ordersResults.getOrder().get(0).getOrderInfo().getSalesOS() == "salesOS"
		   ordersResults.getOrder().get(0).getOrderInfo().getAtgProfileId() == "p12345"
		   ordersResults.getOrder().get(0).getOrderInfo().getIpAddress() == "128.168.1.256"
		   ordersResults.getOrder().get(0).getOrderInfo().getAffiliate() == "affiliateId"
		   ordersResults.getOrder().get(0).getOrderInfo().getMbrNum() == null
		   ordersResults.getOrder().get(0).getOrderInfo().getSiteId() == "BedBathUS"
		   ordersResults.getOrder().get(0).getOrderInfo().getCurrencyCode() == null
		   ordersResults.getOrder().get(0).getOrderInfo().getSchoolPromo().getSchoolID() == "sch123456"
		   ordersResults.getOrder().get(0).getOrderInfo().getSchoolPromo().getPromoCode() == "SCHCOUPON"
		   ordersResults.getOrder().get(0).getOrderInfo().getTaxExemptId() == "true"
		   ordersResults.getOrder().get(0).getOrderInfo().isEmailSignUp() == TRUE
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getDeviceFingerprint() == "fingerPrint"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getApproverId() == "TBS_LEAD"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getStoreAssociateId() == "TBSAssociateId"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getOrderIdentifier() == null
	}
	
	def "transformOrder. This TC is when DatatypeConfigurationException thrown in getXMLCalendar private method"(){
		given:
			this.spyForTransforOrder()
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 0
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			1 * commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> null
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			testObj.newXMLCalender(*_) >> {throw new DatatypeConfigurationException("Mock for DatatypeConfigurationException")}
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults == null
		   thrown BBBSystemException
	}
	
	def "transformOrder. This TC is when orderType is BOPUS in OrderInfo object populated in populateOrderInfo Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> "bopus"
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 0
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			1 * commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLP"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> null
			bbbOrderImplMock.getPriceInfo() >> null
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbStoreShippingGroupMock]
			bbbStoreShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> TRUE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> FALSE
			tBSShippingInfoMock.getTaxExemptId() >> "TaxExemptId"
			bbbOrderImplMock.getSiteId() >> "TBS_BuyBuyBaby"
			
			//getMemberID Private Method Coverage
			userSiteItems.put("TBS_BedBathUS", userSiteItemMock)
			repositoryItemMock.getPropertyValue("userSiteItems") >> userSiteItems
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getOrderInfo().getAtgOrderId() == "BBB12345"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderNumber() == "OLP"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderType() == OrderTypes.BOPUS
		   ordersResults.getOrder().get(0).getOrderInfo().getTbsStoreNo() == "23456"
		   ordersResults.getOrder().get(0).getOrderInfo().getCartCreate() == "Origin"
		   ordersResults.getOrder().get(0).getOrderInfo().getCartFinal() == "Schannel"
		   ordersResults.getOrder().get(0).getOrderInfo().getSalesOS() == "salesOS"
		   ordersResults.getOrder().get(0).getOrderInfo().getAtgProfileId() == "p12345"
		   ordersResults.getOrder().get(0).getOrderInfo().getIpAddress() == "128.168.1.256"
		   ordersResults.getOrder().get(0).getOrderInfo().getAffiliate() == "affiliateId"
		   ordersResults.getOrder().get(0).getOrderInfo().getMbrNum() == null
		   ordersResults.getOrder().get(0).getOrderInfo().getSiteId() == "TBSBedBathBaby"
		   ordersResults.getOrder().get(0).getOrderInfo().getCurrencyCode() == null
		   ordersResults.getOrder().get(0).getOrderInfo().getSchoolPromo().getSchoolID() == "sch123456"
		   ordersResults.getOrder().get(0).getOrderInfo().getSchoolPromo().getPromoCode() == null
		   ordersResults.getOrder().get(0).getOrderInfo().getTaxExemptId() == "TaxExemptId"
		   ordersResults.getOrder().get(0).getOrderInfo().isEmailSignUp() == FALSE
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getDeviceFingerprint() == "fingerPrint"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getApproverId() == "TBS_LEAD"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getStoreAssociateId() == "TBSAssociateId"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getOrderIdentifier() == "TBS"
		   
	}
	
	def "transformOrder. This TC is when RepositoryException thrown in populateOrderInfo"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> "bopus"
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 0
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> {throw new RepositoryException("Mock for RepositoryException")}
						
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults == null
		   thrown BBBSystemException
	}
	
	def "transformOrder. This TC is when OrderType is Hybrid in populateOrderInfo Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> "bopus"
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 0
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			2 * commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLP"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> null
			bbbOrderImplMock.getPriceInfo() >> null
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbStoreShippingGroupMock]
			bbbStoreShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> TRUE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> FALSE
			tBSShippingInfoMock.getTaxExemptId() >> "TaxExemptId"
			bbbOrderImplMock.getSiteId() >> "TBS_BedBathCanada"
			
			//getMemberID Private Method Coverage
			repositoryItemMock.getPropertyValue("userSiteItems") >>> [userSiteItems,null]
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> ""
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getOrderInfo().getAtgOrderId() == "BBB12345"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderNumber() == "XXX"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderType() == OrderTypes.DELIVERY
		   ordersResults.getOrder().get(0).getOrderInfo().getTbsStoreNo() == "23456"
		   ordersResults.getOrder().get(0).getOrderInfo().getCartCreate() == "Origin"
		   ordersResults.getOrder().get(0).getOrderInfo().getCartFinal() == "Schannel"
		   ordersResults.getOrder().get(0).getOrderInfo().getSalesOS() == "salesOS"
		   ordersResults.getOrder().get(0).getOrderInfo().getAtgProfileId() == "p12345"
		   ordersResults.getOrder().get(0).getOrderInfo().getIpAddress() == "128.168.1.256"
		   ordersResults.getOrder().get(0).getOrderInfo().getAffiliate() == "affiliateId"
		   ordersResults.getOrder().get(0).getOrderInfo().getMbrNum() == null
		   ordersResults.getOrder().get(0).getOrderInfo().getSiteId() == "TBSBedBathCanada"
		   ordersResults.getOrder().get(0).getOrderInfo().getCurrencyCode() == null
		   ordersResults.getOrder().get(0).getOrderInfo().getSchoolPromo().getSchoolID() == ""
		   ordersResults.getOrder().get(0).getOrderInfo().getSchoolPromo().getPromoCode() == "SCHCOUPON"
		   ordersResults.getOrder().get(0).getOrderInfo().getTaxExemptId() == "TaxExemptId"
		   ordersResults.getOrder().get(0).getOrderInfo().isEmailSignUp() == FALSE
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getDeviceFingerprint() == "fingerPrint"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getApproverId() == "TBS_LEAD"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getStoreAssociateId() == "TBSAssociateId"
		   ordersResults.getOrder().get(0).getOrderInfo().getOrderInfoAttr().getOrderIdentifier() == "TBS"
		   ordersResults.getOrder().get(1).getOrderInfo().getAtgOrderId() == "BBB12345"
		   ordersResults.getOrder().get(1).getOrderInfo().getOrderNumber() == "OLP"
		   ordersResults.getOrder().get(1).getOrderInfo().getOrderType() == OrderTypes.BOPUS
		   ordersResults.getOrder().get(1).getOrderInfo().getSchoolPromo() == null
	}
	
	def "transformOrder. This TC is when populate orderPriceInfo object in populatePriceInfo Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock,null]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 0
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> null
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,bbbOrderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "M_BFREE"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populatePriceInfo Private Method Coverage
			bbbOrderPriceInfoMock.getTotal() >> 55.99d
			pricingToolsMock.round(55.99,2) >> 56
			bbbHardGoodShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			testObj.isInternationalSurchargeOn() >> FALSE
			bbbHardGoodShippingGroupMock.getCommerceItemRelationships() >> [bbbShippingGroupCommerceItemRelationshipMock]
			bbbShippingGroupCommerceItemRelationshipMock.getCommerceItem() >> null
			ltlDeliveryChargeCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getAmount() >> 2.2d
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getOrderPriceInfo().getOrderTotal() == 56
		   ordersResults.getOrder().get(0).getOrderPriceInfo().getTotalShipping() == null
		   ordersResults.getOrder().get(0).getOrderPriceInfo().getTotalSurcharge() == null
		   ordersResults.getOrder().get(0).getOrderPriceInfo().getTotalGiftWrapCost() == null
		   ordersResults.getOrder().get(0).getOrderPriceInfo().getTotalDeliveryCharge() == null
	}
	
	def "transformOrder. This TC is when orderType is HYBRID populated in populatePriceInfo Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >>> [[bbbCommerceItemMock,tbsCommerceItemMock],[bbbCommerceItemMock],null,[bbbCommerceItemMock]]
			bbbCommerceItemMock.getStoreId() >> "bopus"
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 0
			bbbOrderImplMock.getPaymentGroupRelationships() >> null
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> null
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLP"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			BBBOrderPriceInfo bbbOrderPriceInfoMock = new BBBOrderPriceInfo()
			bbbOrderImplMock.getPriceInfo() >>> [null,bbbOrderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populatePriceInfo Private Method Coverage
			bbbShippingPriceInfoMock.getFinalSurcharge() >> 20d
			pricingToolsMock.round(20.0,2) >> 20
			bbbHardGoodShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			testObj.isInternationalSurchargeOn() >> FALSE
			bbbHardGoodShippingGroupMock.getCommerceItemRelationships() >> [bbbShippingGroupCommerceItemRelationshipMock]
			bbbShippingGroupCommerceItemRelationshipMock.getCommerceItem() >> null
			ltlDeliveryChargeCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getAmount() >> 2.2d
			bbbOrderPriceInfoMock.setStoreSubtotal(12.99)
			pricingToolsMock.round(12.99,2) >> 13
			
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getOrderPriceInfo().getOrderTotal() == 0.0
		   ordersResults.getOrder().get(0).getOrderPriceInfo().getTotalShipping() == null
		   ordersResults.getOrder().get(0).getOrderPriceInfo().getTotalSurcharge() == 20
		   ordersResults.getOrder().get(0).getOrderPriceInfo().getTotalGiftWrapCost() == null
		   ordersResults.getOrder().get(0).getOrderPriceInfo().getTotalDeliveryCharge() == null
		   ordersResults.getOrder().get(1).getOrderPriceInfo().getOrderTotal() == 13
	}
	
	def "transformOrder. This TC is when billingAddress values are empty in populateAddress Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbOrderImplMock.getShippingGroupCount() >> 0
			bbbOrderImplMock.getPaymentGroupRelationships() >> []
			bbbOrderImplMock.getPaymentGroupRelationshipCount() >> 0
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateAddress Private Method Coverage
			testObj.setTerritories(territories)
			bbbRepositoryContactInfoMock.getFirstName() >> ""
			bbbRepositoryContactInfoMock.getMiddleName() >> ""
			bbbRepositoryContactInfoMock.getLastName() >> ""
			bbbRepositoryContactInfoMock.getAddress1() >> ""
			bbbRepositoryContactInfoMock.getAddress2() >> ""
			bbbRepositoryContactInfoMock.getCity() >> ""
			bbbRepositoryContactInfoMock.getPostalCode() >> ""
			bbbRepositoryContactInfoMock.getCountry() >> ""
			bbbRepositoryContactInfoMock.getState() >> ""
			bbbRepositoryContactInfoMock.getCompanyName() >> ""
			bbbRepositoryContactInfoMock.getEmail() >> ""
			bbbRepositoryContactInfoMock.getPhoneNumber() >> ""
			bbbRepositoryContactInfoMock.getMobileNumber() >> "9898989898"
			bbbRepositoryContactInfoMock.getAlternatePhoneNumber() >> ""
			
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getBillingAddress().getFirstName() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getMiddleName() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getLastName() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getAddressLine1() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getAddressLine2() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getCity() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getZipCode() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getCountryCode() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getState() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getCompanyName() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getEmailAddress() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getHomePhoneNumber() == "9898989898"
		   ordersResults.getOrder().get(0).getBillingAddress().getWorkPhoneNumber() == null
		 
	}
	
	def "transformOrder. This TC is when state is not in territories of billingAddress in populateAddress Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbOrderImplMock.getShippingGroupCount() >> 0
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateAddress Private Method Coverage
			testObj.setTerritories(territories)
			bbbRepositoryContactInfoMock.getCountry() >> "MX"
			bbbRepositoryContactInfoMock.getState() >> "NJ"
			bbbRepositoryContactInfoMock.getAlternatePhoneNumber() >> ""
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getBillingAddress().getCountryCode() == "MX"
		   ordersResults.getOrder().get(0).getBillingAddress().getState() == "GR"
		   ordersResults.getOrder().get(0).getBillingAddress().getHomePhoneNumber() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getWorkPhoneNumber() == null
	}
	
	def "transformOrder. This TC is when country is PR in populateAddress Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null 
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbOrderImplMock.getShippingGroupCount() >> 0
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "D_BFREE"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateAddress Private Method Coverage
			testObj.setTerritories(territories)
			bbbRepositoryContactInfoMock.getCountry() >> "PR"
			bbbRepositoryContactInfoMock.getState() >> "NJ"
			bbbRepositoryContactInfoMock.getAlternatePhoneNumber() >> ""
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getBillingAddress().getCountryCode() == "PR"
		   ordersResults.getOrder().get(0).getBillingAddress().getState() == "PR"
		   ordersResults.getOrder().get(0).getBillingAddress().getHomePhoneNumber() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getWorkPhoneNumber() == null
	}
	
	def "transformOrder. This TC is when country is NJ in populateAddress Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbOrderImplMock.getShippingGroupCount() >> 0
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "A_BFREE"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateAddress Private Method Coverage
			testObj.setTerritories(territories)
			bbbRepositoryContactInfoMock.getCountry() >> "NJ"
			bbbRepositoryContactInfoMock.getState() >> "NJ"
			bbbRepositoryContactInfoMock.getAlternatePhoneNumber() >> ""
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getBillingAddress().getCountryCode() == "NJ"
		   ordersResults.getOrder().get(0).getBillingAddress().getState() == "NJ"
		   ordersResults.getOrder().get(0).getBillingAddress().getHomePhoneNumber() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getWorkPhoneNumber() == null
	}
	
	def "transformOrder. This TC is when country is blank in populateAddress Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> bbbRepositoryContactInfoMock
			bbbOrderImplMock.getShippingGroupCount() >> 0
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "M_BFREE"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateAddress Private Method Coverage
			testObj.setTerritories(territories)
			bbbRepositoryContactInfoMock.getCountry() >> ""
			bbbRepositoryContactInfoMock.getState() >> "NJ"
			bbbRepositoryContactInfoMock.getAlternatePhoneNumber() >> ""
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getBillingAddress().getCountryCode() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getState() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getHomePhoneNumber() == null
		   ordersResults.getOrder().get(0).getBillingAddress().getWorkPhoneNumber() == null
	}
	
	def "transformOrder. This TC is when shipping details entered in populateShipping Private Method"(){
		given:
			this.spyForTransforOrder()
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 2
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock,orderPriceInfoMock,bbbOrderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >>> [tBSShippingInfoMock,null]
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateShipping Private method Coverage
			HashMap subtotalPriceInfosMock = ["hg123456": null]
			bbbOrderPriceInfoMock.getShippingItemsSubtotalPriceInfos() >> subtotalPriceInfosMock
			tbsCommerceItemMock.isCMO() >> FALSE
			bbbHardGoodShippingGroupMock.getId() >> "hg123456"
			bbbHardGoodShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getShipOnDate() >> null
			bbbHardGoodShippingGroupMock.getRegistryId() >> "R123456"
			bbbHardGoodShippingGroupMock.getSpecialInstructions() >> null
			bbbHardGoodShippingGroupMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbShippingPriceInfoMock.getRawShipping() >> 21.99d
			pricingToolsMock.round(21.99,2) >> 22
			bbbShippingPriceInfoMock.getFinalShipping() >> 25.99d
			pricingToolsMock.round(25.99,2) >> 26
			bbbShippingPriceInfoMock.getSurcharge() >> 12.99d
			bbbShippingPriceInfoMock.getFinalSurcharge() >> 20.99d
			testObj.isInternationalSurchargeOn() >> FALSE
			bbbHardGoodShippingGroupMock.getCommerceItemRelationships() >> [bbbShippingGroupCommerceItemRelationshipMock]
			tBSShippingInfoMock.isShipPriceOverride() >> TRUE
			bbbShippingGroupCommerceItemRelationshipMock.getCommerceItem() >>> [bbbCommerceItemMock,tbsCommerceItemMock]
			bbbCommerceItemMock.isLtlItem() >> FALSE
			bbbHardGoodShippingGroupMock.getPropertyValue("sddStoreId") >> "sdd12345"
			orderPriceInfoMock.getAmount() >> 50.99d
			HashMap shippingItemTaxPriceMock = ["hg123456": null]
			taxPriceInfoMock.getShippingItemsTaxPriceInfos() >> shippingItemTaxPriceMock
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd123145"
			pricingToolsMock.round(26.0,2) >> 27
			Set<TrackingInfo> trackingInfosMock = new HashSet<TrackingInfo>() 
			trackingInfosMock.add(trackingInfoMock)
			TrackingInfo trackingInfoMock1 = Mock()
			trackingInfosMock.add(trackingInfoMock1)
			bbbHardGoodShippingGroupMock.getTrackingInfos() >> trackingInfosMock
			trackingInfoMock.getTrackingNumber() >> "123456"
			
			//isTaxCalculationFailure method bcoz it is final method
			HashMap orderSpecialInstructions = ["CYBERSOURCE_TAX_FAILURE":"true"]
			bbbOrderImplMock.getSpecialInstructions() >> orderSpecialInstructions
			
			//populateGiftInfo Private Method Coverage
			bbbHardGoodShippingGroupMock.containsGiftWrap() >> FALSE
			bbbHardGoodShippingGroupMock.getGiftWrapMessage() >> ""
			
			//getGiftMessage Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_GIFT_MESSAGE) >> ["Happy Birthday"]
			
			//populateStoreInfoType Private Method Coverage
			searchStoreManagerMock.getStoreType("BedBathUS") >> "Online Store"
			searchStoreManagerMock.searchStoreById("sdd12345",bbbOrderImplMock.getSiteId(), "Online Store") >> null
			
			//populateItems Private Method Coverage
			ItemPriceInfo itemPriceInfoMock1 = Mock()
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			1 * itemPriceInfoMock.getCurrentPriceDetailsForRange(_) >> [bbbDetailedItemPriceInfoMock]
			1 * pricingToolsMock.calculateItemSurchargeInSG(bbbOrderImplMock.getSiteId(), bbbShippingGroupCommerceItemRelationshipMock) >> 87.99d
			bbbDetailedItemPriceInfoMock.getQuantity() >> 2
			bbbDetailedItemPriceInfoMock.getAmount() >> 80.99d
			pricingToolsMock.round(_) >> 80.99d
			tbsCommerceItemMock.getDeliveryItemId() >> "de12345"
			tbsCommerceItemMock.getAssemblyItemId() >> ""
			tbsCommerceItemMock.getCatalogRefId() >> "SKU123456"
			1 * catalogToolsMock.getSKUDetails(bbbOrderImplMock.getSiteId(), tbsCommerceItemMock.getCatalogRefId(), false, true, true) >> skuDetailVOMock
			bbbOrderImplMock.getCommerceItem("de12345") >> nonMerchandiseCommerceItemMock
			nonMerchandiseCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			tbsCommerceItemMock.getQuantity() >> 2
			ItemType itemTypeMock = new ItemType() 
			testObj.createItemInfo(*_) >> itemTypeMock
			
			//setTBSProperties Private Method Coverage
			tbsCommerceItemMock.getShipTime() >> "10'0 clock"
			tbsCommerceItemMock.getTBSItemInfo() >> tbsItemInfoMock
			tbsItemInfoMock.getConfigId() >> "tbsConfigId"
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			skuAttrRelationMock.add(skuAttrRepositoryItemMock)
			skuAttrRelationMock.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION) >> skuAttrRelationMock
			skuAttrRepositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE) >> skuAttributeMock
			skuAttributeMock.getRepositoryId() >> "12_2"
			
			//populateAddress Private Method Coverage
			testObj.setTerritories(territories)
			bbbRepositoryContactInfoMock.getFirstName() >> "John"
			bbbRepositoryContactInfoMock.getMiddleName() >> "Abraham"
			bbbRepositoryContactInfoMock.getLastName() >> "Kennedy"
			bbbRepositoryContactInfoMock.getAddress1() >> "Nagal Nagar"
			bbbRepositoryContactInfoMock.getAddress2() >> "1st Street"
			bbbRepositoryContactInfoMock.getCity() >> "Jercy"
			bbbRepositoryContactInfoMock.getPostalCode() >> "70001"
			bbbRepositoryContactInfoMock.getCountry() >> "GB"
			bbbRepositoryContactInfoMock.getState() >> "MP"
			bbbRepositoryContactInfoMock.getCompanyName() >> "john Associates"
			bbbRepositoryContactInfoMock.getEmail() >> "john@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			bbbRepositoryContactInfoMock.getAlternatePhoneNumber() >> "8989898989"
			
			//populateAdjustmentList Private method Coverage
			PricingAdjustment pricingAdjustmentMock1 = Mock()
			bbbShippingPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock,pricingAdjustmentMock1]
			pricingAdjustmentMock.getPricingModel() >> adjustmentPricingModel
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Override"
			pricingAdjustmentMock1.getPricingModel() >> null
			pricingAdjustmentMock1.getAdjustmentDescription() >> "TBS Price Override"
			adjustmentPricingModel.getRepositoryId() >> "pa12345"
			pricingAdjustmentMock.getAdjustment() >> 10d
			pricingToolsMock.round(10.0,2) >> 10
			adjustmentPricingModel.getPropertyValue("typeDetail") >> "adjustmentType"
			adjustmentPricingModel.getPropertyValue("displayName") >> "adjustmentDesc"
			pricingAdjustmentMock1.getAdjustment() >> 23.2d
			pricingToolsMock.round(23.2,2) >> 24
			tBSShippingInfoMock.getShipPriceValue() >> 22.99d
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getAtgShippingGroupId() == "hg123456"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingMethod() == "SDD"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getFirstName() == "John"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getMiddleName() == "Abraham"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getLastName() == "Kennedy"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getAddressLine1() == "Nagal Nagar"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getAddressLine2() == "1st Street"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getCity() == "Jercy"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getZipCode() == "70001"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getCountryCode() == "MP"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getCompanyName() == "john Associates"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getEmailAddress() == "john@gmail.com"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getHomePhoneNumber() == "9898989898"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getWorkPhoneNumber() == "8989898989"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().isTaxCalculationFailure() == TRUE
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getGiftInfo().getMessage() == "Happy Birthday"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingRawAmount() == 22.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingAmount() == 26.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getSurcharge() == 0.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getFinalSurcharge() == 0.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(0).getDiscountAmount() == 10
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(0).getAdjustmentType() == "adjustmentType"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(0).getAdjustmentDesc() == "adjustmentDesc"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(1).getDiscountAmount() == 24
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(1).getAtgPromotionId() == "PUBpromo999999"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(1).getAdjustmentDesc() == "Override"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList().getAdjustment().get(1).getAdjustmentType() == "Override"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreId() == "sdd123145"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getStoreDescription() == "Error in getting details from MapQuest"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getStoreName() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getAddress() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getCity() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getState() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getZipCode() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getCountry() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getItemList().getItem().get(0).getPresentedLeadTime() == "10'0 clock"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getItemList().getItem().get(0).getConfigId() == "tbsConfigId"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getItemList().getItem().get(0).getExternalCart() == "KIRSCH"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getItemList().getItem().get(1).getPresentedLeadTime() == "10'0 clock"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getItemList().getItem().get(1).getConfigId() == "tbsConfigId"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getItemList().getItem().get(1).getExternalCart() == "KIRSCH"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getSubTotal() == 27
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getTrackingNumber() == "123456"

	}
	
	def "transformOrder. This TC is when getGiftWrapMessage has values and getShippingMethod is not SDD"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 2
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock,orderPriceInfoMock,bbbOrderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateShipping Private method Coverage
			HashMap subtotalPriceInfosMock = ["hg123456": null]
			bbbOrderPriceInfoMock.getShippingItemsSubtotalPriceInfos() >> subtotalPriceInfosMock
			tbsCommerceItemMock.isCMO() >> FALSE
			bbbHardGoodShippingGroupMock.getId() >> "hg123456"
			bbbHardGoodShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDDTYPE"
			bbbHardGoodShippingGroupMock.getShipOnDate() >> null
			bbbHardGoodShippingGroupMock.getRegistryId() >> ""
			HashMap shippingSpecialInstructions = ["IS_COMFIRMATION_ASKED":"true","CONFIRMATION_EMAIL_ID":""]
			bbbHardGoodShippingGroupMock.getSpecialInstructions() >> shippingSpecialInstructions
			bbbHardGoodShippingGroupMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbShippingPriceInfoMock.getRawShipping() >> 21.99d
			pricingToolsMock.round(21.99,2) >> 22
			bbbShippingPriceInfoMock.getFinalShipping() >> 25.99d
			pricingToolsMock.round(25.99,2) >> 26
			bbbShippingPriceInfoMock.getSurcharge() >> 12.99d
			bbbShippingPriceInfoMock.getFinalSurcharge() >> 20.99d
			testObj.isInternationalSurchargeOn() >> FALSE
			bbbHardGoodShippingGroupMock.getCommerceItemRelationships() >> [bbbShippingGroupCommerceItemRelationshipMock]
			tBSShippingInfoMock.isShipPriceOverride() >> TRUE
			bbbShippingGroupCommerceItemRelationshipMock.getCommerceItem() >>> [bbbCommerceItemMock,tbsCommerceItemMock]
			bbbCommerceItemMock.isLtlItem() >> FALSE
			bbbHardGoodShippingGroupMock.getPropertyValue("sddStoreId") >> "sdd12345"
			orderPriceInfoMock.getAmount() >> 50.99d
			HashMap shippingItemTaxPriceMock = ["hg123456": null]
			taxPriceInfoMock.getShippingItemsTaxPriceInfos() >> shippingItemTaxPriceMock
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd123145"
			pricingToolsMock.round(26.0,2) >> 27
			bbbShippingPriceInfoMock.getAdjustments() >> []
			
			//isTaxCalculationFailure method bcoz it is final method
			HashMap orderSpecialInstructions = ["CYBERSOURCE_TAX_FAILURE":"true"]
			bbbOrderImplMock.getSpecialInstructions() >> orderSpecialInstructions
			
			//populateGiftInfo Private Method Coverage
			bbbHardGoodShippingGroupMock.containsGiftWrap() >> FALSE
			bbbHardGoodShippingGroupMock.getGiftWrapMessage() >> "Happy BirthDay"
			
			//populateItems Private Method Coverage
			ItemPriceInfo itemPriceInfoMock1 = Mock()
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getCurrentPriceDetailsForRange(_) >> []
				
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getGiftInfo().getMessage() == "Happy BirthDay"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingMethod() == "SDDTYPE"
	}
	
	def "transformOrder. This TC is when giftMessageList is empty in getGiftMessage private method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 2
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock,orderPriceInfoMock,bbbOrderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "D_BFREE"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateShipping Private method Coverage
			HashMap subtotalPriceInfosMock = ["hg123456": null]
			bbbOrderPriceInfoMock.getShippingItemsSubtotalPriceInfos() >> subtotalPriceInfosMock
			tbsCommerceItemMock.isCMO() >> FALSE
			bbbHardGoodShippingGroupMock.getId() >> "hg123456"
			bbbHardGoodShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDDTYPE"
			bbbHardGoodShippingGroupMock.getShipOnDate() >> null
			bbbHardGoodShippingGroupMock.getRegistryId() >> ""
			HashMap shippingSpecialInstructions = ["IS_COMFIRMATION_ASKED":"true","CONFIRMATION_EMAIL_ID":""]
			bbbHardGoodShippingGroupMock.getSpecialInstructions() >> shippingSpecialInstructions
			bbbHardGoodShippingGroupMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbShippingPriceInfoMock.getRawShipping() >> 21.99d
			pricingToolsMock.round(21.99,2) >> 22
			bbbShippingPriceInfoMock.getFinalShipping() >> 25.99d
			pricingToolsMock.round(25.99,2) >> 26
			bbbShippingPriceInfoMock.getSurcharge() >> 12.99d
			bbbShippingPriceInfoMock.getFinalSurcharge() >> 20.99d
			testObj.isInternationalSurchargeOn() >> FALSE
			bbbHardGoodShippingGroupMock.getCommerceItemRelationships() >> [bbbShippingGroupCommerceItemRelationshipMock]
			tBSShippingInfoMock.isShipPriceOverride() >> TRUE
			bbbShippingGroupCommerceItemRelationshipMock.getCommerceItem() >>> [bbbCommerceItemMock,tbsCommerceItemMock]
			bbbCommerceItemMock.isLtlItem() >> FALSE
			bbbHardGoodShippingGroupMock.getPropertyValue("sddStoreId") >> "sdd12345"
			orderPriceInfoMock.getAmount() >> 50.99d
			HashMap shippingItemTaxPriceMock = ["hg123456": null]
			taxPriceInfoMock.getShippingItemsTaxPriceInfos() >> shippingItemTaxPriceMock
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd123145"
			pricingToolsMock.round(26.0,2) >> 27
			bbbShippingPriceInfoMock.getAdjustments() >> []
			
			//isTaxCalculationFailure method bcoz it is final method
			HashMap orderSpecialInstructions = ["CYBERSOURCE_TAX_FAILURE":"true"]
			bbbOrderImplMock.getSpecialInstructions() >> orderSpecialInstructions
			
			//populateGiftInfo Private Method Coverage
			bbbHardGoodShippingGroupMock.containsGiftWrap() >> FALSE
			bbbHardGoodShippingGroupMock.getGiftWrapMessage() >> ""
			
			//getGiftMessage Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_GIFT_MESSAGE) >> []
			
			//populateItems Private Method Coverage
			ItemPriceInfo itemPriceInfoMock1 = Mock()
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getCurrentPriceDetailsForRange(_) >> []
				
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getGiftInfo().getMessage() == null
	}
	
	def "transformOrder. This TC is when DatatypeConfigurationException thrown in getXMLCalendarShipOnDate Private method"(){
		given:
			this.spyForTransforOrder()
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 2
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock,orderPriceInfoMock,bbbOrderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateShipping Private method Coverage
			HashMap subtotalPriceInfosMock = ["hg123456": null]
			bbbOrderPriceInfoMock.getShippingItemsSubtotalPriceInfos() >> subtotalPriceInfosMock
			tbsCommerceItemMock.isCMO() >> FALSE
			bbbHardGoodShippingGroupMock.getId() >> "hg123456"
			bbbHardGoodShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDDTYPE"
			bbbHardGoodShippingGroupMock.getShipOnDate() >> new Date()
			testObj.newXMLCalenderShipOnDate(*_) >> {throw new DatatypeConfigurationException("")}
			
			//isTaxCalculationFailure method bcoz it is final method
			HashMap orderSpecialInstructions = ["CYBERSOURCE_TAX_FAILURE":"true"]
			bbbOrderImplMock.getSpecialInstructions() >> orderSpecialInstructions
				
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
	   		thrown BBBSystemException
		    ordersResults == null
		
	}
	
	def "transformOrder. This TC is when getSddStoreId is null"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 2
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock,orderPriceInfoMock,bbbOrderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >>> [tBSShippingInfoMock,null]
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateShipping Private method Coverage
			HashMap subtotalPriceInfosMock = ["hg123456": null]
			bbbOrderPriceInfoMock.getShippingItemsSubtotalPriceInfos() >> subtotalPriceInfosMock
			tbsCommerceItemMock.isCMO() >> FALSE
			bbbHardGoodShippingGroupMock.getId() >> "hg123456"
			bbbHardGoodShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getShipOnDate() >> null
			bbbHardGoodShippingGroupMock.getRegistryId() >> "R123456"
			bbbHardGoodShippingGroupMock.getSpecialInstructions() >> null
			bbbHardGoodShippingGroupMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbShippingPriceInfoMock.getRawShipping() >> 21.99d
			pricingToolsMock.round(21.99,2) >> 22
			bbbShippingPriceInfoMock.getFinalShipping() >> 25.99d
			pricingToolsMock.round(25.99,2) >> 26
			bbbShippingPriceInfoMock.getSurcharge() >> 12.99d
			bbbShippingPriceInfoMock.getFinalSurcharge() >> 20.99d
			testObj.isInternationalSurchargeOn() >> FALSE
			bbbHardGoodShippingGroupMock.getCommerceItemRelationships() >> [bbbShippingGroupCommerceItemRelationshipMock]
			tBSShippingInfoMock.isShipPriceOverride() >> TRUE
			bbbShippingGroupCommerceItemRelationshipMock.getCommerceItem() >>> [bbbCommerceItemMock,tbsCommerceItemMock]
			bbbCommerceItemMock.isLtlItem() >> FALSE
			bbbHardGoodShippingGroupMock.getPropertyValue("sddStoreId") >> "sdd12345"
			orderPriceInfoMock.getAmount() >> 50.99d
			HashMap shippingItemTaxPriceMock = ["hg123456": null]
			taxPriceInfoMock.getShippingItemsTaxPriceInfos() >> shippingItemTaxPriceMock
			bbbHardGoodShippingGroupMock.getSddStoreId() >> null
			pricingToolsMock.round(26.0,2) >> 27
			Set<TrackingInfo> trackingInfosMock = new HashSet<TrackingInfo>()
			trackingInfosMock.add(trackingInfoMock)
			TrackingInfo trackingInfoMock1 = Mock()
			trackingInfosMock.add(null)
			bbbHardGoodShippingGroupMock.getTrackingInfos() >> trackingInfosMock
			trackingInfoMock.getTrackingNumber() >> null
			
			//isTaxCalculationFailure method bcoz it is final method
			HashMap orderSpecialInstructions = ["CYBERSOURCE_TAX_FAILURE":"true"]
			bbbOrderImplMock.getSpecialInstructions() >> orderSpecialInstructions
			
			//populateGiftInfo Private Method Coverage
			bbbHardGoodShippingGroupMock.containsGiftWrap() >> FALSE
			bbbHardGoodShippingGroupMock.getGiftWrapMessage() >> ""
			
			//getGiftMessage Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_GIFT_MESSAGE) >> ["Happy Birthday"]
		
			//populateItems Private Method Coverage
			ItemPriceInfo itemPriceInfoMock1 = Mock()
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getCurrentPriceDetailsForRange(_) >> []
			bbbShippingPriceInfoMock.getAdjustments() >> []
			
			//setTBSProperties Private Method Coverage
			tbsCommerceItemMock.getShipTime() >> "10'0 clock"
			tbsCommerceItemMock.getTBSItemInfo() >> tbsItemInfoMock
			tbsItemInfoMock.getConfigId() >> "tbsConfigId"
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			skuAttrRelationMock.add(skuAttrRepositoryItemMock)
			skuAttrRelationMock.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION) >> skuAttrRelationMock
			skuAttrRepositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE) >> skuAttributeMock
			skuAttributeMock.getRepositoryId() >> "12_2"
				
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo() == null
	}
	
	def "transformOrder. This TC is when getExternalCart is CMO in setTBSProperties Private Method"(){
		given:
			this.spyForTransforOrder()
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 2
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock,orderPriceInfoMock,bbbOrderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			bbbHardGoodShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateShipping Private method Coverage
			HashMap subtotalPriceInfosMock = ["hg123456": null]
			bbbOrderPriceInfoMock.getShippingItemsSubtotalPriceInfos() >> subtotalPriceInfosMock
			tbsCommerceItemMock.isCMO() >> TRUE
			bbbHardGoodShippingGroupMock.getId() >> "hg123456"
			bbbHardGoodShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			bbbHardGoodShippingGroupMock.getShipOnDate() >> null
			bbbHardGoodShippingGroupMock.getRegistryId() >> "R123456"
			bbbHardGoodShippingGroupMock.getSpecialInstructions() >> null
			bbbHardGoodShippingGroupMock.getShippingAddress() >> bbbRepositoryContactInfoMock
			bbbShippingPriceInfoMock.getRawShipping() >> 21.99d
			pricingToolsMock.round(21.99,2) >> 22
			bbbShippingPriceInfoMock.getFinalShipping() >> 25.99d
			pricingToolsMock.round(25.99,2) >> 26
			bbbShippingPriceInfoMock.getSurcharge() >> 12.99d
			pricingToolsMock.round(12.99,2) >> 13
			bbbShippingPriceInfoMock.getFinalSurcharge() >> 20.99d
			pricingToolsMock.round(20.99,2) >> 21
			testObj.isInternationalSurchargeOn() >> FALSE
			BBBShippingGroupCommerceItemRelationship bbbShippingGroupCommerceItemRelationshipMock1 = Mock()
			bbbHardGoodShippingGroupMock.getCommerceItemRelationships() >> [bbbShippingGroupCommerceItemRelationshipMock,bbbShippingGroupCommerceItemRelationshipMock1]
			tBSShippingInfoMock.isShipPriceOverride() >> FALSE
			bbbShippingGroupCommerceItemRelationshipMock.getCommerceItem() >>> [bbbCommerceItemMock,tbsCommerceItemMock]
			bbbShippingGroupCommerceItemRelationshipMock1.getCommerceItem() >> tbsCommerceItemMock
			bbbCommerceItemMock.isLtlItem() >> FALSE
			bbbHardGoodShippingGroupMock.getPropertyValue("sddStoreId") >> "sdd12345"
			orderPriceInfoMock.getAmount() >> 50.99d
			HashMap shippingItemTaxPriceMock = ["hg123456": taxPriceInfoMock]
			taxPriceInfoMock.getShippingItemsTaxPriceInfos() >> shippingItemTaxPriceMock
			bbbHardGoodShippingGroupMock.getSddStoreId() >> "sdd123145"
			pricingToolsMock.round(26.0,2) >> 27
			Set<TrackingInfo> trackingInfosMock = new HashSet<TrackingInfo>()
			trackingInfosMock.add(trackingInfoMock)
			TrackingInfo trackingInfoMock1 = Mock()
			trackingInfosMock.add(trackingInfoMock1)
			bbbHardGoodShippingGroupMock.getTrackingInfos() >> trackingInfosMock
			trackingInfoMock.getTrackingNumber() >> null
			
			//isTaxCalculationFailure method bcoz it is final method
			HashMap orderSpecialInstructions = ["CYBERSOURCE_TAX_FAILURE":"true"]
			bbbOrderImplMock.getSpecialInstructions() >> orderSpecialInstructions
			
			//populateGiftInfo Private Method Coverage
			bbbHardGoodShippingGroupMock.containsGiftWrap() >> FALSE
			bbbHardGoodShippingGroupMock.getGiftWrapMessage() >> ""
			
			//getGiftMessage Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING, BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_GIFT_MESSAGE) >> ["Happy Birthday"]
			
			//populateTaxInfo Private Method Coverage
			taxPriceInfoMock.getCityTax() >> 3.2d
			pricingToolsMock.round(3.2,2) >> 3
			taxPriceInfoMock.getCountyTax() >> 4.2d
			pricingToolsMock.round(4.2,2) >> 4
			taxPriceInfoMock.getDistrictTax() >> 2.1d
			pricingToolsMock.round(2.1,2) >> 2
			taxPriceInfoMock.getStateTax() >> 1.1d
			pricingToolsMock.round(1.1,2) >> 1
			taxPriceInfoMock.getAmount() >> 5.2d
			pricingToolsMock.round(5.2,2) >> 5
			
			//populateStoreInfoType Private Method Coverage
			1 * searchStoreManagerMock.getStoreType("BedBathUS") >> "Online Store"
			1 * searchStoreManagerMock.searchStoreById("sdd123145",bbbOrderImplMock.getSiteId(), "Online Store") >> storeDetailsMock
			storeDetailsMock.getStoreDescription() >> ""
			storeDetailsMock.getStoreName() >> ""
			storeDetailsMock.getAddress() >> "Tremont Street"
			storeDetailsMock.getCity() >> "Boston"
			storeDetailsMock.getState() >> "Massachusetts"
			storeDetailsMock.getPostalCode() >> "02120"
			storeDetailsMock.getCountry() >> "US"
			storeDetailsMock.getSatStoreTimings() >> ""
			storeDetailsMock.getSunStoreTimings() >> ""
			storeDetailsMock.getWeekdaysStoreTimings() >> ""
			storeDetailsMock.getStorePhone() >> "5044546930"
			
			//populateItems Private Method Coverage
			ItemPriceInfo itemPriceInfoMock1 = Mock()
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getCurrentPriceDetailsForRange(_) >> [bbbDetailedItemPriceInfoMock]
			1 * pricingToolsMock.calculateItemSurchargeInSG(bbbOrderImplMock.getSiteId(), bbbShippingGroupCommerceItemRelationshipMock) >> 87.99d
			bbbDetailedItemPriceInfoMock.getQuantity() >> 2
			bbbDetailedItemPriceInfoMock.getAmount() >> 80.99d
			pricingToolsMock.round(_) >> 80.99d
			tbsCommerceItemMock.getDeliveryItemId() >> "de12345"
			tbsCommerceItemMock.getAssemblyItemId() >> ""
			tbsCommerceItemMock.getCatalogRefId() >> "SKU123456"
			2 * catalogToolsMock.getSKUDetails(bbbOrderImplMock.getSiteId(), tbsCommerceItemMock.getCatalogRefId(), false, true, true) >> skuDetailVOMock
			bbbOrderImplMock.getCommerceItem("de12345") >>> [nonMerchandiseCommerceItemMock, null]
			nonMerchandiseCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getAmount() >> 80.99d
			tbsCommerceItemMock.getQuantity() >> 1
			ItemType itemTypeMock = new ItemType()
			testObj.createItemInfo(*_) >>> [itemTypeMock, null]
			
			//setTBSProperties Private Method Coverage
			tbsCommerceItemMock.getShipTime() >> "12345"
			tbsCommerceItemMock.getTBSItemInfo() >> tbsItemInfoMock
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			RepositoryItem skuAttributeMock1 = Mock()
			skuAttrRelationMock.add(skuAttrRepositoryItemMock)
			skuAttrRelationMock.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION) >>> [skuAttrRelationMock,null]
			skuAttrRepositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE) >> skuAttributeMock
			skuAttributeMock.getRepositoryId() >> "12_3"
			
			//populateAddress Private Method Coverage
			testObj.setTerritories(territories)
			bbbRepositoryContactInfoMock.getFirstName() >> "John"
			bbbRepositoryContactInfoMock.getMiddleName() >> "Abraham"
			bbbRepositoryContactInfoMock.getLastName() >> "Kennedy"
			bbbRepositoryContactInfoMock.getAddress1() >> "Nagal Nagar"
			bbbRepositoryContactInfoMock.getAddress2() >> "1st Street"
			bbbRepositoryContactInfoMock.getCity() >> "Jercy"
			bbbRepositoryContactInfoMock.getPostalCode() >> "70001"
			bbbRepositoryContactInfoMock.getCountry() >> "GB"
			bbbRepositoryContactInfoMock.getState() >> "MP"
			bbbRepositoryContactInfoMock.getCompanyName() >> "john Associates"
			bbbRepositoryContactInfoMock.getEmail() >> "john@gmail.com"
			bbbRepositoryContactInfoMock.getPhoneNumber() >> "9898989898"
			bbbRepositoryContactInfoMock.getAlternatePhoneNumber() >> "8989898989"
			
			//populateAdjustmentList Private method Coverage
			PricingAdjustment pricingAdjustmentMock1 = Mock()
			bbbShippingPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock]
			pricingAdjustmentMock.getPricingModel() >> null
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Override"
			pricingAdjustmentMock.getAdjustment() >> 10d
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getAtgShippingGroupId() == "hg123456"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingMethod() == "SDD"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getRegistryId() == "R123456"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getFirstName() == "John"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getMiddleName() == "Abraham"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getLastName() == "Kennedy"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getAddressLine1() == "Nagal Nagar"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getAddressLine2() == "1st Street"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getCity() == "Jercy"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getZipCode() == "70001"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getCountryCode() == "MP"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getCompanyName() == "john Associates"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getEmailAddress() == "john@gmail.com"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getHomePhoneNumber() == "9898989898"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getWorkPhoneNumber() == "8989898989"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().isTaxCalculationFailure() == TRUE
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingRawAmount() == 22.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingAmount() == 26.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getSurcharge() == 13.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getFinalSurcharge() == 21.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getAdjustmentList() == null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreId() == "sdd123145"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getStoreDescription() == null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getStoreName() == null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getAddress() == "Tremont Street"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getCity() == "Boston"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getState() == "Massachusetts"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getZipCode() == "02120"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getCountry() == "US"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getSatStoreTimings() == null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getSunStoreTimings() == null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getWeekdaysStoreTimings() == null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getStorePhoneNumber() == "5044546930"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getItemList().getItem().get(0).getPresentedLeadTime() == "12345"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getItemList().getItem().get(0).getConfigId() == null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getItemList().getItem().get(0).getExternalCart() == "CMO"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getSubTotal() == 0.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getTrackingNumber() == null
	}
	
	def "transformOrder. This TC is when passing bbbStoreShippingGroup and TBS_BedBathUS in populateCMOShipInfo Private Method"(){
		given:
			this.spyForTransforOrder()
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 2
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock,orderPriceInfoMock,bbbOrderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbStoreShippingGroupMock]
			bbbStoreShippingGroupMock.getTbsShipInfo() >>> [tBSShippingInfoMock,null]
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "TBS_BedBathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateShipping Private method Coverage
			HashMap subtotalPriceInfosMock = ["hg123456": orderPriceInfoMock]
			bbbOrderPriceInfoMock.getShippingItemsSubtotalPriceInfos() >> subtotalPriceInfosMock
			tbsCommerceItemMock.isCMO() >> TRUE
			bbbStoreShippingGroupMock.getId() >> "hg123456"
			bbbStoreShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			
			//populateCMOShipInfo Private Method Coverage
			testObj.getShipMethod() >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "SDD"
			bbbStoreShippingGroupMock.getShipOnDate() >> DateUtils.addDays(new Date(), 10)
			bbbStoreShippingGroupMock.getStoreId() >> "storeId"
			1 * searchStoreManagerMock.searchStoreById(bbbStoreShippingGroupMock.getStoreId(), "BedBathUS" , "BedBathUS") >> storeDetailsMock 
			storeDetailsMock.getStoreName() >> "Harvey"
			storeDetailsMock.getAddress() >> "Tremont Street"
			storeDetailsMock.getCity() >> "Boston"
			storeDetailsMock.getState() >> "Massachusetts"
			storeDetailsMock.getPostalCode() >> "02120"
			storeDetailsMock.getCountry() >> "US"
			bbbShippingPriceInfoMock.getRawShipping() >> 21.99d
			pricingToolsMock.round(21.99,2) >> 22
			bbbShippingPriceInfoMock.getFinalShipping() >> 25.99d
			pricingToolsMock.round(25.99,2) >> 26
			HashMap shippingItemTaxPriceMock = ["hg123456": null]
			taxPriceInfoMock.getShippingItemsTaxPriceInfos() >> shippingItemTaxPriceMock
			orderPriceInfoMock.getAmount() >> 28.2d
			
			//isTaxCalculationFailure method bcoz it is final method
			HashMap orderSpecialInstructions = ["CYBERSOURCE_TAX_FAILURE":"true"]
			bbbOrderImplMock.getSpecialInstructions() >> orderSpecialInstructions
			
			//populateItems Private Method Coverage
			BBBShippingGroupCommerceItemRelationship bbbShippingGroupCommerceItemRelationshipMock1 = Mock()
			TBSCommerceItem tbsCommerceItemMock1 = Mock()
			ItemPriceInfo itemPriceInfoMock1 = Mock()
			bbbStoreShippingGroupMock.getCommerceItemRelationships() >> [bbbShippingGroupCommerceItemRelationshipMock,bbbShippingGroupCommerceItemRelationshipMock1]
			bbbShippingGroupCommerceItemRelationshipMock.getCommerceItem() >> tbsCommerceItemMock
			bbbShippingGroupCommerceItemRelationshipMock1.getCommerceItem() >> tbsCommerceItemMock1
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			tbsCommerceItemMock1.getPriceInfo() >> itemPriceInfoMock1
			itemPriceInfoMock.getCurrentPriceDetailsForRange(_) >> []
			itemPriceInfoMock1.getCurrentPriceDetailsForRange(_) >> null
			1 * pricingToolsMock.calculateItemSurchargeInSG(bbbOrderImplMock.getSiteId(), bbbShippingGroupCommerceItemRelationshipMock) >> 87.99d
			pricingToolsMock.round(54.2,2) >> 55
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingRawAmount() == 22
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingAmount() == 26
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getSurcharge() == 0.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getFinalSurcharge() == 0.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getFirstName() == "Harvey"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getLastName() == "Harvey"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getAddressLine1() == "Tremont Street"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getCity() == "Boston"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getState() == "Massachusetts"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getZipCode() == "02120"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getCountryCode() == "US"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().isTaxCalculationFailure() == TRUE
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getAtgShippingGroupId() == "hg123456"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getSubTotal() == 55
	}
	
	def "transformOrder. This TC is when passing bbbStoreShippingGroup and TBS_BuyBuyBaby in populateCMOShipInfo Private Method"(){
		given:
			this.spyForTransforOrder()
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >> null
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 2
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock,orderPriceInfoMock,bbbOrderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbStoreShippingGroupMock]
			bbbStoreShippingGroupMock.getTbsShipInfo() >>> [tBSShippingInfoMock,null]
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "TBS_BuyBuyBaby"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateShipping Private method Coverage
			HashMap subtotalPriceInfosMock = ["hg123456": null]
			bbbOrderPriceInfoMock.getShippingItemsSubtotalPriceInfos() >> subtotalPriceInfosMock
			tbsCommerceItemMock.isCMO() >> TRUE
			bbbStoreShippingGroupMock.getId() >> "hg123456"
			bbbStoreShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			
			//populateCMOShipInfo Private Method Coverage
			testObj.getShipMethod() >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "SDD"
			bbbStoreShippingGroupMock.getShipOnDate() >> null
			bbbStoreShippingGroupMock.getStoreId() >> "storeId"
			1 * searchStoreManagerMock.searchStoreById(bbbStoreShippingGroupMock.getStoreId(), "BuyBuyBaby" , "BuyBuyBaby") >> storeDetailsMock
			storeDetailsMock.getStoreName() >> ""
			storeDetailsMock.getAddress() >> "Tremont Street"
			storeDetailsMock.getCity() >> "Boston"
			storeDetailsMock.getState() >> "Massachusetts"
			storeDetailsMock.getPostalCode() >> "02120"
			storeDetailsMock.getCountry() >> "US"
			bbbShippingPriceInfoMock.getRawShipping() >> 21.99d
			pricingToolsMock.round(21.99,2) >> 22
			bbbShippingPriceInfoMock.getFinalShipping() >> 25.99d
			pricingToolsMock.round(25.99,2) >> 26
			HashMap shippingItemTaxPriceMock = ["hg123456": null]
			taxPriceInfoMock.getShippingItemsTaxPriceInfos() >> shippingItemTaxPriceMock
			orderPriceInfoMock.getAmount() >> 28.2d
			pricingToolsMock.round(26.0,2) >> 26
			
			//isTaxCalculationFailure method bcoz it is final method
			HashMap orderSpecialInstructions = ["CYBERSOURCE_TAX_FAILURE":"false"]
			bbbOrderImplMock.getSpecialInstructions() >> orderSpecialInstructions
			
			//populateItems Private Method Coverage
			bbbStoreShippingGroupMock.getCommerceItemRelationships() >> [bbbShippingGroupCommerceItemRelationshipMock]
			bbbShippingGroupCommerceItemRelationshipMock.getCommerceItem() >> tbsCommerceItemMock
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getCurrentPriceDetailsForRange(_) >> [bbbDetailedItemPriceInfoMock]
			1 * pricingToolsMock.calculateItemSurchargeInSG(bbbOrderImplMock.getSiteId(), bbbShippingGroupCommerceItemRelationshipMock) >> 87.99d
			bbbDetailedItemPriceInfoMock.getQuantity() >> 1
			bbbDetailedItemPriceInfoMock.getAmount() >> 80.99d
			pricingToolsMock.round(_) >> 80.99d
			tbsCommerceItemMock.getDeliveryItemId() >> ""
			tbsCommerceItemMock.getAssemblyItemId() >> "as12345"
			tbsCommerceItemMock.getCatalogRefId() >> "SKU123456"
			1 * catalogToolsMock.getSKUDetails(bbbOrderImplMock.getSiteId(), tbsCommerceItemMock.getCatalogRefId(), false, true, true) >> skuDetailVOMock
			bbbOrderImplMock.getCommerceItem("de12345") >> nonMerchandiseCommerceItemMock
			nonMerchandiseCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			tbsCommerceItemMock.getQuantity() >> 2
			ItemType itemTypeMock = new ItemType()
			testObj.createItemInfo(*_) >> itemTypeMock
			
			//setTBSProperties Private Method Coverage
			tbsCommerceItemMock.getShipTime() >> ""
			tbsCommerceItemMock.getTBSItemInfo() >> null
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			repositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION) >> skuAttrRelationMock
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingRawAmount() == 22
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingAmount() == 26
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getSurcharge() == 0.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getFinalSurcharge() == 0.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getFirstName() == null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getLastName() == null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getAddressLine1() == "Tremont Street"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getCity() == "Boston"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getState() == "Massachusetts"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getZipCode() == "02120"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress().getCountryCode() == "US"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().isTaxCalculationFailure() == null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getAtgShippingGroupId() == "hg123456"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getSubTotal() == 26
	}
	
	def "transformOrder. This TC is when passing bbbStoreShippingGroup and TBS_BedBathCanada in populateCMOShipInfo Private Method"(){
		given:
			this.spyForTransforOrder()
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock,null]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 2
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock,orderPriceInfoMock,bbbOrderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbStoreShippingGroupMock]
			bbbStoreShippingGroupMock.getTbsShipInfo() >>> [tBSShippingInfoMock,null]
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "TBS_BedBathCanada"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateShipping Private method Coverage
			HashMap subtotalPriceInfosMock = ["hg123456": null]
			bbbOrderPriceInfoMock.getShippingItemsSubtotalPriceInfos() >> subtotalPriceInfosMock
			tbsCommerceItemMock.isCMO() >> TRUE
			bbbStoreShippingGroupMock.getId() >> "hg123456"
			bbbStoreShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			
			//populateCMOShipInfo Private Method Coverage
			testObj.getShipMethod() >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "SDD"
			bbbStoreShippingGroupMock.getShipOnDate() >> null
			bbbStoreShippingGroupMock.getStoreId() >> "storeId"
			1 * searchStoreManagerMock.searchStoreById(bbbStoreShippingGroupMock.getStoreId(), "BedBathCanada" , "BedBathCanada") >> null
			bbbShippingPriceInfoMock.getRawShipping() >> 21.99d
			pricingToolsMock.round(21.99,2) >> 22
			bbbShippingPriceInfoMock.getFinalShipping() >> 25.99d
			pricingToolsMock.round(25.99,2) >> 26
			taxPriceInfoMock.getShippingItemsTaxPriceInfos() >> null
			orderPriceInfoMock.getAmount() >> 28.2d
			pricingToolsMock.round(26.0,2) >> 26
			
			//isTaxCalculationFailure method bcoz it is final method
			HashMap orderSpecialInstructions = ["CYBERSOURCE_TAX_FAILURE":"false"]
			bbbOrderImplMock.getSpecialInstructions() >> orderSpecialInstructions
			
			//populateItems Private Method Coverage
			bbbStoreShippingGroupMock.getCommerceItemRelationships() >> [bbbShippingGroupCommerceItemRelationshipMock]
			bbbShippingGroupCommerceItemRelationshipMock.getCommerceItem() >> tbsCommerceItemMock
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getCurrentPriceDetailsForRange(_) >> [bbbDetailedItemPriceInfoMock]
			1 * pricingToolsMock.calculateItemSurchargeInSG(bbbOrderImplMock.getSiteId(), bbbShippingGroupCommerceItemRelationshipMock) >> 87.99d
			bbbDetailedItemPriceInfoMock.getQuantity() >> 1
			bbbDetailedItemPriceInfoMock.getAmount() >> 80.99d
			pricingToolsMock.round(_) >> 80.99d
			tbsCommerceItemMock.getDeliveryItemId() >> ""
			tbsCommerceItemMock.getAssemblyItemId() >> "as12345"
			tbsCommerceItemMock.getCatalogRefId() >> "SKU123456"
			1 * catalogToolsMock.getSKUDetails(bbbOrderImplMock.getSiteId(), tbsCommerceItemMock.getCatalogRefId(), false, true, true) >> skuDetailVOMock
			bbbOrderImplMock.getCommerceItem("de12345") >> nonMerchandiseCommerceItemMock
			nonMerchandiseCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			tbsCommerceItemMock.getQuantity() >> 2
			ItemType itemTypeMock = new ItemType()
			testObj.createItemInfo(*_) >> itemTypeMock
			
			//setTBSProperties Private Method Coverage
			tbsCommerceItemMock.getShipTime() >> "10'0 clock"
			tbsCommerceItemMock.getTBSItemInfo() >> tbsItemInfoMock
			tbsItemInfoMock.getConfigId() >> "tbsConfigId"
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			skuAttrRelationMock.add(skuAttrRepositoryItemMock)
			
			repositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION) >> skuAttrRelationMock
			skuAttrRepositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE) >> skuAttributeMock
			skuAttributeMock.getRepositoryId() >> "12_4"
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingRawAmount() == 22
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingAmount() == 26
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getSurcharge() == 0.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getFinalSurcharge() == 0.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress() == null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().isTaxCalculationFailure() == null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getAtgShippingGroupId() == "hg123456"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getSubTotal() == 26
	}
	
	def "transformOrder. This TC is when passing bbbStoreShippingGroup,BedbathUS and BBBBusinessException thrown in populateCMOShipInfo Private Method"(){
		given:
			this.spyForTransforOrder()
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 2
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock,orderPriceInfoMock,bbbOrderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbStoreShippingGroupMock]
			bbbStoreShippingGroupMock.getTbsShipInfo() >>> [tBSShippingInfoMock,null]
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> "BedbathUS"
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateShipping Private method Coverage
			HashMap subtotalPriceInfosMock = ["hg123456": null]
			bbbOrderPriceInfoMock.getShippingItemsSubtotalPriceInfos() >> subtotalPriceInfosMock
			tbsCommerceItemMock.isCMO() >> TRUE
			bbbStoreShippingGroupMock.getId() >> "hg123456"
			bbbStoreShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			
			//populateCMOShipInfo Private Method Coverage
			testObj.getShipMethod() >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "SDD"
			bbbStoreShippingGroupMock.getShipOnDate() >> null
			bbbStoreShippingGroupMock.getStoreId() >> "storeId12345"
			1 * searchStoreManagerMock.searchStoreById(bbbStoreShippingGroupMock.getStoreId(), "BedbathUS" , "BedbathUS") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			bbbShippingPriceInfoMock.getRawShipping() >> 21.99d
			pricingToolsMock.round(21.99,2) >> 22
			bbbShippingPriceInfoMock.getFinalShipping() >> 25.99d
			pricingToolsMock.round(25.99,2) >> 26
			HashMap shippingItemTaxPriceMock = ["hg123456": taxPriceInfoMock]
			taxPriceInfoMock.getShippingItemsTaxPriceInfos() >> shippingItemTaxPriceMock
			orderPriceInfoMock.getAmount() >> 28.2d
			pricingToolsMock.round(31.0,2) >> 31
			
			//isTaxCalculationFailure method bcoz it is final method
			HashMap orderSpecialInstructions = ["CYBERSOURCE_TAX_FAILURE":"true"]
			bbbOrderImplMock.getSpecialInstructions() >> orderSpecialInstructions
			
			//populateTaxInfo Private Method Coverage
			taxPriceInfoMock.getCityTax() >> 3.2d
			pricingToolsMock.round(3.2,2) >> 3
			taxPriceInfoMock.getCountyTax() >> 4.2d
			pricingToolsMock.round(4.2,2) >> 4
			taxPriceInfoMock.getDistrictTax() >> 2.1d
			pricingToolsMock.round(2.1,2) >> 2
			taxPriceInfoMock.getStateTax() >> 1.1d
			pricingToolsMock.round(1.1,2) >> 1
			taxPriceInfoMock.getAmount() >> 5.2d
			pricingToolsMock.round(5.2,2) >> 5
			
			//populateItems Private Method Coverage
			bbbStoreShippingGroupMock.getCommerceItemRelationships() >> [bbbShippingGroupCommerceItemRelationshipMock]
			bbbShippingGroupCommerceItemRelationshipMock.getCommerceItem() >> tbsCommerceItemMock
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getCurrentPriceDetailsForRange(_) >> [bbbDetailedItemPriceInfoMock]
			1 * pricingToolsMock.calculateItemSurchargeInSG(bbbOrderImplMock.getSiteId(), bbbShippingGroupCommerceItemRelationshipMock) >> 87.99d
			bbbDetailedItemPriceInfoMock.getQuantity() >> 1
			bbbDetailedItemPriceInfoMock.getAmount() >> 80.99d
			pricingToolsMock.round(_) >> 80.99d
			tbsCommerceItemMock.getDeliveryItemId() >> "de12345"
			tbsCommerceItemMock.getAssemblyItemId() >> "as12345"
			tbsCommerceItemMock.getCatalogRefId() >> "SKU123456"
			1 * catalogToolsMock.getSKUDetails(bbbOrderImplMock.getSiteId(), tbsCommerceItemMock.getCatalogRefId(), false, true, true) >> skuDetailVOMock
			bbbOrderImplMock.getCommerceItem("de12345") >> {throw new CommerceItemNotFoundException("Mock for CommerceItemNotFoundException")}
			nonMerchandiseCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			tbsCommerceItemMock.getQuantity() >> 2
			ItemType itemTypeMock = new ItemType()
			testObj.createItemInfo(*_) >> itemTypeMock
			
			//setTBSProperties Private Method Coverage
			tbsCommerceItemMock.getShipTime() >> "10'0 clock"
			tbsCommerceItemMock.getTBSItemInfo() >> tbsItemInfoMock
			tbsItemInfoMock.getConfigId() >> "tbsConfigId"
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> null
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingRawAmount() == 22
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingAmount() == 26
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getSurcharge() == 0.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getFinalSurcharge() == 0.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress() == null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().isTaxCalculationFailure() == TRUE
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getAtgShippingGroupId() == "hg123456"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getSubTotal() == 31
		   1 * repositoryItemMock.getPropertyValue('userSiteItems')
		   1 * testObj.logError('BBBBusinessException in MapQuest call to fetch store data for store id = storeId12345', null)
		   1 * testObj.vlogError('BBBBusinessException in MapQuest call to fetch store data for store id = storeId12345', [])
		   1 * testObj.logError('CommerceItem with Id :- de12345 is not found in container.', _)
	}
	
	def "transformOrder. This TC is when passing bbbStoreShippingGroup,BedbathUS and BBBSystemException thrown in populateCMOShipInfo Private Method"(){
		given:
			this.spyForTransforOrder()
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 2
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			tbsCommerceItemMock.getGsOrderId() >> ""
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> new Date()
			bbbOrderImplMock.getPriceInfo() >>> [null,orderPriceInfoMock,orderPriceInfoMock,bbbOrderPriceInfoMock]
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbStoreShippingGroupMock]
			bbbStoreShippingGroupMock.getTbsShipInfo() >>> [tBSShippingInfoMock,null]
			tBSShippingInfoMock.isTaxOverride() >> FALSE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
			bbbOrderImplMock.getSiteId() >> ""
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateShipping Private method Coverage
			HashMap subtotalPriceInfosMock = ["hg123456": null]
			bbbOrderPriceInfoMock.getShippingItemsSubtotalPriceInfos() >> subtotalPriceInfosMock
			tbsCommerceItemMock.isCMO() >> TRUE
			bbbStoreShippingGroupMock.getId() >> "hg123456"
			bbbStoreShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			
			//populateCMOShipInfo Private Method Coverage
			testObj.getShipMethod() >> repositoryItemMock
			repositoryItemMock.getRepositoryId() >> "SDD"
			bbbStoreShippingGroupMock.getShipOnDate() >> null
			bbbStoreShippingGroupMock.getStoreId() >> "storeId"
			1 * searchStoreManagerMock.searchStoreById(bbbStoreShippingGroupMock.getStoreId(), "" , "") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			bbbShippingPriceInfoMock.getRawShipping() >> 21.99d
			pricingToolsMock.round(21.99,2) >> 22
			bbbShippingPriceInfoMock.getFinalShipping() >> 25.99d
			pricingToolsMock.round(25.99,2) >> 26
			HashMap shippingItemTaxPriceMock = ["hg123456": taxPriceInfoMock]
			taxPriceInfoMock.getShippingItemsTaxPriceInfos() >> shippingItemTaxPriceMock
			orderPriceInfoMock.getAmount() >> 28.2d
			pricingToolsMock.round(26.0,2) >> 26
			
			//isTaxCalculationFailure method bcoz it is final method
			HashMap orderSpecialInstructions = ["CYBERSOURCE_TAX_FAILURE":"true"]
			bbbOrderImplMock.getSpecialInstructions() >> orderSpecialInstructions
			
			//populateItems Private Method Coverage
			bbbStoreShippingGroupMock.getCommerceItemRelationships() >> [bbbShippingGroupCommerceItemRelationshipMock]
			bbbShippingGroupCommerceItemRelationshipMock.getCommerceItem() >> tbsCommerceItemMock
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getCurrentPriceDetailsForRange(_) >> [bbbDetailedItemPriceInfoMock]
			1 * pricingToolsMock.calculateItemSurchargeInSG(bbbOrderImplMock.getSiteId(), bbbShippingGroupCommerceItemRelationshipMock) >> 87.99d
			bbbDetailedItemPriceInfoMock.getQuantity() >> 1
			bbbDetailedItemPriceInfoMock.getAmount() >> 80.99d
			pricingToolsMock.round(_) >> 80.99d
			tbsCommerceItemMock.getDeliveryItemId() >> "de12345"
			tbsCommerceItemMock.getAssemblyItemId() >> "as12345"
			tbsCommerceItemMock.getCatalogRefId() >> "SKU123456"
			1 * catalogToolsMock.getSKUDetails(bbbOrderImplMock.getSiteId(), tbsCommerceItemMock.getCatalogRefId(), false, true, true) >> skuDetailVOMock
			bbbOrderImplMock.getCommerceItem("de12345") >> {throw new InvalidParameterException("Mock for InvalidParameterException")}
			nonMerchandiseCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			tbsCommerceItemMock.getQuantity() >> 2
			ItemType itemTypeMock = new ItemType()
			testObj.createItemInfo(*_) >> itemTypeMock
			
			//setTBSProperties Private Method Coverage
			tbsCommerceItemMock.getShipTime() >> "10'0 clock"
			tbsCommerceItemMock.getTBSItemInfo() >> tbsItemInfoMock
			tbsItemInfoMock.getConfigId() >> "tbsConfigId"
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> null
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingRawAmount() == 22
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getShippingAmount() == 26
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getSurcharge() == 0.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingPriceInfo().getFinalSurcharge() == 0.0
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().getShippingAddress() == null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getShippingInfo().isTaxCalculationFailure() == TRUE
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getAtgShippingGroupId() == "hg123456"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getSubTotal() == 26
		   1 * repositoryItemMock.getPropertyValue('userSiteItems')
		   1 * testObj.logError('BBBSystemException in MapQuest call to fetch store data for store id = storeId', null)
		   1 * testObj.vlogError('BBBSystemException in MapQuest call to fetch store data for store id = storeId', [])
		   1 * testObj.logError('Error occurred while retreiving LTLDeliveryCommerceItem', _)
	}
	
	def "transformOrder. This TC is when orderType is BOPUS in populateShipping Private Method"(){
		given:
			this.spyForTransforOrder()	
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> "bopus"
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 2
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLP"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> null
			bbbOrderImplMock.getPriceInfo() >> bbbOrderPriceInfoMock
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbStoreShippingGroupMock]
			bbbStoreShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> TRUE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> FALSE
			tBSShippingInfoMock.getTaxExemptId() >> "TaxExemptId"
			bbbOrderImplMock.getSiteId() >> "TBS_BuyBuyBaby"
			
			//getMemberID Private Method Coverage
			userSiteItems.put("TBS_BedBathUS", userSiteItemMock)
			repositoryItemMock.getPropertyValue("userSiteItems") >> userSiteItems
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateShipping Private method Coverage
			HashMap subtotalPriceInfosMock = ["hg123456": orderPriceInfoMock]
			bbbOrderPriceInfoMock.getShippingItemsSubtotalPriceInfos() >> subtotalPriceInfosMock
			tbsCommerceItemMock.isCMO() >> TRUE
			bbbStoreShippingGroupMock.getId() >> "hg123456"
			bbbStoreShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			
			//isTaxCalculationFailure method bcoz it is final method
			HashMap orderSpecialInstructions = ["CYBERSOURCE_TAX_FAILURE":"true"]
			bbbOrderImplMock.getSpecialInstructions() >> orderSpecialInstructions
			
			//populateStoreShipInfo Private Method Coverage
			bbbStoreShippingGroupMock.getStoreId() >> "StoreId"
			orderPriceInfoMock.getAmount() >> 23d
			pricingToolsMock.round(23.0,2) >> 23
			
			//populateStoreInfoType Private Method Coverage
			1 * searchStoreManagerMock.getStoreType("TBS_BuyBuyBaby") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
			//populateItems Private Method Coverage
			bbbStoreShippingGroupMock.getCommerceItemRelationships() >> []
			
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreId() == "StoreId"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getStoreDescription() == "Error in getting details from MapQuest"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getStoreName() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getAddress() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getCity() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getState() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getZipCode() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getCountry() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getAtgShippingGroupId() == "hg123456"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getItemList().getItem() == []
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getSubTotal() == 23
		   1 * testObj.logDebug('SubmitOrderMarshaller:populateStoreInfoType ends')
		   1 * testObj.logDebug('SubmitOrderMarshaller:populateStoreInfoType starts')
		   1 * testObj.logError('BBBBusinessException in MapQuest call to fetch store data for store id = StoreId', null)
	}
		
	def "transformOrder. This TC is when orderType is BOPUS and BBBSystemException thrown in populateStoreInfoType Private Method"(){
		given:
			this.spyForTransforOrder()
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> "bopus"
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 2
			
			//populateOrderInfo private Method coverage
			testObj.getOrderManager().getOrderTools() >> orderToolsMock
			orderToolsMock.getProfileTools() >> commerceProfileToolsMock
			commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> repositoryItemMock
			bbbOrderImplMock.getId() >> "BBB12345"
			bbbOrderImplMock.getPropertyValue("bopusOrderNumber") >> "OLP"
			bbbOrderImplMock.getTbsStoreNo() >> "23456"
			bbbOrderImplMock.getOriginOfOrder() >> "Origin"
			bbbOrderImplMock.getSalesOS() >> "salesOS"
			bbbOrderImplMock.getProfileId() >> "p12345"
			bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
			bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
			bbbOrderImplMock.getSubmittedDate() >> null
			bbbOrderImplMock.getPriceInfo() >> bbbOrderPriceInfoMock
			bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
			bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
			bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
			bbbOrderImplMock.getSalesChannel() >> "Schannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbStoreShippingGroupMock]
			bbbStoreShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
			tBSShippingInfoMock.isTaxOverride() >> TRUE
			bbbOrderImplMock.getPropertyValue("emailSignUp") >> FALSE
			tBSShippingInfoMock.getTaxExemptId() >> "TaxExemptId"
			bbbOrderImplMock.getSiteId() >> "TBS_BuyBuyBaby"
			
			//getMemberID Private Method Coverage
			userSiteItems.put("TBS_BedBathUS", userSiteItemMock)
			repositoryItemMock.getPropertyValue("userSiteItems") >> userSiteItems
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populateShipping Private method Coverage
			HashMap subtotalPriceInfosMock = ["hg123456": orderPriceInfoMock]
			bbbOrderPriceInfoMock.getShippingItemsSubtotalPriceInfos() >> subtotalPriceInfosMock
			tbsCommerceItemMock.isCMO() >> TRUE
			bbbStoreShippingGroupMock.getId() >> "hg123456"
			bbbStoreShippingGroupMock.getPriceInfo() >> bbbShippingPriceInfoMock
			
			//isTaxCalculationFailure method bcoz it is final method
			HashMap orderSpecialInstructions = ["CYBERSOURCE_TAX_FAILURE":"true"]
			bbbOrderImplMock.getSpecialInstructions() >> orderSpecialInstructions
			
			//populateStoreShipInfo Private Method Coverage
			bbbStoreShippingGroupMock.getStoreId() >> "StoreId"
			orderPriceInfoMock.getAmount() >> 23d
			pricingToolsMock.round(23.0,2) >> 23
			
			//populateStoreInfoType Private Method Coverage
			1 * searchStoreManagerMock.getStoreType("TBS_BuyBuyBaby") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			//populateItems Private Method Coverage
			bbbStoreShippingGroupMock.getCommerceItemRelationships() >> []
			
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreId() == "StoreId"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getStoreDescription() == "Error in getting details from MapQuest"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getStoreName() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getAddress() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getCity() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getState() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getZipCode() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getStoreInfo().getStoreAddress().getCountry() == "NA"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getAtgShippingGroupId() == "hg123456"
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getItemList().getItem() == []
		   ordersResults.getOrder().get(0).getShipmentList().getShipmentInfo().get(0).getSubTotal() == 23
		   1 * testObj.logDebug('SubmitOrderMarshaller:populateStoreInfoType ends')
		   1 * testObj.logDebug('SubmitOrderMarshaller:populateStoreInfoType starts')
		   1 * testObj.logError('BBBSystemException in MapQuest call to fetch store data for store id = StoreId', null)
	}
	
	def "transformOrder. This TC is when happy flow in populateCreditCardPayment and populateGiftCardPayment private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 0
			bbbOrderImplMock.getPaymentGroupRelationships() >> []
			bbbOrderImplMock.getPaymentGroupRelationshipCount() >> 2
			
			//populateOrderInfo private Method coverage
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			this.populateOrderInfoMethod()
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populatePaymentList Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [bbbCreditCardMock, bbbGiftCardMock]
			bbbCreditCardMock.getId() >> "Cc12345"
			bbbCreditCardMock.getAmount() >> 15d
			pricingToolsMock.round(15.0,2) >> 15
			bbbCreditCardMock.getPaymentMethod() >> "VISA"
			bbbGiftCardMock.getId() >> "gc12345"
			bbbGiftCardMock.getAmount() >> 25d
			pricingToolsMock.round(25.0,2) >> 25
			bbbGiftCardMock.getPaymentMethod() >> "VISA"
			
			//populateCreditCardPayment Private Method Coverage
			bbbCreditCardMock.getCreditCardNumber() >> "41111111111111111"
			aesEncryptorComponentMock.encryptString(bbbCreditCardMock.getCreditCardNumber()) >> "41111111111111111"
			bbbCreditCardMock.getPropertyValue("nameOnCard") >> "John"
			bbbCreditCardMock.getNameOnCard() >> "John"
			bbbCreditCardMock.getCreditCardType() >> "VISA"
			bbbCreditCardMock.getExpirationMonth() >> "05"
			bbbCreditCardMock.getExpirationYear() >> "2020"
			CreditCardStatus creditCardStatusMock1 = Mock()
			bbbCreditCardMock.getAuthorizationStatus() >> [creditCardStatusMock,creditCardStatusMock1]
			creditCardStatusMock1.getAuthorizationCode() >> "code12345"
			bbbCreditCardMock.getAmountAuthorized() >> 85d
			pricingToolsMock.round(bbbCreditCardMock.getAmountAuthorized()) >> 85
			creditCardStatusMock1.getTransactionId() >> "trans12345"
			creditCardStatusMock1.getAvsCode() >> "avs12345"
			creditCardStatusMock1.getTransactionTimestamp() >> new Date()
			creditCardStatusMock1.getAuthResponseRecord() >> "responseRecord"
			bbbOrderImplMock.getPropertyValue("xid") >> "xid"
			bbbOrderImplMock.getPropertyValue("eci") >> "eci"
			bbbOrderImplMock.getPropertyValue("cavv") >> "cavv"
			bbbOrderImplMock.getPropertyValue("commerceIndicator") >> "commerceIndicator" 
			bbbOrderImplMock.getPropertyValue("enrolled") >> "enrolled"
			bbbOrderImplMock.getPropertyValue("pAResStatus") >> "paResStatus"
			bbbOrderImplMock.getPropertyValue("signatureVerification") >> "SignatureVerification"
			
			//populateGiftCardPayment Private Method Coverage
			bbbGiftCardMock.getCardNumber() >> "4123111111111111"
			aesEncryptorComponentMock.encryptString(bbbGiftCardMock.getCardNumber()) >> "4123111111111111"
			bbbGiftCardMock.getPropertyValue("pin") >> "123"
			aesEncryptorComponentMock.encryptString(bbbGiftCardMock.getPin()) >> "123"
			GiftCardStatus giftCardStatusMock1 = Mock()
			bbbGiftCardMock.getAuthorizationStatus() >>[giftCardStatusMock, giftCardStatusMock1]
			giftCardStatusMock.getTransactionSuccess() >> FALSE
			giftCardStatusMock1.getTransactionSuccess() >> TRUE
			giftCardStatusMock1.getTransactionTimestamp() >> new Date()
			giftCardStatusMock1.getTraceNumber() >> "123456"
			giftCardStatusMock1.getAuthCode() >> "authCode"
			bbbGiftCardMock.getPropertyValue("balance") >> 100d
			pricingToolsMock.round(100.0,2) >> 100
			giftCardStatusMock1.getAuthRespCode() >> "giftAuthResponsecode"
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPaymentId() == "Cc12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getAmount() == 15
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPaymentType() == PaymentTypes.CREDIT_CARD
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCreditCardNumber() == "41111111111111111"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getNameOnCreditCard() == "John"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCreditCardType() == "VISA"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getExpiration() != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCcAuthInfo().getAuthAmount() == 85
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCcAuthInfo().getRequestId() == "trans12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCcAuthInfo().getAvsCode() == "avs12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCcAuthInfo().getAuthTime() != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCcAuthInfo().getAuthCode() == "code12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCcAuthInfo().getAuthResponseRecord() == "responseRecord"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getXid() == "xid" 
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getEciFlag() == "eci"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getCavvUCAF() == "cavv"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getEcomIndicator() == "commerceIndicator" 
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getEnrollmentStatus() == "enrolled"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getAuthenticationStatus() == "paResStatus"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getSignatureStatus() == "SignatureVerification"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPaymentId() == "gc12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getAmount() == 25
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPaymentType() == PaymentTypes.GIFT_CARD
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getGiftCardNumber() == "4123111111111111"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getPin() == "123"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getGcAuthInfo().getAuthTime() != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getGcAuthInfo().getTraceNumber() == 123456
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getGcAuthInfo().getPreviousBalance() == 100
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getGcAuthInfo().getResponseCode() == "giftAuthResponsecode"
		   
	}

	def "transformOrder. This TC is when getAuthorizationCode is empty in populateCreditCardPayment Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getPaymentGroupRelationships() >> []
			bbbOrderImplMock.getPaymentGroupRelationshipCount() >> 2
			
			//populateOrderInfo private Method coverage
			bbbOrderImplMock.getShippingGroups() >>> [[shippingGroupMock, bbbHardGoodShippingGroupMock],null]
			this.populateOrderInfoMethod()
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populatePaymentList Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [bbbCreditCardMock, bbbGiftCardMock]
			bbbCreditCardMock.getId() >> "Cc12345"
			bbbCreditCardMock.getAmount() >> 15d
			pricingToolsMock.round(15.0,2) >> 15
			bbbCreditCardMock.getPaymentMethod() >> "VISA"
			bbbGiftCardMock.getId() >> "gc12345"
			bbbGiftCardMock.getAmount() >> 25d
			pricingToolsMock.round(25.0,2) >> 25
			bbbGiftCardMock.getPaymentMethod() >> "VISA"
			
			//populateCreditCardPayment Private Method Coverage
			bbbCreditCardMock.getCreditCardNumber() >> "41111111111111111"
			aesEncryptorComponentMock.encryptString(bbbCreditCardMock.getCreditCardNumber()) >> "41111111111111111"
			bbbCreditCardMock.getPropertyValue("nameOnCard") >> ""
			bbbCreditCardMock.getNameOnCard() >> "John"
			bbbCreditCardMock.getCreditCardType() >> "VISA"
			bbbCreditCardMock.getExpirationMonth() >> "05"
			bbbCreditCardMock.getExpirationYear() >> "2020"
			CreditCardStatus creditCardStatusMock1 = Mock()
			bbbCreditCardMock.getAuthorizationStatus() >> [creditCardStatusMock,creditCardStatusMock1]
			creditCardStatusMock1.getAuthorizationCode() >> ""
			creditCardStatusMock1.getTransactionId() >> "trans12345"
			creditCardStatusMock1.getTransactionTimestamp() >> new Date()
			creditCardStatusMock1.getAuthResponseRecord() >> "responseErrorRecord"
			bbbOrderImplMock.getPropertyValue("xid") >> ""
			bbbOrderImplMock.getPropertyValue("eci") >> "eci"
			bbbOrderImplMock.getPropertyValue("cavv") >> "cavv"
			bbbOrderImplMock.getPropertyValue("enrolled") >> "enrolled"
			bbbOrderImplMock.getPropertyValue("pAResStatus") >> "paResStatus"
			bbbOrderImplMock.getPropertyValue("signatureVerification") >> "SignatureVerification"
			
			//populateGiftCardPayment Private Method Coverage
			bbbGiftCardMock.getCardNumber() >> "4123111111111111"
			aesEncryptorComponentMock.encryptString(bbbGiftCardMock.getCardNumber()) >> "4123111111111111"
			bbbGiftCardMock.getPropertyValue("pin") >> "123"
			aesEncryptorComponentMock.encryptString(bbbGiftCardMock.getPin()) >> "123"
			GiftCardStatus giftCardStatusMock1 = Mock()
			bbbGiftCardMock.getAuthorizationStatus() >>[giftCardStatusMock, giftCardStatusMock1]
			giftCardStatusMock.getTransactionSuccess() >> FALSE
			giftCardStatusMock1.getTransactionSuccess() >> TRUE
			giftCardStatusMock1.getTransactionTimestamp() >> new Date()
			giftCardStatusMock1.getTraceNumber() >> ""
			giftCardStatusMock1.getAuthCode() >> "authCode"
			bbbGiftCardMock.getPropertyValue("balance") >> null
			giftCardStatusMock1.getAuthRespCode() >> "giftAuthResponsecode"
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPaymentId() == "Cc12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getAmount() == 15
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPaymentType() == PaymentTypes.CREDIT_CARD
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCreditCardNumber() == "41111111111111111"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getNameOnCreditCard() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCreditCardType() == "VISA"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getExpiration() != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCcAuthInfo() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCcAuthError().getRequestId() == "trans12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCcAuthError().getAuthTime() != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCcAuthError().getAuthError() == "responseErrorRecord"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getXid() == ""
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getEciFlag() == "eci"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getCavvUCAF() == "cavv"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getEcomIndicator() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getEnrollmentStatus() == "enrolled"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getAuthenticationStatus() == "paResStatus"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getSignatureStatus() == "SignatureVerification"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPaymentId() == "gc12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getAmount() == 25
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPaymentType() == PaymentTypes.GIFT_CARD
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getGiftCardNumber() == "4123111111111111"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getPin() == "123"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getGcAuthInfo().getAuthTime() != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getGcAuthInfo().getTraceNumber() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getGcAuthInfo().getPreviousBalance() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getGcAuthInfo().getResponseCode() == "giftAuthResponsecode"
		   
	}
	
	def "transformOrder. This TC is when authStatusList is null in populateCreditCardPayment Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getPaymentGroupRelationships() >> []
			bbbOrderImplMock.getPaymentGroupRelationshipCount() >> 2
			
			//populateOrderInfo private Method coverage
			bbbOrderImplMock.getShippingGroups() >>> [[shippingGroupMock, bbbHardGoodShippingGroupMock],null]
			this.populateOrderInfoMethod()
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populatePaymentList Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [bbbCreditCardMock, bbbGiftCardMock]
			bbbCreditCardMock.getId() >> "Cc12345"
			bbbCreditCardMock.getAmount() >> 15d
			pricingToolsMock.round(15.0,2) >> 15
			bbbCreditCardMock.getPaymentMethod() >> "VISA"
			bbbGiftCardMock.getId() >> "gc12345"
			bbbGiftCardMock.getAmount() >> 25d
			pricingToolsMock.round(25.0,2) >> 25
			bbbGiftCardMock.getPaymentMethod() >> "VISA"
			
			//populateCreditCardPayment Private Method Coverage
			bbbCreditCardMock.getCreditCardNumber() >> "41111111111111111"
			aesEncryptorComponentMock.encryptString(bbbCreditCardMock.getCreditCardNumber()) >> "41111111111111111"
			bbbCreditCardMock.getPropertyValue("nameOnCard") >> ""
			bbbCreditCardMock.getNameOnCard() >> "John"
			bbbCreditCardMock.getCreditCardType() >> "VISA"
			bbbCreditCardMock.getExpirationMonth() >> "05"
			bbbCreditCardMock.getExpirationYear() >> "2020"
			bbbCreditCardMock.getAuthorizationStatus() >> null
			bbbOrderImplMock.getPropertyValue("xid") >> "xid"
			bbbOrderImplMock.getPropertyValue("eci") >> "eci"
			bbbOrderImplMock.getPropertyValue("cavv") >> ""
			bbbOrderImplMock.getPropertyValue("enrolled") >> "enrolled"
			bbbOrderImplMock.getPropertyValue("pAResStatus") >> "paResStatus"
			bbbOrderImplMock.getPropertyValue("signatureVerification") >> "SignatureVerification"
			
			//populateGiftCardPayment Private Method Coverage
			bbbGiftCardMock.getCardNumber() >> "4123111111111111"
			aesEncryptorComponentMock.encryptString(bbbGiftCardMock.getCardNumber()) >> "4123111111111111"
			bbbGiftCardMock.getPropertyValue("pin") >> "123"
			aesEncryptorComponentMock.encryptString(bbbGiftCardMock.getPin()) >> "123"
			GiftCardStatus giftCardStatusMock1 = Mock()
			bbbGiftCardMock.getAuthorizationStatus() >>[giftCardStatusMock, giftCardStatusMock1]
			giftCardStatusMock.getTransactionSuccess() >> FALSE
			giftCardStatusMock1.getTransactionSuccess() >> FALSE
			
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPaymentId() == "Cc12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getAmount() == 15
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPaymentType() == PaymentTypes.CREDIT_CARD
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCreditCardNumber() == "41111111111111111"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getNameOnCreditCard() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCreditCardType() == "VISA"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getExpiration() != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCcAuthInfo() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCcAuthError() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getXid() == "xid"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getEciFlag() == "eci"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getCavvUCAF() == ""
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getEcomIndicator() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getEnrollmentStatus() == "enrolled"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getAuthenticationStatus() == "paResStatus"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getSignatureStatus() == "SignatureVerification"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPaymentId() == "gc12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getAmount() == 25
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPaymentType() == PaymentTypes.GIFT_CARD
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getGiftCardNumber() == "4123111111111111"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getPin() == "123"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getGcAuthInfo() == null
	}
	
	def "transformOrder. This TC is when authStatusList is empty in populateCreditCardPayment"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getPaymentGroupRelationships() >> []
			bbbOrderImplMock.getPaymentGroupRelationshipCount() >> 2
			
			//populateOrderInfo private Method coverage
			bbbOrderImplMock.getShippingGroups() >>> [[shippingGroupMock, bbbHardGoodShippingGroupMock],null]
			this.populateOrderInfoMethod()
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populatePaymentList Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [bbbCreditCardMock, bbbGiftCardMock]
			bbbCreditCardMock.getId() >> "Cc12345"
			bbbCreditCardMock.getAmount() >> 15d
			pricingToolsMock.round(15.0,2) >> 15
			bbbCreditCardMock.getPaymentMethod() >> "VISA"
			bbbGiftCardMock.getId() >> "gc12345"
			bbbGiftCardMock.getAmount() >> 25d
			pricingToolsMock.round(25.0,2) >> 25
			bbbGiftCardMock.getPaymentMethod() >> "VISA"
			
			//populateCreditCardPayment Private Method Coverage
			bbbCreditCardMock.getCreditCardNumber() >> "41111111111111111"
			aesEncryptorComponentMock.encryptString(bbbCreditCardMock.getCreditCardNumber()) >> "41111111111111111"
			bbbCreditCardMock.getPropertyValue("nameOnCard") >> ""
			bbbCreditCardMock.getNameOnCard() >> "John"
			bbbCreditCardMock.getCreditCardType() >> "VISA"
			bbbCreditCardMock.getExpirationMonth() >> "05"
			bbbCreditCardMock.getExpirationYear() >> "2020"
			bbbCreditCardMock.getAuthorizationStatus() >> []
			bbbOrderImplMock.getPropertyValue("xid") >> "xid"
			bbbOrderImplMock.getPropertyValue("eci") >> "eci"
			bbbOrderImplMock.getPropertyValue("cavv") >> ""
			bbbOrderImplMock.getPropertyValue("enrolled") >> "enrolled"
			bbbOrderImplMock.getPropertyValue("pAResStatus") >> "paResStatus"
			bbbOrderImplMock.getPropertyValue("signatureVerification") >> "SignatureVerification"
			
			//populateGiftCardPayment Private Method Coverage
			bbbGiftCardMock.getCardNumber() >> "4123111111111111"
			aesEncryptorComponentMock.encryptString(bbbGiftCardMock.getCardNumber()) >> "4123111111111111"
			bbbGiftCardMock.getPropertyValue("pin") >> "123"
			aesEncryptorComponentMock.encryptString(bbbGiftCardMock.getPin()) >> "123"
			bbbGiftCardMock.getAuthorizationStatus() >> null
			
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPaymentId() == "Cc12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getAmount() == 15
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPaymentType() == PaymentTypes.CREDIT_CARD
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCreditCardNumber() == "41111111111111111"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getNameOnCreditCard() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCreditCardType() == "VISA"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getExpiration() != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCcAuthInfo() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCcAuthError() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getXid() == "xid"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getEciFlag() == "eci"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getCavvUCAF() == ""
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getEcomIndicator() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getEnrollmentStatus() == "enrolled"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getAuthenticationStatus() == "paResStatus"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getCreditCardInfo().getCc3DSecureInfo().getSignatureStatus() == "SignatureVerification"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPaymentId() == "gc12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getAmount() == 25
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPaymentType() == PaymentTypes.GIFT_CARD
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getGiftCardNumber() == "4123111111111111"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getPin() == "123"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getGiftCardInfo().getGcAuthInfo() == null
	}
	
	def "transformOrder. This TC is when EncryptorException thrown in populateCreditCardPayment Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getPaymentGroupRelationships() >> []
			bbbOrderImplMock.getPaymentGroupRelationshipCount() >> 2
			
			//populateOrderInfo private Method coverage
			bbbOrderImplMock.getShippingGroups() >>> [[shippingGroupMock, bbbHardGoodShippingGroupMock],null]
			this.populateOrderInfoMethod()
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populatePaymentList Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [bbbCreditCardMock]
			bbbCreditCardMock.getId() >> "Cc12345"
			bbbCreditCardMock.getAmount() >> 15d
			pricingToolsMock.round(15.0,2) >> 15
			bbbCreditCardMock.getPaymentMethod() >> "VISA"
			
			//populateCreditCardPayment Private Method Coverage
			bbbCreditCardMock.getCreditCardNumber() >> "41111111111111111"
			aesEncryptorComponentMock.encryptString(bbbCreditCardMock.getCreditCardNumber()) >> {throw new EncryptorException("Mock for EncryptorException")}
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults == null
		   thrown BBBSystemException
		   1 * aesEncryptorComponentMock.logError('Error while encrypting Credit card details', _)
	}
	
	def "transformOrder. This TC is when EncryptorException thrown in populateGiftCardPayment Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getPaymentGroupRelationships() >> []
			bbbOrderImplMock.getPaymentGroupRelationshipCount() >> 2
			
			//populateOrderInfo private Method coverage
			bbbOrderImplMock.getShippingGroups() >>> [[shippingGroupMock, bbbHardGoodShippingGroupMock],null]
			this.populateOrderInfoMethod()
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populatePaymentList Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [bbbGiftCardMock]
			bbbGiftCardMock.getId() >> "gc12345"
			bbbGiftCardMock.getAmount() >> 25d
			pricingToolsMock.round(25.0,2) >> 25
			bbbGiftCardMock.getPaymentMethod() >> "VISA"
			
			//populateGiftCardPayment Private Method Coverage
			bbbGiftCardMock.getCardNumber() >> "4123111111111111"
			aesEncryptorComponentMock.encryptString(bbbGiftCardMock.getCardNumber()) >> {throw new EncryptorException("Mock for EncryptorException")}
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults == null
		   thrown BBBSystemException
		   1 * aesEncryptorComponentMock.logError('Error while encrypting Gift card details', _)
	}
	
	def "transformOrder. This TC is when getPayerStatus is unverified in populatePaypalPayment Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >>> [null,bbbRepositoryContactInfoMock]
			bbbOrderImplMock.getShippingGroupCount() >> 0
			bbbOrderImplMock.getPaymentGroupRelationships() >> []
			bbbOrderImplMock.getPaymentGroupRelationshipCount() >> 2
			
			//populateOrderInfo private Method coverage
			BBBHardGoodShippingGroup bbbHardGoodShippingGroupMock1 = Mock()
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock,bbbHardGoodShippingGroupMock1]
			this.populateOrderInfoMethod()
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populatePaymentList Private Method Coverage 
			bbbOrderImplMock.getPaymentGroups() >> [bbbGiftCardMock,paypalMock]
			bbbGiftCardMock.getId() >> "gc12345"
			bbbGiftCardMock.getAmount() >> 25d
			pricingToolsMock.round(25.0,2) >> 25
			bbbGiftCardMock.getPaymentMethod() >> "VISA"
			paypalMock.getId() >> "pp12345"
			paypalMock.getAmount() >> 35d
			pricingToolsMock.round(35.0,2) >> 35
			paypalMock.getPaymentMethod() >> "PAYPAL"
			
			//populateGiftCardPayment Private Method Coverage
			bbbGiftCardMock.getCardNumber() >> "4123111111111111"
			aesEncryptorComponentMock.encryptString(bbbGiftCardMock.getCardNumber()) >> "4123111111111111"
			bbbGiftCardMock.getPropertyValue("pin") >> "123"
			aesEncryptorComponentMock.encryptString(bbbGiftCardMock.getPin()) >> "123"
			bbbGiftCardMock.getAuthorizationStatus() >> []
			
			//populatePaypalPayment Private Method Coverage
			PaypalStatus paypalStatusMock1 = Mock()
			paypalMock.getAuthorizationStatus() >> [paypalStatusMock,paypalStatusMock1]
			paypalStatusMock.getTransactionSuccess() >> FALSE
			paypalStatusMock1.getTransactionSuccess() >> TRUE
			paypalStatusMock1.getOrderTimestamp() >> new Date()
			paypalStatusMock1.getAuthTimeStamp() >> new Date()
			paypalStatusMock1.getPaypalOrder() >> "paypal12345"
			paypalStatusMock1.getTransId() >> "trans12345"
			paypalStatusMock1.getProtectionEligibility() >> "PartiallyEligible"
			paypalStatusMock1.getCorrelationId() >> "789456"
			paypalMock.getPayerId() >> "CY12345"
			paypalMock.getPayerEmail() >> "abraham@gmail.com" 
			paypalMock.getPayerStatus() >> "unverified"
			bbbHardGoodShippingGroupMock.getPropertyValue("isFromPaypal") >> TRUE
			bbbHardGoodShippingGroupMock.getPropertyValue("addressStatus") >> "Unconfirmed"
			bbbHardGoodShippingGroupMock1.getPropertyValue("isFromPaypal") >> FALSE
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbRepositoryContactInfoMock.isFromPaypal() >> TRUE
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPaymentType() == PaymentTypes.GIFT_CARD
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getGiftCardInfo().getGiftCardNumber() == "4123111111111111"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getGiftCardInfo().getPin() == "123"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getGiftCardInfo().getGcAuthInfo() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPaymentType() == PaymentTypes.PAYPAL
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPayPalInfo().getOrderTimeStamp() != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPayPalInfo().getAuthTimeStamp() != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPayPalInfo().getPayPalOrderId() == "paypal12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPayPalInfo().getPayPalTransactionId() == "trans12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPayPalInfo().getPayPalSellerProtection() == "2"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPayPalInfo().getPayPalCorelationId() == "789456"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPayPalInfo().getPayPalPayerId() == "CY12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPayPalInfo().getPayPalPayerEmail() == "abraham@gmail.com" 
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPayPalInfo().getPayPalUserVerified() == "2"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPayPalInfo().getPayPalShippingVerified() == "2"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPayPalInfo().isPayPalBillingAddressUsed() == TRUE
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(1).getPayPalInfo().getPayPalInvoiceId() == "XXX"
	}
	
	def "transformOrder. This TC is when getPayerStatus is verified in populatePaypalPayment Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >>> [null,bbbRepositoryContactInfoMock]
			bbbOrderImplMock.getShippingGroupCount() >> 0
			bbbOrderImplMock.getPaymentGroupRelationships() >> []
			bbbOrderImplMock.getPaymentGroupRelationshipCount() >> 2
			
			//populateOrderInfo private Method coverage
			BBBHardGoodShippingGroup bbbHardGoodShippingGroupMock1 = Mock()
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock,bbbHardGoodShippingGroupMock1]
			this.populateOrderInfoMethod()
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populatePaymentList Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [paypalMock]
			paypalMock.getId() >> "pp12345"
			paypalMock.getAmount() >> 35d
			pricingToolsMock.round(35.0,2) >> 35
			paypalMock.getPaymentMethod() >> "PAYPAL"
			
			//populatePaypalPayment Private Method Coverage
			PaypalStatus paypalStatusMock1 = Mock()
			paypalMock.getAuthorizationStatus() >> [paypalStatusMock,paypalStatusMock1]
			paypalStatusMock.getTransactionSuccess() >> FALSE
			paypalStatusMock1.getTransactionSuccess() >> TRUE
			paypalStatusMock1.getOrderTimestamp() >> new Date()
			paypalStatusMock1.getAuthTimeStamp() >> new Date()
			paypalStatusMock1.getPaypalOrder() >> "paypal12345"
			paypalStatusMock1.getTransId() >> "trans12345"
			paypalStatusMock1.getProtectionEligibility() >> "Eligible"
			paypalStatusMock1.getCorrelationId() >> "789456"
			paypalMock.getPayerId() >> "CY12345"
			paypalMock.getPayerEmail() >> "abraham@gmail.com"
			paypalMock.getPayerStatus() >> "verified"
			bbbHardGoodShippingGroupMock.getPropertyValue("isFromPaypal") >> TRUE
			bbbHardGoodShippingGroupMock.getPropertyValue("addressStatus") >> "Confirmed"
			bbbHardGoodShippingGroupMock1.getPropertyValue("isFromPaypal") >> FALSE
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			bbbRepositoryContactInfoMock.isFromPaypal() >> FALSE
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPaymentType() == PaymentTypes.PAYPAL
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getOrderTimeStamp() != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getAuthTimeStamp() != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalOrderId() == "paypal12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalTransactionId() == "trans12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalSellerProtection() == "1"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalCorelationId() == "789456"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalPayerId() == "CY12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalPayerEmail() == "abraham@gmail.com"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalUserVerified() == "1"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalShippingVerified() == "1"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().isPayPalBillingAddressUsed() == FALSE
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalInvoiceId() == "XXX"
	}
	
	def "transformOrder. This TC is when getPayerStatus is empty in populatePaypalPayment Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 0
			bbbOrderImplMock.getPaymentGroupRelationships() >> []
			bbbOrderImplMock.getPaymentGroupRelationshipCount() >> 2
			
			//populateOrderInfo private Method coverage
			BBBHardGoodShippingGroup bbbHardGoodShippingGroupMock1 = Mock()
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock,bbbHardGoodShippingGroupMock1]
			this.populateOrderInfoMethod()
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populatePaymentList Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [paypalMock]
			paypalMock.getId() >> "pp12345"
			paypalMock.getAmount() >> 35d
			pricingToolsMock.round(35.0,2) >> 35
			paypalMock.getPaymentMethod() >> "PAYPAL"
			
			//populatePaypalPayment Private Method Coverage
			PaypalStatus paypalStatusMock1 = Mock()
			paypalMock.getAuthorizationStatus() >> [paypalStatusMock,paypalStatusMock1]
			paypalStatusMock.getTransactionSuccess() >> FALSE
			paypalStatusMock1.getTransactionSuccess() >> TRUE
			paypalStatusMock1.getOrderTimestamp() >> new Date()
			paypalStatusMock1.getAuthTimeStamp() >> new Date()
			paypalStatusMock1.getPaypalOrder() >> "paypal12345"
			paypalStatusMock1.getTransId() >> "trans12345"
			paypalStatusMock1.getProtectionEligibility() >> ""
			paypalStatusMock1.getCorrelationId() >> "789456"
			paypalMock.getPayerId() >> "CY12345"
			paypalMock.getPayerEmail() >> "abraham@gmail.com"
			paypalMock.getPayerStatus() >> ""
			bbbHardGoodShippingGroupMock.getPropertyValue("isFromPaypal") >> TRUE
			bbbHardGoodShippingGroupMock.getPropertyValue("addressStatus") >> ""
			bbbHardGoodShippingGroupMock1.getPropertyValue("isFromPaypal") >> FALSE
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPaymentType() == PaymentTypes.PAYPAL
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getOrderTimeStamp() != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getAuthTimeStamp() != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalOrderId() == "paypal12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalTransactionId() == "trans12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalSellerProtection() == "0"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalCorelationId() == "789456"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalPayerId() == "CY12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalPayerEmail() == "abraham@gmail.com"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalUserVerified() == "0"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalShippingVerified() == "0"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().isPayPalBillingAddressUsed() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalInvoiceId() == "XXX"
	}
	
	def "transformOrder. This TC is when getAddressStatus is null in populatePaypalPayment Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
		
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 0
			bbbOrderImplMock.getPaymentGroupRelationships() >> []
			bbbOrderImplMock.getPaymentGroupRelationshipCount() >> 2
			
			//populateOrderInfo private Method coverage
			BBBHardGoodShippingGroup bbbHardGoodShippingGroupMock1 = Mock()
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock,bbbHardGoodShippingGroupMock1]
			this.populateOrderInfoMethod()
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populatePaymentList Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [paypalMock]
			paypalMock.getId() >> "pp12345"
			paypalMock.getAmount() >> 35d
			pricingToolsMock.round(35.0,2) >> 35
			paypalMock.getPaymentMethod() >> "PAYPAL"
			
			//populatePaypalPayment Private Method Coverage
			PaypalStatus paypalStatusMock1 = Mock()
			paypalMock.getAuthorizationStatus() >> [paypalStatusMock,paypalStatusMock1]
			paypalStatusMock.getTransactionSuccess() >> FALSE
			paypalStatusMock1.getTransactionSuccess() >> FALSE
			paypalMock.getPayerId() >> "CY12345"
			paypalMock.getPayerEmail() >> "abraham@gmail.com"
			paypalMock.getPayerStatus() >> ""
			bbbHardGoodShippingGroupMock.getPropertyValue("isFromPaypal") >> TRUE
			bbbHardGoodShippingGroupMock.getPropertyValue("addressStatus") >> null
			bbbHardGoodShippingGroupMock1.getPropertyValue("isFromPaypal") >> FALSE
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPaymentType() == PaymentTypes.PAYPAL
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getOrderTimeStamp() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getAuthTimeStamp() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalOrderId() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalTransactionId() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalSellerProtection() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalCorelationId() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalPayerId() == "CY12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalPayerEmail() == "abraham@gmail.com"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalUserVerified() == "0"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalShippingVerified() == "0"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().isPayPalBillingAddressUsed() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalInvoiceId() == "XXX"
	}
	
	def "transformOrder. This TC is when authStatusList is null in populatePaypalPayment Private Method"(){
		given:
			this.spyForTransforOrder()
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock,tbsCommerceItemMock]
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 0
			bbbOrderImplMock.getPaymentGroupRelationships() >> []
			bbbOrderImplMock.getPaymentGroupRelationshipCount() >> 2
			
			//populateOrderInfo private Method coverage
			BBBHardGoodShippingGroup bbbHardGoodShippingGroupMock1 = Mock()
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock,bbbHardGoodShippingGroupMock1]
			this.populateOrderInfoMethod()
						
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> []
			
			//populatePaymentList Private Method Coverage
			Paypal paypalMock1 = Mock()
			bbbOrderImplMock.getPaymentGroups() >> [paypalMock,paypalMock1]
			paypalMock.getId() >> "pp12345"
			paypalMock.getAmount() >> 35d
			pricingToolsMock.round(35.0,2) >> 35
			paypalMock.getPaymentMethod() >> "PAYPAL"
			paypalMock1.getId() >> "pp12345"
			paypalMock1.getAmount() >> 35d
			pricingToolsMock.round(35.0,2) >> 35
			paypalMock1.getPaymentMethod() >> "PAYPAL"
			
			//populatePaypalPayment Private Method Coverage
			paypalMock.getAuthorizationStatus() >> null
			paypalMock1.getAuthorizationStatus() >> []
			paypalMock.getPayerId() >> "CY12345"
			paypalMock.getPayerEmail() >> "abraham@gmail.com"
			paypalMock.getPayerStatus() >> ""
			paypalMock1.getPayerId() >> "CY12345"
			paypalMock1.getPayerEmail() >> "abraham@gmail.com"
			paypalMock1.getPayerStatus() >> ""
			bbbHardGoodShippingGroupMock.getPropertyValue("isFromPaypal") >> TRUE
			bbbHardGoodShippingGroupMock.getPropertyValue("addressStatus") >> null
			bbbHardGoodShippingGroupMock1.getPropertyValue("isFromPaypal") >> FALSE
			bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPaymentType() == PaymentTypes.PAYPAL
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getOrderTimeStamp() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getAuthTimeStamp() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalOrderId() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalTransactionId() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalSellerProtection() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalCorelationId() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalPayerId() == "CY12345"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalPayerEmail() == "abraham@gmail.com"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalUserVerified() == "0"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalShippingVerified() == "0"
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().isPayPalBillingAddressUsed() == null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPayPalInfo().getPayPalInvoiceId() == "XXX"
	}
	
	def "transformOrder. This TC is when paymentmethod is payAtRegister to execute populatePOSPayment private Method"(){
		given:
			this.spyForTransforOrder()
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> "bopus"
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 0
			bbbOrderImplMock.getPaymentGroupRelationships() >> []
			bbbOrderImplMock.getPaymentGroupRelationshipCount() >> 2
			
			//populateOrderInfo private Method coverage
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			this.populateOrderInfoMethod()
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> null
			
			//populatePaymentList Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [bbbGiftCardMock]
			bbbGiftCardMock.getId() >> "pp12345"
			bbbGiftCardMock.getAmount() >> 35d
			pricingToolsMock.round(35.0,2) >> 35
			bbbGiftCardMock.getPaymentMethod() >> "payAtRegister"
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPaymentType() == PaymentTypes.POS
		   ordersResults.getOrder().get(0).getPaymentList().getPaymentInfo().get(0).getPosInfo().getPosPayment() == "Y"
	}
	
	def "transformOrder. This TC is when orderType is BOPUS and getPaymentMethod is Register in populatePaymentList Private Method"(){
		given:
			bbbOrderImplMock.getCommerceItems() >> [bbbCommerceItemMock]
			bbbCommerceItemMock.getStoreId() >> "bopus"
			
			//createOrder Private Method Coverage
			bbbOrderImplMock.getTaxPriceInfo() >>> [null,taxPriceInfoMock]
			bbbOrderImplMock.getBillingAddress() >> null
			bbbOrderImplMock.getShippingGroupCount() >> 0
			bbbOrderImplMock.getPaymentGroupRelationships() >> []
			bbbOrderImplMock.getPaymentGroupRelationshipCount() >> 2
			
			//populateOrderInfo private Method coverage
			bbbOrderImplMock.getShippingGroups() >> [shippingGroupMock, bbbHardGoodShippingGroupMock]
			this.populateOrderInfoMethod()
			
			//populateSchoolPromoType Private Method Coverage
			bbbOrderImplMock.getPropertyValue("schoolId") >> "sch123456"
			bbbOrderImplMock.getSchoolCoupon() >> "SCHCOUPON"
			
			//getTaxExemptID Private Method Coverage
			catalogToolsMock.getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
			BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_TAX_EXEMPT_ID) >> ["true"]
			
			//populatePaymentList Private Method Coverage
			bbbOrderImplMock.getPaymentGroups() >> [paypalMock]
			paypalMock.getId() >> "pp12345"
			paypalMock.getAmount() >> 35d
			pricingToolsMock.round(35.0,2) >> 35
			paypalMock.getPaymentMethod() >> "Register"
			
		when:
			Orders ordersResults = testObj.transformOrder(bbbOrderImplMock)

	   then:
		   ordersResults != null
		   ordersResults.getOrder().get(0).getPaymentList() == null
	}
	
	private spyForTransforOrder() {
		testObj = Spy()
		testObj.setTBSSiteIdMap(tBSSiteIdMap)
		testObj.setCatalogTools(catalogToolsMock)
		testObj.setStoreManager(searchStoreManagerMock)
		testObj.setOrderManager(bbbOrderManagerMock)
		//toBigDecimal Private Method Coverage
		testObj.setPricingTools(pricingToolsMock)
	}
	
	private populateOrderInfoMethod() {
		testObj.getOrderManager().getOrderTools() >> orderToolsMock
		orderToolsMock.getProfileTools() >> commerceProfileToolsMock
		commerceProfileToolsMock.getProfileForOrder(bbbOrderImplMock) >> null
		bbbOrderImplMock.getId() >> "BBB12345"
		bbbOrderImplMock.getOnlineOrderNumber() >> "XXX"
		bbbOrderImplMock.getTbsStoreNo() >> "23456"
		tbsCommerceItemMock.getGsOrderId() >> ""
		bbbOrderImplMock.getOriginOfOrder() >> "Origin"
		bbbOrderImplMock.getSalesOS() >> "salesOS"
		bbbOrderImplMock.getProfileId() >> "p12345"
		bbbOrderImplMock.getPropertyValue("userIP") >> "128.168.1.256"
		bbbOrderImplMock.getPropertyValue("affiliate") >> "affiliateId"
		bbbOrderImplMock.getSubmittedDate() >> new Date()
		bbbOrderImplMock.getPriceInfo() >> null
		bbbOrderImplMock.getPropertyValue("deviceFingerprint") >> "fingerPrint"
		bbbOrderImplMock.getTBSApproverID() >> "TBS_LEAD"
		bbbOrderImplMock.getTBSAssociateID() >> "TBSAssociateId"
		bbbOrderImplMock.getSalesChannel() >> "Schannel"
		testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
		bbbHardGoodShippingGroupMock.getTbsShipInfo() >> tBSShippingInfoMock
		tBSShippingInfoMock.isTaxOverride() >> FALSE
		bbbOrderImplMock.getPropertyValue("emailSignUp") >> TRUE
		bbbOrderImplMock.getSiteId() >> "BedBathUS"
	}
	
	
	////////////////////////////////////////TestCases for transformOrder --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for createPcInfoType --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : createPcInfoType(final ObjectFactory pFactory, BBBCommerceItem commerceItem ) ///////////
	
	def"createPcInfoType. This TC is the Happy flow of createPcInfoType"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			PcInfoType pcInfoTypeMock = new PcInfoType()
			objectFactoryMock.createPcInfoType() >> pcInfoTypeMock
			bbbCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "CR"
			auxiliaryDataMock.getProductRef() >> couponRepositoryItem
			1 * couponRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) >> "vendorId"
			1 * catalogToolsMock.getVendorInfo("vendorId") >> vendorInfoVOMock
			vendorInfoVOMock.getVendorName() >> "vendorName"
			bbbCommerceItemMock.getFullImagePath() >> "/images/fullImage/?"
			bbbCommerceItemMock.getThumbnailImagePath() >> "/images/thumbnail/?"
			bbbCommerceItemMock.getReferenceNumber() >> "-1"
			bbbCommerceItemMock.getPersonalizationDetails() >> "details"
			bbbCommerceItemMock.getPersonalizationOptions() >> "personal"
			bbbCommerceItemMock.getMetaDataUrl() >> "/load/details"
			bbbCommerceItemMock.getModerationUrl() >> "load/moderationURL"
			
		when:
			PcInfoType pcInfoTypeResults = testObj.createPcInfoType(objectFactoryMock, bbbCommerceItemMock)
			
		then:
			pcInfoTypeResults == pcInfoTypeMock
			pcInfoTypeResults.getReferenceId() == "-1"
			pcInfoTypeResults.getPcType() == "CR"
			pcInfoTypeResults.getVendorConfiguratorName() == "vendorName"
			pcInfoTypeResults.getImageUrlLarge() == "/images/fullImage/"
			pcInfoTypeResults.getImageUrlSmall() == "/images/thumbnail/"
			pcInfoTypeResults.getPcDescription() == "details"
			pcInfoTypeResults.getServiceType() == "personal"
			pcInfoTypeResults.isMetaDataFlag() == TRUE
			pcInfoTypeResults.getMetaDataUrl() == "/load/details"
			pcInfoTypeResults.isModerationFlag() == TRUE
			pcInfoTypeResults.getModerationUrl() == "load/moderationURL"
			
	}
	
	def"createPcInfoType. This TC is when BBBSystemException throws in getVendorInfo"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			PcInfoType pcInfoTypeMock = new PcInfoType()
			objectFactoryMock.createPcInfoType() >> pcInfoTypeMock
			bbbCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "CR"
			auxiliaryDataMock.getProductRef() >> couponRepositoryItem
			1 * couponRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) >> "vendorId"
			1 * catalogToolsMock.getVendorInfo("vendorId") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			bbbCommerceItemMock.getFullImagePath() >> "/images/fullImage/"
			bbbCommerceItemMock.getReferenceNumber() >> "-1"
			bbbCommerceItemMock.getPersonalizationDetails() >> "details"
			bbbCommerceItemMock.getPersonalizationOptions() >> "personal"
			bbbCommerceItemMock.getMetaDataUrl() >> "/load/details"
			bbbCommerceItemMock.getModerationUrl() >> "load/moderationURL"
			
		when:
			PcInfoType pcInfoTypeResults = testObj.createPcInfoType(objectFactoryMock, bbbCommerceItemMock)
			
		then:
			pcInfoTypeResults == pcInfoTypeMock
			1 * testObj.logError('SubmitOrderMarshaller.createPcInfoType :: BBBSystemException occured while getting vendor name for vendor Id -- vendorId')
			pcInfoTypeResults.getReferenceId() == "-1"
			pcInfoTypeResults.getPcType() == "CR"
			pcInfoTypeResults.getVendorConfiguratorName() == null
			pcInfoTypeResults.getImageUrlLarge() == null
			pcInfoTypeResults.getImageUrlSmall() == null
			pcInfoTypeResults.getPcDescription() == "details"
			pcInfoTypeResults.getServiceType() == "personal"
			pcInfoTypeResults.isMetaDataFlag() == TRUE
			pcInfoTypeResults.getMetaDataUrl() == "/load/details"
			pcInfoTypeResults.isModerationFlag() == TRUE
			pcInfoTypeResults.getModerationUrl() == "load/moderationURL"
			
	}
	
	def"createPcInfoType. This TC is when BBBBusinessException throws in getVendorInfo"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			PcInfoType pcInfoTypeMock = new PcInfoType()
			objectFactoryMock.createPcInfoType() >> pcInfoTypeMock
			bbbCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "CR"
			auxiliaryDataMock.getProductRef() >> couponRepositoryItem
			1 * couponRepositoryItem.getPropertyValue(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME) >> "vendorId"
			1 * catalogToolsMock.getVendorInfo("vendorId") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			bbbCommerceItemMock.getFullImagePath() >> "/images/fullImage/"
			bbbCommerceItemMock.getReferenceNumber() >> "-1"
			bbbCommerceItemMock.getPersonalizationDetails() >> "details"
			bbbCommerceItemMock.getPersonalizationOptions() >> "personal"
			bbbCommerceItemMock.getMetaDataUrl() >> "/load/details"
			bbbCommerceItemMock.getModerationUrl() >> "load/moderationURL"
			
		when:
			PcInfoType pcInfoTypeResults = testObj.createPcInfoType(objectFactoryMock, bbbCommerceItemMock)
			
		then:
			pcInfoTypeResults == pcInfoTypeMock
			1 * testObj.logError('SubmitOrderMarshaller.createPcInfoType :: BBBBusinessException occured while getting vendor name for vendor Id -- vendorId')
			pcInfoTypeResults.getReferenceId() == "-1"
			pcInfoTypeResults.getPcType() == "CR"
			pcInfoTypeResults.getVendorConfiguratorName() == null
			pcInfoTypeResults.getImageUrlLarge() == null
			pcInfoTypeResults.getImageUrlSmall() == null
			pcInfoTypeResults.getPcDescription() == "details"
			pcInfoTypeResults.getServiceType() == "personal"
			pcInfoTypeResults.isMetaDataFlag() == TRUE
			pcInfoTypeResults.getMetaDataUrl() == "/load/details"
			pcInfoTypeResults.isModerationFlag() == TRUE
			pcInfoTypeResults.getModerationUrl() == "load/moderationURL"
			
	}
	
	////////////////////////////////////////TestCases for createPcInfoType --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for createItemInfo --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public ItemType createItemInfo(....) ///////////
	
	def "createItemInfo. This TC is the Happy flow of createItemInfo"(){
		given:
			this.spyCreateItemInfo()
			bbbCommerceItemMock.getId() >> "C12345"
			bbbCommerceItemMock.getCatalogRefId() >> "SKU12345"
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			1 * catalogToolsMock.getJDADeptForSku(bbbOrderImplMock.getSiteId(), bbbCommerceItemMock.getCatalogRefId()) >> "jdaDeptId"
			bbbCommerceItemMock.isVdcInd() >> TRUE
			bbbCommerceItemMock.getReferenceNumber() >> "1"
			testObj.createPcInfoType(objectFactoryMock, bbbCommerceItemMock) >> pcInfoTypeMock
			skuDetailVOMock.getTaxStatus() >> "taxStatus"
			skuDetailVOMock.getFreeShipMethods() >> [shipMethodVOMock]
			bbbOrderImplMock.getSalesChannel() >> "sChannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			testObj.isInternationalSurchargeOn() >> TRUE
			pricingToolsMock.round(10.0, 2) >> 10
			ItemPriceInfoType itemPriceInfoTypeMock1 = Mock()
			ItemPriceInfoType itemPriceInfoTypeMock2 = Mock()
			ItemPriceInfoType itemPriceInfoTypeMock3 = Mock()
			objectFactoryMock.createItemPriceInfoType() >>> [itemPriceInfoTypeMock,itemPriceInfoTypeMock,itemPriceInfoTypeMock1,itemPriceInfoTypeMock2,itemPriceInfoTypeMock3]
			bbbDetailedItemPriceInfoMock.getAmount() >> 30d
			bbbOrderManagerMock.getOrderTools() >> bbbOrderToolsMock
			1 * bbbOrderToolsMock .fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, BBBCoreConstants.DPI_ITEM_SPLIT) >> repositoryItemMock
			bbbOrderImplMock.isOldOrder() >> TRUE
			bbbOrderImplMock.getPropertyValue("internationalOrderId") >> "BBB123456"
			TaxPriceInfo taxPriceInfoMock1 = Mock()
			itemTaxInfoMock = ["C12345" : taxPriceInfoMock, "eco12345" : taxPriceInfoMock1]
			this.populateTaxType()
			bbbHardGoodShippingGroupMock.getShippingAddress() >> addressMock
			2 * addressMock.getState() >> "New Jercy"
			bbbCommerceItemMock.getRegistryId() >> "52323233"
			bbbCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			2 * itemPriceInfoMock.getListPrice() >> 60.99d
			PricingAdjustment pricingAdjustmentMock1 = Mock()
			pricingAdjustmentMock1.getAdjustment() >> 10.99d
			pricingToolsMock.round(50.0,2) >> 50
			bbbDetailedItemPriceInfoMock.getDetailedUnitPrice() >> 50.0d
			pricingToolsMock.round(100,2) >> 100
			bbbCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.COST_DEFAULT) >> ""
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "CR"
			bbbCommerceItemMock.getPersonalizeCost() >> 22.2d
			pricingToolsMock.round(22.2,2) >> 22
			bbbCommerceItemMock.getPersonalizePrice() >> 62.2d
			pricingToolsMock.round(62.2,2) >> 62
			
			//populateTaxInfo Private method Coverage
			objectFactoryMock.createTaxInfoType() >> taxInfoTypeMock
			taxPriceInfoMock.getCityTax() >> 3.2d
			taxPriceInfoMock.getCountyTax() >> 4.2d
			taxPriceInfoMock.getDistrictTax() >> 2.1d
			taxPriceInfoMock.getStateTax() >> 1.1d
			taxPriceInfoMock.getAmount() >> 5.2d
			
			//populatePromotionList Private Method Coverage
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			adjustmentListTypeMock.getAdjustment() >> [adjustmentTypeMock]
			bbbDetailedItemPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock,pricingAdjustmentMock1]
			pricingAdjustmentMock1.getAdjustmentDescription() >> "Sale price"
			pricingAdjustmentMock.getPricingModel() >> repositoryItemMock
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			pricingAdjustmentMock.getCoupon() >> couponRepositoryItem
			couponRepositoryItem.getRepositoryId() >> "WYAARUNK"
			repositoryItemMock.getRepositoryId() >> "repo12345"
			pricingAdjustmentMock.getAdjustment() >> 22.2d
			pricingToolsMock.round(22.2, 2) >> 22.2
			repositoryItemMock.getPropertyValue("typeDetail") >> "adjustmentType"
			repositoryItemMock.getPropertyValue("displayName") >> "adjustmentDesc"
			
			//populateAssemblyFeeInfo Private Method Coverage
			bbbCommerceItemMock.getAssemblyItemId() >> "assemblyCommerceId"
			bbbOrderImplMock.getCommerceItem("assemblyCommerceId") >> assemblyCommerceItemMock
			objectFactoryMock.createAssemblyFeeType() >> assemblyFeeTypeMock
			assemblyCommerceItemMock.getCatalogRefId() >> "SKU12345"
			assemblyCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getAmount() >> 12.2d
			pricingToolsMock.round(12.2/4) >> 3
			pricingToolsMock.round(3.0, 2) >> 3
			pricingToolsMock.round(3.0 * 2) >> 6
			pricingToolsMock.round(6, 2) >> 6
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, TRUE ? BBBCoreConstants.DPI_ASSEMBLY_SPLIT : BBBCoreConstants.DPI_ASSEMBLY) >> dsLineItemRepositoryItemMock
			
			//populateAssemblyItemOverrideAdjustment Private Method Coverage
			PricingAdjustment pricingAdjustmentMock2 = Mock()
			itemPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock1,pricingAdjustmentMock2]
			pricingAdjustmentMock2.getAdjustmentDescription() >> "TBS Price Override"
			pricingAdjustmentMock2.getAdjustment() >> 12.2d
			pricingToolsMock.round(12.2, 2) >> 12
			
			//populateShipSurchargeInfo Private Method Coverage
			bbbCommerceItemMock.getDeliveryItemId() >> "deliveryCommerceId"
			bbbOrderImplMock.getCommerceItem("deliveryCommerceId") >> deliveryCommerceItemMock
			objectFactoryMock.createShipSurchargeType() >> shipSurchargeTypeMock
			deliveryCommerceItemMock.getCatalogRefId() >> "SKU12345"
			deliveryCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getRawTotalPrice() >> 45.2d
			pricingToolsMock.round(11.3, 2) >> 11
			itemPriceInfoTypeMock1.getUnitPrice() >> 12.2d
			
			//populateDiscountedShipSurchargeInfo Private Method Coverage
			pricingToolsMock.round(0.1999999999999993) >> 0.20
			pricingToolsMock.round(3.2) >> 3.5
			pricingToolsMock.round(3.5,2) >> 3.5
			pricingToolsMock.round(7.0,2) >> 7.0
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, TRUE ? BBBCoreConstants.DPI_DELIVERY_SPLIT : BBBCoreConstants.DPI_DELIVERY) >> dsLineItemRepositoryItemMock
			
			//populateEcoFeeItemInfo Private Method Coverage
			Map<String, String> ecoFeeMap = ["C12345":"eco12345"]
			bbbHardGoodShippingGroupMock.getEcoFeeItemMap() >> ecoFeeMap
			objectFactoryMock.createEcoFeeType() >> ecoFeeTypeMock
			bbbOrderImplMock.getCommerceItem("eco12345") >> ecoFeeCommerceItemMock
			taxPriceInfoMock1.getCityTax() >> 6.2d
			taxPriceInfoMock1.getCountyTax() >> 7.2d
			taxPriceInfoMock1.getDistrictTax() >> 8.1d
			taxPriceInfoMock1.getStateTax() >> 9.1d
			taxPriceInfoMock1.getAmount() >> 10.2d
			ecoFeeCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			ecoFeeCommerceItemMock.getCatalogRefId() >> "SKU12345"
			itemPriceInfoMock.isOnSale() >> TRUE
			itemPriceInfoMock.getSalePrice() >> 40.99d
			pricingToolsMock.round(40.99, 2) >> 41
			pricingToolsMock.round(81.98, 2) >> 82
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, TRUE ? BBBCoreConstants.DPI_ECOFEE_SPLIT : BBBCoreConstants.DPI_ECOFEE) >> dsLineItemRepositoryItemMock
		
		when:
			ItemType ItemTypeResults = testObj.createItemInfo(objectFactoryMock, bbbCommerceItemMock, bbbDetailedItemPriceInfoMock, bbbOrderImplMock, bbbHardGoodShippingGroupMock, skuDetailVOMock,
				 3, 4, 5, itemTaxInfoMock, shippingGroupCommerceItemRelationshipMock, taxMapMock, taxMapMock, 2, TRUE, taxMapMock, taxMapMock, taxMapMock, TRUE)

		then:
			ItemTypeResults == itemTypeMock
			taxPriceInfoMock.getCityTax() == 3.2
			taxPriceInfoMock.getCountyTax() == 4.2
			taxPriceInfoMock.getDistrictTax() == 2.1
			taxPriceInfoMock.getStateTax() == 1.1
			taxPriceInfoMock.getAmount() == 5.2
			1 * testObj.logDebug('CommerceItem:C12345 ProRatedDeliveryCommerceId:deliveryCommerceId')
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() End')
			1 * testObj.logDebug('CommerceItem:C12345 AssemblyCommerceId:assemblyCommerceId')
			1 * testObj.logDebug('CommerceItem:C12345 DeliveryCommerceId:deliveryCommerceId')
			3 * testObj.logDebug('Tax info from repository: Mock for type \'RepositoryItem\' named \'dsLineItemRepositoryItemMock\'')
			1 * testObj.logDebug('Method populateShipSurchargeInfo() Start')
			1 * testObj.logDebug('Tax info from repository: Mock for type \'RepositoryItem\' named \'repositoryItemMock\'')
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() Start')
			1 * testObj.logDebug('Method populateDiscountedShipSurchargeInfo() Start')
			2 * adjustmentTypeMock.setAtgPromotionId('PUBpromo999999')
			2 * adjustmentTypeMock.setAdjustmentType('Override')
			1 * itemPriceInfoTypeMock2.setUnitPrice(3.5)
			1 * itemTypeMock.setJdaDeptNum('jdaDeptId')
			1 * itemTypeMock.setAssemblyFee(assemblyFeeTypeMock)
			1 * adjustmentTypeMock.setAdjustmentType('adjustmentType')
			1 * pricingAdjustmentMock1.getPricingModel()
			1 * itemPriceInfoTypeMock.setPcUnitPrice(62.0)
			1 * assemblyFeeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock)
			1 * adjustmentTypeMock.setAdjustmentDesc('adjustmentDesc')
			1 * itemTypeMock.setTaxCD('taxStatus')
			1 * itemPriceInfoTypeMock1.setAdjustmentList(adjustmentListTypeMock)
			1 * assemblyCommerceItemMock.getId()
			1 * itemPriceInfoTypeMock2.setItemTaxInfo(null)
			1 * itemPriceInfoTypeMock.setTotalAmount(6.0)
			1 * itemPriceInfoTypeMock.setPcUnitCost(22.0)
			1 * itemPriceInfoTypeMock3.setUnitPrice(41.0)
			1 * itemPriceInfoTypeMock2.setTotalAmount(7.0)
			1 * itemTypeMock.setDeliverySurcharge(shipSurchargeTypeMock)
			1 * itemTypeMock.setFreeShipping('Y')
			1 * itemPriceInfoTypeMock1.setTotalAmount(24.399999999999998578914528479799628257751464843750)
			1 * catalogToolsMock.isGiftCardItem('BedBathUS', 'SKU12345')
			2 * shipSurchargeTypeMock.setSku('SKU12345')
			1 * itemTypeMock.setIsAssemblyInd(true)
			1 * taxInfoTypeMock.setCityTax(0.0)
			1 * taxInfoTypeMock.setTotalTax(0.0)
			1 * taxInfoTypeMock.setCityTax(11.1)
			1 * taxInfoTypeMock.setDistrictTax(14.1)
			1 * taxInfoTypeMock.setStateTax(0.0)
			1 * taxInfoTypeMock.setDistrictTax(0.0)
			1 * taxInfoTypeMock.setStateTax(15.1)
			1 * taxInfoTypeMock.setTotalTax(12.1)
			1 * taxInfoTypeMock.setCountyTax(13.1)
			1 * taxInfoTypeMock.setCountyTax(0.0)
			1 * itemTypeMock.setIsPcInd(true)
			2 * adjustmentTypeMock.setDiscountAmount(12.0)
			1 * itemPriceInfoTypeMock.setUnitPrice(3.0)
			1 * itemPriceInfoTypeMock.setTotalAmount(100.0)
			1 * itemTypeMock.setIsGiftCard(false)
			1 * itemPriceInfoTypeMock.setVendorCost(22.0)
			1 * itemTypeMock.setSku('SKU12345')
			1 * adjustmentTypeMock.setDiscountAmount(22.0)
			1 * itemPriceInfoTypeMock3.setTotalAmount(82.0)
			1 * itemTypeMock.setRegistryId('52323233')
			1 * itemPriceInfoTypeMock3.setItemTaxInfo(null)
			2 * itemPriceInfoTypeMock.setItemTaxInfo(null)
			1 * adjustmentTypeMock.setCouponCode('WYAARUNK')
			1 * itemTypeMock.setIsAssemblyInd(false)
			1 * itemTypeMock.setAtgCommerceItemId('C12345')
			1 * pricingToolsMock.round(8.1, 2)
			1 * pricingToolsMock.round(9.1, 2)
			1 * pricingToolsMock.round(10.2, 2)
			1 * pricingToolsMock.round(7.2, 2)
			1 * pricingToolsMock.round(6.2, 2)
			1 * ecoFeeTypeMock.setSku('SKU12345')
			1 * itemTypeMock.setIsVdcInd(true)
			1 * itemTypeMock.setProdRatedDeliverySurcharge(shipSurchargeTypeMock)
			1 * itemTypeMock.setEcoFee(ecoFeeTypeMock)
			1 * itemPriceInfoTypeMock.setUnitPrice(50.0)
			1 * shipSurchargeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock1)
			1 * itemTypeMock.setQuantity(2)
			2 * adjustmentTypeMock.setAdjustmentDesc('Override')
			1 * ecoFeeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock3)
			1 * itemTypeMock.setSurchargeAmount(10.0)
			1 * assemblyFeeTypeMock.setSku('SKU12345')
			1 * itemTypeMock.setPcInfo(pcInfoTypeMock)
			2 * itemPriceInfoTypeMock.setAdjustmentList(adjustmentListTypeMock)
			1 * itemTypeMock.setItemPriceInfo(itemPriceInfoTypeMock)
			1 * shipSurchargeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock2)
			1 * itemPriceInfoTypeMock1.setUnitPrice(11.0)
			1 * adjustmentTypeMock.setAtgPromotionId('repo12345')
	}

	def "createItemInfo. This TC is when passing TBSCommerceItem and useSplit as false"(){
		given:
			this.spyCreateItemInfo()
			tbsCommerceItemMock.getId() >> "C12345"
			tbsCommerceItemMock.getCatalogRefId() >> "SKU12345"
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			1 * catalogToolsMock.getJDADeptForSku(bbbOrderImplMock.getSiteId(), tbsCommerceItemMock.getCatalogRefId()) >> null
			tbsCommerceItemMock.isVdcInd() >> TRUE
			tbsCommerceItemMock.getReferenceNumber() >> "-1"
			tbsCommerceItemMock.isCMO() >> TRUE
			skuDetailVOMock.getTaxStatus() >> "status"
			skuDetailVOMock.getFreeShipMethods() >> [shipMethodVOMock]
			bbbOrderImplMock.getSalesChannel() >> "sChannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			testObj.isInternationalSurchargeOn() >> TRUE
			pricingToolsMock.round(10,2) >> 10
			tbsCommerceItemMock.isKirsch() >> TRUE
			tbsCommerceItemMock.getShipTime() >> "shipTime"
			tbsCommerceItemMock.getTBSItemInfo() >> tbsItemInfoMock
			tbsItemInfoMock.getConfigId() >> "configId"
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			skuAttrRelationMock.add(skuAttrRepositoryItemMock)
			skuAttrRelationMock.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION) >> skuAttrRelationMock
			skuAttrRepositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE) >> skuAttributeMock
			skuAttributeMock.getRepositoryId() >> "12_2"
			objectFactoryMock.createItemPriceInfoType() >> itemPriceInfoTypeMock
			bbbDetailedItemPriceInfoMock.getAmount() >> 30d
			bbbOrderManagerMock.getOrderTools() >> bbbOrderToolsMock
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, BBBCoreConstants.DPI_ITEM) >> dsLineItemRepositoryItemMock
			bbbOrderImplMock.isOldOrder() >> FALSE
			bbbOrderImplMock.getPropertyValue("internationalOrderId") >> null
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			2 * itemPriceInfoMock.getListPrice() >> 60.99d
			PricingAdjustment pricingAdjustmentMock1 = Mock()
			itemPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock1]
			pricingToolsMock.round(60.99,2) >> 70
			bbbDetailedItemPriceInfoMock.getDetailedUnitPrice() >> 50.0d
			pricingToolsMock.round(100,2) >> 100
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.COST_DEFAULT) >> "1"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> ""
			tbsItemInfoMock.getCost() >> 26d
			
			//populateTaxInfoFromRepository Private method Coverage
			objectFactoryMock.createTaxInfoType() >> taxInfoTypeMock
			dsLineItemRepositoryItemMock.getPropertyValue(BBBCoreConstants.CITY_TAX) >> 3.2d
			dsLineItemRepositoryItemMock.getPropertyValue(BBBCoreConstants.COUNTY_TAX) >> 4.2d
			dsLineItemRepositoryItemMock.getPropertyValue(BBBCoreConstants.DISTRICT_TAX) >> 2.1d
			dsLineItemRepositoryItemMock.getPropertyValue(BBBCoreConstants.STATE_TAX) >> 1.1d
			pricingToolsMock.round(10.6, 2) >> 11
			
			//populatePromotionList Private Method Coverage
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			adjustmentListTypeMock.getAdjustment() >> [adjustmentTypeMock]
			bbbDetailedItemPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock1]
			pricingAdjustmentMock1.getAdjustmentDescription() >> "TBS Price Override"
			pricingAdjustmentMock1.getAdjustment() >> 10.99d
			pricingToolsMock.round(10.99,2) >> 11
			tbsItemInfoMock.getOverridePrice() >> 12d
			tbsItemInfoMock.getOverideReason() >> "override Reason"
			tbsItemInfoMock.getCompetitor() >> "tbsItemCompetitor"
			
			//populateEcoFeeItemInfo Private Method Coverage
			Map<String, String> ecoFeeMap = ["C12345":"eco12345"]
			bbbStoreShippingGroupMock.getEcoFeeItemMap() >> ecoFeeMap
			itemTaxInfoMock = ["eco12345" : null]
			objectFactoryMock.createEcoFeeType() >> ecoFeeTypeMock
			bbbOrderImplMock.getCommerceItem("eco12345") >> ecoFeeCommerceItemMock
			ecoFeeCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			ecoFeeCommerceItemMock.getCatalogRefId() >> "SKU12345"
			itemPriceInfoMock.isOnSale() >> TRUE
			itemPriceInfoMock.getSalePrice() >> 0.0d
			pricingToolsMock.round(60.99, 2) >> 70
			pricingToolsMock.round(121.98, 2) >> 122
		
		when:
			ItemType ItemTypeResults = testObj.createItemInfo(objectFactoryMock, tbsCommerceItemMock, bbbDetailedItemPriceInfoMock, bbbOrderImplMock, bbbStoreShippingGroupMock, skuDetailVOMock,
				 3, 4, 5, itemTaxInfoMock, shippingGroupCommerceItemRelationshipMock, taxMapMock, taxMapMock, 2, TRUE, taxMapMock, taxMapMock, taxMapMock, FALSE)

		then:
			ItemTypeResults == itemTypeMock
			1 * adjustmentTypeMock.setAtgPromotionId('PUBpromo999999')
			1 * adjustmentTypeMock.setAdjustmentType('Override')
			1 * adjustmentTypeMock.setItemPriceOverrideReasonCode('override Reason')
			1 * adjustmentTypeMock.setItemPriceOverride(12.0)
			1 * adjustmentTypeMock.setDiscountAmount(11.0)
			1 * adjustmentTypeMock.setAdjustmentDesc('Override')
			1 * adjustmentTypeMock.setItemPriceOverrideCompetitor('tbsItemCompetitor')
			1 * taxInfoTypeMock.setTotalTax(11.0)
			1 * taxInfoTypeMock.setDistrictTax(2.1)
			1 * taxInfoTypeMock.setCountyTax(4.2)
			1 * taxInfoTypeMock.setStateTax(1.1)
			1 * taxInfoTypeMock.setCityTax(3.2)
			1 * itemTypeMock.setExternalCart('KIRSCH')
			1 * tbsCommerceItemMock.getPersonalizeCost()
			1 * itemTypeMock.setConfigId('configId')
			1 * itemPriceInfoTypeMock.setAdjustmentList(adjustmentListTypeMock)
			1 * tbsCommerceItemMock.getPersonalizePrice()
			1 * itemTypeMock.setEcoFee(ecoFeeTypeMock)
			1 * tbsCommerceItemMock.getRegistryId()
			1 * itemTypeMock.setQuantity(2)
			1 * ecoFeeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock)
			1 * itemTypeMock.setPresentedLeadTime('shipTime')
			1 * ecoFeeTypeMock.setSku('SKU12345')
			1 * itemPriceInfoTypeMock.setTotalAmount(100.0)
			2 * itemPriceInfoTypeMock.setUnitPrice(70.0)
			1 * testObj.logDebug('Tax info from repository: Mock for type \'RepositoryItem\' named \'dsLineItemRepositoryItemMock\'')
			1 * itemTypeMock.setSku('SKU12345')
			3 * itemTypeMock.setIsVdcInd(true)
			1 * itemTypeMock.setFreeShipping('Y')
			1 * itemPriceInfoTypeMock.setPcUnitCost(0.0)
			1 * itemTypeMock.setSurchargeAmount(10.0)
			1 * itemTypeMock.setItemPriceInfo(itemPriceInfoTypeMock)
			2 * pricingToolsMock.round(0.0, 2)
			1 * catalogToolsMock.isGiftCardItem('BedBathUS', 'SKU12345')
			1 * itemPriceInfoTypeMock.setItemTaxInfo(null)
			1 * itemTypeMock.setAtgCommerceItemId('C12345')
			1 * itemTypeMock.setIsGiftCard(false)
			1 * itemPriceInfoTypeMock.setPcUnitPrice(0.0)
			1 * pricingAdjustmentMock1.getPricingModel()
			1 * itemTypeMock.setTaxCD('status')
			1 * itemPriceInfoTypeMock.setVendorCost(26.0)
			1 * itemPriceInfoTypeMock.setTotalAmount(122.0)
	}

	def "createItemInfo. This TC is when passing TBSCommerceItem and isLastItem is false"(){
		given:
			this.spyCreateItemInfo()
			tbsCommerceItemMock.getId() >> "C12345"
			tbsCommerceItemMock.getCatalogRefId() >> "SKU12345"
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			1 * catalogToolsMock.getJDADeptForSku(bbbOrderImplMock.getSiteId(), tbsCommerceItemMock.getCatalogRefId()) >> null
			tbsCommerceItemMock.isVdcInd() >> TRUE
			tbsCommerceItemMock.getReferenceNumber() >> ""
			tbsCommerceItemMock.isCMO() >> TRUE
			skuDetailVOMock.getTaxStatus() >> ""
			skuDetailVOMock.getFreeShipMethods() >> []
			bbbOrderImplMock.getSalesChannel() >> "sChannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			testObj.isInternationalSurchargeOn() >> FALSE
			pricingToolsMock.round(10,2) >> 10
			tbsCommerceItemMock.isKirsch() >> FALSE
			tbsCommerceItemMock.getShipTime() >> "shipTime"
			tbsCommerceItemMock.getTBSItemInfo() >> null
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			skuAttrRelationMock.add(skuAttrRepositoryItemMock)
			skuAttrRelationMock.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION) >> skuAttrRelationMock
			skuAttrRepositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE) >> skuAttributeMock
			skuAttributeMock.getRepositoryId() >> "12_3"
			objectFactoryMock.createItemPriceInfoType() >> itemPriceInfoTypeMock
			bbbDetailedItemPriceInfoMock.getAmount() >> 30d
			bbbOrderManagerMock.getOrderTools() >> bbbOrderToolsMock
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, BBBCoreConstants.DPI_ITEM) >> dsLineItemRepositoryItemMock
			bbbOrderImplMock.isOldOrder() >> FALSE
			bbbOrderImplMock.getPropertyValue("internationalOrderId") >> "BBB123456"
			bbbStoreShippingGroupMock.getShipOnDate() >> DateUtils.addDays(new Date(), 10)
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			1 * itemPriceInfoMock.getListPrice() >> 60.99d
			PricingAdjustment pricingAdjustmentMock1 = Mock()
			itemPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock1]
			pricingToolsMock.round(60.99,2) >> 70
			bbbDetailedItemPriceInfoMock.getDetailedUnitPrice() >> 50.0d
			pricingToolsMock.round(100,2) >> 100
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.COST_DEFAULT) >> "1"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> ""
			tbsItemInfoMock.getCost() >> 0.0d
			pricingToolsMock.round(1.0,2) >> 1
			
			//populateTaxInfo Private Method Coverage
			itemTaxInfoMock = ["C12345" : taxPriceInfoMock, "eco12345" : null]
			TaxInfoType taxInfoTypeMock = new TaxInfoType()
			objectFactoryMock.createTaxInfoType() >> taxInfoTypeMock
			this.populateTaxType()
			taxPriceInfoMock.getCityTax() >> 1.0d
			taxPriceInfoMock.getCountyTax() >> 1.0d
			taxPriceInfoMock.getDistrictTax() >> 1.0d
			taxPriceInfoMock.getStateTax() >> 1.0d
			taxPriceInfoMock.getAmount() >> 1.0d
			pricingToolsMock.round(10.0, 2) >> 10
			
			//populatePromotionList Private Method Coverage
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			adjustmentListTypeMock.getAdjustment() >> [adjustmentTypeMock]
			bbbDetailedItemPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock1]
			pricingAdjustmentMock1.getAdjustmentDescription() >> "TBS Price Override"
			pricingAdjustmentMock1.getAdjustment() >> 10.99d
			pricingToolsMock.round(10.99,2) >> 11
			tbsItemInfoMock.getOverridePrice() >> 12d
			tbsItemInfoMock.getOverideReason() >> "override Reason"
			tbsItemInfoMock.getCompetitor() >> "tbsItemCompetitor"
			
			//populateEcoFeeItemInfo Private Method Coverage
			Map<String, String> ecoFeeMap = ["C12345":""]
			bbbStoreShippingGroupMock.getEcoFeeItemMap() >> ecoFeeMap
		
		when:
			ItemType ItemTypeResults = testObj.createItemInfo(objectFactoryMock, tbsCommerceItemMock, bbbDetailedItemPriceInfoMock, bbbOrderImplMock, bbbStoreShippingGroupMock, skuDetailVOMock,
				 3, 4, 5, itemTaxInfoMock, shippingGroupCommerceItemRelationshipMock, taxMapMock, taxMapMock, 2, FALSE, taxMapMock, taxMapMock, taxMapMock, FALSE)

		then:
			ItemTypeResults == itemTypeMock
			taxPriceInfoMock.getCityTax() == 1.0
			taxPriceInfoMock.getCountyTax() == 1.0
			taxPriceInfoMock.getDistrictTax() == 1.0
			taxPriceInfoMock.getStateTax() == 1.0
			taxPriceInfoMock.getAmount() == 1.0
			1 * itemPriceInfoTypeMock.setTotalAmount(100.0)
			1 * itemPriceInfoTypeMock.setItemTaxInfo(_)
			1 * itemPriceInfoTypeMock.setPcUnitCost(0.0)
			1 * itemPriceInfoTypeMock.setPcUnitPrice(0.0)
			1 * itemPriceInfoTypeMock.setUnitPrice(70.0)
			1 * itemPriceInfoTypeMock.setVendorCost(1.0)
			1 * itemPriceInfoTypeMock.setAdjustmentList(adjustmentListTypeMock)
			2 * itemTypeMock.setIsVdcInd(true)
			1 * itemTypeMock.setExternalCart('CMO')
			1 * testObj.logDebug('Tax info from repository: Mock for type \'RepositoryItem\' named \'dsLineItemRepositoryItemMock\'')
			1 * skuDetailVOMock.isVdcSku()
			1 * adjustmentTypeMock.setAdjustmentType('Override')
			1 * adjustmentTypeMock.setDiscountAmount(11.0)
			1 * itemTypeMock.setPresentedLeadTime('shipTime')
			1 * itemTypeMock.setSku('SKU12345')
			1 * itemTypeMock.setSurchargeAmount(10.0)
			1 * adjustmentTypeMock.setAtgPromotionId('PUBpromo999999')
			1 * tbsCommerceItemMock.getPersonalizePrice()
			1 * itemTypeMock.setFreeShipping('N')
			2 * pricingToolsMock.round(0.0, 2)
			1 * itemTypeMock.setIsLtlInd(false)
			1 * skuDetailVOMock.isLtlItem()
			1 * itemTypeMock.setAtgCommerceItemId('C12345')
			1 * itemTypeMock.setIsGiftCard(false)
			1 * itemTypeMock.setQuantity(2)
			1 * catalogToolsMock.isGiftCardItem('BedBathUS', 'SKU12345')
			1 * adjustmentTypeMock.setAdjustmentDesc('Override')
			1 * itemTypeMock.setItemPriceInfo(itemPriceInfoTypeMock)
			1 * tbsCommerceItemMock.getPersonalizeCost()
	}
	
	def "createItemInfo. This TC is when passing TBSCommerceItem and SKUDetailVO is not defined"(){
		given:
			this.spyCreateItemInfo()
			tbsCommerceItemMock.getId() >> "C12345"
			tbsCommerceItemMock.getCatalogRefId() >> "SKU12345"
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			1 * catalogToolsMock.getJDADeptForSku(bbbOrderImplMock.getSiteId(), tbsCommerceItemMock.getCatalogRefId()) >> null
			tbsCommerceItemMock.isVdcInd() >> TRUE
			tbsCommerceItemMock.getReferenceNumber() >> null
			tbsCommerceItemMock.isCMO() >> TRUE
			bbbOrderImplMock.getSalesChannel() >> "sChannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			testObj.isInternationalSurchargeOn() >> FALSE
			pricingToolsMock.round(10,2) >> 10
			tbsCommerceItemMock.isKirsch() >> FALSE
			tbsCommerceItemMock.getShipTime() >> "shipTime"
			tbsCommerceItemMock.getTBSItemInfo() >> tbsItemInfoMock
			tbsItemInfoMock.getConfigId() >> "configId"
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			skuAttrRelationMock.add(skuAttrRepositoryItemMock)
			repositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION) >> skuAttrRelationMock
			skuAttrRepositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE) >> null
			objectFactoryMock.createItemPriceInfoType() >> itemPriceInfoTypeMock
			bbbDetailedItemPriceInfoMock.getAmount() >> 30d
			bbbOrderManagerMock.getOrderTools() >> bbbOrderToolsMock
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, BBBCoreConstants.DPI_ITEM) >> null
			bbbOrderImplMock.isOldOrder() >> FALSE
			bbbOrderImplMock.getPropertyValue("internationalOrderId") >> null
			bbbStoreShippingGroupMock.getShipOnDate() >> null
			bbbOrderImplMock.getSubmittedDate() >> DateUtils.addDays(new Date(), 6)
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			1 * itemPriceInfoMock.getListPrice() >> 60.99d
			itemPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock]
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Price Override"
			pricingToolsMock.round(60.99,2) >> 70
			bbbDetailedItemPriceInfoMock.getDetailedUnitPrice() >> 50.0d
			pricingToolsMock.round(100,2) >> 100
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.COST_DEFAULT) >> "1"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> ""
			tbsItemInfoMock.getCost() >> 0.0d
			pricingToolsMock.round(1.0,2) >> 1
			
			//populatePromotionList Private Method Coverage
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			adjustmentListTypeMock.getAdjustment() >> []
			bbbDetailedItemPriceInfoMock.getAdjustments() >> []
			
			//populateEcoFeeItemInfo Private Method Coverage
			Map<String, String> ecoFeeMap = new HashMap<String, String>()
			bbbStoreShippingGroupMock.getEcoFeeItemMap() >> ecoFeeMap
		
		when:
			ItemType ItemTypeResults = testObj.createItemInfo(objectFactoryMock, tbsCommerceItemMock, bbbDetailedItemPriceInfoMock, bbbOrderImplMock, bbbStoreShippingGroupMock, null,
				 3, 4, 5, itemTaxInfoMock, shippingGroupCommerceItemRelationshipMock, taxMapMock, taxMapMock, 2, FALSE, taxMapMock, taxMapMock, taxMapMock, FALSE)

		then:
			ItemTypeResults == itemTypeMock
			1 * itemTypeMock.setIsGiftCard(false)
			1 * itemTypeMock.setPresentedLeadTime('shipTime')
			1 * testObj.vlogDebug('skuAttrRelation :: [Mock for type \'RepositoryItem\' named \'skuAttrRepositoryItemMock\']', [])
			1 * testObj.logDebug('Tax info from repository: null')
			1 * itemTypeMock.setItemPriceInfo(itemPriceInfoTypeMock)
			1 * catalogToolsMock.isGiftCardItem('BedBathUS', 'SKU12345')
			1 * itemTypeMock.setQuantity(2)
			1 * itemTypeMock.setIsLtlInd(false)
			1 * itemTypeMock.setIsVdcInd(true)
			1 * itemTypeMock.setConfigId('configId')
			1 * itemPriceInfoTypeMock.setAdjustmentList(null)
			1 * itemTypeMock.setAtgCommerceItemId('C12345')
			1 * itemPriceInfoTypeMock.setVendorCost(1.0)
			1 * itemPriceInfoTypeMock.setItemTaxInfo(null)
			1 * itemPriceInfoTypeMock.setTotalAmount(100.0)
			1 * itemPriceInfoTypeMock.setUnitPrice(70.0)
			1 * itemTypeMock.setSku('SKU12345')
	}
	
	def "createItemInfo. This TC is when passing TBSCommerceItem and bbbHardGoodShippingGroupMock"(){
		given:
			this.spyCreateItemInfo()
			tbsCommerceItemMock.getId() >> "C12345"
			tbsCommerceItemMock.getCatalogRefId() >> "SKU12345"
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			1 * catalogToolsMock.getJDADeptForSku(bbbOrderImplMock.getSiteId(), tbsCommerceItemMock.getCatalogRefId()) >> null
			tbsCommerceItemMock.isVdcInd() >> TRUE
			tbsCommerceItemMock.getReferenceNumber() >> null
			tbsCommerceItemMock.isCMO() >> TRUE
			skuDetailVOMock.getTaxStatus() >> ""
			skuDetailVOMock.getFreeShipMethods() >> []
			bbbOrderImplMock.getSalesChannel() >> "sChannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			testObj.isInternationalSurchargeOn() >> FALSE
			pricingToolsMock.round(10,2) >> 10
			tbsCommerceItemMock.isKirsch() >> FALSE
			tbsCommerceItemMock.getShipTime() >> "shipTime"
			tbsCommerceItemMock.getTBSItemInfo() >> tbsItemInfoMock
			tbsItemInfoMock.getConfigId() >> "configId"
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			skuAttrRelationMock.add(skuAttrRepositoryItemMock)
			repositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION) >> skuAttrRelationMock
			skuAttrRepositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE) >> skuAttributeMock
			skuAttributeMock.getRepositoryId() >> "12_4"
			ItemPriceInfoType itemPriceInfoTypeMock1 = Mock()
			ItemPriceInfoType itemPriceInfoTypeMock2 = Mock()
			ItemPriceInfoType itemPriceInfoTypeMock3 = Mock()
			ItemPriceInfoType itemPriceInfoTypeMock4 = Mock()
			objectFactoryMock.createItemPriceInfoType() >>> [itemPriceInfoTypeMock,itemPriceInfoTypeMock1,itemPriceInfoTypeMock2,itemPriceInfoTypeMock3,itemPriceInfoTypeMock4]
			bbbHardGoodShippingGroupMock.getShippingAddress() >> null
			skuDetailVOMock.isLtlItem() >> TRUE
			bbbDetailedItemPriceInfoMock.getAmount() >> 30d
			bbbOrderManagerMock.getOrderTools() >> bbbOrderToolsMock
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, BBBCoreConstants.DPI_ITEM) >> null
			bbbOrderImplMock.isOldOrder() >> FALSE
			bbbOrderImplMock.getPropertyValue("internationalOrderId") >> null
			bbbHardGoodShippingGroupMock.getShipOnDate() >> null
			bbbOrderImplMock.getSubmittedDate() >> DateUtils.addDays(new Date(), 6)
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInformationMock
			1 * itemPriceInformationMock.getListPrice() >> 60.99d
			itemPriceInformationMock.getAdjustments() >> [pricingAdjustmentMock]
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Price Override"
			pricingToolsMock.round(60.99,2) >> 70
			bbbDetailedItemPriceInfoMock.getDetailedUnitPrice() >> 50.0d
			pricingToolsMock.round(100,2) >> 100
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.COST_DEFAULT) >> "1"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "PY"
			tbsCommerceItemMock.getPersonalizeCost() >> 22.2d
			pricingToolsMock.round(23.2,2) >> 24
			
			//populatePromotionList Private Method Coverage
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			adjustmentListTypeMock.getAdjustment() >> []
			bbbDetailedItemPriceInfoMock.getAdjustments() >> null
			
			//setEstimatedDeliveryDates Private Method Coverage
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			itemTypeMock.getSku() >> "SKU12345"
			1 * catalogToolsMock.getExpectedDeliveryDateForLTLItem(_, bbbOrderImplMock.getSiteId(), itemTypeMock.getSku(),_, true) >> "22/8 - 28/8"
			
			//populateAssemblyFeeInfo Private Method Coverage
			tbsCommerceItemMock.getAssemblyItemId() >> "assemblyCommerceId"
			bbbOrderImplMock.getCommerceItem("assemblyCommerceId") >> assemblyCommerceItemMock
			objectFactoryMock.createAssemblyFeeType() >> assemblyFeeTypeMock
			assemblyCommerceItemMock.getCatalogRefId() >> "SKU12345"
			assemblyCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			itemPriceInfoMock.getAmount() >> 12.2d
			pricingToolsMock.round(12.2/4) >> 3
			pricingToolsMock.round(3.0, 2) >> 3
			pricingToolsMock.round(3.0 * 2) >> 6
			pricingToolsMock.round(6, 2) >> 6
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, FALSE ? BBBCoreConstants.DPI_ASSEMBLY_SPLIT : BBBCoreConstants.DPI_ASSEMBLY) >> dsLineItemRepositoryItemMock
			
			//populateTaxInfoFromRepository Private method Coverage
			TaxInfoType taxInfoTypeMock = new TaxInfoType()
			objectFactoryMock.createTaxInfoType() >> taxInfoTypeMock
			dsLineItemRepositoryItemMock.getPropertyValue(BBBCoreConstants.CITY_TAX) >> 3.2d
			dsLineItemRepositoryItemMock.getPropertyValue(BBBCoreConstants.COUNTY_TAX) >> 4.2d
			dsLineItemRepositoryItemMock.getPropertyValue(BBBCoreConstants.DISTRICT_TAX) >> 2.1d
			dsLineItemRepositoryItemMock.getPropertyValue(BBBCoreConstants.STATE_TAX) >> 1.1d
			pricingToolsMock.round(10.6, 2) >> 11
			
			//populateAssemblyItemOverrideAdjustment Private Method Coverage
			itemPriceInfoMock.getAdjustments() >> []
			
			//populateShipSurchargeInfo Private Method Coverage
			tbsCommerceItemMock.getDeliveryItemId() >> "deliveryCommerceId"
			bbbOrderImplMock.getCommerceItem("deliveryCommerceId") >> deliveryCommerceItemMock
			objectFactoryMock.createShipSurchargeType() >> shipSurchargeTypeMock
			deliveryCommerceItemMock.getCatalogRefId() >> "SKU12345"
			ItemPriceInfo itemPriceInfoMock1 = Mock()
			deliveryCommerceItemMock.getPriceInfo() >> itemPriceInfoMock1
			itemPriceInfoMock1.getRawTotalPrice() >> 0.0d
			pricingToolsMock.round(0.0, 2) >> 0.0
			itemPriceInfoTypeMock2.getUnitPrice() >> 121212121.2
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, BBBCoreConstants.DPI_DELIVERY) >> null
			
			//populateDeliveryItemOverrideAdjustment Private Method Coverage
			itemPriceInfoMock1.getAdjustments() >> []
			
			//populateDiscountedShipSurchargeInfo Private Method Coverage
			itemPriceInfoMock1.getAmount() >> 32.0d
			pricingToolsMock.round(8.0) >> 8
			pricingToolsMock.round(0.0) >> 0
			pricingToolsMock.round(8.0,2) >> 8
			pricingToolsMock.round(16.0,2) >> 16
			RepositoryItem dsDiscountRepositoryItemMock = Mock()
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, FALSE ? BBBCoreConstants.DPI_DELIVERY_SPLIT : BBBCoreConstants.DPI_DELIVERY) >> dsDiscountRepositoryItemMock
			dsDiscountRepositoryItemMock.getPropertyValue(BBBCoreConstants.CITY_TAX) >> null
			dsDiscountRepositoryItemMock.getPropertyValue(BBBCoreConstants.COUNTY_TAX) >> null
			dsDiscountRepositoryItemMock.getPropertyValue(BBBCoreConstants.DISTRICT_TAX) >> null
			dsDiscountRepositoryItemMock.getPropertyValue(BBBCoreConstants.STATE_TAX) >> null
			
			//populateEcoFeeItemInfo Private Method Coverage
			Map<String, String> ecoFeeMap = ["C12345":"eco12345"]
			bbbHardGoodShippingGroupMock.getEcoFeeItemMap() >> ecoFeeMap
			objectFactoryMock.createEcoFeeType() >> ecoFeeTypeMock
			bbbOrderImplMock.getCommerceItem("eco12345") >> ecoFeeCommerceItemMock
			ecoFeeCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			ecoFeeCommerceItemMock.getCatalogRefId() >> "SKU12345"
			itemPriceInfoMock.isOnSale() >> FALSE
			itemPriceInfoMock.getListPrice() >> 40.99d
			pricingToolsMock.round(40.99, 2) >> 41
			pricingToolsMock.round(81.98, 2) >> 82
			RepositoryItem dsEcoFeeRepositoryItemMock = Mock()
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, FALSE ? BBBCoreConstants.DPI_ECOFEE_SPLIT : BBBCoreConstants.DPI_ECOFEE) >> dsEcoFeeRepositoryItemMock
			dsEcoFeeRepositoryItemMock.getPropertyValue(BBBCoreConstants.CITY_TAX) >> 0.0d
			dsEcoFeeRepositoryItemMock.getPropertyValue(BBBCoreConstants.COUNTY_TAX) >> 0.0d
			dsEcoFeeRepositoryItemMock.getPropertyValue(BBBCoreConstants.DISTRICT_TAX) >> 0.0d
			dsEcoFeeRepositoryItemMock.getPropertyValue(BBBCoreConstants.STATE_TAX) >> 0.0d
		
		when:
			ItemType ItemTypeResults = testObj.createItemInfo(objectFactoryMock, tbsCommerceItemMock, bbbDetailedItemPriceInfoMock, bbbOrderImplMock, bbbHardGoodShippingGroupMock, skuDetailVOMock,
				 3, 4, 5, itemTaxInfoMock, shippingGroupCommerceItemRelationshipMock, taxMapMock, taxMapMock, 2, FALSE, taxMapMock, taxMapMock, taxMapMock, FALSE)

		then:
			ItemTypeResults == itemTypeMock
			1 * itemTypeMock.setProdRatedDeliverySurcharge(shipSurchargeTypeMock)
			1 * itemTypeMock.setIsLtlInd(true)
			1 * itemTypeMock.setPresentedLeadTime('shipTime')
			1 * itemTypeMock.setIsGiftCard(false)
			1 * itemTypeMock.setEstimatedToDeliveryDate('28/8')
			1 * itemTypeMock.setSku('SKU12345')
			1 * itemTypeMock.setIsAssemblyInd(false)
			1 * itemTypeMock.setItemPriceInfo(itemPriceInfoTypeMock)
			1 * itemTypeMock.setIsAssemblyInd(true)
			1 * itemTypeMock.setAtgCommerceItemId('C12345')
			2 * itemTypeMock.setIsVdcInd(true)
			1 * itemTypeMock.setAssemblyFee(assemblyFeeTypeMock)
			1 * itemTypeMock.setQuantity(2)
			1 * itemTypeMock.setEstimatedFromDeliveryDate('22/8')
			1 * itemTypeMock.setEcoFee(ecoFeeTypeMock)
			1 * itemTypeMock.setDeliverySurcharge(shipSurchargeTypeMock)
			1 * itemTypeMock.setFreeShipping('N')
			1 * itemTypeMock.setSurchargeAmount(10.0)
			2 * testObj.logDebug('Tax info from repository: null')
			1 * itemPriceInfoTypeMock4.setTotalAmount(82.0)
			1 * itemPriceInfoTypeMock1.setTotalAmount(6.0)
			1 * testObj.logDebug('Method populateShipSurchargeInfo() Start')
			1 * itemPriceInfoTypeMock1.setAdjustmentList(null)
			1 * shipSurchargeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock2)
			1 * testObj.logDebug('CommerceItem:C12345 ProRatedDeliveryCommerceId:deliveryCommerceId')
			1 * testObj.logDebug('CommerceItem:C12345 AssemblyCommerceId:assemblyCommerceId')
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() Start')
			1 * catalogToolsMock.isGiftCardItem('BedBathUS', 'SKU12345')
			1 * testObj.logDebug('Method populateDiscountedShipSurchargeInfo() Start')
			1 * itemPriceInfoTypeMock.setUnitPrice(70.0)
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() End')
			1 * itemPriceInfoTypeMock3.setUnitPrice(8.0)
			1 * testObj.logDebug('Tax info from repository: Mock for type \'RepositoryItem\' named \'dsDiscountRepositoryItemMock\'')
			1 * itemPriceInfoTypeMock4.setUnitPrice(41.0)
			1 * itemPriceInfoMock.getSalePrice()
			1 * itemPriceInfoTypeMock3.setItemTaxInfo(null)
			1 * itemPriceInfoTypeMock2.setItemTaxInfo(null)
			1 * testObj.logDebug('Delivery Date for sku SKU12345: 22/8 - 28/8')
			1 * itemPriceInfoTypeMock1.setItemTaxInfo(_)
			1 * itemPriceInfoTypeMock.setItemTaxInfo(null)
			1 * assemblyFeeTypeMock.setSku('SKU12345')
			1 * shipSurchargeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock3)
			1 * testObj.vlogDebug('skuAttrRelation :: [Mock for type \'RepositoryItem\' named \'skuAttrRepositoryItemMock\']', [])
			2 * shipSurchargeTypeMock.setSku('SKU12345')
			1 * testObj.logDebug('Tax info from repository: Mock for type \'RepositoryItem\' named \'dsLineItemRepositoryItemMock\'')
			1 * testObj.logDebug('Tax info from repository: Mock for type \'RepositoryItem\' named \'dsEcoFeeRepositoryItemMock\'')
			1 * bbbHardGoodShippingGroupMock.getShippingMethod()
			1 * itemPriceInfoTypeMock3.setTotalAmount(16.0)
			1 * itemPriceInfoTypeMock4.setItemTaxInfo(null)
			1 * itemPriceInfoTypeMock1.setUnitPrice(3.0)
			1 * assemblyFeeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock1)
			1 * ecoFeeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock4)
			1 * testObj.logDebug('CommerceItem:C12345 DeliveryCommerceId:deliveryCommerceId')
			1 * itemPriceInfoTypeMock2.setTotalAmount(242424242.4)
			1 * itemPriceInfoTypeMock.setAdjustmentList(null)
			1 * itemPriceInfoTypeMock.setVendorCost(24.0)
			1 * itemPriceInfoTypeMock2.setUnitPrice(0.0)
			1 * itemPriceInfoTypeMock2.setAdjustmentList(null)
			1 * ecoFeeTypeMock.setSku('SKU12345')
			1 * itemPriceInfoTypeMock.setTotalAmount(100.0)
	}
	
	def "createItemInfo. This TC is when getPriceInfo returns null in all private methods"(){
		given:
			this.spyCreateItemInfo()
			tbsCommerceItemMock.getId() >> "C12345"
			tbsCommerceItemMock.getCatalogRefId() >> "SKU12345"
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			1 * catalogToolsMock.getJDADeptForSku(bbbOrderImplMock.getSiteId(), tbsCommerceItemMock.getCatalogRefId()) >> "jdaDeptId"
			tbsCommerceItemMock.getReferenceNumber() >> null
			skuDetailVOMock.getTaxStatus() >> ""
			skuDetailVOMock.getFreeShipMethods() >> null
			bbbOrderImplMock.getSalesChannel() >> "sChannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			testObj.isInternationalSurchargeOn() >> TRUE
			1 * pricingToolsMock.round(10.0, 2) >> 10
			tbsCommerceItemMock.isKirsch() >> FALSE
			tbsCommerceItemMock.getShipTime() >> "shipTime"
			tbsCommerceItemMock.getTBSItemInfo() >> tbsItemInfoMock
			tbsItemInfoMock.getConfigId() >> "configId"
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			repositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION) >> skuAttrRelationMock
			ItemPriceInfoType itemPriceInfoTypeMock1 = Mock()
			ItemPriceInfoType itemPriceInfoTypeMock2 = Mock()
			ItemPriceInfoType itemPriceInfoTypeMock3 = Mock()
			ItemPriceInfoType itemPriceInfoTypeMock4 = Mock()
			objectFactoryMock.createItemPriceInfoType() >>> [itemPriceInfoTypeMock,itemPriceInfoTypeMock1,itemPriceInfoTypeMock2,itemPriceInfoTypeMock3,itemPriceInfoTypeMock4]
			bbbHardGoodShippingGroupMock.getShippingAddress() >> addressMock
			1 * addressMock.getState() >> ""
			skuDetailVOMock.isLtlItem() >> FALSE
			skuDetailVOMock.isVdcSku() >> TRUE
			bbbDetailedItemPriceInfoMock.getAmount() >> 30d
			bbbOrderManagerMock.getOrderTools() >> bbbOrderToolsMock
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, BBBCoreConstants.DPI_ITEM) >> null
			bbbOrderImplMock.isOldOrder() >> FALSE
			bbbOrderImplMock.getPropertyValue("internationalOrderId") >> null
			bbbHardGoodShippingGroupMock.getShipOnDate() >> null
			bbbOrderImplMock.getSubmittedDate() >> DateUtils.addDays(new Date(), 6)
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInformationMock
			1 * itemPriceInformationMock.getListPrice() >> 60.99d
			itemPriceInformationMock.getAdjustments() >> [pricingAdjustmentMock]
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Price Override"
			pricingToolsMock.round(60.99,2) >> 70
			bbbDetailedItemPriceInfoMock.getDetailedUnitPrice() >> 50.0d
			pricingToolsMock.round(100,2) >> 100
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.COST_DEFAULT) >> "1"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "PB"
			pricingToolsMock.round(1.0,2) >> 1
			
			//populatePromotionList Private Method Coverage
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			adjustmentListTypeMock.getAdjustment() >> []
			bbbDetailedItemPriceInfoMock.getAdjustments() >> []
			
			//setEstimatedDeliveryDates Private Method Coverage
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			itemTypeMock.getSku() >> "SKU12345"
			1 * catalogToolsMock.getExpectedDeliveryTimeVDC(_, itemTypeMock.getSku(), true, null, true) >> " - "
			
			//populateAssemblyFeeInfo Private Method Coverage
			tbsCommerceItemMock.getAssemblyItemId() >> "assemblyCommerceId"
			bbbOrderImplMock.getCommerceItem("assemblyCommerceId") >> assemblyCommerceItemMock
			objectFactoryMock.createAssemblyFeeType() >> assemblyFeeTypeMock
			assemblyCommerceItemMock.getCatalogRefId() >> "SKU12345"
			assemblyCommerceItemMock.getPriceInfo() >>> [null,itemPriceInfoMock]
			
			//populateAssemblyItemOverrideAdjustment Private Method Coverage
			itemPriceInfoMock.getAdjustments() >> []
			
			//populateShipSurchargeInfo Private Method Coverage
			tbsCommerceItemMock.getDeliveryItemId() >> "deliveryCommerceId"
			bbbOrderImplMock.getCommerceItem("deliveryCommerceId") >> deliveryCommerceItemMock
			objectFactoryMock.createShipSurchargeType() >> shipSurchargeTypeMock
			deliveryCommerceItemMock.getCatalogRefId() >> "SKU12345"
			ItemPriceInfo itemPriceInfoMock1 = Mock()
			deliveryCommerceItemMock.getPriceInfo() >> null
			
			//populateEcoFeeItemInfo Private Method Coverage
			Map<String, String> ecoFeeMap = ["C12345":"eco12345"]
			bbbHardGoodShippingGroupMock.getEcoFeeItemMap() >> ecoFeeMap
			objectFactoryMock.createEcoFeeType() >> ecoFeeTypeMock
			bbbOrderImplMock.getCommerceItem("eco12345") >> ecoFeeCommerceItemMock
			ecoFeeCommerceItemMock.getPriceInfo() >> null
		
		when:
			ItemType ItemTypeResults = testObj.createItemInfo(objectFactoryMock, tbsCommerceItemMock, bbbDetailedItemPriceInfoMock, bbbOrderImplMock, bbbHardGoodShippingGroupMock, skuDetailVOMock,
				 3, 4, 5, itemTaxInfoMock, shippingGroupCommerceItemRelationshipMock, taxMapMock, taxMapMock, 2, FALSE, taxMapMock, taxMapMock, taxMapMock, FALSE)

		then:
			ItemTypeResults == itemTypeMock
			1 * itemPriceInfoTypeMock.setUnitPrice(70.0)
			1 * itemTypeMock.setSurchargeAmount(10.0)
			1 * itemPriceInfoTypeMock.setTotalAmount(100.0)
			1 * testObj.logDebug('Method populateDiscountedShipSurchargeInfo() Start')
			1 * testObj.logDebug('Method populateShipSurchargeInfo() Start')
			1 * tbsCommerceItemMock.getRegistryId()
			1 * itemPriceInfoTypeMock1.setAdjustmentList(null)
			1 * itemTypeMock.setIsVdcInd(false)
			1 * itemPriceInfoTypeMock.setVendorCost(1.0)
			1 * assemblyFeeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock1)
			1 * catalogToolsMock.isGiftCardItem('BedBathUS', 'SKU12345')
			1 * itemTypeMock.setConfigId('configId')
			1 * tbsCommerceItemMock.isVdcInd()
			1 * itemTypeMock.setIsGiftCard(false)
			1 * ecoFeeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock4)
			1 * itemTypeMock.setIsAssemblyInd(true)
			1 * itemTypeMock.setJdaDeptNum('jdaDeptId')
			1 * itemTypeMock.setSku('SKU12345')
			1 * itemTypeMock.setIsLtlInd(false)
			1 * itemTypeMock.setItemPriceInfo(itemPriceInfoTypeMock)
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() End')
			1 * itemTypeMock.setDeliverySurcharge(shipSurchargeTypeMock)
			2 * shipSurchargeTypeMock.setSku('SKU12345')
			1 * shipSurchargeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock3)
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() Start')
			1 * itemTypeMock.setIsAssemblyInd(false)
			1 * testObj.logDebug('CommerceItem:C12345 AssemblyCommerceId:assemblyCommerceId')
			1 * itemTypeMock.setProdRatedDeliverySurcharge(shipSurchargeTypeMock)
			1 * itemTypeMock.setQuantity(2)
			1 * itemPriceInfoTypeMock.setAdjustmentList(null)
			1 * shipSurchargeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock2)
			1 * assemblyFeeTypeMock.setSku('SKU12345')
			1 * testObj.logDebug('Tax info from repository: null')
			1 * itemTypeMock.setAssemblyFee(assemblyFeeTypeMock)
			1 * itemPriceInfoTypeMock.setItemTaxInfo(null)
			1 * testObj.logDebug('Delivery Date for shippingMethod SDD:  - ')
			1 * itemTypeMock.setFreeShipping('N')
			1 * testObj.logDebug('CommerceItem:C12345 ProRatedDeliveryCommerceId:deliveryCommerceId')
			1 * itemTypeMock.setAtgCommerceItemId('C12345')
			1 * testObj.logDebug('CommerceItem:C12345 DeliveryCommerceId:deliveryCommerceId')
			1 * itemTypeMock.setPresentedLeadTime('shipTime')
			1 * itemTypeMock.setEcoFee(ecoFeeTypeMock)
	}
	
	def "createItemInfo. This TC is when getCommerceItem is null in populateEcoFeeItemInfo,populateAssemblyFeeInfo private methods"(){
		given:
			this.spyCreateItemInfo()
			tbsCommerceItemMock.getId() >> "C12345"
			tbsCommerceItemMock.getCatalogRefId() >> "SKU12345"
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			1 * catalogToolsMock.getJDADeptForSku(bbbOrderImplMock.getSiteId(), tbsCommerceItemMock.getCatalogRefId()) >> "jdaDeptId"
			tbsCommerceItemMock.getReferenceNumber() >> null
			skuDetailVOMock.getTaxStatus() >> ""
			tbsCommerceItemMock.isCMO() >> TRUE
			skuDetailVOMock.getFreeShipMethods() >> null
			bbbOrderImplMock.getSalesChannel() >> "sChannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			testObj.isInternationalSurchargeOn() >> TRUE
			1 * pricingToolsMock.round(10.0, 2) >> 10
			tbsCommerceItemMock.isKirsch() >> FALSE
			tbsCommerceItemMock.getShipTime() >> "shipTime"
			tbsCommerceItemMock.getTBSItemInfo() >> tbsItemInfoMock
			tbsItemInfoMock.getConfigId() >> "configId"
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			repositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION) >> null
			ItemPriceInfoType itemPriceInfoTypeMock1 = Mock()
			ItemPriceInfoType itemPriceInfoTypeMock2 = Mock()
			ItemPriceInfoType itemPriceInfoTypeMock3 = Mock()
			objectFactoryMock.createItemPriceInfoType() >>> [itemPriceInfoTypeMock,itemPriceInfoTypeMock1,itemPriceInfoTypeMock2,itemPriceInfoTypeMock3]
			bbbHardGoodShippingGroupMock.getShippingAddress() >> addressMock
			addressMock.getState() >> "New Jercy"
			skuDetailVOMock.isLtlItem() >> FALSE
			skuDetailVOMock.isVdcSku() >> FALSE
			bbbDetailedItemPriceInfoMock.getAmount() >> 30d
			bbbOrderManagerMock.getOrderTools() >> bbbOrderToolsMock
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, BBBCoreConstants.DPI_ITEM) >> null
			bbbOrderImplMock.isOldOrder() >> TRUE
			bbbOrderImplMock.getPropertyValue("internationalOrderId") >> null
			bbbHardGoodShippingGroupMock.getShipOnDate() >> null
			bbbOrderImplMock.getSubmittedDate() >> DateUtils.addDays(new Date(), 6)
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInformationMock
			1 * itemPriceInformationMock.getListPrice() >> 60.99d
			itemPriceInformationMock.getAdjustments() >> [pricingAdjustmentMock]
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Price Override"
			pricingToolsMock.round(60.99,2) >> 70
			bbbDetailedItemPriceInfoMock.getDetailedUnitPrice() >> 50.0d
			pricingToolsMock.round(100,2) >> 100
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.COST_DEFAULT) >> "1"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "PB"
			pricingToolsMock.round(1.0,2) >> 1
			
			//populatePromotionList Private Method Coverage
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			adjustmentListTypeMock.getAdjustment() >> []
			bbbDetailedItemPriceInfoMock.getAdjustments() >> []
			
			//populateTaxInfo Private method Coverage
			itemTaxInfoMock = ["C12345" : taxPriceInfoMock]
			
			//setEstimatedDeliveryDates Private Method Coverage
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			1 * catalogToolsMock.getExpectedDeliveryDate(_, "New Jercy" , bbbOrderImplMock.getSiteId(),_, true) >> "-"
			
			//populateAssemblyFeeInfo Private Method Coverage
			tbsCommerceItemMock.getAssemblyItemId() >> "assemblyCommerceId"
			bbbOrderImplMock.getCommerceItem("assemblyCommerceId") >> null
			
			//populateShipSurchargeInfo Private Method Coverage
			tbsCommerceItemMock.getDeliveryItemId() >> "deliveryCommerceId"
			bbbOrderImplMock.getCommerceItem("deliveryCommerceId") >>> [deliveryCommerceItemMock,null]
			objectFactoryMock.createShipSurchargeType() >> shipSurchargeTypeMock
			deliveryCommerceItemMock.getCatalogRefId() >> "SKU12345"
			ItemPriceInfo itemPriceInfoMock1 = Mock()
			deliveryCommerceItemMock.getPriceInfo() >> itemPriceInfoMock1
			itemPriceInfoMock1.getRawTotalPrice() >> 0.0d
			pricingToolsMock.round(0.0, 2) >> 0.0
			itemPriceInfoTypeMock1.getUnitPrice() >> 121212121.2
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, BBBCoreConstants.DPI_DELIVERY) >> null
			
			//populateDeliveryItemOverrideAdjustment Private Method Coverage
			itemPriceInfoMock1.getAdjustments() >> []
			
			//populateEcoFeeItemInfo Private Method Coverage
			Map<String, String> ecoFeeMap = ["C12345":"eco12345"]
			bbbHardGoodShippingGroupMock.getEcoFeeItemMap() >> ecoFeeMap
			objectFactoryMock.createEcoFeeType() >> ecoFeeTypeMock
			bbbOrderImplMock.getCommerceItem("eco12345") >> null
		
		when:
			ItemType ItemTypeResults = testObj.createItemInfo(objectFactoryMock, tbsCommerceItemMock, bbbDetailedItemPriceInfoMock, bbbOrderImplMock, bbbHardGoodShippingGroupMock, skuDetailVOMock,
				 3, 4, 5, itemTaxInfoMock, shippingGroupCommerceItemRelationshipMock, taxMapMock, taxMapMock, 2, FALSE, taxMapMock, taxMapMock, taxMapMock, FALSE)

		then:
			ItemTypeResults == itemTypeMock
			1 * itemTypeMock.setEcoFee(ecoFeeTypeMock)
			1 * itemTypeMock.setIsLtlInd(false)
			1 * itemTypeMock.setSku('SKU12345')
			1 * itemTypeMock.setConfigId('configId')
			1 * itemTypeMock.setAtgCommerceItemId('C12345')
			1 * itemTypeMock.setItemPriceInfo(itemPriceInfoTypeMock)
			1 * itemTypeMock.setDeliverySurcharge(shipSurchargeTypeMock)
			1 * itemTypeMock.setIsVdcInd(false)
			1 * itemTypeMock.setQuantity(2)
			1 * itemTypeMock.setFreeShipping('N')
			1 * itemTypeMock.setIsAssemblyInd(false)
			1 * itemTypeMock.setPresentedLeadTime('shipTime')
			1 * itemTypeMock.setSurchargeAmount(10.0)
			1 * itemTypeMock.setIsGiftCard(false)
			1 * itemTypeMock.setJdaDeptNum('jdaDeptId')
			1 * testObj.logDebug('Method populateDiscountedShipSurchargeInfo() Start')
			1 * itemPriceInfoMock1.getAmount()
			1 * itemPriceInfoTypeMock1.setItemTaxInfo(null)
			1 * itemPriceInfoTypeMock1.setAdjustmentList(null)
			1 * itemPriceInfoTypeMock1.setTotalAmount(242424242.4)
			1 * itemPriceInfoTypeMock.setUnitPrice(70.0)
			1 * testObj.logDebug('Method populateShipSurchargeInfo() Start')
			1 * itemPriceInfoTypeMock.setTotalAmount(100.0)
			1 * shipSurchargeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock1)
			1 * itemPriceInfoTypeMock.setItemTaxInfo(null)
			1 * ecoFeeTypeMock.setItemPriceInfo(itemPriceInfoTypeMock3)
			1 * shipSurchargeTypeMock.setSku('SKU12345')
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() ends with null AssemblyFeeType')
			2 * testObj.logDebug('Tax info from repository: null')
			1 * testObj.logDebug('CommerceItem:C12345 DeliveryCommerceId:deliveryCommerceId')
			1 * tbsCommerceItemMock.isVdcInd()
			1 * testObj.logDebug('CommerceItem:C12345 AssemblyCommerceId:assemblyCommerceId')
			1 * catalogToolsMock.isGiftCardItem('BedBathUS', 'SKU12345')
			1 * itemPriceInfoTypeMock1.setUnitPrice(0.0)
			1 * itemPriceInfoTypeMock.setAdjustmentList(null)
			1 * testObj.logDebug('Delivery Date for shippingMethod SDD: -')
			1 * itemPriceInfoTypeMock.setVendorCost(1.0)
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() ends with null ProRatedShipSurchargeType')
			1 * testObj.logDebug('CommerceItem:C12345 ProRatedDeliveryCommerceId:deliveryCommerceId')
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() Start')
	}
	
	def "createItemInfo. This TC is when passing shippingGroupMock and throws BBBBusinessException in setEstimatedDeliveryDates private Method"(){
		given:
			this.spyCreateItemInfo()
			tbsCommerceItemMock.getId() >> "C12345"
			tbsCommerceItemMock.getCatalogRefId() >> "SKU12345"
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			1 * catalogToolsMock.getJDADeptForSku(bbbOrderImplMock.getSiteId(), tbsCommerceItemMock.getCatalogRefId()) >> "jdaDeptId"
			tbsCommerceItemMock.getReferenceNumber() >> null
			skuDetailVOMock.getTaxStatus() >> ""
			tbsCommerceItemMock.isCMO() >> FALSE
			bbbOrderImplMock.getSalesChannel() >> "sChannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			testObj.isInternationalSurchargeOn() >> TRUE
			tbsCommerceItemMock.isKirsch() >> FALSE
			tbsCommerceItemMock.getShipTime() >> "shipTime"
			tbsCommerceItemMock.getTBSItemInfo() >> tbsItemInfoMock
			tbsItemInfoMock.getConfigId() >> "configId"
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >>> [null,repositoryItemMock]
			objectFactoryMock.createItemPriceInfoType() >> itemPriceInfoTypeMock
			skuDetailVOMock.isLtlItem() >> TRUE
			bbbDetailedItemPriceInfoMock.getAmount() >> 30d
			bbbOrderImplMock.isOldOrder() >> TRUE
			bbbOrderImplMock.getSubmittedDate() >> DateUtils.addDays(new Date(), 6)
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInformationMock
			1 * itemPriceInformationMock.getListPrice() >> 60.99d
			itemPriceInformationMock.getAdjustments() >> [pricingAdjustmentMock]
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Price Override"
			pricingToolsMock.round(60.99,2) >> 70
			bbbDetailedItemPriceInfoMock.getDetailedUnitPrice() >> 50.0d
			pricingToolsMock.round(100,2) >> 100
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.COST_DEFAULT) >> "1"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "PB"
			pricingToolsMock.round(1.0,2) >> 1
			
			//populateTaxInfo Private method Coverage
			itemTaxInfoMock = ["C12345" : taxPriceInfoMock]
			
			//setEstimatedDeliveryDates Private Method Coverage
			shippingGroupMock.getShippingMethod() >> "SDD"
			itemTypeMock.getSku() >> "SKU12345"
			1 * catalogToolsMock.getExpectedDeliveryDateForLTLItem(_, bbbOrderImplMock.getSiteId(), itemTypeMock.getSku(),_, true) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		
		when:
			ItemType ItemTypeResults = testObj.createItemInfo(objectFactoryMock, tbsCommerceItemMock, bbbDetailedItemPriceInfoMock, bbbOrderImplMock, shippingGroupMock, skuDetailVOMock,
				 3, 4, 5, itemTaxInfoMock, shippingGroupCommerceItemRelationshipMock, taxMapMock, taxMapMock, 2, FALSE, taxMapMock, taxMapMock, taxMapMock, FALSE)

		then:
			ItemTypeResults == itemTypeMock
			1 * testObj.logError('Error While invoking getExpectedDeliveryDate method', _)
			1 * itemTypeMock.setQuantity(2)
			1 * itemPriceInfoTypeMock.setTotalAmount(100.0)
			1 * itemTypeMock.setPresentedLeadTime('shipTime')
			1 * itemTypeMock.setIsGiftCard(false)
			1 * itemTypeMock.setJdaDeptNum('jdaDeptId')
			1 * itemTypeMock.setIsLtlInd(true)
			1 * itemTypeMock.setConfigId('configId')
			1 * itemTypeMock.setAtgCommerceItemId('C12345')
			1 * itemPriceInfoTypeMock.setVendorCost(1.0)
			1 * itemTypeMock.setIsVdcInd(false)
			1 * itemTypeMock.setIsVdcInd(true)
			1 * itemTypeMock.setSku('SKU12345')
			1 * catalogToolsMock.isGiftCardItem('BedBathUS', 'SKU12345')
			1 * itemPriceInfoTypeMock.setUnitPrice(70.0)
			1 * itemTypeMock.setItemPriceInfo(itemPriceInfoTypeMock)
	}
	
	def "createItemInfo. This TC is when CommerceItemNotFoundException thrown in populateEcoFeeItemInfo Private Method"(){
		given:
			this.spyCreateItemInfo()
			tbsCommerceItemMock.getId() >> "C12345"
			tbsCommerceItemMock.getCatalogRefId() >> "SKU12345"
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			1 * catalogToolsMock.getJDADeptForSku(bbbOrderImplMock.getSiteId(), tbsCommerceItemMock.getCatalogRefId()) >> null
			tbsCommerceItemMock.isVdcInd() >> TRUE
			tbsCommerceItemMock.getReferenceNumber() >> null
			tbsCommerceItemMock.isCMO() >> TRUE
			skuDetailVOMock.getTaxStatus() >> ""
			skuDetailVOMock.getFreeShipMethods() >> null
			bbbOrderImplMock.getSalesChannel() >> "sChannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			testObj.isInternationalSurchargeOn() >> FALSE
			pricingToolsMock.round(0.0,2) >> 0
			tbsCommerceItemMock.isKirsch() >> FALSE
			tbsCommerceItemMock.getShipTime() >> "shipTime"
			tbsCommerceItemMock.getTBSItemInfo() >> null
			tbsCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			repositoryItemMock.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION) >> null
			objectFactoryMock.createItemPriceInfoType() >> itemPriceInfoTypeMock
			bbbDetailedItemPriceInfoMock.getAmount() >> 30d
			bbbOrderManagerMock.getOrderTools() >> bbbOrderToolsMock
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, BBBCoreConstants.DPI_ITEM) >> dsLineItemRepositoryItemMock
			bbbOrderImplMock.isOldOrder() >> FALSE
			bbbOrderImplMock.getPropertyValue("internationalOrderId") >> null
			tbsCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			1 * itemPriceInfoMock.getListPrice() >> 60.99d
			PricingAdjustment pricingAdjustmentMock1 = Mock()
			itemPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock1]
			pricingAdjustmentMock1.getAdjustmentDescription() >> "List price"
			pricingToolsMock.round(60.99,2) >> 70
			bbbDetailedItemPriceInfoMock.getDetailedUnitPrice() >> 50.0d
			pricingToolsMock.round(100,2) >> 100
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.COST_DEFAULT) >> "1"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> ""
			tbsItemInfoMock.getCost() >> 0.0d
			pricingToolsMock.round(1.0,2) >> 1
			
			//populateTaxInfoFromRepository Private method Coverage
			objectFactoryMock.createTaxInfoType() >> null
			
			//populatePromotionList Private Method Coverage
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			adjustmentListTypeMock.getAdjustment() >> []
			bbbDetailedItemPriceInfoMock.getAdjustments() >> []
			
			//populateEcoFeeItemInfo Private Method Coverage
			Map<String, String> ecoFeeMap = ["C12345":"eco12345"]
			bbbStoreShippingGroupMock.getEcoFeeItemMap() >> ecoFeeMap
			objectFactoryMock.createEcoFeeType() >> ecoFeeTypeMock
			bbbOrderImplMock.getCommerceItem("eco12345") >> {throw new CommerceItemNotFoundException("CommerceItemNotFoundException")}
			
		when:
			ItemType ItemTypeResults = testObj.createItemInfo(objectFactoryMock, tbsCommerceItemMock, bbbDetailedItemPriceInfoMock, bbbOrderImplMock, bbbStoreShippingGroupMock, skuDetailVOMock,
				 3, 4, 5, itemTaxInfoMock, shippingGroupCommerceItemRelationshipMock, taxMapMock, taxMapMock, 2, FALSE, taxMapMock, taxMapMock, taxMapMock, FALSE)

		then:
			ItemTypeResults == null
			thrown BBBSystemException
			1 * itemTypeMock.setSku('SKU12345')
			1 * itemTypeMock.setFreeShipping('N')
			1 * itemTypeMock.setIsGiftCard(false)
			1 * itemTypeMock.setSurchargeAmount(0.0)
			1 * itemTypeMock.setAtgCommerceItemId('C12345')
			2 * itemTypeMock.setIsVdcInd(true)
			1 * itemTypeMock.setPresentedLeadTime('shipTime')
			1 * itemTypeMock.setQuantity(2)
			1 * testObj.logDebug('Tax info from repository: Mock for type \'RepositoryItem\' named \'dsLineItemRepositoryItemMock\'')
			1 * tbsCommerceItemMock.getRegistryId()
			1 * catalogToolsMock.isGiftCardItem('BedBathUS', 'SKU12345')
			1 * itemPriceInfoTypeMock.setAdjustmentList(null)
			1 * itemPriceInfoTypeMock.setUnitPrice(70.0)
			1 * itemPriceInfoTypeMock.setItemTaxInfo(null)
			1 * itemPriceInfoTypeMock.setVendorCost(1.0)
			1 * itemPriceInfoTypeMock.setTotalAmount(100.0)
	}
	
	def "createItemInfo. This TC is when InvalidParameterException thrown in populateEcoFeeItemInfo Private Method"(){
		given:
			this.spyCreateItemInfo()
			bbbCommerceItemMock.getId() >> "C12345"
			bbbCommerceItemMock.getCatalogRefId() >> "SKU12345"
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			1 * catalogToolsMock.getJDADeptForSku(bbbOrderImplMock.getSiteId(), bbbCommerceItemMock.getCatalogRefId()) >> null
			bbbCommerceItemMock.isVdcInd() >> TRUE
			bbbCommerceItemMock.getReferenceNumber() >> null
			bbbOrderImplMock.getSalesChannel() >> "sChannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			bbbCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			objectFactoryMock.createItemPriceInfoType() >> itemPriceInfoTypeMock
			bbbDetailedItemPriceInfoMock.getAmount() >> 30d
			bbbCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			1 * itemPriceInfoMock.getListPrice() >> 60.99d
			PricingAdjustment pricingAdjustmentMock1 = Mock()
			itemPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock1]
			pricingAdjustmentMock1.getAdjustmentDescription() >> "List price"
			pricingToolsMock.round(60.99,2) >> 70
			bbbDetailedItemPriceInfoMock.getDetailedUnitPrice() >> 50.0d
			pricingToolsMock.round(100,2) >> 100
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.COST_DEFAULT) >> "1"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> ""
			pricingToolsMock.round(1.0,2) >> 1
			
			//populatePromotionList Private Method Coverage
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			adjustmentListTypeMock.getAdjustment() >> []
			bbbDetailedItemPriceInfoMock.getAdjustments() >> []
			
			//populateEcoFeeItemInfo Private Method Coverage
			Map<String, String> ecoFeeMap = ["C12345":"eco12345"]
			bbbStoreShippingGroupMock.getEcoFeeItemMap() >> ecoFeeMap
			objectFactoryMock.createEcoFeeType() >> ecoFeeTypeMock
			bbbOrderImplMock.getCommerceItem("eco12345") >> {throw new InvalidParameterException("InvalidParameterException")}
			
		when:
			ItemType ItemTypeResults = testObj.createItemInfo(objectFactoryMock, bbbCommerceItemMock, bbbDetailedItemPriceInfoMock, bbbOrderImplMock, bbbStoreShippingGroupMock, skuDetailVOMock,
				 3, 4, 5, itemTaxInfoMock, shippingGroupCommerceItemRelationshipMock, null, taxMapMock, 2, FALSE, taxMapMock, taxMapMock, taxMapMock, FALSE)

		then:
			ItemTypeResults == null
			thrown BBBSystemException
			1 * itemPriceInfoTypeMock.setTotalAmount(100.0)
			1 * itemPriceInfoTypeMock.setUnitPrice(70.0)
			1 * itemPriceInfoTypeMock.setVendorCost(1.0)
			1 * itemTypeMock.setItemPriceInfo(itemPriceInfoTypeMock)
			1 * itemTypeMock.setIsVdcInd(true)
			1 * catalogToolsMock.isGiftCardItem('BedBathUS', 'SKU12345')
			1 * itemTypeMock.setSku('SKU12345')
			1 * itemTypeMock.setIsGiftCard(false)
			1 * itemTypeMock.setQuantity(2)
			1 * itemTypeMock.setAtgCommerceItemId('C12345')
	}
	
	def "createItemInfo. This TC is when BBBSystemException thrown in setEstimatedDeliveryDates private Method"(){
		given:
			this.spyCreateItemInfo()
			bbbCommerceItemMock.getId() >> "C12345"
			bbbCommerceItemMock.getCatalogRefId() >> "SKU12345"
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			1 * catalogToolsMock.getJDADeptForSku(bbbOrderImplMock.getSiteId(), bbbCommerceItemMock.getCatalogRefId()) >> "jdaDeptId"
			bbbCommerceItemMock.getReferenceNumber() >> null
			skuDetailVOMock.getTaxStatus() >> ""
			skuDetailVOMock.getFreeShipMethods() >> null
			bbbOrderImplMock.getSalesChannel() >> "sChannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> FALSE
			testObj.isInternationalSurchargeOn() >> TRUE
			1 * pricingToolsMock.round(10.0, 2) >> 10
			bbbCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			objectFactoryMock.createItemPriceInfoType() >> itemPriceInfoTypeMock
			bbbHardGoodShippingGroupMock.getShippingAddress() >> addressMock
			1 * addressMock.getState() >> ""
			skuDetailVOMock.isLtlItem() >> TRUE
			bbbDetailedItemPriceInfoMock.getAmount() >> 30d
			bbbOrderManagerMock.getOrderTools() >> bbbOrderToolsMock
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, BBBCoreConstants.DPI_ITEM) >> null
			bbbOrderImplMock.isOldOrder() >> FALSE
			bbbOrderImplMock.getPropertyValue("internationalOrderId") >> null
			bbbHardGoodShippingGroupMock.getShipOnDate() >> null
			bbbOrderImplMock.getSubmittedDate() >> DateUtils.addDays(new Date(), 6)
			bbbCommerceItemMock.getPriceInfo() >> itemPriceInformationMock
			1 * itemPriceInformationMock.getListPrice() >> 60.99d
			itemPriceInformationMock.getAdjustments() >> [pricingAdjustmentMock]
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Price Override"
			pricingToolsMock.round(60.99,2) >> 70
			bbbDetailedItemPriceInfoMock.getDetailedUnitPrice() >> 50.0d
			pricingToolsMock.round(100,2) >> 100
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.COST_DEFAULT) >> "1"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "PB"
			pricingToolsMock.round(1.0,2) >> 1
			
			//populatePromotionList Private Method Coverage
			PricingAdjustment pricingAdjustmentMock1 = Mock()
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			adjustmentListTypeMock.getAdjustment() >> [adjustmentTypeMock]
			bbbDetailedItemPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock1]
			pricingAdjustmentMock1.getAdjustmentDescription() >> "TBS Price Override"
			pricingAdjustmentMock1.getAdjustment() >> 10.99d
			pricingToolsMock.round(10.99,2) >> 11
			
			//setEstimatedDeliveryDates Private Method Coverage
			bbbHardGoodShippingGroupMock.getShippingMethod() >> "SDD"
			itemTypeMock.getSku() >> "SKU12345"
			1 * catalogToolsMock.getExpectedDeliveryDateForLTLItem(_, bbbOrderImplMock.getSiteId(), itemTypeMock.getSku(),_, true) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			//populateAssemblyFeeInfo Private Method Coverage
			bbbCommerceItemMock.getAssemblyItemId() >> ""
			
			//populateShipSurchargeInfo Private Method Coverage
			bbbCommerceItemMock.getDeliveryItemId() >>> ["deliveryCommerceId",""]
			objectFactoryMock.createShipSurchargeType() >> shipSurchargeTypeMock
			bbbOrderImplMock.getCommerceItem("deliveryCommerceId") >> null

			//populateEcoFeeItemInfo Private Method Coverage
			Map<String, String> ecoFeeMap = ["C12345":""]
			bbbStoreShippingGroupMock.getEcoFeeItemMap() >> ecoFeeMap
		
		when:
			ItemType ItemTypeResults = testObj.createItemInfo(objectFactoryMock, bbbCommerceItemMock, bbbDetailedItemPriceInfoMock, bbbOrderImplMock, bbbHardGoodShippingGroupMock, skuDetailVOMock,
				 3, 4, 5, itemTaxInfoMock, shippingGroupCommerceItemRelationshipMock, taxMapMock, taxMapMock, 2, FALSE, taxMapMock, taxMapMock, taxMapMock, FALSE)

		then:
			ItemTypeResults == itemTypeMock
			1 * testObj.logError('Error While invoking getExpectedDeliveryDate method', _)
			1 * itemTypeMock.setItemPriceInfo(itemPriceInfoTypeMock)
			1 * testObj.logDebug('CommerceItem:C12345 AssemblyCommerceId:')
			1 * itemPriceInfoTypeMock.setVendorCost(1.0)
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() Start')
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() ends with null ProRatedShipSurchargeType')
			1 * adjustmentTypeMock.setDiscountAmount(11.0)
			1 * itemPriceInfoTypeMock.setItemTaxInfo(null)
			1 * itemPriceInfoTypeMock.setTotalAmount(100.0)
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() ends with null AssemblyFeeType')
			1 * adjustmentTypeMock.setAdjustmentType('Override')
			1 * adjustmentTypeMock.setAdjustmentDesc('Override')
			1 * testObj.logDebug('CommerceItem:C12345 DeliveryCommerceId:deliveryCommerceId')
			1 * testObj.logDebug('Method populateShipSurchargeInfo() Start')
			1 * testObj.logDebug('Method populateDiscountedShipSurchargeInfo() Start')
			1 * testObj.logDebug('Tax info from repository: null')
			1 * testObj.logDebug('CommerceItem:C12345 ProRatedDeliveryCommerceId:')
			1 * itemPriceInfoTypeMock.setUnitPrice(70.0)
			1 * itemPriceInfoTypeMock.setAdjustmentList(adjustmentListTypeMock)
			1 * adjustmentTypeMock.setAtgPromotionId('PUBpromo999999')
			1 * bbbHardGoodShippingGroupMock.getEcoFeeItemMap()
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() ends with null ShipSurchargeType')
	}
	
	def "createItemInfo. This TC is when CommerceItemNotFoundException throws in populateDiscountedShipSurchargeInfo Private Method"(){
		given:
			this.spyCreateItemInfo()
			bbbCommerceItemMock.getId() >> "C12345"
			bbbCommerceItemMock.getCatalogRefId() >> "SKU12345"
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			1 * catalogToolsMock.getJDADeptForSku(bbbOrderImplMock.getSiteId(), bbbCommerceItemMock.getCatalogRefId()) >> "jdaDeptId"
			bbbCommerceItemMock.getReferenceNumber() >> null
			skuDetailVOMock.getTaxStatus() >> ""
			skuDetailVOMock.getFreeShipMethods() >> null
			bbbOrderImplMock.getSalesChannel() >> "sChannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			testObj.isInternationalSurchargeOn() >> FALSE
			pricingToolsMock.round(0.0, 2) >> 0
			bbbCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			objectFactoryMock.createItemPriceInfoType() >> itemPriceInfoTypeMock
			bbbHardGoodShippingGroupMock.getShippingAddress() >> addressMock
			1 * addressMock.getState() >> ""
			bbbDetailedItemPriceInfoMock.getAmount() >> 30d
			bbbOrderManagerMock.getOrderTools() >> bbbOrderToolsMock
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, BBBCoreConstants.DPI_ITEM) >> null
			bbbOrderImplMock.isOldOrder() >> TRUE
			bbbOrderImplMock.getPropertyValue("internationalOrderId") >> null
			bbbCommerceItemMock.getPriceInfo() >> itemPriceInformationMock
			1 * itemPriceInformationMock.getListPrice() >> 60.99d
			itemPriceInformationMock.getAdjustments() >> [pricingAdjustmentMock]
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Price Override"
			pricingToolsMock.round(60.99,2) >> 70
			bbbDetailedItemPriceInfoMock.getDetailedUnitPrice() >> 50.0d
			pricingToolsMock.round(100,2) >> 100
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.COST_DEFAULT) >> "1"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "PB"
			pricingToolsMock.round(1.0,2) >> 1
			
			//populateTaxInfo Private Method Coverage
			itemTaxInfoMock = ["C12345" : taxPriceInfoMock, "eco12345" : null]
			TaxInfoType taxInfoTypeMock = Mock()
			objectFactoryMock.createTaxInfoType() >> taxInfoTypeMock
			taxPriceInfoMock.getCityTax() >> 1.0d
			taxPriceInfoMock.getCountyTax() >> 1.0d
			taxPriceInfoMock.getDistrictTax() >> 1.0d
			taxPriceInfoMock.getStateTax() >> 1.0d
			taxPriceInfoMock.getAmount() >> 1.0d
			pricingToolsMock.round(10.0, 2) >> 10
			
			//populatePromotionList Private Method Coverage
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			adjustmentListTypeMock.getAdjustment() >> []
			bbbDetailedItemPriceInfoMock.getAdjustments() >> []
			
			//populateAssemblyFeeInfo Private Method Coverage
			bbbCommerceItemMock.getAssemblyItemId() >> ""
			
			//populateShipSurchargeInfo Private Method Coverage
			bbbCommerceItemMock.getDeliveryItemId() >>> ["","deliveryCommerceId"]
			objectFactoryMock.createShipSurchargeType() >> shipSurchargeTypeMock
			
			//populateDiscountedShipSurchargeInfo Private Method Coverage
			bbbOrderImplMock.getCommerceItem("deliveryCommerceId") >> {throw new CommerceItemNotFoundException("Mock for CommerceItemNotFoundException")}

			//populateEcoFeeItemInfo Private Method Coverage
			Map<String, String> ecoFeeMap = ["C12345":""]
			bbbStoreShippingGroupMock.getEcoFeeItemMap() >> ecoFeeMap
		
		when:
			ItemType ItemTypeResults = testObj.createItemInfo(objectFactoryMock, bbbCommerceItemMock, bbbDetailedItemPriceInfoMock, bbbOrderImplMock, bbbHardGoodShippingGroupMock, skuDetailVOMock,
				 3, 4, 5, itemTaxInfoMock, shippingGroupCommerceItemRelationshipMock, null, taxMapMock, 2, FALSE, taxMapMock, taxMapMock, taxMapMock, FALSE)

		then:
			ItemTypeResults == itemTypeMock
			1 * testObj.logError('Exception occured while fetching Commerce Item from order', _)
			1 * taxInfoTypeMock.setDistrictTax(1.0)
			1 * taxInfoTypeMock.setCityTax(1.0)
			1 * taxInfoTypeMock.setCountyTax(1.0)
			1 * taxInfoTypeMock.setTotalTax(1.0)
			1 * taxInfoTypeMock.setStateTax(1.0)
			1 * taxInfoTypeMock.getTotalTax()
			1 * bbbHardGoodShippingGroupMock.getEcoFeeItemMap()
			1 * itemTypeMock.setIsVdcInd(false)
			1 * testObj.logDebug('Method populateDiscountedShipSurchargeInfo() Start')
			1 * testObj.logDebug('CommerceItem:C12345 AssemblyCommerceId:')
			1 * itemTypeMock.setIsGiftCard(false)
			1 * itemPriceInfoTypeMock.setVendorCost(1.0)
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() ends with null ShipSurchargeType')
			1 * testObj.logDebug('Method populateShipSurchargeInfo() Start')
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() Start')
			1 * testObj.logDebug('Method populateAssemblyFeeInfo() ends with null AssemblyFeeType')
			1 * itemTypeMock.setQuantity(2)
			1 * itemTypeMock.setSku('SKU12345')
			1 * itemPriceInfoTypeMock.setItemTaxInfo(null)
			1 * itemTypeMock.setJdaDeptNum('jdaDeptId')
			1 * itemPriceInfoTypeMock.setTotalAmount(100.0)
			1 * testObj.logDebug('CommerceItem:C12345 ProRatedDeliveryCommerceId:deliveryCommerceId')
			1 * itemTypeMock.setFreeShipping('N')
			1 * itemPriceInfoTypeMock.setAdjustmentList(null)
			1 * itemTypeMock.setItemPriceInfo(itemPriceInfoTypeMock)
			1 * testObj.logDebug('Tax info from repository: null')
			1 * testObj.logDebug('CommerceItem:C12345 DeliveryCommerceId:')
			1 * itemTypeMock.setSurchargeAmount(0.0)
			1 * itemPriceInfoTypeMock.setUnitPrice(70.0)
			1 * itemTypeMock.setAtgCommerceItemId('C12345')
			1 * itemTypeMock.setIsAssemblyInd(false)
			1 * catalogToolsMock.isGiftCardItem('BedBathUS', 'SKU12345')
	}
	
	def "createItemInfo. This TC is when InvalidParameterException throws in populateAssemblyFeeInfo Private Method"(){
		given:
			this.spyCreateItemInfo()
			bbbCommerceItemMock.getId() >> "C12345"
			bbbCommerceItemMock.getCatalogRefId() >> "SKU12345"
			bbbOrderImplMock.getSiteId() >> "BedBathUS"
			1 * catalogToolsMock.getJDADeptForSku(bbbOrderImplMock.getSiteId(), bbbCommerceItemMock.getCatalogRefId()) >> "jdaDeptId"
			bbbCommerceItemMock.getReferenceNumber() >> null
			skuDetailVOMock.getTaxStatus() >> ""
			skuDetailVOMock.getFreeShipMethods() >> null
			bbbOrderImplMock.getSalesChannel() >> "sChannel"
			testObj.isInternationalOrder(bbbOrderImplMock.getSalesChannel()) >> TRUE
			testObj.isInternationalSurchargeOn() >> FALSE
			pricingToolsMock.round(0.0, 2) >> 0
			bbbCommerceItemMock.getAuxiliaryData() >> auxiliaryDataMock
			auxiliaryDataMock.getCatalogRef() >> repositoryItemMock
			objectFactoryMock.createItemPriceInfoType() >> itemPriceInfoTypeMock
			bbbHardGoodShippingGroupMock.getShippingAddress() >> addressMock
			1 * addressMock.getState() >> ""
			bbbDetailedItemPriceInfoMock.getAmount() >> 30d
			bbbOrderManagerMock.getOrderTools() >> bbbOrderToolsMock
			1 * bbbOrderToolsMock.fetchTaxInfoFromDPI(bbbDetailedItemPriceInfoMock, BBBCoreConstants.DPI_ITEM) >> null
			bbbOrderImplMock.isOldOrder() >> FALSE
			bbbOrderImplMock.getPropertyValue("internationalOrderId") >> null
			bbbCommerceItemMock.getPriceInfo() >> itemPriceInformationMock
			1 * itemPriceInformationMock.getListPrice() >> 60.99d
			itemPriceInformationMock.getAdjustments() >> [pricingAdjustmentMock]
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Price Override"
			pricingToolsMock.round(60.99,2) >> 70
			bbbDetailedItemPriceInfoMock.getDetailedUnitPrice() >> 50.0d
			pricingToolsMock.round(100,2) >> 100
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.COST_DEFAULT) >> "1"
			1 * repositoryItemMock.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "PB"
			pricingToolsMock.round(1.0,2) >> 1
			
			//populatePromotionList Private Method Coverage
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			adjustmentListTypeMock.getAdjustment() >> []
			bbbDetailedItemPriceInfoMock.getAdjustments() >> []
			
			//populateAssemblyFeeInfo Private Method Coverage
			bbbCommerceItemMock.getAssemblyItemId() >> "assemblyCommerceId"
			objectFactoryMock.createAssemblyFeeType() >> assemblyFeeTypeMock
			bbbOrderImplMock.getCommerceItem("assemblyCommerceId") >> {throw new InvalidParameterException("Mock for InvalidParameterException")}

			//populateEcoFeeItemInfo Private Method Coverage
			Map<String, String> ecoFeeMap = ["C12345":""]
			bbbStoreShippingGroupMock.getEcoFeeItemMap() >> ecoFeeMap
		
		when:
			ItemType ItemTypeResults = testObj.createItemInfo(objectFactoryMock, bbbCommerceItemMock, bbbDetailedItemPriceInfoMock, bbbOrderImplMock, bbbHardGoodShippingGroupMock, skuDetailVOMock,
				 3, 4, 5, itemTaxInfoMock, shippingGroupCommerceItemRelationshipMock, null, taxMapMock, 2, FALSE, taxMapMock, taxMapMock, taxMapMock, FALSE)

		then:
			ItemTypeResults == itemTypeMock
	}
	
	private spyCreateItemInfo() {
		testObj = Spy()
		testObj.setCatalogTools(catalogToolsMock)
		testObj.setPricingTools(pricingToolsMock)
		testObj.setOrderManager(bbbOrderManagerMock)
		objectFactoryMock.createItemType() >> itemTypeMock
	}

	private populateTaxType() {
		taxMapMock.put(SubmitOrderMarshaller.TAX_TYPE.CITY, 11.1)
		pricingToolsMock.round(11.1, 2) >> 11.1
		taxMapMock.put(SubmitOrderMarshaller.TAX_TYPE.AMOUNT, 12.1)
		pricingToolsMock.round(12.1, 2) >> 12.1
		taxMapMock.put(SubmitOrderMarshaller.TAX_TYPE.COUNTY, 13.1)
		pricingToolsMock.round(13.1, 2) >> 13.1
		taxMapMock.put(SubmitOrderMarshaller.TAX_TYPE.DISTRICT, 14.1)
		pricingToolsMock.round(14.1, 2) >> 14.1
		taxMapMock.put(SubmitOrderMarshaller.TAX_TYPE.STATE, 15.1)
		pricingToolsMock.round(15.1, 2) >> 15.1
	}
	
	////////////////////////////////////////TestCases for createItemInfo --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for populateDeliveryItemOverrideAdjustment --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : protected AdjustmentListType populateDeliveryItemOverrideAdjustment(CommerceItem deliveryCommerceItem, ObjectFactory pFactory) ///////////
	
	def"populateDeliveryItemOverrideAdjustment. This TC is when commerceItem is LTLDeliveryChargeCommerceItem"(){
		given:
			ltlDeliveryChargeCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			PricingAdjustment pricingAdjustmentMock1 = Mock()
			itemPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock1,pricingAdjustmentMock]
			pricingAdjustmentMock1.getAdjustmentDescription() >> "Price Override"
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Price Override"
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			adjustmentListTypeMock.getAdjustment() >> [adjustmentTypeMock]
			AdjustmentType adjustmentTypeMock = Mock()
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			pricingAdjustmentMock.getAdjustment() >> 22.2d
			pricingToolsMock.round(22.2,2) >> 22.2
			ltlDeliveryChargeCommerceItemMock.getTBSItemInfo() >> tbsItemInfoMock
			tbsItemInfoMock.getOverridePrice() >> 27.2d
			tbsItemInfoMock.getOverideReason() >> "reason for Override"
			tbsItemInfoMock.getCompetitor() >> "competitor"
			
						
		when:
			AdjustmentListType results = testObj.populateDeliveryItemOverrideAdjustment(ltlDeliveryChargeCommerceItemMock, objectFactoryMock)
		then:
			results == adjustmentListTypeMock
			1 * adjustmentTypeMock.setDiscountAmount(22.2)
			1 * adjustmentTypeMock.setItemSurchargeOverrideCompetitor('competitor')
			1 * adjustmentTypeMock.setAtgPromotionId('PUBpromo999999')
			1 * adjustmentTypeMock.setAdjustmentType('Override')
			1 * adjustmentTypeMock.setItemSurchargeOverrideReasonCode('reason for Override')
			1 * adjustmentTypeMock.setItemSurchargeOverride(27.2)
			1 * adjustmentTypeMock.setAdjustmentDesc('Override')
	}
	
	def"populateDeliveryItemOverrideAdjustment. This TC is when commerceItem is LTLDeliveryChargeCommerceItem and lTbsItemInfo is null"(){
		given:
			ltlDeliveryChargeCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			PricingAdjustment pricingAdjustmentMock1 = Mock()
			itemPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock]
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Price Override"
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			adjustmentListTypeMock.getAdjustment() >> [adjustmentTypeMock]
			AdjustmentType adjustmentTypeMock = Mock()
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			pricingAdjustmentMock.getAdjustment() >> 22.2d
			pricingToolsMock.round(22.2,2) >> 22.2
			ltlDeliveryChargeCommerceItemMock.getTBSItemInfo() >> null
						
		when:
			AdjustmentListType results = testObj.populateDeliveryItemOverrideAdjustment(ltlDeliveryChargeCommerceItemMock, objectFactoryMock)
		then:
			results == adjustmentListTypeMock
			1 * adjustmentTypeMock.setDiscountAmount(22.2)
			1 * adjustmentTypeMock.setAtgPromotionId('PUBpromo999999')
			1 * adjustmentTypeMock.setAdjustmentType('Override')
			1 * adjustmentTypeMock.setAdjustmentDesc('Override')
	}
	
	////////////////////////////////////////TestCases for populateDeliveryItemOverrideAdjustment --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for populateAssemblyItemOverrideAdjustment --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : protected AdjustmentListType populateAssemblyItemOverrideAdjustment(CommerceItem assemblyFeeCommerceItem, ObjectFactory pFactory) ///////////
	
	def"populateAssemblyItemOverrideAdjustment. This TC is when commerceItem is LTLAssemblyFeeCommerceItem"(){
		given:
			ltlAssemblyFeeCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			PricingAdjustment pricingAdjustmentMock1 = Mock()
			itemPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock1,pricingAdjustmentMock]
			pricingAdjustmentMock1.getAdjustmentDescription() >> "Price Override"
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Price Override"
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			adjustmentListTypeMock.getAdjustment() >> [adjustmentTypeMock]
			AdjustmentType adjustmentTypeMock = Mock()
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			pricingAdjustmentMock.getAdjustment() >> 22.2d
			pricingToolsMock.round(22.2,2) >> 22.2
			ltlAssemblyFeeCommerceItemMock.getTBSItemInfo() >> tbsItemInfoMock
			tbsItemInfoMock.getOverridePrice() >> 27.2d
			tbsItemInfoMock.getOverideReason() >> "reason for Override"
			tbsItemInfoMock.getCompetitor() >> "competitor"
			
						
		when:
			AdjustmentListType results = testObj.populateAssemblyItemOverrideAdjustment(ltlAssemblyFeeCommerceItemMock, objectFactoryMock)
		then:
			results == adjustmentListTypeMock
			1 * adjustmentTypeMock.setDiscountAmount(22.2)
			1 * adjustmentTypeMock.setAssemblyFeeOverride(27.2)
			1 * adjustmentTypeMock.setAdjustmentType('Override')
			1 * adjustmentTypeMock.setAtgPromotionId('PUBpromo999999')
			1 * adjustmentTypeMock.setAssemblyFeeOverrideReasonCode('reason for Override')
			1 * adjustmentTypeMock.setAssemblyFeeOverrideCompetitor('competitor')
			1 * adjustmentTypeMock.setAdjustmentDesc('Override')
	}
	
	def"populateAssemblyItemOverrideAdjustment. This TC is when commerceItem is LTLAssemblyFeeCommerceItem and lTbsItemInfo is null"(){
		given:
			ltlAssemblyFeeCommerceItemMock.getPriceInfo() >> itemPriceInfoMock
			PricingAdjustment pricingAdjustmentMock1 = Mock()
			itemPriceInfoMock.getAdjustments() >> [pricingAdjustmentMock]
			pricingAdjustmentMock.getAdjustmentDescription() >> "TBS Price Override"
			objectFactoryMock.createAdjustmentListType() >> adjustmentListTypeMock
			adjustmentListTypeMock.getAdjustment() >> [adjustmentTypeMock]
			AdjustmentType adjustmentTypeMock = Mock()
			objectFactoryMock.createAdjustmentType() >> adjustmentTypeMock
			pricingAdjustmentMock.getAdjustment() >> 22.2d
			pricingToolsMock.round(22.2,2) >> 22.2
			ltlAssemblyFeeCommerceItemMock.getTBSItemInfo() >> null
						
		when:
			AdjustmentListType results = testObj.populateAssemblyItemOverrideAdjustment(ltlAssemblyFeeCommerceItemMock, objectFactoryMock)
		then:
			results == adjustmentListTypeMock
			1 * adjustmentTypeMock.setDiscountAmount(22.2)
			1 * adjustmentTypeMock.setAdjustmentType('Override')
			1 * adjustmentTypeMock.setAtgPromotionId('PUBpromo999999')
			1 * adjustmentTypeMock.setAdjustmentDesc('Override')
	}
	
	////////////////////////////////////////TestCases for populateAssemblyItemOverrideAdjustment --> ENDS//////////////////////////////////////////////////////////
	
		
}
