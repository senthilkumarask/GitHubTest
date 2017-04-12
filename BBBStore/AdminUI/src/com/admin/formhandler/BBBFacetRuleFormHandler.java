package com.admin.formhandler;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.droplet.GenericFormHandler;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.json.JSONArray;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.admin.constant.BBBAdminConstant;
import com.admin.manager.BBBAdminManager;
/**
 * This formhandler is used for handling operations on the rule item
 * @author Logixal
 *
 */
public class BBBFacetRuleFormHandler extends GenericFormHandler{

	
	private String mCategoryId;
	private String mFacetRuleName;
	private String mFacetValuePair;
	private String mUserCreated;
	private String mLastModifiedUser;
	TransactionManager mTransactionManager;
	private BBBAdminManager mAdminManager;
	private String mRuleId;
	private int mIncludeAll;
	Profile mProfile;

	public Profile getProfile() {
		return mProfile;
	}

	public void setProfile(Profile pProfile) {
		this.mProfile = pProfile;
	}
	
	public int getIncludeAll() {
		return mIncludeAll;
	}
	public void setIncludeAll(int pIncludeAll) {
		this.mIncludeAll = pIncludeAll;
	}
	public String getRuleId() {
		return mRuleId;
	}
	public void setRuleId(String mRuleId) {
		this.mRuleId = mRuleId;
	}
	public BBBAdminManager getAdminManager() {
		return mAdminManager;
	}
	public void setAdminManager(BBBAdminManager pAdminManager) {
		mAdminManager = pAdminManager;
	}

	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	public void setTransactionManager(TransactionManager pTransactionManager) {
		this.mTransactionManager = pTransactionManager;
	}
	
	public String getCategoryId() {
		return mCategoryId;
	}
	public void setCategoryId(String pCategoryId) {
		this.mCategoryId = pCategoryId;
	}

