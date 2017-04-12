package com.bbb.selfservice.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.selfservice.manager.SearchStoreManager;


import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.AppointmentVO;
import com.bbb.commerce.catalog.vo.ScheduleAppointmentVO;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.utils.BBBUtility;

public class ScheduleAppointmentManager  extends BBBDynamoServlet {

	private Repository mAppointmentRepository;
	private Repository mStoreRepository;
	private String mAppointmentListQuery;
	private String mIdAppointmentMappingQuery;
	private String mAppointmentValueQuery;
	private String mDefaultAppointment;
	private String mRegistryId;
	private String mEventDate;
	private String mPreSelectedServiceRefQuery;
	private String mCoregFN;
	private String mCoregLN;
	private String mErrorModal;
	private BBBCatalogToolsImpl mBbbCatalogTools;
	private SearchStoreManager searchStoreManager;
	
	static final String APPOINTMENT ="appointmentType";
	static final String BEDBATHUS_SITE_CODE ="BedBathUS";
	static final String STORE ="store";
	static final String APPOINTMENT_NAME = "appointmentName";
	static final String APPOINTMENT_CODE =  "appointmentCode";
	static final String PRESELECTEDSERVICE_REF = "preselectedServiceRef";
	public static final String RETRIEVED_NO_DATA_FROM_REPOSITORY="6001";
	private String mDefaultAppointmentType;
	private int mDefaultPreSelectedServiceRefValue;
	
	/**
	 * @return the mDefaultAppointmentType
	 */
	public String getDefaultAppointmentType() {
		return mDefaultAppointmentType;
	}

	/**
	 * @param mDefaultAppointmentType the mDefaultAppointmentType to set
	 */
	public void setDefaultAppointmentType(String mDefaultAppointmentType) {
		this.mDefaultAppointmentType = mDefaultAppointmentType;
	}

	/**
	 * @return the mDefaultPreSelectedServiceRefValue
	 */
	public int getDefaultPreSelectedServiceRefValue() {
		return mDefaultPreSelectedServiceRefValue;
	}

	/**
	 * @param mDefaultPreSelectedServiceRefValue the mDefaultPreSelectedServiceRefValue to set
	 */
	public void setDefaultPreSelectedServiceRefValue(
			int mDefaultPreSelectedServiceRefValue) {
		this.mDefaultPreSelectedServiceRefValue = mDefaultPreSelectedServiceRefValue;
	}

	public SearchStoreManager getSearchStoreManager() {
		return searchStoreManager;
	}

	public void setSearchStoreManager(SearchStoreManager searchStoreManager) {
		this.searchStoreManager = searchStoreManager;
	}

