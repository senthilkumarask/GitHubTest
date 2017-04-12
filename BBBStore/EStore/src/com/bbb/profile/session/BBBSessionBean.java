package com.bbb.profile.session;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.captcha.Captcha;
import atg.nucleus.ServiceException;

import com.bbb.account.AccountVo;
import com.bbb.account.profile.vo.ProfileEDWInfoVO;
import com.bbb.commerce.browse.vo.SddZipcodeVO;
import com.bbb.commerce.checklist.vo.CheckListPrevNextCategoriesVO;
import com.bbb.commerce.checklist.vo.CheckListVO;
import com.bbb.commerce.exim.bean.EximSessionBean;
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.common.BBBGenericService;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.wishlist.GiftListVO;

/** @author sku134 */
public class BBBSessionBean extends BBBGenericService {

    /** Class version string. */
    public static final String CLASS_VERSION = "-191919191919191919";
    private AccountVo accountVo;

    /**
	 * @return the accountVo
	 */
	public AccountVo getAccountVo() {
		return accountVo;
	}
	/**
	 * @param accountVo the accountVo to set
	 */
	public void setAccountVo(AccountVo accountVo) {
		this.accountVo = accountVo;
	}


	private GiftRegistryViewBean giftRegistryViewBean;
    private HashMap<String, RegistryVO> registryVOs;
    private Captcha captcha;

    private BBBCommerceItem movedCommerceItem;

    private List<GiftListVO> giftListVO;
    private HashMap<String, ?> hashMap;
    private HashMap values;
    private String[] searchSiteIDs;
    private String babyCA;
    private String importRegistryRedirectUrl;
    private String moveCartItemToWishSuccessUrl;
    private String commerceItemID;
    private String productDetailsRedirectUrl;
    private String legacyMemberID;
    private String legacyEmailID;
    private String userEmailId;
    private String couponError;
    private String couponExpiry;
    private String couponEmail;
    private String catalogRefID;
    private String productId;
    private String maxGiftCardInvalidAttempt;
    private String skuIDToAdd;
    private String registryTypesEvent;
    private List<RegistrySummaryVO> registrySummaryVO;
    private boolean recommRegistriesPopulated = false;
	private long quantity;
    private int creditCardInvalidAttempts;
    private int giftCardInvalidAttempt;
    private boolean preSelectedAddress;
    /** to hold a kick starter id in session */
	private String mKickStarterId;
	/** to hold a EventType id in session */
	private String mEventType;
	/** to hold a Kick Starter Event Type in session */
	private String mKickStarterEventType;
	/** to hold a RegistryId in session */
	private String mRegistryId;
	private String mSddStoreId;
	/** to hold add all action into session */
	private String mAddALLActgion;
	private List<String> topConsultantIds;
	private List<String> shopTheLookIds;
	/** to Manage active registry */
	private String mMngActRegistry;
	// Added to fix BBBSL-2662 - Start
	private String emailChecked;
	private boolean couponsWelcomeMsg =true;
	private RegistrySummaryVO buyoffStartBrowsingSummaryVO;
	private boolean singlePageCheckout= false;
	private boolean isRegistredUser=false;
	private boolean useShipAddr =false;
	private boolean frmWalletRegPage=false;
	private String registryProfileStatus;
	private String registryEvar23Price;
	private boolean regDiffDateLess;
	private RegistryVO simplifyRegVO;
	private SddZipcodeVO landingZipcodeVO;
	private SddZipcodeVO currentZipcodeVO;
	private SddZipcodeVO shippingZipcodeVO;
	private boolean showSDD;
	private boolean primeRegistryCompleted;
	private String vendorParam;
	private CheckListPrevNextCategoriesVO checkListPrevNextCategoriesVO;
	private CheckListVO checklistVO;
	private boolean activateGuideInRegistryRibbon;
	private String registryJsonResultString;
	private String porchZipCode;
	private boolean registryPorchServiceRemoved;
	 
