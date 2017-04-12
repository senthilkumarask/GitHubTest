package com.bbb.commerce.browse.droplet

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException

import com.bbb.commerce.catalog.BBBCatalogToolsImpl
import com.bbb.commerce.catalog.vo.BrandVO
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.search.bean.result.SortOptionVO;
import com.bbb.search.bean.result.SortOptionsVO
import com.bbb.utils.BBBUtility;

import atg.repository.seo.IndirectUrlTemplate
import atg.repository.seo.ItemLinkException
import atg.repository.seo.UrlParameter
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import spock.lang.specification.BBBExtendedSpec;

/**
 * 
 * @author Velmurugan Moorthy
 * 
 * Changes made in Java file : 
 * 
 *
 */

class BrandDetailDropletSpecification extends BBBExtendedSpec {
	
	private BrandDetailDroplet brandDetailDroplet
	private BBBCatalogToolsImpl catalogToolsMock
	private IndirectUrlTemplate brandTemplateMock
	
	def setup() {
		
		brandTemplateMock = Mock()
		catalogToolsMock = Mock()
		brandDetailDroplet = new BrandDetailDroplet([mCatalogTools : catalogToolsMock , brandTemplate : brandTemplateMock ])
		
	}
	
	
	
	/*
	 * service - test cases STARTS
	 * 
	 * Method signature : 
	 * 
	 * public void service ( 
	 * DynamoHttpServletRequest request,
	 * DynamoHttpServletResponse response
	 * ) throws ServletException, IOException 
	 * 
	 */
	
	def "service - brand details retrieved successfully" () {
		
		given : 
		
		BrandDetailDroplet brandDetailDropletSpy = Spy()
		
		def brandName = "BedBath" 
		def brandId = "bbb01"
		def seoUrl = "bedbath.com/pdp/prod01" 
		BrandVO brand = new BrandVO()
		BrandVO bccBrandVO = new BrandVO()
		SortOptionVO sortOptionVO = new SortOptionVO()
		
		brand.setBrandName(brandName)
		brand.setSeoURL(seoUrl)
		
		bccBrandVO.setBrandContent("Beb bath and beyond")
		bccBrandVO.setJsFilePath("/store/js/")
		bccBrandVO.setCssFilePath("/store/css/")
		bccBrandVO.setSortOptionVO(sortOptionVO)
				
		requestMock.getParameter("origBrandName") >> brandName
		requestMock.getParameter("keywordName") >> brandId
		1 * requestMock.setParameter("jsFilePath", bccBrandVO.getJsFilePath())
		1 * requestMock.setParameter("cssFilePath", bccBrandVO.getCssFilePath())
		
		populateBrandDetailDroplet(brandDetailDropletSpy)
		2 * brandDetailDropletSpy.generateSeoUrl(brandName) >> seoUrl
		1 * catalogToolsMock.getBrandDetails(brandId, _) >> brand
		1 * catalogToolsMock.getBccManagedBrand(brand.getBrandName(),"") >> bccBrandVO
		
		when : 
		
		brandDetailDropletSpy.service(requestMock, responseMock)
		
		then :
		 
		2 * requestMock.setParameter("seoUrl", brand.getSeoURL())
		2 * requestMock.serviceLocalParameter("seooutput", requestMock, responseMock)
		1 * requestMock.setParameter("isPromoContentAvailable", _)
		1 * requestMock.setParameter("sortOptionVO", bccBrandVO.getSortOptionVO())
		1 * requestMock.setParameter("brandName", brand.getBrandName())
		
	}
	
	/*
	 * Alternative brancehs covered : 
	 */
	
