package com.admin.droplet;

import java.io.IOException;
import java.sql.SQLException;
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
/**
 * This droplet is used to 
 * @author Logixal
 *
 */
public class BBBFacetDroplet extends DynamoServlet {

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
			logDebug("BBBFacetDroplet : service() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonFacet = new JSONArray();
		JSONObject errorJsonResponse = null;
		JSONObject errorJson = new JSONObject();
		try {
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);
				
			String facetName = pRequest.getParameter("description");
			String facetId = pRequest.getParameter("facet_id");
	
			
				 if(facetId!=null && !(facetId.trim().equals(""))){
						if (isLoggingDebug()) {
							logDebug("Search facet: "+facetId);
						}
						jsonFacet =fetchFacetById(facetId);
			} 
				 else  if(facetName!=null && !(facetName.trim().equals(""))){
				if (isLoggingDebug()) {
					logDebug("Get All facet of facet name : "+facetId);
				}
				jsonFacet = fetchFacetByName(facetName);
				
			} else if ((facetName!=null && facetName.trim().equals("")) || (facetId!=null && facetId.trim().equals(""))) {
			
					if (isLoggingDebug()) {
						logDebug("Get All Facet of id : "+facetId);
					}
					jsonFacet = fetchAllFacets();
				
			}
				
			
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonFacet);
			

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "53");
				errorJson.put("description", "Error while fetching Facet");
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
			logDebug("BBBFacetDroplet : service() : End");
		}
	}

	
	public JSONArray fetchFacetById(String pFacetId) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBFacetDroplet : fetchFacetById() : Start");
		}
		JSONArray jsonFacets = new JSONArray();
		List<RepositoryItem> facetItems = getAdminManager().fetchFacetsByFacetId(pFacetId);
		if(facetItems!=null && !facetItems.isEmpty()){
			for(RepositoryItem facetItem : facetItems){
				if (isLoggingDebug()) {
					logDebug("Creating json object for facet id : "+facetItem.getRepositoryId());
				}
				JSONObject jsonFacet = getAdminManager().createSingleFacetJsonObject(facetItem);
				jsonFacets.put(jsonFacet);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBFacetDroplet : fetchFacetById() : End");
		}
		return jsonFacets;

	}
	
	public JSONArray fetchFacetByName(String pFacetName) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBFacetDroplet : fetchFacetByName() : Start");
		}
		JSONArray jsonFacets = new JSONArray();
		List<RepositoryItem> facetItems = getAdminManager().fetchFacetsByFacetName(pFacetName);
		if(facetItems!=null && !facetItems.isEmpty()){
			for(RepositoryItem facetItem : facetItems){
				if (isLoggingDebug()) {
					logDebug("Creating json object for facet id : "+facetItem.getRepositoryId());
				}
				JSONObject jsonFacet = getAdminManager().createSingleFacetJsonObject(facetItem);
				jsonFacets.put(jsonFacet);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBFacetDroplet : fetchFacetByName() : End");
		}
		return jsonFacets;

	}
	
	public JSONArray fetchAllFacets() throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBFacetDroplet : fetchAllFacets() : Start");
		}
		JSONArray jsonFacets = new JSONArray();
		List<RepositoryItem> facetItems = getAdminManager().fetchAllFacets();
		if(facetItems!=null && !facetItems.isEmpty()){
			for(RepositoryItem facetItem : facetItems){
				if (isLoggingDebug()) {
					logDebug("Creating json object for facet id : "+facetItem.getRepositoryId());
				}
				JSONObject jsonFacet = getAdminManager().createSingleFacetJsonObject(facetItem);
				jsonFacets.put(jsonFacet);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBFacetDroplet : fetchAllFacets() : End");
		}
		return jsonFacets;

	}
	
	
	
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBFacetDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBFacetDroplet : setPageResponse() : End");
		}
	}
}
