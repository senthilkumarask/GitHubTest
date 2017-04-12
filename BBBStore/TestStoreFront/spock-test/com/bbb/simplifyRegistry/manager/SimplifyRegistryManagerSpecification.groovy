package com.bbb.simplifyRegistry.manager

import atg.repository.RepositoryItem
import com.bbb.simplifyRegistry.RegistryInputVO
import com.bbb.simplifyRegistry.RegistryInputsByTypeVO
import com.bbb.simplifyRegistry.tools.SimplifyRegistryTools
import spock.lang.specification.BBBExtendedSpec

class SimplifyRegistryManagerSpecification extends BBBExtendedSpec {

	SimplifyRegistryManager srManager
	SimplifyRegistryTools srTools = Mock()
	RepositoryItem rInputItem = Mock()
	RepositoryItem regInputItem1 = Mock()
	RepositoryItem regInputItem2 = Mock()
	
	def setup(){
		srManager = new SimplifyRegistryManager(simplifyRegistryTools : srTools)
	}
	
	def "getRegInputsByRegType. TC to get the and gets the repositoryData"(){
		given:
		Set regInputList = [regInputItem1,regInputItem2]
			1*srTools.getRegInputsByType("rType") >> rInputItem
			2*rInputItem.getPropertyValue("id") >> "item1"
			2*rInputItem.getPropertyValue("eventType") >> "eventType"
			2*rInputItem.getPropertyValue("isPublic") >> true
			2*rInputItem.getPropertyValue("regInputList") >>  regInputList
			
			2*regInputItem1.getPropertyValue("id") >> "1235"
			2*regInputItem1.getPropertyValue("fieldName") >> "fName"
			2*regInputItem1.getPropertyValue("displayOnForm") >> true
			2*regInputItem1.getPropertyValue("requiredInputCreate") >> true
			2*regInputItem1.getPropertyValue("requiredInputUpdate") >> true
			2*regInputItem1.getPropertyValue("requiredToMakeRegPublic") >> true
			
			1*regInputItem2.getPropertyValue("fieldName") >> null
			1*regInputItem2.getPropertyValue("displayOnForm") >> null
			1*regInputItem2.getPropertyValue("requiredInputCreate") >> null
			1*regInputItem2.getPropertyValue("requiredInputUpdate") >> null
			1*regInputItem2.getPropertyValue("requiredToMakeRegPublic") >> null

			
			
		when:
		   RegistryInputsByTypeVO result = srManager.getRegInputsByRegType("rType") 
		   
		then:
		
		result.getId().equalsIgnoreCase("item1")
		result.getEventType().equalsIgnoreCase("eventType")
		result.isPublic()
		RegistryInputVO riVO = result.getRegistryInputList().get(0)
		riVO.getId().equalsIgnoreCase("1235")
		riVO.getFieldName().equalsIgnoreCase("fName")
		riVO.isDisplayOnForm()
		riVO.isRequiredInputCreate()
		riVO.isRequiredInputUpdate()
		riVO.isRequiredToMakeRegPublic()
	
	}
	
	def "getRegInputsByRegType. TC to get the and gets the repositoryData when regInputList is null"(){
		given:
			1*srTools.getRegInputsByType("rType") >> rInputItem
			1*rInputItem.getPropertyValue("id") >> null
			1*rInputItem.getPropertyValue("eventType") >> null
			1*rInputItem.getPropertyValue("isPublic") >> null
			1*rInputItem.getPropertyValue("regInputList") >>  null
			
			
		when:
		   RegistryInputsByTypeVO result = srManager.getRegInputsByRegType("rType")
		   
		then:
		
		result.getId() == null
		result.getEventType() == null
		!result.isPublic()
	    result.getRegistryInputList() == []
		
	}
	
	def "getRegInputsByRegType. TC when registry input type item is null"(){
		given:
			1*srTools.getRegInputsByType("rType") >> null		
			
		when:
		   RegistryInputsByTypeVO result = srManager.getRegInputsByRegType("rType")
		   
		then:
		
		result.getId() == null
		result.getEventType() == null
		!result.isPublic()
		result.getRegistryInputList() == []
		
	}
}
