package com.bbb.commerce.checklist.droplet

import com.bbb.commerce.checklist.vo.CheckListSeoUrlHierarchy
import com.bbb.constants.BBBCoreConstants;
import com.bbb.search.bean.query.SearchQuery
import com.bbb.search.endeca.EndecaSearchUtil
import spock.lang.specification.BBBExtendedSpec

class ValidateCheckListCategoryDropletSpecification extends BBBExtendedSpec {
	ValidateCheckListCategoryDroplet testObj
	EndecaSearchUtil endecaSearchUtilMock =Mock()
	SearchQuery searchQueryMock =Mock()//new  SearchQuery()
	CheckListSeoUrlHierarchy checkListSeoUrlHierarchyMock =Mock()
	
	def setup(){
		testObj = new ValidateCheckListCategoryDroplet(searchUtil:endecaSearchUtilMock)
	}
	def"service-pass checkListSeoUrlHierarchy with values "(){
		given:
			testObj = Spy()
			testObj.setSearchUtil(endecaSearchUtilMock)
			endecaSearchUtilMock.getCheckListSeoDimId(searchQueryMock , requestMock)>>{}
			testObj.createCheckListSeoUrl(_) >> checkListSeoUrlHierarchyMock
			checkListSeoUrlHierarchyMock.isRegTypeCheckList() >>true
			checkListSeoUrlHierarchyMock.isCategoryEnabled() >> true
			checkListSeoUrlHierarchyMock.isCheckListDisabled() >> false
			checkListSeoUrlHierarchyMock.getCheckListDisplayName() >> "Wedding"
			checkListSeoUrlHierarchyMock.getCheckListCategoryName() >> "Fine Dining"
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter('isValidUrl', 'true')
			1*requestMock.setParameter('checkListDisplayName', 'Wedding')
			1*requestMock.setParameter('categoryDisplayName', 'Fine Dining')
			1*requestMock.setParameter('categoryEnable', 'true')
			1*requestMock.setParameter('checkListEnable', 'true')
	}
	def"service-pass checkListSeoUrlHierarchy has values with guest registry "(){
		given:
			testObj = Spy()
			testObj.setSearchUtil(endecaSearchUtilMock)
			endecaSearchUtilMock.getCheckListSeoDimId(searchQueryMock , requestMock)>>{}
			testObj.createCheckListSeoUrl(_) >> checkListSeoUrlHierarchyMock
			checkListSeoUrlHierarchyMock.isRegTypeCheckList() >>false
			checkListSeoUrlHierarchyMock.isCategoryEnabled() >> false
			checkListSeoUrlHierarchyMock.isCheckListDisabled() >> true
			checkListSeoUrlHierarchyMock.getCheckListDisplayName() >> "Wedding"
			checkListSeoUrlHierarchyMock.getCheckListCategoryName() >> "Fine Dining"
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter('isValidUrl', 'true')
			1*requestMock.setParameter('checkListDisplayName', 'Wedding')
			1*requestMock.setParameter('categoryDisplayName', 'Fine Dining')
			1*requestMock.setParameter('categoryEnable', null)
			1*requestMock.setParameter('checkListEnable', null)
		
	}
	def"service-pass checkListSeoUrlHierarchy as null"(){
		given:
			testObj = Spy()
			testObj.setSearchUtil(endecaSearchUtilMock)
			endecaSearchUtilMock.getCheckListSeoDimId(searchQueryMock , requestMock)>>{}
			testObj.createCheckListSeoUrl(_) >> null
			
		when:
			testObj.service(requestMock, responseMock)
		then:
			1*requestMock.setParameter('isValidUrl', 'false')
			1*requestMock.setParameter('checkListDisplayName', null)
			1*requestMock.setParameter('categoryDisplayName', null)
			1*requestMock.setParameter('categoryEnable', null)
			1*requestMock.setParameter('checkListEnable', null)
		
	}
}
