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

public class BBBJdaSubDeptDroplet extends DynamoServlet {
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
		JSONArray jsonJdaSubDepts = new JSONArray();
		JSONObject errorJsonResponse = null;
		JSONObject errorJson = new JSONObject();
		try {
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);

			String jdaSubDeptName =  pRequest.getParameter("descrip");
			String jdaDeptId =  pRequest.getParameter("jda_dept_id");
			String jdaSubDeptId =  pRequest.getParameter("jda_sub_dept_id");
			
			
			if(jdaDeptId!=null && !jdaDeptId.trim().equals("")){
				
				if(jdaSubDeptId!=null && jdaSubDeptId.trim().equals("")|| jdaSubDeptName!=null && jdaSubDeptName.trim().equals("")){
					if (isLoggingDebug()) {
						logDebug("Getting all jda sub dept");
					}
					jsonJdaSubDepts = getAllJdaSubDept(jdaDeptId);
				} else if(jdaSubDeptId!=null && !jdaSubDeptId.trim().equals("")){
					if (isLoggingDebug()) {
						logDebug("Searching jda sub department by id : "+jdaSubDeptId);
					}
					jsonJdaSubDepts = searchJdaSubDeptById(jdaDeptId,jdaSubDeptId);
				}else if(jdaSubDeptName!=null && !jdaSubDeptName.trim().equals("")){
					if (isLoggingDebug()) {
						logDebug("Searching jda sub department by name : "+jdaSubDeptName);
					}
					jsonJdaSubDepts = searchJdaSubDeptByName(jdaDeptId,jdaSubDeptName);
				}
				
				
			}
			
			
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonJdaSubDepts);
			

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "61");
				errorJson.put("description", "Error while fetching jda sub dept");
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
	
	public JSONArray getAllJdaSubDept(String pJdaDeptId) throws RepositoryException, JSONException, ServletException, IOException{
		
		if (isLoggingDebug()) {
			logDebug("BBBJdaSubDeptDroplet : getAllJdaSubDept() : Start");
		}
		JSONArray jsonJdaSubDepts = new JSONArray();
		RepositoryItem jdaSubDepts[] = getAdminManager().fetchAllJdaSubDept(pJdaDeptId);
		if(jdaSubDepts!=null){
			JSONObject jdaSubDeptJson;
			for(RepositoryItem jdaSubDept:jdaSubDepts){
				jdaSubDeptJson = getAdminManager().createSingleJdaSubDeptJson(jdaSubDept);	
				jsonJdaSubDepts.put(jdaSubDeptJson);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaSubDeptDroplet : getAllJdaSubDept() : End");
		}
		
		return jsonJdaSubDepts;
	}
	
public JSONArray searchJdaSubDeptById(String pJdaDeptId, String pJdaSubDeptId) throws RepositoryException, JSONException, ServletException, IOException{
		
		if (isLoggingDebug()) {
			logDebug("BBBJdaSubDeptDroplet : searchJdaSubDeptById() : Start");
		}
		JSONArray jsonJdaSubDepts = new JSONArray();
		RepositoryItem jdaSubDepts[] = getAdminManager().searchJdaSubDeptById(pJdaDeptId, pJdaSubDeptId);
		if(jdaSubDepts!=null){
			JSONObject jdaSubDeptJson;
			for(RepositoryItem jdaSubDept:jdaSubDepts){
				jdaSubDeptJson = getAdminManager().createSingleJdaSubDeptJson(jdaSubDept);	
				jsonJdaSubDepts.put(jdaSubDeptJson);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaSubDeptDroplet : searchJdaSubDeptById() : End");
		}
		
		return jsonJdaSubDepts;
	}
public JSONArray searchJdaSubDeptByName(String pJdaDeptId, String pJdaSubDeptName) throws RepositoryException, JSONException, ServletException, IOException{
	
	if (isLoggingDebug()) {
		logDebug("BBBJdaSubDeptDroplet : createSingleJdaSubDeptJson() : Start");
	}
	JSONArray jsonJdaSubDepts = new JSONArray();
	RepositoryItem jdaSubDepts[] = getAdminManager().searchJdaSubDeptByName(pJdaDeptId, pJdaSubDeptName);
	if(jdaSubDepts!=null){
		JSONObject jdaSubDeptJson;
		for(RepositoryItem jdaSubDept:jdaSubDepts){
			jdaSubDeptJson = getAdminManager().createSingleJdaSubDeptJson(jdaSubDept);	
			jsonJdaSubDepts.put(jdaSubDeptJson);
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBJdaSubDeptDroplet : createSingleJdaSubDeptJson() : End");
	}
	
	return jsonJdaSubDepts;
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
