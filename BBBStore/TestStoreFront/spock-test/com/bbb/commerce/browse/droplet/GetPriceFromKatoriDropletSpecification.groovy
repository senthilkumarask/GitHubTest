package com.bbb.commerce.browse.droplet

import java.util.Map;

import atg.multisite.SiteContextManager;
import atg.servlet.ServletUtil;

import com.bbb.browse.webservice.manager.BBBEximManager
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.browse.manager.ProductManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.commerce.catalog.BBBConfigToolsImpl;
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.rest.catalog.vo.KatoriPriceRestVO
import com.bbb.rest.output.BBBCustomTagComponent
import com.bbb.search.bean.result.BBBDynamicPriceSkuVO

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class GetPriceFromKatoriDropletSpecification extends BBBExtendedSpec {

	def GetPriceFromKatoriDroplet testObj
	def BBBCatalogTools catalogToolsMock = Mock()
	def BBBEximManager eximPricingManagerMock = Mock()
	def BBBCustomTagComponent customTagComponentMock = Mock()
	def ProductManager productManagerMock = Mock()
	def BBBSessionBean bbbSessionBeanMock = Mock()
	def LblTxtTemplateManager lblTxtTemplateManagerMock = Mock()
	def SKUDetailVO skuDetailVOMock = new SKUDetailVO()
	def BBBDynamicPriceSkuVO bbbDynamicPriceSkuVOMock = Mock()
	def BBBCatalogToolsImpl bbbCatalogToolsImplMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	def setup(){
		testObj = new GetPriceFromKatoriDroplet(catalogTools:catalogToolsMock,eximPricingManager:eximPricingManagerMock,customTagComponent:customTagComponentMock,productManager:productManagerMock)
	}
	
	def"service method. This TC is the Happy flow of service method"(){
		given:
			1 * requestMock.getParameter("refNum") >> "refNum"
			1 * requestMock.getParameter(BBBCoreConstants.SKUID) >> "SKU12345"
			1 * productManagerMock.getSKUDetails(_, "SKU12345", false) >> skuDetailVOMock
			2 * requestMock.getParameter("inCartFlag") >> TRUE
			1 * requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> "prod12345"
			KatoriPriceRestVO katoriPriceRestVOMock = new KatoriPriceRestVO(katoriPersonlizedPrice:"50",katoriItemPrice:"12",isErrorExist:FALSE,currencySymbol:"dollar")
			1 * eximPricingManagerMock.getPriceByRef("refNum", "SKU12345",_, TRUE, "prod12345") >> katoriPriceRestVOMock
			skuDetailVOMock.setDynamicSKUPriceVO(bbbDynamicPriceSkuVOMock)
			bbbDynamicPriceSkuVOMock.getInCartFlag() >> TRUE
			1 * catalogToolsMock.getSalePrice(null,"SKU12345") >> 25d
			1 * catalogToolsMock.getListPrice(null,"SKU12345") >> 40d
			skuDetailVOMock.setPersonalizationType("PR")
			bbbDynamicPriceSkuVOMock.getPricingLabelCode() >> "Dollar Label"
			
			//setShipMessageFlag Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> ["true"]
			1 * catalogToolsMock.isSkuLtl(_, "SKU12345") >> FALSE
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> bbbSessionBeanMock
			1 * bbbSessionBeanMock.isInternationalShippingContext() >> FALSE
			testObj.setHighThreshold("15.3")

			1 * requestMock.setParameter("shipMsgFlag", false)
			1 * requestMock.setParameter('eximItemPrice', '12')
			1 * requestMock.setParameter("eximPersonalizedPrice", '50');
			1 * requestMock.setParameter("inCartFlag", TRUE)
			1 * requestMock.setParameter("dynamicPriceSku", FALSE)
			1 * requestMock.setParameter(BBBInternationalShippingConstants.SHOPPERCURRENCY, "dollar")
			1 * requestMock.setParameter("wasPrice", 40.0)
			1 * requestMock.setParameter("priceLabelCode", "Dollar Label")
			1 * requestMock.serviceParameter("output", requestMock,responseMock)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when itemPrice is empty in service method"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setEximPricingManager(eximPricingManagerMock)
			testObj.setProductManager(productManagerMock)
			1 * requestMock.getParameter("refNum") >> "refNum"
			1 * requestMock.getParameter(BBBCoreConstants.SKUID) >> "SKU12345"
			1 * productManagerMock.getSKUDetails(_, "SKU12345", false) >> skuDetailVOMock
			1 * requestMock.getParameter("inCartFlag") >> ""
			1 * requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> "prod12345"
			KatoriPriceRestVO katoriPriceRestVOMock = new KatoriPriceRestVO(katoriPersonlizedPrice:"50",katoriItemPrice:"",isErrorExist:FALSE)
			1 * eximPricingManagerMock.getPriceByRef("refNum", "SKU12345",_, FALSE, "prod12345") >> katoriPriceRestVOMock
			skuDetailVOMock.setDynamicSKUPriceVO(bbbDynamicPriceSkuVOMock)
			bbbDynamicPriceSkuVOMock.getInCartFlag() >> TRUE
			1 * bbbCatalogToolsImplMock.getSalePrice(null,"SKU12345") >> 0
			
			//setShipMessageFlag Private Method Coverage
			1 * bbbCatalogToolsImplMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> ["true"]
			bbbCatalogToolsImplMock.isSkuLtl(_, "SKU12345") >> FALSE
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> bbbSessionBeanMock
			bbbSessionBeanMock.isInternationalShippingContext() >> FALSE
			testObj.getHighThreshold() >> ""
			bbbCatalogToolsImplMock.getLblTxtTemplateManager() >> lblTxtTemplateManagerMock
			1 * lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD,	null, null,_) >> "threshold"
			
			1 * requestMock.setParameter('dynamicPriceSku', false)
			1 * requestMock.setParameter('eximPersonalizedPrice', '50')
			1 * requestMock.setParameter('inCartFlag', true)
			1 * requestMock.setParameter('shopperCurrency', null)
			1 * requestMock.setParameter('eximItemPrice', '')
			1 * testObj.setHighThreshold('threshold')
			1 * testObj.logDebug('Is Dynamic SKU :false')
			1 * testObj.logDebug('InCartFlag for SKU :true')
			1 * requestMock.serviceParameter("output", requestMock,responseMock)
			1 * requestMock.setParameter("shipMsgFlag", false)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when BBBBusinessException thrown in service method"(){
		given:
			testObj = Spy()
			testObj.setEximPricingManager(eximPricingManagerMock)
			testObj.setProductManager(productManagerMock)
			1 * requestMock.getParameter("refNum") >> "refNum"
			1 * requestMock.getParameter(BBBCoreConstants.SKUID) >> "SKU12345"
			1 * productManagerMock.getSKUDetails(_, "SKU12345", false) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			1 * requestMock.getParameter("inCartFlag") >> ""
			1 * requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> "prod12345"
			KatoriPriceRestVO katoriPriceRestVOMock = new KatoriPriceRestVO(katoriPersonlizedPrice:"50",katoriItemPrice:"12",isErrorExist:TRUE,errorMsg:"Error Occurred in Katori")
			1 * eximPricingManagerMock.getPriceByRef("refNum", "SKU12345",_, FALSE, "prod12345") >> katoriPriceRestVOMock
			
			1 * testObj.logError('Error while getting SKUDetailVO in GetPriceFromKatoriDroplet', _)
			1 * requestMock.setParameter("errorMsg", "Error Occurred in Katori")
			1 * requestMock.serviceParameter("error", requestMock,responseMock)
			1 * requestMock.setParameter("shipMsgFlag", false)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when BBBSystemException thrown in setShipMessageFlag Private Method"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setEximPricingManager(eximPricingManagerMock)
			testObj.setProductManager(productManagerMock)
			1 * requestMock.getParameter("refNum") >> "refNum"
			1 * requestMock.getParameter(BBBCoreConstants.SKUID) >> "SKU12345"
			1 * productManagerMock.getSKUDetails(_, "SKU12345", false) >> skuDetailVOMock
			2 * requestMock.getParameter("inCartFlag") >> TRUE
			1 * requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> "prod12345"
			KatoriPriceRestVO katoriPriceRestVOMock = new KatoriPriceRestVO(katoriPersonlizedPrice:"50",katoriItemPrice:"12",isErrorExist:FALSE,currencySymbol:"dollar")
			1 * eximPricingManagerMock.getPriceByRef("refNum", "SKU12345",_, TRUE, "prod12345") >> katoriPriceRestVOMock
			skuDetailVOMock.setDynamicSKUPriceVO(bbbDynamicPriceSkuVOMock)
			bbbDynamicPriceSkuVOMock.getInCartFlag() >> TRUE
			
			1 * catalogToolsMock.getSalePrice(null,"SKU12345") >> 25d
			1 * catalogToolsMock.getListPrice(null,"SKU12345") >> 40d
			skuDetailVOMock.setPersonalizationType("PY")
			bbbDynamicPriceSkuVOMock.getPricingLabelCode() >> "ORIG"
			
			//setShipMessageFlag Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			1 * requestMock.setParameter("priceLabelCode", "")
			1 * testObj.logError('Error while getting config key ShipMsgDisplayFlag value', _)
			1 * requestMock.setParameter("shipMsgFlag", false)
			1 * requestMock.setParameter('eximItemPrice', '12')
			1 * requestMock.setParameter("eximPersonalizedPrice", '50')
			1 * requestMock.setParameter("inCartFlag", TRUE)
			1 * requestMock.setParameter("dynamicPriceSku", FALSE)
			1 * requestMock.setParameter(BBBInternationalShippingConstants.SHOPPERCURRENCY, "dollar")
			1 * requestMock.serviceParameter("output", requestMock,responseMock)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when BBBBusinessException thrown in setShipMessageFlag Private Method"(){
		given:
			1 * requestMock.getParameter("refNum") >> "refNum"
			1 * requestMock.getParameter(BBBCoreConstants.SKUID) >> "SKU12345"
			1 * productManagerMock.getSKUDetails(_, "SKU12345", false) >> skuDetailVOMock
			2 * requestMock.getParameter("inCartFlag") >> TRUE
			1 * requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> "prod12345"
			KatoriPriceRestVO katoriPriceRestVOMock = new KatoriPriceRestVO(katoriPersonlizedPrice:"50",katoriItemPrice:"12",isErrorExist:FALSE,currencySymbol:"dollar")
			1 * eximPricingManagerMock.getPriceByRef("refNum", "SKU12345", _, TRUE, "prod12345") >> katoriPriceRestVOMock
			skuDetailVOMock.setDynamicSKUPriceVO(bbbDynamicPriceSkuVOMock)
			bbbDynamicPriceSkuVOMock.getInCartFlag() >> TRUE
			1 * catalogToolsMock.getSalePrice(null,"SKU12345") >> 25d
			1 * catalogToolsMock.getListPrice(null,"SKU12345") >> 40d
			skuDetailVOMock.setPersonalizationType("PY")
			bbbDynamicPriceSkuVOMock.getPricingLabelCode() >> null
			
			//setShipMessageFlag Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
			0 * requestMock.setParameter("priceLabelCode", "");
			1 * requestMock.setParameter("shipMsgFlag", false)
			1 * requestMock.setParameter('eximItemPrice', '12')
			1 * requestMock.setParameter("eximPersonalizedPrice", '50');
			1 * requestMock.setParameter("inCartFlag", TRUE)
			1 * requestMock.setParameter("dynamicPriceSku", FALSE)
			1 * requestMock.setParameter(BBBInternationalShippingConstants.SHOPPERCURRENCY, "dollar")
			1 * requestMock.serviceParameter("output", requestMock,responseMock)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when getAllValuesForKey returns false"(){
		given:
			1 * requestMock.getParameter("refNum") >> "refNum"
			1 * requestMock.getParameter(BBBCoreConstants.SKUID) >> "SKU12345"
			1 * productManagerMock.getSKUDetails(_, "SKU12345", false) >> skuDetailVOMock
			2 * requestMock.getParameter("inCartFlag") >> TRUE
			1 * requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> "prod12345"
			KatoriPriceRestVO katoriPriceRestVOMock = new KatoriPriceRestVO(katoriPersonlizedPrice:"50",katoriItemPrice:"12",isErrorExist:FALSE,currencySymbol:"dollar")
			1 * eximPricingManagerMock.getPriceByRef("refNum", "SKU12345", _, TRUE, "prod12345") >> katoriPriceRestVOMock
			skuDetailVOMock.setDynamicSKUPriceVO(bbbDynamicPriceSkuVOMock)
			bbbDynamicPriceSkuVOMock.getInCartFlag() >> TRUE
			1 * catalogToolsMock.getSalePrice(null,"SKU12345") >> 25d
			1 * catalogToolsMock.getListPrice(null,"SKU12345") >> 40d
			skuDetailVOMock.setPersonalizationType("PY")
			bbbDynamicPriceSkuVOMock.getPricingLabelCode() >> "Dollar"
			
			//setShipMessageFlag Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> ["false"]

			1 * requestMock.setParameter("shipMsgFlag", false)
			1 * requestMock.setParameter('eximItemPrice', '12')
			1 * requestMock.setParameter("eximPersonalizedPrice", '50');
			1 * requestMock.setParameter("inCartFlag", TRUE)
			1 * requestMock.setParameter("dynamicPriceSku", FALSE)
			1 * requestMock.setParameter(BBBInternationalShippingConstants.SHOPPERCURRENCY, "dollar")
			1 * requestMock.serviceParameter("output", requestMock,responseMock)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when isSkuLtl returns TRUE"(){
		given:
			1 * requestMock.getParameter("refNum") >> "refNum"
			1 * requestMock.getParameter(BBBCoreConstants.SKUID) >> "SKU12345"
			1 * productManagerMock.getSKUDetails(_, "SKU12345", false) >> skuDetailVOMock
			2 * requestMock.getParameter("inCartFlag") >> TRUE
			1 * requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> "prod12345"
			KatoriPriceRestVO katoriPriceRestVOMock = new KatoriPriceRestVO(katoriPersonlizedPrice:"50",katoriItemPrice:"12",isErrorExist:FALSE,currencySymbol:"dollar")
			1 * eximPricingManagerMock.getPriceByRef("refNum", "SKU12345",_, TRUE, "prod12345") >> katoriPriceRestVOMock
			skuDetailVOMock.setDynamicSKUPriceVO(bbbDynamicPriceSkuVOMock)
			bbbDynamicPriceSkuVOMock.getInCartFlag() >> TRUE
			1 * catalogToolsMock.getSalePrice(null,"SKU12345") >> 0
			
			//setShipMessageFlag Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> ["true"]
			1 * catalogToolsMock.isSkuLtl(_, "SKU12345") >> TRUE

			1 * requestMock.setParameter("shipMsgFlag", false)
			1 * requestMock.setParameter('eximItemPrice', '12')
			1 * requestMock.setParameter("eximPersonalizedPrice", '50');
			1 * requestMock.setParameter("inCartFlag", TRUE)
			1 * requestMock.setParameter("dynamicPriceSku", FALSE)
			1 * requestMock.setParameter(BBBInternationalShippingConstants.SHOPPERCURRENCY, "dollar")
			1 * requestMock.serviceParameter("output", requestMock,responseMock)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when isInternationalShippingContext returns TRUE and BBBSystemException thrown in service method"(){
		given:
			1 * requestMock.getParameter("refNum") >> "refNum"
			1 * requestMock.getParameter(BBBCoreConstants.SKUID) >> "SKU12345"
			1 * productManagerMock.getSKUDetails(_, "SKU12345", false) >> skuDetailVOMock
			2 * requestMock.getParameter("inCartFlag") >> TRUE
			1 * requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> "prod12345"
			KatoriPriceRestVO katoriPriceRestVOMock = new KatoriPriceRestVO(katoriPersonlizedPrice:"50",katoriItemPrice:"12",isErrorExist:FALSE,currencySymbol:"dollar")
			1 * eximPricingManagerMock.getPriceByRef("refNum", "SKU12345",_, TRUE, "prod12345") >> katoriPriceRestVOMock
			skuDetailVOMock.setDynamicSKUPriceVO(bbbDynamicPriceSkuVOMock)
			bbbDynamicPriceSkuVOMock.getInCartFlag() >> TRUE
			1 * catalogToolsMock.getSalePrice(null,"SKU12345") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
			//setShipMessageFlag Private Method Coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> ["true"]
			1 * catalogToolsMock.isSkuLtl(_, "SKU12345") >> FALSE
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> bbbSessionBeanMock
			1 * bbbSessionBeanMock.isInternationalShippingContext() >> TRUE

			1 * requestMock.setParameter("shipMsgFlag", false)
			1 * requestMock.setParameter('eximItemPrice', '12')
			1 * requestMock.setParameter("eximPersonalizedPrice", '50');
			1 * requestMock.setParameter("inCartFlag", TRUE)
			1 * requestMock.setParameter("dynamicPriceSku", FALSE)
			1 * requestMock.setParameter(BBBInternationalShippingConstants.SHOPPERCURRENCY, "dollar")
			1 * requestMock.serviceParameter("output", requestMock,responseMock)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when skuId is not defined"(){
		given:
			1 * requestMock.getParameter("refNum") >> "refNum"
			1 * requestMock.getParameter(BBBCoreConstants.SKUID) >> null
			1 * requestMock.getParameter("inCartFlag") >> ""
			1 * requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> "prod12345"
			KatoriPriceRestVO katoriPriceRestVOMock = new KatoriPriceRestVO(katoriPersonlizedPrice:"50",katoriItemPrice:"12",isErrorExist:TRUE,errorMsg:"Error Occurred in Katori")
			1 * eximPricingManagerMock.getPriceByRef("refNum", null, _, FALSE, "prod12345") >> katoriPriceRestVOMock
			
			1 * requestMock.setParameter("errorMsg", "Error Occurred in Katori")
			1 * requestMock.serviceParameter('error', requestMock,responseMock)
			1 * requestMock.setParameter("shipMsgFlag", false)
			
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when skuId is empty"(){
		given:
			1 * requestMock.getParameter("refNum") >> "refNum"
			1 * requestMock.getParameter(BBBCoreConstants.SKUID) >> ""
			1 * requestMock.getParameter("inCartFlag") >> ""
			1 * requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> "prod12345"
			KatoriPriceRestVO katoriPriceRestVOMock = new KatoriPriceRestVO(katoriPersonlizedPrice:"50",katoriItemPrice:"12",isErrorExist:TRUE,errorMsg:"Error Occurred in Katori")
			1 * eximPricingManagerMock.getPriceByRef("refNum", "", _, FALSE, "prod12345") >> katoriPriceRestVOMock
			
			1 * requestMock.setParameter("errorMsg", "Error Occurred in Katori")
			1 * requestMock.serviceParameter('error', requestMock,responseMock)
			1 * requestMock.setParameter("shipMsgFlag", false)
			
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is getHighThreshold is empty"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setEximPricingManager(eximPricingManagerMock)
			testObj.setProductManager(productManagerMock)
			requestMock.getParameter("refNum") >> "refNum"
			requestMock.getParameter(BBBCoreConstants.SKUID) >> "SKU12345"
			productManagerMock.getSKUDetails(_, "SKU12345", false) >> skuDetailVOMock
			requestMock.getParameter("inCartFlag") >> TRUE
			requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> "prod12345"
			KatoriPriceRestVO katoriPriceRestVOMock = new KatoriPriceRestVO(katoriPersonlizedPrice:"50",katoriItemPrice:"12",isErrorExist:FALSE,currencySymbol:"dollar")
			eximPricingManagerMock.getPriceByRef("refNum", "SKU12345",_, TRUE, "prod12345") >> katoriPriceRestVOMock
			skuDetailVOMock.setDynamicSKUPriceVO(bbbDynamicPriceSkuVOMock)
			bbbDynamicPriceSkuVOMock.getInCartFlag() >> TRUE
			bbbCatalogToolsImplMock.getSalePrice(null,"SKU12345") >> 25d
			bbbCatalogToolsImplMock.getListPrice(null,"SKU12345") >> 40d
			skuDetailVOMock.setPersonalizationType("PR")
			bbbDynamicPriceSkuVOMock.getPricingLabelCode() >> "Dollar Label"
			
			//setShipMessageFlag Private Method Coverage
			bbbCatalogToolsImplMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> ["true"]
			bbbCatalogToolsImplMock.isSkuLtl(_, "SKU12345") >> FALSE
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> bbbSessionBeanMock
			bbbSessionBeanMock.isInternationalShippingContext() >> FALSE
			testObj.getHighThreshold() >> ""
			
			bbbCatalogToolsImplMock.getLblTxtTemplateManager() >> lblTxtTemplateManagerMock
			1 * lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD,	null, null,_) >> "threshold"
			Map<String, String> placeholderMap = ["currency":'$',"higherShipThreshhold":"threshold"]
			1 * lblTxtTemplateManagerMock.getPageTextArea(BBBCoreConstants.TXT_FREESHIPPING_PRODUCT, placeholderMap) >> "testArea"

			1 * requestMock.setParameter('shipMsgFlag', true)
			1 * requestMock.setParameter('freeShippingMsg', 'testArea')
			1 * testObj.setHighThreshold('threshold')
			1 * requestMock.setParameter("shipMsgFlag", false)
			1 * requestMock.setParameter('eximItemPrice', '12')
			1 * requestMock.setParameter("eximPersonalizedPrice", '50')
			1 * requestMock.setParameter("inCartFlag", TRUE)
			1 * requestMock.setParameter("dynamicPriceSku", FALSE)
			1 * requestMock.setParameter(BBBInternationalShippingConstants.SHOPPERCURRENCY, "dollar")
			1 * requestMock.setParameter("wasPrice", 40.0)
			1 * requestMock.setParameter("priceLabelCode", "Dollar Label")
			1 * requestMock.serviceParameter("output", requestMock,responseMock)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	def"service method. This TC is when higherShippingThreshhold is blank"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(bbbCatalogToolsImplMock)
			testObj.setEximPricingManager(eximPricingManagerMock)
			testObj.setProductManager(productManagerMock)
			requestMock.getParameter("refNum") >> "refNum"
			requestMock.getParameter(BBBCoreConstants.SKUID) >> "SKU12345"
			productManagerMock.getSKUDetails(_, "SKU12345", false) >> skuDetailVOMock
			requestMock.getParameter("inCartFlag") >> TRUE
			requestMock.getParameter(BBBCoreConstants.PRODUCTID) >> "prod12345"
			KatoriPriceRestVO katoriPriceRestVOMock = new KatoriPriceRestVO(katoriPersonlizedPrice:"50",katoriItemPrice:"12",isErrorExist:FALSE,currencySymbol:"dollar")
			eximPricingManagerMock.getPriceByRef("refNum", "SKU12345", _, TRUE, "prod12345") >> katoriPriceRestVOMock
			skuDetailVOMock.setDynamicSKUPriceVO(bbbDynamicPriceSkuVOMock)
			bbbDynamicPriceSkuVOMock.getInCartFlag() >> TRUE
			bbbCatalogToolsImplMock.getSalePrice(null,"SKU12345") >> 0
			
			//setShipMessageFlag Private Method Coverage
			bbbCatalogToolsImplMock.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS , BBBCoreConstants.SHIP_MSG_DISPLAY_FLAG) >> ["true"]
			bbbCatalogToolsImplMock.isSkuLtl(_, "SKU12345") >> FALSE
			requestMock.resolveName(BBBGiftRegistryConstants.SESSION_BEAN) >> bbbSessionBeanMock
			bbbSessionBeanMock.isInternationalShippingContext() >> FALSE
			testObj.getHighThreshold() >> ""
			bbbCatalogToolsImplMock.getLblTxtTemplateManager() >> lblTxtTemplateManagerMock
			1 * lblTxtTemplateManagerMock.getPageLabel(BBBCoreConstants.LBL_HIGHER_FREE_SHIPPING_THRESHOLD,	null, null,_) >> ""

			1 * testObj.setHighThreshold('')
			1 * requestMock.setParameter("shipMsgFlag", false)
			1 * requestMock.setParameter('eximItemPrice', '12')
			1 * requestMock.setParameter("eximPersonalizedPrice", '50')
			1 * requestMock.setParameter("inCartFlag", TRUE)
			1 * requestMock.setParameter("dynamicPriceSku", FALSE)
			1 * requestMock.setParameter(BBBInternationalShippingConstants.SHOPPERCURRENCY, "dollar")
			1 * requestMock.serviceParameter("output", requestMock,responseMock)
		
		expect:
			testObj.service(requestMock, responseMock)
	}
	
	
}
