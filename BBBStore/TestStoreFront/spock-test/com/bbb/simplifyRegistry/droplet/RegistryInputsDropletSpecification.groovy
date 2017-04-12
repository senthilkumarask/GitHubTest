package com.bbb.simplifyRegistry.droplet

import atg.repository.RepositoryException
import com.bbb.constants.BBBCmsConstants;
import com.bbb.simplifyRegistry.RegistryInputVO
import com.bbb.simplifyRegistry.RegistryInputsByTypeVO
import com.bbb.simplifyRegistry.manager.SimplifyRegistryManager
import spock.lang.specification.BBBExtendedSpec

class RegistryInputsDropletSpecification extends BBBExtendedSpec {

	RegistryInputsDroplet rInputDroplet
	SimplifyRegistryManager srManager = Mock()
	RegistryInputsByTypeVO ribyTypeVO = new RegistryInputsByTypeVO()
	RegistryInputVO rinputVO = new RegistryInputVO()
	
	def setup(){
		 rInputDroplet = new RegistryInputsDroplet(simplifyRegistryManager : srManager )
	}
	
	def "service.TC to retrives registryInputs by registry type" (){
		given:
		    List rIList = [rinputVO]
			requestMock.getLocalParameter("eventType") >> "eType"
			1*srManager.getRegInputsByRegType("eType") >> ribyTypeVO
			
			ribyTypeVO.setRegistryInputList(rIList)
			
		when:
			rInputDroplet.service(requestMock, responseMock)
		then:
		
			1*requestMock.setParameter("registryInputsByTypeVO",ribyTypeVO)
			1*requestMock.setParameter("registryInputList",rIList)
			1*requestMock.serviceParameter("output", requestMock, responseMock)

	}
	
	def "service.TC when registryInputsByTypeVO is null" (){
		given:
			List rIList = [rinputVO]
			requestMock.getLocalParameter("eventType") >> "eType"
			1*srManager.getRegInputsByRegType("eType") >> null
			
			
		when:
			rInputDroplet.service(requestMock, responseMock)
		then:
		
			0*requestMock.setParameter("registryInputsByTypeVO",_)
			1*requestMock.serviceParameter("empty", requestMock, responseMock)

	}
	
	def "service.TC for RepositoryException" (){
		given:
			List rIList = [rinputVO]
			requestMock.getLocalParameter("eventType") >> "eType"
			1*srManager.getRegInputsByRegType("eType") >> {throw new RepositoryException("exception")}
			
			
		when:
			rInputDroplet.service(requestMock, responseMock)
		then:
		
			0*requestMock.setParameter("registryInputsByTypeVO",_)
			0*requestMock.serviceParameter("empty", requestMock, responseMock)

	}
}
