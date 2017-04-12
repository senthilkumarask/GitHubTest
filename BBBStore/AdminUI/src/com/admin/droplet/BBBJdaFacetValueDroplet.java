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
 * This droplet is used to 
 * @author Logixal
 *
 */
public class BBBJdaFacetValueDroplet extends DynamoServlet {

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
			logDebug("BBBJdaFacetValueDroplet : service() : Start");
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
			String facetid = pRequest.getParameter("facet_id");
	
			if(jdaDeptId!=null && !jdaDeptId.trim().equals("") && jdaSubDeptId!=null && !jdaSubDeptId.trim().equals("")){	 
				if(facetid!=null && !(facetid.trim().equals("")) ){
					if (isLoggingDebug()) {
							logDebug("Search ephFacet: "+facetid);
						}
				jsonEphFacet = fetchFacetValueByFacetId(jdaDeptId,jdaSubDeptId,jdaClass,facetid);
				} 	
			}			
			
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonEphFacet);
			

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "60");
				errorJson.put("description", "Error while fetching jda facet values");
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
			logDebug("BBBJdaFacetValueDroplet : service() : End");
		}
	}

	
	
	public JSONArray fetchFacetValueByFacetId(String pJdaDeptId, String pJdaSubDeptId, String pJdaClass, String pFacetId) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetValueDroplet : fetchFacetValueByFacetId() : Start");
		}
		JSONArray jsonJdaFacetValues = new JSONArray();
		RepositoryItem []jdaFacetItems = getAdminManager().fetchJdaFacetValueByFacetId(pJdaDeptId,pJdaSubDeptId,pJdaClass,pFacetId);
		if(jdaFacetItems!=null && jdaFacetItems.length>0){
			for(RepositoryItem facetItem : jdaFacetItems){
				if (isLoggingDebug()) {
					logDebug("Creating json object for ephFacet id : "+facetItem.getRepositoryId());
				}
				JSONObject jsonFacetEph = getAdminManager().createSingleJdaFacetValueJsonObject(facetItem);
				jsonJdaFacetValues.put(jsonFacetEph);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetValueDroplet : fetchFacetValueByFacetId() : End");
		}
		return jsonJdaFacetValues;

	}
	
	
	
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetValueDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetValueDroplet : setPageResponse() : End");
		}
	}
}
