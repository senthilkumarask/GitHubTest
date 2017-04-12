package com.bbb.commerce.giftregistry.manager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.xml.parsers.FactoryConfigurationError;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import com.bbb.account.BBBProfileTools;
import com.bbb.account.api.BBBAddressAPI;
import com.bbb.account.api.BBBAddressVO;
import com.bbb.account.vo.ProfileSyncRequestVO;
import com.bbb.account.vo.ProfileSyncResponseVO;
import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.cms.manager.LandingTemplateManager;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.giftregistry.bean.AddItemsBean;
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean;
import com.bbb.commerce.giftregistry.droplet.GiftRegistryTypesDroplet;
import com.bbb.commerce.giftregistry.droplet.RegistryEventDateComparator;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.utility.BBBGiftRegistryUtils;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.BridalRegistryVO;
import com.bbb.commerce.giftregistry.vo.EventVO;
import com.bbb.commerce.giftregistry.vo.ForgetRegPassRequestVO;
import com.bbb.commerce.giftregistry.vo.GuestRegistryItemsListVO;
import com.bbb.commerce.giftregistry.vo.LinkRegistryToProfileVO;
import com.bbb.commerce.giftregistry.vo.ManageRegItemsResVO;
import com.bbb.commerce.giftregistry.vo.RegAddressesVO;
import com.bbb.commerce.giftregistry.vo.RegCopyResVO;
import com.bbb.commerce.giftregistry.vo.RegInfoVO;
import com.bbb.commerce.giftregistry.vo.RegNamesVO;
import com.bbb.commerce.giftregistry.vo.RegSearchResVO;
import com.bbb.commerce.giftregistry.vo.RegSkuDetailsVO;
import com.bbb.commerce.giftregistry.vo.RegSkuListVO;
import com.bbb.commerce.giftregistry.vo.RegStatusesResVO;
import com.bbb.commerce.giftregistry.vo.RegistrantVO;
import com.bbb.commerce.giftregistry.vo.RegistryBabyVO;
import com.bbb.commerce.giftregistry.vo.RegistryHeaderVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO;
import com.bbb.commerce.giftregistry.vo.RegistryPrefStoreVO;
import com.bbb.commerce.giftregistry.vo.RegistryReqVO;
import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO;
import com.bbb.commerce.giftregistry.vo.RegistryStatusVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryListVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO;
import com.bbb.commerce.giftregistry.vo.SetAnnouncementCardResVO;
import com.bbb.commerce.giftregistry.vo.ValidateAddItemsResVO;
import com.bbb.commerce.inventory.OnlineInventoryManagerImpl;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.email.BBBEmailHelper;
import com.bbb.email.BBBTemplateEmailSender;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.framework.webservices.vo.ValidationError;
import com.bbb.idm.AssociateVO;
import com.bbb.kickstarters.manager.KickStarterManager;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;

/**
 * This class is the business layer object for the management of gift registry
 * (RegistryVO and RegistryListVO) object manipulation. It provides the high
 * level access to gift registry. It makes calls to lower level utilities in
 * GiftlistTools which makes the web service call.
 *
 * @author sku134
 */
public class GiftRegistryManager extends BBBGenericService {

	private String mRegistryInfoServiceName;
    private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	private static final String REGISTRY_ID = "registryId";
	private static final String STATE = "state";
	private static final String EVENT = "eventType";	
	private static final String EMAIL = "email";
	private static final String DEFAULT_SORT_ORDER = "ASCE";
	//private static final String RECOMMENDER_DESCRIPTOR = "recommenderLanding";
	//private static final String HOME_SITE = "site";
	//private static final String CHANNEL = "channel";
	//private static final String REGISTRY_TYPE = "registryType";
	//private static final String RECOMMENDER_TOP = "upperPromoBoxSlot";
	//private static final String RECOMMENDER_BOTTOM = "bottomPromoBoxSlot";
	//private static final String RECOMMENDER_MIDDLE_LIST = "middlePromoBoxSlot";
	public static final String UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_REPOSITORY_EXCEPTION="6001";
	public static final String RETRIEVED_MORE_DATA_FROM_REPOSITORY="6002";
	public static final String RETRIEVED_NO_DATA_FROM_REPOSITORY="6003";
	public static final String SITE_ID_IS_NULL="6004";
	private static String MSG_COUNTRY_MISSING = "Default Country Missing";
	private static String MSG_REG_SERVICE_NOTWORKING = "Registry service not working";
	//private static final String RECOMMENDATION_DESCRIPTOR = "registrantLanding";
	//private static final String REGISTRY_TYPE_NAME = "registryTypeName";
	//private static final String REGISTRYTYPE = "registrytype";
	//private static final String ID = "id";
	//private static final String PROMOBOX_IMAGE_URL = "imageUrl";
	 // private static final String PROMOBOX_IMAGE_ALT_TEXT = "imageAltText";
	 // private static final String PROMOBOX_IMAGE_MAP_NAME = "imageMapName";
	 // private static final String PROMOBOX_IMAGE_MAP_CONTENT = "imageMapContent";
	  //private static final String PROMOBOX_CONTENT = "promoBoxContent";
	 // private static final String PROMO_IMAGE_LINK = "imageLink";
	  //private static final String PROMOIMAGE_LINK_LABEL = "linkLabel";
	  //private static final String PROMOIMAGE_LINK_URL = "linkUrl";
	private static final String BROWSE_ITEMPURCHASED_LIMIT = "buyoffItemPurchasedLimit";
	private static final String BROWSE_UNFULLFILLED_SIZE_LIMIT = "buyoffItemPurchasedPercentageLimit";
	
	private String mTemplateUrl = null;

	private GiftRegistryTools mGiftRegistryTools;
	private BBBCatalogTools mBbbCatalogTools;
	private BBBProfileTools mTools;
	private BBBAddressAPI bbbAddressAPI;
	private BBBTemplateEmailSender mEmailSender;
	private SiteContext siteContext;
	private GiftRegistryTypesDroplet giftRegistryTypesDroplet;

	private String mLinkingCoRegToRegServiceName;
	private String mSearchRegistryServiceName;
	private String mEmailARegistryType;
	private String emailAPRegRegistryType;
	private String mxEmailARegistryType;
	private String mxEmailAPRegRegistryType;
	private String mEmailCoFoundRegistryType;
	private String mEmailCoNotFoundRegistryType;
	private String mLinkCoRegProfileToRegServiceName;
	private String mRegistrySearchServiceName;
	private String mCopyRegistryServiceName;
	private String successUrlKickStarter;
	
	private Repository mRecommenderLandingPageTemplate;
	private LandingTemplateManager mLandingTemplateManager;
	private String mDefaultSite;
	private String mDefaultChannel;
	private String mDefaultRegistryType;
	private String mEmailRegistryRecommendation;
	private Repository mRegistrantLandingPageTemplate;
	private Repository mRegistryTypeRepository;
	private BBBCatalogTools catalogTools;
	private BBBGiftRegistryUtils giftRegUtils;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private BBBEximManager bbbEximPricingManager;
	private MutableRepository catalogRepository;
	private OnlineInventoryManagerImpl inventoryManager;
	
	/**
	 * @return the inventoryManager
	 */
	public OnlineInventoryManagerImpl getInventoryManager() {
		return inventoryManager;
	}

	/**
	 * @param inventoryManager the inventoryManager to set
	 */
	public void setInventoryManager(OnlineInventoryManagerImpl inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * @param pCatalogRepository the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository pCatalogRepository) {
		this.catalogRepository = pCatalogRepository;
	}
	
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	public BBBGiftRegistryUtils getGiftRegUtils() {
		return giftRegUtils;
	}

	public void setGiftRegUtils(BBBGiftRegistryUtils giftRegUtils) {
		this.giftRegUtils = giftRegUtils;
	}

	/**
	 * @return the successUrlKickStarter
	 */
	public String getSuccessUrlKickStarter() {
		return successUrlKickStarter;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	/**
	 * @param pSuccessUrlKickStarter the successUrlKickStarter to set
	 */
	public void setSuccessUrlKickStarter(String pSuccessUrlKickStarter) {
		successUrlKickStarter = pSuccessUrlKickStarter;
	}

	

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	public BBBEximManager getBbbEximPricingManager() {
		return bbbEximPricingManager;
	}
	public void setBbbEximPricingManager(BBBEximManager bbbEximPricingManager) {
		this.bbbEximPricingManager = bbbEximPricingManager;
	}

	/**
	 * This method is used to get the profileId for a given emailId.
	 *
	 * @param emailId
	 *            the email id
	 * @param siteId
	 *            the site id
	 * @return the profile item from email
	 */
	public MutableRepositoryItem getProfileItemFromEmail(final String emailId,
			final String siteId) {

		this.logDebug("GiftRegistryManager.getProfileItemFromEmail() method start");
		final MutableRepositoryItem pProfileItem = (MutableRepositoryItem) this.getTools()
				.getItemFromEmail(emailId);

		this.logDebug("GiftRegistryManager.getProfileItemFromEmail() method ends");

		return pProfileItem;
	}

	/**
	 * This method is used to associate a registry with a registrant.
	 *
	 * @param registryVO
	 *            the registry vo
	 * @return the repository item
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws RepositoryException
	 *             the repository exception
	 */
	private RepositoryItem primaryRegRepoEntry(final RegistryVO registryVO)
			throws BBBSystemException, RepositoryException {
		this.logDebug("GiftRegistryManager.primaryRegRepoEntry() method starts");

		MutableRepositoryItem giftRegistryItem = null;

		// ADD registrant entry to gift registry repository
		final MutableRepositoryItem pProfileItem = this.getProfileItemFromEmail(registryVO
				.getPrimaryRegistrant().getEmail(), registryVO.getSiteId());

		// ADD registrant entry to user repository
    	giftRegistryItem = this.getGiftRegistryTools().addORUpdateRegistry(registryVO, pProfileItem, null);

        this.logDebug("GiftRegistryManager.primaryRegRepoEntry() method ends");

		return giftRegistryItem;

	}

	/**
	 * This method is used to associate a registry with the current user as
	 * either Primary Registrant or Co Registrant.
	 *
	 * @param registryVO
	 *            the registry vo
	 * @param isImportedAsReg
	 *            the is imported as reg
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws RepositoryException
	 *             the repository exception @ author - ikhan2
	 */
	public void linkRegistry(final RegistryVO registryVO, final boolean isImportedAsReg)
			throws BBBSystemException, RepositoryException {
		this.logDebug("GiftRegistryManager.linkRegistry() method starts");

		MutableRepositoryItem pProfileItem = null;
		pProfileItem = this.getProfileItemFromEmail(registryVO.getRegistrantVO()
				.getEmail(), registryVO.getSiteId());

		if (isImportedAsReg) {
			// If WebService has linked the registry to user as
			// Primary-Registrant
			// ADD registrant entry to gift registry repository with owner
			// profile
			this.getGiftRegistryTools().addORUpdateRegistry(registryVO, pProfileItem, null);
		} else {
			// ADD registrant entry to gift registry repository with coOwner
			// profile
			this.getGiftRegistryTools().addORUpdateRegistry(registryVO, null, pProfileItem);
		}
		this.logDebug("GiftRegistryManager.linkRegistry() method ends.");

	}

	/**
	 * This method is used to insert create registry info in repository.
	 *
	 * @param registryVO
	 *            the registry vo
	 * @param coreEmailPopupStatus
	 *            the core email popup status
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public void giftRegistryRepoEntry(final RegistryVO registryVO,
			final String coreEmailPopupStatus) throws BBBSystemException,
			RepositoryException {
		this.logDebug("GiftRegistryManager.giftRegistryRepoEntry() method start");

		final MutableRepositoryItem pProfileItem = this.getProfileItemFromEmail(registryVO
				.getPrimaryRegistrant().getEmail(), registryVO.getSiteId());

		this.primaryRegRepoEntry(registryVO);

		// ADD coRegistrant entry to gift registry repository so coregistrant profile can be linked
    	if("true".equalsIgnoreCase(coreEmailPopupStatus)){
    		final MutableRepositoryItem pCoProfileItem = this.getProfileItemFromEmail(registryVO.getCoRegistrant().getEmail(), registryVO.getSiteId());

    	this.getGiftRegistryTools().addORUpdateRegistry(registryVO,pProfileItem, pCoProfileItem);

		}

		this.logDebug("GiftRegistryManager.giftRegistryRepoEntry() method ends");
	}
	//BPS-448: Send email notification for inviting recommender(s)
	/**
	 * This method is used to send an email with gift registry URL for
	 * recommendation.
	 *
	 * @param siteId
	 *            the site id
	 * @param values
	 *            the values
	 * @param pEmailInfo
	 *            the email info
	 * @param tokensMap
	 *            the tokens map
	 * @param recipientEmailList
	 *            the recipient email list
	 * @return true, if successful
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean sendEmailRegistryRecommendation(final String siteId, final Map<String, Object> values,
			final TemplateEmailInfoImpl pEmailInfo, final Map<String, RepositoryItem> tokensMap)
					throws BBBSystemException {

		this.logDebug("GiftRegistryManager.sendEmailRegistryRecommendation() method starts");

		boolean emailSuccess = true;

		final Map emailParams = new HashMap<String, String>();
		final Map<String, String> placeHolderValues = new HashMap<String, String>();
		final long uniqueKeyDate = getUniquekeyDate();
		String profileId = null;
		final Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName("/atg/userprofiling/Profile");
		if(profile != null){
		    profileId = profile.getRepositoryId();
		}

		final String emailPersistId = profileId + uniqueKeyDate;

		placeHolderValues.put(BBBGiftRegistryConstants.P_REG_FIRST_NAME,
				(String) values.get(BBBGiftRegistryConstants.P_REG_FIRST_NAME));
		placeHolderValues.put(BBBGiftRegistryConstants.P_REG_LAST_NAME,
				(String) values.get(BBBGiftRegistryConstants.P_REG_LAST_NAME));
		placeHolderValues.put(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE,
				(String) values
						.get(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE));

		placeHolderValues.put(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE,
				atg.core.util.StringUtils.escapeHtmlString(
				(String) values.get(BBBGiftRegistryConstants.MESSAGE_REGISTRY)));
		placeHolderValues.put(BBBGiftRegistryConstants.FRMDATA_SITEID, siteId);
		placeHolderValues.put(BBBCoreConstants.EMAIL_PERSIST_ID, emailPersistId);
		placeHolderValues.put(BBBGiftRegistryConstants.EMAIL_TYPE,
				this.getEmailRegistryRecommendation());
		emailParams.put(BBBCoreConstants.TEMPLATE_URL_PARAM_NAME,
				this.getTemplateUrl());

		try {
			for (Map.Entry<String, RepositoryItem> tokens : tokensMap.entrySet()) {
				String tokenId = tokens.getKey();
				RepositoryItem invitee = tokens.getValue();
				String registryUrl = (String) values.get(BBBCoreConstants.REGISTRY_URL);
				registryUrl = registryUrl + BBBGiftRegistryConstants.AMP_TOKEN + tokenId;
				logDebug("GiftRegistryManager --- The registry url is " + registryUrl);
				placeHolderValues.put(BBBCoreConstants.REGISTRY_URL, registryUrl);
				emailParams.put(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES, placeHolderValues);
				String email = (String) invitee.getPropertyValue(BBBCatalogConstants.INVITEE_EMAILI_ID);
				emailParams.put(BBBCoreConstants.RECIPIENT_EMAIL_PARAM_NAME, email);
				pEmailInfo.setMessageTo(email);
				BBBEmailHelper.sendEmail(null, emailParams, this.getEmailSender(),
						pEmailInfo);
			}

		} catch (final TemplateEmailException e) {
			emailSuccess = false;
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10123+" BBBBusinessException of sendEmailRegistryRecommendation from GiftRegistryManager",e);
		} catch (final RepositoryException rex) {
			emailSuccess = false;
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10124+" RepositoryException of sendEmailRegistryRecommendation from GiftRegistryManager",rex);
		}

		this.logDebug("GiftRegistryManager.sendEmailRegistryRecommendation() method ends");

		return emailSuccess;
	}
	
	/**
	 * This method will be used to get active registries.
	 * @param pbbbSessionBean - session bean
	 * @param request - dynamo request
	 * @param pProfile - profile Object
	 * @param pGiftRegSessionBean - GiftRegSessionBean Object
	 */
	public void getActiveRegistry(BBBSessionBean pbbbSessionBean ,  DynamoHttpServletRequest request , Profile pProfile , GiftRegSessionBean pGiftRegSessionBean){
		
       BBBPerformanceMonitor.start( KickStarterManager.class.getName() + " : " + "getActiveRegistry");
	   logDebug("Enter.KickStarterManager.getActiveRegistry");
		
		String pSiteId = getSiteId();

		List<String> userRegList = new ArrayList<String>();
		List<String> userActiveRegList = new ArrayList<String>();
		String pRecentRegistryId = BBBCoreConstants.BLANK;
		RegistrySummaryVO pRegSummaryVO = null;
		request.setParameter(BBBCoreConstants.SITE_ID, pSiteId);

		if (pProfile.isTransient()) {
			request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_NOT_LOGGED_IN);
		} else {
			BBBSessionBean sessionBean = pbbbSessionBean;
			final HashMap sessionMap = sessionBean.getValues();
			pRegSummaryVO = (RegistrySummaryVO) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
			userRegList = (List) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_LIST);
			userActiveRegList = (List) sessionMap.get(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST);
			String acceptableStatuses = getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY);
			List<String> acceptableStatusesList = new ArrayList<String>();
			if (!BBBUtility.isEmpty(acceptableStatuses)) {
				String[] statusesArray = acceptableStatuses.split(BBBCoreConstants.COMMA);
				acceptableStatusesList.addAll(Arrays.asList(statusesArray));
			}

