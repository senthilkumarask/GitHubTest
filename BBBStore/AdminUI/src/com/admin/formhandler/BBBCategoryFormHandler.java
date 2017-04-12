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
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.admin.constant.BBBAdminConstant;
import com.admin.manager.BBBAdminManager;
/**
 * This formhandler is used for handling operations on the category item
 * @author Logixal
 *
 */
public class BBBCategoryFormHandler extends GenericFormHandler{

	BBBAdminManager mAdminManager;
	private MutableRepository mAdminRepository;
	private String mCategoryName;
	private int mSuggestedQty;
	private String mParentCategory;
	private String mDisplayName;
	private String mCategoryUrlUS;
	private String mCategoryImageUrlUS;
	private String mCategoryUrlTBS;
	private String mCategoryImageUrlTBS;
	private String mCategoryUrlBABY;
	private String mCategoryImageUrlBABY;
	private String mCategoryUrlCA;
	private String mCategoryImageUrlCA;
	
	private String mMobCategoryUrlUS;
	private String mMobImageUrlUS;
	private String mMobCategoryUrlBaby;
	private String mMobImageUrlBaby;
	private String mMobCategoryUrlCA;
	private String mMobImageUrlCA;
	private String mTbsCategoryUrlBaby;
	private String mTbsImageUrlBaby;
	private String mTbsCategoryUrlCA;
	private String mTbsImageUrlCA;
	
	private int mThresholdQty;
	private double mThresholdAmt;
	private String mServiceType;
	private boolean mDisabled;
	private String mCreatedUser;
	private String mLastModifiedUser;
	private String mCategoryId;
	private boolean mConfigComplete;
	private boolean mVisibleOnChecklist;
	private boolean mVisibleOnRegList;
	private boolean mChildPrdNeededToDisp;
	private String mChildCategoryId;
	private int mNewSequenceNo;
	private String mListId;
	private String mRuleId;
	private String mParentListCatId;
	
	private boolean mUrlOverride;
	private boolean mBabyUrlOverride;
	private boolean mCaUrlOverride;
	private boolean mMobUrlOverride;
	private boolean mMobBabyUrlOverride;
	private boolean mMobCAUrlOverride;
	private boolean mTbsUrlOverride;
	private boolean mTbsBabyUrlOverride;
	private boolean mTbsCAUrlOverride;
	private String facetIdPkgCount;
	
	TransactionManager mTransactionManager;
	Profile mProfile;

	public Profile getProfile() {
		return mProfile;
	}

	public void setProfile(Profile pProfile) {
		this.mProfile = pProfile;
	}
	
	public String getParentListCatId() {
		return mParentListCatId;
	}

