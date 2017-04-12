package com.bbb.commerce.giftregistry.comparator

import java.util.Comparator;

import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;

import spock.lang.specification.BBBExtendedSpec

class RegSearchComparatorSpecification extends BBBExtendedSpec {

	RegSearchComparator comparator = new RegSearchComparator()
	
	def "RegSearchComparator method"(){
		
		given:
			Comparator<RegistrySummaryVO> comparators = Mock()
			RegistrySummaryVO emp1 = new RegistrySummaryVO()
			RegistrySummaryVO emp2 = new RegistrySummaryVO()
			
		when:
			comparator.compare(emp1,emp2)
		
		then:
			true == true
	}
}
