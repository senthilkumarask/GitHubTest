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
 * This droplet is used to fetch categories based on search term and category id.
 * @author Logixal
 *
 */
public class BBBCategoryDroplet extends DynamoServlet {

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
			logDebug("BBBCategoryDroplet : service() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonCategories = new JSONArray();
		JSONObject errorJsonResponse = null;
		JSONObject  errorJson =  new JSONObject();
		try {
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);

			String categoryId = pRequest.getParameter("category_id");
			String searchTerm = pRequest.getParameter("search_term");
			boolean search = Boolean.parseBoolean(pRequest.getParameter("search"));
			if(search && categoryId!=null && !categoryId.trim().equals("")){
				if (isLoggingDebug()) {
					logDebug("Search category with id : "+categoryId);
				}
				jsonCategories = searchCategoryById(categoryId);
			}else if(search && (categoryId!=null && categoryId.trim().equals("") || searchTerm!=null && searchTerm.trim().equals(""))){
				if (isLoggingDebug()) {
					logDebug("Getting all categories");
				}
				jsonCategories = getAllCategories();
			}			
			else if(categoryId!=null && !categoryId.trim().equals("")){
				if (isLoggingDebug()) {
					logDebug("Getting category with id : "+categoryId);
				}
				jsonCategories = getCategoryById(categoryId);	
			}else if(searchTerm!=null && !searchTerm.trim().equals("")){
				if (isLoggingDebug()) {
					logDebug("Search category with term : "+searchTerm);
				}
				jsonCategories = getCategoryBySearchTerm(searchTerm);
			}
			
			
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonCategories);
			

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "47");
				errorJson.put("description", "Error while fetching category");
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
/**
 * 
 * @param pCategoryId
 * @return Category in Json format based on category id.
 * @throws RepositoryException
 * @throws JSONException
 * @throws ServletException
 * @throws IOException
 */
	public JSONArray getCategoryById(String pCategoryId) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBCategoryDroplet : getCategoryById() : Start");
		}
		JSONArray jsonCategories = new JSONArray();
		RepositoryItem category = getAdminManager().fetchCategoryById(pCategoryId);
		if(category!=null){
			JSONObject jsonCategory = getAdminManager().createSingleCategoryJsonObject(category);
			jsonCategories.put(jsonCategory);
		}
		if (isLoggingDebug()) {
			logDebug("BBBCategoryDroplet : getCategoryById() : End");
		}
		return jsonCategories;

	}

/**
 * 
 * @param pSearchTerm
 * @return Category in Json format based on search term.
 * @throws RepositoryException
 * @throws JSONException
 * @throws ServletException
 * @throws IOException
 */
	public JSONArray getCategoryBySearchTerm(String pSearchTerm) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBCategoryDroplet : getCategoryBySearchTerm() : Start");
		}
		JSONArray jsonCategories = new JSONArray();
		RepositoryItem[] categories = getAdminManager().searchCategoryByName(pSearchTerm);
		if(categories!=null && categories.length>0){
			for(RepositoryItem  category : categories){
				if (isLoggingDebug()) {
					logDebug("Creating json object for category id : "+category.getRepositoryId());
				}
				JSONObject jsonCategory = getAdminManager().createSingleCategoryJsonObject(category);
				jsonCategories.put(jsonCategory);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBCategoryDroplet : getCategoryBySearchTerm() : End");
		}
		return jsonCategories;

	}
	
	public JSONArray searchCategoryById(String pCategoryId) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBCategoryDroplet : searchCategoryById() : Start");
		}
		JSONArray jsonCategories = new JSONArray();
		RepositoryItem[] categories = getAdminManager().searchCategoryById(pCategoryId);
		if(categories!=null && categories.length>0){
			for(RepositoryItem  category : categories){
				if (isLoggingDebug()) {
					logDebug("Creating json object for category id : "+category.getRepositoryId());
				}
				JSONObject jsonCategory = getAdminManager().createSingleCategoryJsonObject(category);
				jsonCategories.put(jsonCategory);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBCategoryDroplet : searchCategoryById() : End");
		}
		return jsonCategories;

	}

	
	public JSONArray getAllCategories() throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBCategoryDroplet : searchCategoryById() : Start");
		}
		JSONArray jsonCategories = new JSONArray();
		RepositoryItem[] categories = getAdminManager().getAllCategories();
		if(categories!=null && categories.length>0){
			for(RepositoryItem  category : categories){
				if (isLoggingDebug()) {
					logDebug("Creating json object for category id : "+category.getRepositoryId());
				}
				JSONObject jsonCategory = getAdminManager().createSingleCategoryJsonObject(category);
				jsonCategories.put(jsonCategory);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBCategoryDroplet : searchCategoryById() : End");
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
