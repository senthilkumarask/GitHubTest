package com.bbb.commerce.service.pricing


import com.bedbathandbeyond.atg.PricingRequest;
import atg.commerce.pricing.PricingException
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.ShippingPricingEngine
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.userprofiling.Profile
import java.util.Collection;
import java.util.Map;
import com.bbb.account.BBBProfileTools
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;
import com.bedbathandbeyond.atg.MessageHeader;
import com.bedbathandbeyond.atg.PricingRequest;
import com.bedbathandbeyond.atg.PricingResponse;
import atg.multisite.SiteContext;
import com.bedbathandbeyond.atg.Site
import com.bedbathandbeyond.atg.Site.Enum;

import spock.lang.specification.BBBExtendedSpec

class BBBPricingWebServiceSpecification extends BBBExtendedSpec {
	
	
	def PriceListManager priceManager =Mock()
	def BBBProfileTools tools =Mock()
	def BBBPricingWSMapper mapper =Mock()
	def PricingTools pTools = Mock()
	def ShippingPricingEngine engine =Mock()
	def Repository siteRep =Mock()
	
	BBBPricingWebService service 
	
	def setup(){
		service = new BBBPricingWebService()
		service.setProfileTools(tools)
		service.setPriceListManager(priceManager)
		service.setPricingWSMapper(mapper)
		service.setPricingTools(pTools)
		pTools.getShippingPricingEngine() >> engine
		service.setSiteRepository(siteRep)
	}
	
	/**
	 * common Method for setting Spy Parameters in Exception TestCases
	 * @return
	 */
	private setSpyParameters(){
		service = Spy()
		service.setLoggingDebug(true)
		service.setProfileTools(tools)
		service.setPriceListManager(priceManager)
		service.setPricingWSMapper(mapper)
		service.setPricingTools(pTools)
		pTools.getShippingPricingEngine() >> engine
		service.setSiteRepository(siteRep)
	}
	
	def"priceOrder when header site id is BED_BATH_US"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def MessageHeader header =Mock()
		def SiteContextManager manager =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
		service.setLoggingTransaction(true)
		service.setLoggingDebug(true)
		
		pricingRequest.getHeader() >> header
		header.getOrderIdentifier() >>"TBS"
		header.getSiteId() >> Site.BED_BATH_US >>Site.BED_BATH_US >> Site.BED_BATH_US>> Site.TBS_BED_BATH_US 
		
		service.setSiteContextManager(manager)
		1*manager.getSiteContext("TBS_BedBathUS") >> context
		2*context.getSite() >> site1
		1*site1.getId() >> BBBCoreConstants.SITE_BAB_US
		
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> priceList
		header.getCallingAppCode() >> "DS"
		1*mapper.transformRequestToOrder(pricingRequest, _, _) >> bbbOrder
		
