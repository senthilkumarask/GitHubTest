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
public class BBBFacetValueDroplet extends DynamoServlet {

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
			logDebug("BBBFacetValueDroplet : service() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonFacet = new JSONArray();
		JSONObject errorJsonResponse = null;
		JSONObject errorJson = new JSONObject();
		try {
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);
				
			String facetid = pRequest.getParameter("facet_id");
	
				 if(facetid!=null && !(facetid.trim().equals(""))){
						if (isLoggingDebug()) {
							logDebug("Search facet: "+facetid);
						}
						jsonFacet =fetchFacetValueByFacetId(facetid);
			} 
			if(!jsonFacet.isEmpty()){
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonFacet);
			} else {
				jsonResponse.put("status", "error");
				jsonResponse.put("data", new JSONObject().put("description", "Facet not found") );
			}

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "55");
				errorJson.put("description", "Error while fetching Facet values");
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
			logDebug("BBBFacetValueDroplet : service() : End");
		}
	}

	
	public JSONArray fetchFacetValueByFacetId(String pFacetId) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBFacetValueDroplet : fetchFacetValueByFacetId() : Start");
		}
		JSONArray jsonFacets = new JSONArray();
		List<RepositoryItem> facetValueItems = getAdminManager().fetchFacetValueByFacetId(pFacetId);
		if(facetValueItems!=null && !facetValueItems.isEmpty()){
			for(RepositoryItem facetItem : facetValueItems){
				if (isLoggingDebug()) {
					logDebug("Creating json object for facet id : "+facetItem.getRepositoryId());
				}
				JSONObject jsonFacet = getAdminManager().createSingleFacetValueJsonObject(facetItem);
				jsonFacets.put(jsonFacet);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBFacetValueDroplet : fetchFacetById() : End");
		}
		return jsonFacets;

	}
	
	
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBFacetValueDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBFacetValueDroplet : setPageResponse() : End");
		}
	}
}
