package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import atg.json.JSONException;
import atg.json.JSONObject;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * This droplet is used to display the AddToRegistry modal after adding an item to registry.
 */
public class ATRDisplayDroplet extends BBBPresentationDroplet {
	
	/** The Constant JSON_OBJ. */
	public static final String JSON_OBJ = "jsonObj";
	
	public static final String SKU_ID = "skuId";

	public static final String SKU_VO = "skuDetailVO";
	
	/** The catalog tools. */
	private BBBCatalogTools catalogTools;
	
	

	
	@Override
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		
		final String jsonString =  pRequest.getParameter(JSON_OBJ);
		String addItemResults = null;
		String skuId = null;
		String registryId = null;
		String quantity = null;
		String price = null;
		String registryName = null;
		SKUDetailVO skuDetailVO;
		String productId = null;
		
		
			try {
				if(jsonString!=null) {
				logDebug("ATRDisplayDroplet :: jsonString " + jsonString);
				JSONObject jsonObject = new JSONObject(jsonString);
				
				if(jsonObject.has(BBBGiftRegistryConstants.REGISTRY_NAME)) {
					registryName = jsonObject.getString(BBBGiftRegistryConstants.REGISTRY_NAME);
				}
				if(jsonObject.has(BBBCoreConstants.ADDITEMRESULTS)) {
					addItemResults = jsonObject.getString(BBBCoreConstants.ADDITEMRESULTS);
					addItemResults = addItemResults.substring(1, addItemResults.length()-1);
				}
				jsonObject = new JSONObject(addItemResults);
				
				if(jsonObject.has(BBBCoreConstants.SKUID)) {
					skuId = jsonObject.getString(BBBCoreConstants.SKUID);
				}
				if(jsonObject.has(BBBCoreConstants.REGISTRY_ID)) {
					registryId = jsonObject.getString(BBBCoreConstants.REGISTRY_ID);
				}
				if(jsonObject.has(BBBCoreConstants.QTY)) {
					quantity = jsonObject.getString(BBBCoreConstants.QTY);
				}
				if(jsonObject.has(BBBCoreConstants.PRICE)) {
					price = jsonObject.getString(BBBCoreConstants.PRICE);
				}
				
				if(jsonObject.has(BBBCoreConstants.PRODID)) {
					productId = jsonObject.getString(BBBCoreConstants.PRODID);
				}
				}else{
					skuId =  pRequest.getParameter(SKU_ID);
				}
				if(skuId != null) {
					skuDetailVO = getCatalogTools().getMinimalSku(skuId);
					
					
					pRequest.setParameter(BBBCoreConstants.REGISTRY_ID, registryId);
					pRequest.setParameter(BBBCoreConstants.QUANTITY, quantity);
					pRequest.setParameter(BBBCoreConstants.PRICE, price);
					pRequest.setParameter(BBBGiftRegistryConstants.REGISTRY_NAME, registryName);
					pRequest.setParameter(SKU_VO, skuDetailVO);
					pRequest.setParameter(BBBCoreConstants.PRODID, productId);
					pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
						
				}
				
				
			
			} catch (BBBSystemException | BBBBusinessException | JSONException e) {
					logError("Exception in ATRDisplayDroplet", e);
			
		}
	}




	/**
	 * Gets the catalog tools.
	 *
	 * @return the catalog tools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	




	/**
	 * Sets the catalog tools.
	 *
	 * @param catalogTools the new catalog tools
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
}
