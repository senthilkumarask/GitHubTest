package com.bbb.integration.csr;

import static com.bbb.constants.BBBCoreConstants.COHERENCE_CACHE_CONFIG_KEY;
import static com.bbb.constants.BBBCoreConstants.MOBILE_COHERENCE_CACHE_CONFIG_KEY;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.StoreBopusInfoVO;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.vo.LinkRegistryToProfileVO;
import com.bbb.commerce.giftregistry.vo.RegistryReqVO;
import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBCacheInvalidatorSource;
import com.bbb.framework.cache.CoherenceCacheContainer;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.repositorywrapper.ICreateRepositoryItemCallback;
import com.bbb.repositorywrapper.RepositoryItemWrapper;
import com.bbb.repositorywrapper.RepositoryWrapperImpl;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

public class CSRFormHandler extends BBBGenericFormHandler {

	private String[] selectedStoreIdsToEnable;
	private String csrName;
	private String password;
	private String registryId;
	private String emailAddress;
	private String regCoreg;
	private String siteName;
	private String tempPassword;
	private String cacheType;
	private String mobileCacheType;
	private String adminName;
	private String adminPassword;
	private Map<String, String> coherenceCaches = new HashMap<String, String>();
	private Map<String, String> mobileCoherenceCaches = new HashMap<String, String>();
	private BBBCacheInvalidatorSource bbbCacheInvalidatorMessageSource;
	private List<String> listOfCacheKeys = new ArrayList<String>();
	private String[] selectedCacheKeysToDelete;
	private List<String> listOfMobileCacheKeys = new ArrayList<String>();
	private String[] selectedMobileCacheKeysToDelete;
	private int maxCacheKeysLimit;
	private String currentSite;
	private Map<String, String> listOfSites = new HashMap<String, String>();
	private String changeRankSuccessURL;
	private String changeRankFailureURL;
	private String successURLForKeyValue;
	private String failureURLForKeyValue;
	private SiteContextManager siteContextManager;
    private String cacheKeyForValue;
    private String mobileCacheKey;
    private String jsonResult;
    private String jsonResultMobile;
	public int getMaxCacheKeysLimit() {
		return maxCacheKeysLimit;
	}

	public void setMaxCacheKeysLimit(int maxCacheKeysLimit) {
		this.maxCacheKeysLimit = maxCacheKeysLimit;
	}

	public String[] getSelectedCacheKeysToDelete() {
		return selectedCacheKeysToDelete;
	}

	public void setSelectedCacheKeysToDelete(String[] selectedCacheKeysToDelete) {
		this.selectedCacheKeysToDelete = selectedCacheKeysToDelete;
	}

	public List<String> getListOfCacheKeys() {
		return listOfCacheKeys;
	}

	public void setListOfCacheKeys(final List<String> listOfCacheKeys) {
		this.listOfCacheKeys = listOfCacheKeys;
	}

	public String getTempPassword() {
		return this.tempPassword;
	}

	public void setTempPassword(String tempPassword) {
		this.tempPassword = tempPassword;
	}
	
	/**
	 * @return the listOfMobileCacheKeys
	 */
	public List<String> getListOfMobileCacheKeys() {
		return listOfMobileCacheKeys;
	}

	/**
	 * @param listOfMobileCacheKeys the listOfMobileCacheKeys to set
	 */
	public void setListOfMobileCacheKeys(List<String> listOfMobileCacheKeys) {
		this.listOfMobileCacheKeys = listOfMobileCacheKeys;
	}

	/**
	 * @return the selectedMobileCacheKeysToDelete
	 */
	public String[] getSelectedMobileCacheKeysToDelete() {
		return selectedMobileCacheKeysToDelete;
	}

	/**
	 * @param selectedMobileCacheKeysToDelete the selectedMobileCacheKeysToDelete to set
	 */
	public void setSelectedMobileCacheKeysToDelete(
			String[] selectedMobileCacheKeysToDelete) {
		this.selectedMobileCacheKeysToDelete = selectedMobileCacheKeysToDelete;
	}

	/**
	 * @return the mobileCacheType
	 */
	public String getMobileCacheType() {
		return mobileCacheType;
	}

	/**
	 * @param mobileCacheType the mobileCacheType to set
	 */
	public void setMobileCacheType(String mobileCacheType) {
		this.mobileCacheType = mobileCacheType;
	}


	private String storeId;
	private String bopusFlag;

	private BBBCatalogTools mCatalogTools;
	private BBBProfileTools mProfileTools;
	private GiftRegistryTools mGiftRegistryTools;
	private CoherenceCacheContainer coherenceCacheContainer;

	public CoherenceCacheContainer getCoherenceCacheContainer() {
		return coherenceCacheContainer;
	}

	public void setCoherenceCacheContainer(final CoherenceCacheContainer coherenceCacheContainer) {
		this.coherenceCacheContainer = coherenceCacheContainer;
	}

	private MutableRepository mGiftRepository;
	private MutableRepository mStoreRepository;

	private String mLinkRegistryServiceName;
	private String mRegistryInfoServiceName;
	private GiftRegistryManager mGiftRegistryManager;

	private RegistryInfoBean mRegistryInfoBean;
	private StoreInfoBean mStoreInfoBean;
	private List<StoreInfoBean> mStoreInfoBeanList = new ArrayList<StoreInfoBean>();
	private List<String> bopusDisabledStates;
	private String showDisabledStores;
	private List<Boolean> storesToEnableList;
	private ProfileInfoBean mProfileInfoBean;

	private SiteContext mSiteContext;

	private String mRegistrySuccessURL;
	private String mRegistryErrorURL;
	private String mStoreSuccessURL;
	private String mStoreErrorURL;
	private String mProfileSuccessURL;
	private String mProfileErrorURL;
	private String mOrderDetailsSuccessURL;
	private String mOrderDetailsErrorURL;
	private String mClearCacheSuccessURL;
	private String mClearCacheErrorURL;
	private String mClearMobileCacheFailureURL;

	private static final String REGISTRY_OWNER = "registryOwner";
	private static final String REGISTRATION_DATE = "registrationDate";
	private static final String REGISTRY_ID = "registryId";
	private static final String EMAIL = "email";
	private static final String MIGRATED_ACCOUNT = "migratedAccount";
	private static final String REGISTRY_CO_OWNER = "registryCoOwner";
	private static final String CO_REGISTRANT = "coregistrant";
	private static final String EVENT_DATE = "eventDate";
	private static final String EVENT_TYPE = "eventType";
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	private static final String REGISTRY_STATUS = "registryStatus";
	private static final String REGISTRY_STATUS_INACTIVE = "INACTIVE";
	private static final String USER_SITE_ITEMS = "userSiteItems";

	private static final String ERR_MSG_REQUIRED_NAME = "Please provide admin name";
	private static final String ERR_MSG_NOT_VALID_NAME = "Please provide valid admin name";
	private static final String ERR_MSG_REQUIRED_PASSWORD = "Please provide password";
	private static final String ERR_MSG_NOT_VALID_PASSWORD = "Please provide valid password";
	private static final String ERR_MSG_REQUIRED_STORE_ID = "Please provide store id";
	private static final String ERR_MSG_NOT_VALID_STORE_ID = "Please provide valid Store Id";
	private static final String ERR_MSG_REQUIRED_REGISTRY_ID = "Please provide registry id";
	private static final String ERR_MSG_NOT_VALID_REGISTRY_ID = "Please provide valid registry id";
	private static final String ERR_MSG_REQUIRED_EMAIL_ADDRESS = "Please provide email address";
	private static final String ERR_MSG_NOT_VALID_EMAIL_ADDRESS = "Please provide valid email Address";
	private static final String ERR_MSG_NO_BOPUS_DISABLED_STATE = "No bopus disabled state found!!";

	private static final String ERR_MSG_REQUIRED_BOPUS_FLAG = "Please select radio button";
	private static final String ERR_MSG_NO_STORE_FOUND = "No Store Found";
	private static final String ERR_MSG_NO_STATE_FOUND = "No State Found";
	private static final String ERR_MSG_FOLLOWING_STORE_NOT_FOUND = "following Stores are not found:";
	private static final String ERR_MSG_INVALID_STORE_IDS = "Please input valid store ids";
	private static final String ERR_MSG_FOLLOWING_STORE_UPDATE_FAIL = "Updates bopus failed for following stores:";
	private static final String ERR_IN_UPDATE_BOPUS_FLAG = "Error while update Store Bopus Status";
	private static final String ERR_IN_UPDATE_BOPUS_FLAG_FOR_STORE = "Error while update Store Bopus Status for store ";
	private static final String ERR_MSG_NO_PROFILE_FOUND = "No Profile Found with given User Id";
	private static final String ERR_MSG_WHILE_UPDATING_TEMP_PASSWORD = "Error while updating password for user";
	private static final String ERR_MSG_INVALID_PASSWORD = "Error while updating password for user. Invalid Password";
	private static final String ERR_MSG_SESSION_EXPIRED = "Your Session Expired, Please login";
	

	private String storesList;
	private String category;
	private String rank;
	private MutableRepository catRelRankRepository;

	public String getSiteName() {
		return this.siteName;
	}

	public void setSiteName(final String siteName) {
		this.siteName = siteName;
	}

