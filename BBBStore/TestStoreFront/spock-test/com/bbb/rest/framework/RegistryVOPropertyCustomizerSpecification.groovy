package com.bbb.rest.framework

import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.giftregistry.vo.AddressVO
import com.bbb.commerce.giftregistry.vo.RegistrantVO
import com.bbb.commerce.giftregistry.vo.RegistryTypes
import com.bbb.commerce.giftregistry.vo.RegistryVO
import com.bbb.commerce.giftregistry.vo.ShippingVO
import atg.repository.RepositoryItem
import atg.servlet.ServletUtil;
import spock.lang.specification.BBBExtendedSpec

/**
 * @author purusr
 *
 */

class RegistryVOPropertyCustomizerSpecification extends BBBExtendedSpec {
	def RegistryVOPropertyCustomizer testObj
	RepositoryItem repositoryItemMock = Mock()
	BBBProfileTools profileToolsMock = Mock()
	AddressVO addressVO
	RegistryVO registryVOMock
	ShippingVO shippingVOMock
	RegistrantVO registrantVOMock
	RegistryTypes registryTypesMock
	def setup(){
		testObj=new RegistryVOPropertyCustomizer();
		ServletUtil.setCurrentUserProfile(repositoryItemMock)
		requestMock.resolveName("/atg/userprofiling/ProfileTools") >> profileToolsMock
		 addressVO = new AddressVO(firstName:"John",lastName:"Ram",addressLine1:"857",addressLine2:"",city:"New York",state:"NY",country:"US",zip:"10001",primaryPhone:"9137284650")
		 registryVOMock = new RegistryVO(siteId:"BedBathUS",userAddressList:[addressVO])
		 shippingVOMock = new ShippingVO(shippingAddress:addressVO,futureshippingAddress:addressVO)
		 registrantVOMock = new RegistrantVO(cellPhone:"9575486826",primaryPhone:"9878456512",email:"john@gmail.com",contactAddress:addressVO)
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'RegistrantVO' and PropertyName as 'Email'and the User is recognizeduser"(){
		given:
			profileToolsMock.isRecognizedUser(requestMock, repositoryItemMock) >> true
		when:
			Object result =	testObj.getPropertyValue("email", registrantVOMock)
		then:
			result == "masked"
	}

	def "getPropertyValue -This TC is when Object is an Instanceof 'RegistrantVO' and PropertyName as 'Email'and the User is loggedIn user"(){
		given:
			profileToolsMock.isRecognizedUser(requestMock, repositoryItemMock) >> false
		when:
			Object result =	testObj.getPropertyValue("email", registrantVOMock)
		then:
			result == "john@gmail.com"
	}
	
	def "getPropertyValue -This TC is when Object is an Instanceof 'RegistrantVO' and PropertyName as 'cellPhone' and the User is recognizeduser"(){
		given:
			profileToolsMock.isRecognizedUser(requestMock, repositoryItemMock) >> true
		when:
			Object result =	testObj.getPropertyValue("cellPhone", registrantVOMock)
		then:
			result == "XXXXXXXXXX"
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'RegistrantVO' and PropertyName as 'cellPhone' and the User is loggedIn user"(){
		given:
			profileToolsMock.isRecognizedUser(requestMock, repositoryItemMock) >> false
		when:
			Object result =	testObj.getPropertyValue("cellPhone", registrantVOMock)
		then:
			result == "9575486826"
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'RegistrantVO' and PropertyName as 'primaryPhone' and the User is recognizeduser"(){
		given:
			profileToolsMock.isRecognizedUser(requestMock, repositoryItemMock) >> true
		when:
			Object result =	testObj.getPropertyValue("primaryPhone", registrantVOMock)
		then:
			result == "XXXXXXXXXX"
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'RegistrantVO' and PropertyName as 'primaryPhone' and the User is loggedIn user"(){
		given:
			profileToolsMock.isRecognizedUser(requestMock, repositoryItemMock) >> false
		when:
			Object result =	testObj.getPropertyValue("primaryPhone", registrantVOMock)
		then:
			result == "9878456512"
	}
	
