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
public class BBBJdaRuleFormHandler extends GenericFormHandler{

	
	private String mCategoryId;
	private String mJdaDept;
	private String mJdaSubDept;
	private String mJdaClass;
	private String mFacetRuleName;
	private String mFacetValuePair;
	private String mUserCreated;
	private String mLastModifiedUser;
	TransactionManager mTransactionManager;
	private BBBAdminManager mAdminManager;
	private String mRuleId;
	Profile mProfile;

	public Profile getProfile() {
		return mProfile;
	}

	public void setProfile(Profile pProfile) {
		this.mProfile = pProfile;
	}
	
	public String getJdaDept() {
		return mJdaDept;
	}
	public void setJdaDept(String pJdaDept) {
		this.mJdaDept = pJdaDept;
	}
	public String getJdaSubDept() {
		return mJdaSubDept;
	}
	public void setJdaSubDept(String pJdaSubDept) {
		this.mJdaSubDept = pJdaSubDept;
	}
	public String getJdaClass() {
		return mJdaClass;
	}
	public void setJdaClass(String pJdaClass) {
		this.mJdaClass = pJdaClass;
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

	
	
	
	private JSONObject validateFields(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws JSONException {

		JSONObject errorJson = new JSONObject();

		
		if ((getJdaDept()==null) || (getJdaDept()!=null && getJdaDept().trim().equals(""))) {
			errorJson.put("id", "39");
			errorJson.put("description", "Null values found for property jda_dept_id");
			return errorJson;
		}
		if ((getJdaSubDept()==null) || (getJdaSubDept()!=null && getJdaSubDept().trim().equals(""))) {
			errorJson.put("id", "40");
			errorJson.put("description", "Null values found for property jda_sub_dept_id");
			return errorJson;
		}
		
		return errorJson;
	}
	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return Category item in Json format.
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean handleCreateJdaRule(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBJdaRuleFormHandler : handleCreateJdaRule() : Start");
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
			errorJson = validateFields(pRequest,pResponse);
			if(errorJson.length()>0){
				if (isLoggingDebug()) {
					logDebug("Error : "+errorJson);
				}
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,errorJson);
				if (isLoggingDebug()) {
					logDebug("BBBJdaRuleFormHandler : handleCreateJdaRule() : End");
				}
				return false;
			}
			
			RepositoryItem item = getAdminManager().createJdaRule(getCategoryId(), getJdaDept(),getJdaSubDept(),getJdaClass(), getFacetRuleName(), getFacetValuePair(), getUserCreated());
					if(item!=null){
						JSONObject jsonItem = getAdminManager().createSingleJdaRuleJsonObject(item, getCategoryId());
						jsonItemArray.add(jsonItem);
					}
				
			
			
			responseObj.put("data", jsonItemArray);
			responseObj.put("status", "ok");

		}

		catch(RepositoryException e){
			try {
				success=false;
				errorJson.put("id", "40");
				errorJson.put("description", "Error in jda rule creation");
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
					logDebug("BBBJdaRuleFormHandler : handleCreateJdaRule() : End");
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
				logDebug("BBBJdaRuleFormHandler : handleCreateJdaRule() : End");
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
					logDebug("BBBJdaRuleFormHandler : handleCreateJdaRule() : End");
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
						logDebug("BBBJdaRuleFormHandler : handleCreateJdaRule() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBJdaRuleFormHandler : handleCreateEphRule() : End");
		}
		return false;

	}

	public final boolean handleUpdateJdaRule(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBJdaRuleFormHandler : handleUpdateJdaRule() : Start");
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
			errorJson = validateFields(pRequest,pResponse);
			if(errorJson.length()>0){
				if (isLoggingDebug()) {
					logDebug("Error : "+errorJson);
				}
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,errorJson);
				if (isLoggingDebug()) {
					logDebug("BBBJdaRuleFormHandler : handleUpdateJdaRule() : End");
				}
				return false;
			}
			RepositoryItem item = getAdminManager().updateJdaRule(getRuleId(),getJdaDept(),getJdaSubDept(),getJdaClass(),getFacetRuleName(), getFacetValuePair(),getLastModifiedUser());
			if(item!=null){
				JSONObject jsonItem = getAdminManager().createSingleJdaRuleJsonObject(item,getCategoryId());
				jsonItemArray.add(jsonItem);
			}
			responseObj.put("data", jsonItemArray);
			responseObj.put("status", "ok");

		}

		catch(RepositoryException e){
			try {
				success=false;
				errorJson.put("id", "42");
				errorJson.put("description", "Error in eph rule updation");
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
					logDebug("BBBJdaRuleFormHandler : handleUpdateJdaRule() : End");
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
				logDebug("BBBJdaRuleFormHandler : handleUpdateJdaRule() : End");
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
					logDebug("BBBJdaRuleFormHandler : handleUpdateJdaRule() : End");
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
						logDebug("BBBJdaRuleFormHandler : handleUpdateJdaRule() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBJdaRuleFormHandler : handleUpdateEphRule() : End");
		}
		return false;


	}
	
	public final boolean handleRemoveJdaRule(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBJdaRuleFormHandler : handleRemoveJdaRule() : Start");
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
			getAdminManager().deleteJdaRule(getCategoryId(),getRuleId());
			responseObj.put("status", "ok");
		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "43");
				errorJson.put("description", "Error in jda rule deletion");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
			} catch (JSONException e1) {	
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBJdaRuleFormHandler : handleRemoveJdaRule() : End");
				}
				return false;
			}
			if(isLoggingError()){
				e.printStackTrace();
			}
		} catch (SQLException e) {
			try {
				success=false;
				errorJson.put("id", "43");
				errorJson.put("description", "Error in jda rule deletion");
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
					logDebug("BBBJdaRuleFormHandler : handleRemoveJdaRule() : End");
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
				logDebug("BBBJdaRuleFormHandler : handleRemoveJdaRule() : End");
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
					logDebug("BBBJdaRuleFormHandler : handleRemoveJdaRule() : End");
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
						logDebug("BBBJdaRuleFormHandler : handleRemoveJdaRule() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBJdaRuleFormHandler : handleRemoveJdaRule() : End");
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
			logDebug("BBBJdaRuleFormHandler : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug(pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBJdaRuleFormHandler : setPageResponse() : End");
		}
	}
}