	def "service - Exception occured while getting brand details | BBBBusinessException" () {
		
		given :
		
		BrandDetailDroplet brandDetailDropletSpy = Spy()
		
		def brandName = "BedBath"
		def brandId = "bbb01"
		def seoUrl = "bedbath.com/pdp/prod01"
		BrandVO brand = new BrandVO()
		BrandVO bccBrandVO = new BrandVO()
		SortOptionVO sortOptionVO = new SortOptionVO()
		
		brand.setBrandName(brandName)
		brand.setSeoURL(seoUrl)
		
		bccBrandVO.setBrandContent("Beb bath and beyond")
		bccBrandVO.setJsFilePath("/store/js/")
		bccBrandVO.setCssFilePath("/store/css/")
		bccBrandVO.setSortOptionVO(sortOptionVO)
				
		requestMock.getParameter("origBrandName") >> brandName
		requestMock.getParameter("keywordName") >> brandId
		
		populateBrandDetailDroplet(brandDetailDropletSpy)
		2 * brandDetailDropletSpy.generateSeoUrl(brandName) >> seoUrl
		1 * catalogToolsMock.getBrandDetails(brandId, _) >> brand
		1 * catalogToolsMock.getBccManagedBrand(brand.getBrandName(),"") >> {throw new BBBBusinessException("Exception occured while getting brand details ")}
		
		when :
		
		brandDetailDropletSpy.service(requestMock, responseMock)
		
		then :
		
		1 * requestMock.setParameter("seoUrl", _)
		1 * requestMock.serviceLocalParameter("seooutput", requestMock, responseMock)
		1 * requestMock.setParameter("error", "err_brand_system_error")
		1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
		
	}

	/*
	 * Alternative brancehs covered :
	 */
	
	def "service - Exception occured while getting brand details | BBBSystemException" () {
		
		given :
		
		BrandDetailDroplet brandDetailDropletSpy = Spy()
		
		def brandName = "BedBath"
		def brandId = "bbb01"
		def seoUrl = "bedbath.com/pdp/prod01"
		BrandVO brand = new BrandVO()
		BrandVO bccBrandVO = new BrandVO()
		SortOptionVO sortOptionVO = new SortOptionVO()
		
		brand.setBrandName(brandName)
		brand.setSeoURL(seoUrl)
		
		bccBrandVO.setBrandContent("Beb bath and beyond")
		bccBrandVO.setJsFilePath("/store/js/")
		bccBrandVO.setCssFilePath("/store/css/")
		bccBrandVO.setSortOptionVO(sortOptionVO)
				
		requestMock.getParameter("origBrandName") >> brandName
		requestMock.getParameter("keywordName") >> brandId
		
		populateBrandDetailDroplet(brandDetailDropletSpy)
		2 * brandDetailDropletSpy.generateSeoUrl(brandName) >> seoUrl
		1 * catalogToolsMock.getBrandDetails(brandId, _) >> brand
		1 * catalogToolsMock.getBccManagedBrand(brand.getBrandName(),"") >> {throw new BBBSystemException("Exception occured while getting brand details ")}
		
		when :
		
		brandDetailDropletSpy.service(requestMock, responseMock)
		
		then :
		
		1 * requestMock.setParameter("seoUrl", _)
		1 * requestMock.serviceLocalParameter("seooutput", requestMock, responseMock)
		1 * requestMock.setParameter("error", "err_brand_system_error")
		1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
		
	}
	
	/*
	 * Alternative brancehs covered :
	 */
	
	def "service - Exception occured while generating SEO URL | ItemLinkException" () {
		
		given :
		
		BrandDetailDroplet brandDetailDropletSpy = Spy()
		
		def brandName = "BedBath"
		def brandId = "bbb01"
		def seoUrl = "bedbath.com/pdp/prod01"
		BrandVO brand = new BrandVO()
		BrandVO bccBrandVO = new BrandVO()
		SortOptionVO sortOptionVO = new SortOptionVO()
		
		brand.setBrandName(brandName)
		brand.setSeoURL(seoUrl)
		
		bccBrandVO.setBrandContent("Beb bath and beyond")
		bccBrandVO.setJsFilePath("/store/js/")
		bccBrandVO.setCssFilePath("/store/css/")
		bccBrandVO.setSortOptionVO(sortOptionVO)
				
		requestMock.getParameter("origBrandName") >> brandName
		requestMock.getParameter("keywordName") >> brandId
		
		populateBrandDetailDroplet(brandDetailDropletSpy)
		1 * brandDetailDropletSpy.generateSeoUrl(brandName) >> {throw new ItemLinkException("Exception occured while getting brand details ")}
		/*1 * catalogToolsMock.getBrandDetails(brandId, _) >> brand
		1 * catalogToolsMock.getBccManagedBrand(brand.getBrandName(),"") >> {throw new BBBSystemException("Exception occured while getting brand details ")}*/
		
		when :
		
		brandDetailDropletSpy.service(requestMock, responseMock)
		
		then :
		
		/*1 * requestMock.setParameter("seoUrl", _)
		1 * requestMock.serviceLocalParameter("seooutput", requestMock, responseMock)*/
		1 * requestMock.setParameter("error", "err_brand_system_error")
		1 * requestMock.serviceLocalParameter("error", requestMock, responseMock)
		
	}
	
