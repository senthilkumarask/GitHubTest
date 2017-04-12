package com.bbb.commerce.browse.droplet

import java.util.List;
import java.util.Map;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.browse.manager.ProductManager
import com.bbb.commerce.catalog.vo.ProductVO
import com.bbb.commerce.catalog.vo.RollupTypeVO;
import com.bbb.constants.BBBCmsConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import spock.lang.specification.BBBExtendedSpec

class RollUpListDropletSpecification extends BBBExtendedSpec {

	RollUpListDroplet droplet = new RollUpListDroplet()
	ProductManager productManager = Mock()
	
	def setup(){
		droplet.setProductManager(productManager)
	}
	
	def "service method, Happy path"(){
		
		given:
			ProductVO productVO = new ProductVO()
			Map<String, List<RollupTypeVO>> rollupAttributes = new HashMap<String,List<RollupTypeVO>>()
			RollupTypeVO vo = new RollupTypeVO()
			RollupTypeVO vo1 = new RollupTypeVO()
			RollupTypeVO vo2 = new RollupTypeVO()
			RollupTypeVO vo3 = new RollupTypeVO()
			RollupTypeVO vo4 = new RollupTypeVO()
			rollupAttributes.put("size", [vo1,vo2])
			rollupAttributes.put("color", [vo3,vo4])
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("firstRollUpValue") >> "value"
			requestMock.getParameter("firstRollUpType") >> "prodColor"
			productManager.getProductStatus(_,_) >> true
			productManager.getEverLivingProductDetails(_,_) >> productVO
			productVO.setRollupAttributes(rollupAttributes)
			productManager.getEverLivingRollupDetails(_, _, _, _) >> [vo]
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("jsonObject","SIZE")
			1*requestMock.setParameter("rollupList",[vo])
			1*requestMock.serviceParameter("output",requestMock,responseMock)		
	}
	
	def "service method, firstRollUpType set as prodSize"(){
		
		given:
			ProductVO productVO = new ProductVO()
			Map<String, List<RollupTypeVO>> rollupAttributes = new HashMap<String,List<RollupTypeVO>>()
			RollupTypeVO vo = new RollupTypeVO()
			RollupTypeVO vo1 = new RollupTypeVO()
			RollupTypeVO vo2 = new RollupTypeVO()
			RollupTypeVO vo3 = new RollupTypeVO()
			RollupTypeVO vo4 = new RollupTypeVO()
			rollupAttributes.put("size", [vo1,vo2])
			rollupAttributes.put("color", [vo3,vo4])
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("firstRollUpValue") >> "value"
			requestMock.getParameter("firstRollUpType") >> "prodSize"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			productManager.getProductStatus(_,_) >> false
			productManager.getProductDetails(_,_) >> productVO
			productVO.setRollupAttributes(rollupAttributes)
			productManager.getRollupDetails(_, _, _, _) >> [vo]
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("jsonObject","COLOR")
			1*requestMock.setParameter("rollupList",[vo])
			1*requestMock.serviceParameter("output",requestMock,responseMock)	
	}
	
	def "service method, firstRollUpType set as prodSize1 and siteId not null and rollUpAttributes is null"(){
		
		given:
			ProductVO productVO = new ProductVO()
			RollupTypeVO vo = new RollupTypeVO()
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("firstRollUpValue") >> "value"
			requestMock.getParameter("firstRollUpType") >> "prodSize1"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			productManager.getProductStatus(_,_) >> false
			productManager.getProductDetails(_,_) >> productVO
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			0*requestMock.setParameter("jsonObject","COLOR")
			0*requestMock.setParameter("rollupList",[vo])
			1*requestMock.serviceParameter("error",requestMock,responseMock)
	}
	
	def "service method, rollUpAttributes size is 1"(){
		
		given:
			ProductVO productVO = new ProductVO()
			Map<String, List<RollupTypeVO>> rollupAttributes = new HashMap<String,List<RollupTypeVO>>()
			RollupTypeVO vo = new RollupTypeVO()
			RollupTypeVO vo1 = new RollupTypeVO()
			RollupTypeVO vo2 = new RollupTypeVO()
			rollupAttributes.put("size", [vo1,vo2])
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("firstRollUpValue") >> "value"
			requestMock.getParameter("firstRollUpType") >> "prodSize"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			productManager.getProductStatus(_,_) >> false
			productManager.getProductDetails(_,_) >> productVO
			productVO.setRollupAttributes(rollupAttributes)
			productManager.getRollupDetails(_, _, _, _) >> [vo]
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			0*requestMock.setParameter("jsonObject","COLOR")
			0*requestMock.setParameter("rollupList",[vo])
			1*requestMock.serviceParameter("error",requestMock,responseMock)
	}
	
