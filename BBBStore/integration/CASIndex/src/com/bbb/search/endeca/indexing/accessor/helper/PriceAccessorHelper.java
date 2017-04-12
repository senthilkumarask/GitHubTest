package com.bbb.search.endeca.indexing.accessor.helper;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerformanceMonitor;

import com.bbb.commerce.pricing.priceLists.BBBPriceListManager;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;

public class PriceAccessorHelper extends GenericService {

	private BBBPriceListManager mPriceListManager;
	
	// Getting SkuLowPrice for US-Canada
	public Double getSkuLowPrice(final RepositoryItem productItem, final String skuLowPricePropertyName, final String priceListId) {

		return getListPrice(productItem, skuLowPricePropertyName, priceListId);
	}

	// Getting SkuHighPrice for US-Canada
	public Double getSkuHighPrice(final RepositoryItem productItem,	final String skuHighPricePropertyName, final String priceListId) {

		return getListPrice(productItem, skuHighPricePropertyName, priceListId);
	}

	// Getting WasSkuLowPrice for US-Canada
	public Double getWasSkuLowPrice(final RepositoryItem productItem,final String wasSkuLowPricePropertyName, final String priceListId) {

		return getListPrice(productItem, wasSkuLowPricePropertyName, priceListId);
	}

	// Getting WasSkuHighPrice for US-Canada
	public Double getWasSkuHighPrice(final RepositoryItem productItem,final String wasSkuHighPricePropertyName, final String priceListId) {

		return getListPrice(productItem, wasSkuHighPricePropertyName, priceListId);
	}

	// Getting MexioSkuLowPrice for Mexico
	public Double getMexicanSkuLowPrice(final RepositoryItem productItem, final String skuLowPricePropertyNameMX, final String priceListId) {
			return getListPrice(productItem, skuLowPricePropertyNameMX, priceListId);
	}
		
	// Getting MexicoSkuHighPrice for Mexico
	public Double getMexicanSkuHighPrice(final RepositoryItem productItem,final String skuHighPricePropertyNameMX, final String priceListId) {
			return getListPrice(productItem, skuHighPricePropertyNameMX, priceListId);
	}

	// Getting MieCAListPrice for Canada
	public Double getMieCAListPrice(final RepositoryItem skuItem, final String priceListId) {				
			return getListPriceBySku(skuItem, priceListId);
	}
	
	// Getting MieCAListPrice for Mexico
	public Double getMieMXListPrice(final RepositoryItem skuItem, final String priceListId) {	
		return getListPriceBySku(skuItem, priceListId);
	}
	