	private Map<String, Integer> tbsSkuInventory;
	// Added to fix  BBB-93 | DSK- Adding additional access points for the extend account modal
	private String fromPage;
	private String queryParam;
	private String extendModal;
	private boolean ownerRegAddPOBOX;
	private String reqFromHomepage;
	
	public boolean isOwnerRegAddPOBOX() {
		return ownerRegAddPOBOX;
	}
	
	public void setOwnerRegAddPOBOX(boolean ownerRegAddPOBOX) {
		this.ownerRegAddPOBOX = ownerRegAddPOBOX;
	}
	
	public String getQueryParam() {
		return queryParam;
	}
	
	public void setQueryParam(String queryParam) {
		this.queryParam = queryParam;
	}
	
	public String getFromPage() {
		return fromPage;
	}
	
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	
	public String getExtendModal() {
		return extendModal;
	}
	
	public void setExtendModal(String extendModal) {
		this.extendModal = extendModal;
	}
	


	
	private String sbcOauthToken;
	private long sbcTokenExpireTime;
	
	public Map<String, Integer> getTbsSkuInventory() {
		return tbsSkuInventory;
	}
	public void setTbsSkuInventory(Map<String, Integer> skuInventoryAvailability) {
		this.tbsSkuInventory = skuInventoryAvailability;
	}
	public boolean isPrimeRegistryCompleted() {
		return primeRegistryCompleted;
	}
	public void setPrimeRegistryCompleted(boolean primeRegistryCompleted) {
		this.primeRegistryCompleted = primeRegistryCompleted;
	}
	public boolean isActivateGuideInRegistryRibbon() {
		return activateGuideInRegistryRibbon;
	}
	public void setActivateGuideInRegistryRibbon(boolean activateGuideInRegistryRibbon) {
		this.activateGuideInRegistryRibbon = activateGuideInRegistryRibbon;
	}
	public boolean isShowSDD() {
		return showSDD;
	}
	public void setShowSDD(boolean showSDD) {
		this.showSDD = showSDD;
	}
	public SddZipcodeVO getLandingZipcodeVO() {
		return landingZipcodeVO;
	}
	public void setLandingZipcodeVO(SddZipcodeVO landingZipcodeVO) {
		this.landingZipcodeVO = landingZipcodeVO;
	}
	public SddZipcodeVO getCurrentZipcodeVO() {
		return currentZipcodeVO;
	}
	public void setCurrentZipcodeVO(SddZipcodeVO currentZipcodeVO) {
		this.currentZipcodeVO = currentZipcodeVO;
	}
	public SddZipcodeVO getShippingZipcodeVO() {
		return shippingZipcodeVO;
	}
	public void setShippingZipcodeVO(SddZipcodeVO shippingZipcodeVO) {
		this.shippingZipcodeVO = shippingZipcodeVO;
	}
	
	private boolean pwdReqByChallengeQ=false;
	private String pwdReqFrmPageName;
	
	private ProfileEDWInfoVO edwDataVO;
	
	
	public String getRegistryEvar23Price() {
		return registryEvar23Price;
	}
	public void setRegistryEvar23Price(String registryEvar23Price) {
		this.registryEvar23Price = registryEvar23Price;
	}
	
	public RegistryVO getSimplifyRegVO() {
		return simplifyRegVO;
	}
	public void setSimplifyRegVO(RegistryVO simplifyRegVO) {
		this.simplifyRegVO = simplifyRegVO;
	}
	public boolean isSinglePageCheckout() {
		return singlePageCheckout;
	}
	public void setSinglePageCheckout(boolean singlePageCheckout) {
		this.singlePageCheckout = singlePageCheckout;
	}
	public String getUserEmailId() {
		return userEmailId;
	}
	public void setUserEmailId(String userEmailId) {
		this.userEmailId = userEmailId;
	}
	public boolean isUseShipAddr() {
		return useShipAddr;
	}
	public void setUseShipAddr(boolean useShipAddr) {
		this.useShipAddr = useShipAddr;
	}
	public boolean isCouponsWelcomeMsg() {
		return couponsWelcomeMsg;
	}

