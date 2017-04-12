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
 * This droplet is used to get eph node based on parameter provided.
 * @author Logixal
 *
 */
public class BBBEphNodeDroplet extends DynamoServlet {

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
			logDebug("BBBEphNodeDroplet : service() : Start");
		}
		JSONObject jsonResponse = new JSONObject();
		JSONArray jsonEphNode = new JSONArray();
		JSONObject errorJsonResponse = null;
		JSONObject errorJson = new JSONObject();
		try {
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);
			String ephNodeId = pRequest.getParameter("eph_node_id");
			String displayName = pRequest.getParameter("display_name");
			String ephParentId=pRequest.getParameter("parent_node_id");
			
			 
				if((ephNodeId!=null && ephNodeId.trim().equals("")||displayName!=null && displayName.trim().equals("")) && ephParentId!=null && ephParentId.trim().equals("")){
				if (isLoggingDebug()) {
					logDebug("Get All Parent ephNode : "+ephNodeId);
				}
				jsonEphNode=getAllRootEphNode();
			} else  if(ephParentId!=null && ephParentId.trim().equals("") && displayName!=null && !displayName.trim().equals("")){
				if (isLoggingDebug()) {
					logDebug("Search Parent ephNode by name: "+displayName);
				}
				jsonEphNode=searchParentEphByDisplayName(displayName);
				
			} else  if(ephParentId!=null && ephParentId.trim().equals("") && ephNodeId!=null && !ephNodeId.trim().equals("")){
				if (isLoggingDebug()) {
					logDebug("Search Parent ephNode by name: "+ephNodeId);
				}
				jsonEphNode=searchParentEphId(ephNodeId);
				
			}
				else if (ephParentId!=null && !ephParentId.trim().equals("") && displayName!=null && !displayName.trim().equals("")){
					if (isLoggingDebug()) {
						logDebug("Search child ephNode by display name : "+displayName);
					}
					jsonEphNode = searchChildEphNodeByName(ephParentId,displayName);
				}
				else if (ephParentId!=null && !ephParentId.trim().equals("") && ephNodeId!=null && !ephNodeId.trim().equals("")){
					if (isLoggingDebug()) {
						logDebug("Search child ephNode by node  id : "+ephNodeId);
					}
					jsonEphNode = searchChildEphNodeById(ephParentId,ephNodeId);
				}
			
				else if(ephParentId!=null && ephParentId!="" && (displayName!=null && displayName.trim().equals("") || ephNodeId!=null &&ephNodeId.trim().equals(""))){
					if (isLoggingDebug()) {
						logDebug("Fetch all child node of ephNode : "+ephParentId);
					}
					jsonEphNode=fetchChildEphNodes(ephParentId,displayName);
				}
				
				
			
			
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonEphNode);
			

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "52");
				errorJson.put("description", "Error while fetching EphNode");
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
			logDebug("BBBEphNodeDroplet : service() : End");
		}
	}

	public JSONArray searchParentEphId(String pNodeId) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : searchByEphNodeId() : Start");
		}
		JSONArray jsonNodeEph = new JSONArray();
		RepositoryItem[] ephNodeItem = getAdminManager().searchParentEphNodeId(pNodeId);
		if(ephNodeItem!=null && ephNodeItem.length>0){
			for(RepositoryItem ephItem : ephNodeItem){
				if (isLoggingDebug()) {
					logDebug("Creating json object for ephNode id : "+ephItem.getRepositoryId());
				}
				JSONObject jsonEphNode = getAdminManager().createSingleEphNodeJsonObject(ephItem);
				jsonNodeEph.put(jsonEphNode);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : searchByEphNodeId() : End");
		}
		return jsonNodeEph;

	}
	/*
	public JSONArray fetchEphNodeById(String pEphNodeId) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : fetchEphNodeById() : Start");
		}
		JSONArray ephNodeJson = new JSONArray();
		RepositoryItem[] ephNodeItems = getAdminManager().fetchEphNodeById(pEphNodeId);
		if(ephNodeItems!=null){
			for(RepositoryItem ephNodeItem:ephNodeItems){
			JSONObject jsonEphNode = getAdminManager().createSingleEphNodeJsonObject(ephNodeItem);
			ephNodeJson.put(jsonEphNode);
			
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : fetchEphNodeById() : End");
		}
		return ephNodeJson;

	}
	*/
	public JSONArray searchParentEphByDisplayName(String pDisplayName) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : searchByDisplayName() : Start");
		}
		JSONArray jsonNodeEph = new JSONArray();
		RepositoryItem[] ephNodeItem = getAdminManager().searchParentNodeByDisplayName(pDisplayName);
		if(ephNodeItem!=null && ephNodeItem.length>0){
			for(RepositoryItem ephItem : ephNodeItem){
				if (isLoggingDebug()) {
					logDebug("Creating json object for ephNode id : "+ephItem.getRepositoryId());
				}
				JSONObject jsonEph = getAdminManager().createSingleEphNodeJsonObject(ephItem);
				jsonNodeEph.put(jsonEph);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : searchByDisplayName() : End");
		}
		return jsonNodeEph;

	}
	
	public JSONArray fetchChildEphNodes(String pParentNodeId,String pDisplayName) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : fetchChildEphNodes() : Start");
		}
		JSONArray jsonNodeEph = new JSONArray();
		List<RepositoryItem> childNodeArray=getAdminManager().fetchChildEphNodes(pParentNodeId, pDisplayName);
		for (RepositoryItem childItem : childNodeArray){
			if (isLoggingDebug()) {
				logDebug("Creating json object for ephNode id : "+childItem.getRepositoryId());
			}
		if(childNodeArray!=null){
			JSONObject jsonEph = getAdminManager().createSingleEphNodeJsonObject(childItem);
			jsonNodeEph.put(jsonEph);
		}
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : fetchChildEphNodes() : End");
		}
		return jsonNodeEph;

	}
	
	public JSONArray searchChildEphNodeByName(String pParentNodeId,String pDisplayName) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : searchChildEphNode() : Start");
		}
		JSONArray jsonNodeEph = new JSONArray();
		List<RepositoryItem> childNodeArray=getAdminManager().searchChildEphNodeByName(pParentNodeId, pDisplayName);
		for (RepositoryItem ephItem : childNodeArray){
			if (isLoggingDebug()) {
				logDebug("Creating json object for ephNode id : "+ephItem.getRepositoryId());
			}
		if(childNodeArray!=null){
			JSONObject jsonEph = getAdminManager().createSingleEphNodeJsonObject(ephItem);
			jsonNodeEph.put(jsonEph);
		}
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : searchChildEphNode() : End");
		}
		return jsonNodeEph;

	}
	
	public JSONArray searchChildEphNodeById(String pParentNodeId,String pNodeId) throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : searchChildEphNode() : Start");
		}
		JSONArray jsonNodeEph = new JSONArray();
		List<RepositoryItem> childNodeArray=getAdminManager().searchChildEphNodeById(pParentNodeId, pNodeId);
		for (RepositoryItem ephItem : childNodeArray){
			if (isLoggingDebug()) {
				logDebug("Creating json object for ephNode id : "+ephItem.getRepositoryId());
			}
		if(childNodeArray!=null){
			JSONObject jsonEph = getAdminManager().createSingleEphNodeJsonObject(ephItem);
			jsonNodeEph.put(jsonEph);
		}
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : searchChildEphNode() : End");
		}
		return jsonNodeEph;

	}
	
	
	
	public JSONArray getAllRootEphNode() throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : searchChildEphNode() : Start");
		}
		JSONArray jsonNodeEph = new JSONArray();
		RepositoryItem []ephNodeItems =getAdminManager().getAllRootNodes();
		for (RepositoryItem ephNodeItem : ephNodeItems){
			
		if(ephNodeItem!=null){
			if (isLoggingDebug()) {
				logDebug("Creating json object for ephNode id : "+ephNodeItem.getRepositoryId());
			}
			JSONObject jsonEph = getAdminManager().createSingleEphNodeJsonObject(ephNodeItem);
			jsonNodeEph.put(jsonEph);
		}
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : searchChildEphNode() : End");
		}
		return jsonNodeEph;

	}
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphNodeDroplet : setPageResponse() : End");
		}
	}
}
