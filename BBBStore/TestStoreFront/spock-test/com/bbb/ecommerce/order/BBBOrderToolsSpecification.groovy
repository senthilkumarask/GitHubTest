package com.bbb.ecommerce.order

import spock.lang.specification.BBBExtendedSpec
import atg.commerce.CommerceException
import atg.commerce.order.Order
import atg.commerce.order.OrderImpl
import atg.commerce.order.OrderManager
import atg.core.util.Address
import atg.repository.ItemDescriptorImpl
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem
import atg.repository.QueryBuilder;
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView

import com.bbb.commerce.common.BBBRepositoryContactInfo
import com.bbb.commerce.order.BBBOrderPropertyManager;
import com.bbb.constants.BBBCoreConstants
import com.bbb.order.bean.BBBDetailedItemPriceInfo
import java.sql.Timestamp

class BBBOrderToolsSpecification extends BBBExtendedSpec {
	
	def BBBOrderTools orderToolsObj
	def BBBOrder orderMock = Mock()
	def OrderManager orderManagerMock = Mock()
	def Repository orderRep = Mock()
	
	def setup(){
		orderToolsObj = new BBBOrderTools(orderManager:orderManagerMock)
		orderToolsObj.setOrderRepository(orderRep)
		 BBBOrderPropertyManager propertyMnanagerMock = new BBBOrderPropertyManager()
		 propertyMnanagerMock.setOrderName("orderName")
		 propertyMnanagerMock.setSubstatusName("substatus")
		 propertyMnanagerMock.setSubmittedDateName("submittedDate")
		 propertyMnanagerMock.setCreatedByOrderIdName("createdByOrderId")
		 orderToolsObj.setDcPrefix("DC1")
		 orderToolsObj.setPropertyManager(propertyMnanagerMock)
		 orderToolsObj.setSites(["BedBathUS","BuyBuyBaby","BedBathCanada","GS_BedBathCanada","GS_BedBathUS","GS_BuyBuyBaby","TBS_BedBathUS","TBS_BuyBuyBaby","TBS_BedBathCanada"])
	}
	def "updating order status for given order"(){
		given:
		orderToolsObj.setLoggingDebug(true)
		orderMock.getId() >>  "OID1234"
		when:
		boolean updated = orderToolsObj.updateOrderSubstatus(orderMock, "Submited")
		then:
		updated 
		1*orderManagerMock.updateOrder(orderMock)
		1*orderMock.setSubStatus("Submited")
	}
	def "updating order status throw CommerceException"(){
		given:
		orderMock.getId() >>  "OID1234"
		1*orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException("Mock of commerce ExcepOtion")}
		when:
		boolean updated = orderToolsObj.updateOrderSubstatus(orderMock, "Submited")
		then:
		1*orderMock.setSubStatus("Submited")
		!updated
		Exception ex = thrown()
		ex.getMessage().equals("cart_1026:Exception occured while updating the Order substatus for [OID1234]")
	}
	def "updating order state for given order"(){
		given:
		orderToolsObj.setLoggingDebug(true)
		orderMock.getId() >>  "OID1234"
		when:
		boolean updated = orderToolsObj.updateOrderState(orderMock, 0)
		then:
		updated
		1*orderManagerMock.updateOrder(orderMock)
		1*orderMock.setState(0)
	}
	def "updating order state throw CommerceException"(){
		given:
		orderMock.getId() >>  "OID1234"
		1*orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException("Mock of commerce ExcepOtion")}
		when:
		boolean updated = orderToolsObj.updateOrderState(orderMock, 0)
		then:
		1*orderMock.setState(0)
		!updated
		Exception ex = thrown()
		ex.getMessage().equals("cart_1026:Commerce Exception while updating the Order substatus for [OID1234]")
	}
	def "invalidating cache for order empty commerce items List"(){
		given:
		orderToolsObj.setLoggingDebug(true)
		def OrderImpl orderImplMock = Mock()
		def MutableRepositoryItem mutItem = Mock()
		orderImplMock.getRepositoryItem() >> mutItem
		def ItemDescriptorImpl itemDescriptor = Mock()
		1*orderRep.getItemDescriptor(_) >> itemDescriptor
		mutItem.getPropertyValue("commerceItems") >> []
		orderToolsObj.setOrderItemDescriptorName("order")
		when:
		orderToolsObj.invalidateOrderCache(orderImplMock)
		then:
		1*itemDescriptor.removeItemFromCache(mutItem.getRepositoryId())
		
	}
	def "invalidating cache for order with  commerce items"(){
		given:
		orderToolsObj.setLoggingDebug(true)
		def OrderImpl orderImplMock = Mock()
		orderToolsObj.setOrderRepository(orderRep)
		def MutableRepositoryItem mutItem = Mock()
		orderImplMock.getRepositoryItem() >> mutItem
		def ItemDescriptorImpl itemDescriptor = Mock()
		1*orderRep.getItemDescriptor("order") >> itemDescriptor
		2*orderRep.getItemDescriptor("commerceItem") >> itemDescriptor >> null
		def MutableRepositoryItem mcommItem  = Mock()
		def MutableRepositoryItem mcommItem1 = Mock() 
		mcommItem.getRepositoryId() >>  "ci12345"
		mcommItem1.getRepositoryId() >>  "ci12346"
		mutItem.getPropertyValue("commerceItems") >> [mcommItem,mcommItem1]
		orderToolsObj.setOrderItemDescriptorName("order")
		itemDescriptor.removeItemFromCache("ci12345") >> {throw new RepositoryException("Mock of the Respository exception")}
		when:
		orderToolsObj.invalidateOrderCache(orderImplMock)
		then:
		1*orderImplMock.getId()
		0*itemDescriptor.removeItemFromCache("ci12346")
		1*itemDescriptor.removeItemFromCache("ci12345")
		1*itemDescriptor.removeItemFromCache(mutItem.getRepositoryId())
		
	}
	def "invalidating cache for order with  commerce item as null"(){
		given:
		def OrderImpl orderImplMock = Mock()
		orderToolsObj.setOrderRepository(orderRep)
		def MutableRepositoryItem mutItem = Mock()
		orderImplMock.getRepositoryItem() >> mutItem
		def ItemDescriptorImpl itemDescriptor = Mock()
		1*orderRep.getItemDescriptor("order") >> itemDescriptor
		2*orderRep.getItemDescriptor("commerceItem") >> itemDescriptor >> null
		def MutableRepositoryItem mcommItem  = Mock()
		mutItem.getPropertyValue("commerceItems") >> [mcommItem,null]
		orderToolsObj.setOrderItemDescriptorName("order")
		when:
		orderToolsObj.invalidateOrderCache(orderImplMock)
		then:
		1*orderImplMock.getId()
		2*itemDescriptor.removeItemFromCache(mcommItem.getRepositoryId())
		
	}
	def "invalidating cache for order with out commerce items List"(){
		given:
		orderToolsObj.setLoggingDebug(true)
		def OrderImpl orderImplMock = Mock()
		orderToolsObj.setOrderRepository(orderRep)
		def MutableRepositoryItem mutItem = Mock()
		orderImplMock.getRepositoryItem() >> mutItem
		def ItemDescriptorImpl itemDescriptor = Mock()
		1*orderRep.getItemDescriptor("order") >> itemDescriptor
		mutItem.getPropertyValue("commerceItems") >> null
		orderToolsObj.setOrderItemDescriptorName("order")
		when:
		orderToolsObj.invalidateOrderCache(orderImplMock)
		then:
		1*itemDescriptor.removeItemFromCache(mutItem.getRepositoryId())
	}
	def "invalidating cache for order throws repository exception "(){
		given:
		orderToolsObj.setLoggingDebug(true)
		def OrderImpl orderImplMock = Mock()
		orderToolsObj.setOrderRepository(orderRep)
		def MutableRepositoryItem mutItem = Mock()
		orderImplMock.getRepositoryItem() >> mutItem
		def ItemDescriptorImpl itemDescriptor = Mock()
		1*orderRep.getItemDescriptor(_) >> {throw new RepositoryException("Mock of repository exception")}
		mutItem.getPropertyValue("commerceItems") >> null
		orderToolsObj.setOrderItemDescriptorName("order")
		when:
		orderToolsObj.invalidateOrderCache(orderImplMock)
		then:
		0*itemDescriptor.removeItemFromCache(mutItem.getRepositoryId())
		
	}
	def "invalidating cache for order as null"(){
		given:
		orderToolsObj.setLoggingDebug(true)
		def OrderImpl orderImplMock = Mock()
		orderToolsObj.setOrderRepository(orderRep)
		def MutableRepositoryItem mutItem = Mock()
		orderImplMock.getRepositoryItem() >> mutItem
		def ItemDescriptorImpl itemDescriptor = Mock()
		0*orderRep.getItemDescriptor("order") >> itemDescriptor
		0*orderRep.getItemDescriptor("commerceItem") >> itemDescriptor >> null
		def MutableRepositoryItem mcommItem  = Mock()
		def MutableRepositoryItem mcommItem1 = Mock()
		mutItem.getPropertyValue("commerceItems") >> [mcommItem,mcommItem1]
		orderToolsObj.setOrderItemDescriptorName("order")
		when:
		orderToolsObj.invalidateOrderCache(null)
		then:
		0*orderImplMock.getId()
		0*itemDescriptor.removeItemFromCache(mutItem.getRepositoryId())
	}
	def "invalidating cache for order ,item descriptor as null"(){
		given:
		orderToolsObj.setLoggingDebug(true)
		def OrderImpl orderImplMock = Mock()
		orderToolsObj.setOrderRepository(orderRep)
		def MutableRepositoryItem mutItem = Mock()
		orderImplMock.getRepositoryItem() >> mutItem
		def ItemDescriptorImpl itemDescriptor = Mock()
		1*orderRep.getItemDescriptor("order") >> null
		0*orderRep.getItemDescriptor("commerceItem") >> itemDescriptor >> null
		def MutableRepositoryItem mcommItem  = Mock()
		def MutableRepositoryItem mcommItem1 = Mock()
		mutItem.getPropertyValue("commerceItems") >> [mcommItem,mcommItem1]
		orderToolsObj.setOrderItemDescriptorName("order")
		when:
		orderToolsObj.invalidateOrderCache(orderImplMock)
		then:
		1*orderImplMock.getId()
		0*itemDescriptor.removeItemFromCache(mcommItem.getRepositoryId())
	}
	def "invalidating cache for order repository item as null"(){
		given:
		orderToolsObj.setLoggingDebug(true)
		def OrderImpl orderImplMock = Mock()
		orderToolsObj.setOrderRepository(orderRep)
		def MutableRepositoryItem mutItem = Mock()
		orderImplMock.getRepositoryItem() >> null
		def ItemDescriptorImpl itemDescriptor = Mock()
		1*orderRep.getItemDescriptor("order") >> itemDescriptor
		0*orderRep.getItemDescriptor("commerceItem") >> itemDescriptor >> null
		def MutableRepositoryItem mcommItem  = Mock()
		def MutableRepositoryItem mcommItem1 = Mock()
		mutItem.getPropertyValue("commerceItems") >> [mcommItem,mcommItem1]
		orderToolsObj.setOrderItemDescriptorName("order")
		when:
		orderToolsObj.invalidateOrderCache(orderImplMock)
		then:
		1*orderImplMock.getId()
		0*itemDescriptor.removeItemFromCache(mcommItem.getRepositoryId())
	}
	def "updating International order state for given order"(){
		given:
		orderToolsObj.setLoggingDebug(true)
		orderToolsObj.setLoggingDebug(true)
		orderMock.getId() >>  "OID1234"
		when:
		boolean updated = orderToolsObj.updateInternationalOrderState(orderMock, "completed")
		then:
		updated
		1*orderManagerMock.updateOrder(orderMock)
		1*orderMock.setInternationalState("completed")
	}
	def "updating International order state throw CommerceException"(){
		given:
		orderMock.getId() >>  "OID1234"
		1*orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException("Mock of commerce ExcepOtion")}
		when:
		boolean updated = orderToolsObj.updateInternationalOrderState(orderMock, "completed")
		then:
		updated == false
		Exception ex = thrown()
		ex.getMessage().equals("cart_1026:Commerce Exception while updating the Order substatus for [OID1234]")
		1*orderMock.setInternationalState("completed")
	}
	def "updating internatonal details in order"(){
		given:
		orderToolsObj.setLoggingDebug(true)
		when:
		boolean success = orderToolsObj.addInternationalOrder(orderMock,"internationalOrderId","internationalState","currencyCode","countryCode")
		then:
		success
		1*orderMock.setInternationalOrderId("internationalOrderId")
		1*orderMock.setInternationalState("internationalState")
		1*orderMock.setInternationalCountryCode("countryCode")
		1*orderMock.setInternationalCurrencyCode("currencyCode")
		1*orderMock.setInternationalOrderDate(_)
		1*orderManagerMock.updateOrder(orderMock)
	}
	def "updating internatonal details in order throw commerce exception"(){
		given:
		orderToolsObj.setLoggingDebug(true)
		orderManagerMock.updateOrder(orderMock) >> {throw new CommerceException("mock of commerce exception")}
		when:
		boolean success = orderToolsObj.addInternationalOrder(orderMock,"internationalOrderId","internationalState","currencyCode","countryCode")
		then:
		Exception ex = thrown()
		ex.getMessage().equals("cart_1026:Commerce Exception while updating the Order substatus for [null]")
		success == false
		1*orderMock.setInternationalOrderId("internationalOrderId")
		1*orderMock.setInternationalState("internationalState")
		1*orderMock.setInternationalCountryCode("countryCode")
		1*orderMock.setInternationalCurrencyCode("currencyCode")
		1*orderMock.setInternationalOrderDate(_)
	}
	def "fetch tax info from db"(){
		given:
		def RepositoryItem respItem = Mock() 
		respItem.getPropertyValue(BBBCoreConstants.DPI_DSLINEITEM_ITEM_TYPE_PROPERTY) >> "itemType"
		def RepositoryItem respItem1 = Mock()
		respItem.getPropertyValue(BBBCoreConstants.DPI_DSLINEITEM_ITEM_TYPE_PROPERTY) >>"itemType1"
		BBBDetailedItemPriceInfo itemPriceinfo = new BBBDetailedItemPriceInfo()
		itemPriceinfo.setDsLineItemTaxInfos([respItem1,respItem])
		when:
		RepositoryItem teturnItem = orderToolsObj.fetchTaxInfoFromDPI(itemPriceinfo,"itemType")
		then:
		respItem == teturnItem
	}
	def "fetch tax info from db Lis is empty"(){
		given:
		BBBDetailedItemPriceInfo itemPriceinfo = new BBBDetailedItemPriceInfo()
		itemPriceinfo.setDsLineItemTaxInfos([])
		when:
		RepositoryItem teturnItem = orderToolsObj.fetchTaxInfoFromDPI(itemPriceinfo,"itemType")
		then:
		null == teturnItem
	}
	def "addres object in null"(){
		given:
		orderToolsObj = Spy()
		orderToolsObj.setLoggingDebug(true)
		when:
		boolean success = orderToolsObj.isNullAddress(null)
		then:
		success 
		1*orderToolsObj.logDebug("pAddress object is null, returning true")
	}
	def "addres object in prefix  is not null"(){
		given:
		orderToolsObj = Spy()
		orderToolsObj.setLoggingDebug(true)
		Address adr = new Address()
		adr.setPrefix("mr")
		when:
		boolean success = orderToolsObj.isNullAddress(adr)
		then:
		success == false
		1*orderToolsObj.logDebug("prefix is not null, returning false")
	}
	def "addres object in first  name is not null"(){
		given:
		orderToolsObj = Spy()
		orderToolsObj.setLoggingDebug(true)
		Address adr = new Address()
		adr.setFirstName("mr")
		when:
		boolean success = orderToolsObj.isNullAddress(adr)
		then:
		success == false
		1*orderToolsObj.logDebug("First Name is not null, returning false")
	}
	def "addres object in last  name is not null"(){
		given:
		orderToolsObj = Spy()
		orderToolsObj.setLoggingDebug(true)
		Address adr = new Address()
		adr.setLastName("mr")
		when:
		boolean success = orderToolsObj.isNullAddress(adr)
		then:
		success == false
		1*orderToolsObj.logDebug("Last Name is not null, returning false")
	}
	def "addres object in middle  name is not null"(){
		given:
		orderToolsObj = Spy()
		orderToolsObj.setLoggingDebug(true)
		Address adr = new Address()
		adr.setMiddleName("mr")
		when:
		boolean success = orderToolsObj.isNullAddress(adr)
		then:
		success == false
		1*orderToolsObj.logDebug("Middle Name is not null, returning false")
	}
	def "addres object in Suffix is not null"(){
		given:
		orderToolsObj = Spy()
		orderToolsObj.setLoggingDebug(true)
		Address adr = new Address()
		adr.setSuffix("mr")
		when:
		boolean success = orderToolsObj.isNullAddress(adr)
		then:
		success == false
		1*orderToolsObj.logDebug("Suffix is not null, returning false")
	}
	def "addres object in Address1 is not null"(){
		given:
		orderToolsObj = Spy()
		orderToolsObj.setLoggingDebug(true)
		Address adr = new Address()
		adr.setAddress1("mr")
		when:
		boolean success = orderToolsObj.isNullAddress(adr)
		then:
		success == false
		1*orderToolsObj.logDebug("Address1 is not null, returning false")
	}
	def "addres object in Address2 is not null"(){
		given:
		orderToolsObj = Spy()
		orderToolsObj.setLoggingDebug(true)
		Address adr = new Address()
		adr.setAddress2("mr")
		when:
		boolean success = orderToolsObj.isNullAddress(adr)
		then:
		success == false
		1*orderToolsObj.logDebug("Address2 is not null, returning false")
	}
	def "addres object in Address3 is not null"(){
		given:
		orderToolsObj = Spy()
		orderToolsObj.setLoggingDebug(true)
		Address adr = new Address()
		adr.setAddress3("mr")
		when:
		boolean success = orderToolsObj.isNullAddress(adr)
		then:
		success == false
		1*orderToolsObj.logDebug("Address3 is not null, returning false")
	}
	def "addres object in Postal Code is not null"(){
		given:
		orderToolsObj = Spy()
		orderToolsObj.setLoggingDebug(true)
		Address adr = new Address()
		adr.setPostalCode("mr")
		when:
		boolean success = orderToolsObj.isNullAddress(adr)
		then:
		success == false
		1*orderToolsObj.logDebug("Postal Code is not null, returning false")
	}
	def "addres object in City is not null"(){
		given:
		orderToolsObj = Spy()
		orderToolsObj.setLoggingDebug(true)
		Address adr = new Address()
		adr.setCity("mr")
		when:
		boolean success = orderToolsObj.isNullAddress(adr)
		then:
		success == false
		1*orderToolsObj.logDebug("City is not null, returning false")
	}
	def "addres object in Country is not null"(){
		given:
		orderToolsObj = Spy()
		orderToolsObj.setLoggingDebug(true)
		Address adr = new Address()
		adr.setCountry("mr")
		when:
		boolean success = orderToolsObj.isNullAddress(adr)
		then:
		success == false
		1*orderToolsObj.logDebug("Country is not null, returning false")
	}
	def "addres object in owner id is  null"(){
		given:
		orderToolsObj = Spy()
		orderToolsObj.setLoggingDebug(true)
		Address adr = new Address()
		when:
		boolean success = orderToolsObj.isNullAddress(adr)
		then:
		success == true
		1*orderToolsObj.logDebug("Owner Id :: " + adr.getOwnerId())
	}
	def "addres object in owner id  is not null"(){
		given:
		orderToolsObj = Spy()
		orderToolsObj.setLoggingDebug(true)
		Address adr = new Address()
		adr.setOwnerId("OwnerId")
		when:
		boolean success = orderToolsObj.isNullAddress(adr)
		then:
		success == false
		1*orderToolsObj.logDebug("Owner Id :: " + adr.getOwnerId())
	}
	def "creating billing address"(){
		given:
		def MutableRepositoryItem mItem = Mock()
		def MutableRepository mRep= Mock()
		orderToolsObj.setOrderRepository(mRep)
		mRep.createItem("bbbAddress") >> mItem
		when:
		BBBRepositoryContactInfo repoConInfo = orderToolsObj.createBillingAddress()
		then:
		repoConInfo != null
	}
	def "Only pull those orders which are submitted, but haven't been sent to TIBCO"(){
		given:
		def RepositoryItemDescriptor orderItemDescriptor =Mock()
		def RepositoryView rView = Mock()
		def QueryBuilder orderQueryBuilder = Mock()
		orderRep.getItemDescriptor("orderName") >> orderItemDescriptor
		orderItemDescriptor.getRepositoryView() >> rView
		rView.getQueryBuilder() >> orderQueryBuilder
		def RepositoryItem rItem = Mock()
		List<RepositoryItem> lItem = new ArrayList<RepositoryItem>()
		lItem.add(rItem)
		rView.executeQuery(_, _) >> lItem.toArray()
		Date today = new Date()
		orderToolsObj.setLoggingDebug(true)
		when:
		RepositoryItem[] rep = orderToolsObj.getUnsubmittedOrders("completed", new Timestamp(today.getTime()), 0, 199)
		then:
		10*orderQueryBuilder.createComparisonQuery(_, _, QueryBuilder.EQUALS)
		1*orderQueryBuilder.createComparisonQuery(_, _, QueryBuilder.LESS_THAN_OR_EQUALS)
		1*orderQueryBuilder.createPatternMatchQuery(_, _, QueryBuilder.STARTS_WITH)
		rep != null && rep.length ==1
	}
	def "Only pull those orders which are submitted, but haven't been sent to TIBCO order length zer0"(){
		given:
		def RepositoryItemDescriptor orderItemDescriptor =Mock()
		def RepositoryView rView = Mock()
		def QueryBuilder orderQueryBuilder = Mock()
		orderRep.getItemDescriptor("orderName") >> orderItemDescriptor
		orderItemDescriptor.getRepositoryView() >> rView
		rView.getQueryBuilder() >> orderQueryBuilder
		def RepositoryItem rItem = Mock()
		rView.executeQuery(_, _) >> null
		Date today = new Date()
		orderToolsObj.setLoggingDebug(true)
		when:
		RepositoryItem[] rep = orderToolsObj.getUnsubmittedOrders("completed", new Timestamp(today.getTime()), 0, 199)
		then:
		10*orderQueryBuilder.createComparisonQuery(_, _, QueryBuilder.EQUALS)
		1*orderQueryBuilder.createComparisonQuery(_, _, QueryBuilder.LESS_THAN_OR_EQUALS)
		1*orderQueryBuilder.createPatternMatchQuery(_, _, QueryBuilder.STARTS_WITH)
		rep == null
	}
	def "Only pull those orders which are submitted, but haven't been sent to TIBCO order throw repository exception"(){
		given:
		def RepositoryItemDescriptor orderItemDescriptor =Mock()
		def RepositoryView rView = Mock()
		def QueryBuilder orderQueryBuilder = Mock()
		orderRep.getItemDescriptor("orderName") >> orderItemDescriptor
		1*orderItemDescriptor.getRepositoryView() >>rView
		1*rView.getQueryBuilder() >> orderQueryBuilder
		def RepositoryItem rItem = Mock()
		rView.executeQuery(_, _) >> {throw new RepositoryException("Mock of Repository Exception")}
		Date today = new Date()
		when:
		RepositoryItem[] rep = orderToolsObj.getUnsubmittedOrders("completed", new Timestamp(today.getTime()), 0, 199)
		then:
		10*orderQueryBuilder.createComparisonQuery(_, _, QueryBuilder.EQUALS)
		1*orderQueryBuilder.createComparisonQuery(_, _, QueryBuilder.LESS_THAN_OR_EQUALS)
		1*orderQueryBuilder.createPatternMatchQuery(_, _, QueryBuilder.STARTS_WITH)
		Exception ex = thrown()
		ex.getMessage().equals("cart_1026:Error while retriving unsubmitted order") 
	}
	def "fetching order from DB based on order number ,order number is null"(){
		given:
		orderToolsObj.setLoggingDebug(true)
		when:
		Order order = orderToolsObj.getOrderFromOnlineOrBopusOrderNumber("")
		then:
		order == null
	}
	def "fetching order from DB based on order number ,order number starts with olp"(){
		given:
		orderToolsObj =  Spy()
		orderToolsObj.setOrderRepository(orderRep)
		orderToolsObj.setLoggingDebug(true)
		orderToolsObj.setOrderManager(orderManagerMock)
		def RepositoryView rView = Mock()
		1*orderRep.getView("order") >> rView
		def RepositoryItem rItem = Mock()
		rItem.getRepositoryId() >> "OLP1234"
		List<RepositoryItem> lItem = new ArrayList<RepositoryItem>()
		lItem.add(rItem)
		orderToolsObj.fecthOrder(_, rView, _) >> lItem.toArray()
		1*orderManagerMock.loadOrder("OLP1234") >> orderMock
		when:
		Order order = orderToolsObj.getOrderFromOnlineOrBopusOrderNumber("OLP1234")
		then:
		order == orderMock
	}
	def "fetching order from DB based on order number ,order number starts with non olp"(){
		given:
		orderToolsObj =  Spy()
		orderToolsObj.setOrderRepository(orderRep)
		orderToolsObj.setLoggingDebug(true)
		orderToolsObj.setOrderManager(orderManagerMock)
		def RepositoryView rView = Mock()
		1*orderRep.getView("order") >> rView
		def RepositoryItem rItem = Mock()
		rItem.getRepositoryId() >> "Dc1234"
		List<RepositoryItem> lItem = new ArrayList<RepositoryItem>()
		lItem.add(rItem)
		orderToolsObj.fecthOrder(_, rView, _) >> lItem.toArray()
		0*orderManagerMock.loadOrder("OLP1234") >> orderMock
		when:
		Order order = orderToolsObj.getOrderFromOnlineOrBopusOrderNumber("Dc1234")
		then:
		order == null
	}
	def "fetching order from DB based on order number ,order number starts null items available with given number"(){
		given:
		orderToolsObj =  Spy()
		orderToolsObj.setOrderRepository(orderRep)
		orderToolsObj.setLoggingDebug(true)
		orderToolsObj.setArchiveOrderRepository(orderRep)
		orderToolsObj.setOrderManager(orderManagerMock)
		def RepositoryView rView = Mock()
		2*orderRep.getView("order") >> rView
		def RepositoryItem rItem = Mock()
		orderToolsObj.fecthOrder(_, rView, _) >> null
		0*orderManagerMock.loadOrder("OLP1234") >> orderMock
		when:
		Order order = orderToolsObj.getOrderFromOnlineOrBopusOrderNumber("Dc1234")
		then:
		order == null
	}
	def "fetching order from DB based on order number ,order number starts empty object available with given number"(){
		given:
		orderToolsObj =  Spy()
		orderToolsObj.setOrderRepository(orderRep)
		orderToolsObj.setArchiveOrderRepository(orderRep)
		orderToolsObj.setLoggingDebug(true)
		orderToolsObj.setOrderManager(orderManagerMock)
		def RepositoryView rView = Mock()
		2*orderRep.getView("order") >> rView
		def RepositoryItem rItem = Mock()
		List<RepositoryItem> lItem = new ArrayList<RepositoryItem>()
		List<RepositoryItem> lItem1 = new ArrayList<RepositoryItem>()
		rItem.getRepositoryId() >> "OLP1234"
		lItem1.add(rItem)
		orderToolsObj.fecthOrder(_, rView, _) >>  lItem.toArray() >> lItem1.toArray()
		1*orderManagerMock.loadOrder("OLP1234") >> orderMock
		when:
		Order order = orderToolsObj.getOrderFromOnlineOrBopusOrderNumber("Dc1234")
		then:
		order == orderMock
	}
	def "fetching order from DB based on order number ,order number starts with non olp and order id is null"(){
		given:
		orderToolsObj =  Spy()
		orderToolsObj.setOrderRepository(orderRep)
		orderToolsObj.setArchiveOrderRepository(orderRep)
		orderToolsObj.setLoggingDebug(false)
		orderToolsObj.setOrderManager(orderManagerMock)
		def RepositoryView rView = Mock()
		2*orderRep.getView("order") >> rView
		def RepositoryItem rItem = Mock()
		List<RepositoryItem> lItem = new ArrayList<RepositoryItem>()
		lItem.add(rItem)
		orderToolsObj.fecthOrder(_, rView, _) >> lItem.toArray()
		0*orderManagerMock.loadOrder("OLP1234") >> orderMock
		when:
		Order order = orderToolsObj.getOrderFromOnlineOrBopusOrderNumber("Dc1234")
		then:
		order == null
	}
}
