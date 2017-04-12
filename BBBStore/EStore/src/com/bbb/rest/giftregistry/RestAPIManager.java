package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.GenericFormHandler;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.ProfileServices;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.cms.PromoBoxVO;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.browse.droplet.BreadcrumbDroplet;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.RegistryCategoryMapVO;
import com.bbb.commerce.checklist.manager.CheckListManager;
import com.bbb.commerce.checklist.tools.CheckListTools;
import com.bbb.commerce.checklist.vo.CheckListVO;
import com.bbb.commerce.checklist.vo.MyItemCategoryVO;
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.droplet.AddItemToGiftRegistryDroplet;
import com.bbb.commerce.giftregistry.droplet.DateCalculationDroplet;
import com.bbb.commerce.giftregistry.droplet.GiftRegistryFlyoutDroplet;
import com.bbb.commerce.giftregistry.droplet.MyRegistriesDisplayDroplet;
import com.bbb.commerce.giftregistry.droplet.POBoxValidateDroplet;
import com.bbb.commerce.giftregistry.droplet.RegistryEventDateComparator;
import com.bbb.commerce.giftregistry.droplet.RegistryInfoDisplayDroplet;
import com.bbb.commerce.giftregistry.droplet.RegistryItemsDisplayDroplet;
import com.bbb.commerce.giftregistry.formhandler.GiftRegistryFormHandler;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.manager.GiftRegistryRecommendationManager;
import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.AppRegistryInfoDetailVO;
import com.bbb.commerce.giftregistry.vo.EventVO;
import com.bbb.commerce.giftregistry.vo.ProfileRegistryListVO;
import com.bbb.commerce.giftregistry.vo.RegistrantVO;
import com.bbb.commerce.giftregistry.vo.RegistryCategoriesVO;
import com.bbb.commerce.giftregistry.vo.RegistryCategoryBucketVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO;
import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.commerce.giftregistry.vo.RestRegistryInfoDetailVO;
import com.bbb.commerce.giftregistry.vo.ShippingVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/** This class is used to get users registry information for REST module.
 *
 * @author sku134 */
public class RestAPIManager extends BBBGenericService {

    /** The MyRegistriesDisplayDroplet instance */

    private MyRegistriesDisplayDroplet myRegistryDroplet;

    /** The GiftRegistryFlyoutDroplet instance */

    private GiftRegistryFlyoutDroplet giftRegistryFlyoutDroplet;
    /** The Gift reg mgr. */
    private GiftRegistryManager mGiftRegMgr;
    private SiteContext mSiteContext;
    private RegistryItemsDisplayDroplet registryItemDroplet;
    private RegistryInfoDisplayDroplet registryInfoDroplet;
    private POBoxValidateDroplet poBoxValidateDroplet;
    private Profile profile;
    private GiftRegistryTools giftRegistryTools;
    private GiftRegistryRecommendationManager giftRegistryRecommendationManager;
    private static final String SESSION_BEAN = "/com/bbb/profile/session/SessionBean";
    private final String ERR_NO_REG_INFO = "err_no_reg_info";
    private final String ERR_INVALID_REG_INFO_REQ = "err_invalid_reg_info_req";
    private final String ERR_REGINFO_FATAL_ERROR = "err_gift_reg_fatal_error";
    private final String ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR = "err_gift_reg_siteflag_usertoken_error";
    private final String ERR_REGINFO_INVALID_INPUT_FORMAT = "err_gift_reg_invalid_input_format";
    /** Constants for string literal profile. */
    private static final String PROFILE_CONSTANT = "profile";
    /** Constants for string literal site id. */
    private static final String SITE_ID = "siteId";
    public static final String OUTPUT_ERROR_MSG = "errorMsg";
    private PriceListManager priceListManager;
    private BBBCatalogTools catalogTools;
    private AddItemToGiftRegistryDroplet addItemToGiftRegistryDroplet;
    private static final String ERROR_INVALID_INPUT = "err_invalid_input";
    private static final String ERROR_REGISTRY_INFO = "err_fetching_registry_Info";
    private static final String ERROR_REGISTRY_DETAIL = "error_registry_detail";
    private final String ERR_INVALID_HEADERS = "err_invalid_request_headers";
    private BreadcrumbDroplet breadcrumbDroplet; 
    private final String GIFT_REGISTRY_FORMHANDLER_PATH = "/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler";
    private final String profileFormHandlerPath = "/atg/userprofiling/ProfileFormHandler";
    private final String CHANNEL_INFO = "channelInfo";
    private Repository siteRepository;	
    private Repository channelRepository;
    private BBBInventoryManager inventoryManager;
    private ProfileServices profileServices;
    public ProfileServices getProfileServices() {
		return profileServices;
	}

	public void setProfileServices(ProfileServices profileServices) {
		this.profileServices = profileServices;
	}

    private CheckListManager checkListManager;
    
    public CheckListManager getCheckListManager() {
		return checkListManager;
	}

	public void setCheckListManager(CheckListManager checkListManager) {
		this.checkListManager = checkListManager;
	}

	public Repository getChannelRepository() {
		return channelRepository;
	}

	public void setChannelRepository(Repository channelRepository) {
		this.channelRepository = channelRepository;
	}

	public Repository getSiteRepository() {
		return siteRepository;
	}

	public void setSiteRepository(Repository siteRepository) {
		this.siteRepository = siteRepository;
	}

	private final String ERROR_EXISTS = "errorExists";
    private final String RESULT = "result";
    
    private ProfileTools profileTools;
    private LblTxtTemplateManager lblTxtTemplateManager;
    
    public final String REGISTRY_EVENT_TYPE = "registryEventType";
	public final String EVENT_DATE ="eventDate";
	public final String BABY_NURSERY_THEME = "babyNurseryTheme";
	public final String COLLEGE = "college";
	public final String GUEST_COUNT ="guestCount";
	public final String PRIMARY_REG_BABY_MAIDEN_NAME = "primaryRegBabyMaidenName";
	public final String BABY_GENDER = "babyGender";
	public final String BABY_NAME ="babyName";
	public final String BIRTH_DATE = "birthDate";
	public final String SHOWER_DATE ="showerDate";
	public final String PRIMARY_REG_FIRST_NAME = "primaryRegistrantFirstName";
	public final String PRIMARY_REG_PHONE = "primaryRegistrantPhone";
	public final String PRIMARY_REG_LAST_NAME = "primaryRegistrantLastName";
	public final String PRIMARY_REG_EMAIL = "primaryRegistrantEmail";
	public final String REGISTRY_TYPE_NAME = "registryTypeName";
	public final String REG_CONTACT_ADDRESS = "regContactAddress";
	public final String PRIMARY_REG_CELL_PHONE = "primaryRegistrantCellPhone";
	public final String PRIMARY_REG_ADD_FIRST_NAME = "primaryRegContactAddFName";
	public final String PRIMARY_REG_ADD_LAST_NAME = "primaryRegContactAddLName";
	public final String PRIMARY_REG_ADD_LINE_1 = "primaryRegContactAddLine1";
	public final String PRIMARY_REG_ADD_LINE_2 = "primaryRegContactAddLine2";
	public final String PRIMARY_REG_ADD_CITY = "primaryRegContactAddCity";
	public final String PRIMARY_REG_ADD_STATE = "primaryRegContactAddState";
	public final String PRIMARY_REG_ADD_ZIP = "primaryRegContactAddZip";
	public final String CO_REG_FIRST_NAME = "coRegFName";
	public final String CO_REG_LAST_NAME = "coRegLName";
	public final String CO_REG_EMAIL = "coRegEmail";
	public final String CO_REG_BABY_MAIDEN_NAME = "coRegBabyMaidenName";
	public final String CO_REG_EMAIL_STATUS = "coRegEmailStatus";
	public final String SHIPPING_ADDRESS = "shippingAddress";
	public final String SHIPPING_ADDRESS_FIRST_NAME = "shippingAddressFName";
	public final String SHIPPING_ADDRESS_LAST_NAME = "shippingAddressLName";
	public final String SHIPPING_ADDRESS_LINE_1 = "shippingAddressLine1";
	public final String SHIPPING_ADDRESS_LINE_2 = "shippingAddressLine2";
	public final String SHIPPING_ADDRESS_CITY = "shippingAddressCity";
	public final String SHIPPING_ADDRESS_STATE = "shippingAddressState";
	public final String SHIPPING_ADDRESS_ZIP = "shippingAddressZip";
	public final String OPT_IN_WEDDING_OR_BUMP = "optInWeddingOrBump";
	public final String FUTURE_SHIP_SELECTED="futureShipDateSelected";
	public final String FUTURE_SHIPPING_DATE = "futureShippingDate";
	public final String FUTURE_SHIPPING_ADDRESS ="futureShippingAddress";
	public final String FUTURE_SHIPPING_FIRST_NAME = "futureShippingAddFName";
	public final String FUTURE_SHIPPING_LAST_NAME = "futureShippingAddLName";
	public final String FUTURE_SHIPPING_ADD_LINE_1 ="futureShippingAddLine1";
	public final String FUTURE_SHIPPING_ADD_LINE_2="futureShippingAddLine2";
	public final String FUTURE_SHIPPING_ADD_ZIP ="futureShippingAddZip";
	public final String FUTURE_SHIPPING_ADD_CITY ="futureShippingAddCity";
	public final String FUTURE_SHIPPING_ADD_STATE ="futureShippingAddState";
	public final String REF_STORE_CONTACT_METHOD = "refStoreContactMethod";
	public final String PREF_STORE_NUM ="prefStoreNum";
	private final String EMAIL = "email";
	private final String PASSWORD = "password";
	private final String CONFIRM_Password = "confirmPassword";
	private final String FIRST_NAME = "firstName";
	private final String LAST_NAME = "lastName";
	private final String NETWORK_AFFILIATION ="networkAffiliation";
	private final String SITE_CONFIGURATION = "siteConfiguration";
	private final String SITE_HEADER = "X-bbb-site-id";
	private final String MOBILE_NUMBER = "mobileNumber";
	private final String PHONE_NUMBER = "phoneNumber";
	private final String EMAIL_OPTIN = "emailOptIn";
	private final String SHARE_CHECKBOX_ENABLED = "shareCheckBoxEnabled";
	private final String ERR_CREATE_REG_BABY_GENDER = "err_create_reg_baby_gender";
	public final String CHANNEL_HEADER = "X-bbb-channel";
	private CheckListTools checkListTools;
    private MutableRepository checkListRepository;
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

    public ProfileTools getProfileTools() {
		return profileTools;
	}

	public void setProfileTools(ProfileTools profileTools) {
		this.profileTools = profileTools;
	}

    public AddItemToGiftRegistryDroplet getAddItemToGiftRegistryDroplet() {
        return this.addItemToGiftRegistryDroplet;
    }

    public void setAddItemToGiftRegistryDroplet(final AddItemToGiftRegistryDroplet addItemToGiftRegistryDroplet) {
        this.addItemToGiftRegistryDroplet = addItemToGiftRegistryDroplet;
    }

    /** @return the catalogTools */
    public BBBCatalogTools getCatalogTools() {
        return this.catalogTools;
    }

    /** @param catalogTools the catalogTools to set */
    public void setCatalogTools(final BBBCatalogTools catalogTools) {
        this.catalogTools = catalogTools;
    }

    public PriceListManager getPriceListManager() {
        return this.priceListManager;
    }

    public void setPriceListManager(final PriceListManager priceListManager) {
        this.priceListManager = priceListManager;
    }

    DateCalculationDroplet dateCalculationDroplet;

    public RegistryItemsDisplayDroplet getRegistryItemDroplet() {
        return this.registryItemDroplet;
    }

    public void setRegistryItemDroplet(final RegistryItemsDisplayDroplet registryItemDroplet) {
        this.registryItemDroplet = registryItemDroplet;
    }

    public RegistryInfoDisplayDroplet getRegistryInfoDroplet() {
        return this.registryInfoDroplet;
    }

    public void setRegistryInfoDroplet(final RegistryInfoDisplayDroplet registryInfoDroplet) {
        this.registryInfoDroplet = registryInfoDroplet;
    }

    public Profile getProfile() {
        return (Profile) ServletUtil.getCurrentRequest().resolveName("/atg/userprofiling/Profile");
    }

    public void setProfile(final Profile profile) {
        this.profile = profile;
    }

    public DateCalculationDroplet getDateCalculationDroplet() {
        return this.dateCalculationDroplet;
    }

    public void setDateCalculationDroplet(final DateCalculationDroplet dateCalculationDroplet) {
        this.dateCalculationDroplet = dateCalculationDroplet;
    }

