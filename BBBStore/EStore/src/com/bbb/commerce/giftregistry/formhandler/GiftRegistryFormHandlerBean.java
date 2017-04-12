package com.bbb.commerce.giftregistry.formhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import atg.commerce.util.RepeatingRequestMonitor;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.service.dynamo.LangLicense;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.BBBProfileTools;
import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.certona.vo.CertonaReqParameterVO;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean;
import com.bbb.commerce.giftregistry.manager.GiftRegistryRecommendationManager;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.ForgetRegPassRequestVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO;
import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.email.EmailHolder;
import com.bbb.kickstarters.manager.KickStarterManager;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.simplifyRegistry.manager.SimplifyRegistryManager;
import com.bbb.wishlist.BBBWishlistManager;
/**
 * 
 * @author kmagud 
 * Creating this class to hold variables, constants, getters & setters of GiftRegistryFormHandler and SimplifyRegFormHandler
 * This is done to refactor theGiftRegistryFormHandler class
 *
 */
public class GiftRegistryFormHandlerBean extends BBBProfileFormHandler {
	
	/**	Constant for String Incl. */
	protected static final String INCL = "Incl";
	/**	Constant for String itemPrice. */
	protected static final String ITEM_PRICE = "itemPrice";
	/**	Constant for String totalPrice. */
	protected static final String TOTAL_PRICE = "totalPrice";
	/**	Constant for String ltlDeliveryPrices. */
	protected static final String LTL_DELIVERY_PRICES = "ltlDeliveryPrices";
	/**	Constant for String ltlDeliveryServicesDesc. */
	protected static final String LTL_DELIVERY_SERVICES_DESC = "ltlDeliveryServicesDesc";
	/**	Constant for String lbl_email_co_registry_subject. */
	protected static final String LBL_EMAIL_CO_REGISTRY_SUBJECT = "lbl_email_co_registry_subject";
	/**	Constant for String defaultCountry. */
	protected static final String DEFAULT_COUNTRY = "defaultCountry";
	/**	Constant for String registryId. */
	protected static final String REGISTRY_ID = "?registryId=";
	/**	Constant for String eventType. */
	protected static final String EVENT_TYPE = "&eventType=";
	/**	Constant for String messageCC. */
	protected static final String MESSAGE_CC = "messageCC";
	/**	Constant for String wishlist. */
	protected static final String WISH_LIST = "wishlist";
	/**	Constant for String userSiteItems. */
	protected static final String USER_SITE_ITEMS = "userSiteItems";
	/**	Constant for String error invalid profileId. */
	protected static final String ERR_CREATE_REG_INVALID_PROFILEID = "err_invalid_profileId";
	/**	Constant for String error create registry invalid site. */
	protected static final String ERR_INVALID_USER_SITE_ASSOCIATION = "err_create_reg_invalid_site";
	/**	Constant for String source. */
	protected static final String SOURCE = "source";
	/**	Constant for String registry summary. */
	protected static final String REG_SUMMARY_KEY_CONST = "_REG_SUMMARY";
	/**	Constant for String inputString. */
	public static final String INPUT_STRING="inputString";
	/**	Constant for String inputString is null. */
	public static final String INPUT_STRING_IS_NULL="InputString is null";
	/**	Constant for String invalid Data for InputString. */
	public static final String INVALID_DATA_FOR_INPUT_STRING="invalid Data for InputString";
	/**	Constant for String newShippingAddress. */
	public static final String NEW_SHIPPING_ADDRESS = "newShippingAddress";
	/**	Constant for String newFutureShippingAddress. */
	public static final String NEW_FUTURE_SHIPPING_ADDRESS = "newFutureShippingAddress";
	/**	Constant for String web service error code. */
	public static final String WEB_SERVICE_ERROR_CODE = "GiftRegistryFormHandler | webservice error code=";
	/**	Constant for String invalid phone no from validate college. */
	public static final String INVALID_PHONENO_FROM_VALIDATECOLLEGE = "GiftRegistry Invalid registrant phone number from validateCollege of GiftRegistryFormHandler";
	
	
	/**	This is the string of recommededFlag.*/
	private String recommededFlag = BBBCoreConstants.FALSE;
	/**	This is the string to hold inputString.*/
	private String inputString;
	/**	This is the string to hold emailOptIn.*/
	private String emailOptIn;
	/**	This is the flag for errorFlagEmailOptIn.*/
	private boolean errorFlagEmailOptIn;
	/**	This is the string to hold buyoffStartBrowsingSuccessURL.*/
	private String buyoffStartBrowsingSuccessURL;
	/**	This is the Instance of profileRepository.*/
	private MutableRepository profileRepository;
	/**	This is the string to hold ltlDeliveryServices.*/
	private String ltlDeliveryServices;
	/**	This is the string to hold ltlDeliveryPrices.*/
	private String ltlDeliveryPrices;
	/**	This is the string to hold alternateNumber.*/
	private String alternateNumber;
	/**	This is the flag for updateDslFromModal.*/
	private boolean updateDslFromModal;
	/**	This is the string to hold itemPrice.*/
	private String itemPrice;
	/**	This is the string to hold userFullName.*/
	private String userFullName;
	/**	This is the string to hold verifyUserSuccessURLPage.*/
	private String verifyUserSuccessURLPage;
	/**	This is the string to hold verifyUserSuccessURLPage.*/
	private String verifyUserErrorURLPage;
	/**	This is the flag for migrationFlag.*/
	private boolean migrationFlag;
	/**	This is the string to hold ViewEditFailureURL.*/
	private String mViewEditFailureURL;
	/**	This is the string to hold ViewEditSuccessURL.*/
	private String mViewEditSuccessURL;
	/**	This is the Instance of userProfileTools.*/
	private BBBProfileTools userProfileTools;
	/**	This is the Instance of giftRegistryRecommendationManager.*/
	private GiftRegistryRecommendationManager giftRegistryRecommendationManager;
	/**	This is the Instance of GiftRegistryTools.*/
	private GiftRegistryTools giftRegistryTools;
	/**	This is the Instance of RepositoryItem.*/
	private RepositoryItem reposityItem;
	/**	This is the string to hold registryItemsListServiceName.*/
	private String registryItemsListServiceName;
	/**	This is the Instance of RegistryResVO.*/
	private RegistryResVO createRegistryResVO;
	/**	This is the Instance of BBBEximManager.*/
	private BBBEximManager eximPricingManager;
	/**	This is the list of string to hold customizedSkus.*/
	private List<String> customizedSkus;
	/**	This is the string to hold customizationReq.*/
	private String customizationReq;
	/**	This is the string to hold personalizationCode.*/
	private String personalizationCode;
	/**	This is the Map to hold mTbsEmailSiteMap.*/
	private Map<String, String> mTbsEmailSiteMap = new HashMap<String, String>();
	/**	This is the flag for removeSingleItemFlag.*/
	private boolean removeSingleItemFlag=true;
	/**	This is the flag for isItemAddedToRegistry.*/
	private boolean isItemAddedToRegistry;
	/**	This is the instance for CertonaReqParameterVO.*/
	private CertonaReqParameterVO certonaParameter;
	/**	This is the string to hold copyRegistryServiceName.*/
	private String copyRegistryServiceName;
	/**	This is the Instance of AddressVO for registrantAddressFromWS.*/	 
	private AddressVO registrantAddressFromWS;
	/**	This is the Instance of AddressVO for shippingAddressFromWS.*/
	private AddressVO shippingAddressFromWS;
	/**	This is the Instance of AddressVO for futureShippingAddressFromWS.*/
	private AddressVO futureShippingAddressFromWS;
	/**	This is the Instance for Profile.*/
	private Profile mProfile;
	/**	This is the Instance for RegistryVO.*/
	private RegistryVO mRegistryVO;
	/**	This is the Instance for RegistryItemsListVO.*/
	private RegistryItemsListVO mRegistryItemsListVO;
	/**	This is the Instance for ForgetRegPassRequestVO.*/
	private ForgetRegPassRequestVO mForgetRegPassRequestVO;
	/**	This is the Instance for GiftRegSessionBean.*/
	private GiftRegSessionBean giftRegSessionBean;
	/**	This is the Instance for EmailHolder.*/
	private EmailHolder emailHolder;
	/**	This is the Instance for TemplateEmailInfoImpl.*/
	private TemplateEmailInfoImpl mGiftRegEmailInfo;
	/**	This is the list of GiftRegistryViewBean to hold mViewBeans.*/
	private List<GiftRegistryViewBean> mViewBeans = new ArrayList<GiftRegistryViewBean>(1);
	/**	This is the list of GiftRegistryViewBean to hold mModifiedViewBeans.*/
	private List<GiftRegistryViewBean> mModifiedViewBeans;
	/**	This is the list of RegistrySummaryVO to hold mRegistrySearchResults.*/
	private List<RegistrySummaryVO> mRegistrySearchResults;
	/**	This is the map to hold movedItemMap.*/
	private Map<String, String> movedItemMap = new HashMap<String, String>();
	
	
	/**	This is the string to hold skuId.*/
	private String skuId;
	/**	This is the string to hold updateQuantity.*/
	private String updateQuantity;
	/**	This is the string to hold updateRegistryId.*/
	private String updateRegistryId;
	/**	This is the string to hold regItemOldQty.*/
	private String regItemOldQty;
	/**	This is the string to hold purchasedQuantity.*/
	private String purchasedQuantity;
	/**	This is the string to hold rowId.*/
	private String rowId;
	/**	This is the string to hold itemTypes.*/
	private String itemTypes;
	/**	This is the string to hold movedItemIndex.*/
	private String movedItemIndex;
	/**	This is the string to hold movedItemRegistryId.*/
	private String movedItemRegistryId;
	/**	This is the string to hold modifiedItemIndex.*/
	private String modifiedItemIndex;
	/**	This is the string to hold modifiedItemQuantity.*/
	private String modifiedItemQuantity;
	/**	This is the string to hold moveToRegistryResponseURL.*/
	private String moveToRegistryResponseURL;
	/**	This is the string to hold totalPrice.*/
	private String totalPrice;
	/**	This is the string to hold registryURL.*/
	private String registryURL;
	/**	This is the string to hold recipientEmail.*/
	private String recipientEmail;
	/**	This is the string to hold message.*/
	private String message;
	/**	This is the string to hold senderEmail.*/
	private String senderEmail;
	/**	This is the string to hold eventType.*/
	private String eventType;
	/**	This is the string to hold regFirstName.*/
	private String regFirstName;
	/**	This is the string to hold coRegFirstName.*/
	private String coRegFirstName;
	/**	This is the string to hold regLastName.*/
	private String regLastName;
	/**	This is the string to hold coRegLastName.*/
	private String coRegLastName;
	/**	This is the string to hold eventDate.*/
	private String eventDate;
	/**	This is the string to hold registryName.*/
	private String registryName;
	/**	This is the string to hold subject.*/
	private String subject;
	/**	This is the string to hold title.*/
	private String title;
	/**	This is the string to hold registryEventDate.*/
	private String registryEventDate;
	/**	This is the string to hold dateLabel.*/
	private String dateLabel;
	/**	This is the string to hold skuIds.*/
	private String skuIds;
	/**	This is the string to hold size.*/
	private String size;
	/**	This is the string to hold removedProductId.*/
	private String removedProductId;
	/**	This is the string to hold productStringAddItemCertona.*/
	private String productStringAddItemCertona;
	/**	This is the string to hold mAddItemsToRegServiceName.*/
	private String mAddItemsToRegServiceName;
	/**	This is the string to hold addItemsToReg2ServiceName.*/
	private String addItemsToReg2ServiceName;
	/**	This is the string to hold mCreateRegistryServiceName.*/
	private String mCreateRegistryServiceName;
	/**	This is the string to hold mSearchRegistryServiceName.*/
	private String mSearchRegistryServiceName;
	/**	This is the string to hold mUpdateRegItemsServiceName.*/
	private String mUpdateRegItemsServiceName;
	/**	This is the string to hold mUpdateRegServiceName.*/
	private String mUpdateRegServiceName;
	/**	This is the string to hold mImportRegServiceName.*/
	private String mImportRegServiceName;
	/**	This is the string to hold mForgetRegistryPasswordServiceName.*/
	private String mForgetRegistryPasswordServiceName;
	/**	This is the string to hold mAnnCardCountServiceName.*/
	private String mAnnCardCountServiceName;
	/**	This is the string to hold quantity.*/
	private String quantity;
	/**	This is the string to hold jasonCollectionObj.*/
	private String jasonCollectionObj;
	/**	This is the string to hold productId.*/
	private String productId;
	/**	This is the string to hold registryItemOperation.*/
	private String registryItemOperation;
	/**	This is the string to hold mSuccessURL.*/
	private String mSuccessURL;
	/**	This is the string to hold mImportURL.*/
	private String mImportURL;
	/**	This is the string to hold mRegistryEventType.*/
	private String mRegistryEventType;
	/**	This is the string to hold mRegistryId.*/
	private String mRegistryId;
	/**	This is the string to hold mRegistryIdEventType.*/
	private String mRegistryIdEventType;
	/**	This is the string to hold mErrorURL.*/
	private String mErrorURL;
	/**	This is the string to hold mRegContactAddress.*/
	private String mRegContactAddress;
	/**	This is the string to hold importErrorMessage.*/
	private String importErrorMessage;
	/**	This is the string to hold mShippingAddress.*/
	private String mShippingAddress;
	/**	This is the string to hold mFutureShippingDateSelected.*/
	private String mFutureShippingDateSelected;
	/**	This is the string to hold mFutureShippingAddress.*/
	private String mFutureShippingAddress;
	/**	This is the string to hold coRegEmailNotFoundPopupStatus.*/
	private String coRegEmailNotFoundPopupStatus;
	/**	This is the string to hold coRegEmailFoundPopupStatus.*/
	private String coRegEmailFoundPopupStatus;
	/**	This is the string to hold mRegistryPassword.*/
	private String mRegistryPassword;
	/**	This is the string to hold importEventType.*/
	private String importEventType;
	/**	This is the string to hold importEventType.*/
	private String importEventDate;
	/**	This is the string to hold forgotPasswordEmailId.*/
	private String forgotPasswordEmailId;
	/**	This is the string to hold mRegistryTypes.*/
	private String mRegistryTypes;
	/**	This is the string to hold registrySearchSuccessURL.*/
	private String registrySearchSuccessURL;
	/**	This is the string to hold registrySearchErrorURL.*/
	private String registrySearchErrorURL;
	/**	This is the string to hold registryCreationSuccessURL.*/
	private String registryCreationSuccessURL;
	/**	This is the string to hold registryCreationErrorURL.*/
	private String registryCreationErrorURL;
	/**	This is the string to hold registryUpdateSuccessURL.*/
	private String registryUpdateSuccessURL;
	/**	This is the string to hold registryUpdateErrorURL.*/
	private String registryUpdateErrorURL;
	/**	This is the string to hold sortSeq.*/
	private String sortSeq;
	/**	This is the string to hold forgetPasswordRegistryId.*/
	private String forgetPasswordRegistryId;
	/**	This is the string to hold loggedInFailureURL.*/
	private String loggedInFailureURL;
	/**	This is the string to hold captchaAnswer.*/
	private String captchaAnswer;
	/**	This is the string to hold registryImportSearchSuccessURL.*/
	private String registryImportSearchSuccessURL;
	/**	This is the string to hold registryImportSearchErrorURL.*/
	private String registryImportSearchErrorURL;
	/**	This is the string to hold registryGuestSearchSuccessURL.*/
	private String registryGuestSearchSuccessURL;
	/**	This is the string to hold babyLandingURL.*/
	private String babyLandingURL;
	/**	This is the string to hold bridalLandingURL.*/
	private String bridalLandingURL;
	/**	This is the string to hold registryFlyoutURL.*/
	private String registryFlyoutURL;
	/**	This is the string to hold noSearchResultURL.*/
	private String noSearchResultURL;
	/**	This is the string to hold homePageURL.*/
	private String homePageURL;
	/**	This is the string to hold htmlMessage.*/
	private String htmlMessage;
	
	
	/**	This is the flag for downloadFlag.*/
	private boolean downloadFlag = false;
	/**	This is the flag for isMoveItemFromSaveForLater.*/
	private boolean isMoveItemFromSaveForLater;
	/**	This is the flag for fromWishListPage.*/
	private boolean fromWishListPage;
	/**	This is the flag for ccFlag.*/
	private boolean ccFlag;
	/**	This is the flag for errorFlagAddItemToRegistry.*/
	private boolean errorFlagAddItemToRegistry;
	/**	This is the flag for validatedCaptcha.*/
	private boolean validatedCaptcha;
	/**	This is the long to hold daysToGo.*/
	private long daysToGo;
	/**	This is the integer to hold mHidden.*/
	private int mHidden;
	/**	This is the integer to hold mUpdateBatchSize.*/
	protected int mUpdateBatchSize;
	/**	This is the integer to hold mModifiedItemsCount.*/
	protected int mModifiedItemsCount;
	/**	This is the string to hold mKickStarterAddAllAction.*/
	private String mKickStarterAddAllAction;
	/** This is the instance for kickStarterManager. */
	private KickStarterManager mKickStarterManager;
	/** This is the string to hold mSrcRegistryId. */
	private String mSrcRegistryId;
	/** This is the string to hold mDstRegistryId. */
	private String mDstRegistryId;
	/** This is the string to hold comment. */
	private String comment;
	/** This is the string to hold recommendedQuantity. */
	private String recommendedQuantity;
	/** This is the flag for fromFacebook. */
	private boolean fromFacebook;
	/** This is the string to hold refNum. */
	private String refNum;
	/** This is the string to hold personalizationType. */
	private String personalizationType;
	/** This is the string to hold mFullName. */
	private String mFullName;
	/** This is the string to hold regId. */
   	private String regId;
   	/** This is the string to hold recomPopUpErrorURL. */
   	private String recomPopUpErrorURL;
	/** This is the string to hold recomPopUpSuccessURL. */
	private String recomPopUpSuccessURL;
	/** This is the string to hold loginRedirectUrl. */
	private String loginRedirectUrl;
	/** This is the string to hold guestRegistryUri. */
	private String guestRegistryUri;
	/** This is the string to hold emailRecommendationSuccessURL. */
	private String emailRecommendationSuccessURL;
	/** This is the string to hold emailRecommendationErrorURL. */
	private String emailRecommendationErrorURL;
	/** This is the string to hold successUrlAddItem. */
	private String successUrlAddItem;
	/** This is the string to hold successUrlImportRegistry. */
	private String successUrlImportRegistry;
	/** This is the string to hold compareSuccessUrl. */
	private String compareSuccessUrl;
	/** This is the Map to hold updatedItemInfoMap. */
	private Map<String, String> updatedItemInfoMap;
	/** This is the flag for same Shipping Address. */
	private Boolean mSameShippingAddress;
	/** This is the string to hold mCoRegistrant. */
	private String mCoRegistrant;
	/** This is the string to hold mAddress. */
	private String mAddress;
	/** This is the string to hold mMobile. */
	private String mMobile;
	/** This is the string to hold mWeddingDate. */
	private String mWeddingDate;
	/** This is the string to hold mGuestsNumber. */
	private String mGuestsNumber;
	/** This is the string to hold mEmail. */
	private String mEmail;
	/** This is the string to hold regBG. */
	private String regBG;
	/** This is the string to hold coRegBG. */
	private String coRegBG;
	/** This is the Instance for repeatingRequestMonitor. */
	private RepeatingRequestMonitor repeatingRequestMonitor;
	/** This is the Instance for ResourceBundle. */
	protected static ResourceBundle sResourceBundle = ResourceBundle.getBundle("atg.userprofiling.ProfileResources", LangLicense.getLicensedDefault());
	/** This is the string to hold wishListItemId. */
	private String wishListItemId;
	/** This is the string to hold alternateNum. */
	private String alternateNum;
	/** This is the map to hold MoveAllWishListItemsFailureResult. */
	private Map<String, String> mMoveAllWishListItemsFailureResult;
	/** This is the string to hold recommenderProfileId. */
	private String recommenderProfileId;
	/** This is the string to hold requestedFlag. */
	private String requestedFlag;
	/** This is the string to hold toggleSuccessURL. */
	private String toggleSuccessURL;
	/** This is the string to hold toggleFailureURL. */
	private String toggleFailureURL;
	/** This is the string to hold contactPoBoxFlag. */
	private String contactPoBoxFlag;
	/** This is the string to hold contactPoBoxStatus. */
    private String contactPoBoxStatus;
    /** This is the string to hold shipPoBoxFlag. */
    private String shipPoBoxFlag;
    /** This is the string to hold shipPoBoxStatus. */
    private String shipPoBoxStatus;
    /** This is the string to hold futurePoBoxFlag. */
    private String futurePoBoxFlag;
    /** This is the string to hold futurePoBoxStatus. */
    private String futurePoBoxStatus;
    /** This is the string to hold undoSuccessURL. */
    private String undoSuccessURL;
    /** This is the string to hold undoFailureURL. */
	private String undoFailureURL;
	/** This is the string to hold undoFrom. */
	private String undoFrom;
	/** This is the Instance for siteRepository. */
	private Repository siteRepository;
	/** This is the Instance for BBBWishlistManager. */
	private BBBWishlistManager mwishlistManager;
	/** This is the Instance for BBBPropertyManager. */
	private BBBPropertyManager propertyManger;
	/** This is the string to hold defaultRefNum. */
	private String defaultRefNum;
	/** This is the string to hold babygenderStr. */
	private  String babygenderStr;
	/** This is the string to hold createSimplified. */
	private String createSimplified;
	/** This is the string to hold updateSimplified. */
	private String updateSimplified;
	/** This is the flag for poBoxAddress. */
	private boolean poBoxAddress;
	/** This is the integer for totalGiftRegistered. */
	private int totalGiftRegistered;
	/** This is the string to hold desktop. */
    private String desktop;
    /** This is the string to hold hoorayModal. */
	private String hoorayModal;
	/** This is the string to hold successQueryParam. */
	private String successQueryParam;
	/** This is the string to hold fromPage. */
	private String fromPage;
	/** This is the string to hold errorQueryParam. */
	private String errorQueryParam;
	/** This is the string to hold addedItemPrice. */
	private String addedItemPrice;
	/** This is the string to hold createRegistryUri. */
	private String createRegistryUri;
	/** This is the Map to hold regErrorMap. */
	private Map<String, String> regErrorMap;
	/** This is the Instance for LblTxtTemplateManager. */
	private LblTxtTemplateManager lblTxtTemplateManager;
	/** This is the string to hold ltlFlag. */
	private String ltlFlag;
	/** This is the string to hold makeRegistryPublic. */
	private String makeRegistryPublic;
	/** This is the string to hold deactivateRegistry. */
	private String deactivateRegistry;
	/** Query Parameter that will be appended to success/error URL. */
	private String queryParam;
	/** This is the Instance for SimplifyRegistryManager. */
	private SimplifyRegistryManager simplifyRegistryManager;


