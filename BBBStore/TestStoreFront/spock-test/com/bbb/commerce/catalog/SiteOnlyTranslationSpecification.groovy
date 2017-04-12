package com.bbb.commerce.catalog

//import static org.junit.Assert

import spock.lang.specification.BBBExtendedSpec
import atg.nucleus.Nucleus
import atg.nucleus.registry.NucleusRegistry
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemDescriptor
import atg.repository.RepositoryItemImpl
import atg.repository.RepositoryPropertyDescriptor
import atg.repository.dp.Derivation
import atg.repository.dp.DerivedPropertyDescriptor
import atg.repository.dp.PropertyExpression
import atg.servlet.ServletUtil

import com.bbb.constants.BBBCoreConstants
import com.bbb.repository.RepositoryItemMock
import com.bbb.utils.BBBJobContextManager

/**
 *
 * @author Velmurugan Moorthy
 *
 * Changes made in Java file : - Extracing methods and adding utility method's null check like isEmpty(), isCollectionEmpty() etc.,
 * 
 * #162 - if (BBBUtility.isListEmpty((exps)) 
 * #208 - if(BBBUtility.isEmpty(siteId))
 * #248 - if(BBBUtility.isMapNullOrEmpty(translations))
 * #287 - getCurrentSiteId()- method extracted (created)
 * #238 - if(BBBUtility.isEmpty(itemType))
 * 
 */

class SiteOnlyTranslationSpecification extends BBBExtendedSpec {

	private Nucleus nucleusMock
	private Derivation derivation
	private SiteOnlyTranslation siteOnlyTranslationSpy
	BBBJobContextManager cmMock  =Mock()
	
	def setup() {
		
		derivation = Mock()
		NucleusRegistry sNucleusRegistry = Mock()
		
		nucleusMock = Mock()
		Nucleus.setNucleusRegistry(sNucleusRegistry)
		sNucleusRegistry.getKey() >> "nKey"
		sNucleusRegistry.get("nKey") >> nucleusMock
		Nucleus.sUsingChildNucleii = true
		BBBJobContextManager.jobContextInstance =  cmMock
		siteOnlyTranslationSpy = Spy("mDefaultProperty" : "quantity")
		siteOnlyTranslationSpy.setDerivation(derivation)
	
	}
	
	/*
	 * derivePropertyValue - Test cases STARTS
	 * 
	 * Method signature : 
	 * 
	 * public Object derivePropertyValue(RepositoryItemImpl pItem)throws RepositoryException
	 *  
	 */
	
