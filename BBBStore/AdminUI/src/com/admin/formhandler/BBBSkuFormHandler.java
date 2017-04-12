package com.admin.formhandler;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.droplet.GenericFormHandler;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.json.JSONArray;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.admin.constant.BBBAdminConstant;
import com.admin.manager.BBBAdminManager;

/**
 * This form handler is used handling operations on Sku item.
 * @author Logixal
 *
 */
public class BBBSkuFormHandler extends GenericFormHandler {

	private String mSkuId;
	private Integer mSequenceNumber;
	private String mRuleEvaluation;
	private String mUserCreated;
	private Timestamp mCreationDate;
	private String mLastModifiedUser;
	private Timestamp mLastModifiedDate;
	private MutableRepository mAdminRepository;
	private String mRuleId;
	private BBBAdminManager mAdminManager;
	private String mCategoryId;
	private int mNewSequenceNo;
	TransactionManager mTransactionManager;
	Profile mProfile;

	public Profile getProfile() {
		return mProfile;
	}

	public void setProfile(Profile pProfile) {
		this.mProfile = pProfile;
	}
	
	public String getRuleEvaluation() {
		return mRuleEvaluation;
	}

	public void setRuleEvaluation(String mRuleEvaluation) {
		this.mRuleEvaluation = mRuleEvaluation;
	}
	public String getSkuId() {
		return mSkuId;
	}

	public void setSkuId(String mSkuId) {
		this.mSkuId = mSkuId;
	}

	public String getRuleId() {
		return mRuleId;
	}

	public void setRuleId(String mRuleId) {
		this.mRuleId = mRuleId;
	}
	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	public void setTransactionManager(TransactionManager pTransactionManager) {
		this.mTransactionManager = pTransactionManager;
	}

	public int getNewSequenceNo() {
		return mNewSequenceNo;
	}

	public void setNewSequenceNo(int pNewSequenceNo) {
		this.mNewSequenceNo = pNewSequenceNo;
	}


	public String getCategoryId() {
		return mCategoryId;
	}
	public void setCategoryId(String mCategoryId) {
		this.mCategoryId = mCategoryId;
	}
	public BBBAdminManager getAdminManager() {
		return mAdminManager;
	}
	public void setAdminManager(BBBAdminManager pAdminManager) {
		mAdminManager = pAdminManager;
	}


	public MutableRepository getAdminRepository() {
		return mAdminRepository;
	}
	public void setAdminRepository(MutableRepository adminRepository) {
		this.mAdminRepository = adminRepository;
	}
	