	/**
	 * @return the regBG
	 */
	public String getRegBG() {
		return regBG;
	}
	/**
	 * @param regBG the regBG to set
	 */
	public void setRegBG(String regBG) {
		this.regBG = regBG;
	}
	/**
	 * @return the coRegBG
	 */
	public String getCoRegBG() {
		return coRegBG;
	}
	/**
	 * @param coRegBG the coRegBG to set
	 */
	public void setCoRegBG(String coRegBG) {
		this.coRegBG = coRegBG;
	}
	/**
	 * Gets the error url.
	 * 
	 * @return the errorURL
	 */
	public String getErrorURL() {
		return this.mErrorURL;
	}
	/**
	 * Sets the error url.
	 * 
	 * @param pErrorURL
	 *            the errorURL to set
	 */
	public void setErrorURL(final String pErrorURL) {
		this.mErrorURL = pErrorURL;
	}
	/**
	 * Gets the registry event type.
	 * 
	 * @return the registryEventType
	 */
	public String getRegistryEventType() {
		return this.mRegistryEventType;
	}
	/**
	 * Sets the registry event type.
	 * 
	 * @param pRegistryEventType
	 *            the registryEventType to set
	 */
	public void setRegistryEventType(final String pRegistryEventType) {
		this.mRegistryEventType = pRegistryEventType;
	}
	/**
	 * Gets the success url.
	 * 
	 * @return the successURL
	 */
	public String getSuccessURL() {
		return this.mSuccessURL;
	}
	/**
	 * Sets the success url.
	 * 
	 * @param pSuccessURL
	 *            the successURL to set
	 */
	public void setSuccessURL(final String pSuccessURL) {
		this.mSuccessURL = pSuccessURL;
	}
	/**
	 * Gets the registry types.
	 * 
	 * @return the registryTypes
	 */

