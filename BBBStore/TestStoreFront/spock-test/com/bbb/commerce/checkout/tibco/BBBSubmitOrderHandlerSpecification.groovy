package com.bbb.commerce.checkout.tibco

import javax.transaction.TransactionManager

import spock.lang.specification.BBBExtendedSpec
import atg.commerce.order.AuxiliaryData
import atg.commerce.order.CommerceItem
import atg.repository.MutableRepository
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryException
import atg.servlet.DynamoHttpServletRequest
import atg.userprofiling.Profile
import atg.userprofiling.email.TemplateEmailInfoImpl

import com.bbb.browse.webservice.manager.BBBEximManager
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.common.BBBRepositoryContactInfo
import com.bbb.commerce.inventory.OnlineInventoryManagerImpl
import com.bbb.commerce.order.BBBOrderManager
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants
import com.bbb.ecommerce.order.BBBOrder
import com.bbb.email.BBBTemplateEmailSender
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.order.bean.BBBCommerceItem

class BBBSubmitOrderHandlerSpecification extends BBBExtendedSpec {
	
	def BBBSubmitOrderHandler bbbSubmitOrderHandlerMock
	
	def BBBOrder mockBBBOrder = Mock()
	def AuxiliaryData mockAuxiliaryData = Mock()
	def BBBCommerceItem mockCommerceItem = Mock()
	def BBBCatalogTools mockCatalogTools = Mock()
	def BBBEximManager mockBBBEximManager = Mock()
	def BBBOrderManager mockBBBOrderManager = Mock()
	def MutableRepository mockMutableRepository = Mock()
	def MutableRepositoryItem mockRepositoryItem = Mock()
	def TransactionManager mockTransactionManager = Mock()
	def OnlineInventoryManagerImpl mockInventoryManager = Mock()
	
	def setup(){
		bbbSubmitOrderHandlerMock = new BBBSubmitOrderHandler(inventoryManager:mockInventoryManager, catalogTools:mockCatalogTools, 
			transactionManager:mockTransactionManager, orderManager:mockBBBOrderManager)
	}
	
	def "submitOrder When order submission is successful"(){
		given:
		SubmitOrderVO submitOrderMock = new SubmitOrderVO()
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		submitOrderMock.setOrder(mockBBBOrder) 
		mockBBBOrder.getId() >> "mockId"
		1 * bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		1 * bbbSubmitOrderHandlerMock.logInfo("END: Submitting order [mockId] over TIBCO message bus");
		0 * bbbSubmitOrderHandlerMock.logError(*_)
		
		expect:
		bbbSubmitOrderHandlerMock.submitOrder(submitOrderMock)
				
	}
	
	def "submitOrder: When Submit Order VO is NULL"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		0 * bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(*_)
		0 * bbbSubmitOrderHandlerMock.logInfo("END: Submitting order [mockId] over TIBCO message bus");
		