	/*
	 * Alternative brancehs covered :
	 * 
	 * #119 - if(!BBBUtility.isEmpty( bccBrandVO.getJsFilePath()) - JS file path empty
	 * #122 - if(!BBBUtility.isEmpty( bccBrandVO.getCssFilePath()) - CSS file path empty
	 * 
	 */
	
	def "service - JS and CSS file path are invalid (empty)" () {
		
		given :
		
		BrandDetailDroplet brandDetailDropletSpy = Spy()
		
		def brandName = "BedBath"
		def brandId = "bbb01"
		def seoUrl = "bedbath.com/pdp/prod01"
		BrandVO brand = new BrandVO()
		BrandVO bccBrandVO = new BrandVO()
		SortOptionVO sortOptionVO = new SortOptionVO()
		
		brand.setBrandName(brandName)
		brand.setSeoURL(seoUrl)
		
		bccBrandVO.setBrandContent("Beb bath and beyond")
		bccBrandVO.setJsFilePath("")
		bccBrandVO.setCssFilePath("")
		bccBrandVO.setSortOptionVO(sortOptionVO)
				
		requestMock.getParameter("origBrandName") >> brandName
		requestMock.getParameter("keywordName") >> brandId
		0 * requestMock.setParameter("jsFilePath", bccBrandVO.getJsFilePath())
		0 * requestMock.setParameter("cssFilePath", bccBrandVO.getCssFilePath())
		
		populateBrandDetailDroplet(brandDetailDropletSpy)
		
		2 * brandDetailDropletSpy.generateSeoUrl(brandName) >> seoUrl
		1 * catalogToolsMock.getBrandDetails(brandId, _) >> brand
		1 * catalogToolsMock.getBccManagedBrand(brandName, "") >> bccBrandVO
		
		when :
		
		brandDetailDropletSpy.service(requestMock, responseMock)
		
		then :
		
		2 * requestMock.setParameter("seoUrl", brand.getSeoURL())
		2 * requestMock.serviceLocalParameter("seooutput", requestMock, responseMock)
		1 * requestMock.setParameter("promoContent", bccBrandVO.getBrandContent())
		1 * requestMock.setParameter("isPromoContentAvailable", _)
		1 * requestMock.setParameter("sortOptionVO", bccBrandVO.getSortOptionVO())
		1 * requestMock.setParameter("brandName", brand.getBrandName())
		
		
	}
	
	/*
	 * Alternative brancehs covered :
	 *
	 * #118 - if(!bccBrandVO.getBrandContent().toLowerCase().contains(SCRIPT_START) && !bccBrandVO.getBrandContent().toLowerCase().contains(SCRIPT_END) )
	 *  
	 *  !bccBrandVO.getBrandContent().toLowerCase().contains(SCRIPT_END) - false
	 *
	 */
	