	def "getPropertyValue - This TC is whenr Object is an Instanceof 'RegistrantVO' and PropertyName as 'contactAddress' and the User is recognizeduser"(){
		given:
			profileToolsMock.isRecognizedUser(requestMock, repositoryItemMock) >> true
		when:
			AddressVO result =	testObj.getPropertyValue("contactAddress", registrantVOMock)
		then:
			result.getFirstName().equals(null)
			
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'RegistrantVO' and PropertyName as 'contactAddress' and the User is loggedIn user"(){
		given:
			profileToolsMock.isRecognizedUser(requestMock, repositoryItemMock) >> false
		when:
			Object result =	testObj.getPropertyValue("contactAddress", registrantVOMock)
		then:
			result == addressVO
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'RegistrantVO' and PropertyName as 'profileId'"(){
		given:
			
		when:
			Object result =	testObj.getPropertyValue("profileId", registrantVOMock)
		then:
			result == null
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'RegistryVO' and PropertyName as 'userAddressList' and the User is recognizeduser"(){
		given:
			profileToolsMock.isRecognizedUser(requestMock, repositoryItemMock) >> true
		when:
			Object result =	testObj.getPropertyValue("userAddressList", registryVOMock)
		then:
			result == null
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'RegistryVO' and PropertyName as 'userAddressList' and the User is loggedIn user"(){
		given:
			profileToolsMock.isRecognizedUser(requestMock, repositoryItemMock) >> false
		when:
			Object result =	testObj.getPropertyValue("userAddressList", registryVOMock)
		then:
			result == [addressVO]
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'RegistryVO' and PropertyName as 'prefStoreNum'"(){
		given:
			
		when:
			Object result =	testObj.getPropertyValue("prefStoreNum", registryVOMock)
		then:
			result == null
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'ShippingVO' and PropertyName as 'shippingAddress' and the User is recognizeduser"(){
		given:
			profileToolsMock.isRecognizedUser(requestMock, repositoryItemMock) >> true
		when:
			AddressVO result =	testObj.getPropertyValue("shippingAddress", shippingVOMock)
		then:
			result.getFirstName().equals(null)
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'ShippingVO' and PropertyName as 'shippingAddress' and the User is loggedIn user"(){
		given:
			profileToolsMock.isRecognizedUser(requestMock, repositoryItemMock) >> false
		when:
			Object result =	testObj.getPropertyValue("shippingAddress", shippingVOMock)
		then:
			result == addressVO
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'ShippingVO' and PropertyName as 'futureshippingAddress' and the User is recognizeduser"(){
		given:
			profileToolsMock.isRecognizedUser(requestMock, repositoryItemMock) >> true
		when:
			AddressVO result =	testObj.getPropertyValue("futureshippingAddress", shippingVOMock)
		then:
			result.getFirstName().equals(null)
			result.getLastName().equals(null)
			result.getAddressLine1().equals(null)
			result.getAddressLine2().equals(null)
			result.getCity().equals(null)
			result.getCountry().equals(null)
			result.getPrimaryPhone().equals(null)
			result.getState().equals(null)
			result.getZip().equals(null)
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'ShippingVO' and PropertyName as 'futureshippingAddress' and the User is not login"(){
		given:
			ServletUtil.setCurrentUserProfile(null)
			
		when:
			AddressVO result =	testObj.getPropertyValue("futureshippingAddress", shippingVOMock)
		then:
			result == addressVO
			result.getAddressLine1().equals("857")
			result.getAddressLine2().equals("")
			result.getCity().equals("New York")
			result.getCountry().equals("US")
			result.getFirstName().equals("John")
			result.getLastName().equals("Ram")
			result.getPrimaryPhone().equals("9137284650")
			result.getState().equals("NY")
			result.getZip().equals("10001")
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'ShippingVO' and PropertyName as 'futureShippingDate'"(){
		given:
			
		when:
			Object result =	testObj.getPropertyValue("futureShippingDate", shippingVOMock)
		then:
			result == null
	}
	
	def "getPropertyValue - This TC is when Object is an Instanceof 'RegistryTypes' and PropertyName as 'registryTypeId'"(){
		given:
			
		when:
			Object result =	testObj.getPropertyValue("registryTypeId", registryTypesMock)
		then:
			result == null
	}
	
	
	def "setPropertyValue-This TC is when pass all vlaues"(){
		given:
		
		when:
			testObj.setPropertyValue("asdfdF",shippingVOMock,registryVOMock)
		then:
			thrown UnsupportedOperationException
	}
}
