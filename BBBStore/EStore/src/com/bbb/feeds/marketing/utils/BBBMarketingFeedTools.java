package com.bbb.feeds.marketing.utils;

import static com.bbb.constants.BBBCoreConstants.COLLECTION_CHILD_RELN_CACHE_NAME;
import static com.bbb.constants.BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY;
import static com.bbb.constants.BBBCoreConstants.YES_CHAR;
import static com.bbb.constants.BBBCoreConstants.NO_CHAR;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;
import atg.multisite.Site;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextImpl;
import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.feeds.marketing.vo.MarketingFeedVO;
import com.bbb.feeds.utils.BBBFeedTools;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.repositorywrapper.RepositoryItemWrapper;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;


public class BBBMarketingFeedTools extends BBBFeedTools {

	private static final String IS_NOT_CONFIGURED = " is not configured";
	private static final String SCENE7URL = "scene7URL";
	private static final String GREATER_THAN_SYMBOL = " > ";
	private static final String PRODUCTION_URL = "productionURL";
	private static final String INVENTORY_EXCEPTION_OCCURED_FOR_SKU = "Inventory Exception occured for sku ";
	private static final String YES = "Yes";
	private static final String NO = "No";
	private static final String SCENE7_URL = "scene7_url";
	private static final String HTTPS_WITH_SLASH = "https://";
	private static final String HTTPS = "https:";
	protected Map<String,String> units = new HashMap<String,String>();
	List<String> restrictedSkuList = null;
	private Map<String, String> customizationOfferedSiteMap;
	public Map<String, String> getCustomizationOfferedSiteMap() {
		return customizationOfferedSiteMap;
	}

	public void setCustomizationOfferedSiteMap(
			Map<String, String> customizationOfferedSiteMap) {
		this.customizationOfferedSiteMap = customizationOfferedSiteMap;
	}
	public List<String> getRestrictedSkuList() {
		return restrictedSkuList;
	}

	public void setRestrictedSkuList(List<String> restrictedSkuList) {
		this.restrictedSkuList = restrictedSkuList;
	}

	

	/**
	 * 
	 */
	public Repository storeRepository = null;
	private BBBObjectCache objectCache;

	/**
	 * 
	 */
	public Map<String, String> shippingMethodMap = null;
	