	def "service - brand content has invalid content - end script tag" () {
		
		given :
		
		BrandDetailDroplet brandDetailDropletSpy = Spy()
		
		def brandName = "BedBath"
		def brandId = "bbb01"
		def seoUrl = "bedbath.com/pdp/prod01"
		BrandVO brand = new BrandVO()
		BrandVO bccBrandVO = new BrandVO()
		SortOptionVO sortOptionVO = new SortOptionVO()
		
		brand.setBrandName(brandName)
		brand.setSeoURL(seoUrl)
		
		bccBrandVO.setBrandContent("Beb bath and beyond</script>")
		bccBrandVO.setJsFilePath("")
		bccBrandVO.setCssFilePath("")
		bccBrandVO.setSortOptionVO(sortOptionVO)
				
		requestMock.getParameter("origBrandName") >> brandName
		requestMock.getParameter("keywordName") >> brandId
		0 * requestMock.setParameter("jsFilePath", bccBrandVO.getJsFilePath())
		0 * requestMock.setParameter("cssFilePath", bccBrandVO.getCssFilePath())
		
		populateBrandDetailDroplet(brandDetailDropletSpy)
		
		2 * brandDetailDropletSpy.generateSeoUrl(brandName) >> seoUrl
		1 * catalogToolsMock.getBrandDetails(brandId, _) >> brand
		1 * catalogToolsMock.getBccManagedBrand(brandName, "") >> bccBrandVO
		
		when :
		
		brandDetailDropletSpy.service(requestMock, responseMock)
		
		then :
		
		2 * requestMock.setParameter("seoUrl", brand.getSeoURL())
		2 * requestMock.serviceLocalParameter("seooutput", requestMock, responseMock)
		1 * requestMock.setParameter("isPromoContentAvailable", _)
		1 * requestMock.setParameter("sortOptionVO", bccBrandVO.getSortOptionVO())
		1 * requestMock.setParameter("brandName", brand.getBrandName())
		
	}
	
	/*
	 * Alternative brancehs covered :
	 *
	 * #118 - if(!bccBrandVO.getBrandContent().toLowerCase().contains(SCRIPT_START) && !bccBrandVO.getBrandContent().toLowerCase().contains(SCRIPT_END) )
	 * 
	 * !bccBrandVO.getBrandContent().toLowerCase().contains(SCRIPT_START) - false
	 *
	 */
	
	def "service - brand content has invalid content - start script tag" () {
		
		given :
		
		BrandDetailDroplet brandDetailDropletSpy = Spy()
		
		def brandName = "BedBath"
		def brandId = "bbb01"
		def seoUrl = "bedbath.com/pdp/prod01"
		BrandVO brand = new BrandVO()
		BrandVO bccBrandVO = new BrandVO()
		SortOptionVO sortOptionVO = new SortOptionVO()
		
		brand.setBrandName(brandName)
		brand.setSeoURL(seoUrl)
		
		bccBrandVO.setBrandContent("<script>Beb bath and beyond")
		bccBrandVO.setJsFilePath("")
		bccBrandVO.setCssFilePath("")
		bccBrandVO.setSortOptionVO(sortOptionVO)
				
		requestMock.getParameter("origBrandName") >> brandName
		requestMock.getParameter("keywordName") >> brandId
		0 * requestMock.setParameter("jsFilePath", bccBrandVO.getJsFilePath())
		0 * requestMock.setParameter("cssFilePath", bccBrandVO.getCssFilePath())
		
		populateBrandDetailDroplet(brandDetailDropletSpy)
		
		2 * brandDetailDropletSpy.generateSeoUrl(brandName) >> seoUrl
		1 * catalogToolsMock.getBrandDetails(brandId, _) >> brand
		1 * catalogToolsMock.getBccManagedBrand(brandName, "") >> bccBrandVO
		
		when :
		
		brandDetailDropletSpy.service(requestMock, responseMock)
		
		then :
		
		2 * requestMock.setParameter("seoUrl", brand.getSeoURL())
		2 * requestMock.serviceLocalParameter("seooutput", requestMock, responseMock)
		1 * requestMock.setParameter("isPromoContentAvailable", _)
		1 * requestMock.setParameter("sortOptionVO", bccBrandVO.getSortOptionVO())
		1 * requestMock.setParameter("brandName", brand.getBrandName())		
	}
	
	/*
	 * Alternative brancehs covered :
	 *
	 * #117 - if(!BBBUtility.isEmpty( bccBrandVO.getBrandContent())) - false
	 *
	 *
	 */
	
