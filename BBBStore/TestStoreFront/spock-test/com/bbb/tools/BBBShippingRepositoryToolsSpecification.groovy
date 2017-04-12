package com.bbb.tools

import java.util.Map;
import java.util.Set;

import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository
import atg.repository.QueryBuilder
import atg.repository.QueryExpression
import atg.repository.Query
import atg.repository.RepositoryException

import com.bbb.exception.BBBSystemException;

import atg.repository.RepositoryItem
import atg.repository.RepositoryItemImpl
import atg.repository.RepositoryView
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest

import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.RegionVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;

import atg.servlet.ServletUtil

import com.bbb.framework.jaxb.internationalshipping.checkoutrequest.ShippingMethod;
import com.bbb.framework.performance.logger.PerformanceLogger
import com.bbb.framework.validation.BBBValidationRules
import com.bbb.repository.RepositoryItemMock;

import spock.lang.Specification;

class BBBShippingRepositoryToolsSpecification extends Specification {


	def BBBShippingRepositoryTools testObj
	def MutableRepository shippingRepositoryMock = Mock()
	def MutableRepository siteRepositoryMock = Mock()
	def MutableRepository catalogRepositoryMock = Mock()
	def GlobalRepositoryTools globalRepositoryToolsMock = Mock()
	def LblTxtTemplateManager lblTxtTemplateManagerMock = Mock()
	def RepositoryItemMock repositoryItemMock = Mock()
	def DynamoHttpServletRequest requestMock = Mock()
	def PerformanceLogger perfMock = Mock()
	def RepositoryItem promoAttributeIdMock = Mock()
	def RegionVO RegionVoMock = Mock()
	def RepositoryItem siteRepositoryItemMock = Mock() 
	def RepositoryView repositoryViewMock = Mock()
	def RqlStatement rqlStatementMock = Mock()
	def QueryBuilder queryBuilderMock = Mock()
	def QueryExpression queryExpressionMock = Mock()
	def QueryExpression shippingCodeMock = Mock()
	def QueryExpression queryExprSiteIdMock = Mock()
	def QueryExpression queryExprsiteValueMock = Mock()
	def Query queryMock = Mock()
	
	private final String shippingMethod = "LWA"
	private final String siteId = "BEDBATH_US"


	def setup() {

		testObj = new BBBShippingRepositoryTools(shippingRepository:shippingRepositoryMock,globalRepositoryTools:globalRepositoryToolsMock,
			shippingFeeRqlQuery:"shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and state= ?2",
			shippingFeeRqlQueryForNullState:"shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and state is null",
		shippingCostRqlQuery:"shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and giftCard=1",
		shippingDurationRqlQuery:"shipMethodCode EQUALS IGNORECASE ?0 and site = ?1",
		shippingMethodRqlQuery:"id EQUALS IGNORECASE ?0",
		regionsRqlQuery:"zipCodes INCLUDES ?0 AND sddFlag=true AND sites = ?1",sddShipMethodId:"SDD")
		
		ServletUtil.setCurrentRequest(requestMock)
		requestMock.getContextPath() >> "/store"
		requestMock.resolveName("/com/bbb/framework/performance/logger/PerformanceLogger") >> perfMock
		perfMock.isEnableCustomComponentsMonitoring() >> false
		
		
	}
	
	/* getShippingMethod - Test Cases STARTS */
	
	def "getShippingMethod when passing parameter shippingMethod"(){
		
		given:
			shippingRepositoryMock.getItem("LW", BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR) >> repositoryItemMock
		
		when:
			def results = testObj.getShippingMethod("LWA")
		
		then:
			results == repositoryItemMock
	}
	
