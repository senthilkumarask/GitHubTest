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
 * This form handler is used handling operations on list item.
 * @author Logixal
 *
 */
public class BBBListFormHandler extends GenericFormHandler {

	private String mSubTypeCode;
	private String mDisplayName;
	private Integer mSequenceNumber;
	private boolean mDisabled;
	private boolean mAllowDuplicate;
	private String mSites;
	private String mUserCreated;
	private Timestamp mCreationDate;
	private String mLastModifiedUser;
	private Timestamp mLastModifiedDate;
	private MutableRepository mAdminRepository;
	private String mListId;
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

	public String getListId() {
		return mListId;
	}
	public void setListId(String listId) {
		this.mListId = listId;
	}

	public MutableRepository getAdminRepository() {
		return mAdminRepository;
	}
	public void setAdminRepository(MutableRepository adminRepository) {
		this.mAdminRepository = adminRepository;
	}
	public String getSubTypeCode() {
		return mSubTypeCode;
	}
	public void setSubTypeCode(String pSubTypeCode) {
		mSubTypeCode = pSubTypeCode;
	}

	public boolean isDisabled() {
		return mDisabled;
	}
	public void setDisabled(boolean isDisabled) {
		this.mDisabled = isDisabled;
	}
	public boolean isAllowDuplicate() {
		return mAllowDuplicate;
	}
	public void setAllowDuplicate(boolean allowDuplicate) {

		this.mAllowDuplicate = allowDuplicate;
	}
	public String getSites() {
		return mSites;
	}
	public void setSites(String sites) {
		this.mSites = sites;
	}
	public String getUserCreated() {
		String loginName = (String) getProfile().getPropertyValue("login");
		setUserCreated(loginName);
		return mUserCreated;
	}
	public void setUserCreated(String userCreated) {
		this.mUserCreated = (String) getProfile().getPropertyValue("login");
	}
	public String getLastModifiedUser() {
		String loginName = (String) getProfile().getPropertyValue("login");
		setLastModifiedUser(loginName);
		return mLastModifiedUser;
	}
	public void setLastModifiedUser(String lastModifiedUser) {
		this.mLastModifiedUser = (String) getProfile().getPropertyValue("login");
	}
	public String getDisplayName() {
		return mDisplayName;
	}
	public void setDisplayName(String displayName) {
		this.mDisplayName = displayName;
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

		if (getSubTypeCode()==null || getSubTypeCode()=="") {
			errorJson.put("id", "1");
			errorJson.put("description", "Null values found for property subtype_code");
			return errorJson;
		}

		if (getDisplayName()==null || getDisplayName()=="") {
			errorJson.put("id", "2");
			errorJson.put("description", "Null values found for property display_name");
			return errorJson;
		}

		if (getSites()==null || getSites()=="") {
			errorJson.put("id", "3");
			errorJson.put("description", "Null values found for property site_flag");
			return errorJson;
		}


		return errorJson;
	}

	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return List item in Json format.
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean handleCreateList(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : handleCreateList() : Start");
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
				setPageResponse(pResponse,responseObj);
				return false;
			}
			RepositoryItem item = getAdminManager().createList(getSubTypeCode(),getDisplayName(),getSequenceNumber(),isDisabled(),isAllowDuplicate(),getSites(),getUserCreated(),getLastModifiedUser());

