package com.bbb.rest.catalog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;

import org.dozer.DozerBeanMapper;
import org.springframework.web.util.HtmlUtils;

import com.bbb.account.BBBProfileTools;
import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.CollectionProductVO;
import com.bbb.commerce.catalog.vo.PDPAttributesVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.RecommendedCategoryVO;
import com.bbb.commerce.catalog.vo.RollupTypeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.checklist.tools.CheckListTools;
import com.bbb.commerce.exim.bean.EximImagePreviewVO;
import com.bbb.commerce.exim.bean.EximKatoriResponseVO;
import com.bbb.commerce.exim.bean.EximSessionBean;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.porch.service.PorchServiceManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.rest.catalog.vo.BulkProductVO;
import com.bbb.rest.catalog.vo.CatalogItemAttributesVO;
import com.bbb.rest.catalog.vo.CollegeDetailVO;
import com.bbb.rest.catalog.vo.KatoriPriceRestVO;
import com.bbb.rest.catalog.vo.PrdRelationRollupVO;
import com.bbb.rest.catalog.vo.ProductCategoryRestVO;
import com.bbb.rest.catalog.vo.ProductMobileVO;
import com.bbb.rest.catalog.vo.ProductPLPVO;
import com.bbb.rest.catalog.vo.ProductRestVO;
import com.bbb.rest.catalog.vo.ProductRollupVO;
import com.bbb.rest.catalog.vo.SkuRestVO;
import com.bbb.rest.output.BBBRestDozerBeanProvider;
import com.bbb.rest.util.ProductAttributeComparator;
import com.bbb.selfservice.vo.SchoolVO;
import com.bbb.utils.BBBUtility;
import com.bbb.vo.wishlist.LTLWishListDslVO;
import com.bbb.wishlist.GiftListVO;

import atg.core.util.StringUtils;
import atg.droplet.TagConversionException;
import atg.droplet.TagConverterManager;
import atg.multisite.SiteContextManager;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import net.sf.json.JSONObject;

/**
 * @author rsain4
 *
 */
public class BBBRestCatalogToolImpl extends BBBGenericService {
	private static final String REPOSITORY_EXCEPTION = "Repository Exception";
	private static final String SKU_NOT_PRESENT_IN_THE_CATALOG = "Sku Not present in the catalog";
	private static final String FINISH = "FINISH";
	private static final String SIZE3 = "SIZE";
	private static final String COLOR3 = "COLOR";
	private static final String FINISH_SIZE = "FINISH,SIZE";
	private static final String SIZE_FINISH = "SIZE,FINISH";
	private static final String COLOR_SIZE = "COLOR,SIZE";
	private static final String SIZE_COLOR = "SIZE,COLOR";
	private static final String SIZE2 = "size";
	private static final String COLOR2 = "color";
	private static final String NONE = "NONE";
	private static final String ROLLUP_ATTRIBUTE = "rollupAttribute";
	private static final String PROD_ROLLUP_TYPE = "prodRollupType";
	private static final String DEFAULT_CHANNEL_VALUE="Web2.0";
	private static final String CHANNEL_ID = "clientID";
	private static final String STORE_ID = "storeID";

	private BBBCatalogTools catalogTools = null;
	private MutableRepository catalogRepository;
	private BBBInventoryManager bbbInventoryMgr = null;
	private String promoItemProperties;
	private BBBObjectCache mObjectCache;
	private BBBRestDozerBeanProvider dozerBean;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private static final String  module = BBBRestCatalogToolImpl.class.getName();
    private BBBSavedItemsSessionBean savedItemsBean;
    private String userProfile;
    private BBBProfileTools mTools;   
    private PorchServiceManager porchServiceManager;
    /**
	 * @return mTools
	 */
	public BBBProfileTools getTools() {
		return mTools;
	}

	/**
	 * @param pTools
	 */
	public void setTools(BBBProfileTools pTools) {
		mTools = pTools;
	}

	/**
	 * @return the mObjectCache
	 */
	public BBBObjectCache getObjectCache() {
		return this.mObjectCache;
	}

	/**
	 * @param pObjectCache 
	 */
	public void setObjectCache(final BBBObjectCache pObjectCache) {
		this.mObjectCache = pObjectCache;
	}

	/**
	 * @return promoItemProperties
	 */
	public String getPromoItemProperties() {
		return this.promoItemProperties;
	}

	/**
	 * @param promoItemProperties
	 */
	public void setPromoItemProperties(final String promoItemProperties) {
		this.promoItemProperties = promoItemProperties;
	}


	/**
	 * @return dozerBean
	 */
	public BBBRestDozerBeanProvider getDozerBean() {
		return this.dozerBean;
	}

	/**
	 * @param dozerBean
	 */
	public void setDozerBean(final BBBRestDozerBeanProvider dozerBean) {
		this.dozerBean = dozerBean;
	}

