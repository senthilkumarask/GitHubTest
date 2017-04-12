package com.bbb.commerce.order.purchase

import atg.commerce.CommerceException
import atg.commerce.order.CommerceItem
import atg.commerce.order.Order
import atg.commerce.order.OrderHolder
import atg.commerce.order.purchase.CommerceItemShippingInfo
import atg.commerce.order.purchase.CommerceItemShippingInfoContainer;
import atg.commerce.order.purchase.CommerceItemShippingInfoTools
import atg.commerce.order.purchase.ElectronicShippingGroupInitializer
import atg.commerce.order.purchase.HardgoodShippingGroupInitializer
import atg.commerce.order.purchase.PurchaseProcessConfiguration
import atg.commerce.order.purchase.ShippingGroupMapContainer
import atg.nucleus.ServiceMap
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile
import atg.userprofiling.ProfileTools

import java.util.Locale
import java.util.Map;
import javax.servlet.ServletException

import com.bbb.account.BBBProfileTools
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.ShipMethodVO
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.order.bean.BBBCommerceItem
import spock.lang.specification.BBBExtendedSpec

class BBBMultiShippingManagerSpecification extends BBBExtendedSpec {
	def BBBMultiShippingManager msManager
	def OrderHolder cartMock = Mock()
	def Profile profileMock = Mock()
	def CommerceItemShippingInfoTools cisiTools = Mock()
	def PurchaseProcessConfiguration ppconfig = Mock()
	def CommerceItemShippingInfoContainer cisiContainer = Mock()
	def ShippingGroupMapContainer sgmContainer = Mock()
	def ServiceMap sgInitializer = Mock()
	def Order order = Mock()
	def BBBCommerceItem cItem1 = Mock()
	def BBBCommerceItem cItem2 = Mock()
	def BBBCommerceItem cItem3 = Mock()
	def BBBCommerceItem cItem4 = Mock()
	def BBBCommerceItem cItem5 = Mock()
	def BBBCommerceItem cItem6 = Mock()
	def BBBCommerceItem cItem7 = Mock()
	
	def CommerceItem sItem = Mock()
	def ElectronicShippingGroupInitializer esgInitializer = Mock()
    def HardgoodShippingGroupInitializer hgsgInitializer = Mock()


	def BBBCatalogTools cTools = Mock()
	def BBBProfileTools pTools = Mock()
	def CommerceItemShippingInfo cisinfo = Mock()
	def CommerceItemShippingInfo cisinfo1 = Mock()
	
	
	Locale local = Locale.forLanguageTag("en_US")
	ShipMethodVO sMethodVo = new ShipMethodVO()
	ShipMethodVO sMethodVo1 = new ShipMethodVO()
	ShipMethodVO sMethodVo2 = new ShipMethodVO()
	ShipMethodVO sMethodVo3 = new ShipMethodVO()
	ShipMethodVO sMethodVo4 = new ShipMethodVO()
	
	def setup(){
		msManager = new BBBMultiShippingManager(cart :cartMock, profile : profileMock, commerceItemShippingInfoTools : cisiTools , purchaseProcessorConfiguration : ppconfig)
	}
	def"reInitializeShippingContainers . tc to initialize the container"(){
		given:
		setSpySetter()
		boolean createOneInfoPerUnit = true
		boolean initShippingGroups =  true
		boolean initShippingInfos = true
		boolean initBasedOnOrder =  true
		boolean clearShippingGroups = true
		boolean clearShippingInfos = true
		boolean clearAll =  true
		cartMock.getCurrent() >> order
		1*requestMock.resolveName("/atg/commerce/order/purchase/ElectronicShippingGroupInitializer") >> esgInitializer
		1*requestMock.resolveName("/atg/commerce/order/purchase/HardgoodShippingGroupInitializer") >> hgsgInitializer
		
		
		ppconfig.getCommerceItemShippingInfoContainer() >> cisiContainer
		ppconfig.getShippingGroupMapContainer() >> sgmContainer
		cisiTools.clearShippingGroups(cisiContainer, sgmContainer) >> {}
		
		cisiTools.initializeUserShippingMethods(sgmContainer, profileMock,"hd", _) >> {}
		1*cisiTools.removeDeletedShippingGroups(sgmContainer)
		
		//sgInitializer.values() >> ["val"]
		1*cisiTools.initializeBasedOnOrder(order, cisiContainer, sgmContainer, _, createOneInfoPerUnit) >> {}
		msManager.isAnyShippingInfoExistsInContainter()>> false
		
		cisiTools.initializeCommerceItemShippingInfosToDefaultShipping(order, sgmContainer,sgmContainer, createOneInfoPerUnit) >> {}
		
		when:
		boolean value = msManager.reInitializeShippingContainers("hd", initShippingGroups, initShippingInfos, initBasedOnOrder, clearShippingGroups, clearShippingInfos, clearAll, createOneInfoPerUnit)
		
		then:
		value == true
		1*cisiTools.initializeUserShippingMethods(_, _,_, _)
		1 *cisiTools.initializeCommerceItemShippingInfosToDefaultShipping(_, _,_, _)
	}
	
