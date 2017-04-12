package com.bbb.commerce.browse.manager;

import java.util.List;
import java.util.Map;

import com.bbb.common.BBBGenericService;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.ImageVO;
import com.bbb.commerce.catalog.vo.MediaVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.RecommendedCategoryVO;
import com.bbb.commerce.catalog.vo.RollupTypeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.ServletUtil;

/**
 * @author nkum42
 * 
 * This manager is contains  all method which are related to product details . This manager get the data from catalog 
 *
 */
public class ProductManager extends BBBGenericService {
	
	
	
	private BBBCatalogTools bbbCatalogTools;
	
	
	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}
	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}
	
	private MutableRepository catalogRepository;
	
	public final MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	public void setCatalogRepository(final MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}
	
	
	
	/**
	 * Given  the product id and site id, returns a the productVO
	 * @param string, string
	 * @return productVo
	 */
	public  ProductVO getProductDetails(String pSiteId, String pProductId)
			throws BBBSystemException, BBBBusinessException {
		logDebug(" In product Manager siteId["+pSiteId+"] pProductId["+pProductId+"]");
		return bbbCatalogTools.getProductDetails(pSiteId, pProductId);
	}
	
	public  ProductVO getProductDetailsForLazyLoading(String pSiteId, String pProductId, int index)
			throws BBBSystemException, BBBBusinessException {
		logDebug(" In product Manager siteId["+pSiteId+"] pProductId["+pProductId+"]");
		return bbbCatalogTools.getProductDetailsForLazyLoading(pSiteId, pProductId,true, false, index, true);
	}
    /**
     * This is method coded for getting product details for Main product only; Currently used for collection/accessories
     * product. This is also used at places like Breadcrumb/Notify Me as for these place we don't need to fetch every child product of main product.
     * @param pSiteId
     * @param pProductId
     * @return
     * @throws BBBSystemException
     * @throws BBBBusinessException
     */
    public ProductVO getMainProductDetails(final String pSiteId, final String pProductId)
                    throws BBBSystemException, BBBBusinessException {
        return bbbCatalogTools.getMainProductDetails(pSiteId,pProductId,
                         true, false, true);
    }
	
	public  boolean getProductStatus(String pSiteId, String pProductId)
		throws BBBSystemException, BBBBusinessException {
	logDebug(" In product Manager siteId["+pSiteId+"] pProductId["+pProductId+"]");
	return bbbCatalogTools.isEverlivingProduct(pProductId,pSiteId );
	}
	
	
	public  ProductVO getEverLivingProductDetails(String pSiteId, String pProductId)
		throws BBBSystemException, BBBBusinessException {
	logDebug(" In product Manager siteId["+pSiteId+"] pProductId["+pProductId+"]");
	return bbbCatalogTools.getEverLivingProductDetails(pSiteId, pProductId,
            true, false, true);
	}
	public ProductVO getEverLivingMainProductDetails(final String pSiteId, final String pProductId)
            throws BBBSystemException, BBBBusinessException {
    return bbbCatalogTools.getEverLivingMainProductDetails(pSiteId,pProductId,
                 true, false, true);
	}
	
	/**
	 * Given  the product id, returns a the List<MediaVO>
	 * @param string
	 * @return productVo
	 */
	public  List<MediaVO> getMediaDetails(String pProductId, String pSiteId)
			throws BBBSystemException, BBBBusinessException {
		logDebug(" In product Manager pProductId["+pProductId+"]");
		return bbbCatalogTools.getProductMedia(pProductId, pSiteId);
	}
	
	/**
	 * Given  the Product id, site id and rollUpTypeValueMap, returns a SKUId 
	 * @param string, string, Map
	 * @return String
	 */
	public String getEverLivingSKUId(String siteId, String productId,
			Map<String,String> rollUpTypeValueMap) throws BBBSystemException,
			BBBBusinessException {
			logDebug(" In product Manager siteId["+siteId+"] pProductId["+productId+"]");
		return bbbCatalogTools.getEverLivingSKUDetails(siteId, productId, rollUpTypeValueMap);
	}
	
	
	/**
	 * Given  the product id and rollup attributes, returns a the rollup list
	 * @param string, string, string, string
	 * @return rollUpList
	 */
	public  List<RollupTypeVO>getRollupDetails(String productId,String firstRollUpValue, String firstRollUpType, String secondRollUpType)
			throws BBBSystemException, BBBBusinessException {
		logDebug(" In product Manager pProductId["+productId+"] with "+firstRollUpType+" "+firstRollUpValue+" for "+secondRollUpType);
		return bbbCatalogTools.getRollUpList(productId, firstRollUpValue, firstRollUpType, secondRollUpType);
	}
	
	/**
	 * for Ever living pdp without checking if proudct is active Given  the product id and rollup attributes, returns a the rollup list for ever living page
	 * @param string, string, string, string
	 * @return rollUpList
	 */
	public  List<RollupTypeVO>getEverLivingRollupDetails(String productId,String firstRollUpValue, String firstRollUpType, String secondRollUpType)
			throws BBBSystemException, BBBBusinessException {
		logDebug(" In product Manager pProductId["+productId+"] with "+firstRollUpType+" "+firstRollUpValue+" for "+secondRollUpType);
		return bbbCatalogTools.getEverLivingRollUpList(productId, firstRollUpValue, firstRollUpType, secondRollUpType);
	}
	
	/**
	 * Given  the SKU id and site id, returns a the SKUDetailVO 
	 * @param string, string
	 * @return productVo
	 */
	public SKUDetailVO getSKUDetails(String pSiteId, String pSkuId,
			boolean pCalculateAboveBelowLine) throws BBBSystemException,
			BBBBusinessException { 
			logDebug(" getSKUDetails siteId["+pSiteId+"] pSkuId["+pSkuId+"]");
		return bbbCatalogTools.getSKUDetails(pSiteId, pSkuId, pCalculateAboveBelowLine);
	}
	
	/**
	 * Return a new SKUDetailVO with only limited values set such as Sku Image and Sku Color
	 * @param pSkuId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public SKUDetailVO getSkuImageColorDetails(String pSkuId) throws BBBSystemException, BBBBusinessException {
		logDebug("  getSkuImageColorDetails pSkuId["+pSkuId+"]");
		RepositoryItem skuRepoItem = bbbCatalogTools.getSkuRepositoryItem(pSkuId);
		if (bbbCatalogTools.isSkuActive(skuRepoItem)) {
			return new SKUDetailVO(skuRepoItem);
		}
		this.logDebug(pSkuId + " Sku is disabled or no longer available");
        throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY, BBBCatalogErrorCodes.SKU_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY);
	}
	
	public SKUDetailVO getEverLivingSKUDetails(String pSiteId, String pSkuId,
			boolean pCalculateAboveBelowLine) throws BBBSystemException,
			BBBBusinessException {
			logDebug(" In product Manager siteId["+pSiteId+"] pSkuId["+pSkuId+"]");
		return bbbCatalogTools.getEverLivingSKUDetails(pSiteId, pSkuId, pCalculateAboveBelowLine);
	}
	
	/**
	 * Given  the Product id, site id and rollUpTypeValueMap, returns a SKUId 
	 * @param string, string, Map
	 * @return String
	 */
	public String getSKUId(String siteId, String productId,
			Map<String,String> rollUpTypeValueMap) throws BBBSystemException,
			BBBBusinessException {
			logDebug(" In product Manager siteId["+siteId+"] pProductId["+productId+"]");
		return bbbCatalogTools.getSKUDetails(siteId, productId, rollUpTypeValueMap);
	}
	
	
	/**
	 * Given  the Product id, returns Map<String, CategoryVO> 
	 * @param string
	 * @return Map<String, CategoryVO>
	 */
	public Map<String, CategoryVO> getParentCategoryForProduct(String productId,String siteId) throws BBBSystemException,
			BBBBusinessException {
			logDebug(" In product Manager ProductId["+productId+"]");
		return bbbCatalogTools.getParentCategoryForProduct(productId,siteId);
	}
	
	
	/**
	 * Given  the sku id, returns if it is personalized or not
	 * @param string
	 * @return boolean
	 * @throws BBBSystemException 
	 */
	public boolean isPersonalizedSku(String skuId) throws BBBSystemException {
			logDebug(" In product Manager skuId["+skuId+"]");
			RepositoryItem skuRepositoryItem;
			boolean isSKUPersonalizedItem=false;
			try {
				if (BBBUtility.isNotEmpty(skuId)) {
					skuRepositoryItem = this.getCatalogRepository().getItem(skuId, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
					isSKUPersonalizedItem = getBbbCatalogTools().isCustomizationOfferedForSKU(skuRepositoryItem,
							BBBUtility.getCurrentSiteId(ServletUtil.getCurrentRequest()));
				}
			} catch (RepositoryException e) {
				this.logError("Catalog API Method Name [getSKUDetails]: RepositoryException "
						+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSKUDetailsForStore");
				throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
			}
			return isSKUPersonalizedItem;
	}
	/**
	 * Given  the Category id, returns Map<String, CategoryVO> 
	 * @param string
	 * @return Map<String, CategoryVO>
	 */
	public Map<String, CategoryVO> getParentCategory(String categoryId, String siteId) throws BBBSystemException,
			BBBBusinessException {
			logDebug(" In product Manager CategoryId["+categoryId+"]");
		return bbbCatalogTools.getParentCategory(categoryId,siteId);
	}
	
	/**
	 * This method returns immediate parent catId of the product
	 * @param productId,siteId
	 * @return string
	 */
	public String  getImmediateParentCategoryForProduct(String productId, String siteId) throws BBBSystemException,
			BBBBusinessException {
			logDebug(" In product Manager productId["+productId+"]");
		return bbbCatalogTools.getImmediateParentCategoryForProduct(productId,siteId);
	}

	/**
	 * Given  the Product id, site id returns product ImageVo
	 * @param string, string
	 * @return String
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public ImageVO getProductImages(String productId) throws BBBBusinessException, BBBSystemException {
			logDebug(" In product Manager pProductId["+productId+"]");
		return bbbCatalogTools.getProductImages(productId);
	}

	public List<CategoryVO> getProductRelatedCategories (String productId, String siteId) throws BBBBusinessException, BBBSystemException {
			logDebug(" In product Manager pProductId["+productId+"]");
		return bbbCatalogTools.getRelatedCategories(productId, siteId);
	}	
	
	 /**
     * Gets the category Recommendations .
     *
     * @param categoryId the category Id
     * @param productId the product Id
     * @return List<RecommendedCategoryVO>
     */
	public List<RecommendedCategoryVO> getCategoryRecommendation(String categoryId, String productId) {
		return bbbCatalogTools.getCategoryRecommendation(categoryId,productId);
	}
	 /**
     * Gets the product Details for Child and Siblings .
     *
     * @param productId the product Id
     * @return List<ProductVO>
     */
	public final List<ProductVO> getProductDetailsWithSiblings(final String pProductId) throws BBBBusinessException, BBBSystemException {
		
		return bbbCatalogTools.getProductDetailsWithSiblings(pProductId);
	}
	

	}