	public List<AppointmentVO> fetchEnabledAppointmentTypes(String pSiteId) throws BBBSystemException{
		RepositoryView appointmentView = null;	
		List<AppointmentVO> apointmentTypeList = new ArrayList<AppointmentVO>();
		Object params[] = new Object[1];
		params[0] = pSiteId;
		RqlStatement sqlStatement;
		RepositoryItem[] repositoryItems = null;
		try {
			appointmentView = getAppointmentRepository().getView(APPOINTMENT);
			sqlStatement = this.rqlStatementQuery(getAppointmentListQuery());
			repositoryItems = sqlStatement.executeQuery(
					appointmentView, params);
		} catch (RepositoryException e) {
			
			logError(LogMessageFormatter.formatMessage(null,"ScheduleAppointmentManager|fetchAppointmentTypes()|RepositoryException","catalog_1042"),e);
			
		}
		if(null != repositoryItems){
			for(RepositoryItem repositoryItem:repositoryItems){
				AppointmentVO appVo = new AppointmentVO();
				appVo.setAppointmentCode((String) repositoryItem.getPropertyValue(APPOINTMENT_CODE));
				appVo.setAppointmentName((String) repositoryItem.getPropertyValue(APPOINTMENT_NAME));
				apointmentTypeList.add(appVo);
			}
		}
		else{			
			logDebug("repositoryItems returned null");			
			logError(LogMessageFormatter.formatMessage(null,"ScheduleAppointmentManager|fetchAppointmentTypes()|No data for site"));
			//throw new BBBSystemException (RETRIEVED_NO_DATA_FROM_REPOSITORY,RETRIEVED_NO_DATA_FROM_REPOSITORY);

		}
		return apointmentTypeList;
	}

	
	public ScheduleAppointmentVO getScheduleAppointment(String storeId, String appointmentType, String registryId, String coregFN, String coregLN, String eventDate,String pageName)throws BBBSystemException, BBBBusinessException{		
		boolean acceptsAppointment = false;		
		Map<String, String> profileMap = null;	
		boolean directSkedgeMe = false;
		boolean errorOnModal =false;
		String skedgeURL=null;		
		acceptsAppointment=canScheduleAppointmentForSiteId(SiteContextManager.getCurrentSiteId());
		
		ScheduleAppointmentVO scheduleappointmentVO=new ScheduleAppointmentVO();
		if(acceptsAppointment){
			profileMap = getProfileData();	
			
			if(!BBBUtility.isMapNullOrEmpty(profileMap)){		
				if(null==storeId || StringUtils.isEmpty(storeId)){
					storeId=profileMap.get(BBBCoreConstants.STORE_ID);
				}
				scheduleappointmentVO.setFirstname(profileMap.get(BBBCoreConstants.FIRST_NAME));
				scheduleappointmentVO.setLastname(profileMap.get(BBBCoreConstants.LAST_NAME));
				scheduleappointmentVO.setEmail(profileMap.get(BBBCoreConstants.EMAIL));
				scheduleappointmentVO.setPhone(profileMap.get(BBBCoreConstants.PHONE_NUM));
			}
			
			if(isStoreAppointmentValid(storeId, appointmentType)){
				directSkedgeMe=isApponitmentTypeValidForStore(storeId, appointmentType);
				if(directSkedgeMe){
					final List<String> siteConfigurations = this.getBbbCatalogTools().getAllValuesForKey("ThirdPartyURLs", "appointmentSkedgeURL");
					if (!BBBUtility.isListEmpty(siteConfigurations)) {
						skedgeURL = siteConfigurations.get(0);			
					}
				}
			}
			if(!directSkedgeMe){
				errorOnModal = true;
			}			
		}
		
		if(appointmentType !=null){			
			int preSelectedServiceRef = fetchPreSelectedServiceRef(appointmentType);
					scheduleappointmentVO.setPreSelectedServiceRef(preSelectedServiceRef);
		}
		
		scheduleappointmentVO.setStoreId(storeId);
		scheduleappointmentVO.setCoregFN(coregFN);
		scheduleappointmentVO.setCoregLN(coregLN);
		scheduleappointmentVO.setRegistryId(registryId);
		scheduleappointmentVO.setEventDate(eventDate);		
		scheduleappointmentVO.setScheduleappointment(acceptsAppointment);
		scheduleappointmentVO.setDirectskedgeMe(directSkedgeMe);
		scheduleappointmentVO.setErrormodal(errorOnModal);
		scheduleappointmentVO.setSkedgeURL(skedgeURL);
		scheduleappointmentVO.setAppointmentType(appointmentType);
		scheduleappointmentVO.setPageName(pageName);
		return scheduleappointmentVO;
	}
	
	private  Map<String, String> getProfileData() throws BBBSystemException {
		Map<String, String> profileMap = new HashMap<String,String>();
		Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.ATG_PROFILE);
		if(!profile.isTransient()){
			String firstname=(String) profile.getPropertyValue(BBBCoreConstants.FIRST_NAME);
			String lastname=(String) profile.getPropertyValue(BBBCoreConstants.LAST_NAME);
			String email=(String) profile.getPropertyValue(BBBCoreConstants.EMAIL);
			String phone=(String) profile.getPropertyValue(BBBCoreConstants.PHONE_NUM);
			String favoriteStoreId=(String) getSearchStoreManager().fetchFavoriteStoreId(SiteContextManager.getCurrentSiteId(), profile);
			profileMap.put(BBBCoreConstants.FIRST_NAME, firstname);
			profileMap.put(BBBCoreConstants.LAST_NAME, lastname);
			profileMap.put(BBBCoreConstants.EMAIL, email);
			profileMap.put(BBBCoreConstants.PHONE_NUM, phone);
			//if(!favoriteStoreId.isEmpty() && favoriteStoreId!=null){
				profileMap.put(BBBCoreConstants.STORE_ID, favoriteStoreId);
			//}
		}
		return profileMap;
	}
	
