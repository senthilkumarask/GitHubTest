package com.bbb.commerce.cart.droplet

import java.util.List

import com.bbb.constants.BBBCoreConstants
import com.bbb.wishlist.BBBWishlistManager
import spock.lang.specification.BBBExtendedSpec

class FetchSavedItemsProductIdsDropletSpecification extends BBBExtendedSpec  {

	def  FetchSavedItemsProductIdsDroplet sflDropletObject
	def BBBWishlistManager wishlistManagerMock = Mock()
	def setup(){
		sflDropletObject = new FetchSavedItemsProductIdsDroplet(wishListManager:wishlistManagerMock)
	}

	def "service Method. TC to fetch 'prodIdsList' OParam in request " (){
		given:
		wishlistManagerMock.getProdIDsSavedItems() >>"prodIdsList"
		sflDropletObject.setWishListManager(wishlistManagerMock)

		when:
		sflDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.setParameter(BBBCoreConstants.PRODUCT_ID_LIST, "prodIdsList")
		1*requestMock.serviceParameter(BBBCoreConstants.OUTPUT, requestMock, responseMock);
	}


	def "Fetching when the prodIdsList is null"(){
		given:
		sflDropletObject.setWishListManager(wishlistManagerMock)
		wishlistManagerMock.getProdIDsSavedItems() >> null

		when:
		sflDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.serviceParameter(BBBCoreConstants.EMPTY, requestMock, responseMock);
	}


	def "Fetching when the prodIdsList is empty"(){
		given:
		sflDropletObject.setWishListManager(wishlistManagerMock)
		wishlistManagerMock.getProdIDsSavedItems() >> ""

		when:
		sflDropletObject.service(requestMock, responseMock)
		then:
		1*requestMock.serviceParameter(BBBCoreConstants.EMPTY, requestMock, responseMock);
	}
}

