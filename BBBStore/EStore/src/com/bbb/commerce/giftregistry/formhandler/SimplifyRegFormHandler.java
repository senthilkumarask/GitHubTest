package com.bbb.commerce.giftregistry.formhandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import nl.captcha.Captcha;
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
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
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
public class SimplifyRegFormHandler extends GiftRegistryFormHandlerBean {

	private ItemDetailsVO itemDetailsVO;
	private String mPassword;
	
	/**
	 * @return the password
	 */
	public String getPassword() {
		return mPassword;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		mPassword = password;
	}
	
	/**
	 * 
	 * @return itemDetailsVO
	 */
	public ItemDetailsVO getItemDetailsVO() {
		return itemDetailsVO;
	}
	/**
	 * 
	 * @param pItemDetailsVO the pItemDetailsVO to set
	 */
	public void setItemDetailsVO(ItemDetailsVO pItemDetailsVO) {
		itemDetailsVO = pItemDetailsVO;
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
	public final void handleRegistryTypes(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug(" handleRegistryTypes(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		if (this.getSessionBean().getRegistryTypesEvent() != null) {
			this.getSessionBean().setRegistryTypesEvent(null);
		}
		this.getSessionBean().setRegistryTypesEvent(this.getRegistryEventType());

		final MutableRepositoryItem userProfile = (MutableRepositoryItem) ServletUtil
				.getCurrentUserProfile();
		final String loginFrom = BBBWebServiceConstants.TXT_LOGIN_CREATE_REGISTRY;

		userProfile.setPropertyValue(BBBWebServiceConstants.LOGIN_FROM,
				loginFrom);

		this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest,
				pResponse);
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
	public final void handleLoginToManageActiveRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		BBBPerformanceMonitor.start( SimplifyRegFormHandler.class.getName() + " : " + "handleLoginToManageActiveRegistry");
		this.logDebug(" handleLoginToManageActiveRegistry(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
	
		
		this.getSessionBean().setMngActRegistry(BBBGiftRegistryConstants.LOGIN_TO_MANAGE_ACTIVE_REGISTRY);
		final MutableRepositoryItem userProfile = (MutableRepositoryItem) ServletUtil
				.getCurrentUserProfile();
		final String loginFrom = BBBWebServiceConstants.TXT_LOGIN_CREATE_REGISTRY;

		userProfile.setPropertyValue(BBBWebServiceConstants.LOGIN_FROM,
				loginFrom);

		this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest,
				pResponse);		

		this.logDebug("handleLoginToManageActiveRegistry(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");
		
		BBBPerformanceMonitor.end( SimplifyRegFormHandler.class.getName() + " : " + "handleLoginToManageActiveRegistry");
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
	public final void handleViewManageRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException,BBBBusinessException,BBBSystemException {

		this.logDebug("handleViewManageRegistry(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		String registryId = null;
		String registryEventType = null;
		final String siteId = this.getCurrentSiteId();
		
		if (null != this.getRegistryIdEventType()) {
			final String[] registryIdEventType = this.getRegistryIdEventType()
					.split("_");

			registryId = registryIdEventType[0];
			registryEventType = registryIdEventType[1];
			
			final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
					.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
			
			final RegistrySummaryVO regSummaryVO = this
					.getGiftRegistryManager().getRegistryInfo(
							registryId, siteId);
			sessionBean.getValues().put(
					BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY,
					regSummaryVO);
		}

		this.setSuccessURL(this.getSuccessURL() + REGISTRY_ID + registryId
				+ EVENT_TYPE + registryEventType);

		this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest,
				pResponse);
		this.logDebug("handleViewManageRegistry(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");

	}
	
	
	/**
	 * This method is used to view and edit active registry Link.
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws ServletException
	 *            the servlet exception
	 * @throws IOException
	 *            Signals that an I/O exception has occurred.
	 * @throws BBBBusinessException
	 *            Signals that an BBBBusinessException has occurred.
	 * @throws BBBSystemException
	 *            Signals that an BBBSystemException has occurred.
	 */
	public final void handleViewEditRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException,BBBBusinessException,BBBSystemException {
		
		this.logDebug("handleViewEditRegistry(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		String registryId = (String)pRequest.getParameter("registryId");
		String registryEventType = (String)pRequest.getParameter("eventType");
		
		final String siteId = this.getCurrentSiteId();
		
		if (null != registryEventType) {
				
			final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
					.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
			
			/* getting the registry info for the registry id to be viewed or edited and
			   placing the new registry info into the session */
			
			final RegistrySummaryVO regSummaryVO = this
					.getGiftRegistryManager().getRegistryInfo(
							registryId, siteId);
			sessionBean.getValues().put(
					BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY,
					regSummaryVO);
		}
        	
		this.setSuccessURL(this.getViewEditSuccessURL() + REGISTRY_ID + registryId
				+ EVENT_TYPE + registryEventType);
		
		this.setErrorURL(this.getViewEditFailureURL());

		this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest,
				pResponse);
		
		this.logDebug("handleViewManageRegistry(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");

	}

	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 */
	private void preGiftCreateUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse){
		
		  final Dictionary<String, String> value = this.getValue();

			String firstName=this.getRegistryVO().getPrimaryRegistrant().getFirstName();
			String lastName=this.getRegistryVO().getPrimaryRegistrant().getLastName();

	      value.put(this.getPropertyManager().getLoginPropertyName(), getEmail().toLowerCase());
	      value.put(this.getPropertyManager().getEmailAddressPropertyName(), getEmail().toLowerCase());
	      value.put(this.getPropertyManager().getAutoLoginPropertyName(), BBBCoreConstants.TRUE);
	      this.setValueProperty(this.getPropertyManager().getReceiveEmailPropertyName(), BBBCoreConstants.YES);
	      if(isShallowProfileChanges()){
	          	value.put(this.getPropertyManager().getStatusPropertyName() , BBBCoreConstants.FULL_PROFILE_STATUS_VALUE);        	
	      }
	      value.put(this.getPropertyManager().getPasswordPropertyName(), getPassword());
	      value.put(this.getPropertyManager().getConfirmPasswordPropertyName(),getPassword());
	      value.put(this.getPropertyManager().getFirstNamePropertyName(),firstName);
	      value.put(this.getPropertyManager().getLastNamePropertyName(),lastName);
	       
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

	public final boolean handleCreateRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException {

		this.logDebug("SimplifyRegFormHandler.handleCreateRegistry() method start");
		final RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
	        final String myHandleMethod = Thread.currentThread().getStackTrace()[1].getMethodName();
	        if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
		boolean isFromThirdParty = false;
		if(pRequest.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST)!=null){
			isFromThirdParty = Boolean.parseBoolean(pRequest.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST));
		}
		String clientID = pRequest.getHeader(BBBGiftRegistryConstants.CLIENTID);
		
		final String emailId = getEmail();
    	indirectRequest = true;

    	if(getProfile().isTransient()){
			preGiftCreateUser(pRequest,pResponse);
		    if(getSessionBean().isRegistredUser()){
		    	 super.handleLoginUser(pRequest, pResponse);
		    }
		    else {
		    	try{
			     super.handleCreate(pRequest, pResponse);
		    	}
		    	catch(Exception e){
		    		logError(e.getMessage());
		    	}
		    }
		}
		if(getFormError() && getFormExceptions().size()>0){
		  return checkFormRedirect(this.getVerifyUserSuccessURLPage(), this.getVerifyUserErrorURLPage(),
					pRequest, pResponse);
		}
		
		if(BBBCoreConstants.DEFAULT_CHANNEL_VALUE.equalsIgnoreCase(BBBUtility
				.getChannel()) && !isFromThirdParty) {
			setSuccessURL(getRegistryCreationSuccessURL());
			setErrorURL(getRegistryCreationErrorURL());
		}
		pRequest.getParameter("favStoreId");
		this.logDebug("GiftRegistryFormHandler.handleCreateRegistry()  College Value"
				+ this.getRegistryVO().getEvent().getCollege());
		final Profile profile = (Profile) pRequest.resolveName(ComponentName
				.getComponentName(BBBCoreConstants.ATG_PROFILE));
		this.getSessionBean()
				.setRegistryTypesEvent(this.getRegistryEventType());
		
		//adding bride/groom details.
	    if(this.getRegBG()!=null && !this.getRegBG().isEmpty()){
    		this.getRegistryVO().setRegBG(this.getRegBG());
    	}
    	if(this.getCoRegBG()!=null && !this.getCoRegBG().isEmpty()){
    		this.getRegistryVO().setCoRegBG(this.getCoRegBG());
    	}
		
    	this.populatePrimaryRegistrant(emailId);
	    this.populateRegistryVoShipping();
	    
	    //Setting the value for AFFILIATE_TAG column
	    this.setAffiliateTag(clientID);

	    // if user is transient then redirect to loggedInFailureURL
	    if((profile.isTransient() && isFromThirdParty) || !profile.isTransient() ){
			try {
				this.preCreateRegistry(pRequest, pResponse);
				if (getFormError()) {
					return this.checkFormRedirect(this.getSuccessURL(),
							this.getErrorURL(), pRequest, pResponse);
				}
				final String siteId = this.getCurrentSiteId();
				this.setWSDLParameters(siteId);
				this.logDebug("siteFlag: "
						+ this.getRegistryVO().getSiteId());
				this.logDebug("userToken: "
						+ this.getRegistryVO().getUserToken());
				this.logDebug("ServiceName: "
						+ this.getCreateRegistryServiceName());
		
				if (null != this.getSessionBean().getRegistryTypesEvent()) {
					this.getRegistryVO()
							.getRegistryType()
							.setRegistryTypeName(
									this.getSessionBean()
											.getRegistryTypesEvent());
				}
				// Web Service call to create gift registry which will return
				// registryId
				this.setProfileItem(isFromThirdParty, emailId, pRequest );

				// Set the registry status
				this.getRegistryVO()
						.setStatus(
								this.getGiftRegistryManager()
										.getGiftRegistryConfigurationByKey(
												BBBGiftRegistryConstants.GIFT_REGISTRY_ACTIVE_STATUS_CONFIG_KEY));
				// newly added fields
				this.getRegistryVO().setNetworkAffiliation(
						BBBUtility.netWorkFlagValue(this.getRegistryVO()
								.getRegistryType().getRegistryTypeName()));

				//PS-21422 defect fixed. Set configured default store id in preference store number in case when no 
				// store is selected during registry creation.
				if (BBBUtility.isEmpty(this.getRegistryVO().getPrefStoreNum())) {
					this.getRegistryVO().setPrefStoreNum(
							this.getGiftRegistryManager().getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.REGISTRY_DEFAULT_STORE_ID));
				}
				this.setAffiliation();
				String networkAffiliation = this.getRegistryVO().getNetworkAffiliation();
				if(StringUtils.isEmpty(networkAffiliation) || (networkAffiliation.length()!=1) || 
								!(networkAffiliation.equalsIgnoreCase(BBBCoreConstants.YES_CHAR) || networkAffiliation.equalsIgnoreCase(BBBCoreConstants.NO_CHAR))){
                    this.getRegistryVO().setNetworkAffiliation(
                                  BBBUtility.netWorkFlagValue(this.getRegistryVO()
                                                .getRegistryType().getRegistryTypeName()));
                }

				this.setEventDate(siteId);
				this.setShowerDate(siteId);
				this.setBirthDate(siteId);
				this.setShippingFutureDate(siteId);
				this.getRegistryVO().getPrimaryRegistrant()
						.setAddressSelected(this.getRegContactAddress());

				// If new address is not added to the registry and old shipping
				// or
				// billing address is selected need to set that address to
				// registryVO
				final RegistryVO resVO = this.getGiftRegistryManager()
						.setShippingBillingAddr(this.getRegistryVO(),
								this.getShippingAddress(),
								this.getFutureShippingAddress(),
								this.getProfile(),
								this.getRegistrantAddressFromWS(),
								this.getShippingAddressFromWS(),
								this.getFutureShippingAddressFromWS());

				if (StringUtils.isEmpty(resVO.getEvent().getGuestCount())) {
					resVO.getEvent()
							.setGuestCount(BBBCoreConstants.STRING_ZERO);
				}
				this.setRegistryVO(resVO);
				if(getFormError() && getFormExceptions().size()>0){
					return this.checkFormRedirect(this.getSuccessURL(),
							this.getErrorURL(), pRequest, pResponse);
				}else{
					setSessionObjects(pRequest, siteId);
				}
				// BBBSL-3444 | RKG FloodLight
				setAttributeInRequest(pRequest);
				//
				return this.checkFormRedirect(this.getSuccessURL(),
						this.getErrorURL(), pRequest, pResponse);

			} catch (final BBBBusinessException e) {
				this.logError(
						LogMessageFormatter
								.formatMessage(
										pRequest,
										"BBBBusinessException from handleCreateRegistry of SimplifyRegFormHandler",
										BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),
						e);
				this.addFormException(new DropletException(
						this.getMessageHandler()
								.getErrMsg(
										BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION,
										pRequest.getLocale().getLanguage(),
										null, null),
						BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION));
			}

			catch (final BBBSystemException e) {
				this.logError(
						LogMessageFormatter
								.formatMessage(
										pRequest,
										"BBBSystemException from handleCreateRegistry of SimplifyRegFormHandler",
										BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),
						e);
				this.addFormException(new DropletException(
						this.getMessageHandler()
								.getErrMsg(
										BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION,
										pRequest.getLocale().getLanguage(),
										null, null),
						BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION));

			} catch (final TemplateEmailException e) {
				this.addFormException(new DropletException(
						this.getMessageHandler()
								.getErrMsg(
										BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
										pRequest.getLocale().getLanguage(),
										null, null),
						BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION));
			} catch (final RepositoryException e1) {
				this.logError(
						LogMessageFormatter
								.formatMessage(
										pRequest,
										"Repository Exception from handleCreateRegistry of SimplifyRegFormHandler",
										BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1001),
						e1);

				this.addFormException(new DropletException(
						this.getMessageHandler()
								.getErrMsg(
										BBBGiftRegistryConstants.ERR_REGSEARCH_REPO_EXCEPTION,
										pRequest.getLocale().getLanguage(),
										null, null),
						BBBGiftRegistryConstants.ERR_REGSEARCH_REPO_EXCEPTION));
			} finally {
				if (rrm != null) {
                    rrm.removeRequestEntry(myHandleMethod);
                }
			}
		} else {
			this.setSuccessURL(this.getErrorURL());
		}
	        }
		this.logDebug("SimplifyRegFormHandler.handleCreateRegistry() method ends");

		return this.checkFormRedirect(this.getViewEditSuccessURL(), this.getViewEditSuccessURL(),
				pRequest, pResponse);
	}
	/**
	 * setAttribute for state and zip in request
	 * @param pRequest
	 */
	private void setAttributeInRequest(final DynamoHttpServletRequest pRequest) {
		if(null != this.getRegistryVO().getShipping() && null != this.getRegistryVO().getShipping().getShippingAddress() && 
				BBBUtility.isNotEmpty(this.getRegistryVO().getShipping().getShippingAddress().getState())) {
			pRequest.setAttribute("state", this.getRegistryVO().getShipping().getShippingAddress().getState());
		}
		if(null != this.getRegistryVO().getShipping() && null != this.getRegistryVO().getShipping().getShippingAddress() && 
				BBBUtility.isNotEmpty(this.getRegistryVO().getShipping().getShippingAddress().getZip())) {
			pRequest.setAttribute("zip", this.getRegistryVO().getShipping().getShippingAddress().getZip());
		}
	}
	/**
	 * To set the values of setAffiliateTag in RegistryVO
	 * @param clientID
	 */
	private void setAffiliateTag(String clientID) {
		if(clientID == null){
	    	 this.getRegistryVO().setAffiliateTag(BBBCoreConstants.BBB_DESKTOP); 
	    }else if(clientID.equalsIgnoreCase(BBBCoreConstants.MOBILE_CLIENT)){
	    	 this.getRegistryVO().setAffiliateTag(BBBCoreConstants.BBB_MOBILE); 
	    }else{
	    	this.getRegistryVO().setAffiliateTag(clientID);
	    }
	}
	/**
	 * To set the ShippingAddress and FutureShippingAddress 
	 * 			values in RegistryVO 
	 */
	private void populateRegistryVoShipping() {
		if(this.getShipPoBoxStatus()!=null && this.getShipPoBoxStatus().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
	        this.getRegistryVO().getShipping().getShippingAddress().setQasValidated(true);
	    if(this.getShipPoBoxFlag()!=null && this.getShipPoBoxFlag().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
	    	this.getRegistryVO().getShipping().getShippingAddress().setPoBoxAddress(true);

	    if(this.getFuturePoBoxStatus()!=null && this.getFuturePoBoxStatus().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
	        this.getRegistryVO().getShipping().getFutureshippingAddress().setQasValidated(true);
	    if(this.getFuturePoBoxFlag()!=null && this.getFuturePoBoxFlag().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
	        this.getRegistryVO().getShipping().getFutureshippingAddress().setPoBoxAddress(true);
	}
	/**
	 * To set the values of primaryRegistrant
	 * @param emailId
	 */
	private void populatePrimaryRegistrant(final String emailId) {
		// trimming leading and trailing spaces for email id in create registry
		// webservice.
		if (!StringUtils.isBlank(emailId)) {
			this.getRegistryVO().getPrimaryRegistrant()
					.setEmail(emailId.trim());
		}

	    if(this.getContactPoBoxStatus()!=null && this.getContactPoBoxStatus().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
	        this.getRegistryVO().getPrimaryRegistrant().getContactAddress().setQasValidated(true);
	    if(this.getContactPoBoxFlag()!=null && this.getContactPoBoxFlag().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
	    	this.getRegistryVO().getPrimaryRegistrant().getContactAddress().setPoBoxAddress(true);
	}
	
	/**
	 * 
	 * @param pRequest the Request
	 * @param siteId the siteId
	 * @throws BBBBusinessException Signals that an BBBBusiness exception has occurred 
	 * @throws BBBSystemException Signals that an BBBSystem exception has occurred
	 * @throws RepositoryException Signals that an Repository exception has occurred
	 * @throws TemplateEmailException Signals that an TemplateEmail exception has occurred
	 */
	@SuppressWarnings({ "unchecked" })
	private void setSessionObjects(final DynamoHttpServletRequest pRequest,
			final String siteId) throws BBBBusinessException,
			BBBSystemException, RepositoryException, TemplateEmailException {
		String registryTypeEvent = null;
		RegistryResVO createRegistryResVO = null;
		createRegistryResVO = this.getGiftRegistryManager().createRegistry(
				this.getRegistryVO());

		if (createRegistryResVO == null) {
			final BBBSystemException sysExc = new BBBSystemException(
					"Error while creating Registry");
			throw sysExc;
		} else if (createRegistryResVO.getServiceErrorVO().isErrorExists()) {
			this.createRegistryErrorExist(pRequest, createRegistryResVO);

		} else {
			this.setCookieType(pRequest, registryTypeEvent);
			
			// Add info to gift registry repository about the registrants
			this.getRegistryVO().setRegistryId(
					String.valueOf(createRegistryResVO.getRegistryId()));
			final String coreEmailPopupStatus = this
					.getCoRegEmailFoundPopupStatus();
			this.getGiftRegistryManager().giftRegistryRepoEntry(
					this.getRegistryVO(), coreEmailPopupStatus);

			// Update Registrant profile info
			this.getRegistryVO().getPrimaryRegistrant()
					.setAddressSelected(this.getRegContactAddress());
			this.getGiftRegistryManager().updateRegistrantProfileInfo(
					this.getRegistryVO(),
					this.getShippingAddress(),
					this.getFutureShippingAddress(),
					this.getProfile(),
					(String) this.getSiteContext().getSite()
							.getPropertyValue(DEFAULT_COUNTRY));

			// Send email to coregistrant
			final String coRegistrySubject = this.getMessageHandler()
					.getPageLabel(LBL_EMAIL_CO_REGISTRY_SUBJECT,
							pRequest.getLocale().getLanguage(), null, null);

			String giftRegistryURL = null;
			if(siteId.contains("TBS")){
				giftRegistryURL = getTbsEmailSiteMap().get(siteId) + getGuestRegistryUri();
			} else {
			giftRegistryURL = pRequest.getScheme()
					+ BBBGiftRegistryConstants.SCHEME_APPEND
					+ this.getHost(pRequest)
					+ getGuestRegistryUri()
					+ this.getRegistryVO().getRegistryId() + EVENT_TYPE;
			}
			String accountLoginURL = null;
			if(siteId.contains("TBS")){
				accountLoginURL = getTbsEmailSiteMap().get(siteId) + getLoginRedirectUrl();
			} else {
				accountLoginURL = pRequest.getScheme()
						+ BBBGiftRegistryConstants.SCHEME_APPEND
						+ this.getHost(pRequest)
						+ getLoginRedirectUrl();
			}

			this.getGiftRegistryManager().sendEmailToCoregistrant(
					giftRegistryURL, accountLoginURL, siteId,
					coRegistrySubject, this.getRegistryVO(),
					this.getCoRegEmailFoundPopupStatus(),
					this.getCoRegEmailNotFoundPopupStatus(),
					this.getGiftRegEmailInfo());

			// Invalidate registrylist and summaryVO in the session
			final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
					.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
			
			this.setListUserRegIds(siteId, sessionBean);
			
			if(sessionBean.getValues().get("registrySkinnyVOList")!=null)
			{
				Profile profile = (Profile) pRequest
						.resolveName(BBBCoreConstants.ATG_PROFILE);
				if (!profile.isTransient()) {
					List<RegistrySkinnyVO> registrySkinnyVOList = getGiftRegistryManager()
							.getAcceptableGiftRegistries(profile, siteId);
					RegistryEventDateComparator eventDateComparator = new RegistryEventDateComparator();
					eventDateComparator.setSortOrder(2);
					Collections.sort(registrySkinnyVOList, eventDateComparator);
					sessionBean.getValues().put("registrySkinnyVOList", registrySkinnyVOList);
					sessionBean.getValues().put("size", registrySkinnyVOList.size());
				}
			}

		}
		this.setCreateRegistryResVO(createRegistryResVO);

		if (null != this.getRegistryVO().getRegistryType()
				.getRegistryTypeName()) {
			this.getRegistryVO()
					.getRegistryType()
					.setRegistryTypeDesc(
							this.getBbbCatalogTools().getRegistryTypeName(
									this.getRegistryVO().getRegistryType()
											.getRegistryTypeName(), siteId));
		}
		this.getGiftRegSessionBean().setResponseHolder(this.getRegistryVO());
		this.getGiftRegSessionBean().setRegistryOperation(
				BBBGiftRegistryConstants.GR_CREATE);
	}
	/**
	 * To set List user reg id
	 * 
	 * @param siteId the siteId
	 * @param sessionBean the sessionBean
	 * @throws BBBBusinessException the BBB Business Exception
	 * @throws BBBSystemException the BBB System Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setListUserRegIds(final String siteId,
			final BBBSessionBean sessionBean) throws BBBBusinessException,
			BBBSystemException {
		final HashMap sessionMap = sessionBean.getValues();

		List<String> pListUserRegIds = null;
		pListUserRegIds = (List) sessionMap
				.get(BBBGiftRegistryConstants.USER_REGISTRIES_LIST);
		if (pListUserRegIds != null) {
			pListUserRegIds.add(0,
					String.valueOf(this.getRegistryVO().getRegistryId()));
			sessionBean.getValues().put(
					BBBGiftRegistryConstants.USER_REGISTRIES_LIST,
					pListUserRegIds);
			sessionBean.getValues().put(
					BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST,
					pListUserRegIds);
			// update the registrysummaryvo in the BBBsessionBean
			final RegistrySummaryVO regSummaryVO = this
					.getGiftRegistryManager().getRegistryInfo(
							this.getRegistryVO().getRegistryId(), siteId);
			sessionBean.getValues().put(
					BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY,
					regSummaryVO);
		} else {
			pListUserRegIds = new ArrayList<String>();
			pListUserRegIds.add(0,
					String.valueOf(this.getRegistryVO().getRegistryId()));
			sessionBean.getValues().put(
					BBBGiftRegistryConstants.USER_REGISTRIES_LIST,
					pListUserRegIds);
			sessionBean.getValues().put(
					BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST,
					pListUserRegIds);
			// update the registrysummaryvo in the BBBsessionBean
			final RegistrySummaryVO regSummaryVO = this
					.getGiftRegistryManager().getRegistryInfo(
							this.getRegistryVO().getRegistryId(), siteId);
			sessionBean.getValues().put(
					BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY,
					regSummaryVO);
		}
	}
	/**
	 * To set the cookie type registryVO
	 * 
	 * @param pRequest the pRequest
	 * @param registryTypeEvent the registryTypeEvent
	 */
	private void setCookieType(final DynamoHttpServletRequest pRequest,
			String registryTypeEvent) {
		final Cookie cookies[] = pRequest.getCookies();
		for (final Cookie cookie : cookies) {
			if (cookie.getName().equalsIgnoreCase(
					BBBGiftRegistryConstants.REGISTRY_TYPE_EVENT)) {
				registryTypeEvent = cookie.getValue();
				break;
			}
		}
		if ((registryTypeEvent != null)
				&& (this.getSessionBean().getRegistryTypesEvent() != null)
				&& registryTypeEvent
						.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_WEDDING)) {
			if (this.getSessionBean()
					.getRegistryTypesEvent()
					.equalsIgnoreCase(
							BBBGiftRegistryConstants.EVENT_TYPE_WEDDING)
					|| this.getSessionBean()
							.getRegistryTypesEvent()
							.equalsIgnoreCase(
									BBBGiftRegistryConstants.EVENT_TYPE_CERMONY)) {
				this.getRegistryVO().setCookieType(
						BBBCoreConstants.WED_CHANNEL_REF);
			}
		} else if ((registryTypeEvent != null)
				&& (this.getSessionBean().getRegistryTypesEvent() != null)
				&& registryTypeEvent
						.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_BABY)) {
			if (this.getSessionBean()
					.getRegistryTypesEvent()
					.equalsIgnoreCase(
							BBBGiftRegistryConstants.EVENT_TYPE_BABY)) {
				this.getRegistryVO().setCookieType(
						BBBCoreConstants.THEBUMP_REF);
			}
		}
	}
	/**
	 * 
	 * @param siteId
	 */
	private void setShippingFutureDate(final String siteId) {
		// If future shipping date
		if ((this.getRegistryVO().getShipping() != null)
				&& !StringUtils.isEmpty(this.getRegistryVO().getShipping()
						.getFutureShippingDate())) {
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				this.getRegistryVO()
						.getShipping()
						.setFutureShippingDateWS(
								BBBUtility.convertCADateIntoWSFormat(this
										.getRegistryVO().getShipping()
										.getFutureShippingDate()));
			} else {
				this.getRegistryVO()
						.getShipping()
						.setFutureShippingDateWS(
								BBBUtility.convertDateIntoWSFormat(this
										.getRegistryVO().getShipping()
										.getFutureShippingDate()));
			}
		}
	}
	/**
	 * 
	 * @param siteId
	 */
	private void setBirthDate(final String siteId) {
		if (!StringUtils
				.isEmpty(this.getRegistryVO().getEvent().getBirthDate())) {
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				this.getRegistryVO()
						.getEvent()
						.setBirthDateWS(
								BBBUtility.convertCADateIntoWSFormat(this
										.getRegistryVO().getEvent()
										.getBirthDate()));
			} else {
				this.getRegistryVO()
						.getEvent()
						.setBirthDateWS(
								BBBUtility.convertDateIntoWSFormat(this
										.getRegistryVO().getEvent()
										.getBirthDate()));
			}

		}
	}
	
	/**
	 * 
	 * @param siteId
	 */
	private void setShowerDate(final String siteId) {
		if (!StringUtils.isEmpty(this.getRegistryVO().getEvent()
				.getShowerDate())) {
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				this.getRegistryVO()
						.getEvent()
						.setShowerDateWS(
								BBBUtility.convertCADateIntoWSFormat(this
										.getRegistryVO().getEvent()
										.getShowerDate()));
			} else {
				this.getRegistryVO()
						.getEvent()
						.setShowerDateWS(
								BBBUtility.convertDateIntoWSFormat(this
										.getRegistryVO().getEvent()
										.getShowerDate()));
			}
		}
	}

	private void setAffiliation() {
		this.getRegistryVO().setPrefRegContMeth(
				BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_METHOD);
		this.getRegistryVO().setPrefRegContTime(
				BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_TIME);
		this.getRegistryVO().setPrefCoregContMeth(
				BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_METHOD);
		this.getRegistryVO().setPrefCoregContTime(
				BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_TIME);
		this.getRegistryVO().setSignup(BBBGiftRegistryConstants.SIGN_UP_NO);
		this.getRegistryVO().setHint(BBBGiftRegistryConstants.REGISTRY);
		this.getRegistryVO().setWord(BBBGiftRegistryConstants.WORD);
		if(this.getRegistryVO().getRegistryType().getRegistryTypeName().equalsIgnoreCase("Baby")){
        this.getRegistryVO().setIsPublic("1");
		}
		else{
			 this.getRegistryVO().setIsPublic("0");
		}
		if ((this.getRegistryVO().getOptInWeddingOrBump() == null)
				|| (BBBCoreConstants.FALSE).equalsIgnoreCase(this
						.getRegistryVO().getOptInWeddingOrBump())) {
			this.getRegistryVO().setAffiliateOptIn(
					BBBGiftRegistryConstants.FLAG_N);
		} else {
			this.getRegistryVO().setAffiliateOptIn(
					BBBGiftRegistryConstants.FLAG_Y);
		}
	}
	
	/**
	 * 
	 * @param isFromThirdParty the isFromThirdParty
	 * @param email the email
	 * @param request the request
	 */
	private void setProfileItem(boolean isFromThirdParty, String email,DynamoHttpServletRequest request) {
		MutableRepositoryItem pProfileItem;
		String profileId = request.getParameter(BBBAccountConstants.PROFILE_ID);
		String token = request.getHeader(BBBCoreConstants.CLIENT_ID_PARM);
		String siteHeader = request.getHeader("X-bbb-site-id");
		if ((BBBCoreConstants.TRUE).equalsIgnoreCase(this
				.getCoRegEmailFoundPopupStatus())) {
			// Set CO Registrant profile Id to registryVO
			pProfileItem = this.getGiftRegistryManager()
					.getProfileItemFromEmail(
							this.getRegistryVO().getCoRegistrant().getEmail(),
							this.getRegistryVO().getSiteId());
			this.getRegistryVO().getCoRegistrant()
					.setProfileId(pProfileItem.getRepositoryId());
			this.getRegistryVO().getCoRegistrant()
					.setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_Y);
		} else {
			this.getRegistryVO().getCoRegistrant()
					.setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_N);
		}

		pProfileItem = this.getGiftRegistryManager().getProfileItemFromEmail(
				this.getRegistryVO().getPrimaryRegistrant().getEmail(),
				this.getRegistryVO().getSiteId());
		if(!isFromThirdParty){
		if (null != pProfileItem) {
			this.getRegistryVO().getPrimaryRegistrant()
					.setProfileId(pProfileItem.getRepositoryId());
			}else{
				this.addFormException(new DropletException("Invalid Email Provided."));
			}
		}else{
			if(!BBBUtility.isEmpty(profileId)){
				MutableRepository profileRepository = this.getProfileRepository();
				RepositoryItem item = null;
				try {
					item = profileRepository.getItem(profileId, BBBCoreConstants.PROFILE_ITEM_DISCRIPTOR_NAME);
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
				if(item!=null ){
					Object emailId = item.getPropertyValue(BBBCoreConstants.EMAIL);
					Object source = item.getPropertyValue(this.SOURCE);
					if(source!=null && emailId!=null && ((String)emailId).equalsIgnoreCase(email) && 
							((String) source).equalsIgnoreCase(token)){
						if( this.validateSite(siteHeader, item)){
							this.getRegistryVO().getPrimaryRegistrant().setProfileId(item.getRepositoryId());
						}else{
							this.addFormException(new DropletException("Invalid Site Association is provided.", this.ERR_INVALID_USER_SITE_ASSOCIATION));
							logError("Invalid Site Association is provided.");
						}
					}else{
						logError("Invalid ProfileId is Provided.");
						this.addFormException(new DropletException("Invalid ProfileId is Provided.", this.ERR_CREATE_REG_INVALID_PROFILEID));
					}
				}else{
					this.addFormException(new DropletException("Invalid ProfileId is Provided.", this.ERR_CREATE_REG_INVALID_PROFILEID));
				}
			}else{
				this.addFormException(new DropletException("Please provide a profileId", this.ERR_CREATE_REG_INVALID_PROFILEID));
			}
		}
		if (StringUtils.isEmpty(this.getRegistryVO().getCoRegistrant()
				.getProfileId())) {
			this.getRegistryVO().getCoRegistrant().setProfileId(null);
		}
	}

	/*
	 * Set WSDL Site Parameters
	 */
	private void setWSDLParameters(final String pSiteId)
			throws BBBSystemException, BBBBusinessException {
		this.getGiftRegSessionBean().clear();
		this.getRegistryVO()
				.getRegistryType()
				.setRegistryTypeName(
						this.getSessionBean().getRegistryTypesEvent());
		this.getRegistryVO().setSiteId(
				this.getBbbCatalogTools()
						.getAllValuesForKey(
								BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
								pSiteId).get(0));
		this.getRegistryVO().setUserToken(
				this.getBbbCatalogTools()
						.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
						.get(0));
		this.getRegistryVO()
				.setServiceName(this.getCreateRegistryServiceName());
	}
	
	/**
	 * 
	 * @param pRequest
	 * @param createRegistryResVO
	 */
	private void createRegistryErrorExist(
			final DynamoHttpServletRequest pRequest,
			final RegistryResVO createRegistryResVO) {

		int errorId = 0;
		String errorMessage = "";

		if (createRegistryResVO.getServiceErrorVO() != null) {
			errorId = createRegistryResVO.getServiceErrorVO().getErrorId();
		}
		if ((createRegistryResVO.getServiceErrorVO() != null)
				&& (createRegistryResVO.getServiceErrorVO()
						.getErrorDisplayMessage() != null)) {
			errorMessage = createRegistryResVO.getServiceErrorVO()
					.getErrorDisplayMessage();
		}

		if (errorId == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS) {
			if (this.isLoggingError()) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						errorMessage + BBBCoreConstants.COLON + errorId,
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1082));
			}
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_INPUT_FIELD_ERROR,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INPUT_FIELD_ERROR));
		}

		if (errorId == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS) {
			if (this.isLoggingError()) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						errorMessage + BBBCoreConstants.COLON + errorId,
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1082));
			}
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_INPUT_FIELD_ERROR,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INPUT_FIELD_ERROR));
		}

		if (errorId == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR) {
			if (this.isLoggingError()) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						errorMessage + BBBCoreConstants.COLON + errorId,
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1083));
			}
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
		}

		if (errorId == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN) {
			if (this.isLoggingError()) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						errorMessage + BBBCoreConstants.COLON + errorId,
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1084));
			}
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));
		}
		if (errorId == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT) {
			if (this.isLoggingError()) {
				this.logError(LogMessageFormatter.formatMessage(pRequest,
						errorMessage + BBBCoreConstants.COLON + errorId,
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));
			}

			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
		}

		if (errorId == 0) {
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"BBBBusinessException from createRegistryErrorExist of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004));
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION));

		} else {
			this.addFormException(new DropletFormException(errorMessage, this
					.getAbsoluteName()
					+ BBBGiftRegistryConstants.DOT_SEPARATOR
					+ errorId, errorMessage));

		}
	}
