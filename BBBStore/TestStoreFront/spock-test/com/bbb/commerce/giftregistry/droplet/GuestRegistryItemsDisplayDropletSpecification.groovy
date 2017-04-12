package com.bbb.commerce.giftregistry.droplet

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.SKUDetailVO
import com.bbb.commerce.checklist.manager.CheckListManager
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager
import com.bbb.commerce.giftregistry.vo.GuestRegistryItemsFirstCallVO;
import com.bbb.commerce.giftregistry.vo.GuestRegistryItemsListVO;
import com.bbb.commerce.giftregistry.vo.OmnitureGuestRegistryVO
import com.bbb.commerce.giftregistry.vo.RegistryItemVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.profile.session.BBBSessionBean

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class GuestRegistryItemsDisplayDropletSpecification extends BBBExtendedSpec {
	
	GuestRegistryItemsDisplayDroplet testObj
	CheckListManager checkListManagerMock = Mock()
	BBBCatalogTools catalogToolsMock = Mock()
	GiftRegistryManager giftRegistryManagerMock = Mock()
	BBBSessionBean sessionBeanMock = Mock()
	GuestRegistryItemsListVO guestRegistryItemsListVOMock = Mock()
	RegistryItemVO registryItemVOMock = Mock()
	OmnitureGuestRegistryVO omnitureGuestRegistryVOMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	def setup(){
		testObj = new GuestRegistryItemsDisplayDroplet(checkListManager:checkListManagerMock,catalogTools:catalogToolsMock,giftRegistryManager:giftRegistryManagerMock)
	}
	
	////////////////////////////////////////TestCases for service --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public void service(DynamoHttpServletRequest req, DynamoHttpServletResponse res) ///////////
	
	def"service method. This TC is when isFirstAjaxCall is true"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> TRUE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
			//fetchFirstCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "23232555"
			requestMock.getParameter(BBBGiftRegistryConstants.VIEW ) >> "2"
			requestMock.getParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED) >> TRUE
			RegistryItemVO registryItemVOMock = new RegistryItemVO()
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO()
			RegistryItemVO registryItemVOMock2 = new RegistryItemVO()
			RegistryItemVO registryItemVOMock3 = new RegistryItemVO()
			1 * giftRegistryManagerMock.fetchRegItemsListByCategory("eventType","23232555") >> [registryItemVOMock,registryItemVOMock1]
			1 * giftRegistryManagerMock.enableBuyOffStartBrowsing([registryItemVOMock,registryItemVOMock1]) >> TRUE
			1 * giftRegistryManagerMock.removeRegItemsBasedOnFilter([registryItemVOMock,registryItemVOMock1],"2") >> [registryItemVOMock2,registryItemVOMock3]
			requestMock.getParameter(BBBGiftRegistryConstants.EVENT_DATE) >> "12/12/2016"
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBeanMock
			testObj.getSessionBean() >> sessionBeanMock
			
			//fetchCategoryList Private Method Coverage
			1 * checkListManagerMock.getEPHCategoryBasedOnRegistryType("eventType") >> ["one":"firstValue","two":"secondValue"]
		
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("showStartBrowsing",TRUE)
			1 * requestMock.setParameter("regItemCount", 2)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,	null)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST, ["one", "two", "other"])
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			1 * giftRegistryManagerMock.populateCategoryMap(["one":"firstValue", "two":"secondValue", "other":"other"],_,_,_,["one", "two", "other"])
			testObj.getSessionBean().equals(sessionBeanMock)
	}
	
	def"service method. This TC is when isSecondAjaxCall is true"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> TRUE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
			//fetchAllCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "232312311"
			requestMock.getParameter(BBBGiftRegistryConstants.EVENT_DATE) >> "12/12/2016"
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBeanMock
			GuestRegistryItemsListVO guestRegistryItemsListVOMock1 = Mock()
			GuestRegistryItemsListVO guestRegistryItemsListVOMock2 = Mock()
			GuestRegistryItemsListVO guestRegistryItemsListVOMock3 = Mock()
			sessionBeanMock.getValues().put("giftGiverInStockRegItems232312311",["one":guestRegistryItemsListVOMock,"two":guestRegistryItemsListVOMock1])
			requestMock.getParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED) >> TRUE
			sessionBeanMock.getValues().put("giftGiverOutOfStockRegItems232312311",["odd":guestRegistryItemsListVOMock2,"even":guestRegistryItemsListVOMock3])
			OmnitureGuestRegistryVO omnitureGuestRegistryVOMock1 = Mock()
			sessionBeanMock.getValues().put("omnitureList",[omnitureGuestRegistryVOMock,omnitureGuestRegistryVOMock1])
			2 * giftRegistryManagerMock.fliterNotAvliableItem(_)
			1 * giftRegistryManagerMock.removeOutOfStockItems(_,guestRegistryItemsListVOMock,TRUE)
			1 * giftRegistryManagerMock.populateSKUDetailsInRegItem(guestRegistryItemsListVOMock,TRUE)
			1 * giftRegistryManagerMock.setLTLAttributesInRegItem(guestRegistryItemsListVOMock, "12/12/2016")
			SKUDetailVO sKUDetailVOMock = new SKUDetailVO(parentProdId:"prod23456")
			SKUDetailVO sKUDetailVOMock1 = new SKUDetailVO(parentProdId:"prod34567")
			RegistryItemVO registryItemVOMock = new RegistryItemVO(sku:12346l,qtyRequested:1,sKUDetailVO:sKUDetailVOMock,price:"22.99")
			RegistryItemVO registryItemVOMock1 = Mock()
			1 * registryItemVOMock1.getSku() >> 23462l
			2 * registryItemVOMock1.getQtyRequested() >> 1
			1 * registryItemVOMock1.getsKUDetailVO() >> sKUDetailVOMock1
			1 * registryItemVOMock1.getPrice() >> 27.99
			guestRegistryItemsListVOMock.getRegistryItemList() >> [registryItemVOMock,registryItemVOMock1] 
			1 * giftRegistryManagerMock.personlizeImageUrl(guestRegistryItemsListVOMock.getRegistryItemList())
			1 * giftRegistryManagerMock.populatePriceInfoInRegItem(guestRegistryItemsListVOMock)
			
			1 * giftRegistryManagerMock.removeOutOfStockItems(_,guestRegistryItemsListVOMock1,TRUE)
			1 * giftRegistryManagerMock.populateSKUDetailsInRegItem(guestRegistryItemsListVOMock1,TRUE)
			1 * giftRegistryManagerMock.setLTLAttributesInRegItem(guestRegistryItemsListVOMock1, "12/12/2016")
			guestRegistryItemsListVOMock1.getRegistryItemList() >> null
			1 * guestRegistryItemsListVOMock1.getCategoryId() >> "cat12345" 
			1 * giftRegistryManagerMock.personlizeImageUrl(guestRegistryItemsListVOMock1.getRegistryItemList())
			1 * giftRegistryManagerMock.populatePriceInfoInRegItem(guestRegistryItemsListVOMock1)
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,	["one":guestRegistryItemsListVOMock,"two":guestRegistryItemsListVOMock1])
			1 * requestMock.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST, ["cat12345"])
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			testObj.getSessionBean().equals(sessionBeanMock)
			testObj.getSessionBean().getValues().get("giftGiverOutOfStockRegItems232312311").equals("odd":guestRegistryItemsListVOMock2,"even":guestRegistryItemsListVOMock3) 
	}
	
	def"service method. This TC is when isThirdAjaxCall is true"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> TRUE
			
			//fetchBelowLineCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "232312311"
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBeanMock
			requestMock.getParameter(BBBGiftRegistryConstants.EVENT_DATE) >> "12/12/2016"
			GuestRegistryItemsListVO guestRegistryItemsListVOMock1 = Mock()
			sessionBeanMock.getValues().put("giftGiverOutOfStockRegItems232312311",["odd":guestRegistryItemsListVOMock,"even":guestRegistryItemsListVOMock1])
			OmnitureGuestRegistryVO omnitureGuestRegistryVOMock1 = Mock()
			sessionBeanMock.getValues().put("omnitureList",[omnitureGuestRegistryVOMock,omnitureGuestRegistryVOMock1])
			
			1 * giftRegistryManagerMock.fliterNotAvliableItem(guestRegistryItemsListVOMock)
			1 * giftRegistryManagerMock.populateSKUDetailsInRegItem(guestRegistryItemsListVOMock,false)
			1 * giftRegistryManagerMock.setLTLAttributesInRegItem(guestRegistryItemsListVOMock, "12/12/2016")
			SKUDetailVO sKUDetailVOMock = new SKUDetailVO(parentProdId:"prod23456")
			SKUDetailVO sKUDetailVOMock1 = new SKUDetailVO(parentProdId:"prod34567")
			1 * registryItemVOMock.getSku() >> 12346l
			2 * registryItemVOMock.getQtyRequested() >> 1
			1 * registryItemVOMock.getsKUDetailVO() >> sKUDetailVOMock
			1 * registryItemVOMock.getPrice() >> 22.99
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO(sku:23462l,qtyRequested:1,sKUDetailVO:sKUDetailVOMock1,price:"27.99")
			guestRegistryItemsListVOMock.getRegistryItemList() >> [registryItemVOMock,registryItemVOMock1]
			1 * giftRegistryManagerMock.personlizeImageUrl(guestRegistryItemsListVOMock.getRegistryItemList())
			1 * giftRegistryManagerMock.populatePriceInfoInRegItem(guestRegistryItemsListVOMock)
			
			1 * giftRegistryManagerMock.fliterNotAvliableItem(guestRegistryItemsListVOMock1)
			1 * giftRegistryManagerMock.populateSKUDetailsInRegItem(guestRegistryItemsListVOMock1,false)
			1 * giftRegistryManagerMock.setLTLAttributesInRegItem(guestRegistryItemsListVOMock1, "12/12/2016")
			guestRegistryItemsListVOMock.getRegistryItemList() >> null
			1 * giftRegistryManagerMock.personlizeImageUrl(guestRegistryItemsListVOMock1.getRegistryItemList())
			1 * giftRegistryManagerMock.populatePriceInfoInRegItem(guestRegistryItemsListVOMock1)
			guestRegistryItemsListVOMock1.getCategoryId() >> "cat212121"
			
			//fetchOmnitureDetailsForCopyRegistry Private Method Coverage
			2 * omnitureGuestRegistryVOMock.getParentProductId() >> "prod78787" 
			1 * omnitureGuestRegistryVOMock.getQuantity() >> 1
			1 * omnitureGuestRegistryVOMock.getPrice() >> 85.99d
			1 * omnitureGuestRegistryVOMock.getSkuId() >> 2323265l
			
			2 * omnitureGuestRegistryVOMock1.getParentProductId() >> "prod98989"
			1 * omnitureGuestRegistryVOMock1.getQuantity() >> 1
			1 * omnitureGuestRegistryVOMock1.getPrice() >> 22.99d
			1 * omnitureGuestRegistryVOMock1.getSkuId() >> 2328565l
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,["odd":guestRegistryItemsListVOMock,"even":guestRegistryItemsListVOMock1])
			1 * requestMock.setParameter(BBBGiftRegistryConstants.ITEM_LIST, 'prod78787;prod98989;prod23456;prod34567')
			1 * requestMock.setParameter("omnitureList",_)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			testObj.getSessionBean().equals(sessionBeanMock)
			
	}
	
	def"service method. This TC is when isFirstAjaxCall,isSecondAjaxCall,isThirdAjaxCall are false and eventTypeCode is empty"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> ""
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS, null)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			
	}
	
	def"service method. This TC is when isFirstAjaxCall is true and eventTypeCode is null"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> null
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> TRUE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
			//fetchFirstCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "23232555"
			requestMock.getParameter(BBBGiftRegistryConstants.VIEW ) >> ""
			requestMock.getParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED) >> FALSE
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,	null)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST, [])
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"service method. This TC is when isFirstAjaxCall is true and registryId is null"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> TRUE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
			//fetchFirstCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> null
			requestMock.getParameter(BBBGiftRegistryConstants.VIEW ) >> ""
			requestMock.getParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED) >> ""
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,	null)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST, [])
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"service method. This TC is when isFirstAjaxCall is true and BBBBusinessException thrown so regItemsList is null"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> TRUE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
			//fetchFirstCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "23232555"
			requestMock.getParameter(BBBGiftRegistryConstants.VIEW ) >> ""
			requestMock.getParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED) >> FALSE
			1 * giftRegistryManagerMock.fetchRegItemsListByCategory("eventType","23232555") >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * testObj.logError('Mock for BBBBusinessException', _)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,	null)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST, [])
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			1 * testObj.logDebug('Entering fetchFirstCategoryItems| GuestRegistryItemsDisplayDroplet with Registry Type : eventType registry id :23232555')
			1 * testObj.logDebug('Entering GuestRegistryItemsDisplayDroplet with Registry Type : eventType')
			1 * testObj.logDebug('regItemsList is null for registry id :23232555')
	}
	
	def"service method. This TC is when isFirstAjaxCall is true and objectParameter of sessionBean is null"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> TRUE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
			//fetchFirstCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "23232555"
			requestMock.getParameter(BBBGiftRegistryConstants.VIEW ) >> ""
			requestMock.getParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED) >> TRUE
			RegistryItemVO registryItemVOMock = new RegistryItemVO()
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO()
			1 * giftRegistryManagerMock.fetchRegItemsListByCategory("eventType","23232555") >> [registryItemVOMock,registryItemVOMock1]
			1 * giftRegistryManagerMock.enableBuyOffStartBrowsing([registryItemVOMock,registryItemVOMock1]) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.EVENT_DATE) >> "12/12/2016"
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME) >> null
			requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			testObj.getSessionBean() >> sessionBeanMock
			
			//fetchCategoryList Private Method Coverage
			1 * checkListManagerMock.getEPHCategoryBasedOnRegistryType("eventType") >> ["one":"firstValue","other":"otherValue"]
		
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("showStartBrowsing",FALSE)
			1 * requestMock.setParameter("regItemCount", 2)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,	null)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST, ["one", "other"])
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			1 * giftRegistryManagerMock.populateCategoryMap(["one":"firstValue", "other":"otherValue"],_,_,_,["one", "other"])
			testObj.getSessionBean().equals(sessionBeanMock)
			testObj.getSessionBean().getValues().get("giftGiverOutOfStockRegItems23232555").equals([:])
			testObj.getSessionBean().getValues().get("giftGiverInStockRegItems23232555").equals([:])
			testObj.getSessionBean().getValues().get("omnitureList").equals([])
			
	}
	
	def"service method. This TC is when isFirstAjaxCall is true and BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setGiftRegistryManager(giftRegistryManagerMock)
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> TRUE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
			//fetchFirstCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "23232555"
			requestMock.getParameter(BBBGiftRegistryConstants.VIEW ) >> ""
			requestMock.getParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED) >> TRUE
			RegistryItemVO registryItemVOMock = new RegistryItemVO()
			RegistryItemVO registryItemVOMock1 = new RegistryItemVO()
			1 * giftRegistryManagerMock.fetchRegItemsListByCategory("eventType","23232555") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			1 * testObj.logError('Mock for BBBSystemException', _)
			1 * testObj.logDebug('Entering fetchFirstCategoryItems| GuestRegistryItemsDisplayDroplet with Registry Type : eventType registry id :23232555')
			1 * testObj.logDebug('Entering GuestRegistryItemsDisplayDroplet with Registry Type : eventType')
			0 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,	null)
			0 * requestMock.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST, ["one", "other"])
	}
	
	def"service method. This TC is when isSecondAjaxCall is true and regItemListVO.getRegistryItemList() is empty"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> TRUE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
			//fetchAllCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "232312311"
			requestMock.getParameter(BBBGiftRegistryConstants.EVENT_DATE) >> "12/12/2016"
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME) >> null
			requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			GuestRegistryItemsListVO guestRegistryItemsListVOMock2 = Mock()
			sessionBeanMock.getValues().put("giftGiverInStockRegItems232312311",["one": guestRegistryItemsListVOMock])
			requestMock.getParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED) >> FALSE
			sessionBeanMock.getValues().put("giftGiverOutOfStockRegItems232312311",["odd":guestRegistryItemsListVOMock2])
			OmnitureGuestRegistryVO omnitureGuestRegistryVOMock1 = Mock()
			sessionBeanMock.getValues().put("omnitureList",[omnitureGuestRegistryVOMock,omnitureGuestRegistryVOMock1])
			1 * giftRegistryManagerMock.fliterNotAvliableItem(_)
			1 * giftRegistryManagerMock.removeOutOfStockItems(_,guestRegistryItemsListVOMock,FALSE)
			1 * giftRegistryManagerMock.populateSKUDetailsInRegItem(guestRegistryItemsListVOMock,FALSE)
			1 * giftRegistryManagerMock.setLTLAttributesInRegItem(guestRegistryItemsListVOMock, "12/12/2016")
			guestRegistryItemsListVOMock.getRegistryItemList() >> []
			1 * giftRegistryManagerMock.personlizeImageUrl(guestRegistryItemsListVOMock.getRegistryItemList())
			1 * giftRegistryManagerMock.populatePriceInfoInRegItem(guestRegistryItemsListVOMock)
			guestRegistryItemsListVOMock.getCategoryId() >> "cat12345"
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,	["one":guestRegistryItemsListVOMock])
			1 * requestMock.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST, ["cat12345"])
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			testObj.getSessionBean().equals(sessionBeanMock)
			testObj.getSessionBean().getValues().get("giftGiverOutOfStockRegItems232312311").equals("odd":guestRegistryItemsListVOMock2)
	}
	
	def"service method. This TC is when isSecondAjaxCall is true and value of giftGiverInStockRegItemsKey is null"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> TRUE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
			//fetchAllCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "232312311"
			requestMock.getParameter(BBBGiftRegistryConstants.EVENT_DATE) >> "12/12/2016"
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME) >> null
			requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			GuestRegistryItemsListVO guestRegistryItemsListVOMock2 = Mock()
			sessionBeanMock.getValues().put("giftGiverInStockRegItems232312311",null)
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,	null)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST, [])
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"service method. This TC is when isSecondAjaxCall is true and isInventoryCallEnabled is empty"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> TRUE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
			//fetchAllCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "232312311"
			requestMock.getParameter(BBBGiftRegistryConstants.EVENT_DATE) >> "12/12/2016"
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME) >> null
			requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			GuestRegistryItemsListVO guestRegistryItemsListVOMock2 = Mock()
			sessionBeanMock.getValues().put("giftGiverInStockRegItems232312311",["one": guestRegistryItemsListVOMock])
			requestMock.getParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED) >> ""
			sessionBeanMock.getValues().put("giftGiverOutOfStockRegItems232312311",["odd":guestRegistryItemsListVOMock2])
			OmnitureGuestRegistryVO omnitureGuestRegistryVOMock1 = Mock()
			sessionBeanMock.getValues().put("omnitureList",[omnitureGuestRegistryVOMock,omnitureGuestRegistryVOMock1])
			1 * giftRegistryManagerMock.fliterNotAvliableItem(_)
			1 * giftRegistryManagerMock.removeOutOfStockItems(_,guestRegistryItemsListVOMock,TRUE)
			1 * giftRegistryManagerMock.populateSKUDetailsInRegItem(guestRegistryItemsListVOMock,TRUE)
			1 * giftRegistryManagerMock.setLTLAttributesInRegItem(guestRegistryItemsListVOMock, "12/12/2016")
			guestRegistryItemsListVOMock.getRegistryItemList() >> []
			1 * giftRegistryManagerMock.personlizeImageUrl(guestRegistryItemsListVOMock.getRegistryItemList())
			1 * giftRegistryManagerMock.populatePriceInfoInRegItem(guestRegistryItemsListVOMock)
			guestRegistryItemsListVOMock.getCategoryId() >> "cat12345"
			
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,	["one":guestRegistryItemsListVOMock])
			1 * requestMock.setParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST, ["cat12345"])
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			testObj.getSessionBean().equals(sessionBeanMock)
			testObj.getSessionBean().getValues().get("giftGiverOutOfStockRegItems232312311").equals("odd":guestRegistryItemsListVOMock2)
	}
	
	def"service method. This TC is when isFirstAjaxCall,isSecondAjaxCall,isThirdAjaxCall are false and categoryMap is null"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			1 * checkListManagerMock.getEPHCategoryBasedOnRegistryType("eventType") >> null
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS, null)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			
	}
	
	def"service method. This TC is when isFirstAjaxCall,isSecondAjaxCall,isThirdAjaxCall are false and BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setCheckListManager(checkListManagerMock)
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			1 * checkListManagerMock.getEPHCategoryBasedOnRegistryType("eventType") >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * testObj.logError('Error fetching ephCategoriesList', _)
			1 * testObj.logDebug('Inside checkRegistryType For eventTypeCode eventType')
			1 * testObj.logDebug('Entering GuestRegistryItemsDisplayDroplet with Registry Type : eventType')
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS, null)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			
	}
	
	def"service method. This TC is when isThirdAjaxCall is true and regItemListVO.getRegistryItemList() and omnitureList is empty"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> TRUE
			
			//fetchBelowLineCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "232312311"
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME) >> null
			requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter(BBBGiftRegistryConstants.EVENT_DATE) >> "12/12/2016"
			sessionBeanMock.getValues().put("giftGiverOutOfStockRegItems232312311",["odd":guestRegistryItemsListVOMock])
			sessionBeanMock.getValues().put("omnitureList",[])
			
			1 * giftRegistryManagerMock.fliterNotAvliableItem(guestRegistryItemsListVOMock)
			1 * giftRegistryManagerMock.populateSKUDetailsInRegItem(guestRegistryItemsListVOMock,false)
			1 * giftRegistryManagerMock.setLTLAttributesInRegItem(guestRegistryItemsListVOMock, "12/12/2016")
			guestRegistryItemsListVOMock.getRegistryItemList() >> []
			1 * giftRegistryManagerMock.personlizeImageUrl(guestRegistryItemsListVOMock.getRegistryItemList())
			1 * giftRegistryManagerMock.populatePriceInfoInRegItem(guestRegistryItemsListVOMock)
			guestRegistryItemsListVOMock.getCategoryId() >> "cat212121"
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,["odd":guestRegistryItemsListVOMock])
			1 * requestMock.setParameter("omnitureList", _)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			testObj.getSessionBean().equals(sessionBeanMock)
	}
	
	def"service method. This TC is when isThirdAjaxCall is true and certonaProductList.length is lt 1 in fetchOmnitureDetailsForCopyRegistry Private method"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> TRUE
			
			//fetchBelowLineCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "232312311"
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME) >> null
			requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			requestMock.getParameter(BBBGiftRegistryConstants.EVENT_DATE) >> "12/12/2016"
			sessionBeanMock.getValues().put("giftGiverOutOfStockRegItems232312311",["odd":guestRegistryItemsListVOMock])
			sessionBeanMock.getValues().put("omnitureList",[omnitureGuestRegistryVOMock])
			
			1 * giftRegistryManagerMock.fliterNotAvliableItem(guestRegistryItemsListVOMock)
			1 * giftRegistryManagerMock.populateSKUDetailsInRegItem(guestRegistryItemsListVOMock,false)
			1 * giftRegistryManagerMock.setLTLAttributesInRegItem(guestRegistryItemsListVOMock, "12/12/2016")
			guestRegistryItemsListVOMock.getRegistryItemList() >> []
			1 * giftRegistryManagerMock.personlizeImageUrl(guestRegistryItemsListVOMock.getRegistryItemList())
			1 * giftRegistryManagerMock.populatePriceInfoInRegItem(guestRegistryItemsListVOMock)
			guestRegistryItemsListVOMock.getCategoryId() >> "odd"
			
			//fetchOmnitureDetailsForCopyRegistry Private Method Coverage
			2 * omnitureGuestRegistryVOMock.getParentProductId() >> ""
			1 * omnitureGuestRegistryVOMock.getQuantity() >> 1
			1 * omnitureGuestRegistryVOMock.getPrice() >> 85.99d
			1 * omnitureGuestRegistryVOMock.getSkuId() >> 2323265l
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,[:])
			1 * requestMock.setParameter("omnitureList", _)
			1 * requestMock.setParameter("emptyOutOfStockListFlag",true)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			testObj.getSessionBean().equals(sessionBeanMock)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.ITEM_LIST, "")
	}
	
	def"service method. This TC is when isThirdAjaxCall is true and giftGiverOutOfStockRegItemsKey value is null"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> TRUE
			
			//fetchBelowLineCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "232312311"
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBeanMock
			sessionBeanMock.getValues().put("giftGiverOutOfStockRegItems232312311",null)
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,null)
			1 * requestMock.setParameter("omnitureList", _)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			testObj.getSessionBean().equals(sessionBeanMock)
	}
	
	def"service method. This TC is when isThirdAjaxCall is true and giftGiverOutOfStockRegItemsKey value is empty"(){
		given:
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> "eventType"
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> TRUE
			
			//fetchBelowLineCategoryItems Private Method Coverage
			requestMock.getParameter(BBBGiftRegistryConstants.REGISTRY_ID) >> "232312311"
			BBBSessionBean sessionBeanMock = new BBBSessionBean()
			requestMock.getObjectParameter(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBeanMock
			sessionBeanMock.getValues().put("giftGiverOutOfStockRegItems232312311",[:])
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS, null)
			1 * requestMock.setParameter("omnitureList", _)
			1 * requestMock.setParameter("emptyOutOfStockListFlag",true)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			testObj.getSessionBean().equals(sessionBeanMock)
	}
	
	////////////////////////////////////////TestCases for service --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for getDetailsForOOS --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public List<GuestRegistryItemsListVO> getDetailsForOOS(String isThirdAjaxCall,String view,String registryId,String invCheckEnabled,String eventDate) ///////////
	
	def"getDetailsForOOS. This TC is the Happy flow of getDetailsForOOS method"(){
		given:
			String isThirdAjaxCall = "true"
			String view = "view"
			String registryId = "22323565"
			String invCheckEnabled = "true"
			String eventDate = "12/12/2016"
			guestRegistryItemsListVOMock.getRegistryItemList() >> [registryItemVOMock]
			GuestRegistryItemsListVO guestRegistryItemsListVOMock1 = Mock()
			guestRegistryItemsListVOMock1.getRegistryItemList() >> []
			requestMock.getObjectParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS) >> ["odd":guestRegistryItemsListVOMock,"even":guestRegistryItemsListVOMock1]
		
			//service method
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> ""
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
		when:
			List<GuestRegistryItemsListVO> results = testObj.getDetailsForOOS(isThirdAjaxCall, view, registryId, invCheckEnabled, eventDate)
			
		then:
			results == [guestRegistryItemsListVOMock]
			1 * requestMock.setParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL, isThirdAjaxCall)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.VIEW, view)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EVENT_DATE, eventDate)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED, invCheckEnabled)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS,	null)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.OPARAM, requestMock, responseMock)
			
	}
	
	def"getDetailsForOOS. This TC is when ServletException thrown"(){
		given:
			testObj = Spy()
			String isThirdAjaxCall = "true"
			String view = "view"
			String registryId = "22323565"
			String invCheckEnabled = "true"
			String eventDate = "12/12/2016"
			testObj.service(requestMock,responseMock) >> {throw new ServletException("Mock for ServletException")}
			
		when:
			List<GuestRegistryItemsListVO> results = testObj.getDetailsForOOS(isThirdAjaxCall, view, registryId, invCheckEnabled, eventDate)
			
		then:
			results == []
			1 * testObj.logError('GuestRegistryItemsDisplayDroplet:getDetailsForOOS() ServletException: ', _)
			1 * testObj.logDebug('GuestRegistryItemsDisplayDroplet:getDetailsForOOS() method with input parameters:isThirdAjaxCall: true, view: view,registryId: 22323565,invCheckEnabled: true,eventDate: 12/12/2016')
			1 * testObj.logDebug('Calling the service method of GuestRegistryItemsDisplayDroplet')
			1 * requestMock.setParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL, isThirdAjaxCall)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.VIEW, view)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EVENT_DATE, eventDate)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED, invCheckEnabled)
	}
	
	def"getDetailsForOOS. This TC is when IOException thrown"(){
		given:
			testObj = Spy()
			String isThirdAjaxCall = "true"
			String view = "view"
			String registryId = "22323565"
			String invCheckEnabled = "true"
			String eventDate = "12/12/2016"
			testObj.service(requestMock,responseMock) >> {throw new IOException("Mock for IOException")}
			
		when:
			List<GuestRegistryItemsListVO> results = testObj.getDetailsForOOS(isThirdAjaxCall, view, registryId, invCheckEnabled, eventDate)
			
		then:
			results == []
			1 * testObj.logError('GuestRegistryItemsDisplayDroplet:getDetailsForOOS() IOException: ', _)
			1 * testObj.logDebug('GuestRegistryItemsDisplayDroplet:getDetailsForOOS() method with input parameters:isThirdAjaxCall: true, view: view,registryId: 22323565,invCheckEnabled: true,eventDate: 12/12/2016')
			1 * testObj.logDebug('Calling the service method of GuestRegistryItemsDisplayDroplet')
			1 * requestMock.setParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL, isThirdAjaxCall)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.VIEW, view)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EVENT_DATE, eventDate)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED, invCheckEnabled)
	}
	
	////////////////////////////////////////TestCases for getDetailsForOOS --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for getDetailsForAllCat --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public GuestRegistryItemsFirstCallVO getDetailsForAllCat(String isSecondAjaxCall,String registryId,String view,String invCheckEnabled,String eventDate) ///////////
	
	def"getDetailsForAllCat. This TC is the Happy flow of getDetailsForAllCat method"(){
		given:
			String isSecondAjaxCall = "true"
			String view = "view"
			String registryId = "22323565"
			String invCheckEnabled = "true"
			String eventDate = "12/12/2016"
			guestRegistryItemsListVOMock.getRegistryItemList() >> [registryItemVOMock]
			GuestRegistryItemsListVO guestRegistryItemsListVOMock1 = Mock()
			guestRegistryItemsListVOMock1.getRegistryItemList() >> []
			requestMock.getObjectParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS) >> ["odd":guestRegistryItemsListVOMock,"even":guestRegistryItemsListVOMock1]
			requestMock.getObjectParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST) >> ["2","5"]
			
			//service method
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> ""
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
		when:
			GuestRegistryItemsFirstCallVO results = testObj.getDetailsForAllCat(isSecondAjaxCall, registryId, view, invCheckEnabled, eventDate)
			
		then:
			results.getRemainingCategoryBuckets().equals([guestRegistryItemsListVOMock])
			results.getNotInStockCategoryList().equals(["2","5"])
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.VIEW, view)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL, isSecondAjaxCall)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EVENT_DATE,eventDate)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED, invCheckEnabled)
			
	}
	
	def"getDetailsForAllCat. This TC is when ServletException thrown"(){
		given:
			testObj = Spy()
			String isSecondAjaxCall = "true"
			String view = "view"
			String registryId = "22323565"
			String invCheckEnabled = "true"
			String eventDate = "12/12/2016"
			testObj.service(requestMock,responseMock) >> {throw new ServletException("Mock for ServletException")}
			
		when:
			GuestRegistryItemsFirstCallVO results = testObj.getDetailsForAllCat(isSecondAjaxCall, registryId, view, invCheckEnabled, eventDate)
			
		then:
			results.getRemainingCategoryBuckets().equals(null)
			results.getNotInStockCategoryList().equals(null)
			1 * testObj.logError('GuestRegistryItemsDisplayDroplet:getDetailsForAllCat() ServletException: ', _)
			1 * testObj.logDebug('Calling the service method of GuestRegistryItemsDisplayDroplet')
			1 * testObj.logDebug('GuestRegistryItemsDisplayDroplet:getDetailsForAllCat() method with input parameters:isSecondAjaxCall: true, view: view,registryId: 22323565,invCheckEnabled: true,eventDate: 12/12/2016')
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.VIEW, view)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL, isSecondAjaxCall)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EVENT_DATE,eventDate)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED, invCheckEnabled)
			
	}
	
	def"getDetailsForAllCat. This TC is when IOException thrown"(){
		given:
			testObj = Spy()
			String isSecondAjaxCall = "true"
			String view = "view"
			String registryId = "22323565"
			String invCheckEnabled = "true"
			String eventDate = "12/12/2016"
			testObj.service(requestMock,responseMock) >> {throw new IOException("Mock for IOException")}
			
			
		when:
			GuestRegistryItemsFirstCallVO results = testObj.getDetailsForAllCat(isSecondAjaxCall, registryId, view, invCheckEnabled, eventDate)
			
		then:
			results.getRemainingCategoryBuckets().equals(null)
			results.getNotInStockCategoryList().equals(null)
			1 * testObj.logError('GuestRegistryItemsDisplayDroplet:getDetailsForAllCat() IOException: ', _)
			1 * testObj.logDebug('Calling the service method of GuestRegistryItemsDisplayDroplet')
			1 * testObj.logDebug('GuestRegistryItemsDisplayDroplet:getDetailsForAllCat() method with input parameters:isSecondAjaxCall: true, view: view,registryId: 22323565,invCheckEnabled: true,eventDate: 12/12/2016')
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.VIEW, view)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL, isSecondAjaxCall)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EVENT_DATE,eventDate)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED, invCheckEnabled)
			
	}
	
	////////////////////////////////////////TestCases for getDetailsForAllCat --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for getDetailsForFirstC1 --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public GuestRegistryItemsFirstCallVO getDetailsForFirstC1(String eventTypeCode,String isFirstAjaxCall,String registryId,String view,String invCheckEnabled,String eventDate) ///////////
	
	def"getDetailsForFirstC1. This TC is the Happy flow of getDetailsForFirstC1 method"(){
		given:
			String eventTypeCode = "typeCode"
			String isFirstAjaxCall = "true"
			String view = "view"
			String registryId = "22323565"
			String invCheckEnabled = "true"
			String eventDate = "12/12/2016"
			requestMock.getObjectParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS) >> guestRegistryItemsListVOMock
			requestMock.getObjectParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_LIST) >> ["2","5"]
			requestMock.getParameter("showStartBrowsing") >> TRUE
			requestMock.getParameter("regItemCount") >> "5"
			
			//service method
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> ""
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
		when:
			GuestRegistryItemsFirstCallVO results = testObj.getDetailsForFirstC1(eventTypeCode, isFirstAjaxCall, registryId, view, invCheckEnabled, eventDate)
			
		then:
			results.getCategoryBuckets().equals(guestRegistryItemsListVOMock)
			results.getNotInStockCategoryList().equals(["2","5"])
			results.isShowStartBrowsing().equals(TRUE)
			results.getRegItemCount().equals("5")
			1 * requestMock.setParameter(BBBGiftRegistryConstants.VIEW, view)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, eventTypeCode)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL, isFirstAjaxCall)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EVENT_DATE, eventDate)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED, invCheckEnabled)
			
	}
	
	def"getDetailsForFirstC1. This TC is when ServletException thrown"(){
		given:
			testObj = Spy()
			String eventTypeCode = "typeCode"
			String isFirstAjaxCall = "true"
			String view = "view"
			String registryId = "22323565"
			String invCheckEnabled = "true"
			String eventDate = "12/12/2016"
			testObj.service(requestMock, responseMock) >> {throw new ServletException("Mock for ServletException")}
			
		when:
			GuestRegistryItemsFirstCallVO results = testObj.getDetailsForFirstC1(eventTypeCode, isFirstAjaxCall, registryId, view, invCheckEnabled, eventDate)
			
		then:
			results.getCategoryBuckets().equals(null)
			results.getNotInStockCategoryList().equals(null)
			results.isShowStartBrowsing().equals(FALSE)
			results.getRegItemCount().equals(null)
			1 * testObj.logError('GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() ServletException: ', _)
			1 * testObj.logDebug('Calling the service method of GuestRegistryItemsDisplayDroplet')
			1 * testObj.logDebug('GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() method with input parameters:isFirstAjaxCall: true, view: view,registryId: 22323565,invCheckEnabled: true,eventDate: 12/12/2016,eventTypeCode: typeCode')
			1 * requestMock.setParameter(BBBGiftRegistryConstants.VIEW, view)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, eventTypeCode)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL, isFirstAjaxCall)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EVENT_DATE, eventDate)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED, invCheckEnabled)
			
	}
	
	def"getDetailsForFirstC1. This TC is when IOException thrown"(){
		given:
			testObj = Spy()
			String eventTypeCode = "typeCode"
			String isFirstAjaxCall = "true"
			String view = "view"
			String registryId = "22323565"
			String invCheckEnabled = "true"
			String eventDate = "12/12/2016"
			testObj.service(requestMock, responseMock) >> {throw new IOException("Mock for IOException")}
			
		when:
			GuestRegistryItemsFirstCallVO results = testObj.getDetailsForFirstC1(eventTypeCode, isFirstAjaxCall, registryId, view, invCheckEnabled, eventDate)
			
		then:
			results.getCategoryBuckets().equals(null)
			results.getNotInStockCategoryList().equals(null)
			results.isShowStartBrowsing().equals(FALSE)
			results.getRegItemCount().equals(null)
			1 * testObj.logError('GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() IOException: ', _)
			1 * testObj.logDebug('Calling the service method of GuestRegistryItemsDisplayDroplet')
			1 * testObj.logDebug('GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() method with input parameters:isFirstAjaxCall: true, view: view,registryId: 22323565,invCheckEnabled: true,eventDate: 12/12/2016,eventTypeCode: typeCode')
			1 * requestMock.setParameter(BBBGiftRegistryConstants.VIEW, view)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, eventTypeCode)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL, isFirstAjaxCall)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.EVENT_DATE, eventDate)
			1 * requestMock.setParameter(BBBGiftRegistryConstants.INVENTORY_CALL_ENABLED, invCheckEnabled)
			
	}
	
	////////////////////////////////////////TestCases for getDetailsForFirstC1 --> ENDS//////////////////////////////////////////////////////////
	
	////////////////////////////////////////TestCases for getAllC1ForGifterView --> STARTS//////////////////////////////////////////////////////////
	///////////Signature : public LinkedHashMap<String,String> getAllC1ForGifterView(String eventTypeCode) ///////////
	
	def"getAllC1ForGifterView. This TC is the Happy flow of getAllC1ForGifterView method"(){
		given:
			String eventTypeCode = "typeCode"
			requestMock.getObjectParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS) >> ["one":"firstValue","two":"secondValue"]
			
			//service method
			requestMock.getParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE) >> ""
			requestMock.getParameter(BBBGiftRegistryConstants.IS_FIRST_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_SECOND_AJAX_CALL) >> FALSE
			requestMock.getParameter(BBBGiftRegistryConstants.IS_THIRD_AJAX_CALL) >> FALSE
			
		when:
			LinkedHashMap<String,String> results = testObj.getAllC1ForGifterView(eventTypeCode)
			
		then:
			results == ["100::one":"firstValue", "101::two":"secondValue"]
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, eventTypeCode)
			
	}
	
	def"getAllC1ForGifterView. This TC is when ServletException thrown"(){
		given:
			testObj = Spy()
			String eventTypeCode = "typeCode"
			testObj.service(requestMock,responseMock) >> {throw new ServletException("Mock for ServletException")}
			
		when:
			LinkedHashMap<String,String> results = testObj.getAllC1ForGifterView(eventTypeCode)
			
		then:
			results == [:]
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, eventTypeCode)
			1 * testObj.logError('GuestRegistryItemsDisplayDroplet:getAllC1ForGifterView() ServletException: ', _)
			1 * testObj.logDebug('Calling the service method of GuestRegistryItemsDisplayDroplet')
			1 * testObj.logDebug('GuestRegistryItemsDisplayDroplet:getAllC1ForGifterView() method with input parameters: eventTypeCode: typeCode')
			
	}
	
	def"getAllC1ForGifterView. This TC is when IOException thrown"(){
		given:
			testObj = Spy()
			String eventTypeCode = "typeCode"
			testObj.service(requestMock,responseMock) >> {throw new IOException("Mock for IOException")}
			
		when:
			LinkedHashMap<String,String> results = testObj.getAllC1ForGifterView(eventTypeCode)
			
		then:
			results == [:]
			1 * requestMock.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, eventTypeCode)
			1 * testObj.logError('GuestRegistryItemsDisplayDroplet:getAllC1ForGifterView() IOException: ', _)
			1 * testObj.logDebug('Calling the service method of GuestRegistryItemsDisplayDroplet')
			1 * testObj.logDebug('GuestRegistryItemsDisplayDroplet:getAllC1ForGifterView() method with input parameters: eventTypeCode: typeCode')
	}
	
	////////////////////////////////////////TestCases for getAllC1ForGifterView --> ENDS//////////////////////////////////////////////////////////
	
}