	// Getting MieListPrice for US
	public Double getMieListPrice(final RepositoryItem skuItem, final String priceListId) {
			return getListPriceBySku(skuItem, priceListId);
	}
	

/**
 * <p>
 * This method will retrieve the priceList by priceListId, then retrieves the listPrice for the given skuid.
 * </p>
 * @param skuItem
 * @param priceListId
 * @return
 */
	public Double getListPriceBySku(final RepositoryItem skuItem, final String priceListId) {
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL, "getListPriceBySku()");
		Double listPrice = null;
		if (isLoggingDebug()) {
			logDebug("Start : getListPriceBySku() :|PriceListId|SkuId\t:"+priceListId+"|"+skuItem.getRepositoryId());
		}		
		try {
			 RepositoryItem priceListItem=getPriceListManager().getPriceList(priceListId);
			 if(null!=priceListItem){
				 RepositoryItem priceItem=getPriceListManager().getPriceOnBasisOfSKU(priceListItem,skuItem.getRepositoryId());
					if (null != priceItem) {
						listPrice = (Double) priceItem.getPropertyValue(getPriceListManager().getListPricePropertyName());						
								
						if(isLoggingDebug()){
							logDebug("List Price of SKU : " + skuItem.getRepositoryId() + " is : "+ listPrice);
						}				
					} 				
			 }			
		} catch (PriceListException e) {
			if (isLoggingError()) {
				logError("PriceListException found while retrieving listPrice from priceList for sku "+ skuItem	+ "\t from Pricelist :"	+ priceListId , e);
			}
		}
		finally{
			if (isLoggingDebug()) {
				logDebug("End : getListPriceBySku() and listPrice is\t:" + listPrice);
			}	
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL, "getListPriceBySku()");
		return listPrice;
	}
	
	/**
	 * @param pItem
	 * @param listPrice
	 * <p>
	 * This method will perform the below two actions and retrieve listPrice. 
	 * 1. Retrieve the priceItem for given priceListId and skuId 
	 * 2. Retrieve the listprice  
	 * </p>
	 */
	
	public Double getListPrice(final RepositoryItem productItem, final String skuPropertyName, final String priceListId) {
		Double listPrice = null;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL, "getListPrice()");
		String skuId = (String) productItem.getPropertyValue(skuPropertyName);		
		if (isLoggingDebug()) {
			logDebug("Start : getListPrice() :ProductId|PriceListId|SkuId|skuPropertyName\t:" + productItem.getRepositoryId()+"|"+priceListId+"|"+skuId+"|"+skuPropertyName);
		}
		
		try {
			if(StringUtils.isEmpty(priceListId)){
		if (isLoggingDebug()) {
					logDebug("priceListId is null ,see the above csv data for more details"); 
				}
				return listPrice;
		}	
			RepositoryItem priceItem = getPriceListManager().getPrice(priceListId, productItem.getRepositoryId(), skuId, false);
			if (null != priceItem) {
				listPrice = (Double) priceItem.getPropertyValue(getPriceListManager().getListPricePropertyName());							
			}

		} catch (PriceListException e) {
			if (isLoggingError()) {
				logError("PriceListException found while retrieving listPrice from priceList for Product "+ productItem + "\t from Pricelist :"	+ priceListId + " and skuId :" + skuId, e);
			}
		}finally{
			if (isLoggingDebug()) {
				logDebug("End :  getListPrice() "+listPrice);
			}
		}
		BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL, "getListPrice()");
		return listPrice;
	}

	/**
	 * <p>
	 * This method 
	 * </p>
	 * @param siteId
	 * @return
	 */
		
	public final String getListPriceListIdBySiteId(final String siteId) {
		String priceListId=null;
		if (("BEDBATHUS".equalsIgnoreCase(siteId))||("TBS_BEDBATHUS".equalsIgnoreCase(siteId))||("GS_BEDBATHUS".equalsIgnoreCase(siteId))||("BUYBUYBABY".equalsIgnoreCase(siteId))||("TBS_BUYBUYBABY".equalsIgnoreCase(siteId))||("GS_BUYBUYBABY".equalsIgnoreCase(siteId))) {
			priceListId= AccessorConstants.US_LIST_PRICE_LIST_ID;
		}
		
		if(("BEDBATHCANADA".equalsIgnoreCase(siteId))||("TBS_BEDBATHCANADA".equalsIgnoreCase(siteId))||("GS_BEDBATHCANADA".equalsIgnoreCase(siteId))){
			priceListId = AccessorConstants.CA_LIST_PRICE_LIST_ID;
		}				
		return priceListId;
	}
		
	/**
	 * <p>
	 * 
	 * </p>
	 * @param siteId
	 * @return
	 */
	
	public final String getSalePriceListIdBySiteId(final String siteId) {
		String priceListId=null;		
		if (("BEDBATHUS".equalsIgnoreCase(siteId))||("TBS_BEDBATHUS".equalsIgnoreCase(siteId))||("GS_BEDBATHUS".equalsIgnoreCase(siteId))||("BUYBUYBABY".equalsIgnoreCase(siteId))||("TBS_BUYBUYBABY".equalsIgnoreCase(siteId))||("GS_BUYBUYBABY".equalsIgnoreCase(siteId))) {
			priceListId = AccessorConstants.US_SALE_PRICE_LIST_ID;
		}
		
		if(("BEDBATHCANADA".equalsIgnoreCase(siteId))||("TBS_BEDBATHCANADA".equalsIgnoreCase(siteId))||("GS_BEDBATHCANADA".equalsIgnoreCase(siteId))){
			priceListId = AccessorConstants.CA_SALE_PRICE_LIST_ID;
		}
		
		return priceListId;
	}
	
	
	// Getter for BBBPriceListManager
	public BBBPriceListManager getPriceListManager() {
		return mPriceListManager;
	}

	// Setter for BBBPriceListManager
	public void setPriceListManager(BBBPriceListManager priceListManager) {
		this.mPriceListManager = priceListManager;
	}
	
	public Double getSalePrice(RepositoryItem pItem, String skuLowPricePropertyName, String listPriceListId, String salePriceListId){
		if(isLoggingDebug()){
				logDebug("Start getSalePrice()...");
				logDebug("ListPriceID|SalePriceID\t:"+listPriceListId+"|"+salePriceListId);
		}
		Double finalSalePrice=null;
		Double listPrice = getListPrice(pItem, skuLowPricePropertyName, listPriceListId);
		Double salePrice = getListPrice(pItem, skuLowPricePropertyName, salePriceListId);
		
		if(isLoggingDebug()){			
			logDebug("ListPrice|SalePrice\t:"+listPrice+"|"+salePrice);		 
	}
		
		if(null!=salePrice)
			finalSalePrice=salePrice;
		else
			finalSalePrice=listPrice;
		if(isLoggingDebug()){
			logDebug("End getSalePrice()\t:"+finalSalePrice);
		}
		return finalSalePrice;
	}

}