	public void setParentListCatId(String pParentListCatId) {
		mParentListCatId = pParentListCatId;
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

	public String getListId() {
		return mListId;
	}

	public void setListId(String pListId) {
		mListId = pListId;
	}

	public int getNewSequenceNo() {
		return mNewSequenceNo;
	}

	public void setNewSequenceNo(int pNewSequenceNo) {
		this.mNewSequenceNo = pNewSequenceNo;
	}

	public MutableRepository getAdminRepository() {
		return mAdminRepository;
	}

	public void setAdminRepository(MutableRepository mAdminRepository) {
		this.mAdminRepository = mAdminRepository;
	}


	public String getChildCategoryId() {
		return mChildCategoryId;
	}

	public void setChildCategoryId(String mChildCategoryId) {
		this.mChildCategoryId = mChildCategoryId;
	}

	public String getCategoryId() {
		return mCategoryId;
	}

	public void setCategoryId(String mCategoryId) {
		this.mCategoryId = mCategoryId;
	}

	public String getLastModifieduser() {
		String loginName = (String) getProfile().getPropertyValue("login");
		setLastModifieduser(loginName);
		return mLastModifiedUser;
	}

	public void setLastModifieduser(String mLastModifieduser) {
		this.mLastModifiedUser = mLastModifieduser;
	}

	public String getCreatedUser() {
		String loginName = (String) getProfile().getPropertyValue("login");
		setCreatedUser(loginName);
		return mCreatedUser;
	}

	public void setCreatedUser(String mCreatedUser) {
		this.mCreatedUser = mCreatedUser;
	}

	public double getThresholdAmt() {
		return mThresholdAmt;
	}

	public void setThresholdAmt(double mThresholdAmt) {
		this.mThresholdAmt = mThresholdAmt;
	}

	public String getServiceType() {
		return mServiceType;
	}

	public void setServiceType(String mServiceType) {
		this.mServiceType = mServiceType;
	}

	public int getThresholdQty() {
		return mThresholdQty;
	}

	public void setThresholdQty(int mThresholdQty) {
		this.mThresholdQty = mThresholdQty;
	}

	public String getCategoryUrlCA() {
		return mCategoryUrlCA;
	}

	public void setCategoryUrlCA(String mCategoryUrlCA) {
		this.mCategoryUrlCA = mCategoryUrlCA;
	}

	public String getCategoryImageUrlCA() {
		return mCategoryImageUrlCA;
	}

	public void setCategoryImageUrlCA(String mCategoryImageUrlCA) {
		this.mCategoryImageUrlCA = mCategoryImageUrlCA;
	}

	public String getParentCategory() {
		return mParentCategory;
	}

	public void setParentCategory(String mParentCategory) {
		this.mParentCategory = mParentCategory;
	}

	public String getDisplayName() {
		return mDisplayName;
	}

	public void setDisplayName(String displayName) {
		this.mDisplayName = displayName;
	}

	public String getCategoryUrlUS() {
		return mCategoryUrlUS;
	}

	public void setCategoryUrlUS(String categoryUrlUS) {
		this.mCategoryUrlUS = categoryUrlUS;
	}

	public String getCategoryImageUrlUS() {
		return mCategoryImageUrlUS;
	}

	public void setCategoryImageUrlUS(String categoryImageUrlUS) {
		this.mCategoryImageUrlUS = categoryImageUrlUS;
	}

	public String getCategoryUrlTBS() {
		return mCategoryUrlTBS;
	}

	public void setCategoryUrlTBS(String categoryUrlTBS) {
		this.mCategoryUrlTBS = categoryUrlTBS;
	}

	public String getCategoryImageUrlTBS() {
		return mCategoryImageUrlTBS;
	}

	public void setCategoryImageUrlTBS(String categoryImageUrlTBS) {
		this.mCategoryImageUrlTBS = categoryImageUrlTBS;
	}

	public String getCategoryUrlBABY() {
		return mCategoryUrlBABY;
	}

	public void setCategoryUrlBABY(String categoryUrlBABY) {
		this.mCategoryUrlBABY = categoryUrlBABY;
	}

	public String getCategoryImageUrlBABY() {
		return mCategoryImageUrlBABY;
	}

	public void setCategoryImageUrlBABY(String categoryImageUrlBABY) {
		this.mCategoryImageUrlBABY = categoryImageUrlBABY;
	}


	public String getMobCategoryUrlUS() {
		return mMobCategoryUrlUS;
	}

	public void setMobCategoryUrlUS(String mobCategoryUrlUS) {
		this.mMobCategoryUrlUS = mobCategoryUrlUS;
	}
	public String getMobImageUrlUS() {
		return mMobImageUrlUS;
	}

	public void setMobImageUrlUS(String mobImageUrlUS) {
		this.mMobImageUrlUS = mobImageUrlUS;
	}

	public String getMobCategoryUrlBaby() {
		return mMobCategoryUrlBaby;
	}

	public void setMobCategoryUrlBaby(String mobCategoryUrlBaby) {
		this.mMobCategoryUrlBaby = mobCategoryUrlBaby;
	}

	public String getMobImageUrlBaby() {
		return mMobImageUrlBaby;
	}

	public void setMobImageUrlBaby(String mobImageUrlBaby) {
		this.mMobImageUrlBaby = mobImageUrlBaby;
	}

	public String getMobCategoryUrlCA() {
		return mMobCategoryUrlCA;
	}

	public void setMobCategoryUrlCA(String mobCategoryUrlCA) {
		this.mMobCategoryUrlCA = mobCategoryUrlCA;
	}

	public String getMobImageUrlCA() {
		return mMobImageUrlCA;
	}

	public void setMobImageUrlCA(String mobImageUrlCA) {
		this.mMobImageUrlCA = mobImageUrlCA;
	}

	public String getTbsCategoryUrlBaby() {
		return mTbsCategoryUrlBaby;
	}

	public void setTbsCategoryUrlBaby(String tbsCategoryUrlBaby) {
		this.mTbsCategoryUrlBaby = tbsCategoryUrlBaby;
	}

	public String getTbsImageUrlBaby() {
		return mTbsImageUrlBaby;
	}

	public void setTbsImageUrlBaby(String tbsImageUrlBaby) {
		this.mTbsImageUrlBaby = tbsImageUrlBaby;
	}

	public String getTbsCategoryUrlCA() {
		return mTbsCategoryUrlCA;
	}

	public void setTbsCategoryUrlCA(String tbsCategoryUrlCA) {
		this.mTbsCategoryUrlCA = tbsCategoryUrlCA;
	}

	public String getTbsImageUrlCA() {
		return mTbsImageUrlCA;
	}

	public void setTbsImageUrlCA(String tbsImageUrlCA) {
		this.mTbsImageUrlCA = tbsImageUrlCA;
	}

	public int getSuggestedQty() {
		return mSuggestedQty;
	}

	public void setSuggestedQty(int suggestedQty) {
		this.mSuggestedQty = suggestedQty;
	}

	public String getCategoryName() {
		return mCategoryName;
	}

	public void setCategoryName(String categoryName) {
		this.mCategoryName = categoryName;
	}

	public boolean isDisabled() {
		return mDisabled;
	}

	public void setDisabled(boolean pDisabled) {
		this.mDisabled = pDisabled;
	}


	public BBBAdminManager getAdminManager() {
		return mAdminManager;
	}

	public void setAdminManager(
			BBBAdminManager pAdminManager) {
		this.mAdminManager = pAdminManager;
	}

	public boolean isConfigComplete() {
		return mConfigComplete;
	}

	public void setConfigComplete(boolean pConfigComplete) {
		mConfigComplete = pConfigComplete;
	}

	public boolean isVisibleOnChecklist() {
		return mVisibleOnChecklist;
	}

	public void setVisibleOnChecklist(boolean pVisibleOnChecklist) {
		this.mVisibleOnChecklist = pVisibleOnChecklist;
	}

	public boolean isVisibleOnRegList() {
		return mVisibleOnRegList;
	}

	public void setVisibleOnRegList(boolean pVisibleOnRegList) {
		mVisibleOnRegList = pVisibleOnRegList;
	}

	public boolean isChildPrdNeededToDisp() {
		return mChildPrdNeededToDisp;
	}

	public void setChildPrdNeededToDisp(boolean pChildPrdNeededToDisp) {
		mChildPrdNeededToDisp = pChildPrdNeededToDisp;
	}

	public boolean getUrlOverride() {
		return mUrlOverride;
	}

	public void setUrlOverride(boolean urlOverride) {
		this.mUrlOverride = urlOverride;
	}

	public boolean getBabyUrlOverride() {
		return mBabyUrlOverride;
	}

	public void setBabyUrlOverride(boolean babyUrlOverride) {
		this.mBabyUrlOverride = babyUrlOverride;
	}

	public boolean getCaUrlOverride() {
		return mCaUrlOverride;
	}

	public void setCaUrlOverride(boolean caUrlOverride) {
		this.mCaUrlOverride = caUrlOverride;
	}

	public boolean getMobUrlOverride() {
		return mMobUrlOverride;
	}

	public void setMobUrlOverride(boolean mobUrlOverride) {
		this.mMobUrlOverride = mobUrlOverride;
	}

	public boolean getMobBabyUrlOverride() {
		return mMobBabyUrlOverride;
	}

	public void setMobBabyUrlOverride(boolean mobBabyUrlOverride) {
		this.mMobBabyUrlOverride = mobBabyUrlOverride;
	}

	public boolean getMobCAUrlOverride() {
		return mMobCAUrlOverride;
	}

	public void setMobCAUrlOverride(boolean mobCAUrlOverride) {
		this.mMobCAUrlOverride = mobCAUrlOverride;
	}

	public boolean getTbsUrlOverride() {
		return mTbsUrlOverride;
	}

	public void setTbsUrlOverride(boolean tbsUrlOverride) {
		this.mTbsUrlOverride = tbsUrlOverride;
	}

	public boolean getTbsBabyUrlOverride() {
		return mTbsBabyUrlOverride;
	}

	public void setTbsBabyUrlOverride(boolean tbsBabyUrlOverride) {
		this.mTbsBabyUrlOverride = tbsBabyUrlOverride;
	}

	public boolean getTbsCAUrlOverride() {
		return mTbsCAUrlOverride;
	}

	public void setTbsCAUrlOverride(boolean tbsCAUrlOverride) {
		this.mTbsCAUrlOverride = tbsCAUrlOverride;
	}
	
	private JSONObject validateFields(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws JSONException {

		JSONObject errorJson = new JSONObject();

		
		if (getDisplayName()!=null && getDisplayName().trim().equals("")) {
			errorJson.put("id", "14");
			errorJson.put("description", "Null values found for property Display name");
			return errorJson;
		}

		if (getCategoryName()!=null && getCategoryName().trim().equals("")) {
			errorJson.put("id", "15");
			errorJson.put("description", "Null values found for property Category name");
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
	public final boolean handleCreateCategory(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBCategoryFormHandler : handleCreateCategory() : Start");
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
			errorJson = getAdminManager().validateUser(getCreatedUser(),getLastModifieduser());
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
					logDebug("BBBCategoryFormHandler : handleCreateCategory() : End");
				}
				return false;
			}
			RepositoryItem item = getAdminManager().createCategory(getParentCategory(), getCategoryName(),getSuggestedQty(), getDisplayName(), getCategoryUrlUS(), getCategoryImageUrlUS(), getCategoryUrlTBS(), getCategoryImageUrlTBS(), getCategoryUrlBABY(), getCategoryImageUrlBABY(), getCategoryUrlCA(), getCategoryImageUrlCA(),getMobCategoryUrlUS(),getMobImageUrlUS(),getMobCategoryUrlBaby(),getMobImageUrlBaby(),getMobCategoryUrlCA(),getMobImageUrlCA(),getTbsCategoryUrlBaby(),getTbsImageUrlBaby(),getTbsCategoryUrlCA(),getTbsImageUrlCA(),isConfigComplete(),isVisibleOnChecklist(),isVisibleOnRegList(),isChildPrdNeededToDisp(),getThresholdQty() ,getThresholdAmt() , getServiceType(),isDisabled(), getCreatedUser(), getLastModifieduser(),getListId(),getParentListCatId(),getUrlOverride(),getBabyUrlOverride(),getCaUrlOverride(),getMobUrlOverride(),getMobBabyUrlOverride(),getMobCAUrlOverride(),getTbsUrlOverride(),getTbsBabyUrlOverride(),getTbsCAUrlOverride(),getFacetIdPkgCount());
			if(item!=null){
				JSONObject jsonItem = getAdminManager().createSingleCategoryJsonObject(item);
				jsonItemArray.add(jsonItem);
			}
			responseObj.put("data", jsonItemArray);
			responseObj.put("status", "ok");

		}

		catch(RepositoryException e){
			try {
				success=false;
				errorJson.put("id", "16");
				errorJson.put("description", "Error in creating category");
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
					logDebug("BBBCategoryFormHandler : handleCreateCategory() : End");
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
				logDebug("BBBCategoryFormHandler : handleCreateCategory() : End");
			}
			return false;
		} catch (SQLException e) {
			try {
				success=false;
				errorJson.put("id", "16");
				errorJson.put("description", "Error in creating category");
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
					logDebug("BBBCategoryFormHandler : handleCreateCategory() : End");
				}
				return false;
			}
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
			logDebug("BBBCategoryFormHandler : handleCreateCategory() : End");
		}
		return false;


	}