public boolean isStoreAppointmentValid(String favouriteStoreId,String appointmentType){
		
		return  ((favouriteStoreId!= null) && !StringUtils.isEmpty(favouriteStoreId) && (appointmentType!= null) && !StringUtils.isEmpty(appointmentType)) ;
	}

	/*public String getValueForAppointmentCode(String pAppointmentCode,String pSiteId) throws BBBSystemException{
		String query = getAppointmentValueQuery();
		RepositoryView appointmentView = null;	
		Object params[] = new Object[2];
		RqlStatement statement1;
		String appointmentValue = null;
		RepositoryItem[] appointmentItems = null;
		try{
			appointmentView = getAppointmentRepository().getView(APPOINTMENT);
			statement1 = RqlStatement.parseRqlStatement(getAppointmentValueQuery());
			params[0] = pAppointmentCode;
			params[1] = pSiteId;
			appointmentItems = statement1.executeQuery(
					appointmentView, params);
			if((null != appointmentItems) && (appointmentItems.length == 1)){
				for(RepositoryItem item:appointmentItems){
					appointmentValue = (String) item.getPropertyValue(APPOINTMENT_NAME);
					return appointmentValue;
				}
			}
		}
		catch (RepositoryException e) {
			if(isLoggingError()) {

				logError(LogMessageFormatter.formatMessage(null,"ScheduleAppointmentManager|getValueForAppointmentCode()|RepositoryException","catalog_1042"),e);
			}
			throw new BBBSystemException (RETRIEVED_NO_DATA_FROM_REPOSITORY,RETRIEVED_NO_DATA_FROM_REPOSITORY);
		}	
		return appointmentValue;
	}*/
public boolean isApponitmentTypeValidForStore(String pFavouriteStoreId,String pAppointmentType) throws BBBSystemException {
	String query = getIdAppointmentMappingQuery();
	RepositoryView appointmentView = null;	
	Object params[] = new Object[2];
	RqlStatement statement1;
	try {
		appointmentView = getStoreRepository().getView(STORE);
		statement1 = this.rqlStatementQuery(query);				
			params[0] = pFavouriteStoreId;
			params[1] = pAppointmentType;
			RepositoryItem[] appointmentItem = statement1.executeQuery(
					appointmentView, params);
			if ((null != appointmentItem) && (appointmentItem.length == 1)) {
				return true;
			} 
	} catch (RepositoryException e) {
		logError(LogMessageFormatter.formatMessage(null,"ScheduleAppointmentManager|checkAppointmentAvailability()|RepositoryException","catalog_1042"),e);
		
		throw new BBBSystemException (RETRIEVED_NO_DATA_FROM_REPOSITORY,RETRIEVED_NO_DATA_FROM_REPOSITORY);
	}	
	
	return false;
}

/**
 * @param query
 * @return
 * @throws RepositoryException
 */
