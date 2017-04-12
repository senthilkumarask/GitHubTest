package com.bbb.commerce.catalog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import atg.commerce.pricing.priceLists.PriceListManager;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.userprofiling.Profile;

import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.CollectionProductVO;
import com.bbb.commerce.catalog.vo.CreditCardTypeVO;
import com.bbb.commerce.catalog.vo.EcoFeeSKUVO;
import com.bbb.commerce.catalog.vo.GiftWrapVO;
import com.bbb.commerce.catalog.vo.ImageVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.RegistryCategoryMapVO;
import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.catalog.vo.RollupTypeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.SiteVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.commerce.catalog.vo.ThresholdVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.bbb.search.bean.result.SortOptionVO;
import com.bbb.utils.BBBUtility;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBCatalogTools extends BaseTestCase {
	public void testGetSiteDetailFromSiteId() throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		SiteVO site=catalogTools.getSiteDetailFromSiteId(siteId);
		assertNotNull(site);

	}

	public void testCleranceProducts() throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		String clearanceCatUS=(String) getObject("clearanceCatUS");
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
//		String clearanceCatCanada=(String) getObject("clearanceCatCanada");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext("BedBathUS"));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		System.out.println("Current Site Id Context for BedBathUS:" + SiteContextManager.getCurrentSite().getId());
		String clearanceCatBuyBuyBaby=(String) getObject("clearanceCatBuyBuyBaby");	
		catalogTools.setLoggingDebug(true);
		List<ProductVO> listPVOsUS=catalogTools.getClearanceProducts("BedBathUS",
				clearanceCatUS);

		siteContextManager.popSiteContext(BBBSiteContext
				.getBBBSiteContext("BedBathUS"));

		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext("BuyBuyBaby"));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		System.out.println("Current Site Id Context for Baby:" + SiteContextManager.getCurrentSite().getId());
//		List<ProductVO> listPVOsCanada=catalogTools.getClearanceProducts("BedBathCanada",clearanceCatCanada);
		List<ProductVO> listPVOsBuyBuyBaby=catalogTools.getClearanceProducts("BuyBuyBaby",
				clearanceCatBuyBuyBaby);
		siteContextManager.popSiteContext(BBBSiteContext
				.getBBBSiteContext("BuyBuyBaby"));

		addObjectToAssert("listPVOsUS",listPVOsUS.size());
//		addObjectToAssert("listPVOsCanada",listPVOsCanada.size());
		assertNotNull(listPVOsBuyBuyBaby);
		addObjectToAssert("listPVOsBuyBuyBaby",listPVOsBuyBuyBaby.size());
	}

	public void testGetCollegeCollections() throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		//catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String categoryId= (String) getObject("categoryId");
		try{
			List<CollectionProductVO> productCollectionList=catalogTools.getCollegeCollections(siteId, categoryId);
			assertNotNull("college collection list is null",productCollectionList);
			assertFalse("college collection list is empty",productCollectionList.isEmpty());
			for(int i=0;i<productCollectionList.size();i++)
			{
				System.out.println(i+"th product id is [ "+productCollectionList.get(i).getProductId()+" ]");
				assertNotNull(i+"th product id is null",productCollectionList.get(i).getProductId());
			}
			productCollectionList=catalogTools.getCollegeCollections(siteId, "dummyCategoryId");
		}catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals("Exception code not same as CATEGORY_IS_NOT_COLLEGE_CATEGORY",
					e.getMessage(),BBBCatalogErrorCodes.CATEGORY_IS_NOT_COLLEGE_CATEGORY+":"+BBBCatalogErrorCodes.CATEGORY_IS_NOT_COLLEGE_CATEGORY);
		}
	}
	public void testGetGiftProducts()throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		try{
			List<ProductVO> productVOList=catalogTools.getGiftProducts(siteId);
			assertNotNull("List of gift products is null",productVOList);
			assertFalse("List of gift products is empty",productVOList.isEmpty());
			for(int i=0;i<productVOList.size();i++)
			{
				System.out.println(i+"th product id is [ "+productVOList.get(i).getProductId()+" ]");
				assertNotNull(i+"th product id is null",productVOList.get(i).getProductId());
			}
			productVOList=catalogTools.getGiftProducts(null);
		} 
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());

		}
	}
	public void testGetFirstActiveParentProductForSKU() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String skuId=(String) getObject("skuId");
		String productId=catalogTools.getFirstActiveParentProductForSKU(skuId);
		System.out.println("first parent product id"+productId);
	}
	public void testIsFreeShipping() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String skuId=(String) getObject("skuId");
		String shippingMethodId=(String) getObject("shippingMethodId");
		String notAvailableSkuId=(String) getObject("notAvailableSkuId");
		String skuIdNoFreeShip=(String)getObject("skuIdNoFreeShip");
		try{
			//check for working code
			Boolean isFreeShipping=catalogTools.isFreeShipping(siteId,skuId,shippingMethodId);
			System.out.println("isFreeShipping value for siteid "+siteId+" skuId "+skuId+" shippingMethodId "+shippingMethodId+" is"+isFreeShipping);
			assertNotNull("isFreeShipping is null for siteid "+siteId+" skuId "+skuId+" shippingMethodId "+shippingMethodId,isFreeShipping);
			//when sku has no free shipping methods
			isFreeShipping=catalogTools.isFreeShipping(siteId,skuIdNoFreeShip,shippingMethodId);
			System.out.println("isFreeShipping value for siteid "+siteId+" skuId "+skuIdNoFreeShip+" shippingMethodId "+shippingMethodId+" is"+isFreeShipping);
			assertFalse(isFreeShipping);

			//check for exception handling
			isFreeShipping=catalogTools.isFreeShipping(siteId,notAvailableSkuId,shippingMethodId);
		}

		catch(Exception e){
			System.out.println("Method testIsFreeShipping in exception"+e.getMessage());
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY+":"+BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
		}
	}


	public void testIsFixedPriceShipping() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String skuId=(String) getObject("skuId");
		String notAvailableSkuId=(String) getObject("notAvailableSkuId");
		String skuIdGiftCertTrue=(String)getObject("skuIdGiftCertTrue");
		try{
			//check for working code gift cert false
			Boolean isFixedPriceShipping=catalogTools.isFixedPriceShipping(siteId,skuId);
			System.out.println("isFixedPriceShipping value for skuId "+skuId +" is "+isFixedPriceShipping);
			assertNotNull("isFreeShipping is null for siteid "+siteId+" skuId "+skuId,isFixedPriceShipping);
			//gift cert is true
			isFixedPriceShipping=catalogTools.isFixedPriceShipping(siteId,skuIdGiftCertTrue);
			System.out.println("isFixedPriceShipping value for skuId "+skuIdGiftCertTrue +" is "+isFixedPriceShipping);
			assertNotNull("isFreeShipping is null for siteid "+siteId+" skuId "+skuIdGiftCertTrue,isFixedPriceShipping);
			//check for error handling
			isFixedPriceShipping=catalogTools.isFixedPriceShipping(siteId,notAvailableSkuId);
		}

		catch(Exception e){
			System.out.println("in exception");
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY+":"+BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
		}
	}

	////////********here
	
	public void testGetSkuDetails() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String validSkuId= (String) getObject("skuId");
		String futureDateSkuId= (String) getObject("futureDateSkuId");
		String product="prod10033";
		HashMap<String,String> map=new HashMap<String,String> ();
		map.put("color", "Ivory");
		map.put("size", "Full");
		try{
			//check for working code

			SKUDetailVO skuVO=catalogTools.getSKUDetails(siteId, validSkuId, true);
			assertNotNull(skuVO);
			assertNotNull(skuVO.getEligibleShipMethods());
			assertNotNull(skuVO.getDisplayName());
			assertNotNull(skuVO.getShippingSurcharge());
			assertNotNull(skuVO.getSize());
			assertNotNull(skuVO.getNonShippableStates());
			assertFalse(skuVO.isVdcSku());

			String skuId=catalogTools.getSKUDetails("BuyBuyBaby", product, map);
			System.out.println("skuId"+skuId);
			assertNotNull(skuId);
			skuVO=catalogTools.getSKUDetails(siteId, futureDateSkuId, false);

		}

		catch(Exception e){
			System.out.println("e.getMessage()--"+e.getMessage());
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
		}
	}
	public void testGetEverLivingSkuDetails() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String validSkuId= (String) getObject("skuId");
		String futureDateSkuId= (String) getObject("futureDateSkuId");
		String product="prod10031";
		HashMap<String,String> map=new HashMap<String,String> ();
		map.put("color", "White");
