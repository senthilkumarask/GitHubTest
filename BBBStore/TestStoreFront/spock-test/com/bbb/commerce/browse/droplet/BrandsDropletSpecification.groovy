package com.bbb.commerce.browse.droplet

import atg.repository.seo.IndirectUrlTemplate
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.bbb.commerce.browse.vo.BrandsListingVO
import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.vo.BrandVO
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException;
import com.bbb.search.integration.SearchManager
import com.bbb.utils.BBBUtility;

import spock.lang.specification.BBBExtendedSpec
/**
 * 
 * @author Velmurugan Moorthy
 * 
 * Changes in Java file : 
 * 
 * #158 - if (listAllBrands != null) - condition removed 
 * As the null check is done before invoking the method processBrandResults()
 *
 */

public class BrandsDropletSpecification extends BBBExtendedSpec {
	

	private BrandsDroplet brandsDroplet
	private SearchManager searchManagerMock
	private BBBCatalogToolsImpl catalogToolsMock
	private IndirectUrlTemplate brandTemplateMock
	
	
	def setup() {
		
		searchManagerMock = Mock()
		catalogToolsMock = Mock()
		brandTemplateMock = Mock()
		
		brandsDroplet = new BrandsDroplet( [searchManager : searchManagerMock , mCatalogTools : catalogToolsMock , brandTemplate : brandTemplateMock ] )
		
	}
	
	/*
	 * Service - Test cases STARTS
	 * 
	 * Method signature : 
	 * 
	 * public void service ( 
	 *  DynamoHttpServletRequest request,
	 *  DynamoHttpServletResponse response
	 *  )
	 *   throws ServletException,IOException 
	 * 
	 */
	
	/*
	 * Alternative branches covered :
	 * 
	 *	#164 - if(!prefix.matches("[A-Za-z]") && BBBUtility.isInteger(prefix)) - all 4 branches 
	 *  #165 - if (numericBrandListMap.containsKey(prefix)) - both branches 
	 *  #177 - if (alphabetBrandListMap.containsKey(prefix)) - both branches
	 *  
	 */
	
	def "service -> processBrandResults -  Brands are retrieved successfully" () {
		
		given : 
		
		BrandsListingVO brandListingVO = new BrandsListingVO()
		
		BrandVO brand1 = new BrandVO()
		BrandVO brand2 = new BrandVO()
		BrandVO brand3 = new BrandVO()
		
		BrandVO brand4 = new BrandVO()
		BrandVO brand5 = new BrandVO()
		BrandVO brand6 = new BrandVO()
		BrandVO brand7 = new BrandVO()
		BrandVO brand8 = new BrandVO()
		
		List<BrandVO> activeBrandsList = new ArrayList<>()
		List<BrandVO> featuredBrands = new ArrayList<>()
		
		brand1.setBrandName("1Zurich")
		brand2.setBrandName("-Audi")
		brand3.setBrandName("1Ford")
		
		brand4.setBrandName("Kohler")
		brand5.setBrandName("Neycer")
		brand6.setBrandName("Lenovo")
		
		brand7.setBrandName("Ford")
		brand8.setBrandName("Felix")
		
		activeBrandsList.addAll([brand1, brand2, brand3, brand7, brand8])
		featuredBrands.addAll([brand4, brand5, brand6])
		
		brandListingVO.setListBrands(activeBrandsList) 
		brandListingVO.setListFeaturedBrands(featuredBrands)
		
		1 * searchManagerMock.getAllBrands() >> brandListingVO
		
		when : 
		
		brandsDroplet.service(requestMock, responseMock)
		
		then :
		
		1 * requestMock.setParameter("featuredBrands", _)
		1 * requestMock.setParameter("alphabetBrandListMap", _)
		
	}
	
	/*
	 * Alternative branches covered :
	 *
	 *
	 */
	
	def "service -> processBrandResults -  Exception while getting brands | BBBBusinessException" () {
		
		given :
		
		1 * searchManagerMock.getAllBrands() >> {throw new BBBBusinessException("")}
		
		when : 
		
		
		brandsDroplet.service(requestMock, responseMock)
		
		then :
		
		1 * requestMock.setParameter("error", "err_brand_system_error")
		1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
		
	}
	
	def "service -> processBrandResults -  Exception while getting brands | BBBSystemException" () {
		
		given :
		
		1 * searchManagerMock.getAllBrands() >> {throw new BBBSystemException("")}
		
		when :
		
		
		brandsDroplet.service(requestMock, responseMock)
		
		then :
		
		1 * requestMock.setParameter("error", "err_brand_system_error")
		1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
	}
	
