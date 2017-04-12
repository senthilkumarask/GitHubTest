package com.bbb.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbb.account.webservices.BBBProfileServices;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.goldengate.DCPrefixIdGenerator;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.security.PBKDF2PasswordHasher;
import com.bbb.social.facebook.FBProfileTools;
import com.bbb.wishlist.StoreGiftlistFormHandler;
import com.bbb.wishlist.manager.BBBGiftlistManager;

import atg.commerce.order.Order;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.commerce.profile.CommerceProfileFormHandler;
import atg.multisite.SiteContext;
import atg.multisite.SiteGroupManager;
import atg.nucleus.Nucleus;
import atg.payment.creditcard.ExtendableCreditCardTools;
import atg.repository.MutableRepository;
import atg.repository.RepositoryItem;
import atg.service.lockmanager.ClientLockManager;
import atg.userprofiling.CookieManager;
import atg.userprofiling.email.TemplateEmailInfo;

/**
 * 
 * @author Velmurugan Moorthy
 * 
 * Creating this class to hold variables, constants, getters & setters of BBBProfileFormHandler 
 * This is done to refactor the BBBProfileFormHandler class	
 * BBBJ-112
 *
 */

public class BBBProfileFormHandlerBean extends CommerceProfileFormHandler {

	// Constants - START
	private Map<String, String> errorMap;
    protected static final String COPY_ORDER_DETAILS = "copyOrderDetails";
	protected static final String OLD_PWD = "oldPwd";

	protected static final Integer FACEBOOK_SECURITY_STATUS = Integer.valueOf(4);

	//protected static final String FORCE_PASSWORD_CHANGE_REQUEST = "Force Password change Request";
    protected static final String ADD_ITEM_TO_GIFT_LIST = "BBBProfileFormHandler.addItemTOGiftList()";
    protected static final String ADD_ITEM_TO_WISH_LIST = "BBBProfileFormHandler.addItemTOWishList()";
    protected static final String ADDRESS_1 = "address1";
    protected static final String ADDRESS_2 = "address2";
    protected static final String ADDRESS_TO_SHIP = "addressToShip";
    protected static final String AUTO_SIGN_IN = "BBBProfileFormHandler.autoSignIn";
    protected static final String BBB_BUSINESS = "BBBBusiness";
    protected static final String SEND_REGISTRATION_EMAIL = "BBBProfileFormHandler.sendRegistrationEmail :: ";
    protected static final String BBB_SYSTEM = "BBBSystem";
    protected static final String BILLING_ADDRESS = "billingAddress";
    protected static final String BUSINESS_EXCEPTION_ERROR_IN_HANDLE_UPDATE = "BusinessException - Error in handleUpdate";
    protected static final String CITY = "city";
    protected static final String CLASS_NOT_FOUND = "ClassNotFound";
    protected static final String COMMERCE = "Commerce";
    protected static final String COMPANY_NAME = "companyName";
    protected static final String CONFIG_TYPE = "config type ";
    protected static final String CREDIT_CARD_NUMBER = "creditCardNumber";
    protected static final String CREDIT_CARD_TYPE = "creditCardType";
    protected static final String EMAIL_ADDRESS = "Email Address";
    protected static final String END = ": End";
    protected static final String END_TRANSACTION = "BBBProfileFormHandler.endTransaction";
    protected static final String ERROR_ID_IS = " : Error Id is:";
    protected static final String ERROR_OCCURED_WHILE_UPDATING_THE_LOGIN_ATTEMP_COUNT = "Error occurred while updating the login attempt count";
    protected static final String ERROR_RECEIVED_FORGET_PASSWORD_RESPONSE_VO = "Error received forgetPasswordResponseVO : ";
    protected static final String ERROR_RECLAIM_ACCOUNT = "reclaim_account_disp_error_reclaim_account";
    protected static final String EXCEPTION = "Exception - ";
    protected static final String EXCEPTION_BAZAAR_VOICE_KEY_NOT_FOUND_FOR_SITE = "Exception - Bazaar voice key not found for site";
    protected static final String EXCEPTION_BBB_PROFILE_FORM_HANDLER_END_TRANSACTION = "Exception - BBBProfileFormHandler.endTransaction()";
    protected static final String EXCEPTION_BBB_PROFILE_FORM_HANDLER_HANDLE_DEFAULT_BILLING_ADDRESS = "Exception - BBBProfileFormHandler.handleDefaultBillingAddress()";
    protected static final String EXCEPTION_CHECK_USER_TOKEN_BVRR = "Exception - BBBProfileFormHandler.checkUserTokenBVRR()";
    protected static final String EXCEPTION_ERROR_IN_HANDLE_UPDATE = "Exception - Error in handleUpdate";
    protected static final String EXCEPTION_GENERIC_REMOVE_ADDRESS = "Exception - BBBProfileFormHandler.genericRemoveAddress()";
    protected static final String EXCEPTION_GIFT_REGISTRY_MANAGER_UPDATE_PROFILE_REGISTRIES_STATUS = "Exception from mGiftRegistryManager.updateProfileRegistriesStatus method";
    protected static final String EXCEPTION_HANDLE_DEFAULT_SHIPPING_ADDRESS = "Exception - BBBProfileFormHandler.handleDefaultShippingAddress()";
    protected static final String EXCEPTION_HANDLE_EDIT_CARD = "Exception - BBBProfileFormHandler.handleEditCard()";
    protected static final String EXCEPTION_HANDLE_MAKE_DEFAULT_CREDIT_CARD = "Exception - BBBProfileFormHandler.handleMakeDefaultCreditCard()";
    protected static final String EXCEPTION_HANDLE_REGISTRATION = "Exception - BBBProfileFormHandler.handleRegistration()";
    protected static final String EXCEPTION_HANDLE_REMOVE_CARD = "Exception - BBBProfileFormHandler.handleRemoveCard()";
    protected static final String EXCEPTION_HANDLE_UPDATE_CARD = "Exception - BBBProfileFormHandler.handleUpdateCard()";
    protected static final String EXCEPTION_OCCURED_WHILE_FETCHING_CONFIG_VALUE_FOR_CONFIG_KEY = " Exception occurred while fetching config value for config key ";
    protected static final String EXCEPTION_POST_LOGIN_USER = "Exception - BBBProfileFormHandler.postLoginUser()";
    protected static final String EXCEPTION_PROFILE_REGISTRATION_TIBCO_EMAIL_TAG_KEY_NOT_FOUND = "Exception - ProfileRegistrationTibcoEmailTag Key not found";
    protected static final String EXCEPTION_UPDATE_CHECKOUT_DEFAULT_VALUES = "Exception - BBBProfileFormHandler.updateCheckoutDefaultValues()";
    protected static final String EXPIRATION_MONTH = "expirationMonth";
    protected static final String EXPIRATION_YEAR = "expirationYear";
    protected static final String FIELD_NAME = "fieldName";
    protected static final String FIRST_NAME = "firstName";
    protected static final String FIRST_NAME2 = "First Name";
    protected static final String FROM_POST_CREATE_USER = " from postCreateUser() of BBBProfileFormHandler";
    protected static final String GENERIC_REMOVE_ADDRESS_METHOD = "BBBProfileFormHandler.genericRemoveAddress() method";
    protected static final String HANDLE_CHECKOUT_DEFAULTS_METHOD = "BBBProfileFormHandler.handleCheckoutDefaults() method";
    protected static final String HANDLE_CREATE_NEW_CREDIT_CARD_AND_ADDRESS_METHOD = "BBBProfileFormHandler.handleCreateNewCreditCardAndAddress() method";
    protected static final String HANDLE_DEFAULT_BILLING_ADDRESS_METHOD = "BBBProfileFormHandler.handleDefaultBillingAddress() method";
    protected static final String HANDLE_DEFAULT_SHIPPING_ADDRESS_METHOD = "BBBProfileFormHandler.handleDefaultShippingAddress() method";
    protected static final String HANDLE_EXTEND_ACCOUNT = "BBBProfileFormHandler.handleExtendAccount";
    protected static final String HANDLE_FB_LOGIN = "FBConnectFormHandler.handleFbLogin()";
    protected static final String HANDLE_LEGACY_FORGET_PASSWORD = "BBBProfileFormHandler | handleLegacyForgetPassword : ";
    protected static final String HANDLE_MAKE_DEFAULT_BILLING_ADDRESS_METHOD = "BBBProfileFormHandler.handleMakeDefaultBillingAddress() method";
    protected static final String HANDLE_REMOVE_ADDRESS_METHOD = "BBBProfileFormHandler.handleRemoveAddress() method";
    protected static final String HANDLE_UPDATE_ADDRESS_METHOD = "BBBProfileFormHandler.handleUpdateAddress() method";
    protected static final String HANDLE_VALIDATE_AND_EXTEND_ACCOUNT = "BBBProfileFormHandler.handleValidateAndExtendAccount";
    protected static final String ILLEGAL_ACCESS = "IllegalAccess";
    protected static final String INSTANTIATION = "Instantiation";
    protected static final String INTROSPECTION = "Introspection";
    protected static final String LAST_NAME = "lastName";
    protected static final String LAST_NAME2 = "Last Name";
    protected static final String MAKE_BILLING = "makeBilling";
    protected static final String MIDDLE_NAME = "middleName";
    protected static final String NAME_ON_CARD = "nameOnCard";
    protected static final String NEW_BILLING_ADDRESS = "newBillingAddress";
    protected static final String NEW_NICKNAME = "newNickname";
    protected static final String NICKNAME = "nickname";
    protected static final String OWNER_ID = "ownerId";
    protected static final String PASSWORD_ERROR = "passwordError";
    protected static final String POSTAL_CODE = "postalCode";
    protected static final String PROFILE_ID = "p";
    protected static final String PROFILE_REGISTRATION_TIBCO_EMAIL_TAG = "ProfileRegistrationTibcoEmailTag";
    protected static final String PROPERTY_NOT_FOUND = "PropertyNotFound";
    protected static final String RECLAIM_ACCOUNT_DISP_ERROR_FORGET_PASSWORD = "reclaim_account_disp_error_forget_password";
    protected static final String RECLAIM_ACCOUNT_INVALID_EMAIL = "reclaim_account_invalid_email";
    protected static final String RECLAIM_ACCOUNT_NOT_FOUND = "reclaim_account_not_found";
    protected static final String RECLAIM_ACCOUNT_RESPONSE_VO = "reclaimAccountResponseVO";
    protected static final String RECLAIM_ACCOUNT_TECH_ERROR_FORGET_PASSWORD = "reclaim_account_tech_error_forget_password";
    protected static final String RECLAIM_ACCOUNT_TECH_ERROR_RECLAIM_ACCOUNT = "reclaim_account_tech_error_reclaim_account";
    protected static final String RECLAIM_ACCOUNT_UNABLETO_RECLAIM = "reclaim_account_unableto_reclaim";
    protected static final String REPOSITORY = "Repository";
    protected static final String SITE_ID = "s";
    protected static final String START = ": Start";
    protected static final String STATE = "state";
    protected static final String TRANSACTION_DEMARCATION = "TransactionDemarcation";
    protected static final String TRANSACTION_DEMARCATION_ERROR_IN_COMMITING_TRANSACTION = "Transaction Demarcation error in committing transaction";
    protected static final String TRUE = "true";
    protected static final String UPDATE_DEFAULT_BILLING_ADDRESS_METHOD = "BBBProfileFormHandler.updateDefaultBillingAddress() method";
    protected static final String WHILE_ADDING_ITEM_TO_THE_GIFT_REGISTRY = "while adding item to the gift registry";
    protected static final String EXCEPTION_RESET_PASSWORD_TOKEN="Exception in Reset PAssword token";