	/**
	 * @return catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return this.catalogRepository;
	}

	/**
	 * @param catalogRepository
	 */
	public void setCatalogRepository(final MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	/**
	 * @return catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return bbbInventoryMgr
	 */
	public BBBInventoryManager getBbbInventoryMgr() {
		return this.bbbInventoryMgr;
	}

	/**
	 * @param bbbInventoryMgr
	 */
	public void setBbbInventoryMgr(final BBBInventoryManager bbbInventoryMgr) {
		this.bbbInventoryMgr = bbbInventoryMgr;
	}

	private BBBEximManager eximManager;

	public BBBEximManager getEximManager() {
		return eximManager;
	}

	public void setEximManager(BBBEximManager eximManager) {
		this.eximManager = eximManager;
	}

	/**
	 * @param upcCode
	 * @param longitude
	 * @param latitude
	 * @return productMobileVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws ServiceException
	 */
	public ProductMobileVO getProductDetailsByUPC(final String upcCode,final String longitude,final String latitude) throws BBBBusinessException,BBBSystemException,ServiceException{

		if(isLoggingDebug()){
			logDebug("Inside class: BBBRestCatalogToolImpl,  method :getProductDetailsByUPC");
		}
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.CATALOG_API_CALL+" getProductDetailsByUPC");

		
		String productID;
		String skuId;
		String seoUrl;
		String pdpSeoUrl;
		String productTitle;
		double listPrice;
		double salePrice;
		double currentPrice;
		List<String> scene7urlList;
		String scene7url;
		boolean webOfferedFlag;
		boolean  webOffered= false;
		
		RepositoryItem productRepositoryItem= null;
		ProductMobileVO productMobileVO = new ProductMobileVO();

		if(upcCode ==null || upcCode.trim().equals("")){
			productMobileVO.setErrorExist(true);
			productMobileVO.setErrorMessage(BBBCatalogConstants.INVALID_UPC);
			productMobileVO.setErrorCode(BBBCatalogErrorCodes.INVALID_INPUT_PROVIDED);
			if(isLoggingDebug()){
				logDebug(productMobileVO.getErrorMessage());
			}
			return productMobileVO;
			//throw new BBBBusinessException (BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL, "UPC is NULL");
		}
		try{
			skuId= getCatalogTools().getSkuByUPC(upcCode);
			String siteID = SiteContextManager.getCurrentSiteId();
			if(null != skuId && skuId.equals(BBBCoreConstants.SKU_NOT_AVAILABLE)){
				if(isLoggingDebug()){
					logDebug("sku not present in Repository");
				    logDebug("sku not present in Repository");
				}
				java.util.Date today = new java.util.Date();
				java.sql.Timestamp dateTimeStamp=new java.sql.Timestamp(today.getTime());
				String time = dateTimeStamp.toString();
				String channel = null;
				String storeID = null;


				DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
				if(request != null)
				{
					channel = ServletUtil.getCurrentRequest().getHeader(CHANNEL_ID);
					storeID = ServletUtil.getCurrentRequest().getHeader(STORE_ID);
					if (channel == null)
						channel=DEFAULT_CHANNEL_VALUE;
					if (storeID == null)
						storeID="123";
				}
				else

				{
					channel=DEFAULT_CHANNEL_VALUE;
					storeID="123";
				}

				logPersistedInfo("sku_scan", upcCode, longitude, latitude, siteID, channel, storeID,time);
				productMobileVO.setErrorExist(true);
				productMobileVO.setErrorMessage(BBBCatalogConstants.INVALID_SCANNED_SKU);
				productMobileVO.setErrorCode(BBBCatalogErrorCodes.DATA_NOT_FOUND);
				if(isLoggingDebug()){
					logDebug(productMobileVO.getErrorMessage());
				}
				return productMobileVO;
				//throw new BBBBusinessException (BBBCatalogErrorCodes.SCANNED_SKU_NOT_IN_THE_SYSTEM,"SCANNED_SKU_NOT_PRESENT_IN_THE_SYSTEM");
			}

			if(null != skuId && !skuId.isEmpty()){
				productID= getCatalogTools().getParentProductForSku(skuId);

				if (productID!=null&&!productID.isEmpty())
				{
					RepositoryItem skuRepositoryItem=this.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
					productRepositoryItem=this.getCatalogRepository().getItem(productID, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
					seoUrl=	(String)productRepositoryItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME);
					boolean webOfferedProduct = false;
					boolean webOfferedSku = false;
			        if (productRepositoryItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) != null) {
			        	webOfferedProduct = ((Boolean) productRepositoryItem
			                            .getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME)).booleanValue();
			        }
			        if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) != null) {
			        	webOfferedSku = ((Boolean) skuRepositoryItem
		                                .getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)).booleanValue();
			        }

					pdpSeoUrl = seoUrl + "?skuId=" + skuId;
					
					if(null != skuRepositoryItem) {
						productMobileVO.setCustomizationOffered(
								getCatalogTools().isCustomizationOfferedForSKU(skuRepositoryItem, siteID));
						productMobileVO.setCustomizableRequired(
								getCatalogTools().isCustomizationRequiredForSKU(skuRepositoryItem, siteID));
					}
					
					boolean active = getCatalogTools().isSkuActive(skuRepositoryItem);

					if (active
							&& getCatalogTools().isProductActive(
									productRepositoryItem) && webOfferedProduct && webOfferedSku) {					
						productMobileVO.setProductId(productID);
						productMobileVO.setChildSku(skuId);
						productMobileVO.setDisabled(false);
						
					}
					else {
						productMobileVO = getMobileProductDetails(
								skuRepositoryItem, skuId);
						productMobileVO.setProductId(productID);
						productMobileVO.setDisabled(!active);
						productMobileVO.setChildSku(skuId);
					}
					if(active){
						int inventoryStatus = this.getBbbInventoryMgr().getProductAvailability(siteID, skuId, BBBInventoryManager.PRODUCT_DISPLAY, BBBInventoryManager.AVAILABLE);
						if(inventoryStatus == BBBInventoryManager.AVAILABLE || inventoryStatus==BBBInventoryManager.LIMITED_STOCK ){
							productMobileVO.setInStock(true);
						}else{
							productMobileVO.setInStock(false);
						}
					}
					String intlRestricted = (String)skuRepositoryItem.getPropertyValue(BBBCatalogConstants.INTERNATIONAL_RESTRICTED);
					if(!BBBUtility.isEmpty(intlRestricted)){
						if(intlRestricted.equalsIgnoreCase(BBBCoreConstants.YES_CHAR)){
							productMobileVO.setIntlRestricted(true);
						}else{
							productMobileVO.setIntlRestricted(false);
						}
					}
						
					if(webOfferedProduct && (null != seoUrl)){
						productMobileVO.setPdpSeoUrl(pdpSeoUrl);
					}
					if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_PROPERTY_NAME)!=null){
						String imageURL = (String)productRepositoryItem.getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_PROPERTY_NAME);
						if(isLoggingDebug()){
							logDebug("ThirdPArtyImageURLKey" +imageURL);
						}
						scene7urlList = getCatalogTools().getAllValuesForKey(BBBCoreConstants.THIRD_PARTY_URL,
								BBBCoreConstants.SCENE7_URL);
						
						if(isLoggingDebug()){
							logDebug("scene7urlList" +scene7urlList);
						}
						
						if(null != scene7urlList  && !scene7urlList.isEmpty()){
							scene7url = scene7urlList.get(0);
							if(isLoggingDebug()){
								logDebug("scene7url" +scene7url);
							   logDebug("CompleteImageURL" +scene7url+ BBBCoreConstants.SLASH + imageURL);
							}
							productMobileVO.setImageURL(scene7url+ BBBCoreConstants.SLASH + imageURL);
							
						}
					}
					if( productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME)!=null){
						productTitle=(String)productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME);
						if(isLoggingDebug()){
							logDebug("productTitle" +productTitle);
						}
						productMobileVO.setProductTitle(productTitle);
					}
					
					//WebOfferedFlag from product
					if (productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) != null) {
						webOfferedFlag = ((Boolean) productRepositoryItem
								.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)).booleanValue();
						
						if(isLoggingDebug()){
							logDebug("webOfferedFlag" +webOfferedFlag);
						}
						
						productMobileVO.setWebOfferedFlag(webOfferedFlag);
					}
					
					// setting current price
					listPrice=getCatalogTools().getListPrice(productID, skuId).doubleValue();
					salePrice=getCatalogTools().getSalePrice(productID, skuId).doubleValue();
					if (salePrice > 0) {
						currentPrice = salePrice;
					} else {
						currentPrice = listPrice;
					}
					if(isLoggingDebug()){
						logDebug("currentPrice" +currentPrice);
					}
					productMobileVO.setCurrentPrice(currentPrice);
					//BSL-4850 Add Part of collection properties to getProductDetails Start
					String collectionParentProductId = getCatalogTools().getParentProductId(productID, siteID);
					ProductVO parentProductVO = null;
					if(BBBUtility.isNotBlank(collectionParentProductId))
					{
						final boolean isEverLivingCollection = getCatalogTools().isEverlivingProduct(collectionParentProductId,siteID);
						if(isEverLivingCollection){
							parentProductVO = getCatalogTools().getEverLivingProductDetails(siteID, collectionParentProductId,true, false, true);
						}else{
							parentProductVO = getCatalogTools().getProductDetails(siteID,collectionParentProductId);
						}
						if(null!=parentProductVO)
						{
							productMobileVO.setPoc(true);
							productMobileVO.setParentProductId(parentProductVO.getProductId());
							productMobileVO.setParentImage(parentProductVO.getProductImages());
							productMobileVO.setParentProductName(parentProductVO.getName());
						}
					}
					//BSL-4850 Add Part of collection properties to getProductDetails End
					
				}
				else{
					productMobileVO.setErrorExist(true);
					productMobileVO.setErrorMessage(BBBCatalogConstants.ERROR_FETCHING_PRODUCT_ID);
					productMobileVO.setErrorCode(BBBCatalogErrorCodes.DATA_NOT_FOUND);
					if(isLoggingDebug()){
						logDebug(productMobileVO.getErrorMessage());
					}
					return productMobileVO;
					//throw new BBBSystemException ("ERROR_FETCHING_PRODUCT_ID ","ERROR_FETCHING_PRODUCT_ID");
				}
			}

		}

		catch(RepositoryException e){
			 productMobileVO.setErrorExist(true);
			 productMobileVO.setErrorMessage(BBBCatalogConstants.REPOSITORY_EXCEPTION);
			 productMobileVO.setErrorCode(BBBCatalogErrorCodes.NOT_ABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY);
			if(isLoggingDebug()){
				logDebug("Exception in getProductDetailsbyUPC" +e.getMessage());	
			}
			//throw new BBBSystemException(REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
		}
		 catch (final BBBBusinessException e) {
			 productMobileVO.setErrorExist(true);
			 productMobileVO.setErrorMessage(e.getMessage());
			 productMobileVO.setErrorCode(BBBCatalogErrorCodes.DATA_NOT_FOUND);
	        }
	     catch (final BBBSystemException e) {
	        	productMobileVO.setErrorExist(true);
	        	productMobileVO.setErrorMessage(e.getMessage());
	        	productMobileVO.setErrorCode(BBBCatalogErrorCodes.NOT_ABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY);
	        }
		return productMobileVO;
	}
	
	/**
	 * @param listOfupcCode
	 * @param longitude
	 * @param latitude
	 * @return productDetailsMap
	 * @throws BBBBusinessException
	 */
	public BulkProductVO getBulkProductDetailsbyMultipleUPC(
			String listOfupcCode, final String longitude, final String latitude) 
			throws BBBBusinessException {
		BulkProductVO bulkProductVO = new BulkProductVO();
		HashMap<String, ProductMobileVO> productDetailsMap = new HashMap<String, ProductMobileVO>();
		ProductMobileVO productMobileVO = new ProductMobileVO();

		if(isLoggingDebug()){
			logDebug("Inside class: BBBRestCatalogToolImpl,  method :getBulkProductDetailsbyMultipleUPC");
		}
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
				+ " getBulkProductDetailsbyMultipleUPC");
	

		if (BBBUtility.isEmpty(listOfupcCode)
				|| listOfupcCode.equalsIgnoreCase("")) {
			logError("INPUT_PARAMETER_IS_NULL: UPC List is NULL");
			bulkProductVO.setErrorExist(true);
			bulkProductVO.setErrorMessage(BBBCatalogConstants.INVALID_UPC_LIST);
            bulkProductVO.setErrorCode(BBBCatalogErrorCodes.INVALID_INPUT_PROVIDED);
			if(isLoggingDebug()){
				logDebug(bulkProductVO.getErrorMessage());
			}
			return bulkProductVO;
		//	throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,"UPC List is NULL");
		}

		String[] arrayOfUPC = listOfupcCode.split(",");

		for (int upcArrayIndex = 0; upcArrayIndex < arrayOfUPC.length; upcArrayIndex++) {
			
	
			if (BBBUtility.isEmpty(arrayOfUPC[upcArrayIndex])) {
				
				logError("INPUT_PARAMETER_IS_NULL: UPC is Empty ");
				bulkProductVO.setErrorExist(true);
				bulkProductVO.setErrorMessage(BBBCatalogConstants.INVALID_UPC);
				 bulkProductVO.setErrorCode(BBBCatalogErrorCodes.INVALID_INPUT_PROVIDED);
				if(isLoggingDebug()){
					logDebug(bulkProductVO.getErrorMessage());
				}
				return bulkProductVO;
				//throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,"UPC is Empty");
			}

			String upcString = arrayOfUPC[upcArrayIndex].trim();

			if(isLoggingDebug()){
				logDebug("fetching product details for UPC [" + upcString + "]");
			}

			try {

				productMobileVO = getProductDetailsByUPC(upcString, longitude,
						latitude);
			} catch (BBBBusinessException e) {
				
				productMobileVO = new ProductMobileVO();
				productMobileVO.setErrorExist(true);
				productMobileVO.setErrorCode(BBBCatalogErrorCodes.DATA_NOT_FOUND);
				productMobileVO.setErrorMessage(e.getMessage());
				logError((e.getMessage()), e);

			} catch (ServiceException e) {
				
				productMobileVO = new ProductMobileVO();
				productMobileVO.setErrorExist(true);
				productMobileVO.setErrorCode(BBBCatalogErrorCodes.DATA_NOT_FOUND);
				productMobileVO.setErrorMessage(e.getMessage());
				logError((e.getMessage()), e);

			} catch (BBBSystemException e) {
				
				productMobileVO = new ProductMobileVO();
				productMobileVO.setErrorExist(true);
				productMobileVO.setErrorCode(BBBCatalogErrorCodes.NOT_ABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY);
				productMobileVO.setErrorMessage(e.getMessage());

				logError((e.getMessage()), e);

			}

			productDetailsMap.put(upcString, productMobileVO);
            bulkProductVO.setProductMobileVO(productDetailsMap);
		}

		// int indexPosition = Arrays.asList(arrayOfUPC).indexOf(i);

		if(isLoggingDebug()){ logDebug("ProductDetailHashMap in getBulkProductDetailsbyMultipleUPC"
				+ productDetailsMap);
		}
		return bulkProductVO;

	}


	/**
	 * method to fetch product details for the given sku-id and the repository item
	 * @param skuRepositoryItem 
	 * @param skuID 
	 * @param pSkuId : Id of the Sku whose details will be returned by this method
	 * @return productMobileVO
	 * @throws BBBBusinessException exception occurs in case any issue with input parameters
	 * @throws BBBSystemException exception occurs in case of unexpected error occurred

	 */


	public ProductMobileVO getMobileProductDetails(RepositoryItem skuRepositoryItem,String skuID) throws BBBSystemException, BBBBusinessException{

		double listPrice;
		double salePrice;
		if(skuRepositoryItem ==null){
			throw new BBBBusinessException (BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL, "skuRepositoryItem is NULL");
		}


		SKUDetailVO skuVO =new SKUDetailVO(skuRepositoryItem);
		ProductMobileVO productMobileVO = new ProductMobileVO();
		SkuRestVO skuRestVO = new SkuRestVO();

		productMobileVO.setChildSku(skuID);
		skuRestVO.setSkuVO(skuVO);
		try{
			listPrice = getCatalogTools().getListPrice(null,
					skuID).doubleValue();
			skuRestVO.setListPrice(listPrice);
		}catch(BBBSystemException e){
			logError("Error while fetching list price of Sku :" + e.getMessage());
			throw new BBBSystemException(BBBCatalogErrorCodes.err_fetch_Sku_list_price, "Error occurred while fetching List price of sku");
		}
		try{
			salePrice = getCatalogTools().getSalePrice(null,
					skuID).doubleValue();
			skuRestVO.setSalePrice(salePrice);
		}catch(BBBSystemException e){
			logError("Error while fetching sale price of Sku :" + e.getMessage());
			throw new BBBSystemException(BBBCatalogErrorCodes.err_fetch_Sku_Sale_price, "Error occurred while fetching Sale price of sku");
		}

		productMobileVO.setSkuRestVO(skuRestVO);
		return productMobileVO;


	}
	/**
	 * method to fetch Sku details for the given sku-id
	 * @param pSkuId : Id of the Sku whose details will be returned by this method
	 * @param calculateAboveBelowLine 
	 * @param includeStoreItems 
	 * @return skuRestVO
	 * @throws BBBBusinessException exception occurs in case any issue with input parameters
	 * @throws BBBSystemException exception occurs in case of unexpected error occurred

	 */
	public SkuRestVO getEverLivingSkuDetails(String pSkuId,boolean calculateAboveBelowLine,boolean includeStoreItems)
			throws BBBSystemException, BBBBusinessException {

		if(isLoggingDebug()){ logDebug("Inside class: BBBRestCatalogToolImpl,  method :getSkuRestDetails");
		logDebug("calculateAboveBelowLine : " +calculateAboveBelowLine);
		logDebug("SkuId : " +pSkuId);
		logDebug("includeStoreItems : " +includeStoreItems);
		}
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
				+ " getSkuRestDetails");
		String siteId = null;
		double listPrice;
		double salePrice;
		SkuRestVO skuRestVO = new SkuRestVO();
		SKUDetailVO skuVO = new SKUDetailVO();
		int inventoryStatus = 0;
		siteId = SiteContextManager.getCurrentSiteId();
		if(pSkuId ==null || pSkuId.trim().equals("")){
			throw new BBBBusinessException (BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL, "Sku Id is NULL");
		}

		try{	
			skuVO = getCatalogTools().getEverLivingSKUDetails(siteId, pSkuId, true);

			//Calling this method because REST OOB API does not support for Map having List as value,Converting map to list of object
			createSkuAttributes(skuRestVO, skuVO);
			skuRestVO.setSkuVO(skuVO);
		}catch(BBBSystemException e){
			logError("Error while fetching sku detail :" + e.getMessage());
			throw new BBBSystemException(BBBCatalogErrorCodes.err_fetch_sku_detail, "System Exception occurred while fetching sku detail");
		}catch(BBBBusinessException e){
			logError("Error while fetching sku detail :" + e.getMessage());
			throw new BBBSystemException(BBBCatalogErrorCodes.err_fetch_sku_detail, "Business Exception occurred while fetching sku detail");
		}
		try{
			listPrice = getCatalogTools().getListPrice(null,
					pSkuId).doubleValue();
			skuRestVO.setListPrice(listPrice);
		}catch(BBBSystemException e){
			logError("Error while fetching list price of Sku :" + e.getMessage());
			throw new BBBSystemException(BBBCatalogErrorCodes.err_fetch_Sku_list_price, "Business Exception occurred while fetching List price of sku");
		}
		try{
			salePrice = getCatalogTools().getSalePrice(null,
					pSkuId).doubleValue();
			skuRestVO.setSalePrice(salePrice);
		}catch(BBBSystemException e){
			logError("Error while fetching sale price of Sku :" + e.getMessage());
			throw new BBBSystemException(BBBCatalogErrorCodes.err_fetch_Sku_Sale_price, "Business Exception occurred while fetching Sale price of sku");
		}
		try{
			inventoryStatus = getBbbInventoryMgr().getProductAvailability(siteId, pSkuId,
					BBBInventoryManager.PRODUCT_DISPLAY, 0);
			skuRestVO.setInventoryStatus(inventoryStatus);
		} catch (BBBSystemException e) {
			logError("Error while fetching Inventory status of Sku :" + e.getMessage());
			//throw new BBBSystemException(BBBCatalogErrorCodes.err_fetch_Sku_Inventory, "Business Exception occurred while fetching Inventory status of Sku");
			skuRestVO.setInventoryStatus(BBBInventoryManager.NOT_AVAILABLE);
		}catch (BBBBusinessException e) {
			logError("Error while fetching Inventory status of Sku :" + e.getMessage());
			//throw new BBBBusinessException(BBBCatalogErrorCodes.err_fetch_Sku_Inventory, "Business Exception occurred while fetching Inventory status of Sku");
			skuRestVO.setInventoryStatus(BBBInventoryManager.NOT_AVAILABLE);
		}
		finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
					+ " getSkuRestDetails");
		}
		if(isLoggingDebug()){
			logDebug("Exiting class: BBBRestCatalogToolImpl,  method :getSkuRestDetails");
		}
		return skuRestVO;
	}
	/*
	 * Method to get sku details for mobile as well as get information for filling heart icon on pdp page load if the item is in user's wishlist
	 * @param pSkuId : Id of the Sku whose details will be returned by this method
	 * @param calculateAboveBelowLine 
	 * @param includeStoreItems 
	 * @param pdpHeartIconFilled 
	 * @return skuRestVO
	 * @throws BBBBusinessException exception occurs in case any issue with input parameters
	 * @throws BBBSystemException exception occurs in case of unexpected error occurred
	 */
	public SkuRestVO getSkuDetailsForPDP(String pSkuId,boolean calculateAboveBelowLine,boolean includeStoreItems,boolean pdpHeartIconFilled)
			throws BBBSystemException, BBBBusinessException {
		if(isLoggingDebug()){
			logDebug("Inside getSkuDetailsForPDP...");
		}
		SkuRestVO skuRestVO = this.getSkuDetails(pSkuId,calculateAboveBelowLine,includeStoreItems);
		boolean isRefNoPresent = true;
		if(pdpHeartIconFilled){
			List<GiftListVO> giftListVO = null;
			LTLWishListDslVO ltlSavedDslOptions = null;
			List<String> dslOptionSaved = null;
			giftListVO = ((BBBSavedItemsSessionBean) ServletUtil.getCurrentRequest().resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean")).getSaveItems(true);				
			if(null != giftListVO){
				for(GiftListVO giftListVOItem:giftListVO){
					if(pSkuId.equalsIgnoreCase(giftListVOItem.getSkuID())){
						if(giftListVOItem.getReferenceNumber() == null)
						{
							isRefNoPresent = false;
						}


						//Setting DSL options for wishlist
						if(null != skuRestVO && null != skuRestVO.getSavedDsl() && null != skuRestVO.getSavedDsl().getSavedDslOptions() && 
								(skuRestVO.getSavedDsl().getSavedDslOptions().size() != 0) && skuRestVO.getSavedDsl().getSkuId().equalsIgnoreCase(pSkuId) && 
								giftListVOItem.getLtlShipMethodDesc()!= null && !StringUtils.isEmpty(giftListVOItem.getLtlShipMethodDesc())){
							skuRestVO.getSavedDsl().getSavedDslOptions().add(giftListVOItem.getLtlShipMethod());
						}
						else if(giftListVOItem.getLtlShipMethodDesc()!= null && !StringUtils.isEmpty(giftListVOItem.getLtlShipMethodDesc())){
							dslOptionSaved = new ArrayList<String>();
							dslOptionSaved.add(giftListVOItem.getLtlShipMethod());
							ltlSavedDslOptions = new LTLWishListDslVO();
							ltlSavedDslOptions.setSavedDslOptions(dslOptionSaved);
							ltlSavedDslOptions.setSkuId(pSkuId);
							skuRestVO.setSavedDsl(ltlSavedDslOptions);
						}
					}
				}
				
				if(!isRefNoPresent)
				{
					skuRestVO.setPdpHeartIconFilled(true);
				}
				else
				{
					skuRestVO.setPdpHeartIconFilled(false);
				}
			}
		}
		return skuRestVO;
	}
	
	/**
	 * method to fetch Sku details for the given sku-id
	 * @param pSkuId : Id of the Sku whose details will be returned by this method
	 * @param calculateAboveBelowLine 
	 * @param includeStoreItems 
	 * @return skuRestVO
	 * @throws BBBBusinessException exception occurs in case any issue with input parameters
	 * @throws BBBSystemException exception occurs in case of unexpected error occurred

	 */
	public SkuRestVO getSkuDetails(String pSkuId,boolean calculateAboveBelowLine,boolean includeStoreItems)
			throws BBBSystemException, BBBBusinessException {

		if(isLoggingDebug()){ logDebug("Inside class: BBBRestCatalogToolImpl,  method :getSkuRestDetails");
		logDebug("calculateAboveBelowLine : " +calculateAboveBelowLine);
		logDebug("SkuId : " +pSkuId);
		logDebug("includeStoreItems : " +includeStoreItems);
		}
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
				+ " getSkuRestDetails");
		String siteId = null;
		double listPrice;
		double salePrice;
		String formattedListPrice = null;
		String formattedSalePrice = null;
		SkuRestVO skuRestVO = new SkuRestVO();
		SKUDetailVO skuVO = new SKUDetailVO();
		int inventoryStatus = 0;
		siteId = SiteContextManager.getCurrentSiteId();
		String customizationCodes = null;
		
		
		boolean isError=	isErrorExist(pSkuId);
		if(isError)
		{
			skuRestVO.setErrorExist(true);
			skuRestVO.setErrorMsg(BBBCatalogConstants.INVALID_SKU_ID);
			if(isLoggingDebug()){
				logDebug(skuRestVO.getErrorMsg());
			}
			return(skuRestVO);
		}
	/*	if(pSkuId ==null || pSkuId.trim().equals("")){
			throw new BBBBusinessException (BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL, "Sku Id is NULL");
		} */
		try{	
			skuVO = getCatalogTools().getSKUDetails(siteId, pSkuId, calculateAboveBelowLine,true,includeStoreItems);
			//BPSI-2440  | Set the offset and VDC message
			if(skuVO!=null && !StringUtils.isEmpty(skuVO.getSkuId()) && skuVO.isVdcSku()){
				skuVO.setVdcOffsetMessage(getCatalogTools().getActualOffsetMessage(skuVO.getSkuId(), siteId));
			}
			//Calling this method because REST OOB API does not support for Map having List as value,Converting map to list of object
			createSkuAttributes(skuRestVO, skuVO);
			customizationCodes = getCatalogTools().getConfigValueByconfigType(BBBCoreConstants.EXIM_KEYS).get(BBBCoreConstants.CUSTOMIZATBLE_CTA_CODES);
			if (!BBBUtility.isEmpty(customizationCodes) && null!=skuVO && !BBBUtility.isEmpty(skuVO.getCustomizableCodes()) && customizationCodes.contains(skuVO.getCustomizableCodes())) {
				skuVO.setCustomizeCTAFlag(BBBCoreConstants.TRUE);
			}
			
			skuRestVO.setSkuVO(skuVO);
		}catch(BBBSystemException e){
			logError("Error while fetching sku detail :" + e.getMessage());
			skuRestVO.setErrorExist(true);
			skuRestVO.setErrorMsg(BBBCatalogConstants.SKU_NOT_PRESENT_IN_THE_CATALOG);
		}catch(BBBBusinessException e){
			logError("Error while fetching sku detail :" + e.getMessage());
			skuRestVO.setErrorExist(true);
			skuRestVO.setErrorMsg(BBBCatalogConstants.SKU_NOT_PRESENT_IN_THE_CATALOG);
		}
		
		if(skuRestVO.isErrorExist())
		{
			return(skuRestVO);
		}
		EximSessionBean eximSessionBean = getPersonalizedSkuFromSession(pSkuId);
		skuRestVO.setEximKatoriResponse(formEximKatoriResponse(skuRestVO, eximSessionBean));
		if(skuRestVO.getEximKatoriResponse()!=null && eximSessionBean!=null && eximSessionBean.getEximResponse()!=null){
			KatoriPriceRestVO price = getEximManager().getPriceByRefKatori(eximSessionBean.getRefnum(), eximSessionBean.getSkuId(), eximSessionBean.getSiteId(), eximSessionBean.getEximResponse(), null, false, false, null);
			if(price!=null && !price.isErrorExist()){
				skuRestVO.getEximKatoriResponse().setKatoriPriceVO(price);
			}
		}
		try{
			listPrice = getCatalogTools().getListPrice(null,
					pSkuId).doubleValue();
			skuRestVO.setListPrice(listPrice);
			try {
				formattedListPrice = TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY).convertObjectToString(ServletUtil.getCurrentRequest(), listPrice,new Properties()).toString();
			} catch (TagConversionException e) {
				logError("Error in getSkuDetails method " + e.getMessage());
			}			
			skuRestVO.setFormattedListPrice(formattedListPrice);
		}catch(BBBSystemException e){
			logError("Error while fetching list price of Sku :" + e.getMessage());
			skuRestVO.setErrorExist(true);
			skuRestVO.setErrorMsg(BBBCatalogConstants.SKU_NOT_PRESENT_IN_THE_CATALOG);
			return(skuRestVO);
		//	throw new BBBSystemException(BBBCatalogErrorCodes.err_fetch_Sku_list_price, "Business Exception occurred while fetching List price of sku");
		}
		try{
			salePrice = getCatalogTools().getSalePrice(null,
					pSkuId).doubleValue();
			skuRestVO.setSalePrice(salePrice);
			try {
				formattedSalePrice = TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY).convertObjectToString(ServletUtil.getCurrentRequest(), salePrice,new Properties()).toString();
			} catch (TagConversionException e) {
				logError("Error in getSkuDetails method " + e.getMessage());
			}
			skuRestVO.setFormattedSalePrice(formattedSalePrice);
		}catch(BBBSystemException e){
			logError("Error while fetching sale price of Sku :" + e.getMessage());
			skuRestVO.setErrorExist(true);
			skuRestVO.setErrorMsg(BBBCatalogConstants.SKU_NOT_PRESENT_IN_THE_CATALOG);
			return(skuRestVO);
		//	throw new BBBSystemException(BBBCatalogErrorCodes.err_fetch_Sku_Sale_price, "Business Exception occurred while fetching Sale price of sku");
		}
		try{
			inventoryStatus = getBbbInventoryMgr().getProductAvailability(siteId, pSkuId,
					BBBInventoryManager.PRODUCT_DISPLAY,0);
			skuRestVO.setInventoryStatus(inventoryStatus);
		} catch (BBBSystemException e) {
			logError("Error while fetching Inventory status of Sku :" + e.getMessage());
			//throw new BBBSystemException(BBBCatalogErrorCodes.err_fetch_Sku_Inventory, "Business Exception occurred while fetching Inventory status of Sku");
			skuRestVO.setInventoryStatus(BBBInventoryManager.NOT_AVAILABLE);
		}catch (BBBBusinessException e) {
			logError("Error while fetching Inventory status of Sku :" + e.getMessage());
			//throw new BBBBusinessException(BBBCatalogErrorCodes.err_fetch_Sku_Inventory, "Business Exception occurred while fetching Inventory status of Sku");
			skuRestVO.setInventoryStatus(BBBInventoryManager.NOT_AVAILABLE);
		}
		finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
					+ " getSkuRestDetails");
		}
		filterWarrantyInfoAttrs(skuRestVO);
		if(isLoggingDebug()){
			logDebug("Exiting class: BBBRestCatalogToolImpl,  method :getSkuRestDetails");
		}				
		return skuRestVO;
	}

	private void filterWarrantyInfoAttrs(SkuRestVO skuRestVO) {
	try {
		final Double warrantyPrice = Double.parseDouble(getCatalogTools().getAllValuesForKey("ContentCatalogKeys", "WarrantyPrice").get(0));
		final Boolean isWarrantyOn =    Boolean.parseBoolean(getCatalogTools().getAllValuesForKey("FlagDrivenFunctions", "WarrantyOn").get(0))  ;
		final String warrantyAttributeId=getLblTxtTemplateManager().getPageLabel("lbl_warranty_msg_atribute_id", ServletUtil.getCurrentRequest().getLocale().getLanguage(), null);
		BBBSessionBean sessionBean = (BBBSessionBean)ServletUtil.getCurrentRequest().resolveName("/com/bbb/profile/session/SessionBean");
		Boolean isInternationalUser =(Boolean) sessionBean.isInternationalShippingContext();
		final double listPrice=skuRestVO.getListPrice();
		final double salePrice=skuRestVO.getSalePrice();
		double applicablePrice=listPrice;
		if(salePrice > 0){
			applicablePrice=salePrice;
		}
		logDebug("warrantyPrice :" +warrantyPrice+",isWarrantyOn:"+isWarrantyOn+",warrantyAttributeId:"+warrantyAttributeId+",listPrice:"+listPrice+",salePrice:"+salePrice+",applicablePrice:"+applicablePrice+",isInternationalUser:"+isInternationalUser);
		 
		if( (  isInternationalUser && skuRestVO.getSkuAllAttributeVO() != null &&   skuRestVO.getSkuAllAttributeVO().getAttributeVOsList() != null) ||
			(  ! isWarrantyOn && skuRestVO.getSkuAllAttributeVO() != null &&   skuRestVO.getSkuAllAttributeVO().getAttributeVOsList() != null) ||
			( isWarrantyOn && applicablePrice < warrantyPrice   && skuRestVO.getSkuAllAttributeVO() != null &&skuRestVO.getSkuAllAttributeVO().getAttributeVOsList() != null))
		   { 
			List<AttributeVO> attributeVOList=new ArrayList<AttributeVO>();
				for(AttributeVO attributeVO :skuRestVO.getSkuAllAttributeVO().getAttributeVOsList())
				{
					if( ! attributeVO.getAttributeName() .equals(warrantyAttributeId)){
						  attributeVOList.add(attributeVO);
					 }
				}
			skuRestVO.getSkuAllAttributeVO().getAttributeVOsList().clear();
			skuRestVO.getSkuAllAttributeVO().getAttributeVOsList().addAll(attributeVOList);
		   }
		
		} catch (NumberFormatException e) {
			logError("filterWarrantyInfoAttrs:NumberFormatException");
		} catch (BBBSystemException e) {
			logError("filterWarrantyInfoAttrs :BBBSystemException");
		} catch (BBBBusinessException e) {
			logError("filterWarrantyInfoAttrs :BBBBusinessException");
		}catch (Exception e) {
		logError(":filterWarrantyInfoAttrs :Exception");
	    }
	}

	private EximSessionBean getPersonalizedSkuFromSession(String skuId){
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		BBBSessionBean sessionBean = (BBBSessionBean)request.resolveName(BBBCoreConstants.SESSION_BEAN);
		if(sessionBean.getPersonalizedSkus()!=null && !sessionBean.getPersonalizedSkus().isEmpty() && !BBBUtility.isEmpty(skuId)){
			return sessionBean.getPersonalizedSkus().get(skuId);
		}
		return null;
	}
	
	/**
	 *
	 * @param skuRestVO
	 * @param skuVO
	 */
	private void createSkuAttributes(SkuRestVO skuRestVO, SKUDetailVO skuVO) {

		if(isLoggingDebug()){
			logDebug("Entering class: BBBRestCatalogToolImpl,  method :createSkuAttributes");
		}

		CatalogItemAttributesVO productAllAttributesVO =createAttributesVO(skuVO.getSkuAttributes());
		skuRestVO.setSkuAllAttributeVO(productAllAttributesVO);
		
		// Below code is added to sort the attributeVOsList
		if(null != skuRestVO.getSkuAllAttributeVO()){			
			List<AttributeVO> attributeVOsList = skuRestVO.getSkuAllAttributeVO().getAttributeVOsList();
			if(null != attributeVOsList && attributeVOsList.size() > 1){				
				Collections.sort(attributeVOsList, new ProductAttributeComparator());				
				skuRestVO.getSkuAllAttributeVO().setAttributeVOsList(attributeVOsList);				
			}
		}		

		if(isLoggingDebug()){
			logDebug("Exiting class: BBBRestCatalogToolImpl,  method :createSkuAttributes");
		}

	}

	public  String escapeForW3C(String pStr){
		String escapedStr=HtmlUtils.htmlEscape(pStr);
		String spaceEscapedStr=escapedStr.replaceAll("\\s+","%20");

		return spaceEscapedStr;
	}
	
	public ProductRestVO getProductDetails(String pProductId) {
		return this.getProductDetails(pProductId, null, false);
	}
	
	public ProductRestVO getProductDetails(String productId, String colorParam, boolean minimalDetails) {
		return this.getProductColorDetails(productId, colorParam, minimalDetails);
	}
	
	/**
	 * @param pProductId
	 * @return productRestVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public ProductRestVO getProductColorDetails(String pProductId, String colorParam, boolean minimalDetails) {	

		
		if(isLoggingDebug()){
			logDebug("Inside class: BBBRestCatalogToolImpl,  method :getProductColorDetails");
		}
		
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.CATALOG_API_CALL+" getProductColorDetails");
		String siteId= null;
		ProductRestVO productRestVO = new ProductRestVO();
		ProductVO productVO = new ProductVO();
		SKUDetailVO skuVO = new SKUDetailVO();
		String pDefaultChildSKU = null;
		Map<String,String> skuRollupMap = new HashMap<String,String>();

		
		if(isLoggingDebug()){
			logDebug("Product Id :" +pProductId);
		}
		

		siteId = SiteContextManager.getCurrentSiteId();
		try {
			final boolean isEverLiving = getCatalogTools().isEverlivingProduct(pProductId,siteId);
			if(isEverLiving){
				productVO=getCatalogTools().getEverLivingProductDetails(siteId, pProductId,true, false, true);
			}else{
				productVO=getCatalogTools().getProductDetails(siteId, pProductId, minimalDetails);
			}

			try{
				productRestVO.setVendorJs(getCatalogTools().getVendorConfigurationForPDP(productVO.getProductId(), BBBCoreConstants.CHANNEL_MOBILE));
			} catch (BBBSystemException e) {
				logError("System exvception during fetching vendor configuration",e);
			} catch (BBBBusinessException e) {
				logError("Business exception during fetching vendor configuration",e);
			}
			
			
			//Calling this method because REST OOB API does not support for Map having List as value,Converting map to list of object
			createProductAttribute(productRestVO, productVO);

			//Calling this method because REST OOB API does not support for Map having List as value,Converting map to list of object
			createRollupAttributes(productRestVO, productVO);

			//Calling this method because REST OOB API does not support for Map having List as value,Converting map to list of object
			createPrdRelationRollup(productRestVO, productVO);
			
			// fetching proch service family details.
			
			getPorchServiceManager().getPorchServiceFamilyCodes(pProductId,productVO);		
			productRestVO.setProductVO(productVO);
			if(productVO!=null && !(StringUtils.isEmpty(productVO.getName()))){
				productRestVO.setEscapedPrdtName(this.escapeForW3C(productVO.getName()));
			}
			List<String>  lstChidSku =  new ArrayList<String>();

			final RepositoryItem productRepositoryItem=this.getCatalogRepository().getItem(
					pProductId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);

			boolean isCollection = false;
			boolean isLeadProduct = false;
			if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME)!=null)
			{
				isCollection= ((Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME)).booleanValue();
			}
			if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)!=null)
			{
				isLeadProduct= ((Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME)).booleanValue();
			}
			if(isLoggingDebug()){
				logDebug("Product is a Collection ["+isCollection+"] product is a lead product ["+isLeadProduct+"]");
			}
			
			if(!StringUtils.isEmpty(colorParam)){
				productRestVO.setSkuWithColorParam(new HashMap<String, Map<String,SkuRestVO>>());
				productRestVO.setSkuSwatchesWithColorParam(new HashMap<String, Map<String,String>>());
			}
			
			//check if product is collection or lead. If yes, Fetch all the child products and set skuRollUpMap for their child Skus
			if(isCollection || isLeadProduct)
			{
				CollectionProductVO collectionProductVO = (CollectionProductVO) productVO;
				if(isCollection){
					List<ProductVO> list = collectionProductVO.getChildProducts();
					if(list != null && list.size() > 0){
						for(ProductVO product : list){
							if(product != null){
								final RepositoryItem childProductRepositoryItem = this.getCatalogRepository().getItem(product.getProductId(), BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
								lstChidSku = product.getChildSKUs();
								if(lstChidSku != null && lstChidSku.size() >0){
									populateSkuRollUpMap(childProductRepositoryItem,productRestVO,lstChidSku,skuRollupMap,colorParam,product.getProductId());
								}
							}
						}
					}
				}
				if(isLeadProduct){
					lstChidSku = collectionProductVO.getChildSKUs();
					if(lstChidSku != null && lstChidSku.size() >0){
						populateSkuRollUpMap(productRepositoryItem,productRestVO,lstChidSku,skuRollupMap,colorParam,productVO.getProductId());
						//BSL-4850 Add Part of collection properties to getProductDetails Start
						getParentAttributes(productVO.getProductId(),productRestVO,siteId);
						//BSL-4850 Add Part of collection properties to getProductDetails End
						//BSL-5262 Mobile PDP not showing alt images Start
						if(lstChidSku.size()==1){
							pDefaultChildSKU = lstChidSku.get(0);
							skuVO = getCatalogTools().getSKUDetails(siteId, pDefaultChildSKU, false,true,false);
							productVO.setProductImages(skuVO.getSkuImages());
						}
						//BSL-5262 Mobile PDP not showing alt images End
					}
				}
			}
			else{
				lstChidSku =  productVO.getChildSKUs();
				if(lstChidSku != null && lstChidSku.size() >0){
					populateSkuRollUpMap(productRepositoryItem,productRestVO,lstChidSku,skuRollupMap,colorParam,productVO.getProductId());
					//BSL-4850 Add Part of collection properties to getProductDetails Start
					getParentAttributes(productVO.getProductId(),productRestVO,siteId);
					//BSL-4850 Add Part of collection properties to getProductDetails End
					//BSL-5262 Mobile PDP not showing alt images Start
					if(lstChidSku.size()==1){
						pDefaultChildSKU = lstChidSku.get(0);
						skuVO = getCatalogTools().getSKUDetails(siteId, pDefaultChildSKU, false,true,false);
						productVO.setProductImages(skuVO.getSkuImages());
					}
					//BSL-5262 Mobile PDP not showing alt images End
				}
			}
		} catch (BBBSystemException e1) {
			productRestVO.setErrorExist(true);
			productRestVO.setErrorCode(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
			logError("Exiting class: BBBRestCatalogToolImpl,  method :getProductRestDetails  Error Code = " + productRestVO.getErrorCode());

		} catch (BBBBusinessException e1) {
			productRestVO.setErrorExist(true);
			productRestVO.setErrorCode(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
			logError("Exiting class: BBBRestCatalogToolImpl,  method :getProductRestDetails  Error Code = " + productRestVO.getErrorCode());

		}catch (RepositoryException e) {
			productRestVO.setErrorExist(true);
			productRestVO.setErrorCode(BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
			logError("Exiting class: BBBRestCatalogToolImpl,  method :getProductRestDetails  Error Code = " + productRestVO.getErrorCode());

		}
		finally
		{
			BBBPerformanceMonitor.end(
					BBBPerformanceConstants.CATALOG_API_CALL+" getProductRestDetails");
		}
		
		if(isLoggingDebug()){
			logDebug("Exiting class: BBBRestCatalogToolImpl,  method :getProductRestDetails");
		}
		
		return productRestVO;
	}
	
	/**
	 * Gets the products and recommended categories.
	 *
	 * @param productId the product id
	 * @param categoryId the category id
	 * @return the products and recommended categories
	 */
	public ProductCategoryRestVO getProductsAndRecommendedCategories(String productId, String categoryId) {
		
		ProductCategoryRestVO productcategoryRestVO = new ProductCategoryRestVO();
		ProductRestVO productRestVO = new ProductRestVO();
		CollectionProductVO productVO = new CollectionProductVO();
		List<ProductVO> childProducts = null;
		List<RecommendedCategoryVO> recommendedCategoryList = null;
		try {
			childProducts = getCatalogTools().getProductDetailsWithSiblings(productId);
			if(childProducts!=null) {
				productVO.setChildProducts(childProducts);
			}
			if(BBBUtility.isNotEmpty(categoryId)) {
				recommendedCategoryList = getCatalogTools().getCategoryRecommendation(categoryId, productId);
			}
		} catch (BBBBusinessException | BBBSystemException e) {
			logError("BBBException in getProductsAndRecommendedCategories ", e);
		}
		productRestVO.setProductVO(productVO);
		productcategoryRestVO.setRecommendedCategoryList(recommendedCategoryList);
		productcategoryRestVO.setProductRestVO(productRestVO);
		
		return productcategoryRestVO;
	}
	
	/**
	 * @param productId
	 * @param productRestVO
	 * @param siteId
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void getParentAttributes(String productId,ProductRestVO productRestVO, String siteId) throws BBBSystemException, BBBBusinessException
	{
		String collectionParentProductId = getCatalogTools().getParentProductId(productId, siteId);
		ProductVO parentProductVO = null;
		if(BBBUtility.isNotBlank(collectionParentProductId))
		{
			final boolean isEverLivingCollection = getCatalogTools().isEverlivingProduct(collectionParentProductId,siteId);
			if(isEverLivingCollection){
				parentProductVO = getCatalogTools().getEverLivingProductDetails(siteId, collectionParentProductId,true, false, true);
			}else{
				parentProductVO = getCatalogTools().getProductDetails(siteId,collectionParentProductId);
			}
			if(null!=parentProductVO)
			{
				productRestVO.setPoc(true);
				productRestVO.setParentProductId(parentProductVO.getProductId());
				productRestVO.setParentImage(parentProductVO.getProductImages());
				productRestVO.setParentProductName(parentProductVO.getName());
			}
		}
	}
	
	/**
	 * @param pProductId
	 * @param pCategoryId
	 * @param pPoc
	 * @return pdpAttributesVo
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public final PDPAttributesVO getPDPAttributes(final String pProductId, final String pCategoryId, final String pPoc) throws BBBBusinessException, BBBSystemException {
		this.logDebug("Entering Rest API Method Name [getPDPAttributes]");
		String siteId= SiteContextManager.getCurrentSiteId();
		PDPAttributesVO pdpAttributesVo = getCatalogTools().PDPAttributes(pProductId, pCategoryId, pPoc, siteId);
		this.logDebug("Exiting Rest API Method Name [getPDPAttributes]");
		return pdpAttributesVo;
	}

	/**
	 * @param childProductRepositoryItem
	 * @param productRestVO
	 * @param lstChidSku
	 * @param skuRollupMap
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public void populateSkuRollUpMap(final RepositoryItem childProductRepositoryItem, final ProductRestVO productRestVO, final List<String> lstChidSku , final Map<String,String> skuRollupMap,final String  colorParam,final String productId) throws BBBSystemException, BBBBusinessException
	{
		String channelApp = null;
		if(ServletUtil.getCurrentRequest()!= null){
			 channelApp = ServletUtil.getCurrentRequest().getHeader(BBBCoreConstants.CHANNEL);
		}
        boolean mobileAppRequest = false;
		
		if(isLoggingDebug()){
			logDebug("Entering class: BBBRestCatalogToolImpl,  method :populateSkuRollUpMap");
		}
		String rollup = null;
		String color = null;
		String size= null;
		String rollupAttribute = null;

		try{
			RepositoryItem repositoryItemRollupType = (RepositoryItem) childProductRepositoryItem.getPropertyValue(
					PROD_ROLLUP_TYPE);
			if(null != repositoryItemRollupType){
				rollupAttribute = (String) repositoryItemRollupType.getPropertyValue(ROLLUP_ATTRIBUTE);
				if(!rollupAttribute.equalsIgnoreCase(NONE)){
					Map<String, String> skuSizeMap = new HashMap<String, String>();
					for(String skuId : lstChidSku){
						if(getCatalogTools().isSkuActive(skuId))
					    {
						final RepositoryItem skuRepositoryItem = getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
						if(skuRepositoryItem != null){
							color= (String) skuRepositoryItem.getPropertyValue(COLOR2);
							size= (String) skuRepositoryItem.getPropertyValue(SIZE2);
							if(rollupAttribute.equals(COLOR_SIZE) || rollupAttribute.equals(SIZE_COLOR)){
								if(color != null && !color.isEmpty()){
									rollup = color + ":";
								}
								if(size != null && !size.isEmpty()){
									rollup = rollup + size;
								}
								if(color.equals(colorParam)){
									Map<String,SkuRestVO> tempMap = new HashMap<String, SkuRestVO>();
									if(channelApp.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB)){
										tempMap.put(skuId, getSkuDetailsForPDP(skuId, true, true,true));
									}
									else{
										tempMap.put(skuId, getSkuDetails(skuId, true, true));
									}
									productRestVO.getSkuWithColorParam().put(productId, tempMap);
								
									skuSizeMap.put(size, skuId);
									productRestVO.getSkuSwatchesWithColorParam().put(productId,skuSizeMap);
								}
							}
							if(rollupAttribute.equals(FINISH_SIZE) || rollupAttribute.equals(SIZE_FINISH)){
								if(color != null && !color.isEmpty()){
									rollup = color + ":";
								}
								if(size != null && !size.isEmpty()){
									rollup = rollup + size;
								}
							}
							if(rollupAttribute.equals(COLOR3)){
								if(color != null && !color.isEmpty()){
									rollup = color;
									if(color.equals(colorParam)){
										Map<String,SkuRestVO> tempMap = new HashMap<String, SkuRestVO>();
										if(channelApp.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB)){
											tempMap.put(skuId, getSkuDetailsForPDP(skuId, true, true,true));
										}
										else{
											tempMap.put(skuId, getSkuDetails(skuId, true, true));
										}
										productRestVO.getSkuWithColorParam().put(productId, tempMap);
									}
								}

							}
							if(rollupAttribute.equals(SIZE3)){
								if(size != null && !size.isEmpty()){
									rollup = size;
								}
							}
							if(rollupAttribute.equals(FINISH)){
								if(color != null && !color.isEmpty()){
									rollup = color;
								}

							}
							skuRollupMap.put(skuId, rollup);
							productRestVO.setSkuRollupMap(skuRollupMap);
						} else{
							throw new BBBBusinessException (SKU_NOT_PRESENT_IN_THE_CATALOG,BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
						}
					  }
						else {
							if(isLoggingDebug()){
								logDebug("skuId  " + skuId + " is inactive so excluding from rollup list");
							}
	                    }
					}
				}
			}
		}
		catch (RepositoryException e) {
			throw new BBBSystemException(REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
		}
		
		if(isLoggingDebug()){
			logDebug("Exiting class: BBBRestCatalogToolImpl,  method :populateSkuRollUpMap");
		}
		
	}





	/**
	 * This method is used to get value of product attribute and set them into wraper vo.
	 * We are doing this because Rest OOB is not able to return Map with list as value.
	 * @param productRestVO
	 * @param productVO
	 */
	private void createProductAttribute(ProductRestVO productRestVO,
			ProductVO productVO) {
		
		if(isLoggingDebug()){
			logDebug("Entering class: BBBRestCatalogToolImpl,  method :createProductAttribute");
		}
		

		CatalogItemAttributesVO productAllAttributesVO =createAttributesVO(productVO.getAttributesList());
		productRestVO.setProductAllAttributesVO(productAllAttributesVO);

		if(isLoggingDebug()){
			logDebug("Exiting class: BBBRestCatalogToolImpl,  method :createProductAttribute");
		}
		
	}

	/**
	 * This method is used to get value of product rollup and set them into wraper vo.
	 * We are doing this because Rest OOB is not able to return Map with list as value.
	 * @param productRestVO
	 * @param productVO
	 */
	private void createRollupAttributes(ProductRestVO productRestVO,
			ProductVO productVO) {
		
		if(isLoggingDebug()){
			logDebug("Exiting class: BBBRestCatalogToolImpl,  method :createRollupAttributes");
		}
		
		List<ProductRollupVO> lstRollupAttributes = new ArrayList<ProductRollupVO>();

		Map<String, List<RollupTypeVO>> mapRollupAttributesList = productVO.getRollupAttributes();
		if(mapRollupAttributesList != null ){
			Set<String> keySet=mapRollupAttributesList.keySet();

			for(String key:keySet){
				ProductRollupVO productRollupVO = new ProductRollupVO();
				List<RollupTypeVO> rollUpList=mapRollupAttributesList.get(key);
				productRollupVO.setRollupTypeKey(key);
				productRollupVO.setRollupTypeVO(rollUpList);
				lstRollupAttributes.add(productRollupVO);
			}
			productRestVO.setProductRollupVO(lstRollupAttributes);
		}
		
		if(isLoggingDebug()){
			logDebug("Exiting class: BBBRestCatalogToolImpl,  method :createRollupAttributes");
		}
		
	}


	/**
	 * This method is used to get value of Product RelationRollup and set them into wraper vo.
	 * We are doing this because Rest OOB is not able to return Map with list as value.
	 * @param productRestVO
	 * @param productVO
	 */
	private void createPrdRelationRollup(ProductRestVO productRestVO,
			ProductVO productVO) {
		if(isLoggingDebug()){
			logDebug("Exiting class: BBBRestCatalogToolImpl,  method :createPrdRelationRollup");
		}
		List<PrdRelationRollupVO> lstPrdRelationRollupVO = new ArrayList<PrdRelationRollupVO>();

		Map<String, List<RollupTypeVO>> mapAttributesList = productVO.getPrdRelationRollup();

		if(mapAttributesList != null ){


			Set<String> keySet=mapAttributesList.keySet();

			for(String key:keySet){
				PrdRelationRollupVO prdRelationRollupVO = new PrdRelationRollupVO();
				List<RollupTypeVO> rollUpList=mapAttributesList.get(key);
				prdRelationRollupVO.setRollupTypeKey(key);
				prdRelationRollupVO.setRollupTypeVO(rollUpList);
				lstPrdRelationRollupVO.add(prdRelationRollupVO);
			}
			productRestVO.setPrdRelationRollup(lstPrdRelationRollupVO);
		}
		
		if(isLoggingDebug()){
			logDebug("Exiting class: BBBRestCatalogToolImpl,  method :createPrdRelationRollup");
		}
		
	}
	/**
	 * This method calls CatalogTools.getProductDetails to get the details of product for each product in
	 * product list
	 * @param pProductIdList comma separated product list from rest client
	 * @return Map<String,ProductPLPVO> Map with key as product id and values ProductPLPVO object containing
	 * product details
	 * @throws BBBSystemException Non recoverable exception
	 * @throws BBBBusinessException Recoverable exception
	 */
	public Map<String,ProductPLPVO> getProductListDetails(String pProductIdList) throws BBBSystemException, BBBBusinessException{
		Map<String,ProductPLPVO> pMap = null;
		String siteId = SiteContextManager.getCurrentSiteId();
		if(null != pProductIdList){
			List<String> pList  = Arrays.asList(pProductIdList.split(","));
			pMap = new HashMap<String, ProductPLPVO>();
			for(String prodId: pList){
				ProductPLPVO pPLPVO = new ProductPLPVO();
				ProductVO pVo = getCatalogTools().getProductDetails(siteId, prodId);
				//pPLPVO = new ProductPLPVO();
				DozerBeanMapper mapper = getDozerBean().getDozerMapper();
				mapper.map(pVo, pPLPVO);
				pMap.put(prodId, pPLPVO);


				CatalogItemAttributesVO productAllAttributesVO =createAttributesVO(pVo.getAttributesList());
				pPLPVO.setProductAllAttributesVO(productAllAttributesVO);

			}
		}

		return pMap;
	}


	/**
	 * Create Attributes VO for SKU or Product
	 *
	 * @param mapAttributesList
	 * @return
	 */
	private static CatalogItemAttributesVO createAttributesVO( final Map<String, List<AttributeVO>> mapAttributesList ) {

		CatalogItemAttributesVO allAttributeVOs = new CatalogItemAttributesVO();

		if(mapAttributesList !=null){

			Set<String> keySet=mapAttributesList.keySet();
			List<AttributeVO> allAttributesList = new ArrayList<AttributeVO>();

			for(String key:keySet){
				List<AttributeVO> attributesList = mapAttributesList.get(key);

				if( attributesList!=null){
					allAttributesList.addAll(attributesList);
				}
			}
			
			allAttributeVOs.setAttributeVOsList(remDuplicateAttributes(allAttributesList));
			
		}
		return allAttributeVOs;
	}


	/**
	 * Remove duplicate attributes  from List<AttributeVO>
	 * @param allAttributes
	 * @return
	 */
	private static List<AttributeVO> remDuplicateAttributes( List<AttributeVO> allAttributes){

		List<AttributeVO> newAttributesList = null;

		if( allAttributes!=null ){
			//remove duplicates;
			HashSet<AttributeVO> hashSet = new HashSet<AttributeVO>(allAttributes);

			newAttributesList = new ArrayList<AttributeVO>(hashSet);
		}
		return newAttributesList;
	}

	/** This method is to expose Service method of this droplet to REST API.
	 * @param pSchoolId 
	 * @return cdVO
	 * @throws IOException
	 * @throws ServletException
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public CollegeDetailVO getCollegeDetails(final String pSchoolId) throws ServletException, IOException, BBBBusinessException,
	BBBSystemException {

		// To get the College Details.

		CollegeDetailVO cdVO = new CollegeDetailVO();
		SchoolVO schoolVO = null;
		List<ProductVO> productList = null;

		// Check if no school id is passed.
		if (BBBUtility.isEmpty(pSchoolId)) {
			throw new BBBBusinessException(BBBCoreErrorConstants.ERROR_SCHOOL_ID_MISSING,
					"Mandatory parameter School Id's value is missing.");
		}

		final String cacheName = getCatalogTools().getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
				BBBCoreConstants.COLLEGE_DETAIL_CACHE_NAME).get(0);
		int cacheTimeout = 0;
		try {
			cacheTimeout = Integer.parseInt(getCatalogTools().getAllValuesForKey(
					BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY, BBBCoreConstants.COLLEGE_CACHE_TIMEOUT).get(0));
		} catch (NumberFormatException exception) {
			logError("NumberFormatException in getCollegesByState while getting cache timeout"
					+ BBBCoreConstants.COLLEGE_CACHE_TIMEOUT, exception);
		} catch (NullPointerException exception) {
			logError("NullPointerExceptionin getCollegesByState while getting cache timeout"
					+ BBBCoreConstants.COLLEGE_CACHE_TIMEOUT, exception);
		}

		if (null != getObjectCache() && null != getObjectCache().get(pSchoolId, cacheName)) {
			cdVO = (CollegeDetailVO) getObjectCache().get(pSchoolId, cacheName);
		} else {

			schoolVO = getCatalogTools().getSchoolDetailsById(pSchoolId);
			cdVO.setSchoolVO(schoolVO);
			if (null != cdVO.getSchoolVO() && null != cdVO.getSchoolVO().getPromotionRepositoryItem()) {
				Map<String, Object> pMap = new HashMap<String, Object>();
				for (String prop : Arrays.asList(getPromoItemProperties().split(BBBCoreConstants.COMMA))) {
					if (null != cdVO.getSchoolVO().getPromotionRepositoryItem().getPropertyValue(prop))
						pMap.put(prop, cdVO.getSchoolVO().getPromotionRepositoryItem().getPropertyValue(prop));
				}
				cdVO.getSchoolVO().setPromotionRepositoryItem(null);
				cdVO.setPromoMap(pMap);
			}
			String siteId = SiteContextManager.getCurrentSiteId();
			// To get the College Merchandize List.
			try {
				productList = getCatalogTools().getCollegeProduct(pSchoolId, siteId);
			} catch (BBBBusinessException e) {
				logError("Error while fetching the productList for school id : "
						+ pSchoolId + " and siteId : " + siteId + e.getMessage());
			}
			// Dozer Map toe ProductVO to ProductPLPVO
			if (null != productList && !productList.isEmpty()) {
				List<ProductPLPVO> pPlpList = new ArrayList<ProductPLPVO>();
				for (ProductVO pVO : productList) {
					ProductPLPVO pPLPVO = new ProductPLPVO();
					DozerBeanMapper mapper = getDozerBean().getDozerMapper();
					mapper.map(pVO, pPLPVO);
					pPlpList.add(pPLPVO);
				}
				cdVO.setProductVOList(pPlpList);
			}
			getObjectCache().put(pSchoolId, cdVO, cacheName, cacheTimeout);
		}
		return cdVO;
	}

	/**
	 * Wrapper method to get time zones for Rest
	 *
	 * @return timeZoneList
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<String> getTimeZones() throws BBBSystemException, BBBBusinessException {

		List<String> timeZoneList =new ArrayList<String>();
		String siteId = null;
		siteId =  SiteContextManager.getCurrentSiteId();
		timeZoneList = getCatalogTools().getTimeZones(siteId);
		return timeZoneList;
	}

	/**
	 * This method is used to verify whether the category Id is first or not
	 *
	 * @param categoryId
	 * @return firstLevelCategory
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */

	public boolean isFirstLevelCategory(String categoryId) throws BBBSystemException, BBBBusinessException {

		boolean firstLevelCategory = false;
		
		if(isLoggingDebug()){
			logDebug("Inside class: BBBRestCatalogToolImpl,  method :isFirstLevelCategory");
		}
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " isFirstLevelCategory");
		String siteId = null;
		if(isLoggingDebug()){
			logDebug("categoryId :" + categoryId);
		}
		
		siteId = SiteContextManager.getCurrentSiteId();
		try {
			firstLevelCategory = getCatalogTools().isFirstLevelCategory(categoryId, siteId).booleanValue();
		} catch (BBBSystemException e) {
			logError("Catalog API Method Name [isFirstLevelCategory]: RepositoryException ",e);
			throw new BBBSystemException(e.getMessage(), e);
		} finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isFirstLevelCategory");
		}

		
		if(isLoggingDebug()){
			logDebug("Exiting class: BBBRestCatalogToolImpl,  method :isFirstLevelCategory");
		}
		
		return firstLevelCategory;
	}
	
	
	private boolean isErrorExist(String skuId)
	{
		if(skuId==null || skuId.trim().equals(""))
		{
			return(false);
		}
		try {
	        return(false);
	    }
	    catch( Exception e ) {
	    	logError(e.getMessage(),e);
	    	return(true); 
	    }
	}

	public BBBSavedItemsSessionBean getSavedItemsBean() {
		return savedItemsBean;
	}

	public void setSavedItemsBean(BBBSavedItemsSessionBean savedItemsBean) {
		this.savedItemsBean = savedItemsBean;
	}

	public String getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(String userProfile) {
		this.userProfile = userProfile;
	}
