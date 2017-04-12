package com.bbb.tools;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set

import org.junit.Ignore;

import spock.lang.Specification
import spock.lang.specification.BBBExtendedSpec
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository
import atg.repository.Repository
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem
import atg.servlet.DynamoHttpServletRequest
import atg.servlet.ServletUtil

import com.bbb.commerce.catalog.BBBCatalogConstants
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.BrandVO
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.SiteChatAttributesVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.logger.PerformanceLogger
import com.bbb.framework.webservices.RequestMarshaller;
import com.bbb.repository.RepositoryItemMock
import com.bbb.search.bean.result.SortOptionVO;
import com.bbb.search.bean.result.SortOptionsVO
import com.bbb.store.catalog.vo.SortOptionsVo;
import com.bbb.utils.BBBUtility;;
/**
 * 
 * @author Velmurugan Moorthy
 * 
 * This class is created for unit testing using Spock
 * The class tested using this Specification class is BBBSiteRepositoryTools
 *
 */

public class BBBSiteRepositoryToolsSpecification extends BBBExtendedSpec {

	public static final String SITE_ID_BEDBATH_US = "BedBathUS"

	public static final String STATE_ID_NEW_YORK = "NY"

	public static final String PERFORMANCE_LOGGER_PATH = "/com/bbb/framework/performance/logger/PerformanceLogger"

	private static final String REGISTRY_TYPE_CODE = "registryTypeCode"

	def BBBSiteRepositoryTools bbbSiteRepoToolsTestObj
	def DynamoHttpServletRequest requestMock = Mock()
	def MutableRepository MutableRepositoryMock = Mock()
	def MutableRepository siteRepositoryMock = Mock()
	def MutableRepository shippingRepositoryMock = Mock()
	def MutableRepository catalogRepositoryMock = Mock()
	def Repository managedCatalogRepositoryMock = Mock()
	def RepositoryItem siteConfigurationMock = Mock()
	def RepositoryItem nexusStateMock = Mock()
	def RepositoryItem registryTypeItemMock = Mock()
	def RepositoryItem paymentRepoItemMock = Mock()
	def RepositoryItem stateRepoItemMock = Mock()
	def RepositoryItem shippingStateRepoItemMock = Mock()

	def setup() {
		bbbSiteRepoToolsTestObj = new BBBSiteRepositoryTools(siteRepository:siteRepositoryMock,shippingRepository:shippingRepositoryMock,bopusEligibleStateQuery:"bopus=1",brandIdQuery:"brandId = ?0",
		brandNameQuery:"brandName = ?0",sddShipMethodId:"SDD",catalogRepository:catalogRepositoryMock,bbbManagedCatalogRepository:managedCatalogRepositoryMock)
		//ServletUtil.setCurrentRequest(requestMock)
		requestMock.getContextPath() >> "/store"
		//requestMock.resolveName(PERFORMANCE_LOGGER_PATH) >> perfMock
		//perfMock.isEnableCustomComponentsMonitoring() >> false
	}


	/* isPackNHoldWindow - test cases START */
	