	def "service - brand content is invalid - empty" () {
		
		given :
		
		BrandDetailDroplet brandDetailDropletSpy = Spy()
		
		def brandName = "BedBath"
		def brandId = "bbb01"
		def seoUrl = "bedbath.com/pdp/prod01"
		BrandVO brand = new BrandVO()
		BrandVO bccBrandVO = new BrandVO()
		SortOptionVO sortOptionVO = new SortOptionVO()
		
		brand.setBrandName(brandName)
		brand.setSeoURL(seoUrl)
		
		bccBrandVO.setBrandContent("")
		bccBrandVO.setJsFilePath("")
		bccBrandVO.setCssFilePath("")
		bccBrandVO.setSortOptionVO(sortOptionVO)
				
		requestMock.getParameter("origBrandName") >> brandName
		requestMock.getParameter("keywordName") >> brandId
		0 * requestMock.setParameter("jsFilePath", bccBrandVO.getJsFilePath())
		0 * requestMock.setParameter("cssFilePath", bccBrandVO.getCssFilePath())
		
		populateBrandDetailDroplet(brandDetailDropletSpy)
		
		2 * brandDetailDropletSpy.generateSeoUrl(brandName) >> seoUrl
		1 * catalogToolsMock.getBrandDetails(brandId, _) >> brand
		1 * catalogToolsMock.getBccManagedBrand(brandName, "") >> bccBrandVO
		
		when :
		
		brandDetailDropletSpy.service(requestMock, responseMock)
		
		then :
		
		2 * requestMock.setParameter("seoUrl", brand.getSeoURL())
		2 * requestMock.serviceLocalParameter("seooutput", requestMock, responseMock)
		1 * requestMock.setParameter("isPromoContentAvailable", _)
		1 * requestMock.setParameter("sortOptionVO", bccBrandVO.getSortOptionVO())
		1 * requestMock.setParameter("brandName", brand.getBrandName())		
		
	}
	
	
	/*
	 * Alternative brancehs covered :
	 *
	 * #113 - if (brand != null && BBBUtility.isNotEmpty(brand.getBrandName())) 
	 *
	 *
	 */
	
	def "service - brand name does not exist in the catalog - brand name is empty" () {
		
		given :
		
		BrandDetailDroplet brandDetailDropletSpy = Spy()
		
		def brandName = ""
		def brandId = "bbb01"
		def seoUrl = "bedbath.com/pdp/prod01"
		BrandVO brand = new BrandVO()
		
		brand.setBrandName("")
		
		requestMock.getParameter("origBrandName") >> brandName
		requestMock.getParameter("keywordName") >> brandId
		
		populateBrandDetailDroplet(brandDetailDropletSpy)
		
		1 * catalogToolsMock.getBrandDetails(brandId, _) >> brand
		
		expect :
		
		brandDetailDropletSpy.service(requestMock, responseMock)
		
	}
	
	
	/*
	 * Alternative brancehs covered :
	 *
	 * #113 - if (brand != null && BBBUtility.isNotEmpty(brand.getBrandName())) - brand is null
	 *
	 * brand != null  - false
	 *
	 */
	
	def "service - brand details are not present in the catalog - BrandVO is null" () {
		
		given :
		
		BrandDetailDroplet brandDetailDropletSpy = Spy()
		
		def brandName = ""
		def brandId = "bbb01"
		def seoUrl = "bedbath.com/pdp/prod01"
		/*BrandVO brand = new BrandVO()
		
		brand.setBrandName("")
		
		bccBrandVO.setSortOptionVO(sortOptionVO)*/
				
		requestMock.getParameter("origBrandName") >> brandName
		requestMock.getParameter("keywordName") >> brandId
		
		populateBrandDetailDroplet(brandDetailDropletSpy)
		
		1 * catalogToolsMock.getBrandDetails(brandId, _) >> null
		
		expect :
		
		brandDetailDropletSpy.service(requestMock, responseMock)
		
	}
	
	
	/*
	 * service - test cases ENDS
	 */
	
	/*
	 * getBrandDetails - test cases STARTS
	 * 
	 * Method signature : 
	 * 
	 * public Map<String, Object> getBrandDetails(String brandId) throws BBBSystemException 
	 * 
	 */
	
