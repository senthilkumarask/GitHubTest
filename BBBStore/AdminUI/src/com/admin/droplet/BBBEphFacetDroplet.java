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
/**
 * This droplet is used to 
 * @author Logixal
 *
 */
public class BBBEphFacetDroplet extends DynamoServlet {

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
			logDebug("BBBEphFacetDroplet : service() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonEphFacet = new JSONArray();
		JSONObject errorJsonResponse = null;
		JSONObject errorJson = new JSONObject();
		try {
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);
				
			String ephId = pRequest.getParameter("eph_id");
			String facetName = pRequest.getParameter("facet_name");
			String facetid = pRequest.getParameter("facet_id");
	
				 if(facetid!=null && !(facetid.trim().equals("")) && ephId!=null && !(ephId.trim().equals(""))){
						if (isLoggingDebug()) {
							logDebug("Search ephFacet: "+ephId);
						}
						jsonEphFacet =fetchEphFacetById(ephId, facetid);
			} else  if(facetName!=null && !(facetName.trim().equals("")) && ephId!=null && !(ephId.trim().equals(""))){
				if (isLoggingDebug()) {
					logDebug("Get All ephFacet of eph facet name : "+ephId);
				}
				jsonEphFacet = fetchEphFacetByName(ephId,facetName);
				
			} else {
				if (isLoggingDebug()) {
					logDebug("Get All ephFacet of eph id : "+ephId);
				}
				jsonEphFacet = fetchAllEphFacetById(ephId);
			}
				
			
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonEphFacet);
			

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "49");
				errorJson.put("description", "Error while fetching Eph Facet");
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
			logDebug("BBBEphFacetDroplet : service() : End");
		}
	}

	
	public JSONArray fetchAllEphFacetById(String pEphId) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetDroplet : fetchEphById() : Start");
		}
		JSONArray jsonEphFacet = new JSONArray();
		List<RepositoryItem> ephFacetItems = getAdminManager().fetchEphFacetsByEphId(pEphId);
		if(ephFacetItems!=null && !ephFacetItems.isEmpty()){
			for(RepositoryItem facetItem : ephFacetItems){
				if (isLoggingDebug()) {
					logDebug("Creating json object for ephFacet id : "+facetItem.getRepositoryId());
				}
				JSONObject jsonFacetEph = getAdminManager().createSingleFacetJsonObject(facetItem);
				jsonEphFacet.put(jsonFacetEph);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetDroplet : fetchEphById() : End");
		}
		return jsonEphFacet;

	}
	
	public JSONArray fetchEphFacetById(String pEphId, String pFacetId) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetDroplet : fetchEphById() : Start");
		}
		JSONArray jsonEphFacet = new JSONArray();
		List<RepositoryItem> ephFacetItems = getAdminManager().fetchEphFacetsByFacetId(pEphId,pFacetId);
		if(ephFacetItems!=null && !ephFacetItems.isEmpty()){
			for(RepositoryItem facetItem : ephFacetItems){
				if (isLoggingDebug()) {
					logDebug("Creating json object for ephFacet id : "+facetItem.getRepositoryId());
				}
				JSONObject jsonFacetEph = getAdminManager().createSingleFacetJsonObject(facetItem);
				jsonEphFacet.put(jsonFacetEph);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetDroplet : fetchEphById() : End");
		}
		return jsonEphFacet;

	}
	
	public JSONArray fetchEphFacetByName(String pEphId, String pFacetName) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetDroplet : fetchEphById() : Start");
		}
		JSONArray jsonEphFacet = new JSONArray();
		List<RepositoryItem> ephFacetItems = getAdminManager().fetchEphFacetsByFacetName(pEphId,pFacetName);
		if(ephFacetItems!=null && !ephFacetItems.isEmpty()){
			for(RepositoryItem facetItem : ephFacetItems){
				if (isLoggingDebug()) {
					logDebug("Creating json object for ephFacet id : "+facetItem.getRepositoryId());
				}
				JSONObject jsonFacetEph = getAdminManager().createSingleFacetJsonObject(facetItem);
				jsonEphFacet.put(jsonFacetEph);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetDroplet : fetchEphById() : End");
		}
		return jsonEphFacet;

	}
	
	
	
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetDroplet : setPageResponse() : End");
		}
	}
}