		Map<String, Collection<RepositoryItem>> pricingModels = new HashMap()
		pricingModels.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, [item])
		pricingModels.put(BBBCheckoutConstants.ITEM_PROMOTIONS, [item1])
		pricingModels.put(BBBCheckoutConstants.ORDER_PROMOTIONS, [item2])
		
		1*mapper.populatePromotions(bbbOrder, pricingRequest, _) >> pricingModels
		1*engine.getPricingModels(_) >> []
		1*mapper.transformOrderToResponse(bbbOrder, pricingRequest, _) >> pricingResponse
	
		when:
		PricingResponse resp= service.priceOrder(pricingRequest)
		
		then:
		1*header.setSiteId(Site.TBS_BED_BATH_US)
		1* manager.pushSiteContext(context)
		1*pTools.priceOrderSubtotalShipping(bbbOrder, _, _,_,_, _, _)
		1*header.setSiteId(Site.BED_BATH_US)
		resp == pricingResponse	
	}
	
	def"priceOrder,  when  header site id  is BED_BATH_CANADA"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def MessageHeader header =Mock()
		def SiteContextManager manager =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
		
		pricingRequest.getHeader() >> header
		header.getOrderIdentifier() >>"TBS"
		header.getSiteId() >> Site.BED_BATH_CANADA >>Site.BED_BATH_CANADA >>Site.BED_BATH_CANADA >>Site.BED_BATH_CANADA >> Site.TBS_BED_BATH_CANADA 
		
		service.setSiteContextManager(manager)
		1*manager.getSiteContext("TBS_BedBathCanada") >> context
		2*context.getSite() >> site1
		1*site1.getId() >> BBBCoreConstants.SITE_BBB
		
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> priceList
		header.getCallingAppCode() >> "DS"
		1*mapper.transformRequestToOrder(pricingRequest, _, _) >> bbbOrder
		
		Map<String, Collection<RepositoryItem>> pricingModels = new HashMap()
		pricingModels.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, [item])
		pricingModels.put(BBBCheckoutConstants.ITEM_PROMOTIONS, [item1])
		pricingModels.put(BBBCheckoutConstants.ORDER_PROMOTIONS, [item2])
		
		1*mapper.populatePromotions(bbbOrder, pricingRequest, _) >> pricingModels
		1*engine.getPricingModels(_) >> []
		1*mapper.transformOrderToResponse(bbbOrder, pricingRequest, _) >> pricingResponse
		
		when:
		PricingResponse resp= service.priceOrder(pricingRequest)
		
		then:
		1*header.setSiteId(Site.TBS_BED_BATH_CANADA)
		1* manager.pushSiteContext(context)
		1*pTools.priceOrderSubtotalShipping(bbbOrder, _, _,_,_, _, _)
		1*header.setSiteId(Site.BED_BATH_CANADA)
		resp == pricingResponse
	}
	
	def"priceOrder, when  header site id  is BUY_BUY_BABY"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def MessageHeader header =Mock()
		def SiteContextManager manager =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
		
		pricingRequest.getHeader() >> header
		header.getOrderIdentifier() >>"TBS"
		header.getSiteId() >>Site.BUY_BUY_BABY >>Site.BUY_BUY_BABY >>Site.BUY_BUY_BABY >>Site.BUY_BUY_BABY>>Site.BUY_BUY_BABY>>Site.BUY_BUY_BABY>> Site.TBS_BUY_BUY_BABY
		
		service.setSiteContextManager(manager)
		1*manager.getSiteContext("TBS_BuyBuyBaby") >> context
		2*context.getSite() >> site1
		1*site1.getId() >> BBBCoreConstants.SITE_BAB_CA
		
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> priceList
		header.getCallingAppCode() >> "DS"
		1*mapper.transformRequestToOrder(pricingRequest, _, _) >> bbbOrder
		
		Map<String, Collection<RepositoryItem>> pricingModels = new HashMap()
		pricingModels.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, [item])
		pricingModels.put(BBBCheckoutConstants.ITEM_PROMOTIONS, [item1])
		pricingModels.put(BBBCheckoutConstants.ORDER_PROMOTIONS, [item2])
		
		1*mapper.populatePromotions(bbbOrder, pricingRequest, _) >> pricingModels
		1*engine.getPricingModels(_) >> []
		1*mapper.transformOrderToResponse(bbbOrder, pricingRequest, _) >> pricingResponse
		
		when:
		PricingResponse resp= service.priceOrder(pricingRequest)
		
		then:
		1*header.setSiteId(Site.TBS_BUY_BUY_BABY)
		1* manager.pushSiteContext(context)
		1*pTools.priceOrderSubtotalShipping(bbbOrder, _, _,_,_, _, _)
		1*header.setSiteId(Site.BUY_BUY_BABY)
		resp == pricingResponse
	}
	
	def"priceOrder, when  header site id  null"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def MessageHeader header =Mock()
		def SiteContextManager manager =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
		
		pricingRequest.getHeader() >> header
		header.getOrderIdentifier() >>"TBS"
		header.getSiteId() >>null
		
		service.setSiteContextManager(manager)
		1*manager.getSiteContext(_) >> context
		2*context.getSite() >> site1
		1*site1.getId() >> BBBCoreConstants.SITE_BAB_CA
		
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> priceList
		header.getCallingAppCode() >> "DS"
		1*mapper.transformRequestToOrder(pricingRequest, _, _) >> bbbOrder
		
		Map<String, Collection<RepositoryItem>> pricingModels = new HashMap()
		pricingModels.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, [item])
		pricingModels.put(BBBCheckoutConstants.ITEM_PROMOTIONS, [item1])
		pricingModels.put(BBBCheckoutConstants.ORDER_PROMOTIONS, [item2])
		
		1*mapper.populatePromotions(bbbOrder, pricingRequest, _) >> pricingModels
		1*engine.getPricingModels(_) >> []
		1*mapper.transformOrderToResponse(bbbOrder, pricingRequest, _) >> pricingResponse
		
		when:
		PricingResponse resp= service.priceOrder(pricingRequest)
		
		then:
		0*header.setSiteId(Site.TBS_BUY_BUY_BABY)
		1* manager.pushSiteContext(context)
		1*pTools.priceOrderSubtotalShipping(bbbOrder, _, _,_,_, _, _)
		0*header.setSiteId(Site.BUY_BUY_BABY)
		resp == pricingResponse
	}
	
	def"priceOrder, when  header site id PY"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def MessageHeader header =Mock()
		def SiteContextManager manager =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
		
		pricingRequest.getHeader() >> header
		header.getOrderIdentifier() >>"TBS"
		header.getSiteId() >>Site.TBS_BUY_BUY_BABY
		
		service.setSiteContextManager(manager)
		1*manager.getSiteContext("TBS_BuyBuyBaby") >> context
		2*context.getSite() >> site1
		1*site1.getId() >> BBBCoreConstants.SITE_BAB_CA
		
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> priceList
		header.getCallingAppCode() >> "DS"
		1*mapper.transformRequestToOrder(pricingRequest, _, _) >> bbbOrder
		
		Map<String, Collection<RepositoryItem>> pricingModels = new HashMap()
		pricingModels.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, [item])
		pricingModels.put(BBBCheckoutConstants.ITEM_PROMOTIONS, [item1])
		pricingModels.put(BBBCheckoutConstants.ORDER_PROMOTIONS, [item2])
		
		1*mapper.populatePromotions(bbbOrder, pricingRequest, _) >> pricingModels
		1*engine.getPricingModels(_) >> []
		1*mapper.transformOrderToResponse(bbbOrder, pricingRequest, _) >> pricingResponse
		
		when:
		PricingResponse resp= service.priceOrder(pricingRequest)
		
		then:
		0*header.setSiteId(Site.TBS_BUY_BUY_BABY)
		1* manager.pushSiteContext(context)
		1*pTools.priceOrderSubtotalShipping(bbbOrder, _, _,_,_, _, _)
		1*header.setSiteId(Site.BUY_BUY_BABY)
		resp == pricingResponse
	}
	
	def"priceOrder, when  site id  retrieved from context is empty"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def MessageHeader header =Mock()
		def SiteContextManager manager =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
		
		pricingRequest.getHeader() >> header
		header.getOrderIdentifier() >>"identifier"
		header.getSiteId() >> Site.BED_BATH_US 
		
		service.setSiteContextManager(manager)
		1*manager.getSiteContext("BedBathUS") >> context
		2*context.getSite() >> site1
		1*site1.getId() >> ""
		
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> priceList
		header.getCallingAppCode() >> "DS"
		1*mapper.transformRequestToOrder(pricingRequest, _, _) >> bbbOrder
		
		Map<String, Collection<RepositoryItem>> pricingModels = new HashMap()
		pricingModels.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, [item])
		pricingModels.put(BBBCheckoutConstants.ITEM_PROMOTIONS, [item1])
		pricingModels.put(BBBCheckoutConstants.ORDER_PROMOTIONS, [item2])
		
		1*mapper.populatePromotions(bbbOrder, pricingRequest, _) >> pricingModels
		1*engine.getPricingModels(_) >> []
		1*mapper.transformOrderToResponse(bbbOrder, pricingRequest, _) >> pricingResponse
		
		when:
		PricingResponse resp= service.priceOrder(pricingRequest)
		
		then:
		0*header.setSiteId(Site.TBS_BED_BATH_US)
		1* manager.pushSiteContext(context)
		1*pTools.priceOrderSubtotalShipping(bbbOrder, _, _,_,_, _, _)
		0*header.setSiteId(Site.BED_BATH_US)
		resp == pricingResponse
	}
	
	def"priceOrder, when  site id  retrieved from context is not equal to BedBathCanada"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def MessageHeader header =Mock()
		def SiteContextManager manager =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
		
		pricingRequest.getHeader() >> header
		header.getOrderIdentifier() >>"info"
		header.getSiteId() >> Site.BED_BATH_US
		
		service.setSiteContextManager(manager)
		1*manager.getSiteContext("BedBathUS") >> context
		2*context.getSite() >> site1
		1*site1.getId() >> "123"
		
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> priceList
		header.getCallingAppCode() >> "DS"
		1*mapper.transformRequestToOrder(pricingRequest, _, _) >> bbbOrder
		
		Map<String, Collection<RepositoryItem>> pricingModels = new HashMap()
		pricingModels.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, [item])
		pricingModels.put(BBBCheckoutConstants.ITEM_PROMOTIONS, [item1])
		pricingModels.put(BBBCheckoutConstants.ORDER_PROMOTIONS, [item2])
		
		1*mapper.populatePromotions(bbbOrder, pricingRequest, _) >> pricingModels
		1*engine.getPricingModels(_) >> []
		1*mapper.transformOrderToResponse(bbbOrder, pricingRequest, _) >> pricingResponse
		
		when:
		PricingResponse resp= service.priceOrder(pricingRequest)
		
		then:
		0*header.setSiteId(Site.TBS_BED_BATH_US)
		1* manager.pushSiteContext(context)
		1*pTools.priceOrderSubtotalShipping(bbbOrder, _, _,_,_, _, _)
		0*header.setSiteId(Site.BED_BATH_US)
		resp == pricingResponse
	}
	
	def"priceOrder, when  PricingException is thrown"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def MessageHeader header =Mock()
		def SiteContextManager manager =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
		
		setSpyParameters()
		pricingRequest.getHeader() >> header
		header.getOrderIdentifier() >>"TBS"
		header.getSiteId() >>null
		
		service.setSiteContextManager(manager)
		1*manager.getSiteContext(_) >> context
		2*context.getSite() >> site1
		1*site1.getId() >> BBBCoreConstants.SITE_BAB_CA
		
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> priceList
		header.getCallingAppCode() >> "DS"
		1*mapper.transformRequestToOrder(pricingRequest, _, _) >> bbbOrder
		
		Map<String, Collection<RepositoryItem>> pricingModels = new HashMap()
		pricingModels.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, [item])
		pricingModels.put(BBBCheckoutConstants.ITEM_PROMOTIONS, [item1])
		pricingModels.put(BBBCheckoutConstants.ORDER_PROMOTIONS, [item2])
		
		1*mapper.populatePromotions(bbbOrder, pricingRequest, _) >> pricingModels
		pTools.priceOrderSubtotalShipping(bbbOrder, _, _,_, _, _, _) >>{throw new PricingException("")}
		1*engine.getPricingModels(_) >> []
		
		when:
		PricingResponse resp= service.priceOrder(pricingRequest)
		
		then:
		0*header.setSiteId(Site.TBS_BUY_BUY_BABY)
		1* manager.pushSiteContext(context)
		1*service.vlogDebug("BBBPricingWebService.priceOrder: We are going to apply Item promotions - {0} , Order promotions - {1} and Shipping promotions - {2} to order {3}",_, _, _, _)
		1*service.logError("Pricing exception while performing pricing operation", _)
		BBBSystemException e = thrown()
		0*header.setSiteId(Site.BUY_BUY_BABY)
		resp == null
	}
	
	def"priceOrder, when  BBBBusinessException is thrown"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def MessageHeader header =Mock()
		def SiteContextManager manager =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
		
		setSpyParameters()
		pricingRequest.getHeader() >> header
		header.getOrderIdentifier() >>"TBS"
		header.getSiteId() >>null
		
		service.setSiteContextManager(manager)
		1*manager.getSiteContext(_) >> context
		2*context.getSite() >> site1
		1*site1.getId() >> BBBCoreConstants.SITE_BAB_CA
		
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> priceList
		header.getCallingAppCode() >> "DS"	
		1*mapper.transformRequestToOrder(pricingRequest, _, _) >> bbbOrder
		
		Map<String, Collection<RepositoryItem>> pricingModels = new HashMap()
		pricingModels.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, [item])
		pricingModels.put(BBBCheckoutConstants.ITEM_PROMOTIONS, [item1])
		pricingModels.put(BBBCheckoutConstants.ORDER_PROMOTIONS, [item2])
	
		1*mapper.populatePromotions(bbbOrder, pricingRequest, _) >> pricingModels
		pTools.priceOrderSubtotalShipping(bbbOrder, _, _,_, _, _, _) >>{throw new BBBBusinessException("")}
		1*engine.getPricingModels(_) >> []
		
		when:
		PricingResponse resp= service.priceOrder(pricingRequest)
		
		then:
		0*header.setSiteId(Site.TBS_BUY_BUY_BABY)
		1* manager.pushSiteContext(context)
		1*service.logError("Business exception while performing pricing operation", _)
		BBBBusinessException e = thrown()
		0*header.setSiteId(Site.BUY_BUY_BABY)
		resp == null
	}
	
	def"priceOrder, when  BBBSystemException is thrown"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def MessageHeader header =Mock()
		def SiteContextManager manager =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
		
		setSpyParameters()
		pricingRequest.getHeader() >> header
		header.getOrderIdentifier() >>"TBS"
		header.getSiteId() >>null
		
		service.setSiteContextManager(manager)
		1*manager.getSiteContext(_) >> context
		2*context.getSite() >> site1
		1*site1.getId() >> BBBCoreConstants.SITE_BAB_CA
		
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> priceList
		header.getCallingAppCode() >> "DS"
		1*mapper.transformRequestToOrder(pricingRequest, _, _) >> bbbOrder
		
		Map<String, Collection<RepositoryItem>> pricingModels = new HashMap()
		pricingModels.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, [item])
		pricingModels.put(BBBCheckoutConstants.ITEM_PROMOTIONS, [item1])
		pricingModels.put(BBBCheckoutConstants.ORDER_PROMOTIONS, [item2])
		
		1*mapper.populatePromotions(bbbOrder, pricingRequest, _) >> pricingModels
		pTools.priceOrderSubtotalShipping(bbbOrder, _, _,_, _, _, _) >>{throw new BBBSystemException("")}
		1*engine.getPricingModels(_) >> []
		
		when:
		PricingResponse resp= service.priceOrder(pricingRequest)
		
		then:
		0*header.setSiteId(Site.TBS_BUY_BUY_BABY)
		1* manager.pushSiteContext(context)
		1*service.logError("System exception while performing pricing operation", _)
		BBBSystemException e = thrown()
		0*header.setSiteId(Site.BUY_BUY_BABY)
		resp == null
	}
	
	def"priceOrder, when  RepositoryException is thrown in populateProfile method"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def MessageHeader header =Mock()
		def SiteContextManager manager =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
		
		setSpyParameters()
		pricingRequest.getHeader() >> header
		header.getOrderIdentifier() >>"TBS"
		header.getSiteId() >>null
		
		service.setSiteContextManager(manager)
		1*manager.getSiteContext(_) >> context
		2*context.getSite() >> site1
		1*site1.getId() >> BBBCoreConstants.SITE_BAB_CA
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> {throw new RepositoryException("")}
		
		when:
		PricingResponse resp= service.priceOrder(pricingRequest)
		
		then:
		0*header.setSiteId(Site.TBS_BUY_BUY_BABY)
		1* manager.pushSiteContext(context)
		1*service.logError("Repository exception while performing pricing operation", _)
		BBBSystemException e = thrown()
		0*header.setSiteId(Site.BUY_BUY_BABY)
		resp == null
	}
		
	def"priceOrder, when  OrderIdentifier is null"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def MessageHeader header =Mock()
		def SiteContextManager manager =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
		
		pricingRequest.getHeader() >> header
		header.getOrderIdentifier() >>null
		header.getSiteId() >> Site.BED_BATH_US
		
		service.setSiteContextManager(manager)
		1*manager.getSiteContext("BedBathUS") >> context
		2*context.getSite() >> site1
		1*site1.getId() >> "123"
		
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> priceList
		header.getCallingAppCode() >> "DS"
		1*mapper.transformRequestToOrder(pricingRequest, _, _) >> bbbOrder
		
		Map<String, Collection<RepositoryItem>> pricingModels = new HashMap()
		pricingModels.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, [item])
		pricingModels.put(BBBCheckoutConstants.ITEM_PROMOTIONS, [item1])
		pricingModels.put(BBBCheckoutConstants.ORDER_PROMOTIONS, [item2])
		
		1*mapper.populatePromotions(bbbOrder, pricingRequest, _) >> pricingModels
		1*engine.getPricingModels(_) >> []
		1*mapper.transformOrderToResponse(bbbOrder, pricingRequest, _) >> pricingResponse
		
		when:
		PricingResponse resp= service.priceOrder(pricingRequest)
		
		then:
		0*header.setSiteId(Site.TBS_BED_BATH_US)
		1* manager.pushSiteContext(context)
		1*pTools.priceOrderSubtotalShipping(bbbOrder, _, _,_,_, _, _)
		0*header.setSiteId(Site.BED_BATH_US)
		resp == pricingResponse
	}
	
	def"priceOrder, when  getCallingAppCode is null"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def MessageHeader header =Mock()
		def SiteContextManager manager =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
			
		pricingRequest.getHeader() >> header
		header.getOrderIdentifier() >>"TBS"
		header.getSiteId() >> Site.BED_BATH_US >>  Site.BED_BATH_US >>  Site.TBS_BED_BATH_US
		
		service.setSiteContextManager(manager)
		1*manager.getSiteContext("TBS_BedBathUS") >> context
		2*context.getSite() >> site1
		1*site1.getId() >> "123"
		
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> priceList
		header.getCallingAppCode() >> null
		1*mapper.transformRequestToOrder(pricingRequest, _, _) >> bbbOrder
		
		Map<String, Collection<RepositoryItem>> pricingModels = new HashMap()
		pricingModels.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, [item])
		pricingModels.put(BBBCheckoutConstants.ITEM_PROMOTIONS, [item1])
		pricingModels.put(BBBCheckoutConstants.ORDER_PROMOTIONS, [item2])
		
		1*mapper.populatePromotions(bbbOrder, pricingRequest, _) >> pricingModels
		1*engine.getPricingModels(_) >> []
		1*mapper.transformOrderToResponse(bbbOrder, pricingRequest, _) >> pricingResponse
		
		when:
		PricingResponse resp= service.priceOrder(pricingRequest)
		
		then:
		1*header.setSiteId(Site.TBS_BED_BATH_US)
		1* manager.pushSiteContext(context)
		1*pTools.priceOrderSubtotalShipping(bbbOrder, _, _,_,_, _, _)
		1*header.setSiteId(Site.BED_BATH_US)
		resp == pricingResponse
	}
	
	def"priceOrder, when  getCallingAppCode is not equal to DS"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def MessageHeader header =Mock()
		def SiteContextManager manager =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem priceList =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
			
		pricingRequest.getHeader() >> header
		header.getOrderIdentifier() >>"TBS"
		header.getSiteId() >> Site.BED_BATH_US >>  Site.BED_BATH_US >>  Site.TBS_BED_BATH_US
		
		service.setSiteContextManager(manager)
		1*manager.getSiteContext("TBS_BedBathUS") >> context
		2*context.getSite() >> site1
		1*site1.getId() >> "123"
		
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> priceList
		header.getCallingAppCode() >> "PX"
		1*mapper.transformRequestToOrder(pricingRequest, _, _) >> bbbOrder
		
		Map<String, Collection<RepositoryItem>> pricingModels = new HashMap()
		pricingModels.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, [item])
		pricingModels.put(BBBCheckoutConstants.ITEM_PROMOTIONS, [item1])
		pricingModels.put(BBBCheckoutConstants.ORDER_PROMOTIONS, [item2])
		
		1*mapper.populatePromotions(bbbOrder, pricingRequest, _) >> pricingModels
		1*engine.getPricingModels(_) >> []
		1*mapper.transformOrderToResponse(bbbOrder, pricingRequest, _) >> pricingResponse
		
		when:
		PricingResponse resp= service.priceOrder(pricingRequest)
		
		then:
		1*header.setSiteId(Site.TBS_BED_BATH_US)
		1* manager.pushSiteContext(context)
		1*pTools.priceOrderSubtotalShipping(bbbOrder, _, _,_,_, _, _)
		1*header.setSiteId(Site.BED_BATH_US)
		resp == pricingResponse
	}
	
	
	def"priceOrder when SiteContextExeption is thrown"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def SiteContextManager manager =Mock()
		def MessageHeader header =Mock()
		
		setSpyParameters()
		pricingRequest.getHeader() >> header 
		1*header.getSiteId() >> null
		service.setSiteContextManager(manager)
		1*manager.getSiteContext(_) >> {throw new SiteContextException("")}

		when:
		PricingResponse price = service.priceOrder(pricingRequest)
		
		then:
		1*service.logError("Site exception while performing pricing operation", _);
		BBBSystemException e = thrown()
		0*header.setSiteId(Site.TBS_BED_BATH_US)
		price == null
	}
	
	def"priceOrder when RepositoryException is thrown"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def SiteContextManager manager =Mock()
		def MessageHeader header =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		
		setSpyParameters()
		pricingRequest.getHeader() >> header
		1*header.getSiteId() >> null
		service.setSiteContextManager(manager)
		1*manager.getSiteContext(_) >> {throw new RepositoryException("")}

		when:
		PricingResponse price = service.priceOrder(pricingRequest)
		
		then:
		1*service.logError("Repository exception while performing pricing operation", _)
		BBBSystemException e = thrown()
		0*header.setSiteId(Site.TBS_BED_BATH_US)
		price == null
	}
	
	def"when pricing header is null"(){
		
		given:
		def PricingRequest pricingRequest =Mock()
		def SiteContextManager manager =Mock()
		def MessageHeader header =Mock()
		def SiteContext context =Mock()
		def atg.multisite.Site site1 =Mock()
		def RepositoryItem priceList = Mock()
		def BBBOrder bbbOrder = Mock()
		def PricingResponse pricingResponse =Mock()
		def RepositoryItem item =Mock()
		def RepositoryItem item1 =Mock()
		def RepositoryItem item2 =Mock()
		
		pricingRequest.getHeader() >> null >>header >> null
		
		header.getSiteId() >> null
		service.setSiteContextManager(manager)
		1*manager.getSiteContext(_) >> context
		2*context.getSite() >> site1
		1*site1.getId() >> "123"
		
		service.setProfileTools(tools)
		1*priceManager.determinePriceList(_, site1,priceManager.getPriceListPropertyName()) >> priceList
		header.getCallingAppCode() >> "PX"
		1*mapper.transformRequestToOrder(pricingRequest, _, _) >> bbbOrder
		
		Map<String, Collection<RepositoryItem>> pricingModels = new HashMap()
		pricingModels.put(BBBCheckoutConstants.SHIPPING_PROMOTIONS, [item])
		pricingModels.put(BBBCheckoutConstants.ITEM_PROMOTIONS, [item1])
		pricingModels.put(BBBCheckoutConstants.ORDER_PROMOTIONS, [item2])
		
		1*mapper.populatePromotions(bbbOrder, pricingRequest, _) >> pricingModels
		1*engine.getPricingModels(_) >> []
		1*mapper.transformOrderToResponse(bbbOrder, pricingRequest, _) >> pricingResponse
		
		when:
		PricingResponse price = service.priceOrder(pricingRequest)
		
		then:
		0*header.setSiteId(Site.TBS_BED_BATH_US)
		1* manager.pushSiteContext(context)
		1*pTools.priceOrderSubtotalShipping(bbbOrder, _, _,_,_, _, _)
		0*header.setSiteId(Site.BED_BATH_US)
		price == pricingResponse
		
	}
	

}
