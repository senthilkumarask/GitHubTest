package com.bbb.store.catalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import atg.commerce.inventory.InventoryException;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.PreviewAttributes;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.CollectionProductVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.RollupTypeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.inventory.GSBBBInventoryManagerImpl;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBStoreRestConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.rest.catalog.BBBRestCatalogToolImpl;
import com.bbb.rest.catalog.vo.ProductRestVO;
import com.bbb.rest.catalog.vo.ProductRollupVO;
import com.bbb.rest.catalog.vo.SkuRestVO;
import com.bbb.store.catalog.vo.CompareProductVO;
import com.bbb.store.catalog.vo.CompareProductsBean;
import com.bbb.store.catalog.vo.FilteredProductDetailVO;
import com.bbb.store.catalog.vo.FilteredSKUDetailVO;
import com.bbb.store.catalog.vo.FilteredStyleSkuDetailVO;
import com.bbb.store.catalog.vo.GSMediaVO;
import com.bbb.store.catalog.vo.MinimalProductDetailVO;
import com.bbb.store.catalog.vo.MultiSkuDataVO;
import com.bbb.store.catalog.vo.ProductGSVO;
import com.bbb.store.catalog.vo.SkuGSVO;
import com.bbb.store.catalog.vo.SortOptionsVo;
import com.bbb.utils.BBBUtility;

/**
 * The Class BBBGSCatalogToolsImpl.
 * 
 *  Class created for Guided Selling application to fetch product
 *         details and sku details information
 */
public class BBBGSCatalogToolsImpl extends BBBRestCatalogToolImpl {

	private static final String ZERO = "0";

	private static final String ONE = "1";

	private static final String TWO = "2";

	private static final String GET_GS_MINIMAL_SKU_DETAILS = " getGSMinimalSKUDetails";

	private static final String GET_GS_MINIMAL_PRODUCT_DETAILS = " getGSMinimalProductDetails";

	private static final String GET_FIRST_PARENT_CATEGORY_FOR_PRODUCT = " getFirstParentCategoryForProduct";

	private static final String GET_SITE_SORT_DETAILS = " getSiteSortDetails";

	private static final String PREVIEW_ATTRIBUTES_PATH = "/com/bbb/commerce/catalog/PreviewAttributes";

	private static final String GET_MULTIPLE_PRODUCT_DETAILS = " getMultipleProductDetails";

	private static final String CATEGORY = "category";

	private static final String GET_MULTI_SKU_DETAILS = " getMultiSkuDetails";

	private static final String GET_GS_SKU_DETAILS = " getGSSkuDetails";

	private static final String GET_GS_PRODUCT_DETAILS = " getGSProductDetails";

	private static final String GET_PRODUCT_DETAILS = " getProductDetails";

	/** The Constant REPOSITORY_EXCEPTION. */
	private static final String REPOSITORY_EXCEPTION = "Repository Exception";

	/** The catalog tools. */
	private BBBCatalogTools catalogTools = null;

	/** The catalog repository. */
	private MutableRepository catalogRepository;

	/** The product rel item query. */
	private String productRelItemQuery;

	/** The parent product query. */
	private String parentProductQuery;
	
	/** The parent product Null query. */
	private String parentProductNullQuery;

	/** The bbb gs managed repository. */
	private Repository bbbGSManagedRepository;

	/** The sku facet repository. */
	private Repository skuFacetRepository;

	/** The cat good to know query. */
	private String catGoodToKnowQuery;

	/** The sku facet query. */
	private String skuFacetQuery;

	private Repository siteRepository;
	
	private Repository solutionRepository;
	
	private PreviewAttributes previewAttributes;
	
	public PreviewAttributes getPreviewAttributes() {
		return previewAttributes;
	}

	public void setPreviewAttributes(PreviewAttributes previewAttributes) {
		this.previewAttributes = previewAttributes;
	}

	public Repository getSolutionRepository() {
		return solutionRepository;
	}
	
	public void setSolutionRepository(Repository solutionRepository) {
		this.solutionRepository = solutionRepository;
	}
	
	private GSBBBInventoryManagerImpl gsBBBInventoryManager = null;
	
	/**
	 * @return the gsBBBInventoryManager
	 */
	public GSBBBInventoryManagerImpl getGsBBBInventoryManager() {
		return gsBBBInventoryManager;
	}

	/**
	 * @param gsBBBInventoryManager the gsBBBInventoryManager to set
	 */
	public void setGsBBBInventoryManager(
			GSBBBInventoryManagerImpl gsBBBInventoryManager) {
		this.gsBBBInventoryManager = gsBBBInventoryManager;
	}
	
	/**
	 * @return the parentProductNullQuery
	 */
	public String getParentProductNullQuery() {
		return parentProductNullQuery;
	}

	/**
	 * @param parentProductNullQuery the parentProductNullQuery to set
	 */
	public void setParentProductNullQuery(String parentProductNullQuery) {
		this.parentProductNullQuery = parentProductNullQuery;
	}

	public Repository getSiteRepository() {
		return siteRepository;
	}
	
	public void setSiteRepository(Repository siteRepository) {
		this.siteRepository = siteRepository;
	}

	protected static final String BOOLEAN_PROPERTY = "attributeValueBoolean";

	/**
	 * Gets the sku facet query.
	 * 
	 * @return the skuFacetQuery
	 */
	public String getSkuFacetQuery() {
		return skuFacetQuery;
	}

	/**
	 * Sets the sku facet query.
	 * 
	 * @param skuFacetQuery
	 *            the skuFacetQuery to set
	 */
	public void setSkuFacetQuery(String skuFacetQuery) {
		this.skuFacetQuery = skuFacetQuery;
	}

	/**
	 * Gets the cat good to know query.
	 * 
	 * @return the catGoodToKnowQuery
	 */
	public String getCatGoodToKnowQuery() {
		return catGoodToKnowQuery;
	}

	/**
	 * Sets the cat good to know query.
	 * 
	 * @param catGoodToKnowQuery
	 *            the catGoodToKnowQuery to set
	 */
	public void setCatGoodToKnowQuery(String catGoodToKnowQuery) {
		this.catGoodToKnowQuery = catGoodToKnowQuery;
	}

	/**
	 * Gets the sku facet repository.
	 * 
	 * @return the skuFacetRepository
	 */
	public Repository getSkuFacetRepository() {
		return skuFacetRepository;
	}

	/**
	 * Sets the sku facet repository.
	 * 
	 * @param skuFacetRepository
	 *            the skuFacetRepository to set
	 */
	public void setSkuFacetRepository(Repository skuFacetRepository) {
		this.skuFacetRepository = skuFacetRepository;
	}

	/**
	 * Gets the bbb gs managed repository.
	 * 
	 * @return the bbbGSManagedRepository
	 */
	public Repository getBbbGSManagedRepository() {
		return bbbGSManagedRepository;
	}

	/**
	 * Sets the bbb gs managed repository.
	 * 
	 * @param bbbGSManagedRepository
	 *            the bbbGSManagedRepository to set
	 */
	public void setBbbGSManagedRepository(Repository bbbGSManagedRepository) {
		this.bbbGSManagedRepository = bbbGSManagedRepository;
	}

	/**
	 * Gets the parent product query.
	 * 
	 * @return the parentProductQuery
	 */
	public String getParentProductQuery() {
		return parentProductQuery;
	}

	/**
	 * Sets the parent product query.
	 * 
	 * @param parentProductQuery
	 *            the parentProductQuery to set
	 */
	public void setParentProductQuery(String parentProductQuery) {
		this.parentProductQuery = parentProductQuery;
	}

	/**
	 * Gets the product rel item query.
	 * 
	 * @return the productRelItemQuery
	 */
	public String getProductRelItemQuery() {
		return productRelItemQuery;
	}