	public String getPassword() {
		if(BBBUtility.isNotEmpty(this.password)){
			return this.password.trim();
		}
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getRegistryId() {
		if(BBBUtility.isNotEmpty(this.registryId)){
			return this.registryId.trim();
		}
		return this.registryId;
	}

	public void setRegistryId(final String registryId) {
		this.registryId = registryId;
	}

	public String getCacheType() {
		if(BBBUtility.isNotEmpty(this.cacheType)){
			return this.cacheType.trim();
		}
		return this.cacheType;
	}

	public void setCacheType(final String cacheType) {
		this.cacheType = cacheType;
	}

	public String getEmailAddress() {
		if(BBBUtility.isNotEmpty(this.emailAddress)){
			return this.emailAddress.toLowerCase().trim();
		}
		return this.emailAddress;
	}

	public void setEmailAddress(final String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getStoreId() {
		if(BBBUtility.isNotEmpty(this.storeId)){
			return this.storeId.toLowerCase().trim();
		}
		return this.storeId;
	}

	public void setStoreId(final String storeId) {
		this.storeId = storeId;
	}

	public String getBopusFlag() {
		return this.bopusFlag;
	}

	public void setBopusFlag(final String bopusFlag) {
		this.bopusFlag = bopusFlag;
	}

	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	public BBBProfileTools getProfileTools() {
		return this.mProfileTools;
	}

	public void setProfileTools(final BBBProfileTools pProfileTools) {
		this.mProfileTools = pProfileTools;
	}

	public GiftRegistryTools getGiftRegistryTools() {
		return this.mGiftRegistryTools;
	}

	public void setGiftRegistryTools(final GiftRegistryTools pGiftRegistryTools) {
		this.mGiftRegistryTools = pGiftRegistryTools;
	}

	public MutableRepository getGiftRepository() {
		return this.mGiftRepository;
	}

	public void setGiftRepository(final MutableRepository pGiftRepository) {
		this.mGiftRepository = pGiftRepository;
	}

	public MutableRepository getStoreRepository() {
		return this.mStoreRepository;
	}

	public void setStoreRepository(final MutableRepository pStoreRepository) {
		this.mStoreRepository = pStoreRepository;
	}

	public String getRegistryInfoServiceName() {
		return this.mRegistryInfoServiceName;
	}

	public void setRegistryInfoServiceName(final String pRegistryInfoServiceName) {
		this.mRegistryInfoServiceName = pRegistryInfoServiceName;
	}

	public GiftRegistryManager getGiftRegistryManager() {
		return this.mGiftRegistryManager;
	}

	public void setGiftRegistryManager(final GiftRegistryManager pGiftRegistryManager) {
		this.mGiftRegistryManager = pGiftRegistryManager;
	}

	public RegistryInfoBean getRegistryInfoBean() {
		return this.mRegistryInfoBean;
	}

	public void setRegistryInfoBean(final RegistryInfoBean pRegistryInfoBean) {
		this.mRegistryInfoBean = pRegistryInfoBean;
	}

	public StoreInfoBean getStoreInfoBean() {
		return this.mStoreInfoBean;
	}

	public void setProfileInfoBean(final ProfileInfoBean pProfileInfoBean) {
		this.mProfileInfoBean = pProfileInfoBean;
	}

	public ProfileInfoBean getProfileInfoBean() {
		return this.mProfileInfoBean;
	}

	public void setStoreInfoBean(final StoreInfoBean pStoreInfoBean) {
		this.mStoreInfoBean = pStoreInfoBean;
	}

	public SiteContext getSiteContext() {
		return this.mSiteContext;
	}

	public void setSiteContext(final SiteContext pSiteContext) {
		this.mSiteContext = pSiteContext;
	}

	public String getRegistrySuccessURL() {
		return this.mRegistrySuccessURL;
	}

	public void setRegistrySuccessURL(final String pRegistrySuccessURL) {
		this.mRegistrySuccessURL = pRegistrySuccessURL;
	}
	
	public String getClearMobileCacheFailureURL() {
		return mClearMobileCacheFailureURL;
	}

	public void setClearMobileCacheFailureURL(String mClearMobileCacheFailureURL) {
		this.mClearMobileCacheFailureURL = mClearMobileCacheFailureURL;
	}

	public String getClearCacheSuccessURL() {
		return this.mClearCacheSuccessURL;
	}

	public void setClearCacheSuccessURL(final String pClearCacheSuccessURL) {
		this.mClearCacheSuccessURL = pClearCacheSuccessURL;
	}

	public String getRegistryErrorURL() {
		return this.mRegistryErrorURL;
	}

	public void setRegistryErrorURL(final String pErrorURL) {
		this.mRegistryErrorURL = pErrorURL;
	}

	public String getClearCacheErrorURL() {
		return this.mClearCacheErrorURL;
	}

	public void setClearCacheErrorURL(final String pClearCacheErrorURL) {
		this.mClearCacheErrorURL = pClearCacheErrorURL;
	}

	public String getStoreSuccessURL() {
		return this.mStoreSuccessURL;
	}

	public void setStoreSuccessURL(final String pStoreSuccessURL) {
		this.mStoreSuccessURL = pStoreSuccessURL;
	}

	public String getStoreErrorURL() {
		return this.mStoreErrorURL;
	}

	public void setStoreErrorURL(final String pStoreErrorURL) {
		this.mStoreErrorURL = pStoreErrorURL;
	}

	public String getProfileSuccessURL() {
		return this.mProfileSuccessURL;
	}

	public void setProfileSuccessURL(final String pProfileSuccessURL) {
		this.mProfileSuccessURL = pProfileSuccessURL;
	}

	public String getProfileErrorURL() {
		return this.mProfileErrorURL;
	}

	public void setProfileErrorURL(final String pProfileErrorURL) {
		this.mProfileErrorURL = pProfileErrorURL;
	}
	
	public String getOrderDetailsSuccessURL() {
		return this.mOrderDetailsSuccessURL;
	}

	public void setOrderDetailsSuccessURL(final String pOrderDetailsSuccessURL) {
		this.mOrderDetailsSuccessURL = pOrderDetailsSuccessURL;
	}

	public String getOrderDetailsErrorURL() {
		return this.mOrderDetailsErrorURL;
	}

	public void setOrderDetailsErrorURL(final String pOrderDetailsErrorURL) {
		this.mOrderDetailsErrorURL = pOrderDetailsErrorURL;
	}

	public void preUpdateRegistrant(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) {
		this.logDebug("preUpdateRegistrant method start");
		String errorMessage = "";

		if (BBBUtility.isEmpty(this.getCsrName())) {
			errorMessage = "Please Provide CSR Name";
			this.addFormException(new DropletException(errorMessage));
		}
		if (BBBUtility.isEmpty(this.getSiteName())) {
			errorMessage = "Please Select Site Name";
			this.addFormException(new DropletException(errorMessage));
		}
		this.validatePassword();
		if (!this.getFormError()) {
			if (BBBUtility.isEmpty(this.getEmailAddress())) {
				this.addFormException(new DropletException(
						ERR_MSG_REQUIRED_EMAIL_ADDRESS));
			} else {
				this.setEmailAddress(this.getEmailAddress());
			}
			if (!BBBUtility.isValidEmail(this.getEmailAddress())) {
				this.addFormException(new DropletException(
						ERR_MSG_NOT_VALID_EMAIL_ADDRESS));
			}
			if (BBBUtility.isEmpty(this.getRegistryId())) {
				this.addFormException(new DropletException(
						ERR_MSG_REQUIRED_REGISTRY_ID));
			} else {
				this.setRegistryId(this.getRegistryId());
			}
			if (!BBBUtility.isNumericOnly(this.getRegistryId())) {
				this.addFormException(new DropletException(
						ERR_MSG_NOT_VALID_REGISTRY_ID));
			}
		}
		this.logDebug("preUpdateRegistrant method end");
	}

	public void validatePassword() {
		this.logDebug("validatePassword method start");
		String csrPassword = null;
		List<String> listKeys = null;
		try {
			listKeys = this.getCatalogTools().getAllValuesForKey(
					"ContentCatalogKeys", "csr_password");
			if ((listKeys != null) && (listKeys.size() > 0)) {
				csrPassword = listKeys.get(0);
			}
			this.logDebug("CSR Password:" + csrPassword);
		} catch (final BBBSystemException e) {
			this.logError("BBBSystemException - csr_password key not found for site"
					+ e);
		} catch (final BBBBusinessException e) {
			this.logError("BBBSystemException - csr_password key not found for site"
					+ e);
		}

		if (BBBUtility.isEmpty(this.getPassword())) {
			this.addFormException(new DropletException(ERR_MSG_REQUIRED_PASSWORD));
		} else {
			this.setPassword(this.getPassword());
		}
		if (!BBBUtility.isEmpty(this.getPassword())
				&& !this.getPassword().equals(csrPassword)) {
			this.addFormException(new DropletException(ERR_MSG_NOT_VALID_PASSWORD));
		}
		this.logDebug("validatePassword method end");

	}

	public void validateAdminUser() throws BBBSystemException, BBBBusinessException {
		this.logDebug("validateAdminUser method start");
		List<String> adminUsers = null;
		List<String> listOfPasswords = null;
		try {
			adminUsers = this.getCatalogTools().getAllValuesForKey(
					"ContentCatalogKeys", "admin_name");
			listOfPasswords = this.getCatalogTools().getAllValuesForKey(
					"ContentCatalogKeys", "admin_password");
			if (BBBUtility.isEmpty(this.getAdminName())) {
				this.addFormException(new DropletException(
						ERR_MSG_REQUIRED_NAME));
			} else if (adminUsers != null && !adminUsers.isEmpty()
					&& adminUsers.contains(this.getAdminName())) {
				this.setAdminName(this.getAdminName());
			} else {
				this.addFormException(new DropletException(
						ERR_MSG_NOT_VALID_NAME));
			}
			if (BBBUtility.isEmpty(this.getAdminPassword())) {
				this.addFormException(new DropletException(
						ERR_MSG_REQUIRED_PASSWORD));
			} else if (listOfPasswords != null && !listOfPasswords.isEmpty()
					&& this.getAdminPassword().equals(listOfPasswords.get(0))) {
				this.setAdminPassword(this.getAdminPassword());
			} else {
				this.addFormException(new DropletException(
						ERR_MSG_NOT_VALID_PASSWORD));
			}
		} catch (final BBBSystemException e) {
			throw new BBBSystemException("BBBSystemException - admin_name/admin_password key not found for site");
		} catch (final BBBBusinessException e) {
			throw new BBBBusinessException("BBBSystemException - admin_name/admin_password key not found for site");
		}
		this.logDebug("validateAdminUser method end");
	}

	public boolean handleUpdateRegistrant(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug("CSRFormHandler.handleUpdateRegistrant method Start: CSR Name"
				+ this.getCsrName() 
				+ " | RegistryId:" + this.getRegistryId() + " | Email-Id : "
				+ this.getEmailAddress() + " | Reg or Coreg:" + this.getRegCoreg());
		boolean success = false;

		this.preUpdateRegistrant(pRequest, pResponse);

		if (!this.getFormError()) {
			MutableRepositoryItem profilRepositoryItem = null;
			boolean isUserPresent = false;
			if (this.getProfileTools().getItemFromEmail(this.getEmailAddress()) != null) {
				profilRepositoryItem = (MutableRepositoryItem) this.getProfileTools()
						.getItemFromEmail(this.getEmailAddress());
				isUserPresent = this.isUserBelogToCurrentSite(profilRepositoryItem, this.getCurrentSiteId());
			}

			if ((profilRepositoryItem != null) && isUserPresent) {
				RepositoryItem[] registryRepoItems = null;
				try {
					registryRepoItems = this.getGiftRegistryTools()
							.fetchGiftRepositoryItem(this.getCurrentSiteId(), this.getRegistryId());
				} catch (final BBBBusinessException e) {
					this.logError("BBBBusinessException while fetch Registry Item", e);
					this.addFormException(new DropletException("Unable to extract registry data, please contact IT Support"));
				} catch (final BBBSystemException e) {
					this.logError(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,	e);
					this.addFormException(new DropletException("There was a system error while extracting registry data, please contact IT Support"));
				}

				RegistryReqVO registryInfoReqVO = null;
				RegistryResVO registryInfoResVO = null;
				RegistrySummaryVO registrySummaryVO = null;

				// Calling getRegistryInfo web service
				try {
					registryInfoReqVO = this.createRegistryReqVO();
					registryInfoResVO = this.getGiftRegistryManager().getRegistryInfo(registryInfoReqVO);
				} catch (final BBBSystemException e) {
					this.logError("BBBSystemException while calling getRegistryInfo webservice", e);
					this.addFormException(new DropletException("There was a system error while extracting registry data, please contact IT Support"));
				} catch (final BBBBusinessException e) {
					this.logError("BBBBusinessException while calling getRegistryInfo webservice", e);
					this.addFormException(new DropletException("There was a system error while extracting registry data, please contact IT Support"));
				}

				if(registryInfoResVO != null){
					if ((registryRepoItems != null) && (registryRepoItems.length > 0)) {
						MutableRepositoryItem giftRegistryItem = null;
						final MutableRepository giftRegistryRepository = this.getGiftRepository();

						giftRegistryItem = (MutableRepositoryItem) registryRepoItems[0];
						if (CO_REGISTRANT.equalsIgnoreCase(this.getRegCoreg())) {
							giftRegistryItem.setPropertyValue(REGISTRY_CO_OWNER, profilRepositoryItem);
						} else {
							giftRegistryItem.setPropertyValue(REGISTRY_OWNER, profilRepositoryItem);
						}
						try {
							giftRegistryRepository.updateItem(giftRegistryItem);
							success = true;
						} catch (final RepositoryException e) {
							this.logError(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
							this.addFormException(new DropletException("There was a system error while extracting registry data, please contact IT Support"));
						}

					} else {

						MutableRepositoryItem giftRegistryItem = null;
						final MutableRepository giftRegistryRepository = this.getGiftRepository();

							try {
								registrySummaryVO = this.getRegistrySummaryVO(registryInfoResVO);
							} catch (final BBBSystemException e) {
								this.logError("BBBSystemException while calling getRegistrySummaryVO", e);
								this.addFormException(new DropletException("There was a system error while extracting registry data, please contact IT Support"));
							} catch (final BBBBusinessException e) {
								this.logError("BBBBusinessException while calling getRegistrySummaryVO", e);
								this.addFormException(new DropletException("There was a system error while extracting registry data, please contact IT Support"));
							}
							if (registrySummaryVO != null) {
								try {
									giftRegistryItem = giftRegistryRepository.createItem("giftregistry");
								} catch (final RepositoryException repositoryException) {
									this.logError(BBBCatalogErrorCodes.UNABLE_TO_CREATE_RECORD_IN_REPOSITORY_EXCEPTION, repositoryException);
									this.addFormException(new DropletException("There was a system error while creating new record for registry data, please contact IT Support"));
								}

								if (giftRegistryItem != null) {
									giftRegistryItem.setPropertyValue(REGISTRY_ID, this.getRegistryId());
									if (CO_REGISTRANT.equalsIgnoreCase(this.getRegCoreg())) {
										giftRegistryItem.setPropertyValue(REGISTRY_CO_OWNER, profilRepositoryItem);
									} else {
										giftRegistryItem.setPropertyValue(REGISTRY_OWNER, profilRepositoryItem);
									}
									if (this.getFormExceptions().isEmpty()) {
										giftRegistryItem.setPropertyValue(EVENT_TYPE, registrySummaryVO.getRegistryType().getRegistryTypeName());
										try {
											giftRegistryItem.setPropertyValue(
													EVENT_DATE,	this.stringToDateConverter(registrySummaryVO.getEventDate(), this.getCurrentSiteId()));
										} catch (final BBBSystemException e) {
											this.logError("BBBSystemException while calling stringToDateConverter" + e);
											this.addFormException(new DropletException("There was a system error while adding registry data, please contact IT Support"));
										}
										giftRegistryItem.setPropertyValue(REGISTRY_STATUS, REGISTRY_STATUS_INACTIVE);

										try {
											giftRegistryRepository.addItem(giftRegistryItem);
											success = true;
										} catch (final RepositoryException repositoryException) {
											this.logError(BBBCatalogErrorCodes.UNABLE_TO_ADD_DATA_IN_REPOSITORY_EXCEPTION, repositoryException);
											this.addFormException(new DropletException(
													"There was a system error while adding registry data, please contact IT Support"));
										}
									}
								}
							}
						}
					try {
						LinkRegistryToProfileVO linkRegistryRegVO = this.createLinkRegistryReqVO();;
						RegistryResVO registryResVO = null;
						
						boolean regItemsWSCall = false;
						final List<String> regItemsWSCallFlag = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall");

						if (!BBBUtility.isListEmpty(regItemsWSCallFlag)) {
							regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag.get(0));
						}

						if (regItemsWSCall) {
							linkRegistryRegVO.setProfileId(profilRepositoryItem
									.getRepositoryId());
							// Calling LinkRegistryToATGProfile web service
							registryResVO = (RegistryResVO) ServiceHandlerUtil
									.invoke(linkRegistryRegVO);
						} else {
							linkRegistryRegVO.setProfileId(profilRepositoryItem.getRepositoryId());
							registryResVO = this.getGiftRegistryManager().linkRegistryToATGProfile(linkRegistryRegVO);
						}
						if (registryResVO != null) {
							// If web service error
							if ((registryResVO.getServiceErrorVO() != null)
									&& registryResVO.getServiceErrorVO().isErrorExists()) {
								this.addFormException(new DropletException("System error in extracting data from Registry System: "
										+ registryResVO.getServiceErrorVO().getErrorMessage()));

							} else {
								success = true;
							}
					}
				} catch (final BBBSystemException e) {
					this.logError(e.getMessage());
					this.addFormException(new DropletException(
							"There was a system error while extracting registry data, please contact IT Support"));
				} catch (final BBBBusinessException e) {
					this.logError(e.getMessage());
					this.addFormException(new DropletException(
							"There was a system error while extracting registry data, please contact IT Support"));
				}
				}

			} else {
				success = false;
				this.addFormException(new DropletException(
							"Either User " + this.getEmailAddress() + " not belog to Selected Site " + this.getCurrentSiteId() + " or no profile exists with given email address"));
				}
		}

		if (success) {
			this.setRegistrySuccessURL(this.getRegistrySuccessURL() + "?updated=true");
		} else {
			this.setRegistrySuccessURL(this.getRegistrySuccessURL() + "?updated=false");
		}
		return this.checkFormRedirect(this.getRegistrySuccessURL(),
				this.getRegistryErrorURL(), pRequest, pResponse);
	}

	public void preViewRegistryDetails(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) {
		this.logDebug("preViewRegistryDetails method start");
		this.validatePassword();
		String errorMessage = "";
		if (BBBUtility.isEmpty(this.getCsrName())) {
			errorMessage = "Please Provide CSR Name";
			this.addFormException(new DropletException(errorMessage));
		}
		if (BBBUtility.isEmpty(this.getSiteName())) {
			errorMessage = "Please Select Site Name";
			this.addFormException(new DropletException(errorMessage));
		}
		if (BBBUtility.isEmpty(this.getRegistryId())) {
			this.addFormException(new DropletException(ERR_MSG_REQUIRED_REGISTRY_ID));
		} else {
			this.setRegistryId(this.getRegistryId());
		}
		if (!BBBUtility.isNumericOnly(this.getRegistryId())) {
			this.addFormException(new DropletException(ERR_MSG_NOT_VALID_REGISTRY_ID));
		}

		/*
		 * if (!BBBUtility.isValidEmail(getEmailAddress())) {
		 * addFormException(new DropletException(
		 * ERR_MSG_NOT_VALID_EMAIL_ADDRESS)); } }
		 */
		this.logDebug("preViewRegistryDetails method end");

	}

	public boolean handleViewRegistryDetails(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug("handleViewRegistryDetails method start: CSR Name"
				+ this.getCsrName() + "|Activity: View Registry Details|Password:"
				+ "|RegistryId:" + this.getRegistryId());
		this.preViewRegistryDetails(pRequest, pResponse);

		if (!this.getFormError()) {
			RegistryReqVO regReqVO = null;
			RegistryResVO registryResVO = null;
			RegistrySummaryVO registrySummaryVO = null;
			try {
				regReqVO = this.createRegistryReqVO();
				// Calling getRegistryInfo web service
				registryResVO = this.getGiftRegistryManager().getRegistryInfo(
						regReqVO);
				if (registryResVO == null) {
					throw new BBBBusinessException("Error on CSR Page: registryResVO == null");
				}
				registrySummaryVO = this.getRegistrySummaryVO(registryResVO);
				if (registrySummaryVO == null) {
					throw new BBBBusinessException("Error on CSR Page: registrySummaryVO == null");
				}

				this.getRegistryInfoBean().setRegistryId(
						registrySummaryVO.getRegistryId());
				this.getRegistryInfoBean().setEventDate(registrySummaryVO.getEventDate());
				this.getRegistryInfoBean().setEventType(registrySummaryVO.getEventType());
				this.getRegistryInfoBean().setOwnerEmailAddress(registrySummaryVO.getRegistrantEmail());
				this.getRegistryInfoBean().setCoownerEmailAddress(registrySummaryVO.getCoRegistrantEmail());
				this.getRegistryInfoBean().setOwnerProfileId(registrySummaryVO.getOwnerProfileID());
				this.getRegistryInfoBean().setCoownerProfileId(registrySummaryVO.getCoownerProfileID());
				this.getRegistryInfoBean().setOwnerFirstName(registrySummaryVO.getPrimaryRegistrantFirstName());
				this.getRegistryInfoBean().setOwnerLastName(registrySummaryVO.getPrimaryRegistrantLastName());
				this.getRegistryInfoBean().setCoOwnerFirstName(registrySummaryVO.getCoRegistrantFirstName());
				this.getRegistryInfoBean().setCoOwnerLastName(registrySummaryVO.getCoRegistrantLastName());

				// setting the event type from event code
				if ((null != registrySummaryVO.getRegistryType())
						&& (registrySummaryVO.getRegistryType()
								.getRegistryTypeName() != null)) {
					final String eventName = this.getCatalogTools().getRegistryTypeName(
							registrySummaryVO.getRegistryType()
									.getRegistryTypeName(), this.getCurrentSiteId());
					if(BBBUtility.isEmpty(eventName)){
						this.addFormException(new DropletException("Selected Registry is not associated with " + this.getCurrentSiteId() + " , please contact IT Support"));
					}
					this.getRegistryInfoBean().setEventType(eventName);

				}

			} catch (final BBBSystemException e) {
				this.logError(e.getMessage());
				this.addFormException(new DropletException("System error occurred while extracting registry data, please contact IT Support."));
			} catch (final BBBBusinessException e) {
				this.logError(e.getMessage());
				this.addFormException(new DropletException("Selected Registry is not associated with " + this.getCurrentSiteId() + " , please contact IT Support"));
			}

			// Start ATG details - Fetch registry details stored in ATG
			MutableRepositoryItem profilRepositoryItem = null;
			if (this.getProfileTools().getItemFromEmail(this.getEmailAddress()) != null) {

				profilRepositoryItem = (MutableRepositoryItem) this.getProfileTools()
						.getItemFromEmail(this.getEmailAddress());
			}

			if (profilRepositoryItem != null) {

				this.getRegistryInfoBean().setProfileIDOfEmail(
						profilRepositoryItem.getRepositoryId());
				final Date registrationDate = (Date) profilRepositoryItem
						.getPropertyValue(REGISTRATION_DATE);
				this.getRegistryInfoBean().setRegistrationDateOfEmailEnteredATG(
						registrationDate.toString());
			}

			RepositoryItem[] registryRepoItems = null;
			try {
				registryRepoItems = this.getGiftRegistryTools()
						.fetchGiftRepositoryItem(this.getCurrentSiteId(),
								this.getRegistryId());
			} catch (final BBBBusinessException e) {
				this.logError(
						"BBBBusinessException from fetchGiftRepositoryItem of GiftRegistryTools",
						e);
				this.addFormException(new DropletException("Unable to find registry data, please contact IT Support"));
			} catch (final BBBSystemException e) {
				this.logError(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						e);
				this.addFormException(new DropletException("Unable to find registry data, please contact IT Support"));
			}

			// if there is any entry for registry then find details of registry,
			// owner and coowner
			if (registryRepoItems != null) {

				MutableRepositoryItem giftRegistryItem = null;

				giftRegistryItem = (MutableRepositoryItem) registryRepoItems[0];

				this.getRegistryInfoBean().setRegistryIDATG((String) giftRegistryItem.getPropertyValue(REGISTRY_ID));
				final Date eventDateATG = (Date) giftRegistryItem.getPropertyValue(EVENT_DATE);
				if (eventDateATG != null) {
					final String formattedDate = new SimpleDateFormat(BBBCoreConstants.US_DATE_FORMAT).format(eventDateATG);
					this.getRegistryInfoBean().setEventDateATG(formattedDate);
				}
				String eventTypeATG = (String) giftRegistryItem.getPropertyValue(EVENT_TYPE);
				try {
					eventTypeATG = this.getCatalogTools().getRegistryTypeName(eventTypeATG, this.getCurrentSiteId());
				} catch (final BBBSystemException e) {
					this.logError(
							"BBBBusinessException from getRegistryTypeName of CatalogTools",
							e);
					this.addFormException(new DropletException("Unable to extract registry data, please contact IT Support"));
				} catch (final BBBBusinessException e) {
					this.logError("BBBBusinessException from getRegistryTypeName of CatalogTools", e);
					this.addFormException(new DropletException("Unable to find registry data, please contact IT Support"));
				}
				this.getRegistryInfoBean().setEventTypeATG(eventTypeATG);

				if (giftRegistryItem.getPropertyValue(REGISTRY_OWNER) != null) {
					final RepositoryItem ownerProfile = (RepositoryItem) giftRegistryItem.getPropertyValue(REGISTRY_OWNER);

					this.getRegistryInfoBean().setOwnerProfileIDATG(ownerProfile.getRepositoryId());
					this.getRegistryInfoBean().setOwnerEmailAddressATG((String) ownerProfile.getPropertyValue(EMAIL));
					this.getRegistryInfoBean().setOwnerFirstNameATG((String) ownerProfile.getPropertyValue(FIRST_NAME));
					this.getRegistryInfoBean().setOwnerLastNameATG((String) ownerProfile.getPropertyValue(LAST_NAME));

					final Date registrationDate = (Date) ownerProfile.getPropertyValue(REGISTRATION_DATE);
					this.getRegistryInfoBean().setOwnerRegistrationDateATG(registrationDate.toString());

					// private boolean isOwnerMigrated;
					final Boolean isAccountMigrated = (Boolean) ownerProfile.getPropertyValue(MIGRATED_ACCOUNT);
					final boolean value = (isAccountMigrated != null ? isAccountMigrated.booleanValue() : false);
					this.getRegistryInfoBean().setOwnerMigrated(value);

					if (profilRepositoryItem != null) {

						final boolean isInputEmaiAndOwnerSame = profilRepositoryItem.getRepositoryId().equalsIgnoreCase(ownerProfile.getRepositoryId());
						this.getRegistryInfoBean().setOwnerAndInputEmailSame(isInputEmaiAndOwnerSame);
					}

				} else {
					this.getRegistryInfoBean().setOwnerProfileIDATG(null);

				}

				if (giftRegistryItem.getPropertyValue(REGISTRY_CO_OWNER) != null) {

					final RepositoryItem coownerProfile = (RepositoryItem) giftRegistryItem.getPropertyValue(REGISTRY_CO_OWNER);
					this.getRegistryInfoBean().setCoownerProfileIDATG(coownerProfile.getRepositoryId());
					this.getRegistryInfoBean().setCoownerEmailAddressATG((String) coownerProfile.getPropertyValue(EMAIL));
					this.getRegistryInfoBean().setCoOwnerFirstNameATG((String) coownerProfile.getPropertyValue(FIRST_NAME));
					this.getRegistryInfoBean().setCoOwnerLastNameATG((String) coownerProfile.getPropertyValue(LAST_NAME));

					final Date registrationDate = (Date) coownerProfile.getPropertyValue(REGISTRATION_DATE);
					this.getRegistryInfoBean().setCoownerRegistrationDateATG(registrationDate.toString());

					// private boolean isCoownerMigrated;
					// private boolean isOwnerMigrated;
					final Boolean isAccountMigrated = (Boolean) coownerProfile.getPropertyValue(MIGRATED_ACCOUNT);
					final boolean value = (isAccountMigrated != null ? isAccountMigrated.booleanValue() : false);
					this.getRegistryInfoBean().setCoownerMigrated(value);

				} else {
					this.getRegistryInfoBean().setCoownerProfileIDATG(null);

				}
			}
			// End ATG

		}

		this.setRegistryErrorURL(this.getRegistryErrorURL() + "?showData=false");
		final String successURL = this.getRegistrySuccessURL() + "?showData=true";
		this.setRegistrySuccessURL(successURL);
		return this.checkFormRedirect(this.getRegistrySuccessURL(),
				this.getRegistryErrorURL(), pRequest, pResponse);
	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean handleClearCache(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException {
		this.logDebug("handleClearCache method start: Admin Name: "
				+ this.getAdminName() + "|Activity: Clear Cache|Cache Type: "
				+ this.getCacheType());
		this.validateAdminUser();
		String cacheClearMsg = "";

		if (!this.getFormError()) {
			this.logDebug("handleClearCache: Cache clear started for "
					+ this.getCacheType());
			cacheClearMsg = this.invokeToClearCache(pRequest, pResponse,
					this.getCacheType(), null);
			this.logDebug("handleClearCache: Cache clear done.");
		}
		this.setClearCacheErrorURL(this.getClearCacheErrorURL());
		final String successURL = this.getClearCacheSuccessURL() + "?updated="
				+ cacheClearMsg;
		this.setClearCacheSuccessURL(successURL);
		return this.checkFormRedirect(this.getClearCacheSuccessURL(),
				this.getClearCacheErrorURL(), pRequest, pResponse);
	}
	
	/**
	 * Method is used to fetch value of a key in coherence for any type of cache for desktop.
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean handleGetValueForKey(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException {
		String key = BBBCoreConstants.BLANK;
		this.logDebug("handleGetValueForKey method start: Admin Name: "
				+ this.getAdminName() + "|Activity: Cache Key|Cache Type: "
				+ key+ "|" + this.getCacheType());
		this.validateAdminUser();
		String cacheName = BBBCoreConstants.BLANK;
		if (!this.getFormError() && !BBBUtility.isEmpty(this.getCacheKeyForValue())) {
			this.logDebug("handleGetValueForKey: get Cache value started for "
					+ this.getCacheType());
			key = this.getCacheKeyForValue();
			String cacheType = this.getCacheType();
			cacheName = this.getCoherenceCacheMap().get(cacheType);
			setJsonValueForKeyFromCache(key, cacheName, false);
		}
		return this.checkFormRedirect(this.getSuccessURLForKeyValue(),
				this.getFailureURLForKeyValue(), pRequest, pResponse);	
	}
	/**
	 * Method is used to fetch value of a key in coherence for any type of cache for mobile.
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean handleGetValueForKeyMobile(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException {
		String key = BBBCoreConstants.BLANK;
		this.logDebug("handleGetValueForKeyMobile method start: Admin Name: "
				+ this.getAdminName() + "|Activity: Cache Key|Cache Type: "
				+ key+ "|" + this.getCacheType());
		this.validateAdminUser();
		String cacheName = BBBCoreConstants.BLANK;
		if (!this.getFormError() && !BBBUtility.isEmpty(this.getMobileCacheKey())){
			this.logDebug("handleGetValueForKeyMobile: get Mobile Cache value started for "
					+ this.getCacheType());
			key = this.getMobileCacheKey();
			String cacheType = this.getMobileCacheType();
			cacheName = this.getMobileCoherenceCacheMap().get(cacheType);
			setJsonValueForKeyFromCache(key, cacheName, true);
		}
		return this.checkFormRedirect(this.getSuccessURLForKeyValue(),
				this.getFailureURLForKeyValue(), pRequest, pResponse);
	}
	/**
	 * Method fetches value of a key in coherence for any type of cache for mobile and desktop also it converts result to json.
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public void setJsonValueForKeyFromCache(String key, String cacheName, boolean isForMobile) throws BBBSystemException, IOException{
		Object result = getCoherenceCacheContainer().get(key, cacheName);
		String jsonResult = BBBCoreConstants.BLANK;
		if(result != null){
			jsonResult = BBBUtility.convertToJSON(result);
			if(!BBBUtility.isEmpty(jsonResult) && !isForMobile){
				this.setJsonResult(jsonResult);
			}else if(!BBBUtility.isEmpty(jsonResult)){
				this.setJsonResultMobile(jsonResult);
			}else{
				this.addFormException(new DropletException("Error while converting to JSON"));
			}
		}else{
			this.addFormException(new DropletException("No value found for the key in coherence Cache"));
		}
	}
	/**
	 * This method handles the call of clearing of cache of all keys. This method validates the user 
	 * and then calls another methos to clear the back cache and near cache
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean handleClearMobileCache(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException {
		this.logDebug("handleClearMobileCache method start: Admin Name: "
				+ this.getAdminName() + "|Activity: Clear Cache|Cache Type: "
				+ this.getMobileCacheType());
		this.validateAdminUser();
		String cacheClearMsg = "";

		if (!this.getFormError()) {
			this.logDebug("handleClearMobileCache: Cache clear started for "
					+ this.getMobileCacheType());
			cacheClearMsg = this.invokeToClearMobileCache(pRequest, pResponse,
					this.getMobileCacheType(), null);
			this.logDebug("handleClearMobileCache: Cache clear done.");
		}
		this.setClearCacheErrorURL(this.getClearMobileCacheFailureURL());
		final String successURL = this.getClearCacheSuccessURL() + "?updatedMobile="
				+ cacheClearMsg;
		this.setClearCacheSuccessURL(successURL);
		this.logDebug("handleClearMobileCache method end");
		return this.checkFormRedirect(this.getClearCacheSuccessURL(),
				this.getClearCacheErrorURL(), pRequest, pResponse);
	}
	
	/**
	 * This method handles the call to get the size of cache. This method validates the user 
	 * and then calls another method to get the back cache and near cache size
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean handleGetNearMobileCacheSize(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException {
		this.logDebug("handleGetNearMobileCacheSize method start: Admin Name: "
				+ this.getAdminName() + "|Activity: Get Size|Cache Type: "
				+ this.getMobileCacheType());
		this.validateAdminUser();
		String cacheClearMsg = "";

		if (!this.getFormError()) {
			this.logDebug("handleGetNearMobileCacheSize: Get Size started for "
					+ this.getMobileCacheType());
			cacheClearMsg = this.invokeToGetNearMobileCacheSize(pRequest, pResponse,
					this.getMobileCacheType());
			this.logDebug("handleGetNearMobileCacheSize: Get Size done.");
		}
		this.setClearCacheErrorURL(this.getClearMobileCacheFailureURL());
		final String successURL = this.getClearCacheSuccessURL() + "?updatedMobile="
				+ cacheClearMsg;
		this.setClearCacheSuccessURL(successURL);
		this.logDebug("handleGetNearMobileCacheSize method start");
		return this.checkFormRedirect(this.getClearCacheSuccessURL(),
				this.getClearCacheErrorURL(), pRequest, pResponse);
	}
	
	/**
	 * This method gets the size of back cache and near cache. Cache Type 'ALL' is not processed.
	 * 
	 * @param pResponse 
	 * @param pRequest 
	 * @param cacheType
	 * @param selectedKeys
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private String invokeToGetNearMobileCacheSize(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, final String cacheType) throws BBBSystemException,
			BBBBusinessException {
		this.logDebug("invokeToGetNearMobileCacheSize method start");
		String backCacheMessage = "SIZE(Back Cache):  ";
		String nearCacheMessage = "SIZE(Near Cache):  ";
		String cacheClearMsg = "";

		if (BBBCatalogConstants.COHERENCE_CACHES_ALL.equalsIgnoreCase(this
				.getMobileCacheType())) {
			this.addFormException(new DropletException(
					"WARNING: ALL is not allowed. Please select other cache to perform task!"));
		} else {
			final String cacheName = this.getMobileCoherenceCacheMap().get(
					this.getMobileCacheType());
			if(BBBUtility.isNotEmpty(cacheName)){
				backCacheMessage = backCacheMessage
						+ this.getCoherenceCacheContainer().getBackCacheSize(
								cacheName);
				nearCacheMessage = nearCacheMessage + this.getBbbCacheInvalidatorMessageSource()
									.getNearMobileCacheSize(cacheName);
				cacheClearMsg = backCacheMessage + "<br>" + nearCacheMessage;
			}else{
				this.addFormException(new DropletException(
						"ERROR: Invalid Cache Name"));
			}
		}
		this.logDebug("invokeToGetNearMobileCacheSize method end");
		return cacheClearMsg;

	}
	
	
	/**
	 * This method handles the call of clearing of cache of selected keys. This method validates the user 
	 * and then calls another method to clear the back cache and near cache with selected keys
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean handleClearMobileCacheSelectedKeys(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException {
		this.logDebug("handleClearMobileCacheSelectedKeys method start: Admin Name: "
				+ this.getAdminName() + "|Activity: Clear Cache|Cache Type: "
				+ this.getMobileCacheType());
		this.validateAdminUser();
		String cacheClearMsg = "";
		List<String> selectedKeys  = this.preClearMobileCacheSelectedKeys(pRequest, pResponse);
		if (!this.getFormError()) {
			this.logDebug("handleClearMobileCacheSelectedKeys: Cache keys clear started for "
					+ this.getMobileCacheType());
			cacheClearMsg = this.invokeToClearMobileCache(pRequest, pResponse,
					this.getMobileCacheType(), selectedKeys);
			this.logDebug("handleClearMobileCacheSelectedKeys: Cache keys clear done.");
		}
		this.setClearCacheErrorURL(this.getClearMobileCacheFailureURL());
		final String successURL = this.getClearCacheSuccessURL() + "?updatedMobile="
				+ cacheClearMsg;
		this.setClearCacheSuccessURL(successURL);
		this.logDebug("handleClearMobileCacheSelectedKeys method end");
		return this.checkFormRedirect(this.getClearCacheSuccessURL(),
				this.getClearCacheErrorURL(), pRequest, pResponse);
	}
	
	/**
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean handleClearCacheSelectedKeys(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException {
		this.logDebug("handleClearCacheSelectedKeys method start: Admin Name: "
				+ this.getAdminName() + "|Activity: Clear Cache|Cache Type: "
				+ this.getCacheType());
		this.validateAdminUser();
		List<String> selectedKeys = new ArrayList<String>();
		String cacheClearMsg = "";
		this.preClearCacheSelectedKeys(pRequest, pResponse, selectedKeys);
		if (!this.getFormError()) {
			this.logDebug("handleClearCacheSelectedKeys: Cache keys clear started for "
					+ this.getCacheType());
			cacheClearMsg = this.invokeToClearCache(pRequest, pResponse,
					this.getCacheType(), selectedKeys);
			this.logDebug("handleClearCacheSelectedKeys: Cache keys clear done.");
		}
		this.setClearCacheErrorURL(this.getClearCacheErrorURL());
		final String successURL = this.getClearCacheSuccessURL() + "?updated="
				+ cacheClearMsg;
		this.setClearCacheSuccessURL(successURL);
		return this.checkFormRedirect(this.getClearCacheSuccessURL(),
				this.getClearCacheErrorURL(), pRequest, pResponse);
	}
	
	/**
	 * This method denies the processing if cache type is 'ALL' 
	 * and then get the List of keys from array of selected keys.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param selectedKeys
	 */
	private List<String> preClearMobileCacheSelectedKeys(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) {
		if (BBBCatalogConstants.COHERENCE_CACHES_ALL.equalsIgnoreCase(this
				.getMobileCacheType())) {
			this.addFormException(new DropletException(
					"WARNING: ALL is not allowed. Please select other cache to perform task!"));
			return null;
		} else {
			if (this.getSelectedMobileCacheKeysToDelete() != null
					&& this.getSelectedMobileCacheKeysToDelete().length != 0) {
				List<String> selectedKeys = Arrays.asList(this.getSelectedMobileCacheKeysToDelete());
				this.logDebug("Selected Keys: " + selectedKeys);
				return selectedKeys;
			} else {
				this.addFormException(new DropletException(
						"ERROR: Go for 'List All Keys' for a cache then select key/keys to delete."));
				return null;
			}
			
		}
	}
	
	/**
	 * @param pRequest
	 * @param pResponse
	 * @param selectedKeys
	 */
	private void preClearCacheSelectedKeys(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, List<String> selectedKeys) {
		if (BBBCatalogConstants.COHERENCE_CACHES_ALL.equalsIgnoreCase(this
				.getCacheType())) {
			this.addFormException(new DropletException(
					"WARNING: ALL is not allowed. Please select other cache to perform task!"));
		} else {
			if (this.getSelectedCacheKeysToDelete() != null
					&& this.getSelectedCacheKeysToDelete().length != 0) {
				String[] arrayOfKeys = this.getSelectedCacheKeysToDelete();
				for (String key : arrayOfKeys) {
					selectedKeys.add(key);
				}
			} else {
				this.addFormException(new DropletException(
						"ERROR: Go for 'List All Keys' for a cache then select key/keys to delete."));
			}
			this.logDebug("Selected Keys: " + selectedKeys);
		}
	}
	
	/**
	 * This method clears the back cache of mobile cache type and then calls another method 
	 * to end SQL message to all destinations to clear the near cache
	 * 
	 * @param pResponse 
	 * @param pRequest 
	 * @param cacheType
	 * @param selectedKeys
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private String invokeToClearMobileCache(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, final String cacheType,
			final List<String> selectedKeys) throws BBBSystemException,
			BBBBusinessException {
		this.logDebug("invokeToClearMobileCache method start");
		String backCacheMessage = "SIZE(Back Cache): Before: ";

		if (BBBCatalogConstants.COHERENCE_CACHES_ALL.equalsIgnoreCase(this
				.getMobileCacheType())) {
			for (final Map.Entry<String, String> cache : this
					.getMobileCoherenceCacheMap().entrySet()) {
				this.getCoherenceCacheContainer().clearCache(cache.getValue());
			}
			//To clear Near Cache for ALL
			for (final Map.Entry<String, String> cache : this
					.getMobileCoherenceCacheMap().entrySet()) {
				this.getBbbCacheInvalidatorMessageSource().clearNearCache(
						cache.getValue(), null, true);
			}
			backCacheMessage = "Cleared all caches.";
		} else {
			final String cacheName = this.getMobileCoherenceCacheMap().get(
					this.getMobileCacheType());
			if (!BBBUtility.isEmpty(cacheName)) {
				this.logDebug("Start to clear Back Cache...");
				backCacheMessage = backCacheMessage
						+ this.getCoherenceCacheContainer().getBackCacheSize(
								cacheName);
				this.getCoherenceCacheContainer().clearCache(cacheName,
						selectedKeys);
				backCacheMessage = backCacheMessage
						+ ", After: "
						+ this.getCoherenceCacheContainer().getBackCacheSize(
								cacheName);
				this.logDebug("Back Cache clear done.");
				if (this.getBbbCacheInvalidatorMessageSource() != null) {
					this.logDebug("Start to clear Near Cache...");
					this.getBbbCacheInvalidatorMessageSource().clearNearCache(
							cacheName, selectedKeys, true);
					this.logDebug("Near Cache clear done.");
				} else {
					this.addFormException(new DropletException(
							"ERROR: Message Source[BBBCacheInvalidatorSource] component *NOT* initialized."));
				}
			} else {
				this.addFormException(new DropletException(
						"ERROR: Invalid Cache Name"));
			}
		}
		this.logDebug("invokeToClearMobileCache method end");
		return backCacheMessage;

	}

	/**
	 * @param pResponse 
	 * @param pRequest 
	 * @param cacheType
	 * @param selectedKeys
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private String invokeToClearCache(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, final String cacheType,
			final List<String> selectedKeys) throws BBBSystemException,
			BBBBusinessException {
		String backCacheMessage = "SIZE(Back Cache): Before: ";
		String nearCacheMessage = "SIZE(Near Cache): Before: ";
		String cacheClearMsg = "";

		if (BBBCatalogConstants.COHERENCE_CACHES_ALL.equalsIgnoreCase(this
				.getCacheType())) {
			//To clear Back Cache for ALL
			for (final Map.Entry<String, String> cache : this
					.getCoherenceCacheMap().entrySet()) {
				this.getCoherenceCacheContainer().clearCache(cache.getValue());
			}
			//To clear Near Cache for ALL
			for (final Map.Entry<String, String> cache : this
					.getCoherenceCacheMap().entrySet()) {
				this.getBbbCacheInvalidatorMessageSource().clearNearCache(
						cache.getValue(), null, false);
			}
			cacheClearMsg = "Cleared all caches.";
		} else {
			final String cacheName = this.getCoherenceCacheMap().get(
					this.getCacheType());
			if (!BBBUtility.isEmpty(cacheName)) {
				this.logDebug("Start to clear Back Cache...");
				backCacheMessage = backCacheMessage
						+ this.getCoherenceCacheContainer().getBackCacheSize(
								cacheName);
				nearCacheMessage = nearCacheMessage
						+ this.getBbbCacheInvalidatorMessageSource()
								.getNearCacheSize(cacheName);
				this.getCoherenceCacheContainer().clearCache(cacheName,
						selectedKeys);
				backCacheMessage = backCacheMessage
						+ ", After: "
						+ this.getCoherenceCacheContainer().getBackCacheSize(
								cacheName);
				this.logDebug("Back Cache clear done.");
				if (this.getBbbCacheInvalidatorMessageSource() != null) {
					this.logDebug("Start to clear Near Cache...");
					this.getBbbCacheInvalidatorMessageSource().clearNearCache(
							cacheName, selectedKeys, false);
					nearCacheMessage = nearCacheMessage
							+ ", After: "
							+ this.getBbbCacheInvalidatorMessageSource()
									.getNearCacheSize(cacheName);
					this.logDebug("Near Cache clear done.");
					cacheClearMsg = backCacheMessage + "<br>"
							+ nearCacheMessage;
				} else {
					this.addFormException(new DropletException(
							"ERROR: Message Source[BBBCacheInvalidatorSource] component *NOT* initialized."));
				}
			} else {
				this.addFormException(new DropletException(
						"ERROR: Invalid Cache Name"));
			}
		}
		return cacheClearMsg;

	}
	
	/**
	 * This method handles the call to get all the keys of the selected cache type. 
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @Description Get the list of all Keys for selected cache type
	 */
	@SuppressWarnings("rawtypes")
	public boolean handleListAllMobileKeys(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException {
		this.logDebug("handleListAllMobileKeys method start: Admin Name: "
				+ this.getAdminName() + "|Activity: Clear Cache|Cache Type: "
				+ this.getMobileCacheType());
		int cacheSize = 0;
		String cacheType = this.getMobileCacheType();
		String cacheName = this.getMobileCoherenceCacheMap().get(cacheType);
		final String errorURL = this.getClearMobileCacheFailureURL();
		final String successURL = this.getClearCacheSuccessURL() + "?updatedMobile=";
		
		if (!StringUtils.isBlank(cacheName)) {
		    cacheSize =  this.getCoherenceCacheContainer().getBackCacheSize(cacheName);	
		}
		
		List<String> listCacheKeys =  invokeToGetListAllKeys(pRequest, pResponse, cacheType,
				cacheName, successURL, errorURL, String.valueOf(cacheSize));
		this.setListOfMobileCacheKeys(listCacheKeys);
		return this.checkFormRedirect(this.getClearCacheSuccessURL(),
				this.getClearCacheErrorURL(), pRequest, pResponse);
	}	
	

	/**
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @Description Get the list of all Keys for selected cache type
	 */
	@SuppressWarnings("rawtypes")
	public boolean handleListAllKeys(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException {
		this.logDebug("handleListAllKeys method start: Admin Name: "
				+ this.getAdminName() + "|Activity: Clear Cache|Cache Type: "
				+ cacheType);
		int cacheSize = 0;
		String cacheType = this.getCacheType();
		String cacheName = this.getCoherenceCacheMap().get(cacheType);
		final String errorURL = this.getClearCacheErrorURL();
		final String successURL = this.getClearCacheSuccessURL() + "?updated=";
		if(!StringUtils.isBlank(cacheName)) {
		    cacheSize = this.getBbbCacheInvalidatorMessageSource().getNearCacheSize(cacheName);
		}
		List<String> listCacheKeys =  invokeToGetListAllKeys(pRequest, pResponse, cacheType,
				cacheName, successURL, errorURL, String.valueOf(cacheSize));
		this.setListOfCacheKeys(listCacheKeys);
		return this.checkFormRedirect(this.getClearCacheSuccessURL(),
				this.getClearCacheErrorURL(), pRequest, pResponse);
	}

	/**
	 * This method is used to get all the keys of the selected cache type. 
	 * Also gets the max cache keys to be displayed value from config key
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param cacheType
	 * @param cacheName
	 * @param isForMobile
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws ServletException
	 * @throws IOException
	 */
	private List<String> invokeToGetListAllKeys(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, String cacheType,
			String cacheName, String successUrl, String errorUrl, String cacheSize) throws BBBSystemException,
			BBBBusinessException, ServletException, IOException {
		
		this.logDebug("invokeToGetListAllKeys: List Cache Keys done.");
		this.validateAdminUser();
		List<String> listOfCacheKeys = new ArrayList<String>();
		String msg = "SIZE OF CACHE ";

		if (!this.getFormError()) {
			this.logDebug("invokeToGetListAllKeys: List Cache Keys started for "
					+ cacheType);
			final String maxCacheKeys = BBBConfigRepoUtils.getStringValue(
					BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.MAX_CACHE_KEYS_LIMIT);
			this.setMaxCacheKeysLimit(Integer.valueOf(maxCacheKeys));
			if (BBBCatalogConstants.COHERENCE_CACHES_ALL.equalsIgnoreCase(cacheType)) {
				msg += "ALL is not allowed.";
				this.addFormException(new DropletException(
						"WARNING: ALL is not allowed. Please select other cache to list all keys!"));
			} else {
				if (!BBBUtility.isEmpty(cacheName)) {
					this.logDebug("Start to get list of all cache keys...");
					msg += cacheType
							+ ": "
							+ cacheSize;
					Iterator keys = this.getCoherenceCacheContainer()
							.getAllKeys(cacheName);
					if (keys != null) {
						while (keys.hasNext()) {
							listOfCacheKeys.add((String) keys.next());
						}
					}
					
					this.logDebug("get list of all cache keys done.");
				} else {
					this.addFormException(new DropletException(
							"ERROR: Invalid Cache Name"));
				}
			}
			this.logDebug("invokeToGetListAllKeys: List Cache Keys done.");
		}
		this.setClearCacheErrorURL(errorUrl);
		final String successURL = successUrl + msg;
		this.setClearCacheSuccessURL(successURL);
		return listOfCacheKeys;

	}

	public void preViewProfileDetails(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) {
		this.logDebug("preViewProfileDetails method start");
		this.validatePassword();
		if (BBBUtility.isEmpty(this.getEmailAddress())) {
			this.addFormException(new DropletException(
					ERR_MSG_REQUIRED_EMAIL_ADDRESS));
		}
		if (!BBBUtility.isEmpty(this.getEmailAddress())
				&& !BBBUtility.isValidEmail(this.getEmailAddress())) {
			this.addFormException(new DropletException(
					ERR_MSG_NOT_VALID_EMAIL_ADDRESS));
		}
		this.logDebug("preViewProfileDetails method end");
	}

	
	public void preViewOrderDetails(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) {
		this.logDebug("preViewProfileDetails method start");
		this.validatePassword();
		String errorMessage = "";
		
		if (BBBUtility.isEmpty(this.getCsrName())) {
			errorMessage = "Please Provide CSR Name";
			this.addFormException(new DropletException(errorMessage));
		}
		
		if (BBBUtility.isEmpty(this.getEmailAddress())) {
			this.addFormException(new DropletException(
					ERR_MSG_REQUIRED_EMAIL_ADDRESS));
		}
		if (!BBBUtility.isEmpty(this.getEmailAddress())
				&& !BBBUtility.isValidEmail(this.getEmailAddress())) {
			this.addFormException(new DropletException(
					ERR_MSG_NOT_VALID_EMAIL_ADDRESS));
		}
		this.logDebug("preViewProfileDetails method end");
	}
	
	
	public boolean isUserExists() throws ServletException, IOException {

		RepositoryItem profileItem = null;
		if (this.getProfileTools().getItemFromEmail(this.getEmailAddress()) != null) {
			profileItem = this.getProfileTools().getItemFromEmail(
					this.getEmailAddress());
		}
		if (profileItem != null) {
			return true;
		}
		return false;

	}

	public boolean handleViewProfileDetails(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug("handleViewProfileDetails method start");
		this.preViewProfileDetails(pRequest, pResponse);
		if (!this.getFormError()) {

			boolean isUserExists = false;
			isUserExists = this.isUserExists();
			if (isUserExists) {
				final String successURL = this.getProfileSuccessURL() + "?showData=true";
				this.setProfileSuccessURL(successURL);
			} else {
				this.addFormException(new DropletException(ERR_MSG_NO_PROFILE_FOUND));
				final String errorURL = this.getProfileErrorURL() + "&noprofile=true";
				this.setProfileErrorURL(errorURL);
			}
			if (isUserExists) {
				RepositoryItem profileItem = null;
				profileItem = this.getProfileTools()
						.getItemFromEmail(this.getEmailAddress());
				final String profileId = profileItem.getRepositoryId();
				final Date registrationDate = (Date) profileItem
						.getPropertyValue(REGISTRATION_DATE);
				final String firstName = (String) profileItem
						.getPropertyValue(FIRST_NAME);
				final String lastName = (String) profileItem
						.getPropertyValue(LAST_NAME);
				final String emaillAddress = (String) profileItem
						.getPropertyValue(EMAIL);
				String memberId = "";
				@SuppressWarnings("unchecked")
				final Map<String, RepositoryItem> userSiteItems = (Map<String, RepositoryItem>) profileItem
						.getPropertyValue(BBBCertonaConstants.USER_SITE_ITEMS);
				if (userSiteItems != null) {
					final Set<String> keySet = userSiteItems.keySet();
					for (final String key : keySet) {
						final RepositoryItem userSiteAssoc = userSiteItems
								.get(key);
						if (userSiteAssoc != null) {
							if (userSiteAssoc
									.getPropertyValue(BBBCertonaConstants.MEMBER_ID) != null) {
								memberId = (String) userSiteAssoc
										.getPropertyValue(BBBCertonaConstants.MEMBER_ID);
							}
						}
					}
				}
				boolean isAccountLocked = false;
				isAccountLocked = this.getProfileTools()
						.isAccountLocked(profileItem);
				this.getProfileInfoBean().setProfileId(profileId);
				this.getProfileInfoBean().setMemberId(memberId);
				this.getProfileInfoBean().setFirstName(firstName);
				this.getProfileInfoBean().setLastName(lastName);
				this.getProfileInfoBean().setEmailAddress(emaillAddress);
				this.getProfileInfoBean().setRegistrationDate(
						registrationDate.toString());
				if (isAccountLocked) {
					this.getProfileInfoBean()
							.setAccountLocked(BBBCoreConstants.TRUE);
				} else {
					this.getProfileInfoBean().setAccountLocked(
							BBBCoreConstants.FALSE);
				}
				this.getProfileInfoBean().setUserSiteAssociations((Map)profileItem.getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM));

			}

		}
		return this.checkFormRedirect(this.getProfileSuccessURL(), this.getProfileErrorURL(),
				pRequest, pResponse);

	}

	public void preUnlockAccount(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) {
		this.logDebug("preUnlockAccount method start");
		this.validatePassword();
		if (BBBUtility.isEmpty(this.getEmailAddress())) {
			this.addFormException(new DropletException(
					ERR_MSG_REQUIRED_EMAIL_ADDRESS));
		}
		if (!BBBUtility.isEmpty(this.getEmailAddress())
				&& !BBBUtility.isValidEmail(this.getEmailAddress())) {
			this.addFormException(new DropletException(
					ERR_MSG_NOT_VALID_EMAIL_ADDRESS));
		}
		this.logDebug("preUnlockAccount method end");
	}
	
	public void preSetTemporaryPassword(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) {
		this.logDebug("preSetTemporaryPassword method start");
		if (BBBUtility.isEmpty(this.getEmailAddress())) {
			this.addFormException(new DropletException(
					ERR_MSG_REQUIRED_EMAIL_ADDRESS));
		}
		if (!BBBUtility.isEmpty(this.getEmailAddress())
				&& !BBBUtility.isValidEmail(this.getEmailAddress())) {
			this.addFormException(new DropletException(
					ERR_MSG_NOT_VALID_EMAIL_ADDRESS));
		}
		this.logDebug("preSetTemporaryPassword method end");
	}

	public boolean handleUnlockAccount(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug("handleUnlockAccount method start");
		this.preUnlockAccount(pRequest, pResponse);
		if (!this.getFormError()) {

			boolean isUserExists = false;
			isUserExists = this.isUserExists();
			if (!isUserExists) {
				this.addFormException(new DropletException(ERR_MSG_NO_PROFILE_FOUND));
				final String errorURL = this.getProfileErrorURL() + "&noprofile=true";
				this.setProfileErrorURL(errorURL);
			}
			if (isUserExists) {
				RepositoryItem profileItem = null;
				boolean success = false;
				profileItem = this.getProfileTools()
						.getItemFromEmail(this.getEmailAddress());
				success = this.getProfileTools().updateLoginAttemptCount(
						profileItem, true);
				if (success) {
					final String successURL = this.getProfileSuccessURL()
							+ "?updated=true";
					this.setProfileSuccessURL(successURL);
				} else {
					final String errorURL = this.getProfileErrorURL() + "&noprofile=true";
					this.setProfileErrorURL(errorURL);
				}
			}

		}
		return this.checkFormRedirect(this.getProfileSuccessURL(), this.getProfileErrorURL(),
				pRequest, pResponse);

	}
	
	
	public boolean handleSetTemporaryPassword(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug("handleSetTemporaryPassword method start");
		this.preSetTemporaryPassword(pRequest, pResponse);
		if (!this.getFormError()) {

			boolean isUserExists = false;
			isUserExists = this.isUserExists();
			if (!isUserExists) {
				this.addFormException(new DropletException(ERR_MSG_NO_PROFILE_FOUND));
				final String errorURL = this.getOrderDetailsErrorURL() + "&noprofile=true";
				this.setOrderDetailsErrorURL(errorURL);
			}
			if (isUserExists) {
				RepositoryItem profileItem = null;
				boolean success = false;
				profileItem = this.getProfileTools()
						.getItemFromEmail(this.getEmailAddress());
				try {
					success = setTemporaryPasswdForProfile(profileItem,getTempPassword());
				} catch (final RepositoryException e) {
					this.logError("RepositoryException in handleSetTemporaryPassword",
							e);
					this.addFormException(new DropletException(
							ERR_MSG_WHILE_UPDATING_TEMP_PASSWORD));
				}catch (final BBBBusinessException e) {
					this.logError("BBBBusinessException in handleSetTemporaryPassword",
							e);
					this.addFormException(new DropletException(
							ERR_MSG_INVALID_PASSWORD));
				}
				
				if (success) {
					final String successURL = this.getOrderDetailsSuccessURL()
							+ "?updated=true";
					this.setOrderDetailsSuccessURL(successURL);
				} else {
					final String errorURL = this.getOrderDetailsErrorURL() + "&noprofile=true";
					this.setOrderDetailsErrorURL(errorURL);
				}
			}

		}
		return this.checkFormRedirect(this.getOrderDetailsSuccessURL(), this.getOrderDetailsErrorURL(),
				pRequest, pResponse);

	}

	public boolean setTemporaryPasswdForProfile(RepositoryItem pProfile,String newPassword)
	        throws RepositoryException, BBBBusinessException
	    {
		logDebug("CSRFormHandler.setTemporaryPasswdForProfile: Start");
	    boolean resetPwd = this.getProfileTools().generateTempPasswordForProfile(pProfile,newPassword);
       	logDebug("CSRFormHandler.setTemporaryPasswdForProfile: End");
        return resetPwd;
    }
	
	
	/**
	 * Get Bopus Disabled stores
	 * @return
	 */
	public boolean handleViewBopusDisalbedStores(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
		IOException{

		this.logDebug("handleViewBopusDisalbedStores method start");

		try {

			this.validatePassword();

			if (!this.getFormError()) {
				final List<String> bopusDisabledStoreIDs = this.getCatalogTools(). getBopusDisabledStores();

				final List<StoreInfoBean> storeInfoBeans = new ArrayList<StoreInfoBean>();
				for(final String storeID :bopusDisabledStoreIDs){


					final StoreVO storeVO = this.getCatalogTools().getStoreDetails(storeID);

					final StoreInfoBean storeInfoBean = this.getStoreDetails(storeVO);

					storeInfoBeans.add(storeInfoBean);
				}
				if(storeInfoBeans.size()>0){
					this.logDebug("total store founds size="+storeInfoBeans.size());
					this.setShowDisabledStores("true");
					final String successURL = this.getStoreSuccessURL() + "?showData=true";
					this.setStoreSuccessURL(successURL);
					this.setStoreInfoBeanList(storeInfoBeans);

				} else{
					this.addFormException(new DropletException(
							ERR_MSG_NO_STORE_FOUND));
				}

			}

		} catch (final BBBBusinessException e) {
			this.logError("BBBBusinessException in handleViewBopusDisalbedStores",
					e);
			this.addFormException(new DropletException(
					ERR_MSG_NO_STORE_FOUND));
		} catch (final BBBSystemException e) {
			this.logError("BBBSystemException in handleViewBopusDisalbedStores", e);
			this.addFormException(new DropletException(
					ERR_MSG_NO_STORE_FOUND));
		}

		this.logDebug("handleViewBopusDisalbedStores method end");

		return this.checkFormRedirect(this.getStoreSuccessURL(), this.getStoreErrorURL(),
				pRequest, pResponse);
	}

	/**
	 * View disabled states
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */

	public boolean handleViewBopusDisalbedStates(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
		IOException{

		this.logDebug("handleViewBopusDisalbedStates method start");

		try {
			this.validatePassword();

			if (!this.getFormError()) {
				final List<String> bopusDisabledStates = this.getCatalogTools().getBopusDisabledStates();
				this.setBopusDisabledStates(bopusDisabledStates);

				if((bopusDisabledStates!=null) && (bopusDisabledStates.size()>0)){
					this.logDebug("total store founds size="+bopusDisabledStates.size());
					final String successURL = this.getStoreSuccessURL() + "?showStatesData=true";
					this.setStoreSuccessURL(successURL);

				} else{
					this.addFormException(new DropletException(
							ERR_MSG_NO_BOPUS_DISABLED_STATE));
				}
			}

		} catch (final BBBBusinessException e) {
			this.logError("BBBBusinessException in handleViewBopusDisalbedStates",
					e);
			this.addFormException(new DropletException(
					ERR_MSG_NO_STATE_FOUND));
		} catch (final BBBSystemException e) {
			this.logError("BBBSystemException in handleViewBopusDisalbedStates", e);
			this.addFormException(new DropletException(
					ERR_MSG_NO_STATE_FOUND));
		}

		this.logDebug("handleViewBopusDisalbedStates method end");

		return this.checkFormRedirect(this.getStoreSuccessURL(), this.getStoreErrorURL(),
				pRequest, pResponse);
	}


	/**
	 * Handle View Store Details
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleViewStoreDetails(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		this.logDebug("handleViewStoreDetails method start");
		this.validateBopusRequestParam(false);

		if (!this.getFormError()) {

			boolean isStorePresent = false;
			isStorePresent = this.isStorePresent();
			if (isStorePresent) {
				final String successURL = this.getStoreSuccessURL() + "?showData=true";
				this.setStoreSuccessURL(successURL);
			} else {
				this.addFormException(new DropletException(ERR_MSG_NO_STORE_FOUND));
				final String errorURL = this.getStoreErrorURL() + "&nostore=true";
				this.setStoreErrorURL(errorURL);
			}
			if (isStorePresent) {
				try {
					final StoreVO storeVO = this.getCatalogTools().getStoreDetails(
							this.getStoreId());

					final StoreInfoBean storeInfoBean = this.getStoreDetails(storeVO);

					this.getStoreInfoBeanList().add(storeInfoBean);

				} catch (final BBBBusinessException e) {
					this.logError("BBBBusinessException in handleViewStoreDetails",
							e);
					this.addFormException(new DropletException(
							ERR_MSG_NO_STORE_FOUND));
				} catch (final BBBSystemException e) {
					this.logError("BBBSystemException in handleViewStoreDetails", e);
					this.addFormException(new DropletException(
							ERR_MSG_NO_STORE_FOUND));
				}
			}

		}
		return this.checkFormRedirect(this.getStoreSuccessURL(), this.getStoreErrorURL(),
				pRequest, pResponse);
	}

	private StoreInfoBean getStoreDetails(final StoreVO storeVO){

		final StoreInfoBean storeInfoBean = new StoreInfoBean();
		storeInfoBean.setStoreId(storeVO.getStoreId());
		storeInfoBean.setStoreCity(storeVO.getCity());
		storeInfoBean.setStoreState(storeVO.getState());
		storeInfoBean.setStoreZipCode(storeVO.getPostalCode());
		storeInfoBean.setStoreBopusFlagBaby(
				BBBCoreConstants.TRUE);
		storeInfoBean.setStoreBopusFlagUS(
				BBBCoreConstants.TRUE);

		List<StoreBopusInfoVO> storeBopusInfoVO = null;
		storeBopusInfoVO = storeVO.getStoreBopusInfoVO();
		if (storeBopusInfoVO != null) {
			for (final StoreBopusInfoVO storeBopusInfo : storeBopusInfoVO) {
				final String siteId = storeBopusInfo.getSiteId();
				final boolean bopusFlag = storeBopusInfo.getBopusFlag();
				if ((BBBCoreConstants.SITE_BBB)
						.equalsIgnoreCase(siteId) || (TBSConstants.SITE_TBS_BBB).equalsIgnoreCase(siteId)) {
					if (bopusFlag) {
						storeInfoBean.setStoreBopusFlagBaby(BBBCoreConstants.TRUE);
					} else {
						storeInfoBean.setStoreBopusFlagBaby(BBBCoreConstants.FALSE);
					}

				}
				if ((BBBCoreConstants.SITE_BAB_US)
						.equalsIgnoreCase(siteId) || (TBSConstants.SITE_TBS_BAB_US)
						.equalsIgnoreCase(siteId)) {
					if (bopusFlag) {
						storeInfoBean.setStoreBopusFlagUS(BBBCoreConstants.TRUE);
					} else {
						storeInfoBean.setStoreBopusFlagUS(BBBCoreConstants.FALSE);
					}
				}
			}
		}

		return storeInfoBean;

	}

	public void validateBopusRequestParam(final boolean bopusFlagSelection) {
		this.logDebug("validateBopusRequestParam method start");
		this.validatePassword();
		if (BBBUtility.isEmpty(this.getStoreId())) {
			this.addFormException(new DropletException(ERR_MSG_REQUIRED_STORE_ID));
		}
		if (!BBBUtility.isEmpty(this.getStoreId())
				&& !BBBUtility.isNumericOnly(this.getStoreId())) {
			this.addFormException(new DropletException(ERR_MSG_NOT_VALID_STORE_ID));
		}
		if (bopusFlagSelection) {
			if (BBBUtility.isEmpty(this.getBopusFlag())) {
				this.addFormException(new DropletException(
						ERR_MSG_REQUIRED_BOPUS_FLAG));
			}
		}
		this.logDebug("validateBopusRequestParam method end");
	}

	public boolean handleEnableBopus(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		this.logDebug("handleEnableBopus method start");
		this.validateBopusRequestParam(true);
		boolean isStorePresent = false;
		boolean success = false;

		if (!this.getFormError()) {
			isStorePresent = this.isStorePresent();
			if (isStorePresent) {
				success = this.updateBopusStatus(true);
			} else {
				this.addFormException(new DropletException(ERR_MSG_NO_STORE_FOUND));
				final String errorURL = this.getStoreErrorURL() + "&nostore=true";
				this.setStoreErrorURL(errorURL);
			}
			if (success) {
				final String successURL = this.getStoreSuccessURL() + "?updated=true";
				this.setStoreSuccessURL(successURL);
			}

		}
		this.logDebug("handleEnableBopus method end");
		return this.checkFormRedirect(this.getStoreSuccessURL(), this.getStoreErrorURL(),
				pRequest, pResponse);
	}

	public boolean handleDisableBopus(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		this.logDebug("handleDisableBopus method start");
		this.validateBopusRequestParam(true);
		boolean isStorePresent = false;
		boolean success = false;

		if (!this.getFormError()) {
			isStorePresent = this.isStorePresent();
			if (isStorePresent) {
				success = this.updateBopusStatus(false);
			} else {
				this.addFormException(new DropletException(ERR_MSG_NO_STORE_FOUND));
				final String errorURL = this.getStoreErrorURL() + "&nostore=true";
				this.setStoreErrorURL(errorURL);
			}
			if (success) {
				final String successURL = this.getStoreSuccessURL() + "?updated=true";
				this.setStoreSuccessURL(successURL);
			}

		}
		this.logDebug("handleDisableBopus method end");
		return this.checkFormRedirect(this.getStoreSuccessURL(), this.getStoreErrorURL(),
				pRequest, pResponse);
	}

	/**
	 * Validate if a store is present in database
	 * @param pStoreId
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean isStorePresent(final String pStoreId) throws ServletException, IOException {
		try {
			final MutableRepositoryItem storeRepositoryItem = (MutableRepositoryItem) this.getStoreRepository()
					.getItem(pStoreId, BBBCatalogConstants.STORE_ITEM_DESCRIPTOR);
			if (storeRepositoryItem != null) {
				return true;
			}
		} catch (final RepositoryException e) {
			this.logError("RepositoryException in updateBopusState", e);
			return false;
		}

		return false;

	}


	public boolean isStorePresent() throws ServletException, IOException {
		try {
			final MutableRepositoryItem storeRepositoryItem = (MutableRepositoryItem) this.getStoreRepository()
					.getItem(this.storeId, BBBCatalogConstants.STORE_ITEM_DESCRIPTOR);
			if (storeRepositoryItem != null) {
				return true;
			}
		} catch (final RepositoryException e) {
			this.logError("RepositoryException in updateBopusState", e);
			return false;
		}

		return false;

	}

	/**
	 * Validate all store Ids
	 * @param pStores
	 */
	private void valdateStoreIDs(final String[] pStores) {

		if (pStores == null) {
			this.addFormException(new DropletException(ERR_MSG_INVALID_STORE_IDS));
			return;
		}
		for (final String store : pStores) {
			if (BBBUtility.isEmpty(store)) {
				this.addFormException(new DropletException(ERR_MSG_REQUIRED_STORE_ID));
				return;
			}
			if (BBBUtility.isNotEmpty(store) && !BBBUtility.isNumericOnly(store)) {
				this.addFormException(new DropletException(ERR_MSG_NOT_VALID_STORE_ID));
				return;
			}
		}
	}

	/**
	 * Disable multiple stores
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleDisableBopusStores(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		this.logDebug("handleDisableBopusStores method start");
		this.validatePassword();

		boolean isStorePresent = false;
		boolean success = false;

		String[] stores = null;

		if( !StringUtils.isEmpty(this.getStoresList())){
			stores = this.getStoresList().split(",");

		} else{
			this.addFormException(new DropletException(ERR_MSG_INVALID_STORE_IDS ));
		}

		this.valdateStoreIDs(stores);


		if (!this.getFormError() && (stores !=null)) {

			boolean allSuccess = true;
			final StringBuilder notFoundStores = new StringBuilder("");
			final StringBuilder storesFailedUpdate = new StringBuilder("");
			final StringBuilder storesUpdateSuccess = new StringBuilder("");

			for(final String pStoreID: stores){

				success = true;

				isStorePresent = this.isStorePresent(pStoreID);
				if (isStorePresent) {
					//disable a store
					success = this.updateBopusStatus(pStoreID, false);

				} else {
					//store not present
					allSuccess = false;
					notFoundStores.append(pStoreID).append(";");
				}

				//update fail handling
				if (!success) {
					allSuccess = false;
					storesFailedUpdate.append(pStoreID).append(";");
				} else{
					//success stores
					storesUpdateSuccess.append(pStoreID).append(";");
				}

			}

			if(!allSuccess){
				if(notFoundStores.length()>0){
					if(storesUpdateSuccess.length()>0){
						this.addFormException(new DropletException("Stores are updated , however, "+ERR_MSG_FOLLOWING_STORE_NOT_FOUND + notFoundStores.toString() ));
					}else{
						this.addFormException(new DropletException(ERR_MSG_FOLLOWING_STORE_NOT_FOUND + notFoundStores.toString() ));
					}
				}
				if(storesFailedUpdate.length()>0){
					if(storesUpdateSuccess.length()>0){
						this.addFormException(new DropletException("Stores are updated , however, "+ERR_MSG_FOLLOWING_STORE_UPDATE_FAIL + notFoundStores.toString() ));
					}else{
						this.addFormException(new DropletException(ERR_MSG_FOLLOWING_STORE_UPDATE_FAIL + storesFailedUpdate.toString()));
					}
				}
			} else{
				final String successURL = this.getStoreSuccessURL() + "?updated=true";
				this.setStoreSuccessURL(successURL);
			}

		}
		this.logDebug("handleDisableBopusStores method end");
		return this.checkFormRedirect(this.getStoreSuccessURL(), this.getStoreErrorURL(),
				pRequest, pResponse);
	}

	/**
	 * Enable multiple stores
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean handleUpdateStoreBopusStatus(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException{

		this.logDebug("handleUpdateStoreBopusStatus method starts");

		boolean success = false;
		if (!this.getFormError()) {

			final String[] storesToEnables = this.getSelectedStoreIdsToEnable();

			if( storesToEnables!=null){
				for(final String updateStoreID:storesToEnables){

					if(updateStoreID !=null ){

						this.logDebug("handleUpdateStoreBopusStatus : enable bopus for storeID=="+updateStoreID);
						success = this.updateBopusStatus( updateStoreID, true);
						this.logDebug("handleUpdateStoreBopusStatus : update bopus for storeID sucess ? ="+success);
					}
				}
			}

			if (success) {
				final String successURL = this.getStoreSuccessURL() + "?updated=true";
				this.setStoreSuccessURL(successURL);
			}

		}
		this.logDebug("handleUpdateStoreBopusStatus method ends");
		return this.checkFormRedirect(this.getStoreSuccessURL(), this.getStoreErrorURL(),
				pRequest, pResponse);


	}

	public boolean handleViewOrderDetails(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug("handleViewOrderDetails method start");
		this.preViewOrderDetails(pRequest, pResponse);
		if (!this.getFormError()) {

			boolean isUserExists = false;
			isUserExists = this.isUserExists();
			if (isUserExists) {
				RepositoryItem profileItem = null;
				profileItem = this.getProfileTools()
						.getItemFromEmail(this.getEmailAddress());
				final String profileId = profileItem.getRepositoryId();
				final String firstName = (String) profileItem
						.getPropertyValue(FIRST_NAME);
				final String lastName = (String) profileItem
						.getPropertyValue(LAST_NAME);
				
				final String successURL = this.getOrderDetailsSuccessURL() + "?showData=true&email="+this.getEmailAddress() + "&fName=" + firstName + "&lstName=" + lastName ;
				this.setOrderDetailsSuccessURL(successURL);
				
			} else {
				this.addFormException(new DropletException(ERR_MSG_NO_PROFILE_FOUND));
				final String errorURL = this.getOrderDetailsErrorURL() + "&noprofile=true";
				this.setOrderDetailsErrorURL(errorURL);
			}
		}
		return this.checkFormRedirect(this.getOrderDetailsSuccessURL(), this.getOrderDetailsErrorURL(),
				pRequest, pResponse);

	}
	
	public void validateAdminUserSession(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws BBBSystemException, BBBBusinessException {
		this.logDebug("validateAdminUserSession method start");
		Object isActiveSession = pRequest.getSession().getAttribute("UserAuthenticated");
		if(isActiveSession == null) {
			this.addFormException(new DropletException(
					ERR_MSG_SESSION_EXPIRED));
		}
		this.logDebug("validateAdminUserSession method end");
	}	
	
	/**
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean handleValidateUser(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException {
		this.logDebug("handleValidateUser method start. ");
		this.validateAdminUser();
		if (!this.getFormError()) {
			String catalogId = "";
			try {
				catalogId = ((atg.repository.RepositoryItem)getSiteContextManager().getSite(getCurrentSite()).getPropertyValue("defaultCatalog")).getRepositoryId();
			} catch (SiteContextException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setChangeRankSuccessURL(this.getChangeRankSuccessURL()+"?catalogId="+catalogId+"&site="+getListOfSites().get(getCurrentSite()));
			
			pRequest.getSession().setAttribute("UserAuthenticated","true");
		}
		this.logDebug("handleValidateUser method End. ");
		return this.checkFormRedirect(this.getChangeRankSuccessURL(),
				this.getChangeRankFailureURL(), pRequest, pResponse);
	}

	
	/**
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean handleChangeRank(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException {

		this.logDebug("handleChangeRank method Start. ");
		String cacheClearMsg = "";
		String parentCategoryId = pRequest.getParameter("categoryId");
		String catalogId = pRequest.getParameter("catalogId");
		validateAdminUserSession(pRequest, pResponse);
		
		if (!this.getFormError()) {
			String[] rankArray = getRank().split(",");
			String[] categoryArray = getCategory().split(",");
			RepositoryItem repItem = null;
			int index = 0;
			for(String category:categoryArray) {
				createORUpdateRelRank(repItem, category, rankArray[index]);
				index++;
			}
		}
		this.logDebug("handleChangeRank method End. ");
		return this.checkFormRedirect(this.getChangeRankSuccessURL(),
				this.getChangeRankFailureURL(), pRequest, pResponse);
	}


	private void createORUpdateRelRank(RepositoryItem repItem, final String category, final String rank) {
		RepositoryWrapperImpl repWrapper = null;
		RepositoryItemWrapper repItemWrapper = null;
		repWrapper = new RepositoryWrapperImpl(getCatRelRankRepository());
		repItemWrapper = repWrapper.getItem(category, "dimOrder");
		if(repItemWrapper == null) {
			repItemWrapper = repWrapper.createItem("dimOrder",
				new ICreateRepositoryItemCallback() {
				@Override
				public void setCreatedItemProperties(final MutableRepositoryItem createdItem) {
					createdItem.setPropertyValue("categoryId", category);
					createdItem.setPropertyValue("rank", rank);
				}});
		}
		else {
			//repItemWrapper = new RepositoryItemWrapper(repItem);
			repItemWrapper.setProperty("rank", rank);
		}
	}

	/**
	 * update bopus status for a store
	 * @param updateStoreID
	 * @param flag
	 * @return
	 */
	private boolean updateBopusStatus(final String updateStoreID, final boolean flag){

		try {
			final MutableRepository repository = this.getStoreRepository();
			final MutableRepositoryItem storeRepositoryItem = (MutableRepositoryItem) this.getStoreRepository()
					.getItem(updateStoreID, BBBCatalogConstants.STORE_ITEM_DESCRIPTOR);

			@SuppressWarnings("unchecked")
			final Map<String, Boolean> bopusSiteMap = (Map<String, Boolean>) storeRepositoryItem.getPropertyValue(BBBCatalogConstants.BOPUS_STORE_PROPERTY_NAME);

			final Timestamp bopusLastModifiedDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
			final Map<String, Boolean> updatedBopusSiteMap = new HashMap<String, Boolean>();
			final Map<String, Timestamp> updatedBopusLastModifiedDate = new HashMap<String, Timestamp>();
			if ((bopusSiteMap != null) && !bopusSiteMap.isEmpty()) {
				final Set<String> siteIdSet = bopusSiteMap.keySet();
				for (final String siteId : siteIdSet) {
					updatedBopusSiteMap.put(siteId, bopusSiteMap.get(siteId));
					updatedBopusLastModifiedDate.put(siteId, bopusLastModifiedDate);
				}

			}


			updatedBopusSiteMap.put(BBBCoreConstants.SITE_BBB, flag);
			updatedBopusSiteMap.put(BBBCoreConstants.SITE_BAB_US, flag);
			updatedBopusSiteMap.put(BBBCoreConstants.SITE_BAB_CA, flag);
			updatedBopusSiteMap.put(TBSConstants.SITE_TBS_BAB_US, flag);
			updatedBopusSiteMap.put(TBSConstants.SITE_TBS_BAB_CA, flag);
			updatedBopusSiteMap.put(TBSConstants.SITE_TBS_BBB, flag);

			updatedBopusLastModifiedDate.put(BBBCoreConstants.SITE_BBB, bopusLastModifiedDate);
			updatedBopusLastModifiedDate.put(BBBCoreConstants.SITE_BAB_US, bopusLastModifiedDate);
			updatedBopusLastModifiedDate.put(BBBCoreConstants.SITE_BAB_CA, bopusLastModifiedDate);
			updatedBopusLastModifiedDate.put(TBSConstants.SITE_TBS_BAB_US, bopusLastModifiedDate);
			updatedBopusLastModifiedDate.put(TBSConstants.SITE_TBS_BAB_CA, bopusLastModifiedDate);
			updatedBopusLastModifiedDate.put(TBSConstants.SITE_TBS_BBB, bopusLastModifiedDate);

			storeRepositoryItem.setPropertyValue(BBBCatalogConstants.BOPUS_STORE_PROPERTY_NAME,updatedBopusSiteMap);
			storeRepositoryItem.setPropertyValue("bopusFlagLastModifiedDate",updatedBopusLastModifiedDate);

			repository.updateItem(storeRepositoryItem);
		} catch (final RepositoryException e) {
			this.addFormException(new DropletException(ERR_IN_UPDATE_BOPUS_FLAG_FOR_STORE +updateStoreID));
			this.logError("RepositoryException in updateBopusState", e);
			return false;
		}

		return true;

	}

	public boolean updateBopusStatus(final boolean flag) throws ServletException,
			IOException {
		try {
			final MutableRepository repository = this.getStoreRepository();
			final MutableRepositoryItem storeRepositoryItem = (MutableRepositoryItem) this.getStoreRepository()
					.getItem(this.storeId, BBBCatalogConstants.STORE_ITEM_DESCRIPTOR);
			@SuppressWarnings("unchecked")
			final Map<String, Boolean> bopusSiteMap = (Map<String, Boolean>) storeRepositoryItem.getPropertyValue(BBBCatalogConstants.BOPUS_STORE_PROPERTY_NAME);

			final Timestamp bopusLastModifiedDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
			
			final Map<String, Boolean> updatedBopusSiteMap = new HashMap<String, Boolean>();
			final Map<String, Timestamp> updatedBopusLastModifiedDate = new HashMap<String, Timestamp>();
			
			if ((bopusSiteMap != null) && !bopusSiteMap.isEmpty()) {
				final Set<String> siteIdSet = bopusSiteMap.keySet();
				for (final String siteId : siteIdSet) {
					updatedBopusSiteMap.put(siteId, bopusSiteMap.get(siteId));
					updatedBopusLastModifiedDate.put(siteId, bopusLastModifiedDate);
				}

			}
/*			if (getBopusFlag().equalsIgnoreCase(BOPUS_FLAG_BABY)) {
				updatedBopusSiteMap.put(BBBCoreConstants.SITE_BBB, flag);
			} else if (getBopusFlag().equalsIgnoreCase(BOPUS_FLAG_US)) {
				updatedBopusSiteMap.put(BBBCoreConstants.SITE_BAB_US, flag);
			} else {*/

			updatedBopusSiteMap.put(BBBCoreConstants.SITE_BBB, flag);
			updatedBopusSiteMap.put(BBBCoreConstants.SITE_BAB_US, flag);
			updatedBopusSiteMap.put(BBBCoreConstants.SITE_BAB_CA, flag);
			updatedBopusSiteMap.put(TBSConstants.SITE_TBS_BAB_US, flag);
			updatedBopusSiteMap.put(TBSConstants.SITE_TBS_BAB_CA, flag);
			updatedBopusSiteMap.put(TBSConstants.SITE_TBS_BBB, flag);
			
			updatedBopusLastModifiedDate.put(BBBCoreConstants.SITE_BBB, bopusLastModifiedDate);
			updatedBopusLastModifiedDate.put(BBBCoreConstants.SITE_BAB_US, bopusLastModifiedDate);
			updatedBopusLastModifiedDate.put(BBBCoreConstants.SITE_BAB_CA, bopusLastModifiedDate);
			updatedBopusLastModifiedDate.put(TBSConstants.SITE_TBS_BAB_US, bopusLastModifiedDate);
			updatedBopusLastModifiedDate.put(TBSConstants.SITE_TBS_BAB_CA, bopusLastModifiedDate);
			updatedBopusLastModifiedDate.put(TBSConstants.SITE_TBS_BBB, bopusLastModifiedDate);

			/*}*/

			storeRepositoryItem.setPropertyValue(BBBCatalogConstants.BOPUS_STORE_PROPERTY_NAME,updatedBopusSiteMap);
			storeRepositoryItem.setPropertyValue("bopusFlagLastModifiedDate",updatedBopusLastModifiedDate);
			
			repository.updateItem(storeRepositoryItem);
		} catch (final RepositoryException e) {
			this.addFormException(new DropletException(ERR_IN_UPDATE_BOPUS_FLAG));
			this.logError("RepositoryException in updateBopusState", e);
			return false;
		}

		return true;

	}

	private String getCurrentSiteId() {
		if(BBBUtility.isNotEmpty(this.getSiteName())){
			return this.getSiteName();
		}
		final String siteId = this.getSiteContext().getSite().getId();
		return siteId;
	}

	private RegistryReqVO createRegistryReqVO() throws BBBSystemException,
			BBBBusinessException {
		final RegistryReqVO regReqVO = new RegistryReqVO();
		regReqVO.setRegistryId(this.getRegistryId());
		regReqVO.setSiteId(this.getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
				this.getCurrentSiteId()).get(0));
		regReqVO.setUserToken(this.getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY,
				BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
		regReqVO.setServiceName(this.getRegistryInfoServiceName());
		return regReqVO;
	}

	private LinkRegistryToProfileVO createLinkRegistryReqVO()
			throws BBBSystemException, BBBBusinessException {
		final LinkRegistryToProfileVO regReqVO = new LinkRegistryToProfileVO();
		regReqVO.setRegistryId(Long.valueOf(this.getRegistryId()));
		regReqVO.setSiteId(this.getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
				this.getCurrentSiteId()).get(0));
		regReqVO.setUserToken(this.getCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY,
				BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
		regReqVO.setServiceName(this.getLinkRegistryServiceName());
		regReqVO.setEmailId(this.getEmailAddress());
		if (this.getRegCoreg().equalsIgnoreCase(CO_REGISTRANT)) {
			regReqVO.setRegCoreg("CO");
		} else // if(getRegCoreg().equalsIgnoreCase("registrant"))
		{
			regReqVO.setRegCoreg("RE");
		}

		return regReqVO;
	}

	private RegistrySummaryVO getRegistrySummaryVO(final RegistryResVO registryResVO)
			throws BBBSystemException, BBBBusinessException {
		RegistrySummaryVO registrySummaryVO = new RegistrySummaryVO();
		if (registryResVO != null) {
			if ((registryResVO.getServiceErrorVO() != null)
					&& registryResVO.getServiceErrorVO().isErrorExists()) {

				if (!BBBUtility.isEmpty(registryResVO.getServiceErrorVO()
						.getErrorDisplayMessage())
						&& (registryResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR)) {
					this.logError("Fatal error Error Id is:"
							+ registryResVO.getServiceErrorVO().getErrorId());
				}
				if (!BBBUtility.isEmpty(registryResVO.getServiceErrorVO()
						.getErrorDisplayMessage())
						&& (registryResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN)) {
					this.logError("Either user token or site flag invalid"
							+ registryResVO.getServiceErrorVO().getErrorId());
				}
				if (!BBBUtility.isEmpty(registryResVO.getServiceErrorVO()
						.getErrorDisplayMessage())
						&& (registryResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT)) {
					this.logError("input fields format error"
							+ registryResVO.getServiceErrorVO().getErrorId());
				}
			}

			registrySummaryVO = registryResVO.getRegistrySummaryVO();
		}
		return registrySummaryVO;

	}

	public String getRegCoreg() {
		return this.regCoreg;
	}

	public void setRegCoreg(final String regCoreg) {
		this.regCoreg = regCoreg;
	}

	public String getCsrName() {
		return this.csrName;
	}

	public void setCsrName(final String csrName) {
		this.csrName = csrName;
	}

	public String getLinkRegistryServiceName() {
		return this.mLinkRegistryServiceName;
	}

	public void setLinkRegistryServiceName(final String pLinkRegistryServiceName) {
		this.mLinkRegistryServiceName = pLinkRegistryServiceName;
	}

	private Date stringToDateConverter(final String pDate, final String siteId)
			throws BBBSystemException {
		this.logDebug("CSRFormHandler.StringToDateConverter() method Start");

		SimpleDateFormat dateFormat = null;

		if (siteId == null) {
			return null;
		}

		if (siteId.equalsIgnoreCase(BBBGiftRegistryConstants.CANADA_SITE_ID)) {
			dateFormat = new SimpleDateFormat(BBBGiftRegistryConstants.DATE_FORMAT_CANADA);
		} else {
			dateFormat = new SimpleDateFormat(BBBGiftRegistryConstants.DATE_FORMAT_DB);
		}

		Date date = null;

		try {
			date = dateFormat.parse(pDate);
		} catch (final ParseException parseException) {
			throw new BBBSystemException(
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10166,
					parseException.getMessage(), parseException);
		}

		this.logDebug("CSRFormHandler.StringToDateConverter() method ends");
		return date;
	}

	private boolean isUserBelogToCurrentSite(final RepositoryItem pRepItem,
			final String pSiteId) {
		this.logDebug("CSRFormHandler.isUserBelogToCurrentSite() method start");
		if ((pRepItem != null) && (pSiteId != null)) {
			final Map<String, Object> userCurrSiteMap = (Map<String, Object>) pRepItem
					.getPropertyValue(USER_SITE_ITEMS);
			if (userCurrSiteMap != null) {
				if (userCurrSiteMap.containsKey(pSiteId)) {
					return true;
				}

			}
		}

		this.logDebug("CSRFormHandler.isUserBelogToCurrentSite() method End");
		return false;
	}

	public List<StoreInfoBean> getStoreInfoBeanList() {
		return this.mStoreInfoBeanList;
	}

	public void setStoreInfoBeanList(final List<StoreInfoBean> mStoreInfoBeanList) {
		this.mStoreInfoBeanList = mStoreInfoBeanList;
	}

	public List<String> getBopusDisabledStates() {
		return this.bopusDisabledStates;
	}

	public void setBopusDisabledStates(final List<String> bopusDisabledStates) {
		this.bopusDisabledStates = bopusDisabledStates;
	}

	public String getShowDisabledStores() {
		return this.showDisabledStores;
	}

	public void setShowDisabledStores(final String isShowDisabledStores) {
		this.showDisabledStores = isShowDisabledStores;
	}

	public List<Boolean> getStoresToEnableList() {
		return this.storesToEnableList;
	}

	public void setStoresToEnableList(final List<Boolean> storesToEnableList) {
		this.storesToEnableList = storesToEnableList;
	}

	public String getStoresList() {
		return this.storesList;
	}

	public void setStoresList(final String storesList) {
		this.storesList = storesList;
	}

	public String[] getSelectedStoreIdsToEnable() {
		return this.selectedStoreIdsToEnable;
	}

	public void setSelectedStoreIdsToEnable(final String[] selectedStoreIdsToEnable) {
		this.selectedStoreIdsToEnable = selectedStoreIdsToEnable;
	}

	public Map<String, String> getCoherenceCaches() {
		return coherenceCaches;
}

	public void setCoherenceCaches(Map<String, String> coherenceCaches) {
		this.coherenceCaches = coherenceCaches;
	}
	
	public Map<String, String> getCoherenceCacheMap() throws BBBSystemException, BBBBusinessException {
		setCoherenceCaches(this.getCatalogTools().getConfigValueByconfigType(COHERENCE_CACHE_CONFIG_KEY));
		return coherenceCaches;
	}
	
	public Map<String, String> getMobileCoherenceCaches() {
		return mobileCoherenceCaches;
	}

	public void setMobileCoherenceCaches(Map<String, String> mobileCoherenceCaches) {
		this.mobileCoherenceCaches = mobileCoherenceCaches;
	}
	
	
	public Map<String, String> getMobileCoherenceCacheMap() throws BBBSystemException, BBBBusinessException {
		setMobileCoherenceCaches(this.getCatalogTools().getConfigValueByconfigType(MOBILE_COHERENCE_CACHE_CONFIG_KEY));
		return mobileCoherenceCaches;
	}

	public BBBCacheInvalidatorSource getBbbCacheInvalidatorMessageSource() {
		return bbbCacheInvalidatorMessageSource;
	}

	public void setBbbCacheInvalidatorMessageSource(
			BBBCacheInvalidatorSource bbbCacheInvalidatorMessageSource) {
		this.bbbCacheInvalidatorMessageSource = bbbCacheInvalidatorMessageSource;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public String getCurrentSite() {
		return currentSite;
	}

	public void setCurrentSite(String currentSite) {
		this.currentSite = currentSite;
	}

	public Map<String, String> getListOfSites() {
		return listOfSites;
	}

	public void setListOfSites(Map<String, String> listOfSites) {
		this.listOfSites = listOfSites;
	}

	public String getChangeRankSuccessURL() {
		return changeRankSuccessURL;
	}

	public void setChangeRankSuccessURL(String changeRankSuccessURL) {
		this.changeRankSuccessURL = changeRankSuccessURL;
	}

	public String getChangeRankFailureURL() {
		return changeRankFailureURL;
	}

	public void setChangeRankFailureURL(String changeRankFailureURL) {
		this.changeRankFailureURL = changeRankFailureURL;
	}

	public SiteContextManager getSiteContextManager() {
		return siteContextManager;
	}

	public void setSiteContextManager(SiteContextManager siteContextManager) {
		this.siteContextManager = siteContextManager;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public MutableRepository getCatRelRankRepository() {
		return catRelRankRepository;
	}

	public void setCatRelRankRepository(MutableRepository catRelRankRepository) {
		this.catRelRankRepository = catRelRankRepository;
	}

	public String getCacheKeyForValue() {
		return cacheKeyForValue;
	}

	public void setCacheKeyForValue(String cacheKeyForValue) {
		this.cacheKeyForValue = cacheKeyForValue;
	}
	public String getSuccessURLForKeyValue() {
		return successURLForKeyValue;
	}

	public void setSuccessURLForKeyValue(String successURLForKeyValue) {
		this.successURLForKeyValue = successURLForKeyValue;
	}

	public String getFailureURLForKeyValue() {
		return failureURLForKeyValue;
	}

	public void setFailureURLForKeyValue(String failureURLForKeyValue) {
		this.failureURLForKeyValue = failureURLForKeyValue;
	}

	public String getJsonResult() {
		return jsonResult;
	}

	public void setJsonResult(String jsonResult) {
		this.jsonResult = jsonResult;
	}

	public String getMobileCacheKey() {
		return mobileCacheKey;
	}

	public void setMobileCacheKey(String mobileCacheKey) {
		this.mobileCacheKey = mobileCacheKey;
	}

	public String getJsonResultMobile() {
		return jsonResultMobile;
	}

	public void setJsonResultMobile(String jsonResultMobile) {
		this.jsonResultMobile = jsonResultMobile;
	}
	
}
