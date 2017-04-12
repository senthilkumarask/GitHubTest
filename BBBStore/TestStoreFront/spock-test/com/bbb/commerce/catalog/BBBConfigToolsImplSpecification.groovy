package com.bbb.commerce.catalog

import atg.nucleus.ServiceMap
import atg.repository.MutableRepository
import atg.repository.NamedQueryView
import atg.repository.ParameterSupportView;
import atg.repository.Query
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemImpl
import atg.repository.RepositoryView
import atg.servlet.ServletUtil

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar
import java.util.Date;
import java.util.HashMap
import java.util.List;
import java.util.Map;
import java.util.Set

import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.cache.BBBWebCacheIF
import com.bbb.framework.goldengate.DCPrefixIdGenerator
import spock.lang.specification.BBBExtendedSpec;

class BBBConfigToolsImplSpecification extends BBBExtendedSpec{

	BBBConfigToolsImpl impl
	BBBWebCacheIF webIF =Mock()
	MutableRepository mRep =Mock()
	BBBWebCacheIF configCacheContainer =Mock()
	Query query =Mock()
	NamedQueryView view =Mock()
	Repository repo =Mock()

	def setup(){
		impl = new BBBConfigToolsImpl()
		impl.setCacheName("coherence")
		impl.setCacheTimeout(10)
		impl.setConfigCacheContainer(webIF)
		impl.setConfigureRepository(mRep)
		impl.setAdditionalLoggingDebug(true)
		impl.setPreviewDataPath("")
		impl.setBbbManagedCatalogRepository(repo)
		impl.setLblTxtTemplateManager(null)
	}

	private setParamertersForSpy(){
		impl = Spy()
		impl.setCacheName("coherence")
		impl.setCacheTimeout(10)
		impl.setConfigCacheContainer(webIF)
		impl.extractSiteID() >> "tbs"
		impl.setConfigureRepository(mRep)
		impl.setAdditionalLoggingDebug(true)
	}

	def "getAllValuesForKeyRestCall. checks if configType is empty."() {

		given:
		impl = Spy()
		String configType = ""
		String key = "123"

		when:
		impl.getAllValuesForKeyRestCall(configType, key)

		then:
		1*impl.logError("catalog_1011: Config Type passed to getAllValuesForKey() method is Null")
		1*impl.logError("BBBCatalogToolsImpl.getAllValuesForKeyRestCall() :: " + BBBCoreErrorConstants.CATALOG_ERROR_1010)
	}

	def "getAllValuesForKeyRestCall, when getAllValuesForKey is called with ConfigCacheContainer not null,cache values retrieved from ConfigCacheContainer are not null "() {

		given:
		String configType = "config"
		String key = "123"

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> ""
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		2*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> "true"
		1*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "canada"
		2*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> "" >> "channel1"
		//ends getCacheKeyName
		2*webIF.get(_,"coherence") >> ["str1", "str2"]

		when:
		List<String> allValues = impl.getAllValuesForKeyRestCall(configType, key)

		then:
		allValues == ["str1","str2"]
	}

	def "getAllValuesForKeyRestCall, when getAllValuesForKey is called with cache values retrieved from ConfigCacheContainer are null, site and chennel are not empty(getValueFromComponent)"() {

		given:
		setParamertersForSpy()
		RepositoryItem rItem =Mock()
		String configType = BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE
		String key = "stage"
		impl.setOverrideEnabledFromComponent(true)
		impl.setStagingServer(true)
		impl.setStagingSuffix("suf")

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "tbs"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		2*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> "false"
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		0*requestMock.getParameter(BBBCoreConstants.CHANNEL)
		//ends getCacheKeyName

		1*webIF.get(_,"coherence") >> null
		1*impl.executeConfigNamedRQLQuery(BBBCatalogConstants.IDN_CONFIGKEYS_RQL_GET_CONFIGKEYS_BY_CONFIGTYPE,_, BBBCatalogConstants.IDN_CONFIGKEYS)  >> [rItem]
		1*impl.getSiteAndChannelId() >> ["arr1","arr2"]

		//starts getValueFromComponent
		DCPrefixIdGenerator dcGen =Mock()
		BBBConfigKeySiteChannelMapper sitechMap =Mock()
		BBBConfigKeyValues siteChannelMapComponent =Mock()
		ServiceMap serMap =Mock()
		impl.setIdgen(dcGen)
		impl.setConfigKeySiteChannelMapper(sitechMap)

		1*dcGen.getDcPrefix() >> "dcPre"
		1*sitechMap.getSiteChannelToConfigKeyMap() >> serMap
		1*serMap.get("arr1" + "arr2") >> siteChannelMapComponent
		Map<String,String> tempMap = new HashMap()
		tempMap.put("NotifyRegistrant#stagesuf", "1")
		2*siteChannelMapComponent.getConfigKeyValuesMap() >> tempMap
		//ends getValueFromComponent

		when:
		List<String> allValues = impl.getAllValuesForKeyRestCall(configType, key)

		then:
		allValues == ["1"]
	}

	def "getAllValuesForKeyRestCall, when getAllValuesForKey is called with ConfigCacheContainer null, isStagingServer is false(getValueFromComponent)"() {

		given:
		setParamertersForSpy()
		RepositoryItem rItem =Mock()
		String configType = BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE
		String key = "stagesuf"
		impl.setOverrideEnabledFromComponent(true)
		impl.setConfigCacheContainer(null)
		impl.setStagingServer(false)
		impl.setStagingSuffix("suf")

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "tbs"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		2*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> "false"
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		0*requestMock.getParameter(BBBCoreConstants.CHANNEL)
		//ends getCacheKeyName

		0*webIF.get(_,"coherence")
		1*impl.executeConfigNamedRQLQuery(BBBCatalogConstants.IDN_CONFIGKEYS_RQL_GET_CONFIGKEYS_BY_CONFIGTYPE,_, BBBCatalogConstants.IDN_CONFIGKEYS)  >> [rItem]
		1*impl.getSiteAndChannelId() >> ["arr1","arr2"]

		//starts getValueFromComponent
		DCPrefixIdGenerator dcGen =Mock()
		BBBConfigKeySiteChannelMapper sitechMap =Mock()
		BBBConfigKeyValues siteChannelMapComponent =Mock()
		BBBConfigKeyValues siteChannelMapComponent1 =Mock()
		ServiceMap serMap =Mock()
		impl.setIdgen(dcGen)
		impl.setConfigKeySiteChannelMapper(sitechMap)
		3*dcGen.getDcPrefix() >> "pre"
		2*sitechMap.getSiteChannelToConfigKeyMap() >> serMap
		1*serMap.get("arr1" + "arr2") >> siteChannelMapComponent
		1*siteChannelMapComponent.getConfigKeyValuesMap() >> null

		1*serMap.get("arr1") >> siteChannelMapComponent1
		Map<String,String> tempMap = new HashMap()
		tempMap.put("NotifyRegistrant#prestagesuf", "1")
		2*siteChannelMapComponent1.getConfigKeyValuesMap() >> tempMap
		//ends getValueFromComponent

		when:
		List<String> allValues = impl.getAllValuesForKeyRestCall(configType, key)

		then:
		allValues == ["1"]
	}

	def"getAllValuesForKeyRestCall, when foundInComponent is true,staggingSuffix is empty(getAllValuesForKey)"(){

		given:
		setParamertersForSpy()
		RepositoryItem rItem =Mock()
		String configType = BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE
		String key = "123"
		impl.setStagingServer(true)
		impl.setOverrideEnabledFromComponent(true)
		impl.setStagingSuffix("")

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> ""
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> ""
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "canada"
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> ""
		//ends getCacheKeyName
		1*webIF.get(_,"coherence") >>null

		1*impl.executeConfigNamedRQLQuery(BBBCatalogConstants.IDN_CONFIGKEYS_RQL_GET_CONFIGKEYS_BY_CONFIGTYPE,_, BBBCatalogConstants.IDN_CONFIGKEYS)  >> [rItem]
		1*impl.getSiteAndChannelId() >> ["arr1","arr2"]
		1*impl.getValueFromComponent(configType, key, _, "arr1", "arr2") >> false
		1*impl.getValueFromComponent(configType, key, _, "arr1", null) >> false
		1*impl.getValueFromComponent(configType, key, _, null, null) >> true

		when:
		List<String> allValues = impl.getAllValuesForKeyRestCall(configType, key)

		then:
		allValues == null
		1*impl.logDebug("catalog_1012: No Value found for Key "+key+" in Config Type "+ configType+"  passed to getAllValuesForKey() method")
		1*impl.logError("BBBCatalogToolsImpl.getAllValuesForKeyRestCall() :: " + BBBCoreErrorConstants.CATALOG_ERROR_1010)
	}

