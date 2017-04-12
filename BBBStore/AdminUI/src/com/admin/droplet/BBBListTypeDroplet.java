package com.admin.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.json.JSONArray;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import com.admin.constant.BBBAdminConstant;
import com.admin.manager.BBBAdminManager;
/**
 * This droplet is used to fetch all list type items
 * @author Logixal
 *
 */
public class BBBListTypeDroplet extends DynamoServlet {

	private Repository adminRepository;
	private BBBAdminManager adminManager;

	public BBBAdminManager getAdminManager() {
		return adminManager;
	}

	public void setAdminManager(BBBAdminManager adminManager) {
		this.adminManager = adminManager;
	}

	public Repository getAdminRepository() {
		return adminRepository;
	}

	public void setAdminRepository(Repository adminRepository) {
		this.adminRepository = adminRepository;
	}

	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (isLoggingDebug()) {
			logDebug("BBBListTypeDroplet : service() : Start");
		}
		JSONArray jsonListTypes = new JSONArray();
		JSONObject jsonResponse = new JSONObject();
		JSONObject errorJsonResponse = null;
		JSONObject errorJson = new JSONObject();
		try {
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);

			 jsonListTypes  = getListTypes();
			
				jsonResponse.put("status", "ok");
				jsonResponse.put("data", jsonListTypes);
			

		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "64");
				errorJson.put("description", "Error fetching list type");
				jsonResponse.put("status", "error");
				jsonResponse.put("message",errorJson);
				if(isLoggingError()){
					logError("Exception : "+e);
				}
			} catch (JSONException e1) {
				if (isLoggingError()) {
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBListTypeDroplet : service() : End");
				}
				return;
			}

		} catch (JSONException e) {
			if (isLoggingError()) {
				logError("Exception : "+e);
			}
			
			setPageResponse(pResponse, errorJsonResponse);
			if (isLoggingDebug()) {
				logDebug("BBBListTypeDroplet : service() : End");
			}
			return;
		}
		setPageResponse(pResponse,jsonResponse);
		if (isLoggingDebug()) {
			logDebug("BBBListTypeDroplet : service() : End");
		}
	}
/**
 * 
 * @return Array of list types in Json format
 * @throws RepositoryException
 * @throws JSONException
 * @throws ServletException
 * @throws IOException
 */
	public JSONArray getListTypes() throws RepositoryException, JSONException, ServletException, IOException{
		if (isLoggingDebug()) {
			logDebug("BBBListTypeDroplet : getListTypes() : Start");
		}
		RepositoryItem[] listTypes = getAdminManager().fetchAllFromListType();

		JSONArray jsonListTypes = new JSONArray();
		if(listTypes!=null && listTypes.length>0){
			for (RepositoryItem listType : listTypes) {
				if (isLoggingDebug()) {
					logDebug("Creating json for list type with id : "+listType.getRepositoryId());
				}
				JSONObject jsonListType = getAdminManager().createSingleListTypeJsonObject(listType);
				jsonListTypes.add(jsonListType);
			}
		}
		if (isLoggingDebug()) {
			logDebug("BBBListTypeDroplet : getListTypes() : End");
		}
		return jsonListTypes;

	}
/**
 * 
 * @param pResponse
 * @param pResponseObject
 * @throws IOException
 */
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBListTypeDroplet : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug("Response : "+pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBListTypeDroplet : setPageResponse() : End");
		}
	}

}
