package com.bbb.commerce.browse.droplet

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException

import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.manager.ProductManager
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class BreadcrumbDropletSpecification extends BBBExtendedSpec {
	
	BreadcrumbDroplet testObj
	ProductManager productManagerMock = Mock()
	BBBCatalogTools bbbCatalogToolsMock = Mock()
	MutableRepository catalogRepositoryMock = Mock()
	RepositoryItem repositoryItemMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	public final static String  OPARAM_BREADCRUMB="breadCrumb";
	public final static String  ISORPHANPRODUCT = "isOrphanProduct";
	public final static String  OPARAM_OUTPUT="output";
	public final static String  OPARAM_CIRCULAR="output_circular";
	public final static String  OPARAM_ERROR="error";
	public final static String  PARAMETER_CATEGORYID ="categoryId";
	public final static String  PARAMETER_PRODUCTID ="productId";
	public final static String  PARAMETER_POC ="poc";
	public final static String  PARAMETER_OMNITURE ="forOmniture";
	public final static String  PARAMETER_SITEID ="siteId";
	public final static String  PARAMETER_RFXPAGE ="rfx_page=";
	public final static String  PARAMETER_RFXPASSBACK ="&rfx_passback=";
	private static final String CLS_NAME = "CLS=[BreadcrumbDroplet]/MSG::";
	
	def setup(){
		testObj = new BreadcrumbDroplet(productManager:productManagerMock,bbbCatalogTools:bbbCatalogToolsMock,catalogRepository:catalogRepositoryMock)
	}
	
	/////////////////////////////////TCs for service starts////////////////////////////////////////
	//Signature : public void service(final DynamoHttpServletRequest request,final DynamoHttpServletResponse response) ////
	
	def"service method. This TC is the happy flow of service method"(){
		given:
			String categoryId = "cat12345"
			String productId = "prod12345"
			String poc = "poc"
			String siteId = "BedBathUS"
			String forOmniture = "false"
			String rfx_page = "rfx"
			String rfx_passback = "passback"
			
			this.populateReqParameters(requestMock, categoryId, productId, poc, siteId, forOmniture, rfx_page, rfx_passback)
			
			1 * catalogRepositoryMock.getItem("cat12345", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> FALSE
			CategoryVO categoryVOMock = new CategoryVO(phantomCategory:TRUE,isCollege:null)
			CategoryVO categoryVOMock1 = new CategoryVO(phantomCategory:TRUE,isCollege:FALSE)
			CategoryVO categoryVOMock2 = new CategoryVO(phantomCategory:TRUE)
			CategoryVO categoryVOMock3 = new CategoryVO(isCollege:TRUE)
			1 * productManagerMock.getParentCategory("cat12345","BedBathUS") >> ["1":categoryVOMock,"2":categoryVOMock1,"3":categoryVOMock2,"4":categoryVOMock3]
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(OPARAM_CIRCULAR, "rfx_page=rfx&rfx_passback=passback")
			1 * requestMock.setParameter("bts", TRUE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", null)
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, ["1":categoryVOMock,"2":categoryVOMock1,"4":categoryVOMock3])
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
	}

	def"service method. This TC is when rfx_page is null"(){
		given:
			String categoryId = "cat12345"; String productId = "prod12345"; String poc = "poc"; String siteId = "BedBathUS"
			String forOmniture = "false"; String rfx_page = null ; String rfx_passback = "passback"
			
			this.populateReqParameters(requestMock, categoryId, productId, poc, siteId, forOmniture, rfx_page, rfx_passback)
			
			1 * catalogRepositoryMock.getItem("cat12345", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> TRUE
			CategoryVO categoryVOMock = new CategoryVO(phantomCategory:FALSE)
			CategoryVO categoryVOMock1 = new CategoryVO(isCollege:FALSE)
			1 * productManagerMock.getParentCategoryForProduct("prod12345", "BedBathUS") >> ["1":categoryVOMock,"2":categoryVOMock1]
			
			1 * requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			0 * requestMock.setParameter(OPARAM_CIRCULAR, "rfx_page=null&rfx_passback=passback")
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", null)
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, ["1":categoryVOMock,"2":categoryVOMock1])
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
			
	}
	
	def"service method. This TC is when repsoitoryItem returns null and BBBBusinessException is thrown"(){
		given:
			testObj = Spy()
			testObj.setCatalogRepository(catalogRepositoryMock)
			String categoryId = "cat12345"; String productId = "prod12345"; String poc = "poc"
			String siteId = "BedBathUS"; String forOmniture = "false"; String rfx_page = "rfx"; String rfx_passback = "passback"
			
			this.populateReqParameters(requestMock, categoryId, productId, poc, siteId, forOmniture, rfx_page, rfx_passback)
			
			1 * catalogRepositoryMock.getItem("cat12345", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> null
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.serviceParameter(OPARAM_ERROR, requestMock,responseMock)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
			1 * requestMock.setParameter('isPrimaryCat', false)
			1 * requestMock.setParameter('primaryCategory', null)
			1 * requestMock.setParameter('isOrphanProduct', false)
			1 * requestMock.setParameter('bts', false)
			1 * requestMock.setParameter('breadCrumb', null)
			1 * testObj.logError('browse_1022: Business Exception from service of BreadcrumbDroplet for productId=prod12345 |SiteId=BedBathUS:1001:1001')
			1 * testObj.logDebug('Site ID = BedBathUS')
			1 * testObj.logDebug('Entering BreadcrumbDroplet.service()')
			
	}
	
	def"service method. This TC is when rfx_passback is null"(){
		given:
			String categoryId = "cat12345"; String productId = "prod12345"; String poc = "poc"; String siteId = "BedBathUS"
			String forOmniture = "false"; String rfx_page = "rfx" ; String rfx_passback = null
			
			this.populateReqParameters(requestMock, categoryId, productId, poc, siteId, forOmniture, rfx_page, rfx_passback)
			
			1 * catalogRepositoryMock.getItem("cat12345", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> null
			CategoryVO categoryVOMock = new CategoryVO(phantomCategory:null)
			CategoryVO categoryVOMock1 = new CategoryVO(isCollege:FALSE)
			1 * productManagerMock.getParentCategoryForProduct("prod12345", "BedBathUS") >> ["1":categoryVOMock,"2":categoryVOMock1]
			
			1 * requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", null)
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, ["1":categoryVOMock,"2":categoryVOMock1])
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
	}
	
	def"service method. This TC is when categoryId is empty and categoryVO is null"(){
		given:
			String categoryId = ""; String productId = "prod12345"; String poc = "poc"; String siteId = "BedBathUS"
			String forOmniture = null; String rfx_page = null ; String rfx_passback = "passback"
			
			this.populateReqParameters(requestMock, categoryId, productId, poc, siteId, forOmniture, rfx_page, rfx_passback)
			
			productManagerMock.getBbbCatalogTools() >> bbbCatalogToolsMock
			bbbCatalogToolsMock.getPrimaryCategory("prod12345") >> "cat12345"
			1 * catalogRepositoryMock.getItem("cat12345", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> FALSE
			CategoryVO categoryVOMock1 = new CategoryVO(isCollege:FALSE)
			1 * productManagerMock.getParentCategory("cat12345",siteId) >> ["1":null,"2":categoryVOMock1]
			
			1 * requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileApp"
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			thrown NullPointerException
	}
	
	def"service method. This TC is when categoryId is empty and everLivingProduct is true"(){
		given:
			String categoryId = ""; String productId = "prod12345"; String poc = "poc"; String siteId = "BedBathUS"
			String forOmniture = "true"; String rfx_page = null ; String rfx_passback = "passback"
			
			this.populateReqParameters(requestMock, categoryId, productId, poc, siteId, forOmniture, rfx_page, rfx_passback)
			
			productManagerMock.getBbbCatalogTools() >> bbbCatalogToolsMock
			bbbCatalogToolsMock.getPrimaryCategory("prod12345") >> ""
			
			1 * productManagerMock.getProductStatus(siteId, productId) >> TRUE
			
			1 * catalogRepositoryMock.getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
			RepositoryItem repositoryItemMock1 = Mock()
			RepositoryItem repositoryItemMock2 = Mock()
			Set<RepositoryItem> parentCategorySet = new HashSet<RepositoryItem>()
			parentCategorySet.add(repositoryItemMock1)
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME) >> parentCategorySet
			1 * bbbCatalogToolsMock.getCategoryForSite(parentCategorySet, siteId) >> repositoryItemMock2
			1 * productManagerMock.getParentCategoryForProduct(productId, siteId) >> null
			
			1 * requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", '')
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, null)
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
	}
	
	def"service method. This TC is when categoryId is empty and categoryRepositoryItem is null"(){
		given:
			String categoryId = ""; String productId = "prod12345"; String poc = "poc"; String siteId = "BedBathUS"
			String forOmniture = "true"; String rfx_page = null ; String rfx_passback = "passback"
			
			this.populateReqParameters(requestMock, categoryId, productId, poc, siteId, forOmniture, rfx_page, rfx_passback)
			
			productManagerMock.getBbbCatalogTools() >> bbbCatalogToolsMock
			bbbCatalogToolsMock.getPrimaryCategory("prod12345") >> ""
			
			1 * productManagerMock.getProductStatus(siteId, productId) >> TRUE
			
			1 * catalogRepositoryMock.getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> repositoryItemMock
			RepositoryItem repositoryItemMock1 = Mock()
			Set<RepositoryItem> parentCategorySet = new HashSet<RepositoryItem>()
			parentCategorySet.add(repositoryItemMock1)
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME) >> parentCategorySet
			1 * bbbCatalogToolsMock.getCategoryForSite(parentCategorySet, siteId) >> null
			
			1 * requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", '')
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, null)
			1 * requestMock.setParameter(ISORPHANPRODUCT, TRUE)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
	}
	
	def"service method. This TC is when categoryId is empty and breadCrumb is empty"(){
		given:
		
			String categoryId = ""; String productId = "prod12345"; String poc = "poc"; String siteId = "BedBathUS"
			String forOmniture = null; String rfx_page = null ; String rfx_passback = "passback"
			
			this.populateReqParameters(requestMock, categoryId, productId, poc, siteId, forOmniture, rfx_page, rfx_passback)
			
			productManagerMock.getBbbCatalogTools() >> bbbCatalogToolsMock
			bbbCatalogToolsMock.getPrimaryCategory("prod12345") >> "cat12345"
			1 * catalogRepositoryMock.getItem("cat12345", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> null
			1 * productManagerMock.getProductStatus(siteId, productId) >> FALSE
			1 * productManagerMock.getParentCategoryForProduct(productId, siteId) >> null
			1 * productManagerMock.getParentCategoryForProduct(poc, siteId) >> [:]

		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", 'cat12345')
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, [:])
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
	}
	
	def"service method. This TC is when categoryId is empty and when breadCrumb is null"(){
		given:
		
			String categoryId = ""; String productId = "prod12345"; String poc = ""; String siteId = "BedBathUS"
			String forOmniture = null; String rfx_page = null ; String rfx_passback = "passback"
			
			this.populateReqParameters(requestMock, categoryId, productId, poc, siteId, forOmniture, rfx_page, rfx_passback)
			
			productManagerMock.getBbbCatalogTools() >> bbbCatalogToolsMock
			bbbCatalogToolsMock.getPrimaryCategory("prod12345") >> "cat12345"
			1 * catalogRepositoryMock.getItem("cat12345", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> null
			1 * productManagerMock.getProductStatus(siteId, productId) >> FALSE
			1 * productManagerMock.getParentCategoryForProduct(productId, siteId) >> null

		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", 'cat12345')
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, null)
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
	}
	
	def"service method. This TC is when categoryId is empty and when BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setProductManager(productManagerMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			String categoryId = ""; String productId = "prod12345"; String poc = ""; String siteId = "BedBathUS"
			String forOmniture = null; String rfx_page = null ; String rfx_passback = "passback"
			
			this.populateReqParameters(requestMock, categoryId, productId, poc, siteId, forOmniture, rfx_page, rfx_passback)
			
			productManagerMock.getBbbCatalogTools() >> bbbCatalogToolsMock
			bbbCatalogToolsMock.getPrimaryCategory("prod12345") >> "cat12345"
			1 * catalogRepositoryMock.getItem("cat12345", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> null
			1 * productManagerMock.getProductStatus(siteId, productId) >> FALSE
			1 * productManagerMock.getParentCategoryForProduct(productId, siteId) >> {throw new BBBSystemException("Mock for BBBSystemException")}

		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", 'cat12345')
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, null)
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
			1 * requestMock.serviceParameter(OPARAM_ERROR, requestMock, responseMock)
			1 * testObj.logDebug('Site ID = BedBathUS')
			1 * testObj.logDebug('Entering BreadcrumbDroplet.service()')
			1 * testObj.logError('browse_1023: System Exception from service of BreadcrumbDroplet for productId=prod12345 |SiteId=BedBathUS:Mock for BBBSystemException', null)
	}
	
	def"service method. This TC is when categoryId is empty and when RepositoryException thrown"(){
		given:
			testObj = Spy()
			testObj.setProductManager(productManagerMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			String categoryId = ""; String productId = "prod12345"; String poc = ""; String siteId = "BedBathUS"
			String forOmniture = null; String rfx_page = null ; String rfx_passback = "passback"
			
			this.populateReqParameters(requestMock, categoryId, productId, poc, siteId, forOmniture, rfx_page, rfx_passback)
			
			productManagerMock.getBbbCatalogTools() >> bbbCatalogToolsMock
			bbbCatalogToolsMock.getPrimaryCategory("prod12345") >> "cat12345"
			1 * catalogRepositoryMock.getItem("cat12345", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock for RepositoryException")}

		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", 'cat12345')
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, null)
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
			1 * testObj.logDebug('Site ID = BedBathUS')
			1 * testObj.logError('Mock for RepositoryException', _)
			1 * testObj.logDebug('Entering BreadcrumbDroplet.service()')
	}
	
	def"service method. This TC is when categoryId is empty and productRepositoryItem is null"(){
		given:
			String categoryId = ""; String productId = "prod12345"; String poc = "poc"; String siteId = "BedBathUS"
			String forOmniture = "false"; String rfx_page = null ; String rfx_passback = "passback"
			
			this.populateReqParameters(requestMock, categoryId, productId, poc, siteId, forOmniture, rfx_page, rfx_passback)
			
			productManagerMock.getBbbCatalogTools() >> bbbCatalogToolsMock
			bbbCatalogToolsMock.getPrimaryCategory("prod12345") >> ""
			1 * productManagerMock.getProductStatus(siteId, productId) >> TRUE
			1 * catalogRepositoryMock.getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null
			1 * requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "MobileWeb"
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", '')
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, null)
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
	}
	
	def"service method. This TC is when categoryId is empty and"(){
		given:
		
			String categoryId = ""; String productId = "prod12345"; String poc = "poc"; String siteId = "BedBathUS"
			String forOmniture = null; String rfx_page = null ; String rfx_passback = "passback"
			
			this.populateReqParameters(requestMock, categoryId, productId, poc, siteId, forOmniture, rfx_page, rfx_passback)
			
			productManagerMock.getBbbCatalogTools() >> bbbCatalogToolsMock
			bbbCatalogToolsMock.getPrimaryCategory("prod12345") >> "cat12345"
			1 * catalogRepositoryMock.getItem("cat12345", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> null
			1 * productManagerMock.getProductStatus(siteId, productId) >> FALSE
			CategoryVO categoryVOMock1 = new CategoryVO(isCollege:FALSE)
			1 * productManagerMock.getParentCategoryForProduct(productId, siteId) >> ["1":categoryVOMock1]

		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", 'cat12345')
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, ["1":categoryVOMock1])
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
	}
	
	private populateReqParameters(atg.servlet.DynamoHttpServletRequest requestMock, String categoryId, String productId, String poc, String siteId, String forOmniture, String rfx_page, String rfx_passback) {
		 requestMock.getParameter(PARAMETER_CATEGORYID) >> categoryId
		 requestMock.getParameter(PARAMETER_PRODUCTID) >> productId
		 requestMock.getParameter(PARAMETER_POC) >> poc
		 requestMock.getParameter(PARAMETER_SITEID) >> siteId
		 requestMock.getParameter(PARAMETER_OMNITURE) >> forOmniture
		 requestMock.getQueryParameter("rfx_page") >> rfx_page
		 requestMock.getQueryParameter("rfx_passback") >> rfx_passback
	}
	
	////////////////////////////////////TCs for service ends////////////////////////////////////////
	
	/////////////////////////////////TCs for getProductBreadCrumb starts////////////////////////////////////////
	//Signature : public Map<String, CategoryVO> getProductBreadCrumb(String prodId, String catId, String forOmniture) ////
	
	def"getProductBreadCrumb. This TC is the Happy flow of getProductBreadCrumb"(){
		given:
			String catId = "cat12345" 
			String prodId = "prod12345" 
			String forOmniture = "false"
			CategoryVO categoryVOMock1 = new CategoryVO(isCollege:FALSE)
			1 * requestMock.getObjectParameter(OPARAM_BREADCRUMB) >> ["1":categoryVOMock1]
			
			//service method coverage
			1 * requestMock.getParameter(PARAMETER_CATEGORYID) >> catId
			1 * requestMock.getParameter(PARAMETER_SITEID) >> "BedBathUS"
			1 * requestMock.getParameter(PARAMETER_PRODUCTID) >> prodId
			1 * catalogRepositoryMock.getItem(catId, BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> TRUE
			1 * productManagerMock.getParentCategoryForProduct("prod12345", "BedBathUS") >> null
			
		when:
			Map<String, CategoryVO> results = testObj.getProductBreadCrumb(prodId, catId, forOmniture)
			
		then:
			results == ["1":categoryVOMock1]
			1 * requestMock.setParameter(PARAMETER_CATEGORYID, catId)
			1 * requestMock.setParameter(PARAMETER_PRODUCTID, prodId)
			1 * requestMock.setParameter(PARAMETER_OMNITURE, forOmniture)
			1 * requestMock.setParameter(PARAMETER_SITEID,_)
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", null)
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, null)
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
			
	}
	
	def"getProductBreadCrumb. This TC is when ServletException thrown in service method"(){
		given:
			testObj = Spy()
			testObj.setProductManager(productManagerMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			String catId = "cat12345"
			String prodId = null
			String forOmniture = "false"
			testObj.service(requestMock, responseMock) >>  {throw new ServletException("Mock for ServletException")}

		when:
			Map<String, CategoryVO> results = testObj.getProductBreadCrumb(prodId, catId, forOmniture)
			
		then:
			thrown BBBSystemException
			results == null
			1 * testObj.logError('Method: BreadcrumbDroplet.getProductBreadCrumb, Servlet Exception while getting product bread crumb javax.servlet.ServletException: Mock for ServletException')
			1 * requestMock.setParameter(PARAMETER_CATEGORYID, catId)
			1 * requestMock.setParameter(PARAMETER_PRODUCTID, prodId)
			1 * requestMock.setParameter(PARAMETER_OMNITURE, forOmniture)
			1 * requestMock.setParameter(PARAMETER_SITEID,_)
	}
	
	def"getProductBreadCrumb. This TC is when IOException thrown in service method"(){
		given:
			testObj = Spy()
			testObj.setProductManager(productManagerMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setBbbCatalogTools(bbbCatalogToolsMock)
			String catId = "cat12345"
			String prodId = null
			String forOmniture = "false"
			testObj.service(requestMock, responseMock) >> {throw new IOException("Mock for IOException")} 
			
		when:
			Map<String, CategoryVO> results = testObj.getProductBreadCrumb(prodId, catId, forOmniture)
			
		then:
			thrown BBBSystemException
			results == null
			1 * testObj.logError('Method: BreadcrumbDroplet.getProductBreadCrumb, IO Exception while getting product bread crumb java.io.IOException: Mock for IOException')
			1 * requestMock.setParameter(PARAMETER_CATEGORYID, catId)
			1 * requestMock.setParameter(PARAMETER_PRODUCTID, prodId)
			1 * requestMock.setParameter(PARAMETER_OMNITURE, forOmniture)
			1 * requestMock.setParameter(PARAMETER_SITEID,_)
	}
	
	def"getProductBreadCrumb. This TC is when catId and prodId is null"(){
		given:
			String catId = null
			String prodId = null
			String forOmniture = "false"
			
		when:
			Map<String, CategoryVO> results = testObj.getProductBreadCrumb(prodId, catId, forOmniture)
			
		then:
			results == null
			thrown BBBBusinessException
	}
	
	////////////////////////////////////TCs for getProductBreadCrumb ends////////////////////////////////////////
	
	/////////////////////////////////TCs for getBreadcrumbDetails starts////////////////////////////////////////
	//Signature : public Map<String, CategoryVO> getBreadcrumbDetails(Map <String,String> inputParam) ////
	
	def"getBreadcrumbDetails. This TC is the Happy flow of getBreadcrumbDetails"(){
		given:
			Map <String,String> inputParam = ["categoryId":"cat12345","productId":"prod12345","siteId":"BedBathUS","rfx_page":"rfx","rfx_passback":"passback"]
			Vector enumValues = new Vector()
			enumValues.add("paramName1")
			enumValues.add("paramName2")
			Enumeration<String> paramsEnum = enumValues.elements()
			1 * requestMock.getParameterNames() >> paramsEnum
			1 * requestMock.getParameter("paramName1") >> "paramvalue1"
			1 * requestMock.getParameter("paramName2") >> "paramvalue2"
			1 * requestMock.getParameter("errorMsg") >> null
			CategoryVO categoryVOMock1 = new CategoryVO(isCollege:FALSE)
			1 * requestMock.getObjectParameter("breadCrumb") >>  ["1":categoryVOMock1]
			
			//service method coverage
			1 * requestMock.getParameter(PARAMETER_CATEGORYID) >> "cat12345"
			1 * requestMock.getParameter(PARAMETER_SITEID) >> "BedBathUS"
			1 * requestMock.getParameter(PARAMETER_PRODUCTID) >> "prod12345"
			1 * catalogRepositoryMock.getItem("cat12345", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> TRUE
			1 * productManagerMock.getParentCategoryForProduct("prod12345", "BedBathUS") >> null
			
		when:
			Map<String, CategoryVO> results = testObj.getBreadcrumbDetails(inputParam)
			
		then:
			results == ["1":categoryVOMock1]
			1 * requestMock.setParameter(PARAMETER_CATEGORYID,"cat12345")
			1 * requestMock.setParameter(PARAMETER_PRODUCTID,"prod12345")
			1 * requestMock.setParameter(PARAMETER_SITEID,"BedBathUS")
			1 * requestMock.setParameter("rfx_page","rfx")
			1 * requestMock.setParameter("rfx_passback","passback")
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", null)
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, null)
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
			
	}
	
	def"getBreadcrumbDetails. This TC is when inputParam is empty"(){
		given:
			
		when:
			Map<String, CategoryVO> results = testObj.getBreadcrumbDetails([:])
			
		then:
			results == null
			thrown BBBSystemException			
	}
	
	def"getBreadcrumbDetails. This TC is when inputParam is null"(){
		given:
			
		when:
			Map<String, CategoryVO> results = testObj.getBreadcrumbDetails(null)
			
		then:
			results == null
			thrown BBBSystemException
	}
	
	def"getBreadcrumbDetails. This TC is when error is not null"(){
		given:
			testObj = Spy()
			testObj.setProductManager(productManagerMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			Map <String,String> inputParam = ["categoryId":"cat12345","productId":"prod12345","siteId":"BedBathUS","rfx_page":"rfx","rfx_passback":"passback"]
			Vector enumValues = new Vector()
			enumValues.add("paramName1")
			Enumeration<String> paramsEnum = enumValues.elements()
			1 * requestMock.getParameterNames() >> paramsEnum
			1 * requestMock.getParameter("paramName1") >> "paramvalue1"
			1 * requestMock.getParameter("errorMsg") >> "ErrorMsg"
			
			testObj.service(requestMock, responseMock) >> null 
			
		when:
			Map<String, CategoryVO> results = testObj.getBreadcrumbDetails(inputParam)
			
		then:
			results == null
			thrown BBBBusinessException
			1 * testObj.logError('Certona Serivce throws Business ExceptionErrorMsg', null)
			1 * testObj.logDebug('CLS=[BreadcrumbDroplet]/MSG:: getBreadcrumbDetails method ends')
			1 * testObj.logDebug('Param Name = paramName1param value =paramvalue1')
			1 * testObj.logDebug('CLS=[BreadcrumbDroplet]/MSG::entering getBreadcrumbDetails method')
			1 * requestMock.setParameter(PARAMETER_CATEGORYID,"cat12345")
			1 * requestMock.setParameter(PARAMETER_PRODUCTID,"prod12345")
			1 * requestMock.setParameter(PARAMETER_SITEID,"BedBathUS")
			1 * requestMock.setParameter("rfx_page","rfx")
			1 * requestMock.setParameter("rfx_passback","passback")
	}
	
	def"getBreadcrumbDetails. This TC is when breadCrumb is null"(){
		given:
			Map <String,String> inputParam = ["categoryId":"cat12345","productId":"prod12345","siteId":"BedBathUS","rfx_page":"rfx","rfx_passback":"passback"]
			Vector enumValues = new Vector()
			enumValues.add("paramName1")
			Enumeration<String> paramsEnum = enumValues.elements()
			1 * requestMock.getParameterNames() >> paramsEnum
			1 * requestMock.getParameter("paramName1") >> "paramvalue1"
			1 * requestMock.getParameter("errorMsg") >> null
			1 * requestMock.getObjectParameter("breadCrumb") >> null
			
			//service method coverage
			1 * requestMock.getParameter(PARAMETER_CATEGORYID) >> "cat12345"
			1 * requestMock.getParameter(PARAMETER_SITEID) >> "BedBathUS"
			1 * requestMock.getParameter(PARAMETER_PRODUCTID) >> "prod12345"
			1 * catalogRepositoryMock.getItem("cat12345", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> TRUE
			1 * productManagerMock.getParentCategoryForProduct("prod12345", "BedBathUS") >> null
			
		when:
			Map<String, CategoryVO> results = testObj.getBreadcrumbDetails(inputParam)
			
		then:
			results == null
			thrown BBBBusinessException
			1 * requestMock.setParameter(PARAMETER_CATEGORYID,"cat12345")
			1 * requestMock.setParameter(PARAMETER_PRODUCTID,"prod12345")
			1 * requestMock.setParameter(PARAMETER_SITEID,"BedBathUS")
			1 * requestMock.setParameter("rfx_page","rfx")
			1 * requestMock.setParameter("rfx_passback","passback")
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", null)
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, null)
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock)
			
	}
	
	def"getBreadcrumbDetails. This TC is when ServletException thrown in service method"(){
		given:
			Map <String,String> inputParam = ["categoryId":"cat12345","productId":"prod12345","siteId":"BedBathUS","rfx_page":"rfx","rfx_passback":"passback"]
			Vector enumValues = new Vector()
			enumValues.add("paramName1")
			Enumeration<String> paramsEnum = enumValues.elements()
			1 * requestMock.getParameterNames() >> paramsEnum
			1 * requestMock.getParameter("paramName1") >> "paramvalue1"
			
			//service method coverage
			1 * requestMock.getParameter(PARAMETER_CATEGORYID) >> "cat12345"
			1 * requestMock.getParameter(PARAMETER_SITEID) >> "BedBathUS"
			1 * requestMock.getParameter(PARAMETER_PRODUCTID) >> "prod12345"
			1 * catalogRepositoryMock.getItem("cat12345", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> TRUE
			1 * productManagerMock.getParentCategoryForProduct("prod12345", "BedBathUS") >> null
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock) >> {throw new ServletException("Mock for ServletException")}
			
		when:
			Map<String, CategoryVO> results = testObj.getBreadcrumbDetails(inputParam)
			
		then:
			thrown BBBSystemException
			results == null
			1 * requestMock.setParameter(PARAMETER_CATEGORYID,"cat12345")
			1 * requestMock.setParameter(PARAMETER_PRODUCTID,"prod12345")
			1 * requestMock.setParameter(PARAMETER_SITEID,"BedBathUS")
			1 * requestMock.setParameter("rfx_page","rfx")
			1 * requestMock.setParameter("rfx_passback","passback")
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", null)
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, null)
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			
	}
	
	def"getBreadcrumbDetails. This TC is when IOException thrown in service method"(){
		given:
			Map <String,String> inputParam = ["categoryId":"cat12345","productId":"prod12345","siteId":"BedBathUS","rfx_page":"rfx","rfx_passback":"passback"]
			Vector enumValues = new Vector()
			enumValues.add("paramName1")
			Enumeration<String> paramsEnum = enumValues.elements()
			1 * requestMock.getParameterNames() >> paramsEnum
			1 * requestMock.getParameter("paramName1") >> "paramvalue1"
			
			//service method coverage
			1 * requestMock.getParameter(PARAMETER_CATEGORYID) >> "cat12345"
			1 * requestMock.getParameter(PARAMETER_SITEID) >> "BedBathUS"
			1 * requestMock.getParameter(PARAMETER_PRODUCTID) >> "prod12345"
			1 * catalogRepositoryMock.getItem("cat12345", BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR) >> repositoryItemMock
			1 * repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_CATEGORY_PROPERTY_NAME) >> TRUE
			1 * productManagerMock.getParentCategoryForProduct("prod12345", "BedBathUS") >> null
			1 * requestMock.serviceParameter(OPARAM_OUTPUT, requestMock,responseMock) >> {throw new IOException("Mock for IOException")}
			
		when:
			Map<String, CategoryVO> results = testObj.getBreadcrumbDetails(inputParam)
			
		then:
			thrown BBBSystemException
			results == null
			1 * requestMock.setParameter(PARAMETER_CATEGORYID,"cat12345")
			1 * requestMock.setParameter(PARAMETER_PRODUCTID,"prod12345")
			1 * requestMock.setParameter(PARAMETER_SITEID,"BedBathUS")
			1 * requestMock.setParameter("rfx_page","rfx")
			1 * requestMock.setParameter("rfx_passback","passback")
			1 * requestMock.setParameter("bts", FALSE)
			1 * requestMock.setParameter("isPrimaryCat", FALSE)
			1 * requestMock.setParameter("primaryCategory", null)
			1 * requestMock.setParameter(OPARAM_BREADCRUMB, null)
			1 * requestMock.setParameter(ISORPHANPRODUCT, FALSE)
			
	}
	
	
	////////////////////////////////////TCs for getBreadcrumbDetails ends////////////////////////////////////////
	
}
