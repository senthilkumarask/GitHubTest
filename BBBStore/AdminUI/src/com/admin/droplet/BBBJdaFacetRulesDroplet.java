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

public class BBBJdaFacetRulesDroplet extends DynamoServlet {
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
			logDebug("BBBJdaFacetRulesDroplet : service() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonEphRules = new JSONArray();
		JSONObject errorJsonResponse = null;
		JSONObject errorJson = new JSONObject();
		try {
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);

			
			String categoryId = pRequest.getParameter("list_cat_id");
			
		
			
			if(categoryId!=null && !categoryId.trim().equals("")){
				if (isLoggingDebug()) {
					logDebug("Getting eph rule for  category : "+categoryId);
				}
				jsonEphRules =getJdaRules(categoryId);
			 }
			
			
			
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonEphRules);
			

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "59");
				errorJson.put("description", "Error while fetching jda rules");
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
			logDebug("BBBJdaFacetRulesDroplet : service() : End");
		}
	}
	
	
	
	public JSONArray getJdaRules(String pCategoryId) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetRulesDroplet : getJdaRules() : Start");
		}
		JSONArray jsonJdaRules = new JSONArray();
		List<RepositoryItem> jdaRules =  (List<RepositoryItem>)getAdminManager().getJdaRules(pCategoryId); 
		if(jdaRules!=null && !jdaRules.isEmpty()){
			for(RepositoryItem jdaRule : jdaRules){
				if (isLoggingDebug()) {
					logDebug("Creating json object for eph rule id : "+jdaRule.getRepositoryId());
				}
				JSONObject jsonJdaRule = getAdminManager().createSingleJdaRuleJsonObject(jdaRule,pCategoryId);
				jsonJdaRules.put(jsonJdaRule);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetRulesDroplet : getJdaRules() : End");
		}
		return jsonJdaRules;

	}
	

	
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetRulesDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaFacetRulesDroplet : setPageResponse() : End");
		}
	}
}
