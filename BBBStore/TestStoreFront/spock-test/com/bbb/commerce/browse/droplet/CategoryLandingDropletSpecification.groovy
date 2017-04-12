package com.bbb.commerce.browse.droplet

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.LandingTemplateVO
import com.bbb.cms.manager.LandingTemplateManager
import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.CategoryVO
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.personalstore.manager.PersonalStoreManager
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.CategoryRefinementVO
import com.bbb.search.integration.SearchManager

import spock.lang.Ignore;
import spock.lang.specification.BBBExtendedSpec;
/**
 * 
 * @author kmagud
 *
 */
class CategoryLandingDropletSpecification extends BBBExtendedSpec {

	CategoryLandingDroplet testObj
	BBBCatalogTools catalogToolsMock = Mock()
	LandingTemplateManager landingTemplateManagerMock = Mock()
	SearchManager searchManagerMock = Mock()
	PersonalStoreManager personalStoreMgrMock = Mock()
	
	private static final boolean TRUE = true
	private static final boolean FALSE = false
	private static final String SAVEDURL = "SAVEDURL";
	public final static String  PARAMETER_SUB_CATEGORY_LIST="subcategoriesList";
	public final static String  PARAMETER_LANDING_TEMPLATEVO="landingTemplateVO";
	public final static String CATEGORYL1 = "categoryL1";
	public final static String CATEGORYL2 = "categoryL2";
	public final static String CATEGORYL3 = "categoryL3";
	public final static String ROOT_CATEGORY = "RootCategory";
	public final static String CATEGORY_VO = "categoryVO";
	
	def setup(){
		testObj = new CategoryLandingDroplet(mCatalogTools:catalogToolsMock,mLandingTemplateManager:landingTemplateManagerMock,
			mSearchManager:searchManagerMock,mPersonalStoreMgr:personalStoreMgrMock)
	}
	
	/////////////////////////////////TCs for service starts////////////////////////////////////////
	//Signature : public void service(DynamoHttpServletRequest request, DynamoHttpServletResponse response) ////
	