    //ship to po
    protected static final String QAS_POBOXADDRESS="poBoxAddress";
    protected static final String QAS_VALIDATED="qasValidated";

    //    R 2.2 Start : Added for Cookie Auto Login for Mobile App
   // protected static final String HANDLE_COOKIE_LOGIN = "CookieFormHandler.handleCookieLogin()";
    protected static final String ERROR_OCCURED_WHILE_UPDATING_AUTO_LOGIN = "Error occurred while updating user profile as auto Login";
    protected static final Integer COOKIE_LOGIN_SECURITY_STATUS = Integer.valueOf(2);
	protected static final Integer LOGGED_IN_USER_SECURITY_STATUS = Integer.valueOf(4);

	protected static final String ERROR_SHIPPING_GROUP_ID_INVALID = "ERROR_SHIPPING_GROUP_ID_INVALID";
	
	public static final PBKDF2PasswordHasher mPBKDF2PasswordHasher = (PBKDF2PasswordHasher)Nucleus.getGlobalNucleus().resolveName("/com/bbb/security/PBKDF2PasswordHasher");


	
	
	// Constants - END
	
	
	//Instance variables (class variables) - START
	
	private String createRegistryUri;

	   
    //for password reset token validation 
    private MutableRepository passwordSaltRepository;
	private String forgotPasswordTokenQuery;
	private String urlToken;

	//private String babyCAFlagValue = "false"; 
    private SiteContext siteContext;
    private RegistrySearchVO mRegistrySearchVO;
    private TemplateEmailInfo registrationTemplateEmailInfo;
    private Order mOrder;
    private LblTxtTemplateManager messageHandler;
    private FBProfileTools facebookProfileTool; // Facebook Profile Tool instance
    private GiftRegistryManager mGiftRegistryManager;
    private SiteGroupManager mSiteGroup;
    private StoreGiftlistFormHandler mGiftListHandler;
    private ShippingGroupMapContainer mShippingGroupMapContainer;
    private ExtendableCreditCardTools creditCardTools; // property: creditCardTools
    private DCPrefixIdGenerator idGenerator;

    private BBBGetCouponsManager couponsManager;
    private BBBProfileManager profileManager;
    
    private BBBCatalogTools bbbCatalogTools;
    public BBBPropertyManager propertyManager; // This is public because used by GiftRegFormHandler also
    private BBBGiftlistManager giftListManager;
    private BBBProfileServices profileService;
    private BBBPaymentGroupManager paymentGroupManager;
    private BBBSavedItemsSessionBean savedItemsSessionBean;
    private BBBOrder bbbOrder;
    private BBBSessionBean mSessionBean;
    private boolean personalInfoValidated;

    private Map<String,String> successUrlMap;
    private Map<String,String> errorUrlMap;
    private String emailMD5HashPrefix;
	
    //QAS flags
    private String poBoxFlag;
    private String poBoxStatus;
    private String resetEmailTokenQuery;
    private ClientLockManager mClientLockManager;
	
    private String redirectURL;
    private Map<String,String> recUserRedirectionURL;
	
    private String  handleChallengeQuestionFailureUrl;
    private String handleChallengeQuestionSuccesUrl;

    private boolean shallowProfileChanges;
    private boolean shallowProfileUpdateRequest=false;
    
    private String mAssoSite;
    private String redirectPage;
    private String mLoginEmail;
    private String mReclaimLegacyAccountSuccessURL;
    private String mReclaimLegacyAccountIncorrectPasswordURL;
    private String mLegacyPasswordSuccessURL;
    private String mStrReminderSent;
    private String mReclaimLegacyIncorrectPasswordSuccessURL;
    private String addressAdded;
    private String legacyUser;
    private String legacyPasswordPopupURL;
    private String userCheckingOut;
    private String loginURL;


    // migrated user BBBSL-1429
    private boolean mSharedCheckBoxEnabled;
    private boolean mFormErrorVal;
    private boolean mMakePreferredSet;
    private boolean mSuccessMessage;
    private boolean mChangePasswordSuccessMessage;
    private boolean mLegacyForgetPasswordStatus;
    private boolean login;
    private boolean legacyUserBoolean;
    private boolean saveCreditCardInfoToProfile;
    private boolean fromMobile;
    private boolean migratedAccount;
    private boolean loginFromCheckout; // Boolean to check if user login while
    
    private String orderCookieName;
    private String profileID;
    private String orderCookiePath;
    private String cartURL;
    private String migratedUserPopupURL;
    private String profileCookieName;
    private String profileCookiePath;
    private String storeContextPath; // variable to get context path from properties file - REST Specific
    private String emailAddress; // FB email id to be register
    private String facebookLinking; // FB linking flag post registration
    private String mUpdateCardErrorURL; // Update card error redirect URL.
    private String mUpdateCardSuccessURL; // Update card success redirect URL.
    private String mCreateCardErrorURL;
    private String updateAddressErrorURL;
    private String newAddressErrorURL;
    private String mRemoveCard;
    private String updateAddressSuccessURL;
    private String mRegistrationSuccessURL;
    private String mRegistrationErrorURL;
    private String mExtenstionSuccessURL;
    private String mCreateCardSuccessURL;
    private String mDefaultShippingAddress; // Default shipping address.
    private String defaultBillingAddress; // Default Billing address.
    private String defaultMailingAddress; // Default Mailing address.
    private String mNicknameValueMapKey = NICKNAME;
    private String mAddressIdValueMapKey = "addressId";
    private String mNewNicknameValueMapKey = NEW_NICKNAME;
    private String mShippingAddressNicknameMapKey = "shippingAddrNickname";
    private String mAddressId;
    private String mEditAddress;
    private String mDefaultCard; // Default card.
    private String mEditCard; // Edit card.
    private String mPreRegisterErrorURL;
    private String mPreRegisterSuccessURL;
    private String mRemoveCardErrorURL;
    private String mRemoveCardSuccessURL;
    private String newAddressSuccessURL;

