package com.bbb.commerce.catalog

import java.util.List;
import java.util.Set;

import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBGiftRegistryConstants

import atg.nucleus.Nucleus
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.registry.NucleusRegistry
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor
import atg.repository.RepositoryItemImpl
import atg.repository.RepositoryPropertyDescriptor
import atg.repository.UnsupportedFeatureException
import atg.repository.dp.Derivation;
import atg.repository.dp.DerivedPropertyDescriptor
import atg.repository.dp.PropertyExpression
import atg.repository.dp.RepositoryKeyService
import atg.repository.query.PropertyQueryExpression;
import atg.servlet.ServletUtil;
import spock.lang.specification.BBBExtendedSpec;

class SiteLocaleTranslationSpecification extends BBBExtendedSpec  {

	RepositoryItemImpl pItem = Mock()
	SiteLocaleTranslation locale
	Derivation pDerivation =Mock()
	PropertyExpression p1 = Mock()
	RepositoryKeyService keySvice =Mock()
	NucleusRegistry sNucleusRegistry = Mock()
	Nucleus nucleusMock = Mock()
	RepositoryItemDescriptor itemdesc =Mock()
	RepositoryItem tempSiteItem =Mock()
	RepositoryItem tempChannel =Mock()
	RepositoryItem tempChannelTheme =Mock()
	RepositoryItemDescriptor itemdesc1 =Mock()
	RepositoryItem tempSiteItem1 =Mock()
	RepositoryItem tempChannel1 =Mock()
	RepositoryItem tempChannelTheme1 =Mock()
	DerivedPropertyDescriptor pd =Mock()
	Set<RepositoryItem> translations = new HashSet()
	ApplicationLogging mLogger =Mock()
	String st = new String("value")
	String str = new String("returnvalue")

	def setup(){
		locale = Spy()
		locale.setDerivation(pDerivation)
		locale.getLogger() >> mLogger
		mLogger.isLoggingDebug() >> true
		Nucleus.setNucleusRegistry(sNucleusRegistry)
		sNucleusRegistry.getKey() >> "nKey"
		sNucleusRegistry.get("nKey") >> nucleusMock
		Nucleus.sUsingChildNucleii = true
	}

	def"getDefaultProperty,when mDefaultProperty is null"(){

		given:
		1*pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue(SiteLocaleTranslation.DEFAULT_PROPERTY) >> "mDefaultProperty"

		when:
		String obj = locale.getDefaultProperty()

		then:
		obj == "mDefaultProperty"
	}

	def"getDefaultSiteAttribute,when mDefaultSiteAttribute is null"(){

		given:
		1*pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue(SiteLocaleTranslation.DEFAULT_SITE_ATTR) >> "site"

		when:
		String obj = locale.getDefaultSiteAttribute()

		then:
		obj == "site"
	}

	def"getDefaultLanguageAttribute,when mDefaultLanguageAttribute is null"(){

		given:
		1*pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue(SiteLocaleTranslation.DEFAULT_LANGUAGE_ATTR) >> "lang"

		when:
		String obj = locale.getDefaultLanguageAttribute()

		then:
		obj == "lang"
	}

	def"derivePropertyValue, when ExpressionList is null"(){

		given:
		1*pDerivation.getExpressionList() >>null

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == null
	}

	def"derivePropertyValue, when Exception is thrown while obtaining ExpressionList"(){

		given:
		1*pDerivation.getExpressionList() >> [p1]
		1*p1.getPropertyDescriptor() >> {throw new Exception("")}

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == null
	}

	def"derivePropertyValue, determines the derived property value when reqEmailType is empty"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()
		mLogger.isLoggingDebug() >> false

