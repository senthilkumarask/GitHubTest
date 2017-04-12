package com.bbb.commerce.pricing.priceLists;

import com.bbb.framework.performance.BBBPerformanceMonitor;

import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.perfmonitor.PerfStackMismatchException;


/**
 * @author BBB
 *
 */
public class BBBPriceListManager extends PriceListManager {
	
	/**
	 * @param pPriceList
	 * @param pSkuId
	 * @return RepositoryItem
	 * @throws PriceListException
	 */
	public RepositoryItem getPriceOnBasisOfSKU(RepositoryItem pPriceList, String pSkuId)
		     throws PriceListException
		   {
			if(isLoggingDebug()){
				logDebug("BBBPriceListManager.getPriceOnBasisOfSKU(RepositoryItem pPriceList, String pSkuId) :: starts");
			 }
		     String perfName = "getPriceOnBasisOfSKU(RepositoryItem pPriceList, String pSkuId)";
		     BBBPerformanceMonitor.start("BBBPriceListManager", perfName);
		     boolean perfCancelled = false;
		     try
		     {
		         RepositoryItem price = null;
		         try
		         {
		           price = callSuperGetSkuPrice(pPriceList, pSkuId);
		         } catch (Exception ex) {
		        	 if (isLoggingError()){
				        	logError("BBBPriceListManager.getPriceOnBasisOfSKU(RepositoryItem pPriceList,"
				        			+ " String pSkuId) :: SKU-ID = " + pSkuId + " and Exception occurred = " + ex);
				        }
	               BBBPerformanceMonitor.cancel("BBBPriceListManager", perfName);
	               perfCancelled = true;
	               throw new PriceListException(ex);
		         }
		 
		         return price;
		     }
		     finally
		     {
		       try
		       {
		         if (!(perfCancelled)) {
		           BBBPerformanceMonitor.end("BBBPriceListManager", perfName);
		           perfCancelled = true;
		         }
		       } catch (PerfStackMismatchException e) {
		         if (isLoggingWarning())
		           logWarning(e);
		       }
		       if(isLoggingDebug()){
					logDebug("BBBPriceListManager.getPriceOnBasisOfSKU(RepositoryItem pPriceList, String pSkuId) :: ends");
				}
		     }
		   }

	protected RepositoryItem callSuperGetSkuPrice(RepositoryItem pPriceList, String pSkuId) throws PriceListException {
		return super.getSkuPrice(pPriceList, pSkuId);
	}
	
	@Override
	public RepositoryItem getPrice(RepositoryItem pPriceList, String pProductId, String pSkuId, boolean pUseCache)
			     throws PriceListException
			   {
					if(isLoggingDebug()){
					logDebug("BBBPriceListManager.getPrice(RepositoryItem pPriceList, "
							+ "String pProductId, String pSkuId, boolean pUseCache) :: starts");
					}
			         RepositoryItem price = null;
			         try
			         {
			           price = this.getPriceOnBasisOfSKU(pPriceList, pSkuId);
			         } catch (Exception ex) {
			        	 if (isLoggingError()){
					        	logError("BBBPriceListManager.getPrice(RepositoryItem pPriceList, "
					        			+ "String pProductId, String pSkuId, boolean pUseCache) :: SKU-ID = " + pSkuId + " and Exception occurred = " + ex);
					      }
			           throw new PriceListException(ex);
			         }
			         
			         if(isLoggingDebug()){
							logDebug("BBBPriceListManager.getPrice(RepositoryItem pPriceList, "
								+ "String pProductId, String pSkuId, boolean pUseCache) :: ends");
					 }
			         return price;
			   }
	