	def"reInitializeShippingContainers . tc to initialize the container when clearShippingGroups , clearShippingInfos and  initShippingGroups is false"(){
		given:
		setSpySetter()
		boolean createOneInfoPerUnit = true
		boolean initShippingGroups =  false
		boolean initShippingInfos = true
		boolean initBasedOnOrder =  true
		boolean clearShippingGroups = false
		boolean clearShippingInfos = false
		boolean clearAll =  true
		cartMock.getCurrent() >> order
		1*requestMock.resolveName("/atg/commerce/order/purchase/ElectronicShippingGroupInitializer") >> esgInitializer
		1*requestMock.resolveName("/atg/commerce/order/purchase/HardgoodShippingGroupInitializer") >> hgsgInitializer

		ppconfig.getCommerceItemShippingInfoContainer() >> cisiContainer
		ppconfig.getShippingGroupMapContainer() >> sgmContainer
		cisiTools.clearShippingGroups(cisiContainer, sgmContainer) >> {}
		
		1*cisiTools.removeDeletedShippingGroups(sgmContainer)
		
		//sgInitializer.values() >> ["val"]
		1*cisiTools.initializeBasedOnOrder(order, cisiContainer, sgmContainer, _, createOneInfoPerUnit) >> {throw new CommerceException("exception")}
		msManager.isAnyShippingInfoExistsInContainter()>> true
		
		when:
		boolean value = msManager.reInitializeShippingContainers("hd", initShippingGroups, initShippingInfos, initBasedOnOrder, clearShippingGroups, clearShippingInfos, clearAll, createOneInfoPerUnit)
		
		then:
		value == true
		0*cisiTools.initializeUserShippingMethods(_, _,_, _)
		0 *cisiTools.initializeCommerceItemShippingInfosToDefaultShipping(_, _,_, _)
	}
	
	def"reInitializeShippingContainers . tc to initialize the container when clearShippingGroups , clearShippingInfos , cleareAll and initShippingGroups is false"(){
		given:
		setSpySetter()
		boolean createOneInfoPerUnit = true
		boolean initShippingGroups =  false
		boolean initShippingInfos = true
		boolean initBasedOnOrder =  false
		boolean clearShippingGroups = false
		boolean clearShippingInfos = false
		boolean clearAll =  false
		requestMock.resolveName("/atg/commerce/order/purchase/ElectronicShippingGroupInitializer") >> esgInitializer
		requestMock.resolveName("/atg/commerce/order/purchase/HardgoodShippingGroupInitializer") >> hgsgInitializer

		
		when:
		boolean value = msManager.reInitializeShippingContainers("hd", initShippingGroups, initShippingInfos, initBasedOnOrder, clearShippingGroups, clearShippingInfos, clearAll, createOneInfoPerUnit)
		
		then:
		value == true
		0*cisiTools.initializeUserShippingMethods(_, _,_, _)
	}
	
	def"reInitializeShippingContainers . tc to initialize the container when clear all is false"(){
		given:
		setSpySetter()
		boolean createOneInfoPerUnit = true
		boolean initShippingGroups =  false
		boolean initShippingInfos = true
		boolean initBasedOnOrder =  true
		boolean clearShippingGroups = true
		boolean clearShippingInfos = true
		boolean clearAll =  false
		cartMock.getCurrent() >> order
		requestMock.resolveName("/atg/commerce/order/purchase/ElectronicShippingGroupInitializer") >> esgInitializer
		requestMock.resolveName("/atg/commerce/order/purchase/HardgoodShippingGroupInitializer") >> hgsgInitializer

		ppconfig.getCommerceItemShippingInfoContainer() >> cisiContainer
		ppconfig.getShippingGroupMapContainer() >> sgmContainer
		cisiTools.clearShippingGroups(cisiContainer, sgmContainer) >> {}
		
		1*cisiTools.removeDeletedShippingGroups(sgmContainer)
		
		//sgInitializer.values() >> ["val"]
		1*cisiTools.initializeBasedOnOrder(order, cisiContainer, sgmContainer, _, createOneInfoPerUnit) >> {}
		msManager.isAnyShippingInfoExistsInContainter()>> true
		
		when:
		boolean value = msManager.reInitializeShippingContainers("hd", initShippingGroups, initShippingInfos, initBasedOnOrder, clearShippingGroups, clearShippingInfos, clearAll, createOneInfoPerUnit)
		
		then:
		value == true
		0*cisiTools.initializeUserShippingMethods(_, _,_, _)
		0 *cisiTools.initializeCommerceItemShippingInfosToDefaultShipping(_, _,_, _)
	}
	
