package com.bbb.utils

import com.bbb.commerce.catalog.vo.ShipMethodVO
import spock.lang.specification.BBBExtendedSpec

class DeliveryChargeComparatorSpecification extends BBBExtendedSpec {

	ShipMethodVO sVO =new ShipMethodVO()
	ShipMethodVO sVO1=new ShipMethodVO()
	DeliveryChargeComparator dComparator
	 def setup() {
		 dComparator = new DeliveryChargeComparator()
	}
	def"This method is used to compare shipping method delivry charge"(){
		given:
		sVO.setDeliverySurcharge(10)
		sVO1.setDeliverySurcharge(11)
		when:
		int value = dComparator.compare(sVO1, sVO)
		then:
		value == 1
	}
	def"This method is used to compare shipping method delivry charge,return -1"(){
		given:
		sVO.setDeliverySurcharge(12)
		sVO1.setDeliverySurcharge(11)
		when:
		int value = dComparator.compare(sVO1, sVO)
		then:
		value == -1
	}
}
