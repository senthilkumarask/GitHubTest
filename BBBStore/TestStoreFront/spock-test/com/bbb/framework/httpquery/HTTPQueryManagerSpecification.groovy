package com.bbb.framework.httpquery

import java.util.List;
import java.util.Map

import com.bbb.account.service.profile.ResponseVO;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.httpquery.parser.ResultParserIF;
import com.bbb.framework.httpquery.vo.HTTPServiceRequestIF
import com.bbb.framework.httpquery.vo.HTTPServiceResponseIF
import com.bbb.utils.BBBUtility

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;

/**
 *
 * @author Velmurugan Moorthy
 *
 * Changes made in Java file :
 * 
 * #212 - getResultParserIF() - extracted (created) to mock the result
 * #151 - if(! BBBUtility.isEmpty(key)) - Utility method's null check added
 *
 */

class HTTPQueryManagerSpecification extends BBBExtendedSpec {

	private HTTPQueryManager httpQueryManager
	private BBBCatalogTools catalogToolsMock
	private HTTPCallInvoker httpCallInvokerMock

	
	def setup(){
		
		catalogToolsMock = Mock()
		httpCallInvokerMock = Mock()
		
		httpQueryManager = new HTTPQueryManager([mCatalogTools : catalogToolsMock, mHttpCallInvoker : httpCallInvokerMock ])
		
	}
	
	
	/*
	 * invoke - Test cases STARTS
	 * 
	 * Methods invoked internally
	 * 
	 * 
	 */
	
	/*
	 * Alternative branches covered : 
	 * 
	 *
	 */
	
	def "invoke - Invoked HTTP query operation successfully" () {
		
		given : 
		
		HTTPQueryManager httpQueryManagerSpy = Spy()
		
		HTTPServiceRequestIF pRequestVO = Mock()
		HTTPServiceResponseIF responseVO = Mock()
		HTTPServiceResponseIF returnValue = Mock()
		
		httpQueryManagerSpy.setCatalogTools(catalogToolsMock)
		httpQueryManagerSpy.setHttpCallInvoker(httpCallInvokerMock)
		
		Map<String,String> paramsValuesMap = new HashMap<>()
		def serviceName = "pdp_fbw;pdp_cav; "
		def serviceType = "request"
		def siteId = "BedBathUS"
		def remoteResponse = "Response01"  
		def parserClass = "com.bbb.cmo.processor.GetCMOLineItemsResponseProcessor"
		def hostTargetURL = "http://host.bedbath.com"
		
		ResultParserIF resultParser = Mock()
		
		List<String> reqList = new ArrayList<>()
		Map<String, String> wsResponseProcessorMap = new HashMap<>()
		Map<String, String> thirdPartyURSMap = new HashMap<>()
		
		reqList.addAll(["view", "execute", "query", "view"])
		
		wsResponseProcessorMap.put(serviceType, parserClass)
		thirdPartyURSMap.put(serviceType, hostTargetURL)
		
		pRequestVO.getParamsValuesMap() >> paramsValuesMap 
		pRequestVO.getServiceName() >> serviceName
		pRequestVO.getServiceType() >> serviceType
		pRequestVO.getSiteId() >> siteId

		httpQueryManagerSpy.getResultParserIF(parserClass) >> resultParser
		
		resultParser.parse(remoteResponse) >> responseVO
				
		(1.._) * catalogToolsMock.getAllValuesForKey(*_) >> reqList
		(1.._) * catalogToolsMock.getConfigValueByconfigType("ThirdPartyURLs") >> thirdPartyURSMap
		(1.._) * catalogToolsMock.getConfigValueByconfigType("WSResponseProcessor") >> wsResponseProcessorMap
		(1.._) * httpCallInvokerMock.invoke(*_) >> remoteResponse
		
		when : 
		
		returnValue = httpQueryManagerSpy.invoke(pRequestVO)
		
		then : 
		
		returnValue  == responseVO 
	}
	
