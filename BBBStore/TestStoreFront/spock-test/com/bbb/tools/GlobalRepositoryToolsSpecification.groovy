package com.bbb.tools

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.logger.PerformanceLogger;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.PreviewAttributes
import com.bbb.commerce.catalog.vo.EcoFeeSKUVO;
import com.bbb.commerce.catalog.vo.StoreSpecialityVO;

import java.util.List;

import javax.servlet.http.HttpSession;

import atg.repository.MutableRepository;
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.servlet.DynamoHttpServletRequest;
import spock.lang.Specification;

import com.bbb.profile.session.BBBSessionBean;
import com.bbb.repository.RepositoryItemMock;

import atg.servlet.ServletUtil
/**
 * 
 * @author kmagud
 *
 */
class GlobalRepositoryToolsSpecification extends Specification {
	
	GlobalRepositoryTools testObj
	DynamoHttpServletRequest requestMock = Mock()
	PerformanceLogger perfMock = Mock()
	BBBSessionBean sessionBeanMock = new BBBSessionBean()
	PreviewAttributes PreviewAttributesMock = Mock()
	MutableRepository catalogRepositoryMock = Mock()
	RepositoryItemMock repositoryItemMock = Mock()
	RepositoryItemMock ecoFeeRepositoryMock = Mock()
	RepositoryItemMock ecoFeeRepositoryItemMock = Mock()
	RepositoryItemMock relationRepositoryItemMock = Mock()
	RepositoryItemMock skuEcoRepositoryItemMock = Mock()
	RepositoryItemMock catalogRepositoryItemMock = Mock()
	RepositoryItemMock parentListRepositoryItemMock = Mock()
	
	
	def setup() {
		
		testObj = new GlobalRepositoryTools(customizationOfferedSiteMap:["BedBathUS":"customizationOfferedFlag",
                 "TBS_BedBathUS":"customizationOfferedFlag",
                 "TBS_BuyBuyBaby":"customizationOfferedFlag",
                 "TBS_BedBathCanada":"customizationOfferedFlag",
                "BuyBuyBaby":"babCustomizationOfferedFlag",
                "BedBathCanada":"caCustomizationOfferedFlag",
                "GS_BedBathUS":"gsCustomizationOfferedFlag",
                "GS_BuyBuyBaby":"gsCustomizationOfferedFlag",
                "GS_BedBathCanada":"gsCustomizationOfferedFlag",
                "TBS_BedBathUS":"customizationOfferedFlag",
                "TBS_BuyBuyBaby":"babCustomizationOfferedFlag",
                "TBS_BedBathCanada":"caCustomizationOfferedFlag"])
		
		ServletUtil.setCurrentRequest(requestMock)
		requestMock.getContextPath() >> "/store"
		requestMock.resolveName("/com/bbb/framework/performance/logger/PerformanceLogger") >> perfMock
		perfMock.isEnableCustomComponentsMonitoring() >> false
		
	}
	
	/* returnCountryFromSession - Test Cases START */
	
	def "returnCountryFromSession when return country from session" () {
		given:
			testObj = Spy()
			ServletUtil.setCurrentRequest(requestMock)
			requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBeanMock
			sessionBeanMock.getValues().put("defaultUserCountryCode","US")
			
		when:
			def results = testObj.returnCountryFromSession()
		then:
			results == "US"
	}
	
	def "returnCountryFromSession when sessionBeanFromReq returns value and country is null in sessionBean" () {
		given:
			testObj = Spy()
			ServletUtil.setCurrentRequest(requestMock)
			requestMock.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBeanMock
			sessionBeanMock.getValues().put("defaultUserCountryCode", null)
			
		when:
			def results = testObj.returnCountryFromSession()
		then:
			results == null
	}
	
	def "returnCountryFromSession when request is null" () {
		given:
			testObj = Spy()
			ServletUtil.setCurrentRequest(null)
			
		when:
			def results = testObj.returnCountryFromSession()
			
		then:
			results == null
	}
	
	/* returnCountryFromSession - Test Cases ENDS */
	
	/* getEcoFeeSKUDetailForState - Test Cases START */
	
