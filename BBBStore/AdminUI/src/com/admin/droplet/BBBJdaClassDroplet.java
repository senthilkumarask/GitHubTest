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

public class BBBJdaClassDroplet extends DynamoServlet {
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
			logDebug("BBBJdaClassDroplet : service() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonJdaSubDepts = new JSONArray();
		JSONObject errorJsonResponse = null;
		JSONObject errorJson = new JSONObject();
		try {
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);
			String jdaClassId = pRequest.getParameter("jda_class");
			String jdaClassName =  pRequest.getParameter("descrip");
			String jdaDeptId =  pRequest.getParameter("jda_dept_id");
			String jdaSubDeptId =  pRequest.getParameter("jda_sub_dept_id");
			
			
			if(jdaDeptId!=null && !jdaDeptId.trim().equals("") && jdaSubDeptId!=null && !jdaSubDeptId.trim().equals("")){
				if (isLoggingDebug()) {
					logDebug("Getting jda class for jda dept : "+jdaDeptId+" and sub dept id : "+jdaSubDeptId);
				}
				if(jdaClassId!=null && jdaClassId.trim().equals("")|| jdaClassName!=null && jdaClassName.trim().equals("")){
					if (isLoggingDebug()) {
						logDebug("Getting all jda class");
					}
					jsonJdaSubDepts = getAllJdaClass(jdaDeptId, jdaSubDeptId);
				} else if(jdaClassId!=null && !jdaClassId.trim().equals("")){
					if (isLoggingDebug()) {
						logDebug("Searching jda class by id : "+jdaClassId);
					}
					jsonJdaSubDepts = searchJdaClassById(jdaDeptId,jdaSubDeptId,jdaClassId);
				}else if(jdaClassName!=null && !jdaClassName.trim().equals("")){
					if (isLoggingDebug()) {
						logDebug("Searching jda class by name : "+jdaClassName);
					}
					jsonJdaSubDepts = searchJdaClassByName(jdaDeptId, jdaSubDeptId, jdaClassName);
				}
				
				
			}
			
			
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonJdaSubDepts);
			 

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "56");
				errorJson.put("description", "Error while fetching Jda Class");
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
			logDebug("BBBJdaClassDroplet : service() : End");
		}
	}
	
	public JSONArray getAllJdaClass(String pJdaDeptId, String pJdaSubDeptId) throws RepositoryException, JSONException, ServletException, IOException{
		
		if (isLoggingDebug()) {
			logDebug("BBBJdaClassDroplet : getAllJdaClass() : Start");
		}
		JSONArray jsonJdaClass = new JSONArray();
		RepositoryItem jdaClasses[] = getAdminManager().fetchAllClass(pJdaDeptId, pJdaSubDeptId);
		if(jdaClasses!=null){
			JSONObject jdaClassJson;
			for(RepositoryItem jdaClass:jdaClasses){
				jdaClassJson = getAdminManager().createSingleJdaClassJson(jdaClass);	
				jsonJdaClass.put(jdaClassJson);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaClassDroplet : getAllJdaClass() : End");
		}
		
		return jsonJdaClass;
	}
	
public JSONArray searchJdaClassById(String pJdaDeptId, String pJdaSubDeptId,String pJdaClassId) throws RepositoryException, JSONException, ServletException, IOException{
		
		if (isLoggingDebug()) {
			logDebug("BBBJdaClassDroplet : searchJdaClassById() : Start");
		}
		JSONArray jsonJdaClasses = new JSONArray();
		RepositoryItem jdaClasses[] = getAdminManager().searchJdaClassById(pJdaDeptId, pJdaSubDeptId, pJdaClassId);
		if(jdaClasses!=null){
			JSONObject jdaClassJson;
			for(RepositoryItem jdaClass:jdaClasses){
				jdaClassJson = getAdminManager().createSingleJdaClassJson(jdaClass);	
				jsonJdaClasses.put(jdaClassJson);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaClassDroplet : searchJdaClassById() : End");
		}
		
		return jsonJdaClasses;
	}
public JSONArray searchJdaClassByName(String pJdaDeptId, String pJdaSubDeptId, String pJdaClassName) throws RepositoryException, JSONException, ServletException, IOException{
	
	if (isLoggingDebug()) {
		logDebug("BBBJdaClassDroplet : searchJdaClassByName() : Start");
	}
	JSONArray jsonJdaClasses = new JSONArray();
	RepositoryItem jdaClasses[] = getAdminManager().searchJdaClassByName(pJdaDeptId, pJdaSubDeptId, pJdaClassName);
	if(jdaClasses!=null){
		JSONObject jdaClassJson;
		for(RepositoryItem jdaClass:jdaClasses){
			jdaClassJson = getAdminManager().createSingleJdaClassJson(jdaClass);	
			jsonJdaClasses.put(jdaClassJson);
		}
	}
	if (isLoggingDebug()) {
		logDebug("BBBJdaClassDroplet : searchJdaClassByName() : End");
	}
	
	return jsonJdaClasses;
}
	
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBJdaClassDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaClassDroplet : setPageResponse() : End");
		}
	}
}