			JSONObject jsonItem = getAdminManager().createSingleListJsonObject(item);
			jsonItemArray.add(jsonItem);
			responseObj.put("data", jsonItemArray);
			responseObj.put("status", "ok");
		}
		catch(RepositoryException e){
			try {
				success=false;
				if(e.getSourceException() instanceof SQLIntegrityConstraintViolationException){
					errorJson.put("id", "4");
					errorJson.put("description", "List of selected type already created");
					responseObj.put("message", errorJson);
					responseObj.put("status", "error");
				} else {
					errorJson.put("id", "5");
					errorJson.put("description", "List Creation failed");
					responseObj.put("message", errorJson);
					responseObj.put("status", "error");
				}
				if(isLoggingError()){
					logError("Exception : "+e);
				}
			} catch (JSONException e1) {
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBListFormHandler : handleCreateList() : End");
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
				logDebug("BBBListFormHandler : handleCreateList() : End");
			}
			return false;
		} 
		catch (SQLException e) {
			success=false;
			try {
				errorJson.put("id", "5");
				errorJson.put("description", "List Creation failed");
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
					logDebug("BBBListFormHandler : handleCreateList() : End");
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
					logDebug("BBBListFormHandler : handleCreateList() : End");
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
						logDebug("BBBListFormHandler : handleCreateList() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : handleCreateList() : End");
		}
		return false;

	}

	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return List item in Json format
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean handleEditList(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : handleEditList() : Start");
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
			errorJson = validateFields(pRequest,pResponse);
			if(errorJson.length()>0){
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,errorJson);
				return false;
			}

			getAdminManager().updateList(getListId(),getSubTypeCode(),getDisplayName(),isDisabled(),isAllowDuplicate(),getSites(),getLastModifiedUser());

			RepositoryItem item = getAdminRepository().getItem(getListId(), BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);
			if(item!=null){
				JSONObject jsonItem = getAdminManager().createSingleListJsonObject(item);
				jsonItemArray.add(jsonItem);
				responseObj.put("data", jsonItemArray);
				responseObj.put("status", "ok");
			} else {
				responseObj.put("message", new JSONObject().put("description","List not found"));
				responseObj.put("status", "error");
			}



		} catch (SQLException e) {
			success=false;
			try {
				errorJson.put("id", "7");
				errorJson.put("description", "Error Updating List");
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
					logDebug("BBBListFormHandler : handleCreateList() : End");
				}
				return false;
			}
		} catch (RepositoryException e) {
			success=false;
			try {
				errorJson.put("id", "7");
				errorJson.put("description", "Error Updating List");
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
					logDebug("BBBListFormHandler : handleCreateList() : End");
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
				logDebug("BBBListFormHandler : handleCreateList() : End");
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
					logDebug("BBBListFormHandler : handleCreateList() : End");
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
						logDebug("BBBListFormHandler : handleCreateList() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : handleEditList() : End");
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
	public final boolean handleRemoveList(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : handleRemoveList() : Start");
		}
		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = new TransactionDemarcation ();
		boolean success= true;
		JSONObject responseObj = new JSONObject();
		JSONObject errorJsonResponse = null;
		JSONObject errorJson = new JSONObject();
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
			getAdminManager().removeList(getListId(),getLastModifiedUser());
			responseObj.put("status", "ok");
		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "8");
				errorJson.put("description", "Error in list deletion");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
			} catch (JSONException e1) {	
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBListFormHandler : handleRemoveList() : End");
				}
				return false;
			}
			if(isLoggingError()){
				e.printStackTrace();
			}
		} catch (SQLException e) {
			try {
				success=false;
				errorJson.put("id", "8");
				errorJson.put("description", "Error in list deletion");
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
					logDebug("BBBListFormHandler : handleRemoveList() : End");
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
				logDebug("BBBListFormHandler : handleRemoveList() : End");
			}
			return false;
		} catch (TransactionDemarcationException e) {
			try {
				success=false;
				errorJson.put("id", "6");
				errorJson.put("description", "Transaction error");
				responseObj.put("message", new JSONObject().put("description","Transaction error"));
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
					logDebug("BBBListFormHandler : handleCreateList() : End");
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
						logDebug("BBBListFormHandler : handleCreateList() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : handleRemoveList() : End");
		}
		return false;
	}


	public final boolean handleAddCategoriesToList(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException, SQLException {
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : handleAddCategoriesToList() : Start");
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
			if(getListId()==null && getListId()=="" && getCategoryId()==null && getCategoryId()==""){
				errorJson.put("id", "9");
				errorJson.put("description", "Error adding category to the list");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}


			getAdminManager().categoryToListAssociation(getListId(), getCategoryId(),getUserCreated());

			RepositoryItem item = getAdminRepository().getItem(getListId(), BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);

			if(item!=null){
				jsonItemArray = getAdminManager().createSingleListCategoryJsonObject(item);	
			} 
			responseObj.put("data", jsonItemArray);
			responseObj.put("status", "ok");


		} catch (RepositoryException e) {
			try {
				if(e.getMessage().equals("68")){
					success=false;
					errorJson.put("id", "68");
					errorJson.put("description", "Cannot add duplicate category at same level");
					responseObj.put("message", errorJson);
					responseObj.put("status", "error");
				}else{
				success=false;
				errorJson.put("id", "9");
				errorJson.put("description", "Error adding category to the list");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				}
				if(isLoggingError()){
					logError("Exception : "+e);
				}
			} catch (JSONException e1) {	
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBListFormHandler : handleAddCategoriesToList() : End");
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
				logDebug("BBBListFormHandler : handleAddCategoriesToList() : End");
			}
			return false;
		} catch (TransactionDemarcationException e) {
			try {
				errorJson.put("id", "6");
				errorJson.put("description", "Transaction error");
				responseObj.put("message", new JSONObject().put("description","Transaction error"));
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
					logDebug("BBBListFormHandler : handleCreateList() : End");
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
						logDebug("BBBListFormHandler : handleCreateList() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : handleAddCategoriesToList() : End");
		}
		return false;
	}


	public final boolean handleRemoveCategoriesFromList(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException, SQLException {
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : handleRemoveCategoriesFromList() : Start");
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
			if(getListId()==null && getListId()=="" && getCategoryId()==null && getCategoryId()==""){
				errorJson.put("id", "11");
				errorJson.put("description", "Error removing category from the list");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}

			getAdminManager().removeCategoryFromList(getListId(), getCategoryId(),getLastModifiedUser());

			RepositoryItem item = getAdminRepository().getItem(getListId(), BBBAdminConstant.LIST_ITEM_DESCRIPTOR_NAME);

			if(item!=null){
				jsonItemArray = getAdminManager().createSingleListCategoryJsonObject(item);

			} 

			responseObj.put("data", jsonItemArray);
			responseObj.put("status", "ok");


		} catch (RepositoryException e) {
			try {
				errorJson.put("id", "11");
				errorJson.put("description", "Error removing category from the list");
				success=false;
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
					logDebug("BBBListFormHandler : handleRemoveCategoriesFromList() : End");
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
				logDebug("BBBListFormHandler : handleRemoveCategoriesFromList() : End");
			}
			return false;
		} catch (TransactionDemarcationException e) {
			try {
				errorJson.put("id", "6");
				errorJson.put("description", "Transaction error");
				responseObj.put("message", new JSONObject().put("description","Transaction error"));
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
					logDebug("BBBListFormHandler : handleCreateList() : End");
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
						logDebug("BBBListFormHandler : handleCreateList() : End");
					}
					return false;
				}
			}
		}

		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : handleRemoveCategoriesFromList() : End");
		}
		return false;
	}


	public final boolean handleReOrderCategories(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException, SQLException {
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : handleReOrderCategories() : Start");
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
			if(getListId()==null && getListId()=="" && getCategoryId()==null && getCategoryId()==""){
				errorJson.put("id", "12");
				errorJson.put("description", "Error occured in re-sequencing list categories");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}

			getAdminManager().reOrderListCategories(getListId(), getCategoryId(), getNewSequenceNo(),getLastModifiedUser());
			responseObj.put("status", "ok");

		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "12");
				errorJson.put("description", "Error occured in re-sequencing list categories");
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
					logDebug("BBBListFormHandler : handleReOrderCategories() : End");
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
				logDebug("BBBListFormHandler : handleReOrderCategories() : End");
			}
			return false;
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
					logDebug("BBBListFormHandler : handleCreateList() : End");
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
						logDebug("BBBListFormHandler : handleCreateList() : End");
					}
					return false;
				}
			}
		}
		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : handleReOrderCategories() : End");
		}
		return false;
	}

	public final boolean handleReOrderList(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException, SQLException {
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : handleReOrderList() : Start");
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
			if(getListId()==null && getListId()==""){
				errorJson.put("id", "13");
				errorJson.put("description", "Error occured in re-sequencing list");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}

			getAdminManager().reOrderListSequence(getListId(), (getNewSequenceNo()-1), getLastModifiedUser());
			responseObj.put("status", "ok");

		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "13");
				errorJson.put("description", "Error occured in re-sequencing list");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				if(isLoggingError()){
					logError("Exception : "+e);
				}
			} catch (JSONException e1) {
				success=false;
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBListFormHandler : handleReOrderList() : End");
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
				logDebug("BBBListFormHandler : handleReOrderList() : End");
			}
			return false;
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
					logDebug("BBBListFormHandler : handleCreateList() : End");
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
						logDebug("BBBListFormHandler : handleCreateList() : End");
					}
					return false;
				}
			}
		}
		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : handleReOrderList() : End");
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
			logDebug("BBBListFormHandler : setPageResponse() : Start");
		}
		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug(pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
		if (isLoggingDebug()) {
			logDebug("BBBListFormHandler : setPageResponse() : End");
		}
	}

}
