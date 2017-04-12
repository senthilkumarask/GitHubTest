package com.bbb.simplifyRegistry.tools

import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import spock.lang.specification.BBBExtendedSpec

import com.bbb.constants.BBBGiftRegistryConstants;

import atg.repository.Repository;

class SimplifyRegistryToolsSpecification extends BBBExtendedSpec {

	SimplifyRegistryTools srTools
	RepositoryView rView = Mock()
	Repository srRepository = Mock()
	RepositoryItem item = Mock()
	
	def setup(){
		//srTools = new SimplifyRegistryTools()
		 srTools = Spy()
		 srTools.setSimplifyRegistryRepository(srRepository)
	}
	
	def "getRegInputsByType. TC to  retrieve the data from kickStarter repository"(){
		given:
			1*srRepository.getView("registryInputsByType") >> rView
			1*srTools.executeQuery(rView, _, _) >> [item]
			
		when:
			RepositoryItem value = srTools.getRegInputsByType("rType")
		then:
		value == item
	}
	
	def "getRegInputsByType. TC to  retrieve the data from kickStarter repository when execute query return empty array"(){
		given:
			1*srRepository.getView("registryInputsByType") >> rView
			1*srTools.executeQuery(rView, _, _) >> []
			
		when:
			RepositoryItem value = srTools.getRegInputsByType("rType")
		then:
		value == null
	}
	
	def "getRegInputsByType. TC to  retrieve the data from kickStarter repository when execute query return null item list"(){
		given:
			1*srRepository.getView("registryInputsByType") >> rView
			1*srTools.executeQuery(rView, _, _) >> null
			
		when:
			RepositoryItem value = srTools.getRegInputsByType("rType")
		then:
		value == null
	}
}