    private int mCreditCardYearMaxLimit;
    private int profileCookieAge;

    private boolean emailOptIn;
    private boolean emailOptIn_BabyCA;
    private boolean emailOptInSharedSite;
    private boolean extendEmailOptn;
    private boolean showMigratedUserPopupURL; // flag to show extend pop up for

    // checking out
    private boolean userMigratedLoginProp;
    private boolean mUseShippingAddressAsDefault; // Default shipping address.
    private boolean mUseBillingAddressAsDefault; // Default Billing address.
    private boolean mUseMailingAddressAsDefault;
    private boolean autoLoginAfterChangePassword; // auto login user after changing password BBBSL-1017
    private boolean rememberMe;  // Auto Login Feature for Mobile App
	protected boolean indirectRequest = false; 
	private String resetPasswordTokenQuery;
	private String confirmPasswordErrorUrl;
	private String confirmPasswordSuccessURL;
	private String fromPage;// Page Name that will be set from JSP

	private boolean challengeQuestionUpdated;
	private Map<String, String> mTbsBBBSiteMap;
	private CookieManager cookieManager;
	
	private String regEventType;
	private String writeReviewSuccessURL;
	
	
	

    private final Map<String, String> mBillAddrValue = new HashMap<String, String>(); // Billing address values.

    private List<String> mBillingAddressPropertyList; // Billing address property list.
    private List<String> mRequiredBillingAddressPropertyList; // Required billing address property list.
    
    private String[] mAddressProperties = new String[] { FIRST_NAME, MIDDLE_NAME, LAST_NAME, ADDRESS_1, ADDRESS_2,
            CITY, STATE, POSTAL_CODE, OWNER_ID, COMPANY_NAME, NICKNAME, };

    private String[] mCardProperties = new String[] { CREDIT_CARD_NUMBER, CREDIT_CARD_TYPE, EXPIRATION_MONTH,
            EXPIRATION_YEAR, BILLING_ADDRESS, NAME_ON_CARD, };

    private String[] mImmutableCardProperties = new String[] { CREDIT_CARD_NUMBER, CREDIT_CARD_TYPE };

	private List<RepositoryItem> srcAppliedCoupons;
        
    
	//Instance variables (class variables) - END
	
    
    
	/**
	 * Getters & Setters section
	 */

	/**
	 * @return the createRegistryUri
	 */
	public String getCreateRegistryUri() {
		return createRegistryUri;
	}

	/**
	 * @param pCreateRegistryUri
	 *            the createRegistryUri to set
	 */
	public void setCreateRegistryUri(String pCreateRegistryUri) {
		createRegistryUri = pCreateRegistryUri;
	}

	public String getUrlToken() {
		return urlToken;
	}

	public void setUrlToken(String urlToken) {
		this.urlToken = urlToken;
	}

	public MutableRepository getPasswordSaltRepository() {
		return passwordSaltRepository;
	}

	public void setPasswordSaltRepository(MutableRepository passwordSaltRepository) {
		this.passwordSaltRepository = passwordSaltRepository;
	}

	/**
	 * @return the forgotPasswordTokenQuery
	 */
	public String getForgotPasswordTokenQuery() {
		return forgotPasswordTokenQuery;
	}

	/**
	 * @param forgotPasswordTokenQuery
	 *            the forgotPasswordTokenQuery to set
	 */
	public void setForgotPasswordTokenQuery(String forgotPasswordTokenQuery) {
		this.forgotPasswordTokenQuery = forgotPasswordTokenQuery;
	}

	/**
	 * @return Site Context
	 */
	public final SiteContext getSiteContext() {
		return this.siteContext;
	}

	/**
	 * @param siteContext
	 *            the mSiteContext to set
	 */
	public final void setSiteContext(final SiteContext siteContext) {
		this.siteContext = siteContext;
	}

	/**
	 * @return the registrySearchVO
	 */
	public final RegistrySearchVO getRegistrySearchVO() {
		if (this.mRegistrySearchVO == null) {
			this.mRegistrySearchVO = new RegistrySearchVO();
		}
		return this.mRegistrySearchVO;
	}

	/**
	 * @param registrySearchVO
	 *            the registrySearchVO to set
	 */
	public final void setRegistrySearchVO(final RegistrySearchVO registrySearchVO) {
		this.mRegistrySearchVO = registrySearchVO;
	}

	/**
	 * @return the mTemplateEmailInfo
	 */
	public final TemplateEmailInfo getRegistrationTemplateEmailInfo() {
		return this.registrationTemplateEmailInfo;
	}

	/**
	 * @param registrationTemplateEmailInfo
	 *            the mTemplateEmailInfo to set
	 */
	public final void setRegistrationTemplateEmailInfo(final TemplateEmailInfo registrationTemplateEmailInfo) {
		this.registrationTemplateEmailInfo = registrationTemplateEmailInfo;
	}

	/**
	 * Set the Order property.
	 * 
	 * @param pOrder
	 *            an <code>Order</code> value
	 */
	public final void setOrder(final Order pOrder) {
		this.mOrder = pOrder;
	}

	/**
	 * Return the Order property.
	 * 
	 * @return an <code>Order</code> value
	 */
	public  Order getOrder() {
		if (this.mOrder != null) {
			return this.mOrder;
		}

		return this.getShoppingCart().getCurrent();
	}

	/**
	 * @return the lblTxtTemplateManager
	 */
	public final LblTxtTemplateManager getMessageHandler() {
		return this.messageHandler;
	}

	/**
	 * @param messageHandler
	 *            the lblTxtTemplateManager to set
	 */
	public final void setMessageHandler(final LblTxtTemplateManager messageHandler) {
		this.messageHandler = messageHandler;
	}

	/**
	 * @return Facebook Profile Tool
	 */
	public FBProfileTools getFacebookProfileTool() {
		return this.facebookProfileTool;
	}

	/**
	 * @param facebookProfileTool
	 *            the pFbProfileTool to set
	 */
	public void setFacebookProfileTool(final FBProfileTools facebookProfileTool) {
		this.facebookProfileTool = facebookProfileTool;
	}

	/**
	 * @return the giftRegistryManager
	 */
	public final GiftRegistryManager getGiftRegistryManager() {
		return this.mGiftRegistryManager;
	}

	/**
	 * @param pGiftRegistryManager
	 *            the giftRegistryManager to set
	 */
	public final void setGiftRegistryManager(final GiftRegistryManager pGiftRegistryManager) {
		this.mGiftRegistryManager = pGiftRegistryManager;
	}

	/**
	 * @return the siteGroup
	 */
	public final SiteGroupManager getSiteGroup() {
		return this.mSiteGroup;
	}

	/**
	 * @param pSiteGroup
	 *            the siteGroup to set
	 */
	public final void setSiteGroup(final SiteGroupManager pSiteGroup) {
		this.mSiteGroup = pSiteGroup;
	}

	/**
	 * @return the StoreGiftlistFormHandler
	 */
	public final StoreGiftlistFormHandler getGiftListHandler() {
		return this.mGiftListHandler;
	}

	/**
	 * @param mGiftListHandler
	 *            the StoreGiftlistFormHandler to set
	 */
	public final void setGiftListHandler(final StoreGiftlistFormHandler mGiftListHandler) {
		this.mGiftListHandler = mGiftListHandler;
	}

	/**
	 * Set the ShippingGroupMapContainer property.
	 * 
	 * @param pShippingGroupMapContainer
	 *            a ShippingGroupMapContainer value
	 */
	public final void setShippingGroupMapContainer(final ShippingGroupMapContainer pShippingGroupMapContainer) {
		this.mShippingGroupMapContainer = pShippingGroupMapContainer;
	}

	/**
	 * Return the ShippingGroupMapContainer property.
	 * 
	 * @return a <code>ShippingGroupMapContainer</code> value
	 */
	public final ShippingGroupMapContainer getShippingGroupMapContainer() {
		return this.mShippingGroupMapContainer;
	}

	/**
	 * @return ExtendableCreditCardTools
	 */
	public final ExtendableCreditCardTools getCreditCardTools() {
		return this.creditCardTools;
	}

	/**
	 * @param creditCardTools
	 *            new ExtendableCreditCardTools
	 */
	public final void setCreditCardTools(final ExtendableCreditCardTools creditCardTools) {
		this.creditCardTools = creditCardTools;
	}

	/**
	 * @return the idGenerator
	 */
	public final DCPrefixIdGenerator getIdGenerator() {
		return this.idGenerator;
	}