	def "getBrandDetails - Brand details retrieved successfully" () {
		
		given : 
		
		def seoUrl = "bedbath.com/pdp/product01"
		def sortUrlparam = "bedbath.com/pdp/product01?sort=ascending" 
		def brandName = "bedbath"
		
		SortOptionVO sorOption = new SortOptionVO()
		SortOptionsVO defaultSortingOption = new SortOptionsVO()
		Map<String, Object> brandDetails = new HashMap<>()
		
		defaultSortingOption.setSortUrlParam(sortUrlparam)
		defaultSortingOption.setAscending(1)
		
		sorOption.setDefaultSortingOption(defaultSortingOption)
		
		requestMock.getObjectParameter("sortOptionVO") >> sorOption
		requestMock.getObjectParameter("seoUrl") >> seoUrl
		requestMock.getObjectParameter("brandName") >> brandName
		brandDetailDroplet.setLoggingDebug(true)
		
		when :
		 
		brandDetails = brandDetailDroplet.getBrandDetails("bedbath")
		
		then : 
		
		brandDetails.get("brandName").equals(brandName)
		brandDetails.get("seoUrl").equals(seoUrl)
		brandDetails.get("sortOptionVO").equals(sorOption)

	}
	
	/*
	 * Alternative branches covered :
	 *
	 * #172 - if (isLoggingDebug()) 
	 *
	 */
	def "getBrandDetails - Brand details retrieved successfully | loggingDebug disabled" () {
		
		given :
		
		def seoUrl = "bedbath.com/pdp/product01"
		def sortUrlparam = "bedbath.com/pdp/product01?sort=ascending"
		def brandName = "bedbath"
		
		SortOptionVO sorOption = new SortOptionVO()
		SortOptionsVO defaultSortingOption = new SortOptionsVO()
		Map<String, Object> brandDetails = new HashMap<>()
		
		defaultSortingOption.setSortUrlParam(sortUrlparam)
		defaultSortingOption.setAscending(1)
		
		sorOption.setDefaultSortingOption(defaultSortingOption)
		
		requestMock.getObjectParameter("sortOptionVO") >> sorOption
		requestMock.getObjectParameter("seoUrl") >> seoUrl
		requestMock.getObjectParameter("brandName") >> brandName
		brandDetailDroplet.setLoggingDebug(false)
		
		when :
		 
		brandDetails = brandDetailDroplet.getBrandDetails("bedbath")
		
		then :
		
		brandDetails.get("brandName").equals(brandName)
		brandDetails.get("seoUrl").equals(seoUrl)
		brandDetails.get("sortOptionVO").equals(sorOption)

	}
	
	/*
	 * Alternative branches covered : 
	 * 
	 * #171 -  && sorOptions.getDefaultSortingOption().getAscending() != null - false
	 *
	 */
	
	def "getBrandDetails - SEO url is invalid (empty)" () {
		
		given :
		
		def seoUrl = "bedbath.com/pdp/product01"
		def sortUrlparam = ""
		def brandName = "bedbath"
		
		Map<String, Object> brandDetails = new HashMap<>()
		
		SortOptionVO sorOption = new SortOptionVO()
		SortOptionsVO defaultSortingOption = new SortOptionsVO()
		
		defaultSortingOption.setSortUrlParam(sortUrlparam)
		//defaultSortingOption.setAscending(1)
		
		sorOption.setDefaultSortingOption(defaultSortingOption)
		
		requestMock.getObjectParameter("sortOptionVO") >> sorOption
		requestMock.getObjectParameter("seoUrl") >> seoUrl
		requestMock.getObjectParameter("brandName") >> brandName
		
		brandDetailDroplet.setLoggingDebug(true)
		
		when :
		 
		brandDetails = brandDetailDroplet.getBrandDetails("bedbath")
		
		then : 
		
		brandDetails.get("brandName").equals(brandName)
		brandDetails.get("seoUrl").equals(seoUrl)
		brandDetails.get("sortOptionVO").equals(sorOption)
	}
	
	/*
	 * Alternative branches covered :
	 *
	 * #171 -  && sorOptions.getDefaultSortingOption().getAscending() != null - false
	 *
	 */
	