protected RqlStatement rqlStatementQuery(String query)
		throws RepositoryException {
	return RqlStatement.parseRqlStatement(query);
}

	/**
	 * @param pStoreDetails
	 * @param pAppointmentType
	 * @return
	 * @throws BBBSystemException
	 */
	public Map<String, Boolean> checkAppointmentAvailability(
			List<StoreDetails> pStoreDetails, String pAppointmentType)
			throws BBBSystemException {
		Map<String, Boolean> idAppointmentMapping = new HashMap<String, Boolean>();
		Map<String, Boolean> idAppointment = new HashMap<String, Boolean>();
		for (StoreDetails storeDetails : pStoreDetails) {
			idAppointmentMapping = checkAppointmentAvailable(
					storeDetails.getStoreId(), pAppointmentType);
			idAppointment.put(storeDetails.getStoreId(),idAppointmentMapping.get(storeDetails.getStoreId()));
		}
		return idAppointment;
	}
	/**
	 *
	 * @param pStoreVo
	 * @param pAppointmentType
	 * @return
	 * @throws BBBSystemException
	 */
	public Map<String, Boolean> checkAppointmentAvailabilityCanada(
			List<StoreVO> pStoreVo, String pAppointmentType)
			throws BBBSystemException {
		Map<String, Boolean> idAppointmentMapping = new HashMap<String, Boolean>();
		Map<String, Boolean> idAppointment = new HashMap<String, Boolean>();
		for (StoreVO storeDetails : pStoreVo) {
			idAppointmentMapping = checkAppointmentAvailable(
					storeDetails.getStoreId(), pAppointmentType);
			idAppointment.put(storeDetails.getStoreId(),idAppointmentMapping.get(storeDetails.getStoreId()));
		}
		return idAppointment;
	}
	
	

	/**
	 * @param pStoreId
	 * @param pAppointmentType
	 * @return
	 * @throws BBBSystemException
	 */
	public Map<String, Boolean> checkAppointmentAvailable(String pStoreId,
			String pAppointmentType) throws BBBSystemException {
		String query = getIdAppointmentMappingQuery();
		RepositoryView appointmentView = null;
		Map<String, Boolean> idAppointmentMapping = new HashMap<String, Boolean>();
		Object params[] = new Object[2];
		RqlStatement statement1;
		try {
			appointmentView = getStoreRepository().getView(STORE);
			statement1 = this.rqlStatementQuery(query);
			params[0] = pStoreId;
			params[1] = pAppointmentType;
			RepositoryItem[] appointmentItem = statement1.executeQuery(
					appointmentView, params);
			if (null != appointmentItem) {
				idAppointmentMapping.put(pStoreId, true);
			} else {
				idAppointmentMapping.put(pStoreId, false);
			}

		} catch (RepositoryException e) {

			logError(
					LogMessageFormatter.formatMessage(
							null,
							"ScheduleAppointmentManager|checkAppointmentAvailability()|RepositoryException",
							"catalog_1042"), e);
			throw new BBBSystemException(RETRIEVED_NO_DATA_FROM_REPOSITORY,
					RETRIEVED_NO_DATA_FROM_REPOSITORY);
		}
		return idAppointmentMapping;
	}
	
	/**
	 * @param storeDetails
	 * @param appointmentMap
	 * @param appointmentEligible
	 */
	public void checkAppointmentEligible(StoreDetails storeDetails,
			Map<String, Boolean> appointmentMap, boolean appointmentEligible) {

		final boolean appointmentAvailable = appointmentMap.get(storeDetails
				.getStoreId());
		storeDetails.setAppointmentAvailable(appointmentAvailable);
		logDebug("SearchStoreService.searchStores : store ID "
				+ storeDetails.getStoreId() + " appointmentAvailable is "
				+ appointmentAvailable);
		if (appointmentAvailable) {
			storeDetails.setAppointmentEligible(appointmentEligible);
			logDebug("SearchStoreService.searchStores : site id is "
					+ SiteContextManager.getCurrentSiteId() + " store id "
					+ storeDetails.getStoreId() + " appointmentEligible flag "
					+ appointmentEligible);
		}

	}
	/**
	 * 
	 * @param storeDetails
	 * @param appointmentMap
	 * @param appointmentEligible
	 */
	public void checkAppointmentEligibleCanada(StoreVO storeDetails,
			Map<String, Boolean> appointmentMap, boolean appointmentEligible) {

		final boolean appointmentAvailable = appointmentMap.get(storeDetails
				.getStoreId());
		storeDetails.setAppointmentAvailable(appointmentAvailable);
		logDebug("SearchStoreService.searchStores : store ID "
				+ storeDetails.getStoreId() + " appointmentAvailable is "
				+ appointmentAvailable);
		if (appointmentAvailable) {
			storeDetails.setAppointmentEligible(appointmentEligible);
			logDebug("SearchStoreService.searchStores : site id is "
					+ SiteContextManager.getCurrentSiteId() + " store id "
					+ storeDetails.getStoreId() + " appointmentEligible flag "
					+ appointmentEligible);
		}

	}
	

public boolean canScheduleAppointmentForSiteId(String siteId) {

	this.logDebug("ScheduleAppointmentManager.canScheduleAppointmentForSiteId() method started");
	String configValue=null;
	boolean acceptsAppointment = false;
	/***
	 * BBB-99 | BBBSystemException | For TBS, skedgeMe config type does not
	 * contain any config value. This method is invoked from TBS Canada
	 * Store locator & Registry Owner page. We should return FALSE as
	 * default and not to throw exception.
	 */
	configValue = this.getBbbCatalogTools().getConfigKeyValue(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE, siteId, BBBCoreConstants.FALSE);
	acceptsAppointment = Boolean.parseBoolean(configValue);
	this.logDebug("ScheduleAppointmentManager.canScheduleAppointmentForSiteId() method end");
	return acceptsAppointment;
}