	public void setCouponsWelcomeMsg(boolean couponsWelcomeMsg) {
		this.couponsWelcomeMsg = couponsWelcomeMsg;
	}
	
	private Map<String, EximSessionBean> personalizedSkus;
	
	public Map<String, EximSessionBean> getPersonalizedSkus() {
		if(this.personalizedSkus==null)
			this.personalizedSkus = new HashMap<String, EximSessionBean>();
		return this.personalizedSkus;
	}
	public void setPersonalizedSkus(
			Map<String, EximSessionBean> personalizedSkus) {
		this.personalizedSkus = personalizedSkus;
	}


	// START -- Perf Fix -- Disable Compression Taglib based on user Agents matching BCC Managed pattern
	private String compressHtmlTagLib;
	private boolean internationalShippingContext;
	
	//Adding couponCount on session
	private int couponCount;
	
	
	public int getCouponCount() {
		return couponCount;
	}

	public void setCouponCount(int couponCount) {
		this.couponCount = couponCount;
	}

	public String getCompressHtmlTagLib() {
		return compressHtmlTagLib;
	}

	public void setCompressHtmlTagLib(String compressHtmlTagLib) {
		this.compressHtmlTagLib = compressHtmlTagLib;
	}
	// END   -- Perf Fix -- Disable Compression Taglib based on user Agents matching BCC Managed pattern

	/** @return Email Checked */
	public final String getEmailChecked() {
			return emailChecked;
	}

	/** @param emailChecked Email Checked */
	public final void setEmailChecked(final String emailChecked) {
		this.emailChecked = emailChecked;
	}
	// Added to fix BBBSL-2662 - End
	public List<String> getTopConsultantIds() {
		return topConsultantIds;
	}

	public void setTopConsultantIds(List<String> topConsultantIds) {
		this.topConsultantIds = topConsultantIds;
	}

	public List<String> getShopTheLookIds() {
		return shopTheLookIds;
	}

	public void setShopTheLookIds(List<String> shopTheLookIds) {
		this.shopTheLookIds = shopTheLookIds;
	}

	/** Default Constructor. */
    public BBBSessionBean() {
    	//default constructor
    }

    /** @return the registryVOs */
	public HashMap<String, RegistryVO> getRegistryVOs() {
		return registryVOs;
	}

	 /** @param registryVOs the registryVOs to set */
	public void setRegistryVOs(HashMap<String, RegistryVO> registryVOs) {
		this.registryVOs = registryVOs;
	}

	/** @return the movedCommerceItem */
    public final BBBCommerceItem getMovedCommerceItem() {
        return this.movedCommerceItem;
    }

    /** @param movedCommerceItem the movedCommerceItem to set */
    public final void setMovedCommerceItem(final BBBCommerceItem movedCommerceItem) {
        this.movedCommerceItem = movedCommerceItem;
    }

    /** This variable saves whether preselected address was chosen or not. */

    /** @return Coupon Error */
    public final String getCouponError() {
        return this.couponError;
    }

    /** @param couponError Coupon Error */
    public final void setCouponError(final String couponError) {
        this.couponError = couponError;
    }

    /** @return Coupon Expiry */
    public final String getCouponExpiry() {
        return this.couponExpiry;
    }

    /** @param couponExpiry Coupon Expiry */
    public final void setCouponExpiry(final String couponExpiry) {
        this.couponExpiry = couponExpiry;
    }
    
    /** @return Manage Active Registry String */
    public final String getMngActRegistry() {
        return this.mMngActRegistry;
    }

    /** @param couponExpiry Coupon Expiry */
    public final void setMngActRegistry(final String pMngActRegistry) {
        this.mMngActRegistry = pMngActRegistry;
    }
    
    /** @return Coupon Email */
    public final String getCouponEmail() {
        return this.couponEmail;
    }

    /** @param couponEmail Coupon Email */
    public final void setCouponEmail(final String couponEmail) {
        this.couponEmail = couponEmail;
    }