	def "invoke - Exception while invoking the HTTP query operation" () {
		
		given :
		
		HTTPServiceRequestIF pRequestVO = Mock()
		
		Map<String,String> paramsValuesMap = new HashMap<>()
		def serviceName = ";"
		def serviceType = "request"
		def siteId = "BedBathUS"
		def remoteResponse = "Response01"
		def parserClass = "com.bbb.cmo.processor.GetCMOLineItemsResponseProcessor"
		def hostTargetURL = "http://host.bedbath.com"
		
		List<String> reqList = new ArrayList<>()
		Map<String, String> wsResponseProcessorMap = new HashMap<>()
		Map<String, String> thirdPartyURSMap = new HashMap<>()
		
		reqList.addAll(["view", "execute", "query", "view"])
		
		wsResponseProcessorMap.put(serviceType, parserClass)
		thirdPartyURSMap.put(serviceType, hostTargetURL)
		
		pRequestVO.getParamsValuesMap() >> paramsValuesMap
		pRequestVO.getServiceName() >> serviceName
		pRequestVO.getServiceType() >> serviceType
		pRequestVO.getSiteId() >> siteId
		
		(1.._) * catalogToolsMock.getAllValuesForKey(*_) >> reqList
		(1.._) * catalogToolsMock.getConfigValueByconfigType("ThirdPartyURLs") >> thirdPartyURSMap
		(1.._) * catalogToolsMock.getConfigValueByconfigType("WSResponseProcessor") >> wsResponseProcessorMap
		(1.._) * httpCallInvokerMock.invoke(*_) >> remoteResponse
		
		when : 
		
		httpQueryManager.invoke(pRequestVO)
		
		then : 
		
		 BBBBusinessException ex = thrown() 
		 ex.getMessage().equals("ERR_HTTP_QUERY_BIZ_EXCEPTION:ERR_HTTP_QUERY_BIZ_EXCEPTION")
	}
	
	def "invoke - Host target URL is invalid - null" () {
		
		given :
		
		HTTPServiceRequestIF pRequestVO = Mock()
		
		Map<String,String> paramsValuesMap = new HashMap<>()
		def serviceName = ";"
		def serviceType = "request"
		def siteId = "BedBathUS"
		def remoteResponse = "Response01"
		def parserClass = "com.bbb.cmo.processor.GetCMOLineItemsResponseProcessor"
		//def hostTargetURL = "http://host.bedbath.com"
		def hostTargetURL = null
		
		List<String> reqList = new ArrayList<>()
		//Map<String, String> wsResponseProcessorMap = new HashMap<>()
		Map<String, String> thirdPartyURSMap = new HashMap<>()
		
		reqList.addAll(["view", "execute", "query", "view"])
		
		//wsResponseProcessorMap.put(serviceType, parserClass)
		thirdPartyURSMap.put(serviceType, hostTargetURL)
		
		pRequestVO.getParamsValuesMap() >> paramsValuesMap
		pRequestVO.getServiceName() >> serviceName
		pRequestVO.getServiceType() >> serviceType
		pRequestVO.getSiteId() >> siteId
		
		(1.._) * catalogToolsMock.getAllValuesForKey(*_) >> reqList
		(1.._) * catalogToolsMock.getConfigValueByconfigType("ThirdPartyURLs") >> thirdPartyURSMap
		0 * catalogToolsMock.getConfigValueByconfigType("WSResponseProcessor")
		0  * httpCallInvokerMock.invoke(*_) >> remoteResponse
		
		when :
		
		httpQueryManager.invoke(pRequestVO)
		
		then :
		
		BBBBusinessException ex = thrown()
		ex.getMessage().equals("ERR_HTTP_QUERY_HOSTURL_NULL:ERR_HTTP_QUERY_HOSTURL_NULL")
	}
	
