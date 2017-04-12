package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.multisite.SiteContext;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.account.BBBProfileTools;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checklist.manager.CheckListManager;
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.RegistryReqVO;
import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * This droplet Fetch Registry Info from registry Id and display registry
 * summary info.
 *
 * @author skalr2
 *
 */
public class RegistryInfoDisplayDroplet extends BBBPresentationDroplet {

	/** The Gift registry manager. */
	private GiftRegistryManager mGiftRegistryManager;

	/** The Registry info service name. */
	private String mRegistryInfoServiceName;

	/** The Catalog tools. */
	private BBBCatalogTools mCatalogTools;

	private BBBPropertyManager pmgr;

	/** The Constant REGISTRYSUMMARYVO. */
	private static final String REGISTRYSUMMARYVO = "registrySummaryVO";
	private static final String INVITE = "invite";
	private static final String REGISTRYVO = "registryVO";
	//private static final String PROFILE_FAVOURITE_STORE = "profileFavouriteStore";

	/** The Constant EVENTDATE. */
	private static final String EVENTDATE = "eventDate";

	/** The Constant EVENTDATE. */
	private static final String FUTURE_SHIPPING_DATE = "futureShippingDate";

	//private static final String COUNT="count";
	/** The Constant VALIDCHECK. */
	private static final String VALIDCHECK = "validCheck";

	/** The Constant REGISTRY_URL. */
	private static final String REGISTRY_URL = "registryURL";

	/** The Constant CO_REG_FLAG. */
	private static final String CO_REG_FLAG = "coRegFlag";

	/** The Constant ERR_NO_REG_INFO. */
	private static final String ERR_NO_REG_INFO = "err_no_reg_info";

	/** The Constant ERR_INVALID_REG_INFO_REQ. */
	private static final String ERR_INVALID_REG_INFO_REQ = "err_invalid_reg_info_req";

//	/** The Constant ERR_REGINFO_BIZ. */
//	private static final String ERR_REGINFO_BIZ = "err_reginfo_biz_error";

	/** The Constant ERR_REGINFO_SYS. */
	private static final String ERR_REGINFO_SYS = "err_reginfo_sys_error";

	/** The Constant ERR_REGINFO_FATAL_ERROR. */
	private static final String ERR_REGINFO_FATAL_ERROR = "err_gift_reg_fatal_error";

	/** The Constant ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR. */
	private static final String ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR = "err_gift_reg_siteflag_usertoken_error";

	/** The Constant ERR_REGINFO_INVALID_INPUT_FORMAT. */
	private static final String ERR_REGINFO_INVALID_INPUT_FORMAT = "err_gift_reg_invalid_input_format";

	private static final String SESSION_BEAN = "/com/bbb/profile/session/SessionBean";
	
	private static final String REG_SUMMARY_KEY_CONST = "_REG_SUMMARY";
	
	private static final String IS_CHECKLIST_DISABLED = "isChecklistDisabled";

	
	/** url for registryGuest view jsp. */
	private String regGuestViewURL;

	/** The Site context. */
	private SiteContext mSiteContext;

	/** The gift reg session bean. */
	private GiftRegSessionBean mGiftRegSessionBean;

	private static LblTxtTemplateManager contentManager;
	
	/** Profile tools reference variable. */
	private ProfileTools profileTools;
	
	//Declaring gift registry parameter	
	private static final String REGISTRY_TITLE = "registryTitle";

	/** checkListManager reference to fetch checklist enable or not details */
	private CheckListManager checkListManager;
	
	/**
	 * @return the profileTools
	 *//*
	public ProfileTools getProfileTools() {
		return profileTools;
	}

	*//**
	 * @param profileTools the profileTools to set
	 *//*
	public void setProfileTools(ProfileTools profileTools) {
		this.profileTools = profileTools;
	}
*/
	/**
	 * Gets the site context.
	 *
	 * @return the siteContext
	 */
	public SiteContext getSiteContext() {
		return this.mSiteContext;
	}
	
	
	
	/**
	 * get checklist manager.
	 * @return checkListManager
	 */
	public CheckListManager getCheckListManager() {
		return checkListManager;
	}
	
	
	/**
	 *  set checklist manager
	 * @param checkListManager
	 */
	public void setCheckListManager(CheckListManager checkListManager) {
		this.checkListManager = checkListManager;
	}