/**
 *
 * @param pRequest
 * @param pResponse
 * @return To persist registry data in session and redirect to target url on click of start browsing button.
 * @throws ServletException
 * @throws IOException
 * @throws BBBSystemException
 */
	public boolean handleBuyOffStartBrowsing(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException {
		this.logDebug("SimplifyRegFormHandler.handleBuyOffStartBrowsing() method start");
		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
				.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		final String siteId = this.getCurrentSiteId();
		String successURL = this.getBuyoffStartBrowsingSuccessURL();
		RegistrySummaryVO regSummaryVO;
		this.logDebug("BuyOff RegistryId Value"+ this.getRegistryId());

		try {
			regSummaryVO = this.getGiftRegistryManager()
				.getRegistryInfo(this.getRegistryId(), siteId);
			sessionBean.setBuyoffStartBrowsingSummaryVO(regSummaryVO);

			this.logDebug("BuyOff RegistryVO Value"+ regSummaryVO);
		} catch (BBBBusinessException e) {
			this.logError("Error in handle buyOff ", e);
		}
		this.logDebug("BuyOffSuccessURL Value"+ successURL);

		return this.checkFormRedirect(successURL, this.getErrorURL(), pRequest,
				pResponse);
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

	public boolean handleUpdateRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug("SimplifyRegFormHandler.handleUpdateRegistry() method start");
		try {
			this.isRegistryOwnedByProfile(this.getRegistryVO().getRegistryId());
			final String siteId = this.getCurrentSiteId();
			//Code for updating alternate number to registry
			if(BBBUtility.isEmpty(pRequest.getParameter(BBBCoreConstants.ALTERNATE_NUM))){
				this.preUpdateRegistry(pRequest, pResponse);
			}
				String eventType=this.getBbbCatalogTools().getRegistryTypeName(this.getSessionBean().getRegistryTypesEvent(), siteId);
				if(BBBCoreConstants.DEFAULT_CHANNEL_VALUE.equalsIgnoreCase(BBBUtility.getChannel())){
					this.setSuccessURL(this.getRegistryUpdateSuccessURL() + "?eventType=" + eventType
						+ "&registryId=" + this.getRegistryVO().getRegistryId());
					this.setErrorURL(this.getRegistryUpdateErrorURL() + "?registryId=" + this.getRegistryVO().getRegistryId());
				}
				if (this.getFormError()) {
					return this.checkFormRedirect(this.getSuccessURL(),
							this.getErrorURL(), pRequest, pResponse);
				}
				this.setProfileValues();
				this.setServiceParamter(siteId);
				this.setAffiliationValues();
				this.setEventDate(siteId);
				this.setDates(siteId);
	
			    if(this.getContactPoBoxStatus()!=null && this.getContactPoBoxStatus().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
			        this.getRegistryVO().getPrimaryRegistrant().getContactAddress().setQasValidated(true);
			    if(this.getContactPoBoxFlag()!=null && this.getContactPoBoxFlag().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
			    	this.getRegistryVO().getPrimaryRegistrant().getContactAddress().setPoBoxAddress(true);
	
			    this.populateRegistryVoShipping();
				
			    if(this.getRegistryVO().getRegistryType().getRegistryTypeName().equalsIgnoreCase("Baby")){
			        this.getRegistryVO().setIsPublic("1");
					}
					else{
						 this.getRegistryVO().setIsPublic("0");
					}
			  //Code for updating alternate number to registry
			if(BBBUtility.isEmpty(pRequest.getParameter(BBBCoreConstants.ALTERNATE_NUM))){
					this.getRegistryVO().getPrimaryRegistrant()
					.setAddressSelected(this.getRegContactAddress());
					// If New Address is not added to the Registry and old shipping or
					// Billing address is selected need to set that address to
					// registryVO
					final RegistryVO resVO = this.getGiftRegistryManager()
						.setShippingBillingAddr(this.getRegistryVO(),
								this.getShippingAddress(),
								this.getFutureShippingAddress(), this.getProfile(),
								this.getRegistrantAddressFromWS(),
								this.getShippingAddressFromWS(),
								this.getFutureShippingAddressFromWS());
	
					if (StringUtils.isEmpty(resVO.getEvent().getGuestCount())) {
						resVO.getEvent().setGuestCount(BBBCoreConstants.STRING_ZERO);
				}
					
				this.setRegistryVO(resVO);
				
			}else{
				this.setSuccessURL("");
				this.setErrorURL("");
			}
			final RegistryResVO registryResVO = this.getGiftRegistryManager()
					.updateRegistry(this.getRegistryVO());

			if ((registryResVO != null)
					&& registryResVO.getServiceErrorVO().isErrorExists()) {
				this.setFormExceptionsForErrors(pRequest, registryResVO);
			} else {
				setSessionRegistryVo(pRequest, siteId);
			}

			if (null != this.getRegistryVO().getRegistryType()
					.getRegistryTypeName()) {
				this.getRegistryVO()
						.getRegistryType()
						.setRegistryTypeDesc(
								this.getBbbCatalogTools().getRegistryTypeName(
										this.getRegistryVO().getRegistryType()
												.getRegistryTypeName(), siteId));
			}
			this.getGiftRegSessionBean()
					.setResponseHolder(this.getRegistryVO());
			this.getGiftRegSessionBean().setRegistryOperation(
					BBBGiftRegistryConstants.GR_UPDATE);

		} catch (final BBBBusinessException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBBusinessException from handleUpdateRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),
					e);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION));
		} catch (final BBBSystemException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBSystemException from handleUpdateRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),
					e);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION));
		} catch (final TemplateEmailException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"Template Exception from handleUpdateRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1085),
					e);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION));
		} catch (final RepositoryException e1) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"Repository Exception from handleUpdateRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1001),
					e1);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_REGSEARCH_REPO_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_REGSEARCH_REPO_EXCEPTION));
		}
		this.logDebug("SimplifyRegFormHandler.handleUpdateRegistry() method ends");

		// If Success go to update registry confirmation page.
		return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(),
				pRequest, pResponse);
	}
	/**
	 * To set the registryVO in sessionBean
	 * 
	 * @param pRequest the request
	 * @param siteId the siteId
	 * @throws BBBSystemException the BBB System Exception 
	 * @throws RepositoryException the Repository Exception
	 * @throws TemplateEmailException the TemplateEmail Exception 
	 */
	private void setSessionRegistryVo(final DynamoHttpServletRequest pRequest,
			final String siteId) throws BBBSystemException,
			RepositoryException, TemplateEmailException {
		try {
			this.sendCoregistrantEmail(pRequest, siteId);
			final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
					.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
			final String registryId = this.getRegistryVO().getRegistryId();
			if ((sessionBean!= null) && (null != registryId) && (null != sessionBean.getRegistryVOs()) && (null != sessionBean.getRegistryVOs().get(registryId))) {
				sessionBean.getRegistryVOs().remove(registryId);
			}
			if(null != sessionBean && sessionBean.getValues().get("registrySkinnyVOList")!=null)
			{
				Profile profile = (Profile) pRequest
						.resolveName(BBBCoreConstants.ATG_PROFILE);
				if (!profile.isTransient()) {
					List<RegistrySkinnyVO> registrySkinnyVOList = getGiftRegistryManager()
							.getAcceptableGiftRegistries(profile, siteId);
					RegistryEventDateComparator eventDateComparator = new RegistryEventDateComparator();
					eventDateComparator.setSortOrder(2);
					Collections.sort(registrySkinnyVOList, eventDateComparator);
					sessionBean.getValues().put("registrySkinnyVOList", registrySkinnyVOList);
					sessionBean.getValues().put("size", registrySkinnyVOList.size());
					HashMap<String, RegistryVO> registryVOs= sessionBean.getRegistryVOs();
					if(registryVOs==null){
						registryVOs=new HashMap<String, RegistryVO>();
					}
					registryVOs.put(this.getRegistryVO().getRegistryId(),this.getRegistryVO());
					sessionBean.setRegistryVOs(registryVOs);
				}
			}
		} catch (final BBBBusinessException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					"BBBBusinessException in sending EMAIL",
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_20131), e);
		}
	}
	
	/**
	 * 
	 * @param pRequest
	 * @param registryResVO
	 */
	private void setFormExceptionsForErrors(
			final DynamoHttpServletRequest pRequest,
			final RegistryResVO registryResVO) {
		if (!BBBUtility.isEmpty(registryResVO.getServiceErrorVO()
				.getErrorDisplayMessage())
				&& (registryResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS))// Technical
		{
			if (this.isLoggingError()) {
				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"Input field invalid from setFormExceptionsForErrors of SimplifyRegFormHandler webservice error code="
										+ registryResVO.getServiceErrorVO()
												.getErrorId(),
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1086));
			}
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_INPUT_FIELD_ERROR,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INPUT_FIELD_ERROR));
		}
		if (!BBBUtility.isEmpty(registryResVO.getServiceErrorVO()
				.getErrorDisplayMessage())
				&& (registryResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR))// Technical
		{
			if (this.isLoggingError()) {
				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"Fatal error from setFormExceptionsForErrors of SimplifyRegFormHandler webservice error code="
										+ registryResVO.getServiceErrorVO()
												.getErrorId(),
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1087));
			}
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
		}
		if (!BBBUtility.isEmpty(registryResVO.getServiceErrorVO()
				.getErrorDisplayMessage())
				&& (registryResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN))// Technical
		{
			if (this.isLoggingError()) {
				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"Either user token or site flag invalid from setFormExceptionsForErrors of SimplifyRegFormHandler webservice error code="
										+ registryResVO.getServiceErrorVO()
												.getErrorId(),
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1088));
			}
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));
		}
		if (!BBBUtility.isEmpty(registryResVO.getServiceErrorVO()
				.getErrorDisplayMessage())
				&& (registryResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT))// Technical
		{

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry input fields format error from Create/UpdateRegistry of "
									+ "SimplifyRegFormHandler | webservice error code="
									+ registryResVO.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
		}

		if (registryResVO.getServiceErrorVO().getErrorMessage() == null) {
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"BBBBusinessException from setFormExceptionsForErrors of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004));
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION));

		} else {
			this.addFormException(new DropletFormException(registryResVO
					.getServiceErrorVO().getErrorMessage(), this
					.getAbsoluteName()
					+ BBBGiftRegistryConstants.DOT_SEPARATOR
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
	public void sendCoregistrantEmail(final DynamoHttpServletRequest pRequest,
			final String siteId) throws BBBSystemException,
			BBBBusinessException, RepositoryException, TemplateEmailException {
		// Add info to gift registry repository about the registrants
		if (null != this.getSessionBean().getRegistryTypesEvent()) {
			this.getRegistryVO()
					.getRegistryType()
					.setRegistryTypeName(
							this.getSessionBean().getRegistryTypesEvent());
		}
		this.getGiftRegistryManager().giftRegistryRepoUpdate(
				this.getRegistryVO(), this.getCoRegEmailFoundPopupStatus(),
				this.getCoRegEmailNotFoundPopupStatus());

		// Update Registrant profile info
		this.getGiftRegistryManager().updateRegistrantProfileInfo(
				this.getRegistryVO(),
				this.getShippingAddress(),
				this.getFutureShippingAddress(),
				this.getProfile(),
				(String) this.getSiteContext().getSite()
						.getPropertyValue(DEFAULT_COUNTRY));

		// send email to coregistrant
		final String coRegistrySubject = this.getMessageHandler()
				.getPageLabel(LBL_EMAIL_CO_REGISTRY_SUBJECT,
						pRequest.getLocale().getLanguage(), null, null);

		String giftRegistryURL = null;
		if(siteId.contains("TBS")){
			giftRegistryURL = getTbsEmailSiteMap().get(siteId) + getGuestRegistryUri();
		} else {
		giftRegistryURL = pRequest.getScheme()
				+ BBBGiftRegistryConstants.SCHEME_APPEND
				+ this.getHost(pRequest)
				+ getGuestRegistryUri()
				+ this.getRegistryVO().getRegistryId() + EVENT_TYPE;
		}
		String accountLoginURL = null;
		if(siteId.contains("TBS")){
			accountLoginURL = getTbsEmailSiteMap().get(siteId) + getLoginRedirectUrl();
		} else {
			accountLoginURL = pRequest.getScheme()
					+ BBBGiftRegistryConstants.SCHEME_APPEND
					+ this.getHost(pRequest)
					+ getLoginRedirectUrl();
		}

		this.getGiftRegistryManager().sendEmailToCoregistrant(giftRegistryURL,
				accountLoginURL, siteId, coRegistrySubject,
				this.getRegistryVO(), this.getCoRegEmailFoundPopupStatus(),
				this.getCoRegEmailNotFoundPopupStatus(),
				this.getGiftRegEmailInfo());

		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
				.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		// update the registrysummaryvo in the BBBsessionBean
		final RegistrySummaryVO regSummaryVO = this.getGiftRegistryManager()
				.getRegistryInfo(this.getRegistryVO().getRegistryId(), siteId);
		regSummaryVO.setEventDate(this.getRegistryVO().getEvent()
				.getEventDate());
		if (this.getRegistryVO().getCoRegistrant().getFirstName() != null) {
			regSummaryVO.setCoRegistrantFirstName(this.getRegistryVO()
					.getCoRegistrant().getFirstName());
		}
		sessionBean.getValues().put(
				BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, regSummaryVO);

	}
	
	/**
	 * 
	 * @param siteId
	 */
	public final void setEventDate(final String siteId) {
		final String giftRegistryEventDate = this.getRegistryVO().getEvent()
				.getEventDate();
		if (!StringUtils.isEmpty(giftRegistryEventDate)) {
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				this.getRegistryVO()
						.getEvent()
						.setEventDateWS(
								BBBUtility
										.convertCADateIntoWSFormat(giftRegistryEventDate));
			} else {
				this.getRegistryVO()
						.getEvent()
						.setEventDateWS(
								BBBUtility
										.convertUSDateIntoWSFormat(giftRegistryEventDate));
			}
		}
	}
	
	private void setAffiliationValues() {
		// Newly added fields
		this.getRegistryVO().setNetworkAffiliation(
				BBBUtility.netWorkFlagValue(this.getRegistryVO()
						.getRegistryType().getRegistryTypeName()));
		this.getRegistryVO().setPrefRegContMeth(
				BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_METHOD);
		this.getRegistryVO().setPrefRegContTime(
				BBBGiftRegistryConstants.PREF_REGISTRAINT_CONTACT_TIME);
		this.getRegistryVO().setPrefCoregContMeth(
				BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_METHOD);
		
		//PS-21422 defect fixed. Set configured default store id in preference store number in case when no 
		// store is selected during registry update.
		if (BBBUtility.isEmpty(this.getRegistryVO().getPrefStoreNum())) {
			this.getRegistryVO().setPrefStoreNum(
					this.getGiftRegistryManager().getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.REGISTRY_DEFAULT_STORE_ID));
		}
		this.getRegistryVO().setPrefCoregContTime(
				BBBGiftRegistryConstants.PREF_CO_REGISTRAINT_CONTACT_TIME);
		this.getRegistryVO().setSignup(BBBGiftRegistryConstants.SIGN_UP_NO);
		this.getRegistryVO().setHint(BBBGiftRegistryConstants.REGISTRY);
		this.getRegistryVO().setWord(BBBGiftRegistryConstants.WORD);
		if ((this.getRegistryVO().getOptInWeddingOrBump() == null)
				|| (BBBCoreConstants.FALSE).equalsIgnoreCase(this
						.getRegistryVO().getOptInWeddingOrBump())) {
			this.getRegistryVO().setAffiliateOptIn(
					BBBGiftRegistryConstants.FLAG_N);
		} else {
			this.getRegistryVO().setAffiliateOptIn(
					BBBGiftRegistryConstants.FLAG_Y);
		}
	}

	/**
	 * 
	 * @param pSiteId
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void setServiceParamter(final String pSiteId)
			throws BBBSystemException, BBBBusinessException {
		this.getGiftRegSessionBean().clear();
		if (null != this.getSessionBean().getRegistryTypesEvent()) {
			this.getRegistryVO()
					.getRegistryType()
					.setRegistryTypeName(
							this.getSessionBean().getRegistryTypesEvent());
		}
		this.getRegistryVO().setSiteId(
				this.getBbbCatalogTools()
						.getAllValuesForKey(
								BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
								pSiteId).get(0));
		this.getRegistryVO().setUserToken(
				this.getBbbCatalogTools()
						.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
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
		if (!StringUtils.isEmpty(this.getRegistryVO().getEvent()
				.getShowerDate())) {
			final String[] temp = this.getRegistryVO().getEvent()
					.getShowerDate().split(BBBCoreConstants.SLASH);
			String showerDate = null;
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				showerDate = temp[2] + temp[1] + temp[0];
			} else {
				showerDate = temp[2] + temp[0] + temp[1];
			}
			this.getRegistryVO().getEvent().setShowerDateWS(showerDate);
		}
		if (!StringUtils
				.isEmpty(this.getRegistryVO().getEvent().getBirthDate())) {
			final String[] temp = this.getRegistryVO().getEvent()
					.getBirthDate().split(BBBCoreConstants.SLASH);
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
				&& !StringUtils.isEmpty(this.getRegistryVO().getShipping()
						.getFutureShippingDate())) {
			final String temp[] = this.getRegistryVO().getShipping()
					.getFutureShippingDate().split(BBBCoreConstants.SLASH);
			String futShippingDate = null;
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				futShippingDate = temp[2] + temp[1] + temp[0];
			} else {
				futShippingDate = temp[2] + temp[0] + temp[1];
			}
			this.getRegistryVO().getShipping()
					.setFutureShippingDateWS(futShippingDate);
		}
	}

	private void setProfileValues() {
		MutableRepositoryItem pProfileItem;
		if (BBBUtility.isEmpty(this.getRegistryVO().getCoRegistrant()
				.getEmail())) {
			this.getRegistryVO().getCoRegistrant()
					.setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_N);
		} else {
			// Set CO Registrant Profile Id to registryVO
			pProfileItem = this.getGiftRegistryManager()
					.getProfileItemFromEmail(
							this.getRegistryVO().getCoRegistrant().getEmail(),
							this.getRegistryVO().getSiteId());
			if (pProfileItem == null) {
				this.getRegistryVO().getCoRegistrant()
						.setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_N);
			} else {
				this.getRegistryVO().getCoRegistrant()
						.setProfileId(pProfileItem.getRepositoryId());
				this.getRegistryVO().getCoRegistrant()
						.setCoRegEmailFlag(BBBGiftRegistryConstants.FLAG_Y);
			}
		}
		logDebug("true");
		pProfileItem = this.getGiftRegistryManager().getProfileItemFromEmail(
				this.getRegistryVO().getPrimaryRegistrant().getEmail(),
				this.getRegistryVO().getSiteId());
		this.getRegistryVO().getPrimaryRegistrant()
				.setProfileId(pProfileItem.getRepositoryId());
		if (StringUtils.isEmpty(this.getRegistryVO().getCoRegistrant()
				.getProfileId())) {
			this.getRegistryVO().getCoRegistrant().setProfileId(null);
		}
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
	public void preCreateRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse)
			throws BBBBusinessException, BBBSystemException {

		this.logDebug("SimplifyRegFormHandler.preUpdateRegistry() method start");

		this.getFormExceptions().clear();
		if (this.checkCoregistrantValidationStat()) {

			this.coregistrantValidation(this.getSessionBean()
					.getRegistryTypesEvent(), pRequest);

		}

		this.logDebug("SimplifyRegFormHandler.preCreateRegistry() method ends");

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
	public void preUpdateRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse)
			throws BBBBusinessException, BBBSystemException {
		this.logDebug("SimplifyRegFormHandler.preUpdateRegistry() method start");

		this.getFormExceptions().clear();
		this.eventFormValidation(this.getSessionBean().getRegistryTypesEvent(),
				pRequest);
		this.registrantValidation(
				this.getSessionBean().getRegistryTypesEvent(), pRequest);

		if (this.checkCoregistrantValidationStat()) {
			this.coregistrantValidation(this.getSessionBean()
					.getRegistryTypesEvent(), pRequest);
		}

		// validate new shippingAdddress
		if ("newShippingAddress".equalsIgnoreCase(this.getShippingAddress())) {
			this.shippingNewAddressValidation(pRequest);
		}

		// if future address date is not selected
		if (StringUtils.isEmpty(this.getFutureShippingDateSelected())
				|| this.getFutureShippingDateSelected().equalsIgnoreCase(
						"false")) {
			this.getRegistryVO().getShipping().setFutureshippingAddress(null);
			this.getRegistryVO().getShipping().setFutureShippingDate(null);
			this.setFutureShippingAddress(null);
		} else if ("newFutureShippingAddress".equalsIgnoreCase(this
				.getFutureShippingAddress())) {
			this.shippingFutureAddressValidation(pRequest);
			if ((this.getRegistryVO().getShipping().getFutureshippingAddress() != null)
					&& (this.getRegistryVO().getPrimaryRegistrant() != null)) {
				// Set Primary Registrant phone as futureShipping phone
				this.getRegistryVO()
						.getShipping()
						.getFutureshippingAddress()
						.setPrimaryPhone(
								this.getRegistryVO().getPrimaryRegistrant()
										.getPrimaryPhone());
			}
		}
		if (this.getRegistryVO().getRegistryId() != null) {
			this.futureShipDateValidation(this.getRegistryVO().getShipping()
					.getFutureShippingDate(), true, pRequest);
		} else {
			this.futureShipDateValidation(this.getRegistryVO().getShipping()
					.getFutureShippingDate(), false, pRequest);
		}

		this.logDebug("SimplifyRegFormHandler.preCreateRegistry() method ends");

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
	public boolean handleAnnouncementCardCount(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException {

		this.logDebug("SimplifyRegFormHandler.handleAnnouncementCardCount() method start");
		try {

			this.setRegistryVO(this.getGiftRegSessionBean().getRegistryVO());
	        String registryAnnouncement = (String)pRequest.getParameter("registryAnnouncement");
			if(registryAnnouncement!=null){
				int regAccount = Integer.parseInt(registryAnnouncement);
				this.getRegistryVO().setNumRegAnnouncementCards(regAccount);
			}
			final String siteId = this.getSiteContext().getSite().getId();

			this.getRegistryVO()
					.setSiteId(
							this.getBbbCatalogTools()
									.getAllValuesForKey(
											BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
											siteId).get(0));
			this.getRegistryVO().setUserToken(
					this.getBbbCatalogTools()
							.getAllValuesForKey(
									BBBWebServiceConstants.TXT_WSDLKEY,
									BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
							.get(0));
			this.getRegistryVO().setServiceName(
					this.getAnnCardCountServiceName());
			this.logDebug("siteId: " + this.getRegistryVO().getSiteId());
			this.logDebug("userToken: " + this.getRegistryVO().getUserToken());
			this.logDebug("ServiceName: " + this.getAnnCardCountServiceName());

			final SetAnnouncementCardResVO setAnnouncementCardResVO = this
					.getGiftRegistryManager().assignAnnouncementCardCount(
							this.getRegistryVO());

			announcementCardCountError(pRequest, setAnnouncementCardResVO);

			this.logDebug("SimplifyRegFormHandler.handleAnnouncementCardCount() ::"
					+ " SetAnnounceCard Call Error Exists ="
					+ setAnnouncementCardResVO.getServiceErrorVO()
							.isErrorExists());

			return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(),
					pRequest, pResponse);

		} catch (final BBBBusinessException e) {

			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBBusinessException from handleAnnoucementCardCount of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),
					e);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION));

		}

		this.logDebug("SimplifyRegFormHandler.handleAnnouncementCardCount() method ends");

		return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(),
				pRequest, pResponse);
	}
	/**
	 * when service error exist in SetAnnouncementCardResVO
	 * 
	 * @param pRequest the request
	 * @param setAnnouncementCardResVO the setAnnouncementCardResVO
	 */
	private void announcementCardCountError(final DynamoHttpServletRequest pRequest,
			final SetAnnouncementCardResVO setAnnouncementCardResVO) {
		
		if (!BBBUtility.isValidateRegistryId(this.getRegistryVO()
				.getRegistryId())
				|| setAnnouncementCardResVO.getServiceErrorVO()
						.isErrorExists()) {

			if (!BBBUtility.isEmpty(setAnnouncementCardResVO
					.getServiceErrorVO().getErrorDisplayMessage())
					&& (setAnnouncementCardResVO.getServiceErrorVO()
							.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR))// Technical
			// Error
			{

				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"Fatal error from handleAnnouncementCardCount of SimplifyRegFormHandler | webservice error code="
										+ setAnnouncementCardResVO
												.getServiceErrorVO()
												.getErrorId(),
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1089));
				this.addFormException(new DropletException(
						this.getMessageHandler()
								.getErrMsg(
										BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
										pRequest.getLocale().getLanguage(),
										null, null),
						BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
			}
			if (!BBBUtility.isEmpty(setAnnouncementCardResVO
					.getServiceErrorVO().getErrorDisplayMessage())
					&& (setAnnouncementCardResVO.getServiceErrorVO()
							.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN))// Technical
			// Error
			{

				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"Either user token or site flag invalid from handleAnnouncementCardCount of SimplifyRegFormHandler | webservice error code="
										+ setAnnouncementCardResVO
												.getServiceErrorVO()
												.getErrorId(),
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1090));
				this.addFormException(new DropletException(
						this.getMessageHandler()
								.getErrMsg(
										BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
										pRequest.getLocale().getLanguage(),
										null, null),
						BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));
			}
			if (!BBBUtility.isEmpty(setAnnouncementCardResVO
					.getServiceErrorVO().getErrorDisplayMessage())
					&& (setAnnouncementCardResVO.getServiceErrorVO()
							.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT))// Technical
			// Error
			{

				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"GiftRegistry input fields format error from handleAnnouncementCardCount() of "
										+ "SimplifyRegFormHandler | webservice error code="
										+ setAnnouncementCardResVO
												.getServiceErrorVO()
												.getErrorId(),
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

				this.addFormException(new DropletException(
						this.getMessageHandler()
								.getErrMsg(
										BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
										pRequest.getLocale().getLanguage(),
										null, null),
						BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
			}

			this.addFormException(new DropletFormException(
					setAnnouncementCardResVO.getServiceErrorVO()
							.getErrorMessage(), this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ setAnnouncementCardResVO.getServiceErrorVO()
									.getErrorId()));
		}
	}

	private boolean validateUpdateRegistryItem() {
		boolean isError = false;
		if (StringUtils.isEmpty(this.getSkuId())) {
			this.logError("Null or empty sku id ");
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_SKU_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_SKU_ID));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getUpdateRegistryId()) || (!BBBUtility.isValidateRegistryId(this.getUpdateRegistryId()))) {
			this.logError("Null or empty registry id ");
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getProductId())) {
			this.logError("Null or empty product id ");
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_PRODUCT_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_PRODUCT_ID));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRowId())) {
			this.logError("Null or empty row id ");
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_ROW_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_ROW_ID));
			isError = true;
		}
		return isError;
	}

		/**
		 * @ This method is used from mobileApp to remove multiple items from a registry  
		 * @param pRequest
		 * @param pResponse
		 * @return
		 * @throws ServletException
		 * @throws IOException
		 */
		
		public boolean handleRemoveMultipleItemsForRegistry(
				final DynamoHttpServletRequest pRequest,
				final DynamoHttpServletResponse pResponse) throws ServletException,
				IOException,BBBBusinessException {
			logError("Starting handleRemoveItemsForMultipleRegistry .... ");
		
			if(BBBUtility.isEmpty(this.getRegistryId())){
				this.logError("Null or empty registry id ");
				this.addFormException(new DropletException(
						BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID_MSG,
						BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID));
				return false;
			}
			else{
				this.getValue().put(BBBCoreConstants.REGISTRY_ID,this.getRegistryId());
			}
			if(BBBUtility.isEmpty(getInputString())){
				
				logError(INPUT_STRING_IS_NULL);
				this.addFormException(new DropletException(
						this.getMessageHandler().getErrMsg(INPUT_STRING_IS_NULL,pRequest.getLocale()
								.getLanguage(),null, null),INPUT_STRING_IS_NULL));
				return false;
			}
			StringTokenizer pipeSeparatedValues=new StringTokenizer(getInputString(),"|");
			StringBuffer rowId=new StringBuffer("");
			StringBuffer skuId=new StringBuffer("");
			StringBuffer quantity=new StringBuffer("");

			while(pipeSeparatedValues.hasMoreTokens()){			 
				String stringValue=pipeSeparatedValues.nextToken().trim();

				String[] splitStringValue=stringValue.split(",");

				if(splitStringValue.length >= 1 && BBBUtility.isNotEmpty(splitStringValue[0])){
					rowId.append(splitStringValue[0].trim()+",");
				}
				else{
					this.logError("Null or empty row id ");
					this.addFormException(new DropletException(
							BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_ROW_ID_MSG,
							BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_ROW_ID));
					return false;
				}
				if(splitStringValue.length >= 2 && BBBUtility.isNotEmpty(splitStringValue[1])){
					skuId.append(splitStringValue[1].trim()+",");
				}
				else{
					this.logError("Null or empty sku id ");
					this.addFormException(new DropletException(
							BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_SKU_ID_MSG,
							BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_SKU_ID));
					return false;
				}
				if(splitStringValue.length >= 3 && BBBUtility.isNotEmpty(splitStringValue[2])){
					try{
						int i = Integer.parseInt(splitStringValue[2]);
						
						if(i >= 0){
							quantity.append(splitStringValue[2].trim()+",");
						}
						else{
							this.logError("invalid quantity ");
							this.addFormException(new DropletException(
									this.getMessageHandler().getErrMsg(
													BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_QUANT_EXCEPTION,
													pRequest.getLocale().getLanguage(),null, null),BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_QUANT_EXCEPTION));
							return false;
						}
					}
					catch(NumberFormatException e){
						this.logError("invalid quantity ");
						this.addFormException(new DropletException(
								this.getMessageHandler().getErrMsg(
												BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_QUANT_EXCEPTION,
												pRequest.getLocale().getLanguage(),null, null),BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_QUANT_EXCEPTION));
						return false;
					}
				}
				//append zero as quantity if not provided by user because we are removing the item from registry
				else{
					quantity.append(BBBCoreConstants.STRING_ZERO+",");
				}
			}
			
			this.setViewBeans(new ArrayList<GiftRegistryViewBean>());
			while(rowId.length() != 0){
				String rowIdBean = (String) rowId.substring(0, rowId.indexOf(","));
				rowId = rowId.replace(0, rowId.indexOf(",")+1, "");
				String skuIdBean = (String) skuId.substring(0, skuId.indexOf(","));
				skuId = skuId.replace(0, skuId.indexOf(",")+1, "");
				String quantityBean = (String) quantity.substring(0, quantity.indexOf(","));
				quantity = quantity.replace(0, quantity.indexOf(",")+1, "");
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
	public boolean handleUpdateRegistryItems(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (!this.validateUpdateRegistryItem()) {

			this.getValue().put(BBBCoreConstants.REGISTRY_ID,
					this.getUpdateRegistryId());
			this.getValue().put(BBBCoreConstants.ITEM_TYPES,
					this.getItemTypes());
			this.getValue().put(BBBCoreConstants.SKU_PARAM_NAME,this.getSkuId());
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
	public boolean handleUpdateItemToGiftRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		boolean success = true;
		final String dataAccordionId = pRequest
				.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID);
		final String dataScrollTop = pRequest
				.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP);
		this.logDebug("SimplifyRegFormHandler.handleUpdateItemToGiftRegistry() method start");

		try {
			final String pRegistryId = (String) this.getValue().get(
					BBBCoreConstants.REGISTRY_ID);
			
			// Validate registry id
			if(!BBBUtility.isValidateRegistryId(pRegistryId)){
				this.addFormException(new DropletException(
						this.getMessageHandler()
								.getErrMsg(
										BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_QUANTS_SKUS_EXCEPTION,
										pRequest.getLocale().getLanguage(),	null, null)));

				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"Either Items rowIDs, quanties or skus are blank",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_20132));

				success = false;
			}
			this.isRegistryOwnedByProfile(pRegistryId);
			
			if(this.isRemoveSingleItemFlag())
			{
			this.preUpdateItemToGiftRegistry(pRequest, pResponse);
			}
			if (this.getFormError()) {
				success = false;
			}

			final Profile profile = (Profile) pRequest
					.resolveName(ComponentName
							.getComponentName(BBBCoreConstants.ATG_PROFILE));

			// if user is transient then redirect to loggedInFailureURL
			if (success && profile.isTransient()) {
				success = false;
				if (!BBBCoreConstants.REST_REDIRECT_URL
						.equalsIgnoreCase(this.getSuccessURL())) {
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

				this.addFormException(new DropletException(
						this.getMessageHandler()
								.getErrMsg(
										BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_QUANTS_SKUS_EXCEPTION,
										pRequest.getLocale().getLanguage(),
										null, null)));

				this.logError(LogMessageFormatter.formatMessage(pRequest,
						"Either Items rowIDs, quanties or skus are blank",
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_20132));

				success = false;
			}

			boolean allCallSucess = true;

			if (success && rowIDsGroups != null && qtysGroups != null) {
				
				String pSku = (String) getValue().get(
						BBBCoreConstants.SKU_PARAM_NAME);
				String siteId = getSiteContext().getSite().getId();
				 boolean isSkuLtl = getBbbCatalogTools().isSkuLtl(siteId, pSku);
				 
				final int totalCalls = rowIDsGroups.length;

				for (int callIndex = 0; callIndex < this.getModifiedItemsCount(); callIndex++) {
					final GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
					if (!BBBCoreConstants.REST_REDIRECT_URL
							.equalsIgnoreCase(this.getSuccessURL())) {
						this.setSuccessURL(this.getSuccessURL()
								+ BBBCoreConstants.GIFT_REG_UPDATE_STATUS);
					}

					giftRegistryViewBean.setQuantity(this.getModifiedViewBeans().get(callIndex).getQuantity());
					giftRegistryViewBean.setSku(this.getModifiedViewBeans().get(callIndex).getSku());
					giftRegistryViewBean.setRegistryId(pRegistryId);
					giftRegistryViewBean.setRowId(this.getModifiedViewBeans().get(callIndex).getRowId());


					// to test the story BPSI-2136, hardcoding the values that need to send to updateitemToRegistry2 webservice.
					MutableRepository catalogRepository = (MutableRepository) Nucleus.getGlobalNucleus().resolveName("/atg/commerce/catalog/ProductCatalog");
					RepositoryItem skuRepositoryItem = null;
					try {
						skuRepositoryItem = catalogRepository.getItem(this.getModifiedViewBeans().get(callIndex).getSku(),
						        BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
					} catch (RepositoryException e1) {
						logError("error while fetching sku from the repository",e1);
					}
					giftRegistryViewBean.setItemTypes((String) getValue().get(BBBCoreConstants.ITEM_TYPES));
					
					setUpdatedItemForGiftReg(pSku, siteId, isSkuLtl, callIndex,	giftRegistryViewBean, skuRepositoryItem);

					giftRegistryViewBean
							.setSiteFlag(this
									.getBbbCatalogTools()
									.getAllValuesForKey(
											BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
											siteId).get(0));
					giftRegistryViewBean.setUserToken(this
							.getBbbCatalogTools()
							.getAllValuesForKey(
									BBBWebServiceConstants.TXT_WSDLKEY,
									BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
							.get(0));
					giftRegistryViewBean.setServiceName(this
							.getUpdateRegItemsServiceName());

					this.logDebug("siteFlag: "
							+ giftRegistryViewBean.getSiteFlag());
					this.logDebug("userToken: "
							+ giftRegistryViewBean.getUserToken());
					this.logDebug("ServiceName: "
							+ this.getUpdateRegItemsServiceName());

					// update gift registry item
					final ManageRegItemsResVO mageItemsResVO = this.getGiftRegistryManager()
							.removeUpdateGiftRegistryItem(profile,
									giftRegistryViewBean);

					if (mageItemsResVO.getServiceErrorVO().isErrorExists()) {

						allCallSucess = false;

						manageItemErrorForGiftReg(pRequest, mageItemsResVO);

						this.addFormException(new DropletFormException(
								mageItemsResVO.getServiceErrorVO()
										.getErrorMessage(),
								this.getAbsoluteName()
										+ BBBGiftRegistryConstants.DOT_SEPARATOR
										+ mageItemsResVO.getServiceErrorVO()
												.getErrorId()));
						this.logError(LogMessageFormatter
								.formatMessage(
										pRequest,
										"Update registry item SystemException from handleUpdateItemToGiftRegistry of SimplifyRegFormHandler Error Id is "
												+ mageItemsResVO
														.getServiceErrorVO()
														.getErrorId(),
										BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1006));
						this.addFormException(new DropletException(
								this.getMessageHandler()
										.getErrMsg(
												BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION,
												pRequest.getLocale()
														.getLanguage(), null,
												null),
								BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION));
					}
					
					if(allCallSucess){
						allCallSuccessForGiftReg(pRequest, pResponse,
								giftRegistryViewBean);
					}
				}
				
				if (allCallSucess && this.isRemoveSingleItemFlag()) {
					this.logDebug("Invalidating registry cache 2");
						getGiftRegistryManager().getGiftRegistryTools().invalidateRegistryCache(invalidateBeans);
					//}	
					final int oldRequestedQuantity = this
							.findTotalOldQuantity();

					// Update session data so that it will be in sync with the
					// registred items on registry flyout.
					final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
							.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
					final HashMap sessionMap = sessionBean.getValues();
					final RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap
							.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
					if ((registrySummaryVO != null)
							&& registrySummaryVO.getRegistryId()
									.equalsIgnoreCase(pRegistryId)) {
						int totalQuantity = 0;
						totalQuantity = this.findTotalNewQuantity()
								- oldRequestedQuantity;
						// update quantiry in session
						totalQuantity = registrySummaryVO.getGiftRegistered()
								+ totalQuantity;
						registrySummaryVO.setGiftRegistered(totalQuantity);
					}
				}
			}

		} catch (final BBBBusinessException e) {

			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"Update registry item BusinessException from handleUpdateItemToGiftRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1007),
					e);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_BIZ_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_BIZ_EXCEPTION));
		} catch (final BBBSystemException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"Update registry item SystemException from handleUpdateItemToGiftRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1006),
					e);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION));
		}

		this.logDebug("SimplifyRegFormHandler.handleUpdateItemToGiftRegistry() method ends");
		if (BBBUtility.isNotEmpty(this.getSuccessURL())
				&& !BBBCoreConstants.REST_REDIRECT_URL
						.equalsIgnoreCase(this.getSuccessURL())) {
			this.setSuccessURL(getSuccessURL() + BBBCoreConstants.AMPERSAND
					+ BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP
					+ BBBCoreConstants.EQUAL + dataScrollTop
					+ BBBCoreConstants.AMPERSAND
					+ BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID
					+ BBBCoreConstants.EQUAL + dataAccordionId);
		}
		return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(),
				pRequest, pResponse);
	}
	/**
	 * @param pSku the pSku
	 * @param siteId the siteId
	 * @param isSkuLtl the isSkuLtl
	 * @param callIndex the callIndex
	 * @param giftRegistryViewBean the giftRegistryViewBean
	 * @param skuRepositoryItem the skuRepositoryItem
	 * @throws BBBBusinessException
	 * 				Signals that an BBBBusinessException has occurred.
	 * @throws BBBSystemException
	 * 				Signals that an BBBSystemException has occurred
	 * @throws TagConversionException
	 * 				Signals that a TagConversionException has occurred
	 */
	private void setUpdatedItemForGiftReg(String pSku, String siteId,
			boolean isSkuLtl, int callIndex,
			final GiftRegistryViewBean giftRegistryViewBean,
			RepositoryItem skuRepositoryItem) throws BBBBusinessException,
			BBBSystemException, TagConversionException {
		
		if(skuRepositoryItem.getPropertyValue("customizationOfferedFlag").equals(true)){
			giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
		}else{
			//code change for BBBP -222
			if(BBBUtility.isNotEmpty(this.getModifiedViewBeans().get(callIndex).getLtlDeliveryPrices())){
				giftRegistryViewBean.setLtlDeliveryPrices(this.getModifiedViewBeans().get(callIndex).getLtlDeliveryPrices());
			}
			else{
				giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
			}
			if(BBBUtility.isNotEmpty(this.getModifiedViewBeans().get(callIndex).getLtlDeliveryServices())){
				giftRegistryViewBean.setLtlDeliveryServices(this.getModifiedViewBeans().get(callIndex).getLtlDeliveryServices());
			}
			else{
				giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.BLANK);
			}
			if(BBBUtility.isNotEmpty(this.getModifiedViewBeans().get(callIndex).getLtlDeliveryServices()) && this.getModifiedViewBeans().get(callIndex).getLtlDeliveryServices().equalsIgnoreCase(BBBCoreConstants.LWA)){
				giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK+this.getBbbCatalogTools().getAssemblyCharge(SiteContextManager.getCurrentSiteId(), giftRegistryViewBean.getSku()));
				giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.YES_CHAR);
				giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.LW);
			}else{
				giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
				giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
			}
			
			if(isSkuLtl){
				if(giftRegistryViewBean.getLtlDeliveryServices().equalsIgnoreCase(BBBCoreConstants.LWA)){
					giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.LW);
					giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.YES_CHAR);
				}
			}
		if(this.isUpdateDslFromModal()){
				Map<String, String> updatedItemInfoMap = new HashMap<String, String>();
				String ltlDeliveryServicesDesc = "";
				double itemPrice = Double.valueOf(this.getItemPrice());
				RepositoryItem shippingMethod = getBbbCatalogTools().getShippingMethod(this.getLtlDeliveryServices());
				ltlDeliveryServicesDesc = ((String) shippingMethod.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION));
				String assemblySelected = giftRegistryViewBean.getAssemblySelections();
				double deliveryCharge = getBbbCatalogTools().getDeliveryCharge(siteId, pSku, this.getLtlDeliveryServices());
				if(BBBUtility.isNotEmpty(assemblySelected) && assemblySelected.equals(BBBCoreConstants.YES_CHAR)){
					deliveryCharge = deliveryCharge + getBbbCatalogTools().getAssemblyCharge(siteId, pSku);
					ltlDeliveryServicesDesc=ltlDeliveryServicesDesc+BBBGiftRegistryConstants.WITH_ASSEMBLY;
				}
				updatedItemInfoMap.put(LTL_DELIVERY_SERVICES_DESC, ltlDeliveryServicesDesc);
				updatedItemInfoMap.put(BBBCoreConstants.ASSEMBLY_SELECTED, assemblySelected);
				if(Double.compare(deliveryCharge, 0.0) == BBBCoreConstants.ZERO){
					updatedItemInfoMap.put(LTL_DELIVERY_PRICES, "FREE");
				} else{
					updatedItemInfoMap.put(LTL_DELIVERY_PRICES, TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY).
							convertObjectToString(ServletUtil.getCurrentRequest(), deliveryCharge, new Properties()).toString());
				}
				updatedItemInfoMap.put(TOTAL_PRICE, TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY).
						convertObjectToString(ServletUtil.getCurrentRequest(), deliveryCharge + itemPrice, new Properties()).toString());
				this.setUpdatedItemInfoMap(updatedItemInfoMap);
			}

		}
		if(this.getItemTypes()!= null && !StringUtils.isEmpty(this.getItemTypes())){
			giftRegistryViewBean.setItemTypes(this.getItemTypes());
		}
		else{
			if(skuRepositoryItem.getPropertyValue("customizationOfferedFlag").equals(true)){
				giftRegistryViewBean.setItemTypes("PER");
			}
			else if(skuRepositoryItem.getPropertyValue("customizationOfferedFlag").equals(false) && null != skuRepositoryItem.getPropertyValue("ltlFlag") && skuRepositoryItem.getPropertyValue("ltlFlag").equals(true)){
				giftRegistryViewBean.setItemTypes("LTL");
				}
			else{
				giftRegistryViewBean.setItemTypes("");
			}
		}
	}
	/**
	 * @param pRequest the pRequest
	 * @param pResponse the pResponse
	 * @param giftRegistryViewBean the giftRegistryViewBean
	 * @throws BBBSystemException
	 * 				Signals that an BBBSystemException has occurred.
	 * @throws BBBBusinessException
	 * 				Signals that an BBBBusinessException has occurred.
	 * @throws ServletException
	 * 				Signals that a ServletException has occurred.
	 * @throws IOException
	 * 				Signals that an I/O exception has occurred.
	 */
	private void allCallSuccessForGiftReg(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse,
			final GiftRegistryViewBean giftRegistryViewBean)
			throws BBBSystemException, BBBBusinessException, ServletException,
			IOException {
			 getGiftRegistryManager().getGiftRegistryTools().invalidateRegistry(giftRegistryViewBean);
			 List<GiftRegistryViewBean> bean =new ArrayList<GiftRegistryViewBean>();
			 bean.add(giftRegistryViewBean);
			 this.logDebug("Invalidating registry cache 1");
			 getGiftRegistryManager().getGiftRegistryTools().invalidateRegistryCache(bean);
				final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
						.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
				RegistryResVO registryResVO = getGiftRegistryManager().getRegistryInfoFromEcomAdmin(giftRegistryViewBean.getRegistryId(), this.getSiteContext().getSite().getId());
				 sessionBean.getValues().put(giftRegistryViewBean.getRegistryId()+REG_SUMMARY_KEY_CONST, registryResVO);
				 
				//Code for updating  alternate number in registry
				 if(registryResVO != null && BBBUtility.isNotEmpty(this.getAlternateNum())){
					 registryResVO.getRegistryVO().getPrimaryRegistrant().setCellPhone(this.getAlternateNum());
					 registryResVO.getRegistryVO().getEvent().setEventDate(BBBUtility.convertWSDateToUSFormat(registryResVO.getRegistryVO().getEvent()
												.getEventDate()));
					 if(BBBUtility.isNotEmpty(registryResVO.getRegistryVO().getEvent().getShowerDate())){
						 registryResVO.getRegistryVO().getEvent().setShowerDate(BBBUtility.convertWSDateToUSFormat(registryResVO.getRegistryVO().getEvent()
								.getShowerDate()));
					 }
					 if(BBBUtility.isNotEmpty(registryResVO.getRegistryVO().getEvent().getBirthDate())){
						 registryResVO.getRegistryVO().getEvent().setBirthDate(BBBUtility.convertWSDateToUSFormat(registryResVO.getRegistryVO().getEvent()
								.getBirthDate()));
					 }
					 if ((this.getRegistryVO().getShipping() != null)
								&& !StringUtils.isEmpty(this.getRegistryVO().getShipping()
										.getFutureShippingDate())) {
						 registryResVO.getRegistryVO().getShipping().setFutureShippingDate(BBBUtility.convertWSDateToUSFormat(registryResVO.getRegistryVO().getShipping()
									.getFutureShippingDate()));
					 }
					 this.setRegistryVO(registryResVO.getRegistryVO());
					 this.getSessionBean().setRegistryTypesEvent(registryResVO.getRegistryVO().getRegistryType().getRegistryTypeName());
					 pRequest.setParameter(BBBCoreConstants.ALTERNATE_NUM, BBBCoreConstants.TRUE);
					 this.handleUpdateRegistry(pRequest, pResponse);
				 }
				 
	}
	/**
	 * When error occurs in ManageRegItemsResVO
	 * 
	 * @param pRequest the request
	 * @param mageItemsResVO the mageItemsResVO
	 */
	private void manageItemErrorForGiftReg(final DynamoHttpServletRequest pRequest,
			final ManageRegItemsResVO mageItemsResVO) {
		if (!BBBUtility.isEmpty(mageItemsResVO
				.getServiceErrorVO().getErrorDisplayMessage())
				&& (mageItemsResVO.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR))// Technical
		// Error
		{
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Fatal error format error from handleUpdateItemToGiftRegistry() of SimplifyRegFormHandler | webservice error code="
									+ mageItemsResVO
											.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1091));

			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
									pRequest.getLocale()
											.getLanguage(),
									null, null),
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
		}
		if (!BBBUtility.isEmpty(mageItemsResVO
				.getServiceErrorVO().getErrorDisplayMessage())
				&& (mageItemsResVO.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN))// Technical
		// Error
		{
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Either user token or site flag invalid from handleUpdateItemToGiftRegistry() of SimplifyRegFormHandler | webservice error code="
									+ mageItemsResVO
											.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1092));
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
									pRequest.getLocale()
											.getLanguage(),
									null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));
		}
		if (!BBBUtility.isEmpty(mageItemsResVO
				.getServiceErrorVO().getErrorDisplayMessage())
				&& (mageItemsResVO.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT))// Technical
		// Error
		{

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry input fields format error from handleUpdateItemToGiftRegistry() of "
									+ "SimplifyRegFormHandler | webservice error code="
									+ mageItemsResVO
											.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
									pRequest.getLocale()
											.getLanguage(),
									null, null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
		}
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
	public boolean handleUpdateRegistryItem(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		boolean success = true;
		String dataAccordionId = pRequest
				.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID);
		String dataScrollTop = pRequest
				.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP);
		logDebug("SimplifyRegFormHandler.handleUpdateItemToGiftRegistry() method start");

		try {
			String pRegistryId = (String) getValue().get(
					BBBCoreConstants.REGISTRY_ID);
			isRegistryOwnedByProfile(pRegistryId);
			preUpdateItemToGiftRegistry(pRequest, pResponse);

			if (this.getFormError()) {
				success = false;
			}

			Profile profile = (Profile) pRequest.resolveName(ComponentName
					.getComponentName(BBBCoreConstants.ATG_PROFILE));

			// if user is transient then redirect to loggedInFailureURL
			if (success && profile.isTransient()) {
				success = false;
				if (!BBBCoreConstants.REST_REDIRECT_URL
						.equalsIgnoreCase(this.getSuccessURL())) {
					this.setSuccessURL(getLoggedInFailureURL());
				}

			}

			boolean allCallSucess = true;

			if (success) {

				GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
				this.setViewBeans(new ArrayList<GiftRegistryViewBean>());
				if (getSuccessURL() == null) {
					setSuccessURL(getSuccessURL()
							+ BBBCoreConstants.GIFT_REG_REMOVE_STATUS);
				}
				String pSku = (String) getValue().get(
						BBBCoreConstants.SKU_PARAM_NAME);
				String siteId = getSiteContext().getSite().getId();
				 boolean isSkuLtl = getBbbCatalogTools().isSkuLtl(siteId, pSku);
		           
				this.setRegItemOldQty((String) getValue().get(
						BBBCoreConstants.REGISTRY_ITEM_OLD_QTY));
				String regToken = (String) getValue().get(
						BBBCoreConstants.USER_TOKEN);
				String itemRowId = (String) getValue().get(
						BBBCoreConstants.ROWID);
				String refNum = (String) getValue().get("refNum");
				giftRegistryViewBean.setRefNum(refNum);

				// to test the story BPSI-2136, hardcoding the values that need to send to updateitemToRegistry2 webservice.
				MutableRepository catalogRepository = (MutableRepository) Nucleus.getGlobalNucleus().resolveName("/atg/commerce/catalog/ProductCatalog");
				RepositoryItem skuRepositoryItem = null;
				try {
					skuRepositoryItem = catalogRepository.getItem(pSku,
					        BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
				} catch (RepositoryException e1) {
					logError("error while fetching sku from the repository",e1);
				}
				giftRegistryViewBean.setItemTypes((String) getValue().get(BBBCoreConstants.ITEM_TYPES));
			
				setUpdatedItemForReg(pRegistryId, giftRegistryViewBean, pSku,
						siteId, isSkuLtl, regToken, itemRowId,
						skuRepositoryItem);

				logDebug("siteFlag: " + giftRegistryViewBean.getSiteFlag());
				logDebug("userToken: " + giftRegistryViewBean.getUserToken());
				logDebug("ServiceName: " + getUpdateRegItemsServiceName());
				getViewBeans().add(giftRegistryViewBean);

				// update gift registry item
				ManageRegItemsResVO mageItemsResVO = this.getGiftRegistryManager()
						.removeUpdateGiftRegistryItem(profile,
								giftRegistryViewBean);

				if (mageItemsResVO.getServiceErrorVO().isErrorExists()) {

					allCallSucess = false;

					manageItemErrorForReg(pRequest, mageItemsResVO);

					addFormException(new DropletFormException(mageItemsResVO
							.getServiceErrorVO().getErrorMessage(),
							getAbsoluteName()
									+ BBBGiftRegistryConstants.DOT_SEPARATOR
									+ mageItemsResVO.getServiceErrorVO()
											.getErrorId()));
					logError(LogMessageFormatter
							.formatMessage(
									pRequest,
									"Update registry item SystemException from handleUpdateItemToGiftRegistry of SimplifyRegFormHandler Error Id is "
											+ mageItemsResVO
													.getServiceErrorVO()
													.getErrorId(),
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1006));
					addFormException(new DropletException(
							getMessageHandler()
									.getErrMsg(
											BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION,
											pRequest.getLocale().getLanguage(),
											null, null),
							BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION));
				}

				if (allCallSucess) {
					allCallSuccessForReg(pRequest, pResponse, pRegistryId,
							giftRegistryViewBean, siteId);
					
				}
			}

		} catch (BBBBusinessException e) {

			logError(
					LogMessageFormatter.formatMessage(
							pRequest,
							"Update registry item BusinessException from handleUpdateItemToGiftRegistry of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1007), e);
			addFormException(new DropletException(
					getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_BIZ_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_BIZ_EXCEPTION));
		} catch (BBBSystemException e) {
			logError(
					LogMessageFormatter.formatMessage(
							pRequest,
							"Update registry item SystemException from handleUpdateItemToGiftRegistry of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1006), e);
			addFormException(new DropletException(
					getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_SYS_EXCEPTION));
		}

		logDebug("SimplifyRegFormHandler.handleUpdateItemToGiftRegistry() method ends");
		if (BBBUtility.isNotEmpty(this.getSuccessURL())
				&& !BBBCoreConstants.REST_REDIRECT_URL
						.equalsIgnoreCase(this.getSuccessURL())) {
			this.setSuccessURL(getSuccessURL()+ BBBCoreConstants.AMPERSAND
					+ BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP
					+ BBBCoreConstants.EQUAL + dataScrollTop
					+ BBBCoreConstants.AMPERSAND
					+ BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID
					+ BBBCoreConstants.EQUAL + dataAccordionId);
		}
		return checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest,
				pResponse);
	}
	/**
	 * @param pRequest the pRequest
	 * @param pResponse the pResponse
	 * @param pRegistryId the pRegistryId 
	 * @param giftRegistryViewBean the giftRegistryViewBean
	 * @param siteId the siteId
	 * @throws BBBSystemException
	 * 				the bBB system exception
	 * @throws BBBBusinessException
	 * 				the bBB business exception
	 * @throws ServletException
	 * 				the bBB servlet exception
	 * @throws IOException
	 * 				the I/O exception
	 */
	@SuppressWarnings("rawtypes")
	private void allCallSuccessForReg(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, String pRegistryId,
			GiftRegistryViewBean giftRegistryViewBean, String siteId)
			throws BBBSystemException, BBBBusinessException, ServletException,
			IOException {
		
			 getGiftRegistryManager().getGiftRegistryTools().invalidateRegistry(giftRegistryViewBean);
			 List<GiftRegistryViewBean> bean =new ArrayList<GiftRegistryViewBean>();
			 bean.add(giftRegistryViewBean);
			 this.logDebug("Invalidating registry cache 3");
			 getGiftRegistryManager().getGiftRegistryTools().invalidateRegistryCache(bean);
		int oldRequestedQuantity = findTotalOldQuantity();

		// Update session data so that it will be in sync with the
		// registred items on registry flyout.
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest
				.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		final HashMap sessionMap = sessionBean.getValues();
		RegistryResVO registryResVO = getGiftRegistryManager().getRegistryInfoFromEcomAdmin(giftRegistryViewBean.getRegistryId(), siteId);
		 sessionBean.getValues().put(giftRegistryViewBean.getRegistryId()+REG_SUMMARY_KEY_CONST, registryResVO);
		//Code for updating  alternate number in registry
		 if(registryResVO != null && BBBUtility.isNotEmpty(this.getAlternateNum())){
			 registryResVO.getRegistryVO().getPrimaryRegistrant().setCellPhone(this.getAlternateNum());
			 registryResVO.getRegistryVO().getEvent().setEventDate(BBBUtility.convertWSDateToUSFormat(registryResVO.getRegistryVO().getEvent()
										.getEventDate()));
			 this.setRegistryVO(registryResVO.getRegistryVO());
			 this.getSessionBean().setRegistryTypesEvent(registryResVO.getRegistryVO().getRegistryType().getRegistryTypeName());
			 pRequest.setParameter(BBBCoreConstants.ALTERNATE_NUM, BBBCoreConstants.TRUE);
			 this.handleUpdateRegistry(pRequest, pResponse);
		 }
		 RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap
					.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
		if (registrySummaryVO != null
				&& registrySummaryVO.getRegistryId()
						.equalsIgnoreCase(pRegistryId)) {
			int totalQuantity = 0;
			totalQuantity = findTotalNewQuantity()
					- oldRequestedQuantity;
			// update quantiry in session
			totalQuantity = registrySummaryVO.getGiftRegistered()
					+ totalQuantity;
			registrySummaryVO.setGiftRegistered(totalQuantity);
			
		}
	}
	/**
	 * @param pRequest the pRequest
	 * @param mageItemsResVO the mageItemsResVO
	 */
	private void manageItemErrorForReg(final DynamoHttpServletRequest pRequest,
			ManageRegItemsResVO mageItemsResVO) {
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO()
				.getErrorDisplayMessage())
				&& mageItemsResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR)// Technical
		// Error
		{
			logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Fatal error format error from handleUpdateItemToGiftRegistry() of SimplifyRegFormHandler | webservice error code="
									+ mageItemsResVO
											.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1091));

			addFormException(new DropletException(
					getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
									pRequest.getLocale()
											.getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
		}
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO()
				.getErrorDisplayMessage())
				&& mageItemsResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN)// Technical
		// Error
		{
			logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Either user token or site flag invalid from handleUpdateItemToGiftRegistry() of SimplifyRegFormHandler | webservice error code="
									+ mageItemsResVO
											.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1092));
			addFormException(new DropletException(
					getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
									pRequest.getLocale()
											.getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));
		}
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO()
				.getErrorDisplayMessage())
				&& mageItemsResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT)// Technical
		// Error
		{

			logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry input fields format error from handleUpdateItemToGiftRegistry() of "
									+ "SimplifyRegFormHandler | webservice error code="
									+ mageItemsResVO
											.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

			addFormException(new DropletException(
					getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
									pRequest.getLocale()
											.getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
		}
	}
	/**
	 * @param pRegistryId the pRegistryId
	 * @param giftRegistryViewBean the giftRegistryViewBean
	 * @param pSku the pSku
	 * @param siteId the siteId
	 * @param isSkuLtl the isSkuLtl
	 * @param regToken the regToken
	 * @param itemRowId the itemRowId
	 * @param skuRepositoryItem the skuRepositoryItem
	 * @throws BBBBusinessException
	 * 				the bBB business exception
	 * @throws BBBSystemException
	 * 				the bBB System exception
	 * @throws TagConversionException
	 * 				the TagConversion exception
	 */
	private void setUpdatedItemForReg(String pRegistryId,
			GiftRegistryViewBean giftRegistryViewBean, String pSku,
			String siteId, boolean isSkuLtl, String regToken, String itemRowId,
			RepositoryItem skuRepositoryItem) throws BBBBusinessException,
			BBBSystemException, TagConversionException {
		if(skuRepositoryItem.getPropertyValue("customizationOfferedFlag").equals(true)){
			giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
		}else{
			giftRegistryViewBean.setLtlDeliveryServices(this.getLtlDeliveryServices()==null?BBBCoreConstants.BLANK:this.getLtlDeliveryServices());
			giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
			if(isSkuLtl){
				if(giftRegistryViewBean.getLtlDeliveryServices().equalsIgnoreCase(BBBCoreConstants.LWA)){
					giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.LW);
					giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.YES_CHAR);
				}
			}
			if(this.isUpdateDslFromModal()){
				Map<String, String> updatedItemInfoMap = new HashMap<String, String>();
				String ltlDeliveryServicesDesc = "";
				double itemPrice = 0.0;
				if(this.getItemPrice() != null){
					itemPrice = Double.valueOf(this.getItemPrice());
				}
				RepositoryItem shippingMethod = getBbbCatalogTools().getShippingMethod(this.getLtlDeliveryServices());
				ltlDeliveryServicesDesc = ((String) shippingMethod.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION));
				String assemblySelected = giftRegistryViewBean.getAssemblySelections();
				double deliveryCharge = getBbbCatalogTools().getDeliveryCharge(siteId, pSku, this.getLtlDeliveryServices());
				if(BBBUtility.isNotEmpty(assemblySelected) && assemblySelected.equals(BBBCoreConstants.YES_CHAR)){
					deliveryCharge = deliveryCharge + getBbbCatalogTools().getAssemblyCharge(siteId, pSku);
				}
				updatedItemInfoMap.put(LTL_DELIVERY_SERVICES_DESC, INCL+ BBBCoreConstants.SPACE + ltlDeliveryServicesDesc);
				updatedItemInfoMap.put(BBBCoreConstants.ASSEMBLY_SELECTED, assemblySelected);
				if(Double.compare(deliveryCharge, 0.0) == BBBCoreConstants.ZERO){
					updatedItemInfoMap.put(LTL_DELIVERY_PRICES, "FREE");
				} else{
					updatedItemInfoMap.put(LTL_DELIVERY_PRICES, TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY).
							convertObjectToString(ServletUtil.getCurrentRequest(), deliveryCharge, new Properties()).toString());
				}
				updatedItemInfoMap.put(TOTAL_PRICE, TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY).
						convertObjectToString(ServletUtil.getCurrentRequest(), deliveryCharge + itemPrice, new Properties()).toString());
				updatedItemInfoMap.put(ITEM_PRICE, TagConverterManager.getTagConverterByName(BBBCoreConstants.CURRENCY).
						convertObjectToString(ServletUtil.getCurrentRequest(), itemPrice, new Properties()).toString());
				this.setUpdatedItemInfoMap(updatedItemInfoMap);
			}
		}
		
		giftRegistryViewBean.setRegToken(regToken);
		giftRegistryViewBean.setRegistryId(pRegistryId);
		giftRegistryViewBean.setRowId(itemRowId);
		// Set quantity =0 for removing an item
		giftRegistryViewBean
				.setRegItemOldQty(StringUtils
						.isEmpty(this.getRegItemOldQty()) ? "0"
						: this.getRegItemOldQty());
		giftRegistryViewBean.setQuantity(getModifiedItemQuantity());
		giftRegistryViewBean.setSku(pSku);
		giftRegistryViewBean.setSiteFlag(getBbbCatalogTools()
				.getAllValuesForKey(
						BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
						siteId).get(0));
		giftRegistryViewBean.setUserToken(getBbbCatalogTools()
				.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
						BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
				.get(0));
		giftRegistryViewBean
				.setServiceName(getUpdateRegItemsServiceName());
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
	public void preUpdateItemToGiftRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse)
			throws BBBBusinessException, BBBSystemException {

		this.logDebug("SimplifyRegFormHandler.preUpdateAllItemToGiftRegistry() method start");
		
		for (final GiftRegistryViewBean viewBean : this.getViewBeans()) {

			final String giftPurchasedQuantity = viewBean
					.getPurchasedQuantity();
			final String newRequestedQuantity = viewBean.getQuantity();
			final String oldRequestedQuantity = viewBean.getRegItemOldQty();

			// ignore empty sku
			if (viewBean.getSku() == null) {
				continue;
			}

			// if requested old quantity is same as requested new quantity then
			// skip validation
			if (!oldRequestedQuantity.equalsIgnoreCase(newRequestedQuantity)) {

				if (!this.validateRequestedQuantity(newRequestedQuantity,
						giftPurchasedQuantity, pRequest)) {
					this.logError(LogMessageFormatter
							.formatMessage(
									pRequest,
									"Quantity is not valid from preUpdateAllItemToGiftRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1064));
					this.addFormException(new DropletException(
							this.getMessageHandler()
									.getErrMsg(
											BBBGiftRegistryConstants.ERR_UPDATE_REGITEM_QUANT_EXCEPTION,
											pRequest.getLocale().getLanguage(),
											null, null)));

					return;
				}
			}

		}

		this.logDebug("SimplifyRegFormHandler.preUpdateAllItemToGiftRegistry() method ends");
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
	private boolean validateRequestedQuantity(final String requestedQuantity,
			final String pPurchasedQuantity,
			final DynamoHttpServletRequest pRequest) {

		this.logDebug("SimplifyRegFormHandler.checkCoregistrantValidationStat() method start");

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

			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"NumberFormatException from validateRequestedQuantity() of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1093),
					nfexc);
			return validationFlag;
		}
		this.logDebug("SimplifyRegFormHandler.checkCoregistrantValidationStat() method end");
		return true;

	}

	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 */
	private static boolean isItemQuantityChanged(final String oldQuantity,
			final String newQuantity) {

		boolean isModified = true;

		if (oldQuantity != newQuantity) {

			if ((oldQuantity != null) && (newQuantity != null)) {

				if (oldQuantity.equalsIgnoreCase(newQuantity)) {
					isModified = false;
				}
			}
		} else {
			isModified = false;
		}

		return isModified;

	}

	/**
	 * Create batches of RowIDs based on permissible batch size
	 * 
	 * @return
	 */
	private String[] getRowIdsGroups() {

		String rowIDSGroup[] = null;

		final int batchSize = this.getUpdateBatchSize();
		final int totalCalls = (int) Math
				.ceil(((double) this.getModifiedItemsCount() / batchSize));

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
	 * Create batches of quantities based on batch size
	 * 
	 * @return
	 */
	private String[] getQuantityGroups() {

		String qtysGroup[] = null;

		final int batchSize = this.getUpdateBatchSize();
		final int totalCalls = (int) Math
				.ceil(((double) this.getModifiedItemsCount() / batchSize));

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
	 * Create batches of SKUs based on batchsize
	 * 
	 * @return
	 */
	private String[] getSKUsGroups() {

		String skuIDSGroup[] = null;

		final int batchSize = this.getUpdateBatchSize();
		final int totalCalls = (int) Math
				.ceil(((double) this.getModifiedItemsCount() / batchSize));

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
	 * List of modified skus separated by ,
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
			if (SimplifyRegFormHandler.isItemQuantityChanged(
					bean.getRegItemOldQty(), bean.getQuantity()) || BBBUtility.isNotEmpty(bean.getLtlDeliveryServices())) {

				// count number of items modified
				this.mModifiedItemsCount++;

				this.getModifiedViewBeans().add(bean);

			}
		}
	}

	/**
	 * Total quantities sumup of all items's old quantities
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
	 * Total quantities sumup of all items's new quantities
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

	public boolean handleRemoveRegistryItems(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		if (!this.validateUpdateRegistryItem()) {
			this.getValue().put(BBBCoreConstants.SKU_PARAM_NAME,
					this.getSkuId());
			this.getValue().put(BBBCoreConstants.REGISTRY_ID,
					this.getUpdateRegistryId());
			this.getValue().put(BBBCoreConstants.PRODUCT_ID,
					this.getProductId());
			this.getValue().put(BBBCoreConstants.REGISTRY_ITEM_OLD_QTY,
					this.getRegItemOldQty());
			this.getValue().put(BBBCoreConstants.ROWID, this.getRowId());
			this.getValue().put(BBBCoreConstants.ITEM_TYPES, this.getItemTypes());
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
	
	public boolean handleRemoveItemFromGiftRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		boolean success = true;
		final String dataAccordionId = pRequest
				.getParameter(BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID);
		final String dataScrollTop = pRequest
				.getParameter(BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP);
		this.logDebug("SimplifyRegFormHandler.handleRemoveItemFromGiftRegistry() method start");

		try {

			final Profile profile = (Profile) pRequest
					.resolveName(ComponentName
							.getComponentName(BBBCoreConstants.ATG_PROFILE));
			final String pRegistryId = (String) this.getValue().get(
					BBBCoreConstants.REGISTRY_ID);
			
			
			// Validate registry id
			if(!BBBUtility.isValidateRegistryId(pRegistryId)){
				this.logError(
						LogMessageFormatter
								.formatMessage(
										pRequest,
										"BBBBusinessException from handleRemoveItemFromGiftRegistry of SimplifyRegFormHandler"));
				this.addFormException(new DropletException(
						this.getMessageHandler()
								.getErrMsg(
										BBBGiftRegistryConstants.ERR_REM_REGITEM_BIZ_EXCEPTION,
										pRequest.getLocale().getLanguage(), null,
										null),
						BBBGiftRegistryConstants.ERR_REM_REGITEM_BIZ_EXCEPTION));
				success = false;
			}
			
			this.isRegistryOwnedByProfile(pRegistryId);
			pRequest.setParameter(BBBCoreConstants.PROFILE, profile);

			this.preRemoveItemFromGiftRegistry(pRequest, pResponse);

			if (this.getFormError()) {
				success = false;
				if (!BBBCoreConstants.REST_REDIRECT_URL
						.equalsIgnoreCase(this.getSuccessURL())) {
					this.setSuccessURL(this.getLoggedInFailureURL());
				}

			}

			if (success) {

				final GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
				if (this.getSuccessURL() == null) {
					this.setSuccessURL(this.getSuccessURL()
							+ BBBCoreConstants.GIFT_REG_REMOVE_STATUS);
				}
				final String pSku = (String) this.getValue().get(
						BBBCoreConstants.SKU_PARAM_NAME);
				final String prodId = (String) this.getValue().get(
						BBBCoreConstants.PRODUCT_ID_PARAM_NAME);
				final String regToken = (String) this.getValue().get(
						BBBCoreConstants.USER_TOKEN);
				final String itemRowId = (String) this.getValue().get(
						BBBCoreConstants.ROWID);
				// to test the story BPSI-2136, hardcoding the values that need to send to updateitemToRegistry2 webservice.
				MutableRepository catalogRepository = (MutableRepository) Nucleus.getGlobalNucleus().resolveName("/atg/commerce/catalog/ProductCatalog");
				RepositoryItem skuRepositoryItem = null;
				try {
					skuRepositoryItem = catalogRepository.getItem(pSku,
					        BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
				} catch (RepositoryException e1) {
					logError("error while fetching sku from the repository",e1);
				}

				if(skuRepositoryItem != null && skuRepositoryItem.getPropertyValue("customizationOfferedFlag").equals(true)){
						giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
						giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.BLANK);
						giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
						giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
					}else{
						giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
						giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.BLANK);
						giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
						giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
				}
				giftRegistryViewBean.setItemTypes((String) getValue().get(BBBCoreConstants.ITEM_TYPES));

				giftRegistryViewBean.setRegToken(regToken);
				giftRegistryViewBean.setRegistryId(pRegistryId);
				giftRegistryViewBean.setRowId(itemRowId);
				// Set quantity =0 for removing an item
				giftRegistryViewBean.setQuantity(String
						.valueOf(BBBCoreConstants.ZERO));
				giftRegistryViewBean.setSku(pSku);

				final String siteId = this.getSiteContext().getSite().getId();
				giftRegistryViewBean.setSiteFlag(this
						.getBbbCatalogTools()
						.getAllValuesForKey(
								BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
								siteId).get(0));
				giftRegistryViewBean.setUserToken(this
						.getBbbCatalogTools()
						.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
						.get(0));
				giftRegistryViewBean.setServiceName(this
						.getUpdateRegItemsServiceName());

				this.logDebug("siteFlag: " + giftRegistryViewBean.getSiteFlag());
				this.logDebug("userToken: "
						+ giftRegistryViewBean.getUserToken());
				this.logDebug("ServiceName: "
						+ this.getUpdateRegItemsServiceName());

				// remove gift registry item
				final ManageRegItemsResVO mageItemsResVO = this.getGiftRegistryManager()
						.removeUpdateGiftRegistryItem(profile,
								giftRegistryViewBean);

				if (mageItemsResVO.getServiceErrorVO().isErrorExists()) {

					this.errorRemoveItems(pRequest, mageItemsResVO);
				} else {
					sessionUpdateForRemoveItem(pRequest, pRegistryId,
							giftRegistryViewBean, prodId, siteId);

				}
				this.getGiftRegSessionBean().setRegistryOperation(
						BBBGiftRegistryConstants.GR_ITEM_REMOVE);

			}

		} catch (final BBBBusinessException e) {

			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBBusinessException from handleRemoveItemFromGiftRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1009),
					e);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_REM_REGITEM_BIZ_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_REM_REGITEM_BIZ_EXCEPTION));
		} catch (final BBBSystemException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBSystemException from handleRemoveItemFromGiftRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1008),
					e);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_REM_REGITEM_SYS_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_REM_REGITEM_SYS_EXCEPTION));
		}

		this.logDebug("SimplifyRegFormHandler.handleRemoveItemFromGiftRegistry() method ends");
		if (!BBBCoreConstants.REST_REDIRECT_URL
				.equalsIgnoreCase(this.getSuccessURL())) {
			this.setSuccessURL(getSuccessURL()+ BBBCoreConstants.AMPERSAND
					+ BBBGiftRegistryConstants.PARAM_DATA_SCROLL_TOP
					+ BBBCoreConstants.EQUAL + dataScrollTop
					+ BBBCoreConstants.AMPERSAND
					+ BBBGiftRegistryConstants.PARAM_DATA_ACCORDION_ID
					+ BBBCoreConstants.EQUAL + dataAccordionId);
		}
		return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(),
				pRequest, pResponse);
	}
	/**
	 * @param pRequest the request
	 * @param pRegistryId the pRegistryId
	 * @param giftRegistryViewBean the giftRegistryViewBean
	 * @param prodId the prodId
	 * @param siteId the siteId
	 * @throws BBBSystemException
	 * 				Signals that an BBBSystemException has occurred.
	 * @throws BBBBusinessException
	 * 				Signals that an BBBBusinessException has occurred.
	 */
	@SuppressWarnings("rawtypes")
	private void sessionUpdateForRemoveItem(
			final DynamoHttpServletRequest pRequest, final String pRegistryId,
			final GiftRegistryViewBean giftRegistryViewBean,
			final String prodId, final String siteId)
			throws BBBSystemException, BBBBusinessException {
		
			 getGiftRegistryManager().getGiftRegistryTools().invalidateRegistry(giftRegistryViewBean);
			 List<GiftRegistryViewBean> bean =new ArrayList<GiftRegistryViewBean>();
			 bean.add(giftRegistryViewBean);
			 this.logDebug("Invalidating registry cache");
			 getGiftRegistryManager().getGiftRegistryTools().invalidateRegistryCache(bean);
	
		// for omniture tagging
		if (!StringUtils
				.isEmpty(giftRegistryViewBean.getQuantity())
				&& (giftRegistryViewBean.getQuantity()
						.equalsIgnoreCase("0"))) {
			this.setRegistryItemOperation(BBBGiftRegistryConstants.GR_ITEM_REMOVE);
			this.setRemovedProductId(prodId);
		}

		final int oldRequestedQuantity = Integer
				.parseInt((String) this.getValue().get(
						"regItemOldQty"));
		// Update session data so that it will be in sync with the
		// registred items on registry flyout.
		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
				.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		final HashMap sessionMap = sessionBean.getValues();
		RegistryResVO registryResVO = getGiftRegistryManager().getRegistryInfoFromEcomAdmin(giftRegistryViewBean.getRegistryId(), siteId);
		 sessionBean.getValues().put(giftRegistryViewBean.getRegistryId()+REG_SUMMARY_KEY_CONST, registryResVO);
		final RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap
				.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
		if ((registrySummaryVO != null)
				&& registrySummaryVO.getRegistryId()
						.equalsIgnoreCase(pRegistryId)) {
			// update quantiry in session
			registrySummaryVO.setGiftRegistered(registrySummaryVO
					.getGiftRegistered() - oldRequestedQuantity);

		} else if ((registrySummaryVO != null)
				&& !registrySummaryVO.getRegistryId()
						.equalsIgnoreCase(pRegistryId)) {
			// update the registrysummaryvo in the BBBsessionBean
			final RegistrySummaryVO regSummaryVO = this
					.getGiftRegistryManager().getRegistryInfo(
							giftRegistryViewBean.getRegistryId(),
							siteId);
			sessionBean
					.getValues()
					.put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY,
							regSummaryVO);
		}
	}
	
	/**
	 * 
	 * @param pRequest
	 * @param mageItemsResVO
	 */
	private void errorRemoveItems(final DynamoHttpServletRequest pRequest,
			final ManageRegItemsResVO mageItemsResVO) {
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO()
				.getErrorDisplayMessage())
				&& (mageItemsResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR))// Technical
		// Error
		{
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Fatal error from errorRemoveItems of SimplifyRegFormHandler Webservice error id ="
									+ mageItemsResVO.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1094));
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
		}
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO()
				.getErrorDisplayMessage())
				&& (mageItemsResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN))// Technical
		// Error
		{

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Either user token or site flag invalid from errorRemoveItems of SimplifyRegFormHandler Webservice error id ="
									+ mageItemsResVO.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1095));
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));
		}
		if (!BBBUtility.isEmpty(mageItemsResVO.getServiceErrorVO()
				.getErrorDisplayMessage())
				&& (mageItemsResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT))// Technical
		// Error
		{

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry input fields format error from handleRemoveItemFromGiftRegistry() of "
									+ "SimplifyRegFormHandler | webservice error code="
									+ mageItemsResVO.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
		}

		this.addFormException(new DropletException(this
				.getMessageHandler().getErrMsg(
						BBBGiftRegistryConstants.ERR_REM_REGITEM_SYS_EXCEPTION,
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
	public void preRemoveItemFromGiftRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse)
			throws BBBBusinessException, BBBSystemException {

		this.logDebug("SimplifyRegFormHandler.preRemoveItemFromGiftRegistry() method start");

		final Profile profile = (Profile) pRequest
				.getObjectParameter(BBBCoreConstants.PROFILE);

		if (profile.isTransient()) {
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"User type is transient Exception from preRemoveItemFromGiftRegistry of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1003));
			this.addFormException(new DropletFormException(
					"err_rem_regitem_transient_user",
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_NEW_ADDRESS_LAST_NAME,
					"err_rem_regitem_transient_user"));
		}
		this.logDebug("SimplifyRegFormHandler.preRemoveItemFromGiftRegistry() method ends");
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
	public boolean handleEmailGiftRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		boolean isError = false;
		if (StringUtils.isEmpty(this.getSenderEmail())) {
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.EMPTY_SENDER_MAIL_MESSAGE,
					BBBGiftRegistryConstants.ERR_SENDER_MAIL_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRecipientEmail())) {
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.EMPTY_RECEPIENT_MAIL_MESSAGE,
					BBBGiftRegistryConstants.ERR_RECEPIENT_MAIL_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getEventType())) {
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.EMPTY_EVENT_TYPE_MESSAGE,
					BBBGiftRegistryConstants.ERR_EVENT_TYPE_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRegistryId())) {
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.EMPTY_REGISTRY_ID_MESSAGE,
					BBBGiftRegistryConstants.ERR_REGISTRY_ID_EMPTY_OR_NULL));
			isError = true;
		}else if(!BBBUtility.isValidateRegistryId(this.getRegistryId())){
			
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.INVALID_REGISTRY_ID_MESSAGE,
					BBBGiftRegistryConstants.ERR_REGISTRY_ID_EMPTY_OR_NULL));
			isError = true;
		}

		if (this.getDaysToGo() < 0) {
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.ERR_DAYS_TO_GO_INVALID_MESSAGE,
					BBBGiftRegistryConstants.ERR_DAYS_TO_GO_INVALID));
			isError = true;
		}
		
		
		if(!BBBUtility.isRegistryNameValid(this.getRegistryName())){
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.ERR_REGISTRY_NAME,
					BBBGiftRegistryConstants.ERR_INVALID_REGISTRY_NAME));
			isError = true;
		}

		if (!isError) {
			Map<String, Object> values = this.getEmailHolder().getValues();

			values.put(BBBCoreConstants.REGISTRY_URL, this.getRegistryURL());
			values.put(BBBCoreConstants.RECIPIENT_EMAIL,
					this.getRecipientEmail());
			values.put(BBBGiftRegistryConstants.MESSAGE_REGISTRY,
					this.getMessage());
			values.put(BBBCoreConstants.SENDER_EMAIL, this.getSenderEmail());
			values.put(BBBGiftRegistryConstants.EVENT_TYPE, this.getEventType());
			values.put(BBBGiftRegistryConstants.CC_FLAG,
					Boolean.valueOf(this.isCcFlag()));
			values.put(BBBGiftRegistryConstants.P_REG_FIRST_NAME,
					this.getRegFirstName());
			values.put(BBBGiftRegistryConstants.P_REG_LAST_NAME,
					this.getRegLastName());
			values.put(BBBGiftRegistryConstants.COREG_FIRST_NAME,
					this.getCoRegFirstName());
			values.put(BBBGiftRegistryConstants.COREG_LAST_NAME,
					this.getCoRegLastName());
			values.put(BBBGiftRegistryConstants.EVENT_DATE,
					this.getRegistryEventDate());
			values.put(BBBGiftRegistryConstants.DAYS_TO_GO,
					Long.valueOf(this.getDaysToGo()));
			values.put(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE,
					this.getRegistryName());// this is the registry type name
			values.put(BBBGiftRegistryConstants.REGISTRY_ID_PARAM,
					this.getRegistryId());
			values.put(BBBCoreConstants.SUBJECT_PARAM_NAME, this.getSubject());
			values.put(BBBGiftRegistryConstants.TITLE, this.getTitle());
			values.put(
					BBBGiftRegistryConstants.DATE_LABEL,
					"<strong>" + this.getDaysToGo() + "</strong>&nbsp;"
							+ this.getDateLabel());
			this.setValidatedCaptcha(false);
			return this.handleEmailRegistry(pRequest, pResponse);
		}
		final JSONObject responseJson = new JSONObject();
		try {
			responseJson.put(BBBGiftRegistryConstants.ERROR_MESSAGES,
					BBBGiftRegistryConstants.EMPTY_INPUTT_JSON_MESSAGE);
		} catch (final JSONException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"JSONException from handleEmailGiftRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1096),
					e);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
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
	 */	public boolean handleEmailRegistryRecommendation(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug("SimplifyRegFormHandler.handleEmailRegistryRecommendation() method start");
		try {
			setErrorMap(new HashMap<String, String>());
				final Map<String, Object> values = this.getEmailHolder()
						.getValues();
				this.setRecipientEmail((String) values
						.get(BBBCoreConstants.RECIPIENT_EMAIL));
				String registryId = (String) values
						.get(BBBCoreConstants.REGISTRY_ID);
				pRequest.setParameter(BBBGiftRegistryConstants.EMAIL_TYPE,
						BBBGiftRegistryConstants.EMAIL_REGISTRY_RECOMMENDATION);
			String eventTypeAsConfigurable = this.getEventType();
			if(BBBUtility.isEmpty(eventTypeAsConfigurable)) {
				eventTypeAsConfigurable = (String) values
						.get(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE);
			}
			pRequest.setParameter(
					BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE,
					eventTypeAsConfigurable);
				if (BBBUtility.isNotEmpty(this.getRecipientEmail())) {
					this.setRecipientEmail(this.getRecipientEmail().replace(
							BBBCoreConstants.SPACE, BBBCoreConstants.BLANK).trim());
				}
				boolean isValidEmailAddress = true;
				Set<String> recipientEmailList = null;
				if (!BBBUtility.isEmpty(this.getRecipientEmail())) {
					recipientEmailList = new HashSet<String>(
							Arrays.asList(this.getRecipientEmail().split(",")));
					for (final String email : recipientEmailList) {
						if (!BBBUtility.isValidEmail(email)) {
							isValidEmailAddress = false;
							this.logDebug("SimplifyRegFormHandler.handleEmailRegistryRecommendation() invalid emailId: "
									 + email);
							String errorSendingEmail = this
									.getMessageHandler()
									.getErrMsg(
											BBBGiftRegistryConstants.ERR_INVALID_EMAIL_ID,
											pRequest.getLocale().getLanguage(), 
											null, null);
							this.addFormException(new DropletException(
									errorSendingEmail,
									BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION));
							getErrorMap().put(BBBCoreConstants.EMAIL_ERROR, 
									errorSendingEmail);
							break;
						}
					}
				} else {
					isValidEmailAddress = false;
				}
				if (isValidEmailAddress) {
					this.logDebug("SimplifyRegFormHandler.handleEmailRegistryRecommendation() recipientEmail "
							+ this.getRecipientEmail());
					final String siteId = this.getSiteContext().getSite()
							.getId();
					RegistryVO registryVO = new RegistryVO();
					registryVO.getPrimaryRegistrant().setFirstName(this.getRegFirstName());
					registryVO.setRegistryId(registryId);
					registryVO.getEvent().setEventDate(this.getRegistryEventDate());
					registryVO.getRegistryType().setRegistryTypeName(this.getEventType());
					registryVO.setSiteId(siteId);
					Map<String, RepositoryItem> tokensMap = this.getGiftRegistryManager().getGiftRegistryTools().
							persistRecommendationToken(registryVO, recipientEmailList);
					final boolean isEmailSuccess = this.getGiftRegistryManager()
							.sendEmailRegistryRecommendation(siteId, values,
									this.getGiftRegEmailInfo(), tokensMap);
					if (isEmailSuccess) {
						this.logDebug("SimplifyRegFormHandler.handleEmailRegistryRecommendation() isEmailSuccess=true , email sent to "
								+ this.getRecipientEmail());
					} else {
						this.logDebug("SimplifyRegFormHandler.handleEmailRegistryRecommendation() isEmailSuccess=false , "
								+ "there seems to be some problem with SMTP");
						String errorSendingEmail = this.getMessageHandler()
								.getErrMsg(BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
										pRequest.getLocale().getLanguage(), null, null);
						this.addFormException(new DropletException(errorSendingEmail,BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION));
						getErrorMap().put(BBBCoreConstants.EMAIL_ERROR, errorSendingEmail);
					}
				}
			} catch (final BBBSystemException e) {
			this.logError(
					LogMessageFormatter.formatMessage(pRequest,
									"BBBSystemException from handleEmailRegistryRecommendation of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1097), e);
			String errorSendingEmail = this.getMessageHandler().getErrMsg(
							BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null);
			this.addFormException(new DropletException(errorSendingEmail,BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION));
			     getErrorMap().put(BBBCoreConstants.EMAIL_ERROR, errorSendingEmail);
		}
		this.logDebug("SimplifyRegFormHandler.handleEmailRegistryRecommendation() method ends");
		return this.checkFormRedirect(this.getEmailRecommendationSuccessURL(), this.getEmailRecommendationErrorURL(),
				pRequest, pResponse);
	}
	 
	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleEmailGiftRegistryRecommendation (
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		boolean isError = false;
		if (StringUtils.isEmpty(this.getRecipientEmail())) {
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.EMPTY_REGISTRY_ID_MESSAGE,
					BBBGiftRegistryConstants.ERR_RECEPIENT_MAIL_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRegistryURL())) {
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.EMPTY_REGISTRY_URL_MESSAGE,
					BBBGiftRegistryConstants.ERR_REGISTRY_URL_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRegFirstName())) {
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.EMPTY_FIRST_NAME_MESSAGE,
					BBBGiftRegistryConstants.ERR_FIRST_NAME_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRegLastName())) {
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.EMPTY_LAST_NAME_MESSAGE,
					BBBGiftRegistryConstants.ERR_LAST_NAME_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRegistryEventDate())) {
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.EMPTY_EVENT_DATE_MESSAGE,
					BBBGiftRegistryConstants.ERR_EVENT_DATE_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getEventType())) {
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.EMPTY_EVENT_TYPE_MESSAGE,
					BBBGiftRegistryConstants.ERR_EVENT_TYPE_EMPTY_OR_NULL));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getRegistryId())) {
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.EMPTY_REGISTRY_ID_MESSAGE,
					BBBGiftRegistryConstants.ERR_REGISTRY_ID_EMPTY_OR_NULL));
			isError = true;
		}else if(!BBBUtility.isValidateRegistryId(this.getRegistryId())){
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.INVALID_REGISTRY_ID_MESSAGE,
					BBBGiftRegistryConstants.ERR_REGISTRY_ID_EMPTY_OR_NULL));
			isError = true;
		}
		if(!BBBUtility.isRegistryNameValid(this.getRegistryName())){
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.ERR_REGISTRY_NAME,
					BBBGiftRegistryConstants.ERR_INVALID_REGISTRY_NAME));
			isError = true;
		}
		if (!isError) {
			Map<String, Object> values = this.getEmailHolder().getValues();
			values.put(BBBCoreConstants.RECIPIENT_EMAIL, 
					this.getRecipientEmail());
			values.put(BBBCoreConstants.REGISTRY_URL, this.getRegistryURL());
			values.put(BBBGiftRegistryConstants.P_REG_FIRST_NAME, 
					this.getRegFirstName());
			values.put(BBBGiftRegistryConstants.P_REG_LAST_NAME, 
					this.getRegLastName());
			values.put(BBBGiftRegistryConstants.MESSAGE_REGISTRY,
					this.getMessage());
			values.put(BBBGiftRegistryConstants.EVENT_TYPE, this.getEventType());
			values.put(BBBGiftRegistryConstants.EVENT_TYPE_CONFIGURABLE,
					this.getRegistryName());// this is the registry type name
			values.put(BBBGiftRegistryConstants.REGISTRY_ID_PARAM,
					this.getRegistryId());
			setErrorMap(new HashMap<String, String>());
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
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		this.logDebug("SimplifyRegFormHandler.handleEmailRegistry() method start");

		final JSONObject responseJson = new JSONObject();
		try {

			pResponse
					.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON);

			if (this.validateCaptcha(pRequest, pResponse)) {

				final Map<String, Object> values = this.getEmailHolder()
						.getValues();
				final String senderEmailAddress = (String) values
						.get(BBBCoreConstants.SENDER_EMAIL);
				if (this.getEmailHolder().getCcFlag()) {
					values.put(MESSAGE_CC, senderEmailAddress);
				} else {
					if (values.containsKey(MESSAGE_CC)) {
						values.remove(MESSAGE_CC);
					}
				}
				this.setRecipientEmail((String) values
						.get(BBBCoreConstants.RECIPIENT_EMAIL));

				if (BBBUtility.isNotEmpty(this.getRecipientEmail())) {
					this.setRecipientEmail(this.getRecipientEmail().replace(
							BBBCoreConstants.SPACE, BBBCoreConstants.BLANK)
							.trim());
				}

				boolean isValidEmailAddress = true;

				String[] emails = null;
				if (!BBBUtility.isEmpty(this.getRecipientEmail())) {
					emails = this.getRecipientEmail()
							.split(BBBCoreConstants.SEMICOLON);
					for (final String email : emails) {
						if (!BBBUtility.isValidEmail(email)) {
							isValidEmailAddress = false;
							break;
						}
					}
				} else {
					isValidEmailAddress = false;
				}

				if (!(isValidEmailAddress && BBBUtility
						.isValidEmail(senderEmailAddress))) {
					// email is invalid
					responseJson.put(BBBGiftRegistryConstants.ERROR,
							BBBGiftRegistryConstants.GENERAL_ERROR);
					responseJson.put(
							BBBGiftRegistryConstants.ERROR_MESSAGES,
							this.getMessageHandler().getErrMsg(
									"err_js_email_extended_multiple",
									pRequest.getLocale().getLanguage(), null,
									null));
				} else {
					this.logDebug("SimplifyRegFormHandler.handleEmailRegistry() recipientEmail "
							+ this.getRecipientEmail());

					sendEmailFromRegistry(pRequest, responseJson, values);
				}
			} else {

				// when captch is incorrect
				responseJson.put(BBBGiftRegistryConstants.ERROR,
						BBBGiftRegistryConstants.GENERAL_ERROR);
				responseJson
						.put(BBBGiftRegistryConstants.ERROR_MESSAGES,
								this.getMessageHandler().getErrMsg(
										"err_email_incorrect_captcha",
										pRequest.getLocale().getLanguage(),
										null, null));

			}

			this.logDebug("SimplifyRegFormHandler.handleEmailRegistry() printing to output stream");

			final PrintWriter out = pResponse.getWriter();
			out.print(responseJson.toString());
			out.flush();
			out.close();
			this.logDebug("Json String " + responseJson);
		} catch (final JSONException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"JSONException from handleEmailRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1096),
					e);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION));

		} catch (final BBBSystemException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBSystemException from handleEmailRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1097),
					e);
			this.addFormException(new DropletException(this
					.getMessageHandler().getErrMsg(
							BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION));
		}

		this.logDebug("SimplifyRegFormHandler.handleEmailRegistry() method ends");
		this.logDebug("Success URL " + this.getSuccessURL() + "Error URL " + this.getErrorURL());
		return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(),
				pRequest, pResponse);
	}
	/**
	 * 
	 * @param pRequest the request
	 * @param responseJson the responseJson 
	 * @param values the values
	 * @throws BBBSystemException 
	 * 				Signals that an BBBSystemException has occurred.
	 * @throws JSONException
	 * 				Signals that an BBBSystemException has occurred.
	 */
	private void sendEmailFromRegistry(final DynamoHttpServletRequest pRequest,
			final JSONObject responseJson, final Map<String, Object> values)
			throws BBBSystemException, JSONException {
		final Map<String, String> placeHolderMap = new HashMap<String, String>();

		final String siteId = this.getSiteContext().getSite()
				.getId();

		final boolean isEmailSuccess = this.getGiftRegistryManager()
				.sendEmailRegistry(siteId, values,
						this.getGiftRegEmailInfo());

		if (isEmailSuccess) {
			this.setRecipientEmail(this.getRecipientEmail().replaceAll(
					BBBCoreConstants.SEMICOLON,
					BBBCoreConstants.SEMICOLON
							+ BBBCoreConstants.SPACE));
			placeHolderMap.put(BBBCoreConstants.RECIPIENT_EMAIL,
					this.getRecipientEmail());
			responseJson.put(
					BBBGiftRegistryConstants.SUCCESS,
					this.getMessageHandler()
							.getPageTextArea(
									"txt_registry_email_sent_msg",
									pRequest.getLocale()
											.getLanguage(),
									placeHolderMap, null));

			this.logDebug("SimplifyRegFormHandler.handleEmailRegistry() isEmailSuccess=true , email sent to "
					+ this.getRecipientEmail());

		} else {
			this.logDebug("SimplifyRegFormHandler.handleEmailRegistry() isEmailSuccess=false , there seems to be some problem with SMTP");
			responseJson.put(BBBGiftRegistryConstants.ERROR,
					BBBGiftRegistryConstants.SERVER_ERROR);
			responseJson.put(
					BBBGiftRegistryConstants.ERROR_MESSAGES,
					this.getMessageHandler().getErrMsg(
							"err_email_internal_error",
							pRequest.getLocale().getLanguage(),
							null, null));
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
									pRequest.getLocale()
											.getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION));

		}
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
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		this.logDebug("SimplifyRegFormHandler.handleEmailRegistry() method start");

		final JSONObject responseJson = new JSONObject();
		try {

			pResponse
					.setContentType(BBBGiftRegistryConstants.RESPONSE_TYPE_JSON);

			if (this.validateCaptcha(pRequest, pResponse)) {

				final Map<String, Object> values = this.getEmailHolder()
						.getValues();
				final String senderEmailAddress = (String) values
						.get(BBBCoreConstants.SENDER_EMAIL);
				if (this.getEmailHolder().getCcFlag()) {
					values.put(MESSAGE_CC, senderEmailAddress);
				} else {
					if (values.containsKey(MESSAGE_CC)) {
						values.remove(MESSAGE_CC);
					}
				}
				this.setRecipientEmail((String) values
						.get(BBBCoreConstants.RECIPIENT_EMAIL));

				if (BBBUtility.isNotEmpty(this.getRecipientEmail())) {
					this.setRecipientEmail(this.getRecipientEmail().replace(
							BBBCoreConstants.SPACE, BBBCoreConstants.BLANK)
							.trim());
				}

				boolean isValidEmailAddress = true;

				String[] emails = null;
				if (!BBBUtility.isEmpty(this.getRecipientEmail())) {
					emails = this.getRecipientEmail()
							.split(BBBCoreConstants.SEMICOLON);
					for (final String email : emails) {
						if (!BBBUtility.isValidEmail(email)) {
							isValidEmailAddress = false;
							break;
						}
					}
				} else {
					isValidEmailAddress = false;
				}

				if (!(isValidEmailAddress && BBBUtility
						.isValidEmail(senderEmailAddress))) {
					// email is invalid
					responseJson.put(BBBGiftRegistryConstants.ERROR,
							BBBGiftRegistryConstants.GENERAL_ERROR);
					responseJson.put(
							BBBGiftRegistryConstants.ERROR_MESSAGES,
							this.getMessageHandler().getErrMsg(
									"err_js_email_extended_multiple",
									pRequest.getLocale().getLanguage(), null,
									null));
				} else {
					this.logDebug("SimplifyRegFormHandler.handleEmailMxRegistry() recipientEmail "
							+ this.getRecipientEmail());

					sendEmailFromMxRegistry(pRequest, responseJson, values);
				}
			} else {

				// when captch is incorrect
				responseJson.put(BBBGiftRegistryConstants.ERROR,
						BBBGiftRegistryConstants.GENERAL_ERROR);
				responseJson
						.put(BBBGiftRegistryConstants.ERROR_MESSAGES,
								this.getMessageHandler().getErrMsg(
										"err_email_incorrect_captcha",
										pRequest.getLocale().getLanguage(),
										null, null));

			}

			this.logDebug("SimplifyRegFormHandler.handleEmailMxRegistry() printing to output stream");

			final PrintWriter out = pResponse.getWriter();
			out.print(responseJson.toString());
			out.flush();
			out.close();

		} catch (final JSONException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"JSONException from handleEmailMxRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1096),
					e);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION));

		} catch (final BBBSystemException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBSystemException from handleEmailMxRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1097),
					e);
			this.addFormException(new DropletException(this
					.getMessageHandler().getErrMsg(
							BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_EMAIL_SYS_EXCEPTION));
		}

		this.logDebug("SimplifyRegFormHandler.handleEmailMxRegistry() method ends");

		return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(),
				pRequest, pResponse);
	}
	/**
	 * 
	 * @param pRequest the request
	 * @param responseJson the responseJson
	 * @param values the values
	 * @throws BBBSystemException
	 * 		Signals that an BBBSystem Exception has occurred
	 * @throws JSONException
	 * 		Signals that an JSON Exception has occurred
	 */
	private void sendEmailFromMxRegistry(final DynamoHttpServletRequest pRequest,
			final JSONObject responseJson, final Map<String, Object> values)
			throws BBBSystemException, JSONException {
		final Map<String, String> placeHolderMap = new HashMap<String, String>();

		final String siteId = this.getSiteContext().getSite()
				.getId();

		final boolean isEmailSuccess = this.getGiftRegistryManager()
				.sendEmailMxRegistry(siteId, values,
						this.getGiftRegEmailInfo());

		if (isEmailSuccess) {
			this.setRecipientEmail(this.getRecipientEmail().replaceAll(
					BBBCoreConstants.SEMICOLON,
					BBBCoreConstants.SEMICOLON
							+ BBBCoreConstants.SPACE));
			placeHolderMap.put(BBBCoreConstants.RECIPIENT_EMAIL,
					this.getRecipientEmail());
			responseJson.put(
					BBBGiftRegistryConstants.SUCCESS,
					this.getMessageHandler()
							.getPageTextArea(
									"txt_registry_email_sent_msg_mx",
									pRequest.getLocale()
											.getLanguage(),
									placeHolderMap, null));

			this.logDebug("SimplifyRegFormHandler.handleEmailMxRegistry() isEmailSuccess=true , email sent to "
					+ this.getRecipientEmail());

		} else {
			this.logDebug("SimplifyRegFormHandler.handleEmailMxRegistry() isEmailSuccess=false , there seems to be some problem with SMTP");
			responseJson.put(BBBGiftRegistryConstants.ERROR,
					BBBGiftRegistryConstants.SERVER_ERROR);
			responseJson.put(
					BBBGiftRegistryConstants.ERROR_MESSAGES,
					this.getMessageHandler().getErrMsg(
							"err_email_internal_error",
							pRequest.getLocale().getLanguage(),
							null, null));
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION,
									pRequest.getLocale()
											.getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_EMAIL_SENDING_EXCEPTION));

		}
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
	private boolean validateCaptcha(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug("SimplifyRegFormHandler.validateCaptcha() method starts");
		boolean success = true;

		this.logDebug("SessionId = " + pRequest.getSession());
		this.logDebug("SessionId = " + pRequest.getSession().getId());

		// Captcha captcha = (Captcha) pRequest.getSession().getAttribute(
		// Captcha.NAME);
		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
				.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		final Captcha captcha = sessionBean.getCaptcha();

		this.logDebug("Captcha User entered = " + this.getCaptchaAnswer());
		this.logDebug("Captcha  = " + captcha);

		pRequest.setCharacterEncoding("UTF-8");

		// if captcha validation is enabled
		if (this.isValidatedCaptcha() && this.getCaptchaAnswer()!=null  && !captcha.isCorrect(this.getCaptchaAnswer())) {

			success = false;
		}
		this.logDebug("SimplifyRegFormHandler.validateCaptcha() method ends");
		return success;

	}

	/**
	 * This method is used to check if any co registrant field is filled by the
	 * user or not.
	 * 
	 * @return true if any field is filled by the user else, return false.
	 */
	private boolean checkCoregistrantValidationStat() {

		this.logDebug("SimplifyRegFormHandler.checkCoregistrantValidationStat() method start");

		boolean validationFlag = false;

		if (!BBBUtility.isEmpty(this.getRegistryVO().getCoRegistrant()
				.getFirstName())) {
			validationFlag = true;
			return validationFlag;
		}

		if (!BBBUtility.isEmpty(this.getRegistryVO().getCoRegistrant()
				.getLastName())) {
			validationFlag = true;
			return validationFlag;
		}

		if (!BBBUtility.isEmpty(this.getRegistryVO().getCoRegistrant()
				.getEmail())) {
			validationFlag = true;
			return validationFlag;
		}

		this.logDebug("SimplifyRegFormHandler.checkCoregistrantValidationStat() method ends");

		return validationFlag;
	}

	/**
	 * This methos is used for shipping create registry shipping validation.
	 * 
	 */
	private void shippingNewAddressValidation(
			final DynamoHttpServletRequest pRequest) {

		this.logDebug("SimplifyRegFormHandler.ShippingNewAddressValidation() method start");

		// VALIDATE SHIPPING NEW ADDRESS FIRST NAME
		if (!BBBUtility.isValidFirstName(this.getRegistryVO().getShipping()
				.getShippingAddress().getFirstName())) {
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_NEW_ADDRESS_FIRST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
		}

		// VALIDATE SHIPPING NEW LAST NAME
		if (!BBBUtility.isValidLastName(this.getRegistryVO().getShipping()
				.getShippingAddress().getLastName())) {
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_NEW_ADDRESS_LAST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

		}

		// VALIDATE SHIPPING NEW ADDRESS
		this.shippingNewAddress(pRequest);

		this.logDebug("SimplifyRegFormHandler.ShippingNewAddressValidation() method ends");

	}

	/**
	 * This method is used to validate new shipping address.
	 */
	private void shippingNewAddress(final DynamoHttpServletRequest pRequest) {

		this.logDebug("SimplifyRegFormHandler.shippingNewAddress() method start");

		// VALIDATE SHIPPING NEW ADDRESS LINE1
		if (!BBBUtility.isValidAddressLine1(this.getRegistryVO().getShipping()
				.getShippingAddress().getAddressLine1())) {
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Invalid AddressLine1 from shippingNewAddress of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1032));
			this.addFormException(new DropletFormException(

					BBBGiftRegistryConstants.ERR_CREATE_REG_ADDRESS_LINE1_INVALID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_NEW_ADDRESS_CONTACT_ADDR_LINE1,
					BBBGiftRegistryConstants.ERR_CREATE_REG_ADDRESS_LINE1_INVALID));

		}

		// VALIDATE SHIPPING NEW ADDRESS LINE2
		if (!BBBUtility.isEmpty(this.getRegistryVO().getShipping()
				.getShippingAddress().getAddressLine2())
				&& !BBBUtility.isValidAddressLine2(this.getRegistryVO()
						.getShipping().getShippingAddress().getAddressLine2())) {
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Invalid AddressLine2 from shippingNewAddress of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1033));
			this.addFormException(new DropletFormException(
					"err_create_reg_address_line2_invalid",
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_NEW_ADDRESS_CONTACT_ADDR_LINE2,
					"err_create_reg_address_line2_invalid"));
		}

		// VALIDATE SHIPPING NEW ADDRESS CITY
		if (!BBBUtility.isValidCity(this.getRegistryVO().getShipping()
				.getShippingAddress().getCity())) {
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Invalid Shipping city from shippingNewAddress of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1035));
			this.addFormException(new DropletFormException(
					"err_create_reg_city_invalid",
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_NEW_ADDRESS_CONTACT_ADDR_CITY,
					"err_create_reg_city_invalid"));
		}

		// VALIDATE SHIPPING NEW ADDRESS ZIP
		if (!BBBUtility.isValidZip(this.getRegistryVO().getShipping()
				.getShippingAddress().getZip())) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry Invalid zip for shipping address from shippingNewAddress of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1047));

			this.addFormException(new DropletFormException(
					"err_create_reg_zip_invalid",
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_NEW_ADDRESS_CONTACT_ADDR_ZIP,
					"err_create_reg_zip_invalid"));
		}

		this.logDebug("SimplifyRegFormHandler.shippingNewAddress() method ends");
	}

	/**
	 * This method is used for shipping create registry future shipping
	 * validation.
	 */
	private void shippingFutureAddressValidation(
			final DynamoHttpServletRequest pRequest) {

		this.logDebug("SimplifyRegFormHandler.ShippingFutureAddressValidation() method start");

		// VALIDATE SHIPPING NEW ADDRESS FIRST NAME
		if (!BBBUtility.isValidFirstName(this.getRegistryVO().getShipping()
				.getFutureshippingAddress().getFirstName())) {
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_FUTURE_ADDRESS_FIRST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
		}

		// VALIDATE SHIPPING NEW LAST NAME
		if (!BBBUtility.isValidLastName(this.getRegistryVO().getShipping()
				.getFutureshippingAddress().getLastName())) {
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_FUTURE_ADDRESS_LAST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

		}

		this.shippingNewFutureAddValidation(pRequest);

		this.logDebug("SimplifyRegFormHandler.ShippingFutureAddressValidation() method ends");

	}

	/**
	 * This method is used for future shipping future date.
	 * 
	 * @param futureShippingDate
	 *            the future shipping date
	 */
	private void futureShipDateValidation(final String futureShippingDate,
			final boolean updateFlag, final DynamoHttpServletRequest pRequest) {

		this.logDebug("SimplifyRegFormHandler.futureShipDateValidation() method start");
		if (!StringUtils.isEmpty(futureShippingDate) && updateFlag) {
			if (StringUtils.isEmpty(futureShippingDate)) {

				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"GiftRegistry empty future shipping date from futureShipDateValidation of SimplifyRegFormHandler",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1054));

				this.addFormException(new DropletFormException(
						"err_reg_shipping_date_future_invalid",
						this.getAbsoluteName()
								+ BBBGiftRegistryConstants.DOT_SEPARATOR
								+ BBBGiftRegistryConstants.FUTURE_SHIPPING_DATE,
						"err_reg_shipping_date_future_invalid"));
			}
		} else {
			if (!StringUtils.isEmpty(futureShippingDate)
					&& !isValidFutureDate(futureShippingDate,
							BBBCoreConstants.DATE_FORMAT)) {

				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"GiftRegistry Invalid future shipping date from futureShipDateValidation of SimplifyRegFormHandler",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1054));

				this.addFormException(new DropletFormException(
						"err_reg_shipping_date_future_invalid",
						this.getAbsoluteName()
								+ BBBGiftRegistryConstants.DOT_SEPARATOR
								+ BBBGiftRegistryConstants.FUTURE_SHIPPING_DATE,
						"err_reg_shipping_date_future_invalid"));
			}
		}
		this.logDebug("SimplifyRegFormHandler.futureShipDateValidation() method ends");

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

	public static boolean isValidFutureDate(final String dateStr,
			final String formatStr) {
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
	private void shippingNewFutureAddValidation(
			final DynamoHttpServletRequest pRequest) {

		this.logDebug("SimplifyRegFormHandler.shippingNewFutureAddValidation() method start");

		// VALIDATE SHIPPING NEW ADDRESS LINE1
		if (!BBBUtility.isValidAddressLine1(this.getRegistryVO().getShipping()
				.getFutureshippingAddress().getAddressLine1())) {
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Invalid New Future shipping AddressLine1 from shippingNewFutureAddValidation of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1032));
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_CREATE_REG_ADDRESS_LINE1_INVALID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_FUTURE_ADDRESS_CONTACT_ADDR_LINE1,
					BBBGiftRegistryConstants.ERR_CREATE_REG_ADDRESS_LINE1_INVALID));

		}

		// VALIDATE SHIPPING NEW ADDRESS LINE2
		if (!BBBUtility.isEmpty(this.getRegistryVO().getShipping()
				.getFutureshippingAddress().getAddressLine2())
				&& !BBBUtility.isValidAddressLine2(this.getRegistryVO()
						.getShipping().getFutureshippingAddress()
						.getAddressLine2())) {
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Invalid New Future shipping AddressLine2 from shippingNewFutureAddValidation of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1033));
			this.addFormException(new DropletFormException(
					"err_create_reg_address_line2_invalid",
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_FUTURE_ADDRESS_CONTACT_ADDR_LINE2,
					"err_create_reg_address_line2_invalid"));
		}

		// VALIDATE SHIPPING NEW ADDRESS CITY
		if (!BBBUtility.isValidCity(this.getRegistryVO().getShipping()
				.getFutureshippingAddress().getCity())) {
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Invalid New Shipping city from shippingNewFutureAddValidation of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1035));
			this.addFormException(new DropletFormException(
					"err_create_reg_city_invalid",
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_FUTURE_ADDRESS_CONTACT_ADDR_CITY,
					"err_create_reg_city_invalid"));
		}

		// VALIDATE FUTURE SHIPPING NEW ADDRESS ZIP
		if (!BBBUtility.isValidZip(this.getRegistryVO().getShipping()
				.getFutureshippingAddress().getZip())) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry Invalid zip for future shipping address from shippingNewFutureAddValidation of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1047));

			this.addFormException(new DropletFormException(
					"err_create_reg_zip_invalid",
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.SHIPPING_FUTURE_ADDRESS_CONTACT_ADDR_ZIP,
					"err_create_reg_zip_invalid"));
		}

		this.logDebug("SimplifyRegFormHandler.shippingNewFutureAddValidation() method ends");

	}

	/**
	 * event type validation method.
	 * 
	 * @param pEventType
	 *            the event type
	 */
	private void eventFormValidation(final String pEventType,
			final DynamoHttpServletRequest pRequest) {

		this.logDebug("SimplifyRegFormHandler.eventFormValidation() method start");
		final String siteId = this.getCurrentSiteId();
		String dateFormat = BBBCoreConstants.DATE_FORMAT;

		if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
			dateFormat = BBBCoreConstants.CA_DATE_FORMAT;
		}

		if (pEventType
				.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_WEDDING)) {
			this.validateEventDate(this.getRegistryVO().getEvent()
					.getEventDate(), BBBGiftRegistryConstants.EVENT_DATE,
					dateFormat, pRequest);
			this.validateShowerDate(this.getRegistryVO().getEvent()
					.getShowerDate(), BBBGiftRegistryConstants.SHOWER_DATE,
					dateFormat, pRequest);
			this.validateNumberOfGuest(this.getRegistryVO().getEvent()
					.getGuestCount(),
					BBBGiftRegistryConstants.NUMBER_OF_GUESTS, pRequest);
		} else if (pEventType
				.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_BABY)) {
			final String decorTheme = this.getRegistryVO().getEvent()
					.getBabyNurseryTheme();
			if ((null != decorTheme) && (decorTheme.length() > 1)) {
				this.getRegistryVO()
						.getEvent()
						.setBabyNurseryTheme(
								BBBUtility.EncodeNurseryDecorTheme(this
										.getRegistryVO().getEvent()
										.getBabyNurseryTheme()));
			}
			this.validateBabyName(
					this.getRegistryVO().getEvent().getBabyName(),
					BBBGiftRegistryConstants.BABY_NAME, pRequest);
			this.validateEventDate(this.getRegistryVO().getEvent()
					.getEventDate(), BBBGiftRegistryConstants.EVENT_DATE,
					dateFormat, pRequest);
			this.validateShowerDate(this.getRegistryVO().getEvent()
					.getShowerDate(), BBBGiftRegistryConstants.SHOWER_DATE,
					dateFormat, pRequest);
		} else if (pEventType
				.equalsIgnoreCase(BBBGiftRegistryConstants.EVENT_TYPE_COLLEGE)) {
			this.validateEventDate(this.getRegistryVO().getEvent()
					.getEventDate(), BBBGiftRegistryConstants.EVENT_DATE,
					dateFormat, pRequest);
			this.validateCollege(this.getRegistryVO().getEvent().getCollege(),
					BBBGiftRegistryConstants.EVENT_TYPE_COLLEGE, pRequest);
		} else {
			this.validateEventDate(this.getRegistryVO().getEvent()
					.getEventDate(), BBBGiftRegistryConstants.EVENT_DATE,
					dateFormat, pRequest);
		}

		this.logDebug("SimplifyRegFormHandler.eventFormValidation() method ends");

	}
	
	/**
	 * 
	 * @param babyName
	 * @param pFieldName
	 * @param pRequest
	 */
	private void validateBabyName(final String babyName,
			final String pFieldName, final DynamoHttpServletRequest pRequest) {

		this.logDebug("SimplifyRegFormHandler.validateBabyName() method start");

		if (!StringUtils.isEmpty(babyName)) {

			if (!BBBUtility.isStringPatternValid(
					BBBCoreConstants.BABY_NAME_REGULAR_EXP, babyName)) {
				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"Invalid baby name from validateBabyName of SimplifyRegFormHandler",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1034));
				this.addFormException(new DropletFormException(
						"err_create_reg_babyname_invalid", this
								.getAbsoluteName()
								+ BBBGiftRegistryConstants.DOT_SEPARATOR
								+ pFieldName,
						"err_create_reg_babyname_invalid"));

			} else {
				if (!BBBUtility.isStringLengthValid(babyName,
						BBBCoreConstants.ONE, BBBCoreConstants.THIRTY)) {
					this.addFormException(new DropletFormException(
							"err_create_reg_babyname_invalid", this
									.getAbsoluteName()
									+ BBBGiftRegistryConstants.DOT_SEPARATOR
									+ pFieldName,
							"err_create_reg_babyname_invalid"));
				}
			}

		}

		this.logDebug("SimplifyRegFormHandler.validateBabyName() method ends");
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

		this.logDebug("SimplifyRegFormHandler.validateCollege() method start");

		if (!StringUtils.isEmpty(college)) {

			if (BBBUtility.isStringPatternValid(
					BBBCoreConstants.ALPHANUMERIC_WITH_SPECIAL_CHAR, college)) {
				if (!BBBUtility.isStringLengthValid(college,
						BBBCoreConstants.ONE, BBBCoreConstants.HUNDRED)) {

					this.logError(LogMessageFormatter
							.formatMessage(
									pRequest,
									"GiftRegistry Invalid college name length from validateCollege of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1038));

					this.addFormException(new DropletFormException(
							"err_create_reg_college_invalid_length", this
									.getAbsoluteName()
									+ BBBGiftRegistryConstants.DOT_SEPARATOR
									+ pFieldName,
							"err_create_reg_college_invalid_length"));
				}

			} else {

				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"GiftRegistry Invalid college name from validateCollege of SimplifyRegFormHandler",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1037));

				this.addFormException(new DropletFormException(
						"err_create_reg_college_invalid", this
								.getAbsoluteName()
								+ BBBGiftRegistryConstants.DOT_SEPARATOR
								+ pFieldName, "err_create_reg_college_invalid"));

			}

		}

		this.logDebug("SimplifyRegFormHandler.validateCollege() method ends");
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
	private void validateEventDate(final String pEventDate,
			final String pFieldName, final String dateFormat,
			final DynamoHttpServletRequest pRequest) {
		this.logDebug("SimplifyRegFormHandler.validateEventDate() method starts");
		if (StringUtils.isEmpty(pEventDate)) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry empty event date from validateEventDate of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1039));

			this.addFormException(new DropletFormException(
					"err_create_reg_event_date_empty", this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ pFieldName, "err_create_reg_event_date_empty"));

		} else {

			if (!isValidDate(pEventDate, dateFormat)) {

				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"GiftRegistry Invalid event date from validateEventDate of SimplifyRegFormHandler",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1040));

				this.addFormException(new DropletFormException(
						"err_create_reg_event_date_invalid", this
								.getAbsoluteName()
								+ BBBGiftRegistryConstants.DOT_SEPARATOR
								+ pFieldName,
						"err_create_reg_event_date_invalid"));
			}

		}
		this.logDebug("SimplifyRegFormHandler.validateEventDate() method ends");
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
	private void validateShowerDate(final String showerDate,
			final String pFieldName, final String dateFormat,
			final DynamoHttpServletRequest pRequest) {

		this.logDebug("SimplifyRegFormHandler.validateShowerDate() method starts");

		if (!StringUtils.isEmpty(showerDate)
				&& !isValidDate(showerDate, dateFormat)) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry Invalid shower date from validateShowerDate of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1046));

			this.addFormException(new DropletFormException(
					"err_create_reg_shower_date_invalid", this
							.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ pFieldName, "err_create_reg_shower_date_invalid"));
		}
		this.logDebug("SimplifyRegFormHandler.validateShowerDate() method ends");

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

	public static boolean isValidDate(final String dateStr,
			final String formatStr) {
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
		if (pastDate.getTime().after(testDate)
				|| futureDate.getTime().before(testDate)) {
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
	private void validateNumberOfGuest(final String noOfGuest,
			final String pFieldName, final DynamoHttpServletRequest pRequest) {
		this.logDebug("SimplifyRegFormHandler.validateNumberOfGuest() method starts");

		if (StringUtils.isEmpty(noOfGuest)) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry number of guests empty from validateNumberOfGuest of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1042));

			this.addFormException(new DropletFormException(
					"err_create_reg_noofguest_empty", this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ pFieldName, "err_create_reg_noofguest_empty"));
		} else if (!BBBUtility.isStringPatternValid(
				BBBCoreConstants.NUMERIC_FIELD_ONLY, String.valueOf(noOfGuest))) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry invalid format of num of guests from validateNumberOfGuest of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1043));

			this.addFormException(new DropletFormException(
					"err_create_reg_noofguest_invalid", this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ pFieldName, "err_create_reg_noofguest_invalid"));
		}
		this.logDebug("SimplifyRegFormHandler.validateNumberOfGuest() method ends");

	}

	/**
	 * This method is used to validate Registrant fields in registrant page
	 * during registry creation process.
	 * 
	 * @param eventType1
	 *            the event type
	 */
	private void registrantValidation(final String eventType1,
			final DynamoHttpServletRequest pRequest) {

		this.logDebug("SimplifyRegFormHandler.registrantValidation() method start");
		//VALIDATE REGISTRANT FIRST NAME
		if (BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant()
				.getFirstName()) || !BBBUtility.isValidFirstName(this.getRegistryVO().getPrimaryRegistrant()
				.getFirstName())) {
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_FIRST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
		}

		// VALIDATE REGISTRANT LAST NAME
		if (BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant()
				.getLastName()) || !BBBUtility.isValidFirstName(this.getRegistryVO().getPrimaryRegistrant()
				.getLastName())) {
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_LAST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

		}

		// VALIDATE REGISTRANT PHONE NUMBER
		if (!BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant()
				.getPrimaryPhone())
				&& !BBBUtility.isValidPhoneNumber(this.getRegistryVO()
						.getPrimaryRegistrant().getPrimaryPhone())) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry Invalid registrant phone number from validateCollege of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1044));

			this.addFormException(new DropletFormException(
					"err_create_reg_phone_number_is_invalid", this
							.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REG_PHONE_NUMBER,
					"err_create_reg_phone_number_is_invalid"));
		}

		// VALIDATE REGISTRANT CELL NUMBER
		if (!BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant()
				.getCellPhone())
				&& !BBBUtility.isValidPhoneNumber(this.getRegistryVO()
						.getPrimaryRegistrant().getCellPhone())) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry Invalid registrant cell phone from validateCollege of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1044));

			this.addFormException(new DropletFormException(
					"err_create_reg_phone_number_is_invalid", this
							.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REG_MOBILE_NUMBER,
					"err_create_reg_phone_number_is_invalid"));
		}

		this.registrantAddressValidation(pRequest);

		this.logDebug("SimplifyRegFormHandler.registrantValidation() method ends");
	}

	/**
	 * This method is used to validate registrant address fields in create
	 * registry page.
	 * 
	 */
	private void registrantAddressValidation(
			final DynamoHttpServletRequest pRequest) {
		this.logDebug("SimplifyRegFormHandler.registrantAddressValidation() method starts");

		if ("newPrimaryRegAddress"
				.equalsIgnoreCase(this.getRegContactAddress())) {
			// VALIDATE REGISTRANT ADDRESS LINE1
			if (!BBBUtility.isValidAddressLine1(this.getRegistryVO()
					.getPrimaryRegistrant().getContactAddress()
					.getAddressLine1())) {
				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"Invalid registraint AddressLine1 from registrantAddressValidation of SimplifyRegFormHandler",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1032));
				this.addFormException(new DropletFormException(
						BBBGiftRegistryConstants.ERR_CREATE_REG_ADDRESS_LINE1_INVALID,
						this.getAbsoluteName()
								+ BBBGiftRegistryConstants.DOT_SEPARATOR
								+ BBBGiftRegistryConstants.REG_CONTACT_ADDR_LINE1,
						BBBGiftRegistryConstants.ERR_CREATE_REG_ADDRESS_LINE1_INVALID));
			}

			// VALIDATE REGISTRANT ADDRESS LINE2
			if (!BBBUtility.isEmpty(this.getRegistryVO().getPrimaryRegistrant()
					.getContactAddress().getAddressLine2())
					&& !BBBUtility.isValidAddressLine2(this.getRegistryVO()
							.getPrimaryRegistrant().getContactAddress()
							.getAddressLine2())) {
				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"Invalid registraint AddressLine2 from registrantAddressValidation of SimplifyRegFormHandler",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1033));
				this.addFormException(new DropletFormException(
						"err_create_reg_address_line2_invalid",
						this.getAbsoluteName()
								+ BBBGiftRegistryConstants.DOT_SEPARATOR
								+ BBBGiftRegistryConstants.REG_CONTACT_ADDR_LINE2,
						"err_create_reg_address_line2_invalid"));
			}

			// VALIDATE REGISTRANT ADDRESS CITY
			if (!BBBUtility.isValidCity(this.getRegistryVO()
					.getPrimaryRegistrant().getContactAddress().getCity())) {
				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"Invalid registraint city from registrantAddressValidation of SimplifyRegFormHandler",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1035));
				this.addFormException(new DropletFormException(
						"err_create_reg_city_invalid",
						this.getAbsoluteName()
								+ BBBGiftRegistryConstants.DOT_SEPARATOR
								+ BBBGiftRegistryConstants.REG_CONTACT_ADDR_CITY,
						"err_create_reg_city_invalid"));
			}
			if (!BBBUtility.isValidState((this.getRegistryVO()
					.getPrimaryRegistrant().getContactAddress().getState()))) {
				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"Invalid registraint city from registrantAddressValidation of SimplifyRegFormHandler",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1035));
				this.addFormException(new DropletFormException(
						"err_create_reg_state_invalid",
						this.getAbsoluteName()
								+ BBBGiftRegistryConstants.DOT_SEPARATOR
								+ BBBGiftRegistryConstants.REG_CONTACT_ADDR_CITY,
						"err_create_reg_state_invalid"));
			}

			// VALIDATE REGISTRANT ADDRESS ZIP
			if (!BBBUtility.isValidZip(this.getRegistryVO()
					.getPrimaryRegistrant().getContactAddress().getZip())) {

				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"GiftRegistry Invalid zip for registrant address from registrantAddressValidation of SimplifyRegFormHandler",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1047));

				this.addFormException(new DropletFormException(
						"err_create_reg_zip_invalid",
						this.getAbsoluteName()
								+ BBBGiftRegistryConstants.DOT_SEPARATOR
								+ BBBGiftRegistryConstants.REG_CONTACT_ADDR_ZIP,
						"err_create_reg_zip_invalid"));
			}
		}

		this.logDebug("SimplifyRegFormHandler.registrantAddressValidation() method ends");

		final String siteId = this.getCurrentSiteId();
		if ((null != siteId)
				&& (null != this.getRegistryVO().getPrimaryRegistrant()
						.getContactAddress().getAddressLine1())) {
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				this.getRegistryVO().getPrimaryRegistrant().getContactAddress()
						.setCountry(BBBGiftRegistryConstants.CA);
			} else {
				this.getRegistryVO().getPrimaryRegistrant().getContactAddress()
						.setCountry(BBBGiftRegistryConstants.US);
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
	private void coregistrantValidation(final String pEventType,
			final DynamoHttpServletRequest pRequest) {

		this.logDebug("SimplifyRegFormHandler.coregistrantValidation() method start");

		// VALIDATE COREGISTRANT FIRST NAME
		if (!BBBUtility.isValidFirstName(this.getRegistryVO().getCoRegistrant()
				.getFirstName())) {
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_FIRST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
		}

		// VALIDATE COREGISTRANT LAST NAME
		if (!BBBUtility.isValidLastName(this.getRegistryVO().getCoRegistrant()
				.getLastName())) {
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_LAST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

		}

		// VALIDATE COREGISTRANT EMAIL
		if (!BBBUtility.isEmpty(this.getRegistryVO().getCoRegistrant()
				.getEmail())
				&& !BBBUtility.isValidEmail(this.getRegistryVO()
						.getCoRegistrant().getEmail())) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry Invalid Co-registrant email from coregistrantValidation of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1041));

			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_CREATE_REG_INVALID_EMAIL_LENGTH,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.COREG_EMAIL,
					BBBGiftRegistryConstants.ERR_CREATE_REG_INVALID_EMAIL_LENGTH));
		}

		this.logDebug("SimplifyRegFormHandler.coregistrantValidation() method ends");
		final String siteId = this.getSiteContext().getSite().getId();
		if ((null != siteId)
				&& (null != this.getRegistryVO().getCoRegistrant()
						.getFirstName())) {
			if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
				this.getRegistryVO().getCoRegistrant().getContactAddress()
						.setCountry(BBBGiftRegistryConstants.CA);
			} else {
				this.getRegistryVO().getCoRegistrant().getContactAddress()
						.setCountry(BBBGiftRegistryConstants.US);
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
	public boolean handleRegistrySearchFromFlyout(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug("SimplifyRegFormHandler.handleRegistrySearchfromFlyout() method starts");
		this.setRegistrySearchErrorURL(this.getRegistryFlyoutURL());
		this.logDebug("SimplifyRegFormHandler.handleRegistrySearchfromFlyout() method ends");
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
		if(getSessionBean()!=null){
			final HashMap sessionMap = getSessionBean().getValues();
			if(sessionMap!=null){
					RegistrySummaryVO pRegSummaryVO = (RegistrySummaryVO) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
					if(pRegSummaryVO!=null){
					RegistryTypes regType = pRegSummaryVO.getRegistryType();
						if(regType!=null){
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
	public boolean handleRegistrySearchFromBridalLanding(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug("SimplifyRegFormHandler.handleRegistrySearchfromBridalLanding() method starts");
		this.setRegistrySearchErrorURL(this.getBridalLandingURL());
		this.logDebug("SimplifyRegFormHandler.handleRegistrySearchfromBridalLanding() method ends");
		return this.handleRegistrySearch(pRequest, pResponse);
	}
	
	/**
	 * @param pRequest
	 * @param pResponse
	 * @return flag
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean handleRegistrySearchFromBabyLanding(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug("SimplifyRegFormHandler.handleRegistrySearchfromBabyLanding() method starts");
		this.setRegistrySearchErrorURL(this.getBabyLandingURL());
		this.logDebug("SimplifyRegFormHandler.handleRegistrySearchfromBabyLanding() method ends");
		return this.handleRegistrySearch(pRequest, pResponse);
	}

	/**
	 * @param pRequest
	 * @param pResponse
	 * @return flag
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean handleRegistrySearchFromNoSearchResultsPage(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug("SimplifyRegFormHandler.handleRegistrySearchFromNoSearchResultsPage() method starts");
		this.setRegistrySearchErrorURL(this.getNoSearchResultURL());
		this.logDebug("SimplifyRegFormHandler.handleRegistrySearchFromNoSearchResultsPage() method ends");
		return this.handleRegistrySearch(pRequest, pResponse);
	}
	
	/**
	 * @param pRequest
	 * @param pResponse
	 * @return flag
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean handleRegistrySearchFromImportRegistryPage(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug("SimplifyRegFormHandler.handleRegistrySearchFromImportRegistryPage() method starts");
		this.setRegistrySearchSuccessURL(this.getRegistryImportSearchSuccessURL());
		this.setRegistrySearchErrorURL(this.getRegistryImportSearchErrorURL());
		this.logDebug("SimplifyRegFormHandler.handleRegistrySearchFromImportRegistryPage() method ends");
		return this.handleRegistrySearch(pRequest, pResponse);
	}
	
	/**
	 * @param pRequest
	 * @param pResponse
	 * @return flag
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean handleRegistrySearchFromHomePage(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug("SimplifyRegFormHandler.handleRegistrySearchFromHomePage() method starts");
		this.setRegistrySearchErrorURL(this.getHomePageURL());
		this.logDebug("SimplifyRegFormHandler.handleRegistrySearchFromHomePage() method ends");
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

	public boolean handleRegistrySearch(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug("SimplifyRegFormHandler.handleRegistrySearch() method starts");

		try {

			final String siteId = this.getSiteContext().getSite().getId();
			// Find old registries block

			if (this.getHidden() == 2) {
				final String excludedRegNums = this.getGiftRegistryManager()
						.getCommaSaperatedRegistryIds(this.getProfile(), siteId);
				if (!StringUtils.isEmpty(excludedRegNums)) {
					this.getRegistrySearchVO().setExcludedRegNums(
							excludedRegNums);
				}
				this.getRegistrySearchVO().setReturnLeagacyRegistries(true);
				this.getRegistrySearchVO().setProfileId(this.getProfile());
				this.getRegistrySearchVO().setGiftGiver(false);

			} else {
				this.getRegistrySearchVO().setGiftGiver(true);
			}
			this.getGiftRegSessionBean().setRequestVO(null);
			this.preRegistrySearch(pRequest, pResponse);

			if (!this.getFormError()) {

				this.getRegistrySearchVO().setProfileId(this.getProfile());
				if (!this.getProfile().isTransient()) {
					this.getRegistrySearchVO().setFilterRegistriesInProfile(
							true);
				}

				this.getRegistrySearchVO()
						.setSiteId(
								this.getBbbCatalogTools()
										.getAllValuesForKey(
												BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
												siteId).get(0));
				this.getRegistrySearchVO()
						.setUserToken(
								this.getBbbCatalogTools()
										.getAllValuesForKey(
												BBBWebServiceConstants.TXT_WSDLKEY,
												BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
										.get(0));
				this.getRegistrySearchVO().setServiceName(
						this.getSearchRegistryServiceName());

				this.logDebug("siteFlag: " + this.getRegistryVO().getSiteId());
				this.logDebug("userToken: "
						+ this.getRegistryVO().getUserToken());
				this.logDebug("ServiceName: "
						+ this.getCreateRegistryServiceName());

				this.getGiftRegSessionBean().setRequestVO(
						this.getRegistrySearchVO());
				
				GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
				if(getSessionBean()!=null){
					final HashMap sessionMap = getSessionBean().getValues();
					if(sessionMap!=null){
							RegistrySummaryVO pRegSummaryVO = (RegistrySummaryVO) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
							if(pRegSummaryVO!=null){
							RegistryTypes regType = pRegSummaryVO.getRegistryType();
								if(regType!=null){
								 giftRegistryViewBean.setRegistryName(regType.getRegistryTypeDesc());
								 getSessionBean().setGiftRegistryViewBean(giftRegistryViewBean);
								}
						    }
					}
				}
			}
			this.logDebug("SimplifyRegFormHandler.handleRegistrySearch() method ends");

			return this.checkFormRedirect(this.getRegistrySearchSuccessURL(),
					this.getRegistrySearchErrorURL(), pRequest, pResponse);

		} catch (final BBBBusinessException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBBusinessException from handleRegistrySearch of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),
					e);

			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION));

			return this.checkFormRedirect(this.getRegistrySearchSuccessURL(),
					this.getRegistrySearchErrorURL(), pRequest, pResponse);
		} catch (final BBBSystemException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBSystemException from handleRegistrySearch of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),
					e);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION));

			return this.checkFormRedirect(this.getRegistrySearchSuccessURL(),
					this.getRegistrySearchErrorURL(), pRequest, pResponse);
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
	public void preRegistrySearch(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse)
			throws BBBBusinessException, BBBSystemException {
		this.logDebug("SimplifyRegFormHandler.preRegistrySearch() method starts");

		try {
			if ((this.getHidden() == 1) || (this.getHidden() == 4)
					|| (this.getHidden() == 5)) {

				this.validateFirstName();

				this.validateLastName();

			} else if (this.getHidden() == 2) {

				this.searchLegacyRegistry(pRequest);

			} else if (this.getHidden() == 3) {
				if ((!StringUtils.isEmpty(this.getRegistrySearchVO()
						.getFirstName()) || !StringUtils.isEmpty(this
						.getRegistrySearchVO().getLastName()))
						&& !StringUtils.isEmpty(this.getRegistrySearchVO()
								.getRegistryId())) {
					this.addFormException(new DropletFormException(
							BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
							this.getAbsoluteName()
									+ BBBGiftRegistryConstants.DOT_SEPARATOR
									+ BBBGiftRegistryConstants.REG_SRCH_FIRST_LAST_REGID,
							BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
				} else if ((StringUtils.isEmpty(this.getRegistrySearchVO()
						.getFirstName()) || StringUtils.isEmpty(this
						.getRegistrySearchVO().getLastName()))
						&& StringUtils.isEmpty(this.getRegistrySearchVO()
								.getRegistryId())) {
					this.addFormException(new DropletFormException(
							BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
							this.getAbsoluteName()
									+ BBBGiftRegistryConstants.DOT_SEPARATOR
									+ BBBGiftRegistryConstants.REG_SRCH_FIRST_LAST_REGID_EMPTY,
							BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));
				} else if (!StringUtils.isEmpty(this.getRegistrySearchVO()
						.getFirstName())
						|| !StringUtils.isEmpty(this.getRegistrySearchVO()
								.getLastName())) {
					this.validateFirstName();

					this.validateLastName();
				} else if (!StringUtils.isEmpty(this.getRegistrySearchVO()
						.getRegistryId())
						&& !BBBUtility.isValidateRegistryId(this
								.getRegistrySearchVO().getRegistryId())) {

					this.logError(LogMessageFormatter
							.formatMessage(
									pRequest,
									"GiftRegistry Invalid registry id from preRegistrySearch of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1045));

					this.addFormException(new DropletFormException(
							BBBGiftRegistryConstants.ERR_CREATE_REG_REGISTRY_ID_INVALID,
							this.getAbsoluteName()
									+ BBBGiftRegistryConstants.DOT_SEPARATOR
									+ BBBGiftRegistryConstants.REGISTRY_SEARCH_REGID,
							BBBGiftRegistryConstants.ERR_CREATE_REG_REGISTRY_ID_INVALID));
				}
			}
			this.logDebug("SimplifyRegFormHandler.preRegistrySearch() method ends");

		} catch (final NumberFormatException e) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry Invalid number from preRegistrySearch of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1053));

			this.addFormException(new DropletException(this
					.getMessageHandler().getErrMsg(
							BBBGiftRegistryConstants.ERR_NO_FORMAT_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.ERR_NO_FORMAT_EXCEPTION));
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							" err_no_format_exception from preRegistrySearch of SimplifyRegFormHandler",
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

	public boolean handleMxRegistrySearch(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug("SimplifyRegFormHandler.handleMxRegistrySearch() method starts");

		try {

			final String siteId = this.getSiteContext().getSite().getId();
			// Find old registries block

			if (this.getHidden() == 2) {
				final String excludedRegNums = this.getGiftRegistryManager()
						.getCommaSaperatedRegistryIds(this.getProfile(), siteId);
				if (!StringUtils.isEmpty(excludedRegNums)) {
					this.getRegistrySearchVO().setExcludedRegNums(
							excludedRegNums);
				}
				this.getRegistrySearchVO().setReturnLeagacyRegistries(true);
				this.getRegistrySearchVO().setProfileId(this.getProfile());
				this.getRegistrySearchVO().setGiftGiver(false);

			} else {
				this.getRegistrySearchVO().setGiftGiver(true);
			}
			this.getGiftRegSessionBean().setRequestVO(null);
			this.preRegistrySearch(pRequest, pResponse);

			if (!this.getFormError()) {

				this.getRegistrySearchVO().setProfileId(this.getProfile());
				if (!this.getProfile().isTransient()) {
					this.getRegistrySearchVO().setFilterRegistriesInProfile(
							true);
				}

				this.getRegistrySearchVO()
						.setSiteId("5");
				
				this.getRegistrySearchVO()
						.setUserToken(
								this.getBbbCatalogTools()
										.getAllValuesForKey(
												BBBWebServiceConstants.TXT_WSDLKEY,
												BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
										.get(0));
				this.getRegistrySearchVO().setServiceName(
						this.getSearchRegistryServiceName());

				this.logDebug("siteFlag: " + this.getRegistryVO().getSiteId());
				this.logDebug("userToken: "
						+ this.getRegistryVO().getUserToken());
				this.logDebug("ServiceName: "
						+ this.getCreateRegistryServiceName());

				this.getGiftRegSessionBean().setRequestVO(
						this.getRegistrySearchVO());
			}
			this.logDebug("SimplifyRegFormHandler.handleRegistrySearch() method ends");

			return this.checkFormRedirect(this.getRegistrySearchSuccessURL(),
					this.getRegistrySearchErrorURL(), pRequest, pResponse);

		} catch (final BBBBusinessException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBBusinessException from handleRegistrySearch of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),
					e);

			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_RESEARCH_BIZ_EXCEPTION));

			return this.checkFormRedirect(this.getRegistrySearchSuccessURL(),
					this.getRegistrySearchErrorURL(), pRequest, pResponse);
		} catch (final BBBSystemException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBSystemException from handleRegistrySearch of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),
					e);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION));

			return this.checkFormRedirect(this.getRegistrySearchSuccessURL(),
					this.getRegistrySearchErrorURL(), pRequest, pResponse);
		}

	}
	

	private void searchLegacyRegistry(final DynamoHttpServletRequest pRequest) {
		if ((!StringUtils.isEmpty(this.getRegistrySearchVO().getFirstName()) || !StringUtils
				.isEmpty(this.getRegistrySearchVO().getLastName()))
				&& !StringUtils.isEmpty(this.getRegistrySearchVO().getEmail())
				&& !StringUtils.isEmpty(this.getRegistrySearchVO()
						.getRegistryId())) {
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Either First name, Last name or Email or registry id should be used from searchLegacyRegistry of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1062));
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_EMAIL_REGID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REG_SRCH_FIRST_LAST_EMAIL_REGID,
					BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_EMAIL_REGID));
		} else if ((StringUtils.isEmpty(this.getRegistrySearchVO()
				.getFirstName()) || StringUtils.isEmpty(this
				.getRegistrySearchVO().getLastName()))
				&& StringUtils.isEmpty(this.getRegistrySearchVO().getEmail())
				&& StringUtils.isEmpty(this.getRegistrySearchVO()
						.getRegistryId())) {
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_EMAIL_REGID_EMPTY,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REG_SRCH_FIRST_LAST_EMAIL_REGID_EMPTY,
					BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_EMAIL_REGID_EMPTY));
		} else if ((!StringUtils.isEmpty(this.getRegistrySearchVO()
				.getFirstName()) || !StringUtils.isEmpty(this
				.getRegistrySearchVO().getLastName()))
				&& !StringUtils.isEmpty(this.getRegistrySearchVO().getEmail())) {
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Either First name, Last name or Email should be used from searchLegacyRegistry of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1061));
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_EMAIL,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REG_SRCH_FIRST_LAST_EMAIL,
					BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_EMAIL));
		} else if ((!StringUtils.isEmpty(this.getRegistrySearchVO()
				.getFirstName()) || !StringUtils.isEmpty(this
				.getRegistrySearchVO().getLastName()))
				&& !StringUtils.isEmpty(this.getRegistrySearchVO()
						.getRegistryId())) {
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Either First name, Last name or registry id should be used from searchLegacyRegistry of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1063));
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_REGID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REG_SRCH_FIRST_LAST_REGID,
					BBBGiftRegistryConstants.ERR_SRCH_FIRST_LAST_REGID));
		} else if (!StringUtils.isEmpty(this.getRegistrySearchVO()
				.getRegistryId())
				&& !StringUtils.isEmpty(this.getRegistrySearchVO().getEmail())) {
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Either email or registry id should be used from searchLegacyRegistry of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1060));
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_SRCH_EMAIL_REGID, this
							.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REG_SRCH_EMAIL_REGID,
					BBBGiftRegistryConstants.ERR_SRCH_EMAIL_REGID));
		} else if (!StringUtils.isEmpty(this.getRegistrySearchVO()
				.getFirstName())
				|| !StringUtils.isEmpty(this.getRegistrySearchVO()
						.getLastName())) {

			this.validateFirstName();

			this.validateLastName();
		} else if (!StringUtils.isEmpty(this.getRegistrySearchVO().getEmail())) {

			if (!BBBUtility.isEmpty(this.getRegistrySearchVO().getEmail())
					&& !BBBUtility.isValidEmail(this.getRegistrySearchVO()
							.getEmail())) {

				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"GiftRegistry Invalid search email from searchLegacyRegistry of SimplifyRegFormHandler",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1041));

				this.addFormException(new DropletFormException(
						BBBGiftRegistryConstants.ERR_CREATE_REG_INVALID_EMAIL_LENGTH,
						this.getAbsoluteName()
								+ BBBGiftRegistryConstants.DOT_SEPARATOR
								+ BBBGiftRegistryConstants.COREG_EMAIL,
						BBBGiftRegistryConstants.ERR_CREATE_REG_INVALID_EMAIL_LENGTH));
			}
		} else if (!StringUtils.isEmpty(this.getRegistrySearchVO()
				.getRegistryId())
				&& !BBBUtility.isValidateRegistryId(this.getRegistrySearchVO()
						.getRegistryId())) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry Invalid registry id from preRegistrySearch of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1045));

			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_CREATE_REG_REGISTRY_ID_INVALID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REGISTRY_SEARCH_REGID,
					BBBGiftRegistryConstants.ERR_CREATE_REG_REGISTRY_ID_INVALID));
		}
	}

	/**
	 * Validate first name.
	 */
	private void validateFirstName() {
		this.logDebug("SimplifyRegFormHandler.validateFirstName() method starts");

		if (!StringUtils.isEmpty(this.getRegistrySearchVO().getFirstName())
				&& (this.getRegistrySearchVO().getFirstName().length() < 1)) {
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REGISTRY_SEARCH_FIRST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_FIRST_NAME_INVALID));
		}
		this.logDebug("SimplifyRegFormHandler.validateFirstName() method ends");

	}

	/**
	 * Validate last name.
	 */
	private void validateLastName() {
		this.logDebug("SimplifyRegFormHandler.validateLastName() method starts");

		if (StringUtils.isEmpty(this.getRegistrySearchVO().getLastName())
				|| !BBBUtility.isValidLastName(this.getRegistrySearchVO()
						.getLastName())) {
			this.addFormException(new DropletFormException(
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID,
					this.getAbsoluteName()
							+ BBBGiftRegistryConstants.DOT_SEPARATOR
							+ BBBGiftRegistryConstants.REGISTRY_SEARCH_LAST_NAME,
					BBBGiftRegistryConstants.ERR_CREATE_REG_LAST_NAME_INVALID));

		}
		this.logDebug("SimplifyRegFormHandler.validateLastName() method ends");

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
	public void handleClearSessionBeanData(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug("SimplifyRegFormHandler.handleClearSessionBeanData() method starts");
		final Profile profile = (Profile) pRequest.resolveName(ComponentName
				.getComponentName(BBBCoreConstants.ATG_PROFILE));
		String kickStarterId = this.getSessionBean().getKickStarterId();
		String eventType = this.getSessionBean().getEventType();
		if (!profile.isTransient()) {
			this.getSessionBean().setGiftRegistryViewBean(null);
		}
       if(kickStarterId!=null && eventType!=null){
    	   this.getSessionBean().setKickStarterId(kickStarterId);
    	   this.getSessionBean().setEventType(eventType);
       }
		this.logDebug("SimplifyRegFormHandler.handleClearSessionBeanData() method ends");

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
	@SuppressWarnings("rawtypes")
	public boolean handleAddItemToGiftRegistryFromCetona(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug("SimplifyRegFormHandler.handleAddItemToGiftRegistryFromCetona() method starts");
		EximSummaryResponseVO eximSummaryResponseVO = null;
		final GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
		final AddItemsBean addItemsBean = new AddItemsBean();
		final String siteId = this.getSiteContext().getSite().getId();
		final List<AddItemsBean> lstaddItems = new ArrayList<AddItemsBean>();
		try {
			
			// add registry owner check
			final Profile profile = (Profile) pRequest.resolveName(ComponentName
							.getComponentName(BBBCoreConstants.ATG_PROFILE));
			if (!profile.isTransient()) {
				this.isRegistryOwnedByProfile(this.getRegistryId());
			}

			giftRegistryViewBean.setSiteFlag(this
					.getBbbCatalogTools()
					.getAllValuesForKey(
							BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
							siteId).get(0));
			giftRegistryViewBean.setUserToken(this
					.getBbbCatalogTools()
					.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
							BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
            giftRegistryViewBean.setServiceName(this.getAddItemsToReg2ServiceName());
            
            this.setAddItemsBean(giftRegistryViewBean, addItemsBean, siteId);

            this.logDebug("PersonalizationCode: " + getPersonalizationCode() + " RefNum: " + getRefNum());
			lstaddItems.add(addItemsBean);
            giftRegistryViewBean.setAdditem(lstaddItems);
			if (BBBUtility.isNotEmpty(getRefNum()) && BBBUtility.isNotEmpty(getPersonalizationCode())) {
				eximSummaryResponseVO = getEximPricingManager()
						.invokeSummaryAPI(addItemsBean.getProductId(),null, getRefNum());
			}
			setGiftRegistryBean(getRefNum(), giftRegistryViewBean,
					eximSummaryResponseVO, getPersonalizationCode());

			this.logDebug("siteFlag: " + giftRegistryViewBean.getSiteFlag());
			this.logDebug("userToken: " + giftRegistryViewBean.getUserToken());
			this.logDebug("ServiceName: " + this.getAddItemsToRegServiceName());

			giftRegistryViewBean.setRegistrySize(giftRegistryViewBean
					.getAdditem().size());

			this.preAddItemToGiftRegistry(pRequest, pResponse,
					giftRegistryViewBean);
			final ValidateAddItemsResVO addItemsResVO = this
					.getGiftRegistryManager().addItemToGiftRegistry(
							giftRegistryViewBean);
			if (!addItemsResVO.getServiceErrorVO().isErrorExists()) {
				this.setErrorFlagAddItemToRegistry(false);
				// Update session data so that it will be in sync with the
				// registered items on registry flyout.
				final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
						.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
				final HashMap sessionMap = sessionBean.getValues();
				final RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap
						.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
				if ((registrySummaryVO != null)
						&& registrySummaryVO.getRegistryId().equalsIgnoreCase(
								this.getRegistryId())) {
					int totalQuantity = 0;
					totalQuantity += Integer.parseInt(addItemsBean
							.getQuantity());

					// update quantity in session
					totalQuantity = totalQuantity
							+ registrySummaryVO.getGiftRegistered();
					registrySummaryVO.setGiftRegistered(totalQuantity);
				}
				this.setRegistryItemOperation(BBBGiftRegistryConstants.GR_ADD_ITEM_CERTONA);
                final String priceTotal = this.totalPrice(
						addItemsBean.getQuantity(), addItemsBean.getPrice());
				final String omniProductList = ";"
						+ addItemsBean.getProductId() + ";;;event22="
						+ addItemsBean.getQuantity() + "|event23=" + priceTotal
						+ ";eVar30=" + addItemsBean.getSku();

				this.setProductStringAddItemCertona(omniProductList);				
				this.setItemAddedToRegistry(true);// for defect PS-17524 
				return this.checkFormRedirect(this.getSuccessURL(),this.getErrorURL(), pRequest, pResponse);
			}
			this.errorAdditemCertona(pRequest, addItemsResVO);
			this.setErrorURL(this.getSuccessURL());
			return this.checkFormRedirect(this.getSuccessURL(),	this.getErrorURL(), pRequest, pResponse);

		} catch (final BBBBusinessException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBBusinessException from handleAddItemToGiftRegistryFromCetona of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),
					e);
			this.setErrorFlagAddItemToRegistry(true);
			this.addFormException(new DropletException(this
					.getMessageHandler().getErrMsg(
							BBBGiftRegistryConstants.BUSINESS_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.BUSINESS_EXCEPTION));

		} catch (final BBBSystemException es) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBBusinessException from handleAddItemToGiftRegistryFromCetona of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),
					es);
			this.setErrorFlagAddItemToRegistry(true);
			this.addFormException(new DropletException(this
					.getMessageHandler().getErrMsg(
							BBBGiftRegistryConstants.SYSTEM_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.SYSTEM_EXCEPTION));
		} catch (final NumberFormatException es) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"NumberFormatException from handleAddItemToGiftRegistryFromCetona of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10101),
					es);
			this.setErrorFlagAddItemToRegistry(true);
			
		}
		return this.isErrorFlagAddItemToRegistry();
	}
	/**
	 * 
	 * @param giftRegistryViewBean the giftRegistryViewBean
	 * @param addItemsBean the addItemsBean
	 * @param siteId the siteId
	 * @throws BBBSystemException
	 * 			Signals that an BBBSystemException exception has occurred.
	 * @throws BBBBusinessException
	 * 			Signals that an BBBBusinessException exception has occurred.
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
		if(isSkuLtl){
		    addItemsBean.setLtlDeliveryServices(this.getLtlDeliveryServices()==null?BBBCoreConstants.BLANK:this.getLtlDeliveryServices());
		    addItemsBean.setLtlDeliveryPrices(this.getLtlDeliveryPrices()==null?BBBCoreConstants.BLANK:this.getLtlDeliveryPrices());
			if (!StringUtils.isEmpty(this.getLtlDeliveryServices())) {
		    	addItemsBean.setLtlDeliveryPrices(String
						.valueOf(getBbbCatalogTools().getDeliveryCharge(
								siteId, this.getSkuIds(),
								this.getLtlDeliveryServices())
								* Double.valueOf(this.getQuantity())));
		    	}
		}
		addItemsBean.setPersonalizationCode(this.getPersonalizationCode());
		giftRegistryViewBean.setParentProductId(addItemsBean.getProductId());
		// set price
		final double listPrice = this
				.getBbbCatalogTools()
				.getListPrice(addItemsBean.getProductId(),
						addItemsBean.getSku()).doubleValue();
		final double salePrice = this
				.getBbbCatalogTools()
				.getSalePrice(addItemsBean.getProductId(),
						addItemsBean.getSku()).doubleValue();
		if ((salePrice > 0)) {
			addItemsBean.setPrice(Double.toString(salePrice));
		} else {
			addItemsBean.setPrice(Double.toString(listPrice));
		}
	}

	private void errorAdditemCertona(final DynamoHttpServletRequest pRequest,
			final ValidateAddItemsResVO addItemsResVO) {

		if (!BBBUtility.isEmpty(addItemsResVO.getServiceErrorVO()
				.getErrorMessage())
				&& (addItemsResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR))// Technical
		// Error
		{
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Fatal error from errorAdditemCertona of SimplifyRegFormHandler | webservice code ="
									+ addItemsResVO.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10102));
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
		} else if (!BBBUtility.isEmpty(addItemsResVO.getServiceErrorVO()
				.getErrorMessage())
				&& (addItemsResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN))// Technical
		// Error
		{
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Either user token or site flag invalid from errorAdditemCertona of SimplifyRegFormHandler | webservice code ="
									+ addItemsResVO.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10103));
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));
		} else if (!BBBUtility.isEmpty(addItemsResVO.getServiceErrorVO()
				.getErrorMessage())
				&& (addItemsResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT))// Technical
		// Error
		{
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry input fields format error from handleAddItemToGiftRegistry() of "
									+ "SimplifyRegFormHandler | webservice error code="
									+ addItemsResVO.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
		} else {
			this.addFormException(new DropletFormException(addItemsResVO
					.getServiceErrorVO().getErrorMessage(), this
					.getAbsoluteName()
					+ BBBGiftRegistryConstants.DOT_SEPARATOR
					+ addItemsResVO.getServiceErrorVO().getErrorId(),
					addItemsResVO.getServiceErrorVO().getErrorMessage()));
			this.addFormException(new DropletException(this
					.getMessageHandler().getErrMsg(
							BBBGiftRegistryConstants.SYSTEM_EXCEPTION,
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean handleAddAllItemsToGiftRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		
		BBBPerformanceMonitor.start( SimplifyRegFormHandler.class.getName() + " : " + "handleAddAllItemsToGiftRegistry");
		logDebug("Start.handleAddAllItemsToGiftRegistry");

		this.getSessionBean().setAddALLActgion(BBBGiftRegistryConstants.ADD_ALL_ITEMS);
		this.setKickStarterAddAllAction(BBBGiftRegistryConstants.ADD_ALL_ITEMS);
		this.handleAddItemToGiftRegistry(pRequest, pResponse);
		logDebug("Exit.handleAddAllItemsToGiftRegistry");
		
		BBBPerformanceMonitor.end( SimplifyRegFormHandler.class.getName() + " : " + "handleAddAllItemsToGiftRegistry");
		return isCcFlag();
	}
		
	public boolean recommendationHandling(DynamoHttpServletRequest request,DynamoHttpServletResponse response , Profile profile){
		try {
			logDebug("Entering in  recommendationHandling method");
			String registryId = getRegId();
			String skuId=getSkuId();
			String comment=getComment();
			String quantity = getRecommendedQuantity();
			String recommenderProfileId = profile.getRepositoryId();
			Long recommendequantity = Long.valueOf(quantity);
			request.setParameter("registryId", "");
			GiftRegistryRecommendationManager giftRegistryRecommendationManager = (GiftRegistryRecommendationManager) Nucleus
				.getGlobalNucleus()
				.resolveName(
					"/com/bbb/commerce/giftregistry/manager/GiftRegistryRecommendationManager");
			request.resolveName("/com/bbb/commerce/giftregistry/manager/GiftRegistryRecommendationManager");
			logDebug("registry id= " + registryId + "skuid= " + skuId 
					+ "Quantity= " + recommendequantity 
					+ "recommenderProfileId= " + recommenderProfileId 
					+"comment= " + comment );
			giftRegistryRecommendationManager
					.createRegistryRecommendationProduct(registryId, skuId, 
							comment, recommendequantity, recommenderProfileId);
		} catch (BBBSystemException e) {
			logError("Error while adding recommmendation's"+e.getMessage(),e);
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.ERR_ADD_RECOMM_BIZ_EXCEPTION, 
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
	public boolean handleAddItemToGiftRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug("SimplifyRegFormHandler.handleAddItemToGiftRegistry() method starts...");		
		final Profile profile = (Profile) pRequest.resolveName(ComponentName
				.getComponentName(BBBCoreConstants.ATG_PROFILE));
		if(this.getRecommededFlag().equalsIgnoreCase(BBBCoreConstants.TRUE)){
			recommendationHandling(pRequest, pResponse, profile);
			return this.checkFormRedirect(this.getRecomPopUpSuccessURL(),
					this.getRecomPopUpErrorURL(), pRequest, pResponse);
		}else{
		try {
			final GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
			this.setAlternateNum(null);
			//Code for checking if alternate number is present in json object for updating to registry
			if(BBBUtility.isNotEmpty(this.getJasonCollectionObj()) && this.getJasonCollectionObj().contains(BBBCoreConstants.ALTERNATE_NUM)){
				int start=this.getJasonCollectionObj().indexOf(BBBCoreConstants.ALTERNATE_NUM);
				int ind=this.getJasonCollectionObj().indexOf(":" ,start);
				int end=this.getJasonCollectionObj().indexOf("\"" ,ind+2);
				this.setAlternateNum(this.getJasonCollectionObj().substring(ind+2, end));
			}
		  
		  if(BBBUtility.isNotEmpty(this.getAlternateNum()) && !BBBUtility.isValidPhoneNumber(this.getAlternateNum())){
			  this.setErrorFlagAddItemToRegistry(true);
			  this.addFormException(new DropletException(
						BBBCoreConstants.INVALID_PHONE,
						BBBCoreConstants.INVALID_PHONE));
			  logError(BBBCoreConstants.INVALID_PHONE);
			  return this.isErrorFlagAddItemToRegistry();
		  }
		  if (this.getJasonCollectionObj() != null 
		  			&& !(getKickStarterAddAllAction()!=null) ) {
				String countryCode=	(String) profile
						.getPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE);
				String currencyCode=	(String) profile
						.getPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE);
				Boolean internationalContext= (Boolean) profile
						.getPropertyValue(BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT);
				giftRegistryViewBean.setUserCountry(countryCode);
				giftRegistryViewBean.setUserCurrency(currencyCode);
				giftRegistryViewBean
						.setInternationalContext(internationalContext);
				this.getGiftRegistryManager()
						.getGiftRegistryTools()
						.addItemJSONObjectParser(giftRegistryViewBean,
								this.getJasonCollectionObj());
			}
			if(this.getJasonCollectionObj()!=null){
					if (this.getJasonCollectionObj().contains(
							BBBGiftRegistryConstants.IS_FROM_PENDING_TAB)) { 
						if (giftRegistryViewBean.getIsDeclined().equals(
								BBBCoreConstants.FALSE)) {
							boolean returnValue = getGiftRegistryRecommendationManager()
									.acceptRecommendation(giftRegistryViewBean,
											false);
							if (!returnValue) {
								this.addFormException(new DropletException(
										BBBGiftRegistryConstants.ERR_FETCHING_PROFILE,
										BBBGiftRegistryConstants.ERR_MSG_FETCHING_PROFILE));
								logError(BBBGiftRegistryConstants.ERR_MSG_FETCHING_PROFILE);
							}
					}else{
								boolean returnValue = getGiftRegistryRecommendationManager()
										.declinePendingItems(giftRegistryViewBean);
								if (!returnValue) {
									this.addFormException(new DropletException(
											BBBGiftRegistryConstants.ERR_FETCHING_PROFILE,
											BBBGiftRegistryConstants.ERR_MSG_FETCHING_PROFILE));
									logError(BBBGiftRegistryConstants.ERR_MSG_FETCHING_PROFILE);
					}
								return returnValue;
							}
					} else if (this.getJasonCollectionObj().contains(
							BBBGiftRegistryConstants.IS_FROM_DECLINED_TAB)) {
						boolean returnValue = getGiftRegistryRecommendationManager()
								.acceptRecommendation(giftRegistryViewBean,
										true);
						if (!returnValue) {
							this.addFormException(new DropletException(
									BBBGiftRegistryConstants.ERR_FETCHING_PROFILE,
									BBBGiftRegistryConstants.ERR_MSG_FETCHING_PROFILE));
							logError(BBBGiftRegistryConstants.ERR_MSG_FETCHING_PROFILE);
				}
			}
				}
		  	if(getKickStarterAddAllAction()!=null){
				this.getKickStarterManager()
					.setKickStarterItemsIntoSessionVo(
							this.getJasonCollectionObj(),
							giftRegistryViewBean, this.getSessionBean());
				this.getSessionBean().setAddALLActgion(
						BBBGiftRegistryConstants.ADD_ALL_ITEMS);
				if(giftRegistryViewBean.getAdditem()==null){
					return true;
				}
			}
			// added for save for later
			else if (this.isMoveItemFromSaveForLater()) {
				if (StringUtils.isEmpty(this.getWishListItemId())) {
					this.addFormException(new DropletException(
							BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_WISHLIST_ID_MSG,
							BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_WISHLIST_ID));
					return this.isErrorFlagAddItemToRegistry();
				}
				try {
					final RepositoryItem wishListItem = this.getGiftListManager()
							.getGiftitem(this.getWishListItemId());
					if (wishListItem == null) {
						this.addFormException(new DropletException(
								BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID_MSG,
								BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID));
						return this.isErrorFlagAddItemToRegistry();
					}

					final List<AddItemsBean> additemList = new ArrayList<AddItemsBean>();
					final AddItemsBean addItemsBean = new AddItemsBean();
					addItemsBean.setRefNum(getRefNum());
					addItemsBean.setPersonalizationCode(getPersonalizationType());
					addItemsBean
						.setSku((String) wishListItem
							.getPropertyValue(BBBCoreConstants.CATALOG_REF_ID));
					addItemsBean.setProductId((String) wishListItem
							.getPropertyValue(BBBCoreConstants.PRODUCTID));
					addItemsBean.setRegistryId(this.getRegistryId());
					addItemsBean
							.setQuantity(((Long) wishListItem
									.getPropertyValue(BBBCoreConstants.QUANTITY_DESIRED))
									.toString());
						addItemsBean.setCustomizationRequired(this.getCustomizationReq());
						//Added for LTL items
						
						if(BBBUtility.isNotEmpty(this.getLtlDeliveryServices())){
							addItemsBean.setLtlDeliveryServices(this.getLtlDeliveryServices());
							addItemsBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK+(this.getBbbCatalogTools().getDeliveryCharge(SiteContextManager.getCurrentSiteId(),
									addItemsBean.getSku(), addItemsBean.getLtlDeliveryServices())));
							}
						if(BBBUtility.isNotEmpty(this.getAlternateNumber())){
							this.setAlternateNum(this.getAlternateNumber());
						}
						
					final RegistryVO registryVO = this.getGiftRegistryManager()
							.getRegistryDetailInfo(this.getRegistryId(),
									SiteContextManager.getCurrentSiteId());
					final String registryCodeType = registryVO
							.getRegistryType().getRegistryTypeName();
					final String giftRegistryName = this
							.getGiftRegistryManager()
							.getRegistryNameForRegCode(registryCodeType);
					this.getCertonaParameter()
							.setRegistryName(giftRegistryName);
					this.getCertonaParameter().setItemId(
							addItemsBean.getProductId());
					this.getCertonaParameter().setQuantity(
							Integer.parseInt(addItemsBean.getQuantity()));
					this.getCertonaParameter().setTransactionId(
							this.getRegistryId());
					this.getCertonaParameter().setCustomerId(
							profile.getRepositoryId());

					// set price
					final double listPrice = this
							.getBbbCatalogTools()
							.getListPrice(addItemsBean.getProductId(),
									addItemsBean.getSku()).doubleValue();
					final double salePrice = this
							.getBbbCatalogTools()
							.getSalePrice(addItemsBean.getProductId(),
									addItemsBean.getSku()).doubleValue();
					if ((salePrice > 0)) {
						addItemsBean.setPrice(Double.toString(salePrice));
					} else {
						addItemsBean.setPrice(Double.toString(listPrice));
					}
					this.getCertonaParameter().setPrice(
							Double.parseDouble(addItemsBean.getPrice()));
					additemList.add(addItemsBean);
					giftRegistryViewBean.setAdditem(additemList);
					giftRegistryViewBean.setParentProductId(addItemsBean
							.getProductId());
					giftRegistryViewBean.setRegistryId(this.getRegistryId());
					giftRegistryViewBean
							.setQuantity(addItemsBean.getQuantity());
					giftRegistryViewBean
							.setRegistryName(this.getRegistryName());

				} catch (final CommerceException e) {
					this.logError(
							LogMessageFormatter
									.formatMessage(
											pRequest,
											"CommerceException from handleAddItemToGiftRegistry of SimplifyRegFormHandler",
											BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),
							e);
					this.addFormException(new DropletException(
							BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM_MSG,
							BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM));
				} catch (final RepositoryException e) {
					this.logError(
							LogMessageFormatter
									.formatMessage(
											pRequest,
											"RepositoryException from handleAddItemToGiftRegistry of SimplifyRegFormHandler while removing item",
											BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),
							e);
					this.addFormException(new DropletException(
							BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM_MSG,
							BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM));

				}
			}
			if (!profile.isTransient()) {
				this.isRegistryOwnedByProfile(giftRegistryViewBean
						.getRegistryId());
			}
			final String siteId = this.getSiteContext().getSite().getId();
			giftRegistryViewBean.setSiteFlag(this
					.getBbbCatalogTools()
					.getAllValuesForKey(
							BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
							siteId).get(0));
			giftRegistryViewBean.setUserToken(this
					.getBbbCatalogTools()
					.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
							BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
							.get(0));
				String refNum = giftRegistryViewBean.getAdditem().get(0).getRefNum();
				String  personalizationCode = giftRegistryViewBean.getAdditem().get(0).getPersonalizationCode();
				EximSummaryResponseVO eximSummaryResponseVO = null;
				String omniProductList =giftRegistryViewBean.getOmniProductList();
				if(refNum!=null && !refNum.isEmpty()){
					eximSummaryResponseVO =  getEximPricingManager().invokeSummaryAPI(giftRegistryViewBean.getAdditem().get(0).getProductId(), null, refNum);				
					if(null != eximSummaryResponseVO && null != eximSummaryResponseVO.getCustomizations() && !eximSummaryResponseVO.getCustomizations().isEmpty()) {
							EximCustomizedAttributesVO eximAtrributes = eximSummaryResponseVO.getCustomizations().get(0);
							if (null != eximAtrributes && eximAtrributes.getErrors().isEmpty()) {					
								omniProductList +="|eVar54=" + getEximPricingManager().getEximValueMap().get(eximAtrributes.getCustomizationService());
							}
						}										
				}
				giftRegistryViewBean.setServiceName(this.getAddItemsToReg2ServiceName());				
				giftRegistryViewBean.setOmniProductList(omniProductList);
				setGiftRegistryBean(refNum,giftRegistryViewBean,eximSummaryResponseVO,personalizationCode);
			this.logDebug("siteFlag: " + giftRegistryViewBean.getSiteFlag());
			this.logDebug("userToken: " 
					+ giftRegistryViewBean.getUserToken());
			this.logDebug("ServiceName: " 
						+ giftRegistryViewBean.getServiceName());			
			giftRegistryViewBean.setRegistrySize(giftRegistryViewBean
					.getAdditem().size());
			final String parentProdId = giftRegistryViewBean
					.getParentProductId();
			String appendData = BBBCoreConstants.BLANK;
			if (giftRegistryViewBean.getAdditem().size() > 0) {
				if (!StringUtils.isBlank(giftRegistryViewBean.getAdditem()
						.get(0).getSku())) {
					appendData = "&skuId="
							+ giftRegistryViewBean.getAdditem().get(0).getSku();
				}
				if (!StringUtils.isBlank(giftRegistryViewBean.getAdditem()
						.get(0).getParamRegistryId())) {
					appendData = appendData
							+ "&registryId="
							+ giftRegistryViewBean.getAdditem().get(0)
									.getParamRegistryId();
				}
				if (!StringUtils.isBlank(giftRegistryViewBean.getAdditem()
						.get(0).getProductId())) {
					appendData = appendData
							+ "&regProdId="
							+ giftRegistryViewBean.getAdditem().get(0)
									.getProductId();
				}
					if (!StringUtils.isBlank((String)pRequest.getParameter("ltlFlag"))) {
						appendData = appendData
								+ "&ltlFlag="
								+ pRequest.getParameter("ltlFlag");
			}
			// Redirect to product comparison page for anonymous user if Add to Registry is called from compare page. R2.2 Story 178-A4 : Start
			try {
				if(this.getJasonCollectionObj() != null){
					JSONObject jsonObject = new JSONObject(
							this.getJasonCollectionObj());
					if(jsonObject != null 
							&& jsonObject.has("fromComparisonPage") 
							&& !BBBUtility.isEmpty(jsonObject
									.getString("fromComparisonPage")))
						this.setSuccessURL(BBBGiftRegistryConstants.COMPARE_SUCCESS_URL
								+ parentProdId + appendData);
					else if(jsonObject != null 
							&& jsonObject.has("returnURL") 
							&& !BBBUtility.isEmpty(jsonObject
							.getString("returnURL"))) {
						this.setSuccessURL(jsonObject
								.getString("returnURL"));
					} else{
						this.setSuccessURL(BBBGiftRegistryConstants.SUCCESS_URL_ADD_ITEM
								+ parentProdId + appendData);
					}
					if(!BBBUtility.isEmpty(this.getSuccessURL()))
						giftRegistryViewBean.setSuccessURL(this.getSuccessURL());
				}
			} catch (JSONException excep) {
				if (this.isLoggingError()) {
					this.logError(LogMessageFormatter.formatMessage(pRequest, "JSONexception in SimplifyRegFormHandler.handleAddItemToGiftRegistry", BBBCoreErrorConstants.ACCOUNT_ERROR_1261 ), excep);
				}
			}
			
			this.getSessionBean().setGiftRegistryViewBean(giftRegistryViewBean);
			if (profile.isTransient()) {

				final String loginFrom = BBBWebServiceConstants.TXT_LOGIN_ADD_REGISTRY;

				profile.setPropertyValue(BBBWebServiceConstants.LOGIN_FROM,
						loginFrom);

				pResponse.setHeader("BBB-ajax-redirect-url",
						getLoginRedirectUrl());
			} else {
				this.preAddItemToGiftRegistry(pRequest, pResponse,
						giftRegistryViewBean);
				ValidateAddItemsResVO addItemsResVO = null;
				if(!(getKickStarterAddAllAction()!=null && getKickStarterAddAllAction().equals(BBBGiftRegistryConstants.ADD_ALL_ITEMS))){
					addItemsResVO = this.getGiftRegistryManager().addItemToGiftRegistry(giftRegistryViewBean); 
				}else{
					addItemsResVO = this.getGiftRegistryManager().addBulkItemsToGiftRegistry(giftRegistryViewBean, getSessionBean());	
				}
				if (!addItemsResVO.getServiceErrorVO().isErrorExists()) {
		
					final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
							.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
						 getGiftRegistryManager().getGiftRegistryTools().invalidateRegistry(giftRegistryViewBean);
						 RegistryResVO registryResVO = getGiftRegistryManager().getRegistryInfoFromEcomAdmin(giftRegistryViewBean.getRegistryId(), siteId);
						 sessionBean.getValues().put(giftRegistryViewBean.getRegistryId()+REG_SUMMARY_KEY_CONST, registryResVO);
						//Code for updating  alternate number in registry
						 if(registryResVO!=null && BBBUtility.isNotEmpty(this.getAlternateNum())){
							 registryResVO.getRegistryVO().getPrimaryRegistrant().setCellPhone(this.getAlternateNum());
							 if(!siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
								 registryResVO.getRegistryVO().getEvent().setEventDate(BBBUtility.convertWSDateToUSFormat(registryResVO.getRegistryVO().getEvent()
											.getEventDate()));
								 if(BBBUtility.isNotEmpty(registryResVO.getRegistryVO().getEvent().getShowerDate())){
									 registryResVO.getRegistryVO().getEvent().setShowerDate(BBBUtility.convertWSDateToUSFormat(registryResVO.getRegistryVO().getEvent()
											.getShowerDate()));
								 }
								 if(BBBUtility.isNotEmpty(registryResVO.getRegistryVO().getEvent().getBirthDate())){
									 registryResVO.getRegistryVO().getEvent().setBirthDate(BBBUtility.convertWSDateToUSFormat(registryResVO.getRegistryVO().getEvent()
											.getBirthDate()));
								 }
								 if ((this.getRegistryVO().getShipping() != null)
											&& !StringUtils.isEmpty(this.getRegistryVO().getShipping()
													.getFutureShippingDate())) {
									 registryResVO.getRegistryVO().getShipping().setFutureShippingDate(BBBUtility.convertWSDateToUSFormat(registryResVO.getRegistryVO().getShipping()
												.getFutureShippingDate()));
								 }
							 }
							 this.setRegistryVO(registryResVO.getRegistryVO());
							 this.getSessionBean().setRegistryTypesEvent(registryResVO.getRegistryVO().getRegistryType().getRegistryTypeName());
							 pRequest.setParameter(BBBCoreConstants.ALTERNATE_NUM, BBBCoreConstants.TRUE);
							 this.handleUpdateRegistry(pRequest, pResponse);
						 }
					
					final String channel = pRequest
							.getHeader(BBBCoreConstants.CHANNEL);
					if (BBBUtility.isNotEmpty(channel)
							&& (channel
							.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel
							.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))
							&& giftRegistryViewBean != null 
							&& giftRegistryViewBean.getAdditem() != null
							&& giftRegistryViewBean.getAdditem().size() == 1) {	
					RegistryItemsListVO registryItemsListVO = new RegistryItemsListVO();
					registryItemsListVO = getRegistryItemList(
							giftRegistryViewBean, siteId);
					List<RegistryItemVO> registryItemsList = registryItemsListVO
							.getRegistryItemList();
					Iterator<RegistryItemVO> registryItemsIterator = registryItemsList
							.iterator();
					ItemDetailsVO itemDetailsVO = new ItemDetailsVO();
					AddItemsBean addItemsBean1 = giftRegistryViewBean
					.getAdditem().get(0);
					long skuIdFromAddItem = Long.parseLong(addItemsBean1
								.getSku());
					while (registryItemsIterator.hasNext()) {
						RegistryItemVO registryItemVo = registryItemsIterator
								.next();
						if (registryItemVo.getSku() == skuIdFromAddItem) {
							itemDetailsVO.setRowId(registryItemVo.getRowID());
							itemDetailsVO.setPurchasedQuantity(registryItemVo
								.getQtyPurchased());
							itemDetailsVO.setRegItemOldQty(registryItemVo
								.getQtyRequested());
							itemDetailsVO.setSkuId(registryItemVo.getSku());
							break;
						}
						
					}
					setItemDetailsVO(itemDetailsVO);
					}
					this.setErrorFlagAddItemToRegistry(false);
					// Update session data so that it will be in sync with the
					// registred items on registry flyout.
					final HashMap sessionMap = sessionBean.getValues();
					final RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap
							.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
					if ((registrySummaryVO != null)
							&& registrySummaryVO.getRegistryId()
									.equalsIgnoreCase(
											giftRegistryViewBean
													.getRegistryId())) {
						int totalQuantity = 0;

						final List<AddItemsBean> additemList = giftRegistryViewBean
								.getAdditem();

						for (final AddItemsBean addItemsBean : additemList) {
							totalQuantity += Integer.parseInt(addItemsBean
									.getQuantity());
						}

						// update quantiry in session
						totalQuantity = totalQuantity
								+ registrySummaryVO.getGiftRegistered();
						registrySummaryVO.setGiftRegistered(totalQuantity);

					} else if ((registrySummaryVO != null)
							&& !registrySummaryVO.getRegistryId().equalsIgnoreCase(giftRegistryViewBean.getRegistryId())) {
						// update the registrysummaryvo in the BBBsessionBean
						final RegistrySummaryVO regSummaryVO = this.getGiftRegistryManager().getRegistryInfo(giftRegistryViewBean.getRegistryId(),siteId);
						 sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY,regSummaryVO);
					}
					this.getGiftRegSessionBean().setRegistryOperation(BBBGiftRegistryConstants.GR_ITEM_UPDATE);
					if (this.isFromWishListPage()) {
						updateWishListItems(pRequest, pResponse);
					}

				} else {
					this.errorAdditemCertona(pRequest, addItemsResVO);
				}
				// change for calculating total price (certona)
				final List<AddItemsBean> additemList = giftRegistryViewBean
						.getAdditem();
				double allItemTotalPrice = 0;
				for (final AddItemsBean addItemsBean : additemList) {
					if (addItemsBean != null) {
						allItemTotalPrice += Double.parseDouble(this.totalPrice(addItemsBean.getQuantity(),addItemsBean.getPrice()));
					}
				}
				this.setTotalPrice(String.valueOf(BBBUtility.round(allItemTotalPrice)));
				// change for calculating total price (certona)
			}
			this.logDebug("get handleAddItemToGiftRegistry value from request");
			this.logDebug("SimplifyRegFormHandler.handleAddItemToGiftRegistry() method ends");

		} 
		}catch (final BBBBusinessException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBBusinessException from handleAddItemToGiftRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),
					e);
			this.setErrorFlagAddItemToRegistry(true);
			this.addFormException(new DropletException(this
					.getMessageHandler().getErrMsg(
							BBBGiftRegistryConstants.BUSINESS_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.BUSINESS_EXCEPTION));

		} catch (final BBBSystemException es) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBBusinessException from handleAddItemToGiftRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),
					es);
			this.setErrorFlagAddItemToRegistry(true);
			this.addFormException(new DropletException(this
					.getMessageHandler().getErrMsg(
							BBBGiftRegistryConstants.SYSTEM_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.SYSTEM_EXCEPTION));
		} catch (final NumberFormatException es) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"NumberFormatException from handleAddItemToGiftRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10104),
					es);
			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.INVALID_QUANTITY_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null)));
			this.setErrorFlagAddItemToRegistry(true);
			this.setErrorURL(this.getSuccessURL());
		}
		return this.isErrorFlagAddItemToRegistry();

		}
	}
	/**
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	private void updateWishListItems(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		try {
			this.setMovedItemRegistryId(this.getRegistryId());

			this.setRegistryId(null);
			final RepositoryItem wishList = ((RepositoryItem) this
					.getProfile().getPropertyValue(WISH_LIST));
			final List<RepositoryItem> items = (List<RepositoryItem>) wishList
					.getPropertyValue(BBBCoreConstants.GIFT_LIST_ITEMS);
			final Iterator<RepositoryItem> itr = items
					.iterator();
			while (itr.hasNext()) {
				final MutableRepositoryItem item = (MutableRepositoryItem) itr
						.next();
				if (item.getRepositoryId().equalsIgnoreCase(
						this.getWishListItemId())) {
					if (!this.getMovedItemIndex().equals("0")) {
						this.getMovedItemMap()
								.put(this.getMovedItemIndex(),
										(String) item
												.getPropertyValue("productId"));
					}
					itr.remove();
				}
			}
			((MutableRepository) wishList.getRepository())
					.updateItem((MutableRepositoryItem) wishList);
			this.checkFormRedirect(
					this.getMoveToRegistryResponseURL(),
					this.getMoveToRegistryResponseURL(),
					pRequest, pResponse);
		} catch (final RepositoryException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"RepositoryException from handleAddItemToGiftRegistry of SimplifyRegFormHandler while removing item",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),
					e);
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM_MSG,
					BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM));
		}
	}

	private void setGiftRegistryBean(String refNum,
			GiftRegistryViewBean giftRegistryViewBean, EximSummaryResponseVO eximSummaryResponseVO , String personalizationCode ) {

		logDebug("Starting Method setGiftRegistryBean : Setting values in giftregistryview Bean");

		logDebug("REFNUM IS " + refNum + " PersonlizationCode is" + personalizationCode);

		if(refNum !=null && !refNum.isEmpty() && personalizationCode!=null && !personalizationCode.isEmpty() && null != eximSummaryResponseVO &&
				null != eximSummaryResponseVO.getCustomizations() && !eximSummaryResponseVO.getCustomizations().isEmpty()){
			EximCustomizedAttributesVO eximAtrributes = eximSummaryResponseVO.getCustomizations().get(0);
			giftRegistryViewBean.setRefNum(refNum);
			giftRegistryViewBean.setAssemblyPrices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setPersonlizationCodes(eximAtrributes.getCustomizationService());
			giftRegistryViewBean.setAssemblySelections(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setItemTypes("PER");
			giftRegistryViewBean.setLtlDeliveryPrices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setLtlDeliveryServices(BBBCoreConstants.BLANK);
			giftRegistryViewBean.setPersonalizationDescrips(eximAtrributes.getNamedrop());

			List<EximImagesVO> images = eximAtrributes.getImages();
			for(EximImagesVO imageVO : images) {
				if(imageVO.getId().equalsIgnoreCase(BBBCoreConstants.IMAGE_ID) || imageVO.getId().equalsIgnoreCase("")) {
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
				}else{
					giftRegistryViewBean.setPersonalizedImageUrls(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setPersonalizedImageUrlThumbs(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setPersonalizedMobImageUrlThumbs(BBBCoreConstants.BLANK);
					giftRegistryViewBean.setPersonalizedMobImageUrls(BBBCoreConstants.BLANK);
				}
			}

			if(giftRegistryViewBean.getAdditem().get(0).getPersonalizationCode().equalsIgnoreCase(BBBCoreConstants.PERSONALIZATION_CODE_CR)){


				giftRegistryViewBean.setPersonalizationPrices(Double.toString(eximAtrributes.getRetailPriceAdder()));
				giftRegistryViewBean.setCustomizationPrices(Double.toString(eximAtrributes.getRetailPriceAdder()));

			}else if(giftRegistryViewBean.getAdditem().get(0).getPersonalizationCode().equalsIgnoreCase(BBBCoreConstants.PERSONALIZATION_CODE_PB)){

				giftRegistryViewBean.setPersonalizationPrices(giftRegistryViewBean.getAdditem().get(0).getPrice());
				giftRegistryViewBean.setCustomizationPrices(Double.toString(eximAtrributes.getRetailPriceAdder()));


			}else if(giftRegistryViewBean.getAdditem().get(0).getPersonalizationCode().equalsIgnoreCase(BBBCoreConstants.PERSONALIZATION_CODE_PY)){

				String basePrice= giftRegistryViewBean.getAdditem().get(0).getPrice();

				Double personalisedPrice = eximAtrributes.getRetailPriceAdder() + Double.parseDouble(basePrice);

				giftRegistryViewBean.setPersonalizationPrices(Double.toString(personalisedPrice));
				giftRegistryViewBean.setCustomizationPrices(Double.toString(eximAtrributes.getRetailPriceAdder()));
			}

		}else{
			giftRegistryViewBean.setRefNum("");
			giftRegistryViewBean.setAssemblyPrices("");
			giftRegistryViewBean.setPersonlizationCodes("");
			giftRegistryViewBean.setAssemblySelections("");
			giftRegistryViewBean.setItemTypes("");
			giftRegistryViewBean.setLtlDeliveryPrices("");
			giftRegistryViewBean.setLtlDeliveryServices("");
			giftRegistryViewBean.setPersonalizationDescrips("");
			giftRegistryViewBean.setPersonalizationPrices("");
			giftRegistryViewBean.setPersonalizedImageUrls("");
			giftRegistryViewBean.setPersonalizedImageUrlThumbs("");
			giftRegistryViewBean.setPersonalizedMobImageUrls("");
			giftRegistryViewBean.setPersonalizedMobImageUrlThumbs("");
			giftRegistryViewBean.setCustomizationPrices("");

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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean handleEmailOptInChange(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug("SimplifyRegFormHandler.handleEmailOptIn() method starts");
		this.setErrorFlagEmailOptIn(false);
		try {
			getGiftRegistryRecommendationManager().setEmailOptInChange(
					this.getRegistryId(), this.getEmailOptIn());
		} catch (BBBSystemException e) {
			this.setErrorFlagEmailOptIn(true);
			logError("Exception while setting email options: " + e);
		}
		this.logDebug("SimplifyRegFormHandler.handleEmailOptIn() method ends with return parameter"
				+ " errorFlagEmailOptIn: " + this.isErrorFlagEmailOptIn());
		return this.isErrorFlagEmailOptIn();
	}
	/**
	 * Get RegistryItem List from Registry WS
	 *
	 * @param giftRegistryViewBean
	 * @param siteId
	 * @return RegistryItemsListVO
	 *
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private RegistryItemsListVO getRegistryItemList(final GiftRegistryViewBean giftRegistryViewBean, final String siteId){
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
			if ((getBbbCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY,
					BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)).size() != 0) {
				registrySearchVO.setUserToken(getBbbCatalogTools()
						.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
								.get(0));
			}
			if ((getBbbCatalogTools().getAllValuesForKey(
					BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId)).size() != 0) {
				registrySearchVO.setSiteId(getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).get(0));
			}
			if ((getBbbCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, "MaxSizeRegistryItems")).size() != 0) {
				registrySearchVO.setBlkSize(Integer.parseInt(getBbbCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS, "MaxSizeRegistryItems").get(0)));
			}
			registryItemsListVO = getGiftRegistryManager().fetchRegistryItems(registrySearchVO);
		} catch (BBBSystemException e) {
			logError("Exception while fetching registry items :" + e.getMessage());
		} catch (BBBBusinessException e) {
			logError("Exception while fetching registry items :" + e.getMessage());
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
	public void preAddItemToGiftRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse,
			final GiftRegistryViewBean giftRegistryViewBean)
			throws IOException, ServletException, NumberFormatException {

		this.logDebug("SimplifyRegFormHandler.preAddItemToGiftRegistry() method starts");

		this.logDebug("get preAddItemToGiftRegistry value from request");

		for (int i = 0; i < giftRegistryViewBean.getAdditem().size(); i++) {
			final AddItemsBean addItemsBean = giftRegistryViewBean.getAdditem()
					.get(i);
			if (addItemsBean.getQuantity() != null) {
				Integer.parseInt(addItemsBean.getQuantity());

				if (!BBBUtility.isValidNumber(addItemsBean.getQuantity())) {

					this.logError(LogMessageFormatter
							.formatMessage(
									pRequest,
									"GiftRegistry Invalid items quantity from preAddItemToGiftRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1056));

					this.addFormException(new DropletException(
							this.getMessageHandler()
									.getErrMsg(
											BBBGiftRegistryConstants.INVALID_QUANTITY_EXCEPTION,
											pRequest.getLocale().getLanguage(),
											null, null),
							BBBGiftRegistryConstants.INVALID_QUANTITY_EXCEPTION));
					throw new NumberFormatException(
							BBBGiftRegistryConstants.INVALID_QUANTITY_EXCEPTION);
				}

			} else {

				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"GiftRegistry empty items quantity from preAddItemToGiftRegistry of SimplifyRegFormHandler",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1055));

				this.addFormException(new DropletException(
						this.getMessageHandler().getErrMsg(
								BBBGiftRegistryConstants.QUANTITY_EXCEPTION,
								pRequest.getLocale().getLanguage(), null, null),
						BBBGiftRegistryConstants.QUANTITY_EXCEPTION));
				throw new NumberFormatException(
						BBBGiftRegistryConstants.QUANTITY_EXCEPTION);
			}
		}
		this.logDebug("SimplifyRegFormHandler.preAddItemToGiftRegistry() method ends");

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
	public boolean handleImportRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		this.logDebug("SimplifyRegFormHandler.handleImportRegistry() method starts");

		boolean isNoError = true;

		String errorVal = this.getMessageHandler().getErrMsg(
				BBBGiftRegistryConstants.IMPORT_EXCEPTION,
				pRequest.getLocale().getLanguage(), null, null);
		if (StringUtils.isEmpty(errorVal)) {
			errorVal = BBBGiftRegistryConstants.NO_DATA;
		}
		final Profile profile = (Profile) pRequest.resolveName(ComponentName
				.getComponentName(BBBCoreConstants.ATG_PROFILE));
		if ((this.getCheckForValidSession() && !this.isValidSession(pRequest))
				|| profile.isTransient()) {
			if (this.getCheckForValidSession()
					&& !this.isValidSession(pRequest)) {
				this.logError(LogMessageFormatter
						.formatMessage(
								pRequest,
								"Session expired exception from handleImportRegistry of SimplifyRegFormHandler",
								BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1059));
				this.addFormException(new DropletException(
						BBBGiftRegistryConstants.SESSION_EXCEPTION,
						BBBGiftRegistryConstants.SESSION_EXCEPTION));
			}
			this.getSessionBean().setImportRegistryRedirectUrl(
					BBBGiftRegistryConstants.SUCCESS_URL_IMPORT_REGISTRY);
			this.setImportURL(BBBGiftRegistryConstants.LOGIN_REDIRECT_URL);
			isNoError = false;

		}
		if (isNoError) {
			try {

				this.setRegistryVO(profile);
				if (this.preImportRegistry(pRequest, pResponse)) {

					final RegistryResVO importResVO = this
							.getGiftRegistryManager().importRegistry(profile,
									this.getRegistryVO());

					if (!importResVO.getServiceErrorVO().isErrorExists()) {

						this.getRegistryVO().getEvent()
								.setEventDate(this.getImportEventDate());

						this.getRegistryVO().getRegistryType()
								.setRegistryTypeName(this.getImportEventType());

						/**
						 * Link this registry as primary registrant or co
						 * registrant
						 */
						this.getGiftRegistryManager().linkRegistry(
								this.getRegistryVO(),
								importResVO.getImportedAsReg());

						// Invalidate registrylist and summaryVO in the session
						final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
								.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);

						List<String> userRegList = new ArrayList<String>();
						List<String> userActiveRegList = new ArrayList<String>();
						RegistrySummaryVO registrySummaryVO = null;
						userRegList = (List<String>) sessionBean
								.getValues()
								.get(BBBGiftRegistryConstants.USER_REGISTRIES_LIST);
						userActiveRegList = (List<String>) sessionBean
								.getValues()
								.get(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST);
						registrySummaryVO = (RegistrySummaryVO) sessionBean
								.getValues()
								.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
						if (userRegList != null) {
							userRegList.add(String.valueOf(this.getRegistryVO()
									.getRegistryId()));
						}
						if (userActiveRegList != null) {
							userActiveRegList.add(String.valueOf(this
									.getRegistryVO().getRegistryId()));
						}

						try {
							// Method call to sync the User Registries status
							// with legacy database

							this.getGiftRegistryManager()
									.updateProfileRegistriesStatus(
											this.getProfile(), this
													.getSiteContext().getSite()
													.getId());
						} catch (final BBBSystemException e) {
							this.logError(
									"BBBSystemException from mGiftRegistryManager.updateProfileRegistriesStatus method",
									e);
						} catch (final BBBBusinessException e) {
							this.logError(
									"BBBBusinessException from mGiftRegistryManager.updateProfileRegistriesStatus method",
									e);
						}

						sessionBean.getValues().put(
								BBBGiftRegistryConstants.USER_REGISTRIES_LIST,
								userRegList);
						sessionBean
								.getValues()
								.put(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST,
										userActiveRegList);
						sessionBean
								.getValues()
								.put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY,
										registrySummaryVO);
					} else {

						this.setFormExceptionsForImportErrors(pRequest,
								errorVal, importResVO);
						isNoError = false;
					}
				}
				this.logDebug("SimplifyRegFormHandler.handleImportRegistry() method ends");

			} catch (final BBBSystemException bbbsysexp) {

				this.logError(
						LogMessageFormatter
								.formatMessage(
										pRequest,
										"GiftRegistry error in importing registry from handleImportRegistry of SimplifyRegFormHandler",
										BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1052),
						bbbsysexp);

				this.addFormException(new DropletException(errorVal,
						BBBGiftRegistryConstants.IMPORT_EXCEPTION));
				this.setImportErrorMessage(errorVal);
				isNoError = false;
				return isNoError;
			}

			catch (final RepositoryException reposexp) {
				this.logError(
						LogMessageFormatter
								.formatMessage(
										pRequest,
										"RepositoryException from handleImportRegistry of SimplifyRegFormHandler",
										BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10105),
						reposexp);
				this.addFormException(new DropletException(
						this.getMessageHandler().getErrMsg(
								BBBGiftRegistryConstants.REPO_EXCEPTION,
								pRequest.getLocale().getLanguage(), null, null),
						BBBGiftRegistryConstants.REPO_EXCEPTION));
				this.setImportErrorMessage(this.getMessageHandler()
						.getErrMsg(BBBGiftRegistryConstants.REPO_EXCEPTION,
								pRequest.getLocale().getLanguage(), null, null));
				isNoError = false;
				return isNoError;
			} catch (final BBBBusinessException bbbbexp) {
				this.logError(
						LogMessageFormatter
								.formatMessage(
										pRequest,
										"BBBBusinessException from handleImportRegistry of SimplifyRegFormHandler",
										BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10106),
						bbbbexp);

				if (bbbbexp.getMessage().equals(
						BBBCoreConstants.PASSWORD_NOT_MATCH)) {

					this.logError(
							LogMessageFormatter
									.formatMessage(
											pRequest,
											"GiftRegistry Incorrect password from handleImportRegistry of SimplifyRegFormHandler",
											BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1050),
							bbbbexp);

					this.addFormException(new DropletException(
							this.getMessageHandler()
									.getErrMsg(
											BBBGiftRegistryConstants.PASSWORD_EXCEPTION,
											pRequest.getLocale().getLanguage(),
											null, null),
							BBBGiftRegistryConstants.PASSWORD_EXCEPTION));
					this.setImportErrorMessage(this
							.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.PASSWORD_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null));
					isNoError = false;
				} else {
					this.addFormException(new DropletException(errorVal,
							BBBGiftRegistryConstants.IMPORT_EXCEPTION));
					isNoError = false;
					this.setImportErrorMessage(errorVal);
					return isNoError;
				}

			}
		}

		return isNoError;
	}

	private void setFormExceptionsForImportErrors(
			final DynamoHttpServletRequest pRequest, final String errorVal,
			final RegistryResVO importResVO) {

		String errorMsg = null;
		if (importResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REG_INVALID_REG_PASSWORD) {
			errorMsg = this.getMessageHandler().getErrMsg(
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_PASSWORD,
					pRequest.getLocale().getLanguage(), null, null);
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Invalid password GiftRegistry from setFormExceptionsForImportErrors of SimplifyRegFormHandler Webservice code ="
									+ importResVO.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10108));

		} else if (importResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS) {
			errorMsg = this.getMessageHandler().getErrMsg(
					BBBGiftRegistryConstants.ERR_GIFT_REG_INPUT_FIELD_ERROR,
					pRequest.getLocale().getLanguage(), null, null);
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Invalid input fields from setFormExceptionsForImportErrors of SimplifyRegFormHandler Webservice code ="
									+ importResVO.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10109));
		} else if (importResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR) {
			errorMsg = this.getMessageHandler().getErrMsg(
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
					pRequest.getLocale().getLanguage(), null, null);
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Fatal error from setFormExceptionsForImportErrors of SimplifyRegFormHandler Webservice code ="
									+ importResVO.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10110));
		} else if (importResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN) {
			errorMsg = this
					.getMessageHandler()
					.getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
							pRequest.getLocale().getLanguage(), null, null);
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Either user token or site flag invalid from setFormExceptionsForImportErrors of SimplifyRegFormHandler Webservice code ="
									+ importResVO.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10111));
		} else if (importResVO.getServiceErrorVO().getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry input fields format error from handleImportRegistry() of "
									+ "SimplifyRegFormHandler | webservice error code="
									+ importResVO.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));
			errorMsg = this.getMessageHandler().getErrMsg(
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
					pRequest.getLocale().getLanguage(), null, null);

		} else {
			errorMsg = errorVal;
		}
		this.addFormException(new DropletException(errorMsg));
		this.setImportErrorMessage(errorMsg);

	}

	private void setRegistryVO(final Profile profile)
			throws BBBSystemException, BBBBusinessException {
		this.getRegistryVO().setRegistryId(this.getRegistryId());
		this.getRegistryVO().setPassword(this.getRegistryPassword());
		final String siteId = this.getSiteContext().getSite().getId();

		/**
		 * Set info for whom the registry needs to be linked either as
		 * Registrant or CoRegistrant
		 */
		this.getRegistryVO().getRegistrantVO()
				.setProfileId(profile.getRepositoryId());
		this.getRegistryVO()
				.getRegistrantVO()
				.setEmail(
						(String) profile
								.getPropertyValue(BBBCoreConstants.EMAIL));
		this.getRegistryVO()
				.getRegistrantVO()
				.setFirstName(
						(String) profile
								.getPropertyValue(BBBCoreConstants.FIRST_NAME));
		this.getRegistryVO()
				.getRegistrantVO()
				.setLastName(
						(String) profile
								.getPropertyValue(BBBCoreConstants.LAST_NAME));
		this.getRegistryVO()
				.getRegistrantVO()
				.setPrimaryPhone(
						(String) profile
								.getPropertyValue(BBBCoreConstants.PHONE_NUM));
		this.getRegistryVO()
				.getRegistrantVO()
				.setCellPhone(
						(String) profile
								.getPropertyValue(BBBCoreConstants.MOBILE_NUM));

		this.getRegistryVO().setSiteId(
				this.getBbbCatalogTools()
						.getAllValuesForKey(
								BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
								siteId).get(0));
		this.getRegistryVO().setUserToken(
				this.getBbbCatalogTools()
						.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
								BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
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
	public boolean preImportRegistry(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse)
			throws BBBSystemException, BBBBusinessException {

		this.logDebug("SimplifyRegFormHandler.preImportRegistry() method starts");
		if (BBBUtility.isEmpty(this.getRegistryPassword())) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry empty password from preImportRegistry of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1051));

			this.addFormException(new DropletException(this
					.getMessageHandler().getErrMsg(
							BBBGiftRegistryConstants.EMPTY_PASSWORD_EXCEPTION,
							pRequest.getLocale().getLanguage(), null, null),
					BBBGiftRegistryConstants.EMPTY_PASSWORD_EXCEPTION));
			this.setImportErrorMessage(BBBGiftRegistryConstants.EMPTY_PASSWORD_EXCEPTION);
			return false;
		}
		if (!BBBUtility.isValidateRegistryId(this.getRegistryId())) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry invalid registry ID from preImportRegistry of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1051));

			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.INVALID_REGISTRY_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null)));
			this.setImportErrorMessage(BBBGiftRegistryConstants.INVALID_REGISTRY_EXCEPTION);
			return false;
		}
		if (BBBUtility.isEmpty(this.getImportEventDate())) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry invalid import event date from preImportRegistry of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1051));

			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_INVALID_EVENT_DATE_IMPORT_REGISTRY,
									pRequest.getLocale().getLanguage(), null,
									null)));
			this.setImportErrorMessage(BBBGiftRegistryConstants.ERR_INVALID_EVENT_DATE_IMPORT_REGISTRY);
			return false;
		}
		if (BBBUtility.isEmpty(this.getImportEventType())) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry invalid import event type from preImportRegistry of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1051));

			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.ERR_INVALID_EVENT_TYPE_IMPORT_REGISTRY,
									pRequest.getLocale().getLanguage(), null,
									null)));
			this.setImportErrorMessage(BBBGiftRegistryConstants.ERR_INVALID_EVENT_TYPE_IMPORT_REGISTRY);
			return false;
		}
		this.logDebug("SimplifyRegFormHandler.preImportRegistry() method ends");

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
	public boolean handleforgetRegistryPassword(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException { 
		this.logDebug("SimplifyRegFormHandler.handleforgetRegistryPassword() method starts");

		boolean isNoError = true;
		String errorVal = this.getMessageHandler().getErrMsg(
				BBBGiftRegistryConstants.FORGET_PASSWORD_EXCEPTION,
				pRequest.getLocale().getLanguage(), null, null);
		if (StringUtils.isEmpty(errorVal)) {
			errorVal = BBBGiftRegistryConstants.NO_DATA;
		}
		if ((this.getCheckForValidSession() && !this.isValidSession(pRequest))) {
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Session expired exception from handleforgetRegistryPassword of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1059));
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.SESSION_EXCEPTION,
					BBBGiftRegistryConstants.SESSION_EXCEPTION));
			isNoError = true;
		}
			try {

				if (this.preForgetRegistryPassword(pRequest, pResponse)) {
					
					this.populateforgetRegPassReqVO();
					
					final RegistryResVO forgetRegistryPass = this
							.getGiftRegistryManager().forgetRegPasswordService(
									this.getForgetRegPassRequestVO());
					if (!forgetRegistryPass.getServiceErrorVO().isErrorExists()) {
						isNoError = true;
					} else {
						errorVal = populateForgetRegPassError(pRequest,
								errorVal, forgetRegistryPass);
						
						this.addFormException(new DropletException(errorVal,
								"err_forgot_registry_password"));
						this.setImportErrorMessage(errorVal);
						isNoError = false;
					}
				}
				this.logDebug("SimplifyRegFormHandler.handleforgetRegistryPassword() method ends");

			} catch (final BBBSystemException bbbsysexp) {
				this.logError(
						LogMessageFormatter
								.formatMessage(
										pRequest,
										"FORGET_PASSWORD_EXCEPTION BBBSystemException from handleforgetRegistryPassword of SimplifyRegFormHandler",
										BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1010),
						bbbsysexp);
				this.addFormException(new DropletException(errorVal, errorVal));
				this.setImportErrorMessage(errorVal);
				isNoError = false;
				return isNoError;
			}

			catch (final BBBBusinessException bbbbexp) {
				this.logError(
						LogMessageFormatter
								.formatMessage(
										pRequest,
										"FORGET_PASSWORD_EXCEPTION BBBBusinessException from handleforgetRegistryPassword of SimplifyRegFormHandler",
										BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1010),
						bbbbexp);

				if (bbbbexp.getMessage().equals(
						BBBCoreConstants.PASSWORD_NOT_MATCH)) {

					this.logError(
							LogMessageFormatter
									.formatMessage(
											pRequest,
											"GiftRegistry Incorrect password from handleforgetRegistryPassword of SimplifyRegFormHandler",
											BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1050),
							bbbbexp);

					this.addFormException(new DropletException(
							this.getMessageHandler()
									.getErrMsg(
											BBBGiftRegistryConstants.PASSWORD_EXCEPTION,
											pRequest.getLocale().getLanguage(),
											null, null),
							BBBGiftRegistryConstants.PASSWORD_EXCEPTION));
					this.setImportErrorMessage(this
							.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.PASSWORD_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null));
					isNoError = false;
				} else {
					this.addFormException(new DropletException(errorVal,
							errorVal));
					isNoError = false;
					this.setImportErrorMessage(errorVal);
					return isNoError;
				}

			}

		return isNoError;
	}
	/**
	 * To set error for forget registry password
	 * 
	 * @param pRequest the request
	 * @param errorVal the errorVal
	 * @param forgetRegistryPass the forgetRegistryPass
	 * @return errorVal
	 */
	private String populateForgetRegPassError(
			final DynamoHttpServletRequest pRequest, String errorVal,
			final RegistryResVO forgetRegistryPass) {
		if (!BBBUtility.isEmpty(forgetRegistryPass
				.getServiceErrorVO().getErrorDisplayMessage())
				&& (forgetRegistryPass.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_FATAL_ERROR))// Technical
		// Error
		{
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Fatal error from handleAddItemToGiftRegistry() of SimplifyRegFormHandler | webservice error code="
									+ forgetRegistryPass
											.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10107));
			errorVal = this
					.getMessageHandler()
					.getErrMsg(
							BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR,
							pRequest.getLocale().getLanguage(),
							null, null);
			this.addFormException(new DropletException(
					errorVal,
					BBBGiftRegistryConstants.ERROR_GIFT_REG_FATAL_ERROR));
		}
		if (!BBBUtility.isEmpty(forgetRegistryPass
				.getServiceErrorVO().getErrorDisplayMessage())
				&& (forgetRegistryPass.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_SITE_FLAG_USER_TOKEN))// Technical
		// Error
		{
			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"Either user token or site flag invalid from handleforgetRegistryPassword of SimplifyRegFormHandler Webservice code ="
									+ forgetRegistryPass
											.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10112));
			errorVal = this
					.getMessageHandler()
					.getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR,
							pRequest.getLocale().getLanguage(),
							null, null);
			this.addFormException(new DropletException(
					errorVal,
					BBBGiftRegistryConstants.ERR_GIFT_REG_DITEFLAG_USERTOKEN_ERROR));

		}
		if (!BBBUtility.isEmpty(forgetRegistryPass
				.getServiceErrorVO().getErrorDisplayMessage())
				&& (forgetRegistryPass.getServiceErrorVO()
						.getErrorId() == BBBWebServiceConstants.ERR_CODE_GIFT_REGISTRY_INPUT_FIELDS_FORMAT))// Technical
		// Error
		{

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry input fields format error from handleAddItemToGiftRegistry() of "
									+ "SimplifyRegFormHandler | webservice error code="
									+ forgetRegistryPass
											.getServiceErrorVO()
											.getErrorId(),
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1049));

			errorVal = this
					.getMessageHandler()
					.getErrMsg(
							BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT,
							pRequest.getLocale().getLanguage(),
							null, null);
			this.addFormException(new DropletException(
					errorVal,
					BBBGiftRegistryConstants.ERR_GIFT_REG_INVALID_INPUT_FORMAT));
		}
		return errorVal;
	}
	/**
	 * To set the forget registry password requestVO
	 * 
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private void populateforgetRegPassReqVO() throws BBBSystemException,
			BBBBusinessException {
		this.getForgetRegPassRequestVO().setForgetPassRegistryId(
				this.getForgetPasswordRegistryId());

		final String siteId = this.getSiteContext().getSite()
				.getId();
		this.getForgetRegPassRequestVO().setSiteFlag(siteId);

		this.getForgetRegPassRequestVO()
				.setSiteFlag(
						this.getBbbCatalogTools()
								.getAllValuesForKey(
										BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
										siteId).get(0));
		this.getForgetRegPassRequestVO()
				.setUserToken(
						this.getBbbCatalogTools()
								.getAllValuesForKey(
										BBBWebServiceConstants.TXT_WSDLKEY,
										BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
								.get(0));
		this.getForgetRegPassRequestVO().setServiceName(
				this.getForgetRegistryPasswordServiceName());
		this.logDebug("siteId: "
				+ this.getForgetRegPassRequestVO().getSiteFlag());
		this.logDebug("userToken: "
				+ this.getForgetRegPassRequestVO().getUserToken());
		this.logDebug("ServiceName: "
				+ this.getForgetRegistryPasswordServiceName());
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
	public boolean preForgetRegistryPassword(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse)
			throws BBBSystemException, BBBBusinessException {

		this.logDebug("SimplifyRegFormHandler.preForgetRegistryPassword() method starts");
		if (!BBBUtility
				.isValidateRegistryId(this.getForgetPasswordRegistryId())) {

			this.logError(LogMessageFormatter
					.formatMessage(
							pRequest,
							"GiftRegistry Invalid registryId from preForgetRegistryPassword of SimplifyRegFormHandler",
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1048));

			this.addFormException(new DropletException(
					this.getMessageHandler()
							.getErrMsg(
									BBBGiftRegistryConstants.INVALID_REGISTRY_EXCEPTION,
									pRequest.getLocale().getLanguage(), null,
									null),
					BBBGiftRegistryConstants.INVALID_REGISTRY_EXCEPTION));
			return false;
		}
		this.logDebug("SimplifyRegFormHandler.preForgetRegistryPassword() method ends");

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
	public boolean handleSwitchCategoryPrice(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException {

		this.logDebug("SimplifyRegFormHandler.handleSwitchCategoryPrice() method start");

		return this.checkFormRedirect(this.getSuccessURL(), this.getErrorURL(),
				pRequest, pResponse);
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
			priceTotal = Integer.parseInt(pQuantity)
					* Double.parseDouble(price);

		}
		this.logDebug("GiftRegistryTools.totalPrice() method ends");

		return "" + priceTotal;

	}

	/**
	 * This handle method is used to move all wish list items into an existing
	 * registry
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked" })
	public boolean handleMoveAllWishListItemsToRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		this.logDebug("SimplifyRegFormHandler ::handleMoveAllWishListItemsToRegistry method starts");
		this.logDebug("SimplifyRegFormHandler ::handleMoveAllWishListItemsToRegistry getting wish list for profile with email "
				+ this.getProfile().getPropertyValue("email"));
		final HashMap<String, String> error = new HashMap<String, String>();
		final WishListVO wishListItems = this.getWishlistManager()
				.getWishListItems();
		if (!StringUtils.isEmpty(this.getRegistryId())) {
			if (wishListItems != null) {
				final List<GiftListVO> itemArray = wishListItems
						.getWishListItems();
				if (itemArray != null) {
					for (final GiftListVO item : itemArray) {
						if (item != null) {
							String giftListItemId = null;
							giftListItemId = item.getWishListItemId();
							this.logDebug("SimplifyRegFormHandler ::handleMoveAllWishListItemsToRegistry move wishlist item Id: "
									+ giftListItemId + "to registry");
							this.setWishListItemId(giftListItemId);
							this.handleMoveWishListItemToRegistry(pRequest,
									pResponse);
							if (this.getFormError()) {
								error.put(giftListItemId, this
										.getFormExceptions().get(0).toString());
								this.getFormExceptions().clear();
							}
						}
					}
					this.setMoveAllWishListItemsFailureResult(error);
				}
			}
		} else {
			this.logDebug("Input registry id is null or empty");
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID));
		}
		this.logDebug("SimplifyRegFormHandler ::handleMoveAllWishListItemsToRegistry method ends");
		return false;
	}

	/**
	 * This handle method is used to move a particular wish list item into an
	 * existing registry
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleMoveWishListItemToRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		final String giftListId = ((RepositoryItem) this.getProfile()
				.getPropertyValue(WISH_LIST)).getRepositoryId();
		this.logDebug("handleMoveWishListItemToRegistry: Wish List id associated with the profile "
				+ giftListId
				+ " registry Id to move wish list item to "
				+ this.getRegistryId());
		if (!this.preMoveWishListItemToRegistry()) {
			try {
				final RepositoryItem wishListItem = this.getGiftListManager()
						.getGiftitem(this.getWishListItemId());
				if (wishListItem != null) {
					final AddItemsBean addItemsBean = this
							.getGiftRegistryManager().getGiftRegistryTools()
							.populateRegistryItemWIthWishListItem(wishListItem);
					addItemsBean.setRegistryId(this.getRegistryId());
					final List<AddItemsBean> additemList = new ArrayList<AddItemsBean>();
					additemList.add(addItemsBean);
					final GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
					giftRegistryViewBean.setAdditem(additemList);
					final String siteId = this.getSiteContext().getSite()
							.getId();

					giftRegistryViewBean.setRegistryId(this.getRegistryId());

					giftRegistryViewBean
							.setSiteFlag(this
									.getBbbCatalogTools()
									.getAllValuesForKey(
											BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
											siteId).get(0));
					giftRegistryViewBean.setUserToken(this
							.getBbbCatalogTools()
							.getAllValuesForKey(
									BBBWebServiceConstants.TXT_WSDLKEY,
									BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN)
							.get(0));
					giftRegistryViewBean.setServiceName(this
							.getAddItemsToRegServiceName());

					this.logDebug("siteFlag: "
							+ giftRegistryViewBean.getSiteFlag());
					this.logDebug("userToken: "
							+ giftRegistryViewBean.getUserToken());
					this.logDebug("ServiceName: "
							+ giftRegistryViewBean.getServiceName());

					giftRegistryViewBean.setRegistrySize(giftRegistryViewBean
							.getAdditem().size());
					this.getSessionBean().setGiftRegistryViewBean(
							giftRegistryViewBean);
					this.logDebug(" calling addIem to add item to registry ");
					final boolean errorToAdd = this.addIem(pRequest, pResponse,
							giftRegistryViewBean, siteId);

					if (!errorToAdd) {
						final String[] listItemIdToRemove = { this
								.getWishListItemId() };
						this.logDebug("wish list item to remove after moving to registry   "
								+ this.getWishListItemId());
						this.removeItemsFromGiftlist(pRequest, pResponse,
								giftListId, listItemIdToRemove);
					} else {
						this.logDebug("error adding item to registry hence not removing item from wish list");
						this.addFormException(new DropletException(
								BBBGiftRegistryConstants.ERR_MOVE_WISHLIST_ITEM_TO_REGISTRY_MSG,
								BBBGiftRegistryConstants.ERR_MOVE_WISHLIST_ITEM_TO_REGISTRY));
					}
				} else {
					this.logDebug("Wish list item is invalid or not available in the repository");
					this.addFormException(new DropletException(
							BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID_MSG,
							BBBGiftRegistryConstants.ERR_WISHLIST_ITEM_INVALID));
				}

			} catch (final CommerceException e) {

				this.logError(
						LogMessageFormatter
								.formatMessage(
										pRequest,
										"BBBBusinessException from handleMoveWishListItemToRegistry of SimplifyRegFormHandler",
										BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),
						e);

				this.addFormException(new DropletException(
						BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM_MSG,
						BBBGiftRegistryConstants.ERR_FETCH_WISHLIST_ITEM));
			} catch (final BBBBusinessException e) {

				this.logError(
						LogMessageFormatter
								.formatMessage(
										pRequest,
										"BBBBusinessException from handleMoveWishListItemToRegistry of SimplifyRegFormHandler",
										BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),
						e);

				this.addFormException(new DropletException(
						BBBGiftRegistryConstants.ERR_CONFIG_VALUE_NOT_FOUND_MSG,
						BBBGiftRegistryConstants.ERR_CONFIG_VALUE_NOT_FOUND));
			} catch (final BBBSystemException e) {

				this.logError(
						LogMessageFormatter
								.formatMessage(
										pRequest,
										"BBBBusinessException from handleMoveWishListItemToRegistry of SimplifyRegFormHandler",
										BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),
						e);

				this.addFormException(new DropletException(
						BBBGiftRegistryConstants.ERR_SYSTEM_CONFIG_VALUE_NOT_FOUND_MSG,
						BBBGiftRegistryConstants.ERR_SYSTEM_CONFIG_VALUE_NOT_FOUND));
			}
		}
		return false;
	}

	private boolean preMoveWishListItemToRegistry() {
		boolean isError = false;
		if (StringUtils.isEmpty(this.getRegistryId())) {
			this.logDebug("Input registry id is null or empty");
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_REGISTRY_ID));
			isError = true;
		}
		if (StringUtils.isEmpty(this.getWishListItemId())) {
			this.logDebug("Input wishlist id is null or empty");
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_WISHLIST_ID_MSG,
					BBBGiftRegistryConstants.ERR_NULL_OR_EMPTY_WISHLIST_ID));
			isError = true;
		}
		return isError;
	}

	/**
	 * This method is used to remove a wish list item from wishlist
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param giftListId
	 * @param listItemIdToRemove
	 * @throws ServletException
	 * @throws IOException
	 * @throws CommerceException
	 */
	protected void removeItemsFromGiftlist(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, final String giftListId,
			final String[] listItemIdToRemove) throws ServletException,
			IOException, CommerceException {

		final String pGiftlistId = giftListId;
		if (listItemIdToRemove == null) {
			return;
		}

		try {
			for (final String id : listItemIdToRemove) {
				this.logDebug("Wish list item to remove  " + id
						+ "  from wish list id  " + pGiftlistId);
				this.getGiftListManager().removeItemFromGiftlist(pGiftlistId, id);
			}
		} catch (final RepositoryException ex) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"RepositoryException in SimplifyRegFormHandler while removeItemsFromGiftlist : Item not found",
									BBBCoreErrorConstants.ACCOUNT_ERROR_1251),
					ex);

		} catch (final InvalidGiftTypeException ige) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"InvalidGiftTypeException in SimplifyRegFormHandler while removeItemsFromGiftlist : Item not found",
									BBBCoreErrorConstants.ACCOUNT_ERROR_1251),
					ige);

		}
	}

	/**
	 * This method is used to add a sku item into registry
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @param giftRegistryViewBean
	 * @param siteId
	 * @return
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean addIem(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse,
			final GiftRegistryViewBean giftRegistryViewBean, final String siteId) {
		try {

			final ValidateAddItemsResVO addItemsResVO = this
					.getGiftRegistryManager().addItemToGiftRegistry(
							giftRegistryViewBean);

			if (!addItemsResVO.getServiceErrorVO().isErrorExists()) {
				this.logDebug(" no error exists while adding item to registry ");
				this.setErrorFlagAddItemToRegistry(false);

				// Update session data so that it will be in sync with the
				// registred items on registry flyout.
				final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
						.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
				final HashMap sessionMap = sessionBean.getValues();
				final RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap
						.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
				if ((registrySummaryVO != null)
						&& registrySummaryVO.getRegistryId().equalsIgnoreCase(
								giftRegistryViewBean.getRegistryId())) {
					int totalQuantity = 0;

					final List<AddItemsBean> additemList = giftRegistryViewBean
							.getAdditem();

					for (final AddItemsBean addItemsBean : additemList) {
						totalQuantity += Integer.parseInt(addItemsBean
								.getQuantity());
					}

					// update quantiry in session
					totalQuantity = totalQuantity
							+ registrySummaryVO.getGiftRegistered();
					registrySummaryVO.setGiftRegistered(totalQuantity);

				} else if ((registrySummaryVO != null)
						&& !registrySummaryVO.getRegistryId().equalsIgnoreCase(
								giftRegistryViewBean.getRegistryId())) {
					// update the registrysummaryvo in the BBBsessionBean
					final RegistrySummaryVO regSummaryVO = this
							.getGiftRegistryManager().getRegistryInfo(
									giftRegistryViewBean.getRegistryId(),
									siteId);
					sessionBean.getValues().put(
							BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY,
							regSummaryVO);
				}

			} else {
				this.errorAdditemCertona(pRequest, addItemsResVO);
			}

		} catch (final BBBBusinessException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBBusinessException from Add Item To GiftRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10104),
					e);
			this.setErrorFlagAddItemToRegistry(true);
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.ERR_ADDING_ITEM_TO_REGISTRY_MSG,
					BBBGiftRegistryConstants.ERR_ADDING_ITEM_TO_REGISTRY));
		} catch (final BBBSystemException e) {
			this.logError(
					LogMessageFormatter
							.formatMessage(
									pRequest,
									"BBBSystemException from Add Item To GiftRegistry of SimplifyRegFormHandler",
									BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10104),
					e);
			this.setErrorFlagAddItemToRegistry(true);
			this.addFormException(new DropletException(
					BBBGiftRegistryConstants.ERR_ADDING_ITEM_TO_REGISTRY_MSG,
					BBBGiftRegistryConstants.ERR_ADDING_ITEM_TO_REGISTRY));
		}
		return this.isErrorFlagAddItemToRegistry();

	}

	public int getUpdateBatchSize() {

		List<String> listKeys = null;
		String batchSizeString = null;
		int batchSize = this.getUpdateBatchSize();
		try {
			listKeys = this.getBbbCatalogTools().getAllValuesForKey(
					"ContentCatalogKeys", "registry_update_batchsize");
			if ((listKeys != null) && (listKeys.size() > 0)) {
				batchSizeString = listKeys.get(0);

				if (batchSizeString != null) {
					batchSize = Integer.parseInt(batchSizeString);
				}
			}
		} catch (final BBBSystemException e) {
			this.logError("BBBSystemException - registry_update_batchsize key not found for site"
					+ e);
		} catch (final BBBBusinessException e) {
			this.logError("BBBSystemException - registry_update_batchsize key not found for site"
					+ e);
		} catch (final NumberFormatException nfe) {
			this.logError("NumberFormatException - registry_update_batchsize value format exception"
					+ nfe);
		}
		return batchSize;
	}

	/**
	 * Return the host path For Mobile get from config key
	 * 
	 * @param pRequest
	 * @return
	 */
	private String getHost(final DynamoHttpServletRequest pRequest) {

		String hostPath = "";
		final String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
		if (BBBUtility.isNotEmpty(channel)
				&& (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel
						.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))) {
			try {

				final List<String> configValue = this.getBbbCatalogTools()
						.getAllValuesForKey(
								BBBCoreConstants.MOBILEWEB_CONFIG_TYPE,
								BBBCoreConstants.REQUESTDOMAIN_CONFIGURE);
				if ((configValue != null) && (configValue.size() > 0)) {
					hostPath = configValue.get(0);
				}
			} catch (final BBBSystemException e) {
				this.logError("SimplifyRegFormHandler.getHost :: System Exception occured while fetching config value for config key "
						+ BBBCoreConstants.REQUESTDOMAIN_CONFIGURE
						+ "config type "
						+ BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
			} catch (final BBBBusinessException e) {
				this.logError("SimplifyRegFormHandler.getHost :: Business Exception occured while fetching config value for config key "
						+ BBBCoreConstants.REQUESTDOMAIN_CONFIGURE
						+ "config type "
						+ BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
			}
		} else {
			hostPath = pRequest.getServerName();
		}
		return hostPath;
	}
	
	public boolean handleVerifyUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException{
		 
		String email = getEmail();
		BBBProfileTools profileTools = (BBBProfileTools) getProfileTools();
		 String loginEmail = (String) this.getValue().get(this.getPropertyManager().getEmailAddressPropertyName());
		  final Dictionary<String, String> value = this.getValue();
		  value.put(this.getPropertyManager().getLoginPropertyName(), getEmail().toLowerCase());		
	      value.put(this.getPropertyManager().getEmailAddressPropertyName(), getEmail().toLowerCase());
	         
	        
		if(StringUtils.isBlank(email)){
			
			 this.addFormException(new DropletException("User Already exists",
                     BBBCoreConstants.ERR_USER_ALREADY_ASSOCIATED_TO_SITE));
				getSessionBean().setRegistredUser(true);
		}
		else{
			 String profileStatus = null;
			try {
				profileStatus = profileTools.checkForRegistration(email);
			} catch (BBBBusinessException e) {
				logError(e.getMessage(),e);
			}
			 if(!StringUtils.isBlank(profileStatus)){
				 if(profileStatus.equalsIgnoreCase("profile_already_exist")){
					 getSessionBean().setRegistryProfileStatus("regUserAlreadyExists");
					 getSessionBean().setRegistredUser(true);
				 }
				 else if(profileStatus.equalsIgnoreCase("profile_available_for_extenstion")){
					 this.setMigrationFlag(true);
					 setUserMigratedLoginProp(true);
					getSessionBean().setRegistredUser(true);
					getSessionBean().setRegistryProfileStatus("refProfileExtenssion");
				 }
				 
				 else if(profileStatus.equalsIgnoreCase("Profile not found")){
					 getSessionBean().setRegistryProfileStatus("regNewUser");
					 getSessionBean().setRegistredUser(false);
				 }
			 }
			
					 
             
		}
		
		getFormError();
		 if (getErrorMap().isEmpty()) {
	        	getSessionBean().setUserEmailId(getLoginEmail());
	        }
        
		 this.checkFormRedirect(this.getVerifyUserErrorURLPage(), this.getVerifyUserErrorURLPage(), pRequest,
					pResponse);
		   
		 return true;
	}

	private void checkForMigratedAccount(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws UnsupportedEncodingException {

        this.logDebug("SimplifyRegFormHandler.checkForMigratedAccount() method started");

        
    	setErrorMap(new HashMap<String, String>());
        final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
        String loginEmail = getEmail();
        if ((loginEmail == null) || loginEmail.trim().isEmpty()) {
            final String emailEmptyError = this.getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_EMAIL_EMPTY,
                    pRequest.getLocale().getLanguage(), null, null);
            this.addFormException(new DropletException(emailEmptyError, BBBCoreConstants.ERR_PROFILE_EMAIL_EMPTY));
            getErrorMap().put(BBBCoreConstants.REGISTER_ERROR, emailEmptyError);
        } else {

            boolean isDuplicateInput = false;
            loginEmail = loginEmail.toLowerCase();

            if (!BBBUtility.isValidEmail(loginEmail)) {
                final String emailInvalidError = this.getMessageHandler().getErrMsg(
                        BBBCoreConstants.ERR_PROFILE_EMAIL_INVALID, pRequest.getLocale().getLanguage(), null, null);
                this.addFormException(new DropletException(emailInvalidError,
                        BBBCoreConstants.ERR_PROFILE_EMAIL_INVALID));
                this.getErrorMap().put(BBBCoreConstants.REGISTER_ERROR, emailInvalidError);
            } else {
            	String status="";
                final RepositoryItem repItem = getProfileTools().getItemFromEmail(loginEmail); 
                if(null!=repItem){
                	status=(String)repItem.getPropertyValue(this.getPropertyManager().getStatusPropertyName() );	
                }

                if(StringUtils.isEmpty(status)){
                	logDebug("status: new Profile Creation Flow...");
                }
                else{
                	logDebug("Existing Profile status\t:"+status);
                }
                 
                if (!BBBCoreConstants.SHALLOW_PROFILE_STATUS_VALUE.equalsIgnoreCase(status) && repItem != null) {
                    if (getProfileManager().isUserPresentToOtherGroup(repItem, this.getSiteContext().getSite().getId())) {
                        final String loginEmptyError = this.getMessageHandler().getErrMsg(
                                BBBCoreConstants.ERR_USER_ALREADY_ASSOCIATED_TO_SITE,
                                pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null);
                        this.addFormException(new DropletException(loginEmptyError,
                                BBBCoreConstants.ERR_USER_ALREADY_ASSOCIATED_TO_SITE));
                       this.getErrorMap().put(BBBCoreConstants.REGISTER_ERROR, loginEmptyError);
                    } else {

                        final Object tempMigrationFlag = repItem.getPropertyValue(this.getPropertyManager()
                                .getMigratedAccount());
                        final Object tempUserMigratedLoginProp = repItem.getPropertyValue(this.getPropertyManager()
                                .getLoggedIn());
                        if ((tempMigrationFlag != null) && (tempUserMigratedLoginProp != null)) {
                            this.setMigrationFlag(((Boolean) repItem.getPropertyValue(this.getPropertyManager()
                                    .getMigratedAccount())).booleanValue());
                            setUserMigratedLoginProp((Boolean) repItem.getPropertyValue(this.getPropertyManager()
                                    .getLoggedIn()));
                        } else {
                            this.setMigrationFlag(false);
                            setUserMigratedLoginProp(false);
                        }
                        if (this.isMigrationFlag() && !isUserMigratedLoginProp()) {
                            this.setLegacyUser(BBBCoreConstants.YES);
                            pRequest.getSession().setAttribute(BBBCoreConstants.EMAIL_ADDR, loginEmail);
                        }
                        isDuplicateInput = ((BBBProfileTools) this.getProfileTools()).isDuplicateEmailAddress(loginEmail, this.getSiteContext().getSite().getId());
                        
                        if (isDuplicateInput) {
                                     if(!BBBCoreConstants.SHALLOW_PROFILE_STATUS_VALUE.equalsIgnoreCase(status)){                         	
                            final String loginEmptyError = this.getMessageHandler().getErrMsg(
                                    BBBCoreConstants.ERR_USER_ALREADY_ASSOCIATED_TO_SITE,
                                    pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null);
                            this.addFormException(new DropletException(loginEmptyError,
                                    BBBCoreConstants.ERR_USER_ALREADY_ASSOCIATED_TO_SITE));
                            this.getErrorMap().put(BBBCoreConstants.REGISTER_ERROR, loginEmptyError);
                        }
                          	
                      
                    }
                    }

                    if (this.getErrorMap().isEmpty()) {
                        @SuppressWarnings ("unchecked")
                        final Map<String, String> siteItems = (Map<String, String>) repItem
                        .getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM);
                        if (siteItems != null) {
                            final Set<String> keys = siteItems.keySet();
                            for (final String key : keys) {
                                pRequest.getSession().setAttribute(BBBCoreConstants.SITE, key);
                                if (siteItems.size() == 1) {
                                    if (!key.equalsIgnoreCase(this.getSiteContext().getSite().getId())
                                            && !this.getSiteContext().getSite().getId()
                                            .equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
                                        this.setShowMigratedUserPopupURL(true);
                                    }
                                }
                            }
                            this.setLoginEmail(loginEmail);
                        }
                    }
                    
                    if (getLoginEmail() != null) {
                        setPreRegisterSuccessURL(getExtenstionSuccessURL()+ java.net.URLEncoder.encode(getLoginEmail(), BBBCoreConstants.UTF_8).replace(
                                        BBBCoreConstants.PLUS, BBBCoreConstants.PERCENT_TWENTY));
                    } else if (getErrorMap().isEmpty()) {
                    	getSessionBean().setUserEmailId(loginEmail);
                    }

                }
            }
        }

        this.logDebug("SimplifyRegFormHandler.checkForMigratedAccount() method ends");

    }
	
	 
	 
	@Override
	public void postLoginUser(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		logDebug("post login user");
		//super.postLoginUser(pRequest, pResponse);
	}

	
	private boolean isRegistryOwnedByProfile(final String registryId)
			throws BBBBusinessException, BBBSystemException {
		final String profileId = this.getProfile().getRepositoryId();
		final String siteId = this.getCurrentSiteId();
		return this.getGiftRegistryManager().isRegistryOwnedByProfile(
				profileId, registryId, siteId);
	}
	
  /**
	 * This method is used to copy a guest registry with items and quantity into a
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
	@SuppressWarnings("rawtypes")
	public boolean handleCopyRegistry(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {		          
		
		final GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
		RegCopyResVO resCopyVo =new RegCopyResVO();
		try {
			giftRegistryViewBean.setSiteFlag(getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,getSiteContext().getSite().getId()).get(0));
			giftRegistryViewBean.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
			giftRegistryViewBean.setServiceName(getCopyRegistryServiceName());
			giftRegistryViewBean.setSourceRegistry(getSrcRegistryId());
			if(getSessionBean()!=null){
			final HashMap sessionMap = getSessionBean().getValues();
			if(sessionMap!=null){
			RegistrySummaryVO pRegSummaryVO = (RegistrySummaryVO) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
			if(pRegSummaryVO!=null){
			RegistryTypes regType = pRegSummaryVO.getRegistryType();
			if(regType!=null){
			 giftRegistryViewBean.setTargetRegistry(pRegSummaryVO.getRegistryId() );
			 giftRegistryViewBean.setRegistryName(regType.getRegistryTypeDesc());
			 resCopyVo = getGiftRegistryManager().copyRegistry(giftRegistryViewBean);
			 giftRegistryViewBean.setCopyRegErr(resCopyVo.hasError());
			 giftRegistryViewBean.setTotQtySrcReg(resCopyVo.gettotalNumOfItemsCopied());
			 int totQty= giftRegistryViewBean.getTotQuantity();
			 int totSrcRegQty =Integer.valueOf(resCopyVo.gettotalNumOfItemsCopied());
			// giftRegistryViewBean.setTotQuantity(totQty+totSrcRegQty);
			 getSessionBean().setGiftRegistryViewBean(giftRegistryViewBean);
			   }
			  }
			}
		}
		} catch (BBBSystemException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logError(e.getMessage(),e);
		} catch (BBBBusinessException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logError(e.getMessage(),e);
		}		
		   
		return checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest,
				pResponse);
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
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public boolean handlePrintInvitationCards(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

	  String imageHost = this.getGiftRegistryManager().getGiftRegistryConfigurationByKey(BBBGiftRegistryConstants.PRINT_CARDS_AT_HOME_IMAGES_HOST);
	  if(! StringUtils.isEmpty(imageHost)
			&& !StringUtils.isEmpty(getHtmlMessage())) {
		  setHtmlMessage(getHtmlMessage().replaceAll("/_assets", imageHost+"/_assets"));
	  }

		getGiftRegistryManager().generatePDFDocument(pRequest, pResponse, getHtmlMessage(), isDownloadFlag());
	    return checkFormRedirect(this.getSuccessURL(), this.getErrorURL(), pRequest, pResponse);
	}
	
	/**
	 * 
	 * Used to block/unblock the Recommender for Recommending to a registry.
	 * The required parameters are registryId, recommenderProfileId and the requestedFlag.
	 * The requestedFlag is either 'block'/'unblock'.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean handleToggleBlockRecommender(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse) 
			throws IOException,	ServletException {
		this.logDebug("SimplifyRegFormHandler.handleToggleBlockRecommender() method starts");
		boolean result = false;
		setErrorMap(new HashMap<String, String>());
		Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		if(!profile.isTransient()){
			try {
				 result = this.getGiftRegistryRecommendationManager().persistToggleRecommenderStatus(getRegistryId(), getRecommenderProfileId(), getRequestedFlag());
				 if(!result){
					 String errorMessage = this.getMessageHandler().getErrMsg(BBBGiftRegistryConstants.TOGGLE_PERSIST_ERROR_KEY, pRequest.getLocale().getLanguage(), null, null);
					 getErrorMap().put(BBBGiftRegistryConstants.TOGGLE_PERSIST_ERROR,errorMessage);
					 this.addFormException(new DropletException(errorMessage));
					 logError(errorMessage);
				 }
			} catch (BBBSystemException e) {
				String errorMessage = this.getMessageHandler().getErrMsg(BBBGiftRegistryConstants.ERR_REGSEARCH_SYS_EXCEPTION, pRequest.getLocale().getLanguage(), null, null);
				getErrorMap().put(BBBGiftRegistryConstants.TOGGLE_PERSIST_ERROR,errorMessage);
				this.addFormException(new DropletException(errorMessage));
				logError("Unable to get the registry recommendations.", e);
			}
			
			this.logDebug("SimplifyRegFormHandler.handleToggleBlockRecommender() method ends");
		}
		else{
			String errorMessage = this.getMessageHandler().getErrMsg(BBBGiftRegistryConstants.ILLEGAL_TOGGLE_REQUEST_KEY, pRequest.getLocale().getLanguage(), null, null);
			getErrorMap().put(BBBGiftRegistryConstants.TOGGLE_PERSIST_ERROR,errorMessage);
			this.addFormException(new DropletException(errorMessage));
			logError("Illegal Request is received. Non-Logged in user has requested to " + getRequestedFlag() +
					" the recommender with profileId:-" + getRecommenderProfileId() + " for registryId:-" + getRegistryId());
		}
		return this.checkFormRedirect(getToggleSuccessURL(), getToggleFailureURL(), pRequest, pResponse);
	}
	
	
	public boolean handleUndoRedo(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse) 
			throws IOException,	ServletException {
		boolean result = false;
		Profile profile = (Profile)pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		if(!profile.isTransient()){
			if(!(this.getUndoFrom().isEmpty()||this.getRegistryId().isEmpty())){
				String regsitryId = this.getRegistryId();
				boolean validations = true;
				if(!BBBUtility.isValidateRegistryId(regsitryId)){
					this.logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException from handleUndoRedo of SimplifyRegFormHandler"));
					validations = false;
				}
				if(!validations){
					GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean();
					try {
						this.isRegistryOwnedByProfile(regsitryId);
						this.preRemoveItemFromGiftRegistry(pRequest, pResponse);
						if(!this.getFormError()){
							this.setRegistryFlags(giftRegistryViewBean);
							this.populateGiftRegistryRemove(giftRegistryViewBean);
							result = this.getGiftRegistryRecommendationManager().performUndo(giftRegistryViewBean, getUndoFrom());
							if(!result){
								this.updateSessionAfterRemovingFromRegistry(pRequest, pResponse, giftRegistryViewBean);
							}else{
								this.addFormException(new DropletException(""));
							}
						}
					}catch (BBBSystemException e) {
						// TODO Auto-generated catch block
					//	e.printStackTrace();
						logError(e.getMessage());
					} catch (BBBBusinessException e) {
						// TODO Auto-generated catch block
					//	e.printStackTrace();
						logError(e.getMessage());
					}
				}
			}else{
				this.addFormException(new DropletException("Illegal Parameters."));
			}
		}else{
			logDebug("Non-logged in user.");
		}
		
		return this.checkFormRedirect(this.getUndoSuccessURL(), this.getUndoFailureURL(), pRequest, pResponse);
	}
	
	private String getRowIdAfterAddition(AddItemsBean addItemsBean) throws BBBSystemException{
		// TODO Auto-generated method stub
		if(addItemsBean!=null){
			try {
				return this.getGiftRegistryRecommendationManager().getRowIdAfterAddition(addItemsBean);
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//logError("RepositoryException",e);
				throw new BBBSystemException(e.getMessage());
			}
		}
		return null;
	}

	
	private void setRegistryFlags(GiftRegistryViewBean giftRegistryViewBean) throws BBBSystemException, BBBBusinessException{
		String siteId = this.getSiteContext().getSite().getId();
		giftRegistryViewBean.setSiteFlag(this.getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId).get(0));
		giftRegistryViewBean.setUserToken(this.getBbbCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
		giftRegistryViewBean.setServiceName(this.getAddItemsToRegServiceName());
		this.logDebug("siteFlag: " + giftRegistryViewBean.getSiteFlag());
		this.logDebug("userToken: " + giftRegistryViewBean.getUserToken());
		this.logDebug("ServiceName: " + this.getAddItemsToRegServiceName());
	}
	
	private void populateGiftRegistryRemove(GiftRegistryViewBean giftRegistryViewBean){
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
	
	private void updateSessionAfterRemovingFromRegistry(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse, GiftRegistryViewBean giftRegistryViewBean) throws BBBBusinessException, BBBSystemException{
		String siteId = this.getSiteContext().getSite().getId();
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		HashMap sessionMap = sessionBean.getValues();
		RegistrySummaryVO registrySummaryVO = (RegistrySummaryVO) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
		if ((registrySummaryVO != null) && registrySummaryVO.getRegistryId().equalsIgnoreCase(this.getRegistryId())) {
			int quantity = Integer.valueOf(this.getQuantity());
			// update quantiry in session
			registrySummaryVO.setGiftRegistered(registrySummaryVO.getGiftRegistered() - quantity);
		} else if ((registrySummaryVO != null) && !registrySummaryVO.getRegistryId().equalsIgnoreCase(this.getRegistryId())) {
			// update the registrysummaryvo in the BBBsessionBean
			final RegistrySummaryVO regSummaryVO = this.getGiftRegistryManager().getRegistryInfo(giftRegistryViewBean.getRegistryId(),siteId);
			sessionBean.getValues().put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY,regSummaryVO);
		}
	}

	
	@SuppressWarnings("unchecked")
	private boolean validateSite(String siteId, RepositoryItem profileItem){
		Map<String, RepositoryItem> sites = (Map<String, RepositoryItem>) profileItem.getPropertyValue(this.USER_SITE_ITEMS);
		if(sites!=null && sites.size()>0){
			for(String site:sites.keySet()){
				if((((String)((RepositoryItem) sites.get(site)).getPropertyValue(BBBCoreConstants.SITE_ID))).equalsIgnoreCase(siteId))
					return true;
			}
		}
		return false;
	}
	
}