	def "getBrandDetails - Sort options info are invalid (empty)" () {
		
		given :
		
		def seoUrl = "bedbath.com/pdp/product01"
		def sortUrlparam = "bedbath.com/pdp/product01?sort=ascending" 
		def brandName = "bedbath"
		
		Map<String, Object> brandDetails = new HashMap<>()
		
		SortOptionVO sorOption = new SortOptionVO()
		SortOptionsVO defaultSortingOption = new SortOptionsVO()
		
		defaultSortingOption.setSortUrlParam(sortUrlparam)
		//defaultSortingOption.setAscending(1)
		
		sorOption.setDefaultSortingOption(defaultSortingOption)
		
		requestMock.getObjectParameter("sortOptionVO") >> sorOption
		requestMock.getObjectParameter("seoUrl") >> seoUrl
		requestMock.getObjectParameter("brandName") >> brandName
		
		brandDetailDroplet.setLoggingDebug(true)
		
		when :
		 
		brandDetails = brandDetailDroplet.getBrandDetails("bedbath")
		
		then :
		
		brandDetails.get("brandName").equals(brandName)
		brandDetails.get("seoUrl").equals(seoUrl)
		brandDetails.get("sortOptionVO").equals(sorOption)
	}
	
	/*
	 * Alternative branches covered :
	 *
	 * #170 -  if (sorOptions != null && sorOptions.getDefaultSortingOption() != null ) - getDefaultSortingOption() - null
	 *
	 */
	
	def "getBrandDetails - No default sorting options available" () {
		
		given :
		
		def seoUrl = "bedbath.com/pdp/product01"
		def sortUrlparam = "bedbath.com/pdp/product01?sort=ascending"
		def brandName = "bedbath"
		
		Map<String, Object> brandDetails = new HashMap<>()
		
		SortOptionVO sorOption = new SortOptionVO()
		SortOptionsVO defaultSortingOption = new SortOptionsVO()
		
		defaultSortingOption.setSortUrlParam(sortUrlparam)
		
		requestMock.getObjectParameter("sortOptionVO") >> sorOption
		requestMock.getObjectParameter("seoUrl") >> seoUrl
		requestMock.getObjectParameter("brandName") >> brandName
		
		brandDetailDroplet.setLoggingDebug(true)
		
		when :
		 
		brandDetails = brandDetailDroplet.getBrandDetails("bedbath")
		
		then :
		
		brandDetails.get("brandName").equals(brandName)
		brandDetails.get("seoUrl").equals(seoUrl)
		brandDetails.get("sortOptionVO").equals(sorOption)
		
	}
	
	/*
	 * Alternative branches covered :
	 *
	 * #170 -  if (sorOptions != null && sorOptions.getDefaultSortingOption() != null ) - sortOptions - null
	 *
	 */
	
	def "getBrandDetails - SortOptions not available" () {
		
		given :
		
		def seoUrl = "bedbath.com/pdp/product01"
		def sortUrlparam = "bedbath.com/pdp/product01?sort=ascending"
		def brandName = "bedbath"
		
		Map<String, Object> brandDetails = new HashMap<>()
		
		SortOptionVO sorOption = null
		SortOptionsVO defaultSortingOption = new SortOptionsVO()
		
		defaultSortingOption.setSortUrlParam(sortUrlparam)
		
		requestMock.getObjectParameter("sortOptionVO") >> sorOption
		requestMock.getObjectParameter("seoUrl") >> seoUrl
		requestMock.getObjectParameter("brandName") >> brandName
		
		brandDetailDroplet.setLoggingDebug(true)
		
		when :
		 
		brandDetails = brandDetailDroplet.getBrandDetails("bedbath")
		
		then :
		
		brandDetails.get("brandName").equals(brandName)
		brandDetails.get("seoUrl").equals(seoUrl)
		brandDetails.get("sortOptionVO").equals(sorOption)
		
	}
	
	/*
	 * Alternative branches covered :
	 *
	 */
	