	public String getRegistryTypes() {
		return this.mRegistryTypes;
	}
	/**
	 * Sets the registry types.
	 * 
	 * @param pRegistryTypes
	 *            the registryTypes to set
	 */
	public void setRegistryTypes(final String pRegistryTypes) {
		this.mRegistryTypes = pRegistryTypes;
	}
	/**
	 * Gets the reg contact address.
	 * 
	 * @return the regContactAddress
	 */
	public String getRegContactAddress() {
		return this.mRegContactAddress;
	}
	/**
	 * Sets the reg contact address.
	 * 
	 * @param pRegContactAddress
	 *            the regContactAddress to set
	 */
	public void setRegContactAddress(final String pRegContactAddress) {
		this.mRegContactAddress = pRegContactAddress;
	}
	/**
	 * Gets the shipping address.
	 * 
	 * @return the shippingAddress
	 */
	public String getShippingAddress() {
		return this.mShippingAddress;
	}
	/**
	 * Sets the shipping address.
	 * 
	 * @param pShippingAddress
	 *            the shippingAddress to set
	 */
	public void setShippingAddress(final String pShippingAddress) {
		this.mShippingAddress = pShippingAddress;
	}
	/**
	 * Gets the future shipping date selected.
	 * 
	 * @return the futureShuppingDateSelected
	 */
	public String getFutureShippingDateSelected() {
		return this.mFutureShippingDateSelected;
	}
	/**
	 * Sets the future shipping date selected.
	 * 
	 * @param pFutureShuppingDateSelected
	 *            the futureShuppingDateSelected to set
	 */
	public void setFutureShippingDateSelected(
			final String pFutureShuppingDateSelected) {
		this.mFutureShippingDateSelected = pFutureShuppingDateSelected;
	}
	/**
	 * Gets the future shipping address.
	 * 
	 * @return the futureShuppingAddress
	 */
	public String getFutureShippingAddress() {
		return this.mFutureShippingAddress;
	}
	/**
	 * Sets the future shipping address.
	 * 
	 * @param pFutureShuppingAddress
	 *            the futureShuppingAddress to set
	 */
	public void setFutureShippingAddress(final String pFutureShuppingAddress) {
		this.mFutureShippingAddress = pFutureShuppingAddress;
	}
	/**
	 * Gets the co reg email not found popup status.
	 * 
	 * @return the coRegEmailNotFoundPopupStatus
	 */
	public String getCoRegEmailNotFoundPopupStatus() {
		return this.coRegEmailNotFoundPopupStatus;
	}
	/**
	 * Sets the co reg email not found popup status.
	 * 
	 * @param pCoRegEmailNotFoundPopupStatus
	 *            the coRegEmailNotFoundPopupStatus to set
	 */
	public void setCoRegEmailNotFoundPopupStatus(
			final String pCoRegEmailNotFoundPopupStatus) {
		this.coRegEmailNotFoundPopupStatus = pCoRegEmailNotFoundPopupStatus;
	}
	/**
	 * Gets the co reg email found popup status.
	 * 
	 * @return the coRegEmailFoundPopupStatus
	 */
	public String getCoRegEmailFoundPopupStatus() {
		return this.coRegEmailFoundPopupStatus;
	}
	/**
	 * Sets the co reg email found popup status.
	 * 
	 * @param pCoRegEmailFoundPopupStatus
	 *            the coRegEmailFoundPopupStatus to set
	 */
	public void setCoRegEmailFoundPopupStatus(
			final String pCoRegEmailFoundPopupStatus) {
		this.coRegEmailFoundPopupStatus = pCoRegEmailFoundPopupStatus;
	}
	/**
	 * Gets the registry id.
	 * 
	 * @return the registryId
	 */
	public String getRegistryId() {
		return this.mRegistryId;
	}
	/**
	 * Sets the registry id.
	 * 
	 * @param pRegistryId
	 *            the registryId to set
	 */
	public void setRegistryId(final String pRegistryId) {
		this.mRegistryId = pRegistryId;
	}
	/**
	 * Gets the sku ids.
	 * 
	 * @return the skuIds
	 */
	public String getSkuIds() {
		return this.skuIds;
	}
	/**
	 * Sets the sku ids.
	 * 
	 * @param pSkuIds
	 *            the skuIds to set
	 */
	public void setSkuIds(final String pSkuIds) {
		this.skuIds = pSkuIds;
	}
	/**
	 * Checks if is error flag add item to registry.
	 * 
	 * @return the errorFlagAddItemToRegistry
	 */
	public boolean isErrorFlagAddItemToRegistry() {
		return this.errorFlagAddItemToRegistry;
	}
	/**
	 * Sets the error flag add item to registry.
	 * 
	 * @param pErrorFlagAddItemToRegistry
	 *            the errorFlagAddItemToRegistry to set
	 */
	public void setErrorFlagAddItemToRegistry(
			final boolean pErrorFlagAddItemToRegistry) {
		this.errorFlagAddItemToRegistry = pErrorFlagAddItemToRegistry;
	}
	/**
	 * Gets the quantity.
	 * 
	 * @return the quantity
	 */
	public String getQuantity() {
		return this.quantity;
	}
	/**
	 * Sets the quantity.
	 * 
	 * @param pQuantity
	 *            the quantity to set
	 */
	public void setQuantity(final String pQuantity) {
		this.quantity = pQuantity;
	}
	/**
	 * Gets the product id.
	 * 
	 * @return the productId
	 */
	public String getProductId() {
		return this.productId;
	}
	/**
	 * Sets the product id.
	 * 
	 * @param pProductId
	 *            the productId to set
	 */
	public void setProductId(final String pProductId) {
		this.productId = pProductId;
	}
	/**
	 * Gets the registry password.
	 * 
	 * @return the registryPassword
	 */
	public String getRegistryPassword() {
		return this.mRegistryPassword;
	}
	/**
	 * Sets the registry password.
	 * 
	 * @param pRegistryPassword
	 *            the registryPassword to set
	 */
	public void setRegistryPassword(final String pRegistryPassword) {
		this.mRegistryPassword = pRegistryPassword;
	}
	/**
	 * Gets the import error message.
	 * 
	 * @return the importErrorMessage
	 */
	public String getImportErrorMessage() {
		return this.importErrorMessage;
	}
	/**
	 * Sets the import error message.
	 * 
	 * @param pImportErrorMessage
	 *            the importErrorMessage to set
	 */
	public void setImportErrorMessage(final String pImportErrorMessage) {
		this.importErrorMessage = pImportErrorMessage;
	}
	/**
	 * Gets the import event type.
	 * 
	 * @return the importEventType
	 */
	public String getImportEventType() {
		return this.importEventType;
	}
	/**
	 * Sets the import event type.
	 * 
	 * @param pImportEventType
	 *            the importEventType to set
	 */
	public void setImportEventType(final String pImportEventType) {
		this.importEventType = pImportEventType;
	}
	/**
	 * Gets the import event date.
	 * 
	 * @return the importEventDate
	 */
	public String getImportEventDate() {
		return this.importEventDate;
	}
	/**
	 * Sets the import event date.
	 * 
	 * @param pImportEventDate
	 *            the importEventDate to set
	 */
	public void setImportEventDate(final String pImportEventDate) {
		this.importEventDate = pImportEventDate;
	}
	/**
	 * Gets the forgot password email id.
	 * 
	 * @return the forgotPasswordEmailId
	 */
	public String getForgotPasswordEmailId() {
		return this.forgotPasswordEmailId;
	}
	/**
	 * Sets the forgot password email id.
	 * 
	 * @param pForgotPasswordEmailId
	 *            the forgotPasswordEmailId to set
	 */
	public void setForgotPasswordEmailId(final String pForgotPasswordEmailId) {
		this.forgotPasswordEmailId = pForgotPasswordEmailId;
	}
	/**
	 * Gets the sort seq.
	 * 
	 * @return the sortSeq
	 */
	public String getSortSeq() {
		return this.sortSeq;
	}
	/**
	 * Sets the sort seq.
	 * 
	 * @param pSortSeq
	 *            the sortSeq to set
	 */
	public void setSortSeq(final String pSortSeq) {
		this.sortSeq = pSortSeq;
	}
	/**
	 * Gets the jason collection obj.
	 * 
	 * @return the jasonCollectionObj
	 */
	public String getJasonCollectionObj() {
		return this.jasonCollectionObj;
	}
	/**
	 * Sets the jason collection obj.
	 * 
	 * @param pJasonCollectionObj
	 *            the jasonCollectionObj to set
	 */
	public void setJasonCollectionObj(final String pJasonCollectionObj) {
		this.jasonCollectionObj = pJasonCollectionObj;
	}
	/**
	 * Gets the captcha answer.
	 * 
	 * @return the captchaAnswer
	 */
	public String getCaptchaAnswer() {
		return this.captchaAnswer;
	}
	/**
	 * Sets the captcha answer.
	 * 
	 * @param captchaAnswer
	 *            the captchaAnswer to set
	 */
	public void setCaptchaAnswer(final String captchaAnswer) {
		this.captchaAnswer = captchaAnswer;
	}
	/**
	 * Gets the forget password registry id.
	 * 
	 * @return the forgetPasswordRegistryId
	 */
	public String getForgetPasswordRegistryId() {
		return this.forgetPasswordRegistryId;
	}
	/**
	 * Sets the forget password registry id.
	 * 
	 * @param pForgetPasswordRegistryId
	 *            the forgetPasswordRegistryId to set
	 */
	public void setForgetPasswordRegistryId(
			final String pForgetPasswordRegistryId) {
		this.forgetPasswordRegistryId = pForgetPasswordRegistryId;
	}
	/**
	 * Checks if is validate captcha.
	 * 
	 * @return the validateCaptcha
	 */
	public boolean isValidatedCaptcha() {
		return this.validatedCaptcha;
	}
	/**
	 * Sets the validate captcha.
	 * 
	 * @param validateCaptcha
	 *            the validateCaptcha to set
	 */
	public void setValidatedCaptcha(final boolean validatedCaptcha) {
		this.validatedCaptcha = validatedCaptcha;
	}
	/**
	 * Gets the creates the registry service name.
	 * 
	 * @return the creates the registry service name
	 */
	public String getCreateRegistryServiceName() {
		return this.mCreateRegistryServiceName;
	}
	/**
	 * Sets the creates the registry service name.
	 * 
	 * @param pCreateRegistryServiceName
	 *            the new creates the registry service name
	 */
	public void setCreateRegistryServiceName(
			final String pCreateRegistryServiceName) {
		this.mCreateRegistryServiceName = pCreateRegistryServiceName;
	}
	/**
	 * Gets the search registry service name.
	 * 
	 * @return the searchRegistryServiceName
	 */
	public String getSearchRegistryServiceName() {
		return this.mSearchRegistryServiceName;
	}
	/**
	 * Sets the search registry service name.
	 * 
	 * @param searchRegistryServiceName
	 *            the searchRegistryServiceName to set
	 */
	public void setSearchRegistryServiceName(
			final String searchRegistryServiceName) {
		this.mSearchRegistryServiceName = searchRegistryServiceName;
	}
	/**
	 * Gets the adds the items to reg service name.
	 * 
	 * @return the mAddItemsToRegServiceName
	 */
	public String getAddItemsToRegServiceName() {
		return this.mAddItemsToRegServiceName;
	}
	/**
	 * Sets the adds the items to reg service name.
	 * 
	 * @param pAddItemsToRegServiceName
	 *            the new adds the items to reg service name
	 */
	public void setAddItemsToRegServiceName(
			final String pAddItemsToRegServiceName) {
		this.mAddItemsToRegServiceName = pAddItemsToRegServiceName;
	}
	/**
	 * Gets the update reg items service name.
	 * 
	 * @return the mUpdateRegItemsServiceName
	 */
	public String getUpdateRegItemsServiceName() {
		return this.mUpdateRegItemsServiceName;
	}
	/**
	 * Sets the update reg items service name.
	 * 
	 * @param pUpdateRegItemsServiceName
	 *            the new update reg items service name
	 */
	public void setUpdateRegItemsServiceName(
			final String pUpdateRegItemsServiceName) {
		this.mUpdateRegItemsServiceName = pUpdateRegItemsServiceName;
	}
	/**
	 * Gets the update reg service name.
	 * 
	 * @return the mUpdateRegServiceName
	 */
	public String getUpdateRegServiceName() {
		return this.mUpdateRegServiceName;
	}
	/**
	 * Sets the update reg service name.
	 * 
	 * @param pUpdateRegServiceName
	 *            the new update reg service name
	 */
	public void setUpdateRegServiceName(final String pUpdateRegServiceName) {
		this.mUpdateRegServiceName = pUpdateRegServiceName;
	}
	/**
	 * Gets the import reg service name.
	 * 
	 * @return the mImportRegServiceName
	 */
	public String getImportRegServiceName() {
		return this.mImportRegServiceName;
	}
	/**
	 * Sets the import reg service name.
	 * 
	 * @param pImportRegServiceName
	 *            the new import reg service name
	 */
	public void setImportRegServiceName(final String pImportRegServiceName) {
		this.mImportRegServiceName = pImportRegServiceName;
	}
	/**
	 * Gets the forget registry password service name.
	 * 
	 * @return the mForgetRegistryPasswordServiceName
	 */
	public String getForgetRegistryPasswordServiceName() {
		return this.mForgetRegistryPasswordServiceName;
	}
	/**
	 * Sets the forget registry password service name.
	 * 
	 * @param pForgetRegistryPasswordServiceName
	 *            the new forget registry password service name
	 */
	public void setForgetRegistryPasswordServiceName(
			final String pForgetRegistryPasswordServiceName) {
		this.mForgetRegistryPasswordServiceName = pForgetRegistryPasswordServiceName;
	}
	/**
	 * Gets the ann card count service name.
	 * 
	 * @return the mAnnCardCountServiceName
	 */
	public String getAnnCardCountServiceName() {
		return this.mAnnCardCountServiceName;
	}
	/**
	 * Sets the ann card count service name.
	 * 
	 * @param pAnnCardCountServiceName
	 *            the new ann card count service name
	 */
	public void setAnnCardCountServiceName(final String pAnnCardCountServiceName) {
		this.mAnnCardCountServiceName = pAnnCardCountServiceName;
	}
	/**
	 * Gets the registry item operation.
	 * 
	 * @return the registry item operation
	 */
	public String getRegistryItemOperation() {
		return this.registryItemOperation;
	}
	/**
	 * Sets the registry item operation.
	 * 
	 * @param registryItemOperation
	 *            the new registry item operation
	 */
	public void setRegistryItemOperation(final String registryItemOperation) {
		this.registryItemOperation = registryItemOperation;
	}
	
