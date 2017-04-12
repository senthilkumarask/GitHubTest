package com.bbb.commerce.catalog

import atg.nucleus.Nucleus;
import atg.nucleus.registry.NucleusRegistry
import atg.repository.Repository;
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemImpl;
import spock.lang.specification.BBBExtendedSpec;

class ChildSkusSpecification extends BBBExtendedSpec{

	ChildSkus sku
	RepositoryItemImpl pItem =Mock()
	NucleusRegistry sNucleusRegistry = Mock()
	Repository catalogRepository =Mock()
	Nucleus nucleusMock = Mock()
	String pValue = new String("abc")

	def setup(){
		sku = new ChildSkus()
		Nucleus.setNucleusRegistry(sNucleusRegistry)
		sNucleusRegistry.getKey() >> "nKey"
		sNucleusRegistry.get("nKey") >> nucleusMock
		Nucleus.sUsingChildNucleii = true
		sku.setCatalogRepository(null)
	}

	def"getPropertyValue, returns the childSkuList"(){

		given:
		RepositoryItem rItem =Mock()
		RepositoryItem r1 =Mock()
		RepositoryItem r2 =Mock()
		RepositoryItem childProdItem =Mock()
		RepositoryItem r4 =Mock()

		1*pItem.getRepositoryId() >> "rId"
		1*nucleusMock.resolveName("/atg/commerce/catalog/ProductCatalog") >> catalogRepository
		2*catalogRepository.getItem("rId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> rItem

		1*rItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> [r1]
		1*rItem.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> [r2]
		1*r2.getRepositoryId() >> "skuId"
		1*r1.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME) >> childProdItem
		childProdItem.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> [r4]
		1*r4.getRepositoryId() >> "skuId1"

		when:
		List<String> list = sku.getPropertyValue(pItem,pValue)

		then:
		list == ["skuId","skuId1"]
	}

	def"getPropertyValue, when CHILD_SKU_PRODUCT_PROPERTY_NAME is empty"(){

		given:
		RepositoryItem rItem =Mock()
		RepositoryItem r1 =Mock()
		RepositoryItem childProdItem =Mock()

		1*pItem.getRepositoryId() >> "rId"
		1*nucleusMock.resolveName("/atg/commerce/catalog/ProductCatalog") >> catalogRepository
		2*catalogRepository.getItem("rId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> rItem

		1*rItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> [r1]
		1*rItem.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> []
		1*r1.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME) >> childProdItem
		childProdItem.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> null

		when:
		List<String> list = sku.getPropertyValue(pItem,pValue)

		then:
		list.isEmpty() 
	}

	def"getPropertyValue, when CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME is empty"(){

		given:
		RepositoryItem rItem =Mock()
		RepositoryItem r1 =Mock()
		RepositoryItem childProdItem =Mock()

		1*pItem.getRepositoryId() >> "rId"
		1*nucleusMock.resolveName("/atg/commerce/catalog/ProductCatalog") >> catalogRepository
		2*catalogRepository.getItem("rId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> rItem

		1*rItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME) >> []
		1*rItem.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME) >> []
		0*r1.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME)

		when:
		List<String> list = sku.getPropertyValue(pItem,pValue)

		then:
		list.isEmpty() 
	}

	def"getPropertyValue,when productRepositoryItem is null"(){

		given:
		RepositoryItem rItem =Mock()
		RepositoryItem r1 =Mock()
		RepositoryItem childProdItem =Mock()

		1*pItem.getRepositoryId() >> "rId"
		1*nucleusMock.resolveName("/atg/commerce/catalog/ProductCatalog") >> catalogRepository
		1*catalogRepository.getItem("rId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null

		0*rItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME)
		0*rItem.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME)
		0*r1.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME)

		when:
		List<String> list = sku.getPropertyValue(pItem,pValue)

		then:
		list.isEmpty()
	}

	def"getPropertyValue, when pItem is null"(){

		given:
		RepositoryItem rItem =Mock()
		RepositoryItem r1 =Mock()

		0*pItem.getRepositoryId()
		1*nucleusMock.resolveName("/atg/commerce/catalog/ProductCatalog") >> catalogRepository
		0*catalogRepository.getItem("rId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR)

		0*rItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME)
		0*rItem.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME)
		0*r1.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME)

		when:
		List<String> list = sku.getPropertyValue(null,pValue)

		then:
		list.isEmpty()
	}

	def"getPropertyValue, when RepositoryException is thrown"(){

		given:
		RepositoryItem rItem =Mock()
		RepositoryItem r1 =Mock()

		1*pItem.getRepositoryId() >> "rId"
		1*nucleusMock.resolveName("/atg/commerce/catalog/ProductCatalog") >> catalogRepository
		1*catalogRepository.getItem("rId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException("")}

		0*rItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME)
		0*rItem.getPropertyValue(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME)
		0*r1.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_PRODUCT_RELATION_PROPERTY_NAME)

		when:
		List<String> list = sku.getPropertyValue(pItem,pValue)

		then:
		list.isEmpty()
	}
}