	/**
	 * @param idGenerator
	 *            the idGenerator to set
	 */
	public final void setIdGenerator(final DCPrefixIdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	public BBBGetCouponsManager getCouponsManager() {
		return couponsManager;
	}

	public void setCouponsManager(BBBGetCouponsManager couponsManager) {
		this.couponsManager = couponsManager;
	}

	/**
	 * @return mManager
	 */
	public final BBBProfileManager getProfileManager() {
		return this.profileManager;
	}

	/**
	 * @param profileManager
	 */
	public final void setProfileManager(final BBBProfileManager profileManager) {
		this.profileManager = profileManager;
	}

	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	/** @return mPmgr */
    public BBBPropertyManager getPropertyManager() {
        return this.propertyManager;
    }

    /** @param propertyManager Property Manager */
    public final void setPropertyManager(final BBBPropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }

    /**
	 * @return the giftListManager
	 */
	public final BBBGiftlistManager getGiftListManager() {
		return this.giftListManager;
	}

	/**
	 * @param giftListManager the giftListManager to set
	 */
	public final void setGiftListManager(final BBBGiftlistManager giftListManager) {
		this.giftListManager = giftListManager;
	}

	/** @return the profileServices */
    public final BBBProfileServices getProfileService() {
        return this.profileService;
    }

    /** @param profileServices the profileServices to set */
    public final void setProfileService(final BBBProfileServices profileServices) {
        this.profileService = profileServices;
    }

    /** @return Payment Group Manager */
    public final BBBPaymentGroupManager getPaymentGroupManager() {
        return this.paymentGroupManager;
    }

    /** @param paymentGroupManager paymentGroupManager */
    public final void setPaymentGroupManager(final BBBPaymentGroupManager paymentGroupManager) {
        this.paymentGroupManager = paymentGroupManager;
    }

    /** @return Saved Item Session Bean */
    public final BBBSavedItemsSessionBean getSavedItemsSessionBean() {
        return this.savedItemsSessionBean;
    }

    /** @param savedItemsSessionBean Saved Item Session Bean */
    public final void setSavedItemsSessionBean(final BBBSavedItemsSessionBean savedItemsSessionBean) {
        this.savedItemsSessionBean = savedItemsSessionBean;
    }

    /** @return BBB Order */
    public final BBBOrder getBBBOrder() {
        return this.bbbOrder;
    }

    /** @param bbbOrder */
    public final void setBBBOrder(final BBBOrder bbbOrder) {
        this.bbbOrder = bbbOrder;
    }
    
    /** @return the sessionBean */
    public final BBBSessionBean getSessionBean() {
        return this.mSessionBean;
    }

    /** @param pSessionBean the sessionBean to set */
    public final void setSessionBean(final BBBSessionBean pSessionBean) {
        this.mSessionBean = pSessionBean;
    }
    
    /**
	 * @return personalInfoValidated
	 */
	public boolean isPersonalInfoValidated() {
		return personalInfoValidated;
	}

	/**
	 * @param personalInfoValidated
	 */
	public void setPersonalInfoValidated(boolean personalInfoValidated) {
		this.personalInfoValidated = personalInfoValidated;
	}

    /**
	 * @return the emailMD5HashPrefix
	 */
	public String getEmailMD5HashPrefix() {
		return this.emailMD5HashPrefix;
	}

	/**
	 * @param emailMD5HashPrefix the emailMD5HashPrefix to set
	 */
	public void setEmailMD5HashPrefix(String emailMD5HashPrefix) {
		this.emailMD5HashPrefix = emailMD5HashPrefix;
	}

    public Map<String, String> getErrorUrlMap() {
		return errorUrlMap;
	}

	public void setErrorUrlMap(Map<String, String> errorUrlMap) {
		this.errorUrlMap = errorUrlMap;
	}

	public Map<String, String> getSuccessUrlMap() {
		return successUrlMap;
	}

	public void setSuccessUrlMap(Map<String, String> successUrlMap) {
		this.successUrlMap = successUrlMap;
	}

	public String getPoBoxFlag() {
		return poBoxFlag;
	}

	public void setPoBoxFlag(String poBoxFlag) {
		this.poBoxFlag = poBoxFlag;
	}

	public String getPoBoxStatus() {
		return poBoxStatus;
	}

	public void setPoBoxStatus(String poBoxStatus) {
		this.poBoxStatus = poBoxStatus;
	}

	public String getResetEmailTokenQuery() {
		return resetEmailTokenQuery;
	}

	public void setResetEmailTokenQuery(String resetEmailTokenQuery) {
		this.resetEmailTokenQuery = resetEmailTokenQuery;
	}	

    public ClientLockManager getClientLockManager() {
		return mClientLockManager;
	}

	public void setClientLockManager(ClientLockManager mClientLockManager) {
		this.mClientLockManager = mClientLockManager;
	}

	public String getRedirectPage() {
		return redirectPage;
	}

	public void setRedirectPage(String redirectPage) {
		this.redirectPage = redirectPage;
	}

	public String getRedirectURL() {
		return redirectURL;
	}

	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}

	public Map<String, String> getRecUserRedirectionURL() {
		return recUserRedirectionURL;
	}

	public void setRecUserRedirectionURL(Map<String, String> recUserRedirectionURL) {
		this.recUserRedirectionURL = recUserRedirectionURL;
	}
	
	/**
	 * @return the handleChallengeQuestionFailureUrl
	 */
	public String getHandleChallengeQuestionFailureUrl() {
		return handleChallengeQuestionFailureUrl;
	}

	/**
	 * @param handleChallengeQuestionFailureUrl the handleChallengeQuestionFailureUrl to set
	 */
	public void setHandleChallengeQuestionFailureUrl(
			String handleChallengeQuestionFailureUrl) {
		this.handleChallengeQuestionFailureUrl = handleChallengeQuestionFailureUrl;
	}
    /**
 	 * @return the handleChallengeQuestionSuccesUrl
 	 */
 	public String getHandleChallengeQuestionSuccesUrl() {
 		return handleChallengeQuestionSuccesUrl;
 	}

 	/**
 	 * @param handleChallengeQuestionSuccesUrl the handleChallengeQuestionSuccesUrl to set
 	 */
 	public void setHandleChallengeQuestionSuccesUrl(
 			String handleChallengeQuestionSuccesUrl) {
 		this.handleChallengeQuestionSuccesUrl = handleChallengeQuestionSuccesUrl;
 	}

 	public boolean isShallowProfileChanges() {
		return shallowProfileChanges;
	}

	public void setShallowProfileChanges(boolean shallowProfileChanges) {
		this.shallowProfileChanges = shallowProfileChanges;
	}

	public boolean isShallowProfileUpdateRequest() {
		return shallowProfileUpdateRequest;
	}

	public void setShallowProfileUpdateRequest(boolean profileUpdateRequest) {
		this.shallowProfileUpdateRequest = profileUpdateRequest;
	}
	/** @return the mLoginUrl */
    public final String getLoginURL() {
        return this.loginURL;
    }

    /** @param loginURL the mLoginUrl to set */
    public final void setLoginURL(final String loginURL) {
        this.loginURL = loginURL;
    }

    /** @return the mUserCheckingOut */
    public final String getUserCheckingOut() {
        return this.userCheckingOut;
    }

    /** @param userCheckingOut the pUserCheckingOut to set */
    public final void setUserCheckingOut(final String userCheckingOut) {
        this.userCheckingOut = userCheckingOut;
    }

    /** @return the mLegacyPasswordPopup */
    public final String getLegacyPasswordPopupURL() {
        return this.legacyPasswordPopupURL;
    }

    /** @param legacyPasswordPopupURL the mLegacyPasswordPopup to set */
    public final void setLegacyPasswordPopupURL(final String legacyPasswordPopupURL) {
        this.legacyPasswordPopupURL = legacyPasswordPopupURL;
    }

    /** @return the legacyUser */
    public final String getLegacyUser() {
        return this.legacyUser;
    }

    /** @param legacyUser the legacyUser to set */
    public final void setLegacyUser(final String legacyUser) {
        this.legacyUser = legacyUser;
    }
    /** @return the addressAdded */
    public final String getAddressAdded() {
        return this.addressAdded;
    }

    /** @param addressAdded the addressAdded to set */
    public final void setAddressAdded(final String addressAdded) {
        this.addressAdded = addressAdded;
    }

    /** @return the reclaimLegacyIncorrectPasswordSuccessURL */
    public final String getReclaimLegacyIncorrectPasswordSuccessURL() {
        return this.mReclaimLegacyIncorrectPasswordSuccessURL;
    }