	/**
	 * Sets the product rel item query.
	 * 
	 * @param productRelItemQuery
	 *            the productRelItemQuery to set
	 */
	public void setProductRelItemQuery(String productRelItemQuery) {
		this.productRelItemQuery = productRelItemQuery;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbb.rest.catalog.BBBRestCatalogToolImpl#getCatalogRepository()
	 */
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.rest.catalog.BBBRestCatalogToolImpl#setCatalogRepository(atg.
	 * repository.MutableRepository)
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbb.rest.catalog.BBBRestCatalogToolImpl#getCatalogTools()
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bbb.rest.catalog.BBBRestCatalogToolImpl#setCatalogTools(com.bbb.commerce
	 * .catalog.BBBCatalogTools)
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	private List<String> gsApplicableChannels;
	
	public List<String> getGsApplicableChannels() {
		return gsApplicableChannels;
	}
	public void setGsApplicableChannels(List<String> gsApplicableChannels) {
		this.gsApplicableChannels = gsApplicableChannels;
	}

	
	/**
	 * Method created to get the product details information for the product and
	 * category passed as an input parameter.
	 * @param pProductId
	 * @param pCategoryId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public FilteredProductDetailVO getProductDetails(String pProductId, String pCategoryId)
			throws BBBSystemException, BBBBusinessException {
		
		logDebug("Inside class: BBBGSCatalogToolsImpl,  method :getProductDetails");
		logDebug("Product Id : " + pProductId);
		logDebug("Category Id : " + pCategoryId);
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
				+ GET_PRODUCT_DETAILS);
		logDebug("Exiting class: BBBGSCatalogToolsImpl,  method :getProductDetails");
		BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
				+ GET_PRODUCT_DETAILS);
		return BBBGSFilteredResponse.getFilteredProductDetails(getGSProductDetails(pProductId,pCategoryId));
	}
	
	/**
	 * @param pProductId
	 * @param pCategoryId
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public ProductGSVO getGSProductDetails(String pProductId, String pCategoryId) throws BBBBusinessException, BBBSystemException{
		
		logDebug("Inside class: BBBGSCatalogToolsImpl,  method :getGSProductDetails");
		logDebug("Product Id : " + pProductId);
		logDebug("Category Id : " + pCategoryId);
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
				+ GET_GS_PRODUCT_DETAILS);
		ProductGSVO productGSVO =  new ProductGSVO();
		try {
			final RepositoryItem productRepositoryItem = this
					.getCatalogRepository().getItem(pProductId,
							BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
			if (productRepositoryItem == null) {
				throw new BBBBusinessException(
						BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
						BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
			} else {
				getProductSpecificDetails(productGSVO,pProductId,pCategoryId, productRepositoryItem);
				getSiblingProducts(productGSVO, pProductId, productRepositoryItem);
			}
		}catch (RepositoryException e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
					+ GET_GS_PRODUCT_DETAILS);
			throw new BBBSystemException(
					REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
		}finally{
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
					+ GET_GS_PRODUCT_DETAILS);
		}
		logDebug("Exiting class: BBBGSCatalogToolsImpl,  method :getProductDetails");
		return productGSVO;
	}
	
	/**
	 * Method created to get the product specific details information for both parent product and sibling products
	 * @param pProductId
	 * @param pCategoryId
	 * @param productRepositoryItem
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void getProductSpecificDetails(ProductGSVO productGSVO,String pProductId, String pCategoryId,RepositoryItem productRepositoryItem)
			throws BBBSystemException, BBBBusinessException {

		String priceRangeDesc[] = null;
		String salePriceRangeDesc[] = null;
		String primaryCategory = "";
		List<GSMediaVO> productMedia=new ArrayList<GSMediaVO>();

		logDebug("Inside class: BBBGSCatalogToolsImpl,  method :getProductSpecificDetails");
		logDebug("Product Id : " + pProductId);
		logDebug("Category Id : " + pCategoryId);

		ProductRestVO productRestVO = super.getProductDetails(pProductId);
		if(null != productRestVO && productRestVO.isErrorExist()){
			throw new BBBBusinessException(productRestVO.getErrorCode(),productRestVO.getErrorCode());
		} else {
			productGSVO.setProductRestVO(productRestVO);
			String priceRangeDescription = productGSVO.getProductRestVO()
					.getProductVO().getPriceRangeDescription();
			String salePriceRangeDescription = productGSVO.getProductRestVO()
					.getProductVO().getSalePriceRangeDescription();
	
			if (!BBBUtility.isEmpty(priceRangeDescription)) {
				if (priceRangeDescription.contains(BBBCoreConstants.HYPHEN)) {
					priceRangeDesc = priceRangeDescription
							.split(BBBCoreConstants.HYPHEN);
					productGSVO.setProductLowPrice(priceRangeDesc[0].trim());
					productGSVO.setProductHighPrice(priceRangeDesc[1].trim());
				} else {
					productGSVO.setProductLowPrice(priceRangeDescription);
				}
			}
			
			if (!BBBUtility.isEmpty(salePriceRangeDescription)) {
				if (salePriceRangeDescription.contains(BBBCoreConstants.HYPHEN)) {
					salePriceRangeDesc = salePriceRangeDescription
							.split(BBBCoreConstants.HYPHEN);
					productGSVO.setProductLowSalePrice(salePriceRangeDesc[0].trim());
					productGSVO.setProductHighSalePrice(salePriceRangeDesc[1].trim());
				} else {
					productGSVO.setProductLowSalePrice(salePriceRangeDescription);
				}
			}
			String siteId = SiteContextManager.getCurrentSite().getId();
			
			if(BBBUtility.isNotEmpty((String) productRepositoryItem.getPropertyValue(BBBStoreRestConstants.PRIMARY_CATEGORY)) && checkGSCategoryForSite((String) productRepositoryItem.getPropertyValue(BBBStoreRestConstants.PRIMARY_CATEGORY), siteId)){
				primaryCategory = (String) productRepositoryItem.getPropertyValue(BBBStoreRestConstants.PRIMARY_CATEGORY);
			} else {
				primaryCategory = getFirstParentCategoryForProduct(pProductId,siteId);
			}
			productGSVO.setPrimaryCategory(primaryCategory);
			logDebug("Primary Category : " + primaryCategory);
			String seoUrl = (String) productRepositoryItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME);
			productGSVO.setSeoUrl(seoUrl);
			Map<String, String> productImages = (Map<String, String>) productRepositoryItem.getPropertyValue(BBBStoreRestConstants.PRODUCT_IMAGES);
			if (!BBBUtility.isMapNullOrEmpty(productImages)) {
				productGSVO.setProductImageMap(productImages);
			}
			
			
			productMedia=getProductMedia(pProductId, siteId);
			
			if (!productMedia.isEmpty()){
				productGSVO.setOtherMedia(productMedia);
			}
			
			try {
				getGoodToKnow(productGSVO, productRepositoryItem, pCategoryId,
						primaryCategory,siteId);
			} catch (RepositoryException e) {
				logDebug("Inside getGoodToKnow method" + REPOSITORY_EXCEPTION);
			}
	
			logDebug("Exiting class: BBBRestCatalogToolImpl,  method :getProductSpecificDetails");
		}

	}
	
	@SuppressWarnings("unchecked")
	public List<GSMediaVO> getProductMedia(String productId,String siteId) throws BBBSystemException{
		logDebug("Catalog API Method Name [getProductMedia] product Id "+productId+" siteId "+siteId);
		List<GSMediaVO> productMedia=new ArrayList<GSMediaVO>();
		try{
			final RepositoryItem productRepositoryItem=this.getCatalogRepository().getItem(productId, BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
			if(productRepositoryItem!=null && productRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIA_RELN_PRODUCT_PROPERTY_NAME)!=null ){
				List<RepositoryItem> prdtMediaItemList=(List<RepositoryItem>) productRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIA_RELN_PRODUCT_PROPERTY_NAME);
				if(prdtMediaItemList!=null && !prdtMediaItemList.isEmpty()){
					Set<RepositoryItem> mediaSites;
					for(RepositoryItem prdtMediaRepoItem:prdtMediaItemList){
						//get all the sites for which the media is applicable
						mediaSites=(Set<RepositoryItem>) prdtMediaRepoItem.getPropertyValue(BBBCatalogConstants.SITES_OTHER_MEDIA_PROPERTY_NAME);
						if(mediaSites!=null){
							for(RepositoryItem siteItem:mediaSites ){
								//if the site to which video is applicable is same as current site add the mediVO in the list
								if(siteItem.getRepositoryId().equalsIgnoreCase(siteId)){
									logDebug(prdtMediaRepoItem.getRepositoryId()+" nmedia Id is applicable for site "+siteId+" adding it the list of MediaVO for the product");
									productMedia.add(populateMediaVO( prdtMediaRepoItem));
									break;
								}
							}
						}
					}
				}

			}
		}catch(RepositoryException e){
			logDebug("Catalog API Method Name [getProductMedia]: RepositoryException ");
			throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,e);
		}
		return productMedia;
	}
	
	private GSMediaVO populateMediaVO(RepositoryItem prdtMediaRepoItem){
		GSMediaVO gsMediaVO=new GSMediaVO();
		if(prdtMediaRepoItem.getPropertyValue(BBBCatalogConstants.PROVIDER_OTHER_MEDIA_PROPERTY_NAME)!=null){
			gsMediaVO.setProviderId((String) prdtMediaRepoItem.getPropertyValue(BBBCatalogConstants.PROVIDER_OTHER_MEDIA_PROPERTY_NAME));
		}
		if(prdtMediaRepoItem.getPropertyValue(BBBCatalogConstants.MEDIA_TYPE_OTHER_MEDIA_PROPERTY_NAME)!=null){
			gsMediaVO.setMediaType((String) prdtMediaRepoItem.getPropertyValue(BBBCatalogConstants.MEDIA_TYPE_OTHER_MEDIA_PROPERTY_NAME));
		}
		if(prdtMediaRepoItem.getPropertyValue(BBBStoreRestConstants.WIDGET_OTHER_MEDIA_PROPERTY_NAME)!=null){
			gsMediaVO.setWidget((String) prdtMediaRepoItem.getPropertyValue(BBBStoreRestConstants.WIDGET_OTHER_MEDIA_PROPERTY_NAME));
		}
		return gsMediaVO;
	}

	/**
	 * Method created to get the good to know map from category good to knows
	 * and sku facet.
	 * 
	 * @param productGSVO
	 *            the product gsvo
	 * @param productRepositoryItem
	 *            the product repository item
	 * @param pCategoryId
	 *            the category id
	 * @param primaryCategoryId
	 *            the primary category id
	 * @return the good to know
	 * @throws RepositoryException
	 *             the repository exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException 
	 */
	private void getGoodToKnow(final ProductGSVO productGSVO,
			final RepositoryItem productRepositoryItem, final String pCategoryId,
			final String primaryCategoryId, final String siteId) throws RepositoryException,
			BBBBusinessException, BBBSystemException {

		logDebug("Inside class: BBBGSCatalogToolsImpl,  method :getGoodToKnow");
		logDebug("Inside class: BBBGSCatalogToolsImpl,  method : getGoodToKnow : Category Id : " + pCategoryId);
		logDebug("Inside class: BBBGSCatalogToolsImpl,  method : getGoodToKnow : Primary Category Id : "
				+ primaryCategoryId);

		List<String> goodToKnowFacetValuesList = new ArrayList<String>();
		String productGoodToKnow = "";
		List<RepositoryItem> catGoodToKnowsList = new ArrayList<RepositoryItem>();
		String categoryId = "";
		String goodToKnowName = "";
		String goodToKnowFacetTypeName = null;
		String facetType = null;
		
		// GS-157. Changed map to list and internal map to linked hash map to Sort Good To Knows in the order defined in BCC.
		final ArrayList<String> goodToKnow = new ArrayList<String>();
		final Map<String, Map<String, List<String>>> categoryGoodToKnow = 
				new LinkedHashMap<String, Map<String, List<String>>>();
		Map<String, List<String>> categoryGoodToKnowFacet = null;
		Map<String, CategoryVO> parentCategoryForProduct = null;
		
		//GS-168. Error Message accessing PDP.
		try{
			parentCategoryForProduct = this.catalogTools.getParentCategoryForProduct(
					productRepositoryItem.getRepositoryId(), siteId);
		} catch(BBBBusinessException bbbexcp){
			this.logError("Business exception while fetching parent category for product " + productRepositoryItem.getRepositoryId(), bbbexcp);
		}

		if (!BBBUtility.isEmpty(pCategoryId)) {
			categoryId = pCategoryId;
		} else if (!BBBUtility.isMapNullOrEmpty(parentCategoryForProduct)) {
			if (null != parentCategoryForProduct.get(TWO)) {
				categoryId = parentCategoryForProduct.get(TWO).getCategoryId();
			} else if (null != parentCategoryForProduct.get(ONE)) {
				categoryId = parentCategoryForProduct.get(ONE).getCategoryId();
			} else {
				categoryId = parentCategoryForProduct.get(ZERO).getCategoryId();
			}

		} else if (!BBBUtility.isEmpty(primaryCategoryId)) {
			categoryId = primaryCategoryId;
		} else {
			logDebug(BBBCatalogConstants.NO_PARENT_CATEGORY_FOR_PRODUCT_IN_REPOSITORY);
			return;
		}
		logDebug("Inside class: BBBGSCatalogToolsImpl,  method : getGoodToKnow : Primary Category Id Selected: "
				+ categoryId);
		
		RepositoryItem[] catRelItem = null;
		final RepositoryView catRelItemView = this.getBbbGSManagedRepository()
				.getView(BBBStoreRestConstants.GS_MANAGED_ITEM_DESC);
		final RqlStatement catGoodToKnowStatement = RqlStatement
				.parseRqlStatement(this.getCatGoodToKnowQuery());
		final Object categoryParams[] = new Object[1];
		categoryParams[0] = categoryId;
		catRelItem = catGoodToKnowStatement.executeQuery(catRelItemView, categoryParams);

		if (catRelItem != null) {

			catGoodToKnowsList = (List<RepositoryItem>) catRelItem[0]
					.getPropertyValue(BBBStoreRestConstants.CAT_GOOD_TO_KNOWS);
			if (!catGoodToKnowsList.isEmpty()) {
				final Iterator<RepositoryItem> catGoodToKnowsIterator = catGoodToKnowsList.iterator();
				RepositoryItem catGoodToKnowItem;
				while (catGoodToKnowsIterator.hasNext()) {

					catGoodToKnowItem = catGoodToKnowsIterator.next();
					if (catGoodToKnowItem != null) {
						categoryGoodToKnowFacet = new HashMap<String, List<String>>();
						goodToKnowName = (String) catGoodToKnowItem
								.getPropertyValue(BBBStoreRestConstants.GOOD_TO_KNOW_NAME);
						goodToKnowFacetValuesList = (List<String>) catGoodToKnowItem
								.getPropertyValue(BBBStoreRestConstants.GOOD_TO_KNOW_FACET_VALUES);
						goodToKnowFacetTypeName = (String) catGoodToKnowItem
								.getPropertyValue(BBBStoreRestConstants.GOOD_TO_KNOW_FACET_NAME);
						categoryGoodToKnowFacet.put(goodToKnowFacetTypeName, goodToKnowFacetValuesList);
					}
					categoryGoodToKnow.put(goodToKnowName, categoryGoodToKnowFacet);
				}

			} else {
				logDebug(BBBStoreRestConstants.CATEGORY_GOOD_TO_KNOW_NOT_PRESENT);
			}
			if (!categoryGoodToKnow.isEmpty()) {
				final List<String> childSkus = productGSVO.getProductRestVO().getProductVO().getChildSKUs();

				if (childSkus != null && !childSkus.isEmpty()) {
					final String skuId = childSkus.get(0);
					
					if (!BBBUtility.isEmpty(skuId)) {
						logDebug("Inside class: BBBGSCatalogToolsImpl,  method : getGoodToKnow : skuId " +skuId);
						RepositoryItem[] skuFacetItem = null;
						final RepositoryView skuFacetView = this
								.getSkuFacetRepository().getView(BBBStoreRestConstants.SKU_FACET_ITEM_DESC);
						final RqlStatement skuFacetStatement = RqlStatement.parseRqlStatement(this.getSkuFacetQuery());
						final Object skuFacetParams[] = new Object[1];
						skuFacetParams[0] = skuId;
						skuFacetItem = skuFacetStatement.executeQuery(skuFacetView, skuFacetParams);
						if (null != skuFacetItem) {
							Iterator categoryGoodToKnowIt = categoryGoodToKnow.entrySet().iterator();
							Map.Entry catGoodToKnow;
							Map<String, List<String>> catGoodToKnowFacetType; 
							boolean goodToKnowExists;
							Map.Entry catFacetType;
							String catFacetTypeName;
							RepositoryItem facetTypeItem;
							while (categoryGoodToKnowIt.hasNext()) {
								catGoodToKnow = (Map.Entry) categoryGoodToKnowIt.next();
								final String catGoodToKnowName = (String) catGoodToKnow.getKey();
								logDebug("Inside class: BBBGSCatalogToolsImpl,  method : "
										+ "getGoodToKnow : catGoodToKnowName "
										+ catGoodToKnowName);
								catGoodToKnowFacetType = (Map<String, List<String>>) catGoodToKnow
										.getValue();
								Iterator catGoodToKnowFacetTypeIt = catGoodToKnowFacetType
										.entrySet().iterator();
								while (catGoodToKnowFacetTypeIt.hasNext()) {
									goodToKnowExists = false;
									catFacetType = (Map.Entry) catGoodToKnowFacetTypeIt.next();
									catFacetTypeName = (String) catFacetType.getKey();
									logDebug("Inside class: BBBGSCatalogToolsImpl,  method : getGoodToKnow : catFacetTypeName " +catFacetTypeName);
									final List<String> catFacetValues = (List<String>) catFacetType.getValue();
									for (RepositoryItem skuFacet : skuFacetItem) {
										if (null != skuFacet) {
											facetTypeItem = (RepositoryItem) skuFacet
													.getPropertyValue(BBBStoreRestConstants.FACET_TYPE);
											facetType = (String) facetTypeItem
													.getPropertyValue(BBBStoreRestConstants.SKU_FACET_TYPE_DESCRIPTION);
											productGoodToKnow = (String) skuFacet
													.getPropertyValue(BBBStoreRestConstants.FACET_VALUE);
											logDebug("Inside class: BBBGSCatalogToolsImpl,  method "
													+ ": getGoodToKnow : facetType " + facetType
													+ "and productGoodToKnow "
													+ productGoodToKnow);
											if (null != catFacetValues
													&& catFacetValues
															.contains(productGoodToKnow)
													&& catFacetTypeName
															.equals(facetType)) {
												goodToKnowExists = true;
												break;
										}
									}
									}
									// GS-157 defect. Sort Good To Knows in the order defined in BCC.
									if (goodToKnowExists) {
										goodToKnow.add(catGoodToKnowName);
									}
								}

							}
						}

					} else {
						logDebug(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
						return;
					}
				} else {
					logDebug(BBBCatalogConstants.NO_CHILD_SKU_FOR_PRODUCT_IN_REPOSITORY);
					return;
				}
			}
		}
		else {
			logDebug(BBBStoreRestConstants.CATEGORY_GOOD_TO_KNOW_NOT_PRESENT);
			return;
		}

		productGSVO.setGoodToKnow(goodToKnow);
	}

	/**
	 * This method is used to get the sibling products for the product passed as
	 * an input parameter.
	 * 
	 * @param productGSVO
	 *            the product gsvo
	 * @param productRepositoryItem
	 *            the product repository item
	 * @param siteId
	 *            the site id
	 * @return the sibling products
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */

	private void getSiblingProducts(ProductGSVO productGSVO,String parentProductId,RepositoryItem productRepositoryItem)
			throws BBBSystemException, BBBBusinessException {

		logDebug("Inside class: BBBGSCatalogToolsImpl,  method :getSiblingProducts");

		boolean isLeadProduct;
		List<ProductGSVO> siblingProductsList = new ArrayList<ProductGSVO>();
		List<String> productList = new ArrayList<String>();
		String productId = "";
		RepositoryItem productRelRepositoryItem = null;
		
		isLeadProduct = (Boolean) productRepositoryItem
				.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME);
		if (isLeadProduct) {
			
			 CollectionProductVO collectionProductVO = (CollectionProductVO)productGSVO.getProductRestVO().getProductVO();
			 if(collectionProductVO.getChildProducts() != null){
			 Iterator<ProductVO> it = collectionProductVO.getChildProducts().iterator();
			 RepositoryItem chilProductRepositoryItem;
			 ProductGSVO mProductGSVO;
			 while(it.hasNext()){
				 productId = it.next().getProductId();
				 logDebug("Is Lead Product : Child Product Id"+productId);
				 
				try {
					chilProductRepositoryItem = this.getCatalogRepository().getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
					if (null != chilProductRepositoryItem && getCatalogTools().isProductActive(chilProductRepositoryItem)) {
						 mProductGSVO = new ProductGSVO();
						 getProductSpecificDetails(mProductGSVO,productId, null,chilProductRepositoryItem);
						 siblingProductsList.add(mProductGSVO);
					}
				} catch (RepositoryException e) {
					throw new BBBSystemException(
							REPOSITORY_EXCEPTION,
							BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
				}
				
			 }
			 }
			 productGSVO.setSiblingProducts(siblingProductsList);
			
		} else {
			try {
				RepositoryItem[] productRelItem = null;
				RepositoryView productRelView = getCatalogRepository().getView(
						BBBStoreRestConstants.BBB_PRD_RELN);
				RqlStatement productRelStatement = RqlStatement
						.parseRqlStatement(getProductRelItemQuery());
				Object productRelParams[] = new Object[1];
				productRelParams[0] = productRepositoryItem.getRepositoryId();
				logDebug("Is Not Lead Product : ProductId"+productRelParams[0]);
				productRelItem = productRelStatement.executeQuery(
						productRelView, productRelParams);

				if (productRelItem != null) {
					for (RepositoryItem productRelationItem : productRelItem) {
						boolean isParentProductItem = false;
						if (productRelationItem != null) {
							RepositoryItem[] parentProductItem = null;
							RepositoryView parentProductView = getCatalogRepository().getView(BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
							RqlStatement parentProductStatement = RqlStatement.parseRqlStatement(getParentProductQuery());
							Object parentProductParams[] = new Object[1];
							parentProductParams[0] = productRelationItem.getRepositoryId();
							logDebug("Is Not Lead Product : Product Relation Id" +parentProductParams[0]);
							parentProductItem = parentProductStatement.executeQuery(parentProductView,parentProductParams);

							if (parentProductItem != null) {
								logDebug("Inside not Null Site Id check");
								isParentProductItem = true;

							} else {
								parentProductStatement = RqlStatement.parseRqlStatement(getParentProductNullQuery());
								logDebug("Inside Null Site Id check");
								parentProductItem = parentProductStatement.executeQuery(parentProductView,parentProductParams);
								if(parentProductItem != null){
									isParentProductItem = true;
								}
								
							}
							if (isParentProductItem && getCatalogTools().isProductActive(parentProductItem[0])){
								List<RepositoryItem> childProducts = (List<RepositoryItem>) parentProductItem[0].getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME);
								Iterator<RepositoryItem> it = childProducts.iterator();
								RepositoryItem childProductItem;
								while (it.hasNext()) {
									childProductItem = it.next();
									if (childProductItem != null) {
										productRelRepositoryItem = (RepositoryItem) childProductItem.getPropertyValue(BBBCatalogConstants.PRODUCT_ID_PRODUCT_PROPERTY_NAME);
										if(null != productRelRepositoryItem && getCatalogTools().isProductActive(productRelRepositoryItem)){
										productId = productRelRepositoryItem.getRepositoryId();
										logDebug("Is Not Lead Product : Child Product Id" +productId);
										if (!BBBUtility.isEmpty(productId)&& !productId.equals(parentProductId)) {
											ProductGSVO mProductGSVO = new ProductGSVO();
											getProductSpecificDetails(mProductGSVO,productId, null,productRelRepositoryItem);
											if (!productList.contains(productId)) {
												productList.add(productId);
												siblingProductsList.add(mProductGSVO);
											}
										}
									}
									}
								}
							}
						}
					}
					productGSVO.setSiblingProducts(siblingProductsList);

				}

			} catch (RepositoryException e) {
				throw new BBBSystemException(
						REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
			}
		}

	}

	/**
	 * Same functionality working in method populateSkuRollUpMap
	 * Method for creating the sku roll up map for the product passed as an
	 * input parameter.
	 * 
	 * @param productVO
	 *            the product vo
	 * @param productRestVO
	 *            the product rest vo
	 * @param pProductId
	 *            the product id
	 * @return the sku rollup map details
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 *//*
	public void getSkuRollupMapDetails(ProductVO productVO,
			ProductRestVO productRestVO, String pProductId)
			throws BBBBusinessException, BBBSystemException {

		String rollupAttribute = null;
		String color = null;
		String size = null;
		String rollup = null;
		Double listprice;
		Double salePrice;
		Map<String, String> skuRollupMap = new HashMap<String, String>();
		List<String> lstChidSku = productVO.getChildSKUs();
		try {
			final RepositoryItem productRepositoryItem = this
					.getCatalogRepository().getItem(pProductId,
							BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
			RepositoryItem repositoryItemRollupType = (RepositoryItem) productRepositoryItem
					.getPropertyValue(BBBCatalogConstants.PROD_ROLLUP_TYPE);
			if (null != repositoryItemRollupType) {
				rollupAttribute = (String) repositoryItemRollupType
						.getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE);
				if (!rollupAttribute.equalsIgnoreCase(BBBCatalogConstants.NONE)) {
					for (String skuId : lstChidSku) {
						final RepositoryItem skuRepositoryItem = getCatalogRepository()
								.getItem(skuId,
										BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
						if (skuRepositoryItem != null) {
							color = (String) skuRepositoryItem
									.getPropertyValue(BBBCatalogConstants.COLOR2);
							size = (String) skuRepositoryItem
									.getPropertyValue(BBBCatalogConstants.SIZE2);
							listprice = getCatalogTools().getListPrice(
									pProductId, skuId);
							salePrice = getCatalogTools().getSalePrice(
									pProductId, skuId);

							if (rollupAttribute
									.equals(BBBCatalogConstants.COLOR_SIZE)
									|| rollupAttribute
											.equals(BBBCatalogConstants.SIZE_COLOR)) {
								if (color != null && !color.isEmpty()) {
									rollup = color + ":";
								}
								if (size != null && !size.isEmpty()) {
									rollup = rollup + size;
								}
							}
							if (rollupAttribute
									.equals(BBBCatalogConstants.FINISH_SIZE)
									|| rollupAttribute
											.equals(BBBCatalogConstants.SIZE_FINISH)) {
								if (color != null && !color.isEmpty()) {
									rollup = color + ":";
								}
								if (size != null && !size.isEmpty()) {
									rollup = rollup + size;
								}
							}
							if (rollupAttribute
									.equals(BBBCatalogConstants.COLOR3)) {
								if (color != null && !color.isEmpty()) {
									rollup = color;
								}

							}
							if (rollupAttribute
									.equals(BBBCatalogConstants.SIZE3)) {
								if (size != null && !size.isEmpty()) {
									rollup = size;
								}
							}
							if (rollupAttribute
									.equals(BBBCatalogConstants.FINISH)) {
								if (color != null && !color.isEmpty()) {
									rollup = color;
								}

							}

							if (salePrice > 0) {
								rollup = rollup + ":" + salePrice;
							} else if (listprice > 0) {
								rollup = rollup + ":" + listprice;
							}

							skuRollupMap.put(skuId, rollup);
							productRestVO.setSkuRollupMap(skuRollupMap);
						} else {
							throw new BBBBusinessException(
									BBBCatalogConstants.SKU_NOT_PRESENT_IN_THE_CATALOG,
									BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
						}
					}
				}
			}

		} catch (RepositoryException e) {
			throw new BBBSystemException(
					REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
		}

	}*/
	
	/**
	 * Method for creating the sku roll up map for the product passed as an
	 * input parameter.
	 * @param childProductRepositoryItem
	 * @param productRestVO
	 * @param lstChidSku
	 * @param skuRollupMap
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public void populateSkuRollUpMap(final RepositoryItem childProductRepositoryItem, final ProductRestVO productRestVO, final List<String> lstChidSku , final Map<String,String> skuRollupMap,final String  colorParam,final String productId) throws BBBSystemException, BBBBusinessException
	{
		
		logDebug("Entering class: BBBRestCatalogToolImpl,  method :populateSkuRollUpMap");

		
		StringBuffer rollup = null;
		StringBuffer color = null;
		StringBuffer size= null;
		String rollupAttribute = null;
		Double listprice;
		Double salePrice;
		try{
		RepositoryItem repositoryItemRollupType = (RepositoryItem) childProductRepositoryItem.getPropertyValue(
				BBBCatalogConstants.PROD_ROLLUP_TYPE);
		if(null != repositoryItemRollupType){
			rollupAttribute = (String) repositoryItemRollupType.getPropertyValue(BBBCatalogConstants.ROLLUP_ATTRIBUTE);
			if(!rollupAttribute.equalsIgnoreCase(BBBCatalogConstants.NONE)){
				List<String> rollupValuesList = new ArrayList<String>();
				Map<String, String> skuSizeMap = new HashMap<String, String>();
				RepositoryItem skuRepositoryItem;
				Map<String, String> skuImages;
				for(String skuId : lstChidSku){
					skuRepositoryItem = getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
					if(skuRepositoryItem != null){
						color= (StringBuffer) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.COLOR2);
						size= (StringBuffer) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SIZE2);
						listprice = getCatalogTools().getListPrice(
								childProductRepositoryItem.getRepositoryId().toString(), skuId);
						salePrice = getCatalogTools().getSalePrice(
								childProductRepositoryItem.getRepositoryId().toString(), skuId);
						if(rollupAttribute.equals(BBBCatalogConstants.COLOR_SIZE) || rollupAttribute.equals(BBBCatalogConstants.SIZE_COLOR)){
							if(color != null && color.length() > 0){
								rollup = color.append(':');
							}
							if(size != null && size.length() > 0){
								rollup = rollup.append(size);
							}
							//Start GS-130 Alternate images
							for(ProductRollupVO productRollupVO: productRestVO.getProductRollupVO()){
								if(BBBCatalogConstants.COLOR3.equalsIgnoreCase(productRollupVO.getRollupTypeKey())){
									for(RollupTypeVO rollupTypeVO: productRollupVO.getRollupTypeVO()){
										if(rollupTypeVO.getRollupAttribute().equals(color) && !rollupValuesList.contains(rollupTypeVO.getRollupAttribute())){
											skuImages = (Map<String, String>) skuRepositoryItem
													.getPropertyValue(BBBCatalogConstants.PROPERTY_SKU_IMAGES);
											rollupValuesList.add(rollupTypeVO.getRollupAttribute());
											if (!BBBUtility.isMapNullOrEmpty(skuImages)) {
												rollupTypeVO.setSkuImageMap(skuImages);
											}
											break;
										}
									}
								}
							}	
							
							//End GS-130
							
							if(color.equals(colorParam)){
								Map<String,SkuRestVO> tempMap = new HashMap<String, SkuRestVO>();
								tempMap.put(skuId, getSkuDetails(skuId, true, true));
								productRestVO.getSkuWithColorParam().put(productId, tempMap);
							
								skuSizeMap.put(size.toString(), skuId);
								productRestVO.getSkuSwatchesWithColorParam().put(productId,skuSizeMap);
							}
							
						}
						if(rollupAttribute.equals(BBBCatalogConstants.FINISH_SIZE) || rollupAttribute.equals(BBBCatalogConstants.SIZE_FINISH)){
							if(color != null && color.length() > 0){
								rollup = color.append(':');
							}
							if(size != null && size.length() > 0){
								rollup = rollup.append(size);
							}
						}
						if(rollupAttribute.equals(BBBCatalogConstants.COLOR3)){
							if(color != null && color.length() > 0){
								rollup = color;
								if(color.equals(colorParam)){
									Map<String,SkuRestVO> tempMap = new HashMap<String, SkuRestVO>();
									tempMap.put(skuId, getSkuDetails(skuId, true, true));
									productRestVO.getSkuWithColorParam().put(productId, tempMap);
								}
							}

						}
						if(rollupAttribute.equals(BBBCatalogConstants.SIZE3)){
							if(size != null && size.length() > 0){
								rollup = size;
							}
						}
						if(rollupAttribute.equals(BBBCatalogConstants.FINISH)){
							if(color != null && color.length() > 0){
								rollup = color;
							}

						}
						if (salePrice > 0) {
							rollup = rollup.append(':').append(salePrice);
						} else if (listprice > 0) {
							rollup = rollup.append(':').append(listprice);
						}
						skuRollupMap.put(skuId, rollup.toString());
						productRestVO.setSkuRollupMap(skuRollupMap);
					} else{
						throw new BBBBusinessException (BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,BBBCatalogConstants.SKU_NOT_PRESENT_IN_THE_CATALOG);
					}
				}
			}
		}
		}
		catch (RepositoryException e) {
			throw new BBBSystemException(REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
		}
		
		logDebug("Exiting class: BBBRestCatalogToolImpl,  method :populateSkuRollUpMap");

	}
	
	
	/**
	 * @param pSkuId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public FilteredSKUDetailVO getSkuDetails(String pSkuId)throws BBBSystemException,
 BBBBusinessException {

		logDebug("Inside class: BBBGSCatalogToolsImpl,  method :getSkuDetails");
		return BBBGSFilteredResponse.filteredSkuDetails(getGSSkuDetails(pSkuId));

	}
	/**
	 * Gets the sku details.
	 * 
	 * @param pSkuId
	 *            the sku id
	 * @return the sku details
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public SkuGSVO getGSSkuDetails(String pSkuId) throws BBBSystemException,
			BBBBusinessException {
		logDebug("Inside class: BBBGSCatalogToolsImpl,  method :getGSSkuDetails");
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
				+ GET_GS_SKU_DETAILS);
		SkuGSVO skuGSVO = new SkuGSVO();

		SkuRestVO skuRestVO = super.getSkuDetails(pSkuId, false, true);

		skuGSVO.setSkuRestVO(skuRestVO);
		try {

			final RepositoryItem skuRepositoryItem = this
					.getCatalogRepository().getItem(pSkuId,
							BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);

			Map<String, String> skuImages = (Map<String, String>) skuRepositoryItem
					.getPropertyValue(BBBStoreRestConstants.SKU_IMAGES);
			if (!BBBUtility.isMapNullOrEmpty(skuImages)) {
				skuGSVO.setSkuImageMap(skuImages);
			}
			
			Map<String,Map<String,Integer>> multiSKUDetails = new HashMap<String,Map<String,Integer>>();
			List<String> pSkuIds=new ArrayList<String>();
			pSkuIds.add(0, pSkuId);
			multiSKUDetails= getGsBBBInventoryManager().getMultiSkuWebAndStoreInventory(pSkuIds);
			skuGSVO.setSkuInventory(multiSKUDetails.get(pSkuId));
			
			

		} catch (RepositoryException e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
					+ GET_GS_SKU_DETAILS);
			throw new BBBSystemException(
					REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
		} catch (InventoryException e) {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
					+ GET_GS_SKU_DETAILS);
			throw new BBBBusinessException(
					REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.INVENTORY_INFO_NOT_AVAILABLE_IN_REPOSITORY_FOR_GIVEN_SKUID);
		} finally {

			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
					+ GET_GS_SKU_DETAILS);
		}

		
		logDebug("Exiting class: BBBRestCatalogToolImpl,  method :getGSSkuDetails");
		
		return skuGSVO;
	}

	/**
	 * Gets the multi sku details as list of skuIds.
	 * 
	 * @param skuIdList
	 *            the sku id list
	 * @return the multi sku details
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public Map<String, MultiSkuDataVO> getMultiSkuDetails(
			final String skuIdJsonList) throws BBBSystemException,
			BBBBusinessException, RepositoryException {

		
			logDebug("Inside class: BBBGSCatalogToolsImpl,  method :getMultiSkuDetails ");
			logDebug("Inside class: BBBGSCatalogToolsImpl,  method :getMultiSkuDetails, param :skuIdList "
					+ skuIdJsonList);
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
				+ GET_MULTI_SKU_DETAILS);
		String skuId = null;
		String skuIdArr[] = skuIdJsonList.split(",");
		List<String> skuIdList = Arrays.asList(skuIdArr);

		Map<String, MultiSkuDataVO> multiSkuDataVoMap = new HashMap<String, MultiSkuDataVO>();
		MultiSkuDataVO multiSkuDataVO = null;
		Iterator<String> skuIterator = skuIdList.iterator();
		while (skuIterator.hasNext()) {
			multiSkuDataVO = new MultiSkuDataVO();

			skuId = (String) skuIterator.next();
			try {
				multiSkuDataVO.setSkuGsVo(getSkuDetails(skuId));

			} catch (BBBBusinessException ex) {
				multiSkuDataVO.setSkuError(true);
				multiSkuDataVO.setErrorMessage("For Sku:" + skuId
						+ " Error is: " + ex.getMessage());
			} catch (BBBSystemException ex) {
				multiSkuDataVO.setSkuError(true);
				multiSkuDataVO.setErrorMessage("For Sku:" + skuId
						+ " Error is: " + ex.getMessage());
			}
			multiSkuDataVoMap.put(skuId, multiSkuDataVO);
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
				+ GET_MULTI_SKU_DETAILS);

		return multiSkuDataVoMap;

	}

	public String getCategoryName(String productId, String categoryId)
			throws BBBSystemException, BBBBusinessException {
		String primaryCategoryName = null;
		String primaryCategory = "";
		try {
			if (!categoryId.isEmpty() && !categoryId.equals("")) {
				final RepositoryItem categoryRepositoryItem = this
						.getCatalogRepository().getItem(categoryId, CATEGORY);
				if (categoryRepositoryItem == null) {
					throw new BBBBusinessException(
							BBBCatalogErrorCodes.CATEGORY_NOT_AVAILABLE_IN_REPOSITORY,
							BBBCatalogErrorCodes.CATEGORY_NOT_AVAILABLE_IN_REPOSITORY);
				} else {
					primaryCategoryName = (String) categoryRepositoryItem
							.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME);

					
						logDebug("Primary Category Name : "
								+ primaryCategoryName);
					
				}
			} else {
				final RepositoryItem productRepositoryItem = this
						.getCatalogRepository().getItem(productId,
								BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
				if (productRepositoryItem == null) {
					throw new BBBBusinessException(
							BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
							BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
				} else {
					primaryCategory = (String) productRepositoryItem.getPropertyValue(BBBStoreRestConstants.PRIMARY_CATEGORY);
					
					logDebug("Primary Category : " + primaryCategory);
					
				}
				if (BBBUtility.isNotEmpty(primaryCategory)) {
					final RepositoryItem categoryRepositoryItem = this
							.getCatalogRepository()
							.getItem(
									primaryCategory,
									BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
					if (categoryRepositoryItem == null) {
						throw new BBBBusinessException(
								BBBCatalogErrorCodes.CATEGORY_NOT_AVAILABLE_IN_REPOSITORY,
								BBBCatalogErrorCodes.CATEGORY_NOT_AVAILABLE_IN_REPOSITORY);
					} else {
						primaryCategoryName = (String) categoryRepositoryItem
								.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME);

						
							logDebug("Primary Category Name : "
									+ primaryCategoryName);
						
					}
				}
			}
		} catch (RepositoryException e) {
			throw new BBBSystemException(
					REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
		}
		return primaryCategoryName;
	}

	public boolean validateWebActiveProduct(String productId, String siteId) throws BBBSystemException {

		RepositoryItem productRepositoryItem = null;
		Boolean webOffered = false;
		Boolean disable = false;
		try {
			productRepositoryItem = getCatalogRepository().getItem(productId,
					BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);

			if (productRepositoryItem == null) {
				throw new BBBBusinessException(
						BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
						BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
			} else {

				String webSiteId = siteId
						.substring(BBBStoreRestConstants.NUMERIC_THREE);
				if (webSiteId.equalsIgnoreCase(BBBCatalogConstants.BED_BATH_US)) {
					webOffered = (Boolean) productRepositoryItem
							.getPropertyValue(BBBStoreRestConstants.WEB_OFFERED_DEFAULT);
					disable = (Boolean) productRepositoryItem
							.getPropertyValue(BBBStoreRestConstants.PROD_DISABLE_DEFAULT);
				} else {
					RepositoryItem siteItem = null;
					String tempAttributeName = null;
					@SuppressWarnings("unchecked")
					final Set<RepositoryItem> prdTranslations = (Set<RepositoryItem>) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.PROD_TRANS_SITE_TRANS_PROPERTY_NAME);
					for (RepositoryItem transRepo : prdTranslations) {

						siteItem = (RepositoryItem) transRepo
								.getPropertyValue(BBBCatalogConstants.SITE_PRODUCT_PROPERTY_NAME);
						tempAttributeName = (String) transRepo
								.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_NAME_SITE_TRANS_PROPERTY_NAME);
						if (siteItem.getRepositoryId().equals(webSiteId)
								&& (tempAttributeName
										.equals(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) || tempAttributeName
										.equals(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME))) {
							if (tempAttributeName
									.equals(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME)) {
								webOffered = (Boolean) transRepo
										.getPropertyValue(BOOLEAN_PROPERTY);
							} else {
								disable = (Boolean) transRepo
										.getPropertyValue(BOOLEAN_PROPERTY);
							}
						}
					}
				}
				Date previewDate = new Date();
				if (isPreviewEnabled()) {
					previewDate = getPreviewDate();
				}
				Date startDate = (Date) productRepositoryItem
						.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME);
				Date endDate = (Date) productRepositoryItem
						.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME);
				logDebug(productRepositoryItem.getRepositoryId()
						+ " Product id details ::Product startDate["
						+ startDate + "]Product endDate[" + endDate + "]");
				if (((endDate != null && previewDate.after(endDate)) || (startDate != null && previewDate
						.before(startDate)))
						|| (disable != null && disable)
						|| (webOffered != null && !webOffered)) {
					return false;
				} else {
					return true;
				}
			}
		} catch (RepositoryException e) {
			throw new BBBSystemException(
					REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
		} catch (BBBBusinessException e) {
			//e.printStackTrace();
			logError(e.getMessage(),e);
		}
		return false;
	}
	
	public boolean validateWebActiveSku(String skuId, String siteId) throws BBBSystemException {
		
		RepositoryItem skuRepositoryItem = null;
		Boolean webOffered=false;
		Boolean disable=false;
		try {
			skuRepositoryItem = getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
			
		if(skuRepositoryItem==null){
			throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,BBBCatalogConstants.SKU_NOT_PRESENT_IN_THE_CATALOG);
		}
		else {
			
			String webSiteId=siteId.substring(BBBStoreRestConstants.NUMERIC_THREE);
			if(webSiteId.equalsIgnoreCase(BBBCatalogConstants.BED_BATH_US)){
				webOffered=(Boolean) skuRepositoryItem.getPropertyValue(BBBStoreRestConstants.WEB_OFFERED_DEFAULT);
				disable=(Boolean) skuRepositoryItem.getPropertyValue(BBBStoreRestConstants.DISABLE_DEFAULT);
			}
			else{
				RepositoryItem siteItem = null;
				String tempAttributeName = null;
				@SuppressWarnings("unchecked")
				final Set<RepositoryItem> skuTranslations = (Set<RepositoryItem>) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SKU_TRANS_SITE_TRANS_PROPERTY_NAME);
				for (RepositoryItem transRepo : skuTranslations) {
					
					siteItem = (RepositoryItem) transRepo
							.getPropertyValue(BBBCatalogConstants.SITE_SKU_PROPERTY_NAME);
					tempAttributeName = (String) transRepo.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_NAME_SITE_TRANS_PROPERTY_NAME);
					if (siteItem.getRepositoryId().equals(webSiteId) && (tempAttributeName.equals(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)||tempAttributeName.equals(BBBCatalogConstants.DISABLE_PROPERTY_NAME))) {
						if(tempAttributeName.equals(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)){
							webOffered=(Boolean) transRepo.getPropertyValue(BOOLEAN_PROPERTY);
						}
						else{
							disable=(Boolean) transRepo.getPropertyValue(BOOLEAN_PROPERTY);
						}
					}
				}
			}
			Date previewDate = new Date();
			if(isPreviewEnabled()){
				previewDate = getPreviewDate();
			}
			Date startDate = (Date) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.START_DATE_SKU_PROPERTY_NAME);
			Date endDate = (Date) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.END_DATE_SKU_PROPERTY_NAME);
			logDebug(skuRepositoryItem.getRepositoryId()+" Sku id details ::Sku startDate["+startDate+
					"]Sku endDate["+endDate+"]");
			if(((endDate!=null && previewDate.after(endDate))|| (startDate!=null&& previewDate.before(startDate))) ||(disable!=null && disable) ||(webOffered!=null && !webOffered)){
				return false;
			}
			else{
				return true;
			}
		}
		} catch (RepositoryException e) {
			throw new BBBSystemException(
					REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
		} catch (BBBBusinessException e) {
			//e.printStackTrace();
			logError(e.getMessage(),e);
		}
		return false;
	}


	/**
	 * Method will send a map with key as product id and value as
	 * CompareProductVO Input will be a json string with multiple elements Each
	 * element will be having 3 fields as productId, skuId and categoryId.
	 * 
	 * @param jsonCompareListObj
	 *            the json compare list obj
	 * @return the multiple product details
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public Map<String, CompareProductVO> getMultipleProductDetails(
			String jsonCompareListObj) throws BBBSystemException,
			BBBBusinessException {

		logDebug("Inside class: BBBGSCatalogToolsImpl,  method :getMultipleProductDetails");
		logDebug("Inside class: BBBGSCatalogToolsImpl,  method :getMultipleProductDetails, param :jsonCompareListObj"
				+ jsonCompareListObj);
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
				+ GET_MULTIPLE_PRODUCT_DETAILS);

		List<CompareProductsBean> compareListVO = getCompareProductsListBean(jsonCompareListObj);

		FilteredProductDetailVO filteredProductVO;
		CompareProductVO compareProductVO = null;
		CompareProductsBean compareProductsBean = null;
		String productId = "";
		String skuId = "";
		String categoryId = "";
		Double listprice;
		Double salePrice;
		String key = "";
		Map<String, CompareProductVO> multipleProductDetailsMap = new HashMap<String, CompareProductVO>();

		if (compareListVO != null && !compareListVO.isEmpty()) {
			Iterator<CompareProductsBean> compareListIteartor = compareListVO
					.iterator();

			while (compareListIteartor.hasNext()) {
				compareProductVO = new CompareProductVO();
				SkuGSVO skuGSVO = null;
				compareProductsBean = compareListIteartor.next();
				productId = compareProductsBean.getProductId();
				categoryId = compareProductsBean.getCategoryId();
				skuId = compareProductsBean.getSkuId();
				filteredProductVO = this.getProductDetails(productId, categoryId);
				
				if (BBBUtility.isNotEmpty(skuId)) {
					skuGSVO = this.getGSSkuDetails(skuId);
				}
				if (filteredProductVO != null) {
				
					compareProductVO.setDisplayName(filteredProductVO.getName());
					compareProductVO.setLongDescription(filteredProductVO.getLongDescription());
					compareProductVO.setAttributeList(filteredProductVO.getAttributesList());
					compareProductVO.setGoodToKnow(filteredProductVO.getGoodToKnow());
					compareProductVO.setSiblingProducts(filteredProductVO.getSiblingProducts());
					compareProductVO.setAverageOverallRating(filteredProductVO.getBvReviews().getAverageOverallRating());
					compareProductVO.setChildSKUs(filteredProductVO.getChildSKUs());
					compareProductVO.setSkuRollupMap(filteredProductVO.getSkuRollupMap());
					
					if(skuGSVO != null){
							compareProductVO.setCompareProductImage(skuGSVO.getSkuImageMap());
							compareProductVO.setSkuInStock(skuGSVO.getSkuRestVO().getSkuVO().isSkuInStock());
							compareProductVO.setStoreSKU(skuGSVO.getSkuRestVO().getSkuVO().isStoreSKU());
							compareProductVO.setSkuInventory(skuGSVO.getSkuInventory());
							salePrice = skuGSVO.getSkuRestVO().getSalePrice();
							listprice = skuGSVO.getSkuRestVO().getListPrice();
							if (salePrice != null && salePrice > 0) {
								compareProductVO.setProductLowSalePrice(salePrice.toString());
							} if (listprice != null && listprice > 0) {
								compareProductVO.setProductLowPrice(listprice.toString());
							}
						
					} else {
						compareProductVO.setRollupAttributes(filteredProductVO.getRollupAttributes());
						compareProductVO.setCompareProductImage(filteredProductVO.getProductImageMap());
						compareProductVO.setProductLowPrice(filteredProductVO.getProductLowPrice());
						compareProductVO.setProductHighPrice(filteredProductVO.getProductHighPrice());
						compareProductVO.setProductLowSalePrice(filteredProductVO.getProductLowSalePrice());
						compareProductVO.setProductHighSalePrice(filteredProductVO.getProductHighSalePrice());
					}

					if (!BBBUtility.isEmpty(skuId)) {
						key = productId + "_" + skuId;
					} else {
						key = productId;
					}

					multipleProductDetailsMap.put(key, compareProductVO);
				}

			}
		}

		BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
				+ GET_MULTIPLE_PRODUCT_DETAILS);
		logDebug("Exiting class: BBBGSCatalogToolsImpl,  method :getMultipleProductDetails");
		return multipleProductDetailsMap;

	}

	/**
	 * Method created for parsing the json string passed as an input parameter
	 * json string will be holding productId,skuId and categoryId combination
	 * Once parsed for each element compareProductsBean object will be created
	 * and added to compareProductsList list.
	 * 
	 * @param jsonCompareListObj
	 *            the json compare list obj
	 * @return the compare products list bean
	 */
	private List<CompareProductsBean> getCompareProductsListBean(
			String jsonCompareListObj) {

		logDebug("Inside class: BBBGSCatalogToolsImpl, method :getCompareProductsBean, param :jsonCompareListObj"
				+ jsonCompareListObj);
		JSONObject jsonObject = null;
		jsonObject = (JSONObject) JSONSerializer.toJSON(jsonCompareListObj);
		DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
		List<String> dynaBeanProperties = (ArrayList<String>) getPropertyNames(JSONResultbean);
		List<CompareProductsBean> compareProductsList = new ArrayList<CompareProductsBean>();
		CompareProductsBean compareProductsBean = null;

		if (dynaBeanProperties.contains(BBBStoreRestConstants.COMPARE_PRODUCTS)) {
			List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean
					.get(BBBStoreRestConstants.COMPARE_PRODUCTS);
			List<String> fieldsBeanProperties;
			for (DynaBean item : itemArray) {
				fieldsBeanProperties = (ArrayList) getPropertyNames(item);
				compareProductsBean = new CompareProductsBean();

				if (fieldsBeanProperties.contains(BBBStoreRestConstants.SKU_ID)) {
					if (BBBUtility.isNotEmpty(item.get(
							BBBStoreRestConstants.SKU_ID).toString())) {
						compareProductsBean.setSkuId(item.get(
								BBBStoreRestConstants.SKU_ID).toString());
					} else {
						compareProductsBean.setSkuId(null);
					}
				} else {
					compareProductsBean.setSkuId(null);
				}

				if (fieldsBeanProperties
						.contains(BBBStoreRestConstants.PRODUCT_ID)) {
					if (BBBUtility.isNotEmpty(item.get(
							BBBStoreRestConstants.PRODUCT_ID).toString())) {
						compareProductsBean.setProductId(item.get(
								BBBStoreRestConstants.PRODUCT_ID).toString());
					} else {
						compareProductsBean.setProductId(null);
					}
				} else {
					compareProductsBean.setProductId(null);
				}

				if (fieldsBeanProperties
						.contains(BBBStoreRestConstants.CATEGORY_ID)) {
					if (BBBUtility.isNotEmpty(item.get(
							BBBStoreRestConstants.CATEGORY_ID).toString())) {
						compareProductsBean.setCategoryId(item.get(
								BBBStoreRestConstants.CATEGORY_ID).toString());
					} else {
						compareProductsBean.setCategoryId(null);
					}
				} else {
					compareProductsBean.setCategoryId(null);
				}

				compareProductsList.add(compareProductsBean);

			}
		}

		return compareProductsList;
	}

	/**
	 * Method created to get the property names in a list.
	 * 
	 * @param pDynaBean
	 *            the dyna bean
	 * @return the property names
	 */
	private List<String> getPropertyNames(DynaBean pDynaBean) {

		DynaClass dynaClass = pDynaBean.getDynaClass();
		DynaProperty properties[] = dynaClass.getDynaProperties();
		List<String> propertyNames = new ArrayList<String>();
		String name;
		for (int i = 0; i < properties.length; i++) {
			name = properties[i].getName();
			propertyNames.add(name);
		}
		logDebug("BBBGSCatalogToolsImpl.getPropertyNames() method ends");

		return propertyNames;
	}

	private Date getPreviewDate() {
		PreviewAttributes previewAttributes = getPreviewAttributes();
		Date previewDate = new Date();
		if (null != previewAttributes
				&& previewAttributes.getPreviewDate() != null) {
			previewDate = previewAttributes.getPreviewDate();
		}
		return previewDate;
	}

	/**
	 * This method is to identify whether it is a preview site
	 * 
	 * @return boolean
	 */
	private boolean isPreviewEnabled() {
		try {
			List<String> values = getCatalogTools().getAllValuesForKey(
					BBBCatalogConstants.CONTENT_CATALOG_KEYS,
					BBBCatalogConstants.PREVIEW_ENABLED);
			if (values != null & !values.isEmpty()) {
				return values.get(0).equalsIgnoreCase(BBBCatalogConstants.TRUE) ? true
						: false;
			}
		} catch (BBBSystemException e) {
			   StringBuffer logDebug = new StringBuffer();
				logDebug.append("key not found for preview enabled ").append(
						e.getMessage());
				logError("catalog_1023 :" + logDebug.toString(), e);
			
		} catch (BBBBusinessException e) {
			StringBuffer logDebug = new StringBuffer(40);
			logDebug.append("key not found for preview enabled ").append(
					e.getMessage());
			logError("catalog_1024:" + logDebug.toString(), e);
		}
		return false;
	}
	
	/**
	 * Method to return site specific sort options
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	@SuppressWarnings("unchecked")
	public Set<SortOptionsVo> getSiteSortDetails() throws BBBSystemException, BBBBusinessException {
		
		Set<SortOptionsVo> sortOptionsVoSet = new LinkedHashSet<SortOptionsVo>();
		SortOptionsVo sortOptionsVo = new SortOptionsVo();
		boolean isDefault;
		logDebug("Inside class: BBBGSCatalogToolsImpl,  method :getSiteSortDetails");
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
				+ GET_SITE_SORT_DETAILS);
		try {
			DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			String channelHeader = pRequest.getHeader(BBBCoreConstants.CHANNEL);
			String channelTheme = pRequest.getHeader(BBBCoreConstants.CHANNEL_THEME);
			String channel = "" ;
			final String siteId = SiteContextManager.getCurrentSiteId();

			if(BBBUtility.isEmpty(channelHeader)){
				channelHeader=BBBCoreConstants.DEFAULT_CHANNEL_VALUE;
			}
			
/*			if ((channelHeader.equalsIgnoreCase(BBBCoreConstants.FF1) || channelHeader
					.equalsIgnoreCase(BBBCoreConstants.FF2))
					&& !BBBUtility.isEmpty(channelTheme)) {*/
			if (this.getGsApplicableChannels().contains(channelHeader)
					&& !BBBUtility.isEmpty(channelTheme)) {
				channel = channelHeader + "_" + channelTheme;
			} else {
				channel = channelHeader;
			}
			RepositoryItem siteItem = getSiteRepository().getItem(siteId, BBBStoreRestConstants.SITE_ITEM_DESCRIPTOR);
			if(siteItem != null){
				final Map<String, RepositoryItem> defSiteSortMap  = (Map<String, RepositoryItem>) siteItem.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION);
				final Map<String, List<RepositoryItem>> siteSortOptMap = (Map<String, List<RepositoryItem>>) siteItem.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST);
				
				RepositoryItem defSiteSortOpt = null;
				RepositoryItem siteSortOptItem = null;
				List<RepositoryItem> siteSortOptList = new ArrayList<RepositoryItem>();
				
				if (defSiteSortMap != null && !defSiteSortMap.isEmpty()) {
					if (defSiteSortMap.keySet().contains(channel)) {
						defSiteSortOpt = (RepositoryItem) defSiteSortMap
								.get(channel);
					} else if (!BBBUtility.isEmpty(channelTheme)
							&& channel.contains(channelTheme)) {
						defSiteSortOpt = (RepositoryItem) defSiteSortMap
								.get(channelHeader);
					} else {
						defSiteSortOpt = (RepositoryItem) defSiteSortMap
								.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
					}
				} else{
					logError("Catalog API Method Name [BBBGSCatalogToolsImpl:getSiteSortDetails]:Defaul Site Sort Option Map Null");					
				}
	            
	            
				if (siteSortOptMap != null && !siteSortOptMap.isEmpty()) {
					if (siteSortOptMap.keySet().contains(channel)) {
						siteSortOptItem = (RepositoryItem) siteSortOptMap
								.get(channel);
					} else if (!BBBUtility.isEmpty(channelTheme)
							&& channel.contains(channelTheme)) {
						siteSortOptItem = (RepositoryItem) siteSortOptMap
								.get(channelHeader);
					} else {
						siteSortOptItem = (RepositoryItem) siteSortOptMap
								.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
					}
					siteSortOptList = (List<RepositoryItem>) siteSortOptItem.getPropertyValue(BBBCatalogConstants.SORTINGOPTIONS);
				} else {
					logError("Catalog API Method Name [BBBGSCatalogToolsImpl:getSiteSortDetails]:Site Sort Options Map Null");
				}
				
        		if(defSiteSortOpt != null) {
					if(BBBUtility.isNotEmpty(defSiteSortOpt.getPropertyValue(BBBStoreRestConstants.SORTING_URL_PARAM).toString())){
						sortOptionsVo.setSortingCode(defSiteSortOpt.getPropertyValue(BBBStoreRestConstants.SORTING_URL_PARAM).toString());
					}
					if(BBBUtility.isNotEmpty(defSiteSortOpt.getPropertyValue(BBBStoreRestConstants.SORTING_ORDER).toString())){
						sortOptionsVo.setSortingOrder((Integer) defSiteSortOpt.getPropertyValue(BBBStoreRestConstants.SORTING_ORDER));
					}
					if(BBBUtility.isNotEmpty(defSiteSortOpt.getPropertyValue(BBBStoreRestConstants.SORTING_VALUE).toString())){
						sortOptionsVo.setSortingValue(defSiteSortOpt.getPropertyValue(BBBStoreRestConstants.SORTING_VALUE).toString());
					}
					sortOptionsVo.setDefault(true);
					sortOptionsVoSet.add(sortOptionsVo);
				}
				for (RepositoryItem siteSortOption : siteSortOptList) {
					if(siteSortOption != null) {
						isDefault = false;
						sortOptionsVo = new SortOptionsVo();
						String defaultSortingOptionsId = siteSortOption.getRepositoryId();
						if(defSiteSortOpt != null && defSiteSortOpt.getRepositoryId().equals(defaultSortingOptionsId)){
							isDefault = true;
						}
						if(!isDefault){
							if(BBBUtility.isNotEmpty(siteSortOption.getPropertyValue(BBBStoreRestConstants.SORTING_URL_PARAM).toString())){
								sortOptionsVo.setSortingCode(siteSortOption.getPropertyValue(BBBStoreRestConstants.SORTING_URL_PARAM).toString());
							}
							if(BBBUtility.isNotEmpty(siteSortOption.getPropertyValue(BBBStoreRestConstants.SORTING_ORDER).toString())){
								sortOptionsVo.setSortingOrder((Integer) siteSortOption.getPropertyValue(BBBStoreRestConstants.SORTING_ORDER));
							}
							if(BBBUtility.isNotEmpty(siteSortOption.getPropertyValue(BBBStoreRestConstants.SORTING_VALUE).toString())){
								sortOptionsVo.setSortingValue(siteSortOption.getPropertyValue(BBBStoreRestConstants.SORTING_VALUE).toString());
							}
							sortOptionsVo.setDefault(false);
							sortOptionsVoSet.add(sortOptionsVo);
						}
					}
				}
			}else {
				logError("Catalog API Method Name [BBBGSCatalogToolsImpl:getSiteSortDetails]:Site Item Null");
			}
		} catch (RepositoryException e) {
			logError(e);
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
					+ GET_SITE_SORT_DETAILS);
			 throw new BBBSystemException(
					REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
		
		} finally {
			logDebug("Exiting class: BBBGSCatalogToolsImpl,  method :getSiteSortDetails");
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
					+ GET_SITE_SORT_DETAILS);
		}
		
		return sortOptionsVoSet;
	}
	
	public Map<String, List<String>> getSolutionsPerSku(List<String> pSkuIds) throws BBBSystemException, BBBBusinessException {
		
		BBBPerformanceMonitor.start("BBBGSCatalogToolsImpl getSolutionsPerSku");
		
		logDebug("BBBGSCatalogToolsImpl : getSolutionsPerSku() starts");
		
		
		Map<String, List<String>> solutionsMap = new HashMap<String, List<String>>();
		List<String> solutions = null;
		Set<RepositoryItem> skuFacetType=null;
		for (String skuId : pSkuIds) {
			solutions= new ArrayList<String>();
			RepositoryItem[] skuFacetItem = null;
			RepositoryView skuFacetView;
			skuFacetType = new HashSet<RepositoryItem>();
			
			
			try {
				
				skuFacetView = getSkuFacetRepository().getView(BBBStoreRestConstants.SKU_FACET_ITEM_DESC);
				RqlStatement skuFacetStatement = RqlStatement.parseRqlStatement(getSkuFacetQuery());
				Object skuFacetParams[] = new Object[1]; 
				skuFacetParams[0] = skuId;
				skuFacetItem = skuFacetStatement.executeQuery(skuFacetView,skuFacetParams);
				
				if (skuFacetItem != null) {
					RepositoryItem facetType = null;
					for (RepositoryItem skuFacet : skuFacetItem) {
						if (null !=skuFacet.getPropertyValue(BBBStoreRestConstants.FACET_TYPE)) {
							facetType=(RepositoryItem) skuFacet.getPropertyValue(BBBStoreRestConstants.FACET_TYPE);
							skuFacetType.add(facetType);
						}
					}
					
				}else{
					
					logDebug("No SkuFacets for given SKU: "+skuId);
					
				}
				
				if (skuFacetType !=null){
					RepositoryItem[] solutionLandingItems = null;
					RepositoryView solutionLanding = null;
					
					QueryBuilder queryBuilder;
					QueryExpression queryExpSiteId;
					QueryExpression querySiteId;
					Query queryHomePage;
					
					for (RepositoryItem skuFacet : skuFacetType){
						
						solutionLanding = getSolutionRepository().getView(BBBStoreRestConstants.SOLUTION_TEMP);
						queryBuilder = solutionLanding.getQueryBuilder();
						
						queryExpSiteId = queryBuilder.createPropertyQueryExpression(BBBStoreRestConstants.SOLUTION_FACETS);
						querySiteId = queryBuilder.createConstantQueryExpression(skuFacet);
						
			
						final Query[] queries = new Query[1];
						
						queries[0] = queryBuilder.createComparisonQuery(querySiteId,queryExpSiteId, QueryBuilder.EQUALS);
						queryHomePage = queryBuilder.createAndQuery(queries);
						
						
						solutionLandingItems = solutionLanding.executeQuery(queryHomePage);
						
												
						if(null!=solutionLandingItems)
						{
							for (RepositoryItem solutionLandingItem: solutionLandingItems){
								
								if(!solutions.contains(solutionLandingItem.getRepositoryId())){
									solutions.add(solutionLandingItem.getRepositoryId());
								}
								
							}
						}
						
					}
					
				}
			
				
				
			} catch (RepositoryException e) {
				 logError(e);
				 BBBPerformanceMonitor.end("BBBGSCatalogToolsImpl getSolutionsPerSku");
				 throw new BBBSystemException(REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
			
			}
			finally {
				logDebug("Exiting class: BBBGSCatalogToolsImpl,  method :getSolutionsPerSku");
				BBBPerformanceMonitor.end("BBBGSCatalogToolsImpl getSolutionsPerSku");
			}
			
			solutionsMap.put(skuId, solutions);
			
		}
				
		return solutionsMap;
		
	}
	
	/**
	 * @param productId
	 * @param siteId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public String getFirstParentCategoryForProduct(String productId, String siteId) throws BBBBusinessException,
	BBBSystemException {
		logDebug("Catalog API Method Name [getFirstParentCategoryForProduct] productId["+productId+"]");
		Map<String,CategoryVO> parentCategoryMap=new LinkedHashMap<String, CategoryVO>();
		String categoryId = null;
		if(!StringUtils.isEmpty(productId)){
			try{
				BBBPerformanceMonitor.start(
						BBBPerformanceConstants.CATALOG_API_CALL+GET_FIRST_PARENT_CATEGORY_FOR_PRODUCT);
				RepositoryItem productRepositoryItem=this.getCatalogRepository().getItem(productId,BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);

				if(productRepositoryItem!=null){
					logDebug("productRepositoryItem is not null for product id "+productId);
					Set<RepositoryItem> parentCategorySet=(Set<RepositoryItem>) productRepositoryItem.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME);
					RepositoryItem categoryRepositoryItem=this.getCategoryForSite(parentCategorySet, siteId);
					if(categoryRepositoryItem!=null){
						categoryId=categoryRepositoryItem.getRepositoryId();
					}
					
					else{
						logDebug("Product Id "+productId+" does not have any parent for site "+siteId);
					}
				}
				else{
					logDebug("Product Id "+productId+" is not present in the repository");
				}

			}catch(RepositoryException e)
			{
				logDebug("Catalog API Method Name [getAllParentCategoryForProduct]: RepositoryException ");
			} finally{
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL+GET_FIRST_PARENT_CATEGORY_FOR_PRODUCT);
			}
		}
		else{
			logDebug("Product Id passed is null");
		}
		return categoryId;
	}
	
	/**
	 * @param parentCategorySet
	 * @param siteId
	 * @return
	 */
	private RepositoryItem getCategoryForSite(Set<RepositoryItem> parentCategorySet ,String siteId){
		RepositoryItem categoryRepositoryItem=null;
		if(parentCategorySet!=null && !parentCategorySet.isEmpty()){
			logDebug("Parent category set is not null values for parent category::"+parentCategorySet);
			Set<String> catSiteId;
			for(RepositoryItem catRepo:parentCategorySet){
				catSiteId = (Set<String>) catRepo.getPropertyValue("siteIds");
				logDebug("sites applicable for potential parent with id::"+catRepo.getRepositoryId()+" are "+catSiteId +" and the current site id is "+siteId);
				if(catSiteId.contains(siteId)){
					categoryRepositoryItem=catRepo;
					logDebug("Category id "+catRepo.getRepositoryId()+" is the selected parent ");
					break;
				}
			}
		}
		else{
			logDebug("Parent category set is NULL  ");
		}
		return categoryRepositoryItem;
	}
	
	/** This methods take a Category Id and Site Id as Input and returns a boolean Result.
	 *  The result returned is true when the Category Repository Item contains the passed SiteId, 
	 *  and is false when the category is not associated with that Site.
	 *  
	 * @param String catId
	 * @param String siteId
	 * @return boolean result
	 */
	private boolean checkGSCategoryForSite(String catId,String siteId){
		RepositoryItem catRepo = null;
		boolean result = false;
		logDebug("Catalog API Method Name [checkGSCategoryForSite]: Start");
		if(BBBUtility.isNotEmpty(catId) && BBBUtility.isNotEmpty(siteId)){
			logDebug("Parameters passed :: CategoryId="+catId+" SiteId="+siteId);
			try {
				catRepo = getCatalogRepository().getItem(catId, BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
				if(catRepo!=null){
					Set<String> catSiteId = (Set<String>) catRepo.getPropertyValue("siteIds");
					logDebug("sites applicable for potential parent with id::"+catRepo.getRepositoryId()+" are "+catSiteId +" and the current site id is "+siteId);
					if(catSiteId.contains(siteId)){
						logDebug("Category id belongs to the same Site");
						result = true;
					}
				}
			} catch (RepositoryException e) {
				logDebug("Catalog API Method Name [checkGSCategoryForSite]: RepositoryException ");
			}
			
		}
		else{
			logDebug("Category id is not a match for this Site");
		}
		logDebug("Catalog API Method Name [checkGSCategoryForSite]: End");
		return result;
	}
	
	
	 /** Creates a RolluptypeVO based on rollup parameter
    *
    * @param propertyName
    * @param skuRepositoryItem
    * @return */
   protected RollupTypeVO createRollUpVO(final String propertyName, final RepositoryItem skuRepositoryItem, final String rollUpType) {
       final RollupTypeVO rollupTypeVO = new RollupTypeVO();
       rollupTypeVO.setRollupAttribute(propertyName);
       if (skuRepositoryItem.getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME) != null) {
           final String swatchImage = (String) skuRepositoryItem
                           .getPropertyValue(BBBCatalogConstants.SWATCH_IMAGE_IMAGE_PROPERTY_NAME);
           final String swatchImagePath = swatchImage;
           this.logDebug("Method[createRollUpVO] value of swatchImagePath " + swatchImagePath);
           rollupTypeVO.setSwatchImagePath(swatchImagePath);

           // START -- Added following Code for R2.1 Item - PDP Color Swatch Story

           // Setting the Large image path for the corrresponding Swatch.
           rollupTypeVO.setLargeImagePath((String) skuRepositoryItem
                           .getPropertyValue(BBBCatalogConstants.LARGE_IMAGE_PROPERTY_NAME));

           // Setting the Thumbnail image path for the corrresponding Swatch.
           rollupTypeVO.setThumbnailImagePath((String) skuRepositoryItem
                           .getPropertyValue(BBBCatalogConstants.THUMBNAIL_IMAGE_PROPERTY_NAME));

           // Setting the Small image path for the corrresponding Swatch.
           rollupTypeVO.setSmallImagePath((String) skuRepositoryItem
                           .getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_PROPERTY_NAME));

           // END -- Added following getters / setters for R2.1 Item - PDP Color Swatch Story
           //GS-130- Start
           if(rollUpType.equals("COLOR")){
		         Map<String, String> skuImages = (Map<String, String>) skuRepositoryItem
							.getPropertyValue(BBBCatalogConstants.PROPERTY_SKU_IMAGES);
					if (!BBBUtility.isMapNullOrEmpty(skuImages)) {
						rollupTypeVO.setSkuImageMap(skuImages);
					}
			}
           //GS-130-END
       }
       this.logDebug(rollupTypeVO.toString());
       return rollupTypeVO;
   }

	/**
	 * @param pProductId
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public MinimalProductDetailVO getGSMinimalProductDetails(String pProductId) throws BBBBusinessException, BBBSystemException{
		
		
			logDebug("Inside class: BBBGSCatalogToolsImpl,  method :getGSMinimalProductDetails");
			logDebug("Product Id : " + pProductId);
		
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
				+ GET_GS_MINIMAL_PRODUCT_DETAILS);
		MinimalProductDetailVO minimalProductGSVO =  new MinimalProductDetailVO();

		if(!(BBBUtility.isEmpty(pProductId))){
			minimalProductGSVO.setProductId(pProductId);
			try {
				final RepositoryItem productRepositoryItem = this
						.getCatalogRepository().getItem(pProductId,
								BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
				if (productRepositoryItem == null) {
					throw new BBBBusinessException(
							BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
							BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY);
				} else {
					String siteId = SiteContextManager.getCurrentSite().getId();
					
					if(BBBUtility.isNotEmpty((String) productRepositoryItem.getPropertyValue(BBBStoreRestConstants.PRIMARY_CATEGORY)) && checkGSCategoryForSite((String) productRepositoryItem.getPropertyValue(BBBStoreRestConstants.PRIMARY_CATEGORY), siteId)){
						minimalProductGSVO.setPrimaryCategory((String) productRepositoryItem.getPropertyValue(BBBStoreRestConstants.PRIMARY_CATEGORY));
					} else {
						minimalProductGSVO.setPrimaryCategory(getFirstParentCategoryForProduct(pProductId,siteId));
					}			}
			}catch (RepositoryException e) {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
						+ GET_GS_PRODUCT_DETAILS);
				throw new BBBSystemException(
						REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
			}finally{
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
						+ GET_GS_PRODUCT_DETAILS);
			}
		} else {
			
			logDebug("Product Id passed is null or empty");
			
		}
		
		logDebug("Exiting class: BBBGSCatalogToolsImpl,  method :getGSMinimalProductDetails");
		
		return minimalProductGSVO;
	}

	/**
	 * @param pProductId
	 * @return
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public FilteredStyleSkuDetailVO getGSMinimalSKUDetails(String pSkuId) throws BBBBusinessException, BBBSystemException{
		
		
		logDebug("Inside class: BBBGSCatalogToolsImpl,  method :getGSMinimalSKUDetails");
		logDebug("Product Id : " + pSkuId);
		
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL
				+ GET_GS_MINIMAL_SKU_DETAILS);
		FilteredStyleSkuDetailVO minimalSKUGSVO =  new FilteredStyleSkuDetailVO();

		if(!(BBBUtility.isEmpty(pSkuId))){
			minimalSKUGSVO.setSkuId(pSkuId);
			double listPrice;
			double salePrice;
			
			try {
				final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(pSkuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
		        if (skuRepositoryItem == null) {
		            this.logDebug("Repository Item is null for sku id " + pSkuId);
		            throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
		                            BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
		        } else {
			
					SKUDetailVO skuDetailVO = (SKUDetailVO) this.getCatalogTools().getMinimalSku(pSkuId);
					listPrice = getCatalogTools().getListPrice(null,pSkuId).doubleValue();
					salePrice = getCatalogTools().getSalePrice(null,pSkuId).doubleValue();
			        
					minimalSKUGSVO.setDisplayName(skuDetailVO.getDisplayName());
					minimalSKUGSVO.setDescription(skuDetailVO.getDescription());
					minimalSKUGSVO.setLongDescription(skuDetailVO.getLongDescription());
					minimalSKUGSVO.setColor(skuDetailVO.getColor());
					minimalSKUGSVO.setSize(skuDetailVO.getSize());
					minimalSKUGSVO.setUpc(skuDetailVO.getUpc());
			        minimalSKUGSVO.setListPrice(listPrice);
			        minimalSKUGSVO.setSalePrice(salePrice);
					Map<String, String> skuImages = (Map<String, String>) skuRepositoryItem.getPropertyValue(BBBStoreRestConstants.SKU_IMAGES);
					if (!BBBUtility.isMapNullOrEmpty(skuImages)) {
						minimalSKUGSVO.setSkuImageMap(skuImages);
					}
				}
			}catch (RepositoryException e) {
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
						+ GET_GS_MINIMAL_SKU_DETAILS);
				throw new BBBSystemException(
						REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.REPOSITORY_NOT_CONFIGURED_REPOSITORY_EXCEPTION);
			}finally{
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL
						+ GET_GS_MINIMAL_SKU_DETAILS);
			}
		} else {
			
			logDebug("SKU Id passed is null or empty");
			
		}
		
		logDebug("Exiting class: BBBGSCatalogToolsImpl,  method :getGSMinimalSKUDetails");
		
		return minimalSKUGSVO;
	}
}