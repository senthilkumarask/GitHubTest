package com.admin.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.json.JSONArray;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.admin.constant.BBBAdminConstant;
import com.admin.manager.BBBAdminManager;

public class BBBSkuDroplet extends DynamoServlet {
	private BBBAdminManager adminManager;
	
	
	
	public BBBAdminManager getAdminManager() {
		return adminManager;
	}

	public void setAdminManager(BBBAdminManager adminManager) {
		this.adminManager = adminManager;
	}

	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	
		
		if (isLoggingDebug()) {
			logDebug("BBBListCategoryDroplet : service() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonCategories = new JSONArray();
		JSONObject errorJsonResponse = null;
		JSONObject errorJson = new JSONObject();
		try {
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);

			String categoryId=pRequest.getParameter("list_cat_id");
			String displayName=pRequest.getParameter("display_name");
			String skuId=pRequest.getParameter("sku_id");
		
			if(categoryId!=null && categoryId!=""){
				if (isLoggingDebug()) {
					logDebug("Getting Sku category with id : "+categoryId);
				}
				jsonCategories = getJsonForFetchSkuFromCategories(categoryId);	
			
			}
			else if(displayName!=null && !displayName.trim().equals("") ){
				if (isLoggingDebug()) {
					logDebug("Searching SKU  by name : "+displayName);
				}
				jsonCategories = searchSkuByName(displayName);
			
			}
			
			else if(skuId!=null && !skuId.trim().equals("")){
				if (isLoggingDebug()) {
					logDebug("Searching SKU  by id : "+skuId);
				}
				jsonCategories = searchSkuById(skuId);
			}
		/*	else if((skuId!=null && skuId.trim().equals(""))||(displayName!=null && displayName.trim().equals("")) ){
				if (isLoggingDebug()) {
					logDebug("Searching SKU  by id : "+skuId);
				}
				jsonCategories = getAllSkus();
		}*/
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonCategories);
			

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "65");
				errorJson.put("description", "Error while fetching Sku");
				jsonResponse.put("message",errorJson);
				jsonResponse.put("status", "error");
				if(isLoggingError()){
					logError("Exception : "+e);
				}
			} catch (JSONException e1) {
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				return;
			}

		} catch (JSONException e) {
			if(isLoggingError()){
				logError("Exception : "+e);
			}
			setPageResponse(pResponse, errorJsonResponse);
			return;
		}
		setPageResponse(pResponse, jsonResponse);
		if (isLoggingDebug()) {
			logDebug("BBBSkuDroplet : service() : End");
		}
	}
	
public JSONArray searchSkuById(String pSkuId) throws RepositoryException, JSONException, ServletException, IOException{
		
		if (isLoggingDebug()) {
			logDebug("BBBSkuDroplet : searchSkuByName() : Start");
		}
		JSONArray jsonSkus = new JSONArray();
		RepositoryItem sku[] = getAdminManager().searchSkuById(pSkuId);
		if(sku!=null){
			JSONObject skuJson;
			for(RepositoryItem skuItem:sku){
				skuJson = getAdminManager().createSingleSkuSearchJsonObject(skuItem);	
				jsonSkus.put(skuJson);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBSkuDroplet : searchSkuByName() : End");
		}
		
		return jsonSkus;
	}
	
	public JSONArray getJsonForFetchSkuFromCategories(String pCategoryId) throws RepositoryException, JSONException, ServletException, IOException{
		
		if (isLoggingDebug()) {
			logDebug("BBBSkuDroplet : getJsonForFetchSkuFromCategories() : Start");
		}
		
		JSONArray jsonCategories = getAdminManager().createCategorySkuJsonObject(pCategoryId);
		if (isLoggingDebug()) {
			logDebug("BBBSkuDroplet : getJsonForFetchSkuFromCategories() : End");
		}
		
		return jsonCategories;
	}
	public JSONArray searchSkuByName(String pDisplayName) throws RepositoryException, JSONException, ServletException, IOException{
		
		if (isLoggingDebug()) {
			logDebug("BBBSkuDroplet : searchSkuByName() : Start");
		}
		JSONArray jsonSkus = new JSONArray();
		RepositoryItem sku[] = getAdminManager().searchSkuByDisplayName(pDisplayName);
		if(sku!=null){
			JSONObject skuJson;
			for(RepositoryItem skuItem:sku){
				skuJson = getAdminManager().createSingleSkuSearchJsonObject(skuItem);	
				jsonSkus.put(skuJson);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBSkuDroplet : searchSkuByName() : End");
		}
		
		return jsonSkus;
	}

	/*public JSONArray getAllSkus() throws RepositoryException, JSONException, ServletException, IOException{
		
		if (isLoggingDebug()) {
			logDebug("BBBSkuDroplet : getAllSkus() : Start");
		}
		JSONArray jsonSkus = new JSONArray();
		RepositoryItem sku[] = getAdminManager().fetchAllSkus();
		if(sku!=null){
			JSONObject skuJson;
			for(RepositoryItem skuItem:sku){
				skuJson = getAdminManager().createSingleSkuSearchJsonObject(skuItem);	
				jsonSkus.put(skuJson);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBSkuDroplet : getAllSkus() : End");
		}
		
		return jsonSkus;
	}*/
	
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBSkuDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBSkuDroplet : setPageResponse() : End");
		}
	}
}