	public String getFacetRuleName() {
		return mFacetRuleName;
	}
	public void setFacetRuleName(String pFacetRuleName) {
		this.mFacetRuleName = pFacetRuleName;
	}
	public String getFacetValuePair() {
		return mFacetValuePair;
	}
	public void setFacetValuePair(String pFacetValuePair) {
		this.mFacetValuePair = pFacetValuePair;
	}
	public String getUserCreated() {
		String loginName = (String) getProfile().getPropertyValue("login");
		setUserCreated(loginName);
		return mUserCreated;
	}
	public void setUserCreated(String pUserCreated) {
		this.mUserCreated = pUserCreated;
	}
	public String getLastModifiedUser() {
		String loginName = (String) getProfile().getPropertyValue("login");
		setLastModifiedUser(loginName);
		return mLastModifiedUser;
	}
	public void setLastModifiedUser(String pLastModifiedUser) {
		this.mLastModifiedUser = pLastModifiedUser;
	}

	
	
	

	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return Category item in Json format.
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean handleCreateFacetRule(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBFacetRuleFormHandler : BBBFacetRuleFormHandler() : Start");
		}
		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = new TransactionDemarcation ();
		boolean success= true;
		JSONObject responseObj = new JSONObject();
		JSONArray jsonItemArray = new JSONArray();
		JSONObject errorJson = new JSONObject();
		JSONObject errorJsonResponse = null;
		try {
			td.begin(tm);
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);
			errorJson = getAdminManager().validateUser(getUserCreated(),getLastModifiedUser());
			if(errorJson.length()>0){
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}	
			RepositoryItem item = getAdminManager().createFacetRule(getCategoryId(), getFacetRuleName(), getFacetValuePair(), getUserCreated());
			JSONObject jsonItem = getAdminManager().createSingleFacetRuleJsonObject(item, getCategoryId());
			jsonItemArray.add(jsonItem);
			responseObj.put("data", jsonItemArray);
			responseObj.put("status", "ok");

		}

		catch(RepositoryException e){
			try {
				success=false;
				errorJson.put("id", "44");
				errorJson.put("description", "Error in facet rule creation");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				if(isLoggingError()){
					logError("Exception : "+e);
				}
			} catch (JSONException e1) {
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBFacetRuleFormHandler : handleCreatefacetRule() : End");
				}
				return false;
			}

		}
		catch(JSONException e){
			if(isLoggingError()){
				logError("Exception : "+e);
			}
			success=false;
			setPageResponse(pResponse, errorJsonResponse);
			if (isLoggingDebug()) {
				logDebug("BBBFacetRuleFormHandler : handleCreateFacetRule() : End");
			}
			return false;
		}  catch (TransactionDemarcationException e) {
			try {
				errorJson.put("id", "6");
				errorJson.put("description", "Transaction error");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				if(isLoggingError()){
					logError("Exception : "+e);
				}
			} catch (JSONException e1) {	
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBFacetRuleFormHandler : handleCreateFacetRule() : End");
				}
				return false;
			}
		} finally  {
			try {
				td.end(!success);
			} catch (TransactionDemarcationException e) {
				try {
					errorJson.put("id", "6");
					errorJson.put("description", "Transaction error");
					responseObj.put("message", errorJson);
					responseObj.put("status", "error");
					if(isLoggingError()){
						logError("Exception : "+e);
					}
				} catch (JSONException e1) {	
					if(isLoggingError()){
						logError("Exception : "+e1);
					}
					setPageResponse(pResponse, errorJsonResponse);
					if (isLoggingDebug()) {
						logDebug("BBBFacetRuleFormHandler : handleCreateFacetRule() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBFacetRuleFormHandler : handleCreateFacetRule() : End");
		}
		return false;

	}

	public final boolean handleUpdateFacetRule(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBFacetRuleFormHandler : handleUpdateFacetRule() : Start");
		}
		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = new TransactionDemarcation ();
		boolean success= true;
		JSONObject responseObj = new JSONObject();
		JSONArray jsonItemArray = new JSONArray();
		JSONObject errorJson = new JSONObject();
		JSONObject errorJsonResponse = null;
		try {
			td.begin(tm);
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);
			errorJson = getAdminManager().validateUser(getUserCreated(),getLastModifiedUser());
			if(errorJson.length()>0){
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}
			RepositoryItem item = getAdminManager().updateFacetRule(getRuleId(), getFacetRuleName(), getFacetValuePair(), getLastModifiedUser());
			if(item!=null){
				JSONObject jsonItem = getAdminManager().createSingleFacetRuleJsonObject(item,"");
				jsonItemArray.add(jsonItem);
			}
			responseObj.put("data", jsonItemArray);
			responseObj.put("status", "ok");

		}

		catch(RepositoryException e){
			try {
				success=false;
				errorJson.put("id", "45");
				errorJson.put("description", "Error in facet rule updation");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				if(isLoggingError()){
					logError("Exception : "+e);
				}
			} catch (JSONException e1) {
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBFacetRuleFormHandler : handleUpdateFacetRule() : End");
				}
				return false;
			}

		}
		catch(JSONException e){
			if(isLoggingError()){
				logError("Exception : "+e);
			}
			success=false;
			setPageResponse(pResponse, errorJsonResponse);
			if (isLoggingDebug()) {
				logDebug("BBBFacetRuleFormHandler : handleUpdateFacetRule() : End");
			}
			return false;
		}  catch (TransactionDemarcationException e) {
			try {
				errorJson.put("id", "6");
				errorJson.put("description", "Transaction error");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				if(isLoggingError()){
					logError("Exception : "+e);
				}
			} catch (JSONException e1) {	
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBFacetRuleFormHandler : handleUpdateFacetRule() : End");
				}
				return false;
			}
		} finally  {
			try {
				td.end(!success);
			} catch (TransactionDemarcationException e) {
				try {
					errorJson.put("id", "6");
					errorJson.put("description", "Transaction error");
					responseObj.put("message", errorJson);
					responseObj.put("status", "error");
					if(isLoggingError()){
						logError("Exception : "+e);
					}
				} catch (JSONException e1) {	
					if(isLoggingError()){
						logError("Exception : "+e1);
					}
					setPageResponse(pResponse, errorJsonResponse);
					if (isLoggingDebug()) {
						logDebug("BBBFacetRuleFormHandler : handleUpdateFacetRule() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBFacetRuleFormHandler : handleUpdateFacetRule() : End");
		}
		return false;


	}
	
	public final boolean handleRemoveFacetRule(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBFacetRuleFormHandler : handleRemoveFacetRules() : Start");
		}
		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = new TransactionDemarcation ();
		boolean success= true;
		JSONObject responseObj = new JSONObject();
		JSONObject errorJson = new JSONObject();
		JSONObject errorJsonResponse = null;
		try {
			td.begin(tm);
			errorJsonResponse = new JSONObject(BBBAdminConstant.JSON_ERROR_OBJECT);
			errorJson = getAdminManager().validateUser(getUserCreated(),getLastModifiedUser());
			if(errorJson.length()>0){
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}
			getAdminManager().deleteFacetRules(getCategoryId(),getRuleId());
			responseObj.put("status", "ok");
		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "46");
				errorJson.put("description", "Error in facet rule deletion");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
			} catch (JSONException e1) {	
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBFacetRuleFormHandler : handleRemoveFacetRules() : End");
				}
				return false;
			}
			if(isLoggingError()){
				e.printStackTrace();
			}
		} catch (SQLException e) {
			try {
				success=false;
				errorJson.put("id", "46");
				errorJson.put("description", "Error in facet rule deletion");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				if(isLoggingError()){
					logError("Exception : "+e);
				}
			} catch (JSONException e1) {	
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBFacetRuleFormHandler : handleRemoveFacetRules() : End");
				}
				return false;
			}
		} catch (JSONException e) {	
			if(isLoggingError()){
				logError("Exception : "+e);
			}
			success=false;
			setPageResponse(pResponse, errorJsonResponse);
			if (isLoggingDebug()) {
				logDebug("BBBFacetRuleFormHandler : handleRemoveFacet() : End");
			}
			return false;
		} catch (TransactionDemarcationException e) {
			try {
				success=false;
				errorJson.put("id", "6");
				errorJson.put("description", "Transaction error");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				if(isLoggingError()){
					logError("Exception : "+e);
				}
			} catch (JSONException e1) {	
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBFacetRuleFormHandler : handleRemoveFacetRules() : End");
				}
				return false;
			}
		} finally  {
			try {
				td.end(!success);
			} catch (TransactionDemarcationException e) {
				try {
					errorJson.put("id", "6");
					errorJson.put("description", "Transaction error");
					responseObj.put("message", errorJson);
					responseObj.put("status", "error");
					if(isLoggingError()){
						logError("Exception : "+e);
					}
				} catch (JSONException e1) {	
					if(isLoggingError()){
						logError("Exception : "+e1);
					}
					setPageResponse(pResponse, errorJsonResponse);
					if (isLoggingDebug()) {
						logDebug("BBBFacetRuleFormHandler : handleRemoveFacetRules() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBFacetRuleFormHandler : handleRemoveFacetRules() : End");
		}
		return false;
	}


	/**
	 * 
	 * @param pResponse
	 * @param pResponseObject
	 * @throws IOException
	 */
	public final void setPageResponse(final DynamoHttpServletResponse pResponse, JSONObject pResponseObject) throws IOException{
		if (isLoggingDebug()) {
			logDebug("BBBFacetRuleFormHandler : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug(pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBFacetRuleFormHandler : setPageResponse() : End");
		}
	}
}
