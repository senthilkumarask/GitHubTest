package com.bbb.commerce.catalog

import java.sql.Timestamp;
import java.util.List
import java.util.Set

import org.junit.Ignore;

import com.bbb.repository.RepositoryItemMock
import com.bbb.utils.BBBUtility

import atg.core.util.StringUtils;
import atg.nucleus.Nucleus;
import atg.nucleus.logging.ApplicationLogging
import atg.nucleus.registry.NucleusRegistry
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryItemImpl
import atg.repository.RepositoryPropertyDescriptor
import atg.repository.dp.Derivation;
import atg.repository.dp.DerivedPropertyDescriptor
import atg.repository.dp.PropertyExpression
import atg.repository.dp.RepositoryKeyService
import atg.servlet.ServletUtil
import spock.lang.IgnoreIf;
import spock.lang.specification.BBBExtendedSpec;

/**
 * 
 * @author Velmurugan Moorthy
 * 
 * Changes made in Java file :
 * 
 * #196 - BBBUtility.isListEmpty(exps) - Utility class' null check added 
 * #239 -if(BBBUtility.isEmpty(siteId)) - Utility class' null check added
 * #296 - if( ( BBBUtility.isCollectionEmpty(translations)) || (BBBUtility.isEmpty(property))) - Utility class' emtpy check added
 *
 */

class SiteTranslationSpecification extends BBBExtendedSpec {

	private SiteTranslation siteTranslationSpy
	private Derivation derivation
	private Nucleus nucleusMock
	
	def setup() {
	
		
		derivation = Mock()
		NucleusRegistry sNucleusRegistry = Mock()
		
		nucleusMock = Mock()
		Nucleus.setNucleusRegistry(sNucleusRegistry)
		sNucleusRegistry.getKey() >> "nKey"
		sNucleusRegistry.get("nKey") >> nucleusMock
		Nucleus.sUsingChildNucleii = true
		
		siteTranslationSpy = Spy()
		siteTranslationSpy.setDerivation(derivation)
	}
	
	/*
	 * derivePropertyValue - test cases STARTS
	 * 
	 *
	 */
	