private EximKatoriResponseVO formEximKatoriResponse(SkuRestVO skuRestVO, EximSessionBean eximSessionBean){
		if(eximSessionBean!=null){
			if(eximSessionBean.getEximResponse()!=null && !BBBUtility.isEmpty(eximSessionBean.getRefnum()) 
					&& !BBBUtility.isEmpty(eximSessionBean.getSkuId())){
				EximKatoriResponseVO eximKatoriResponseVO = new EximKatoriResponseVO();
				eximKatoriResponseVO.setSku(eximSessionBean.getSkuId());
				eximKatoriResponseVO.setAltImages(eximSessionBean.isAltImages());
				eximKatoriResponseVO.setCost_adder(eximSessionBean.getEximResponse().getCostPriceAdder());
				eximKatoriResponseVO.setCustomization_service(skuRestVO.getSkuVO().getCustomizableCodes());
				eximKatoriResponseVO.setCustomization_status(eximSessionBean.getEximResponse().getCustomizationStatus());
				eximKatoriResponseVO.setDescription(eximSessionBean.getEximResponse().getDescription());
				setImageURLForPersonalizedItems(eximKatoriResponseVO, eximSessionBean);
				eximKatoriResponseVO.setNamedrop(eximSessionBean.getEximResponse().getNamedrop());
				eximKatoriResponseVO.setPersonalizationComplete(eximSessionBean.isPersonalizationComplete());
				eximKatoriResponseVO.setPersonalizedSingleCode(eximSessionBean.getPersonalizedSingleCode());
				eximKatoriResponseVO.setPersonalizedSingleCodeDescription(eximSessionBean.getPersonalizedSingleCodeDescription());
				eximKatoriResponseVO.setPrice_adder(eximSessionBean.getEximResponse().getRetailPriceAdder());
				eximKatoriResponseVO.setQuantity(eximSessionBean.getQuantity());
				eximKatoriResponseVO.setRefnum(eximSessionBean.getRefnum());
				eximKatoriResponseVO.setSku(eximSessionBean.getSkuId());
				return eximKatoriResponseVO;
}
		}
		return null;
	}
	
	private void setImageURLForPersonalizedItems(EximKatoriResponseVO eximKatoriResponseVO, EximSessionBean eximSessionBean){
		for (EximImagePreviewVO eximImage : eximSessionBean.getEximResponse().getImages().get(0).getPreviews()) {
			if(eximImage.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_LARGE)){
				eximKatoriResponseVO.setImageURL(eximImage.getUrl());
				eximKatoriResponseVO.setMobileURL(eximImage.getUrl());
			}
			if(eximImage.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_MEDIUM)){
				eximKatoriResponseVO.setImageURL_thumb(eximImage.getUrl());
			}
			if(eximImage.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_X_SMALL)){
				eximKatoriResponseVO.setMobileURL_thumb(eximImage.getUrl());
			}
		}
	}
	
	public JSONObject removePersonalizedListFromSession(String[] skuList){
		logDebug("skuList :- " + skuList);
		JSONObject result = new JSONObject();
		result.put("isErrorExists", false);
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		BBBSessionBean sessionBean = (BBBSessionBean)request.resolveName(BBBCoreConstants.SESSION_BEAN);
		for(String sku : skuList){
			if(sessionBean.getPersonalizedSkus().containsKey(sku.trim())){
				sessionBean.getPersonalizedSkus().remove(sku.trim());
			}else{
				logDebug("Sku " + sku.trim() + " is not found in session.");
			}
		}
		return result;
	}
	
	/** 
	 * Method to fetch vdc and offset message for mobile.
	 * @param shippingMethod
	 * @param skuID
	 * @param requireMsgInDate
	 * @param messageType
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public String getVDCMessage(String shippingMethod, String skuID, boolean requireMsgInDate, String messageType) throws BBBSystemException, BBBBusinessException{
		logDebug("Inside class: BBBRestCatalogToolImpl,  method :getVDCMessage, Skuid = "+skuID+", shippingMethod = "+shippingMethod+", messageType = "+messageType+",requireMsgInDate  = "+requireMsgInDate);
		
		String vdcMessage = null;
		try {
			if(BBBCoreConstants.MSG_TYPE_VDC.equalsIgnoreCase(messageType)){
				vdcMessage = getCatalogTools().getVDCShipMessage(skuID, requireMsgInDate, shippingMethod, new Date(), true);
			} else if(BBBCoreConstants.MSG_TYPE_OFFSET.equalsIgnoreCase(messageType)){
				vdcMessage = getCatalogTools().getActualOffsetMessage(skuID, SiteContextManager.getCurrentSiteId());
			}
		} catch (BBBSystemException exception) {
			vdcMessage = BBBCoreConstants.BLANK;
			logError("Exception occured while trying to fetch vdc message", exception);
		} catch (BBBBusinessException exception) {
			vdcMessage = BBBCoreConstants.BLANK;
			logError("Exception occured while trying to fetch vdc message", exception);
		}
		logDebug("VDC message for SKU = "+skuID+" is "+vdcMessage);
		return vdcMessage;
	}
	
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	

	/**
	 * @return the porchServiceManager
	 */
	public PorchServiceManager getPorchServiceManager() {
		return porchServiceManager;
	}

	/**
	 * @param porchServiceManager the porchServiceManager to set
	 */
	public void setPorchServiceManager(PorchServiceManager porchServiceManager) {
		this.porchServiceManager = porchServiceManager;
	}

}
