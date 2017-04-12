package com.bbb.commerce.browse.droplet

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec

class MinimalProductDetailDropletSpecification extends BBBExtendedSpec {
	
	def BBBCatalogTools mCatalogTools = Mock()
	
	MinimalProductDetailDroplet droplet = new MinimalProductDetailDroplet()
	
	def setup(){
		droplet.setCatalogTools(mCatalogTools)
	}
	
	def "service method, happy path"(){
		
		given:
			def ProductVO vo = new ProductVO()
			droplet.setLoggingDebug(true)
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prodId"
			requestMock.getParameter(BBBSearchBrowseConstants.IS_META_DETAILS) >> "true"
			1*mCatalogTools.getProductVOMetaDetails(_,_) >> vo
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter(BBBCoreConstants.PRODUCTVO,vo);
			1*requestMock.serviceParameter(BBBCoreConstants.OPARAM, _, _)
	}
	
	def "service method, isMetadataRequired is false"(){
		
		given:
			def ProductVO vo = new ProductVO()
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prodId"
			requestMock.getParameter(BBBSearchBrowseConstants.IS_META_DETAILS) >> "false"
			1*mCatalogTools.getProductDetails(_,_,_,_,_) >> vo
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			1*requestMock.setParameter(BBBCoreConstants.PRODUCTVO,vo);
			1*requestMock.serviceParameter(BBBCoreConstants.OPARAM, _, _)
	}
	
	def "service method, isMetadataRequired is false and BBBSystemException is thrown"(){
		
		given:
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prodId"
			requestMock.getParameter(BBBSearchBrowseConstants.IS_META_DETAILS) >> "false"
			1*mCatalogTools.getProductDetails(_,_,_,_,_) >> {throw new BBBSystemException("BBBSystemException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			0*droplet.logDebug("MinimalProductDetailDroplet getProductVOMetaDetails:productId")
	}
	
	def "service method, isMetadataRequired is null and BBBBusinessException is thrown"(){
		
		given:
			requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER) >> "prodId"
			requestMock.getParameter(BBBSearchBrowseConstants.IS_META_DETAILS) >> null
			1*mCatalogTools.getProductDetails(_,_,_,_,_) >> {throw new BBBBusinessException("BBBBusinessException is thrown")}
		
		when:
			droplet.service(requestMock, responseMock)
		
		then:
			0*droplet.logDebug("MinimalProductDetailDroplet getProductVOMetaDetails:productId")
	}
}