	def "invoke - Exception while getting parser | InstantiationException" () {
		
		given :
		
		HTTPQueryManager httpQueryManagerSpy = Spy()
		
		HTTPServiceRequestIF pRequestVO = Mock()
		HTTPServiceResponseIF responseVO = Mock()
		HTTPServiceResponseIF returnValue = Mock()
		
		httpQueryManagerSpy.setCatalogTools(catalogToolsMock)
		httpQueryManagerSpy.setHttpCallInvoker(httpCallInvokerMock)
		
		Map<String,String> paramsValuesMap = new HashMap<>()
		def serviceName = "pdp_fbw;pdp_cav; "
		def serviceType = "request"
		def siteId = "BedBathUS"
		def remoteResponse = "Response01"
		def parserClass = "com.bbb.cmo.processor.GetCMOLineItemsResponseProcessor"
		def hostTargetURL = "http://host.bedbath.com"
		
		ResultParserIF resultParser = Mock()
		
		List<String> reqList = new ArrayList<>()
		Map<String, String> wsResponseProcessorMap = new HashMap<>()
		Map<String, String> thirdPartyURSMap = new HashMap<>()
		
		reqList.addAll(["view", "execute", "query", "view"])
		
		wsResponseProcessorMap.put(serviceType, parserClass)
		thirdPartyURSMap.put(serviceType, hostTargetURL)
		
		pRequestVO.getParamsValuesMap() >> paramsValuesMap
		pRequestVO.getServiceName() >> serviceName
		pRequestVO.getServiceType() >> serviceType
		pRequestVO.getSiteId() >> siteId

		httpQueryManagerSpy.getResultParserIF(parserClass) >> {throw new InstantiationException()}
		
		resultParser.parse(remoteResponse) >> responseVO
				
		(1.._) * catalogToolsMock.getAllValuesForKey(*_) >> reqList
		(1.._) * catalogToolsMock.getConfigValueByconfigType("ThirdPartyURLs") >> thirdPartyURSMap
		(1.._) * catalogToolsMock.getConfigValueByconfigType("WSResponseProcessor") >> wsResponseProcessorMap
		(1.._) * httpCallInvokerMock.invoke(*_) >> remoteResponse
		
		when :
		
		returnValue = httpQueryManagerSpy.invoke(pRequestVO)
		
		then :
		
		BBBBusinessException ex = thrown()
		ex.getMessage().equals("ERR_HTTP_QUERY_BIZ_EXCEPTION:ERR_HTTP_QUERY_BIZ_EXCEPTION")
	}
	
	//IllegalAccessException
	