	/**
	 * Sets the site context.
	 *
	 * @param pSiteContext
	 *            the siteContext to set
	 */
	public void setSiteContext(final SiteContext pSiteContext) {
		this.mSiteContext = pSiteContext;
	}

	/**
	 * Fetch Registry Info from registry Id and display registry summary info.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */

	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug(" RegistryInfoDisplayDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		BBBPerformanceMonitor.start("RegistryInfoDisplayDroplet", "GetRegistryInfo");
		RegistryResVO registryResVO = null;
		RegistrySummaryVO registrySummaryVO = null;
		RegistryVO registryVO = null;
		final RegistryReqVO regReqVO = new RegistryReqVO();

		String registryId = null;
		String fromGiftGiver = null;
		String requestURL = null;
		String siteId = null;
		String evDate = null;
		String futureShippingDate = null;
		boolean coRegFlag = false;
		
		boolean isCoRegOwner = false;

		/* variable to find if request is from owner or guest */
		String displayView = null;
		final DateFormat sdf = new SimpleDateFormat(
				BBBGiftRegistryConstants.DATE_FORMAT);
		final SimpleDateFormat monthParse = new SimpleDateFormat("MM", pRequest.getLocale());
		final SimpleDateFormat monthDisplay = new SimpleDateFormat("MMMM", pRequest.getLocale());

		registryId = pRequest
				.getParameter(BBBGiftRegistryConstants.REGISTRY_ID);
		displayView = pRequest
				.getParameter(BBBGiftRegistryConstants.DISPLAY_VIEW);
		fromGiftGiver = pRequest
				.getParameter("fromGiftGiver");
		requestURL = pRequest
				.getParameter("requestURL");
		siteId = this.getSiteContext().getSite().getId();
		this.logDebug(BBBCoreConstants.SITE_ID + "[" + siteId + "]");
		this.logDebug(BBBGiftRegistryConstants.REGISTRY_ID + "[" + registryId + "]");
		
		
		final String profileId = this.getProfile().getRepositoryId();
		List<String> userRegList = new ArrayList<String>();
		
		
		try {
			
			BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(SESSION_BEAN);
			final HashMap sessionMap = sessionBean.getValues();
			userRegList = (List) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_LIST);
			if((userRegList ==null || ( !userRegList.contains(registryId))) && BBBGiftRegistryConstants.OWNER_VIEW.equalsIgnoreCase(displayView)){
				try {
					this.getGiftRegistryManager().getUserRegistryList(getProfile(), siteId);
				} catch (RepositoryException e) {
					logError("Exception occurred while fetching registries",e);
				}
			}

			// if valid registry
			if (this.validateRegistry(registryId, displayView, pRequest)) {

				regReqVO.setRegistryId(registryId);
				regReqVO.setProfileId(profileId);
				regReqVO.setSiteId(this.getCatalogTools().getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId)
						.get(0));
				regReqVO.setUserToken(this.getCatalogTools().getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY,
						BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
				regReqVO.setServiceName(this.getRegistryInfoServiceName());

				registryResVO = (RegistryResVO) sessionMap.get(registryId+REG_SUMMARY_KEY_CONST);
				
				if(registryResVO!=null && registryResVO.getRegistrySummaryVO()!=null && registryResVO.getRegistrySummaryVO().getPrimaryRegistrantEmail()!=null){
					if(registryResVO.getRegistrySummaryVO().getPrimaryRegistrantEmail().equalsIgnoreCase(BBBGiftRegistryConstants.MASKED)){
						registryResVO=null;	
					}
				}
				
				if(null == registryResVO)
				{
					this.logDebug(" RegistryInfoDisplayDroplet service - registryResVO is null and not in session, fetching it from DB");
					// calling the GiftRegistryManager's getRegistryInfo method
					registryResVO = this.getGiftRegistryManager().getRegistryInfo(regReqVO);
					sessionBean.getValues().put(registryId+REG_SUMMARY_KEY_CONST, registryResVO);
					this.logDebug(" RegistryInfoDisplayDroplet service - Saved registryResVO in session");
				}

				if (registryResVO != null) {

					if ((registryResVO.getServiceErrorVO() != null)
							&& registryResVO.getServiceErrorVO()
									.isErrorExists()) {

						if (!BBBUtility.isEmpty(registryResVO
								.getServiceErrorVO().getErrorDisplayMessage())||(!BBBUtility.isEmpty(registryResVO
										.getServiceErrorVO().getErrorMessage())
								&& (registryResVO.getServiceErrorVO()
										.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR)))// Technical
																													// Error
						{
							this.logError(LogMessageFormatter.formatMessage(pRequest, "Fatal error from service of RegistriesInfoDisplayDroplet : Error Id is:"	+ registryResVO.getServiceErrorVO().getErrorId(), BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1011));
							pRequest.setParameter(OUTPUT_ERROR_MSG,
									ERR_REGINFO_FATAL_ERROR);
							pRequest.serviceParameter(OPARAM_ERROR, pRequest,
									pResponse);
							return;
						}
						if (!BBBUtility.isEmpty(registryResVO
								.getServiceErrorVO().getErrorDisplayMessage())||(!BBBUtility.isEmpty(registryResVO
										.getServiceErrorVO().getErrorMessage())
								&& (registryResVO.getServiceErrorVO()
										.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN)))// Technical
																															// Error
						{
							this.logError(LogMessageFormatter.formatMessage(pRequest, "Either user token or site flag invalid from service of RegistryInfoDisplayDroplet : Error Id is:"	+ registryResVO.getServiceErrorVO().getErrorId(), BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1002));
							pRequest.setParameter(OUTPUT_ERROR_MSG,
									ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR);
							pRequest.serviceParameter(OPARAM_ERROR, pRequest,
									pResponse);
							return;
						}
						if (!BBBUtility.isEmpty(registryResVO
								.getServiceErrorVO().getErrorDisplayMessage())||(!BBBUtility.isEmpty(registryResVO
										.getServiceErrorVO().getErrorMessage())
								&& (registryResVO.getServiceErrorVO()
										.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT)))// Technical
																															// Error
						{
							this.logError(LogMessageFormatter.formatMessage(pRequest,
									"GiftRegistry input fields format error from service() of " +
									"RegistryInfoDisplayDroplet | webservice error code=" + registryResVO.getServiceErrorVO().getErrorId(),
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

							pRequest.setParameter(OUTPUT_ERROR_MSG,
									ERR_REGINFO_INVALID_INPUT_FORMAT);
							pRequest.serviceParameter(OPARAM_ERROR, pRequest,
									pResponse);
							return;
						}
					}
					//We moving the below commented code into filteringConfiguration.xml file for the defect JANSEVTN-72
					
					/* changes starts for GFT-1244
					boolean isRecognizedUser = false;
					BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();
					isRecognizedUser = profileTools.isRecognizedUser(pRequest, getProfile());
					
					if ((BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel()) || BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel())) && isRecognizedUser){
						// For recognized user remove PII information from VO
						registryResVO = updateRegistryResVORecUser(registryResVO);
					}
					 changes starts for GFT-1244*/
					
					registryVO = registryResVO.getRegistryVO();

					registrySummaryVO = registryResVO.getRegistrySummaryVO();

					// PS-62346 - PDP Mobile for recognized user throwing 500 errors
					/***
					 * If recognized user, selected an Inactive registry as
					 * default on registry ribbon and lands directly to the PDP,
					 * Internal Server error displayed on mobile.
					 */
					if (registrySummaryVO == null || registryVO == null) {
						this.logError(LogMessageFormatter.formatMessage(pRequest, "Registry Sync Issue for recognized user profile : " + profileId + " and registry id: " + registryId, BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1002));
						pRequest.setParameter(OUTPUT_ERROR_MSG, "Registry Sync Issue for Recognized User");
						pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
						return;						
					}

					registrySummaryVO.setEventVO(registryResVO.getRegistryVO().getEvent());

					// added for appointment scheduling --START
					registrySummaryVO.setPrimaryRegistrantEmail(registryResVO.getRegistryVO().getPrimaryRegistrant().getEmail());
					registrySummaryVO.setPrimaryRegistrantPrimaryPhoneNum(registryResVO.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone());
					registrySummaryVO.setPrimaryRegistrantMobileNum(registryResVO.getRegistryVO().getPrimaryRegistrant().getCellPhone());
					registrySummaryVO.setFavStoreId(registryResVO.getRegistryVO().getPrefStoreNum());
				
					//Skedge Me Check whether the Preferred Store is in Favourite store or not
					if (BBBGiftRegistryConstants.OWNER_VIEW.equalsIgnoreCase(displayView)) {
						if (!StringUtils.isEmpty(registrySummaryVO.getFavStoreId())) {
								registrySummaryVO.
									setAllowedToScheduleAStoreAppointment(getGiftRegistryManager().canScheuleAppointment(registrySummaryVO.getFavStoreId(),
											registrySummaryVO.getRegistryType().getRegistryTypeName(), siteId));
							}
					}
					 // -- END


					if(!this.getProfile().isTransient() &&
							this.getProfile().getPropertyValue(this.pmgr.getLoginPropertyName()).equals(registryResVO.getRegistryVO().getCoRegistrant().getEmail())){
						coRegFlag = true;
					}

					if (null != registrySummaryVO) {

					// setting the event type from event code
					if ((null != registrySummaryVO.getRegistryType())
							&& (registrySummaryVO.getRegistryType()
									.getRegistryTypeName() != null)) {
						registrySummaryVO.setEventType(this.getCatalogTools()
								.getRegistryTypeName(
										registrySummaryVO.getRegistryType()
												.getRegistryTypeName(),siteId));
						
						// to fetch the checklist info whether checklist is
						// disabled or not for requested registry so that same
						// can be used to display old my items or checklist view
						// for my items.
						pRequest.setParameter(
								IS_CHECKLIST_DISABLED,
								getCheckListManager().showCheckListButton(
										registrySummaryVO.getRegistryType()
												.getRegistryTypeName()));

					}
					
					// converting date to as required on the page format
					// "MMM DD/YYYY"
					if (registrySummaryVO.getEventDate() != null) {
						registrySummaryVO.setEventDate(BBBUtility
								.convertDateWSToAppFormat(registrySummaryVO
										.getEventDate()));
						evDate = registrySummaryVO.getEventDate();
						if(registrySummaryVO.getFutureShippingDate()!=null){
							futureShippingDate = registrySummaryVO.getFutureShippingDate();
						}

						/*final Date date = (Date) sdf.parse(evDate);
						final Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						String day = Integer.toString(cal
								.get(Calendar.DATE));
						if (day.length() == 1) {
							day = BBBCoreConstants.ZERO + day;
						}
						String month = Integer.toString(cal
								.get(Calendar.MONTH) + 1);
						final String year = Integer.toString(cal
								.get(Calendar.YEAR));
						month = monthDisplay
								.format(monthParse.parse(month));
						evDate = month.substring(0, 3) + " " + day + ", "
								+ year;*/
					}


					// If future Shipping date is greater than current date
					// set the
					// shipping address as future shipping address.
					/*if (registrySummaryVO.getFutureShippingDate() != null) {
						registrySummaryVO.setFutureShippingDate(BBBUtility
								.convertDateWSToAppFormat(registrySummaryVO
										.getFutureShippingDate()));
						final Date futShpDate = sdf.parse(registrySummaryVO
								.getFutureShippingDate());
						final Date todayDate = new Date();

						if ((futShpDate.getTime() - todayDate.getTime()) > 0) {
							registrySummaryVO
									.setShippingAddress(registrySummaryVO
											.getFutureShippingAddress());
						}

					}*/
					
					// calling save registry info in a session method

					//Check Registry Status. If status is not in active and Lead ignore it from Session
					final String registryStatus = this.getGiftRegistryManager().getRegistryStatusFromRepo(siteId, registryId);
					final String acceptableStatuses = this.getGiftRegistryManager().getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY);
					final List<String> acceptableStatusesList = new ArrayList<String>();
					if(!BBBUtility.isEmpty(acceptableStatuses)) {
						final String[] statusesArray = acceptableStatuses.split(BBBCoreConstants.COMMA);
						acceptableStatusesList.addAll(Arrays.asList(statusesArray));
					}

					if(acceptableStatusesList.contains(registryStatus)){
						this.saveRegistryInfoToSession(registrySummaryVO, pRequest);

					}

					// update info to registryFlyout
					// update the RegistrySummaryVO in the BBBsessionBean is registry viewed is user registry.
					if(BBBGiftRegistryConstants.OWNER_VIEW.equalsIgnoreCase(displayView)){
//					sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
//					final RegistrySummaryVO regSummaryVO = this.getGiftRegistryManager().getRegistryInfo(registryId, siteId);

					//If Registry status is not Active/Lead don't put it in session
					if(acceptableStatusesList.contains(registryStatus) && !pRequest.getRequestURI().contains("view_registry_guest.jsp")){
						RegistrySummaryVO regSummaryVO = null;
						if(null != registryResVO && null != registryResVO.getRegistrySummaryVO()) {
							regSummaryVO = registryResVO.getRegistrySummaryVO();
						} else {
							regSummaryVO = this.getGiftRegistryManager().getRegistryInfo(registryId, siteId);
						}
						//Fix for ribbon issue as Primary registrant name was not updating into ribbon, as it was not getting updated name from session registry summaryvo
						regSummaryVO.setPrimaryRegistrantFirstName(registrySummaryVO.getPrimaryRegistrantFirstName());
						
						sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, regSummaryVO);
					}
					if(!pRequest.getRequestURI().contains("view_registry_guest.jsp")){
					this.getGiftRegSessionBean().setRegistryOperation(BBBGiftRegistryConstants.OWNER_VIEW);
					}
					}
					
					
					//Code changes to see if it's a registry owner or co-owner
					isCoRegOwner= this.getGiftRegistryManager().isCoOwerByProfile(this.getProfile().getRepositoryId(), registryId, siteId);
					registryVO.setCoRegOwner(isCoRegOwner);
					String regIsPublic= registryResVO.getRegistryVO().getIsPublic();
					if (regIsPublic.equalsIgnoreCase(BBBCoreConstants.STRING_ONE)){
						settingRegistryTitle(registryResVO, registrySummaryVO);
					}
					
					pRequest.setParameter(CO_REG_FLAG,coRegFlag);
					/*if (null != fromGiftGiver &&
							fromGiftGiver.equalsIgnoreCase("true")) {
						String[] requestURLSubString = requestURL.split("&_requestid=");
						pRequest.setParameter(REGISTRY_URL, requestURLSubString[0]);
					} else {
						pRequest.setParameter(REGISTRY_URL,
								this.getRegGuestViewURL());
					}*/
					
					
					pRequest.setParameter(REGISTRY_URL,
							this.getRegGuestViewURL());
					pRequest.setParameter(EVENTDATE, evDate);
					pRequest.setParameter(FUTURE_SHIPPING_DATE, futureShippingDate);
					pRequest.setParameter(REGISTRYSUMMARYVO,
							registrySummaryVO);
					pRequest.setParameter(INVITE,this.getCatalogTools().isInviteFriends());

					pRequest.setParameter(REGISTRYVO,
							registryVO);
		
						pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,
								pResponse);
					} else {
						pRequest.setParameter(OUTPUT_ERROR_MSG, ERR_NO_REG_INFO);
						pRequest.serviceParameter(OPARAM_ERROR, pRequest,
								pResponse);
					}
				} else {
					pRequest.setParameter(OUTPUT_ERROR_MSG, ERR_NO_REG_INFO);
					pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
				}

			} else {

				pRequest.setParameter(OUTPUT_ERROR_MSG,
						ERR_INVALID_REG_INFO_REQ);
				pRequest.serviceParameter(OPARAM_ERROR, pRequest, pResponse);
			}

		} catch (final BBBBusinessException bExcep) {

			this.logError("BBBBusinessException", bExcep);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);

		} catch (final BBBSystemException sysExcep) {

			this.logError("BBBSystemException", sysExcep);
			pRequest.setParameter(OUTPUT_ERROR_MSG, ERR_REGINFO_SYS);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} 
		this.logDebug(" RegistryInfoDisplayDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");
		BBBPerformanceMonitor.end("RegistryInfoDisplayDroplet", "GetRegistryInfo");

	}

	/**
	 * This methods creates the registry title
	 * for public registries
	 * @param registryResVO
	 * @param registrySummaryVO
	 */
	// BBBSL-9359 |Rendering the regTitle 
	protected void settingRegistryTitle(RegistryResVO registryResVO,
			RegistrySummaryVO registrySummaryVO) {
	
		String primFirstName;
		String primLastName;
		String coFirstName;
		String coLastName;
		String registryName;
		String regTitle;
		
		primFirstName = registrySummaryVO.getPrimaryRegistrantFirstName();
		primLastName = registrySummaryVO.getPrimaryRegistrantLastName();
		coFirstName = registrySummaryVO.getCoRegistrantFirstName();
		coLastName = registrySummaryVO.getCoRegistrantLastName();
		registryName = registrySummaryVO.getEventType();
		 
		StringBuilder registryTitle = new StringBuilder();
		 registryTitle.append(primFirstName).append(BBBCmsConstants.SPACE)
				.append(primLastName);
		 if (null != coFirstName && null != coLastName) {
			 registryTitle.append(BBBCoreConstants.AMPERSAND_SPACE).append(coFirstName).append(BBBCmsConstants.SPACE)
				.append(coLastName);
		 }
		regTitle=registryTitle.append(BBBCoreConstants.HYPHEN_SPACE).append(registryName).toString();
		registrySummaryVO.setRegTitle(regTitle);
	}
	
	/**
	 * 
	 * This method resets the data that needs to hide for recognized user.
	 *
	 * @param registryResVO
	 *            the registry response vo
	 * 
	 * This method is created for GFT-1244 : Recognized User State - Edit Registry Info
	 */
	/*private RegistryResVO updateRegistryResVORecUser(RegistryResVO registryResVO){
		this.logDebug("updateRegistryResVORecUser method starts...");
		if(null != registryResVO.getRegistrySummaryVO()){
			registryResVO.getRegistrySummaryVO().setCoRegistrantEmail(BBBGiftRegistryConstants.MASKED);
			//registryResVO.getRegistrySummaryVO().setCoRegistrantFirstName(null);
			registryResVO.getRegistrySummaryVO().setCoRegistrantMaidenName(null);
			//registryResVO.getRegistrySummaryVO().setCoRegistrantLastName(null);
			registryResVO.getRegistrySummaryVO().setCoRegistrantFullName(null);
			registryResVO.getRegistrySummaryVO().setFutureShippingAddress(new AddressVO());
			
			registryResVO.getRegistrySummaryVO().setPrimaryRegistrantEmail(BBBGiftRegistryConstants.MASKED);
			//registryResVO.getRegistrySummaryVO().setPrimaryRegistrantFirstName(null);
			registryResVO.getRegistrySummaryVO().setPrimaryRegistrantMaidenName(null);
			//registryResVO.getRegistrySummaryVO().setPrimaryRegistrantLastName(null);
			registryResVO.getRegistrySummaryVO().setPrimaryRegistrantFullName(null);
			if(null != registryResVO.getRegistrySummaryVO().getPrimaryRegistrantMobileNum()){
				registryResVO.getRegistrySummaryVO().setPrimaryRegistrantMobileNum(
						BBBUtility.maskAllDigits(registryResVO.getRegistrySummaryVO().getPrimaryRegistrantMobileNum()));				
			}			
			if(null != registryResVO.getRegistrySummaryVO().getPrimaryRegistrantPrimaryPhoneNum()){
				registryResVO.getRegistrySummaryVO().setPrimaryRegistrantPrimaryPhoneNum(BBBUtility.maskAllDigits(
						registryResVO.getRegistrySummaryVO().getPrimaryRegistrantPrimaryPhoneNum()));
			}						
			registryResVO.getRegistrySummaryVO().setRegistrantEmail(BBBGiftRegistryConstants.MASKED);
			registryResVO.getRegistrySummaryVO().setRegistryInfo(null);
			
			registryResVO.getRegistrySummaryVO().setShippingAddress(new AddressVO());
		}
		
		if(null != registryResVO.getRegistryVO()){			
    		if(null != registryResVO.getRegistryVO().getCoRegistrant()){
    			if(null != registryResVO.getRegistryVO().getCoRegistrant().getCellPhone()){
    				registryResVO.getRegistryVO().getCoRegistrant().setCellPhone(
    						BBBUtility.maskAllDigits(registryResVO.getRegistryVO().getCoRegistrant().getCellPhone()));
    			}
    			
    			registryResVO.getRegistryVO().getCoRegistrant().setEmail(BBBGiftRegistryConstants.MASKED);
    			registryResVO.getRegistryVO().getCoRegistrant().setContactAddress(null);
    			//registryResVO.getRegistryVO().getCoRegistrant().setFirstName(null);
    			//registryResVO.getRegistryVO().getCoRegistrant().setLastName(null);
    			if(null != registryResVO.getRegistryVO().getCoRegistrant().getPrimaryPhone()){
    				registryResVO.getRegistryVO().getCoRegistrant().setPrimaryPhone(
    						BBBUtility.maskAllDigits(registryResVO.getRegistryVO().getCoRegistrant().getPrimaryPhone()));
    			}    			
    		}
    		if(null != registryResVO.getRegistryVO().getPrimaryRegistrant()){
    			if(null != registryResVO.getRegistryVO().getPrimaryRegistrant().getCellPhone()){
    				registryResVO.getRegistryVO().getPrimaryRegistrant().setCellPhone(BBBUtility.maskAllDigits(
    						registryResVO.getRegistryVO().getPrimaryRegistrant().getCellPhone()));
    			}    			
    			// registryResVO.getRegistryVO().getPrimaryRegistrant().setEmail(BBBGiftRegistryConstants.MASKED);
    			registryResVO.getRegistryVO().getPrimaryRegistrant().setContactAddress(null);
    			//registryResVO.getRegistryVO().getPrimaryRegistrant().setFirstName(null);
    			//registryResVO.getRegistryVO().getPrimaryRegistrant().setLastName(null);
    			if(null != registryResVO.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone()){
    				registryResVO.getRegistryVO().getPrimaryRegistrant().setPrimaryPhone(BBBUtility.maskAllDigits(
    						registryResVO.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone()));
    			}    			
    		}
    		if(null != registryResVO.getRegistryVO().getRegistrantVO()){
    			if(null != registryResVO.getRegistryVO().getRegistrantVO().getCellPhone()){
    				registryResVO.getRegistryVO().getRegistrantVO().setCellPhone(BBBUtility.maskAllDigits(
    						registryResVO.getRegistryVO().getRegistrantVO().getCellPhone()));
    			}    			
    			registryResVO.getRegistryVO().getRegistrantVO().setEmail(BBBGiftRegistryConstants.MASKED);
    			registryResVO.getRegistryVO().getRegistrantVO().setContactAddress(null);
    			//registryResVO.getRegistryVO().getRegistrantVO().setFirstName(null);
    			//registryResVO.getRegistryVO().getRegistrantVO().setLastName(null);
    			if(null != registryResVO.getRegistryVO().getRegistrantVO().getPrimaryPhone()){
    				registryResVO.getRegistryVO().getRegistrantVO().setPrimaryPhone(BBBUtility.maskAllDigits(
    						registryResVO.getRegistryVO().getRegistrantVO().getPrimaryPhone()));
    			}    			
    		}
    		if(null != registryResVO.getRegistryVO().getShipping()){
    			registryResVO.getRegistryVO().getShipping().setFutureshippingAddress(new AddressVO());    			
    			registryResVO.getRegistryVO().getShipping().setShippingAddress(new AddressVO());
    		}
    		registryResVO.getRegistryVO().setUserAddressList(null);    	
		}		
		this.logDebug("updateRegistryResVORecUser method ends.");
		return registryResVO;
	}
	*/

	/**
	 * This method saves registry info in a session to keep a track on last
	 * viewed registries.
	 *
	 * @param registrySummaryVO
	 *            the registry summary vo
	 * @param pRequest
	 *            the request
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */
	public void saveRegistryInfoToSession(
			final RegistrySummaryVO registrySummaryVO,
			final DynamoHttpServletRequest pRequest) throws IOException,
			ServletException {
		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
				.resolveName(SESSION_BEAN);

		final HashMap sessionMap = sessionBean.getValues();
		final List<RegistrySummaryVO> usersCurrentlyViewedRegistry = (List) sessionMap
				.get(BBBSearchBrowseConstants.LAST_VIEWED_REGISTRY_ID_LIST);
		if (registrySummaryVO != null) {
			// if list of last viewed registry ids already exists add new
			// registry
			// id in that
			if (usersCurrentlyViewedRegistry != null) {
				if (usersCurrentlyViewedRegistry.contains(registrySummaryVO)) {
					// if registry id already exists remove and again add to
					// update its position in the list
					usersCurrentlyViewedRegistry.remove(registrySummaryVO);
					this.logDebug("registryId " + registrySummaryVO.getRegistryId()
							+ " already exists in last viewed list");
				}
				usersCurrentlyViewedRegistry.add(registrySummaryVO);
				this.logDebug("adding registryId "
						+ registrySummaryVO.getRegistryId()
						+ " in already existing list");
				sessionBean.getValues().put(
						BBBSearchBrowseConstants.LAST_VIEWED_REGISTRY_ID_LIST,
						usersCurrentlyViewedRegistry);
			}
			// if session is new and no last viewed registry list exists create
			// new list and add registry id in it
			else {
				final List<RegistrySummaryVO> lastViewedRegistryList = new ArrayList<RegistrySummaryVO>();
				lastViewedRegistryList.add(registrySummaryVO);
				this.logDebug("new session so adding registryId "
						+ registrySummaryVO.getRegistryId() + " in new list");
				sessionBean.getValues().put(
						BBBSearchBrowseConstants.LAST_VIEWED_REGISTRY_ID_LIST,
						lastViewedRegistryList);
			}
		}

	}

	/**
	 * This method validates if the registryId belongs to this profile
	 *
	 * Return false if pRegistryId is null Return true if request is not from
	 * owner view Return true if the pRegistryId is in users's registries set
	 * otherwise return false.
	 *
	 * @param pRegistryId
	 *            the registry id
	 * @param displayView
	 *            the display view
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true, if successful
	 */
	private boolean validateRegistry(final String pRegistryId, final String displayView,
			final DynamoHttpServletRequest pRequest) {

		boolean success = false;
		boolean validCheck = false;

		if (pRegistryId == null) {

			return success;

		} else if ((displayView == null)
				|| !displayView
						.equalsIgnoreCase(BBBGiftRegistryConstants.OWNER_VIEW)) {

			validCheck = this.getGiftRegistryManager().isUserOwnRegistry(this.getProfile(), pRegistryId, pRequest);
			pRequest.setParameter(VALIDCHECK, validCheck);
			success = true;

		} else  {

			// if user is transient
			if (this.getProfile().isTransient()) {
				success = false;

			} else {
				// //check user
				success = this.getGiftRegistryManager().isUserOwnRegistry(this.getProfile(), pRegistryId, pRequest);
				pRequest.setParameter(VALIDCHECK, success);
			}
		}

		return success;

	}

	/**
	 * Gets the gift registry manager.
	 *
	 * @return the giftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return this.mGiftRegistryManager;
	}

	/**
	 * Sets the gift registry manager.
	 *
	 * @param giftRegistryManager
	 *            the giftRegistryManager to set
	 */
	public void setGiftRegistryManager(
			final GiftRegistryManager giftRegistryManager) {
		this.mGiftRegistryManager = giftRegistryManager;
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
	 * Gets the catalog tools.
	 *
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * Sets the catalog tools.
	 *
	 * @param pCatalogTools
	 *            the new catalog tools
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	/**
	 * Gets the reg guest view url.
	 *
	 * @return the reg guest view url
	 */
	public String getRegGuestViewURL() {
		return this.regGuestViewURL;
	}

	/**
	 * Sets the reg guest view url.
	 *
	 * @param regGuestViewURL
	 *            the new reg guest view url
	 */
	public void setRegGuestViewURL(final String regGuestViewURL) {
		this.regGuestViewURL = regGuestViewURL;
	}

	/**
	 * Gets the profile.
	 *
	 * @return the profile
	 */
	public Profile getProfile() {
		final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		final Profile profile = (Profile) request.resolveName(BBBCoreConstants.ATG_PROFILE);
		return profile;
	}


	/**
	 * @return the pmgr
	 */
	public BBBPropertyManager getPmgr() {
		return this.pmgr;
	}

	/**
	 * @param mPmgr the mPmgr to set
	 */
	public void setPmgr(final BBBPropertyManager pmgr) {
		this.pmgr = pmgr;
	}

	/**
	 * @return the giftRegSessionBean
	 */
	public GiftRegSessionBean getGiftRegSessionBean() {
		return this.mGiftRegSessionBean;
	}

	/**
	 * @param giftRegSessionBean the giftRegSessionBean to set
	 */
	public void setGiftRegSessionBean(final GiftRegSessionBean giftRegSessionBean) {
		this.mGiftRegSessionBean = giftRegSessionBean;
	}

	public LblTxtTemplateManager getContentManager() {
		return contentManager;
	}

	public void setContentManager(final LblTxtTemplateManager contentManager) {
		RegistryInfoDisplayDroplet.contentManager = contentManager;
	}

}
