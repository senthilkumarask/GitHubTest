package com.bbb.search.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.StringUtils;
import atg.multisite.Site;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.result.BBBProduct;
import com.bbb.search.bean.result.BBBProductList;
import com.bbb.commerce.pricing.priceLists.BBBPriceListManager;
/**
 * This Droplet will be used for Sku/Upc Search to get the sku/upc related information to be displayed on upc search results page by doing a Repository
 * Lookup only instead of fetching it from endeca and then doing a repository lookup.
 * @author Logixal 
 *
 */
public class TBSSkuUpcSearchDroplet extends BBBDynamoServlet {
	
	private final static String SITE = "site";
	private final static String PROFILE = "profile";	
	private final static String STARTS_WITH_ZERO = "0";
	private final static String COMMA = ",";
	private final static String UPC = "upc";
	private final static String LIST_PRICE_LIST = "priceList";
	private final static String SALE_PRICE_LIST = "salePriceList";
	private final static String LIST_PRICE = "listPrice";
	
	private BBBCatalogTools mCatalogTools;
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
	private PriceListManager mPriceListManager;
	public PriceListManager getPriceListManager() {
		return mPriceListManager;
	}

	public void setPriceListManager(PriceListManager pPriceListManager) {
		this.mPriceListManager = pPriceListManager;
	}
	