    /** @param pReclaimLegacyIncorrectPasswordSuccessURL the reclaimLegacyIncorrectPasswordSuccessURL to set */
    public final void setReclaimLegacyIncorrectPasswordSuccessURL(final String pReclaimLegacyIncorrectPasswordSuccessURL) {
        this.mReclaimLegacyIncorrectPasswordSuccessURL = pReclaimLegacyIncorrectPasswordSuccessURL;
    }

    /** @return the mStringVal */
    public final String getStrReminderSent() {
        return this.mStrReminderSent;
    }

    /** @param pStrReminderSent the pStrReminderSent to set */
    public final void setStrReminderSent(final String pStrReminderSent) {
        this.mStrReminderSent = pStrReminderSent;
    }
    
    /** @return the mLegacyPasswordSuccessURL */
    public final String getLegacyPasswordSuccessURL() {
        return this.mLegacyPasswordSuccessURL;
    }

    /** @param pLegacyPasswordSuccessURL the pLegacyPasswordSuccessURL to set */
    public final void setLegacyPasswordSuccessURL(final String pLegacyPasswordSuccessURL) {
        this.mLegacyPasswordSuccessURL = pLegacyPasswordSuccessURL;
    }
    /** @return the assoSite */
    public final String getAssoSite() {
        return this.mAssoSite;
    }

    /** @param pAssoSite the assoSite to set */
    public final void setAssoSite(final String pAssoSite) {
        this.mAssoSite = pAssoSite;
    }

    /** @return the loginEmail */
    public final String getLoginEmail() {
        return this.mLoginEmail;
    }

    /** @param pLoginEmail the loginEmail to set */
    public final void setLoginEmail(final String pLoginEmail) {
        this.mLoginEmail = pLoginEmail;
    }

    /** @return the mReclaimLegacyAccountSuccessURL */
    public final String getReclaimLegacyAccountSuccessURL() {
        return this.mReclaimLegacyAccountSuccessURL;
    }

    /** @param pReclaimLegacyAccountSuccessURL the mReclaimLegacyAccountSuccessURL to set */
    public final void setReclaimLegacyAccountSuccessURL(final String pReclaimLegacyAccountSuccessURL) {
        this.mReclaimLegacyAccountSuccessURL = pReclaimLegacyAccountSuccessURL;
    }

    /** @return the mReclaimLegacyAccountIncorrectPasswordURL */
    public final String getReclaimLegacyAccountIncorrectPasswordURL() {
        return this.mReclaimLegacyAccountIncorrectPasswordURL;
    }

    /** @param pReclaimLegacyAccountIncorrectPasswordURL the mReclaimLegacyAccountIncorrectPasswordURL to set */
    public final void setReclaimLegacyAccountIncorrectPasswordURL(final String pReclaimLegacyAccountIncorrectPasswordURL) {
        this.mReclaimLegacyAccountIncorrectPasswordURL = pReclaimLegacyAccountIncorrectPasswordURL;
    }
    
    /** @return Legacy User Flag */
    public final boolean isLegacyUserBoolean() {
        return this.legacyUserBoolean;
    }

    /** @param legacyUser the legacyUser to set */
    public final void setLegacyUserBoolean(final boolean legacyUser) {
        this.legacyUserBoolean = legacyUser;
    }


    /** @return the sharedCheckBoxEnabled */
    public final boolean getSharedCheckBoxEnabled() {
        return this.mSharedCheckBoxEnabled;
    }

    /** @param pSharedCheckBoxEnabled the sharedCheckBoxEnabled to set */
    public final void setSharedCheckBoxEnabled(final boolean pSharedCheckBoxEnabled) {
        this.mSharedCheckBoxEnabled = pSharedCheckBoxEnabled;
    }

    /** @return the mMakePreferredSet */
    public final boolean isMakePreferredSet() {
        return this.mMakePreferredSet;
    }

    /** @param pMakePreferredSet the mMakePreferredSet to set */
    public final void setMakePreferredSet(final boolean pMakePreferredSet) {
        this.mMakePreferredSet = pMakePreferredSet;
    }

    /** @return the changePasswordSuccessMessage */
    public final boolean isChangePasswordSuccessMessage() {
        return this.mChangePasswordSuccessMessage;
    }

    /** @param pChangePasswordSuccessMessage the changePasswordSuccessMessage to set */
    public final void setChangePasswordSuccessMessage(final boolean pChangePasswordSuccessMessage) {
        this.mChangePasswordSuccessMessage = pChangePasswordSuccessMessage;
    }

    /** @return the successMessage */
    public final boolean isSuccessMessage() {
        return this.mSuccessMessage;
    }

    /** @param pSuccessMessage the successMessage to set */
    public final void setSuccessMessage(final boolean pSuccessMessage) {
        this.mSuccessMessage = pSuccessMessage;
    }

    /** @return Login From Checkout */
    public final boolean isLoginFromCheckout() {
        return this.loginFromCheckout;
    }

    /** @param loginFromCheckout loginFromCheckout */
    public final void setLoginFromCheckout(final boolean loginFromCheckout) {
        this.loginFromCheckout = loginFromCheckout;
    }
    /** @return the mLegacyForgetPasswordStatus */
    public final boolean isLegacyForgetPasswordStatus() {
        return this.mLegacyForgetPasswordStatus;
    }

    /** @param pLegacyForgetPasswordStatus the mLegacyForgetPasswordStatus to set */
    public final void setLegacyForgetPasswordStatus(final boolean pLegacyForgetPasswordStatus) {
        this.mLegacyForgetPasswordStatus = pLegacyForgetPasswordStatus;
    }

    
    /** @return the mFormErrorVal */
    public final boolean isFormErrorVal() {
        return this.mFormErrorVal;
    }

    /** @param pFormErrorVal the pFormErrorVal to set */
    public final void setFormErrorVal(final boolean pFormErrorVal) {
        this.mFormErrorVal = pFormErrorVal;
    }

    /** @return Migrated Account Flag */
    public final boolean isMigratedAccount() {
        return this.migratedAccount;
    }

    /** @param migratedAccount migratedAccount */
    public final void setMigratedAccount(final boolean migratedAccount) {
        this.migratedAccount = migratedAccount;
    }

    /** @return Channel Flag */
    public final boolean isFromMobile() {
        return this.fromMobile;
    }

    /** @param fromMobile fromMobile */
    public final void setFromMobile(final boolean fromMobile) {
        this.fromMobile = fromMobile;
    }

    /** @return Credit Card Save Flag */
    public final boolean isSaveCreditCardInfoToProfile() {
        return this.saveCreditCardInfoToProfile;
    }

    /** @param saveCreditCardInfoToProfile saveCreditCardInfoToProfile */
    public final void setSaveCreditCardInfoToProfile(final boolean saveCreditCardInfoToProfile) {
        this.saveCreditCardInfoToProfile = saveCreditCardInfoToProfile;
    }

    /** @return the mLogin */
    public final boolean isLogin() {
        return this.login;
    }

    /** @param login the mLogin to set */
    public final void setLogin(final boolean login) {
        this.login = login;
    }

    /** @return the mStoreContextPath */
    public final String getStoreContextPath() {
        return this.storeContextPath;
    }

    /** @param storeContextPath the mStoreContextPath to set */
    public final void setStoreContextPath(final String storeContextPath) {
        this.storeContextPath = storeContextPath;
    }

    /** @return Migrate User Popup URL */
    public final String getMigratedUserPopupURL() {
        return this.migratedUserPopupURL;
    }

    /** @param migratedUserPopupURL */
    public final void setMigratedUserPopupURL(final String migratedUserPopupURL) {
        this.migratedUserPopupURL = migratedUserPopupURL;
    }

    /** @return the cartURL */
    public final String getCartURL() {
        return this.cartURL;
    }

    /** @return the profileCookieName */
    public final String getProfileCookieName() {
        return this.profileCookieName;
    }

    /** @param profileCookieName the profileCookieName to set */
    public final void setProfileCookieName(final String profileCookieName) {
        this.profileCookieName = profileCookieName;
    }
    /** @return the profileCookiePath */
    public final String getProfileCookiePath() {
        return this.profileCookiePath;
    }

    /** @param profileCookiePath the profileCookiePath to set */
    public final void setProfileCookiePath(final String profileCookiePath) {
        this.profileCookiePath = profileCookiePath;
    }

    /** @param cartURL the cartURL to set */
    public final void setCartURL(final String cartURL) {
        this.cartURL = cartURL;
    }

    /** @return Profile ID */
    public final String getProfileID() {
        return this.profileID;
    }

    /** @param profileID profileID */
    public final void setProfileID(final String profileID) {
        this.profileID = profileID;
    }

    
    /** @return the orderCookiePath */
    public final String getOrderCookiePath() {
        return this.orderCookiePath;
    }