	def "derivePropertyValue - retrieving derived properties successfully" () {
		
		given :
		
		def propName = "loginPropertyName"
		def siteId = "TBS_BedBathCanada"
		def repositoryKey = "TBS_BedBathCanada"
		def requestLocale = "TBS_BedBathCanada"
		def property = "login"
		def translationName = "loginPropertyGermanTranslation"
		def tempLocale = "TBS_BedBathCanada"
		def tempAttributeName = "login"
		def returnValue 
		def attributeValueClob = "bedbath"
		def bbbCanadaSiteId = "BedBathCanada"
		
		ApplicationLogging logger = Mock()
		logger.isLoggingDebug() >> true
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor pDescriptor = new RepositoryPropertyDescriptor(propName)
		RepositoryItemDescriptor pItemDescriptorMock = Mock()
		PropertyExpression propertyExpression = Mock()
		PropertyExpression propertyExpression2 = Mock()
		RepositoryKeyService keyServiceMock = Mock()
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		RepositoryItemMock translationRepoItem2 = new RepositoryItemMock(["id" : "translation02"])
		RepositoryItemMock tempSiteItem = new RepositoryItemMock(["id" : bbbCanadaSiteId]) 
		List propertyExpressions = new ArrayList<>()
		Set<RepositoryItem> translations = new HashSet<>()
		
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		derivedPropDesc.getValue(SiteTranslation.DEFAULT_LOCALE) >> requestLocale
		derivedPropDesc.getValue(SiteTranslation.DEFAULT_PROPERTY) >> "inventory"
		derivedPropDesc.getValue(SiteTranslation.DEFAULT_SITE_ATTR) >> siteId
		derivedPropDesc.getValue(SiteTranslation.PROPERTY) >> property
		derivedPropDesc.getValue(SiteTranslation.TRANSLATIONS) >> "loginPropertyGermanTranslation"
		
		translationRepoItem1.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
											 "attributeName" : tempAttributeName , "attributeValueClob" : attributeValueClob ])
		
		keyServiceMock.getRepositoryKey() >> repositoryKey
		translations.add(translationRepoItem1)
		propertyExpression.getPropertyDescriptor() >> pDescriptor 
		propertyExpression2.getPropertyDescriptor() >> pDescriptor
		
		requestMock.getAttribute("siteId") >> siteId
		
		propertyExpressions.addAll([propertyExpression, propertyExpression2])
		
		propertyExpression.evaluate(propName) >> "loginId"
		
		repoItemMock.getRepositoryId() >> "repoItem01"
		repoItemMock.getPropertyValue(translationName) >> translations
		
		derivation.getExpressionList() >> propertyExpressions
		derivation.getPropertyDescriptor() >> derivedPropDesc
		nucleusMock.resolveName(SiteTranslation.KEY_SERVICE_PATH) >> keyServiceMock
		
		when : 
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then : 
		
		returnValue == attributeValueClob 
	}
	
	
	/* Method invoked internally : 
	 * 
	 * setValues() , getKeyService()
	 * 
	 * Alternative branches covered : 
	 * 
	 * #229 - if (StringUtils.isEmpty(siteId)) - true
	 * #239 - if(BBBUtility.isEmpty(siteId)) - true
	 * #250 - if(null != keySvice ) - false
	 * #263 - else if(siteId.equals("TBS_BuyBuyBaby") )  - true
	 * #271 - if(siteId.equalsIgnoreCase(getDefaultSiteAttribute()) && reqLocale.equalsIgnoreCase(getDefaultLocale()) ) - false
	 * #296 - if( ( BBBUtility.isCollectionEmpty(translations)) || (BBBUtility.isEmpty(property))) - false
	 * 
	 * 
	 */
	
	def "derivePropertyValue - Site ID, request locale and other inputs are invalid - null or empty" () {
		
		given :
		
		def propName = "loginPropertyName"
		def siteId = "TBS_BuyBuyBaby"
		def repositoryKey = "TBS_BedBathCanada"
		def requestLocale = "TBS_BuyBuyBaby"
		def property = "login"
		def translationName = "loginPropertyGermanTranslation"
		def tempLocale = "TBS_BuyBuyBaby"
		def tempAttributeName = "login"
		def returnValue
		def attributeValueBoolean = false
		def desktopSiteId = "BuyBuyBaby"
		double orderAmt = 20.00
		
		ApplicationLogging logger = Mock()
		logger.isLoggingDebug() >> true
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor pDescriptor = new RepositoryPropertyDescriptor(propName)
		RepositoryItemDescriptor pItemDescriptorMock = Mock()
		PropertyExpression propertyExpression = Mock()
		PropertyExpression propertyExpression2 = Mock()
		RepositoryKeyService keyServiceMock = Mock()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		
		RepositoryItemMock tempSiteItem = new RepositoryItemMock(["id" : desktopSiteId])
		
		List propertyExpressions = new ArrayList<>()
		Set<RepositoryItemMock> translations = new LinkedHashSet<>()
		
		translationRepoItem1.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
											 "attributeName" : tempAttributeName , "attributeValueBoolean" : attributeValueBoolean ])
		
		translations.addAll([translationRepoItem1])
		propertyExpression.getPropertyDescriptor() >> pDescriptor
		propertyExpression2.getPropertyDescriptor() >> pDescriptor
		
		requestMock.getAttribute("siteId") >> ""
		
		propertyExpressions.addAll([propertyExpression, propertyExpression2])
		
		propertyExpression.evaluate(propName) >> "loginId"
		
		repoItemMock.getRepositoryId() >> "repoItem01"
		repoItemMock.getPropertyValue(translationName) >> translations
		
		derivation.getExpressionList() >> propertyExpressions
		
		siteTranslationSpy.getDefaultProperty() >> null
		siteTranslationSpy.getDefaultSiteAttribute() >> siteId
		siteTranslationSpy.getDefaultLocale() >> "TBS_BedBathCanada"
		siteTranslationSpy.getTranslations() >> "loginPropertyGermanTranslation"
		siteTranslationSpy.getProperty() >> property 
		siteTranslationSpy.getCurrentSiteId() >> ""
		nucleusMock.resolveName(SiteTranslation.KEY_SERVICE_PATH) >> null
		
		when :
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
	}
	
	
	/* Method invoked internally :
	 *
	 * setValues() , getKeyService()
	 *
	 * Alternative branches covered :
	 *
	 * #377 - else if(null != intValue) - true
	 * #438 - if (nucleus != null)  - false
	 */
	
	def "derivePropertyValue - Number type attribute exists in the translation" () {
		
		given :
		
		def propName = "loginPropertyName"
		def siteId = "TBS_BuyBuyBaby"
		def repositoryKey = "TBS_BuyBuyBaby"
		def requestLocale = "TBS_BuyBuyBaby"
		def property = "login"
		def translationName = "loginPropertyGermanTranslation"
		def tempLocale = "TBS_BuyBuyBaby"
		def tempAttributeName = "orderAmt"
		def returnValue
		def desktopSiteId = "BuyBuyBaby"
		double orderAmt = 20.00
		
		ApplicationLogging logger = Mock()
		logger.isLoggingDebug() >> true
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor pDescriptor = new RepositoryPropertyDescriptor(propName)
		RepositoryItemDescriptor pItemDescriptorMock = Mock()
		PropertyExpression propertyExpression = Mock()
		PropertyExpression propertyExpression2 = Mock()
		RepositoryKeyService keyServiceMock = Mock()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		
		RepositoryItemMock tempSiteItem = new RepositoryItemMock(["id" : desktopSiteId])
		
		List propertyExpressions = new ArrayList<>()
		Set<RepositoryItemMock> translations = new LinkedHashSet<>()
		
		translationRepoItem1.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
											 "attributeName" : tempAttributeName , "attributeValueNumber" : orderAmt ])
		
		translations.addAll([translationRepoItem1])
		propertyExpression.getPropertyDescriptor() >> pDescriptor
		propertyExpression2.getPropertyDescriptor() >> pDescriptor
		
		requestMock.getAttribute("siteId") >> ""
		
		propertyExpressions.addAll([propertyExpression, propertyExpression2])
		
		propertyExpression.evaluate(propName) >> "loginId"
		
		repoItemMock.getRepositoryId() >> "repoItem01"
		repoItemMock.getPropertyValue(translationName) >> translations
		
		derivation.getExpressionList() >> propertyExpressions
		
		siteTranslationSpy.getDefaultProperty() >> null
		siteTranslationSpy.getDefaultSiteAttribute() >> siteId
		siteTranslationSpy.getDefaultLocale() >> tempLocale
		siteTranslationSpy.getTranslations() >> "loginPropertyGermanTranslation"
		siteTranslationSpy.getProperty() >> tempAttributeName
		siteTranslationSpy.getCurrentSiteId() >> ""
		nucleusMock.resolveName(SiteTranslation.KEY_SERVICE_PATH) >> null
		
		when :
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == orderAmt
	}
	
	/* Method invoked internally :
	 *
	 * setValues() , getKeyService()
	 *
	 * Alternative branches covered :
	 *
	 * #374 - if(null != stringValue && !stringValue.isEmpty()) - true
	 * TODO : 
	 * 
	 * #374 - if(null != stringValue && !stringValue.isEmpty()) - !stringValue.isEmpty() - false
	 *
	 */
	
	def "derivePropertyValue - String type attribute exists in the translation" () {
		
		given :
		
		def propName = "loginPropertyName"
		def siteId = "TBS_BuyBuyBaby"
		def repositoryKey = "TBS_BuyBuyBaby"
		def requestLocale = "TBS_BuyBuyBaby"
		def property = "firstName"
		def translationName = "loginPropertyGermanTranslation"
		def tempLocale = "TBS_BuyBuyBaby"
		def tempAttributeName = "firstName"
		def returnValue
		def desktopSiteId = "BuyBuyBaby"
		def firstName = "Alice"
		
		ApplicationLogging logger = Mock()
		logger.isLoggingDebug() >> true
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor pDescriptor = new RepositoryPropertyDescriptor(propName)
		RepositoryItemDescriptor pItemDescriptorMock = Mock()
		PropertyExpression propertyExpression = Mock()
		PropertyExpression propertyExpression2 = Mock()
		RepositoryKeyService keyServiceMock = Mock()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		RepositoryItemMock translationRepoItem2 = new RepositoryItemMock(["id" : "translation02"])
		RepositoryItemMock tempSiteItem = new RepositoryItemMock(["id" : desktopSiteId])
		
		List propertyExpressions = new ArrayList<>()
		Set<RepositoryItemMock> translations = new LinkedHashSet<>()
		
		translationRepoItem1.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
											 "attributeName" : tempAttributeName , "attributeValueString" : firstName ])
		translationRepoItem2.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
			"attributeName" : tempAttributeName , "attributeValueString" : "" ])
		
		translations.addAll([translationRepoItem1, translationRepoItem2])
		propertyExpression.getPropertyDescriptor() >> pDescriptor
		propertyExpression2.getPropertyDescriptor() >> pDescriptor
		
		requestMock.getAttribute("siteId") >> ""
		
		propertyExpressions.addAll([propertyExpression, propertyExpression2])
		
		propertyExpression.evaluate(propName) >> "loginId"
		
		repoItemMock.getRepositoryId() >> "repoItem01"
		repoItemMock.getPropertyValue(translationName) >> translations
		
		derivation.getExpressionList() >> propertyExpressions
		
		siteTranslationSpy.getDefaultProperty() >> null
		siteTranslationSpy.getDefaultSiteAttribute() >> siteId
		siteTranslationSpy.getDefaultLocale() >> tempLocale
		siteTranslationSpy.getTranslations() >> "loginPropertyGermanTranslation"
		siteTranslationSpy.getProperty() >> tempAttributeName
		siteTranslationSpy.getCurrentSiteId() >> ""
		
		nucleusMock.resolveName(SiteTranslation.KEY_SERVICE_PATH) >> null
		
		when :
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == firstName
	}
	
	/* Method invoked internally :
	 *
	 * setValues() , getKeyService()
	 *
	 * Alternative branches covered :
	 *
	 * #380 - else if(null != boolValue) - true
	 *
	 */
	
	def "derivePropertyValue - boolean type attribute exists in the translation" () {
		
		given :
		
		def propName = "loginPropertyName"
		def siteId = "TBS_BuyBuyBaby"
		def repositoryKey = "TBS_BuyBuyBaby"
		def requestLocale = "TBS_BuyBuyBaby"
		def property = "firstName"
		def translationName = "loginPropertyGermanTranslation"
		def tempLocale = "TBS_BuyBuyBaby"
		def tempAttributeName = "shareAccount"
		def tempAttributeValue = true
		def returnValue
		def desktopSiteId = "BuyBuyBaby"
		def firstName = "Alice"
		
		ApplicationLogging logger = Mock()
		logger.isLoggingDebug() >> true
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor pDescriptor = new RepositoryPropertyDescriptor(propName)
		RepositoryItemDescriptor pItemDescriptorMock = Mock()
		PropertyExpression propertyExpression = Mock()
		PropertyExpression propertyExpression2 = Mock()
		RepositoryKeyService keyServiceMock = Mock()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		
		RepositoryItemMock tempSiteItem = new RepositoryItemMock(["id" : desktopSiteId])
		
		List propertyExpressions = new ArrayList<>()
		Set<RepositoryItemMock> translations = new LinkedHashSet<>()
		
		translationRepoItem1.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
											 "attributeName" : tempAttributeName , "attributeValueBoolean" : tempAttributeValue ])
		
		translations.addAll([translationRepoItem1])
		propertyExpression.getPropertyDescriptor() >> pDescriptor
		propertyExpression2.getPropertyDescriptor() >> pDescriptor
		
		requestMock.getAttribute("siteId") >> ""
		
		propertyExpressions.addAll([propertyExpression, propertyExpression2])
		
		propertyExpression.evaluate(propName) >> "loginId"
		
		repoItemMock.getRepositoryId() >> "repoItem01"
		repoItemMock.getPropertyValue(translationName) >> translations
		
		derivation.getExpressionList() >> propertyExpressions
		
		siteTranslationSpy.getDefaultProperty() >> null
		siteTranslationSpy.getDefaultSiteAttribute() >> siteId
		siteTranslationSpy.getDefaultLocale() >> tempLocale
		siteTranslationSpy.getTranslations() >> "loginPropertyGermanTranslation"
		siteTranslationSpy.getProperty() >> tempAttributeName
		siteTranslationSpy.getCurrentSiteId() >> ""
		
		nucleusMock.resolveName(SiteTranslation.KEY_SERVICE_PATH) >> null
		
		when :
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == tempAttributeValue
	}
	
	/* Method invoked internally :
	 *
	 * setValues() , getKeyService()
	 *
	 * Alternative branches covered :
	 *
	 * #386 - else if(null != attributeValueDate) - true
	 *
	 */
	
	def "derivePropertyValue - Date type attribute exists in the translation" () {
		
		given :
		
		def propName = "loginPropertyName"
		def siteId = "TBS_BuyBuyBaby"
		def repositoryKey = "TBS_BuyBuyBaby"
		def requestLocale = "TBS_BuyBuyBaby"
		def property = "firstName"
		def translationName = "loginPropertyGermanTranslation"
		def tempLocale = "TBS_BuyBuyBaby"
		def tempAttributeName = "firstName"
		def returnValue
		def desktopSiteId = "BuyBuyBaby"
		def firstName = "Alice"
		Timestamp attributeValueDate = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())
		
		ApplicationLogging logger = Mock()
		logger.isLoggingDebug() >> true
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor pDescriptor = new RepositoryPropertyDescriptor(propName)
		RepositoryItemDescriptor pItemDescriptorMock = Mock()
		PropertyExpression propertyExpression = Mock()
		PropertyExpression propertyExpression2 = Mock()
		RepositoryKeyService keyServiceMock = Mock()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		
		RepositoryItemMock tempSiteItem = new RepositoryItemMock(["id" : desktopSiteId])
		
		List propertyExpressions = new ArrayList<>()
		Set<RepositoryItemMock> translations = new LinkedHashSet<>()
		
		translationRepoItem1.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
											 "attributeName" : tempAttributeName , "attributeValueDate" : attributeValueDate ])
		
		translations.addAll([translationRepoItem1])
		propertyExpression.getPropertyDescriptor() >> pDescriptor
		propertyExpression2.getPropertyDescriptor() >> pDescriptor
		
		requestMock.getAttribute("siteId") >> ""
		
		propertyExpressions.addAll([propertyExpression, propertyExpression2])
		
		propertyExpression.evaluate(propName) >> "loginId"
		
		repoItemMock.getRepositoryId() >> "repoItem01"
		repoItemMock.getPropertyValue(translationName) >> translations
		
		derivation.getExpressionList() >> propertyExpressions
		
		siteTranslationSpy.getDefaultProperty() >> null
		siteTranslationSpy.getDefaultSiteAttribute() >> siteId
		siteTranslationSpy.getDefaultLocale() >> tempLocale
		siteTranslationSpy.getTranslations() >> "loginPropertyGermanTranslation"
		siteTranslationSpy.getProperty() >> tempAttributeName
		siteTranslationSpy.getCurrentSiteId() >> ""
		
		nucleusMock.resolveName(SiteTranslation.KEY_SERVICE_PATH) >> null
		
		when :
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == attributeValueDate
	}
	
	/* Method invoked internally :
	 *
	 * setValues() , getKeyService()
	 *
	 * Alternative branches covered :
	 *
	 * #386 - else if(null != attributeValueDate) - false (all condition above this would be failing)
	 * 
	 */
	
	def "derivePropertyValue - Integer type attribute exists in the translation" () {
		
		given :
		
		def propName = "loginPropertyName"
		def siteId = "TBS_BuyBuyBaby"
		def repositoryKey = "TBS_BuyBuyBaby"
		def requestLocale = "TBS_BuyBuyBaby"
		def property = "login"
		def translationName = "loginPropertyGermanTranslation"
		def tempLocale = "TBS_BuyBuyBaby"
		def tempAttributeName = "age"
		def returnValue
		def desktopSiteId = "BuyBuyBaby"
		double orderAmt = 20.00
		
		ApplicationLogging logger = Mock()
		logger.isLoggingDebug() >> true
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor pDescriptor = new RepositoryPropertyDescriptor(propName)
		RepositoryItemDescriptor pItemDescriptorMock = Mock()
		PropertyExpression propertyExpression = Mock()
		PropertyExpression propertyExpression2 = Mock()
		RepositoryKeyService keyServiceMock = Mock()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		
		RepositoryItemMock tempSiteItem = new RepositoryItemMock(["id" : desktopSiteId])
		
		List propertyExpressions = new ArrayList<>()
		Set<RepositoryItemMock> translations = new LinkedHashSet<>()
		
		translationRepoItem1.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
											 "attributeName" : tempAttributeName , "attributeValueInt" : orderAmt ])
		
		translations.addAll([translationRepoItem1])
		propertyExpression.getPropertyDescriptor() >> pDescriptor
		propertyExpression2.getPropertyDescriptor() >> pDescriptor
		
		requestMock.getAttribute("siteId") >> ""
		
		propertyExpressions.addAll([propertyExpression, propertyExpression2])
		
		propertyExpression.evaluate(propName) >> "loginId"
		
		repoItemMock.getRepositoryId() >> "repoItem01"
		repoItemMock.getPropertyValue(translationName) >> translations
		
		derivation.getExpressionList() >> propertyExpressions
		
		siteTranslationSpy.getDefaultProperty() >> null
		siteTranslationSpy.getDefaultSiteAttribute() >> siteId
		siteTranslationSpy.getDefaultLocale() >> tempLocale
		siteTranslationSpy.getTranslations() >> "loginPropertyGermanTranslation"
		siteTranslationSpy.getProperty() >> tempAttributeName
		siteTranslationSpy.getCurrentSiteId() >> ""
		
		nucleusMock.resolveName(SiteTranslation.KEY_SERVICE_PATH) >> null
		
		when :
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
	}
	
	
	/*
	 * Alternative branches : 
	 * 
	 * #318 - if(null != tempLocale && (tempLocale.equalsIgnoreCase(getDefaultLocale()))) 
	 *  		  null != tempLocale - false
	 *  #312 - if ( (null != tempSiteItem && (tempSiteItem.getRepositoryId().equalsIgnoreCase(siteId)))
	 * 							&& (null != tempAttributeName && (tempAttributeName.equalsIgnoreCase(property))) ) - all the branches in the whole condition
	 * 				
	 *  Can't be covered : 
	 *  
	 *  #399 -if(pItem instanceof RepositoryItemImpl) - false - as it'll throw nullPtrException 
	 */
	
	def "derivePropertyValue - Translation has no locale value - null" () {
		
		given :
		
		def propName = "loginPropertyName"
		def siteId = "TBS_BedBathCanada"
		def repositoryKey = "TBS_BedBathCanada"
		def requestLocale = "TBS_BedBathUS"
		def property = "login"
		def translationName = "loginPropertyGermanTranslation"
		def tempLocale = "TBS_BedBathCanada"
		def tempAttributeName = "login"
		def returnValue
		def firstName = ""
		def bbbCanadaSiteId = "BedBathCanada"
		
		ApplicationLogging logger = Mock()
		logger.isLoggingDebug() >> true
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor pDescriptor = new RepositoryPropertyDescriptor(propName)
		RepositoryItemDescriptor pItemDescriptorMock = Mock()
		PropertyExpression propertyExpression = Mock()
		PropertyExpression propertyExpression2 = Mock()
		RepositoryKeyService keyServiceMock = Mock()
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		RepositoryItemMock translationRepoItem2 = new RepositoryItemMock(["id" : "translation02"])
		RepositoryItemMock translationRepoItem3 = new RepositoryItemMock(["id" : "translation03"])
		RepositoryItemMock translationRepoItem4 = new RepositoryItemMock(["id" : "translation04"])
		RepositoryItemMock translationRepoItem5 = new RepositoryItemMock(["id" : "translation05"])
		RepositoryItemMock translationRepoItem6 = new RepositoryItemMock(["id" : "translation06"])
		
		RepositoryItemMock tempSiteItem = new RepositoryItemMock(["id" : bbbCanadaSiteId])
		RepositoryItemMock tempSiteItem2 = new RepositoryItemMock(["id" : ""])
		List propertyExpressions = new ArrayList<>()
		Set<RepositoryItemMock> translations = new LinkedHashSet<>()
		
		translationRepoItem1.setProperties([ "site" : tempSiteItem , "locale" : null ,
											 "attributeName" : tempAttributeName , "attributeValueClob" : null ])
		translationRepoItem2.setProperties([ "site" : null , "locale" : tempLocale ,
			"attributeName" : tempAttributeName , "attributeValueClob" : null ])
		translationRepoItem3.setProperties([ "site" : tempSiteItem2 , "locale" : tempLocale ,
			"attributeName" : tempAttributeName , "attributeValueString" : firstName ])
		translationRepoItem4.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
			"attributeName" : "" , "attributeValueString" : firstName ])
		translationRepoItem5.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
			"attributeName" : null , "attributeValueString" : firstName ])
		translationRepoItem6.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
			"attributeName" : tempAttributeName , "attributeValueString" : firstName ])
		
		keyServiceMock.getRepositoryKey() >> repositoryKey
		translations.addAll([translationRepoItem1, translationRepoItem2, translationRepoItem3, translationRepoItem4, translationRepoItem5, translationRepoItem6])
		propertyExpression.getPropertyDescriptor() >> pDescriptor
		propertyExpression2.getPropertyDescriptor() >> pDescriptor
		
		requestMock.getAttribute("siteId") >> siteId
		
		propertyExpressions.addAll([propertyExpression, propertyExpression2])
		
		propertyExpression.evaluate(propName) >> "loginId"
		
		repoItemMock.getRepositoryId() >> "repoItem01"
		repoItemMock.getPropertyValue(translationName) >> translations
		
		derivation.getExpressionList() >> propertyExpressions
		
		siteTranslationSpy.getDefaultProperty() >> "DefaultProperty"
		siteTranslationSpy.getDefaultSiteAttribute() >> siteId
		siteTranslationSpy.getDefaultLocale() >> requestLocale
		siteTranslationSpy.getTranslations() >> "loginPropertyGermanTranslation"
		siteTranslationSpy.getProperty() >> property
		
		nucleusMock.resolveName(SiteTranslation.KEY_SERVICE_PATH) >> keyServiceMock
		
		when :
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
	}
	
	/*
	 * Alternative branches :
	 *
	 *
	 * TODO : 
	 * 
	 * 
	 *
	 */
	def "derivePropertyValue - No property descriptor exists in property expression - null" () {
		
		given :
		
		def propName = "loginPropertyName"
		def siteId = "TBS_BuyBuyBaby"
		def repositoryKey = "TBS_BuyBuyBaby"
		def requestLocale = "TBS_BuyBuyBaby"
		def property = "login"
		def translationName = "loginPropertyGermanTranslation"
		def tempLocale = "TBS_BuyBuyBaby"
		def tempAttributeName = "age"
		def returnValue
		def desktopSiteId = "BuyBuyBaby"
		double orderAmt = 20.00
		
		ApplicationLogging logger = Mock()
		logger.isLoggingDebug() >> true
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryItemDescriptor pItemDescriptorMock = Mock()
		PropertyExpression propertyExpression = Mock()
		PropertyExpression propertyExpression2 = Mock()
		RepositoryKeyService keyServiceMock = Mock()
		
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		
		RepositoryItemMock tempSiteItem = new RepositoryItemMock(["id" : desktopSiteId])
		
		List propertyExpressions = new ArrayList<>()
		Set<RepositoryItemMock> translations = new LinkedHashSet<>()
		
		translationRepoItem1.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
											 "attributeName" : tempAttributeName , "attributeValueInt" : orderAmt ])
		
		translations.addAll([translationRepoItem1])
		requestMock.getAttribute("siteId") >> ""
		
		propertyExpressions.addAll([propertyExpression, propertyExpression2])
		propertyExpression.evaluate(propName) >> "loginId"
		
		repoItemMock.getRepositoryId() >> "repoItem01"
		repoItemMock.getPropertyValue(translationName) >> translations
		
		siteTranslationSpy.getDefaultLocale() >> tempLocale
		siteTranslationSpy.getTranslations() >> "loginPropertyGermanTranslation"
		siteTranslationSpy.getProperty() >> tempAttributeName
		
		nucleusMock.resolveName(SiteTranslation.KEY_SERVICE_PATH) >> null
		
		when :
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
	}
	
	/*
	 * Alternative branches :
	 *
	 *
	 * #201 - if(null != pe) - false
	 * #218 - if(null == propName && getDefaultProperty()!=null)  - getDefaultProperty()!=null - true
	 * #271- if(siteId.equalsIgnoreCase(getDefaultSiteAttribute()) && reqLocale.equalsIgnoreCase(getDefaultLocale()) )
	 * 
	 * TODO :
	 *
	 */
	
	def "derivePropertyValue - Property expression returned is invalid - null" () {
		
		given :
		
		def propName = "loginPropertyName"
		def siteId = "TBS_BedBathCanada"
		def repositoryKey = "TBS_BedBathCanada"
		def requestLocale = "TBS_BedBathCanada"
		def property = "login"
		def translationName = "loginPropertyGermanTranslation"
		def tempLocale = "TBS_BedBathCanada"
		def tempAttributeName = "login"
		def returnValue 
		def attributeValueClob = "bedbath"
		def bbbCanadaSiteId = "BedBathCanada"
		
		ApplicationLogging logger = Mock()
		logger.isLoggingDebug() >> true
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor pDescriptor = new RepositoryPropertyDescriptor(propName)
		RepositoryItemDescriptor pItemDescriptorMock = Mock()
		PropertyExpression propertyExpression = Mock()
		PropertyExpression propertyExpression2 = Mock()
		RepositoryKeyService keyServiceMock = Mock()
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		RepositoryItemMock translationRepoItem2 = new RepositoryItemMock(["id" : "translation02"])
		RepositoryItemMock tempSiteItem = new RepositoryItemMock(["id" : bbbCanadaSiteId]) 
		List propertyExpressions = new ArrayList<>()
		Set<RepositoryItem> translations = new HashSet<>()
		
		translationRepoItem1.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
											 "attributeName" : tempAttributeName , "attributeValueClob" : attributeValueClob ])
		
		keyServiceMock.getRepositoryKey() >> repositoryKey
		translations.add(translationRepoItem1)
		propertyExpression.getPropertyDescriptor() >> pDescriptor 
		propertyExpression2.getPropertyDescriptor() >> pDescriptor
		
		requestMock.getAttribute("siteId") >> bbbCanadaSiteId
		
		propertyExpressions.addAll([propertyExpression, propertyExpression2])
		
		propertyExpression.evaluate(propName) >> "loginId"
		
		repoItemMock.getRepositoryId() >> "repoItem01"
		repoItemMock.getPropertyValue(translationName) >> translations
		
		derivation.getExpressionList() >> propertyExpressions
		siteTranslationSpy.getDefaultProperty() >> "DefaultProperty"
		siteTranslationSpy.getDefaultSiteAttribute() >> bbbCanadaSiteId
		siteTranslationSpy.getDefaultLocale() >> requestLocale
		siteTranslationSpy.getTranslations() >> "loginPropertyGermanTranslation"
		siteTranslationSpy.getProperty() >> property
		
		nucleusMock.resolveName(SiteTranslation.KEY_SERVICE_PATH) >> keyServiceMock
		
		when : 
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then : 
		
		returnValue == null 
	}

	/*
	 * Alternative branches :
	 *
	 * TODO :
	 *
	 */
	
	def "derivePropertyValue - No property expression returned - null" () {
		
		given :
		
		def returnValue
		RepositoryItemImpl repoItemMock = Mock()
		
		when :
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
		
	}
	
	/*
	 * derivePropertyValue - test cases STARTS
	 *
	 *
	 */
	
	def "derivePropertyValue - No translations found" () {
		
		given :
		
		def propName = "loginPropertyName"
		def siteId = "TBS_BedBathCanada"
		def repositoryKey = "TBS_BedBathCanada"
		def requestLocale = "TBS_BedBathCanada"
		def property = "login"
		def translationName = "loginPropertyGermanTranslation"
		def tempLocale = "TBS_BedBathCanada"
		def tempAttributeName = "login"
		def returnValue
		def attributeValueClob = "bedbath"
		def bbbCanadaSiteId = "BedBathCanada"
		
		ApplicationLogging logger = Mock()
		logger.isLoggingDebug() >> true
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor pDescriptor = new RepositoryPropertyDescriptor(propName)
		RepositoryItemDescriptor pItemDescriptorMock = Mock()
		PropertyExpression propertyExpression = Mock()
		PropertyExpression propertyExpression2 = Mock()
		RepositoryKeyService keyServiceMock = Mock()
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		RepositoryItemMock translationRepoItem2 = new RepositoryItemMock(["id" : "translation02"])
		RepositoryItemMock tempSiteItem = new RepositoryItemMock(["id" : bbbCanadaSiteId])
		List propertyExpressions = new ArrayList<>()
		Set<RepositoryItem> translations = new HashSet<>()
		
		translationRepoItem1.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
											 "attributeName" : tempAttributeName , "attributeValueClob" : attributeValueClob ])
		
		keyServiceMock.getRepositoryKey() >> repositoryKey
		translations.add(translationRepoItem1)
		propertyExpression.getPropertyDescriptor() >> pDescriptor
		propertyExpression2.getPropertyDescriptor() >> pDescriptor
		
		requestMock.getAttribute("siteId") >> siteId
		
		propertyExpressions.addAll([propertyExpression, propertyExpression2])
		
		propertyExpression.evaluate(propName) >> "loginId"
		
		repoItemMock.getRepositoryId() >> "repoItem01"
		repoItemMock.getPropertyValue(translationName) >> null
		
		derivation.getExpressionList() >> propertyExpressions
		siteTranslationSpy.getDefaultProperty() >> "DefaultProperty"
		siteTranslationSpy.getDefaultSiteAttribute() >> siteId
		siteTranslationSpy.getDefaultLocale() >> requestLocale
		siteTranslationSpy.getTranslations() >> "loginPropertyGermanTranslation"
		siteTranslationSpy.getProperty() >> property
		
		nucleusMock.resolveName(SiteTranslation.KEY_SERVICE_PATH) >> keyServiceMock
		
		when :
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
	}
	
	def "derivePropertyValue - No Property found" () {
		
		given :
		
		def propName = "loginPropertyName"
		def siteId = "TBS_BedBathCanada"
		def repositoryKey = "TBS_BedBathCanada"
		def requestLocale = "TBS_BedBathCanada"
		def property = "login"
		def translationName = "loginPropertyGermanTranslation"
		def tempLocale = "TBS_BedBathCanada"
		def tempAttributeName = "login"
		def returnValue
		def attributeValueClob = "bedbath"
		def bbbCanadaSiteId = "BedBathCanada"
		
		ApplicationLogging logger = Mock()
		logger.isLoggingDebug() >> true
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor pDescriptor = new RepositoryPropertyDescriptor(propName)
		RepositoryItemDescriptor pItemDescriptorMock = Mock()
		PropertyExpression propertyExpression = Mock()
		PropertyExpression propertyExpression2 = Mock()
		RepositoryKeyService keyServiceMock = Mock()
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		RepositoryItemMock translationRepoItem2 = new RepositoryItemMock(["id" : "translation02"])
		RepositoryItemMock tempSiteItem = new RepositoryItemMock(["id" : bbbCanadaSiteId])
		List propertyExpressions = new ArrayList<>()
		Set<RepositoryItem> translations = new HashSet<>()
		
		translationRepoItem1.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
											 "attributeName" : tempAttributeName , "attributeValueClob" : attributeValueClob ])
		
		
		keyServiceMock.getRepositoryKey() >> repositoryKey
		translations.add(translationRepoItem1)
		propertyExpression.getPropertyDescriptor() >> pDescriptor
		propertyExpression2.getPropertyDescriptor() >> pDescriptor
		
		requestMock.getAttribute("siteId") >> siteId
		
		propertyExpressions.addAll([propertyExpression, propertyExpression2])
		
		propertyExpression.evaluate(propName) >> "loginId"
		
		repoItemMock.getRepositoryId() >> "repoItem01"
		repoItemMock.getPropertyValue(translationName) >> translations
		
		derivation.getExpressionList() >> propertyExpressions
		siteTranslationSpy.getDefaultProperty() >> "DefaultProperty"
		siteTranslationSpy.getDefaultSiteAttribute() >> siteId
		siteTranslationSpy.getDefaultLocale() >> requestLocale
		siteTranslationSpy.getTranslations() >> "loginPropertyGermanTranslation"
		siteTranslationSpy.getProperty() >> ""
		
		nucleusMock.resolveName(SiteTranslation.KEY_SERVICE_PATH) >> keyServiceMock
		
		when :
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
	}

	/*
	 * Alternative branches :
	 *
	 *
	 * #218 - if(null == propName && getDefaultProperty()!=null)  - getDefaultProperty()!=null - false
	 * #226 - if(null != ServletUtil.getCurrentRequest()) - false
	 * #260 - if(siteId.equals("TBS_BedBathUS") ) - true
	 * #271 - if(siteId.equalsIgnoreCase(getDefaultSiteAttribute()) && reqLocale.equalsIgnoreCase(getDefaultLocale()) ) - true
	 * #277 - if(pe!=null) - true
	 * 
	 */
	
	def "derivePropertyValue - Default property value is invalid - null" () {
		
		given :
		
		def propName = "loginPropertyName"
		def siteId = "TBS_BedBathCanada"
		def repositoryKey = "TBS_BedBathCanada"
		def requestLocale = "TBS_BedBathCanada"
		def property = "login"
		def translationName = "loginPropertyGermanTranslation"
		def tempLocale = "TBS_BedBathCanada"
		def tempAttributeName = "login"
		def returnValue
		def attributeValueClob = "bedbath"
		def bbbCanadaSiteId = "BedBathCanada"
		
		ApplicationLogging logger = Mock()
		logger.isLoggingDebug() >> true
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor pDescriptor = new RepositoryPropertyDescriptor(propName)
		RepositoryItemDescriptor pItemDescriptorMock = Mock()
		PropertyExpression propertyExpression = Mock()
		PropertyExpression propertyExpression2 = Mock()
		RepositoryKeyService keyServiceMock = Mock()
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		RepositoryItemMock translationRepoItem2 = new RepositoryItemMock(["id" : "translation02"])
		RepositoryItemMock tempSiteItem = new RepositoryItemMock(["id" : bbbCanadaSiteId])
		List propertyExpressions = new ArrayList<>()
		Set<RepositoryItem> translations = new HashSet<>()
		
		translationRepoItem1.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
											 "attributeName" : tempAttributeName , "attributeValueClob" : attributeValueClob ])
		
		keyServiceMock.getRepositoryKey() >> repositoryKey
		translations.add(translationRepoItem1)
		propertyExpression.getPropertyDescriptor() >> pDescriptor
		propertyExpression2.getPropertyDescriptor() >> pDescriptor
		
		requestMock.getAttribute("siteId") >> bbbCanadaSiteId
		
		propertyExpressions.addAll([propertyExpression, propertyExpression2])
		
		propertyExpression.evaluate(propName) >> "loginId"
		
		repoItemMock.getRepositoryId() >> "repoItem01"
		repoItemMock.getPropertyValue(translationName) >> translations
		
		derivation.getExpressionList() >> propertyExpressions
		
		siteTranslationSpy.getDefaultProperty() >> null
		siteTranslationSpy.getDefaultSiteAttribute() >> "BedBathUS"
		siteTranslationSpy.getDefaultLocale() >> requestLocale
		siteTranslationSpy.getTranslations() >> "loginPropertyGermanTranslation"
		siteTranslationSpy.getProperty() >> property
		siteTranslationSpy.getCurrentSiteId() >> "TBS_BedBathUS"
		
		
		nucleusMock.resolveName(SiteTranslation.KEY_SERVICE_PATH) >> keyServiceMock
		
		ServletUtil.setCurrentRequest(null)
		
		when :
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == null
	}
	
	def "derivePropertyValue - Translations exists for a property" () {
		
		given :
		
		def propName = "loginPropertyName"
		def siteId = "TBS_BedBathCanada"
		def repositoryKey = "TBS_BedBathCanada"
		def requestLocale = "TBS_BedBathCanada"
		def property = "login"
		def translationName = "loginPropertyGermanTranslation"
		def tempLocale = "TBS_BedBathCanada"
		def tempAttributeName = "login"
		def returnValue
		def attributeValueClob = "bedbath"
		def bbbCanadaSiteId = "BedBathCanada"
		
		ApplicationLogging logger = Mock()
		logger.isLoggingDebug() >> true
		RepositoryItemImpl repoItemMock = Mock()
		RepositoryPropertyDescriptor pDescriptor = new RepositoryPropertyDescriptor(propName)
		RepositoryItemDescriptor pItemDescriptorMock = Mock()
		PropertyExpression propertyExpression = Mock()
		PropertyExpression propertyExpression2 = Mock()
		RepositoryKeyService keyServiceMock = Mock()
		RepositoryItemMock translationRepoItem1 = new RepositoryItemMock(["id" : "translation01"])
		RepositoryItemMock translationRepoItem2 = new RepositoryItemMock(["id" : "translation02"])
		RepositoryItemMock tempSiteItem = new RepositoryItemMock(["id" : bbbCanadaSiteId])
		List propertyExpressions = new ArrayList<>()
		Set<RepositoryItem> translations = new HashSet<>()
		
		translationRepoItem1.setProperties([ "site" : tempSiteItem , "locale" : tempLocale ,
											 "attributeName" : tempAttributeName , "attributeValueClob" : attributeValueClob ])
		
		keyServiceMock.getRepositoryKey() >> repositoryKey
		translations.add(translationRepoItem1)
		propertyExpression.getPropertyDescriptor() >> pDescriptor
		propertyExpression2.getPropertyDescriptor() >> pDescriptor
		
		requestMock.getAttribute("siteId") >> bbbCanadaSiteId
		
		propertyExpressions.addAll([propertyExpression, propertyExpression2])
		
		propertyExpression.evaluate(propName) >> "loginId"
		propertyExpression.evaluate(repoItemMock) >> "loginId"
		
		repoItemMock.getRepositoryId() >> "repoItem01"
		repoItemMock.getPropertyValue(translationName) >> translations
		
		derivation.getExpressionList() >> propertyExpressions
		
		siteTranslationSpy.getDefaultProperty() >> propName
		siteTranslationSpy.getDefaultSiteAttribute() >> "BedBathUS"
		//siteTranslationSpy.getDerivationExpressionList() >> propertyExpressions
		siteTranslationSpy.getDefaultLocale() >> requestLocale
		siteTranslationSpy.getTranslations() >> "loginPropertyGermanTranslation"
		siteTranslationSpy.getProperty() >> property
		//siteTranslationSpy.getGlobalNucleus() >> nucleusMock
		siteTranslationSpy.getCurrentSiteId() >> "TBS_BedBathUS"
		
		
		nucleusMock.resolveName(SiteTranslation.KEY_SERVICE_PATH) >> keyServiceMock
		
		ServletUtil.setCurrentRequest(null)
		
		when :
		
		returnValue = siteTranslationSpy.derivePropertyValue(repoItemMock)
		
		then :
		
		returnValue == "loginId"
	}
		
	/*
	 * derivePropertyValue - test cases ENDS
	 *
	 */
	
	/*
	 * getDefaultLanguageAttribute - test cases STARTS
	 *
	 */
	
	def "getDefaultLanguageAttribute - fetching default language successfully" () {
		
		given :
		
		def defaultLanguage = "EN"
		def returnValue 
		
		DerivedPropertyDescriptor derivedPropDesc = Mock()
		
		derivedPropDesc.getValue(SiteTranslation.DEFAULT_LANGUAGE_ATTR) >> defaultLanguage
		
		derivation.getPropertyDescriptor()  >> derivedPropDesc
		
		when :
		
		returnValue = siteTranslationSpy.getDefaultLanguageAttribute()
		
		then : 
		
		returnValue == defaultLanguage
	}
	
	/*
	 * getDefaultLanguageAttribute - test cases ENDS
	 *
	 */
}