	@Override
	public RepositoryItem getPrice(String pPriceListId, String pProductId, String pSkuId, String pParentSkuId, boolean pUseCache)
			     throws PriceListException
			   {
				 if(isLoggingDebug()){
					logDebug("BBBPriceListManager.getPrice(String pPriceListId, String pProductId, "
							+ "String pSkuId, String pParentSkuId, boolean pUseCache) :: starts");
				 }
			     
			     Repository priceListRep = getPriceListRepository();
				 
			       RepositoryItem priceList = null;
			       try
			       {
			         priceList = priceListRep.getItem(pPriceListId, getPriceListItemType());
			       } catch (RepositoryException exc) {
			    	   if (isLoggingError()){
				        	logError("BBBPriceListManager.getPrice(String pPriceListId, String pProductId, "
				        			+ "String pSkuId, String pParentSkuId, boolean pUseCache) :: "
				        			+ "PriceListId = " + pPriceListId + " and Exception occurred = " + exc);
				        }
			         throw new PriceListException(exc);
			       }
			     
			         RepositoryItem price = null;
			         try
			         {
			           price = this.getPriceOnBasisOfSKU(priceList, pSkuId);
			         } catch (Exception ex) {
			        	 if (isLoggingError()){
					        	logError("BBBPriceListManager.getPrice(String pPriceListId, String pProductId, "
					        			+ "String pSkuId, String pParentSkuId, boolean pUseCache) "
					        			+ ":: SKU-ID = " + pSkuId + " and Exception occurred = " + ex);
					     }
			           throw new PriceListException(ex);
			         }
			         if(isLoggingDebug()){
							logDebug("BBBPriceListManager.getPrice(String pPriceListId, String pProductId, "
						        			+ "String pSkuId, String pParentSkuId, boolean pUseCache) :: ends");
			         }
			         return price;
			   }
	
	
	@Override
	public RepositoryItem getPrice(String pPriceListId, String pProductId, String pSkuId, boolean pUseCache)
			     throws PriceListException
			   {
				if(isLoggingDebug()){
					logDebug("BBBPriceListManager.getPrice(String pPriceListId, "
							+ "String pProductId, String pSkuId, boolean pUseCache) :: starts");
				 }
			     Repository priceListRep = getPriceListRepository();
			      
			     RepositoryItem priceList = null;
			     	try
			         {
			              priceList = priceListRep.getItem(pPriceListId, getPriceListItemType());
			         } catch (RepositoryException exc) {
			        	 if (isLoggingError()){
					        	logError("BBBPriceListManager.getPrice(String pPriceListId, "
					        			+ "String pProductId, String pSkuId, boolean pUseCache) :: "
					        			+ "PriceListId = " + pPriceListId + " and Exception occurred = " + exc);
					        }
			              throw new PriceListException(exc);
			         }
			     
			         RepositoryItem price = null;
			         try
			         {
			           price = this.getPriceOnBasisOfSKU(priceList, pSkuId);
			         } catch (Exception ex) {
			        	 if (isLoggingError()){
					        	logError("BBBPriceListManager.getPrice(String pPriceListId, "
					        			+ "String pProductId, String pSkuId, boolean pUseCache) "
					        			+ ":: SKU-ID = " + pSkuId + " and Exception occurred = " + ex);
					        }
			           throw new PriceListException(ex);
			         }
			         if(isLoggingDebug()){
							logDebug("BBBPriceListManager.getPrice(String pPriceListId, "
								+ "String pProductId, String pSkuId, boolean pUseCache) :: ends");
					 }
			         return price;
	}
	
	@Override
	public RepositoryItem getPrice(RepositoryItem pPriceList, String pProductId, String pSkuId, String pParentSkuId, boolean pUseCache)
			     throws PriceListException
			   {
					 if(isLoggingDebug()){
						logDebug("BBBPriceListManager.getPrice(RepositoryItem pPriceList,"
								+ "String pProductId, String pSkuId, "
								+ "String pParentSkuId, boolean pUseCache) :: starts");
					 }
			         RepositoryItem price = null;
			         try
			         {
			           price = this.getPriceOnBasisOfSKU(pPriceList, pSkuId);
			         } catch (Exception ex) {
				        if (isLoggingError()){
				        	logError("BBBPriceListManager.getPrice(RepositoryItem pPriceList,"
							+ "String pProductId, String pSkuId, "
							+ "String pParentSkuId, boolean pUseCache) :: SKU-ID = " + pSkuId + " and Exception occurred = " + ex);
				        }
			           throw new PriceListException(ex);
			         }
			         if(isLoggingDebug()){
							logDebug("BBBPriceListManager.getPrice(RepositoryItem pPriceList,"
								+ "String pProductId, String pSkuId, "
								+ "String pParentSkuId, boolean pUseCache) :: ends");
					 }
			         return price;
			   }
}