	/**
	 * This method will do the following: 
	 *  1. Take input searchType, siteId, profile and keyword from searchbox as input.
	 *  2. It will then do a split on the keyword entered by the user and create an array of sku/upc numbers.
	 *  3. It will then create SKUDetailVO, TBSProduct and BBBProductList VO's which are required by the product_list_upc_from_repo-rwd.jsp to display
	 *     sku/product information on the upc search results page.
	 *     
	 *  @param pRequest
	 *  @param pResponse
	 */
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " TBSSkuUpcSearchDroplet service");
		logDebug("Starting TBSSkuUpcSearchDroplet : service()");
		String siteID = null;
		Site site =  (Site) pRequest.getObjectParameter(SITE);
		if(site!=null) {
			siteID = site.getId();
		}
		Profile profile = (Profile) pRequest.getObjectParameter(PROFILE);
		String origKeyword = pRequest.getParameter(TBSConstants.KEYWORD);
		RepositoryItem listPriceList = null;
		RepositoryItem	salePriceList = null;
		RepositoryItem skuRepositoryItem = null;
		RepositoryItem parentProductRepositoryItem = null;
		String parentProdId = null;
		BBBProduct productVO = null;
		BBBProductList productListVO = null;
		Map<String,String> skuUpcList = null;
		ArrayList<String> skuUpcNumbersList = null;
		ArrayList<String> foundSkuUpcNumbersList = null;
		ArrayList<String> notFoundSkuUpcNumbersList = null;
		StringBuffer foundSkuUpcNumbers = new StringBuffer();
		StringBuffer notFoundSkuUpcNumbers = new StringBuffer();
		
		// if searchterm not empty do the processing else render the empty oparam. 
		if((!StringUtils.isBlank(origKeyword))){
			skuUpcNumbersList = new ArrayList<String>(Arrays.asList(origKeyword.split(COMMA)));
			foundSkuUpcNumbersList = new ArrayList<String>();
			notFoundSkuUpcNumbersList = new ArrayList<String>();
			productListVO = new BBBProductList();
			skuUpcList = new HashMap<String, String>();
			
			//Fetching PriceLists from profile to get pricing data from PriceLists Repository.
			try {
				listPriceList = getPriceListManager().getPriceList(profile,LIST_PRICE_LIST, false);
				salePriceList = (RepositoryItem) profile.getDataSource().getPropertyValue(SALE_PRICE_LIST);
			} catch (PriceListException e1) {
				logError("Could not fetch pricelist for the given profile"+e1);
			}
			
			for(String skuUpcId:skuUpcNumbersList) {
				//Trim leading and trailing spaces from a sku/upc number.
				skuUpcId = skuUpcId.trim();
				//Check to see if a sku/upc is repeated in search term.
					if(((skuUpcList.containsKey(skuUpcId)) || (skuUpcList.containsValue(skuUpcId)))) {
						continue;
					}
					//Check to see if a sku/upc starts with zero 
					if(skuUpcId.startsWith(STARTS_WITH_ZERO)){
						skuUpcId = skuUpcId.substring(1);
					}
					skuRepositoryItem = ((BBBCatalogTools) getCatalogTools()).getSKUForUPCSearch(skuUpcId);
					//add the search term to notFoundList to be displayed on page, if the sku/upc number is not found 
					//or if there is an exception. 
					if(skuRepositoryItem == null) {
						notFoundSkuUpcNumbersList.add(skuUpcId);
					}else{
						skuUpcList.put(skuRepositoryItem.getRepositoryId(), (String) skuRepositoryItem.getPropertyValue(UPC));
						final Set<RepositoryItem> parentProduct = (Set<RepositoryItem>) skuRepositoryItem.getPropertyValue(BBBCatalogConstants.PARENT_PRODUCTS_PRODUCT_PROPERTY_NAME);
						if ((parentProduct != null) && !parentProduct.isEmpty()) {
							int size = parentProduct.size();
							int counter = 0;
							for (final RepositoryItem productRepositoryItem : parentProduct) {
								counter++;
								//If product is active, break the loop or if all products are deactive, then assign the last product and proceed.
								if(getCatalogTools().isTBSProductActiveMIESearch(productRepositoryItem) || size == counter){
									parentProductRepositoryItem = productRepositoryItem;
									break;
								}
							}
						}
						try {
							productVO = getCatalogTools().getProductDetailsForUPCSearch(parentProductRepositoryItem, skuRepositoryItem);
							setWasIsPriceInVO(productVO,listPriceList,salePriceList,parentProdId,skuRepositoryItem.getRepositoryId());
							productListVO.getBBBProducts().add(productVO);
							foundSkuUpcNumbersList.add(skuUpcId);
						} catch (BBBBusinessException e) {
							notFoundSkuUpcNumbersList.add(skuUpcId);
							logError("Product not avaiable : "+parentProdId+" "+e);
						} catch (PriceListException e) {
							logError("Error while fetching the priceLists"+e);
 						}				
				  }
			}
			//Code to generate the list of comma separated found and unfound skus.
			for(String foundSkuUpcNum:foundSkuUpcNumbersList) {
				foundSkuUpcNumbers = foundSkuUpcNumbers.append(foundSkuUpcNum+COMMA);
			}
			if(foundSkuUpcNumbers.length()>0 && foundSkuUpcNumbers.charAt(foundSkuUpcNumbers.length()-1)==','){
				foundSkuUpcNumbers = foundSkuUpcNumbers.deleteCharAt(foundSkuUpcNumbers.length()-1);
			}
			for(String notFoundSkuUpcNum:notFoundSkuUpcNumbersList) {
				notFoundSkuUpcNumbers = notFoundSkuUpcNumbers.append(notFoundSkuUpcNum+COMMA);
			}
			if(notFoundSkuUpcNumbers.length()>0 && notFoundSkuUpcNumbers.charAt(notFoundSkuUpcNumbers.length()-1)==','){
				notFoundSkuUpcNumbers = notFoundSkuUpcNumbers.deleteCharAt(notFoundSkuUpcNumbers.length()-1);
			}
			//if no sku/upc are found then render no_search_results jsp else display the found sku/upc.
			if(foundSkuUpcNumbersList.size() == 0 && notFoundSkuUpcNumbersList.size() > 0) {
				pRequest.setParameter("enteredSearchTerm", origKeyword);
				pRequest.setParameter("OUTPUT_EMPTY_MESSAGE", "No Search Results Found");
				pRequest.serviceLocalParameter(BBBCoreConstants.ERROR_OPARAM, pRequest,pResponse);
			}else{
				pRequest.setParameter("bbbProductListVO", productListVO);
				pRequest.setParameter("foundSkuUpcNumbers", foundSkuUpcNumbers);
				pRequest.setParameter("notFoundSkuUpcNumbers", notFoundSkuUpcNumbers);
				pRequest.setParameter("foundSkuUpcCount", foundSkuUpcNumbersList.size());
				pRequest.setParameter("notFoundSkuUpcCount", notFoundSkuUpcNumbersList.size());
				pRequest.serviceLocalParameter(BBBCoreConstants.OPARAM, pRequest,pResponse);
			}
		} else {
			pRequest.setParameter("enteredSearchTerm", origKeyword);
			pRequest.setParameter("OUTPUT_EMPTY_MESSAGE", "Search type and/or searchTerm is empty");
			pRequest.serviceLocalParameter(BBBCoreConstants.EMPTY_OPARAM, pRequest,pResponse);
		}
		logDebug("TBSSkuUpcSearchDroplet : service() : bbbProductListVO : "+productListVO);
		logDebug("TBSSkuUpcSearchDroplet : service() : foundSkuUpcNumbers : "+foundSkuUpcNumbers);
		logDebug("TBSSkuUpcSearchDroplet : service() : invalidSkuUpcNumbers : "+notFoundSkuUpcNumbers);
		logDebug("TBSSkuUpcSearchDroplet : service() : validSkuUpcCount : "+foundSkuUpcNumbersList);
		logDebug("TBSSkuUpcSearchDroplet : service() : totalSkuUpcCount : "+skuUpcNumbersList.size());
	}
	
	/**
	 * This method will do the following:
	 *  1. Set the wasPrice as null and priceRange with listPriceListPrice value.
	 *  2. Check if salePriceList is present on Profile.
	 *  3. If present fetch the salePriceListPrice value and compare with the listPriceList value.
	 *  4. If salePriceListPrice is less than listPriceListPrice then set wasPrice with listPriceListPrice and priceRange with salePriceListPrice.
	 *   
	 * @param productVO
	 * @param pListPriceList
	 * @param pSalePriceList
	 * @param pProduct
	 * @param pSku
	 * @throws PriceListException
	 */
	public void setWasIsPriceInVO(BBBProduct productVO, RepositoryItem pListPriceList, RepositoryItem pSalePriceList, String pProduct, String pSku) 
			throws PriceListException {
		logDebug("Starting setWasIsPriceInVO method");
		RepositoryItem salePriceListPrice = null;
		Double listPrice = null;
		Double salePrice = null;
		RepositoryItem listPriceListPrice = ((BBBPriceListManager) getPriceListManager()).getPrice(pListPriceList, pProduct, pSku);
		productVO.setWasPriceRange(null);
		listPrice = ((Double)listPriceListPrice.getPropertyValue(LIST_PRICE));
		productVO.setPriceRange(listPrice.toString());
		if(pSalePriceList!=null) {
			salePriceListPrice = ((BBBPriceListManager) getPriceListManager()).getPrice(pSalePriceList, pProduct, pSku);
			if(salePriceListPrice!=null) {
				salePrice = ((Double)salePriceListPrice.getPropertyValue(LIST_PRICE));
				if(salePrice < listPrice) {
					productVO.setPriceRange(salePrice.toString());
				}
			}
			logDebug("List Price : "+listPrice+" Sale Price : "+salePrice);
			logDebug("Ending setWasIsPriceInVO method");
		}
	}
}