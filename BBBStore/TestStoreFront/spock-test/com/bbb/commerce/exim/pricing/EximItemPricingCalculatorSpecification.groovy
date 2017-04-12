package com.bbb.commerce.exim.pricing

import java.util.Locale;
import java.util.Map

import com.bbb.browse.webservice.manager.BBBEximManager
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.exim.bean.ErrorVO
import com.bbb.commerce.exim.bean.EximCustomizedAttributesVO;
import com.bbb.commerce.exim.bean.EximImagePreviewVO
import com.bbb.commerce.exim.bean.EximImagesVO
import com.bbb.constants.BBBCheckoutConstants
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.order.bean.BBBCommerceItem
import com.bbb.order.bean.BBBItemPriceInfo
import com.sun.xml.rpc.processor.modeler.j2ee.xml.pathType;
import com.bbb.commerce.exim.bean.EximSummaryResponseVO;
import atg.commerce.order.AuxiliaryData
import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.DetailedItemPriceInfo
import atg.commerce.pricing.DetailedItemPriceTools
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingAdjustment
import atg.commerce.pricing.PricingException
import atg.commerce.pricing.PricingTools
import atg.repository.RepositoryItem
import spock.lang.specification.BBBExtendedSpec;;

class EximItemPricingCalculatorSpecification extends BBBExtendedSpec {
	
	EximItemPricingCalculator calc
	
	def setup(){
		calc = Spy()
		calc.setCustomizationOfferedSiteMap(null)
	}
	
	def"priceItem,  when site specific customization is true for the sku and katori is enabled across sites and exim pricingrequired flag is true and exim error is true"(){
		
		given:
		def BBBItemPriceInfo pPriceQuote = new BBBItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def DetailedItemPriceInfo old =Mock()
		def DetailedItemPriceInfo new1 =Mock()
		Locale pLocale
		
		pItem.getQuantity() >> 10
		
		calc.setPricingTools(pTools)
		
		Map<String, Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, null)
		
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		1*tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		
		calc.setEximPricingManager(manager)
		1*manager.getKatoriAvailability() >> "true"
		
		pItem.getReferenceNumber() >> "refNumber"
		
		rItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "personalise"
		pItem.getPersonalizePrice() >> 200.0
		pItem.isEximErrorExists() >> true
		pItem.isEximPricingReq() >> true
		
		calc.getEximPersonalizeDetails(pItem,200.0, "tbs") >> 500.0
		
		pItem.getRegistryId() >> "registryId"
		
		pItem.getPrevPrice() >> 300.0
		pPriceQuote.setSalePrice(100.0)
		pTools.round(pItem.getQuantity()*pItem.getPrevPrice()) >> 3000.0
		
		pPriceQuote.getCurrentPriceDetails().add(old)
		pTools.getDetailedItemPriceTools() >>det
		1*det.createInitialDetailedItemPriceInfos(3000.0, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters,"eximPriceAdjustmentDescription") >> [new1]
		
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		1*calc.logDebug("personalizationType [personalise] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		1*pItem.setEximPricingReq(false)
		1*manager.setModerateImageURL(pItem)
		pPriceQuote.getPersonalizedPrice() == 500.0
		pPriceQuote.getRawTotalPrice() == 3000.0
		pPriceQuote.getAmount() == 3000.0
		pPriceQuote.getSalePrice() == 300.0
		pPriceQuote.getCurrentPriceDetails().contains(old) == false
		pPriceQuote.getCurrentPriceDetails().contains(new1) == true
		pPriceQuote.getAdjustments().isEmpty() == false
		PricingAdjustment adj1 = pPriceQuote.getAdjustments().get(0)
		adj1.getAdjustment() == 300.0
	}
	
	def"priceItem, when exim pricingrequired flag is false, exim error is true  along with sale price is equal to zero"(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def DetailedItemPriceInfo new1 =Mock()
		Locale pLocale
		
		pItem.getQuantity() >> 10
		
		calc.setPricingTools(pTools)
		
		EximCustomizedAttributesVO eximVo = new EximCustomizedAttributesVO()
		Map<String,EximCustomizedAttributesVO> eximDetailMap = new HashMap()
		
		Map<String, Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, null)
		pExtraParameters.put(BBBCoreConstants.EXIM_DETAILS_MAP,eximDetailMap)
		
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		1*tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "true"
		
		pItem.getReferenceNumber() >> "refNumber"
		rItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "UX"
		pItem.getPersonalizePrice() >> 200.0
		pItem.isEximErrorExists() >> true
		pItem.isEximPricingReq() >> false
		
		pItem.getRegistryId() >> ""
		pItem.getPrevPrice() >>100.0
		pTools.round(1000.0) >> 1000.0
		pPriceQuote.getCurrentPriceDetails()
		
