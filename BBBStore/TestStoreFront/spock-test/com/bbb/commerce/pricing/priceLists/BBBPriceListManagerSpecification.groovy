package com.bbb.commerce.pricing.priceLists
import atg.commerce.pricing.priceLists.PriceListException
import atg.repository.MutableRepository;
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import spock.lang.specification.BBBExtendedSpec

class BBBPriceListManagerSpecification extends BBBExtendedSpec {

	def RepositoryItem pPriceList = Mock()
	def RepositoryItem priceMock = Mock()
	def BBBPriceListManager priceListManangerObj
	def MutableRepository priceListRep= Mock()

	String skuId ="23654"
	String pProductid ="12345"
	String pPriceListId="235"
	String pParentSkuId = "23222"
	boolean useCache=true


	def setup(){

		priceListManangerObj = Spy(BBBPriceListManager)

	}



	def "getPriceOnBasisOfSKU, This method will test whether the callSuperGetSkuPrice method returns a price for skuId"(){

		given:
		priceMock.getItemDisplayName() >> "BBBItem"
		priceListManangerObj.callSuperGetSkuPrice(pPriceList, skuId) >> {return priceMock}

		when:
		RepositoryItem price = priceListManangerObj.getPriceOnBasisOfSKU(pPriceList, skuId)

		then:
		price.getItemDisplayName() == "BBBItem"

	}

	def "getPriceOnBasisOfSKU, This method will trace the exception flow if price is not returned from super call"(){

		given:
		priceListManangerObj.callSuperGetSkuPrice(pPriceList, "skuId") >> {throw new Exception("exception")}

		when:
		RepositoryItem returnItem = priceListManangerObj.getPriceOnBasisOfSKU(pPriceList, "skuId")

		then:
		PriceListException exception = thrown()

	}

	def"getPrice(pPriceList, pProductid,skuId,useCache), test whether the price is retreievd from getPriceOnBasisOfSKU method"(){

		given:
		priceMock.getItemDisplayName() >> "BBBItem"
		priceListManangerObj.getPriceOnBasisOfSKU(pPriceList, skuId) >> {return priceMock}

		when:
		RepositoryItem price = priceListManangerObj.getPrice(pPriceList, pProductid,skuId,useCache)

		then:
		price.getItemDisplayName() == "BBBItem"
	}

	def "getPrice(pPriceList,pProductid,skuId,useCache), This method will trace the exception flow if price is not returned from getPriceOnBasisOfSKU call"(){

		given:
		priceListManangerObj = Spy(BBBPriceListManager)
		priceListManangerObj.getPriceOnBasisOfSKU(pPriceList, "skuId") >> {throw new Exception("exception")}

		when:
		RepositoryItem price = priceListManangerObj.getPrice(pPriceList,pProductid,skuId,useCache)

		then:
		PriceListException exception = thrown()

	}

	def"getPrice(pPriceListId, pProductid,skuId,pParentSkuId,useCache),  tests if exception is thrown if the priceList is not obatined from the priceListRepository "(){

		given:
		priceListManangerObj.getPriceListRepository() >> priceListRep
		priceListManangerObj.getPriceListItemType() >> "priceListItemType"
		priceListRep.getItem(pPriceListId, "priceListItemType") >> {throw new RepositoryException("exception")}


		when:
		priceListManangerObj.getPrice(pPriceListId, pProductid,skuId,pParentSkuId,useCache)

		then:
		PriceListException exception = thrown()
	}

