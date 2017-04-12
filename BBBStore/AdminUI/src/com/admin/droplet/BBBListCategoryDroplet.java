package com.admin.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.json.JSONArray;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.admin.constant.BBBAdminConstant;
import com.admin.manager.BBBAdminManager;

public class BBBListCategoryDroplet extends DynamoServlet {
	private BBBAdminManager adminManager;
	private Repository mAdminRepository;
	
	
	public Repository getAdminRepository() {
		return mAdminRepository;
	}

	public void setAdminRepository(Repository mAdminRepository) {
		this.mAdminRepository = mAdminRepository;
	}

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

			String listId=pRequest.getParameter("list_id");
		
			if(listId!=null && listId!=""){
				if (isLoggingDebug()) {
					logDebug("Getting list category with id : "+listId);
				}
				jsonCategories = getJsonForFetchCategoriesFromList(listId);	
			
			}
			
		
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonCategories);
			

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "62");
				errorJson.put("description", "Error while fetching list categories");
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
			logDebug("BBBCategoryDroplet : service() : End");
		}
	}
	
	public JSONArray getJsonForFetchCategoriesFromList(String pListId) throws RepositoryException, JSONException, ServletException, IOException{
		
		if (isLoggingDebug()) {
			logDebug("BBBCategoryDroplet : getCategoryById() : Start");
		}
		JSONArray jsonCategories = new JSONArray();
		RepositoryItem listItem = getAdminRepository().getItem(pListId, BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
		jsonCategories = getAdminManager().createSingleListCategoryJsonObject(listItem);
		if (isLoggingDebug()) {
			logDebug("BBBCategoryDroplet : getCategoryById() : End");
		}
		
		return jsonCategories;
	}
	
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBCategoryDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBCategoryDroplet : setPageResponse() : End");
		}
	}
}
