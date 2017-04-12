package com.bbb.commerce.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.inventory.InventoryException;
import atg.multisite.SiteContextManager;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.inventory.vo.BBBStoreInventoryVO;
import com.bbb.commerce.inventory.vo.InventoryVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;


/**
 * This class is used to get inventory status of products
 * 
 * @author jpadhi
 * @version $Revision: #1 $
 */
public class GSBBBInventoryManagerImpl extends BBBInventoryManagerImpl 
		 {
	private String storeIdProperty;
	
	 
	public String getStoreIdProperty() {
		return storeIdProperty;
	}


	public void setStoreIdProperty(String storeIdProperty) {
		this.storeIdProperty = storeIdProperty;
	}


	public Map<String, Map<String, Integer>> getMultiSkuWebAndStoreInventory(
			List<String> pSkuIds) throws InventoryException {

		BBBPerformanceMonitor.start("BBBInventoryManager getMultiSkuWebAndStoreInventory");
		
		logDebug("BBBInventoryManager : getProductAvailability() starts");
		
		
		Map<String,Map<String,Integer>> multiSKUDetails = new HashMap<String,Map<String,Integer>>();
		Map<String,Integer> skuDetails = null;
		BBBStoreInventoryVO storeDetails= null;
		
		String siteId = SiteContextManager.getCurrentSiteId();
		String storeId = ServletUtil.getCurrentRequest().getHeader(getStoreIdProperty());
		
		for (String skuId : pSkuIds) {
			skuDetails = new HashMap<String, Integer>(); 
		      try{
		    	  
		    	  int webStockLevel=getSkuWebStock(siteId, skuId);
		    	  skuDetails.put(WEB_STOCK, webStockLevel);
		    	  if (webStockLevel > 0)
		    	  {
		    		  skuDetails.put(WEB, AVAILABLE_BOOLEAN);
		    	  }else{
		    		  skuDetails.put(WEB, NON_AVAILABLE_BOOLEAN);
		    	  }		    	  
		        
		      }catch(BBBBusinessException bbe){
		        	skuDetails.put(WEB, NON_AVAILABLE_BOOLEAN);
		        	logError(bbe);
		        
		      } catch(BBBSystemException bse){
		        	skuDetails.put(WEB, NON_AVAILABLE_BOOLEAN);
		        	logError(bse);
		      } catch(Exception e){
		           	skuDetails.put(WEB, NON_AVAILABLE_BOOLEAN);
		        	logError(e);
		      } 
		      try{
		    	  skuDetails.put(STORE_STOCK, 0);
		    	  storeDetails=getStoreInventoryManager().getInventory(skuId, storeId , false);
		    	  skuDetails.put(STORE_STOCK, (int) storeDetails.getStoreInventoryStock());
		    	  if(storeDetails.getStoreInventoryStock() >0 ){
		    		  skuDetails.put(STORE, AVAILABLE_BOOLEAN);
		    	  }else{
		    		  skuDetails.put(STORE, NON_AVAILABLE_BOOLEAN);
		    	  }
		        
		      }catch(BBBBusinessException bbe){
		    	  	skuDetails.put(STORE, NON_AVAILABLE_BOOLEAN);
		        	logError(bbe);
		      } catch(Exception e){		       
		        	skuDetails.put(STORE, NON_AVAILABLE_BOOLEAN);
		        	logError(e);
		      }
		      multiSKUDetails.put(skuId, skuDetails);
		    }
		
		BBBPerformanceMonitor.end("BBBInventoryManager getMultiSkuWebAndStoreInventory");
		return multiSKUDetails;
	}
	
	
	public int getSkuWebStock(String pSiteId, String pSkuId) throws BBBBusinessException, BBBSystemException {
		 BBBPerformanceMonitor.start("GSBBBInventoryManager getSkuWebStock");
			
			logDebug("GSBBBInventoryManager : getSkuWebStock() starts");
			logDebug("Input parameters : pSiteId " + pSiteId + " ,pSkuId "
						+ pSkuId);
			
			int webStock =0;
			SKUDetailVO skuDetailVO = null;

			try {
				
					logDebug("Calling CatalogTools getSKUDetails() method : Input Parameters are pSiteId - "
							+ pSiteId
							+ " ,pSkuId - "
							+ pSkuId
							+ " & calculateAboveBelowLine - " + false);
				
				skuDetailVO = getCatalogTools().getSKUDetails(pSiteId, pSkuId,
						false);

				
				logDebug("response skuDetailVO from CatalogTools getSKUDetails() method :"
							+ skuDetailVO);
				

				String caFlag = skuDetailVO.getEcomFulfillment();

				if (skuDetailVO.isVdcSku()) {
					webStock = getGSVDCProductAvailability(pSiteId, pSkuId);
				} else {
					webStock = getGSNonVDCProductAvailability(pSiteId,
							pSkuId, caFlag);
				}
			} catch (InventoryException e) {
				if(isLoggingDebug()){
				logDebug(BBBCoreErrorConstants.CHECKOUT_ERROR_1021 +
						": GSBBBInventoryManager : getSkuWebStock() Inventory Exception occoured",
						e);
				}
				webStock=0;
			}

			
			logDebug("GSBBBInventoryManager : getSkuWebStock() ends.");
			logDebug("Output - webStock " + webStock);
			
			BBBPerformanceMonitor.end("BBBInventoryManager getProductAvailability");
			return webStock;
		}
	
	public int getGSVDCProductAvailability(String pSiteId, String pSkuId) throws InventoryException {
	    BBBPerformanceMonitor.start("GSBBBInventoryManager getGSVDCProductAvailability");
		
			logDebug("getGSVDCProductAvailability() : starts");

			StringBuilder sb = new StringBuilder();
			sb.append("Input Parametrs: skuID - ");
			sb.append(pSkuId);
			sb.append(" , siteId - ");
			sb.append(pSiteId);
			logDebug(sb.toString());
		

		int webStock = 0;
		InventoryVO inventoryVO = null;

		try {
			
			logDebug("Calling CatalogTools getInventory() method : Input Parameters are pSkuId - "
						+ pSkuId + " & pSiteId - " + pSiteId);
			

			inventoryVO = getOnlineInventoryManager().getInventory(pSkuId,
					pSiteId);

			
			logDebug("output parameter from CatalogTools getInventory() method : "
						+ inventoryVO);
			
			
			if(inventoryVO != null){
				long afs = inventoryVO.getGlobalStockLevel();

				if (afs > 0) {
					webStock = (int) afs;
				}
			}else{
				BBBPerformanceMonitor.end("GSBBBInventoryManager getGSVDCProductAvailability");
				throw new InventoryException("InventoryVO from catalog is null");
			}

			
		} catch (BBBBusinessException e) {
			BBBPerformanceMonitor.end("GSBBBInventoryManager getGSVDCProductAvailability");
			throw new InventoryException(e);
		} catch (BBBSystemException e) {
			BBBPerformanceMonitor.end("GSBBBInventoryManager getGSVDCProductAvailability");
			throw new InventoryException(e);
		}
		BBBPerformanceMonitor.end("GSBBBInventoryManager getGSVDCProductAvailability");
		return webStock;
	}

	public int getGSNonVDCProductAvailability(String pSiteId, String pSkuId,
			String caFlag) throws InventoryException {
	    BBBPerformanceMonitor.start("GSBBBInventoryManager getGSNonVDCProductAvailability");
		
			logDebug("getGSNonVDCProductAvailability() : starts");

			StringBuilder sb = new StringBuilder();
			sb.append("Input Parametrs: skuID - ");
			sb.append(pSkuId);
			sb.append(" , siteId - ");
			sb.append(pSiteId);
			sb.append(" , caFlag - ");
			sb.append(caFlag);
			logDebug(sb.toString());
		

		int webStock = 0;
		InventoryVO inventoryVO = null;

		try {
			inventoryVO = getOnlineInventoryManager().getInventory(pSkuId,
					pSiteId);
			if(inventoryVO != null){
				long afs = inventoryVO.getGlobalStockLevel();
				long altAFS = inventoryVO.getSiteStockLevel();

				if ("GS_BedBathCanada".equalsIgnoreCase(pSiteId)) {
						if (caFlag != null && "e".equalsIgnoreCase(caFlag.trim()) ) {
							if ((afs + altAFS) > 0) {
								webStock = (int) (afs + altAFS);
							} 
						} else {
							if (altAFS > 0) {
								webStock = (int) altAFS;
							}
						}
					} else {
						if ((afs + altAFS) > 0) {
							webStock = (int) (afs + altAFS);
						} 
					}
				
			}else {
				BBBPerformanceMonitor.end("GSBBBInventoryManager getGSVDCProductAvailability");
				throw new InventoryException("InventoryVO from catalog i null");
			}
			

			
			logDebug("getGSNonVDCProductAvailability() : product availability status : "
						+ webStock);
			
		} catch (BBBBusinessException e) {
			BBBPerformanceMonitor.end("GSBBBInventoryManager getGSVDCProductAvailability");
			throw new InventoryException(e);
		} catch (BBBSystemException e) {
			BBBPerformanceMonitor.end("GSBBBInventoryManager getGSVDCProductAvailability");
			throw new InventoryException(e);
		} 
		BBBPerformanceMonitor.end("BBBInventoryManager getGSNonVDCProductAvailability");
		return webStock;
	}


	
	
}