    /** get Registries information for the profile
     *
     * @return RegistrySummaryVO
     * @throws BBBBusinessException
     * @throws BBBSystemException */
    @SuppressWarnings ({ "unchecked", "rawtypes" })
    public List getRegistryInfoForProfileID() throws BBBBusinessException, BBBSystemException {
        this.logDebug("Inside class  : RestAPIManager  method :getRegistryInfoForProfileID");

        List<RegistrySummaryVO> pRegistryVO = null;
        final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
        final DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
        final Profile user = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
        pRequest.setParameter(PROFILE_CONSTANT, user);
        pRequest.setParameter(SITE_ID, this.getSiteContext().getSite().getId());
        String errorMsg = null;
        try {
            this.getMyRegistryDroplet().service(pRequest, response);
            errorMsg = (String) pRequest.getObjectParameter(OUTPUT_ERROR_MSG);
            if (errorMsg != null) {
            	//Log Error and set registry VO as null rather than throwing Exception - [To behave same as in Desktop i.e. If registry webservice throw error
            	//Then, Show create registry only and log error in logs. ]
            	this.logError("RestAPIManager.getRegistryInfoForProfileID():: Error while fetching registry web service: " + errorMsg);
            	pRegistryVO = null;
            } else {

                pRegistryVO = (List<RegistrySummaryVO>) pRequest
                                .getObjectParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO);

                if (null != pRegistryVO) {
                    for (final RegistrySummaryVO registrySummaryVO : pRegistryVO) {
                    	if(!BBBUtility.isEmpty(registrySummaryVO.getEventDate()) && !(registrySummaryVO.getEventDate()).equalsIgnoreCase("0")){
                        pRequest.setParameter("eventDate", registrySummaryVO.getEventDate());
                        this.getDateCalculationDroplet().service(pRequest, response);
                        registrySummaryVO.setDaysToGo(Integer.parseInt(pRequest.getParameter("daysToGo")));
                       final Boolean eventYetToCome = (Boolean.parseBoolean(pRequest.getParameter("check")));
                        registrySummaryVO.setEventYetToCome(eventYetToCome);
                        if (!eventYetToCome) {
                            registrySummaryVO.setDaysToNextCeleb(Integer.parseInt(pRequest
                                            .getParameter("daysToNextCeleb")));                       
                        }
                    	}
                    }
                }
            }

        } catch (final IOException e) {
        	this.logError(" RestAPIManager.getRegistryInfoForProfileID():: " + BBBCatalogErrorCodes.REGISTRY_INFO_EXCEPTION_IO_EXCEPTION + 
                    " - IO Exception in Registry Detail");

        } catch (final ServletException e) {
        	this.logError(" RestAPIManager.getRegistryInfoForProfileID():: " + BBBCatalogErrorCodes.REGISTRY_INFO_EXCEPTION_SERVLET_EXCEPTION + 
                    " - Servlet Exception in Registry Detail");

        }
        this.logDebug("Return class  : RestAPIManager  method :getRegistryInfoForProfileID" + pRegistryVO);