	/**
	 * @return the mViewBeans
	 */
	public List<GiftRegistryViewBean> getViewBeans() {
		return this.mViewBeans;
	}
	/**
	 * @param mViewBeans the mViewBeans to set
	 */
	public void setViewBeans(final List<GiftRegistryViewBean> mViewBeans) {
		this.mViewBeans = mViewBeans;
	}
	/**
	 * @return the productStringAddItemCertona
	 */
	public String getProductStringAddItemCertona() {
		return this.productStringAddItemCertona;
	}
	/**
	 * @param productStringAddItemCertona the productStringAddItemCertona to set
	 */
	public void setProductStringAddItemCertona(
			final String productStringAddItemCertona) {
		this.productStringAddItemCertona = productStringAddItemCertona;
	}
	
	/**
	 * 
	 * @return the size
	 */
	public String getSize() {
		return this.size;
	}
	
	/**
	 * 
	 * @param pSize the pSize to set
	 */
	public void setSize(final String pSize) {
		this.size = pSize;
		if (pSize != null) {
			this.mViewBeans = new ArrayList<GiftRegistryViewBean>(
					Integer.parseInt(pSize));
	
			for (int i = 0; i < Integer.parseInt(pSize); i++) {
				this.mViewBeans.add(i, new GiftRegistryViewBean());
			}
		}
	}
	/**
	 * 
	 * @param mUpdateBatchSize the mUpdateBatchSize to set
	 */
	
	public void setUpdateBatchSize(final int mUpdateBatchSize) {
		this.mUpdateBatchSize = mUpdateBatchSize;
	}
	/**
	 * 
	 * @return the mModifiedItemsCount
	 */
	
	public int getModifiedItemsCount() {
		return this.mModifiedItemsCount;
	}
	/**
	 * 
	 * @param mModifiedItemsCount the mModifiedItemsCount to set
	 */
	
	public void setModifiedItemsCount(final int mModifiedItemsCount) {
		this.mModifiedItemsCount = mModifiedItemsCount;
	}
	/**
	 * 
	 * @return mModifiedViewBeans
	 */
	
	public List<GiftRegistryViewBean> getModifiedViewBeans() {
		return this.mModifiedViewBeans;
	}
	/**
	 * 
	 * @param mModifiedViewBeans the mModifiedViewBeans to set
	 */
	
	public void setModifiedViewBeans(
			final List<GiftRegistryViewBean> mModifiedViewBeans) {
		this.mModifiedViewBeans = mModifiedViewBeans;
	}
	
	/**
	 * 
	 * @return totalPrice
	 */
	public String getTotalPrice() {
		return this.totalPrice;
	}
	
	/**
	 * 
	 * @param totalPrice the totalPrice to set
	 */
	public void setTotalPrice(final String totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	/**
	 * @return the movedItemRegistryId
	 */
	public String getMovedItemRegistryId() {
		return this.movedItemRegistryId;
	}

	/**
	 * @param movedItemRegistryId
	 *            the movedItemRegistryId to set
	 */
	public void setMovedItemRegistryId(final String movedItemRegistryId) {
		this.movedItemRegistryId = movedItemRegistryId;
	}

	/**
	 * @return the movedItemMap
	 */
	public Map<String, String> getMovedItemMap() {
		return this.movedItemMap;
	}

	/**
	 * @param movedItemMap
	 *            the movedItemMap to set
	 */
	public void setMovedItemMap(final Map<String, String> movedItemMap) {
		this.movedItemMap = movedItemMap;
	}
	
	/**
	 * @return the movedItemIndex
	 */
	public String getMovedItemIndex() {
		return this.movedItemIndex;
	}

	/**
	 * @param movedItemIndex
	 *            the movedItemIndex to set
	 */
	public void setMovedItemIndex(final String movedItemIndex) {
		this.movedItemIndex = movedItemIndex;
	}
	/**
	 * 
	 * @return modifiedItemQuantity
	 */
	public String getModifiedItemQuantity() {
		return this.modifiedItemQuantity;
	}
	/**
	 * 
	 * @param modifiedItemQuantity the modifiedItemQuantity to set
	 */
	public void setModifiedItemQuantity(String modifiedItemQuantity) {
		this.modifiedItemQuantity = modifiedItemQuantity;
	}

	/**
	 * @return the fromWishListPage
	 */
	public boolean isFromWishListPage() {
		return this.fromWishListPage;
	}

	/**
	 * @param fromWishListPage
	 *            the fromWishListPage to set
	 */
	public void setFromWishListPage(final boolean fromWishListPage) {
		this.fromWishListPage = fromWishListPage;
	}

	/**
	 * @return the moveToRegistryResponseURL
	 */
	public String getMoveToRegistryResponseURL() {
		return this.moveToRegistryResponseURL;
	}

	/**
	 * @param moveToRegistryResponseURL
	 *            the moveToRegistryResponseURL to set
	 */
	public void setMoveToRegistryResponseURL(
			final String moveToRegistryResponseURL) {
		this.moveToRegistryResponseURL = moveToRegistryResponseURL;
	}

	/**
	 * @return the isMoveItemFromSaveForLater
	 */
	public boolean isMoveItemFromSaveForLater() {
		return this.isMoveItemFromSaveForLater;
	}

	/**
	 * @param pIsMoveItemFromSaveForLater
	 *            the isMoveItemFromSaveForLater to set
	 */
	public void setMoveItemFromSaveForLater(
			final boolean pIsMoveItemFromSaveForLater) {
		this.isMoveItemFromSaveForLater = pIsMoveItemFromSaveForLater;
	}
	/**
	 * 
	 * @return modifiedItemIndex
	 */
	public String getModifiedItemIndex() {
		return this.modifiedItemIndex;
	}
	/**
	 * 
	 * @param modifiedItemIndex the modifiedItemIndex to set
	 */
	public void setModifiedItemIndex(String modifiedItemIndex) {
		this.modifiedItemIndex = modifiedItemIndex;
	}

	/**
	 * @return the regItemOldQty
	 */
	public String getRegItemOldQty() {
		return this.regItemOldQty;
	}

	/**
	 * @param regItemOldQty
	 *            the regItemOldQty to set
	 */
	public void setRegItemOldQty(final String regItemOldQty) {
		this.regItemOldQty = regItemOldQty;
	}

	/**
	 * @return the purchasedQuantity
	 */
	public String getPurchasedQuantity() {
		return this.purchasedQuantity;
	}

	/**
	 * @param purchasedQuantity
	 *            the purchasedQuantity to set
	 */
	public void setPurchasedQuantity(final String purchasedQuantity) {
		this.purchasedQuantity = purchasedQuantity;
	}

	/**
	 * @return the rowId
	 */
	public String getRowId() {
		return this.rowId;
	}

	/**
	 * @param rowId
	 *            the rowId to set
	 */
	public void setRowId(final String rowId) {
		this.rowId = rowId;
	}

	/**
	 * @return the skuId
	 */
	public String getSkuId() {
		return this.skuId;
	}

	/**
	 * @param skuId
	 *            the skuId to set
	 */
	public void setSkuId(final String skuId) {
		this.skuId = skuId;
	}

	/**
	 * @return the updateQuantity
	 */
	public String getUpdateQuantity() {
		return this.updateQuantity;
	}

	/**
	 * @param updateQuantity
	 *            the updateQuantity to set
	 */
	public void setUpdateQuantity(final String updateQuantity) {
		this.updateQuantity = updateQuantity;
	}
	
	/**
	 * @return the updateRegistryId
	 */
	public String getUpdateRegistryId() {
		return this.updateRegistryId;
	}

	/**
	 * @param updateRegistryId
	 *            the updateRegistryId to set
	 */
	public void setUpdateRegistryId(final String updateRegistryId) {
		this.updateRegistryId = updateRegistryId;
	}

	/**
	 * @return the dateLabel
	 */
	public String getDateLabel() {
		return this.dateLabel;
	}

	/**
	 * @param dateLabel
	 *            the dateLabel to set
	 */
	public void setDateLabel(final String dateLabel) {
		this.dateLabel = dateLabel;
	}

	/**
	 * @return the registryEventDate
	 */
	public String getRegistryEventDate() {
		return this.registryEventDate;
	}

	/**
	 * @param registryEventDate
	 *            the registryEventDate to set
	 */
	public void setRegistryEventDate(final String registryEventDate) {
		this.registryEventDate = registryEventDate;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}
	
	/**
	 * @param coRegFirstName
	 *            the coRegFirstName to set
	 */
	public void setCoRegFirstName(final String coRegFirstName) {
		this.coRegFirstName = coRegFirstName;
	}

	/**
	 * @return the regLastName
	 */
	public String getRegLastName() {
		return this.regLastName;
	}

	/**
	 * @param regLastName
	 *            the regLastName to set
	 */
	public void setRegLastName(final String regLastName) {
		this.regLastName = regLastName;
	}

	/**
	 * @return the coRegLastName
	 */
	public String getCoRegLastName() {
		return this.coRegLastName;
	}

	/**
	 * @param coRegLastName
	 *            the coRegLastName to set
	 */
	public void setCoRegLastName(final String coRegLastName) {
		this.coRegLastName = coRegLastName;
	}

	/**
	 * @return the ccFlag
	 */
	public boolean isCcFlag() {
		return this.ccFlag;
	}

	/**
	 * @param ccFlag
	 *            the ccFlag to set
	 */
	public void setCcFlag(final boolean ccFlag) {
		this.ccFlag = ccFlag;
	}

	/**
	 * @return the registryURL
	 */
	public String getRegistryURL() {
		return this.registryURL;
	}

	/**
	 * @param registryURL
	 *            the registryURL to set
	 */
	public void setRegistryURL(final String registryURL) {
		this.registryURL = registryURL;
	}

	/**
	 * @return the recipientEmail
	 */
	public String getRecipientEmail() {
		return this.recipientEmail;
	}

	/**
	 * @param recipientEmail
	 *            the recipientEmail to set
	 */
	public void setRecipientEmail(final String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(final String message) {
		this.message = message;
	}

	/**
	 * @return the senderEmail
	 */
	public String getSenderEmail() {
		return this.senderEmail;
	}

	/**
	 * @param senderEmail
	 *            the senderEmail to set
	 */
	public void setSenderEmail(final String senderEmail) {
		this.senderEmail = senderEmail;
	}

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return this.eventType;
	}

	/**
	 * @param eventType
	 *            the eventType to set
	 */
	public void setEventType(final String eventType) {
		this.eventType = eventType;
	}
	/**
	 * 
	 * @return mRegistryIdEventType
	 */
	public String getRegistryIdEventType() {
		return this.mRegistryIdEventType;
	}
	/**
	 * 
	 * @param mRegistryIdEventType the mRegistryIdEventType to set
	 */
	public void setRegistryIdEventType(final String mRegistryIdEventType) {
		this.mRegistryIdEventType = mRegistryIdEventType;
	}
	
	/**
	 * Getter for loggedInFailureURL.
	 * 
	 * @return loggedInFailureURL
	 */
	public String getLoggedInFailureURL() {
		return this.loggedInFailureURL;
	}

	/**
	 * Setter for.
	 * 
	 * @param loggedInFailureURL the new logged in failure url
	 */
	public void setLoggedInFailureURL(final String loggedInFailureURL) {
		this.loggedInFailureURL = loggedInFailureURL;
	}

	/**
	 * @return removedProductId
	 */
	public String getRemovedProductId() {
		return this.removedProductId;
	}

	/**
	 * @param removedProductId
	 */
	public void setRemovedProductId(final String removedProductId) {
		this.removedProductId = removedProductId;
	}
	
	/**
	 * Gets the import url.
	 * 
	 * @return the importURL
	 */
	public String getImportURL() {
		return this.mImportURL;
	}

	/**
	 * Sets the import url.
	 * 
	 * @param pImportURL the importURL to set
	 */
	public void setImportURL(final String pImportURL) {
		this.mImportURL = pImportURL;
	}
	
	/**
	 * Gets the hidden.
	 * 
	 * @return mHidden
	 */
	public final int getHidden() {
		return this.mHidden;
	}

	/**
	 * Sets the hidden.
	 * 
	 * @param pHidden the new hidden
	 */
	public final void setHidden(final int pHidden) {
		this.mHidden = pHidden;
	}

	/**
	 * Gets the registry search success url.
	 * 
	 * @return registrySearchSuccessURL
	 */
	public final String getRegistrySearchSuccessURL() {
		return this.registrySearchSuccessURL;
	}

	/**
	 * Sets the registry search success url.
	 * 
	 * @param pRegistrySearchSuccessURL
	 *            the new registry search success url
	 */
	public final void setRegistrySearchSuccessURL(
			final String pRegistrySearchSuccessURL) {
		this.registrySearchSuccessURL = pRegistrySearchSuccessURL;
	}

	/**
	 * Gets the registry search error url.
	 * 
	 * @return registrySearchErrorURL
	 */
	public final String getRegistrySearchErrorURL() {
		return this.registrySearchErrorURL;
	}

	/**
	 * Sets the registry search error url.
	 * 
	 * @param pRegistrySearchErrorURL
	 *            the new registry search error url
	 */
	public final void setRegistrySearchErrorURL(
			final String pRegistrySearchErrorURL) {
		this.registrySearchErrorURL = pRegistrySearchErrorURL;
	}
	
	/**
	 * Gets the registry Creation error url.
	 * 
	 * @return registryCreationErrorURL
	 */
	public final String getRegistryCreationErrorURL() {
		return this.registryCreationErrorURL;
	}

	/**
	 * Sets the registry Creation error url.
	 * 
	 * @param pRegistryCreationErrorURL
	 *            the new registry Creation error url
	 */
	public final void setRegistryCreationErrorURL(
			final String pRegistryCreationErrorURL) {
		this.registryCreationErrorURL = pRegistryCreationErrorURL;
	}
	
	/**
	 * Gets the registry Creation success url.
	 * 
	 * @return registryCreationSuccessURL
	 */
	public final String getRegistryCreationSuccessURL() {
		return this.registryCreationSuccessURL;
	}

	/**
	 * Sets the registry creation success url.
	 * 
	 * @param registryCreationSuccessURL
	 *            the new registry creation success url
	 */
	public final void setRegistryCreationSuccessURL(
			final String registryCreationSuccessURL) {
		this.registryCreationSuccessURL = registryCreationSuccessURL;
	}
	
	/**
	 * Gets the registry Update error url.
	 * 
	 * @return registryUpdateErrorURL
	 */
	public final String getRegistryUpdateErrorURL() {
		return this.registryUpdateErrorURL;
	}

	/**
	 * Sets the registry Update error url.
	 * 
	 * @param pRegistryUpdateErrorURL
	 *            the new registry Update error url
	 */
	public final void setRegistryUpdateErrorURL(
			final String pRegistryUpdateErrorURL) {
		this.registryUpdateErrorURL = pRegistryUpdateErrorURL;
	}
	
	/**
	 * Gets the registry Update success url.
	 * 
	 * @return registryUpdateSuccessURL
	 */
	public final String getRegistryUpdateSuccessURL() {
		return this.registryUpdateSuccessURL;
	}

	/**
	 * Sets the registry Update success url.
	 * 
	 * @param registryUpdateSuccessURL
	 *            the new registry Update success url
	 */
	public final void setRegistryUpdateSuccessURL(
			final String registryUpdateSuccessURL) {
		this.registryUpdateSuccessURL = registryUpdateSuccessURL;
	}
	/**
	 * Gets the registry import search error url.
	 * 
	 * @return registryImportSearchErrorURL
	 */
	public String getRegistryImportSearchErrorURL() {
		return this.registryImportSearchErrorURL;
	}
	
	/**
	 * Sets the registry import search error url.
	 * 
	 * @param registryImportSearchErrorURL
	 *            the new registry import search error url
	 */
	public void setRegistryImportSearchErrorURL(final String registryImportSearchErrorURL) {
		this.registryImportSearchErrorURL = registryImportSearchErrorURL;
	}
	
	/**
	 * Gets the registry import search success url.
	 * 
	 * @return registryImportSearchSuccessURL
	 */
	public String getRegistryImportSearchSuccessURL() {
		return this.registryImportSearchSuccessURL;
	}
	
	/**
	 * Sets the registry import search success url.
	 * 
	 * @param registryImportSearchSuccessURL
	 *            the new registry import search success url
	 */
	public void setRegistryImportSearchSuccessURL(
			final String registryImportSearchSuccessURL) {
		this.registryImportSearchSuccessURL = registryImportSearchSuccessURL;
	}
	
	/**
	 * Gets the registry Guest search success url.
	 * 
	 * @return registryGuestSearchSuccessURL
	 */
	public String getRegistryGuestSearchSuccessURL() {
		return this.registryGuestSearchSuccessURL;
	}
	
	/**
	 * Sets the registry Guest search success url.
	 * 
	 * @param registryGuestSearchSuccessURL
	 *            the new registry Guest search success url
	 */
	public void setRegistryGuestSearchSuccessURL(
			final String registryGuestSearchSuccessURL) {
		this.registryGuestSearchSuccessURL = registryGuestSearchSuccessURL;
	}
	
	/**
	 * @return babyLandingURL
	 */
	public String getBabyLandingURL() {
		return this.babyLandingURL;
	}

	/**
	 * @param babyLandingURL
	 */
	public void setBabyLandingURL(final String babyLandingURL) {
		this.babyLandingURL = babyLandingURL;
	}
	
	/**
	 * Gets the registry search results.
	 * 
	 * @return mRegistrySearchResults
	 */
	public List<RegistrySummaryVO> getRegistrySearchResults() {
		return this.mRegistrySearchResults;
	}

	/**
	 * Sets the registry search results.
	 * 
	 * @param pRegistrySearchResults
	 *            the new registry search results
	 */
	public void setRegistrySearchResults(
			final List<RegistrySummaryVO> pRegistrySearchResults) {
		this.mRegistrySearchResults = pRegistrySearchResults;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return mEmail;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		mEmail = email;
	}
	/**
	 * @return the sameShippingAddress
	 */
	public Boolean getSameShippingAddress() {
		return mSameShippingAddress;
	}
	/**
	 * @param sameShippingAddress the sameShippingAddress to set
	 */
	public void setSameShippingAddress(Boolean sameShippingAddress) {
		mSameShippingAddress = sameShippingAddress;
	}
	/**
	 * @return the verifyUserErrorURLPage
	 */
	public String getVerifyUserErrorURLPage() {
		return verifyUserErrorURLPage;
	}
	/**
	 * @param verifyUserErrorURLPage the verifyUserErrorURLPage to set
	 */
	public void setVerifyUserErrorURLPage(String verifyUserErrorURLPage) {
		this.verifyUserErrorURLPage = verifyUserErrorURLPage;
	}
	/**
	 * @return the verifyUserSuccessURLPage
	 */
	public String getVerifyUserSuccessURLPage() {
		return verifyUserSuccessURLPage;
	}
	/**
	 * @param verifyUserSuccessURLPage the verifyUserSuccessURLPage to set
	 */
	public void setVerifyUserSuccessURLPage(String verifyUserSuccessURLPage) {
		this.verifyUserSuccessURLPage = verifyUserSuccessURLPage;
	}
	/**
	 * @return the userFullName
	 */
	public String getUserFullName() {
		return userFullName;
	}
	/**
	 * @param userFullName the userFullName to set
	 */
	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}
	/**
	 * 
	 * @return inputString
	 */
	public String getInputString() {
		return inputString;
	}
	/**
	 * 
	 * @param pInputString the pInputString to set
	 */
	public void setInputString(String pInputString) {
		inputString = pInputString;
	}
	/**
	 * 
	 * @return registryItemsListServiceName
	 */
	public String getRegistryItemsListServiceName() {
		return registryItemsListServiceName;
	}
	/**
	 * 
	 * @param pRegistryItemsListServiceName the pRegistryItemsListServiceName to set
	 */
	public void setRegistryItemsListServiceName(String pRegistryItemsListServiceName) {
		registryItemsListServiceName = pRegistryItemsListServiceName;
	}
	
