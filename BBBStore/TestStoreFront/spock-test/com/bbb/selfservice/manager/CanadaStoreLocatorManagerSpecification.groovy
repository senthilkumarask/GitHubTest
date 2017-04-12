package com.bbb.selfservice.manager

import java.util.List;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.StoreVO;

import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class CanadaStoreLocatorManagerSpecification extends BBBExtendedSpec {
	
	CanadaStoreLocatorManager testObj
	BBBCatalogTools catalogToolsMock = Mock()
	
	def setup(){
		testObj = new CanadaStoreLocatorManager(catalogTools:catalogToolsMock)
	}
	
	def"getCanadaStoreLocator. This TC is the Happy Flow of getCanadaStoreLocator"(){
		given:
			StoreVO storeVOMock = new StoreVO()
			StoreVO storeVOMock1 = new StoreVO()
			1 * catalogToolsMock.getCanadaStoreLocatorInfo() >> [storeVOMock,storeVOMock1]
		when:
			List<StoreVO> results = testObj.getCanadaStoreLocator()
		then:
			results.equals([storeVOMock,storeVOMock1]) == true
	}

}
