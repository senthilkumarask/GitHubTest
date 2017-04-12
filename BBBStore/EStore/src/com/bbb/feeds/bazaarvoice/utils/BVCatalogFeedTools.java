package com.bbb.feeds.bazaarvoice.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.feeds.utils.BBBFeedTools;

import atg.adapter.gsa.GSARepository;
import atg.repository.RepositoryItem;

/**
 * Catalog feed helper class. Fetches data from Catalog repository based on
 * date.For full feed all data is fetched.For incremental feed data is fetched
 * based on last modified date
 *
 * @author njai13
 *
 */
public class BVCatalogFeedTools extends BBBFeedTools {
	
	private ArrayList<String> ignoreProductsList;

	public ArrayList<String> getIgnoreProductsList() {
		return ignoreProductsList;
	}

	public void setIgnoreProductsList(ArrayList<String> ignoreProductsList) {
		this.ignoreProductsList = ignoreProductsList;
	}
	private int collectionParentsBatchSize;
	private String collectionParentProductsSQL;
	
	public String getCollectionParentProductsSQL() {
		return collectionParentProductsSQL;
	}

	public void setCollectionParentProductsSQL(String collectionParentProductsSQL) {
		this.collectionParentProductsSQL = collectionParentProductsSQL;
	}

	public int getCollectionParentsBatchSize() {
		return collectionParentsBatchSize;
	}

	public void setCollectionParentsBatchSize(int collectionParentsBatchSize) {
		this.collectionParentsBatchSize = collectionParentsBatchSize;
	}

	/**
	 * This method fetches properties for category to be sent in Bazaarvoice feed if
	 * full feed is required or if last modified date is null ,full data for
	 * category is fetched.For incremental feed data that has been modified since
	 * last feed is only fetched
	 *
	 * @param isFullDataFeed
	 *          :if full feed is required=true else false
	 * @param lastModifiedDate
	 *          :date since last feed was run
	 * @return List of CertonaCategoryVo
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<CategoryVO> getCategoryDetails(final boolean isFullDataFeed, final Timestamp lastModifiedDate, final String siteId)
			throws BBBSystemException, BBBBusinessException {
		this.logDebug("BVCatalogFeedTools [getCategoryDetails]");
		final List<CategoryVO> catVOList = new ArrayList<CategoryVO>();
		final RepositoryItem[] catItems = this.getCatalogItemsForFeedGeneration(isFullDataFeed,lastModifiedDate,
											BBBCatalogConstants.CATEGORY_ITEM_DESCRIPTOR);
		if ((catItems != null) && (catItems.length > 0)) {
			for (final RepositoryItem catItem : catItems) {
				
				// BBBSL-2302. Categories other than the current site needs to be skipped as we need to send only site specific category details in respective site feed.
				if(BBBCoreConstants.SITE_BAB_US.equalsIgnoreCase(siteId)){
					if(!catItem.getRepositoryId().startsWith("1"))
							continue;
				}
				else if(BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId)){
					if(!catItem.getRepositoryId().startsWith("2"))
							continue;
				}
				else if(BBBCoreConstants.SITE_BBB.equalsIgnoreCase(siteId)){
					if(!catItem.getRepositoryId().startsWith("3"))
							continue;
				}
				
				logDebug("Adding category in BV Feed with category Id: " + catItem.getRepositoryId() + "for the site Id: " + siteId);
				final CategoryVO categoryVO = this.populateCategoryVO(catItem);
				if (categoryVO != null) {
					catVOList.add(categoryVO);
				}
			}
		} else {
			this.logDebug("No data available in repository for category feed");
			throw new BBBBusinessException(BBBCatalogErrorCodes.NO_DATA_FOR_CATEGORY_FEED,BBBCatalogErrorCodes.NO_DATA_FOR_CATEGORY_FEED);
		}
		this.logDebug("Exiting getCategoryDetails of BVCatalogFeedTools");
		return catVOList;
	}

	/**
	 * the method sets the properties in CertonaCategoryVo corresponding to a
	 * particular repository item
	 *
	 * @param categoryRepositoryItem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private CategoryVO populateCategoryVO(final RepositoryItem categoryRepoItem) {
		CategoryVO categoryVO = null;
		if (categoryRepoItem != null) {
			this.logDebug("BVCatalogFeedTools [populateCategoryVO] Fetch data forcategory Id:" + categoryRepoItem.getRepositoryId());
			categoryVO = this.setCatBasicproperties(categoryRepoItem);
		}

		return categoryVO;
	}
	/***
	 * The method sets basic properties of category for bazaarvoice feed
	 * @param categoryRepoItem
	 * @return CertonaCategoryVO
	 */
	private CategoryVO setCatBasicproperties(final RepositoryItem categoryRepoItem){
		final CategoryVO categoryVO = new CategoryVO();
		categoryVO.setCategoryId(categoryRepoItem.getRepositoryId());
		if (categoryRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME) != null) {
			categoryVO.setCategoryName((String) categoryRepoItem
					.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
		}

		final Object catImage =  categoryRepoItem
				.getPropertyValue(BBBCatalogConstants.SMALL_IMAGE_CATEGORY_PROPERTY_NAME);
		if (catImage != null) {
			categoryVO.setCategoryImage((String) catImage);
		}
		categoryVO.setCategoryPageUrl(this.getCategoryPageURL(categoryVO.getCategoryName(), categoryVO.getCategoryId()));
		final Object parentCatObj = categoryRepoItem
				.getPropertyValue(BBBCatalogConstants.FIXED_PARENT_CATEGORIES_PROPERTY_NAME);
		if(parentCatObj != null) {
			final Set<RepositoryItem> parentCategorySet = (Set<RepositoryItem>)parentCatObj;
			if(parentCategorySet.size() >0) {
				final Iterator<RepositoryItem> it = parentCategorySet.iterator();
				final List<String> parentCatList = new ArrayList<String>();
				while(it.hasNext()) {
					parentCatList.add(it.next().getRepositoryId());
				}
				categoryVO.setParentCategories(parentCatList);
			}
		}

		return categoryVO;
	}