    /** @return the creditCardInvalidAttempts Credit Card Invalid Attempts */
    public final int getCreditCardInvalidAttempts() {
        return this.creditCardInvalidAttempts;
    }

    /** @param creditCardInvalidAttempts Credit Card Invalid Attempts */
    public final void setCreditCardInvalidAttempts(final int creditCardInvalidAttempts) {
        this.creditCardInvalidAttempts = creditCardInvalidAttempts;
    }

    /** @return the preSelectedAddress */
    public final boolean isPreSelectedAddress() {
        return this.preSelectedAddress;
    }

    /** @param preSelectedAddress the preSelectedAddress to set */
    public final void setPreSelectedAddress(final boolean preSelectedAddress) {
        this.preSelectedAddress = preSelectedAddress;
    }

    /** @return the mLegacyEmailId */
    public final String getLegacyEmailId() {
        return this.legacyEmailID;
    }

    /** @param legacyEmailID the mLegacyEmailId to set */
    public final void setLegacyEmailId(final String legacyEmailID) {
        this.legacyEmailID = legacyEmailID;
    }

    /** @return the mMemberId */
    public final String getLegacyMemberId() {
        return this.legacyMemberID;
    }

    /** @param legacyMemberID the mMemberId to set */
    public final void setLegacyMemberId(final String legacyMemberID) {
        this.legacyMemberID = legacyMemberID;
    }

    /** @return Catalog Reference ID */
    public final String getCatalogRefId() {
        return this.catalogRefID;
    }

    /** @param mCatalogRefId Catalog Reference ID */
    public final void setCatalogRefId(final String mCatalogRefId) {
        this.catalogRefID = mCatalogRefId;
    }

    /** @return */
    public final String getProductId() {
        return this.productId;
    }

    /** @param mProductId */
    public final void setProductId(final String mProductId) {
        this.productId = mProductId;
    }

    /** @return the hashMap */
    public final HashMap<String, ?> getHashMap() {
        return this.hashMap;
    }

    /** @param hashMap the hashMap to set */
    public final void setHashMap(final HashMap<String, ?> hashMap) {
        this.hashMap = hashMap;
    }

    /** @return the registryTypesEvent */
    public String getRegistryTypesEvent() {
        return this.registryTypesEvent;
    }

    /** @param pRegistryTypesEvent the registryTypesEvent to set */
    public void setRegistryTypesEvent(final String pRegistryTypesEvent) {
        this.registryTypesEvent = pRegistryTypesEvent;
    }

    /** A map containing miscellaneous session values.
     * 
     * @return the values. */
    public HashMap getValues() {
        if (this.values == null) {
            this.values = new HashMap();
        }
        return this.values;
    }

    /** <i>skuIdToAdd</i> property contains SKU id to be added into shopping cart. This property added in order to set two form
     * handlers' property from a single HTML element.
     * 
     * @return */
    public final String getSkuIdToAdd() {
        return this.skuIDToAdd;
    }

    /** <i>skuIdToAdd</i> property in form of String array. */
    /** @return */
    public final String[] getSkuIdToAddArray() {
        return this.skuIDToAdd == null ? null : new String[] { this.skuIDToAdd };
    }

    /** @param pSkuIdToAdd */
    public final void setSkuIdToAdd(final String pSkuIdToAdd) {
        this.skuIDToAdd = pSkuIdToAdd;
    }

    /** This property contains site IDs using by the user for search. This property should be updated each time the user runs new
     * ATG search.
     * 
     * @return site IDs of current search process. */
    public final String[] getSearchSiteIds() {
        if (this.searchSiteIDs == null) {
            this.searchSiteIDs = new String[0];
        }
        return this.searchSiteIDs;
    }

    /** @param pSearchSiteIds */
    public final void setSearchSiteIds(final String[] pSearchSiteIds) {
        this.searchSiteIDs = pSearchSiteIds;
    }

    /** @return the giftRegistryViewBean */
    public final GiftRegistryViewBean getGiftRegistryViewBean() {
        return this.giftRegistryViewBean;
    }