		pTools.getDetailedItemPriceTools() >>det
		1*det.createInitialDetailedItemPriceInfos(1000.0, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters,"eximPriceAdjustmentDescription") >> [new1]
		
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		1*calc.logDebug("personalizationType [UX] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		0*pItem.setEximPricingReq(false)
		0*manager.setModerateImageURL(pItem)
		pPriceQuote.getRawTotalPrice() == 1000.0
		pPriceQuote.getAmount() == 1000.0
		pPriceQuote.getListPrice()  == 100.0
		pPriceQuote.getCurrentPriceDetails().contains(new1) == true
		pPriceQuote.getAdjustments().isEmpty() == false
		pPriceQuote.getSalePrice() == 0.0
		PricingAdjustment adj1 = pPriceQuote.getAdjustments().get(0)
		adj1.getAdjustment() == 100.0
	}

	
	def"priceItem, when site specific customization is true for the sku and katori is enabled across sites and exim pricingrequired flag is false and exim error is false"(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def DetailedItemPriceInfo new1 =Mock()
		Locale pLocale
		
		pItem.getQuantity() >> 10
		
		calc.setPricingTools(pTools)
		
		EximCustomizedAttributesVO eximVo = new EximCustomizedAttributesVO()
		Map<String,EximCustomizedAttributesVO> eximDetailMap = new HashMap()
		eximDetailMap.put("refNumber",eximVo) 
		
		Map<String, Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, null)
		pExtraParameters.put(BBBCoreConstants.EXIM_DETAILS_MAP,eximDetailMap)
		
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		1*tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "true"
		
		pItem.getReferenceNumber() >> "refNumber"
		
		rItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> BBBCoreConstants.PERSONALIZATION_CODE_CR
		pItem.getPersonalizePrice() >> 200.0
		pItem.isEximErrorExists() >> false
		pItem.isEximPricingReq() >> false
		
		1*manager.setEximPersonalizeDetailsInCI(pItem, eximDetailMap.get("refNumber")) >> 400.0
		
		pItem.getRegistryId() >> ""
		
		pPriceQuote.setSalePrice(100.0)
		pTools.round(_) >> 4000.0
		pPriceQuote.getCurrentPriceDetails()
		pTools.getDetailedItemPriceTools() >>det
		det.createInitialDetailedItemPriceInfos(4000.0, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters,"eximPriceAdjustmentDescription") >> [new1]
		
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		1*calc.logDebug("personalizationType [CR] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		0*pItem.setEximPricingReq(false)
		0*manager.setModerateImageURL(pItem)
		pPriceQuote.getRawTotalPrice() == 4000.0
		pPriceQuote.getAmount() == 4000.0
		pPriceQuote.getListPrice()  == 400.0
		pPriceQuote.getCurrentPriceDetails().contains(new1) == true
		pPriceQuote.getAdjustments().isEmpty() == false
		pPriceQuote.getSalePrice() == 400.0
		PricingAdjustment adj1 = pPriceQuote.getAdjustments().get(0)
		adj1.getAdjustment() == 400.0
	}
	
	def"priceItem, when site specific customization is true for the sku and katori is enabled across sites and exim pricingrequired flag is false and exim error is false and sale price is 0"(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def DetailedItemPriceInfo new1 =Mock()
		Locale pLocale
		
		pItem.getQuantity() >> 10
		calc.setPricingTools(pTools)
		
		EximCustomizedAttributesVO eximVo = new EximCustomizedAttributesVO()
		Map<String,EximCustomizedAttributesVO> eximDetailMap = new HashMap()
		
		Map<String, Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, null)
		pExtraParameters.put(BBBCoreConstants.EXIM_DETAILS_MAP,eximDetailMap)
		
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		1*tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "true"
		
		pItem.getReferenceNumber() >> "refNumber"
		
		rItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> BBBCoreConstants.PERSONALIZATION_CODE_CR
		pItem.getPersonalizePrice() >> 200.0
		pItem.isEximErrorExists() >> false
		pItem.isEximPricingReq() >> false
		
		pItem.getRegistryId() >> ""
		pTools.round(_) >> 2000.0
		
		pTools.getDetailedItemPriceTools() >>det
		1*det.createInitialDetailedItemPriceInfos(2000.0, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters,"eximPriceAdjustmentDescription") >> [new1]
		
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		1*calc.logDebug("personalizationType [CR] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		0*pItem.setEximPricingReq(false)
		0*manager.setModerateImageURL(pItem)
		pPriceQuote.getRawTotalPrice() == 2000.0
		pPriceQuote.getAmount() == 2000.0
		pPriceQuote.getListPrice()  == 200.0
		pPriceQuote.getSalePrice() == 0.0
		pPriceQuote.getCurrentPriceDetails().contains(new1) == true
		pPriceQuote.getAdjustments().isEmpty() == false
		PricingAdjustment adj1 = pPriceQuote.getAdjustments().get(0)
		adj1.getAdjustment() == 200.0
	}
	
	def"priceItem, when eximpricingrequired flag is false, exim error is false and personalizationType is PY along with salePrice greater than zero"(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def DetailedItemPriceInfo new1 =Mock()
		Locale pLocale
		
		pItem.getQuantity() >> 10
		
		calc.setPricingTools(pTools)
		
		EximCustomizedAttributesVO eximVo = new EximCustomizedAttributesVO()
		Map<String,EximCustomizedAttributesVO> eximDetailMap = new HashMap()
		
		Map<String, Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, null)
		pExtraParameters.put(BBBCoreConstants.EXIM_DETAILS_MAP,eximDetailMap)
		
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		1*tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "true"
		
		pItem.getReferenceNumber() >> "refNumber"
		
		rItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "PY"
		pItem.getPersonalizePrice() >> 200.0
		pItem.isEximErrorExists() >> false
		pItem.isEximPricingReq() >> false
		
		pItem.getRegistryId() >> ""
		
		pPriceQuote.setListPrice(500.0)
		pPriceQuote.setSalePrice(100.0)
		pTools.round(7000.0) >> 7000.0
		pTools.round(3000.0) >> 3000.0
		