    /** @param pOrderCookiePath the orderCookiePath to set */
    public final void setOrderCookiePath(final String pOrderCookiePath) {
        this.orderCookiePath = pOrderCookiePath;
    }

    /** @return the orderCookieName */
    public final String getOrderCookieName() {
        return this.orderCookieName;
    }

    /** @param pOrderCookieName the orderCookieName to set */
    public final void setOrderCookieName(final String pOrderCookieName) {
        this.orderCookieName = pOrderCookieName;
    }
    
  
    /** @return the mEmailAddress */
    public final String getEmailAddress() {
        return this.emailAddress;
    }

    /** @param emailAddress the mEmailAddress to set */
    public final void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

   
    
    /** @return the mFBLinking */
    public final String getFacebookLinking() {
        return this.facebookLinking;
    }

    /** @param facebookLinking the pFBLinking to set */
    public final void setFacebookLinking(final String facebookLinking) {
        this.facebookLinking = facebookLinking;
    }


    /** @return Registration Success URL */
    public final String getRegistrationSuccessURL() {
        return this.mRegistrationSuccessURL;
    }

    /** @param mRegistrationSuccessURL Registration Success URL */
    public final void setRegistrationSuccessURL(final String mRegistrationSuccessURL) {
        this.mRegistrationSuccessURL = mRegistrationSuccessURL;
    }

    /** @return Registration Error URL */
    public final String getRegistrationErrorURL() {
        return this.mRegistrationErrorURL;
    }

    /** @param mRegistrationErrorURL Registration Error URL */
    public final void setRegistrationErrorURL(final String mRegistrationErrorURL) {
        this.mRegistrationErrorURL = mRegistrationErrorURL;
    }

    
    /** @return the extenstionSuccessURL */
    public final String getExtenstionSuccessURL() {
        return this.mExtenstionSuccessURL;
    }

    /** @param pExtenstionSuccessURL the extenstionSuccessURL to set */
    public final void setExtenstionSuccessURL(final String pExtenstionSuccessURL) {
        this.mExtenstionSuccessURL = pExtenstionSuccessURL;
    }


    /** @return Default Billing Address Code */
    public final String getDefaultBillingAddressCode() {
        return this.defaultBillingAddress;
    }

    /** @param mDefaultBillingAddressCode Default Billing Address Code */
    public final void setDefaultBillingAddressCode(final String mDefaultBillingAddressCode) {
        this.defaultBillingAddress = mDefaultBillingAddressCode;
    }

    /** @return Default Mailing Address Code */
    public final String getDefaultMailingAddressCode() {
        return this.defaultMailingAddress;
    }

    /** @param mDefaultMailingAddressCode Default Mailing Address Code */
    public final void setDefaultMailingAddressCode(final String mDefaultMailingAddressCode) {
        this.defaultMailingAddress = mDefaultMailingAddressCode;
    }

    /** @return Default Shipping Address Code */
    public final String getDefaultShippingAddressCode() {
        return this.mDefaultShippingAddress;
    }

    /** @param mDefaultShippingAddressCode */
    public final void setDefaultShippingAddressCode(final String mDefaultShippingAddressCode) {
        this.mDefaultShippingAddress = mDefaultShippingAddressCode;
    }

    /** Sets property efaultCard, naming the credit card to be the default.
     * 
     * @param pDefaultCard - nickname of default credit card */
    public final void setDefaultCard(final String pDefaultCard) {
        this.mDefaultCard = pDefaultCard;
    }

    /** Returns property editCard, naming the credit card to be edited. The edit credit card property.
     * 
     * @return Default credit card nickname */
    public final String getDefaultCard() {
        return this.mDefaultCard;
    }

    /** Sets property editCard, naming the credit card to be edited.
     * 
     * @param pEditCard - nickname of card being edited */
    public final void setEditCard(final String pEditCard) {
        this.mEditCard = pEditCard;
    }

    /** Returns property editCard, naming the credit card to be edited. The edit credit card property.
     * 
     * @return The nickname of the credit card being edited */
    public final String getEditCard() {
        return this.mEditCard;
    }    
    
    /** @param pDefaultShippingAddress default shipping address. */
    public final void setDefaultShippingAddress(final String pDefaultShippingAddress) {
        this.mDefaultShippingAddress = pDefaultShippingAddress;
    }

    /** @return default shipping address. */
    public final String getDefaultShippingAddress() {
        return this.mDefaultShippingAddress;
    }
    

    /** Sets property removeCard, naming the address to be removed by handleRemoveCard().
     * 
     * @param pRemoveCard **/
    public final void setRemoveCard(final String pRemoveCard) {
        this.mRemoveCard = pRemoveCard;
    }

    /** Returns property removeCard, naming the address to be removed by handleRemoveCard().
     * 
     * @return mRemoveCard **/
    public final String getRemoveCard() {
        return this.mRemoveCard;
    }
    
    /** Sets property editAddress, naming the address to be edited
     * 
     * @param pEditAddress editAddress **/
    public final void setEditAddress(final String pEditAddress) {
        this.mEditAddress = pEditAddress;
    }

    /** Returns property editAddress, naming the address to be edited
     * 
     * @return Edit Address **/
    public final String getEditAddress() {
    	return this.mEditAddress;
    }
    
    /** Sets property addressId, naming the address to be removed by handleRemoveAddress().
     * 
     * @param pRemoveAddress Address to be removed */
    public final void setAddressId(final String pRemoveAddress) {
        this.mAddressId = pRemoveAddress;
    }

    /** Returns property addressId, naming the address to be removed by handleRemoveAddress().
     * 
     * @return Address ID */
    public final String getAddressId() {
        return this.mAddressId;
    }
    /** Sets property newAddressErrorURL, used to redirect user in case of an error creating an address.
     * 
     * @param newAddressErrorURL newAddressErrorURL **/
    public final void setNewAddressErrorURL(final String newAddressErrorURL) {
        this.newAddressErrorURL = newAddressErrorURL;
    }

    /** Returns property newAddressErrorURL, used to redirect user in case of an error creating an address.
     * 
     * @return The URL to redirect the user to in case of an error creating the address. **/
    public final String getNewAddressErrorURL() {
        return this.newAddressErrorURL;
    }
    
    /** Sets property updateCardErrorURL, used to redirect user in case of an error updating a new credit card.
     * 
     * @param pUpdateCardErrorURL - credit card update error URL */
    public final void setUpdateCardErrorURL(final String pUpdateCardErrorURL) {
        this.mUpdateCardErrorURL = pUpdateCardErrorURL;
    }

    /** Returns property updateCardErrorURL, used to redirect user in case of an error updating a new credit card. The
     * URL to redirect the user to in case of an error while adding a new credit card.
     * 
     * @return update credit card error URL */
    public final String getUpdateCardErrorURL() {
        return this.mUpdateCardErrorURL;
    }

    /** Sets property removeCardSuccessURL, used to redirect user when a credit card is successfully removed.
     * 
     * @param pRemoveCardSuccessURL */
    public final void setRemoveCardSuccessURL(final String pRemoveCardSuccessURL) {
        this.mRemoveCardSuccessURL = pRemoveCardSuccessURL;
    }

    /** Returns property removeCardSuccessURL, used to redirect user in a credit card is successfully removed.
     * 
     * @return mRemoveCardSuccessURL */
    public final String getRemoveCardSuccessURL() {
        return this.mRemoveCardSuccessURL;
    }

    /** Sets property removeCardErrorURL, used to redirect user in case of an error removing a credit card.
     * 
     * @param pRemoveCardErrorURL Remove Card Error URL */
    public final void setRemoveCardErrorURL(final String pRemoveCardErrorURL) {
        this.mRemoveCardErrorURL = pRemoveCardErrorURL;
    }

    /** Returns property removeCardErrorURL, used to redirect user in case of an error removing a credit card.
     * 
     * @return Remove Card Error URL */
    public final String getRemoveCardErrorURL() {
        return this.mRemoveCardErrorURL;
    }
    /** @return Pre Register Success URL */
    public final String getPreRegisterSuccessURL() {
        return this.mPreRegisterSuccessURL;
    }

    /** @param pPreRegisterSuccessURL Pre Register Success URL */
    
    public final void setPreRegisterSuccessURL(final String pPreRegisterSuccessURL) {
        this.mPreRegisterSuccessURL = pPreRegisterSuccessURL;
    }
    
    /** @return Pre Register Error URL */
    
    public final String getPreRegisterErrorURL() {
        return this.mPreRegisterErrorURL;
    }

    /** @param pPreRegisterErrorURL Pre Register Error URL */
    
    public final void setPreRegisterErrorURL(final String pPreRegisterErrorURL) {
        this.mPreRegisterErrorURL = pPreRegisterErrorURL;
    }
    