	def "derivePropertyValue - Retrieved the value for translation successfully" () {
		
		given :
		
		def siteId = "BedBathUS"
		def defaultSiteAttribute = ""
		def itemType = "inventory"
		def propertyName = "translations"
		def derivationPropertyName = "inventory"
		Long inventory = 20 
		def returnValue 
		def threadId = "1"
		def jobId = "job01"
		
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor repoPropertyDescriptor = Mock()
		RepositoryItemDescriptor repoItemDescriptor = Mock()
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		List propertyExpressions = new ArrayList<>()
		Map<String,RepositoryItem> translations = new TreeMap<>()
		
		Map<String, String> jobInfo = new HashMap<>()
		Map<String, Map<String, String>> jobContext  = new HashMap<>()
		Map<String, String> innerJobContextMap = new HashMap<>()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])

		jobInfo.put(threadId, jobId)
		innerJobContextMap.put(BBBCoreConstants.SITE_ID, siteId)
		jobContext.put(jobId, innerJobContextMap)
		
		repoPropertyDescriptor.getName() >> "InventoryProperty"
				
		PropertyExpression propertyExpression1 = new PropertyExpression(repoItemDescriptor, "quantity") // "quantity")
		
		propertyExpression1.setItemDescriptor(null)
		propertyExpression1.setPropertyDescriptor(repoPropertyDescriptor)
		propertyExpression1.evaluate(repoItemMock) >> "20"
		
		propertyExpressions.add(propertyExpression1)
		
		translationRepoItem1.setProperties(["inventory" : inventory])
		
		translations.put(siteId, translationRepoItem1)
		
		repoItemMock.getPropertyValue(propertyName) >> translations
		
		derivedPropDesc.getName() >> "inventory"
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_PROPERTY)
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		BBBJobContextManager.jobContext= jobContext
		BBBJobContextManager.jobInfo = jobInfo
		
		siteOnlyTranslationSpy.getDefaultSiteAttribute() >> defaultSiteAttribute
		siteOnlyTranslationSpy.setItemType(itemType)
		siteOnlyTranslationSpy.setDerivation(derivation)
		
		when : 
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then : 
		
		returnValue == inventory
	}

	def "derivePropertyValue - No property name exists | Request - DynamoHttpRequest - is invalid - null " () {
		
		given :
		
		def siteId = "BedBathUS"
		def defaultSiteAttribute = ""
		def itemType = "inventory"
		def propertyName = "translations"
		def derivationPropertyName = "inventory"
		Long inventory = 20
		def returnValue
		def propertyValue = "20"
		def threadId = "1"
		def jobId = "job01"
		
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor repoPropertyDescriptor = Mock()
		RepositoryItemDescriptor repoItemDescriptor = Mock()
		
		List propertyExpressions = new ArrayList<>()
		Map<String,RepositoryItem> translations = new TreeMap<>()
		
		Map<String, String> jobInfo = new HashMap<>()
		Map<String, String> innerJobContextMap = new HashMap<>()
		Map<String, Map<String, String>> jobContext  = new HashMap<>()
		
		jobInfo.put(siteId, "SampleJob")
		innerJobContextMap.put(BBBCoreConstants.SITE_ID, siteId)
		jobContext.put(jobId, innerJobContextMap)
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		
		jobContext.put(BBBCoreConstants.SITE_ID, innerJobContextMap)
		
		ServletUtil.setCurrentRequest(null)
		
		repoPropertyDescriptor.getName() >> null
				
		PropertyExpression propertyExpression1 = new PropertyExpression(repoItemDescriptor, "quantity") // "quantity")
		
		propertyExpression1.setItemDescriptor(null)
		propertyExpression1.setPropertyDescriptor(repoPropertyDescriptor)
		propertyExpression1.evaluate(repoItemMock) >> "20"
		
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_PROPERTY) >> "inventory"
		derivedPropDesc.getValue(SiteOnlyTranslation.ITEM_TYPE) >> itemType
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_SITE_ATTR) >> defaultSiteAttribute
		
		derivation.getPropertyDescriptor() >> derivedPropDesc
		
		repoItemMock.getPropertyValue(repoPropertyDescriptor) >> propertyValue
		repoItemMock.getPropertyValue(propertyName) >> translations
		repoItemMock.getPropertyValue(derivationPropertyName) >> propertyValue
		
		translationRepoItem1.setProperties(["quantity" : "20"])
		propertyExpressions.add(propertyExpression1)
		translations.put(siteId, translationRepoItem1)
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		BBBJobContextManager.jobContext= jobContext
		BBBJobContextManager.jobInfo = jobInfo
		
		siteOnlyTranslationSpy.getCurrentSiteId() >> ""
		
		when : 
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then : 
		
		returnValue == propertyValue
	}
	
	def "derivePropertyValue - Translation for the property has no value - null" () {
		
		given :
		
		def siteId = "BedBathUS"
		def defaultSiteAttribute = ""
		def itemType = "inventory"
		def propertyName = "translations"
		def derivationPropertyName = "inventory"
		Long inventory = 20
		def returnValue
		def threadId = "1"
		def jobId = "job01"
		
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor repoPropertyDescriptor = Mock()
		RepositoryItemDescriptor repoItemDescriptor = Mock()
		
		List exps = new ArrayList<>()
		List propertyExpressions = new ArrayList<>()
		Map<String,RepositoryItem> translations = new TreeMap<>()
		
		Map<String, String> jobInfo = new HashMap<>()
		Map<String, String> innerJobContextMap = new HashMap<>()
		Map<String, Map<String, String>> jobContext  = new HashMap<>()
		
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		
		jobInfo.put(siteId, "SampleJob")
		innerJobContextMap.put(BBBCoreConstants.SITE_ID, siteId)
		jobContext.put(jobId, innerJobContextMap)
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])

		requestMock.getAttribute(SiteOnlyTranslation.SITE_ID) >> siteId
		
		repoPropertyDescriptor.getName() >> "InventoryProperty"
				
		PropertyExpression propertyExpression1 = new PropertyExpression(repoItemDescriptor, "quantity") // "quantity")
		
		propertyExpression1.setItemDescriptor(null)
		propertyExpression1.setPropertyDescriptor(repoPropertyDescriptor)
		propertyExpression1.evaluate(repoItemMock) >> "20"
		
		translations.put(siteId, translationRepoItem1)
		
		repoItemMock.getPropertyValue(propertyName) >> translations
		propertyExpressions.add(propertyExpression1)
		
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_PROPERTY) >> "inventory"
		derivedPropDesc.getValue(SiteOnlyTranslation.ITEM_TYPE) >> itemType
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_SITE_ATTR) >> defaultSiteAttribute
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		BBBJobContextManager.jobContext= jobContext
		BBBJobContextManager.jobInfo = jobInfo
		
		when :
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == 0
	}
	
	def "derivePropertyValue - No translation available for the site - empty" () {
		
		given :
		
		def siteId = "BedBathUS"
		def defaultSiteAttribute = ""
		def itemType = "inventory"
		def propertyName = "translations"
		def derivationPropertyName = "inventory"
		Long inventory = 20
		def returnValue
		def threadId = "1"
		def jobId = "job01"
		
		Derivation derivation = Mock()
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor repoPropertyDescriptor = Mock()
		RepositoryItemDescriptor repoItemDescriptor = Mock()
		
		List exps = new ArrayList<>()
		List propertyExpressions = new ArrayList<>()
		Map<String,RepositoryItem> translations = new TreeMap<>()

		Map<String, String> jobInfo = new HashMap<>()
		Map<String, String> innerJobContextMap = new HashMap<>()
		Map<String, Map<String, String>> jobContext  = new HashMap<>()
		
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])

		requestMock.getAttribute(SiteOnlyTranslation.SITE_ID) >> siteId
		
		repoPropertyDescriptor.getName() >> "InventoryProperty"
				
		PropertyExpression propertyExpression1 = new PropertyExpression(repoItemDescriptor, "quantity") // "quantity")
		
		propertyExpression1.setItemDescriptor(null)
		propertyExpression1.setPropertyDescriptor(repoPropertyDescriptor)
		propertyExpression1.evaluate(repoItemMock) >> "20"
		
		propertyExpressions.add(propertyExpression1)
		
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_PROPERTY) >> "inventory"
		derivedPropDesc.getValue(SiteOnlyTranslation.ITEM_TYPE) >> itemType
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_SITE_ATTR) >> defaultSiteAttribute
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		BBBJobContextManager.jobContext= jobContext
		BBBJobContextManager.jobInfo = jobInfo
		
		translations.put(siteId, null)
		
		repoItemMock.getPropertyValue(propertyName) >> translations
		
		when :
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
	}
	
	def "derivePropertyValue - TBS BedBathUS reqeust | retrieved the value for translation successfully" () {
		
		given :
		
		def siteId = "TBS_BedBathUS"
		def defaultSiteAttribute = siteId
		def itemType = "inventory"
		def propertyName = "translations"
		def derivationPropertyName = "inventory"
		Long inventory = 20
		def returnValue
		def threadId = "1"
		def jobId = "job01"
		
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor repoPropertyDescriptor = Mock()
		RepositoryItemDescriptor repoItemDescriptor = Mock()
		
		List exps = new ArrayList<>()
		List propertyExpressions = new ArrayList<>()
		Map<String,RepositoryItem> translations = new TreeMap<>()
		
		Map<String, String> jobInfo = new HashMap<>()
		Map<String, String> innerJobContextMap = new HashMap<>()
		Map<String, Map<String, String>> jobContext  = new HashMap<>()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_PROPERTY) >> "inventory"
		derivedPropDesc.getValue(SiteOnlyTranslation.ITEM_TYPE) >> itemType
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_SITE_ATTR) >> defaultSiteAttribute

		requestMock.getAttribute(SiteOnlyTranslation.SITE_ID) >> siteId
		
		repoPropertyDescriptor.getName() >> "InventoryProperty"
		
		PropertyExpression propertyExpression1 = new PropertyExpression(repoItemDescriptor, "quantity") // "quantity")
		
		propertyExpression1.setItemDescriptor(null)
		propertyExpression1.setPropertyDescriptor(repoPropertyDescriptor)
		propertyExpression1.evaluate(repoItemMock) >> "20"
		
		propertyExpressions.add(propertyExpression1)
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		BBBJobContextManager.jobContext= jobContext
		BBBJobContextManager.jobInfo = jobInfo
		
		translationRepoItem1.setProperties(["inventory" : inventory])
		
		translations.put(siteId, translationRepoItem1)
		
		repoItemMock.getPropertyValue(propertyName) >> translations
		
		when :
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
	}
	
	def "derivePropertyValue - TBS BuyBuyBaby reqeust | retrieved the value for translation successfully" () {
		
		given :
		
		def siteId = "TBS_BuyBuyBaby"
		def defaultSiteAttribute = siteId
		def itemType = "inventory"
		def propertyName = "translations"
		def derivationPropertyName = "inventory"
		Long inventory = 20
		def returnValue
		def threadId = "1"
		def jobId = "job01"
		
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor repoPropertyDescriptor = Mock()
		RepositoryItemDescriptor repoItemDescriptor = Mock()
		
		List exps = new ArrayList<>()
		List propertyExpressions = new ArrayList<>()
		Map<String,RepositoryItem> translations = new TreeMap<>()
		
		Map<String, String> jobInfo = new HashMap<>()
		Map<String, String> innerJobContextMap = new HashMap<>()
		Map<String, Map<String, String>> jobContext  = new HashMap<>()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_PROPERTY) >> "inventory"
		derivedPropDesc.getValue(SiteOnlyTranslation.ITEM_TYPE) >> itemType
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_SITE_ATTR) >> defaultSiteAttribute
		
		requestMock.getAttribute(SiteOnlyTranslation.SITE_ID) >> siteId
		
		repoPropertyDescriptor.getName() >> "InventoryProperty"
				
		PropertyExpression propertyExpression1 = new PropertyExpression(repoItemDescriptor, "quantity") // "quantity")
		
		propertyExpression1.setItemDescriptor(null)
		propertyExpression1.setPropertyDescriptor(repoPropertyDescriptor)
		propertyExpression1.evaluate(repoItemMock) >> "20"
		
		propertyExpressions.add(propertyExpression1)
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		BBBJobContextManager.jobContext= jobContext
		BBBJobContextManager.jobInfo = jobInfo
		
		translationRepoItem1.setProperties(["inventory" : inventory])
		
		translations.put(siteId, translationRepoItem1)
		
		repoItemMock.getPropertyValue(propertyName) >> translations
		
		when :
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
	}
	
	def "derivePropertyValue - TBS BedBathCanada reqeust | retrieved the value for translation successfully" () {
		
		given :
		
		def siteId = "TBS_BedBathCanada"
		def defaultSiteAttribute = siteId
		def itemType = "inventory"
		def propertyName = "translations"
		def derivationPropertyName = "inventory"
		Long inventory = 20
		def returnValue
		def threadId = "1"
		def jobId = "job01"
		
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor repoPropertyDescriptor = Mock()
		RepositoryItemDescriptor repoItemDescriptor = Mock()
		
		List exps = new ArrayList<>()
		List propertyExpressions = new ArrayList<>()
		Map<String,RepositoryItem> translations = new TreeMap<>()
		
		Map<String, String> jobInfo = new HashMap<>()
		Map<String, String> innerJobContextMap = new HashMap<>()
		Map<String, Map<String, String>> jobContext  = new HashMap<>()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_PROPERTY) >> "inventory"
		derivedPropDesc.getValue(SiteOnlyTranslation.ITEM_TYPE) >> itemType
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_SITE_ATTR) >> defaultSiteAttribute

		requestMock.getAttribute(SiteOnlyTranslation.SITE_ID) >> siteId
		
		repoPropertyDescriptor.getName() >> "InventoryProperty"
				
		PropertyExpression propertyExpression1 = new PropertyExpression(repoItemDescriptor, "quantity") // "quantity")
		
		propertyExpression1.setItemDescriptor(null)
		propertyExpression1.setPropertyDescriptor(repoPropertyDescriptor)
		propertyExpression1.evaluate(repoItemMock) >> "20"
		
		propertyExpressions.add(propertyExpression1)
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		BBBJobContextManager.jobContext= jobContext
		BBBJobContextManager.jobInfo = jobInfo
		translationRepoItem1.setProperties(["inventory" : inventory])
		
		translations.put(siteId, translationRepoItem1)
		
		repoItemMock.getPropertyValue(propertyName) >> translations
		
		when :
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
	}
	
	def "derivePropertyValue - No property expressions found" () {
		
		given :
		
		RepositoryItemImpl repoItemMock = Mock()
		def returnValue
		def threadId = "1"
		def jobId = "job01"
		
		when :
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
	}
	
	def "derivePropertyValue - site ID has default value | site ID equals default site attribute" () {
		
		given :
		
		def siteId = "BedBathUS"
		def defaultSiteAttribute = siteId
		def itemType = "inventory"
		def propertyName = "translations"
		def derivationPropertyName = "inventory"
		Long inventory = 20
		def returnValue
		def threadId = "1"
		def jobId = "job01"
		
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor repoPropertyDescriptor = Mock()
		RepositoryItemDescriptor repoItemDescriptor = Mock()
		
		List exps = new ArrayList<>()
		List propertyExpressions = new ArrayList<>()
		
		Map<String, String> jobInfo = new HashMap<>()
		Map<String, String> innerJobContextMap = new HashMap<>()
		Map<String, Map<String, String>> jobContext  = new HashMap<>()
		
		Map<String,RepositoryItem> translations = new TreeMap<>()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_PROPERTY) >> "inventory"
		derivedPropDesc.getValue(SiteOnlyTranslation.ITEM_TYPE) >> itemType
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_SITE_ATTR) >> defaultSiteAttribute

		requestMock.getAttribute(SiteOnlyTranslation.SITE_ID) >> siteId
		
		repoPropertyDescriptor.getName() >> "InventoryProperty"
				
		PropertyExpression propertyExpression1 = new PropertyExpression(repoItemDescriptor, "quantity") // "quantity")
		
		propertyExpression1.setItemDescriptor(null)
		propertyExpression1.setPropertyDescriptor(repoPropertyDescriptor)
		propertyExpression1.evaluate(repoItemMock) >> "20"
		
		propertyExpressions.add(propertyExpression1)
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		BBBJobContextManager.jobContext= jobContext
		BBBJobContextManager.jobInfo = jobInfo
		translationRepoItem1.setProperties(["inventory" : inventory])
		
		translationRepoItem1.setProperties(["inventory" : inventory])
		
		translations.put(siteId, translationRepoItem1)
		
		repoItemMock.getPropertyValue(propertyName) >> translations
		
		when :
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
	}

	def "derivePropertyValue - Null pointer exception while accessing the item type" () {
		
		given :
		
		def siteId = "BedBathUS"
		def defaultSiteAttribute = ""
		def itemType = "inventory"
		def propertyName = "translations"
		def derivationPropertyName = "inventory"
		Long inventory = 20 
		def returnValue 
		def threadId = "1"
		def jobId = "job01"
		
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor repoPropertyDescriptor = Mock()
		RepositoryItemDescriptor repoItemDescriptor = Mock()
		
		List exps = new ArrayList<>()
		List propertyExpressions = new ArrayList<>()
		
		Map<String, String> jobInfo = new HashMap<>()
		Map<String, String> innerJobContextMap = new HashMap<>()
		Map<String, Map<String, String>> jobContext  = new HashMap<>()
		
		Map<String,RepositoryItem> translations = new TreeMap<>()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_PROPERTY) >> "inventory"
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_SITE_ATTR) >> defaultSiteAttribute

		requestMock.getAttribute(SiteOnlyTranslation.SITE_ID) >> siteId
		
		repoPropertyDescriptor.getName() >> "InventoryProperty"
				
		PropertyExpression propertyExpression1 = new PropertyExpression(repoItemDescriptor, "quantity") // "quantity")
		
		propertyExpression1.setItemDescriptor(null)
		propertyExpression1.setPropertyDescriptor(repoPropertyDescriptor)
		propertyExpression1.evaluate(repoItemMock) >> "20"
		
		propertyExpressions.add(propertyExpression1)
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		BBBJobContextManager.jobContext= jobContext
		BBBJobContextManager.jobInfo = jobInfo
		
		translationRepoItem1.setProperties(["inventory" : inventory])
		
		translations.put(siteId, translationRepoItem1)
		
		repoItemMock.getPropertyValue(propertyName) >> translations
		
		when : 
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then : 
		
		returnValue == null
	}
	
	def "derivePropertyValue - Item type is invalid - empty" () {
		
		given :
		
		def siteId = "BedBathUS"
		def defaultSiteAttribute = ""
		def itemType = ""
		def propertyName = "translations"
		def derivationPropertyName = "inventory"
		Long inventory = 20
		def returnValue
		def threadId = "1"
		def jobId = "job01"
		
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor repoPropertyDescriptor = Mock()
		RepositoryItemDescriptor repoItemDescriptor = Mock()
		
		List exps = new ArrayList<>()
		List propertyExpressions = new ArrayList<>()
		
		Map<String, String> jobInfo = new HashMap<>()
		Map<String, String> innerJobContextMap = new HashMap<>()
		Map<String, Map<String, String>> jobContext  = new HashMap<>()
		
		Map<String,RepositoryItem> translations = new TreeMap<>()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_PROPERTY) >> "inventory"
		derivedPropDesc.getValue(SiteOnlyTranslation.ITEM_TYPE) >> itemType
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_SITE_ATTR) >> defaultSiteAttribute

		requestMock.getAttribute(SiteOnlyTranslation.SITE_ID) >> siteId
		
		repoPropertyDescriptor.getName() >> "InventoryProperty"
				
		PropertyExpression propertyExpression1 = new PropertyExpression(repoItemDescriptor, "quantity") // "quantity")
		
		propertyExpression1.setItemDescriptor(null)
		propertyExpression1.setPropertyDescriptor(repoPropertyDescriptor)
		propertyExpression1.evaluate(repoItemMock) >> "20"
		
		propertyExpressions.add(propertyExpression1)
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		BBBJobContextManager.jobContext= jobContext
		BBBJobContextManager.jobInfo = jobInfo
		
		translationRepoItem1.setProperties(["inventory" : inventory])
		
		translations.put(siteId, translationRepoItem1)
		
		repoItemMock.getPropertyValue(propertyName) >> translations
		
		when :
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
	}
	
	def "derivePropertyValue - Job ID exists and Job context has valid data" () {
		
		given :
		
		def siteId = "BedBathUS"
		def defaultSiteAttribute = ""
		def itemType = "inventory"
		def propertyName = "translations"
		def derivationPropertyName = "inventory"
		Long inventory = 20
		def returnValue
		def propertyValue = "20"
		def threadId = "1"
		def jobId = "job01"
		
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor repoPropertyDescriptor = Mock()
		RepositoryItemDescriptor repoItemDescriptor = Mock()
		
		List propertyExpressions = new ArrayList<>()
		Map<String,RepositoryItem> translations = new TreeMap<>()
		
		Map<String, String> jobInfo = new HashMap<>()
		Map<String, String> innerJobContextMap = new HashMap<>()
		Map<String, Map<String, String>> jobContext  = new HashMap<>()
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_PROPERTY) >> "inventory"
		derivedPropDesc.getValue(SiteOnlyTranslation.ITEM_TYPE) >> itemType
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_SITE_ATTR) >> defaultSiteAttribute
		
		jobInfo.put(siteId, "SampleJob")
		innerJobContextMap.put(BBBCoreConstants.SITE_ID, siteId)
		jobContext.put(jobId, innerJobContextMap)
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		jobContext.put(BBBCoreConstants.SITE_ID, siteId)
		ServletUtil.setCurrentRequest(null)
		
		repoPropertyDescriptor.getName() >> null
				
		PropertyExpression propertyExpression1 = new PropertyExpression(repoItemDescriptor, "quantity") // "quantity")
		
		propertyExpression1.setItemDescriptor(null)
		propertyExpression1.setPropertyDescriptor(repoPropertyDescriptor)
		propertyExpression1.evaluate(repoItemMock) >> "20"
		
		repoItemMock.getPropertyValue(repoPropertyDescriptor) >> propertyValue
		repoItemMock.getPropertyValue(propertyName) >> translations
		repoItemMock.getPropertyValue(derivationPropertyName) >> propertyValue
		
		translationRepoItem1.setProperties(["quantity" : "20"])
		propertyExpressions.add(propertyExpression1)
		translations.put(siteId, translationRepoItem1)
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		BBBJobContextManager.jobContext= jobContext
		BBBJobContextManager.jobInfo = jobInfo
		
		siteOnlyTranslationSpy.getCurrentSiteId() >> ""

		when :
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == propertyValue
	}
	
	def "derivePropertyValue - No Job information - job context found" () {
		
		given :
		
		def siteId = "BedBathUS"
		def defaultSiteAttribute = ""
		def itemType = "inventory"
		def propertyName = "translations"
		def derivationPropertyName = "inventory"
		Long inventory = 20
		def returnValue
		def propertyValue = "20"
		def threadId = "1"
		def jobId = "job01"
		
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor repoPropertyDescriptor = Mock()
		RepositoryItemDescriptor repoItemDescriptor = Mock()
		
		List propertyExpressions = new ArrayList<>()
		Map<String,RepositoryItem> translations = new TreeMap<>()
		
		Map<String, String> jobInfo = new HashMap<>()
		Map<String, String> innerJobContextMap = new HashMap<>()
		Map<String, Map<String, String>> jobContext  = new HashMap<>()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_PROPERTY) >> "inventory"
		derivedPropDesc.getValue(SiteOnlyTranslation.ITEM_TYPE) >> itemType
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_SITE_ATTR) >> defaultSiteAttribute
		
		ServletUtil.setCurrentRequest(null)
		
		requestMock.getAttribute(SiteOnlyTranslation.SITE_ID) >> siteId
		
		repoPropertyDescriptor.getName() >> null
				
		PropertyExpression propertyExpression1 = new PropertyExpression(repoItemDescriptor, "quantity") // "quantity")
		
		propertyExpression1.setItemDescriptor(null)
		propertyExpression1.setPropertyDescriptor(repoPropertyDescriptor)
		propertyExpression1.evaluate(repoItemMock) >> "20"
		
		repoItemMock.getPropertyValue(repoPropertyDescriptor) >> propertyValue
		repoItemMock.getPropertyValue(propertyName) >> null
		repoItemMock.getPropertyValue(derivationPropertyName) >> propertyValue
		
		translationRepoItem1.setProperties(["quantity" : "20"])
		propertyExpressions.add(propertyExpression1)
		translations.put(siteId, translationRepoItem1)
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		BBBJobContextManager.jobContext= jobContext
		BBBJobContextManager.jobInfo = jobInfo
		
		siteOnlyTranslationSpy.getCurrentSiteId() >> ""
		
		when :
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue.equals(propertyValue)
	}
	
	def "derivePropertyValue - No translations found" () {
		
		given :
		
		def siteId = "BedBathUS"
		def defaultSiteAttribute = ""
		def itemType = "repoKey"
		def propertyName = "translations"
		def derivationPropertyName = "inventory"
		Long inventory = 20
		def returnValue
		def propertyValue = "20"
		def threadId = "1"
		def jobId = "job01"
		
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor repoPropertyDescriptor = Mock()
		RepositoryItemDescriptor repoItemDescriptor = Mock()
		
		List propertyExpressions = new ArrayList<>()
		Map<String,RepositoryItem> translations = new TreeMap<>()
		
		Map<String, String> jobInfo = new HashMap<>()
		Map<String, String> innerJobContextMap = new HashMap<>()
		Map<String, Map<String, String>> jobContext  = new HashMap<>()
		
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_PROPERTY) >> "inventory"
		derivedPropDesc.getValue(SiteOnlyTranslation.ITEM_TYPE) >> itemType
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_SITE_ATTR) >> defaultSiteAttribute
		
		jobInfo.put(siteId, "SampleJob")
		innerJobContextMap.put(BBBCoreConstants.SITE_ID, siteId)
		jobContext.put(jobId, innerJobContextMap)
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		jobContext.put(BBBCoreConstants.SITE_ID, siteId)
		
		requestMock.getAttribute(SiteOnlyTranslation.SITE_ID) >> siteId
		
		repoPropertyDescriptor.getName() >> null
				
		PropertyExpression propertyExpression1 = new PropertyExpression(repoItemDescriptor, "quantity") // "quantity")
		
		propertyExpression1.setItemDescriptor(null)
		propertyExpression1.setPropertyDescriptor(repoPropertyDescriptor)
		propertyExpression1.evaluate(repoItemMock) >> "20"
		
		repoItemMock.getPropertyValue(repoPropertyDescriptor) >> propertyValue
		repoItemMock.getPropertyValue(propertyName) >> null
		repoItemMock.getPropertyValue(derivationPropertyName) >> propertyValue
		
		translationRepoItem1.setProperties(["quantity" : "20"])
		propertyExpressions.add(propertyExpression1)
		translations.put(siteId, translationRepoItem1)
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		BBBJobContextManager.jobContext= jobContext
		BBBJobContextManager.jobInfo = jobInfo
		
		siteOnlyTranslationSpy.getCurrentSiteId() >> ""
		
		when :
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue.equals(propertyValue)
	}
	
	def "derivePropertyValue - JobID is invalid - null exists in the Job context" () {
		
		given :
		
		def siteId = "BedBathUS"
		def defaultSiteAttribute = ""
		def itemType = "inventory"
		def propertyName = "translations"
		def derivationPropertyName = "inventory"
		Long inventory = 20
		def returnValue
		def threadId = "1"
		def jobId = "job01"
		
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor repoPropertyDescriptor = Mock()
		RepositoryItemDescriptor repoItemDescriptor = Mock()
		
		
		List propertyExpressions = new ArrayList<>()
		Map<String,RepositoryItem> translations = new TreeMap<>()
		
		Map<String, String> jobInfo = new HashMap<>()
		Map<String, String> innerJobContextMap = new HashMap<>()
		Map<String, Map<String, String>> jobContext  = new HashMap<>()

		DerivedPropertyDescriptor derivedPropDesc = Mock()
		
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_PROPERTY) >> "inventory"
		derivedPropDesc.getValue(SiteOnlyTranslation.ITEM_TYPE) >> itemType
		derivedPropDesc.getValue(SiteOnlyTranslation.DEFAULT_SITE_ATTR) >> defaultSiteAttribute
		
		jobInfo.put(siteId, "SampleJob")
		innerJobContextMap.put(BBBCoreConstants.SITE_ID, siteId)
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])

		requestMock.getAttribute(SiteOnlyTranslation.SITE_ID) >> siteId
		jobContext.put(BBBCoreConstants.SITE_ID, siteId)
		repoPropertyDescriptor.getName() >> "InventoryProperty"
				
		PropertyExpression propertyExpression1 = new PropertyExpression(repoItemDescriptor, "quantity") // "quantity")
		
		propertyExpression1.setItemDescriptor(null)
		propertyExpression1.setPropertyDescriptor(repoPropertyDescriptor)
		propertyExpression1.evaluate(repoItemMock) >> "20"
		
		propertyExpressions.add(propertyExpression1)
		
		translationRepoItem1.setProperties(["inventory" : inventory])
		
		translations.put(siteId, translationRepoItem1)
		
		repoItemMock.getPropertyValue(propertyName) >> translations
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		BBBJobContextManager.jobContext= jobContext
		BBBJobContextManager.jobInfo = jobInfo
		
		siteOnlyTranslationSpy.setDerivation(derivation)
		
		when :
		
		returnValue = siteOnlyTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == 0
	}
	
}

