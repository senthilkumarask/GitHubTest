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
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistryReqVO;
import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
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
public class MxRegistryInfoDisplayDroplet extends BBBPresentationDroplet {

	/** The Gift registry manager. */
	private GiftRegistryManager mGiftRegistryManager;

	/** The Registry info service name. */
	private String mRegistryInfoServiceName;

	/** The Catalog tools. */
	private BBBCatalogTools mCatalogTools;

	private BBBPropertyManager pmgr;

	/** The Constant REGISTRYSUMMARYVO. */
	private static final String REGISTRYSUMMARYVO = "registrySummaryVO";

	private static final String REGISTRYVO = "registryVO";
	//private static final String PROFILE_FAVOURITE_STORE = "profileFavouriteStore";

	/** The Constant EVENTDATE. */
	private static final String EVENTDATE = "eventDate";

	/** The Constant EVENTDATE. */
	private static final String FUTURE_SHIPPING_DATE = "futureShippingDate";

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

	/** Profile reference variable. */
	private Profile profile;

	/** url for registryGuest view jsp. */
	private String regGuestViewURL;

	/** The Site context. */
	private SiteContext mSiteContext;

	/** The gift reg session bean. */
	private GiftRegSessionBean mGiftRegSessionBean;

	private LblTxtTemplateManager contentManager;
	/**
	 * Gets the site context.
	 *
	 * @return the siteContext
	 */
	public SiteContext getSiteContext() {
		return this.mSiteContext;
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
		this.logDebug(" MxRegistryInfoDisplayDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		BBBPerformanceMonitor.start("MxRegistryInfoDisplayDroplet", "GetRegistryInfo");
		RegistryResVO registryResVO = null;
		RegistrySummaryVO registrySummaryVO = null;
		RegistryVO registryVO = null;
		final RegistryReqVO regReqVO = new RegistryReqVO();

		String registryId = null;
		String siteId = null;
		String evDate = null;
		String futureShippingDate = null;
		boolean coRegFlag = false;

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

		siteId = this.getSiteContext().getSite().getId();
		this.logDebug(BBBCoreConstants.SITE_ID + "[" + siteId + "]");
		this.logDebug(BBBGiftRegistryConstants.REGISTRY_ID + "[" + registryId + "]");

		try {

			// if valid registry
			if (this.validateRegistry(registryId, displayView, pRequest)) {

				regReqVO.setRegistryId(registryId);
				/*
				regReqVO.setSiteId(this.getCatalogTools().getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId)
						.get(0));
				*/
				// for mx registry
				regReqVO.setSiteId("5");
				// end
				
				regReqVO.setUserToken(this.getCatalogTools().getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY,
						BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
				regReqVO.setServiceName(this.getRegistryInfoServiceName());

				// calling the GiftRegistryManager's getRegistryInfo method
				registryResVO = this.getGiftRegistryManager().getRegistryInfo(regReqVO);

				if (registryResVO != null) {

					if (registryResVO.getServiceErrorVO()
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
						if ((!BBBUtility.isEmpty(registryResVO
										.getServiceErrorVO().getErrorMessage())
								&& (registryResVO.getServiceErrorVO()
										.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN)))// Technical
																															// Error
						{
							this.logError(LogMessageFormatter.formatMessage(pRequest, "Either user token or site flag invalid from service of MxRegistryInfoDisplayDroplet : Error Id is:"	+ registryResVO.getServiceErrorVO().getErrorId(), BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1002));
							pRequest.setParameter(OUTPUT_ERROR_MSG,
									ERR_REGINFO_SITEFLAG_USERTOKEN_ERROR);
							pRequest.serviceParameter(OPARAM_ERROR, pRequest,
									pResponse);
							return;
						}
						if ((!BBBUtility.isEmpty(registryResVO
										.getServiceErrorVO().getErrorMessage())
								&& (registryResVO.getServiceErrorVO()
										.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT)))// Technical
																															// Error
						{
							this.logError(LogMessageFormatter.formatMessage(pRequest,
									"GiftRegistry input fields format error from service() of " +
									"MxRegistryInfoDisplayDroplet | webservice error code=" + registryResVO.getServiceErrorVO().getErrorId(),
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

							pRequest.setParameter(OUTPUT_ERROR_MSG,
									ERR_REGINFO_INVALID_INPUT_FORMAT);
							pRequest.serviceParameter(OPARAM_ERROR, pRequest,
									pResponse);
							return;
						}
					}

					registryVO = registryResVO.getRegistryVO();

					registrySummaryVO = registryResVO.getRegistrySummaryVO();
				    if (null != registrySummaryVO) {
					registrySummaryVO.setEventVO(registryResVO.getRegistryVO().getEvent());

					// added for appointment scheduling --START
					registrySummaryVO.setPrimaryRegistrantEmail(registryResVO.getRegistryVO().getPrimaryRegistrant().getEmail());
					registrySummaryVO.setPrimaryRegistrantPrimaryPhoneNum(registryResVO.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone());
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
										


					// setting the event type from event code
					if ((null != registrySummaryVO.getRegistryType())
							&& (registrySummaryVO.getRegistryType()
									.getRegistryTypeName() != null)) {
						registrySummaryVO.setEventType(this.getCatalogTools()
								.getRegistryTypeName(
										registrySummaryVO.getRegistryType()
												.getRegistryTypeName(),siteId));
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
					final BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
					final RegistrySummaryVO regSummaryVO = this.getGiftRegistryManager().getRegistryInfo(registryId, siteId);

					//If Registry status is not Active/Lead don't put it in session
					if(acceptableStatusesList.contains(registryStatus) && !pRequest.getRequestURI().contains("view_registry_guest.jsp")){
						sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, regSummaryVO);
					}
					if(!pRequest.getRequestURI().contains("view_registry_guest.jsp")){
					this.getGiftRegSessionBean().setRegistryOperation(BBBGiftRegistryConstants.OWNER_VIEW);
					}
					}
					pRequest.setParameter(CO_REG_FLAG,coRegFlag);
					pRequest.setParameter(REGISTRY_URL,
							this.getRegGuestViewURL());
					pRequest.setParameter(EVENTDATE, evDate);
					pRequest.setParameter(FUTURE_SHIPPING_DATE, futureShippingDate);
					pRequest.setParameter(REGISTRYSUMMARYVO,
							registrySummaryVO);
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
			//pRequest.setParameter(OUTPUT_ERROR_MSG, ERR_REGINFO_BIZ);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);

		} catch (final BBBSystemException sysExcep) {

			this.logError("BBBSystemException", sysExcep);
			pRequest.setParameter(OUTPUT_ERROR_MSG, ERR_REGINFO_SYS);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} /*catch (ParseException e) {
			logError("BBBSystemException", e);
			//pRequest.setParameter(OUTPUT_ERROR_MSG, ERR_REGINFO_BIZ);
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		}*/
		this.logDebug(" MxRegistryInfoDisplayDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");
		BBBPerformanceMonitor.end("MxRegistryInfoDisplayDroplet", "GetRegistryInfo");

	}

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
	 * Sets the profile.
	 *
	 * @param profile
	 *            the profile to set
	 */
	public void setProfile(final Profile profile) {
		this.profile = profile;
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
		this.contentManager = contentManager;
	}

}