    /** Sets property updateCardSuccessURL.
     * 
     * @param pUpdateCardSuccessURL - credit card update success URL */
    public final void setUpdateCardSuccessURL(final String pUpdateCardSuccessURL) {
        this.mUpdateCardSuccessURL = pUpdateCardSuccessURL;
    }

    /** Returns property updateCardSuccessURL. The URL to redirect the user to in case of an error while adding a new
     * credit card.
     * 
     * @return mUpdateCardSuccessURL */
    public final String getUpdateCardSuccessURL() {
        return this.mUpdateCardSuccessURL;
    }

    /** Sets property createCardErrorURL, used to redirect user in case of an error adding a new credit card.
     * 
     * @param pCreateCardErrorURL **/
    public final void setCreateCardErrorURL(final String pCreateCardErrorURL) {
        this.mCreateCardErrorURL = pCreateCardErrorURL;
    }

    /** Returns property createCardErrorURL, used to redirect user in case of an error adding a new credit card.
     * 
     * @return mCreateCardErrorURL */
    public final String getCreateCardErrorURL() {
        return this.mCreateCardErrorURL;
    }

    /** Sets property updateAddressErrorURL, used to redirect user in case of an error updating an address.
     * 
     * @param updateAddressErrorURL updateAddressErrorURL **/
    public final void setUpdateAddressErrorURL(final String updateAddressErrorURL) {
        this.updateAddressErrorURL = updateAddressErrorURL;
    }
    
    /** Returns property updateAddressErrorURL, used to redirect user in case of an error updating an address.
     * 
     * @return The URL to redirect the user to in case of an error updating the address. **/
    public final String getUpdateAddressErrorURL() {
        return this.updateAddressErrorURL;
    }

    /** Sets property updateAddressSuccessURL, used to redirect user when an address is successfully updated.
     * 
     * @param updateAddressSuccessURL updateAddressSuccessURL **/
    public final void setUpdateAddressSuccessURL(final String updateAddressSuccessURL) {
        this.updateAddressSuccessURL = updateAddressSuccessURL;
    }

    /** Returns property updateAddressSuccessURL, used to redirect user when an address is successfully updated.
     * 
     * @return The URL to redirect the user to when the address has been updated successfully. **/
    public final String getUpdateAddressSuccessURL() {
        return this.updateAddressSuccessURL;
    }
    
    /** Sets property createCardSuccessURL, used to redirect user if a new credit card was successfully added.
     * 
     * @param pCreateCardSuccessURL Credit Card Success URL */
    public final void setCreateCardSuccessURL(final String pCreateCardSuccessURL) {
        this.mCreateCardSuccessURL = pCreateCardSuccessURL;
    }

    /** Returns property createCardSuccessURL, used to redirect user if a new credit card was successfully added.
     * 
     * @return Credit Card Success URL */
    public final String getCreateCardSuccessURL() {
        return this.mCreateCardSuccessURL;
    }

    /** @param pDefaultBillingAddress the defaultBillingAddress to set */
    public final void setDefaultBillingAddress(final String pDefaultBillingAddress) {
        this.defaultBillingAddress = pDefaultBillingAddress;
    }

    /** @return the defaultBillingAddress */
    public final String getDefaultBillingAddress() {
        return this.defaultBillingAddress;
    }    

    /** @return the String */
    public final String getNicknameValueMapKey() {
        return this.mNicknameValueMapKey;
    }

    /** @param pNicknameValueMapKey the String to set */
    public final void setNicknameValueMapKey(final String pNicknameValueMapKey) {
        this.mNicknameValueMapKey = pNicknameValueMapKey;
    }


    /** @return the String */
    public final String getAddressIdValueMapKey() {
        return this.mAddressIdValueMapKey;
    }

    /** @param pAddressIdValueMapKey the String to set */
    public final void setAddressIdValueMapKey(final String pAddressIdValueMapKey) {
        this.mAddressIdValueMapKey = pAddressIdValueMapKey;
    }
    /** @return the String */
    public final String getNewNicknameValueMapKey() {
        return this.mNewNicknameValueMapKey;
    }

    /** @param pNewNicknameValueMapKey the String to set */
    public final void setNewNicknameValueMapKey(final String pNewNicknameValueMapKey) {
        this.mNewNicknameValueMapKey = pNewNicknameValueMapKey;
    }

    /** @return the String */
    public final String getShippingAddressNicknameMapKey() {
        return this.mShippingAddressNicknameMapKey;
    }

    /** @param pShippingAddressNicknameMapKey the String to set */
    public final void setShippingAddressNicknameMapKey(final String pShippingAddressNicknameMapKey) {
        this.mShippingAddressNicknameMapKey = pShippingAddressNicknameMapKey;
    }


    /** Sets property newAddressSuccessURL, used to redirect user after successfully creating an address.
     * 
     * @param newAddressSuccessURL newAddressSuccessURL **/
    public final void setNewAddressSuccessURL(final String newAddressSuccessURL) {
        this.newAddressSuccessURL = newAddressSuccessURL;
    }

    /** Returns property newAddressSuccessURL, used to redirect user after successfully creating an address.
     * 
     * @return the URL to redirect user to after successfully creating an address **/
    public final String getNewAddressSuccessURL() {
        return this.newAddressSuccessURL;
    }

    public String getDefaultMailingAddress() {
		return defaultMailingAddress;
	}

	public void setDefaultMailingAddress(String defaultMailingAddress) {
		this.defaultMailingAddress = defaultMailingAddress;
	}
	
	/** @return the mCreditCardYearMaxLimit */
    public final int getCreditCardYearMaxLimit() {
        return this.mCreditCardYearMaxLimit;
    }

    /** @param pCreditCardYearMaxLimit the mCreditCardYearMaxLimit to set */
    public final void setCreditCardYearMaxLimit(final int pCreditCardYearMaxLimit) {
        this.mCreditCardYearMaxLimit = pCreditCardYearMaxLimit;
    }
    /** @return the profileCookieAge */
    public final int getProfileCookieAge() {
        return this.profileCookieAge;
    }

    /** @param profileCookieAge the profileCookieAge to set */
    public final void setProfileCookieAge(final int profileCookieAge) {
        this.profileCookieAge = profileCookieAge;
    }

    /** @return the mEmailOptIn */
    public final boolean isEmailOptIn() {
        return this.emailOptIn;
    }

    /** @param emailOptIn the mEmailOptIn to set */
    public final void setEmailOptIn(final boolean emailOptIn) {
        this.emailOptIn = emailOptIn;
    }

    /** @return the mEmailOptInBaby */
    public final boolean isEmailOptIn_BabyCA() {
        return this.emailOptIn_BabyCA;
    }

    /** @param emailOptIn the mEmailOptInBaby to set */
    public final void setEmailOptIn_BabyCA(final boolean emailOptIn_BabyCA) {
        this.emailOptIn_BabyCA = emailOptIn_BabyCA;
    }
    /** @return Show Migrated User Popup */
    public final boolean isShowMigratedUserPopupURL() {
        return this.showMigratedUserPopupURL;
    }

    /** @param showMigratedUserPopupURL showMigratedUserPopupURL) { */
    public final void setShowMigratedUserPopupURL(final boolean showMigratedUserPopupURL) {
        this.showMigratedUserPopupURL = showMigratedUserPopupURL;
    }


    /** @return flag to identify if user need to auto login after password reset */
    public final boolean isAutoLoginAfterChangePassword() {
        return this.autoLoginAfterChangePassword;
    }

    /** Set flag to identify if user need to auto login after password reset.
     * 
     * @param autoLoginAfterChangePassword */
    public final void setAutoLoginAfterChangePassword(final boolean autoLoginAfterChangePassword) {
        this.autoLoginAfterChangePassword = autoLoginAfterChangePassword;
    }

   
    
    /** @return the userMigratedLoginProp */
    public final boolean isUserMigratedLoginProp() {
        return this.userMigratedLoginProp;
    }

    /** @param userMigratedLoginProp the userMigratedLoginProp to set */
    public final void setUserMigratedLoginProp(final boolean userMigratedLoginProp) {
        this.userMigratedLoginProp = userMigratedLoginProp;
    }
    
    /** @return Use Shipping Address as Default */
    public final boolean isUseShippingAddressAsDefault() {
        return this.mUseShippingAddressAsDefault;
    }

    /** @param pUseShippingAddressAsDefault Use Shipping Address as Default */
    public final void setUseShippingAddressAsDefault(final boolean pUseShippingAddressAsDefault) {
        this.mUseShippingAddressAsDefault = pUseShippingAddressAsDefault;
    }

    public boolean isUseMailingAddressAsDefault() {
		return mUseMailingAddressAsDefault;
	}

