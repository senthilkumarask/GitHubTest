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
public class BBBJdaFacetDroplet extends DynamoServlet {

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
			logDebug("BBBJdaFacetDroplet : service() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonEphFacet = new JSONArray();
		JSONObject errorJsonResponse = null;
		JSONObject errorJson = new JSONObject();
		try {
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);
				
			String jdaDeptId = pRequest.getParameter("jda_dept_id");
			String jdaSubDeptId = pRequest.getParameter("jda_sub_dept_id");
			String jdaClass = pRequest.getParameter("jda_class");
			String facetName = pRequest.getParameter("description");
			String facetid = pRequest.getParameter("facet_id");
	
				if(jdaDeptId!=null && !jdaDeptId.trim().equals("") && jdaSubDeptId!=null && !jdaSubDeptId.trim().equals("")){
					
					if(facetid!=null && !facetid.trim().equals("")){
						jsonEphFacet = fetchJdaFacetById(jdaDeptId,jdaSubDeptId,jdaClass,facetid);
					} else if(facetName!=null && !facetName.trim().equals("")){
						jsonEphFacet = fetchEphFacetByName(jdaDeptId,jdaSubDeptId,jdaClass,facetName);
					} else {
						jsonEphFacet = fetchAllJdaFacet(jdaDeptId,jdaSubDeptId,jdaClass);
					}
					
				}
				
		
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonEphFacet);
			

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "58");
				errorJson.put("description", "Error while fetching jda facet");
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
			logDebug("BBBJdaFacetDroplet : service() : End");
		}
	}

	
	public JSONArray fetchAllJdaFacet(String pJdaDeptId, String pJdaSubDeptId, String pJdaClass) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetDroplet : fetchAllJdaFacet() : Start");
		}
		JSONArray jsonJdaFacets = new JSONArray();
		List<RepositoryItem> jdaFacetItems = getAdminManager().fetchAllJdaFacets(pJdaDeptId, pJdaSubDeptId, pJdaClass);
		if(jdaFacetItems!=null && !jdaFacetItems.isEmpty()){
			for(RepositoryItem facetItem : jdaFacetItems){
				if (isLoggingDebug()) {
					logDebug("Creating json object for jda facet id : "+facetItem.getRepositoryId());
				}
				JSONObject jsonJdaFacet = getAdminManager().createSingleFacetJsonObject(facetItem);
				jsonJdaFacets.put(jsonJdaFacet);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetDroplet : fetchAllJdaFacet() : End");
		}
		return jsonJdaFacets;

	}
	
	public JSONArray fetchJdaFacetById(String pJdaDeptId, String pJdaSubDeptId, String pJdaClass, String pFacetId) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetDroplet : fetchJdaFacetById() : Start");
		}
		JSONArray jsonJdaFacets = new JSONArray();
		List<RepositoryItem> jdaFacetItems = getAdminManager().fetchJdaFacetsByFacetId(pJdaDeptId, pJdaSubDeptId, pJdaClass, pFacetId);
		if(jdaFacetItems!=null && !jdaFacetItems.isEmpty()){
			for(RepositoryItem facetItem : jdaFacetItems){
				if (isLoggingDebug()) {
					logDebug("Creating json object for jdaFacet id : "+facetItem.getRepositoryId());
				}
				JSONObject jsonJdaFacet = getAdminManager().createSingleFacetJsonObject(facetItem);
				jsonJdaFacets.put(jsonJdaFacet);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetDroplet : fetchJdaFacetById() : End");
		}
		return jsonJdaFacets;

	}
	
	public JSONArray fetchEphFacetByName(String pJdaDeptId, String pJdaSubDeptId, String pJdaClass, String pFacetName) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetDroplet : fetchEphFacetByName() : Start");
		}
		JSONArray jsonJdaFacets = new JSONArray();
		List<RepositoryItem> jdaFacetItems = getAdminManager().fetchJdaFacetsByFacetName(pJdaDeptId,pJdaSubDeptId,pJdaClass,pFacetName);
		if(jdaFacetItems!=null && !jdaFacetItems.isEmpty()){
			for(RepositoryItem facetItem : jdaFacetItems){
				if (isLoggingDebug()) {
					logDebug("Creating json object for jdaFacet id : "+facetItem.getRepositoryId());
				}
				JSONObject jsonJdaFacet = getAdminManager().createSingleFacetJsonObject(facetItem);
				jsonJdaFacets.put(jsonJdaFacet);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetDroplet : fetchEphFacetByName() : End");
		}
		return jsonJdaFacets;

	}
	
	
	
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetDroplet : setPageResponse() : End");
		}
	}
}