    /** @param pGiftRegistryViewBean the giftRegistryViewBean to set */
    public final void setGiftRegistryViewBean(final GiftRegistryViewBean pGiftRegistryViewBean) {
        this.giftRegistryViewBean = pGiftRegistryViewBean;
    }

    /** @return */
    public final String getMoveCartItemToWishSuccessUrl() {
        return this.moveCartItemToWishSuccessUrl;
    }

    /** @param pMoveCartItemToWishSuccessUrl */
    public final void setMoveCartItemToWishSuccessUrl(final String pMoveCartItemToWishSuccessUrl) {
        this.moveCartItemToWishSuccessUrl = pMoveCartItemToWishSuccessUrl;
    }

    /** @return */
    public final String getCommerceItemId() {
        return this.commerceItemID;
    }

    /** @param pCommerceItemId */
    public final void setCommerceItemId(final String pCommerceItemId) {
        this.commerceItemID = pCommerceItemId;
    }

    /** @return */
    public final long getQuantity() {
        return this.quantity;
    }

    /** @param mQuantity */
    public final void setQuantity(final long mQuantity) {
        this.quantity = mQuantity;
    }

    /** Clear Move Cart Items. */
    public final void clearMoveCartItemToWishBean() {
        this.setCommerceItemId(null);
        this.setMoveCartItemToWishSuccessUrl(null);
        this.setQuantity(0L);
    }

    /** Clear Items in WishList. */
    public final void clearItemInWishList() {
        this.setCatalogRefId(null);
        this.setProductId(null);
        this.setQuantity(0L);
    }

    /** @return the importRegistryRedirectUrl */
    public final String getImportRegistryRedirectUrl() {
        return this.importRegistryRedirectUrl;
    }

    /** @param pImportRegistryRedirectUrl the importRegistryRedirectUrl to set */
    public final void setImportRegistryRedirectUrl(final String pImportRegistryRedirectUrl) {
        this.importRegistryRedirectUrl = pImportRegistryRedirectUrl;
    }

    /** @return the mGiftCardInvalidAttempt */
    public final int getGiftCardInvalidAttempt() {
        return this.giftCardInvalidAttempt;
    }

    /** @param pGiftCardInvalidAttempt the mGiftCardInvalidAttempt to set */
    public final void setGiftCardInvalidAttempt(final int pGiftCardInvalidAttempt) {
        this.giftCardInvalidAttempt = pGiftCardInvalidAttempt;
    }

    /** @return the mMaxGiftCardInvalidAttempt */
    public final String getMaxGiftCardInvalidAttempt() {
        return this.maxGiftCardInvalidAttempt;
    }

    /** @param pMaxGiftCardInvalidAttempt the mMaxGiftCardInvalidAttempt to set */
    public final void setMaxGiftCardInvalidAttempt(final String pMaxGiftCardInvalidAttempt) {
        this.maxGiftCardInvalidAttempt = pMaxGiftCardInvalidAttempt;
    }

    @Override
    public final void doStartService() throws ServiceException {
        this.setGiftCardInvalidAttempt(0);
        super.doStartService();
    }

    /** @return the giftListVO */
    public final List<GiftListVO> getGiftListVO() {
        return this.giftListVO;
    }

    /** @param giftListVO the giftListVO to set */
    public final void setGiftListVO(final List<GiftListVO> giftListVO) {
        this.giftListVO = giftListVO;
    }

    /** @return the productDetailsRedirectUrl */
    public final String getProductDetailsRedirectUrl() {
        return this.productDetailsRedirectUrl;
    }

    /** @param productDetailsRedirectUrl the productDetailsRedirectUrl to set */
    public final void setProductDetailsRedirectUrl(final String productDetailsRedirectUrl) {
        this.productDetailsRedirectUrl = productDetailsRedirectUrl;
    }

    /** @return the captcha */
    public final Captcha getCaptcha() {
        return this.captcha;
    }

