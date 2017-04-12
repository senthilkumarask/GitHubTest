package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.vo.CouponListVo;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

public class TBSUPCSearchInventoryDroplet extends BBBDynamoServlet {
	
	private static final String PROP_SKUID_LIST = "upcItems";
	private final static String INSTOCK="inStock";
	private static final String PARAM_SITEID="siteId";
	private BBBInventoryManager inventoryManager;
	private BBBCatalogTools mCatalogTools;
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		String siteId = pRequest.getParameter(PARAM_SITEID);
		String upcItems = pRequest.getParameter(PROP_SKUID_LIST);
		boolean inStock = true;
		RepositoryItem skuRepositoryItem = null;
		
		
		try {
			if (BBBUtility.isNotEmpty(upcItems)) {
				if (upcItems.contains(BBBCoreConstants.COMMA)) {
					vlogDebug("TBSItemExclusionDroplet :: service() :: upcItems :: " + upcItems + " & siteId :: " + siteId);
					String[] splittedSkuIds = upcItems.split(BBBCoreConstants.COMMA);
					for (String skuUpcId : splittedSkuIds) {
						skuUpcId = skuUpcId.trim();
						if(skuUpcId.startsWith("0")){
							skuUpcId = skuUpcId.substring(1);
						}
						//Fetch SkuID for UPS Code
						skuRepositoryItem = getCatalogTools().getSKUForUPCSearch(skuUpcId);
						if(skuRepositoryItem == null || skuOutOfStock(siteId, skuRepositoryItem.getRepositoryId(),pRequest)) {
							inStock = false;
							break;
						}
					}
				} else {
					upcItems = upcItems.trim();
					if(upcItems.startsWith("0")){
						upcItems = upcItems.substring(1);
					}
					//Fetch SkuID for UPS Code
					skuRepositoryItem = getCatalogTools().getSKUForUPCSearch(upcItems);
					if(skuRepositoryItem == null || skuOutOfStock(siteId, skuRepositoryItem.getRepositoryId(),pRequest)) {
						inStock = false;
					}
				}
			}
				pRequest.setParameter(INSTOCK, inStock);
				pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse);
			
		} catch (BBBBusinessException bbbBusinessException) {
			inStock = false;
			pRequest.setParameter(INSTOCK, inStock);
			logError("System exception occured while querying inventory repository :", bbbBusinessException);
			pRequest.serviceParameter(TBSConstants.ERROR, pRequest, pResponse);
		} catch (BBBSystemException bbbSystemException) {
			inStock = false;
			pRequest.setParameter(INSTOCK, inStock);
			vlogError("Business exception occured while querying inventory repository :", bbbSystemException);
			pRequest.serviceParameter(TBSConstants.ERROR, pRequest, pResponse);
		}
	}

	private boolean skuOutOfStock (String siteId, String skuId, DynamoHttpServletRequest pRequest) throws BBBBusinessException, BBBSystemException {
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		 Map<String, Integer> skuInventoryAvailability = new LinkedHashMap<String, Integer>();			
			Map<String, Integer> sessionInventory = sessionBean.getTbsSkuInventory();
			if(sessionInventory!=null){
				skuInventoryAvailability =sessionBean.getTbsSkuInventory();
			}
			if(sessionBean.getTbsSkuInventory()!=null && sessionInventory.get(siteId+"_"+skuId)!=null){
				vlogDebug("TBSUPCSearchInventoryDroplet :: service() ::picking up cached values for ::"+skuId);
			 int inStockStatus = sessionInventory.get(siteId+"_"+skuId);
			 if(inStockStatus == BBBInventoryManager.NOT_AVAILABLE){
					return true;
				}
			 return false;
			}else{
				 int inStockStatus = getInventoryManager().getProductAvailability(siteId, skuId, BBBInventoryManager.PRODUCT_DISPLAY,0);
				 vlogDebug("TBSUPCSearchInventoryDroplet :: service()::picking up DOM values::"+skuId);
					skuInventoryAvailability.put(siteId+"_"+skuId,inStockStatus);
					sessionBean.setTbsSkuInventory(skuInventoryAvailability);
					if(inStockStatus == BBBInventoryManager.NOT_AVAILABLE){
						return true;
					}
					return false;
					}
		
		
	}
	
	public BBBInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(final BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}
	
}