	def"getAllValuesForKeyRestCall, when foundInComponent is true, siteChannelArr contains only one element,prefix is empty(getAllValuesForKey)"(){

		given:
		setParamertersForSpy()
		RepositoryItem rItem =Mock()
		DCPrefixIdGenerator dcGen =Mock()
		String configType = "abc"
		String key = null
		impl.setStagingServer(false)
		impl.setOverrideEnabledFromComponent(true)
		impl.setStagingSuffix("")
		impl.setIdgen(dcGen)

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> ""
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> ""
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "canada"
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> ""
		//ends getCacheKeyName
		1*webIF.get(_,"coherence") >>null

		1*impl.executeConfigNamedRQLQuery(BBBCatalogConstants.IDN_CONFIGKEYS_RQL_GET_CONFIGKEYS_BY_CONFIGTYPE,_, BBBCatalogConstants.IDN_CONFIGKEYS)  >> [rItem]
		1*impl.getSiteAndChannelId() >> ["arr1",null]
		1*impl.getValueFromComponent(configType, key, _, "arr1", null) >> false
		1*impl.getValueFromComponent(configType, key, _, null, null) >> true
		1*dcGen.getDcPrefix() >> ""

		when:
		List<String> allValues = impl.getAllValuesForKeyRestCall(configType, key)

		then:
		allValues == null
		1*impl.logError("catalog_1012: No Value found for Key "+key+" in Config Type "+ configType+"  passed to getAllValuesForKey() method")
		1*impl.logError("BBBCatalogToolsImpl.getAllValuesForKeyRestCall() :: " + BBBCoreErrorConstants.CATALOG_ERROR_1010)
	}

	def"getAllValuesForKeyRestCall, when foundInComponent is true, siteChannelArr contains null,prefix is empty(getAllValuesForKey)"(){

		given:
		setParamertersForSpy()
		RepositoryItem rItem =Mock()
		DCPrefixIdGenerator dcGen =Mock()
		String configType = "abc"
		String key = null
		impl.setStagingServer(false)
		impl.setOverrideEnabledFromComponent(true)
		impl.setStagingSuffix("")
		impl.setIdgen(dcGen)

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> ""
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> ""
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "canada"
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> "channelId"
		//ends getCacheKeyName
		1*webIF.get(_,"coherence") >>null

		1*impl.executeConfigNamedRQLQuery(BBBCatalogConstants.IDN_CONFIGKEYS_RQL_GET_CONFIGKEYS_BY_CONFIGTYPE,_, BBBCatalogConstants.IDN_CONFIGKEYS)  >> [rItem]
		1*impl.getSiteAndChannelId() >> [null,null]
		1*impl.getValueFromComponent(configType, key, _, null, null) >> true
		1*dcGen.getDcPrefix() >> ""

		when:
		List<String> allValues = impl.getAllValuesForKeyRestCall(configType, key)

		then:
		allValues == null
		1*impl.logError("catalog_1012: No Value found for Key "+key+" in Config Type "+ configType+"  passed to getAllValuesForKey() method")
		1*impl.logError("BBBCatalogToolsImpl.getAllValuesForKeyRestCall() :: " + BBBCoreErrorConstants.CATALOG_ERROR_1010)
	}

	def"getAllValuesForKeyRestCall, when foundInComponent is true, siteChannelArr contains null,prefix is empty(getAllValuesForKey)."(){

		given:
		setParamertersForSpy()
		RepositoryItem rItem =Mock()
		DCPrefixIdGenerator dcGen =Mock()
		String configType = "abc"
		String key = "123"
		impl.setStagingServer(false)
		impl.setOverrideEnabledFromComponent(true)
		impl.setStagingSuffix("")
		impl.setIdgen(dcGen)

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> ""
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> ""
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "canada"
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> ""
		//ends getCacheKeyName
		1*webIF.get(_,"coherence") >>null

		1*impl.executeConfigNamedRQLQuery(BBBCatalogConstants.IDN_CONFIGKEYS_RQL_GET_CONFIGKEYS_BY_CONFIGTYPE,_, BBBCatalogConstants.IDN_CONFIGKEYS)  >> [rItem]
		1*impl.getSiteAndChannelId() >> ["str1",null]
		1*impl.getValueFromComponent(configType, key, _, "str1", null) >> true
		1*dcGen.getDcPrefix() >> ""

		when:
		List<String> allValues = impl.getAllValuesForKeyRestCall(configType, key)

		then:
		allValues == null
		1*impl.logError("catalog_1012: No Value found for Key "+key+" in Config Type "+ configType+"  passed to getAllValuesForKey() method")
		1*impl.logError("BBBCatalogToolsImpl.getAllValuesForKeyRestCall() :: " + BBBCoreErrorConstants.CATALOG_ERROR_1010)
	}