	def "invoke - Exception while getting parser | IllegalAccessException" () {
		
		given :
		
		HTTPQueryManager httpQueryManagerSpy = Spy()
		
		HTTPServiceRequestIF pRequestVO = Mock()
		HTTPServiceResponseIF responseVO = Mock()
		HTTPServiceResponseIF returnValue = Mock()
		
		httpQueryManagerSpy.setCatalogTools(catalogToolsMock)
		httpQueryManagerSpy.setHttpCallInvoker(httpCallInvokerMock)
		
		Map<String,String> paramsValuesMap = new HashMap<>()
		def serviceName = "pdp_fbw;pdp_cav; "
		def serviceType = "request"
		def siteId = "BedBathUS"
		def remoteResponse = "Response01"
		def parserClass = "com.bbb.cmo.processor.GetCMOLineItemsResponseProcessor"
		def hostTargetURL = "http://host.bedbath.com"
		
		ResultParserIF resultParser = Mock()
		
		List<String> reqList = new ArrayList<>()
		Map<String, String> wsResponseProcessorMap = new HashMap<>()
		Map<String, String> thirdPartyURSMap = new HashMap<>()
		
		reqList.addAll(["view", "execute", "query", "view"])
		
		wsResponseProcessorMap.put(serviceType, parserClass)
		thirdPartyURSMap.put(serviceType, hostTargetURL)
		
		pRequestVO.getParamsValuesMap() >> paramsValuesMap
		pRequestVO.getServiceName() >> serviceName
		pRequestVO.getServiceType() >> serviceType
		pRequestVO.getSiteId() >> siteId

		httpQueryManagerSpy.getResultParserIF(parserClass) >> {throw new IllegalAccessException()}
		
		resultParser.parse(remoteResponse) >> responseVO
				
		(1.._) * catalogToolsMock.getAllValuesForKey(*_) >> reqList
		(1.._) * catalogToolsMock.getConfigValueByconfigType("ThirdPartyURLs") >> thirdPartyURSMap
		(1.._) * catalogToolsMock.getConfigValueByconfigType("WSResponseProcessor") >> wsResponseProcessorMap
		(1.._) * httpCallInvokerMock.invoke(*_) >> remoteResponse
		
		when :
		
		returnValue = httpQueryManagerSpy.invoke(pRequestVO)
		
		then :
		
		BBBBusinessException ex = thrown()
		ex.getMessage().equals("ERR_HTTP_QUERY_BIZ_EXCEPTION:ERR_HTTP_QUERY_BIZ_EXCEPTION")
	}

	
	def "invoke - Exception while invoking HTTP call" () {
		
		given :
		
		HTTPQueryManager httpQueryManagerSpy = Spy()
		
		HTTPServiceRequestIF pRequestVO = Mock()
		HTTPServiceResponseIF responseVO = Mock()
		HTTPServiceResponseIF returnValue = Mock()
		
		httpQueryManagerSpy.setCatalogTools(catalogToolsMock)
		httpQueryManagerSpy.setHttpCallInvoker(httpCallInvokerMock)
		
		Map<String,String> paramsValuesMap = new HashMap<>()
		def serviceName = "pdp_fbw;pdp_cav; "
		def serviceType = "request"
		def siteId = "BedBathUS"
		def remoteResponse = "Response01"
		def parserClass = "com.bbb.cmo.processor.GetCMOLineItemsResponseProcessor"
		def hostTargetURL = "http://host.bedbath.com"
		
		ResultParserIF resultParser = Mock()
		
		List<String> reqList = new ArrayList<>()
		Map<String, String> wsResponseProcessorMap = new HashMap<>()
		Map<String, String> thirdPartyURSMap = new HashMap<>()
		
		reqList.addAll(["view", "execute", "query", "view"])
		
		wsResponseProcessorMap.put(serviceType, parserClass)
		thirdPartyURSMap.put(serviceType, hostTargetURL)
		
		pRequestVO.getParamsValuesMap() >> paramsValuesMap
		pRequestVO.getServiceName() >> serviceName
		pRequestVO.getServiceType() >> serviceType
		pRequestVO.getSiteId() >> siteId

		httpQueryManagerSpy.getResultParserIF(parserClass) >> resultParser
		
		resultParser.parse(remoteResponse) >> responseVO
				
		(1.._) * catalogToolsMock.getAllValuesForKey(*_) >> reqList
		(1.._) * catalogToolsMock.getConfigValueByconfigType("ThirdPartyURLs") >> thirdPartyURSMap
		0 * catalogToolsMock.getConfigValueByconfigType("WSResponseProcessor") >> wsResponseProcessorMap
		(1.._) * httpCallInvokerMock.invoke(*_) >> {throw new BBBSystemException("")}
		
		when :
		
		returnValue = httpQueryManagerSpy.invoke(pRequestVO)
		
		then :
		
		returnValue == null
	}
	
	
	def "invoke - Parsed value is invalid - null | Service names are invalid - null" () {
		
		given :
		
		HTTPQueryManager httpQueryManagerSpy = Spy()
		
		HTTPServiceRequestIF pRequestVO = Mock()
		HTTPServiceResponseIF responseVO = Mock()
		HTTPServiceResponseIF returnValue = Mock()
		
		httpQueryManagerSpy.setCatalogTools(catalogToolsMock)
		httpQueryManagerSpy.setHttpCallInvoker(httpCallInvokerMock)
		
		Map<String,String> paramsValuesMap = new HashMap<>()
		def serviceName = "pdp_fbw;pdp_cav; "
		def serviceType = "request"
		def siteId = "BedBathUS"
		def remoteResponse = "Response01"
		def parserClass = "com.bbb.cmo.processor.GetCMOLineItemsResponseProcessor"
		def hostTargetURL = "http://host.bedbath.com"
		
		ResultParserIF resultParser = Mock()
		
		List<String> reqList = new ArrayList<>()
		Map<String, String> wsResponseProcessorMap = new HashMap<>()
		Map<String, String> thirdPartyURSMap = new HashMap<>()
		
		reqList.addAll(["view", "execute", "query", "view"])
		
		wsResponseProcessorMap.put(serviceType, parserClass)
		thirdPartyURSMap.put(serviceType, hostTargetURL)
		
		pRequestVO.getParamsValuesMap() >> paramsValuesMap
		pRequestVO.getServiceName() >> null
		pRequestVO.getServiceType() >> serviceType
		pRequestVO.getSiteId() >> siteId

		httpQueryManagerSpy.getResultParserIF(parserClass) >> resultParser
		
		resultParser.parse(remoteResponse) >> null
				
		(1.._) * catalogToolsMock.getAllValuesForKey(*_) >> reqList
		(1.._) * catalogToolsMock.getConfigValueByconfigType("ThirdPartyURLs") >> thirdPartyURSMap
		(1.._) * catalogToolsMock.getConfigValueByconfigType("WSResponseProcessor") >> wsResponseProcessorMap
		(1.._) * httpCallInvokerMock.invoke(*_) >> remoteResponse
		
		when :
		
		returnValue = httpQueryManagerSpy.invoke(pRequestVO)
		
		then :
		
		returnValue == null
	}

