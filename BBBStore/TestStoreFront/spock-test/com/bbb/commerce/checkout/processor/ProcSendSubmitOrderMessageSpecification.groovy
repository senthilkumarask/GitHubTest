package com.bbb.commerce.checkout.processor

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checkout.tibco.BBBSubmitOrderHandler
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.BBBTagConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import javax.servlet.http.HttpServletRequest

import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.PipelineConstants;
import atg.service.pipeline.PipelineResult
import spock.lang.specification.BBBExtendedSpec;

/**
 * 
 * @author Velmurugan Moorthy
 * 
 * This class is to unit test the ProcSendSubmitOrderMessage (processor class)
 * This class contains the spock unit test cases
 */

class ProcSendSubmitOrderMessageSpecification extends BBBExtendedSpec {

	
	// Constants - starts 
	
	private static final int SUCCESS = 1
	private static final int FAILURE = 2
	private static final String JMS_INVENTORY_DEC = "JMSInventoryDec"
	private static final String ORDER_SUBSTATUS_DUMMY_IGNORE_INVENTORY = "DUMMY_IGNORE_INVENTORY"
	private static final String ORDER_SUB_STATUS = "substatus";
	
	// Constants - ends
	
	ProcSendSubmitOrderMessage procSendSubmitOrderMessageMock
	def BBBSubmitOrderHandler submitOrderHandlerMock
	
	def setup() {
		
		submitOrderHandlerMock = Mock()
		procSendSubmitOrderMessageMock = Spy()
		
	}
	
	/*=============================================================================================================================
	 * ProcSendSubmitOrderMessageSpecification.runProcess() - test cases - STARTS		  												  *
	 * Signature of the method : public int runProcess(Object pParam, PipelineResult pResult) throws Exception					  *	
	 * ============================================================================================================================
	 */
	
	def "runProcess - ProcSendSubmitOrderMessageSpecification -> runProcess - happy flow" () {
		
		given : 
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true" 
				def orderSubStatus = "In transit"
				def List<String> config = new ArrayList<>()
				
				procSendSubmitOrderMessageMock.isLoggingDebug() >> true
				procSendSubmitOrderMessageMock.isLoggingInfo() >> true
				procSendSubmitOrderMessageMock.isLoggingError() >> true

				
				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				config.add("true")		
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
	  
	  		returnValue == SUCCESS
			1 * submitOrderHandlerMock.processSubmitOrder(*_)
			//1 * submitOrderHandlerMock.submitInventoryMesssage(*_)
			1 * submitOrderHandlerMock.sendMail(*_)
			
			0 * submitOrderHandlerMock.decrementInventoryRepository(*_)
	}

	def "runProcess - Order submission(OrderSubmissionTag) is not set (empty)" () {
		
		given :
				procSendSubmitOrderMessageMock.isLoggingDebug() >> true
				procSendSubmitOrderMessageMock.isLoggingInfo() >> true
				procSendSubmitOrderMessageMock.isLoggingError() >> true

				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = ""
				def orderSubStatus = "In transit"
				def List<String> config = new ArrayList<>()
				
				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				config.add("true")
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
	  		
	  		returnValue == 	SUCCESS
			0 * submitOrderHandlerMock.processSubmitOrder(*_)
			//1 * submitOrderHandlerMock.submitInventoryMesssage(*_)
			0 * submitOrderHandlerMock.decrementInventoryRepository(*_)
			1 * submitOrderHandlerMock.sendMail(*_)
	}
	
	def "runProcess - order is dummy order (order SubStatus is DUMMY_IGNORE_INVENTORY) " () {
		
		given :
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true" 
				def orderSubStatus = BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_IGNORE_INVENTORY
				def List<String> config = new ArrayList<>()
				
				procSendSubmitOrderMessageMock.isLoggingDebug() >> true
				procSendSubmitOrderMessageMock.isLoggingInfo() >> true
				procSendSubmitOrderMessageMock.isLoggingError() >> true

				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				config.add("true")
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
			  
			  returnValue == 	SUCCESS
			 
			  0 * submitOrderHandlerMock.processSubmitOrder(*_)
			  0 * submitOrderHandlerMock.submitInventoryMesssage(*_)
			  0 * submitOrderHandlerMock.decrementInventoryRepository(*_)
			  0 * submitOrderHandlerMock.sendMail(*_)
	}
		