    /** @param captcha the captcha to set */
    public final void setCaptcha(final Captcha captcha) {
        this.captcha = captcha;
    }
	/**
	 * Get KickStarterId.
	 * 
	 * @return mkickStarterId
	 */
	public String getKickStarterId() {
		return this.mKickStarterId;
	}
	/**
	 * To Set kick starter id.
	 * 
	 * @param pKickStarterId
	 *            A KickStarterId to set
	 */
	public void setKickStarterId(String pKickStarterId) {
		this.mKickStarterId = pKickStarterId;
	}
	/**
	 * @return the mEventType
	 */
	public String getEventType() {
		return this.mEventType;
	}

	/**
	 * @param pEventType
	 *            the eventType to set
	 */
	public void setEventType(final String pEventType) {
		this.mEventType = pEventType;
	}
	
	/**
	 * Sets the store id.
	 * 
	 * @param pStoreId
	 *            the storeId to set
	 */
	public void setSddStoreId(final String pSddStoreId) {
		this.mSddStoreId = pSddStoreId;
	}
	/**
	 * Gets the store id.
	 * 
	 * @return the storeId
	 */
	public String getSddStoreId() {
		return this.mSddStoreId;
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
     * This method is used to get add all item action
     * @return mAddALLActgion - To get add all items action
     */
    public String getAddALLActgion() {
		return mAddALLActgion;
	}
    
    /**
     * This method is used to set add all item action
     * @param pAddALLActgion - To set add all items action
     */
	public void setAddALLActgion(String pAddALLActgion) {
		this.mAddALLActgion = pAddALLActgion;
	}

    /**
     * This method is used to hold Kick Starter Event Type
     * @return mKickStarterEventType - To get Kick Starter Event Type
     */
    public String getKickStarterEventType() {
		return mKickStarterEventType;
	}
    
    /**
     * This method is used to set add all item action
     * @param pAddALLActgion - To set add all items action
     */
	public void setKickStarterEventType(String pKickStarterEventType) {
		this.mKickStarterEventType = pKickStarterEventType;
	}
	
    public String getBabyCA() {
		return babyCA;
	}

	public void setBabyCA(String babyCA) {
		this.babyCA = babyCA;
	}

	/**
	 * @return the internationalShippingFlow
	 */
	public boolean isInternationalShippingContext() {
		return internationalShippingContext;
	}

	/**
	 * @param internationalShippingFlow the internationalShippingFlow to set
	 */
	public void setInternationalShippingContext(boolean internationalShippingContext) {
		this.internationalShippingContext = internationalShippingContext;
	}
	
	
	// PSI6 Surge Social Recommendation START
	private boolean recommenderFlow;
	private String recommenderRedirectUrl;
	
	/**
	 * @return recommenderFlow
	 */
	public boolean isRecommenderFlow() {
		return recommenderFlow;
	}

	/**
	 * @param recommenderFlow
	 */
	public void setRecommenderFlow(boolean recommenderFlow) {
		this.recommenderFlow = recommenderFlow;
	}
	
	/** @return the productDetailsRedirectUrl */
    public final String getRecommenderRedirectUrl() {
        return this.recommenderRedirectUrl;
    }

    /** @param productDetailsRedirectUrl the productDetailsRedirectUrl to set */
    public final void setRecommenderRedirectUrl(final String recommenderRedirectUrl) {
        this.recommenderRedirectUrl = recommenderRedirectUrl;
    }
	
	// PSI6 Surge Social Recommendation END
	
	@Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBSessionBean [mRegistryTypesEvent=").append(this.registryTypesEvent)
        .append(", mGiftRegistryViewBean=").append(this.giftRegistryViewBean).append(", mCaptcha=")
        .append(this.captcha).append(", movedCommerceItem=").append(this.movedCommerceItem)
        .append(", giftListVO=").append(this.giftListVO).append(", mHashMap=").append(this.hashMap)
        .append(", mValues=").append(this.values).append(", mSearchSiteIds=")
        .append(Arrays.toString(this.searchSiteIDs)).append(", importRegistryRedirectUrl=")
        .append(this.importRegistryRedirectUrl).append(", mMoveCartItemToWishSuccessUrl=")
        .append(this.moveCartItemToWishSuccessUrl).append(", mCommerceItemId=").append(this.commerceItemID)
        .append(", productDetailsRedirectUrl=").append(this.productDetailsRedirectUrl)
        .append(", mLegacyMemberId=").append(this.legacyMemberID).append(", mLegacyEmailId=")
        .append(this.legacyEmailID).append(", couponError=").append(this.couponError).append(", couponExpiry=")
        .append(this.couponExpiry).append(", couponEmail=").append(this.couponEmail).append(", mCatalogRefId=")
        .append(this.catalogRefID).append(", mProductId=").append(this.productId)
        .append(", mMaxGiftCardInvalidAttempt=").append(this.maxGiftCardInvalidAttempt)
        .append(", mSkuIdToAdd=").append(this.skuIDToAdd).append(", mQuantity=").append(this.quantity)
        .append(", creditCardInvalidAttempts=").append(this.creditCardInvalidAttempts)
        .append(", mGiftCardInvalidAttempt=").append(this.giftCardInvalidAttempt)
        .append(", preSelectedAddress=").append(this.preSelectedAddress)
        .append(", kickStarterId=").append(this.mKickStarterId)
        .append(", eventType=").append(this.mEventType)
        .append(", registryId=").append(this.mRegistryId)
        .append(", registryEvar23Price=").append(this.registryEvar23Price)
        .append(", kickStarterEventType=").append(this.mKickStarterEventType);        
        return builder.toString();
    }

	public List<RegistrySummaryVO> getRegistrySummaryVO() {
		return registrySummaryVO;
	}

	public void setRegistrySummaryVO(List<RegistrySummaryVO> registrySummaryVO) {
		this.registrySummaryVO = registrySummaryVO;
	}

	public RegistrySummaryVO getBuyoffStartBrowsingSummaryVO() {
		return buyoffStartBrowsingSummaryVO;
	}

	public void setBuyoffStartBrowsingSummaryVO(
			RegistrySummaryVO buyoffStartBrowsingSummaryVO) {
		this.buyoffStartBrowsingSummaryVO = buyoffStartBrowsingSummaryVO;
	}
	
	/**
	 * @return the frmWalletRegPage
	 */
	public boolean isFrmWalletRegPage() {
		return frmWalletRegPage;
	}

	/**
	 * @param frmWalletRegPage the frmWalletRegPage to set
	 */
	public void setFrmWalletRegPage(boolean frmWalletRegPage) {
		this.frmWalletRegPage = frmWalletRegPage;
	}
	/**
	 * @return the isRegistredUser
	 */
	public boolean isRegistredUser() {
		return isRegistredUser;
	}
	/**
	 * @param isRegistredUser the isRegistredUser to set
	 */
	public void setRegistredUser(boolean isRegistredUser) {
		this.isRegistredUser = isRegistredUser;
	}
	/**
	 * @return the registryProfileStatus
	 */
	public String getRegistryProfileStatus() {
		return registryProfileStatus;
	}
	/**
	 * @param registryProfileStatus the registryProfileStatus to set
	 */
	public void setRegistryProfileStatus(String registryProfileStatus) {
		this.registryProfileStatus = registryProfileStatus;
	}

	public boolean isPwdReqByChallengeQ() {
		return pwdReqByChallengeQ;
	}
	public void setPwdReqByChallengeQ(boolean pwdReqByChallengeQ) {
		this.pwdReqByChallengeQ = pwdReqByChallengeQ;
	}
	public String getPwdReqFrmPageName() {
		return pwdReqFrmPageName;
	}
	public void setPwdReqFrmPageName(String pwdReqFrmPageName) {
		this.pwdReqFrmPageName = pwdReqFrmPageName;
	}
	public ProfileEDWInfoVO getEdwDataVO() {
		return edwDataVO;
	}
	public void setEdwDataVO(ProfileEDWInfoVO edwDataVO) {
		this.edwDataVO = edwDataVO;
	}

	public boolean isRecommRegistriesPopulated() {
		return recommRegistriesPopulated;
	}
	public void setRecommRegistriesPopulated(boolean recommRegistriesPopulated) {
		this.recommRegistriesPopulated = recommRegistriesPopulated;
	}
	/**
	 * @return the vendorParam
	 */
	public String getVendorParam() {
		return vendorParam;
	}
	/**
	 * @param vendorParam the vendorParam to set
	 */
	public void setVendorParam(String vendorParam) {
		this.vendorParam = vendorParam;
	}
	public CheckListPrevNextCategoriesVO getCheckListPrevNextCategoriesVO() {
		return checkListPrevNextCategoriesVO;
	}
	
		/**
	 * @return the registryJsonResultString
	 */
	public String getRegistryJsonResultString() {
		return registryJsonResultString;
	}
	/**
	 * @param registryJsonResultString the registryJsonResultString to set
	 */
	public void setRegistryJsonResultString(String registryJsonResultString) {
		this.registryJsonResultString = registryJsonResultString;
	}
	/**
	 * @return the porchZipCode
	 */
	public String getPorchZipCode() {
		return porchZipCode;
	}
	/**
	 * @param porchZipCode the porchZipCode to set
	 */
	public void setPorchZipCode(String porchZipCode) {
		this.porchZipCode = porchZipCode;
	}
	public void setCheckListPrevNextCategoriesVO(
			CheckListPrevNextCategoriesVO checkListPrevNextCategoriesVO) {
		this.checkListPrevNextCategoriesVO = checkListPrevNextCategoriesVO;
	}
	
	public CheckListVO getChecklistVO() {
		return checklistVO;
	}
	public void setChecklistVO(CheckListVO checklistVO) {
		this.checklistVO = checklistVO;
	}
	public boolean isRegDiffDateLess() {
		return regDiffDateLess;
	}
	public void setRegDiffDateLess(boolean regDiffDateLess) {
		this.regDiffDateLess = regDiffDateLess;
	}
	
	/* 
 	 * Start of fix for High Memory Consumption issue - JIRA (PS-56452)
 	 */
 	public void doStopService() throws ServiceException
 	{
 	    logDebug("Start of BBBSessionBean.doStopService() method");
 	    this.values = null;
 	    logDebug("End of BBBSessionBean.doStopService() method");
 	}
 	/* 
 	 * End of fix for High Memory Consumption issue - JIRA (PS-56452)
 	 */
	/**
	 * @return the sbcOauthToken
	 */
	public String getSbcOauthToken() {
		return sbcOauthToken;
	}
	/**
	 * @param sbcOauthToken the sbcOauthToken to set
	 */
	public void setSbcOauthToken(String sbcOauthToken) {
		this.sbcOauthToken = sbcOauthToken;
	}
	/**
	 * @return the sbcTokenExpireTime
	 */
	public long getSbcTokenExpireTime() {
		return sbcTokenExpireTime;
	}
	/**
	 * @param sbcTokenExpireTime the sbcTokenExpireTime to set
	 */
	public void setSbcTokenExpireTime(long sbcTokenExpireTime) {
		this.sbcTokenExpireTime = sbcTokenExpireTime;
	}
	/**
	 * @return the registryPorchServiceRemoved
	 */
	public boolean isRegistryPorchServiceRemoved() {
		return registryPorchServiceRemoved;
	}
	/**
	 * @param registryPorchServiceRemoved the registryPorchServiceRemoved to set
	 */
	public void setRegistryPorchServiceRemoved(boolean registryPorchServiceRemoved) {
		this.registryPorchServiceRemoved = registryPorchServiceRemoved;
	}
	public String getReqFromHomepage() {
		return reqFromHomepage;
	}
	
	public void setReqFromHomepage(String reqFromHomepage) {
		this.reqFromHomepage = reqFromHomepage;
	}
	
 
 

}