		1*locale.extractSiteID() >>""
		2*locale.getDefaultSiteAttribute() >> ""
		0*pItem.getRepositoryId()
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "prop"
		1*pItem.getPropertyValue("prop") >> st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> "true"
		1*requestMock.getHeader("X-bbb-channel") >> ""
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> null
		1*locale.getKeyService() >> null
		2*locale.getDefaultLocale() >> "mDefaultLocale"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "reqConfigurableType"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> ""

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == st
		st.equals("value") == true
	}

	def"derivePropertyValue, determines the derived property value when reqEmailType is null"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()

		1*locale.extractSiteID() >>""
		2*locale.getDefaultSiteAttribute() >> ""
		1*pItem.getRepositoryId() >> "repID"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "prop"
		1*pItem.getPropertyValue("prop") >> st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> "true"
		1*requestMock.getHeader("X-bbb-channel") >> ""
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> null
		1*locale.getKeyService() >> keySvice
		1*keySvice.getRepositoryKey() >> "reqLocale"
		1*locale.getDefaultLocale() >> "mDefaultLocale"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "reqConfigurableType"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> null

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"
		1*pItem.getPropertyValue("translationName") >> null

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == st
		st.equals("value") == true
	}

	def"derivePropertyValue, determines the derived property value when reqEmailType is null,property is null"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()
		mLogger.isLoggingDebug() >> false

		1*locale.extractSiteID() >>"tbs"
		1*locale.getDefaultSiteAttribute() >> "canada"
		1*pItem.getRepositoryId() >> "repID"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "prop"
		1*pItem.getPropertyValue("prop") >> st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> null
		1*locale.getKeyService() >> keySvice
		1*keySvice.getRepositoryKey() >> "reqLocale"

		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "reqConfigurableType"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> null

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> null
		translations.add(r1)
		1*pItem.getPropertyValue("translationName") >> translations

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == st
		st.equals("value") == true
	}

	def"derivePropertyValue, determines the derived property value when reqEmailType is null,property is empty"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()

		1*locale.extractSiteID() >>"tbs"
		1*locale.getDefaultSiteAttribute() >> "canada"
		1*pItem.getRepositoryId() >> "repID"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "prop"
		1*pItem.getPropertyValue("prop") >> st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> null
		1*locale.getKeyService() >> keySvice
		1*keySvice.getRepositoryKey() >> "reqLocale"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "reqConfigurableType"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> null

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> ""
		translations.add(r1)
		1*pItem.getPropertyValue("translationName") >> translations

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == st
		st.equals("value") == true
	}

	def"derivePropertyValue, when Exception is thrown"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()

		1*locale.extractSiteID() >>"tbs"
		1*locale.getDefaultSiteAttribute() >> "canada"
		1*pItem.getRepositoryId() >> "repID"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "prop"
		1*pItem.getPropertyValue("prop") >> st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> null
		1*locale.getKeyService() >> keySvice
		1*keySvice.getRepositoryKey() >> "reqLocale"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "reqConfigurableType"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> null

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> ""
		1*pItem.getPropertyValue("translationName") >> []

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == st
		st.equals("value") == true
	}

	def"derivePropertyValue, determines the derived property value when reqEmailType is null,translationList is empty"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()

		1*locale.extractSiteID() >>"tbs"
		1*locale.getDefaultSiteAttribute() >> "canada"
		1*pItem.getRepositoryId() >> "repID"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "prop"
		1*pItem.getPropertyValue("prop") >> st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		/*1*requestMock.getHeader("X-bbb-channel") >> ""*/
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> null
		1*locale.getKeyService() >> keySvice
		1*keySvice.getRepositoryKey() >> "reqLocale"

		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "reqConfigurableType"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> null

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"
		1*pItem.getPropertyValue("translationName") >> translations

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == st
		st.equals("value") == true
	}

	def"derivePropertyValue, determines the derived property value"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()

		1*locale.extractSiteID() >>"tbs"
		1*pItem.getRepositoryId() >> "repId"
		1*pDerivation.getExpressionList() >> [p1]
		1*p1.getPropertyDescriptor() >> rdesc
		1*rdesc.getName() >> "RepositoryPropertyDescriptor"
		1*p1.evaluate(pItem) >> st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> "true"
		1*requestMock.getParameter(BBBCoreConstants.SITE_ID)  >> "TBS_BedBathUS"

		1*requestMock.getHeader("X-bbb-channel") >> BBBCoreConstants.FF1
		1*requestMock.getHeader("X-bbb-channel-theme") >> "channelTheme"
		1*locale.getKeyService() >> keySvice
		1*keySvice.getRepositoryKey() >> "reqLocale"

		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "reqConfigurableType"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> "reqEmailType"

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"
		1*pd.getValue("defaultLocale") >> "tempLocale"

		translations.add(r1)
		1*pItem.getPropertyValue("translationName") >> translations

		1*r1.getPropertyValue("site") >> tempSiteItem1
		1*r1.getPropertyValue("locale") >> "tempLocale"
		r1.getItemDescriptor() >>itemdesc
		itemdesc.hasProperty("channel") >> true
		itemdesc.hasProperty("channelTheme") >> true
		itemdesc.hasProperty("configurableType") >> true
		1*r1.getPropertyValue("channel") >> tempChannel1
		1*r1.getPropertyValue("channelTheme") >> tempChannelTheme1
		1*r1.getPropertyValue("configurableType") >> "tempConfigurableType"
		1*tempSiteItem1.getRepositoryId() >>"BedBathUS"
		1*tempChannel1.getRepositoryId() >> BBBCoreConstants.FF1
		1*tempChannelTheme1.getRepositoryId() >> "channelTheme"
		r1.getPropertyValue("property") >>  str

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == str
		str.equals("returnvalue") == true
	}

	def"derivePropertyValue, determines the derived property value when tempChannelTheme repsositoryID is not equal to channelThemeID "(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r2 =Mock()

		1*locale.extractSiteID() >>"tbs"
		1*pItem.getRepositoryId() >> "repId"
		1*pDerivation.getExpressionList() >> [p1]
		1*p1.getPropertyDescriptor() >> rdesc
		1*rdesc.getName() >> "RepositoryPropertyDescriptor"
		1*p1.evaluate(pItem) >> st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> "true"
		1*requestMock.getParameter(BBBCoreConstants.SITE_ID)  >> "TBS_BedBathUS"

		1*requestMock.getHeader("X-bbb-channel") >> BBBCoreConstants.FF1
		1*requestMock.getHeader("X-bbb-channel-theme") >> "channelTheme"
		1*locale.getKeyService() >> keySvice
		1*keySvice.getRepositoryKey() >> "reqLocale"

		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "reqConfigurableType"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> "reqEmailType"

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"

		translations.add(r2)
		1*pItem.getPropertyValue("translationName") >> translations

		r2.getPropertyValue("site") >> tempSiteItem
		r2.getPropertyValue("locale") >> "tempLocale"
		r2.getItemDescriptor() >>itemdesc
		itemdesc.hasProperty("channel") >> true
		r2.getPropertyValue("channel") >> tempChannel
		itemdesc.hasProperty("channelTheme") >> true
		r2.getPropertyValue("channelTheme") >> tempChannelTheme
		itemdesc.hasProperty("configurableType") >> true
		r2.getPropertyValue("configurableType") >> "tempConfigurableType"
		tempSiteItem.getRepositoryId() >>"BedBathUS"
		pd.getValue("defaultLocale") >> "tempLocale"
		tempChannel.getRepositoryId() >> BBBCoreConstants.FF1
		tempChannelTheme.getRepositoryId() >> "abc"

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == st
		st.equals("value") == true
	}

	def"derivePropertyValue, determines the derived property value, when tempconfigurableItem is equal to reqConfigurableItem"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()
		RepositoryItem r2 =Mock()

		1*locale.extractSiteID() >>""
		1*pItem.getRepositoryId() >> "repId"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "default"
		1*pItem.getPropertyValue("default") >>st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		1*locale.getDefaultSiteAttribute() >> "TBS_BuyBuyBaby"

		1*requestMock.getHeader("X-bbb-channel") >> ""
		2*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.FF2
		1*requestMock.getHeader("X-bbb-channel-theme") >> "channelTheme"
		1*locale.getKeyService() >> null
		2*locale.getDefaultLocale() >> "tempLocale"

		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "tempConfigurableType"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> "reqEmailType"

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"

		Set<RepositoryItem> translations = new HashSet()
		translations.add(r1)
		1*pItem.getPropertyValue("translationName") >> translations

		r1.getPropertyValue("site") >> tempSiteItem
		r1.getPropertyValue("locale") >> "tempLocale"
		r1.getItemDescriptor() >>itemdesc
		itemdesc.hasProperty("channel") >> true
		1*r1.getPropertyValue("channel") >> tempChannel
		itemdesc.hasProperty("channelTheme") >> false
		itemdesc.hasProperty("configurableType") >> true
		1*r1.getPropertyValue("configurableType") >> "tempConfigurableType"
		tempSiteItem.getRepositoryId() >>"BuyBuyBaby"
		tempChannel.getRepositoryId() >> BBBCoreConstants.FF2
		r1.getPropertyValue("property") >>  str

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == str
		str.equals("returnvalue") == true
	}

	def"derivePropertyValue, determines the derived property value, when tempChannel repositoryId is not equal to channelID"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r2 =Mock()

		1*locale.extractSiteID() >>""
		1*pItem.getRepositoryId() >> "repId"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "default"
		1*pItem.getPropertyValue("default") >>st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		1*locale.getDefaultSiteAttribute() >> "TBS_BuyBuyBaby"

		1*requestMock.getHeader("X-bbb-channel") >> ""
		2*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.FF2
		1*requestMock.getHeader("X-bbb-channel-theme") >> "channelTheme"
		1*locale.getKeyService() >> null
		2*locale.getDefaultLocale() >> "tempLocale"

		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "tempConfigurableType"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> "reqEmailType"

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"

		Set<RepositoryItem> translations = new HashSet()
		translations.add(r2)
		1*pItem.getPropertyValue("translationName") >> translations

		r2.getPropertyValue("site") >> tempSiteItem1
		r2.getPropertyValue("locale") >> "tempLocale"
		r2.getItemDescriptor() >>itemdesc
		itemdesc.hasProperty("channelTheme") >> false
		itemdesc.hasProperty("configurableType") >> true
		itemdesc.hasProperty("channel") >> true
		r2.getPropertyValue("channel") >> tempChannel1
		r2.getPropertyValue("configurableType") >> "tempConfigurableType"
		tempSiteItem1.getRepositoryId() >>"BuyBuyBaby"
		pd.getValue("defaultLocale") >> "tempLocale"
		tempChannel1.getRepositoryId() >> "rep"

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == st
		st.equals("value") == true
	}

	def"derivePropertyValue, determines the derived property value, when reqConfigurableItem is empty"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()

		1*locale.extractSiteID() >> null
		1*pItem.getRepositoryId() >> "repId"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "default"
		1*pItem.getPropertyValue("default") >>st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		1*locale.getDefaultSiteAttribute() >> "TBS_BedBathCanada"

		1*requestMock.getHeader("X-bbb-channel") >> null
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> null
		0*requestMock.getHeader("X-bbb-channel-theme")
		1*locale.getKeyService() >> null
		2*locale.getDefaultLocale() >> "tempLocale"

		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> ""
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> "reqEmailType"

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"

		Set<RepositoryItem> translations = new HashSet()
		translations.add(r1)
		1*pItem.getPropertyValue("translationName") >> translations

		r1.getPropertyValue("site") >> tempSiteItem
		r1.getPropertyValue("locale") >> "tempLocale"
		r1.getItemDescriptor() >>itemdesc
		itemdesc.hasProperty("channel") >> true
		1*r1.getPropertyValue("channel") >> tempChannel
		itemdesc.hasProperty("channelTheme") >> false
		itemdesc.hasProperty("configurableType") >> true
		1*r1.getPropertyValue("configurableType") >> "tempConfigurableType"
		tempSiteItem.getRepositoryId() >>"BedBathCanada"
		tempChannel.getRepositoryId() >> "DesktopWeb"
		r1.getPropertyValue("property") >>  str

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == str
		str.equals("returnvalue") == true
	}

	def"derivePropertyValue, determines the derived property value, when tempConfigurableType is not equal to reqConfigurableType "(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()

		1*locale.extractSiteID() >> null
		1*pItem.getRepositoryId() >> "repId"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "default"
		1*pItem.getPropertyValue("default") >>st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		1*locale.getDefaultSiteAttribute() >> "TBS_BedBathCanada"

		1*requestMock.getHeader("X-bbb-channel") >> null
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> null
		0*requestMock.getHeader("X-bbb-channel-theme")
		1*locale.getKeyService() >> null
		2*locale.getDefaultLocale() >> "tempLocale"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "req"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> "reqEmailType"

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"

		translations.add(r1)
		1*pItem.getPropertyValue("translationName") >> translations

		r1.getPropertyValue("site") >> tempSiteItem
		r1.getPropertyValue("locale") >> "tempLocale"
		r1.getItemDescriptor() >>itemdesc
		itemdesc.hasProperty("channel") >> true
		1*r1.getPropertyValue("channel") >> tempChannel
		itemdesc.hasProperty("channelTheme") >> false
		itemdesc.hasProperty("configurableType") >> true
		1*r1.getPropertyValue("configurableType") >> "tempConfigurableType"
		tempSiteItem.getRepositoryId() >>"BedBathCanada"
		tempChannel.getRepositoryId() >> "DesktopWeb"

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == st
		st.equals("value") == true
	}

	def"derivePropertyValue, determines the derived property value, when  reqConfigurableType is empty"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()

		1*locale.extractSiteID() >> null
		1*pItem.getRepositoryId() >> "repId"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "default"
		1*pItem.getPropertyValue("default") >>st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		1*locale.getDefaultSiteAttribute() >> "TBS_BedBathCanada"

		1*requestMock.getHeader("X-bbb-channel") >> ""
		2*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.FF1
		1*requestMock.getHeader("X-bbb-channel-theme") >> "X-BBB"
		1*locale.getKeyService() >> null
		3*locale.getDefaultLocale() >> "tempLocale"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "req"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> "reqEmailType"

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"

		translations.add(r1)
		1*pItem.getPropertyValue("translationName") >> translations

		r1.getPropertyValue("site") >> tempSiteItem
		r1.getPropertyValue("locale") >> "tempLocale"
		r1.getItemDescriptor() >>itemdesc
		itemdesc.hasProperty("channel") >> true
		1*r1.getPropertyValue("channel") >> tempChannel
		itemdesc.hasProperty("channelTheme") >> false
		itemdesc.hasProperty("configurableType") >> true
		1*r1.getPropertyValue("configurableType") >> ""
		tempSiteItem.getRepositoryId() >>"BedBathCanada"
		tempChannel.getRepositoryId() >> BBBCoreConstants.FF1
		r1.getPropertyValue("property") >>  str

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == str
		str.equals("returnvalue") == true
	}

	def"derivePropertyValue, determines the derived property value, when  reqConfigurableType is empty is not equal to tempconfigurableType"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()

		1*locale.extractSiteID() >> null
		1*pItem.getRepositoryId() >> "repId"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "default"
		1*pItem.getPropertyValue("default") >>st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		1*locale.getDefaultSiteAttribute() >> "TBS_BedBathCanada"
		1*requestMock.getHeader("X-bbb-channel") >> ""
		2*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.FF1
		1*requestMock.getHeader("X-bbb-channel-theme") >> "X-BBB"
		1*locale.getKeyService() >> null
		2*locale.getDefaultLocale() >> "tempLocale"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "req"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> "reqEmailType"

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"

		translations.add(r1)
		1*pItem.getPropertyValue("translationName") >> translations

		r1.getPropertyValue("site") >> tempSiteItem
		r1.getPropertyValue("locale") >> "tempLocale"
		r1.getItemDescriptor() >>itemdesc
		itemdesc.hasProperty("channel") >> true
		1*r1.getPropertyValue("channel") >> tempChannel
		itemdesc.hasProperty("channelTheme") >> false
		itemdesc.hasProperty("configurableType") >> true
		1*r1.getPropertyValue("configurableType") >> "conTy"
		tempSiteItem.getRepositoryId() >>"BedBathCanada"
		tempChannel.getRepositoryId() >> BBBCoreConstants.FF1
		r1.getPropertyValue("property") >>  str

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == st
		st.equals("value") == true
	}

	def"derivePropertyValue, when tempChannel is null"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()

		1*locale.extractSiteID() >> null
		1*pItem.getRepositoryId() >> "repId"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "default"
		1*pItem.getPropertyValue("default") >>st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		1*locale.getDefaultSiteAttribute() >> "TBS_BedBathCanada"

		1*requestMock.getHeader("X-bbb-channel") >> null
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> null
		0*requestMock.getHeader("X-bbb-channel-theme")
		1*locale.getKeyService() >> null
		3*locale.getDefaultLocale() >> "tempLocale"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "req"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> "reqEmailType"

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"

		translations.add(r1)
		1*pItem.getPropertyValue("translationName") >> translations

		r1.getPropertyValue("site") >> tempSiteItem
		r1.getPropertyValue("locale") >> "tempLocale"
		r1.getItemDescriptor() >>itemdesc
		itemdesc.hasProperty("channel") >> false
		itemdesc.hasProperty("channelTheme") >> false
		itemdesc.hasProperty("configurableType") >> false
		tempSiteItem.getRepositoryId() >>"BedBathCanada"
		r1.getPropertyValue("property") >>  str

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == str
		str.equals("returnvalue") == true
	}

	def"derivePropertyValue, when tempLocale is null"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()

		1*locale.extractSiteID() >> null
		1*pItem.getRepositoryId() >> "repId"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "default"
		1*pItem.getPropertyValue("default") >>st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		1*locale.getDefaultSiteAttribute() >> "TBS_BedBathCanada"

		1*requestMock.getHeader("X-bbb-channel") >> null
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> null
		0*requestMock.getHeader("X-bbb-channel-theme")
		1*locale.getKeyService() >> null
		2*locale.getDefaultLocale() >> "tempLocale"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "req"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> "reqEmailType"

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"
		translations.add(r1)
		1*pItem.getPropertyValue("translationName") >> translations
		r1.getPropertyValue("site") >> tempSiteItem
		tempSiteItem.getRepositoryId() >>"BedBathCanada"
		r1.getItemDescriptor() >>itemdesc
		itemdesc.hasProperty("channel") >> false
		r1.getPropertyValue("locale") >> null

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == st
		st.equals("value") == true
	}

	def"derivePropertyValue, when tempLocale is not equal to defaultLocale"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()

		1*locale.extractSiteID() >> null
		1*pItem.getRepositoryId() >> "repId"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "default"
		1*pItem.getPropertyValue("default") >>st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		1*locale.getDefaultSiteAttribute() >> "TBS_BedBathCanada"

		1*requestMock.getHeader("X-bbb-channel") >> null
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> null
		0*requestMock.getHeader("X-bbb-channel-theme")
		1*locale.getKeyService() >> null
		3*locale.getDefaultLocale() >> "tempLocale"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "req"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> "reqEmailType"

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"
		translations.add(r1)
		1*pItem.getPropertyValue("translationName") >> translations
		r1.getPropertyValue("site") >> tempSiteItem
		r1.getPropertyValue("locale") >> "temp"
		tempSiteItem.getRepositoryId() >>"BedBathCanada"
		r1.getItemDescriptor() >>itemdesc
		itemdesc.hasProperty("channel") >> false

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == st
		st.equals("value") == true
	}

	def"derivePropertyValue, when tempSiteItem is null"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()

		1*locale.extractSiteID() >> null
		1*pItem.getRepositoryId() >> "repId"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "default"
		1*pItem.getPropertyValue("default") >>st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		1*locale.getDefaultSiteAttribute() >> "TBS_BedBathCanada"

		1*requestMock.getHeader("X-bbb-channel") >> null
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> null
		0*requestMock.getHeader("X-bbb-channel-theme")
		1*locale.getKeyService() >> null
		1*locale.getDefaultLocale() >> "tempLocale"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "req"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> "reqEmailType"

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"
		translations.add(r1)
		1*pItem.getPropertyValue("translationName") >> translations
		r1.getPropertyValue("site") >> null
		r1.getItemDescriptor() >>itemdesc

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == st
		st.equals("value") == true
	}

	def"derivePropertyValue, when tempSiteItem repositpryID is not equal to siteID"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()
		RepositoryItem r1 =Mock()

		1*locale.extractSiteID() >> null
		1*pItem.getRepositoryId() >> "repId"
		1*pDerivation.getExpressionList() >> [null]
		1*locale.getDefaultProperty() >> "default"
		1*pItem.getPropertyValue("default") >>st

		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		1*locale.getDefaultSiteAttribute() >> "TBS_BedBathCanada"

		1*requestMock.getHeader("X-bbb-channel") >> null
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> null
		0*requestMock.getHeader("X-bbb-channel-theme")
		1*locale.getKeyService() >> null
		1*locale.getDefaultLocale() >> "tempLocale"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE) >> "req"
		1*requestMock.getParameter(BBBGiftRegistryConstants.EMAIL_TYPE) >> "reqEmailType"

		pDerivation.getPropertyDescriptor() >> pd
		1*pd.getValue("translations") >> "translationName"
		1*pd.getValue("property") >> "property"
		translations.add(r1)
		1*pItem.getPropertyValue("translationName") >> translations
		r1.getPropertyValue("site") >> tempSiteItem
		r1.getItemDescriptor() >>itemdesc
		tempSiteItem.getRepositoryId() >> "tempSite"

		when:
		Object obj = locale.derivePropertyValue(pItem)

		then:
		obj == st
		st.equals("value") == true
	}

	def"derivePropertyValue,UnsupportedFeatureException is thrown"(){
		given:
		String pBean = new String("")

		when:
		locale.derivePropertyValue(pBean)

		then:
		UnsupportedFeatureException e = thrown()
	}

	def"getKeyService,Returns LocaleKeyService service "(){

		given:
		RepositoryKeyService keyserv = Mock()
		1*nucleusMock.resolveName(SiteLocaleTranslation.KEY_SERVICE_PATH) >> keyserv

		when:
		RepositoryKeyService obj =locale.getKeyService()

		then:
		obj==keyserv
	}

	def"getKeyService,when keyService is not instance of RepositoryKeyService"(){

		given:
		RepositoryKeyService keyserv = Mock()
		1*nucleusMock.resolveName(SiteLocaleTranslation.KEY_SERVICE_PATH) >> str

		when:
		RepositoryKeyService obj =locale.getKeyService()

		then:
		obj==null
	}

	def"getKeyService,when nucleus is null"(){

		given:
		RepositoryKeyService keyserv = Mock()
		Nucleus.sUsingChildNucleii = false
		0*nucleusMock.resolveName(SiteLocaleTranslation.KEY_SERVICE_PATH)

		when:
		RepositoryKeyService obj =locale.getKeyService()

		then:
		obj==null
	}
	
	def"createQuery"(){
		
		given:
		int pQueryType=1
		boolean pDerivedPropertyOnLeft=true
		boolean pCountDerivedProperty =true
		QueryExpression pOther= Mock()
		int pOperator =1
		boolean pIgnoreCase =true 
		QueryExpression pMinScore =Mock()
		QueryExpression pSearchStringFormat =Mock()
		Query pItemQuery =Mock()
		QueryBuilder pBuilder =Mock()
		PropertyQueryExpression pParentProperty= Mock()
		List pChildPropertyList=[]
		
		when:
		locale.createQuery(pQueryType, pDerivedPropertyOnLeft, pCountDerivedProperty,pOther,pOperator,  pIgnoreCase,
			              pMinScore,pSearchStringFormat,pItemQuery,pBuilder,pParentProperty,pChildPropertyList)
		
		then:
		UnsupportedFeatureException e = thrown()	
	}

}
