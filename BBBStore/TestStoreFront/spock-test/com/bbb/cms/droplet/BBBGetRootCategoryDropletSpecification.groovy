package com.bbb.cms.droplet

import java.util.Map;

import com.bbb.certona.CertonaConfig;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.CategoryVO
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;
import com.sun.jms.admin.Administrator.SpyMessageListener;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec;

class BBBGetRootCategoryDropletSpecification extends BBBExtendedSpec {
	
	def BBBGetRootCategoryDroplet testObj
	def BBBCatalogTools catalogToolsMock = Mock()
	 
	def setup(){
		
		testObj = Spy()
		
		testObj.setCatalogTools(catalogToolsMock)
		
		testObj.getCatalogTools() >> catalogToolsMock
		
	}
	
	def "testService when childCategory is not null"(){
		given:
		          
		String childCatId="childCatId"
		String rootCategoryId="rootCategoryId"
		String rootCategory="rootCategory"
		
		CategoryVO categoryVO = new CategoryVO()
		categoryVO.setCategoryId(rootCategoryId)
		
		 
		Map<String, CategoryVO> categoryMap= new HashMap<String, CategoryVO>()
		categoryMap.put("0", categoryVO)
		
		
		2* requestMock.getParameter("childCategory") >> childCatId
		1* testObj.logDebug("Starting method BBBGetRootCategoryDroplet")
		1* testObj.logDebug("Existing method BBBGetRootCategoryDroplet")
		1* testObj.logDebug("ChildCategory :"+childCatId)
		1* testObj.logDebug("RootCategory :"+rootCategoryId)
		1* requestMock.serviceParameter("output", requestMock, responseMock)
		1* requestMock.setParameter("rootCategory", rootCategoryId);
		
		1* catalogToolsMock.getParentCategory(childCatId, null) >> categoryMap
		expect:
		testObj.service(requestMock, responseMock)
		
	}
	
	def "testService when childCategory is not null and categoryMap have multiple CategoryVO in categoryMap for a child category "(){
		given:
				  
		String childCatId="childCatId"
		String rootCategoryId="rootCategoryId"
		String rootCategory="rootCategory"
		
		
		String rootCategoryId2="rootCategoryId"
		
		CategoryVO categoryVO = new CategoryVO()
		categoryVO.setCategoryId(rootCategoryId)
		
		CategoryVO categoryVO1 = new CategoryVO()
		categoryVO1.setCategoryId(rootCategoryId2)
		
		 
		Map<String, CategoryVO> categoryMap= new HashMap<String, CategoryVO>()
		categoryMap.put("0", categoryVO)
		categoryMap.put("1", categoryVO1)
		
		
		2* requestMock.getParameter("childCategory") >> childCatId
		1* testObj.logDebug("Starting method BBBGetRootCategoryDroplet")
		1* testObj.logDebug("Existing method BBBGetRootCategoryDroplet")
		1* testObj.logDebug("ChildCategory :"+childCatId)
		1* testObj.logDebug("RootCategory :"+rootCategoryId)
		1* requestMock.serviceParameter("output", requestMock, responseMock)
		1* requestMock.setParameter("rootCategory", rootCategoryId2);
		
		1* catalogToolsMock.getParentCategory(childCatId, null) >> categoryMap
		expect:
		testObj.service(requestMock, responseMock)
		
	}
	
	def "testService when  categoryMap have null value for a child category"(){
		given:
				  
		String childCatId="childCatId"
		String rootCategoryId="rootCategoryId"
		String rootCategory="rootCategory"
		
		CategoryVO categoryVO = new CategoryVO()
		categoryVO.setCategoryId(rootCategoryId)
		
		 
		Map<String, CategoryVO> categoryMap= new HashMap<String, CategoryVO>()
		categoryMap.put("0", null)
		
		
		2* requestMock.getParameter("childCategory") >> childCatId
		1* testObj.logDebug("Starting method BBBGetRootCategoryDroplet")
		1* testObj.logDebug("Existing method BBBGetRootCategoryDroplet")
		1* testObj.logDebug("ChildCategory :"+childCatId)
		0* testObj.logDebug("RootCategory :"+rootCategoryId)
		0* requestMock.serviceParameter("output", requestMock, responseMock)
		0* requestMock.setParameter("rootCategory", rootCategoryId);
		1* catalogToolsMock.getParentCategory(childCatId, null) >> categoryMap
		expect:
		testObj.service(requestMock, responseMock)
		
	}
	