		pPriceQuote.getCurrentPriceDetails()
		pTools.getDetailedItemPriceTools() >>det
		1*det.createInitialDetailedItemPriceInfos(3000.0, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters,"eximPriceAdjustmentDescription") >> [new1]
		
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		1*calc.logDebug("personalizationType [PY] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		0*pItem.setEximPricingReq(false)
		0*manager.setModerateImageURL(pItem)
		pPriceQuote.getRawTotalPrice() == 7000.0
		pPriceQuote.getAmount() == 3000.0
		pPriceQuote.getListPrice()  == 700.0
		pPriceQuote.getSalePrice() == 300.0
		pPriceQuote.getCurrentPriceDetails().contains(new1) == true
		pPriceQuote.getAdjustments().isEmpty() == false
		PricingAdjustment adj1 = pPriceQuote.getAdjustments().get(0)
		adj1.getAdjustment() == 300.0
	}
	
	def"priceItem, when exim pricingrequired flag is false, exim error is false and personalizationType is PY along with sale price equal to zero"(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def DetailedItemPriceInfo new1 =Mock()
		Locale pLocale
		
		pItem.getQuantity() >> 10
		
		calc.setPricingTools(pTools)
		
		EximCustomizedAttributesVO eximVo = new EximCustomizedAttributesVO()
		Map<String,EximCustomizedAttributesVO> eximDetailMap = new HashMap()
		
		Map<String, Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, null)
		pExtraParameters.put(BBBCoreConstants.EXIM_DETAILS_MAP,eximDetailMap)
		
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "true"
		
		pItem.getReferenceNumber() >> "refNumber"
		
		rItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "PY"
		pItem.getPersonalizePrice() >> 200.0
		pItem.isEximErrorExists() >> false
		pItem.isEximPricingReq() >> false
		
		pItem.getRegistryId() >> ""
		
		pPriceQuote.setListPrice(500.0)
		pTools.round(7000.0) >> 7000.0
		
		pPriceQuote.getCurrentPriceDetails()
		
		pTools.getDetailedItemPriceTools() >>det
		det.createInitialDetailedItemPriceInfos(7000.0, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters,"eximPriceAdjustmentDescription") >> [new1]
		
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		1*calc.logDebug("personalizationType [PY] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		0*pItem.setEximPricingReq(false)
		0*manager.setModerateImageURL(pItem)
		pPriceQuote.getRawTotalPrice() == 7000.0
		pPriceQuote.getAmount() == 7000.0
		pPriceQuote.getListPrice()  == 700.0
		pPriceQuote.getSalePrice() == 0.0
		pPriceQuote.getCurrentPriceDetails().contains(new1) == true
		pPriceQuote.getAdjustments().isEmpty() == false
		PricingAdjustment adj1 = pPriceQuote.getAdjustments().get(0)
		adj1.getAdjustment() == 700.0
	}
	
	def"priceItem, when exim pricingrequired flag is false, exim error is false and personalizationType is UX along with sale price equal greater than zero"(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def DetailedItemPriceInfo new1 =Mock()
		Locale pLocale
		
		pItem.getQuantity() >> 10
		
		calc.setPricingTools(pTools)
		
		EximCustomizedAttributesVO eximVo = new EximCustomizedAttributesVO()
		Map<String,EximCustomizedAttributesVO> eximDetailMap = new HashMap()
		
		Map<String, Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, null)
		pExtraParameters.put(BBBCoreConstants.EXIM_DETAILS_MAP,eximDetailMap)
		
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "true"
		
		pItem.getReferenceNumber() >> "refNumber"
		
		rItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "UX"
		pItem.getPersonalizePrice() >> 200.0
		pItem.isEximErrorExists() >> false
		pItem.isEximPricingReq() >> false
		
		pItem.getRegistryId() >> ""
		
		pPriceQuote.setListPrice(500.0)
		pPriceQuote.setSalePrice(100.0)
		pTools.round(5000.0) >> 5000.0
		pTools.round(1000.0) >> 1000.0
		
		pPriceQuote.getCurrentPriceDetails()
		
		pTools.getDetailedItemPriceTools() >>det
		det.createInitialDetailedItemPriceInfos(1000.0, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters,"eximPriceAdjustmentDescription") >> [new1]
		
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		1*calc.logDebug("personalizationType [UX] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		0*pItem.setEximPricingReq(false)
		0*manager.setModerateImageURL(pItem)
		pPriceQuote.getRawTotalPrice() == 5000.0
		pPriceQuote.getAmount() == 1000.0
		pPriceQuote.getSalePrice() == 100.0
		pPriceQuote.getListPrice()  == 500.0
		pPriceQuote.getCurrentPriceDetails().contains(new1) == true
		pPriceQuote.getAdjustments().isEmpty() == false
		PricingAdjustment adj1 = pPriceQuote.getAdjustments().get(0)
		adj1.getAdjustment() == 100.0
	}
	
	def"priceItem, when BBBBusinessException is thrown while retreiving personalised price"(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		def AuxiliaryData aux =Mock()
		def LblTxtTemplateManager lbl=Mock()
		Locale pLocale
		
		pItem.getQuantity() >> 10
		
		calc.setPricingTools(pTools)
		calc.setLblTxtTemplateManager(lbl)
		1*lbl.getErrMsg(BBBCoreErrorConstants.ERROR_ADD_TO_CART_EXIM, "EN", null, null) >> "error"
		
		Map<String, Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, null)
		
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "true"
		