	def "runProcess - order is dummy order (order SubStatus is ORDER_SUBSTATUS_DUMMY_RESTORE_INVENTORY) " () {
		
		given :
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true"
				def orderSubStatus = BBBCoreConstants.ORDER_SUBSTATUS_DUMMY_RESTORE_INVENTORY
				def List<String> config = new ArrayList<>()
		
				procSendSubmitOrderMessageMock.isLoggingDebug() >> true
				procSendSubmitOrderMessageMock.isLoggingInfo() >> true
				procSendSubmitOrderMessageMock.isLoggingError() >> true
						
				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				config.add("true")
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
			  
			returnValue == 	SUCCESS
			(3.._) * procSendSubmitOrderMessageMock.logInfo(*_);
			0 * submitOrderHandlerMock.decrementInventoryRepository(*_)
			0 * submitOrderHandlerMock.processSubmitOrder(*_)
			0 * submitOrderHandlerMock.sendMail(*_)
	}

	def "runProcess - JMS Inventory flag is empty" () {
		
		given :
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true"
				def jmsInvDecEnabled = "false"
				def orderSubStatus = "In transit"
				def List<String> config = new ArrayList<>()
			
				procSendSubmitOrderMessageMock.isLoggingDebug() >> true
				procSendSubmitOrderMessageMock.isLoggingInfo() >> true
				procSendSubmitOrderMessageMock.isLoggingError() >> true
				
				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
	  
			returnValue == SUCCESS
			1 * submitOrderHandlerMock.processSubmitOrder(*_)
			1 * submitOrderHandlerMock.sendMail(*_)
			//1 * submitOrderHandlerMock.submitInventoryMesssage(*_)
			0 * submitOrderHandlerMock.decrementInventoryRepository(*_)
	}

	def "runProcess - JMS Inventory is disabled (jmsInvDecEnabled - false) " () {
		
		given :
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true"
				def jmsInvDecEnabled = "false"
				def orderSubStatus = "In transit"
				def List<String> config = new ArrayList<>()
			
				procSendSubmitOrderMessageMock.isLoggingDebug() >> true
				procSendSubmitOrderMessageMock.isLoggingInfo() >> true
				procSendSubmitOrderMessageMock.isLoggingError() >> true
				
				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				config.add(jmsInvDecEnabled)
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
	  
			returnValue == SUCCESS
			//1 * submitOrderHandlerMock.decrementInventoryRepository(*_)
			1 * submitOrderHandlerMock.processSubmitOrder(*_)
			1 * submitOrderHandlerMock.sendMail(*_)
			
			0 * submitOrderHandlerMock.submitInventoryMesssage(*_)
	}
	
	def "runProcess - JMS Inventory is not set (jmsInvDecEnabled - null) " () {
		
		given :
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true"
				def jmsInvDecEnabled = null
				def orderSubStatus = "In transit"
				def List<String> config = new ArrayList<>()
			
				procSendSubmitOrderMessageMock.isLoggingDebug() >> true
				procSendSubmitOrderMessageMock.isLoggingInfo() >> true
				procSendSubmitOrderMessageMock.isLoggingError() >> true
				
				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				config.add(jmsInvDecEnabled)
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
	  
			returnValue == SUCCESS
			//1 * submitOrderHandlerMock.decrementInventoryRepository(*_)
			1 * submitOrderHandlerMock.processSubmitOrder(*_)
			1 * submitOrderHandlerMock.sendMail(*_)
			
			0 * submitOrderHandlerMock.submitInventoryMesssage(*_)
	}
	
	def "runProcess - order is international order and placed in desktop (channel is desktop)" () {
		
		given :
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true"
				def orderSubStatus = "In transit"
				def List<String> config = new ArrayList<>()
				
				procSendSubmitOrderMessageMock.isLoggingDebug() >> true
				procSendSubmitOrderMessageMock.isLoggingInfo() >> true
				procSendSubmitOrderMessageMock.isLoggingError() >> true
				
				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				orderMock.getSalesChannel() >> BBBInternationalShippingConstants.CHANNEL_DESKTOP_BFREE
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				config.add("true")
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
	  
			  returnValue == SUCCESS
			  1 * submitOrderHandlerMock.processSubmitOrder(*_)
			  0 * submitOrderHandlerMock.submitInventoryMesssage(*_)
			  0 * submitOrderHandlerMock.decrementInventoryRepository(*_)
			  0 * submitOrderHandlerMock.sendMail(*_)
	}
	
	def "runProcess - order is international order and placed in mobile app (channel is mobile app)" () {
		
		given :
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true"
				def orderSubStatus = "In transit"
				def List<String> config = new ArrayList<>()
				
				procSendSubmitOrderMessageMock.isLoggingDebug() >> true
				procSendSubmitOrderMessageMock.isLoggingInfo() >> true
				procSendSubmitOrderMessageMock.isLoggingError() >> true
				
				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				orderMock.getSalesChannel() >> BBBInternationalShippingConstants.CHANNEL_MOBILE_APP_BFREE
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				config.add("true")
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
	  
			  returnValue == SUCCESS
			  1 * submitOrderHandlerMock.processSubmitOrder(*_)
			  0 * submitOrderHandlerMock.submitInventoryMesssage(*_)
			  0 * submitOrderHandlerMock.decrementInventoryRepository(*_)
			  0 * submitOrderHandlerMock.sendMail(*_)
	}
	
