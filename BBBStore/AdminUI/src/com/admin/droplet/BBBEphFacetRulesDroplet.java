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

public class BBBEphFacetRulesDroplet extends DynamoServlet {
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
			logDebug("BBBEphFacetRulesDroplet : service() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonEphRules = new JSONArray();
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
			String categoryId = pRequest.getParameter("list_cat_id");
			
		
			if(facetRuleId!=null && facetRuleId.equals("") || facetRuleName!=null && facetRuleName.equals("")){
				if (isLoggingDebug()) {
					logDebug("Getting all EPH facet rules");
				}
				jsonEphRules = getAllFacetRules();	
			
			}
			if(categoryId!=null && categoryId!=""){
				if (isLoggingDebug()) {
					logDebug("Getting eph rule for  category : "+categoryId);
				}
				jsonEphRules =getEphRules(categoryId);
			 }
			if(facetRuleId!=null && facetRuleId!=""){
				if (isLoggingDebug()) {
					logDebug("Getting all EPH facet rules with id "+facetRuleId);
				}
				jsonEphRules = getEphFacetRulesById(facetRuleId);
				
			}
			if(facetRuleName!=null && facetRuleName!=""){
				if (isLoggingDebug()) {
					logDebug("Getting all EPH facet rules with rule name: "+facetRuleName);
				}
				jsonEphRules = getEphFacetRulesByName(facetRuleName);
				
			}
			
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonEphRules);
			

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "50");
				errorJson.put("description", "Error while fetching eph rules");
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
			logDebug("BBBEphFacetRulesDroplet : service() : End");
		}
	}
	
	public JSONArray getAllFacetRules() throws RepositoryException, JSONException, ServletException, IOException{
		
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetRulesDroplet : getAllFacetRules() : Start");
		}
		JSONArray jsonEphFacetRules = new JSONArray();
		RepositoryItem[]facetRules = getAdminManager().fetchAllRules();
		for(RepositoryItem facetRule : facetRules){
			JSONObject facetRuleJson=  getAdminManager().createSingleEphRuleJsonObject(facetRule, "");
			jsonEphFacetRules.put(facetRuleJson);
		}
		
		
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetRulesDroplet : getAllFacetRules() : End");
		}
		
		return jsonEphFacetRules;
	}
	
	public JSONArray getEphRules(String pCategoryId) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetRulesDroplet : getEphRules() : Start");
		}
		JSONArray jsonNodeEph = new JSONArray();
		List<RepositoryItem> ephRules =  (List<RepositoryItem>)getAdminManager().getEphRules(pCategoryId); 
		if(ephRules!=null && !ephRules.isEmpty()){
			for(RepositoryItem ephRule : ephRules){
				if (isLoggingDebug()) {
					logDebug("Creating json object for eph rule id : "+ephRule.getRepositoryId());
				}
				JSONObject jsonEphNode = getAdminManager().createSingleEphRuleJsonObject(ephRule,pCategoryId);
				jsonNodeEph.put(jsonEphNode);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetRulesDroplet : getEphRules() : End");
		}
		return jsonNodeEph;

	}
	
public JSONArray getEphFacetRulesById(String pRuleId) throws RepositoryException, JSONException, ServletException, IOException{
		
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetRulesDroplet : getEphFacetRulesById() : Start");
		}
		JSONArray jsonEphFacetRules = new JSONArray();
		RepositoryItem[]facetRules = getAdminManager().searchEphRulesById(pRuleId);
		if(facetRules!=null && facetRules.length>0){
		for(RepositoryItem facetRule : facetRules){
			JSONObject facetRuleJson=  getAdminManager().createSingleEphRuleJsonObject(facetRule, "");
			jsonEphFacetRules.put(facetRuleJson);
		}
}
		
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetRulesDroplet : getEphFacetRulesById() : End");
		}
		
		return jsonEphFacetRules;
	}

public JSONArray getEphFacetRulesByName(String pRuleName) throws RepositoryException, JSONException, ServletException, IOException{
	
	if (isLoggingDebug()) {
		logDebug("BBBEphFacetRulesDroplet : getEphFacetRulesByName() : Start");
	}
	JSONArray jsonEphFacetRules = new JSONArray();
	RepositoryItem[]facetRules = getAdminManager().searchEphRulesByName(pRuleName);
	if(facetRules!=null && facetRules.length>0){
	for(RepositoryItem facetRule : facetRules){
		JSONObject facetRuleJson=  getAdminManager().createSingleEphRuleJsonObject(facetRule, "");
		jsonEphFacetRules.put(facetRuleJson);
	}
	}
	
	if (isLoggingDebug()) {
		logDebug("BBBEphFacetRulesDroplet : getEphFacetRulesByName() : End");
	}
	
	return jsonEphFacetRules;
}

	
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetRulesDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphFacetRulesDroplet : setPageResponse() : End");
		}
	}
}
