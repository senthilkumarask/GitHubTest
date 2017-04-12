package com.bbb.commerce.order.droplet

import com.bbb.rest.checkout.vo.AppliedCouponListVO
import spock.lang.specification.BBBExtendedSpec

class UserCouponsComparatorSpecification extends BBBExtendedSpec {
	def UserCouponsComparator uccObject
	AppliedCouponListVO listVO1 = new AppliedCouponListVO()
	AppliedCouponListVO listVO2 = new AppliedCouponListVO()
	
	def setup(){
	uccObject = Spy()
	}
	
	def "compare .TC when expiry date of listVO1 is less then expiry date of list vo2" (){
		given:
		String siteId = "BedBathCanada"
		listVO1.setExpiryDate("04/06/2005")
		listVO2.setExpiryDate("04/06/2006")
		uccObject.getSiteId() >> siteId
		
		when:
		int value = uccObject.compare(listVO1, listVO2 )
		then:
		value == -1
	}
	
	def "compare .TC when expiry date of listVO1 is greater then expiry date of list vo2" (){
		given:
		String siteId = "TBS_BedBathCanada"
		listVO2.setExpiryDate("04/06/2005")
		listVO1.setExpiryDate("04/06/2006")
		uccObject.getSiteId() >> siteId
		
		when:
		int value = uccObject.compare(listVO1, listVO2 )
		then:
		value == 1
	}
	
	def "compare .TC when expiry date of listVO1 is equals to expiry date of list vo2" (){
		given:
		String siteId = "TBS_BedBathCanada"
		listVO2.setExpiryDate("04/06/2005")
		listVO1.setExpiryDate("04/06/2005")
		uccObject.getSiteId() >> siteId
		
		when:
		int value = uccObject.compare(listVO1, listVO2 )
		then:
		value == 0
	}
	
	def "compare .TC when site id is not in (TBS_BedBathCanada and BedBathCanada )" (){
		given:
		String siteId = "BedBathUS"
		listVO2.setExpiryDate("04/06/2005")
		listVO1.setExpiryDate("04/06/2006")
		uccObject.getSiteId() >> siteId
		
		when:
		int value = uccObject.compare(listVO1, listVO2 )
		then:
		value == 1
	}
	
	def "compare .TC when expiry date of listVO1 is null" (){
		given:
		String siteId = "BedBathUS"
		listVO2.setExpiryDate("04/06/2005")
		listVO1.setExpiryDate(null)
		uccObject.getSiteId() >> siteId
		
		when:
		int value = uccObject.compare(listVO1, listVO2 )
		then:
		value == 0
	}
	def "compare .TC when expiry date of listVO2 is null" (){
		given:
		String siteId = "BedBathUS"
		listVO2.setExpiryDate(null)
		listVO1.setExpiryDate("04/06/2005")
		uccObject.getSiteId() >> siteId
		
		when:
		int value = uccObject.compare(listVO1, listVO2 )
		then:
		value == 0
	}
	
	def "compare .TC for parse exception" (){
		given:
		
		String siteId = "BedBathUS"
		listVO2.setExpiryDate("")
		listVO1.setExpiryDate("")
		uccObject.getSiteId() >> siteId
		
		when:
		int value = uccObject.compare(listVO1, listVO2 )
		then:
		value == 0
	}
	
	def "compare .TC for parse exception when expiry date of vo1 is empty" (){
		given:
		
		String siteId = "BedBathUS"
		listVO1.setExpiryDate("04/06/2005")
		listVO2.setExpiryDate("")
		uccObject.getSiteId() >> siteId
		
		when:
		int value = uccObject.compare(listVO1, listVO2 )
		then:
		value == 0
	}

}