	@Override
	public boolean isProductWebOffered(final RepositoryItem productRepoItem, final String siteId) {

		//return getCatalogTools().isProductActive(productRepoItem);
		//Object prdTranslations = productRepoItem.getPropertyValue(BBBCatalogConstants.PROD_TRANS_SITE_TRANS_PROPERTY_NAME);
		final Object obj = productRepoItem.getPropertyValue("webOffered");
		return obj == null ? false: (Boolean) obj;
	}


	/**
	 * This method fetches properties for Product to be sent in certona feed if
	 * full feed is required or if last modified date is null ,full data for
	 * Product is fetched.For incremental feed data that has been modified since
	 * last feed is only fetched
	 *
	 * @param isFullDataFeed
	 *          :if full feed is required=true else false
	 * @param lastModifiedDate
	 *          :date since last feed was run
	 * @return List of CertonaProductVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public Map<String, ProductVO> getProductDetails(final boolean isFullDataFeed, final Timestamp lastModifiedDate, final String siteId)
			throws BBBSystemException, BBBBusinessException {

		this.logDebug("BVCatalogFeedTools [getProductDetails]");
		final Map<String, ProductVO> products = new HashMap<String, ProductVO>();
		final RepositoryItem[] productItems = this.getCatalogItemsForFeedGeneration(isFullDataFeed,lastModifiedDate,
														BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
		if ((productItems != null) && (productItems.length > 0)) {

			for (final RepositoryItem productItem : productItems) {
				final Object prdTranslations = productItem.getPropertyValue(BBBCatalogConstants.PROD_TRANS_SITE_TRANS_PROPERTY_NAME);
				//Commented as a fix for BBBSL-1888
				/*if(!isProductWebOffered(productItems[i], siteId)) {
					continue;
				}*/
				if(this.getIgnoreProductsList() == null || !this.getIgnoreProductsList().contains(productItem.getPropertyValue(BBBCoreConstants.ID))){
					final ProductVO productVO = this.populateProductVO(productItem, siteId);
					if (productVO != null) {
						products.put(productVO.getProductId(), productVO);
					}
				}
			}
			this.populateCollectionChildParentProducts(products);
		} else {
			this.logDebug("No data available in repository for Product feed");
			throw new BBBBusinessException(BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
		}
		this.logDebug("Exiting getProductDetails of BVCatalogFeedTools");
		return products;
	}
	
	private void populateCollectionChildParentProducts(Map<String, ProductVO> products){
		PreparedStatement preparedStatement = null;
		int batchInitalIndex = 0;
		int batchFinalIndex = getCollectionParentsBatchSize();
		String parentProductId = null;
		String childProductId = null;
		ProductVO currentChildProduct = null;
		Set<String> collectionChildParentProducts = null;
		int batchResultSize = 0, currentBatchCount=0;
		Connection connection = null;
		ResultSet resultSet = null;
		try{
			while(true){
				connection = ((GSARepository)getCatalogRepository()).getDataSource().getConnection();
				logInfo("Current Batch count is " + currentBatchCount + ". InitialBatchIndex is " + batchInitalIndex + ". BatchFinalIndex is " + batchFinalIndex);
				preparedStatement = connection.prepareStatement(this.getCollectionParentProductsSQL());
				preparedStatement.clearParameters();
				preparedStatement.setInt(1, batchFinalIndex);
				preparedStatement.setInt(2, batchInitalIndex);
				preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
				preparedStatement.setMaxRows(getCollectionParentsBatchSize());
				resultSet = preparedStatement.executeQuery();
				while(resultSet.next()){
					parentProductId = resultSet.getString(BBBCatalogConstants.PARENT_PRODUCT_COLUMN_NAME);
					childProductId = resultSet.getString(BBBCatalogConstants.CHILD_PRODUCT_COLUMN_NAME);
					currentChildProduct = products.get(childProductId);
					if(isLoggingDebug()){
						logDebug("Assigning parent products to "+ childProductId);
					}
					if(currentChildProduct!=null){
						logDebug(childProductId + " was found in this feed of batch:- " + currentBatchCount);
						collectionChildParentProducts = currentChildProduct.getCollectionParentProductIds();
						collectionChildParentProducts.add(parentProductId);
					}
					batchResultSize ++;
				}
				resultSet.close();
				preparedStatement.close();
				connection.close();
				if(batchResultSize<getCollectionParentsBatchSize()){
					logInfo("Batch termination has been detected at " + (currentBatchCount + 1) + "BatchResultSize is " + batchResultSize);
					break;
				}
				batchResultSize = 0;
				batchFinalIndex += getCollectionParentsBatchSize();
				batchInitalIndex += getCollectionParentsBatchSize();
				currentBatchCount++;
			}
		} catch (SQLException e) {
			logError("Error occurred while assigning parent products to child collection products.",e);
		}finally {
			try {
				if(resultSet!=null && !resultSet.isClosed())
					resultSet.close();
				if(preparedStatement!=null && !preparedStatement.isClosed())
					preparedStatement.close();
				if(connection!=null && !connection.isClosed())
					connection.close();
			} catch (SQLException e) {
				logError("Error occurred while assigning parent products to child collection products out of loop.", e);
			}
		}
	}

	/**
	 * the method sets the properties in ProductVO corresponding to a
	 * particular repository item
	 *
	 * @param prodRepoItem
	 * @return ProductVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	private ProductVO populateProductVO(final RepositoryItem prodRepoItem, String siteId)
			throws BBBSystemException, BBBBusinessException {
		if (prodRepoItem == null) {
			return null;
		}

		this.logDebug("BVCatalogFeedTools [populateProductVO] Fetch data for product Id:" + prodRepoItem.getRepositoryId());
		final ProductVO productVO = new ProductVO();
		
		// BBBSL-2302. Corrected the logic to set disable flag in BV feed. Making the disable flag site specific.
		final boolean dateIsActive = isPrdActiveForStartEndDate(prodRepoItem);
		
		Set prodTranslations = (Set)prodRepoItem.getPropertyValue(BBBCatalogConstants.PROD_TRANS_SITE_TRANS_PROPERTY_NAME);
		final boolean isProdDisabled = this.isProductDisabled(prodTranslations, siteId, prodRepoItem);
		logDebug("Product's Disabled property is: " + isProdDisabled + " for product: " + prodRepoItem.getRepositoryId() + " for site: " + siteId);
		
		final boolean webOffered = this.isProductWebOffered(prodTranslations, siteId, prodRepoItem);
		logDebug("Product's Web offered property is: " + webOffered + " for product: " + prodRepoItem.getRepositoryId() + " for site: " + siteId);
		if(isProdDisabled){
			productVO.setBvRemoved(Boolean.TRUE);
		} else if(!dateIsActive || !webOffered){
			productVO.setDisabled(Boolean.TRUE);
		} else {
			productVO.setDisabled(Boolean.FALSE);
		}
		
		//Added for BBBSL-1888
		productVO.setProductId(prodRepoItem.getRepositoryId());
		productVO.setName((String) prodRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_PRODUCT_PROPERTY_NAME));
		final Set<RepositoryItem> parentCategorySet = this.getParentCategories(prodRepoItem);
		if((parentCategorySet != null)
				&& (parentCategorySet.size() >0)) {
			final Iterator<RepositoryItem> it = parentCategorySet.iterator();
			final List<String> parentCatList = new ArrayList<String>();
			while(it.hasNext()) {
				parentCatList.add(it.next().getRepositoryId());
			}
			productVO.setParentCategoryList(parentCatList);
		}
		productVO.setProductPageUrl(this.getProductPageURL(productVO.getName(), productVO.getProductId()));
		productVO.setImageUrl((String) prodRepoItem
				.getPropertyValue("scene7URL"));
		final List<RepositoryItem> skus = this.getChildSkus(prodRepoItem);

		// Added code for BBBSL-1885. jda and UPC/EAN property to productVo.
		Boolean isCollection = (Boolean)prodRepoItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME);
		if(isCollection == null){
			isCollection=false;
		}
		productVO.setCollection(isCollection);
		if(isCollection) {
			@SuppressWarnings("unchecked")
			final
			List<RepositoryItem> childproducts=(List<RepositoryItem>)prodRepoItem.getPropertyValue(BBBCatalogConstants.CHILD_PRODUCTS_RELATION_PRODUCT_PROPERTY_NAME);
			if((childproducts!=null) && (childproducts.size() > 0)) {
				final RepositoryItem firstChildProduct=childproducts.get(0);
				final RepositoryItem productItem=(RepositoryItem)firstChildProduct.getPropertyValue(BBBCatalogConstants.PRODUCT_ID_PRODUCT_PROPERTY_NAME);
				final List<RepositoryItem> childSkus = this.getChildSkus(productItem);
				if((childSkus!=null) && (childSkus.size() > 0)){
					final RepositoryItem jda=(RepositoryItem)childSkus.get(0).getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME);
					if(jda!=null) {
						productVO.setDepartment(jda.getRepositoryId());
					}
					final String upcCode = (String) childSkus.get(0).getPropertyValue(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME);
					if(!StringUtils.isBlank(upcCode)){
					processUpcCode(upcCode, productVO);
					}
				}
			}
		} else {
			if((skus !=null)
					&& (skus.size()>0)) {
				//productVO.setEan(skus.get(0).getPropertyValue(BBBCatalogConstants.E));
				//productVO.setModelNumber("");
				//productVO.setMfPartNumber("");

				// Get UPC code for the sku. BBBSL-1886.
				final String upcCode = (String) skus.get(0).getPropertyValue(BBBCatalogConstants.UPC_SKU_PROPERTY_NAME);
				if(!StringUtils.isBlank(upcCode)){
					processUpcCode(upcCode, productVO);
				}
				final RepositoryItem jda=(RepositoryItem)skus.get(0).getPropertyValue(BBBCatalogConstants.JDA_DEPT_SKU_PROPERTY_NAME);
				if(jda!=null) {
					productVO.setDepartment(jda.getRepositoryId());
				}
				//productVO.setIsbn("");
			}
		}
		productVO.setShortDescription((String) prodRepoItem
				.getPropertyValue(BBBCatalogConstants.DESCRIPTION_PRODUCT_PROPERTY_NAME));
		final RepositoryItem brandRepoItem = (RepositoryItem) prodRepoItem
				.getPropertyValue(BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR);
		if (brandRepoItem != null) {
			productVO.setBrandId(brandRepoItem.getRepositoryId());
		}
		//regional parameters names, descriptions, productPage URL, image urls
		return productVO;
	}

	/**
	 * BBBSL-2302. Method to check product site translated web offered property. Returns
	 * true if product is web offered. Also if no translation is found, then returns
	 * the default value which is set for US site
	 * 
	 * @param prodTranslations
	 * @param siteId
	 * @param prodRepoItem
	 * @return
	 */
	private boolean isProductWebOffered(Set<RepositoryItem> prodTranslations,
			String siteId, RepositoryItem prodRepoItem) {

		boolean webOffered = false;
		if(BBBCoreConstants.SITE_BAB_US.equalsIgnoreCase(siteId)){
			if(prodRepoItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) != null) {
				webOffered = ((Boolean)(prodRepoItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME))).booleanValue();
			}
			return webOffered;
		}
		if(prodTranslations != null && !prodTranslations.isEmpty()){
			for(RepositoryItem item : prodTranslations) {
				if(BBBCoreConstants.SITE_BBB.equalsIgnoreCase(siteId)){
					if(item.getRepositoryId().equalsIgnoreCase(BBBCoreConstants._2_EN_US_WEB_OFFERED_Y)){
						return (!webOffered);
					} else if(item.getRepositoryId().equalsIgnoreCase(BBBCoreConstants._2_EN_US_WEB_OFFERED_N)){
						return webOffered;
					}
				} else if(BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId)){
					if(item.getRepositoryId().equalsIgnoreCase(BBBCoreConstants._3_EN_US_WEB_OFFERED_Y)){
						return (!webOffered);
					} else if(item.getRepositoryId().equalsIgnoreCase(BBBCoreConstants._3_EN_US_WEB_OFFERED_N)){
						return webOffered;
					}
				}
			}
		}
		
		logDebug("No translation found for Web offered property for the product " + prodRepoItem.getRepositoryId() + " for site: " + siteId);
		if(prodRepoItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) != null) {
			webOffered = ((Boolean)(prodRepoItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME))).booleanValue();
		}
		return webOffered;
	}

	/**
	 * BBBSL-2302. Method to check product site translated disable property. Returns
	 * true if product is disabled. Also if no translation is found, then returns
	 * the default value which is set for US site
	 * 
	 * @param prodTranslations
	 * @param siteId
	 * @param prodRepoItem
	 * @return
	 */
	private boolean isProductDisabled(Set<RepositoryItem> prodTranslations, String siteId, RepositoryItem prodRepoItem) {

		boolean disabled = true;
		if(BBBCoreConstants.SITE_BAB_US.equalsIgnoreCase(siteId)){
			if(prodRepoItem.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) != null) {
				disabled = ((Boolean)(prodRepoItem.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME))).booleanValue();
			}
			return disabled;
		}
		if(prodTranslations != null && !prodTranslations.isEmpty()){

			for(RepositoryItem item : prodTranslations){
				if(BBBCoreConstants.SITE_BBB.equalsIgnoreCase(siteId)){
					if(item.getRepositoryId().equalsIgnoreCase(BBBCoreConstants._2_EN_US_PROD_DISABLE_Y)){
						return disabled;
					} else if(item.getRepositoryId().equalsIgnoreCase(BBBCoreConstants._2_EN_US_PROD_DISABLE_N)){
						return (!disabled);
					}
				} else if(BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId)){
					if(item.getRepositoryId().equalsIgnoreCase(BBBCoreConstants._3_EN_US_PROD_DISABLE_Y)){
						return disabled;
					} else if(item.getRepositoryId().equalsIgnoreCase(BBBCoreConstants._3_EN_US_PROD_DISABLE_N)){
						return (!disabled);
					}
				}
			}
		}
		
		logDebug("No translation found for Disabled property for the product " + prodRepoItem.getRepositoryId() + " for site: " + siteId);
		if(prodRepoItem.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME) != null) {
			disabled = ((Boolean)(prodRepoItem.getPropertyValue(BBBCatalogConstants.DISABLE_PRODUCT_PROPERTY_NAME))).booleanValue();
		}
		return disabled;
	}

	/**
	 * This method checks if upcCode length is greater than 12, then sets
	 * upc code in EAN property of the product, else append the required
	 * number of zeros to upc code to make it to length 12 and sets in upc
	 * property of the product. CR BBBSL-1886
	 * @param upcCode
	 * @param productVO
	 */
	private static void processUpcCode(String upcCode, final ProductVO productVO) {
		if (upcCode.length() < 12) {
			upcCode = StringUtils.leftPad(upcCode, 12, "0");
			productVO.setUpcCode(upcCode);
		}
		else if (upcCode.length() == 12) {
			productVO.setUpcCode(upcCode);
		}
		else if (upcCode.length() > 12) {
			productVO.setEan(upcCode);
		}
	}

	/**
	 * The method returns if a product is active or not based
	 * on start date and end date check.
	 * Note that there are 4 paramerts to check if a product is alive
	 * Start date > current date
	 * End date < current date
	 * disable false
	 * weboffered true
	 * out of this start and end date is common for all ste but disable and weboffered flag is
	 * site dependent.Hence check for start and end date is made here
	 * and rest of the check will be made afterwards
	 * @param prodRepoItem
	 * @return
	 */

	private boolean isPrdActiveForStartEndDate(final RepositoryItem prodRepoItem){
		boolean dateIsActive = true;
		final Date currentDate = new Date();
		// get the start and end date of the product to determine if the product is
		// active or not
		if ((prodRepoItem.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME) != null)
				&& (prodRepoItem.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME) != null)) {
			final Date startDate = (Date) prodRepoItem.getPropertyValue(BBBCatalogConstants.START_DATE_PRODUCT_PROPERTY_NAME);
			final Date endDate = (Date) prodRepoItem.getPropertyValue(BBBCatalogConstants.END_DATE_PRODUCT_PROPERTY_NAME);
			if ((((endDate != null) && currentDate.after(endDate)) || ((startDate != null) && currentDate.before(startDate)))) {
				dateIsActive = false;
			}
		}
		return dateIsActive;
	}

	/**
	 * This method fetches properties for brand to be sent in BV feed if
	 * full feed is required or if last modified date is null ,full data for
	 * Product is fetched.For incremental feed data that has been modified since
	 * last feed is only fetched
	 *
	 * @param isFullDataFeed
	 *          :if full feed is required=true else false
	 * @param lastModifiedDate
	 *          :date since last feed was run
	 * @return List of BrandVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public List<BrandVO> getBrandDetails(final boolean isFullDataFeed, final Timestamp lastModifiedDate, final String siteId)
			throws BBBSystemException, BBBBusinessException {

		this.logDebug("BVCatalogFeedTools [getBrandDetails]");
		final List<BrandVO> brandVOList = new ArrayList<BrandVO>();
		final RepositoryItem[] brandItems = this.getBrandItemsForFeedGeneration(isFullDataFeed, lastModifiedDate, siteId);
		if ((brandItems != null) && (brandItems.length > 0)) {

			for (final RepositoryItem brandItem : brandItems) {
				final BrandVO brandVO = this.populateBrandVO(brandItem);
				if (brandVO != null) {
					brandVOList.add(brandVO);
				}
			}
		} else {
			this.logDebug("No data available in repository for Product feed");
			throw new BBBBusinessException(BBBCatalogErrorCodes.NO_DATA_FOR_PRODUCT_FEED);
		}
		this.logDebug("Exiting getProductDetails of BVCatalogFeedTools");
		return brandVOList;
	}

	/**
	 * the method sets the properties in BrandVO corresponding to a
	 * particular repository item
	 *
	 * @param brandRepoItem
	 * @return BrandVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	private BrandVO populateBrandVO(final RepositoryItem brandRepoItem) throws BBBSystemException,
	BBBBusinessException {
		final BrandVO brandVO = new BrandVO();
		if (brandRepoItem != null) {
			this.logDebug("BVCatalogFeedTools [populateProductVO] Fetch data for product Id:" + brandRepoItem.getRepositoryId());
			brandVO.setBrandId(brandRepoItem.getRepositoryId());
			brandVO.setBrandName((String) brandRepoItem.getPropertyValue(BBBCatalogConstants.BRAND_NAME_BRAND_PROPERTY_NAME));
		}
		return brandVO;
	}

}