	def "invoke - Exception while parsing - HTTP query parser is invalid - NULL" () {
		
		given :
		
		HTTPQueryManager httpQueryManagerSpy = Spy()
		
		HTTPServiceRequestIF pRequestVO = Mock()
		HTTPServiceResponseIF responseVO = Mock()
		HTTPServiceResponseIF returnValue = Mock()
		
		httpQueryManagerSpy.setCatalogTools(catalogToolsMock)
		httpQueryManagerSpy.setHttpCallInvoker(httpCallInvokerMock)
		
		Map<String,String> paramsValuesMap = new HashMap<>()
		def serviceName = "pdp_fbw;pdp_cav; "
		def serviceType = "request"
		def siteId = "BedBathUS"
		def remoteResponse = "Response01"
		def parserClass = ""
		def hostTargetURL = "http://host.bedbath.com"
		
		ResultParserIF resultParser = Mock()
		
		List<String> reqList = new ArrayList<>()
		Map<String, String> wsResponseProcessorMap = new HashMap<>()
		Map<String, String> thirdPartyURSMap = new HashMap<>()
		
		reqList.addAll(["view", "execute", "query", "view"])
		
		wsResponseProcessorMap.put(serviceType, parserClass)
		thirdPartyURSMap.put(serviceType, hostTargetURL)
		
		pRequestVO.getParamsValuesMap() >> paramsValuesMap
		pRequestVO.getServiceName() >> serviceName
		pRequestVO.getServiceType() >> serviceType
		pRequestVO.getSiteId() >> siteId

		httpQueryManagerSpy.getResultParserIF(parserClass) >> resultParser
		
		resultParser.parse(remoteResponse) >> responseVO
				
		(1.._) * catalogToolsMock.getAllValuesForKey(*_) >> reqList
		(1.._) * catalogToolsMock.getConfigValueByconfigType("ThirdPartyURLs") >> thirdPartyURSMap
		(1.._) * catalogToolsMock.getConfigValueByconfigType("WSResponseProcessor") >> wsResponseProcessorMap
		(1.._) * httpCallInvokerMock.invoke(*_) >> remoteResponse
		
		when :
		
		returnValue = httpQueryManagerSpy.invoke(pRequestVO)
		
		then :
		
		BBBBusinessException ex = thrown()
		ex.getMessage().equals("ERR_HTTP_QUERY_PARSER_NULL:ERR_HTTP_QUERY_PARSER_NULL")
	}
		
}
