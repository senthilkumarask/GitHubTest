package com.bbb.simplifyRegistry.droplet

import atg.repository.RepositoryException
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants
import com.bbb.simplifyRegistry.RegistryInputVO;
import com.bbb.simplifyRegistry.RegistryInputsByTypeVO
import com.bbb.simplifyRegistry.manager.SimplifyRegistryManager

import spock.lang.specification.BBBExtendedSpec

class SimpleRegFieldsDropletSpecification extends BBBExtendedSpec {
	
	SimpleRegFieldsDroplet srfDroplet
	SimplifyRegistryManager srManager = Mock()
	RegistryInputsByTypeVO ribTypeVO = new RegistryInputsByTypeVO()
	RegistryInputVO rinputVO = new RegistryInputVO()
	RegistryInputVO rinputVO1 = new RegistryInputVO()
	
	def setup(){
		srfDroplet = new SimpleRegFieldsDroplet(simplifyRegistryManager : srManager)
	}
	
	def"service.TC to iterate the list of fields"(){
		given:
		   
			srfDroplet = Spy()
			srfDroplet.setSimplifyRegistryManager(srManager)
			
			List rIList = [rinputVO, rinputVO1]
			requestMock.getLocalParameter("eventType") >> "University"
			1*srfDroplet.getSiteId() >> "BedBathCanada"
			
			1*srManager.getRegInputsByRegType("College/University") >> ribTypeVO
			ribTypeVO.setRegistryInputList(rIList)

			rinputVO.setFieldName("fieldName")
			rinputVO1.setFieldName("")
			rinputVO.setDisplayOnForm(true)
			rinputVO.setRequiredInputCreate(true)
			rinputVO.setRequiredToMakeRegPublic(true)
		when:
			srfDroplet.service(requestMock, responseMock)
		
		then:
		
			1*requestMock.setParameter('inputListMap', ['fieldName':['requiredToMakeRegPublic':'true', 'isDisplayonForm':'true', 'isMandatoryOnCreate':'true']])
			1*requestMock.setParameter("registryInputsByTypeVO",ribTypeVO)
			1*requestMock.setParameter("registryInputList",rIList)
			1*requestMock.serviceParameter("output", requestMock, responseMock)

	}
	
	def"service.TC to iterate the list of fields when registry input list is null"(){
		given:
		   
			srfDroplet = Spy()
			srfDroplet.setSimplifyRegistryManager(srManager)
			
			//List rIList = [rinputVO, rinputVO1]
			requestMock.getLocalParameter("eventType") >> "notUniversity"
			srfDroplet.getSiteId() >> "TBS_BedBathCanada"
			
			srManager.getRegInputsByRegType("notUniversity") >> ribTypeVO
			ribTypeVO.setRegistryInputList(null)

		when:
			srfDroplet.service(requestMock, responseMock)
		
		then:
		
			1*requestMock.setParameter("inputListMap", [:])
			1*requestMock.setParameter("registryInputsByTypeVO",ribTypeVO)
			1*requestMock.setParameter("registryInputList",null)
			

	}
	
	def"service.TC for RepositoryException"(){
		given:
		   
			srfDroplet = Spy()
			srfDroplet.setSimplifyRegistryManager(srManager)
			
			//List rIList = [rinputVO, rinputVO1]
			requestMock.getLocalParameter("eventType") >> "notUniversity"
			srfDroplet.getSiteId() >> "notTBS_BedBathCanada"
			
			srManager.getRegInputsByRegType("notUniversity") >> {throw new  RepositoryException("exception")}
			ribTypeVO.setRegistryInputList(null)

		when:
			srfDroplet.service(requestMock, responseMock)
		
		then:
		
			0*requestMock.setParameter("inputListMap", [:])
			0*requestMock.setParameter("registryInputsByTypeVO",ribTypeVO)
			0*requestMock.setParameter("registryInputList",null)

	}

}
