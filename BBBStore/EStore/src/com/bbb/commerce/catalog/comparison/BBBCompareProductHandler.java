package com.bbb.commerce.catalog.comparison;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.catalog.comparison.ProductListHandler;
import atg.droplet.DropletException;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.comparison.vo.CompareProductEntryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

/**
 * @author magga3
 * 
 */
public class BBBCompareProductHandler extends ProductListHandler {

	// Variables for form Exception : Start

	private static final String ERR_PRODUCT_NOT_ACTIVE = "err_product_not_active";
	private static final String NO_PRODUCT_LIST = "err_no_product_list";
	private static final String ERR_REMOVING_PRODUCT = "err_removing_product";
	private static final String ERR_FETCHING_PRODUCT_INFO = "err_fetching_product_info";
	private static final String ERR_ADDING_PRODUCT = "err_adding_product";
	private static final String ERR_INVALID_PRODUCT_ID = "err_invalid_product_id";
	private String comparisonPage;

	private BBBProductComparisonList productComparisonList;
	private MutableRepository catalogRepository;
	private LblTxtTemplateManager messageHandler;
	private BBBCatalogTools catalogTools;
	private String successURL;
    private String collectionProductId; 
	/**
	 * @return catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return messageHandler
	 */
	public LblTxtTemplateManager getMessageHandler() {
		return this.messageHandler;
	}

	/**
	 * @param messageHandler
	 */
	public void setMessageHandler(LblTxtTemplateManager messageHandler) {
		this.messageHandler = messageHandler;
	}

	/**
	 * Product Comparison List - OOB Component to maintain Comparison List
	 * 
	 * @return productComparisonList
	 */
	public BBBProductComparisonList getProductComparisonList() {
		return this.productComparisonList;
	}

	/**
	 * @param productComparisonList
	 */
	public void setProductComparisonList(
			BBBProductComparisonList productComparisonList) {
		this.productComparisonList = productComparisonList;
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
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	/**
	 * Override the handleAddProduct method to add productId to comparison list
	 * after checking whether the list already contains input product or not
	 * 
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 * @throws ServletException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * 
	 * @return true if product added otherwise false
	 * */
	@Override
	public boolean handleAddProduct(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.handleAddProduct() : Start");
		}
     if(this.getProductID()==null) {
		this.setProductID(pRequest.getParameter("prodIdToCompare"));
     }
		this.setSkuID(pRequest.getParameter("skuIdToCompare"));
        this.setCollectionProductId(pRequest.getParameter("collectionProduct"));
		boolean result = false;
		if (getProductComparisonList() == null) {
			logError("BBBCompareProductHandler.handleAddProduct() : Product Comparison List is null");
			addFormException(new DropletException(this.getMessageHandler()
					.getErrMsg(NO_PRODUCT_LIST, "EN", null), NO_PRODUCT_LIST));
			return false;
		}
		String skuId = this.getSkuID();
		String productId=this.getProductID();
       if(this.getCollectionProductId()!=null)
       {
    	     preAddProduct(pRequest, pResponse);
	    	 String collectionProductId= this.getCollectionProductId();
	    	 BBBProductComparisonList bbbProductComparisonList = getProductComparisonList();
				    	 List<CompareProductEntryVO> compareProductsList = bbbProductComparisonList.getItems();
				    	 List<CompareProductEntryVO> compareProductsListDummy= new ArrayList<CompareProductEntryVO>();
				    	 if(skuId!=null)
				    	 {
				    		 for(CompareProductEntryVO productToCompare : compareProductsList)
				    		 {
				    			 if((skuId.equals(productToCompare.getSkuId()) || skuId.equals(productToCompare.getDefaultSkuId())) && productToCompare.getProductId().equals(productId))
				    			 {
				    				 compareProductsListDummy.add(productToCompare);
				    			 }
				    		 }
				    		 for(CompareProductEntryVO productToCompare : compareProductsListDummy)
				    		 {
				    			 if((skuId.equals(productToCompare.getSkuId()) || skuId.equals(productToCompare.getDefaultSkuId())) && productToCompare.getProductId().equals(productId))
				    			 {
				    				 removeProduct(pRequest,pResponse,productToCompare.getSkuId(),productToCompare.getProductId());
				    			 }
				    		 } 
				    		 
				    	 }
				    	 for (CompareProductEntryVO productToCompare : compareProductsList) {      
								if (productToCompare.getProductId().equals(this.getCollectionProductId())) {
									productToCompare.setProductId(productId);
									productToCompare.setSkuId(skuId);
									skuProductImage(skuId,productToCompare);
									break;
								}
							}
			    	 postAddProduct(pRequest, pResponse);
			    	 String pSuccessURL = this.getSuccessURL();
			    	 this.checkFormRedirect(pSuccessURL, pSuccessURL, pRequest,
								pResponse);
       }		
       else	if (this.getSkuID() != null) {
			preAddProduct(pRequest, pResponse);
			BBBProductComparisonList bbbProductComparisonList = getProductComparisonList();
			List<CompareProductEntryVO> compareProductsList = bbbProductComparisonList
					.getItems();
			for (CompareProductEntryVO productToCompare : compareProductsList) {
				if (productToCompare.getProductId().equals(this.getProductID())) {
					productToCompare.setProductId(productId);
					productToCompare.setSkuId(skuId);
					productToCompare.setInCartFlag(getCatalogTools().getSkuIncartFlag(skuId));
					skuProductImage(skuId,productToCompare);
					break;
				}
			}
			postAddProduct(pRequest, pResponse);
			String pSuccessURL = this.getSuccessURL();
			this.checkFormRedirect(pSuccessURL, pSuccessURL, pRequest,
					pResponse);
		} 
        else {
			preAddProduct(pRequest, pResponse);
			if (getFormError()) {
				return false;
			}
			if (isLoggingDebug()) {
				logDebug("BBBCompareProductHandler.handleAddProduct() : Adding Product Id : "
						+ this.getProductID());
			}
			CompareProductEntryVO entryVO = populateVO();
			if (entryVO != null) {
				result = getProductComparisonList().add(entryVO);
			}
			if (!result) {
				logError("BBBCompareProductHandler.handleAddProduct() : Error while adding Product to comparison List");
				addFormException(new DropletException(this.getMessageHandler()
						.getErrMsg(ERR_ADDING_PRODUCT, "EN", null),
						ERR_ADDING_PRODUCT));
				return false;
			}
			postAddProduct(pRequest, pResponse);

			if (isLoggingDebug()) {
				logDebug("BBBCompareProductHandler.handleAddProduct() : End");
			}
		}

		return true;
	}