	def "service method, secondRollUpAttribute is color1"(){
		
		given:
			ProductVO productVO = new ProductVO()
			Map<String, List<RollupTypeVO>> rollupAttributes = new HashMap<String,List<RollupTypeVO>>()
			RollupTypeVO vo = new RollupTypeVO()
			RollupTypeVO vo1 = new RollupTypeVO()
			RollupTypeVO vo2 = new RollupTypeVO()
			RollupTypeVO vo3 = new RollupTypeVO()
			RollupTypeVO vo4 = new RollupTypeVO()
			rollupAttributes.put("size", [vo1,vo2])
			rollupAttributes.put("color1", [vo3,vo4])
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("firstRollUpValue") >> "value"
			requestMock.getParameter("firstRollUpType") >> "prodSize"
			requestMock.getParameter(BBBCmsConstants.SITE_ID) >> "siteId"
			productManager.getProductStatus(_,_) >> false
			productManager.getProductDetails(_,_) >> productVO
			productVO.setRollupAttributes(rollupAttributes)
			productManager.getRollupDetails(_, _, _, _) >> [vo]
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*requestMock.setParameter("jsonObject","FINISH")
			1*requestMock.setParameter("rollupList",[vo])
			1*requestMock.serviceParameter("output",requestMock,responseMock)
	}
	
	def "service method, product id is null"(){
		
		given:
			ProductVO productVO = new ProductVO()
			Map<String, List<RollupTypeVO>> rollupAttributes = new HashMap<String,List<RollupTypeVO>>()
			RollupTypeVO vo = new RollupTypeVO()
			RollupTypeVO vo1 = new RollupTypeVO()
			RollupTypeVO vo2 = new RollupTypeVO()
			RollupTypeVO vo3 = new RollupTypeVO()
			RollupTypeVO vo4 = new RollupTypeVO()
			rollupAttributes.put("size", [vo1,vo2])
			rollupAttributes.put("color", [vo3,vo4])
			requestMock.getParameter("firstRollUpValue") >> "value"
			requestMock.getParameter("firstRollUpType") >> "prodColor"
			droplet.getCurrentSiteId() >> "siteId"
			productManager.getProductStatus(_,_) >> true
			productManager.getEverLivingProductDetails(_,_) >> productVO
			productVO.setRollupAttributes(rollupAttributes)
			productManager.getEverLivingRollupDetails(_, _, _, _) >> [vo]
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			0*requestMock.setParameter("jsonObject","COLOR")
			0*requestMock.setParameter("rollupList",[vo])
			1*requestMock.serviceParameter("error",requestMock,responseMock)
	}
	
	def "service method, BBBSystemException is thrown"(){
		
		given:
			droplet = Spy()
			droplet.setProductManager(productManager)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("firstRollUpValue") >> "value"
			requestMock.getParameter("firstRollUpType") >> "prodColor"
			droplet.getCurrentSiteId() >> "siteId"
			productManager.getProductStatus(_,_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "System Exception  from service of RollUpListDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1035),_)
			1*requestMock.serviceParameter("error",requestMock,responseMock)
	}
	
	def "service method, BBBBusinessException is thrown"(){
		
		given:
			droplet = Spy()
			droplet.setProductManager(productManager)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prdId"
			requestMock.getParameter("firstRollUpValue") >> "value"
			requestMock.getParameter("firstRollUpType") >> "prodColor"
			droplet.getCurrentSiteId() >> "siteId"
			productManager.getProductStatus(_,_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
			
		when:
			droplet.service(requestMock,responseMock)
			
		then:
			1*droplet.logError(LogMessageFormatter.formatMessage(requestMock, "Business Exception  from service of RollUpListDroplet ",BBBCoreErrorConstants.BROWSE_ERROR_1034),_)
			1*requestMock.serviceParameter("error",requestMock,responseMock)
	}
	
}