	def "getShippingMethod when Repository Exception Occurs"(){
		
		given:
			shippingRepositoryMock.getItem("LW", BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock Repository Exception")}
		
		when:
			def results = testObj.getShippingMethod("LWA")
		
		then:
			results == null
			thrown BBBSystemException
	}
	
	def "getShippingMethod when Repository Exception Occurs and loggingError is false"(){
		
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			shippingRepositoryMock.getItem("LW", BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock Repository Exception")}
			testObj.isLoggingError() >> false
		when:
			def results = testObj.getShippingMethod("LWA")
		
		then:
			results == null
			thrown BBBSystemException
	}
	
	def "getShippingMethod when passing shippingMethod as LW"(){
		
		given:
			shippingRepositoryMock.getItem("LW", BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR) >> repositoryItemMock
		
		when:
			def results = testObj.getShippingMethod("LW")
		
		then:
			results == repositoryItemMock
	}
	
	def "getShippingMethod when repsoitoryItem returns null"(){
		
		given:
			shippingRepositoryMock.getItem("LW", BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR) >> null
		
		when:
			def results = testObj.getShippingMethod("LW")
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	/* getShippingMethod - Test Cases ENDS */ 
	
	/* getStateTax - Test Cases START */
	
	def "getStateTax when passing parameter stateCode"() {

		given:
			shippingRepositoryMock.getItem("NJ",BBBCatalogConstants.STATE_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.PROPERTY_NAME_STATE_TAX) >> 22

		when:
			def results = testObj.getStateTax("NJ")

		then:
			results == 22
	}
	
	def "getStateTax when Repository Exception occurs"() {
		
		given:
			shippingRepositoryMock.getItem("NJ",BBBCatalogConstants.STATE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock Repository Exception")}
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.PROPERTY_NAME_STATE_TAX) >> 22
		
		when:
			def results = testObj.getStateTax("NJ")
		
		then:
			results == 0.0
		}
	
	def "getStateTax when stateCode is empty"() {
		
		given:
		
		when:
			def results = testObj.getStateTax("")
		
		then:
			results == 0.0
	}
	
	def "getStateTax when stateCode is null"() {
		
		given:
		
		when:
			def results = testObj.getStateTax(null)
			
		then:
			results == 0.0
	}
	
	def "getStateTax when repositoryItem is null"() {
		
		given:
			shippingRepositoryMock.getItem(null,BBBCatalogConstants.STATE_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.PROPERTY_NAME_STATE_TAX) >> null
		
		when:
			def results = testObj.getStateTax("NJ")
	
		then:	
			results == 0.0
			
	}
	
	def "getStateTax when stateTaxValue is null"() {
		
		given:
			shippingRepositoryMock.getItem("NJ",BBBCatalogConstants.STATE_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.PROPERTY_NAME_STATE_TAX) >> null
		
		when:
			def results = testObj.getStateTax("NJ")
	
		then:
			results == 0.0
			
	}
	
	/* getStateTax - Test Case ENDS */
	
	/* getStoreIdsFromRegion - Test Cases START */
	
	def "getStoreIdsFromRegion when passing parameter regionId" (){
		
		given:
			shippingRepositoryMock.getItem("DC11600001",BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_SDD_FLAG) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_ID) >> "regionId"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_NAME) >> "regionName"
			Date cutOffTimeRegion = new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_CUT_OFF) >> cutOffTimeRegion
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_DISP_CUT_OFF) >> "displayCutOffTime"
			Set regionStores = new HashSet<String>()
			regionStores.add("stores")
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_STORES) >> regionStores
			Set regionZipCodes = new HashSet<String>()
			regionZipCodes.add("zipCodes")
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_ZIPCODES) >> regionZipCodes
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_PROMO_ATT) >> promoAttributeIdMock
			promoAttributeIdMock.getRepositoryId() >> "DC1bbAI5140002"
			Set sitesSet = new HashSet<RepositoryItem>()
			sitesSet.add(siteRepositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITES) >> sitesSet
			Double minShipFee = new Double(0.1f)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIP_FEE) >> minShipFee
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.GET_BY_TIME) >> "getByTime"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.TIME_ZONE) >> "regionTimeZone"
		
		when:
			def RegionVO results = testObj.getStoreIdsFromRegion("DC11600001")
			
		then:
			 results.storeIds == regionStores
	}
	
	def "getStoreIdsFromRegion when Repository Exception occurs" (){
		
		given:
			shippingRepositoryMock.getItem("DC11600001",BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock Repository Exception")}
		
		when:
			def RegionVO results = testObj.getStoreIdsFromRegion("DC11600001")
			
		then:
			 results == null
			 thrown BBBSystemException
	}
	
	def "getStoreIdsFromRegion when region Id is empty" (){
		given:
		
		when:
			def RegionVoMock = testObj.getStoreIdsFromRegion("")
			
		then:
			RegionVoMock == null
			thrown BBBBusinessException	
	}
	
	def "getStoreIdsFromRegion when region Id is null" (){
		given:
		
		when:
			def RegionVoMock = testObj.getStoreIdsFromRegion(null)
			
		then:
			RegionVoMock == null
			thrown BBBBusinessException
	}
	
	
	def "getStoreIdsFromRegion when regionStores is null" (){
		
		given:
			shippingRepositoryMock.getItem("DC11600001",BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_SDD_FLAG) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_ID) >> "regionId"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_NAME) >> "regionName"
			Date cutOffTimeRegion = new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_CUT_OFF) >> cutOffTimeRegion
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_DISP_CUT_OFF) >> "displayCutOffTime"
			Set regionStores = new HashSet<String>()
			regionStores.add("stores")
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_STORES) >> null
			Set regionZipCodes = new HashSet<String>()
			regionZipCodes.add("zipCodes")
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_ZIPCODES) >> regionZipCodes
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_PROMO_ATT) >> promoAttributeIdMock
			promoAttributeIdMock.getRepositoryId() >> "DC1bbAI5140002"
			Set sitesSet = new HashSet<RepositoryItem>()
			sitesSet.add(siteRepositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITES) >> sitesSet
			Double minShipFee = new Double(0.1f)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIP_FEE) >> minShipFee
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.GET_BY_TIME) >> "getByTime"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.TIME_ZONE) >> "regionTimeZone"
		
		when:
			def RegionVO results = testObj.getStoreIdsFromRegion("DC11600001")
			
		then:
			 results == null
	}
	
	def "getStoreIdsFromRegion when sdd flag returns false" (){
		
		given:
			shippingRepositoryMock.getItem("DC11600001",BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_SDD_FLAG) >> false
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_ID) >> "regionId"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_NAME) >> "regionName"
			Date cutOffTimeRegion = new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_CUT_OFF) >> cutOffTimeRegion
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_DISP_CUT_OFF) >> "displayCutOffTime"
			Set regionStores = new HashSet<String>()
			regionStores.add("stores")
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_STORES) >> regionStores
			Set regionZipCodes = new HashSet<String>()
			regionZipCodes.add("zipCodes")
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_ZIPCODES) >> regionZipCodes
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_PROMO_ATT) >> promoAttributeIdMock
			promoAttributeIdMock.getRepositoryId() >> "DC1bbAI5140002"
			Set sitesSet = new HashSet<RepositoryItem>()
			sitesSet.add(siteRepositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITES) >> sitesSet
			Double minShipFee = new Double(0.1f)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIP_FEE) >> minShipFee
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.GET_BY_TIME) >> "getByTime"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.TIME_ZONE) >> "regionTimeZone"
			
		when:
			def RegionVO results = testObj.getStoreIdsFromRegion("DC11600001")
			
		then:
			 results == null
	}
	
	def "getStoreIdsFromRegion when regionStores is given null" (){
		
		given:
			shippingRepositoryMock.getItem("DC11600001",BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_SDD_FLAG) >> true
			Set regionStores = new HashSet<String>()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_STORES) >> regionStores
			Double minShipFee = new Double(0.1f)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIP_FEE) >> minShipFee
		
		when:
			def RegionVO results = testObj.getStoreIdsFromRegion("DC11600001")
			
		then:
			 results == null
	}
	
	/* getStoreIdsFromRegion - Test Cases ENDS */
	
	/* getZipCodesFromRegion - Test Cases START */
	
	def "getZipCodesFromRegion when passing parameter regionId" (){
		
		given:
			shippingRepositoryMock.getItem("DC11600001",BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_SDD_FLAG) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_ID) >> "regionId"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_NAME) >> "regionName"
			Date cutOffTimeRegion = new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_CUT_OFF) >> cutOffTimeRegion
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_DISP_CUT_OFF) >> "displayCutOffTime"
			Set regionStores = new HashSet<String>()
			regionStores.add("stores")
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_STORES) >> regionStores
			Set regionZipCodes = new HashSet<String>()
			regionZipCodes.add("zipCodes")
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_ZIPCODES) >> regionZipCodes
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_PROMO_ATT) >> promoAttributeIdMock
			promoAttributeIdMock.getRepositoryId() >> "DC1bbAI5140002"
			Set sitesSet = new HashSet<RepositoryItem>()
			sitesSet.add(siteRepositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITES) >> sitesSet
			Double minShipFee = new Double(0.1f)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIP_FEE) >> minShipFee
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.GET_BY_TIME) >> "getByTime"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.TIME_ZONE) >> "regionTimeZone"
		
		when:
			def RegionVO results = testObj.getZipCodesFromRegion("DC11600001")
			
		then:
			 results.zipCodes == regionZipCodes
	}
	
	def "getZipCodesFromRegion when Repository Exception occurs" (){
		
		given:
			shippingRepositoryMock.getItem("DC11600001",BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock Repository Exception")}
		
		when:
			def RegionVO results = testObj.getZipCodesFromRegion("DC11600001")
			
		then:
			 results == null
			 thrown BBBSystemException
	}
	
	def "getZipCodesFromRegion when region Id is empty" (){
		given:
		
		when:
			def RegionVoMock = testObj.getZipCodesFromRegion("")
			
		then:
			RegionVoMock == null
			thrown BBBBusinessException
	}
	
	def "getZipCodesFromRegion when region Id is null" (){
		given:
		
		when:
			def RegionVoMock = testObj.getZipCodesFromRegion(null)
			
		then:
			RegionVoMock == null
			thrown BBBBusinessException
	}
	
	
	def "getZipCodesFromRegion when regionZipCodes is empty" (){
		
		given:
			shippingRepositoryMock.getItem("DC11600001",BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_SDD_FLAG) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_ID) >> "regionId"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_NAME) >> "regionName"
			Date cutOffTimeRegion = new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_CUT_OFF) >> cutOffTimeRegion
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_DISP_CUT_OFF) >> "displayCutOffTime"
			Set regionStores = new HashSet<String>()
			regionStores.add("stores")
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_STORES) >> regionStores
			Set<String> regionZipCodes = new HashSet<String>()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_ZIPCODES) >> regionZipCodes
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_PROMO_ATT) >> promoAttributeIdMock
			promoAttributeIdMock.getRepositoryId() >> "DC1bbAI5140002"
			Set sitesSet = new HashSet<RepositoryItem>()
			sitesSet.add(siteRepositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITES) >> sitesSet
			Double minShipFee = new Double(0.1f)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIP_FEE) >> minShipFee
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.GET_BY_TIME) >> "getByTime"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.TIME_ZONE) >> "regionTimeZone"
		
		when:
			def RegionVO results = testObj.getZipCodesFromRegion("DC11600001")
			
		then:
			 results == null
	}
	
	def "getZipCodesFromRegion when sdd flag returns false" (){
		
		given:
			shippingRepositoryMock.getItem("DC11600001",BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_SDD_FLAG) >> false
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_ID) >> "regionId"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_NAME) >> "regionName"
			Date cutOffTimeRegion = new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_CUT_OFF) >> cutOffTimeRegion
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_DISP_CUT_OFF) >> "displayCutOffTime"
			Set regionStores = new HashSet<String>()
			regionStores.add("stores")
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_STORES) >> regionStores
			Set regionZipCodes = new HashSet<String>()
			regionZipCodes.add("zipCodes")
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_ZIPCODES) >> regionZipCodes
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_PROMO_ATT) >> promoAttributeIdMock
			promoAttributeIdMock.getRepositoryId() >> "DC1bbAI5140002"
			Set sitesSet = new HashSet<RepositoryItem>()
			sitesSet.add(siteRepositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITES) >> sitesSet
			Double minShipFee = new Double(0.1f)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIP_FEE) >> minShipFee
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.GET_BY_TIME) >> "getByTime"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.TIME_ZONE) >> "regionTimeZone"
		
		when:
			def RegionVO results = testObj.getZipCodesFromRegion("DC11600001")
			
		then:
			 results == null
	}
	
	def "getZipCodesFromRegion when zipcode is empty" (){
		
		given:
			shippingRepositoryMock.getItem("DC11600001",BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_SDD_FLAG) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_ZIPCODES) >> null
			Double minShipFee = new Double(0.1f)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIP_FEE) >> minShipFee
			
		when:
			def RegionVO results = testObj.getZipCodesFromRegion("DC11600001")
			
		then:
			 results == null
	}
	
	/* getZipCodesFromRegion - Test Cases ENDS */
	
	/* getAllRegionDetails - Test Cases START */
	
	def "getAllRegionDetails when passing parameter regionId" (){
		
		given:
			shippingRepositoryMock.getItem("DC11600001",BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryItemMock
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_SDD_FLAG) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_ID) >> "regionId"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_REGION_NAME) >> "regionName"
			Date cutOffTimeRegion = new Date()
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_CUT_OFF) >> cutOffTimeRegion
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_DISP_CUT_OFF) >> "displayCutOffTime"
			Set regionStores = new HashSet<String>()
			regionStores.add("stores")
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_STORES) >> regionStores
			Set regionZipCodes = new HashSet<String>()
			regionZipCodes.add("zipCodes")
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_ZIPCODES) >> regionZipCodes
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_PROMO_ATT) >> promoAttributeIdMock
			promoAttributeIdMock.getRepositoryId() >> "DC1bbAI5140002"
			Set sitesSet = new HashSet<RepositoryItem>()
			sitesSet.add(siteRepositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.SITES) >> sitesSet
			Double minShipFee = new Double(0.1f)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIP_FEE) >> minShipFee
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.GET_BY_TIME) >> "getByTime"
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.TIME_ZONE) >> "regionTimeZone"
			siteRepositoryItemMock.getRepositoryId() >> "BUYBUYBABY"
		
		when:
			def RegionVO results = testObj.getAllRegionDetails("DC11600001")
			
		then:
			 results.regionId == "regionId"
			 results.zipCodes == regionZipCodes
			 results.minShipFee == minShipFee
			 results.sddFlag == true
	}
	
	def "getAllRegionDetails when Repository Exception occurs" (){
		
		given:
			shippingRepositoryMock.getItem("DC11600001",BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock Repository Exception")}
		
		when:
			def RegionVO results = testObj.getAllRegionDetails("DC11600001")
			
		then:
			 results == null
			 thrown BBBSystemException
	}
	
	def "getAllRegionDetails when region Id is empty" (){
		given:
		
		when:
			def RegionVoMock = testObj.getAllRegionDetails("")
			
		then:
			RegionVoMock == null
			thrown BBBBusinessException
	}
	
	def "getAllRegionDetails when region Id is null" (){
		given:
		
		when:
			def RegionVoMock = testObj.getAllRegionDetails(null)
			
		then:
			RegionVoMock == null
			thrown BBBBusinessException
	}
	
	def "getAllRegionDetails when repository item is null" (){
		
		given:
			shippingRepositoryMock.getItem("DC11600001",BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> null
		
		when:
			def RegionVO results = testObj.getAllRegionDetails("DC11600001")
			
		then:
			 results == null
	}
	
	/* getRegionDataFromZip - Test Cases STARTS */
	
	def "getRegionDataFromZip by passing parameter zipcode"(){
		
		given:
			testObj = Spy()
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.getCurrentSiteId() >> "BEDBATH_US" 
			shippingRepositoryMock.getView(BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryViewMock
			testObj.getRQLStatement() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			rqlStatementMock.executeQuery(repositoryViewMock,["98001", "BEDBATH_US"]) >> [item1]
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_PROMO_ATT) >> promoAttributeIdMock
			Date cutOffTimeRegion = new Date()
			Set regionStores = new HashSet<String>()
			regionStores.add("stores")
			Set regionZipCodes = new HashSet<String>()
			regionZipCodes.add("zipCodes")
			promoAttributeIdMock.getRepositoryId() >> "DC1bbAI5140002"
			Set sitesSet = new HashSet<RepositoryItem>()
			sitesSet.add(siteRepositoryItemMock)
			Double minShipFee = new Double(0.1f)
			
			item1.setProperties(["sddFlag" : true , "regionId" : "regionId" , "regionName" : "regionName" , "cutOffTimeRegion" : cutOffTimeRegion , "displayCutOffTime" : "displayCutOffTime" ,
				 "stores" : regionStores , "zipCodes" : regionZipCodes , "promoAttributeId" : promoAttributeIdMock , "sites" : sitesSet, "minShipFee" : minShipFee , "getByTime" : "getByTime" ,
				 "regionTimeZone" : "regionTimeZone"])
			
			testObj.populateRegionVO(item1, "regionNStore")
		
		when:
			def RegionVO results = testObj.getRegionDataFromZip("98001")
		
		then:
		
			results.regionId == "regionId"
			results.displayCutoffTime == "displayCutOffTime"
			results.timeZone == "regionTimeZone"
	}
	
	def "getRegionDataFromZip when Repository Exception occurs"(){
		
		given:
			testObj = Spy()
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			shippingRepositoryMock.getView(BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryViewMock
			testObj.getRQLStatement() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			rqlStatementMock.executeQuery(repositoryViewMock,["98001", "BEDBATH_US"]) >> {throw new RepositoryException("Mock Repository Exception")}
		
		when:
			def RegionVO results = testObj.getRegionDataFromZip("98001")
		
		then:
		
			results == null
			thrown BBBSystemException
	}
	
	def "getRegionDataFromZip by passing type as null for populateRegionVO"(){
		
		given:
			testObj = Spy()
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			shippingRepositoryMock.getView(BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryViewMock
			testObj.getRQLStatement() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			rqlStatementMock.executeQuery(repositoryViewMock,["98001", "BEDBATH_US"]) >> [item1]
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_PROMO_ATT) >> promoAttributeIdMock
			Date cutOffTimeRegion = new Date()
			Set regionStores = new HashSet<String>()
			regionStores.add("stores")
			Set regionZipCodes = new HashSet<String>()
			regionZipCodes.add("zipCodes")
			promoAttributeIdMock.getRepositoryId() >> "DC1bbAI5140002"
			Set sitesSet = new HashSet<RepositoryItem>()
			sitesSet.add(siteRepositoryItemMock)
			Double minShipFee = new Double(0.1f)
			
			item1.setProperties(["sddFlag" : true , "regionId" : "regionId" , "regionName" : "regionName" , "cutOffTimeRegion" : cutOffTimeRegion , "displayCutOffTime" : "displayCutOffTime" ,
				 "stores" : regionStores , "zipCodes" : regionZipCodes , "promoAttributeId" : promoAttributeIdMock , "sites" : sitesSet, "minShipFee" : minShipFee , "getByTime" : "getByTime" ,
				 "regionTimeZone" : "regionTimeZone"])
			
			testObj.populateRegionVO(item1, null)
		
		when:
			def RegionVO results = testObj.getRegionDataFromZip("98001")
		
		then:
		
			results.regionId == "regionId"
			results.displayCutoffTime == "displayCutOffTime"
			results.timeZone == "regionTimeZone"
	}
	
	
	def "getRegionDataFromZip by passing promoAttributeId as null"(){
		
		given:
			testObj = Spy()
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			shippingRepositoryMock.getView(BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryViewMock
			testObj.getRQLStatement() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			rqlStatementMock.executeQuery(repositoryViewMock,["98001", "BEDBATH_US"]) >> [item1]
			
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.REGIONS_PROMO_ATT) >> promoAttributeIdMock
			Date cutOffTimeRegion = new Date()
			Set regionStores = new HashSet<String>()
			Set regionZipCodes = new HashSet<String>()
			regionZipCodes.add("zipCodes")
			promoAttributeIdMock.getRepositoryId() >> "DC1bbAI5140002"
			Set sitesSet = new HashSet<RepositoryItem>()
			sitesSet.add(siteRepositoryItemMock)
			Double minShipFee = new Double(0.1f)
			
			item1.setProperties(["sddFlag" : true , "regionId" : "" , "regionName" : "regionName" , "cutOffTimeRegion" : cutOffTimeRegion , "displayCutOffTime" : "" ,
				 "stores" : regionStores , "zipCodes" : regionZipCodes , "promoAttributeId" : null , "sites" : sitesSet, "minShipFee" : minShipFee , "getByTime" : "getByTime" ,
				 "regionTimeZone" : "regionTimeZone"])
			
			testObj.populateRegionVO(item1, "regionNStore")
		
		when:
			def RegionVO results = testObj.getRegionDataFromZip("98001")
		
		then:
		
			results.regionId == null
			results.displayCutoffTime == null
			results.timeZone == null
	}
	
	def "getRegionDataFromZip by passing type as different"(){
		
		given:
			testObj = Spy()
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			shippingRepositoryMock.getView(BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryViewMock
			testObj.getRQLStatement() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			rqlStatementMock.executeQuery(repositoryViewMock,["98001", "BEDBATH_US"]) >> [item1]
			Double minShipFee = new Double(0.1f)
			item1.setProperties(["sddFlag" : true , "regionId" : "" , "regionName" : "regionName" , "displayCutOffTime" : "" , "promoAttributeId" : null , "minShipFee" : minShipFee , 
				"getByTime" : "getByTime" , "regionTimeZone" : "regionTimeZone"])
			
			testObj.populateRegionVO(item1, "different")
		
		when:
			def RegionVO results = testObj.getRegionDataFromZip("98001")
		
		then:
			results != null
	}
	
	
	def "getRegionDataFromZip when repository returns null"(){
		
		given:
			testObj = Spy()
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			shippingRepositoryMock.getView(BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryViewMock
			testObj.getRQLStatement() >> rqlStatementMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			rqlStatementMock.executeQuery(repositoryViewMock,["98001", "BEDBATH_US"]) >> null
		
		when:
			def RegionVO results = testObj.getRegionDataFromZip("98001")
		
		then:
			results == null
	}
	
	def "getRegionDataFromZip by passing zipcode as null"(){
		
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			shippingRepositoryMock.getView(BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryViewMock
		
		when:
			def RegionVoMock = testObj.getRegionDataFromZip(null)
		
		then:
			RegionVoMock == null
			thrown BBBBusinessException
		
	}
	
	def "getRegionDataFromZip by passing zipcode as empty"(){
		
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			shippingRepositoryMock.getView(BBBCatalogConstants.REGIONS_ITEM_DESCRIPTOR) >> repositoryViewMock
		
		when:
			def RegionVoMock = testObj.getRegionDataFromZip("")
		
		then:
			RegionVoMock == null
			thrown BBBBusinessException
		
	}
	
	/* getRegionDataFromZip - Test Cases ENDS */
	
	/* getShippingDuration - Test Cases STARTS */
	
	def "getShippingDuration passing shipping method and siteId"(){
		given:
		
			testObj = Spy()
			
			testObj.setShippingRepository(shippingRepositoryMock)
			shippingRepositoryMock.getView(BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR) >> repositoryViewMock 
			repositoryViewMock.getQueryBuilder() >> queryBuilderMock
			queryBuilderMock.createPropertyQueryExpression(BBBCatalogConstants.SHIP_METHOD_CD) >> queryExpressionMock
			queryBuilderMock.createConstantQueryExpression(shippingMethod) >> shippingCodeMock
			queryBuilderMock.createPropertyQueryExpression("site") >> queryExprSiteIdMock
			queryBuilderMock.createConstantQueryExpression(siteId) >> queryExprsiteValueMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			repositoryViewMock.executeQuery(*_) >> [item1]
		
		when:
			def RepositoryItem results = testObj.getShippingDuration(shippingMethod, siteId) 
		
		then:
			results == item1
	}
	
	def "getShippingDuration when Repository Exception Occurs"(){
		given:
		
			testObj = Spy()
			
			testObj.setShippingRepository(shippingRepositoryMock)
			shippingRepositoryMock.getView(BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR) >> repositoryViewMock
			repositoryViewMock.getQueryBuilder() >> queryBuilderMock
			queryBuilderMock.createPropertyQueryExpression(BBBCatalogConstants.SHIP_METHOD_CD) >> queryExpressionMock
			queryBuilderMock.createConstantQueryExpression(shippingMethod) >> shippingCodeMock
			queryBuilderMock.createPropertyQueryExpression("site") >> queryExprSiteIdMock
			queryBuilderMock.createConstantQueryExpression(siteId) >> queryExprsiteValueMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			repositoryViewMock.executeQuery(*_) >> {throw new RepositoryException("Mock Repository Exception")}
		
		when:
			def RepositoryItem results = testObj.getShippingDuration(shippingMethod, siteId)
		
		then:
			results == null
			thrown BBBSystemException
	}
	
	def "getShippingDuration when repository item returns with item size 0"(){
		given:
		
			testObj = Spy()
			
			testObj.setShippingRepository(shippingRepositoryMock)
			shippingRepositoryMock.getView(BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR) >> repositoryViewMock
			repositoryViewMock.getQueryBuilder() >> queryBuilderMock
			queryBuilderMock.createPropertyQueryExpression(BBBCatalogConstants.SHIP_METHOD_CD) >> queryExpressionMock
			queryBuilderMock.createConstantQueryExpression(shippingMethod) >> shippingCodeMock
			queryBuilderMock.createPropertyQueryExpression("site") >> queryExprSiteIdMock
			queryBuilderMock.createConstantQueryExpression(siteId) >> queryExprsiteValueMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			repositoryViewMock.executeQuery(*_) >> []
		
		when:
			def RepositoryItem results = testObj.getShippingDuration(shippingMethod, siteId)
		
		then:
			results == null
	}
	
	def "getShippingDuration when repository item returns null"(){
		given:
		
			testObj = Spy()
			
			testObj.setShippingRepository(shippingRepositoryMock)
			shippingRepositoryMock.getView(BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR) >> repositoryViewMock
			repositoryViewMock.getQueryBuilder() >> queryBuilderMock
			queryBuilderMock.createPropertyQueryExpression(BBBCatalogConstants.SHIP_METHOD_CD) >> queryExpressionMock
			queryBuilderMock.createConstantQueryExpression(shippingMethod) >> shippingCodeMock
			queryBuilderMock.createPropertyQueryExpression("site") >> queryExprSiteIdMock
			queryBuilderMock.createConstantQueryExpression(siteId) >> queryExprsiteValueMock
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			repositoryViewMock.executeQuery(*_) >> null
		
		when:
			def RepositoryItem results = testObj.getShippingDuration(shippingMethod, siteId)
		
		then:
			results == null
	}
	
	/* getShippingDuration - Test Cases ENDS */
	
	/* shippingCostForGiftCard - Test Cases START */
	
	def "shippingCostForGiftCard when passing siteId and shippingMethod" (){
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setShippingCostRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and giftCard=1")
				
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingCostRqlQuery(),["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,shippingRepositoryMock) >> repositoryItemList
			
			Double priceValue = new Double(6.99f)
			item1.setProperties(["price" : priceValue])
			
		when:
			def results = testObj.shippingCostForGiftCard(siteId, shippingMethod)
		
		then:
		results == priceValue
	}
	
	def "shippingCostForGiftCard when passing siteId as null" (){
		given:
			testObj = Spy()
			
		when:
			def results = testObj.shippingCostForGiftCard(null, shippingMethod)
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "shippingCostForGiftCard when passing siteId as empty" (){
		given:
			testObj = Spy()
			
		when:
			def results = testObj.shippingCostForGiftCard("", shippingMethod)
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "shippingCostForGiftCard when passing shippingMethod as empty" (){
		given:
			testObj = Spy()
			
		when:
			def results = testObj.shippingCostForGiftCard(siteId, "")
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "shippingCostForGiftCard when passing shippingMethod as null" (){
		given:
			testObj = Spy()
			
		when:
			def results = testObj.shippingCostForGiftCard(siteId, null)
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "shippingCostForGiftCard when repository item is null" (){
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setShippingCostRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and giftCard=1")
				
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingCostRqlQuery(),["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,shippingRepositoryMock) >> null
			
		when:
			def results = testObj.shippingCostForGiftCard(siteId, shippingMethod)
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "shippingCostForGiftCard when repository item is 0" (){
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setShippingCostRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and giftCard=1")
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingCostRqlQuery(),["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,shippingRepositoryMock) >> []
			
		when:
			def results = testObj.shippingCostForGiftCard(siteId, shippingMethod)
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "shippingCostForGiftCard when repository item gives price as null" (){
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setShippingCostRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and giftCard=1")
	
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
	
			testObj.executeRQLQuery(testObj.getShippingCostRqlQuery(),["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,shippingRepositoryMock) >> repositoryItemList
			item1.setProperties(["price" : null])
		when:
			def results = testObj.shippingCostForGiftCard(siteId, shippingMethod)
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	/* shippingCostForGiftCard - Test Cases ENDS */
	
	/* getBopusDisabledStates - Test Cases START */
	
	def "getBopusDisabledStates when passing values" (){
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery("bopus=0 or bopus is null",BBBCatalogConstants.STATE_ITEM_DESCRIPTOR, shippingRepositoryMock) >> repositoryItemList
			
			item1.setRepositoryId("3456")
			item1.setProperties(["descrip" : "Vermont"])
			
		when:
			def List<String> results = testObj.getBopusDisabledStates()
		
		then:
			results.size() > 0
			results[0].contains("Vermont")
	}
	
	def "getBopusDisabledStates when Repository Exception occurs" (){
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery("bopus=0 or bopus is null",BBBCatalogConstants.STATE_ITEM_DESCRIPTOR, shippingRepositoryMock) >> {throw new RepositoryException("Mock Repository Exception")}
			
		when:
			def List<String> results = testObj.getBopusDisabledStates()
		
		then:
			results == null
			thrown BBBSystemException
	}
	
	def "getBopusDisabledStates when repository item returns null" (){
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery("bopus=0 or bopus is null",BBBCatalogConstants.STATE_ITEM_DESCRIPTOR, shippingRepositoryMock) >> null
		
		when:
			def List<String> results = testObj.getBopusDisabledStates()
		
		then:
			results == null
			thrown BBBBusinessException
	}
	
	def "getBopusDisabledStates when item returns null" (){
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [null]
			testObj.executeRQLQuery("bopus=0 or bopus is null",BBBCatalogConstants.STATE_ITEM_DESCRIPTOR, shippingRepositoryMock) >> repositoryItemList
			
		when:
			def List<String> results = testObj.getBopusDisabledStates()
		
		then:
			results.size() == 0
	}
	
	/* getBopusDisabledStates - Test Cases ENDS */
	
	/* getShippingFee - Test Cases STARTS */
	
	def "getShippingFee when passing all parameters" (){
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setShippingFeeRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and state= ?2")
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingFeeRqlQuery(),["SDD", "BEDBATH_US", "NJ"],BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,shippingRepositoryMock) >> repositoryItemList
			
			Double lowerLimitValue = new Double(3.0f)
			Double upperLimitValue = new Double(30.0f)
			Double priceValue = new Double(90.0f)
			testObj.setSddShipMethodId("SDD")
			
			Set<RepositoryItem> regionsRepositoryItem = new HashSet<RepositoryItem>()
			regionsRepositoryItem.add(repositoryItemMock)
			
			repositoryItemMock.getRepositoryId() >> "3652"
			item1.setProperties(["lowerLimit" : lowerLimitValue , "upperLimit" : upperLimitValue, "price" : priceValue, "regions" : regionsRepositoryItem])
			
		when:
			def results	= testObj.getShippingFee(siteId, "SDD", "NJ", 25.0, "3652")
		
		then:
		
		results == 90.0
		
	}
	
	def "getShippingFee when stateId passed as empty" (){
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setShippingFeeRqlQueryForNullState("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and state is null")
	
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingFeeRqlQueryForNullState(),["SDD", "BEDBATH_US", ""],BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,shippingRepositoryMock) >> repositoryItemList
			
			Double priceValue = new Double(90.0f)
			testObj.setSddShipMethodId("SD")
			
			Set<RepositoryItem> regionsRepositoryItem = new HashSet<RepositoryItem>()
			regionsRepositoryItem.add(repositoryItemMock)
			
			repositoryItemMock.getRepositoryId() >> "3652"
			item1.setProperties(["lowerLimit" : null , "upperLimit" : null, "price" : priceValue, "regions" : regionsRepositoryItem])
		
		when:
			def results	= testObj.getShippingFee(siteId, "SDD", "", 0.0, "3652")
		
		then:
		
		results == 90.0
		
	}
	
	def "getShippingFee when stateId passed as null" (){
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setShippingFeeRqlQueryForNullState("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and state is null")
	
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingFeeRqlQueryForNullState(),["SDD", "BEDBATH_US", null],BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,shippingRepositoryMock) >> null
			
		when:
			def results	= testObj.getShippingFee(siteId, "SDD", null, 25.0, "3652")
		
		then:
		
			results == null
			thrown BBBBusinessException
	}
	
	def "getShippingFee when repository item passed as null" (){
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
	
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(*_) >> null
		
		when:
			def results	= testObj.getShippingFee(siteId, "SDD", "NJ", 25.0, "3652")
		
		then:
			
			results == null
			thrown BBBBusinessException
	}
	
	def "getShippingFee when siteId passed as null" (){
		given:
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			
		when:
			def results	= testObj.getShippingFee(null, "SDD", "NJ", 25.0, "3652")
		
		then:
			results == 0.0
		
	}
	
	def "getShippingFee when siteId passed as empty" (){
		given:
		
		testObj = Spy()
		testObj.setShippingRepository(shippingRepositoryMock)

		when:
			def results	= testObj.getShippingFee("", "SDD", "NJ", 25.0, "3652")
		
		then:
		
		results == 0.0
		
	}
	
	def "getShippingFee when shippingMethodId passed as null" (){
		given:
		
		testObj = Spy()
		testObj.setShippingRepository(shippingRepositoryMock)

		when:
			def results	= testObj.getShippingFee(siteId, null, "NJ", 25.0, "3652")
		
		then:
		
		results == 0.0
		
	}
	
	def "getShippingFee when shippingMethodId passed as empty" (){
		given:
		
		testObj = Spy()
		testObj.setShippingRepository(shippingRepositoryMock)

		when:
			def results	= testObj.getShippingFee(siteId,"", "NJ", 25.0, "3652")
		
		then:
		
		results == 0.0
		
	}
	
	def "getShippingFee when repositoryItem returns length 0" (){
		given:
		
		testObj = Spy()
		testObj.setShippingRepository(shippingRepositoryMock)

		def RepositoryItemMock item1 = new RepositoryItemMock()
		def RepositoryItem[] repositoryItemList = [item1]
		testObj.executeRQLQuery(*_) >> []
		
		when:
			def results	= testObj.getShippingFee(siteId, "SDD", null, 25.0, "3652")
		
		then:
		
		results == null
		thrown BBBBusinessException
		
	}
	
	def "getShippingFee when regionId and regionIdFromOrder is different" (){
		given:
		
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setShippingFeeRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and state= ?2")
	
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingFeeRqlQuery(),["SDD", "BEDBATH_US", "NJ"],BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,shippingRepositoryMock) >> repositoryItemList
			
			Double lowerLimitValue = new Double(3.0f)
			Double upperLimitValue = new Double(30.0f)
			Double priceValue = new Double(90.0f)
			testObj.setSddShipMethodId("SDD")
			
			Set<RepositoryItem> regionsRepositoryItem = new HashSet<RepositoryItem>()
			regionsRepositoryItem.add(repositoryItemMock)
			
			
			repositoryItemMock.getRepositoryId() >> "365"
			item1.setProperties(["lowerLimit" : lowerLimitValue , "upperLimit" : upperLimitValue, "price" : priceValue, "regions" : regionsRepositoryItem])
		
		when:
			def results	= testObj.getShippingFee(siteId, "SDD", "NJ", 25.0, "3652")
		
		then:
		
		results == 0.0
		
	}
	
	def "getShippingFee when regions item is null" (){
		given:
		
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setShippingFeeRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and state= ?2")
	
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingFeeRqlQuery(),["SDD", "BEDBATH_US", "NJ"],BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,shippingRepositoryMock) >> repositoryItemList
			
			Double lowerLimitValue = new Double(3.0f)
			Double upperLimitValue = new Double(30.0f)
			Double priceValue = new Double(90.0f)
			testObj.setSddShipMethodId("SDD")
			
			repositoryItemMock.getRepositoryId() >> "365"
			item1.setProperties(["lowerLimit" : lowerLimitValue , "upperLimit" : upperLimitValue, "price" : priceValue, "regions" : null])
			
		when:
			def results	= testObj.getShippingFee(siteId, "SDD", "NJ", 25.0, "3652")
		
		then:
		
		results == 0.0
		
	}
	
	def "getShippingFee when regionIdFromOrder passed as empty" (){
		given:
		
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setShippingFeeRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and state= ?2")
	
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			
			testObj.executeRQLQuery(testObj.getShippingFeeRqlQuery(),["SDD", "BEDBATH_US", "NJ"],BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,shippingRepositoryMock) >> repositoryItemList
			
			Set<RepositoryItem> regionsRepositoryItem = new HashSet<RepositoryItem>()
			regionsRepositoryItem.add(repositoryItemMock)
			
			Double lowerLimitValue = new Double(3.0f)
			Double upperLimitValue = new Double(30.0f)
			Double priceValue = new Double(90.0f)
			testObj.setSddShipMethodId("SDD")
			
			repositoryItemMock.getRepositoryId() >> "365"
			item1.setProperties(["lowerLimit" : lowerLimitValue , "upperLimit" : upperLimitValue, "price" : priceValue, "regions" : regionsRepositoryItem])
		
		when:
			def results	= testObj.getShippingFee(siteId, "SDD", "NJ", 25.0, "")
		
		then:
		
		results == 90.0
		
	}
	
	def "getShippingFee when subTotalAmount is greater than upperLimit value" (){
		given:
		
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setShippingFeeRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and state= ?2")
	
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingFeeRqlQuery(),["SDD", "BEDBATH_US", "NJ"],BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,shippingRepositoryMock) >> repositoryItemList
			
			Set<RepositoryItem> regionsRepositoryItem = new HashSet<RepositoryItem>()
			regionsRepositoryItem.add(repositoryItemMock)
			
			Double lowerLimitValue = new Double(3.0f)
			Double upperLimitValue = new Double(20.0f)
			Double priceValue = new Double(90.0f)
			testObj.setSddShipMethodId("SDD")
			
			repositoryItemMock.getRepositoryId() >> "365"
			item1.setProperties(["lowerLimit" : lowerLimitValue , "upperLimit" : upperLimitValue, "price" : priceValue, "regions" : regionsRepositoryItem])
			
		when:
			def results	= testObj.getShippingFee(siteId, "SDD", "NJ", 25.0, "3652")
		
		then:
		
		results == 0.0
		
	}
	
	def "getShippingFee when repsoitory item property returns null" (){
		given:
		
			testObj = Spy()
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setShippingFeeRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1 and state= ?2")
	
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItemMock item2 = new RepositoryItemMock()
			def RepositoryItemMock item3 = new RepositoryItemMock()
			def RepositoryItemMock item4 = new RepositoryItemMock()
			
			def RepositoryItem[] repositoryItemList = [item1,item2,item3,item4]
			testObj.executeRQLQuery(testObj.getShippingFeeRqlQuery(),["SDD", "BEDBATH_US", "NJ"],BBBCatalogConstants.SHIPPING_METHOD_PRICES_ITEM_DESCRIPTOR,shippingRepositoryMock) >> repositoryItemList
			
			Double lowerLimitValue = new Double(3.0f)
			Double upperLimitValue = new Double(10.0f)
			Double lowerLimitValue1= new Double(76.0f)
			Double upperLimitValue1 = new Double(45.0f)
			Double lowerLimitValue2 = new Double(3.0f)
			Double upperLimitValue2 = new Double(45.0f)
			Double priceValue = new Double(90.0f)
			
			testObj.setSddShipMethodId("SDD")
			
			Set<RepositoryItem> regionsRepositoryItem = new HashSet<RepositoryItem>()
			regionsRepositoryItem.add(repositoryItemMock)
			
			repositoryItemMock.getRepositoryId() >> "3652"
			
			item1.setProperties(["lowerLimit" : lowerLimitValue , "upperLimit" : upperLimitValue, "price" : priceValue, "regions" : regionsRepositoryItem])
			item2.setProperties(["lowerLimit" : lowerLimitValue1 , "upperLimit" : upperLimitValue1, "price" : priceValue, "regions" : regionsRepositoryItem])
			item3.setProperties(["lowerLimit" : lowerLimitValue2 , "upperLimit" : upperLimitValue2, "price" : null, "regions" : regionsRepositoryItem])
			item4.setProperties(["lowerLimit" : lowerLimitValue2 , "upperLimit" : upperLimitValue2, "price" : priceValue, "regions" : regionsRepositoryItem])
			
		when:
			def results	= testObj.getShippingFee(siteId, "SDD", "NJ", 25.0, "3652")
		
		then:
		
		results == 90.0
		
	}
	
	/* getShippingFee - Test Cases ENDS */
	
	/* getExpectedDeliveryTimeVDC - Test Cases STARTS */
	
	def "getExpectedDeliveryTimeVDC by passing all parameters" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS) >> 1
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS) >> 4
			
			siteRepositoryMock.getItem("BEDBATH_US",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbWeekendsRepositoryItem = new HashSet<RepositoryItem>()
			bbbWeekendsRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_WEEKENDS_SITE_PROPERTY_NAME) >> bbbWeekendsRepositoryItem
			repositoryItemMock.getPropertyValue("weekendDays") >> "SUNDAY"
			
			siteRepositoryMock.getItem("BEDBATH_US", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbHolidayRepositoryItem = new HashSet<RepositoryItem>()
			bbbHolidayRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_HOLIDAYS_SITE_PROPERTY_NAME) >> bbbHolidayRepositoryItem
			repositoryItemMock.getPropertyValue("holidayDate") >> new Date()
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingDurationRqlQuery(), ["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> repositoryItemList
			
			Date cutOffTimeDate = new Date()
			
			item1.setProperties(["minDaysToShipVDC" : 3, "maxDaysToShipVDC" : 8 , "cutOffTime" : cutOffTimeDate ])
			
		
		when:
			def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", true, new Date(), false, false)	
		
		then:
			results != null
		
	}
	
	def "getExpectedDeliveryTimeVDC when Repository Exception occurs" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS) >> 1
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS) >> 4
			
			siteRepositoryMock.getItem("BEDBATH_US",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbWeekendsRepositoryItem = new HashSet<RepositoryItem>()
			bbbWeekendsRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_WEEKENDS_SITE_PROPERTY_NAME) >> bbbWeekendsRepositoryItem
			repositoryItemMock.getPropertyValue("weekendDays") >> "SUNDAY"
			
			siteRepositoryMock.getItem("BEDBATH_US", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbHolidayRepositoryItem = new HashSet<RepositoryItem>()
			bbbHolidayRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_HOLIDAYS_SITE_PROPERTY_NAME) >> bbbHolidayRepositoryItem
			repositoryItemMock.getPropertyValue("holidayDate") >> new Date()
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingDurationRqlQuery(), ["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> {throw new RepositoryException("Mock Repository Exception")}
			
			Date cutOffTimeDate = new Date()
			
			item1.setProperties(["minDaysToShipVDC" : 3, "maxDaysToShipVDC" : 8 , "cutOffTime" : cutOffTimeDate ])
			
		
		when:
			def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", true, new Date(), false, false)
		
		then:
			results == null
			thrown BBBSystemException
		
	}
	
	def "getExpectedDeliveryTimeVDC when weekEndDays gives null" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS) >> 1
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS) >> 4
			
			siteRepositoryMock.getItem("BEDBATH_US",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			testObj.getWeekEndDays(*_) >> null
			
			siteRepositoryMock.getItem("BEDBATH_US", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbHolidayRepositoryItem = new HashSet<RepositoryItem>()
			bbbHolidayRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_HOLIDAYS_SITE_PROPERTY_NAME) >> bbbHolidayRepositoryItem
			repositoryItemMock.getPropertyValue("holidayDate") >> new Date()
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingDurationRqlQuery(), ["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> repositoryItemList
			
			Date cutOffTimeDate = new Date()
			
			item1.setProperties(["minDaysToShipVDC" : 3, "maxDaysToShipVDC" : 8 , "cutOffTime" : cutOffTimeDate ])
			
		
		when:
			def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", true, new Date(), false, false)	
		
		then:
			results != null
	}
	
	def "getExpectedDeliveryTimeVDC when getHolidayList gives null" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS) >> 1
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS) >> 4
			
			siteRepositoryMock.getItem("BEDBATH_US",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbWeekendsRepositoryItem = new HashSet<RepositoryItem>()
			bbbWeekendsRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_WEEKENDS_SITE_PROPERTY_NAME) >> bbbWeekendsRepositoryItem
			repositoryItemMock.getPropertyValue("weekendDays") >> "SUNDAY"
			
			siteRepositoryMock.getItem("BEDBATH_US", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			testObj.getHolidayList(*_) >> null
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingDurationRqlQuery(), ["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> repositoryItemList
			
			Date cutOffTimeDate = new Date()
			
			item1.setProperties(["minDaysToShipVDC" : 3, "maxDaysToShipVDC" : 8 , "cutOffTime" : cutOffTimeDate ])
			
		
		when:
			def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", true, new Date(), false, false)
		
		then:
			results != null
	}
	
	def "getExpectedDeliveryTimeVDC when minDays lt 14 and requireMsgInDate is false" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS) >> 1
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS) >> 4
			
			siteRepositoryMock.getItem("BEDBATH_US",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbWeekendsRepositoryItem = new HashSet<RepositoryItem>()
			bbbWeekendsRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_WEEKENDS_SITE_PROPERTY_NAME) >> bbbWeekendsRepositoryItem
			repositoryItemMock.getPropertyValue("weekendDays") >> "SUNDAY"
			
			siteRepositoryMock.getItem("BEDBATH_US", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbHolidayRepositoryItem = new HashSet<RepositoryItem>()
			bbbHolidayRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_HOLIDAYS_SITE_PROPERTY_NAME) >> bbbHolidayRepositoryItem
			repositoryItemMock.getPropertyValue("holidayDate") >> new Date()
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingDurationRqlQuery(), ["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> repositoryItemList
			Date cutOffTimeDate = new Date()
			
			lblTxtTemplateManagerMock.getPageLabel(*_) >> "someValue"
			
			item1.setProperties(["minDaysToShipVDC" : 3, "maxDaysToShipVDC" : 8 , "cutOffTime" : cutOffTimeDate ])
		
		when:
			def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", false, new Date(), false, true)
		
		then:
			results == "someValue"
	}
	
	def "getExpectedDeliveryTimeVDC when minDays gt 14 and requireMsgInDate is false" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			testObj.setLblTxtTemplateManager(lblTxtTemplateManagerMock)
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS) >> 8
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS) >> 4
			
			siteRepositoryMock.getItem("BEDBATH_US",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbWeekendsRepositoryItem = new HashSet<RepositoryItem>()
			bbbWeekendsRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_WEEKENDS_SITE_PROPERTY_NAME) >> bbbWeekendsRepositoryItem
			repositoryItemMock.getPropertyValue("weekendDays") >> "SUNDAY"
			
			siteRepositoryMock.getItem("BEDBATH_US", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbHolidayRepositoryItem = new HashSet<RepositoryItem>()
			bbbHolidayRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_HOLIDAYS_SITE_PROPERTY_NAME) >> bbbHolidayRepositoryItem
			repositoryItemMock.getPropertyValue("holidayDate") >> new Date()
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingDurationRqlQuery(), ["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> repositoryItemList
			Date cutOffTimeDate = new Date()
			
			lblTxtTemplateManagerMock.getPageLabel(*_) >> "someValue"
			
			item1.setProperties(["minDaysToShipVDC" : 8, "maxDaysToShipVDC" : 8 , "cutOffTime" : cutOffTimeDate ])
		
		when:
			def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", false, new Date(), false, true)
		
		then:
			results == "someValue"
	}
	
	def "getExpectedDeliveryTimeVDC when getHolidayList returns size of 0" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS) >> 1
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS) >> 4
			
			siteRepositoryMock.getItem("BEDBATH_US",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbWeekendsRepositoryItem = new HashSet<RepositoryItem>()
			bbbWeekendsRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_WEEKENDS_SITE_PROPERTY_NAME) >> bbbWeekendsRepositoryItem
			repositoryItemMock.getPropertyValue("weekendDays") >> "SUNDAY"
			
			siteRepositoryMock.getItem("BEDBATH_US", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<Date> newDate = new HashSet<Date>()
			testObj.getHolidayList(*_) >> newDate
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingDurationRqlQuery(), ["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> repositoryItemList
			
			Date cutOffTimeDate = new Date()
			
			item1.setProperties(["minDaysToShipVDC" : 3, "maxDaysToShipVDC" : 8 , "cutOffTime" : cutOffTimeDate ])
			
		
		when:
			def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", true, new Date(), false, false)
		
		then:
			results != null
	}
	
	def "getExpectedDeliveryTimeVDC when repository item by ShippingDurationRqlQuery gives null" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			testObj.setShippingMethodRqlQuery("id EQUALS IGNORECASE ?0")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS) >> 1
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS) >> 4
			
			siteRepositoryMock.getItem("BEDBATH_US",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbWeekendsRepositoryItem = new HashSet<RepositoryItem>()
			bbbWeekendsRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_WEEKENDS_SITE_PROPERTY_NAME) >> bbbWeekendsRepositoryItem
			repositoryItemMock.getPropertyValue("weekendDays") >> ""
			
			siteRepositoryMock.getItem("BEDBATH_US", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbHolidayRepositoryItem = new HashSet<RepositoryItem>()
			bbbHolidayRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_HOLIDAYS_SITE_PROPERTY_NAME) >> bbbHolidayRepositoryItem
			repositoryItemMock.getPropertyValue("holidayDate") >> new Date()
			
			
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingDurationRqlQuery(), ["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> null
			
			testObj.executeRQLQuery(testObj.getShippingMethodRqlQuery(), ["LWA"],BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> repositoryItemList
			
			Date cutOffTimeDate = new Date()
			
			item1.setProperties(["minDaysToShipVDC" : 3, "maxDaysToShipVDC" : 8 , "cutOffTime" : cutOffTimeDate ])
			
		
		when:
			def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", true, null, false, false)
		
		then:
			results != null
		
	}
	
	def "getExpectedDeliveryTimeVDC when minDaysToShipVDC of ShippingDurationRqlQuery gives null" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			testObj.setShippingMethodRqlQuery("id EQUALS IGNORECASE ?0")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS) >> 1
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS) >> 4
			
			siteRepositoryMock.getItem("BEDBATH_US",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbWeekendsRepositoryItem = new HashSet<RepositoryItem>()
			bbbWeekendsRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_WEEKENDS_SITE_PROPERTY_NAME) >> bbbWeekendsRepositoryItem
			repositoryItemMock.getPropertyValue("weekendDays") >> ""
			
			siteRepositoryMock.getItem("BEDBATH_US", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbHolidayRepositoryItem = new HashSet<RepositoryItem>()
			bbbHolidayRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_HOLIDAYS_SITE_PROPERTY_NAME) >> bbbHolidayRepositoryItem
			repositoryItemMock.getPropertyValue("holidayDate") >> new Date()
			
			
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingDurationRqlQuery(), ["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> null
			
			testObj.executeRQLQuery(testObj.getShippingMethodRqlQuery(), ["LWA"],BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> repositoryItemList
			
			Date cutOffTimeDate = new Date()
			
			item1.setProperties(["minDaysToShipVDC" : null, "maxDaysToShipVDC" : 8 , "cutOffTime" : cutOffTimeDate ])
			
		
		when:
			def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", true, null, false, false)
		
		then:
			results != null
		
	}
	
	def "getExpectedDeliveryTimeVDC when maxDaysToShipVDC of ShippingDurationRqlQuery gives null" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			testObj.setShippingMethodRqlQuery("id EQUALS IGNORECASE ?0")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS) >> 4
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS) >> 0
			
			siteRepositoryMock.getItem("BEDBATH_US",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbWeekendsRepositoryItem = new HashSet<RepositoryItem>()
			bbbWeekendsRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_WEEKENDS_SITE_PROPERTY_NAME) >> bbbWeekendsRepositoryItem
			repositoryItemMock.getPropertyValue("weekendDays") >> ""
			
			siteRepositoryMock.getItem("BEDBATH_US", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbHolidayRepositoryItem = new HashSet<RepositoryItem>()
			bbbHolidayRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_HOLIDAYS_SITE_PROPERTY_NAME) >> bbbHolidayRepositoryItem
			repositoryItemMock.getPropertyValue("holidayDate") >> new Date()
			
			
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingDurationRqlQuery(), ["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> null
			
			testObj.executeRQLQuery(testObj.getShippingMethodRqlQuery(), ["LWA"],BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> repositoryItemList
			
			Date cutOffTimeDate = new Date()
			
			item1.setProperties(["minDaysToShipVDC" : 3, "maxDaysToShipVDC" : null , "cutOffTime" : cutOffTimeDate ])
			
		
		when:
			def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", true, null, false, false)
		
		then:
			results != null
		
	}
	
	def "getExpectedDeliveryTimeVDC when fromshippingpage giving true" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			testObj.setShippingMethodRqlQuery("id EQUALS IGNORECASE ?0")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			
		when:
			def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", false, null, false, true)
		
		then:
			results == null
			thrown BBBSystemException
		
	}
	
	def "getExpectedDeliveryTimeVDC when isCustomizationOfferedForSKU returns false" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			testObj.setShippingMethodRqlQuery("id EQUALS IGNORECASE ?0")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> false
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			
		when:
			def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", true, null, false, false)
		
		then:
			results == null
			thrown BBBSystemException
		
	}
	
	def "getExpectedDeliveryTimeVDC when ltl and customizationOfferedsku is false" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			testObj.setShippingMethodRqlQuery("id EQUALS IGNORECASE ?0")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> false
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> false
			
		when:
			def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", false, null, false, false)
		
		then:
			results.equals("")
	}
	
	def "getExpectedDeliveryTimeVDC when catalog repository item returns null" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			testObj.setShippingMethodRqlQuery("id EQUALS IGNORECASE ?0")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> null
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			
		when:
			def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", false, null, false, false)
		
		then:
			results == ""
		
	}
	
	def "getExpectedDeliveryTimeVDC when ltl sku returns null" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS) >> null
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS) >> null
			
			siteRepositoryMock.getItem("BEDBATH_US",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbWeekendsRepositoryItem = new HashSet<RepositoryItem>()
			bbbWeekendsRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_WEEKENDS_SITE_PROPERTY_NAME) >> bbbWeekendsRepositoryItem
			repositoryItemMock.getPropertyValue("weekendDays") >> "SUNDAY"
			
			siteRepositoryMock.getItem("BEDBATH_US", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbHolidayRepositoryItem = new HashSet<RepositoryItem>()
			bbbHolidayRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_HOLIDAYS_SITE_PROPERTY_NAME) >> bbbHolidayRepositoryItem
			repositoryItemMock.getPropertyValue("holidayDate") >> new Date()
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingDurationRqlQuery(), ["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> repositoryItemList
			
			Date cutOffTimeDate = new Date()
			
			item1.setProperties(["minDaysToShipVDC" : 3, "maxDaysToShipVDC" : 8 , "cutOffTime" : cutOffTimeDate ])
		
	
	when:
		def results = testObj.getExpectedDeliveryTimeVDC("", "2883993", true, new Date(), false, false)
	
	then:
		results != null
		
	}
	
	def "getExpectedDeliveryTimeVDC when minDaysToShipVDC is null" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS) >> 1
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS) >> null
			
			siteRepositoryMock.getItem("BEDBATH_US",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbWeekendsRepositoryItem = new HashSet<RepositoryItem>()
			bbbWeekendsRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_WEEKENDS_SITE_PROPERTY_NAME) >> bbbWeekendsRepositoryItem
			repositoryItemMock.getPropertyValue("weekendDays") >> "SUNDAY"
			
			siteRepositoryMock.getItem("BEDBATH_US", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbHolidayRepositoryItem = new HashSet<RepositoryItem>()
			bbbHolidayRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_HOLIDAYS_SITE_PROPERTY_NAME) >> bbbHolidayRepositoryItem
			repositoryItemMock.getPropertyValue("holidayDate") >> new Date()
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingDurationRqlQuery(), ["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> repositoryItemList
			
			Date cutOffTimeDate = new Date()
			
			item1.setProperties(["minDaysToShipVDC" : null, "maxDaysToShipVDC" : 8 , "cutOffTime" : cutOffTimeDate ])
			
	
	when:
		def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", true, new Date(), false, false)
	
	then:
		results != null
		
	}
	
	def "getExpectedDeliveryTimeVDC when maxDaysToShipVDC is null" (){
		given:
			testObj = Spy()
			
			testObj.getCurrentSiteId() >> "BEDBATH_US"
			
			testObj.setShippingRepository(shippingRepositoryMock)
			testObj.setCatalogRepository(catalogRepositoryMock)
			testObj.setGlobalRepositoryTools(globalRepositoryToolsMock)
			testObj.setSiteRepository(siteRepositoryMock)
			testObj.setShippingDurationRqlQuery("shipMethodCode EQUALS IGNORECASE ?0 and site = ?1")
			
			catalogRepositoryMock.getItem("2883993", BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> repositoryItemMock
			globalRepositoryToolsMock.isCustomizationOfferedForSKU(repositoryItemMock, "BEDBATH_US") >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> true
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MIN_SHIPPING_DAYS) >> 1
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.MAX_SHIPPING_DAYS) >> null
			
			siteRepositoryMock.getItem("BEDBATH_US",BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbWeekendsRepositoryItem = new HashSet<RepositoryItem>()
			bbbWeekendsRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_WEEKENDS_SITE_PROPERTY_NAME) >> bbbWeekendsRepositoryItem
			repositoryItemMock.getPropertyValue("weekendDays") >> "SUNDAY"
			
			siteRepositoryMock.getItem("BEDBATH_US", BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> repositoryItemMock
			Set<RepositoryItem> bbbHolidayRepositoryItem = new HashSet<RepositoryItem>()
			bbbHolidayRepositoryItem.add(repositoryItemMock)
			repositoryItemMock.getPropertyValue(BBBCatalogConstants.BBB_HOLIDAYS_SITE_PROPERTY_NAME) >> bbbHolidayRepositoryItem
			repositoryItemMock.getPropertyValue("holidayDate") >> new Date()
			
			def RepositoryItemMock item1 = new RepositoryItemMock()
			def RepositoryItem[] repositoryItemList = [item1]
			testObj.executeRQLQuery(testObj.getShippingDurationRqlQuery(), ["LWA", "BEDBATH_US"],BBBCatalogConstants.SHIPPING_DURATIONS_ITEM_DESCRIPTOR,testObj.getShippingRepository()) >> repositoryItemList
			
			Date cutOffTimeDate = new Date()
			
			item1.setProperties(["minDaysToShipVDC" : 4, "maxDaysToShipVDC" : null , "cutOffTime" : cutOffTimeDate ])
			
		
	when:
		def results = testObj.getExpectedDeliveryTimeVDC(shippingMethod, "2883993", true, new Date(), false, false)
	
	then:
		results != null
		
	}
	
	/* getExpectedDeliveryTimeVDC - Test Cases ENDS */
	
	/* getOffsetDays - Test Cases START */
	
	def "getOffsetDays when passing parameters" (){
		given:
			
			def Calendar calenderMock = Mock()
			Set<Integer> weekEndDays = new HashSet<Integer>()
			weekEndDays.add("SUNDAY")
			
			Set<Date> holidayList = new HashSet<Date>()
			holidayList.add(new Date())
			
			def cutOffDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 1, 23, 45).time
			
		when:
			def results = testObj.getOffsetDays(1, calenderMock, weekEndDays, holidayList, cutOffDate)
		
		then:
			results != null
		
	}
	
	def "getOffsetDays when calCutOffTime hour gt calCurrentDate" (){
		given:
			
			def Calendar calenderMock = Mock()
			Set<Integer> weekEndDays = new HashSet<Integer>()
			weekEndDays.add(4)
			
			Set<Date> holidayList = new HashSet<Date>()
			holidayList.add(new Date())
			
			def cutOffDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 23, 59, 45).time
			
		when:
			def results = testObj.getOffsetDays(1, calenderMock, weekEndDays, holidayList, cutOffDate)
		
		then:
			results != null
	}
	
	def "getOffsetDays when cutoffDate is null" (){
		given:
			
			def Calendar calenderMock = Mock()
			Set<Integer> weekEndDays = new HashSet<Integer>()
			weekEndDays.add(3)
			
			Set<Date> holidayList = new HashSet<Date>()
			holidayList.add(new Date())
			
			def cutOffDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 23, 59, 45).time
			
		when:
			def results = testObj.getOffsetDays(1, calenderMock, weekEndDays, holidayList, null)
		
		then:
			results != null
	}
	
	def "getOffsetDays when holidaylist returns false" (){
		given:
			
			def Calendar calenderMock = Mock()
			Set<Integer> weekEndDays = new HashSet<Integer>()
			weekEndDays.add("SUNDAY")
			
			Set<Date> holidayList = new HashSet<Date>()
			def holiday = new GregorianCalendar(2016, Calendar.AUGUST, 10, 2, 23, 45).time
			holidayList.add(holiday)
			
			def cutOffDate = new GregorianCalendar(2016, Calendar.AUGUST, 3, 2, 23, 45).time
			
		when:
			def results = testObj.getOffsetDays(1, calenderMock, weekEndDays, holidayList, cutOffDate)
		
		then:
			results != null
		
	}
	
	/* getOffsetDays - Test Cases ENDS */
	
	
}