        return pRegistryVO;

    }

    public MyRegistriesDisplayDroplet getMyRegistryDroplet() {
        return this.myRegistryDroplet;
    }

    public void setMyRegistryDroplet(final MyRegistriesDisplayDroplet myRegistryDroplet) {
        this.myRegistryDroplet = myRegistryDroplet;
    }

    @SuppressWarnings ({ "rawtypes", "unchecked" })
    public List getRegistryListForProfile() throws BBBSystemException {
        this.logDebug("Inside class  : RestAPIManager  method :getRegistryListForProfile starting");

        final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
        final DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
        pRequest.setParameter(SITE_ID, getSiteId());
        try {
            this.getAddItemToGiftRegistryDroplet().service(pRequest, pResponse);
        } catch (final IOException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.REGISTRY_INFO_EXCEPTION_IO_EXCEPTION,
                            "IO Exception in Registry List for Profile");

        } catch (final ServletException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.REGISTRY_INFO_EXCEPTION_SERVLET_EXCEPTION,
                            "Servlet Exception in Registry List for Profile");

        }

        // BBBI-5262
        List<RegistrySkinnyVO> registrySkinnyVOList = (List<RegistrySkinnyVO>) pRequest.getObjectParameter("registrySkinnyVOList");
        if (registrySkinnyVOList == null) {
	        BBBSessionBean sessionBean = (BBBSessionBean) pRequest
					.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
	        registrySkinnyVOList = (List<RegistrySkinnyVO>) sessionBean.getValues().get("registrySkinnyVOList");
	        if (registrySkinnyVOList == null) {
	        	Profile profile = (Profile) pRequest
						.resolveName(BBBCoreConstants.ATG_PROFILE);
				if (!profile.isTransient()) {
					 try {
						registrySkinnyVOList = getGiftRegistryManager()
								.getAcceptableGiftRegistries(profile, getSiteId());
					} catch (BBBBusinessException | RepositoryException e) {
						  this.logError("RestAPIManager  method :getRegistryListForProfile BBBBusinessException exception" +e);
					}
					 if(registrySkinnyVOList != null){
						RegistryEventDateComparator eventDateComparator = new RegistryEventDateComparator();
						eventDateComparator.setSortOrder(2);
						Collections.sort(registrySkinnyVOList, eventDateComparator);
						sessionBean.getValues().put("size", registrySkinnyVOList.size());
					 }
					sessionBean.getValues().put("registrySkinnyVOList", registrySkinnyVOList);
					
				}
	           // registrySkinnyVOList = (List<RegistrySkinnyVO>) sessionBean.getValues().get("registrySkinnyVOList");
	        }
        }

        this.logDebug("RestAPIManager  method :getRegistryListForProfile exiting");
        return registrySkinnyVOList;
    }

    /** This is rest method to fetch all the associated Registries
     * Own and Recommended for the profile
    *
    * @return ProfileRegistryListVO
    * @throws BBBSystemException 
     * @throws BBBBusinessException 
    * 
    * */
    
    @SuppressWarnings ({ "rawtypes", "unchecked" })
    public ProfileRegistryListVO getOwnAndRecommendedRegistriesForProfile(String skuId) throws BBBSystemException, BBBBusinessException {
        this.logDebug("Inside class  : RestAPIManager  method [getOwnAndRecommendedRegistriesForProfile] starting");

        final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
        final DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
        ProfileRegistryListVO profileRegistryListVO = new ProfileRegistryListVO();
        pRequest.setParameter(SITE_ID, getSiteId());
        try {
        	
            this.getAddItemToGiftRegistryDroplet().service(pRequest, pResponse);
            
        } catch (final IOException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.REGISTRY_INFO_EXCEPTION_IO_EXCEPTION,
                            "IO Exception in Registry List for Profile");

        } catch (final ServletException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.REGISTRY_INFO_EXCEPTION_SERVLET_EXCEPTION,
                            "Servlet Exception in Registry List for Profile");

        }

        // BBBI-5262
        List<RegistrySkinnyVO> registrySkinnyVOList = (List<RegistrySkinnyVO>) pRequest.getObjectParameter("registrySkinnyVOList");
        if (registrySkinnyVOList == null) {
	        BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
	        if (sessionBean.getValues().get("registrySkinnyVOList") != null) {
	            registrySkinnyVOList = (List<RegistrySkinnyVO>) sessionBean.getValues().get("registrySkinnyVOList");
	        }
		}

        if (registrySkinnyVOList != null) {
            //to get throshold msg
            boolean isNotifyRegistrant = this.getCatalogTools().getValueForConfigKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBGiftRegistryConstants.NOTIFY_REGISTRANT_FLAG,false);
            //String skuId = (String)pRequest.getParameter(BBBCoreConstants.SKUID);
            this.logDebug("RestAPIManager  method :: Threshold Msg :: skuId : "+ skuId);
            if(!BBBUtility.isEmpty(skuId) && isNotifyRegistrant){
            	for(RegistrySkinnyVO registrySkinnyVO :registrySkinnyVOList){
            		this.logDebug("RestAPIManager  method :: Threshold Msg :: skuId : "+ skuId + " eventDate :" + registrySkinnyVO.getEventDate());
            		 String thresholdMessage =getGiftRegistryManager().getNotifyRegistrantMsgType(skuId,registrySkinnyVO.getEventDate());
            		 if(!BBBUtility.isEmpty(thresholdMessage)) registrySkinnyVO.setThresholdMsg(thresholdMessage);
            	}
            }
            profileRegistryListVO.setProfileRegistryList(registrySkinnyVOList);
        }
        
        List<RegistrySummaryVO> recommendedRegistryList = getGiftRegistryManager().recommendRegistryList(getProfile().getRepositoryId());
        if (recommendedRegistryList != null) {
        	profileRegistryListVO.setRecommendedRegistryList(recommendedRegistryList);
		} else {
			profileRegistryListVO.setRecommendedRegistryList(new ArrayList<RegistrySummaryVO>());
		}
        
        this.logDebug("RestAPIManager  method [ getOwnAndRecommendedRegistriesForProfile ] exiting with returning " + profileRegistryListVO);
        return profileRegistryListVO;
    }
	
	/**
     * 
     * @return
     * @throws BBBBusinessException
     * @throws RepositoryException
     * @throws BBBSystemException
     */
    public Integer getOwnRegistriesForProfileCount() throws BBBBusinessException, RepositoryException, BBBSystemException{
    	
    	final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
        String siteId = getSiteId();
        Profile profile = (Profile) pRequest
				.resolveName(BBBCoreConstants.ATG_PROFILE);
    	
    	return getGiftRegistryManager().getUserRegistryListCount(profile, siteId);
    	
    	
    }
    
    
    /** checking user is isTransient or not for the session
     *
     * @return
     * @throws BBBBusinessException
     * @throws BBBSystemException
     * @throws ServletException
     * @throws IOException */
    public boolean checkUserStatus() throws BBBBusinessException, BBBSystemException, ServletException, IOException {

        final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
        final Profile user = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
        this.logDebug("Inside  class  : RestAPIManager  method :checkUserStatus user status" + user.isTransient());

        return user.isTransient();
    }

    private void setPriceInRegistryItem(final Map<String, List<RegistryItemVO>> categoryBuckets,boolean isGiftGiver)
                    throws ServletException, IOException {
        for (final String key : categoryBuckets.keySet()) {
            if (key != null) {
                final List<RegistryItemVO> list = categoryBuckets.get(key);
                if (list != null) {
                    for (final RegistryItemVO itemVO : list) {
                        if (itemVO != null) {
                            final String skuId = String.valueOf(itemVO.getSku());
                            try {
                                final String productId = this.getCatalogTools().getParentProductForSku(skuId, true);
                                final double salePrice = this.getCatalogTools().getSalePrice(productId, skuId);
                                if (salePrice > 0) {
                                    itemVO.setPrice(String.valueOf(salePrice));
                                } else {
                                    final double listPrice = this.getCatalogTools().getListPrice(productId, skuId);
                                    itemVO.setPrice(String.valueOf(listPrice));
                                }
                                // Set in cart price in Registry Item VO.
                                final boolean isDynamicSKU=this.getCatalogTools().getSkuIncartFlag(skuId);
                                if(!isGiftGiver && isDynamicSKU){
                                	final double inCartPrice=this.getCatalogTools().getIncartPrice(productId, skuId);
                                	itemVO.setPrice(String.valueOf(inCartPrice));
                                }
                                
                                if(itemVO.getPrice() != null){
                                	itemVO.setTotalPrice(itemVO.getDeliverySurcharge()+itemVO.getAssemblyFees()+Double.valueOf(itemVO.getPrice()));
                				}
                                itemVO.setTotalDeliveryCharges(itemVO.getDeliverySurcharge()+itemVO.getAssemblyFees());
                            } catch (final BBBBusinessException e) {
                                this.logDebug("setPriceInRegistryItem :: Business Exception while fetching parent product Id for sku id: "
                                                + skuId + "Exception: " + e);
                            } catch (final BBBSystemException e) {
                                this.logDebug("setPriceInRegistryItem :: System Exception while fetching parent product Id for sku id: "
                                                + skuId + "Exception: " + e);
                            }
                        }
                    }
                }
            }
        }
    }

    private List<RegistryCategoryBucketVO> convertCategoryBucketOutput(
                    final Map<String, List<RegistryItemVO>> categoryBuckets) {
        final List<RegistryCategoryBucketVO> list = new ArrayList<RegistryCategoryBucketVO>();
        for (final String key : categoryBuckets.keySet()) {
            final RegistryCategoryBucketVO vo = new RegistryCategoryBucketVO();
            if (key != null) {
                vo.setCatgoryName(key);
                vo.setItems(categoryBuckets.get(key));
                final List<RegistryItemVO> itemList = vo.getItems();
                if (!BBBUtility.isListEmpty(itemList)) {
                    for (final RegistryItemVO item : itemList) {
                        if (item != null) {
                            final String skuId = String.valueOf(item.getSku());
                            try {
                                final String productId = this.getCatalogTools().getParentProductForSku(skuId, true);
                                item.getsKUDetailVO().setParentProdId(productId); // set Parent product id for sku
                            } catch (final BBBBusinessException e) {
                                this.logDebug("convertCategoryBucketOutput :: Business Exception while fetching parent product Id for sku id: "
                                                + skuId);
                            } catch (final BBBSystemException e) {
                                this.logDebug("convertCategoryBucketOutput :: System Exception while fetching parent product Id for sku id: "
                                                + skuId);
                            }
                        }
                    }
                }
                list.add(vo);
            }
        }
        return list;
    }

    private List<RegistryCategoriesVO> convertCategoryForRegistryOutput(
                    final Map<String, RegistryCategoryMapVO> categoryForRegistry) {
        final List<RegistryCategoriesVO> list = new ArrayList<RegistryCategoriesVO>();
        for (final String key : categoryForRegistry.keySet()) {
            final RegistryCategoriesVO vo = new RegistryCategoriesVO();
            if (key != null) {
                vo.setCatgoryName(key);
                vo.setCategoryDetail(categoryForRegistry.get(key));
                list.add(vo);
            }
        }
        return list;
    }
    
	public Map<String,String> fetchPOBoxAddress(String registryId) {
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		request.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId);
		request.setParameter(BBBGiftRegistryConstants.DISPLAY_VIEW, "guest");
		Map<String,String> poBoxMap = new HashMap<String,String>();
		try{
			this.getRegistryInfoDroplet().service(request, ServletUtil.getCurrentResponse());
		}
		catch (ServletException e) {
			logError("GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() ServletException: " +e.getMessage());
			logDebug("GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() ServletException Stack Trace:"+e);
		}
		catch (IOException e) {
			logError("GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() ServletException: " +e.getMessage());
			logDebug("GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() ServletException Stack Trace:"+e);
		}

		RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) request.getObjectParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO);
    
		this.logDebug("contents of registrySummaryVO::"+registrySummaryVO);
    
    if (registrySummaryVO != null) {  
    	request.setParameter("address", registrySummaryVO.getShippingAddress().getAddressLine1()+ BBBCoreConstants.SPACE + registrySummaryVO.getShippingAddress().getAddressLine2());
    	poBoxMap.put("summaryVORegId", registrySummaryVO.getRegistryId());
    }
    try{
    	this.getPoBoxValidateDroplet().service(request, ServletUtil.getCurrentResponse());
    }
    catch (ServletException e) {
		logError("GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() ServletException: " +e.getMessage());
		logDebug("GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() ServletException Stack Trace:"+e);
	}
	catch (IOException e) {
		logError("GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() ServletException: " +e.getMessage());
		logDebug("GuestRegistryItemsDisplayDroplet:getDetailsForFirstC1() ServletException Stack Trace:"+e);
	}
    poBoxMap.put("poBoxAddress", request.getParameter("isValid"));
    return poBoxMap;
}

    /** get registry Details for owner and guest user
     *
     * @param registryId
     * @param displayView
     * @return
     * @throws BBBBusinessException
     * @throws BBBSystemException
     * @throws RepositoryException */
    @SuppressWarnings ({ "rawtypes", "unchecked" })
    public RestRegistryInfoDetailVO getRegistryDetailAPI(final Map<String, String> inputParam) throws BBBSystemException {
    	
        if (null == inputParam) {
            return this.getRegistryInfErrVO(ERROR_INVALID_INPUT, BBBCatalogErrorCodes.REGISTRY_DETAIL_INPUT_EXCEPTION,
                            true);
        }
        final String registryId = inputParam.get("registryId");
        String displayView = "guest";
        String c1id = null;
        String c2id = null;
        String c3id = null;
        String c1name = null;
        String c2name = null;
        String c3name = null;
        String fromChecklist = null;
        String qty = null;
        String fetchEph = null; 
       this.logDebug("Passing parameters for loadSelectedCategoryItems() method...");
       if(inputParam.get(BBBCoreConstants.LOAD_SELECTED_CATEGORY) != null && ((String)inputParam.get(BBBCoreConstants.LOAD_SELECTED_CATEGORY)).equalsIgnoreCase(BBBCoreConstants.TRUE))
       {
			if (inputParam.get(BBBCoreConstants.C1_ID) != null) {
				c1id = inputParam.get(BBBCoreConstants.C1_ID);
			}
			if (inputParam.get(BBBCoreConstants.C2_ID) != null) {
				c2id = inputParam.get(BBBCoreConstants.C2_ID);
			}
			if (inputParam.get(BBBCoreConstants.C3_ID) != null) {
				c3id = inputParam.get(BBBCoreConstants.C3_ID);
			}
			if (inputParam.get(BBBCoreConstants.C1_NAME) != null) {
				c1name = inputParam.get(BBBCoreConstants.C1_NAME);
			}
			if (inputParam.get(BBBCoreConstants.C2_NAME) != null) {
				c2name = inputParam.get(BBBCoreConstants.C2_NAME);
			}
			if (inputParam.get(BBBCoreConstants.C3_NAME) != null) {
				c3name = inputParam.get(BBBCoreConstants.C3_NAME);
			}
		}
        if ((inputParam.get("displayView") != null) && !StringUtils.isEmpty(inputParam.get("displayView"))) {
            displayView = inputParam.get("displayView");
        }
        String sortSeq = "1";
        if ((inputParam.get("sortSeq") != null) && !StringUtils.isEmpty(inputParam.get("sortSeq"))) {
            sortSeq = inputParam.get("sortSeq");
        }
        String view = BBBCoreConstants.VIEW_ALL;
        if ((inputParam.get("view") != null) && !StringUtils.isEmpty(inputParam.get("view"))) {
        	view = inputParam.get("view");
        }
        int startIdx = 0;
        if ((inputParam.get("startIdx") != null) && !StringUtils.isEmpty(inputParam.get("startIdx"))) {
            startIdx = Integer.parseInt(inputParam.get("startIdx"));
        }
        int bulkSize = 24;
        if ((inputParam.get("bulkSize") != null) && !StringUtils.isEmpty(inputParam.get("bulkSize"))) {
            bulkSize = Integer.parseInt(inputParam.get("bulkSize"));
        }
        boolean isGiftGiver = true;
        if ((inputParam.get("isGiftGiver") != null) && !StringUtils.isEmpty(inputParam.get("isGiftGiver"))) {
            isGiftGiver = Boolean.parseBoolean(inputParam.get("isGiftGiver"));
        }
        boolean isAvailForWebPurchaseFlag = true;
        if ((inputParam.get("isAvailForWebPurchaseFlag") != null)
                        && !StringUtils.isEmpty(inputParam.get("isAvailForWebPurchaseFlag"))) {
            isAvailForWebPurchaseFlag = Boolean.parseBoolean(inputParam.get("isAvailForWebPurchaseFlag"));
        }
        boolean fromRegistryController=false;
        if ((inputParam.get(BBBCoreConstants.FROM_REGISTRY_CONTROLLER) != null) && !StringUtils.isEmpty(inputParam.get(BBBCoreConstants.FROM_REGISTRY_CONTROLLER))) {
        	fromRegistryController = Boolean.parseBoolean(inputParam.get(BBBCoreConstants.FROM_REGISTRY_CONTROLLER));
        }
        
        if ((inputParam.get(BBBGiftRegistryConstants.FROM_CHECKLIST) != null) && !StringUtils.isEmpty(inputParam.get(BBBGiftRegistryConstants.FROM_CHECKLIST))) {
        	fromChecklist = inputParam.get(BBBGiftRegistryConstants.FROM_CHECKLIST);
        }
        
        if(!BBBUtility.isEmpty(inputParam.get(BBBGiftRegistryConstants.IS_EPH)))
        {
        	fetchEph = inputParam.get(BBBGiftRegistryConstants.IS_EPH);
        }
        RegistrySummaryVO registrySummaryVO = null;

        RegistryVO registryVO = null;

        final RestRegistryInfoDetailVO infoDetailVO = new RestRegistryInfoDetailVO();
        final RegistryResVO registryResVO = new RegistryResVO();
        RepositoryItem[] registryIdRepItems = null;

        final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
        final DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
        pRequest.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId);
        pRequest.setParameter(BBBGiftRegistryConstants.DISPLAY_VIEW, displayView);
        

        String errorMsg = null;
        BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(SESSION_BEAN);
        this.logDebug("Inside class  : RestAPIManager  method :getRegistryDetailAPI");

        final String pSiteId = getSiteId();
        try {
            if (displayView.equalsIgnoreCase(BBBGiftRegistryConstants.OWNER_VIEW)) {

                List<String> pListUserRegIds = new ArrayList<String>();
                final Profile user = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
                HashMap values = null;
                if(sessionBean != null){
                	values = sessionBean.getValues();
                }

                if(values !=null &&!values.containsKey(BBBGiftRegistryConstants.USER_REGISTRIES_LIST)) {
                	 this.logDebug("Registry list not found in session. So fetching the registry list");
                	 
                try {
                    registryIdRepItems = this.getGiftRegistryManager().fetchUserRegistries(pSiteId,
                                    user.getRepositoryId());
                } catch (final RepositoryException e) {
                    this.logDebug("RestAPIManager  method :getRegistryDetailAPI repository exception");
                    return this.getRegistryInfErrVO(ERROR_REGISTRY_INFO,
                                    BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, true);
                } catch (final BBBSystemException e) {
                    this.logError("RestAPIManager  method :getRegistryDetailAPI System exception");
                    return this.getRegistryInfErrVO(ERROR_REGISTRY_INFO,
                                    BBBCatalogErrorCodes.REGISTRY_DETAIL_EXCEPTION_SERVLET_EXCEPTION, true);
                } catch (final BBBBusinessException e) {
                    return this.getRegistryInfErrVO(ERROR_REGISTRY_INFO,
                                    BBBCatalogErrorCodes.REGISTRY_DETAIL_EXCEPTION_SERVLET_EXCEPTION, true);
                }
		                
		               
                if (registryIdRepItems != null) {
                    pListUserRegIds = new ArrayList<String>(registryIdRepItems.length);

                    for (final RepositoryItem registryIdRepItem : registryIdRepItems) {
                        pListUserRegIds.add(registryIdRepItem.getRepositoryId());
                    }
                    sessionBean = (BBBSessionBean) pRequest.resolveName(SESSION_BEAN);
                    sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_LIST, pListUserRegIds);
                    infoDetailVO.setFirstRegistry(pListUserRegIds.size() == 1);
                }
                
            }
            }

            this.getRegistryInfoDroplet().service(pRequest, pResponse);

            registrySummaryVO = (RegistrySummaryVO) pRequest.getObjectParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO);
            
            this.logDebug("contents of registrySummaryVO::"+registrySummaryVO);
            
            if (registrySummaryVO == null) {
                return this.getRegistryInfErrVO(ERROR_REGISTRY_INFO,
                                BBBCatalogErrorCodes.REGISTRY_DETAIL_EXCEPTION_SERVLET_EXCEPTION, true);
            }
            infoDetailVO.setCoRegOwner(Boolean.valueOf(pRequest.getParameter("coRegFlag")));
            pRequest.setParameter("address", registrySummaryVO.getShippingAddress().getAddressLine1()+ BBBCoreConstants.SPACE + registrySummaryVO.getShippingAddress().getAddressLine2());
            this.getPoBoxValidateDroplet().service(pRequest, pResponse);
            
            infoDetailVO.setIsPOBoxAddress(pRequest.getParameter("isValid"));

            
            if(!BBBUtility.isEmpty(registrySummaryVO.getEventDate()) && (!registrySummaryVO.getEventDate().equalsIgnoreCase("0"))){
            if (BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(pSiteId)) {
                pRequest.setParameter("eventDate",
                                BBBUtility.convertUSDateIntoWSFormatCanada(registrySummaryVO.getEventDate()));
            } else {
                pRequest.setParameter("eventDate", registrySummaryVO.getEventDate());
            }
            }

            registryVO = (RegistryVO) pRequest.getObjectParameter(BBBCoreConstants.REGISTRY_VO);
            
            if(!BBBUtility.isEmpty(pRequest.getParameter("eventDate")) && !(pRequest.getParameter("eventDate")).equalsIgnoreCase("0")){
            this.getDateCalculationDroplet().service(pRequest, pResponse);  
                registrySummaryVO.setDaysToGo(Integer.parseInt(pRequest.getParameter("daysToGo")));
                
                final Boolean eventYetToCome = (Boolean.parseBoolean(pRequest.getParameter("check")));
                registrySummaryVO.setEventYetToCome(eventYetToCome);
                if (!eventYetToCome) {
                    registrySummaryVO.setDaysToNextCeleb(Integer.parseInt(pRequest
                                    .getParameter("daysToNextCeleb")));
                }
            }
                
            errorMsg = (String) pRequest.getObjectParameter(OUTPUT_ERROR_MSG);
            if (errorMsg != null) {
                if ((errorMsg != null)
                                && (errorMsg.equalsIgnoreCase(this.ERR_NO_REG_INFO)
                                                || errorMsg.equalsIgnoreCase(this.ERR_INVALID_REG_INFO_REQ) || errorMsg
                                                    .equalsIgnoreCase(this.ERR_REGINFO_FATAL_ERROR))) {
                    registryResVO.getServiceErrorVO().setErrorId(
                                    BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR);
                    registryResVO.getServiceErrorVO().setErrorMessage(errorMsg);
                    registryResVO.getServiceErrorVO().setErrorExists(true);
                } else if ((errorMsg != null) && (errorMsg.equalsIgnoreCase(this.ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR))) {
                    registryResVO.getServiceErrorVO().setErrorId(
                                    BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN);
                    registryResVO.getServiceErrorVO().setErrorMessage(errorMsg);
                    registryResVO.getServiceErrorVO().setErrorExists(true);
                } else if ((errorMsg != null) && (errorMsg.equalsIgnoreCase(this.ERR_REGINFO_INVALID_INPUT_FORMAT))) {
                    registryResVO.getServiceErrorVO().setErrorId(
                                    BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT);
                    registryResVO.getServiceErrorVO().setErrorMessage(errorMsg);
                    registryResVO.getServiceErrorVO().setErrorExists(true);
                }

            } else {
                final String eventTypeCode = registrySummaryVO.getRegistryType().getRegistryTypeName();
                if (null != eventTypeCode && !fromRegistryController) {

                    pRequest.setParameter("siteId", pSiteId);
                    pRequest.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId);
                    pRequest.setParameter(BBBGiftRegistryConstants.SORT_SEQ, sortSeq);
                    pRequest.setParameter(BBBGiftRegistryConstants.VIEW, view);
                    pRequest.setParameter(BBBGiftRegistryConstants.REG_EVENT_TYPE_CODE, eventTypeCode);
                    pRequest.setParameter(BBBGiftRegistryConstants.START_INDEX, startIdx);
                    pRequest.setParameter(BBBGiftRegistryConstants.BULK_SIZE, bulkSize);
                    pRequest.setParameter(BBBGiftRegistryConstants.IS_GIFT_GIVER, isGiftGiver);
                    pRequest.setParameter(BBBGiftRegistryConstants.IS_AVAIL_WEBPUR, isAvailForWebPurchaseFlag);
                    pRequest.setParameter("profile", this.getProfile());
                    pRequest.setParameter(BBBGiftRegistryConstants.FROM_CHECKLIST, fromChecklist);
                    boolean myItemsFlag=false;
                    try {
                    	myItemsFlag = this.getCatalogTools().getValueForConfigKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBGiftRegistryConstants.MY_ITEMS_CHECKLIST_FLAG,false);
					} catch (BBBBusinessException e1) {
						logError("Business Exception while fetching myItemsFlag  method :getRegistryDetailAPI repository exception",e1);
					}
					pRequest.setParameter(BBBGiftRegistryConstants.EVENT_TYPE, registrySummaryVO.getEventType());
					String channel = pRequest.getHeader(this.CHANNEL_HEADER);
					// Mobile Web Changes for Registry My items : Checklist
					boolean showCheckList=true;
					if((BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(channel)) ||
							(BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(channel) && BBBCoreConstants.TRUE.equals(fetchEph))){
						try {
							showCheckList = getCheckListManager().showCheckListButton(eventTypeCode);
						} catch (BBBBusinessException e) {
							logError("Business Exception while fetching checklist enabled  method :getRegistryDetailAPI repository exception",e);
						}
						
					if(!sortSeq.equals(BBBGiftRegistryConstants.PRICE_SORT_SEQ)){
						pRequest.setParameter(BBBGiftRegistryConstants.IS_MY_ITEMS_CHECKLIST, (myItemsFlag && (!showCheckList)));
					}
					}
					if(!StringUtils.isEmpty(c1id)){
				        pRequest.setParameter(BBBCoreConstants.C1_ID, c1id);
				        pRequest.setParameter(BBBCoreConstants.C2_ID, c2id);
				        pRequest.setParameter(BBBCoreConstants.C3_ID, c3id);
				        pRequest.setParameter(BBBCoreConstants.C1_NAME, c1name);
				        pRequest.setParameter(BBBCoreConstants.C2_NAME, c2name);
				        pRequest.setParameter(BBBCoreConstants.C3_NAME, c3name);
				         qty = this.getQty(registryId, c3id, c2id);
				        pRequest.setParameter(BBBCoreConstants.QTY, qty);
				        }
                    this.getRegistryItemDroplet().service(pRequest, pResponse);
                   
                    errorMsg = (String) pRequest.getObjectParameter(OUTPUT_ERROR_MSG);
                   
                    String showStartBrowsing = pRequest.getParameter("showStartBrowsing");
                    if (errorMsg == null) {
                        List<RegistryCategoryBucketVO> list = null;
                        List<RegistryCategoriesVO> categoryForRegistrylist;
                        final List<String> priceRangeList = (List) pRequest
                                        .getObjectParameter(BBBGiftRegistryConstants.PRICE_RANGE_LIST);
                        final String skuList = pRequest.getParameter("skuList");
                        final String sortSequence = pRequest.getParameter(BBBGiftRegistryConstants.SORT_SEQUENCE);
                        final String isListEmpty = pRequest.getParameter(BBBGiftRegistryConstants.EMPTY_LIST);

                        final Map<String, List<RegistryItemVO>> categoryBuckets = (Map) pRequest
                                        .getObjectParameter(BBBGiftRegistryConstants.CATEGORY_BUCKETS);
                        if (categoryBuckets != null) {
                            this.setPriceInRegistryItem(categoryBuckets,isGiftGiver);
                        }
						
                        if (categoryBuckets != null) {
							if (isGiftGiver) {
							
                        		 infoDetailVO.setCategoryBuckets(new ArrayList<RegistryCategoryBucketVO>());
								 
                        	} else {
						
								if(sortSeq.equals(BBBGiftRegistryConstants.PRICE_SORT_SEQ)){
									list = this.convertCategoryBucketOutputForPrice(categoryBuckets,priceRangeList);
								} else{
									list = this.convertCategoryBucketOutput(categoryBuckets);
								}
								infoDetailVO.setCategoryBuckets(list);
							
							}
                        	
                        }

                        final Map<String, RegistryCategoryMapVO> categoryForRegistry = (Map) pRequest
                                        .getObjectParameter(BBBGiftRegistryConstants.REGISTRY_CATEGORY_MAP_VO);
                        if (categoryForRegistry != null) {
                            categoryForRegistrylist = this.convertCategoryForRegistryOutput(categoryForRegistry);
                            infoDetailVO.setCategoryForRegistry(categoryForRegistrylist);
                        }

                        final Map<String, List<RegistryItemVO>> inStockCategoryBuckets = (Map) pRequest
                                        .getObjectParameter(BBBGiftRegistryConstants.INSTOCK_CATEGORY_BUCKETS);
                        if (inStockCategoryBuckets != null) {
                            this.setPriceInRegistryItem(inStockCategoryBuckets,isGiftGiver);
                        }
                       
                        if (inStockCategoryBuckets != null && isGiftGiver) {
                        	if(BBBGiftRegistryConstants.PRICE_SORT_SEQ.equalsIgnoreCase(sortSequence)){
                        		list = this.convertCategoryBucketOutputForPrice(inStockCategoryBuckets,priceRangeList);
                        	}else{
                        		list = this.convertCategoryBucketOutput(inStockCategoryBuckets);
                        	}
                            infoDetailVO.setInStockCategoryBuckets(list);
                        	
                        }
                        final Map<String, List<RegistryItemVO>> notInStockCategoryBuckets = (Map) pRequest
                                        .getObjectParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_CATEGORY_BUCKETS);
                        if (notInStockCategoryBuckets != null) {
                            this.setPriceInRegistryItem(notInStockCategoryBuckets,isGiftGiver);
                        }
                        if (notInStockCategoryBuckets != null && isGiftGiver) {
                        	if(BBBGiftRegistryConstants.PRICE_SORT_SEQ.equalsIgnoreCase(sortSequence)){
                        		list = this.convertCategoryBucketOutputForPrice(notInStockCategoryBuckets,priceRangeList);
                        	}else{
                        		list = this.convertCategoryBucketOutput(notInStockCategoryBuckets);
                        	}
                            infoDetailVO.setNotInStockCategoryBuckets(list);
                        	
                        }

                        final PromoBoxVO promoBox = (PromoBoxVO) pRequest
                                        .getObjectParameter(BBBGiftRegistryConstants.PROMOBOX);
                        final String countStr = pRequest.getParameter(BBBGiftRegistryConstants.COUNT);
                        int count = 0;
                        if (null != countStr) {
                            count = Integer.parseInt(countStr);
                        }

                        final String emptyOutOfStockListFlag = pRequest.getParameter("emptyOutOfStockListFlag");
                        boolean outOfStockListFlag = true;
                        if ((emptyOutOfStockListFlag != null) && emptyOutOfStockListFlag.equalsIgnoreCase("false")) {
                            outOfStockListFlag = false;
                        }
                        if((BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(channel)) ||
    							(BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(channel) && BBBCoreConstants.TRUE.equals(fetchEph))){
                        	try {
								if(!this.getCheckListManager().showC1CategoryOnRlp(c1id, registryId, eventTypeCode).equalsIgnoreCase(BBBCoreConstants.SHOW)){
									infoDetailVO.setOther(pRequest.getParameter(BBBCoreConstants.OTHER));
								}
							} catch (BBBBusinessException e) {
								logError("Business Exception while fetching showC1CategoryOnRlp method :getRegistryDetailAPI repository exception",e);
							}
                        	
                        	
                        	if(myItemsFlag && !isGiftGiver && !showCheckList){
                        		
                        		if(!StringUtils.isEmpty(c1id)){
                                    
                                    infoDetailVO.setItems((List<RegistryItemVO>)pRequest.getObjectParameter(BBBGiftRegistryConstants.REGISTRY_ITEMS_ALL));  
                                    infoDetailVO.setQty(qty);
                            	}
                        		
		                        List<MyItemCategoryVO> ephCategoryBuckets = (List<MyItemCategoryVO>) pRequest.getObjectParameter(BBBGiftRegistryConstants.EPH_CATEGORY_BUCKETS);
		                        /* BBBI-1945 : MOB: My Items – JSP Implementation*/
		                        infoDetailVO.setEphCategoryBuckets(ephCategoryBuckets);
		                        String expandedCategoryId=pRequest.getParameter(BBBGiftRegistryConstants.EXPANDED_CATEGORY);
		                        infoDetailVO.setExpandedCategoryId(expandedCategoryId);
                        	}   
		                        
		                        if(myItemsFlag && isGiftGiver && !showCheckList && !BBBGiftRegistryConstants.PRICE_SORT_SEQ.equalsIgnoreCase(sortSequence) ){
		                        /*In stock category bucket for My items gift giver view*/
		                        List<MyItemCategoryVO> inStockEPHCategoryBuckets = (List<MyItemCategoryVO>) pRequest.getObjectParameter(BBBGiftRegistryConstants.IN_STOCK_EPH_BUCKETS);
		                        infoDetailVO.setInStockEPHCategoryBuckets(inStockEPHCategoryBuckets);
		                        
		                        /*Not In stock category bucket for My items gift giver view*/
		                        List<MyItemCategoryVO> notInStockEPHCategoryBuckets = (List<MyItemCategoryVO>) pRequest.getObjectParameter(BBBGiftRegistryConstants.NOT_IN_STOCK_EPH_BUCKETS);
		                        infoDetailVO.setNotInStockEPHCategoryBuckets(notInStockEPHCategoryBuckets);
		                        }
                        }
                        infoDetailVO.setPriceRangeList(priceRangeList);
                        infoDetailVO.setIsListEmpty(isListEmpty);
                        infoDetailVO.setSortSequence(sortSequence);
                        infoDetailVO.setSkuList(skuList);
                        infoDetailVO.setCount(count);
                        infoDetailVO.setOutOfStockListFlag(outOfStockListFlag);
                        infoDetailVO.setPromoBox(promoBox);
                        infoDetailVO.setShowStartBrowsing(showStartBrowsing);
                       
                       
                        
                    } else {
                        if ((errorMsg != null)
                                        && (errorMsg.equalsIgnoreCase(this.ERR_NO_REG_INFO)
                                                        || errorMsg.equalsIgnoreCase(this.ERR_INVALID_REG_INFO_REQ) || errorMsg
                                                            .equalsIgnoreCase(this.ERR_REGINFO_FATAL_ERROR))) {
                            registryResVO.getServiceErrorVO().setErrorId(
                                            BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR);
                            registryResVO.getServiceErrorVO().setErrorMessage(errorMsg);
                            registryResVO.getServiceErrorVO().setErrorExists(true);
                        } else if ((errorMsg != null)
                                        && (errorMsg.equalsIgnoreCase(this.ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR))) {
                            registryResVO.getServiceErrorVO().setErrorId(
                                            BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN);
                            registryResVO.getServiceErrorVO().setErrorMessage(errorMsg);
                            registryResVO.getServiceErrorVO().setErrorExists(true);
                        } else if ((errorMsg != null)
                                        && (errorMsg.equalsIgnoreCase(this.ERR_REGINFO_INVALID_INPUT_FORMAT))) {
                            registryResVO.getServiceErrorVO().setErrorId(
                                            BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT);
                            registryResVO.getServiceErrorVO().setErrorMessage(errorMsg);
                            registryResVO.getServiceErrorVO().setErrorExists(true);
                        }

                    }
                }
            }
            
            //Performance hit : Everytime user reg list will be fetched because of this piece of code
           /* if (sessionBean != null) {
				sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_LIST, null);
			}*/
        } catch (final IOException e) {
            this.logError("RestAPIManager  method :getRegistryDetailAPI Servlet exception");
            return this.getRegistryInfErrVO(ERROR_REGISTRY_DETAIL,
                            BBBCatalogErrorCodes.REGISTRY_DETAIL_EXCEPTION_IO_EXCEPTION, true);

        } catch (final ServletException e) {
            this.logError("RestAPIManager  method :getRegistryDetailAPI Servlet exception");
            return this.getRegistryInfErrVO(ERROR_REGISTRY_DETAIL,
                            BBBCatalogErrorCodes.REGISTRY_DETAIL_EXCEPTION_SERVLET_EXCEPTION, true);

        }
        registryResVO.setRegistrySummaryVO(registrySummaryVO);
        registryResVO.getRegistrySummaryVO().getShippingAddress().setPoBoxAddress((Boolean.valueOf(infoDetailVO.getIsPOBoxAddress())));
        registryResVO.setRegistryVO(registryVO);

        infoDetailVO.setRegistryResVO(registryResVO);

        final Profile user = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);

        boolean isRegistryOwnedByProfile = false;
        if (!user.isTransient()) {
            try {
                try {
                    isRegistryOwnedByProfile = this.getGiftRegistryManager().isRegistryOwnedByProfile(
                                    user.getRepositoryId(), registryId, pSiteId);
                } catch (final BBBSystemException e) {
                    this.logError("RestAPIManager  method :getRegistryDetailAPI System exception");
                    return this.getRegistryInfErrVO(ERROR_REGISTRY_DETAIL,
                                    BBBCatalogErrorCodes.REGISTRY_DETAIL_EXCEPTION_SERVLET_EXCEPTION, true);
                }
                infoDetailVO.getRegistryResVO().getRegistrySummaryVO().setRegistryOwnedByProfile(true);
            } catch (final BBBBusinessException bbbe) {
                this.logDebug("RestAPIManager.getRegistryDetailAPI() :: Logged in user viewing others registry.");
            }
        }
        if (!isRegistryOwnedByProfile) {
            // mask the required field when registry detail is not for logged in user registry
            infoDetailVO.getRegistryResVO().setRegistryVO(null);
            /*infoDetailVO.getRegistryResVO().getRegistrySummaryVO().setAddrSubType(null);
             * infoDetailVO.getRegistryResVO().getRegistrySummaryVO().setCoRegistrantEmail(null);
             * infoDetailVO.getRegistryResVO().getRegistrySummaryVO().setEventVO(null);
             * infoDetailVO.getRegistryResVO().getRegistrySummaryVO().setFutureShippingAddress(null);
             * infoDetailVO.getRegistryResVO().getRegistrySummaryVO().setFutureShippingDate(null);
             * infoDetailVO.getRegistryResVO().getRegistrySummaryVO().setOwnerProfileID(null);
             * infoDetailVO.getRegistryResVO().getRegistrySummaryVO().setPersonalWebsiteToken(null);
             * infoDetailVO.getRegistryResVO().getRegistrySummaryVO().setRegistrantEmail(null);
             * infoDetailVO.getRegistryResVO().getRegistrySummaryVO().setRegistryInfo(null);
             * infoDetailVO.getRegistryResVO().getRegistrySummaryVO().setShippingAddress(null);
             * infoDetailVO.getRegistryResVO().getRegistrySummaryVO().setState(null);
             * infoDetailVO.getRegistryResVO().getRegistrySummaryVO().setSubType(null);
             * infoDetailVO.getRegistryResVO().getRegistrySummaryVO().setRegToken(null); */}
        int[] recommendationCount = new int[2];
        recommendationCount = this.getGiftRegistryTools().getRecommendationCount(registryId);
        infoDetailVO.setRecommendationCount(recommendationCount);
        infoDetailVO.setEmailOptIn(this.getGiftRegistryRecommendationManager().getEmailOptInValue(registryId));
        infoDetailVO.setRecommendationSize(recommendationCount[1]);
        this.logDebug("Return  class  : RestAPIManager  method :getRegistryDetailAPI");
        return infoDetailVO;

    }

	protected String getSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
    
    /**
     * Rest call to check if  to show recommendation tab for a registry or not
     * @param registryId
     * @return
     */
     public String showRecommendationForRegistry(String registryId){
     	this.logDebug("Return  class  : RestAPIManager  method : showRecommendationForRegistry");
     	String showRecommendationTab = BBBCoreConstants.FALSE;
     	try{
 	    	 final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
 	         final DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
 	         if(pRequest != null){
 	        	 pRequest.setParameter(BBBGiftRegistryConstants.REGISTRY_ID, registryId); 
 		    	 this.getRegistryInfoDroplet().service(pRequest, pResponse);
 		    	 RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) pRequest.getObjectParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO);
 		    	 RegistryVO registryVO = (RegistryVO) pRequest.getObjectParameter(BBBCoreConstants.REGISTRY_VO);
 		    	   Boolean eventYetToCome = false;
 		    	 if(registrySummaryVO != null && registryVO != null){
 					String eventType =  registrySummaryVO.getEventType();
 					if(!BBBUtility.isEmpty(pRequest.getParameter("eventDate")) && !(pRequest.getParameter("eventDate")).equalsIgnoreCase("0")){
 			           this.getDateCalculationDroplet().service(pRequest, pResponse);  
 			                //registrySummaryVO.setDaysToGo(Integer.parseInt(pRequest.getParameter("daysToGo")));
 			                
 			                 eventYetToCome = (Boolean.parseBoolean(pRequest.getParameter("check")));
 			                registrySummaryVO.setEventYetToCome(eventYetToCome);
 			              
 			            } 
 					//boolean eventYetToCome =registrySummaryVO.isEventYetToCome();
 					
 					String isPublic =  registryVO.getIsPublic();
 					int[] recommendationCount = new int[2];
 			        recommendationCount = this.getGiftRegistryTools().getRecommendationCount(registryId);
 					boolean disableInviteFriends = this.getCatalogTools().getValueForConfigKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,BBBCmsConstants.INVITE_FRIENDS_KEY,false);
 					if(disableInviteFriends){
 						showRecommendationTab = BBBCoreConstants.FALSE;
 					} else if ((isPublic).equalsIgnoreCase(BBBCoreConstants.STRING_ONE)) {
 						if(recommendationCount[1] > 0) {
 							showRecommendationTab = BBBCoreConstants.TRUE;
 						} else if (eventYetToCome) {
 							showRecommendationTab = BBBCoreConstants.TRUE;
 						}
 					} else if ((isPublic).equalsIgnoreCase(BBBCoreConstants.STRING_ZERO)) {
 						showRecommendationTab = BBBCoreConstants.TRUE;
 					}
 					showRecommendationTab = showRecommendationTab+BBBCoreConstants.COLON+eventType+BBBCoreConstants.COLON+registryVO.getPrefStoreNum();
 		    	 }
 				this.logDebug("Return  class  : RestAPIManager  method :showRecommendationForRegistry showRecommendationTab = " +showRecommendationTab);
 	         }
     	}catch (final IOException e) {
             this.logError("RestAPIManager  method :showRecommendationForRegistry IOException exception"+e);
         } catch (final ServletException e) {
             this.logError("RestAPIManager  method :showRecommendationForRegistry Servlet exception"+e);
         } catch (BBBSystemException e) {
         	 this.logError("RestAPIManager  method :showRecommendationForRegistry BBBSystemException exception"+e);
 		} catch (BBBBusinessException e) {
 			 this.logError("RestAPIManager  method :showRecommendationForRegistry BBBBusinessException exception"+e);
 		}
         return showRecommendationTab;
     }
     
    /** This method is used to get the quantity of leaf node visited from checklist
    *
    * @param registryId
    *  @param c3id
    *   @param c2id
    * @return
    * @throws BBBSystemException
     */
    public String getQty(String registryId,String c3id,String c2id) throws BBBSystemException{
    	 this.logDebug("RestAPIManager method getQty(): registryId:" + registryId + " c3id: " + c3id + " c2id: " +c2id);	
    	  RepositoryItem skuItem = null;
    	  String qty = null;
	        try {
				RegistryItemsListVO registryItems = this.getGiftRegistryTools().fetchRegistryItemsFromEcomAdmin(registryId,BBBCoreConstants.RETURN_FALSE,BBBGiftRegistryConstants.DEFAULT_ALL_VIEW_FILTER);
				int regItemQuantity = 0;
				String suggestedQuantity = "0";
				if(null != registryItems && !BBBUtility.isListEmpty(registryItems.getRegistryItemList())){
					this.logDebug("Items are present in registry: " + registryId);
					String catId = null;
					
					if(!StringUtils.isEmpty(c3id)){
						catId = c3id;
					}
					else{
						catId = c2id;
					}
					MutableRepositoryItem checkListItem = null;
					try {
						checkListItem = (MutableRepositoryItem) this.getCheckListRepository().getItem(catId,BBBCoreConstants.CHECKLIST_CATEGORY_ITEM_DESCRIPTOR);
						if(null != checkListItem.getPropertyValue(BBBCoreConstants.SUGGESTED_QUANTITY)){
							suggestedQuantity =	checkListItem.getPropertyValue(BBBCoreConstants.SUGGESTED_QUANTITY).toString();
						}

					} catch (RepositoryException e) {
						this.logError("RestAPIManager  method :getRegistryDetailAPI repository exception");
					}
					for(RegistryItemVO regItemVO:registryItems.getRegistryItemList()){
						if(!BBBUtility.isEmpty(String.valueOf(regItemVO.getSku()))){
							skuItem = this.getCheckListTools().getSkuComputedAttributeItem(String.valueOf(regItemVO.getSku()));
						}
						List<String> catListForSku = this.getCheckListTools().getCategoryListForSku(skuItem);						
						if(!BBBUtility.isListEmpty(catListForSku)){
							this.logDebug("The sku does not belong to Other category.");	
							if(catListForSku.contains(catId)){
								 regItemQuantity = regItemQuantity + this.getCheckListTools().getPackageCountForSku(skuItem) * regItemVO.getQtyRequested();
								 this.logDebug("Current category is contained in category list of sku.");							
							}
						}
					}			
						}
				qty = 	String.valueOf(regItemQuantity) + " of " + suggestedQuantity;
				 this.logDebug("Quantity is : " + qty);		
	        } catch (BBBBusinessException e) {
	        	this.logError("RestAPIManager  method :getRegistryDetailAPI business exception");
			}
			return qty;
    }
    
    private List<RegistryCategoryBucketVO> convertCategoryBucketOutputForPrice(
			Map<String, List<RegistryItemVO>> categoryBuckets,
			List<String> priceRangeList) {
    	 final List<RegistryCategoryBucketVO> list = new ArrayList<RegistryCategoryBucketVO>();
         for (final String key : priceRangeList) {
             final RegistryCategoryBucketVO vo = new RegistryCategoryBucketVO();
             if (key != null) {
                 vo.setCatgoryName(key);
                 vo.setItems(categoryBuckets.get(key));
                 final List<RegistryItemVO> itemList = vo.getItems();
                 if (!BBBUtility.isListEmpty(itemList)) {
                     for (final RegistryItemVO item : itemList) {
                         if (item != null) {
                             final String skuId = String.valueOf(item.getSku());
                             try {
                                 final String productId = this.getCatalogTools().getParentProductForSku(skuId, true);
                                 item.getsKUDetailVO().setParentProdId(productId); // set Parent product id for sku
                             } catch (final BBBBusinessException e) {
                                 this.logDebug("convertCategoryBucketOutput :: Business Exception while fetching parent product Id for sku id: "
                                                 + skuId);
                             } catch (final BBBSystemException e) {
                                 this.logDebug("convertCategoryBucketOutput :: System Exception while fetching parent product Id for sku id: "
                                                 + skuId);
                             }
                         }
                     }
                 }
                 list.add(vo);
             }
         }
         return list;
	}

	/** This method is used to create a RestRegistryInfoDetailVO instance and set error message and code.
     *
     * @return */
    private RestRegistryInfoDetailVO getRegistryInfErrVO(final String errorMsg, final String errorCode,
                    final boolean errorExist) {
        final RestRegistryInfoDetailVO restRegistryInfoDetailVO = new RestRegistryInfoDetailVO();
        restRegistryInfoDetailVO.setErrorMessage(errorMsg);
        restRegistryInfoDetailVO.setErrorExist(errorExist);
        restRegistryInfoDetailVO.setErrorCode(errorCode);
        return restRegistryInfoDetailVO;
    }

    /** get registry Dashboard details
     *
     * @param registryId
     * @return
     * @throws BBBBusinessException
     * @throws BBBSystemException
     * @throws RepositoryException */
    public RegistryResVO getRegistryDashboard() throws BBBBusinessException, BBBSystemException, RepositoryException {

        RegistrySummaryVO registrySummaryVO = null;
        final RegistryResVO registryResVO = new RegistryResVO();

        final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
        final DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
        String errorMsg = null;
        pRequest.setParameter(BBBCoreConstants.USER_PROFILE, this.getProfile());
        this.logDebug("Inside class  : RestAPIManager  method :getRegistryDashboardAPI");

        try {

            this.getGiftRegistryFlyoutDroplet().service(pRequest, pResponse);
            registrySummaryVO = (RegistrySummaryVO) pRequest.getObjectParameter(BBBCoreConstants.REGISTRY_SUMMARY_VO);

            errorMsg = (String) pRequest.getObjectParameter(OUTPUT_ERROR_MSG);
            if (errorMsg != null) {
                if ((errorMsg.equalsIgnoreCase(this.ERR_NO_REG_INFO)
                                                || errorMsg.equalsIgnoreCase(this.ERR_INVALID_REG_INFO_REQ) || errorMsg
                                                    .equalsIgnoreCase(this.ERR_REGINFO_FATAL_ERROR))) {
                    registryResVO.getServiceErrorVO().setErrorId(
                                    BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR);
                    registryResVO.getServiceErrorVO().setErrorMessage(errorMsg);
                    registryResVO.getServiceErrorVO().setErrorExists(true);
                } else if (errorMsg.equalsIgnoreCase(this.ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR)) {
                    registryResVO.getServiceErrorVO().setErrorId(
                                    BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN);
                    registryResVO.getServiceErrorVO().setErrorMessage(errorMsg);
                    registryResVO.getServiceErrorVO().setErrorExists(true);
                }

            }

        } catch (final IOException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.REGISTRY_DETAIL_EXCEPTION_IO_EXCEPTION,
                            "IO Exception in Registry Detail");

        } catch (final ServletException e) {
            throw new BBBSystemException(BBBCatalogErrorCodes.REGISTRY_DETAIL_EXCEPTION_SERVLET_EXCEPTION,
                            "Servlet  Exception in Registry Detail");

        }
        registryResVO.setRegistrySummaryVO(registrySummaryVO);
        this.logDebug("Return  class  : RestAPIManager  method :getRegistryDashboardAPI");
        return registryResVO;

    }

    /** Gets the site context.
     *
     * @return the siteContext */
    public SiteContext getSiteContext() {
        return this.mSiteContext;
    }

    /** Sets the site context.
     *
     * @param pSiteContext the siteContext to set */
    public void setSiteContext(final SiteContext pSiteContext) {
        this.mSiteContext = pSiteContext;
    }

    /** Gets the gift registry manager.
     *
     * @return mGiftRegMgr */
    public GiftRegistryManager getGiftRegistryManager() {
        return this.mGiftRegMgr;
    }

    /** Sets the gift registry manager.
     *
     * @param pGiftRegMgr the new gift registry manager */
    public void setGiftRegistryManager(final GiftRegistryManager pGiftRegMgr) {
        this.mGiftRegMgr = pGiftRegMgr;
    }

    @SuppressWarnings ({ "rawtypes", "unchecked" })
    public Map convertToJsonMessage(final Map<String, List<RegistryItemVO>> categoryBuckets) {
        final Map lisItemMap = new HashMap();
        List<RegistryItemVO> registryItemVOs = null;
        if (BBBUtility.isMapNullOrEmpty(categoryBuckets)) {
            return lisItemMap;
        } else {
            for (final Map.Entry<String, List<RegistryItemVO>> entry : categoryBuckets.entrySet()) {
                final Map innerListMap = new HashMap();
                int count = 0;

                final String key = entry.getKey();
                registryItemVOs = entry.getValue();
                if (registryItemVOs != null) {
                    final Iterator iterator = registryItemVOs.iterator();
                    while (iterator.hasNext()) {
                        count++;
                        final RegistryItemVO itemVO = (RegistryItemVO) (iterator.next());
                        innerListMap.put("" + count, itemVO);
                    }
                    lisItemMap.put(key, innerListMap);

                } else {
                    lisItemMap.put(key, null);
                }
            }
        }
        return lisItemMap;

    }

	public RegistrySummaryVO getActiveRegistryForRest() {
		Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.ATG_PROFILE);
		String siteId = getSiteContext().getSiteContextManager().getCurrentSiteId();
		GiftRegSessionBean giftRegSessionBean = (GiftRegSessionBean) ServletUtil.getCurrentRequest().resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean");
		
		String pRecentRegistryId = "";
		RegistrySummaryVO pRegSummaryVO = null;
		if (!profile.isTransient()) {

			List<String> userRegList = new ArrayList<String>();
			List<String> userActiveRegList = new ArrayList<String>();
			BBBSessionBean sessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(SESSION_BEAN);
			final HashMap sessionMap = sessionBean.getValues();
			pRegSummaryVO = (RegistrySummaryVO) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
			
			
			try {
				if(pRegSummaryVO == null ){
				
	            
				
					userRegList = (List) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_LIST);
					userActiveRegList = (List) sessionMap.get(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST);
					String acceptableStatuses = getGiftRegistryManager().getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY);
					List<String> acceptableStatusesList = new ArrayList<String>();
					if (!BBBUtility.isEmpty(acceptableStatuses)) {
						String[] statusesArray = acceptableStatuses.split(BBBCoreConstants.COMMA);
						acceptableStatusesList.addAll(Arrays.asList(statusesArray));
					}
		
					// Get Registry Data from the Database
					if (BBBUtility.isListEmpty(userActiveRegList)) {
						RepositoryItem[] userRegistriesRepItems;
		
						logDebug("GiftRegistryManager.getActiveRegistryForRest : Fetch user registries from database");
						userRegistriesRepItems = getGiftRegistryManager().fetchUserRegistries(siteId, profile.getRepositoryId());
						// Set Active Registry Data
						if (userRegistriesRepItems != null) {
							
							userRegList = new ArrayList<String>(userRegistriesRepItems.length);
							userActiveRegList = new ArrayList<String>(userRegistriesRepItems.length);
							for (RepositoryItem repositoryItem : userRegistriesRepItems) {
								String registryId = repositoryItem.getRepositoryId();
								String registryStatus = getGiftRegistryManager().getRegistryStatusFromRepo(siteId,	registryId);
								if (acceptableStatusesList.contains(registryStatus)) {
									userActiveRegList.add(registryId);
								}
								userRegList.add(registryId);
							}
						}
					}
					if(!BBBUtility.isListEmpty(userActiveRegList)){
						if(userActiveRegList.size() == 1){
							pRecentRegistryId = (String) userActiveRegList.get(0);
							pRegSummaryVO = getGiftRegistryManager().getRegistryInfo( pRecentRegistryId, siteId);
							sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, pRegSummaryVO);
						
							
				            
				            
						}
						else if(userActiveRegList.size() > 1){
							pRecentRegistryId = getGiftRegistryManager().fetchUsersSoonestOrRecent(userActiveRegList);
							// cases when the user has more than 1 registries and all not having event date. The recent registry id fetch is null.
							// Registry summary vo in that case is not populated.
							// FIxed as part of ILD-20
							if(pRecentRegistryId==null){
								pRecentRegistryId=userActiveRegList.get(0);
							}
							pRegSummaryVO = getGiftRegistryManager().getRegistryInfo( pRecentRegistryId, siteId);
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
			if (pRegSummaryVO != null && pRegSummaryVO.getShippingAddress() != null) {
				boolean isPOBoxAddress = BBBUtility.isPOBoxAddress(pRegSummaryVO.getShippingAddress().getAddressLine1(), pRegSummaryVO.getShippingAddress().getAddressLine2());
				pRegSummaryVO.setActiveRegistryHasPoBoxAddress(isPOBoxAddress);
			}
		}
		
		return 	pRegSummaryVO;
	}

	protected long getDateDiff(String siteId, RegistrySummaryVO pRegSummaryVO) throws ParseException {
		return BBBUtility.getDateDiff(pRegSummaryVO.getEventDate(), siteId);
	}    
	@SuppressWarnings("unchecked")
	public boolean isBTSProduct(String prodId, boolean forOmniture) throws BBBSystemException, BBBBusinessException
	{
		boolean bts = false;
		if(prodId != null)
		{
			DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
			DynamoHttpServletResponse response = ServletUtil.getCurrentResponse(); 
			request.setParameter(BreadcrumbDroplet.PARAMETER_PRODUCTID, prodId);
			request.setParameter(BreadcrumbDroplet.PARAMETER_OMNITURE, forOmniture);
			request.setParameter(BreadcrumbDroplet.PARAMETER_SITEID,getSiteId());
			try {
				getBreadcrumbDroplet().service(request, response);
				bts = (Boolean) request.getObjectParameter("bts");
			} catch (ServletException e) {
				logDebug("Method: BreadcrumbDroplet.getProductBreadCrumb, Servlet Exception while getting product bread crumb " + e);
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_PROD_BREADCRUMB, "Some error occurred while getting product bread crumb");
			} catch (IOException e) {
				logDebug("Method: BreadcrumbDroplet.getProductBreadCrumb, IO Exception while getting product bread crumb " + e);
				throw new BBBSystemException(BBBCoreErrorConstants.ERROR_PROD_BREADCRUMB, "Some error occurred while getting product bread crumb");
			}
		}
		else{
			logDebug("Method: BreadcrumbDroplet.getProductBreadCrumb, input parameter is null");
			throw new BBBBusinessException(BBBCoreErrorConstants.ERROR_PROD_BREADCRUMB_INPUT_NULL, "input parameter is null");
		}
		return bts;
	}
    /** Exposing current site id restful service Rest name
     * /rest/bean/com/bbb/rest/giftregistry/RestAPIManager/currentSiteId
     *
     * @return site Id */
    public String currentSiteId()

    {

        return this.getSiteContext().getSite().getId();

    }

    /** @return the giftRegistryFlyoutDroplet */
    public GiftRegistryFlyoutDroplet getGiftRegistryFlyoutDroplet() {
        return this.giftRegistryFlyoutDroplet;
    }

    /** @param giftRegistryFlyoutDroplet the giftRegistryFlyoutDroplet to set */
    public void setGiftRegistryFlyoutDroplet(final GiftRegistryFlyoutDroplet giftRegistryFlyoutDroplet) {
        this.giftRegistryFlyoutDroplet = giftRegistryFlyoutDroplet;
    }
	
	/**
	 * @return the breadcrumbDroplet
	 */
	public BreadcrumbDroplet getBreadcrumbDroplet() {
		return breadcrumbDroplet;
	}

	/**
	 * @param breadcrumbDroplet the breadcrumbDroplet to set
	 */
	public void setBreadcrumbDroplet(BreadcrumbDroplet breadcrumbDroplet) {
		this.breadcrumbDroplet = breadcrumbDroplet;
	}

    /**
     * This is a REST API for Mobile App to fetch registry Item details from ECOMAdmin database.
     * 
     * @param registryId
     * @return
     */
    public AppRegistryInfoDetailVO getRegistryDetailForApp(String registryId) {
    	
    	this.logDebug("RestAPIManager.getRegistryDetailForApp(): Starts :: RegistryId " + registryId);
    	AppRegistryInfoDetailVO regInfoDetail = new AppRegistryInfoDetailVO();
    	DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
    	String view = "1";
    	if(!BBBUtility.isEmpty((String)request.getParameter("view"))) view = (String)request.getParameter("view");
    	try {
    		 RegistryItemsListVO regItemsListVO = getGiftRegistryManager().fetchRegistryItemsFromEcomAdmin(registryId, view);
    		 regInfoDetail.setItems(regItemsListVO);
    		
		} catch (BBBSystemException e) {
			this.logError("System Exception while retrieving registry Item details for registry id: " + registryId);
		} catch (BBBBusinessException e) {
			this.logError("Business Exception while retrieving registry Item details for registry id: " + registryId);
		}
    	
    	this.logDebug("RestAPIManager.getRegistryDetailForApp(): Ends");
    	return regInfoDetail;
    }

	public GiftRegistryTools getGiftRegistryTools() {
		return giftRegistryTools;
	}

	public void setGiftRegistryTools(GiftRegistryTools giftRegistryTools) {
		this.giftRegistryTools = giftRegistryTools;
	}

	public GiftRegistryRecommendationManager getGiftRegistryRecommendationManager() {
		return giftRegistryRecommendationManager;
	}

	public void setGiftRegistryRecommendationManager(
			GiftRegistryRecommendationManager giftRegistryRecommendationManager) {
		this.giftRegistryRecommendationManager = giftRegistryRecommendationManager;
	}
	
	public Map<String, Object> createUser2() throws ServletException, IOException{
		if(isLoggingDebug()){
			logDebug("Entering createUser2 method.");
		}
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
		Map<String, Object> output = new HashMap<String, Object>();
		boolean errorExists=false;
		Object result = null;
		if(validateSiteAndChannel(request, response)){
			if(isLoggingDebug()){
				logDebug("Valid Site and channel are observed. Site :- " + request.getHeader(this.SITE_HEADER) + " Channel :- " +  request.getHeader(this.CHANNEL_HEADER));
			}
			BBBProfileFormHandler profileFormHandler = (BBBProfileFormHandler) request.resolveName(this.profileFormHandlerPath);
			setLoginParameters(request, profileFormHandler);
			request.setParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST, true);
			profileFormHandler.handleCreateUser(request, response);
			if((profileFormHandler.getErrorMap()!=null && profileFormHandler.getErrorMap().size()>0) || (profileFormHandler.getFormExceptions()!=null && profileFormHandler.getFormExceptions().size()>0)){
				errorExists = true;
				result = getAllFormExceptions(profileFormHandler, request.getLocale().getLanguage());
			}else{
				String profileId = getProfileIdFromEmail(request.getParameter(this.EMAIL), request);
				if(!BBBUtility.isEmpty(profileId)){
					errorExists = false;
					result = profileId;
				}else{
					errorExists = true;
					List<String> errorList = new ArrayList<String>();
					errorList.add("A System Error Occurred. Please try again later.");
					result = errorList;
				}
			}
		}else{
			errorExists = true;
			result = getLblTxtTemplateManager().getErrMsg(this.ERR_INVALID_HEADERS, request.getLocale().getLanguage(), null);
		}
		output.put(this.ERROR_EXISTS,errorExists);
		output.put(this.RESULT,result);
		if(isLoggingDebug()){
			logDebug("Result JSON Object is " + output);
		}
		ServletUtil.invalidateSessionNameContext(request, request.getSession(false));
		ServletUtil.invalidateSession(request, request.getSession(false));
		return output;
	}
	
	public String getProfileIdFromEmail(String email, DynamoHttpServletRequest request){
		if(isLoggingDebug()){
			logDebug("Starting getProfileIdFromEmail() method. Email :- " + email);
		}
		MutableRepositoryItem pProfileItem = (MutableRepositoryItem) this.getProfileTools().getItemFromEmail(email);
		if(pProfileItem!=null){
			logDebug(pProfileItem.getRepositoryId());
			if(isLoggingDebug()){
				logDebug("End getProfileIdFromEmail() method. ProfileItem :- " + pProfileItem);
			}
			return pProfileItem.getRepositoryId();
		}
		if(isLoggingDebug()){
			logDebug("End getProfileIdFromEmail() method. ProfileItem :- " + null);
		}
		return null;
	}
	
	public Set<String> getAllFormExceptions(GenericFormHandler formHandler, String language){
		Set<String> result = new HashSet<String>();
		if(isLoggingDebug()){
			logDebug("Entered getAllFormExceptions() Method()");
			logDebug("Form Exceptions that occured are :- " + formHandler.getFormExceptions());
		}
		for(Object exception:formHandler.getFormExceptions())
		{
			String errorMsg = getLblTxtTemplateManager().getErrMsg(((DropletException)exception).getErrorCode(), language, null);
			if(!BBBUtility.isEmpty(errorMsg))
			{
				result.add(errorMsg);
			}
			
			String detailMsgCode = ((DropletException)exception).getMessage();
			String detailMsg = getLblTxtTemplateManager().getErrMsg(detailMsgCode, language, null);
			
			if(!BBBUtility.isEmpty(detailMsg))
			{
				result.add(detailMsg);
			} else {
				result.add(detailMsgCode);
			}
		}
		if(isLoggingDebug()){
			logDebug("End getAllFormExceptions() Method()");
		}
		return result;
	}
	
	private void setLoginParameters(DynamoHttpServletRequest request, BBBProfileFormHandler profileFormHandler){
		if(isLoggingDebug()){
			logDebug("Starting setLoginParameters() method"); 
		}
		String email = request.getParameter(this.EMAIL);
		String password = request.getParameter(this.PASSWORD);
		String confirmPassword = request.getParameter(this.CONFIRM_Password);
		String firstName = request.getParameter(this.FIRST_NAME);
		String lastName = request.getParameter(this.LAST_NAME);
		String mobileNumber = request.getParameter(this.MOBILE_NUMBER);
		String phoneNumber = request.getParameter(this.PHONE_NUMBER);
		String emailOpt = request.getParameter(this.EMAIL_OPTIN);
		String shareCheckBox = request.getParameter(this.SHARE_CHECKBOX_ENABLED);
		Boolean emailOptIn = false;
		Boolean shareCheckBoxEnabled= false;
		try{
			if(!BBBUtility.isEmpty(emailOpt)){
				emailOptIn = Boolean.parseBoolean(emailOpt);
			}
			if(!BBBUtility.isEmpty(shareCheckBox)){
				shareCheckBoxEnabled = Boolean.parseBoolean(shareCheckBox);
			}
		}catch(Exception e){
			logError("Illegal Parameters. Please check and resubmit", e);
		}
		BBBPropertyManager propertyManager = profileFormHandler.getPropertyManager();
		@SuppressWarnings ("unchecked")
        final Dictionary<String, String> value = profileFormHandler.getValue();
		if(!BBBUtility.isEmpty(email)){
			value.put(propertyManager.getEmailAddressPropertyName(), email);
		}
		if(!BBBUtility.isEmpty(password)){
			value.put(propertyManager.getPasswordPropertyName(), password);
		}
		if(!BBBUtility.isEmpty(confirmPassword)){
			value.put(propertyManager.getConfirmPasswordPropertyName(), confirmPassword);
		}
		if(!BBBUtility.isEmpty(firstName)){
			value.put(propertyManager.getFirstNamePropertyName(), firstName);
		}
		if(!BBBUtility.isEmpty(lastName)){
			value.put(propertyManager.getLastNamePropertyName(), lastName);
		}
		if(!BBBUtility.isEmpty(mobileNumber)){
			value.put(propertyManager.getMobileNumberPropertyName(), mobileNumber);
		}
		if(!BBBUtility.isEmpty(phoneNumber)){
			value.put(propertyManager.getPhoneNumberPropertyName(), phoneNumber);
		}
		profileFormHandler.setEmailOptIn(emailOptIn);
		profileFormHandler.setSharedCheckBoxEnabled(shareCheckBoxEnabled);
		if(isLoggingDebug()){
			logDebug("End setLoginParameters() method"); 
		}
	}
	
	public Map<String, Object> createRegistry2() throws BBBSystemException, BBBBusinessException, ServletException, IOException{
		if(isLoggingDebug()){
			logDebug("Entering createRegistry2 method.");
		}
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
		Map<String, Object> output = new HashMap<String, Object>();
		boolean errorExists=false;
		Object result = null;
		if(validateSiteAndChannel(request, response)){
			if(isLoggingDebug()){
				logDebug("Valid Site and channel are observed. Site :- " + request.getHeader(this.SITE_HEADER) + " Channel :- " +  request.getHeader(this.CHANNEL_HEADER));
			}
			GiftRegistryFormHandler giftRegistryFormHandler = (GiftRegistryFormHandler)request.resolveName(this.GIFT_REGISTRY_FORMHANDLER_PATH);
			setCreateRegistryParameters(request, giftRegistryFormHandler);
			request.setParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST, true);
			
			if((request.getParameter(this.PRIMARY_REG_EMAIL) != null) && (request.getParameter(this.PASSWORD) != null)){
				String respVo = profileServices.loginUser(request.getParameter(this.PRIMARY_REG_EMAIL),request.getParameter(this.PASSWORD));
				if(isLoggingDebug()){
					logDebug("Profile ID : " + respVo);
				}
			}
			
			giftRegistryFormHandler.handleCreateRegistry(request, response);
			if((giftRegistryFormHandler.getErrorMap()!=null && giftRegistryFormHandler.getErrorMap().size()>0) || (giftRegistryFormHandler.getFormExceptions()!=null && giftRegistryFormHandler.getFormExceptions().size()>0)){
				errorExists = true;
				result = getAllFormExceptions(giftRegistryFormHandler, request.getLocale().getLanguage());
			}else{
				RegistryResVO webServiceMap = giftRegistryFormHandler.getCreateRegistryResVO();
				if(webServiceMap.isWebServiceError()){
					errorExists = true;
					result = webServiceMap.getError().getErrorMsg();
				}else{
					errorExists = false;
					result = webServiceMap.getRegistryId();
				}
			}
		}else{
			errorExists = true;
			result = getLblTxtTemplateManager().getErrMsg(this.ERR_INVALID_HEADERS, request.getLocale().getLanguage(), null);
			result = "Please enter valid Request values.";
		}
		output.put(this.ERROR_EXISTS, errorExists);
		output.put(this.RESULT, result);
		if(isLoggingDebug()){
			logDebug("Result JSON Object is " + output);
		}
		ServletUtil.invalidateSessionNameContext(request, request.getSession(false));
		ServletUtil.invalidateSession(request, request.getSession(false));
		return output;
	}
		
	@SuppressWarnings("unchecked")
	private Map<String, RepositoryItem> getValidSites(){
		if(isLoggingDebug()){
			logDebug("Starting getValidSites() method.");
		}
		try {
			RqlStatement statement = RqlStatement.parseRqlStatement("ALL");
			RepositoryView view = this.getSiteRepository().getView("siteGroup");
			RepositoryItem[] sites = executeQuery(statement, view);
			if(!BBBUtility.isArrayEmpty(sites)){
				Map<String, RepositoryItem> result = new HashMap<String, RepositoryItem>();
				for(RepositoryItem item:sites){
					for(RepositoryItem singleSite : (Set<RepositoryItem>)item.getPropertyValue("sites")){
						result.put(singleSite.getRepositoryId(), singleSite);
					}
				}
				if(isLoggingDebug()){
					logDebug("End getValidSites() method.");
				}
				return result;
			}
		} catch (RepositoryException e) {
			logError("Error fetching all site groups.");
		}
		if(isLoggingDebug()){
			logDebug("End getValidSites() method.");
		}
		return null;
	}

	protected RepositoryItem[] executeQuery(RqlStatement statement, RepositoryView view) throws RepositoryException {
		return statement.executeQuery(view, null);
	}
	
	private boolean validateSiteAndChannel(DynamoHttpServletRequest request, DynamoHttpServletResponse response){
		String sId = request.getHeader(this.SITE_HEADER);
		Map<String, RepositoryItem> validSites = getValidSites();
		String channel = request.getHeader(this.CHANNEL_HEADER);
		if(isLoggingDebug()){
			logDebug("Starting validateSiteAndChannel method. Site :- " + sId + " channel:- " + channel);
		}
		boolean res= false;
		try {
			RepositoryItem repositoryItem = this.getSiteRepository().getItem(sId, this.SITE_CONFIGURATION);
			if(repositoryItem!=null && validSites!=null && validSites.containsKey(repositoryItem.getRepositoryId())){
				res = true;
			}
		} catch (RepositoryException e) {
			logError("Repository Exception occured while fetching site object.", e);
		}
		if(res){
			try {
				RepositoryItem item = this.getChannelRepository().getItem(channel, this.CHANNEL_INFO);
				if(item != null){
				if((((String)item.getRepositoryId()).equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || ((String)item.getRepositoryId()).equalsIgnoreCase(BBBCoreConstants.DEFAULT_CHANNEL_VALUE)))
					res = true;
				else
					res = false;
				} else {
					res = false;
				}
			} catch (RepositoryException e) {
				logError("Repository Exception occured while fetching site object.", e);
			}
		}
		if(isLoggingDebug()){
			logDebug("End validateSiteAndChannel method. Valid Values :- " + res);
		}
		return res;
	}
		
	public void setCreateRegistryParameters(DynamoHttpServletRequest request, GiftRegistryFormHandler giftRegistryFormHandler){
		if(isLoggingDebug()){
			logDebug("Starting setCreateRegistryParameters() method"); 
		}
		String registryEventType = request.getParameter(this.REGISTRY_EVENT_TYPE);
		String eventDate = request.getParameter(this.EVENT_DATE);
		String babyNurseryTheme = request.getParameter(this.BABY_NURSERY_THEME);
		String college = request.getParameter(this.COLLEGE);
		String guestCount = request.getParameter(this.GUEST_COUNT);
		String primaryRegBabyMaidenName = request.getParameter(this.PRIMARY_REG_BABY_MAIDEN_NAME);
		String babyGender = request.getParameter(this.BABY_GENDER);
		String babyName = request.getParameter(this.BABY_NAME);
		String birthDate = request.getParameter(this.BIRTH_DATE);
		String showerDate = request.getParameter(this.SHOWER_DATE);
		String primaryRegistrantFirstName = request.getParameter(this.PRIMARY_REG_FIRST_NAME);
		String primaryRegistrantPhone = request.getParameter(this.PRIMARY_REG_PHONE);
		String primaryRegistrantLastName = request.getParameter(this.PRIMARY_REG_LAST_NAME);
		String primaryRegistrantEmail = request.getParameter(this.PRIMARY_REG_EMAIL);
		String registryTypeName = request.getParameter(this.REGISTRY_TYPE_NAME);
		String regContactAddress = request.getParameter(this.REG_CONTACT_ADDRESS);
		String primaryRegistrantCellPhone = request.getParameter(this.PRIMARY_REG_CELL_PHONE);
		String primaryRegContactAddFName = request.getParameter(this.PRIMARY_REG_ADD_FIRST_NAME);
		String primaryRegContactAddLName = request.getParameter(this.PRIMARY_REG_ADD_LAST_NAME);
		String primaryRegContactAddLine1 = request.getParameter(this.PRIMARY_REG_ADD_LINE_1);
		String primaryRegContactAddLine2 = request.getParameter(this.PRIMARY_REG_ADD_LINE_2);
		String primaryRegContactAddCity = request.getParameter(this.PRIMARY_REG_ADD_CITY);
		String primaryRegContactAddState = request.getParameter(this.PRIMARY_REG_ADD_STATE);
		String primaryRegContactAddZip = request.getParameter(this.PRIMARY_REG_ADD_ZIP);
		String coRegFName = request.getParameter(this.CO_REG_FIRST_NAME);
		String coRegLName = request.getParameter(this.CO_REG_LAST_NAME);
		String coRegEmail = request.getParameter(this.CO_REG_EMAIL);
		String coRegBabyMaidenName = request.getParameter(this.CO_REG_BABY_MAIDEN_NAME);
		String coRegEmailFoundPopupStatus = request.getParameter(this.CO_REG_EMAIL_STATUS);
		String shippingAddress = request.getParameter(this.SHIPPING_ADDRESS);
		String shippingAddressFName = request.getParameter(this.SHIPPING_ADDRESS_FIRST_NAME);
		String shippingAddressLName = request.getParameter(this.SHIPPING_ADDRESS_LAST_NAME);
		String shippingAddressLine1 = request.getParameter(this.SHIPPING_ADDRESS_LINE_1);
		String shippingAddressLine2 = request.getParameter(this.SHIPPING_ADDRESS_LINE_2);
		String shippingAddressCity = request.getParameter(this.SHIPPING_ADDRESS_CITY);
		String shippingAddressState = request.getParameter(this.SHIPPING_ADDRESS_STATE);
		String shippingAddressZip = request.getParameter(this.SHIPPING_ADDRESS_ZIP);
		String optInWeddingOrBump = request.getParameter(this.OPT_IN_WEDDING_OR_BUMP);
		String futureShipDateSelected = request.getParameter(this.FUTURE_SHIP_SELECTED);
		String futureShippingAddress = request.getParameter(this.FUTURE_SHIPPING_ADDRESS);
		String futureShippingAddFName = request.getParameter(this.FUTURE_SHIPPING_FIRST_NAME);
		String futureShippingAddLName = request.getParameter(this.FUTURE_SHIPPING_LAST_NAME);
		String futureShippingDate = request.getParameter(this.FUTURE_SHIPPING_DATE);
		String futureShippingAddLine1 = request.getParameter(this.FUTURE_SHIPPING_ADD_LINE_1);
		String futureShippingAddLine2 = request.getParameter(this.FUTURE_SHIPPING_ADD_LINE_2);
		String futureShippingAddZip = request.getParameter(this.FUTURE_SHIPPING_ADD_ZIP);
		String futureShippingAddState = request.getParameter(this.FUTURE_SHIPPING_ADD_STATE);
		String futureShippingAddCity = request.getParameter(this.FUTURE_SHIPPING_ADD_CITY);
		String refStoreContactMethod = request.getParameter(this.REF_STORE_CONTACT_METHOD);
		String prefStoreNum = request.getParameter(this.PREF_STORE_NUM);
		String networkAffiliation = request.getParameter(this.NETWORK_AFFILIATION);
		RegistryVO registryVO = giftRegistryFormHandler.getRegistryVO();
		giftRegistryFormHandler.setRegistryEventType(registryEventType);
		EventVO event = registryVO.getEvent();
		event.setEventDate(eventDate);
		event.setBabyNurseryTheme(babyNurseryTheme);
		event.setCollege(college);
		event.setGuestCount(guestCount);
		RegistrantVO primaryRegistrant = registryVO.getPrimaryRegistrant();
		primaryRegistrant.setBabyMaidenName(primaryRegBabyMaidenName);
		if(!BBBUtility.isEmpty(babyGender) ){
			if(babyGender.length()!=1)
				event.setBabyGender(babyGender);
			else
				giftRegistryFormHandler.addFormException(new DropletException("Invalid Baby Gender.", this.ERR_CREATE_REG_BABY_GENDER));
		}
		event.setBabyName(babyName);
		event.setBirthDate(birthDate);
		event.setShowerDate(showerDate);
		primaryRegistrant.setFirstName(primaryRegistrantFirstName);
		primaryRegistrant.setPrimaryPhone(primaryRegistrantPhone);
		primaryRegistrant.setLastName(primaryRegistrantLastName);
		primaryRegistrant.setEmail(primaryRegistrantEmail);
		registryVO.getRegistryType().setRegistryTypeName(registryTypeName);
		giftRegistryFormHandler.setRegContactAddress(regContactAddress);
		primaryRegistrant.setCellPhone(primaryRegistrantCellPhone);
		primaryRegistrant.getContactAddress().setFirstName(primaryRegContactAddFName);
		primaryRegistrant.getContactAddress().setLastName(primaryRegContactAddLName);
		primaryRegistrant.getContactAddress().setAddressLine1(primaryRegContactAddLine1);
		primaryRegistrant.getContactAddress().setAddressLine2(primaryRegContactAddLine2);
		primaryRegistrant.getContactAddress().setCity(primaryRegContactAddCity);
		primaryRegistrant.getContactAddress().setState(primaryRegContactAddState);
		primaryRegistrant.getContactAddress().setZip(primaryRegContactAddZip);
		RegistrantVO coRegistrant = registryVO.getCoRegistrant();
		coRegistrant.setFirstName(coRegFName);
		coRegistrant.setLastName(coRegLName);
		coRegistrant.setEmail(coRegEmail);
		coRegistrant.setBabyMaidenName(coRegBabyMaidenName);
		giftRegistryFormHandler.setCoRegEmailNotFoundPopupStatus(coRegEmailFoundPopupStatus);
		giftRegistryFormHandler.setShippingAddress(shippingAddress);
		ShippingVO shippingVO = registryVO.getShipping();
		AddressVO shippingAdd = shippingVO.getShippingAddress();
		shippingAdd.setFirstName(shippingAddressFName);
		shippingAdd.setLastName(shippingAddressLName);
		shippingAdd.setAddressLine1(shippingAddressLine1);
		shippingAdd.setAddressLine2(shippingAddressLine2);
		shippingAdd.setCity(shippingAddressCity);
		shippingAdd.setZip(shippingAddressZip);
		shippingAdd.setState(shippingAddressState);
		registryVO.setOptInWeddingOrBump(optInWeddingOrBump);
		giftRegistryFormHandler.setFutureShippingDateSelected(futureShipDateSelected);
		giftRegistryFormHandler.setFutureShippingAddress(futureShippingAddress);
		AddressVO futureShippingAdd = shippingVO.getFutureshippingAddress();
		futureShippingAdd.setFirstName(futureShippingAddFName);
		shippingVO.setFutureShippingDate(futureShippingDate);
		futureShippingAdd.setLastName(futureShippingAddLName);
		futureShippingAdd.setAddressLine1(futureShippingAddLine1);
		futureShippingAdd.setAddressLine2(futureShippingAddLine2);
		futureShippingAdd.setZip(futureShippingAddZip);
		futureShippingAdd.setState(futureShippingAddState);
		futureShippingAdd.setCity(futureShippingAddCity);
		registryVO.setRefStoreContactMethod(refStoreContactMethod);
		registryVO.setPrefStoreNum(prefStoreNum);
		registryVO.setNetworkAffiliation(networkAffiliation);
		if(isLoggingDebug()){
			logDebug("End Create Registry Method()");
			logDebug(registryVO.toString());
		}
	}

	@SuppressWarnings("unchecked")
	public List<MyItemCategoryVO> loadChecklistItems(final Map<String, String> inputParam) throws BBBSystemException {
		if (null == inputParam) {
            return null;
        }
        String categoryId = null;
        if ((inputParam.get("categoryId") != null) && !StringUtils.isEmpty(inputParam.get("categoryId"))) {
        	categoryId = inputParam.get("categoryId");
        }
        
        String regEventDate = null;
        if ((inputParam.get(EVENT_DATE) != null) && !StringUtils.isEmpty(inputParam.get(EVENT_DATE))) {
        	regEventDate = inputParam.get(EVENT_DATE);
        }
        final DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
        final DynamoHttpServletResponse pResponse = ServletUtil.getCurrentResponse();
        pRequest.setParameter("categoryId", categoryId);
        pRequest.setParameter(EVENT_DATE, regEventDate);
        pRequest.setParameter(SITE_ID, getSiteId());

        try {
			getRegistryItemDroplet().setRegItemsSecondCall(pRequest, pResponse);
		} catch (ServletException | IOException e) {
			logError("Exception while loadChecklistItems");
		}
        List<MyItemCategoryVO> ephCategoryBuckets =  (List<MyItemCategoryVO>) pRequest.getObjectParameter(BBBGiftRegistryConstants.EPH_CATEGORY_BUCKETS);
        return ephCategoryBuckets;

    }
	
	public BBBInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	public POBoxValidateDroplet getPoBoxValidateDroplet() {
		return poBoxValidateDroplet;
	}

	public void setPoBoxValidateDroplet(POBoxValidateDroplet poBoxValidateDroplet) {
		this.poBoxValidateDroplet = poBoxValidateDroplet;
	}

	public MutableRepository getCheckListRepository() {
		return checkListRepository;
	}

	public void setCheckListRepository(MutableRepository checkListRepository) {
		this.checkListRepository = checkListRepository;
	}

	public CheckListTools getCheckListTools() {
		return checkListTools;
	}

	public void setCheckListTools(CheckListTools checkListTools) {
		this.checkListTools = checkListTools;
	}
}