	/**
	 * Override the preAddProduct method to validate product Id and Sku Id
	 * Validates product and sku from productCatalog
	 * 
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 * @throws ServletException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * 
	 * */
	@Override
	public void preAddProduct(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.preAddProduct() : Start :: Validating ProductId: "
					+ this.getProductID());
		}
		if (BBBUtility.isNotEmpty(this.getProductID())) {
			try {
				boolean isActive = getCatalogTools().isProductActive(
						this.getProductID());
				if (!isActive) {
					addFormException(new DropletException(this
							.getMessageHandler().getErrMsg(
									ERR_PRODUCT_NOT_ACTIVE, "EN", null),
							ERR_PRODUCT_NOT_ACTIVE));
				}
			} catch (BBBSystemException e) {
				logError("BBBCompareProductHandler.preAddProduct() : Repository Exception while validating product id "
						+ this.getProductID() + "Exception: " + e);
				addFormException(new DropletException(this.getMessageHandler()
						.getErrMsg(ERR_FETCHING_PRODUCT_INFO, "EN", null),
						ERR_FETCHING_PRODUCT_INFO));
			} catch (BBBBusinessException e) {
				logError("BBBCompareProductHandler.preAddProduct() : Repository Exception while validating product id "
						+ this.getProductID() + "Exception: " + e);
				addFormException(new DropletException(this.getMessageHandler()
						.getErrMsg(ERR_FETCHING_PRODUCT_INFO, "EN", null),
						ERR_FETCHING_PRODUCT_INFO));
			}
		} else {
			logError("BBBCompareProductHandler.preAddProduct() : Product Id is null");
			addFormException(new DropletException(this.getMessageHandler()
					.getErrMsg(ERR_INVALID_PRODUCT_ID, "EN", null),
					ERR_INVALID_PRODUCT_ID));
		}

		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.preAddProduct() : End");
		}
	}

	/**
	 * Override the postAddProduct method to save the comma separated product
	 * ids of the products currently in the compare drawer in the cookie. The
	 * cookie is updated each time the product is added in the drawer.
	 * 
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 * @throws ServletException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * 
	 * */
	@Override
	public void postAddProduct(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		String productList = "";
		String skuList = "";
		List<CompareProductEntryVO> compareProductsList = getProductComparisonList()
				.getItems();
		for (CompareProductEntryVO compareProduct : compareProductsList) {
			productList = productList + compareProduct.getProductId() + ",";
			if (compareProduct.getSkuId() != null
					&& !BBBUtility.isEmpty(compareProduct.getSkuId())) {
				skuList = skuList + compareProduct.getSkuId() + ",";

			} else {
				skuList = skuList + "null" + ",";
			}
		}
		if (!BBBUtility.isEmpty(productList) && !BBBUtility.isEmpty(skuList)) {
			productList = productList.substring(0, productList.length() - 1);
			skuList = skuList.substring(0, skuList.length() - 1);
			getProductComparisonList().addListToCookie(pRequest, pResponse,
					productList, skuList, false);
		}
	}

	/**
	 * Override the handleRemoveProduct method to remove product from comparison
	 * List given we have product Id
	 * 
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 * @throws ServletException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * 
	 * @return true if product removed otherwise false
	 * */
	@Override
	public boolean handleRemoveProduct(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.handleRemoveProduct() : Start");
		}
		boolean result = false;
		if (getProductComparisonList() == null) {
			logError("BBBCompareProductHandler.handleRemoveProduct() : Product Comparison List is null");
			addFormException(new DropletException(this.getMessageHandler()
					.getErrMsg(NO_PRODUCT_LIST, "EN", null), NO_PRODUCT_LIST));
			return false;
		}
		preRemoveProduct(pRequest, pResponse);
		if (getFormError()) {
			return false;
		}
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.handleRemoveProduct() : Removing Product Id : "
					+ this.getProductID());
		}
		CompareProductEntryVO entryVO = populateVO();
		if (entryVO != null) {
			result = getProductComparisonList().removeItem(entryVO, pRequest,
					pResponse);
		}
		if (!result) {
			logError("BBBCompareProductHandler.handleRemoveProduct() : Error while removing Product from comparison List");
			addFormException(new DropletException(this.getMessageHandler()
					.getErrMsg(ERR_REMOVING_PRODUCT, "EN", null),
					ERR_REMOVING_PRODUCT));
			return false;
		}
		postRemoveProduct(pRequest, pResponse);
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.handleRemoveProduct() : End");
		}
		return true;
	}

	/**
	 * Override the preRemoveProduct method to validate product Id Validates
	 * product from productCatalog
	 * 
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 * @throws ServletException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * */
	@Override
	public void preRemoveProduct(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.preRemoveProduct() : Start");
		}
		if (BBBUtility.isNotEmpty(this.getProductID())) {
			try {
				boolean isActive = getCatalogTools().isProductActive(
						this.getProductID());
				if (!isActive) {
					addFormException(new DropletException(this
							.getMessageHandler().getErrMsg(
									ERR_PRODUCT_NOT_ACTIVE, "EN", null),
							ERR_PRODUCT_NOT_ACTIVE));
				}
			} catch (BBBSystemException e) {
				logError("BBBCompareProductHandler.preRemoveProduct() : Repository Exception while validating product id "
						+ this.getProductID() + "Exception: " + e);
				addFormException(new DropletException(this.getMessageHandler()
						.getErrMsg(ERR_FETCHING_PRODUCT_INFO, "EN", null),
						ERR_FETCHING_PRODUCT_INFO));
			} catch (BBBBusinessException e) {
				logError("BBBCompareProductHandler.preRemoveProduct() : Repository Exception while validating product id "
						+ this.getProductID() + "Exception: " + e);
				addFormException(new DropletException(this.getMessageHandler()
						.getErrMsg(ERR_FETCHING_PRODUCT_INFO, "EN", null),
						ERR_FETCHING_PRODUCT_INFO));
			}
		} else {
			logError("BBBCompareProductHandler.preRemoveProduct() : Product Id is null");
			addFormException(new DropletException(this.getMessageHandler()
					.getErrMsg(ERR_INVALID_PRODUCT_ID, "EN", null),
					ERR_INVALID_PRODUCT_ID));
		}
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.preRemoveProduct() : End");
		}
	}

	/**
	 * Override the handleClearList method to remove all products from
	 * comparison List
	 * 
	 * 
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 * @throws ServletException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * 
	 * @return true if all products are removed otherwise false
	 * */
	@Override
	public boolean handleClearList(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.handleClearList() : Start :: Clearing all products from comparison List");
		}
		if (getProductComparisonList() == null) {
			logError("BBBCompareProductHandler.handleClearList() : Product Comparison List is null");
			addFormException(new DropletException(this.getMessageHandler()
					.getErrMsg(NO_PRODUCT_LIST, "EN", null), NO_PRODUCT_LIST));
			return false;
		}
		getProductComparisonList().clear();

		postClearList(pRequest, pResponse);
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.handleClearList() : End");
		}
		return true;
	}

	/**
	 * Override the postClearList method to delete the cookie from browser if
	 * the compare drawer is cleared.
	 * 
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @param pResponse
	 *            - DynamoHttpServletResponse
	 * @throws ServletException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * 
	 * */
	@Override
	public void postClearList(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		String productList = "";
		String skuList = "";
		boolean killCookie = true;
		getProductComparisonList().addListToCookie(pRequest, pResponse,
				productList, skuList, killCookie);
	}

	/**
	 * Populates compareProductEntryVO with given productID Image Path to show
	 * in drawer
	 * 
	 * @return CompareProductEntryVO
	 */
	private CompareProductEntryVO populateVO() {
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.populateVO() : Start");
		}
		CompareProductEntryVO compareProductEntryVO = new CompareProductEntryVO();
		compareProductEntryVO.setProductId(this.getProductID());
		if (getSkuID() != null) {
			compareProductEntryVO.setSkuId(this.getSkuID());
			try {
				RepositoryItem skuRepositoryItem = getCatalogRepository()
						.getItem(getSkuID(),
								BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
				if (skuRepositoryItem != null
						&& skuRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) != null) {

					String ImagePath = (String) skuRepositoryItem
							.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME);
					List<String> scene7KeysList = getCatalogTools()
							.getAllValuesForKey(
									BBBCoreConstants.THIRD_PARTY_URL,
									BBBCoreConstants.SCENE7_URL);
					String scene7Path = scene7KeysList.get(0);
					compareProductEntryVO.setImagePath(scene7Path
							+ BBBCoreConstants.SLASH + ImagePath);
					compareProductEntryVO.getImagePath();
					// setting product Name 
                    compareProductEntryVO.setProductName((String)skuRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));
                    //setting LTL product flag 
                    if(skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) != null){
                    	compareProductEntryVO.setLtlProduct((Boolean) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME));
                    }

				}
			} catch (RepositoryException e) {
				logDebug("unable to retrieve item from Sku Repository for skuId"+getSkuID());

			} catch (BBBSystemException e) {
				logError("BBBCompareProductHandler.populateVO() : System Exception while fetching scene7 key "+ e);
			} catch (BBBBusinessException e) {
				logError("BBBCompareProductHandler.populateVO() : Business Exception while fetching scene7 key "+ e);
			}
		} else {
			RepositoryItem productRepositoryItem;
			try {
				productRepositoryItem = this.getCatalogRepository().getItem(
						this.getProductID(),
						BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
				if (productRepositoryItem != null
						&& productRepositoryItem
								.getPropertyValue(BBBCatalogConstants.COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME) != null) {
					final String imagePath = (String) productRepositoryItem
							.getPropertyValue(BBBCatalogConstants.COLLECTION_THUMBNAIL_PRODUCT_PROPERTY_NAME);
					List<String> scene7KeysList = getCatalogTools()
							.getAllValuesForKey(
									BBBCoreConstants.THIRD_PARTY_URL,
									BBBCoreConstants.SCENE7_URL);
					String scene7Path = scene7KeysList.get(0);
					compareProductEntryVO.setImagePath(scene7Path
							+ BBBCoreConstants.SLASH + imagePath);
					compareProductEntryVO.setProductName((String)productRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));

					// setting LTL flag 
                    
                    if(productRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME) != null){ 

                            compareProductEntryVO.setLtlProduct((Boolean) productRepositoryItem.getPropertyValue(BBBCatalogConstants.LTL_PRODUCT_PROPERTY_NAME));                                           

                    }else{ 
                            compareProductEntryVO.setLtlProduct(false); 
                    }


				}
			} catch (RepositoryException e) {
				logError("BBBCompareProductHandler.populateVO() : Repository Exception while getting product Info product id "
						+ e);
				addFormException(new DropletException(this.getMessageHandler()
						.getErrMsg(ERR_FETCHING_PRODUCT_INFO, "EN", null),
						ERR_FETCHING_PRODUCT_INFO));
			} catch (BBBSystemException e) {
				logError("BBBCompareProductHandler.populateVO() : System Exception while fetching scene7 key "
						+ e);
				addFormException(new DropletException(this.getMessageHandler()
						.getErrMsg(ERR_FETCHING_PRODUCT_INFO, "EN", null),
						ERR_FETCHING_PRODUCT_INFO));
			} catch (BBBBusinessException e) {
				logError("BBBCompareProductHandler.populateVO() : Business Exception while fetching scene7 key "+ e);
				addFormException(new DropletException(this.getMessageHandler()
						.getErrMsg(ERR_FETCHING_PRODUCT_INFO, "EN", null),
						ERR_FETCHING_PRODUCT_INFO));
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.populateVO() : End");
		}

		return compareProductEntryVO;
	}

	

	public String getSuccessURL() {
		return successURL;
	}

	public void setSuccessURL(String successURL) {
		this.successURL = successURL;
	}

	public String getComparisonPage() {
		return comparisonPage;
	}

	public void setComparisonPage(String comparisonPage) {
		this.comparisonPage = comparisonPage;
	}

	public String getCollectionProductId() {
		return collectionProductId;
	}

	public void setCollectionProductId(String collectionProductId) {
		this.collectionProductId = collectionProductId;
	}
	public boolean removeProduct(DynamoHttpServletRequest pRequest,DynamoHttpServletResponse pResponse,String skuId,String productId) throws ServletException,
	IOException
	{
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.handleRemoveProduct() : Start");
		}
		boolean result = false;
		if (getProductComparisonList() == null) {
			logError("BBBCompareProductHandler.handleRemoveProduct() : Product Comparison List is null");
			return false;
		}
		
		
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.handleRemoveProduct() : Removing Product Id : "
					+ this.getProductID());
		}
		CompareProductEntryVO entryVO = new CompareProductEntryVO();
		entryVO.setSkuId(skuId);
		entryVO.setProductId(productId);
		
			result = getProductComparisonList().removeItem(entryVO, pRequest,
					pResponse);
		
		if (!result) {
			logError("BBBCompareProductHandler.handleRemoveProduct() : Error while removing Product from comparison List");
			return false;
		}
		postRemoveProduct(pRequest, pResponse);
		if (isLoggingDebug()) {
			logDebug("BBBCompareProductHandler.handleRemoveProduct() : End");
		}
		return true;
	}
	
	public void skuProductImage(String skuId,CompareProductEntryVO productToCompare)
	{
		try {
			RepositoryItem skuRepositoryItem = getCatalogRepository()
					.getItem(skuId,
							BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
			if (skuRepositoryItem != null
					&& skuRepositoryItem.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME) != null) {

				String ImagePath = (String) skuRepositoryItem
						.getPropertyValue(BBBCatalogConstants.MEDIUM_IMAGE_IMAGE_PROPERTY_NAME);
				List<String> scene7KeysList = getCatalogTools()
						.getAllValuesForKey(
								BBBCoreConstants.THIRD_PARTY_URL,
								BBBCoreConstants.SCENE7_URL);
				String scene7Path = scene7KeysList.get(0);
				productToCompare.setImagePath(scene7Path
						+ BBBCoreConstants.SLASH + ImagePath);
				// product Name added to the CompareProductEntryVO

                productToCompare.setProductName((String)skuRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_NAME_CATEGORY_PROPERTY_NAME));

		}
		} catch (RepositoryException e) {
			logDebug("unable to retrieve item from Sku Repository for skuId"+getSkuID());

		} catch (BBBSystemException e) {
			logError("BBBCompareProductHandler.populateVO() : System Exception while fetching scene7 key "+ e);
		} catch (BBBBusinessException e) {
			logError("BBBCompareProductHandler.populateVO() : Business Exception while fetching scene7 key "+ e);
		}
	}
	
}