	def "getPrice(pPriceListId, pProductid,skuId,pParentSkuId,useCache), uses the priceList obatined from PriceList Repository and tests if the price is returned by calling getPriceOnBasisOfSKU(pPriceList, skuId)"(){

		given:
		priceListManangerObj.getPriceListRepository() >> priceListRep
		priceListManangerObj.getPriceListItemType() >> "priceListItemType"
		priceListRep.getItem(pPriceListId, "priceListItemType") >> pPriceList
		priceListManangerObj.getPriceOnBasisOfSKU(pPriceList, skuId) >> priceMock
		priceMock.getItemDisplayName() >> "BBBItem"

		when:
		RepositoryItem price = priceListManangerObj.getPrice(pPriceListId, pProductid,skuId,pParentSkuId,useCache)

		then:
		price.getItemDisplayName() == "BBBItem"

	}
	def "getPrice(pPriceListId, pProductid,skuId,pParentSkuId,useCache), uses the priceList obatined from PriceList Repository and tests if the price is returned null and traces the exception flow"(){

		given:
		priceListManangerObj.getPriceListRepository() >> priceListRep
		priceListManangerObj.getPriceListItemType() >> "priceListItemType"
		priceListRep.getItem(pPriceListId, "priceListItemType") >> pPriceList
		priceListManangerObj.getPriceOnBasisOfSKU(pPriceList, "skuId") >> {throw new Exception("exception")}


		when:
		RepositoryItem price = priceListManangerObj.getPrice(pPriceListId, pProductid,skuId,pParentSkuId,useCache)

		then:
		PriceListException exception = thrown()

	}

	def"getPrice(pPriceListId, pProductid,skuId,useCache),  tests if exception is thrown if the priceList is not obtained from the priceListRepository "(){

		given:
		priceListManangerObj.getPriceListRepository() >> priceListRep
		priceListManangerObj.getPriceListItemType() >> "priceListItemType"
		priceListRep.getItem(pPriceListId, "priceListItemType") >> {throw new RepositoryException("exception")}


		when:
		priceListManangerObj.getPrice(pPriceListId, pProductid,skuId,useCache)

		then:
		PriceListException exception = thrown()
	}

	def "getPrice(pPriceListId, pProductid,skuId,useCache), uses the priceList obatined from PriceList Repository and tests if the price is returned by passing priceList and skuId as parameters in getPriceOnBasisOfSKU method"(){

		given:
		priceListManangerObj.getPriceListRepository() >> priceListRep
		priceListManangerObj.getPriceListItemType() >> "priceListItemType"
		priceListRep.getItem(pPriceListId, "priceListItemType") >> pPriceList
		priceListManangerObj.getPriceOnBasisOfSKU(pPriceList, skuId) >> {return priceMock}
		priceMock.getItemDisplayName() >> "BBBItem"

		when:
		RepositoryItem price = priceListManangerObj.getPrice(pPriceListId, pProductid,skuId,useCache)

		then:
		price.getItemDisplayName() == "BBBItem"

	}
	def "getPrice(pPriceListId, pProductid,skuId,useCache), uses the priceList obatined from PriceList Repository and executes the exception flow if price obtained is null on calling getPriceOnBasisOfSKU(PriceList, skuId) method "(){

		given:
		priceListManangerObj.getPriceListRepository() >> priceListRep
		priceListManangerObj.getPriceListItemType() >> "priceListItemType"
		priceListRep.getItem(pPriceListId, "priceListItemType") >> pPriceList
		priceListManangerObj.getPriceOnBasisOfSKU(pPriceList, skuId) >> {throw new Exception("exception")}


		when:
		RepositoryItem price = priceListManangerObj.getPrice(pPriceListId, pProductid,skuId,useCache)

		then:
		PriceListException exception = thrown()

	}

	def "getPrice(pPriceList, pProductid,skuId,pParentSkuId,useCache), uses the priceList obatined from PriceList Repository and tests if the price is returned "(){

		given:
		priceListManangerObj.getPriceOnBasisOfSKU(pPriceList, skuId) >> {return priceMock}
		priceMock.getItemDisplayName() >> "BBBItem"

		when:
		RepositoryItem price = priceListManangerObj.getPrice(pPriceList, pProductid,skuId,pParentSkuId,useCache)

		then:
		price.getItemDisplayName() == "BBBItem"

	}

	def"getPrice(pPriceList, pProductid,skuId,pParentSkuId,useCache),  tests if exception is thrown if the priceList is not obatined from the priceListRepository "(){

		given:
		priceListManangerObj.getPriceOnBasisOfSKU(pPriceList, skuId) >> {throw new Exception("exception")}


		when:
		priceListManangerObj.getPrice(pPriceList, pProductid,skuId,pParentSkuId,useCache)

		then:
		PriceListException exception = thrown()
	}



}