	def"reInitializeShippingContainers profile is null"(){
		given:
		msManager = Spy()
		msManager.setCart(cartMock)
		msManager.setProfile(null)

		boolean createOneInfoPerUnit = true
		boolean initShippingGroups =  false
		boolean initShippingInfos = true
		boolean initBasedOnOrder =  true
		boolean clearShippingGroups = true
		boolean clearShippingInfos = true
		boolean clearAll =  false
		cartMock.getCurrent() >> order
		cartMock.getClass() >> null
		requestMock.resolveName("/atg/commerce/order/purchase/ElectronicShippingGroupInitializer") >> esgInitializer
		requestMock.resolveName("/atg/commerce/order/purchase/HardgoodShippingGroupInitializer") >> hgsgInitializer

		when:
		boolean value = msManager.reInitializeShippingContainers("hd", initShippingGroups, initShippingInfos, initBasedOnOrder, clearShippingGroups, clearShippingInfos, clearAll, createOneInfoPerUnit)
		
		then:
		value == false
	}
	
	private setSpySetter(){
		msManager = Spy()
		msManager.setCart(cartMock)
		msManager.setProfile(profileMock)
		msManager.setCommerceItemShippingInfoTools(cisiTools)
		msManager.setPurchaseProcessorConfiguration(ppconfig)
		//msManager.setShippingGroupInitializers(sgInitializer)
	
	}
	
	/******************************************* getCommerceItemShippingInfos **************************************/
	
	def"getCommerceItemShippingInfos.TC to get commerceItem shipping infos" (){
		given:
        setSpySetter()
		msManager.setCatalogTools(cTools)
		1*msManager.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false,true, false, false, true, false) >> true
		cartMock.getCurrent() >> order
		1*order.getCommerceItems() >> [cItem1,cItem3,cItem4, cItem5,cItem6 ,sItem]
		
		cItem1.isLtlItem() >> true
		cItem3.isLtlItem() >> true
		cItem4.isLtlItem() >> true
		cItem5.isLtlItem() >> true
		cItem6.isLtlItem() >> false
		
		
		cItem1.getCatalogRefId() >> "sku1"
		cItem3.getCatalogRefId() >> "sku3"
		cItem4.getCatalogRefId() >> "sku4"
		cItem5.getCatalogRefId() >> "sku5"
		cItem5.getCatalogRefId() >> "sku6"
		
		
		
		profileMock.getProfileTools() >> pTools
		pTools.getUserLocale(requestMock, responseMock) >> local
		
		1*cTools.getLTLEligibleShippingMethods("sku1", _,_) >> [sMethodVo]
		1*cTools.getLTLEligibleShippingMethods("sku3", _,_) >> [sMethodVo1]
		1*cTools.getLTLEligibleShippingMethods("sku4", _,_) >> [sMethodVo2]
		1*cTools.getLTLEligibleShippingMethods("sku5", _,_) >> []
		//1*cTools.getLTLEligibleShippingMethods("sku5", _,_) >> {throw new BBBSystemException("exception")}
		
		
		cItem1.getRegistrantShipMethod() >> "sId1"
		cItem1.getRegisryShipMethod() >> "sm1"
		
		cItem3.getRegistrantShipMethod() >> "sId1"
		cItem3.getRegisryShipMethod() >> "sm1"
		
		cItem4.getRegistrantShipMethod() >> "hah"
		cItem4.getRegisryShipMethod() >> "hah"



		sMethodVo.setShipMethodId("sId1") 
		sMethodVo1.setShipMethodId("sm1")
		sMethodVo2.setShipMethodId("sm1")
		
		
		1*ppconfig.getCommerceItemShippingInfoContainer() >> cisiContainer
		1*cisiContainer.getAllCommerceItemShippingInfos() >> [cisinfo, cisinfo1]
		
