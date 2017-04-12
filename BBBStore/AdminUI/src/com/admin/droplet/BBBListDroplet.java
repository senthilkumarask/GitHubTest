package com.admin.droplet;

import java.io.IOException;

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
/**
 * This droplet is used to fetch list items by either passing site id and single list item by passing list id.
 */
public class BBBListDroplet extends DynamoServlet {


	private BBBAdminManager mAdminManager;
	public BBBAdminManager getAdminManager() {
		return mAdminManager;
	}

	public void setAdminManager(BBBAdminManager pAdminManager) {
		this.mAdminManager = pAdminManager;
	}
	
	
	public void service(DynamoHttpServletRequest pRequest,DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBListDroplet : service() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		JSONObject errorJsonResponse = null;
		JSONObject errorJson = new JSONObject();
		try {
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);
			String siteId  = pRequest.getParameter("site_id");
			String listId = pRequest.getParameter("list_id");
			
			if(siteId!=null && !siteId.isEmpty()){
				if (isLoggingDebug()) {
					logDebug("BBBListDroplet : service() : Searching list based on site : "+siteId);
				}
				jsonResponse = getSiteSpecificList(siteId);
			} else if(listId!=null && !listId.isEmpty()){
				if (isLoggingDebug()) {
					logDebug("BBBListDroplet : service() : Searching list based on list id : "+listId);
				}
				jsonResponse = getListById(listId);
			}

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "63");
				errorJson.put("description", "Error while fetching list");
				jsonResponse.put("message",errorJson);
				jsonResponse.put("status", "error");
				if(isLoggingError()){
					logError("Exception : "+e);
				}
			} catch (JSONException e1) {
				
				if (isLoggingError()) {
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse,errorJsonResponse);
				return;
			}
		} catch (JSONException e) {
			if (isLoggingError()) {
				logError("Exception : "+e);
			}
			setPageResponse(pResponse,errorJsonResponse);
			return;
		}

		setPageResponse(pResponse, jsonResponse);
		if (isLoggingDebug()) {
			logDebug("BBBListDroplet : service() : End");
		}
	}

	/**
	 * 
	 * @param pListId
	 * @return JSONArray of list items
	 * @throws RepositoryException
	 * @throws JSONException
	 * @throws ServletException
	 * @throws IOException
	 */
	public JSONObject getListById(String pListId) throws RepositoryException, JSONException, ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBListDroplet : getListById() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonListItems = new JSONArray();
		RepositoryItem listItem = getAdminManager().fetchListById(pListId);	
		if(listItem!=null){
			if (isLoggingDebug()) {
				logDebug("List item fetch : "+listItem);
			}
		JSONObject jsonListItem = getAdminManager().createSingleListJsonObject(listItem);	
		jsonListItems.put(jsonListItem);
		jsonResponse.put("data", jsonListItems);
		jsonResponse.put("status", "ok");
		} else {
			jsonResponse.put("status", "error");
			jsonResponse.put("message", new JSONObject().put("description", "List not found"));
		}
		if (isLoggingDebug()) {
			logDebug("BBBListDroplet : getListById() : End");
		}
		return jsonResponse;

	}

	/**
	 * 
	 * @param pSiteId
	 * @return JSONArray of list items
	 * @throws RepositoryException
	 * @throws JSONException
	 * @throws ServletException
	 * @throws IOException
	 */
	public JSONObject  getSiteSpecificList(String pSiteId) throws RepositoryException, JSONException, ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBListDroplet : getSiteSpecificList() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		RepositoryItem[] listItems = getAdminManager().fetchSiteList(pSiteId);
		if(listItems!=null && listItems.length>0){
			if (isLoggingDebug()) {
				logDebug("List items fetch : "+listItems);
			}
		JSONArray jsonListItems = createListJson(listItems);
		jsonResponse.put("status", "ok");
		jsonResponse.put("data", jsonListItems);
		} else {
			jsonResponse.put("status", "error");
			jsonResponse.put("message", new JSONObject().put("description", "List not found for current site"));
		}
		if (isLoggingDebug()) {
			logDebug("BBBListDroplet : getSiteSpecificList() : End");
		}
		return jsonResponse;

	}
	/**
	 * 
	 * @param pResponse
	 * @param pResponseObject
	 * @throws IOException
	 */
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBListDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBListDroplet : setPageResponse() : End");
		}
	}

	/**
	 * 
	 * @param listItems
	 * @return Array of List JSONItems
	 * @throws IOException
	 * @throws JSONException
	 * @throws RepositoryException
	 */
	public final JSONArray createListJson(RepositoryItem[] listItems) throws IOException, JSONException, RepositoryException{
		if (isLoggingDebug()) {
			logDebug("BBBListDroplet : createListJson() : Start");
		}
		JSONArray jsonListItems = new JSONArray();
		
		for (RepositoryItem listItem : listItems) {
			if (isLoggingDebug()) {
				logDebug("BBBListDroplet : Creating Json object for item with id : "+listItem.getRepositoryId());
			}
			JSONObject jsonListItem = getAdminManager().createSingleListJsonObject(listItem);
			jsonListItems.put(jsonListItem);
		}
		if (isLoggingDebug()) {
			logDebug("BBBListDroplet : createListJson() : End");
		}
		return jsonListItems;
	}
}
