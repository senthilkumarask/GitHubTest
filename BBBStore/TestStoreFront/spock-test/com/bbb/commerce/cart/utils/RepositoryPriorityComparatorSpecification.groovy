package com.bbb.commerce.cart.utils;

import org.junit.Ignore;

import com.bbb.commerce.cart.utils.RepositoryPriorityComparator
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.repository.RepositoryItemMock;
import spock.lang.specification.BBBExtendedSpec;

/**
 * 
 * @author Velmurugan Moorthy
 * 
 * This class is created to unit test the class - RepositoryPriorityComparator
 *
 */

class RepositoryPriorityComparatorSpecification extends BBBExtendedSpec {

	RepositoryPriorityComparator repositoryPriorityObjMock
	
	def setup() {
		
		repositoryPriorityObjMock = new RepositoryPriorityComparator()
	}
	
	def "compare - happy flow (all inputs are valid)" () {
		
		given : 
				repositoryPriorityObjMock = Spy()
				
				def repoItemId1 = "repoItem001"
				def repoItemId2 = "repoItem002"
				
				RepositoryItemMock inputRepoItem1 = new RepositoryItemMock(["id":repoItemId1])
				RepositoryItemMock inputRepoItem2 = new RepositoryItemMock(["id":repoItemId2])
			
				inputRepoItem1.setProperties(["priority":2]);
				inputRepoItem2.setProperties(["priority":2]);
		
		when : 
		
			 def result = repositoryPriorityObjMock.compare(inputRepoItem1, inputRepoItem2)
			 
		then : 
			
			result == 0
		
	}
	
	def "compare - repository priority is not set(null) only for the first input repository item" () {
		
		given :
				repositoryPriorityObjMock = Spy()
				
				def repoItemId1 = "repoItem001"
				def repoItemId2 = "repoItem002"
				
				RepositoryItemMock inputRepoItem1 = new RepositoryItemMock(["id":repoItemId1])
				RepositoryItemMock inputRepoItem2 = new RepositoryItemMock(["id":repoItemId2])
			
				inputRepoItem1.getPropertyValue(BBBCatalogConstants.PROPERTY_NAME_PRIORITY) >> null 
				inputRepoItem2.setProperties(["priority":2]);
		
		when :
		
			 def result = repositoryPriorityObjMock.compare(inputRepoItem1, inputRepoItem2)
			 
		then :
			
			result == -1
		
	}
	
	def "compare - repository priority is not set(null) for both input repository items" () {
		
		given :
				repositoryPriorityObjMock = Spy()
				
				def repoItemId1 = "repoItem001"
				def repoItemId2 = "repoItem002"
				
				RepositoryItemMock inputRepoItem1 = new RepositoryItemMock(["id":repoItemId1])
				RepositoryItemMock inputRepoItem2 = new RepositoryItemMock(["id":repoItemId2])
			
				inputRepoItem1.setProperties(["priority":2]);
				inputRepoItem2.getPropertyValue(BBBCatalogConstants.PROPERTY_NAME_PRIORITY) >> null
		
		when :
		
			 def result = repositoryPriorityObjMock.compare(inputRepoItem1, inputRepoItem2)
			 
		then :
			
			result == 1
		
	}
	
}