		pItem.getReferenceNumber() >> "124"
		
		rItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "personalise"
		pItem.getPersonalizePrice() >> 200.0
		pItem.isEximErrorExists() >> true
		pItem.isEximPricingReq() >> true
		
		calc.getEximPersonalizeDetails(pItem,200.0, "tbs") >> {throw new BBBBusinessException("")}
			
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		1*calc.logDebug("personalizationType [personalise] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		0*pItem.setEximPricingReq(true)
		1*pItem.setEximErrorExists(true)
		0*manager.setModerateImageURL(pItem)
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getSalePrice() == 0.0
		pPriceQuote.getAmount() == 0.0
		PricingException pxe =thrown()
	}
	
	def"priceItem, when BBBBusinessException is thrown while calling setModerateImageURL"(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def DetailedItemPriceInfo new1 =Mock()
		def LblTxtTemplateManager lbl=Mock()
		Locale pLocale
		
		pItem.getQuantity() >> 10
		pItem.getRegistryId() >> "regID"
		
		calc.setPricingTools(pTools)
		calc.setLblTxtTemplateManager(lbl)
		lbl.getErrMsg(BBBCoreErrorConstants.ERROR_ADD_TO_CART_EXIM, "EN", null, null) >> "error"
		
		Map<String, Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, null)
		
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "true"
		
		pItem.getReferenceNumber() >> "124"
		
		rItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "CR"
		pItem.getPersonalizePrice() >> 200.0
		pItem.isEximErrorExists() >> true
		pItem.isEximPricingReq() >> false
		
		manager.setModerateImageURL(pItem) >> {throw new BBBBusinessException("")}
		
		pTools.round(_) >> 2000.0
		pTools.getDetailedItemPriceTools() >>det
		det.createInitialDetailedItemPriceInfos(2000.0, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters,"eximPriceAdjustmentDescription") >> [new1]
		
		
			
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		1*calc.logDebug("personalizationType [CR] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		0*pItem.setEximPricingReq(true)
		pPriceQuote.getRawTotalPrice() == 2000.0
		pPriceQuote.getAmount() == 2000.0
		pPriceQuote.getSalePrice() == 0.0
		pPriceQuote.getCurrentPriceDetails().contains(new1) == true
		1*calc.logError("Unable to set the moderated image in the order",_)
	}
	
	def"priceItem, when BBBSystemException is thrown while calling setModerateImageURL"(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def DetailedItemPriceInfo new1 =Mock()
		def LblTxtTemplateManager lbl=Mock()
		Locale pLocale
		
		pItem.getQuantity() >> 10
		pItem.getRegistryId() >> "regID"
		
		calc.setPricingTools(pTools)
		calc.setLblTxtTemplateManager(lbl)
		lbl.getErrMsg(BBBCoreErrorConstants.ERROR_ADD_TO_CART_EXIM, "EN", null, null) >> "error"
		
		Map<String, Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, null)
		
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "true"
		pItem.getReferenceNumber() >> "124"
		
		rItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "CR"
		pItem.getPersonalizePrice() >> 200.0
		pItem.isEximErrorExists() >> true
		pItem.isEximPricingReq() >> false
		
		manager.setModerateImageURL(pItem) >> {throw new BBBSystemException("")}
		
		pTools.round(_) >> 2000.0
		pTools.getDetailedItemPriceTools() >>det
		det.createInitialDetailedItemPriceInfos(2000.0, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters,"eximPriceAdjustmentDescription") >> [new1]
			
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		1*calc.logDebug("personalizationType [CR] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		0*pItem.setEximPricingReq(true)
		pPriceQuote.getRawTotalPrice() == 2000.0
		pPriceQuote.getAmount() == 2000.0
		pPriceQuote.getSalePrice() == 0.0
		pPriceQuote.getCurrentPriceDetails().contains(new1) == true
		1*calc.logError("Unable to set the moderated image in the order",_)
	}
	
	def"priceItem,priceItem, when BBBSystemException is thrown while retreiving personalised price"(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		def AuxiliaryData aux =Mock()
		def LblTxtTemplateManager lbl=Mock()
		Locale pLocale
		
		pItem.getQuantity() >> 10
		
		calc.setPricingTools(pTools)
		calc.setLblTxtTemplateManager(lbl)
		lbl.getErrMsg(BBBCoreErrorConstants.ERROR_ADD_TO_CART_EXIM, "EN", null, null) >> "error"
		
		Map<String, Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, null)
		
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "true"
		
		pItem.getReferenceNumber() >> "124"
		
		rItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "personalise"
		pItem.getPersonalizePrice() >> 200.0
		pItem.isEximErrorExists() >> true
		pItem.isEximPricingReq() >> true
		
		calc.getEximPersonalizeDetails(pItem,200.0, "tbs") >> {throw new BBBSystemException("")}
			
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		1*calc.logDebug("personalizationType [personalise] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		0*pItem.setEximPricingReq(true)
		1*pItem.setEximErrorExists(true)
		0*manager.setModerateImageURL(pItem)
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getSalePrice() == 0.0
		pPriceQuote.getAmount() == 0.0
		PricingException pxe =thrown()
	}
	
