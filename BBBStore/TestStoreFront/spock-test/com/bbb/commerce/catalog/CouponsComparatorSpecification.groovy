package com.bbb.commerce.catalog

import com.bbb.account.vo.CouponListVo
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.TBSConstants;

import spock.lang.specification.BBBExtendedSpec;

class CouponsComparatorSpecification extends BBBExtendedSpec {

	CouponsComparator coupon
	CouponListVo obj1 = new CouponListVo()
	CouponListVo obj2 = new CouponListVo()

	def setup(){
		coupon = Spy()
	}

	def"compare, when date of obj1 is greater than date of obj2"(){

		given:
		obj1.setExpiryDate("27/12/2016")
		obj2.setExpiryDate("25/12/2016")
		coupon.extractSiteId() >> BBBCoreConstants.SITE_BAB_CA

		when:
		int value = coupon.compare(obj1, obj2)

		then:
		value == 1
	}

	def"compare,when date of obj1 is less than date of obj2"(){

		given:
		obj1.setExpiryDate("27/12/2016")
		obj2.setExpiryDate("28/12/2016")
		coupon.extractSiteId() >> TBSConstants.SITE_TBS_BAB_CA

		when:
		int value = coupon.compare(obj1, obj2)

		then:
		value == -1
	}

	def"compare,when date of obj1 is equal to date of obj2"(){

		given:
		obj1.setExpiryDate("11/27/2016")
		obj2.setExpiryDate("11/27/2016")
		coupon.extractSiteId() >> "can"

		when:
		int value = coupon.compare(obj1, obj2)

		then:
		value == 0
	}

	def"compare,when obj2 date is null"(){

		given:
		obj1.setExpiryDate("11/27/2016")
		obj2.setExpiryDate(null)
		coupon.extractSiteId() >> "can"

		when:
		int value = coupon.compare(obj1, obj2)

		then:
		value == 0
	}

	def"compare,when obj1 date is null"(){

		given:
		obj2.setExpiryDate("11/27/2016")
		obj1.setExpiryDate(null)
		coupon.extractSiteId() >> "can"

		when:
		int value = coupon.compare(obj1, obj2)

		then:
		value == 0
	}

	def"compare,when date is not in the correct format"(){

		given:
		obj1.setExpiryDate("27/12/2016")
		obj2.setExpiryDate("25/1016")
		coupon.extractSiteId() >> BBBCoreConstants.SITE_BAB_CA

		when:
		int value = coupon.compare(obj1, obj2)

		then:
		value == 0
	}

}