	/**
	 * This method will generate Commision Junction feed VO's
	 * @param isFullDataFeed
	 * @param lastModDate 
	 * @param lastModifiedDate
	 * @param feedHeaders
	 * @param catalogId
	 * @param siteName 
	 * @param siteId
	 * @return list of MarketingFeedVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<MarketingFeedVO> getCJMarketingFeedVOList(
			final boolean isFullDataFeed,final Timestamp lastModDate,
			final List<String> feedHeaders,final String catalogId,final String siteName) throws BBBSystemException, BBBBusinessException {
		
		MLOGGING.logInfo("BBBMarketingFeedTools [getCJMarketingFeedVOList] start");
		final List<MarketingFeedVO> feedVOList = new ArrayList<MarketingFeedVO>();
		final RepositoryItem[] productItems = getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
													BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		if (productItems == null ||
				productItems.length == 0) {

			MLOGGING.logInfo("No data available in repository for Commision Junction Marketing Feed");
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1008,BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
		}
		final List<RepositoryItem> productsList = new ArrayList<RepositoryItem>();
		productsList.addAll(Arrays.asList(productItems));
		RepositoryItemWrapper productWrapper = null;
		RepositoryItemWrapper skuWrapper = null;			
		MarketingFeedVO feedVO = null;
		RepositoryItem siteRepItem = null;
		
		try {
			siteRepItem = getSiteManager().getSite(siteName);
			if(siteRepItem == null) {
				throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1009,siteName+IS_NOT_CONFIGURED);
			}
		} catch (RepositoryException e) {
			MLOGGING.logError("Repository Exception:" ,e);
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1010,siteName+IS_NOT_CONFIGURED);
		}
		final String seanURL = HTTPS+getConfigTemplateManager().getThirdPartyURL(SCENE7_URL, BBBCoreConstants.THIRD_PARTY_URL)+"/";
		final String siteURL = HTTPS_WITH_SLASH+(String)(siteRepItem.getPropertyValue(PRODUCTION_URL));

		final Object currObject = siteRepItem.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY);
		final Object giftWrapperProduct = siteRepItem.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRODUCT_SITE_PROPERTY_NAME);
		if(giftWrapperProduct!= null) {
			productsList.add((RepositoryItem)giftWrapperProduct);
		}
		
		//String[] restrictedSku = this.getCJMarketingFeedRestrictedSKU(this.getRestrictedSkuList());

		for (RepositoryItem productItem: productsList) {
			productWrapper = new RepositoryItemWrapper(productItem);				

			if(!isProductWebOffered(productItem, siteName)) {
				addDeactiveProducts(productItem.getRepositoryId());
				continue;
			}

			final List<RepositoryItem> skus = productWrapper.getList(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
			if(skus== null) {
				getProductsWithOutSkus().append(productItem.getRepositoryId()+"\n");
				continue;
			}
			
			feedVO = new MarketingFeedVO();
			// Setting Product Attributes
			final String productName = getPlainText(productWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
			
			//NAME
			feedVO.addFeedDataMap(feedHeaders.get(0), productName);
			//KEYWORDS,
			feedVO.addFeedDataMap(feedHeaders.get(1), productName);
			//DESCRIPTION  ---productDescription
			feedVO.addFeedDataMap(feedHeaders.get(2), getPlainText(productWrapper.getString(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)));
			
			//BUYURL ---- productPageURL
			feedVO.addFeedDataMap(feedHeaders.get(4), siteURL+ getProductPageURL(productName, productItem.getRepositoryId()));

			//IMAGEURL ---productImageURL
			feedVO.addFeedDataMap(feedHeaders.get(6), seanURL+ productWrapper.getString(SCENE7URL));
			
			//CURRENCY  --- currency
			feedVO.addFeedDataMap(feedHeaders.get(9), currObject!=null?(String)currObject:"USD");

			//ADVERTISERCATEGORY --- Cataegory Name 
			feedVO.addFeedDataMap(feedHeaders.get(12), getProductNavigationPath(productItem, catalogId)+GREATER_THAN_SYMBOL+productName);
			
			//MANUFACTURER  ---  brandName
			feedVO.addFeedDataMap(feedHeaders.get(14), getProductBrandName(productWrapper));

			//MANUFACTURERID
			//OFFLINE   
			feedVO.addFeedDataMap(feedHeaders.get(16), NO);
			
			//ONLINE
			feedVO.addFeedDataMap(feedHeaders.get(17), YES);
			
			for(RepositoryItem sku: skus) {
				skuWrapper = new RepositoryItemWrapper(sku);
				
				
				if((!isSkuWebOffered(sku, siteName)) || (this.getRestrictedSkuList().contains(sku.getRepositoryId()))) {
					addProductsWithDisabledSkus(productItem.getRepositoryId(), sku.getRepositoryId());
					continue;
				}

				final MarketingFeedVO feedVOBySku = (MarketingFeedVO)feedVO.deepClone();
				//Setting Sku Attributes

				String skuDescription = skuWrapper.getString(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME);
				if(skuDescription !=null && skuDescription.length()>0)
				{
					feedVOBySku.addFeedDataMap(feedHeaders.get(2), getPlainText(skuDescription));
				}
				
				//SKU---skuId
				feedVOBySku.addFeedDataMap(feedHeaders.get(3), sku.getRepositoryId());
				
				//AVAILABLE  --- isInStock
				int available = 1;
				try {
					available = getInventoryManager().getProductAvailability(siteName, sku.getRepositoryId(), BBBInventoryManager.PRODUCT_DISPLAY,0);
				}catch(Exception e) {
					MLOGGING.logError(INVENTORY_EXCEPTION_OCCURED_FOR_SKU+sku.getRepositoryId()+ "\n"+e.getMessage());
				}
				feedVOBySku.addFeedDataMap(feedHeaders.get(5), available==1 ? NO: YES);

				
				//PRICE----price
				feedVOBySku.addFeedDataMap(feedHeaders.get(7), String.valueOf(getSalePriceBySite(siteRepItem,productItem.getRepositoryId(), sku.getRepositoryId())));
				
				//RETAILPRICE   -----> price
				feedVOBySku.addFeedDataMap(feedHeaders.get(8), String.valueOf(getListPriceBySite(siteRepItem, productItem.getRepositoryId(), sku.getRepositoryId())));
				
				//upc and partNumber
				feedVOBySku.addFeedDataMap(feedHeaders.get(10), skuWrapper.getString(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME));

				//PROMOTIONALTEXT

				//GIFT  ----  giftWrapEligible
				final boolean isGiftWrapEligible = skuWrapper.getBoolean(BBBCatalogConstants.GIFT_WRAP_ELIGIBLE_SKU_PROPERTY_NAME);
				feedVOBySku.addFeedDataMap(feedHeaders.get(15), Boolean.toString(isGiftWrapEligible)!=null && isGiftWrapEligible?YES:NO);
				
				//INSTOCK  --- isInStock
				feedVOBySku.addFeedDataMap(feedHeaders.get(18), available==1 ? NO: YES);
				
				//MERCHANDISETYPE ---> jdaDeptDescription
				final RepositoryItem jdaDept = skuWrapper
						.getRepositoryItem(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME);
				feedVOBySku.addFeedDataMap(feedHeaders.get(19),
								jdaDept == null ? 
										"": (String) jdaDept.getPropertyValue(BBBCatalogConstants.DESCRIPTION_JDA_PROPERTY_NAME));
				if(feedVOBySku.get(feedHeaders.get(2))!=null && feedVOBySku.get(feedHeaders.get(2)).length()!=0)
				{
					feedVOList.add(feedVOBySku);
				}
			}
		}
		MLOGGING.logInfo("BBBMarketingFeedTools [getCJMarketingFeedVOList] end");
		return feedVOList;
	}

		 	 

	/**
	 * This method will generate Google Marketing feed VO's
	 * @param isFullDataFeed
	 * @param lastModDate
	 * @param feedHeaders
	 * @param catalogId
	 * @param siteName
	 * @return MarketingFeedVO list
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public List<MarketingFeedVO> getGoogleMarketingFeedVOList(
			final boolean isFullDataFeed,final Timestamp lastModDate,
			final List<String> feedHeaders,final String catalogId,final String siteName)  throws BBBSystemException, BBBBusinessException {
		
		MLOGGING.logInfo("BBBMarketingFeedTools [getGoogleMarketingFeedVOList] start");
		final List<MarketingFeedVO> feedVOList = new ArrayList<MarketingFeedVO>();
		final RepositoryItem[] productItems = getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
													BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		if (productItems == null ||
				productItems.length == 0) {
			
			MLOGGING.logInfo("No data available in repository for Google Marketing Feed");
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1008,BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
		}
		final List<RepositoryItem> productsList = new ArrayList<RepositoryItem>();
		productsList.addAll(Arrays.asList(productItems));

		RepositoryItemWrapper productWrapper = null;
		RepositoryItemWrapper skuWrapper = null;			
		MarketingFeedVO feedVO = null;
		RepositoryItem siteRepItem = null;
		String catItemPropSkuColor;   //BBBSL-6884
		String catItemPropSkuSize;    
		String brand;                
		String name;				  
		try {
			siteRepItem = getSiteManager().getSite(siteName);
			if(siteRepItem == null) {
				throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1009,siteName+IS_NOT_CONFIGURED);
			}
		} catch (RepositoryException e) {
			MLOGGING.logError("Repository Exception:" ,e);
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1010,siteName+IS_NOT_CONFIGURED);
		}
		final String seanURL = HTTPS+getConfigTemplateManager().getThirdPartyURL(SCENE7_URL, BBBCoreConstants.THIRD_PARTY_URL)+"/";
		final String siteURL = HTTPS_WITH_SLASH+(String)(siteRepItem.getPropertyValue(PRODUCTION_URL));
		//3. price, 6.  upcCode
		for (RepositoryItem productItem: productsList) {
			productWrapper = new RepositoryItemWrapper(productItem);				
			if(!isProductWebOffered(productItem, siteName)) {
				addDeactiveProducts(productItem.getRepositoryId());
				continue;
			}
			
			final List<RepositoryItem> skus = productWrapper.getList(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
			if(skus== null) {
				getProductsWithOutSkus().append(productItem.getRepositoryId()+"\n");
				continue;
			}
			
			feedVO = new MarketingFeedVO();
			// Setting Product Attributes
			final String productName = getPlainText(productWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));

			//staticNewText
			feedVO.addFeedDataMap(feedHeaders.get(0), "NEW");
			//name
			name = getPlainText(productWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
			
			//productPageURL
			feedVO.addFeedDataMap(feedHeaders.get(1), siteURL+ getProductPageURL(productName, productItem.getRepositoryId()));

			//productLongDescription
			feedVO.addFeedDataMap(feedHeaders.get(4), getPlainText(productWrapper.getString(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)));
			
			//brandName
			brand=getProductBrandName(productWrapper);
			feedVO.addFeedDataMap(feedHeaders.get(7), brand );
			
			//productImageURL
			feedVO.addFeedDataMap(feedHeaders.get(8), seanURL+ productWrapper.getString(SCENE7URL));
			
			//skuId - Product Id
			feedVO.addFeedDataMap(feedHeaders.get(10), productItem.getRepositoryId());
			
			

			for(RepositoryItem sku: skus) {
				skuWrapper = new RepositoryItemWrapper(sku);
				if(!isSkuWebOffered(sku, siteName)) {
					addProductsWithDisabledSkus(productItem.getRepositoryId(), sku.getRepositoryId());
					continue;
				}

				final MarketingFeedVO feedVOBySku = (MarketingFeedVO)feedVO.deepClone();
				//Setting Sku Attributes

				//price
				feedVOBySku.addFeedDataMap(feedHeaders.get(3), String.valueOf(getSalePriceBySite(siteRepItem,productItem.getRepositoryId(), sku.getRepositoryId())));
				
				//upc and partNumber
				feedVOBySku.addFeedDataMap(feedHeaders.get(6), skuWrapper.getString(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME));
				
				// jdaDeptDescription
				final RepositoryItem jdaDept = skuWrapper
						.getRepositoryItem(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME);
				feedVOBySku.addFeedDataMap(feedHeaders.get(9),jdaDept == null ? "": 
												(String) jdaDept.getPropertyValue(BBBCatalogConstants.DESCRIPTION_JDA_PROPERTY_NAME));

				//skuColor
				catItemPropSkuColor = skuWrapper.getString(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME);
				feedVOBySku.addFeedDataMap(feedHeaders.get(11), catItemPropSkuColor!=null?catItemPropSkuColor:"");

				//skuSize
				catItemPropSkuSize = skuWrapper.getString(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME);					
				feedVOBySku.addFeedDataMap(feedHeaders.get(12), catItemPropSkuSize!=null?catItemPropSkuSize:"");
				
				//name    & BBBSL-6884
				feedVOBySku.addFeedDataMap(feedHeaders.get(2), getTitleWithBrandColorSize(brand, name, catItemPropSkuColor, catItemPropSkuSize));
				
				feedVOList.add(feedVOBySku);	
			}
		}
		MLOGGING.logInfo("BBBMarketingFeedTools [getGoogleMarketingFeedVOList] end");
		return feedVOList;
	}

	/**
	 * This method will generate Google Local Shopping Marketing feed VO's
	 * @param isFullDataFeed
	 * @param lastModDate
	 * @param feedHeaders
	 * @param catalogId
	 * @param siteName
	 * @return MarketingFeedVO list
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public List<MarketingFeedVO> getGoogleShoppingMarketingFeedVOList(
			final boolean isFullDataFeed,final Timestamp lastModDate,
			final List<String> feedHeaders,final String catalogId,final String siteName)  throws BBBSystemException, BBBBusinessException {
		MLOGGING.logInfo("BBBMarketingFeedTools [getGoogleShoppingMarketingFeedVOList] start");
		final List<MarketingFeedVO> feedVOList = new ArrayList<MarketingFeedVO>();
		final RepositoryItem[] productItems = getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
													BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		if (productItems == null ||
				productItems.length == 0) {
			
			MLOGGING.logInfo("No data available in repository for Google Shopping Marketing Feed");
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1008,BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
		}
		final List<RepositoryItem> productsList = new ArrayList<RepositoryItem>();
		productsList.addAll(Arrays.asList(productItems));

		RepositoryItemWrapper productWrapper = null;
		RepositoryItemWrapper skuWrapper = null;			
		MarketingFeedVO feedVO = null;
		RepositoryItem siteRepItem = null;
		String colorProperty;   //BBBSL-6884
		String sizeProperty;    
		String brandName;      
		try {
			siteRepItem = getSiteManager().getSite(siteName);
			if(siteRepItem == null) {
				throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1009,siteName+IS_NOT_CONFIGURED);
			}
		} catch (RepositoryException e) {
			MLOGGING.logError("Repository Exception:" ,e);
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1010,siteName+IS_NOT_CONFIGURED);
		}
		final String seanURL = HTTPS+getConfigTemplateManager().getThirdPartyURL(SCENE7_URL, BBBCoreConstants.THIRD_PARTY_URL)+"/";
		final String siteURL = HTTPS_WITH_SLASH+(String)(siteRepItem.getPropertyValue(PRODUCTION_URL));

		final Object giftWrapperProduct = siteRepItem.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRODUCT_SITE_PROPERTY_NAME);
		if(giftWrapperProduct!= null) {
			productsList.add((RepositoryItem)giftWrapperProduct);
		}

		for (RepositoryItem productItem: productsList) {
			productWrapper = new RepositoryItemWrapper(productItem);				
			if(!isProductWebOffered(productItem, siteName)) {
				addDeactiveProducts(productItem.getRepositoryId());
				continue;
			}
			
			final List<RepositoryItem> skus = productWrapper.getList(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
			if(skus== null) {
				getProductsWithOutSkus().append(productItem.getRepositoryId()+"\n");
				continue;
			}
			
			//0skuId,productDescription,productLongDescription,productPageURL,productImageURL,staticNewText,6price,7upcCode,
			//brandName,partNumberStaticText,productNavigationPath
			
			
			feedVO = new MarketingFeedVO();
			
			// Setting Product Attributes
			final String productName = getPlainText(productWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
			
			//productLongDescription
			feedVO.addFeedDataMap(feedHeaders.get(2), getPlainText(productWrapper.getString(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME)));
			
			//staticNewText
			feedVO.addFeedDataMap(feedHeaders.get(5), "NEW");
			
			//brandName
			brandName = getProductBrandName(productWrapper);
			feedVO.addFeedDataMap(feedHeaders.get(8), brandName);	
			
			//Setting Category Attributes
			feedVO.addFeedDataMap(feedHeaders.get(9), getProductNavigationPath(productItem, catalogId)+GREATER_THAN_SYMBOL+productName);
			
			//Setting Product Id
			feedVO.addFeedDataMap(feedHeaders.get(10), productItem.getRepositoryId());
			
			
			
			for(RepositoryItem sku: skus) {
				skuWrapper = new RepositoryItemWrapper(sku);
				
				if(!isSkuWebOffered(sku, siteName)) {
					continue;
				}

				final MarketingFeedVO feedVOBySku = (MarketingFeedVO)feedVO.deepClone();
				//Setting Sku Attributes
				
				//skuId
				feedVOBySku.addFeedDataMap(feedHeaders.get(0), sku.getRepositoryId());
				
				//productPageURL with sku id
				feedVOBySku.addFeedDataMap(feedHeaders.get(3), siteURL+ getProductPageURL(productName, productItem.getRepositoryId()) + BBBCatalogConstants.SKU_ID_CONSTANT + sku.getRepositoryId());
				
				//SkuImageURL
				feedVOBySku.addFeedDataMap(feedHeaders.get(4), seanURL+ skuWrapper.getString(SCENE7URL));
				
				//price
				feedVOBySku.addFeedDataMap(feedHeaders.get(6), String.valueOf(getSalePriceBySite(siteRepItem,productItem.getRepositoryId(), sku.getRepositoryId())));
				
				//upc and partNumber
				
				//For length correction we will prefix 0s with the code, to make it equal to 13.
				String lengthCorrectedUpcCode = checkCodeSizeAndRectify(13, skuWrapper.getString(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME));
				
				int originalLength = 0;
				
				if(!BBBUtility.isEmpty(lengthCorrectedUpcCode)){
					 originalLength = lengthCorrectedUpcCode.length();
				}
			
				
				if (originalLength == 13) {
					
					MLOGGING.logInfo("code length is equal to 13, checksum will be added to the code,  lengthCorrectedUpcCode:: "
							+ lengthCorrectedUpcCode);
					
					String checkSumAddedCode = addCheckSum(lengthCorrectedUpcCode);
					MLOGGING.logInfo("checkSumAddedCode :: "
							+ checkSumAddedCode);
					
					feedVOBySku.addFeedDataMap(feedHeaders.get(7), checkSumAddedCode);
										
					
				} else {

					MLOGGING.logError("code length is greater or less than 13, this is an error condition , simply setting it in the Vo, without adding checksum,  lengthCorrectedUpcCode:: "
							+ lengthCorrectedUpcCode);

					feedVOBySku.addFeedDataMap(feedHeaders.get(7),
							lengthCorrectedUpcCode);

				}
				
				
				//Setting color Attributes
				colorProperty = skuWrapper.getString(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME);
				feedVOBySku.addFeedDataMap(feedHeaders.get(11), colorProperty);
				
				//Setting size Attributes
				sizeProperty = skuWrapper.getString(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME);
				Map<String, String> units= new HashMap<String,String>();
				units = this.getUnits();
				
				if(!BBBUtility.isEmpty(sizeProperty) && units != null){
					sizeProperty = sizeProperty.replace("&#39;", BBBUtility.isEmpty(units.get("feet")) ? "ft": (String)units.get("feet"));
					sizeProperty = sizeProperty.replace("&quot;", BBBUtility.isEmpty(units.get("inch")) ? "inch": (String)units.get("inch"));
				}
				feedVOBySku.addFeedDataMap(feedHeaders.get(12),sizeProperty );
				
				
				
				//productDescription  & BBBSL-6884
				feedVOBySku.addFeedDataMap(feedHeaders.get(1), getTitleWithBrandColorSize(brandName, productName, colorProperty, sizeProperty));
				
				feedVOList.add(feedVOBySku);
			}
		}
		logDebug("BBBMarketingFeedTools [getGoogleShoppingMarketingFeedVOList] end");
		return feedVOList;
	}

	/**
	 * This method will generate Google Local Store Marketing feed VO's
	 * @param isFullDataFeed
	 * @param lastModDate
	 * @param feedHeaders
	 * @param catalogId
	 * @param siteName
	 * @return MarketingFeedVO list
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public List<MarketingFeedVO> getGoogleLocalStoresMarketingFeedVOList(
			final boolean isFullDataFeed,final Timestamp lastModDate,
			final List<String> feedHeaders,final String catalogId,final String siteName)  throws BBBSystemException, BBBBusinessException {

		MLOGGING.logInfo("BBBMarketingFeedTools [getGoogleLocalStoresMarketingFeedVOList] start");
		final List<MarketingFeedVO> feedVOList = new ArrayList<MarketingFeedVO>();
		final RepositoryItem[] storeRepositoryItems = getStoreItemsForFeedGeneration(isFullDataFeed,lastModDate);
		if (storeRepositoryItems == null ||
				storeRepositoryItems.length == 0) {

			MLOGGING.logInfo("No data available in repository for Google Local Stores Marketing Feed");
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1008,BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
		}
		RepositoryItemWrapper storeRepItemWrapper = null;
		MarketingFeedVO feedVO = null;
		RepositoryItem siteRepItem = null;
		String siteURL = "";		
		try {
			siteRepItem = getSiteManager().getSite(siteName);
			if(siteRepItem == null) {
				throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1009,siteName+IS_NOT_CONFIGURED);
			}
			siteURL = HTTPS_WITH_SLASH+(String)(siteRepItem.getPropertyValue(PRODUCTION_URL));
		} catch (RepositoryException e) {
			logError (siteName+IS_NOT_CONFIGURED);
		}
		
		//storeId,name,address,city,state,postalCode,country,phoneNumber,longitude,latitude,hours,siteProductionURL,staticText
		for (RepositoryItem storeItem: storeRepositoryItems) {

			storeRepItemWrapper = new RepositoryItemWrapper(storeItem);					
			feedVO = new MarketingFeedVO();
			// Setting Store Attributes
			//storeId
			feedVO.addFeedDataMap(feedHeaders.get(0), storeItem.getRepositoryId());
			//name
			feedVO.addFeedDataMap(feedHeaders.get(1), storeRepItemWrapper.getString(BBBCatalogConstants.STORE_NAME_STORE_PROPERTY_NAME));
			//address
			feedVO.addFeedDataMap(feedHeaders.get(2), storeRepItemWrapper.getString(BBBCatalogConstants.ADDRESS_STORE_PROPERTY_NAME));
			//city
			feedVO.addFeedDataMap(feedHeaders.get(3), storeRepItemWrapper.getString(BBBCatalogConstants.CITY_STORE_PROPERTY_NAME));
			//state
			feedVO.addFeedDataMap(feedHeaders.get(4), storeRepItemWrapper.getString(BBBCatalogConstants.STATE_ITEM_DESCRIPTOR));
			//postalCode
			feedVO.addFeedDataMap(feedHeaders.get(5), storeRepItemWrapper.getString(BBBCatalogConstants.POSTAL_CODE_STORE_PROPERTY_NAME));
			//country
			feedVO.addFeedDataMap(feedHeaders.get(6), storeRepItemWrapper.getString(BBBCatalogConstants.COUNTRYCODE_STORE_PROPERTY_NAME));
			//phoneNumber
			feedVO.addFeedDataMap(feedHeaders.get(7), storeRepItemWrapper.getString(BBBCatalogConstants.PHONE_STORE_PROPERTY_NAME));
			//longitude
			feedVO.addFeedDataMap(feedHeaders.get(8), storeRepItemWrapper.getString(BBBCatalogConstants.LONGITUDE_STORE_PROPERTY_NAME));
			//latitude
			feedVO.addFeedDataMap(feedHeaders.get(9), storeRepItemWrapper.getString(BBBCatalogConstants.LATITUDE_STORE_PROPERTY_NAME));
			//hours
			feedVO.addFeedDataMap(feedHeaders.get(10), storeRepItemWrapper.getString(BBBCatalogConstants.HOURS_STORE_PROPERTY_NAME));
			//siteProductionURL
			feedVO.addFeedDataMap(feedHeaders.get(11), siteURL);
			//staticText
			feedVO.addFeedDataMap(feedHeaders.get(12), "230");
			feedVOList.add(feedVO);
		}
		MLOGGING.logInfo("BBBMarketingFeedTools [getGoogleShoppingMarketingFeedVOList] end");	
		return feedVOList;
	}

	/**
	 * @param isFullDataFeed
	 * @param lastModDate
	 * @return repository item
	 * @throws BBBSystemException
	 */
	public RepositoryItem[] getStoreItemsForFeedGeneration(final boolean isFullDataFeed,final Timestamp lastModDate) throws BBBSystemException {
		
		MLOGGING.logInfo("BBBMarketingFeedTools [getStoreItemsForFeedGeneration] Start.");
		RepositoryItem[] storeItems = null;
		final String rql = "all";
		try {
			final RepositoryView storeRepView = this.getStoreRepository().getView(BBBCatalogConstants.STORE_ITEM_DESCRIPTOR);
			if (isFullDataFeed || lastModDate == null) {
				MLOGGING.logInfo("Fetch data for full feed :isFullDataFeed: " + isFullDataFeed + " lastModifiedDate:"+ lastModDate);
				final RqlStatement statement = RqlStatement.parseRqlStatement(rql);
				storeItems = statement.executeQuery(storeRepView, new Object[] {});						
			} else {
				MLOGGING.logInfo("Fetch data for incremental feed lastModifiedDate:" + lastModDate);
				//rql = "lastModifiedDate=?1";
				final RqlStatement statement = RqlStatement.parseRqlStatement(rql);
				storeItems = statement.executeQuery(storeRepView, new Object[] {});			
			}			
		} catch (RepositoryException e) {
			MLOGGING.logError("RepositoryException:",e);
			throw new BBBSystemException(BBBCoreErrorConstants.FEED_ERROR_1011,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
		}
		MLOGGING.logInfo("BBBMarketingFeedTools [getStoreItemsForFeedGeneration] End. ");		
		return storeItems;
	}

	/**
	 * This method will generate Tell Apart Marketing feed VO's
	 * @param isFullDataFeed
	 * @param lastModDate
	 * @param feedHeaders
	 * @param catalogId
	 * @param siteName
	 * @return MarketingFeedVO list
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public List<MarketingFeedVO> getTellApartMarketingFeedVOList(
			final boolean isFullDataFeed,final Timestamp lastModDate,
			final List<String> feedHeaders,final String catalogId,final String siteName)  throws BBBSystemException, BBBBusinessException {
		


		MLOGGING.logInfo("BBBMarketingFeedTools [getTellApartMarketingFeedVOList] start");
		final List<MarketingFeedVO> feedVOList = new ArrayList<MarketingFeedVO>();
		final RepositoryItem[] productItems = getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
													BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		if (productItems == null ||
				productItems.length == 0) {

			MLOGGING.logInfo("No data available in repository for Tell Apart Marketing Feed");
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1008,BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
		}
		final List<RepositoryItem> productsList = new ArrayList<RepositoryItem>();
		productsList.addAll(Arrays.asList(productItems));

		RepositoryItemWrapper productWrapper = null;
		RepositoryItemWrapper skuWrapper = null;			
		MarketingFeedVO feedVO = null;
		RepositoryItem siteRepItem = null;
		String skuClassDesc = "";
		String categoryName =null;
		
		try {
			siteRepItem = getSiteManager().getSite(siteName);
			if(siteRepItem == null) {
				throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1009,siteName+IS_NOT_CONFIGURED);
			}
		} catch (RepositoryException e) {
			MLOGGING.logError("RepositoryException:",e);
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1010,siteName+IS_NOT_CONFIGURED);
		}
		final String seanURL = HTTPS+getConfigTemplateManager().getThirdPartyURL(SCENE7_URL, BBBCoreConstants.THIRD_PARTY_URL)+"/";
		final String siteURL = HTTPS_WITH_SLASH+(String)(siteRepItem.getPropertyValue(PRODUCTION_URL));

		final Object giftWrapperProduct = siteRepItem.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRODUCT_SITE_PROPERTY_NAME);
		if(giftWrapperProduct!= null) {
			productsList.add((RepositoryItem)giftWrapperProduct);
		}
		// 0skuId,productDescription,productLongDescription,3price,4price,productPageURL,productImageURL,7jdaSubDeptDescription,8classDescription,
		//9isSpecialPurchase,10isClearance,11isBeyondValue,12isFreeShipping
		
		for (RepositoryItem productItem: productsList) {

			productWrapper = new RepositoryItemWrapper(productItem);				
				
			if(!isProductWebOffered(productItem, siteName)) {
				addDeactiveProducts(productItem.getRepositoryId());
				continue;
			}
			
			final List<RepositoryItem> skus = productWrapper.getList(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
			if(skus== null) {
				getProductsWithOutSkus().append(productItem.getRepositoryId()+"\n");
				continue;
			}
			
			feedVO = new MarketingFeedVO();
			// Setting Product Attributes
			final String productName = getPlainText(productWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
			//displayName  ---PRODUCT TITLE
			feedVO.addFeedDataMap(feedHeaders.get(1), getPlainText(productWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)));
			//productDescription
			feedVO.addFeedDataMap(feedHeaders.get(2), getPlainText(productWrapper.getString(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)));			
			//productPageURL
			feedVO.addFeedDataMap(feedHeaders.get(5), siteURL+ getProductPageURL(productName, productItem.getRepositoryId()));
			//productImageURL
			feedVO.addFeedDataMap(feedHeaders.get(6), seanURL+ productWrapper.getString(SCENE7URL));
			
			//Category Path
			//BBBSL-7017 changes start| Configuring category path for Collection child products
			 categoryName =getProductNavigationPath(productItem, catalogId);
			
			if (BBBUtility.isEmpty(categoryName) || null ==categoryName) {
				final RepositoryItem parentProductItem = getParentProductItem(productItem.getRepositoryId(), siteName);
				categoryName = getProductNavigationPath(parentProductItem, catalogId);
			}
			feedVO.addFeedDataMap(feedHeaders.get(7), categoryName);
			//BBBSL-7017 changes end
			
			for(RepositoryItem sku: skus) {
				skuWrapper = new RepositoryItemWrapper(sku);
				
				if(!isSkuWebOffered(sku, siteName)) {
					addProductsWithDisabledSkus(productItem.getRepositoryId(), sku.getRepositoryId());
					continue;
				}

				final MarketingFeedVO feedVOBySku = (MarketingFeedVO)feedVO.deepClone();
				//Setting Sku Attributes

				//skuId ---SKU
				feedVOBySku.addFeedDataMap(feedHeaders.get(0), sku.getRepositoryId());
				//retialPrice
				feedVOBySku.addFeedDataMap(feedHeaders.get(3), String.valueOf(getListPriceBySite(siteRepItem, productItem.getRepositoryId(), sku.getRepositoryId())));
				
				//Sale price
				feedVOBySku.addFeedDataMap(feedHeaders.get(4), String.valueOf(getSalePriceBySite(siteRepItem,productItem.getRepositoryId(), sku.getRepositoryId())));
				
				
				final RepositoryItem jdaSubDept = skuWrapper
						.getRepositoryItem(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME);
				/*
				feedVOBySku.addFeedDataMap(feedHeaders.get(7),
							jdaSubDept == null ? 
									"": (String) jdaSubDept.getPropertyValue(BBBCatalogConstants.DESCRIPTION_JDA_PROPERTY_NAME));
				*/
				
				//classDescription --FULL_CATEGORY
				final RepositoryItem jdaDept = skuWrapper.getRepositoryItem(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME);
				skuClassDesc = jdaDept == null ? "": (String) jdaDept.getPropertyValue(BBBCatalogConstants.DESCRIPTION_JDA_PROPERTY_NAME)+GREATER_THAN_SYMBOL;
				skuClassDesc = jdaSubDept == null ?	"": (String) jdaSubDept.getPropertyValue(BBBCatalogConstants.DESCRIPTION_JDA_PROPERTY_NAME)+GREATER_THAN_SYMBOL;
				
				final RepositoryItem[] classes = getCatalogTools().getSkuClass(sku);
				if(classes != null && classes.length>0) {
					skuClassDesc += classes[0].getPropertyValue(BBBCatalogConstants.DESCRIPTION_JDA_PROPERTY_NAME);
				}
				feedVOBySku.addFeedDataMap(feedHeaders.get(8), skuClassDesc);
				
				// isSpecialPurchase --ATTR_SPECIAL_PURCHASE
				feedVO.addFeedDataMap(feedHeaders.get(9),getCatalogTools().
															isSpecialPurchaseProduct(siteName,productItem) ? 
																	"Special Purchase" : "");
				// isClearance ----ATTR_CLEARANCE
				feedVO.addFeedDataMap(feedHeaders.get(10),getCatalogTools().
						isClearanceProduct(siteName,productItem) ? 
								"Clearance" : "");

				// isBeyondValue--ATTR_BEYOND_VALUE
				feedVO.addFeedDataMap(feedHeaders.get(11),getCatalogTools().
						isBeyondValueProduct(siteName,productItem) ? 
								"Beyond Value" : "");
				
				//isFreeShipping  --ATTR_FREE_SHIPPING
				feedVOBySku.addFeedDataMap(feedHeaders.get(12),
						getCatalogTools().isFreeShipping(siteName,
							sku.getRepositoryId(),
							getShippingMethodMap().get("standard")) ? 
							"Free Shipping" : "");

				feedVOList.add(feedVOBySku);	
			}
		}
		MLOGGING.logInfo("BBBMarketingFeedTools [getTellApartMarketingFeedVOList] end");
		return feedVOList;
	}

	/**
	 * This method will generate ICrossing Marketing feed VO's
	 * @param isFullDataFeed
	 * @param lastModDate
	 * @param feedHeaders
	 * @param catalogId
	 * @param siteName
	 * @return MarketingFeedVO list
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public List<MarketingFeedVO> getICrossingMarketingFeedVOList(
			final boolean isFullDataFeed,final Timestamp lastModDate,
			final List<String> feedHeaders,final String catalogId,final String siteName)  throws BBBSystemException, BBBBusinessException {

		MLOGGING.logInfo("BBBMarketingFeedTools [getICrossingMarketingFeedVOList] start");
		final List<MarketingFeedVO> feedVOList = new ArrayList<MarketingFeedVO>();
		final RepositoryItem[] productItems = getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
													BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		if (productItems == null ||
				productItems.length == 0) {

			MLOGGING.logInfo("No data available in repository for Commision Junction Marketing Feed");
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1008,BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
		}
		final List<RepositoryItem> productsList = new ArrayList<RepositoryItem>();
		productsList.addAll(Arrays.asList(productItems));

		RepositoryItemWrapper productWrapper = null;
		RepositoryItemWrapper skuWrapper = null;			
		MarketingFeedVO feedVO = null;
		RepositoryItem siteRepItem = null;
		String catItemProp = "";
		
		try {
			siteRepItem = getSiteManager().getSite(siteName);
			if(siteRepItem == null) {
				throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1009,siteName+IS_NOT_CONFIGURED);
			}
		} catch (RepositoryException e) {
			MLOGGING.logError("RepositoryException:", e);
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1010,siteName+IS_NOT_CONFIGURED);
		}
		final String seanURL = HTTPS+getConfigTemplateManager().getThirdPartyURL(SCENE7_URL, BBBCoreConstants.THIRD_PARTY_URL)+"/";
		final String siteURL = HTTPS_WITH_SLASH+(String)(siteRepItem.getPropertyValue(PRODUCTION_URL));

		final Object currObject = siteRepItem.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY);
		final Object giftWrapperProduct = siteRepItem.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRODUCT_SITE_PROPERTY_NAME);
		if(giftWrapperProduct!= null) {
			productsList.add((RepositoryItem)giftWrapperProduct);
		}

		for (RepositoryItem productItem: productsList) {
			productWrapper = new RepositoryItemWrapper(productItem);				
				
			if(!isProductWebOffered(productItem, siteName)) {
				addDeactiveProducts(productItem.getRepositoryId());
				continue;
			}
			//skuId,productDescription,productLongDescription,productPageURL,productImageURL,categoryDescription,staticNullText,
			//productImageURL,8price,brandName,**10skuColor,staticNewText,staticNullText,staticNullText,jdaDeptDescription,giftWrapEligible,
			//isInStock,staticNullText,staticNullText,staticNullText,staticNullText,staticNullText,22partNumberStaticText,attrDescStaticText,
			//price,staticNullText,staticNullText,**27skuSize,productDescription,upcCode
			
			final List<RepositoryItem> skus = productWrapper.getList(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
			if(skus== null) {
				getProductsWithOutSkus().append(productItem.getRepositoryId()+"\n");
				continue;
			}
			
			feedVO = new MarketingFeedVO();
			// Setting Product Attributes
			final String productName = productWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME);
			//productDescription
			feedVO.addFeedDataMap(feedHeaders.get(1), productWrapper.getString(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME));
			//productLongDescription
			feedVO.addFeedDataMap(feedHeaders.get(2), productWrapper.getString(BBBCatalogConstants.LONG_DESCRIPTION_PRODUCT_PROPERTY_NAME));
			//productPageURL
			feedVO.addFeedDataMap(feedHeaders.get(3), siteURL+ getProductPageURL(productName, productItem.getRepositoryId()));
			//productImageURL
			feedVO.addFeedDataMap(feedHeaders.get(4), seanURL+ productWrapper.getString(SCENE7URL));
			//staticNullText
			feedVO.addFeedDataMap(feedHeaders.get(6), "");
			//brandName
			feedVO.addFeedDataMap(feedHeaders.get(9), getProductBrandName(productWrapper));
			//attrDescStaticText
			feedVO.addFeedDataMap(feedHeaders.get(23), "");
			
			//currency
			feedVO.addFeedDataMap(feedHeaders.get(9), currObject!=null?(String)currObject:"USD");
			
			
			//Setting Category Attributes
			feedVO.addFeedDataMap(feedHeaders.get(5), getParentCategoryName(productWrapper, siteName));
			
			for(RepositoryItem sku: skus) {
				skuWrapper = new RepositoryItemWrapper(sku);
				
				if(!isSkuWebOffered(sku, siteName)) {
					addProductsWithDisabledSkus(productItem.getRepositoryId(), sku.getRepositoryId());
					continue;
				}

				final MarketingFeedVO feedVOBySku = (MarketingFeedVO)feedVO.deepClone();
				//Setting Sku Attributes
				
				//skuId
				feedVOBySku.addFeedDataMap(feedHeaders.get(0), sku.getRepositoryId());
				
				//price
				feedVOBySku.addFeedDataMap(feedHeaders.get(8), String.valueOf(getListPriceBySite(siteRepItem,productItem.getRepositoryId(), sku.getRepositoryId())));
				//skuColor
				catItemProp = skuWrapper.getString(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME);
				feedVOBySku.addFeedDataMap(feedHeaders.get(10), catItemProp!=null?catItemProp:"");
				
				// jdaSubDeptDescription
				final RepositoryItem jdaSubDept = skuWrapper
						.getRepositoryItem(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME);
				feedVOBySku.addFeedDataMap(feedHeaders.get(14),
								jdaSubDept == null ? 
										"": (String) jdaSubDept.getPropertyValue(BBBCatalogConstants.DESCRIPTION_JDA_PROPERTY_NAME));
				//giftWrapEligible
				final boolean isGiftWrapEligible = skuWrapper.getBoolean(BBBCatalogConstants.GIFT_WRAP_ELIGIBLE_SKU_PROPERTY_NAME);
				feedVOBySku.addFeedDataMap(feedHeaders.get(15), Boolean.toString(isGiftWrapEligible)!=null && isGiftWrapEligible?YES:NO);
				//isInStock
				int available = 1;
				try {
					available = getInventoryManager().getProductAvailability(siteName, sku.getRepositoryId(), BBBInventoryManager.PRODUCT_DISPLAY,0);
				}catch(Exception e) {
					logError(INVENTORY_EXCEPTION_OCCURED_FOR_SKU+sku.getRepositoryId() +"\n"+e.getMessage());
				}
				feedVOBySku.addFeedDataMap(feedHeaders.get(16), available==1 ? NO: YES);
				
				//skuSize
				catItemProp = skuWrapper.getString(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME);					
				feedVOBySku.addFeedDataMap(feedHeaders.get(27), catItemProp!=null?catItemProp:"");			
				//upc and partNumber
				feedVOBySku.addFeedDataMap(feedHeaders.get(29), skuWrapper.getString(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME));
				feedVOList.add(feedVOBySku);	
			}
		}
		MLOGGING.logInfo("BBBMarketingFeedTools [getICrossingMarketingFeedVOList] end");
		return feedVOList;
	}

	/**
	 * This method will generate Rimm Kaufman Group Marketing feed VO's
	 * @param isFullDataFeed
	 * @param lastModDate
	 * @param feedHeaders
	 * @param catalogId
	 * @param siteName
	 * @return MarketingFeedVO list
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public List<MarketingFeedVO> getRKGMarketingFeedVOList(
			final boolean isFullDataFeed,final Timestamp lastModDate,
			final List<String> feedHeaders,final String catalogId,final String siteName)  throws BBBSystemException, BBBBusinessException {

		MLOGGING.logInfo("BBBMarketingFeedTools [getRKGMarketingFeedVOList] start");
		final List<MarketingFeedVO> feedVOList = new ArrayList<MarketingFeedVO>();
		final RepositoryItem[] productItems = getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
													BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		if (productItems == null ||
				productItems.length == 0) {

			MLOGGING.logInfo("No data available in repository for RKG Marketing Feed");
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1008,BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
		}
		final List<RepositoryItem> productsList = new ArrayList<RepositoryItem>();
		productsList.addAll(Arrays.asList(productItems));

		RepositoryItemWrapper productWrapper = null;
		RepositoryItemWrapper skuWrapper = null;			
		MarketingFeedVO feedVO = null;
		RepositoryItem siteRepItem = null;
		String catItemPropSkuColor;  //BBBSL-6884
		String brand;                
		
		try {
			siteRepItem = getSiteManager().getSite(siteName);
			if(siteRepItem == null) {
				throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1009,siteName+IS_NOT_CONFIGURED);
			}
		} catch (RepositoryException e) {
			MLOGGING.logError("RepositoryException", e);
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1010,siteName+IS_NOT_CONFIGURED);
		}
		final String seanURL = HTTPS+getConfigTemplateManager().getThirdPartyURL(SCENE7_URL, BBBCoreConstants.THIRD_PARTY_URL)+"/";
		final String siteURL = HTTPS_WITH_SLASH+(String)(siteRepItem.getPropertyValue(PRODUCTION_URL));

		final Object giftWrapperProduct = siteRepItem.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRODUCT_SITE_PROPERTY_NAME);
		if(giftWrapperProduct!= null) {
			productsList.add((RepositoryItem)giftWrapperProduct);
		}
		
		for (RepositoryItem productItem: productsList) {

			productWrapper = new RepositoryItemWrapper(productItem);
				
			if(!isProductWebOffered(productItem, siteName)) {
				addDeactiveProducts(productItem.getRepositoryId());
				continue;
			}

			final List<RepositoryItem> skus = productWrapper.getList(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
			if(skus== null) {
				getProductsWithOutSkus().append(productItem.getRepositoryId()+"\n");
				continue;
			}
			
			feedVO = new MarketingFeedVO();
			// Setting Product Attributes
			final String productName = getPlainText(productWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
			
			//category			
			feedVO.addFeedDataMap(feedHeaders.get(1), getParentCategoryName(productWrapper, siteName));

			//link
			//feedVO.addFeedDataMap(feedHeaders.get(2), siteURL+ getProductPageURL(productName, productItem.getRepositoryId()));

			//brandName
			 brand = getProductBrandName(productWrapper);
			feedVO.addFeedDataMap(feedHeaders.get(5), brand);			

			//image  ----> productImageURL
			feedVO.addFeedDataMap(feedHeaders.get(8), seanURL+ productWrapper.getString(SCENE7URL));
			
			//productDescription
			feedVO.addFeedDataMap(feedHeaders.get(9), getPlainText(productWrapper.getString(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)));	
			
			//Product Id - item group id
			feedVO.addFeedDataMap(feedHeaders.get(20), productItem.getRepositoryId());

			for(RepositoryItem sku: skus) {
				skuWrapper = new RepositoryItemWrapper(sku);
				if(!isSkuWebOffered(sku, siteName)) {
					addProductsWithDisabledSkus(productItem.getRepositoryId(), sku.getRepositoryId());
					continue;
				}
				
				final MarketingFeedVO feedVOBySku = (MarketingFeedVO)feedVO.deepClone();
				// setting product url with skuId
				feedVOBySku.addFeedDataMap(feedHeaders.get(2), (siteURL+getProductPageURL(productName, productItem.getRepositoryId())+BBBCatalogConstants.SKU_ID_CONSTANT+sku.getRepositoryId()));
				//Setting Sku Attributes
				
				//skuId
				feedVOBySku.addFeedDataMap(feedHeaders.get(3), sku.getRepositoryId());

				//price
				feedVOBySku.addFeedDataMap(feedHeaders.get(4), String.valueOf(getSalePriceBySite(siteRepItem, productItem.getRepositoryId(), sku.getRepositoryId())));
				
				//image  ----> SkuImageUrl
				feedVOBySku.addFeedDataMap(feedHeaders.get(8), seanURL+ skuWrapper.getString(SCENE7URL));
				
				// jdaDeptDeptDescription
				final RepositoryItem jdaDept = skuWrapper
						.getRepositoryItem(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME);
				feedVOBySku.addFeedDataMap(feedHeaders.get(6),
								jdaDept == null ? 
										"": (String) jdaDept.getPropertyValue(BBBCatalogConstants.DESCRIPTION_JDA_PROPERTY_NAME));
				feedVOBySku.addFeedDataMap(feedHeaders.get(10),
						jdaDept == null ? 
								"": (String) jdaDept.getPropertyValue(BBBCatalogConstants.DESCRIPTION_JDA_PROPERTY_NAME));
				
				
				//upc and partNumber
				feedVOBySku.addFeedDataMap(feedHeaders.get(7), skuWrapper.getString(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME));

				//isInStock
				int available = 1;
				try {
					available = getInventoryManager().getProductAvailability(siteName, sku.getRepositoryId(), BBBInventoryManager.PRODUCT_DISPLAY,0);
				}catch(Exception e) {
					logError(INVENTORY_EXCEPTION_OCCURED_FOR_SKU+sku.getRepositoryId() +"\n"+e.getMessage());
				}
				feedVOBySku.addFeedDataMap(feedHeaders.get(12), available==1 ? NO: YES);
				
				//skuColor
				catItemPropSkuColor = skuWrapper.getString(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME);
				feedVOBySku.addFeedDataMap(feedHeaders.get(13), catItemPropSkuColor!=null?catItemPropSkuColor:"");

				//Setting size Attributes
				String sizeProperty = skuWrapper.getString(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME);
				Map<String, String> units= new HashMap<String,String>();
				units = this.getUnits();
				
				if(!BBBUtility.isEmpty(sizeProperty) && units != null){
					sizeProperty = sizeProperty.replace("&#39;", BBBUtility.isEmpty(units.get("feet")) ? "ft": (String)units.get("feet"));
					sizeProperty = sizeProperty.replace("&quot;", BBBUtility.isEmpty(units.get("inch")) ? "inch": (String)units.get("inch"));
				}
				feedVOBySku.addFeedDataMap(feedHeaders.get(14),sizeProperty);
				
				//productName
				feedVOBySku.addFeedDataMap(feedHeaders.get(0), getTitleWithBrandColorSize(brand, productName, catItemPropSkuColor, sizeProperty));

				//skuWithFreeStdShipping
				feedVOBySku.addFeedDataMap(feedHeaders.get(15),
						getCatalogTools().isFreeShipping(siteName,
								sku.getRepositoryId(),
								getShippingMethodMap().get("standard")) ? 
								sku.getRepositoryId() : "");
				
				//vendorSku
				feedVOBySku.addFeedDataMap(
								feedHeaders.get(16),
								StringUtils.isEmpty(skuWrapper
										.getString(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME)) ? ""
										: sku.getRepositoryId());
				
				//isWebOnly
				
				final boolean isWebOnly = isSkuWebOnly(sku, siteName);
				feedVOBySku.addFeedDataMap(
						feedHeaders.get(17), isWebOnly?YES:NO);
				Double deliverSurcharge=0.0;
				if(null!= sku.getPropertyValue("shippingSurchargeDefault"))
				{
					deliverSurcharge=(Double)sku.getPropertyValue("shippingSurchargeDefault");
				}
				if(sku.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU)==null || Boolean.FALSE==(Boolean)sku.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU))
				{
					
					feedVOBySku.addFeedDataMap(feedHeaders.get(18), NO_CHAR);
					feedVOBySku.addFeedDataMap(
							feedHeaders.get(19),Double.toString(deliverSurcharge));
					
				}else
				{
					feedVOBySku.addFeedDataMap(feedHeaders.get(18), YES_CHAR);
					 deliverSurcharge += getDeliveryChargeForLTLSku(sku.getRepositoryId(), siteName);
					 feedVOBySku.addFeedDataMap(
								feedHeaders.get(19),Double.toString(deliverSurcharge));
				}
					
						
			
				
				feedVOList.add(feedVOBySku);
				
				//vendorName
				//feedVOBySku.addFeedDataMap(feedHeaders.get(10), skuWrapper.getString(BBBCatalogConstants.VENDOR_ID_SKU_PROPERTY_NAME));
				
				


         // emptying the StringBuffer
				
					
			}
		}
		MLOGGING.logInfo("BBBMarketingFeedTools [getRKGMarketingFeedVOList] end");
		return feedVOList;
	}

	/**
	 * This method will generate Facebook feed VO's
	 * @param isFullDataFeed
	 * @param lastModDate
	 * @param feedHeaders
	 * @param catalogId
	 * @param siteName
	 * @return MarketingFeedVO list
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public List<MarketingFeedVO> getFacebookFeedVOList(
			final boolean isFullDataFeed,final Timestamp lastModDate,
			final List<String> feedHeaders,final String catalogId,final String siteName)  throws BBBSystemException, BBBBusinessException {

		MLOGGING.logInfo("BBBMarketingFeedTools [getFacebookFeedVOList] start");
		final List<MarketingFeedVO> feedVOList = new ArrayList<MarketingFeedVO>();
		final RepositoryItem[] productItems = getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
													BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		if (productItems == null ||
				productItems.length == 0) {

			MLOGGING.logInfo("No data available in repository for Facebook Feed");
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1008,BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
		}
		final List<RepositoryItem> productsList = new ArrayList<RepositoryItem>();
		productsList.addAll(Arrays.asList(productItems));

		RepositoryItemWrapper productWrapper = null;
		RepositoryItemWrapper skuWrapper = null;			
		MarketingFeedVO feedVO = null;
		RepositoryItem siteRepItem = null;
		
		try {
			siteRepItem = getSiteManager().getSite(siteName);
			if(siteRepItem == null) {
				throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1009,siteName+IS_NOT_CONFIGURED);
			}
		} catch (RepositoryException e) {
			MLOGGING.logError("RepositoryException", e);
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1010,siteName+IS_NOT_CONFIGURED);
		}
		final String seanURL = HTTPS+getConfigTemplateManager().getThirdPartyURL(SCENE7_URL, BBBCoreConstants.THIRD_PARTY_URL)+"/";
		final String siteURL = HTTPS_WITH_SLASH+(String)(siteRepItem.getPropertyValue(PRODUCTION_URL));

		final Object giftWrapperProduct = siteRepItem.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRODUCT_SITE_PROPERTY_NAME);
		if(giftWrapperProduct!= null) {
			productsList.add((RepositoryItem)giftWrapperProduct);
		}
		
		for (RepositoryItem productItem: productsList) {

			productWrapper = new RepositoryItemWrapper(productItem);
				
			if(!isProductWebOffered(productItem, siteName)) {
				addDeactiveProducts(productItem.getRepositoryId());
				continue;
			}

			final List<RepositoryItem> skus = productWrapper.getList(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
			if(skus== null) {
				getProductsWithOutSkus().append(productItem.getRepositoryId()+"\n");
				continue;
			}
			
			feedVO = new MarketingFeedVO();
			// Setting Product Attributes
			final String productName = getPlainText(productWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));

			//titile
			feedVO.addFeedDataMap(feedHeaders.get(0), productName);

			//link
			feedVO.addFeedDataMap(feedHeaders.get(1), siteURL+ getProductPageURL(productName, productItem.getRepositoryId()));

			//image  ----> productImageURL
			feedVO.addFeedDataMap(feedHeaders.get(4), seanURL+ productWrapper.getString(SCENE7URL));
			
			//productDescription
			feedVO.addFeedDataMap(feedHeaders.get(5), getPlainText(productWrapper.getString(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)));	
			
			for(RepositoryItem sku: skus) {
				skuWrapper = new RepositoryItemWrapper(sku);
				if(!isSkuWebOffered(sku, siteName)) {
					addProductsWithDisabledSkus(productItem.getRepositoryId(), sku.getRepositoryId());
					continue;
				}

				final MarketingFeedVO feedVOBySku = (MarketingFeedVO)feedVO.deepClone();
				
				//skuId
				feedVOBySku.addFeedDataMap(feedHeaders.get(2), sku.getRepositoryId());
				
				String price = String.valueOf(getSalePriceBySite(siteRepItem, productItem.getRepositoryId(), sku.getRepositoryId()));
				final Object currObject = siteRepItem.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY);
				String currency = BBBCoreConstants.BLANK;
				if(currObject!=null){
					currency = (String)currObject;
				}
				else{
					currency = BBBCheckoutConstants.SITE_CURRENCY_USD;
				}
				
				//price
				feedVOBySku.addFeedDataMap(feedHeaders.get(3), price + BBBCoreConstants.BLANK + currency);
				
				//upc and partNumber
				feedVOBySku.addFeedDataMap(feedHeaders.get(6), skuWrapper.getString(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME));

				//isInStock
				int available = 1;
				try {
					available = getInventoryManager().getProductAvailability(siteName, sku.getRepositoryId(), BBBInventoryManager.PRODUCT_DISPLAY,0);
				}catch(Exception e) {
					logError(INVENTORY_EXCEPTION_OCCURED_FOR_SKU+sku.getRepositoryId() +"\n"+e.getMessage());
				}
				feedVOBySku.addFeedDataMap(feedHeaders.get(7), available==1 ? BBBCheckoutConstants.OUT_OF_STOCK : BBBCheckoutConstants.IN_STOCK);
				
				feedVOBySku.addFeedDataMap(feedHeaders.get(8), BBBCheckoutConstants.NEW);
				
				feedVOList.add(feedVOBySku);
				
			}
		}
		MLOGGING.logInfo("BBBMarketingFeedTools [getFacebookFeedVOList] end");
		return feedVOList;
	}
	
	/**
	 * This method will generate Your Amigo Marketing feed VO's
	 * @param isFullDataFeed
	 * @param lastModDate
	 * @param feedHeaders
	 * @param catalogId
	 * @param siteName
	 * @return MarketingFeedVO list
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public List<MarketingFeedVO> getYourAmigoMarketingFeedVOList(
			final boolean isFullDataFeed,final Timestamp lastModDate,
			final List<String> feedHeaders,final String catalogId,final String siteName)  throws BBBSystemException, BBBBusinessException {
		
		MLOGGING.logInfo("BBBMarketingFeedTools [getYourAmigoMarketingFeedVOList] start");
		final List<MarketingFeedVO> feedVOList = new ArrayList<MarketingFeedVO>();
		final RepositoryItem[] productItems = getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
													BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		if (productItems == null ||
				productItems.length == 0) {
			
			MLOGGING.logInfo("No data available in repository for Your Amigo Marketing Feed");
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1008,BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
		}
		final List<RepositoryItem> productsList = new ArrayList<RepositoryItem>();
		productsList.addAll(Arrays.asList(productItems));

		RepositoryItemWrapper productWrapper = null;
		RepositoryItemWrapper skuWrapper = null;			
		MarketingFeedVO feedVO = null;
		RepositoryItem siteRepItem = null;
		
		try {
			siteRepItem = getSiteManager().getSite(siteName);
			if(siteRepItem == null) {
				throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1009,siteName+IS_NOT_CONFIGURED);
			}
		} catch (RepositoryException e) {
			throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1010,siteName+IS_NOT_CONFIGURED);
		}
		final String seanURL = HTTPS+getConfigTemplateManager().getThirdPartyURL(SCENE7_URL, BBBCoreConstants.THIRD_PARTY_URL)+"/";
		final String siteURL = HTTPS_WITH_SLASH+(String)(siteRepItem.getPropertyValue(PRODUCTION_URL));

		final Object currObject = siteRepItem.getPropertyValue(BBBCheckoutConstants.SITE_CURRENCY);
		final Object giftWrapperProduct = siteRepItem.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRODUCT_SITE_PROPERTY_NAME);
		if(giftWrapperProduct!= null) {
			productsList.add((RepositoryItem)giftWrapperProduct);
		}
		//productDescription,productDescription,productLongDescription,skuId,productPageURL,productImageURL,price,price
		//currency,upcCode,attrDescStaticText,categoryDescription,partNumberStaticText,giftWrapEligible,isInStock,jdaSubDeptDescription,
		//vendorSku

		for (RepositoryItem productItem: productsList) {
			productWrapper = new RepositoryItemWrapper(productItem);				
				
			if(!isProductWebOffered(productItem, siteName)) {
				addDeactiveProducts(productItem.getRepositoryId());
				continue;
			}
			
			final List<RepositoryItem> skus = productWrapper.getList(BBBCatalogConstants.CHILD_SKU_PRODUCT_PROPERTY_NAME);
			if(skus== null) {
				getProductsWithOutSkus().append(productItem.getRepositoryId()+"\n");
				continue;
			}
			
			feedVO = new MarketingFeedVO();
			// Setting Product Attributes
			final String productName = getPlainText(productWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
			
			//NAME ---> displayName
			feedVO.addFeedDataMap(feedHeaders.get(0), productName);
			
			//KEYWORDS ---> No Matching field. SEO Keywords are not been used to skipping this.
			feedVO.addFeedDataMap(feedHeaders.get(1), productName);
			
			//DESCRIPTION  ---> productDescription
			feedVO.addFeedDataMap(feedHeaders.get(2), getPlainText(productWrapper.getString(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME)));
			
			//BUYURL   ---> productPageURL
			feedVO.addFeedDataMap(feedHeaders.get(4), siteURL+ getProductPageURL(productName, productItem.getRepositoryId()));
			
			//IMAGEURL  
			feedVO.addFeedDataMap(feedHeaders.get(6), seanURL+ productWrapper.getString(SCENE7URL));
			
			//currency
			feedVO.addFeedDataMap(feedHeaders.get(9), currObject!=null?(String)currObject:"USD");
			
			//ADVERTISERCATEGORY  --- Cataegory Name 
			feedVO.addFeedDataMap(feedHeaders.get(12), getProductNavigationPath(productItem, catalogId));

			//MANUFACTURERID  -----> attrDescStaticText
			//feedVO.addFeedDataMap(feedHeaders.get(13), "");
			
			//BRANDNAME  ----> brandName
			final RepositoryItem brandRepositoryItem= productWrapper.getRepositoryItem(BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR);
			if(brandRepositoryItem!=null){
				feedVO.addFeedDataMap(feedHeaders.get(17), (String)brandRepositoryItem.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME));
			}
			
			BazaarVoiceProductVO bazaarVoiceVO = getCatalogTools().getBazaarVoiceDetails(productItem.getRepositoryId());
			
            feedVO.addFeedDataMap(feedHeaders.get(21),
            			String.valueOf(bazaarVoiceVO.getAverageOverallRating()));
                    
           	feedVO.addFeedDataMap(feedHeaders.get(22),
            			(String.valueOf(bazaarVoiceVO.getTotalReviewCount())));
			
			for(RepositoryItem sku: skus) {
				skuWrapper = new RepositoryItemWrapper(sku);

				if(!isSkuWebOffered(sku, siteName)) {
					addProductsWithDisabledSkus(productItem.getRepositoryId(), sku.getRepositoryId());
					continue;
				}

				final MarketingFeedVO feedVOBySku = (MarketingFeedVO)feedVO.deepClone();
				//Setting Sku Attributes
				
				//skuId
				feedVOBySku.addFeedDataMap(feedHeaders.get(3), sku.getRepositoryId());
				
				//PRICE   -----> price
				feedVOBySku.addFeedDataMap(feedHeaders.get(7), String.valueOf(getSalePriceBySite(siteRepItem, productItem.getRepositoryId(), sku.getRepositoryId())));
				
				//RETAILPRICE   -----> price
				feedVOBySku.addFeedDataMap(feedHeaders.get(8), String.valueOf(getListPriceBySite(siteRepItem, productItem.getRepositoryId(), sku.getRepositoryId())));
				
				//UPC   -------> upc and partNumber
				feedVOBySku.addFeedDataMap(feedHeaders.get(10), skuWrapper.getString(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME));
				
				//skuColor
				feedVOBySku.addFeedDataMap(feedHeaders.get(19), skuWrapper.getString(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME)!=null?
						skuWrapper.getString(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME):"");

				//skuSize
				feedVOBySku.addFeedDataMap(feedHeaders.get(20), skuWrapper.getString(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME)!=null?
						skuWrapper.getString(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME):"");
				
				//PROMOTIONALTEXT ------> Need to decided which to showup
				//feedVOBySku.addFeedDataMap(feedHeaders.get(11), "");
				
				//GIFT  ---------> giftWrapEligible
				final boolean isGiftWrapEligible = skuWrapper.getBoolean(BBBCatalogConstants.GIFT_WRAP_ELIGIBLE_SKU_PROPERTY_NAME);
				feedVOBySku.addFeedDataMap(feedHeaders.get(14), Boolean.toString(isGiftWrapEligible)!=null && isGiftWrapEligible?YES:NO);
				
				//isInStock
				int available = 1;
				try {
					available = getInventoryManager().getProductAvailability(siteName, sku.getRepositoryId(), BBBInventoryManager.PRODUCT_DISPLAY,0);
				}catch(Exception e) {
					logError(INVENTORY_EXCEPTION_OCCURED_FOR_SKU+sku.getRepositoryId() +"\n"+e.getMessage());
				}
				feedVOBySku.addFeedDataMap(feedHeaders.get(5), available==1 ? NO: YES);
				feedVOBySku.addFeedDataMap(feedHeaders.get(15), available==1 ? NO: YES);
				
				
				// MERCHANDISETYPE  -----> jdaSubDeptDescription
				final RepositoryItem jdaSubDept = skuWrapper
						.getRepositoryItem(BBBCatalogConstants.JDA_SUBDEPT_SKU_PROPERTY_NAME);
				feedVOBySku.addFeedDataMap(feedHeaders.get(16),
								jdaSubDept == null ? 
										"": (String) jdaSubDept.getPropertyValue(BBBCatalogConstants.DESCRIPTION_JDA_PROPERTY_NAME));
				//VDCSKU -----> vendorSku
				feedVOBySku.addFeedDataMap(
						feedHeaders.get(18),
						StringUtils.isEmpty(skuWrapper
								.getString(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME)) ? ""
								: sku.getRepositoryId());
				feedVOList.add(feedVOBySku);	
			}
		}
		MLOGGING.logInfo("BBBMarketingFeedTools [getYourAmigoMarketingFeedVOList] end");
		return feedVOList;
	}

	/**
	 * @param isFullDataFeed
	 * @param lastModDate
	 * @param feedHeaders
	 * @param catalogId
	 * @param siteName
	 * @return MarketingFeedVO list
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<MarketingFeedVO> getOmnitureCollectionMarketingFeedVOList(
			final boolean isFullDataFeed,final Timestamp lastModDate,
			final List<String> feedHeaders,final String catalogId,final String siteName) throws BBBSystemException, BBBBusinessException {
		MLOGGING.logInfo("BBBMarketingFeedTools [getOmnitureCollectionMarketingFeedVOList] start");
		final List<MarketingFeedVO> feedVOList = new ArrayList<MarketingFeedVO>();
		final RepositoryItem[] collectionItems = getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
				"collection", "collection=true");
		
		if (collectionItems == null ||
				collectionItems.length == 0) {

			MLOGGING.logInfo("No data available in repository for Omniture collection Marketing Feed");
			return null;
			//throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1008,BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
		}
		final List<RepositoryItem> productList = new ArrayList<RepositoryItem>();
		productList.addAll(Arrays.asList(collectionItems));

		RepositoryItemWrapper productWrapper = null;
		MarketingFeedVO feedVO = null;
		
		RepositoryItem tempCategoryItem = null;
		
		String prdLTL = NO;
		//BBBH-1596 Updating Omniture Feed start
		Site site;
		try {
			site = getSiteContextManager().getSite(siteName);
			final SiteContextImpl context = new SiteContextImpl(getSiteContextManager(),site);
			getSiteContextManager().pushSiteContext(context);
			//Key, Collection Friendly Name, Collection Type, Main Level, Category, SubCategory, Brand
			for (RepositoryItem productItem: productList) {
				try {
					productWrapper = new RepositoryItemWrapper(productItem);				
					
					if(!isProductWebOffered(productItem, siteName)) {
						addDeactiveProducts(productItem.getRepositoryId());
						continue;
					}
					
					
					feedVO = new MarketingFeedVO();
					// Setting Product Attributes
					//Key,
					feedVO.addFeedDataMap(feedHeaders.get(0), productItem.getRepositoryId());
					
					//Collection Friendly Name
					feedVO.addFeedDataMap(feedHeaders.get(1), getPlainText(productWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)));
					//Collection Type
					feedVO.addFeedDataMap(feedHeaders.get(2), "");
					
					//SubCategory
					//BBBH-1596  - Omniture L2 boosting changes start
					String subCategory = null;
					String subCategoryId = null;
					
					tempCategoryItem = getParentCateogry(productItem, catalogId);
					if(tempCategoryItem != null){
						subCategory  =(String)tempCategoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME);
						subCategoryId = tempCategoryItem.getRepositoryId();
					}
					
					//Category
					String category=null;
					String categoryId = null;
					final StringBuilder categorySB = new StringBuilder();
					final StringBuilder subCategorySB = new StringBuilder();
					tempCategoryItem = getParentCateogry(tempCategoryItem, catalogId);
					if(tempCategoryItem != null){
						category  =(String)tempCategoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME);
						categoryId = tempCategoryItem.getRepositoryId();
						
					}
					
					//Main Level
					String main = null;
					String mainId = null;
					final StringBuilder mainSB = new StringBuilder();
					tempCategoryItem = getParentCateogry(tempCategoryItem, catalogId);
					if((tempCategoryItem != null)){
						main = (String)tempCategoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME);
						mainId = tempCategoryItem.getRepositoryId();
					}
					//When product is tagged to L1 directly
					if(BBBUtility.compareStringsIgnoreCase(category, BBBCatalogConstants.COMMERCE_ROOT)) {
						subCategory = BBBUtility.isEmpty(subCategory) ? ""
								: subCategorySB.append(subCategory)
										.append(BBBCoreConstants.SEMICOLON)
										.append(subCategoryId).toString();
						//Setting L1 in Main header and blank strings in Category and Sub Category headers.
						feedVO.addFeedDataMap(feedHeaders.get(3), subCategory);
						feedVO.addFeedDataMap(feedHeaders.get(4), BBBCatalogConstants.CATEGORY_ID_BLANK);
						feedVO.addFeedDataMap(feedHeaders.get(5), BBBCatalogConstants.CATEGORY_ID_BLANK);
					} 
					//When product is tagged to L2 directly
					else if ((tempCategoryItem != null)
							&& ((String) tempCategoryItem
									.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME))
									.equalsIgnoreCase(BBBCatalogConstants.COMMERCE_ROOT)) {
						subCategory = (BBBUtility.isEmpty(category) && BBBUtility.isEmpty(subCategory)) ? ""
								: subCategorySB
								.append(category)
								.append(BBBCoreConstants.GREATER_THAN_SYMBOL)
								.append(subCategory)
								.append(BBBCoreConstants.SEMICOLON)
								.append(subCategoryId).toString();
						category = BBBUtility.isEmpty(category) ? "" : categorySB
								.append(category)
								.append(BBBCoreConstants.SEMICOLON)
								.append(categoryId).toString();
						//Setting L1 in Main Header and L2 in Category header and blank string in Sub Category header.
						feedVO.addFeedDataMap(feedHeaders.get(3), category);
						feedVO.addFeedDataMap(feedHeaders.get(4), subCategory);
						feedVO.addFeedDataMap(feedHeaders.get(5), BBBCatalogConstants.CATEGORY_ID_BLANK);
					}
					//When product is not tagged to any categories or to L3
					else {
						subCategory = (BBBUtility.isEmpty(main) && BBBUtility.isEmpty(category) && BBBUtility.isEmpty(subCategory)) ? ""
								: subCategorySB
								.append(main)
								.append(BBBCoreConstants.GREATER_THAN_SYMBOL)
								.append(category)
								.append(BBBCoreConstants.GREATER_THAN_SYMBOL)
								.append(subCategory)
								.append(BBBCoreConstants.SEMICOLON)
								.append(subCategoryId).toString();
						category = (BBBUtility.isEmpty(main) && BBBUtility.isEmpty(category)) ? ""
								: categorySB
								.append(main)
								.append(BBBCoreConstants.GREATER_THAN_SYMBOL)
								.append(category)
								.append(BBBCoreConstants.SEMICOLON)
								.append(categoryId).toString();
						main = BBBUtility.isEmpty(main) ? "" : mainSB.append(main)
								.append(BBBCoreConstants.SEMICOLON).append(mainId)
								.toString();
						feedVO.addFeedDataMap(feedHeaders.get(3), main);
						feedVO.addFeedDataMap(feedHeaders.get(4), category);
						feedVO.addFeedDataMap(feedHeaders.get(5), subCategory);
					}
					//BBBH-1596 end
					//Brand
					feedVO.addFeedDataMap(feedHeaders.get(6), getProductBrandName(productWrapper));
					
					//If one of the child SKUs is LTL, then mark it as Yes
					final List<RepositoryItem> childSkus = this.getChildSkus(productItem);
					if(null != childSkus) {
						for (RepositoryItem repositoryItem : childSkus) {
							boolean isSkuLtl = (Boolean) repositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU);
							if(isSkuLtl){
								prdLTL = YES;
								break;
							} 
						}
					}
					
					feedVO.addFeedDataMap(feedHeaders.get(7), prdLTL);
					
					feedVOList.add(feedVO);
					
				} catch (Exception e) {
					MLOGGING.logError(e.getMessage());
					continue;
				}
			}
			getSiteContextManager().popSiteContext(context);
		} catch (SiteContextException e1) {
			MLOGGING.logError("SiteContextException occured in BBBMarketingFeedTools"
					+ "getOmnitureCollectionMarketingFeedVOList" +
					"while while pushing siteId for catalogId " + catalogId, e1);
		}
		//BBBH-1596 Updating Omniture Feed end
		MLOGGING.logInfo("BBBMarketingFeedTools [getOmnitureCollectionMarketingFeedVOList] end");
		return feedVOList;
	}
	
	/**
	 * @param isFullDataFeed
	 * @param lastModDate
	 * @param feedHeaders
	 * @param catalogId
	 * @param siteName
	 * @return MarketingFeedVO list
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<MarketingFeedVO> getOmnitureProductMarketingFeedVOList(
			final boolean isFullDataFeed,final Timestamp lastModDate,
			final List<String> feedHeaders,final String catalogId,final String siteName) throws BBBSystemException, BBBBusinessException {
		MLOGGING.logInfo("BBBMarketingFeedTools [getOmnitureProductMarketingFeedVOList] start");
		final List<MarketingFeedVO> feedVOList = new ArrayList<MarketingFeedVO>();
		final RepositoryItem[] productItems = getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
													BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		
		if (productItems == null ||
				productItems.length == 0) {
			
			MLOGGING.logInfo("No data available in repository for Omniture Product Marketing Feed");
			return null;
			//throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1008,BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
		}
		final List<RepositoryItem> productList = new ArrayList<RepositoryItem>();
		productList.addAll(Arrays.asList(productItems));
		RepositoryItemWrapper productWrapper = null;
		MarketingFeedVO feedVO = null;
		//BBBH-1596 Updating Omniture Feed start
		Site site;
		try {
			site = getSiteContextManager().getSite(siteName);
			final SiteContextImpl context = new SiteContextImpl(getSiteContextManager(),site);
			getSiteContextManager().pushSiteContext(context);
			RepositoryItem tempCategoryItem = null;
			//Key, Friendly Name, Main Level, Category, SubCategory, Brand, Product Type
			
			for (RepositoryItem productItem: productList) {
				try {
					productWrapper = new RepositoryItemWrapper(productItem);		
					if(!isProductWebOffered(productItem, siteName)) {
						addDeactiveProducts(productItem.getRepositoryId());
						continue;
					}
					
					feedVO = new MarketingFeedVO();
					String prdVDC = NO;
					String prdLTL = NO;
					// Setting Product Attributes
					//Key,
					feedVO.addFeedDataMap(feedHeaders.get(0), productItem.getRepositoryId());
					
					//Friendly Name
					feedVO.addFeedDataMap(feedHeaders.get(1), getPlainText(productWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)));
					
					//SubCategory
					//BBBH-1596 Updating omniture feed start
					tempCategoryItem = getParentCateogry(productItem, catalogId);
					
					// BBBSL-4071 start
					// Get the category of the parent collection product if the category of the current product is not defined
					if (tempCategoryItem == null) {
						final RepositoryItem parentProductItem = getParentProductItem(productItem.getRepositoryId(), siteName);
						tempCategoryItem = getParentCateogry(parentProductItem, catalogId);
					}
					// BBBSL-4071 end
					
					String subCategory = null;
					String subCategoryId = null;
					if(tempCategoryItem != null){
						subCategory  =(String)tempCategoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME);
						subCategoryId = tempCategoryItem.getRepositoryId();
					}
					final StringBuilder subCategorySB = new StringBuilder();
					
					//Category				
					String Category=null;
					String categoryId = null;
					tempCategoryItem = getParentCateogry(tempCategoryItem, catalogId);
					//for PS-18469 : START
					if(tempCategoryItem != null){
						Category  =(String)tempCategoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME);
						categoryId = tempCategoryItem.getRepositoryId();
						
					}
					final StringBuilder categorySB = new StringBuilder();
					// for PS-18469 : END
					
					//Main Level
					tempCategoryItem = getParentCateogry(tempCategoryItem, catalogId);
					
					String main = null;
					String mainId = null;
					final StringBuilder mainSB = new StringBuilder();
					//for PS-18469 : START
					if((tempCategoryItem != null)){
						main = (String)tempCategoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME);
						mainId = tempCategoryItem.getRepositoryId();
					}
					//When product is tagged to L1 directly
					if(BBBUtility.compareStringsIgnoreCase(Category, BBBCatalogConstants.COMMERCE_ROOT)) {
						subCategory = BBBUtility.isEmpty(subCategory) ? ""
								: subCategorySB.append(subCategory)
										.append(BBBCoreConstants.SEMICOLON)
										.append(subCategoryId).toString();
						//Setting L1 in Main header and blank strings in Category and Sub Category headers.
						feedVO.addFeedDataMap(feedHeaders.get(2), subCategory);
						feedVO.addFeedDataMap(feedHeaders.get(3), BBBCatalogConstants.CATEGORY_ID_BLANK);
						feedVO.addFeedDataMap(feedHeaders.get(4), BBBCatalogConstants.CATEGORY_ID_BLANK);
					} 
					//When product is tagged to L2 directly
					else if ((tempCategoryItem != null)
							&& ((String) tempCategoryItem
									.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME))
									.equalsIgnoreCase(BBBCatalogConstants.COMMERCE_ROOT)) {
						subCategory = (BBBUtility.isEmpty(Category) && BBBUtility.isEmpty(subCategory)) ? ""
								: subCategorySB
								.append(Category)
								.append(BBBCoreConstants.GREATER_THAN_SYMBOL)
								.append(subCategory)
								.append(BBBCoreConstants.SEMICOLON)
								.append(subCategoryId).toString();
						Category = BBBUtility.isEmpty(Category) ? "" : categorySB
								.append(Category)
								.append(BBBCoreConstants.SEMICOLON)
								.append(categoryId).toString();
						//Setting L1 in Main Header and L2 in Category header and blank string in Sub Category header.
						feedVO.addFeedDataMap(feedHeaders.get(2), Category);
						feedVO.addFeedDataMap(feedHeaders.get(3), subCategory);
						feedVO.addFeedDataMap(feedHeaders.get(4), BBBCatalogConstants.CATEGORY_ID_BLANK);
					}
					//When product is not tagged to any categories or to L3
					else{
						subCategory = (BBBUtility.isEmpty(main) && BBBUtility.isEmpty(Category) && BBBUtility.isEmpty(subCategory)) ? ""
								: subCategorySB
								.append(main)
								.append(BBBCoreConstants.GREATER_THAN_SYMBOL)
								.append(Category)
								.append(BBBCoreConstants.GREATER_THAN_SYMBOL)
								.append(subCategory)
								.append(BBBCoreConstants.SEMICOLON)
								.append(subCategoryId).toString();
						Category = (BBBUtility.isEmpty(main) && BBBUtility.isEmpty(Category)) ? ""
								: categorySB
								.append(main)
								.append(BBBCoreConstants.GREATER_THAN_SYMBOL)
								.append(Category)
								.append(BBBCoreConstants.SEMICOLON)
								.append(categoryId).toString();
						main = BBBUtility.isEmpty(main) ? "" : mainSB.append(main)
								.append(BBBCoreConstants.SEMICOLON).append(mainId)
								.toString();
						feedVO.addFeedDataMap(feedHeaders.get(2), main);
						feedVO.addFeedDataMap(feedHeaders.get(3), Category);
						feedVO.addFeedDataMap(feedHeaders.get(4), subCategory);
					}
					
					//BBBH-1596 Updating omniture feed end
					
					//for PS-18469 : END
					
					//Brand
					feedVO.addFeedDataMap(feedHeaders.get(5), getProductBrandName(productWrapper));
					
					//Product Type
					feedVO.addFeedDataMap(feedHeaders.get(6), "");
					
					//If one of the child SKUs is VDC, then mark it as Yes
					final List<RepositoryItem> childSkus = this.getChildSkus(productItem);
					
					if(null != childSkus) {
						for (RepositoryItem repositoryItem : childSkus) {
							String isSkuVdc = (String) repositoryItem.getPropertyValue(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME);
							if(null != isSkuVdc){
								prdVDC = YES;
								break;
							} 
						}
					}
					feedVO.addFeedDataMap(feedHeaders.get(7), prdVDC);
					
					//If one of the child SKUs is LTL, then mark it as Yes
					if(null != childSkus) {
						for (RepositoryItem repositoryItem : childSkus) {
							boolean isSkuLtl = (Boolean) repositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU);
							if(isSkuLtl){
								prdLTL = YES;
								break;
							} 
						}
					}
					
					feedVO.addFeedDataMap(feedHeaders.get(8), prdLTL);
					//Personalized
					//If one of the child SKUs is customized offered, then mark it as Yes
					Boolean isCustomizationOffered= false;
					if(null != childSkus) {
						String customizationOfferedFlag = this.getCustomizationOfferedSiteMap().get(siteName);
						for (RepositoryItem repositoryItem : childSkus) {
							 if(null != repositoryItem.getPropertyValue(customizationOfferedFlag) ){
						        	isCustomizationOffered = ((Boolean) repositoryItem.getPropertyValue(customizationOfferedFlag)).booleanValue();
						        	if(isCustomizationOffered){
						        		break;
						        	}
							} 
						}
					}
					
					feedVO.addFeedDataMap(feedHeaders.get(9), isCustomizationOffered?"Y":"N");
					feedVOList.add(feedVO);
				} catch (Exception e) {
					MLOGGING.logError("Exception:"+ e);
					continue;
				}
			}
			getSiteContextManager().popSiteContext(context);
		} catch (SiteContextException e1) {
			MLOGGING.logError("SiteContextException occured in BBBMarketingFeedTools"
					+ "getOmnitureProductMarketingFeedVOList" +
					"while while pushing siteId for catalogId " + catalogId, e1);
		}

		//BBBH-1596 Updating Omniture Feed end
		MLOGGING.logInfo("BBBMarketingFeedTools [getOmnitureProductMarketingFeedVOList] end");
		return feedVOList;
	}
	
	

	/**
	 * @param isFullDataFeed
	 * @param lastModDate
	 * @param feedHeaders
	 * @param catalogId
	 * @param siteName
	 * @return MarketingFeedVO list
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<MarketingFeedVO> getOmnitureSkuMarketingFeedVOList(
									final boolean isFullDataFeed,final Timestamp lastModDate,
									final List<String> feedHeaders,final String catalogId,final String siteName) throws BBBSystemException, BBBBusinessException {
		
		MLOGGING.logInfo("BBBMarketingFeedTools [getOmnitureSkuMarketingFeedVOList] start");
		final List<MarketingFeedVO> feedVOList = new ArrayList<MarketingFeedVO>();
		final RepositoryItem[] skuItems = getCatalogItemsForFeedGeneration(isFullDataFeed,lastModDate, 
													BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
		if (skuItems == null ||
				skuItems.length == 0) {
			
			MLOGGING.logInfo("No data available in repository for Omniture Sku Marketing Feed");
			return null;
			//throw new BBBBusinessException(BBBCoreErrorConstants.FEED_ERROR_1008,BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
		}
		final List<RepositoryItem> skuList = new ArrayList<RepositoryItem>();
		skuList.addAll(Arrays.asList(skuItems));

		RepositoryItemWrapper skuWrapper = null;
		RepositoryItemWrapper productWrapper = null;
		RepositoryItem tempCatalogItem = null;
		MarketingFeedVO feedVO = null;

		String tempStr = null;
		//BBBH-1596 Updating Omniture Feed start
		Site site;
		try {
			site = getSiteContextManager().getSite(siteName);
			final SiteContextImpl context = new SiteContextImpl(getSiteContextManager(),site);
			getSiteContextManager().pushSiteContext(context);
			//Key, Sku Friendly Name, Accessory Flag, Color, Size, Finish, Sku Type, Main Level, Category, SubCategory, Brand
			for (RepositoryItem skuItem: skuList) {
				try {
					skuWrapper = new RepositoryItemWrapper(skuItem);			
					if(!isSkuWebOffered(skuItem, siteName)) {
						addProductsWithDisabledSkus("", skuItem.getRepositoryId());
						continue;
					}
					
					productWrapper = getProduct(skuWrapper, siteName);
					if(productWrapper == null) continue;
					
					feedVO = new MarketingFeedVO();
					// Setting Sku Attributes
					//Key
					feedVO.addFeedDataMap(feedHeaders.get(0), skuItem.getRepositoryId());			
					
					//Sku Friendly Name
					feedVO.addFeedDataMap(feedHeaders.get(1), getPlainText(skuWrapper.getString(BBBCatalogConstants.DISPLAY_NAME_SKU_PROPERTY_NAME)));
					
					//Accessory Flag
					feedVO.addFeedDataMap(feedHeaders.get(2), "");
					
					//Color
					tempStr = skuWrapper.getString(BBBCatalogConstants.COLOR_SURCHARGE_SKU_PROPERTY_NAME);
					feedVO.addFeedDataMap(feedHeaders.get(3), tempStr!=null?tempStr:"");
					
					//Size
					tempStr = skuWrapper.getString(BBBCatalogConstants.SIZE_SKU_PROPERTY_NAME);					
					feedVO.addFeedDataMap(feedHeaders.get(4), tempStr!=null?tempStr:"");
					
					//Finish
					feedVO.addFeedDataMap(feedHeaders.get(5), "");
					
					//Sku Type
					feedVO.addFeedDataMap(feedHeaders.get(6), "");
					
					//SubCategory
					//BBBH-1596 Updating omniture feed start
					tempCatalogItem = getParentCateogry(productWrapper.getOriginalItem(), catalogId);
					if (tempCatalogItem == null) {
						final RepositoryItem parentProductItem = getParentProductItem(productWrapper.getOriginalItem().getRepositoryId(), siteName);
						tempCatalogItem = getParentCateogry(parentProductItem, catalogId);
					}
					String subCategory = null;
					String subCategoryId = null;
					if(tempCatalogItem != null){
						subCategory  =(String)tempCatalogItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME);
						subCategoryId = tempCatalogItem.getRepositoryId();
					}
					final StringBuilder subCategorySB = new StringBuilder();
					
					//Category
					tempCatalogItem = getParentCateogry(tempCatalogItem, catalogId);
					String category=null;
					String categoryId = null;
					if(tempCatalogItem != null){
						category  =(String)tempCatalogItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME);
						categoryId = tempCatalogItem.getRepositoryId();
						
					}
					final StringBuilder categorySB = new StringBuilder();
					
					//Main Level
					tempCatalogItem = getParentCateogry(tempCatalogItem, catalogId);
					String main = null;
					String mainId = null;
					if((tempCatalogItem != null)){
						main = (String)tempCatalogItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME);
						mainId = tempCatalogItem.getRepositoryId();
					}
					final StringBuilder mainSB = new StringBuilder();
					//When product is tagged to L1 directly
					if(BBBUtility.compareStringsIgnoreCase(category, BBBCatalogConstants.COMMERCE_ROOT)) {
						subCategory = BBBUtility.isEmpty(subCategory) ? ""
								: subCategorySB.append(subCategory)
										.append(BBBCoreConstants.SEMICOLON)
										.append(subCategoryId).toString();
						//Setting L1 in Main header and blank strings in Category and Sub Category headers.
						feedVO.addFeedDataMap(feedHeaders.get(7), subCategory);
						feedVO.addFeedDataMap(feedHeaders.get(8), BBBCatalogConstants.CATEGORY_ID_BLANK);
						feedVO.addFeedDataMap(feedHeaders.get(9), BBBCatalogConstants.CATEGORY_ID_BLANK);
					} 
					//When product is tagged to L2 directly
					else if ((tempCatalogItem != null)
							&& ((String) tempCatalogItem
									.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME))
									.equalsIgnoreCase(BBBCatalogConstants.COMMERCE_ROOT)) {
						subCategory = (BBBUtility.isEmpty(category) && BBBUtility.isEmpty(subCategory)) ? ""
								: subCategorySB
								.append(category)
								.append(BBBCoreConstants.GREATER_THAN_SYMBOL)
								.append(subCategory)
								.append(BBBCoreConstants.SEMICOLON)
								.append(subCategoryId).toString();
						category = BBBUtility.isEmpty(category) ? "" : categorySB
								.append(category)
								.append(BBBCoreConstants.SEMICOLON)
								.append(categoryId).toString();
						//Setting L1 in Main Header and L2 in Category header and blank string in Sub Category header.
						feedVO.addFeedDataMap(feedHeaders.get(7), category);
						feedVO.addFeedDataMap(feedHeaders.get(8), subCategory);
						feedVO.addFeedDataMap(feedHeaders.get(9), BBBCatalogConstants.CATEGORY_ID_BLANK);
					}
					//When product is not tagged to any categories or to L3
					else {
						subCategory = (BBBUtility.isEmpty(main) && BBBUtility.isEmpty(category) && BBBUtility.isEmpty(subCategory)) ? ""
								: subCategorySB
								.append(main)
								.append(BBBCoreConstants.GREATER_THAN_SYMBOL)
								.append(category)
								.append(BBBCoreConstants.GREATER_THAN_SYMBOL)
								.append(subCategory)
								.append(BBBCoreConstants.SEMICOLON)
								.append(subCategoryId).toString();
						category = (BBBUtility.isEmpty(main) && BBBUtility.isEmpty(category)) ? ""
								: categorySB
								.append(main)
								.append(BBBCoreConstants.GREATER_THAN_SYMBOL)
								.append(category)
								.append(BBBCoreConstants.SEMICOLON)
								.append(categoryId).toString();
						main = BBBUtility.isEmpty(main) ? "" : mainSB.append(main)
								.append(BBBCoreConstants.SEMICOLON).append(mainId)
								.toString();
						feedVO.addFeedDataMap(feedHeaders.get(7), main);
						feedVO.addFeedDataMap(feedHeaders.get(8), category);
						feedVO.addFeedDataMap(feedHeaders.get(9), subCategory);
					}
					//BBBH-1596 Updating omniture feed end
					
					//Brand
					feedVO.addFeedDataMap(feedHeaders.get(10), getProductBrandName(productWrapper));
					feedVOList.add(feedVO);
					
					
					//VDC
					tempStr = skuWrapper.getString(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME);
					feedVO.addFeedDataMap(feedHeaders.get(11), tempStr!=null? YES: NO);
					
					//LTL
					Boolean isLtl = (Boolean) skuItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU);
					feedVO.addFeedDataMap(feedHeaders.get(12), isLtl==null||(!isLtl)? NO: YES);
					
					//Personalized
					String customizationOfferedFlag = this.getCustomizationOfferedSiteMap().get(siteName);
					Boolean isCustomizationOffered= false;
			        if(null != skuItem.getPropertyValue(customizationOfferedFlag) ){
			        	isCustomizationOffered = ((Boolean) skuItem.getPropertyValue(customizationOfferedFlag)).booleanValue();
			        }
					feedVO.addFeedDataMap(feedHeaders.get(13), isCustomizationOffered?"Y":"N");
				} catch (Exception e) {
					MLOGGING.logError(e.getMessage());
					continue;
				}				
			}
			getSiteContextManager().popSiteContext(context);
		} catch (SiteContextException e1) {
			MLOGGING.logError("SiteContextException occured in BBBMarketingFeedTools"
					+ "getOmnitureSkuMarketingFeedVOList" +
					"while while pushing siteId for catalogId " + catalogId, e1);
		}

		//BBBH-1596 Updating Omniture Feed end
		MLOGGING.logInfo("BBBMarketingFeedTools [getOmnitureSkuMarketingFeedVOList] end");
		return feedVOList;
	}
	
	/**
	 * This method gets parent collection product for the child product from ObjectCache
	 * @param productId child product id
	 * @param siteId
	 * @return
	 */
	public RepositoryItem getParentProductItem(String productId, String siteId) {
		MLOGGING.logDebug("BBBMarketingFeedTools [getParentProduct] start");
		String collectionChildRelnCacheName = BBBConfigRepoUtils.getStringValue(OBJECT_CACHE_CONFIG_KEY, COLLECTION_CHILD_RELN_CACHE_NAME);
		String cacheKey = new StringBuilder(productId).append(BBBCoreConstants.UNDERSCORE).append(siteId).toString();
		String collectionParentProductId = (String) getObjectCache().get(cacheKey, collectionChildRelnCacheName);
		if (collectionParentProductId == null) {
			return null;
		}
		RepositoryItem parentProductItem = null;
		try {
			parentProductItem = this.getCatalogRepository().getItem(collectionParentProductId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		} catch (RepositoryException exception) {
			MLOGGING.logError("Error while getting product repository item for productId : + " + collectionParentProductId, exception);
		}
		MLOGGING.logDebug("BBBMarketingFeedTools [getParentProduct] end");
		return parentProductItem;
	}
	
	/**
	 * This method will check the code size, specified in the size parameter.
	 * @param size
	 * @param upcCode
	 * @return
	 */
	public String checkCodeSizeAndRectify(int size, String code) {
		
		MLOGGING.logDebug("checkCodeSizeAndRectify start size :: "+size +"code :: " + code );
				
		if (!BBBUtility.isEmpty(code)) {
			
			StringBuilder sb = new StringBuilder(code);
			
			int originalLength = code.length();
			
			MLOGGING.logDebug("originalLength :: "+originalLength);

			if (originalLength < size) {
				// append 0's with upcCode to make it equal to size

				do {
					
					// keep appending 0s with upcCode, use string buffer instead of appending
					//code = "0" + code
					sb.insert(0, "0");
					originalLength = sb.length();
					code=sb.toString();

				} while (originalLength < size);

				// After this operation all upcCode will be equal to size.

			}else{
				//length is greater than 13, which is not allowed
				MLOGGING.logDebug("Length is greater than " + size +" checkCodeSizeAndRectify Ends code :: " + code);
				return code;
			}
		}

		MLOGGING.logDebug("CheckCodeSizeAndRectify Ends final value of code :: " + code);
		return code;
	}
	
	/**
	 * This method will add the checksum to the entered code, checksum will be added to the end.
	 * Method for calculating checksum :
	 * 1. Multiply each digit of entered code with alternative 3 and 1 in an alternative sequence.
	 * 2. Add all numbers obtained as a result of the above operation.
	 * 3. Subtract the number from a higher or equal multiple of 10.
	 * 4. Add the number obtained in step 3 above to the entered code.
	 * 5. At the end of the operation length of code will be code.length() + 1.
	 * @param code
	 * @return
	 */
	public String addCheckSum(String code) {

		MLOGGING.logDebug("addCheckSum start code :: " + code);
		
		if (!BBBUtility.isEmpty(code)) {
			
			//Array for initial code
			int[] integers = new int[code.length()];
			//Array for post multiplication data
			int multipliedValue = 0;
			int sumMultipliedValues = 0;

			
			for (int i = 0; i < code.length(); i++) {

				//0 is subtracted, or else we would get the ascii values.
				integers[i] = code.charAt(i) - '0';
				MLOGGING.logDebug("integers array values:: " + integers[i]);

				if (i % 2 == 0) {
					// even place multiply by 3, starting from 0
					multipliedValue = integers[i] * 3;
				} else {
					// odd place multiply by 1, starting from 1
					multipliedValue = integers[i] * 1;
				}

				sumMultipliedValues += multipliedValue;

			}
			
			MLOGGING.logDebug("sumMultipliedValues:: " + sumMultipliedValues);
			
			//get the nearest integer
			int nearestInteger = 10 * ((sumMultipliedValues + 9) / 10);
			MLOGGING.logDebug("nearestInteger:: " + nearestInteger);

			//Calculate the checkdigit
			int checkdigit = 0;
			checkdigit = nearestInteger - sumMultipliedValues;
			MLOGGING.logDebug("numInt :: " + sumMultipliedValues + "checkdigit:: " + checkdigit);

			code = code + checkdigit;
		}

		MLOGGING.logDebug("addCheckSum Ends Final value of Code :: " + code);
		return code;

	}
	
	/**
	 * @return storeRepository
	 */
	public Repository getStoreRepository() {
		return this.storeRepository;
	}

	/**
	 * @param storeRepository
	 */
	public void setStoreRepository(final Repository storeRepository) {
		this.storeRepository = storeRepository;
	}

	/**
	 * @return shippingMethodMap
	 */
	public Map<String, String> getShippingMethodMap() {
		return this.shippingMethodMap;
	}

	/**
	 * @param shippingMethodMap
	 */
	public void setShippingMethodMap(final Map<String, String> shippingMethodMap) {
		this.shippingMethodMap = shippingMethodMap;
	}

	/**
	 * @return the objectCache
	 */
	public BBBObjectCache getObjectCache() {
		return objectCache;
	}

	/**
	 * @param objectCache the objectCache to set
	 */
	public void setObjectCache(BBBObjectCache objectCache) {
		this.objectCache = objectCache;
	}
	
	public Map<String, String> getUnits() {
		return units;
	}

	public void setUnits(Map<String, String> units) {
		this.units = units;
	}

}