public int fetchPreSelectedServiceRef(String pAppointmentCode) throws BBBSystemException{
	this.logDebug("ScheduleAppointmentManager.fetchPreSelectedServiceRef() method started : appointment code : " + pAppointmentCode);
	RepositoryView appointmentView = null;	
	String query = getPreSelectedServiceRefQuery();
	Object params[] = new Object[1];
	params[0] = pAppointmentCode;
	RqlStatement sqlStatement;
	RepositoryItem[] repositoryItems = null;
	try {
		appointmentView = getAppointmentRepository().getView(APPOINTMENT);
		sqlStatement = this.rqlStatementQuery(query);
		repositoryItems = sqlStatement.executeQuery(
				appointmentView, params);
		if((null!= repositoryItems &&(repositoryItems.length == 1))){
			for(RepositoryItem item:repositoryItems){	
			this.logDebug("ScheduleAppointmentManager.fetchPreSelectedServiceRef() method end");
			return  (Integer) item.getPropertyValue(PRESELECTEDSERVICE_REF);
			}
		}
	}
	catch (RepositoryException e) {			
		logError(LogMessageFormatter.formatMessage(null,"ScheduleAppointmentManager|fetchPreSelectedServiceRef()|RepositoryException","catalog_1042"),e);
		throw new BBBSystemException (RETRIEVED_NO_DATA_FROM_REPOSITORY,RETRIEVED_NO_DATA_FROM_REPOSITORY);
	}	
	
	if(null != pAppointmentCode && pAppointmentCode.equalsIgnoreCase(getDefaultAppointmentType())){
		return getDefaultPreSelectedServiceRefValue();
	}
	this.logDebug("ScheduleAppointmentManager.fetchPreSelectedServiceRef() method end");
	return fetchPreSelectedServiceRef(getDefaultAppointmentType());
}


public String getAppointmentListQuery() {
	return mAppointmentListQuery;
}

public void setAppointmentListQuery(String pAppointmentListQuery) {
	mAppointmentListQuery = pAppointmentListQuery;
}

public String getIdAppointmentMappingQuery() {
	return mIdAppointmentMappingQuery;
}

public void setIdAppointmentMappingQuery(String pIdAppointmentMappingQuery) {
	mIdAppointmentMappingQuery = pIdAppointmentMappingQuery;
}

public BBBCatalogToolsImpl getBbbCatalogTools() {
	return mBbbCatalogTools;
}

public void setBbbCatalogTools(BBBCatalogToolsImpl pBbbCatalogTools) {
	mBbbCatalogTools = pBbbCatalogTools;
}

public Repository getAppointmentRepository() {
	return mAppointmentRepository;
}

public void setAppointmentRepository(Repository pAppointmentRepository) {
	mAppointmentRepository = pAppointmentRepository;
}
public Repository getStoreRepository() {
	return mStoreRepository;
}
public void setStoreRepository(Repository pStoreRepository) {
	mStoreRepository = pStoreRepository;
}

public String getAppointmentValueQuery() {
	return mAppointmentValueQuery;
}

public void setAppointmentValueQuery(String pAppointmentValueQuery) {
	mAppointmentValueQuery = pAppointmentValueQuery;
}

public String getDefaultAppointment() {
	return mDefaultAppointment;
}

public void setDefaultAppointment(String pDefaultAppointment) {
	mDefaultAppointment = pDefaultAppointment;
}

public String getErrorModal() {
	return mErrorModal;
}

public void setErrorModal(String pErrorModal) {
	mErrorModal = pErrorModal;
}



public String getEventDate() {
	return mEventDate;
}

public void setEventDate(String pEventDate) {
	mEventDate = pEventDate;
}

public String getCoregFN() {
	return mCoregFN;
}

public void setCoregFN(String pCoregFN) {
	mCoregFN = pCoregFN;
}

public String getCoregLN() {
	return mCoregLN;
}

public void setCoregLN(String pCoregLN) {
	mCoregLN = pCoregLN;
}

public String getRegistryId() {
	return mRegistryId;
}

public void setRegistryId(String pRegistryId) {
	mRegistryId = pRegistryId;
}

public String getPreSelectedServiceRefQuery() {
	return mPreSelectedServiceRefQuery;
}

public void setPreSelectedServiceRefQuery(String pPreSelectedServiceRefQuery) {
	mPreSelectedServiceRefQuery = pPreSelectedServiceRefQuery;
}
}
