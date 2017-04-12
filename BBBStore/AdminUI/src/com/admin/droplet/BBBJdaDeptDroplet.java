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

public class BBBJdaDeptDroplet extends DynamoServlet {
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
			logDebug("BBBJdaDeptDroplet : service() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonJdaDepts = new JSONArray();
		JSONObject errorJsonResponse = null;
		JSONObject errorJson = new JSONObject();
		try {
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);

			String jdaDeptName =  pRequest.getParameter("descrip");
			String jdaDeptId =  pRequest.getParameter("jda_dept_id");
			
			if(jdaDeptName!=null && jdaDeptName.trim().equals("") || jdaDeptId!=null && jdaDeptId.trim().equals("")){
				if (isLoggingDebug()) {
					logDebug("Getting all jda dept");
				}
				jsonJdaDepts = getAllJdaDept();	
			
			}
			else if(jdaDeptName!=null && !jdaDeptName.trim().equals("")){
				if (isLoggingDebug()) {
					logDebug("Searching jda dept by name : "+jdaDeptName);
				}
				jsonJdaDepts = searchJdaDeptByName(jdaDeptName);	
			}else  if(jdaDeptId!=null && !jdaDeptId.trim().equals("")){
				if (isLoggingDebug()) {
					logDebug("Searching jda dept by id : "+jdaDeptId);
				}
				jsonJdaDepts = searchJdaDeptById(jdaDeptId);
			}
			
			
			
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonJdaDepts);
			

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "57");
				errorJson.put("description", "Error while fetching jda dept");
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
			logDebug("BBBJdaDeptDroplet : service() : End");
		}
	}
	
	public JSONArray getAllJdaDept() throws RepositoryException, JSONException, ServletException, IOException{
		
		if (isLoggingDebug()) {
			logDebug("BBBJdaDeptDroplet : getAllJdaDept() : Start");
		}
		JSONArray jsonJdaDepts = new JSONArray();
		RepositoryItem jdaDepts[] = getAdminManager().fetchAllJdaDept();
		if(jdaDepts!=null){
			JSONObject jdaDeptJson;
			for(RepositoryItem jdaDept:jdaDepts){
				jdaDeptJson = getAdminManager().createSingleJdaDeptJson(jdaDept);	
				jsonJdaDepts.put(jdaDeptJson);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaDeptDroplet : getAllJdaDept() : End");
		}
		
		return jsonJdaDepts;
	}
	
public JSONArray searchJdaDeptById(String pJdaDeptId) throws RepositoryException, JSONException, ServletException, IOException{
		
		if (isLoggingDebug()) {
			logDebug("BBBJdaDeptDroplet : searchJdaDeptById() : Start");
		}
		JSONArray jsonJdaDepts = new JSONArray();
		RepositoryItem jdaDepts[] = getAdminManager().searchJdaDeptById(pJdaDeptId);
		if(jdaDepts!=null){
			JSONObject jdaDeptJson;
			for(RepositoryItem jdaDept:jdaDepts){
				jdaDeptJson = getAdminManager().createSingleJdaDeptJson(jdaDept);	
				jsonJdaDepts.put(jdaDeptJson);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaDeptDroplet : searchJdaDeptById() : End");
		}
		
		return jsonJdaDepts;
	}
public JSONArray searchJdaDeptByName(String pJdaDeptName) throws RepositoryException, JSONException, ServletException, IOException{
	
	if (isLoggingDebug()) {
		logDebug("BBBJdaDeptDroplet : searchJdaDeptByName() : Start");
	}
	JSONArray jsonJdaDepts = new JSONArray();
	RepositoryItem jdaDepts[] = getAdminManager().searchJdaDeptByName(pJdaDeptName);
	if(jdaDepts!=null){
		JSONObject jdaDeptJson;
		for(RepositoryItem jdaDept:jdaDepts){
			jdaDeptJson = getAdminManager().createSingleJdaDeptJson(jdaDept);	
			jsonJdaDepts.put(jdaDeptJson);
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBJdaDeptDroplet : searchJdaDeptByName() : End");
	}
	
	return jsonJdaDepts;
}
	
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBJdaDeptDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaDeptDroplet : setPageResponse() : End");
		}
	}
}