	def "testService when childCategory is not null and childCategory dont have parent category"(){
		given:
				  
		String childCatId="childCatId"
		String rootCategoryId="rootCategoryId"
		String rootCategory="rootCategory"
		
		CategoryVO categoryVO = new CategoryVO()
		categoryVO.setCategoryId(rootCategoryId)
		
		2* requestMock.getParameter("childCategory") >> childCatId
		1* testObj.logDebug("Starting method BBBGetRootCategoryDroplet")
		1* testObj.logDebug("Existing method BBBGetRootCategoryDroplet")
		1* testObj.logDebug("ChildCategory :"+childCatId)
		0* testObj.logDebug("RootCategory :"+rootCategoryId)
		1* requestMock.serviceParameter("empty", requestMock, responseMock)
		
		1* catalogToolsMock.getParentCategory(childCatId, null) >> null
		expect:
		testObj.service(requestMock, responseMock)
		
	}
	
	def "testService when childCategory is not null and BBBBusinessException in getParentCategory"(){
		given:
				  
		String childCatId="childCatId"
		String rootCategoryId="rootCategoryId"
		String rootCategory="rootCategory"
		
		CategoryVO categoryVO = new CategoryVO()
		categoryVO.setCategoryId(rootCategoryId)
		
		2* requestMock.getParameter("childCategory") >> childCatId
		1* testObj.logDebug("Starting method BBBGetRootCategoryDroplet")
		1* testObj.logDebug("Existing method BBBGetRootCategoryDroplet")
		1* testObj.logError("Exception while fetching category ["+childCatId+"] - BBBBusinessException");
		
		1* testObj.logDebug("ChildCategory :"+childCatId)
		0* testObj.logDebug("RootCategory :"+rootCategoryId)
		1* requestMock.serviceParameter("error", requestMock, responseMock)
		
		1* catalogToolsMock.getParentCategory(childCatId, null) >>  {throw new BBBBusinessException("BBBBusinessException")}
		expect:
		testObj.service(requestMock, responseMock)
		
	}
	
	def "testService when childCategory is not null and BBBSystemException in getParentCategory"(){
		given:
				  
		String childCatId="childCatId"
		String rootCategoryId="rootCategoryId"
		String rootCategory="rootCategory"
		
		CategoryVO categoryVO = new CategoryVO()
		categoryVO.setCategoryId(rootCategoryId)
		
		2* requestMock.getParameter("childCategory") >> childCatId
		1* testObj.logDebug("Starting method BBBGetRootCategoryDroplet")
		1* testObj.logDebug("Existing method BBBGetRootCategoryDroplet")
		1* testObj.logError(LogMessageFormatter.formatMessage(requestMock, "BBBGetRootCategoryDroplet|service()|BBBSystemException","catalog_1026s"),_);
		
		1* testObj.logDebug("ChildCategory :"+childCatId)
		0* testObj.logDebug("RootCategory :"+rootCategoryId)
		1* requestMock.serviceParameter("error", requestMock, responseMock)
		
		1* catalogToolsMock.getParentCategory(childCatId, null) >>  {throw new BBBSystemException("BBBSystemException")}
		expect:
		testObj.service(requestMock, responseMock)
		
	}
	
	
	
	def "testService when childCategory is  null"(){
		given:
				  
		String childCatId="childCatId"
		String rootCategoryId=null
		String rootCategory="rootCategory"
		
	
		1* testObj.logDebug("Starting method BBBGetRootCategoryDroplet")
		1* testObj.logDebug("Existing method BBBGetRootCategoryDroplet")
		
		1* requestMock.getParameter("childCategory") >> null
		1* requestMock.serviceParameter("empty", requestMock, responseMock)
		 
		expect:
		testObj.service(requestMock, responseMock)
		
	}

}