	/*
	 * Alternative branches covered : 
	 * 
	 * #114 -  if(activeBrandsList != null)
	 *  	
	 */
	def "service -> processBrandResults -  Active brands are empty" () {
		
		given :
		
		BrandsListingVO brandListingVO = new BrandsListingVO()
		
		1 * searchManagerMock.getAllBrands() >> brandListingVO
		
		when : 
		
		brandsDroplet.service(requestMock, responseMock)
		
		then :
		
		0 * requestMock.setParameter("featuredBrands", _)
		0 * requestMock.setParameter("alphabetBrandListMap", _)
		
	}
	
	/*
	 * Service - Test cases ENDS
	 *
	 */
	
	
	/*
	 * getAllBrands - test cases STARTS
	 *
	 * Method signature :
	 *
	 * public BrandsListingVO getAllBrands() throws BBBSystemException
	 *
	 */
	
	def "getAllBrands - All brands retrieved successfully" () {
		
		given :
		
		BrandVO brand1 = new BrandVO()
		BrandVO brand2 = new BrandVO()
		BrandVO brand3 = new BrandVO()
		
		List<BrandVO>  featuredBrands = new ArrayList<>()
		List<BrandVO>  alphabetBrandList = new ArrayList<>()
		List<BrandVO>  numericBrandList  = new ArrayList<>()
		Map<String, ArrayList<BrandVO>>  alphabetBrandListMap = new TreeMap<String, ArrayList<BrandVO>>()
		Map<String, ArrayList<BrandVO>>  numericBrandListMap = new TreeMap<String, ArrayList<BrandVO>>()
		
		BrandsListingVO brandsListing = new BrandsListingVO()
		
		brand1.setBrandName("Zurich")
		brand2.setBrandName("Audi")
		brand3.setBrandName("Ford")
		
		alphabetBrandListMap.put("alphabetBrandList", featuredBrands)
		numericBrandListMap.put("numberBrandList", alphabetBrandList)
		featuredBrands.addAll([brand1, brand2, brand3])
		
		requestMock.getObjectParameter("alphabetBrandListMobileMap") >> alphabetBrandListMap
		requestMock.getObjectParameter("numericBrandListMobileMap") >> numericBrandListMap
		requestMock.getObjectParameter("featuredBrands") >> featuredBrands
		
		when :
		
		brandsListing = brandsDroplet.getAllBrands()
		
		then :
		
		brandsListing.getListFeaturedBrands().equals(featuredBrands)
		brandsListing.getAlphabetBrandListMap().equals(alphabetBrandListMap)
		brandsListing.getNumericBrandListMap() == numericBrandListMap
		
	}
	/*
	 * Alternative branches covered : 
	 * #228 - if (brandMap != null) 
	 */
	def "getAllBrands - Brand information is not available - brandMap is null" () {
		
		given :
		
		BrandsListingVO brandsListing = new BrandsListingVO()
		
		when :
		
		brandsListing = brandsDroplet.getAllBrands()
		
		then :
		
		brandsListing.getListFeaturedBrands() == null
		brandsListing.getAlphabetBrandListMap() == null
		brandsListing.getNumericBrandListMap() == null
		
	}
	
	def "getAllBrands - Servlet Exception while retrieving brands" () {
		
		given :
		
		BrandsDroplet brandsDropletSpy = Spy()
		BrandsListingVO brandsListing = new BrandsListingVO()
		
		brandsDropletSpy.service(requestMock, responseMock) >> {throw new ServletException("Exception while retrieving brands")}
		
		when :
		
		brandsListing = brandsDropletSpy.getAllBrands()
		
		then :
		
		thrown BBBSystemException
		brandsListing.getListFeaturedBrands() == null
		brandsListing.getAlphabetBrandListMap() == null
		brandsListing.getNumericBrandListMap() == null
	}
	
	def "getAllBrands - IOException while retrieving brands" () {
		
		given :
		
		BrandsDroplet brandsDropletSpy = Spy()
		BrandsListingVO brandsListing = new BrandsListingVO()
		
		brandsDropletSpy.service(requestMock, responseMock) >> {throw new IOException("Exception while retrieving brands")}
		
		when :
		
		brandsListing = brandsDropletSpy.getAllBrands()
		
		then :
		
		thrown BBBSystemException
		brandsListing.getListFeaturedBrands() == null
		brandsListing.getAlphabetBrandListMap() == null
		brandsListing.getNumericBrandListMap() == null
	}
	
	
	/*
	 * getAllBrands - test cases ENDS
	 *
	 */
	
	

}
