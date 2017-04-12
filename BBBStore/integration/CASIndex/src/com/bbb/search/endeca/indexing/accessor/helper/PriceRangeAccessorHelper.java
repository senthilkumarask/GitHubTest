package com.bbb.search.endeca.indexing.accessor.helper;

import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;

import atg.nucleus.GenericService;
import atg.repository.RepositoryItem;

public class PriceRangeAccessorHelper extends GenericService {
	
	private PriceAccessorHelper mAccessorHelper;	
		
	
	/**
	 * <p>
	 * THis is for ISPriceRangeAccors
	 * </p>
	 * @param pItem
	 * @param listPriceListId
	 * @param salePriceListId
	 * @return
	 */
	public String getUpdatedPriceRangeDesc(final RepositoryItem pItem,final String listPriceListId,final String salePriceListId){
		BBBPerformanceMonitor.start(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "getUpdatedPriceRangeDesc");
				Double skuLowlistPrice=null;
				Double skuHighlistPrice=null;
				String priceRangeDescrip=null;
				if(isLoggingDebug()){
					logDebug("RepositoryItem (Expecting product repository item)\t:"+pItem);				
				}	 				
				if(null!=pItem){					
					skuLowlistPrice=getAccessorHelper().getSalePrice(pItem, AccessorConstants.SKU_LOW_PRICE_PROPERTY_NAME,listPriceListId, salePriceListId );
					skuHighlistPrice=getAccessorHelper().getSkuHighPrice(pItem, AccessorConstants.SKU_HIGH_PRICE_PROPERTY_NAME, listPriceListId);
					
					if(isLoggingDebug()){
						logDebug("skuLowlistPrice\t:"+skuLowlistPrice);
						logDebug("skuHighlistPrice\t:"+skuHighlistPrice);
					}
						
					priceRangeDescrip=(String)pItem.getPropertyValue(AccessorConstants.PRICE_RANGE_DESCRIPTION_PROPERTY_NAME);
					
					if(isLoggingDebug()){
						logDebug("PriceRangeAccessorHelper :Updated PriceRangeDescrip Value\t:"+priceRangeDescrip);
					}					
					
				}
				BBBPerformanceMonitor.end(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "getUpdatedPriceRangeDesc");
				return replacePriceRangeDesc(skuLowlistPrice,skuHighlistPrice,priceRangeDescrip);
			}
	
	
	/**
	 * <p>
	 * It is for WasPriceRangeAccessor
	 * </p>
	 * @param pItem
	 * @param listPriceListId
	 * @return
	 */
	
	public String getUpdatedPriceRangeDesc(final RepositoryItem pItem,final String listPriceListId ){
		BBBPerformanceMonitor.start(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "getUpdatedPriceRangeDesc");
				Double skuLowlistPrice=null;
				Double skuHighlistPrice=null;
				String priceRangeDescrip=null;
				if(isLoggingDebug()){
					logDebug("RepositoryItem (Expecting product repository item)\t:"+pItem);				
				}	 				
				if(null!=pItem){					
					skuLowlistPrice=getAccessorHelper().getSkuLowPrice(pItem, AccessorConstants.SKU_LOW_PRICE_PROPERTY_NAME, listPriceListId);
					skuHighlistPrice=getAccessorHelper().getSkuHighPrice(pItem, AccessorConstants.SKU_HIGH_PRICE_PROPERTY_NAME, listPriceListId);
					
					if(isLoggingDebug()){
						logDebug("skuLowlistPrice\t:"+skuLowlistPrice);
						logDebug("skuHighlistPrice\t:"+skuHighlistPrice);
					}
						
					priceRangeDescrip=(String)pItem.getPropertyValue(AccessorConstants.PRICE_RANGE_DESCRIPTION_PROPERTY_NAME);
					
					if(isLoggingDebug()){
						logDebug("PriceRangeAccessorHelper :Updated PriceRangeDescrip Value\t:"+priceRangeDescrip);
					}					
					
				}
				BBBPerformanceMonitor.end(BBBPerformanceConstants.PRICE_ACCESSOR_CALL , "getUpdatedPriceRangeDesc");
				return replacePriceRangeDesc(skuLowlistPrice,skuHighlistPrice,priceRangeDescrip);
			}

	/**
	 * 
	 * @param skuLowlistPrice
	 * @param skuHighlistPrice
	 * @param priceRangeDescrip
	 * @return
	 */
	
	private String replacePriceRangeDesc(Double skuLowlistPrice, Double skuHighlistPrice, String priceRangeDescrip){		 
		if(isLoggingDebug()){
			 logDebug("Entering replacePriceRangeDesc()...");
		 }
		 
		String updatedPriceRangeDesc= replacePercentTokens(skuLowlistPrice, skuHighlistPrice,priceRangeDescrip);		
		
		
		 if(isLoggingDebug()){
			 logDebug("updatedPriceRangeDesc\t:"+updatedPriceRangeDesc);
			 logDebug("Exting replacePriceRangeDesc()...");
		 }		 
		return updatedPriceRangeDesc;
	}

	/**
	 * @param skuLowlistPrice
	 * @param skuHighlistPrice
	 * @param updatedPriceRangeDesc
	 * @return
	 */

	private  String replacePercentTokens(Double skuLowlistPrice, Double skuHighlistPrice, String priceRangeDesc) {
		String updatedPriceRangeDesc = priceRangeDesc;

		if (updatedPriceRangeDesc != null) {
			String capitalPriceRangeDesc = priceRangeDesc.toUpperCase();
			boolean isPriceRangeContainsL = capitalPriceRangeDesc.contains("%L");
			boolean isPriceRangeContainsH = capitalPriceRangeDesc.contains("%H");
			
			//This is for if the price range description has both %L & %H and low ,high prices.
			if (isPriceRangeContainsL && isPriceRangeContainsH) {
				if (null == skuLowlistPrice && null == skuHighlistPrice) {
					updatedPriceRangeDesc= null;
				} else {
					updatedPriceRangeDesc = updatedPriceRangeDesc.replaceAll(
							"%L", String.valueOf(skuLowlistPrice));

					updatedPriceRangeDesc = updatedPriceRangeDesc.replaceAll(
							"%H", String.valueOf(skuHighlistPrice));
				}
			} else if (isPriceRangeContainsL && !isPriceRangeContainsH) {
				//This is for if the price range description has only %L and low prices.
				if (null == skuLowlistPrice) {
					updatedPriceRangeDesc= null;
				} else {
					updatedPriceRangeDesc = updatedPriceRangeDesc.replaceAll(
							"%L", String.valueOf(skuLowlistPrice));
				}
			} else if (!isPriceRangeContainsL && isPriceRangeContainsH) {
				//This is for if the price range description has only %H and high prices.
				if (null == skuHighlistPrice) {
					updatedPriceRangeDesc= null;
				} else {
					updatedPriceRangeDesc = updatedPriceRangeDesc.replaceAll("%H", String.valueOf(skuHighlistPrice));
				}
			}
		}
		if(isLoggingDebug()){
			logDebug("PriceRangeDesc\t=" + priceRangeDesc
					+ "\t=After Replacing with low&high Price =" + updatedPriceRangeDesc);
		}
		
		return updatedPriceRangeDesc;
	}
	
	// Getter for PriceAccessorHelper
	public PriceAccessorHelper getAccessorHelper() {
		return mAccessorHelper;
	}

	// Setter for PriceAccessorHelper
	public void setAccessorHelper(PriceAccessorHelper accessorHelper) {
		this.mAccessorHelper = accessorHelper;
	}	
}