	public void setUseMailingAddressAsDefault(boolean pUseMailingAddressAsDefault) {
		this.mUseMailingAddressAsDefault = pUseMailingAddressAsDefault;
	}
    /** @return the useBillingAddressAsDefault */
    public final boolean isUseBillingAddressAsDefault() {
        return this.mUseBillingAddressAsDefault;
    }


    /** @param pUseBillingAddressAsDefault the useBillingAddressAsDefault to set */
    public final void setUseBillingAddressAsDefault(final boolean pUseBillingAddressAsDefault) {
        this.mUseBillingAddressAsDefault = pUseBillingAddressAsDefault;
    }

	/**
	 * @return the resetPasswordTokenQuery
	 */
	public String getResetPasswordTokenQuery() {
		return resetPasswordTokenQuery;
	}

	/**
	 * @param resetPasswordTokenQuery the resetPasswordTokenQuery to set
	 */
	public void setResetPasswordTokenQuery(String resetPasswordTokenQuery) {
		this.resetPasswordTokenQuery = resetPasswordTokenQuery;
	}

	/**
	 * @return the confirmPasswordErrorUrl
	 */
	public String getConfirmPasswordErrorUrl() {
		return confirmPasswordErrorUrl;
	}

	/**
	 * @param confirmPasswordErrorUrl the confirmPasswordErrorUrl to set
	 */
	public void setConfirmPasswordErrorUrl(String confirmPasswordErrorUrl) {
		this.confirmPasswordErrorUrl = confirmPasswordErrorUrl;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}
	
	public String getConfirmPasswordSuccessURL() {
		return confirmPasswordSuccessURL;
	}

	public void setConfirmPasswordSuccessURL(String confirmPasswordSuccessURL) {
		this.confirmPasswordSuccessURL = confirmPasswordSuccessURL;
	}

	/**
	 * @return fromPage
	 */
	public String getFromPage() {
		return fromPage;
	}

	/**
	 * @param fromPage
	 */
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	public String getRegEventType() {
		return regEventType;
	}

	public void setRegEventType(String regEventType) {
		this.regEventType = regEventType;
	}
	/**
	 * @return the cookieManager
	 */
	public CookieManager getCookieManager() {
		return cookieManager;
	}

	/**
	 * @param cookieManager the cookieManager to set
	 */
	public void setCookieManager(CookieManager cookieManager) {
		this.cookieManager = cookieManager;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, String> getTbsBBBSiteMap() {
		Map<String, String> tbsBBBSiteMap= new HashMap<String, String> ();
		tbsBBBSiteMap.put("TBS_BedBathUS","BedBathUS");
		tbsBBBSiteMap.put("TBS_BedBathCanada","BedBathCanada");
		tbsBBBSiteMap.put("TBS_BuyBuyBaby","BuyBuyBaby");
		return tbsBBBSiteMap;
	}
	/**
	 * 
	 * @param pTbsBBBSiteMap
	 */
	public void setTbsBBBSiteMap(Map<String, String> pTbsBBBSiteMap) {
		mTbsBBBSiteMap = pTbsBBBSiteMap;
	}

	/**
	 * @return the challengeQuestionUpdated
	 */
	public boolean isChallengeQuestionUpdated() {
		return challengeQuestionUpdated;
	}

	/**
	 * @param challengeQuestionUpdated the challengeQuestionUpdated to set
	 */
	public void setChallengeQuestionUpdated(boolean challengeQuestionUpdated) {
		this.challengeQuestionUpdated = challengeQuestionUpdated;
	}

	

    /** @param pAddressProperties Sets property addressProperties, naming the properties in a secondary address record. */
    public final void setAddressProperties(final String[] pAddressProperties) {
        this.mAddressProperties = pAddressProperties;
    }

    /** Returns property addressProperties, naming the properties in a secondary address record.
     * 
     * @return Address Properties */
    public final String[] getAddressProperties() {
        return this.mAddressProperties;
    }
    
    /** Sets property cardProperties, naming the properties in a credit card entry.
     * 
     * @param pCardProperties cardProperties **/
    public final void setCardProperties(final String[] pCardProperties) {
        this.mCardProperties = pCardProperties;
    }

    /** Returns property cardProperties, naming the properties in a credit card entry.
     * 
     * @return The list of card properties that are available */
    public final String[] getCardProperties() {
        return this.mCardProperties;
    }

    /** Returns immutableCardProperties property value. This property contains names of credit card's immutable
     * properties.
     * 
     * @return immutableCardProperties */
    public final String[] getImmutableCardProperties() {
        return this.mImmutableCardProperties;
    }

    /** Sets immutableCardProperties property value.
     * 
     * @param pImmutableCardProperties property names to be immutable. */
    public final void setImmutableCardProperties(final String[] pImmutableCardProperties) {
        this.mImmutableCardProperties = pImmutableCardProperties;
    }



    /** Sets the Address property list, which is a list that mirrors the original design of the AddressProperties
     * property with the property names defined in a configuration file. This List will be created by the
     * initializeAddressPropertyList method creating the appropriate list containing the values from the property
     * manager.
     * 
     * @param pBillingAddressPropertyList - Billing address property list */
    public final void setBillingAddressPropertyList(final List<String> pBillingAddressPropertyList) {
        this.mBillingAddressPropertyList = pBillingAddressPropertyList;
    }

    /** Returns the BillingAddressPropertyList.
     * 
     * @return a List that contains the Address properties that are available The address property list. */
    public final List<String> getBillingAddressPropertyList() {
        return this.mBillingAddressPropertyList;
    }



    /** @return The value of the property EditValue. This is a map that stores the pending values for an editing
     *         operations on the B2CStore profile. map storing edit value for the B2CStore profile. */
    public final Map<String, String> getBillAddrValue() {
        return this.mBillAddrValue;
    }

    /** @return required billing address property list. */
    public final List<String> getRequiredBillingAddressPropertyList() {
        return this.mRequiredBillingAddressPropertyList;
    }

    /** @param pRequiredBillingAddressPropertyList required billing address property list. */
    public final void setRequiredBillingAddressPropertyList(final List<String> pRequiredBillingAddressPropertyList) {
        this.mRequiredBillingAddressPropertyList = pRequiredBillingAddressPropertyList;
    }
	
	/**
	 * @return the srcAppliedCoupons
	 */
	public List<RepositoryItem> getSrcAppliedCoupons() {
		return srcAppliedCoupons;
	}

	/**
	 * @param srcAppliedCoupons the srcAppliedCoupons to set
	 */
	public void setSrcAppliedCoupons(List<RepositoryItem> srcAppliedCoupons) {
		this.srcAppliedCoupons = srcAppliedCoupons;
	}
	/**
	 * @return the writeReviewSuccessURL
	 */
	public String getWriteReviewSuccessURL() {
		return writeReviewSuccessURL;
	}
	

	/**
	 * @param writeReviewSuccessURL the writeReviewSuccessURL to set
	 */
	public void setWriteReviewSuccessURL(String writeReviewSuccessURL) {
		this.writeReviewSuccessURL = writeReviewSuccessURL;
	}

    
    public boolean isEmailOptInSharedSite() {
		return emailOptInSharedSite;
	}

	public void setEmailOptInSharedSite(boolean emailOptInSharedSite) {
		this.emailOptInSharedSite = emailOptInSharedSite;
	}


	public boolean isExtendEmailOptn() {
		return extendEmailOptn;
	}

	public void setExtendEmailOptn(boolean extendEmailOptn) {
		this.extendEmailOptn = extendEmailOptn;
	}


	class SendTibcoEmailNewThread implements Runnable{
    	
    	private Object tibcoEmailObj; 
    	public SendTibcoEmailNewThread(Object emailParams) {
    		this.tibcoEmailObj=emailParams;
    	}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			long startTime = System.currentTimeMillis();
			logInfo("Start time recorded as " + startTime);
			logInfo("Name of the current thread : " + Thread.currentThread().getId());
			 try {
				getProfileManager().sendProfileRegistrationTibcoEmail((Map<String, Object>)tibcoEmailObj);
			} catch (BBBSystemException e) {
				if (isLoggingError()) {
				  logError(LogMessageFormatter.formatMessage(null, BBBCoreConstants.ERR_PROFILE_SEND_EMAIL,
		                    BBBCoreErrorConstants.ACCOUNT_ERROR_1123), e);
				}
				
			} catch (Exception e) {
				if (isLoggingError()) {
					logError(BBBCoreErrorConstants.CHECKOUT_ERROR_1009 + ": Error while sending Tibco Email messages", e);
				}
			}
			
			logInfo("Total Time of SendTibcoEmailNewThread.run() method:" + (System.currentTimeMillis() - startTime));
			
		}
    
    }   
    
	
}