	def "runProcess - order is international order and placed in mobile (channel is mobile)" () {
		
		given :
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true"
				def orderSubStatus = "In transit"
				def List<String> config = new ArrayList<>()
				
				procSendSubmitOrderMessageMock.isLoggingDebug() >> true
				procSendSubmitOrderMessageMock.isLoggingInfo() >> true
				procSendSubmitOrderMessageMock.isLoggingError() >> true
				
				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				orderMock.getSalesChannel() >> BBBInternationalShippingConstants.CHANNEL_MOBILE_BFREE
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				config.add("true")
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
	  
			  returnValue == SUCCESS
			  1 * submitOrderHandlerMock.processSubmitOrder(*_)
			  0 * submitOrderHandlerMock.submitInventoryMesssage(*_)
			  0 * submitOrderHandlerMock.decrementInventoryRepository(*_)
			  0 * submitOrderHandlerMock.sendMail(*_)
	}
	
	def "runProcess - Error while processing 'Send Submit Order Message' pipeline request | loggingError is enabled (true) " () {
		
			Map<String,String> param = new HashMap<String, String>()
			def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
			def HttpServletRequest httpServletRequestMock = Mock()
			def PipelineResult pipeLineResultMock = Mock()
			def BBBCatalogTools catalogToolsMock  = Mock()
			def orderSubmissionTagOn = "true"
			def orderSubStatus = "In transit"
			def List<String> config = new ArrayList<>()
			
			procSendSubmitOrderMessageMock.isLoggingDebug() >> true
			procSendSubmitOrderMessageMock.isLoggingInfo() >> true
			procSendSubmitOrderMessageMock.isLoggingError() >> true
			
			orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
			
			param.put(PipelineConstants.REQUEST,httpServletRequestMock)
			param.put(PipelineConstants.ORDER, orderMock)
			
			
			submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
			procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
			
			config.add("true")
			
			submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> {throw new Exception("Error while processing Send Submit Order Message pipeline request")}
			submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
			
	when :
	
		def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		 
	then :
	
		 returnValue == SUCCESS
		//1 * procSendSubmitOrderMessageMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while processing 'Send Submit Order Message' pipeline request", _)
	}

	def "runProcess - Error while processing 'Send Submit Order Message' pipeline request | loggingError is disabled (false) " () {
		
			Map<String,String> param = new HashMap<String, String>()
			def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
			def HttpServletRequest httpServletRequestMock = Mock()
			def PipelineResult pipeLineResultMock = Mock()
			def BBBCatalogTools catalogToolsMock  = Mock()
			def orderSubmissionTagOn = "true"
			def orderSubStatus = "In transit"
			def List<String> config = new ArrayList<>()
			
			procSendSubmitOrderMessageMock.isLoggingDebug() >> true
			procSendSubmitOrderMessageMock.isLoggingInfo() >> true
			procSendSubmitOrderMessageMock.isLoggingError() >> false
			
			orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
			
			param.put(PipelineConstants.REQUEST,httpServletRequestMock)
			param.put(PipelineConstants.ORDER, orderMock)
			
			
			submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
			procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
			
			config.add("true")
			
			submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> {throw new Exception("Error while processing Send Submit Order Message pipeline request")}
			submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
			
	when :
	
		def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		 
	then :
	
		 returnValue == SUCCESS
		0 * procSendSubmitOrderMessageMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while processing 'Send Submit Order Message' pipeline request", _)
	}
	
	def "runProcess - Error while sending Inventory decrement from 'Send Submit Order Message' pipeline request | loggingError is enabled (true)" () {
		
		given :
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true"
				def orderSubStatus = "In transit"
				def List<String> config = new ArrayList<>()
				
				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				
				procSendSubmitOrderMessageMock.isLoggingError() >> true
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				config.add("true")
				procSendSubmitOrderMessageMock.isLoggingDebug() >> true
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> {throw new Exception("Error while sending Inventory decrement from 'Send Submit Order Message' pipeline request")}
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
	  
	  		//1 * procSendSubmitOrderMessageMock.logDebug(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while sending Inventory decrement from 'Send Submit Order Message' pipeline request", _)
			returnValue == SUCCESS
	}

	def "runProcess - Error while sending Inventory decrement from 'Send Submit Order Message' pipeline request | loggingError is disabled (false)" () {
		
		given :
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true"
				def orderSubStatus = "In transit"
				def List<String> config = new ArrayList<>()
				
				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				
				procSendSubmitOrderMessageMock.isLoggingError() >> false
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				config.add("true")
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> {throw new Exception("Error while sending Inventory decrement from 'Send Submit Order Message' pipeline request")}
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
	  
			  0 * procSendSubmitOrderMessageMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while sending Inventory decrement from 'Send Submit Order Message' pipeline request", _)
			returnValue == SUCCESS
	}
		