	def"service method.This TC is the Happy flow of service method"(){
		given:
			String categoryId =  "cat12345"
			String fetchSubCategories = "true"
			String siteId = "BedBathUS"
			1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			1 * requestMock.getParameter(BBBCoreConstants.ID) >> categoryId
			1 * requestMock.getParameter("fetchSubCategories") >> fetchSubCategories
			2 * requestMock.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) >> TRUE
			2 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) >> FALSE
			CategoryVO subCategoryVOMock = new CategoryVO(categoryId:"cat33333")
			CategoryVO subCategoryVOMock1 = new CategoryVO(categoryId:"cat44444")
			CategoryVO categoryVOMock = new CategoryVO(subCategories:[subCategoryVOMock,subCategoryVOMock1])
			LandingTemplateVO landingTemplateVOMock = new LandingTemplateVO(mCategory:categoryVOMock)
			1 * landingTemplateManagerMock.getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId) >> landingTemplateVOMock
			catalogToolsMock.isFirstLevelCategory(categoryId, siteId) >> FALSE
			
			//getSubCategories Public Method Coverage
			2 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, siteId+ROOT_CATEGORY) >> ["root"]
			CategoryRefinementVO categoryRefinementVOMock = new CategoryRefinementVO(query:"query",name:"refinement")
			CategoryRefinementVO categoryRefinementVOMock1 = new CategoryRefinementVO(query:"query1",name:"refinement1")
			CategoryParentVO categoryParentVOMock = new CategoryParentVO(name:"Tablecloths",categoryRefinement:[categoryRefinementVOMock,categoryRefinementVOMock1])
			CategoryParentVO categoryParentVOMock1 = new CategoryParentVO(name:"Fine China",categoryRefinement:null)
			1 * searchManagerMock.getCategoryTree(_) >> ["1":categoryParentVOMock,"2":categoryParentVOMock1]
			
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(PARAMETER_SUB_CATEGORY_LIST, _)
			1 * requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"service method.This TC is when isFirstLevelCategory is true and catVar is false"(){
		given:
			String categoryId =  "cat12345"
			String fetchSubCategories = "true"
			String siteId = "BedBathUS"
			1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			1 * requestMock.getParameter(BBBCoreConstants.ID) >> categoryId
			1 * requestMock.getParameter("fetchSubCategories") >> fetchSubCategories
			2 * requestMock.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) >> FALSE
			1 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) >> null
			CategoryVO categoryVOMock = new CategoryVO(subCategories:[])
			LandingTemplateVO landingTemplateVOMock = new LandingTemplateVO(mCategory:categoryVOMock)
			1 * landingTemplateManagerMock.getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId) >> landingTemplateVOMock
			catalogToolsMock.isFirstLevelCategory(categoryId, siteId) >> TRUE
			
			//getSubCategories Public Method Coverage
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, siteId+ROOT_CATEGORY) >> null
			1 * searchManagerMock.getCategoryTree(_) >> null
			CategoryVO categoryVOMock3 = new CategoryVO()
			CategoryVO categoryVOMock2 = new CategoryVO(subCategories:[categoryVOMock3])
			1 * catalogToolsMock.getCategoryDetail(siteId,categoryId,false) >> categoryVOMock2
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter (PARAMETER_SUB_CATEGORY_LIST, [categoryVOMock3])
			1 * requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"service method.This TC is when fetchSubCategories is false"(){
		given:
			String categoryId =  "cat12345"
			String fetchSubCategories = "false"
			String siteId = "BedBathUS"
			1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			1 * requestMock.getParameter(BBBCoreConstants.ID) >> categoryId
			1 * requestMock.getParameter("fetchSubCategories") >> fetchSubCategories
			1 * requestMock.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) >> null
			1 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) >> null
			CategoryVO categoryVOMock = new CategoryVO(subCategories:null)
			LandingTemplateVO landingTemplateVOMock = new LandingTemplateVO(mCategory:categoryVOMock)
			1 * landingTemplateManagerMock.getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId) >> landingTemplateVOMock
			1 * catalogToolsMock.isFirstLevelCategory(categoryId, siteId) >> TRUE
			CategoryVO categoryVOMock3 = new CategoryVO(categoryName:"Tablecloths")
			CategoryVO categoryVOMock4 = new CategoryVO(categoryName:"Fine China")
			CategoryVO categoryVOMock5 = new CategoryVO()
			1 * catalogToolsMock.getParentCategory(categoryId, siteId) >> ["0":categoryVOMock3,"1":categoryVOMock4,"2":categoryVOMock5,"3":null]
			CategoryVO categoryVOMock6 = new CategoryVO(categoryName:"Tablecloths",seoURL:"category/dining/glasses-drinkware/coffee-mugs/12766/",isCollege:null)
			1 * catalogToolsMock.getCategoryDetail(siteId, categoryId,false) >> categoryVOMock6 
			1 * catalogToolsMock.getBccManagedCategory(categoryVOMock6)
			
			//setUrlToCookie Private method Coverage
			1 * requestMock.getContextPath() >> "store/"
			1 * requestMock.getServerName() >> "bedbathandbeyond.com"
			responseMock.getResponse() >> responseMock

		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter (CATEGORYL1, "Tablecloths")
			1 * requestMock.setParameter (CATEGORYL2, "Fine China")
			1 * requestMock.setParameter (CATEGORYL3, "")
			1 * requestMock.setParameter (CATEGORY_VO, categoryVOMock6)
			1 * requestMock.setParameter (PARAMETER_LANDING_TEMPLATEVO, landingTemplateVOMock)
			1 * requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"service method.This TC is when getIsCollege is true in setUrlToCookie private method"(){
		given:
			String categoryId =  "cat12345"
			String fetchSubCategories = "false"
			String siteId = "BedBathUS"
			1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			1 * requestMock.getParameter(BBBCoreConstants.ID) >> categoryId
			1 * requestMock.getParameter("fetchSubCategories") >> fetchSubCategories
			2 * requestMock.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) >> TRUE
			1 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) >> null
			LandingTemplateVO landingTemplateVOMock = new LandingTemplateVO(mCategory:null)
			1 * landingTemplateManagerMock.getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId) >> landingTemplateVOMock
			1 * catalogToolsMock.isFirstLevelCategory(categoryId, siteId) >> FALSE
			1 * catalogToolsMock.getParentCategory(categoryId, siteId) >> null
			CategoryVO categoryVOMock6 = new CategoryVO(categoryName:"Coffee Mugs",seoURL:"category/dining/glasses-drinkware/coffee-mugs/12766/",isCollege:TRUE)
			1 * catalogToolsMock.getCategoryDetail(siteId, categoryId,false) >> categoryVOMock6
			1 * catalogToolsMock.getBccManagedCategory(categoryVOMock6)
			
			//setUrlToCookie Private method Coverage
			1 * requestMock.getContextPath() >> "store/"
			1 * requestMock.getServerName() >> "bedbathandbeyond.com"
			responseMock.getResponse() >> responseMock

		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter (CATEGORYL1, "")
			1 * requestMock.setParameter (CATEGORYL2, "")
			1 * requestMock.setParameter (CATEGORYL3, "Coffee Mugs")
			1 * requestMock.setParameter (CATEGORY_VO, categoryVOMock6)
			1 * requestMock.setParameter (PARAMETER_LANDING_TEMPLATEVO, landingTemplateVOMock)
			1 * requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"service method.This TC is when categoryL1 is null and getIsCollege is false in setUrlToCookie private method"(){
		given:
			String categoryId =  "cat12345"
			String fetchSubCategories = "false"
			String siteId = "BedBathUS"
			1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			1 * requestMock.getParameter(BBBCoreConstants.ID) >> categoryId
			1 * requestMock.getParameter("fetchSubCategories") >> fetchSubCategories
			1 * requestMock.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) >> null
			1 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) >> null
			CategoryVO categoryVOMock = new CategoryVO(subCategories:null)
			LandingTemplateVO landingTemplateVOMock = new LandingTemplateVO(mCategory:categoryVOMock)
			1 * landingTemplateManagerMock.getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId) >> landingTemplateVOMock
			1 * catalogToolsMock.isFirstLevelCategory(categoryId, siteId) >> TRUE
			CategoryVO categoryVOMock3 = new CategoryVO(categoryName:null)
			CategoryVO categoryVOMock4 = new CategoryVO(categoryName:"Fine China")
			1 * catalogToolsMock.getParentCategory(categoryId, siteId) >> ["0":categoryVOMock3,"1":categoryVOMock4]
			CategoryVO categoryVOMock6 = new CategoryVO(categoryName:"Tablecloths",seoURL:"category/dining/glasses-drinkware/coffee-mugs/12766/",isCollege:FALSE)
			1 * catalogToolsMock.getCategoryDetail(siteId, categoryId,false) >> categoryVOMock6
			
			
			//setUrlToCookie Private method Coverage
			1 * requestMock.getContextPath() >> "store/"
			1 * requestMock.getServerName() >> "bedbathandbeyond.com"
			responseMock.getResponse() >> responseMock

		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter (CATEGORYL1, null)
			1 * requestMock.setParameter (CATEGORYL2, "Fine China")
			1 * requestMock.setParameter (CATEGORYL3, "Tablecloths")
			1 * requestMock.setParameter (CATEGORY_VO, categoryVOMock6)
			1 * requestMock.setParameter (PARAMETER_LANDING_TEMPLATEVO, landingTemplateVOMock)
			1 * requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
			1 * catalogToolsMock.getBccManagedCategory(categoryVOMock6)
	}
	
	def"service method.This TC is when getCategoryDetail's categoryVO is null"(){
		given:
			String categoryId =  "cat12345"
			String fetchSubCategories = "false"
			String siteId = "BedBathUS"
			1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			1 * requestMock.getParameter(BBBCoreConstants.ID) >> categoryId
			1 * requestMock.getParameter("fetchSubCategories") >> fetchSubCategories
			2 * requestMock.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) >> TRUE
			1 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) >> null
			LandingTemplateVO landingTemplateVOMock = new LandingTemplateVO(mCategory:null)
			1 * landingTemplateManagerMock.getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId) >> landingTemplateVOMock
			1 * catalogToolsMock.isFirstLevelCategory(categoryId, siteId) >> FALSE
			1 * catalogToolsMock.getParentCategory(categoryId, siteId) >> null
			1 * catalogToolsMock.getCategoryDetail(siteId, categoryId,false) >> null
			1 * catalogToolsMock.getBccManagedCategory(null)

		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter (CATEGORYL1, "")
			1 * requestMock.setParameter (CATEGORYL2, "")
			1 * requestMock.setParameter (CATEGORYL3, "")
			1 * requestMock.setParameter (CATEGORY_VO, null)
			1 * requestMock.setParameter (PARAMETER_LANDING_TEMPLATEVO, landingTemplateVOMock)
			1 * requestMock.serviceParameter (BBBCoreConstants.OPARAM, requestMock, responseMock)
	}
	
	def"service method.This TC is when isFirstLevelCategory and catVar is false"(){
		given:
			String categoryId =  "cat12345"
			String fetchSubCategories = "false"
			String siteId = "BedBathUS"
			1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			1 * requestMock.getParameter(BBBCoreConstants.ID) >> categoryId
			1 * requestMock.getParameter("fetchSubCategories") >> fetchSubCategories
			2 * requestMock.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) >> FALSE
			1 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) >> null
			LandingTemplateVO landingTemplateVOMock = new LandingTemplateVO(mCategory:null)
			1 * landingTemplateManagerMock.getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId) >> landingTemplateVOMock
			1 * catalogToolsMock.isFirstLevelCategory(categoryId, siteId) >> FALSE
			CategoryVO categoryVOMock = new CategoryVO(seoURL:"",subCategories:null,categoryId:"cat88888")
			1 * catalogToolsMock.getCategoryDetail(siteId, categoryId,false) >> categoryVOMock
			1 * catalogToolsMock.getBccManagedCategory(categoryVOMock)
			1 * personalStoreMgrMock.createCategoryCookie(requestMock, responseMock,"cat88888", "L3")
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(CATEGORY_VO, categoryVOMock)
			1 * requestMock.serviceParameter(BBBSearchBrowseConstants.OPARAM_SUBCAT, requestMock, responseMock)
		
	}
	
	def"service method.This TC is when categoryVO.getSubCategories size is equal to 0"(){
		given:
			String categoryId =  "cat12345"
			String fetchSubCategories = "false"
			String siteId = "BedBathUS"
			1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			1 * requestMock.getParameter(BBBCoreConstants.ID) >> categoryId
			1 * requestMock.getParameter("fetchSubCategories") >> fetchSubCategories
			2 * requestMock.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) >> FALSE
			1 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) >> null
			LandingTemplateVO landingTemplateVOMock = new LandingTemplateVO(mCategory:null)
			1 * landingTemplateManagerMock.getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId) >> landingTemplateVOMock
			1 * catalogToolsMock.isFirstLevelCategory(categoryId, siteId) >> FALSE
			CategoryVO categoryVOMock = new CategoryVO(seoURL:"",subCategories:[],categoryId:"cat88888")
			1 * catalogToolsMock.getCategoryDetail(siteId, categoryId,false) >> categoryVOMock
			1 * catalogToolsMock.getBccManagedCategory(categoryVOMock)
			1 * personalStoreMgrMock.createCategoryCookie(requestMock, responseMock,"cat88888", "L3")
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(CATEGORY_VO, categoryVOMock)
			1 * requestMock.serviceParameter(BBBSearchBrowseConstants.OPARAM_SUBCAT, requestMock, responseMock)
		
	}
	
	def"service method.This TC is when categoryVO.getSubCategories has value"(){
		given:
			String categoryId =  "cat12345"
			String fetchSubCategories = "false"
			String siteId = "BedBathUS"
			1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			1 * requestMock.getParameter(BBBCoreConstants.ID) >> categoryId
			1 * requestMock.getParameter("fetchSubCategories") >> fetchSubCategories
			2 * requestMock.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) >> FALSE
			1 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) >> null
			LandingTemplateVO landingTemplateVOMock = new LandingTemplateVO(mCategory:null)
			1 * landingTemplateManagerMock.getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId) >> landingTemplateVOMock
			1 * catalogToolsMock.isFirstLevelCategory(categoryId, siteId) >> FALSE
			CategoryVO subCategoryVOMock = new CategoryVO(categoryId:"cat33333")
			CategoryVO categoryVOMock = new CategoryVO(seoURL:"",subCategories:[subCategoryVOMock],categoryId:"cat88888")
			1 * catalogToolsMock.getCategoryDetail(siteId, categoryId,false) >> categoryVOMock
			1 * catalogToolsMock.getBccManagedCategory(categoryVOMock)
			1 * personalStoreMgrMock.createCategoryCookie(requestMock, responseMock,"cat88888", "L2")
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(CATEGORY_VO, categoryVOMock)
			1 * requestMock.serviceParameter(BBBSearchBrowseConstants.OPARAM_SUBCAT, requestMock, responseMock)
		
	}
	
	def"service method.This TC is when isFirstLevelCategory and catVar is false and getCategoryDetail's categoryVO is null"(){
		given:
			String categoryId =  "cat12345"
			String fetchSubCategories = "false"
			String siteId = "BedBathUS"
			1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			1 * requestMock.getParameter(BBBCoreConstants.ID) >> categoryId
			1 * requestMock.getParameter("fetchSubCategories") >> fetchSubCategories
			2 * requestMock.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) >> FALSE
			1 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) >> null
			LandingTemplateVO landingTemplateVOMock = new LandingTemplateVO(mCategory:null)
			1 * landingTemplateManagerMock.getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId) >> landingTemplateVOMock
			1 * catalogToolsMock.isFirstLevelCategory(categoryId, siteId) >> FALSE
			1 * catalogToolsMock.getCategoryDetail(siteId, categoryId,false) >> null
			1 * catalogToolsMock.getBccManagedCategory(null)
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			thrown NullPointerException
			1 * requestMock.setParameter(CATEGORY_VO, null)
			1 * requestMock.serviceParameter(BBBSearchBrowseConstants.OPARAM_SUBCAT, requestMock, responseMock)
		
	}
	
	def"service method.This TC is when subCatPlpVar is true"(){
		given:
			String categoryId =  "cat12345"
			String fetchSubCategories = "false"
			String siteId = "BedBathUS"
			1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			1 * requestMock.getParameter(BBBCoreConstants.ID) >> categoryId
			1 * requestMock.getParameter("fetchSubCategories") >> fetchSubCategories
			2 * requestMock.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) >> FALSE
			2 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) >> TRUE
			LandingTemplateVO landingTemplateVOMock = new LandingTemplateVO(mCategory:null)
			1 * landingTemplateManagerMock.getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId) >> landingTemplateVOMock
			1 * catalogToolsMock.isFirstLevelCategory(categoryId, siteId) >> FALSE
			CategoryVO categoryVOMock = new CategoryVO(seoURL:"")
			1 * catalogToolsMock.getCategoryDetail(siteId, categoryId,false) >> categoryVOMock
			1 * catalogToolsMock.getBccManagedCategory(categoryVOMock)
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(CATEGORY_VO, categoryVOMock)
			1 * requestMock.serviceParameter(BBBSearchBrowseConstants.OPARAM_SUBCAT, requestMock, responseMock)
		
	}
	
	def"service method.This TC is when subCatPlpVar is true and CategoryVO is null"(){
		given:
			String categoryId =  "cat12345"
			String fetchSubCategories = "false"
			String siteId = "BedBathUS"
			1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			1 * requestMock.getParameter(BBBCoreConstants.ID) >> categoryId
			1 * requestMock.getParameter("fetchSubCategories") >> fetchSubCategories
			2 * requestMock.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) >> FALSE
			2 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) >> TRUE
			LandingTemplateVO landingTemplateVOMock = new LandingTemplateVO(mCategory:null)
			1 * landingTemplateManagerMock.getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId) >> landingTemplateVOMock
			1 * catalogToolsMock.isFirstLevelCategory(categoryId, siteId) >> FALSE
			1 * catalogToolsMock.getCategoryDetail(siteId, categoryId,false) >> null
			1 * catalogToolsMock.getBccManagedCategory(null)
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(CATEGORY_VO, null)
			1 * requestMock.serviceParameter(BBBSearchBrowseConstants.OPARAM_SUBCAT, requestMock, responseMock)
		
	}
	
	def"service method.This TC is when BBBSystemException thrown in service method"(){
		given:
			testObj = Spy()
			testObj.setLandingTemplateManager(landingTemplateManagerMock)
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			String categoryId =  "cat12345"
			String fetchSubCategories = "false"
			String siteId = "BedBathUS"
			1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			1 * requestMock.getParameter(BBBCoreConstants.ID) >> categoryId
			1 * requestMock.getParameter("fetchSubCategories") >> fetchSubCategories
			2 * requestMock.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) >> FALSE
			2 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) >> TRUE
			CategoryVO subCategoryVOMock = new CategoryVO(categoryId:"cat33333")
			CategoryVO categoryVOMock = new CategoryVO(subCategories:[subCategoryVOMock])
			LandingTemplateVO landingTemplateVOMock = new LandingTemplateVO(mCategory:categoryVOMock,mL2CategoryCount:2,mL3CategoryCount:3)
			1 * landingTemplateManagerMock.getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId) >> landingTemplateVOMock
			1 * catalogToolsMock.isFirstLevelCategory(categoryId, siteId) >> {throw new BBBSystemException("Mock for BBBSystemException")}
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR, BBBCoreErrorConstants.ERROR_CAT_LANDING)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
			1 * responseMock.setStatus(404)
			1 * testObj.logDebug('request Parameters value[categoryId=cat12345][siteId=BedBathUS]')
			1 * testObj.logDebug('Max L3 Category from BCC : 3')
			1 * testObj.logDebug('Boost Paramter from BCC : cat33333;')
			1 * testObj.logDebug('Max L2 Category from BCC : 2')
			1 * testObj.logError('browse_1024: System Exception [Category Landing] from service of CategoryLandingDroplet ', _)
			1 * testObj.getCategoryContent('BedBathUS', 'cat12345')
			
	}
	
	def"service method.This TC is when BBBBusinessException thrown in service method"(){
		given:
			testObj = Spy()
			testObj.setLandingTemplateManager(landingTemplateManagerMock)
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setLoggingDebug(TRUE)
			String categoryId =  "cat12345"
			String fetchSubCategories = "false"
			String siteId = "BedBathUS"
			1 * requestMock.getParameter(BBBCoreConstants.SITE_ID) >> siteId
			1 * requestMock.getParameter(BBBCoreConstants.ID) >> categoryId
			1 * requestMock.getParameter("fetchSubCategories") >> fetchSubCategories
			2 * requestMock.getParameter(BBBSearchBrowseConstants.CATEGORY_LANDING_FLAG) >> FALSE
			2 * requestMock.getParameter(BBBSearchBrowseConstants.PRODUCT_LISTING_FLAG) >> TRUE
			CategoryVO subCategoryVOMock = new CategoryVO(categoryId:"cat33333")
			CategoryVO categoryVOMock = new CategoryVO(subCategories:[subCategoryVOMock])
			LandingTemplateVO landingTemplateVOMock = new LandingTemplateVO(mCategory:categoryVOMock,mL2CategoryCount:2,mL3CategoryCount:3)
			1 * landingTemplateManagerMock.getLandingTemplateData(BBBSearchBrowseConstants.CATEGORY_LANDING_TEMPLATE,categoryId,siteId) >> landingTemplateVOMock
			1 * catalogToolsMock.isFirstLevelCategory(categoryId, siteId) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
			
		when:
			testObj.service(requestMock, responseMock)
			
		then:
			1 * requestMock.setParameter(BBBCoreConstants.ERROR, BBBCoreErrorConstants.ERROR_CAT_LANDING)
			1 * requestMock.serviceLocalParameter(BBBCoreConstants.ERROR, requestMock, responseMock)
			1 * responseMock.setStatus(404)
			1 * testObj.logDebug('request Parameters value[categoryId=cat12345][siteId=BedBathUS]')
			1 * testObj.logDebug('Max L3 Category from BCC : 3')
			1 * testObj.logDebug('Boost Paramter from BCC : cat33333;')
			1 * testObj.logDebug('Max L2 Category from BCC : 2')
			1 * testObj.logError('browse_1025: Business Exception [Category Landing] from service of CategoryLandingDroplet ', _)
			1 * testObj.getCategoryContent('BedBathUS', 'cat12345')
			
	}
	
	////////////////////////////////////TCs for service ends////////////////////////////////////////
	
	/////////////////////////////////TCs for getSubCategories starts////////////////////////////////////////
	//Signature : public List <CategoryVO> getSubCategories(final String siteId,final String categoryId, String boostParam) ////

	def"getSubCategories. This Tc is when BBBBusinessException thrown"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setSearchManager(searchManagerMock)
			testObj.setLoggingDebug(TRUE)
			String siteId = "BedBathUS"
			String categoryId = "cat12345"
			String boostParam = ""
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, siteId+ROOT_CATEGORY) >> null
			1 * searchManagerMock.getCategoryTree(_) >> {throw new BBBBusinessException("Mock for BBBBusinessException")}
		when:
			List<CategoryVO> results = testObj.getSubCategories(siteId, categoryId, boostParam)
		then:
			results == null
			1 * testObj.logError('BBBBusinessException Generated in Underlying Search Procedure : Mock for BBBBusinessException')
			1 * testObj.logDebug('Exiting method getSubCategories')
			1 * testObj.logDebug('setting checkPhantom flag as true.')
			1 * testObj.logDebug('Entering getSubCategories method')
			1 * testObj.logDebug('START: Call to fetch Sub Category List')
	}
	
	def"getSubCategories. This Tc is when BBBSystemException thrown"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setSearchManager(searchManagerMock)
			testObj.setLoggingDebug(TRUE)
			String siteId = "BedBathUS"
			String categoryId = "cat12345"
			String boostParam = ""
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, siteId+ROOT_CATEGORY) >> null
			1 * searchManagerMock.getCategoryTree(_) >> {throw new BBBSystemException("Mock for BBBSystemException")}
		when:
			List<CategoryVO> results = testObj.getSubCategories(siteId, categoryId, boostParam)
		then:
			results == null
			1 * testObj.logError('BBBSystemException Generated in Underlying Search Procedure : Mock for BBBSystemException')
			1 * testObj.logDebug('Exiting method getSubCategories')
			1 * testObj.logDebug('setting checkPhantom flag as true.')
			1 * testObj.logDebug('Entering getSubCategories method')
			1 * testObj.logDebug('START: Call to fetch Sub Category List')
	}
	
	def"getSubCategories. This Tc is when Exception thrown"(){
		given:
			testObj = Spy()
			testObj.setCatalogTools(catalogToolsMock)
			testObj.setSearchManager(searchManagerMock)
			testObj.setLoggingDebug(TRUE)
			String siteId = "BedBathUS"
			String categoryId = "cat12345"
			String boostParam = ""
			1 * catalogToolsMock.getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, siteId+ROOT_CATEGORY) >> null
			1 * searchManagerMock.getCategoryTree(_) >> {throw new Exception("Mock for Exception")}
		when:
			List<CategoryVO> results = testObj.getSubCategories(siteId, categoryId, boostParam)
		then:
			results == null
			1 * testObj.logError('Some Exception Generated in Underlying Search Procedure : Mock for Exception')
			1 * testObj.logDebug('Exiting method getSubCategories')
			1 * testObj.logDebug('setting checkPhantom flag as true.')
			1 * testObj.logDebug('Entering getSubCategories method')
			1 * testObj.logDebug('START: Call to fetch Sub Category List')
	}
	
	////////////////////////////////////TCs for getSubCategories ends////////////////////////////////////////
}