	/**
	 * This method is used to remove category item.
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean handleRemoveCategory(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBCategoryFormHandler : handleRemoveCategory() : Start");
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
			errorJson = getAdminManager().validateUser(getCreatedUser(),getLastModifieduser());
			if(errorJson.length()>0){
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}
			getAdminManager().removeCategory(getCategoryId(),getLastModifieduser());
			responseObj.put("status", "ok");
		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "17");
				errorJson.put("description", "Error in category deletion");
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
					logDebug("BBBCategoryFormHandler : handleRemoveCategory() : End");
				}
				return false;
			}
		} catch (SQLException e) {
			try {
				success=false;
				errorJson.put("id", "17");
				errorJson.put("description", "Error in category deletion");
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
					logDebug("BBBCategoryFormHandler : handleRemoveCategory() : End");
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
				logDebug("BBBCategoryFormHandler : handleRemoveCategory() : End");
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
			logDebug("BBBCategoryFormHandler : handleRemoveCategory() : End");
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

		if(pResponseObject!=null && pResponseObject.length()>0){
			pResponse.getWriter().write(pResponseObject.toString());
			if (isLoggingDebug()) {
				logDebug(pResponseObject.toString());
			}
			pResponse.flushBuffer();
		}
	}

	/**
	 * 	
	 * @param pRequest
	 * @param pResponse
	 * @return Category item in Json format.
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean handleEditCategory(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBCategoryFormHandler : handleEditCategory() : Start");
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
			errorJson = getAdminManager().validateUser(getCreatedUser(),getLastModifieduser());
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

			RepositoryItem item = getAdminManager().updateCategory(getCategoryId(), getCategoryName(), getSuggestedQty(), getParentCategory(), getDisplayName(), getCategoryUrlUS(), getCategoryImageUrlUS(), getCategoryUrlTBS(), getCategoryImageUrlTBS(), getCategoryUrlBABY(), getCategoryImageUrlBABY(), getCategoryUrlCA(), getCategoryImageUrlCA(),getMobCategoryUrlUS(),getMobImageUrlUS(),getMobCategoryUrlBaby(),getMobImageUrlBaby(),getMobCategoryUrlCA(),getMobImageUrlCA(),getTbsCategoryUrlBaby(),getTbsImageUrlBaby(),getTbsCategoryUrlCA(),getTbsImageUrlCA(), getThresholdQty(), getThresholdAmt(), getServiceType(), isDisabled(), getLastModifieduser(),isConfigComplete(),isVisibleOnChecklist(),isVisibleOnRegList(),isChildPrdNeededToDisp(),getUrlOverride(),getBabyUrlOverride(),getCaUrlOverride(),getMobUrlOverride(),getMobBabyUrlOverride(),getMobCAUrlOverride(),getTbsUrlOverride(),getTbsBabyUrlOverride(),getTbsCAUrlOverride(),getFacetIdPkgCount());
			if(item!=null){
				JSONObject jsonItem = getAdminManager().createSingleCategoryJsonObject(item);
				jsonItemArray.add(jsonItem);
			}
			responseObj.put("data", jsonItemArray);
			responseObj.put("status", "ok");


		} catch (SQLException e) {
			try {
				success=false;
				errorJson.put("id", "18");
				errorJson.put("description", "Error Updating Category");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				if(isLoggingError()){
					logError("Exception : "+e);
				}
			} catch (JSONException e1) {
				if(isLoggingError()){
					logError("Exception : "+e);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBCategoryFormHandler : handleEditCategory() : End");
				}
				return false;
			}
		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "18");
				errorJson.put("description", "Error Updating Category");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				if(isLoggingError()){
					logError("Exception : "+e);
				}
			} catch (JSONException e1) {
				if(isLoggingError()){
					logError("Exception : "+e);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBCategoryFormHandler : handleEditCategory() : End");
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
				logDebug("BBBCategoryFormHandler : handleEditCategory() : End");
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
			logDebug("BBBCategoryFormHandler : handleEditCategory() : Start");
		}
		return false;
	}

	public final boolean handleAddChildCategories(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException, SQLException {
		if (isLoggingDebug()) {
			logDebug("BBBCategoryFormHandler : handleAddChildCategories() : Start");
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
			errorJson = getAdminManager().validateUser(getCreatedUser(),getLastModifieduser());
			if(errorJson.length()>0){
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}
			if((getCategoryId()!=null && getCategoryId().trim().equals("")) || (getChildCategoryId()!=null && getChildCategoryId().trim().equals(""))){
				errorJson.put("id", "19");
				errorJson.put("description", "Empty values for category and child category");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}

			getAdminManager().childCategoryToCategoryAssociation(getCategoryId(), getChildCategoryId(),getCreatedUser());

			RepositoryItem item = getAdminRepository().getItem(getCategoryId(), BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);

			if(item!=null){
				jsonItemArray = getAdminManager().createSingleChildCategoryJsonObject(item.getRepositoryId());
			} 
			responseObj.put("data", jsonItemArray);
			responseObj.put("status", "ok");


		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "20");
				errorJson.put("description", "Parent cannot be added in child  category");
				if(e.getMessage().equals(BBBAdminConstant.NO_PARENT_IN_CHILD)){
					responseObj.put("message", errorJson);
					responseObj.put("status", "error");
				} else if(e.getMessage().equals("68")){

					errorJson.put("id", "68");
					errorJson.put("description", "Cannot add duplicate category at same level");
					responseObj.put("message", errorJson);
					responseObj.put("status", "error");
				
				}
				else{
					errorJson.put("id", "21");
					errorJson.put("description", "Error adding child category to category");
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
					logDebug("BBBCategoryFormHandler : handleAddChildCategories() : End");
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
				logDebug("BBBCategoryFormHandler : handleAddChildCategories() : End");
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
			logDebug("BBBCategoryFormHandler : handleAddChildCategories() : End");
		}
		return false;
	}


	public final boolean handleRemoveChildCategories(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException, SQLException {
		if (isLoggingDebug()) {
			logDebug("BBBCategoryFormHandler : handleRemoveChildCategories() : Start");
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
			errorJson = getAdminManager().validateUser(getCreatedUser(),getLastModifieduser());
			if(errorJson.length()>0){
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}
			if((getCategoryId()!=null && getCategoryId().trim().equals("")) || (getChildCategoryId()!=null && getChildCategoryId().trim().equals(""))){
				errorJson.put("id", "19");
				errorJson.put("description", "Empty values for category and child category");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}

			getAdminManager().removeChildCategoriesFromCategories(getCategoryId(), getChildCategoryId());

			RepositoryItem item = getAdminRepository().getItem(getCategoryId(), BBBAdminConstant.CATEGORY_ITEM_DESCRIPTOR_NAME);

			if(item!=null){
				jsonItemArray = getAdminManager().createSingleChildCategoryJsonObject(item.getRepositoryId());
			} 

			responseObj.put("data", jsonItemArray);
			responseObj.put("status", "ok");


		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "22");
				errorJson.put("description", "Error removing child category from category");
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
					logDebug("BBBCategoryFormHandler : handleRemoveChildCategories() : End");
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
				logDebug("BBBCategoryFormHandler : handleRemoveChildCategories() : End");
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
			logDebug("BBBCategoryFormHandler : handleRemoveChildCategories() : End");
		}
		return false;
	}
	
	public final boolean handleReOrderChildCategories(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException, SQLException {
		if (isLoggingDebug()) {
			logDebug("BBBCategoryFormHandler : handleReOrderChildCategories() : Start");
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
			errorJson = getAdminManager().validateUser(getCreatedUser(),getLastModifieduser());
			if(errorJson.length()>0){
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}
			if((getCategoryId()!=null && getCategoryId().trim().equals("")) || (getChildCategoryId()!=null && getChildCategoryId().trim().equals(""))){
				errorJson.put("id", "19");
				errorJson.put("description", "Empty values for category and child category");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}
			getAdminManager().reOrderCatChildCategories(getCategoryId(), getChildCategoryId(), getNewSequenceNo(),getLastModifieduser());

			responseObj.put("status", "ok");
			
		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "23");
				errorJson.put("description", "Error occured in re-sequencing child categories");
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
					logDebug("BBBCategoryFormHandler : handleReOrderChildCategories() : End");
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
				logDebug("BBBCategoryFormHandler : handleReOrderChildCategories() : End");
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
			logDebug("BBBCategoryFormHandler : handleReOrderChildCategories() : End");
		}
		return false;
	}
	public final boolean handleReOrderSku(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBCategoryFormHandler : handleReOrderSku() : Start");
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
			errorJson = getAdminManager().validateUser(getCreatedUser(),getLastModifieduser());
			if(errorJson.length()>0){
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}
			if((getCategoryId()!=null && getCategoryId().trim().equals("")) || (getRuleId()!=null && getRuleId().trim().equals(""))){
				errorJson.put("id", "24");
				errorJson.put("description", "Empty values for category and sku id");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}

			getAdminManager().reOrderCatSkus(getCategoryId(), getRuleId(), getNewSequenceNo());
			responseObj.put("status", "ok");

			
		} catch (JSONException e) {	
			if(isLoggingError()){
				logError("Exception : "+e);
			}
			success=false;
			setPageResponse(pResponse, errorJsonResponse);
			if (isLoggingDebug()) {
				logDebug("BBBSkuFormHandler : handleReOrderSku() : End");
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
					logDebug("BBBSkuFormHandler : handleCreateSku() : End");
				}
				return false;
			}
		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "25");
				errorJson.put("description", "Error occured in re-sequencing category skus");
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
						logDebug("BBBSkuFormHandler : handleReOrderSku() : End");
					}
					return false;
				}
			}
		}
		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBSkuFormHandler : handleReOrderSku() : End");
		}
		return false;
	}	
	
	
	public final boolean handleReOrderEph(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBCategoryFormHandler : handleReOrderEph() : Start");
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
			errorJson = getAdminManager().validateUser(getCreatedUser(),getLastModifieduser());
			if(errorJson.length()>0){
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}
			if((getCategoryId()!=null && getCategoryId().trim().equals("")) ||  (getRuleId()!=null && getRuleId().trim().equals(""))){
				errorJson.put("id", "26");
				errorJson.put("description", "Empty values for category and eph rule id");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}

			getAdminManager().reOrderCatEph(getCategoryId(), getRuleId(), getNewSequenceNo());
			responseObj.put("status", "ok");

			
		} catch (JSONException e) {	
			if(isLoggingError()){
				logError("Exception : "+e);
			}
			success=false;
			setPageResponse(pResponse, errorJsonResponse);
			if (isLoggingDebug()) {
				logDebug("BBBCategoryFormHandler : handleReOrderEph() : End");
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
					logDebug("BBBCategoryFormHandler : handleReOrderEph() : End");
				}
				return false;
			}
		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "27");
				errorJson.put("description", "Error occured in re-sequencing category eph rules");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
			} catch (JSONException e1) {	
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBCategoryFormHandler : handleReOrderEph() : End");
				}
				return false;
			}
			if(isLoggingError()){
				e.printStackTrace();
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
						logDebug("BBBCategoryFormHandler : handleReOrderEph() : End");
					}
					return false;
				}
			}
		}
		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBCategoryFormHandler : handleReOrderEph() : End");
		}
		return false;
	}	
	
	public final boolean handleReOrderFacetRules(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (isLoggingDebug()) {
			logDebug("BBBCategoryFormHandler : handleReOrderEph() : Start");
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
			errorJson = getAdminManager().validateUser(getCreatedUser(),getLastModifieduser());
			if(errorJson.length()>0){
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}
			if((getCategoryId()!=null && getCategoryId().trim().equals("")) ||  (getRuleId()!=null && getRuleId().trim().equals(""))){
				errorJson.put("id", "28");
				errorJson.put("description", "Empty values for category and facet rule id");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
				setPageResponse(pResponse,responseObj);
				return false;
			}

			getAdminManager().reOrderCatFacets(getCategoryId(), getRuleId(), getNewSequenceNo());
			responseObj.put("status", "ok");

			
		} catch (JSONException e) {	
			if(isLoggingError()){
				logError("Exception : "+e);
			}
			success=false;
			setPageResponse(pResponse, errorJsonResponse);
			if (isLoggingDebug()) {
				logDebug("BBBCategoryFormHandler : handleReOrderEph() : End");
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
					logDebug("BBBCategoryFormHandler : handleReOrderEph() : End");
				}
				return false;
			}
		} catch (RepositoryException e) {
			try {
				success=false;
				errorJson.put("id", "29");
				errorJson.put("description", "Error occured in re-sequencing category facet rules");
				responseObj.put("message", errorJson);
				responseObj.put("status", "error");
			} catch (JSONException e1) {	
				if(isLoggingError()){
					logError("Exception : "+e1);
				}
				setPageResponse(pResponse, errorJsonResponse);
				if (isLoggingDebug()) {
					logDebug("BBBCategoryFormHandler : handleReOrderEph() : End");
				}
				return false;
			}
			if(isLoggingError()){
				e.printStackTrace();
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
						logDebug("BBBCategoryFormHandler : handleReOrderEph() : End");
					}
					return false;
				}
			}
		}
		setPageResponse(pResponse,responseObj);
		if (isLoggingDebug()) {
			logDebug("BBBCategoryFormHandler : handleReOrderEph() : End");
		}
		return false;
	}

	public String getFacetIdPkgCount() {
		return facetIdPkgCount;
	}

	public void setFacetIdPkgCount(String facetIdPkgCount) {
		this.facetIdPkgCount = facetIdPkgCount;
	}	
}