	def "isPackNHoldWindow - happy flow" () {

		given:

		def Date currentDate = new Date()

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_SITE_PROPERTY_NAME) >>  true
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_START_DATE_SITE_PROPERTY_NAME) >> currentDate - 1
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) >> currentDate + 1

		when :

		def packNHoldWindow = bbbSiteRepoToolsTestObj.isPackNHoldWindow(SITE_ID_BEDBATH_US, currentDate)

		then :
		packNHoldWindow == true
	}

	def "isPackNHoldWindow - packAndHold site level property is not set (null)" () {
		
				given:
		
				def Date currentDate = new Date()
		
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_SITE_PROPERTY_NAME) >>  null
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_START_DATE_SITE_PROPERTY_NAME) >> currentDate - 1
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) >> currentDate + 1
		
		
				when :
		
				def packNHoldWindow = bbbSiteRepoToolsTestObj.isPackNHoldWindow(SITE_ID_BEDBATH_US, currentDate)
		
				then :
				packNHoldWindow == false
			}
		
	
	def "isPackNHoldWindow - input date after packNHoldStart (start date) - failing branch" () {

		given:

		def Date currentDate = new Date()

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_SITE_PROPERTY_NAME) >>  true
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_START_DATE_SITE_PROPERTY_NAME) >> currentDate + 1
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) >> currentDate - 1

		when :

		def packNHoldWindow = bbbSiteRepoToolsTestObj.isPackNHoldWindow(SITE_ID_BEDBATH_US, currentDate)

		then :
		packNHoldWindow == false
	}

	def "isPackNHoldWindow - input date before packNHoldEnd (end date) - failing branch" () {

		given:

		def Date currentDate = new Date()

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_SITE_PROPERTY_NAME) >>  true
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_START_DATE_SITE_PROPERTY_NAME) >> currentDate - 1
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) >> currentDate - 1

		when :

		def packNHoldWindow = bbbSiteRepoToolsTestObj.isPackNHoldWindow(SITE_ID_BEDBATH_US, currentDate)

		then :
		packNHoldWindow == false
	}
	
	def "isPackNHoldWindow - packNHoldStart (start date) is not set (null)" () {
		
				given:
		
				def Date currentDate = new Date()
		
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_SITE_PROPERTY_NAME) >>  true
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_START_DATE_SITE_PROPERTY_NAME) >> null
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) >> currentDate - 1
		
				when :
		
				def packNHoldWindow = bbbSiteRepoToolsTestObj.isPackNHoldWindow(SITE_ID_BEDBATH_US, currentDate)
		
				then :
				packNHoldWindow == false
	 }

	def "isPackNHoldWindow - packNHoldEnd (end date) is not set (null)" () {

		given:

		def Date currentDate = new Date()

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_SITE_PROPERTY_NAME) >>  true
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_START_DATE_SITE_PROPERTY_NAME) >> currentDate - 1
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) >> null

		when :

		def packNHoldWindow = bbbSiteRepoToolsTestObj.isPackNHoldWindow(SITE_ID_BEDBATH_US, currentDate)

		then :
		packNHoldWindow == false
	}

	def "isPackNHoldWinodw - siteConfig property is not set (null)" () {

		given:

		def Date currentDate = new Date()

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> null

		when :

		def packNHoldWindow = bbbSiteRepoToolsTestObj.isPackNHoldWindow(SITE_ID_BEDBATH_US, currentDate)

		then :
		thrown BBBBusinessException
	}


	def "isPackNHoldWindow - with null input date" () {

		when:

		def packNHoldWindow = bbbSiteRepoToolsTestObj.isPackNHoldWindow(SITE_ID_BEDBATH_US, null)

		then:

		packNHoldWindow == null

		thrown BBBBusinessException
	}

	def "isPackNHoldWindow - with both siteID and input date null" () {

		given:

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_SITE_PROPERTY_NAME) >>  true

		when :

		def packNHoldWindow = bbbSiteRepoToolsTestObj.isPackNHoldWindow(null, null)

		then :
		thrown BBBBusinessException
	}


	def "isPackNHoldWindow - siteID is empty " () {

		given:

		def Date currentDate = new Date()

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_SITE_PROPERTY_NAME) >>  true
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_START_DATE_SITE_PROPERTY_NAME) >> currentDate - 1
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) >> currentDate + 1


		when :

		def packNHoldWindow = bbbSiteRepoToolsTestObj.isPackNHoldWindow("", currentDate)

		then :
		thrown BBBBusinessException
	}

	def "isPackNHoldWinodw - Exception while getting repository item" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Exception while getting site repo item")}

		when:

		def packNHoldWindow = bbbSiteRepoToolsTestObj.isPackNHoldWindow(SITE_ID_BEDBATH_US, new Date())

		then:

		packNHoldWindow == null
		thrown BBBSystemException
	}

	/* isPackNHoldWindow - test cases ENDS */


	/* getRegistryTypes - test cases START */

	def "getRegistryTypes - happy flow" () {

		given :

		Set registryTypesSet = new HashSet<RepositoryItem>()
		registryTypesSet.add(registryTypeItemMock)

		List<RegistryTypeVO> registryTypeVOList

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPES_SITE_PROPERTY_NAME) >> registryTypesSet

		when :

		def registryTypesResult = bbbSiteRepoToolsTestObj.getRegistryTypes(SITE_ID_BEDBATH_US);

		then :
		
		registryTypesResult.isEmpty() == false
		
	}

	def "getRegistryTypes - site level property - registryTypes is empty" () {

		given :

		Set registryTypesSet = new HashSet<RepositoryItem>()

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPES_SITE_PROPERTY_NAME) >> registryTypesSet

		when :

		def registryTypesResult = bbbSiteRepoToolsTestObj.getRegistryTypes(SITE_ID_BEDBATH_US);

		then :
		registryTypesResult.isEmpty() == true
	}


	def "getRegistryTypes - site level property - registryTypes is not defined" () {

		given :

		Set registryRepositoryItems = new HashSet<RepositoryItem>()
		List<RegistryTypeVO> registryTypeVOList
		Set<RepositoryItemMock> repositoryItemSetMock = new HashSet<RepositoryItemMock>()
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, STATE_ID_NEW_YORK) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPES_SITE_PROPERTY_NAME) >> registryTypeVOList
		
		when :

		def registryTypesResult = bbbSiteRepoToolsTestObj.getRegistryTypes(SITE_ID_BEDBATH_US);

		then :
		registryRepositoryItems.isEmpty() == true
		
	}

	def "getRegistryTypes - siteConfiguration is not set(null)" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, null) >> siteConfigurationMock
		
		when:
		def results = bbbSiteRepoToolsTestObj.getRegistryTypes(SITE_ID_BEDBATH_US);

		then:
		results == null
		thrown BBBBusinessException

	}
	def "getRegistryTypes - RepositoryException while getting site repo item" () {

		given :

		Set registryTypesSet = new HashSet<RepositoryItem>()
		registryTypesSet.add(registryTypeItemMock)

		List<RegistryTypeVO> registryTypeVOList

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Exception while getting site repo item")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPES_SITE_PROPERTY_NAME) >> registryTypesSet

		when :

		def registryTypesResult = bbbSiteRepoToolsTestObj.getRegistryTypes(SITE_ID_BEDBATH_US);

		then :
		registryTypesResult == null
		thrown BBBSystemException
	}

	/* getRegistryTypes - test cases ENDS */


	/* getDefaultShippingMethod - test cases - STARTS  
	 */

	def "getDefaultShippingMethod - happy flow" () {
		
				given :
				
				def shipMethodId = "shipMethod01"
				 
				RepositoryItemMock shippingMethodRepositoryItemMock = new RepositoryItemMock(["id":shipMethodId])
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFAULT_SHIP_METHOD_PROPERTY_NAME) >> shippingMethodRepositoryItemMock
		
				when:
		
				def defaultShippingMethod = bbbSiteRepoToolsTestObj.getDefaultShippingMethod(SITE_ID_BEDBATH_US)
		
				then:

				defaultShippingMethod.getShipMethodId() == shipMethodId
		
	}		
	
	def "getDefaultShippingMethod - Site Configuration is not set (null)" () {

		given :
		
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, null) >> siteConfigurationMock
		
		when:

		def defaultShippingMethod = bbbSiteRepoToolsTestObj.getDefaultShippingMethod(SITE_ID_BEDBATH_US)

		then:
		
		defaultShippingMethod == null
		thrown BBBBusinessException
	}

	def "getDefaultShippingMethod shippingMethodRepository is not set (null)" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFAULT_SHIP_METHOD_PROPERTY_NAME) >> null

		when:

		def defaultShippingMethod = bbbSiteRepoToolsTestObj.getDefaultShippingMethod(SITE_ID_BEDBATH_US)

		then:
		defaultShippingMethod == null
		thrown BBBBusinessException
	}

	def "getDefaultShippingMethod - Exception while getting site repo item" () {

		given :
		RepositoryItem shippingMethodRepositoryItemMock = Mock()
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Exception while getting repo item")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFAULT_SHIP_METHOD_PROPERTY_NAME) >> shippingMethodRepositoryItemMock

		when:

		def defaultShippingMethod = bbbSiteRepoToolsTestObj.getDefaultShippingMethod(SITE_ID_BEDBATH_US)

		then:
		defaultShippingMethod == null
		thrown BBBSystemException

	}

	/* getDefaultShippingMethod - test cases - ENDS
	 */

	/*
	 * isNexusState - test cases - STARTS
	 */

	def "isNexusState - happy flow" () {

		given:

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		Set nexusStates = new HashSet<RepositoryItem>()
		nexusStates.add(nexusStateMock)
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.NEXUS_STATE_SITE_PROPERTY_NAME) >> nexusStates
		nexusStateMock.getRepositoryId() >> STATE_ID_NEW_YORK

		when:

		def results = bbbSiteRepoToolsTestObj.isNexusState(SITE_ID_BEDBATH_US, STATE_ID_NEW_YORK) 

		then:
		
		results == true


	}

	def "isNexusState - Nexus state ID is not set (null)" () {

		given:

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		Set nexusStates = new HashSet<RepositoryItem>()
		nexusStates.add(nexusStateMock)
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.NEXUS_STATE_SITE_PROPERTY_NAME) >> nexusStates
		nexusStateMock.getRepositoryId() >> null


		when:

		def results = bbbSiteRepoToolsTestObj.isNexusState(SITE_ID_BEDBATH_US, null) //calling the test methods

		then:
		results == null
		thrown BBBBusinessException

	}

	def "isNexusState - Nexus state ID is empty" () {

		given:

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		Set nexusStates = new HashSet<RepositoryItem>()
		nexusStates.add(nexusStateMock)
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.NEXUS_STATE_SITE_PROPERTY_NAME) >> nexusStates
		nexusStateMock.getRepositoryId() >> ""


		when:

		def results = bbbSiteRepoToolsTestObj.isNexusState(SITE_ID_BEDBATH_US, "") //calling the test methods

		then:
		results == null
		thrown BBBBusinessException

	}
	
	def "isNexusState - stateID does not match input state ID" () {

		given:

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		Set nexusStates = new HashSet<RepositoryItem>()
		nexusStates.add(nexusStateMock)
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.NEXUS_STATE_SITE_PROPERTY_NAME) >> nexusStates
		nexusStateMock.getRepositoryId() >> "AK"

		when:

		def results = bbbSiteRepoToolsTestObj.isNexusState(SITE_ID_BEDBATH_US, STATE_ID_NEW_YORK) //calling the test methods

		then:
		results == false


	}
	def "isNexusState - Site ID is not set (null)" () {

		given:

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		Set nexusStates = new HashSet<RepositoryItem>()
		nexusStates.add(nexusStateMock)
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.NEXUS_STATE_SITE_PROPERTY_NAME) >> nexusStates
		nexusStateMock.getRepositoryId() >> STATE_ID_NEW_YORK

		when:

		def results = bbbSiteRepoToolsTestObj.isNexusState(null, STATE_ID_NEW_YORK) //calling the test methods

		then:
		results == null
		thrown BBBBusinessException

	}

	def "isNexusState - Site ID is empty" () {

		given:

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		Set nexusStates = new HashSet<RepositoryItem>()
		nexusStates.add(nexusStateMock)
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.NEXUS_STATE_SITE_PROPERTY_NAME) >> nexusStates
		
		when:

		def results = bbbSiteRepoToolsTestObj.isNexusState("", STATE_ID_NEW_YORK) //calling the test methods

		then:
		results == null
		thrown BBBBusinessException

	}

	def "isNexusState - Site Configuration is not set (null)" (){

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, null) >> siteConfigurationMock
	
		when:
		def results = bbbSiteRepoToolsTestObj.isNexusState(SITE_ID_BEDBATH_US, STATE_ID_NEW_YORK)

		then:
		results == null
		thrown BBBBusinessException

	}


	/**
	 * isNexusState - RepositoryException while getting site repo item
	 * 
	 */
	def "isNexusState - Repository exception while getting site repo item " (){

		given :

		Set nexusStates = new HashSet<RepositoryItem>()
		nexusStates.add(nexusStateMock)
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Exception while getting site repo item")}

		when:
		def results = bbbSiteRepoToolsTestObj.isNexusState(SITE_ID_BEDBATH_US, "NY")

		then:
		results == null
		thrown BBBSystemException

	}

	/* isNexusState - test cases ENDS
	 */

	/* getCreditCardTypes - test cases - STARTS */


	def "getCreditCardTypes - Happy flow" () {

		given :
		
		def creditCardId = "paymentCard01"
		def creditCardCode = "AMEX"
		RepositoryItemMock paymentCardRepositoryItemMock = new RepositoryItemMock(["id":creditCardId])
		List<RepositoryItemMock> paymentCardRepositoryItemList = new ArrayList()
		
		paymentCardRepositoryItemMock.setProperties(["code":creditCardId, "cardCode":creditCardCode])
		
		paymentCardRepositoryItemList.add(paymentCardRepositoryItemMock)
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock		
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PAYMENT_CARDS_PROPERTY_NAME) >> paymentCardRepositoryItemList

		when:

		def creditCardTypes = bbbSiteRepoToolsTestObj.getCreditCardTypes(SITE_ID_BEDBATH_US)

		then:
		creditCardTypes.get(0).getCode().equals(creditCardCode)
	}

	def "getCreditCardTypes - paymentCards property is empty" () {

		given :
		RepositoryItem paymentCardRepositoryItemMock = Mock()
		List<RepositoryItem> paymentCardRepositoryItemList = new ArrayList()
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PAYMENT_CARDS_PROPERTY_NAME) >> paymentCardRepositoryItemList

		when:

		def creditCardTypes = bbbSiteRepoToolsTestObj.getCreditCardTypes(SITE_ID_BEDBATH_US)

		then:
		creditCardTypes == null
		thrown BBBBusinessException
	}
	
	def "getCreditCardTypes - PaymentRepository is null" () {
		
				given :
				RepositoryItem paymentCardRepositoryItemMock = Mock()
				List<RepositoryItem> paymentCardRepositoryItemList = new ArrayList()
				paymentCardRepositoryItemList.add(paymentRepoItemMock)
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PAYMENT_CARDS_PROPERTY_NAME) >> null
		
				when:
		
				def creditCardTypes = bbbSiteRepoToolsTestObj.getCreditCardTypes(SITE_ID_BEDBATH_US)
		
				then:
				creditCardTypes == null
				thrown BBBBusinessException
	 }
		
	
	def "getCreditCardTypes - Site Configuration is null" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, null) >> siteConfigurationMock

		when:

		def creditCardTypes = bbbSiteRepoToolsTestObj.getCreditCardTypes(SITE_ID_BEDBATH_US)

		then:
		creditCardTypes == null
		thrown BBBBusinessException
	}
	
	def "getCreditCardTypes - PaymentRepository RepositoryException" () {

		given :
		RepositoryItem paymentCardRepositoryItemMock = Mock()
		List<RepositoryItem> paymentCardRepositoryItemList = new ArrayList()
		paymentCardRepositoryItemList.add(paymentRepoItemMock)
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Exception in getting site repo item")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.PAYMENT_CARDS_PROPERTY_NAME) >> paymentCardRepositoryItemList

		when:

		def creditCardTypes = bbbSiteRepoToolsTestObj.getCreditCardTypes(SITE_ID_BEDBATH_US)

		then:
		creditCardTypes == null
		thrown BBBSystemException
	}
	/* getCreditCardTypes - test cases - ENDS */


	/* getStates - test cases - START */

	// NO_SHOW_REGISTRY - test cases - starts
	
	def "getStates - Site Configuration is not set (null)" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, null) >> siteConfigurationMock
		List<StateVO> states = new ArrayList<>()

		when:

		states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, false, BBBCatalogConstants.NO_SHOW_REGISTRY);

		then:
		states.isEmpty() == true
		thrown BBBBusinessException
	}

	def "getStates - stateRepoItems is not null but empty" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		List<StateVO> states = new ArrayList<>()
		Set<RepositoryItem> stateRepItems = new HashSet<>()
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems

		when:

		states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, false, "");

		then:
		states.isEmpty() == true
		thrown BBBBusinessException
	}

	// Sample states list - [YT,MB,ON,SK,NB,NS,NT,NU,AB,PE,QC,NL,BC] - states list
	def "getStates - noShowPage value is NO_SHOW_REGISTRY, SHOW_ON_REGISTRY_PAGES is enabled (true)" () {

		given :
		
		def stateId = "VT"
		List<StateVO> states = new ArrayList<>()
		Set<RepositoryItem> stateRepItems = new HashSet<>()
		
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		stateRepoItemMock.getRepositoryId() >> stateId
		stateRepItems.add(stateRepoItemMock)
		
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems
		stateRepoItemMock.getPropertyValue(BBBCatalogConstants.SHOW_ON_REGISTRY_PAGES) >> true		
		bbbSiteRepoToolsTestObj.isNexusState(SITE_ID_BEDBATH_US, stateId) >> true

		when:

		states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, false, BBBCatalogConstants.NO_SHOW_REGISTRY);

		then:
		
				states.get(0).getStateCode() == stateId
				states.get(0).isShowOnReg() == true				
				
	}

	def "getStates - noShowPage value is NO_SHOW_REGISTRY, SHOW_ON_REGISTRY_PAGES is disabled (false)" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		List<StateVO> states = new ArrayList<>()
		Set<RepositoryItem> stateRepItems = new HashSet<>()
		stateRepItems.add(stateRepoItemMock)
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems

		stateRepoItemMock.getPropertyValue(BBBCatalogConstants.SHOW_ON_REGISTRY_PAGES) >> false

		when:

		states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, false, BBBCatalogConstants.NO_SHOW_REGISTRY);

		then:
		states.isEmpty() == true
	}

	def "getStates - noShowPage value is NO_SHOW_REGISTRY, SHOW_ON_REGISTRY_PAGES value is not set (null)" () { 

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		List<StateVO> states = new ArrayList<>()
		Set<RepositoryItem> stateRepItems = new HashSet<>()
		stateRepItems.add(stateRepoItemMock)
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems

		stateRepoItemMock.getPropertyValue(BBBCatalogConstants.SHOW_ON_REGISTRY_PAGES) >> null

		when:

		states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, false, BBBCatalogConstants.NO_SHOW_REGISTRY);

		then:
		states.isEmpty() == true
		thrown BBBBusinessException
	}

	// NO_SHOW_REGISTRY - test cases - ends
	
	// NO_SHOW_SHIPPING - test cases - ends

	def "getStates - noShowPage value is NO_SHOW_SHIPPING, SHOW_ON_SHIPPING_PAGES false" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		List<StateVO> states = new ArrayList<>()
		Set<RepositoryItem> stateRepItems = new HashSet<>()
		stateRepItems.add(stateRepoItemMock)
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems

		stateRepoItemMock.getPropertyValue(BBBCatalogConstants.SHOW_ON_SHIPPING_PAGES) >> false

		when:

		states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, false, BBBCatalogConstants.NO_SHOW_SHIPPING);

		then:
		
		states.isEmpty() == true
	}


	def "getStates - noShowPage value is NO_SHOW_SHIPPING, SHOW_ON_SHIPPING_PAGES true" () {

		given :

		def stateId = "VT"
		List<StateVO> states = new ArrayList<>()
		Set<RepositoryItem> stateRepItems = new HashSet<>()
		
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		stateRepoItemMock.getRepositoryId() >> stateId
		stateRepItems.add(stateRepoItemMock)
		
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems
		stateRepoItemMock.getPropertyValue(BBBCatalogConstants.SHOW_ON_SHIPPING_PAGES) >> true
		bbbSiteRepoToolsTestObj.isNexusState(SITE_ID_BEDBATH_US, stateId) >> true


		when:

		states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, false, BBBCatalogConstants.NO_SHOW_SHIPPING);

		then:
				states.get(0).getStateCode() == stateId
				states.get(0).isShowOnShipping() == true
	}

	def "getStates - noShowPage value is NO_SHOW_SHIPPING, SHOW_ON_SHIPPING_PAGES null" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		List<StateVO> states = new ArrayList<>()
		Set<RepositoryItem> stateRepItems = new HashSet<>()
		stateRepItems.add(stateRepoItemMock)
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems

		stateRepoItemMock.getPropertyValue(BBBCatalogConstants.SHOW_ON_SHIPPING_PAGES) >> null

		when:

		states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, false, BBBCatalogConstants.NO_SHOW_SHIPPING);

		then:
		states.isEmpty() == true
		thrown BBBBusinessException
	}

	def "getStates - noShowPage is empty" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		List<StateVO> states = new ArrayList<>()
		Set<RepositoryItem> stateRepItems = new HashSet<>()
		stateRepItems.add(stateRepoItemMock)
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems

		stateRepoItemMock.getPropertyValue(BBBCatalogConstants.SHOW_ON_SHIPPING_PAGES) >> null

		when:

		states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, false, "");

		then:
		states.isEmpty() == true
		thrown BBBBusinessException
	}
	
	// NO_SHOW_SHIPPING - test cases - ends
	
	// NO_SHOW_BILLING - test cases - starts

	def "getStates -> getStatesVo - noShowPage value is NO_SHOW_BILLING, SHOW_ON_BILLING_PAGES flag is enabled (true)" () {
		
				given :
				
				def stateId = "VT"
				List<StateVO> states = new ArrayList<>()
				Set<RepositoryItem> stateRepItems = new HashSet<>()
				
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock				
				stateRepoItemMock.getRepositoryId() >> stateId				
				stateRepItems.add(stateRepoItemMock)
				
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems
				stateRepoItemMock.getPropertyValue(BBBCatalogConstants.SHOW_ON_BILLING_PAGES) >> true
				bbbSiteRepoToolsTestObj.isNexusState(SITE_ID_BEDBATH_US, stateId) >> true
		
				when:
		
				states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, true, BBBCatalogConstants.NO_SHOW_BILLING);
		
				then:
				states.get(0).getStateCode() == stateId
				states.get(0).isShowOnBilling() == true
		}
		
	
	def "getStates - noShowPage value is NO_SHOW_BILLING, SHOW_ON_BILLING_PAGES billing pages flags are false" () {

		given :
		
		def stateId = "VT"
		List<StateVO> states = new ArrayList<>()
		Set<RepositoryItem> stateRepItems = new HashSet<>()
		
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock		
		stateRepoItemMock.getRepositoryId() >> stateId		
		stateRepItems.add(stateRepoItemMock)
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems
		
		stateRepoItemMock.getPropertyValue(BBBCatalogConstants.SHOW_ON_BILLING_PAGES) >> false
			
		when:

		states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, true, BBBCatalogConstants.NO_SHOW_BILLING);

		then:
				states.isEmpty() == true
	}


	def "getStates - noShowPage value is NO_SHOW_BILLING, SHOW_ON_BILLING_PAGES is not set (null)" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		List<StateVO> states = new ArrayList<>()
		Set<RepositoryItem> stateRepItems = new HashSet<>()
		stateRepItems.add(stateRepoItemMock)
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems

		stateRepoItemMock.getPropertyValue(BBBCatalogConstants.SHOW_ON_BILLING_PAGES) >> null

		when:

		states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, false, BBBCatalogConstants.NO_SHOW_BILLING);

		then:
		
		states.isEmpty() == true
		thrown BBBBusinessException
	}

	def "getStates - testing a military state is added to the state list if the showMilitaryStates is true" () {
		
				given :
				
				def stateId = "VT"
				def stateDescription = "Vermont"
				def List<StateVO> states = new ArrayList<>()
				def RepositoryItemMock tempStateRepoItemMock = new RepositoryItemMock(["id" : stateId])
				
				Set<RepositoryItem> stateRepItems = new HashSet<>()
			
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				tempStateRepoItemMock.setProperties(["descrip" : stateDescription,"isMilitaryState" : true, "showOnBilling" : true ])
				
				stateRepItems.add(tempStateRepoItemMock)
				
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems
				
				bbbSiteRepoToolsTestObj.isNexusState(SITE_ID_BEDBATH_US, stateId) >> true
		
				when:
		
				states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, true, BBBCatalogConstants.NO_SHOW_BILLING);
				
				then:
				
				states.isEmpty() == false
				states.get(0).isMilitaryState == true
				states.get(0).getStateCode() == stateId
				states.get(0).getStateName() == stateDescription
				
			}

	def "getStates - testing a military state is not added to the state list if the showMilitaryStates is false" () {

		given :
		
		def stateId = "VT"
		def stateDescription = "Vermont"
		def List<StateVO> states = new ArrayList<>()
		def RepositoryItemMock tempStateRepoItemMock = new RepositoryItemMock(["id" : stateId])
		
		Set<RepositoryItem> stateRepItems = new HashSet<>()
	
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		tempStateRepoItemMock.setProperties(["descrip" : stateDescription,"isMilitaryState" : true, "showOnBilling" : true ])
		
		stateRepItems.add(tempStateRepoItemMock)
		
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems
		
		bbbSiteRepoToolsTestObj.isNexusState(SITE_ID_BEDBATH_US, stateId) >> true

		when:

		states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, false, BBBCatalogConstants.NO_SHOW_BILLING);
		
		then:

		!states.contains(tempStateRepoItemMock)
	}

	def "getStates - testing a non-military state is not added to the state list if the showMilitaryStates is false" () {
		
				given :
				
				def stateId = "VT"
				def stateDescription = "Vermont"
				def List<StateVO> states = new ArrayList<>()
				def RepositoryItemMock tempStateRepoItemMock = new RepositoryItemMock(["id" : stateId])
				def militaryState = false
				def showOnBilling = true
				
				Set<RepositoryItem> stateRepItems = new HashSet<>()
			
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				tempStateRepoItemMock.setProperties(["descrip" : stateDescription,"isMilitaryState" : militaryState, "showOnBilling" : showOnBilling ])
				
				stateRepItems.add(tempStateRepoItemMock)
				
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems
				
				bbbSiteRepoToolsTestObj.isNexusState(SITE_ID_BEDBATH_US, stateId) >> true
		
				when:
		
				states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, false, BBBCatalogConstants.NO_SHOW_BILLING);
				
				then:
		
				states.isEmpty() == false
				states.get(0).isMilitaryState == militaryState
				states.get(0).getStateCode() == stateId
				states.get(0).getStateName() == stateDescription
				
			}
	
	def "getStates - Exception while getting repo item" () {

		given :
		def stateId = "VT"
		def stateDescription = "Vermont"
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("exception while getting repo item")}
		List<StateVO> states = new ArrayList<>()
		stateRepoItemMock.getRepositoryId() >> stateId
		Set<RepositoryItem> stateRepItems = new HashSet<>()
		stateRepItems.add(stateRepoItemMock)
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME) >> stateRepItems
		stateRepoItemMock.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) >> stateDescription
		stateRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_MILITARY_STATE) >> true
		bbbSiteRepoToolsTestObj.isNexusState(SITE_ID_BEDBATH_US, stateId) >> true

		when:

		states = bbbSiteRepoToolsTestObj.getStates(SITE_ID_BEDBATH_US, false, BBBCatalogConstants.NO_SHOW_BILLING);

		then:
		states.empty == true
		thrown BBBSystemException
	}
	
   // NO_SHOW_BILLING - scenarios - ends
	
	/* getStates - test cases - ENDS */


	/* getWrapSkuDetails - test cases - STARTS */
	
	def "getWrapSkuDetails - happy flow" () {
		
				given :
				
				def giftWrapSkuId = "giftWrapSku001"
				def giftWrapProductId = "giftWrapProd001"
				def Double wrapPrice = 10.0
				def RepositoryItemMock giftWrapSkuItemMock = new RepositoryItemMock(["id":giftWrapSkuId])
				def RepositoryItemMock giftWrapProductItemMock = new RepositoryItemMock(["id":giftWrapProductId])
				
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRICE_SITE_PROPERTY_NAME) >> wrapPrice
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_SKU_SITE_PROPERTY_NAME) >> giftWrapSkuItemMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRODUCT_SITE_PROPERTY_NAME) >> giftWrapProductItemMock
				
				when:
		
				def giftWrapVO = bbbSiteRepoToolsTestObj.getWrapSkuDetails(SITE_ID_BEDBATH_US)
		
				then:
				
				giftWrapVO.getWrapSkuPrice() == wrapPrice
				giftWrapVO.getWrapSkuId() == giftWrapSkuId
				giftWrapVO.getWrapProductId() == giftWrapProductId
	 }
		
	def "getWrapSkuDetails - Site Configuration is not set (null)" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, null) >> siteConfigurationMock

		when:

		def giftWrapVO = bbbSiteRepoToolsTestObj.getWrapSkuDetails(SITE_ID_BEDBATH_US)

		then:
		giftWrapVO == null
		thrown BBBBusinessException
	}
	def "getWrapSkuDetails - giftWrapSku is not set (null)" () {

		given :
		

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_SKU_SITE_PROPERTY_NAME) >> null

		when:

		def giftWrapVO = bbbSiteRepoToolsTestObj.getWrapSkuDetails(SITE_ID_BEDBATH_US)

		then:
		
		giftWrapVO.getWrapSkuId().isEmpty() == true
	}


	def "getWrapSkuDetails - giftWrapProduct is not set (null)" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRODUCT_SITE_PROPERTY_NAME) >> null

		when:

		def giftWrapVO = bbbSiteRepoToolsTestObj.getWrapSkuDetails(SITE_ID_BEDBATH_US)

		then:
		
		giftWrapVO.getWrapProductId().isEmpty() == true
	}
	
	def "getWrapSkuDetails - wrapPrice is not set (null)"() {
		
				given :
				
				def Double wrapPrice = null
				
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRICE_SITE_PROPERTY_NAME) >> wrapPrice
				
				when:
		
				def giftWrapVO = bbbSiteRepoToolsTestObj.getWrapSkuDetails(SITE_ID_BEDBATH_US)
		
				then:
				
				giftWrapVO.getWrapSkuPrice() == 0.0
				
	 }
	
	def "getWrapSkuDetails - Exception while getting site repo item" () {

		given :

		RepositoryItem giftWrapProductItem = Mock()
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Exception while getting site repo item")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRODUCT_SITE_PROPERTY_NAME) >> giftWrapProductItem

		when:

		def giftWrapVO = bbbSiteRepoToolsTestObj.getWrapSkuDetails(SITE_ID_BEDBATH_US)

		then:
		giftWrapVO == null
		thrown BBBSystemException
	}

	/* getWrapSkuDetails - test cases - ENDS */

	/* getCommonGreetings - test cases - STARTS */

	def "getCommonGreetings - happy flow" () {

		given :

		Map<String, String> commonGreetingsMap = new HashMap<String, String>()
		commonGreetingsMap.put("birthday", "HappyBirthday");
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.COMMON_GREETINGS_SITE_PROPERTY_NAME) >> commonGreetingsMap

		when:

		def resultMap = bbbSiteRepoToolsTestObj.getCommonGreetings(SITE_ID_BEDBATH_US)

		then:
			resultMap.equals(commonGreetingsMap)
	}
		
	
	def "getCommonGreetings -  Site Configuration is null" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, null) >> siteConfigurationMock

		when:

		def commonGreetingsMap = bbbSiteRepoToolsTestObj.getCommonGreetings(SITE_ID_BEDBATH_US)

		then:
		commonGreetingsMap == null
		thrown BBBBusinessException
	}

	def "getCommonGreetings - commonGreetingsMap is null" () {

		given :
		

		Map<String, String> commonGreetingsMap = null
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.COMMON_GREETINGS_SITE_PROPERTY_NAME) >> commonGreetingsMap

		when:

		def resultsMap = bbbSiteRepoToolsTestObj.getCommonGreetings(SITE_ID_BEDBATH_US)

		then:
		resultsMap.isEmpty() == true
	}

	def "getCommonGreetings - Repository exception while getting site item" () {

		given :

		Map<String, String> commonGreetingsMap = new HashMap<String, String>()
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Exception while getting repo item")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.COMMON_GREETINGS_SITE_PROPERTY_NAME) >> commonGreetingsMap

		when:

		commonGreetingsMap = bbbSiteRepoToolsTestObj.getCommonGreetings(SITE_ID_BEDBATH_US)

		then:
		commonGreetingsMap.isEmpty() == true
		thrown BBBSystemException
	}

	/* getCommonGreetings - test cases - ENDS */

	/* getLTLAssemblyFeeSkuDetails - test cases - STARTS */

	def "getLTLAssemblyFeeSkuDetails - happy flow" () {
		
				given :
				
				
		
				Double ltlAssemblySkuPrice = 10.0
				String ltlAssemblyFeeSkuId = "sku15025883"
				String ltlAssemblyFeeProductId = "prod15025883"
				
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.LTL_ASSEMBLY_SKU_PRICE_SITE_PROPERTY_NAME) >> ltlAssemblySkuPrice
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.LTL_ASSEMBLY_SKU_SITE_PROPERTY_NAME) >> ltlAssemblyFeeSkuId
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.LTL_ASSEMBLY_PRODUCT_SITE_PROPERTY_NAME) >> ltlAssemblyFeeProductId
				
				when:
		
				def ltlAssemblyFeeVO = bbbSiteRepoToolsTestObj.getLTLAssemblyFeeSkuDetails(SITE_ID_BEDBATH_US)
		
				then:
				ltlAssemblyFeeVO.getLtlAssemblySkuPrice().equals(ltlAssemblySkuPrice)
				ltlAssemblyFeeVO.getLtlAssemblySkuId().equals(ltlAssemblyFeeSkuId)
				ltlAssemblyFeeVO.getLtlAssemblyProductId().equals(ltlAssemblyFeeProductId)
			}
	
	def "getLTLAssemblyFeeSkuDetails - Site Configuration is null" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, null) >> siteConfigurationMock

		when:

		def ltlAssemblyFeeVO = bbbSiteRepoToolsTestObj.getLTLAssemblyFeeSkuDetails(SITE_ID_BEDBATH_US)

		then:
		ltlAssemblyFeeVO == null
		thrown BBBBusinessException
	}

		def "getLTLAssemblyFeeSkuDetails - all inputs are null" () {
		
				given :
				
				Double ltlAssemblySkuPrice = null
				String ltlAssemblyFeeSkuId = null
				String ltlAssemblyFeeProductId = null
				
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.LTL_ASSEMBLY_SKU_PRICE_SITE_PROPERTY_NAME) >> ltlAssemblySkuPrice
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.LTL_ASSEMBLY_SKU_SITE_PROPERTY_NAME) >> ltlAssemblyFeeSkuId
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.LTL_ASSEMBLY_PRODUCT_SITE_PROPERTY_NAME) >> ltlAssemblyFeeProductId
				
				when:
		
				def ltlAssemblyFeeVO = bbbSiteRepoToolsTestObj.getLTLAssemblyFeeSkuDetails(SITE_ID_BEDBATH_US)
		
				then:
				ltlAssemblyFeeVO.getLtlAssemblySkuPrice() == 0.0
				ltlAssemblyFeeVO.getLtlAssemblySkuId().isEmpty() == true
				ltlAssemblyFeeVO.getLtlAssemblyProductId().isEmpty() == true
			}

	def "getLTLAssemblyFeeSkuDetails - RepositoryException while gettin site repo item" () {

		given :

		String assemblyFeeProduct = "15025883"
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Exception while getting site repo item")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.LTL_ASSEMBLY_PRODUCT_SITE_PROPERTY_NAME) >> assemblyFeeProduct

		when:

		def ltlAssemblyFeeVO = bbbSiteRepoToolsTestObj.getLTLAssemblyFeeSkuDetails(SITE_ID_BEDBATH_US)

		then:
		ltlAssemblyFeeVO == null
		thrown BBBSystemException
	}

	/* getLTLAssemblyFeeSkuDetails - test cases - ENDS */



	/* getLTLDeliveryChargeSkuDetails - test cases - STARTS */

	def "getLTLDeliveryChargeSkuDetails - happy flow" () {
		
				given :
		
				Double ltlAssemblySkuPrice = 10.0
				def ltlDeliveryChargeSkuId = "ltlDelCharSku01"
				def ltlDeliveryChargeProductId = "ltlDelCharProd01"
				
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.LTL_DELIVERY_CHARGE_SKU_PRICE_SITE_PROPERTY_NAME) >> ltlAssemblySkuPrice
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.LTL_DELIVERY_CHARGE_SKU_SITE_PROPERTY_NAME) >> ltlDeliveryChargeSkuId
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.LTL_DELIVERY_CHARGE_PRODUCT_SITE_PROPERTY_NAME) >> ltlDeliveryChargeProductId
				
				when:
		
				def ltlAssemblyFeeVO = bbbSiteRepoToolsTestObj.getLTLDeliveryChargeSkuDetails(SITE_ID_BEDBATH_US)
		
				then:
				ltlAssemblyFeeVO.getLtlDeliveryChargeSkuPrice() == ltlAssemblySkuPrice
				ltlAssemblyFeeVO.getLtlDeliveryChargeSkuId() == ltlDeliveryChargeSkuId
				ltlAssemblyFeeVO.getLtlDeliveryChargeProductId() == ltlDeliveryChargeProductId
				
	}
		
	
	def "getLTLDeliveryChargeSkuDetails - Site Configuration is null" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, null) >> siteConfigurationMock

		when:

		def ltlAssemblyFeeVO = bbbSiteRepoToolsTestObj.getLTLDeliveryChargeSkuDetails(SITE_ID_BEDBATH_US)

		then:
		ltlAssemblyFeeVO == null
		thrown BBBBusinessException
	}

	
	def "getLTLDeliveryChargeSkuDetails - SiteConfiguration ltlDeliveryChargeSkuPrice is not set (null)" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.LTL_DELIVERY_CHARGE_SKU_PRICE_SITE_PROPERTY_NAME) >> null

		when:

		def ltlAssemblyFeeVO = bbbSiteRepoToolsTestObj.getLTLDeliveryChargeSkuDetails(SITE_ID_BEDBATH_US)

		then:
		ltlAssemblyFeeVO.getLtlDeliveryChargeSkuPrice() == 0.0
	}
	
	def "getLTLDeliveryChargeSkuDetails - Site level ltlDeliveryChargeSku is not set (null)" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.LTL_DELIVERY_CHARGE_SKU_SITE_PROPERTY_NAME) >> null

		when:

		def ltlAssemblyFeeVO = bbbSiteRepoToolsTestObj.getLTLDeliveryChargeSkuDetails(SITE_ID_BEDBATH_US)

		then:
		ltlAssemblyFeeVO.getLtlDeliveryChargeSkuId() == ""
	}



	def "getLTLDeliveryChargeSkuDetails - site level ltlDeliveryChargeProduct is not set (null)" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.LTL_DELIVERY_CHARGE_PRODUCT_SITE_PROPERTY_NAME) >> null

		when:

		def ltlAssemblyFeeVO = bbbSiteRepoToolsTestObj.getLTLDeliveryChargeSkuDetails(SITE_ID_BEDBATH_US)

		then:
		ltlAssemblyFeeVO.getLtlDeliveryChargeProductId() == ""
	}

	def "getLtlDeliveryChargeSkuPrice - RepositoryException while getting site repo item" () {

		given :

		def ltlDeliveryChargeProduct = "178393"
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Exception while getting site repo item")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.LTL_DELIVERY_CHARGE_PRODUCT_SITE_PROPERTY_NAME) >> ltlDeliveryChargeProduct

		when:

		def ltlAssemblyFeeVO = bbbSiteRepoToolsTestObj.getLTLDeliveryChargeSkuDetails(SITE_ID_BEDBATH_US)

		then:

		ltlAssemblyFeeVO == null
		thrown BBBSystemException
	}
	/* getLTLDeliveryChargeSkuDetails - test cases - ENDS */


	/* getShippingMethodsForSku - test cases - STARTS */

	def "getShippingMethodsForSku - happy flow" () {
		
				given :
		
				def skuId = "SDD"
				def isLtlSku = false
				def isLtlShippingMethod = false
				def RepositoryItemMock skuRepoItemMock = new RepositoryItemMock(["id":skuId])
				List<ShipMethodVO> shipMethodVOListMock = new ArrayList<ShipMethodVO>()
				def shippingCharge = 3.75 
				
				Set<RepositoryItem> applicableShipMethodSet = new HashSet<>()
				Set<RepositoryItem> eligibleShipMethodSet = new HashSet<>()
				skuRepoItemMock.setProperties(["ltlFlag":isLtlSku,"isLTLShippingMethod":isLtlShippingMethod,"eligibleShipMethods":eligibleShipMethodSet,"shippingCharge":shippingCharge])
		
				applicableShipMethodSet.add(skuRepoItemMock)
				eligibleShipMethodSet.add(skuRepoItemMock)
				bbbSiteRepoToolsTestObj.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepoItemMock
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME) >> applicableShipMethodSet
				bbbSiteRepoToolsTestObj.getSddShipMethodId() >> skuId
		
				when :
		
				List<ShipMethodVO> resultShipMethodVOList = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(SITE_ID_BEDBATH_US,skuId,true)
		
				then :
				
				resultShipMethodVOList.get(0).isLtlShipMethod().equals(isLtlShippingMethod)
				resultShipMethodVOList.get(0).isEligibleShipMethod() == true
			}
		
	def "getShippingMethodsForSku - Sku repository item is null" () {

		given :
		def skuId = "12345"
		def RepositoryItemMock skuRepoItemMock = null
		def ShipMethodVO shipMethodVoMock = Mock()
		def List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>()
		
		bbbSiteRepoToolsTestObj.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepoItemMock 

		when :
		def result = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(skuId, skuId, false)

		then :
		
		result == null
		0 * skuRepoItemMock.getPropertyValue(*_)
		0 * shipMethodVoMock.setEligibleShipMethod(true)
		0 * shipMethodVOList.add(shipMethodVoMock)
		thrown BBBBusinessException
	}
	
	def "getShippingMethodsForSku - applicable Shipping method is empty not null" () {

		given :

		def skuId = "SDD"
		def isLtlSku = false
		def RepositoryItemMock skuRepoItemMock = Mock()
		def ShipMethodVO shipMethodVoMock = Mock()
		List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>()
		
		Set<RepositoryItem> applicableShipMethodSet = new HashSet<>()
		Set<RepositoryItem> eligibleShipMethodSet = new HashSet<>()

		bbbSiteRepoToolsTestObj.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepoItemMock
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME) >> applicableShipMethodSet
		bbbSiteRepoToolsTestObj.getSddShipMethodId() >> skuId

		when :

		shipMethodVOList = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(SITE_ID_BEDBATH_US,skuId,true)

		then :
		shipMethodVOList.isEmpty() == true
		0 * skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD)
		0 * shipMethodVoMock.setEligibleShipMethod(true)
		0 * shipMethodVOList.add(shipMethodVoMock)
		thrown BBBBusinessException

	}
		
	def "getShippingMethodsForSku - applicable Shipping method is null not empty" () {

		given :

		def skuId = "SDD"
		def isLtlSku = false
		def RepositoryItemMock skuRepoItemMock = Mock()
		def List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>()
		def ShipMethodVO shipMethodVoMock = Mock()
		
		Set<RepositoryItem> applicableShipMethodSet = null

		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD) >> isLtlSku

		bbbSiteRepoToolsTestObj.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepoItemMock
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME) >> applicableShipMethodSet
		bbbSiteRepoToolsTestObj.getSddShipMethodId() >> skuId

		when :

		shipMethodVOList = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(SITE_ID_BEDBATH_US,skuId,true)

		then :
		shipMethodVOList.isEmpty() == true
		//This code is inside the if block (applicableShipMetho != null) so will not be executed
		0 * skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD)		
		thrown BBBBusinessException

	}
	
	
	def "getShippingMethodsForSku - ApplicableShippingMethod not empty  and isLtlSku null  and LTLShippingMethod true" () {

		given :

		def skuId = "SDD"
		def isLtlSku = null
		def isLtlShippingMethod = true
		def RepositoryItemMock skuRepoItemMock = Mock()
		def List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>()
		def ShipMethodVO shipMethodVoMock = Mock()

		Set<RepositoryItem> applicableShipMethodSet = new HashSet<>()

		skuRepoItemMock.getRepositoryId() >> skuId
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> isLtlSku
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD) >> isLtlShippingMethod
		applicableShipMethodSet.add(skuRepoItemMock)

		bbbSiteRepoToolsTestObj.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepoItemMock
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME) >> applicableShipMethodSet
		bbbSiteRepoToolsTestObj.getSddShipMethodId() >> skuId

		when :

		shipMethodVOList = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(SITE_ID_BEDBATH_US,skuId,true)

		then :
		
		shipMethodVOList.isEmpty() == true
		0 * shipMethodVoMock.setEligibleShipMethod(true)
		0 * shipMethodVOList.add(shipMethodVoMock)
	}
	
	def "getShippingMethodsForSku  - for TBS_BedBathUS and sameDayDeliveryFlag is enabled (true)" () {

		given :

		def skuId = "SDD"
		def isLtlSku = false
		def isLtlShippingMethod = false
		def sameDayDeliveryFlag = true
		def RepositoryItemMock skuRepoItemMock = Mock()
		List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>()


		Set<RepositoryItem> applicableShipMethodSet = new HashSet<>()

		skuRepoItemMock.getRepositoryId() >> skuId
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> isLtlSku
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD) >> isLtlShippingMethod
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.ELIGIBLE_SHIP_METHODS_PROPERTY_NAME) >> applicableShipMethodSet
		applicableShipMethodSet.add(skuRepoItemMock)

		bbbSiteRepoToolsTestObj.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepoItemMock
		siteRepositoryMock.getItem(TBSConstants.SITE_TBS_BAB_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME) >> applicableShipMethodSet
		bbbSiteRepoToolsTestObj.getSddShipMethodId() >> skuId

		when :

		shipMethodVOList = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(TBSConstants.SITE_TBS_BAB_US,skuId,sameDayDeliveryFlag)

		then :
		shipMethodVOList.get(0).isLtlShipMethod() == isLtlShippingMethod
		0 * bbbSiteRepoToolsTestObj.updateListWithSddShipMethod(_)

	}
	
	def "getShippingMethodsForSku  - for site TBS_BedBathCanada" () {

		given :

		def skuId = "SDD"
		def isLtlSku = false
		def isLtlShippingMethod = false
		def sameDayDeliveryFlag = true
		def RepositoryItemMock skuRepoItemMock = Mock()
		List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>()


		Set<RepositoryItem> applicableShipMethodSet = new HashSet<>()

		skuRepoItemMock.getRepositoryId() >> skuId
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> isLtlSku
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD) >> isLtlShippingMethod
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.ELIGIBLE_SHIP_METHODS_PROPERTY_NAME) >> applicableShipMethodSet
		applicableShipMethodSet.add(skuRepoItemMock)

		bbbSiteRepoToolsTestObj.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepoItemMock
		siteRepositoryMock.getItem(TBSConstants.SITE_TBS_BAB_CA, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME) >> applicableShipMethodSet
		bbbSiteRepoToolsTestObj.getSddShipMethodId() >> skuId

		when :

		shipMethodVOList = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(TBSConstants.SITE_TBS_BAB_CA,skuId,sameDayDeliveryFlag)

		then :
		shipMethodVOList.get(0).isLtlShipMethod() == isLtlShippingMethod
		shipMethodVOList.get(0).shippingRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) == isLtlSku
		0 * bbbSiteRepoToolsTestObj.updateListWithSddShipMethod(_)
	}

	def "getShippingMethodsForSku - for site TBS_BuyBuyBaby" () {

		given :

		def skuId = "SDD"
		def isLtlSku = false
		def isLtlShippingMethod = false
		def sameDayDeliveryFlag = true
		def RepositoryItemMock skuRepoItemMock = Mock()
		List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>()


		Set<RepositoryItem> applicableShipMethodSet = new HashSet<>()

		skuRepoItemMock.getRepositoryId() >> skuId
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> isLtlSku
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD) >> isLtlShippingMethod
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.ELIGIBLE_SHIP_METHODS_PROPERTY_NAME) >> applicableShipMethodSet
		applicableShipMethodSet.add(skuRepoItemMock)

		bbbSiteRepoToolsTestObj.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepoItemMock
		siteRepositoryMock.getItem(TBSConstants.SITE_TBS_BBB, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME) >> applicableShipMethodSet
		bbbSiteRepoToolsTestObj.getSddShipMethodId() >> skuId

		when :

		shipMethodVOList = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(TBSConstants.SITE_TBS_BBB,skuId,sameDayDeliveryFlag)

		then :
		shipMethodVOList.get(0).isLtlShipMethod() == isLtlShippingMethod
		shipMethodVOList.get(0).shippingRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) == isLtlSku
		0 * bbbSiteRepoToolsTestObj.updateListWithSddShipMethod(_)

	}

	def "getShippingMethodsForSku  - sameDeliveryFlag is disabled (false)" () {

		given :

		def skuId = "SDD"
		def isLtlSku = false
		def isLtlShippingMethod = false
		def sameDayDeliveryFlag = false
		def RepositoryItemMock skuRepoItemMock = Mock()
		List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>()


		Set<RepositoryItem> applicableShipMethodSet = new HashSet<>()

		skuRepoItemMock.getRepositoryId() >> skuId
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> isLtlSku
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD) >> isLtlShippingMethod
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.ELIGIBLE_SHIP_METHODS_PROPERTY_NAME) >> applicableShipMethodSet
		applicableShipMethodSet.add(skuRepoItemMock)

		bbbSiteRepoToolsTestObj.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepoItemMock
		siteRepositoryMock.getItem(TBSConstants.SITE_TBS_BBB, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME) >> applicableShipMethodSet
		bbbSiteRepoToolsTestObj.getSddShipMethodId() >> skuId

		when :

		shipMethodVOList = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(TBSConstants.SITE_TBS_BBB,skuId,sameDayDeliveryFlag)

		then :
		
		shipMethodVOList.get(0).isLtlShipMethod() == isLtlShippingMethod
		shipMethodVOList.get(0).shippingRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) == isLtlSku
		0 * bbbSiteRepoToolsTestObj.updateListWithSddShipMethod(_)

	}

	def "getShippingMethodsForSku - siteID is null for updateListWithSddShipMethod method call" () {

		given :

		def skuId = "SDD"
		def isLtlSku = false
		def isLtlShippingMethod = false
		def sameDayDeliveryFlag = true
		def siteID = null
		def RepositoryItemMock skuRepoItemMock = Mock()
		List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>()


		Set<RepositoryItem> applicableShipMethodSet = new HashSet<>()

		skuRepoItemMock.getRepositoryId() >> skuId
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> isLtlSku
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD) >> isLtlShippingMethod
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.ELIGIBLE_SHIP_METHODS_PROPERTY_NAME) >> applicableShipMethodSet
		applicableShipMethodSet.add(skuRepoItemMock)

		bbbSiteRepoToolsTestObj.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepoItemMock
		siteRepositoryMock.getItem(siteID, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME) >> applicableShipMethodSet
		bbbSiteRepoToolsTestObj.getSddShipMethodId() >> skuId

		when :

		shipMethodVOList = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(siteID,skuId,sameDayDeliveryFlag)
		
		then :
		
		shipMethodVOList.get(0).isLtlShipMethod() == isLtlShippingMethod
		shipMethodVOList.get(0).shippingRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) == isLtlSku
		
	}

	def "getShippingMethodsForSku - ApplicableShippingMethod not equals sddShipMethod" () {

		given :

		def sddSkuId = "SDD"
		def skuId = "tempSku123"
		def isLtlSku = false
		def isLtlShippingMethod = false
		def sameDayDeliveryFlag = false
		def RepositoryItemMock skuRepoItemMock = Mock()
		List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>()


		Set<RepositoryItem> applicableShipMethodSet = new HashSet<>()

		skuRepoItemMock.getRepositoryId() >> skuId
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> isLtlSku
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD) >> isLtlShippingMethod
		applicableShipMethodSet.add(skuRepoItemMock)

		bbbSiteRepoToolsTestObj.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepoItemMock
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME) >> applicableShipMethodSet
		bbbSiteRepoToolsTestObj.getSddShipMethodId() >> sddSkuId

		when :

		shipMethodVOList = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(SITE_ID_BEDBATH_US,skuId,sameDayDeliveryFlag)

		then :
		shipMethodVOList.get(0).isLtlShipMethod() == isLtlShippingMethod
		shipMethodVOList.get(0).shippingRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) == isLtlSku
	}
	
	def "getShippingMethodsForSku - shipIdList does not contain eligible ship methods | isLtlSku false" () {

		given :

		def sddShipMethodId = "SDD"
		def isLtlSku = false
		def isLtlShippingMethod = true
		def RepositoryItemMock shippingMethodRepoItemMock = Mock()
		def RepositoryItemMock eligibleshipMethodRepoItemMock = Mock()
		def RepositoryItemMock skuRepoItemMock = Mock()
		List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>()

		Set<RepositoryItem> applicableShipMethodSet = new HashSet<>()
		Set<RepositoryItem> eligibleShipMethodSet = new HashSet<>()

		shippingMethodRepoItemMock.getRepositoryId() >> sddShipMethodId
		shippingMethodRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> isLtlSku

		eligibleshipMethodRepoItemMock.getRepositoryId() >> "shipMethod1"
		eligibleshipMethodRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD) >> isLtlShippingMethod
		eligibleshipMethodRepoItemMock.getRepositoryId() >> sddShipMethodId

		skuRepoItemMock.getRepositoryId() >> sddShipMethodId
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> isLtlSku
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.ELIGIBLE_SHIP_METHODS_PROPERTY_NAME) >> eligibleShipMethodSet

		applicableShipMethodSet.add(shippingMethodRepoItemMock)
		eligibleShipMethodSet.add(eligibleshipMethodRepoItemMock)

		bbbSiteRepoToolsTestObj.getCatalogRepository().getItem(sddShipMethodId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepoItemMock
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME) >> applicableShipMethodSet
		bbbSiteRepoToolsTestObj.getSddShipMethodId() >> sddShipMethodId

		when :

		shipMethodVOList = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(SITE_ID_BEDBATH_US,sddShipMethodId,true)

		then :
		shipMethodVOList.isEmpty() == true

	}

	def "getShippingMethodsForSku - shipIdList does not contain eligible ship methods | isLtlSku true" () {

		given :

		def sddShipMethodId = "SDD"
		def isLtlSku = true
		def isLtlShippingMethod = true
		def RepositoryItemMock shippingMethodRepoItemMock = Mock()
		def RepositoryItemMock eligibleshipMethodRepoItemMock = Mock()
		def RepositoryItemMock skuRepoItemMock = Mock()
		List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>()
		ShipMethodVO shipMethodVOMock = Mock()
		
		Set<RepositoryItem> applicableShipMethodSet = new HashSet<>()
		Set<RepositoryItem> eligibleShipMethodSet = new HashSet<>()

		shippingMethodRepoItemMock.getRepositoryId() >> sddShipMethodId
		shippingMethodRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD) >> isLtlSku

		eligibleshipMethodRepoItemMock.getRepositoryId() >> "shipMethod1"
		eligibleshipMethodRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD) >> isLtlShippingMethod
		eligibleshipMethodRepoItemMock.getRepositoryId() >> sddShipMethodId

		skuRepoItemMock.getRepositoryId() >> sddShipMethodId
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> isLtlSku
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.ELIGIBLE_SHIP_METHODS_PROPERTY_NAME) >> eligibleShipMethodSet

		applicableShipMethodSet.add(shippingMethodRepoItemMock)
		eligibleShipMethodSet.add(eligibleshipMethodRepoItemMock)

		bbbSiteRepoToolsTestObj.getCatalogRepository().getItem(sddShipMethodId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepoItemMock
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME) >> applicableShipMethodSet
		bbbSiteRepoToolsTestObj.getSddShipMethodId() >> sddShipMethodId

		when :

		shipMethodVOList = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(SITE_ID_BEDBATH_US,sddShipMethodId,true)

		then :
		
		shipMethodVOList.get(0).isLtlShipMethod() == isLtlShippingMethod
		shipMethodVOList.get(0).isEligibleShipMethod() == true

	}

	def "getShippingMethodsForSku - site configuration is null" () {

		given :

		def skuId = "SDD"
		def isLtlSku = false
		def isLtlShippingMethod = false
		def RepositoryItemMock skuRepoItemMock = Mock()
		List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>()

		Set<RepositoryItem> applicableShipMethodSet = new HashSet<>()
		Set<RepositoryItem> eligibleShipMethodSet = new HashSet<>()

		applicableShipMethodSet.add(skuRepoItemMock)
		eligibleShipMethodSet.add(skuRepoItemMock)
		catalogRepositoryMock.getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> skuRepoItemMock

		skuRepoItemMock.getRepositoryId() >> skuId
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> isLtlSku
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD) >> isLtlShippingMethod
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.ELIGIBLE_SHIP_METHODS_PROPERTY_NAME) >> eligibleShipMethodSet

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> null
		bbbSiteRepoToolsTestObj.getSddShipMethodId() >> skuId

		when :

		shipMethodVOList = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(SITE_ID_BEDBATH_US,skuId,true)

		then :
		shipMethodVOList.isEmpty() == true
		thrown BBBBusinessException
	}
	
	def "getShippingMethodsForSku - RepositoryException while getting repo item" () {

		given :

		def skuId = "SDD"
		def isLtlSku = false
		def isLtlShippingMethod = false
		def RepositoryItemMock skuRepoItemMock = Mock()
		List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>()

		Set<RepositoryItem> applicableShipMethodSet = new HashSet<>()
		Set<RepositoryItem> eligibleShipMethodSet = new HashSet<>()

		skuRepoItemMock.getRepositoryId() >> skuId
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU) >> isLtlSku
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD) >> isLtlShippingMethod
		skuRepoItemMock.getPropertyValue(BBBCatalogConstants.ELIGIBLE_SHIP_METHODS_PROPERTY_NAME) >> eligibleShipMethodSet
		applicableShipMethodSet.add(skuRepoItemMock)
		eligibleShipMethodSet.add(skuRepoItemMock)
		bbbSiteRepoToolsTestObj.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Exception while getting site item")}
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME) >> applicableShipMethodSet
		bbbSiteRepoToolsTestObj.getSddShipMethodId() >> skuId

		when :

		shipMethodVOList = bbbSiteRepoToolsTestObj.getShippingMethodsForSku(SITE_ID_BEDBATH_US,skuId,true)

		then :
		
		shipMethodVOList.isEmpty() == true
		thrown BBBSystemException
	}

	/* getShippingMethodsForSku - test cases - ENDS */	


	/* getDefaultCountryForSite - test cases - STARTS */
	
	def "getDefaultCountryForSite - happy flow" () {
		
		given :
		   	    
		 def inputDefaultCountry = "US"
		 siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		 siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFAULT_COUNTRY_SITE_PROPERTY_NAME) >> inputDefaultCountry
		 
		when:
		  
		  def defaultCountryForSite = bbbSiteRepoToolsTestObj.getDefaultCountryForSite(SITE_ID_BEDBATH_US)
		
		then:
			defaultCountryForSite == inputDefaultCountry
	 }
		
	def "getDefaultCountryForSite - SiteID is null" () {

		when:

		def defaultCountryForSite = bbbSiteRepoToolsTestObj.getDefaultCountryForSite(null)

		then:
		defaultCountryForSite == null
		thrown BBBBusinessException
	}

	def "getDefaultCountryForSite - SiteID is emtpy" () {

		when:

		def defaultCountryForSite = bbbSiteRepoToolsTestObj.getDefaultCountryForSite("")

		then:
		defaultCountryForSite == null
		thrown BBBBusinessException
	}


	def "getDefaultCountryForSite - Site Configuration is null" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> null

		when:

		def defaultCountryForSite = bbbSiteRepoToolsTestObj.getDefaultCountryForSite(SITE_ID_BEDBATH_US)

		then:
		defaultCountryForSite == null
		thrown BBBBusinessException
	}


	def "getDefaultCountryForSite - Site Configuration is not null DEFAULT_COUNTRY_SITE_PROPERTY_NAME is null" () {

		given :

		def inputDefaultCountry = null
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFAULT_COUNTRY_SITE_PROPERTY_NAME) >> null
		when:

		def defaultCountryForSite = bbbSiteRepoToolsTestObj.getDefaultCountryForSite(SITE_ID_BEDBATH_US)

		then:
		defaultCountryForSite == null
	}

	def "getDefaultCountryForSite - Exception while getting siteItem " () {

		given :

		def inputDefaultCountry = "US"
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Exception while getting Site Item")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFAULT_COUNTRY_SITE_PROPERTY_NAME) >> inputDefaultCountry
		when:

		def defaultCountryForSite = bbbSiteRepoToolsTestObj.getDefaultCountryForSite(SITE_ID_BEDBATH_US)

		then:
		defaultCountryForSite != inputDefaultCountry
		thrown BBBSystemException
	}


	/* getDefaultCountryForSite - test cases - ENDS */


	/* getSiteChatAttributes- test cases - STARTS */
	
	def "getSiteChatAttributes - Happy flow - flags are true " (){
		
				given :
		
				def siteChatOnOffFlag = true
				def pdpChatOnOffFlag = true
				def pdpChatOverrideFlag = true
				def pdpDaasOnOffFlag = true
				def pdpDaasOverrideFlag = true
				def currentDate = new Date()
		
				def chatUrl = "chat.bbb.com"
				def weekDayOpenTime = currentDate - 1
				def weekDayCloseTime = currentDate + 1
				def weekEndOpenTime = currentDate - 2
				def weekEndCloseTime = currentDate + 2
		
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_GLOBAL_PROPERTY_NAME) >> siteChatOnOffFlag
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_PDP_PROPERTY_NAME) >> siteChatOnOffFlag
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_PDP_OVERRIDE_PROPERTY_NAME) >> siteChatOnOffFlag
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DAAS_PDP_PROPERTY_NAME) >> siteChatOnOffFlag
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DAAS_PDP_OVERRIDE_PROPERTY_NAME) >> siteChatOnOffFlag
		
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_URL_PROPERTY_NAME) >> chatUrl
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_WEEKDAY_OPEN_TIME_PROPERTY_NAME) >> weekDayOpenTime
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_WEEKDAY_CLOSE_TIME_PROPERTY_NAME) >> weekDayCloseTime
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_WEEKEND_OPEN_TIME_PROPERTY_NAME) >> weekEndOpenTime
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_WEEKEND_CLOSE_TIME_PROPERTY_NAME) >> weekEndCloseTime
		
				when:
		
				def siteChatAttributes = bbbSiteRepoToolsTestObj.getSiteChatAttributes(SITE_ID_BEDBATH_US)
		
				then:
		
				siteChatAttributes.getChatURL() == chatUrl
				siteChatAttributes.getWeekDayOpenTime() == weekDayOpenTime
				siteChatAttributes.getWeekDayCloseTime() == weekDayCloseTime
				siteChatAttributes.getWeekEndOpenTime() == weekEndOpenTime
				siteChatAttributes.getWeekEndCloseTime() == weekEndCloseTime
		
		}
	
	def "getSiteChatAttributes - SiteConfiguration is not set (null)" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> null

		when:

		def SiteChatAttributes = bbbSiteRepoToolsTestObj.getSiteChatAttributes(SITE_ID_BEDBATH_US)

		then:
		SiteChatAttributes == null
		thrown BBBSystemException

	}

	def "getSiteChatAttributes - inputValues are not set (null)" () {

		given :

		def siteChatOnOffFlag = null
		def pdpChatOnOffFlag = null
		def pdpChatOverrideFlag = null
		def pdpDaasOnOffFlag = null
		def pdpDaasOverrideFlag = null
		def currentDate = new Date()

		def chatUrl = "chat.bbb.com"
		def weekDayOpenTime = currentDate - 1
		def weekDayCloseTime = currentDate + 1
		def weekEndOpenTime = currentDate - 2
		def weekEndCloseTime = currentDate + 2

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_GLOBAL_PROPERTY_NAME) >> siteChatOnOffFlag
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_PDP_PROPERTY_NAME) >> siteChatOnOffFlag
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_PDP_OVERRIDE_PROPERTY_NAME) >> siteChatOnOffFlag
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DAAS_PDP_PROPERTY_NAME) >> siteChatOnOffFlag
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DAAS_PDP_OVERRIDE_PROPERTY_NAME) >> siteChatOnOffFlag

		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_URL_PROPERTY_NAME) >> chatUrl
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_WEEKDAY_OPEN_TIME_PROPERTY_NAME) >> weekDayOpenTime
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_WEEKDAY_CLOSE_TIME_PROPERTY_NAME) >> weekDayCloseTime
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_WEEKEND_OPEN_TIME_PROPERTY_NAME) >> weekEndOpenTime
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_WEEKEND_CLOSE_TIME_PROPERTY_NAME) >> weekEndCloseTime

		when:

		def siteChatAttributes = bbbSiteRepoToolsTestObj.getSiteChatAttributes(SITE_ID_BEDBATH_US)

		then:

		siteChatAttributes.getChatURL() == chatUrl
		siteChatAttributes.getWeekDayOpenTime() == weekDayOpenTime
		siteChatAttributes.getWeekDayCloseTime() == weekDayCloseTime
		siteChatAttributes.getWeekEndOpenTime() == weekEndOpenTime
		siteChatAttributes.getWeekEndCloseTime() == weekEndCloseTime

	}

	def "getSiteChatAttributes - exception while getting repo item" (){

		given :

		def siteChatOnOffFlag = true
		def pdpChatOnOffFlag = true
		def pdpChatOverrideFlag = true
		def pdpDaasOnOffFlag = true
		def pdpDaasOverrideFlag = true
		def currentDate = new Date()

		def chatUrl = "chat.bbb.com"
		def weekDayOpenTime = currentDate - 1
		def weekDayCloseTime = currentDate + 1
		def weekEndOpenTime = currentDate - 2
		def weekEndCloseTime = currentDate + 2

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("exception while getting repo item")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_GLOBAL_PROPERTY_NAME) >> siteChatOnOffFlag
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_PDP_PROPERTY_NAME) >> siteChatOnOffFlag
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_PDP_OVERRIDE_PROPERTY_NAME) >> siteChatOnOffFlag
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DAAS_PDP_PROPERTY_NAME) >> siteChatOnOffFlag
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DAAS_PDP_OVERRIDE_PROPERTY_NAME) >> siteChatOnOffFlag

		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_URL_PROPERTY_NAME) >> chatUrl
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_WEEKDAY_OPEN_TIME_PROPERTY_NAME) >> weekDayOpenTime
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_WEEKDAY_CLOSE_TIME_PROPERTY_NAME) >> weekDayCloseTime
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_WEEKEND_OPEN_TIME_PROPERTY_NAME) >> weekEndOpenTime
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.CHAT_WEEKEND_CLOSE_TIME_PROPERTY_NAME) >> weekEndCloseTime

		when:

		def siteChatAttributes = bbbSiteRepoToolsTestObj.getSiteChatAttributes(SITE_ID_BEDBATH_US)

		then:

		siteChatAttributes == null
		thrown BBBSystemException

	}

	/* getSiteChatAttributes- test cases - ENDS */

	/* getGridListAttributes- test cases - STARTS */
	
	def "getGridListAttributes - Happy flow " () {
		
				given :
		
				def inputDefaultCountry = "US"
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.GRID_LIST_PROPERTY_NAME) >> inputDefaultCountry
				when:
		
				def siteGridListDefaultValue = bbbSiteRepoToolsTestObj.getGridListAttributes(SITE_ID_BEDBATH_US)
		
				then:
				siteGridListDefaultValue == inputDefaultCountry
		}
	
	def "getGridListAttributes - Site Configuration is null" () {

		given :

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> null

		when:

		def siteGridListDefaultValue = bbbSiteRepoToolsTestObj.getGridListAttributes(SITE_ID_BEDBATH_US)

		then:
		siteGridListDefaultValue == null
		thrown BBBSystemException
	}

	def "getGridListAttributes - Site Configuration is not null GRID_LIST_PROPERTY_NAME is null" () {

		given :

		def inputDefaultCountry = null
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.GRID_LIST_PROPERTY_NAME) >> null
		when:

		def siteGridListDefaultValue = bbbSiteRepoToolsTestObj.getGridListAttributes(SITE_ID_BEDBATH_US)

		then:
		siteGridListDefaultValue == null
	}

		def "getGridListAttributes - Exception while getting site repo item " () {

		given :

		def inputDefaultCountry = "US"
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Repository exception - getting site repo item")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.GRID_LIST_PROPERTY_NAME) >> inputDefaultCountry
		when:

		def siteGridListDefaultValue = bbbSiteRepoToolsTestObj.getGridListAttributes(SITE_ID_BEDBATH_US)

		then:
		siteGridListDefaultValue != inputDefaultCountry

	}

	/* getGridListAttributes- test cases - ENDS */

	/* getBopusEligibleStates- test cases - STARTS */

		def "getBopusEligibleStates - happy flow" () {
			
					given :
					
					bbbSiteRepoToolsTestObj = Spy()
					bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
					Set<RepositoryItem> allBopusExcludedStates = new HashSet<>()
					List<String> bopusEligibleStates = new ArrayList<>()
					def bopusRepoItemId = "bopus01"
					def bopusExcludedItemId = "bopusExcl01"
					RepositoryItemMock bopusRepoItemMock = new RepositoryItemMock(["id":bopusRepoItemId])
					RepositoryItemMock bopusExcludedState = new RepositoryItemMock(["id":bopusExcludedItemId])
					RepositoryItemMock[] bopusRepoItems = [bopusRepoItemMock]//bopusEligibleStates.addAll(bopusRepoItems)
					allBopusExcludedStates.addAll(bopusExcludedState)
					bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bopusRepoItems
					siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,	BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
					siteConfigurationMock.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUDED_STATE_SITE_PROPERTY_NAME) >> allBopusExcludedStates
			
					when :
			
					bopusEligibleStates =  bbbSiteRepoToolsTestObj.getBopusEligibleStates(SITE_ID_BEDBATH_US)
			
					then :
					bopusEligibleStates.get(0).equals(bopusRepoItemId)
					
	}
			
		
	def "getBopusEligibleStates -  scenario with null values" () {


		given :

		bbbSiteRepoToolsTestObj = Spy()

		bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)
		bbbSiteRepoToolsTestObj.setBopusEligibleStateQuery("bopus=1")

		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		List<String> bopusEligibleStates = new ArrayList<>()
		bbbSiteRepoToolsTestObj.executeRQLQuery(bbbSiteRepoToolsTestObj.getBopusEligibleStateQuery(),BBBCatalogConstants.STATE_ITEM_DESCRIPTOR, shippingRepositoryMock) >> null
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,	BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> null

		when :

		bopusEligibleStates =  bbbSiteRepoToolsTestObj.getBopusEligibleStates(SITE_ID_BEDBATH_US)

		then :
		bopusEligibleStates.isEmpty() == true
		thrown BBBException


	}

	def "getBopusEligibleStates -  allBopusExcludedStates is null and is empty" () {

		given :

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		Set<RepositoryItem> allBopusExcludedStates = null
		List<RepositoryItem> allStateRepoItemsList = Mock()
		
		def bopusStateId = "bopus01"
		List<String> bopusEligibleStates = new ArrayList<>()
		RepositoryItemMock bopusRepoItemMock = new RepositoryItemMock(["id":bopusStateId])
		RepositoryItemMock bopusExcludedState = new RepositoryItemMock()
		RepositoryItemMock[] bopusRepoItems = [bopusRepoItemMock]
		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bopusRepoItems
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,	BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUDED_STATE_SITE_PROPERTY_NAME) >> allBopusExcludedStates

		when :

		bopusEligibleStates =  bbbSiteRepoToolsTestObj.getBopusEligibleStates(SITE_ID_BEDBATH_US)

		then :
		bopusEligibleStates.get(0).equals(bopusStateId)
	}


	def "getBopusEligibleStates - allBopusExcludedStates is empty" () {


		given :

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		Set<RepositoryItem> allBopusExcludedStates = new HashSet<>()
		List<String> bopusEligibleStates = new ArrayList<>()
		def bopusRepoItemId = "bopus01"
		RepositoryItemMock bopusRepoItemMock = new RepositoryItemMock("id":bopusRepoItemId)
		RepositoryItemMock[] bopusRepoItems = [bopusRepoItemMock]
		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bopusRepoItems
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,	BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUDED_STATE_SITE_PROPERTY_NAME) >> allBopusExcludedStates

		when :

		bopusEligibleStates =  bbbSiteRepoToolsTestObj.getBopusEligibleStates(SITE_ID_BEDBATH_US)

		then :
		bopusEligibleStates.get(0).equals(bopusRepoItemId)

	}

	/* 
	 * (allBopusExcludedStates != null) && (!allBopusExcludedStates.isEmpty()) - code to be tested
	 * If block's one branch(condition) will be skipped 
	 * Because, it can't be checked for empty with null object		  
	 * i.e., allBopusStates == null. allBopusExcludedStates.isEmpty() will throw NullPointerException
	 * 
	 */
	
	def "getBopusEligibleStates - allStateRepoItems is empty" () {

		given :

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		Set<RepositoryItem> allBopusExcludedStates = new HashSet<>()
		List<String> bopusEligibleStates = new ArrayList<>()
		RepositoryItemMock bopusRepoItemMock = new RepositoryItemMock()
		RepositoryItemMock bopusExcludedState = new RepositoryItemMock()
		RepositoryItemMock[] bopusRepoItems = []
		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bopusRepoItems
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,	BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUDED_STATE_SITE_PROPERTY_NAME) >> allBopusExcludedStates

		when :

		bopusEligibleStates =  bbbSiteRepoToolsTestObj.getBopusEligibleStates(SITE_ID_BEDBATH_US)

		then :
		bopusEligibleStates.isEmpty() == true
		thrown BBBBusinessException
	}

	def "getBopusEligibleStates - siteId is null scenario" () {
		
		when :
		def bopusEligibleStates = bbbSiteRepoToolsTestObj.getBopusEligibleStates(null)
		then :
		bopusEligibleStates == null
		0 * bbbSiteRepoToolsTestObj.executeRQLQuery(*_)
		thrown BBBBusinessException
	}

	def "getBopusEligibleStates siteId is empty scenario" () {

		when :
		def bopusEligibleStates = bbbSiteRepoToolsTestObj.getBopusEligibleStates("")
		then :
		bopusEligibleStates == null
		thrown BBBBusinessException
	}

	def "getBopusEligibleStates - Exception while executing RQL query" () {


		given :

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		Set<RepositoryItem> allBopusExcludedStates = new HashSet<>()
		List<String> bopusEligibleStates = new ArrayList<>()
		RepositoryItemMock bopusRepoItemMock = new RepositoryItemMock()
		RepositoryItemMock bopusExcludedState = new RepositoryItemMock()
		RepositoryItemMock[] bopusRepoItems = [bopusRepoItemMock]//bopusEligibleStates.addAll(bopusRepoItems)
		allBopusExcludedStates.addAll(bopusExcludedState)
		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> {throw new RepositoryException("Exception while executing RQL query")}
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,	BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUDED_STATE_SITE_PROPERTY_NAME) >> allBopusExcludedStates

		when :

		bopusEligibleStates =  bbbSiteRepoToolsTestObj.getBopusEligibleStates(SITE_ID_BEDBATH_US)

		then :

		bopusEligibleStates.isEmpty() == true
		thrown BBBSystemException

	}


	/* getBopusEligibleStates- test cases - ENDS */	


	/* getSiteDetailFromSiteId - test cases - STARTS */

	def "getSiteDetailFromSiteId - happy flow " () {
		
				given :
				def siteName = "BedBathUS"
				def defaultCountry = "US"
		
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITE_NAME_SITE_PROPERTY_NAME) >> siteName
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFAULT_COUNTRY_SITE_PROPERTY_NAME) >> defaultCountry
		
				when :
				def siteDetails = bbbSiteRepoToolsTestObj.getSiteDetailFromSiteId(SITE_ID_BEDBATH_US)
		
				then :
				
				siteDetails.getSiteName().equals(siteName)
				siteDetails.getCountryCode().equals(defaultCountry)
	}
		
	def "getSiteDetailFromSiteId - siteId is null" () {

		when :

		def siteDetails = bbbSiteRepoToolsTestObj.getSiteDetailFromSiteId(null)

		then :

		siteDetails == null
		thrown BBBBusinessException

	}

	def "getSiteDetailFromSiteId - siteId is empty" () {

		when :

		def siteDetails = bbbSiteRepoToolsTestObj.getSiteDetailFromSiteId("")

		then :

		siteDetails == null
		thrown BBBBusinessException

	}

	def "getSiteDetailFromSiteId - siteRepoItem is null" () {

		given :
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> null

		when :
		def siteDetails = bbbSiteRepoToolsTestObj.getSiteDetailFromSiteId(SITE_ID_BEDBATH_US)

		then :

		siteDetails == null
		thrown BBBBusinessException

	}

	def "getSiteDetailFromSiteId - with null values " () {

		given :
		def siteName = null
		def defaultCountry = null

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITE_NAME_SITE_PROPERTY_NAME) >> siteName
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFAULT_COUNTRY_SITE_PROPERTY_NAME) >> defaultCountry

		when :
		def siteDetails = bbbSiteRepoToolsTestObj.getSiteDetailFromSiteId(SITE_ID_BEDBATH_US)

		then :

		siteDetails.getCountryCode() == null
		siteDetails.getSiteName() == null
	}

	def "getSiteDetailFromSiteId - exception while getting repo item" () {

		given :
		def siteName = "BedBathUS"
		def defaultCountry = "US"

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("exception while getting repo item")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITE_NAME_SITE_PROPERTY_NAME) >> siteName
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFAULT_COUNTRY_SITE_PROPERTY_NAME) >> defaultCountry

		when :
		def siteDetails = bbbSiteRepoToolsTestObj.getSiteDetailFromSiteId(SITE_ID_BEDBATH_US)

		then :

		siteDetails == null
		thrown BBBSystemException
	}

	/* getSiteDetailFromSiteId - test cases - ENDS */


	/* getRegistryTypeName - test cases - STARTS */

	def "getRegistryTypeName - happy flow" () {
		
				given :
				def registryTypes
				def registryCode = "BIR"
				def registryTypeCode = "BIR"
				def registryTypeName = "BirthDayRegistry"
		
				Set registryTypesSet = new HashSet<RepositoryItem>()
				registryTypesSet.add(registryTypeItemMock)
		
		
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPES_SITE_PROPERTY_NAME) >> registryTypesSet
				registryTypeItemMock.getPropertyValue(REGISTRY_TYPE_CODE) >> registryTypeCode
				registryTypeItemMock.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_NAME_REGISTRY_PROPERTY_NAME) >> registryTypeName
		
				when :
		
				registryTypes = 	bbbSiteRepoToolsTestObj.getRegistryTypeName(registryCode, SITE_ID_BEDBATH_US)
		
				then :
				registryTypes.equals(registryTypeName)
				
	}

	def "getRegistryTypeName - SiteConfiguration object is not set (null)" () {

		given :
		def registryTypes
		def registryCode = "BIR"
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> null

		when :

		registryTypes = 	bbbSiteRepoToolsTestObj.getRegistryTypeName(registryCode, SITE_ID_BEDBATH_US)

		then :
		registryTypes == null
		thrown BBBBusinessException

	}

	def "getRegistryTypeName - registryTypeCode is not set (null)" () {

		given :
		def registryTypes
		def registryCode = "BIR"
		def registryTypeCode = null

		Set registryTypesSet = new HashSet<RepositoryItem>()
		registryTypesSet.add(registryTypeItemMock)


		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPES_SITE_PROPERTY_NAME) >> registryTypesSet
		registryTypeItemMock.getPropertyValue(REGISTRY_TYPE_CODE) >> registryTypeCode

		when :

		registryTypes = 	bbbSiteRepoToolsTestObj.getRegistryTypeName(registryCode, SITE_ID_BEDBATH_US)

		then :
		registryTypes.isEmpty() == true
	}

	def "getRegistryTypeName - registryRepositoryItem is empty" () {

		given :
		def registryTypes
		def registryCode = "BIR"
		def registryTypeCode = "BIR"

		Set registryTypesSet = new HashSet<RepositoryItem>()

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPES_SITE_PROPERTY_NAME) >> registryTypesSet
		registryTypeItemMock.getPropertyValue(REGISTRY_TYPE_CODE) >> registryTypeCode

		when :

		registryTypes = 	bbbSiteRepoToolsTestObj.getRegistryTypeName(registryCode, SITE_ID_BEDBATH_US)

		then :
		registryTypes == ""
	}


	def "getRegistryTypeName - registryCode(input) not equals registryTypeCode (repo item)" () {

		given :
		def registryTypes
		def registryCode = "BIR"
		def registryTypeCode = "ANN"

		Set registryTypesSet = new HashSet<RepositoryItem>()
		registryTypesSet.add(registryTypeItemMock)


		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPES_SITE_PROPERTY_NAME) >> registryTypesSet
		registryTypeItemMock.getPropertyValue(REGISTRY_TYPE_CODE) >> registryTypeCode

		when :

		registryTypes = 	bbbSiteRepoToolsTestObj.getRegistryTypeName(registryCode, SITE_ID_BEDBATH_US)

		then :
		registryTypes == ""
		0 * registryTypeItemMock.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_NAME_REGISTRY_PROPERTY_NAME)
	}

	def "getRegistryTypeName - RepositoryException while getting site repo item" () {

		given :
		def registryTypes
		def registryCode = "BIR"
		def registryTypeCode = "BIR"
		def registryTypeName = "BirthDayRegistry"

		Set registryTypesSet = new HashSet<RepositoryItem>()
		registryTypesSet.add(registryTypeItemMock)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Exception while getting site repo item ")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPES_SITE_PROPERTY_NAME) >> registryTypesSet
		registryTypeItemMock.getPropertyValue(REGISTRY_TYPE_CODE) >> registryTypeCode
		registryTypeItemMock.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_NAME_REGISTRY_PROPERTY_NAME) >> registryTypeName

		when :

		registryTypes = 	bbbSiteRepoToolsTestObj.getRegistryTypeName(registryCode, SITE_ID_BEDBATH_US)

		then :
		registryTypes == null
		thrown BBBSystemException

	}

	/* getRegistryTypeName - test cases - ENDS */

	/* getDefaultPLPView - test cases - STARTS */

	def "getDefaultPLPView - happy flow" () {
		
				given :
				def defaultPLPView = "Single SKU view"
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFAULT_VIEW_PLP) >> defaultPLPView
				
				when :
				def defaultPLPViewReturned  = bbbSiteRepoToolsTestObj.getDefaultPLPView(SITE_ID_BEDBATH_US)
				
				then :
				defaultPLPViewReturned.equals(defaultPLPView)
	}		
	
	def "getDefaultPLPView - siteConfiguration is not set(null)" () {

		given :
		def defaultPLPView
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> null
		when :
		defaultPLPView  = bbbSiteRepoToolsTestObj.getDefaultPLPView(SITE_ID_BEDBATH_US)
		then :
		defaultPLPView == null
		thrown BBBSystemException
	}

	def "getDefaultPLPView - siteConfiguration RepositoryException scenario" () {

		given :
		def defaultPLPView = "Single SKU view"
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("getDefaultPLPView - Mock RepositoryException")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFAULT_VIEW_PLP) >> defaultPLPView
		when :
		def defaultPLPViewReturned  = bbbSiteRepoToolsTestObj.getDefaultPLPView(SITE_ID_BEDBATH_US)
		then :
		defaultPLPViewReturned.equals(defaultPLPView) == false
		thrown BBBSystemException
	}

	def "getDefaultPLPView IllegalArgumentException scenario" () { // not working

		given :
		def defaultPLPView = "Single SKU view"
		def undefinedColumn = "randomPage"

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFAULT_VIEW_PLP) >> {throw new IllegalArgumentException("getDefaultPLPView Mock IllegalArgumentException")}
		when :
		def defaultPLPViewReturned  = bbbSiteRepoToolsTestObj.getDefaultPLPView(SITE_ID_BEDBATH_US)
		then :
		defaultPLPViewReturned == null
		thrown BBBSystemException
	}

	/* getDefaultPLPView - test cases - ENDS */

	/* getTaxOverrideThreshold - test cases - STARTS */

	def "getTaxOverrideThreshold - happy flow" () {
		
				given :
		
				double inputTaxOverrideThreshold = 15.00
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.TBS_TAX_OVERRIDE_THRESHOLD) >> inputTaxOverrideThreshold
		
				when :
				def taxOverrideThreshold = bbbSiteRepoToolsTestObj.getTaxOverrideThreshold(SITE_ID_BEDBATH_US)
		
				then :
				taxOverrideThreshold == inputTaxOverrideThreshold
		
		}
	
	def "getTaxOverrideThreshold - siteConfiguration is not set (null)" () {

		given :

		def taxOverrideThreshold
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> null

		when :

		taxOverrideThreshold = bbbSiteRepoToolsTestObj.getTaxOverrideThreshold(SITE_ID_BEDBATH_US)

		then :
		taxOverrideThreshold == 0.0
	}

	def "getTaxOverrideThreshold - taxOverrideThreshold is not set(null)" () {

		given :

		def inputTaxOverrideThreshold = null
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.TBS_TAX_OVERRIDE_THRESHOLD) >> inputTaxOverrideThreshold

		when :
		def taxOverrideThreshold = bbbSiteRepoToolsTestObj.getTaxOverrideThreshold(SITE_ID_BEDBATH_US)

		then :
		taxOverrideThreshold == 0.0

	}

	def "getTaxOverrideThreshold - RepositoryException while getting tax threshold value " () {

		given :

		double inputTaxOverrideThreshold = 15.00
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Mock Exception while getting Site repo Item")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.TBS_TAX_OVERRIDE_THRESHOLD) >> inputTaxOverrideThreshold

		when :
		def taxOverrideThreshold = bbbSiteRepoToolsTestObj.getTaxOverrideThreshold(SITE_ID_BEDBATH_US)

		then :
		taxOverrideThreshold != inputTaxOverrideThreshold
		thrown BBBSystemException

	}

	/* getTaxOverrideThreshold - test cases - ENDS */

	/* getSiteLevelAttributes - test cases - STARTS */
	
	def "getSiteLevelAttributes - happy flow" () {
		
				given :
				def siteConfiguration = siteConfigurationMock
				Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<>()
				Set<RepositoryItem> attributeRepoItems = new HashSet<>()
				def attribRepoItemId = "attribRepo01"
				RepositoryItemMock attributeRepoItemMock = new RepositoryItemMock(["id":attribRepoItemId])
				def displayDescription = "displayDescriptionValue"
				def actionUrl = "http://test.bbb.com"
				def imageUrl = "http://img.bbb.com"
				
				attributeRepoItemMock.setProperties(["attributeValue":attributeRepoItems,"displayDescrip":displayDescription,"actionURL":actionUrl,"imageURL":imageUrl])
				
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration
				siteConfiguration.getPropertyValue(BBBCatalogConstants.SITE_ATTRIBUTE_VALUES_LIST) >> attributeRepoItems
		
				attributeRepoItems.add(attributeRepoItemMock)
				attributeNameRepoItemMap.put("InitalAttributes",attributeRepoItemMock)
				when :
				def siteLevelAttributes = bbbSiteRepoToolsTestObj.getSiteLevelAttributes(SITE_ID_BEDBATH_US, attributeNameRepoItemMap)
				then :
				siteLevelAttributes.get(attribRepoItemId).getActionURL() == actionUrl
				siteLevelAttributes.get(attribRepoItemId).getImageURL() == imageUrl
				siteLevelAttributes.get(attribRepoItemId).getAttributeDescrip() == displayDescription
	 }
	
	def "getSiteLevelAttributes - site configuration is not set (null)" () {
		
				given :
				def siteConfiguration = null
				Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<>()
				Set<RepositoryItem> attributeRepoItems = new HashSet<>()
				def attribRepoItemId = "attribRepo01"
				RepositoryItemMock attributeRepoItemMock = new RepositoryItemMock(["id":attribRepoItemId])
				def displayDescription = "displayDescriptionValue"
				def actionUrl = "http://test.bbb.com"
				def imageUrl = "http://img.bbb.com"
				
				attributeRepoItemMock.setProperties(["attributeValue":attributeRepoItems,"displayDescrip":displayDescription,"actionURL":actionUrl,"imageURL":imageUrl])
				
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration
		
				attributeRepoItems.add(attributeRepoItemMock)
				attributeNameRepoItemMap.put("InitalAttributes",attributeRepoItemMock)
				
				when :
				
				def siteLevelAttributes = bbbSiteRepoToolsTestObj.getSiteLevelAttributes(SITE_ID_BEDBATH_US, attributeNameRepoItemMap)
				
				then :
				
				siteLevelAttributes == null
	 }
		
	def "getSiteLevelAttributes - input values are null" () {

		given :
		
		def siteConfiguration = null
		Map<String, RepositoryItem> attributeNameRepoItemMap = null
		//Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<>()
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration
		
		when :
		def siteLevelAttributes = bbbSiteRepoToolsTestObj.getSiteLevelAttributes(null, attributeNameRepoItemMap)
		
		then :
		
		siteLevelAttributes.isEmpty()
		
	}


	def "getSiteLevelAttributes - attributeRepoItems is null" () {

		given :
		def siteConfiguration = siteConfigurationMock
		Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<>()
		Set<RepositoryItem> attributeRepoItems = null
		RepositoryItemMock attributeRepoItem = new RepositoryItemMock()
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration
		when :
		def siteLevelAttributes = bbbSiteRepoToolsTestObj.getSiteLevelAttributes(SITE_ID_BEDBATH_US, attributeNameRepoItemMap)
		then :
		siteLevelAttributes == null
	}

	def "getSiteLevelAttributes - attributeRepoItems is empty" () {

		given :
		
		def siteConfiguration = siteConfigurationMock
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration
		Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<>()
		RepositoryItemMock attributeRepoItemMock = new RepositoryItemMock()
		
		when :
		
		def siteLevelAttributes = bbbSiteRepoToolsTestObj.getSiteLevelAttributes(SITE_ID_BEDBATH_US, attributeNameRepoItemMap)
		
		then :
		
		siteLevelAttributes == null
	}

	def "getSiteLevelAttributes - attrRepItems(RepositoryItem set) is null" () {

		given :
		
		def siteConfiguration = siteConfigurationMock
		Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<>()
		Set<RepositoryItem> attributeRepoItems = null
		RepositoryItemMock attributeRepoItem = new RepositoryItemMock()
		def displayDescription = "displayDescriptionValue"
		def actionUrl = "http://test.bbb.com"
		def imageUrl = "http://img.bbb.com"
		RepositoryItemMock attributeRepoItemMock = new RepositoryItemMock()

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration
		siteConfiguration.getPropertyValue(BBBCatalogConstants.SITE_ATTRIBUTE_VALUES_LIST) >> attributeRepoItems
		attributeRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME) >> displayDescription
		attributeRepoItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME) >> actionUrl
		attributeRepoItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME) >> imageUrl

		attributeNameRepoItemMap.put("InitalAttributes",attributeRepoItem)
		
		when :
		
		def siteLevelAttributes = bbbSiteRepoToolsTestObj.getSiteLevelAttributes(SITE_ID_BEDBATH_US, attributeNameRepoItemMap)
		
		then :
		
		siteLevelAttributes == null
		0 * attributeRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME)
		0 * attributeRepoItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME)
		0 * attributeRepoItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME)
	}
	
	def "getSiteLevelAttributes - SITE ID is null" () {

		given :
		
		def siteConfiguration = siteConfigurationMock
		Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<>()
		Set<RepositoryItem> attributeRepoItems = null
		RepositoryItemMock attributeRepoItem = new RepositoryItemMock()
		def displayDescription = "displayDescriptionValue"
		def actionUrl = "http://test.bbb.com"
		def imageUrl = "http://img.bbb.com"
		RepositoryItemMock attributeRepoItemMock = new RepositoryItemMock()

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration
		siteConfiguration.getPropertyValue(BBBCatalogConstants.SITE_ATTRIBUTE_VALUES_LIST) >> attributeRepoItems
		attributeRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME) >> displayDescription
		attributeRepoItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME) >> actionUrl
		attributeRepoItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME) >> imageUrl
		attributeNameRepoItemMap.put("InitalAttributes",attributeRepoItem)
		
		when :
		
		def siteLevelAttributes = bbbSiteRepoToolsTestObj.getSiteLevelAttributes(null, attributeNameRepoItemMap)
		
		then :
		
		siteLevelAttributes.isEmpty() == false
		 0 * attributeRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME)
		 0 * attributeRepoItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME)
		 0 * attributeRepoItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME)

	}

	def "getSiteLevelAttributes - siteAttributeValues mismatch with input attributeRepoItems" () {

		given :
		
		def siteConfiguration = siteConfigurationMock
		Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<>()
		Set<RepositoryItem> attributeRepoItems = new HashSet<>()
		RepositoryItemMock attributeRepoItem = new RepositoryItemMock()
		RepositoryItemMock tempAttributeRepoItem = new RepositoryItemMock()
		def displayDescription = "displayDescriptionValue"
		def actionUrl = "http://test.bbb.com"
		def imageUrl = "http://img.bbb.com"

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration
		siteConfiguration.getPropertyValue(BBBCatalogConstants.SITE_ATTRIBUTE_VALUES_LIST) >> attributeRepoItems
		attributeRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME) >> displayDescription
		attributeRepoItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME) >> actionUrl
		attributeRepoItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME) >> imageUrl

		attributeRepoItems.add(attributeRepoItem)
		attributeNameRepoItemMap.put("InitalAttributes",tempAttributeRepoItem)
		
		when :
		
		def siteLevelAttributes = bbbSiteRepoToolsTestObj.getSiteLevelAttributes(SITE_ID_BEDBATH_US, attributeNameRepoItemMap)
		
		then :
		siteLevelAttributes.isEmpty() == true
		0 * attributeRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME)
		0 * attributeRepoItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME)
		0 * attributeRepoItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME)
	}


	def "getSiteLevelAttributes exception while getting repo item" () {

		given :
		def siteConfiguration = siteConfigurationMock
		Map<String, RepositoryItem> attributeNameRepoItemMap = new HashMap<>()
		Set<RepositoryItem> attributeRepoItems = new HashSet<>()
		RepositoryItemMock attributeRepoItem = new RepositoryItemMock()
		def displayDescription = "displayDescriptionValue"
		def actionUrl = "http://test.bbb.com"
		def imageUrl = "http://img.bbb.com"

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("exception while getting repo item")}
		siteConfiguration.getPropertyValue(BBBCatalogConstants.SITE_ATTRIBUTE_VALUES_LIST) >> attributeRepoItems
		attributeRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME) >> displayDescription
		attributeRepoItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME) >> actionUrl
		attributeRepoItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME) >> imageUrl

		attributeRepoItems.add(attributeRepoItem)
		attributeNameRepoItemMap.put("InitalAttributes",attributeRepoItem)
		when :
		def siteLevelAttributes = bbbSiteRepoToolsTestObj.getSiteLevelAttributes(SITE_ID_BEDBATH_US, attributeNameRepoItemMap)

		then :

		siteLevelAttributes == null
		0 * attributeRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME)
		0 * attributeRepoItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME)
		0 * attributeRepoItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME)
	}

	/* getSiteLevelAttributes - test cases - ENDS */


	/* siteAttributeValues - test cases - STARTS */

	   def "siteAttributeValues - happy flow" () {
		
				given :
				def siteConfiguration   = siteConfigurationMock
				def displayDescription = "displayDescriptionValue"
				def repoItemMockId = "repo01"
		
				Set<RepositoryItem> attributeRepoItems = new HashSet<>()
				RepositoryItemMock attributeRepoItemMock = new RepositoryItemMock(["id":repoItemMockId])
				attributeRepoItemMock.setProperties(["displayDescrip":displayDescription])
				attributeRepoItems.add(attributeRepoItemMock)
		
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration
				siteConfiguration.getPropertyValue(BBBCatalogConstants.SITE_ATTRIBUTE_VALUES_LIST) >> attributeRepoItems
		
				when :
		
				def siteAttributeValues = bbbSiteRepoToolsTestObj.siteAttributeValues(SITE_ID_BEDBATH_US)
		
				then :
		
				siteAttributeValues.get(0).equals(displayDescription)
				
		}
	
	def "siteAttributeValues - siteConfiguration is not set (null)" () {

		given :
		
		def siteConfiguration   = null
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration
		
		when :
		
		def siteAttributeValues = bbbSiteRepoToolsTestObj.siteAttributeValues(SITE_ID_BEDBATH_US)
		
		then :
		
		siteAttributeValues == null
		thrown BBBBusinessException
	}


	def "siteAttributeValues - SITE_ATTRIBUTE_VALUES_LIST not set (null)" () {

		given :
		
		def siteConfiguration   = siteConfigurationMock
		Set<RepositoryItem> attributeRepoItems = null

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration
		siteConfiguration.getPropertyValue(BBBCatalogConstants.SITE_ATTRIBUTE_VALUES_LIST) >> attributeRepoItems

		when :

		def siteAttributeValues = bbbSiteRepoToolsTestObj.siteAttributeValues(SITE_ID_BEDBATH_US)

		then :

		siteAttributeValues == null
	}

	def "siteAttributeValues - SITE_ATTRIBUTE_VALUES_LIST is empty" () {

		given :
		
		def siteConfiguration   = siteConfigurationMock
		Set<RepositoryItem> attributeRepoItems = new HashSet<>()

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration
		siteConfiguration.getPropertyValue(BBBCatalogConstants.SITE_ATTRIBUTE_VALUES_LIST) >> attributeRepoItems

		when :

		def siteAttributeValues = bbbSiteRepoToolsTestObj.siteAttributeValues(SITE_ID_BEDBATH_US)

		then :

		siteAttributeValues == null
	}

	def "siteAttributeValues - Exception while getting repository item" () {

		given :
		def siteConfiguration   = siteConfigurationMock
		def repoItemMockId = "repo01"
		RepositoryItemMock attributeRepoItemMock = new RepositoryItemMock(["id":repoItemMockId])
	
		Set<RepositoryItem> attributeRepoItems = new HashSet<>()
		attributeRepoItems.add(attributeRepoItemMock)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Exception while getting site repo item ")}
		siteConfiguration.getPropertyValue(BBBCatalogConstants.SITE_ATTRIBUTE_VALUES_LIST) >> attributeRepoItems

		when :

		def siteAttributeValues = bbbSiteRepoToolsTestObj.siteAttributeValues(SITE_ID_BEDBATH_US)

		then :

		siteAttributeValues == null
		thrown BBBSystemException
		0 * attributeRepoItemMock.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME)
	}

	/* siteAttributeValues - test cases - ENDS */

	/* getSimilarProductsIgnoreList - test cases - STARTS */
	
	def "getSimilarProductsIgnoreList - happy flow" () {

		given :
		def siteConfiguration   = siteConfigurationMock
		Set<RepositoryItem> similarProuctsIgnoreListInput = new HashSet<>()
		def repoItemMockId = "repo01"
		RepositoryItemMock similarProductIgnoreItem = new RepositoryItemMock(["id":repoItemMockId])
		similarProuctsIgnoreListInput.add(similarProductIgnoreItem)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration
		siteConfiguration.getPropertyValue(BBBCatalogConstants.SIMILAR_PRODUCT_IGNORE_LIST_PROPERTY_NAME) >> similarProuctsIgnoreListInput

		when :
		def similarProductsIgnoreList = bbbSiteRepoToolsTestObj.getSimilarProductsIgnoreList(SITE_ID_BEDBATH_US)

		then :
		similarProductsIgnoreList.contains(repoItemMockId)
		0 * bbbSiteRepoToolsTestObj.logDebug("no rows fetched for given site id")
		0 * bbbSiteRepoToolsTestObj.logDebug("Similar Products Ignore List Repository Items Set is null")
		
	}
	
	def "getSimilarProductsIgnoreList - siteConfiguration is not set(null)" () {

		given :
		def RepositoryItemMock siteConfiguration   = null
		RepositoryItemMock similarProductIgnoreItem = new RepositoryItemMock()
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration

		when :
		def Set<String> similarProductsIgnoreList = bbbSiteRepoToolsTestObj.getSimilarProductsIgnoreList(SITE_ID_BEDBATH_US)

		then :
		similarProductsIgnoreList.isEmpty() == true
	}

	def "getSimilarProductsIgnoreList - SIMILAR_PRODUCT_IGNORE_LIST_PROPERTY_NAME is not set (null)" () {

		given :
		def siteConfiguration   = siteConfigurationMock
		Set<RepositoryItem> similarProuctsIgnoreListInput = null

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfiguration
		siteConfiguration.getPropertyValue(BBBCatalogConstants.SIMILAR_PRODUCT_IGNORE_LIST_PROPERTY_NAME) >> similarProuctsIgnoreListInput

		when :
		def similarProductsIgnoreList = bbbSiteRepoToolsTestObj.getSimilarProductsIgnoreList(SITE_ID_BEDBATH_US)

		then :
		
		similarProductsIgnoreList.isEmpty() == true
	}


	def "getSimilarProductsIgnoreList - Exception while getting repo item" () {

		given :
		def siteConfiguration   = siteConfigurationMock
		Set<RepositoryItem> similarProuctsIgnoreListInput = new HashSet<>()
		RepositoryItemMock similarProductIgnoreItem = new RepositoryItemMock()
		similarProuctsIgnoreListInput.add(similarProductIgnoreItem)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("while getting repo item")}
		siteConfiguration.getPropertyValue(BBBCatalogConstants.SIMILAR_PRODUCT_IGNORE_LIST_PROPERTY_NAME) >> similarProuctsIgnoreListInput

		when :
		def similarProductsIgnoreList = bbbSiteRepoToolsTestObj.getSimilarProductsIgnoreList(SITE_ID_BEDBATH_US)

		then :
		similarProductsIgnoreList.isEmpty() == true
	}

	/* getSimilarProductsIgnoreList - test cases - ENDS */


	/* getBccManagedCategory - test cases - STARTS */

	def "getBccManagedCategory - happy flow" () {
		
				given :
		
				def requestObjectMock = requestMock
				def requestChannel = "Temp Channel"
				Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
				RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
				RepositoryItemMock siteSortOpt = new RepositoryItemMock()
				RepositoryItemMock catSortOpt = new RepositoryItemMock()
				Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
				Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
				Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
				List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
				RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
				RepositoryItemMock categoryPromoId = new RepositoryItemMock()
				RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
				List<RepositoryItem> catSortOptList = new ArrayList<>()
				RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]
				RepositoryItemMock categoryPromoContent = new RepositoryItemMock()
		
				def cssFilePath = "/store/css"
				def jsFilePath = "/store/js"
				def brandContet = "SpockBrand"
				
				//DynamoHttpServletRequest requestObjectMock = Mock()
				
				bbbSiteRepoToolsTestObj = Spy()
				bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		
				siteSortOptList.add(siteSortOpt)
				catSortOptList.add(catSortOpt)
		
				categoryPromoId.setProperties(["brandContent":"SpockBrand","cssFilePath":"/store/css", "jsFilePath":"/store/js"])
				managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
					"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
					"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
					"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView"])
		
				siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":"BBBSort",
					"sortingValue":"SortVal1","sortingOrder":3,
					"sortingUrlParam":"sort?="])
		
				catSortOpt.setProperties(["sortingOptions":catSortOptList])
		
				siteSortOpt.setRepositoryId("siteSortOpt001")
				catSortOpt.setRepositoryId("catSortOpt001")
				defCatSortOpt.setRepositoryId("defCatSortOpt001")
		
				bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails
		
				CategoryVO categoryVoMock = new CategoryVO()
				ServletUtil.getCurrentRequest() >> requestObjectMock
				requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
				ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US
		
				siteSortOptMap.put("SiteSortOption", siteSortOptList)
				siteSortOptMap.put(requestChannel,siteSortOpt)
				siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)
		
		
				defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
				defSiteSortMap.put("DefCategorySortMap", defSiteSortOpt)
				defSiteSortMap.put(requestChannel, defSiteSortOpt)
				defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)
		
				defCatSortMap.put("DefCategorySortMap", defCatSortOpt)
				defCatSortMap.put(requestChannel, defCatSortOpt)
				defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)
		
				catSortOptMap.put("CatSortOPtMap",catSortOptList)
				catSortOptMap.put(requestChannel, catSortOpt)
				catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)
		
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap
		
				when :
		
				bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)
		
				then :
				categoryVoMock.getJsFilePath().equals(jsFilePath)
				categoryVoMock.getCssFilePath().equals(cssFilePath)
				categoryVoMock.getBannerContent().equals(brandContet)
		}
	
	def "getBccManagedCategory - input parameter categoryVo is not set (null)" () {
		
		given : 
		
		CategoryVO categoryVoMock = Mock()
		
		when :

		bbbSiteRepoToolsTestObj.getBccManagedCategory(null);

		then :
		
		0 * ServletUtil.getCurrentRequest()
		thrown BBBBusinessException
	}

	
	def "getBccManagedCategory - sortOption equals default site sort Option (defSiteSortOpt) property" () {

		given :

		def requestObjectMock = requestMock
		def requestChannel = "Temp Channel"
		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
		List<RepositoryItem> catSortOptList = new ArrayList<>()
		RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]
		RepositoryItemMock categoryPromoContent = new RepositoryItemMock()
		SortOptionsVO sortOptionMock = Mock("RepositoryId":"sortOption01")
		
		def defSiteSortCode = "BBBSort"
		def defSiteSortVal = "SortVal1"
		def defSiteSortOrder = 1
		def defSiteSortUrlParam = "sort?="
		
		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)

		siteSortOptList.add(siteSortOpt)
		catSortOptList.add(catSortOpt)

		categoryPromoId.setProperties(["brandContent":"SpockBrand","cssFilePath":"/store/css", "jsFilePath":"/store/js"])
		managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView"])

		defSiteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":defSiteSortCode,
			"sortingValue":defSiteSortVal,"sortingOrder":defSiteSortOrder,
			"sortingUrlParam":"sort?="])

		catSortOpt.setProperties(["sortingOptions":catSortOptList])

		siteSortOpt.setRepositoryId("siteSortOpt001")
		catSortOpt.setRepositoryId("catSortOpt001")
		defCatSortOpt.setRepositoryId("defCatSortOpt001")
		defSiteSortOpt.setRepositoryId("catSortOpt001")

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails

		CategoryVO categoryVoMock = new CategoryVO()
		ServletUtil.getCurrentRequest() >> requestObjectMock
		requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
		ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(requestChannel,siteSortOpt)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)


		defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
		defSiteSortMap.put("DefCategorySortMap", defSiteSortOpt)
		defSiteSortMap.put(requestChannel, defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)

		defCatSortMap.put("DefCategorySortMap", defCatSortOpt)
		defCatSortMap.put(requestChannel, defCatSortOpt)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(requestChannel, catSortOpt)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap

		when :

		bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)

		then :
		categoryVoMock.getSortOptionVO().getDefaultSortingOption().getSortCode().equals(defSiteSortCode)
		categoryVoMock.getSortOptionVO().getDefaultSortingOption().getSortValue().equals(defSiteSortVal)
		categoryVoMock.getSortOptionVO().getDefaultSortingOption().getSortUrlParam().equals(defSiteSortUrlParam)
	}

	def "getBccManagedCategory - sortOption is defined in list (inDefInList property - true)" () {

		given :
		
		def requestObjectMock = requestMock
		def requestChannel = "Temp Channel"
		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
		List<RepositoryItem> catSortOptList = new ArrayList<>()
		RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]
		
		RepositoryItemMock categoryPromoContent = new RepositoryItemMock()

		def defSiteSortCode = "BBBSort"
		def defSiteSortVal = "SortVal1"
		def defSiteSortOrder = 1
		def defSiteSortUrlParam = "sort?="
		
		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)

		siteSortOptList.add(siteSortOpt)
		catSortOptList.add(catSortOpt)

		categoryPromoId.setProperties(["brandContent":"SpockBrand","cssFilePath":"/store/css", "jsFilePath":"/store/js"])
		managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView"])

		siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":defSiteSortCode,
			"sortingValue":defSiteSortVal,"sortingOrder":defSiteSortOrder,
			"sortingUrlParam":defSiteSortUrlParam])

		catSortOpt.setProperties(["sortingOptions":catSortOptList,"sortingCode":defSiteSortCode,
			"sortingValue":defSiteSortVal,"sortingOrder":defSiteSortOrder,
			"sortingUrlParam":defSiteSortUrlParam])
		
		siteSortOpt.setRepositoryId("siteSortOpt001")
		catSortOpt.setRepositoryId("defCatSortOpt001")
		defCatSortOpt.setRepositoryId("defCatSortOpt001")
		defSiteSortOpt.setRepositoryId("catSortOpt001")

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails

		CategoryVO categoryVoMock = new CategoryVO()
		ServletUtil.getCurrentRequest() >> requestObjectMock
		requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
		ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(requestChannel,siteSortOpt)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)

		defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
		defSiteSortMap.put("DefCategorySortMap", defSiteSortOpt)
		defSiteSortMap.put(requestChannel, defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)

		defCatSortMap.put("DefCategorySortMap", defCatSortOpt)
		defCatSortMap.put(requestChannel, defCatSortOpt)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(requestChannel, catSortOpt)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap

		when :

		bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)

		then :
		
		categoryVoMock.getSortOptionVO().getSortingOptions().get(0).getSortCode().equals(defSiteSortCode)
		categoryVoMock.getSortOptionVO().getSortingOptions().get(0).getSortValue().equals(defSiteSortVal)
		categoryVoMock.getSortOptionVO().getSortingOptions().get(0).getSortUrlParam().equals(defSiteSortUrlParam)
		
	}

	
	def "getBccManagedCategory - categoryPromoContent PROMO_CONTENT property null" () {

		given :

		def requestObjectMock = requestMock
		def requestChannel = "Temp Channel"
		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		RepositoryItemMock bbbManagedCategoryDetailsItem = new RepositoryItemMock()
		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
		List<RepositoryItem> catSortOptList = new ArrayList<>()
		RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]

		RepositoryItemMock categoryPromoContent = new RepositoryItemMock()
		def promoContent = null

		def cssFilePath = "/store/css"
		def jsFilePath = "/store/js"
		
		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)

		siteSortOptList.add(siteSortOpt)
		catSortOptList.add(catSortOpt)

		categoryPromoId.setProperties(["brandContent":promoContent,"cssFilePath":"/store/css", "jsFilePath":"/store/js"])
		managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView"])


		siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":"BBBSort",
			"sortingValue":"SortVal1","sortingOrder":3,
			"sortingUrlParam":"sort?="])

		catSortOpt.setProperties(["sortingOptions":catSortOptList])

		siteSortOpt.setRepositoryId("siteSortOpt001")
		catSortOpt.setRepositoryId("catSortOpt001")
		defCatSortOpt.setRepositoryId("defCatSortOpt001")

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails

		CategoryVO categoryVoMock = Mock()
		ServletUtil.getCurrentRequest() >> requestObjectMock
		requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
		ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(requestChannel,siteSortOpt)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)


		defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
		defSiteSortMap.put("DefCategorySortMap", defSiteSortOpt)
		defSiteSortMap.put(requestChannel, defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)

		defCatSortMap.put("DefCategorySortMap", defCatSortOpt)
		defCatSortMap.put(requestChannel, defCatSortOpt)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(requestChannel, catSortOpt)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap

		when :

		bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)

		then :
		
		categoryVoMock.getCssFilePath() == null
		categoryVoMock.getJsFilePath() == null
		categoryVoMock.getBannerContent() == null
	}


	def "getBccManagedCategory - isChatEnable is not set (null)" () {

		given :

		def requestObjectMock = requestMock
		def requestChannel = "Temp Channel"
		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
		List<RepositoryItem> catSortOptList = new ArrayList<>()
		RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]

		RepositoryItemMock categoryPromoContent = new RepositoryItemMock()

		def chatEnabled = null

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)

		siteSortOptList.add(siteSortOpt)
		catSortOptList.add(catSortOpt)

		categoryPromoId.setProperties(["brandContent":"SpockBrand","cssFilePath":"/store/css", "jsFilePath":"/store/js"])
		managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":chatEnabled,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView"])

		siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":"BBBSort",
			"sortingValue":"SortVal1","sortingOrder":3,
			"sortingUrlParam":"sort?="])

		catSortOpt.setProperties(["sortingOptions":catSortOptList])

		siteSortOpt.setRepositoryId("siteSortOpt001")
		catSortOpt.setRepositoryId("catSortOpt001")
		defCatSortOpt.setRepositoryId("defCatSortOpt001")

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails

		CategoryVO categoryVoMock = Mock()
		ServletUtil.getCurrentRequest() >> requestObjectMock
		requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
		ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(requestChannel,siteSortOpt)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)

		defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
		defSiteSortMap.put("DefCategorySortMap", defSiteSortOpt)
		defSiteSortMap.put(requestChannel, defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)

		defCatSortMap.put("DefCategorySortMap", defCatSortOpt)
		defCatSortMap.put(requestChannel, defCatSortOpt)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(requestChannel, catSortOpt)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap

		when :

		bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)

		then :
		
		categoryVoMock.getChatEnabled() == null
		categoryVoMock.getChatURL() == null
		categoryVoMock.getChatCode() == null
	}

	def "getBccManagedCategory - displayAskAndAnswer, zoomValue, defaultViewValue are not set (null)" () {

		given :

		def requestObjectMock = requestMock
		def requestChannel = "Temp Channel"
		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
		List<RepositoryItem> catSortOptList = new ArrayList<>()
		RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]

		RepositoryItemMock categoryPromoContent = new RepositoryItemMock()

		def displayAskAndAnswer = null
		def zoomValue = null
		def defaultViewValue = null

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)

		siteSortOptList.add(siteSortOpt)
		catSortOptList.add(catSortOpt)

		categoryPromoId.setProperties(["brandContent":"SpockBrand","cssFilePath":"/store/css", "jsFilePath":"/store/js"])
		managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":displayAskAndAnswer, "zoomValue" :zoomValue,"defaultViewValue":defaultViewValue])

		siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":"BBBSort",
			"sortingValue":"SortVal1","sortingOrder":3,
			"sortingUrlParam":"sort?="])

		catSortOpt.setProperties(["sortingOptions":catSortOptList])

		siteSortOpt.setRepositoryId("siteSortOpt001")
		catSortOpt.setRepositoryId("catSortOpt001")
		defCatSortOpt.setRepositoryId("defCatSortOpt001")

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails

		CategoryVO categoryVoMock = Mock()
		ServletUtil.getCurrentRequest() >> requestObjectMock
		requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
		ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(requestChannel,siteSortOpt)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)


		defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
		defSiteSortMap.put("DefCategorySortMap", defSiteSortOpt)
		defSiteSortMap.put(requestChannel, defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)

		defCatSortMap.put("DefCategorySortMap", defCatSortOpt)
		defCatSortMap.put(requestChannel, defCatSortOpt)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(requestChannel, catSortOpt)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap

		when :

		bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)

		then :
		
		categoryVoMock.getDisplayAskAndAnswer() == null
		categoryVoMock.getZoomValue() == null
		categoryVoMock.getDefaultViewValue() == null
	}
	
	def "getBccManagedCategory - defSiteSortMap, siteSortOptMap and defCatSortMap doesn't have channel attribute" () {

		given :

		def requestObjectMock = requestMock
		def requestChannel = "Temp Channel"
		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
		List<RepositoryItem> catSortOptList = new ArrayList<>()
		RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]

		RepositoryItemMock categoryPromoContent = new RepositoryItemMock()

		def chatLinkPlaceHolder = "Enter you chat link here"
		
		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)

		siteSortOptList.add(siteSortOpt)
		catSortOptList.add(catSortOpt)

		categoryPromoId.setProperties(["brandContent":"SpockBrand","cssFilePath":"/store/css", "jsFilePath":"/store/js"])
		managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":chatLinkPlaceHolder,
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView"])

		siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":"BBBSort",
			"sortingValue":"SortVal1","sortingOrder":3,
			"sortingUrlParam":"sort?="])

		catSortOpt.setProperties(["sortingOptions":catSortOptList])

		siteSortOpt.setRepositoryId("siteSortOpt001")
		catSortOpt.setRepositoryId("catSortOpt001")
		defCatSortOpt.setRepositoryId("defCatSortOpt001")

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails

		CategoryVO categoryVoMock = Mock()
		ServletUtil.getCurrentRequest() >> requestObjectMock
		requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
		ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)

		defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
		defSiteSortMap.put("DefCategorySortMap", defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)

		defCatSortMap.put("DefCategorySortMap", defCatSortOpt)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap

		when :

		bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)

		then :
		categoryVoMock.getChatLinkPlaceholder() == null
	}
	
	def "getBccManagedCategory - bbbManagedCategoryDetails array with first entry null" () {
		
				given :
		
				def requestObjectMock = requestMock
				def requestChannel = "Temp Channel"
				Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
				RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
				RepositoryItemMock siteSortOpt = new RepositoryItemMock()
				RepositoryItemMock catSortOpt = new RepositoryItemMock()
				Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
				Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
				Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
				List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
				RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
				RepositoryItemMock categoryPromoId = new RepositoryItemMock()
				RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
				List<RepositoryItem> catSortOptList = new ArrayList<>()
				RepositoryItem[] bbbManagedCategoryDetails = [null]
				//RepositoryItemMock bbbManagedCategoryDetailsItem = new RepositoryItemMock()
				RepositoryItemMock categoryPromoContent = new RepositoryItemMock()
		
				def sortingCode = "BBB"
				def sortingValue = "SortVal"
				def sortingUrlParam = "sort?="
				def defBrandSortOrder = "ascending"
				def defSiteSortOrder = "descending"
				def brandPromoSetSortOrder = "ascending"
				def siteDefSortOptSortOder = "descending"
				
				CategoryVO categoryVoMock = new CategoryVO()
				
				bbbSiteRepoToolsTestObj = Spy()
				bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		
				siteSortOptList.add(siteSortOpt)
				catSortOptList.add(catSortOpt)
		
				categoryPromoId.setProperties(["brandContent":"SpockBrand","cssFilePath":"/store/css", "jsFilePath":"/store/js"])
				managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
					"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
					"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
					"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView"])
		
				siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":sortingCode,
					"sortingValue":sortingValue,"sortingOrder":3,
					"sortingUrlParam":sortingUrlParam])
		
				defCatSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":sortingCode,
					"sortingValue":sortingValue,"sortingOrder":3,
					"sortingUrlParam":sortingUrlParam])
				
				catSortOpt.setProperties(["sortingOptions":catSortOptList])
		
				siteSortOpt.setRepositoryId("siteSortOpt001")
				catSortOpt.setRepositoryId("catSortOpt001")
				defCatSortOpt.setRepositoryId("defCatSortOpt001")
		
				bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails
		
		
				ServletUtil.getCurrentRequest() >> requestObjectMock
				requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
				ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US
		
				siteSortOptMap.put("SiteSortOption", siteSortOptList)
				siteSortOptMap.put(requestChannel,siteSortOpt)
				siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)
		
		
				defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
				defSiteSortMap.put("DefCategorySortMap", defSiteSortOpt)
				defSiteSortMap.put(requestChannel, defSiteSortOpt)
				defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)
		
				defCatSortMap.put("DefCategorySortMap", defCatSortOpt)
				defCatSortMap.put(requestChannel, defCatSortOpt)
				defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)
		
				catSortOptMap.put("CatSortOPtMap",catSortOptList)
				catSortOptMap.put(requestChannel, catSortOpt)
				catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)
		
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap
		
				when :
		
				bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)
		
				then :
				
				categoryVoMock.getSortOptionVO().getSortingOptions().get(0).getSortValue().equals(sortingValue)
				categoryVoMock.getSortOptionVO().getSortingOptions().get(0).getSortUrlParam().equals(sortingUrlParam)
			}
		
	def "getBccManagedCategory - managedCategoryItem is not set (null)" () {

		given :

		def requestObjectMock = requestMock
		def requestChannel = "Temp Channel"
		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
		List<RepositoryItem> catSortOptList = new ArrayList<>()
		RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]

		RepositoryItemMock categoryPromoContent = new RepositoryItemMock()
		CategoryVO categoryVoMock = new CategoryVO()
		
		def siteSortCode = "BBBSort"
		def siteSortVal = "SortVal1"
		def siteSortOrder = 1
		def siteSortUrlParam = "sort?="
		
		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)

		siteSortOptList.add(siteSortOpt)
		catSortOptList.add(catSortOpt)

		categoryPromoId.setProperties(["brandContent":"SpockBrand","cssFilePath":"/store/css", "jsFilePath":"/store/js"])
		
		siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":siteSortCode,
			"sortingValue":siteSortVal,"sortingOrder":siteSortOrder,
			"sortingUrlParam":siteSortUrlParam])

		catSortOpt.setProperties(["sortingOptions":catSortOptList])

		siteSortOpt.setRepositoryId("siteSortOpt001")
		catSortOpt.setRepositoryId("catSortOpt001")
		defCatSortOpt.setRepositoryId("defCatSortOpt001")

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> null

		
		ServletUtil.getCurrentRequest() >> requestObjectMock
		requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
		ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(requestChannel,siteSortOpt)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)

		defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
		defSiteSortMap.put("DesktopWeb", defSiteSortOpt)
		defSiteSortMap.put(requestChannel, defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)

		defCatSortMap.put("DefCategorySortMap", null)
		defCatSortMap.put(requestChannel, defCatSortOpt)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(requestChannel, catSortOpt)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap

		when :

		bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)

		then :
				categoryVoMock.getSortOptionVO().getSortingOptions().get(0).getSortValue().equals(siteSortVal)
				categoryVoMock.getSortOptionVO().getSortingOptions().get(0).getSortCode().equals(siteSortCode)
				categoryVoMock.getSortOptionVO().getSortingOptions().get(0).getSortUrlParam().equals(siteSortUrlParam)
	}

	def "getBccManagedCategory - default site sort option (defSiteSortOpt) and site sort option list (siteSortOptList) are not set (null)" () {

		given :

		def requestObjectMock = requestMock
		def requestChannel = "Temp Channel"
		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		RepositoryItemMock defSiteSortOpt = null
		RepositoryItemMock siteSortItem = null
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		List<RepositoryItemMock> siteSortOptList = null
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = null
		List<RepositoryItem> catSortOptList = new ArrayList<>()
		RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]
		RepositoryItemMock categoryPromoContent = new RepositoryItemMock()
		SortOptionsVO sortOptionsVoMock = Mock() 
		
		def siteSortCode = "BBBSort"
		def siteSortVal = "SortVal1"
		def siteSortOrder = 1
		def siteSortUrlParam = "sort?="
				
		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)

		catSortOptList.add(catSortOpt)
		categoryPromoId.setProperties(["brandContent":"SpockBrand","cssFilePath":"/store/css", "jsFilePath":"/store/js"])
		siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":siteSortCode,
			"sortingValue":siteSortVal,"sortingOrder":siteSortOrder,
			"sortingUrlParam":siteSortUrlParam])
		catSortOpt.setProperties(["sortingOptions":catSortOptList,"sortingCode":siteSortCode,
			"sortingValue":siteSortVal,"sortingOrder":siteSortOrder,
			"sortingUrlParam":siteSortUrlParam])
		
		managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView"])
		
		siteSortOpt.setRepositoryId("siteSortOpt001")
		catSortOpt.setRepositoryId("catSortOpt001")
		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails

		CategoryVO categoryVoMock = new CategoryVO()
		ServletUtil.getCurrentRequest() >> requestObjectMock
		requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
		ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(requestChannel,siteSortOpt)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)


		defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
		defSiteSortMap.put("DesktopWeb", defSiteSortOpt)
		defSiteSortMap.put(requestChannel, defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, null)

		defCatSortMap.put("DefCategorySortMap", null)
		defCatSortMap.put(requestChannel, defCatSortOpt)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(requestChannel, catSortOpt)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap

		when :

		 bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)

		then :
		
		categoryVoMock.getSortOptionVO().getDefaultSortingOption().getSortValue().equals(siteSortVal)
		categoryVoMock.getSortOptionVO().getDefaultSortingOption().getSortCode().equals(siteSortCode)
		categoryVoMock.getSortOptionVO().getDefaultSortingOption().getSortUrlParam().equals(siteSortUrlParam)
	}


	def "getBccManagedCategory - default category sort option (defCatSortOpt) and category sort option list (catSortOptList) are not set(null)" () {

		given :

		def requestObjectMock = requestMock
		def requestChannel = "Temp Channel"
		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = null
		RepositoryItemMock catSortOptItem = null
		List<RepositoryItem> catSortOptList = null
		RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]
		SortOptionsVO sortOptionsVoMock = Mock()
		RepositoryItemMock categoryPromoContent = new RepositoryItemMock()
		
		def siteSortCode = "BBBSort"
		def siteSortVal = "SortVal1"
		def siteSortOrder = 1
		def siteSortUrlParam = "sort?="

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)

		siteSortOptList.add(siteSortOpt)

		categoryPromoId.setProperties(["brandContent":"SpockBrand","cssFilePath":"/store/css", "jsFilePath":"/store/js"])
		managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView"])


		siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":siteSortCode,
			"sortingValue":siteSortVal,"sortingOrder":siteSortOrder,
			"sortingUrlParam":siteSortUrlParam])

		catSortOpt.setProperties(["sortingOptions":catSortOptList])

		defSiteSortOpt.setRepositoryId("catSortOpt001")
		siteSortOpt.setRepositoryId("siteSortOpt001")
		catSortOpt.setRepositoryId("catSortOpt001")
		defSiteSortOpt.getRepositoryId()>>"catSortOpt001"


		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails

		CategoryVO categoryVoMock = new CategoryVO()
		ServletUtil.getCurrentRequest() >> requestObjectMock
		requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
		ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(requestChannel,siteSortOpt)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)


		defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
		defSiteSortMap.put("DefCategorySortMap", defSiteSortOpt)
		defSiteSortMap.put(requestChannel, defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)

		defCatSortMap.put("DefCategorySortMap", defCatSortOpt)
		defCatSortMap.put(requestChannel, defCatSortOpt)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(requestChannel, catSortOpt)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap

		when :

		 bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)

		then :
		
		categoryVoMock.getSortOptionVO().getSortingOptions().get(0).getSortValue().equals(siteSortVal)
		categoryVoMock.getSortOptionVO().getSortingOptions().get(0).getSortCode().equals(siteSortCode)
		categoryVoMock.getSortOptionVO().getSortingOptions().get(0).getSortUrlParam().equals(siteSortUrlParam)
	}

	def "getBccManagedCategory - defaultCategorySortOption(defCatSortOpt), defaultSiteSortOption(defSiteSortOpt), categorySortOptionList(catSortOptList) and siteSortOptionList(siteSortOptList) are not set (null)" () {

		given :

		def requestObjectMock = requestMock
		def requestChannel = "Temp Channel"
		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		RepositoryItemMock defSiteSortOpt = null
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		List<RepositoryItemMock> siteSortOptList = null
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = null
		RepositoryItemMock catSortOptItem = null
		List<RepositoryItem> catSortOptList = null
		RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]
		SortOptionsVO sortOptionsVoMock = Mock()

		RepositoryItemMock categoryPromoContent = new RepositoryItemMock()

		def siteSortCode = "BBBSort"
		def siteSortVal = "SortVal1"
		def siteSortOrder = 1
		def siteSortUrlParam = "sort?="
		
		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		categoryPromoId.setProperties(["brandContent":"SpockBrand","cssFilePath":"/store/css", "jsFilePath":"/store/js"])
		managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView"])

		siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":siteSortCode,
			"sortingValue":siteSortVal,"sortingOrder":siteSortOrder,
			"sortingUrlParam":siteSortUrlParam])

		catSortOpt.setProperties(["sortingOptions":catSortOptList])
		siteSortOpt.setRepositoryId("siteSortOpt001")
		catSortOpt.setRepositoryId("catSortOpt001")

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails

		CategoryVO categoryVoMock = new CategoryVO()
		ServletUtil.getCurrentRequest() >> requestObjectMock
		requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
		ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(requestChannel,siteSortOpt)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)

		defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
		defSiteSortMap.put("DefCategorySortMap", defSiteSortOpt)
		defSiteSortMap.put(requestChannel, defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)

		defCatSortMap.put("DefCategorySortMap", defCatSortOpt)
		defCatSortMap.put(requestChannel, defCatSortOpt)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(requestChannel, catSortOpt)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap

		when :

		 bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)

		then :

				categoryVoMock.getSortOptionVO().getDefaultSortingOption().getSortValue() == null
				categoryVoMock.getSortOptionVO().getDefaultSortingOption().getSortCode() == null
				categoryVoMock.getSortOptionVO().getDefaultSortingOption().getSortUrlParam() == null
	}

	def "getBccManagedCategory - siteID and channel is emtpy" () {

		given :

		def requestObjectMock = requestMock
		def requestChannel = ""
		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		
		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
		List<RepositoryItem> catSortOptList = new ArrayList<>()
		RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]

		RepositoryItemMock categoryPromoContent = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)


		siteSortOptList.add(siteSortOpt)
		catSortOptList.add(catSortOpt)

		categoryPromoId.setProperties(["brandContent":"SpockBrand","cssFilePath":"/store/css", "jsFilePath":"/store/js"])
		managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView"])


		siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":"BBBSort",
			"sortingValue":"SortVal1","sortingOrder":3,
			"sortingUrlParam":"sort?="])

		catSortOpt.setProperties(["sortingOptions":catSortOptList])

		siteSortOpt.setRepositoryId("siteSortOpt001")
		catSortOpt.setRepositoryId("catSortOpt001")
		defCatSortOpt.setRepositoryId("defCatSortOpt001")

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		CategoryVO categoryVoMock = new CategoryVO()
		ServletUtil.getCurrentRequest() >> requestObjectMock
		requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
		ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> ""

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(requestChannel,siteSortOpt)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)

		defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
		defSiteSortMap.put("DefCategorySortMap", defSiteSortOpt)
		defSiteSortMap.put(requestChannel, defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)

		defCatSortMap.put("DefCategorySortMap", defCatSortOpt)
		defCatSortMap.put(requestChannel, defCatSortOpt)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(requestChannel, catSortOpt)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)


		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap

		when :

		 bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)

		then :
		
		catSortOptMap.get("CatSortOPtMap") == catSortOptList		
		1 * bbbSiteRepoToolsTestObj.getCurrentSiteId()
		requestObjectMock.getHeader(BBBCoreConstants.CHANNEL).isEmpty()

	}


	def "getBccManagedCategory - defaultSiteSortMap (defSiteSortMap), siteSortOptionMap (siteSortOptMap), defaultCategorySortMap (defCatSortMap) are not set(null)" () {

		given :
		def requestObjectMock = requestMock
		def requestChannel = "Temp Channel"

		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		Map<String, RepositoryItem> catSortOptMap = null
		Map<String, List<RepositoryItem>> siteSortOptMap = null
		Map<String, RepositoryItem>  defCatSortMap = null
		Map<String, RepositoryItem> defSiteSortMap = null
		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
		List<RepositoryItem> catSortOptList = new ArrayList<>()
		RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]

		RepositoryItemMock categoryPromoContent = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)


		siteSortOptList.add(siteSortOpt)
		catSortOptList.add(catSortOpt)

		categoryPromoId.setProperties(["brandContent":"SpockBrand","cssFilePath":"/store/css", "jsFilePath":"/store/js"])
		managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView"])

		siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":"BBBSort",
			"sortingValue":"SortVal1","sortingOrder":3,
			"sortingUrlParam":"sort?="])

		catSortOpt.setProperties(["sortingOptions":catSortOptList])

		siteSortOpt.setRepositoryId("siteSortOpt001")
		catSortOpt.setRepositoryId("catSortOpt001")
		defCatSortOpt.setRepositoryId("defCatSortOpt001")

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails

		CategoryVO categoryVoMock = Mock()
		ServletUtil.getCurrentRequest() >> requestObjectMock
		requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
		ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap

		when :

		 bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)

		then :

		1 * bbbSiteRepoToolsTestObj.logError("Catalog API Method Name[getBccManagedCategory]"+ ":Default Site Sort Option Map Null")
		1 * bbbSiteRepoToolsTestObj.logError("Catalog API Method Name [getBccManagedCategory]"+ ":Site Sort Options Map Null")
	}
	
	
	def "getBccManagedCategory - isChatEnabled false and category promo content (CATEGORY_PROMO_ID) is not set (null)" () {
		
				given :
		
				def requestObjectMock = requestMock
				def requestChannel = "Temp Channel"
				Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
				RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
				RepositoryItemMock siteSortOpt = new RepositoryItemMock()
				RepositoryItemMock catSortOpt = new RepositoryItemMock()
				Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
				Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
				Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
				List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
				RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
				RepositoryItemMock categoryPromoId = null
				RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
				List<RepositoryItem> catSortOptList = new ArrayList<>()
				RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]
		
				RepositoryItemMock categoryPromoContent = new RepositoryItemMock()
		
				bbbSiteRepoToolsTestObj = Spy()
				bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		
				siteSortOptList.add(siteSortOpt)
				catSortOptList.add(catSortOpt)
		
				managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":false,"chatURL":"chat.bbb.com",
					"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
					"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
					"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView"])
		
				siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":"BBBSort",
					"sortingValue":"SortVal1","sortingOrder":3,
					"sortingUrlParam":"sort?="])
		
				catSortOpt.setProperties(["sortingOptions":catSortOptList])
		
				siteSortOpt.setRepositoryId("siteSortOpt001")
				catSortOpt.setRepositoryId("catSortOpt001")
				defCatSortOpt.setRepositoryId("defCatSortOpt001")
		
				bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails
		
				CategoryVO categoryVoMock = new CategoryVO()
				ServletUtil.getCurrentRequest() >> requestObjectMock
				requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
				ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US
		
				siteSortOptMap.put("SiteSortOption", siteSortOptList)
				siteSortOptMap.put(requestChannel,siteSortOpt)
				siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)
		
				defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
				defSiteSortMap.put("DefCategorySortMap", defSiteSortOpt)
				defSiteSortMap.put(requestChannel, defSiteSortOpt)
				defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)
		
				defCatSortMap.put("DefCategorySortMap", defCatSortOpt)
				defCatSortMap.put(requestChannel, defCatSortOpt)
				defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)
		
				catSortOptMap.put("CatSortOPtMap",catSortOptList)
				catSortOptMap.put(requestChannel, catSortOpt)
				catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)
		
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteConfigurationMock
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
				siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap
		
				when :
		
				 bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)
		
				then :

						categoryVoMock.getChatEnabled() == false
						1 * bbbSiteRepoToolsTestObj.logTrace("Category Banner Content is not available for category id: "+ categoryVoMock.getCategoryId())
		
			}
		
	def "getBccManagedCategory - Exception while getting repository item" () {

		given :

		def requestObjectMock = requestMock
		def requestChannel = "Temp Channel"
		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
		List<RepositoryItem> catSortOptList = new ArrayList<>()
		RepositoryItem[] bbbManagedCategoryDetails = [managedCategoryDetailItem]

		RepositoryItemMock categoryPromoContent = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)

		siteSortOptList.add(siteSortOpt)
		catSortOptList.add(catSortOpt)

		categoryPromoId.setProperties(["brandContent":"SpockBrand","cssFilePath":"/store/css", "jsFilePath":"/store/js"])
		managedCategoryDetailItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView"])

		siteSortOpt.setProperties(["sortingOptions":siteSortOptList,"sortingCode":"BBBSort",
			"sortingValue":"SortVal1","sortingOrder":3,
			"sortingUrlParam":"sort?="])

		catSortOpt.setProperties(["sortingOptions":catSortOptList])

		siteSortOpt.setRepositoryId("siteSortOpt001")
		catSortOpt.setRepositoryId("catSortOpt001")
		defCatSortOpt.setRepositoryId("defCatSortOpt001")

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bbbManagedCategoryDetails

		CategoryVO categoryVoMock = Mock()
		ServletUtil.getCurrentRequest() >> requestObjectMock
		requestObjectMock.getHeader(BBBCoreConstants.CHANNEL) >> requestChannel
		ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID) >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(requestChannel,siteSortOpt)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)

		defSiteSortMap.put("DefSiteSortMap", siteSortOptList)
		defSiteSortMap.put("DefCategorySortMap", defSiteSortOpt)
		defSiteSortMap.put(requestChannel, defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)

		defCatSortMap.put("DefCategorySortMap", defCatSortOpt)
		defCatSortMap.put(requestChannel, defCatSortOpt)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(requestChannel, catSortOpt)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> {throw new RepositoryException("Exception while getting site repo item")}
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION) >> defSiteSortMap
		siteConfigurationMock.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST) >> siteSortOptMap

		when :

		 bbbSiteRepoToolsTestObj.getBccManagedCategory(categoryVoMock)

		then :
		
		1 * bbbSiteRepoToolsTestObj.logError("Catalog API Method Name [getBccManagedCategory]:RepositoryException", _)
		thrown BBBSystemException
	}


	/* getBccManagedCategory - test cases - ENDS */

	/* getBccManagedBrand - test cases - STARTS */
	
	def "getBccManagedBrand - happy flow" () {
		
				given :
				
				RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()
		
				bbbSiteRepoToolsTestObj = Spy()
				bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
				bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)
		
				def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
				def RepositoryItem[] bccManagedBrandRepositoryItem = [brandRepositoryItem]
		
				RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
				RepositoryItemMock siteSortOpt = new RepositoryItemMock()
				RepositoryItemMock catSortOpt = new RepositoryItemMock()
				RepositoryItemMock siteDefSortOpt = new RepositoryItemMock()
				RepositoryItemMock siteSortOptItem = new RepositoryItemMock()
				RepositoryItemMock defBrandSortOpt = new RepositoryItemMock()
				RepositoryItemMock brandSortOptItem = new RepositoryItemMock()
				RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
				RepositoryItemMock categoryPromoId = new RepositoryItemMock()
				RepositoryItemMock brandPromoSet = new RepositoryItemMock()
				RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
		
				Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
				Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
				Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
				Map<String, RepositoryItem> defBrandSortMap  = new HashMap<>()
				Map<String, RepositoryItem> BrandSortOptMap = new HashMap<>()
		
		
				List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
				List<RepositoryItem> catSortOptList = new ArrayList<>()
		
				Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
				SortOptionsVO sortOptionsVoMock = Mock()
				SortOptionVO sortOptionsMock = Mock()
				bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bccManagedBrandRepositoryItem
				bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		
				siteSortOptMap.put("SiteSortOption", siteSortOptList)
				siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)
		
				siteSortOptItem.setProperties(["sortingOptions":siteSortOptList])
				defBrandSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
				defSiteSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
				brandPromoSet.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
				siteDefSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		
				defSiteSortMap.put("DesktopWeb", defSiteSortOpt)
				defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, null)
		
				defCatSortMap.put("DefCategorySortMap", null)
				defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)
		
				catSortOptMap.put("CatSortOPtMap",catSortOptList)
				catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)
		
				defBrandSortMap.put("DefBranSortMap", defBrandSortOpt)
				defBrandSortMap.put("DesktopWeb", defBrandSortOpt)
		
				siteSortOptMap.put("DesktopWeb", siteDefSortOpt)
		
		
				brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
					"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
					"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
					"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
					"defBrandSortOption":defBrandSortMap,"brandSortOptList":defBrandSortMap,"brandPromoId":brandPromoSet])
		
				brandPromoSet.setProperties(["brandContent":"BedBathBeyond","cssFilePath":"/store/css","jsFilePath":"/store/js"])
		
				bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock
				siteRepoItemMock.setProperties(["defSiteSortOption":defSiteSortMap,"siteSortOptionList":siteSortOptMap])
		
				when :
		
				def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","DesktopWeb");
		
				then :
				
				bccManagedBrand.getBrandContent().equals("BedBathBeyond")
				bccManagedBrand.getCssFilePath().equals("/store/css")
				bccManagedBrand.getJsFilePath().equals("/store/js")
			}
		
	def "getBccManagedBrand input parameter brand is empty" () {

		when :

		def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("","DesktopWeb");

		then :
		
		bccManagedBrand == null
		thrown BBBBusinessException
	}

	def "getBccManagedBrand - input parameter Channel is empty" () {

		when :

		def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","");

		then :
		bccManagedBrand == null
		thrown BBBSystemException
	}

	def "getBccManagedBrand - managedBrandRepositoryItem is not set (null)" () {

		given :

		RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)

		def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] bccManagedBrandRepositoryItem = null
		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteDefSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOptItem = new RepositoryItemMock()
		RepositoryItemMock defBrandSortOpt = new RepositoryItemMock()
		RepositoryItemMock brandSortOptItem = new RepositoryItemMock()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()

		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock brandPromoSet = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()

		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		Map<String, RepositoryItem> defBrandSortMap  = new HashMap<>()
		Map<String, RepositoryItem> BrandSortOptMap = new HashMap<>()


		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		List<RepositoryItem> catSortOptList = new ArrayList<>()

		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bccManagedBrandRepositoryItem
		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)

		siteSortOptItem.setProperties(["sortingOptions":siteSortOptList])
		defBrandSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		defSiteSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		brandPromoSet.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		siteDefSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])

		defSiteSortMap.put("DesktopWeb", defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, null)

		defCatSortMap.put("DefCategorySortMap", null)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		defBrandSortMap.put("DefBranSortMap", defBrandSortOpt)
		defBrandSortMap.put("DesktopWeb", defBrandSortOpt)

		siteSortOptMap.put("DesktopWeb", siteDefSortOpt)


		brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
			"defBrandSortOption":defBrandSortMap,"brandSortOptList":defBrandSortMap,"brandPromoId":brandPromoSet])

		brandPromoSet.setProperties(["brandContent":"BedBathBeyond","cssFilePath":"/store/css","jsFilePath":"/store/js"])

		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock
		siteRepoItemMock.setProperties(["defSiteSortOption":defSiteSortMap,"siteSortOptionList":siteSortOptMap])

		when :

		def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","DesktopWeb");

		then :
		
		bccManagedBrand.getBrandContent() == null
		bccManagedBrand.getCssFilePath() == null
		bccManagedBrand.getJsFilePath() == null

	}


	def "getBccManagedBrand - defaultBrandSortOption (defBrandSortOpt) is not set (null)" () {

		given :

		RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)

		def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] bccManagedBrandRepositoryItem = [brandRepositoryItem]

		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteDefSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOptItem = new RepositoryItemMock()
		RepositoryItemMock defBrandSortOpt = null
		RepositoryItemMock brandSortOptItem = new RepositoryItemMock()
		RepositoryItemMock tempBrandSortOptItem = new RepositoryItemMock()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock brandPromoSet = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()

		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		Map<String, RepositoryItem> defBrandSortMap  = new HashMap<>()
		Map<String, RepositoryItem> brandSortOptMap = new HashMap<>()


		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		List<RepositoryItem> catSortOptList = new ArrayList<>()
		List<RepositoryItem> brandSortOptList = new ArrayList<>()

		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		SortOptionsVO sortOptionsVoMock = Mock()
		SortOptionVO sortOptionVoMock = Mock()

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bccManagedBrandRepositoryItem
		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		
		brandSortOptList.add(tempBrandSortOptItem)

		siteSortOptItem.setProperties(["sortingOptions":siteSortOptList])
		brandSortOptItem.setProperties(["sortingOptions":brandSortOptList])

		defSiteSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		brandPromoSet.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		siteDefSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])

		defSiteSortMap.put("DesktopWeb", null)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)

		defCatSortMap.put("DefCategorySortMap", null)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		defBrandSortMap.put("DefBranSortMap", defBrandSortOpt)
		defBrandSortMap.put("DesktopWeb", defBrandSortOpt)

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)
		siteSortOptMap.put("DesktopWeb", siteDefSortOpt)

		brandSortOptMap.put("SiteSortOption", siteSortOptList)
		brandSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,brandSortOptItem)

		brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
			"defBrandSortOption":defBrandSortMap,"brandSortOptList":brandSortOptMap,"brandPromoId":brandPromoSet])

		brandPromoSet.setProperties(["brandContent":"BedBathBeyond","cssFilePath":"/store/css","jsFilePath":"/store/js"])

		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock
		siteRepoItemMock.setProperties(["defSiteSortOption":defSiteSortMap,"siteSortOptionList":siteSortOptMap])

		when :

		def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","DesktopWeb");

		then :
		
		0 * sortOptionsVoMock.setSortCode(*_)
		bccManagedBrand.getBrandContent().equals("BedBathBeyond")
		bccManagedBrand.getCssFilePath().equals("/store/css")
		bccManagedBrand.getJsFilePath().equals("/store/js")
	}

	/**
	 * getBccManagedBrand - RepositoryException scenario 	
	 */


	def "getBccManagedBrand - RepositoryException scenario" () {

		given :

		RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)

		def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] bccManagedBrandRepositoryItem = [brandRepositoryItem]

		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteDefSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOptItem = new RepositoryItemMock()
		RepositoryItemMock defBrandSortOpt = new RepositoryItemMock()
		RepositoryItemMock brandSortOptItem = new RepositoryItemMock()
		RepositoryItemMock tempBrandSortOptItem = new RepositoryItemMock()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock brandPromoSet = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()

		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		Map<String, RepositoryItem> defBrandSortMap  = new HashMap<>()
		Map<String, RepositoryItem> brandSortOptMap = new HashMap<>()

		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		List<RepositoryItem> catSortOptList = new ArrayList<>()
		List<RepositoryItem> brandSortOptList = new ArrayList<>()

		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		
		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> {throw new RepositoryException("Mock Repository Exception")}
		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US

		brandSortOptList.add(tempBrandSortOptItem)

		siteSortOptItem.setProperties(["sortingOptions":siteSortOptList])
		brandSortOptItem.setProperties(["sortingOptions":brandSortOptList])

		defBrandSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		defSiteSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		brandPromoSet.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		siteDefSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])

		defSiteSortMap.put("DesktopWeb", null)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, defSiteSortOpt)

		defCatSortMap.put("DefCategorySortMap", null)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		defBrandSortMap.put("DefBranSortMap", defBrandSortOpt)
		defBrandSortMap.put("DesktopWeb", defBrandSortOpt)
		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)
		siteSortOptMap.put("DesktopWeb", siteDefSortOpt)
		brandSortOptMap.put("SiteSortOption", siteSortOptList)
		brandSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,brandSortOptItem)

		brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
			"defBrandSortOption":defBrandSortMap,"brandSortOptList":brandSortOptMap,"brandPromoId":brandPromoSet])

		brandPromoSet.setProperties(["brandContent":"BedBathBeyond","cssFilePath":"/store/css","jsFilePath":"/store/js"])

		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock
		siteRepoItemMock.setProperties(["defSiteSortOption":defSiteSortMap,"siteSortOptionList":siteSortOptMap])

		when :

		def brandVO = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","DesktopWeb");

		then :
		brandVO.getJsFilePath() == null
		brandVO.getCssFilePath() == null
		brandVO.getBrandContent() == null
		1 * bbbSiteRepoToolsTestObj.logError("Catalog API Method Name [getBccManagedBrand]:RepositoryException", _)
	}

	def "getBccManagedBrand - BrandSortOptionList (brandSortOptList) is empty" () {

		given :

		RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)

		def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] bccManagedBrandRepositoryItem = [brandRepositoryItem]

		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteDefSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOptItem = new RepositoryItemMock()
		RepositoryItemMock defBrandSortOpt = new RepositoryItemMock()
		RepositoryItemMock brandSortOptItem = new RepositoryItemMock()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock brandPromoSet = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()

		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		Map<String, RepositoryItem> defBrandSortMap  = new HashMap<>()
		Map<String, RepositoryItem> BrandSortOptMap = new HashMap<>()

		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		List<RepositoryItem> catSortOptList = new ArrayList<>()

		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		SortOptionsVO sortOptionsVoMock = Mock()
		SortOptionVO sortOptionVoMock = Mock()
		
		def sortingCode = "BBB"
		def sortingValue = "SortVal"
		def sortingUrlParam = "sort?="
		def defBrandSortOrder = "ascending"
		def defSiteSortOrder = "descending"
		def brandPromoSetSortOrder = "ascending"
		def siteDefSortOptSortOder = "descending"
		 
		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bccManagedBrandRepositoryItem
		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US

		siteSortOptList.add(siteSortOpt)

		siteSortOptMap.put("SiteSortOption", siteSortOptItem)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)
		siteSortOptMap.put("DesktopWeb", siteDefSortOpt)

		siteSortOptItem.getPropertyValue(BBBCatalogConstants.SORTINGOPTIONS) >> siteSortOptList
		siteSortOptItem.setProperties(["sortingOptions":siteSortOptList])

		defBrandSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingValue,"sortingOrder":1,"sortingUrlParam":sortingUrlParam])
		defSiteSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingValue,"sortingOrder":2,"sortingUrlParam":sortingUrlParam,"sortingOptions":siteSortOptList])
		brandPromoSet.setProperties(["sortingCode":sortingCode,"sortingValue":sortingValue,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		siteDefSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingValue,"sortingOrder":4,"sortingUrlParam":sortingUrlParam,"sortingOptions":siteSortOptList])
		
		defSiteSortMap.put("DesktopWeb", defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, null)

		defCatSortMap.put("DefCategorySortMap", null)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		defBrandSortMap.put("DefBranSortMap", defBrandSortOpt)
		defBrandSortMap.put("DesktopWeb", defBrandSortOpt)

		brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
			"defBrandSortOption":defBrandSortMap,"brandSortOptList":defBrandSortMap,"brandPromoId":brandPromoSet])

		brandPromoSet.setProperties(["brandContent":"BedBathBeyond","cssFilePath":"/store/css","jsFilePath":"/store/js"])

		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock
		siteRepoItemMock.setProperties(["defSiteSortOption":defSiteSortMap,"siteSortOptionList":siteSortOptMap])

		when :

		def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","DesktopWeb");

		then :
		
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortCode().equals(sortingCode)		
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortValue().equals(sortingValue)
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortUrlParam().equals(sortingUrlParam)
		
	}

	def "getBccManagedBrand - bccManagedBrandRepositoryItem is not set (null) and siteDefSortOpt has valid data (not null)" () {
		
				given :
		
				RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()
		
				bbbSiteRepoToolsTestObj = Spy()
				bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
				bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)
				bbbSiteRepoToolsTestObj.setBrandNameQuery("brandName")
				bbbSiteRepoToolsTestObj.setBrandIdQuery("brandId")
				bbbSiteRepoToolsTestObj.setCatalogRepository(catalogRepositoryMock)
		
				def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
				def RepositoryItem[] bccManagedBrandRepositoryItem = null
				def siteDefSortOptId = "siteDefSortOpt01"
				def defSiteSortOptId = "defSiteSortOpt01"
				def siteSortOptId = "siteSortOpt01"
				def catSortOptId = "catSortOpt01"
				def defBrandSortOptId = "defBrandSortOpt01"
				def brandPromoSetId = "brandPromoSet01"
				def defCartSortOptId = "defCartSortOpt01"
				def categoryPromoIdRepoId = "categoryPromoIdRepoId01"
				
				RepositoryItemMock defSiteSortOpt = new RepositoryItemMock(["id":defSiteSortOptId])
				RepositoryItemMock siteSortOpt = new RepositoryItemMock(["id":siteSortOptId])
				RepositoryItemMock defBrandSortOpt = new RepositoryItemMock(["id":defBrandSortOptId])
				RepositoryItemMock catSortOpt = new RepositoryItemMock(["id":catSortOptId])
				RepositoryItemMock siteDefSortOpt = new RepositoryItemMock(["id":siteDefSortOptId])
				RepositoryItemMock brandPromoSet = new RepositoryItemMock(["id":brandPromoSetId])
				RepositoryItemMock defCatSortOpt = new RepositoryItemMock(["id":defCartSortOptId])
				RepositoryItemMock categoryPromoId = new RepositoryItemMock(["id":categoryPromoIdRepoId])
				
				def sortingCode = "BBB"
				def sortingValue = "SortVal"
				def sortingUrlParam = "sort?="
				def defBrandSortOrder = "ascending"
				def defSiteSortOrder = "descending"
				def brandPromoSetSortOrder = "ascending"
				def siteDefSortOptSortOder = "descending"
		
				Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
				Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
				Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
				Map<String, RepositoryItem> defBrandSortMap  = new HashMap<>()
				Map<String, RepositoryItem> BrandSortOptMap = new HashMap<>()
		
				List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
				List<RepositoryItem> catSortOptList = new ArrayList<>()
		
				Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
				
				SortOptionVO sortOptionsMock = Mock()
				SortOptionsVO sortOptionMock = Mock()
		
				bbbSiteRepoToolsTestObj.executeRQLQuery(bbbSiteRepoToolsTestObj.getBrandNameQuery(),['BBB'], BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR, bbbSiteRepoToolsTestObj.getCatalogRepository()) >> brandRepositoryItem
				bbbSiteRepoToolsTestObj.executeRQLQuery(bbbSiteRepoToolsTestObj.getBrandIdQuery(),['BBB'], BBBCatalogConstants.BCC_MANAGED_BRAND_ITEM_DESCRIPTOR,
				(MutableRepository) bbbSiteRepoToolsTestObj.getBbbManagedCatalogRepository()) >> null
				bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		
				siteSortOptList.add(siteSortOpt)
		
				siteSortOptMap.put("SiteSortOption", siteSortOpt)
				siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defSiteSortOpt)
				siteSortOptMap.put("DesktopWeb", siteDefSortOpt)
		
				defBrandSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":1,"sortingUrlParam":"sort?="])
				defSiteSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":2,"sortingUrlParam":"sort?=","sortingOptions":siteSortOptList])
				siteSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":2,"sortingUrlParam":"sort?=","sortingOptions":siteSortOptList])
				brandPromoSet.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
				siteDefSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":4,"sortingUrlParam":"sort?=","sortingOptions":siteSortOptList])
				
				siteDefSortOpt.getPropertyValue(BBBCatalogConstants.SORTINGVALUE) >> "SortVal"
				
				defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, siteDefSortOpt)
		
				defCatSortMap.put("DefCategorySortMap", null)
				defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)
		
				catSortOptMap.put("CatSortOPtMap",catSortOptList)
				catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)
		
				defBrandSortMap.put("DefBranSortMap", defBrandSortOpt)
				defBrandSortMap.put("DesktopWeb", defBrandSortOpt)
		
				brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
					"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
					"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
					"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
					"defBrandSortOption":defBrandSortMap,"brandSortOptList":defBrandSortMap,"brandPromoId":brandPromoSet])
		
				brandPromoSet.setProperties(["brandContent":"BedBathBeyond","cssFilePath":"/store/css","jsFilePath":"/store/js"])
		
				bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
				siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock
				siteRepoItemMock.setProperties(["defSiteSortOption":defSiteSortMap,"siteSortOptionList":siteSortOptMap])
		
				when :
		
				def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","Mobile");
		
				then :
		
				bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortCode().equals(sortingCode)		
				bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortValue().equals(sortingValue)
				bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortUrlParam().equals(sortingUrlParam)
			}
		
		
	def "getBccManagedBrand - bccManagedBrandRepositoryItem, sortOptList and siteDefSortOpt are not set (null)" () {

		given :

		RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)
		bbbSiteRepoToolsTestObj.setBrandNameQuery("brandName")
		bbbSiteRepoToolsTestObj.setBrandIdQuery("brandId")
		bbbSiteRepoToolsTestObj.setCatalogRepository(catalogRepositoryMock)

		def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] bccManagedBrandRepositoryItem = null

		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteDefSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOptItem = new RepositoryItemMock()
		RepositoryItemMock defBrandSortOpt = new RepositoryItemMock()
		RepositoryItemMock brandSortOptItem = new RepositoryItemMock()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock brandPromoSet = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()

		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = null
		Map<String, RepositoryItem> defBrandSortMap  = new HashMap<>()
		Map<String, RepositoryItem> BrandSortOptMap = new HashMap<>()


		List<RepositoryItemMock> siteSortOptList = null
		List<RepositoryItem> catSortOptList = new ArrayList<>()

		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		bbbSiteRepoToolsTestObj.executeRQLQuery(bbbSiteRepoToolsTestObj.getBrandNameQuery(),['BBB'], BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR, bbbSiteRepoToolsTestObj.getCatalogRepository()) >> brandRepositoryItem
		bbbSiteRepoToolsTestObj.executeRQLQuery(bbbSiteRepoToolsTestObj.getBrandIdQuery(),['BBB'], BBBCatalogConstants.BCC_MANAGED_BRAND_ITEM_DESCRIPTOR,
		(MutableRepository) bbbSiteRepoToolsTestObj.getBbbManagedCatalogRepository()) >> null
		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptItem)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)
		siteSortOptMap.put("DesktopWeb", siteDefSortOpt)

		siteSortOptItem.getPropertyValue(BBBCatalogConstants.SORTINGOPTIONS) >> siteSortOptList
		siteSortOptItem.setProperties(["sortingOptions":siteSortOptList])

		defBrandSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":1,"sortingUrlParam":"sort?="])
		defSiteSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":2,"sortingUrlParam":"sort?=","sortingOptions":siteSortOptList])
		brandPromoSet.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])

		defCatSortMap.put("DefCategorySortMap", null)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		defBrandSortMap.put("DefBranSortMap", defBrandSortOpt)
		defBrandSortMap.put("DesktopWeb", defBrandSortOpt)

		brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
			"defBrandSortOption":defBrandSortMap,"brandSortOptList":defBrandSortMap,"brandPromoId":brandPromoSet])

		brandPromoSet.setProperties(["brandContent":"BedBathBeyond","cssFilePath":"/store/css","jsFilePath":"/store/js"])

		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock
		siteRepoItemMock.setProperties(["defSiteSortOption":defSiteSortMap,"siteSortOptionList":siteSortOptMap])

		when :

		def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","DesktopWeb");

		then :
		
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortCode() == null
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortValue() == null
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortUrlParam() == null
				
	}

	def "getBccManagedBrand - siteSortOptions (siteSortOptMap) is empty" () {

		given :
		RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)

		def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] bccManagedBrandRepositoryItem = [brandRepositoryItem]

		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteDefSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOptItem = new RepositoryItemMock()
		RepositoryItemMock defBrandSortOpt = new RepositoryItemMock()
		RepositoryItemMock brandSortOptItem = new RepositoryItemMock()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock brandPromoSet = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()

		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		Map<String, RepositoryItem> defBrandSortMap  = new HashMap<>()
		Map<String, RepositoryItem> BrandSortOptMap = new HashMap<>()

		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		List<RepositoryItem> catSortOptList = new ArrayList<>()

		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bccManagedBrandRepositoryItem
		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US

		def sortingCode = "BBB"
		def sortingVal = "sortingValue"
		def sortingUrlParam = "sort?="

		siteSortOptItem.setProperties(["sortingOptions":siteSortOptList])
		defBrandSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		defSiteSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		brandPromoSet.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		siteDefSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])


		defSiteSortMap.put("DesktopWeb", defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, null)

		defCatSortMap.put("DefCategorySortMap", null)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		defBrandSortMap.put("DefBranSortMap", defBrandSortOpt)
		defBrandSortMap.put("DesktopWeb", defBrandSortOpt)

		brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
			"defBrandSortOption":defBrandSortMap,"brandSortOptList":defBrandSortMap,"brandPromoId":brandPromoSet])

		brandPromoSet.setProperties(["brandContent":"BedBathBeyond","cssFilePath":"/store/css","jsFilePath":"/store/js"])

		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock
		siteRepoItemMock.setProperties(["defSiteSortOption":defSiteSortMap,"siteSortOptionList":siteSortOptMap])

		when :

		def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","DesktopWeb");

		then :
		
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortCode().equals(sortingCode)
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortValue().equals(sortingVal)
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortUrlParam().equals(sortingUrlParam)
		0 * siteSortOptMap.get(*_)
		0 * siteSortOptItem.getPropertyValue(BBBCatalogConstants.SORTINGOPTIONS)
		

	}

	def "getBccManagedBrand - siteSortOptMap has no channel attribute" () {

		given :
		RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)

		def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] bccManagedBrandRepositoryItem = [brandRepositoryItem]

		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteDefSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOptItem = new RepositoryItemMock()
		RepositoryItemMock defBrandSortOpt = new RepositoryItemMock()
		RepositoryItemMock brandSortOptItem = new RepositoryItemMock()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock brandPromoSet = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()

		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		Map<String, RepositoryItem> defBrandSortMap  = new HashMap<>()
		Map<String, RepositoryItem> BrandSortOptMap = new HashMap<>()


		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		List<RepositoryItem> catSortOptList = new ArrayList<>()

		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		
		def sortingCode = "BBB"
		def sortingVal = "sortingValue"
		def sortingUrlParam = "sort?="
		
		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bccManagedBrandRepositoryItem
		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)
		siteSortOptItem.setProperties(["sortingOptions":siteSortOptList])
		defBrandSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		defSiteSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		brandPromoSet.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		siteDefSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		defBrandSortMap.put("DefBranSortMap", defBrandSortOpt)
		defBrandSortMap.put("DesktopWeb", defBrandSortOpt)

		brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
			"defBrandSortOption":defBrandSortMap,"brandSortOptList":defBrandSortMap,"brandPromoId":brandPromoSet])

		brandPromoSet.setProperties(["brandContent":"BedBathBeyond","cssFilePath":"/store/css","jsFilePath":"/store/js"])

		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock
		siteRepoItemMock.setProperties(["defSiteSortOption":defSiteSortMap,"siteSortOptionList":siteSortOptMap])

		when :

		def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","Mobile");

		then :
		
		0 * siteSortOptMap.get("Mobile")
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortCode().equals(sortingCode)
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortValue().equals(sortingVal)
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortUrlParam().equals(sortingUrlParam)
	}


	def "getBccManagedBrand - site sort options (map - siteSortOptMap) is not set (null)" () {

		given :
		RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)

		def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] bccManagedBrandRepositoryItem = [brandRepositoryItem]

		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteDefSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOptItem = new RepositoryItemMock()
		RepositoryItemMock defBrandSortOpt = new RepositoryItemMock()
		RepositoryItemMock brandSortOptItem = new RepositoryItemMock()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock brandPromoSet = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()

		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		Map<String, RepositoryItem> defBrandSortMap  = new HashMap<>()
		Map<String, RepositoryItem> BrandSortOptMap = new HashMap<>()


		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		List<RepositoryItem> catSortOptList = new ArrayList<>()

		Map<String, List<RepositoryItem>> siteSortOptMap = null

		def sortingCode = "BBB"
		def sortingVal = "sortingValue"
		def sortingUrlParam = "sort?="
		
		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bccManagedBrandRepositoryItem
		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US

		siteSortOptItem.setProperties(["sortingOptions":siteSortOptList])
		defBrandSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		defSiteSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		brandPromoSet.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		siteDefSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])

		defSiteSortMap.put("DesktopWeb", defSiteSortOpt)
		defSiteSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE, null)

		defCatSortMap.put("DefCategorySortMap", null)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		defBrandSortMap.put("DefBranSortMap", defBrandSortOpt)
		defBrandSortMap.put("DesktopWeb", defBrandSortOpt)

		brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
			"defBrandSortOption":defBrandSortMap,"brandSortOptList":defBrandSortMap,"brandPromoId":brandPromoSet])

		brandPromoSet.setProperties(["brandContent":"BedBathBeyond","cssFilePath":"/store/css","jsFilePath":"/store/js"])

		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock

		when :

		def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","DesktopWeb");

		then :
		
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortCode().equals(sortingCode)
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortValue().equals(sortingVal)
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortUrlParam().equals(sortingUrlParam)
		0 * siteSortOptItem.getPropertyValue(BBBCatalogConstants.SORTINGOPTIONS)
		

	}

	def "getBccManagedBrand - default site sort(map- defSiteSortMap) is empty" () {

		given :
		RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)

		def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] bccManagedBrandRepositoryItem = [brandRepositoryItem]

		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteDefSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOptItem = new RepositoryItemMock()
		RepositoryItemMock defBrandSortOpt = new RepositoryItemMock()
		RepositoryItemMock brandSortOptItem = new RepositoryItemMock()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock brandPromoSet = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()

		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		Map<String, RepositoryItem> defBrandSortMap  = new HashMap<>()
		Map<String, RepositoryItem> BrandSortOptMap = new HashMap<>()


		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		List<RepositoryItem> catSortOptList = new ArrayList<>()

		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()

		def sortingCode = "BBB"
		def sortingVal = "sortingValue"
		def sortingUrlParam = "sort?="
		
		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bccManagedBrandRepositoryItem
		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US

		siteSortOptItem.setProperties(["sortingOptions":siteSortOptList])
		defBrandSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		defSiteSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		brandPromoSet.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		siteDefSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		
		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)
		siteSortOptMap.put("DesktopWeb", siteDefSortOpt)

		defCatSortMap.put("DefCategorySortMap", null)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		defBrandSortMap.put("DefBranSortMap", defBrandSortOpt)
		defBrandSortMap.put("DesktopWeb", defBrandSortOpt)

		brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
			"defBrandSortOption":defBrandSortMap,"brandSortOptList":defBrandSortMap,"brandPromoId":brandPromoSet])

		brandPromoSet.setProperties(["brandContent":"BedBathBeyond","cssFilePath":"/store/css","jsFilePath":"/store/js"])

		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock
		siteRepoItemMock.setProperties(["defSiteSortOption":defSiteSortMap,"siteSortOptionList":siteSortOptMap])

		when :

		def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","DesktopWeb");

		then :
		
		1 * bbbSiteRepoToolsTestObj.logError("Catalog API Method Name[getBccManagedCategory]"+ ":Default Site Sort Option Map Null")
		0 * defSiteSortMap.get(*_)
		0 * defSiteSortMap.getPropertyValue(BBBCatalogConstants.SORTINGOPTIONS)		
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortCode().equals(sortingCode)
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortValue().equals(sortingVal)
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortUrlParam().equals(sortingUrlParam)

	}

	def "getBccManagedBrand - default site sort map (defSiteSortMap) doesn't have channel(device accessed by user ex : Mobile)" () {

		given :
		RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)

		def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] bccManagedBrandRepositoryItem = [brandRepositoryItem]

		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteDefSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOptItem = new RepositoryItemMock()
		RepositoryItemMock defBrandSortOpt = new RepositoryItemMock()
		RepositoryItemMock brandSortOptItem = new RepositoryItemMock()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock brandPromoSet = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()

		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		Map<String, RepositoryItem> defBrandSortMap  = new HashMap<>()
		Map<String, RepositoryItem> BrandSortOptMap = new HashMap<>()

		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		List<RepositoryItem> catSortOptList = new ArrayList<>()

		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()

		def sortingCode = "BBB"
		def sortingVal = "sortingValue"
		def sortingUrlParam = "sort?="
		
		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bccManagedBrandRepositoryItem
		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US

		siteSortOptItem.setProperties(["sortingOptions":siteSortOptList])
		defBrandSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		defSiteSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		brandPromoSet.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		siteDefSortOpt.setProperties(["sortingCode":sortingCode,"sortingValue":sortingVal,"sortingOrder":3,"sortingUrlParam":sortingUrlParam])
		
		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)
		siteSortOptMap.put("DesktopWeb", siteDefSortOpt)

		defSiteSortMap.put("DesktopWeb", defSiteSortOpt)
		defCatSortMap.put("DefCategorySortMap", null)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)
		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		defBrandSortMap.put("DefBranSortMap", defBrandSortOpt)
		defBrandSortMap.put("DesktopWeb", defBrandSortOpt)

		brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
			"defBrandSortOption":defBrandSortMap,"brandSortOptList":defBrandSortMap,"brandPromoId":brandPromoSet])

		brandPromoSet.setProperties(["brandContent":"BedBathBeyond","cssFilePath":"/store/css","jsFilePath":"/store/js"])

		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock
		siteRepoItemMock.setProperties(["defSiteSortOption":defSiteSortMap,"siteSortOptionList":siteSortOptMap])

		when :

		def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","Mobile");

		then :
		
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortCode().equals(sortingCode)
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortValue().equals(sortingVal)
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortUrlParam().equals(sortingUrlParam)

	}

	def "getBccManagedBrand - brandPromoSet is not set(null) and brandSortOption(map- brandSortOptMap) doesnot have channel accessed by user (ex : mobile, Desktop)" () {

		given :
		
		RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)

		def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] bccManagedBrandRepositoryItem = [brandRepositoryItem]

		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteDefSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOptItem = new RepositoryItemMock()
		RepositoryItemMock defBrandSortOpt = new RepositoryItemMock()
		RepositoryItemMock brandSortOptItem = new RepositoryItemMock()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock brandPromoSet = null
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()

		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		Map<String, RepositoryItem> defBrandSortMap  = new HashMap<>()
		Map<String, RepositoryItem> BrandSortOptMap = new HashMap<>()

		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		List<RepositoryItem> catSortOptList = new ArrayList<>()

		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		
		BrandVO brandVoMock = Mock()
		
		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bccManagedBrandRepositoryItem
		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)
		siteSortOptMap.put("DesktopWeb", siteDefSortOpt)

		siteSortOptItem.setProperties(["sortingOptions":siteSortOptList])
		defBrandSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		defSiteSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		siteDefSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])

		defCatSortMap.put("DefCategorySortMap", null)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		defBrandSortMap.put("DefBranSortMap", defBrandSortOpt)
		defBrandSortMap.put("DesktopWeb", defBrandSortOpt)

		brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
			"defBrandSortOption":defBrandSortMap,"brandSortOptList":defBrandSortMap,"brandPromoId":brandPromoSet])

		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock
		siteRepoItemMock.setProperties(["defSiteSortOption":defSiteSortMap,"siteSortOptionList":siteSortOptMap])

		when :

		def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","Mobile");

		then :
		
		bccManagedBrand.getBrandContent() == null
		bccManagedBrand.getCssFilePath() == null
		bccManagedBrand.getJsFilePath() == null
	}

	def "getBccManagedBrand - brandPromoSet and it's properties are not set (null) with valid defaultBrandSort options (defBrandSortMap)" () {

		given :
		RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)

		def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] bccManagedBrandRepositoryItem = [brandRepositoryItem]

		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteDefSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOptItem = new RepositoryItemMock()
		RepositoryItemMock defBrandSortOpt = new RepositoryItemMock()
		RepositoryItemMock brandSortOptItem = new RepositoryItemMock()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock brandPromoSet = new RepositoryItemMock()
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()

		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		Map<String, RepositoryItem> defBrandSortMap  = new HashMap<>()
		Map<String, RepositoryItem> BrandSortOptMap = new HashMap<>()


		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		List<RepositoryItem> catSortOptList = new ArrayList<>()

		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()
		BrandVO brandVoMock = Mock()
		
		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bccManagedBrandRepositoryItem
		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)
		siteSortOptMap.put("DesktopWeb", siteDefSortOpt)

		siteSortOptItem.setProperties(["sortingOptions":siteSortOptList])
		defBrandSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		defSiteSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		siteDefSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])

		defCatSortMap.put("DefCategorySortMap", null)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
			"defBrandSortOption":defBrandSortMap,"brandSortOptList":defBrandSortMap,"brandPromoId":brandPromoSet])

		brandPromoSet.setProperties(["brandContent":null,"cssFilePath":null,"jsFilePath":null])

		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock
		siteRepoItemMock.setProperties(["defSiteSortOption":defSiteSortMap,"siteSortOptionList":siteSortOptMap])

		when :

		def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","Mobile");

		then :
		
		bccManagedBrand.getBrandContent() == null
		bccManagedBrand.getCssFilePath() == null
		bccManagedBrand.getJsFilePath() == null

	}

	def "getBccManagedBrand - defaultBrandSort property (defBrandSortMap) is not set (null)" () {

		given :
		RepositoryItemMock siteRepoItemMock = new RepositoryItemMock()

		bbbSiteRepoToolsTestObj = Spy()
		bbbSiteRepoToolsTestObj.setSiteRepository(siteRepositoryMock)
		bbbSiteRepoToolsTestObj.setShippingRepository(shippingRepositoryMock)
		
		def brandPromoId = "brandPromo01"
		
		def RepositoryItemMock brandRepositoryItem = new RepositoryItemMock()
		def RepositoryItem[] bccManagedBrandRepositoryItem = [brandRepositoryItem]

		RepositoryItemMock defSiteSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOpt = new RepositoryItemMock()
		RepositoryItemMock catSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteDefSortOpt = new RepositoryItemMock()
		RepositoryItemMock siteSortOptItem = new RepositoryItemMock()
		RepositoryItemMock defBrandSortOpt = new RepositoryItemMock()
		RepositoryItemMock managedCategoryDetailItem = new RepositoryItemMock()
		RepositoryItemMock categoryPromoId = new RepositoryItemMock()
		RepositoryItemMock brandPromoSet = new RepositoryItemMock(["id":brandPromoId])
		RepositoryItemMock defCatSortOpt = new RepositoryItemMock()
		
		Map<String, RepositoryItem>  defCatSortMap = new HashMap<>()
		Map<String, RepositoryItem> catSortOptMap = new HashMap<>()
		Map<String, RepositoryItem> defSiteSortMap = new HashMap<>()
		Map<String, RepositoryItem> defBrandSortMap  = null

		List<RepositoryItemMock> siteSortOptList = new ArrayList<>()
		List<RepositoryItem> catSortOptList = new ArrayList<>()

		Map<String, List<RepositoryItem>> siteSortOptMap = new HashMap<>()

		bbbSiteRepoToolsTestObj.executeRQLQuery(*_) >> bccManagedBrandRepositoryItem
		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US

		siteSortOptMap.put("SiteSortOption", siteSortOptList)
		siteSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,siteSortOpt)
		siteSortOptMap.put("DesktopWeb", siteDefSortOpt)

		siteSortOptItem.setProperties(["sortingOptions":siteSortOptList])
		defBrandSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		defSiteSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])
		siteDefSortOpt.setProperties(["sortingCode":"BBB","sortingValue":"SortVal","sortingOrder":3,"sortingUrlParam":"sort?="])

		defCatSortMap.put("DefCategorySortMap", null)
		defCatSortMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,defCatSortOpt)

		catSortOptMap.put("CatSortOPtMap",catSortOptList)
		catSortOptMap.put(BBBCoreConstants.DEFAULT_CHANNEL_VALUE,catSortOpt)

		brandRepositoryItem.setProperties(["catPromoId":categoryPromoId, "chatEnabled":true,"chatURL":"chat.bbb.com",
			"chatCode":"bbbchat","chatLinkPlaceholder":"Enter you chat link here",
			"defCatSortOption":defCatSortMap,"catSortOptionList":catSortOptMap,
			"displayaskandanswer":"DisplayAskAndAnswer", "zoomValue" :"20%","defaultViewValue":"DesktopView",
			"defBrandSortOption":defBrandSortMap,"brandSortOptList":defBrandSortMap,"brandPromoId":brandPromoSet])

		bbbSiteRepoToolsTestObj.getCurrentSiteId() >> SITE_ID_BEDBATH_US
		siteRepositoryMock.getItem(SITE_ID_BEDBATH_US,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR) >> siteRepoItemMock
		siteRepoItemMock.setProperties(["defSiteSortOption":defSiteSortMap,"siteSortOptionList":siteSortOptMap])

		when :

		def bccManagedBrand = bbbSiteRepoToolsTestObj.getBccManagedBrand("BBB","Mobile");

		then :
		
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortUrlParam() == null
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortCode() == null
		bccManagedBrand.getSortOptionVO().getDefaultSortingOption().getSortValue() == null
		
		bccManagedBrand.getBrandContent() == null
		bccManagedBrand.getCssFilePath() == null
		bccManagedBrand.getJsFilePath() == null
	}


	/* getBccManagedBrand - test cases - ENDS */

}