	public String getUserCreated() {
		String loginName = (String) getProfile().getPropertyValue("login");
		setUserCreated(loginName);
		return mUserCreated;
	}
	public void setUserCreated(String userCreated) {
		this.mUserCreated = userCreated;
	}
	public String getLastModifiedUser() {
		String loginName = (String) getProfile().getPropertyValue("login");
		setLastModifiedUser(loginName);
		return mLastModifiedUser;
	}
	public void setLastModifiedUser(String lastModifiedUser) {
		this.mLastModifiedUser = lastModifiedUser;
	}
	public Timestamp getCreationDate() {
		return mCreationDate;
	}
	public void setCreationDate(Timestamp creationDate) {
		this.mCreationDate = creationDate;
	}
	public Timestamp getLastModifiedDate() {
		return mLastModifiedDate;
	}
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.mLastModifiedDate = lastModifiedDate;
	}
	public Integer getSequenceNumber() {
		return mSequenceNumber;
	}
	public void setSequenceNumber(Integer sequenceNumber) {
		this.mSequenceNumber = sequenceNumber;
	}

	private JSONObject validateFields(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws JSONException {

		JSONObject errorJson = new JSONObject();

		if (getRuleEvaluation()==null || getRuleEvaluation()=="") {
			errorJson.put("id", "30");
			errorJson.put("description", "Null values found for property rule_evaluation");
			return errorJson;
		}

		if (getSkuId()==null || getSkuId()=="") {
			errorJson.put("id", "31");
			errorJson.put("description", "Null values found for property sku_id");
			return errorJson;
		}

		return errorJson;
	}

	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return  item in Json format.
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean handleCreateSku(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBSkuFormHandler : handleCreateSku() : Start");
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
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,errorJson);
				return false;
			}
			RepositoryItem item = getAdminManager().createSku(getSkuId(), getCategoryId(), getRuleEvaluation(),getUserCreated(), getLastModifiedUser());

			JSONObject jsonItem = getAdminManager().createSingleSkuJsonObject(item);
			jsonItemArray.add(jsonItem);
			responseObj.put("data", jsonItemArray);
			responseObj.put("status", "ok");
		}
		catch(RepositoryException e){
			try {
				success=false;
				errorJson.put("id", "32");
				errorJson.put("description", "SKU Creation failed");
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
					logDebug("BBBSkuFormHandler : handleCreateSku() : End");
				}
				return false;
			}

		}
		catch(JSONException e){	
			success=false;
			if(isLoggingError()){
				logError("Exception : "+e);
			}
			setPageResponse(pResponse, errorJsonResponse);
			if (isLoggingDebug()) {
				logDebug("BBBSkuFormHandler : handleCreateSku() : End");
			}
			return false;
		} 
		catch (SQLException e) {
			success=false;
			try {
				errorJson.put("id", "32");
				errorJson.put("description", "SKU Creation failed");
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
					logDebug("BBBSkuFormHandler : handleCreateSku() : End");
				}
				return false;
			}

		} catch (TransactionDemarcationException e) {
			success=false;
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
					logDebug("BBBSkuFormHandler : handleCreateSku() : End");
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
						logDebug("BBBSkuFormHandler : handleCreateSku() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBSkuFormHandler : handleCreateSku() : End");
		}
		return false;

	}

	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return  item in Json format
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean handleEditSku(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBSkuFormHandler : handleEditSku() : Start");
		}
		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = new TransactionDemarcation ();
		boolean success= true;	
		JSONObject responseObj = new JSONObject();
		JSONObject errorJson = new JSONObject();
		JSONArray jsonItemArray = new JSONArray();

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
			if (getRuleEvaluation()==null || getRuleEvaluation()=="") {
				errorJson.put("id", "30");
				errorJson.put("description", "Null values found for property rule_evaluation");
			}
			if(errorJson.length()>0){
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}

			getAdminManager().updateSku(getRuleId(),getRuleEvaluation(),getLastModifiedUser());

			RepositoryItem item = getAdminRepository().getItem(getRuleId(), BBBAdminConstant.SKU_RULE_ITEM_DESCRIPTOR_NAME);
			if(item!=null){
				JSONObject jsonItem = getAdminManager().createSingleSkuJsonObject(item);
				jsonItemArray.add(jsonItem);
				
			}
				responseObj.put("data", jsonItemArray);
				responseObj.put("status", "ok");
			



		} catch (SQLException e) {
			success=false;
			try {
				errorJson.put("id", "33");
				errorJson.put("description", "Error Updating Sku");
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
					logDebug("BBBSkuFormHandler : handleCreateSku() : End");
				}
				return false;
			}
		} catch (RepositoryException e) {
			success=false;
			try {
				errorJson.put("id", "33");
				errorJson.put("description", "Error Updating Sku");
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
					logDebug("BBBSkuFormHandler : handleCreateSku() : End");
				}
				return false;
			}
		} catch (JSONException e) {
			success=false;
			if(isLoggingError()){
				logError("Exception : "+e);
			}
			setPageResponse(pResponse, errorJsonResponse);
			if (isLoggingDebug()) {
				logDebug("BBBSkuFormHandler : handleCreateSku() : End");
			}
			return false;
		} catch (TransactionDemarcationException e) {
			success=false;
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
					logDebug("BBBSkuFormHandler : handleCreateSku() : End");
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
						logDebug("BBBSkuFormHandler : handleCreateSku() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBSkuFormHandler : handleEditSku() : End");
		}
		return false;
	}
	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean handleRemoveSku(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBSkuFormHandler : handleRemoveSku() : Start");
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
			getAdminManager().deleteSku(getCategoryId(),getRuleId());
			responseObj.put("status", "ok");
		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "34");
				errorJson.put("description", "Error in deletion of sku");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
			} catch (JSONException e1) {	
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBSkuFormHandler : handleRemoveSku() : End");
				}
				return false;
			}
			if(isLoggingError()){
				e.printStackTrace();
			}
		} catch (SQLException e) {
			try {
				success=false;
				errorJson.put("id", "34");
				errorJson.put("description", "Error in deletion of sku");
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
					logDebug("BBBSkuFormHandler : handleRemoveSku() : End");
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
				logDebug("BBBSkuFormHandler : handleRemoveSku() : End");
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
					logDebug("BBBSkuFormHandler : handleCreateSku() : End");
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
						logDebug("BBBSkuFormHandler : handleCreateSku() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBSkuFormHandler : handleRemove() : End");
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
			logDebug("BBBSkuFormHandler : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug(pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBSkuFormHandler : setPageResponse() : End");
		}
	}

}