	/**
	 * 
	 * @return customizedSkus
	 */
	public List<String> getCustomizedSkus() {
		return customizedSkus;
	}
	/**
	 * 
	 * @param pCustomizedSkus the pCustomizedSkus to set
	 */
	public void setCustomizedSkus(List<String> pCustomizedSkus) {
		customizedSkus = pCustomizedSkus;
	}
	/**
	 * 
	 * @return mTbsEmailSiteMap
	 */
	public Map<String, String> getTbsEmailSiteMap() {
		return mTbsEmailSiteMap;
	}
	/**
	 * 
	 * @param pTbsEmailSiteMap the pTbsEmailSiteMap to set
	 */
	public void setTbsEmailSiteMap(Map<String, String> pTbsEmailSiteMap) {
		mTbsEmailSiteMap = pTbsEmailSiteMap;
	}
	/**
	 * 
	 * @return removeSingleItemFlag
	 */
	public boolean isRemoveSingleItemFlag() {
		return removeSingleItemFlag;
	}
	/**
	 * 
	 * @param pRemoveSingleItemFlag the pRemoveSingleItemFlag to set
	 */
	public void setRemoveSingleItemFlag(boolean pRemoveSingleItemFlag) {
		removeSingleItemFlag = pRemoveSingleItemFlag;
	}
	/**
	 * 
	 * @return isItemAddedToRegistry
	 */
	public boolean isItemAddedToRegistry() {
		return isItemAddedToRegistry;
	}
	/**
	 * 
	 * @param pIsItemAddedToRegistry the pIsItemAddedToRegistry to set
	 */
	public void setItemAddedToRegistry(boolean pIsItemAddedToRegistry) {
		isItemAddedToRegistry = pIsItemAddedToRegistry;
	}
	/**
	 * 
	 * @return addItemsToReg2ServiceName
	 */
	public String getAddItemsToReg2ServiceName() {
		return addItemsToReg2ServiceName;
	}
	/**
	 * 
	 * @param pAddItemsToReg2ServiceName the pAddItemsToReg2ServiceName to set
	 */
	public void setAddItemsToReg2ServiceName(String pAddItemsToReg2ServiceName) {
		addItemsToReg2ServiceName = pAddItemsToReg2ServiceName;
	}
	/**
	 * 
	 * @return copyRegistryServiceName
	 */
	public String getCopyRegistryServiceName() {
		return copyRegistryServiceName;
	}
	/**
	 * 
	 * @param pCopyRegistryServiceName the pCopyRegistryServiceName to set
	 */
	public void setCopyRegistryServiceName(String pCopyRegistryServiceName) {
		copyRegistryServiceName = pCopyRegistryServiceName;
	}
	/**
	 * 
	 * @return mSrcRegistryId
	 */
	public String getSrcRegistryId() {
		return mSrcRegistryId;
	}
	/**
	 * 
	 * @param pSrcRegistryId the pSrcRegistryId to set
	 */
	public void setSrcRegistryId(String pSrcRegistryId) {
		mSrcRegistryId = pSrcRegistryId;
	}
	/**
	 * 
	 * @return mDstRegistryId
	 */
	public String getDstRegistryId() {
		return mDstRegistryId;
	}
	/**
	 * 
	 * @param pDstRegistryId the pDstRegistryId to set
	 */
	public void setDstRegistryId(String pDstRegistryId) {
		mDstRegistryId = pDstRegistryId;
	}
	/**
	 * 
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * 
	 * @param pComment the pComment to set
	 */
	public void setComment(String pComment) {
		comment = pComment;
	}
	
	/**
	 * @return the updatedItemInfoMap
	 */
	public Map<String, String> getUpdatedItemInfoMap() {
		return updatedItemInfoMap;
	}
	/**
	 * @param updatedItemInfoMap the updatedItemInfoMap to set
	 */
	public void setUpdatedItemInfoMap(Map<String, String> updatedItemInfoMap) {
		this.updatedItemInfoMap = updatedItemInfoMap;
	}
	