//		map.put("size", "Full");
		try{
			//check for working code

			SKUDetailVO skuVO=catalogTools.getEverLivingSKUDetails(siteId, validSkuId, true);
			assertNotNull(skuVO);
			assertNotNull(skuVO.getDisplayName());
			assertNotNull(skuVO.getShippingSurcharge());
			assertNotNull(skuVO.getSize());
			assertNotNull(skuVO.getNonShippableStates());
			assertFalse(skuVO.isVdcSku());

			String skuId=catalogTools.getSKUDetails("BuyBuyBaby", product, map);
			System.out.println("skuId"+skuId);
			assertNotNull(skuId);
			skuVO=catalogTools.getSKUDetails(siteId, futureDateSkuId, false);

		}

		catch(Exception e){
			System.out.println("e.getMessage()--"+e.getMessage());
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
		}
	}

	public void testGetEverLivingProductDetails() throws Exception {

		Profile profileItem = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");

		//MutableRepositoryItem profileItem = (MutableRepositoryItem) profileTool.getItemFromEmail("andrew@example.com");

		PriceListManager priceListManager = (PriceListManager)getObject("bbbPriceListManager");
		String listPriceId = (String)getObject("listPriceId");
		String salePriceId = (String)getObject("salePriceId");
		RepositoryItem listPriceListItem = priceListManager.getPriceList(listPriceId);
		RepositoryItem salePriceListItem = priceListManager.getPriceList(salePriceId);
		profileItem.setPropertyValue("priceList", listPriceListItem);
		profileItem.setPropertyValue("salePriceList", salePriceListItem);

		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String productId= (String) getObject("productId");
		String nonCollectionProductId= (String) getObject("nonCollectionProductId");

		ProductVO productVOtest=catalogTools.getEverLivingProductDetails(siteId, "prod60026", true, false, true);
		System.out.println("productVO1  "+productVOtest.getProductId());
		System.out.println("productVO1  name "+productVOtest.getName());

		try{
			CollectionProductVO productVO=(CollectionProductVO)catalogTools.getEverLivingProductDetails(siteId, "prod60026", true, false, true);
			System.out.println("product name "+productVO.getName());
			System.out.println("product description "+productVO.getLongDescription());
			System.out.println("product type"+productVO.getProductType());
			System.out.println("price range "+productVO.getPriceRangeDescription());
			System.out.println("product tabs"+productVO.getProductTabs());
			System.out.println("prouduct brands"+productVO.getProductBrand());
			System.out.println("product images"+productVO.getProductImages());
			assertTrue(productId+ " productId is not a collection or lead sku ",productVO.getLeadSKU());
			assertNotNull("ProductVO is null",productVO);
			System.out.println("productVO iscollection test   "+productVO.isCollection());
			List<ProductVO> childProducts=productVO.getChildProducts();
			assertNotNull(childProducts);
			assertFalse(childProducts.isEmpty());
			//SUB PRODUCT VO TEST
			for(int i=0;i<childProducts.size();i++)
			{
				ProductVO childRepoId=productVO.getChildProducts().get(i);
				System.out.println("product name "+childRepoId.getName());
				System.out.println("product description "+childRepoId.getLongDescription());
				System.out.println("product type"+childRepoId.getProductType());
				System.out.println("price range "+childRepoId.getPriceRangeDescription());
				System.out.println("product tabs"+childRepoId.getProductTabs());
				System.out.println("prouduct brands"+childRepoId.getProductBrand());
				System.out.println("product images"+childRepoId.getProductImages());
				assertNotNull(childRepoId.getProductId());
				if(childRepoId.getAttributesList()!=null){
					for(int g=0;g<childRepoId.getAttributesList().size();g++)
					{
						Set<String> keySet=childRepoId.getAttributesList().keySet();
						assertNotNull(keySet);
						Iterator<String> setIt=keySet.iterator();
						while(setIt.hasNext())
						{
							String key=setIt.next();
							assertNotNull(key);
							List<AttributeVO> attributeList=childRepoId.getAttributesList().get(key);
							for(int f=0;f<attributeList.size();f++)
							{
								AttributeVO attributeVO=attributeList.get(f);
								assertNotNull(attributeVO.getAttributeName());
								assertNotNull(attributeList.get(f).getAttributeDescrip());
								assertNotNull(attributeList.get(f).getAttributeName());
								String placeholder=attributeVO.getPlaceHolder();
								System.out.println("Placeholder value"+placeholder+" key value"+key);
								System.out.println(f +"th list attribute name"+attributeList.get(f).getAttributeName()+" of product id"+ childRepoId.getProductId());
								System.out.println(f +"th list attribute getAttributeDescrip"+attributeList.get(f).getAttributeDescrip()+" of product id"+ childRepoId.getProductId());
							}
						}
					}
				}
				else{
					System.out.println("child product attributes are null");
				}

				if(childRepoId.getChildSKUs()!=null && !childRepoId.getChildSKUs().isEmpty())
				{
					List<String> child=childRepoId.getChildSKUs();
					assertFalse(child.isEmpty());
					System.out.println("no of childre for product id "+childRepoId.getProductId()+   "is " +child.size());
					for(int w=0;w<child.size();w++)
					{
						assertNotNull(child.get(w));
						System.out.println(w+"th child sku id "+child.get(w));
					}
				}
		/*		Map<String, List<RollupTypeVO>> childRollUpAttributes=childRepoId.getRollupAttributes();
				System.out.println("childRollUpAttributes MAp---"+childRollUpAttributes);
				Set<String> rollUpKeySetForChilProduct=childRollUpAttributes.keySet();
				assertNotNull(rollUpKeySetForChilProduct);
				assertNotNull(childRollUpAttributes);
				for(String key:rollUpKeySetForChilProduct)
				{
					assertNotNull(key);
					System.out.println(childRepoId.getProductId()+ " Roll Upattribute List   "+childRollUpAttributes.get(key));
				}*/
				ImageVO images=childRepoId.getProductImages();
				if(images!=null)
				{

					System.out.println(childRepoId.getProductId()+"Zoom Image Index child for product ["+images.getZoomImageIndex()+"]");
					System.out.println(childRepoId.getProductId()+"Zoom Image Index forchild product ["+images.getZoomImage()+"]");
					System.out.println(childRepoId.getProductId()+"Zoom Image Index for child product ["+images.isAnywhereZoomAvailable()+"]");
				}
				else{
					System.out.println("No Images available for the CHILD product"+childRepoId.getProductId());
				}

			}
			//MAIN COLLECTION VO TEST
			assertNotNull(productVO.getName());
			System.out.println("Name"+productVO.getName());
			System.out.println("productVO.getProductBrand()---"+productVO.getProductBrand());
			if(productVO.getProductBrand()!=null){
				//assertNotNull(productVO.getProductBrand().getBrandId());
				System.out.println("brandID"+productVO.getProductBrand().getBrandId());
			}
			if(productVO.getAttributesList()!=null && !productVO.getAttributesList().isEmpty())
			{
				Set<String> attributeKeyMainProduct=productVO.getAttributesList().keySet();
				assertNotNull(attributeKeyMainProduct);
				for(String key:attributeKeyMainProduct)
				{
					assertNotNull(key);
					List<AttributeVO> attributeList=productVO.getAttributesList().get(key);
					for(int f=0;f<attributeList.size();f++)
					{
						AttributeVO attributeVO=attributeList.get(f);
						assertNotNull(attributeVO.getAttributeName());
						String placeholder=attributeVO.getPlaceHolder();
						assertEquals(key,placeholder);
						System.out.println("key---"+key);
						System.out.println("placeholder---"+placeholder);
						System.out.println(f +"th list attribute name "+attributeList.get(f).getAttributeName()+" of product id"+ productVO.getProductId());
					}
				}
			}
			ImageVO images=productVO.getProductImages();
			if(images!=null)
			{
				System.out.println(productVO.getProductId()+" main product ThumbNail Image for product ["+images.getThumbnailImage()+"]");
				System.out.println("Large Image for product ["+images.getLargeImage()+"]");
				System.out.println("Small Image for product ["+images.getSmallImage()+"]");
				System.out.println("Zoom Image Index for product ["+images.getZoomImageIndex()+"]");
				System.out.println("Zoom Image Index for product ["+images.getZoomImage()+"]");
				System.out.println("Zoom Image Index for product ["+images.isAnywhereZoomAvailable()+"]");
			}
			else{
				System.out.println("No Images available for the MAin product "+productVO.getProductId());
			}
			List<String> collectionRollUp=productVO.getCollectionRollUp();
			System.out.println("collectionRollUp  "+collectionRollUp);
			System.out.println("tabs"+productVO.getProductTabs().get(0).getTabName());
			assertNotNull(productVO);
			assertNotNull(productVO.getProductTabs());
			assertNotNull(productVO.getProductTabs().get(0).getTabName());
			System.out.println("getLeadSKU   "+productVO.getLeadSKU());
			assertTrue(productVO.isShowImagesInCollection());
			ProductVO productVO1=catalogTools.getProductDetails(siteId, nonCollectionProductId);
			System.out.println("Name"+productVO1.getName());
			assertNotNull(productVO1);
			System.out.println("short"+productVO1.getShortDescription());
			@SuppressWarnings("unused")
			ProductVO productVO2=catalogTools.getProductDetails(siteId, "prod10015");

		}

		catch(Exception e){
			System.out.println("Ever living exception:"+e);
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
		}
	}
	
	
	public void testGetProductDetails() throws Exception {

		Profile profileItem = (Profile) getRequest().resolveName("/atg/userprofiling/Profile");

		//MutableRepositoryItem profileItem = (MutableRepositoryItem) profileTool.getItemFromEmail("andrew@example.com");

		PriceListManager priceListManager = (PriceListManager)getObject("bbbPriceListManager");
		String listPriceId = (String)getObject("listPriceId");
		String salePriceId = (String)getObject("salePriceId");
		RepositoryItem listPriceListItem = priceListManager.getPriceList(listPriceId);
		RepositoryItem salePriceListItem = priceListManager.getPriceList(salePriceId);
		profileItem.setPropertyValue("priceList", listPriceListItem);
		profileItem.setPropertyValue("salePriceList", salePriceListItem);

		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String productId= (String) getObject("productId");
		String nonCollectionProductId= (String) getObject("nonCollectionProductId");

		ProductVO productVOtest=catalogTools.getProductDetails(siteId, "prod10051");
		System.out.println("productVO1  "+productVOtest.getProductId());
		System.out.println("productVO1  name "+productVOtest.getName());

		try{
			CollectionProductVO productVO=(CollectionProductVO)catalogTools.getProductDetails(siteId, productId);
			System.out.println("product name "+productVO.getName());
			System.out.println("product description "+productVO.getLongDescription());
			System.out.println("product type"+productVO.getProductType());
			System.out.println("price range "+productVO.getPriceRangeDescription());
			System.out.println("product tabs"+productVO.getProductTabs());
			System.out.println("prouduct brands"+productVO.getProductBrand());
			System.out.println("product images"+productVO.getProductImages());
			assertTrue(productId+ " productId is not a collection or lead sku ",productVO.getLeadSKU());
			assertNotNull("ProductVO is null",productVO);
			System.out.println("productVO iscollection test   "+productVO.isCollection());
			List<ProductVO> childProducts=productVO.getChildProducts();
			assertNotNull(childProducts);
			assertFalse(childProducts.isEmpty());
			//SUB PRODUCT VO TEST
			for(int i=0;i<childProducts.size();i++)
			{
				ProductVO childRepoId=productVO.getChildProducts().get(i);
				System.out.println("product name "+childRepoId.getName());
				System.out.println("product description "+childRepoId.getLongDescription());
				System.out.println("product type"+childRepoId.getProductType());
				System.out.println("price range "+childRepoId.getPriceRangeDescription());
				System.out.println("product tabs"+childRepoId.getProductTabs());
				System.out.println("prouduct brands"+childRepoId.getProductBrand());
				System.out.println("product images"+childRepoId.getProductImages());
				assertNotNull(childRepoId.getProductId());
				if(childRepoId.getAttributesList()!=null){
					for(int g=0;g<childRepoId.getAttributesList().size();g++)
					{
						Set<String> keySet=childRepoId.getAttributesList().keySet();
						assertNotNull(keySet);
						Iterator<String> setIt=keySet.iterator();
						while(setIt.hasNext())
						{
							String key=setIt.next();
							assertNotNull(key);
							List<AttributeVO> attributeList=childRepoId.getAttributesList().get(key);
							for(int f=0;f<attributeList.size();f++)
							{
								AttributeVO attributeVO=attributeList.get(f);
								assertNotNull(attributeVO.getAttributeName());
								assertNotNull(attributeList.get(f).getAttributeDescrip());
								assertNotNull(attributeList.get(f).getAttributeName());
								String placeholder=attributeVO.getPlaceHolder();
								System.out.println("Placeholder value"+placeholder+" key value"+key);
								System.out.println(f +"th list attribute name"+attributeList.get(f).getAttributeName()+" of product id"+ childRepoId.getProductId());
								System.out.println(f +"th list attribute getAttributeDescrip"+attributeList.get(f).getAttributeDescrip()+" of product id"+ childRepoId.getProductId());
							}
						}
					}
				}
				else{
					System.out.println("child product attributes are null");
				}

				if(childRepoId.getChildSKUs()!=null && !childRepoId.getChildSKUs().isEmpty())
				{
					List<String> child=childRepoId.getChildSKUs();
					assertFalse(child.isEmpty());
					System.out.println("no of childre for product id "+childRepoId.getProductId()+   "is " +child.size());
					for(int w=0;w<child.size();w++)
					{
						assertNotNull(child.get(w));
						System.out.println(w+"th child sku id "+child.get(w));
					}
				}
				
				Map<String, List<RollupTypeVO>> childRollUpAttributes = childRepoId.getPrdRelationRollup();
				System.out.println("childRollUpAttributes MAp---"+childRollUpAttributes);
				Set<String> rollUpKeySetForChilProduct=childRollUpAttributes.keySet();
				assertNotNull(rollUpKeySetForChilProduct);
				assertNotNull(childRollUpAttributes);
				for(String key:rollUpKeySetForChilProduct)
				{
					assertNotNull(key);
					System.out.println(childRepoId.getProductId()+ " Roll Upattribute List   "+childRollUpAttributes.get(key));
				}
				ImageVO images=childRepoId.getProductImages();
				if(images!=null)
				{

					System.out.println(childRepoId.getProductId()+"Zoom Image Index child for product ["+images.getZoomImageIndex()+"]");
					System.out.println(childRepoId.getProductId()+"Zoom Image Index forchild product ["+images.getZoomImage()+"]");
					System.out.println(childRepoId.getProductId()+"Zoom Image Index for child product ["+images.isAnywhereZoomAvailable()+"]");
				}
				else{
					System.out.println("No Images available for the CHILD product"+childRepoId.getProductId());
				}

			}
			//MAIN COLLECTION VO TEST
			assertNotNull(productVO.getName());
			System.out.println("Name"+productVO.getName());
			System.out.println("productVO.getProductBrand()---"+productVO.getProductBrand());
			if(productVO.getProductBrand()!=null){
				//assertNotNull(productVO.getProductBrand().getBrandId());
				System.out.println("brandID"+productVO.getProductBrand().getBrandId());
			}
			if(productVO.getAttributesList()!=null && !productVO.getAttributesList().isEmpty())
			{
				Set<String> attributeKeyMainProduct=productVO.getAttributesList().keySet();
				assertNotNull(attributeKeyMainProduct);
				for(String key:attributeKeyMainProduct)
				{
					assertNotNull(key);
					List<AttributeVO> attributeList=productVO.getAttributesList().get(key);
					for(int f=0;f<attributeList.size();f++)
					{
						AttributeVO attributeVO=attributeList.get(f);
						assertNotNull(attributeVO.getAttributeName());
						String placeholder=attributeVO.getPlaceHolder();
						assertEquals(key,placeholder);
						System.out.println("key---"+key);
						System.out.println("placeholder---"+placeholder);
						System.out.println(f +"th list attribute name "+attributeList.get(f).getAttributeName()+" of product id"+ productVO.getProductId());
					}
				}
			}
			ImageVO images=productVO.getProductImages();
			if(images!=null)
			{
				System.out.println(productVO.getProductId()+" main product ThumbNail Image for product ["+images.getThumbnailImage()+"]");
				System.out.println("Large Image for product ["+images.getLargeImage()+"]");
				System.out.println("Small Image for product ["+images.getSmallImage()+"]");
				System.out.println("Zoom Image Index for product ["+images.getZoomImageIndex()+"]");
				System.out.println("Zoom Image Index for product ["+images.getZoomImage()+"]");
				System.out.println("Zoom Image Index for product ["+images.isAnywhereZoomAvailable()+"]");
			}
			else{
				System.out.println("No Images available for the MAin product "+productVO.getProductId());
			}
			List<String> collectionRollUp=productVO.getCollectionRollUp();
			System.out.println("collectionRollUp  "+collectionRollUp);
			System.out.println("tabs"+productVO.getProductTabs().get(0).getTabName());
			assertNotNull(productVO);
			assertNotNull(productVO.getProductTabs());
			assertNotNull(productVO.getProductTabs().get(0).getTabName());
			System.out.println("getLeadSKU   "+productVO.getLeadSKU());
			assertTrue(productVO.isShowImagesInCollection());
			ProductVO productVO1=catalogTools.getProductDetails(siteId, nonCollectionProductId);
			System.out.println("Name"+productVO1.getName());
			assertNotNull(productVO1);
			System.out.println("short"+productVO1.getShortDescription());
			@SuppressWarnings("unused")
			ProductVO productVO2=catalogTools.getProductDetails(siteId, "prod10015");

		}

		catch(Exception e){
			System.out.println("PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY");
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
		}
	}

	public void testCategoryDetail() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String categoryId=(String) getObject("categoryId");
		
		getRequest().setParameter("siteId", siteId);
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		
		
		try{
			CategoryVO categoryVO=catalogTools.getCategoryDetail(siteId,categoryId,false);
			assertNotNull(categoryVO);
			List<CategoryVO> subCategories=(List<CategoryVO>)categoryVO.getSubCategories();
			assertNotNull(subCategories);
			assertNotNull(categoryVO.getCategoryName());
			assertNotNull(categoryVO.getCategoryId());
			System.out.println("level 1---------------------- name "+categoryVO.getCategoryName());
			System.out.println("level 1 ---------------------- CategoryId "+categoryVO.getCategoryId());
			List<CategoryVO> sub=categoryVO.getSubCategories();
			if(sub!=null){
				System.out.println("---------level1 children--------------------------------");
				for(int i=0;i<sub.size();i++){
					System.out.println("---------level2 "+i+"th--------------child--------------------------------");
					assertNotNull(sub.get(i).getCategoryName());
					assertNotNull(sub.get(i).getCategoryId());
					System.out.println("---------level2 name "+sub.get(i).getCategoryName());
					System.out.println("---------level2 getCategoryId "+sub.get(i).getCategoryId());

					List<CategoryVO> sub2=sub.get(i).getSubCategories();
					if(sub2!=null){
						System.out.println("---------level2 children--------------------------------");
						for(int g=0;g<sub2.size();g++){
							System.out.println("---------level3 "+g+"th--------------child--------------------------------");
							System.out.println("---------level3  in subcategories---------------level 3");
							assertNotNull(sub2.get(g).getCategoryName());
							assertNotNull(sub2.get(g).getCategoryId());
							System.out.println("---------level3 name "+sub2.get(g).getCategoryName());
							System.out.println("---------level3 getCategoryId "+sub2.get(g).getCategoryId());


						}
					}
				}

			}	
			String	categoryIdNotAvailable=(String) getObject("categoryIdNotAvailable");
			catalogTools.getCategoryDetail(siteId,categoryIdNotAvailable,false);
		}
		catch(Exception e){
			System.out.println(" catehory is disabled in exception "+e.getMessage());
			assertTrue(e.getMessage().contains(BBBCatalogErrorCodes.CATEGORY_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY));
		}

	}


	public void testGetNonShippableStatesForSku() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String skuId=(String) getObject("skuId");
		String stateId=(String) getObject("stateId");
		String notAvailableSkuId=(String) getObject("notAvailableSkuId");
		String skuIdNoNonShipStates=(String)getObject("skuIdNoNonShipStates");
		try{
			Boolean isNexus=catalogTools.isNexusState(siteId, stateId);
			assertNotNull(isNexus);

			//getNonShippableStatesForSku check
			List<StateVO> nonShippableStatesVO=catalogTools.getNonShippableStatesForSku(siteId,skuId);
			System.out.println("nonShippableStatesVO---"+nonShippableStatesVO);
			assertNotNull(nonShippableStatesVO);
			assertFalse(nonShippableStatesVO.isEmpty());
			for(int i=0;i<nonShippableStatesVO.size();i++){
				System.out.println("state Code  "+nonShippableStatesVO.get(i).getStateName());
				assertNotNull(nonShippableStatesVO.get(i).getStateName());
			}

			//test when no non shippable state exists for sku
			nonShippableStatesVO=catalogTools.getNonShippableStatesForSku(siteId,skuIdNoNonShipStates);
			System.out.println("nonShippableStatesVO for skuid "+skuIdNoNonShipStates+" is "+nonShippableStatesVO);
			assertNull(nonShippableStatesVO);
			nonShippableStatesVO=catalogTools.getNonShippableStatesForSku(siteId,notAvailableSkuId);
		}
		catch(Exception e){
			System.out.println("in exception");
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY+":"+BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
		}
	}
	

	//test getNonShippableStatesForSku when input is null
	public void testGetNonShippableStatesForSkuInputNull() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String skuId=(String) getObject("skuId");
		try{
			@SuppressWarnings("unused")
			List<StateVO> nonShippableStatesVO=catalogTools.getNonShippableStatesForSku(null,skuId);
		}catch(Exception e){
			System.out.println("in exception");
			assertEquals("Exception code not same as input parameter is null",
					e.getMessage(),BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL+":"+BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}
	}
	public void testIsNexus() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String stateId=(String) getObject("stateId");
		String stateId2=(String) getObject("stateId2");
		try{
			Boolean isNexus=catalogTools.isNexusState(siteId, stateId);
			System.out.println("isNexus"+isNexus);
			assertNotNull(isNexus);
			assertFalse(isNexus);
			Boolean isNexus2=catalogTools.isNexusState(siteId, stateId2);
			System.out.println("isNexus"+isNexus2);
			assertNotNull(isNexus2);
			assertTrue(isNexus2);
			isNexus=catalogTools.isNexusState("invalidsite", stateId);
		}catch(Exception e){
			System.out.println("in exception");
			assertEquals("Exception code not same as site not avilable in repository",
					e.getMessage(),BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY+":"+BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
		}
	}
	//to test that exception is thrown  when null parameter is passed to isNexus
	public void testIsNexusInputNull() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String stateId=(String) getObject("stateId");
		try{
			@SuppressWarnings("unused")
			Boolean isNexus=catalogTools.isNexusState(null, stateId);
		}catch(BBBBusinessException e){
			System.out.println("in exception");
			assertEquals("Exception code not same as input parameter is null",
					e.getMessage(),BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL+":"+BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}
	}

	public void testShippingMethodsForSku() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String skuId=(String) getObject("skuId");
		String notAvailableSkuId=(String) getObject("notAvailableSkuId");
		String skuIdNotHavingShippingMethods=(String)getObject("skuIdNotHavingShippingMethods");
		//getShippingMethodsForSku
		try{
			List<ShipMethodVO> shipMethodVOList=null;//catalogTools.getShippingMethodsForSku(siteId, skuId);
			assertNotNull(shipMethodVOList);
			assertFalse(shipMethodVOList.isEmpty());
			for(int i=0;i<shipMethodVOList.size();i++){
				System.out.println("ShipMethodId  "+shipMethodVOList.get(i).getShipMethodId());
				assertNotNull(shipMethodVOList.get(i).getShipMethodId());
			}

			//Test when sku itself doesnot have shipping method
			shipMethodVOList= null;//catalogTools.getShippingMethodsForSku(siteId, skuIdNotHavingShippingMethods);
			shipMethodVOList=null;//catalogTools.getShippingMethodsForSku(siteId, notAvailableSkuId);
		}
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY+":"+BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
		}
	}
	public void testCreditCardTypes() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");

		try{
			//getCreditCardTypes
			List<CreditCardTypeVO> creditCardTypeVO=catalogTools.getCreditCardTypes(siteId);
			System.out.println("creditCardTypeVO---"+creditCardTypeVO);
			assertNotNull(creditCardTypeVO);
			assertFalse(creditCardTypeVO.isEmpty());
			for(int i=0;i<creditCardTypeVO.size();i++){
				System.out.println("credit card code"+creditCardTypeVO.get(i).getName());
				assertNotNull(creditCardTypeVO.get(i).getName());
			}
		}
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.CATEGORY_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
		}
	}
	public void testDefaultShippingMethod() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);

		String siteId= (String) getObject("siteId");
		try{
			//getDefaultShippingMethod
			ShipMethodVO shipMethodVO=catalogTools.getDefaultShippingMethod(siteId);
			assertNotNull(shipMethodVO);

		}

		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.CATEGORY_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
		}
	}

	public void testShippingCostForGiftCard() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String shippingMethodId=(String) getObject("shippingMethodId");
		String siteId= (String) getObject("siteId");
		try{
			//getDefaultShippingMethod
			Double cost=catalogTools.shippingCostForGiftCard(siteId,shippingMethodId);
			System.out.println("cost---"+cost);
			assertNotNull(cost);

		}
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.NO_GIFT_CARD_FOR_SHIPPING_ID_IN_REPOSITORY);
		}
	}



	public void testGetSKUSurcharge() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String skuId= (String) getObject("skuId");
		String siteId= (String) getObject("siteId");
		String notAvailableSkuId=(String) getObject("notAvailableSkuId");
		String noSurchargeSkuId=(String)getObject("noSurchargeSkuId");
		String FreeshippingMethodId=(String)getObject("FreeshippingMethodId");
		String FreeskuId=(String)getObject("FreeskuId");
		System.out.println("noSurchargeSkuId "+noSurchargeSkuId);
		String shippingMethodId=(String) getObject("shippingMethodId");
		try{
			//getDefaultShippingMethod
			Double surcharge=catalogTools.getSKUSurcharge(siteId, skuId,shippingMethodId);
			assertNotNull(surcharge);
			System.out.println("surcharge--value for skuid-"+skuId+" is "+surcharge);
			surcharge=catalogTools.getSKUSurcharge(siteId, noSurchargeSkuId,shippingMethodId);
			System.out.println("surcharge--value for skuid-"+noSurchargeSkuId+" is "+surcharge);
			//test when shipping method is free for the sku and surcharge is 0.0
			surcharge=catalogTools.getSKUSurcharge(siteId, FreeskuId,FreeshippingMethodId);
			System.out.println("surcharge--value for skuid-"+FreeskuId+" and shipping method "+FreeshippingMethodId+"is "+surcharge);
			surcharge=catalogTools.getSKUSurcharge(siteId, notAvailableSkuId,shippingMethodId);

		}

		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY+":"+BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
		}
	}

	public void testGetRegistryTypes() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		String siteId= (String) getObject("siteId");
		catalogTools.setLoggingDebug(true);
		try{
			List<RegistryTypeVO> registry=catalogTools.getRegistryTypes(siteId);
			System.out.println("registry"+registry);
			for(int i=0;i<registry.size();i++){
				System.out.println("registry name"+registry.get(i).getRegistryDescription());
			}
			assertNotNull(registry);
			assertFalse(registry.isEmpty());
		}	catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
		}
	}

	public void testGetShippingFee() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		String siteId= (String) getObject("siteId");
		String shippingMethodId= (String) getObject("shippingMethodId");
		String stateId= (String) getObject("stateId");
		String nullStateId=(String) getObject("nullStateId");
		double subTotalAmount= (Double) getObject("subTotalAmount");
		double subTotalAmount1= (Double) getObject("subTotalAmount1");
		catalogTools.setLoggingDebug(true);
		/*Double charge=catalogTools.getShippingFee(siteId, shippingMethodId, stateId, subTotalAmount);
		System.out.println("charge   "+charge);
		assertNotNull(charge);
		charge=catalogTools.getShippingFee(siteId, shippingMethodId, null, subTotalAmount1);
		assertNotNull(charge);
		charge=catalogTools.getShippingFee(siteId, shippingMethodId, nullStateId, subTotalAmount1);
		assertNotNull(charge);*/


	}
	public void testIsGiftCardItem() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String skuId= (String) getObject("skuId");
		String isGiftCardSkuId= (String) getObject("isGiftCardSkuId");
		Boolean isGiftCard=catalogTools.isGiftCardItem(siteId, skuId);
		System.out.println("isGift   "+isGiftCard);
		assertFalse(isGiftCard);
		isGiftCard=catalogTools.isGiftCardItem(siteId, isGiftCardSkuId);
		System.out.println("isGift   "+isGiftCard);
		assertTrue(isGiftCard);


	}

	public void testisGiftWrapItem() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String skuId= (String) getObject("skuId");
		String isGiftWrapSkuId= (String) getObject("isGiftWrapSkuId");

		System.out.println("skuId"+skuId);
		System.out.println("isGiftWrapSkuId"+isGiftWrapSkuId);
		Boolean isGiftWrap=catalogTools.isGiftWrapItem(siteId, isGiftWrapSkuId);
		System.out.println("isGiftWrap   "+isGiftWrap);

		assertTrue(isGiftWrap);

		isGiftWrap=catalogTools.isGiftWrapItem(siteId, skuId);
		System.out.println("isGiftWrap 2  "+isGiftWrap);

		assertFalse(isGiftWrap);
	}

	public void testGetConfigValueByconfigType() throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String configType = (String) getObject("configType");
		Map<String,String> configMap = catalogTools.getConfigValueByconfigType(configType);
		//assertNotNull("Config Values are null for "+configType, configMap);
		System.out.println("configMap----"+configMap);
		Set<String> key=configMap.keySet();
		for(String keyValue:key){
			assertNotNull(keyValue);
			System.out.println("configMap value "+configMap.get(keyValue));
		}
		addObjectToAssert("configMap", configMap.size());
	}

	public void testGetAllValuesForKey() throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String configType = (String) getObject("configType");
		String key = (String) getObject("key");
		List<String> valueList = catalogTools.getAllValuesForKey(configType, key);
		//System.out.println();
		//assertNotNull("Config Values are null for "+configType+" and key "+key, valueList);
		addObjectToAssert("valueList",valueList.size());
	}
	public void testGetWrapSkuDetails() throws Exception
	{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		try{
			GiftWrapVO giftWrap=catalogTools.getWrapSkuDetails(siteId);
			System.out.println("giftWrap"+giftWrap.getWrapSkuPrice());
			System.out.println("giftWrap"+giftWrap.getWrapProductId());
			System.out.println("giftWrap"+giftWrap.getWrapSkuId());
			assertNotNull(giftWrap);
			assertNotNull(giftWrap.getWrapSkuPrice());
			assertNotNull(giftWrap.getWrapProductId());
			assertNotNull(giftWrap.getWrapSkuId());
			giftWrap=catalogTools.getWrapSkuDetails("invalidsiteid");
		}
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals("Exception code not same as site not avilable in repository",
					e.getMessage(),BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY+":"+BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
		}

	}
	public void testGetCommonGreetings() throws Exception
	{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		Map<String,String> greetingsMap=catalogTools.getCommonGreetings(siteId);
		assertNotNull(greetingsMap);
		if(greetingsMap!=null)
		{
			Set<String> key=greetingsMap.keySet();
			System.out.println("key--"+key);
			for(String greetings: key)
			{
				System.out.println("Map i"+greetings);
				assertNotNull("Greeting value is null",greetings);
			}
		}
		else{
			System.out.println("map is null");
		}
		assertNotNull(greetingsMap);

	}

	public void testIsPackNHoldWindow() throws Exception
	{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		Date date=new Date();
		try{
			Boolean ispackNHold=catalogTools.isPackNHoldWindow(siteId, date);
			System.out.println("is pack n hold "+ispackNHold);
			assertNotNull("is pack n hold boolean is null",ispackNHold);
			ispackNHold=catalogTools.isPackNHoldWindow(null, date);
		}
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals("Exception code not same as input parameter is null",
					e.getMessage(),BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL+":"+BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}
	}
	//to test when site id is invalid for pack n hold
	public void testPackNHoldSiteInvalid() throws Exception
	{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String invalidSiteId= (String) getObject("siteId");
		Date date=new Date();
		try{
			@SuppressWarnings("unused")
			Boolean ispackNHold=catalogTools.isPackNHoldWindow(invalidSiteId, date);
		}
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals("Exception code not same as SITE_NOT_AVAILABLE_IN_REPOSITORY",
					e.getMessage(),BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY+":"+BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
		}
	}

	public void testGetSkuThreshold() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String subDeptJdaNull=(String) getObject("subDeptJdaNull");
		String skuId= (String) getObject("skuId");
		String skuIdpresentinthreshold=(String)getObject("skuIdpresentinthreshold");
		try{

			ThresholdVO thresholdVO=catalogTools.getSkuThreshold(siteId, skuId);
			assertNotNull(thresholdVO);
			thresholdVO=catalogTools.getSkuThreshold(siteId, subDeptJdaNull);
			assertNotNull(thresholdVO);
			thresholdVO=catalogTools.getSkuThreshold(siteId, skuIdpresentinthreshold);
			assertNotNull(thresholdVO);
			thresholdVO=catalogTools.getSkuThreshold(siteId, "dummysku");
		}
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals("Exception code not same as sku not available in repsoitory",
					e.getMessage(),BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY+":"+BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
		}
	}

	public void testGetBrandDetails() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String brandId= (String) getObject("brandId");
		try{
			BrandVO brandVO=catalogTools.getBrandDetails(brandId, siteId);
			System.out.println(brandVO.getBrandId()+" brand id");
			assertNotNull(brandVO);
			assertNotNull(brandVO.getBrandId());
			//to check if exception is thrown when site
			brandVO=catalogTools.getBrandDetails(brandId, "notapplicablesite");
			assertNull("brand id is not null when site is not applicable",brandVO.getBrandId());
		}
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals("Exception code not same as site not availabl in repsoitory",
					e.getMessage(),BBBCatalogErrorCodes.BRAND_NOT_APPLICABLE_FOR_THE_ANY_SITE);
		}

	}
	public void testGetSiteTag() throws Exception
	{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		try{
			String sitetag=catalogTools.getSiteTag(siteId);
			System.out.println("site tag"+sitetag); 
			assertNotNull("site tag is null for site"+siteId,sitetag);
			sitetag=catalogTools.getSiteTag("invalidsite");
		}
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals("Exception code not same as site not availabl in repsoitory",
					e.getMessage(),BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY+":"+BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
		}
	}

	public void testGetPromotions() throws Exception
	{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String couponId= (String) getObject("couponId");

		RepositoryItem[] promoId=catalogTools.getPromotions(couponId);
		System.out.println("prmoid repository"+promoId.length);
		assertNotNull("promoid repo item is null for couponid "+couponId,promoId);
		for(int i=0;i<promoId.length;i++)
		{
			assertNotNull("promo repo id is null for couponid "+couponId,promoId[i].getRepositoryId());
		}

	}
	public void testGetPromotionId() throws Exception
	{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String couponId= (String) getObject("couponId");

		String promoId=catalogTools.getPromotionId(couponId);
		System.out.println("prmoid "+promoId);
		assertNotNull("promoid repo item is null for couponid "+couponId,promoId);

	}

	public void testGetSchoolDetails() throws Exception
	{
		/*BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String schoolId= (String) getObject("schoolId");
		System.out.println("schoolId in test"+schoolId);
		try{
			SchoolVO school=catalogTools.getSchoolDetailsById(schoolId);
			assertNotNull(school);
			assertNotNull(school.getSchoolId());
			System.out.println("id of school---"+school.getSchoolId());
			// test for exception in case of invalid schoolId which is not present in repository
			school=catalogTools.getSchoolDetailsById("testSchool");
		}
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals("Exception in school APi is not same as school id not present in Repository",
					e.getMessage(),BBBCatalogErrorCodes.SCHOOL_ID_NOT_AVAILABLE_IN_REPOSITORY);
		}*/
		
			/*
		   * TODO commenting the test case due to lack of test data. This needs to be
		   * pushed on QA with proper test data on week ending 18 Mar 2012.
		   */
		
		assertTrue(true);

	}

	public void testGetRollUpList() throws Exception
	{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String productId=(String) getObject("productId");
		String firstRollUpValue=(String) getObject("firstRollUpValue");
		String firstRollUpType=(String) getObject("firstRollUpType");
		String secondRollUpType=(String) getObject("secondRollUpType");
		List<RollupTypeVO> rollUpList=catalogTools.getRollUpList(productId, firstRollUpValue, firstRollUpType, secondRollUpType);
		System.out.println("rollUpList  "+rollUpList);
		assertNotNull(rollUpList);
		assertFalse(rollUpList.isEmpty());

		for(int i=0;i<rollUpList.size();i++){
			System.out.println(i+"th rollUpList value for rollupType "+secondRollUpType +"is  "+rollUpList.get(i).getRollupAttribute());
			assertNotNull(rollUpList.get(i).getRollupAttribute());
		}
	}

	public void testIsSKUBelowLine() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String skuId= (String) getObject("skuId");
		String caSiteId=(String)getObject("caSiteId");
		String skuIdForceBelowIsFalse=(String)getObject("skuIdForceBelowIsFalse");
		String skuIdEcomE=(String)getObject("skuIdEcomE");
		System.out.println("caSiteId "+caSiteId);
		boolean isSKUBelowLine=catalogTools.isSKUBelowLine(siteId, skuId);
		assertNotNull(isSKUBelowLine);
		System.out.println("isSKUBelowLine value for siteid "+siteId+"  and skuId " +skuId+ "is " +isSKUBelowLine);
		isSKUBelowLine=catalogTools.isSKUBelowLine(caSiteId, skuId);
		assertNotNull(isSKUBelowLine);
		System.out.println("isSKUBelowLine value for siteid "+caSiteId+"  and skuId " +skuId+ "is " +isSKUBelowLine);
		//to check when force below flag is set false for sku
		isSKUBelowLine=catalogTools.isSKUBelowLine(siteId, skuIdForceBelowIsFalse);
		assertNotNull(isSKUBelowLine);
		System.out.println("isSKUBelowLine value for siteid "+siteId+"  and skuId " +skuIdForceBelowIsFalse+ "is " +isSKUBelowLine);

		//To check when Ecom=e and site=CA
		isSKUBelowLine=catalogTools.isSKUBelowLine(caSiteId, skuIdEcomE);
		assertNotNull(isSKUBelowLine);
		System.out.println("isSKUBelowLine value for siteid "+caSiteId+"  and skuId " +skuIdEcomE+ "is " +isSKUBelowLine);

	}





	public void testIsLeafNode() throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String trueCategoryId= (String) getObject("trueCategoryId");
		String falseCategoryId= (String) getObject("falseCategoryId");
		boolean isLeafNode = catalogTools.isLeafCategory(siteId, trueCategoryId);
		assertTrue("Category ID : "+trueCategoryId+" should return TRUE",isLeafNode);
		isLeafNode = catalogTools.isLeafCategory(siteId, falseCategoryId);
		assertFalse("Category ID : "+falseCategoryId+" should return FALSE", isLeafNode);
	}

	public void testIsSkuAvailable() throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String skuId= (String) getObject("skuId");
		Boolean isSkuAvailable=catalogTools.isSKUAvailable(siteId, skuId);
		assertNotNull("sku available boolean is null",isSkuAvailable);
		assertTrue("sku is disabled hence assert failed",isSkuAvailable);
	}

	public void testGetCollegeProduct() throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		//catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String collegeId= (String) getObject("collegeId");
		try{
			List<ProductVO> collProductList=catalogTools.getCollegeProduct(collegeId, siteId);
			assertNotNull(collProductList);
			assertFalse(collProductList.isEmpty());
			System.out.println("collProductList  "+collProductList);
			System.out.println("no of products  "+collProductList.size());
			for(ProductVO product: collProductList){
				System.out.println("product id"+product.getProductId());
				assertNotNull("productId is null for college products",product.getProductId());

			}
			//To test for invalid college id exception
			collProductList=catalogTools.getCollegeProduct("invalidCollegid", siteId);
		}catch(Exception e){
		
			System.out.println("in exception"+e.getMessage());
			assertEquals("Exception code not same as no sku available for given college Id",
					e.getMessage(),BBBCatalogErrorCodes.NO_PRODUCT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_COLLEGE_ID+":"+BBBCatalogErrorCodes.NO_PRODUCT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_COLLEGE_ID);
		}
	}

	public void testGetDefaultCountryForSite()throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		try{
			String defaultCountry=catalogTools.getDefaultCountryForSite(siteId);
			assertNotNull("Default country for site is null",defaultCountry);
			System.out.println("defaultCountry  "+defaultCountry);
			//Test for invalid site id
			defaultCountry=catalogTools.getDefaultCountryForSite("site");
		}
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals("Exception code not same as site not available in repository",e.getMessage(),BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY+":"+BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
		}

	}

	public void testGetBopusEligibleStates()throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		try{
			List<String> stateList=catalogTools.getBopusEligibleStates(siteId);
			System.out.println("stateList  "+stateList);
			assertNotNull("List of Bopus eligible states is null",stateList);
			assertFalse("List of Bopus eligible states is empty",stateList.isEmpty());
			String siteIdNull=null;
			stateList=catalogTools.getBopusEligibleStates(siteIdNull);
		}
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals("input siteid is not null",e.getMessage(),BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL+":"+BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
		}
	}

	public void testGetExpectedDeliveryDate ()throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String shippingMethodId=(String) getObject("shippingMethodId");
		String siteId=(String) getObject("siteId");
		try{
			String expect=catalogTools.getExpectedDeliveryDate(shippingMethodId, siteId);
			System.out.println("expect  "+expect);
			assertNotNull("Expected delivery date string is null",expect);
			expect=catalogTools.getExpectedDeliveryDate(shippingMethodId,"AK",siteId);
		}
		catch(Exception e){
			System.out.println("in exception" + e.getMessage());
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_AVAILABLE_IN_REPOSITORY+":"+BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_AVAILABLE_IN_REPOSITORY);
		}
	}

	public void testGetParentCategoryForProduct()throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String productId=(String) getObject("productId");
		String siteId="BuyBuyBaby";
		Map<String,CategoryVO> parents=catalogTools.getParentCategoryForProduct(productId,siteId);
		assertNotNull(parents);
		assertFalse(parents.isEmpty());
		Set<String> key=parents.keySet();
		for(String kkeyVal:key)
		{
			assertNotNull(parents.get(kkeyVal));
			System.out.println(kkeyVal+ "th category id "+parents.get(kkeyVal).getCategoryId()+" name "+parents.get(kkeyVal).getCategoryName());
		}
	}

	public void testGetParentCategory()throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String categoryId=(String) getObject("categoryId");
		String rootCategoryId=(String) getObject("rootCategoryId");
		String siteId="BuyBuyBaby";
		Map<String,CategoryVO> parents=catalogTools.getParentCategory(categoryId,siteId);
		assertNotNull(parents);
		assertFalse(parents.isEmpty());
		Set<String> key=parents.keySet();
		for(String kkeyVal:key)
		{
			assertNotNull(parents.get(kkeyVal));
			System.out.println(kkeyVal+ "th category id "+parents.get(kkeyVal).getCategoryId()+" name "+parents.get(kkeyVal).getCategoryName());
		}
		parents=catalogTools.getParentCategory(rootCategoryId,siteId);
		System.out.println("rootcat "+parents);
	}
	public void testGetCanadaStoreLocatorInfo()throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		List<StoreVO> canadaStoreList=catalogTools.getCanadaStoreLocatorInfo();
		assertNotNull("canada store locator list is null",canadaStoreList);
		assertFalse("canada store locator list is empty",canadaStoreList.isEmpty());
		for(int i=0;i<canadaStoreList.size();i++){
			System.out.println("store id "+canadaStoreList.get(i).getStoreId());
			System.out.println("store type "+canadaStoreList.get(i).getStoreType());
			if(canadaStoreList.get(i).getStoreSpecialityVO()!=null && canadaStoreList.get(i).getStoreSpecialityVO().isEmpty()){
				for(int j=0;j<canadaStoreList.get(i).getStoreSpecialityVO().size();j++){
					System.out.println("image code  "+canadaStoreList.get(i).getStoreSpecialityVO().get(j).getCodeImage());
				}
			}
			System.out.println("store address "+canadaStoreList.get(i).getAddress()+" "+canadaStoreList.get(i).getCity()+" "+canadaStoreList.get(i).getPostalCode()+" "+canadaStoreList.get(i).getProvince());
			System.out.println("store opening timing "+canadaStoreList.get(i).getHours());
			System.out.println(" is display online:"+canadaStoreList.get(i).isDisplayOnline());
			assertNotNull(canadaStoreList.get(i).getStoreId());
			assertNotNull(canadaStoreList.get(i).getStoreType());
		}
	}

	public void testGetCollegeIdForSite()throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		
		SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
		try {
			siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(siteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}
		try{
			String categoryId=catalogTools.getCollegeIdForSite(siteId);
			assertNotNull("college id for site is null",categoryId);
			System.out.println("categoryId id "+categoryId);
		}catch(BBBBusinessException e){
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.NO_DEFAULT_COLLEGE_ID_FOR_SITE+":"+BBBCatalogErrorCodes.NO_DEFAULT_COLLEGE_ID_FOR_SITE);
		}
	}
	public void testIsFirstLevelCategory(){
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		Boolean isFirstLevelCategory;
		String siteId= (String) getObject("siteId");
		String categoryId = (String) getObject("categoryId");
		try{
			isFirstLevelCategory=catalogTools.isFirstLevelCategory(categoryId, siteId);
			assertTrue(isFirstLevelCategory);

		}catch(BBBBusinessException e){
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.NO_DEFAULT_COLLEGE_ID_FOR_SITE);
		}
		catch(BBBSystemException e){
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.NO_DEFAULT_COLLEGE_ID_FOR_SITE);
		}
	}
	public void testGetParentProductForSku() throws BBBBusinessException, BBBSystemException{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String skuId= (String) getObject("skuId");
		String parentProductId=catalogTools.getParentProductForSku(skuId);
		System.out.println("parentProductId  for sku"+parentProductId);
		
		 parentProductId=catalogTools.getParentProductForSku(skuId,true);
		System.out.println("get parent without checking if its active :parentProductId  for sku"+parentProductId);
		assertNotNull(parentProductId);
	}
	public void testGetSkuImages()throws BBBBusinessException, BBBSystemException{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String skuId= (String) getObject("skuId");
		ImageVO images=catalogTools.getSkuImages(skuId);
		System.out.println("images  "+images);
		assertNotNull(images);
	}
	public void testGetRootCollegeIdFrmConfig()throws BBBBusinessException, BBBSystemException{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId="BedBathUS";
		String id=catalogTools.getRootCollegeIdFrmConfig(siteId);
		System.out.println("id  "+id);
	}
	public void testGetTimeZones()throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		try{
			List<String> timezones=catalogTools.getTimeZones(siteId);
			assertNotNull("timezones for site is null",timezones);
			System.out.println("timezones "+timezones);
		}catch(BBBBusinessException e){
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.NO_DEFAULT_COLLEGE_ID_FOR_SITE);
		}
	}
	public void testGetcustomerCareEmailAddress()throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		try{
			String emailaddress=catalogTools.getcustomerCareEmailAddress(siteId);
			//	assertNotNull("emailaddress for site is null",emailaddress);
			System.out.println("emailaddress id "+emailaddress);
		}catch(BBBBusinessException e){
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.NO_DEFAULT_COLLEGE_ID_FOR_SITE);
		}
	}
	public void testGetStates() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);

		String siteId= (String) getObject("siteId");
		try{
			//getDefaultShippingMethod
			boolean showMilitaryStates = Boolean.parseBoolean((String)getObject("showMilitaryStates"));

			List<StateVO> stateVOList=catalogTools.getStates(siteId,showMilitaryStates, null);
			for(int i=0;i<stateVOList.size();i++){
				System.out.println("state name  "+stateVOList.get(i).getStateName());
			}
			System.out.println("stateVOList---"+stateVOList);
			assertNotNull(stateVOList);

		}
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.STATE_NOT_AVAILABLE_IN_REPOSITORY);
		}
	}
	public void testIsEcoFeeEligibleForState() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		//passing the values which return true
		String stateId1= (String) getObject("stateId1");
		Boolean isStateEligible1=catalogTools.isEcoFeeEligibleForState(stateId1);
		System.out.println("isStateEligible1   "+isStateEligible1);
		assertTrue(isStateEligible1);
		//passing the values which return false
		String stateId2= (String) getObject("stateId2");
		Boolean isStateEligible2=catalogTools.isEcoFeeEligibleForState(stateId2);
		System.out.println("isStateEligible2   "+isStateEligible2);
		assertFalse(isStateEligible2);
	}
	public void testIsEcoFeeEligibleForStore() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		//passing the values which return true
		String storeId1= (String) getObject("storeId1");
		Boolean isStoreEligible1=catalogTools.isEcoFeeEligibleForStore(storeId1);
		System.out.println("isStoreEligible1   "+isStoreEligible1);
		assertNotNull(isStoreEligible1);
		//passing the values which return false
		String storeId2= (String) getObject("storeId2");
		Boolean isStoreEligible2=catalogTools.isEcoFeeEligibleForStore(storeId2);
		System.out.println("isStoreEligible2   "+isStoreEligible2);
		assertFalse(isStoreEligible2);
	}
	public void testGetEcoFeeSKUDetailForState() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);

		String stateId= (String) getObject("stateId");
		String skuId= (String) getObject("skuId");
		EcoFeeSKUVO ecoFeeSKUVOState=catalogTools.getEcoFeeSKUDetailForState(stateId,skuId);
		System.out.println("ecoFeeSKUVOState.SkuId   "+ecoFeeSKUVOState.getFeeEcoSKUId());
		assertNotNull(ecoFeeSKUVOState.getFeeEcoSKUId());
		System.out.println("ecoFeeSKUVOState.ProductId   "+ecoFeeSKUVOState.getEcoFeeProductId());
		assertNotNull(ecoFeeSKUVOState.getEcoFeeProductId());
	}
	public void testGetEcoFeeSKUDetailForStore() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);

		String storeId= (String) getObject("storeId");
		String skuId= (String) getObject("skuId");
		try{
			EcoFeeSKUVO ecoFeeSKUVOState=catalogTools.getEcoFeeSKUDetailForStore(storeId,skuId);
			System.out.println("ecoFeeSKUVOState.SkuId   "+ecoFeeSKUVOState.getFeeEcoSKUId());
			assertNotNull(ecoFeeSKUVOState.getFeeEcoSKUId());
			System.out.println("ecoFeeSKUVOState.ProductId   "+ecoFeeSKUVOState.getEcoFeeProductId());
			assertNotNull(ecoFeeSKUVOState.getEcoFeeProductId());
		}catch(BBBBusinessException e){
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.NO_ECO_FEE_SKU_AVAILABLE_FOR_SKU+":"+BBBCatalogErrorCodes.NO_ECO_FEE_SKU_AVAILABLE_FOR_SKU);
		}
	}

	public void testGetMaxInventorySKUForProduct() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);

		String productId= (String) getObject("productId");		
		String siteId= (String) getObject("siteId");
		String skuId=catalogTools.getMaxInventorySKUForProduct(catalogTools.getProductDetails(siteId, productId, false,false,true), siteId);
		System.out.println("skuid final"+skuId);
	}

	public void testIsSkuExcluded() throws Exception {

		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String skuId = (String) getObject("skuId");
		String promotionCode = (String) getObject("promotionCode");
		Boolean isSkuExcluded = catalogTools.isSkuExcluded(skuId, promotionCode, true);
		System.out.println(" isSkuExcluded 1 "+isSkuExcluded+ "skuId "+skuId);
		assertNotNull( isSkuExcluded);

		Calendar c=Calendar.getInstance();
		c.setTimeInMillis(0);
		System.out.println("default date"+BBBUtility.getXMLCalendar(c.getTime()));

	}
	public void testSortSkuByCategory() throws Exception {

		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		// catalogTools.setLoggingDebug(true);
		List<String> skuIdList=new ArrayList<String>();
		skuIdList.add("sku40048");
		skuIdList.add("sku40069");
		skuIdList.add("sku40041"); 
		skuIdList.add("sku30005"); 
		skuIdList.add("sku30003"); 
		//has both
		String registryCode =(String) getObject("registryCode");
		String siteId= (String) getObject("siteId");
		Map<String,List<SKUDetailVO>> map =catalogTools.sortSkuByCategory(skuIdList, registryCode, siteId);
		Set<String> keyString =map.keySet();
		assertNotNull(map);
		assertFalse(map.isEmpty());
		System.out.println("****************************registryCode:"+registryCode+" *************siteId: "+siteId);
		for(String key:keyString){

			System.out.println("key  "+key +" list "+map.get(key));
			List<SKUDetailVO> list=map.get(key);
			for(int i=0;i<list.size();i++){
				System.out.println("skuId ****"+list.get(i).getSkuId());
			}
		}
	}

	public void testGetCategoryForRegistry() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String registryTypeId =(String) getObject("registryCode");
		Map<String,RegistryCategoryMapVO> map=null;
		try{
		
		map=catalogTools.getCategoryForRegistry(registryTypeId);
		assertFalse(map.isEmpty());
		if(map!=null && !map.isEmpty()){
			assertNotNull(map);
			assertFalse(map.isEmpty());
			Set<String> keySet=map.keySet();
			for(String key:keySet){
				System.out.println("applicable category name :"+key+" RegistryCategoryMapVO "+map.get(key).getCatId());
			}
		}else{
			assertNull(map);
		}
		}catch(BBBSystemException bbbsys){
			System.out.println("Systme Exception"+bbbsys);
			assertNull(map);
		}
	}

	public void testSortSkuByPrice() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		Map <String,Double> skuIdPriceMap =new HashMap<String,Double>();
		skuIdPriceMap.put("sku40048", 20.0);
		skuIdPriceMap.put("sku40069", 120.0);
		skuIdPriceMap.put("sku40041", 250.0);
		skuIdPriceMap.put("sku30005", 67.0);
		skuIdPriceMap.put("sku30003", 89.0);
		String registryCode =(String) getObject("registryCode");
		String siteId= (String) getObject("siteId");
		Map<String,List<SKUDetailVO>>  map=catalogTools.sortSkuByPrice(skuIdPriceMap, registryCode, siteId,null);
		System.out.println("map "+map);
		assertNotNull(map);
		assertFalse(map.isEmpty());
		if(map!=null && !map.isEmpty()){
			Set<String> keySet=map.keySet();
			for(String key:keySet){
				System.out.println("applicable price name :"+key);
				List<SKUDetailVO> list=map.get(key);
				for(int i=0;i<list.size();i++){
					System.out.println(i+"th skuId  ****"+list.get(i).getSkuId());
				}
			}
		}
	}
	public void testGetPriceRanges() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");


		String registryCode =(String) getObject("registryCode");
		String siteId= (String) getObject("siteId");
		 List<String>  priceRangeList=catalogTools.getPriceRanges(registryCode,null);
		System.out.println("priceRangeList "+priceRangeList);
		assertNotNull(priceRangeList);
		assertFalse(priceRangeList.isEmpty());
		if(priceRangeList!=null && !priceRangeList.isEmpty()){

			for(String price:priceRangeList){
				System.out.println("price range  :"+price);
				
			}
		}
	}
	public void testSortSkubyRegistry() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");

		Map <String,Double> skuIdPriceMap =new HashMap<String,Double>();
		skuIdPriceMap.put("sku40048", 20.0);
		skuIdPriceMap.put("sku40069", 120.0);
		skuIdPriceMap.put("sku40041", 250.0);
		skuIdPriceMap.put("sku30005", 67.0);
		skuIdPriceMap.put("sku30003", 89.0);
		String registryCode =(String) getObject("registryCode");
		String siteId= (String) getObject("siteId");
		Map<String,List<SKUDetailVO>>  map=catalogTools.sortSkubyRegistry(null,skuIdPriceMap, registryCode, siteId,null,null);
		System.out.println("map "+map);
		assertNotNull(map);
		assertFalse(map.isEmpty());
		if(map!=null && !map.isEmpty()){
			Set<String> keySet=map.keySet();
			for(String key:keySet){
				System.out.println("applicable price name :"+key);
				List<SKUDetailVO> list=map.get(key);
				for(int i=0;i<list.size();i++){
					System.out.println(i+"th skuId  ****"+list.get(i).getSkuId());
				}
			}
		}
	}
	public void testThirdPartyTagStatus() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		String siteId = (String) getObject("siteId");
		String tagName = (String) getObject("tagName");
		String tagStatus = catalogTools.getThirdPartyTagStatus(siteId, catalogTools, tagName);
		System.out.println("Third Party tagStatus "+ tagStatus);
		assertNotNull(tagStatus);
		if(tagStatus != null && tagStatus.equalsIgnoreCase("true")){
			assertEquals(true, Boolean.parseBoolean(tagStatus));
		}else if(tagStatus != null && tagStatus.equalsIgnoreCase("false")){
			assertEquals(false, Boolean.parseBoolean(tagStatus));
		}
	}

	// ---------------------------------
	public void testIsSkuActive() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String skuId = (String) getObject("skuId");
		final RepositoryItem skuRepositoryItem = catalogTools
				.getCatalogRepository().getItem(skuId,
						BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
		Boolean skuStatus = catalogTools.isSkuActive(skuRepositoryItem);
		assertNotNull(skuStatus);
		if (skuStatus != null && skuStatus.equals("true")) {
			assertEquals(true, (boolean) skuStatus);
		} else if (skuStatus != null && skuStatus.equals("false")) {
			assertEquals(false, (boolean) skuStatus);
		}
	}

	public void testGetSkuPropFlagStatus() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String skuId = (String) getObject("skuId");
		Map<String, String> resultList = catalogTools.getSkuPropFlagStatus(skuId);
		assertNotNull(resultList);

	}

	public void testIsProductActive() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String productId = (String) getObject("productId");
		String siteId = (String) getObject("siteId");
		final RepositoryItem productRepositoryItem = catalogTools
				.getCatalogRepository().getItem(productId,
						BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);

		Boolean status = catalogTools.isProductActive(productRepositoryItem,
				siteId);
		assertNotNull(status);
		if (status != null && status.equals("true")) {
			assertEquals(true, (boolean) status);
		} else if (status != null && status.equals("false")) {
			assertEquals(false, (boolean) status);
		}
	}
	
	public void testIsEverlivingProduct() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String productId = (String) getObject("productId");
		String siteId = (String) getObject("siteId");
		final RepositoryItem productRepositoryItem = catalogTools
				.getCatalogRepository().getItem(productId,
						BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);

		Boolean status = catalogTools.isEverlivingProduct(productId,
				siteId);
		assertNotNull(status);
		if (status != null && status.equals("true")) {
			assertEquals(true, (boolean) status);
		} else if (status != null && status.equals("false")) {
			assertEquals(false, (boolean) status);
		}
	}

	public void testIsSkuActiveForStore() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String skuId = (String) getObject("skuId");
		final RepositoryItem skuRepositoryItem = catalogTools
				.getCatalogRepository().getItem(skuId,
						BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);

		Boolean status = catalogTools.isSkuActiveForStore(skuRepositoryItem);
		assertNotNull(status);
		if (status != null && status.equals("true")) {
			assertEquals(true, (boolean) status);
		} else if (status != null && status.equals("false")) {
			assertEquals(false, (boolean) status);
		}
	}
	
	public void testGetBccManagedBrand() throws Exception {
		
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		BrandVO brandVO = catalogTools.getBccManagedBrand("B. Smith","");
		assertNotNull(brandVO.getSortOptionVO().getDefaultSortingOption());
		assertNotNull(brandVO.getSortOptionVO().getSortingOptions());
	}
	
	public void testGetExpectedDeliveryDateForLTLItem()throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String shippingMethodId=(String) getObject("shippingMethodId");
		String siteId=(String) getObject("siteId");
		String skuId=(String) getObject("skuId");
		try{
			String expect=catalogTools.getExpectedDeliveryDateForLTLItem(shippingMethodId, siteId, skuId,null,true);
			System.out.println("expect  "+expect);
			assertNotNull("Expected delivery date string is null",expect);
			StringTokenizer st = new StringTokenizer(expect, "-");
			String expectedDateFormat =  "MM/dd";
			if(st.hasMoreElements()){
				 SimpleDateFormat formatter = new SimpleDateFormat(expectedDateFormat);
				 String dateStr =(String) st.nextElement();
				 assertNotNull("Expected delivery date is having null values",dateStr);
				 Date d = formatter.parse(dateStr);
				 assertNotNull("date format is incorrect", d);
			}
			
		}
		catch(Exception e){
			System.out.println("in exception" + e.getMessage());
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_AVAILABLE_IN_REPOSITORY+":"+BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_AVAILABLE_IN_REPOSITORY);
		}
	}
	
	
	public void testGetAssemblyCharge()throws Exception{
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId=(String) getObject("siteId");
		String skuId=(String) getObject("skuId");
		
		double assemblyCharge=catalogTools.getAssemblyCharge(siteId, skuId);
		System.out.println("Calculated assembly charge "+assemblyCharge);
		assertEquals(150.00D, assemblyCharge);
		
	}
	/**
	 * To test shipping methods for LTL sku
	 * 
	 * @throws Exception
	 */
	public void testShippingMethodsForLTLSku() throws Exception {
		BBBCatalogToolsImpl catalogTools = (BBBCatalogToolsImpl) getObject("BBBCatalogTools");
		catalogTools.setLoggingDebug(true);
		String siteId= (String) getObject("siteId");
		String skuId=(String) getObject("skuId");
		String skuIdHavingOneShippingMethods=(String)getObject("skuIdHavingOneShippingMethods");
		//getShippingMethodsForSku
		try{
			List<ShipMethodVO> shipMethodVOList =null; //=catalogTools.getShippingMethodsForSku(siteId, skuId);
			assertNotNull(shipMethodVOList);
			assertFalse(shipMethodVOList.isEmpty());
			List<String> shipMethods = new ArrayList<String>();
			for(int i=0;i<shipMethodVOList.size();i++){
				shipMethods.add(shipMethodVOList.get(i).getShipMethodId());
			}
			
			assertTrue(shipMethods.contains("LC"));
			assertTrue(shipMethods.contains("LR"));
			assertTrue(shipMethods.contains("LT"));
			assertTrue(shipMethods.contains("LW"));
			
			//Test when sku itself have single shipping method
			shipMethodVOList= null; //=catalogTools.getShippingMethodsForSku(siteId, skuIdHavingOneShippingMethods);
			shipMethods.clear();
			for(int i=0;i<shipMethodVOList.size();i++){
				shipMethods.add(shipMethodVOList.get(i).getShipMethodId());
			}
			
			assertTrue(shipMethods.contains("LC"));
			
		}
		catch(Exception e){
			System.out.println("in exception"+e.getMessage());
			assertEquals(e.getMessage(),BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY+":"+BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
		}
	}

}