		expect:
		bbbSubmitOrderHandlerMock.submitOrder(null)
	}
	
	def "submitOrder: When Order is NULL"(){
		given:
		SubmitOrderVO submitOrderMock = new SubmitOrderVO()
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		submitOrderMock.setOrder(null)
		0 * bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(*_)
		0 * bbbSubmitOrderHandlerMock.logInfo("END: Submitting order [mockId] over TIBCO message bus");
		
		expect:
		bbbSubmitOrderHandlerMock.submitOrder(submitOrderMock)
	}
	
	def "submitOrder: When order submission throws BBBSystemException"(){
		given:
		SubmitOrderVO submitOrderMock = new SubmitOrderVO()
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		submitOrderMock.setOrder(mockBBBOrder)
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(submitOrderMock) >> {throw new BBBSystemException("")}
		1 * bbbSubmitOrderHandlerMock.logError(*_)
		0 * bbbSubmitOrderHandlerMock.logInfo("END: Submitting order [mockId] over TIBCO message bus");
		
		when:
		bbbSubmitOrderHandlerMock.submitOrder(submitOrderMock)
		
		then:
		BBBSystemException se = thrown()
	}
	
	def "submitOrder: When order submission throws BBBBusinessException"(){
		given:
		SubmitOrderVO submitOrderMock = new SubmitOrderVO()
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		submitOrderMock.setOrder(mockBBBOrder)
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(submitOrderMock) >> {throw new BBBBusinessException("")}
		1 * bbbSubmitOrderHandlerMock.logError(*_)
		0 * bbbSubmitOrderHandlerMock.logInfo("END: Submitting order [mockId] over TIBCO message bus");
		
		when:
		bbbSubmitOrderHandlerMock.submitOrder(submitOrderMock)
		
		then:
		BBBSystemException se = thrown()
	}
	
	def "submitOrder: When order submission throws Exception"(){
		given:
		SubmitOrderVO submitOrderMock = new SubmitOrderVO()
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		submitOrderMock.setOrder(mockBBBOrder)
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(submitOrderMock) >> {throw new Exception("")}
		1 * bbbSubmitOrderHandlerMock.logError(*_)
		0 * bbbSubmitOrderHandlerMock.logInfo("END: Submitting order [mockId] over TIBCO message bus");
		
		when:
		bbbSubmitOrderHandlerMock.submitOrder(submitOrderMock)
		
		then:
		BBBSystemException se = thrown()
	}
	
	def "submitInventoryMesssage: When inventory message submission is successful"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		
		mockCommerceItem.getRepositoryItem() >> mockRepositoryItem
		mockCommerceItem.isVdcInd() >> true
		mockCommerceItem.getQuantity() >> 100
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		List<String> updateAllInventory = Arrays.asList("true")
		
		mockBBBOrder.getSiteId() >> "mockSiteId"
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		Map<String, String> mockDataCenterMap = new HashMap<String, String>()
		mockDataCenterMap.put("mockDcPrefix", "mockDataCenter")
		
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getContentCatalogConfigration("UpdateAllInventory") >> updateAllInventory
		
		bbbSubmitOrderHandlerMock.getConsumer() >> "mockConsumer"
		bbbSubmitOrderHandlerMock.getPayload() >> "mockPayload"
		bbbSubmitOrderHandlerMock.getProducer() >> "mockProducer"
		bbbSubmitOrderHandlerMock.getMessageFormat() >> "mockMessageFormat"
		bbbSubmitOrderHandlerMock.setDcPrefix("mockDcPrefix")
		bbbSubmitOrderHandlerMock.setDataCenterMap(mockDataCenterMap)
		bbbSubmitOrderHandlerMock.getInventoryManager() >> mockInventoryManager
		1 * mockInventoryManager.decrementInventoryStock(_)
		1 * bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		expect:
		bbbSubmitOrderHandlerMock.submitInventoryMesssage(mockBBBOrder)
	}
	
	def "submitInventoryMesssage: When determining Content Catalog Configuration throws Exception"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		
		mockCommerceItem.getRepositoryItem() >> mockRepositoryItem
		mockCommerceItem.isVdcInd() >> true
		mockCommerceItem.getQuantity() >> 100
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		
		mockBBBOrder.getSiteId() >> "mockSiteId"
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		Map<String, String> mockDataCenterMap = new HashMap<String, String>()
		mockDataCenterMap.put("mockDcPrefix", "mockDataCenter")
		
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getContentCatalogConfigration("UpdateAllInventory") >> {throw new Exception("")}
		
		bbbSubmitOrderHandlerMock.getConsumer() >> "mockConsumer"
		bbbSubmitOrderHandlerMock.getPayload() >> "mockPayload"
		bbbSubmitOrderHandlerMock.getProducer() >> "mockProducer"
		bbbSubmitOrderHandlerMock.getMessageFormat() >> "mockMessageFormat"
		bbbSubmitOrderHandlerMock.setDcPrefix("mockDcPrefix")
		bbbSubmitOrderHandlerMock.setDataCenterMap(mockDataCenterMap)
		bbbSubmitOrderHandlerMock.getInventoryManager() >> mockInventoryManager
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1014 + 
			": Error while retrieving configure keys value for [" + BBBCoreConstants.UPDATE_ALL_INVENTORY_CONFIG + "]", _)
		1 * mockInventoryManager.decrementInventoryStock(_)
		1 * bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		expect:
		bbbSubmitOrderHandlerMock.submitInventoryMesssage(mockBBBOrder)
	}
	
	def "submitInventoryMesssage: When Content Catalog Configuration returns NULL"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		
		mockCommerceItem.getRepositoryItem() >> mockRepositoryItem
		mockCommerceItem.isVdcInd() >> true
		mockCommerceItem.getQuantity() >> 100
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		
		mockBBBOrder.getSiteId() >> "mockSiteId"
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		Map<String, String> mockDataCenterMap = new HashMap<String, String>()
		mockDataCenterMap.put("mockDcPrefix", "mockDataCenter")
		
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getContentCatalogConfigration("UpdateAllInventory") >> null
		
		bbbSubmitOrderHandlerMock.getConsumer() >> "mockConsumer"
		bbbSubmitOrderHandlerMock.getPayload() >> "mockPayload"
		bbbSubmitOrderHandlerMock.getProducer() >> "mockProducer"
		bbbSubmitOrderHandlerMock.getMessageFormat() >> "mockMessageFormat"
		bbbSubmitOrderHandlerMock.setDcPrefix("mockDcPrefix")
		bbbSubmitOrderHandlerMock.setDataCenterMap(mockDataCenterMap)
		bbbSubmitOrderHandlerMock.getInventoryManager() >> mockInventoryManager
		1 * mockInventoryManager.decrementInventoryStock(_)
		1 * bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		expect:
		bbbSubmitOrderHandlerMock.submitInventoryMesssage(mockBBBOrder)
	}
	
	def "submitInventoryMesssage: When Content Catalog Configuration returns empty list of configurations"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		
		mockCommerceItem.getRepositoryItem() >> mockRepositoryItem
		mockCommerceItem.isVdcInd() >> true
		mockCommerceItem.getQuantity() >> 100
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		
		mockBBBOrder.getSiteId() >> "mockSiteId"
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		Map<String, String> mockDataCenterMap = new HashMap<String, String>()
		mockDataCenterMap.put("mockDcPrefix", "mockDataCenter")
		
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getContentCatalogConfigration("UpdateAllInventory") >> []
		
		bbbSubmitOrderHandlerMock.getConsumer() >> "mockConsumer"
		bbbSubmitOrderHandlerMock.getPayload() >> "mockPayload"
		bbbSubmitOrderHandlerMock.getProducer() >> "mockProducer"
		bbbSubmitOrderHandlerMock.getMessageFormat() >> "mockMessageFormat"
		bbbSubmitOrderHandlerMock.setDcPrefix("mockDcPrefix")
		bbbSubmitOrderHandlerMock.setDataCenterMap(mockDataCenterMap)
		bbbSubmitOrderHandlerMock.getInventoryManager() >> mockInventoryManager
		1 * mockInventoryManager.decrementInventoryStock(_)
		1 * bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		expect:
		bbbSubmitOrderHandlerMock.submitInventoryMesssage(mockBBBOrder)
	}
	
	def "submitInventoryMesssage: When BBBCommerceItem contains a store ID"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		
		mockCommerceItem.getRepositoryItem() >> mockRepositoryItem
		mockCommerceItem.isVdcInd() >> true
		mockCommerceItem.getQuantity() >> 100
		mockCommerceItem.getStoreId() >> 'mockStoreId'
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		List<String> updateAllInventory = Arrays.asList("true")
		
		mockBBBOrder.getSiteId() >> "mockSiteId"
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		Map<String, String> mockDataCenterMap = new HashMap<String, String>()
		mockDataCenterMap.put("mockDcPrefix", "mockDataCenter")
		
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getContentCatalogConfigration("UpdateAllInventory") >> updateAllInventory
		
		bbbSubmitOrderHandlerMock.getConsumer() >> "mockConsumer"
		bbbSubmitOrderHandlerMock.getPayload() >> "mockPayload"
		bbbSubmitOrderHandlerMock.getProducer() >> "mockProducer"
		bbbSubmitOrderHandlerMock.getMessageFormat() >> "mockMessageFormat"
		bbbSubmitOrderHandlerMock.setDcPrefix("mockDcPrefix")
		bbbSubmitOrderHandlerMock.setDataCenterMap(mockDataCenterMap)
		bbbSubmitOrderHandlerMock.getInventoryManager() >> mockInventoryManager
		0 * mockInventoryManager.decrementInventoryStock(_)
		0 * bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		expect:
		bbbSubmitOrderHandlerMock.submitInventoryMesssage(mockBBBOrder)
	}
	
	def "submitInventoryMesssage: Not to update inventory for non-vdc item and config updateallinventory is false"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		
		mockCommerceItem.getRepositoryItem() >> mockRepositoryItem
		mockCommerceItem.isVdcInd() >> false
		mockCommerceItem.getQuantity() >> 100
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		List<String> updateAllInventory = Arrays.asList("false")
		
		mockBBBOrder.getSiteId() >> "mockSiteId"
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		Map<String, String> mockDataCenterMap = new HashMap<String, String>()
		mockDataCenterMap.put("mockDcPrefix", "mockDataCenter")
		
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getContentCatalogConfigration("UpdateAllInventory") >> updateAllInventory
		
		bbbSubmitOrderHandlerMock.getConsumer() >> "mockConsumer"
		bbbSubmitOrderHandlerMock.getPayload() >> "mockPayload"
		bbbSubmitOrderHandlerMock.getProducer() >> "mockProducer"
		bbbSubmitOrderHandlerMock.getMessageFormat() >> "mockMessageFormat"
		bbbSubmitOrderHandlerMock.setDcPrefix("mockDcPrefix")
		bbbSubmitOrderHandlerMock.setDataCenterMap(mockDataCenterMap)
		bbbSubmitOrderHandlerMock.getInventoryManager() >> mockInventoryManager
		0 * mockInventoryManager.decrementInventoryStock(_)
		0 * bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		expect:
		bbbSubmitOrderHandlerMock.submitInventoryMesssage(mockBBBOrder)
	}
	
	def "submitInventoryMesssage: When item is not an instance of BBBCommerceItem and Inventory size is 0"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		
		def CommerceItem mockCommerceItem = Mock()
		List<CommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		
		mockBBBOrder.getSiteId() >> "mockSiteId"
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		Map<String, String> mockDataCenterMap = new HashMap<String, String>()
		mockDataCenterMap.put("mockDcPrefix", "mockDataCenter")
		
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getContentCatalogConfigration("UpdateAllInventory") >> null
		
		bbbSubmitOrderHandlerMock.getConsumer() >> "mockConsumer"
		bbbSubmitOrderHandlerMock.getPayload() >> "mockPayload"
		bbbSubmitOrderHandlerMock.getProducer() >> "mockProducer"
		bbbSubmitOrderHandlerMock.getMessageFormat() >> "mockMessageFormat"
		bbbSubmitOrderHandlerMock.setDcPrefix("mockDcPrefix")
		bbbSubmitOrderHandlerMock.setDataCenterMap(mockDataCenterMap)
		bbbSubmitOrderHandlerMock.getInventoryManager() >> mockInventoryManager
		0 * mockInventoryManager.decrementInventoryStock(_)
		0 * bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		expect:
		bbbSubmitOrderHandlerMock.submitInventoryMesssage(mockBBBOrder)
	}
	
	def "decrementInventoryRepository: When Inventory Repository is getting decreased"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		
		mockCommerceItem.getRepositoryItem() >> mockRepositoryItem
		mockCommerceItem.isVdcInd() >> true
		mockCommerceItem.getQuantity() >> 100
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		List<String> updateAllInventory = Arrays.asList("true")
		
		mockBBBOrder.getSiteId() >> "mockSiteId"
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		Map<String, String> mockDataCenterMap = new HashMap<String, String>()
		mockDataCenterMap.put("mockDcPrefix", "mockDataCenter")
		
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getContentCatalogConfigration("UpdateAllInventory") >> updateAllInventory
		
		bbbSubmitOrderHandlerMock.getConsumer() >> "mockConsumer"
		bbbSubmitOrderHandlerMock.getPayload() >> "mockPayload"
		bbbSubmitOrderHandlerMock.getProducer() >> "mockProducer"
		bbbSubmitOrderHandlerMock.getMessageFormat() >> "mockMessageFormat"
		bbbSubmitOrderHandlerMock.setDcPrefix("mockDcPrefix")
		bbbSubmitOrderHandlerMock.setDataCenterMap(mockDataCenterMap)
		bbbSubmitOrderHandlerMock.getInventoryManager() >> mockInventoryManager
		1 * mockInventoryManager.decrementInventoryStock(_) >> {}
		
		expect:
		bbbSubmitOrderHandlerMock.decrementInventoryRepository(mockBBBOrder)
	}
	
	def "decrementInventoryRepository: When item is not an instance of BBBCommerceItem and Inventory size is 0"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		
		def CommerceItem mockCommerceItem = Mock()
		List<CommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		List<String> updateAllInventory = Arrays.asList("true")
		
		mockBBBOrder.getSiteId() >> "mockSiteId"
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		Map<String, String> mockDataCenterMap = new HashMap<String, String>()
		mockDataCenterMap.put("mockDcPrefix", "mockDataCenter")
		
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getContentCatalogConfigration("UpdateAllInventory") >> updateAllInventory
		
		bbbSubmitOrderHandlerMock.getConsumer() >> "mockConsumer"
		bbbSubmitOrderHandlerMock.getPayload() >> "mockPayload"
		bbbSubmitOrderHandlerMock.getProducer() >> "mockProducer"
		bbbSubmitOrderHandlerMock.getMessageFormat() >> "mockMessageFormat"
		bbbSubmitOrderHandlerMock.setDcPrefix("mockDcPrefix")
		bbbSubmitOrderHandlerMock.setDataCenterMap(mockDataCenterMap)
		bbbSubmitOrderHandlerMock.getInventoryManager() >> mockInventoryManager
		0 * mockInventoryManager.decrementInventoryStock(_) >> {}
		
		expect:
		bbbSubmitOrderHandlerMock.decrementInventoryRepository(mockBBBOrder)
	}
	
	def "sendMail: When summary email is being sent to user"(){
		given:
		def Profile mockProfile = Mock()
		def BBBTemplateEmailSender mockEmailSender = Mock()
		def TemplateEmailInfoImpl mockEmailTemplate = Mock()
		def BBBRepositoryContactInfo mockBillingAddress = Mock()
		
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setEmailSender(mockEmailSender)
		
		mockBBBOrder.getBillingAddress() >> mockBillingAddress
		mockBBBOrder.getProfileId() >> "mockProfileId"
		mockBBBOrder.getSubmittedDate() >> new Date()
		mockBillingAddress.getEmail() >> "email@mockery.com" 
		
		requestMock.getHeader("X-bbb-channel") >> "mockChannel"
		requestMock.resolveName("/atg/userprofiling/Profile") >> mockProfile
		mockProfile.isTransient() >> true

		requestMock.getLocale() >> new Locale("en_US")
		requestMock.resolveName("/atg/userprofiling/email/EmailOrderConfirmEmailInfo") >> mockEmailTemplate
		
		1 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		1 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 + ": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(mockBBBOrder, requestMock)
	}
	
	def "sendMail: When Request is NULL"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		0 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		0 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 +
			": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(mockBBBOrder, null)
	}
	
	def "sendMail: When BBBOrder is NULL"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		0 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		0 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 + 
			": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(null, requestMock)
	}
	
	def "sendMail: When BillingAddress is NULL"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		mockBBBOrder.getBillingAddress() >> null
		
		0 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		0 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 + ": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(mockBBBOrder, requestMock)
	}
	
	def "sendMail: When recipient email address is NULL"(){
		given:
		def BBBRepositoryContactInfo mockBillingAddress = Mock()
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		mockBBBOrder.getBillingAddress() >> mockBillingAddress
		mockBillingAddress.getEmail() >> null
		
		0 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		0 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 + ": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(mockBBBOrder, requestMock)
	}
	
	def "sendMail: When email template is NULL"(){
		given:
		def Profile mockProfile = Mock()
		def BBBTemplateEmailSender mockEmailSender = Mock()
		def BBBRepositoryContactInfo mockBillingAddress = Mock()
		
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setEmailSender(mockEmailSender)
		
		mockBBBOrder.getBillingAddress() >> mockBillingAddress
		mockBBBOrder.getProfileId() >> "mockProfileId"
		mockBBBOrder.getSubmittedDate() >> new Date()
		mockBillingAddress.getEmail() >> "email@mockery.com"
		
		requestMock.getHeader("X-bbb-channel") >> "mockChannel"
		requestMock.resolveName("/atg/userprofiling/Profile") >> mockProfile
		mockProfile.isTransient() >> true

		requestMock.getLocale() >> new Locale("en_US")
		requestMock.resolveName("/atg/userprofiling/email/EmailOrderConfirmEmailInfo") >> null
		
		1 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		0 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 + 
			": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(mockBBBOrder, requestMock)
	}
	
	def "sendMail: When email channel is MobileWeb"(){
		given:
		def Profile mockProfile = Mock()
		def BBBTemplateEmailSender mockEmailSender = Mock()
		def TemplateEmailInfoImpl mockEmailTemplate = Mock()
		def BBBRepositoryContactInfo mockBillingAddress = Mock()
		List<String> mockConfigValue = Arrays.asList("mockServerName")
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setEmailSender(mockEmailSender)
		
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> mockConfigValue
		
		mockBBBOrder.getBillingAddress() >> mockBillingAddress
		mockBBBOrder.getProfileId() >> "mockProfileId"
		mockBBBOrder.getSubmittedDate() >> new Date()
		mockBillingAddress.getEmail() >> "email@mockery.com"
		
		requestMock.getHeader("X-bbb-channel") >> "MobileWeb"
		requestMock.resolveName("/atg/userprofiling/Profile") >> mockProfile
		mockProfile.isTransient() >> true

		requestMock.getLocale() >> new Locale("en_US")
		requestMock.resolveName("/atg/userprofiling/email/EmailOrderConfirmEmailInfo") >> mockEmailTemplate
		
		1 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		1 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 + 
			": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(mockBBBOrder, requestMock)
	}
	
	def "sendMail: When email channel is MobileApp"(){
		given:
		def Profile mockProfile = Mock()
		def BBBTemplateEmailSender mockEmailSender = Mock()
		def TemplateEmailInfoImpl mockEmailTemplate = Mock()
		def BBBRepositoryContactInfo mockBillingAddress = Mock()
		List<String> mockConfigValue = Arrays.asList("mockServerName")
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setEmailSender(mockEmailSender)
		
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> mockConfigValue
		
		mockBBBOrder.getBillingAddress() >> mockBillingAddress
		mockBBBOrder.getProfileId() >> "mockProfileId"
		mockBBBOrder.getSubmittedDate() >> new Date()
		mockBillingAddress.getEmail() >> "email@mockery.com"
		
		requestMock.getHeader("X-bbb-channel") >> "MobileApp"
		requestMock.resolveName("/atg/userprofiling/Profile") >> mockProfile
		mockProfile.isTransient() >> true

		requestMock.getLocale() >> new Locale("en_US")
		requestMock.resolveName("/atg/userprofiling/email/EmailOrderConfirmEmailInfo") >> mockEmailTemplate
		
		1 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		1 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 + 
			": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(mockBBBOrder, requestMock)
	}
	
	def "sendMail: When email channel is NULL"(){
		given:
		def Profile mockProfile = Mock()
		def BBBTemplateEmailSender mockEmailSender = Mock()
		def TemplateEmailInfoImpl mockEmailTemplate = Mock()
		def BBBRepositoryContactInfo mockBillingAddress = Mock()
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setEmailSender(mockEmailSender)
		
		mockBBBOrder.getBillingAddress() >> mockBillingAddress
		mockBBBOrder.getProfileId() >> "mockProfileId"
		mockBBBOrder.getSubmittedDate() >> new Date()
		mockBillingAddress.getEmail() >> "email@mockery.com"
		
		requestMock.getHeader("X-bbb-channel") >> null
		requestMock.resolveName("/atg/userprofiling/Profile") >> mockProfile
		mockProfile.isTransient() >> true

		requestMock.getLocale() >> new Locale("en_US")
		requestMock.resolveName("/atg/userprofiling/email/EmailOrderConfirmEmailInfo") >> mockEmailTemplate
		
		1 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		1 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 + 
			": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(mockBBBOrder, requestMock)
	}
	
	def "sendMail: When submitted date is NULL"(){
		given:
		def Profile mockProfile = Mock()
		def BBBTemplateEmailSender mockEmailSender = Mock()
		def TemplateEmailInfoImpl mockEmailTemplate = Mock()
		def BBBRepositoryContactInfo mockBillingAddress = Mock()
		
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setEmailSender(mockEmailSender)
		
		mockBBBOrder.getBillingAddress() >> mockBillingAddress
		mockBBBOrder.getProfileId() >> "mockProfileId"
		mockBBBOrder.getSubmittedDate() >> null
		mockBillingAddress.getEmail() >> "email@mockery.com"
		
		requestMock.getHeader("X-bbb-channel") >> "mockChannel"
		requestMock.resolveName("/atg/userprofiling/Profile") >> mockProfile
		mockProfile.isTransient() >> true

		requestMock.getLocale() >> new Locale("en_US")
		requestMock.resolveName("/atg/userprofiling/email/EmailOrderConfirmEmailInfo") >> mockEmailTemplate
		
		1 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		1 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 + 
			": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(mockBBBOrder, requestMock)
	}
	
	def "sendMail: When Profile is NOT transient"(){
		given:
		def Profile mockProfile = Mock()
		def BBBTemplateEmailSender mockEmailSender = Mock()
		def TemplateEmailInfoImpl mockEmailTemplate = Mock()
		def BBBRepositoryContactInfo mockBillingAddress = Mock()
		
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setEmailSender(mockEmailSender)
		
		mockBBBOrder.getBillingAddress() >> mockBillingAddress
		mockBBBOrder.getProfileId() >> "mockProfileId"
		mockBBBOrder.getSubmittedDate() >> new Date()
		mockBillingAddress.getEmail() >> "email@mockery.com"
		
		requestMock.getHeader("X-bbb-channel") >> "mockChannel"
		requestMock.resolveName("/atg/userprofiling/Profile") >> mockProfile
		mockProfile.isTransient() >> false

		requestMock.getLocale() >> new Locale("en_US")
		requestMock.resolveName("/atg/userprofiling/email/EmailOrderConfirmEmailInfo") >> mockEmailTemplate
		
		1 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		1 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 +
			": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(mockBBBOrder, requestMock)
	}
	
	def "sendMail: When config value is NULL"(){
		given:
		def Profile mockProfile = Mock()
		def BBBTemplateEmailSender mockEmailSender = Mock()
		def TemplateEmailInfoImpl mockEmailTemplate = Mock()
		def BBBRepositoryContactInfo mockBillingAddress = Mock()
		
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setEmailSender(mockEmailSender)
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> null
		
		mockBBBOrder.getBillingAddress() >> mockBillingAddress
		mockBBBOrder.getProfileId() >> "mockProfileId"
		mockBBBOrder.getSubmittedDate() >> new Date()
		mockBillingAddress.getEmail() >> "email@mockery.com"
		
		requestMock.getHeader("X-bbb-channel") >> "MobileWeb"
		requestMock.resolveName("/atg/userprofiling/Profile") >> mockProfile
		mockProfile.isTransient() >> true

		requestMock.getLocale() >> new Locale("en_US")
		requestMock.resolveName("/atg/userprofiling/email/EmailOrderConfirmEmailInfo") >> mockEmailTemplate
		
		1 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		1 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 +
			": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(mockBBBOrder, requestMock)
	}
	
	def "sendMail: When config value is EMPTY"(){
		given:
		def Profile mockProfile = Mock()
		def BBBTemplateEmailSender mockEmailSender = Mock()
		def TemplateEmailInfoImpl mockEmailTemplate = Mock()
		def BBBRepositoryContactInfo mockBillingAddress = Mock()
		
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setEmailSender(mockEmailSender)
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> []
		
		mockBBBOrder.getBillingAddress() >> mockBillingAddress
		mockBBBOrder.getProfileId() >> "mockProfileId"
		mockBBBOrder.getSubmittedDate() >> new Date()
		mockBillingAddress.getEmail() >> "email@mockery.com"
		
		requestMock.getHeader("X-bbb-channel") >> "MobileWeb"
		requestMock.resolveName("/atg/userprofiling/Profile") >> mockProfile
		mockProfile.isTransient() >> true

		requestMock.getLocale() >> new Locale("en_US")
		requestMock.resolveName("/atg/userprofiling/email/EmailOrderConfirmEmailInfo") >> mockEmailTemplate
		
		1 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		1 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 +
			": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(mockBBBOrder, requestMock)
	}
	
	def "sendMail: BBBSystemException encountered while fetching config key for config value"(){
		given:
		def Profile mockProfile = Mock()
		def BBBTemplateEmailSender mockEmailSender = Mock()
		def TemplateEmailInfoImpl mockEmailTemplate = Mock()
		def BBBRepositoryContactInfo mockBillingAddress = Mock()
		
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setEmailSender(mockEmailSender)
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> {throw new BBBSystemException("")}
		
		mockBBBOrder.getBillingAddress() >> mockBillingAddress
		mockBBBOrder.getProfileId() >> "mockProfileId"
		mockBBBOrder.getSubmittedDate() >> new Date()
		mockBillingAddress.getEmail() >> "email@mockery.com"
		
		requestMock.getHeader("X-bbb-channel") >> "MobileWeb"
		requestMock.resolveName("/atg/userprofiling/Profile") >> mockProfile
		mockProfile.isTransient() >> true

		requestMock.getLocale() >> new Locale("en_US")
		requestMock.resolveName("/atg/userprofiling/email/EmailOrderConfirmEmailInfo") >> mockEmailTemplate
		
		1 * bbbSubmitOrderHandlerMock.logError("BBBSubmitOrderHandler.collectParams :: System Exception occured while fetching config value for config key " 
			+ BBBCoreConstants.REQUESTDOMAIN_CONFIGURE + "config type " + BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, _)
		1 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		1 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 +
			": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(mockBBBOrder, requestMock)
	}
	
	def "sendMail: BBBBusinessException encountered while fetching config key for config value"(){
		given:
		def Profile mockProfile = Mock()
		def BBBTemplateEmailSender mockEmailSender = Mock()
		def TemplateEmailInfoImpl mockEmailTemplate = Mock()
		def BBBRepositoryContactInfo mockBillingAddress = Mock()
		
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setEmailSender(mockEmailSender)
		bbbSubmitOrderHandlerMock.getCatalogTools() >> mockCatalogTools
		mockCatalogTools.getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE) >> {throw new BBBBusinessException("")}
		
		mockBBBOrder.getBillingAddress() >> mockBillingAddress
		mockBBBOrder.getProfileId() >> "mockProfileId"
		mockBBBOrder.getSubmittedDate() >> new Date()
		mockBillingAddress.getEmail() >> "email@mockery.com"
		
		requestMock.getHeader("X-bbb-channel") >> "MobileWeb"
		requestMock.resolveName("/atg/userprofiling/Profile") >> mockProfile
		mockProfile.isTransient() >> true

		requestMock.getLocale() >> new Locale("en_US")
		requestMock.resolveName("/atg/userprofiling/email/EmailOrderConfirmEmailInfo") >> mockEmailTemplate
		
		1 * bbbSubmitOrderHandlerMock.logError("BBBSubmitOrderHandler.collectParams :: Business Exception occured while fetching config value for config key " 
			+ BBBCoreConstants.REQUESTDOMAIN_CONFIGURE + "config type " + BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, _)
		1 * bbbSubmitOrderHandlerMock.getDynamoHttpServletRequest(_)
		1 * bbbSubmitOrderHandlerMock.extractedSendEmail(_, _) >> {}
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1018 +
			": TemplateEmailException occurred while sending Order Confirmation Email", _)
		1 * bbbSubmitOrderHandlerMock.logDebug("END: Sending Order Summary Mail to user")
		
		expect:
		bbbSubmitOrderHandlerMock.sendMail(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When submitted order is processed successfully"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setOrderManager(mockBBBOrderManager)
		bbbSubmitOrderHandlerMock.setOrderRepository(mockMutableRepository)
		bbbSubmitOrderHandlerMock.setTransactionManager(mockTransactionManager)
		
		mockBBBOrder.getId() >> "mockId"
		mockBBBOrder.getSubStatus() >> "Unsubmitted"
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		mockCommerceItem.getReferenceNumber() >> "mockReferenceNumber"
		mockCommerceItem.getAuxiliaryData() >> mockAuxiliaryData
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		bbbSubmitOrderHandlerMock.getBbbEximPricingManager() >> mockBBBEximManager
		mockBBBEximManager.getKatoriAvailability() >> "true"
		
		mockAuxiliaryData.getProductId() >> "mockProductId"
		
		1 * bbbSubmitOrderHandlerMock.getOrderManager().updateOrderSubstatus(mockBBBOrder, BBBCoreConstants.ORDER_SUBSTATUS_SUBMITTED)
		
		mockBBBEximManager.invokeLockAPI(_, _) >> "success"
		1 * bbbSubmitOrderHandlerMock.logDebug("Response from Lock API call: success")
		0 * bbbSubmitOrderHandlerMock.logError("BBBSystemException Error in invoking Lock API refNum:[mockReferenceNumber]" , _)
		0 * bbbSubmitOrderHandlerMock.logError("BBBBusinessException Error in invoking Lock API refNum:[mockReferenceNumber]" , _)
		0 * bbbSubmitOrderHandlerMock.logError("Error in calling Lock API from submit order page, refNum:[mockReferenceNumber]")
		1 * bbbSubmitOrderHandlerMock.logInfo("Successfully Submitted order : " + "mockId")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When BBBOrder is NULL"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 + 
			": Transaction demarcation failure while processing Submit Order request", _);
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 + 
			": System exception while processing Submit Order request. updating updateSubstatusFail flag to true ", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 + 
			": Exception while processing Submit Order request. updating updateSubstatusFail flag to true ", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 + 
			": Transaction failure while processing Submit Order request", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016+ 
			": Transaction demarcation failure while updating order substatus to FAILED", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016
			+ ": Transaction failure while updating order substatus to FAILED. Rolling back the transaction.")
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016	+ 
			": Transaction failure while updating order substatus to FAILED", _)
		1 * bbbSubmitOrderHandlerMock.logInfo("END: Processing order for submission")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(null, requestMock)
	}
	
	def "processSubmitOrder: When submitted order's status is Dummy Restore Inventory"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setTransactionManager(mockTransactionManager)
		mockBBBOrder.getSubStatus() >> "DUMMY_RESTORE_INVENTORY"
		
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 +
			": Transaction demarcation failure while processing Submit Order request", _);
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 +
			": System exception while processing Submit Order request. updating updateSubstatusFail flag to true ", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 +
			": Exception while processing Submit Order request. updating updateSubstatusFail flag to true ", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 +
			": Transaction failure while processing Submit Order request", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016+
			": Transaction demarcation failure while updating order substatus to FAILED", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016
			+ ": Transaction failure while updating order substatus to FAILED. Rolling back the transaction.")
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016	+
			": Transaction failure while updating order substatus to FAILED", _)
		0 * bbbSubmitOrderHandlerMock.logInfo("Successfully Submitted order : " + "mockId")
		1 * bbbSubmitOrderHandlerMock.logInfo("END: Processing order for submission")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When submitted order's status is Dummy Ignore Inventory"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setTransactionManager(mockTransactionManager)
		mockBBBOrder.getSubStatus() >> "DUMMY_IGNORE_INVENTORY"
		
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 +
			": Transaction demarcation failure while processing Submit Order request", _);
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 +
			": System exception while processing Submit Order request. updating updateSubstatusFail flag to true ", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 +
			": Exception while processing Submit Order request. updating updateSubstatusFail flag to true ", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 +
			": Transaction failure while processing Submit Order request", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016+
			": Transaction demarcation failure while updating order substatus to FAILED", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016
			+ ": Transaction failure while updating order substatus to FAILED. Rolling back the transaction.")
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016	+
			": Transaction failure while updating order substatus to FAILED", _)
		0 * bbbSubmitOrderHandlerMock.logInfo("Successfully Submitted order : " + "mockId")
		1 * bbbSubmitOrderHandlerMock.logInfo("END: Processing order for submission")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When item is not an instance of BBBCommerceItem"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setOrderManager(mockBBBOrderManager)
		bbbSubmitOrderHandlerMock.setTransactionManager(mockTransactionManager)
		
		mockBBBOrder.getId() >> "mockId"
		mockBBBOrder.getSubStatus() >> "Unsubmitted"
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		def CommerceItem mockCommerceItem = Mock()
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		
		1 * bbbSubmitOrderHandlerMock.getOrderManager().updateOrderSubstatus(mockBBBOrder, BBBCoreConstants.ORDER_SUBSTATUS_SUBMITTED)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 +
			": Transaction demarcation failure while processing Submit Order request", _);
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 +
			": System exception while processing Submit Order request. updating updateSubstatusFail flag to true ", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 +
			": Exception while processing Submit Order request. updating updateSubstatusFail flag to true ", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 +
			": Transaction failure while processing Submit Order request", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016+
			": Transaction demarcation failure while updating order substatus to FAILED", _)
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016
			+ ": Transaction failure while updating order substatus to FAILED. Rolling back the transaction.")
		0 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016	+
			": Transaction failure while updating order substatus to FAILED", _)
		1 * bbbSubmitOrderHandlerMock.logInfo("Successfully Submitted order : " + "mockId")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When EximManager's invokeLockAPI throws BBBSystemException"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setOrderManager(mockBBBOrderManager)
		bbbSubmitOrderHandlerMock.setOrderRepository(mockMutableRepository)
		bbbSubmitOrderHandlerMock.setTransactionManager(mockTransactionManager)
		
		mockBBBOrder.getId() >> "mockId"
		mockBBBOrder.getSubStatus() >> "Unsubmitted"
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		mockCommerceItem.getReferenceNumber() >> "mockReferenceNumber"
		mockCommerceItem.getAuxiliaryData() >> mockAuxiliaryData
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		bbbSubmitOrderHandlerMock.getBbbEximPricingManager() >> mockBBBEximManager
		mockBBBEximManager.getKatoriAvailability() >> "true"
		
		mockAuxiliaryData.getProductId() >> "mockProductId"
		
		mockBBBEximManager.invokeLockAPI(_, _) >> {throw new BBBSystemException("")}
		1 * bbbSubmitOrderHandlerMock.logError("BBBSystemException Error in invoking Lock API refNum:[mockReferenceNumber]" , _)
		1 * bbbSubmitOrderHandlerMock.logError("Error in calling Lock API from submit order page, refNum:[mockReferenceNumber]")
		1 * bbbSubmitOrderHandlerMock.logInfo("Successfully Submitted order : " + "mockId")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When EximManager's invokeLockAPI throws BBBBusinessException"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setOrderManager(mockBBBOrderManager)
		bbbSubmitOrderHandlerMock.setOrderRepository(mockMutableRepository)
		bbbSubmitOrderHandlerMock.setTransactionManager(mockTransactionManager)
		
		mockBBBOrder.getId() >> "mockId"
		mockBBBOrder.getSubStatus() >> "Unsubmitted"
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		mockCommerceItem.getReferenceNumber() >> "mockReferenceNumber"
		mockCommerceItem.getAuxiliaryData() >> mockAuxiliaryData
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		bbbSubmitOrderHandlerMock.getBbbEximPricingManager() >> mockBBBEximManager
		mockBBBEximManager.getKatoriAvailability() >> "true"
		
		mockAuxiliaryData.getProductId() >> "mockProductId"
		
		mockBBBEximManager.invokeLockAPI(_, _) >> {throw new BBBBusinessException("")}
		1 * bbbSubmitOrderHandlerMock.logError("BBBBusinessException Error in invoking Lock API refNum:[mockReferenceNumber]" , _)
		1 * bbbSubmitOrderHandlerMock.logError("Error in calling Lock API from submit order page, refNum:[mockReferenceNumber]")
		1 * bbbSubmitOrderHandlerMock.logInfo("Successfully Submitted order : " + "mockId")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When EximManager's invokeLockAPI returns any other response than success"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setOrderManager(mockBBBOrderManager)
		bbbSubmitOrderHandlerMock.setOrderRepository(mockMutableRepository)
		bbbSubmitOrderHandlerMock.setTransactionManager(mockTransactionManager)
		
		mockBBBOrder.getId() >> "mockId"
		mockBBBOrder.getSubStatus() >> "Unsubmitted"
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		mockCommerceItem.getReferenceNumber() >> "mockReferenceNumber"
		mockCommerceItem.getAuxiliaryData() >> mockAuxiliaryData
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		bbbSubmitOrderHandlerMock.getBbbEximPricingManager() >> mockBBBEximManager
		mockBBBEximManager.getKatoriAvailability() >> "true"
		
		mockAuxiliaryData.getProductId() >> "mockProductId"
		
		mockBBBEximManager.invokeLockAPI(_, _) >> "otherThanSuccessResponse"
		1 * bbbSubmitOrderHandlerMock.logInfo("Successfully Submitted order : " + "mockId")
		1 * bbbSubmitOrderHandlerMock.logError("Error in calling Lock API from submit order page, refNum:[mockReferenceNumber]")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When ReferenceNumber of CommerceItem is NULL"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setOrderManager(mockBBBOrderManager)
		bbbSubmitOrderHandlerMock.setOrderRepository(mockMutableRepository)
		bbbSubmitOrderHandlerMock.setTransactionManager(mockTransactionManager)
		
		mockBBBOrder.getId() >> "mockId"
		mockBBBOrder.getSubStatus() >> "Unsubmitted"
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		mockCommerceItem.getReferenceNumber() >> null
		mockCommerceItem.getAuxiliaryData() >> mockAuxiliaryData
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		bbbSubmitOrderHandlerMock.getBbbEximPricingManager() >> mockBBBEximManager
		mockBBBEximManager.getKatoriAvailability() >> true
		
		mockAuxiliaryData.getProductId() >> "mockProductId"
		
		mockBBBEximManager.invokeLockAPI(_, _) >> "otherThanSuccessResponse"
		1 * bbbSubmitOrderHandlerMock.logInfo("Successfully Submitted order : " + "mockId")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When ReferenceNumber of CommerceItem is MINUS_ONE"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setOrderManager(mockBBBOrderManager)
		bbbSubmitOrderHandlerMock.setOrderRepository(mockMutableRepository)
		bbbSubmitOrderHandlerMock.setTransactionManager(mockTransactionManager)
		
		mockBBBOrder.getId() >> "mockId"
		mockBBBOrder.getSubStatus() >> "Unsubmitted"
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		mockCommerceItem.getReferenceNumber() >> "-1"
		mockCommerceItem.getAuxiliaryData() >> mockAuxiliaryData
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		bbbSubmitOrderHandlerMock.getBbbEximPricingManager() >> mockBBBEximManager
		mockBBBEximManager.getKatoriAvailability() >> true
		
		mockAuxiliaryData.getProductId() >> "mockProductId"
		
		mockBBBEximManager.invokeLockAPI(_, _) >> "otherThanSuccessResponse"
		1 * bbbSubmitOrderHandlerMock.logInfo("Successfully Submitted order : " + "mockId")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When EximManager's KatoriAvailability doesn't return TRUE"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setOrderManager(mockBBBOrderManager)
		bbbSubmitOrderHandlerMock.setOrderRepository(mockMutableRepository)
		bbbSubmitOrderHandlerMock.setTransactionManager(mockTransactionManager)
		
		mockBBBOrder.getId() >> "mockId"
		mockBBBOrder.getSubStatus() >> "Unsubmitted"
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		mockCommerceItem.getReferenceNumber() >> "mockReferenceNumber"
		mockCommerceItem.getAuxiliaryData() >> mockAuxiliaryData
		List<BBBCommerceItem> mockCommerceItems = Arrays.asList(mockCommerceItem)
		mockBBBOrder.getCommerceItems() >> mockCommerceItems
		
		bbbSubmitOrderHandlerMock.getBbbEximPricingManager() >> mockBBBEximManager
		mockBBBEximManager.getKatoriAvailability() >> false
		
		mockAuxiliaryData.getProductId() >> "mockProductId"
		
		mockBBBEximManager.invokeLockAPI(_, _) >> "otherThanSuccessResponse"
		1 * bbbSubmitOrderHandlerMock.logInfo("Successfully Submitted order : " + "mockId")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When Begin Transaction throws TransactionDemarcationException"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setOrderManager(mockBBBOrderManager)
		bbbSubmitOrderHandlerMock.setOrderRepository(mockMutableRepository)
		
		mockBBBOrder.getId() >> "mockId"
		mockBBBOrder.getSubStatus() >> "Unsubmitted"
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 + 
			": Transaction demarcation failure while processing Submit Order request", _)
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 + 
			": Transaction failure while processing Submit Order request. Rolling back the transaction. Will retry again")
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016+ 
			": Transaction demarcation failure while updating order substatus to FAILED",_)
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016+ 
			": Transaction failure while updating order substatus to FAILED. Rolling back the transaction.")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When submitOrderMessage throws BBBSystemException"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setOrderManager(mockBBBOrderManager)
		bbbSubmitOrderHandlerMock.setOrderRepository(mockMutableRepository)
		bbbSubmitOrderHandlerMock.setTransactionManager(mockTransactionManager)
		
		mockBBBOrder.getId() >> "mockId"
		mockBBBOrder.getSubStatus() >> "Unsubmitted"
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {throw new BBBSystemException("")}
		
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 + 
			": System exception while processing Submit Order request. updating updateSubstatusFail flag to true ", _)
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 + 
			": Transaction failure while processing Submit Order request. Rolling back the transaction. Will retry again")
		1 * bbbSubmitOrderHandlerMock.logInfo("Submission Failure for order [mockId]")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When BBBSystemException encountered while updateOrderStatusFail execution"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setOrderManager(mockBBBOrderManager)
		bbbSubmitOrderHandlerMock.setOrderRepository(mockMutableRepository)
		bbbSubmitOrderHandlerMock.setTransactionManager(mockTransactionManager)
		
		mockBBBOrder.getId() >> "mockId"
		mockBBBOrder.getSubStatus() >> "Unsubmitted"
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {throw new BBBSystemException("")}
		
		mockBBBOrderManager.updateOrderSubstatus(mockBBBOrder, "FAILED") >> {throw new BBBSystemException("")}
		mockMutableRepository.getItemForUpdate("mockId", "order") >> mockRepositoryItem
		
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 + 
			": Transaction failure while processing Submit Order request. Rolling back the transaction. Will retry again")
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003+ 
			": System exception while updating order substatus through getOrderManager().updateOrderSubstatus", _)
		1 * bbbSubmitOrderHandlerMock.logInfo("Submission Failure for order [mockId]")
		1 * bbbSubmitOrderHandlerMock.logInfo("END: Processing order [mockId] for submission")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When RepositoryException encountered while updateOrderStatusFail execution"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setOrderManager(mockBBBOrderManager)
		bbbSubmitOrderHandlerMock.setOrderRepository(mockMutableRepository)
		bbbSubmitOrderHandlerMock.setTransactionManager(mockTransactionManager)
		
		mockBBBOrder.getId() >> "mockId"
		mockBBBOrder.getSubStatus() >> "Unsubmitted"
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {throw new BBBSystemException("")}
		
		mockBBBOrderManager.updateOrderSubstatus(mockBBBOrder, "FAILED") >> {throw new BBBSystemException("")}
		mockMutableRepository.getItemForUpdate("mockId", "order") >> mockRepositoryItem
		mockMutableRepository.updateItem(mockRepositoryItem) >> {throw new RepositoryException("")}
		
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 + 
			": System exception while processing Submit Order request. updating updateSubstatusFail flag to true ", _)
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 + 
			": Transaction failure while processing Submit Order request. Rolling back the transaction. Will retry again")
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003+ 
			": System exception while updating order substatus through getOrderManager().updateOrderSubstatus",_)
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003+ 
			": RepositoryException occured while updating order substatus to FAILED", _)
		1 * bbbSubmitOrderHandlerMock.logInfo("Submission Failure for order [mockId]")
		1 * bbbSubmitOrderHandlerMock.logInfo("END: Processing order [mockId] for submission")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
	
	def "processSubmitOrder: When Exception encountered while updateOrder execution"(){
		given:
		bbbSubmitOrderHandlerMock = Spy(BBBSubmitOrderHandler)
		bbbSubmitOrderHandlerMock.setOrderManager(mockBBBOrderManager)
		bbbSubmitOrderHandlerMock.setOrderRepository(mockMutableRepository)
		bbbSubmitOrderHandlerMock.setTransactionManager(mockTransactionManager)
		
		mockBBBOrder.getId() >> "mockId"
		mockBBBOrder.getSubStatus() >> "Unsubmitted"
		bbbSubmitOrderHandlerMock.extractedSendTextMessage(_) >> {}
		
		mockBBBOrderManager.updateOrderSubstatus(mockBBBOrder, "SUBMITTED") >> {throw new Exception("")}
		
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1003 + 
			": Exception while processing Submit Order request. updating updateSubstatusFail flag to true ", _)
		1 * bbbSubmitOrderHandlerMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1016 + 
			": Transaction failure while processing Submit Order request. Rolling back the transaction. Will retry again")
		1 * bbbSubmitOrderHandlerMock.logInfo("Submission Failure for order [mockId]")
		1 * bbbSubmitOrderHandlerMock.logInfo("END: Processing order [mockId] for submission")
		
		expect:
		bbbSubmitOrderHandlerMock.processSubmitOrder(mockBBBOrder, requestMock)
	}
}