	def"getAllValuesForKeyRestCall, when isOverrideEnabledFromComponent is false, foundInComponent is false."(){

		given:
		setParamertersForSpy()
		RepositoryItemImpl rItem =Mock()
		RepositoryItem r1 =Mock()
		DCPrefixIdGenerator dcGen =Mock()
		Set<RepositoryItem> configKeyValues = new HashSet()
		configKeyValues.add(r1)
		String configType = "abc"
		String key = "123"
		impl.setStagingServer(false)
		impl.setOverrideEnabledFromComponent(false)
		impl.setStagingSuffix("")
		impl.setIdgen(dcGen)

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> ""
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> ""
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "canada"
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> ""
		//ends getCacheKeyName
		1*webIF.get(_,"coherence") >>null

		1*impl.executeConfigNamedRQLQuery(BBBCatalogConstants.IDN_CONFIGKEYS_RQL_GET_CONFIGKEYS_BY_CONFIGTYPE,_, BBBCatalogConstants.IDN_CONFIGKEYS)  >> [rItem]
		1*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY) >> configKeyValues
		1*r1.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY) >> "key"
		1*r1.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY) >>"value"
		0*impl.getSiteAndChannelId() >> ["str1",null]
		0*impl.getValueFromComponent(configType, key, _, "str1", null)
		1*dcGen.getDcPrefix() >> ""

		when:
		List<String> allValues = impl.getAllValuesForKeyRestCall(configType, key)

		then:
		allValues == null
		1*impl.logError("catalog_1012: No Value found for Key "+key+" in Config Type "+ configType+"  passed to getAllValuesForKey() method")
		1*impl.logError("BBBCatalogToolsImpl.getAllValuesForKeyRestCall() :: " + BBBCoreErrorConstants.CATALOG_ERROR_1010)
	}

	def"getAllValuesForKeyRestCall, when isOverrideEnabledFromComponent is true, foundInComponent is false."(){

		given:
		setParamertersForSpy()
		RepositoryItemImpl rItem =Mock()
		RepositoryItem r1 =Mock()
		DCPrefixIdGenerator dcGen =Mock()
		Set<RepositoryItem> configKeyValues = new HashSet()
		configKeyValues.add(r1)
		String configType = "abc"
		String key = "123"
		impl.setStagingServer(false)
		impl.setOverrideEnabledFromComponent(true)
		impl.setStagingSuffix("")
		impl.setIdgen(dcGen)

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> ""
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> ""
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "canada"
		1*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> ""
		//ends getCacheKeyName
		1*webIF.get(_,"coherence") >>null

		1*impl.executeConfigNamedRQLQuery(BBBCatalogConstants.IDN_CONFIGKEYS_RQL_GET_CONFIGKEYS_BY_CONFIGTYPE,_, BBBCatalogConstants.IDN_CONFIGKEYS)  >> [rItem]
		1*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY) >> configKeyValues
		1*r1.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY) >> "key"
		1*r1.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY) >>"value"
		1*impl.getSiteAndChannelId() >> [null,null]
		1*impl.getValueFromComponent(configType, key, _, null, null) >> false
		1*dcGen.getDcPrefix() >> ""

		when:
		List<String> allValues = impl.getAllValuesForKeyRestCall(configType, key)

		then:
		allValues == null
		1*impl.logError("catalog_1012: No Value found for Key "+key+" in Config Type "+ configType+"  passed to getAllValuesForKey() method")
		1*impl.logError("BBBCatalogToolsImpl.getAllValuesForKeyRestCall() :: " + BBBCoreErrorConstants.CATALOG_ERROR_1010)
	}

	def "getAllValuesForKeyRestCall, when RepositoryException is thrown(getAllValuesForKey)"() {

		given:
		setParamertersForSpy()
		RepositoryItem rItem =Mock()
		String configType = BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE
		String key = "stage"
		impl.setOverrideEnabledFromComponent(true)
		impl.setStagingServer(true)
		impl.setStagingSuffix("suf")

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "tbs"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		2*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> "false"
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		0*requestMock.getParameter(BBBCoreConstants.CHANNEL)
		//ends getCacheKeyName

		1*webIF.get(_,"coherence") >> null
		1*impl.executeConfigNamedRQLQuery(BBBCatalogConstants.IDN_CONFIGKEYS_RQL_GET_CONFIGKEYS_BY_CONFIGTYPE,_, BBBCatalogConstants.IDN_CONFIGKEYS)  >> {throw new RepositoryException()}
		0*impl.getSiteAndChannelId()

		when:
		List<String> allValues = impl.getAllValuesForKeyRestCall(configType, key)

		then:
		allValues == null
		1*impl.logError("catalog_1014: Exception occured in getAllValuesForKey() method:: ")
		1*impl.logError("BBBCatalogToolsImpl.getAllValuesForKeyRestCall() :: " + "2003" , _)
	}

	def"getValueFromComponent, when siteChannelMapComponent is null "(){

		given:
		DCPrefixIdGenerator dcGen =Mock()
		BBBConfigKeyValues values =Mock()
		ServiceMap map = Mock()
		BBBConfigKeySiteChannelMapper mapper =Mock()
		String configType ="config"
		String key ="key"
		String site ="tbs"
		String channel ="channel"
		Map<String, String> configValueForConfigKeys = new HashMap()
		impl.setIdgen(dcGen)
		impl.setStagingServer(true)
		impl.setStagingSuffix("suffix")
		impl.setConfigKeySiteChannelMapper(mapper)
		1*mapper.getSiteChannelToConfigKeyMap() >> map
		1*map.get(site + channel) >> null

		1*dcGen.getDcPrefix() >> ""

		when:
		boolean flag = impl.getValueFromComponent(configType, key,configValueForConfigKeys,site, channel)

		then:
		flag == false
	}

	def"getValueFromComponent, when site is empty, prefix is empty and key ends with suffix"(){

		given:
		DCPrefixIdGenerator dcGen =Mock()
		BBBConfigKeyValues defaultMapComponent =Mock()
		ServiceMap map = Mock()
		BBBConfigKeySiteChannelMapper mapper =Mock()
		String configType ="config"
		String key ="keysuffix"
		String site =""
		String channel ="channel"
		Map<String, String> configValueForConfigKeys = new HashMap()
		impl.setIdgen(dcGen)
		impl.setStagingServer(true)
		impl.setStagingSuffix("suffix")
		impl.setConfigKeySiteChannelMapper(mapper)
		1*mapper.getSiteChannelToConfigKeyMap() >> map
		1*map.get("default") >> defaultMapComponent

		Map<String,String> tempMap = new HashMap()
		tempMap.put("config#keysuffix", "1")
		2*defaultMapComponent.getConfigKeyValuesMap() >> tempMap
		1*dcGen.getDcPrefix() >> ""

		when:
		boolean flag = impl.getValueFromComponent(configType, key,configValueForConfigKeys,site, channel)

		then:
		flag == true
	}

	def"getValueFromComponent, when channel is empty, suffix is empty,siteMapComponent.getConfigKeyValuesMap() is null and  keystarts with prefix"(){

		given:
		DCPrefixIdGenerator dcGen =Mock()
		BBBConfigKeyValues siteMapComponent =Mock()
		ServiceMap map = Mock()
		BBBConfigKeySiteChannelMapper mapper =Mock()
		String configType ="config"
		String key ="prefixkey"
		String site ="tbs"
		String channel =""
		Map<String, String> configValueForConfigKeys = new HashMap()
		impl.setIdgen(dcGen)
		impl.setStagingServer(true)
		impl.setStagingSuffix("")
		impl.setConfigKeySiteChannelMapper(mapper)
		1*mapper.getSiteChannelToConfigKeyMap() >> map
		1*map.get(site) >> siteMapComponent

		1*siteMapComponent.getConfigKeyValuesMap() >>null
		1*dcGen.getDcPrefix() >> "prefix"

		when:
		boolean flag = impl.getValueFromComponent(configType, key,configValueForConfigKeys,site, channel)

		then:
		flag == false
	}

	def"getValueFromComponent, when channel is empty, suffix is empty,siteMapComponent is null and  keystarts with prefix"(){

		given:
		DCPrefixIdGenerator dcGen =Mock()
		BBBConfigKeyValues siteMapComponent =Mock()
		ServiceMap map = Mock()
		BBBConfigKeySiteChannelMapper mapper =Mock()
		String configType ="config"
		String key ="prefixkey"
		String site ="tbs"
		String channel =""
		Map<String, String> configValueForConfigKeys = new HashMap()
		impl.setIdgen(dcGen)
		impl.setStagingServer(true)
		impl.setStagingSuffix("")
		impl.setConfigKeySiteChannelMapper(mapper)
		1*mapper.getSiteChannelToConfigKeyMap() >> map
		1*map.get(site) >> null

		0*siteMapComponent.getConfigKeyValuesMap()
		1*dcGen.getDcPrefix() >> "prefix"

		when:
		boolean flag = impl.getValueFromComponent(configType, key,configValueForConfigKeys,site, channel)

		then:
		flag == false
	}

	def"getValueFromComponent, when site is empty and defaultMapComponent is null"(){

		given:
		DCPrefixIdGenerator dcGen =Mock()
		BBBConfigKeyValues defaultMapComponent =Mock()
		ServiceMap map = Mock()
		BBBConfigKeySiteChannelMapper mapper =Mock()
		String configType ="config"
		String key ="prefixkey"
		String site =""
		String channel =""
		Map<String, String> configValueForConfigKeys = new HashMap()
		impl.setIdgen(dcGen)
		impl.setStagingServer(true)
		impl.setStagingSuffix("")
		impl.setConfigKeySiteChannelMapper(mapper)
		1*mapper.getSiteChannelToConfigKeyMap() >> map
		1*map.get("default") >> null

		0*defaultMapComponent.getConfigKeyValuesMap()
		1*dcGen.getDcPrefix() >> "prefix"

		when:
		boolean flag = impl.getValueFromComponent(configType, key,configValueForConfigKeys,site, channel)

		then:
		flag == false
	}

	def"getValueFromComponent, when site is empty and defaultMapComponent.getConfigKeyValuesMap() is null"(){

		given:
		DCPrefixIdGenerator dcGen =Mock()
		BBBConfigKeyValues defaultMapComponent =Mock()
		ServiceMap map = Mock()
		BBBConfigKeySiteChannelMapper mapper =Mock()
		String configType ="config"
		String key ="prefixkey"
		String site =""
		String channel =""
		Map<String, String> configValueForConfigKeys = new HashMap()
		impl.setIdgen(dcGen)
		impl.setStagingServer(true)
		impl.setStagingSuffix("")
		impl.setConfigKeySiteChannelMapper(mapper)
		1*mapper.getSiteChannelToConfigKeyMap() >> map
		1*map.get("default") >> defaultMapComponent

		1*defaultMapComponent.getConfigKeyValuesMap() >> null
		1*dcGen.getDcPrefix() >> "prefix"

		when:
		boolean flag = impl.getValueFromComponent(configType, key,configValueForConfigKeys,site, channel)

		then:
		flag == false
	}

	def"getValueForConfigKey,when configValue is not empty"(){

		given:
		impl = Spy()
		String configType = "config"
		String key ="key"
		boolean defaultValue = true
		1*impl.getAllValuesForKey(configType,key) >> ["true"]

		when:
		boolean flag = impl.getValueForConfigKey(configType, key, defaultValue)

		then:
		flag == true
	}

	def"getValueForConfigKey, when configValue is empty"(){

		given:
		impl = Spy()
		String configType = "config"
		String key ="key"
		boolean defaultValue = false
		1*impl.getAllValuesForKey(configType,key) >> [""]

		when:
		boolean flag = impl.getValueForConfigKey(configType, key, defaultValue)

		then:
		flag == false
	}

	def"getValueForConfigKey, when configValuesList is null"(){

		given:
		impl = Spy()
		String configType = "config"
		String key ="key"
		boolean defaultValue = false
		1*impl.getAllValuesForKey(configType,key) >> null

		when:
		boolean flag = impl.getValueForConfigKey(configType, key, defaultValue)

		then:
		flag == false
	}

	def"getValueForConfigKey(overloaded),when configValue is not empty"(){

		given:
		impl = Spy()
		String configType = "config"
		String key ="key"
		String defaultValue = "false"
		1*impl.getAllValuesForKey(configType,key) >> ["value"]

		when:
		String flag = impl.getValueForConfigKey(configType, key, defaultValue)

		then:
		flag == "value"
	}

	def"getValueForConfigKey(overloaded), when configValue is empty"(){

		given:
		impl = Spy()
		String configType = "config"
		String key ="key"
		String defaultValue = "false"
		1*impl.getAllValuesForKey(configType,key) >> [""]

		when:
		String flag = impl.getValueForConfigKey(configType, key, defaultValue)

		then:
		flag == "false"
	}

	def"getValueForConfigKey(overloaded), when configValuesList is null"(){

		given:
		impl = Spy()
		String configType = "config"
		String key ="key"
		String defaultValue = "false"
		1*impl.getAllValuesForKey(configType,key) >> null

		when:
		String flag = impl.getValueForConfigKey(configType, key, defaultValue)

		then:
		flag == "false"
	}

	def"getConfigValueByconfigType, when configType is null"(){

		given:
		String configType ="config"

		when:
		Map<String, String> map =impl.getConfigValueByconfigType(null)

		then:
		map == null
		BBBBusinessException e = thrown()
	}

	def"getConfigValueByconfigType, when configCacheContainer is not null and map obtained from configCacheContainer is not null"(){

		given:
		String configType ="config"
		impl.setCacheName("coherence")
		impl.setCacheTimeout(10)
		impl.setConfigCacheContainer(configCacheContainer)

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		//ends
		Map<String, String> configValueForConfigKeys = new HashMap<String, String>()
		configValueForConfigKeys.put("str", "1")
		2*configCacheContainer.get(_, "coherence") >> configValueForConfigKeys

		when:
		Map<String, String> map =impl.getConfigValueByconfigType(configType)

		then:
		map.containsKey("str") == true
		map.containsValue("1") == true
	}

	def"getConfigValueByconfigType, when configCacheContainer is not null and map obtained from configCacheContainer is null, isStagingServer is false"(){

		given:
		setParamertersForSpy()
		DCPrefixIdGenerator dcGen =Mock()
		String configType ="config"
		impl.setConfigCacheContainer(configCacheContainer)
		impl.setStagingServer(false)
		impl.setOverrideEnabledFromComponent(true)
		impl.setIdgen(dcGen)

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		//ends getCacheKeyName

		Map<String, String> configValueForConfigKeys = new HashMap<String, String>()
		1*configCacheContainer.get(_, "coherence") >> null

		//for executeConfigNamedRQLQuery
		RepositoryItemImpl rItem =Mock()
		1*mRep.getView(_) >>view
		1*view.getNamedQuery(_) >>query
		1*impl.extractDBCall(_, view, query) >> [rItem]
		//ends executeConfigNamedRQLQuery

		Set<RepositoryItem> set = new HashSet()
		set.add(rItem)
		1*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY) >> set
		1*impl.getSiteAndChannelId() >> ["str1", "str2"]
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY) >>"PreconfigKey"
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY) >> "configValue"
		impl.getValueFromComponent(configType,"PreconfigKey", _, "str1", "str2") >> false
		1*impl.getValueFromComponent(configType,"PreconfigKey", _, "str1", null) >> false
		impl.getValueFromComponent(configType,"PreconfigKey", _, null, null) >> false
		dcGen.getDcPrefix() >> "Pre"
		1*configCacheContainer.put(_, _, "coherence", 10)

		when:
		Map<String, String> map =impl.getConfigValueByconfigType(configType)

		then:
		map.containsKey("PreconfigKey") == true
		map.containsKey("configKey") == true
		map.containsValue("configValue")== true
	}

	def"getConfigValueByconfigType, when configCacheContainer is null , isStagingServer is true and getSiteAndChannelId list has second element as null "(){

		given:
		setParamertersForSpy()
		String configType ="config"
		impl.setConfigCacheContainer(null)
		impl.setStagingServer(true)
		impl.setOverrideEnabledFromComponent(true)
		impl.setStagingSuffix("suf")

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		//ends getCacheKeyName

		Map<String, String> configValueForConfigKeys = new HashMap<String, String>()
		0*configCacheContainer.get(_, "coherence")

		//for executeConfigNamedRQLQuery
		RepositoryItemImpl rItem =Mock()
		1*mRep.getView(_) >>view
		1*view.getNamedQuery(_) >>query
		1*impl.extractDBCall(_, view, query) >> [rItem]
		//ends executeConfigNamedRQLQuery

		Set<RepositoryItem> set = new HashSet()
		set.add(rItem)
		1*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY) >> set
		1*impl.getSiteAndChannelId() >> ["str1", ""]
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY) >>"configKeysuf"
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY) >> "configValue"
		0*impl.getValueFromComponent(configType,"configKeysuf", _, "str1", "str2")
		1*impl.getValueFromComponent(configType,"configKeysuf", _, "str1", null) >> false
		impl.getValueFromComponent(configType,"configKeysuf", _, null, null) >> false
		0*configCacheContainer.put(_, _, "coherence", 10)

		when:
		Map<String, String> map =impl.getConfigValueByconfigType(configType)

		then:
		map.containsKey("configKeysuf") == true
		map.containsKey("configKey") == true
		map.containsValue("configValue")== true
	}

	def"getConfigValueByconfigType, when configCacheContainer is null , isStagingServer is true and getSiteAndChannelId list has all elements as null"(){

		given:
		setParamertersForSpy()
		String configType ="config"
		impl.setConfigCacheContainer(null)
		impl.setStagingServer(true)
		impl.setOverrideEnabledFromComponent(true)
		impl.setStagingSuffix("suf")

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		//ends getCacheKeyName

		Map<String, String> configValueForConfigKeys = new HashMap<String, String>()
		0*configCacheContainer.get(_, "coherence")

		//for executeConfigNamedRQLQuery
		RepositoryItemImpl rItem =Mock()
		1*mRep.getView(_) >>view
		1*view.getNamedQuery(_) >>query
		1*impl.extractDBCall(_, view, query) >> [rItem]
		//ends executeConfigNamedRQLQuery

		Set<RepositoryItem> set = new HashSet()
		set.add(rItem)
		1*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY) >> set
		1*impl.getSiteAndChannelId() >> ["", ""]
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY) >>"configKey"
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY) >> "configValue"
		0*impl.getValueFromComponent(configType,"configKey", _, "", "")
		0*impl.getValueFromComponent(configType,"configKey", _, "", null)
		1*impl.getValueFromComponent(configType,"configKey", _, null, null) >> false
		0*configCacheContainer.put(_, _, "coherence", 10)

		when:
		Map<String, String> map =impl.getConfigValueByconfigType(configType)

		then:
		map.containsKey("configKeysuf") == false
		map.containsKey("configKey") == true
		map.containsValue("configValue")== true
	}

	def"getConfigValueByconfigType, when configCacheContainer is null , isStagingServer is true ,stagging suffix is empty"(){

		given:
		setParamertersForSpy()
		String configType ="config"
		impl.setConfigCacheContainer(null)
		impl.setStagingServer(true)
		impl.setOverrideEnabledFromComponent(true)
		impl.setStagingSuffix("")

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		//ends getCacheKeyName

		Map<String, String> configValueForConfigKeys = new HashMap<String, String>()
		0*configCacheContainer.get(_, "coherence")

		//for executeConfigNamedRQLQuery
		RepositoryItemImpl rItem =Mock()
		1*mRep.getView(_) >>view
		1*view.getNamedQuery(_) >>query
		1*impl.extractDBCall(_, view, query) >> [rItem]
		//ends executeConfigNamedRQLQuery

		Set<RepositoryItem> set = new HashSet()
		set.add(rItem)
		1*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY) >> set
		1*impl.getSiteAndChannelId() >> ["", ""]
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY) >>"configKey"
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY) >> "configValue"
		0*impl.getValueFromComponent(configType,"configKey", _, "", "")
		0*impl.getValueFromComponent(configType,"configKey", _, "", null)
		1*impl.getValueFromComponent(configType,"configKey", _, null, null) >> false
		0*configCacheContainer.put(_, _, "coherence", 10)

		when:
		Map<String, String> map =impl.getConfigValueByconfigType(configType)

		then:
		map.containsKey("configKey") == true
		map.containsValue("configValue") == true
	}

	def"getConfigValueByconfigType, when configCacheContainer is null , isStagingServer is false ,key does not starts with prefix"(){

		given:
		setParamertersForSpy()
		DCPrefixIdGenerator dcGen =Mock()
		String configType ="config"
		impl.setConfigCacheContainer(null)
		impl.setStagingServer(false)
		impl.setOverrideEnabledFromComponent(true)
		impl.setStagingSuffix("")
		impl.setIdgen(dcGen)
		1*dcGen.getDcPrefix() >> "pre"

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		//ends getCacheKeyName

		Map<String, String> configValueForConfigKeys = new HashMap<String, String>()
		0*configCacheContainer.get(_, "coherence")

		//for executeConfigNamedRQLQuery
		RepositoryItemImpl rItem =Mock()
		1*mRep.getView(_) >>view
		1*view.getNamedQuery(_) >>query
		1*impl.extractDBCall(_, view, query) >> [rItem]
		//ends executeConfigNamedRQLQuery

		Set<RepositoryItem> set = new HashSet()
		set.add(rItem)
		1*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY) >> set
		1*impl.getSiteAndChannelId() >> ["", ""]
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY) >>"configKey"
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY) >> "configValue"
		0*impl.getValueFromComponent(configType,"configKey", _, "", "")
		0*impl.getValueFromComponent(configType,"configKey", _, "", null)
		1*impl.getValueFromComponent(configType,"configKey", _, null, null) >> false
		0*configCacheContainer.put(_, _, "coherence", 10)

		when:
		Map<String, String> map =impl.getConfigValueByconfigType(configType)

		then:
		map.containsKey("configKey") == true
		map.containsValue("configValue") == true
	}

	def"getConfigValueByconfigType, when configCacheContainer is null , isStagingServer is false ,prefix is empty"(){

		given:
		setParamertersForSpy()
		DCPrefixIdGenerator dcGen =Mock()
		String configType ="config"
		impl.setConfigCacheContainer(null)
		impl.setStagingServer(false)
		impl.setOverrideEnabledFromComponent(true)
		impl.setStagingSuffix("")
		impl.setIdgen(dcGen)
		1*dcGen.getDcPrefix() >> ""

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		//ends getCacheKeyName

		Map<String, String> configValueForConfigKeys = new HashMap<String, String>()
		0*configCacheContainer.get(_, "coherence")

		//for executeConfigNamedRQLQuery
		RepositoryItemImpl rItem =Mock()
		1*mRep.getView(_) >>view
		1*view.getNamedQuery(_) >>query
		1*impl.extractDBCall(_, view, query) >> [rItem]
		//ends executeConfigNamedRQLQuery

		Set<RepositoryItem> set = new HashSet()
		set.add(rItem)
		1*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY) >> set
		1*impl.getSiteAndChannelId() >> ["1", "2"]
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY) >>"configKey"
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY) >> "configValue"
		1*impl.getValueFromComponent(configType,"configKey", _, "1", "2") >> false
		1*impl.getValueFromComponent(configType,"configKey", _, "1", null) >> true
		0*impl.getValueFromComponent(configType,"configKey", _, null, null)
		0*configCacheContainer.put(_, _, "coherence", 10)

		when:
		Map<String, String> map =impl.getConfigValueByconfigType(configType)

		then:
		map.containsKey("configKey") == false
		map.containsValue("configValue") == false
	}

	def"getConfigValueByconfigType, when configCacheContainer is null ,getSiteAndChannelId has two elements, getValueFromComponent method returns true in the first call"(){

		given:
		setParamertersForSpy()
		DCPrefixIdGenerator dcGen =Mock()
		String configType ="config"
		impl.setConfigCacheContainer(null)
		impl.setStagingServer(false)
		impl.setOverrideEnabledFromComponent(true)
		impl.setStagingSuffix("")
		impl.setIdgen(dcGen)
		1*dcGen.getDcPrefix() >> ""

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		//ends getCacheKeyName

		Map<String, String> configValueForConfigKeys = new HashMap<String, String>()
		0*configCacheContainer.get(_, "coherence")

		//for executeConfigNamedRQLQuery
		RepositoryItemImpl rItem =Mock()
		1*mRep.getView(_) >>view
		1*view.getNamedQuery(_) >>query
		1*impl.extractDBCall(_, view, query) >> [rItem]
		//ends executeConfigNamedRQLQuery

		Set<RepositoryItem> set = new HashSet()
		set.add(rItem)
		1*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY) >> set
		1*impl.getSiteAndChannelId() >> ["1", "2"]
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY) >>"configKey"
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY) >> "configValue"
		1*impl.getValueFromComponent(configType,"configKey", _, "1", "2") >> true
		0*impl.getValueFromComponent(configType,"configKey", _, "1", null)
		0*impl.getValueFromComponent(configType,"configKey", _, null, null)
		0*configCacheContainer.put(_, _, "coherence", 10)

		when:
		Map<String, String> map =impl.getConfigValueByconfigType(configType)

		then:
		map.containsKey("configKey") == false
		map.containsValue("configValue") == false
	}

	def"getConfigValueByconfigType, when configCacheContainer is null ,getSiteAndChannelId has a single element, getValueFromComponent method returns true in the first call"(){

		given:
		setParamertersForSpy()
		DCPrefixIdGenerator dcGen =Mock()
		String configType ="config"
		impl.setConfigCacheContainer(null)
		impl.setStagingServer(false)
		impl.setOverrideEnabledFromComponent(true)
		impl.setStagingSuffix("")
		impl.setIdgen(dcGen)
		1*dcGen.getDcPrefix() >> ""

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		//ends getCacheKeyName

		Map<String, String> configValueForConfigKeys = new HashMap<String, String>()
		0*configCacheContainer.get(_, "coherence")

		//for executeConfigNamedRQLQuery
		RepositoryItemImpl rItem =Mock()
		1*mRep.getView(_) >>view
		1*view.getNamedQuery(_) >>query
		1*impl.extractDBCall(_, view, query) >> [rItem]
		//ends executeConfigNamedRQLQuery

		Set<RepositoryItem> set = new HashSet()
		set.add(rItem)
		1*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY) >> set
		1*impl.getSiteAndChannelId() >> ["1", ""]
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY) >>"configKey"
		rItem.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY) >> "configValue"
		0*impl.getValueFromComponent(configType,"configKey", _, "1", "")
		1*impl.getValueFromComponent(configType,"configKey", _, "1", null) >> true
		0*impl.getValueFromComponent(configType,"configKey", _, null, null)
		0*configCacheContainer.put(_, _, "coherence", 10)

		when:
		Map<String, String> map =impl.getConfigValueByconfigType(configType)

		then:
		map.containsKey("configKey") == false
		map.containsValue("configValue") == false
	}

	def"getConfigValueByconfigType, when configCacheContainer is null ,isOverrideEnabledFromComponent is false"(){

		given:
		setParamertersForSpy()
		DCPrefixIdGenerator dcGen =Mock()
		String configType ="config"
		impl.setConfigCacheContainer(null)
		impl.setStagingServer(false)
		impl.setOverrideEnabledFromComponent(false)
		impl.setStagingSuffix("")
		impl.setIdgen(dcGen)
		1*dcGen.getDcPrefix() >> ""

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		//ends getCacheKeyName

		Map<String, String> configValueForConfigKeys = new HashMap<String, String>()
		0*configCacheContainer.get(_, "coherence")

		//for executeConfigNamedRQLQuery
		RepositoryItemImpl rItem =Mock()
		1*mRep.getView(_) >>view
		1*view.getNamedQuery(_) >>query
		1*impl.extractDBCall(_, view, query) >> [rItem]
		//ends executeConfigNamedRQLQuery

		Set<RepositoryItem> set = new HashSet()
		set.add(rItem)
		1*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY) >> set
		0*impl.getSiteAndChannelId()
		1*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY) >>"configKey"
		1*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY) >> "configValue"
		0*impl.getValueFromComponent(configType,"configKey", _, "1", "")
		0*impl.getValueFromComponent(configType,"configKey", _, "1", null)
		0*impl.getValueFromComponent(configType,"configKey", _, null, null)
		0*configCacheContainer.put(_, _, "coherence", 10)

		when:
		Map<String, String> map =impl.getConfigValueByconfigType(configType)

		then:
		map.containsKey("configKey") == true
		map.containsValue("configValue") == true
	}

	def"getConfigValueByconfigType, when configKeys are null"(){

		given:
		setParamertersForSpy()
		DCPrefixIdGenerator dcGen =Mock()
		String configType ="config"
		impl.setConfigCacheContainer(null)
		impl.setStagingServer(false)
		impl.setOverrideEnabledFromComponent(false)
		impl.setStagingSuffix("")
		impl.setIdgen(dcGen)
		1*dcGen.getDcPrefix() >> ""

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		//ends getCacheKeyName

		0*configCacheContainer.get(_, "coherence")

		//for executeConfigNamedRQLQuery
		RepositoryItemImpl rItem =Mock()
		1*mRep.getView(_) >>view
		1*view.getNamedQuery(_) >> null
		//ends executeConfigNamedRQLQuery

		0*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY)
		0*impl.getSiteAndChannelId()
		0*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY) >>"configKey"
		0*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY) >> "configValue"
		0*impl.getValueFromComponent(configType,"configKey", _, "1", "")
		0*impl.getValueFromComponent(configType,"configKey", _, "1", null)
		0*impl.getValueFromComponent(configType,"configKey", _, null, null)
		0*configCacheContainer.put(_, _, "coherence", 10)

		when:
		Map<String, String> map =impl.getConfigValueByconfigType(configType)

		then:
		map.isEmpty() == true
	}

	def"getConfigValueByconfigType, when RepositoryException is thrown"(){

		given:
		setParamertersForSpy()
		DCPrefixIdGenerator dcGen =Mock()
		String configType ="config"
		impl.setConfigCacheContainer(null)
		impl.setStagingServer(false)
		impl.setOverrideEnabledFromComponent(false)
		impl.setStagingSuffix("")
		impl.setIdgen(dcGen)

		//for getCacheKeyName
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "siteId"
		1*requestMock.getHeader(BBBCoreConstants.CHANNEL) >> "channel"
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		//ends getCacheKeyName

		0*configCacheContainer.get(_, "coherence")

		//for executeConfigNamedRQLQuery
		RepositoryItemImpl rItem =Mock()
		1*mRep.getView(_) >> {throw new RepositoryException("")}
		//ends executeConfigNamedRQLQuery

		0*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_VALUE_PROPERTY)
		0*impl.getSiteAndChannelId()
		0*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_KEY_PROPERTY) >>"configKey"
		0*rItem.getPropertyValue(BBBCatalogConstants.CONFIG_VALUE_PROPERTY) >> "configValue"
		0*impl.getValueFromComponent(configType,"configKey", _, "1", "")
		0*impl.getValueFromComponent(configType,"configKey", _, "1", null)
		0*impl.getValueFromComponent(configType,"configKey", _, null, null)
		0*configCacheContainer.put(_, _, "coherence", 10)

		when:
		Map<String, String> map =impl.getConfigValueByconfigType(configType)

		then:
		map == null
		1*impl.logError("catalog_1010: Exception occured in getConfigValueByconfigType() method:: "+configType);
		BBBSystemException e = thrown()
	}

	def"executeConfigNamedRQLQuery, when view is null"(){

		given:
		String pQueryName ="query"
		Object[] pParams =["config"]
		String pViewName = "view"
		1*mRep.getView(pViewName) >> null

		when:
		RepositoryItem[] item =impl.executeConfigNamedRQLQuery(pQueryName,pParams,pViewName)

		then:
		item == null
	}

	def"executeConfigNamedRQLQuery, when configureRepository is empty"(){

		given:
		String pQueryName ="query"
		Object[] pParams =["config"]
		String pViewName = "123"
		impl.setConfigureRepository(null)
		0*mRep.getView(pViewName)

		when:
		RepositoryItem[] item =impl.executeConfigNamedRQLQuery(pQueryName,pParams,pViewName)

		then:
		item == null
	}

	def"executeConfigNamedRQLQuery, when pViewName is empty"(){

		given:
		String pQueryName ="query"
		Object[] pParams =["config"]
		String pViewName = ""
		0*mRep.getView(pViewName)

		when:
		RepositoryItem[] item =impl.executeConfigNamedRQLQuery(pQueryName,pParams,pViewName)

		then:
		item == null
	}

	def"executeConfigNamedRQLQuery, when pQueryName is empty"(){

		given:
		String pQueryName =""
		Object[] pParams =["config"]
		String pViewName = "view"
		0*mRep.getView(pViewName)

		when:
		RepositoryItem[] item =impl.executeConfigNamedRQLQuery(pQueryName,pParams,pViewName)

		then:
		item == null
	}

	def"getValueForConfigKey, when configValuesList contains integer value"(){

		given:
		impl =Spy()
		String configType ="configType"
		String key ="key"
		int defaultValue =10
		1*impl.getAllValuesForKey(configType, key) >> ["40"]

		when:
		int value =impl.getValueForConfigKey(configType,key,defaultValue)

		then:
		value == 40
	}

	def"getValueForConfigKey, when configValuesList does not contains integer value"(){

		given:
		impl =Spy()
		String configType ="configType"
		String key ="key"
		int defaultValue =10
		1*impl.getAllValuesForKey(configType, key) >> ["str"]

		when:
		int value =impl.getValueForConfigKey(configType,key,defaultValue)

		then:
		value == 10
	}

	def"getValueForConfigKey, when configValuesList is empty"(){

		given:
		impl =Spy()
		String configType ="configType"
		String key ="key"
		int defaultValue =10
		1*impl.getAllValuesForKey(configType, key) >> []

		when:
		int value =impl.getValueForConfigKey(configType,key,defaultValue)

		then:
		value == 10
	}

	def"getValueForConfigKey, when BBBSystemException is thrown"(){

		given:
		impl =Spy()
		String configType ="configType"
		String key ="key"
		int defaultValue =10
		1*impl.getAllValuesForKey(configType, key) >> {throw new BBBSystemException("msg")}

		when:
		int value =impl.getValueForConfigKey(configType,key,defaultValue)

		then:
		1*impl.logError("Exception while fetching value for config type "+ configType +" config key "+ key +" Exception "+ "msg")
		value == 10
	}

	def"getConfigKeyValue(overloaded), when configValuesList contains integer value"(){

		given:
		impl =Spy()
		String configType ="configType"
		String key ="key"
		String defaultValue ="10"
		1*impl.getAllValuesForKey(configType, key) >> ["40"]

		when:
		String value =impl.getConfigKeyValue(configType,key,defaultValue)

		then:
		value == "40"
	}

	def"getConfigKeyValue(overloaded), when configValuesList  contains empty value"(){

		given:
		impl =Spy()
		String configType ="configType"
		String key ="key"
		String defaultValue ="10"
		1*impl.getAllValuesForKey(configType, key) >> [""]

		when:
		String value =impl.getConfigKeyValue(configType,key,defaultValue)

		then:
		value == "10"
	}

	def"getConfigKeyValue(overloaded), when configValuesList is empty"(){

		given:
		impl =Spy()
		String configType ="configType"
		String key ="key"
		String defaultValue ="10"
		1*impl.getAllValuesForKey(configType, key) >> []

		when:
		String value =impl.getConfigKeyValue(configType,key,defaultValue)

		then:
		value == "10"
	}

	def"getConfigKeyValue(overloaded), when BBBSystemException is thrown"(){

		given:
		impl =Spy()
		String configType ="configType"
		String key ="key"
		String defaultValue ="10"
		1*impl.getAllValuesForKey(configType, key) >> {throw new BBBSystemException("Mock of system")}

		when:
		String value =impl.getConfigKeyValue(configType,key,defaultValue)

		then:
		1*impl.logError("Exception while fetching value for config type "+ configType +" config key "+ key +" Exception "+ "Mock of system")
		value == "10"
	}

	def"getConfigKeyValue(overloaded), when Exception is thrown"(){

		given:
		impl =Spy()
		String configType ="configType"
		String key ="key"
		String defaultValue ="10"
		1*impl.getAllValuesForKey(configType, key) >> {throw new Exception("exc")}

		when:
		String value =impl.getConfigKeyValue(configType,key,defaultValue)

		then:
		1*impl.logError("Exception while fetching value for config type "+ configType +" config key "+ key +" Exception "+ "exc")
		value == "10"
	}

	def"getSiteAndChannelId, when SiteID at index 0 and channelId at index 1 is returned"(){

		given:
		impl =Spy()
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> ""
		1*impl.extractSiteID() >> "tbs"
		2*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> "true"
		1*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "canada"
		requestMock.getParameter(BBBCoreConstants.CHANNEL) >> "" >> "channel1"

		when:
		String[] arr =impl.getSiteAndChannelId()

		then:
		arr[0] == "canada"
		arr[1] == "channel1"
	}

	def"getSiteAndChannelId, when servletRequest is null"(){

		given:
		impl =Spy()
		ServletUtil.setCurrentRequest(null)
		0*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> ""
		1*impl.extractSiteID() >> "tbs"
		0*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> "true"
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID) >> "canada"
		0*requestMock.getParameter(BBBCoreConstants.CHANNEL) >> "" >> "channel1"

		when:
		String[] arr =impl.getSiteAndChannelId()

		then:
		arr[0] == "tbs"
		arr[1] == null
	}

	def"getSiteAndChannelId, when IS_FROM_SCHEDULER is null"(){

		given:
		impl =Spy()
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "tbs"
		0*impl.extractSiteID() >> "tbs"
		1*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> null
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		0*requestMock.getParameter(BBBCoreConstants.CHANNEL)

		when:
		String[] arr =impl.getSiteAndChannelId()

		then:
		arr[0] == "tbs"
		arr[1] == "DesktopWeb"
	}

	def"getSiteAndChannelId, when IS_FROM_SCHEDULER is false"(){

		given:
		impl =Spy()
		1*requestMock.getAttribute(BBBCoreConstants.SITE_ID) >> "tbs"
		0*impl.extractSiteID() >> "tbs"
		2*requestMock.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) >> "false"
		0*requestMock.getParameter(BBBCoreConstants.SITE_ID)
		0*requestMock.getParameter(BBBCoreConstants.CHANNEL)

		when:
		String[] arr =impl.getSiteAndChannelId()

		then:
		arr[0] == "tbs"
		arr[1] == "DesktopWeb"
	}

	def"executeRQLQuery, when query result is returned "(){

		given:
		impl =Spy()
		String rqlQuery = "age > 30"
		String viewName = "view"
		RepositoryView repView =Mock()
		RepositoryItem item =Mock()
		1* mRep.getView(viewName) >> repView
		1*impl.extractDbCallForOverloadedMethod(_, _, repView) >> [item]

		when:
		RepositoryItem[]  result =impl.executeRQLQuery(rqlQuery, viewName, mRep)

		then:
		result == [item]
	}

	def"executeRQLQuery, when query result is null and view is null "(){

		given:
		impl =Spy()
		String rqlQuery = "age > 30"
		String viewName = "view"
		RepositoryView repView =Mock()
		impl.setLoggingError(true)
		1* mRep.getView(viewName) >> null
		1*impl.extractDbCallForOverloadedMethod(_, _, null) >> null

		when:
		RepositoryItem[]  result =impl.executeRQLQuery(rqlQuery, viewName, mRep)

		then:
		1*impl.logError("catalog_1019 : View " + viewName + " is null")
		1*impl.logDebug("No results returned for query [" + rqlQuery + "]")
		result == null
	}

	def"executeRQLQuery, when query result is null,RepositoryException is thrown "(){

		given:
		impl =Spy()
		String rqlQuery = "age > 30"
		String viewName = "view"
		RepositoryView repView =Mock()
		impl.setLoggingError(true)
		1* mRep.getView(viewName) >> null
		1*impl.extractDbCallForOverloadedMethod(_, _, null) >>  {throw new RepositoryException("")}

		when:
		RepositoryItem[]  result =impl.executeRQLQuery(rqlQuery, viewName, mRep)

		then:
		1*impl.logError("catalog_1020 : Unable to retrieve data");
		result == null
		BBBSystemException e = thrown()
	}

	def"executeRQLQuery, when repository is null"(){

		given:
		impl =Spy()
		String rqlQuery = "age > 30"
		String viewName = "view"
		impl.setLoggingError(true)
		0* mRep.getView(_)
		0*impl.extractDbCallForOverloadedMethod(_, _, null)

		when:
		RepositoryItem[]  result =impl.executeRQLQuery(rqlQuery, viewName, null)

		then:
		result == null
		1*impl.logError("catalog_1021 : Repository has no data")
	}

	def"executeRQLQuery, when rqlQuery is null"(){

		given:
		impl =Spy()
		String viewName = "view"
		impl.setLoggingError(true)
		0* mRep.getView(_)
		0*impl.extractDbCallForOverloadedMethod(_, _, null)

		when:
		RepositoryItem[]  result =impl.executeRQLQuery(null, viewName, null)

		then:
		result == null
		1*impl.logError("catalog_1022 : Query String is null")
	}

	def"executeRQLQuery(overloaded), when rqlQuery is null"(){

		given:
		impl =Spy()
		String rqlQuery = null
		String viewName = "view"
		impl.setLoggingError(false)
		0* mRep.getView(_)
		0*impl.extractDbCallForOverloadedMethod(_, _, null)

		when:
		RepositoryItem[]  result =impl.executeRQLQuery(rqlQuery,viewName)

		then:
		result == null
	}

	def"getWeekEndDays, when repositoryException is thrown"(){

		given:
		impl =Spy()
		String siteId ="tbs"
		impl.setSiteRepository(mRep)
		1*mRep.getItem(siteId,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("")}

		when:
		Set<Integer> set = impl.getWeekEndDays(siteId)

		then:
		set == null
		1*impl.logError("RepositoryException ")
		BBBSystemException e = thrown()
	}

	def"getWeekEndDays,return the Week Ends as integer"(){

		given:
		String siteId ="tbs"
		impl.setSiteRepository(mRep)
		RepositoryItem siteConfiguration =Mock()
		RepositoryItem item1 =Mock()
		RepositoryItem item2 =Mock()
		RepositoryItem item3 =Mock()
		RepositoryItem item4 =Mock()
		RepositoryItem item5 =Mock()
		RepositoryItem item6 =Mock()
		RepositoryItem item7 =Mock()
		RepositoryItem item8 =Mock()
		1*mRep.getItem(siteId,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration

		Set<RepositoryItem> bbbWeekendsRepositoryItem = new HashSet()
		bbbWeekendsRepositoryItem.add(item1)
		bbbWeekendsRepositoryItem.add(item2)
		bbbWeekendsRepositoryItem.add(item3)
		bbbWeekendsRepositoryItem.add(item4)
		bbbWeekendsRepositoryItem.add(item5)
		bbbWeekendsRepositoryItem.add(item6)
		bbbWeekendsRepositoryItem.add(item7)
		bbbWeekendsRepositoryItem.add(item8)
		1*siteConfiguration.getPropertyValue(BBBCatalogConstants.BBB_WEEKENDS_SITE_PROPERTY_NAME) >>bbbWeekendsRepositoryItem
		1*item1.getPropertyValue("weekendDays") >> "SUNDAY"
		1*item2.getPropertyValue("weekendDays") >> "MONDAY"
		1*item3.getPropertyValue("weekendDays") >> "TUESDAY"
		1*item4.getPropertyValue("weekendDays") >> "WEDNESDAY"
		1*item5.getPropertyValue("weekendDays") >> "THURSDAY"
		1*item6.getPropertyValue("weekendDays") >> "FRIDAY"
		1*item7.getPropertyValue("weekendDays") >> "SATURDAY"
		1*item8.getPropertyValue("weekendDays") >> "SAT"

		when:
		Set<Integer> set = impl.getWeekEndDays(siteId)

		then:
		set.contains(1) == true
		set.contains(2) == true
		set.contains(3) == true
		set.contains(4) == true
		set.contains(5) == true
		set.contains(6) == true
		set.contains(7) == true
	}

	def"getStateName, when statesRepositoryItem is not null"(){

		given:
		String stateCode= "state"
		impl.setShippingRepository(mRep)
		RepositoryItem statesRepositoryItem =Mock()
		1*mRep.getItem(stateCode.trim(),BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> statesRepositoryItem
		2*statesRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> "state"

		when:
		String name = impl.getStateName(stateCode)

		then:
		name == "state"
	}

	def"getStateName, when statesRepositoryItem is null"(){

		given:
		String stateCode= "state"
		impl.setShippingRepository(mRep)
		RepositoryItem statesRepositoryItem =Mock()
		1*mRep.getItem(stateCode.trim(),BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> null
		0*statesRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME)

		when:
		String name = impl.getStateName(stateCode)

		then:
		name == ""
	}

	def"getStateName, when statesRepositoryItem is not null, DESCRIPTION_STATE_PROPERTY_NAME is null"(){

		given:
		String stateCode= "state"
		impl.setShippingRepository(mRep)
		RepositoryItem statesRepositoryItem =Mock()
		1*mRep.getItem(stateCode.trim(),BBBCatalogConstants.STATES_ITEM_DESCRIPTOR) >> statesRepositoryItem
		1*statesRepositoryItem.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> null

		when:
		String name = impl.getStateName(stateCode)

		then:
		name == ""
	}

	def"getHolidayList, RepositoryException is thrown"(){

		given:
		impl =Spy()
		String siteId ="tbs"
		impl.setSiteRepository(mRep)
		1*mRep.getItem(siteId,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("")}

		when:
		Set<Date> set = impl.getHolidayList(siteId)

		then:
		set == null
		1*impl.logError("RepositoryException ")
		BBBSystemException e = thrown()
	}

	def"getHolidayList,returns the holidayList"(){

		given:
		String siteId ="tbs"
		impl.setSiteRepository(mRep)
		RepositoryItem siteConfiguration =Mock()
		RepositoryItem item1 =Mock()
		/*RepositoryItem item2 =Mock()*/
		1*mRep.getItem(siteId,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration

		Set<RepositoryItem> bbbHolidayRepositoryItem = new HashSet()
		bbbHolidayRepositoryItem.add(item1)
		/*bbbHolidayRepositoryItem.add(item2)*/
		1*siteConfiguration.getPropertyValue(BBBCatalogConstants.BBB_HOLIDAYS_SITE_PROPERTY_NAME) >>bbbHolidayRepositoryItem
		1*item1.getPropertyValue("holidayDate") >> new Date()
		/*1*item2.getPropertyValue("holidayDate") >> new Date()*/ // for parsing exception

		when:
		Set<Integer> set = impl.getHolidayList(siteId)

		then:
		set.isEmpty() == false
	}

	def"isEcoFeeEligibleForState, if pStateId is empty"(){

		given:
		impl =Spy()
		String pStateId =""

		when:
		boolean flag = impl.isEcoFeeEligibleForState(pStateId)

		then:
		flag == false
		1*impl.logDebug("input parameter state id is null")
		BBBBusinessException e = thrown()
		e.getMessage().equals("2006:2006")
	}

	def"isEcoFeeEligibleForState, if items are not null"(){

		given:
		impl =Spy()
		String pStateId ="pState"
		RepositoryView view = Mock()
		RepositoryItem item =Mock()
		QueryBuilder queryBuilder = Mock()
		QueryExpression queryExpStateId= Mock()
		QueryExpression queryStateId = Mock()
		Query queryEcoFeeState =Mock()

		impl.setCatalogRepository(mRep)
		1*mRep.getView(BBBCatalogConstants.ECO_FEE_ITEM_DESCRIPTOR) >> view
		1*view.getQueryBuilder() >>queryBuilder
		1*queryBuilder.createPropertyQueryExpression(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME) >> queryExpStateId
		1*queryBuilder.createConstantQueryExpression(pStateId) >> queryStateId
		1*queryBuilder.createComparisonQuery(queryStateId, queryExpStateId, QueryBuilder.EQUALS) >> queryEcoFeeState
		1*impl.extractDBCallForIsEcoFeeEligibleForState(view, queryEcoFeeState) >> [item]

		when:
		boolean flag = impl.isEcoFeeEligibleForState(pStateId)

		then:
		flag == true
	}

	def"isEcoFeeEligibleForState, if items are null"(){

		given:
		impl =Spy()
		String pStateId ="pState"
		RepositoryView view = Mock()
		RepositoryItem item =Mock()
		QueryBuilder queryBuilder = Mock()
		QueryExpression queryExpStateId= Mock()
		QueryExpression queryStateId = Mock()
		Query queryEcoFeeState =Mock()

		impl.setCatalogRepository(mRep)
		1*mRep.getView(BBBCatalogConstants.ECO_FEE_ITEM_DESCRIPTOR) >> view
		1*view.getQueryBuilder() >>queryBuilder
		1*queryBuilder.createPropertyQueryExpression(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME) >> queryExpStateId
		1*queryBuilder.createConstantQueryExpression(pStateId) >> queryStateId
		1*queryBuilder.createComparisonQuery(queryStateId, queryExpStateId, QueryBuilder.EQUALS) >> queryEcoFeeState
		1*impl.extractDBCallForIsEcoFeeEligibleForState(view, queryEcoFeeState) >> null

		when:
		boolean flag = impl.isEcoFeeEligibleForState(pStateId)

		then:
		flag == false
	}

	def"isEcoFeeEligibleForState, if RepositoryException is thrown"(){

		given:
		impl =Spy()
		String pStateId ="pState"
		RepositoryView view = Mock()
		QueryBuilder queryBuilder = Mock()
		QueryExpression queryExpStateId= Mock()
		QueryExpression queryStateId = Mock()
		Query queryEcoFeeState =Mock()

		impl.setCatalogRepository(mRep)
		1*mRep.getView(BBBCatalogConstants.ECO_FEE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("")}
		0*view.getQueryBuilder() >>queryBuilder
		0*queryBuilder.createPropertyQueryExpression(BBBCatalogConstants.STATE_ECO_FEE_PROPERTY_NAME)
		0*queryBuilder.createConstantQueryExpression(pStateId)
		0*queryBuilder.createComparisonQuery(queryStateId, queryExpStateId, QueryBuilder.EQUALS)
		0*impl.extractDBCallForIsEcoFeeEligibleForState(view, queryEcoFeeState) >> null

		when:
		boolean flag = impl.isEcoFeeEligibleForState(pStateId)

		then:
		flag == false
		1*impl.logError("Catalog API Method Name [isEcoFeeEligibleForState]: RepositoryException ")
		BBBSystemException e = thrown()
		e.getMessage().equals("2003:2003")
	}

	def"getSiteBasedFormattedDate, when includeYearFlag is true"(){

		given:
		String siteId ="tbs"
		Calendar date = Calendar.getInstance()
		boolean includeYearFlag = true

		when:
		String value =impl.getSiteBasedFormattedDate( siteId, date,  includeYearFlag)

		then:
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		value.equals(dateFormat.format(date.getTime()))
	}

	def"getSiteBasedFormattedDate, when includeYearFlag is false and siteID equals SITE_BAB_CA"(){

		given:
		String siteId =BBBCoreConstants.SITE_BAB_CA
		Calendar date = Calendar.getInstance()
		boolean includeYearFlag = false

		when:
		String value =impl.getSiteBasedFormattedDate( siteId, date,  includeYearFlag)

		then:
		value.equals(String.format("%02d", date.get(Calendar.DAY_OF_MONTH)) + "/" + String.format("%02d",date.get(Calendar.MONTH) + 1))
	}

	def"getSiteBasedFormattedDate, when includeYearFlag is false and siteID is not equal to SITE_BAB_CA"(){

		given:
		String siteId ="tbs"
		Calendar date = Calendar.getInstance()
		boolean includeYearFlag = false

		when:
		String value =impl.getSiteBasedFormattedDate( siteId, date,  includeYearFlag)

		then:
		value.equals(String.format("%02d", date.get(Calendar.MONTH)+1) + "/" + String.format("%02d",date.get(Calendar.DAY_OF_MONTH)))
	}

	def"isHoliday, Checks if is not  holiday "(){

		given:
		Set<Date> holidays = new HashSet()
		Calendar date = Calendar.getInstance()
		holidays.add(new Date(String.format("%02d", date.get(Calendar.MONTH)+1) + "/" + String.format("%02d",date.get(Calendar.DAY_OF_MONTH)+1)+"/"+String.format("%02d",date.get(Calendar.YEAR))))
		holidays.add(new Date(String.format("%02d", date.get(Calendar.MONTH)+2) + "/" + String.format("%02d",date.get(Calendar.DAY_OF_MONTH))+"/"+String.format("%02d",date.get(Calendar.YEAR))))
		holidays.add(new Date(String.format("%02d", date.get(Calendar.MONTH)+1) + "/" + String.format("%02d",date.get(Calendar.DAY_OF_MONTH))+"/"+String.format("%02d",date.get(Calendar.YEAR)+1)))
		when:
		boolean flag =impl.isHoliday(holidays,date)

		then:
		flag == false
	}
	def"isHoliday, Checks if is  holiday "(){

		given:
		Set<Date> holidays = new HashSet()
		Calendar date = Calendar.getInstance()
		holidays.add(new Date())
		when:
		boolean flag =impl.isHoliday(holidays,date)

		then:
		flag == true
	}

	def"isHoliday, Checks if holidays list is null "(){

		given:
		Calendar date = Calendar.getInstance()

		when:
		boolean flag =impl.isHoliday(null,date)

		then:
		flag == false
	}

	def"isHoliday, Checks if  holidayList size is empty "(){

		given:
		Set<Date> holidays = new HashSet()
		Calendar date = Calendar.getInstance()

		when:
		boolean flag =impl.isHoliday(holidays,date)

		then:
		flag == false
	}

	def"enableRepoCallforDynPrice, Enable repo callfor dyn price."(){

		given:
		impl =Spy()
		impl.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_REPO_CALL_DYN_PRICE) >> ["false"]

		when:
		boolean flag =impl.enableRepoCallforDynPrice()

		then:
		flag == false
	}

	def"enableRepoCallforDynPrice, when list is empty"(){

		given:
		impl =Spy()
		impl.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_REPO_CALL_DYN_PRICE) >> []

		when:
		boolean flag =impl.enableRepoCallforDynPrice()

		then:
		flag == true
	}

	def"enableRepoCallforDynPrice, when exception is thrown"(){

		given:
		impl =Spy()
		impl.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.ENABLE_REPO_CALL_DYN_PRICE) >> {throw new BBBSystemException("BBBSystemException")}

		when:
		boolean flag =impl.enableRepoCallforDynPrice()

		then:
		1*impl.logError("BBBSystemException")
		flag == true
	}

	def"updateListWithSddShipMethod, when sddShippingMethodItem is not null"(){

		given:
		List<ShipMethodVO> shipMethodVOList = new ArrayList()
		RepositoryItem sddShippingMethodItem =Mock()
		impl.setShippingRepository(mRep)
		impl.setSddShipMethodId("sdd")

		1*mRep.getItem("sdd", BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR) >> sddShippingMethodItem
		sddShippingMethodItem.getRepositoryId() >>"rep"

		when:
		impl.updateListWithSddShipMethod(shipMethodVOList)

		then:
		1*sddShippingMethodItem.getRepositoryId()
		shipMethodVOList.size() ==1
		shipMethodVOList.get(0).isEligibleShipMethod()
		shipMethodVOList.get(0).getShipMethodId().equals("rep")
	}

	def"updateListWithSddShipMethod,  when sddShippingMethodItem is null"(){

		given:
		List<ShipMethodVO> shipMethodVOList = new ArrayList()
		RepositoryItem sddShippingMethodItem =Mock()
		impl.setShippingRepository(mRep)
		impl.setSddShipMethodId("sdd")

		1*mRep.getItem("sdd", BBBCatalogConstants.SHIPPING_METHOD_ITEM_DESCRIPTOR) >> null

		when:
		impl.updateListWithSddShipMethod(shipMethodVOList)

		then:
		0*sddShippingMethodItem.getRepositoryId()
		shipMethodVOList.size() ==0
	}


}