	def "runProcess - Error while sending email from 'Send Submit Order Message' pipeline request and loggingError is enabled (true)" () {
		
		given :
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true"
				def orderSubStatus = "In transit"
				def List<String> config = new ArrayList<>()
				
				procSendSubmitOrderMessageMock.isLoggingError() >> true
				
				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				submitOrderHandlerMock.sendMail(*_) >> {throw new Exception("Error while sending email from 'Send Submit Order Message' pipeline request")}
				
				config.add("true")
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
	  
			  returnValue == SUCCESS
			  1 * procSendSubmitOrderMessageMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while sending email from 'Send Submit Order Message' pipeline request", _)
	}

	def "runProcess - Error while sending email from 'Send Submit Order Message' pipeline request and loggingError is disabled (false)" () {
		
		given :
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true"
				def orderSubStatus = "In transit"
				def List<String> config = new ArrayList<>()
				
				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				
				procSendSubmitOrderMessageMock.isLoggingError() >> false
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				submitOrderHandlerMock.sendMail(*_) >> {throw new Exception("Error while sending email from 'Send Submit Order Message' pipeline request")}
				
				config.add("true")
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
	  
			  returnValue == SUCCESS
			  0 * procSendSubmitOrderMessageMock.logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while sending email from 'Send Submit Order Message' pipeline request", _)
	}
	
	def "runProcess - Order object is corrupted/invalid (order object is null)" () {
		
		given :
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = null
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true"
				def orderSubStatus = "In transit"
				def List<String> config = new ArrayList<>()
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				config.add("true")
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
	  
			  returnValue == null
			  thrown InvalidParameterException
			  0 * submitOrderHandlerMock.processSubmitOrder(*_)
			  0 * submitOrderHandlerMock.submitInventoryMesssage(*_)
			  0 * submitOrderHandlerMock.sendMail(*_)
			  0 * submitOrderHandlerMock.decrementInventoryRepository(*_)
	}

	def "runProcess - loggingFlags (loggingDebug, loggingInfo) are disabled (false)" () {
		
		given :
		
				Map<String,String> param = new HashMap<String, String>()
				def BBBOrderImpl orderMock = Mock(["id":"bbb01"])
				def HttpServletRequest httpServletRequestMock = Mock()
				def PipelineResult pipeLineResultMock = Mock()
				def BBBCatalogTools catalogToolsMock  = Mock()
				def orderSubmissionTagOn = "true"
				def orderSubStatus = "In transit"
				def List<String> config = new ArrayList<>()

				procSendSubmitOrderMessageMock.isLoggingDebug() >> false
				procSendSubmitOrderMessageMock.isLoggingInfo() >> false
				
				orderMock.getPropertyValue(ORDER_SUB_STATUS) >> orderSubStatus
				
				param.put(PipelineConstants.REQUEST,httpServletRequestMock)
				param.put(PipelineConstants.ORDER, orderMock)
				
				submitOrderHandlerMock.getCatalogTools() >> catalogToolsMock
				procSendSubmitOrderMessageMock.setOrderHelper(submitOrderHandlerMock)
				
				config.add("true")
				
				submitOrderHandlerMock.getCatalogTools().getThirdPartyTagStatus(*_) >> orderSubmissionTagOn
				submitOrderHandlerMock.getCatalogTools().getContentCatalogConfigration(JMS_INVENTORY_DEC) >> config
				
		when :
		
			def returnValue = procSendSubmitOrderMessageMock.runProcess(param, pipeLineResultMock)
		
	  then :
	  
	  			0 * procSendSubmitOrderMessageMock.logDebug(*_)
				//0 * procSendSubmitOrderMessageMock.logInfo(*_)
	}

	/*==============================================================================
	 * ProcSendSubmitOrderMessageSpecification.runProcess() - test cases - ENDS		  	   *
	 * =============================================================================
	 */
	
	/*==============================================================================
	 * ProcSendSubmitOrderMessageSpecification.getRetCodes() - test cases - STARTS	  	   *
	 * Signature of the method : public int[] getRetCodes()						   *
	 * =============================================================================
	 */
	
	def "getRetCodes - happy flow" () {
		
		when : 
			  	def returnCodes = procSendSubmitOrderMessageMock.getRetCodes()
		then : 
				returnCodes.size() == 2 	  
		
	}
	
	/*==============================================================================
	 * ProcSendSubmitOrderMessageSpecification.getRetCodes() - test cases - ENDS	  	   	   *		
	 * =============================================================================
	 */
	
		
}


