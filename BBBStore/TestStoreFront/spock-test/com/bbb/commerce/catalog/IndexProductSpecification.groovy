package com.bbb.commerce.catalog

import atg.nucleus.Nucleus
import atg.nucleus.registry.NucleusRegistry
import atg.repository.NamedQueryView
import atg.repository.Query
import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemImpl;
import spock.lang.specification.BBBExtendedSpec;

class IndexProductSpecification extends BBBExtendedSpec {

	IndexProduct index
	RepositoryItemImpl pItem =Mock()
	String pValue= new String("val")
	NucleusRegistry sNucleusRegistry = Mock()
	Nucleus nucleusMock = Mock()
	Repository rep =Mock()
	RepositoryItem r1 =Mock()
	RepositoryItem r2 =Mock()
	RepositoryItem r3 =Mock()
	RepositoryItem r4 =Mock()

	def setup(){
		index = Spy()
		Nucleus.setNucleusRegistry(sNucleusRegistry)
		sNucleusRegistry.getKey() >> "nKey"
		sNucleusRegistry.get("nKey") >> nucleusMock
		Nucleus.sUsingChildNucleii = true
	}

	def"getPropertyValue, when COLLECTION_PRODUCT_PROPERTY_NAME is true"(){

		given:
		1*pItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> true
		0*pItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> false

		when:
		Boolean obj = index.getPropertyValue(pItem, pValue)

		then:
		obj == true
	}

	def"getPropertyValue, when LEAD_PRODUCT_PRODUCT_PROPERTY_NAME is true"(){

		given:
		1*pItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> false
		1*pItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> true

		when:
		Boolean obj = index.getPropertyValue(pItem, pValue)

		then:
		obj == true
	}

	def"getPropertyValue, returns the indexed product"(){

		given:
		NamedQueryView view =Mock()
		Query query =Mock()
		RepositoryItem productRepositoryItem= Mock()
		RepositoryItem productRepositoryItem1= Mock()

		1*pItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> false
		1*pItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> false

		nucleusMock.resolveName("/atg/commerce/catalog/ProductCatalog") >> rep
		1*rep.getView(BBBCatalogConstants.PRODUCT_RELATION_ITEM_DESCRIPTOR) >> view
		1*view.getNamedQuery(BBBCatalogConstants.INDEX_RQL_GET_PROD_RELATION_BY_CHILD_PROD_ID) >> query
		1*index.extractDBCall(_, view, query) >> [r1,r2,r3]

		//for isChildOfALeadProduct
		r1.getRepositoryId() >>"rId_1"
		rep.getItem("rId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItem
		productRepositoryItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> false

		r2.getRepositoryId() >>"rId2_2"
		rep.getItem("rId2",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> {throw new RepositoryException("")}

		r3.getRepositoryId() >>"rId3_3"
		rep.getItem("rId3",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> productRepositoryItem1
		productRepositoryItem1.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> true
		//ends isChildOfALeadProduct

		1*r1.getPropertyValue(BBBCatalogConstants.LIKE_UNLIKE_COLL_PRODUCT_PROPERTY_NAME) >> true
		1*r2.getPropertyValue(BBBCatalogConstants.LIKE_UNLIKE_COLL_PRODUCT_PROPERTY_NAME) >> true
		0*r3.getPropertyValue(BBBCatalogConstants.LIKE_UNLIKE_COLL_PRODUCT_PROPERTY_NAME) >> false

		when:
		Object obj = index.getPropertyValue(pItem, pValue)

		then:
		obj == true
	}

	def"getPropertyValue,when product is not a child of Lead product and likeUnlike property is false"(){

		given:
		NamedQueryView view =Mock()
		Query query =Mock()

		1*pItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> false
		1*pItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> false
		nucleusMock.resolveName("/atg/commerce/catalog/ProductCatalog") >> rep
		1*rep.getView(BBBCatalogConstants.PRODUCT_RELATION_ITEM_DESCRIPTOR) >> view
		1*view.getNamedQuery(BBBCatalogConstants.INDEX_RQL_GET_PROD_RELATION_BY_CHILD_PROD_ID) >> query
		1*index.extractDBCall(_, view, query) >> [r1]

		//for isChildOfALeadProduct
		r1.getRepositoryId() >>"rId_1"
		rep.getItem("rId",BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR) >> null
		//ends isChildOfALeadProduct

		1*r1.getPropertyValue(BBBCatalogConstants.LIKE_UNLIKE_COLL_PRODUCT_PROPERTY_NAME) >> false

		when:
		Object obj = index.getPropertyValue(pItem, pValue)

		then:
		obj == true
	}

	def"getPropertyValue, when query is null(executeConfigNamedRQLQuery)"(){

		given:
		NamedQueryView view =Mock()

		1*pItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> false
		1*pItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> false
		nucleusMock.resolveName("/atg/commerce/catalog/ProductCatalog") >> rep
		1*rep.getView(BBBCatalogConstants.PRODUCT_RELATION_ITEM_DESCRIPTOR) >> view
		1*view.getNamedQuery(BBBCatalogConstants.INDEX_RQL_GET_PROD_RELATION_BY_CHILD_PROD_ID) >> null
		0*index.extractDBCall(_, view, _)

		when:
		Object obj = index.getPropertyValue(pItem, pValue)

		then:
		obj == true
	}

	def"getPropertyValue, when view is null(executeConfigNamedRQLQuery)"(){

		given:
		NamedQueryView view =Mock()

		1*pItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> false
		1*pItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> false
		nucleusMock.resolveName("/atg/commerce/catalog/ProductCatalog") >> rep
		1*rep.getView(BBBCatalogConstants.PRODUCT_RELATION_ITEM_DESCRIPTOR) >> null
		0*view.getNamedQuery(BBBCatalogConstants.INDEX_RQL_GET_PROD_RELATION_BY_CHILD_PROD_ID)
		0*index.extractDBCall(_, view, _)

		when:
		Object obj = index.getPropertyValue(pItem, pValue)

		then:
		obj == true
	}

	def"getPropertyValue, when list returned is empty(executeConfigNamedRQLQuery)"(){

		given:
		NamedQueryView view =Mock()
		Query query =Mock()

		1*pItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> false
		1*pItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> false
		nucleusMock.resolveName("/atg/commerce/catalog/ProductCatalog") >> rep
		1*rep.getView(BBBCatalogConstants.PRODUCT_RELATION_ITEM_DESCRIPTOR) >> view
		1*view.getNamedQuery(BBBCatalogConstants.INDEX_RQL_GET_PROD_RELATION_BY_CHILD_PROD_ID) >> query
		1*index.extractDBCall(_, view, _) >> []

		when:
		Object obj = index.getPropertyValue(pItem, pValue)

		then:
		obj == true
	}

	def"getPropertyValue, when RepositoryException is thrown in executeConfigNamedRQLQuery"(){

		given:
		NamedQueryView view =Mock()
		Query query =Mock()

		1*pItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME) >> false
		1*pItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME) >> false
		nucleusMock.resolveName("/atg/commerce/catalog/ProductCatalog") >> rep
		1*rep.getView(BBBCatalogConstants.PRODUCT_RELATION_ITEM_DESCRIPTOR) >> view
		1*view.getNamedQuery(BBBCatalogConstants.INDEX_RQL_GET_PROD_RELATION_BY_CHILD_PROD_ID) >> query
		1*index.extractDBCall(_, view, _) >> {throw new RepositoryException("")}

		when:
		Object obj = index.getPropertyValue(pItem, pValue)

		then:
		obj == true
	}

}