	def"priceItem, when exim pricingrequired flag is false, exim error is false and personalizationType is UX along with sale price is equal to zero"(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def DetailedItemPriceInfo new1 =Mock()
		Locale pLocale
		
		pItem.getQuantity() >> 10
		
		calc.setPricingTools(pTools)
		
		EximCustomizedAttributesVO eximVo = new EximCustomizedAttributesVO()
		Map<String, Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, null)
		pExtraParameters.put(BBBCoreConstants.EXIM_DETAILS_MAP,null)
		
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "true"
		pItem.getReferenceNumber() >> "ref"
		rItem.getPropertyValue(BBBCoreConstants.PERSONALIZATION_TYPE) >> "UX"
		pItem.getPersonalizePrice() >> 200.0
		pItem.isEximErrorExists() >> false
		pItem.isEximPricingReq() >> false
		
		pItem.getRegistryId() >> ""
		pPriceQuote.setListPrice(500.0)
		pTools.round(5000.0) >> 5000.0
		pPriceQuote.getCurrentPriceDetails()
		
		pTools.getDetailedItemPriceTools() >>det
		det.createInitialDetailedItemPriceInfos(5000.0, pPriceQuote, pItem, pPricingModel, pLocale, pProfile, pExtraParameters,"eximPriceAdjustmentDescription") >> [new1]
		
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		1*calc.logDebug("personalizationType [UX] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		0*pItem.setEximPricingReq(false)
		0*manager.setModerateImageURL(pItem)
		pPriceQuote.getRawTotalPrice() == 5000.0
		pPriceQuote.getAmount() == 5000.0
		pPriceQuote.getListPrice()  == 500.0
		pPriceQuote.getSalePrice() == 0.0
		pPriceQuote.getCurrentPriceDetails().contains(new1) == true
		pPriceQuote.getAdjustments().isEmpty() == false
		PricingAdjustment adj1 = pPriceQuote.getAdjustments().get(0)
		adj1.getAdjustment() == 500.0
	}
	
