package com.bbb.commerce.giftregistry.formhandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import nl.captcha.Captcha;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringEscapeUtils;

import atg.commerce.CommerceException;
import atg.commerce.gifts.InvalidGiftTypeException;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.droplet.TagConversionException;
import atg.droplet.TagConverterManager;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.naming.ComponentName;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailException;
import net.sf.ezmorph.MorphException;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;

import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.exim.bean.EximCustomizedAttributesVO;
import com.bbb.commerce.exim.bean.EximImagePreviewVO;
import com.bbb.commerce.exim.bean.EximImagesVO;
import com.bbb.commerce.exim.bean.EximSummaryResponseVO;
import com.bbb.commerce.giftregistry.bean.AddItemsBean;
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean;
import com.bbb.commerce.giftregistry.droplet.RegistryEventDateComparator;
import com.bbb.commerce.giftregistry.manager.GiftRegistryRecommendationManager;
import com.bbb.commerce.giftregistry.vo.ItemDetailsVO;
import com.bbb.commerce.giftregistry.vo.ManageRegItemsResVO;
import com.bbb.commerce.giftregistry.vo.RegCopyResVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO;
import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.giftregistry.vo.RegistryTypes;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.commerce.giftregistry.vo.SetAnnouncementCardResVO;
import com.bbb.commerce.giftregistry.vo.ValidateAddItemsResVO;
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBEximConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.simplifyRegistry.RegistryInputVO;
import com.bbb.simplifyRegistry.RegistryInputsByTypeVO;
import com.bbb.simplifyRegistry.manager.SimplifyRegistryManager;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;
import com.bbb.vo.wishlist.WishListVO;
import com.bbb.wishlist.GiftListVO;

/**
 * This class provides convenient form handling methods for operating on the
 * current customer's gift registries. It can be used to create new gift
 * registries, edit gift registries and add items to the gift registry during
 * the browse and/or shopping process. Gift registry management is the core
 * functionality of this form handler. It controls creating, updating and item
 * management to all of the customer's gift registries. This includes creating,
 * updating, publishing, and deleting gift registries as well as adding items to
 * the registries. This functionality is invoked via the various handleXXX
 * methods of the form handler.
 *
 * @author sku134
 *
 */
public class GiftRegistryFormHandler extends GiftRegistryFormHandlerBean {
	
	private static final String CUSTOMIZATION_OFFERED_FLAG = "customizationOfferedFlag";
	/**	Variable of string password. */
	private String password;
	/**	Variable of ItemDetailsVO itemDetailsVO. */
	private List<ItemDetailsVO> itemDetailsVO;

	
	/**
	 * @return password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 
	 * @return itemDetailsVO
	 */
	public List<ItemDetailsVO> getItemDetailsVO() {
		return itemDetailsVO;
	}
	/**
	 * 
	 * @param itemDetailsVO the itemDetailsVO to set
	 */
	public void setItemDetailsVO(List<ItemDetailsVO> itemDetailsVO) {
		this.itemDetailsVO = itemDetailsVO;
	}



	/**
	 * @param pEventType
	 * @return returns list of all the RegistryInputVO's based on eventType
	 * @throws RepositoryException
	 */
	public RegistryInputsByTypeVO getRegistryInputsVO(String pEventType) throws RepositoryException {
		this.logDebug("GiftRegistryFormHandler getRegistryInputList method started ");
		RegistryInputsByTypeVO registryInputsByTypeVO = new RegistryInputsByTypeVO();
		try {
			registryInputsByTypeVO = getSimplifyRegistryManager().getRegInputsByRegType(pEventType);
		} catch (RepositoryException e) {
			logError("Error occurred in getRegistryInputsVo method when registryInputsByTypeVO",e);
			
		}
		this.logDebug("GiftRegistryFormHandler getRegistryInputList method END ");
		return registryInputsByTypeVO;
	}
	
	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return true/false
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleVerifyUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {

		String email = this.getRegistryVO().getPrimaryRegistrant().getEmail();
		BBBProfileTools profileTools = (BBBProfileTools) getProfileTools();
		final Dictionary<String, String> value = (Dictionary<String, String>) this.getValue();
		value.put(this.getPropertyManager().getLoginPropertyName(), email.toLowerCase());
		value.put(this.getPropertyManager().getEmailAddressPropertyName(), email.toLowerCase());
		if (StringUtils.isBlank(email)) {

			addFormException(
					new DropletException("User Already exists", BBBCoreConstants.ERR_USER_ALREADY_ASSOCIATED_TO_SITE));
			getSessionBean().setRegistredUser(true);
		} else {
			String profileStatus = null;
			try {
				profileStatus = profileTools.checkForRegistration(email);
			} catch (BBBBusinessException e) {
				if (isLoggingError()) {
					logError("Error while validating the profile is valid or not by mail id: " + e, e);
				}
			}
			if (!StringUtils.isBlank(profileStatus)) {
				if (profileStatus.equalsIgnoreCase("profile_already_exist")) {
					getSessionBean().setRegistryProfileStatus("regUserAlreadyExists");
					getSessionBean().setRegistredUser(true);
				} else if (profileStatus.equalsIgnoreCase("profile_available_for_extenstion")) {
					this.setMigrationFlag(true);
					setUserMigratedLoginProp(true);
					getSessionBean().setRegistredUser(true);
					getSessionBean().setRegistryProfileStatus("refProfileExtenssion");
				}

				else if (profileStatus.equalsIgnoreCase("Profile not found")) {
					getSessionBean().setRegistryProfileStatus("regNewUser");
					getSessionBean().setRegistredUser(false);
				}
			}

		}

		if (getErrorMap().isEmpty()) {
			getSessionBean().setUserEmailId(getLoginEmail());
		}

		this.checkFormRedirect(this.getVerifyUserErrorURLPage(), this.getVerifyUserErrorURLPage(), pRequest, pResponse);

		return true;
	}


	public void postLoginUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		logDebug("post login user");
		// Calling post login user with extra parameter which will avoid cart merging
		super.postLoginUser(pRequest, pResponse);
	}

	/**
	 * The method performs action when user clicks and selects a registry type
	 * from the drop down to create a registry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public final void handleRegistryTypes(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		this.logDebug(" handleRegistryTypes(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		// BBBH-391 | Client DOM XSRF changes
		
		if (StringUtils.isNotEmpty(getFromPage())) {
			setSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
			setErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));
		}

		if (this.getSessionBean().getRegistryTypesEvent() != null) {
			this.getSessionBean().setRegistryTypesEvent(null);
		}
		this.getSessionBean().setRegistryTypesEvent(this.getRegistryEventType());

		final MutableRepositoryItem userProfile = (MutableRepositoryItem) ServletUtil.getCurrentUserProfile();
		final String loginFrom = BBBWebServiceConstants.TXT_LOGIN_CREATE_REGISTRY;

		userProfile.setPropertyValue(BBBWebServiceConstants.LOGIN_FROM, loginFrom);

		this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
		this.logDebug("handleRegistryTypes(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");

	}

	/**
	 * This method is used to capture a session attribute for customer click on
	 * Manage Active registry Link.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public final void handleLoginToManageActiveRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		BBBPerformanceMonitor
				.start(GiftRegistryFormHandler.class.getName() + " : " + "handleLoginToManageActiveRegistry");
		this.logDebug(
				" handleLoginToManageActiveRegistry(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		this.getSessionBean().setMngActRegistry(BBBGiftRegistryConstants.LOGIN_TO_MANAGE_ACTIVE_REGISTRY);
		final MutableRepositoryItem userProfile = (MutableRepositoryItem) ServletUtil.getCurrentUserProfile();
		final String loginFrom = BBBWebServiceConstants.TXT_LOGIN_CREATE_REGISTRY;

		userProfile.setPropertyValue(BBBWebServiceConstants.LOGIN_FROM, loginFrom);

		this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);

		this.logDebug("handleLoginToManageActiveRegistry(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");

		BBBPerformanceMonitor
				.end(GiftRegistryFormHandler.class.getName() + " : " + "handleLoginToManageActiveRegistry");
	}

	/**
	 * The method performs action when user clicks and selects a registry type
	 * from the drop down to create a registry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public final void handleViewManageRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse)
					throws ServletException, IOException, BBBBusinessException, BBBSystemException {

		this.logDebug("handleViewManageRegistry(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		// BBBH-391 | Client DOM XSRF changes
		final String siteId = this.getCurrentSiteId();
		if (StringUtils.isNotEmpty(getFromPage())) {
			setSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
			setErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));
		}
		
		String registryId = null;
		String registryEventType = null;
		if (null != this.getRegistryIdEventType()) {
			final String[] registryIdEventType = this.getRegistryIdEventType().split("_");

			registryId = registryIdEventType[0];
			registryEventType = registryIdEventType[1];

			final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
					.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);

			final RegistrySummaryVO regSummaryVO = this.getGiftRegistryManager().getRegistryInfo(registryId, siteId);
			sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, regSummaryVO);
		}

		this.setSuccessURL(this.getSuccessURL() + REGISTRY_ID + registryId + EVENT_TYPE + registryEventType);

		this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
		this.logDebug("handleViewManageRegistry(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");

	}

	/**
	 * This method is used to view and edit active registry Link.
	 * 
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws BBBBusinessException
	 *             Signals that an BBBBusinessException has occurred.
	 * @throws BBBSystemException
	 *             Signals that an BBBSystemException has occurred.
	 */
	public final void handleViewEditRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse)
					throws ServletException, IOException, BBBBusinessException, BBBSystemException {

		this.logDebug("handleViewEditRegistry(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		String registryId = (String) pRequest.getParameter("registryId");
		String registryEventType = (String) pRequest.getParameter("eventType");

		final String siteId = this.getCurrentSiteId();

		if (null != registryEventType) {

			final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
					.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);

			/*
			 * getting the registry info for the registry id to be viewed or
			 * edited and placing the new registry info into the session
			 */

			final RegistrySummaryVO regSummaryVO = this.getGiftRegistryManager().getRegistryInfo(registryId, siteId);
			sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, regSummaryVO);
		}

		this.setSuccessURL(this.getViewEditSuccessURL() + REGISTRY_ID + registryId + EVENT_TYPE + registryEventType);

		this.setErrorURL(this.getViewEditFailureURL());

		this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);

		this.logDebug("handleViewManageRegistry(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");

	}

	/**
	 * @param pRequest
	 * @param pResponse
	 */
	private void preGiftRegCreateUser() {

		final Dictionary<String, String> value = (Dictionary<String, String>) this.getValue();
		final String emailId = this.getRegistryVO().getPrimaryRegistrant().getEmail();

		String firstName = this.getRegistryVO().getPrimaryRegistrant().getFirstName();
		String lastName = this.getRegistryVO().getPrimaryRegistrant().getLastName();

		value.put(this.getPropertyManager().getLoginPropertyName(), emailId.toLowerCase());
		value.put(this.getPropertyManager().getEmailAddressPropertyName(), emailId.toLowerCase());
		value.put(this.getPropertyManager().getAutoLoginPropertyName(), BBBCoreConstants.TRUE);
		this.setValueProperty(this.getPropertyManager().getReceiveEmailPropertyName(), BBBCoreConstants.YES);
		if (isShallowProfileChanges()) {
			value.put(this.getPropertyManager().getStatusPropertyName(), BBBCoreConstants.FULL_PROFILE_STATUS_VALUE);
		}
		value.put(this.getPropertyManager().getPasswordPropertyName(), getPassword());
		value.put(this.getPropertyManager().getConfirmPasswordPropertyName(), getPassword());
		value.put(this.getPropertyManager().getFirstNamePropertyName(), firstName);
		value.put(this.getPropertyManager().getLastNamePropertyName(), lastName);

	}

	private void preGiftRegUpdateUser() {

		final Dictionary<String, String> value = (Dictionary<String, String>) this.getValue();
		final String emailId = this.getRegistryVO().getPrimaryRegistrant().getEmail();

		String firstName = this.getRegistryVO().getPrimaryRegistrant().getFirstName();
		String lastName = this.getRegistryVO().getPrimaryRegistrant().getLastName();
		String primaryPhone = this.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone();
		String cellPhone = this.getRegistryVO().getPrimaryRegistrant().getCellPhone();

		value.put(this.getPropertyManager().getLoginPropertyName(), emailId.toLowerCase());
		value.put(this.getPropertyManager().getEmailAddressPropertyName(), emailId.toLowerCase());
		value.put(this.getPropertyManager().getAutoLoginPropertyName(), BBBCoreConstants.TRUE);
		this.setValueProperty(this.getPropertyManager().getReceiveEmailPropertyName(), BBBCoreConstants.YES);
		if (isShallowProfileChanges()) {
			value.put(this.getPropertyManager().getStatusPropertyName(), BBBCoreConstants.FULL_PROFILE_STATUS_VALUE);
		}
		value.put(this.getPropertyManager().getFirstNamePropertyName(), firstName);
		value.put(this.getPropertyManager().getLastNamePropertyName(), lastName);
		if (!BBBUtility.isEmpty(cellPhone)) {
			value.put(propertyManager.getMobileNumberPropertyName(), cellPhone);
		}
		if (!BBBUtility.isEmpty(primaryPhone)) {
			value.put(propertyManager.getPhoneNumberPropertyName(), primaryPhone);
		}
	}

	/**
	 * This method is used to create a registry for a user.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true / false
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */

	public  boolean handleCreateRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse)
					throws ServletException, IOException, BBBSystemException, BBBBusinessException {

		this.logDebug("GiftRegistryFormHandler.handleCreateRegistry() method start");
		this.getSessionBean().setSimplifyRegVO(this.getRegistryVO());
		// BBBH-391 | Client DOM XSRF changes
		final String siteId = this.getCurrentSiteId();
		if (StringUtils.isNotEmpty(getFromPage())) {
			setRegistryCreationSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
			setRegistryCreationErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()) + "?regType=" + this.getRegistryEventType());
		}

		final RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
		final String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
		if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
			boolean isFromThirdParty = false;
			if (pRequest.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST) != null) {
				isFromThirdParty = Boolean
						.parseBoolean(pRequest.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST));
			}
			String clientID = pRequest.getHeader(BBBGiftRegistryConstants.CLIENTID);
			final String emailId = this.getRegistryVO().getPrimaryRegistrant().getEmail();
			boolean isRecognizedUser = false;
			BBBProfileTools profileTools = (BBBProfileTools) getProfileTools();
			isRecognizedUser = profileTools.isRecognizedUser(pRequest, getProfile());
			if (getProfile().isTransient() || isRecognizedUser) {
				this.createTransientUserReg(pRequest, pResponse, siteId, emailId,
						isRecognizedUser, profileTools);

			}
			if (getFormError() && !getFormExceptions().isEmpty()) {
				return checkFormRedirect(this.getRegistryCreationSuccessURL(), this.getRegistryCreationErrorURL(), pRequest,
						pResponse);

			}

			if (BBBCoreConstants.DEFAULT_CHANNEL_VALUE.equalsIgnoreCase(BBBUtility.getChannel()) && !isFromThirdParty) {
				setSuccessURL(getRegistryCreationSuccessURL());
				setErrorURL(getRegistryCreationErrorURL());
			}
			pRequest.getParameter("favStoreId");
			this.logDebug("GiftRegistryFormHandler.handleCreateRegistry()  College Value"
					+ this.getRegistryVO().getEvent().getCollege());
			final Profile profile = (Profile) pRequest
					.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE));
			this.getSessionBean().setRegistryTypesEvent(this.getRegistryEventType());
			// trimming leading and trailing spaces for email id in create
			// registry
			// webservice.
			if (!StringUtils.isBlank(emailId)) {
				this.getRegistryVO().getPrimaryRegistrant().setEmail(emailId.trim());
			}

			
			this.setQasValues();

			// Setting the value for AFFILIATE_TAG column
			this.setAffiliateTag(clientID);

			// if user is transient then redirect to loggedInFailureURL
			if ((profile.isTransient() && isFromThirdParty) || !profile.isTransient()) {
				try {
					// adding code to keep simplify create validation separate
					// from XO(createRegistry) and regular update reg
					// validations.
					if (this.getCreateSimplified() != null && (this.getCreateSimplified()).equalsIgnoreCase("true"))
						this.preCreateSimplifyRegistry(pRequest, pResponse);
					else
						this.preCreateRegistry(pRequest, pResponse);

					if (getFormError()) {
						return this.checkFormRedirect(this.getRegistryCreationSuccessURL(), this.getRegistryCreationErrorURL(),
								pRequest, pResponse);
					}

					this.setWSDLParameters(siteId);
					this.logDebug("siteFlag: " + this.getRegistryVO().getSiteId());
					this.logDebug("userToken: " + this.getRegistryVO().getUserToken());
					this.logDebug("ServiceName: " + this.getCreateRegistryServiceName());

					if (null != this.getSessionBean().getRegistryTypesEvent()) {
						this.getRegistryVO().getRegistryType()
								.setRegistryTypeName(this.getSessionBean().getRegistryTypesEvent());
					}
					// Web Service call to create gift registry which will
					// return
					// registryId
					this.setProfileItem(isFromThirdParty, emailId, pRequest);

					// Set the registry status
					this.getRegistryVO().setStatus(getGiftRegistryManager().getGiftRegistryConfigurationByKey(
							BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY));
					// newly added fields
					this.getRegistryVO().setNetworkAffiliation(this.getRegistryVO().getNetworkAffiliation());

					// PS-21422 defect fixed. Set configured default store id in
					// preference store number in case when no
					// store is selected during registry creation.
					if (BBBUtility.isEmpty(this.getRegistryVO().getPrefStoreNum())) {
						this.getRegistryVO().setPrefStoreNum(getGiftRegistryManager()
								.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.REGISTRY_DEFAULT_STORE_ID));
					}
					this.setAffiliation();
					this.setEventDate(siteId);
					this.setShowerDate(siteId);
					this.setBirthDate(siteId);
					this.setShippingFutureDate(siteId);
					if (!(null != this.getCreateSimplified()
							&& (this.getCreateSimplified()).equalsIgnoreCase("true"))) {
						this.getRegistryVO().getPrimaryRegistrant().setAddressSelected(this.getRegContactAddress());
					}

					// If new address is not added to the registry and old
					// shipping
					// or
					// billing address is selected need to set that address to
					// registryVO
					final RegistryVO resVO = getGiftRegistryManager().setShippingBillingAddr(this.getRegistryVO(),
							this.getShippingAddress(), this.getFutureShippingAddress(), this.getProfile(),
							this.getRegistrantAddressFromWS(), this.getShippingAddressFromWS(),
							this.getFutureShippingAddressFromWS());
							
					if (!StringUtils.isBlank(getBabygenderStr())) {
						resVO.getEvent().setBabyGender(getBabygenderStr());
					} 

					// if registry type is college then add college name under
					// contact address vo's company field.
					if ((this.getRegistryVO().getRegistryType().getRegistryTypeName())
							.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_COLLEGE)) {
						this.getRegistryVO().getPrimaryRegistrant().getContactAddress()
								.setCompany(this.getRegistryVO().getEvent().getCollege());
					}

					if (StringUtils.isEmpty(resVO.getEvent().getGuestCount())) {
						resVO.getEvent().setGuestCount(BBBCoreConstants.STRING_ZERO);
					}
					resVO.setCreate(true);
					this.setRegistryVO(resVO);
					if (getFormError() && !getFormExceptions().isEmpty()) {
						return this.checkFormRedirect(this.getRegistryCreationSuccessURL(), this.getRegistryCreationErrorURL(),
								pRequest, pResponse);
					} else {
						setSessionObjects(pRequest, siteId);
					}

					String regId = this.getRegistryVO().getRegistryId();
					String eventType = getBbbCatalogTools()
							.getRegistryTypeName(this.getRegistryVO().getRegistryType().getRegistryTypeName(), siteId);
					String successURL = this.getSuccessURL() + "?registryId=" + regId + "&eventType=" + eventType
							+ "&hoorayModal=" + getHoorayModalFlag();
					if (this.getDesktop() != null && this.getDesktop().equalsIgnoreCase("true"))
						this.setRegistryCreationSuccessURL(successURL);

				} catch (final BBBBusinessException e) {
					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"BBBBusinessException from handleCreateRegistry of GiftRegistryFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);
					this.addFormException(new DropletException(
							this.getMessageHandler().getErrMsg(BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION,
									pRequest.getLocale().getLanguage(), null, null),
							BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION));
				}

				catch (final BBBSystemException e) {
					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"BBBSystemException from handleCreateRegistry of GiftRegistryFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005), e);
					this.addFormException(new DropletException(
							this.getMessageHandler().getErrMsg(BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION,
									pRequest.getLocale().getLanguage(), null, null),
							BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION));

				} catch (final TemplateEmailException e) {
					this.addFormException(new DropletException(
							this.getMessageHandler().getErrMsg(BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
									pRequest.getLocale().getLanguage(), null, null),
							BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION));
				} catch (final RepositoryException e1) {
					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"Repository Exception from handleCreateRegistry of GiftRegistryFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1001), e1);

					this.addFormException(new DropletException(
							this.getMessageHandler().getErrMsg(BBBGiftRegistryConstants.ERR_REGSEARCH_REPO_EXCEPTION,
									pRequest.getLocale().getLanguage(), null, null),
							BBBGiftRegistryConstants.ERR_REGSEARCH_REPO_EXCEPTION));
				} finally {
					if (rrm != null) {
						rrm.removeRequestEntry(myHandleMethod);
					}
				}
			} else {
				this.setSuccessURL(this.getRegistryCreationErrorURL());
			}
		}
		this.logDebug("GiftRegistryFormHandler.handleCreateRegistry() method ends");

		return this.checkFormRedirect(this.getRegistryCreationSuccessURL(), this.getRegistryCreationErrorURL(), pRequest,
				pResponse);
	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @param siteId
	 * @param emailId
	 * @param isRecognizedUser
	 * @param profileTools
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 */
	private void createTransientUserReg(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, final String siteId,
			final String emailId, boolean isRecognizedUser,
			BBBProfileTools profileTools) throws ServletException, IOException,
			BBBSystemException {
		
		preGiftRegCreateUser();
		String profileStatus = null;
		try {
			profileStatus = profileTools.checkForRegistration(emailId);
		} catch (BBBBusinessException e) {
			if (isLoggingError()) {
				logError("Error while validating the profile is valid or not by mail id: " + e);
			}
		}
		if ((!StringUtils.isBlank(profileStatus) && (profileStatus.equalsIgnoreCase("profile_already_exist")
				|| profileStatus.equalsIgnoreCase("profile_available_for_extenstion"))) || isRecognizedUser) {
			pRequest.setParameter("userCreatingRegistry", Boolean.TRUE);
			this.superLoginUser(pRequest, pResponse);
		} else {

			try {
				this.superHandleCreate(pRequest, pResponse);// create user
														// and logs in
			} catch (Exception e) {
				logError(e.getMessage(),e);
			}
			String memberId = null;
			if (!BBBUtility.isEmpty(this.getSessionBean().getLegacyMemberId())) {
				memberId = this.getSessionBean().getLegacyMemberId();
			}
			final String emailOptInFlag = this.isEmailOptIn() ? BBBCoreConstants.YES : BBBCoreConstants.NO;
			final String emailOptInBabyCAFlag = this.isEmailOptIn_BabyCA() ? BBBCoreConstants.YES
					: BBBCoreConstants.NO;

			profileTools.createSiteItemRedirect(emailId, siteId, memberId, null, emailOptInFlag,
					emailOptInBabyCAFlag);

		}
	}
	/**
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void superHandleCreate(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		super.handleCreate(pRequest, pResponse);
	}
	/**
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void superLoginUser(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		super.handleLoginUser(pRequest, pResponse);
	}

	/**
	 * To set QAS PO BOX values in RegistryVO.
	 */
	private void setQasValues() {
		if (this.getRegBG() != null && !this.getRegBG().isEmpty()) {
			this.getRegistryVO().setRegBG(this.getRegBG());
		}
		if (this.getCoRegBG() != null && !this.getCoRegBG().isEmpty()) {
			this.getRegistryVO().setCoRegBG(this.getCoRegBG());
		}

		if (this.getContactPoBoxStatus() != null
				&& this.getContactPoBoxStatus().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
			this.getRegistryVO().getPrimaryRegistrant().getContactAddress().setQasValidated(true);
		if (this.getContactPoBoxFlag() != null
				&& this.getContactPoBoxFlag().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
			this.getRegistryVO().getPrimaryRegistrant().getContactAddress().setPoBoxAddress(true);

		if (this.getShipPoBoxStatus() != null
				&& this.getShipPoBoxStatus().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
			this.getRegistryVO().getShipping().getShippingAddress().setQasValidated(true);
		if (this.getShipPoBoxFlag() != null
				&& this.getShipPoBoxFlag().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
			this.getRegistryVO().getShipping().getShippingAddress().setPoBoxAddress(true);

		if (this.getFuturePoBoxStatus() != null
				&& this.getFuturePoBoxStatus().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
			this.getRegistryVO().getShipping().getFutureshippingAddress().setQasValidated(true);
		if (this.getFuturePoBoxFlag() != null
				&& this.getFuturePoBoxFlag().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
			this.getRegistryVO().getShipping().getFutureshippingAddress().setPoBoxAddress(true);
	}

	/**
	 * @param clientID
	 */
	private void setAffiliateTag(String clientID) {
		if (clientID == null) {
			this.getRegistryVO().setAffiliateTag(BBBCoreConstants.BBB_DESKTOP);
		} else if (clientID.equalsIgnoreCase(BBBCoreConstants.MOBILE_CLIENT)) {
			this.getRegistryVO().setAffiliateTag(BBBCoreConstants.BBB_MOBILE);
		} else {
			this.getRegistryVO().setAffiliateTag(clientID);
		}
	}

	private boolean getHoorayModalFlag() {
		boolean hoorayModalFlag = false;
		try {
			List<String> hoorayModalOn = getBbbCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
					getHoorayModal());
			if (!BBBUtility.isListEmpty(hoorayModalOn)) {
				hoorayModalFlag = Boolean.parseBoolean(hoorayModalOn.get(0));
			}
		} catch (BBBSystemException bse) {
			this.logError("System Exception occured while fetching key(hoorayModalFlag) from FlagDrivenFunctions", bse);
			return hoorayModalFlag;
		} catch (BBBBusinessException bbe) {
			this.logError("Business Exception occured while fetching key(hoorayModalFlag) from FlagDrivenFunctions",
					bbe);
			return hoorayModalFlag;
		}
		return hoorayModalFlag;
	}
	
	/**
	 * 
	 * @param pRequest
	 * @param siteId
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws RepositoryException
	 * @throws TemplateEmailException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setSessionObjects(final DynamoHttpServletRequest pRequest, final String siteId)
			throws BBBBusinessException, BBBSystemException, RepositoryException, TemplateEmailException {
		String registryTypeEvent = null;
		RegistryResVO createRegistryResVO = null;
		RegistrySummaryVO regSummaryVO = null;
		createRegistryResVO = getGiftRegistryManager().createRegistry(this.getRegistryVO());
		if (createRegistryResVO == null) {
			final BBBSystemException sysExc = new BBBSystemException("Error while creating Registry");
			throw sysExc;
		} else if (createRegistryResVO.getServiceErrorVO().isErrorExists()) {
			this.createRegistryErrorExist(pRequest, createRegistryResVO);

		} else {
			final Cookie cookies[] = pRequest.getCookies();
			if(cookies != null){
			for (final Cookie cookie : cookies) {
				if (cookie.getName().equalsIgnoreCase(BBBGiftRegistryConstants.REGISTRY_TYPE_EVENT)) {
					registryTypeEvent = cookie.getValue();
					break;
				}
			}
			}
			if((registryTypeEvent != null) && (this.getSessionBean().getRegistryTypesEvent() != null)){
				if ((registryTypeEvent
						.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_WEDDING))
						&& (this.getSessionBean()
								.getRegistryTypesEvent()
								.equalsIgnoreCase(
										BBBGiftRegistryConstants.EVENT_TYPE_WEDDING) || this
								.getSessionBean()
								.getRegistryTypesEvent()
								.equalsIgnoreCase(
										BBBGiftRegistryConstants.EVENT_TYPE_CERMONY))) {
					this.getRegistryVO().setCookieType(BBBCoreConstants.WED_CHANNEL_REF);
				}
			 else if ((registryTypeEvent.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_BABY)) && this.getSessionBean().getRegistryTypesEvent()
					.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_BABY)) {
					this.getRegistryVO().setCookieType(BBBCoreConstants.THEBUMP_REF);
				}
			}

			// Add info to gift registry repository about the registrants
			this.getRegistryVO().setRegistryId(String.valueOf(createRegistryResVO.getRegistryId()));
			final String coreEmailPopupStatus = this.getCoRegEmailFoundPopupStatus();
			getGiftRegistryManager().giftRegistryRepoEntry(this.getRegistryVO(), coreEmailPopupStatus);

			// Update Registrant profile info
			this.getRegistryVO().getPrimaryRegistrant().setAddressSelected(this.getRegContactAddress());
			getGiftRegistryManager().updateRegistrantProfileInfo(this.getRegistryVO(), this.getShippingAddress(),
					this.getFutureShippingAddress(), this.getProfile(),
					(String) this.getSiteContext().getSite().getPropertyValue(DEFAULT_COUNTRY));

			// Send email to coregistrant
			final String coRegistrySubject = this.getMessageHandler().getPageLabel(LBL_EMAIL_CO_REGISTRY_SUBJECT,
					pRequest.getLocale().getLanguage(), null, null);

			String giftRegistryURL = null;
			if (siteId.contains("TBS")) {
				giftRegistryURL = getTbsEmailSiteMap().get(siteId) + getGuestRegistryUri();
			} else {
				giftRegistryURL = pRequest.getScheme() + BBBGiftRegistryConstants.SCHEME_APPEND + this.getHost(pRequest)
						+ getGuestRegistryUri() + this.getRegistryVO().getRegistryId() + EVENT_TYPE;
			}
			String accountLoginURL = null;
			if (siteId.contains("TBS")) {
				accountLoginURL = getTbsEmailSiteMap().get(siteId) + getLoginRedirectUrl();
			} else {
				accountLoginURL = pRequest.getScheme() + BBBGiftRegistryConstants.SCHEME_APPEND + this.getHost(pRequest)
						+ getLoginRedirectUrl();
			}

			getGiftRegistryManager().sendEmailToCoregistrant(giftRegistryURL, accountLoginURL, siteId,
					coRegistrySubject, this.getRegistryVO(), this.getCoRegEmailFoundPopupStatus(),
					this.getCoRegEmailNotFoundPopupStatus(), this.getGiftRegEmailInfo());

			// Invalidate registrylist and summaryVO in the session
//			final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
//					.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
			final HashMap sessionMap = this.getSessionBean().getValues();

			List<String> pListUserRegIds = null;
			pListUserRegIds = (List) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_LIST);
			if (pListUserRegIds != null) {
				pListUserRegIds.add(0, String.valueOf(this.getRegistryVO().getRegistryId()));
				this.getSessionBean().getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_LIST, pListUserRegIds);
				this.getSessionBean().getValues().put(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST, pListUserRegIds);
				// update the registrysummaryvo in the BBBsessionBean
//				final RegistrySummaryVO regSummaryVO = this.getGiftRegistryManager()
//						.getRegistryInfo(this.getRegistryVO().getRegistryId(), siteId);
//				sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, regSummaryVO);
				
				
			} else {
				pListUserRegIds = new ArrayList<String>();
				pListUserRegIds.add(0, String.valueOf(this.getRegistryVO().getRegistryId()));
				this.getSessionBean().getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_LIST, pListUserRegIds);
				this.getSessionBean().getValues().put(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST, pListUserRegIds);
				// update the registrysummaryvo in the BBBsessionBean
//				final RegistrySummaryVO regSummaryVO = this.getGiftRegistryManager()
//						.getRegistryInfo(this.getRegistryVO().getRegistryId(), siteId);
//				this.getSessionBean().getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, regSummaryVO);
			}
			// update the registrysummaryvo in the BBBsessionBean
			regSummaryVO = this.getGiftRegistryManager().getRegistryInfo(this.getRegistryVO().getRegistryId(), siteId);
			this.getSessionBean().getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, regSummaryVO);
			
			Profile profile = this.getProfile();			
//			Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
				if (!profile.isTransient()) {
					List<RegistrySkinnyVO> registrySkinnyVOList = getGiftRegistryManager()
							.getAcceptableGiftRegistries(profile, siteId);
					RegistryEventDateComparator eventDateComparator = new RegistryEventDateComparator();
					eventDateComparator.setSortOrder(2);
					Collections.sort(registrySkinnyVOList, eventDateComparator);
					this.getSessionBean().getValues().put(BBBCoreConstants.REGISTRY_SKINNY_VO_LIST, registrySkinnyVOList);
					this.getSessionBean().getValues().put("size", registrySkinnyVOList.size());
				}
		}
		this.setCreateRegistryResVO(createRegistryResVO);

		if (null != this.getRegistryVO() && null != this.getRegistryVO().getRegistryType() && null != regSummaryVO && null != regSummaryVO.getRegistryType() && null != regSummaryVO.getRegistryType().getRegistryTypeDesc()) {
			this.getRegistryVO().getRegistryType().setRegistryTypeDesc(regSummaryVO.getRegistryType().getRegistryTypeDesc());
//			this.getRegistryVO().getRegistryType().setRegistryTypeDesc(getBbbCatalogTools()
//					.getRegistryTypeName(this.getRegistryVO().getRegistryType().getRegistryTypeName(), siteId));
		}
		this.getGiftRegSessionBean().setResponseHolder(this.getRegistryVO());
		this.getGiftRegSessionBean().setRegistryOperation(BBBGiftRegistryConstants.GR_CREATE);
	}

	private void setShippingFutureDate(final String siteId) {
		// If future shipping date
		if ((this.getRegistryVO().getShipping() != null)
				&& !StringUtils.isEmpty(this.getRegistryVO().getShipping().getFutureShippingDate())) {
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				this.getRegistryVO().getShipping().setFutureShippingDateWS(BBBUtility
						.convertCADateIntoWSFormat(this.getRegistryVO().getShipping().getFutureShippingDate()));
			} else {
				this.getRegistryVO().getShipping().setFutureShippingDateWS(
						BBBUtility.convertDateIntoWSFormat(this.getRegistryVO().getShipping().getFutureShippingDate()));
			}
		}
	}

	private void setBirthDate(final String siteId) {
		if (!StringUtils.isEmpty(this.getRegistryVO().getEvent().getBirthDate())) {
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				this.getRegistryVO().getEvent().setBirthDateWS(
						BBBUtility.convertCADateIntoWSFormat(this.getRegistryVO().getEvent().getBirthDate()));
			} else {
				this.getRegistryVO().getEvent().setBirthDateWS(
						BBBUtility.convertDateIntoWSFormat(this.getRegistryVO().getEvent().getBirthDate()));
			}

		}
	}

	private void setShowerDate(final String siteId) {
		if (!StringUtils.isEmpty(this.getRegistryVO().getEvent().getShowerDate())) {
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				this.getRegistryVO().getEvent().setShowerDateWS(
						BBBUtility.convertCADateIntoWSFormat(this.getRegistryVO().getEvent().getShowerDate()));
			} else {
				this.getRegistryVO().getEvent().setShowerDateWS(
						BBBUtility.convertDateIntoWSFormat(this.getRegistryVO().getEvent().getShowerDate()));
			}
		}
	}

	private void setAffiliation() {
		this.getRegistryVO().setPrefRegContMeth(BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_METHOD);
		this.getRegistryVO().setPrefRegContTime(BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_TIME);
		this.getRegistryVO().setPrefCoregContMeth(BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_METHOD);
		this.getRegistryVO().setPrefCoregContTime(BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_TIME);
		this.getRegistryVO().setSignup(BBBGiftRegistryConstants.SIGN_UP_NO);
		this.getRegistryVO().setHint(BBBGiftRegistryConstants.REGISTRY);
		this.getRegistryVO().setWord(BBBGiftRegistryConstants.WORD);

		if ((this.getRegistryVO().getOptInWeddingOrBump() == null)
				|| (BBBCoreConstants.FALSE).equalsIgnoreCase(this.getRegistryVO().getOptInWeddingOrBump())) {
			this.getRegistryVO().setAffiliateOptIn(BBBGiftRegistryConstants.FLAG_N);
		} else {
			this.getRegistryVO().setAffiliateOptIn(BBBGiftRegistryConstants.FLAG_Y);
		}
	}
	
	/**
	 * 
	 * @param isFromThirdParty
	 * @param email
	 * @param request
	 */
	private void setProfileItem(boolean isFromThirdParty, String email, DynamoHttpServletRequest request) {
		MutableRepositoryItem pProfileItem;
		String profileId = request.getParameter(BBBAccountConstants.PROFILE_ID);

		if (null == profileId) {
			final Profile profile = (Profile) request
					.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE));
			profileId = (String) profile.getPropertyValue("id");
		}
		String token = request.getHeader(BBBCoreConstants.CLIENT_ID_PARM);
		String siteHeader = request.getHeader("X-bbb-site-id");
		if ((BBBCoreConstants.TRUE).equalsIgnoreCase(this.getCoRegEmailFoundPopupStatus())) {
			// Set CO Registrant profile Id to registryVO
			pProfileItem = getGiftRegistryManager().getProfileItemFromEmail(
					this.getRegistryVO().getCoRegistrant().getEmail(), this.getRegistryVO().getSiteId());
			this.getRegistryVO().getCoRegistrant().setProfileId(pProfileItem.getRepositoryId());
			this.getRegistryVO().getCoRegistrant().setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_Y);
		} else {
			this.getRegistryVO().getCoRegistrant().setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_N);
		}

		pProfileItem = getGiftRegistryManager().getProfileItemFromEmail(
				this.getRegistryVO().getPrimaryRegistrant().getEmail(), this.getRegistryVO().getSiteId());
		if (null == pProfileItem) {
			Profile profile = (Profile) request
					.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE));

			try {
				pProfileItem = (MutableRepositoryItem) this.getProfileRepository().getItem(profileId,
						BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME);
			} catch (RepositoryException e) {
				logError("Repsoitory Exception occurred in getting Profile Item", e);
			}
		}
		if (!isFromThirdParty) {
			if (null != pProfileItem) {
				this.getRegistryVO().getPrimaryRegistrant().setProfileId(pProfileItem.getRepositoryId());
			} else {
				this.getRegistryVO().getPrimaryRegistrant().setProfileId(profileId);
				this.addFormException(new DropletException("Invalid Email Provided."));
			}
		} else {
			if (!BBBUtility.isEmpty(profileId)) {
				RepositoryItem item = null;
				try {
					item = this.getProfileRepository().getItem(profileId, BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME);
				} catch (RepositoryException e) {
					logError("Repsoitory Exception occurred in getting Profile Item", e);
				}
				if (item != null) {
					Object emailId = item.getPropertyValue(BBBCoreConstants.EMAIL);
					Object source = item.getPropertyValue(SOURCE);
					if (source != null && emailId != null && ((String) emailId).equalsIgnoreCase(email)
							&& ((String) source).equalsIgnoreCase(token)) {
						if (this.validateSite(siteHeader, item)) {
							this.getRegistryVO().getPrimaryRegistrant().setProfileId(item.getRepositoryId());
						} else {
							this.addFormException(new DropletException("Invalid Site Association is provided.",
									ERR_INVALID_USER_SITE_ASSOCIATION));
							logError("Invalid Site Association is provided.");
						}
					} else {
						logError(BBBCoreConstants.INVALID_PROFILE_ID_IS_PROVIDED);
						this.addFormException(new DropletException(BBBCoreConstants.INVALID_PROFILE_ID_IS_PROVIDED,
								ERR_CREATE_REG_INVALID_PROFILEID));
					}
				} else {
					this.addFormException(new DropletException(BBBCoreConstants.INVALID_PROFILE_ID_IS_PROVIDED,
							ERR_CREATE_REG_INVALID_PROFILEID));
				}
			} else {
				this.addFormException(
						new DropletException("Please provide a profileId", ERR_CREATE_REG_INVALID_PROFILEID));
			}
		}
		if (StringUtils.isEmpty(this.getRegistryVO().getCoRegistrant().getProfileId())) {
			this.getRegistryVO().getCoRegistrant().setProfileId(null);
		}
	}

	/*
	 * Set WSDL Site Parameters
	 */
	private void setWSDLParameters(final String pSiteId) throws BBBSystemException, BBBBusinessException {
		this.getGiftRegSessionBean().clear();
		this.getRegistryVO().getRegistryType().setRegistryTypeName(this.getSessionBean().getRegistryTypesEvent());
		this.getRegistryVO().setSiteId(
				getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId).get(0));
		this.getRegistryVO().setUserToken(getBbbCatalogTools()
				.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
				.get(0));
		this.getRegistryVO().setServiceName(this.getCreateRegistryServiceName());
	}

	private void createRegistryErrorExist(final DynamoHttpServletRequest pRequest,
			final RegistryResVO createRegistryResVO) {

		int errorId = 0;
		String errorMessage = "";

		if (createRegistryResVO.getServiceErrorVO() != null) {
			errorId = createRegistryResVO.getServiceErrorVO().getErrorId();
		}
		if ((createRegistryResVO.getServiceErrorVO() != null)
				&& (createRegistryResVO.getServiceErrorVO().getErrorDisplayMessage() != null)) {
			errorMessage = createRegistryResVO.getServiceErrorVO().getErrorDisplayMessage();
		}

		if (errorId == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS) {
			if (this.isLoggingError()) {
				this.logError(
						LogMessageFormatter.formatMessage(pRequest, errorMessage + BBBCoreConstants.COLON + errorId,
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1082));
			}
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_GIFT_REG_INPUT_FIELD_ERROR,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INPUT_FIELD_ERROR));
		}

		if (errorId == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR) {
			if (this.isLoggingError()) {
				this.logError(
						LogMessageFormatter.formatMessage(pRequest, errorMessage + BBBCoreConstants.COLON + errorId,
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1083));
			}
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
		}

		if (errorId == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN) {
			if (this.isLoggingError()) {
				this.logError(
						LogMessageFormatter.formatMessage(pRequest, errorMessage + BBBCoreConstants.COLON + errorId,
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1084));
			}
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));
		}
		if (errorId == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT) {
			if (this.isLoggingError()) {
				this.logError(
						LogMessageFormatter.formatMessage(pRequest, errorMessage + BBBCoreConstants.COLON + errorId,
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));
			}

			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
		}

		if (errorId == 0) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBBusinessException from createRegistryErrorExist of GiftRegistryformHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004));
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION));

		} else {
			this.addFormException(new DropletFormException(errorMessage,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR + errorId, errorMessage));

		}
	}

	/**
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return To persist registry data in session and redirect to target url on
	 *         click of start browsing button.
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 */
	public boolean handleBuyOffStartBrowsing(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException, BBBSystemException {
		this.logDebug("GiftRegistryFormHandler.handleBuyOffStartBrowsing() method start");
		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		final String siteId = this.getCurrentSiteId();
		String successURL = this.getBuyoffStartBrowsingSuccessURL();
		RegistrySummaryVO regSummaryVO;
		this.logDebug("BuyOff RegistryId Value" + this.getRegistryId());

		try {
			regSummaryVO = getGiftRegistryManager().getRegistryInfo(this.getRegistryId(), siteId);
			sessionBean.setBuyoffStartBrowsingSummaryVO(regSummaryVO);

			this.logDebug("BuyOff RegistryVO Value" + regSummaryVO);
		} catch (BBBBusinessException e) {
			this.logError("Error in handle buyOff ", e);
		}
		this.logDebug("BuyOffSuccessURL Value" + successURL);

		return this.checkFormRedirect(successURL, this.getErrorURL(), pRequest, pResponse);
	}

	/**
	 * This method is used to update a registry for a user.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true / false
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */

	public boolean handleUpdateRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		this.logDebug("GiftRegistryFormHandler.handleUpdateRegistry() method start");
		try {
			boolean isCoRegOwner = this.getRegistryVO().isCoRegOwner();
			this.isRegistryOwnedByProfile(this.getRegistryVO().getRegistryId());
			final String siteId = this.getCurrentSiteId();
			// Code for updating alternate number to registry
			if (BBBUtility.isEmpty(pRequest.getParameter(BBBCoreConstants.ALTERNATE_NUM))
					&& ((!BBBUtility.isEmpty(this.getDeactivateRegistry()) && this.getDeactivateRegistry().equalsIgnoreCase(BBBCoreConstants.FALSE)) || (!BBBUtility.isEmpty(this.getMakeRegistryPublic()) && this.getMakeRegistryPublic().equalsIgnoreCase(BBBCoreConstants.TRUE))))
			{
				this.preUpdateRegistry(pRequest, pResponse);
			} else if (BBBUtility.isEmpty(pRequest.getParameter(BBBCoreConstants.ALTERNATE_NUM))
					&& !BBBUtility.isEmpty(this.getUpdateSimplified())
					&& (this.getUpdateSimplified()).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
				this.preUpdateSimplifiedRegistry(pRequest, pResponse);
			}
			else if(BBBUtility.isEmpty(pRequest.getParameter(BBBCoreConstants.ALTERNATE_NUM))
					&& ((!BBBUtility.isEmpty(this.getDeactivateRegistry()) && this.getDeactivateRegistry().equalsIgnoreCase(BBBCoreConstants.TRUE)) || (!BBBUtility.isEmpty(this.getMakeRegistryPublic()) && this.getMakeRegistryPublic().equalsIgnoreCase(BBBCoreConstants.FALSE))))
			{
				this.preUpdatePrivateRegistry(pRequest, pResponse);
			}
			
			
			String eventType = getBbbCatalogTools().getRegistryTypeName(this.getSessionBean().getRegistryTypesEvent(),
					siteId);
			if (BBBCoreConstants.DEFAULT_CHANNEL_VALUE.equalsIgnoreCase(BBBUtility.getChannel())) {
				this.setSuccessURL(this.getRegistryUpdateSuccessURL() + "?eventType=" + eventType + "&registryId="
						+ this.getRegistryVO().getRegistryId());
				this.setErrorURL(
						this.getRegistryUpdateErrorURL() + "?registryId=" + this.getRegistryVO().getRegistryId());
			}
			if (this.getFormError()) {
				return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
			}
			this.setProfileValues();
			this.setServiceParamter(siteId);
			this.setAffiliationValues();
			this.setEventDate(siteId);
			this.setDates(siteId);
						
			this.setPoBoxAddress(BBBUtility.isPOBoxAddress(
					this.getRegistryVO().getPrimaryRegistrant().getContactAddress().getAddressLine1(),
					this.getRegistryVO().getPrimaryRegistrant().getContactAddress().getAddressLine2()));
			this.setPoBoxAddress(isPoBoxAddress());
			
			this.setQasValues();

			if (this.getMakeRegistryPublic() != null)
			{
				if(this.getMakeRegistryPublic().equalsIgnoreCase(BBBCoreConstants.TRUE))
				{
					this.getRegistryVO().setIsPublic("1");
				}
				else
				{
					this.getRegistryVO().setIsPublic("0");
				}
			}
			else if(this.getDeactivateRegistry() != null)
			{
				if(this.getDeactivateRegistry().equalsIgnoreCase(BBBCoreConstants.TRUE))
				{
					this.getRegistryVO().setIsPublic("0");
				}
				else
				{
					this.getRegistryVO().setIsPublic("1");
				}
			}
			

			// Code for updating alternate number to registry
			if (BBBUtility.isEmpty(pRequest.getParameter(BBBCoreConstants.ALTERNATE_NUM))) {
				this.getRegistryVO().getPrimaryRegistrant().setAddressSelected(this.getRegContactAddress());
				// If New Address is not added to the Registry and old shipping
				// or
				// Billing address is selected need to set that address to
				// registryVO
				final RegistryVO resVO = getGiftRegistryManager().setShippingBillingAddr(this.getRegistryVO(),
						this.getShippingAddress(), this.getFutureShippingAddress(), this.getProfile(),
						this.getRegistrantAddressFromWS(), this.getShippingAddressFromWS(), this.getFutureShippingAddressFromWS());

				// if registry type is college then add college name under
				// contact address vo's company field.
				if ((this.getRegistryVO().getRegistryType().getRegistryTypeName())
						.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_COLLEGE)) {
					this.getRegistryVO().getPrimaryRegistrant().getContactAddress()
							.setCompany(this.getRegistryVO().getEvent().getCollege());
				}
				if (!StringUtils.isBlank(getBabygenderStr())) {
					resVO.getEvent().setBabyGender(getBabygenderStr());
				}

				if (StringUtils.isEmpty(resVO.getEvent().getGuestCount())) {
					resVO.getEvent().setGuestCount(BBBCoreConstants.STRING_ZERO);
				}
				this.setRegistryVO(resVO);

			} else {
				this.setSuccessURL("");
				this.setErrorURL("");
			}
			final RegistryResVO registryResVO = getGiftRegistryManager().updateRegistry(this.getRegistryVO());

			if ((registryResVO != null) && registryResVO.getServiceErrorVO().isErrorExists()) {
				this.setFormExceptionsForErrors(pRequest, registryResVO);
			} else {
				try {
					// Updating code to update ATG profile on successful update
					// registry
					if (!isCoRegOwner) {
						preGiftRegUpdateUser();

						this.logDebug("Calling Handle Method From Edit Registry Form is " + this.getUpdateSimplified());

						// RMT - 41925 && //GFT-1263 updating mobile no in
						// profile starts
						if (!BBBUtility.isEmpty(this.getUpdateSimplified())
								&& (this.getUpdateSimplified()).equalsIgnoreCase(BBBCoreConstants.TRUE)) {
							this.superHandleUpdate(pRequest, pResponse);
						} else {
							// BBBP-9620 ||DSK | Getting system error on moving
							// LTL item
							// to registry thorough alternate ph modal
							pRequest.setParameter(BBBCoreConstants.IS_FROM_GIFT_REGISTRY, BBBCoreConstants.TRUE);
							final BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();
							final String mobilenumberProperty = this.getPropertyManager().getMobileNumberPropertyName();
							String mobileNumber = this.getStringValueProperty(mobilenumberProperty);
							super.updatePhoneNumberMob(pRequest, profileTools, mobileNumber);
						}
					}
					//RMT - 41925 && //GFT-1263 updating mobile no in profile ends
					
					this.sendCoregistrantEmail(pRequest, siteId);
					
					this.setSessionBeanValues(pRequest, siteId);

				} catch (final BBBBusinessException e) {
					this.logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException in sending EMAIL",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_20131), e);
				}
			}

			if (null != this.getRegistryVO().getRegistryType().getRegistryTypeName()) {
				this.getRegistryVO().getRegistryType().setRegistryTypeDesc(getBbbCatalogTools()
						.getRegistryTypeName(this.getRegistryVO().getRegistryType().getRegistryTypeName(), siteId));
			}
			this.getGiftRegSessionBean().setResponseHolder(this.getRegistryVO());
			this.getGiftRegSessionBean().setRegistryOperation(BBBGiftRegistryConstants.GR_UPDATE);

		} catch (final BBBBusinessException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBBusinessException from handleUpdateRegistry of GiftRegistryformHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION));
		} catch (final BBBSystemException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBSystemException from handleUpdateRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION));
		} catch (final TemplateEmailException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Template Exception from handleUpdateRegistry of GiftRegistryformHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1085), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION));
		} catch (final RepositoryException e1) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Repository Exception from handleUpdateRegistry of GiftRegistryFormhandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1001), e1);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_REGSEARCH_REPO_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_REGSEARCH_REPO_EXCEPTION));
		}
		this.logDebug("GiftRegistryFormHandler.handleUpdateRegistry() method ends");

		// If Success go to update registry confirmation page.
		return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
	}
	/**
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void superHandleUpdate(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		super.handleUpdate(pRequest, pResponse);
	}

	/**
	 * @param pRequest
	 * @param siteId
	 * @throws BBBBusinessException
	 * @throws RepositoryException
	 * @throws BBBSystemException
	 */
	private void setSessionBeanValues(final DynamoHttpServletRequest pRequest,
			final String siteId) throws BBBBusinessException,
			RepositoryException, BBBSystemException {
		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
				.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		final String registryId = this.getRegistryVO().getRegistryId();
		if ((sessionBean != null) && (null != registryId) && (null != sessionBean.getRegistryVOs())
				&& (null != sessionBean.getRegistryVOs().get(registryId))) {
			sessionBean.getRegistryVOs().remove(registryId);
		}
		if (sessionBean != null && sessionBean.getValues().get(BBBCoreConstants.REGISTRY_SKINNY_VO_LIST) != null) {
			Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
			if (!profile.isTransient()) {
				List<RegistrySkinnyVO> registrySkinnyVOList = getGiftRegistryManager()
						.getAcceptableGiftRegistries(profile, siteId);
				RegistryEventDateComparator eventDateComparator = new RegistryEventDateComparator();
				eventDateComparator.setSortOrder(2);
				Collections.sort(registrySkinnyVOList, eventDateComparator);
				sessionBean.getValues().put(BBBCoreConstants.REGISTRY_SKINNY_VO_LIST, registrySkinnyVOList);
				sessionBean.getValues().put("size", registrySkinnyVOList.size());
				HashMap<String, RegistryVO> registryVOs = sessionBean.getRegistryVOs();
				if (registryVOs == null) {
					registryVOs = new HashMap<String, RegistryVO>();
				}
				registryVOs.put(this.getRegistryVO().getRegistryId(), this.getRegistryVO());
				sessionBean.setRegistryVOs(registryVOs);
			}
		}
		String updateSessionReqd=BBBCoreConstants.TRUE;
		if(pRequest.getParameter(BBBCoreConstants.UPDATE_REG_SUMMARY_REQUIRED) !=null){
			 updateSessionReqd=pRequest.getParameter(BBBCoreConstants.UPDATE_REG_SUMMARY_REQUIRED);
		}
		if(BBBCoreConstants.TRUE.equalsIgnoreCase(updateSessionReqd)){
		RegistryResVO registryResponseVO = getGiftRegistryManager().getRegistryInfoFromEcomAdmin(
		        this.getRegistryVO().getRegistryId(), this.getSiteContext().getSite().getId());
		final RegistrySummaryVO regSummaryVO = registryResponseVO.getRegistrySummaryVO();
		//BBBI-4808 || Setting Event Type 
		if(BBBUtility.isEmpty(regSummaryVO.getEventType()))
		{
			regSummaryVO.setEventType(this.getEventType());
		}
		
		if (regSummaryVO.getEventDate() !=null) {
						if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
							regSummaryVO.setEventDate(BBBUtility.convertWSDateToCAFormat(regSummaryVO.getEventDate()));
							
						}
					}
					
		if(sessionBean != null){
			sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, regSummaryVO);
			sessionBean.getValues().put(this.getRegistryVO().getRegistryId() + REG_SUMMARY_KEY_CONST,
					registryResponseVO);
		}
		}
	}


	/**
	 * 
	 * @param pEventType
	 * @param pRequest
	 */
	private void eventFormSimplifyUpdateValidation(final String pEventType, final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.eventFormSimplifyUpdateValidation() method start");
		final String siteId = this.getCurrentSiteId();
		String dateFormat = BBBCoreConstants.DATE_FORMAT;

		if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
			dateFormat = BBBCoreConstants.CA_DATE_FORMAT;
		}
		if (!StringUtils.isEmpty(this.getRegistryVO().getNetworkAffiliation())) {
			this.validateNetworkAffiliation(this.getRegistryVO().getNetworkAffiliation(),
					BBBGiftRegistryConstants.NETWORK_AFFILIATION, pRequest);
		}
		if (pEventType.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_WEDDING)) {
			if (!StringUtils.isEmpty(this.getRegistryVO().getEvent().getEventDate())) {
				this.validateEventDate(this.getRegistryVO().getEvent().getEventDate(),
						BBBGiftRegistryConstants.EVENT_DATE, dateFormat, pRequest);
			}
			if (!StringUtils.isEmpty(this.getRegistryVO().getEvent().getShowerDate())) {
				this.validateShowerDate(this.getRegistryVO().getEvent().getShowerDate(),
						BBBGiftRegistryConstants.SHOWER_DATE, dateFormat, pRequest);
			}
			if (!StringUtils.isEmpty(this.getRegistryVO().getEvent().getGuestCount())) {
				this.validateNumberOfGuest(this.getRegistryVO().getEvent().getGuestCount(),
						BBBGiftRegistryConstants.NUMBER_OF_GUESTS, pRequest);
			}
		} else if (pEventType.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_BABY)) {
			final String decorTheme = this.getRegistryVO().getEvent().getBabyNurseryTheme();
			if ((null != decorTheme) && (decorTheme.length() > 1)) {
				this.getRegistryVO().getEvent().setBabyNurseryTheme(
						BBBUtility.EncodeNurseryDecorTheme(this.getRegistryVO().getEvent().getBabyNurseryTheme()));
			}
			if (!StringUtils.isEmpty(this.getRegistryVO().getEvent().getBabyName())) {
				this.validateBabyName(this.getRegistryVO().getEvent().getBabyName(), BBBGiftRegistryConstants.BABY_NAME,
						pRequest);
			}
			if (!StringUtils.isEmpty(this.getRegistryVO().getEvent().getEventDate())) {
				this.validateEventDate(this.getRegistryVO().getEvent().getEventDate(),
						BBBGiftRegistryConstants.EVENT_DATE, dateFormat, pRequest);
			}
			if (!StringUtils.isEmpty(this.getRegistryVO().getEvent().getShowerDate())) {
				this.validateShowerDate(this.getRegistryVO().getEvent().getShowerDate(),
						BBBGiftRegistryConstants.SHOWER_DATE, dateFormat, pRequest);
			}
		} else if (pEventType.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_COLLEGE)) {
			if (!StringUtils.isEmpty(this.getRegistryVO().getEvent().getEventDate())) {
				this.validateEventDate(this.getRegistryVO().getEvent().getEventDate(),
						BBBGiftRegistryConstants.EVENT_DATE, dateFormat, pRequest);
			}
			if (!StringUtils.isEmpty(this.getRegistryVO().getEvent().getCollege())) {
				this.validateCollege(this.getRegistryVO().getEvent().getCollege(),
						BBBGiftRegistryConstants.EVENT_TYPE_COLLEGE, pRequest);
			}
		} else {
			if (!StringUtils.isEmpty(this.getRegistryVO().getEvent().getEventDate())) {
				this.validateEventDate(this.getRegistryVO().getEvent().getEventDate(),
						BBBGiftRegistryConstants.EVENT_DATE, dateFormat, pRequest);
			}
		}

		this.logDebug("GiftRegistryFormHandler.eventFormSimplifyUpdateValidation() method ends");
	}

	/**
	 * This method is used to validate CoRegistrant fields in registrant page
	 * during registry creation process.
	 *
	 * @param pEventType
	 *            the event type
	 */
	private void coregistrantSimplifyUpdateValidation(final String pEventType,
			final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.coregistrantSimplifyUpdateValidation() method start");

		// VALIDATE COREGISTRANT FIRST NAME
		if (!StringUtils.isEmpty(this.getRegistryVO().getCoRegistrant().getFirstName())
				&& !BBBUtility.isValidFirstName(this.getRegistryVO().getCoRegistrant().getFirstName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_FIRST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
		}

		// VALIDATE COREGISTRANT LAST NAME
		if (!StringUtils.isEmpty(this.getRegistryVO().getCoRegistrant().getLastName())
				&& !BBBUtility.isValidLastName(this.getRegistryVO().getCoRegistrant().getLastName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_LAST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

		}

		// VALIDATE COREGISTRANT EMAIL
		if (!BBBUtility.isEmpty(this.getRegistryVO().getCoRegistrant().getEmail())
				&& !BBBUtility.isValidEmail(this.getRegistryVO().getCoRegistrant().getEmail())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					BBBCoreConstants.GIFTREGISTRY_INVALID_CO_REGISTRANT_EMAIL,
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1041));

			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_INVALID_EMAIL_LENGTH,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_EMAIL,
					BBBGiftRegistryConstants.ERR_CREATE_REG_INVALID_EMAIL_LENGTH));
		}

		this.logDebug("GiftRegistryFormHandler.coregistrantValidation() method ends");
		final String siteId = this.getSiteContext().getSite().getId();
		if ((null != siteId) && (null != this.getRegistryVO().getCoRegistrant().getFirstName())) {
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				this.getRegistryVO().getCoRegistrant().getContactAddress().setCountry(BBBGiftRegistryConstants.CA);
			} else {
				this.getRegistryVO().getCoRegistrant().getContactAddress().setCountry(BBBGiftRegistryConstants.US);
			}
		}

	}

	/**
	 * This method is called before updating Gift Registry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public void preUpdateSimplifiedRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws BBBBusinessException, BBBSystemException {
		this.logDebug("GiftRegistryFormHandler.preUpdateSimplifiedRegistry() method start");

		this.getFormExceptions().clear();
		this.registrantValidationUpdateSimplified(this.getSessionBean().getRegistryTypesEvent(), pRequest);
		this.eventFormSimplifyUpdateValidation(this.getSessionBean().getRegistryTypesEvent(), pRequest);
		this.coregistrantSimplifyUpdateValidation(this.getSessionBean().getRegistryTypesEvent(), pRequest);

		// validate new shippingAdddress
		if (!StringUtils.isEmpty(this.getShippingAddress())
				&& NEW_SHIPPING_ADDRESS.equalsIgnoreCase(this.getShippingAddress())) {
			this.shippingNewAddressValidation(pRequest);
		}

		boolean isUserMoving = false;

		// if future address date is not selected
		if (StringUtils.isEmpty(this.getFutureShippingDateSelected())
				|| this.getFutureShippingDateSelected().equalsIgnoreCase("false")) {
			this.getRegistryVO().getShipping().setFutureshippingAddress(null);
			this.getRegistryVO().getShipping().setFutureShippingDate(null);
			this.setFutureShippingAddress(null);
		} else if (NEW_FUTURE_SHIPPING_ADDRESS.equalsIgnoreCase(this.getFutureShippingAddress())) {
			isUserMoving=true;
			if(StringUtils.isEmpty(this.getRegistryVO().getShipping().getFutureShippingDate()))
			{
				this.addFormException(new DropletFormException("err_create_reg_empty_future_date",
						this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
								+ "futureAddressDate",
								"err_create_reg_empty_future_date"));
			}
			this.shippingFutureAddressValidation(pRequest);
			if ((this.getRegistryVO().getShipping().getFutureshippingAddress() != null)
					&& (this.getRegistryVO().getPrimaryRegistrant() != null)) {
				// Set Primary Registrant phone as futureShipping phone
				this.getRegistryVO().getShipping().getFutureshippingAddress()
						.setPrimaryPhone(this.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone());
			}
		}
	if((!StringUtils.isEmpty(this.getFutureShippingDateSelected()) && this.getFutureShippingDateSelected().equalsIgnoreCase("true")) && (this.getRegistryVO().getRegistryId() != null
			&& !StringUtils.isEmpty(this.getRegistryVO().getShipping().getFutureShippingDate()))) {
			if(!isUserMoving) {
					this.addFormException(new DropletFormException("err_create_reg_address_line1_invalid_future",
						this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ "futureAddressDate",
										"err_create_reg_address_line1_invalid_future"));
			}
			this.futureShipDateValidation(this.getRegistryVO().getShipping().getFutureShippingDate(), true, pRequest);
		
	}
		this.logDebug("GiftRegistryFormHandler.preUpdateSimplifiedRegistry() method ends");

	}
	
	/**
	 * 
	 * @param pRequest
	 * @param registryResVO
	 */
	private void setFormExceptionsForErrors(final DynamoHttpServletRequest pRequest,
			final RegistryResVO registryResVO) {
		if (!BBBUtility.isEmpty(registryResVO.getServiceErrorVO().getErrorDisplayMessage()) && (registryResVO
				.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS))// Technical
		{
			if (this.isLoggingError()) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"Input field invalid from setFormExceptionsForErrors of GiftRegistryformHandler webservice error code="
								+ registryResVO.getServiceErrorVO().getErrorId(),
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1086));
			}
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_GIFT_REG_INPUT_FIELD_ERROR,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INPUT_FIELD_ERROR));
		}
		if (!BBBUtility.isEmpty(registryResVO.getServiceErrorVO().getErrorDisplayMessage()) && (registryResVO
				.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR))// Technical
		{
			if (this.isLoggingError()) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"Fatal error from setFormExceptionsForErrors of GiftRegistryformHandler webservice error code="
								+ registryResVO.getServiceErrorVO().getErrorId(),
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1087));
			}
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
		}
		if (!BBBUtility.isEmpty(registryResVO.getServiceErrorVO().getErrorDisplayMessage())
				&& (registryResVO.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN))// Technical
		{
			if (this.isLoggingError()) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"Either user token or site flag invalid from setFormExceptionsForErrors of GiftRegistryformHandler webservice error code="
								+ registryResVO.getServiceErrorVO().getErrorId(),
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1088));
			}
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));
		}
		if (!BBBUtility.isEmpty(registryResVO.getServiceErrorVO().getErrorDisplayMessage()) && (registryResVO
				.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT))// Technical
		{

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry input fields format error from Create/UpdateRegistry of "
							+ WEB_SERVICE_ERROR_CODE + registryResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
		}

		if (registryResVO.getServiceErrorVO().getErrorMessage() == null) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBBusinessException from setFormExceptionsForErrors of GiftRegistryformHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004));
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION));

		} else {
			this.addFormException(new DropletFormException(registryResVO.getServiceErrorVO().getErrorMessage(),
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ registryResVO.getServiceErrorVO().getErrorId(),
					registryResVO.getServiceErrorVO().getErrorMessage()));

		}
	}
	
	/**
	 * 
	 * @param pRequest
	 * @param siteId
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws RepositoryException
	 * @throws TemplateEmailException
	 */
	@SuppressWarnings("unchecked")
	public void sendCoregistrantEmail(final DynamoHttpServletRequest pRequest, final String siteId)
			throws BBBSystemException, BBBBusinessException, RepositoryException, TemplateEmailException {
		// Add info to gift registry repository about the registrants
		if (null != this.getSessionBean().getRegistryTypesEvent()) {
			this.getRegistryVO().getRegistryType().setRegistryTypeName(this.getSessionBean().getRegistryTypesEvent());
		}
		getGiftRegistryManager().giftRegistryRepoUpdate(this.getRegistryVO(), this.getCoRegEmailFoundPopupStatus(),
				this.getCoRegEmailNotFoundPopupStatus());

		// Update Registrant profile info
		getGiftRegistryManager().updateRegistrantProfileInfo(this.getRegistryVO(), this.getShippingAddress(),
				this.getFutureShippingAddress(), this.getProfile(),
				(String) this.getSiteContext().getSite().getPropertyValue(DEFAULT_COUNTRY));

		// send email to coregistrant
		final String coRegistrySubject = this.getLblTxtTemplateManager().getPageLabel(LBL_EMAIL_CO_REGISTRY_SUBJECT,
				pRequest.getLocale().getLanguage(), null, null);

		String giftRegistryURL = null;
		if (siteId.contains("TBS")) {
			giftRegistryURL = getTbsEmailSiteMap().get(siteId) + getGuestRegistryUri();
		} else {
			giftRegistryURL = pRequest.getScheme() + BBBGiftRegistryConstants.SCHEME_APPEND + this.getHost(pRequest)
					+ getGuestRegistryUri() + this.getRegistryVO().getRegistryId() + EVENT_TYPE;
		}
		String accountLoginURL = null;
		if (siteId.contains("TBS")) {
			accountLoginURL = getTbsEmailSiteMap().get(siteId) + getLoginRedirectUrl();
		} else {
			accountLoginURL = pRequest.getScheme() + BBBGiftRegistryConstants.SCHEME_APPEND + this.getHost(pRequest)
					+ getLoginRedirectUrl();
		}

		getGiftRegistryManager().sendEmailToCoregistrant(giftRegistryURL, accountLoginURL, siteId, coRegistrySubject,
				this.getRegistryVO(), this.getCoRegEmailFoundPopupStatus(), this.getCoRegEmailNotFoundPopupStatus(),
				this.getGiftRegEmailInfo());

		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		// update the registrysummaryvo in the BBBsessionBean
		final RegistrySummaryVO regSummaryVO = getGiftRegistryManager()
				.getRegistryInfo(this.getRegistryVO().getRegistryId(), siteId);
		regSummaryVO.setEventDate(this.getRegistryVO().getEvent().getEventDate());
		if (this.getRegistryVO().getCoRegistrant().getFirstName() != null) {
			regSummaryVO.setCoRegistrantFirstName(this.getRegistryVO().getCoRegistrant().getFirstName());
		}
		sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, regSummaryVO);
	}

	public final void setEventDate(final String siteId) {
		final String giftRegistryEventDate = this.getRegistryVO().getEvent().getEventDate();
		if (!StringUtils.isEmpty(giftRegistryEventDate)) {
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				this.getRegistryVO().getEvent()
						.setEventDateWS(BBBUtility.convertCADateIntoWSFormat(giftRegistryEventDate));
			} else {
				this.getRegistryVO().getEvent()
						.setEventDateWS(BBBUtility.convertUSDateIntoWSFormat(giftRegistryEventDate));
			}
		}
	}

	private void setAffiliationValues() {
		// Newly added fields
		this.getRegistryVO().setNetworkAffiliation(this.getRegistryVO().getNetworkAffiliation());
		this.getRegistryVO().setPrefRegContMeth(BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_METHOD);
		this.getRegistryVO().setPrefRegContTime(BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_TIME);
		this.getRegistryVO().setPrefCoregContMeth(BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_METHOD);

		// PS-21422 defect fixed. Set configured default store id in preference
		// store number in case when no
		// store is selected during registry update.
		if (BBBUtility.isEmpty(this.getRegistryVO().getPrefStoreNum())) {
			this.getRegistryVO().setPrefStoreNum(getGiftRegistryManager()
					.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.REGISTRY_DEFAULT_STORE_ID));
		}
		this.getRegistryVO().setPrefCoregContTime(BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_TIME);
		this.getRegistryVO().setSignup(BBBGiftRegistryConstants.SIGN_UP_NO);
		this.getRegistryVO().setHint(BBBGiftRegistryConstants.REGISTRY);
		this.getRegistryVO().setWord(BBBGiftRegistryConstants.WORD);
		if ((this.getRegistryVO().getOptInWeddingOrBump() == null)
				|| (BBBCoreConstants.FALSE).equalsIgnoreCase(this.getRegistryVO().getOptInWeddingOrBump())) {
			this.getRegistryVO().setAffiliateOptIn(BBBGiftRegistryConstants.FLAG_N);
		} else {
			this.getRegistryVO().setAffiliateOptIn(BBBGiftRegistryConstants.FLAG_Y);
		}
	}
	
	/**
	 * 
	 * @param pSiteId
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void setServiceParamter(final String pSiteId) throws BBBSystemException, BBBBusinessException {
		this.getGiftRegSessionBean().clear();
		if (null != this.getSessionBean().getRegistryTypesEvent()) {
			this.getRegistryVO().getRegistryType().setRegistryTypeName(this.getSessionBean().getRegistryTypesEvent());
		}
		this.getRegistryVO().setSiteId(
				getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId).get(0));
		this.getRegistryVO().setUserToken(getBbbCatalogTools()
				.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
				.get(0));
		this.getRegistryVO().setServiceName(this.getUpdateRegServiceName());

		this.logDebug("siteFlag: " + this.getRegistryVO().getSiteId());
		this.logDebug("userToken: " + this.getRegistryVO().getUserToken());
		this.logDebug("ServiceName: " + this.getUpdateRegServiceName());
	}

	public String getCurrentSiteId() {
		final String siteId = this.getSiteContext().getSite().getId();
		return siteId;
	}
	
	/**
	 * 
	 * @param siteId
	 */
	private void setDates(final String siteId) {
		if (!StringUtils.isEmpty(this.getRegistryVO().getEvent().getShowerDate())) {
			final String[] temp = this.getRegistryVO().getEvent().getShowerDate().split(BBBCoreConstants.SLASH);
			String showerDate = null;
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				showerDate = temp[2] + temp[1] + temp[0];
			} else {
				showerDate = temp[2] + temp[0] + temp[1];
			}
			this.getRegistryVO().getEvent().setShowerDateWS(showerDate);
		}
		if (!StringUtils.isEmpty(this.getRegistryVO().getEvent().getBirthDate())) {
			final String[] temp = this.getRegistryVO().getEvent().getBirthDate().split(BBBCoreConstants.SLASH);
			String birthDate = null;
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				birthDate = temp[2] + temp[1] + temp[0];
			} else {
				birthDate = temp[2] + temp[0] + temp[1];
			}
			this.getRegistryVO().getEvent().setBirthDateWS(birthDate);
		}

		// Future Shipping Date
		if ((this.getRegistryVO().getShipping() != null)
				&& !StringUtils.isEmpty(this.getRegistryVO().getShipping().getFutureShippingDate())) {
			final String futureShippingDate = this.getRegistryVO().getShipping().getFutureShippingDate();
			String futShippingDate = null;
			if (futureShippingDate != null) {
				if (futureShippingDate.contains(BBBCoreConstants.SLASH)) {
					final String temp[] = futureShippingDate.split(BBBCoreConstants.SLASH);

					if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
						futShippingDate = temp[2] + temp[1] + temp[0];
					} else {
						futShippingDate = temp[2] + temp[0] + temp[1];
					}
				} else if (futureShippingDate.length() == 8) {
					futShippingDate = futureShippingDate;
				}
			}
			this.getRegistryVO().getShipping().setFutureShippingDateWS(futShippingDate);
		}
	}

	private void setProfileValues() {
		MutableRepositoryItem pProfileItem;
		if (BBBUtility.isEmpty(this.getRegistryVO().getCoRegistrant().getEmail())) {
			this.getRegistryVO().getCoRegistrant().setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_N);
		} else {
			// Set CO Registrant Profile Id to registryVO
			pProfileItem = getGiftRegistryManager().getProfileItemFromEmail(
					this.getRegistryVO().getCoRegistrant().getEmail(), this.getRegistryVO().getSiteId());
			if (pProfileItem == null) {
				this.getRegistryVO().getCoRegistrant().setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_N);
			} else {
				this.getRegistryVO().getCoRegistrant().setProfileId(pProfileItem.getRepositoryId());
				this.getRegistryVO().getCoRegistrant().setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_Y);
			}
		}
		String profileId = (String) getProfile().getPropertyValue("id");
		this.getRegistryVO().getPrimaryRegistrant().setProfileId(profileId);
		if (StringUtils.isEmpty(this.getRegistryVO().getCoRegistrant().getProfileId())) {
			this.getRegistryVO().getCoRegistrant().setProfileId(null);
		}
	}
	
	/**
	 * 
	 * @param pEventType
	 * @param pRequest
	 */
	private void eventFormValidation(final String pEventType, final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.eventFormValidation() method start");
		final String siteId = this.getCurrentSiteId();
		String dateFormat = BBBCoreConstants.DATE_FORMAT;

		if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
			dateFormat = BBBCoreConstants.CA_DATE_FORMAT;
		}

		if (pEventType.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_WEDDING)) {
			this.validateEventDate(this.getRegistryVO().getEvent().getEventDate(), BBBGiftRegistryConstants.EVENT_DATE,
					dateFormat, pRequest);
			this.validateShowerDate(this.getRegistryVO().getEvent().getShowerDate(),
					BBBGiftRegistryConstants.SHOWER_DATE, dateFormat, pRequest);
			this.validateNumberOfGuest(this.getRegistryVO().getEvent().getGuestCount(),
					BBBGiftRegistryConstants.NUMBER_OF_GUESTS, pRequest);
		} else if (pEventType.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_BABY)) {
			final String decorTheme = this.getRegistryVO().getEvent().getBabyNurseryTheme();
			if ((null != decorTheme) && (decorTheme.length() > 1)) {
				this.getRegistryVO().getEvent().setBabyNurseryTheme(
						BBBUtility.EncodeNurseryDecorTheme(this.getRegistryVO().getEvent().getBabyNurseryTheme()));
			}
			this.validateBabyName(this.getRegistryVO().getEvent().getBabyName(), BBBGiftRegistryConstants.BABY_NAME,
					pRequest);
			this.validateEventDate(this.getRegistryVO().getEvent().getEventDate(), BBBGiftRegistryConstants.EVENT_DATE,
					dateFormat, pRequest);
			this.validateShowerDate(this.getRegistryVO().getEvent().getShowerDate(),
					BBBGiftRegistryConstants.SHOWER_DATE, dateFormat, pRequest);
		} else if (pEventType.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_COLLEGE)) {
			this.validateEventDate(this.getRegistryVO().getEvent().getEventDate(), BBBGiftRegistryConstants.EVENT_DATE,
					dateFormat, pRequest);
			this.validateCollege(this.getRegistryVO().getEvent().getCollege(),
					BBBGiftRegistryConstants.EVENT_TYPE_COLLEGE, pRequest);
		} else {
			this.validateEventDate(this.getRegistryVO().getEvent().getEventDate(), BBBGiftRegistryConstants.EVENT_DATE,
					dateFormat, pRequest);
		}

		this.logDebug("GiftRegistryFormHandler.eventFormValidation() method ends");

	}

	/**
	 * This method is called before creating gift registy.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public void preCreateRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws BBBBusinessException, BBBSystemException {

		this.logDebug("GiftRegistryFormHandler.preUpdateRegistry() method start");

		this.getFormExceptions().clear();
		this.eventFormValidation(this.getSessionBean().getRegistryTypesEvent(), pRequest);
		this.registrantValidation(this.getSessionBean().getRegistryTypesEvent(), pRequest);

		if (this.checkCoregistrantValidationStat()) {

			this.coregistrantValidation(this.getSessionBean().getRegistryTypesEvent(), pRequest);

		}

		// validate new shippingAdddress
		if (NEW_SHIPPING_ADDRESS.equalsIgnoreCase(this.getShippingAddress())) {
			this.shippingNewAddressValidation(pRequest);
		}

		// if future address date is not selected
		if (StringUtils.isEmpty(this.getFutureShippingDateSelected())
				|| this.getFutureShippingDateSelected().equalsIgnoreCase("false")) {
			this.getRegistryVO().getShipping().setFutureshippingAddress(null);
			this.getRegistryVO().getShipping().setFutureShippingDate(null);
			this.setFutureShippingAddress(null);
		} else if (NEW_FUTURE_SHIPPING_ADDRESS.equalsIgnoreCase(this.getFutureShippingAddress())) {
			this.shippingFutureAddressValidation(pRequest);
			if ((this.getRegistryVO().getShipping().getFutureshippingAddress() != null)
					&& (this.getRegistryVO().getPrimaryRegistrant() != null)) {
				// Set Primary Registrant phone as futureShipping phone
				this.getRegistryVO().getShipping().getFutureshippingAddress()
						.setPrimaryPhone(this.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone());
			}
		}
		if (this.getRegistryVO().getRegistryId() != null) {
			this.futureShipDateValidation(this.getRegistryVO().getShipping().getFutureShippingDate(), true, pRequest);
		} else {
			this.futureShipDateValidation(this.getRegistryVO().getShipping().getFutureShippingDate(), false, pRequest);
		}

		this.logDebug("GiftRegistryFormHandler.preCreateRegistry() method ends");

	}

	/**
	 * This method is called before creating gift registy.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public void preCreateSimplifyRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws BBBBusinessException, BBBSystemException {

		this.logDebug("GiftRegistryFormHandler.preUpdateRegistry() method start");

		this.getFormExceptions().clear();

		final String siteId = this.getCurrentSiteId();

		String eventType = getBbbCatalogTools().getRegistryTypeName(this.getSessionBean().getRegistryTypesEvent(),
				siteId);

		RegistryInputsByTypeVO registryInputsByTypeVO = new RegistryInputsByTypeVO();

		List<RegistryInputVO> registryAllInputList = new ArrayList<RegistryInputVO>();
		try {
			registryInputsByTypeVO = this.getRegistryInputsVO(eventType);
			if (registryInputsByTypeVO != null) {
				registryAllInputList = registryInputsByTypeVO.getRegistryInputList();
			}
		} catch (RepositoryException e) {
			logError("Error in retrieving BCC Registry Mandatory Input fields" + e.getMessage(), e);
		}

		if (null != registryAllInputList) {
			this.eventCreateSimplifyFormValidation(this.getSessionBean().getRegistryTypesEvent(), pRequest,
					registryAllInputList);

			this.registrantValidationSimplified(pRequest, registryAllInputList);

			if (this.checkCoregistrantValidationStat()) {
				this.coregistrantCreateSimplifyValidation(eventType, pRequest, registryAllInputList);
			}

			

			// validate new shippingAdddress
			if ((isFieldRequiresValidation(registryAllInputList, BBBCoreConstants.SHOW_SHIPPING_ADDRESS)
					|| (!isFieldRequiresValidation(registryAllInputList, BBBCoreConstants.SHOW_SHIPPING_ADDRESS)
							&& isFieldDisplayedOnForm(registryAllInputList, BBBCoreConstants.SHOW_SHIPPING_ADDRESS)
							&& !StringUtils.isEmpty(this.getRegistryVO().getShipping().getShippingAddress().getAddressLine1()))) 
							&& (NEW_SHIPPING_ADDRESS.equalsIgnoreCase(this.getShippingAddress()))) {
					this.shippingNewAddressValidation(pRequest);
			}
			
			boolean isUserMoving = false;
			
			// if future address date is not selected
			if (null != this.getFutureShippingDateSelected()) {
				if (StringUtils.isEmpty(this.getFutureShippingDateSelected())
						|| this.getFutureShippingDateSelected().equalsIgnoreCase("false")) {
					this.getRegistryVO().getShipping().setFutureshippingAddress(null);
					this.getRegistryVO().getShipping().setFutureShippingDate(null);
					this.setFutureShippingAddress(null);
				} else if ((isFieldRequiresValidation(registryAllInputList, BBBCoreConstants.SHOW_FUTURE_SHIPPING_ADDRESS)
						|| (!isFieldRequiresValidation(registryAllInputList, BBBCoreConstants.SHOW_FUTURE_SHIPPING_ADDRESS)
								&& isFieldDisplayedOnForm(registryAllInputList, BBBCoreConstants.SHOW_FUTURE_SHIPPING_ADDRESS)
								&& !StringUtils.isEmpty(this.getRegistryVO().getShipping().getFutureshippingAddress()
										.getAddressLine1())))|| NEW_FUTURE_SHIPPING_ADDRESS.equalsIgnoreCase(this.getFutureShippingAddress())) {
						
						isUserMoving=true;
						if(StringUtils.isEmpty(this.getRegistryVO().getShipping().getFutureShippingDate()))
						{
							this.addFormException(new DropletFormException("err_create_reg_empty_future_date",
									this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
											+ "futureAddressDate",
											"err_create_reg_empty_future_date"));
						}
						this.shippingFutureAddressValidation(pRequest);
						if ((this.getRegistryVO().getShipping().getFutureshippingAddress() != null)
								&& (this.getRegistryVO().getPrimaryRegistrant() != null)) {
							// Set Primary Registrant phone as futureShipping
							// phone
							this.getRegistryVO().getShipping().getFutureshippingAddress()
									.setPrimaryPhone(this.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone());
						}
				}
			}
		if(!StringUtils.isEmpty(this.getFutureShippingDateSelected()) && this.getFutureShippingDateSelected().equalsIgnoreCase("true"))	{
			
				if (isFieldRequiresValidation(registryAllInputList, BBBCoreConstants.FUTURE_SHIPPING_DATE)
						|| (!isFieldRequiresValidation(registryAllInputList, BBBCoreConstants.FUTURE_SHIPPING_DATE)
								&& isFieldDisplayedOnForm(registryAllInputList, BBBCoreConstants.FUTURE_SHIPPING_DATE)
								&& !StringUtils.isEmpty(this.getRegistryVO().getShipping().getFutureShippingDate()))) {
					if(!isUserMoving){
							this.addFormException(new DropletFormException("err_create_reg_address_line1_invalid_future",
									this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
											+ "futureAddressDate",
											"err_create_reg_address_line1_invalid_future"));
					}
					if (this.getRegistryVO().getRegistryId() != null) {
					this.futureShipDateValidation(this.getRegistryVO().getShipping().getFutureShippingDate(), true,
							pRequest);
			} else {
					this.futureShipDateValidation(this.getRegistryVO().getShipping().getFutureShippingDate(), false,
							pRequest);
				}
			}
		}
		} else {
			logError("Error in retrieving BCC Registry Input fields");
		}
		
		
		// adding code for registry public/private

		boolean isPublic = regStatusAsPerRegInputs(registryAllInputList, this.getRegistryVO());
		if (null != registryInputsByTypeVO && isPublic) {
			isPublic = registryInputsByTypeVO.isPublic();
		}
		if (isPublic)
			this.getRegistryVO().setIsPublic("1");
		else {
			this.getRegistryVO().setIsPublic("0");
		}

		this.logDebug("GiftRegistryFormHandler.preCreateRegistry() method ends");

	}
	
	
	
	Boolean regStatusAsPerRegInputs(List<RegistryInputVO> registryAllInputList, RegistryVO registryVO){		
		boolean isPublic =true;
		if (!BBBUtility.isListEmpty(registryAllInputList)) {
			for (RegistryInputVO registryInput : registryAllInputList) {
				if ((registryInput.getFieldName()).equalsIgnoreCase(BBBCoreConstants.SHOW_SHIPPING_ADDRESS)){
					 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getShipping().getShippingAddress().getAddressLine1())){
						isPublic=false;
						break;
					 }
					}else if ((registryInput.getFieldName()).equalsIgnoreCase(BBBCoreConstants.SHOW_CONTACT_ADDRESS)){
						 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getContactAddress().getAddressLine1())){
							isPublic=false;
							break;
						 } 
					}else
						if ((registryInput.getFieldName()).equalsIgnoreCase(BBBCoreConstants.SHOW_FUTURE_SHIPPING_ADDRESS)){
							 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getShipping().getFutureshippingAddress().getAddressLine1())){
								isPublic=false;
								break;
							 }
							}else
								if ((registryInput.getFieldName()).equalsIgnoreCase(BBBCoreConstants.FUTURE_SHIPPING_DATE)){
									 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getShipping().getFutureShippingDate())){
										isPublic=false;
										break;
									 }
									}else if ((registryInput.getFieldName()).equalsIgnoreCase("eventDate")){
										 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getEvent().getEventDate())){
												isPublic=false;
												break;
											 }
											}else if ((registryInput.getFieldName()).equalsIgnoreCase("showerDate")){
												 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getEvent().getShowerDate())){
														isPublic=false;
														break;
													 }
													}else if ((registryInput.getFieldName()).equalsIgnoreCase("babyGender")){
														 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getEvent().getBabyGender())){
																isPublic=false;
																break;
															 }
															} else if ((registryInput.getFieldName()).equalsIgnoreCase("babyName")){
																 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getEvent().getBabyName())){
																		isPublic=false;
																		break;
																	 }
																	} else if ((registryInput.getFieldName()).equalsIgnoreCase(BBBCoreConstants.NURSERY_THEME)){
																		 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getEvent().getBabyNurseryTheme())){
																				isPublic=false;
																				break;
																			 }
																			} else if ((registryInput.getFieldName()).equalsIgnoreCase("college")){
																				 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getEvent().getCollege())){
																						isPublic=false;
																						break;
																					 }
																					}else if ((registryInput.getFieldName()).equalsIgnoreCase("phoneNumber")){
																						 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone())){
																								isPublic=false;
																								break;
																							 }
																							}else if ((registryInput.getFieldName()).equalsIgnoreCase("mobileNumber")){
																								 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getCellPhone())){
																										isPublic=false;
																										break;
																									 }
																									}else if ((registryInput.getFieldName()).equalsIgnoreCase("CoRegistrantFirstName")){
																										 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getCoRegistrant().getFirstName())){
																												isPublic=false;
																												break;
																											 }
																											}else if ((registryInput.getFieldName()).equalsIgnoreCase("CoRegistrantLastName")){
																												 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getCoRegistrant().getLastName())){
																														isPublic=false;
																														break;
																													 }
																													} else if ((registryInput.getFieldName()).equalsIgnoreCase("CoRegistrantEmail")){
																														 if(registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getCoRegistrant().getEmail())){
																																isPublic=false;
																																break;
																															 }
																															}else if (((registryInput.getFieldName()).equalsIgnoreCase("numberOfGuests")) && (registryInput.isRequiredToMakeRegPublic() && StringUtils.isEmpty(this.getRegistryVO().getEvent().getGuestCount()))){
																																		isPublic=false;
																																		break;
																																	}
				}
			}		
		
		return isPublic;
	}

	/**
	 * This method is called before updating Gift Registry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public void preUpdateRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws BBBBusinessException, BBBSystemException {
		this.logDebug("GiftRegistryFormHandler.preUpdateRegistry() method start");

		this.getFormExceptions().clear();

		this.registrantValidation(this.getSessionBean().getRegistryTypesEvent(), pRequest);
		this.eventFormValidation(this.getSessionBean().getRegistryTypesEvent(), pRequest);

		if (this.checkCoregistrantValidationStat()) {
			this.coregistrantValidation(this.getSessionBean().getRegistryTypesEvent(), pRequest);
		}

		// validate new shippingAdddress
		if (NEW_SHIPPING_ADDRESS.equalsIgnoreCase(this.getShippingAddress())) {
			this.shippingNewAddressValidation(pRequest);
		}

		// if future address date is not selected
		if (StringUtils.isEmpty(this.getFutureShippingDateSelected())
				|| this.getFutureShippingDateSelected().equalsIgnoreCase("false")) {
			this.getRegistryVO().getShipping().setFutureshippingAddress(null);
			this.getRegistryVO().getShipping().setFutureShippingDate(null);
			this.setFutureShippingAddress(null);
		} else if (NEW_FUTURE_SHIPPING_ADDRESS.equalsIgnoreCase(this.getFutureShippingAddress())) {
			this.shippingFutureAddressValidation(pRequest);
			if ((this.getRegistryVO().getShipping().getFutureshippingAddress() != null)
					&& (this.getRegistryVO().getPrimaryRegistrant() != null)) {
				// Set Primary Registrant phone as futureShipping phone
				this.getRegistryVO().getShipping().getFutureshippingAddress()
						.setPrimaryPhone(this.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone());
			}
		}
		if (this.getRegistryVO().getRegistryId() != null) {
			this.futureShipDateValidation(this.getRegistryVO().getShipping().getFutureShippingDate(), true, pRequest);
		} else {
			this.futureShipDateValidation(this.getRegistryVO().getShipping().getFutureShippingDate(), false, pRequest);
		}

		this.logDebug("GiftRegistryFormHandler.preUpdateRegistry() method ends");

	}
	
	/**
	 * This method is called before updating private Gift Registry. For private, only first name last name validations
	 * will take place. And contact address validation will take place if it exists 
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public void preUpdatePrivateRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws BBBBusinessException, BBBSystemException {
		this.logDebug("GiftRegistryFormHandler.preUpdatePrivateRegistry() method start");
		
				// VALIDATE REGISTRANT FIRST NAME
				if (BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getFirstName())
						|| !BBBUtility.isValidFirstName(this.getRegistryVO().getPrimaryRegistrant().getFirstName())) {
					this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
							this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
									+ BBBGiftRegistryConstants.COREG_FIRST_NAME,
							BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
				}

				// VALIDATE REGISTRANT LAST NAME
				if (BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getLastName())
						|| !BBBUtility.isValidFirstName(this.getRegistryVO().getPrimaryRegistrant().getLastName())) {
					this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
							this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
									+ BBBGiftRegistryConstants.COREG_LAST_NAME,
							BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

				}
				
				// VALIDATE REGISTRANT CONTACT ADDRESS IF EXISTS
				if(!BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getContactAddress().getAddressLine1())){
				this.registrantAddressValidation(pRequest);
				}
		this.logDebug("GiftRegistryFormHandler.preUpdatePrivateRegistry() method ends");
	}

	/**
	 * This method is used to send card announcement for a user's registry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true / false
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public boolean handleAnnouncementCardCount(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException, BBBSystemException {

		this.logDebug("GiftRegistryFormHandler.handleAnnouncementCardCount() method start");
		try {
			// BBBH-391 | Client DOM XSRF changes
			final String siteId = this.getSiteContext().getSite().getId();
			if (StringUtils.isNotEmpty(getFromPage())) {
				setSuccessURL(pRequest.getContextPath()
						+ getSuccessUrlMap().get(getFromPage()));
				setErrorURL(pRequest.getContextPath()
						+ getErrorUrlMap().get(getFromPage()));
			}
	
			this.setRegistryVO(this.getGiftRegSessionBean().getRegistryVO());
			String registryAnnouncement = (String) pRequest.getParameter("registryAnnouncement");
			if (registryAnnouncement != null) {
				int regAccount = Integer.parseInt(registryAnnouncement);
				this.getRegistryVO().setNumRegAnnouncementCards(regAccount);
			}

			this.getRegistryVO().setSiteId(getBbbCatalogTools()
					.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
			this.getRegistryVO().setUserToken(getBbbCatalogTools()
					.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
					.get(0));
			this.getRegistryVO().setServiceName(this.getAnnCardCountServiceName());
			this.logDebug("siteId: " + this.getRegistryVO().getSiteId());
			this.logDebug("userToken: " + this.getRegistryVO().getUserToken());
			this.logDebug("ServiceName: " + this.getAnnCardCountServiceName());

			final SetAnnouncementCardResVO setAnnouncementCardResVO = this.getGiftRegistryManager()
					.assignAnnouncementCardCount(this.getRegistryVO());

			if (!BBBUtility.isValidateRegistryId(this.getRegistryVO().getRegistryId())
					|| setAnnouncementCardResVO.getServiceErrorVO().isErrorExists()) {

				if (!BBBUtility.isEmpty(setAnnouncementCardResVO.getServiceErrorVO().getErrorDisplayMessage())
						&& (setAnnouncementCardResVO.getServiceErrorVO()
								.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR))// Technical
				// Error
				{

					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"Fatal error from handleAnnouncementCardCount of GiftRegistryFormHandler | webservice error code="
									+ setAnnouncementCardResVO.getServiceErrorVO().getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1089));
					this.addFormException(new DropletException(
							this.getLblTxtTemplateManager().getErrMsg(
									BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
									pRequest.getLocale().getLanguage(), null, null),
							BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
				}
				if (!BBBUtility.isEmpty(setAnnouncementCardResVO.getServiceErrorVO().getErrorDisplayMessage())
						&& (setAnnouncementCardResVO.getServiceErrorVO()
								.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN))// Technical
				// Error
				{

					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"Either user token or site flag invalid from handleAnnouncementCardCount of GiftRegistryFormHandler | webservice error code="
									+ setAnnouncementCardResVO.getServiceErrorVO().getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1090));
					this.addFormException(new DropletException(
							this.getLblTxtTemplateManager().getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
									pRequest.getLocale().getLanguage(), null, null),
							BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));
				}
				if (!BBBUtility.isEmpty(setAnnouncementCardResVO.getServiceErrorVO().getErrorDisplayMessage())
						&& (setAnnouncementCardResVO.getServiceErrorVO()
								.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT))// Technical
				// Error
				{

					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"GiftRegistry input fields format error from handleAnnouncementCardCount() of "
									+ WEB_SERVICE_ERROR_CODE + setAnnouncementCardResVO.getServiceErrorVO().getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

					this.addFormException(new DropletException(
							this.getLblTxtTemplateManager().getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
									pRequest.getLocale().getLanguage(), null, null),
							BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
				}

				this.addFormException(
						new DropletFormException(setAnnouncementCardResVO.getServiceErrorVO().getErrorMessage(),
								this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
										+ setAnnouncementCardResVO.getServiceErrorVO().getErrorId()));
			}

			this.logDebug(
					"GiftRegistryFormHandler.handleAnnouncementCardCount() ::" + " SetAnnounceCard Call Error Exists ="
							+ setAnnouncementCardResVO.getServiceErrorVO().isErrorExists());

			return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);

		} catch (final BBBBusinessException e) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBBusinessException from handleAnnoucementCardCount of GiftRegistryformHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION));

		}

		this.logDebug("GiftRegistryFormHandler.handleAnnouncementCardCount() method ends");

		return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
	}

	private boolean validateUpdateRegistryItem() {
		boolean isError = false;
		if (StringUtils.isEmpty(this.getSkuId())) {
			this.logError("Null or empty sku id ");
			this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_SKU_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_SKU_ID));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getUpdateRegistryId())
				|| (!BBBUtility.isValidateRegistryId(this.getUpdateRegistryId()))) {
			this.logError("Null or empty registry id ");
			this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getProductId())) {
			this.logError("Null or empty product id ");
			this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_PRODUCT_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_PRODUCT_ID));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRowId())) {
			this.logError("Null or empty row id ");
			this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_ROW_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_ROW_ID));
			isError = true;
		}
		return isError;
	}

	/**
	 * @ This method is used from mobileApp to remove multiple items from a
	 * registry
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */

	public boolean handleRemoveMultipleItemsForRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException, BBBBusinessException {
		logError("Starting handleRemoveItemsForMultipleRegistry .... ");
		if (BBBUtility.isEmpty(this.getRegistryId())) {
			this.logError("Null or empty registry id ");
			this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID));
			return false;
		} else {
			this.getValue().put(BBBCoreConstants.REGISTRY_ID, this.getRegistryId());
		}
		if (BBBUtility.isEmpty(getInputString())) {

			logError(INPUT_STRING_IS_NULL);
			this.addFormException(new DropletException(this.getLblTxtTemplateManager().getErrMsg(INPUT_STRING_IS_NULL,
					pRequest.getLocale().getLanguage(), null, null), INPUT_STRING_IS_NULL));
			return false;
		}
		StringTokenizer pipeSeparatedValues = new StringTokenizer(getInputString(), "|");
		StringBuilder rowId = new StringBuilder("");
		StringBuilder skuId = new StringBuilder("");
		StringBuilder quantity = new StringBuilder("");

		while (pipeSeparatedValues.hasMoreTokens()) {
			String stringValue = pipeSeparatedValues.nextToken().trim();

			String[] splitStringValue = stringValue.split(",");

			if (splitStringValue.length >= 1 && BBBUtility.isNotEmpty(splitStringValue[0])) {
				rowId.append(splitStringValue[0].trim() + ",");
			} else {
				this.logError("Null or empty row id ");
				this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_ROW_ID_MSG,
						BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_ROW_ID));
				return false;
			}
			if (splitStringValue.length >= 2 && BBBUtility.isNotEmpty(splitStringValue[1])) {
				skuId.append(splitStringValue[1].trim() + ",");
			} else {
				this.logError("Null or empty sku id ");
				this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_SKU_ID_MSG,
						BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_SKU_ID));
				return false;
			}
			if (splitStringValue.length >= 3 && BBBUtility.isNotEmpty(splitStringValue[2])) {
				try {
					int i = Integer.parseInt(splitStringValue[2]);

					if (i >= 0) {
						quantity.append(splitStringValue[2].trim() + ",");
					} else {
						this.logError("invalid quantity ");
						this.addFormException(new DropletException(
								this.getLblTxtTemplateManager().getErrMsg(
										BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_QUANT_EXCEPTION,
										pRequest.getLocale().getLanguage(), null, null),
								BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_QUANT_EXCEPTION));
						return false;
					}
				} catch (NumberFormatException e) {
					this.logError("invalid quantity ");
					this.addFormException(new DropletException(
							this.getLblTxtTemplateManager().getErrMsg(
									BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_QUANT_EXCEPTION,
									pRequest.getLocale().getLanguage(), null, null),
							BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_QUANT_EXCEPTION));
					return false;
				}
			}
			// append zero as quantity if not provided by user because we are
			// removing the item from registry
			else {
				quantity.append(BBBCoreConstants.STRING_ZERO + ",");
			}
		}

		this.setViewBeans(new ArrayList<GiftRegistryViewBean>());
		while (rowId.length() != 0) {
			String rowIdBean = (String) rowId.substring(0, rowId.indexOf(","));
			rowId = rowId.replace(0, rowId.indexOf(",") + 1, "");
			String skuIdBean = (String) skuId.substring(0, skuId.indexOf(","));
			skuId = skuId.replace(0, skuId.indexOf(",") + 1, "");
			String quantityBean = (String) quantity.substring(0, quantity.indexOf(","));
			quantity = quantity.replace(0, quantity.indexOf(",") + 1, "");
			GiftRegistryViewBean viewBean = new GiftRegistryViewBean();

			viewBean.setQuantity(quantityBean);
			viewBean.setSku(skuIdBean);
			viewBean.setRowId(rowIdBean);
			viewBean.setRegistryId(getRegistryId());

			this.getViewBeans().add(viewBean);
		}
		this.setRemoveSingleItemFlag(false);
		return this.handleUpdateItemToGiftRegistry(pRequest, pResponse);
	}

	/**
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleUpdateRegistryItems(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (!this.validateUpdateRegistryItem()) {

			this.getValue().put(BBBCoreConstants.REGISTRY_ID, this.getUpdateRegistryId());
			this.getValue().put(BBBCoreConstants.ITEM_TYPES, this.getItemTypes());
			this.getValue().put(BBBCoreConstants.SKU_PARAM_NAME, this.getSkuId());
			final GiftRegistryViewBean viewBean = new GiftRegistryViewBean();
			viewBean.setRegistryId(this.getUpdateRegistryId());
			viewBean.setQuantity(this.getUpdateQuantity());
			viewBean.setProductId(this.getProductId());
			viewBean.setSku(this.getSkuId());
			viewBean.setRegItemOldQty(this.getRegItemOldQty());
			viewBean.setPurchasedQuantity(this.getPurchasedQuantity());
			viewBean.setLtlDeliveryServices(this.getLtlDeliveryServices());
			viewBean.setRowId(this.getRowId());
			viewBean.setItemTypes(this.getItemTypes());
		
			this.setViewBeans(new ArrayList<GiftRegistryViewBean>());
			this.getViewBeans().add(viewBean);
			return this.handleUpdateItemToGiftRegistry(pRequest, pResponse);
		}
		return false;
	}

	/**
	 * This method is used to update item quantity in a user's Gift Registry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true / false
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("rawtypes")
	public boolean handleUpdateItemToGiftRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		boolean success = true;
		final String dataAccordionId = pRequest.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID);
		final String dataScrollTop = pRequest.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP);
		this.logDebug("GiftRegistryFormHandler.handleUpdateItemToGiftRegistry() method start");
		// BBBH-391 | Client DOM XSRF changes
		this.setRedirectUrls(pRequest);
		
		try {
			final String pRegistryId = (String) this.getValue().get(BBBCoreConstants.REGISTRY_ID);

			// Validate registry id
			if (!BBBUtility.isValidateRegistryId(pRegistryId)) {
				this.addFormException(new DropletException(this.getLblTxtTemplateManager().getErrMsg(
						BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_QUANTS_SKUS_EXCEPTION,
						pRequest.getLocale().getLanguage(), null, null)));

				this.logError(
						LogMessageFormatter.formatMessage(pRequest, "Either Items rowIDs, quanties or skus are blank",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_20132));

				success = false;
			}
			this.isRegistryOwnedByProfile(pRegistryId);

			if (this.isRemoveSingleItemFlag()) {
				this.preUpdateItemToGiftRegistry(pRequest, pResponse);
			}
			if (this.getFormError()) {
				success = false;
			}

			final Profile profile = (Profile) pRequest
					.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE));

			// if user is transient then redirect to loggedInFailureURL
			if (success && profile.isTransient()) {
				success = false;
				if (!BBBCoreConstants.REST_REDIRECT_URL.equalsIgnoreCase(this.getSuccessURL())) {
					this.setSuccessURL(this.getLoggedInFailureURL());
				}

			}

			// set modified items beans
			this.findModifiedItemsBeans();
			List<GiftRegistryViewBean> invalidateBeans = new ArrayList<GiftRegistryViewBean>();
			invalidateBeans = this.getModifiedViewBeans();
			// create batches
			String[] rowIDsGroups = null;
			String[] skuIDsGroups = null;
			String[] qtysGroups = null;
			rowIDsGroups = this.getRowIdsGroups();
			skuIDsGroups = this.getSKUsGroups();
			qtysGroups = this.getQuantityGroups();

			// check if there is atleast a single modified item
			if ((rowIDsGroups == null) || (qtysGroups == null)) {

				this.addFormException(new DropletException(this.getLblTxtTemplateManager().getErrMsg(
						BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_QUANTS_SKUS_EXCEPTION,
						pRequest.getLocale().getLanguage(), null, null)));

				this.logError(
						LogMessageFormatter.formatMessage(pRequest, "Either Items rowIDs, quanties or skus are blank",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_20132));

				success = false;
			}

			boolean allCallSucess = true;

			if (success) {

				String pSku = (String) getValue().get(BBBCoreConstants.SKU_PARAM_NAME);
				String siteId = getSiteContext().getSite().getId();
				boolean isSkuLtl = getBbbCatalogTools().isSkuLtl(siteId, pSku);

				final int totalCalls = rowIDsGroups.length;

				for (int callIndex = 0; callIndex < this.mModifiedItemsCount; callIndex++) {
					final GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
					if (!BBBCoreConstants.REST_REDIRECT_URL.equalsIgnoreCase(this.getSuccessURL())) {
						this.setSuccessURL(this.getSuccessURL() + BBBCoreConstants.GIFT_REG_UPDATE_STATUS);
					}

					giftRegistryViewBean.setQuantity(this.getModifiedViewBeans().get(callIndex).getQuantity());
					giftRegistryViewBean.setSku(this.getModifiedViewBeans().get(callIndex).getSku());
					giftRegistryViewBean.setRegistryId(pRegistryId);
					giftRegistryViewBean.setRowId(this.getModifiedViewBeans().get(callIndex).getRowId());

					// to test the story BPSI-2136, hardcoding the values that
					// need to send to updateitemToRegistry2 webservice.
					MutableRepository catalogRepository = (MutableRepository) Nucleus.getGlobalNucleus()
							.resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH);
					RepositoryItem skuRepositoryItem = null;
					try {
						skuRepositoryItem = catalogRepository.getItem(this.getModifiedViewBeans().get(callIndex).getSku(),
								BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
					} catch (RepositoryException e1) {
						logError(BBBCoreConstants.ERR_WHILE_FETCHING_SKU_FROM_REPOSITORY, e1);
					}
					giftRegistryViewBean.setItemTypes((String) getValue().get(BBBCoreConstants.ITEM_TYPES));
					if (null != skuRepositoryItem && skuRepositoryItem.getPropertyValue(CUSTOMIZATION_OFFERED_FLAG).equals(true)) {
						giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
						giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.BLANK);
						giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
						giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
					} else {
						// code change for BBBP -222
						if (BBBUtility.isNotEmpty(this.getModifiedViewBeans().get(callIndex).getLtlDeliveryPrices())) {
							giftRegistryViewBean.setLtlDeliveryPrices(
									this.getModifiedViewBeans().get(callIndex).getLtlDeliveryPrices());
						} else {
							giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
						}
						if (BBBUtility.isNotEmpty(this.getModifiedViewBeans().get(callIndex).getLtlDeliveryServices())) {
							giftRegistryViewBean.setLtlDeliveryServices(
									this.getModifiedViewBeans().get(callIndex).getLtlDeliveryServices());
						} else {
							giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.BLANK);
						}
						if (BBBUtility.isNotEmpty(this.getModifiedViewBeans().get(callIndex).getLtlDeliveryServices())
								&& this.getModifiedViewBeans().get(callIndex).getLtlDeliveryServices()
										.equalsIgnoreCase(BBBCoreConstants.LWA)) {
							giftRegistryViewBean
									.setAssemblyPrices(BBBCoreConstants.BLANK + getBbbCatalogTools().getAssemblyCharge(
											SiteContextManager.getCurrentSiteId(), giftRegistryViewBean.getSku()));
							giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.YES_CHAR);
							giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.LW);
						} else {
							giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
							giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
						}

						if (isSkuLtl && giftRegistryViewBean.getLtlDeliveryServices().equalsIgnoreCase(BBBCoreConstants.LWA)) {
								giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.LW);
								giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.YES_CHAR);
						}
						if (this.isUpdateDslFromModal()) {
							this.setItemInfoMapGiftReg(pSku, siteId, giftRegistryViewBean);
						}

					}
					if (this.getItemTypes() != null && !StringUtils.isEmpty(this.getItemTypes())) {
						giftRegistryViewBean.setItemTypes(this.getItemTypes());
					} else {
						if (getRefNum() != null && !getRefNum().isEmpty()) {
							giftRegistryViewBean.setItemTypes(BBBCoreConstants.PERSONALIZATION_ITEM_TYPE);
						} else if (skuRepositoryItem.getPropertyValue(CUSTOMIZATION_OFFERED_FLAG).equals(false)
								&& null != skuRepositoryItem.getPropertyValue(BBBCoreConstants.LTL_FLAG)
								&& skuRepositoryItem.getPropertyValue(BBBCoreConstants.LTL_FLAG).equals(true)) {
							giftRegistryViewBean.setItemTypes(BBBCoreConstants.LTL_ITEM_TYPE);
						} else {
							giftRegistryViewBean.setItemTypes("");
						}
					}

					giftRegistryViewBean.setSiteFlag(this.getBbbCatalogTools()
							.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
					giftRegistryViewBean.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(
							BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
					giftRegistryViewBean.setServiceName(this.getUpdateRegItemsServiceName());

					this.logDebug("siteFlag: " + giftRegistryViewBean.getSiteFlag());
					this.logDebug("userToken: " + giftRegistryViewBean.getUserToken());
					this.logDebug("ServiceName: " + this.getUpdateRegItemsServiceName());

					// update gift registry item
					final ManageRegItemsResVO mageItemsResVO = this.getGiftRegistryManager()
							.removeUpdateGiftRegistryItem(profile, giftRegistryViewBean);

					if (mageItemsResVO.getServiceErrorVO().isErrorExists()) {

						allCallSucess = false;
						this.manageItemErrorForGiftReg(pRequest, mageItemsResVO);
					}

					if (allCallSucess) {
						this.updateAlternateNumInGiftReg(pRequest, pResponse, giftRegistryViewBean);
					}
				}

				if (allCallSucess && this.isRemoveSingleItemFlag()) {
					this.logDebug("Invalidating registry cache 2");
					getGiftRegistryManager().getGiftRegistryTools().invalidateRegistryCache(invalidateBeans);
					final int oldRequestedQuantity = this.findTotalOldQuantity();

					// Update session data so that it will be in sync with the
					// registred items on registry flyout.
					final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
							.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
					final Map sessionMap = sessionBean.getValues();
					final RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap
							.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
					if ((registrySummaryVO != null)
							&& registrySummaryVO.getRegistryId().equalsIgnoreCase(pRegistryId)) {
						int totalQuantity = 0;
						totalQuantity = this.findTotalNewQuantity() - oldRequestedQuantity;
						// update quantiry in session
						totalQuantity = registrySummaryVO.getGiftRegistered() + totalQuantity;
						this.setTotalGiftRegistered(totalQuantity);
                        registrySummaryVO.setGiftRegistered(totalQuantity);
					}
				}
			}

		} catch (final BBBBusinessException e) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Update registry item BusinessException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1007), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_BIZ_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_BIZ_EXCEPTION));
		} catch (final BBBSystemException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Update registry item SystemException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1006), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION));
		}

		this.logDebug("GiftRegistryFormHandler.handleUpdateItemToGiftRegistry() method ends");
		if (BBBUtility.isNotEmpty(this.getSuccessURL())
				&& !BBBCoreConstants.REST_REDIRECT_URL.equalsIgnoreCase(this.getSuccessURL())) {
			this.setSuccessURL(getSuccessURL() + BBBCoreConstants.AMPERSAND + BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP
					+ BBBCoreConstants.EQUAL + dataScrollTop + BBBCoreConstants.AMPERSAND
					+ BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID + BBBCoreConstants.EQUAL + dataAccordionId);
		}
		return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
	}
	/**
	 * @param pRequest
	 * @param pResponse
	 * @param giftRegistryViewBean
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws ServletException
	 * @throws IOException
	 */
	private void updateAlternateNumInGiftReg(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse,
			final GiftRegistryViewBean giftRegistryViewBean)
			throws BBBSystemException, BBBBusinessException, ServletException,
			IOException {
		
		getGiftRegistryManager().getGiftRegistryTools().invalidateRegistry(giftRegistryViewBean);
		List<GiftRegistryViewBean> bean = new ArrayList<GiftRegistryViewBean>();
		bean.add(giftRegistryViewBean);
		this.logDebug("Invalidating registry cache 1");
		getGiftRegistryManager().getGiftRegistryTools().invalidateRegistryCache(bean);
		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
				.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		RegistryResVO registryResVO = getGiftRegistryManager().getRegistryInfoFromEcomAdmin(
				giftRegistryViewBean.getRegistryId(), this.getSiteContext().getSite().getId());
		sessionBean.getValues().put(giftRegistryViewBean.getRegistryId() + REG_SUMMARY_KEY_CONST,
				registryResVO);

		// Code for updating alternate number in registry
		if (registryResVO != null && BBBUtility.isNotEmpty(this.getAlternateNum())) {
			registryResVO.getRegistryVO().getPrimaryRegistrant().setCellPhone(this.getAlternateNum());
			registryResVO.getRegistryVO().getEvent().setBabyGender(BBBCoreConstants.BLANK);
			registryResVO.getRegistryVO().getEvent().setEventDate(BBBUtility
					.convertWSDateToUSFormat(registryResVO.getRegistryVO().getEvent().getEventDate()));
			if (BBBUtility.isNotEmpty(registryResVO.getRegistryVO().getEvent().getShowerDate())) {
				registryResVO.getRegistryVO().getEvent()
						.setShowerDate(BBBUtility.convertWSDateToUSFormat(
								registryResVO.getRegistryVO().getEvent().getShowerDate()));
			}
			if (BBBUtility.isNotEmpty(registryResVO.getRegistryVO().getEvent().getBirthDate())) {
				registryResVO.getRegistryVO().getEvent()
						.setBirthDate(BBBUtility.convertWSDateToUSFormat(
								registryResVO.getRegistryVO().getEvent().getBirthDate()));
			}
			if ((this.getRegistryVO().getShipping() != null) && !StringUtils
					.isEmpty(this.getRegistryVO().getShipping().getFutureShippingDate())) {
				registryResVO.getRegistryVO().getShipping()
						.setFutureShippingDate(BBBUtility.convertWSDateToUSFormat(
								registryResVO.getRegistryVO().getShipping().getFutureShippingDate()));
			}
			this.setRegistryVO(registryResVO.getRegistryVO());
			this.getSessionBean().setRegistryTypesEvent(
					registryResVO.getRegistryVO().getRegistryType().getRegistryTypeName());
			pRequest.setParameter(BBBCoreConstants.ALTERNATE_NUM, BBBCoreConstants.TRUE);
			this.handleUpdateRegistry(pRequest, pResponse);
		}

	}
	/**
	 * @param pSku
	 * @param siteId
	 * @param giftRegistryViewBean
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws TagConversionException
	 */
	private void setItemInfoMapGiftReg(String pSku, String siteId,
			final GiftRegistryViewBean giftRegistryViewBean)
			throws BBBBusinessException, BBBSystemException,
			TagConversionException {
		Map<String, String> updatedItemInfoMap = new HashMap<String, String>();
		String ltlDeliveryServicesDesc = "";
		double itemPrice = Double.valueOf(this.getItemPrice());
		RepositoryItem shippingMethod = getBbbCatalogTools()
				.getShippingMethod(this.getLtlDeliveryServices());
		ltlDeliveryServicesDesc = ((String) shippingMethod
				.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION));
		String assemblySelected = giftRegistryViewBean.getAssemblySelections();
		double deliveryCharge = getBbbCatalogTools().getDeliveryCharge(siteId, pSku,
				this.getLtlDeliveryServices());
		if (BBBUtility.isNotEmpty(assemblySelected)
				&& assemblySelected.equals(BBBCoreConstants.YES_CHAR)) {
			deliveryCharge = deliveryCharge + getBbbCatalogTools().getAssemblyCharge(siteId, pSku);
			ltlDeliveryServicesDesc = ltlDeliveryServicesDesc
					+ BBBGiftRegistryConstants.WITH_ASSEMBLY;
		}
		updatedItemInfoMap.put(LTL_DELIVERY_SERVICES_DESC, ltlDeliveryServicesDesc);
		updatedItemInfoMap.put(BBBCoreConstants.ASSEMBLY_SELECTED, assemblySelected);
		if (Double.compare(deliveryCharge, 0.0) == BBBCoreConstants.ZERO) {
			updatedItemInfoMap.put(LTL_DELIVERY_PRICES, "FREE");
		} else {
			updatedItemInfoMap.put(LTL_DELIVERY_PRICES,
					TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY)
							.convertObjectToString(ServletUtil.getCurrentRequest(), deliveryCharge,
									new Properties())
							.toString());
		}
		updatedItemInfoMap.put(TOTAL_PRICE,
				TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY)
						.convertObjectToString(ServletUtil.getCurrentRequest(),
								deliveryCharge + itemPrice, new Properties())
						.toString());
		updatedItemInfoMap.put(ITEM_PRICE, TagConverterManager
				.getTagConverterByName(BBBCoreConstants.CURRENCY)
				.convertObjectToString(ServletUtil.getCurrentRequest(), itemPrice, new Properties())
				.toString());
		this.setUpdatedItemInfoMap(updatedItemInfoMap);
	}

	/**
	 * @param pRequest
	 * @param mageItemsResVO
	 */
	private void manageItemErrorForGiftReg(
			final DynamoHttpServletRequest pRequest,
			final ManageRegItemsResVO mageItemsResVO) {
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO().getErrorDisplayMessage())
				&& (mageItemsResVO.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR))// Technical
		// Error
		{
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Fatal error format error from handleUpdateItemToGiftRegistry() of GiftRegistryFormHandler | webservice error code="
							+ mageItemsResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1091));

			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
						
		}
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO().getErrorDisplayMessage())
				&& (mageItemsResVO.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN))// Technical
		// Error
		{
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Either user token or site flag invalid from handleUpdateItemToGiftRegistry() of GiftRegistryFormHandler | webservice error code="
							+ mageItemsResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1092));
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));
		}
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO().getErrorDisplayMessage())
				&& (mageItemsResVO.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT))// Technical
		// Error
		{

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry input fields format error from handleUpdateItemToGiftRegistry() of "
							+ WEB_SERVICE_ERROR_CODE + mageItemsResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
		}
		
		this.addFormException(
				new DropletFormException(mageItemsResVO.getServiceErrorVO().getErrorMessage(),
						this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
								+ mageItemsResVO.getServiceErrorVO().getErrorId()));
		this.logError(LogMessageFormatter.formatMessage(pRequest,
				"Update registry item SystemException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler Error Id is "
						+ mageItemsResVO.getServiceErrorVO().getErrorId(),
				BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1006));
		this.addFormException(new DropletException(
				this.getLblTxtTemplateManager().getErrMsg(
						BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION,
						pRequest.getLocale().getLanguage(), null, null),
				BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION));
	}

	/**
	 * This method is used to update a single registry items quantity in a
	 * user's Gift Registry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true / false
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	
	public boolean handleUpdateRegistryItem(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		boolean success = true;
		String dataAccordionId = pRequest.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID);
		String dataScrollTop = pRequest.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP);
		logDebug("GiftRegistryFormHandler.handleUpdateItemToGiftRegistry() method start");
		// BBBH-391 | Client DOM XSRF changes
		String siteId = getSiteContext().getSite().getId();
		this.setRedirectUrls(pRequest);
		try {
			String pRegistryId = (String) getValue().get(BBBCoreConstants.REGISTRY_ID);
			isRegistryOwnedByProfile(pRegistryId);
			preUpdateItemToGiftRegistry(pRequest, pResponse);

			if (this.getFormError()) {
				success = false;
			}

			Profile profile = (Profile) pRequest
					.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE));

			// if user is transient then redirect to loggedInFailureURL
			if (success && profile.isTransient()) {
				success = false;
				if (!BBBCoreConstants.REST_REDIRECT_URL.equalsIgnoreCase(this.getSuccessURL())) {
					this.setSuccessURL(getLoggedInFailureURL());
				}

			}

			boolean allCallSucess = true;

			if (success) {

				GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
				this.setViewBeans(new ArrayList<GiftRegistryViewBean>());
				if (getSuccessURL() == null) {
					setSuccessURL(getSuccessURL() + BBBCoreConstants.GIFT_REG_REMOVE_STATUS);
				}
				String pSku = (String) getValue().get(BBBCoreConstants.SKU_PARAM_NAME);
				
				boolean isSkuLtl = getBbbCatalogTools().isSkuLtl(siteId, pSku);
				this.setRegItemOldQty((String) getValue().get(BBBCoreConstants.REGISTRY_ITEM_OLD_QTY));

				String regToken = (String) getValue().get(BBBCoreConstants.USER_TOKEN);
				String itemRowId = (String) getValue().get(BBBCoreConstants.ROWID);
				String refNum = (String) getValue().get("refNum");
				if (BBBUtility.isEmpty(refNum)) {
					refNum = getDefaultRefNum();
				}
				giftRegistryViewBean.setRefNum(refNum);
				// BBBI - Interactive Checklist .On add/ update/remove item update the ribbon to the registry if it is guide
               getSessionBean().setActivateGuideInRegistryRibbon(false);
				// to test the story BPSI-2136, hardcoding the values that need
				// to send to updateitemToRegistry2 webservice.
				MutableRepository catalogRepository = (MutableRepository) Nucleus.getGlobalNucleus()
						.resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH);
				RepositoryItem skuRepositoryItem = null;
				try {
					skuRepositoryItem = catalogRepository.getItem(pSku, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
				} catch (RepositoryException e1) {
					logError(BBBCoreConstants.ERR_WHILE_FETCHING_SKU_FROM_REPOSITORY, e1);
				}
				giftRegistryViewBean.setItemTypes((String) getValue().get(BBBCoreConstants.ITEM_TYPES));
				if (null != skuRepositoryItem && skuRepositoryItem.getPropertyValue(CUSTOMIZATION_OFFERED_FLAG).equals(true)) {
					giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
				} else {
					giftRegistryViewBean.setLtlDeliveryServices(this.getLtlDeliveryServices() == null
							? BBBCoreConstants.BLANK : this.getLtlDeliveryServices());
					giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
					
					if (isSkuLtl && giftRegistryViewBean.getLtlDeliveryServices().equalsIgnoreCase(BBBCoreConstants.LWA)) {
							giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.LW);
							giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.YES_CHAR);
					}
					if (this.isUpdateDslFromModal()) {
						this.setItemInfoMapReg(siteId, giftRegistryViewBean, pSku);
					}
				}

				giftRegistryViewBean.setRegToken(regToken);
				giftRegistryViewBean.setRegistryId(pRegistryId);
				giftRegistryViewBean.setRowId(itemRowId);
				// Set quantity =0 for removing an item
				giftRegistryViewBean
						.setRegItemOldQty(StringUtils.isEmpty(this.getRegItemOldQty()) ? "0" : this.getRegItemOldQty());
				giftRegistryViewBean.setQuantity(getModifiedItemQuantity());
				giftRegistryViewBean.setSku(pSku);
				giftRegistryViewBean.setSiteFlag(getBbbCatalogTools()
						.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
				giftRegistryViewBean
						.setUserToken(getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
				giftRegistryViewBean.setServiceName(getUpdateRegItemsServiceName());

				logDebug("siteFlag: " + giftRegistryViewBean.getSiteFlag());
				logDebug("userToken: " + giftRegistryViewBean.getUserToken());
				logDebug("ServiceName: " + getUpdateRegItemsServiceName());
				getViewBeans().add(giftRegistryViewBean);

				// update gift registry item
				ManageRegItemsResVO mageItemsResVO = this.getGiftRegistryManager().removeUpdateGiftRegistryItem(profile,
						giftRegistryViewBean);

				if (mageItemsResVO.getServiceErrorVO().isErrorExists()) {

					allCallSucess = false;
					this.manageItemErrorForReg(pRequest, mageItemsResVO);
				}

				if (allCallSucess) {
					this.updateSessionObjInReg(pRequest, pResponse, siteId,	pRegistryId, giftRegistryViewBean);
				}
			}

		} catch (BBBBusinessException e) {

			logError(LogMessageFormatter.formatMessage(pRequest,
					"Update registry item BusinessException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1007), e);
			addFormException(new DropletException(
					getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_BIZ_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_BIZ_EXCEPTION));
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(pRequest,
					"Update registry item SystemException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1006), e);
			addFormException(new DropletException(
					getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION));
		}

		logDebug("GiftRegistryFormHandler.handleUpdateItemToGiftRegistry() method ends");
		if (BBBUtility.isNotEmpty(this.getSuccessURL())
				&& !BBBCoreConstants.REST_REDIRECT_URL.equalsIgnoreCase(this.getSuccessURL())) {
			this.setSuccessURL(getSuccessURL() + BBBCoreConstants.AMPERSAND + BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP
					+ BBBCoreConstants.EQUAL + dataScrollTop + BBBCoreConstants.AMPERSAND
					+ BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID + BBBCoreConstants.EQUAL + dataAccordionId);
		}
		return checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
	}
	/**
	 * @param siteId
	 * @param giftRegistryViewBean
	 * @param pSku
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws TagConversionException
	 */
	private void setItemInfoMapReg(String siteId,
			GiftRegistryViewBean giftRegistryViewBean, String pSku)
			throws BBBBusinessException, BBBSystemException,
			TagConversionException {
		Map<String, String> updatedItemInfoMap = new HashMap<String, String>();
		String ltlDeliveryServicesDesc = "";
		double itemPrice = 0.0;
		if (this.getItemPrice() != null) {
			itemPrice = Double.valueOf(this.getItemPrice());
		}
		RepositoryItem shippingMethod = getBbbCatalogTools()
				.getShippingMethod(this.getLtlDeliveryServices());
		ltlDeliveryServicesDesc = ((String) shippingMethod
				.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION));
		String assemblySelected = giftRegistryViewBean.getAssemblySelections();
		double deliveryCharge = getBbbCatalogTools().getDeliveryCharge(siteId, pSku,
				this.getLtlDeliveryServices());
		if (BBBUtility.isNotEmpty(assemblySelected)
				&& assemblySelected.equals(BBBCoreConstants.YES_CHAR)) {
			deliveryCharge = deliveryCharge + getBbbCatalogTools().getAssemblyCharge(siteId, pSku);
		}
		updatedItemInfoMap.put(LTL_DELIVERY_SERVICES_DESC,
				INCL + BBBCoreConstants.SPACE + ltlDeliveryServicesDesc);
		updatedItemInfoMap.put(BBBCoreConstants.ASSEMBLY_SELECTED, assemblySelected);
		if (Double.compare(deliveryCharge, 0.0) == BBBCoreConstants.ZERO) {
			updatedItemInfoMap.put(LTL_DELIVERY_PRICES, "FREE");
		} else {
			updatedItemInfoMap.put(LTL_DELIVERY_PRICES,
					TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY)
							.convertObjectToString(ServletUtil.getCurrentRequest(), deliveryCharge,
									new Properties())
							.toString());
		}
		updatedItemInfoMap.put(TOTAL_PRICE,
				TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY)
						.convertObjectToString(ServletUtil.getCurrentRequest(),
								deliveryCharge + itemPrice, new Properties())
						.toString());
		updatedItemInfoMap.put(ITEM_PRICE, TagConverterManager
				.getTagConverterByName(BBBCoreConstants.CURRENCY)
				.convertObjectToString(ServletUtil.getCurrentRequest(), itemPrice, new Properties())
				.toString());
		this.setUpdatedItemInfoMap(updatedItemInfoMap);
	}
	/**
	 * @param pRequest
	 */
	private void setRedirectUrls(final DynamoHttpServletRequest pRequest) {
		if (StringUtils.isNotEmpty(getFromPage())) {
			StringBuilder appendData = new StringBuilder(BBBCoreConstants.BLANK);
			if(StringUtils.isNotEmpty(getQueryParam())){
				appendData.append(BBBCoreConstants.QUESTION_MARK).append(
						getQueryParam());
			}
			StringBuilder successURL = new StringBuilder(BBBCoreConstants.BLANK);
			StringBuilder errorURL = new StringBuilder(BBBCoreConstants.BLANK);
			successURL
					.append(pRequest.getContextPath())
					.append(getSuccessUrlMap().get(getFromPage()))
					.append(appendData);
			errorURL.append(pRequest.getContextPath())
					.append(getErrorUrlMap().get(getFromPage()))
					.append(appendData);

			setSuccessURL(successURL.toString());
			setErrorURL(errorURL.toString());
		}
	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @param siteId
	 * @param pRegistryId
	 * @param giftRegistryViewBean
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws ServletException
	 * @throws IOException
	 */@SuppressWarnings("rawtypes")
	private void updateSessionObjInReg(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, String siteId,
			String pRegistryId, GiftRegistryViewBean giftRegistryViewBean)
			throws BBBSystemException, BBBBusinessException, ServletException,
			IOException {
	
		getGiftRegistryManager().getGiftRegistryTools().invalidateRegistry(giftRegistryViewBean);
		List<GiftRegistryViewBean> bean = new ArrayList<GiftRegistryViewBean>();
		bean.add(giftRegistryViewBean);
		this.logDebug("Invalidating registry cache 3");
		getGiftRegistryManager().getGiftRegistryTools().invalidateRegistryCache(bean);
		int oldRequestedQuantity = findTotalOldQuantity();

		// Update session data so that it will be in sync with the
		// registred items on registry flyout.
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest
				.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		final Map sessionMap = sessionBean.getValues();
		RegistryResVO registryResVO = getGiftRegistryManager()
				.getRegistryInfoFromEcomAdmin(giftRegistryViewBean.getRegistryId(), siteId);
		sessionBean.getValues().put(giftRegistryViewBean.getRegistryId() + REG_SUMMARY_KEY_CONST,
				registryResVO);
		// Code for updating alternate number in registry
		if (registryResVO != null && BBBUtility.isNotEmpty(this.getAlternateNum())) {
			registryResVO.getRegistryVO().getPrimaryRegistrant().setCellPhone(this.getAlternateNum());
			registryResVO.getRegistryVO().getEvent().setBabyGender(BBBCoreConstants.BLANK);
			registryResVO.getRegistryVO().getEvent().setEventDate(BBBUtility
					.convertWSDateToUSFormat(registryResVO.getRegistryVO().getEvent().getEventDate()));
			this.setRegistryVO(registryResVO.getRegistryVO());
			this.getSessionBean().setRegistryTypesEvent(
					registryResVO.getRegistryVO().getRegistryType().getRegistryTypeName());
			pRequest.setParameter(BBBCoreConstants.ALTERNATE_NUM, BBBCoreConstants.TRUE);
			this.handleUpdateRegistry(pRequest, pResponse);
		}
		RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap
				.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
		if (registrySummaryVO != null && registrySummaryVO.getRegistryId().equalsIgnoreCase(pRegistryId)) {
			int totalQuantity = 0;
			totalQuantity = findTotalNewQuantity() - oldRequestedQuantity;
			// update quantiry in session
			totalQuantity = registrySummaryVO.getGiftRegistered() + totalQuantity;
			registrySummaryVO.setGiftRegistered(totalQuantity);

		}
	}

	/**
	 * @param pRequest
	 * @param mageItemsResVO
	 */
	private void manageItemErrorForReg(final DynamoHttpServletRequest pRequest,
			ManageRegItemsResVO mageItemsResVO) {
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO().getErrorDisplayMessage())
				&& mageItemsResVO.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR)// Technical
		// Error
		{
			logError(LogMessageFormatter.formatMessage(pRequest,
					"Fatal error format error from handleUpdateItemToGiftRegistry() of GiftRegistryFormHandler | webservice error code="
							+ mageItemsResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1091));

			addFormException(new DropletException(
					getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
		}
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO().getErrorDisplayMessage())
				&& mageItemsResVO.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN)// Technical
		// Error
		{
			logError(LogMessageFormatter.formatMessage(pRequest,
					"Either user token or site flag invalid from handleUpdateItemToGiftRegistry() of GiftRegistryFormHandler | webservice error code="
							+ mageItemsResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1092));
			addFormException(new DropletException(
					getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));
		}
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO().getErrorDisplayMessage())
				&& mageItemsResVO.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT)// Technical
		// Error
		{

			logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry input fields format error from handleUpdateItemToGiftRegistry() of "
							+ WEB_SERVICE_ERROR_CODE + mageItemsResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

			addFormException(new DropletException(
					getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
		}
		
		addFormException(new DropletFormException(mageItemsResVO.getServiceErrorVO().getErrorMessage(),
				getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
						+ mageItemsResVO.getServiceErrorVO().getErrorId()));
		logError(LogMessageFormatter.formatMessage(pRequest,
				"Update registry item SystemException from handleUpdateItemToGiftRegistry of GiftRegistryFormHandler Error Id is "
						+ mageItemsResVO.getServiceErrorVO().getErrorId(),
				BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1006));
		addFormException(new DropletException(
				getLblTxtTemplateManager().getErrMsg(
						BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION,
						pRequest.getLocale().getLanguage(), null, null),
				BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION));
	}

	/**
	 * pre method for handleUpdateItemToGiftRegistry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public void preUpdateItemToGiftRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws BBBBusinessException, BBBSystemException {

		this.logDebug("GiftRegistryFormHandler.preUpdateAllItemToGiftRegistry() method start");

		for (final GiftRegistryViewBean viewBean : this.getViewBeans()) {

			final String giftPurchasedQuantity = viewBean.getPurchasedQuantity();
			final String newRequestedQuantity = viewBean.getQuantity();
			final String oldRequestedQuantity = viewBean.getRegItemOldQty();

			// ignore empty sku
			if (viewBean.getSku() == null) {
				continue;
			}

			// if requested old quantity is same as requested new quantity then
			// skip validation
			if (!oldRequestedQuantity.equalsIgnoreCase(newRequestedQuantity) && !this.validateRequestedQuantity(newRequestedQuantity, giftPurchasedQuantity, pRequest)) {
					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"Quantity is not valid from preUpdateAllItemToGiftRegistry of GiftRegistryFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1064));
					this.addFormException(new DropletException(this.getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_QUANT_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null)));

					return;
			}

		}

		this.logDebug("GiftRegistryFormHandler.preUpdateAllItemToGiftRegistry() method ends");
	}

	/**
	 * Validate requested quantity.
	 *
	 * @param requestedQuantity
	 *            the requested quantity
	 * @param pPurchasedQuantity
	 *            the purchased quantity
	 * @return boolean
	 */
	private boolean validateRequestedQuantity(final String requestedQuantity, final String pPurchasedQuantity,
			final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.checkCoregistrantValidationStat() method start");

		final boolean validationFlag = false;

		int rQuantity = 0;

		if (BBBUtility.isEmpty(requestedQuantity)) {
			return validationFlag;
		}

		if (BBBUtility.isEmpty(pPurchasedQuantity)) {
			return validationFlag;
		}
		try {
			rQuantity = Integer.parseInt(requestedQuantity);

			// requested quantity is not supposed to be greater than 99
			if ((rQuantity <= 0) || (rQuantity > 99)) {
				return validationFlag;
			}

		} catch (final NumberFormatException nfexc) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"NumberFormatException from validateRequestedQuantity() of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1093), nfexc);
			return validationFlag;
		}
		this.logDebug("GiftRegistryFormHandler.checkCoregistrantValidationStat() method end");
		return true;

	}

	/**
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return
	 */
	private static boolean isItemQuantityChanged(final String oldQuantity, final String newQuantity) {

		boolean isModified = true;

			if ((oldQuantity != null) && (newQuantity != null) && oldQuantity.equalsIgnoreCase(newQuantity)) {
					isModified = false;
			} 

		return isModified;

	}

	/**
	 * Create batches of RowIDs based on permissible batch size.
	 *
	 * @return
	 */
	private String[] getRowIdsGroups() {

		String rowIDSGroup[] = null;

		final int batchSize = this.getUpdateBatchSize();
		final int totalCalls = (int) Math.ceil(((double) this.mModifiedItemsCount / batchSize));

		rowIDSGroup = new String[totalCalls];

		int groupIndex = 0;
		String rowIDs = "";

		for (int k = 0; k < totalCalls; k++) {

			boolean isPopulated = false;
			rowIDs = "";
			final int b = k * batchSize;

			int maxIndex = 0;
			if ((this.getModifiedViewBeans().size() - b) >= batchSize) {
				maxIndex = batchSize;
			} else {
				maxIndex = this.getModifiedViewBeans().size() - b;
			}

			for (int m = b; m < (b + maxIndex); m++) {

				if (!StringUtils.isBlank(rowIDs)) {
					rowIDs += ",";
				}
				rowIDs += this.getModifiedViewBeans().get(m).getRowId();
				isPopulated = true;
			}

			if (isPopulated) {
				rowIDSGroup[groupIndex++] = rowIDs;
			}
		}

		return rowIDSGroup;

	}

	/**
	 * Create batches of quantities based on batch size.
	 *
	 * @return
	 */
	private String[] getQuantityGroups() {

		String qtysGroup[] = null;

		final int batchSize = this.getUpdateBatchSize();
		final int totalCalls = (int) Math.ceil(((double) this.mModifiedItemsCount / batchSize));

		qtysGroup = new String[totalCalls];

		int groupIndex = 0;
		String qtys = "";

		for (int k = 0; k < totalCalls; k++) {

			boolean isPopulated = false;
			qtys = "";
			final int b = k * batchSize;

			int maxIndex = 0;
			if ((this.getModifiedViewBeans().size() - b) >= batchSize) {
				maxIndex = batchSize;
			} else {
				maxIndex = this.getModifiedViewBeans().size() - b;
			}

			for (int m = b; m < (b + maxIndex); m++) {

				if (!StringUtils.isBlank(qtys)) {
					qtys += ",";
				}
				qtys += this.getModifiedViewBeans().get(m).getQuantity();
				isPopulated = true;
			}

			if (isPopulated) {
				qtysGroup[groupIndex++] = qtys;
			}
		}

		return qtysGroup;

	}

	/**
	 * Create batches of SKUs based on batchsize.
	 *
	 * @return
	 */
	private String[] getSKUsGroups() {

		String skuIDSGroup[] = null;

		final int batchSize = this.getUpdateBatchSize();
		final int totalCalls = (int) Math.ceil(((double) this.mModifiedItemsCount / batchSize));

		skuIDSGroup = new String[totalCalls];

		int groupIndex = 0;
		String skuIDs = "";

		for (int k = 0; k < totalCalls; k++) {

			boolean isPopulated = false;
			skuIDs = "";
			final int b = k * batchSize;

			int maxIndex = 0;
			if ((this.getModifiedViewBeans().size() - b) >= batchSize) {
				maxIndex = batchSize;
			} else {
				maxIndex = this.getModifiedViewBeans().size() - b;
			}

			for (int m = b; m < (b + maxIndex); m++) {

				if (!StringUtils.isBlank(skuIDs)) {
					skuIDs += ",";
				}
				skuIDs += this.getModifiedViewBeans().get(m).getSku();
				isPopulated = true;
			}

			if (isPopulated) {
				skuIDSGroup[groupIndex++] = skuIDs;
			}
		}

		return skuIDSGroup;

	}

	/**
	 * List of modified skus separated by.
	 *
	 * @return String
	 */
	private void findModifiedItemsBeans() {

		this.setModifiedViewBeans(new ArrayList<GiftRegistryViewBean>());
		for (final GiftRegistryViewBean bean : this.getViewBeans()) {

			// ignore empty bean
			if (bean.getRowId() == null) {
				continue;
			}

			// send webservice calls will be sent for modified items
			if (GiftRegistryFormHandler.isItemQuantityChanged(bean.getRegItemOldQty(), bean.getQuantity())
					|| BBBUtility.isNotEmpty(bean.getLtlDeliveryServices())) {

				// count number of items modified
				this.mModifiedItemsCount++;

				this.getModifiedViewBeans().add(bean);

			}
		}
	}

	/**
	 * Total quantities sumup of all items's old quantities.
	 *
	 * @return int
	 */
	private int findTotalOldQuantity() {

		int oldQty = 0;

		for (final GiftRegistryViewBean bean : this.getViewBeans()) {
			// ignore empty row
			if (bean.getRowId() == null) {
				continue;
			}
			oldQty += Integer.parseInt(bean.getRegItemOldQty());
		}

		return oldQty;
	}

	/**
	 * Total quantities sumup of all items's new quantities.
	 *
	 * @return int
	 */
	private int findTotalNewQuantity() {
		int newQty = 0;

		for (final GiftRegistryViewBean bean : this.getViewBeans()) {
			// ignore empty row
			if (bean.getRowId() == null) {
				continue;
			}
			newQty += Integer.parseInt(bean.getQuantity());
		}

		return newQty;
	}

	public boolean handleRemoveRegistryItems(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (!this.validateUpdateRegistryItem()) {
			this.getValue().put(BBBCoreConstants.SKU_PARAM_NAME, this.getSkuId());
			this.getValue().put(BBBCoreConstants.REGISTRY_ID, this.getUpdateRegistryId());
			this.getValue().put(BBBCoreConstants.PRODUCT_ID, this.getProductId());
			this.getValue().put(BBBCoreConstants.REGISTRY_ITEM_OLD_QTY, this.getRegItemOldQty());
			this.getValue().put(BBBCoreConstants.ROWID, this.getRowId());
			this.getValue().put(BBBCoreConstants.ITEM_TYPES, this.getItemTypes());
			this.getValue().put(BBBCoreConstants.LTL_DELIVERY_SERVICES, this.getLtlDeliveryServices());
			return this.handleRemoveItemFromGiftRegistry(pRequest, pResponse);
		}
		return false;
	}

	/**
	 * This method is used to update item quantity in a user's Gift Registry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true / false
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	
	public boolean handleRemoveItemFromGiftRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		boolean success = true;
		final String dataAccordionId = pRequest.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID);
		final String dataScrollTop = pRequest.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP);
		this.logDebug("GiftRegistryFormHandler.handleRemoveItemFromGiftRegistry() method start");
		// BBBH-391 | Client DOM XSRF changes
		final String siteId = this.getSiteContext().getSite().getId();
		
		this.populateRedirectUrls(pRequest);
		try {

			final Profile profile = (Profile) pRequest
					.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE));
			final String pRegistryId = (String) this.getValue().get(BBBCoreConstants.REGISTRY_ID);

			// Validate registry id
			if (!BBBUtility.isValidateRegistryId(pRegistryId)) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"BBBBusinessException from handleRemoveItemFromGiftRegistry of GiftRegistryFormHandler"));
				this.addFormException(new DropletException(
						this.getLblTxtTemplateManager().getErrMsg(
								BBBGiftRegistryConstants.ERR_REM_REGITEM_BIZ_EXCEPTION,
								pRequest.getLocale().getLanguage(), null, null),
						BBBGiftRegistryConstants.ERR_REM_REGITEM_BIZ_EXCEPTION));
				success = false;
			}

			this.isRegistryOwnedByProfile(pRegistryId);
			pRequest.setParameter(BBBCoreConstants.PROFILE, profile);

			this.preRemoveItemFromGiftRegistry(pRequest, pResponse);

			if (this.getFormError()) {
				success = false;
				if (!BBBCoreConstants.REST_REDIRECT_URL.equalsIgnoreCase(this.getSuccessURL())) {
					this.setSuccessURL(this.getLoggedInFailureURL());
				}

			}

			if (success) {

				final GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
				if (this.getSuccessURL() == null) {
					this.setSuccessURL(this.getSuccessURL() + BBBCoreConstants.GIFT_REG_REMOVE_STATUS);
				}
				final String pSku = (String) this.getValue().get(BBBCoreConstants.SKU_PARAM_NAME);
				final String prodId = (String) this.getValue().get(BBBCoreConstants.PRODUCT_ID_PARAM_NAME);
				// BBBI - Interactive Checklist .On add/ update/remove item update the ribbon to the registry if it is guide
			   getSessionBean().setActivateGuideInRegistryRibbon(false);
				this.setRegItemOldQty((String) getValue().get(BBBCoreConstants.REGISTRY_ITEM_OLD_QTY));
				final String regToken = (String) this.getValue().get(BBBCoreConstants.USER_TOKEN);
				final String itemRowId = (String) this.getValue().get(BBBCoreConstants.ROWID);
				// to test the story BPSI-2136, hardcoding the values that need
				// to send to updateitemToRegistry2 webservice.
				MutableRepository catalogRepository = (MutableRepository) Nucleus.getGlobalNucleus()
						.resolveName(BBBCoreConstants.PRODUCT_CATALOG_PATH);
				RepositoryItem skuRepositoryItem = null;
				try {
					skuRepositoryItem = catalogRepository.getItem(pSku, BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
				} catch (RepositoryException e1) {
					logError(BBBCoreConstants.ERR_WHILE_FETCHING_SKU_FROM_REPOSITORY, e1);
				}

				if (null != skuRepositoryItem && skuRepositoryItem.getPropertyValue(CUSTOMIZATION_OFFERED_FLAG).equals(true)) {
					giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
				} else {
					giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
				}
				giftRegistryViewBean.setItemTypes((String) getValue().get(BBBCoreConstants.ITEM_TYPES));

				giftRegistryViewBean.setRegToken(regToken);
				giftRegistryViewBean.setRegistryId(pRegistryId);
				giftRegistryViewBean.setRowId(itemRowId);
				giftRegistryViewBean.setRefNum(this.getRefNum());
				// Set quantity =0 for removing an item
				giftRegistryViewBean.setQuantity(String.valueOf(BBBCoreConstants.ZERO));
				giftRegistryViewBean.setSku(pSku);
				
				giftRegistryViewBean.setSiteFlag(this.getBbbCatalogTools()
						.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
				giftRegistryViewBean
						.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
				giftRegistryViewBean.setServiceName(this.getUpdateRegItemsServiceName());

				this.logDebug("siteFlag: " + giftRegistryViewBean.getSiteFlag());
				this.logDebug("userToken: " + giftRegistryViewBean.getUserToken());
				this.logDebug("ServiceName: " + this.getUpdateRegItemsServiceName());

				// remove gift registry item
				final ManageRegItemsResVO mageItemsResVO = this.getGiftRegistryManager()
						.removeUpdateGiftRegistryItem(profile, giftRegistryViewBean);

				if (mageItemsResVO.getServiceErrorVO().isErrorExists()) {

					this.errorRemoveItems(pRequest, mageItemsResVO);
				} else {
					this.sessionUpdateForRemoveItem(pRequest, siteId, pRegistryId,
							giftRegistryViewBean, prodId);

				}
				this.getGiftRegSessionBean().setRegistryOperation(BBBGiftRegistryConstants.GR_ITEM_REMOVE);

			}

		} catch (final BBBBusinessException e) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBBusinessException from handleRemoveItemFromGiftRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1009), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_REM_REGITEM_BIZ_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_REM_REGITEM_BIZ_EXCEPTION));
		} catch (final BBBSystemException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBSystemException from handleRemoveItemFromGiftRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1008), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_REM_REGITEM_SYS_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_REM_REGITEM_SYS_EXCEPTION));
		}

		this.logDebug("GiftRegistryFormHandler.handleRemoveItemFromGiftRegistry() method ends");
		if (!BBBCoreConstants.REST_REDIRECT_URL.equalsIgnoreCase(this.getSuccessURL())) {
			this.setSuccessURL(getSuccessURL() + BBBCoreConstants.AMPERSAND + BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP
					+ BBBCoreConstants.EQUAL + dataScrollTop + BBBCoreConstants.AMPERSAND
					+ BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID + BBBCoreConstants.EQUAL + dataAccordionId);
		}
		return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
	}

	/**
	 * @param pRequest
	 * @param siteId
	 * @param pRegistryId
	 * @param giftRegistryViewBean
	 * @param prodId
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */@SuppressWarnings("rawtypes")
	private void sessionUpdateForRemoveItem(
			final DynamoHttpServletRequest pRequest, final String siteId,
			final String pRegistryId,
			final GiftRegistryViewBean giftRegistryViewBean, final String prodId)
			throws BBBSystemException, BBBBusinessException {

		getGiftRegistryManager().getGiftRegistryTools().invalidateRegistry(giftRegistryViewBean);
		List<GiftRegistryViewBean> bean = new ArrayList<GiftRegistryViewBean>();
		bean.add(giftRegistryViewBean);
		this.logDebug("Invalidating registry cache");
		getGiftRegistryManager().getGiftRegistryTools().invalidateRegistryCache(bean);
		
		// for omniture tagging
		if (!StringUtils.isEmpty(giftRegistryViewBean.getQuantity())
				&& (giftRegistryViewBean.getQuantity().equalsIgnoreCase("0"))) {
			this.setRegistryItemOperation(BBBGiftRegistryConstants.GR_ITEM_REMOVE);
			this.setRemovedProductId(prodId);
		}

		final int oldRequestedQuantity = Integer.parseInt(StringUtils.isEmpty(this.getRegItemOldQty()) ? "0"
				: (String) this.getValue().get("regItemOldQty"));
		// Update session data so that it will be in sync with the
		// registred items on registry flyout.
		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
				.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		final Map sessionMap = sessionBean.getValues();
		RegistryResVO registryResVO = getGiftRegistryManager()
				.getRegistryInfoFromEcomAdmin(giftRegistryViewBean.getRegistryId(), siteId);
		sessionBean.getValues().put(giftRegistryViewBean.getRegistryId() + REG_SUMMARY_KEY_CONST,
				registryResVO);
		final RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap
				.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
		if ((registrySummaryVO != null)
				&& registrySummaryVO.getRegistryId().equalsIgnoreCase(pRegistryId)) {
			// update quantiry in session
			int qtyAfterDelete = registrySummaryVO.getGiftRegistered() - oldRequestedQuantity;
			registrySummaryVO
					.setGiftRegistered(qtyAfterDelete);
			this.setTotalGiftRegistered(qtyAfterDelete);

		} else if ((registrySummaryVO != null)
				&& !registrySummaryVO.getRegistryId().equalsIgnoreCase(pRegistryId)) {
			// update the registrysummaryvo in the BBBsessionBean
			final RegistrySummaryVO regSummaryVO = this.getGiftRegistryManager()
					.getRegistryInfo(giftRegistryViewBean.getRegistryId(), siteId);
			sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, regSummaryVO);
		}
	}

	/**
	 * @param pRequest
	 */
	private void populateRedirectUrls(final DynamoHttpServletRequest pRequest) {
		if (StringUtils.isNotEmpty(getFromPage())) {
			
			StringBuilder appendData = new StringBuilder(BBBCoreConstants.BLANK);
			if(StringUtils.isNotEmpty(getQueryParam())){
				appendData.append(BBBCoreConstants.QUESTION_MARK).append(getQueryParam());
			}
			StringBuilder successURL = new StringBuilder(BBBCoreConstants.BLANK);
			StringBuilder errorURL = new StringBuilder(BBBCoreConstants.BLANK);
			successURL.append(pRequest.getContextPath())
					.append(getSuccessUrlMap().get(getFromPage()))
					.append(appendData);
			errorURL.append(pRequest.getContextPath())
					.append(getErrorUrlMap().get(getFromPage()));

			setSuccessURL(successURL.toString());
			setErrorURL(errorURL.toString());
			
		}
	}
	
	/**
	 * 
	 * @param pRequest
	 * @param mageItemsResVO
	 */
	private void errorRemoveItems(final DynamoHttpServletRequest pRequest, final ManageRegItemsResVO mageItemsResVO) {
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO().getErrorDisplayMessage()) && (mageItemsResVO
				.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR))// Technical
		// Error
		{
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Fatal error from errorRemoveItems of GiftRegistryFormHandler Webservice error id ="
							+ mageItemsResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1094));
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
		}
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO().getErrorDisplayMessage())
				&& (mageItemsResVO.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN))// Technical
		// Error
		{

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Either user token or site flag invalid from errorRemoveItems of GiftRegistryFormHandler Webservice error id ="
							+ mageItemsResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1095));
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));
		}
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO().getErrorDisplayMessage()) && (mageItemsResVO
				.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT))// Technical
		// Error
		{

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry input fields format error from handleRemoveItemFromGiftRegistry() of "
							+ WEB_SERVICE_ERROR_CODE + mageItemsResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
		}

		this.addFormException(new DropletException(
				this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_REM_REGITEM_SYS_EXCEPTION,
						pRequest.getLocale().getLanguage(), null, null),
				BBBGiftRegistryConstants.ERR_REM_REGITEM_SYS_EXCEPTION));
	}

	/**
	 * pre method for handleRemoveItemFromGiftRegistry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public void preRemoveItemFromGiftRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws BBBBusinessException, BBBSystemException {

		this.logDebug("GiftRegistryFormHandler.preRemoveItemFromGiftRegistry() method start");

		final Profile profile = (Profile) pRequest.getObjectParameter(BBBCoreConstants.PROFILE);

		if (profile.isTransient()) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"User type is transient Exception from preRemoveItemFromGiftRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1003));
			this.addFormException(new DropletFormException("err_rem_regitem_transient_user",
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_NEW_ADDRESS_LAST_NAME,
					"err_rem_regitem_transient_user"));
		}
		this.logDebug("GiftRegistryFormHandler.preRemoveItemFromGiftRegistry() method ends");
	}

	/**
	 * This method is a wrapper method used to send an email with gift registry
	 * URL for REST API.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true / false
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public boolean handleEmailGiftRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		boolean isError = false;
		if (StringUtils.isEmpty(this.getSenderEmail())) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.EMPTY_SENDER_MAIL_MESSAGE,
					BBBGiftRegistryConstants.ERR_SENDER_MAIL_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRecipientEmail())) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.EMPTY_RECEPIENT_MAIL_MESSAGE,
					BBBGiftRegistryConstants.ERR_RECEPIENT_MAIL_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getEventType())) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.EMPTY_EVENT_TYPE_MESSAGE,
					BBBGiftRegistryConstants.ERR_EVENT_TYPE_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRegistryId())) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.EMPTY_REGISTRY_ID_MESSAGE,
					BBBGiftRegistryConstants.ERR_REGISTRY_ID_EMPTY_OR_NULL));
			isError = true;
		} else if (!BBBUtility.isValidateRegistryId(this.getRegistryId())) {

			this.addFormException(new DropletException(BBBGiftRegistryConstants.INVALID_REGISTRY_ID_MESSAGE,
					BBBGiftRegistryConstants.ERR_REGISTRY_ID_EMPTY_OR_NULL));
			isError = true;
		}

		if (this.getDaysToGo() < 0) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_DAYS_TO_GO_INVALID_MESSAGE,
					BBBGiftRegistryConstants.ERR_DAYS_TO_GO_INVALID));
			isError = true;
		}

		if (!BBBUtility.isRegistryNameValid(this.getRegistryName())) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_REGISTRY_NAME,
					BBBGiftRegistryConstants.ERR_INVALID_REGISTRY_NAME));
			isError = true;
		}

		if (!isError) {
			Map<String, Object> values = this.getEmailHolder().getValues();

			values.put(BBBCoreConstants.REGISTRY_URL, this.getRegistryURL());
			values.put(BBBCoreConstants.RECIPIENT_EMAIL, this.getRecipientEmail());
			values.put(BBBGiftRegistryConstants.MESSAGE_REGISTRY, this.getMessage());
			values.put(BBBCoreConstants.SENDER_EMAIL, this.getSenderEmail());
			values.put(BBBGiftRegistryConstants.EVENT_TYPE, this.getEventType());
			values.put(BBBGiftRegistryConstants.CC_FLAG, Boolean.valueOf(this.isCcFlag()));
			values.put(BBBGiftRegistryConstants.P_REG_FIRST_NAME, this.getRegFirstName());
			values.put(BBBGiftRegistryConstants.P_REG_LAST_NAME, this.getRegLastName());
			values.put(BBBGiftRegistryConstants.COREG_FIRST_NAME, this.getCoRegFirstName());
			values.put(BBBGiftRegistryConstants.COREG_LAST_NAME, this.getCoRegLastName());
			values.put(BBBGiftRegistryConstants.EVENT_DATE, this.getRegistryEventDate());
			values.put(BBBGiftRegistryConstants.DAYS_TO_GO, Long.valueOf(this.getDaysToGo()));
			values.put(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE, this.getRegistryName());// this
																									// is
																									// the
																									// registry
																									// type
																									// name
			values.put(BBBGiftRegistryConstants.REGISTRY_ID_PARAM, this.getRegistryId());
			values.put(BBBCoreConstants.SUBJECT_PARAM_NAME, this.getSubject());
			values.put(BBBGiftRegistryConstants.TITLE, this.getTitle());
			values.put(BBBGiftRegistryConstants.DATE_LABEL,
					"<strong>" + this.getDaysToGo() + "</strong>&nbsp;" + this.getDateLabel());
			this.setValidatedCaptcha(false);
			return this.handleEmailRegistry(pRequest, pResponse);
		}
		final JSONObject responseJson = new JSONObject();
		try {
			responseJson.put(BBBGiftRegistryConstants.ERROR_MESSAGES,
					BBBGiftRegistryConstants.EMPTY_INPUTT_JSON_MESSAGE);
		} catch (final JSONException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"JSONException from handleEmailGiftRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1096), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION));
		}
		final PrintWriter out = pResponse.getWriter();
		out.print(responseJson.toString());
		out.flush();
		out.close();
		return false;
	}

	/**
	 * This method is used to send an email with gift registry URL for
	 * recommendation.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true / false
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public boolean handleEmailRegistryRecommendation(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		this.logDebug("GiftRegistryFormHandler.handleEmailRegistryRecommendation() method start");
		try {
			final Map<String, Object> values = this.getEmailHolder().getValues();
			this.setRecipientEmail((String) values.get(BBBCoreConstants.RECIPIENT_EMAIL));
			String registryId = (String) values.get(BBBCoreConstants.REGISTRY_ID);
			pRequest.setParameter(BBBGiftRegistryConstants.EMAIL_TYPE,
					BBBGiftRegistryConstants.EMAIL_REGISTRY_RECOMMENDATION);
			String eventTypeAsConfigurable = this.getEventType();
			if (BBBUtility.isEmpty(eventTypeAsConfigurable)) {
				eventTypeAsConfigurable = (String) values.get(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE);
			}
			pRequest.setParameter(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE, eventTypeAsConfigurable);
			if (BBBUtility.isNotEmpty(this.getRecipientEmail())) {
				this.setRecipientEmail(this.getRecipientEmail().replace(BBBCoreConstants.SPACE, BBBCoreConstants.BLANK)
						.trim());
			}
			boolean isValidEmailAddress = true;
			Set<String> recipientEmailList = null;
			if (!BBBUtility.isEmpty(this.getRecipientEmail())) {
				recipientEmailList = new HashSet<String>(Arrays.asList(this.getRecipientEmail().split(",")));
				for (final String email : recipientEmailList) {
					if (!BBBUtility.isValidEmail(email)) {
						isValidEmailAddress = false;
						this.logDebug("GiftRegistryFormHandler.handleEmailRegistryRecommendation() invalid emailId: "
								+ email);
						String errorSendingEmail = this.getLblTxtTemplateManager().getErrMsg(
								BBBGiftRegistryConstants.ERR_INVALID_EMAIL_ID, pRequest.getLocale().getLanguage(), null,
								null);
						this.addFormException(new DropletException(errorSendingEmail,
								BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION));
						getRegErrorMap().put(BBBCoreConstants.EMAIL_ERROR, errorSendingEmail);
						break;
					}
				}
			} else {
				isValidEmailAddress = false;
			}
			if (isValidEmailAddress) {
				this.logDebug("GiftRegistryFormHandler.handleEmailRegistryRecommendation() recipientEmail "
						+ this.getRecipientEmail());
				final String siteId = this.getSiteContext().getSite().getId();
				RegistryVO registryVO = new RegistryVO();
				registryVO.getPrimaryRegistrant().setFirstName(this.getRegFirstName());
				registryVO.setRegistryId(registryId);
				registryVO.getEvent().setEventDate(this.getRegistryEventDate());
				registryVO.getRegistryType().setRegistryTypeName(this.getEventType());
				registryVO.setSiteId(siteId);
				Map<String, RepositoryItem> tokensMap = getGiftRegistryManager().getGiftRegistryTools()
						.persistRecommendationToken(registryVO, recipientEmailList);
				final boolean isEmailSuccess = this.getGiftRegistryManager().sendEmailRegistryRecommendation(siteId,
						values, this.getGiftRegEmailInfo(), tokensMap);
				if (isEmailSuccess) {
					this.logDebug(
							"GiftRegistryFormHandler.handleEmailRegistryRecommendation() isEmailSuccess=true , email sent to "
									+ this.getRecipientEmail());
				} else {
					this.logDebug("GiftRegistryFormHandler.handleEmailRegistryRecommendation() isEmailSuccess=false , "
							+ "there seems to be some problem with SMTP");
					String errorSendingEmail = this.getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION, pRequest.getLocale().getLanguage(),
							null, null);
					this.addFormException(
							new DropletException(errorSendingEmail, BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION));
					getRegErrorMap().put(BBBCoreConstants.EMAIL_ERROR, errorSendingEmail);
				}
			}
		} catch (final BBBSystemException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBSystemException from handleEmailRegistryRecommendation of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1097), e);
			String errorSendingEmail = this.getLblTxtTemplateManager().getErrMsg(
					BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION, pRequest.getLocale().getLanguage(), null, null);
			this.addFormException(
					new DropletException(errorSendingEmail, BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION));
			getRegErrorMap().put(BBBCoreConstants.EMAIL_ERROR, errorSendingEmail);
		}
		this.logDebug("GiftRegistryFormHandler.handleEmailRegistryRecommendation() method ends");
		return this.checkFormRedirect(this.getEmailRecommendationSuccessURL(), this.getEmailRecommendationErrorURL(), pRequest,
				pResponse);
	}
	
	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return true/false
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleEmailGiftRegistryRecommendation(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		boolean isError = false;
		if (StringUtils.isEmpty(this.getRecipientEmail())) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.EMPTY_REGISTRY_ID_MESSAGE,
					BBBGiftRegistryConstants.ERR_RECEPIENT_MAIL_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRegistryURL())) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.EMPTY_REGISTRY_URL_MESSAGE,
					BBBGiftRegistryConstants.ERR_REGISTRY_URL_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRegFirstName())) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.EMPTY_FIRST_NAME_MESSAGE,
					BBBGiftRegistryConstants.ERR_FIRST_NAME_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRegLastName())) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.EMPTY_LAST_NAME_MESSAGE,
					BBBGiftRegistryConstants.ERR_LAST_NAME_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRegistryEventDate())) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.EMPTY_EVENT_DATE_MESSAGE,
					BBBGiftRegistryConstants.ERR_EVENT_DATE_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getEventType())) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.EMPTY_EVENT_TYPE_MESSAGE,
					BBBGiftRegistryConstants.ERR_EVENT_TYPE_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRegistryId())) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.EMPTY_REGISTRY_ID_MESSAGE,
					BBBGiftRegistryConstants.ERR_REGISTRY_ID_EMPTY_OR_NULL));
			isError = true;
		} else if (!BBBUtility.isValidateRegistryId(this.getRegistryId())) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.INVALID_REGISTRY_ID_MESSAGE,
					BBBGiftRegistryConstants.ERR_REGISTRY_ID_EMPTY_OR_NULL));
			isError = true;
		}
		if (!BBBUtility.isRegistryNameValid(this.getRegistryName())) {
			this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_REGISTRY_NAME,
					BBBGiftRegistryConstants.ERR_INVALID_REGISTRY_NAME));
			isError = true;
		}
		if (!isError) {
			Map<String, Object> values = this.getEmailHolder().getValues();
			values.put(BBBCoreConstants.RECIPIENT_EMAIL, this.getRecipientEmail());
			values.put(BBBCoreConstants.REGISTRY_URL, this.getRegistryURL());
			values.put(BBBGiftRegistryConstants.P_REG_FIRST_NAME, this.getRegFirstName());
			values.put(BBBGiftRegistryConstants.P_REG_LAST_NAME, this.getRegLastName());
			values.put(BBBGiftRegistryConstants.MESSAGE_REGISTRY, this.getMessage());
			values.put(BBBGiftRegistryConstants.EVENT_TYPE, this.getEventType());
			values.put(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE, this.getRegistryName());// this
																									// is
																									// the
																									// registry
																									// type
																									// name
			values.put(BBBGiftRegistryConstants.REGISTRY_ID_PARAM, this.getRegistryId());
			this.setRegErrorMap(new HashMap<String, String>());
			return this.handleEmailRegistryRecommendation(pRequest, pResponse);
		}
		return false;
	}

	/**
	 * This method is used to send an email with gift registry URL.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true / false
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public boolean handleEmailRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		this.logDebug("GiftRegistryFormHandler.handleEmailRegistry() method start");
		StringBuilder successURL = new StringBuilder(BBBCoreConstants.BLANK);
		StringBuilder errorURL = new StringBuilder(BBBCoreConstants.BLANK);
		if (StringUtils.isNotEmpty(getFromPage())) {
			
			successURL.append(pRequest.getContextPath())
			.append(getSuccessUrlMap().get(getFromPage()));
			
			errorURL.append(pRequest.getContextPath())
			.append(getErrorUrlMap().get(getFromPage()));
			
			StringBuilder appendData = new StringBuilder("");
			if(StringUtils.isNotEmpty(getSuccessQueryParam())){
				appendData.append(BBBCoreConstants.QUESTION_MARK).append(getSuccessQueryParam());
				successURL.append(appendData);
				
			}
			if(StringUtils.isNotEmpty(getErrorQueryParam())){
				appendData.append(BBBCoreConstants.QUESTION_MARK).append(getErrorQueryParam());
				errorURL.append(appendData);
			}
		}
			setSuccessURL(successURL.toString());
			setErrorURL(errorURL.toString());
		final JSONObject responseJson = new JSONObject();
		try {

			pResponse.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON);

			if (this.validateCaptcha(pRequest, pResponse)) {

				final Map<String, Object> values = this.getEmailHolder().getValues();
				final String senderEmailAddress = (String) values.get(BBBCoreConstants.SENDER_EMAIL);
				if (this.getEmailHolder().getCcFlag()) {
					values.put(MESSAGE_CC, senderEmailAddress);
				} else {
					if (values.containsKey(MESSAGE_CC)) {
						values.remove(MESSAGE_CC);
					}
				}
				this.setRecipientEmail((String) values.get(BBBCoreConstants.RECIPIENT_EMAIL));

				if (BBBUtility.isNotEmpty(this.getRecipientEmail())) {
					this.setRecipientEmail(this.getRecipientEmail().replace(BBBCoreConstants.SPACE, BBBCoreConstants.BLANK)
							.trim());
				}

				boolean isValidEmailAddress = true;

				String[] emails = null;
				if (!BBBUtility.isEmpty(this.getRecipientEmail())) {
					emails = this.getRecipientEmail().split(BBBCoreConstants.SEMICOLON);
					for (final String email : emails) {
						if (!BBBUtility.isValidEmail(email)) {
							isValidEmailAddress = false;
							break;
						}
					}
				} else {
					isValidEmailAddress = false;
				}

				if (!(isValidEmailAddress && BBBUtility.isValidEmail(senderEmailAddress))) {
					// email is invalid
					responseJson.put(BBBGiftRegistryConstants.ERROR, BBBGiftRegistryConstants.GENERAL_ERROR);
					responseJson.put(BBBGiftRegistryConstants.ERROR_MESSAGES, this.getLblTxtTemplateManager().getErrMsg(
							"err_js_email_extended_multiple", pRequest.getLocale().getLanguage(), null, null));
				} else {
					this.logDebug(
							"GiftRegistryFormHandler.handleEmailRegistry() recipientEmail " + this.getRecipientEmail());

					final Map<String, String> placeHolderMap = new HashMap<String, String>();

					final String siteId = this.getSiteContext().getSite().getId();

					final boolean isEmailSuccess = this.getGiftRegistryManager().sendEmailRegistry(siteId, values,
							this.getGiftRegEmailInfo());

					if (isEmailSuccess) {
						this.setRecipientEmail(this.getRecipientEmail().replaceAll(BBBCoreConstants.SEMICOLON,
								BBBCoreConstants.SEMICOLON + BBBCoreConstants.SPACE));
						placeHolderMap.put(BBBCoreConstants.RECIPIENT_EMAIL, this.getRecipientEmail());
						responseJson.put(BBBGiftRegistryConstants.SUCCESS,
								this.getLblTxtTemplateManager().getPageTextArea("txt_registry_email_sent_msg",
										pRequest.getLocale().getLanguage(), placeHolderMap, null));

						this.logDebug(
								"GiftRegistryFormHandler.handleEmailRegistry() isEmailSuccess=true , email sent to "
										+ this.getRecipientEmail());

					} else {
						this.logDebug(
								"GiftRegistryFormHandler.handleEmailRegistry() isEmailSuccess=false , there seems to be some problem with SMTP");
						responseJson.put(BBBGiftRegistryConstants.ERROR, BBBGiftRegistryConstants.SERVER_ERROR);
						responseJson.put(BBBGiftRegistryConstants.ERROR_MESSAGES, this.getLblTxtTemplateManager()
								.getErrMsg("err_email_internal_error", pRequest.getLocale().getLanguage(), null, null));
						this.addFormException(new DropletException(
								this.getLblTxtTemplateManager().getErrMsg(
										BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
										pRequest.getLocale().getLanguage(), null, null),
								BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION));

					}
				}
			} else {

				// when captch is incorrect
				responseJson.put(BBBGiftRegistryConstants.ERROR, BBBGiftRegistryConstants.GENERAL_ERROR);
				responseJson.put(BBBGiftRegistryConstants.ERROR_MESSAGES, this.getLblTxtTemplateManager()
						.getErrMsg("err_email_incorrect_captcha", pRequest.getLocale().getLanguage(), null, null));

			}

			this.logDebug("GiftRegistryFormHandler.handleEmailRegistry() printing to output stream");

			final PrintWriter out = pResponse.getWriter();
			out.print(responseJson.toString());
			out.flush();
			out.close();
			this.logDebug("Json String " + responseJson);
		} catch (final JSONException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"JSONException from handleEmailRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1096), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION));

		} catch (final BBBSystemException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBSystemException from handleEmailRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1097), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION));
		}

		this.logDebug("GiftRegistryFormHandler.handleEmailRegistry() method ends");
		this.logDebug("Success URL " + this.getSuccessURL() + "Error URL " + this.getErrorURL());
		return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
	}

	/**
	 * This method is used to send an email with gift registry URL.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true / false
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public boolean handleEmailMxRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		this.logDebug("GiftRegistryFormHandler.handleEmailRegistry() method start");

		final JSONObject responseJson = new JSONObject();
		try {

			pResponse.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON);

			if (this.validateCaptcha(pRequest, pResponse)) {

				final Map<String, Object> values = this.getEmailHolder().getValues();
				final String senderEmailAddress = (String) values.get(BBBCoreConstants.SENDER_EMAIL);
				if (this.getEmailHolder().getCcFlag()) {
					values.put(MESSAGE_CC, senderEmailAddress);
				} else {
					if (values.containsKey(MESSAGE_CC)) {
						values.remove(MESSAGE_CC);
					}
				}
				this.setRecipientEmail((String) values.get(BBBCoreConstants.RECIPIENT_EMAIL));

				if (BBBUtility.isNotEmpty(this.getRecipientEmail())) {
					this.setRecipientEmail(this.getRecipientEmail().replace(BBBCoreConstants.SPACE, BBBCoreConstants.BLANK)
							.trim());
				}

				boolean isValidEmailAddress = true;

				String[] emails = null;
				if (!BBBUtility.isEmpty(this.getRecipientEmail())) {
					emails = this.getRecipientEmail().split(BBBCoreConstants.SEMICOLON);
					for (final String email : emails) {
						if (!BBBUtility.isValidEmail(email)) {
							isValidEmailAddress = false;
							break;
						}
					}
				} else {
					isValidEmailAddress = false;
				}

				if (!(isValidEmailAddress && BBBUtility.isValidEmail(senderEmailAddress))) {
					// email is invalid
					responseJson.put(BBBGiftRegistryConstants.ERROR, BBBGiftRegistryConstants.GENERAL_ERROR);
					responseJson.put(BBBGiftRegistryConstants.ERROR_MESSAGES, this.getLblTxtTemplateManager().getErrMsg(
							"err_js_email_extended_multiple", pRequest.getLocale().getLanguage(), null, null));
				} else {
					this.logDebug(
							"GiftRegistryFormHandler.handleEmailMxRegistry() recipientEmail " + this.getRecipientEmail());

					final Map<String, String> placeHolderMap = new HashMap<String, String>();

					final String siteId = this.getSiteContext().getSite().getId();

					final boolean isEmailSuccess = this.getGiftRegistryManager().sendEmailMxRegistry(siteId, values,
							this.getGiftRegEmailInfo());

					if (isEmailSuccess) {
						this.setRecipientEmail(this.getRecipientEmail().replaceAll(BBBCoreConstants.SEMICOLON,
								BBBCoreConstants.SEMICOLON + BBBCoreConstants.SPACE));
						placeHolderMap.put(BBBCoreConstants.RECIPIENT_EMAIL, this.getRecipientEmail());
						responseJson.put(BBBGiftRegistryConstants.SUCCESS,
								this.getLblTxtTemplateManager().getPageTextArea("txt_registry_email_sent_msg_mx",
										pRequest.getLocale().getLanguage(), placeHolderMap, null));

						this.logDebug(
								"GiftRegistryFormHandler.handleEmailMxRegistry() isEmailSuccess=true , email sent to "
										+ this.getRecipientEmail());

					} else {
						this.logDebug(
								"GiftRegistryFormHandler.handleEmailMxRegistry() isEmailSuccess=false , there seems to be some problem with SMTP");
						responseJson.put(BBBGiftRegistryConstants.ERROR, BBBGiftRegistryConstants.SERVER_ERROR);
						responseJson.put(BBBGiftRegistryConstants.ERROR_MESSAGES, this.getLblTxtTemplateManager()
								.getErrMsg("err_email_internal_error", pRequest.getLocale().getLanguage(), null, null));
						this.addFormException(new DropletException(
								this.getLblTxtTemplateManager().getErrMsg(
										BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
										pRequest.getLocale().getLanguage(), null, null),
								BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION));

					}
				}
			} else {

				// when captch is incorrect
				responseJson.put(BBBGiftRegistryConstants.ERROR, BBBGiftRegistryConstants.GENERAL_ERROR);
				responseJson.put(BBBGiftRegistryConstants.ERROR_MESSAGES, this.getLblTxtTemplateManager()
						.getErrMsg("err_email_incorrect_captcha", pRequest.getLocale().getLanguage(), null, null));

			}

			this.logDebug("GiftRegistryFormHandler.handleEmailMxRegistry() printing to output stream");

			final PrintWriter out = pResponse.getWriter();
			out.print(responseJson.toString());
			out.flush();
			out.close();

		} catch (final JSONException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"JSONException from handleEmailMxRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1096), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION));

		} catch (final BBBSystemException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBSystemException from handleEmailMxRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1097), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION));
		}

		this.logDebug("GiftRegistryFormHandler.handleEmailMxRegistry() method ends");

		return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
	}

	/**
	 * Validate captcha.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true, if successful
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private boolean validateCaptcha(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		this.logDebug("GiftRegistryFormHandler.validateCaptcha() method starts");
		boolean success = true;

		this.logDebug("SessionId = " + pRequest.getSession());
		this.logDebug("SessionId = " + pRequest.getSession().getId());

		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		final Captcha captcha = sessionBean.getCaptcha();

		this.logDebug("Captcha User entered = " + this.getCaptchaAnswer());
		this.logDebug("Captcha  = " + captcha);

		pRequest.setCharacterEncoding("UTF-8");

		// if captcha validation is enabled
		if (this.isValidatedCaptcha() && this.getCaptchaAnswer() != null
				&& !captcha.isCorrect(this.getCaptchaAnswer())) {

			success = false;
		}
		this.logDebug("GiftRegistryFormHandler.validateCaptcha() method ends");
		return success;

	}

	/**
	 * This method is used to check if any co registrant field is filled by the
	 * user or not.
	 *
	 * @return true if any field is filled by the user else, return false.
	 */
	private boolean checkCoregistrantValidationStat() {

		this.logDebug("GiftRegistryFormHandler.checkCoregistrantValidationStat() method start");

		boolean validationFlag = false;

		if (!BBBUtility.isEmpty(this.getRegistryVO().getCoRegistrant().getFirstName())) {
			validationFlag = true;
			return validationFlag;
		}

		if (!BBBUtility.isEmpty(this.getRegistryVO().getCoRegistrant().getLastName())) {
			validationFlag = true;
			return validationFlag;
		}

		if (!BBBUtility.isEmpty(this.getRegistryVO().getCoRegistrant().getEmail())) {
			validationFlag = true;
			return validationFlag;
		}

		if (!BBBUtility.isEmpty(this.getPassword())) {
			validationFlag = true;
			return validationFlag;
		}

		this.logDebug("GiftRegistryFormHandler.checkCoregistrantValidationStat() method ends");

		return validationFlag;
	}

	/**
	 * This methos is used for shipping create registry shipping validation.
	 *
	 */
	private void shippingNewAddressValidation(final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.ShippingNewAddressValidation() method start");

		// VALIDATE SHIPPING NEW ADDRESS FIRST NAME
		if (!BBBUtility.isValidFirstName(this.getRegistryVO().getShipping().getShippingAddress().getFirstName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_NEW_ADDRESS_FIRST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
		}

		// VALIDATE SHIPPING NEW LAST NAME
		if (!BBBUtility.isValidLastName(this.getRegistryVO().getShipping().getShippingAddress().getLastName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_NEW_ADDRESS_LAST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

		}

		// VALIDATE SHIPPING NEW ADDRESS
		this.shippingNewAddress(pRequest);

		this.logDebug("GiftRegistryFormHandler.ShippingNewAddressValidation() method ends");

	}

	/**
	 * This method is used to validate new shipping address.
	 */
	private void shippingNewAddress(final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.shippingNewAddress() method start");

		// VALIDATE SHIPPING NEW ADDRESS LINE1
		if (!BBBUtility
				.isValidAddressLine1(this.getRegistryVO().getShipping().getShippingAddress().getAddressLine1())) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Invalid AddressLine1 from shippingNewAddress of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1032));
			this.addFormException(new DropletFormException(

					BBBGiftRegistryConstants.ERR_CREATE_REG_ADDRESS_LINE1_INVALID_SHIPPING,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_NEW_ADDRESS_CONTACT_ADDR_LINE1,
					BBBGiftRegistryConstants.ERR_CREATE_REG_ADDRESS_LINE1_INVALID_SHIPPING));

		}

		// VALIDATE SHIPPING NEW ADDRESS LINE2
		if (!BBBUtility.isEmpty(this.getRegistryVO().getShipping().getShippingAddress().getAddressLine2())
				&& !BBBUtility.isValidAddressLine2(
						this.getRegistryVO().getShipping().getShippingAddress().getAddressLine2())) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Invalid AddressLine2 from shippingNewAddress of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1033));
			this.addFormException(new DropletFormException("err_create_reg_address_line2_invalid_shipping",
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_NEW_ADDRESS_CONTACT_ADDR_LINE2,
					"err_create_reg_address_line2_invalid_shipping"));
		}

		// VALIDATE SHIPPING NEW ADDRESS CITY
		if (!BBBUtility.isValidCity(this.getRegistryVO().getShipping().getShippingAddress().getCity())) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Invalid Shipping city from shippingNewAddress of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1035));
			this.addFormException(new DropletFormException("err_create_reg_city_invalid_shipping",
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_NEW_ADDRESS_CONTACT_ADDR_CITY,
					"err_create_reg_city_invalid_shipping"));
		}

		// VALIDATE SHIPPING NEW ADDRESS ZIP
		if (!BBBUtility.isValidZip(this.getRegistryVO().getShipping().getShippingAddress().getZip())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry Invalid zip for shipping address from shippingNewAddress of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1047));

			this.addFormException(new DropletFormException("err_create_reg_zip_invalid_shipping",
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_NEW_ADDRESS_CONTACT_ADDR_ZIP,
					"err_create_reg_zip_invalid_shipping"));
		}

		this.logDebug("GiftRegistryFormHandler.shippingNewAddress() method ends");
	}

	/**
	 * This method is used for shipping create registry future shipping
	 * validation.
	 */
	private void shippingFutureAddressValidation(final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.ShippingFutureAddressValidation() method start");

		// VALIDATE SHIPPING NEW ADDRESS FIRST NAME
		if (!BBBUtility
				.isValidFirstName(this.getRegistryVO().getShipping().getFutureshippingAddress().getFirstName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_FUTURE_ADDRESS_FIRST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
		}

		// VALIDATE SHIPPING NEW LAST NAME
		if (!BBBUtility.isValidLastName(this.getRegistryVO().getShipping().getFutureshippingAddress().getLastName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_FUTURE_ADDRESS_LAST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

		}

		this.shippingNewFutureAddValidation(pRequest);

		this.logDebug("GiftRegistryFormHandler.ShippingFutureAddressValidation() method ends");

	}

	/**
	 * This method is used for future shipping future date.
	 *
	 * @param futureShippingDate
	 *            the future shipping date
	 */
	private void futureShipDateValidation(final String futureShippingDate, final boolean updateFlag,
			final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.futureShipDateValidation() method start");
			if (!StringUtils.isEmpty(this.getFutureShippingDateSelected()) && 
					this.getFutureShippingDateSelected().equalsIgnoreCase("true") 
					&&  StringUtils.isEmpty(futureShippingDate)) {

				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"GiftRegistry empty future shipping date from futureShipDateValidation of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1054));

				this.addFormException(new DropletFormException(BBBCoreConstants.ERR_REG_SHIPPING_DATE_FURTURE_INVALID,
						this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
								+ BBBGiftRegistryConstants.FUTURE_SHIPPING_DATE,
						BBBCoreConstants.ERR_REG_SHIPPING_DATE_FURTURE_INVALID));
			}
			else if (!StringUtils.isEmpty(this.getFutureShippingDateSelected()) && 
					this.getFutureShippingDateSelected().equalsIgnoreCase("true") 
					&&  !StringUtils.isEmpty(futureShippingDate)
					&& !isValidFutureDate(futureShippingDate, BBBCoreConstants.DATE_FORMAT)) {

				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"GiftRegistry Invalid future shipping date from futureShipDateValidation of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1054));

				this.addFormException(new DropletFormException(BBBCoreConstants.ERR_REG_SHIPPING_DATE_FURTURE_INVALID,
						this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
								+ BBBGiftRegistryConstants.FUTURE_SHIPPING_DATE,
						BBBCoreConstants.ERR_REG_SHIPPING_DATE_FURTURE_INVALID));
			}
		
		this.logDebug("GiftRegistryFormHandler.futureShipDateValidation() method ends");

	}

	/**
	 * Checking valid date format.
	 *
	 * @param dateStr
	 *            the date str
	 * @param formatStr
	 *            the format str
	 * @return boolean
	 */

	public static boolean isValidFutureDate(final String dateStr, final String formatStr) {
		if (formatStr == null) {
			return false;
		}
		final SimpleDateFormat sDateFormat = new SimpleDateFormat(formatStr,
				ServletUtil.getCurrentRequest().getLocale());
		Date futureShipDate = null;
		try {
			futureShipDate = sDateFormat.parse(dateStr);
		} catch (final ParseException e) {
			// invalid date format
			return false;
		}
		final Calendar currentDate = Calendar.getInstance();
		currentDate.set(Calendar.HOUR_OF_DAY, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 0);
		currentDate.set(Calendar.MILLISECOND, 0);
		if (currentDate.getTime().after(futureShipDate)) {
			return false;
		}

		return true;
	}

	/**
	 * This method is used for future shipping new address validation.
	 */
	private void shippingNewFutureAddValidation(final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.shippingNewFutureAddValidation() method start");

		// VALIDATE SHIPPING NEW ADDRESS LINE1
		if (!BBBUtility
				.isValidAddressLine1(this.getRegistryVO().getShipping().getFutureshippingAddress().getAddressLine1())) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Invalid New Future shipping AddressLine1 from shippingNewFutureAddValidation of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1032));
			this.addFormException(
					new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_ADDRESS_LINE1_INVALID_FUTURE,
							this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
									+ BBBGiftRegistryConstants.SHIPPING_FUTURE_ADDRESS_CONTACT_ADDR_LINE1,
							BBBGiftRegistryConstants.ERR_CREATE_REG_ADDRESS_LINE1_INVALID_FUTURE));

		}

		// VALIDATE SHIPPING NEW ADDRESS LINE2
		if (!BBBUtility.isEmpty(this.getRegistryVO().getShipping().getFutureshippingAddress().getAddressLine2())
				&& !BBBUtility.isValidAddressLine2(
						this.getRegistryVO().getShipping().getFutureshippingAddress().getAddressLine2())) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Invalid New Future shipping AddressLine2 from shippingNewFutureAddValidation of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1033));
			this.addFormException(new DropletFormException("err_create_reg_address_line2_invalid_future",
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_FUTURE_ADDRESS_CONTACT_ADDR_LINE2,
					"err_create_reg_address_line2_invalid_future"));
		}

		// VALIDATE SHIPPING NEW ADDRESS CITY
		if (!BBBUtility.isValidCity(this.getRegistryVO().getShipping().getFutureshippingAddress().getCity())) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Invalid New Shipping city from shippingNewFutureAddValidation of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1035));
			this.addFormException(new DropletFormException("err_create_reg_city_invalid_future",
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_FUTURE_ADDRESS_CONTACT_ADDR_CITY,
					"err_create_reg_city_invalid_future"));
		}

		// VALIDATE FUTURE SHIPPING NEW ADDRESS ZIP
		if (!BBBUtility.isValidZip(this.getRegistryVO().getShipping().getFutureshippingAddress().getZip())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry Invalid zip for future shipping address from shippingNewFutureAddValidation of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1047));

			this.addFormException(new DropletFormException("err_create_reg_zip_invalid_future",
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_FUTURE_ADDRESS_CONTACT_ADDR_ZIP,
					"err_create_reg_zip_invalid_future"));
		}

		this.logDebug("GiftRegistryFormHandler.shippingNewFutureAddValidation() method ends");

	}

	/**
	 * event type validation method.
	 * 
	 * @param pEventType
	 *            the event type
	 * @param pRequest
	 * @param registryInputList
	 *            List of all RegistryInputListVO's based on eventType
	 * @param registryAllInputList
	 *            List of mandatory RegistryInputListVO's for create registry
	 */
	private void eventCreateSimplifyFormValidation(final String pEventType, final DynamoHttpServletRequest pRequest,
			List<RegistryInputVO> registryAllInputList) {

		this.logDebug("GiftRegistryFormHandler.eventFormValidation() method start");
		final String siteId = this.getCurrentSiteId();
		String dateFormat = BBBCoreConstants.DATE_FORMAT;

		if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
			dateFormat = BBBCoreConstants.CA_DATE_FORMAT;
		}
		if (null != registryAllInputList) {
			if (isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.NETWORK_AFFILIATION)
					|| (!isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.NETWORK_AFFILIATION)
							&& isFieldDisplayedOnForm(registryAllInputList,
									BBBGiftRegistryConstants.NETWORK_AFFILIATION)
							&& !StringUtils.isEmpty(this.getRegistryVO().getNetworkAffiliation()))) {
				this.validateNetworkAffiliation(this.getRegistryVO().getNetworkAffiliation(),
						BBBGiftRegistryConstants.NETWORK_AFFILIATION, pRequest);
			}
			if (pEventType.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_WEDDING)) {
				if (isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.EVENT_DATE)
						|| (!isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.EVENT_DATE)
								&& isFieldDisplayedOnForm(registryAllInputList, BBBGiftRegistryConstants.EVENT_DATE)
								&& !StringUtils.isEmpty(this.getRegistryVO().getEvent().getEventDate()))) {
					this.validateEventDate(this.getRegistryVO().getEvent().getEventDate(),
							BBBGiftRegistryConstants.EVENT_DATE, dateFormat, pRequest);
				}
				if (isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.SHOWER_DATE_REG_INPUTFIELD)
						|| (!isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.SHOWER_DATE)
								&& isFieldDisplayedOnForm(registryAllInputList, BBBGiftRegistryConstants.SHOWER_DATE)
								&& !StringUtils.isEmpty(this.getRegistryVO().getEvent().getShowerDate()))) {
					this.validateShowerDate(this.getRegistryVO().getEvent().getShowerDate(),
							BBBGiftRegistryConstants.SHOWER_DATE, dateFormat, pRequest);
				}
				if (isFieldRequiresValidation(registryAllInputList,
						BBBGiftRegistryConstants.NUMBER_OF_GUESTS_REG_INPUTFIELD)
						|| (!isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.NUMBER_OF_GUESTS_REG_INPUTFIELD)
								&& isFieldDisplayedOnForm(registryAllInputList,
										BBBGiftRegistryConstants.NUMBER_OF_GUESTS_REG_INPUTFIELD)
								&& !StringUtils.isEmpty(this.getRegistryVO().getEvent().getGuestCount()))) {
					this.validateNumberOfGuest(this.getRegistryVO().getEvent().getGuestCount(),
							BBBGiftRegistryConstants.NUMBER_OF_GUESTS, pRequest);
				}
			} else if (pEventType.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_BABY)) {
				final String decorTheme = this.getRegistryVO().getEvent().getBabyNurseryTheme();
				if (isFieldRequiresValidation(registryAllInputList, BBBCoreConstants.NURSERY_THEME)
						|| (!isFieldRequiresValidation(registryAllInputList, BBBCoreConstants.NURSERY_THEME)
								&& isFieldDisplayedOnForm(registryAllInputList, BBBCoreConstants.NURSERY_THEME)
								&& !StringUtils.isEmpty(this.getRegistryVO().getEvent().getBabyNurseryTheme()))) {
					this.getRegistryVO().getEvent().setBabyNurseryTheme(
							BBBUtility.EncodeNurseryDecorTheme(this.getRegistryVO().getEvent().getBabyNurseryTheme()));
				}
				if (isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.BABY_NAME_REG_INPUTFIELD)
						|| (!isFieldRequiresValidation(registryAllInputList,
								BBBGiftRegistryConstants.BABY_NAME_REG_INPUTFIELD)
								&& isFieldDisplayedOnForm(registryAllInputList,
										BBBGiftRegistryConstants.BABY_NAME_REG_INPUTFIELD)
								&& !StringUtils.isEmpty(this.getRegistryVO().getEvent().getBabyName()))) {
					this.validateBabyName(this.getRegistryVO().getEvent().getBabyName(),
							BBBGiftRegistryConstants.BABY_NAME, pRequest);
				}
				if (isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.EVENT_DATE)
						|| (!isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.EVENT_DATE)
								&& isFieldDisplayedOnForm(registryAllInputList, BBBGiftRegistryConstants.EVENT_DATE)
								&& !StringUtils.isEmpty(this.getRegistryVO().getEvent().getEventDate()))) {
					this.validateEventDate(this.getRegistryVO().getEvent().getEventDate(),
							BBBGiftRegistryConstants.EVENT_DATE, dateFormat, pRequest);
				}
				if (isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.SHOWER_DATE_REG_INPUTFIELD)
						|| (!isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.SHOWER_DATE)
								&& isFieldDisplayedOnForm(registryAllInputList, BBBGiftRegistryConstants.SHOWER_DATE)
								&& !StringUtils.isEmpty(this.getRegistryVO().getEvent().getShowerDate()))) {
					this.validateShowerDate(this.getRegistryVO().getEvent().getShowerDate(),
							BBBGiftRegistryConstants.SHOWER_DATE, dateFormat, pRequest);
				}
			} else if (pEventType.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_COLLEGE)) {
				if (isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.EVENT_DATE)
						|| (!isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.EVENT_DATE)
								&& isFieldDisplayedOnForm(registryAllInputList, BBBGiftRegistryConstants.EVENT_DATE)
								&& !StringUtils.isEmpty(this.getRegistryVO().getEvent().getEventDate()))) {
					this.validateEventDate(this.getRegistryVO().getEvent().getEventDate(),
							BBBGiftRegistryConstants.EVENT_DATE, dateFormat, pRequest);
				}
				this.validateCollege(this.getRegistryVO().getEvent().getCollege(),
						BBBGiftRegistryConstants.EVENT_TYPE_COLLEGE, pRequest);
			} else{
				if (isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.EVENT_DATE)
						|| (!isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.EVENT_DATE)
								&& isFieldDisplayedOnForm(registryAllInputList, BBBGiftRegistryConstants.EVENT_DATE)
								&& !StringUtils.isEmpty(this.getRegistryVO().getEvent().getEventDate()))) {
					this.validateEventDate(this.getRegistryVO().getEvent().getEventDate(),
							BBBGiftRegistryConstants.EVENT_DATE, dateFormat, pRequest);
				}
			}
		}

		this.logDebug("GiftRegistryFormHandler.eventFormValidation() method ends");

	}
	
	/**
	 * 
	 * @param babyName
	 * @param pFieldName
	 * @param pRequest
	 */
	private void validateBabyName(final String babyName, final String pFieldName,
			final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.validateBabyName() method start");

		if (!StringUtils.isEmpty(babyName)) {

			if (!BBBUtility.isStringPatternValid(BBBCoreConstants.BABY_NAME_REGULAR_EXP, babyName)) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"Invalid baby name from validateBabyName of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1034));
				this.addFormException(new DropletFormException(BBBCoreConstants.ERR_CREATE_REG_BABYNAME_INVALID,
						this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR + pFieldName,
						BBBCoreConstants.ERR_CREATE_REG_BABYNAME_INVALID));

			} else {
				if (!BBBUtility.isStringLengthValid(babyName, BBBCoreConstants.ONE, BBBCoreConstants.THIRTY)) {
					this.addFormException(new DropletFormException(BBBCoreConstants.ERR_CREATE_REG_BABYNAME_INVALID,
							this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR + pFieldName,
							BBBCoreConstants.ERR_CREATE_REG_BABYNAME_INVALID));
				}
			}

		}

		this.logDebug("GiftRegistryFormHandler.validateBabyName() method ends");
	}

	/**
	 * baby name validator.
	 *
	 * @param college
	 *            the college
	 * @param pFieldName
	 *            the field name
	 */
	private void validateCollege(final String college, final String pFieldName,
			final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.validateCollege() method start");

		if (!StringUtils.isEmpty(college)) {

			if (BBBUtility.isStringPatternValid(BBBCoreConstants.ALPHANUMERIC_WITH_SPECIAL_CHAR, college)) {
				if (!BBBUtility.isStringLengthValid(college, BBBCoreConstants.ONE, BBBCoreConstants.HUNDRED)) {

					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"GiftRegistry Invalid college name length from validateCollege of GiftRegistryFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1038));

					this.addFormException(new DropletFormException("err_create_reg_college_invalid_length",
							this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR + pFieldName,
							"err_create_reg_college_invalid_length"));
				}

			} else {

				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"GiftRegistry Invalid college name from validateCollege of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1037));

				this.addFormException(new DropletFormException("err_create_reg_college_invalid",
						this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR + pFieldName,
						"err_create_reg_college_invalid"));

			}

		}

		this.logDebug("GiftRegistryFormHandler.validateCollege() method ends");
	}

	/**
	 * Event Date validator.
	 *
	 * @param eventDate
	 *            the event date
	 * @param pFieldName
	 *            the field name
	 * @param dateFormat
	 *            the date format
	 */
	private void validateEventDate(final String pEventDate, final String pFieldName, final String dateFormat,
			final DynamoHttpServletRequest pRequest) {
		this.logDebug("GiftRegistryFormHandler.validateEventDate() method starts");
		if (StringUtils.isEmpty(pEventDate)) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry empty event date from validateEventDate of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1039));

			this.addFormException(new DropletFormException("err_create_reg_event_date_empty",
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR + pFieldName,
					"err_create_reg_event_date_empty"));

		} else {

			if (!isValidDate(pEventDate, dateFormat)) {

				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"GiftRegistry Invalid event date from validateEventDate of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1040));

				this.addFormException(new DropletFormException("err_create_reg_event_date_invalid",
						this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR + pFieldName,
						"err_create_reg_event_date_invalid"));
			}

		}
		this.logDebug("GiftRegistryFormHandler.validateEventDate() method ends");
	}

	private void validateNetworkAffiliation(final String networkAff, final String pFieldName,
			final DynamoHttpServletRequest pRequest) {
		this.logDebug("GiftRegistryFormHandler.validateNetworkAffiliation() method starts");
		if (StringUtils.isEmpty(networkAff)) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry empty event date from validateNetworkAffiliation of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_20134));

			this.addFormException(new DropletFormException("err_networkAffiliation_empty",
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR + pFieldName,
					"err_networkAffiliation_empty"));

		} else {

			if (!(networkAff.equalsIgnoreCase("Y") || networkAff.equalsIgnoreCase("N"))) {

				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"GiftRegistry Invalid networkaffiliation flag GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1040));

				this.addFormException(new DropletFormException("err_networkAffiliation_invalid",
						this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR + pFieldName,
						"err_networkAffiliation_invalid"));
			}

		}
		this.logDebug("GiftRegistryFormHandler.networkAffiliation() method ends");
	}

	/**
	 * Validate shower date.
	 *
	 * @param showerDate
	 *            the shower date
	 * @param pFieldName
	 *            the field name
	 * @param dateFormat
	 *            the date format
	 */
	private void validateShowerDate(final String showerDate, final String pFieldName, final String dateFormat,
			final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.validateShowerDate() method starts");

		if (!StringUtils.isEmpty(showerDate) && !isValidDate(showerDate, dateFormat)) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry Invalid shower date from validateShowerDate of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1046));

			this.addFormException(new DropletFormException("err_create_reg_shower_date_invalid",
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR + pFieldName,
					"err_create_reg_shower_date_invalid"));
		}
		this.logDebug("GiftRegistryFormHandler.validateShowerDate() method ends");

	}

	/**
	 * Checking valid date format.
	 *
	 * @param dateStr
	 *            the date str
	 * @param formatStr
	 *            the format str
	 * @return boolean
	 */

	public static boolean isValidDate(final String dateStr, final String formatStr) {
		if (formatStr == null) {
			return false;
		}
		final SimpleDateFormat df = new SimpleDateFormat(formatStr);
		Date testDate = null;
		try {
			testDate = df.parse(dateStr);
		} catch (final ParseException e) {
			// invalid date format
			return false;
		}

		final Calendar pastDate = Calendar.getInstance();
		final Calendar futureDate = Calendar.getInstance();
		pastDate.set(Calendar.YEAR, pastDate.get(Calendar.YEAR) - 2);
		pastDate.set(Calendar.HOUR_OF_DAY, 0);
		pastDate.set(Calendar.MINUTE, 0);
		pastDate.set(Calendar.SECOND, 0);
		pastDate.set(Calendar.MILLISECOND, 0);

		futureDate.set(Calendar.YEAR, pastDate.get(Calendar.YEAR) + 7);
		futureDate.set(Calendar.HOUR_OF_DAY, 0);
		futureDate.set(Calendar.MINUTE, 0);
		futureDate.set(Calendar.SECOND, 0);
		futureDate.set(Calendar.MILLISECOND, 0);
		if (pastDate.getTime().after(testDate) || futureDate.getTime().before(testDate)) {
			return false;
		}

		if (!df.format(testDate).equals(dateStr)) {
			return false;
		}
		return true;
	}

	/**
	 * no of guest validation.
	 *
	 * @param noOfGuest
	 *            the no of guest
	 * @param pFieldName
	 *            the field name
	 */
	private void validateNumberOfGuest(final String noOfGuest, final String pFieldName,
			final DynamoHttpServletRequest pRequest) {
		this.logDebug("GiftRegistryFormHandler.validateNumberOfGuest() method starts");

		if (StringUtils.isEmpty(noOfGuest)) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry number of guests empty from validateNumberOfGuest of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1042));

			this.addFormException(new DropletFormException("err_create_reg_noofguest_empty",
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR + pFieldName,
					"err_create_reg_noofguest_empty"));
		} else if (!BBBUtility.isStringPatternValid(BBBCoreConstants.NUMERIC_FIELD_ONLY, String.valueOf(noOfGuest))) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry invalid format of num of guests from validateNumberOfGuest of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1043));

			this.addFormException(new DropletFormException("err_create_reg_noofguest_invalid",
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR + pFieldName,
					"err_create_reg_noofguest_invalid"));
		}
		this.logDebug("GiftRegistryFormHandler.validateNumberOfGuest() method ends");

	}

	/**
	 * This method is used to validate Registrant fields in registrant page
	 * during registry creation process.
	 *
	 * @param eventType1
	 *            the event type
	 */
	private void registrantValidationUpdateSimplified(final String pEvent, final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.registrantValidation() method start");
		// VALIDATE REGISTRANT FIRST NAME
		if (BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getFirstName())
				|| !BBBUtility.isValidFirstName(this.getRegistryVO().getPrimaryRegistrant().getFirstName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_FIRST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
		}

		// VALIDATE REGISTRANT LAST NAME
		if (BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getLastName())
				|| !BBBUtility.isValidFirstName(this.getRegistryVO().getPrimaryRegistrant().getLastName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_LAST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

		}

		// VALIDATE REGISTRANT PHONE NUMBER
		if (!BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone())
				&& !BBBUtility.isValidPhoneNumber(this.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest, INVALID_PHONENO_FROM_VALIDATECOLLEGE,
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1044));

			this.addFormException(new DropletFormException(
					"err_create_reg_phone_number_is_invalid", this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR + BBBGiftRegistryConstants.REG_PHONE_NUMBER,
					"err_create_reg_phone_number_is_invalid"));
		}

		// VALIDATE REGISTRANT CELL NUMBER
		if (!BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getCellPhone())
				&& !BBBUtility.isValidPhoneNumber(this.getRegistryVO().getPrimaryRegistrant().getCellPhone())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					BBBCoreConstants.INVALID_CELLPHNO_FROM_VALIDATECOLLEGE,
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1044));

			this.addFormException(new DropletFormException(
					"err_create_reg_phone_number_is_invalid", this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR + BBBGiftRegistryConstants.REG_MOBILE_NUMBER,
					"err_create_reg_phone_number_is_invalid"));
		}

		if (!StringUtils.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getContactAddress().getAddressLine1())) {
			this.registrantAddressValidation(pRequest);
		}
		this.logDebug("GiftRegistryFormHandler.registrantValidation() method ends");
	}

	/**
	 * This method is used to validate Registrant fields in registrant page
	 * during registry creation process.
	 *
	 * @param eventType1
	 *            the event type
	 */
	private void registrantValidation(final String pEvent, final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.registrantValidation() method start");
		// VALIDATE REGISTRANT FIRST NAME
		if (BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getFirstName())
				|| !BBBUtility.isValidFirstName(this.getRegistryVO().getPrimaryRegistrant().getFirstName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_FIRST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
		}

		// VALIDATE REGISTRANT LAST NAME
		if (BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getLastName())
				|| !BBBUtility.isValidFirstName(this.getRegistryVO().getPrimaryRegistrant().getLastName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_LAST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

		}

		// VALIDATE REGISTRANT PHONE NUMBER
		if (!BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone())
				&& !BBBUtility.isValidPhoneNumber(this.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest, INVALID_PHONENO_FROM_VALIDATECOLLEGE,
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1044));

			this.addFormException(new DropletFormException(
					"err_create_reg_phone_number_is_invalid", this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR + BBBGiftRegistryConstants.REG_PHONE_NUMBER,
					"err_create_reg_phone_number_is_invalid"));
		}

		// VALIDATE REGISTRANT CELL NUMBER
		if (!BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getCellPhone())
				&& !BBBUtility.isValidPhoneNumber(this.getRegistryVO().getPrimaryRegistrant().getCellPhone())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					BBBCoreConstants.INVALID_CELLPHNO_FROM_VALIDATECOLLEGE,
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1044));

			this.addFormException(new DropletFormException(
					"err_create_reg_phone_number_is_invalid", this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR + BBBGiftRegistryConstants.REG_MOBILE_NUMBER,
					"err_create_reg_phone_number_is_invalid"));
		}

		this.registrantAddressValidation(pRequest);
		this.logDebug("GiftRegistryFormHandler.registrantValidation() method ends");
	}

	/**
	 * Validation method for primary registrant details.
	 * 
	 * @param pRequest
	 * @param registryInputList
	 *            List of all RegistryInputListVO's based on eventType
	 * @param registryAllInputList
	 *            List of mandatory RegistryInputListVO's for create registry
	 */
	private void registrantValidationSimplified(final DynamoHttpServletRequest pRequest,
			List<RegistryInputVO> registryAllInputList) {

		this.logDebug("GiftRegistryFormHandler.registrantValidation() method start");
		// VALIDATE REGISTRANT FIRST NAME
		if (BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getFirstName())
				|| !BBBUtility.isValidFirstName(this.getRegistryVO().getPrimaryRegistrant().getFirstName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_FIRST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
		}

		// VALIDATE REGISTRANT LAST NAME
		if (BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getLastName())
				|| !BBBUtility.isValidFirstName(this.getRegistryVO().getPrimaryRegistrant().getLastName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_LAST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

		}

		// VALIDATE REGISTRANT PHONE NUMBER
		if ((isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.PHONE_NUMBER_REG_INPUTFIELD)
				|| (!isFieldRequiresValidation(registryAllInputList,
						BBBGiftRegistryConstants.PHONE_NUMBER_REG_INPUTFIELD)
						&& isFieldDisplayedOnForm(registryAllInputList,
								BBBGiftRegistryConstants.PHONE_NUMBER_REG_INPUTFIELD)
						&& !StringUtils.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone())))
						&& (!BBBUtility.isValidPhoneNumber(this.getRegistryVO().getPrimaryRegistrant().getPrimaryPhone()))) {

				this.logError(LogMessageFormatter.formatMessage(pRequest, INVALID_PHONENO_FROM_VALIDATECOLLEGE,
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1044));

				this.addFormException(new DropletFormException(
						"err_create_reg_phone_number_is_invalid", this.getAbsoluteName()
								+ BBBGiftRegistryConstants.DOT_SEPARATOR + BBBGiftRegistryConstants.REG_PHONE_NUMBER,
						"err_create_reg_phone_number_is_invalid"));
		}

		// VALIDATE REGISTRANT CELL NUMBER
		if ((isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.MOBILE_NUMBER_REG_INPUTFIELD)
				|| (!isFieldRequiresValidation(registryAllInputList,
						BBBGiftRegistryConstants.MOBILE_NUMBER_REG_INPUTFIELD)
						&& isFieldDisplayedOnForm(registryAllInputList,
								BBBGiftRegistryConstants.MOBILE_NUMBER_REG_INPUTFIELD)
						&& !StringUtils.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getCellPhone())))
						&& (!BBBUtility.isValidPhoneNumber(this.getRegistryVO().getPrimaryRegistrant().getCellPhone()))) {

				this.logError(LogMessageFormatter.formatMessage(pRequest,
						BBBCoreConstants.INVALID_CELLPHNO_FROM_VALIDATECOLLEGE,
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1044));

				this.addFormException(new DropletFormException("err_create_reg_phone_number_is_invalid",
						this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
								+ BBBGiftRegistryConstants.REG_MOBILE_NUMBER,
						"err_create_reg_phone_number_is_invalid"));			
		}

		// VALIDATE ADDRESS ONLY IF ADDRESS LINE1 has been entered by the user
		
		if (isFieldRequiresValidation(registryAllInputList, BBBCoreConstants.SHOW_CONTACT_ADDRESS)
				|| (!isFieldRequiresValidation(registryAllInputList, BBBCoreConstants.SHOW_CONTACT_ADDRESS)
						&& isFieldDisplayedOnForm(registryAllInputList, BBBCoreConstants.SHOW_CONTACT_ADDRESS) && !StringUtils.isEmpty(
								this.getRegistryVO().getPrimaryRegistrant().getContactAddress().getAddressLine1()))) {
			this.registrantAddressValidation(pRequest);
		}


		this.logDebug("GiftRegistryFormHandler.registrantValidation() method ends");
	}

	/**
	 * @param registryAllInputList
	 *            All registry input fields
	 * @param registryInputList
	 *            Only mandatory registry input fields
	 * @param fieldName
	 * @return true if field doesn't exist in BCC input fields list
	 */
	private boolean isFieldRequiresValidation(final List<RegistryInputVO> registryAllInputList,
			final String fieldName) {
		boolean validate = false;
		if (!registryAllInputList.isEmpty()) {
			for (RegistryInputVO registryInput : registryAllInputList) {
				if ((registryInput.getFieldName()).equalsIgnoreCase(fieldName)
						&& registryInput.isDisplayOnForm()
						&& registryInput.isRequiredInputCreate()) {
						validate = true;
						break;
				}
			}
		}
		return validate;
	}
	
	/**
	 * 
	 * @param registryAllInputList
	 * @param fieldName
	 * @return
	 */
	private boolean isFieldDisplayedOnForm(final List<RegistryInputVO> registryAllInputList, final String fieldName) {
		boolean validate = false;
		if (!registryAllInputList.isEmpty()) {
			for (RegistryInputVO registryInput : registryAllInputList) {
				if ((registryInput.getFieldName()).equalsIgnoreCase(fieldName) && registryInput.isDisplayOnForm()) {
						validate = true;
						break;
				}
			}
		}
		return validate;
	}

	/**
	 * This method is used to validate registrant address fields in create registry page.
	 *
	 */
	private void registrantAddressValidation(final DynamoHttpServletRequest pRequest) {
		this.logDebug("GiftRegistryFormHandler.registrantAddressValidation() method starts");

		// VALIDATE REGISTRANT ADDRESS LINE1
		if (!BBBUtility.isValidAddressLine1(
				this.getRegistryVO().getPrimaryRegistrant().getContactAddress().getAddressLine1())) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Invalid registraint AddressLine1 from registrantAddressValidation of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1032));
			this.addFormException(new DropletFormException(

					BBBGiftRegistryConstants.ERR_CREATE_REG_ADDRESS_LINE1_INVALID_CONTACT,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REG_CONTACT_ADDR_LINE1,
					BBBGiftRegistryConstants.ERR_CREATE_REG_ADDRESS_LINE1_INVALID_CONTACT));
		}

		// VALIDATE REGISTRANT ADDRESS LINE2
		if (!BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant().getContactAddress().getAddressLine2())
				&& !BBBUtility.isValidAddressLine2(
						this.getRegistryVO().getPrimaryRegistrant().getContactAddress().getAddressLine2())) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Invalid registraint AddressLine2 from registrantAddressValidation of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1033));
			this.addFormException(new DropletFormException(
					"err_create_reg_address_line2_invalid_contact", this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR + BBBGiftRegistryConstants.REG_CONTACT_ADDR_LINE2,
					"err_create_reg_address_line2_invalid_contact"));
		}

		// VALIDATE REGISTRANT ADDRESS CITY
		if (!BBBUtility.isValidCity(this.getRegistryVO().getPrimaryRegistrant().getContactAddress().getCity())) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Invalid registraint city from registrantAddressValidation of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1035));
			this.addFormException(new DropletFormException("err_create_reg_city_invalid_contact", this.getAbsoluteName()
					+ BBBGiftRegistryConstants.DOT_SEPARATOR + BBBGiftRegistryConstants.REG_CONTACT_ADDR_CITY,
					"err_create_reg_city_invalid_contact"));
		}
		if (!BBBUtility.isValidState((this.getRegistryVO().getPrimaryRegistrant().getContactAddress().getState()))) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Invalid registraint city from registrantAddressValidation of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1035));
			this.addFormException(new DropletFormException("err_create_reg_state_invalid_contact", this.getAbsoluteName()
					+ BBBGiftRegistryConstants.DOT_SEPARATOR + BBBGiftRegistryConstants.REG_CONTACT_ADDR_CITY,
					"err_create_reg_state_invalid_contact"));
		}

		// VALIDATE REGISTRANT ADDRESS ZIP
		if (!BBBUtility.isValidZip(this.getRegistryVO().getPrimaryRegistrant().getContactAddress().getZip())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry Invalid zip for registrant address from registrantAddressValidation of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1047));

			this.addFormException(new DropletFormException("err_create_reg_zip_invalid_contact", this.getAbsoluteName()
					+ BBBGiftRegistryConstants.DOT_SEPARATOR + BBBGiftRegistryConstants.REG_CONTACT_ADDR_ZIP,
					"err_create_reg_zip_invalid_contact"));
		}

		this.logDebug("GiftRegistryFormHandler.registrantAddressValidation() method ends");

		final String siteId = this.getCurrentSiteId();
		if ((null != siteId)
				&& (null != this.getRegistryVO().getPrimaryRegistrant().getContactAddress().getAddressLine1())) {
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				this.getRegistryVO().getPrimaryRegistrant().getContactAddress().setCountry(BBBGiftRegistryConstants.CA);
			} else {
				this.getRegistryVO().getPrimaryRegistrant().getContactAddress().setCountry(BBBGiftRegistryConstants.US);
			}
		}
	}

	/**
	 * This method is used to validate CoRegistrant fields in registrant page
	 * during registry creation process.
	 *
	 * @param pEventType
	 *            the event type
	 */
	private void coregistrantValidation(final String pEventType, final DynamoHttpServletRequest pRequest) {

		this.logDebug("GiftRegistryFormHandler.coregistrantValidation() method start");

		// VALIDATE COREGISTRANT FIRST NAME
		if (!BBBUtility.isValidFirstName(this.getRegistryVO().getCoRegistrant().getFirstName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_FIRST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
		}

		// VALIDATE COREGISTRANT LAST NAME
		if (!BBBUtility.isValidLastName(this.getRegistryVO().getCoRegistrant().getLastName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_LAST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

		}

		// VALIDATE COREGISTRANT EMAIL
		if (!BBBUtility.isEmpty(this.getRegistryVO().getCoRegistrant().getEmail())
				&& !BBBUtility.isValidEmail(this.getRegistryVO().getCoRegistrant().getEmail())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					BBBCoreConstants.GIFTREGISTRY_INVALID_CO_REGISTRANT_EMAIL,
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1041));

			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_INVALID_EMAIL_LENGTH,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_EMAIL,
					BBBGiftRegistryConstants.ERR_CREATE_REG_INVALID_EMAIL_LENGTH));
		}

		this.logDebug("GiftRegistryFormHandler.coregistrantValidation() method ends");
		final String siteId = this.getSiteContext().getSite().getId();
		if ((null != siteId) && (null != this.getRegistryVO().getCoRegistrant().getFirstName())) {
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				this.getRegistryVO().getCoRegistrant().getContactAddress().setCountry(BBBGiftRegistryConstants.CA);
			} else {
				this.getRegistryVO().getCoRegistrant().getContactAddress().setCountry(BBBGiftRegistryConstants.US);
			}
		}

	}

	/**
	 * This method is used to validate CoRegistrant fields in registrant page
	 * during registry creation process.
	 * 
	 * @param pEventType
	 *            the event type
	 * @param pRequest
	 * @param registryInputList
	 *            List of all RegistryInputListVO's based on eventType
	 * @param registryAllInputList
	 *            List of mandatory RegistryInputListVO's for create registry
	 */
	private void coregistrantCreateSimplifyValidation(final String pEventType, final DynamoHttpServletRequest pRequest,
			List<RegistryInputVO> registryAllInputList) {

		if (null != registryAllInputList) {
			// VALIDATE COREGISTRANT FIRST NAME
			if ((isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.COREG_FIRSTNAME_REG_INPUTFIELD)
					|| (!isFieldRequiresValidation(registryAllInputList,
							BBBGiftRegistryConstants.COREG_FIRSTNAME_REG_INPUTFIELD)
							&& isFieldDisplayedOnForm(registryAllInputList,
									BBBGiftRegistryConstants.COREG_FIRSTNAME_REG_INPUTFIELD)
							&& !StringUtils.isEmpty(this.getRegistryVO().getCoRegistrant().getFirstName())))
							&& (!BBBUtility.isValidFirstName(this.getRegistryVO().getCoRegistrant().getFirstName()))) {

					this.addFormException(
							new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_CO_REG_FIRST_NAME_INVALID,
									this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
											+ BBBGiftRegistryConstants.COREG_FIRST_NAME,
									BBBGiftRegistryConstants.ERR_CREATE_CO_REG_FIRST_NAME_INVALID));
			}
			// VALIDATE COREGISTRANT LAST NAME
			if ((isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.COREG_LASTNAME_REG_INPUTFIELD)
					|| (!isFieldRequiresValidation(registryAllInputList,
							BBBGiftRegistryConstants.COREG_LASTNAME_REG_INPUTFIELD)
							&& isFieldDisplayedOnForm(registryAllInputList,
									BBBGiftRegistryConstants.COREG_LASTNAME_REG_INPUTFIELD)
							&& !StringUtils.isEmpty(this.getRegistryVO().getCoRegistrant().getLastName())))
							&& (!BBBUtility.isValidLastName(this.getRegistryVO().getCoRegistrant().getLastName()))) {
				
					this.addFormException(
							new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_CO_REG_LAST_NAME_INVALID,
									this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
											+ BBBGiftRegistryConstants.COREG_LAST_NAME,
									BBBGiftRegistryConstants.ERR_CREATE_CO_REG_LAST_NAME_INVALID));

			}

			// VALIDATE COREGISTRANT EMAIL
			if ((isFieldRequiresValidation(registryAllInputList, BBBGiftRegistryConstants.COREG_EMAIL_REG_INPUTFIELD)
					|| (!isFieldRequiresValidation(registryAllInputList,
							BBBGiftRegistryConstants.COREG_EMAIL_REG_INPUTFIELD)
							&& isFieldDisplayedOnForm(registryAllInputList,
									BBBGiftRegistryConstants.COREG_EMAIL_REG_INPUTFIELD)
							&& !StringUtils.isEmpty(this.getRegistryVO().getCoRegistrant().getEmail())))
							&& (!BBBUtility.isEmpty(this.getRegistryVO().getCoRegistrant().getEmail())
									&& !BBBUtility.isValidEmail(this.getRegistryVO().getCoRegistrant().getEmail()))) {

					this.logError(LogMessageFormatter.formatMessage(pRequest,
							BBBCoreConstants.GIFTREGISTRY_INVALID_CO_REGISTRANT_EMAIL,
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1041));

					this.addFormException(
							new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_INVALID_EMAIL_LENGTH,
									this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
											+ BBBGiftRegistryConstants.COREG_EMAIL,
									BBBGiftRegistryConstants.ERR_CREATE_REG_INVALID_EMAIL_LENGTH));
			}
		}

		this.logDebug("GiftRegistryFormHandler.coregistrantValidation() method ends");
		final String siteId = this.getSiteContext().getSite().getId();
		if ((null != siteId) && (null != this.getRegistryVO().getCoRegistrant().getFirstName())) {
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				this.getRegistryVO().getCoRegistrant().getContactAddress().setCountry(BBBGiftRegistryConstants.CA);
			} else {
				this.getRegistryVO().getCoRegistrant().getContactAddress().setCountry(BBBGiftRegistryConstants.US);
			}
		}

	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @return flag
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean handleRegistrySearchFromFlyout(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		this.logDebug("GiftRegistryFormHandler.handleRegistrySearchfromFlyout() method starts");
		this.setRegistrySearchErrorURL(this.getRegistryFlyoutURL());
		this.logDebug("GiftRegistryFormHandler.handleRegistrySearchfromFlyout() method ends");
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
		if (getSessionBean() != null) {
			final Map sessionMap = getSessionBean().getValues();
			if (sessionMap != null) {
				RegistrySummaryVO pRegSummaryVO = (RegistrySummaryVO) sessionMap
						.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
				if (pRegSummaryVO != null) {
					RegistryTypes regType = pRegSummaryVO.getRegistryType();
					if (regType != null) {
						giftRegistryViewBean.setRegistryName(regType.getRegistryTypeDesc());
						getSessionBean().setGiftRegistryViewBean(giftRegistryViewBean);
					}
				}
			}
		}
		return this.handleRegistrySearch(pRequest, pResponse);
	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @return flag
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean handleRegistrySearchFromBridalLanding(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		this.logDebug("GiftRegistryFormHandler.handleRegistrySearchfromBridalLanding() method starts");
		this.setRegistrySearchErrorURL(this.getBridalLandingURL());
		this.logDebug("GiftRegistryFormHandler.handleRegistrySearchfromBridalLanding() method ends");
		return this.handleRegistrySearch(pRequest, pResponse);
	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @return flag
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean handleRegistrySearchFromBabyLanding(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		this.logDebug("GiftRegistryFormHandler.handleRegistrySearchfromBabyLanding() method starts");
		this.setRegistrySearchErrorURL(this.getBabyLandingURL());
		this.logDebug("GiftRegistryFormHandler.handleRegistrySearchfromBabyLanding() method ends");
		return this.handleRegistrySearch(pRequest, pResponse);
	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @return flag
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean handleRegistrySearchFromNoSearchResultsPage(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		this.logDebug("GiftRegistryFormHandler.handleRegistrySearchFromNoSearchResultsPage() method starts");
		this.setRegistrySearchErrorURL(this.getNoSearchResultURL());
		this.logDebug("GiftRegistryFormHandler.handleRegistrySearchFromNoSearchResultsPage() method ends");
		return this.handleRegistrySearch(pRequest, pResponse);
	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @return flag
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean handleRegistrySearchFromImportRegistryPage(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		this.logDebug("GiftRegistryFormHandler.handleRegistrySearchFromImportRegistryPage() method starts");
		this.setRegistrySearchSuccessURL(this.getRegistryImportSearchSuccessURL());
		this.setRegistrySearchErrorURL(this.getRegistryImportSearchErrorURL());
		this.logDebug("GiftRegistryFormHandler.handleRegistrySearchFromImportRegistryPage() method ends");
		return this.handleRegistrySearch(pRequest, pResponse);
	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @return flag
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean handleRegistrySearchFromHomePage(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		this.logDebug("GiftRegistryFormHandler.handleRegistrySearchFromHomePage() method starts");
		this.setRegistrySearchErrorURL(this.getHomePageURL());
		this.logDebug("GiftRegistryFormHandler.handleRegistrySearchFromHomePage() method ends");
		return this.handleRegistrySearch(pRequest, pResponse);
	}

	/**
	 * Calls preRegistrySearch for input validations. Creates RegistrySearchVO
	 * object with search parameters and calls GiftRegistryManager method
	 * searchRegistry
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return flag
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */

	public boolean handleRegistrySearch(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		this.logDebug("GiftRegistryFormHandler.handleRegistrySearch() method starts");

		try {
			final String siteId = this.getSiteContext().getSite().getId();
			// BBBH-391 | Client DOM XSRF changes
			if (StringUtils.isNotEmpty(getFromPage())) {
				StringBuilder appendData = new StringBuilder(BBBCoreConstants.BLANK);
				if(StringUtils.isNotEmpty(getQueryParam())){
					appendData.append(BBBCoreConstants.QUESTION_MARK).append(getQueryParam());
				}
				StringBuilder successURL = new StringBuilder(BBBCoreConstants.BLANK);
				StringBuilder errorURL = new StringBuilder(BBBCoreConstants.BLANK);
				successURL
						.append(pRequest.getContextPath())
						.append(getSuccessUrlMap().get(getFromPage()))
						.append(appendData);
				errorURL.append(pRequest.getContextPath())
						.append(getErrorUrlMap().get(getFromPage()))
						.append(appendData);

				setRegistrySearchSuccessURL(successURL.toString());
				setRegistrySearchErrorURL(errorURL.toString());
			}

			// Find old registries block

			if (this.getHidden() == 2) {
				final String excludedRegNums = getGiftRegistryManager().getCommaSaperatedRegistryIds(this.getProfile(),
						siteId);
				if (!StringUtils.isEmpty(excludedRegNums)) {
					getRegistrySearchVO().setExcludedRegNums(excludedRegNums);
				}
				getRegistrySearchVO().setReturnLeagacyRegistries(true);
				getRegistrySearchVO().setProfileId(this.getProfile());
				getRegistrySearchVO().setGiftGiver(false);

			} else {
				getRegistrySearchVO().setGiftGiver(true);
			}
			this.getGiftRegSessionBean().setRequestVO(null);
			this.preRegistrySearch(pRequest, pResponse);

			if (!this.getFormError()) {

				getRegistrySearchVO().setProfileId(this.getProfile());
				if (!this.getProfile().isTransient()) {
					getRegistrySearchVO().setFilterRegistriesInProfile(true);
				}

				getRegistrySearchVO().setSiteId(getBbbCatalogTools()
						.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
				getRegistrySearchVO()
						.setUserToken(getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
				getRegistrySearchVO().setServiceName(this.getSearchRegistryServiceName());

				this.logDebug("siteFlag: " + this.getRegistryVO().getSiteId());
				this.logDebug("userToken: " + this.getRegistryVO().getUserToken());
				this.logDebug("ServiceName: " + this.getCreateRegistryServiceName());

				this.getGiftRegSessionBean().setRequestVO(getRegistrySearchVO());

				GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
				if (getSessionBean() != null) {
					final Map sessionMap = getSessionBean().getValues();
					if (sessionMap != null) {
						RegistrySummaryVO pRegSummaryVO = (RegistrySummaryVO) sessionMap
								.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
						if (pRegSummaryVO != null) {
							RegistryTypes regType = pRegSummaryVO.getRegistryType();
							if (regType != null) {
								giftRegistryViewBean.setRegistryName(regType.getRegistryTypeDesc());
								getSessionBean().setGiftRegistryViewBean(giftRegistryViewBean);
							}
						}
					}
				}
			}
			this.logDebug("GiftRegistryFormHandler.handleRegistrySearch() method ends");

			return this.checkFormRedirect(this.getRegistrySearchSuccessURL(), this.getRegistrySearchErrorURL(),
					pRequest, pResponse);

		} catch (final BBBBusinessException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBBusinessException from handleRegistrySearch of GiftRegistryformHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);

			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION));

			return this.checkFormRedirect(this.getRegistrySearchSuccessURL(), this.getRegistrySearchErrorURL(),
					pRequest, pResponse);
		} catch (final BBBSystemException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBSystemException from handleRegistrySearch of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION));

			return this.checkFormRedirect(this.getRegistrySearchSuccessURL(), this.getRegistrySearchErrorURL(),
					pRequest, pResponse);
		}

	}

	/**
	 * registry search validation.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public void preRegistrySearch(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws BBBBusinessException, BBBSystemException {
		this.logDebug("GiftRegistryFormHandler.preRegistrySearch() method starts");

		try {
			if ((this.getHidden() == 1) || (this.getHidden() == 4) || (this.getHidden() == 5)) {

				this.validateFirstName();

				if (("true").equals(pRequest.getParameter(BBBCoreConstants.FROM_MX))) {
					this.validateMxLastName();
				} else {
					this.validateLastName();
				}

			} else if (this.getHidden() == 2) {

				this.searchLegacyRegistry(pRequest);

			} else if (this.getHidden() == 3) {
				if ((!StringUtils.isEmpty(getRegistrySearchVO().getFirstName())
						|| !StringUtils.isEmpty(this.getRegistrySearchVO().getLastName()))
						&& !StringUtils.isEmpty(getRegistrySearchVO().getRegistryId())) {
					this.addFormException(
							new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
									this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
											+ BBBGiftRegistryConstants.REG_SRCH_FIRST_LAST_REGID,
									BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
				} else if ((StringUtils.isEmpty(getRegistrySearchVO().getFirstName())
						|| StringUtils.isEmpty(this.getRegistrySearchVO().getLastName()))
						&& StringUtils.isEmpty(getRegistrySearchVO().getRegistryId())) {
					this.addFormException(
							new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
									this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
											+ BBBGiftRegistryConstants.REG_SRCH_FIRST_LAST_REGID_EMPTY,
									BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));
				} else if (!StringUtils.isEmpty(getRegistrySearchVO().getFirstName())
						|| !StringUtils.isEmpty(getRegistrySearchVO().getLastName())) {
					this.validateFirstName();

					if (("true").equals(pRequest.getParameter(BBBCoreConstants.FROM_MX))) {
						this.validateMxLastName();

					} else {
						this.validateLastName();
					}
				} else if (!StringUtils.isEmpty(getRegistrySearchVO().getRegistryId())
						&& !BBBUtility.isValidateRegistryId(this.getRegistrySearchVO().getRegistryId())) {

					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"GiftRegistry Invalid registry id from preRegistrySearch of GiftRegistryFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1045));

					this.addFormException(
							new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_REGISTRY_ID_INVALID,
									this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
											+ BBBGiftRegistryConstants.REGISTRY_SEARCH_REGID,
									BBBGiftRegistryConstants.ERR_CREATE_REG_REGISTRY_ID_INVALID));
				}
			} else if (this.getHidden() == BBBGiftRegistryConstants.TBS_REGISTRY_SEARCH) {
				if(!BBBUtility.isEmpty(this.getRegistrySearchVO().getFirstName())){
					this.getRegistrySearchVO().setFirstName(this.getRegistrySearchVO().getFirstName().trim());
			}
				if(!BBBUtility.isEmpty(this.getRegistrySearchVO().getLastName())){
					this.getRegistrySearchVO().setLastName(this.getRegistrySearchVO().getLastName().trim());
				}
				if(!BBBUtility.isEmpty(this.getRegistrySearchVO().getRegistryId())){
					this.getRegistrySearchVO().setRegistryId(this.getRegistrySearchVO().getRegistryId().trim());
				}
				if(BBBUtility.isEmpty(this.getRegistrySearchVO().getRegistryId())&&(BBBUtility.isEmpty(this.getRegistrySearchVO().getFirstName())||BBBUtility.isEmpty(this.getRegistrySearchVO().getLastName()))){
					//add exception
					this.addFormException(
							new DropletFormException(BBBGiftRegistryConstants.ERR_REGSEARCH_ALL_FILLED,
									this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
											+ BBBGiftRegistryConstants.REG_SRCH_PRE,
									BBBGiftRegistryConstants.ERR_REGSEARCH_ALL_FILLED));
				}
				else if(!BBBUtility.isEmpty(this.getRegistrySearchVO().getRegistryId())&&(!BBBUtility.isEmpty(this.getRegistrySearchVO().getFirstName())||!BBBUtility.isEmpty(this.getRegistrySearchVO().getLastName()))){
					//add exception with new label "Enter First and Last name or Registry ID" | err_regsearch_all_filled
					this.addFormException(
							new DropletFormException(BBBGiftRegistryConstants.ERR_REGSEARCH_ALL_FILLED,
									this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
											+ BBBGiftRegistryConstants.REG_SRCH_PRE,
									BBBGiftRegistryConstants.ERR_REGSEARCH_ALL_FILLED));
				}
				else if(!BBBUtility.isEmpty(this.getRegistrySearchVO().getRegistryId())&&(BBBUtility.isEmpty(this.getRegistrySearchVO().getFirstName())&&BBBUtility.isEmpty(this.getRegistrySearchVO().getLastName()))){
					if(!BBBUtility.isValidateRegistryId(this.getRegistrySearchVO().getRegistryId())){
						//add exception with new label "Please enter a valid Registry ID" | err_regsearch_invalid_regid
						this.addFormException(
								new DropletFormException(BBBGiftRegistryConstants.ERR_REGSEARCH_INVALID_REGID,
										this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
												+ BBBGiftRegistryConstants.REG_SRCH_PRE,
										BBBGiftRegistryConstants.ERR_REGSEARCH_INVALID_REGID));
					}
				}
				else if(BBBUtility.isEmpty(this.getRegistrySearchVO().getRegistryId())&&(!BBBUtility.isEmpty(this.getRegistrySearchVO().getFirstName())&&!BBBUtility.isEmpty(this.getRegistrySearchVO().getLastName()))){
					this.validateFirstName();
					if (("true").equals(pRequest.getParameter(BBBCoreConstants.FROM_MX))) {
						this.validateMxLastName();
					} else {
						this.validateLastName();
					}
				}
			}
			this.logDebug("GiftRegistryFormHandler.preRegistrySearch() method ends");

		} catch (final NumberFormatException e) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry Invalid number from preRegistrySearch of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1053));

			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_NO_FORMAT_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_NO_FORMAT_EXCEPTION));
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					" err_no_format_exception from preRegistrySearch of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10100));
		}

	}

	/**
	 * Calls preRegistrySearch for input validations. Creates RegistrySearchVO
	 * object with search parameters and calls GiftRegistryManager method
	 * searchRegistry
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return flag
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */

	public boolean handleMxRegistrySearch(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		this.logDebug("GiftRegistryFormHandler.handleMxRegistrySearch() method starts");
		// BBBH-391 | Client DOM XSRF
		final String siteId = this.getSiteContext().getSite().getId();
		if (StringUtils.isNotEmpty(getFromPage())) {
			setRegistrySearchSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
			setRegistrySearchErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));
		}
		try {

			// Find old registries block

			if (this.getHidden() == 2) {
				final String excludedRegNums = getGiftRegistryManager().getCommaSaperatedRegistryIds(this.getProfile(),
						siteId);
				if (!StringUtils.isEmpty(excludedRegNums)) {
					getRegistrySearchVO().setExcludedRegNums(excludedRegNums);
				}
				getRegistrySearchVO().setReturnLeagacyRegistries(true);
				getRegistrySearchVO().setProfileId(this.getProfile());
				getRegistrySearchVO().setGiftGiver(false);

			} else {
				getRegistrySearchVO().setGiftGiver(true);
			}
			this.getGiftRegSessionBean().setRequestVO(null);
			pRequest.setParameter(BBBCoreConstants.FROM_MX, "true");
			this.preRegistrySearch(pRequest, pResponse);

			if (!this.getFormError()) {

				getRegistrySearchVO().setProfileId(this.getProfile());
				if (!this.getProfile().isTransient()) {
					getRegistrySearchVO().setFilterRegistriesInProfile(true);
				}

				getRegistrySearchVO().setSiteId("5");

				getRegistrySearchVO()
						.setUserToken(getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
				getRegistrySearchVO().setServiceName(this.getSearchRegistryServiceName());

				this.logDebug("siteFlag: " + this.getRegistryVO().getSiteId());
				this.logDebug("userToken: " + this.getRegistryVO().getUserToken());
				this.logDebug("ServiceName: " + this.getCreateRegistryServiceName());

				this.getGiftRegSessionBean().setRequestVO(getRegistrySearchVO());
			}
			this.logDebug("GiftRegistryFormHandler.handleRegistrySearch() method ends");

			return this.checkFormRedirect(this.getRegistrySearchSuccessURL(), this.getRegistrySearchErrorURL(),
					pRequest, pResponse);

		} catch (final BBBBusinessException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBBusinessException from handleRegistrySearch of GiftRegistryformHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);

			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION));

			return this.checkFormRedirect(this.getRegistrySearchSuccessURL(), this.getRegistrySearchErrorURL(),
					pRequest, pResponse);
		} catch (final BBBSystemException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBSystemException from handleRegistrySearch of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005), e);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION));

			return this.checkFormRedirect(this.getRegistrySearchSuccessURL(), this.getRegistrySearchErrorURL(),
					pRequest, pResponse);
		}

	}
	
	/**
	 * 
	 * @param pRequest
	 */
	private void searchLegacyRegistry(final DynamoHttpServletRequest pRequest) {
		if ((!StringUtils.isEmpty(getRegistrySearchVO().getFirstName())
				|| !StringUtils.isEmpty(getRegistrySearchVO().getLastName()))
				&& !StringUtils.isEmpty(getRegistrySearchVO().getEmail())
				&& !StringUtils.isEmpty(getRegistrySearchVO().getRegistryId())) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Either First name, Last name or Email or registry id should be used from searchLegacyRegistry of GiftRegistryFormhandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1062));
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_EMAIL_REGID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REG_SRCH_FIRST_LAST_EMAIL_REGID,
					BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_EMAIL_REGID));
		} else if ((StringUtils.isEmpty(getRegistrySearchVO().getFirstName())
				|| StringUtils.isEmpty(this.getRegistrySearchVO().getLastName()))
				&& StringUtils.isEmpty(getRegistrySearchVO().getEmail())
				&& StringUtils.isEmpty(getRegistrySearchVO().getRegistryId())) {
			this.addFormException(
					new DropletFormException(BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_EMAIL_REGID_EMPTY,
							this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
									+ BBBGiftRegistryConstants.REG_SRCH_FIRST_LAST_EMAIL_REGID_EMPTY,
							BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_EMAIL_REGID_EMPTY));
		} else if ((!StringUtils.isEmpty(getRegistrySearchVO().getFirstName())
				|| !StringUtils.isEmpty(this.getRegistrySearchVO().getLastName()))
				&& !StringUtils.isEmpty(getRegistrySearchVO().getEmail())) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Either First name, Last name or Email should be used from searchLegacyRegistry of GiftRegistryFormhandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1061));
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_EMAIL,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REG_SRCH_FIRST_LAST_EMAIL,
					BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_EMAIL));
		} else if ((!StringUtils.isEmpty(getRegistrySearchVO().getFirstName())
				|| !StringUtils.isEmpty(this.getRegistrySearchVO().getLastName()))
				&& !StringUtils.isEmpty(getRegistrySearchVO().getRegistryId())) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Either First name, Last name or registry id should be used from searchLegacyRegistry of GiftRegistryFormhandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1063));
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_REGID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REG_SRCH_FIRST_LAST_REGID,
					BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_REGID));
		} else if (!StringUtils.isEmpty(getRegistrySearchVO().getRegistryId())
				&& !StringUtils.isEmpty(getRegistrySearchVO().getEmail())) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Either email or registry id should be used from searchLegacyRegistry of GiftRegistryFormhandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1060));
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_SRCH_EMAIL_REGID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REG_SRCH_EMAIL_REGID,
					BBBGiftRegistryConstants.ERR_SRCH_EMAIL_REGID));
		} else if (!StringUtils.isEmpty(getRegistrySearchVO().getFirstName())
				|| !StringUtils.isEmpty(getRegistrySearchVO().getLastName())) {

			this.validateFirstName();

			this.validateLastName();
		} else if (!StringUtils.isEmpty(getRegistrySearchVO().getEmail())
				&& !BBBUtility.isValidEmail(getRegistrySearchVO().getEmail())) {

				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"GiftRegistry Invalid search email from searchLegacyRegistry of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1041));

				this.addFormException(
						new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_INVALID_EMAIL_LENGTH,
								this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
										+ BBBGiftRegistryConstants.COREG_EMAIL,
								BBBGiftRegistryConstants.ERR_CREATE_REG_INVALID_EMAIL_LENGTH));
			
		} else if (!StringUtils.isEmpty(getRegistrySearchVO().getRegistryId())
				&& !BBBUtility.isValidateRegistryId(getRegistrySearchVO().getRegistryId())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry Invalid registry id from preRegistrySearch of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1045));

			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_REGISTRY_ID_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REGISTRY_SEARCH_REGID,
					BBBGiftRegistryConstants.ERR_CREATE_REG_REGISTRY_ID_INVALID));
		}
	}

	/**
	 * Validate first name.
	 */
	private void validateFirstName() {
		this.logDebug("GiftRegistryFormHandler.validateFirstName() method starts");

		if (StringUtils.isEmpty(getRegistrySearchVO().getFirstName())
				|| !BBBUtility.isValidFirstName(getRegistrySearchVO().getFirstName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REGISTRY_SEARCH_FIRST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
		}
		this.logDebug("GiftRegistryFormHandler.validateFirstName() method ends");

	}

	/**
	 * Validate last name.
	 */
	private void validateLastName() {
		this.logDebug("GiftRegistryFormHandler.validateLastName() method starts");

		if (StringUtils.isEmpty(this.getRegistrySearchVO().getLastName())
				|| !BBBUtility.isValidLastName(this.getRegistrySearchVO().getLastName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REGISTRY_SEARCH_LAST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

		}
		this.logDebug("GiftRegistryFormHandler.validateLastName() method ends");

	}

	/**
	 * Validate Maxico last name.
	 */
	private void validateMxLastName() {
		this.logDebug("GiftRegistryFormHandler.validateMxLastName() method starts");

		if (StringUtils.isEmpty(this.getRegistrySearchVO().getLastName())
				|| !BBBUtility.isValidMxLastName(this.getRegistrySearchVO().getLastName())) {
			this.addFormException(new DropletFormException(BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REGISTRY_SEARCH_LAST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

		}
		this.logDebug("GiftRegistryFormHandler.validateMxLastName() method ends");

	}

	/**
	 * This method is used to remove the giftRegistryViewBean instance from
	 * session. In product detail page, we need the bean giftRegistryViewBean at
	 * the time of login redirect
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
	public void handleClearSessionBeanData(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		this.logDebug("GiftRegistryFormHandler.handleClearSessionBeanData() method starts");
		final Profile profile = (Profile) pRequest
				.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE));
		String kickStarterId = this.getSessionBean().getKickStarterId();
		String eventType = this.getSessionBean().getEventType();
		if (!profile.isTransient()) {
			this.getSessionBean().setGiftRegistryViewBean(null);
		}
		if (kickStarterId != null && eventType != null) {
			this.getSessionBean().setKickStarterId(kickStarterId);
			this.getSessionBean().setEventType(eventType);
		}
		this.logDebug("GiftRegistryFormHandler.handleClearSessionBeanData() method ends");

	}

	/**
	 * Handle add item to gift registry from cetona.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean handleAddItemToGiftRegistryFromCetona(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		this.logDebug("GiftRegistryFormHandler.handleAddItemToGiftRegistryFromCetona() method starts");
		EximSummaryResponseVO eximSummaryResponseVO = null;
		final GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
		final AddItemsBean addItemsBean = new AddItemsBean();
		final String siteId = this.getSiteContext().getSite().getId();
		final List<AddItemsBean> lstaddItems = new ArrayList<AddItemsBean>();
		this.populateRedirectUrls(pRequest);
		try {
			// add registry owner check
			final Profile profile = (Profile) pRequest
					.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE));
			if (!profile.isTransient()) {
				this.isRegistryOwnedByProfile(this.getRegistryId());
			}

			giftRegistryViewBean.setSiteFlag(this.getBbbCatalogTools()
					.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
			giftRegistryViewBean.setUserToken(this.getBbbCatalogTools()
					.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
					.get(0));
			giftRegistryViewBean.setServiceName(this.getAddItemsToReg2ServiceName());
			
			this.setAddItemsBean(giftRegistryViewBean, addItemsBean, siteId);

			this.logDebug("PersonalizationCode: " + getPersonalizationCode() + " RefNum: " + getRefNum());
			lstaddItems.add(addItemsBean);
			giftRegistryViewBean.setAdditem(lstaddItems);
			if (BBBUtility.isNotEmpty(getRefNum()) && BBBUtility.isNotEmpty(getPersonalizationCode())) {
				eximSummaryResponseVO = getEximPricingManager().invokeSummaryAPI(addItemsBean.getProductId(), null,
						getRefNum());
			}
			setGiftRegistryBean(this.getRefNum(), giftRegistryViewBean, eximSummaryResponseVO, getPersonalizationCode());

			this.logDebug("siteFlag: " + giftRegistryViewBean.getSiteFlag());
			this.logDebug("userToken: " + giftRegistryViewBean.getUserToken());
			this.logDebug("ServiceName: " + this.getAddItemsToRegServiceName());

			giftRegistryViewBean.setRegistrySize(giftRegistryViewBean.getAdditem().size());

			this.preAddItemToGiftRegistry(pRequest, pResponse, giftRegistryViewBean);
			final ValidateAddItemsResVO addItemsResVO = this.getGiftRegistryManager()
					.addItemToGiftRegistry(giftRegistryViewBean);

			if (!addItemsResVO.getServiceErrorVO().isErrorExists()) {
				this.setErrorFlagAddItemToRegistry(false);
				// Update session data so that it will be in sync with the
				// registred items on registry flyout.
				final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
						.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
				final Map sessionMap = sessionBean.getValues();
				final RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap
						.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
				if ((registrySummaryVO != null)
						&& registrySummaryVO.getRegistryId().equalsIgnoreCase(this.getRegistryId())) {
					int totalQuantity = 0;
					totalQuantity += Integer.parseInt(addItemsBean.getQuantity());

					// update quantiry in session
					totalQuantity = totalQuantity + registrySummaryVO.getGiftRegistered();
					registrySummaryVO.setGiftRegistered(totalQuantity);
				}
				
				
				sessionMap.put(this.getRegistryId()+REG_SUMMARY_KEY_CONST, null);
				getGiftRegistryManager().getGiftRegistryTools().invalidateRegistry(giftRegistryViewBean);
				
				this.setRegistryItemOperation(BBBGiftRegistryConstants.GR_ADD_ITEM_CERTONA);
				final String priceTotal = this.totalPrice(addItemsBean.getQuantity(), addItemsBean.getPrice());
				final String omniProductList = ";" + addItemsBean.getProductId() + ";;;event22="
						+ addItemsBean.getQuantity() + "|event23=" + priceTotal + ";eVar30=" + addItemsBean.getSku();

				this.setProductStringAddItemCertona(omniProductList);
				this.setItemAddedToRegistry(true);// for defect PS-17524
				return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
			}
			this.errorAdditemCertona(pRequest, addItemsResVO);
			this.setErrorURL(this.getSuccessURL());
			return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);

		} catch (final BBBBusinessException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBBusinessException from handleAddItemToGiftRegistryFromCetona of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);
			this.setErrorFlagAddItemToRegistry(true);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.BUSINESS_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.BUSINESS_EXCEPTION));

		} catch (final BBBSystemException es) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBBusinessException from handleAddItemToGiftRegistryFromCetona of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005), es);
			this.setErrorFlagAddItemToRegistry(true);
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.SYSTEM_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.SYSTEM_EXCEPTION));
		} catch (final NumberFormatException es) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"NumberFormatException from handleAddItemToGiftRegistryFromCetona of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10101), es);
			this.setErrorFlagAddItemToRegistry(true);
		}
		return this.isErrorFlagAddItemToRegistry();
	}

	/**
	 * @param giftRegistryViewBean
	 * @param addItemsBean
	 * @param siteId
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void setAddItemsBean(
			final GiftRegistryViewBean giftRegistryViewBean,
			final AddItemsBean addItemsBean, final String siteId)
			throws BBBSystemException, BBBBusinessException {
		
		addItemsBean.setProductId(this.getProductId());
		addItemsBean.setQuantity(this.getQuantity());
		addItemsBean.setRegistryId(this.getRegistryId());
		addItemsBean.setSku(this.getSkuIds());
		addItemsBean.setRefNum(this.getRefNum());
		boolean isSkuLtl = getBbbCatalogTools().isSkuLtl(siteId, this.getSkuIds());
		if (isSkuLtl) {
			this.setLtlFlag(BBBCoreConstants.TRUE);
			addItemsBean.setLtlDeliveryServices(
					this.getLtlDeliveryServices() == null ? BBBCoreConstants.BLANK : this.getLtlDeliveryServices());
			addItemsBean.setLtlDeliveryPrices(
					this.getLtlDeliveryPrices() == null ? BBBCoreConstants.BLANK : this.getLtlDeliveryPrices());
			if (!StringUtils.isEmpty(this.getLtlDeliveryServices())) {
				addItemsBean.setLtlDeliveryPrices(String.valueOf(getBbbCatalogTools().getDeliveryCharge(siteId,
						this.getSkuIds(), this.getLtlDeliveryServices()) * Double.valueOf(this.getQuantity())));
			}
		}
		addItemsBean.setPersonalizationCode(this.getPersonalizationCode());
		giftRegistryViewBean.setParentProductId(addItemsBean.getProductId());
		// set price
		final double listPrice = this.getBbbCatalogTools()
				.getListPrice(addItemsBean.getProductId(), addItemsBean.getSku()).doubleValue();
		final double salePrice = this.getBbbCatalogTools()
				.getSalePrice(addItemsBean.getProductId(), addItemsBean.getSku()).doubleValue();
		if ((salePrice > 0)) {
			addItemsBean.setPrice(Double.toString(salePrice));
		} else {
			addItemsBean.setPrice(Double.toString(listPrice));
		}
	}
	
	/**
	 * 
	 * @param pRequest
	 * @param addItemsResVO
	 */
	private void errorAdditemCertona(final DynamoHttpServletRequest pRequest,
			final ValidateAddItemsResVO addItemsResVO) {

		if (!BBBUtility.isEmpty(addItemsResVO.getServiceErrorVO().getErrorMessage()) && (addItemsResVO
				.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR))// Technical
		// Error
		{
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Fatal error from errorAdditemCertona of GiftRegistryFormHandler | webservice code ="
							+ addItemsResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10102));
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
		} else if (!BBBUtility.isEmpty(addItemsResVO.getServiceErrorVO().getErrorMessage())
				&& (addItemsResVO.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN))// Technical
		// Error
		{
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Either user token or site flag invalid from errorAdditemCertona of GiftRegistryFormHandler | webservice code ="
							+ addItemsResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10103));
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));
		} else if (!BBBUtility.isEmpty(addItemsResVO.getServiceErrorVO().getErrorMessage()) && (addItemsResVO
				.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT))// Technical
		// Error
		{
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry input fields format error from handleAddItemToGiftRegistry() of "
							+ WEB_SERVICE_ERROR_CODE + addItemsResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
		} else {
			this.addFormException(new DropletFormException(addItemsResVO.getServiceErrorVO().getErrorMessage(),
					this.getAbsoluteName() + BBBGiftRegistryConstants.DOT_SEPARATOR
							+ addItemsResVO.getServiceErrorVO().getErrorId(),
					addItemsResVO.getServiceErrorVO().getErrorMessage()));
			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.SYSTEM_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.SYSTEM_EXCEPTION));
		}
		this.setErrorFlagAddItemToRegistry(true);

	}

	/**
	 * adding all items to the gift registry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */

	public boolean handleAddAllItemsToGiftRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {

		BBBPerformanceMonitor
				.start(GiftRegistryFormHandler.class.getName() + " : " + "handleAddAllItemsToGiftRegistry");
		logDebug("Start.handleAddAllItemsToGiftRegistry");

		this.getSessionBean().setAddALLActgion(BBBGiftRegistryConstants.ADD_ALL_ITEMS);
		this.setKickStarterAddAllAction(BBBGiftRegistryConstants.ADD_ALL_ITEMS);
		this.handleAddItemToGiftRegistry(pRequest, pResponse);
		logDebug("Exit.handleAddAllItemsToGiftRegistry");

		BBBPerformanceMonitor.end(GiftRegistryFormHandler.class.getName() + " : " + "handleAddAllItemsToGiftRegistry");
		return isCcFlag();
	}
	/**
	 * 
	 * @param request
	 * @param response
	 * @param profile
	 * @return
	 */
	public boolean recommendationHandling(DynamoHttpServletRequest request, DynamoHttpServletResponse response,
			Profile profile) {
		try {
			logDebug("Entering in  recommendationHandling method");
			String registryId = getRegId();
			String skuId = getSkuId();
			String comment = getComment();
			String quantity = getRecommendedQuantity();
			String recommenderProfileId = profile.getRepositoryId();
			Long recommendequantity = Long.valueOf(quantity);
			request.setParameter("registryId", "");
			GiftRegistryRecommendationManager giftRegistryRecommendationManager = (GiftRegistryRecommendationManager) Nucleus
					.getGlobalNucleus()
					.resolveName("/com/bbb/commerce/giftregistry/manager/GiftRegistryRecommendationManager");
			request.resolveName("/com/bbb/commerce/giftregistry/manager/GiftRegistryRecommendationManager");
			logDebug("registry id= " + registryId + "skuid= " + skuId + "Quantity= " + recommendequantity
					+ "recommenderProfileId= " + recommenderProfileId + "comment= " + comment);
			giftRegistryRecommendationManager.createRegistryRecommendationProduct(registryId, skuId, comment,
					recommendequantity, recommenderProfileId);
		} catch (BBBSystemException e) {
			logError("Error while adding recommmendation's" + e.getMessage(), e);
			this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_ADD_RECOMM_BIZ_EXCEPTION,
					BBBGiftRegistryConstants.ERR_ADD_RECOMM_BIZ_EXCEPTION_MSG));
		}
		return true;
	}

	/**
	 * adding item to the gift registry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletExcepgtion
	 *             the servlet exception
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean handleAddItemToGiftRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {

		this.logDebug("GiftRegistryFormHandler.handleAddItemToGiftRegistry() method starts...");
		final Profile profile = (Profile) pRequest
				.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE));
		 String addAllRegItems= pRequest.getParameter("addAll");
		// Porch persist zipcode and payload in session 
		 String  itemPorchJson=null;
		net.sf.json.JSONObject porchJsonObjectString = null;
		if(null!=this.getJasonCollectionObj() && (StringUtils.isBlank(addAllRegItems))){
		porchJsonObjectString = (net.sf.json.JSONObject) JSONSerializer.toJSON(this.getJasonCollectionObj());
		final DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava((JSON) porchJsonObjectString);
		final List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean.get("addItemResults");
		
		 if(null!= itemArray || !itemArray.isEmpty()){
			    itemPorchJson=null;
			 	try{
				   itemPorchJson=(String) itemArray.get(0).get("porchPayLoadJson");
				   
				   if(!atg.core.util.StringUtils.isBlank(itemPorchJson)){
					   getSessionBean().setRegistryJsonResultString(itemPorchJson);
					   net.sf.json.JSONObject porchJsonObject = (net.sf.json.JSONObject) JSONSerializer.toJSON(itemPorchJson);
					   String porchZipCode = (String) porchJsonObject.get("postalCode");
					   if(!StringUtils.isBlank(porchZipCode))
					   getSessionBean().setPorchZipCode(porchZipCode);   
					}
				   
			 	}
			 	catch(MorphException e){
			 		itemPorchJson=null;
			 	}
				
		 }	
		}
		if (this.getRecommededFlag().equalsIgnoreCase(BBBCoreConstants.TRUE)) {
			recommendationHandling(pRequest, pResponse, profile);
			return this.checkFormRedirect(this.getRecomPopUpSuccessURL(), this.getRecomPopUpErrorURL(), pRequest, pResponse);
		} else {
			try {
				final GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
				this.setAlternateNum(null);
				// Code for checking if alternate number is present in json
				// object for updating to registry
				if (BBBUtility.isNotEmpty(this.getJasonCollectionObj())
						&& this.getJasonCollectionObj().contains(BBBCoreConstants.ALTERNATE_NUM)) {
					int start = this.getJasonCollectionObj().indexOf(BBBCoreConstants.ALTERNATE_NUM);
					int ind = this.getJasonCollectionObj().indexOf(":", start);
					int end = this.getJasonCollectionObj().indexOf("\"", ind + 2);
					this.setAlternateNum(this.getJasonCollectionObj().substring(ind + 2, end));
				}

				if (BBBUtility.isNotEmpty(this.getAlternateNum())
						&& !BBBUtility.isValidPhoneNumber(this.getAlternateNum())) {
					this.setErrorFlagAddItemToRegistry(true);
					this.addFormException(
							new DropletException(BBBCoreConstants.INVALID_PHONE, BBBCoreConstants.INVALID_PHONE));
					logError(BBBCoreConstants.INVALID_PHONE);
					return this.isErrorFlagAddItemToRegistry();
				}
				if (this.getJasonCollectionObj() != null && !(getKickStarterAddAllAction() != null)) {
					String countryCode = (String) profile
							.getPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE);
					String currencyCode = (String) profile
							.getPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE);
					Boolean internationalContext = (Boolean) profile
							.getPropertyValue(BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT);
					giftRegistryViewBean.setUserCountry(countryCode);
					giftRegistryViewBean.setUserCurrency(currencyCode);
					giftRegistryViewBean.setInternationalContext(internationalContext);
					getGiftRegistryManager().getGiftRegistryTools().addItemJSONObjectParser(giftRegistryViewBean,
							this.getJasonCollectionObj());
					if (this.getJasonCollectionObj().contains(BBBCatalogConstants.IS_LTL_SKU)
							&& !giftRegistryViewBean.getAdditem().isEmpty() && BBBCoreConstants.TRUE.equals(giftRegistryViewBean.getAdditem().get(0).getLtlFlag())) {
							this.setLtlFlag(BBBCoreConstants.TRUE);
					}
				}
				if (this.getJasonCollectionObj() != null) {
					if (this.getJasonCollectionObj().contains(BBBGiftRegistryConstants.IS_FROM_PENDING_TAB)) {
						if (giftRegistryViewBean.getIsDeclined().equals(BBBCoreConstants.FALSE)) {
							boolean returnValue = getGiftRegistryRecommendationManager()
									.acceptRecommendation(giftRegistryViewBean, false);
							if (!returnValue) {
								this.addFormException(
										new DropletException(BBBGiftRegistryConstants.ERR_FETCHING_PROFILE,
												BBBGiftRegistryConstants.ERR_MSG_FETCHING_PROFILE));
								logError(BBBGiftRegistryConstants.ERR_MSG_FETCHING_PROFILE);
							}
						} else {
							boolean returnValue = getGiftRegistryRecommendationManager()
									.declinePendingItems(giftRegistryViewBean);
							if (!returnValue) {
								this.addFormException(
										new DropletException(BBBGiftRegistryConstants.ERR_FETCHING_PROFILE,
												BBBGiftRegistryConstants.ERR_MSG_FETCHING_PROFILE));
								logError(BBBGiftRegistryConstants.ERR_MSG_FETCHING_PROFILE);
							}
							return returnValue;
						}
					} else if (this.getJasonCollectionObj().contains(BBBGiftRegistryConstants.IS_FROM_DECLINED_TAB)) {
						boolean returnValue = getGiftRegistryRecommendationManager()
								.acceptRecommendation(giftRegistryViewBean, true);
						if (!returnValue) {
							this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_FETCHING_PROFILE,
									BBBGiftRegistryConstants.ERR_MSG_FETCHING_PROFILE));
							logError(BBBGiftRegistryConstants.ERR_MSG_FETCHING_PROFILE);
						}
					}
				}
				if (getKickStarterAddAllAction() != null) {
					this.getKickStarterManager().setKickStarterItemsIntoSessionVo(this.getJasonCollectionObj(),
							giftRegistryViewBean, this.getSessionBean());
					this.getSessionBean().setAddALLActgion(BBBGiftRegistryConstants.ADD_ALL_ITEMS);
					if (giftRegistryViewBean.getAdditem() == null) {
						return true;
					}
				}
				// added for save for later
				else if (this.isMoveItemFromSaveForLater()) {
					if (StringUtils.isEmpty(this.getWishListItemId())) {
						this.addFormException(
								new DropletException(BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_WISHLIST_ID_MSG,
										BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_WISHLIST_ID));
						return this.isErrorFlagAddItemToRegistry();
					}
					try {
						final RepositoryItem wishListItem = this.getGiftListManager().getGiftitem(this.getWishListItemId());
						if (wishListItem == null) {
							this.addFormException(
									new DropletException(BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID_MSG,
											BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID));
							return this.isErrorFlagAddItemToRegistry();
						}

						this.addItemBeanValues(profile, giftRegistryViewBean, wishListItem);

					} catch (final CommerceException e) {
						this.logError(LogMessageFormatter.formatMessage(pRequest,
								"CommerceException from handleAddItemToGiftRegistry of GiftRegistryFormHandler",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);
						this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM_MSG,
								BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM));
					} catch (final RepositoryException e) {
						this.logError(LogMessageFormatter.formatMessage(pRequest,
								"RepositoryException from handleAddItemToGiftRegistry of GiftRegistryFormHandler while removing item",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);
						this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM_MSG,
								BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM));

					}
				}
				if (!profile.isTransient()) {
					this.isRegistryOwnedByProfile(giftRegistryViewBean.getRegistryId());
				}
				final String siteId = this.getSiteContext().getSite().getId();
				giftRegistryViewBean.setSiteFlag(this.getBbbCatalogTools()
						.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
				giftRegistryViewBean
						.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
				String refNum = giftRegistryViewBean.getAdditem().get(0).getRefNum();
				String personalizationCode = giftRegistryViewBean.getAdditem().get(0).getPersonalizationCode();
				EximSummaryResponseVO eximSummaryResponseVO = null;
				String omniProductList = BBBCoreConstants.BLANK;
				String enableKatoriFlag = getEximPricingManager().getKatoriAvailability();
				if (refNum != null && !refNum.isEmpty() && "false".equalsIgnoreCase(enableKatoriFlag)
						&& (!BBBUtility.isEmpty(personalizationCode))
						&& (personalizationCode.equalsIgnoreCase(BBBCoreConstants.PERSONALIZATION_CODE_CR)
								|| personalizationCode.equalsIgnoreCase(BBBCoreConstants.PERSONALIZATION_CODE_PB))) {
					throw new BBBBusinessException(BBBCoreErrorConstants.ERROR_ENABLE_KATORI_FALSE_ADDTOREGISTRY);
				}
				String totalPrice=BBBCoreConstants.BLANK;
				if (refNum != null && !refNum.isEmpty() && "true".equalsIgnoreCase(enableKatoriFlag)) {
					eximSummaryResponseVO = getEximPricingManager()
							.invokeSummaryAPI(giftRegistryViewBean.getAdditem().get(0).getProductId(), null, refNum);
					if (null != eximSummaryResponseVO && null != eximSummaryResponseVO.getCustomizations()
							&& !eximSummaryResponseVO.getCustomizations().isEmpty()) {
						EximCustomizedAttributesVO eximAtrributes = eximSummaryResponseVO.getCustomizations().get(0);
						//RM Defect 44562 start 
						Map<String, Object> formattedPrice=getEximPricingManager().calculateFormattedPrice(giftRegistryViewBean.getAdditem().get(0).getSku(), siteId, eximAtrributes.getRetailPriceAdder(), false, giftRegistryViewBean.getAdditem().get(0).getProductId());
						if(formattedPrice!=null && !formattedPrice.isEmpty()){
							double eximPersonlizedPrice = 0.00;
							eximPersonlizedPrice = ((Double)formattedPrice.get(BBBEximConstants.EXIM_ITEM_PRICE)).doubleValue();
							totalPrice=this.totalPrice(giftRegistryViewBean.getAdditem().get(0).getQuantity(), Double.toString(eximPersonlizedPrice));
						}else{
							totalPrice=this.totalPrice(giftRegistryViewBean.getAdditem().get(0).getQuantity(), giftRegistryViewBean.getAdditem().get(0).getPrice());
						}						
						omniProductList = setOmniProductList(
								giftRegistryViewBean, omniProductList,
								totalPrice,eximAtrributes);
						
					}
					giftRegistryViewBean.setOmniProductList(omniProductList);
				}
				//RM defect 44562 end
				// BBBI - Interactive Checklist .On add/ update/remove item update the ribbon to the registry if it is guide
		        getSessionBean().setActivateGuideInRegistryRibbon(false);
				giftRegistryViewBean.setServiceName(this.getAddItemsToReg2ServiceName());
				setGiftRegistryBean(refNum, giftRegistryViewBean, eximSummaryResponseVO, personalizationCode);
				this.logDebug("siteFlag: " + giftRegistryViewBean.getSiteFlag());
				this.logDebug("userToken: " + giftRegistryViewBean.getUserToken());
				this.logDebug("ServiceName: " + giftRegistryViewBean.getServiceName());
				giftRegistryViewBean.setRegistrySize(giftRegistryViewBean.getAdditem().size());
				final String parentProdId = giftRegistryViewBean.getParentProductId();
				String appendData = BBBCoreConstants.BLANK;
				if (!giftRegistryViewBean.getAdditem().isEmpty()) {
					
					this.populateJasonCollectionObj(pRequest, giftRegistryViewBean,
							siteId, parentProdId, appendData);
					this.getSessionBean().setGiftRegistryViewBean(giftRegistryViewBean);
					if (profile.isTransient()) {

						final String loginFrom = BBBWebServiceConstants.TXT_LOGIN_ADD_REGISTRY;

						profile.setPropertyValue(BBBWebServiceConstants.LOGIN_FROM, loginFrom);

						pResponse.setHeader("BBB-ajax-redirect-url", getLoginRedirectUrl());
					} else {
						this.preAddItemToGiftRegistry(pRequest, pResponse, giftRegistryViewBean);
						ValidateAddItemsResVO addItemsResVO = null;
						if (!(getKickStarterAddAllAction() != null
								&& getKickStarterAddAllAction().equals(BBBGiftRegistryConstants.ADD_ALL_ITEMS))) {
							addItemsResVO = getGiftRegistryManager().addItemToGiftRegistry(giftRegistryViewBean);
						} else {
							addItemsResVO = getGiftRegistryManager().addBulkItemsToGiftRegistry(giftRegistryViewBean,
									getSessionBean());
						}
						if (!addItemsResVO.getServiceErrorVO().isErrorExists()) {
							
							getSessionBean().setActivateGuideInRegistryRibbon(false);
							final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
									.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
							getGiftRegistryManager().getGiftRegistryTools().invalidateRegistry(giftRegistryViewBean);
							
							this.updateAlternateNumInReg(pRequest, pResponse, giftRegistryViewBean,
									siteId, sessionBean);
							
							final String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
							if (BBBUtility.isNotEmpty(channel)
									&& (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB)
											|| channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))
									&& giftRegistryViewBean != null && giftRegistryViewBean.getAdditem() != null
									&& !giftRegistryViewBean.getAdditem().isEmpty()) {
								RegistryItemsListVO registryItemsListVO = getRegistryItemList(giftRegistryViewBean, siteId);
								List<RegistryItemVO> registryItemsList = registryItemsListVO.getRegistryItemList();
								Iterator<RegistryItemVO> registryItemsIterator = registryItemsList.iterator();
								List<ItemDetailsVO> itemDetailsVOs = new ArrayList<ItemDetailsVO>();
								for (int i = 0; i < giftRegistryViewBean.getAdditem().size(); i++) {
									AddItemsBean addItemsBean1 = giftRegistryViewBean.getAdditem().get(i);
									long skuIdFromAddItem = Long.parseLong(addItemsBean1.getSku());
									while (registryItemsIterator.hasNext()) {
										RegistryItemVO registryItemVo = registryItemsIterator.next();
										if (registryItemVo.getSku() == skuIdFromAddItem) {
											ItemDetailsVO addItemVO = new ItemDetailsVO();
											addItemVO.setRowId(registryItemVo.getRowID());
											addItemVO.setPurchasedQuantity(registryItemVo.getQtyPurchased());
											addItemVO.setRegItemOldQty(registryItemVo.getQtyRequested());
											addItemVO.setSkuId(registryItemVo.getSku());
											itemDetailsVOs.add(addItemVO);
											registryItemsIterator = registryItemsList.iterator();
											break;
										}

									}
								}
								setItemDetailsVO(itemDetailsVOs);
							}
							this.setErrorFlagAddItemToRegistry(false);
							// Update session data so that it will be in sync
							// with the
							// registred items on registry flyout.
							final Map sessionMap = sessionBean.getValues();
							final RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap
									.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
							if ((registrySummaryVO != null) && registrySummaryVO.getRegistryId()
									.equalsIgnoreCase(giftRegistryViewBean.getRegistryId())) {
								int totalQuantity = 0;

								final List<AddItemsBean> additemList = giftRegistryViewBean.getAdditem();

								for (final AddItemsBean addItemsBean : additemList) {
									totalQuantity += Integer.parseInt(addItemsBean.getQuantity());
								}

								// update quantiry in session
								totalQuantity = totalQuantity + registrySummaryVO.getGiftRegistered();
								
								registrySummaryVO.setGiftRegistered(totalQuantity);

							} else if ((registrySummaryVO != null) && !registrySummaryVO.getRegistryId()
									.equalsIgnoreCase(giftRegistryViewBean.getRegistryId())) {
								// update the registrysummaryvo in the
								// BBBsessionBean
								final RegistrySummaryVO regSummaryVO = getGiftRegistryManager()
										.getRegistryInfo(giftRegistryViewBean.getRegistryId(), siteId);
								sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY,
										regSummaryVO);
							}
							this.getGiftRegSessionBean().setRegistryOperation(BBBGiftRegistryConstants.GR_ITEM_UPDATE);
							
							this.updateWishListItems(pRequest, pResponse);

						} else {
							this.errorAdditemCertona(pRequest, addItemsResVO);
						}
						// change for calculating total price (certona)
						final List<AddItemsBean> additemList = giftRegistryViewBean.getAdditem();
						double allItemTotalPrice = 0;
						for (final AddItemsBean addItemsBean : additemList) {
							if (addItemsBean != null) {
								allItemTotalPrice += Double.parseDouble(
										this.totalPrice(addItemsBean.getQuantity(), addItemsBean.getPrice()));
							}
						}
						this.setTotalPrice(String.valueOf(BBBUtility.round(allItemTotalPrice)));
						// change for calculating total price (certona)
					}
					this.logDebug("get handleAddItemToGiftRegistry value from request");
					this.logDebug("GiftRegistryFormHandler.handleAddItemToGiftRegistry() method ends");

				}
			} catch (final BBBBusinessException e) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"BBBBusinessException from handleAddItemToGiftRegistry of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);
				this.setErrorFlagAddItemToRegistry(true);
				this.addFormException(new DropletException(
						this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.BUSINESS_EXCEPTION,
								pRequest.getLocale().getLanguage(), null, null),
						BBBGiftRegistryConstants.BUSINESS_EXCEPTION));

			} catch (final BBBSystemException es) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"BBBBusinessException from handleAddItemToGiftRegistry of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005), es);
				this.setErrorFlagAddItemToRegistry(true);
				this.addFormException(new DropletException(
						this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.SYSTEM_EXCEPTION,
								pRequest.getLocale().getLanguage(), null, null),
						BBBGiftRegistryConstants.SYSTEM_EXCEPTION));
			} catch (final NumberFormatException es) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"NumberFormatException from handleAddItemToGiftRegistry of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10104), es);
				this.addFormException(new DropletException(
						this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.INVALID_QUANTITY_EXCEPTION,
								pRequest.getLocale().getLanguage(), null, null)));
				this.setErrorFlagAddItemToRegistry(true);
				this.setErrorURL(this.getSuccessURL());
			}
			return this.isErrorFlagAddItemToRegistry();
		}
	}
	/**
	 * @param pRequest
	 * @param pResponse
	 * @param giftRegistryViewBean
	 * @param siteId
	 * @param sessionBean
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void updateAlternateNumInReg(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse,
			final GiftRegistryViewBean giftRegistryViewBean,
			final String siteId, final BBBSessionBean sessionBean)
			throws BBBBusinessException, BBBSystemException, ServletException,
			IOException {
		RegistryResVO registryResVO = getGiftRegistryManager()
				.getRegistryInfoFromEcomAdmin(giftRegistryViewBean.getRegistryId(), siteId);
		sessionBean.getValues().put(giftRegistryViewBean.getRegistryId() + REG_SUMMARY_KEY_CONST,
				registryResVO);
		// Code for updating alternate number in registry
		if (registryResVO != null && BBBUtility.isNotEmpty(this.getAlternateNum())) {
			registryResVO.getRegistryVO().getPrimaryRegistrant().setCellPhone(this.getAlternateNum());
			registryResVO.getRegistryVO().getEvent().setBabyGender(BBBCoreConstants.BLANK);
			if (!siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				if (BBBUtility.isNotEmpty(registryResVO.getRegistryVO().getEvent().getEventDate())) {
				registryResVO.getRegistryVO().getEvent()
						.setEventDate(BBBUtility.convertWSDateToUSFormat(
								registryResVO.getRegistryVO().getEvent().getEventDate()));
				}
				if (BBBUtility
						.isNotEmpty(registryResVO.getRegistryVO().getEvent().getShowerDate())) {
					registryResVO.getRegistryVO().getEvent()
							.setShowerDate(BBBUtility.convertWSDateToUSFormat(
									registryResVO.getRegistryVO().getEvent().getShowerDate()));
				}
				if (BBBUtility
						.isNotEmpty(registryResVO.getRegistryVO().getEvent().getBirthDate())) {
					registryResVO.getRegistryVO().getEvent()
							.setBirthDate(BBBUtility.convertWSDateToUSFormat(
									registryResVO.getRegistryVO().getEvent().getBirthDate()));
				}
				if ((this.getRegistryVO().getShipping() != null) && !StringUtils
						.isEmpty(this.getRegistryVO().getShipping().getFutureShippingDate())) {
					registryResVO.getRegistryVO().getShipping()
							.setFutureShippingDate(BBBUtility.convertWSDateToUSFormat(registryResVO
									.getRegistryVO().getShipping().getFutureShippingDate()));
				}
			}
			this.setRegistryVO(registryResVO.getRegistryVO());
			this.getSessionBean().setRegistryTypesEvent(
					registryResVO.getRegistryVO().getRegistryType().getRegistryTypeName());
			pRequest.setParameter(BBBCoreConstants.ALTERNATE_NUM, BBBCoreConstants.TRUE);
			pRequest.setParameter(BBBCoreConstants.UPDATE_REG_SUMMARY_REQUIRED, BBBCoreConstants.FALSE);
			this.handleUpdateRegistry(pRequest, pResponse);
		}
	}
	/**
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void updateWishListItems(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (this.isFromWishListPage()) {
			try {
				this.setMovedItemRegistryId(this.getRegistryId());

				this.setRegistryId(null);
				final RepositoryItem wishList = ((RepositoryItem) this.getProfile()
						.getPropertyValue(WISH_LIST));
				final List<RepositoryItem> items = (List<RepositoryItem>) wishList
						.getPropertyValue(BBBCoreConstants.GIFT_LIST_ITEMS);
				final Iterator<RepositoryItem> itr = items.iterator();
				while (itr.hasNext()) {
					final MutableRepositoryItem item = (MutableRepositoryItem) itr.next();
					if (item.getRepositoryId().equalsIgnoreCase(this.getWishListItemId())) {
						if (!this.getMovedItemIndex().equals("0")) {
							this.getMovedItemMap().put(this.getMovedItemIndex(),
									(String) item.getPropertyValue("productId"));
						}
						itr.remove();
					}
				}
				((MutableRepository) wishList.getRepository())
						.updateItem((MutableRepositoryItem) wishList);
				this.checkFormRedirect(this.getMoveToRegistryResponseURL(),
						this.getMoveToRegistryResponseURL(), pRequest, pResponse);
			} catch (final RepositoryException e) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"RepositoryException from handleAddItemToGiftRegistry of GiftRegistryFormHandler while removing item",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);
				this.addFormException(
						new DropletException(BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM_MSG,
								BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM));
			}
		}
	}
	/**
	 * @param pRequest
	 * @param giftRegistryViewBean
	 * @param siteId
	 * @param parentProdId
	 * @param resultData
	 */
	private void populateJasonCollectionObj(
			final DynamoHttpServletRequest pRequest,
			final GiftRegistryViewBean giftRegistryViewBean,
			final String siteId, final String parentProdId, String appendData) {
		String resultData = appendData;
		if (!StringUtils.isBlank(giftRegistryViewBean.getAdditem().get(0).getSku())) {
			resultData = "&skuId=" + giftRegistryViewBean.getAdditem().get(0).getSku();
		}
		if (!StringUtils.isBlank(giftRegistryViewBean.getAdditem().get(0).getParamRegistryId())) {
			resultData = resultData + "&registryId="
					+ giftRegistryViewBean.getAdditem().get(0).getParamRegistryId();
		}
		if (!StringUtils.isBlank(giftRegistryViewBean.getAdditem().get(0).getProductId())) {
			resultData = resultData + "&regProdId="
					+ giftRegistryViewBean.getAdditem().get(0).getProductId();
		}
		if (!StringUtils.isBlank((String) pRequest.getParameter(BBBCoreConstants.LTL_FLAG))) {
			resultData = resultData + "&ltlFlag=" + pRequest.getParameter(BBBCoreConstants.LTL_FLAG);
		}
		// Redirect to product comparison page for anonymous user if
		// Add to Registry is called from compare page. R2.2 Story
		// 178-A4 : Start
		try {
			if (this.getJasonCollectionObj() != null) {
				JSONObject jsonObject = new JSONObject(this.getJasonCollectionObj());
				if (jsonObject.has("fromComparisonPage")
						&& !BBBUtility.isEmpty(jsonObject.getString("fromComparisonPage"))) {
					if (siteId.contains("TBS")) {
						this.setSuccessURL(BBBGiftRegistryConstants.COMPARE_SUCCESS_URL_TBS + parentProdId
								+ resultData);
					} else {
						this.setSuccessURL(BBBGiftRegistryConstants.COMPARE_SUCCESS_URL + parentProdId
								+ resultData);
						giftRegistryViewBean.setSuccessURLforNotifyProduct(pRequest.getContextPath()
								+ BBBGiftRegistryConstants.SUCCESS_URL_ADD_ITEM + parentProdId + resultData);
					}
				} else if (jsonObject.has(BBBCoreConstants.RETURN_URL)
						&& !BBBUtility.isEmpty(jsonObject.getString(BBBCoreConstants.RETURN_URL))) {
					this.setSuccessURL(jsonObject.getString(BBBCoreConstants.RETURN_URL));
				} else {
					this.setSuccessURL(pRequest.getContextPath()
							+ BBBGiftRegistryConstants.SUCCESS_URL_ADD_ITEM + parentProdId + resultData);
					String childProdId = giftRegistryViewBean.getAdditem().get(0).getProductId();
					if (!BBBUtility.compareStringsIgnoreCase(childProdId, parentProdId)) {
						giftRegistryViewBean.setSuccessURLforChildProduct(pRequest.getContextPath()
								+ BBBGiftRegistryConstants.SUCCESS_URL_ADD_ITEM + childProdId + resultData);
					}
				}
				if (!BBBUtility.isEmpty(this.getSuccessURL()))
					giftRegistryViewBean.setSuccessURL(this.getSuccessURL());
			}
		} catch (JSONException excep) {
			if (this.isLoggingError()) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"JSONexception in GiftRegistryFormHandler.handleAddItemToGiftRegistry",
						BBBCoreErrorConstants.ACCOUNT_ERROR_1261), excep);
			}
		}
	}
	/**
	 * @param profile
	 * @param giftRegistryViewBean
	 * @param wishListItem
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws RepositoryException
	 */
	private void addItemBeanValues(final Profile profile,
			final GiftRegistryViewBean giftRegistryViewBean,
			final RepositoryItem wishListItem) throws BBBSystemException,
			BBBBusinessException, RepositoryException {
		final List<AddItemsBean> additemList = new ArrayList<AddItemsBean>();
		final AddItemsBean addItemsBean = new AddItemsBean();
		addItemsBean.setRefNum(getRefNum());
		addItemsBean.setPersonalizationCode(getPersonalizationType());
		addItemsBean.setSku((String) wishListItem.getPropertyValue(BBBCoreConstants.CATALOG_REF_ID));
		addItemsBean.setProductId((String) wishListItem.getPropertyValue(BBBCoreConstants.PRODUCTID));
		addItemsBean.setRegistryId(this.getRegistryId());
		addItemsBean.setQuantity(
				((Long) wishListItem.getPropertyValue(BBBCoreConstants.QUANTITY_DESIRED)).toString());
		addItemsBean.setCustomizationRequired(this.getCustomizationReq());
		// Added for LTL items
		String siteId = getSiteContext().getSite().getId();
		boolean isSkuLtl = getBbbCatalogTools().isSkuLtl(siteId, addItemsBean.getSku());
		if (isSkuLtl) {
			this.setLtlFlag(BBBCoreConstants.TRUE);
			this.setLtlDeliveryServices((String) wishListItem
					.getPropertyValue(BBBCoreConstants.LTL_SHIP_METHOD));
			if (BBBUtility.isNotEmpty(this.getLtlDeliveryServices())) {
				addItemsBean.setLtlDeliveryServices(this.getLtlDeliveryServices());
				addItemsBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK
						+ (getBbbCatalogTools().getDeliveryCharge(SiteContextManager.getCurrentSiteId(),
								addItemsBean.getSku(), addItemsBean.getLtlDeliveryServices())));
			}
			if (BBBUtility.isNotEmpty(this.getAlternateNumber())) {
				this.setAlternateNum(this.getAlternateNumber());
			}
		}
		final RegistryVO registryVO = getGiftRegistryManager()
				.getRegistryDetailInfo(this.getRegistryId(), SiteContextManager.getCurrentSiteId());
		final String registryCodeType = registryVO.getRegistryType().getRegistryTypeName();
		final String giftRegistryName = this.getGiftRegistryManager()
				.getRegistryNameForRegCode(registryCodeType);
		this.getCertonaParameter().setRegistryName(giftRegistryName);
		this.getCertonaParameter().setItemId(addItemsBean.getProductId());
		this.getCertonaParameter().setQuantity(Integer.parseInt(addItemsBean.getQuantity()));
		this.getCertonaParameter().setTransactionId(this.getRegistryId());
		this.getCertonaParameter().setCustomerId(profile.getRepositoryId());

		// set price
		final double listPrice = this.getBbbCatalogTools()
				.getListPrice(addItemsBean.getProductId(), addItemsBean.getSku()).doubleValue();
		final double salePrice = this.getBbbCatalogTools()
				.getSalePrice(addItemsBean.getProductId(), addItemsBean.getSku()).doubleValue();
		if ((salePrice > 0)) {
			addItemsBean.setPrice(Double.toString(salePrice));
		} else {
			addItemsBean.setPrice(Double.toString(listPrice));
		}
		this.getCertonaParameter().setPrice(Double.parseDouble(addItemsBean.getPrice()));
		additemList.add(addItemsBean);
		giftRegistryViewBean.setAdditem(additemList);
		giftRegistryViewBean.setParentProductId(addItemsBean.getProductId());
		giftRegistryViewBean.setRegistryId(this.getRegistryId());
		giftRegistryViewBean.setQuantity(addItemsBean.getQuantity());
		giftRegistryViewBean.setRegistryName(this.getRegistryName());
	}

	/**
	 * @param giftRegistryViewBean
	 * @param omniProductList
	 * @param totalPrice
	 * @return
	 */
	private String setOmniProductList(
			final GiftRegistryViewBean giftRegistryViewBean,
			String omniProductList, String totalPrice,EximCustomizedAttributesVO eximAtrributes) {
		String omniProductResultList = omniProductList;
		omniProductResultList += ";" + giftRegistryViewBean.getAdditem().get(0).getProductId()
				+ ";;;event22=" + giftRegistryViewBean.getAdditem().get(0).getQuantity()
				+ "|event23=" + totalPrice + ";eVar30="
				+ giftRegistryViewBean.getAdditem().get(0).getSku() + ",";	
		omniProductResultList = omniProductResultList.substring(0,
				omniProductResultList.lastIndexOf(","));
		if (null != eximAtrributes && eximAtrributes.getErrors().isEmpty()) {
			omniProductResultList += "|eVar54=" + getEximPricingManager().getEximValueMap()
					.get(eximAtrributes.getCustomizationService());
		}
		return omniProductResultList;
	}
	
	/**
	 * 
	 * @param refNum
	 * @param giftRegistryViewBean
	 * @param eximSummaryResponseVO
	 * @param personalizationCode
	 */
	private void setGiftRegistryBean(String refNum, GiftRegistryViewBean giftRegistryViewBean,
			EximSummaryResponseVO eximSummaryResponseVO, String personalizationCode) {

		logDebug("Starting Method setGiftRegistryBean : Setting values in giftregistryview Bean");

		logDebug("REFNUM IS " + refNum + " PersonlizationCode is" + personalizationCode);

		if (!BBBUtility.isEmpty(refNum) && !BBBUtility.isEmpty(personalizationCode)
				&& null != eximSummaryResponseVO && !BBBUtility.isListEmpty(eximSummaryResponseVO.getCustomizations())) {
			EximCustomizedAttributesVO eximAtrributes = eximSummaryResponseVO.getCustomizations().get(0);
			giftRegistryViewBean.setRefNum(refNum);
			giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setPersonlizationCodes(eximAtrributes.getCustomizationService());
			giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setItemTypes(BBBCoreConstants.PERSONALIZATION_ITEM_TYPE);
			giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setPersonalizationDescrips(eximAtrributes.getNamedrop());

			List<EximImagesVO> images = eximAtrributes.getImages();
			for (EximImagesVO imageVO : images) {
				if (imageVO.getId().equalsIgnoreCase(BBBCoreConstants.IMAGE_ID)
						|| imageVO.getId().equalsIgnoreCase("")) {
					List<EximImagePreviewVO> previews = imageVO.getPreviews();
					for (EximImagePreviewVO preview : previews) {
						if (preview.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_LARGE))
							giftRegistryViewBean.setPersonalizedImageUrls(preview.getUrl());
						if (preview.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_X_SMALL)) {
							giftRegistryViewBean.setPersonalizedImageUrlThumbs(preview.getUrl());
							giftRegistryViewBean.setPersonalizedMobImageUrlThumbs(preview.getUrl());
						}
						if (preview.getSize().equalsIgnoreCase(BBBCoreConstants.IMAGE_PREVIEW_MEDIUM))
							giftRegistryViewBean.setPersonalizedMobImageUrls(preview.getUrl());
					}
					break;
				} else {
					giftRegistryViewBean.setPersonalizedImageUrls(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setPersonalizedImageUrlThumbs(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setPersonalizedMobImageUrlThumbs(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setPersonalizedMobImageUrls(BBBCoreConstants.BLANK);
				}
			}

			if (giftRegistryViewBean.getAdditem().get(0).getPersonalizationCode()
					.equalsIgnoreCase(BBBCoreConstants.PERSONALIZATION_CODE_CR)) {

				giftRegistryViewBean.setPersonalizationPrices(Double.toString(eximAtrributes.getRetailPriceAdder()));
				giftRegistryViewBean.setCustomizationPrices(Double.toString(eximAtrributes.getRetailPriceAdder()));

			} else if (giftRegistryViewBean.getAdditem().get(0).getPersonalizationCode()
					.equalsIgnoreCase(BBBCoreConstants.PERSONALIZATION_CODE_PB)) {

				giftRegistryViewBean.setPersonalizationPrices(giftRegistryViewBean.getAdditem().get(0).getPrice());
				giftRegistryViewBean.setCustomizationPrices(Double.toString(eximAtrributes.getRetailPriceAdder()));

			} else if (giftRegistryViewBean.getAdditem().get(0).getPersonalizationCode()
					.equalsIgnoreCase(BBBCoreConstants.PERSONALIZATION_CODE_PY)) {

				String basePrice = giftRegistryViewBean.getAdditem().get(0).getPrice();

				Double personalisedPrice = eximAtrributes.getRetailPriceAdder() + Double.parseDouble(basePrice);

				giftRegistryViewBean.setPersonalizationPrices(Double.toString(personalisedPrice));
				giftRegistryViewBean.setCustomizationPrices(Double.toString(eximAtrributes.getRetailPriceAdder()));
			}

		} else {
			giftRegistryViewBean.setRefNum(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setPersonlizationCodes(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
			if ((BBBCoreConstants.TRUE).equalsIgnoreCase(getLtlFlag())) {
				giftRegistryViewBean.setItemTypes(BBBCoreConstants.LTL_ITEM_TYPE);
			} else {
				giftRegistryViewBean.setItemTypes(BBBCoreConstants.BLANK);
			}
			giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setPersonalizationDescrips(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setPersonalizationPrices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setPersonalizedImageUrls(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setPersonalizedImageUrlThumbs(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setPersonalizedMobImageUrls(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setPersonalizedMobImageUrlThumbs(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setCustomizationPrices(BBBCoreConstants.BLANK);

		}

		logDebug("End of method -- setGiftRegistryBean ");
	}

	/**
	 * adding item to the gift registry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */
	public boolean handleEmailOptInChange(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		this.logDebug("GiftRegistryFormHandler.handleEmailOptIn() method starts");
		this.setErrorFlagEmailOptIn(false);
		try {
			getGiftRegistryRecommendationManager().setEmailOptInChange(this.getRegistryId(), this.getEmailOptIn());
		} catch (BBBSystemException e) {
			this.setErrorFlagEmailOptIn(true);
			logError("Exception while setting email options: " + e);
		}
		this.logDebug("GiftRegistryFormHandler.handleEmailOptIn() method ends with return parameter"
				+ " errorFlagEmailOptIn: " + this.isErrorFlagEmailOptIn());
		return this.isErrorFlagEmailOptIn();
	}

	/**
	 * Get RegistryItem List from Registry WS.
	 *
	 * @param giftRegistryViewBean
	 * @param siteId
	 * @return RegistryItemsListVO
	 *
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private RegistryItemsListVO getRegistryItemList(final GiftRegistryViewBean giftRegistryViewBean,
			final String siteId) {
		RegistryItemsListVO registryItemsListVO = new RegistryItemsListVO();
		RegistrySearchVO registrySearchVO = new RegistrySearchVO();
		int startIdx = 0;
		registrySearchVO.setView(Integer.parseInt(BBBGiftRegistryConstants.DEFAULT_ALL_VIEW_FILTER));
		registrySearchVO.setStartIdx(startIdx);
		registrySearchVO.setServiceName(getRegistryItemsListServiceName());
		registrySearchVO.setRegistryId(giftRegistryViewBean.getRegistryId());
		registrySearchVO.setGiftGiver(false);
		registrySearchVO.setAvailForWebPurchaseFlag(true);
		try {
			if (!(getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
					BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)).isEmpty()) {
				registrySearchVO
						.setUserToken(getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
			}
			if (!(getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId))
					.isEmpty()) {
				registrySearchVO.setSiteId(getBbbCatalogTools()
						.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
			}
			if (!(getBbbCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,
					"MaxSizeRegistryItems")).isEmpty()) {
				registrySearchVO.setBlkSize(Integer.parseInt(getBbbCatalogTools()
						.getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, "MaxSizeRegistryItems").get(0)));
			}
			registryItemsListVO = this.getGiftRegistryManager().fetchRegistryItems(registrySearchVO);
		} catch (BBBSystemException e) {
			logError("Exception while fetching registry items :" + e.getMessage(), e);
		} catch (BBBBusinessException e) {
			logError("Exception while fetching registry items :" + e.getMessage(), e);
		}
		return registryItemsListVO;
	}

	/**
	 * adding item to the gift registry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @param giftRegistryViewBean
	 *            the gift registry view bean
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 * @throws NumberFormatException
	 *             the number format exception
	 */
	public void preAddItemToGiftRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, final GiftRegistryViewBean giftRegistryViewBean)
					throws IOException, ServletException, NumberFormatException {

		this.logDebug("GiftRegistryFormHandler.preAddItemToGiftRegistry() method starts");

		this.logDebug("get preAddItemToGiftRegistry value from request");

		for (int i = 0; i < giftRegistryViewBean.getAdditem().size(); i++) {
			final AddItemsBean addItemsBean = giftRegistryViewBean.getAdditem().get(i);
			if (addItemsBean.getQuantity() != null) {
				Integer.parseInt(addItemsBean.getQuantity());

				if (!BBBUtility.isValidNumber(addItemsBean.getQuantity())) {

					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"GiftRegistry Invalid items quantity from preAddItemToGiftRegistry of GiftRegistryFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1056));

					this.addFormException(new DropletException(
							this.getLblTxtTemplateManager().getErrMsg(
									BBBGiftRegistryConstants.INVALID_QUANTITY_EXCEPTION,
									pRequest.getLocale().getLanguage(), null, null),
							BBBGiftRegistryConstants.INVALID_QUANTITY_EXCEPTION));
					throw new NumberFormatException(BBBGiftRegistryConstants.INVALID_QUANTITY_EXCEPTION);
				}

			} else {

				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"GiftRegistry empty items quantity from preAddItemToGiftRegistry of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1055));

				this.addFormException(new DropletException(
						this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.QUANTITY_EXCEPTION,
								pRequest.getLocale().getLanguage(), null, null),
						BBBGiftRegistryConstants.QUANTITY_EXCEPTION));
				throw new NumberFormatException(BBBGiftRegistryConstants.QUANTITY_EXCEPTION);
			}
		}
		this.logDebug("GiftRegistryFormHandler.preAddItemToGiftRegistry() method ends");

	}

	/**
	 * The method is invoked through ajax call from the JSP page. The method
	 * performs the following functionality User Authentication & Session Check
	 * Call Import Registry Web Service
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */
	@SuppressWarnings({ "unchecked" })
	public boolean handleImportRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		this.logDebug("GiftRegistryFormHandler.handleImportRegistry() method starts");

		boolean isNoError = true;

		String errorVal = this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.IMPORT_EXCEPTION,
				pRequest.getLocale().getLanguage(), null, null);
		if (StringUtils.isEmpty(errorVal)) {
			errorVal = BBBGiftRegistryConstants.NO_DATA;
		}
		final Profile profile = (Profile) pRequest
				.resolveName(ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE));
		if ((this.getCheckForValidSession() && !this.isValidSession(pRequest)) || profile.isTransient()) {
			if (this.getCheckForValidSession() && !this.isValidSession(pRequest)) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"Session expired exception from handleImportRegistry of GiftRegistryFormhandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1059));
				this.addFormException(new DropletException(BBBGiftRegistryConstants.SESSION_EXCEPTION,
						BBBGiftRegistryConstants.SESSION_EXCEPTION));
			}
			this.getSessionBean().setImportRegistryRedirectUrl(BBBGiftRegistryConstants.SUCCESS_URL_IMPORT_REGISTRY);
			this.setImportURL(BBBGiftRegistryConstants.LOGIN_REDIRECT_URL);
			isNoError = false;

		}
		if (isNoError) {
			try {

				this.setRegistryVO(profile);
				if (this.preImportRegistry(pRequest, pResponse)) {

					final RegistryResVO importResVO = this.getGiftRegistryManager().importRegistry(profile,
							this.getRegistryVO());

					if (!importResVO.getServiceErrorVO().isErrorExists()) {

						this.getRegistryVO().getEvent().setEventDate(this.getImportEventDate());

						this.getRegistryVO().getRegistryType().setRegistryTypeName(this.getImportEventType());

						/**
						 * Link this registry as primary registrant or co
						 * registrant
						 */
						getGiftRegistryManager().linkRegistry(this.getRegistryVO(), importResVO.getImportedAsReg());

						// Invalidate registrylist and summaryVO in the session
						final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
								.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);

						RegistrySummaryVO registrySummaryVO = null;
						List<String> userRegList = (List<String>) sessionBean.getValues()
								.get(BBBGiftRegistryConstants.USER_REGISTRIES_LIST);
						List<String> userActiveRegList = (List<String>) sessionBean.getValues()
								.get(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST);
						registrySummaryVO = (RegistrySummaryVO) sessionBean.getValues()
								.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
						if (userRegList != null) {
							userRegList.add(String.valueOf(this.getRegistryVO().getRegistryId()));
						}
						if (userActiveRegList != null) {
							userActiveRegList.add(String.valueOf(this.getRegistryVO().getRegistryId()));
						}

						this.updateProfileRegistryStatus();

						sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_LIST, userRegList);
						sessionBean.getValues().put(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST,
								userActiveRegList);
						sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY,
								registrySummaryVO);
					} else {

						this.setFormExceptionsForImportErrors(pRequest, errorVal, importResVO);
						isNoError = false;
					}
				}
				this.logDebug("GiftRegistryFormHandler.handleImportRegistry() method ends");

			} catch (final BBBSystemException bbbsysexp) {

				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"GiftRegistry error in importing registry from handleImportRegistry of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1052), bbbsysexp);

				this.addFormException(new DropletException(errorVal, BBBGiftRegistryConstants.IMPORT_EXCEPTION));
				this.setImportErrorMessage(errorVal);
				isNoError = false;
				return isNoError;
			}

			catch (final RepositoryException reposexp) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"RepositoryException from handleImportRegistry of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10105), reposexp);
				this.addFormException(new DropletException(
						this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.REPO_EXCEPTION,
								pRequest.getLocale().getLanguage(), null, null),
						BBBGiftRegistryConstants.REPO_EXCEPTION));
				this.setImportErrorMessage(this.getLblTxtTemplateManager().getErrMsg(
						BBBGiftRegistryConstants.REPO_EXCEPTION, pRequest.getLocale().getLanguage(), null, null));
				isNoError = false;
				return isNoError;
			} catch (final BBBBusinessException bbbbexp) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"BBBBusinessException from handleImportRegistry of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10106), bbbbexp);

				if (bbbbexp.getMessage().equals(BBBCoreConstants.PASSWORD_NOT_MATCH)) {

					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"GiftRegistry Incorrect password from handleImportRegistry of GiftRegistryFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1050), bbbbexp);

					this.addFormException(new DropletException(
							this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.PASSWORD_EXCEPTION,
									pRequest.getLocale().getLanguage(), null, null),
							BBBGiftRegistryConstants.PASSWORD_EXCEPTION));
					this.setImportErrorMessage(
							this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.PASSWORD_EXCEPTION,
									pRequest.getLocale().getLanguage(), null, null));
					isNoError = false;
				} else {
					this.addFormException(new DropletException(errorVal, BBBGiftRegistryConstants.IMPORT_EXCEPTION));
					isNoError = false;
					this.setImportErrorMessage(errorVal);
					return isNoError;
				}

			}
		}

		return isNoError;
	}
	/**
	 * 
	 */
	private void updateProfileRegistryStatus() {
		try {
			// Method call to sync the User Registries status
			// with legacy database

			this.getGiftRegistryManager().updateProfileRegistriesStatus(this.getProfile(),
					this.getSiteContext().getSite().getId());
		} catch (final BBBSystemException e) {
			this.logError(
					"BBBSystemException from mGiftRegistryManager.updateProfileRegistriesStatus method",
					e);
		} catch (final BBBBusinessException e) {
			this.logError(
					"BBBBusinessException from mGiftRegistryManager.updateProfileRegistriesStatus method",
					e);
		}
	}
	
	/**
	 * 
	 * @param pRequest
	 * @param errorVal
	 * @param importResVO
	 */
	private void setFormExceptionsForImportErrors(final DynamoHttpServletRequest pRequest, final String errorVal,
			final RegistryResVO importResVO) {

		String errorMsg = null;
		if (importResVO.getServiceErrorVO()
				.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REG_INVALID_REG_PASSWORD) {
			errorMsg = this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_PASSWORD,
					pRequest.getLocale().getLanguage(), null, null);
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Invalid password GiftRegistry from setFormExceptionsForImportErrors of GiftRegistryFormHandler Webservice code ="
							+ importResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10108));

		} else if (importResVO.getServiceErrorVO()
				.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS) {
			errorMsg = this.getLblTxtTemplateManager().getErrMsg(
					BBBGiftRegistryConstants.ERR_GIFT_REG_INPUT_FIELD_ERROR, pRequest.getLocale().getLanguage(), null,
					null);
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Invalid input fields from setFormExceptionsForImportErrors of GiftRegistryFormHandler Webservice code ="
							+ importResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10109));
		} else if (importResVO.getServiceErrorVO()
				.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR) {
			errorMsg = this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
					pRequest.getLocale().getLanguage(), null, null);
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Fatal error from setFormExceptionsForImportErrors of GiftRegistryFormHandler Webservice code ="
							+ importResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10110));
		} else if (importResVO.getServiceErrorVO()
				.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN) {
			errorMsg = this.getLblTxtTemplateManager().getErrMsg(
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR, pRequest.getLocale().getLanguage(),
					null, null);
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Either user token or site flag invalid from setFormExceptionsForImportErrors of GiftRegistryFormHandler Webservice code ="
							+ importResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10111));
		} else if (importResVO.getServiceErrorVO()
				.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry input fields format error from handleImportRegistry() of "
							+ WEB_SERVICE_ERROR_CODE + importResVO.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));
			errorMsg = this.getLblTxtTemplateManager().getErrMsg(
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT, pRequest.getLocale().getLanguage(),
					null, null);

		} else {
			errorMsg = errorVal;
		}
		this.addFormException(new DropletException(errorMsg));
		this.setImportErrorMessage(errorMsg);

	}
	
	/**
	 * 
	 * @param profile
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void setRegistryVO(final Profile profile) throws BBBSystemException, BBBBusinessException {
		this.getRegistryVO().setRegistryId(this.getRegistryId());
		this.getRegistryVO().setPassword(this.getRegistryPassword());
		final String siteId = this.getSiteContext().getSite().getId();

		/**
		 * Set info for whom the registry needs to be linked either as
		 * Registrant or CoRegistrant
		 */
		this.getRegistryVO().getRegistrantVO().setProfileId(profile.getRepositoryId());
		this.getRegistryVO().getRegistrantVO().setEmail((String) profile.getPropertyValue(BBBCoreConstants.EMAIL));
		this.getRegistryVO().getRegistrantVO()
				.setFirstName((String) profile.getPropertyValue(BBBCoreConstants.FIRST_NAME));
		this.getRegistryVO().getRegistrantVO()
				.setLastName((String) profile.getPropertyValue(BBBCoreConstants.LAST_NAME));
		this.getRegistryVO().getRegistrantVO()
				.setPrimaryPhone((String) profile.getPropertyValue(BBBCoreConstants.PHONE_NUM));
		this.getRegistryVO().getRegistrantVO()
				.setCellPhone((String) profile.getPropertyValue(BBBCoreConstants.MOBILE_NUM));

		this.getRegistryVO().setSiteId(
				getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
		this.getRegistryVO().setUserToken(getBbbCatalogTools()
				.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
				.get(0));
		this.getRegistryVO().setServiceName(this.getImportRegServiceName());
		this.logDebug("siteId: " + this.getRegistryVO().getSiteId());
		this.logDebug("userToken: " + this.getRegistryVO().getUserToken());
		this.logDebug("ServiceName: " + this.getImportRegServiceName());
	}

	/**
	 * validate input field from the request.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true, if successful
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public boolean preImportRegistry(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws BBBSystemException, BBBBusinessException {

		this.logDebug("GiftRegistryFormHandler.preImportRegistry() method starts");
		if (BBBUtility.isEmpty(this.getRegistryPassword())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry empty password from preImportRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1051));

			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.EMPTY_PASSWORD_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.EMPTY_PASSWORD_EXCEPTION));
			this.setImportErrorMessage(BBBGiftRegistryConstants.EMPTY_PASSWORD_EXCEPTION);
			return false;
		}
		if (!BBBUtility.isValidateRegistryId(this.getRegistryId())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry invalid registry ID from preImportRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1051));

			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.INVALID_REGISTRY_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null)));
			this.setImportErrorMessage(BBBGiftRegistryConstants.INVALID_REGISTRY_EXCEPTION);
			return false;
		}
		if (BBBUtility.isEmpty(this.getImportEventDate())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry invalid import event date from preImportRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1051));

			this.addFormException(new DropletException(this.getLblTxtTemplateManager().getErrMsg(
					BBBGiftRegistryConstants.ERR_INVALID_EVENT_DATE_IMPORT_REGISTRY, pRequest.getLocale().getLanguage(),
					null, null)));
			this.setImportErrorMessage(BBBGiftRegistryConstants.ERR_INVALID_EVENT_DATE_IMPORT_REGISTRY);
			return false;
		}
		if (BBBUtility.isEmpty(this.getImportEventType())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry invalid import event type from preImportRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1051));

			this.addFormException(new DropletException(this.getLblTxtTemplateManager().getErrMsg(
					BBBGiftRegistryConstants.ERR_INVALID_EVENT_TYPE_IMPORT_REGISTRY, pRequest.getLocale().getLanguage(),
					null, null)));
			this.setImportErrorMessage(BBBGiftRegistryConstants.ERR_INVALID_EVENT_TYPE_IMPORT_REGISTRY);
			return false;
		}
		this.logDebug("GiftRegistryFormHandler.preImportRegistry() method ends");

		return true;
	}

	/**
	 * method is invoked through an ajax call from the JSP
	 * page.preforgetRegistryPassword is used to to perform input validations
	 * from the forget password form and generates form errors in case of any
	 * error problem. cases When all the necessary validations are successful,
	 * method will return iNoError true to the jsp popup
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */
	public boolean handleforgetRegistryPassword(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		this.logDebug("GiftRegistryFormHandler.handleforgetRegistryPassword() method starts");

		boolean isNoError = true;
		String errorVal = this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.FORGET_PASSWORD_EXCEPTION,
				pRequest.getLocale().getLanguage(), null, null);
		if (StringUtils.isEmpty(errorVal)) {
			errorVal = BBBGiftRegistryConstants.NO_DATA;
		}
		if ((this.getCheckForValidSession() && !this.isValidSession(pRequest))) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Session expired exception from handleforgetRegistryPassword of GiftRegistryFormhandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1059));
			this.addFormException(new DropletException(BBBGiftRegistryConstants.SESSION_EXCEPTION,
					BBBGiftRegistryConstants.SESSION_EXCEPTION));
			isNoError = true;
		}
			try {

				if (this.preForgetRegistryPassword(pRequest, pResponse)) {
					this.getForgetRegPassRequestVO().setForgetPassRegistryId(this.getForgetPasswordRegistryId());

					final String siteId = this.getSiteContext().getSite().getId();
					this.getForgetRegPassRequestVO().setSiteFlag(siteId);

					this.getForgetRegPassRequestVO().setSiteFlag(getBbbCatalogTools()
							.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
					this.getForgetRegPassRequestVO()
							.setUserToken(getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
									BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
					this.getForgetRegPassRequestVO().setServiceName(this.getForgetRegistryPasswordServiceName());
					this.logDebug("siteId: " + this.getForgetRegPassRequestVO().getSiteFlag());
					this.logDebug("userToken: " + this.getForgetRegPassRequestVO().getUserToken());
					this.logDebug("ServiceName: " + this.getForgetRegistryPasswordServiceName());
					final RegistryResVO forgetRegistryPass = this.getGiftRegistryManager()
							.forgetRegPasswordService(this.getForgetRegPassRequestVO());
					if (!forgetRegistryPass.getServiceErrorVO().isErrorExists()) {
						isNoError = true;
					} else {
						errorVal = this.populateForgetRegPassError(pRequest,
								errorVal, forgetRegistryPass);
						this.addFormException(new DropletException(errorVal, "err_forgot_registry_password"));
						this.setImportErrorMessage(errorVal);
						isNoError = false;
					}
				}
				this.logDebug("GiftRegistryFormHandler.handleforgetRegistryPassword() method ends");

			} catch (final BBBSystemException bbbsysexp) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"FORGET_PASSWORD_EXCEPTION BBBSystemException from handleforgetRegistryPassword of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1010), bbbsysexp);
				this.addFormException(new DropletException(errorVal, errorVal));
				this.setImportErrorMessage(errorVal);
				isNoError = false;
				return isNoError;
			}

			catch (final BBBBusinessException bbbbexp) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"FORGET_PASSWORD_EXCEPTION BBBBusinessException from handleforgetRegistryPassword of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1010), bbbbexp);

				if (bbbbexp.getMessage().equals(BBBCoreConstants.PASSWORD_NOT_MATCH)) {

					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"GiftRegistry Incorrect password from handleforgetRegistryPassword of GiftRegistryFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1050), bbbbexp);

					this.addFormException(new DropletException(
							this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.PASSWORD_EXCEPTION,
									pRequest.getLocale().getLanguage(), null, null),
							BBBGiftRegistryConstants.PASSWORD_EXCEPTION));
					this.setImportErrorMessage(
							this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.PASSWORD_EXCEPTION,
									pRequest.getLocale().getLanguage(), null, null));
					isNoError = false;
				} else {
					this.addFormException(new DropletException(errorVal, errorVal));
					isNoError = false;
					this.setImportErrorMessage(errorVal);
					return isNoError;
				}

			}

		return isNoError;
	}

	/**
	 * @param pRequest
	 * @param errorVal
	 * @param forgetRegistryPass
	 * @return
	 */
	private String populateForgetRegPassError(
			final DynamoHttpServletRequest pRequest, String errorVal,
			final RegistryResVO forgetRegistryPass) {
		String errorValues = errorVal;
		if (!BBBUtility.isEmpty(forgetRegistryPass.getServiceErrorVO().getErrorDisplayMessage())
				&& (forgetRegistryPass.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR))// Technical
		// Error
		{
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Fatal error from handleAddItemToGiftRegistry() of GiftRegistryFormHandler | webservice error code="
							+ forgetRegistryPass.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10107));
			errorValues = this.getLblTxtTemplateManager().getErrMsg(
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
					pRequest.getLocale().getLanguage(), null, null);
			this.addFormException(new DropletException(errorValues,
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
		}
		if (!BBBUtility.isEmpty(forgetRegistryPass.getServiceErrorVO().getErrorDisplayMessage())
				&& (forgetRegistryPass.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN))// Technical
		// Error
		{
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"Either user token or site flag invalid from handleforgetRegistryPassword of GiftRegistryFormHandler Webservice code ="
							+ forgetRegistryPass.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10112));
			errorValues = this.getLblTxtTemplateManager().getErrMsg(
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
					pRequest.getLocale().getLanguage(), null, null);
			this.addFormException(new DropletException(errorValues,
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));

		}
		if (!BBBUtility.isEmpty(forgetRegistryPass.getServiceErrorVO().getErrorDisplayMessage())
				&& (forgetRegistryPass.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT))// Technical
		// Error
		{

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry input fields format error from handleAddItemToGiftRegistry() of "
							+ WEB_SERVICE_ERROR_CODE + forgetRegistryPass.getServiceErrorVO().getErrorId(),
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

			errorValues = this.getLblTxtTemplateManager().getErrMsg(
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
					pRequest.getLocale().getLanguage(), null, null);
			this.addFormException(new DropletException(errorValues,
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
		}
		return errorValues;
	}

	/**
	 * validate input field from the request.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true, if successful
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public boolean preForgetRegistryPassword(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws BBBSystemException, BBBBusinessException {

		this.logDebug("GiftRegistryFormHandler.preForgetRegistryPassword() method starts");
		if (!BBBUtility.isValidateRegistryId(this.getForgetPasswordRegistryId())) {

			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"GiftRegistry Invalid registryId from preForgetRegistryPassword of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1048));

			this.addFormException(new DropletException(
					this.getLblTxtTemplateManager().getErrMsg(BBBGiftRegistryConstants.INVALID_REGISTRY_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.INVALID_REGISTRY_EXCEPTION));
			return false;
		}
		this.logDebug("GiftRegistryFormHandler.preForgetRegistryPassword() method ends");

		return true;
	}

	/**
	 * This method is used to switch between category & price.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true / false
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public boolean handleSwitchCategoryPrice(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException, BBBSystemException {

		this.logDebug("GiftRegistryFormHandler.handleSwitchCategoryPrice() method start");

		return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
	}

	/**
	 * Total price.
	 *
	 * @param pQuantity
	 *            the quantity
	 * @param price
	 *            the price
	 * @return the string
	 */
	public String totalPrice(final String pQuantity, String pPrice) {
		this.logDebug("GiftRegistryTools.totalPrice() method start");

		double priceTotal = 0;
		String price = pPrice;
		if (!StringUtils.isEmpty(pQuantity) && !StringUtils.isEmpty(price)) {
			if (price.startsWith("$")) {
				price = price.substring(1);
			}
			priceTotal = Integer.parseInt(pQuantity) * Double.parseDouble(price);

		}
		this.logDebug("GiftRegistryTools.totalPrice() method ends");

		return "" + priceTotal;

	}

	/**
	 * This handle method is used to move all wish list items into an existing registry.
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked" })
	public boolean handleMoveAllWishListItemsToRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		this.logDebug("GiftRegistryFormHandler ::handleMoveAllWishListItemsToRegistry method starts");
		this.logDebug(
				"GiftRegistryFormHandler ::handleMoveAllWishListItemsToRegistry getting wish list for profile with email "
						+ this.getProfile().getPropertyValue("email"));
		final Map<String, String> error = new HashMap<String, String>();
		final WishListVO wishListItems = this.getWishlistManager().getWishListItems();
		if (!StringUtils.isEmpty(this.getRegistryId())) {
			if (wishListItems != null) {
				final List<GiftListVO> itemArray = wishListItems.getWishListItems();
				if (itemArray != null) {
					for (final GiftListVO item : itemArray) {
						if (item != null) {
							String giftListItemId = null;
							giftListItemId = item.getWishListItemId();
							this.logDebug(
									"GiftRegistryFormHandler ::handleMoveAllWishListItemsToRegistry move wishlist item Id: "
											+ giftListItemId + "to registry");
							this.setWishListItemId(giftListItemId);
							this.handleMoveWishListItemToRegistry(pRequest, pResponse);
							if (this.getFormError()) {
								error.put(giftListItemId, this.getFormExceptions().get(0).toString());
								this.getFormExceptions().clear();
							}
						}
					}
					this.setMoveAllWishListItemsFailureResult(error);
				}
			}
		} else {
			this.logDebug("Input registry id is null or empty");
			this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID));
		}
		this.logDebug("GiftRegistryFormHandler ::handleMoveAllWishListItemsToRegistry method ends");
		return false;
	}

	/**
	 * This handle method is used to move a particular wish list item into an existing registry.
	 *
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleMoveWishListItemToRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		final String giftListId = ((RepositoryItem) this.getProfile().getPropertyValue(WISH_LIST)).getRepositoryId();
		this.logDebug("handleMoveWishListItemToRegistry: Wish List id associated with the profile " + giftListId
				+ " registry Id to move wish list item to " + this.getRegistryId());
		if (!this.preMoveWishListItemToRegistry()) {
			try {
				final RepositoryItem wishListItem = this.getGiftListManager().getGiftitem(this.getWishListItemId());
				if (wishListItem != null) {
					final AddItemsBean addItemsBean = this.getGiftRegistryManager().getGiftRegistryTools()
							.populateRegistryItemWIthWishListItem(wishListItem);
					addItemsBean.setRegistryId(this.getRegistryId());
					final List<AddItemsBean> additemList = new ArrayList<AddItemsBean>();
					additemList.add(addItemsBean);
					final GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
					giftRegistryViewBean.setAdditem(additemList);
					final String siteId = this.getSiteContext().getSite().getId();

					giftRegistryViewBean.setRegistryId(this.getRegistryId());

					giftRegistryViewBean.setSiteFlag(this.getBbbCatalogTools()
							.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
					giftRegistryViewBean.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(
							BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
					giftRegistryViewBean.setServiceName(this.getAddItemsToRegServiceName());

					this.logDebug("siteFlag: " + giftRegistryViewBean.getSiteFlag());
					this.logDebug("userToken: " + giftRegistryViewBean.getUserToken());
					this.logDebug("ServiceName: " + giftRegistryViewBean.getServiceName());

					giftRegistryViewBean.setRegistrySize(giftRegistryViewBean.getAdditem().size());
					this.getSessionBean().setGiftRegistryViewBean(giftRegistryViewBean);
					this.logDebug(" calling addIem to add item to registry ");
					final boolean errorToAdd = this.addIem(pRequest, pResponse, giftRegistryViewBean, siteId);

					if (!errorToAdd) {
						final String[] listItemIdToRemove = { this.getWishListItemId() };
						this.logDebug(
								"wish list item to remove after moving to registry   " + this.getWishListItemId());
						this.removeItemsFromGiftlist(pRequest, pResponse, giftListId, listItemIdToRemove);
					} else {
						this.logDebug("error adding item to registry hence not removing item from wish list");
						this.addFormException(
								new DropletException(BBBGiftRegistryConstants.ERR_MOVE_WISHLIST_ITEM_TO_REGISTRY_MSG,
										BBBGiftRegistryConstants.ERR_MOVE_WISHLIST_ITEM_TO_REGISTRY));
					}
				} else {
					this.logDebug("Wish list item is invalid or not available in the repository");
					this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID_MSG,
							BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID));
				}

			} catch (final CommerceException e) {

				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"BBBBusinessException from handleMoveWishListItemToRegistry of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);

				this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM_MSG,
						BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM));
			} catch (final BBBBusinessException e) {

				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"BBBBusinessException from handleMoveWishListItemToRegistry of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004), e);

				this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_CONFIG_VALUE_NOT_FOUND_MSG,
						BBBGiftRegistryConstants.ERR_CONFIG_VALUE_NOT_FOUND));
			} catch (final BBBSystemException e) {

				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"BBBBusinessException from handleMoveWishListItemToRegistry of GiftRegistryFormHandler",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005), e);

				this.addFormException(
						new DropletException(BBBGiftRegistryConstants.ERR_SYSTEM_CONFIG_VALUE_NOT_FOUND_MSG,
								BBBGiftRegistryConstants.ERR_SYSTEM_CONFIG_VALUE_NOT_FOUND));
			}
		}
		return false;
	}
	/**
	 * 
	 * @return true/false
	 */
	private boolean preMoveWishListItemToRegistry() {
		boolean isError = false;
		if (StringUtils.isEmpty(this.getRegistryId())) {
			this.logDebug("Input registry id is null or empty");
			this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getWishListItemId())) {
			this.logDebug("Input wishlist id is null or empty");
			this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_WISHLIST_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_WISHLIST_ID));
			isError = true;
		}
		return isError;
	}

	/**
	 * This method is used to remove a wish list item from wishlist.
	 *
	 * @param pRequest
	 * @param pResponse
	 * @param giftListId
	 * @param listItemIdToRemove
	 * @throws ServletException
	 * @throws IOException
	 * @throws CommerceException
	 */
	protected void removeItemsFromGiftlist(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, final String giftListId, final String[] listItemIdToRemove)
					throws ServletException, IOException, CommerceException {

		final String pGiftlistId = giftListId;
		if (listItemIdToRemove == null) {
			return;
		}

		try {
			for (final String id : listItemIdToRemove) {
				this.logDebug("Wish list item to remove  " + id + "  from wish list id  " + pGiftlistId);
				this.getGiftListManager().removeItemFromGiftlist(pGiftlistId, id);
			}
		} catch (final RepositoryException ex) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"RepositoryException in GiftRegistryFormHandler while removeItemsFromGiftlist : Item not found",
					BBBCoreErrorConstants.ACCOUNT_ERROR_1251), ex);

		} catch (final InvalidGiftTypeException ige) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"InvalidGiftTypeException in GiftRegistryFormHandler while removeItemsFromGiftlist : Item not found",
					BBBCoreErrorConstants.ACCOUNT_ERROR_1251), ige);

		}
	}

	/**
	 * This method is used to add a sku item into registry.
	 *
	 * @param pRequest
	 * @param pResponse
	 * @param giftRegistryViewBean
	 * @param siteId
	 * @return
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean addIem(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse,
			final GiftRegistryViewBean giftRegistryViewBean, final String siteId) {
		try {

			final ValidateAddItemsResVO addItemsResVO = this.getGiftRegistryManager()
					.addItemToGiftRegistry(giftRegistryViewBean);

			if (!addItemsResVO.getServiceErrorVO().isErrorExists()) {
				this.logDebug(" no error exists while adding item to registry ");
				this.setErrorFlagAddItemToRegistry(false);

				// Update session data so that it will be in sync with the
				// registred items on registry flyout.
				final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
						.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
				final Map sessionMap = sessionBean.getValues();
				final RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap
						.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
				if ((registrySummaryVO != null)
						&& registrySummaryVO.getRegistryId().equalsIgnoreCase(giftRegistryViewBean.getRegistryId())) {
					int totalQuantity = 0;

					final List<AddItemsBean> additemList = giftRegistryViewBean.getAdditem();

					for (final AddItemsBean addItemsBean : additemList) {
						totalQuantity += Integer.parseInt(addItemsBean.getQuantity());
					}

					// update quantiry in session
					totalQuantity = totalQuantity + registrySummaryVO.getGiftRegistered();
					registrySummaryVO.setGiftRegistered(totalQuantity);

				} else if ((registrySummaryVO != null)
						&& !registrySummaryVO.getRegistryId().equalsIgnoreCase(giftRegistryViewBean.getRegistryId())) {
					// update the registrysummaryvo in the BBBsessionBean
					final RegistrySummaryVO regSummaryVO = this.getGiftRegistryManager()
							.getRegistryInfo(giftRegistryViewBean.getRegistryId(), siteId);
					sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, regSummaryVO);
				}

			} else {
				this.errorAdditemCertona(pRequest, addItemsResVO);
			}

		} catch (final BBBBusinessException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBBusinessException from Add Item To GiftRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10104), e);
			this.setErrorFlagAddItemToRegistry(true);
			this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_ADDING_ITEM_TO_REGISTRY_MSG,
					BBBGiftRegistryConstants.ERR_ADDING_ITEM_TO_REGISTRY));
		} catch (final BBBSystemException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBSystemException from Add Item To GiftRegistry of GiftRegistryFormHandler",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10104), e);
			this.setErrorFlagAddItemToRegistry(true);
			this.addFormException(new DropletException(BBBGiftRegistryConstants.ERR_ADDING_ITEM_TO_REGISTRY_MSG,
					BBBGiftRegistryConstants.ERR_ADDING_ITEM_TO_REGISTRY));
		}
		return this.isErrorFlagAddItemToRegistry();

	}
	/**
	 * 
	 * @return batchsize
	 */
	public int getUpdateBatchSize() {

		List<String> listKeys = null;
		String batchSizeString = null;
		int batchSize = this.mUpdateBatchSize;
		try {
			listKeys = getBbbCatalogTools().getAllValuesForKey("ContentCatalogKeys", "registry_update_batchsize");
			if (!BBBUtility.isListEmpty(listKeys)) {
				batchSizeString = listKeys.get(0);

				if (batchSizeString != null) {
					batchSize = Integer.parseInt(batchSizeString);
				}
			}
		} catch (final BBBSystemException e) {
			this.logError("BBBSystemException - registry_update_batchsize key not found for site" + e);
		} catch (final BBBBusinessException e) {
			this.logError("BBBSystemException - registry_update_batchsize key not found for site" + e);
		} catch (final NumberFormatException nfe) {
			this.logError("NumberFormatException - registry_update_batchsize value format exception" + nfe);
		}
		return batchSize;
	}

	/**
	 * Return the host path For Mobile get from config key.
	 *
	 * @param pRequest
	 * @return
	 */
	private String getHost(final DynamoHttpServletRequest pRequest) {

		String hostPath = "";
		final String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
		if (BBBUtility.isNotEmpty(channel) && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB)
				|| channel.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))) {
			try {

				final List<String> configValue = getBbbCatalogTools().getAllValuesForKey(
						BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE);
				if (!BBBUtility.isListEmpty(configValue)) {
					hostPath = configValue.get(0);
				}
			} catch (final BBBSystemException e) {
				this.logError(
						"GiftRegistryFormHandler.getHost :: System Exception occured while fetching config value for config key "
								+ BBBCoreConstants.REQUESTDOMAIN_CONFIGURE + "config type "
								+ BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
			} catch (final BBBBusinessException e) {
				this.logError(
						"GiftRegistryFormHandler.getHost :: Business Exception occured while fetching config value for config key "
								+ BBBCoreConstants.REQUESTDOMAIN_CONFIGURE + "config type "
								+ BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
			}
		} else {
			hostPath = pRequest.getServerName();
		}
		return hostPath;
	}

	private boolean isRegistryOwnedByProfile(final String registryId) throws BBBBusinessException, BBBSystemException {
		final String profileId = this.getProfile().getRepositoryId();
		final String siteId = this.getCurrentSiteId();
		return getGiftRegistryManager().isRegistryOwnedByProfile(profileId, registryId, siteId);
	}

	/**
	 * This method is used to copy a guest registry with items and quantity into
	 * a user's Gift Registry.
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true / false
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("rawtypes")
	public boolean handleCopyRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		final GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
		// BBBH-391 | Client DOM XSRF changes
		String siteId = SiteContextManager.getCurrentSiteId();
		if (StringUtils.isNotEmpty(getFromPage())) {
			setSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
			setErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));
		}
		try {
			giftRegistryViewBean
					.setSiteFlag(getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
							getSiteContext().getSite().getId()).get(0));
			giftRegistryViewBean.setUserToken(getBbbCatalogTools()
					.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
					.get(0));
			giftRegistryViewBean.setServiceName(getCopyRegistryServiceName());
			giftRegistryViewBean.setSourceRegistry(getSrcRegistryId());
			if (getSessionBean() != null) {
				final Map sessionMap = getSessionBean().getValues();
				if (sessionMap != null) {
					RegistrySummaryVO pRegSummaryVO = (RegistrySummaryVO) sessionMap
							.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
					if (pRegSummaryVO != null) {
						RegistryTypes regType = pRegSummaryVO.getRegistryType();
						if (regType != null) {
							giftRegistryViewBean.setTargetRegistry(pRegSummaryVO.getRegistryId());
							giftRegistryViewBean.setRegistryName(regType.getRegistryTypeDesc());
							RegCopyResVO resCopyVo = getGiftRegistryManager().copyRegistry(giftRegistryViewBean);
							giftRegistryViewBean.setCopyRegErr(resCopyVo.hasError());
							giftRegistryViewBean.setTotQtySrcReg(resCopyVo.gettotalNumOfItemsCopied());
							int totQty = giftRegistryViewBean.getTotQuantity();
							int totSrcRegQty = Integer.valueOf(resCopyVo.gettotalNumOfItemsCopied());
							getSessionBean().setGiftRegistryViewBean(giftRegistryViewBean);
							// update quantiry in session
							int totalQuantity = totSrcRegQty + pRegSummaryVO.getGiftRegistered();
							pRegSummaryVO.setGiftRegistered(totalQuantity);
							// BBBI - Interactive Checklist .On add/ update/remove item update the ribbon to the registry if it is guide
			                getSessionBean().setActivateGuideInRegistryRibbon(false);
							if (!resCopyVo.hasError()) {
								getSessionBean().getValues().put(giftRegistryViewBean.getTargetRegistry() + REG_SUMMARY_KEY_CONST, null);
							}
						}
					}
				}
			}
		} catch (BBBSystemException e) {
			logError(e.getMessage(), e);
		} catch (BBBBusinessException e) {
			logError(e.getMessage(), e);
		}

		return checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
	}

	/**
	 * This method is used to Generate the PDF document with Invtation cards.
	 *
	 *
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return true / false
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public boolean handlePrintInvitationCards(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {

		String imageHost = getGiftRegistryManager()
				.getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.PRINT_CARDS_AT_HOME_IMAGES_HOST);
		if (!StringUtils.isEmpty(imageHost) && !StringUtils.isEmpty(getHtmlMessage())) {
			setHtmlMessage(getHtmlMessage().replaceAll("/_assets", imageHost + "/_assets"));
		}
		getGiftRegistryManager().generatePDFDocument(pRequest, pResponse,
				StringEscapeUtils.unescapeHtml(getHtmlMessage()), isDownloadFlag());
		return checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
	}

	/**
	 *
	 * Used to block/unblock the Recommender for Recommending to a registry. The
	 * required parameters are registryId, recommenderProfileId and the
	 * requestedFlag. The requestedFlag is either 'block'/'unblock'.
	 *
	 * @param pRequest
	 * @param pResponse
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean handleToggleBlockRecommender(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
		this.logDebug("GiftRegistryFormHandler.handleToggleBlockRecommender() method starts");
		boolean result = false;
		this.setRegErrorMap(new HashMap<String, String>());
		Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		// BBBH-391 | Client DOM XSRF changes
		String siteId = getSiteContext().getSite().getId();
		if (!BBBUtility.siteIsTbs(siteId)
				&& StringUtils.isNotEmpty(getFromPage())) {
			StringBuilder appendData = new StringBuilder(BBBCoreConstants.BLANK);
			if(StringUtils.isNotEmpty(getQueryParam())){
				appendData.append(BBBCoreConstants.QUESTION_MARK).append(getQueryParam());
			}
			StringBuilder successURL = new StringBuilder(BBBCoreConstants.BLANK);
			StringBuilder errorURL = new StringBuilder(BBBCoreConstants.BLANK);
			successURL
					.append(pRequest.getContextPath())
					.append(getSuccessUrlMap().get(getFromPage()))
					.append(appendData);
			errorURL.append(pRequest.getContextPath())
					.append(getErrorUrlMap().get(getFromPage()))
					.append(appendData);

			setSuccessURL(successURL.toString());
			setErrorURL(errorURL.toString());
		}

		if(!profile.isTransient()){
			try {
				result = this.getGiftRegistryRecommendationManager().persistToggleRecommenderStatus(getRegistryId(),
						getRecommenderProfileId(), getRequestedFlag());
				if (!result) {
					String errorMessage = this.getLblTxtTemplateManager().getErrMsg(
							BBBGiftRegistryConstants.TOGGLE_PERSIST_ERROR_KEY, pRequest.getLocale().getLanguage(), null,
							null);
					getRegErrorMap().put(BBBGiftRegistryConstants.TOGGLE_PERSIST_ERROR, errorMessage);
					addFormException(new DropletException(errorMessage));
					logError(errorMessage);
				}
			} catch (BBBSystemException e) {
				String errorMessage = this.getLblTxtTemplateManager().getErrMsg(
						BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION, pRequest.getLocale().getLanguage(), null,
						null);
				getRegErrorMap().put(BBBGiftRegistryConstants.TOGGLE_PERSIST_ERROR, errorMessage);
				this.addFormException(new DropletException(errorMessage));
				logError("Unable to get the registry recommendations.", e);
			}

			this.logDebug("GiftRegistryFormHandler.handleToggleBlockRecommender() method ends");
		} else {
			String errorMessage = this.getLblTxtTemplateManager().getErrMsg(
					BBBGiftRegistryConstants.ILLEGAL_TOGGLE_REQUEST_KEY, pRequest.getLocale().getLanguage(), null,
					null);
			getRegErrorMap().put(BBBGiftRegistryConstants.TOGGLE_PERSIST_ERROR, errorMessage);
			this.addFormException(new DropletException(errorMessage));
			logError("Illegal Request is received. Non-Logged in user has requested to " + getRequestedFlag()
					+ " the recommender with profileId:-" + getRecommenderProfileId() + " for registryId:-" + getRegistryId());
		}
		return this.checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
	}
	
	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return true/false
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean handleUndoRedo(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws IOException, ServletException {
		boolean result = false;
		Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		if (!profile.isTransient()) {
			if (!(this.getUndoFrom().isEmpty() || this.getRegistryId().isEmpty())) {
				String regsitryId = this.getRegistryId();
				boolean validations = true;
				if (!BBBUtility.isValidateRegistryId(regsitryId)) {
					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"BBBBusinessException from handleUndoRedo of GiftRegistryFormHandler"));
					validations = false;
				}
				if (!validations) {
					GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
					try {
						this.isRegistryOwnedByProfile(regsitryId);
						this.preRemoveItemFromGiftRegistry(pRequest, pResponse);
						if (!this.getFormError()) {
							this.setRegistryFlags(giftRegistryViewBean);
							this.populateGiftRegistryRemove(giftRegistryViewBean);
							result = this.getGiftRegistryRecommendationManager().performUndo(giftRegistryViewBean,
									getUndoFrom());
							if (!result) {
								this.updateSessionAfterRemovingFromRegistry(pRequest, pResponse, giftRegistryViewBean);
							} else {
								this.addFormException(new DropletException(""));
							}
						}
					} catch (BBBSystemException e) {
						logError(e.getMessage(), e);
					} catch (BBBBusinessException e) {
						logError(e.getMessage(), e);
					}
				}
			} else {
				this.addFormException(new DropletException("Illegal Parameters."));
			}
		} else {
			logDebug("Non-logged in user.");
		}

		return this.checkFormRedirect(this.getUndoSuccessURL(), this.getUndoFailureURL(), pRequest, pResponse);
	}
	
	/**
	 * 
	 * @param giftRegistryViewBean
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void setRegistryFlags(GiftRegistryViewBean giftRegistryViewBean)
			throws BBBSystemException, BBBBusinessException {
		String siteId = this.getSiteContext().getSite().getId();
		giftRegistryViewBean.setSiteFlag(
				getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
		giftRegistryViewBean.setUserToken(getBbbCatalogTools()
				.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY, BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
				.get(0));
		giftRegistryViewBean.setServiceName(this.getAddItemsToRegServiceName());
		this.logDebug("siteFlag: " + giftRegistryViewBean.getSiteFlag());
		this.logDebug("userToken: " + giftRegistryViewBean.getUserToken());
		this.logDebug("ServiceName: " + this.getAddItemsToRegServiceName());
	}
	
	/**
	 * 
	 * @param giftRegistryViewBean
	 */
	private void populateGiftRegistryRemove(GiftRegistryViewBean giftRegistryViewBean) {
		String pSku = this.getSkuId();
		String prodId = this.getProductId();
		String itemRowId = (String) this.getValue().get(BBBCoreConstants.ROWID);
		giftRegistryViewBean.setProductId(prodId);
		giftRegistryViewBean.setRegistryId(this.getRegistryId());
		giftRegistryViewBean.setRowId(itemRowId);
		// Set quantity =0 for removing an item
		giftRegistryViewBean.setQuantity(String.valueOf(BBBCoreConstants.ZERO));
		giftRegistryViewBean.setSku(pSku);
	}
	
	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param giftRegistryViewBean
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private void updateSessionAfterRemovingFromRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, GiftRegistryViewBean giftRegistryViewBean)
					throws BBBBusinessException, BBBSystemException {
		String siteId = this.getSiteContext().getSite().getId();
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		Map sessionMap = sessionBean.getValues();
		RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap
				.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
		if ((registrySummaryVO != null) && registrySummaryVO.getRegistryId().equalsIgnoreCase(this.getRegistryId())) {
			int quantity = Integer.valueOf(this.getQuantity());
			// update quantiry in session
			registrySummaryVO.setGiftRegistered(registrySummaryVO.getGiftRegistered() - quantity);
		} else if ((registrySummaryVO != null)
				&& !registrySummaryVO.getRegistryId().equalsIgnoreCase(this.getRegistryId())) {
			// update the registrysummaryvo in the BBBsessionBean
			final RegistrySummaryVO regSummaryVO = getGiftRegistryManager()
					.getRegistryInfo(giftRegistryViewBean.getRegistryId(), siteId);
			sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, regSummaryVO);
		}
	}
	
	/**
	 * 
	 * @param siteId
	 * @param profileItem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean validateSite(String siteId, RepositoryItem profileItem) {
		Map<String, RepositoryItem> sites = (Map<String, RepositoryItem>) profileItem
				.getPropertyValue(USER_SITE_ITEMS);
		if (sites != null && !sites.isEmpty()) {
			for (Entry<String, RepositoryItem> site : sites.entrySet()) {
				if ((((String) ((RepositoryItem) sites.get(site.getKey())).getPropertyValue(BBBCoreConstants.SITE_ID)))
						.equalsIgnoreCase(siteId))
					return true;
			}
		}
		return false;
	}
}