	def "getEcoFeeSKUDetailForState when passing parameters and isActive is false" () {
	 given:
	 
		 testObj = Spy()
		 
		 testObj.setCatalogRepository(catalogRepositoryMock)
		 
		 catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
		 repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
		 repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
		 repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
		 repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
		 
		 
		 List<String> valueList = new ArrayList<String>()
		 valueList.add("somevalue")
		 	
		 testObj.getAllValuesForKey(*_) >> valueList
		 
	 when:
		 def EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
	 then:
	 	results == null
		thrown BBBBusinessException
	}
	
	def "getEcoFeeSKUDetailForState when Repository Exception occurs" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock Repository Exception")}
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
		then:
			results == null
		   thrown BBBSystemException
	   }
	
	def "getEcoFeeSKUDetailForState when getAllValuesForKey value is empty" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> false
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
			List<String> valueList = new ArrayList<String>()
			testObj.getAllValuesForKey(*_) >> valueList
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
			
		then:
			results == null
			thrown BBBBusinessException
	   }
	
	def "getEcoFeeSKUDetailForState when RepoStateId not equal to stateId" () {
		given:
		
			testObj = Spy()
			
			ServletUtil.setCurrentRequest(requestMock)
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			
			def startDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 1, 23, 45).time
			Date endDate = new Date()
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> startDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
			
			Set<RepositoryItem> ecoFeeSKURelationList = new HashSet<RepositoryItem>()
			ecoFeeSKURelationList.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME) >> ecoFeeSKURelationList 
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME) >> repositoryItemMock
			
			repositoryItemMock.getRepositoryId() >> "32256" 
			
			List<String> valueList = new ArrayList<String>()
			valueList.add("true")
			testObj.getAllValuesForKey(*_) >> valueList
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
		then:
			results == null
			thrown BBBBusinessException
		
	}
	
	def "getEcoFeeSKUDetailForState when passing disabled false" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			
			def startDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 1, 23, 45).time
			Date endDate = new Date()
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> startDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
			
			Set<RepositoryItem> ecoFeeSKURelationList = new HashSet<RepositoryItem>()
			ecoFeeSKURelationList.add(ecoFeeRepositoryMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME) >> ecoFeeSKURelationList
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME) >> ecoFeeRepositoryItemMock
			ecoFeeRepositoryItemMock.getRepositoryId() >> "NJ"
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.SKU_ECO_FEE_PROPERTY_NAME) >> relationRepositoryItemMock
			relationRepositoryItemMock.getRepositoryId() >> "2322523"
			
			catalogRepositoryMock.getItem("2322523",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> catalogRepositoryItemMock
			
			Set<RepositoryItem> parentProductList = new HashSet<RepositoryItem>()
			parentProductList.add(parentListRepositoryItemMock)
			catalogRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> parentProductList
			parentListRepositoryItemMock.getRepositoryId() >> "p5223322"
					
			List<String> valueList = new ArrayList<String>()
			valueList.add("true")
			testObj.getAllValuesForKey(*_) >> valueList
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
			
		then:
			results.ecoFeeSKUId == "2322523"
			results.ecoFeeProductId == "p5223322"
		
	}
	
	def "getEcoFeeSKUDetailForState when ecoFeeSKURelationList item is empty " () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			
			def startDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 1, 23, 45).time
			Date endDate = new Date()
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> startDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
			
			Set<RepositoryItem> ecoFeeSKURelationList = new HashSet<RepositoryItem>()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME) >> ecoFeeSKURelationList
					
			List<String> valueList = new ArrayList<String>()
			valueList.add("true")
			testObj.getAllValuesForKey(*_) >> valueList
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
			
		then:
			results == null
			thrown BBBBusinessException
		
	}
	
	def "getEcoFeeSKUDetailForState when passing PreviewEnabled value" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			testObj.setPreviewEnabled(true)
			
			
			def startDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 1, 23, 45).time
			Date endDate = new Date()
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> startDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> endDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> null
			
			Set<RepositoryItem> ecoFeeSKURelationList = new HashSet<RepositoryItem>()
			ecoFeeSKURelationList.add(ecoFeeRepositoryMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME) >> ecoFeeSKURelationList
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME) >> ecoFeeRepositoryItemMock
			ecoFeeRepositoryItemMock.getRepositoryId() >> "NJ"
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.SKU_ECO_FEE_PROPERTY_NAME) >> relationRepositoryItemMock
			relationRepositoryItemMock.getRepositoryId() >> "2322523"
			
			catalogRepositoryMock.getItem("2322523",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> catalogRepositoryItemMock
			
			Set<RepositoryItem> parentProductList = new HashSet<RepositoryItem>()
			parentProductList.add(parentListRepositoryItemMock)
			catalogRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> parentProductList
			parentListRepositoryItemMock.getRepositoryId() >> "p5223322"
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
			
		then:
			results == null
			thrown BBBBusinessException
			
		
	}
	
	def "getEcoFeeSKUDetailForState when passing getAllValuesForKey value is null" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			
			def startDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 1, 23, 45).time
			Date endDate = new Date()
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> startDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> endDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> false
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
			
			Set<RepositoryItem> ecoFeeSKURelationList = new HashSet<RepositoryItem>()
			ecoFeeSKURelationList.add(ecoFeeRepositoryMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME) >> ecoFeeSKURelationList
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME) >> ecoFeeRepositoryItemMock
			ecoFeeRepositoryItemMock.getRepositoryId() >> "NJ"
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.SKU_ECO_FEE_PROPERTY_NAME) >> relationRepositoryItemMock
			relationRepositoryItemMock.getRepositoryId() >> "2322523"
			
			catalogRepositoryMock.getItem("2322523",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> catalogRepositoryItemMock
			
			Set<RepositoryItem> parentProductList = new HashSet<RepositoryItem>()
			parentProductList.add(parentListRepositoryItemMock)
			catalogRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> parentProductList
			parentListRepositoryItemMock.getRepositoryId() >> "p5223322"
			
			List<String> valueList = new ArrayList<String>()
			valueList.add(null)
			testObj.getAllValuesForKey(*_) >> null
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
			
		then:
			results == null
			thrown BBBBusinessException
			
		
	}
	
	def "getEcoFeeSKUDetailForState when passing getAllValuesForKey value is empty" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			
			def startDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 1, 23, 45).time
			def endDate = new GregorianCalendar(2016, Calendar.AUGUST, 28, 1, 23, 45).time
			
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> startDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> endDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> false
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
			
			Set<RepositoryItem> ecoFeeSKURelationList = new HashSet<RepositoryItem>()
			ecoFeeSKURelationList.add(ecoFeeRepositoryMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME) >> ecoFeeSKURelationList
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME) >> ecoFeeRepositoryItemMock
			ecoFeeRepositoryItemMock.getRepositoryId() >> "NJ"
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.SKU_ECO_FEE_PROPERTY_NAME) >> relationRepositoryItemMock
			relationRepositoryItemMock.getRepositoryId() >> "2322523"
			
			catalogRepositoryMock.getItem("2322523",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> catalogRepositoryItemMock
			
			Set<RepositoryItem> parentProductList = new HashSet<RepositoryItem>()
			parentProductList.add(parentListRepositoryItemMock)
			catalogRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> parentProductList
			parentListRepositoryItemMock.getRepositoryId() >> "p5223322"
			
			List<String> valueList = new ArrayList<String>()
			valueList.add("")
			testObj.getAllValuesForKey(*_) >> valueList
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
			
		then:
			results == null
			thrown BBBBusinessException
			
		
	}
	
	def "getEcoFeeSKUDetailForState when passing stateId as null" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
			
			
			List<String> valueList = new ArrayList<String>()
			valueList.add("somevalue")
				
			testObj.getAllValuesForKey(*_) >> valueList
			
		when:
			EcoFeeSKUVO results = testObj.getEcoFeeSKUDetailForState(null, "12225")
		then:
			results == null
			thrown BBBBusinessException
	   }
	
	def "getEcoFeeSKUDetailForState when passing stateId as empty" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
			
			
			List<String> valueList = new ArrayList<String>()
			valueList.add("somevalue")
				
			testObj.getAllValuesForKey(*_) >> valueList
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("", "12225")
		then:
			results == null
			thrown BBBBusinessException
	   
		}
	
	def "getEcoFeeSKUDetailForState when passing skuId as null" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
			
			
			List<String> valueList = new ArrayList<String>()
			valueList.add("somevalue")
				
			testObj.getAllValuesForKey(*_) >> valueList
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", null)
		then:
			results == null
			thrown BBBBusinessException
	   }
	
	def "getEcoFeeSKUDetailForState when passing skuId as empty" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
			
			
			List<String> valueList = new ArrayList<String>()
			valueList.add("somevalue")
				
			testObj.getAllValuesForKey(*_) >> valueList
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "")
		then:
			results == null
			thrown BBBBusinessException
	   }
	
	def "getEcoFeeSKUDetailForState when repository item returns null" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
		then:
			results == null
			thrown BBBBusinessException
	   }
	
	
	def "getEcoFeeSKUDetailForState when passing ecoFeeSKU as Null" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			
			def startDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 1, 23, 45).time
			Date endDate = new Date()
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> startDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
			
			Set<RepositoryItem> ecoFeeSKURelationList = new HashSet<RepositoryItem>()
			ecoFeeSKURelationList.add(ecoFeeRepositoryMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME) >> ecoFeeSKURelationList
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME) >> ecoFeeRepositoryItemMock
			ecoFeeRepositoryItemMock.getRepositoryId() >> "NJ"
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.SKU_ECO_FEE_PROPERTY_NAME) >> null
			relationRepositoryItemMock.getRepositoryId() >> "2322523"
			
			catalogRepositoryMock.getItem("2322523",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> catalogRepositoryItemMock
			
			Set<RepositoryItem> parentProductList = new HashSet<RepositoryItem>()
			parentProductList.add(parentListRepositoryItemMock)
			catalogRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> parentProductList
			parentListRepositoryItemMock.getRepositoryId() >> "p5223322"
					
			List<String> valueList = new ArrayList<String>()
			valueList.add("true")
			testObj.getAllValuesForKey(*_) >> valueList
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
			
		then:
			results == null
			thrown BBBBusinessException
			
		
	}
	
	def "getEcoFeeSKUDetailForState when passing ecoFeeSKU id as Null" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			
			def startDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 1, 23, 45).time
			Date endDate = new Date()
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> startDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
			
			Set<RepositoryItem> ecoFeeSKURelationList = new HashSet<RepositoryItem>()
			ecoFeeSKURelationList.add(ecoFeeRepositoryMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME) >> ecoFeeSKURelationList
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME) >> ecoFeeRepositoryItemMock
			ecoFeeRepositoryItemMock.getRepositoryId() >> "NJ"
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.SKU_ECO_FEE_PROPERTY_NAME) >> relationRepositoryItemMock
			relationRepositoryItemMock.getRepositoryId() >> null
			
			catalogRepositoryMock.getItem("2322523",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> catalogRepositoryItemMock
			
			Set<RepositoryItem> parentProductList = new HashSet<RepositoryItem>()
			parentProductList.add(parentListRepositoryItemMock)
			catalogRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> parentProductList
			parentListRepositoryItemMock.getRepositoryId() >> "p5223322"
					
			List<String> valueList = new ArrayList<String>()
			valueList.add("true")
			testObj.getAllValuesForKey(*_) >> valueList
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
			
		then:
			results == null
			thrown BBBBusinessException
			
		
	}
	
	def "getEcoFeeSKUDetailForState when passing state property as Null" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			
			def startDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 1, 23, 45).time
			Date endDate = new Date()
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> startDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
			
			Set<RepositoryItem> ecoFeeSKURelationList = new HashSet<RepositoryItem>()
			ecoFeeSKURelationList.add(ecoFeeRepositoryMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME) >> ecoFeeSKURelationList
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME) >> null
			ecoFeeRepositoryItemMock.getRepositoryId() >> "NJ"
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.SKU_ECO_FEE_PROPERTY_NAME) >> relationRepositoryItemMock
			relationRepositoryItemMock.getRepositoryId() >> null
			
			catalogRepositoryMock.getItem("2322523",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> catalogRepositoryItemMock
			
			Set<RepositoryItem> parentProductList = new HashSet<RepositoryItem>()
			parentProductList.add(parentListRepositoryItemMock)
			catalogRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> parentProductList
			parentListRepositoryItemMock.getRepositoryId() >> "p5223322"
					
			List<String> valueList = new ArrayList<String>()
			valueList.add("true")
			testObj.getAllValuesForKey(*_) >> valueList
			
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
			
		then:
			results == null
			thrown BBBBusinessException
		
	}
	
	def "getEcoFeeSKUDetailForState when passing ecoFeeSKUs item as Null" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			
			def startDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 1, 23, 45).time
			Date endDate = new Date()
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> startDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
			
			Set<RepositoryItem> ecoFeeSKURelationList = new HashSet<RepositoryItem>()
			ecoFeeSKURelationList.add(ecoFeeRepositoryMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME) >> null
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME) >> relationRepositoryItemMock
			ecoFeeRepositoryItemMock.getRepositoryId() >> "NJ"
			
			ecoFeeRepositoryMock.getPropertyValue(BBBCatalogConstants.SKU_ECO_FEE_PROPERTY_NAME) >> relationRepositoryItemMock
			relationRepositoryItemMock.getRepositoryId() >> null
			
			catalogRepositoryMock.getItem("2322523",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> catalogRepositoryItemMock
			
			Set<RepositoryItem> parentProductList = new HashSet<RepositoryItem>()
			parentProductList.add(parentListRepositoryItemMock)
			catalogRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> parentProductList
			parentListRepositoryItemMock.getRepositoryId() >> "p5223322"
					
			List<String> valueList = new ArrayList<String>()
			valueList.add("true")
			testObj.getAllValuesForKey(*_) >> valueList
				
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
			
		then:
			results == null
			thrown BBBBusinessException
		
	}
	
	def "getEcoFeeSKUDetailForState when repository item returns two items" () {
		given:
		
			testObj = Spy()
			
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("12225", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			
			def startDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 1, 23, 45).time
			Date endDate = new Date()
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> startDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
			
			RepositoryItemMock item1 = new RepositoryItemMock()
			RepositoryItemMock item2 = new RepositoryItemMock()
			RepositoryItem[] repositoryItemList = [item1,item2]
			
			Set<RepositoryItem> ecoFeeSKURelationList = new HashSet<RepositoryItem>()
			ecoFeeSKURelationList.addAll([item1,item2])
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.ECO_FEE_SKU_RELN_PROPERTY_NAME) >> ecoFeeSKURelationList
			ecoFeeRepositoryItemMock.getRepositoryId() >> "NJ"
			relationRepositoryItemMock.getRepositoryId() >> "2322523"
			
			item1.setProperties(["state" : ecoFeeRepositoryItemMock , "ecoFeeSKU" : relationRepositoryItemMock])
			item2.setProperties(["state" : ecoFeeRepositoryItemMock , "ecoFeeSKU" : relationRepositoryItemMock])
			
			catalogRepositoryMock.getItem("2322523",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> catalogRepositoryItemMock
			
			Set<RepositoryItem> parentProductList = new HashSet<RepositoryItem>()
			parentProductList.add(parentListRepositoryItemMock)
			catalogRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> parentProductList
			parentListRepositoryItemMock.getRepositoryId() >> "p5223322"
					
			List<String> valueList = new ArrayList<String>()
			valueList.add("true")
			testObj.getAllValuesForKey(*_) >> valueList
				
		when:
			EcoFeeSKUVO  results = testObj.getEcoFeeSKUDetailForState("NJ", "12225")
			
		then:
			results == null
			thrown BBBBusinessException
	}
	
	/* getEcoFeeSKUDetailForState - Test Cases ENDS */
	
	/* isPreviewEnabled- Test Cases START */
	
	
	def "isPreviewEnabled when BBBSystemException occurs" () {
		given:
		
			testObj = Spy()
			List<String> valueList = new ArrayList<String>()
			testObj.getAllValuesForKey(*_) >> {throw new BBBSystemException("Mock BBBSystemException")}
			
		when:
			def results = testObj.isPreviewEnabled()
			 
		then:
			results != null
	}
	
	def "isPreviewEnabled when BBBBusinessException occurs" () {
		given:
		
			testObj = Spy()
			List<String> valueList = new ArrayList<String>()
			testObj.getAllValuesForKey(*_) >> {throw new BBBBusinessException("Mock BBBBusinessException")}
			
		when:
			def results = testObj.isPreviewEnabled()
			 
		then:
			results != null
					
	}
	
	/* isPreviewEnabled - Test Cases ENDS */
	
	/* isSkuActive - Test Cases START */
	
	def "isSkuActive when passing parameters" () {
		given:
		
			testObj = Spy()
			testObj.setPreviewEnabled(true)
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> "Y"
			
		when:
			def results = testObj.isSkuActive(repositoryItemMock, "true")
			
		then:
			results == false
	   }
	
	def "isSkuActive when given request is null" () {
		given:
		
			testObj = Spy()
			testObj.setPreviewEnabled(false)
			ServletUtil.setCurrentRequest(null)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> "Y"
			
		when:
			def results = testObj.isSkuActive(repositoryItemMock, "true")
			
		then:
			results == false
	   }
	
	def "isSkuActive when passing repository Itemm as null" () {
		given:
		
			testObj = Spy()
			testObj.setPreviewEnabled(true)
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> "Y"
			
		when:
			def results = testObj.isSkuActive(null, "true")
			
		then:
			results == false
	   }
	
	def "isSkuActive when request header is MobileApp" () {
		given:
		
			testObj = Spy()
			testObj.setPreviewEnabled(true)
			
			requestMock.getHeader(*_) >> "MobileApp"
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> false
			
		when:
			def results = testObj.isSkuActive(repositoryItemMock, "false")
			
		then:
			results == true
	   }
	
	def "isSkuActive when request header is MobileWeb" () {
		given:
		
			testObj = Spy()
			testObj.setPreviewEnabled(true)
			
			requestMock.getHeader(*_) >> "MobileWeb"
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> false
			
		when:
			def results = testObj.isSkuActive(repositoryItemMock, "false")
			
		then:
			results == false
	   }
	
	def "isSkuActive when passing intlRestricted as null" () {
		given:
		
			testObj = Spy()
			testObj.setPreviewEnabled(true)
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> null
				
		when:
			def results = testObj.isSkuActive(repositoryItemMock, "true")
			
		then:
			results == false
	   }
	
	def "isSkuActive when passing intlRestricted as N" () {
		given:
		
			testObj = Spy()
			testObj.setPreviewEnabled(true)
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED) >> "N"
				
		when:
			def results = testObj.isSkuActive(repositoryItemMock, "true")
			
		then:
			results == false
	   }
	
	def "isSkuActive when preview date before start date" () {
		given:
		
			testObj = Spy()
			testObj.setPreviewEnabled(true)
			
			requestMock.getHeader(*_) >> "MobileWeb"
			
			def startDate = new GregorianCalendar(2061, Calendar.AUGUST, 3, 1, 23, 45).time
			def endDate = new GregorianCalendar(2061, Calendar.AUGUST, 3, 1, 23, 45).time
			
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> startDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> endDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
			
		when:
			def results = testObj.isSkuActive(repositoryItemMock, "false")
			
		then:
			results == false
	   }
	
	def "isSkuActive when weboffered is false" () {
		given:
		
			testObj = Spy()
			testObj.setPreviewEnabled(true)
			
			requestMock.getHeader(*_) >> "MobileWeb"
			
			def startDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 1, 23, 45).time
			def endDate = new GregorianCalendar(2061, Calendar.AUGUST, 3, 1, 23, 45).time
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME) >> startDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME) >> endDate
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> false
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
			
		when:
			def results = testObj.isSkuActive(repositoryItemMock, "false")
			
		then:
			results == false
	   }
	
	/* isSkuActive - Test Cases ENDS */
	
	/* getFirstActiveParentProductForInactiveSKU - Test Cases START */
	
	def "getFirstActiveParentProductForInactiveSKU when passing skuId as null" () {
		given:
		
			testObj = Spy()
		
		when:
			def results = testObj.getFirstActiveParentProductForInactiveSKU(null)
			
		then:
			results == null
			thrown BBBBusinessException
	   }
	
	def "getFirstActiveParentProductForInactiveSKU when passing skuId as empty" () {
		given:
		
			testObj = Spy()
		
		when:
			def results = testObj.getFirstActiveParentProductForInactiveSKU("")
			
		then:
			results == null
			thrown BBBBusinessException
	   }
	
	def "getFirstActiveParentProductForInactiveSKU when repository item returns null" () {
		given:
			testObj = Spy()
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("222121",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null
		
		when:
			def results = testObj.getFirstActiveParentProductForInactiveSKU("222121")
			
		then:
			results == null
			thrown BBBBusinessException
	   }
	
	def "getFirstActiveParentProductForInactiveSKU when repository exception occurs" () {
		given:
			testObj = Spy()
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("222121",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock Repository Exception")}
		
		when:
			def results = testObj.getFirstActiveParentProductForInactiveSKU("222121")
			
		then:
			results == null
			thrown BBBSystemException
	   }
	
	def "getFirstActiveParentProductForInactiveSKU when parentProductList item id is null" () {
		given:
			testObj = Spy()
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("222121",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> catalogRepositoryItemMock
			Set<RepositoryItem> parentProductList = new HashSet<RepositoryItem>()
			parentProductList.add(repositoryItemMock)
			
			catalogRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> parentProductList
			repositoryItemMock.getRepositoryId() >> null
			
		when:
			def results = testObj.getFirstActiveParentProductForInactiveSKU("222121")
			
		then:
			results == null
			thrown BBBBusinessException
	   }
	
	def "getFirstActiveParentProductForInactiveSKU when parentProductList item is null" () {
		given:
		
			testObj = Spy()
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("222121",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> catalogRepositoryItemMock
			catalogRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> null
			
		when:
			def results = testObj.getFirstActiveParentProductForInactiveSKU("222121")
			
		then:
			results == null
			thrown BBBBusinessException
	   }
	
	def "getFirstActiveParentProductForInactiveSKU when parentProductList item is empty" () {
		given:
		
			testObj = Spy()
			testObj.setCatalogRepository(catalogRepositoryMock)
			catalogRepositoryMock.getItem("222121",BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> catalogRepositoryItemMock
			Set<RepositoryItem> parentProductList = new HashSet<RepositoryItem>()
			catalogRepositoryItemMock.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCT_PROPERTY_NAME) >> parentProductList
			
		when:
			def results = testObj.getFirstActiveParentProductForInactiveSKU("222121")
			
		then:
			results == null
			thrown BBBBusinessException
	   }
	
	/* getFirstActiveParentProductForInactiveSKU - Test Cases ENDS */
	
	/* getPreviewDate - Test Cases START */
	
	def "getPreviewDate by passsing PreviewAttributes" () {
		given:
		
			testObj = Spy()
			
			ServletUtil.setCurrentRequest(requestMock)
			
			requestMock.resolveName("/com/bbb/commerce/catalog/PreviewAttributes") >> PreviewAttributesMock 
			PreviewAttributesMock.getPreviewDate() >> new Date()
			
		when:
			def results = testObj.getPreviewDate()			
		then:
			results != null
	   }
	
	def "getPreviewDate by passing PreviewAttributes as null" () {
		given:
		
			testObj = Spy()
			
			ServletUtil.setCurrentRequest(requestMock)
			requestMock.resolveName("/com/bbb/commerce/catalog/PreviewAttributes") >> null
			
		when:
			def results = testObj.getPreviewDate()
		then:
			results != null
	   }
	
	def "getPreviewDate by passing previewDate as null" () {
		given:
			
			testObj = Spy()
			ServletUtil.setCurrentRequest(requestMock)
			requestMock.resolveName("/com/bbb/commerce/catalog/PreviewAttributes") >> PreviewAttributesMock
			PreviewAttributesMock.getPreviewDate() >> null
			
		when:
			def results = testObj.getPreviewDate()
		then:
			results != null
	   }
	
	/* getPreviewDate - Test Cases ENDS */
	
	/* isCustomizationOfferedForSKU - Test Cases START */
	
	def "isCustomizationOfferedForSKU by passing values" () {
		given:
		
			repositoryItemMock.getPropertyValue(testObj.getCustomizationOfferedSiteMap().get("BedBathUS")) >> true
		
		when:
			def results = testObj.isCustomizationOfferedForSKU(repositoryItemMock, "BedBathUS")
		then:
			results == true
	   }
	
	def "isCustomizationOfferedForSKU when repository item property returns null" () {
		given:
		
			repositoryItemMock.getPropertyValue(testObj.getCustomizationOfferedSiteMap().get("BuyBuyBaby")) >> null
		
		when:
			def results = testObj.isCustomizationOfferedForSKU(repositoryItemMock, "BuyBuyBaby")
		then:
			results == false
	   }
	
	/* isCustomizationOfferedForSKU - Test Cases ENDS */
	
	/* getStoreSpecialityList - Test Cases START */ 
	
	def "getStoreSpecialityList when item gives properties" () {
		given:
		
			testObj = Spy()
			
			Set<RepositoryItem> specialityItemSetMock = new HashSet<RepositoryItem>()
			specialityItemSetMock.add(repositoryItemMock)
			
			repositoryItemMock.getRepositoryId() >> "2232" 
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SPECIALITY_CODE_NAME_STORE_PROPERTY_NAME) >> "specialityCode"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CODE_IMAGE_STORE_PROPERTY_NAME) >> "codeChange"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRIORITY_STORE_PROPERTY_NAME) >> 3332
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STORE_LIST_ALT_TEXT_STORE_PROPERTY_NAME) >> "storeListAlt"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STORE_LIST_TITLE_TEXT_STORE_PROPERTY_NAME) >> "storeListTitle"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IMG_LOC_STORE_PROPERTY_NAME) >> "imgLoc"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IMG_ALT_TEXT_STORE_PROPERTY_NAME) >> "imgAlt"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IMG_TITLE_TEXT_STORE_PROPERTY_NAME) >> "imgTitle"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEGEND_FILE_LOC_STORE_PROPERTY_NAME) >> "legendFile"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEGEND_ALT_TEXT_STORE_PROPERTY_NAME) >> "legendAlt"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEGEND_TITLE_TEXT_STORE_PROPERTY_NAME) >> "legendTitle"
			
		when:
			def List<StoreSpecialityVO> results = testObj.getStoreSpecialityList(specialityItemSetMock)
		then:
			results.size() > 0
	   }
	
	def "getStoreSpecialityList when item gives properties as null" () {
		given:
		
			testObj = Spy()
			
			Set<RepositoryItem> specialityItemSetMock = new HashSet<RepositoryItem>()
			specialityItemSetMock.add(repositoryItemMock)
			
			repositoryItemMock.getRepositoryId() >> "2232"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SPECIALITY_CODE_NAME_STORE_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.CODE_IMAGE_STORE_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.PRIORITY_STORE_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STORE_LIST_ALT_TEXT_STORE_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.STORE_LIST_TITLE_TEXT_STORE_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IMG_LOC_STORE_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IMG_ALT_TEXT_STORE_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IMG_TITLE_TEXT_STORE_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEGEND_FILE_LOC_STORE_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEGEND_ALT_TEXT_STORE_PROPERTY_NAME) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.LEGEND_TITLE_TEXT_STORE_PROPERTY_NAME) >> null
			
		when:
			def List<StoreSpecialityVO> results = testObj.getStoreSpecialityList(specialityItemSetMock)
		then:
			results != null
	   }
	
	/* getStoreSpecialityList - Test Cases ENDS */
	
	

}