	/**
	 * @return the coRegistrant
	 */
	public String getCoRegistrant() {
		return mCoRegistrant;
	}
	/**
	 * @param coRegistrant the coRegistrant to set
	 */
	public void setCoRegistrant(String coRegistrant) {
		mCoRegistrant = coRegistrant;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return mAddress;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		mAddress = address;
	}
	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mMobile;
	}
	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		mMobile = mobile;
	}
	/**
	 * @return the weddingDate
	 */
	public String getWeddingDate() {
		return mWeddingDate;
	}
	/**
	 * @param weddingDate the weddingDate to set
	 */
	public void setWeddingDate(String weddingDate) {
		mWeddingDate = weddingDate;
	}
	/**
	 * @return the guestsNumber
	 */
	public String getGuestsNumber() {
		return mGuestsNumber;
	}
	
	/**
	 * @param guestsNumber the guestsNumber to set
	 */
	public void setGuestsNumber(String guestsNumber) {
		mGuestsNumber = guestsNumber;
	}
	/**
	 * 
	 * @return ltlDeliveryServices
	 */
	public String getLtlDeliveryServices() {
		return ltlDeliveryServices;
	}
	/**
	 * 
	 * @param pLtlDeliveryServices the pLtlDeliveryServices to set
	 */
	public void setLtlDeliveryServices(String pLtlDeliveryServices) {
		ltlDeliveryServices = pLtlDeliveryServices;
	}
	/**
	 * 
	 * @return ltlDeliveryPrices
	 */
	public String getLtlDeliveryPrices() {
		return ltlDeliveryPrices;
	}
	/**
	 * 
	 * @param pLtlDeliveryPrices the pLtlDeliveryPrices to set
	 */
	public void setLtlDeliveryPrices(String pLtlDeliveryPrices) {
		ltlDeliveryPrices = pLtlDeliveryPrices;
	}
	/**
	 * 
	 * @return alternateNumber
	 */
	public String getAlternateNumber() {
		return alternateNumber;
	}
	/**
	 * 
	 * @param pAlternateNumber the pAlternateNumber to set
	 */
	public void setAlternateNumber(String pAlternateNumber) {
		alternateNumber = pAlternateNumber;
	}
	/**
	 * 
	 * @return updateDslFromModal
	 */
	public boolean isUpdateDslFromModal() {
		return updateDslFromModal;
	}
	/**
	 * 
	 * @param pUpdateDslFromModal the pUpdateDslFromModal to set
	 */
	public void setUpdateDslFromModal(boolean pUpdateDslFromModal) {
		updateDslFromModal = pUpdateDslFromModal;
	}
	/**
	 * 
	 * @return itemPrice
	 */
	public String getItemPrice() {
		return itemPrice;
	}
	/**
	 * 
	 * @param pItemPrice the pItemPrice to set
	 */
	public void setItemPrice(String pItemPrice) {
		itemPrice = pItemPrice;
	}
	/**
	 * 
	 * @return migrationFlag
	 */
	public boolean isMigrationFlag() {
		return migrationFlag;
	}
	/**
	 * 
	 * @param pMigrationFlag the pMigrationFlag to set
	 */
	public void setMigrationFlag(boolean pMigrationFlag) {
		migrationFlag = pMigrationFlag;
	}
	/**
	 * @return the successUrlAddItem
	 */
	public String getSuccessUrlAddItem() {
		return successUrlAddItem;
	}
	/**
	 * @param successUrlAddItem the successUrlAddItem to set
	 */
	public void setSuccessUrlAddItem(String successUrlAddItem) {
		this.successUrlAddItem = successUrlAddItem;
	}
	/**
	 * @return refNum
	 */
	public String getRefNum() {
		return refNum;
	}
	/**
	 * @param refNum the refNum to set
	 */
	public void setRefNum(String refNum) {
		this.refNum = refNum;
	}
	/**
	 * @return personalizationType
	 */
	public String getPersonalizationType() {
		return personalizationType;
	}
	/**
	 * @param personalizationType the personalizationType to set
	 */
	public void setPersonalizationType(String personalizationType) {
		this.personalizationType = personalizationType;
	}
	/**
	 * 
	 * @return personalizationCode
	 */
	public String getPersonalizationCode() {
		return personalizationCode;
	}
	/**
	 * 
	 * @param personalizationCode the personalizationCode to set
	 */
	public void setPersonalizationCode(String personalizationCode) {
		this.personalizationCode = personalizationCode;
	}
	/**
	 * @return the loginRedirectUrl
	 */
	public String getLoginRedirectUrl() {
		return loginRedirectUrl;
	}
	/**
	 * @param loginRedirectUrl the loginRedirectUrl to set
	 */
	public void setLoginRedirectUrl(String loginRedirectUrl) {
		this.loginRedirectUrl = loginRedirectUrl;
	}
	/**
	 * @return the guestRegistryUri
	 */
	public String getGuestRegistryUri() {
		return guestRegistryUri;
	}
	/**
	 * @param guestRegistryUri the guestRegistryUri to set
	 */
	public void setGuestRegistryUri(String guestRegistryUri) {
		this.guestRegistryUri = guestRegistryUri;
	}
	/**
	 * @return the compareSuccessUrl
	 */
	public String getCompareSuccessUrl() {
		return compareSuccessUrl;
	}
	/**
	 * @param compareSuccessUrl the compareSuccessUrl to set
	 */
	public void setCompareSuccessUrl(String compareSuccessUrl) {
		this.compareSuccessUrl = compareSuccessUrl;
	}
	/**
	 * @return the successUrlImportRegistry
	 */
	public String getSuccessUrlImportRegistry() {
		return successUrlImportRegistry;
	}
	/**
	 * @param successUrlImportRegistry the successUrlImportRegistry to set
	 */
	public void setSuccessUrlImportRegistry(String successUrlImportRegistry) {
		this.successUrlImportRegistry = successUrlImportRegistry;
	}
	/**
	 * 
	 * @return recommendedQuantity
	 */
	public String getRecommendedQuantity() {
		return recommendedQuantity;
	}
	/**
	 * 
	 * @param pRecommendedQuantity the pRecommendedQuantity to set
	 */
	public void setRecommendedQuantity(String pRecommendedQuantity) {
		recommendedQuantity = pRecommendedQuantity;
	}
	/**
	 * 
	 * @return fromFacebook
	 */
	public boolean isFromFacebook() {
		return fromFacebook;
	}
	/**
	 * 
	 * @param pFromFacebook the pFromFacebook to set
	 */
	public void setFromFacebook(boolean pFromFacebook) {
		fromFacebook = pFromFacebook;
	}
	/**
	 * 
	 * @return regId
	 */
	public String getRegId() {
		return regId;
	}
	/**
	 * 
	 * @param pRegId the pRegId to set
	 */
	public void setRegId(String pRegId) {
		regId = pRegId;
	}
	/**
	 * 
	 * @return recomPopUpErrorURL
	 */
	public String getRecomPopUpErrorURL() {
		return recomPopUpErrorURL;
	}
	/**
	 * 
	 * @param pRecomPopUpErrorURL the pRecomPopUpErrorURL to set
	 */
	public void setRecomPopUpErrorURL(String pRecomPopUpErrorURL) {
		recomPopUpErrorURL = pRecomPopUpErrorURL;
	}
	/**
	 * @return mfullName
	 */
	public String getFullName() {
		return mFullName;
	}
	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		mFullName = fullName;
	}
	/**
	 * 
	 * @return recomPopUpSuccessURL
	 */
	public String getRecomPopUpSuccessURL() {
		return recomPopUpSuccessURL;
	}
	/**
	 * 
	 * @param pRecomPopUpSuccessURL the pRecomPopUpSuccessURL to set
	 */
	public void setRecomPopUpSuccessURL(String pRecomPopUpSuccessURL) {
		recomPopUpSuccessURL = pRecomPopUpSuccessURL;
	}
	
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(final String subject) {
		this.subject = subject;
	}

	/**
	 * @return the eventDate
	 */
	public String getEventDate() {
		return this.eventDate;
	}

	/**
	 * @return the daysToGo
	 */
	public long getDaysToGo() {
		return this.daysToGo;
	}

	/**
	 * @param daysToGo
	 *            the daysToGo to set
	 */
	public void setDaysToGo(final long daysToGo) {
		this.daysToGo = daysToGo;
	}

	/**
	 * @return the registryName
	 */
	public String getRegistryName() {
		return this.registryName;
	}

	/**
	 * @param registryName
	 *            the registryName to set
	 */
	public void setRegistryName(final String registryName) {
		this.registryName = registryName;
	}

	/**
	 * @return the regFirstName
	 */
	public String getRegFirstName() {
		return this.regFirstName;
	}

	/**
	 * @param regFirstName
	 *            the regFirstName to set
	 */
	public void setRegFirstName(final String regFirstName) {
		this.regFirstName = regFirstName;
	}

	/**
	 * @return the coRegFirstName
	 */
	public String getCoRegFirstName() {
		return this.coRegFirstName;
	}
	
	/**
	 * @return bridalLandingURL
	 */
	public String getBridalLandingURL() {
		return this.bridalLandingURL;
	}

	/**
	 * @param bridalLandingURL
	 */
	public void setBridalLandingURL(final String bridalLandingURL) {
		this.bridalLandingURL = bridalLandingURL;
	}

	/**
	 * @return registryFlyoutURL
	 */
	public String getRegistryFlyoutURL() {
		return this.registryFlyoutURL;
	}

	/**
	 * @param registryFlyoutURL
	 */
	public void setRegistryFlyoutURL(final String registryFlyoutURL) {
		this.registryFlyoutURL = registryFlyoutURL;
	}

	/**
	 * @return noSearchResultURL
	 */
	public String getNoSearchResultURL() {
		return this.noSearchResultURL;
	}

	/**
	 * @param noSearchResultURL
	 */
	public void setNoSearchResultURL(final String noSearchResultURL) {
		this.noSearchResultURL = noSearchResultURL;
	}
	
	/**
	 * @return homePageURL
	 */
	public String getHomePageURL() {
		return this.homePageURL;
	}

	/**
	 * @param homePageURL
	 */
	public void setHomePageURL(final String homePageURL) {
		this.homePageURL = homePageURL;
	}	
		
  /**
   * To get kick starter action.
   *  @return  mKickStarterAddAllAction -get kick starte item
   *
   **/
  public final String getKickStarterAddAllAction() {
      return this.mKickStarterAddAllAction;
  }

  /** 
   * To set kick starter action.
   *  @param pKickStarterAddAllAction - to set kick starter items 
   * 
   * */
  public final void setKickStarterAddAllAction(String pKickStarterAddAllAction) {
      this.mKickStarterAddAllAction = pKickStarterAddAllAction;
  }
  
  /**
	 * Gets the view/edit registry success url.
	 * 
	 * @return mViewEditSuccessURL
	 */
	public String getViewEditSuccessURL() {
		return mViewEditSuccessURL;
	}
  
	/**
	 * Sets the view/edit registry error url.
	 * 
	 * @param pViewEditSuccessURL
	 *            the new view/edit registry success url
	 */
	public void setViewEditSuccessURL(String pViewEditSuccessURL) {
		this.mViewEditSuccessURL = pViewEditSuccessURL;
	}
	
	/**
	 * Gets the view/edit registry error url.
	 * 
	 * @return mViewEditFailureURL
	 */
	public String getViewEditFailureURL() {
		return mViewEditFailureURL;
	}

	/**
	 * Sets the view/edit registry error url.
	 * 
	 * @param pViewEditSuccessURL
	 *            the new view/edit registry error url
	 */
	public void setViewEditFailureURL(String pViewEditFailureURL) {
		this.mViewEditFailureURL = pViewEditFailureURL;
	}
	/**
	 * 
	 * @return emailOptIn
	 */
	public String getEmailOptIn() {
		return emailOptIn;
	}
	/**
	 * 
	 * @param emailOptIn the emailOptIn to set
	 */
	public void setEmailOptIn(String emailOptIn) {
		this.emailOptIn = emailOptIn;
	}
	/**
	 * 
	 * @return errorFlagEmailOptIn
	 */
	public boolean isErrorFlagEmailOptIn() {
		return errorFlagEmailOptIn;
	}
	/**
	 * 
	 * @param errorFlagEmailOptIn the errorFlagEmailOptIn to set
	 */
	public void setErrorFlagEmailOptIn(boolean errorFlagEmailOptIn) {
		this.errorFlagEmailOptIn = errorFlagEmailOptIn;
	}
	/**
	 * 
	 * @return buyoffStartBrowsingSuccessURL
	 */
	public String getBuyoffStartBrowsingSuccessURL() {
		return buyoffStartBrowsingSuccessURL;
	}
	/**
	 * 
	 * @param buyoffStartBrowsingSuccessURL the buyoffStartBrowsingSuccessURL to set
	 */
	public void setBuyoffStartBrowsingSuccessURL(
			String buyoffStartBrowsingSuccessURL) {
		this.buyoffStartBrowsingSuccessURL = buyoffStartBrowsingSuccessURL;
	}
	/**
	 * 
	 * @return itemTypes
	 */
	public String getItemTypes() {
		return itemTypes;
	}
	/**
	 * 
	 * @param itemTypes the itemTypes to set
	 */
	public void setItemTypes(String itemTypes) {
		this.itemTypes = itemTypes;
	}
	/**
	 * 
	 * @return customizationReq
	 */
	public String getCustomizationReq() {
		return customizationReq;
	}
	/**
	 * 
	 * @param customizationReq the customizationReq to set
	 */
	public void setCustomizationReq(String customizationReq) {
		this.customizationReq = customizationReq;
	}
	/**
	 * 
	 * @return htmlMessage
	 */
	public String getHtmlMessage() {
		return htmlMessage;
	}
	/**
	 * 
	 * @param htmlMessage the htmlMessage to set
	 */
	public void setHtmlMessage(String htmlMessage) {
		this.htmlMessage = htmlMessage;
	}
	/**
	 * 
	 * @return downloadFlag
	 */
	public boolean isDownloadFlag() {
		return downloadFlag;
	}
	/**
	 * 
	 * @param downloadFlag the downloadFlag to set
	 */
	public void setDownloadFlag(boolean downloadFlag) {
		this.downloadFlag = downloadFlag;
	}
	/**
	 * @return the wishListItemId
	 */
	public String getWishListItemId() {
		return this.wishListItemId;
	}

	/**
	 * @param wishListItemId
	 *            the wishListItemId to set
	 */
	public void setWishListItemId(final String wishListItemId) {
		this.wishListItemId = wishListItemId;
	}
	/**
	 * @return the alternateNum
	 */
	public String getAlternateNum() {
		return alternateNum;
	}
	/**
	 * @param alternateNum
	 *            the alternateNum to set
	 */
	public void setAlternateNum(String alternateNum) {
		this.alternateNum = alternateNum;
	}
	/**
	 * @return mMoveAllWishListItemsFailureResult
	 */
	public Map<String, String> getMoveAllWishListItemsFailureResult() {
		return this.mMoveAllWishListItemsFailureResult;
	}

	/**
	 * @param mMoveAllWishListItemsFailureResult
	 *            the mMoveAllWishListItemsFailureResult to set
	 */
	public void setMoveAllWishListItemsFailureResult(
			final Map<String, String> mMoveAllWishListItemsFailureResult) {
		this.mMoveAllWishListItemsFailureResult = mMoveAllWishListItemsFailureResult;
	}
	/**
	 * 
	 * @return recommenderProfileId
	 */
	public String getRecommenderProfileId() {
		return recommenderProfileId;
	}
	/**
	 * 
	 * @param recommenderProfileId the recommenderProfileId to set
	 */
	public void setRecommenderProfileId(String recommenderProfileId) {
		this.recommenderProfileId = recommenderProfileId;
	}
	/**
	 * 
	 * @return requestedFlag
	 */
	public String getRequestedFlag() {
		return requestedFlag;
	}
	/**
	 * 
	 * @param requestedFlag the requestedFlag to set
	 */
	public void setRequestedFlag(String requestedFlag) {
		this.requestedFlag = requestedFlag;
	}
	/**
	 * 
	 * @return toggleSuccessURL
	 */
	public String getToggleSuccessURL() {
		return toggleSuccessURL;
	}
	/**
	 * 
	 * @param toggleSuccessURL the toggleSuccessURL to set 
	 */
	public void setToggleSuccessURL(String toggleSuccessURL) {
		this.toggleSuccessURL = toggleSuccessURL;
	}
	/**
	 * 
	 * @return toggleFailureURL
	 */
	public String getToggleFailureURL() {
		return toggleFailureURL;
	}
	/**
	 * 
	 * @param toggleFailureURL the toggleFailureURL to set
	 */
	public void setToggleFailureURL(String toggleFailureURL) {
		this.toggleFailureURL = toggleFailureURL;
	}
	/**
	 * 
	 * @return contactPoBoxFlag
	 */
	public String getContactPoBoxFlag() {
		return contactPoBoxFlag;
	}
	/**
	 * 
	 * @param contactPoBoxFlag the contactPoBoxFlag to set
	 */
	public void setContactPoBoxFlag(String contactPoBoxFlag) {
		this.contactPoBoxFlag = contactPoBoxFlag;
	}
	/**
	 * 
	 * @return contactPoBoxStatus
	 */
	public String getContactPoBoxStatus() {
		return contactPoBoxStatus;
	}
	/**
	 * 
	 * @param contactPoBoxStatus the contactPoBoxStatus to set
	 */
	public void setContactPoBoxStatus(String contactPoBoxStatus) {
		this.contactPoBoxStatus = contactPoBoxStatus;
	}
	/**
	 * 
	 * @return shipPoBoxFlag
	 */
	public String getShipPoBoxFlag() {
		return shipPoBoxFlag;
	}
	/**
	 * 
	 * @param shipPoBoxFlag the shipPoBoxFlag to set
	 */
	public void setShipPoBoxFlag(String shipPoBoxFlag) {
		this.shipPoBoxFlag = shipPoBoxFlag;
	}
	/**
	 * 
	 * @return shipPoBoxStatus
	 */
	public String getShipPoBoxStatus() {
		return shipPoBoxStatus;
	}
	/**
	 * 
	 * @param shipPoBoxStatus the shipPoBoxStatus to set
	 */
	public void setShipPoBoxStatus(String shipPoBoxStatus) {
		this.shipPoBoxStatus = shipPoBoxStatus;
	}
	/**
	 * 
	 * @return futurePoBoxFlag
	 */
	public String getFuturePoBoxFlag() {
		return futurePoBoxFlag;
	}
	/**
	 * 
	 * @param futurePoBoxFlag the futurePoBoxFlag to set
	 */
	public void setFuturePoBoxFlag(String futurePoBoxFlag) {
		this.futurePoBoxFlag = futurePoBoxFlag;
	}
	/**
	 * 
	 * @return futurePoBoxStatus
	 */
	public String getFuturePoBoxStatus() {
		return futurePoBoxStatus;
	}
	/**
	 * 
	 * @param futurePoBoxStatus the futurePoBoxStatus to set
	 */
	public void setFuturePoBoxStatus(String futurePoBoxStatus) {
		this.futurePoBoxStatus = futurePoBoxStatus;
	}
	/**
	 * 
	 * @return undoFrom
	 */
	public String getUndoFrom() {
		return undoFrom;
	}
	/**
	 * 
	 * @param undoFrom the undoFrom to set
	 */
	public void setUndoFrom(String undoFrom) {
		this.undoFrom = undoFrom;
	}
	/**
	 * 
	 * @return undoSuccessURL
	 */
	public String getUndoSuccessURL() {
		return undoSuccessURL;
	}
	/**
	 * 
	 * @param undoSuccessURL the undoSuccessURL to set
	 */
	public void setUndoSuccessURL(String undoSuccessURL) {
		this.undoSuccessURL = undoSuccessURL;
	}
	/**
	 * 
	 * @return undoFailureURL
	 */
	public String getUndoFailureURL() {
		return undoFailureURL;
	}
	/**
	 * 
	 * @param undoFailureURL the undoFailureURL to set
	 */
	public void setUndoFailureURL(String undoFailureURL) {
		this.undoFailureURL = undoFailureURL;
	}
	/**
	 * 
	 * @return recommededFlag
	 */
	public String getRecommededFlag() {
		return recommededFlag;
	}
	/**
	 * 
	 * @param pRecommededFlag the pRecommededFlag to set
	 */
	public void setRecommededFlag(String pRecommededFlag) {
		recommededFlag = pRecommededFlag;
	}
	/**
	 * Set the RepeatingRequestMonitor property.
	 * @param pRepeatingRequestMonitor a <code>RepeatingRequestMonitor</code> value
	 */
	 public void setRepeatingRequestMonitor(RepeatingRequestMonitor repeatingRequestMonitor) {
	    this.repeatingRequestMonitor = repeatingRequestMonitor;
	 }
	 /**
	  * Return the RepeatingRequestMonitor property.
	  * @return a <code>RepeatingRequestMonitor</code> value
	  */
	 public RepeatingRequestMonitor getRepeatingRequestMonitor() {
	    return this.repeatingRequestMonitor;
	 }
	 /**
	  * 
	  * @return profileRepository
	  */
	 public MutableRepository getProfileRepository() {
			return profileRepository;
	 }
	 /**
	  * 
	  * @param pProfileRepository the pProfileRepository to set
	  */
	 public void setProfileRepository(MutableRepository pProfileRepository) {
			profileRepository = pProfileRepository;
	 }
	 /**
	  * @return the userProfileTools
	  */
	public BBBProfileTools getUserProfileTools() {
		return userProfileTools;
	}
	/**
	 * @param userProfileTools the userProfileTools to set
	 */
	public void setUserProfileTools(BBBProfileTools userProfileTools) {
		this.userProfileTools = userProfileTools;
	}
	/**
	 * 
	 * @return giftRegistryRecommendationManager
	 */
	public GiftRegistryRecommendationManager getGiftRegistryRecommendationManager() {
		return giftRegistryRecommendationManager;
	}
	/**
	 * 
	 * @param pGiftRegistryRecommendationManager the pGiftRegistryRecommendationManager to set
	 */
	public void setGiftRegistryRecommendationManager(
			GiftRegistryRecommendationManager pGiftRegistryRecommendationManager) {
		giftRegistryRecommendationManager = pGiftRegistryRecommendationManager;
	}
	/**
	 * 
	 * @return giftRegistryTools
	 */
	public GiftRegistryTools getGiftRegistryTools() {
		return giftRegistryTools;
	}
	/**
	 * 
	 * @param pGiftRegistryTools the pGiftRegistryTools to set
	 */
	public void setGiftRegistryTools(GiftRegistryTools pGiftRegistryTools) {
		giftRegistryTools = pGiftRegistryTools;
	}
	/**
	 * 
	 * @return createRegistryResVO
	 */
	public RegistryResVO getCreateRegistryResVO() {
		return createRegistryResVO;
	}
	/**
	 * 
	 * @param pCreateRegistryResVO the pCreateRegistryResVO to set
	 */
	public void setCreateRegistryResVO(RegistryResVO pCreateRegistryResVO) {
		createRegistryResVO = pCreateRegistryResVO;
	}
	/**
	 * 
	 * @return eximPricingManager
	 */
	public BBBEximManager getEximPricingManager() {
		return eximPricingManager;
	}
	/**
	 * 
	 * @param pEximPricingManager the pEximPricingManager to set
	 */
	public void setEximPricingManager(BBBEximManager pEximPricingManager) {
		eximPricingManager = pEximPricingManager;
	}
	/**
	 * 
	 * @return certonaParameter
	 */
	public CertonaReqParameterVO getCertonaParameter() {
		if (this.certonaParameter == null) {
			this.certonaParameter = new CertonaReqParameterVO();
		}
		return this.certonaParameter;
	}
	/**
	 * 
	 * @param certonaParameter the certonaParameter to set
	 */
	public void setCertonaParameter(final CertonaReqParameterVO certonaParameter) {
		this.certonaParameter = certonaParameter;
	}
	/**
	 * Gets the registrant address from ws.
	 * 
	 * @return the registrant address from ws
	 */
	public AddressVO getRegistrantAddressFromWS() {

		if (this.registrantAddressFromWS != null) {
			return this.registrantAddressFromWS;
		}
		this.registrantAddressFromWS = new AddressVO();
		return this.registrantAddressFromWS;
	}
	/**
	 * Sets the registrant address from ws.
	 * 
	 * @param registrantAddressFromWS
	 *            the new registrant address from ws
	 */
	public void setRegistrantAddressFromWS(
			final AddressVO registrantAddressFromWS) {
		this.registrantAddressFromWS = registrantAddressFromWS;
	}
	/**
	 * Gets the shipping address from ws.
	 * 
	 * @return the shipping address from ws
	 */
	public AddressVO getShippingAddressFromWS() {

		if (this.shippingAddressFromWS != null) {
			return this.shippingAddressFromWS;
		}
		this.shippingAddressFromWS = new AddressVO();
		return this.shippingAddressFromWS;
	}
	/**
	 * Sets the shipping address from ws.
	 * 
	 * @param shippingAddressFromWS
	 *            the new shipping address from ws
	 */
	public void setShippingAddressFromWS(final AddressVO shippingAddressFromWS) {
		this.shippingAddressFromWS = shippingAddressFromWS;
	}
	/**
	 * Gets the future shipping address from ws.
	 * 
	 * @return the future shipping address from ws
	 */
	public AddressVO getFutureShippingAddressFromWS() {

		if (this.futureShippingAddressFromWS != null) {
			return this.futureShippingAddressFromWS;
		}
		this.futureShippingAddressFromWS = new AddressVO();
		return this.futureShippingAddressFromWS;
	}
	/**
	 * Sets the future shipping address from ws.
	 * 
	 * @param futureShippingAddressFromWS
	 *            the new future shipping address from ws
	 */
	public void setFutureShippingAddressFromWS(
			final AddressVO futureShippingAddressFromWS) {
		this.futureShippingAddressFromWS = futureShippingAddressFromWS;
	}
	/**
	 * Gets the profile.
	 * 
	 * @return the profile
	 */
	public Profile getProfile() {
		return this.mProfile;
	}
	/**
	 * Sets the profile.
	 * 
	 * @param pProfile
	 *            the profile to set
	 */
	public void setProfile(final Profile pProfile) {
		this.mProfile = pProfile;
	}
	/**
	 * Gets the registry vo.
	 * 
	 * @return the registryVO
	 */
	public RegistryVO getRegistryVO() {
		if (this.mRegistryVO != null) {
			return this.mRegistryVO;
		}
		this.mRegistryVO = new RegistryVO();
		return this.mRegistryVO;
	}
	/**
	 * Sets the registry vo.
	 * 
	 * @param pRegistryVO
	 *            the registryVO to set
	 */
	public void setRegistryVO(final RegistryVO pRegistryVO) {
		this.mRegistryVO = pRegistryVO;
	}
	/**
	 * Gets the registry items list vo.
	 * 
	 * @return the registryItemListVO
	 */
	public RegistryItemsListVO getRegistryItemsListVO() {
		if (this.mRegistryItemsListVO != null) {
			return this.mRegistryItemsListVO;
		}
		this.mRegistryItemsListVO = new RegistryItemsListVO();
		return this.mRegistryItemsListVO;
	}
	/**
	 * Sets the registry items list vo.
	 * 
	 * @param pRegistryItemsListVO
	 *            the registryVO to set
	 */
	public void setRegistryItemsListVO(
			final RegistryItemsListVO pRegistryItemsListVO) {
		this.mRegistryItemsListVO = pRegistryItemsListVO;
	}
	/**
	 * Gets the forget reg pass request vo.
	 * 
	 * @return the forgetRegPassRequestVO
	 */
	public ForgetRegPassRequestVO getForgetRegPassRequestVO() {
		if (this.mForgetRegPassRequestVO != null) {
			return this.mForgetRegPassRequestVO;
		}
		this.mForgetRegPassRequestVO = new ForgetRegPassRequestVO();
		return this.mForgetRegPassRequestVO;
	}
	/**
	 * Sets the forget reg pass request vo.
	 * 
	 * @param pForgetRegPassRequestVO
	 *            the forgetRegPassRequestVO to set
	 */
	public void setForgetRegPassRequestVO(
			final ForgetRegPassRequestVO pForgetRegPassRequestVO) {
		this.mForgetRegPassRequestVO = pForgetRegPassRequestVO;
	}
	/**
	 * Getter for GiftRegSessionBean.
	 * 
	 * @return giftRegSessionBean
	 */
	public GiftRegSessionBean getGiftRegSessionBean() {
		return this.giftRegSessionBean;
	}
	/**
	 * Setter for.
	 * 
	 * @param giftRegSessionBean
	 *            the new gift reg session bean
	 */
	public void setGiftRegSessionBean(
			final GiftRegSessionBean giftRegSessionBean) {
		this.giftRegSessionBean = giftRegSessionBean;
	}
	/**
	 * Gets the email holder.
	 * 
	 * @return the emailHolder
	 */
	public EmailHolder getEmailHolder() {
		return this.emailHolder;
	}
	/**
	 * Sets the email holder.
	 * 
	 * @param emailHolder
	 *            the emailHolder to set
	 */
	public void setEmailHolder(final EmailHolder emailHolder) {
		this.emailHolder = emailHolder;
	}
	/**
	 * Gets the gift reg email info.
	 * 
	 * @return the mGiftRegEmailInfo
	 */
	public TemplateEmailInfoImpl getGiftRegEmailInfo() {
		return this.mGiftRegEmailInfo;
	}
	/**
	 * Sets the gift reg email info.
	 * 
	 * @param mGiftRegEmailInfo
	 *            the mGiftRegEmailInfo to set
	 */
	public void setGiftRegEmailInfo(
			final TemplateEmailInfoImpl mGiftRegEmailInfo) {
		this.mGiftRegEmailInfo = mGiftRegEmailInfo;
	}
	/**
	 * 
	 * @return mKickStarterManager
	 */
	public KickStarterManager getKickStarterManager() {
		return mKickStarterManager;
	}
	/**
	 * 
	 * @param pKickStarterManager the pKickStarterManager to set
	 */
	public void setKickStarterManager(KickStarterManager pKickStarterManager) {
		mKickStarterManager = pKickStarterManager;
	}
	/**
	 * 
	 * @return siteRepository
	 */
	public Repository getSiteRepository() {
		return siteRepository;
	}
	/**
	 * 
	 * @param siteRepository the siteRepository to set
	 */
	public void setSiteRepository(Repository siteRepository) {
		this.siteRepository = siteRepository;
	}
	/**
     * @return the mwishlistManager
     */
    public BBBWishlistManager getWishlistManager() {
            return this.mwishlistManager;
    }
    /**
     * @param mwishlistManager
     *            the mwishlistManager to set
     */
    public void setWishlistManager(final BBBWishlistManager mwishlistManager) {
            this.mwishlistManager = mwishlistManager;
    }
    /**
     * 
     * @return reposityItem
     */
    public RepositoryItem getReposityItem() {
		return reposityItem;
	}
    /**
     * 
     * @param reposityItem the reposityItem to set
     */
	public void setReposityItem(RepositoryItem reposityItem) {
		this.reposityItem = reposityItem;
	}
	
	// GiftRegistryFormHandler
	/**
	 * 
	 * @return successQueryParam
	 */
	public String getSuccessQueryParam() {
		return successQueryParam;
	}
	/**
	 * 
	 * @param successQueryParam the successQueryParam to set
	 */
	public void setSuccessQueryParam(String successQueryParam) {
		this.successQueryParam = successQueryParam;
	}
	/**
	 * 
	 * @return errorQueryParam
	 */
	public String getErrorQueryParam() {
		return errorQueryParam;
	}
	/**
	 * 
	 * @param errorQueryParam the errorQueryParam to set
	 */
	public void setErrorQueryParam(String errorQueryParam) {
		this.errorQueryParam = errorQueryParam;
	}
	/**
	 * 
	 * @return fromPage
	 */
	public String getFromPage() {
		return fromPage;
	}
	/**
	 * 
	 * @param fromPage the fromPage to set 
	 */
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	/**
	 * 
	 * @return hoorayModal
	 */
	public String getHoorayModal() {
		return hoorayModal;
	}
	/**
	 * 
	 * @param hoorayModal the hoorayModal to set
	 */
	public void setHoorayModal(String hoorayModal) {
		this.hoorayModal = hoorayModal;
	}
	/**
	 * 
	 * @return desktop
	 */
	public String getDesktop() {
		return desktop;
	}
	/**
	 * 
	 * @param desktop the desktop to set
	 */
	public void setDesktop(String desktop) {
		this.desktop = desktop;
	}
	/**
	 * 
	 * @return updateSimplified
	 */
	public String getUpdateSimplified() {
		return updateSimplified;
	}
	/**
	 * 
	 * @param updateSimplified the updateSimplified to set
	 */
	public void setUpdateSimplified(String updateSimplified) {
		this.updateSimplified = updateSimplified;
	}
	/**
	 * 
	 * @return createSimplified
	 */
	public String getCreateSimplified() {
		return createSimplified;
	}
	/**
	 * 
	 * @param createSimplified the createSimplified to set
	 */
	public void setCreateSimplified(String createSimplified) {
		this.createSimplified = createSimplified;
	}
	/**
	 * 
	 * @return propertyManger
	 */
	public BBBPropertyManager getPropertyManger() {
		return propertyManger;
	}
	/**
	 * 
	 * @param propertyManger the propertyManger to set
	 */
	public void setPropertyManger(BBBPropertyManager propertyManger) {
		this.propertyManger = propertyManger;
	}
	/**
	 * 
	 * @return regErrorMap
	 */
	public Map<String, String> getRegErrorMap() {
		return regErrorMap;
	}
	/**
	 * 
	 * @param regErrorMap the regErrorMap to set
	 */
	public void setRegErrorMap(Map<String, String> regErrorMap) {
		this.regErrorMap = regErrorMap;
	}
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
	/**
	 * 
	 * @return addedItemPrice
	 */
	public String getAddedItemPrice() {
		return addedItemPrice;
	}
	/**
	 * 
	 * @param addedItemPrice the addedItemPrice to set
	 */
	public void setAddedItemPrice(String addedItemPrice) {
		this.addedItemPrice = addedItemPrice;
	}
	/**
	 * @return queryParam
	 */
	public String getQueryParam() {
		return queryParam;
	}
	/**
	 * @param queryParam
	 */
	public void setQueryParam(String queryParam) {
		this.queryParam = queryParam;
	}
	/**
	 * 
	 * @return makeRegistryPublic
	 */
	public String getMakeRegistryPublic() {
		return makeRegistryPublic;
	}
	/**
	 * 
	 * @param makeRegistryPublic the makeRegistryPublic to set
	 */
	public void setMakeRegistryPublic(String makeRegistryPublic) {
		this.makeRegistryPublic = makeRegistryPublic;
	}
	/**
	 * 
	 * @return deactivateRegistry
	 */
	public String getDeactivateRegistry() {
		return deactivateRegistry;
	}
	/**
	 * 
	 * @param deactivateRegistry the deactivateRegistry to set
	 */
	public void setDeactivateRegistry(String deactivateRegistry) {
		this.deactivateRegistry = deactivateRegistry;
	}
	/**
	 * 
	 * @return ltlFlag
	 */
	public String getLtlFlag() {
		return ltlFlag;
	}
	/**
	 * 
	 * @param ltlFlag the ltlFlag to set
	 */
	public void setLtlFlag(String ltlFlag) {
		this.ltlFlag = ltlFlag;
	}
	/**
	 * 
	 * @return simplifyRegistryManager
	 */
	public SimplifyRegistryManager getSimplifyRegistryManager() {
		return simplifyRegistryManager;
	}
	/**
	 * 
	 * @param simplifyRegistryManager the simplifyRegistryManager to set
	 */
	public void setSimplifyRegistryManager(SimplifyRegistryManager simplifyRegistryManager) {
		this.simplifyRegistryManager = simplifyRegistryManager;
	}
	/**
	 * 
	 * @return poBoxAddress
	 */
	public boolean isPoBoxAddress() {
		return poBoxAddress;
	}
	/**
	 * 
	 * @param poBoxAddress the poBoxAddress to set
	 */
	public void setPoBoxAddress(boolean poBoxAddress) {
		this.poBoxAddress = poBoxAddress;
	}
	/**
	 * Gets the lbl txt template manager.
	 *
	 * @return the lblTxtTemplateManager
	 */
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return this.lblTxtTemplateManager;
	}

	/**
	 * Sets the lbl txt template manager.
	 *
	 * @param pLblTxtTemplateManager
	 *            the lblTxtTemplateManager to set
	 */
	public void setLblTxtTemplateManager(final LblTxtTemplateManager pLblTxtTemplateManager) {
		this.lblTxtTemplateManager = pLblTxtTemplateManager;
	}
	/**
	 * 
	 * @return defaultRefNum
	 */
	public String getDefaultRefNum() {
		return defaultRefNum;
	}
	/**
	 * 
	 * @param defaultRefNum the defaultRefNum to set
	 */
	public void setDefaultRefNum(String defaultRefNum) {
		this.defaultRefNum = defaultRefNum;
	}
	/**
	 * 
	 * @return babygenderStr
	 */
	public String getBabygenderStr() {
		return babygenderStr;
	}
	/**
	 * 
	 * @param babygenderStr the babygenderStr to set
	 */
	public void setBabygenderStr(String babygenderStr) {
		this.babygenderStr = babygenderStr;
	}
	/**
	 * 
	 * @return totalGiftRegistered
	 */
	public int getTotalGiftRegistered() {
		return totalGiftRegistered;
	}
	/**
	 * 
	 * @param totalGiftRegistered the totalGiftRegistered to set
	 */
	public void setTotalGiftRegistered(int totalGiftRegistered) {
		this.totalGiftRegistered = totalGiftRegistered;
	}
	/**
	 * 
	 * @return emailRecommendationErrorURL
	 */
	public String getEmailRecommendationErrorURL() {
		return emailRecommendationErrorURL;
	}
	/**
	 * 
	 * @param emailRecommendationErrorURL the emailRecommendationErrorURL to set
	 */
	public void setEmailRecommendationErrorURL(String emailRecommendationErrorURL) {
		this.emailRecommendationErrorURL = emailRecommendationErrorURL;
	}
	/**
	 * 
	 * @return emailRecommendationSuccessURL
	 */
	public String getEmailRecommendationSuccessURL() {
		return emailRecommendationSuccessURL;
	}
	/**
	 * 
	 * @param emailRecommendationSuccessURL the emailRecommendationSuccessURL to set
	 */
	public void setEmailRecommendationSuccessURL(String emailRecommendationSuccessURL) {
		this.emailRecommendationSuccessURL = emailRecommendationSuccessURL;
	}

}
