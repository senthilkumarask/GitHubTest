package com.admin.formhandler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
public class BBBEphRuleFormHandler extends GenericFormHandler{

	
	private String mCategoryId;
	private String mEphId;
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
	public String getEphId() {
		return mEphId;
	}
	public void setEphId(String pEphId) {
		this.mEphId = pEphId;
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

		
		if ((getEphId()==null) || (getEphId()!=null && getEphId().trim().equals(""))) {
			errorJson.put("id", "35");
			errorJson.put("description", "Null values found for property eph_node_id");
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
	public final boolean handleCreateEphRule(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBEphRuleFormHandler : handleCreateEphRule() : Start");
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
					logDebug("BBBEphRuleFormHandler : handleCreateEphRule() : End");
				}
				return false;
			}
			List<String> nodesInHierarchy = new <String>ArrayList();
			getAdminManager().getAllNodesInHierarchy(getEphId(),nodesInHierarchy);
			
			if(nodesInHierarchy.size()>1 && getIncludeAll()!=1){
				responseObj.put("message", new JSONObject().put("description","Current node is not leaf node"));
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				if (isLoggingDebug()) {
					logDebug("BBBEphRuleFormHandler : handleCreateEphRule() : End");
				}
				return false;
			}
			
			
			if(!nodesInHierarchy.isEmpty()){
				for(String node :  nodesInHierarchy){
					System.out.println("Creating EPH rule for Node :  "+node);
					RepositoryItem item = getAdminManager().createEphRule(getCategoryId(), node, getFacetRuleName(), getFacetValuePair(), getUserCreated());
					if(item!=null){
						JSONObject jsonItem = getAdminManager().createSingleEphRuleJsonObject(item, getCategoryId());
						jsonItemArray.add(jsonItem);
					}
				}
			}
			
			
			responseObj.put("data", jsonItemArray);
			responseObj.put("status", "ok");

		}

		catch(RepositoryException e){
			try {
				success=false;
				errorJson.put("id", "36");
				errorJson.put("description", "Error in eph rule creation");
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
					logDebug("BBBEphRuleFormHandler : handleCreateEphRule() : End");
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
				logDebug("BBBEphRuleFormHandler : handleCreateEphRule() : End");
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
					logDebug("BBBEphRuleFormHandler : handleCreateEphRule() : End");
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
						logDebug("BBBEphRuleFormHandler : handleCreateEphRule() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBEphRuleFormHandler : handleCreateEphRule() : End");
		}
		return false;

	}

	public final boolean handleUpdateEphRule(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBEphRuleFormHandler : handleUpdateEphRule() : Start");
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
					logDebug("BBBEphRuleFormHandler : handleUpdateEphRule() : End");
				}
				return false;
			}
			List<String> nodesInHierarchy = new <String>ArrayList();
			RepositoryItem ephRule = getAdminManager().getAdminRepository().getItem(getRuleId(), BBBAdminConstant.EPH_FACET_RULE_ITEM_DESCRIPTOR);
			String ephId = (String)ephRule.getPropertyValue(BBBAdminConstant.EPH_ID_PROPERTY_NAME);
			if(!ephId.equals(getEphId())){
				List<String> existingNodesInCategory = new ArrayList<String>();
				RepositoryItem category = getAdminManager().getAdminRepository().getItem(getCategoryId(), BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);
				List<RepositoryItem> ephRules = (List<RepositoryItem>)category.getPropertyValue(BBBAdminConstant.CATEGORY_EPH_RULES_PROPERTY_NAME);
				if(ephRules!=null && !ephRules.isEmpty()){
					for(RepositoryItem ruleItem : ephRules){
						String ephNodeId =  (String)ruleItem.getPropertyValue(BBBAdminConstant.EPH_ID_PROPERTY_NAME);
						existingNodesInCategory.add(ephNodeId);
					}	
				}


				getAdminManager().getAllNodesInHierarchy(getEphId(),nodesInHierarchy);
				
				if(nodesInHierarchy.size()>1 && getIncludeAll()!=1){
					responseObj.put("message", new JSONObject().put("description","Current node is not leaf node"));
					responseObj.put("status", "error");
					setPageResponse(pResponse,responseObj);
					if (isLoggingDebug()) {
						logDebug("BBBEphRuleFormHandler : handleCreateEphRule() : End");
					}
					return false;
				}
				if(!nodesInHierarchy.isEmpty()){
					if(nodesInHierarchy.size()>1){
						getAdminManager().deleteEph(getCategoryId(),getRuleId());
						for(String node :  nodesInHierarchy){
							if(!existingNodesInCategory.contains(node)){
								RepositoryItem item = getAdminManager().createEphRule(getCategoryId(), node, getFacetRuleName(), getFacetValuePair(), getUserCreated());
								if(item!=null){
									JSONObject jsonItem = getAdminManager().createSingleEphRuleJsonObject(item, getCategoryId());
									jsonItemArray.add(jsonItem);
								}
							}
						}
					} else {
						setFacetValuePair("");
						RepositoryItem item = getAdminManager().updateEphRule(getRuleId(),getEphId(),getFacetRuleName(), getFacetValuePair(),getUserCreated());
						if(item!=null){
							JSONObject jsonItem = getAdminManager().createSingleEphRuleJsonObject(item,getCategoryId());
							jsonItemArray.add(jsonItem);
						}
					}
				} 


			} else{
				
				RepositoryItem item = getAdminManager().updateEphRule(getRuleId(),getEphId(),getFacetRuleName(), getFacetValuePair(),getUserCreated());
				if(item!=null){
					JSONObject jsonItem = getAdminManager().createSingleEphRuleJsonObject(item,getCategoryId());
					jsonItemArray.add(jsonItem);
				}
					
				
				
			}
			
			responseObj.put("data", jsonItemArray);
			responseObj.put("status", "ok");

		}

		catch(RepositoryException e){
			try {
				success=false;
				errorJson.put("id", "37");
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
					logDebug("BBBEphRuleFormHandler : handleUpdateEphRule() : End");
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
				logDebug("BBBEphRuleFormHandler : handleUpdateEphRule() : End");
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
					logDebug("BBBEphRuleFormHandler : handleUpdateEphRule() : End");
				}
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
						logDebug("BBBEphRuleFormHandler : handleUpdateEphRule() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBEphRuleFormHandler : handleUpdateEphRule() : End");
		}
		return false;


	}
	
	public final boolean handleRemoveEph(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBEphRuleFormHandler : handleRemoveEph() : Start");
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
			getAdminManager().deleteEph(getCategoryId(),getRuleId());
			responseObj.put("status", "ok");
		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "38");
				errorJson.put("description", "Error in eph rule deletion");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
			} catch (JSONException e1) {	
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBEphRuleFormHandler : handleRemoveEph() : End");
				}
				return false;
			}
			if(isLoggingError()){
				e.printStackTrace();
			}
		} catch (SQLException e) {
			try {
				success=false;
				errorJson.put("id", "38");
				errorJson.put("description", "Error in eph rule deletion");
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
					logDebug("BBBEphRuleFormHandler : handleRemoveEph() : End");
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
				logDebug("BBBEphRuleFormHandler : handleRemoveEph() : End");
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
					logDebug("BBBEphRuleFormHandler : handleCreateSku() : End");
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
						logDebug("BBBEphRuleFormHandler : handleRemoveEph() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBEphRuleFormHandler : handleRemoveEph() : End");
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
			logDebug("BBBEphRuleFormHandler : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug(pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBEphRuleFormHandler : setPageResponse() : End");
		}
	}
}
