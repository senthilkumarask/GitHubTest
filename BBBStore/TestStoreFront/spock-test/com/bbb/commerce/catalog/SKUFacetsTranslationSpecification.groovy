package com.bbb.commerce.catalog

import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemImpl
import atg.repository.RepositoryView

import java.util.ArrayList;
import java.util.List;

import com.bbb.repository.RepositoryItemMock
import spock.lang.specification.BBBExtendedSpec;

/**
 *
 * @author Velmurugan Moorthy
 *
 * Changes made in Java file :
 *
 * #27 - getSkuFacetRepository() extracted (created)
 * #46 - invokeSkuFacetRepoQuery() extracted (created)
 */

public class SKUFacetsTranslationSpecification extends BBBExtendedSpec {

	private SKUFacetsTranslation skuFacetsTranslation
	private SKUFacetsTranslation skuFacetsTranslationSpy
	
	def setup() {
		
		skuFacetsTranslationSpy = Spy()
		
	}

	/*
	 *  getPropertyValue - Test cases STARTS
	 *
	 *  Method signature :
	 *
	 *  public Object getPropertyValue(RepositoryItemImpl pItem, Object pValue)
	 *
	 */

	def "getPropertyValue - Getting facet translations for a SKU successfully" () {
		
		given :
		
		//SKUFacetsTranslation skuFacetsTranslationSpy = Spy()
		Repository skuFacetRepositoryMock = Mock()
		RepositoryView skuFacetViewMock = Mock()
		RepositoryItemImpl pItem = Mock()
		RepositoryItemMock skuFacetItem1 = new RepositoryItemMock(["id" :"skuFacet01"])
		RepositoryItemMock skuFacetItem2 = new RepositoryItemMock(["id" :"skuFacet02"])
		RepositoryItemMock[] skuFacetItems = [skuFacetItem1, skuFacetItem2]
		List<RepositoryItem> skuFacetList = (List<RepositoryItem>) new ArrayList()
		
		skuFacetRepositoryMock.getView("skuFacet") >> skuFacetViewMock
		
		1 * skuFacetsTranslationSpy.getSkuFacetRepository() >> skuFacetRepositoryMock
		1 * skuFacetsTranslationSpy.invokeSkuFacetRepoQuery(skuFacetViewMock, _, _) >> skuFacetItems
		pItem.getRepositoryId() >> "sku01"
		
		when :
		
		skuFacetList = skuFacetsTranslationSpy.getPropertyValue(pItem, skuFacetViewMock)
		
		then :
		
		skuFacetList.contains(skuFacetItem1)
		skuFacetList.contains(skuFacetItem2)
	}
	
	def "getPropertyValue - Exception while getting facet translations for a SKU | RepositoryException" () {
		
		given :
		
		Repository skuFacetRepositoryMock = Mock()
		RepositoryView skuFacetViewMock = Mock()
		RepositoryItemImpl pItem = Mock()
		RepositoryItemMock skuFacetItem1 = new RepositoryItemMock(["id" :"skuFacet01"])
		RepositoryItemMock skuFacetItem2 = new RepositoryItemMock(["id" :"skuFacet02"])
		RepositoryItemMock[] skuFacetItems = [skuFacetItem1, skuFacetItem2]
		List<RepositoryItem> skuFacetList = (List<RepositoryItem>) new ArrayList()
		
		skuFacetRepositoryMock.getView("skuFacet") >> skuFacetViewMock
		
		1 * skuFacetsTranslationSpy.getSkuFacetRepository() >> skuFacetRepositoryMock
		1 * skuFacetsTranslationSpy.invokeSkuFacetRepoQuery(skuFacetViewMock, _, _) >> {throw new RepositoryException("")}
		pItem.getRepositoryId() >> "sku01"
		
		when :
		
		skuFacetList = skuFacetsTranslationSpy.getPropertyValue(pItem, skuFacetViewMock)
		
		then :
		
		skuFacetList.isEmpty()
	}
	
	/*
	 *  getPropertyValue - Test cases STARTS
	 *  
	 */ 

}