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

public class BBBFacetRuleDroplet extends DynamoServlet {
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
			logDebug("BBBFacetRuleDroplet : service() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonFacetRules = new JSONArray();
		JSONObject errorJsonResponse = null;
		JSONObject errorJson = new JSONObject();
		try {
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);
			String facetRuleId=pRequest.getParameter("rule_id");
			if(facetRuleId!=null){
			facetRuleId = facetRuleId.trim();}
			String facetRuleName = pRequest.getParameter("facet_rule_name");
			if(facetRuleName!=null){
				facetRuleName= facetRuleName.trim();
			}
			String categoryId=pRequest.getParameter("list_cat_id");
		
			if(categoryId!=null && categoryId!=""){
				if (isLoggingDebug()) {
					logDebug("Getting facets from category with id : "+categoryId);
				}
				jsonFacetRules = getFacetRulesFromCategories(categoryId);	
			
			}
			if(facetRuleId!=null && facetRuleId!=""){
				if (isLoggingDebug()) {
					logDebug("Getting all facet rules with id "+facetRuleId);
				}
				jsonFacetRules = getFacetRulesById(facetRuleId);
				
			}
			if(facetRuleName!=null && facetRuleName!=""){
				if (isLoggingDebug()) {
					logDebug("Getting all facet rules with rule name: "+facetRuleName);
				}
				jsonFacetRules = getFacetRulesByName(facetRuleName);
				
			}
			
			
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonFacetRules);
			

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "54");
				errorJson.put("description", "Error while fetching facet rule");
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
			logDebug("BBBFacetRuleDroplet : service() : End");
		}
	}
	
	public JSONArray getFacetRulesFromCategories(String pCategoryId) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBFacetRuleDroplet : getFacetRulesFromCategories() : Start");
		}
		JSONArray jsonFacetRule = new JSONArray();
		List<RepositoryItem> facetRules =  (List<RepositoryItem>)getAdminManager().fetchFacetRulesFromCategory(pCategoryId); 
		if(facetRules!=null && !facetRules.isEmpty()){
			for(RepositoryItem facetRule : facetRules){
				if (isLoggingDebug()) {
					logDebug("Creating json object for facet rule id : "+facetRule.getRepositoryId());
				}
				JSONObject jsonFacets = getAdminManager().createSingleFacetRuleJsonObject(facetRule,pCategoryId);
				jsonFacetRule.put(jsonFacets);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBFacetRulesDroplet : getFacetRulesFromCategories() : End");
		}
		return jsonFacetRule;

	}		
public JSONArray getFacetRulesById(String pRuleId) throws RepositoryException, JSONException, ServletException, IOException{
		
		if (isLoggingDebug()) {
			logDebug("BBBFacetRulesDroplet : getFacetRulesById() : Start");
		}
		JSONArray jsonFacetRules = new JSONArray();
		RepositoryItem[]facetRules = getAdminManager().searchFacetRulesById(pRuleId);
		if(facetRules!=null && facetRules.length>0){
		for(RepositoryItem facetRule : facetRules){
			JSONObject facetRuleJson=  getAdminManager().createSingleFacetRuleJsonObject(facetRule,"");
			jsonFacetRules.put(facetRuleJson);
		}
}
		
		if (isLoggingDebug()) {
			logDebug("BBBFacetRulesDroplet : getFacetRulesById() : End");
		}
		
		return jsonFacetRules;
	}

public JSONArray getFacetRulesByName(String pRuleName) throws RepositoryException, JSONException, ServletException, IOException{
	
	if (isLoggingDebug()) {
		logDebug("BBBFacetRulesDroplet : getFacetRulesByName() : Start");
	}
	JSONArray jsonFacetRules = new JSONArray();
	RepositoryItem[]facetRules = getAdminManager().searchFacetRulesByName(pRuleName);
	if(facetRules!=null && facetRules.length>0){
	for(RepositoryItem facetRule : facetRules){
		JSONObject facetRuleJson=  getAdminManager().createSingleFacetRuleJsonObject(facetRule, "");
		jsonFacetRules.put(facetRuleJson);
	}
	}
	
	if (isLoggingDebug()) {
		logDebug("BBBFacetRulesDroplet : getFacetRulesByName() : End");
	}
	
	return jsonFacetRules;
}
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBFacetRulesDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBFacetRulesDroplet : setPageResponse() : End");
		}
	}
}