			// Get Registry Data from the Database
			if (userActiveRegList == null || userActiveRegList.isEmpty()) {
				RepositoryItem[] userRegistriesRepItems;
				try {
					logDebug("KickStarterManager getActiveRegistry : Fetch user registries from database");
					userRegistriesRepItems =  fetchUserRegistries(pSiteId,pProfile.getRepositoryId());
					// Set Active Registry Data
					if (userRegistriesRepItems != null) {
						userRegList = new ArrayList<String>(userRegistriesRepItems.length);
						userActiveRegList = new ArrayList<String>(userRegistriesRepItems.length);
						for (RepositoryItem repositoryItem : userRegistriesRepItems) {
							String registryId = repositoryItem.getRepositoryId();
							String registryStatus =  getRegistryStatusFromRepo(pSiteId,	registryId);
							if (acceptableStatusesList.contains(registryStatus)) {
								userActiveRegList.add(registryId);
							}
							userRegList.add(registryId);
						}
						if(!BBBUtility.isListEmpty(userActiveRegList)){
							if(userActiveRegList.size() == 1){
								if (pRegSummaryVO == null ) {
									pRecentRegistryId = (String) userActiveRegList.get(0);
									pRegSummaryVO =  getRegistryInfo( pRecentRegistryId, pSiteId);
									sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								}else{
									sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								}
								if (BBBGiftRegistryConstants.GR_CREATE.equals(pGiftRegSessionBean.getRegistryOperation())
									|| BBBGiftRegistryConstants.GR_UPDATE.equals(pGiftRegSessionBean.getRegistryOperation())
									|| BBBGiftRegistryConstants.GR_ITEM_REMOVE.equals(pGiftRegSessionBean.getRegistryOperation())
									|| BBBGiftRegistryConstants.OWNER_VIEW.equals(pGiftRegSessionBean.getRegistryOperation())) {
								sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
								request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								}else{
									Long diffDays = (long) 0;
									if(pRegSummaryVO != null && pRegSummaryVO.getEventDate()!=null)
									{
										diffDays = getDateDiff(pSiteId, pRegSummaryVO);
									}
									if (diffDays >= -90) {
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_SINGLE_REGISTRY);
									} else {
										request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, null);
										request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
									}
								}
							}
							if( userActiveRegList.size() > 1){
								if (pRegSummaryVO == null ) {
									pRecentRegistryId =  fetchUsersSoonestOrRecent(userActiveRegList);
									// cases when the user has more than 1 registries and all not having event date. The recent registry id fetch is null.
									// Registry summary vo in that case is not populated.
									// FIxed as part of ILD-20
									if(pRecentRegistryId==null){
										pRecentRegistryId=userActiveRegList.get(0);
									}
									pRegSummaryVO =  getRegistryInfo( pRecentRegistryId, pSiteId);
									sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								}else{
									sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								}
								if (BBBGiftRegistryConstants.GR_CREATE.equals(pGiftRegSessionBean.getRegistryOperation())
										|| BBBGiftRegistryConstants.GR_UPDATE.equals(pGiftRegSessionBean.getRegistryOperation())
										|| BBBGiftRegistryConstants.GR_ITEM_REMOVE.equals(pGiftRegSessionBean.getRegistryOperation())
										|| BBBGiftRegistryConstants.OWNER_VIEW.equals(pGiftRegSessionBean.getRegistryOperation())) {
									sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								}else{
									Long diffDays =(long) 0;
									if(pRegSummaryVO != null && pRegSummaryVO.getEventDate()!=null)
									{
										diffDays = getDateDiff(pSiteId, pRegSummaryVO);
									}
									if (diffDays >= -90) {
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES);
									} else {
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, null);
									request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
									}
								}
							}
							
							
						}											
					}
					
					sessionBean.getValues().put(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST,userActiveRegList);
					sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_LIST,userRegList);
					logDebug(" KickStarterManager : Set Active User registries in Sessionbean " + userActiveRegList);
				
					if(userActiveRegList == null || userActiveRegList.isEmpty()){
						request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
						logDebug("KickStarterManager : User Status set to : USER_LOGGED_IN_WITH_NO_REGISTRIES");
					
						}
					}

				catch (RepositoryException e) {
					logError(LogMessageFormatter.formatMessage(request,	"Repository Exception from service of GetRegistryFlyoutDroplet ", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1075), e);
				} catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(request,	"System Exception from service of GetRegistryFlyoutDroplet ", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1076), e);
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(request,	"BBBBusinessException from SERVICE of GiftRegistryFlyoutDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005), e);
				} catch (Exception ex) {
					logError(LogMessageFormatter.formatMessage(request,	"Other Exception from service of GetRegistryFlyoutDroplet ", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1077), ex);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.BBB_SYSTEM_EXCEPTION);
				}
			}
			else if (userActiveRegList != null && userActiveRegList.size() == 1) {
				try {
					logDebug("KickStarterManager : User_Status set to : USER_LOGGED_IN_WITH_SINGLE_REGISTRY");
					if (pRegSummaryVO == null) {
						pRecentRegistryId = (String) userActiveRegList.get(0);
						pRegSummaryVO =  getRegistryInfo( pRecentRegistryId, pSiteId);
						sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
					}

					if (pRegSummaryVO != null) {
						String registryStatus = getRegistryStatusFromRepo(pSiteId,	pRegSummaryVO.getRegistryId());
						if (acceptableStatusesList.contains(registryStatus)) {
							if (BBBGiftRegistryConstants.GR_CREATE.equals(pGiftRegSessionBean.getRegistryOperation())
								|| BBBGiftRegistryConstants.GR_UPDATE.equals(pGiftRegSessionBean.getRegistryOperation())
								|| BBBGiftRegistryConstants.GR_ITEM_REMOVE.equals(pGiftRegSessionBean.getRegistryOperation())
								|| BBBGiftRegistryConstants.OWNER_VIEW.equals(pGiftRegSessionBean.getRegistryOperation())) {
							request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
							request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_SINGLE_REGISTRY);
							} else {
							// check if user registry event is past date and more than 90 days.
							Long diffDays =(long) 0;
							if(pRegSummaryVO.getEventDate()!=null)
							{
								diffDays = getDateDiff(pSiteId, pRegSummaryVO);
							}
							if (diffDays >= -90) {
								request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_SINGLE_REGISTRY);
							} else {
								request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
							}
						}
					}else{
						pRecentRegistryId =  fetchUsersSoonestOrRecent(userActiveRegList);
						logDebug(" KickStarterManager  MSG= soonestOrRecent pRecentRegistryId =" + pRecentRegistryId);
			
						pRegSummaryVO =  getRegistryInfo(pRecentRegistryId, pSiteId);
						sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
						request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES);
						request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
						}
					} else {
						request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
					}

				} catch (NumberFormatException e) {
					request.setParameter(BBBCoreConstants.USER_STATUS, "7");
					logError("NumberFormatException " + e);
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(request,	"BBBBusinessException from service of GiftRegistryFlyoutDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.BBB_BUSINESS_EXCEPTION);
				} catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(request, "BBBBusinessException from SERVICE of GiftRegistryFlyoutDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005), e);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.BBB_SYSTEM_EXCEPTION);
				} catch (Exception ex) {
					logError("BBBSystemException " + ex);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.BBB_SYSTEM_EXCEPTION);
				}
			} else if (userActiveRegList != null && userActiveRegList.size() > 1) {
				try {

					logDebug("KickStarterManager : User_Status set to : USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES");
				
					if (pRegSummaryVO == null) {
						pRecentRegistryId =  fetchUsersSoonestOrRecent(userActiveRegList);
						// cases when the user has more than 1 registries and all not having event date. The recent registry id fetch is null.
						// Registry summary vo in that case is not populated.
						// FIxed as part of ILD-20
						if(pRecentRegistryId==null){
							pRecentRegistryId=userActiveRegList.get(0);
						}
						logDebug(" KickStarterManager MSG= soonestOrRecent pRecentRegistryId =" + pRecentRegistryId);
				
						pRegSummaryVO =  getRegistryInfo(pRecentRegistryId, pSiteId);
						sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
						
						logDebug(" KickStarterManager  MSG= soonestOrRecent pRegSummaryVO =" + pRegSummaryVO);
						
						}

					if (pRegSummaryVO != null) {
						String registryStatus =  getRegistryStatusFromRepo(pSiteId,	pRegSummaryVO.getRegistryId());
						if (acceptableStatusesList.contains(registryStatus)) {
							if (BBBGiftRegistryConstants.GR_CREATE.equals(pGiftRegSessionBean.getRegistryOperation())
								|| BBBGiftRegistryConstants.GR_UPDATE.equals(pGiftRegSessionBean.getRegistryOperation())
								|| BBBGiftRegistryConstants.GR_ITEM_REMOVE.equals(pGiftRegSessionBean.getRegistryOperation())
								|| BBBGiftRegistryConstants.OWNER_VIEW.equals(pGiftRegSessionBean.getRegistryOperation())) {
							request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
							request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES);

							} else {

							Long diffDays =(long) 0;
							if(pRegSummaryVO.getEventDate()!=null)
							{
									diffDays = getDateDiff(pSiteId, pRegSummaryVO);
							}
							if (diffDays >= -90) {
								request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES);
							} else {
								pRecentRegistryId =  fetchUsersSoonestRegistry(userActiveRegList, pSiteId);
								if(pRecentRegistryId == null){
									request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
								}else{
									
									logDebug("KickStarterManager MSG= soonestOrRecent pRecentRegistryId =" + pRecentRegistryId);
									
									pRegSummaryVO =  getRegistryInfo(pRecentRegistryId, pSiteId);
									sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
									request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_MULTIPLE_REGISTRIES);
									request.setParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO, pRegSummaryVO);
								}
							}
						}
						}
					}else {
						request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.USER_LOGGED_IN_WITH_NO_REGISTRIES);
					}

				} catch (NumberFormatException e) {
					request.setParameter(BBBCoreConstants.USER_STATUS, "7");
					logError("NumberFormatException " + e);
				} catch (RepositoryException e) {
					request.setParameter(BBBCoreConstants.USER_STATUS, "8");
					logError("RepositoryException " + e);
				} catch (BBBBusinessException e) {
					logError(LogMessageFormatter.formatMessage(request, "BBBBusinessException from service of GiftRegistryFlyoutDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.BBB_BUSINESS_EXCEPTION); 
				} catch (BBBSystemException e) {
					logError(LogMessageFormatter.formatMessage(request,	"BBBBusinessException from SERVICE of GiftRegistryFlyoutDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005), e);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.BBB_SYSTEM_EXCEPTION);
				} catch (Exception ex) {
					logError("BBBSystemException " + ex);
					request.setParameter(BBBCoreConstants.USER_STATUS, BBBCoreConstants.BBB_SYSTEM_EXCEPTION);
					}
				}
			if (userActiveRegList == null || userActiveRegList.isEmpty()) {
				request.setParameter(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_SIZE, 0);
			}
			else{
				int userActiveRegSize = 0;
				if(sessionBean.getValues().get(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST) !=null){
					userActiveRegSize = ((ArrayList<String>) sessionBean.getValues().get(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST)).size();
				}				
				request.setParameter(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_SIZE, userActiveRegSize);
			}
		}

		if (pRegSummaryVO != null) {
			
			logDebug(" GiftRegistryFlyoutDroplet service() MSG= soonestOrRecent set pRegSummaryVO info "
					+ " id :"	+ pRegSummaryVO.getRegistryId()
					+ " eventDate :" + pRegSummaryVO.getEventDate()
					+ " eventType :" + pRegSummaryVO.getEventType()
					+ " giftReg purchased :" + pRegSummaryVO.getGiftPurchased()
					+ " giftReg count :" + pRegSummaryVO.getGiftRegistered()
					+ " primaryRName :"	+ pRegSummaryVO.getPrimaryRegistrantFirstName()
					+ " coRegFname :"	+ pRegSummaryVO.getCoRegistrantFirstName());
			
			}
		
		     logDebug("Exit.KickStarterManager.getActiveRegistry");
			
	BBBPerformanceMonitor.end( KickStarterManager.class.getName() + " : " + "getActiveRegistry");
	
	}

	protected long getDateDiff(String pSiteId, RegistrySummaryVO pRegSummaryVO) throws ParseException {
		return BBBUtility.getDateDiff(pRegSummaryVO.getEventDate(), pSiteId);
	}
	/**
	 * This method is used to update gift registry repository.
	 *
	 * @param registryVO
	 *            the registry vo
	 * @param coEmailPopupStatus
	 *            the co email popup status
	 * @param coEmailNotFoundPopup
	 *            the co email not found popup
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public void giftRegistryRepoUpdate(final RegistryVO registryVO,
			final String coEmailPopupStatus, final String coEmailNotFoundPopup)
			throws BBBSystemException, BBBBusinessException,
			RepositoryException {

		this.logDebug("GiftRegistryManager.giftRegistryRepoUpdate() method start");

		// ADD registrant entry to gift registry repository
		final MutableRepositoryItem pProfileItem = this.getProfileItemFromEmail(registryVO
				.getPrimaryRegistrant().getEmail(), registryVO.getSiteId());

		// ADD coRegistrant entry to gift registry repository so coregistrant profile can be linked
		MutableRepositoryItem pCoProfileItem=null;
		if("true".equalsIgnoreCase(coEmailPopupStatus) && (registryVO.getCoRegistrant()!=null) && (registryVO.getCoRegistrant().getEmail()!=null)){
    		 pCoProfileItem = this.getProfileItemFromEmail(registryVO.getCoRegistrant().getEmail(), registryVO.getSiteId());
		}
		this.getGiftRegistryTools().addORUpdateRegistry(registryVO, pProfileItem,
				pCoProfileItem);

		this.logDebug("GiftRegistryManager.giftRegistryRepoUpdate() method ends");
	}


	/**
	 * This method is invoked to get the shipping address for registry items.If
	 * items are added to the cart from registry then during checkout
	 *
	 * @param registryId
	 *            the registry id
	 * @param siteId
	 *            the site id
	 * @return RegistrySummaryVO
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public RegistrySummaryVO getRegistryInfo(final String registryId, final String siteId)
			throws BBBBusinessException, BBBSystemException {
		this.logDebug("GiftRegistryManager.getRegistryInfo() method start");
		RegistrySummaryVO registrySummaryVO =null;
		RegistrySummaryVO registrySummaryVOForCart=null;

		//avoid web service call in case of add to cart click
		final DynamoHttpServletRequest pRequest=ServletUtil.getCurrentRequest();
		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
				.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		 final HashMap<?, ?> sessionMap = sessionBean.getValues();
		 final List<RegistrySummaryVO> usersCurrentlyViewedRegistry = (List) sessionMap
					.get(BBBSearchBrowseConstants.LAST_VIEWED_REGISTRY_ID_LIST);
		 if((null!=usersCurrentlyViewedRegistry) && (null!=usersCurrentlyViewedRegistry.get(0)))
		 {
		  registrySummaryVOForCart = usersCurrentlyViewedRegistry.get(0);
	     }
		 if((null!=registrySummaryVOForCart)&&registrySummaryVOForCart.getRegistryId().equalsIgnoreCase(registryId))
		 {
		 registrySummaryVO=registrySummaryVOForCart;
		 }
		 else
		{
		final RegistryReqVO regReqVO = new RegistryReqVO();
		final String siteID = this.getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0);
		regReqVO.setRegistryId(registryId);
		regReqVO.setServiceName(this.getRegistryInfoServiceName());
		regReqVO.setSiteId(siteID);
		regReqVO.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY,
				BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));

		RegistryResVO registryResVO = null;

		registryResVO = getRegistryInfoFromWSorDB(regReqVO);

		this.logDebug("GiftRegistryManager.getRegistryInfo() MSG= registryResVO from :" + registryResVO);

		if (null != registryResVO) {
			registrySummaryVO = registryResVO.getRegistrySummaryVO();
			if (!((registryResVO.getRegistryVO() == null) || (registryResVO.getRegistryVO().getPrimaryRegistrant() == null))) {
					registrySummaryVO.setRegistrantEmail(registryResVO.getRegistryVO().getPrimaryRegistrant().getEmail());
			}
			
			if(null != registrySummaryVO){
				if(registryResVO.getRegistryVO() != null){
					registrySummaryVO.setEventVO(registryResVO.getRegistryVO().getEvent());
					registrySummaryVO.setPrimaryRegistrantMobileNum(registryResVO.getRegistryVO().getPrimaryRegistrant().getCellPhone());
		}
				
				registrySummaryVO.setEventType(this.getCatalogTools()
						.getRegistryTypeName(
								registrySummaryVO.getRegistryType()
										.getRegistryTypeName(),siteId));
			}
		}

		// sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY_CART, registrySummaryVO);
		 }

		// Setting type desc - wedding for BRD, baby for BA1
		if ((null != registrySummaryVO) && (null != this.getBbbCatalogTools()) && (null != registrySummaryVO.getRegistryType())) {
			registrySummaryVO.getRegistryType().setRegistryTypeDesc(
					this.getBbbCatalogTools().getRegistryTypeName(registrySummaryVO.getRegistryType().getRegistryTypeName(), siteId));
		}
		this.logDebug("GiftRegistryManager.getRegistryInfo() MSG= registryResVO.registrySummaryVO from webservice :" + registrySummaryVO);

		this.logDebug("GiftRegistryManager.getRegistryInfo() method ends");

		if ((registrySummaryVO != null) && (registrySummaryVO.getEventDate() != null)) {
			if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
				registrySummaryVO.setEventDate(BBBUtility.convertWSDateToCAFormat(registrySummaryVO.getEventDate()));
				if(registrySummaryVO.getFutureShippingDate() != null){
					registrySummaryVO.setFutureShippingDate(BBBUtility.convertAppFormatDateToCAFormat(registrySummaryVO.getFutureShippingDate()));
				}
			}else{
				registrySummaryVO.setEventDate(BBBUtility.convertWSDateToUSFormat(registrySummaryVO.getEventDate()));
			}

		}
	//	registrySummaryVO = maskRegistrySummaryVO(registrySummaryVO);
		return registrySummaryVO;
	}
	
	private RegistrySummaryVO maskRegistrySummaryVO(RegistrySummaryVO registrySummaryVO)
	  {
	    DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
	    Profile profile = (Profile)request.resolveName("/atg/userprofiling/Profile");
	    Integer COOKIE_LOGIN_SECURITY_STATUS = Integer.valueOf(2);
	    Integer securityStatus = (Integer)profile.getPropertyValue("securityStatus");
	    if ((profile.isTransient()) || (securityStatus.intValue() < COOKIE_LOGIN_SECURITY_STATUS.intValue()))
	    {
	      RegistrySummaryVO maskRegistrySummaryVO = new RegistrySummaryVO();
	      maskRegistrySummaryVO.setRegistryId(registrySummaryVO.getRegistryId());
	      maskRegistrySummaryVO.setRegistryType(registrySummaryVO.getRegistryType());
	      maskRegistrySummaryVO.setCoRegistrantFirstName(registrySummaryVO.getCoRegistrantFirstName());
	      maskRegistrySummaryVO.setPrimaryRegistrantFirstName(registrySummaryVO.getPrimaryRegistrantFirstName());
	      maskRegistrySummaryVO.setEventType(registrySummaryVO.getEventType());
	      return maskRegistrySummaryVO;
	    }
	    return registrySummaryVO;
	  }
	
//	public RegistryResVO getRegistrySummaryVO(String registryId, String siteId)
//	throws BBBSystemException, BBBBusinessException, RepositoryException {
//		
//		RegistryResVO registryResVO =null;
//		RegistrySummaryVO registrySummaryVOForCart=null;
//		RegistrySummaryVO registrySummaryVO=null;
//		
//		RepositoryItem[] regAddrItems = null;
//		RepositoryItem[] registryInfo=null;
//		registryInfo = this.getRegistryInfoFromDB(registryId, siteId);
//		regAddrItems = this.getRegistryAddressesFromDB(registryId);
//		if(registryInfo!=null){
//			for(RepositoryItem item:registryInfo){
//				registryResVO.setRegistryId((Long)item.getPropertyValue("registryNum"));
//				registrySummaryVO
//			}
//			
//			
//			
//		}
//		
//		return registryResVO;
//		}
	
	public boolean canScheuleAppointment(String storeId, String registryType, String siteId) {
		boolean isUserAllowedToScheduleAppointment = getGiftRegistryTools().canScheduleAppointmentForRegType(siteId, registryType);
		isUserAllowedToScheduleAppointment = isUserAllowedToScheduleAppointment ? getGiftRegistryTools().canScheduleAppointmentForStore(storeId, registryType)
				: isUserAllowedToScheduleAppointment;
		return isUserAllowedToScheduleAppointment;
	}
	
	
	public RegInfoVO getRegistryInfoFromDB(String registryId, String siteId)
	throws BBBSystemException, BBBBusinessException, RepositoryException {


		RepositoryItem[] registryItems=null;
		RegInfoVO regInfoVO=new RegInfoVO();
		registryItems= this.getGiftRegistryTools().getRegistryInfoFromDB(registryId,siteId);
		if(registryItems!=null){
				regInfoVO.setRegistryNum((Integer)registryItems[0].getPropertyValue("registryNum"));
				regInfoVO.setEventType((String)registryItems[0].getPropertyValue("eventType"));
				regInfoVO.setEventDate((Integer)registryItems[0].getPropertyValue("eventDate"));
				regInfoVO.setActionCD((String) registryItems[0].getPropertyValue("actionCD"));
				regInfoVO.setGiftWrap((String)registryItems[0].getPropertyValue("GiftWrap"));
				regInfoVO.setOnlineRegFlag((String)registryItems[0].getPropertyValue("onlineRegFlag"));
    			regInfoVO.setProcessFlag((String)registryItems[0].getPropertyValue("processFlag"));
				
		}
		
		return regInfoVO;
		}
	
	public RegAddressesVO getRegistryAddressesFromDB(String registryId)
	throws BBBSystemException, BBBBusinessException, RepositoryException {

		RepositoryItem[] regAddressItems=null;
		RegAddressesVO regAddressesVO=new RegAddressesVO();
		List<RegNamesVO> addressesVO=new ArrayList<RegNamesVO>();
		// Call GiftRegistryTools API getRegistryInfo(regReqVO)
		regAddressItems= this.getGiftRegistryTools().getRegistryAddressesFromDB(registryId);
		for(RepositoryItem item:regAddressItems){
			RegNamesVO regNamesVO=new RegNamesVO();
			regNamesVO.setRegistryNum((Integer)item.getPropertyValue("registryNum"));
			regNamesVO.setNameAddrNum((Integer)item.getPropertyValue("nameAddrNum"));
			regNamesVO.setNameAddrType((String)item.getPropertyValue("nameAddrType"));
			regNamesVO.setLastName((String)item.getPropertyValue("lastName"));
			regNamesVO.setFirstName((String)item.getPropertyValue("firstName"));
			regNamesVO.setAddress1((String)item.getPropertyValue("address1"));
			regNamesVO.setAddress2((String)item.getPropertyValue("address2"));
			regNamesVO.setCity((String)item.getPropertyValue("city"));
			regNamesVO.setState((String)item.getPropertyValue("state"));
			regNamesVO.setZipCode((String)item.getPropertyValue("zipCode"));
			regNamesVO.setDayPhone((String)item.getPropertyValue("dayPhone"));
			regNamesVO.setDayPhoneExt((String)item.getPropertyValue("dayPhoneExt"));			
			addressesVO.add(regNamesVO);
		}
		regAddressesVO.setRegAddressVO(addressesVO);
		
		
		
		return regAddressesVO;
		}
	
	public RegSkuListVO getRegistrySkuDetailsFromDB(String registryId)
	throws BBBSystemException, BBBBusinessException, RepositoryException {

		RepositoryItem[] registrySkus=null;
		
		RegSkuListVO regSkuListVO=new RegSkuListVO();
		List<RegSkuDetailsVO> skuList=new ArrayList<RegSkuDetailsVO>();

		registrySkus= this.getGiftRegistryTools().getRegistrySkuDetailsFromDB(registryId);
		if(registrySkus!=null){
			for(RepositoryItem item:registrySkus){
				RegSkuDetailsVO regSkuDetailsVO=new RegSkuDetailsVO();
				regSkuDetailsVO.setRegistryNum((Integer)item.getPropertyValue("registryNum"));
				regSkuDetailsVO.setSkuId((Integer)item.getPropertyValue("skuId"));
				regSkuDetailsVO.setQtyFulfilled((Integer)item.getPropertyValue("qtyFulfilled"));
				regSkuDetailsVO.setQtyRequested((Integer)item.getPropertyValue("qtyRequested"));
				regSkuDetailsVO.setActionCD((String)item.getPropertyValue("actionCD"));
				skuList.add(regSkuDetailsVO);
			}
			regSkuListVO.setRegAddressVO(skuList);
		}
		return regSkuListVO;
		}
	
	

	public RegistrySummaryVO getRegistryInfoFromWebService(final String registryId, final String siteId)
			throws BBBBusinessException, BBBSystemException {

		this.logDebug("GiftRegistryManager.getRegistryInfoFromWebService() method start");
		RegistrySummaryVO registrySummaryVO =null;

		final RegistryReqVO registryRequestVO = new RegistryReqVO();
		registryRequestVO.setRegistryId(registryId);
		registryRequestVO.setServiceName(this.getRegistryInfoServiceName());
		registryRequestVO.setSiteId(this.getBbbCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
		registryRequestVO.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY,
				BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));

		final RegistryResVO registryResVO = this.getGiftRegistryTools()
				.getRegistryInfo(registryRequestVO);

		this.logDebug("GiftRegistryManager.getRegistryInfoFromWebService() MSG= registryResVO from webservice :" + registryResVO);

		registrySummaryVO = registryResVO.getRegistrySummaryVO();
		if (!((registryResVO.getRegistryVO() == null) || (registryResVO.getRegistryVO().getPrimaryRegistrant() == null))) {
			registrySummaryVO.setRegistrantEmail(registryResVO.getRegistryVO().getPrimaryRegistrant().getEmail());
		}

		// Setting type desc - wedding for BRD, baby for BA1
		if ((null != registrySummaryVO) && (null != this.getBbbCatalogTools()) && (null != registrySummaryVO.getRegistryType())) {
			registrySummaryVO.getRegistryType().setRegistryTypeDesc(
					this.getBbbCatalogTools().getRegistryTypeName(registrySummaryVO.getRegistryType().getRegistryTypeName(), siteId));
		}
		this.logDebug("GiftRegistryManager.getRegistryInfoFromWebService() MSG= registryResVO.registrySummaryVO from webservice :" + registrySummaryVO);

		this.logDebug("GiftRegistryManager.getRegistryInfoFromWebService() method ends");

		if ((registrySummaryVO != null) && (registrySummaryVO.getEventDate() != null)) {
			if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
				registrySummaryVO.setEventDate(BBBUtility.convertWSDateToCAFormat(registrySummaryVO.getEventDate()));
				if(registrySummaryVO.getFutureShippingDate() != null){
					registrySummaryVO.setFutureShippingDate(BBBUtility.convertAppFormatDateToCAFormat(registrySummaryVO.getFutureShippingDate()));
				}
			}else{
				registrySummaryVO.setEventDate(BBBUtility.convertWSDateToUSFormat(registrySummaryVO.getEventDate()));
			}

		}
		return registrySummaryVO;
	}


	/**
	 * This method will invoke to get the registry info for updation.
	 *
	 * @param registryId
	 *            the registry id
	 * @param siteId
	 *            the site id
	 * @return RegistryVO
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public RegistryVO getRegistryDetailInfo(final String registryId, final String siteId)
			throws BBBBusinessException, BBBSystemException {
		this.logDebug("GiftRegistryManager.getRegistryDetailInfo() method start");

		final RegistryReqVO regReqVO = new RegistryReqVO();
		regReqVO.setRegistryId(registryId);
		regReqVO.setServiceName(this.getRegistryInfoServiceName());
		regReqVO.setSiteId(this.getBbbCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
		regReqVO.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY,
				BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));

		final RegistryResVO registryResVO = this.getRegistryInfo(regReqVO);
			this.logDebug("GiftRegistryManager.getRegistryDetailInfo() method ends");

		if (registryResVO == null) {
			return null;
		}

		if (registryResVO.getServiceErrorVO() == null || (!registryResVO.getServiceErrorVO().isErrorExists() && BBBUtility.isEmpty(registryResVO.getServiceErrorVO().getErrorMessage()))) {
			if (null != registryResVO.getRegistryVO() &&  StringUtils.isEmpty(registryResVO.getRegistryVO().getRegistryType().getRegistryTypeDesc())) {
				final String registryTypeDesc = this.getBbbCatalogTools().getRegistryTypeName(
								registryResVO.getRegistryVO().getRegistryType().getRegistryTypeName(), siteId);
				registryResVO.getRegistryVO().getRegistryType().setRegistryTypeDesc(registryTypeDesc);
			}
		} else {
			this.logError("GiftRegistryManager.getRegistryDetailInfo recieved Error in service response for registryid"
					+ registryId + " Error = " + registryResVO.getServiceErrorVO().getErrorDisplayMessage());
		}
		
		return registryResVO.getRegistryVO();
	}

	/**
	 * this method fetches all the registry types configured in the associated site
	 *
	 * @return returns the list of registryVOs
	 * @throws RepositoryException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked" })
	public List<RegistryTypeVO> getRegistryTypes() throws RepositoryException, BBBSystemException, BBBBusinessException {
		this.logDebug("GiftRegistryManager  method :getRegistryTypes entering");

		final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		final DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
		pRequest.setParameter("siteId",getSiteId());
		try {
			this.getGiftRegistryTypesDroplet().service(pRequest, pResponse);
		} catch (final IOException e) {
			this.logError("IOException in getRegistryTypes method in GiftRegistryManager ",e);
			throw new BBBSystemException(e.getMessage(), e);
		} catch (final ServletException e) {
			this.logError("ServletException in getRegistryTypes method in GiftRegistryManager ",e);
			throw new BBBSystemException(e.getMessage(), e);
		}
		List<RegistryTypeVO> registryTypeVOList = null;
		if(pRequest.getObjectParameter("registryTypes") != null){
			registryTypeVOList = (List<RegistryTypeVO>) pRequest.getObjectParameter("registryTypes");
		}
		this.logDebug("GiftRegistryManager  method :getRegistryTypes exiting");
		return registryTypeVOList;
	}

	/**
	 * fetching registry.
	 *
	 * @param siteId
	 *            the site id
	 * @return registryTypes
	 * @throws RepositoryException
	 *             the repository exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public List<RegistryTypeVO> fetchRegistryTypes(final String siteId)
			throws RepositoryException, BBBSystemException,
			BBBBusinessException {
		this.logDebug("GiftRegistryManager.fetchRegistryTypes() method start");

		final List<RegistryTypeVO> registryTypes = this.getBbbCatalogTools()
				.getRegistryTypes(siteId);
		this.logDebug("GiftRegistryManager.fetchRegistryTypes() method ends");

		return registryTypes;
	}

	/**
	 * getting user registry list.
	 *
	 * @param profileId
	 *            the profile id
	 * @param siteId
	 *            the site id
	 * @return registrySkinnyVOList
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws RepositoryException
	 *             the repository exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public List<RegistrySkinnyVO> getUserRegistryList(final Profile profileId,
			final String siteId) throws BBBBusinessException, RepositoryException,
			BBBSystemException {
		return this.getGiftRegistryTools().getUserRegistryList(profileId, siteId);
	}

	/*
	 * 
	 * 
	 */
	public Integer getUserRegistryListCount(final Profile profile, final String siteId) throws BBBBusinessException,
			RepositoryException, BBBSystemException {

		Integer count = 0;
		
		RepositoryItem[] registryIdRepItems = this.getGiftRegistryTools().fetchUserRegistries(siteId,
				profile.getRepositoryId(), true);

		if (null != registryIdRepItems) {

			count = registryIdRepItems.length;
		}

		return count;
	}
	/**
	 * getting user registry list for wedding and commitment registry.
	 *
	 * @param profileId
	 *            the profile id
	 * @param siteId
	 *            the site id
	 * @return registrySkinnyVOList
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws RepositoryException
	 *             the repository exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public List<BridalRegistryVO> getBridalRegistries(final Profile profileId,
			final String siteId) throws BBBBusinessException, RepositoryException,
			BBBSystemException {

		final List<RegistrySkinnyVO> registryIdRepItems = this.getGiftRegistryTools().getFutureRegistryList(profileId, siteId);
		final List<BridalRegistryVO> bridalRegistryVOList = new ArrayList<BridalRegistryVO>();
		final String acceptableStatuses = this.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY);
		final List<String> acceptableStatusesList = new ArrayList<String>();
		if(!BBBUtility.isEmpty(acceptableStatuses)) {
			final String[] statusesArray = acceptableStatuses.split(BBBCoreConstants.COMMA);
			acceptableStatusesList.addAll(Arrays.asList(statusesArray));
		}
		if(registryIdRepItems != null){
			final RegistryEventDateComparator eventDateComparator = new RegistryEventDateComparator();
			eventDateComparator.setSortOrder(1);
			Collections.sort(registryIdRepItems, eventDateComparator);
			for(int index = 0; index < registryIdRepItems.size(); index++){
				final BridalRegistryVO bridalRegistryVO = new BridalRegistryVO();
				final String eventCode = registryIdRepItems.get(index).getEventCode();
				final String registryStatus = registryIdRepItems.get(index).getStatus();
				if (eventCode.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_WEDDING)
						|| eventCode.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_CERMONY))
				{
					//if(!(registryStatus.equalsIgnoreCase("inactive") || registryStatus.equalsIgnoreCase("hold") || registryStatus.equalsIgnoreCase("cancelled"))) {
					if(acceptableStatusesList.contains(registryStatus))
					{
						String eventDate = registryIdRepItems.get(index).getEventDate();
						final RegistrySummaryVO  registrySummaryVO=this.getRegistryInfo(registryIdRepItems.get(index).getRegistryId(), siteId);
						if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)){
							eventDate = BBBUtility.convertAppFormatDateToCAFormat(eventDate);
						}
						bridalRegistryVO.setEventDate(eventDate);
						bridalRegistryVO.setEventCode(eventCode);
						bridalRegistryVO.setBridalToolkitToken(registrySummaryVO.getBridalToolkitToken());
						bridalRegistryVO.setEventType( registryIdRepItems.get(index).getEventType());
						bridalRegistryVOList.add(bridalRegistryVO);
					}
				}

			}

		}
		return bridalRegistryVOList;
	}


	/**
	 * getting future baby registry list for ProfiledId.
	 *
	 * @param profileId
	 *            the profile id
	 * @param siteId
	 *            the site id
	 * @return registrySkinnyVOList
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public List<RegistrySkinnyVO> fetchUsersBabyRegistries(final Profile profileId,
			final String siteId) throws BBBBusinessException, BBBSystemException,
			RepositoryException {

		this.logDebug("GiftRegistryManager.fetchUsersBabyRegistries() method start");
		final List<RegistrySkinnyVO> registryList = this.getGiftRegistryTools()
				.getFutureRegistryList(profileId, siteId);
		final String acceptableStatuses = this.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY);
		final List<String> acceptableStatusesList = new ArrayList<String>();
		if(!BBBUtility.isEmpty(acceptableStatuses)) {
			final String[] statusesArray = acceptableStatuses.split(BBBCoreConstants.COMMA);
			acceptableStatusesList.addAll(Arrays.asList(statusesArray));
		}
		for (int index = registryList.size() - 1; index >= 0; --index) {

			final RegistrySkinnyVO registryVO = registryList
					.get(index);

			if (((registryVO.getEventType() != null)
					&& !registryVO.getEventCode().equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_BABY))
					|| !acceptableStatusesList.contains(registryVO.getStatus())) {
				registryList.remove(index);
			}
		}

		this.logDebug("GiftRegistryManager.fetchUsersBabyRegistries() method end");
		return registryList;
	}

	/**
	 * getting future wedding and baby registry list for ProfiledId.
	 *
	 * @param profileId
	 *            the profile id
	 * @param siteId
	 *            the site id
	 * @return registrySkinnyVOList
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public List<RegistrySkinnyVO> fetchUsersWeddingOrBabyRegistries(
			final Profile profileId, final String siteId) throws BBBBusinessException,
			BBBSystemException, RepositoryException {

		this.logDebug("GiftRegistryManager.fetchUsersWeddingRegistries() method start");

		final List<RegistrySkinnyVO> registryList = this.getGiftRegistryTools()
				.getFutureRegistryList(profileId, siteId);
		final String acceptableStatuses = this.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY);
		final List<String> acceptableStatusesList = new ArrayList<String>();
		if(!BBBUtility.isEmpty(acceptableStatuses)) {
			final String[] statusesArray = acceptableStatuses.split(BBBCoreConstants.COMMA);
			acceptableStatusesList.addAll(Arrays.asList(statusesArray));
		}

		for (int index = registryList.size() - 1; index >= 0; --index) {

			final RegistrySkinnyVO registryVO = registryList
					.get(index);

			if (((registryVO.getEventType() != null)
					&& !registryVO.getEventCode().equalsIgnoreCase(
							BBBGiftRegistryConstants.EVENT_TYPE_WEDDING)
					&& !registryVO.getEventCode().equalsIgnoreCase(
									BBBGiftRegistryConstants.EVENT_TYPE_BABY)
					&& !registryVO.getEventCode().equalsIgnoreCase(
									BBBGiftRegistryConstants.EVENT_TYPE_CERMONY))
					|| !acceptableStatusesList.contains(registryVO.getStatus())) {
				registryList.remove(index);
			}

		}

		this.logDebug("GiftRegistryManager.fetchUsersWeddingRegistries() method end");

		return registryList;
	}

	/**
	 * This Method will return the default site id based on Registry Type
	 * 
	 * @param siteId
	 * @return
	 */
	public String getDefaultRegistryTypeBySite(String siteId) {
		List<String> registryTypeList = null;
		String registryType = null;
			try {
				registryTypeList = getBbbCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, siteId);
			} catch (BBBSystemException e) {
				logError(e.getMessage());
			} catch (BBBBusinessException e) {
				logError(e.getMessage());
			}
			if (registryTypeList != null
					&& registryTypeList.size() > BBBCoreConstants.ZERO) {
				registryType = registryTypeList.get(BBBCoreConstants.ZERO);
			}
		return registryType;
	}
	
	/**
	 * adding item to the registry.
	 *
	 * @param pGiftRegistryViewBean
	 *            the gift registry view bean
	 * @return error value
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public ValidateAddItemsResVO addItemToGiftRegistry(
			final GiftRegistryViewBean pGiftRegistryViewBean)
			throws BBBBusinessException, BBBSystemException {
        
		 boolean regItemsWSCall = false;
		 ValidateAddItemsResVO addItemsResVO = null;
			int count = 0;
			this.logDebug("GiftRegistryManager.addItemToGiftRegistry() method start");

			for (int i = 0; i < pGiftRegistryViewBean.getAdditem().size(); i++) {
				final AddItemsBean addItemsBean = pGiftRegistryViewBean.getAdditem().get(
						i);
				pGiftRegistryViewBean.setProductId(addItemsBean.getProductId());
				pGiftRegistryViewBean.setSku(addItemsBean.getSku());
				if (StringUtils.isEmpty(addItemsBean.getRegistryId())) {
					addItemsBean.setRegistryId(pGiftRegistryViewBean
							.getRegistryId());
				}

				final String registryId = addItemsBean.getRegistryId();
				//Added for populating LTL ship method
				if(BBBUtility.isNotEmpty(addItemsBean.getLtlDeliveryServices())){
					pGiftRegistryViewBean.setItemTypes(BBBCoreConstants.LTL);
					pGiftRegistryViewBean.setLtlDeliveryServices(addItemsBean.getLtlDeliveryServices());
					pGiftRegistryViewBean.setLtlDeliveryPrices(addItemsBean.getLtlDeliveryPrices());
					if(addItemsBean.getLtlDeliveryServices().equalsIgnoreCase(BBBCoreConstants.LWA)){
						pGiftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.LW);
						pGiftRegistryViewBean.setAssemblySelections(BBBCoreConstants.YES_CHAR);
						pGiftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK+this.getCatalogTools().getAssemblyCharge(getSiteId(), pGiftRegistryViewBean.getSku()));
					}
				}
				pGiftRegistryViewBean.setRegistryId(registryId);
				pGiftRegistryViewBean.setQuantity(addItemsBean.getQuantity());

				final RepositoryItem profileItem = ServletUtil.getCurrentUserProfile();
				String profileID = null;
				String userEmailID = null;

				if(profileItem !=null){
					profileID = profileItem.getRepositoryId();
					userEmailID = (String)profileItem.getPropertyValue("email");
				}

				//validate registryID
				if(BBBUtility.isValidNumber(registryId)){
					
					  try
				        {
				           List<String> regItemsWSCallFlag = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall");
				         
				            if (!BBBUtility.isListEmpty(regItemsWSCallFlag))
				            {
				                  regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag.get(0));
				            }
				        }  catch (final BBBSystemException e) {
				   		    this.logError("Error getting Config key type RegItemsWSCall" + e);
					    } catch (final BBBBusinessException e) {
						    this.logError("Error getting Config key type RegItemsWSCall" + e);
					    }
				          
					      if (regItemsWSCall)
				            {
				                  // WS Call | calling the GiftRegistryTool's getRegistryInfo method
					    	  addItemsResVO = invokeServiceHandler(pGiftRegistryViewBean);
				            	if ((addItemsResVO.getServiceErrorVO() != null)
                                        && addItemsResVO.getServiceErrorVO().isErrorExists()) {

                                 if (!BBBUtility.isEmpty(addItemsResVO.getServiceErrorVO()
                                               .getErrorDisplayMessage()) ) {


                                        this.logError("CLS=[GiftRegistryTools]/MTHD=[addItemToGiftRegistry]/ Error =[" +
                                                      " RegistryID = " + pGiftRegistryViewBean.getRegistryId() +
                                                      " |ProfileID = " +profileID +
                                                      " |EmailID = " + userEmailID +
                                                      " |ErrorID = " +
                                                     + addItemsResVO.getServiceErrorVO().getErrorId() + " |ErrorMessage ="+addItemsResVO.getServiceErrorVO()
                                               .getErrorDisplayMessage());

                                 }
                                 return addItemsResVO;
                              }

				            }
				            // If Registry flag is false , information will fetch from the DB
				            else
				            {   
				            	addItemsResVO = new ValidateAddItemsResVO();
				            	List<ValidationError> lValErrors = new ArrayList<ValidationError>();
			                    ValidationError valError = new ValidationError();
			                    double minSkuValue = 8.0;
			                    double maxSkuValue = 8.0;
			                    String minSkuLength = getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.MIN_SKU_LENGTH);
			                    String maxSkuLength = getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.MAX_SKU_LENGTH);
			                    this.logDebug("minSkuLength" +minSkuLength + "maxSkuLength" + maxSkuLength);
			                    
			                    if(!BBBUtility.isEmpty(minSkuLength) && !BBBUtility.isEmpty(maxSkuLength)) {
			                    	 minSkuValue = Math.pow(10, Integer.valueOf(minSkuLength) - 1); //10000000
				                     maxSkuValue = Math.pow(10, Integer.valueOf(maxSkuLength)) - 1; //99999999
			                    }
			                    
				            	String sku =  pGiftRegistryViewBean.getSku();
				    			String quantity = pGiftRegistryViewBean.getQuantity();
				    			if (BBBUtility.isEmpty(pGiftRegistryViewBean.getItemTypes())) 
				    			{
				    				pGiftRegistryViewBean.setItemTypes("REG");
				    			}
				    			String itemTypes = pGiftRegistryViewBean.getItemTypes();
				    			String referenceIDs =  pGiftRegistryViewBean.getRefNum();
				    			String assemblySelections = pGiftRegistryViewBean.getAssemblySelections();
				    			String assemblyPrices = pGiftRegistryViewBean.getAssemblyPrices();
				    			String ltlDeliveryServices = pGiftRegistryViewBean.getLtlDeliveryServices();
				    			String ltlDeliveryPrices = pGiftRegistryViewBean.getLtlDeliveryPrices();
				    			String personlizationCodes = pGiftRegistryViewBean.getPersonlizationCodes();
				    			String personalizationPrices = pGiftRegistryViewBean.getPersonlizationCodes();
				    			String customizationPrices = pGiftRegistryViewBean.getCustomizationPrices();
				    			String personalizationDescrips = pGiftRegistryViewBean.getPersonalizationDescrips();
				    			String personalizedImageUrls = pGiftRegistryViewBean.getPersonalizedImageUrls();
				    			String personalizedImageUrlThumbs = pGiftRegistryViewBean.getPersonalizedImageUrlThumbs();
				    			String personalizedMobImageUrls = pGiftRegistryViewBean.getPersonalizedMobImageUrls();
				    			String personalizedMobImageUrlThumbs = pGiftRegistryViewBean.getPersonalizedMobImageUrlThumbs();
				    			
				    			String inputFields[]={registryId,sku,quantity,itemTypes,referenceIDs,assemblySelections,assemblyPrices,ltlDeliveryServices,
				    					ltlDeliveryPrices,personlizationCodes,personalizationPrices,customizationPrices,personalizationDescrips,personalizedImageUrls,
				    					personalizedImageUrlThumbs,personalizedMobImageUrls,personalizedMobImageUrlThumbs};
				    		try {
				    			/*validate inputs*/
				    			ErrorStatus errorStatus = (ErrorStatus) getGiftRegUtils().validateInput(inputFields);
				    			
				    			if(errorStatus.isErrorExists()) {
				    				
				    				addItemsResVO.getServiceErrorVO().setErrorExists(errorStatus.isErrorExists());
					    			addItemsResVO.getServiceErrorVO().setErrorMessage(errorStatus.getErrorMessage());
					    			addItemsResVO.getServiceErrorVO().setErrorId(errorStatus.getErrorId());
					    			
				    				this.logError("CLS=[GiftRegistryTools]/MTHD=[addItemToGiftRegistry]/ Error =[" +
											" RegistryID = " + pGiftRegistryViewBean.getRegistryId() +
											" |ProfileID = " +profileID +
											" |EmailID = " + userEmailID +
											" |ErrorID = " +
											+ addItemsResVO.getServiceErrorVO().getErrorId() + " |ErrorMessag ="+addItemsResVO.getServiceErrorVO()
											.getErrorDisplayMessage());
				   					return addItemsResVO;
				   					
				   				    } else if(Double.valueOf(sku) < minSkuValue) {
				   				    	
				                           valError.setKey(BBBGiftRegistryConstants.MIN_SKU_LENGTH + count);
				                           valError.setValue(BBBCatalogConstants.INVALID_SKU_ID);
				                           lValErrors.add(valError);
				                           
				                    } else if(Double.valueOf(sku) > maxSkuValue) {
				                    	
				                           valError.setKey(BBBGiftRegistryConstants.MAX_SKU_LENGTH + count);
				                           valError.setValue(BBBCatalogConstants.INVALID_SKU_ID);
				                           lValErrors.add(valError); 
				                           
				                    } else if(Integer.valueOf(quantity) > 9999) {
				                    	
				                    	valError.setKey(BBBGiftRegistryConstants.QUANTITY + count);
				                        valError.setValue(BBBGiftRegistryConstants.INVALID_QTY);
				                        lValErrors.add(valError);
				                           
				                    } 
				                    
				                    
				                    if(lValErrors.size() > 0 )
				                    {
				                    	   ServiceErrorVO serviceError = new ServiceErrorVO();
				                           serviceError.setErrorExists(true);
                                           serviceError.setErrorId(200);
				                           addItemsResVO.setServiceErrorVO(serviceError);
				                           this.logError("CLS=[GiftRegistryTools]/MTHD=[addItemToGiftRegistry]/ Error =[" +
													" RegistryID = " + pGiftRegistryViewBean.getRegistryId() +
													" |ProfileID = " +profileID +
													" |EmailID = " + userEmailID +
													" |ErrorID = " +
													+ addItemsResVO.getServiceErrorVO().getErrorId() + " |ErrorMessag ="+addItemsResVO.getServiceErrorVO()
													.getErrorDisplayMessage() + "|ValidationError = " + lValErrors.get(0).getKey() + lValErrors.get(0).getValue());
				                           return addItemsResVO;
				                           
				                    } else {
				                    	addItemsResVO =  this.getGiftRegistryTools().addItemToGiftRegistry(pGiftRegistryViewBean);
				                     }
				                 } catch(Exception ex)  {
				
	                                ServiceErrorVO serviceError = (ServiceErrorVO) getGiftRegUtils().logAndFormatError("AddItemsToGiftRegistry2",null , BBBGiftRegistryConstants.SERVICE_ERROR_VO , ex , registryId, sku, quantity, itemTypes, referenceIDs, assemblySelections, assemblyPrices, ltlDeliveryServices, ltlDeliveryPrices, personlizationCodes, personalizationPrices, customizationPrices, personalizationDescrips, personalizedImageUrls, personalizedImageUrlThumbs, personalizedMobImageUrls, personalizedMobImageUrlThumbs);
	                                addItemsResVO.setServiceErrorVO(serviceError);
	                             }
                             }
	    
	           }else{
		           this.logError("CLS=[GiftRegistryTools]/MTHD=[addItemToGiftRegistry]/ Error =[" +
				   " Invalid RegistryID = " + pGiftRegistryViewBean.getRegistryId() +
				   " |ProfileID = " +profileID +
				   " |EmailID = " + userEmailID );
	               }
		        if(addItemsResVO!=null && !addItemsResVO.getServiceErrorVO().isErrorExists()){
					String enableKatoriFlag = getBbbEximPricingManager().getKatoriAvailability();
					if(pGiftRegistryViewBean.getRefNum() !=null && !pGiftRegistryViewBean.getRefNum().isEmpty() && "true".equalsIgnoreCase(enableKatoriFlag)){
						String response = null;
						try {				
							response = getBbbEximPricingManager().invokeLockAPI(pGiftRegistryViewBean.getProductId(), pGiftRegistryViewBean.getRefNum());
						} catch (Exception e) {
							logError("Error in invoking Lock API" ,e );
						}
						if(response!=null && !response.equalsIgnoreCase("success")){
							logError("Error in calling Lock API after adding item to registry");
						}					
					}
					count+=Integer.parseInt(addItemsBean.getQuantity());
					this.getGiftRegistryTools().removePersonalizedSkuFromSession(pGiftRegistryViewBean.getAdditem());
				}
		
			
          } 
			if(addItemsResVO!=null){
				addItemsResVO.setCount(count);
			}
			this.logDebug("GiftRegistryManager.addItemToGiftRegistry() method ends");
			return addItemsResVO;
      }

	protected ValidateAddItemsResVO invokeServiceHandler(final GiftRegistryViewBean pGiftRegistryViewBean)
			throws BBBBusinessException, BBBSystemException {
		return (ValidateAddItemsResVO) ServiceHandlerUtil
					.invoke(pGiftRegistryViewBean);
	}
			

	/**
	 * adding more items to the registry with single web service call.
	 *
	 * @param pGiftRegistryViewBean
	 *            the gift registry view bean
	 * @return error value
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public ValidateAddItemsResVO addBulkItemsToGiftRegistry(
			final GiftRegistryViewBean pGiftRegistryViewBean,BBBSessionBean pSessionBean)
			throws BBBBusinessException, BBBSystemException {
		BBBPerformanceMonitor.start( GiftRegistryManager.class.getName() + " : " + "addBulkItemsToGiftRegistry");
		this.logDebug("GiftRegistryManager.addBulkItemsToGiftRegistry() method starts");

		ValidateAddItemsResVO addItemsResVO = null;
		StringBuilder skuBuilder = new StringBuilder();
		StringBuilder productBuilder = new StringBuilder();
		StringBuilder qtyBuilder = new StringBuilder();
		String registryId =pGiftRegistryViewBean.getRegistryId();
		int totalQty = 0;
		for (int i = 0; i < pGiftRegistryViewBean.getAdditem().size(); i++) {
			 AddItemsBean addItemsBean = pGiftRegistryViewBean.getAdditem().get(i);
			// registryId = addItemsBean.getRegistryId();
			productBuilder.append(addItemsBean.getProductId());
			skuBuilder.append(addItemsBean.getSku());
			qtyBuilder.append(addItemsBean.getQuantity());
			if(!(i==pGiftRegistryViewBean.getAdditem().size()-1)){
				productBuilder.append(",");
				skuBuilder.append(",");
				qtyBuilder.append(",");
			}
			totalQty = totalQty + Integer.parseInt(addItemsBean.getQuantity());
		}

		logDebug("Print product ids...."+productBuilder.toString());
		logDebug("Print sku ids...."+skuBuilder.toString());
		logDebug("Print qty ids...."+qtyBuilder.toString());	

		//pGiftRegistryViewBean.setRegistryId(registryId);

		pGiftRegistryViewBean.setProductId(productBuilder.toString());
		pGiftRegistryViewBean.setSku(skuBuilder.toString());
		pGiftRegistryViewBean.setQuantity(qtyBuilder.toString());
		final RepositoryItem profileItem = ServletUtil.getCurrentUserProfile();
		String profileID = null;
		String userEmailID = null;
		boolean regItemsWSCall = false;

		if(profileItem !=null){
			profileID = profileItem.getRepositoryId();
			userEmailID = (String)profileItem.getPropertyValue("email");
		}

		//validate registryID
		if(BBBUtility.isValidNumber(registryId)){
			 try
		        {
		           List<String> regItemsWSCallFlag = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall");
		         
		            if (!BBBUtility.isListEmpty(regItemsWSCallFlag))
		            {
		                  regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag.get(0));
		            }
		        }  catch (final BBBSystemException e) {
		   		    this.logError("Error getting Config key type RegItemsWSCall" + e);
			    } catch (final BBBBusinessException e) {
				    this.logError("Error getting Config key type RegItemsWSCall" + e);
			    }
		          
			 if (regItemsWSCall)
	            {
	                  // WS Call | calling the GiftRegistryTool's getRegistryInfo method
				 addItemsResVO = invokeServiceHandler(pGiftRegistryViewBean);
	            	if ((addItemsResVO.getServiceErrorVO() != null)
                         && addItemsResVO.getServiceErrorVO().isErrorExists()) {

                  if (!BBBUtility.isEmpty(addItemsResVO.getServiceErrorVO()
                                .getErrorDisplayMessage()) ) {


                         this.logError("CLS=[GiftRegistryTools]/MTHD=[addItemToGiftRegistry]/ Error =[" +
                                       " RegistryID = " + pGiftRegistryViewBean.getRegistryId() +
                                       " |ProfileID = " +profileID +
                                       " |EmailID = " + userEmailID +
                                       " |ErrorID = " +
                                      + addItemsResVO.getServiceErrorVO().getErrorId() + " |ErrorMessage ="+addItemsResVO.getServiceErrorVO()
                                .getErrorDisplayMessage());

                  }
                  return addItemsResVO;
               }
	         } else {
	        	 addItemsResVO = new ValidateAddItemsResVO();
	        	 String inputFields[]={registryId,pGiftRegistryViewBean.getSku(),pGiftRegistryViewBean.getQuantity()};
	        	 try {
		    	
	    			/*validate inputs*/
	    			ErrorStatus errorStatus = (ErrorStatus) getGiftRegUtils().validateInput(inputFields);
	    			
	    			if(errorStatus.isErrorExists()) {
	    				
	    				addItemsResVO.getServiceErrorVO().setErrorExists(errorStatus.isErrorExists());
		    			addItemsResVO.getServiceErrorVO().setErrorMessage(errorStatus.getErrorMessage());
		    			addItemsResVO.getServiceErrorVO().setErrorId(errorStatus.getErrorId());
		    			
	    				this.logError("CLS=[GiftRegistryTools]/MTHD=[addItemToGiftRegistry]/ Error =[" +
								" RegistryID = " + pGiftRegistryViewBean.getRegistryId() +
								" |ProfileID = " +profileID +
								" |EmailID = " + userEmailID +
								" |ErrorID = " +
								+ addItemsResVO.getServiceErrorVO().getErrorId() + " |ErrorMessag ="+addItemsResVO.getServiceErrorVO()
								.getErrorDisplayMessage());
	   					return addItemsResVO;
	   					
	   				    } 
	    			List<ValidationError> lValErrors = new ArrayList<ValidationError>();
	    			double minSkuValue = 8.0;
                    double maxSkuValue = 8.0;
                    String minSkuLength = getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.MIN_SKU_LENGTH);
                    String maxSkuLength = getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.MAX_SKU_LENGTH);
                    this.logDebug("minSkuLength" +minSkuLength + "maxSkuLength" + maxSkuLength);
                    
                    if(!BBBUtility.isEmpty(minSkuLength) && !BBBUtility.isEmpty(maxSkuLength)) {
                    	 minSkuValue = Math.pow(10, Integer.valueOf(minSkuLength) - 1); //10000000
	                     maxSkuValue = Math.pow(10, Integer.valueOf(maxSkuLength)) - 1; //99999999
                    }
	    			for (int i = 0; i < pGiftRegistryViewBean.getAdditem().size(); i++) {
	    				AddItemsBean addItemsBean = pGiftRegistryViewBean.getAdditem().get(i);
	    				pGiftRegistryViewBean.setProductId(addItemsBean.getProductId());
	    				pGiftRegistryViewBean.setSku(addItemsBean.getSku());
	    				pGiftRegistryViewBean.setQuantity(addItemsBean.getQuantity());
	    				if (BBBUtility.isEmpty(pGiftRegistryViewBean.getItemTypes())) 
		    			{
		    				pGiftRegistryViewBean.setItemTypes("REG");
		    			}
	    				
	    				//addItemsResVO = new ValidateAddItemsResVO();
		            	ValidationError valError = new ValidationError();
		            	 if(Double.valueOf(addItemsBean.getSku()) < minSkuValue) {
	   				    	
	                           valError.setKey(BBBGiftRegistryConstants.MIN_SKU_LENGTH);
	                           valError.setValue(BBBCatalogConstants.INVALID_SKU_ID);
	                           lValErrors.add(valError);
	                           
	                    } else if(Double.valueOf(addItemsBean.getSku()) > maxSkuValue) {
	                    	
	                           valError.setKey(BBBGiftRegistryConstants.MAX_SKU_LENGTH);
	                           valError.setValue(BBBCatalogConstants.INVALID_SKU_ID);
	                           lValErrors.add(valError); 
	                           
	                    } else if(Integer.valueOf(addItemsBean.getQuantity()) > 9999) {
	                    	
	                    	valError.setKey(BBBGiftRegistryConstants.QUANTITY);
	                        valError.setValue(BBBGiftRegistryConstants.INVALID_QTY);
	                        lValErrors.add(valError);
	                           
	                    } 
	                     if(lValErrors.size() > 0 )
	                    {
	                    	   ServiceErrorVO serviceError = new ServiceErrorVO();
	                           serviceError.setErrorExists(true);
                               serviceError.setErrorId(200);
	                           addItemsResVO.setServiceErrorVO(serviceError);
	                           this.logError("CLS=[GiftRegistryTools]/MTHD=[addItemToGiftRegistry]/ Error =[" +
										" RegistryID = " + pGiftRegistryViewBean.getRegistryId() +
										" |ProfileID = " +profileID +
										" |EmailID = " + userEmailID +
										" |ErrorID = " +
										+ addItemsResVO.getServiceErrorVO().getErrorId() + " |ErrorMessag ="+addItemsResVO.getServiceErrorVO()
										.getErrorDisplayMessage() + "|ValidationError = " + lValErrors.get(0).getKey() + lValErrors.get(0).getValue());
	                           return addItemsResVO;
	                           
	                    } else {
	                    	addItemsResVO = this.getGiftRegistryTools().addItemToGiftRegistry(pGiftRegistryViewBean);
	                     }
	                    
		            	
	    			}
	         } catch(Exception ex)  {
			
            ServiceErrorVO serviceError = (ServiceErrorVO) getGiftRegUtils().logAndFormatError("AddItemsToGiftRegistry2", null , BBBGiftRegistryConstants.SERVICE_ERROR_VO , ex , registryId, pGiftRegistryViewBean.getSku(), pGiftRegistryViewBean.getQuantity(),pGiftRegistryViewBean.getItemTypes());
            addItemsResVO.setServiceErrorVO(serviceError);
         }
		}
		}else{
			this.logError("CLS=[GiftRegistryTools]/MTHD=[addItemToGiftRegistry]/ Error =[" +
					" Invalid RegistryID = " + pGiftRegistryViewBean.getRegistryId() +
					" |ProfileID = " +profileID +
					" |EmailID = " + userEmailID );
		}
		if (addItemsResVO!=null && (addItemsResVO.getServiceErrorVO() != null)
                && !addItemsResVO.getServiceErrorVO().isErrorExists()) {
				pGiftRegistryViewBean.setTotQuantity(totalQty);
				pGiftRegistryViewBean.setRegistryName(pSessionBean.getEventType());
		}
			   
		if(addItemsResVO!=null){
			addItemsResVO.setCount(totalQty);
		}
		skuBuilder = null;productBuilder=null;qtyBuilder=null;	
		this.logDebug("GiftRegistryTools.addAllItemsToGiftRegistry() method ends");	

		BBBPerformanceMonitor.end( GiftRegistryTools.class.getName() + " : " + "addBulkItemsToGiftRegistry");

		return addItemsResVO;
		
	}

	/**
	 * importing registry to the profile.
	 *
	 * @param profile
	 *            the profile
	 * @param pRegistryVO
	 *            the registry vo
	 * @return isErrorExist
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public RegistryResVO importRegistry(final Profile profile, final RegistryVO pRegistryVO)
			throws BBBBusinessException, BBBSystemException {
		return this.getGiftRegistryTools().importRegistry(profile, pRegistryVO);
	}

	/**
	 * Forgot Registry password.
	 *
	 * @param forgotRegPassRequestVO
	 *            the forgot reg pass request vo
	 * @return the registry res vo
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public RegistryResVO forgetRegPasswordService(
			final ForgetRegPassRequestVO forgotRegPassRequestVO)
			throws BBBBusinessException, BBBSystemException {
		return this.getGiftRegistryTools()
				.forgotRegPasswordService(forgotRegPassRequestVO);
	}

	/**
	 * creating registry.
	 *
	 * @param registryVO
	 *            the registry vo
	 * @return validateRegistryResVO
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */

	public RegistryResVO createRegistry(final RegistryVO registryVO)
			throws BBBBusinessException, BBBSystemException {
		
		RegistryResVO createRegistryResVO = null;
		boolean regItemsWSCall = false;

		List<String> regItemsWSCallFlag = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, 
				BBBGiftRegistryConstants.REGISTRY_WS_CALL);
		if (!BBBUtility.isListEmpty(regItemsWSCallFlag)) {
			regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag.get(BBBCoreConstants.ZERO));
		}
		if (regItemsWSCall) {
			if (isLoggingDebug()) {
				this.logDebug("GiftRegistryManager.createRegistry() method in web-service mode[regItemsWSCall]: " + regItemsWSCall);
			}
			createRegistryResVO = this.getGiftRegistryTools().createRegistry(registryVO);
		} else {
			if (isLoggingDebug()) {
				this.logDebug("GiftRegistryManager.createRegistry() method in PROCEDURE mode[regItemsWSCall]: " + regItemsWSCall);
			}
			try {
				/***
				 * Step 1: Do initial validation for all the input variables for
				 * invalid characters
				 */
				ErrorStatus errorStatus = validateInputForInvalidCharectars(registryVO);
				
				if (errorStatus.isErrorExists()) {
					createRegistryResVO = new RegistryResVO();
					ServiceErrorVO errorVO = new ServiceErrorVO();
					errorVO.setErrorExists(true);
					errorVO.setErrorId(errorStatus.getErrorId());
					createRegistryResVO.setServiceErrorVO(errorVO);
				} else {
					
					/***
					 * Validate all the input values and post successful
					 * validation invoke tools method to call stored procedures
					 * Step 2: populate RegistryHeaderVO
					 */
					RegistryHeaderVO registryHeaderVO =  populateRegHeaderVO(registryVO);
					/**
					 * Step 3: populate RegistryBabyVO
					 * */
					RegistryBabyVO registryBabyVO = populateRegBabyVO(registryVO);
					/***
					 * Step 4: populate RegNamesVO for PrimaryRegistrant
					 */
					List<RegNamesVO>registrantsVOs = new ArrayList<RegNamesVO>();
					populateRegNamesVO(registryVO, registryVO.getPrimaryRegistrant(), BBBGiftRegistryConstants.REG_SUB_TYPE, registrantsVOs);
					/***
					 * Step 5: populate RegNamesVO for Co-Registrant, if any input value provided for co-registrant
					 ***/
					if (!getGiftRegUtils().isCoRegistrantEmpty(registryVO)) {
						populateRegNamesVO(registryVO, registryVO.getCoRegistrant(), BBBGiftRegistryConstants.COREG_SUB_TYPE, registrantsVOs);
					}
					/***
					 * Step 6: populate RegNamesVO for Shipping Address
					 */
					List<RegNamesVO> shippingVOs = new ArrayList<RegNamesVO>();
					if (registryVO.getShipping() != null && registryVO.getShipping().getShippingAddress() != null) {
						populateShippingVO(registryVO, registryVO.getShipping().getShippingAddress(), BBBGiftRegistryConstants.SH, shippingVOs);
					}
					/**
					 * Step 7: populate RegNamesVO for  Future Shipping Address, if availed.
					 */
					if (registryVO.getShipping() != null &&  registryVO.getShipping().getFutureshippingAddress() != null
							&& !getGiftRegUtils().isFutureShippingEmpty(registryVO.getShipping())) {
						populateShippingVO(registryVO, registryVO.getShipping().getFutureshippingAddress(), BBBGiftRegistryConstants.FU, shippingVOs);
					}
					/***
					 * Step 8: populate RegistryPrefStoreVO
					 */
					RegistryPrefStoreVO registryPrefStoreVO = populateRegPrefStoreVO(registryVO);
					
					/**
					 * Step 9: validate all registry fields & prefStore inputs
					 */
					boolean validationResult = this.validateInputForCreateOrUpdateRegistry(registryVO, registryHeaderVO, 
							registrantsVOs, shippingVOs, registryBabyVO, registryPrefStoreVO );
					if (validationResult) {
						/***
						 * Step 10: invoke GiftRegistryTools.createRegistry() to execute procedures
						 * to create the registry
						 */
						createRegistryResVO = this.getGiftRegistryTools().createRegistry(registryVO, registryHeaderVO, 
								registrantsVOs, shippingVOs, registryBabyVO, registryPrefStoreVO );
					} else {
						createRegistryResVO = new RegistryResVO();
						ServiceErrorVO errorVO = new ServiceErrorVO();
						errorVO.setErrorExists(true);
						errorVO.setErrorId(BBBGiftRegistryConstants.VALIDATION);
						createRegistryResVO.setServiceErrorVO(errorVO);
					}
				}
			} catch (Exception exception) {
				this.logError("Some Exception occured while preparing and validating the inputs", exception);
				Object[] args = getGiftRegUtils().populateInputToLogErrorOrValidate(registryVO);
				ServiceErrorVO serviceErrorVO = (ServiceErrorVO) getGiftRegUtils().logAndFormatError("CreateRegistry2", null, BBBGiftRegistryConstants.SERVICE_ERROR_VO, exception, args);
				createRegistryResVO = new RegistryResVO();
				createRegistryResVO.setServiceErrorVO(serviceErrorVO);
			}
		}
		
		return createRegistryResVO;
	}
	
	/**
	 * Search registry for user
	 * @param registrySearchVO
	 * @return  List<RegistrySummaryVO>
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public RegSearchResVO searchRegistries(
			final RegistrySearchVO registrySearchVO, final String siteId)
			throws BBBSystemException, BBBBusinessException {
		this.logDebug("GiftRegistryTools.searchRegistries() method start");
		RegSearchResVO regSearchResVO = null;
		boolean regItemsWSCall = false;
		List<String> regItemsWSCallFlag = this.getCatalogTools()
				.getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
						"RegItemsWSCall");

		if (regItemsWSCallFlag != null && !regItemsWSCallFlag.isEmpty()) {
			regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag.get(0));
		}

		// invoking regSearch web service
		if (regItemsWSCall) {
			regSearchResVO = this.getGiftRegistryTools().searchRegistries(
					registrySearchVO, siteId);
		}

		else if ("regSearch"
				.equals(registrySearchVO.getServiceName())) {
			regSearchResVO = searchRegistryWithFilter(registrySearchVO);
		}

		else if ("getRegistryInfoByProfileId".equals(registrySearchVO
				.getServiceName())) {
			regSearchResVO = searchRegistriesbyProfileId(registrySearchVO);
		}

		if (regSearchResVO != null && regSearchResVO.getServiceErrorVO() != null && !regSearchResVO.getServiceErrorVO().isErrorExists()) {

			regSearchResVO = getGiftRegistryTools().changedRegistryTypeName(
					regSearchResVO, siteId);
		}

		List<String> pListUserRegIds = new ArrayList<String>();
		if (regSearchResVO != null
				&& !regSearchResVO.getServiceErrorVO().isErrorExists()) {

			final List<RegistrySummaryVO> regSummaryVO = regSearchResVO
					.getListRegistrySummaryVO();
			
			regSearchResVO.setListRegistrySummaryVO(regSummaryVO);
		}
		this.logDebug("GiftRegistryTools.searchRegistries() method ends.");
		return regSearchResVO;
	}

	/**This method searches registry based on first name and last name with or without state,
	 * by registry id, by email id for gift giver and non gift giver.
	 * Also it sorts the result based on criterion set in request
	 * @param registrySearchVO
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private RegSearchResVO searchRegistryWithFilter(RegistrySearchVO registrySearchVO) throws BBBSystemException, BBBBusinessException
	{
		this.logDebug("GiftRegistryTools.searchRegistryWithFilter() method start");
		RegSearchResVO regSearchResVO = null;
		try
		{
			//validate inputs
			regSearchResVO = validRegistrySearchVO(registrySearchVO);

			if (regSearchResVO.getServiceErrorVO().isErrorExists()) {
				return regSearchResVO;
			} else {
				
				// flow for Gift giver
				if (registrySearchVO.getGiftGiver()) {
					if (!BBBUtility.isEmpty(registrySearchVO.getRegistryId())) {
						// search by registry number
						if (getGiftRegUtils().isNumeric(
								registrySearchVO.getRegistryId())) {
							regSearchResVO = getGiftRegistryTools().getRegistryListByRegNum(
									registrySearchVO);
						} else {
							regSearchResVO
									.setServiceErrorVO((ServiceErrorVO) getGiftRegUtils()
											.setErrorInResponse(
													BBBGiftRegistryConstants.SERVICE_ERROR_VO,
													"Invalid registry number",
													"Invalid registry number",
													true, 200, null));
						}
					}
					// search by email
					else if (!BBBUtility.isEmpty(registrySearchVO.getEmail())) {
						regSearchResVO = getGiftRegistryTools().getRegistryListByEmail(
								registrySearchVO);
					}
					// search by first name , last name /email
					else {
						regSearchResVO = getGiftRegistryTools().getRegistryListByName(
								registrySearchVO);
					}
				}

				// For logged in user
				else
				{
					if(!registrySearchVO.getGiftGiver() && BBBUtility.isEmpty(registrySearchVO.getProfileId().getRepositoryId()))
					{
						regSearchResVO.setServiceErrorVO((ServiceErrorVO) getGiftRegUtils().setErrorInResponse
								(BBBGiftRegistryConstants.SERVICE_ERROR_VO, "Profile Id is required when isGiftGiver = false", "Profile Id is required for registrant search", true, 200, null));
					}
					//search by registry id
					if(!BBBUtility.isEmpty(registrySearchVO.getRegistryId()))
					{
						if(getGiftRegUtils().isNumeric(registrySearchVO.getRegistryId()))
						{
							regSearchResVO = getGiftRegistryTools().regSearchByRegUsingRegNum(registrySearchVO);
						}
						else
						{
							regSearchResVO.setServiceErrorVO((ServiceErrorVO) getGiftRegUtils().setErrorInResponse
									(BBBGiftRegistryConstants.SERVICE_ERROR_VO, "Invalid registry number", "Invalid registry number", true, 200, null));
						}
					}
					//search by email
					else if(!BBBUtility.isEmpty(registrySearchVO.getEmail()))
					{
						regSearchResVO = getGiftRegistryTools().regSearchByRegUsingEmail(registrySearchVO);
					}
					//search by name/state
					else 
					{
						regSearchResVO = getGiftRegistryTools().regSearchByRegUsingName(registrySearchVO);
					}
				}
			}
		} 
		catch (Exception ex) 
 {
			if (ex.getMessage()!=null && ex.getMessage().contains("Registry not found")) {
				logInfo("GiftRegistryTools.searchRegistryWithFilter() :: Registry not found");
			
			} else {

				this.logError(
						"Error occurred while executing searchRegistryWithFilter",
						ex);
			}
				// logging exception in db through stored procedure
				regSearchResVO = new RegSearchResVO();
			ServiceErrorVO serviceErrorVO = (ServiceErrorVO) getGiftRegUtils().logAndFormatError(registrySearchVO.getServiceName() ,null, BBBGiftRegistryConstants.SERVICE_ERROR_VO , ex ,
					registrySearchVO.getFirstName() ,registrySearchVO.getSiteId() , registrySearchVO.getLastName(),registrySearchVO.getStartIdx(),
					registrySearchVO.getBlkSize(),registrySearchVO.getGiftGiver(),
					getGiftRegistryTools().getFilterOptions(registrySearchVO.getEvent(), registrySearchVO.getState()),registrySearchVO.getSortSeq(),
					registrySearchVO.isReturnLeagacyRegistries(),registrySearchVO.getExcludedRegNums(),registrySearchVO.getEmail());
				regSearchResVO.setServiceErrorVO(serviceErrorVO);
		}
		this.logDebug("GiftRegistryTools.searchRegistryWithFilter() method ends.");
		return regSearchResVO;

	}

	/**This method takes inputs from request and perform validation.
	 * Error message is set in response if any of the validation fails.
	 * @param registrySearchVO
	 * @return RegSearchResVO 
	 * @throws Exception
	 */
	public RegSearchResVO validRegistrySearchVO(RegistrySearchVO registrySearchVO) throws Exception
	{
		RegSearchResVO regSearchResVO = new RegSearchResVO();
		if(BBBUtility.isEmpty(registrySearchVO.getRegistryId()))
		{
			if(BBBUtility.isEmpty(registrySearchVO.getEmail()))
			{
				if(!BBBUtility.isEmpty(registrySearchVO.getFirstName()) && 
						registrySearchVO.getFirstName().length() < 1 && registrySearchVO.getLastName().length() < 2)
				{
					regSearchResVO.setServiceErrorVO((ServiceErrorVO)getGiftRegUtils().setErrorInResponse(BBBGiftRegistryConstants.SERVICE_ERROR_VO,
							BBBGiftRegistryConstants.ERROR_FIRST_LAST_NAME, BBBGiftRegistryConstants.ERROR_FIRST_LAST_NAME, true, 200, null));
					return regSearchResVO;
				}
			}
			else
			{
				if(!getGiftRegUtils().isValidEmail(registrySearchVO.getEmail()))
				{
					regSearchResVO.setServiceErrorVO((ServiceErrorVO)getGiftRegUtils().setErrorInResponse(BBBGiftRegistryConstants.SERVICE_ERROR_VO,
							BBBGiftRegistryConstants.ERROR_EMAIL_INVALID_CHARS, BBBGiftRegistryConstants.ERROR_EMAIL_INVALID_CHARS, true, 200, null));
					return regSearchResVO;
				}
			}

		}
		boolean isValidFilter = true;

		if(!BBBUtility.isEmpty(registrySearchVO.getState()) && 
				!getGiftRegUtils().isValidState(registrySearchVO.getState(), registrySearchVO.getSiteId()))
		{
			isValidFilter = false;

		}
		if(!BBBUtility.isEmpty(registrySearchVO.getEvent()))
			if(getGiftRegUtils().hasInvalidChars(registrySearchVO.getEvent()))
			{
				isValidFilter = false;
			}
			else if(getGiftRegUtils().hasInvalidCharsForHackOnly(registrySearchVO.getEvent()))
			{
				isValidFilter = false;
			}

		if(!isValidFilter)
		{
			regSearchResVO.setServiceErrorVO((ServiceErrorVO)getGiftRegUtils().setErrorInResponse(BBBGiftRegistryConstants.SERVICE_ERROR_VO,
					BBBGiftRegistryConstants.ERROR_INVALID_FILTERS, BBBGiftRegistryConstants.ERROR_INVALID_FILTERS, true, 200, null));
			return regSearchResVO;
		}

		if(registrySearchVO.getGiftGiver())
		{
			if(!BBBUtility.isEmpty(registrySearchVO.getRegistryId()))
			{
				if(!getGiftRegUtils().isNumeric(registrySearchVO.getRegistryId()))
				{
					regSearchResVO.setServiceErrorVO((ServiceErrorVO)getGiftRegUtils().setErrorInResponse(BBBGiftRegistryConstants.SERVICE_ERROR_VO,
							BBBGiftRegistryConstants.ERROR_INVALID_REGISTRY, BBBGiftRegistryConstants.ERROR_INVALID_REGISTRY, true, 200, null));            			 return regSearchResVO;
				}
			}
		}

		return regSearchResVO;
	}
    
	/**This method searches registry based on profileId and siteId
	 * @param registrySearchVO
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private RegSearchResVO searchRegistriesbyProfileId(RegistrySearchVO registrySearchVO) throws BBBSystemException, BBBBusinessException
	{
		RegSearchResVO regSearchResVO = null;
		this.logDebug("GiftRegistryTools.searchRegistriesbyProfileId() method start");
		try
		{
			//validate inputs
			//validate Profile Id
	        regSearchResVO = new RegSearchResVO();
			ServiceErrorVO serviceError = null;
	    	Profile profile = registrySearchVO.getProfileId();
	    	String[] inputFields = {profile.getRepositoryId()};
	    			
	    	ErrorStatus errorStatus = getGiftRegUtils().validateInput(inputFields);
	    	if(errorStatus.isErrorExists()) {
	    		
	    	regSearchResVO.getServiceErrorVO().setErrorExists(errorStatus.isErrorExists());
	    	regSearchResVO.getServiceErrorVO().setErrorMessage(errorStatus.getErrorMessage());
	    	regSearchResVO.getServiceErrorVO().setErrorId(errorStatus.getErrorId());
	    	return regSearchResVO;
	    	
	    	} 
	    	if(BBBUtility.isEmpty(profile.toString())) {
	    		   serviceError = new ServiceErrorVO();
	    		   serviceError.setErrorExists(true);
	    		   serviceError.setErrorMessage(BBBGiftRegistryConstants.ERROR_INVALID_PROFILE_ID);
	    		   serviceError.setErrorDisplayMessage(BBBGiftRegistryConstants.ERROR_INVALID_PROFILE_ID);
	    		   serviceError.setErrorId(200);
	    		   regSearchResVO.setServiceErrorVO(serviceError);
                   return regSearchResVO;
            } else {
            	regSearchResVO = getGiftRegistryTools().searchRegistriesByProfileId(registrySearchVO);
            }
	    	
		} 
		catch (Exception ex) 
		{
			this.logError("Error occurred while executing searchRegistryWithFilter", ex);
			
			//logging exception in db through stored procedure
			regSearchResVO = new RegSearchResVO();
			ServiceErrorVO serviceErrorVO = (ServiceErrorVO) getGiftRegUtils().logAndFormatError(registrySearchVO.getServiceName() ,null, BBBGiftRegistryConstants.SERVICE_ERROR_VO , ex ,
					registrySearchVO.getFirstName() ,registrySearchVO.getSiteId() , registrySearchVO.getLastName(),registrySearchVO.getStartIdx(),
					registrySearchVO.getBlkSize(),registrySearchVO.getGiftGiver(),
					getGiftRegistryTools().getFilterOptions(registrySearchVO.getEvent(), registrySearchVO.getState()),registrySearchVO.getSortSeq(),
					registrySearchVO.isReturnLeagacyRegistries(),registrySearchVO.getExcludedRegNums(),registrySearchVO.getEmail());
			regSearchResVO.setServiceErrorVO(serviceErrorVO);
		}
		this.logDebug("GiftRegistryTools.searchRegistriesbyProfileId() method ends.");
		return regSearchResVO;

	}
	/**
	 * Set Announcement Card counts for a user's registry.
	 *
	 * @param registryVO
	 *            the registry vo
	 * @return SetAnnouncementCardResVO
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public SetAnnouncementCardResVO assignAnnouncementCardCount(
			final RegistryVO registryVO) throws BBBSystemException,
			BBBBusinessException {

		final SetAnnouncementCardResVO setAnnouncementCardResVO = this.getGiftRegistryTools()
				.assignAnnouncementCardCount(registryVO);

		return setAnnouncementCardResVO;
	}

	/**
	 * This method is invoked to get the registry info against a registryId.
	 *
	 * @param regReqVO
	 *            the reg req vo
	 * @return RegistryResVO
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public RegistryResVO getRegistryInfo(final RegistryReqVO regReqVO)
			throws BBBSystemException, BBBBusinessException {
		this.logDebug(" GiftRegistryManager getRegistryInfo - START");
		RegistryResVO registryResVO = null;
		
		registryResVO = getRegistryInfoFromWSorDB(regReqVO);
		
		final String siteId = regReqVO.getSiteId();
		final String registryId= regReqVO.getRegistryId();
		if ((registryResVO != null) &&  (registryResVO.getRegistrySummaryVO() != null) && (registryResVO.getRegistrySummaryVO().getEventDate() != null)) {
			if(siteId.equalsIgnoreCase(BBBGiftRegistryConstants.CANADA_SITE_ID)){
				registryResVO.getRegistrySummaryVO().setEventDate(BBBUtility.convertWSDateToCAFormat(registryResVO.getRegistrySummaryVO().getEventDate()));
				if(registryResVO.getRegistrySummaryVO().getFutureShippingDate() != null){
					registryResVO.getRegistrySummaryVO().setFutureShippingDate(BBBUtility.convertAppFormatDateToCAFormat(registryResVO.getRegistrySummaryVO().getFutureShippingDate()));
				}
			}else{
				registryResVO.getRegistrySummaryVO().setEventDate(BBBUtility.convertWSDateToUSFormat(registryResVO.getRegistrySummaryVO().getEventDate()));
			}


		}
		this.logDebug(" GiftRegistryManager getRegistryInfo - END");

		return registryResVO;
	}

	/**
	 * This method is invoked to fetch registry items against registryId.
	 *
	 * @param pRegistrySearchVO
	 *            the registry search vo
	 * @return RegistryItemsListVO
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public RegistryItemsListVO fetchRegistryItems(
			final RegistrySearchVO pRegistrySearchVO) throws BBBSystemException,
			BBBBusinessException {
		// Call GiftRegistryTools API fetchRegistryItems(registrySearchVO)
		RegistryItemsListVO registryItemsListVO = null;
		boolean regItemsWSCall = false;
		List<String> regItemsWSCallFlag = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall");
		if (!BBBUtility.isListEmpty(regItemsWSCallFlag))
		{
			regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag.get(0));
		}
		if (regItemsWSCall)
		{
			// WS Call | calling the GiftRegistryTool's getRegistryInfo method
			 registryItemsListVO = this.getGiftRegistryTools().fetchRegistryItems(pRegistrySearchVO);
			this.logDebug("getGiftRegistryTools().fetchRegistryItems -MSG= registryItemsListVO  from web service :" + registryItemsListVO);
		}
		else
		{
			// DB Call | calling the GiftRegistryTool's getRegistryInfoFromEcomAdmin method
			registryItemsListVO = this.getGiftRegistryTools().fetchRegistryItemsFromEcomAdmin(pRegistrySearchVO.getRegistryId(), String.valueOf(pRegistrySearchVO.getView()));
			this.logDebug("getGiftRegistryTools().fetchRegistryItemsFromEcomAdmin MSG= registryItemsListVO from repository :" + registryItemsListVO);
		}
		
		return registryItemsListVO;
	}
	
	public RegistryItemsListVO fetchRegistryItemsFromRepo(String registryId, String view) throws BBBSystemException,
			BBBBusinessException {

		// Call GiftRegistryTools API fetchRegistryItems(registrySearchVO)
		final RegistryItemsListVO registryItemsListVO = this.getGiftRegistryTools()
				.fetchRegistryItemsFromRepo(registryId, view);

		return registryItemsListVO;
	}

	public RegistryItemsListVO fetchRegistryItemsFromEcomAdmin(String registryId, String view) throws BBBSystemException,
	BBBBusinessException {

		// Call GiftRegistryTools API fetchRegistryItemsFromEcomAdmin(registryId)
		final RegistryItemsListVO registryItemsListVO = this.getGiftRegistryTools()
				.fetchRegistryItemsFromEcomAdmin(registryId, view);
		
		return registryItemsListVO;
	}
	
	
	public RegistryItemsListVO fetchRegistryItemsFromEcomAdmin(String registryId, boolean isGiftGiver, String regView) throws BBBSystemException,
			BBBBusinessException
	{
	    RegistryItemsListVO registryItemsListVO = null;
		if (isGiftGiver)
		{
			registryItemsListVO = this.getGiftRegistryTools().fetchRegistryItemsFromEcomAdmin(registryId, regView);
		}
		else
		{
			registryItemsListVO = this.getGiftRegistryTools().fetchRegistryItemsFromEcomAdmin(registryId, false, regView);
		}
		
		return registryItemsListVO;
	}
	
	
	/**
	 * Fetch registry items from ecom admin.
	 * This is for the case where redesigned 
	 * My Items tab is to be shown. 
	 * @param registryId the registry id
	 * @param isGiftGiver the is gift giver
	 * @param isRLV the is rlv
	 * @param regView the reg view
	 * @return the registry items list vo
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public RegistryItemsListVO fetchRegistryItemsFromEcomAdmin(
			String registryId, boolean isGiftGiver, boolean isRLV,
			String regView) throws BBBSystemException, BBBBusinessException {
		
		if(isRLV) {
			return this.getGiftRegistryTools().fetchRegistryItemsFromEcomAdmin(registryId, isGiftGiver, isRLV, regView);
		} else {
			return fetchRegistryItemsFromEcomAdmin(registryId, isGiftGiver, regView);
		}
	}
	
	/* changes starts for story : BBBP-4572 & BBBP-4573 : Notify Registrant (Show Message) while adding N & D status items */
	
	/**
	 * This method is invoked to fetch registry event date from sessionBean using request object and registry id
	 *
	 *@param pRequest
	 * 			  the request object
	 * @param registryId
	 *            the registry id	 * 
	 * @return regEventDate 
	 * 			  the registry event date
	 * @throws BBBSystemException
	 *             the BBB system exception
	 * @throws BBBBusinessException
	 * 			  the BBB business exception
	 */
	
	public String getRegistryDate(final DynamoHttpServletRequest pRequest, String regId) throws BBBSystemException, BBBBusinessException{
		String regEventDate = null;	
		this.logDebug("getRegistryDate method starts.." + "registryId : " + regId);
		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(SESSION_BEAN);		
		List<RegistrySkinnyVO> rsVOList = (List<RegistrySkinnyVO>) sessionBean.getValues().get(BBBGiftRegistryConstants.PROP_REGISTRY_SKINNY_VO_LIST);
		if(!BBBUtility.isListEmpty(rsVOList)) {
		   this.logDebug("list of registries in session bean" + rsVOList);
           for (final RegistrySkinnyVO registry : rsVOList) {
                if (null != registry && registry.getRegistryId().equalsIgnoreCase(regId)) {
                	regEventDate = registry.getEventDate();
                	break;
                }
           }
		}
		this.logDebug("getRegistryDate method end. regEventDate = " + regEventDate);
		return regEventDate;
	}
	
	
	/**
	 * This method is invoked to fetch notify registrant message type, if required
	 *
	 * 
	 * @param regEventDate 
	 * 			  the registry event date
	 * @param regEventDate2 
	 * @return RegistryItemVO
	 * 		
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException 
	 */
	
	public String getNotifyRegistrantMsgType(String skuId, String regEventDate) throws BBBSystemException{		
		this.logDebug("getNotifyRegistrantMsgType() method starts...");		
		String displayMessageType = "";
		
		String siteId = getSiteId();
		this.logDebug("sku id :"+skuId+"siteid : " + siteId +" regEventDate : " + regEventDate);
		if(BBBUtility.isEmpty(regEventDate) || BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(regEventDate)|| siteId.contains(BBBGiftRegistryConstants.SITE_ID_TBS) || skuId == null){
			this.logDebug("regEventDate:"+regEventDate+":siteId:"+siteId+":regEventDate:"+regEventDate+"--return NotifyRegistrantMsgType as blank");
			return displayMessageType;
		}
		
		String skuStatusCode = "";
		RepositoryItem skuItem = null;
		long configuredInvThreshold = 0L;
		int configuredDaysOffThreshold = 0;
		
		try {		
			skuItem = getCatalogRepository().getItem(skuId, BBBGiftRegistryConstants.SKU);
			if(skuItem == null){
				this.logDebug("SKU item is null, sending displayMessageType as blank.");
				return displayMessageType;
			}
			
			skuStatusCode = getSkuNotifyRegStatusCode(siteId, skuStatusCode,
					skuItem);
			
			if(null != skuStatusCode && (skuStatusCode.equals(BBBGiftRegistryConstants.NOTIFY_REG_SKU_STATUS_CODE_N) || skuStatusCode.equals(BBBGiftRegistryConstants.NOTIFY_REG_SKU_STATUS_CODE_D))){

				//get configured threshold from config key
				configuredInvThreshold = getConfiguredInvThreshold(skuItem);
				if(configuredInvThreshold == -1 || configuredInvThreshold == 0){
					return displayMessageType;
				}
				
				boolean verifyInventoryRule = verifyInventoryRule(skuItem, siteId, configuredInvThreshold);
				
				List<String> daysOffthresholdList = this.getCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.DAYS_OFF_THRESHOLD_KEY);
				if (!BBBUtility.isListEmpty(daysOffthresholdList)){
					configuredDaysOffThreshold = Integer.parseInt(daysOffthresholdList.get(0));
				}
				String currentChannel = BBBUtility.getChannel();
				DateFormat dateFormat = null;
				if(regEventDate.contains(BBBGiftRegistryConstants.FORWARD_SLASH)){
					if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) && null != currentChannel && currentChannel.equalsIgnoreCase(BBBCoreConstants.DEFAULT_CHANNEL_VALUE)){
						dateFormat = new SimpleDateFormat(BBBGiftRegistryConstants.NOTIFY_REG_DATE_FORMAT1_CA);
					}
					else {
						dateFormat = new SimpleDateFormat(BBBGiftRegistryConstants.NOTIFY_REG_DATE_FORMAT1);
					}
				}
				else{
					dateFormat = new SimpleDateFormat(BBBGiftRegistryConstants.NOTIFY_REG_DATE_FORMAT2);
				}				
				
				Date today = new Date();					
				GregorianCalendar thresholdDate = new GregorianCalendar();
				thresholdDate.setTime(today);
				thresholdDate.add(Calendar.DATE, configuredDaysOffThreshold);
				thresholdDate.set(Calendar.HOUR, 0);
				thresholdDate.set(Calendar.MINUTE,0);
				thresholdDate.set(Calendar.SECOND,0);
				thresholdDate.set(Calendar.AM_PM,0);
				
		        Date dateObj = dateFormat.parse(regEventDate);        
		       	GregorianCalendar event = new GregorianCalendar();
		       	event.setTime(dateObj);
		       	
				if(verifyInventoryRule && event.getTime().after(thresholdDate.getTime())){
					displayMessageType = skuStatusCode;
				}
			}
		} 		
		catch(RepositoryException repoExcep){
			this.logError("getNotifyRegistrantMsgType method : Error occurred while getting sku item from sku id",repoExcep);
		}
		catch(BBBBusinessException excep){
			this.logError("getNotifyRegistrantMsgType method :  Error occurred while fetching configKeys from bcc",excep);
		}
		catch (BBBSystemException e) {
			this.logError("getNotifyRegistrantMsgType method :  Error occurred while setting notify registrant details",e);					
		}
		catch(ParseException pe){
			this.logError("getNotifyRegistrantMsgType method :  Error occurred while parsing date",pe);
		}
		
		this.logDebug("getNotifyRegistrantMsgType() method ends. Value of displayMessageType" + displayMessageType);
		return displayMessageType;		
	}

	protected String getSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

	/**
	 * This method fetches sku status code from skuItem.
	 * 
	 * @param siteId
	 * @param skuStatusCode
	 * @param skuItem
	 * @return
	 */
	private String getSkuNotifyRegStatusCode(String siteId,
			String skuStatusCode, RepositoryItem skuItem) {
		if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US) || siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)){
			if(skuItem.getPropertyValue(BBBGiftRegistryConstants.PROP_WEB_SKU_STATUS_CD) != null){
				skuStatusCode = (String) skuItem.getPropertyValue(BBBGiftRegistryConstants.PROP_WEB_SKU_STATUS_CD);
			}
		} else if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
			if(skuItem.getPropertyValue(BBBGiftRegistryConstants.PROP_CA_WEB_SKU_STATUS_CD) != null){
				skuStatusCode = (String) skuItem.getPropertyValue(BBBGiftRegistryConstants.PROP_CA_WEB_SKU_STATUS_CD);
			}
		}
		return skuStatusCode;
	}
	
	/**
	 * This method is invoked to fetch inventory threshold 
	 * value corresponding to sub-dept or dept.
	 * If not present, it will return global threshold value.
	 *
	 *@param skuItem
	 * 			  the skuItem
	 * @return configuredinvThreshold 
	 * 			  the threshold value configured in BCC
	 * @throws BBBSystemException
	 *             the BBB system exception
	 * @throws BBBBusinessException
	 * 			  the BBB business exception
	 */
	
	private long getConfiguredInvThreshold(RepositoryItem skuItem){
		//long inventoryThresholdInBCC = 0L;
		String associatedDept = null;	
		String associatedSubDept = null;
		
		long configuredInvThreshold = -1L;
		List<String> inventoryThresholdList = null;
		
		// fetching jdaSubDept and corresponding inventory threshold 
		if(skuItem.getPropertyValue(BBBGiftRegistryConstants.JDA_SUB_DEPT) != null){
			associatedSubDept = ((RepositoryItem)skuItem.getPropertyValue(BBBGiftRegistryConstants.JDA_SUB_DEPT)).getRepositoryId();
			if(StringUtils.isNotBlank(associatedSubDept)){
				try{
					inventoryThresholdList = this.getCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, associatedSubDept);
					if (!BBBUtility.isListEmpty(inventoryThresholdList)){
						configuredInvThreshold = Integer.parseInt(inventoryThresholdList.get(0));
					}
				}
				catch(BBBBusinessException | BBBSystemException excep){
					this.logDebug("key for associated sub-department" + associatedSubDept + "not found. ");
				}
			}			
		}	
		
		// if jdaSubDept or configured value is not present then fetching jdaSubDept and corresponding inventory threshold
		if(configuredInvThreshold == -1 && skuItem.getPropertyValue(BBBGiftRegistryConstants.JDA_DEPT) != null){
			associatedDept = ((RepositoryItem)skuItem.getPropertyValue(BBBGiftRegistryConstants.JDA_DEPT)).getRepositoryId();
			if(StringUtils.isNotBlank(associatedDept)){
				try{
					inventoryThresholdList = this.getCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, associatedDept);
					if (!BBBUtility.isListEmpty(inventoryThresholdList)){
						configuredInvThreshold = Integer.parseInt(inventoryThresholdList.get(0));
					}
				}
				catch(BBBBusinessException | BBBSystemException excep){
					this.logDebug("key for associated department" + associatedDept + "not found. ");
				}
			}	
		}
		
		// if jdaSubDept and jdaDept are not present or associated config key not present , fetch global threshold value.
		if(configuredInvThreshold == -1){
			try {
				inventoryThresholdList = this.getCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.NOTIFY_REG_CONFIG_TYPE, BBBGiftRegistryConstants.GLOBAL_INV_THRESHOLD_KEY);
				if (!BBBUtility.isListEmpty(inventoryThresholdList)){
					configuredInvThreshold = Integer.parseInt(inventoryThresholdList.get(0));
				}
			} 
			catch (BBBSystemException | BBBBusinessException e) {
				this.logError("key for global threshold not found. ");
			}			
		}		
		
		return configuredInvThreshold;		
	}
	
	
	/**
	 * This method is invoked to verify the threshold value with inventory
	 *
	 * @param skuRepositoryItem
	 *            sku repository item
	 * @param sku
	 *            sku id
	 * @param skuRepositoryItem
	 *            sku repository item
	 * @param skuRepositoryItem
	 *            sku repository item                      
	 * @return boolean
	 * @throws BBBSystemException 
	 */
	
	private boolean verifyInventoryRule(RepositoryItem skuRepositoryItem, String siteId, long configuredInvThreshold) throws BBBSystemException{
		String skuId = (String) skuRepositoryItem.getPropertyValue(BBBGiftRegistryConstants.NOTIFY_REG_SKU_ID);
		try{
			final long afs = this.getInventoryManager().getAfs(skuId, siteId).longValue();
	        this.logDebug("Value of afs[ " + afs + "]");
	        if (afs < configuredInvThreshold) {
	            return true;
	        }
	        return false;
	    } 
		catch (final BBBBusinessException | BBBSystemException e) {
	        this.logError("giftRegistryManager [verifyInventoryRule]: RepositoryException ",e);
	        throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                    BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
	    } 
		finally {
	    	this.logDebug("giftRegistryManager [verifyInventoryRule] siteId [ " + siteId + "] skuId " + skuId + " Exit");	        
	    }		
	}
	
	/* changes ends for story : BBBP-4572 & BBBP-4573 : Notify Registrant (Show Message) while adding N & D status items */
	
	/**
	 * This method is invoked to convert RegistrySummaryVO to RegistryVO.
	 *
	 * @param registrySummaryVO
	 *            the registry summary vo
	 * @return registryVO
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	private RegistryVO convertRegSummaryVOToRegistryVO(
			final RegistrySummaryVO registrySummaryVO) throws BBBSystemException,
			BBBBusinessException {
		this.logDebug("GiftRegistryManager.convertRegSummaryVOToRegistryVO() method start");

		final RegistryVO registryVO = new RegistryVO();

		final RegistrantVO registrantVO = new RegistrantVO();
		if (null != registrySummaryVO.getCoRegistrantFirstName()) {
			registrantVO.setFirstName(registrySummaryVO
					.getCoRegistrantFirstName());
		}
		if (null != registrySummaryVO.getCoRegistrantLastName()) {
			registrantVO.setLastName(registrySummaryVO
					.getCoRegistrantLastName());
		}

		registryVO.getPrimaryRegistrant().setFirstName(
				registrySummaryVO.getPrimaryRegistrantFirstName());
		registryVO.getPrimaryRegistrant().setLastName(
				registrySummaryVO.getPrimaryRegistrantLastName());

		final EventVO eventVO = new EventVO();
		eventVO.setEventDate(registrySummaryVO.getEventDate());

		registryVO.setRegistryId(registrySummaryVO.getRegistryId());
		registryVO.setCoRegistrant(registrantVO);
		registryVO.setEvent(eventVO);

		if (null != registrySummaryVO.getEventCode()) {
			registryVO.getRegistryType().setRegistryTypeName(
					registrySummaryVO.getEventCode());

		}

		if (registrySummaryVO.getRegistryType() != null) {
			registryVO.setRegistryType(registrySummaryVO.getRegistryType());
		}
		this.logDebug("GiftRegistryManager.convertRegSummaryVOToRegistryVO() method ends");

		return registryVO;
	}

	/**
	 * This method is invoked to link co-registrant to registry against
	 * emailAddress.
	 *
	 * @param emailId
	 *            the email id
	 * @param pProfile
	 *            the profile
	 * @param siteId
	 *            the site id
	 * @return boolean
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws RepositoryException
	 *             the repository exception
	 */
	public boolean doCoRegLinking(final String emailId, final Profile pProfile,
			final String siteId, final DynamoHttpServletRequest pRequest) throws BBBSystemException, BBBBusinessException,
			RepositoryException {
		this.logDebug("GiftRegistryManager.doCoRegLinking() method start");

		final boolean noError = false;
		final RegistrySearchVO regSearchVO = new RegistrySearchVO();
		regSearchVO.setEmail(emailId);
		regSearchVO.setProfileId(pProfile);
		regSearchVO.setReturnLeagacyRegistries(true);
		String catalogSiteId = "0";
		if (null != this.getBbbCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId)) {
			catalogSiteId = this.getBbbCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(
					0);
		}
		regSearchVO.setSiteId(catalogSiteId);
		regSearchVO.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(
				BBBWebServiceConstants.TXT_WSDLKEY,
				BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));

		regSearchVO.setServiceName(this.getSearchRegistryServiceName());

       // Call GiftRegistryTools API searchRegistries(registrySearchVO)

     	final RegSearchResVO regSearchResVO = this.searchRegistries(regSearchVO,siteId);
		if (regSearchResVO.getServiceErrorVO().isErrorExists()) {
			if (!BBBUtility.isEmpty(regSearchResVO.getServiceErrorVO()
					.getErrorDisplayMessage())
					&& (regSearchResVO.getServiceErrorVO().getErrorDisplayMessage().contains(".SQLException")))// SQL
																																		// Error
			{
				LogMessageFormatter.formatMessage(pRequest,"DB Connection could not be fetched in searchRegistries of doCoRegLinking from GiftRegistryManager webservice error id"+ regSearchResVO.getServiceErrorVO().getErrorId(),BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10117);
				throw new BBBSystemException("err_code_db_conn","err_code_db_conn");
			}
			if (!BBBUtility.isEmpty(regSearchResVO.getServiceErrorVO()
					.getErrorDisplayMessage())
					&& (regSearchResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS))// Technical
																																		// Error
			{
				LogMessageFormatter.formatMessage(pRequest,"Input field invalid in searchRegistries of doCoRegLinking from GiftRegistryManager webservice error id"+ regSearchResVO.getServiceErrorVO().getErrorId(),BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10117);
				throw new BBBSystemException("err_gift_reg_input_field_error","err_gift_reg_input_field_error");
			}
			if (!BBBUtility.isEmpty(regSearchResVO.getServiceErrorVO()
					.getErrorDisplayMessage())
					&& (regSearchResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR))// Technical
																																	// Error
			{

				LogMessageFormatter.formatMessage(pRequest,"Fatal error in searchRegistries of doCoRegLinking from GiftRegistryManager webservice error id"+ regSearchResVO.getServiceErrorVO().getErrorId(),BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10118);
				throw new BBBSystemException("err_gift_reg_fatal_error","err_gift_reg_fatal_error");
			}
			if (!BBBUtility.isEmpty(regSearchResVO.getServiceErrorVO()
					.getErrorDisplayMessage())
					&& (regSearchResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN))// Technical
																																				// Error
			{
				LogMessageFormatter.formatMessage(pRequest,"Either user token or site flag invalid in searchRegistries of doCoRegLinking from GiftRegistryManager webservice error id"+ regSearchResVO.getServiceErrorVO().getErrorId(),BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10119);
				throw new BBBSystemException("err_gift_reg_siteflag_usertoken_error",
						"err_gift_reg_siteflag_usertoken_error");
			}
			if (!BBBUtility.isEmpty(regSearchResVO.getServiceErrorVO()
					.getErrorDisplayMessage())
					&& (regSearchResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT))// Technical
																																			// Error
			{
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"GiftRegistry input fields format error in searchRegistries from doCoRegLinking()-SearchRegistry of " +
						"GiftRegistryManager | webservice error code=" + regSearchResVO.getServiceErrorVO().getErrorId(),
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

				throw new BBBSystemException("err_gift_reg_invalid_input_format",
						"err_gift_reg_invalid_input_format");
			}
		}

		final List<RegistrySummaryVO> linkedRegistriesVOList = regSearchResVO
				.getListRegistrySummaryVO();

		if (!BBBUtility.isListEmpty(linkedRegistriesVOList)) {

			RegistryResVO linkCoRegToRegistries = null;
			final RegistryReqVO regReqVO = new RegistryReqVO();
			regReqVO.setEmailId(emailId);
			regReqVO.setProfileId(pProfile.getRepositoryId());
			regReqVO.setSiteId(catalogSiteId);
			regReqVO.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY,
					BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
			regReqVO.setServiceName(this.getLinkCoRegProfileToRegServiceName());
			boolean regItemsWSCall = false;
			List<String> regItemsWSCallFlag = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall");
			if (!BBBUtility.isListEmpty(regItemsWSCallFlag)){
				regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag.get(0));
			}
			if (regItemsWSCall) {
				if (isLoggingDebug()) {
					this.logDebug("Invoking GiftRegistryTools.linkCoRegProfileToReg() method in WS mode[regItemsWSCall]: " + regItemsWSCall);
				}
				linkCoRegToRegistries = this.getGiftRegistryTools().linkCoRegProfileToReg(regReqVO, regItemsWSCall);
			} else {
				if (isLoggingDebug()) {
					this.logDebug("Invoking GiftRegistryTools.linkCoRegProfileToReg() method in PROC mode[regItemsWSCall]: " + regItemsWSCall);
				}
				if (!BBBUtility.isEmpty(pProfile.getRepositoryId())) {
					linkCoRegToRegistries = this.getGiftRegistryTools().linkCoRegProfileToReg(regReqVO, regItemsWSCall);
				} else {
					logError("Profile Id Can't be empty");
					ServiceErrorVO errorVO = new ServiceErrorVO();
					errorVO.setErrorExists(true);
					errorVO.setErrorId(BBBGiftRegistryConstants.VALIDATION);
					linkCoRegToRegistries = new RegistryResVO();
					linkCoRegToRegistries.setServiceErrorVO(errorVO);
				} 
			}
			

			// Call GiftRegistryTools API
			// linkCoRegistrantToRegistries(registryReqVO)
			

			if (linkCoRegToRegistries.getServiceErrorVO().isErrorExists()) {

				if (!BBBUtility.isEmpty(linkCoRegToRegistries
						.getServiceErrorVO().getErrorDisplayMessage())
						&& (linkCoRegToRegistries.getServiceErrorVO()
								.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS))// Technical
																											// Error
				{
					LogMessageFormatter.formatMessage(pRequest," input field invalid in linkCoRegToRegistries of doCoRegLinking from GiftRegistryManager webservice error id"+ regSearchResVO.getServiceErrorVO().getErrorId(),BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10120);
					throw new BBBSystemException("err_gift_reg_input_field_error",
							"err_gift_reg_input_field_error");
				}

				if (!BBBUtility.isEmpty(linkCoRegToRegistries
						.getServiceErrorVO().getErrorDisplayMessage())
						&& (linkCoRegToRegistries.getServiceErrorVO()
								.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR))// Technical
																											// Error
				{
					LogMessageFormatter.formatMessage(pRequest," Fatal error in linkCoRegToRegistries of doCoRegLinking from GiftRegistryManager webservice error id"+ regSearchResVO.getServiceErrorVO().getErrorId(),BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10121);
					throw new BBBSystemException("err_gift_reg_fatal_error","err_gift_reg_fatal_error");
				}
				if (!BBBUtility.isEmpty(linkCoRegToRegistries
						.getServiceErrorVO().getErrorDisplayMessage())
						&& (linkCoRegToRegistries.getServiceErrorVO()
								.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN))// Technical
																													// Error
				{
					LogMessageFormatter.formatMessage(pRequest," Either user token or site flag in linkCoRegToRegistries of doCoRegLinking from GiftRegistryManager webservice error id"+ regSearchResVO.getServiceErrorVO().getErrorId(),BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10122);
					throw new BBBSystemException("err_gift_reg_siteflag_usertoken_error",
							"err_gift_reg_siteflag_usertoken_error");
				}
				if (!BBBUtility.isEmpty(linkCoRegToRegistries
						.getServiceErrorVO().getErrorDisplayMessage())
						&& (linkCoRegToRegistries.getServiceErrorVO()
								.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT))// Technical
																													// Error
				{
					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"GiftRegistry input fields format error from doCoRegLinking() -linkCoRegistrant of " +
							"GiftRegistryManager | webservice error code=" + linkCoRegToRegistries.getServiceErrorVO().getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

					throw new BBBSystemException("err_gift_reg_invalid_input_format",
							"err_gift_reg_invalid_input_format");
				}
			}

			// String registryId = null;

			MutableRepositoryItem pProfileItem = null;

			RegistryVO registryVO = new RegistryVO();

			for (final RegistrySummaryVO regSumVO : linkedRegistriesVOList) {
				registryVO = this.convertRegSummaryVOToRegistryVO(regSumVO);
				// registryId = registryVO.getRegistryId();

				if (registryVO != null) {

					// Check if email  match to registry coRegistrant email//ADD coregistrant entry to gift registry repository
					if(emailId.equalsIgnoreCase(regSumVO.getCoRegistrantEmail())){
						pProfileItem = this.getProfileItemFromEmail(emailId,
								regReqVO.getSiteId());
						registryVO.setSiteId(regReqVO.getSiteId());

						// add or update registry with the current profile as
						// CoRegistrant
						if (null != pProfileItem) {
							this.getGiftRegistryTools().addORUpdateRegistry(registryVO, null,pProfileItem);
						}
					}
				}

			}

		}
		this.logDebug("GiftRegistryManager.doCoRegLinking() method ends");

		return noError;
	}



	/**
	 * Fetch users soonest or recent.
	 *
	 * @param registryIds
	 *            the registry ids
	 * @return Registry Id
	 * @throws RepositoryException
	 *             the repository exception
	 * @throws NumberFormatException
	 *             the number format exception
	 */
	public String fetchUsersSoonestOrRecent(final List<String> registryIds)
			throws RepositoryException, NumberFormatException {
		return this.getGiftRegistryTools().fetchUsersSoonestOrRecent(registryIds);
	}

	public String fetchUsersSoonestRegistry(final List<String> registryIds, final String siteId )
			throws RepositoryException, NumberFormatException {
		final List<String> registrylist = registryIds;
		final List<String> futureRegistrylist = new ArrayList<String>();
		for(final String regId : registrylist)
		{
			try {
				final RegistryReqVO regReqVO = new RegistryReqVO();
				RegistrySummaryVO registrySummaryVO =null;
				regReqVO.setRegistryId(regId);
				regReqVO.setServiceName(this.getRegistryInfoServiceName());
				regReqVO.setSiteId(this.getBbbCatalogTools().getAllValuesForKey(	BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
				regReqVO.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
				RegistryResVO registryResVO = null;

				registryResVO = getRegistryInfoFromWSorDB(regReqVO);

				if ((registryResVO != null) &&  (registryResVO.getRegistrySummaryVO() != null) && (registryResVO.getRegistrySummaryVO().getEventDate() != null)) {
					if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
						registryResVO.getRegistrySummaryVO().setEventDate(BBBUtility.convertWSDateToCAFormat(registryResVO.getRegistrySummaryVO().getEventDate()));
						if(registryResVO.getRegistrySummaryVO().getFutureShippingDate() != null){
							registryResVO.getRegistrySummaryVO().setFutureShippingDate(BBBUtility.convertAppFormatDateToCAFormat(registryResVO.getRegistrySummaryVO().getFutureShippingDate()));
						}
					}else{
						registryResVO.getRegistrySummaryVO().setEventDate(BBBUtility.convertWSDateToUSFormat(registryResVO.getRegistrySummaryVO().getEventDate()));
					}
					this.logDebug("GiftRegistryManager.fetchUsersSoonestRegistry MSG= registryResVO from webservice :" + registryResVO);

					registrySummaryVO = registryResVO.getRegistrySummaryVO();
					final long diffDays = getDateDiff(siteId, registrySummaryVO);
					if (diffDays >= -90) {
						futureRegistrylist.add(regId);
					}
					
				}

				
			} catch (final BBBSystemException e) {
				this.logError("Error in fetchUsersSoonestRegistry",e);
				throw new RepositoryException(e.getMessage(), e);
			} catch (final BBBBusinessException e) {
				this.logError("Error in fetchUsersSoonestRegistry",e);
				throw new RepositoryException(e.getMessage(), e);
			} catch (final ParseException e) {
				this.logError("Error in fetchUsersSoonestRegistry",e);
				throw new RepositoryException(e.getMessage(), e);
			}
		}
		if(futureRegistrylist.isEmpty()){
			return null;
		}
		return this.getGiftRegistryTools().fetchUsersSoonestOrRecent(futureRegistrylist);
	}


	/**
	 * This method is used to update registrant porfile information during
	 * registry create or update.
	 *
	 * @param pRegistryVO
	 *            the registry vo
	 * @param shippingAddress
	 *            the shipping address
	 * @param futureShipAddress
	 *            the future ship address
	 * @param pProfile
	 *            the profile
	 * @param defaultCountry
	 *            the default country
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public void updateRegistrantProfileInfo(final RegistryVO pRegistryVO,
			final String shippingAddress, final String futureShipAddress, final Profile pProfile,
			final String defaultCountry) throws BBBSystemException,
			BBBBusinessException {

		this.logDebug("GiftRegistryFormHandler.updateRegistrantProfileInfo() method start");
		// Updating registrant phone and mobile number
		/*updateContactNumbers(pRegistryVO.getPrimaryRegistrant().getEmail(),
				pRegistryVO.getPrimaryRegistrant().getPrimaryPhone(),
				pRegistryVO.getPrimaryRegistrant().getCellPhone());*/

		BBBAddressVO bbbAddressVO = null;

		/*********************** New Logic Address logic for shipping to- Address BOOk ***********************/
		if ("newShippingAddress".equalsIgnoreCase(shippingAddress)
				|| "oldShippingAddressFromWS".equalsIgnoreCase(shippingAddress)) {

			/*
			 * If shipping address is new or from webservice save it to address
			 * book
			 */
			if ((pRegistryVO.getShipping().getShippingAddress()
					.getAddressLine1() != null)
					&& !pRegistryVO.getShipping().getShippingAddress()
							.getAddressLine1().trim().equalsIgnoreCase("")) {
				bbbAddressVO = new BBBAddressVO();
				bbbAddressVO = this.addressVOMap(pRegistryVO.getShipping()
						.getShippingAddress(), bbbAddressVO);
				bbbAddressVO.setCountry(defaultCountry);
				@SuppressWarnings("unchecked")
				final List<RepositoryItem> addresses = this.getTools().getAllAvailableAddresses(pProfile);
				if(BBBUtility.isListEmpty(addresses)){
					this.getBbbAddressAPI().addNewShippingAddress(pProfile,
							bbbAddressVO, pRegistryVO.getSiteId(), true, true);
				} else {
					this.getBbbAddressAPI().addNewShippingAddress(pProfile,
						bbbAddressVO, pRegistryVO.getSiteId(), false, false);
				}

			}
		}

		/***********************
		 * New Logic Address logic for primary registrant address to- Address
		 * BOOk
		 ***********************/
		final String status = pRegistryVO.getPrimaryRegistrant().getAddressSelected();
		if ("newPrimaryRegAddress".equalsIgnoreCase(pRegistryVO
				.getPrimaryRegistrant().getAddressSelected())
				|| "oldRegistrantAddressFromWS".equalsIgnoreCase(pRegistryVO
						.getPrimaryRegistrant().getAddressSelected())) {

			/*
			 * If primary registrant address is new or from webservice Save it
			 * to address book
			 */
			if ((pRegistryVO.getPrimaryRegistrant().getContactAddress()
					.getAddressLine1() != null)
					&& !pRegistryVO.getPrimaryRegistrant().getContactAddress()
							.getAddressLine1().trim().equalsIgnoreCase("")) {
				bbbAddressVO = new BBBAddressVO();
				bbbAddressVO = this.addressVOMap(pRegistryVO.getPrimaryRegistrant()
						.getContactAddress(), bbbAddressVO);
				bbbAddressVO.setCountry(defaultCountry);
				@SuppressWarnings("unchecked")
				final List<RepositoryItem> addresses = this.getTools().getAllAvailableAddresses(pProfile);
				if (((addresses == null) || addresses.isEmpty()) && ((status != null) && status.equalsIgnoreCase("newPrimaryRegAddress"))) {
					this.getBbbAddressAPI().addNewShippingAddress(pProfile,
							bbbAddressVO, pRegistryVO.getSiteId(), true, true);
				} else {
					this.getBbbAddressAPI().addNewShippingAddress(pProfile,
						bbbAddressVO, pRegistryVO.getSiteId(), false, false);
				}
			}

		}

		/*********************** New Logic Address logic for future shipping to- Address BOOk ***********************/
		if ("newFutureShippingAddress".equalsIgnoreCase(futureShipAddress)
				|| "oldFutShippingAddressFromWS"
						.equalsIgnoreCase(futureShipAddress)) {

			/*
			 * If future shipping address is new or from webservice Save it to
			 * address book
			 */
			if ((pRegistryVO.getShipping().getFutureshippingAddress()
					.getAddressLine1() != null)
					&& !pRegistryVO.getShipping().getFutureshippingAddress()
							.getAddressLine1().trim().equalsIgnoreCase("")
					&& !pRegistryVO.getShipping().getFutureshippingAddress()
							.getCity().trim().equalsIgnoreCase("")) {
				bbbAddressVO = new BBBAddressVO();
				bbbAddressVO = this.addressVOMap(pRegistryVO.getShipping()
						.getFutureshippingAddress(), bbbAddressVO);
				bbbAddressVO.setCountry(defaultCountry);
				@SuppressWarnings("unchecked")
				final List<RepositoryItem> addresses = this.getTools().getAllAvailableAddresses(pProfile);
				if (BBBUtility.isListEmpty(addresses)) {
					this.getBbbAddressAPI().addNewShippingAddress(pProfile,
							bbbAddressVO, pRegistryVO.getSiteId(), true, true);
				} else {
					this.getBbbAddressAPI().addNewShippingAddress(pProfile,
						bbbAddressVO, pRegistryVO.getSiteId(), false, false);
				}
			}

		}

		this.logDebug("GiftRegistryFormHandler.updateRegistrantProfileInfo() method ends");

	}

	/**
	 * This method is used to map address VO.
	 *
	 * @param pAddressCO
	 *            the address co
	 * @param bbbAddressVO
	 *            the bbb address vo
	 * @return the bBB address vo
	 */
	private BBBAddressVO addressVOMap(final AddressVO pAddressCO,
			final BBBAddressVO bbbAddressVO) {

		this.logDebug("GiftRegistryManager.addressVOMap() method start");

		if (!BBBUtility.isEmpty(pAddressCO.getFirstName())) {
			bbbAddressVO.setFirstName(pAddressCO.getFirstName());
		}

		if (!BBBUtility.isEmpty(pAddressCO.getLastName())) {
			bbbAddressVO.setLastName(pAddressCO.getLastName());
		}

		if (!BBBUtility.isEmpty(pAddressCO.getAddressLine1())) {
			bbbAddressVO.setAddress1(pAddressCO.getAddressLine1());
		}

		if (!BBBUtility.isEmpty(pAddressCO.getAddressLine2())) {
			bbbAddressVO.setAddress2(pAddressCO.getAddressLine2());
		}

		if (!BBBUtility.isEmpty(pAddressCO.getCity())) {
			bbbAddressVO.setCity(pAddressCO.getCity());
		}

		if (!BBBUtility.isEmpty(pAddressCO.getState())) {
			bbbAddressVO.setState(pAddressCO.getState());
		}

		if (!BBBUtility.isEmpty(pAddressCO.getZip())) {
			bbbAddressVO.setPostalCode(pAddressCO.getZip());
		}

		bbbAddressVO.setPoBoxAddress(pAddressCO.isPoBoxAddress());
		bbbAddressVO.setQasValidated(pAddressCO.isQasValidated());
		
		this.logDebug("GiftRegistryManager.addressVOMap() method ends");
		return bbbAddressVO;
	}

	/**
	 * Compare date.
	 *
	 * @param date
	 *            the date
	 * @return the date
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws ParseException
	 *             the parse exception
	 */
	public Date compareDate(final String date) throws BBBBusinessException,
			BBBSystemException, ParseException {
		return this.getGiftRegistryTools().compareDate(date);
	}

	/**
	 * This method is used to rend a email to coregistrant once registry is
	 * created and updated.
	 *
	 * @param siteId
	 *            the site id
	 * @param subject
	 *            the subject
	 * @param registryVO
	 *            the registry vo
	 * @param pCoRegEmailFoundPopupStatus
	 *            the co reg email found popup status
	 * @param coEmailNotFoundPopup
	 *            the co email not found popup
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws TemplateEmailException
	 *             the template email exception
	 * @throws RepositoryException
	 *             the repository exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sendEmailToCoregistrant(final String registryURL, final String accountLoginURL, final String siteId, final String subject,
			final RegistryVO registryVO, final String pCoRegEmailFoundPopupStatus,
			final String coEmailNotFoundPopup, final TemplateEmailInfoImpl emailInfo ) throws BBBBusinessException,
			BBBSystemException, TemplateEmailException, RepositoryException {

		this.logDebug("GiftRegistryManager.sendEmailToCoregistrant() method start");

		final Map emailParams = new HashMap<String, String>();
		final Map<String, String> placeHolderValues = new HashMap<String, String>();
		final long uniqueKeyDate = getUniquekeyDate();
		String profileId = null;
		final Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName("/atg/userprofiling/Profile");
		if(profile != null){
		    profileId = profile.getRepositoryId();
		}


		final String emailPersistId = profileId + uniqueKeyDate;


		placeHolderValues.put(BBBGiftRegistryConstants.P_REG_FIRST_NAME,
				registryVO.getPrimaryRegistrant().getFirstName());
		placeHolderValues.put(BBBGiftRegistryConstants.P_REG_LAST_NAME,
				registryVO.getPrimaryRegistrant().getLastName());
		placeHolderValues.put(BBBGiftRegistryConstants.COREG_FIRST_NAME,
				registryVO.getCoRegistrant().getFirstName());
		placeHolderValues.put(BBBGiftRegistryConstants.COREG_LAST_NAME,
				registryVO.getCoRegistrant().getLastName());

		placeHolderValues.put(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE,
				this.getBbbCatalogTools().getRegistryTypeName(registryVO.getRegistryType().getRegistryTypeName(),siteId));

		placeHolderValues.put(BBBGiftRegistryConstants.REGISTRY_GUEST_URL,
				registryURL + this.getBbbCatalogTools().getRegistryTypeName(registryVO.getRegistryType().getRegistryTypeName(),siteId));

		placeHolderValues.put(BBBGiftRegistryConstants.ACCOUNT_LOGIN_URL, accountLoginURL);


		placeHolderValues.put(BBBGiftRegistryConstants.REGISTRY_ID_KEY,
				registryVO.getRegistryId());
		placeHolderValues.put(BBBCoreConstants.EMAIL_PERSIST_ID, emailPersistId);

		emailParams.put(BBBCoreConstants.SUBJECT_PARAM_NAME, subject);
		emailParams.put(BBBCoreConstants.TEMPLATE_URL_PARAM_NAME,
				this.getTemplateUrl());
		emailParams.put(BBBCoreConstants.SENDER_EMAIL_PARAM_NAME, registryVO
				.getPrimaryRegistrant().getEmail());
		emailParams.put(BBBCoreConstants.SITE_ID, siteId);
		emailParams.put(BBBCoreConstants.RECIPIENT_EMAIL_PARAM_NAME, registryVO
				.getCoRegistrant().getEmail());

		placeHolderValues.put(BBBGiftRegistryConstants.FRMDATA_SITEID, siteId);

		if (BBBCoreConstants.TRUE.equals(pCoRegEmailFoundPopupStatus)) {
			placeHolderValues.put(BBBGiftRegistryConstants.EMAIL_TYPE,
					this.getEmailCoFoundRegistryType());
			emailParams.put(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES,
					placeHolderValues);
			BBBEmailHelper.sendEmail(null, emailParams, this.getEmailSender(),
					emailInfo);
		} else if (BBBCoreConstants.TRUE.equalsIgnoreCase(coEmailNotFoundPopup)
				&& !BBBUtility.isEmpty(registryVO.getCoRegistrant().getEmail())) {
			placeHolderValues.put(BBBGiftRegistryConstants.EMAIL_TYPE,
					this.getEmailCoNotFoundRegistryType());
			emailParams.put(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES,
					placeHolderValues);
			BBBEmailHelper.sendEmail(null, emailParams, this.getEmailSender(),
					emailInfo);
		}

		this.logDebug("GiftRegistryManager.sendEmailToCoregistrant() method ends");

	}

	protected long getUniquekeyDate() {
		final Calendar currentDate = Calendar.getInstance();
		final long uniqueKeyDate = currentDate.getTimeInMillis();
		return uniqueKeyDate;
	}

	/**
	 * Remove an item from a registry.
	 *
	 * @param pProfile
	 *            the profile
	 * @param pGiftRegistryViewBean
	 *            the gift registry view bean
	 * @return error value
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public ManageRegItemsResVO removeUpdateGiftRegistryItem(final Profile pProfile,
			final GiftRegistryViewBean pGiftRegistryViewBean)
			throws BBBBusinessException, BBBSystemException {

		boolean regItemsWSCall = false;
		List<String> regItemsWSCallFlag = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall");
		if (!BBBUtility.isListEmpty(regItemsWSCallFlag)){
			regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag.get(0));
		}
		if (regItemsWSCall){
			// if RegItemsWSCall key is on, invoke method for calling webservice
			final ManageRegItemsResVO mageItemsResVO = this.getGiftRegistryTools().removeUpdateGiftRegistryItem(pGiftRegistryViewBean);		
			return mageItemsResVO;
		}
		else{
			// if RegItemsWSCall key is off, invoke method to update ecomadmin without webservice call
			final ManageRegItemsResVO mageItemsResVO = this.getGiftRegistryTools().removeUpdateGiftRegistryItemInEcomAdmin(pGiftRegistryViewBean);
			return mageItemsResVO;
		}			
	}

	/**
	 * This method is used to rend a email to coregistrant once registry is
	 * created and updated.
	 *
	 * @param siteId
	 *            the site id
	 * @param values
	 *            the values
	 * @return true, if successful
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean sendEmailRegistry(final String siteId, final Map<String, Object> values,
			final TemplateEmailInfoImpl pEmailInfo)
			throws BBBSystemException {

		this.logDebug("GiftRegistryManager.sendEmailRegistry() method starts");

		String namesRegAndCoReg = "";
		String pRegAndCoRegFname = "";

		boolean emailSuccess = true;

		final Map emailParams = new HashMap<String, String>();
		final Map<String, String> placeHolderValues = new HashMap<String, String>();
		final long uniqueKeyDate = getUniquekeyDate();
		String profileId = null;
		final Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName("/atg/userprofiling/Profile");
		if(profile != null){
		    profileId = profile.getRepositoryId();
		}

		final String emailPersistId = profileId + uniqueKeyDate;

		placeHolderValues.put(BBBCoreConstants.REGISTRY_URL,(String) values.get(BBBCoreConstants.REGISTRY_URL));
		placeHolderValues.put(BBBGiftRegistryConstants.P_REG_FIRST_NAME,
				(String) values.get(BBBGiftRegistryConstants.P_REG_FIRST_NAME));
		placeHolderValues.put(BBBGiftRegistryConstants.P_REG_LAST_NAME,
				(String) values.get(BBBGiftRegistryConstants.P_REG_LAST_NAME));
		placeHolderValues.put(BBBGiftRegistryConstants.COREG_FIRST_NAME,
				(String) values.get(BBBGiftRegistryConstants.COREG_FIRST_NAME));
		placeHolderValues.put(BBBGiftRegistryConstants.COREG_LAST_NAME,
				(String) values.get(BBBGiftRegistryConstants.COREG_LAST_NAME));
		placeHolderValues.put(BBBCoreConstants.MESSAGE, atg.core.util.StringUtils.escapeHtmlString(
				(String) values.get(BBBCoreConstants.MESSAGE)));
		namesRegAndCoReg = namesRegAndCoReg
				+ values.get(BBBGiftRegistryConstants.P_REG_FIRST_NAME) + " "
				+ values.get(BBBGiftRegistryConstants.P_REG_LAST_NAME);

		pRegAndCoRegFname = pRegAndCoRegFname
				+ values.get(BBBGiftRegistryConstants.P_REG_FIRST_NAME);

		if ((values.get(BBBGiftRegistryConstants.COREG_FIRST_NAME) != null)
				&& (values.get(BBBGiftRegistryConstants.COREG_FIRST_NAME)
						.toString().trim().length() > 0)) {
			namesRegAndCoReg = namesRegAndCoReg + " &amp; "
					+ values.get(BBBGiftRegistryConstants.COREG_FIRST_NAME)
					+ " "
					+ values.get(BBBGiftRegistryConstants.COREG_LAST_NAME);
			pRegAndCoRegFname = pRegAndCoRegFname + " and "
					+ values.get(BBBGiftRegistryConstants.COREG_FIRST_NAME);
			placeHolderValues.put(BBBGiftRegistryConstants.EMAIL_TYPE,
					this.getEmailARegistryType());
		} else {
			placeHolderValues.put(BBBGiftRegistryConstants.EMAIL_TYPE,
					this.getEmailAPRegRegistryType());
		}

		placeHolderValues.put(BBBGiftRegistryConstants.ALL_NAMES_KEY,
				namesRegAndCoReg);

		placeHolderValues.put(BBBGiftRegistryConstants.FIRST_NAMES_KEY,
				pRegAndCoRegFname);

		placeHolderValues.put(BBBGiftRegistryConstants.EVENT_TYPE_REGISTRY,
				(String) values.get(BBBGiftRegistryConstants.EVENT_TYPE_REGISTRY));
		placeHolderValues.put(BBBGiftRegistryConstants.EVENT_DATE,
				(String) values.get(BBBGiftRegistryConstants.EVENT_DATE));
		placeHolderValues
				.put(BBBGiftRegistryConstants.DAYS_TO_GO,
						values.get(BBBGiftRegistryConstants.DAYS_TO_GO) != null ? String.valueOf(values
								.get(BBBGiftRegistryConstants.DAYS_TO_GO))
								: null);
		placeHolderValues.put(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE,
				(String) values
						.get(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE));

		placeHolderValues.put(BBBGiftRegistryConstants.DATE_LABEL,(String)values.get(BBBGiftRegistryConstants.DATE_LABEL));
		placeHolderValues.put(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE,atg.core.util.StringUtils.escapeHtmlString(
				(String) values.get(BBBGiftRegistryConstants.MESSAGE_REGISTRY)));
		placeHolderValues.put(BBBGiftRegistryConstants.FRMDATA_SITEID, siteId);
		placeHolderValues.put(BBBCoreConstants.EMAIL_PERSIST_ID, emailPersistId);

		emailParams.put(BBBCoreConstants.SENDER_EMAIL_PARAM_NAME,
				values.get(BBBCoreConstants.SENDER_EMAIL));

		//To Address
		emailParams.put(BBBCoreConstants.RECIPIENT_EMAIL_PARAM_NAME,
				pEmailInfo.getMessageFrom());
		pEmailInfo.setMessageTo(pEmailInfo.getMessageFrom());

		if (values.containsKey("messageCC")) {

			pEmailInfo.setMessageCc((String) values.get(BBBCoreConstants.SENDER_EMAIL));
			this.logDebug("GiftRegistryManager.sendEmailRegistry() MSG=[setting messageCC="+(String) values.get(BBBCoreConstants.SENDER_EMAIL));
		}

		//All recipients should be in BCC
		String bccRecipients = (String) values.get(BBBCoreConstants.RECIPIENT_EMAIL);
		if(BBBUtility.isNotEmpty(bccRecipients)){
			bccRecipients = bccRecipients.replace(BBBCoreConstants.SPACE, BBBCoreConstants.BLANK).trim();
			bccRecipients = bccRecipients.replace(BBBCoreConstants.SEMICOLON, BBBCoreConstants.COMMA);
			this.logDebug("GiftRegistryManager.sendEmailRegistry() MSG=[setting bccRecipients="+bccRecipients);
		}
		pEmailInfo.setMessageBcc(bccRecipients);

		emailParams.put(BBBCoreConstants.TEMPLATE_URL_PARAM_NAME,
				this.getTemplateUrl());
		if (values.get(BBBCoreConstants.SUBJECT_PARAM_NAME) != null) {
			emailParams.put(BBBCoreConstants.SUBJECT_PARAM_NAME,
					values.get(BBBCoreConstants.SUBJECT_PARAM_NAME));
		}

		emailParams.put(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES,
				placeHolderValues);

		try {
			BBBEmailHelper.sendEmail(null, emailParams, this.getEmailSender(),
					pEmailInfo);

		} catch (final TemplateEmailException e) {
			emailSuccess = false;
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10123+" BBBBusinessException of sendEmailRegistry from GiftRegistryManager",e);
		} catch (final RepositoryException rex) {
			emailSuccess = false;
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10124+" RepositoryException of sendEmailRegistry from GiftRegistryManager",rex);
		}

		this.logDebug("GiftRegistryManager.sendEmailRegistry() method ends");

		return emailSuccess;
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean sendEmailMxRegistry(final String siteId, final Map<String, Object> values,
			final TemplateEmailInfoImpl pEmailInfo)
			throws BBBSystemException {

		this.logDebug("GiftRegistryManager.sendEmailRegistry() method starts");

		String namesRegAndCoReg = "";
		String pRegAndCoRegFname = "";

		boolean emailSuccess = true;

		final Map emailParams = new HashMap<String, String>();
		final Map<String, String> placeHolderValues = new HashMap<String, String>();
		final long uniqueKeyDate = getUniquekeyDate();
		String profileId = null;
		final Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName("/atg/userprofiling/Profile");
		if(profile != null){
		    profileId = profile.getRepositoryId();
		}

		final String emailPersistId = profileId + uniqueKeyDate;

		placeHolderValues.put(BBBCoreConstants.REGISTRY_URL,(String) values.get(BBBCoreConstants.REGISTRY_URL));
		placeHolderValues.put(BBBGiftRegistryConstants.P_REG_FIRST_NAME,
				(String) values.get(BBBGiftRegistryConstants.P_REG_FIRST_NAME));
		placeHolderValues.put(BBBGiftRegistryConstants.P_REG_LAST_NAME,
				(String) values.get(BBBGiftRegistryConstants.P_REG_LAST_NAME));
		placeHolderValues.put(BBBGiftRegistryConstants.COREG_FIRST_NAME,
				(String) values.get(BBBGiftRegistryConstants.COREG_FIRST_NAME));
		placeHolderValues.put(BBBGiftRegistryConstants.COREG_LAST_NAME,
				(String) values.get(BBBGiftRegistryConstants.COREG_LAST_NAME));
		placeHolderValues.put(BBBCoreConstants.MESSAGE, atg.core.util.StringUtils.escapeHtmlString(
				(String) values.get(BBBCoreConstants.MESSAGE)));
		namesRegAndCoReg = namesRegAndCoReg
				+ values.get(BBBGiftRegistryConstants.P_REG_FIRST_NAME) + " "
				+ values.get(BBBGiftRegistryConstants.P_REG_LAST_NAME);

		pRegAndCoRegFname = pRegAndCoRegFname
				+ values.get(BBBGiftRegistryConstants.P_REG_FIRST_NAME);

		if ((values.get(BBBGiftRegistryConstants.COREG_FIRST_NAME) != null)
				&& (values.get(BBBGiftRegistryConstants.COREG_FIRST_NAME)
						.toString().trim().length() > 0)) {
			namesRegAndCoReg = namesRegAndCoReg + " &amp; "
					+ values.get(BBBGiftRegistryConstants.COREG_FIRST_NAME)
					+ " "
					+ values.get(BBBGiftRegistryConstants.COREG_LAST_NAME);
			pRegAndCoRegFname = pRegAndCoRegFname + " and "
					+ values.get(BBBGiftRegistryConstants.COREG_FIRST_NAME);
			placeHolderValues.put(BBBGiftRegistryConstants.EMAIL_TYPE,
					this.getMxEmailARegistryType());
		} else {
			placeHolderValues.put(BBBGiftRegistryConstants.EMAIL_TYPE,
					this.getMxEmailAPRegRegistryType());
		}

		placeHolderValues.put(BBBGiftRegistryConstants.ALL_NAMES_KEY,
				namesRegAndCoReg);

		placeHolderValues.put(BBBGiftRegistryConstants.FIRST_NAMES_KEY,
				pRegAndCoRegFname);

		placeHolderValues.put(BBBGiftRegistryConstants.EVENT_DATE,
				(String) values.get(BBBGiftRegistryConstants.EVENT_DATE));
		placeHolderValues
				.put(BBBGiftRegistryConstants.DAYS_TO_GO,
						values.get(BBBGiftRegistryConstants.DAYS_TO_GO) != null ? String.valueOf(values
								.get(BBBGiftRegistryConstants.DAYS_TO_GO))
								: null);
		placeHolderValues.put(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE,
				(String) values
						.get(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE));

		placeHolderValues.put(BBBGiftRegistryConstants.DATE_LABEL,(String)values.get(BBBGiftRegistryConstants.DATE_LABEL));
		placeHolderValues.put(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE,atg.core.util.StringUtils.escapeHtmlString(
				(String) values.get(BBBGiftRegistryConstants.MESSAGE_REGISTRY)));
		placeHolderValues.put(BBBGiftRegistryConstants.FRMDATA_SITEID, siteId);
		placeHolderValues.put(BBBCoreConstants.EMAIL_PERSIST_ID, emailPersistId);

		emailParams.put(BBBCoreConstants.SENDER_EMAIL_PARAM_NAME,
				values.get(BBBCoreConstants.SENDER_EMAIL));

		//To Address
		emailParams.put(BBBCoreConstants.RECIPIENT_EMAIL_PARAM_NAME,
				pEmailInfo.getMessageFrom());
		pEmailInfo.setMessageTo(pEmailInfo.getMessageFrom());

		if (values.containsKey("messageCC")) {

			pEmailInfo.setMessageCc((String) values.get(BBBCoreConstants.SENDER_EMAIL));
			this.logDebug("GiftRegistryManager.sendEmailRegistry() MSG=[setting messageCC="+(String) values.get(BBBCoreConstants.SENDER_EMAIL));
		}

		//All recipients should be in BCC
		String bccRecipients = (String) values.get(BBBCoreConstants.RECIPIENT_EMAIL);
		if(BBBUtility.isNotEmpty(bccRecipients)){
			bccRecipients = bccRecipients.replace(BBBCoreConstants.SPACE, BBBCoreConstants.BLANK).trim();
			bccRecipients = bccRecipients.replace(BBBCoreConstants.SEMICOLON, BBBCoreConstants.COMMA);
			this.logDebug("GiftRegistryManager.sendEmailRegistry() MSG=[setting bccRecipients="+bccRecipients);
		}
		pEmailInfo.setMessageBcc(bccRecipients);

		emailParams.put(BBBCoreConstants.TEMPLATE_URL_PARAM_NAME,
				this.getTemplateUrl());
		if (values.get(BBBCoreConstants.SUBJECT_PARAM_NAME) != null) {
			emailParams.put(BBBCoreConstants.SUBJECT_PARAM_NAME,
					values.get(BBBCoreConstants.SUBJECT_PARAM_NAME));
		}

		emailParams.put(BBBGiftRegistryConstants.PLACE_HOLDER_VALUES,
				placeHolderValues);

		try {
			BBBEmailHelper.sendEmail(null, emailParams, this.getEmailSender(),
					pEmailInfo);

		} catch (final TemplateEmailException e) {
			emailSuccess = false;
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10123+" BBBBusinessException of sendEmailRegistry from GiftRegistryManager",e);
		} catch (final RepositoryException rex) {
			emailSuccess = false;
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10124+" RepositoryException of sendEmailRegistry from GiftRegistryManager",rex);
		}

		this.logDebug("GiftRegistryManager.sendEmailRegistry() method ends");

		return emailSuccess;
	}

	/**
	 * This method is used to update coregistrant by invoking a updateRegistry
	 * webservice.
	 *
	 * @param pRegistryVO
	 *            the registry vo
	 * @return the registry res vo
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public RegistryResVO updateRegistry(final RegistryVO pRegistryVO)
			throws BBBBusinessException, BBBSystemException {

		this.logDebug("GiftRegistryManager.updateRegistry() method starts...");
		RegistryResVO updateRegistryResVO = null;
		boolean regItemsWSCall = false;
		List<String> regItemsWSCallFlag = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall");

		if (!BBBUtility.isListEmpty(regItemsWSCallFlag)) {
			regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag.get(0));
		}

		if (regItemsWSCall) {
			this.logDebug("GiftRegistryManager.updateRegistry() method in web-service mode[regItemsWSCall]: " + regItemsWSCall);
			updateRegistryResVO = this.getGiftRegistryTools().updateRegistry(pRegistryVO);
		} else {
			this.logDebug("GiftRegistryManager.updateRegistry() method in PROC mode[regItemsWSCall]: " + regItemsWSCall);
				/***
				 * Step 1: Do initial validation for all the input variables for
				 * invalid characters
				 */
				ErrorStatus errorStatus = validateInputForInvalidCharectars(pRegistryVO);
				if (errorStatus != null && errorStatus.isErrorExists()) {
					updateRegistryResVO = new RegistryResVO();
					ServiceErrorVO errorVO = new ServiceErrorVO();
					errorVO.setErrorExists(true);
					errorVO.setErrorId(errorStatus.getErrorId());
					updateRegistryResVO.setServiceErrorVO(errorVO);
				} else {
					/***
				 * Validate all the input values and post successful validation
				 * invoke tools method to call stored procedures Step 2:
				 * populate RegistryHeaderVO
					 * 
					 */
					RegistryHeaderVO registryHeaderVO = populateRegHeaderVO(pRegistryVO);
					/**
					 * Step 3: populate RegistryBabyVO, if registry type is baby
					 * */
					RegistryBabyVO registryBabyVO = populateRegBabyVO(pRegistryVO);

					/***
					 * Step 4: populate RegNamesVO for PrimaryRegistrant
					 */
					List<RegNamesVO> registrantsVOs = new ArrayList<RegNamesVO>();
					populateRegNamesVO(pRegistryVO, pRegistryVO.getPrimaryRegistrant(), BBBGiftRegistryConstants.RE, registrantsVOs);
					/***
					* Step 5: populate Co-registrantVO, if any input value provided
					* for co-registrant
					***/
					//if (!getGiftRegUtils().isCoRegistrantEmpty(pRegistryVO)) {
						populateRegNamesVO(pRegistryVO, pRegistryVO.getCoRegistrant(), BBBGiftRegistryConstants.CO, registrantsVOs);
					//}
					/***
					 * Step 6: populate RegNamesVO for Shipping Address
					 */
					List<RegNamesVO> shippingVOs = new ArrayList<RegNamesVO>();
					if (pRegistryVO.getShipping() != null && pRegistryVO.getShipping().getShippingAddress() != null) {
						populateShippingVO(pRegistryVO, pRegistryVO.getShipping().getShippingAddress(), BBBGiftRegistryConstants.SH, shippingVOs);
					}

					/**
					 * Step 7: populate RegNamesVO for  Future Shipping Address, if availed.
					 */
					if (pRegistryVO.getShipping() != null
							&& pRegistryVO.getShipping().getFutureshippingAddress() != null
							&& !getGiftRegUtils().isFutureShippingEmpty(pRegistryVO.getShipping())) {
						populateShippingVO(pRegistryVO, pRegistryVO.getShipping().getFutureshippingAddress(), BBBGiftRegistryConstants.FU, shippingVOs);
					}

					/***
					 * Step 8: populate RegistryPrefStoreVO
					 */
					RegistryPrefStoreVO registryPrefStoreVO = populateRegPrefStoreVO(pRegistryVO);

					/**
					 * Step 9: validate all registry fields & prefStore inputs
					 */
				boolean validationResult = this.validateInputForCreateOrUpdateRegistry(pRegistryVO, registryHeaderVO, registrantsVOs, shippingVOs,
								registryBabyVO, registryPrefStoreVO);
					if (validationResult && registryHeaderVO != null && !BBBUtility.isEmpty((registryHeaderVO.getRegNum()))) {
						/***
					 * Step 10: Validate existing registry id then set eventType
					 * in registryHeaderVO
						 */
						RegistryResVO registryInfo = this.getGiftRegistryTools().getRegistryInfoFromEcomAdmin(registryHeaderVO.getRegNum(), registryHeaderVO.getSiteId());
					if (registryInfo != null && registryInfo.getRegistrySummaryVO() != null && !BBBUtility.isEmpty(registryInfo.getRegistrySummaryVO().getEventType())) {
							registryHeaderVO.setEventType(registryInfo.getRegistrySummaryVO().getEventType());
						}
						/***
					 * Step 11: invoke updateRegistry to execute procedures to
					 * update the registry
						 */
					updateRegistryResVO = this.getGiftRegistryTools().updateRegistry(pRegistryVO, registryHeaderVO, registrantsVOs, shippingVOs,
									registryBabyVO, registryPrefStoreVO);
					} else {
						updateRegistryResVO = new RegistryResVO();
						ServiceErrorVO errorVO = new ServiceErrorVO();
						errorVO.setErrorExists(true);
						errorVO.setErrorId(200);
						updateRegistryResVO.setServiceErrorVO(errorVO);
					}
				}
			}

		return updateRegistryResVO;
	}

	/**
	 * This method is used to update coregistrant by invoking a
	 * updateRegistryWithAtgInfo webservice.
	 *
	 * @param pProfileSyncRequestVO
	 *            the profile sync request vo
	 * @return the profile sync response vo
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public ProfileSyncResponseVO updateRegistryWithAtgInfo(
            final ProfileSyncRequestVO pProfileSyncRequestVO)
                         throws BBBBusinessException, BBBSystemException {

     boolean regItemsWSCall = false;
     ProfileSyncResponseVO profileSyncResponseVO = null;

     try
     {
            List<String> regItemsWSCallFlag = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall");
            if (!BBBUtility.isListEmpty(regItemsWSCallFlag))
            {
                  regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag.get(0));
            }
            if (regItemsWSCall)
            {
                  // WS Call | calling the GiftRegistryTool's getRegistryInfo method
                  profileSyncResponseVO = this.getGiftRegistryTools()
                                .updateRegistryWithAtgInfo(pProfileSyncRequestVO , regItemsWSCall);
            }
            // If Registry flag is false , information will fetch from the DB
            else
            {
                  if(BBBUtility.isEmpty(pProfileSyncRequestVO.getProfileId()))
                  {
                         ErrorStatus errorStatus = new ErrorStatus();
                         errorStatus.setErrorExists(true);
                         errorStatus.setErrorMessage(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_EMPTY_PROFILE_ID, "EN", null));
                         errorStatus.setDisplayMessage(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_EMPTY_PROFILE_ID, "EN", null));
                         errorStatus.setErrorId(200);
                         errorStatus.setValidationErrors(null);
                         profileSyncResponseVO = new ProfileSyncResponseVO();
                         profileSyncResponseVO.setErrorStatus(errorStatus);
                         return profileSyncResponseVO;
                  }
                  String lastName = pProfileSyncRequestVO.getLastName();
                  String firstName = pProfileSyncRequestVO.getFirstName();
                  String phone1 = pProfileSyncRequestVO.getPhoneNumber();
                  String mobileNo = pProfileSyncRequestVO.getMobileNumber();
                  String email = pProfileSyncRequestVO.getEmailAddress();
                  List<ValidationError> lValErrors = new ArrayList<ValidationError>();
                  ValidationError valError = new ValidationError();
                  if(getGiftRegUtils().hasInvalidChars(lastName))
                  {
                         valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_LAST_NAME);
                         valError.setValue(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_LST_NM_INVALID, "EN", null));
                         lValErrors.add(valError);
                  }
                  //utility
                  else if(BBBUtility.isEmpty(lastName))
                  {
                         valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_LAST_NAME);
                         valError.setValue(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_LST_NM_EMPTY, "EN", null));
                         lValErrors.add(valError);
                  }
                  else if(!BBBUtility.isEmpty(lastName) && lastName.length() > 30)
                  {
                         valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_LAST_NAME);
                         valError.setValue(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_LST_NM_TOO_LONG, "EN", null));
                         lValErrors.add(valError);
                  }

                  if(getGiftRegUtils().hasInvalidChars(firstName))
                  {
                         valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_FIRST_NAME);
                         valError.setValue(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_FST_NM_INVALID, "EN", null));
                         lValErrors.add(valError);
                  }
                  else if(BBBUtility.isEmpty(firstName))
                  {
                         valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_FIRST_NAME);
                         valError.setValue(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_FST_NM_EMPTY, "EN", null));
                         lValErrors.add(valError);
                  }
                  else if(!BBBUtility.isEmpty(firstName) && firstName.length() > 30)
                  {
                         valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_FIRST_NAME);
                         valError.setValue(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_FST_NM_TOO_LONG, "EN", null));
                         lValErrors.add(valError);
                  }

                  if(!BBBUtility.isEmpty(phone1) && getGiftRegUtils().hasInvalidChars(phone1))
                  {
                         valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_PHONE1);
                         valError.setValue(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_PHONE_INVALID_CHARS, "EN", null));
                         lValErrors.add(valError);  
                  }
                  else if(!BBBUtility.isEmpty(phone1) && !(getGiftRegUtils().isValidPhone(phone1)))
                  {
                         valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_PHONE1);
                         valError.setValue(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_PHONE_INVALID, "EN", null));
                         lValErrors.add(valError);
                  }
                  if(!BBBUtility.isEmpty(mobileNo) && getGiftRegUtils().hasInvalidChars(mobileNo))
                  {
                         valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_PHONE2);
                         valError.setValue(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_PHONE_INVALID_CHARS, "EN", null));
                         lValErrors.add(valError);  
                  }
                  else if(!BBBUtility.isEmpty(mobileNo) && !(getGiftRegUtils().isValidPhone(mobileNo)))
                  {
                         valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_PHONE2);
                         valError.setValue(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_PHONE_INVALID, "EN", null));
                         lValErrors.add(valError);
                  }

                  if(getGiftRegUtils().hasInvalidChars(email))
                  {
                         valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_EMAIL);
                         valError.setValue(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_EMAIL_INVALID_CHARS, "EN", null));
                         lValErrors.add(valError);
                  }
                  else if(BBBUtility.isEmpty(email))
                  {
                         valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_EMAIL);
                         valError.setValue(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_EMAIL_EMPTY, "EN", null));
                         lValErrors.add(valError);
                  }

                  else if(!BBBUtility.isValidEmail(email))
                  {
                         valError.setKey(BBBGiftRegistryConstants.ERROR_KEY_EMAIL);
                         valError.setValue(getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_EMAIL_INVALID, "EN", null));
                         lValErrors.add(valError);
                  }

                  if(lValErrors.size() > 0 )
                  {
                         ErrorStatus errorStatus = new ErrorStatus();
                         errorStatus.setErrorExists(true);
                         errorStatus.setErrorMessage(null);
                         errorStatus.setDisplayMessage(null);
                         errorStatus.setErrorId(200);
                         errorStatus.setValidationErrors(lValErrors);
                         profileSyncResponseVO = new ProfileSyncResponseVO();
                         profileSyncResponseVO.setErrorStatus(errorStatus);
                  }

                  else
                  {
                         profileSyncResponseVO = this.getGiftRegistryTools()
                                       .updateRegistryWithAtgInfo(pProfileSyncRequestVO , regItemsWSCall);
                  }
            }
     }
     catch(Exception ex)
     {
            profileSyncResponseVO = new ProfileSyncResponseVO();
            ErrorStatus errorStatus = (ErrorStatus) getGiftRegUtils().logAndFormatError("UpdateRegistryWithAtgInfo", null , BBBGiftRegistryConstants.ERROR_STATUS , ex , pProfileSyncRequestVO.getProfileId(),pProfileSyncRequestVO.getEmailAddress(),pProfileSyncRequestVO.getLastName(),
                         pProfileSyncRequestVO.getFirstName(),pProfileSyncRequestVO.getPhoneNumber(),pProfileSyncRequestVO.getMobileNumber());
            profileSyncResponseVO.setErrorStatus(errorStatus);
     }
     return profileSyncResponseVO;
}

	
	/**
	 * Sets the shipping billing addr.
	 *
	 * @param pRegistryVO
	 *            the registry vo
	 * @param shippingAddress
	 *            the shipping address
	 * @param futureShippingAddr
	 *            the future shipping addr
	 * @param pProfile
	 *            the profile
	 * @param registrantAddressFromWS
	 *            the registrant address from ws
	 * @param shippingAddressFromWS
	 *            the shipping address from ws
	 * @param futureShippingAddressFromWS
	 *            the future shipping address from ws
	 * @return the registry vo
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public RegistryVO setShippingBillingAddr(final RegistryVO pRegistryVO,
			final String shippingAddress, final String futureShippingAddr,
			final Profile pProfile, final AddressVO registrantAddressFromWS,
			final AddressVO shippingAddressFromWS,
			final AddressVO futureShippingAddressFromWS) throws BBBSystemException,
			BBBBusinessException {
		this.logDebug("GiftRegistryManager.setShippingBillingAddr() method start");

		BBBAddressVO bbbAddressVO = null;
		AddressVO addressVO = null;

		final String primaryPhoneNum = pRegistryVO.getPrimaryRegistrant()
				.getPrimaryPhone();

		/** Primary registrant address section */
		if ("newPrimaryRegAddress".equalsIgnoreCase(pRegistryVO
				.getPrimaryRegistrant().getAddressSelected())) {

			// already bind with
			// pRegistryVO.getPrimaryRegistrant().setContactAddress
			pRegistryVO.getPrimaryRegistrant().getContactAddress()
					.setPrimaryPhone(primaryPhoneNum);

		} else if ("oldRegistrantAddressFromWS".equalsIgnoreCase(pRegistryVO
				.getPrimaryRegistrant().getAddressSelected())) {
			// Old address from webservice to addressbook
			if (registrantAddressFromWS != null) {

				registrantAddressFromWS.setPrimaryPhone(primaryPhoneNum);
				pRegistryVO.getPrimaryRegistrant().setContactAddress(
						registrantAddressFromWS);

			}

		} else {
			// pick from addressbook
			final String addressBookRepositoryId = pRegistryVO.getPrimaryRegistrant()
					.getAddressSelected();
			if (!StringUtils.isEmpty(addressBookRepositoryId)) {
				bbbAddressVO = this.getBbbAddressAPI().fetchAddress(pProfile,
						pRegistryVO.getSiteId(), addressBookRepositoryId);
				addressVO = this.addressMapping(pRegistryVO.getPrimaryRegistrant()
						.getContactAddress(), bbbAddressVO);
				addressVO.setPrimaryPhone(primaryPhoneNum);

				pRegistryVO.getPrimaryRegistrant().setContactAddress(addressVO);
			}
		}

		/** Shipping Address section */
		if ("shipAdrressSameAsRegistrant".equalsIgnoreCase(shippingAddress)) {

			pRegistryVO.getShipping().setShippingAddress(
					pRegistryVO.getPrimaryRegistrant().getContactAddress());
			pRegistryVO.getShipping().getShippingAddress()
					.setPrimaryPhone(primaryPhoneNum);

		} else if ("oldShippingAddressFromWS".equalsIgnoreCase(shippingAddress)) {

			if (shippingAddressFromWS != null) {
				shippingAddressFromWS.setPrimaryPhone(primaryPhoneNum);
				pRegistryVO.getShipping().setShippingAddress(
						shippingAddressFromWS);
			}

		} else if ("newShippingAddress".equalsIgnoreCase(shippingAddress)) {
			// already bind with pRegistryVO.getShipping().setShippingAddress
			if ((pRegistryVO.getShipping().getShippingAddress()
					.getAddressLine1() != null)
					&& !pRegistryVO.getShipping().getShippingAddress()
							.getAddressLine1().trim().equalsIgnoreCase("")) {

				pRegistryVO.getShipping().getShippingAddress()
						.setPrimaryPhone(primaryPhoneNum);
			}

		} else {

			// pick the selected addresss from addressbook
			final String addressBookRepositoryId = shippingAddress;
			if(!StringUtils.isEmpty(shippingAddress)){
			bbbAddressVO = this.getBbbAddressAPI().fetchAddress(pProfile,
					pRegistryVO.getSiteId(), addressBookRepositoryId);
			addressVO = this.addressMapping(pRegistryVO.getShipping()
					.getShippingAddress(), bbbAddressVO);
			addressVO.setPrimaryPhone(primaryPhoneNum);
			pRegistryVO.getShipping().setShippingAddress(addressVO);
			}
		}

		/** Future shipping Address section */
		if ("futShipAdrressSameAsRegistrant"
				.equalsIgnoreCase(futureShippingAddr)) {
			// future shipping address same as registrant contact address
			pRegistryVO.getShipping().setFutureshippingAddress(
					pRegistryVO.getPrimaryRegistrant().getContactAddress());
			pRegistryVO.getShipping().getFutureshippingAddress()
					.setPrimaryPhone(primaryPhoneNum);

		} else if ("oldFutShippingAddressFromWS"
				.equalsIgnoreCase(futureShippingAddr)) {

			if (futureShippingAddressFromWS != null) {
				futureShippingAddressFromWS.setPrimaryPhone(primaryPhoneNum);
				pRegistryVO.getShipping().setFutureshippingAddress(
						futureShippingAddressFromWS);
			}

		} else if ("newFutureShippingAddress"
				.equalsIgnoreCase(futureShippingAddr)) {
			// already bind with
			// pRegistryVO.getShipping().setFutureshippingAddress
			if ((pRegistryVO.getShipping().getFutureshippingAddress()
					.getAddressLine1() != null)
					&& !pRegistryVO.getShipping().getFutureshippingAddress()
							.getAddressLine1().trim().equalsIgnoreCase("")) {

				pRegistryVO.getShipping().getFutureshippingAddress()
						.setPrimaryPhone(primaryPhoneNum);
			}

		} else if (!StringUtils.isEmpty(futureShippingAddr)) {
			// pick the selected addresss from addressbook
			final String addressBookRepositoryId = futureShippingAddr;

			bbbAddressVO = this.getBbbAddressAPI().fetchAddress(pProfile,
					pRegistryVO.getSiteId(), addressBookRepositoryId);
			addressVO = this.addressMapping(pRegistryVO.getShipping()
					.getFutureshippingAddress(), bbbAddressVO);
			addressVO.setPrimaryPhone(primaryPhoneNum);
			pRegistryVO.getShipping().setFutureshippingAddress(addressVO);
		}
		this.logDebug("GiftRegistryManager.setShippingBillingAddr() method ends");

		return pRegistryVO;
	}

	/**
	 * This method is used to map address VO.
	 *
	 * @param pAddressCO
	 *            the address co
	 * @param bbbAddressVO
	 *            the bbb address vo
	 * @return the address vo
	 */
	private AddressVO addressMapping(final AddressVO pAddressCO,
			final BBBAddressVO bbbAddressVO) {

		this.logDebug("GiftRegistryManager.addressVOMap() method start");

		if (!BBBUtility.isEmpty(bbbAddressVO.getFirstName())) {
			pAddressCO.setFirstName(bbbAddressVO.getFirstName());
		}

		if (!BBBUtility.isEmpty(bbbAddressVO.getLastName())) {
			pAddressCO.setLastName(bbbAddressVO.getLastName());
		}

		if (!BBBUtility.isEmpty(bbbAddressVO.getAddress1())) {
			pAddressCO.setAddressLine1(bbbAddressVO.getAddress1());
		}

		if (!BBBUtility.isEmpty(bbbAddressVO.getAddress2())) {
			pAddressCO.setAddressLine2(bbbAddressVO.getAddress2());
		}

		if (!BBBUtility.isEmpty(bbbAddressVO.getCity())) {
			pAddressCO.setCity(bbbAddressVO.getCity());
		}

		if (!BBBUtility.isEmpty(bbbAddressVO.getState())) {
			pAddressCO.setState(bbbAddressVO.getState());
		}

		if (!BBBUtility.isEmpty(bbbAddressVO.getPostalCode())) {
			pAddressCO.setZip(bbbAddressVO.getPostalCode());
		}

		this.logDebug("GiftRegistryManager.addressVOMap() method ends");
		return pAddressCO;
	}

	/**
	 * Gets the link co reg profile to reg service name.
	 *
	 * @return the linkCoRegProfileToRegServiceName
	 */
	public String getLinkCoRegProfileToRegServiceName() {
		return this.mLinkCoRegProfileToRegServiceName;
	}

	/**
	 * Sets the link co reg profile to reg service name.
	 *
	 * @param linkCoRegProfileToRegServiceName
	 *            the linkCoRegProfileToRegServiceName to set
	 */
	public void setLinkCoRegProfileToRegServiceName(
			final String linkCoRegProfileToRegServiceName) {
		this.mLinkCoRegProfileToRegServiceName = linkCoRegProfileToRegServiceName;
	}

	/**
	 * This method is used to associate a migrated registry with the current
	 * user as either Primary Registrant or Co Registrant.
	 *
	 * @param registryVO
	 *            the registry vo
	 * @param isPrimaryRegistrant
	 *            the is primary registrant
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws RepositoryException
	 *             the repository exception @ author - ikhan2
	 */
	public void linkMigratedRegistry(final RegistryVO registryVO,
			final boolean isPrimaryRegistrant) throws BBBSystemException,
			RepositoryException {
		this.logDebug("GiftRegistryManager.linkMigratedRegistry() method start");

		MutableRepositoryItem pProfileItem = null;
		final boolean userAlreadyExistSameSite = this.getTools()
				.isDuplicateEmailAddress(
						registryVO.getRegistrantVO().getEmail(),
						registryVO.getSiteId());

		if (userAlreadyExistSameSite) {

			pProfileItem = (MutableRepositoryItem) this.getTools()
					.getItemFromEmail(registryVO.getRegistrantVO().getEmail());

			if (isPrimaryRegistrant) {
				// If WebService has linked the registry to user as
				// Primary-Registrant
				// ADD registrant entry to gift registry repository with owner
				// profile
				this.getGiftRegistryTools().addORUpdateRegistry(registryVO,
						pProfileItem, null);
			} else {
				// ADD registrant entry to gift registry repository with coOwner
				// profile
				try {
					final RepositoryItem[] grRepositoryItems = this.getGiftRegistryTools()
							.fetchGiftRepositoryItem(registryVO.getSiteId(),
									registryVO.getRegistryId());
					if ((grRepositoryItems != null)
							&& (grRepositoryItems.length > 0)) {
						this.getGiftRegistryTools().addORUpdateRegistry(registryVO,
								null, pProfileItem);
					}
					else
					{
						throw new BBBBusinessException("Registry is not yet migrated. Hence cannot attach a co-reg");
					}
				} catch (final BBBBusinessException e) {
					this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10125+" BBBBusinessException [link registry] from linkMigratedRegistry of GiftRegistryManager",e);
					throw new BBBSystemException(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10125,
							"Error occured while feching registry",e);
				}

			}

		} else {
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10126+" Profile not found [link registry] from linkMigratedRegistry of GiftRegistryManager");
			throw new BBBSystemException(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10126,"Profile not found");
		}
		this.logDebug("GiftRegistryManager.linkMigratedRegistry() method ends");

	}


	public RepositoryItem[] fetchUserRegistries(final String siteId, final String profileId)
			throws RepositoryException, BBBSystemException, BBBBusinessException{

		return this.getGiftRegistryTools().fetchUserRegistries(siteId, profileId, true);
	}

	/**
	 * getting latest registry id
	 * @param profile
	 * @return
	 */
	public String getCommaSaperatedRegistryIds(final Profile profile, final String pSiteId)
	{
		String registryIds="";
		RepositoryItem[] registryIdRepItems;
		try {
			registryIdRepItems = this.fetchUserRegistries(pSiteId, profile.getRepositoryId());

			if(registryIdRepItems != null){
			for (final RepositoryItem registryIdRepItem : registryIdRepItems) {
				final String registryId = registryIdRepItem.getRepositoryId();
				registryIds+=registryId+",";
			}
		}
		} catch (final RepositoryException e) {
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10127+" RepositoryException [Fetching registryid] from getCommaSaperatedRegistryIds of  GiftRegistryManager",e);
		} catch (final BBBSystemException e) {
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10128+" BBBSystemException [Fetching registryid] from getCommaSaperatedRegistryIds of  GiftRegistryManager",e);
		} catch (final BBBBusinessException e) {
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10129+" BBBBusinessException [Fetching registryid] from getCommaSaperatedRegistryIds of  GiftRegistryManager",e);
		}

		if(!StringUtils.isEmpty(registryIds))
		{
			registryIds=registryIds.substring(0, registryIds.lastIndexOf(","));
		}
		return registryIds;
	}

	private static final String SESSION_BEAN = "/com/bbb/profile/session/SessionBean";
	private static final String GuestRegistryItemsListVO = null;

	/**
	 * This method check is profile contains pRegistryId
	 *
	 * Return true if profile contains pRegistryId otherwise return false.
	 *
	 * @param pProfile
	 *            the profile
	 * @param pRegistryId
	 *            the registry id
	 * @return true, if is user own registry
	 */
	public static boolean isUserOwnRegistry(final Profile pProfile, final String pRegistryId, final DynamoHttpServletRequest pRequest) {

		boolean success = false;

		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(SESSION_BEAN);
		final HashMap sessionMap = sessionBean.getValues();
		final List<String> pListUserRegIds = (List) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_LIST);

		if (pListUserRegIds != null) {
			// check if the pRegistryId in the users' registryIDs set
			if(pListUserRegIds.contains(pRegistryId)){
				success = true;
			}
		}
		return success;
	}

	/**
	 * Get the GetRegistryStatusesByProfileId webservice name
	 * @return String GetRegistryStatusesByProfileId webservice Name
	 *
	 */
	public String getRegistryStatuByProfileIdServiceName() {
		return this.registryStatuByProfileIdServiceName;
	}

	/**
	 * set the GetRegistryStatusesByProfileId webservice name
	 * @param String GetRegistryStatusesByProfileId webservice Name
	 *
	 */
	public void setRegistryStatuByProfileIdServiceName(
			final String registryStatuByProfileIdServiceName) {
		this.registryStatuByProfileIdServiceName = registryStatuByProfileIdServiceName;
	}

	/**
	 * This method will clear the registries from BBBSessionBean
	 *
	 * @param pRequest
	 */
	public void invalidateSessionRegistriesData(final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryManager.invalidateSessionRegistriesData() method started");
    	// Invalidate registrylist and summaryVO in the session
    	final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
    			.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);

		final HashMap<String, Object> values = sessionBean.getValues();
    	values.put(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST, null);
    	values.put(BBBGiftRegistryConstants.USER_REGISTRIES_LIST, null);
    	values.put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, null);
    	this.logDebug("GiftRegistryManager.invalidateSessionRegistriesData() method end");
	}

	public RegistrySummaryListVO searchRegistryByCriteria(final Map<String,String> inputMap)
			throws BBBBusinessException, BBBSystemException {
		this.logDebug(" GiftRegistryManager searchRegistryByCriteria(Map<String,String> inputMap) - start");
         RegistrySummaryListVO registrySummaryListVO= new RegistrySummaryListVO() ;
		List<RegistrySummaryVO> pRegistryVO = null;
		int pageNo = BBBGiftRegistryConstants.DEFAULT_TAB;
		int perPage = BBBGiftRegistryConstants.DEFAULT_PAGE_SIZE;
		final RegistrySearchVO registrySearchVO = new RegistrySearchVO();

		final String siteId = getSiteId();
		final Map<String,String> inputDataMap = inputMap;
		if(!BBBUtility.isEmpty(inputDataMap.get(REGISTRY_ID))){
			registrySearchVO.setRegistryId(inputDataMap.get(REGISTRY_ID));
		} else {
			if(!BBBUtility.isValidFirstName(inputDataMap.get(FIRST_NAME))){
				registrySummaryListVO.setErrorExist(true);
				registrySummaryListVO.setErrorMessage("Either FirstName or Last Name is incorrect");
				logError(registrySummaryListVO.getErrorMessage());
				return(registrySummaryListVO);
				//throw new BBBBusinessException(BBBCatalogErrorCodes.REGISTRY_SEARCH_EXCEPTION_BUSSINESS_EXCEPTION, "Enter either First Name & Last Name OR Registry Id while Search Registry by Criteria.");
			}
			if(!BBBUtility.isValidLastName(inputDataMap.get(LAST_NAME))){
				registrySummaryListVO.setErrorExist(true);
				registrySummaryListVO.setErrorMessage("Either First Name or Last Name is Incorrect");
				logError(registrySummaryListVO.getErrorMessage());
				return(registrySummaryListVO);
			//	throw new BBBBusinessException(BBBCatalogErrorCodes.REGISTRY_SEARCH_EXCEPTION_BUSSINESS_EXCEPTION, "Enter either First Name & Last Name OR Registry Id while Search Registry by Criteria.");
			}
			registrySearchVO.setFirstName(inputDataMap.get(FIRST_NAME) != null ? inputDataMap.get(FIRST_NAME).trim() : inputDataMap.get(FIRST_NAME));
			registrySearchVO.setLastName(inputDataMap.get(LAST_NAME) != null ? inputDataMap.get(LAST_NAME).trim() : inputDataMap.get(LAST_NAME));
		}	
	/*	else{
			if(BBBUtility.isValidFirstName(inputDataMap.get(FIRST_NAME))){
				if(BBBUtility.isValidLastName(inputDataMap.get(LAST_NAME))){
					registrySearchVO.setFirstName(inputDataMap.get(FIRST_NAME));
					registrySearchVO.setLastName(inputDataMap.get(LAST_NAME));
				}
				else{
					throw new BBBBusinessException(BBBCatalogErrorCodes.REGISTRY_SEARCH_EXCEPTION_BUSSINESS_EXCEPTION, "Enter either First Name & Last Name OR Registry Id while Search Registry by Criteria.");
				}
			} 
			else{
				throw new BBBBusinessException(BBBCatalogErrorCodes.REGISTRY_SEARCH_EXCEPTION_BUSSINESS_EXCEPTION, "Enter either First Name & Last Name OR Registry Id while Search Registry by Criteria.");
			}
		} */
		if((inputDataMap.get(EMAIL) != null) && !"".equals(inputDataMap.get(EMAIL))){
			registrySearchVO.setEmail(inputDataMap.get(EMAIL));
		}

		if((inputDataMap.get(STATE) != null) && !"".equals(inputDataMap.get(STATE))){
			registrySearchVO.setState(inputDataMap.get(STATE));
		}
		if((inputDataMap.get(EVENT) != null) && !"".equals(inputDataMap.get(EVENT))){
			registrySearchVO.setEvent(inputDataMap.get(EVENT));
		}
		registrySearchVO.setServiceName(this.getRegistrySearchServiceName());

		if (null != this.getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId)) {
					registrySearchVO.setSiteId(this.getBbbCatalogTools()
									.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).get(0));
		}

		registrySearchVO.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
		registrySearchVO.setReturnLeagacyRegistries(false);
		registrySearchVO.setGiftGiver(true);

		final String pageNum = inputDataMap.get(BBBGiftRegistryConstants.PAGE_NO);
		if ((pageNum != null) && !"".equals(pageNum)) {
			pageNo = Integer.parseInt(pageNum);
		}

		final String perPageNum = inputDataMap.get(BBBGiftRegistryConstants.PER_PAGE);
		if ((perPageNum != null) && !"".equals(perPageNum)) {
			perPage = Integer.parseInt(perPageNum);
		}

		final String sortOrder = inputDataMap.get(BBBGiftRegistryConstants.SORTING_ORDER);
		if(sortOrder == null){
			registrySearchVO.setSortSeqOrder(DEFAULT_SORT_ORDER);
		}else if(!"".equalsIgnoreCase(sortOrder)){
			registrySearchVO.setSortSeqOrder(sortOrder);
		}

		final String sortPassString = inputDataMap.get(BBBGiftRegistryConstants.SORT_PASS_STRING);
		if (sortPassString == null) {
			registrySearchVO.setSort("NAME");
		} else if (!"".equalsIgnoreCase(sortPassString)) {
			registrySearchVO.setSort(sortPassString);
		}

		final int forStart = ((pageNo - 1) * perPage);

		registrySearchVO.setStartIdx(forStart);
		registrySearchVO.setBlkSize(perPage);

		RegSearchResVO searchResponseVO = null;
		try{
		searchResponseVO = this.searchRegistries(registrySearchVO, siteId);
		}catch(BBBSystemException e){
			registrySummaryListVO.setErrorExist(true);
			registrySummaryListVO.setErrorMessage(BBBCoreErrorConstants.OOPS_PAGE+" while search registry");
			logError(registrySummaryListVO.getErrorMessage());
			this.logDebug(" GiftRegistryManager searchRegistryByCriteria(Map<String,String> inputMap) - ends.");
			return(registrySummaryListVO);
		}
		
		if (searchResponseVO != null){
			if(searchResponseVO.getServiceErrorVO().isErrorExists()){
				if (!BBBUtility.isEmpty(searchResponseVO.getServiceErrorVO()
						.getErrorDisplayMessage())){
				  this.logError(searchResponseVO.getServiceErrorVO()
							.getErrorDisplayMessage());
				}
				registrySummaryListVO.setErrorExist(true);
				registrySummaryListVO.setErrorMessage(BBBCoreErrorConstants.OOPS_PAGE+" while search registry");
				logError(registrySummaryListVO.getErrorMessage());
				this.logDebug(" GiftRegistryManager searchRegistryByCriteria(Map<String,String> inputMap) - ends");
				return(registrySummaryListVO);
				// throw new BBBSystemException(BBBCatalogErrorCodes.REGISTRY_SEARCH_EXCEPTION_SYSTEM_EXCEPTION, "System Exception raised while Search Registry by Criteria.");
			}
			pRegistryVO = searchResponseVO.getListRegistrySummaryVO();
			pRegistryVO = changeToRestOutput(pRegistryVO);
		}
        registrySummaryListVO.setAtgResponse(pRegistryVO);
        this.logDebug(" GiftRegistryManager searchRegistryByCriteria(Map<String,String> inputMap) - ends");
		return registrySummaryListVO;
	}

	/***
	 * customize the response VO
	 * @param registrySummaryVOs
	 * @return
	 */
	private static List<RegistrySummaryVO>changeToRestOutput(final List<RegistrySummaryVO> registrySummaryVOs)
	{
		final List<RegistrySummaryVO> summaryVOs=new ArrayList<RegistrySummaryVO>();
		if (!BBBUtility.isListEmpty(registrySummaryVOs) ) {
			for (final RegistrySummaryVO registrySummaryVO : registrySummaryVOs) // or
			{
				final String regFullName = registrySummaryVO
						.getPrimaryRegistrantFirstName();
				if ((null != regFullName) && !BBBUtility.isEmpty(regFullName)) {
					final String[] fullName = regFullName.split("\\s+");
					registrySummaryVO
							.setPrimaryRegistrantFirstName(fullName[0]);
					if (fullName.length > 1) {
						registrySummaryVO
								.setPrimaryRegistrantLastName(fullName[1]);
					} else {
						registrySummaryVO.setPrimaryRegistrantLastName("");
					}
					registrySummaryVO.setPrimaryRegistrantFullName(regFullName);
				}
				final String regCoFullName = registrySummaryVO
						.getCoRegistrantFirstName();
				if ((null != regCoFullName)
						&& !BBBUtility.isEmpty(regCoFullName)) {
					final String[] coFullName = regCoFullName.split("\\s+");
					registrySummaryVO.setCoRegistrantFirstName(coFullName[0]);
					if (coFullName.length > 1) {
						registrySummaryVO
								.setCoRegistrantLastName(coFullName[1]);
					} else {
						registrySummaryVO.setCoRegistrantLastName("");
					}
					registrySummaryVO.setCoRegistrantFullName(regCoFullName);
				}
				summaryVOs.add(registrySummaryVO);
			}
		}
		return summaryVOs;
	}
	/**
	 * This method will call the GetRegistryStatusesByProfileId webservice and
	 * will call syncRegistryStatuses method to sync registry statuses with webservice response data.
	 *
	 * @param profile Profile
	 * @param siteId site Id
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	public void updateProfileRegistriesStatus(final Profile profile, final String siteId) throws BBBBusinessException, BBBSystemException{

		this.logDebug("GiftRegistryManager.updateProfileRegistriesStatus() method started");
		List<RegistrySkinnyVO> registrySkinnyVOList = null;
		try {
			syncRegistryStatusWithECOM(profile, siteId);
			registrySkinnyVOList = this.getUserRegistryList(profile, siteId);
			if (registrySkinnyVOList != null) {
				logDebug("Putting Registry Skinny VO List into session. Size :: " + registrySkinnyVOList.size() + "List Content :: " + registrySkinnyVOList );
			}
			BBBSessionBean sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(SESSION_BEAN);
			sessionBean.getValues().put("registrySkinnyVOList", registrySkinnyVOList);
		} catch (final RepositoryException e) {
			throw new BBBSystemException(e.getMessage());
		}
	        
		this.logDebug("GiftRegistryManager.updateProfileRegistriesStatus() method end");
	}

	/**
	 * @param profile
	 * @param siteId
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws RepositoryException 
	 */
	public void syncRegistryStatusWithECOM(final Profile profile, final String siteId) throws BBBSystemException,	BBBBusinessException, RepositoryException {
		
		this.logDebug("GiftRegistryManager.syncRegistryStatusWithECOM() method started");
		final RegistryStatusVO registryStatusVO = new RegistryStatusVO();
		registryStatusVO.setProfileId(profile.getRepositoryId());
		registryStatusVO.setSiteId(this.getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,	siteId).get(0));
		registryStatusVO.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
						BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
		RegStatusesResVO regStatusesResVO = new RegStatusesResVO();
		RepositoryItem[] userRegistries = getGiftRegistryTools().fetchUserRegistries(siteId, profile.getRepositoryId(), false);
		final List<RegistrySkinnyVO> registrySkinnyVOList = new ArrayList<RegistrySkinnyVO>();
		if (!BBBUtility.isArrayEmpty(userRegistries)) {
			for (RepositoryItem userRegistry : userRegistries) {
				final RegistrySkinnyVO skinnyVO = new RegistrySkinnyVO();
				skinnyVO.setRegistryId(userRegistry.getRepositoryId());
				skinnyVO.setStatus((String) userRegistry.getPropertyValue("registryStatus"));
				registrySkinnyVOList.add(skinnyVO);
			}
		}
		boolean regItemsWSCall = false;
		List<String> regItemsWSCallFlag = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall");
		if (!BBBUtility.isListEmpty(regItemsWSCallFlag)) {
			regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag.get(0));
		}
		if (regItemsWSCall) {
			// Web Service Call
			registryStatusVO.setServiceName(this.getRegistryStatuByProfileIdServiceName());
			regStatusesResVO = this.getGiftRegistryTools().getRegistriesStatus(registryStatusVO);
			this.logDebug("getGiftRegistryTools().getRegistriesStatus -MSG= regStatusesResVO  from web service :" + regStatusesResVO);
		} else {
			// Stored Procedure Call
			regStatusesResVO = this.getRegistryStatusesByProfileId(registryStatusVO);
			this.logDebug("getGiftRegistryTools().getRegistryStatusFromDB MSG= regStatusesResVO from repository :" + regStatusesResVO);
		}
		if ((regStatusesResVO != null) && !regStatusesResVO.getServiceErrorVO().isErrorExists()) {
			this.syncRegistryStatuses(regStatusesResVO, registrySkinnyVOList, siteId);
		}
		
		this.logDebug("GiftRegistryManager.syncRegistryStatusWithECOM() method ends");
		
	}
	/**
	 * This method will call the GetRegistryStatusesByProfileId method of GiftRegistryTools and
	 * will get latest statuses of registries associated with particular profile 
	 *
	 * @param registryStatusVO RegistryStatusVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public RegStatusesResVO getRegistryStatusesByProfileId(RegistryStatusVO registryStatusVO) throws BBBSystemException, BBBBusinessException{
		String siteId = BBBCoreConstants.BLANK;
		String profileId = BBBCoreConstants.BLANK;
		RegStatusesResVO regStatusesResVO = new RegStatusesResVO();
		ServiceErrorVO serviceErrorVO = new ServiceErrorVO();
		if(null != registryStatusVO){
			siteId = registryStatusVO.getSiteId();
			profileId = registryStatusVO.getProfileId();
		}
		if(BBBUtility.isEmpty(profileId)){
			serviceErrorVO.setErrorExists(true);
			serviceErrorVO.setErrorMessage(BBBGiftRegistryConstants.ERROR_INVALID_PROFILE_ID);
			serviceErrorVO.setErrorDisplayMessage(BBBGiftRegistryConstants.ERROR_INVALID_PROFILE_ID);
			serviceErrorVO.setErrorId(BBBGiftRegistryConstants.VALIDATION);
		}else if(getGiftRegUtils().hasInvalidChars(profileId)){
			serviceErrorVO.setErrorExists(true);
			serviceErrorVO.setErrorMessage(BBBGiftRegistryConstants.INVALID_CHARS_IN_INPUT);
			serviceErrorVO.setErrorDisplayMessage(BBBCoreConstants.BLANK);
			serviceErrorVO.setErrorId(BBBGiftRegistryConstants.INPUT_FIEL_DS_UNEXPECTED_FORMAT);
		} 
		regStatusesResVO.setServiceErrorVO(serviceErrorVO);
		if(!regStatusesResVO.getServiceErrorVO().isErrorExists()){
			regStatusesResVO = this.getGiftRegistryTools().GetRegistryStatusesByProfileId(registryStatusVO);
		}
		return regStatusesResVO;
	}

	/**
	 * This method will sync registries with webservice response.
	 *
	 * @param regStatusesResVO   GetRegistryStatusesByProfileId webservice response
	 * @param registrySkinnyVOList Users registries list
	 * @param siteId siteId
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private void syncRegistryStatuses(final RegStatusesResVO regStatusesResVO, final List<RegistrySkinnyVO> registrySkinnyVOList, final String siteId) throws BBBBusinessException, BBBSystemException {

		this.logDebug("GiftRegistryManager.syncRegistryStatuses() method started");
		boolean clearSessionCache = false;
		final List<RegistryStatusVO> regStatusList = regStatusesResVO.getListRegistryStatusVO();
    	if(null == regStatusList || regStatusList.size() == 0) {
    		for(final RegistrySkinnyVO registrySkinnyVO:registrySkinnyVOList) {
				this.getGiftRegistryTools().updateRegistryStatus(
											registrySkinnyVO,
											siteId,
											this.getGiftRegistryConfigurationByKey(
												BBBGiftRegistryConstants.GIFT_REGISTRY_DELETE_STATUS_CONFIG_KEY));
        	}
    		clearSessionCache = true;
    	}
    	else {

	    	for(final RegistrySkinnyVO registrySkinnyVO: registrySkinnyVOList) {
	    		boolean foundRegistry = false;
	    		for(final RegistryStatusVO regStatus: regStatusList) {
	    			if(!regStatus.getRegistryId().equals(registrySkinnyVO.getRegistryId())) {
	    				continue;
	    			}
					foundRegistry = true;
					if((registrySkinnyVO.getStatus() == null)
							|| !registrySkinnyVO.getStatus().equals(regStatus.getStatusDesc())) {
						this.getGiftRegistryTools().updateRegistryStatus(registrySkinnyVO, siteId, regStatus.getStatusDesc());
						clearSessionCache = true;
					}
	    		}
	    		if(!foundRegistry) {

					this.getGiftRegistryTools().updateRegistryStatus(
							registrySkinnyVO,
							siteId,
							this.getGiftRegistryConfigurationByKey(
								BBBGiftRegistryConstants.GIFT_REGISTRY_DELETE_STATUS_CONFIG_KEY));
	    		}
	    	}
    	}
		if(clearSessionCache) {
			this.invalidateSessionRegistriesData(ServletUtil.getCurrentRequest());
		}
		this.logDebug("GiftRegistryManager.syncRegistryStatuses() method end");
	}

	/**
	 * This method will fetch gift registry configuration  for the given key
	 * @param giftRegistryStatusKey
	 * @return String Gift Registry Status
	 */
	public String getGiftRegistryConfigurationByKey(final String configKey) {

		this.logDebug("GiftRegistryManager.getGiftRegistryConfigurationByKey() method started");
		String configValue=null;
		try{

		 	final List<String> giftRegistryStatusList = this.getBbbCatalogTools().getAllValuesForKey(
												BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,
												configKey);
	    	if((giftRegistryStatusList != null)
	    			&& (giftRegistryStatusList.size() > 0)) {
	    		configValue = giftRegistryStatusList.get(0);
	    	}
		}catch(final BBBSystemException e) {
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10131+" BBBSystemException from getGiftRegistryConfigurationByKey of GiftRegistryTools",e);
		}
		catch(final BBBBusinessException e) {
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10131+" BBBSystemException from getGiftRegistryConfigurationByKey of GiftRegistryTools",e);
		}
		this.logDebug("GiftRegistryManager.getGiftRegistryConfigurationByKey() method end");
		return configValue;
	}

	/**
	 * This method will reurn users registries that are in acceptable status.
	 * Acceptable status will be confiugred through Configuration parameters.
	 *
	 * @param profile  Profile
	 * @param siteId  siteId
	 * @return Acceptable Registries to show in Product details page.
	 * @throws BBBBusinessException
	 * @throws RepositoryException
	 * @throws BBBSystemException
	 */
	public List<RegistrySkinnyVO> getAcceptableGiftRegistries(final Profile profile, final String siteId) throws BBBBusinessException, RepositoryException, BBBSystemException {

		this.logDebug("GiftRegistryManager.getAcceptableGiftRegistries() method started");
		final List<RegistrySkinnyVO> registrySkinnyVOList = this.getUserRegistryList(profile, siteId);
		final String acceptableStatuses = this.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY);
		if(!BBBUtility.isEmpty(acceptableStatuses)) {
			final String[] statusesArray = acceptableStatuses.split(BBBCoreConstants.COMMA);
			final List<String> acceptableStatusesList = new ArrayList<String>(Arrays.asList(statusesArray));
			final ListIterator<RegistrySkinnyVO> iterator = registrySkinnyVOList.listIterator();
			 do
		        {
		            if(!iterator.hasNext()
		                || (registrySkinnyVOList.isEmpty())) {
						break;
					}

		            final RegistrySkinnyVO registrySkinnyVO = iterator.next();
		            if(!acceptableStatusesList.contains(registrySkinnyVO.getStatus())) {
		            	iterator.remove();
		            }
		        }while(true);
		}
		this.logDebug("GiftRegistryManager.getAcceptableGiftRegistries() method end");
		return registrySkinnyVOList;
	}

	public void generatePDFDocument(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse, String htmlMessage, boolean downloadFlag)
			throws FactoryConfigurationError {
		this.logDebug("GiftRegistryManager.generatePDFDocument() method started");		
		try {
		  Document doc = new Document(com.itextpdf.text.PageSize.ARCH_A);
		  pResponse.setContentType("application/pdf");
		  if(downloadFlag) {
			  pResponse.setHeader("Content-disposition", "attachment; filename=printCards.pdf");
		  }
		  ServletContext context = ServletUtil.getCurrentServletContext(pRequest);
          PdfWriter pdfWriter = PdfWriter.getInstance(doc, pResponse.getOutputStream());
          logDebug("HTML Content "+htmlMessage);
          byte[] byteArray = (htmlMessage).getBytes("ISO-8859-1");
          ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
          doc.open();
          //Resolve CSS
          CSSResolver cssResolver = XMLWorkerHelper.getInstance().getDefaultCssResolver(true);
          XMLWorkerFontProvider fontProv = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
          fontProv.registerDirectories();
          fontProv.register(context.getResource("/WEB-INF/fonts/Playball-Regular.ttf").getPath());
          fontProv.register(context.getResource("/WEB-INF/fonts/LibreBaskerville-Regular.ttf").getPath());
          fontProv.register(context.getResource("/WEB-INF/fonts/OpenSans-CondLight.ttf").getPath());
          fontProv.register(context.getResource("/WEB-INF/fonts/JuliusSansOne-Regular.ttf").getPath());
          fontProv.register(context.getResource("/WEB-INF/fonts/Raleway-Regular.ttf").getPath());
          fontProv.register(context.getResource("/WEB-INF/fonts/AMERICAT.ttf").getPath());
          fontProv.register(context.getResource("/WEB-INF/fonts/Tangerine_Regular.ttf").getPath());
          CssAppliers cs = new CssAppliersImpl(fontProv);
          HtmlPipelineContext htmlContext = new HtmlPipelineContext(cs);
          htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
          //Image Provider in XmlWorker
          AbstractImageProvider imageProvider = new AbstractImageProvider() {
        	  public Image retrieve(String src) {
        		  if(src.startsWith("//s7d9.scene7.com")){
        			  try {
        				  Image img = Image.getInstance("https:" + src);
        				  return img;
					} catch (BadElementException e) {
						logError("BadElementException while generating PDF through Xml Worker:  " + e);
					} catch (MalformedURLException e) {
						logError("MalformedURLException while generating PDF through Xml Worker:  " + e);
					} catch (IOException e) {
						logError("IOException while generating PDF through Xml Worker:  " + e);
					}
        		  }
        		 return null;
        	  }
			@Override
			public String getImageRootPath() {
				return null;
			}
          };
          htmlContext.setImageProvider(imageProvider);
          Pipeline pipeline = new CssResolverPipeline(cssResolver, new HtmlPipeline(htmlContext, new PdfWriterPipeline(doc, pdfWriter)));
          XMLWorker worker = new XMLWorker(pipeline, true);
          XMLParser p = new XMLParser(worker);
          p.parse(bais);
		  doc.close();
		  pdfWriter.close();
		  pResponse.flushBuffer(); 
		  logDebug("HTML Rendering to PDF is complete");
	    } catch (Exception ex) {
	      logError("Exception while generating the PDF", ex);
	    }
	}
	

	/**
	 * This method will get registry status from database.
	 *
	 * @param siteId
	 * @param registryId
	 * @throws BBBSystemException
	 */
	public String getRegistryStatusFromRepo(final String pSiteId, final String pRegistryId)
			throws BBBSystemException {
		this.logDebug("GiftRegistryManager.getRegistryStatusFromRepo method started");
		String registryStatus = "";
		RepositoryItem[] repositoryItems = null;
		try {
			repositoryItems = this.getGiftRegistryTools().fetchGiftRepositoryItem(pSiteId, pRegistryId);
		} catch (final BBBBusinessException e) {
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_20130	+ " BBBBusinessException in getRegistryStatusFromRepo of GiftRegistryTools", e);
			throw new BBBSystemException("BBBBusinessException in getRegistryStatusFromRepo of GiftRegistryTools", e);
		}
		if (repositoryItems != null) {
			registryStatus = (String) repositoryItems[0]
					.getPropertyValue("registryStatus");
		}
		this.logDebug("Registry Status for Registry Id " + pRegistryId + " is " + registryStatus);
		this.logDebug("GiftRegistryManager.getRegistryStatusFromRepo method end");
		return registryStatus;

	}

	// for certona tagging

	public String getRegistryNameForRegCode(final String registryCodeType) throws RepositoryException, BBBSystemException, BBBBusinessException {
		String registryName = null;
		 final List<RegistryTypeVO> registryTypeVOs = this.getRegistryTypes();
		 for(final RegistryTypeVO registryVO : registryTypeVOs ){
			 if(registryVO.getRegistryCode().equalsIgnoreCase(registryCodeType)){
				 registryName =  registryVO.getRegistryName();
				 break;
			 }
		 }
		 return registryName;
	}
	
	public RegistrySummaryVO getActiveRegistryForRest() {
		Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.ATG_PROFILE);
		String siteId = getSiteContext().getSiteContextManager().getCurrentSiteId();
		GiftRegSessionBean giftRegSessionBean = (GiftRegSessionBean) ServletUtil.getCurrentRequest().resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean");
		
		String pRecentRegistryId = "";
		RegistrySummaryVO pRegSummaryVO = null;
		if (!profile.isTransient()) {

			BBBSessionBean sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(SESSION_BEAN);
			List<String> userRegList = new ArrayList<String>();
			List<String> userActiveRegList = new ArrayList<String>();
			final HashMap sessionMap = sessionBean.getValues();
			pRegSummaryVO = (RegistrySummaryVO) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
			try {
			
				if (pRegSummaryVO == null ) {
					userRegList = (List) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_LIST);
					userActiveRegList = (List) sessionMap.get(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST);
					String acceptableStatuses = getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY);
					List<String> acceptableStatusesList = new ArrayList<String>();
					if (!BBBUtility.isEmpty(acceptableStatuses)) {
						String[] statusesArray = acceptableStatuses.split(BBBCoreConstants.COMMA);
						acceptableStatusesList.addAll(Arrays.asList(statusesArray));
					}
		
					// Get Registry Data from the Database
					if (userActiveRegList == null || userActiveRegList.isEmpty()) {
						RepositoryItem[] userRegistriesRepItems;
		
						logDebug("GiftRegistryManager.getActiveRegistryForRest : Fetch user registries from database");
						userRegistriesRepItems = fetchUserRegistries(siteId, profile.getRepositoryId());
						// Set Active Registry Data
						if (userRegistriesRepItems != null) {
							userRegList = new ArrayList<String>(userRegistriesRepItems.length);
							userActiveRegList = new ArrayList<String>(userRegistriesRepItems.length);
							for (RepositoryItem repositoryItem : userRegistriesRepItems) {
								String registryId = repositoryItem.getRepositoryId();
								String registryStatus = getRegistryStatusFromRepo(siteId,	registryId);
								if (acceptableStatusesList.contains(registryStatus)) {
									userActiveRegList.add(registryId);
								}
								userRegList.add(registryId);
							}
						}
					}
					if(userActiveRegList!=null && !userActiveRegList.isEmpty()){
						if(userActiveRegList != null && userActiveRegList.size() == 1){
							pRecentRegistryId = (String) userActiveRegList.get(0);
							pRegSummaryVO = getRegistryInfo( pRecentRegistryId, siteId);
							sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
						}
						else if(userActiveRegList != null && userActiveRegList.size() > 1){
							pRecentRegistryId = fetchUsersSoonestOrRecent(userActiveRegList);
							// cases when the user has more than 1 registries and all not having event date. The recent registry id fetch is null.
							// Registry summary vo in that case is not populated.
							// FIxed as part of ILD-20
							if(pRecentRegistryId==null){
								pRecentRegistryId=userActiveRegList.get(0);
							}
							pRegSummaryVO = getRegistryInfo( pRecentRegistryId, siteId);
							sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
						}											
						if (!BBBGiftRegistryConstants.GR_CREATE.equals(giftRegSessionBean.getRegistryOperation())
							|| !BBBGiftRegistryConstants.GR_UPDATE.equals(giftRegSessionBean.getRegistryOperation())
							|| !BBBGiftRegistryConstants.GR_ITEM_UPDATE.equals(giftRegSessionBean.getRegistryOperation())
							|| !BBBGiftRegistryConstants.GR_ITEM_REMOVE.equals(giftRegSessionBean.getRegistryOperation())
							|| !BBBGiftRegistryConstants.OWNER_VIEW.equals(giftRegSessionBean.getRegistryOperation())) {
	
							Long diffDays = (long) 0;
							if(pRegSummaryVO != null && pRegSummaryVO.getEventDate()!=null)
							{
								diffDays = getDateDiff(siteId, pRegSummaryVO);
							}
							if (diffDays < -90) {
								pRegSummaryVO = null;
							}
						}
					}
				}

			}catch (RepositoryException e) {
				logError(LogMessageFormatter.formatMessage(ServletUtil.getCurrentRequest(),	"Repository Exception from service of GetRegistryFlyoutDroplet ", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1075), e);
				pRegSummaryVO = null;
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(ServletUtil.getCurrentRequest(),	"System Exception from service of GetRegistryFlyoutDroplet ", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1076), e);
				pRegSummaryVO = null;
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(ServletUtil.getCurrentRequest(),	"BBBBusinessException from SERVICE of GiftRegistryFlyoutDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005), e);
				pRegSummaryVO = null;
			} catch (Exception ex) {
				logError(LogMessageFormatter.formatMessage(ServletUtil.getCurrentRequest(),	"Other Exception from service of GetRegistryFlyoutDroplet ", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1077), ex);
				pRegSummaryVO = null;
			}
		}
		return 	pRegSummaryVO;
	}
	
   /**
	 * This method will set success URL for kick starter page
	 *
	 * @param pBBBSessionBean - session bean
	 * @param pGiftRegistryViewBean - gift registry bean
	 */
   public void setSuccessURL(BBBSessionBean pBBBSessionBean,GiftRegistryViewBean pGiftRegistryViewBean){
		StringBuffer constructSuccessURL= new StringBuffer();
		constructSuccessURL.append(getSuccessUrlKickStarter());
		constructSuccessURL.append(BBBGiftRegistryConstants.ID);
		constructSuccessURL.append(BBBGiftRegistryConstants.EQUAL);
		constructSuccessURL.append(pBBBSessionBean.getKickStarterId());
		if(pBBBSessionBean.getEventType()!=null){
		constructSuccessURL.append(BBBGiftRegistryConstants.AMPERSAND);
		constructSuccessURL.append(BBBGiftRegistryConstants.EVENT_TYPE);
		constructSuccessURL.append(BBBGiftRegistryConstants.EQUAL);
		constructSuccessURL.append(pBBBSessionBean.getEventType());
		}
		if(pBBBSessionBean.getRegistryId()!=null){
		constructSuccessURL.append(BBBGiftRegistryConstants.AMPERSAND);
		constructSuccessURL.append(BBBGiftRegistryConstants.REGISTRY_ID);
		constructSuccessURL.append(BBBGiftRegistryConstants.EQUAL);
		constructSuccessURL.append(pBBBSessionBean.getRegistryId());	
		}
		pGiftRegistryViewBean.setSuccessURL(constructSuccessURL.toString());
			logDebug("kick starter success redirect url construction.."+constructSuccessURL);
		constructSuccessURL=null;			  
   }
	public boolean isRegistryOwnedByProfile(final String pProfileId, final String pRegistryId, final String pSiteId) throws BBBBusinessException, BBBSystemException{
		boolean retVal = false;
		final RepositoryItem[] repositoryItems  = this.getGiftRegistryTools().fetchGiftRepositoryItem(pSiteId, pRegistryId);
		if((repositoryItems==null) || (repositoryItems.length==0)){
			throw new BBBBusinessException("Registry not found in repository");
		}
		final RepositoryItem ownerProfile=(RepositoryItem)repositoryItems[0].getPropertyValue("registryOwner");
		final RepositoryItem coOwnerProfile=(RepositoryItem)repositoryItems[0].getPropertyValue("registryCoOwner");

		if(ownerProfile!=null){
			final String ownerId = ownerProfile.getRepositoryId();
			if(BBBUtility.isNotEmpty(ownerId) && ownerId.equals(pProfileId)){
				retVal=true;
			}
		}
		if(coOwnerProfile !=null){
			final String coOwnerId = coOwnerProfile.getRepositoryId();
			if(BBBUtility.isNotEmpty(coOwnerId) && coOwnerId.equals(pProfileId)){
				retVal=true;
			}
		}

		if(!retVal){
			throw new BBBBusinessException("Profile not assoicated with registry");
		}
		return retVal;
	}
	
	
	public boolean isCoOwerByProfile(final String pProfileId, final String pRegistryId, final String pSiteId ) throws BBBBusinessException, BBBSystemException{
		boolean isCoRegOwner = false;
		final RepositoryItem[] repositoryItems  = this.getGiftRegistryTools().fetchGiftRepositoryItem(pSiteId, pRegistryId);
		if((repositoryItems==null) || (repositoryItems.length==0)){
			isCoRegOwner = false;
		}else{	
			//final RepositoryItem ownerProfile=(RepositoryItem)repositoryItems[0].getPropertyValue("registryOwner");
			final RepositoryItem coOwnerProfile=(RepositoryItem)repositoryItems[0].getPropertyValue("registryCoOwner");

			if(coOwnerProfile !=null){
				final String coOwnerId = coOwnerProfile.getRepositoryId();
				if(BBBUtility.isNotEmpty(coOwnerId) && coOwnerId.equals(pProfileId)){
					isCoRegOwner=true;
				}
			}
		}

		return isCoRegOwner;
	}
	
	/**
	 * build the registry collection for the given order
	 * @param pReq
	 * @param order
	 */
	@SuppressWarnings("unchecked")
	public void populateRegistryMapInOrder(DynamoHttpServletRequest pReq, BBBOrder order){
		Map<String, RegistrySummaryVO> registryMap = order.getRegistryMap();
		if(registryMap == null) {
			registryMap = new HashMap<String, RegistrySummaryVO>();
		}
		@SuppressWarnings("rawtypes")
		List commerceItems = order.getCommerceItems();
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = commerceItems.iterator(); iterator.hasNext();) {
			Object currentItem = iterator.next();
			if(currentItem instanceof BBBCommerceItem) {
				String registryId = ((BBBCommerceItem) currentItem).getRegistryId();

				if( !BBBUtility.isEmpty(registryId) 
						&& registryMap.containsKey(registryId) == false){

					RegistrySummaryVO registrySummaryVO;
					try {
						registrySummaryVO = this.getRegistryInfo(registryId, order.getSiteId());
						String countryName = getBbbCatalogTools().getDefaultCountryForSite(getSiteId());
						if(registrySummaryVO != null){
							registrySummaryVO.getShippingAddress().setCountry(countryName);
							registryMap.put(registryId, registrySummaryVO);
						}       					

					} catch (BBBSystemException e) {
						logError(LogMessageFormatter.formatMessage(pReq, MSG_COUNTRY_MISSING), e);
					} catch (BBBBusinessException e) {
						logError(LogMessageFormatter.formatMessage(pReq, MSG_COUNTRY_MISSING), e);
					} catch (Exception e) {
						logError(LogMessageFormatter.formatMessage(pReq, MSG_REG_SERVICE_NOTWORKING), e);
					}
				}
			}
		}
		order.setRegistryMap(registryMap);
	}
	
	public SiteContext getSiteContext() {
		return this.siteContext;
	}

	public void setSiteContext(final SiteContext siteContext) {
		this.siteContext = siteContext;
	}

	public GiftRegistryTypesDroplet getGiftRegistryTypesDroplet() {
		return this.giftRegistryTypesDroplet;
	}

	public void setGiftRegistryTypesDroplet(
			final GiftRegistryTypesDroplet giftRegistryTypesDroplet) {
		this.giftRegistryTypesDroplet = giftRegistryTypesDroplet;
	}


	/**getRegistryStatuByProfileId webservice name */
	private String registryStatuByProfileIdServiceName;
	/**
	 * @return the registrySearchServiceName
	 */
	public String getRegistrySearchServiceName() {
		return this.mRegistrySearchServiceName;
	}

	/**
	 * @param registrySearchServiceName the registrySearchServiceName to set
	 */
	public void setRegistrySearchServiceName(final String registrySearchServiceName) {
		this.mRegistrySearchServiceName = registrySearchServiceName;
	}
	
	
	public String getCopyRegistryServiceName() {
		return this.mCopyRegistryServiceName;
	}

	public void setCopyRegistryServiceName(final String pCopyRegistryServiceName) {
		this.mCopyRegistryServiceName = pCopyRegistryServiceName;
	}
	
	
	/**
	 * Gets the email co found registry type.
	 *
	 * @return the mEmailCoFoundRegistryType
	 */
	public String getEmailCoFoundRegistryType() {
		return this.mEmailCoFoundRegistryType;
	}

	/**
	 * Sets the email co found registry type.
	 *
	 * @param pEmailCoFoundRegistryType
	 *            the new email co found registry type
	 */
	public void setEmailCoFoundRegistryType(final String pEmailCoFoundRegistryType) {
		this.mEmailCoFoundRegistryType = pEmailCoFoundRegistryType;
	}

	/**
	 * @return the emailAPRegRegistryType
	 */
	public String getEmailAPRegRegistryType() {
		return this.emailAPRegRegistryType;
	}

	/**
	 * @param emailAPRegRegistryType the emailAPRegRegistryType to set
	 */
	public void setEmailAPRegRegistryType(final String emailAPRegRegistryType) {
		this.emailAPRegRegistryType = emailAPRegRegistryType;
	}

	
	public String getMxEmailARegistryType() {
		return mxEmailARegistryType;
	}

	public void setMxEmailARegistryType(String mxEmailARegistryType) {
		this.mxEmailARegistryType = mxEmailARegistryType;
	}

	public String getMxEmailAPRegRegistryType() {
		return mxEmailAPRegRegistryType;
	}

	public void setMxEmailAPRegRegistryType(String mxEmailAPRegRegistryType) {
		this.mxEmailAPRegRegistryType = mxEmailAPRegRegistryType;
	}
	

	/**
	 * Gets the email co not found registry type.
	 *
	 * @return the mEmailCoNotFoundRegistryType
	 */
	public String getEmailCoNotFoundRegistryType() {
		return this.mEmailCoNotFoundRegistryType;
	}

	/**
	 * Sets the email co not found registry type.
	 *
	 * @param pEmailCoNotFoundRegistryType
	 *            the new email co not found registry type
	 */
	public void setEmailCoNotFoundRegistryType(
			final String pEmailCoNotFoundRegistryType) {
		this.mEmailCoNotFoundRegistryType = pEmailCoNotFoundRegistryType;
	}

	/**
	 * Gets the email a registry type.
	 *
	 * @return the mEmailARegistryType
	 */
	public String getEmailARegistryType() {
		return this.mEmailARegistryType;
	}

	/**
	 * Sets the email a registry type.
	 *
	 * @param pEmailARegistryType
	 *            the EmailARegistryType to set
	 */
	public void setEmailARegistryType(final String pEmailARegistryType) {
		this.mEmailARegistryType = pEmailARegistryType;
	}
	/**
	 * Gets the gift registry tools.
	 *
	 * @return the giftRegistryTools
	 */
	public GiftRegistryTools getGiftRegistryTools() {
		return this.mGiftRegistryTools;
	}

	/**
	 * Sets the gift registry tools.
	 *
	 * @param pGiftRegistryTools
	 *            the giftRegistryTools to set
	 */
	public void setGiftRegistryTools(final GiftRegistryTools pGiftRegistryTools) {
		this.mGiftRegistryTools = pGiftRegistryTools;
	}

	/**
	 * Gets the bbb catalog tools.
	 *
	 * @return the bbbCatalogTools
	 */
	public BBBCatalogTools getBbbCatalogTools() {
		return this.mBbbCatalogTools;
	}

	/**
	 * Sets the bbb catalog tools.
	 *
	 * @param pBbbCatalogTools
	 *            the bbbCatalogTools to set
	 */
	public void setBbbCatalogTools(final BBBCatalogTools pBbbCatalogTools) {
		this.mBbbCatalogTools = pBbbCatalogTools;
	}
	
	/**
	 * Gets the tools.
	 *
	 * @return the tools
	 */
	public BBBProfileTools getTools() {
		return this.mTools;
	}

	/**
	 * Sets the tools.
	 *
	 * @param pTools
	 *            the tools to set
	 */
	public void setTools(final BBBProfileTools pTools) {
		this.mTools = pTools;
	}
	
	/**
	 * Gets the bbb address api.
	 *
	 * @return the bbbAddressAPI
	 */
	public BBBAddressAPI getBbbAddressAPI() {
		return this.bbbAddressAPI;
	}

	/**
	 * Sets the bbb address api.
	 *
	 * @param pBbbAddressAPI
	 *            the bbbAddressAPI to set
	 */
	public void setBbbAddressAPI(final BBBAddressAPI pBbbAddressAPI) {
		this.bbbAddressAPI = pBbbAddressAPI;
	}

	/**
	 * Gets the email sender.
	 *
	 * @return the emailSender
	 */
	public BBBTemplateEmailSender getEmailSender() {
		return this.mEmailSender;
	}

	/**
	 * Sets the email sender.
	 *
	 * @param pEmailSender
	 *            the emailSender to set
	 */
	public void setEmailSender(final BBBTemplateEmailSender pEmailSender) {
		this.mEmailSender = pEmailSender;
	}

	/**
	 * Gets the template url.
	 *
	 * @return the templateUrl
	 */
	public String getTemplateUrl() {
		return this.mTemplateUrl;
	}

	/**
	 * Sets the template url.
	 *
	 * @param pTemplateUrl
	 *            the templateUrl to set
	 */
	public void setTemplateUrl(final String pTemplateUrl) {
		this.mTemplateUrl = pTemplateUrl;
	}


	/**
	 * Gets the linking co reg to reg service name.
	 *
	 * @return the linkingCoRegToRegServiceName
	 */
	public String getLinkingCoRegToRegServiceName() {
		return this.mLinkingCoRegToRegServiceName;
	}

	/**
	 * Sets the linking co reg to reg service name.
	 *
	 * @param linkingCoRegToRegServiceName
	 *            the linkingCoRegToRegServiceName to set
	 */
	public void setLinkingCoRegToRegServiceName(
			final String linkingCoRegToRegServiceName) {
		this.mLinkingCoRegToRegServiceName = linkingCoRegToRegServiceName;
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
	public void setSearchRegistryServiceName(final String searchRegistryServiceName) {
		this.mSearchRegistryServiceName = searchRegistryServiceName;
	}

	/**
	 * Gets the registry info service name.
	 *
	 * @return the registryInfoServiceName
	 */
	public String getRegistryInfoServiceName() {
		return this.mRegistryInfoServiceName;
	}

	/**
	 * Sets the registry info service name.
	 *
	 * @param registryInfoServiceName
	 *            the registryInfoServiceName to set
	 */
	public void setRegistryInfoServiceName(final String registryInfoServiceName) {
		this.mRegistryInfoServiceName = registryInfoServiceName;
	}

	/**
	 * @param GiftRegistryViewBean
	 * 
	 * @return RegCopyResVO
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	public RegCopyResVO copyRegistry(GiftRegistryViewBean pGiftRegistryViewBean) throws BBBBusinessException, BBBSystemException {
		RegCopyResVO regCopyResVO =new RegCopyResVO();
	   BBBSessionBean sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(SESSION_BEAN);	   
	   boolean regItemsWSCall = false;
	   
		List<String> regItemsWSCallFlag = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall");
		if (regItemsWSCallFlag != null && !regItemsWSCallFlag.isEmpty()){
			regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag.get(0));
		}
		if (regItemsWSCall){
			regCopyResVO  = this.getGiftRegistryTools().copyRegistry(pGiftRegistryViewBean);
		}
		else{
			regCopyResVO  = this.getGiftRegistryTools().copyRegistryInEcomAdmin(pGiftRegistryViewBean);
		}
		
		return regCopyResVO;   
	}
	
	/**
	 * @param srcRegistryId
	 * @param dstRegistryId
	 * @return 
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	
	@SuppressWarnings("unchecked")
	public RegCopyResVO copyRegistry(String srcRegistryId, String targetRegistryId, String regType, String siteId ) throws BBBBusinessException, BBBSystemException {
		
		RegCopyResVO resCopyVo =new RegCopyResVO();
		final GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
		
		
		giftRegistryViewBean.setSiteFlag(this.getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).get(0));
		giftRegistryViewBean.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
		giftRegistryViewBean.setServiceName(this.getCopyRegistryServiceName());
		giftRegistryViewBean.setSourceRegistry(srcRegistryId);
		giftRegistryViewBean.setTargetRegistry(targetRegistryId);
		giftRegistryViewBean.setRegistryName(regType);
		
		resCopyVo = copyRegistry(giftRegistryViewBean);
		
		giftRegistryViewBean.setCopyRegErr(resCopyVo.hasError());
		giftRegistryViewBean.setTotQtySrcReg(resCopyVo.gettotalNumOfItemsCopied());
		if (!resCopyVo.hasError()) {
			final DynamoHttpServletRequest pRequest=ServletUtil.getCurrentRequest();
			if (pRequest != null) {
				final BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
				sessionBean.getValues().put(giftRegistryViewBean.getTargetRegistry() + BBBGiftRegistryConstants.REG_SUMMARY_KEY_CONST, null);
			}
		}
						
		return resCopyVo;
	}
	public Repository getRecommenderLandingPageTemplate() {
		return mRecommenderLandingPageTemplate;
	}
	public void setRecommenderLandingPageTemplate(
			Repository pRecommenderLandingPageTemplate) {
		mRecommenderLandingPageTemplate = pRecommenderLandingPageTemplate;
	}
	public LandingTemplateManager getLandingTemplateManager() {
		return mLandingTemplateManager;
	}
	public void setLandingTemplateManager(
			LandingTemplateManager pLandingTemplateManager) {
		mLandingTemplateManager = pLandingTemplateManager;
	}
	public String getDefaultSite() {
		return mDefaultSite;
	}
	public void setDefaultSite(String pDefaultSite) {
		mDefaultSite = pDefaultSite;
	}
	public String getDefaultChannel() {
		return mDefaultChannel;
	}
	public void setDefaultChannel(String pDefaultChannel) {
		mDefaultChannel = pDefaultChannel;
	}
	public String getDefaultRegistryType() {
		return mDefaultRegistryType;
	}
	public void setDefaultRegistryType(String pDefaultRegistryType) {
		mDefaultRegistryType = pDefaultRegistryType;
	}
	/**
	 * Gets the email registry recommendation type.
	 *
	 * @return the mEmailARegistryType
	 */
	public String getEmailRegistryRecommendation() {
		return this.mEmailRegistryRecommendation;
	}
		
	/**
	 * Sets the email registry recommendation type.
	 *
	 * @param pEmailRegistryRecommendation
	 *            the EmailRegistryRecommendationType to set
	 */
	public void setEmailRegistryRecommendation(String pEmailRegistryRecommendation) {
		this.mEmailRegistryRecommendation = pEmailRegistryRecommendation;
	}
		// PSI6 Surge Social Recommendation BPS-456 BPS-1112
	/**
	 *
	 * @param profileId
	 * @return
	 * @throws BBBSystemException
	 */
	public List<RegistrySummaryVO> recommendRegistryList(String profileId)
			throws BBBSystemException {
		this.logDebug("GiftRegistryManager.recommendRegistryList() method start");
		RepositoryItem[] recommendationRegistryList;
		List<RegistrySummaryVO> registrySummaryVOList = new ArrayList<RegistrySummaryVO>();
		try {
			recommendationRegistryList = this.getGiftRegistryTools().getRegistryFromProfileId(profileId);
			if(null == recommendationRegistryList) {
				return null;
			}
			populatingRecommendationRegistryList(recommendationRegistryList,registrySummaryVOList);

			}catch (BBBBusinessException bbbbe) {
			this.logError("Recommender Registry BBB Profile "
					+ profileId + " Error = " + BBBCoreErrorConstants.GIFTREGISTRY_ERROR_20133,bbbbe);
}
		this.logDebug("GiftRegistryManager.recommendRegistryList() method end");
		return registrySummaryVOList;
	}

	private void populatingRecommendationRegistryList(
			RepositoryItem[] recommendationRegistryList,List<RegistrySummaryVO> registrySummaryVOList) {
		
		logDebug("Entering populatingRecommendationRegistryList method :- Populating Recommendation Registry List");
		
        for(RepositoryItem recommendationRegistryItem: recommendationRegistryList) {
        	
        	Calendar eventDate = getEventDateTime(recommendationRegistryItem);			
			Calendar currentCal = getCurrentDate();			
			if(currentCal.compareTo(eventDate) == 1) {
				continue;
			}
			String siteId = (String) recommendationRegistryItem.getPropertyValue(BBBCatalogConstants.SITE_ID);
			if(siteId!=null)				
			populatingRecommendedRegistriesBySiteId(registrySummaryVOList,
					recommendationRegistryItem, siteId);
        }
        logDebug("Ending populatingRecommendationRegistryList method:- ");		
	}

	private void populatingRecommendedRegistriesBySiteId(
			List<RegistrySummaryVO> registrySummaryVOList,
			RepositoryItem recommendationRegistryItem, String siteId) {
		if (siteId.equals(SiteContextManager.getCurrentSite().getId()) ||
				recommendationRegistryItem.getPropertyValue(BBBCatalogConstants.EVENT_TYPE).equals(BBBCatalogConstants.REGISTRY_TYPE_BABY)) {
		registrySummaryVOList.add(populateRegistrySummaryVO(recommendationRegistryItem));
		}
	}

	private Calendar getCurrentDate() {
		Calendar currentCal = Calendar.getInstance();
		currentCal.set(Calendar.HOUR_OF_DAY, 0);
		currentCal.set(Calendar.MINUTE, 0);
		currentCal.set(Calendar.SECOND, 0);
		currentCal.set(Calendar.MILLISECOND, 0);
		return currentCal;
	}

	private Calendar getEventDateTime(
			RepositoryItem recommendationRegistryItem) {
		Timestamp eventDateTime = (Timestamp) recommendationRegistryItem.getPropertyValue(BBBCatalogConstants.EVENT_DATE);
		Calendar eventDate = Calendar.getInstance();
		eventDate.setTimeInMillis(eventDateTime.getTime());
		return eventDate;
	}

	/**
	 * 
	 * @param registryId
	 * @param token
	 * @param bbbProfile
	 * @return
	 * @throws BBBSystemException
	 */
	public RegistrySummaryVO persistRecommenderReln(String registryId, String token, String isFromFb) throws BBBSystemException {
		this.logDebug("GiftRegistryManager.populateRegistrySummaryVO() method start");
		
		final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		final Profile bbbProfile = (Profile)request.resolveName("/atg/userprofiling/Profile");

		RepositoryItem recommendationRegistryItem = getGiftRegistryTools().persistRecommenderReln(registryId, token,bbbProfile,isFromFb);
		RegistrySummaryVO registrySummaryVO = populateRegistrySummaryVO(recommendationRegistryItem);
		
		this.logDebug("GiftRegistryManager.populateRegistrySummaryVO() method start");
		return registrySummaryVO;
	}

	/**
	 * 
	 * @param recommendedRegistry
	 * @return
	 */
	public RegistrySummaryVO populateRegistrySummaryVO(RepositoryItem recommendedRegistry) {
		this.logDebug("GiftRegistryManager.populateAddressRegistrySummaryVO() method start");
		RegistrySummaryVO registrySummaryVO = new RegistrySummaryVO();
		String siteId = getSiteId();
		String registryStatus=null;
			
			if(null != recommendedRegistry.getPropertyValue("registryId")) {
				registrySummaryVO.setRegistryId(recommendedRegistry.getPropertyValue("registryId").toString());
				try {
					registryStatus = getGiftRegistryTools().getRegistryStatus(recommendedRegistry.getPropertyValue("registryId").toString(),this.getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
				} catch (BBBBusinessException | BBBSystemException e) {
					logError(e.getMessage(),e);
				} 
				if(registryStatus==null || (BBBCoreConstants.YES_CHAR.equalsIgnoreCase(registryStatus))){
					registrySummaryVO.setIsPublic(BBBGiftRegistryConstants.IS_PUBLIC_TRUE);
				}else{
					registrySummaryVO.setIsPublic(BBBGiftRegistryConstants.IS_PUBLIC_FALSE);
				}
			
			}
			if(null != recommendedRegistry.getPropertyValue("eventType")) {
				registrySummaryVO.setEventType(recommendedRegistry.getPropertyValue("eventType").toString());
			}
			if(null != recommendedRegistry.getPropertyValue("registrantName")) {
				registrySummaryVO.setPrimaryRegistrantFirstName(recommendedRegistry.getPropertyValue("registrantName").toString());
			}
			
			
			this.logDebug("GiftRegistryManager.populateAddressRegistrySummaryVO() method start");
			
		return registrySummaryVO;
	}
	// PSI6 Surge Social Recommendation BPS-456 BPS-1112

	public Repository getRegistrantLandingPageTemplate() {
		return mRegistrantLandingPageTemplate;
	}

	public void setRegistrantLandingPageTemplate(
			Repository pRegistrantLandingPageTemplate) {
		mRegistrantLandingPageTemplate = pRegistrantLandingPageTemplate;
	}

	public Repository getRegistryTypeRepository() {
		return mRegistryTypeRepository;
	}

	public void setRegistryTypeRepository(Repository pRegistryTypeRepository) {
		mRegistryTypeRepository = pRegistryTypeRepository;
	}
	
	
	private RegistryResVO getRegistryInfoFromWSorDB(RegistryReqVO regReqVO) throws BBBBusinessException, BBBSystemException
	{
		RegistryResVO registryResVO = null;

		boolean regItemsWSCall = false;
		List<String> regItemsWSCallFlag = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "RegItemsWSCall");
		if (!BBBUtility.isListEmpty(regItemsWSCallFlag))
		{
			regItemsWSCall = Boolean.parseBoolean(regItemsWSCallFlag.get(0));
		}
		if (regItemsWSCall)
		{
			// WS Call | calling the GiftRegistryTool's getRegistryInfo method
			registryResVO = this.getGiftRegistryTools().getRegistryInfo(regReqVO);
			this.logDebug("GiftRegistryManager.getRegistryInfoFromWSorDB MSG= registryResVO from Web Service :" + registryResVO);
		}
		else
		{
			// DB Call | calling the GiftRegistryTool's
			// getRegistryInfoFromEcomAdmin method
			registryResVO = this.getGiftRegistryTools().getRegistryInfoFromEcomAdmin(regReqVO.getRegistryId(), regReqVO.getSiteId());
			this.logDebug("GiftRegistryManager.getRegistryInfoFromWSorDB MSG= registryResVO from repository :" + registryResVO);
		}

		return registryResVO;
	}
	
	
	public RegistryResVO getRegistryInfoFromEcomAdmin(String registryId, String siteId) throws BBBBusinessException, BBBSystemException
	{
		RegistryResVO registryResVO = null;
		String siteID = getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0);
		registryResVO = this.getGiftRegistryTools().getRegistryInfoFromEcomAdmin(registryId, siteID);
		
		if ((registryResVO != null) &&  (registryResVO.getRegistrySummaryVO() != null) && (registryResVO.getRegistrySummaryVO().getEventDate() != null)) {
			if(siteID.equalsIgnoreCase(BBBGiftRegistryConstants.CANADA_SITE_ID)){
				registryResVO.getRegistrySummaryVO().setEventDate(BBBUtility.convertWSDateToCAFormat(registryResVO.getRegistrySummaryVO().getEventDate()));
				if(registryResVO.getRegistrySummaryVO().getFutureShippingDate() != null){
					registryResVO.getRegistrySummaryVO().setFutureShippingDate(BBBUtility.convertAppFormatDateToCAFormat(registryResVO.getRegistrySummaryVO().getFutureShippingDate()));
				}
			}else{
				registryResVO.getRegistrySummaryVO().setEventDate(BBBUtility.convertWSDateToUSFormat(registryResVO.getRegistrySummaryVO().getEventDate()));
			}
		}
		
		this.logDebug("GiftRegistryManager.getRegistryInfoFromEcomAdmin MSG= registryResVO from repository :" + registryResVO);
		return registryResVO;
	}
	
	/**
	 * This method populates RegisteryheaderVO from RegistryVO
	 * @param registryVO
	 * @return  RegistryHeaderVO
	 */

	public RegistryHeaderVO populateRegHeaderVO(RegistryVO registryVO) {
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryManager.populateRegHeaderVO() method starts");
		}
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		RegistryHeaderVO headerVO = new RegistryHeaderVO();
		if (!registryVO.isCreate()) {
			headerVO.setRegNum(registryVO.getRegistryId());
		}
		headerVO.setEventType(registryVO.getRegistryType().getRegistryTypeName());
		headerVO.setSiteId(registryVO.getSiteId());
		if(!(pRequest.getContextPath().equalsIgnoreCase(BBBCoreConstants.CONTEXT_TBS))){
			headerVO.setPassword(registryVO.getWord());
		}
		else{ 
			headerVO.setPassword(BBBCoreConstants.TBS+BBBCoreConstants.COLON
						+((AssociateVO) pRequest.getSession().getAttribute(BBBGiftRegistryConstants.ASSOCIATE_ATTRIBUTE_NAME1)).getAssociateId()
						+BBBCoreConstants.COLON
						+(String) pRequest.getSession().getAttribute(BBBGiftRegistryConstants.STORE_NUMBER));
		}
		headerVO.setPasswordHint(registryVO.getHint());
		headerVO.setIsPublic(!BBBUtility.isEmpty(registryVO.getIsPublic()) ? registryVO.getIsPublic() : "1");
		String networkAffiliation = BBBCoreConstants.NO_CHAR;
		if (!BBBUtility.isEmpty(registryVO.getNetworkAffiliation())) {
			networkAffiliation =  registryVO.getNetworkAffiliation();
		}
		headerVO.setNetworkAffiliation(networkAffiliation);
		if (registryVO.getEvent() != null) {
			headerVO.setEventDate(registryVO.getEvent().getEventDateWS());
			headerVO.setEstimateNumGuests(registryVO.getEvent().getGuestCount());
		
			if (!BBBUtility.isEmpty(registryVO.getEvent().getShowerDateWS())) {
				headerVO.setShowerDate(registryVO.getEvent().getShowerDateWS());
			} else {
				headerVO.setShowerDate(BBBCoreConstants.BLANK);
			}
			
			if (!BBBUtility.isEmpty(registryVO.getEvent().getBirthDateWS())) {
				headerVO.setOtherDate(registryVO.getEvent().getBirthDateWS());
			} else {
				headerVO.setOtherDate(BBBCoreConstants.BLANK);
			}
		}
		
		headerVO.setPromoEmailFlag(registryVO.getSignup());
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryManager.populateRegHeaderVO() method ends :: " + headerVO);
		} 
		
		return headerVO;
	}
	
	/**
	 * This method populates RegistryBabyVO from RegistryVO for Baby Registry
	 * @param registryVO
	 * @return  RegistryBabyVO
	 */

	public RegistryBabyVO populateRegBabyVO(RegistryVO registryVO) {
		
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryManager.populateRegBabyVO() method starts");
		}
		RegistryBabyVO babyVO = new RegistryBabyVO();
		String gender = BBBCoreConstants.BLANK;
		if (registryVO.getEvent() != null) {
			if (!BBBUtility.isEmpty(registryVO.getEvent().getBabyGender())) {
				gender = registryVO.getEvent().getBabyGender();
			}
			babyVO.setDecor(registryVO.getEvent().getBabyNurseryTheme());
			babyVO.setFirstName(registryVO.getPrimaryRegistrant().getBabyMaidenName()); 
		}
		babyVO.setGender(gender);
		
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryManager.populateRegBabyVO() method ends :: " + babyVO);
		}
		return babyVO;
	}
	
	/**
	 * This method populates Registrant and Co Registrant details into List<RegNamesVO> from RegistryVO
	 * @param registryVO
	 * @return  List<RegNamesVO>
	 */
	
	public void populateRegNamesVO(RegistryVO registryVO, RegistrantVO registrantVO, String registrantType, List<RegNamesVO> regNamesVOs) {
		
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryManager.populateRegNamesVO() method starts for Registrant type :: " + registrantType);
		}
		String affiliateOptIn = BBBCoreConstants.BLANK;
		RegNamesVO regNamesVO = new RegNamesVO();
		
		regNamesVO.setNameAddrType(registrantType);
		regNamesVO.setFirstName(registrantVO.getFirstName());
		regNamesVO.setLastName(registrantVO.getLastName());
		regNamesVO.setDayPhone(getGiftRegUtils().getPhone(registrantVO.getPrimaryPhone()));
		regNamesVO.setEvePhone(getGiftRegUtils().getPhone(registrantVO.getCellPhone()));
		if (null != registrantVO.getContactAddress()) {
			regNamesVO.setCompany(registrantVO.getContactAddress().getCompany());
			regNamesVO.setAddress1(registrantVO.getContactAddress().getAddressLine1());
			regNamesVO.setAddress2(registrantVO.getContactAddress().getAddressLine2());
			regNamesVO.setCity(registrantVO.getContactAddress().getCity());
			regNamesVO.setState(registrantVO.getContactAddress().getState());
			regNamesVO.setZipCode(getGiftRegUtils().stripAllWhiteSpaceAndMakeUppercase(registrantVO.getContactAddress().getZip()));
		}
		regNamesVO.setEmailId(registrantVO.getEmail());
		regNamesVO.setDayPhoneExt(getGiftRegUtils().getPhoneExt(registrantVO.getPrimaryPhone()));
		regNamesVO.setEvePhoneExt(getGiftRegUtils().getPhoneExt(registrantVO.getCellPhone()));
		regNamesVO.setPrefContMeth(Integer.toString(registryVO.getPrefRegContMeth()));
		String preferredContactTime = BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_TIME;
		if (BBBGiftRegistryConstants.REG_SUB_TYPE.equalsIgnoreCase(registrantType) && !BBBUtility.isEmpty(registryVO.getPrefRegContTime())) {
			preferredContactTime = registryVO.getPrefRegContTime();
		} else if (BBBGiftRegistryConstants.COREG_SUB_TYPE.equalsIgnoreCase(registrantType) && !BBBUtility.isEmpty(registryVO.getPrefCoregContTime())) {
			preferredContactTime = registryVO.getPrefCoregContTime();
		}
		regNamesVO.setPrefContTime(preferredContactTime);
		if (BBBGiftRegistryConstants.REG_SUB_TYPE.equalsIgnoreCase(registrantType)) {
			regNamesVO.setEmailFlag(!BBBUtility.isEmpty(registryVO.getSignup()) ? registryVO.getSignup() : BBBCoreConstants.NO_CHAR);
		} else if (BBBGiftRegistryConstants.COREG_SUB_TYPE.equalsIgnoreCase(registrantType)) {
			regNamesVO.setEmailFlag(!BBBUtility.isEmpty(registryVO.getCoRegistrant().getCoRegEmailFlag()) ?
					registryVO.getCoRegistrant().getCoRegEmailFlag() : BBBCoreConstants.NO_CHAR);
		}
		
		regNamesVO.setMaiden(registrantVO.getBabyMaidenName());
		regNamesVO.setAtgProfileId(registrantVO.getProfileId());
		if (BBBGiftRegistryConstants.REG_SUB_TYPE.equalsIgnoreCase(registrantType) && (BBBGiftRegistryConstants.EVENT_TYPE_WEDDING.equalsIgnoreCase(registryVO.getRegistryType().getRegistryTypeName()) ||
				BBBGiftRegistryConstants.EVENT_TYPE_BABY.equalsIgnoreCase(registryVO.getRegistryType().getRegistryTypeName()))) {
			affiliateOptIn = registryVO.getAffiliateOptIn();
		}
		regNamesVO.setAffiliateOptIn(!BBBUtility.isEmpty(affiliateOptIn) ? affiliateOptIn : BBBCoreConstants.BLANK);
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryManager.populateRegNamesVO() method ends for Registrant type :: " + registrantType);
		}
		regNamesVOs.add(regNamesVO);
		
	}
	
	/**
	 * This method populates List<RegNamesVO> from RegistryVO for 
	 * Shipping Address and Future Shipping Address
	 * @param registryVO
	 * @return  List<RegNamesVO>
	 */

	
	public void populateShippingVO(RegistryVO registryVO, AddressVO addressVO, String addressType, List<RegNamesVO> regNamesVOs) {
		
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryManager.populateShippingVO() method starts for address type :: " + addressType);
		}
		RegNamesVO shippingVO = new RegNamesVO();
		shippingVO.setNameAddrType(addressType);
		shippingVO.setFirstName(addressVO.getFirstName());
		shippingVO.setLastName(addressVO.getLastName());
		shippingVO.setDayPhone(getGiftRegUtils().getPhone(addressVO.getPrimaryPhone()));
		if (!BBBUtility.isEmpty(addressVO.getAddressLine1())) {
			shippingVO.setCompany(addressVO.getCompany());
			shippingVO.setAddress1(addressVO.getAddressLine1());
			shippingVO.setAddress2(addressVO.getAddressLine2());
			shippingVO.setCity(((addressVO.getCity())));
			shippingVO.setState(((addressVO.getState())));
			shippingVO.setZipCode(getGiftRegUtils().stripAllWhiteSpaceAndMakeUppercase(addressVO.getZip()));
			shippingVO.setDayPhoneExt(getGiftRegUtils().getPhoneExt(addressVO.getPrimaryPhone()));
		}

		if (BBBGiftRegistryConstants.FU.equalsIgnoreCase(addressType)) {
			shippingVO.setAsOfDateFtrShipping(registryVO.getShipping().getFutureShippingDateWS());
		}
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryManager.populateRegNamesVO() method ends for Registrant type :: " + addressType);
		}
		regNamesVOs.add(shippingVO);
		
	}
	
	/**
	 * This method populates RegistryPrefStoreVO from RegistryVO for Preferred store selected
	 * @param registryVO
	 * @return  RegistryPrefStoreVO
	 */

	public RegistryPrefStoreVO populateRegPrefStoreVO(RegistryVO registryVO) {
		
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryManager.populateRegPrefStoreVO() method starts");
		}
		RegistryPrefStoreVO prefStoreVO = new RegistryPrefStoreVO();
		String prefStoreNumber = BBBCoreConstants.BLANK;
		String refStoreContactMethod = BBBCoreConstants.NO_CHAR;
		if (!BBBUtility.isEmpty(registryVO.getPrefStoreNum())) {
			prefStoreNumber = registryVO.getPrefStoreNum();
		}
		
		if (!BBBUtility.isEmpty(registryVO.getRefStoreContactMethod())) {
			refStoreContactMethod = registryVO.getRefStoreContactMethod();
		}
		prefStoreVO.setStoreNum(prefStoreNumber);
		prefStoreVO.setContactFlag(refStoreContactMethod);
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryManager.populateRegPrefStoreVO() method ends :: [ " + prefStoreVO + " ]");
		}
		return prefStoreVO;
	}
	
	/**
	 * This method vaidates the input RegistryVO for invalid characters 
	 * @param RegistryVO
	 * @return ErrorStatus
	 */

	public ErrorStatus validateInputForInvalidCharectars(RegistryVO registryVO) {
		
		ErrorStatus errorStatus = null;
		Object[] args = getGiftRegUtils().populateInputToLogErrorOrValidate(registryVO);
		errorStatus = getGiftRegUtils().validateInput((String[]) args);
		if (errorStatus != null && !errorStatus.isErrorExists()) {
			String[] decorTheme = {registryVO.getEvent().getBabyNurseryTheme()};
			errorStatus = getGiftRegUtils().validateInputForHackOnly(decorTheme);
		}
		return errorStatus;
	}

	public boolean validateInputForCreateOrUpdateRegistry(RegistryVO registryVO, RegistryHeaderVO registryHeaderVO, List<RegNamesVO> registrantsVOs,
			List<RegNamesVO> shippingVOs, RegistryBabyVO registryBabyVO, RegistryPrefStoreVO registryPrefStoreVO) throws BBBBusinessException, BBBSystemException {

		boolean isInputValid = false;
		List<ValidationError>validationErrors  = new ArrayList<ValidationError>();
		
		validationErrors.addAll(getGiftRegUtils().validateRegistryFields(registryVO.getSiteId(), registryBabyVO, 
				registryHeaderVO, registrantsVOs, shippingVOs, registryVO.isCreate()));
		validationErrors.addAll(getGiftRegUtils().validateRegPrefStore(registryPrefStoreVO));
		if (validationErrors.size() == BBBCoreConstants.ZERO) {
			isInputValid = true;
		} else {
			logError("Input validation failed. Result :: " + isInputValid + " Validation Errors :: " + validationErrors);
		}
		return isInputValid;
	
	}

	/**
	 * This method used to link registry with profile on the basis of email. Its
	 * used in CSR Forms.
	 * 
	 * @param linkRegistryRegVO
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public RegistryResVO linkRegistryToATGProfile(LinkRegistryToProfileVO linkRegistryRegVO) throws BBBBusinessException, BBBSystemException {
		this.logDebug("GiftRegistryManager.linkRegistryToATGProfile() method starts...");
		RegistryResVO linkRegistryResVO = null;
		final long registryNum = linkRegistryRegVO.getRegistryId();
		final String registrantType = linkRegistryRegVO.getRegCoreg();
		final String profileId = linkRegistryRegVO.getProfileId();
		final String email = linkRegistryRegVO.getEmailId();
		
		ErrorStatus valErrors = this.linkRegistryToATGProfileValidations(registrantType, profileId, email);	
		if(registryNum < 0 || valErrors.isErrorExists()) {
			linkRegistryResVO = new RegistryResVO();
			ServiceErrorVO errorVO = new ServiceErrorVO();
			errorVO.setErrorExists(true);
			errorVO.setErrorId(valErrors.getErrorId());
			errorVO.setErrorMessage(valErrors.getErrorMessage());
			errorVO.setErrorDisplayMessage(valErrors.getDisplayMessage());
			linkRegistryResVO.setServiceErrorVO(errorVO);
		} else {
			linkRegistryResVO = this.getGiftRegistryTools().linkRegistryToATGProfile(registryNum, registrantType, profileId, email);
}
		this.logDebug("GiftRegistryManager.linkRegistryToATGProfile() method end.");

		return linkRegistryResVO;
	}

	/**
	 * This method validates inputs from linkRegistryToATGProfile
	 * 
	 * @param registrantType
	 * @param profileId
	 * @param emailId
	 * @return
	 */
	private ErrorStatus linkRegistryToATGProfileValidations(final String registrantType, final String profileId, final String emailId) {
		if(!BBBGiftRegistryConstants.RE.equalsIgnoreCase(registrantType) && !BBBGiftRegistryConstants.CO.equalsIgnoreCase(registrantType)) {
			ErrorStatus registrantErrors = new ErrorStatus();
			registrantErrors.setErrorExists(true);
			registrantErrors.setErrorMessage(BBBGiftRegistryConstants.ERROR_INVALID_REGISTRANT_TYPE);
			registrantErrors.setDisplayMessage(BBBGiftRegistryConstants.ERROR_DISPLAY_REGISTRANT_TYPE);
			registrantErrors.setErrorId(BBBGiftRegistryConstants.VALIDATION);
			registrantErrors.setValidationErrors(null);
			return registrantErrors;
		}
		
		if(BBBUtility.isEmpty(profileId)) {
			ErrorStatus profileErrors = new ErrorStatus();
			profileErrors.setErrorExists(true);
			profileErrors.setErrorMessage(BBBGiftRegistryConstants.ERROR_INVALID_ATG_PROFILE);
			profileErrors.setDisplayMessage(BBBGiftRegistryConstants.ERROR_DISPLAY_ATG_PROFILE);
			profileErrors.setErrorId(BBBGiftRegistryConstants.VALIDATION);
			profileErrors.setValidationErrors(null);
			return profileErrors;
		}
		
		if(!BBBUtility.isEmpty(emailId) && !this.getGiftRegUtils().isValidEmail(emailId)) {
			ErrorStatus emailErrors = new ErrorStatus();
			emailErrors.setErrorExists(true);
			emailErrors.setErrorMessage(BBBGiftRegistryConstants.ERROR_EMAIL_INVALID_MSSG);
			emailErrors.setDisplayMessage(BBBGiftRegistryConstants.ERROR_EMAIL_INVALID_CHARS_MSSG);
			emailErrors.setErrorId(BBBGiftRegistryConstants.VALIDATION);
			emailErrors.setValidationErrors(null);
			return emailErrors;
		} else if(BBBUtility.isEmpty(emailId)) {
			ErrorStatus emailErrors = new ErrorStatus();
			emailErrors.setErrorExists(true);
			emailErrors.setErrorMessage(BBBGiftRegistryConstants.ERROR_EMAIL_INVALID_MSSG);
			emailErrors.setDisplayMessage(BBBGiftRegistryConstants.ERROR_EMAIL_EMPTY_MSSG);
			emailErrors.setErrorId(BBBGiftRegistryConstants.VALIDATION);
			emailErrors.setValidationErrors(null);
			return emailErrors;
		}
		
		// Now validate all input parameters for invalid characters.
		final String[] inputFields = { registrantType, profileId, emailId };
		ErrorStatus errorStatus = this.getGiftRegUtils().validateInput(inputFields);
		
		return errorStatus;
	}
	
	/**
	 * Service called from Mobile - Returns the notify json based on sku id to display the slider while adding to registry
	 *
	 * @param skuId the sku id
	 * @param regEventDate the reg event date
	 * @return the notify flag
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public JSONObject getNotifyInfo(String skuId, String regId, String regEventDate) throws BBBSystemException, BBBBusinessException{
		this.logDebug("GiftRegistryManager : getNotifyInfo method start");
		boolean isNotifyRegistrant = false;
		JSONObject jsonRootObject = new JSONObject();
		jsonRootObject.element(BBBCoreConstants.NOTIFY_CONSTANT, false);
		List<String> notifyRegFlagList = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.NOTIFY_REGISTRANT_FLAG);
		if (notifyRegFlagList != null && !notifyRegFlagList.isEmpty()){
			isNotifyRegistrant = Boolean.parseBoolean(notifyRegFlagList.get(0));
		}
		if(isNotifyRegistrant && !StringUtils.isBlank(skuId)){
			if(regEventDate.isEmpty()){
				regEventDate = getRegistryDate(ServletUtil.getCurrentRequest(), regId);
			}
			String displayMessage = getNotifyRegistrantMsgType(skuId, regEventDate);
			if(!StringUtils.isBlank(displayMessage)){
				jsonRootObject.element(BBBCoreConstants.NOTIFY_CONSTANT, true);
				jsonRootObject.element(BBBCoreConstants.DISPLAY_MESSAGE, displayMessage);
				this.logDebug("Notify user before add to registry");
			}
		}
		this.logDebug("GiftRegistryManager : getNotifyInfo method end");
		return jsonRootObject;          
	}

	/** This method returns the data of registry items from the stored procedure.
	 * @param eventTypeCode
	 * @param registryId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public List<RegistryItemVO> fetchRegItemsListByCategory(String eventTypeCode,
			String registryId) throws BBBBusinessException, BBBSystemException {
		logDebug("Entering populateCategoryInRegItemInfo ");
		logDebug("GiftRegistryManager fetchRegItemsListByCategory start : "+System.currentTimeMillis());
		String siteId=getBbbCatalogTools()
		.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,getSiteId()).get(0);
		
		Map<String, RegistryItemVO> guestRegistryListMap=new HashMap<String, RegistryItemVO>();
		List<RegistryItemVO> guestRegistryList=new ArrayList<RegistryItemVO>();
		
		try {
			guestRegistryListMap= getGiftRegistryTools().fetchRegistryItemsBasedOnCategory(siteId, eventTypeCode,registryId);
		} catch (SQLException e) {
			if(e.getMessage() !=null && e.getMessage().contains("Registry not found")){
				logInfo("GiftRegistryManager fetchRegItemsListByCategory:: Registry not found");
			}
			logError("SQLException occurred while fetchRegItemsListByCategory with registry Id :"+registryId,e);
		} catch (ParseException e) {
			logError("ParseException occurred while fetchRegItemsListByCategory with registry Id :"+registryId,e);
		}
		
		logDebug("GiftRegistryManager fetchRegItemsListByCategory end : "+System.currentTimeMillis());
		
		if(guestRegistryListMap !=null){
			guestRegistryList.addAll(guestRegistryListMap.values());
		}
		return guestRegistryList;
		
	}

	/** Populate instock,of stock maps and not in stock list. 
	 * @param categoryIdMap
	 * @param regItemsList
	 * @param regItemsInStockListByCategory
	 * @param regItemsOutOfStockListByCategory
	 * @param notInStockCategoryList
	 */
	@SuppressWarnings("rawtypes")
	public void populateCategoryMap(
			LinkedHashMap<String, String> categoryIdMap, List<RegistryItemVO> regItemsList,
			Map<String, GuestRegistryItemsListVO> regItemsInStockListByCategory,Map<String, GuestRegistryItemsListVO> regItemsOutOfStockListByCategory,
			List<String> notInStockCategoryList) {
		logDebug("Entering populateCategoryInRegItemInfo ");
		if(categoryIdMap ==null || regItemsList ==null){
			return;
		}
		List<RegistryItemVO> tempRegItemInStockList= new ArrayList<RegistryItemVO>();
		List<RegistryItemVO> tempRegItemOutOfStockList= new ArrayList<RegistryItemVO>();
		List<RegistryItemVO> tempRegItemList= new ArrayList<RegistryItemVO>();
		// Templist for those items that lie in categories that is not listed for this registry event code.
		for(RegistryItemVO regItemVO : regItemsList){
			tempRegItemList.add(regItemVO);
		}
		
		// Empty In Stock Map to maintain category listing 
		Iterator iterCat = categoryIdMap.entrySet().iterator();
	    while (iterCat.hasNext()) {
	        Map.Entry catId = (Map.Entry)iterCat.next();
	        String outCatId=(String) catId.getKey();
	        GuestRegistryItemsListVO guestRegistryListVO=new GuestRegistryItemsListVO();
	        guestRegistryListVO.setCategoryId(outCatId);
	        guestRegistryListVO.setDisplayName((String) catId.getValue());
	        guestRegistryListVO.setRegistryItemList(new ArrayList<RegistryItemVO>());
	        regItemsInStockListByCategory.put(outCatId, guestRegistryListVO);
	        
	    }
	    
	    // Empty out of stock list to maintain category listing 
	    iterCat = categoryIdMap.entrySet().iterator();
	    while (iterCat.hasNext()) {
	        Map.Entry catId = (Map.Entry)iterCat.next();
	        String outCatId=(String) catId.getKey();
	        GuestRegistryItemsListVO guestRegistryListVO=new GuestRegistryItemsListVO();
	        guestRegistryListVO.setCategoryId(outCatId);
	        guestRegistryListVO.setDisplayName((String) catId.getValue());
	        guestRegistryListVO.setRegistryItemList(new ArrayList<RegistryItemVO>());
	        regItemsOutOfStockListByCategory.put(outCatId, guestRegistryListVO);
	        
	    }
		Iterator<RegistryItemVO> regItemIter=regItemsList.iterator();
		// Iterate over the list of registry items
		while(regItemIter.hasNext()){
			RegistryItemVO regItemVO=regItemIter.next();
			//Set registry items eph id as other if not present
			if(BBBUtility.isEmpty(regItemVO.getEphCatId())){
				regItemVO.setEphCatId(BBBGiftRegistryConstants.OTHER);
			}
			Iterator<String> categoryListMapIter=categoryIdMap.keySet().iterator();
			// Iterate over category map and populate in stock and out of stock  map based on isAboveLine item and remove the item from temp list
			while(categoryListMapIter.hasNext()){
				String categoryId=categoryListMapIter.next();
				// If the item is LTL and is site has enable LTL flag as off : The item will fall in not in stock bucket.
				if(regItemVO.getEphCatId().equalsIgnoreCase(categoryId) && regItemVO.isAboveLine()){
					if(regItemsInStockListByCategory.containsKey(categoryId)){
						GuestRegistryItemsListVO guestRegistryItemsListVO=regItemsInStockListByCategory.get(categoryId);
						List<RegistryItemVO> registryItemList = guestRegistryItemsListVO.getRegistryItemList();
						registryItemList.add(regItemVO);
						regItemsInStockListByCategory.put(categoryId, guestRegistryItemsListVO);
					}
					tempRegItemList.remove(regItemVO);
					break;
				}else if(regItemVO.getEphCatId().equalsIgnoreCase(categoryId) && !regItemVO.isAboveLine()){
					if(regItemsOutOfStockListByCategory.containsKey(categoryId)){
						GuestRegistryItemsListVO guestRegistryItemsListVO=regItemsOutOfStockListByCategory.get(categoryId);
						List<RegistryItemVO> registryItemList = guestRegistryItemsListVO.getRegistryItemList();
						registryItemList.add(regItemVO);
						regItemsOutOfStockListByCategory.put(categoryId, guestRegistryItemsListVO);
					}
					tempRegItemList.remove(regItemVO);
					break;
				}
			}
		}
		
		// Populate remaining reg item which dont lie in any category  in the map
		if(tempRegItemList !=null && tempRegItemList.size() >0){
		Iterator<RegistryItemVO> iter=tempRegItemList.iterator();
		while(iter.hasNext()){
			RegistryItemVO regItem=iter.next();
			regItem.setEphCatId(BBBGiftRegistryConstants.OTHER);
			if(regItem.isAboveLine()){
				tempRegItemInStockList.add(regItem);
			}else{
				tempRegItemOutOfStockList.add(regItem);
			}
		}
		
		// Add the item in the corresponding map- instock and out of stock.
		GuestRegistryItemsListVO guestRegItemListVOOtherInStock=regItemsInStockListByCategory.get(BBBGiftRegistryConstants.OTHER);
		guestRegItemListVOOtherInStock.getRegistryItemList().addAll(tempRegItemInStockList);
        
		regItemsInStockListByCategory.put(BBBGiftRegistryConstants.OTHER, guestRegItemListVOOtherInStock);
		
		GuestRegistryItemsListVO guestRegItemListVOOtherOutOfStock=regItemsOutOfStockListByCategory.get(BBBGiftRegistryConstants.OTHER);
		guestRegItemListVOOtherOutOfStock.getRegistryItemList().addAll(tempRegItemOutOfStockList);
		
		regItemsOutOfStockListByCategory.put(BBBGiftRegistryConstants.OTHER, guestRegItemListVOOtherOutOfStock);
		}
		
		Iterator it = regItemsInStockListByCategory.entrySet().iterator();
		  List<String> tempCatList=new ArrayList<String>();
		 while(it.hasNext()){
			 Map.Entry pair = (Map.Entry)it.next();
	         GuestRegistryItemsListVO regItemListVO=(GuestRegistryItemsListVO) pair.getValue();
			  if(regItemListVO.getRegistryItemList() ==null || regItemListVO.getRegistryItemList().size()<1){
				  tempCatList.add(regItemListVO.getCategoryId());
			  }
		  }
		 regItemsInStockListByCategory.keySet().removeAll(tempCatList);
		// Remove all the remaining category in not in stock for removal.
		notInStockCategoryList.removeAll(regItemsInStockListByCategory.keySet());
		
	}
		
	public void populateSKUDetailsInRegItem(
			GuestRegistryItemsListVO guestRegistryItemsListVO, boolean isPerformInventoryCheck) {
		if(guestRegistryItemsListVO ==null || guestRegistryItemsListVO.getRegistryItemList() ==null){
			return;
		}
		List<RegistryItemVO> regItemList=guestRegistryItemsListVO.getRegistryItemList();
        for(RegistryItemVO regItem : regItemList){
        	populateSKUDetailsInRegItem(regItem,isPerformInventoryCheck);
        }
		
	}

	public void populateSKUDetailsInRegItem(
			RegistryItemVO registryItemVO,boolean isPerformInventoryCheck) {
		SKUDetailVO skuVO = null;
		
		if(registryItemVO ==null || registryItemVO.getSku()==0){
			return;
		}
		boolean checkInventory=true;
		if(!isPerformInventoryCheck){
			checkInventory=false;
		}else if(!registryItemVO.isAboveLine()){
			checkInventory=false;
		}
        
		try{
              skuVO = getGiftRegistryTools().getSKUDetailsWithProductId(
                    getSiteId(), Long.toString(registryItemVO.getSku()),
                    registryItemVO,checkInventory);
              
        } catch(BBBBusinessException | BBBSystemException be){
              this.logError("BBBBusinessException",be);
        }
		registryItemVO.setsKUDetailVO(skuVO);
		
	}

	public void populatePriceInfoInRegItem(
			GuestRegistryItemsListVO guestRegistryItemsListVO) {
		
		if(guestRegistryItemsListVO ==null || guestRegistryItemsListVO.getRegistryItemList() ==null){
			return;
		}
		List<RegistryItemVO> regItemList=guestRegistryItemsListVO.getRegistryItemList();
        for(RegistryItemVO regItem : regItemList){
        	getGiftRegistryTools().setPriceInRegItem(regItem,regItem.getsKUDetailVO().getParentProdId(),true);
        	if(BBBCoreConstants.PER.equalsIgnoreCase(regItem.getItemType())){
        		try {
        			double personalizedPrice=getGiftRegistryTools().calculatePersonalizedPrice(true,
							regItem.getCustomizedDoublePrice(),regItem.getPersonlisedDoublePrice(),regItem.getsKUDetailVO(), getSiteId());
        			regItem.setPersonlisedDoublePrice(personalizedPrice);
        			regItem.setPersonlisedPrice(BigDecimal
        					.valueOf(personalizedPrice));
				} catch (BBBSystemException e) {
					logError("Error populating price",e);
				}
			}
        }
		
	}
	
	

	/**
	 * Method to enable showing start browsing button on RLP based on some conditions
	 * @param listRegistryVo
	 * @return boolean
	 * @throws BBBSystemException
	 */
	public boolean enableBuyOffStartBrowsing(List<RegistryItemVO> listRegistryVo) throws BBBSystemException{
		String buyOffstartBrowsingKey=null;
		Boolean isbuyOffstartBrowsingt=false;
		int unfulfilledCount = 0;
		logDebug("RegistryItemsDisplayDroplet:enableBuyOffStartBrowsing - started");
		logDebug("Input RegistryVO list" + listRegistryVo);
			buyOffstartBrowsingKey = BBBConfigRepoUtils.getStringValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
					BBBCmsConstants.BUYOFF_START_BROWSING_KEY);
			if(null!=buyOffstartBrowsingKey && !buyOffstartBrowsingKey.isEmpty()){
				isbuyOffstartBrowsingt=Boolean.valueOf(buyOffstartBrowsingKey);
				logDebug(" Is buyoff start browsing key on ? "+isbuyOffstartBrowsingt);			
			}

			
			if(null != isbuyOffstartBrowsingt && (isbuyOffstartBrowsingt == true)){
				for(RegistryItemVO reVo : listRegistryVo){
					if(reVo.getQtyPurchased()< reVo.getQtyRequested()){
						unfulfilledCount+=1;
	    			}
				}
				
				final String itemSizeLimit = getGiftRegistryConfigurationByKey(BROWSE_ITEMPURCHASED_LIMIT);
			    final String totQtyPurchasedCountLimit = getGiftRegistryConfigurationByKey(BROWSE_UNFULLFILLED_SIZE_LIMIT);
				logDebug("Number of sku's ? "+listRegistryVo.size());	
				logDebug("Number of sku's limit ? "+itemSizeLimit);
				logDebug("Number of unfulfilled sku's ? "+ unfulfilledCount);	
				logDebug("Percentage unfulfilled sku's limit ? "+totQtyPurchasedCountLimit);
				logDebug("Percentage of unfulfilled sku's ? "+ (Integer.parseInt(totQtyPurchasedCountLimit)* listRegistryVo.size())*1.0/100.0);
				if(!BBBUtility.isEmpty(itemSizeLimit) && !BBBUtility.isEmpty(totQtyPurchasedCountLimit) && (unfulfilledCount <= Integer.parseInt(itemSizeLimit)) || (unfulfilledCount*1.0 <= ((Integer.parseInt(totQtyPurchasedCountLimit)* listRegistryVo.size())*1.0/100.0))){
					return true;
				}
				else
				{
					return false;
				}				
			}			
		return false;		
	}
	
	/**
	 * if the item is not exist in the content remove from the list
	 * @param listRegistryItemVO
	 * @return
	 */
	public List<RegistryItemVO> fliterNotAvliableItem(GuestRegistryItemsListVO guestRegistryItemsListVO)
	{
		if(guestRegistryItemsListVO ==null || guestRegistryItemsListVO.getRegistryItemList() ==null){
			return null;
		}
		List<RegistryItemVO> regItemList=guestRegistryItemsListVO.getRegistryItemList();
        return	fliterNotAvliableItem(regItemList);
	}

	public List<RegistryItemVO> fliterNotAvliableItem(List<RegistryItemVO> listRegistryItemVO )
	{
		if (null != listRegistryItemVO && listRegistryItemVO.size() > 0) {
			for (int index = listRegistryItemVO.size() - 1; index >= 0; --index) {

				RegistryItemVO registryItemVO = (RegistryItemVO) listRegistryItemVO
						.get(index);
				try{
					
				getCatalogTools().getParentProductForSku(
						String.valueOf(registryItemVO.getSku()), true);
				}
				catch (Exception exception)
				{
					listRegistryItemVO.remove(index);
				}
			}
			
		}
		return listRegistryItemVO;
	}
	
	/**
	 * Fetch first c1 data.This method iterates over the list of in stock category map.Checks the inventory (if flag is on). If the inventory is available 
	 * then populate in the category .Else move to out of stock list. As soon as category C1 is fetch populate  break the loop.
	 *
	 * @param regItemsInStockListByCategory the reg items in stock list by category
	 * @param regItemsOutOfStockListByCategory the reg items out of stock list by category
	 * @param isPerformInventoryCheck the is perform inventory check
	 * @return the guest registry items list vo
	 */
	@SuppressWarnings("rawtypes")
	public GuestRegistryItemsListVO fetchFirstC1Data(
			Map<String, GuestRegistryItemsListVO> regItemsInStockListByCategory,
			Map<String, GuestRegistryItemsListVO> regItemsOutOfStockListByCategory,
		   boolean isPerformInventoryCheck) {
	    boolean foundFirstC1=false;
	    Iterator it = regItemsInStockListByCategory.entrySet().iterator();
	    // Iterate over in stock list.
	    while(it.hasNext()){
	    	 Map.Entry pair = (Map.Entry)it.next();
	         GuestRegistryItemsListVO regItemList=(GuestRegistryItemsListVO) pair.getValue();
	         List<RegistryItemVO> tempRegItemList = new ArrayList<RegistryItemVO>();
	         // Create a temp list of items
	         for(RegistryItemVO regItem:regItemList.getRegistryItemList()){
	        	 tempRegItemList.add(regItem);
	         }
	         // filter not available events	  
	         fliterNotAvliableItem(tempRegItemList);
	         if(tempRegItemList!=null && tempRegItemList.size()>0){
	        	 // Fetch SKU Detail VO
	        	 for(RegistryItemVO regItem:tempRegItemList){
	        		 String skuId=String.valueOf(regItem.getSku());
	        	    SKUDetailVO skuDetailVO=null;
					try {
						skuDetailVO = getGiftRegistryTools().getSKUDetailsWithProductId(getSiteId(), skuId, regItem,isPerformInventoryCheck);
					} catch (BBBSystemException | BBBBusinessException e) {
						logError(" Error Occured while fetching skudetails",e);
						regItemList.getRegistryItemList().remove(regItem);
					}
					regItem.setsKUDetailVO(skuDetailVO);
					// If in stock available.Update foundFirstC1 available and add to the list.
					if(skuDetailVO != null && skuDetailVO.isSkuInStock()){
					foundFirstC1=true;
					}else{
					// Update the out of stock map in case inventory is not available.
						GuestRegistryItemsListVO guestRegistryItemsListVO=new GuestRegistryItemsListVO();
						regItem.setAboveLine(false);
						regItem.setIsBelowLineItem(BBBCoreConstants.TRUE);
						if(regItemsOutOfStockListByCategory.containsKey((String) pair.getKey())){
							guestRegistryItemsListVO=regItemsOutOfStockListByCategory.get((String) pair.getKey());
							List<RegistryItemVO> registryItemList = guestRegistryItemsListVO.getRegistryItemList();
							registryItemList.add(regItem);
						}
						regItemsOutOfStockListByCategory.put((String) pair.getKey(), guestRegistryItemsListVO);
						// Remove out of stock item.
						regItemList.getRegistryItemList().remove(regItem);
					}
	        	 }
	        	 if(foundFirstC1 && !regItemList.getRegistryItemList().isEmpty()){
	        		 GuestRegistryItemsListVO firstC1= new GuestRegistryItemsListVO();
	        		 firstC1.setCategoryId((String) pair.getKey());
	        		 firstC1.setRegistryItemList(regItemList.getRegistryItemList());
	        		 return firstC1;
	        	 }
	         }
	    }
		return null;
	}

	/**
	 * Sets the ltl attributes in reg item.
	 *
	 * @param siteId the site id
	 * @param regEventDate the reg event date
	 * @param isNotifyRegistrant the is notify registrant
	 * @param registryItemsList the registry items list
	 * @param reVo the re vo
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public void setLTLAttributesInRegItem( String regEventDate,
			boolean isNotifyRegistrant, List<RegistryItemVO> registryItemsList,
			RegistryItemVO reVo) throws BBBSystemException,
			BBBBusinessException {
		
		logDebug("Setting LTL attribute in registry item for sku " + reVo.getSku());
		Long skuId = reVo.getSku();
		String sku="";
		String siteId=getSiteId();
		if(skuId>0){
			sku=Long.toString(skuId);
		}				
		if(!BBBUtility.isEmpty(reVo.getPersonalisedCode())){
			reVo.setPersonalizationOptionsDisplay(getBbbEximPricingManager().getPersonalizedOptionsDisplayCode(reVo.getPersonalisedCode()));
		}
		/* changes starts for story : BBBP-4572 : Notify Registrant (Show Message) while adding N & D status items */
		if (isNotifyRegistrant && ! siteId.contains("TBS")){				
			String displayMessageType = getNotifyRegistrantMsgType(sku, regEventDate);
			reVo.setDisplayNotifyRegistrantMsg(displayMessageType);
		}	
		/* changes ends for story : BBBP-4572 : Notify Registrant (Show Message) while adding N & D status items */
		
		if(BBBUtility.isNotEmpty(reVo.getRefNum()) && BBBCoreConstants.MINUS_ONE.equalsIgnoreCase(reVo.getRefNum().trim())){
			reVo.setRefNum(BBBCoreConstants.BLANK);
		}
		if(BBBUtility.isNotEmpty(reVo.getLtlDeliveryServices())){
			reVo.setDeliverySurcharge(getCatalogTools().getDeliveryCharge(
					getSiteId(), sku,
					reVo.getLtlDeliveryServices()));
			if((BBBUtility.isNotEmpty(reVo.getAssemblySelected()) && reVo.getAssemblySelected().equalsIgnoreCase(BBBCoreConstants.YES_CHAR)) 
					|| reVo.getLtlDeliveryServices().equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)){
				//set in transient property of gift list
				boolean isShippingMethodExistsForSku = getCatalogTools()
						.isShippingMethodExistsForSku(siteId, sku,
								BBBCoreConstants.LW, true);
				reVo.setShipMethodUnsupported(!isShippingMethodExistsForSku);
				reVo.setAssemblyFees(getCatalogTools().getAssemblyCharge(getSiteId(), sku));
				 RepositoryItem shippingMethod = getCatalogTools().getShippingMethod(reVo.getLtlDeliveryServices().trim());
				 String shippingMethodDesc = ((String) shippingMethod.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION)).trim();
				 reVo.setLtlShipMethodDesc(shippingMethodDesc+BBBGiftRegistryConstants.WITH_ASSEMBLY);
				 reVo.setLtlDeliveryServices(shippingMethod.getRepositoryId().trim()+BBBCoreConstants.A);
				}
			else{
				boolean isShippingMethodExistsForSku = getCatalogTools()
						.isShippingMethodExistsForSku(siteId, sku,
								reVo.getLtlDeliveryServices(), false);
				reVo.setShipMethodUnsupported(!isShippingMethodExistsForSku);
				 RepositoryItem shippingMethod = getCatalogTools().getShippingMethod(reVo.getLtlDeliveryServices().trim());
				 String shippingMethodDesc = ((String) shippingMethod.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION)).trim();
				 reVo.setLtlShipMethodDesc(shippingMethodDesc);
				 reVo.setLtlDeliveryServices(reVo.getLtlDeliveryServices().trim());
			}
		}
		
		if(null != reVo.getItemType() && BBBCoreConstants.LTL.equalsIgnoreCase(reVo.getItemType()))
		{
			int noOfSameSku = Collections.frequency(getLTLskuList(registryItemsList), sku);
			if(noOfSameSku == 1 && reVo.getQtyPurchased() == 0)
			{
				if (BBBUtility.isEmpty(reVo.getLtlDeliveryServices())
						|| BBBUtility.isNotEmpty(reVo.getLtlDeliveryServices())
						&& reVo.isShipMethodUnsupported()) {
					reVo.setDSLUpdateable(true);
				}
			}
		}
	}
	
	public List<String> getLTLskuList(List<RegistryItemVO> registryItemList)
	{
		ArrayList<String> arrList = new ArrayList<String>();
		for(RegistryItemVO reVo : registryItemList)
		{
			if(BBBCoreConstants.LTL.equalsIgnoreCase(reVo.getItemType()))
			{
				arrList.add(String.valueOf(reVo.getSku()));
			}
		}
		return arrList;
	}

	public void setLTLAttributesInRegItem(GuestRegistryItemsListVO guestRegistryItemsListVO,String eventDate) {
		
		if(guestRegistryItemsListVO ==null || guestRegistryItemsListVO.getRegistryItemList()==null || guestRegistryItemsListVO.getRegistryItemList().size()<1){
			return;
		}
		boolean isNotifyRegistrant = false;
		String notifyRegFlag = BBBConfigRepoUtils.getStringValue(
					BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
					BBBGiftRegistryConstants.NOTIFY_REGISTRANT_FLAG);
		if (notifyRegFlag != null && !notifyRegFlag.isEmpty()){
			isNotifyRegistrant = Boolean.parseBoolean(notifyRegFlag);
		}
		for(RegistryItemVO regItemVO:guestRegistryItemsListVO.getRegistryItemList()){
		if(BBBCoreConstants.LTL.equalsIgnoreCase(regItemVO.getItemType()) ||BBBCoreConstants.PER.equalsIgnoreCase(regItemVO.getItemType())){	
		try {
			setLTLAttributesInRegItem(eventDate, isNotifyRegistrant, guestRegistryItemsListVO.getRegistryItemList(), regItemVO);
		} catch (BBBSystemException | BBBBusinessException e) {
			logError(" BBBSystemException | BBBBusinessException Occured while setLTLAttributesInRegItem",e);
		}
		}
		}
	}

	public List<RegistryItemVO> removeRegItemsBasedOnFilter(List<RegistryItemVO> registryItemVOList,String regView) {
		List<RegistryItemVO> registryItemVOs =new ArrayList<RegistryItemVO>();
		Iterator<RegistryItemVO> iterRegItem=registryItemVOList.iterator();
		while(iterRegItem.hasNext()){
			RegistryItemVO registryItemVO = iterRegItem.next();
			int qtyRequested = registryItemVO.getQtyRequested();
			int qtyFulfilled = registryItemVO.getQtyFulfilled();
			int qtyWebPurchased = registryItemVO
					.getQtyWebPurchased();
			int qtyPurchased = qtyFulfilled + qtyWebPurchased;
			if (regView
							.equalsIgnoreCase(BBBCoreConstants.VIEW_REMAINING) && (qtyRequested > qtyPurchased)
					|| (regView
							.equalsIgnoreCase(BBBCoreConstants.VIEW_PURCHASED) && (qtyRequested <= qtyPurchased))) {
				registryItemVOs.add(registryItemVO);
			}
		}
		return registryItemVOs;
	}

	public void personlizeImageUrl(List<RegistryItemVO> registryItemList) {
		if(registryItemList==null){
			return;
		}
		
		List<String> moderateKeyValueList = new ArrayList<String>();
		try {
			moderateKeyValueList = getCatalogTools().getAllValuesForKey(
					BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,
					BBBCoreConstants.MODERATED);
		} catch (BBBSystemException | BBBBusinessException e) {
			logError(" Error Occured while fetching personlizeImageUrl",e);
		}
		
		String moderateKeyValue = "";
		if (null != moderateKeyValueList && !(moderateKeyValueList.isEmpty())) {
			moderateKeyValue = moderateKeyValueList.get(0);
		}
		for(RegistryItemVO regItemVO:registryItemList){
			if(BBBCoreConstants.PER.equalsIgnoreCase(regItemVO.getItemType())){
				getGiftRegistryTools().personlizeImageUrl(regItemVO,moderateKeyValue);
			}
		}
	}

	/** Remove the items from in stock list and add in out of stock in case of inventory not available.
	 * @param giftGiverOutOfStockRegItemsMap
	 * @param registryItemsListVO
	 * @param isPerformInventoryCheck
	 */
	public void removeOutOfStockItems(
			Map<String, GuestRegistryItemsListVO> giftGiverOutOfStockRegItemsMap,
			GuestRegistryItemsListVO registryItemsListVO, boolean isPerformInventoryCheck) {
        if(registryItemsListVO ==null){
        	return;
        }
        
        List<RegistryItemVO> regItemList = registryItemsListVO.getRegistryItemList();
        
        fliterNotAvliableItem(regItemList);
        List<RegistryItemVO> tempRegItemList = new ArrayList<RegistryItemVO>();	  
      
        for(RegistryItemVO regItemVO :regItemList){
        	tempRegItemList.add(regItemVO);
        }
	       
	         if(tempRegItemList!=null && tempRegItemList.size()>0){
	        	 for(RegistryItemVO regItem:tempRegItemList){
	        		 String skuId=String.valueOf(regItem.getSku());
	        	    SKUDetailVO skuDetailVO=null;
					try {
						skuDetailVO = getGiftRegistryTools().getSKUDetailsWithProductId(getSiteId(), skuId, regItem,isPerformInventoryCheck);
					} catch (BBBSystemException | BBBBusinessException e) {
						logError(" Error Occured while fetching skudetails",e);
						registryItemsListVO.getRegistryItemList().remove(regItem);
					}
					regItem.setsKUDetailVO(skuDetailVO);
					// Update out of stock maps
					if(skuDetailVO!=null && !skuDetailVO.isSkuInStock()){
						GuestRegistryItemsListVO guestRegistryItemsListVO=new GuestRegistryItemsListVO();
						regItem.setAboveLine(false);
						regItem.setIsBelowLineItem(BBBCoreConstants.TRUE);
						if(giftGiverOutOfStockRegItemsMap.containsKey(registryItemsListVO.getCategoryId())){
							guestRegistryItemsListVO=giftGiverOutOfStockRegItemsMap.get(registryItemsListVO.getCategoryId());
							List<RegistryItemVO> registryItemList = guestRegistryItemsListVO.getRegistryItemList();
							registryItemList.add(regItem);
						}
						giftGiverOutOfStockRegItemsMap.put(registryItemsListVO.getCategoryId(), guestRegistryItemsListVO);
						regItemList.remove(regItem);
					}
	        	 }
	         }
	    }
}