	def"priceItem, when enableKatoriFlag is false "(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		Locale pLocale
		
		calc.setPricingTools(pTools)
		Map<String, Object> pExtraParameters = new HashMap()
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "false"
		pItem.getReferenceNumber() >> "ref"
		
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		0*calc.logDebug("personalizationType [UX] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		0*pItem.setEximPricingReq(false)
		0*manager.setModerateImageURL(pItem)
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getAmount() == 0.0
		pPriceQuote.getSalePrice() == 0.0
		pPriceQuote.getListPrice()  == 0.0
	}
	
	def"priceItem, when isCustomizationOffered is false "(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		Locale pLocale
		
		calc.setPricingTools(pTools)
		Map<String, Object> pExtraParameters = new HashMap()
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		tools.isCustomizationOfferedForSKU(rItem,"tbs") >> false
		
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "true"
		pItem.getReferenceNumber() >> "ref"
		
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		0*calc.logDebug("personalizationType [UX] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		0*pItem.setEximPricingReq(false)
		0*manager.setModerateImageURL(pItem)
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getAmount() == 0.0
		pPriceQuote.getListPrice()  == 0.0
		pPriceQuote.getSalePrice() == 0.0
	}
	
	def"priceItem, when getReferenceNumber is empty "(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def BBBCommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		Locale pLocale
		
		calc.setPricingTools(pTools)
		Map<String, Object> pExtraParameters = new HashMap()
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "true"
		pItem.getReferenceNumber() >> ""
		
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		0*calc.logDebug("personalizationType [UX] isEximPricingReqd ["+ pItem.isEximPricingReq() + "] personalizedPrice [" + pItem.getPersonalizePrice())
		0*pItem.setEximPricingReq(false)
		0*manager.setModerateImageURL(pItem)
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getAmount() == 0.0
		pPriceQuote.getListPrice()  == 0.0
	}
	
	def"priceItem, when item is not an instance of BBBCommerceItem "(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def CommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		Locale pLocale
		
		calc.setPricingTools(pTools)
		Map<String, Object> pExtraParameters = new HashMap()
		calc.extractSiteId() >> "tbs"
		pItem.getAuxiliaryData() >>aux
		aux.getCatalogRef() >> rItem
		calc.setBbbCatalogTools(tools)
		tools.isCustomizationOfferedForSKU(rItem,"tbs") >> true
		
		calc.setEximPricingManager(manager)
		manager.getKatoriAvailability() >> "true"

		
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		0*calc.logDebug("personalizationType [UX] isEximPricingReqd ["+ _+ "] personalizedPrice [" + _)
		0*manager.setModerateImageURL(pItem)
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getAmount() == 0.0
		pPriceQuote.getSalePrice() == 0.0
		pPriceQuote.getListPrice()  == 0.0
	}
	
	def"priceItem, when request is coming from Pricing web service then skip the calculation "(){
		
		given:
		def ItemPriceInfo pPriceQuote = new ItemPriceInfo()
		def BBBCatalogTools tools =Mock()
		def CommerceItem pItem = Mock()
		def RepositoryItem pPricingModel =Mock()
		def RepositoryItem rItem =Mock()
		def RepositoryItem pProfile =Mock()
		def AuxiliaryData aux =Mock()
		def DetailedItemPriceTools det =Mock()
		def PricingTools pTools =Mock()
		def BBBEximManager manager =Mock()
		Locale pLocale
		
		Map<String, Object> pExtraParameters = new HashMap()
		pExtraParameters.put(BBBCheckoutConstants.PRICING_WEBSERVICE_ORDER, "123")
	
		when:
		calc.priceItem(pPriceQuote, pItem, pPricingModel,pLocale, pProfile,pExtraParameters)
		
		then:
		0*calc.logDebug("personalizationType [UX] isEximPricingReqd ["+ _+ "] personalizedPrice [" + _)
		0*manager.setModerateImageURL(pItem)
		pPriceQuote.getRawTotalPrice() == 0.0
		pPriceQuote.getAmount() == 0.0
		pPriceQuote.getListPrice()  == 0.0
	}
	
	def"getEximPersonalizeDetails, invokes the summary API on the basis of CI's reference number and set the reponse in the respective commerceItem properties"(){
		
		given:
		def AuxiliaryData aux =Mock()
		def BBBEximManager manager =Mock()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem rep =Mock()
		def EximSummaryResponseVO vo = new EximSummaryResponseVO()
		def EximCustomizedAttributesVO attVo = new EximCustomizedAttributesVO()
		def EximImagesVO image = new EximImagesVO()
		def EximImagesVO image1 = new EximImagesVO()
		def EximImagePreviewVO  previewVo =new EximImagePreviewVO()
		def EximImagePreviewVO  previewVo1 =new EximImagePreviewVO()
		def EximImagePreviewVO  previewVo2 =new EximImagePreviewVO()
		double unitPersonalizedFee = 200.0
		String siteId = "tbs"
		
		pItem.getReferenceNumber() >> "refNumParam"
		pItem.getId() >> "id"
		pItem.getAuxiliaryData() >> aux
		pItem.getPersonalizationOptions() >> "options"
		aux.getProductRef() >> rep
		rep.getPropertyValue("Id")  >> "Id"
		
		calc.setEximPricingManager(manager)
		manager.invokeSummaryAPI("Id",null,"refNumParam") >> vo
		vo.setCustomizations([attVo])
		
		attVo.setRetailPriceAdder("100.0")
		attVo.setErrors([])
		
		attVo.setImages([image1,image])
		image.setId(BBBCoreConstants.IMAGE_ID)
		image.setPreviews([previewVo,previewVo1,previewVo2])
		
		previewVo.setSize(BBBCoreConstants.IMAGE_PREVIEW_LARGE)
		previewVo.setUrl("url")
		previewVo1.setSize(BBBCoreConstants.IMAGE_PREVIEW_X_SMALL)
		previewVo1.setUrl("url1")
		previewVo2.setSize(BBBCoreConstants.IMAGE_PREVIEW_SMALL)
		previewVo2.setUrl("url2")
		
		image1.setId("123")
		image1.setPreviews([])
		
		
		when:
		Double Value = calc.getEximPersonalizeDetails(pItem,unitPersonalizedFee, siteId)
		
		then:
		1*calc.logDebug("ref num :: " + pItem.getReferenceNumber() + " is associated with commerce item :: " + pItem.getId())
		1*calc.logDebug("eximAtrributes error :: " + attVo.getErrors() + " and eximPersonlizedPrice :: " + attVo.getRetailPriceAdder())
		1*pItem.setPersonalizePrice(attVo.getRetailPriceAdder());
		1*pItem.setPersonalizeCost(attVo.getCostPriceAdder());
		1*pItem.setPersonalizationDetails(attVo.getNamedrop());
		1*pItem.setPersonalizationOptions(attVo.getCustomizationService())
		1*pItem.setPersonalizationOptionsDisplay(manager.getPersonalizedOptionsDisplayCode("options"))
		1*pItem.setMetaDataFlag(attVo.getMetadataStatus())
		1*pItem.setMetaDataUrl(attVo.getMetadataUrl());
		1*pItem.setModerationFlag(attVo.getModerationStatus())
		1*pItem.setModerationUrl(attVo.getModerationUrl())
		1*pItem.setFullImagePath(_)
		1*pItem.setThumbnailImagePath(_)
		1*pItem.setMobileThumbnailImagePath(_)
		Value == 100.0
	}
	
	def"getEximPersonalizeDetails, invokes the summary API on the basis of CI's reference number and set the reponse in the respective commerceItem properties when imgaeID is empty"(){
		
		given:
		def AuxiliaryData aux =Mock()
		def BBBEximManager manager =Mock()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem rep =Mock()
		def EximSummaryResponseVO vo = new EximSummaryResponseVO()
		def EximCustomizedAttributesVO attVo = new EximCustomizedAttributesVO()
		def EximImagesVO image = new EximImagesVO()
		def EximImagePreviewVO  previewVo =new EximImagePreviewVO()
		double unitPersonalizedFee = 200.0
		String siteId = "tbs"
		
		pItem.getReferenceNumber() >> "refNumParam"
		pItem.getId() >> "id"
		pItem.getAuxiliaryData() >> aux
		pItem.getPersonalizationOptions() >> "options"
		aux.getProductRef() >> rep
		rep.getPropertyValue("Id")  >> "Id"
		
		calc.setEximPricingManager(manager)
		manager.invokeSummaryAPI("Id",null,"refNumParam") >> vo
		vo.setCustomizations([attVo])
		
		attVo.setRetailPriceAdder("100.0")
		attVo.setErrors([])
		
		attVo.setImages([image])
		image.setId("")
		image.setPreviews([previewVo])
		
		previewVo.setSize(BBBCoreConstants.IMAGE_PREVIEW_LARGE)
		previewVo.setUrl("url")
		
		when:
		Double Value = calc.getEximPersonalizeDetails(pItem,unitPersonalizedFee, siteId)
		
		then:
		1*calc.logDebug("ref num :: " + pItem.getReferenceNumber() + " is associated with commerce item :: " + pItem.getId())
		1*calc.logDebug("eximAtrributes error :: " + attVo.getErrors() + " and eximPersonlizedPrice :: " + attVo.getRetailPriceAdder())
		1*pItem.setPersonalizePrice(attVo.getRetailPriceAdder());
		1*pItem.setPersonalizeCost(attVo.getCostPriceAdder());
		1*pItem.setPersonalizationDetails(attVo.getNamedrop());
		1*pItem.setPersonalizationOptions(attVo.getCustomizationService())
		1*pItem.setPersonalizationOptionsDisplay(manager.getPersonalizedOptionsDisplayCode("options"))
		1*pItem.setMetaDataFlag(attVo.getMetadataStatus())
		1*pItem.setMetaDataUrl(attVo.getMetadataUrl());
		1*pItem.setModerationFlag(attVo.getModerationStatus())
		1*pItem.setModerationUrl(attVo.getModerationUrl())
		1*pItem.setFullImagePath(_)
		Value == 100.0
	}
	
	def"getEximPersonalizeDetails, when exim response contains errors"(){
		
		given:
		def AuxiliaryData aux =Mock()
		def BBBEximManager manager =Mock()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem rep =Mock()
		def EximSummaryResponseVO vo = new EximSummaryResponseVO()
		def EximCustomizedAttributesVO attVo = new EximCustomizedAttributesVO()
		def EximImagesVO image = new EximImagesVO()
		def ErrorVO err = new ErrorVO()
		def EximImagePreviewVO  previewVo =new EximImagePreviewVO()
		double unitPersonalizedFee = 200.0
		String siteId = "tbs"
		
		pItem.getReferenceNumber() >> "refNumParam"
		pItem.getId() >> "id"
		pItem.getAuxiliaryData() >> aux
		pItem.getPersonalizationOptions() >> "options"
		aux.getProductRef() >> rep
		rep.getPropertyValue("Id")  >> "Id"
		
		calc.setEximPricingManager(manager)
		manager.invokeSummaryAPI("Id",null,"refNumParam") >> vo
		vo.setCustomizations([attVo])
		attVo.setErrors([err])
		err.setMessage("error")
		err.setCode("code")
		
		when:
		Double Value = calc.getEximPersonalizeDetails(pItem,unitPersonalizedFee, siteId)
		
		then:
		1*calc.logDebug("ref num :: " + pItem.getReferenceNumber() + " is associated with commerce item :: " + pItem.getId())
		0*calc.logDebug("eximAtrributes error :: " + attVo.getErrors() + " and eximPersonlizedPrice :: " + attVo.getRetailPriceAdder())
		0*pItem.setPersonalizePrice(attVo.getRetailPriceAdder());
		0*pItem.setPersonalizeCost(attVo.getCostPriceAdder());
		0*pItem.setPersonalizationDetails(attVo.getNamedrop());
		0*pItem.setPersonalizationOptions(attVo.getCustomizationService())
		0*pItem.setPersonalizationOptionsDisplay(manager.getPersonalizedOptionsDisplayCode("options"))
		0*pItem.setMetaDataFlag(attVo.getMetadataStatus())
		0*pItem.setMetaDataUrl(attVo.getMetadataUrl());
		0*pItem.setModerationFlag(attVo.getModerationStatus())
		0*pItem.setModerationUrl(attVo.getModerationUrl())
		0*pItem.setFullImagePath(_)
		Value == null
		1*calc.logError("There seems to be an error with your personalization.Please try again!!error")
		BBBBusinessException exc = thrown()
	}
	
	def"getEximPersonalizeDetails, when API invocation returns null"(){
		
		given:
		def AuxiliaryData aux =Mock()
		def BBBEximManager manager =Mock()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem rep =Mock()
		def EximSummaryResponseVO vo = new EximSummaryResponseVO()
		def EximCustomizedAttributesVO attVo = new EximCustomizedAttributesVO()
		def EximImagesVO image = new EximImagesVO()
		def EximImagePreviewVO  previewVo =new EximImagePreviewVO()
		double unitPersonalizedFee = 200.0
		String siteId = "tbs"
		
		pItem.getReferenceNumber() >> "refNumParam"
		pItem.getId() >> "id"
		pItem.getAuxiliaryData() >> aux
		pItem.getPersonalizationOptions() >> "options"
		aux.getProductRef() >> rep
		rep.getPropertyValue("Id")  >> "Id"
		
		calc.setEximPricingManager(manager)
		manager.invokeSummaryAPI("Id",null,"refNumParam") >> null
		
		when:
		Double Value = calc.getEximPersonalizeDetails(pItem,unitPersonalizedFee, siteId)
		
		then:
		1*calc.logDebug("ref num :: " + pItem.getReferenceNumber() + " is associated with commerce item :: " + pItem.getId())
		0*calc.logDebug("eximAtrributes error :: " + attVo.getErrors() + " and eximPersonlizedPrice :: " + attVo.getRetailPriceAdder())
		0*pItem.setPersonalizePrice(attVo.getRetailPriceAdder());
		0*pItem.setPersonalizeCost(attVo.getCostPriceAdder());
		0*pItem.setPersonalizationDetails(attVo.getNamedrop());
		0*pItem.setPersonalizationOptions(attVo.getCustomizationService())
		0*pItem.setPersonalizationOptionsDisplay(manager.getPersonalizedOptionsDisplayCode("options"))
		0*pItem.setMetaDataFlag(attVo.getMetadataStatus())
		0*pItem.setMetaDataUrl(attVo.getMetadataUrl());
		0*pItem.setModerationFlag(attVo.getModerationStatus())
		0*pItem.setModerationUrl(attVo.getModerationUrl())
		0*pItem.setFullImagePath(_)
		Value == null
		0*calc.logError("There seems to be an error with your personalization.Please try again!!error")
		BBBSystemException exc = thrown()
	}
	
	def"getEximPersonalizeDetails, when customizations list is null"(){
		
		given:
		def AuxiliaryData aux =Mock()
		def BBBEximManager manager =Mock()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem rep =Mock()
		def EximSummaryResponseVO vo = new EximSummaryResponseVO()
		def EximCustomizedAttributesVO attVo = new EximCustomizedAttributesVO()
		def EximImagesVO image = new EximImagesVO()
		def EximImagePreviewVO  previewVo =new EximImagePreviewVO()
		double unitPersonalizedFee = 200.0
		String siteId = "tbs"
		
		pItem.getReferenceNumber() >> "refNumParam"
		pItem.getId() >> "id"
		pItem.getAuxiliaryData() >> aux
		pItem.getPersonalizationOptions() >> "options"
		aux.getProductRef() >> rep
		rep.getPropertyValue("Id")  >> "Id"
		
		calc.setEximPricingManager(manager)
		manager.invokeSummaryAPI("Id",null,"refNumParam") >> vo
		vo.setCustomizations(null)
		
		when:
		Double Value = calc.getEximPersonalizeDetails(pItem,unitPersonalizedFee, siteId)
		
		then:
		1*calc.logDebug("ref num :: " + pItem.getReferenceNumber() + " is associated with commerce item :: " + pItem.getId())
		0*calc.logDebug("eximAtrributes error :: " + attVo.getErrors() + " and eximPersonlizedPrice :: " + attVo.getRetailPriceAdder())
		0*pItem.setPersonalizePrice(attVo.getRetailPriceAdder());
		0*pItem.setPersonalizeCost(attVo.getCostPriceAdder());
		0*pItem.setPersonalizationDetails(attVo.getNamedrop());
		0*pItem.setPersonalizationOptions(attVo.getCustomizationService())
		0*pItem.setPersonalizationOptionsDisplay(manager.getPersonalizedOptionsDisplayCode("options"))
		0*pItem.setMetaDataFlag(attVo.getMetadataStatus())
		0*pItem.setMetaDataUrl(attVo.getMetadataUrl());
		0*pItem.setModerationFlag(attVo.getModerationStatus())
		0*pItem.setModerationUrl(attVo.getModerationUrl())
		0*pItem.setFullImagePath(_)
		Value == null
		0*calc.logError("There seems to be an error with your personalization.Please try again!!error")
		BBBSystemException exc = thrown()
	}
	
	def"getEximPersonalizeDetails, when customizations list is empty"(){
		
		given:
		def AuxiliaryData aux =Mock()
		def BBBEximManager manager =Mock()
		def BBBCommerceItem pItem =Mock()
		def RepositoryItem rep =Mock()
		def EximSummaryResponseVO vo = new EximSummaryResponseVO()
		def EximCustomizedAttributesVO attVo = new EximCustomizedAttributesVO()
		def EximImagesVO image = new EximImagesVO()
		def EximImagePreviewVO  previewVo =new EximImagePreviewVO()
		double unitPersonalizedFee = 200.0
		String siteId = "tbs"
		
		pItem.getReferenceNumber() >> "refNumParam"
		pItem.getId() >> "id"
		pItem.getAuxiliaryData() >> aux
		pItem.getPersonalizationOptions() >> "options"
		aux.getProductRef() >> rep
		rep.getPropertyValue("Id")  >> "Id"
		
		calc.setEximPricingManager(manager)
		manager.invokeSummaryAPI("Id",null,"refNumParam") >> vo
		vo.setCustomizations([])
		
		when:
		Double Value = calc.getEximPersonalizeDetails(pItem,unitPersonalizedFee, siteId)
		
		then:
		1*calc.logDebug("ref num :: " + pItem.getReferenceNumber() + " is associated with commerce item :: " + pItem.getId())
		0*calc.logDebug("eximAtrributes error :: " + attVo.getErrors() + " and eximPersonlizedPrice :: " + attVo.getRetailPriceAdder())
		0*pItem.setPersonalizePrice(attVo.getRetailPriceAdder());
		0*pItem.setPersonalizeCost(attVo.getCostPriceAdder());
		0*pItem.setPersonalizationDetails(attVo.getNamedrop());
		0*pItem.setPersonalizationOptions(attVo.getCustomizationService())
		0*pItem.setPersonalizationOptionsDisplay(manager.getPersonalizedOptionsDisplayCode("options"))
		0*pItem.setMetaDataFlag(attVo.getMetadataStatus())
		0*pItem.setMetaDataUrl(attVo.getMetadataUrl());
		0*pItem.setModerationFlag(attVo.getModerationStatus())
		0*pItem.setModerationUrl(attVo.getModerationUrl())
		0*pItem.setFullImagePath(_)
		Value == null
		0*calc.logError("There seems to be an error with your personalization.Please try again!!error")
		BBBSystemException exc = thrown()
	}

}
