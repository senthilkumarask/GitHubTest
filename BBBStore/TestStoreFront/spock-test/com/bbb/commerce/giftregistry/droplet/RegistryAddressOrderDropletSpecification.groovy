package com.bbb.commerce.giftregistry.droplet

import atg.nucleus.naming.ParameterName;
import atg.repository.MutableRepositoryItem
import atg.repository.RepositoryItem

import com.bbb.commerce.giftregistry.vo.AddressVO

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class RegistryAddressOrderDropletSpecification extends BBBExtendedSpec {
	
	RegistryAddressOrderDroplet testObj
	RepositoryItem repositoryItemMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	
	public static final ParameterName DEFAULT_ID = ParameterName.getParameterName("defaultId")
	public static final ParameterName DEFAULT_KEY = ParameterName.getParameterName("defaultKey")
	public static final ParameterName SORT_BY_KEYS = ParameterName.getParameterName("sortByKeys")
	public static final ParameterName OUTPUT = ParameterName.getParameterName("output")
	public static final ParameterName SHIP_FIRST_NAME = ParameterName.getParameterName("shippingFirstName")
	public static final ParameterName SHIP_LAST_NAME = ParameterName.getParameterName("shippingLastName")
	public static final ParameterName MAP = ParameterName.getParameterName("map")
	public static final ParameterName WS_ADDRESS_VO = ParameterName.getParameterName("wsAddressVO")
	
	def setup(){
		testObj = new RegistryAddressOrderDroplet(territories:"AS,GU,FM,MP,PR,PW,MH,VI")
	}
	
	def"service method. This TC is the Happy flow of service method"(){
		given:
			testObj = Spy()
			requestMock.getParameter(DEFAULT_ID) >> "255542"
			requestMock.getParameter(DEFAULT_KEY) >> "key"
			requestMock.getParameter(SORT_BY_KEYS) >> "true"
			RepositoryItem repositoryItemMock1 = Mock()
			requestMock.getObjectParameter(MAP) >> ["1":null,"2":repositoryItemMock,"3":repositoryItemMock1]
			requestMock.getParameter(SHIP_FIRST_NAME) >> "John"
			requestMock.getParameter(SHIP_LAST_NAME) >> "Kennedy"
			AddressVO addressVOMock = new AddressVO(addressLine2:"Nagal Street",addressLine1:"Nagar",city:"New Jercy",state:"NJ",zip:"70001")
			requestMock.getObjectParameter(WS_ADDRESS_VO) >> addressVOMock
			repositoryItemMock.getRepositoryId() >> "2555"
			repositoryItemMock1.getRepositoryId() >> "255542"
			1 * repositoryItemMock1.getPropertyValue("address2") >> null
			1 * repositoryItemMock1.getPropertyValue("address1") >> "Begampur"
			1 * repositoryItemMock1.getPropertyValue("city") >> "Jercy"
			1 * repositoryItemMock1.getPropertyValue("state") >> "NJ"
			1 * repositoryItemMock1.getPropertyValue("postalCode") >> "70002"
			1 * repositoryItemMock1.getPropertyValue("firstName") >> "John"
			1 * repositoryItemMock1.getPropertyValue("lastName") >> "Gobinath"
			2 * repositoryItemMock.getPropertyValue("address2") >> "Nagal Street"
			1 * repositoryItemMock.getPropertyValue("address1") >> "Nagar"
			1 * repositoryItemMock.getPropertyValue("city") >> "New Jercy"
			1 * repositoryItemMock.getPropertyValue("state") >> "NJ"
			1 * repositoryItemMock.getPropertyValue("postalCode") >> "70001"
			1 * repositoryItemMock.getPropertyValue("firstName") >> "John"
			1 * repositoryItemMock.getPropertyValue("lastName") >> "Kennedy"
			1 * repositoryItemMock.getPropertyValue("id") >> "repoItem"
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("isWSAddressInAddressBook", TRUE)
			1 * requestMock.setParameter("sortedArray", _)
			1 * requestMock.setParameter("sortedArraySize", 3)
			1 * requestMock.serviceLocalParameter(OUTPUT, requestMock, responseMock)
			1 * testObj.logDebug('Default entry was found in the map')
			1 * testObj.logDebug(' defaultId = 255542')
			1 * testObj.logDebug(' defaultKey = key')
			1 * testObj.logDebug('RegistryAddressOrderDroplet/MSG =[Web service address is found in AddressBook at id =repoItem')
			1 * testObj.logDebug(' map = {1=null, 2=Mock for type \'RepositoryItem\' named \'repositoryItemMock\', 3=Mock for type \'RepositoryItem\' named \'repositoryItemMock1\'}')
			1 * testObj.logDebug('RegistryAddressOrderDroplet/MSG =[WSAddress =NagarNagal StreetNew JercyNJ70001JohnKennedy')
			1 * testObj.logDebug(' sortByKeys = true')
			1 * testObj.logDebug('RegistryAddressOrderDroplet Service starts')
			1 * testObj.logDebug('Map size: 3')
			1 * testObj.logDebug('RegistryAddressOrderDroplet/MSG =[While Sorting to new Array, address found at index =2')
	}
	
	def"service method. This TC is when sortByKeysParameter is empty and map is null"(){
		given:
			requestMock.getParameter(DEFAULT_ID) >> "255542"
			requestMock.getParameter(DEFAULT_KEY) >> "key"
			requestMock.getParameter(SORT_BY_KEYS) >> ""
			requestMock.getObjectParameter(MAP) >> null
			requestMock.getParameter(SHIP_FIRST_NAME) >> "John"
			requestMock.getParameter(SHIP_LAST_NAME) >> "Kennedy"
			AddressVO addressVOMock = new AddressVO()
			requestMock.getObjectParameter(WS_ADDRESS_VO) >> addressVOMock
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("sortedArray", null)
			1 * requestMock.serviceLocalParameter(OUTPUT, requestMock, responseMock)
	}
	
	def"service method. This TC is when shippingFirstName and shippingLastName is empty and isWSAddFoundInAddressBook is false"(){
		given:
			testObj = Spy()
			testObj.setTerritories("AS,GU,FM,MP,PR,PW,MH,VI")
			requestMock.getParameter(DEFAULT_ID) >> null
			requestMock.getParameter(DEFAULT_KEY) >> "key"
			requestMock.getParameter(SORT_BY_KEYS) >> "false"
			RepositoryItem repositoryItemMock1 = Mock()
			requestMock.getObjectParameter(MAP) >> ["1":null,"key":repositoryItemMock,"2":repositoryItemMock1]
			requestMock.getParameter(SHIP_FIRST_NAME) >> ""
			requestMock.getParameter(SHIP_LAST_NAME) >> ""
			AddressVO addressVOMock = new AddressVO(addressLine2:null,addressLine1:"Nagar",city:"New Jercy",state:"NJ",zip:"70001")
			requestMock.getObjectParameter(WS_ADDRESS_VO) >> addressVOMock
			repositoryItemMock.getRepositoryId() >> "2555"
			repositoryItemMock1.getRepositoryId() >> "255542"
			1 * repositoryItemMock1.getPropertyValue("address2") >> null
			1 * repositoryItemMock1.getPropertyValue("address1") >> "Begampur"
			1 * repositoryItemMock1.getPropertyValue("city") >> "Jercy"
			2 * repositoryItemMock1.getPropertyValue("state") >> "MP"
			1 * repositoryItemMock1.getPropertyValue("postalCode") >> "70002"
			2 * repositoryItemMock.getPropertyValue("address2") >> "Nagal Street"
			1 * repositoryItemMock.getPropertyValue("address1") >> "Nagar"
			1 * repositoryItemMock.getPropertyValue("city") >> "New Jercy"
			2 * repositoryItemMock.getPropertyValue("state") >> "NJ"
			1 * repositoryItemMock.getPropertyValue("postalCode") >> "70001"
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("isWSAddressInAddressBook", FALSE)
			1 * requestMock.setParameter("sortedArray", _)
			1 * requestMock.setParameter("sortedArraySize", 3)
			1 * requestMock.serviceLocalParameter(OUTPUT, requestMock, responseMock)
			1 * testObj.logDebug(' sortByKeys = false')
			1 * testObj.logDebug(' defaultId = null')
			1 * testObj.logDebug('Default entry was found in the map')
			1 * testObj.logDebug('Map size: 3')
			1 * testObj.logDebug('RegistryAddressOrderDroplet/MSG =[WSAddress =NagarNew JercyNJ70001')
			1 * testObj.logDebug('RegistryAddressOrderDroplet Service ends')
			1 * testObj.logDebug(' defaultKey = key')
			1 * testObj.logDebug(' map = {1=null, key=Mock for type \'RepositoryItem\' named \'repositoryItemMock\', 2=Mock for type \'RepositoryItem\' named \'repositoryItemMock1\'}')
			1 * testObj.logDebug('RegistryAddressOrderDroplet Service starts')
	}
	
	def"service method. This TC is when defaultKey and wsAddressVO is null, map is empty"(){
		given:
			testObj = Spy()
			testObj.setTerritories("AS,GU,FM,MP,PR,PW,MH,VI")
			requestMock.getParameter(DEFAULT_ID) >> "321325"
			requestMock.getParameter(DEFAULT_KEY) >> null
			requestMock.getParameter(SORT_BY_KEYS) >> "false"
			RepositoryItem repositoryItemMock1 = Mock()
			requestMock.getObjectParameter(MAP) >> [:]
			requestMock.getParameter(SHIP_FIRST_NAME) >> ""
			requestMock.getParameter(SHIP_LAST_NAME) >> ""
			requestMock.getObjectParameter(WS_ADDRESS_VO) >> null
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("isWSAddressInAddressBook", TRUE)
			1 * requestMock.setParameter("sortedArray", _)
			1 * requestMock.setParameter("sortedArraySize", 0)
			1 * requestMock.serviceLocalParameter(OUTPUT, requestMock, responseMock)
			1 * testObj.logDebug('RegistryAddressOrderDroplet/MSG =[WebService Contact Address is null]')
			1 * testObj.logDebug(' defaultKey = null')
			1 * testObj.logDebug(' sortByKeys = false')
			1 * testObj.logDebug('Defauld entry was not found in the map')
			1 * testObj.logDebug(' map = {}')
	}
	
	def"service method. This TC is when map does not containKey of defaultKey value"(){
		given:
			requestMock.getParameter(DEFAULT_ID) >> "321325"
			requestMock.getParameter(DEFAULT_KEY) >> "key"
			requestMock.getParameter(SORT_BY_KEYS) >> "false"
			RepositoryItem repositoryItemMock1 = Mock()
			requestMock.getObjectParameter(MAP) >> [:]
			requestMock.getParameter(SHIP_FIRST_NAME) >> ""
			requestMock.getParameter(SHIP_LAST_NAME) >> ""
			requestMock.getObjectParameter(WS_ADDRESS_VO) >> null
		
		when:
			testObj.service(requestMock, responseMock)
		then:
			1 * requestMock.setParameter("isWSAddressInAddressBook", TRUE)
			1 * requestMock.setParameter("sortedArray", _)
			1 * requestMock.setParameter("sortedArraySize", 0)
			1 * requestMock.serviceLocalParameter(OUTPUT, requestMock, responseMock)
	}
	
	def"getDefaultMapKey. This TC is when map is null"(){
		given:
			
		when:
			String results = testObj.getDefaultMapKey(null, "key")
		then:
			results == null
	}
	
	def"getDefaultMapKey. This TC is when value is not instance of RepositoryItem"(){
		given:
			
		when:
			String results = testObj.getDefaultMapKey(["1":"item1"], "key")
		then:
			results == null
	}
	
	
	

}