	def "getBrandDetails - Exception while getting brand details - ServletException" () {
		
		given :
		
		def seoUrl = "bedbath.com/pdp/product01"
		def sortUrlparam = "bedbath.com/pdp/product01?sort=ascending"
		def brandName = "bedbath"
		
		SortOptionVO sorOption = new SortOptionVO()
		SortOptionsVO defaultSortingOption = new SortOptionsVO()
		Map<String, Object> brandDetails = new HashMap<>()
		
		BrandDetailDroplet brandDetailDropletSpy = Spy()
		
		populateBrandDetailDroplet(brandDetailDropletSpy)
		
		defaultSortingOption.setSortUrlParam(sortUrlparam)
		defaultSortingOption.setAscending(1)
		
		sorOption.setDefaultSortingOption(defaultSortingOption)
		
		brandDetailDropletSpy.service(requestMock, responseMock) >> {throw new ServletException("")}
		
		
		brandDetailDropletSpy.setLoggingDebug(true)
		
		when :
		 
		brandDetails = brandDetailDropletSpy.getBrandDetails("bedbath")
		
		then :
		
		brandDetails.get("brandName") == null
		brandDetails.get("seoUrl") == null
		brandDetails.get("sortOptionVO") == null
		thrown BBBSystemException

	}


	/*
	 * Alternative branches covered :
	 *
	 *
	 */
	
	def "getBrandDetails - Exception while getting brand details - IOException" () {
		
		given :
		
		def seoUrl = "bedbath.com/pdp/product01"
		def sortUrlparam = "bedbath.com/pdp/product01?sort=ascending"
		def brandName = "bedbath"
		
		SortOptionVO sorOption = new SortOptionVO()
		SortOptionsVO defaultSortingOption = new SortOptionsVO()
		Map<String, Object> brandDetails = new HashMap<>()
		
		BrandDetailDroplet brandDetailDropletSpy = Spy()
		
		populateBrandDetailDroplet(brandDetailDropletSpy)
		
		defaultSortingOption.setSortUrlParam(sortUrlparam)
		defaultSortingOption.setAscending(1)
		
		sorOption.setDefaultSortingOption(defaultSortingOption)
		
		brandDetailDropletSpy.service(requestMock, responseMock) >> {throw new IOException("")}
		
		
		brandDetailDropletSpy.setLoggingDebug(true)
		
		when :
		 
		brandDetails = brandDetailDropletSpy.getBrandDetails("bedbath")
		
		then :
		
		brandDetails.get("brandName") == null
		brandDetails.get("seoUrl") == null
		brandDetails.get("sortOptionVO") == null
		thrown BBBSystemException

	}

	
	/*
	 * getBrandDetails - test cases ENDS
	 */
	
	
	/*
	 * generateSeoUrl  - Test cases - STARTS
	 * 
	 * Method signature : 
	 * 
	 * public String generateSeoUrl (String pBrandName) throws ItemLinkException 
	 * 
	 */

	
	def "generateSeoUrl - SEO Url generated successfully" () {
		
		given : 
		
		BrandDetailDroplet brandDetailDropletSpy = Spy()
		def formattedUrl = "http://bedbath.com/pdp/product01"
		def brandName = "bedbath"
		
		UrlParameter pUrlParam1 = Mock()
		UrlParameter pUrlParam2 = Mock()
		UrlParameter[] pUrlParams = [pUrlParam1 , pUrlParam2]
		
		populateBrandDetailDroplet(brandDetailDropletSpy)
		
		brandTemplateMock.cloneUrlParameters() >> pUrlParams
		brandTemplateMock.formatUrl(pUrlParams, _) >> formattedUrl
		
		expect : 
		
		brandDetailDropletSpy.generateSeoUrl("BEDBATH").equals(formattedUrl)
		
	}

		
	/*
	 * generateSeoUrl  - Test cases - ENDS
	 *
	 */
	
	/*
	 * Data populating methods
	 */
	
	private populateBrandDetailDroplet(BrandDetailDroplet brandDetailDropletSpy) {
		brandDetailDropletSpy.setCatalogTools(catalogToolsMock)
		brandDetailDropletSpy.setBrandTemplate(brandTemplateMock)
	}


}