		cisinfo.getCommerceItem() >> cItem2
		cisinfo1.getCommerceItem() >> sItem
		when:
		Map<String, CommerceItemShippingInfo> value = msManager.getCommerceItemShippingInfos(true)
		then:
		value.get("0") == cisinfo
		1 * cItem1.setRegisryShipMethod('sId1')
		1 * cItem4.setRegisryShipMethod('')
		1 * cItem1.setHighestshipMethod('sId1')
		1 * cItem3.setHighestshipMethod('sm1')
	}
	
	def"getCommerceItemShippingInfos.TC when commerce ites list in order is null" (){
		given:
		setSpySetter()
		msManager.setCatalogTools(cTools)
		1*msManager.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false,true, false, false, true, false) >> true
		cartMock.getCurrent() >> order
		1*order.getCommerceItems() >> null
		

		1*ppconfig.getCommerceItemShippingInfoContainer() >> cisiContainer
		1*cisiContainer.getAllCommerceItemShippingInfos() >> [cisinfo]
		cisinfo.getCommerceItem() >> cItem2
		when:
		
		Map<String, CommerceItemShippingInfo> value = msManager.getCommerceItemShippingInfos(true)
		then:
		
		value.get("0") == cisinfo
		0 * cItem1.setRegisryShipMethod('sId1')
		0 * cItem4.setRegisryShipMethod('')
		0 * cItem1.setHighestshipMethod('sId1')
		0 * cItem3.setHighestshipMethod('sm1')
	}
	
	/***************************************** exception scenario ********************/
	
	def"getCommerceItemShippingInfos.TC for exception" (){
		given:
		setSpySetter()
		msManager.setCatalogTools(cTools)
		//1*msManager.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false,true, false, false, true, false) >> true
		cartMock.getCurrent() >> order
		1*order.getCommerceItems() >> [cItem1,cItem3,cItem4,cItem5]
		
		cItem1.isLtlItem() >> true
		cItem3.isLtlItem() >> true
		cItem4.isLtlItem() >> true
		cItem5.isLtlItem() >> true
		
		
		cItem1.getCatalogRefId() >> "sku1"
		cItem3.getCatalogRefId() >> "sku3"
		cItem4.getCatalogRefId() >> "sku4"
		cItem5.getCatalogRefId() >> "sku5"
		
		profileMock.getProfileTools() >> pTools
		pTools.getUserLocale(requestMock, responseMock) >> local >> local >> {throw new ServletException("exception")} >> {throw new IOException("exception")}

		1*cTools.getLTLEligibleShippingMethods("sku1", _,_) >> {throw new BBBSystemException("exception")}
		1*cTools.getLTLEligibleShippingMethods("sku3", _,_) >> {throw new BBBBusinessException("exception")}

		1*ppconfig.getCommerceItemShippingInfoContainer() >> cisiContainer
		1*cisiContainer.getAllCommerceItemShippingInfos() >> [cisinfo]
		cisinfo.getCommerceItem() >> cItem2
		when:
		
		Map<String, CommerceItemShippingInfo> value = msManager.getCommerceItemShippingInfos(false)
		then:
		
		value.get("0") == cisinfo
		0 * cItem1.setRegisryShipMethod('sId1')
		0 * cItem4.setRegisryShipMethod('')
		0 * cItem1.setHighestshipMethod('sId1')
		0 * cItem3.setHighestshipMethod('sm1')
	}
	
	/************************************************isAnyShippingInfoExistsInContainter************************************************/
	def"isAnyShippingInfoExistsInContainter. Tc to check there is any shipping infos in the container"(){
		given:
		def ArrayList ciInfolist = [cisinfo]

		
		ppconfig.getCommerceItemShippingInfoContainer() >> cisiContainer
		cisiContainer.getCommerceItemShippingInfoMap() >> ["c1" : ciInfolist ]
		cisiContainer.getCommerceItemShippingInfos("c1") >> ciInfolist
		
		when:
		boolean value = msManager.isAnyShippingInfoExistsInContainter()
		
		then:
		value == true
	}
	
	def"isAnyShippingInfoExistsInContainter. Tc when commerceItem shipping info object is null "(){
		given:
		def ArrayList ciInfolist = []
		def ArrayList ciInfolist1 = null
		
		1*ppconfig.getCommerceItemShippingInfoContainer() >> cisiContainer
		1*cisiContainer.getCommerceItemShippingInfoMap() >> ["c1" : ciInfolist , "c2" : ciInfolist1 ]
		cisiContainer.getCommerceItemShippingInfos("c1") >> ciInfolist
		cisiContainer.getCommerceItemShippingInfos("c2") >> ciInfolist1
		when:
		boolean value = msManager.isAnyShippingInfoExistsInContainter()
		
		then:
		value == false
	}
	
	def"isAnyShippingInfoExistsInContainter. Tc when commerceItem shipping info Map is null"(){
		given:

		1*ppconfig.getCommerceItemShippingInfoContainer() >> cisiContainer
		1*cisiContainer.getCommerceItemShippingInfoMap() >> null
			
		when:
		boolean value = msManager.isAnyShippingInfoExistsInContainter()
		
		then:
		value == false
	}
}
