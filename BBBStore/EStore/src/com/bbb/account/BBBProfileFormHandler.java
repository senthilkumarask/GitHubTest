package com.bbb.account;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.lang.StringEscapeUtils;

import com.bbb.account.vo.ProfileSyncRequestVO;
import com.bbb.account.vo.UpdateWalletProfileRespVo;
import com.bbb.account.vo.reclaim.ForgetPasswordResponseVO;
import com.bbb.account.vo.reclaim.ReclaimAccountResponseVO;
import com.bbb.account.webservices.BBBProfileServices;
import com.bbb.account.webservices.BBBProfileServicesConstant;
import com.bbb.browse.BazaarVoiceUtil;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.commerce.catalog.vo.CreditCardTypeVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.checklist.vo.NonRegistryGuideVO;
import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.BBBPaymentGroupManager;
import com.bbb.constants.BBBAccountConstants;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.BBBURLConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.goldengate.DCPrefixIdGenerator;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.framework.webservices.vo.ValidationError;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.security.PBKDF2PasswordHasher;
import com.bbb.security.PasswordHashingService;
import com.bbb.social.facebook.FBConstants;
import com.bbb.social.facebook.FBProfileTools;
import com.bbb.social.facebook.vo.UserVO;
import com.bbb.utils.BBBUtility;
import com.bbb.wishlist.StoreGiftlistFormHandler;
import com.bbb.wishlist.manager.BBBGiftlistManager;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CreditCard;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.commerce.profile.CommerceProfileFormHandler;
import atg.commerce.profile.CommerceProfileTools;
import atg.core.util.StringUtils;
import atg.droplet.DropletConstants;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteGroupManager;
import atg.naming.NameContext;
import atg.nucleus.Nucleus;
import atg.payment.creditcard.CreditCardInfo;
import atg.payment.creditcard.ExtendableCreditCardTools;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;
import atg.userprofiling.CookieManager;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileFormHandler;
import atg.userprofiling.ProfileSwapEvent;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.PropertyManager;
import atg.userprofiling.email.TemplateEmailInfo;
import atg.userprofiling.email.TemplateEmailInfoImpl;
import net.sf.json.JSONObject;

public class BBBProfileFormHandler extends BBBProfileFormHandlerBean {
    	
	private BBBProfileTools mTools = (BBBProfileTools) this.getProfileTools();
	private Map<String, String> errorMap;
    String mGiftlistId ="";
    private boolean migrationFlag;
    private boolean resetPasswordFlag;
    private Map<String, Object> mEditValue;
    private LblTxtTemplateManager lblTxtTemplateManager;

    public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}
	
    /** Constructor of BBBProfileFormHandler. */
    public BBBProfileFormHandler () {
        this.errorMap = new HashMap<String, String>();
        this.mEditValue = new HashMap<String, Object>();
    }

     
    /** @return the stateList
     * @throws BBBBusinessException
     * @throws BBBSystemException */
    public final List<StateVO> getStateList() throws BBBSystemException, BBBBusinessException {
        return getBbbCatalogTools().getStates(getSiteContext().getSite().getId(), true, null);
    }

    /** @return the credit card types
     * @throws BBBBusinessException
     * @throws BBBSystemException */
    public final List<CreditCardTypeVO> getCreditCardTypes() throws BBBSystemException, BBBBusinessException {
        return getBbbCatalogTools().getCreditCardTypes(getSiteContext().getSite().getId());
    }

    /** This method is customized to validate the different form fields to match the business rules.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception */
    @Override
    protected final void preUpdateUser(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        super.preUpdateUser(pRequest, pResponse);
		this.setPersonalInfoValidated(false);

        this.mTools = (BBBProfileTools) this.getProfileTools();
        this.errorMap = new HashMap<String, String>();

        String errorMessage;
        final String emailProperty = getPropertyManager().getEmailAddressPropertyName();
        final String email = this.getStringValueProperty(emailProperty);

        if (!BBBUtility.isValidEmail(email)) {
			this.setPersonalInfoValidated(true);
            this.errorMap.put(FIELD_NAME, EMAIL_ADDRESS);
            errorMessage = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_INVALID,
                    pRequest.getLocale().getLanguage(), this.errorMap, null);
            this.addFormException(new DropletException(errorMessage, BBBCoreConstants.ERR_PROFILE_INVALID));
        }

        final String firstNameProperty = getPropertyManager().getFirstNamePropertyName();
        final String firstName = this.getStringValueProperty(firstNameProperty);

        if (!BBBUtility.isValidFirstName(firstName)) {
			this.setPersonalInfoValidated(true);
            this.errorMap.put(FIELD_NAME, FIRST_NAME2);
            errorMessage = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_SUBSCRIBTION_FIRSTNAME_INVALID,
                    pRequest.getLocale().getLanguage(), this.errorMap, null);
            this.addFormException(new DropletException(errorMessage,
                    BBBCoreConstants.ERR_SUBSCRIBTION_FIRSTNAME_INVALID));
        }

        final String lastNameProperty = getPropertyManager().getLastNamePropertyName();
        final String lastName = this.getStringValueProperty(lastNameProperty);

        if (!BBBUtility.isValidLastName(lastName)) {
			this.setPersonalInfoValidated(true);
            this.errorMap.put(FIELD_NAME, LAST_NAME2);
            errorMessage = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_LASTNAME_INVALID,
                    pRequest.getLocale().getLanguage(), this.errorMap, null);
            this.addFormException(new DropletException(errorMessage, BBBCoreConstants.ERR_PROFILE_LASTNAME_INVALID));
        }

        final String phoneNumberProperty = getPropertyManager().getPhoneNumberPropertyName();
        final String phoneNumber = this.getStringValueProperty(phoneNumberProperty);

        if (!BBBUtility.isEmpty(phoneNumber) && !BBBUtility.isValidPhoneNumber(phoneNumber)) {
			this.setPersonalInfoValidated(true);
            this.errorMap.put(FIELD_NAME, "Phone Number");
            errorMessage = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_CREATE_REG_PHONE_NUMBER_IS_INVALID,
                    pRequest.getLocale().getLanguage(), this.errorMap, null);
            this.addFormException(new DropletException(errorMessage,
                    BBBCoreConstants.ERR_CREATE_REG_PHONE_NUMBER_IS_INVALID));
        }

        final String mobileNumberProperty = getPropertyManager().getMobileNumberPropertyName();
        final String mobileNumber = this.getStringValueProperty(mobileNumberProperty);
        if (!BBBUtility.isEmpty(mobileNumber) && !BBBUtility.isValidPhoneNumber(mobileNumber)) {
			this.setPersonalInfoValidated(true);
            this.errorMap.put(FIELD_NAME, "Mobile Number");
            errorMessage = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_MOBILE_NUMBER_IS_INVALID,
                    pRequest.getLocale().getLanguage(), this.errorMap, null);
            this.addFormException(new DropletException(errorMessage, BBBCoreConstants.ERR_MOBILE_NUMBER_IS_INVALID));
        }

        final String currentEmail = (String) this.getProfileItem().getPropertyValue(emailProperty);
        if (BBBUtility.isEmpty(currentEmail)) {
            this.addFormException(new DropletException(getMessageHandler().getErrMsg(
                    BBBCoreConstants.ERR_UPDATEPROFILE_LOGINFIRST, pRequest.getLocale().getLanguage(), this.errorMap,
                    null), BBBCoreConstants.ERR_UPDATEPROFILE_LOGINFIRST));
        }

        final String pEmail = this.getStringValueProperty(emailProperty);
        if (!pEmail.equalsIgnoreCase(currentEmail) && this.mTools.isDuplicateEmailAddress(pEmail)) {
			this.setPersonalInfoValidated(true);
            this.addFormException(new DropletException(getMessageHandler().getErrMsg(
                    BBBCoreConstants.ERR_UPDATEPROFILE_EMAILALREADYEXIST, pRequest.getLocale().getLanguage(),
                    this.errorMap, null), BBBCoreConstants.ERR_UPDATEPROFILE_EMAILALREADYEXIST));
        }

        if (!this.getFormError()) {
            final String loginProperty = getPropertyManager().getLoginPropertyName();
            this.setValueProperty(loginProperty, pEmail);
        }
    }

    @Override
    protected final void postUpdateUser(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        super.postUpdateUser(pRequest, pResponse);

        this.mTools = (BBBProfileTools) this.getProfileTools();
        boolean isRegistryUpdated = false;

        if (!this.getFormError()) {
            final Map<String, String> propertyMap = new HashMap<String, String>();
            
            this.mTools.updateSiteItem(this.getProfile(), getSiteContext().getSite().getId(), propertyMap);
            
            if(!BBBCoreConstants.TRUE.equals(pRequest.getParameter(BBBCoreConstants.IS_FROM_GIFT_REGISTRY)))
            {
            	String profileSyncWithAtgInfoFlag = BBBCoreConstants.FALSE;
            	try {
            		final List<String> profileSyncWithAtgInfoFlagList = getBbbCatalogTools().getAllValuesForKey(
            				BBBCmsConstants.CONTENT_CATALOG_KEYS, "ProfileSyncWithAtgInfoTag");
            		if ((profileSyncWithAtgInfoFlagList != null) && !profileSyncWithAtgInfoFlagList.isEmpty()) {
            			profileSyncWithAtgInfoFlag = profileSyncWithAtgInfoFlagList.get(0);
            			this.logDebug("profileSyncWithAtgInfo Flag : " + profileSyncWithAtgInfoFlag);
            		}
            		if ((BBBCoreConstants.TRUE).equalsIgnoreCase(profileSyncWithAtgInfoFlag) && !isShallowProfileUpdateRequest()) {
            			isRegistryUpdated = this.updateRegistry();
            		}
            		this.setSuccessMessage(true);
            	} catch (final BBBSystemException bbbSystemException) {
            		this.logError(LogMessageFormatter.formatMessage(pRequest,
            				BBB_SYSTEM + EXCEPTION_ERROR_IN_HANDLE_UPDATE, BBBCoreErrorConstants.ACCOUNT_ERROR_1001),
            				bbbSystemException);
            	} catch (final BBBBusinessException bbbBusinessException) {
            		this.logError(LogMessageFormatter.formatMessage(pRequest, BUSINESS_EXCEPTION_ERROR_IN_HANDLE_UPDATE,
            				BBBCoreErrorConstants.ACCOUNT_ERROR_1002), bbbBusinessException);
            	}
            	if (!isRegistryUpdated && (BBBCoreConstants.TRUE).equalsIgnoreCase(profileSyncWithAtgInfoFlag) && !isShallowProfileUpdateRequest()) {
            		this.addFormException(new DropletException(getMessageHandler().getErrMsg(
            				BBBCoreConstants.ERR_UPDATEPROFILE, pRequest.getLocale().getLanguage(), this.errorMap, null),
            				BBBCoreConstants.ERR_UPDATEPROFILE));
            		this.setSuccessMessage(false);
            	}
            }
            //Adding personal info to wallet
            String walletId = (String)getProfile().getPropertyValue(BBBCoreConstants.WALLETID);
            String email = (String)getProfile().getPropertyValue(BBBCoreConstants.LOGIN);
            if(!BBBUtility.isEmpty(walletId)){
            	String firstName = (String)getProfile().getPropertyValue(BBBCoreConstants.FIRST_NAME);
            	String lastName = (String)getProfile().getPropertyValue(BBBCoreConstants.LAST_NAME);
            	String mobilePhone = (String)getProfile().getPropertyValue(BBBCoreConstants.MOBILENUMBER);
            	try{
            		UpdateWalletProfileRespVo updateWalletProfileRespVo = new UpdateWalletProfileRespVo();
            		updateWalletProfileRespVo = getCouponsManager().updateWalletProfile(email, firstName, lastName, mobilePhone, walletId);
            		if(updateWalletProfileRespVo.getmErrorStatus().isErrorExists() ){
            			this.addFormException(new DropletException(getMessageHandler().getErrMsg(
            			BBBCoreConstants.ERR_UPDATEPROFILE, pRequest.getLocale().getLanguage(), this.errorMap, null),
                    	BBBCoreConstants.ERR_UPDATEPROFILE));
            			this.setSuccessMessage(false);
            		}
            	}catch(BBBSystemException e){
            		logError("Error updating wallet profile:: "+e);
            	}catch(BBBBusinessException e){
            		logError("Error updating wallet profile:: "+e);
            	}
            }
            String databaseEmail = (String)pRequest.getAttribute("databaseEmail");
            if(email != null && databaseEmail != null
    				&& !databaseEmail.equalsIgnoreCase(email)){
    			//BBBSessionBean session = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
            	BBBSessionBean session = BBBProfileManager.resolveSessionBean(pRequest);
            	
    			session.setEdwDataVO(null);
    		}
        }
        
        if(isShallowProfileUpdateRequest()) {
        	pRequest.setParameter(HANDLE_CREATE, HANDLE_SUCCESS);
        	postCreateUser(pRequest, pResponse);
        	final String email = (String) this.getProfile().getPropertyValue(BBBCoreConstants.LOGIN);
        	TransactionManager transMgr = this.getTransactionManager();
        	TransactionDemarcation transDemarcation = new TransactionDemarcation ();
        	boolean rollback = false;
			try {
				transDemarcation.begin(transMgr, TransactionDemarcation.REQUIRED);
				getProfileManager().updateOrderForCurrentProfile(email, getOrder());
			} catch (TransactionDemarcationException re) {
				rollback = true;
				if (isLoggingError()) {
					logError(re.getMessage(),re);
				}
			} finally {
				try {
					transDemarcation.end(rollback);
				} catch (TransactionDemarcationException e) {
					if (isLoggingError()) {
						logError("TransactionDemarcationException while transaction end: "+ e.getMessage(), e);
					}
				}
			}
        }
    }

    /** This method is overridden to perform the following validations -Whether the New Password and Confirm password
     * matches or not. -The password should not contain the user's firstname and lastname.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse */
    @Override
    protected final void preChangePassword(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) {

        this.logDebug("BBBProfileFormHandler.preChangePassword() method started\n" + " is Request from Mobile : "
                + this.isFromMobile());
        
        ////reset password token validation 
        String resetFlag= (String) this.getValue().get(BBBCoreConstants.RESET_FLAG);
        BBBProfileTools profileTools=(com.bbb.account.BBBProfileTools) getProfileTools();
        if(!BBBUtility.isEmpty(resetFlag)) 
        {
        String emailID = processInputEmailId();
		String receivedToken = (String) this.getValue().get(BBBCoreConstants.URL_TOKEN);
        Boolean generatedPassword =null;
        //Boolean isTokenValid = false;        
        BBBSessionBean sessionBean = ((BBBSessionBean) (pRequest.resolveName(BBBCoreConstants.SESSION_BEAN)));
        RepositoryItem userProfileRepositoryItem = null;
        
		if (!StringUtils.isEmpty(emailID) ) {
			    String forgotPwdToken = null;
			    userProfileRepositoryItem = profileTools.getItemFromEmail(emailID);
				if(null != userProfileRepositoryItem && !StringUtils.isEmpty(receivedToken))
				{
					forgotPwdToken= (String) userProfileRepositoryItem.getPropertyValue(BBBCoreConstants.FORGOT_PWD_TOKEN);
					generatedPassword= (Boolean)userProfileRepositoryItem.getPropertyValue(BBBCoreConstants.GENERATED_PASSWORD);
					if(!generatedPassword)
					{
						processPasswordNotGenreatedError(pRequest);
						return;
					}
					// isTokenValid=validateToken(forgotPwdToken,receivedToken,pRequest);
					if (generatedPassword && !validateToken(forgotPwdToken, receivedToken, pRequest))
						return;
				}
				else
				{
				  processResetEmailError(pRequest);
					return;
				}
			}
		else
		{
			processResetEmailError(pRequest);
			return;
		}		
		 populateAccVoChangePassword(sessionBean, userProfileRepositoryItem);
        }
        /// end of reset password token validation 
        //final String loginProperty = getPropertyManager().getLoginPropertyName();
        validateUserData(pRequest);        
    }


	private String processInputEmailId() {
		String emailID= (String) this.getValue().get(BBBCoreConstants.LOGIN_EMAIL_ID); 
        if (!StringUtils.isEmpty(emailID)) {
			emailID = emailID.toLowerCase().trim();
		}
		return emailID;
	}

	/**
	 * validateUserData - 
	 * This method validates the user data such as email, first name, last name,
	 * password, confirm password etc. If the user data has erroneous values
	 * we'll handle it
	 * 
	 */

	private void validateUserData(final DynamoHttpServletRequest pRequest) {

		String loginEmail = this.getStringValueProperty(getPropertyManager().getLoginPropertyName());
		loginEmail = populateLoginEmail(loginEmail);
		final String oldPassword = this.getStringValueProperty(getPropertyManager().getOldPasswordPropertyName());
		final String password = this.getStringValueProperty(getPropertyManager().getPasswordPropertyName());
		String firstName = this.getStringValueProperty(getPropertyManager().getFirstNamePropertyName());
		String lastName = this.getStringValueProperty(getPropertyManager().getLastNamePropertyName());

		if (!isAccountVoNull() && org.apache.commons.lang.StringUtils.isEmpty(firstName)) {
			firstName = getSessionBean().getAccountVo().getFirstName();
		}

		if (!isAccountVoNull() && org.apache.commons.lang.StringUtils.isEmpty(lastName)) {
			lastName = getSessionBean().getAccountVo().getLastName();
		}
		if (this.isFromMobile()) {
			this.mTools = (BBBProfileTools) this.getProfileTools();
			final RepositoryItem profileItem = this.mTools.getItemFromEmail(loginEmail);

			if (profileItem != null) {

				firstName = (String) profileItem.getPropertyValue(getPropertyManager().getFirstNamePropertyName());
				lastName = (String) profileItem.getPropertyValue(getPropertyManager().getLastNamePropertyName());
				populateValueDictionary(loginEmail, firstName, getPropertyManager().getLastNamePropertyName(),
						lastName);
				super.setRepositoryId(profileItem.getRepositoryId());

			} else {
				processLoginPasswdError(pRequest);
			}

		} else {
			if ((!this.isFromMobile()) && (BBBUtility.isEmpty(firstName))) {
				firstName = (String) this.getProfile()
						.getPropertyValue(getPropertyManager().getFirstNamePropertyName());
			}
			if ((!this.isFromMobile()) && (BBBUtility.isEmpty(lastName))) {
				lastName = (String) this.getProfile().getPropertyValue(getPropertyManager().getLastNamePropertyName());
			}
		}

		validateInputPasswords(pRequest, oldPassword, password);
		validateUser(pRequest, oldPassword, password, firstName, lastName);
	}


	private void validateUser(final DynamoHttpServletRequest pRequest, final String oldPassword, final String password,
			String firstName, String lastName) {
		if ((getLegacyUser() != null) && (this.errorMap.isEmpty())) {
        	validateLegacyUser(pRequest, oldPassword);	
        }
        if (getLegacyUser() == null) {
        	vaidateATGUser(pRequest, password, firstName, lastName);	
        }
	}


	private String populateLoginEmail(String loginEmail) {
		if(!isAccountVoNull() && loginEmail== null)
		   {   //if(loginEmail== null){
		       	loginEmail= (String)getSessionBean().getAccountVo().getEmail();
		       //}
		   }
		    if (!BBBUtility.isEmpty(loginEmail)) {
		        loginEmail = loginEmail.toLowerCase();
		    }
		return loginEmail;
	}


	private void processLoginPasswdError(final DynamoHttpServletRequest pRequest) {
		final String errorMessage = getMessageHandler().getErrMsg(
		        BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR, pRequest.getLocale().getLanguage(), null, null);
		this.addFormException(new DropletException(errorMessage));
		this.errorMap.put(BBBCoreConstants.LOGIN_ERROR, errorMessage);
	}


	private void populateValueDictionary(String loginEmail, String firstName, final String lastNameProperty,
			String lastName) {
		@SuppressWarnings ("unchecked")
		final Dictionary<String, String> value = this.getValue();
		value.put(getPropertyManager().getFirstNamePropertyName(), firstName);
		value.put(lastNameProperty, lastName); 
		value.put(getPropertyManager().getLoginPropertyName(), loginEmail);
	}


	private void processPasswordNotGenreatedError(final DynamoHttpServletRequest pRequest) {
		final String errorMessage = getMessageHandler().getErrMsg(
				BBBCoreConstants.ERR_RESET_LINK, pRequest.getLocale().getLanguage(), null, null);
		this.addFormException(new DropletException(errorMessage));
		this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, errorMessage);
	}


	private boolean isAccountVoNull() {
		return null == getSessionBean().getAccountVo();
	}


	private void processResetEmailError(final DynamoHttpServletRequest pRequest) {
		final String errorMessage = getMessageHandler().getErrMsg(
				BBBCoreConstants.ERR_RESET_EMAIL, pRequest.getLocale().getLanguage(), null, null);
		this.addFormException(new DropletException(errorMessage));
		this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, errorMessage);
	}


	private void vaidateATGUser(final DynamoHttpServletRequest pRequest,
			final String password, String firstName, String lastName) {
		
            if (BBBUtility.isEmpty(firstName)) {
                processEmptyATGUserFirstName(pRequest);

            } else if (BBBUtility.isEmpty(lastName)) {
                processAtgUserEmptyLastName(pRequest);

            } else if (!BBBUtility.isValidFirstName(firstName)) {
                processInvalidATGUsrFirstName(pRequest);
            } else if (!BBBUtility.isValidLastName(lastName)) {
                processInvalidATGUserLastName(pRequest);

            } else {
                validateAtgUserPassword(pRequest, password, firstName, lastName);
            }
	}


	private void validateAtgUserPassword(final DynamoHttpServletRequest pRequest, final String password,
			String firstName, String lastName) {
		boolean passwordContainsFirstNameLastName = BBBUtility.checkPasswordContainsFirstNameLastName(firstName, lastName, password);
		final String errorMessage = getMessageHandler().getErrMsg(
			BBBCoreConstants.ERR_PROFILE_PASSWORDNOTIN_FNAMEORLNAME, pRequest.getLocale().getLanguage(), null,
			null);
		if (passwordContainsFirstNameLastName) {                    
		    this.addFormException(new DropletException(errorMessage,
		            BBBCoreConstants.ERR_PROFILE_PASSWORDNOTIN_FNAMEORLNAME));
		    
		}
		if (passwordContainsFirstNameLastName && this.isAutoLoginAfterChangePassword()) {
		    this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, errorMessage);
		}
	}


	private void processEmptyATGUserFirstName(final DynamoHttpServletRequest pRequest) {
		final String error = getMessageHandler().getErrMsg(
		        BBBCoreConstants.ERR_PROFILE_FIRSTNAME_FIELD_EMPTY, pRequest.getLocale().getLanguage(), null,
		        null);
		this.addFormException(new DropletException(error, BBBCoreConstants.ERR_PROFILE_FIRSTNAME_FIELD_EMPTY));
		if (this.isAutoLoginAfterChangePassword()) { 
		    this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, error);
		}
	}


	private void processAtgUserEmptyLastName(final DynamoHttpServletRequest pRequest) {
		final String error = getMessageHandler().getErrMsg(
		        BBBCoreConstants.ERR_PROFILE_LASTNAME_FIELD_EMPTY, pRequest.getLocale().getLanguage(), null,
		        null);
		this.addFormException(new DropletException(error, BBBCoreConstants.ERR_PROFILE_LASTNAME_FIELD_EMPTY));
		if (this.isAutoLoginAfterChangePassword()) {
		    this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, error);
		}
	}


	private void processInvalidATGUsrFirstName(final DynamoHttpServletRequest pRequest) {
		final String error = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_FIRSTNAME_INVALID,
		        pRequest.getLocale().getLanguage(), null, null);
		this.addFormException(new DropletException(error, BBBCoreConstants.ERR_PROFILE_FIRSTNAME_INVALID));
		if (this.isAutoLoginAfterChangePassword()) {
		    this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, error);
		}
	}


	private void processInvalidATGUserLastName(final DynamoHttpServletRequest pRequest) {
		final String error = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_LASTNAME_INVALID,
		        pRequest.getLocale().getLanguage(), null, null);
		this.addFormException(new DropletException(error, BBBCoreConstants.ERR_PROFILE_LASTNAME_INVALID));
		if (this.isAutoLoginAfterChangePassword()) {
		    this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, error);
		}
	}


	private void validateLegacyUser(final DynamoHttpServletRequest pRequest, final String oldPassword) {
		
            final String email = (String) pRequest.getSession().getAttribute(BBBCoreConstants.EMAIL_ADDR);
            boolean isValid = false;
            if (email != null) {
                isValid = processValidLegacyUser(pRequest, oldPassword, email, isValid);
            }

            if (this.errorMap.isEmpty() && !isValid) {
                processInvalidLegacyUser(pRequest);
            }

	}


	private boolean processValidLegacyUser(final DynamoHttpServletRequest pRequest, final String oldPassword,
			final String email, boolean isValid) {
		try {
		     isValid = getProfileManager().isOldAccountValid(email, getSiteContext().getSite().getId(),
		            oldPassword);

		} catch (final BBBBusinessException exception) {
		    final String error = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_LOGIN_FRAG_TECH_ISSUE,
		            pRequest.getLocale().getLanguage(), null, null);
		    this.addFormException(new DropletException(error, BBBCoreConstants.ERR_LOGIN_FRAG_TECH_ISSUE));
		    this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, error);
		    this.logError(LogMessageFormatter.formatMessage(pRequest,
		            BBBCoreConstants.ERR_LOGIN_FRAG_TECH_ISSUE, BBBCoreErrorConstants.ACCOUNT_ERROR_1126),
		            exception);

		} catch (final BBBSystemException exception) {
		    final String error = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_LOGIN_FRAG_TECH_ISSUE,
		            pRequest.getLocale().getLanguage(), null, null);
		    this.addFormException(new DropletException(error, BBBCoreConstants.ERR_LOGIN_FRAG_TECH_ISSUE));
		    this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, error);
		    this.logError(LogMessageFormatter.formatMessage(pRequest,
		            BBBCoreConstants.ERR_LOGIN_FRAG_TECH_ISSUE, BBBCoreErrorConstants.ACCOUNT_ERROR_1126),
		            exception);
		}
		return isValid;
	}


	private void processInvalidLegacyUser(final DynamoHttpServletRequest pRequest) {
		final String errMsg = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_LEGACY_PASSWORD_ERROR,
		        pRequest.getLocale().getLanguage(), null, null);
		this.addFormException(new DropletException(errMsg, BBBCoreConstants.ERR_LEGACY_PASSWORD_ERROR));
		this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, errMsg);
	}


	private void validateInputPasswords(final DynamoHttpServletRequest pRequest, final String oldPassword,
			final String password) {
		
		final String confirmPassword = this.getStringValueProperty(getPropertyManager().getConfirmPasswordPropertyName());
        this.errorMap = new HashMap<String, String>();
        
		if (BBBUtility.isEmpty(oldPassword)) {
            final String error = getMessageHandler().getErrMsg(
                    BBBCoreConstants.ERR_PROFILE_OLDPASSWORD_FIELD_EMPTY, pRequest.getLocale().getLanguage(), null,
                    null);
            this.addFormException(new DropletException(error, BBBCoreConstants.ERR_PROFILE_OLDPASSWORD_FIELD_EMPTY));
            this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, error);

        } else if (BBBUtility.isEmpty(password)) {
            final String error = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_PASSWORD_FIELD_EMPTY,
                    pRequest.getLocale().getLanguage(), null, null);
            this.addFormException(new DropletException(error, BBBCoreConstants.ERR_PROFILE_PASSWORD_FIELD_EMPTY));
            this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, error);

        } else if (BBBUtility.isEmpty(confirmPassword)) {
            final String error = getMessageHandler().getErrMsg(
                    BBBCoreConstants.ERR_PROFILE_CONFIRMPASSWORD_FIELD_EMPTY, pRequest.getLocale().getLanguage(), null,
                    null);
            this.addFormException(new DropletException(error, BBBCoreConstants.ERR_PROFILE_CONFIRMPASSWORD_FIELD_EMPTY));
            this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, error);

        } else if (!confirmPassword.equals(password)) {
            final String errMsg = getMessageHandler().getErrMsg(
                    BBBCoreConstants.ERR_PROFILE_PASSWORD_CONFIRMPASSWORD_NOTEQUAL, pRequest.getLocale().getLanguage(),
                    null, null);
            this.addFormException(new DropletException(errMsg,
                    BBBCoreConstants.ERR_PROFILE_PASSWORD_CONFIRMPASSWORD_NOTEQUAL));
            this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, errMsg);

        } else if (BBBUtility.isNotEmpty(oldPassword) && oldPassword.equals(password)) {
            final String errMsg = getMessageHandler().getErrMsg(
                    BBBCoreConstants.ERR_PROFILE_PASSWORD_OLD_NEW_NOTEQUAL, pRequest.getLocale().getLanguage(), null,
                    null);
            this.addFormException(new DropletException(errMsg, BBBCoreConstants.ERR_PROFILE_PASSWORD_OLD_NEW_NOTEQUAL));
            this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, errMsg);
        }
	}


	private void populateAccVoChangePassword(BBBSessionBean sessionBean, RepositoryItem userProfileRepositoryItem) {
		if (null != userProfileRepositoryItem  ) {
			  AccountVo accvo = new AccountVo();
				if (null != userProfileRepositoryItem
						.getPropertyValue(BBBCoreConstants.EMAIL)) {
					accvo.setEmail((String) userProfileRepositoryItem
							.getPropertyValue(BBBCoreConstants.EMAIL));
					}
				if (null != userProfileRepositoryItem
						.getPropertyValue(BBBCoreConstants.PASSWORD)) {
					accvo.setPassword((String) userProfileRepositoryItem
							.getPropertyValue(BBBCoreConstants.PASSWORD));
					}
				if (null != userProfileRepositoryItem
						.getPropertyValue(BBBCoreConstants.FIRST_NAME)) {
					accvo.setFirstName((String) userProfileRepositoryItem
							.getPropertyValue(BBBCoreConstants.FIRST_NAME));
					}
				if (null != userProfileRepositoryItem
						.getPropertyValue(BBBCoreConstants.LAST_NAME)) {
					accvo.setLastName((String) userProfileRepositoryItem
							.getPropertyValue(BBBCoreConstants.LAST_NAME));
					}
			sessionBean.setAccountVo(accvo);
			this.setValueProperty(getPropertyManager().getOldPasswordPropertyName(), accvo.getPassword());
			}
	}

    /** This calls the web-service to initiate the web-service forgot password flow.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws BBBBusinessException Exception
     * @throws BBBSystemException Exception */
    public final void handleLegacyForgetPassword(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws BBBSystemException, BBBBusinessException {

    	String key = "legacyForgetPassword";
    	setSuccessURL(pRequest,key);
    	setErrorURL(pRequest,key);
    	
        final String email = this.getStringValueProperty(BBBCoreConstants.LOGIN);

        final ForgetPasswordResponseVO forgetPasswordResponseVO = getProfileManager().forgetPasswordResponseVO(email,
                getSiteContext().getSite().getId());

        if (forgetPasswordResponseVO.getErrorStatus().isErrorExists()) {

            this.setFormErrorVal(true);
            final ErrorStatus errStatus = forgetPasswordResponseVO.getErrorStatus();

            // Business error from the web-services
            if (!BBBUtility.isEmpty(errStatus.getDisplayMessage())) {
                if (errStatus.getErrorId() == BBBCoreConstants.ERR_CODE_RECLAIM_INVALID_USER_ID) {
                    this.logError(LogMessageFormatter.formatMessage(null, HANDLE_LEGACY_FORGET_PASSWORD + "Error ID"
                            + errStatus.getErrorId(), BBBCoreErrorConstants.ACCOUNT_ERROR_1005));
                    this.addFormException(new DropletException(errStatus.getDisplayMessage(),
                            RECLAIM_ACCOUNT_INVALID_EMAIL));
                    this.errorMap
                    .put(RECLAIM_ACCOUNT_INVALID_EMAIL, BBBCoreConstants.ERR_RECLAIM_ACCOUNT_INVALID_EMAIL);
                } else {
                    this.logError(LogMessageFormatter.formatMessage(null, HANDLE_LEGACY_FORGET_PASSWORD + "Business"
                            + ERROR_RECEIVED_FORGET_PASSWORD_RESPONSE_VO + errStatus.getDisplayMessage(),
                            BBBCoreErrorConstants.ACCOUNT_ERROR_1006));
                    this.addFormException(new DropletException(errStatus.getDisplayMessage(),
                            RECLAIM_ACCOUNT_DISP_ERROR_FORGET_PASSWORD));
                    this.errorMap.put(RECLAIM_ACCOUNT_DISP_ERROR_FORGET_PASSWORD, errStatus.getDisplayMessage());
                }
            }

            // Technical Error
            if (!BBBUtility.isEmpty(errStatus.getErrorMessage())) {
                this.logError(LogMessageFormatter.formatMessage(null, HANDLE_LEGACY_FORGET_PASSWORD + "Technical"
                        + ERROR_RECEIVED_FORGET_PASSWORD_RESPONSE_VO + errStatus.getErrorMessage(),
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1007));
                this.addFormException(new DropletException(
                        BBBCoreConstants.ERR_RECLAIM_ACCOUNT_TECH_ERROR_FORGET_PASSWORD,
                        RECLAIM_ACCOUNT_TECH_ERROR_FORGET_PASSWORD));
                this.errorMap.put(RECLAIM_ACCOUNT_TECH_ERROR_FORGET_PASSWORD,
                        BBBCoreConstants.ERR_RECLAIM_ACCOUNT_TECH_ERROR_FORGET_PASSWORD);
            }
            // Field validation error from webservice
            if ((errStatus.getValidationErrors() != null) && !errStatus.getValidationErrors().isEmpty()) {
                final Iterator<com.bbb.framework.webservices.vo.ValidationError> valErrorIterator = errStatus
                        .getValidationErrors().iterator();
                while (valErrorIterator.hasNext()) {
                    final com.bbb.framework.webservices.vo.ValidationError valError = valErrorIterator.next();
                    if (!BBBUtility.isEmpty(valError.getKey()) && !BBBUtility.isEmpty(valError.getValue())) {
                        this.logError(LogMessageFormatter.formatMessage(null, HANDLE_LEGACY_FORGET_PASSWORD
                                + "Validation Errors received while forget password from Legacy System\nkey="
                                + valError.getKey() + " validation Error: " + valError.getValue(),
                                BBBCoreErrorConstants.ACCOUNT_ERROR_1008));
                        this.addFormException(new DropletException(valError.getValue(), "reclaim_account_ws_err"));
                        if (valError.getKey().toLowerCase().contains("siteFlag")) {
                            this.errorMap.put("reclaim_account_ws_err", BBBCoreConstants.ERR_RECLAIM_ACCOUNT_WS_ERR);
                        } else {
                            this.errorMap.put("reclaim_account_ws_err", valError.getValue());
                        }
                    }
                }
            }

        } else {

            this.setLegacyForgetPasswordStatus(true);
            this.setStrReminderSent(BBBCoreConstants.RECLAIM_PWD_SENT);
        }

        this.logDebug("BBBProfileFormHandler.handleLegacyForgetPassword() method ends");

    }
    
	/**
	 * This method is used to reset password with generated token for user's
	 * profile for MOBILE.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean handleResetPasswordWithToken(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug("BBBProfileFormHandler.handleResetPasswordWithToken() method started");
		final String password = this
				.getStringValueProperty(getPropertyManager()
						.getPasswordPropertyName());
		final String confirmPassword = this
				.getStringValueProperty(getPropertyManager()
						.getConfirmPasswordPropertyName());
		final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();

		if (this.errorMap.isEmpty()) {
			this.preResetPasswordWithToken(pRequest, pResponse);
			if (this.errorMap.isEmpty() && !BBBUtility.isEmpty(password)
					&& !BBBUtility.isEmpty(confirmPassword)) {
				final TransactionManager tm = this.getTransactionManager();
				final TransactionDemarcation td = this
						.getTransactionDemarcation();
				if (tm != null) {
					try {
						td.begin(tm, TransactionDemarcation.REQUIRED);
						if (this.getFormExceptions().size() == 0) {
							final String loginProperty = getPropertyManager()
									.getLoginPropertyName();
							String email = this
									.getStringValueProperty(loginProperty);
							email = email.toLowerCase().trim();
							final String sPassword = this
									.getStringValueProperty(getPropertyManager()
											.getPasswordPropertyName());
							final boolean isUpdated = getProfileManager()
									.updatePasswordFromToken(email, sPassword);
							if (isUpdated) {
								//isSuccessMessage() = true;
								setSuccessMessage(true);
								this.getProfile()
										.setPropertyValue(
												getPropertyManager()
														.getResetPasswordPropertyName(),
												Boolean.FALSE);
							} else if (!isUpdated) {
								final String errMsg = getMessageHandler()
										.getErrMsg(
												BBBCoreConstants.UPDATE_FAILED,
												pRequest.getLocale()
														.getLanguage(),
												errorPlaceHolderMap, null);
								this.addFormException(new DropletException(
										errMsg, BBBCoreConstants.UPDATE_FAILED));
								this.errorMap
										.put(BBBCoreConstants.PASSWORD_ERROR,
												errMsg);
							}
						}
					} catch (final TransactionDemarcationException e) {

						this.logError(LogMessageFormatter.formatMessage(
								pRequest, TRANSACTION_DEMARCATION
										+ EXCEPTION_RESET_PASSWORD_TOKEN,
								BBBCoreErrorConstants.ACCOUNT_ERROR_1384), e);
						throw new ServletException(e);
					} finally {
						try {
							if (tm != null) {
								td.end();
							}
						} catch (final TransactionDemarcationException e) {
							this.logError(
									TRANSACTION_DEMARCATION_ERROR_IN_COMMITING_TRANSACTION,
									e);
						}

					}
				}
			} else {
				logDebug("BBBProfileFormHandler.handleResetPasswordWithToken(): Validate generated token only.");
			}
		}
		this.logDebug("BBBProfileFormHandler.handleResetPasswordWithToken() method ends.");
		return this.checkFormRedirect(this.getChangePasswordSuccessURL(),
				this.getChangePasswordErrorURL(), pRequest, pResponse);
	}

	/**
	 * This method is used to validate all the form fields and password
	 * constraints including token sent to email address during reset password.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	private final void preResetPasswordWithToken(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		logDebug("BBBProfileFormHandler.preResetPasswordWithToken() method Started");
		final String receivedToken = this.getStringValueProperty(getPropertyManager().getResetPasswordToken());
		String formEmail = this.getStringValueProperty(getPropertyManager().getResetEmailPropertyName());
		final String password = this.getStringValueProperty(getPropertyManager().getPasswordPropertyName());
		final String confirmPassword = this
				.getStringValueProperty(getPropertyManager().getConfirmPasswordPropertyName());
		Boolean generatedPassword = false;
		final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();

		if (BBBUtility.isEmpty(formEmail) || BBBUtility.isEmpty(receivedToken)) {
			processEmptyProfile(pRequest, errorPlaceHolderMap);
		} else {
			RepositoryItem[] userProfileRepositoryItem = null;
			formEmail = formEmail.toLowerCase().trim();
			String ForgotPwdToken = null;
			userProfileRepositoryItem = ((BBBProfileTools) this.getProfileTools()).getItemsFromEmail(formEmail);
			if (null != userProfileRepositoryItem) {

				ForgotPwdToken = (String) userProfileRepositoryItem[0]
						.getPropertyValue(BBBCoreConstants.FORGOT_PWD_TOKEN);
				generatedPassword = (Boolean) userProfileRepositoryItem[0]
						.getPropertyValue(BBBCoreConstants.GENERATED_PASSWORD);

				if (StringUtils.isEmpty(ForgotPwdToken) || !generatedPassword) {
					processInvalidProfile(pRequest, errorPlaceHolderMap);
					return;
				}
				if (!(StringUtils.isEmpty(ForgotPwdToken) || !generatedPassword)
						&& (!validateToken(ForgotPwdToken, receivedToken, pRequest))) {
					return;
				}
			} else {
				processInvalidProfile(pRequest, errorPlaceHolderMap);
				return;
			}

			processResetPasswordToken(pRequest, password, confirmPassword, errorPlaceHolderMap,
					userProfileRepositoryItem);
		}
	}


	private void processResetPasswordToken(final DynamoHttpServletRequest pRequest, final String password,
			final String confirmPassword, final Map<String, String> errorPlaceHolderMap,
			RepositoryItem[] userProfileRepositoryItem) {
		
		if (null != userProfileRepositoryItem
					&& userProfileRepositoryItem.length > 0
					&& !BBBUtility.isEmpty(password)
					&& !BBBUtility.isEmpty(confirmPassword)) {
				populateUserDataPreResetPswd(pRequest, getPropertyManager().getLoginPropertyName(),
						getPropertyManager().getFirstNamePropertyName(), getPropertyManager().getLastNamePropertyName(),
						password, confirmPassword, errorPlaceHolderMap, userProfileRepositoryItem);
			} 
			else {
				processInvalidResetPaswdToken(pRequest, errorPlaceHolderMap);				
			}
	}


	private void processInvalidResetPaswdToken(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap) {
		final String errorMessage = this.getMessageHandler().getErrMsg(
						BBBCoreConstants.ERR_PROFILE_INVALID_RESET_PASSWORD_TOKEN,
						pRequest.getLocale().getLanguage(),
						errorPlaceHolderMap, null);
		this.addFormException(new DropletException(errorMessage,BBBCoreConstants.ERR_PROFILE_INVALID_RESET_PASSWORD_TOKEN));
//				final String errorMessage = getMessageHandler().getErrMsg(
//						BBBCoreConstants.ERR_RESET_EMAIL, pRequest.getLocale().getLanguage(), null, null);
//				this.addFormException(new DropletException(errorMessage));
		this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, errorMessage);
	}


	private void processInvalidProfile(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap) {
		final String errorMessage = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_INVALID_RESET_PASSWORD_TOKEN,
				pRequest.getLocale().getLanguage(),errorPlaceHolderMap, null);
		this.addFormException(new DropletException(errorMessage,BBBCoreConstants.ERR_PROFILE_INVALID_RESET_PASSWORD_TOKEN));
		this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, errorMessage);
	}


	private void processEmptyProfile(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap) {
		final String errorMessage = getMessageHandler().getErrMsg(
				BBBCoreConstants.ERR_PROFILE_EMPTY_RESET_PASSWORD_TOKEN,
				pRequest.getLocale().getLanguage(), errorPlaceHolderMap,
				null);
		this.addFormException(new DropletException(errorMessage,
				BBBCoreConstants.ERR_PROFILE_EMPTY_RESET_PASSWORD_TOKEN));
		this.errorMap.put(BBBCoreConstants.RESET_PASSWORD_ERROR,
				errorMessage);
	}


	private void populateUserDataPreResetPswd(final DynamoHttpServletRequest pRequest, final String loginProperty,
			final String firstNameProperty, final String lastNameProperty, final String password,
			final String confirmPassword, final Map<String, String> errorPlaceHolderMap,
			RepositoryItem[] userProfileRepositoryItem) {
		//			userProfileItem = (MutableRepositoryItem) userProfileRepositoryItem[0];
					final String email = (String) userProfileRepositoryItem[0]
							.getPropertyValue(BBBCoreConstants.EMAIL);
					final String firstName = (String) userProfileRepositoryItem[0]
							.getPropertyValue(BBBCoreConstants.FIRST_NAME);
					final String lastName = (String) userProfileRepositoryItem[0]
							.getPropertyValue(BBBCoreConstants.LAST_NAME);
					this.setValueProperty(loginProperty, email);
					this.setValueProperty(firstNameProperty, firstName);
					this.setValueProperty(lastNameProperty, lastName);

					this.errorMap = new HashMap<String, String>();
					validateFieldsPreResetPswd(pRequest, password, confirmPassword, errorPlaceHolderMap,
							firstName, lastName);
	}


	private void validateFieldsPreResetPswd(final DynamoHttpServletRequest pRequest, final String password,
			final String confirmPassword, final Map<String, String> errorPlaceHolderMap, final String firstName,
			final String lastName) {
		
		validatePasswordPreReset(pRequest, password, confirmPassword, errorPlaceHolderMap);

		if (BBBUtility.isEmpty(firstName)) {
			processEmptyFirstName(pRequest, errorPlaceHolderMap);

		} else if (BBBUtility.isEmpty(lastName)) {
			processEmptyLastName(pRequest, errorPlaceHolderMap);
		} else if (!BBBUtility.isValidFirstName(firstName)) {
			processInvalidFirstName(pRequest, errorPlaceHolderMap);
		} else if (!BBBUtility.isValidLastName(lastName)) {
			processInvalidLastName(pRequest, errorPlaceHolderMap);
		} else {
			processPasswordWithUserName(pRequest, password, errorPlaceHolderMap, firstName, lastName);
		}
	}


	private void processPasswordWithUserName(final DynamoHttpServletRequest pRequest, final String password,
			final Map<String, String> errorPlaceHolderMap, final String firstName, final String lastName) {
		if (BBBUtility.checkPasswordContainsFirstNameLastName(
				firstName, lastName, password)) {
			final String errorMessage = this
					.getMessageHandler()
					.getErrMsg(
							BBBCoreConstants.ERR_PROFILE_PASSWORDNOTIN_FNAMEORLNAME,
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null);
			this.addFormException(new DropletException(
					errorMessage,
					BBBCoreConstants.ERR_PROFILE_PASSWORDNOTIN_FNAMEORLNAME));
		}
	}


	private void processInvalidLastName(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap) {
		final String error = getMessageHandler().getErrMsg(
				BBBCoreConstants.ERR_PROFILE_LASTNAME_INVALID,
				pRequest.getLocale().getLanguage(),
				errorPlaceHolderMap, null);
		this.addFormException(new DropletException(error,
				BBBCoreConstants.ERR_PROFILE_LASTNAME_INVALID));
	}


	private void processInvalidFirstName(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap) {
		final String error = getMessageHandler().getErrMsg(
				BBBCoreConstants.ERR_PROFILE_FIRSTNAME_INVALID,
				pRequest.getLocale().getLanguage(),
				errorPlaceHolderMap, null);
		this.addFormException(new DropletException(error,
				BBBCoreConstants.ERR_PROFILE_FIRSTNAME_INVALID));
	}


	private void processEmptyLastName(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap) {
		final String error = getMessageHandler().getErrMsg(
				BBBCoreConstants.ERR_PROFILE_LASTNAME_FIELD_EMPTY,
				pRequest.getLocale().getLanguage(),
				errorPlaceHolderMap, null);
		this.addFormException(new DropletException(error,
				BBBCoreConstants.ERR_PROFILE_LASTNAME_FIELD_EMPTY));
	}


	private void processEmptyFirstName(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap) {
		final String error = getMessageHandler().getErrMsg(
				BBBCoreConstants.ERR_PROFILE_FIRSTNAME_FIELD_EMPTY,
				pRequest.getLocale().getLanguage(),
				errorPlaceHolderMap, null);
		this.addFormException(new DropletException(error,
				BBBCoreConstants.ERR_PROFILE_FIRSTNAME_FIELD_EMPTY));
	}


	private void validatePasswordPreReset(final DynamoHttpServletRequest pRequest, final String password,
			final String confirmPassword, final Map<String, String> errorPlaceHolderMap) {
		
		if (BBBUtility.isEmpty(password)) {
			final String error = getMessageHandler().getErrMsg(
					BBBCoreConstants.ERR_PROFILE_PASSWORD_FIELD_EMPTY,
					pRequest.getLocale().getLanguage(),
					errorPlaceHolderMap, null);
			this.addFormException(new DropletException(error,
					BBBCoreConstants.ERR_PROFILE_PASSWORD_FIELD_EMPTY));
			this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, error);

		} else if (BBBUtility.isEmpty(confirmPassword)) {
			final String error = this
					.getMessageHandler()
					.getErrMsg(
							BBBCoreConstants.ERR_PROFILE_CONFIRMPASSWORD_FIELD_EMPTY,
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null);
			this.addFormException(new DropletException(
					error,
					BBBCoreConstants.ERR_PROFILE_CONFIRMPASSWORD_FIELD_EMPTY));
			this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, error);

		} else if (!confirmPassword.equals(password)) {
			final String errMsg = this
					.getMessageHandler()
					.getErrMsg(
							BBBCoreConstants.ERR_PROFILE_PASSWORD_CONFIRMPASSWORD_NOTEQUAL,
							pRequest.getLocale().getLanguage(),
							errorPlaceHolderMap, null);
			this.addFormException(new DropletException(
					errMsg,
					BBBCoreConstants.ERR_PROFILE_PASSWORD_CONFIRMPASSWORD_NOTEQUAL));
			this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, errMsg);
		}
	}

    /** This method is overridden to change the new password in user's profile.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return boolean
     * @throws ServletException Exception
     * @throws IOException Exception */
    @SuppressWarnings("unchecked")
	@Override
    public final boolean handleChangePassword(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	// BBBH-391 | Client DOM XSRF changes
    	//String siteId = SiteContextManager.getCurrentSiteId();
		if (StringUtils.isNotEmpty(getFromPage())) {
			setChangePasswordSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
			setChangePasswordErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));

		}
        this.logDebug("BBBProfileFormHandler.handleChangePassword() method started");
        String email = (String) pRequest.getSession().getAttribute(BBBCoreConstants.EMAIL_ADDR);
        String oldPassword = (String) pRequest.getSession().getAttribute(OLD_PWD);
        
		if (BBBUtility.isNotEmpty(oldPassword)) {
			this.getValue().put(BBBCoreConstants.OLD_PASSWORD, oldPassword);
        }

        if (this.errorMap.isEmpty() && BBBUtility.isNotEmpty(email)) {
            email = email.toLowerCase().trim();
            this.preChangePassword(pRequest, pResponse);
            if (this.getFormExceptions().size() == 0) {
                final String sPassword = this.getStringValueProperty(getPropertyManager().getPasswordPropertyName());
                final boolean isUpdated = getProfileManager().updatePassword(email, sPassword);
                if (isUpdated) {
                    //isSuccessMessage() = true;
                	setSuccessMessage(true);
					if (getProfile().isTransient()) {
						this.autoSignIn(email, pRequest, pResponse);
					}
                    this.getProfile().setPropertyValue(getPropertyManager().getResetPasswordPropertyName(),
                            Boolean.FALSE);
                    pRequest.getSession().removeAttribute(BBBCoreConstants.EMAIL_ADDR);
                    pRequest.getSession().removeAttribute(OLD_PWD);
                    pRequest.getSession().removeAttribute("fstName");
                    pRequest.getSession().removeAttribute("lstName");
                } else if (!isUpdated) {
                    final String errMsg = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR,
                            pRequest.getLocale().getLanguage(), null, null);
                    this.addFormException(new DropletException(errMsg, BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR));
                    this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, errMsg);
                }
            }
        }


        if (BBBUtility.isEmpty(email)) {
            this.logDebug("Email is null in current Request-session");
            final Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
            if (!profile.isTransient()) {
                super.handleChangePassword(pRequest, pResponse);
                if (this.getFormExceptions().size() == 0) {
                    //isChangePasswordSuccessMessage() = true;
                    setChangePasswordSuccessMessage(true);
                }
            } else {
                final String errMsg = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR,
                        pRequest.getLocale().getLanguage(), null, null);
                this.addFormException(new DropletException(errMsg, BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR));
                this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, errMsg);
            }
        }
        this.logDebug("BBBProfileFormHandler.handleChangePassword() method ends");

        return this.checkFormRedirect(this.getChangePasswordSuccessURL(), this.getChangePasswordErrorURL(), pRequest,
                pResponse);
    } 

    /** This method is overridden from BBBProfileFormHandler class. Performs the validation of create profile form fields
     * UC_Create_Profile
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception */
    @Override
    protected final void preCreateUser(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.preCreateUser() method started");

        this.errorMap = new HashMap<String, String>();

        /*
         * code refactored to reduce cyclomatic complexity and do validations related to login and password in
         * validateLoginPassword method
         */
        
        //changes starts for defect BBBSL-7637
        Order order = this.getOrder();
        if(null != order){
            final List<RepositoryItem> srcAppliedCoupons = ((BBBPromotionTools)getPromotionTools()).getCouponListFromOrder(order);
            setSrcAppliedCoupons(srcAppliedCoupons);           
        }
        //changes ends for defect BBBSL-7637

        if (this.validateLoginPassword(pRequest)) {

            /*
             * code refactored to reduce cyclomatic complexity and do validations related to phone and mobile numbers in
             * validatePhoneAndMobileNumbers method
             */

            this.validatePhoneAndMobileNumbers();

//           if(getBbbCatalogTools().isChallengeQuestionOn() && !this.getEditValue().isEmpty()){
//        	   Map<String, Object> questionMap=  this.getEditValue();
//        	  String ChallengeAnswer1=(String)questionMap.get(BBBCoreConstants.CHALLENGE_ANSWER_1);
//        	  String ChallengeAnswer2=(String)questionMap.get(BBBCoreConstants.CHALLENGE_ANSWER_2);
//        	  if((StringUtils.isEmpty(ChallengeAnswer1) && !StringUtils.isEmpty(ChallengeAnswer2)) || (StringUtils.isEmpty(ChallengeAnswer2) && !StringUtils.isEmpty(ChallengeAnswer1)) ){
//        		  this.addFormException(new DropletException(BBBCoreConstants.ERR_CHALLENGE_QUESTION_SETUP,
//                          BBBCoreConstants.ERR_CHALLENGE_QUESTION_SETUP));
//                  this.errorMap.put(BBBCoreConstants.CREATE_CHALLENGE_QUESTION_ERROR,
//                          BBBCoreConstants.ERR_CHALLENGE_QUESTION_SETUP);
//        	  }
//        	   
//           }

            final String sFirstName = (String) this.getValueProperty(getPropertyManager()
                    .getFirstNamePropertyName());
            if ((sFirstName == null) || sFirstName.trim().isEmpty()) {
                this.addFormException(new DropletException(BBBCoreConstants.ERR_PROFILE_FIRSTNAME_FIELD_EMPTY,
                        BBBCoreConstants.ERR_PROFILE_FIRSTNAME_FIELD_EMPTY));
                this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_FIRSTNAME_ERROR,
                        BBBCoreConstants.ERR_PROFILE_FIRSTNAME_FIELD_EMPTY);
            } else {

                if (!BBBUtility.isValidFirstName(sFirstName)) {
                    this.addFormException(new DropletException(BBBCoreConstants.ERR_PROFILE_FIRSTNAME_INVALID,
                            BBBCoreConstants.ERR_PROFILE_FIRSTNAME_INVALID));
                    this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_FIRSTNAME_ERROR,
                            BBBCoreConstants.ERR_PROFILE_FIRSTNAME_INVALID);
                }
            }

            final String sLastName = (String) this
                    .getValueProperty(getPropertyManager().getLastNamePropertyName());
            if ((sLastName == null) || sLastName.trim().isEmpty()) {
                this.addFormException(new DropletException(BBBCoreConstants.ERR_PROFILE_LASTNAME_FIELD_EMPTY,
                        BBBCoreConstants.ERR_PROFILE_LASTNAME_FIELD_EMPTY));
                this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_LASTNAME_ERROR,
                        BBBCoreConstants.ERR_PROFILE_LASTNAME_FIELD_EMPTY);
            } else {

                if (!BBBUtility.isValidLastName(sLastName)) {
                    this.addFormException(new DropletException(BBBCoreConstants.ERR_PROFILE_LASTNAME_INVALID,
                            BBBCoreConstants.ERR_PROFILE_LASTNAME_INVALID));
                    this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_LASTNAME_ERROR,
                            BBBCoreConstants.ERR_PROFILE_LASTNAME_INVALID);
                }
        }
        if (this.errorMap.isEmpty() && this.validateLoginPassword(pRequest)) {

            /*
             * code refactored to reduce cyclomatic complexity and do validations related to phone and mobile numbers in
             * validatePhoneAndMobileNumbers method
             */

            this.validatePhoneAndMobileNumbers();

            String email = (String) this.getValueProperty(getPropertyManager().getLoginPropertyName());
            
        	final boolean emailPattern = !BBBUtility.isValidEmail(email);
            
            if (BBBUtility.isEmpty(email)) {
                processEmptyProfileEmail();
            } else if (emailPattern) {
                this.addFormException(new DropletException(BBBCoreConstants.ERR_PROFILE_EMAIL_INVALID,
                        BBBCoreConstants.ERR_PROFILE_EMAIL_INVALID));
                this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_EMAIL_ERROR, BBBCoreConstants.ERR_PROFILE_EMAIL_INVALID);
            }
            
			/* Below code is moved to handleRegistration method for change of defect BBBSL-6896*/
            /*RepositoryItem[] items = null;
            try {
				items = mTools.lookupUsers(email, email, BBBProfileServicesConstant.USER);
			} catch (RepositoryException e) {
				logDebug("Exception occured during searching for duplicate user", e);
			}
            if(items!=null && items.length!=0){
            	final String emailErrMsg =getMessageHandler().getErrMsg( BBBCoreConstants.ERR_ACCOUNT_ALREADY_EXISTS, pRequest.getLocale().getLanguage(), null, null);
            	this.addFormException(new DropletException(BBBCoreConstants.ERR_ACCOUNT_ALREADY_EXISTS, BBBCoreConstants.ERR_ACCOUNT_ALREADY_EXISTS));
            	this.errorMap.put(BBBCoreConstants.ERR_ACCOUNT_ALREADY_EXISTS, emailErrMsg);
            }*/
            if (this.errorMap.isEmpty()) {
                super.preCreateUser(pRequest, pResponse);
            }

            this.logDebug("BBBProfileFormHandler.preCreateUser() method ends");

        } else {

            this.logDebug("BBBProfileFormHandler.preCreateUser() method ends");

        }
        }
    }

	/** @return Phone number validation */
    private boolean validatePhoneAndMobileNumbers() {

        this.logDebug("BBBProfileFormHandler.validatePhoneAndMobileNumbers() method started");

        boolean result = true;
        final String sPhoneNumber = (String) this.getValueProperty(getPropertyManager()
                .getPhoneNumberPropertyName());

        if (!BBBUtility.isEmpty(sPhoneNumber) && !BBBUtility.isValidPhoneNumber(sPhoneNumber)) {
            this.addFormException(new DropletException(BBBCoreConstants.PHONE_NUMBER_IS_INVALID,
                    BBBCoreConstants.PHONE_NUMBER_IS_INVALID));
            this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_PHONENUMBER_ERROR, BBBCoreConstants.ERR_PROFILE_INVALID);
            result = false;
        }

        final String sMobileNumber = (String) this.getValueProperty(getPropertyManager()
                .getMobileNumberPropertyName());

        if (!BBBUtility.isEmpty(sMobileNumber) && !BBBUtility.isValidPhoneNumber(sMobileNumber)) {
            this.addFormException(new DropletException(BBBCoreConstants.MOBILE_NUMBER_IS_INVALD,
                    BBBCoreConstants.MOBILE_NUMBER_IS_INVALD));
            this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_MOBILENUMBER_ERROR, BBBCoreConstants.ERR_PROFILE_INVALID);
            result = false;
        }

        this.logDebug("BBBProfileFormHandler.validatePhoneAndMobileNumbers() method ends");

        return result;
    }

    /** @param pRequest DynamoHttpServletRequest
     * @return Password Validation */
    private boolean validateLoginPassword(final DynamoHttpServletRequest pRequest) {

        this.logDebug("BBBProfileFormHandler.validateLoginPassword() method started");

        this.mTools = (BBBProfileTools) this.getProfileTools();
        boolean result = true;
        final String strLogin = (String) this.getValueProperty(getPropertyManager().getLoginPropertyName());
      
       

        if ((strLogin == null) || strLogin.isEmpty()) {
            final String passwordError = getMessageHandler().getErrMsg(
                    BBBCoreConstants.ERR_PROFILE_LOGIN_FIELD_EMPTY, pRequest.getLocale().getLanguage(), null, null);
            this.addFormException(new DropletException(passwordError, BBBCoreConstants.ERR_PROFILE_LOGIN_FIELD_EMPTY));
            this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_EMAIL_ERROR, passwordError);
            result = false;
        } else {
            final String sPassword = (String) this.getValueProperty(getPropertyManager().getPasswordPropertyName());

            if ((sPassword == null) || sPassword.isEmpty()) {
                final String passwordError = getMessageHandler().getErrMsg(
                        BBBCoreConstants.ERR_PROFILE_PASSWORD_FIELD_EMPTY, pRequest.getLocale().getLanguage(), null, null);
                this.addFormException(new DropletException(passwordError, BBBCoreConstants.ERR_PROFILE_PASSWORD_FIELD_EMPTY));
                this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_PASSWORD_ERROR, passwordError);
                result = false;
            } else {
            	if(null==  this.getValue().get( getPropertyManager().getSaveCreditCardInfoToProfile())){
            		
                final String sConfirmPassword = (String) this.getValueProperty(getPropertyManager()
                        .getConfirmPasswordPropertyName());
                if ((sConfirmPassword == null) || sConfirmPassword.isEmpty()) {
                    final String passwordError = getMessageHandler().getErrMsg(
                            BBBCoreConstants.ERR_PROFILE_CONFIRMPASSWORD_FIELD_EMPTY, pRequest.getLocale().getLanguage(),
                            null, null);
                    this.addFormException(new DropletException(passwordError,
                            BBBCoreConstants.ERR_PROFILE_CONFIRMPASSWORD_FIELD_EMPTY));
                    this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_CONFIRMPASSWORD_ERROR, passwordError);
                    result = false;
                } else {

                    if (!(sConfirmPassword).equals(sPassword)) {
                        final String passwordError = getMessageHandler().getErrMsg(
                                BBBCoreConstants.ERR_PROFILE_PASSWORD_CONFIRMPASSWORD_NOTEQUAL,
                                pRequest.getLocale().getLanguage(), null, null);
                        this.addFormException(new DropletException(PASSWORD_ERROR,
                                BBBCoreConstants.ERR_PROFILE_PASSWORD_CONFIRMPASSWORD_NOTEQUAL));
                        this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_CONFIRMPASSWORD_ERROR, passwordError);
                        result = false;
                    }
                }}

                final String firstName = (String) this.getValueProperty(getPropertyManager()
                        .getFirstNamePropertyName());
                final String lastName = (String) this.getValueProperty(getPropertyManager()
                        .getLastNamePropertyName());
                
            	if ((!BBBUtility.isEmpty(firstName) && !BBBUtility.isEmpty(lastName)) && (sPassword.toLowerCase().contains(firstName.toLowerCase())
                        || sPassword.toLowerCase().contains(lastName.toLowerCase()))) {
                    final String passwordError = getMessageHandler().getErrMsg(
                            BBBCoreConstants.ERR_PROFILE_PASSWORD_CONTAINS_NAME, pRequest.getLocale().getLanguage(),
                            null, null);
                    this.addFormException(new DropletException(passwordError,
                            BBBCoreConstants.ERR_PROFILE_PASSWORD_CONTAINS_NAME));
                    this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_CONTAINS_NAME, passwordError);
                    result = false;
                }

                final Map<String, String> map = new HashMap<String, String>();

                map.put(getPropertyManager().getLoginPropertyName(), strLogin);

                final boolean passedRules = this.mTools.getPasswordRuleChecker().checkRules(sPassword, map);
                if (!passedRules) {
                    this.addFormException(new DropletException(this.mTools.getPasswordRuleChecker()
                            .getLastRuleCheckedDescription(), BBBCoreErrorConstants.ACCOUNT_ERROR_1290));
                    this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_PASSWORD_ERROR, this.mTools
                            .getPasswordRuleChecker().getLastRuleCheckedDescription());
                }

                result = passedRules;
            }
        }

        this.logDebug("BBBProfileFormHandler.validateLoginPassword() method ends");

        return result;

    }

    /** This method is overridden from BBBProfileFormHandler Performs the following 
     * 1. sends email to registered user 
     * 2. calls postCreateUser for ProfileFormHandler class 
     * 3. Associates siteId to the registered user 
     * 4. in case of the legacy users their member id is stored in bb_user_site_assoc and
     *    reclaimAccount web-service is called UC_Create_Profile
     *  
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception */
    @Override
    protected void postCreateUser(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.postCreateUser() method started");

        String profileRegistrationTibcoEmailFlag = BBBCoreConstants.FALSE;
        final String siteId = getSiteContext().getSite().getId();
        final String sProfileType = this.getLoginProfileType();
        final String sEmail = (String) this.getValueProperty(getPropertyManager().getEmailAddressPropertyName());
        String memberId = null;
        if (!BBBUtility.isEmpty(getSessionBean().getLegacyMemberId())) {
            memberId = getSessionBean().getLegacyMemberId();
        }
        if(pRequest.getContextPath().equalsIgnoreCase(BBBCoreConstants.CONTEXT_TBS)){
    		postTBSCreateUser(pRequest);
        }
        
      //below method calling is done for defect BBBSL-7637 
        if(null != this.getSrcAppliedCoupons() && this.getSrcAppliedCoupons().size() > 0){
        	((BBBPromotionTools)getPromotionTools()).removeAlreadyAppliedCoupons(this.getSrcAppliedCoupons(), this.getProfile(), this.getOrder().getSiteId());
       		try {
				this.mTools.repriceOrder(this.getOrder(),
						this.getProfile(), this.getUserPricingModels(),
						getUserLocale(pRequest, pResponse),
						this.mTools.getRepriceOrderPricingOp());
			} catch (CommerceException e) {
				 this.logError("CommerceException in BBBProfileFormHandler.postCreateUser()", e);
			}
		}

        this.removeOrderCookie(pRequest, pResponse);
        populateDataPostLogin(pRequest, pResponse);
        
        
        if (!BBBUtility.isEmpty(getSessionBean().getLegacyMemberId())) {
            memberId = getSessionBean().getLegacyMemberId();
        }
        this.mTools = (BBBProfileTools) this.getProfileTools();
        processSavedItems(pRequest, pResponse);
        checkGiftListItemsForShipAvailability(pRequest);         

        
        
		if ((sEmail == null) || sEmail.isEmpty()) {
			processEmptyProfileEmail();
		} else {
			profileRegistrationTibcoEmailFlag = processValidEmailProfile(pRequest, pResponse, siteId, memberId, sEmail,
					sProfileType, profileRegistrationTibcoEmailFlag);
		}
        processRedirects(pRequest, pResponse);
        invokeBVCallNonTransientProfile(pRequest);
        this.doCoRegLinking(sEmail, pRequest, pResponse, siteId);
        populateAddProfileCookie(pResponse);
        triggerEmailPostCreateUser(pRequest, pResponse, siteId, sEmail, sProfileType, profileRegistrationTibcoEmailFlag);
        this.logDebug("BBBProfileFormHandler.postCreateUser() method ends");
    }


	private void populateDataPostLogin(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) {
		
		
        addKickStarterSuccessUrl(pRequest);
        populateSubscribeProperty(pRequest);
        
	}


	private String processValidEmailProfile(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, final String siteId, String memberId, final String sEmail,
			final String sProfileType, String profileRegistrationTibcoEmailFlag) throws ServletException, IOException {
		if (sProfileType == null) {
		    processNullProfile();
		} else {
		    // Code for sending email begins here
		    profileRegistrationTibcoEmailFlag = isprofileRegistrationTibcoEmail(pRequest,
					profileRegistrationTibcoEmailFlag);
		    final String emailOptInFlag = this.isEmailOptIn() ? BBBCoreConstants.YES : BBBCoreConstants.NO;
		    final String emailOptInBabyCAFlag = this.isEmailOptIn_BabyCA() ? BBBCoreConstants.YES : BBBCoreConstants.NO;
		    final String emailOptInSharedSite = this.isEmailOptInSharedSite() ? BBBCoreConstants.YES : BBBCoreConstants.NO;
		    prepareEmailData(pRequest, pResponse, siteId, memberId, sEmail, emailOptInFlag, emailOptInBabyCAFlag, emailOptInSharedSite);
		}
		
		if (!BBBUtility.isEmpty(memberId)) {
		    processAccountErrors(pRequest, memberId);
		}
		
		populateProfileProperties();
		invokeUserTokenBvrrPostCreateUser(pRequest, sEmail);
		return profileRegistrationTibcoEmailFlag;
	}


	private void triggerEmailPostCreateUser(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse,
			final String siteId, final String sEmail, final String sProfileType,
			String profileRegistrationTibcoEmailFlag) {
		
		if ((BBBCoreConstants.TRUE).equalsIgnoreCase(profileRegistrationTibcoEmailFlag)) {
            // Send email using ATG System and Tibco Message
			sendRegistrationTibcoEmail(pRequest, siteId);
        }
        
        // Send email using ATG System and Tibco Message
        this.sendRegistrationEmail(pRequest, sProfileType, sEmail, siteId);
	}

	/** This method is used to make the Tibco email messages to run in a new thread making it async. 
	 * @param emailParams
	 */
	private void sendTibcoEmailInNewThread(Object emailParams) {
		// TODO Auto-generated method stub
		SendTibcoEmailNewThread newTibcoThread = new SendTibcoEmailNewThread(emailParams);
		Thread newThread = new Thread(newTibcoThread);
		newThread.start();
	}
	
	private void processEmptyProfileEmail() {
		this.addFormException(new DropletException(BBBCoreConstants.ERR_PROFILE_EMAIL_EMPTY,
		        BBBCoreConstants.ERR_PROFILE_EMAIL_EMPTY));
		this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_EMAIL_ERROR, BBBCoreConstants.ERR_PROFILE_EMAIL_EMPTY);
	}

	/**
	 * This method is to prepare the data required to send E-mail post create
	 * user
	 * 
	 */
	private void prepareEmailData(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse,
			final String siteId, String memberId, final String sEmail, final String emailOptInFlag,
			final String emailOptInBabyCAFlag, final String emailOptInSharedSite) throws ServletException, IOException {
		
		addSiteToProfile(pRequest, siteId, memberId, sEmail, emailOptInFlag, emailOptInBabyCAFlag, emailOptInSharedSite);

		// Add SessionBean related code here
		/**
		 * Start: LTL - this code is to get ship grp id of ship grp with empty ship method 
		 * and set the empty ship method back to same id post create user.
		 */
		//List<ShippingGroup> shippingGroupList = getOrder().getShippingGroups();
		String emptyShipMethod = getEmptyShipGroupId();
		/**
		 *End: LTL - this code is to get ship grp id of ship grp with empty ship method 
		 *and set the empty ship method back to same id post create user. 
		 */
		
		// Added for Cookie Auto Login while creating account for Mobile App
		addAutoLoginCookie(pRequest);
		// END: Added for Cookie Auto Login while creating account for Mobile App
		
		super.postCreateUser(pRequest, pResponse);
		storeUserChallengeQuestion();
		
		//Start: LTL - this code is to set the empty ship method back to ship grp with id being emptyShipMethod.
		setEmptyShipMethod(emptyShipMethod);
		//End: LTL - this code is to set the empty ship method back to ship grp with id being emptyShipMethod.
	}

	/**
	 * This method is used to set the destination URL after the task is completed
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processRedirects(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {

		/*
		 * interceptor code for add to item to redirect the user to the product
		 * details page--Start
		 */
		redirectToImportOrAddToItem(pRequest, pResponse);
		
		// <!-- create registry interceptor-->		
		//Commented below line as part of PS-64095
		//addSuccessUrlBasedOnSite(pRequest);
		/*
		 * // interceptor code for add to item to redirect the user to the
		 * product // details page --End
		 */
		// <!-- Social Recommendation Flow interceptor BPS-456-->
		if (getSessionBean().isRecommenderFlow()) {
			this.setCreateSuccessURL(getSessionBean().getRecommenderRedirectUrl() + "&createAccRecommenderFlow=true");
		}
		// Social Recommendation Flow --End BPS-456
		
		//checkList
		if (BBBCoreConstants.TRUE.equalsIgnoreCase((String) pRequest.getSession().getAttribute(BBBCoreConstants.CHECK_LIST_FLOW))
				&& BBBUtility.isNotBlank((String) pRequest.getSession().getAttribute(BBBCoreConstants.CHECK_LIST_FLOW_SUCCESS_URL))) {
				this.setCreateSuccessURL((String) pRequest.getSession().getAttribute(BBBCoreConstants.CHECK_LIST_FLOW_SUCCESS_URL));
			}
	}


	private void populateSubscribeProperty(final DynamoHttpServletRequest pRequest) {
		if (!this.getFormError() && isEmailOptIn()) {
            pRequest.getSession().setAttribute(BBBCoreConstants.SUBSCRIBE, BBBCoreConstants.YES);
        }
	}
    
    /**
     * LTL - this code is to get ship grp id of ship grp with empty ship method 
     * and set the empty ship method back to same id post create user.
     */
	private String getEmptyShipGroupId() {
		String emptyShipMethod = null;
		for(ShippingGroup shippingGroup : (List<ShippingGroup>)getOrder().getShippingGroups()){
			if(BBBUtility.isEmpty(shippingGroup.getShippingMethod())){
				emptyShipMethod = shippingGroup.getId();
			}
		}
		return emptyShipMethod;
	}


	private void populateProfileProperties() {
		this.getProfile().setPropertyValue(getPropertyManager().getNewRegistrationPropertyName(), Boolean.TRUE);
		this.getProfile().setPropertyValue(getPropertyManager().getDescriptionPropertyName(),this.getIdGenerator().getDcPrefix() + "-" + System.getProperty(BBBCoreConstants.JVM_PROPERTY));
	}


	private void processNullProfile() {
		this.addFormException(new DropletException(
		        BBBCoreConstants.ERR_PROFILE_REPOSITORY_PROFILE_TYPE_FETCHED_NULL,
		        BBBCoreConstants.ERR_PROFILE_REPOSITORY_PROFILE_TYPE_FETCHED_NULL));
		this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_SYSTEM_ERROR,
		        BBBCoreConstants.ERR_PROFILE_REPOSITORY_PROFILE_TYPE_FETCHED_NULL);
	}

	private void processSavedItems(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		if (((this.getSavedItemsSessionBean() != null) && (this.getSavedItemsSessionBean().getGiftListVO() != null))
                || (pRequest.getSession().getAttribute(BBBCoreConstants.ADDED) != null)) {
            addNonEmptySavedItemToWishList(pRequest, pResponse);
        } else if (getSessionBean().getMoveCartItemToWishSuccessUrl() != null) {
            this.addItemTOGiftList(pRequest, pResponse);
        }
	}


	private void populateAddProfileCookie(final DynamoHttpServletResponse pResponse) {
		if (this.getProfile() != null) {
			final JSONObject profileJsonObject = this.createProfileJSONObject(this.getProfile().getRepositoryId(),
					getSiteContext().getSite().getId());
			final Cookie profileCookie = new Cookie(this.getProfileCookieName(), profileJsonObject.toString());
			profileCookie.setMaxAge(this.getProfileCookieAge());
			profileCookie.setPath(this.getProfileCookiePath());
			BBBUtility.addCookie(pResponse, profileCookie, true);
		}
	}

	/**
	 * Method call for Bazaar Voice product ratings and reviews
	 * 
	 */
	
	private void invokeBVCallNonTransientProfile(final DynamoHttpServletRequest pRequest) {
		
		if (!BBBUtility.isEmpty(this.getFacebookLinking()) && !this.getProfile().isTransient()) {

			final RepositoryItem bbbUserProfile = this.getProfile().getDataSource();
			final UserVO fbUserProfile = (UserVO) pRequest.getSession().getAttribute(FBConstants.FB_BASIC_INFO);
			if ((bbbUserProfile != null) && (fbUserProfile != null)) {
				try {
					this.getFacebookProfileTool().linkBBBProfileWithFBProfile(bbbUserProfile, fbUserProfile);
				} catch (final BBBSystemException e) {
					this.addFormException(new DropletException(BBBCoreConstants.ERROR_OCCURR_LINKING_FB,
							BBBCoreConstants.ERROR_OCCURR_LINKING_FB));
					this.logError(LogMessageFormatter.formatMessage(pRequest,
							"BBBSystemException - BBBProfileFormHandler.postCreateUser()",
							BBBCoreErrorConstants.ACCOUNT_ERROR_1019), e);
				}
			} else {
				this.addFormException(new DropletException(BBBCoreConstants.ERROR_OCCURR_LINKING_FB,
						BBBCoreConstants.ERROR_OCCURR_LINKING_FB));
			}
		}
	}


	private void redirectToImportOrAddToItem(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if (getSessionBean().getImportRegistryRedirectUrl() != null) {
            // interceptor code for import registry
            this.importRegistryPostCreate(pRequest, pResponse);
        } else {
            this.addToItemPostCreateUser(pRequest, pResponse);
        }
	}


	private void invokeUserTokenBvrrPostCreateUser(final DynamoHttpServletRequest pRequest, final String sEmail) {
		try {
		    // Method call for Bazaar Voice product ratings and reviews
		    this.checkUserTokenBVRR(pRequest, null, false, sEmail);
		} catch (final BBBBusinessException e) {
		    this.addFormException(new DropletException(getMessageHandler().getErrMsg(
		            BBBCoreConstants.BAZAAR_VOICE_ERROR, pRequest.getLocale().getLanguage(), null, null)));
		    this.logError(LogMessageFormatter.formatMessage(pRequest,
		            "BBBBusinessException - BBBProfileFormHandler.checkUserTokenBVRR()",
		            BBBCoreErrorConstants.ACCOUNT_ERROR_1018), e);
		}
	}


	private void processAccountErrors(final DynamoHttpServletRequest pRequest, String memberId) {
		try {
		    final ReclaimAccountResponseVO reclaimAccountResponseVO = getProfileManager()
		            .reclaimAccountResponseVO(this.getProfile().getRepositoryId().toString(), this
		                    .getSiteContext().getSite().getId(), memberId);
		    if (reclaimAccountResponseVO.getErrorStatus().isErrorExists()) {
		        this.logDebug("BBBProfileFormHandler.postCreateUser() | reclaimAccountResponseVO | ");

		        final ErrorStatus errStatus = getProfileManager().reclaimAccountResponseVO(
		                this.getProfile().getRepositoryId().toString(),
		                SiteContextManager.getCurrentSiteId().toString(), memberId).getErrorStatus();
		        processReclaimAccountErrors(pRequest, errStatus);
		        processAccountTechnicalErrors(pRequest, errStatus);
		        processAccountBusinessErrors(pRequest, errStatus);
		        processAccountValidationErrors(pRequest, errStatus);
		    }
		} catch (final BBBSystemException e) {
		    this.logError(
		            LogMessageFormatter.formatMessage(pRequest,
		                    Thread.currentThread().getStackTrace()[0].getMethodName()
		                    + RECLAIM_ACCOUNT_RESPONSE_VO, BBBCoreErrorConstants.ACCOUNT_ERROR_1016), e);
		} catch (final BBBBusinessException e) {
		    this.logError(
		            LogMessageFormatter.formatMessage(pRequest,
		                    Thread.currentThread().getStackTrace()[0].getMethodName()
		                    + RECLAIM_ACCOUNT_RESPONSE_VO, BBBCoreErrorConstants.ACCOUNT_ERROR_1017), e);
		}
	}


	private void processReclaimAccountErrors(final DynamoHttpServletRequest pRequest, final ErrorStatus errStatus) {
		
		if (!BBBUtility.isEmpty(errStatus.getDisplayMessage())
		        && (errStatus.getErrorId() == BBBCoreConstants.ERR_CODE_ACCOUNT_NOT_FOUND))
		{
		    this.logError(LogMessageFormatter.formatMessage(pRequest, Thread.currentThread()
		            .getStackTrace()[0].getMethodName() + ERROR_ID_IS + errStatus.getErrorId(),
		            BBBCoreErrorConstants.ACCOUNT_ERROR_1011));
		    this.addFormException(new DropletException(BBBCoreConstants.ERR_RECLAIM_ACCOUNT_NOT_FOUND,
		            RECLAIM_ACCOUNT_NOT_FOUND));
		    this.errorMap
		    .put(RECLAIM_ACCOUNT_NOT_FOUND, BBBCoreConstants.ERR_RECLAIM_ACCOUNT_NOT_FOUND);
		} 
		if (!BBBUtility.isEmpty(errStatus.getDisplayMessage())
		        && (errStatus.getErrorId() == BBBCoreConstants.ERR_CODE_UNABLE_TO_RECLAIM)) {
		    this.logError(LogMessageFormatter.formatMessage(pRequest, Thread.currentThread()
		            .getStackTrace()[0].getMethodName() + ERROR_ID_IS + errStatus.getErrorId(),
		            BBBCoreErrorConstants.ACCOUNT_ERROR_1012));
		    this.addFormException(new DropletException(
		            BBBCoreConstants.ERR_RECLAIM_ACCOUNT_UNABLE_TO_RECLAIM,
		            RECLAIM_ACCOUNT_UNABLETO_RECLAIM));
		    this.errorMap.put(RECLAIM_ACCOUNT_UNABLETO_RECLAIM,
		            BBBCoreConstants.ERR_RECLAIM_ACCOUNT_UNABLE_TO_RECLAIM);
		}
	}

	/**
	 *  Field validation error from webservice handled here
	 * 
	 */
	private void processAccountValidationErrors(final DynamoHttpServletRequest pRequest, final ErrorStatus errStatus) {
		
		if ((errStatus.getValidationErrors() != null) && !errStatus.getValidationErrors().isEmpty()) {		    
		    final Iterator<ValidationError> valErrorIterator = errStatus.getValidationErrors()
		            .iterator();
		    while (valErrorIterator.hasNext()) {

		        final ValidationError valError = valErrorIterator.next();
		        if (!BBBUtility.isEmpty(valError.getKey()) && !BBBUtility.isEmpty(valError.getValue())) {
		            this.logError(LogMessageFormatter.formatMessage(
		                    pRequest,
		                    Thread.currentThread().getStackTrace()[0].getMethodName()
		                    + "Validation Errors received while reclaimAccountResponseVO from Legacy"
		                    + " System\nkey = " + valError.getKey() + " validation Error: "
		                    + valError.getValue(), BBBCoreErrorConstants.ACCOUNT_ERROR_1015));
		            this.addFormException(new DropletException(valError.getValue(), valError.getKey()));
		        }
		    }
		}
	}

	/**
	 * Business error from webservice handled here
	 */
	private void processAccountBusinessErrors(final DynamoHttpServletRequest pRequest, final ErrorStatus errStatus) {
		if ((errStatus.getErrorId() == BBBCoreConstants.ZERO)
		        && !BBBUtility.isEmpty(errStatus.getDisplayMessage())) { 
		    this.logError(LogMessageFormatter.formatMessage(
		            pRequest,
		            Thread.currentThread().getStackTrace()[0].getMethodName()
		            + "Business Error received reclaimAccountResponseVO : "
		            + errStatus.getDisplayMessage(), BBBCoreErrorConstants.ACCOUNT_ERROR_1014));
		    this.addFormException(new DropletException(errStatus.getDisplayMessage(),
		            ERROR_RECLAIM_ACCOUNT));
		    this.errorMap.put(ERROR_RECLAIM_ACCOUNT, errStatus.getDisplayMessage());
		}
	}


	private void processAccountTechnicalErrors(final DynamoHttpServletRequest pRequest, final ErrorStatus errStatus) {
		if (!BBBUtility.isEmpty(errStatus.getErrorMessage())) {
		    this.logError(LogMessageFormatter.formatMessage(
		            pRequest,
		            Thread.currentThread().getStackTrace()[0].getMethodName()
		            + "Technical Error received reclaimAccountResponseVO "
		            + errStatus.getErrorMessage(), BBBCoreErrorConstants.ACCOUNT_ERROR_1013));
		    this.addFormException(new DropletException(BBBCoreConstants.ERR_RECLAIM_ACCOUNT,
		            RECLAIM_ACCOUNT_TECH_ERROR_RECLAIM_ACCOUNT));
		    this.errorMap.put(RECLAIM_ACCOUNT_TECH_ERROR_RECLAIM_ACCOUNT,
		            BBBCoreConstants.ERR_RECLAIM_ACCOUNT);
		}
	}

	/**
	 * Start: LTL - this code is to set the empty ship method back to ship grp
	 * with id being emptyShipMethod.
	 */
	private void setEmptyShipMethod(String emptyShipMethod) {
		try {
			if(BBBUtility.isNotEmpty(emptyShipMethod) && getOrder().getShippingGroup(emptyShipMethod) instanceof BBBHardGoodShippingGroup){
				getOrder().getShippingGroup(emptyShipMethod).setShippingMethod("");
			}
		} catch (ShippingGroupNotFoundException e) {
			if (this.isLoggingError()) {
		        this.logError("Exception occured while fetching shipping group " + "for multi Ship flow - ", e);
		    }
		    this.addFormException(new DropletException(ERROR_SHIPPING_GROUP_ID_INVALID, ERROR_SHIPPING_GROUP_ID_INVALID));

		} catch (InvalidParameterException e) {
			if (this.isLoggingError()) {
		        this.logError("Exception occured while fetching shipping group " + "for multi Ship flow - ", e);
		    }
		    this.addFormException(new DropletException(ERROR_SHIPPING_GROUP_ID_INVALID, ERROR_SHIPPING_GROUP_ID_INVALID));
		}
	}


	private void storeUserChallengeQuestion() {
		if (getBbbCatalogTools().isChallengeQuestionOn() && null != this.getEditValue() && !this.getEditValue().isEmpty()) {
			// Storing  challenge Question  for user
			if (!StringUtils.isEmpty((String) this.getEditValue().get(
					BBBCoreConstants.CHALLENGE_QUESTION_1))
					&& !StringUtils
							.isEmpty((String) this
									.getEditValue()
									.get(BBBCoreConstants.CHALLENGE_QUESTION_2))) {
				mTools.storeChallengeQuestionInProfile(
						this.getProfile().getDataSource()
								.getRepositoryId(), this.getEditValue());
			}
		}
	}

	/**
	 * Added for Cookie Auto Login while creating account for Mobile App
	 */
	private void addAutoLoginCookie(final DynamoHttpServletRequest pRequest) {
		if(BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility.getOriginOfTraffic())){
			if(null != this.getProfile()){
				if(isLoggingDebug()){
					logDebug("Updating the User Profile Auto Login Property Status");
				}
				pRequest.setAttribute(getPropertyManager().getAutoLoginPropertyName(), true);
				this.getProfile().setPropertyValue(getPropertyManager().getAutoLoginPropertyName(), true);
			}
		}
	}


	private void addSiteToProfile(final DynamoHttpServletRequest pRequest, final String siteId, String memberId,
			final String sEmail, final String emailOptInFlag, final String emailOptInBabyCAFlag, final String emailOptInSharedSite) {
		try {

		    if ((siteId != null) && getSharedCheckBoxEnabled()) {
		        //getProfileManager().addSiteToProfile(sEmail, siteId, memberId, null, emailOptInFlag);
		        // add this check to create UserSiteItems for BedBathUS and BuyBuyBaby
		        if(siteId.equals(BBBCoreConstants.SITE_BAB_US)){
					getProfileManager().addSiteToProfile(sEmail, TBSConstants.SITE_TBS_BAB_US,
							memberId, null, emailOptInFlag);
					getProfileManager().addSiteToProfile(sEmail, TBSConstants.SITE_TBS_BBB,
							memberId, null, emailOptInSharedSite);
				}else if(siteId.equals(BBBCoreConstants.SITE_BBB)){
					getProfileManager().addSiteToProfile(sEmail, TBSConstants.SITE_TBS_BBB,
							memberId, null, emailOptInFlag);
					getProfileManager().addSiteToProfile(sEmail, TBSConstants.SITE_TBS_BAB_US,
							memberId, null, emailOptInSharedSite);
				}else if(siteId.equals(TBSConstants.SITE_TBS_BAB_US)){
					getProfileManager().addSiteToProfile(sEmail, TBSConstants.SITE_TBS_BBB,
							memberId, null, emailOptInFlag);
				}else if(siteId.equals(TBSConstants.SITE_TBS_BBB)){
					getProfileManager().addSiteToProfile(sEmail, TBSConstants.SITE_TBS_BAB_US,
							memberId, null, emailOptInFlag);
				}
		    } else {
		        this.mTools.createSiteItemRedirect(sEmail, siteId, memberId, null, emailOptInFlag,emailOptInBabyCAFlag);
		    }
		} catch (final BBBSystemException e) {
		    this.logError(LogMessageFormatter.formatMessage(pRequest,
		            BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE,
		            BBBCoreErrorConstants.ACCOUNT_ERROR_1128), e);
		    this.addFormException(new DropletException(BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE,
		            BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE));
		    this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_SYSTEM_ERROR,
		            BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE);
		}
	}


	private String isprofileRegistrationTibcoEmail(final DynamoHttpServletRequest pRequest,
			String profileRegistrationTibcoEmailFlag) {
		try {
		    final List<String> profileRegistrationTibcoEmailFlagList = getBbbCatalogTools()
		            .getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS,
		                    PROFILE_REGISTRATION_TIBCO_EMAIL_TAG);
		    if ((profileRegistrationTibcoEmailFlagList != null)
		            && !profileRegistrationTibcoEmailFlagList.isEmpty()) {
		        profileRegistrationTibcoEmailFlag = profileRegistrationTibcoEmailFlagList.get(0);
		        this.logDebug("profileRegistrationTibcoEmailFlag  : " + profileRegistrationTibcoEmailFlag);
		    }
		} catch (final Exception e) {
		    this.logError(LogMessageFormatter.formatMessage(pRequest,
		            EXCEPTION_PROFILE_REGISTRATION_TIBCO_EMAIL_TAG_KEY_NOT_FOUND,
		            BBBCoreErrorConstants.ACCOUNT_ERROR_1009), e);
		}
		return profileRegistrationTibcoEmailFlag;
	}


	private void addNonEmptySavedItemToWishList(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		try {
		    getGiftListHandler().addSavedItemTOWishList(pRequest, pResponse);
		} catch (final RepositoryException e) {
		    this.logError("RepositoryException in BBBProfileFormHandler.postCreateUser()", e);
		}
		this.addItemTOWishList(pRequest, pResponse);
		if (pRequest.getSession().getAttribute(BBBCoreConstants.ADDED) != null) {
		    this.setCreateSuccessURL((getSessionBean().getProductDetailsRedirectUrl()));
		}
	}

	/**
	 * adding Success URL if User is coming from Kick Starter Page to Manage his
	 * active registry.
	 */
	private void addKickStarterSuccessUrl(final DynamoHttpServletRequest pRequest) {
		if(getSessionBean().getMngActRegistry()!=null ){
        	if(getSessionBean().getKickStarterId()!=null){
        	if(getSessionBean().getKickStarterEventType()!=null && getSessionBean().getKickStarterEventType().equals(BBBGiftRegistryConstants.TOP_CONSULTANT)){
        		this.setCreateSuccessURL(pRequest.getContextPath()+"/topconsultant/"+getSessionBean().getKickStarterId());
        	}else{
        		this.setCreateSuccessURL(pRequest.getContextPath()+"/shopthislook/"+getSessionBean().getKickStarterId());
        	}
        	}else{
        		this.setCreateSuccessURL(pRequest.getContextPath()+"/kickstarters");
        	}
        }
	}
    /** This method is overridden from BBBProfileFormHandler to create the BBB siteAssociation when a profile created in TBS site. 
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception */
	private void postTBSCreateUser(final DynamoHttpServletRequest pRequest) {
		final String emailOptInFlag = this.isEmailOptIn() ? BBBCoreConstants.YES : BBBCoreConstants.NO;
		final String emailOptInBabyCAFlag = this.isEmailOptIn_BabyCA() ? BBBCoreConstants.YES : BBBCoreConstants.NO;
		
		String siteId = SiteContextManager.getCurrentSiteId();
		final String sEmail = (String) this.getValueProperty(getPropertyManager().getEmailAddressPropertyName());
		String memberId = null;
		if (!BBBUtility.isEmpty(this.getSessionBean().getLegacyMemberId())) {
		    memberId = this.getSessionBean().getLegacyMemberId();
		}
		
		try {
			RepositoryItem lProfileItem = getProfileTools().getItemFromEmail(sEmail);
			Map userSiteItemsMap = null;
			if(lProfileItem  != null ){
				userSiteItemsMap = (Map) lProfileItem.getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM);

				if(userSiteItemsMap == null){
					userSiteItemsMap = new HashMap();
				}
				((BBBProfileTools)getProfileTools()).createSiteItemRedirect(sEmail, siteId, memberId, null, emailOptInFlag,emailOptInBabyCAFlag, lProfileItem);
				String lBBBSite = getTbsBBBSiteMap().get(siteId);
				if(!StringUtils.isBlank(lBBBSite)){
					if(!userSiteItemsMap.containsKey(lBBBSite)){
						((BBBProfileTools)getProfileTools()).createSiteItemRedirect(sEmail, lBBBSite, memberId, null, emailOptInFlag,emailOptInBabyCAFlag, lProfileItem);
					}
				}
			}
		} catch (BBBSystemException e) {
			 this.logError(LogMessageFormatter.formatMessage(pRequest,
		             BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE,
		             BBBCoreErrorConstants.ACCOUNT_ERROR_1128), e);
		     this.addFormException(new DropletException(BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE,
		             BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE));
		     this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_SYSTEM_ERROR,
		             BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE);
		}
	}

    /** CoReg Link to reg after user registration
     * 
     * @param emailId
     * @param pRequest
     * @param pResponse
     * @param siteId
     * @throws ServletException
     * @throws IOException */
    public final void doCoRegLinking(final String emailId, final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse, final String siteId) throws ServletException, IOException {
        try {
            this.mTools = (BBBProfileTools) this.getProfileTools();
            final Profile pProfile = this.getProfile();
            if (pProfile != null) {
                final boolean linkCoRegToReg = getGiftRegistryManager().doCoRegLinking(emailId, pProfile, siteId,
                        pRequest);
                	
                if (linkCoRegToReg) {

                    this.logDebug("BBBProfileFormHandler.postCreateUser() doCoRegLinking successfull");

                } else {
                    this.logDebug("BBBProfileFormHandler.postCreateUser() doCoRegLinking:: err_coreg_link_reg ");

                }
            } else {
                final String msg = this.formatUserMessage(BBBCoreErrorConstants.ACCOUNT_ERROR_1316, pRequest);

                this.logDebug("BBBProfileFormHandler.postCreateUser() doCoRegLinking" + msg);

            }
        } catch (final RepositoryException exc) {
            this.logError(
                    LogMessageFormatter.formatMessage(pRequest, "doCoRegLinking fails RepositoryException"
                            + FROM_POST_CREATE_USER, BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1012), exc);
        } catch (final BBBSystemException e) {
            this.logError(
                    LogMessageFormatter.formatMessage(pRequest, "doCoRegLinking fails SystemException"
                            + FROM_POST_CREATE_USER, BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1012), e);
        } catch (final BBBBusinessException e) {
            this.logError(
                    LogMessageFormatter.formatMessage(pRequest, "doCoRegLinking fails BusinessException"
                            + FROM_POST_CREATE_USER, BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1012), e);
            this.logDebug("BBBProfileFormHandler.postCreateUser() doCoRegLinking method ends");

        }
    }

    /** Redirect URL Import Registry Post Login
     * 
     * @param pRequest
     * @param pResponse
     * @throws ServletException
     * @throws IOException */
    public final void importRegistryPostLogin(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.importRegistryPostLogin() method started");

        this.mLoginSuccessURL = getSessionBean().getImportRegistryRedirectUrl();
        this.mLoginErrorURL = getSessionBean().getImportRegistryRedirectUrl();
        getSessionBean().setImportRegistryRedirectUrl(null);

        this.logDebug("BBBProfileFormHandler.AddToItemPostLoginUser() method end");

    }

    /** Redirect URL Import Registry Post Create.
     * 
     * @param pRequest
     * @param pResponse
     * @throws ServletException
     * @throws IOException */
    private void importRegistryPostCreate(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.importRegistryPostCreate() method started");

        this.setCreateSuccessURL(getSessionBean().getImportRegistryRedirectUrl());
        getSessionBean().setImportRegistryRedirectUrl(null);

        this.logDebug("BBBProfileFormHandler.importRegistryPostCreate() method end");

    }

    /** add item to gift registry
     * 
     * @param pRequest
     * @param pResponse
     * @throws ServletException
     * @throws IOException */
    private void addToItemPostCreateUser(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.AddToItemPostCreateUser() method started");

        final GiftRegistryViewBean giftRegistryViewBean = getSessionBean().getGiftRegistryViewBean();
        if ((giftRegistryViewBean != null) && (giftRegistryViewBean.getSuccessURL() != null)) {
            String addItemRedirectURL = giftRegistryViewBean.getSuccessURL();
            //BBBSL-4294 - add pop up variable as true to open a create registry modal on PDP when user is doing add to cart as guest and create a account.
            addItemRedirectURL = addItemRedirectURL + "&noregpopup=true";
            this.setCreateSuccessURL(addItemRedirectURL);
            // mSessionBean.setGiftRegistryViewBean(null);
        }

        this.logDebug("BBBProfileFormHandler.AddToItemPostCreateUser() method end");

    }

    /** 1. Does the validations for registering the email. 2. Returns to the registration continuation or account extend
     * or the login page accordingly while calling the duplicate email validation. UC_Create_Profile
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse */

    private void preRegister(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {

        this.logDebug("BBBProfileFormHandler.preRegister() method started");

        this.mTools = (BBBProfileTools) this.getProfileTools();
        this.errorMap = new HashMap<String, String>();
        final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
        String loginEmail = (String) this.getValue().get(getPropertyManager().getEmailAddressPropertyName());
        if ((loginEmail == null) || loginEmail.trim().isEmpty()) {
            final String emailEmptyError = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_EMAIL_EMPTY,
                    pRequest.getLocale().getLanguage(), null, null);
            this.addFormException(new DropletException(emailEmptyError, BBBCoreConstants.ERR_PROFILE_EMAIL_EMPTY));
            this.errorMap.put(BBBCoreConstants.REGISTER_ERROR, emailEmptyError);
        } else {

            boolean isDuplicateInput = false;
            loginEmail = loginEmail.toLowerCase();

            if (!BBBUtility.isValidEmail(loginEmail)) {
                final String emailInvalidError = getMessageHandler().getErrMsg(
                        BBBCoreConstants.ERR_PROFILE_EMAIL_INVALID, pRequest.getLocale().getLanguage(), null, null);
                this.addFormException(new DropletException(emailInvalidError,
                        BBBCoreConstants.ERR_PROFILE_EMAIL_INVALID));
                this.errorMap.put(BBBCoreConstants.REGISTER_ERROR, emailInvalidError);
            } else {
            	String status="";
                final RepositoryItem repItem = this.mTools.getItemFromEmail(loginEmail); 
                if(null!=repItem)
                status=(String)repItem.getPropertyValue(getPropertyManager().getStatusPropertyName() );

                if(StringUtils.isEmpty(status)){
                	logDebug("status: new Profile Creation Flow...");
                }
                else {
                	logDebug("Existing Profile status\t:"+status);
                }
                 
                if (!BBBCoreConstants.SHALLOW_PROFILE_STATUS_VALUE.equalsIgnoreCase(status) && repItem != null) {
                    if (getProfileManager().isUserPresentToOtherGroup(repItem, getSiteContext().getSite().getId())) {
                        final String loginEmptyError = getMessageHandler().getErrMsg(
                                BBBCoreConstants.ERR_USER_ALREADY_ASSOCIATED_TO_SITE,
                                pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null);
                        this.addFormException(new DropletException(loginEmptyError,
                                BBBCoreConstants.ERR_USER_ALREADY_ASSOCIATED_TO_SITE));
                        this.errorMap.put(BBBCoreConstants.REGISTER_ERROR, loginEmptyError);
                    } else {

                        final Object tempMigrationFlag = repItem.getPropertyValue(getPropertyManager()
                                .getMigratedAccount());
                        final Object tempUserMigratedLoginProp = repItem.getPropertyValue(getPropertyManager()
                                .getLoggedIn());
                        if ((tempMigrationFlag != null) && (tempUserMigratedLoginProp != null)) {
                            this.migrationFlag = ((Boolean) repItem.getPropertyValue(getPropertyManager()
                                    .getMigratedAccount())).booleanValue();
                            /*isUserMigratedLoginProp() = ((Boolean) repItem.getPropertyValue(getPropertyManager()
                            .getLoggedIn())).booleanValue();*/
                            setUserMigratedLoginProp(((Boolean) repItem.getPropertyValue(getPropertyManager()
                                    .getLoggedIn())).booleanValue());
                            
                        } else {
                            this.migrationFlag = false;
                            //isUserMigratedLoginProp() = false;
                            setUserMigratedLoginProp(false);
                        }
                        if (this.migrationFlag && !isUserMigratedLoginProp()) {
                            //getLegacyUser() = BBBCoreConstants.YES;
                        	setLegacyUser(BBBCoreConstants.YES);
                            pRequest.getSession().setAttribute(BBBCoreConstants.EMAIL_ADDR, loginEmail);
                        }
                        isDuplicateInput = this.mTools.isDuplicateEmailAddress(loginEmail, getSiteContext().getSite().getId());
                        
                        if (isDuplicateInput) {
                                     if(!BBBCoreConstants.SHALLOW_PROFILE_STATUS_VALUE.equalsIgnoreCase(status)){                         	
                            final String loginEmptyError = getMessageHandler().getErrMsg(
                                    BBBCoreConstants.ERR_USER_ALREADY_ASSOCIATED_TO_SITE,
                                    pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null);
                            this.addFormException(new DropletException(loginEmptyError,
                                    BBBCoreConstants.ERR_USER_ALREADY_ASSOCIATED_TO_SITE));
                            this.errorMap.put(BBBCoreConstants.REGISTER_ERROR, loginEmptyError);
                        }
                          	
                      
                    }
                    }

                    if (this.errorMap.isEmpty()) {
                        @SuppressWarnings ("unchecked")
                        final Map<String, String> siteItems = (Map<String, String>) repItem
                        .getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM);
                        if (siteItems != null) {
                            final Set<String> keys = siteItems.keySet();
                            for (final String key : keys) {
                                pRequest.getSession().setAttribute(BBBCoreConstants.SITE, key);
                                if (siteItems.size() == 1) {
                                    if (!key.equalsIgnoreCase(getSiteContext().getSite().getId())
                                            && !getSiteContext().getSite().getId()
                                            .equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
                                        this.setShowMigratedUserPopupURL(true);
                                    }
                                }
                            }
                            //getLoginEmail() = loginEmail;
                            setLoginEmail(loginEmail);
                        }
                    }

                }
            }
        }

        this.logDebug("BBBProfileFormHandler.preRegister() method ends");

    }

    /** to call the preRegister() method for the email validation * UC_Create_Profile
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException
     * @throws IOException */

    public final boolean handleRegister(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.handleRegister() method started");

        final String loginEmail = (String) this.getValue().get(getPropertyManager().getEmailAddressPropertyName());
        this.preRegister(pRequest, pResponse);
        if (!isUserMigratedLoginProp() && this.migrationFlag) {
            if ((getLoginEmail() != null) && this.isShowMigratedUserPopupURL()) {
                this.errorMap.clear();
                this.getFormExceptions().clear();
                //this.mPreRegisterSuccessURL = getMigratedUserPopupURL();
                setPreRegisterSuccessURL(getMigratedUserPopupURL());
                pRequest.getSession().setAttribute(BBBCoreConstants.EMAIL_ADDR, getLoginEmail());
            }
        } else {

            if (getLoginEmail() != null) {
            	/*
            	 * this.mPreRegisterSuccessURL = getExtenstionSuccessURL()
                + java.net.URLEncoder.encode(getLoginEmail(), BBBCoreConstants.UTF_8).replace(
                        BBBCoreConstants.PLUS, BBBCoreConstants.PERCENT_TWENTY);
                 */
            	setPreRegisterSuccessURL(getExtenstionSuccessURL()
                        + java.net.URLEncoder.encode(getLoginEmail(), BBBCoreConstants.UTF_8).replace(
                                BBBCoreConstants.PLUS, BBBCoreConstants.PERCENT_TWENTY));
                
            } else if (this.errorMap.isEmpty()) {
            	getSessionBean().setUserEmailId(loginEmail);
            }
        }
        this.logDebug("BBBProfileFormHandler.handleRegister() method ends");

        return this.checkFormRedirect(this.getPreRegisterSuccessURL(), this.getPreRegisterErrorURL(), pRequest,
                pResponse);

    }
    /** This method is used to create a salt value
     * 
     * @param pEmail email - email is used to create secure id
     */
    public boolean createPasswordSalt(String pEmail){
   	 this.logDebug("BBBProfileFormHandler.createPasswordSalt() method started");
   	   boolean isPasswordCreated = false;
   	   String saltValue = null;	
		saltValue = mPBKDF2PasswordHasher.generateSalt();
			isPasswordCreated = mPBKDF2PasswordHasher.mPwdHashingService.createPasswordSalt(mPBKDF2PasswordHasher.mPwdHashingService.sha256(pEmail), saltValue , mPBKDF2PasswordHasher.getNumIterations());	
			   this.logDebug("BBBProfileFormHandler.createPasswordSalt() method end");
		return isPasswordCreated;
   }
   

    /** Wrapper method for create user
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public  boolean handleCreateUser(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.handleCreateUser() method started");
        TransactionManager tm = getTransactionManager();
        TransactionDemarcation td = getTransactionDemarcation();
        try {
          if (tm != null){
        	  td.begin(tm, TransactionDemarcation.REQUIRES_NEW);
          }	  
        return this.handleCreate(pRequest, pResponse);
        }
        catch (TransactionDemarcationException e) {
            throw new ServletException(e);
          }
        finally {
            endTransactionDemarcation(tm, td);
            this.logDebug("BBBProfileFormHandler.handleCreateUser() method ending::time["+new java.util.Date()+"]");
          }
    }


	/**
	 * @param tm
	 * @param td
	 * @throws ServletException
	 */
	protected void endTransactionDemarcation(TransactionManager tm,
			TransactionDemarcation td) throws ServletException {
		try {
		if (tm != null){
			td.end();
		}
		}
		catch (TransactionDemarcationException e) {
			throw new ServletException(e);
		}
	}

    /** Wrapper method for login user
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleLoginUser(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    	if(!indirectRequest){
    		this.setLoginErrorURL(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT);
    	}
    	String isFromsinglePageCheckout = null!=this.getValue().get("SinglePageCheckout") ? (String)this.getValue().get("SinglePageCheckout") : "false";
    	BBBSessionBean sessionBean = ((BBBSessionBean) (pRequest.resolveName(BBBCoreConstants.SESSION_BEAN)));
    	if(getSessionBean().isFrmWalletRegPage()){
    		setLoginSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get("walletOffersPage"));
    		getSessionBean().setFrmWalletRegPage(false);
    	}else if(getSessionBean().isPwdReqByChallengeQ() && !BBBUtility.isEmpty(sessionBean.getPwdReqFrmPageName())
    			&& !BBBUtility.isEmpty(getSuccessUrlMap().get(sessionBean.getPwdReqFrmPageName())))
    	{
    		setLoginSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(sessionBean.getPwdReqFrmPageName()));
    		getSessionBean().setPwdReqByChallengeQ(false);
    		sessionBean.setPwdReqFrmPageName(null);
    	}
    	boolean singlePageCheckout = (!BBBUtility.isEmpty(isFromsinglePageCheckout) && BBBCoreConstants.TRUE.equalsIgnoreCase(isFromsinglePageCheckout)) ? true : false;
        this.logDebug("BBBProfileFormHandler.handleLoginUser() method started");
        if (BBBCoreConstants.REC_USER_CHECKOUT.equals(pRequest.getSession().getAttribute(BBBCoreConstants.REC_USER_CHECKOUT))) {
        	setLoginErrorURL(getLoginErrorURL() + BBBCoreConstants.CHECKOUT_TRUE);
		}        
        
        // if mobile recognized user is trying to login with different email ID, do not perform cart merge.
        String recogMobileUserLogin = pRequest.getParameter(BBBCoreConstants.RECOG_MOBILE_USER_LOGIN);
        if(null != recogMobileUserLogin && BBBCoreConstants.TRUE.equalsIgnoreCase(recogMobileUserLogin)){
			setUserCheckingOut(BBBCoreConstants.USER_CHECKING_OUT);
        }
        
        boolean login = super.handleLogin(pRequest, pResponse);
        
        if(!login && isMigratedAccount() && !org.apache.commons.lang.StringUtils.equalsIgnoreCase(this.getLoginErrorURL(),BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)){
        	this.postLoginUser(pRequest, pResponse);
        	// Check to make sure our swap or postLogin didn't cause any form errors
        	int status = checkFormError(getLoginErrorURL(), pRequest, pResponse);
            if (status != STATUS_SUCCESS) 
              return status == STATUS_ERROR_STAY;
        	if (!checkFormSuccess(getLoginSuccessURL(), pRequest, pResponse))
                return false;
        	return true;
        }
        sessionBean = ((BBBSessionBean) (pRequest.resolveName(BBBCoreConstants.SESSION_BEAN)));
        sessionBean.setSinglePageCheckout(singlePageCheckout);
        return login;
    }

    public void setSuccessURL(DynamoHttpServletRequest pRequest,String key){
    	setLoginSuccessURL(pRequest.getContextPath()+ getSuccessUrlMap().get(key));
    }
    
    public void setErrorURL(DynamoHttpServletRequest pRequest,String key){
    	setLoginErrorURL(pRequest.getContextPath()+ getErrorUrlMap().get(key));
    }
    
    /** wrapper handler method for review page login
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleReviewPageLogin(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleReviewPageLogin() method started");
    
    	indirectRequest = true;
    	return handleLoginUser(pRequest,pResponse);

    }
    
    /** wrapper handler method for login
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleCheckoutPageLogin(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleCheckoutPageLogin() method started");
    	String key = "checkoutPageLogin";
    	setUserCheckingOut("userCheckingOut");
    	setSuccessURL(pRequest,key);
    	setErrorURL(pRequest,key);
    	indirectRequest = true;
    	return handleLoginUser(pRequest,pResponse);

    }

    public final boolean handleSpCheckoutLogin(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleCheckoutPageLogin() method started");
    	String key = "spCheckoutLogin";
    	setUserCheckingOut("userCheckingOut");
    	setSuccessURL(pRequest,key);
    	setErrorURL(pRequest,key);
    	indirectRequest = true;
    	return handleLoginUser(pRequest,pResponse);

    }
    
	/**
	 * This method is used to swap the profile for recognized user.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean handleRecUserLogin(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		this.logDebug("BBBProfileFormHandler.handleRecUserLogin() method started");
		String key = "recUserLogin";
		setUserCheckingOut("userCheckingOut");
		setLoginSuccessURL((String) pRequest.getSession().getAttribute(BBBCoreConstants.REDIRECT_URL));
		setErrorURL(pRequest, key);
		indirectRequest = true;
		pRequest.setParameter(BBBCoreConstants.SITE_ID, SiteContextManager.getCurrentSiteId());
		// User May be - as guest/same/different
		/**
		 * For same user, we will have to validate resolved profile's email id
		 * and entered email in the form.
		 */
		final Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		final Integer loggedInSecurityStatus = (Integer) profile.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS);
		final String emailProperty = getPropertyManager().getEmailAddressPropertyName();
		final String recUserEmail = (String) profile.getPropertyValue(emailProperty);
		String loginEmail = this.getStringValueProperty(BBBCoreConstants.EMAIL);
		boolean doLogout = true;
		boolean status = false;
		this.mTools = (BBBProfileTools) this.getProfileTools();
		PropertyManager pmgr = mTools.getPropertyManager();
		String loginPropertyName = pmgr.getLoginPropertyName();
		if (null == loginEmail) {
			loginEmail = getStringValueProperty(loginPropertyName);
		}

		if (!StringUtils.isEmpty(recUserEmail) && !StringUtils.isEmpty(loginEmail) && loginEmail.equalsIgnoreCase(recUserEmail)) {
			this.logDebug("BBBProfileFormHandler.handleRecUserLogin(): recognized user logging-in");
			doLogout = false;
		}

		if (COOKIE_LOGIN_SECURITY_STATUS.equals(loggedInSecurityStatus) && doLogout) {
			try {
				ProfileFormHandler profileFormHandler = (ProfileFormHandler) pRequest.resolveName(BBBCoreConstants.PROFILE_FORM_HANDLER);
				((BBBProfileFormHandler) profileFormHandler).handleRefreshUserProfile(pRequest, pResponse);
			} catch (ServletException se) {
				this.logError("Error during logout of recognized user: " + se);
				throw new ServletException(se);
			} catch (IOException ie) {
				this.logError("Error during logout of recognized user: " + ie);
				throw new IOException(ie);
			} catch (Exception e) {
				this.logError("Error during logout of recognized user: " + e);
			}
		}
		if (!doLogout) {
			// If recognized user logs in then just confirm the password and
			// proceed.
			status = this.handleConfirmPassword(pRequest, pResponse);
		} else {
			reloadRegistrySummaryVO(pRequest, pResponse);
			status = handleLoginUser(pRequest, pResponse);
		}
		pRequest.getSession().removeAttribute(BBBCoreConstants.REC_USER_CHECKOUT);
		this.logDebug("END BBBProfileFormHandler.handleRecUserLogin() with status: " + status  + " and doLogout for profile is " + doLogout);
		return status;
	}

    /**
     * This method is used to reload the RegistrySummaryVO for registry flyout on desktop
     * @param pRequest
     * @param pResponse
     */
    private void reloadRegistrySummaryVO(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    	logDebug("BBBProfileFormHandler.reloadRegistrySummaryVO started...");
    	List<String> userRegList = new ArrayList<String>();
		List<String> userActiveRegList = new ArrayList<String>();
		RegistrySummaryVO pRegSummaryVO = null;
    	BBBSessionBean sessionBean = ((BBBSessionBean) (pRequest.resolveName(BBBCoreConstants.SESSION_BEAN)));
		final HashMap sessionMap = sessionBean.getValues();
		pRegSummaryVO = (RegistrySummaryVO) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY);
		userRegList = (List) sessionMap.get(BBBGiftRegistryConstants.USER_REGISTRIES_LIST);
		userActiveRegList = (List) sessionMap.get(BBBGiftRegistryConstants.USER_ACTIVE_REGISTRIES_LIST);
		if(pRegSummaryVO != null) {
			sessionMap.put(BBBGiftRegistryConstants.USER_REGISTRIES_SUMMARY, null);
		}
		if(userRegList != null && !userRegList.isEmpty()) {
			userRegList.clear();
		}
		if(userActiveRegList != null && !userActiveRegList.isEmpty()) {
			userActiveRegList.clear();
		}
		logDebug("BBBProfileFormHandler.reloadRegistrySummaryVO end.");
	}

	/** wrapper handler method for login
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleWishListlogin(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleWishListlogin() method started");
    	String key = "wishlistLogin";
    	setSuccessURL(pRequest,key);
    	setErrorURL(pRequest,key);
    	indirectRequest = true;
    	return handleLoginUser(pRequest,pResponse);
        
    }
    
    /** wrapper handler method for login
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleCheckoutUserLogin(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleCheckoutUserLogin() method started");
    	String key = "checkoutUserLogin";
    	setSuccessURL(pRequest,key);
    	setErrorURL(pRequest,key);
    	indirectRequest = true;
    	return handleLoginUser(pRequest,pResponse);
        
    }
    
    
    /** wrapper handler method for login 
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleExtendedLogin(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleExtendedLogin() method started");
    	String key = "extendedLogin";
    	setSuccessURL(pRequest,key);
    	indirectRequest = true;
    	return handleLoginUser(pRequest,pResponse);
        
        
    }
    
    /** wrapper handler method for login 
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleRegularLogin(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleRegularLogin() method started");
    	String key = "regularLogin";
    	setSuccessURL(pRequest,key);
    	setErrorURL(pRequest,key);
    	indirectRequest = true;
    	return handleLoginUser(pRequest,pResponse);
        
    }
    
    /** wrapper handler method for login 
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleChangePwdLogin(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleChangePwdLogin() method started");
    	String key = "changePwdLogin";
    	setSuccessURL(pRequest,key);
    	setErrorURL(pRequest,key);
    	indirectRequest = true;
    	return handleLoginUser(pRequest,pResponse);
    }
    
    /** wrapper handler method for login 
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleIncorrectPasswordLogin(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleIncorrectPasswordLogin() method started");
    	String key = "incorrectPasswordLogin";
    	setSuccessURL(pRequest,key);
    	setErrorURL(pRequest,key);
    	indirectRequest = true;
    	return handleLoginUser(pRequest,pResponse);
    }

    /** wrapper handler method for login 
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleCheckoutLogin(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleCheckoutLogin() method started");
    	String key = "checkoutLogin";
    	setSuccessURL(pRequest,key);
    	setErrorURL(pRequest,key);
    	indirectRequest = true;
    	return handleLoginUser(pRequest,pResponse);
        
    }
    
    /** wrapper handler method for register 
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleCheckoutLoginRegister(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleCheckoutLoginRegister() method started");
    	String key = "checkoutLoginRegister";
    	setSuccessURL(pRequest,key);
    	setErrorURL(pRequest,key);
    	return handleRegister(pRequest,pResponse);
        
    }
    /** wrapper handler method for register 
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleLoginRegister(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleLoginRegister() method started");
    	String key = "loginRegister";
    	setCreateSuccessURL(pRequest.getContextPath()+getSuccessUrlMap().get(key));
        setCreateErrorURL(pRequest.getContextPath()+getErrorUrlMap().get(key));
    	return handleRegister(pRequest,pResponse);
        
    }
    /** wrapper handler method for register
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleCreateUserFragRegister(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleCreateUserFragRegister() method started");
    	String key = "createUserFragRegister";
    	setPreRegisterSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(key));
    	setPreRegisterErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(key));
    	// BBBH-391 | Client DOM XSRF changes
    		setExtenstionSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(BBBURLConstants.EXTENSION));
    		setMigratedUserPopupURL(pRequest.getContextPath() + getSuccessUrlMap().get(BBBURLConstants.MIGRATED));
    	
    	
    	return handleRegister(pRequest,pResponse);
        
    }
    
    /** wrapper handler method for login 
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleTrackOrderLogin(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    	this.logDebug("BBBProfileFormHandler.handleTrackOrderLogin() method started");
    	String key = "trackOrderLogin";
    	setSuccessURL(pRequest,key);
    	setErrorURL(pRequest,key);
    	indirectRequest = true;
    	return handleLoginUser(pRequest,pResponse);
    }
    
    /** wrapper handler method for update profile
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleUpdate(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleUpdate() method started");
    	String key = "update";
    	
    	//BBBP-9620 ||DSK | Getting system error on moving LTL item to registry thorough alternate ph modal
    	if(!(BBBCoreConstants.TRUE.equals(pRequest.getParameter(BBBCoreConstants.IS_FROM_GIFT_REGISTRY))))
    	 {
    		if(BBBCoreConstants.DEFAULT_CHANNEL_VALUE.equalsIgnoreCase(BBBUtility.getChannel()) && !isShallowProfileUpdateRequest()) {
    			setUpdateSuccessURL(pRequest.getContextPath()+getSuccessUrlMap().get(key));
    			setUpdateErrorURL(pRequest.getContextPath()+getErrorUrlMap().get(key));
    		}
    	 }
    	final String emailProperty = getPropertyManager().getEmailAddressPropertyName();
     	 // added for profile id change break
         String databaseEmail = (String)getProfile().getPropertyValue(emailProperty);
         pRequest.setAttribute("databaseEmail", databaseEmail);
       // String cuurentEmail =  getLoginEmail();
		final String email = this.getStringValueProperty(emailProperty);
		this.preUpdateUser(pRequest, pResponse);
		if (email != null && databaseEmail != null
				&& !databaseEmail.equalsIgnoreCase(email)
				&& !this.isPersonalInfoValidated()) {
         	 RepositoryItem repoItem = PBKDF2PasswordHasher.mPwdHashingService.getPasswordSalt(PBKDF2PasswordHasher.mPwdHashingService.sha256(databaseEmail));
         	 if(repoItem!=null){
         		String newKey =  PBKDF2PasswordHasher.mPwdHashingService.sha256(email);
 				  String oldkey = (String)repoItem.getPropertyValue("secId");
 				  String saltValue = (String)repoItem.getPropertyValue("value");
 				  try {
					PBKDF2PasswordHasher.mPwdHashingService.updatePasswordSalt(oldkey, newKey, saltValue ,mPBKDF2PasswordHasher.getNumIterations());
				} catch (Exception e) {
					logError(e.getMessage(),e);
				}
 				  logDebug("updated latest login property in salt table");
 			      }
        }
		
    	return super.handleUpdate(pRequest, pResponse);
        
    }
    
    /** This method is overridden from BBBProfileFormHandler. Performs the following 1. converts login-id(email) to lower
     * case 2. calls handleCreateUser for ProfileFormHandler class
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */

    @Override
    public  boolean handleCreate(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.handleCreate() method started");

        @SuppressWarnings ("unchecked")
        final Dictionary<String, String> value = this.getValue();

        // Getting email string and putting in login with case changed to lower
        // case
       
        final String loginEmail = value.get(getPropertyManager().getEmailAddressPropertyName());
        if (StringUtils.isEmpty(loginEmail)) {
            processEmptyProfileEmail();
        } else {
            value.put(getPropertyManager().getLoginPropertyName(), loginEmail.toLowerCase());
            value.put(getPropertyManager().getEmailAddressPropertyName(), loginEmail.toLowerCase());
            value.put(getPropertyManager().getAutoLoginPropertyName(), BBBCoreConstants.TRUE);
            this.setValueProperty(getPropertyManager().getReceiveEmailPropertyName(), BBBCoreConstants.YES);
            if(isShallowProfileChanges()){
            	value.put(getPropertyManager().getStatusPropertyName() , BBBCoreConstants.FULL_PROFILE_STATUS_VALUE);        	
            }

        }

        //checking for third party integrations.
        String isFromThirdParty = pRequest.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST);
        if(!BBBUtility.isEmpty(isFromThirdParty)){
            value.put(getPropertyManager().getmSourcePropertyName() , pRequest.getHeader(BBBCoreConstants.CLIENT_ID_PARM));
            logDebug(getPropertyManager().getmSourcePropertyName() + " is set to " + pRequest.getHeader(BBBCoreConstants.CLIENT_ID_PARM));
        }

       if(!StringUtils.isEmpty(loginEmail) && ((BBBProfileTools)getProfileTools()).isProfileStatusShallow(loginEmail.toLowerCase())){
        	return  updateShallowAccount(pRequest, pResponse, loginEmail.toLowerCase());
        }else{
        // Creating Salt Value
            if(!StringUtils.isEmpty(loginEmail)){
        	if(!createPasswordSalt(loginEmail.toLowerCase())){
        		logDebug("fail to create salt in HandleCreate Method");
        	}
        }
        return super.handleCreate(pRequest, pResponse);

    }
    }
    
    //if(isLoggingDebug())logDebug("Is Profile Status Shallow?: "); 
    
    //dkhadka: Shallow Profile Registration begins from here!!!
    /*
     * 1.  	handleCreateUserFragRegister
     * 2.	handleCreateAcct
     * 3.	handleCreate
     * 2.	handleRegister
     * 3.	preRegister
     * 4.	And goes to create_account.jsp
     * 
     */       
	private boolean updateShallowAccount(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse,String loginEmail) throws ServletException, IOException{
    	if(isLoggingDebug()) {
    		logDebug("BBBProfileFormHandler.updateShallowAccount() method started.");
    	}
    	 
    	RepositoryItem profileItem=  getProfileTools().getItemFromEmail(loginEmail);   
    	if(profileItem!=null){        	
    		RepositoryItem oldProfile = this.getProfile().getDataSource();
    		super.setRepositoryId(profileItem.getRepositoryId());
    		RepositoryItem profile = this.getProfile().getDataSource();
    		try {
				super.copyPropertiesOnLogin(oldProfile, profile);
			} catch (RepositoryException e) {
				logError("Error occurred while copying properties from oldProfile", e);
			}
    		setUpdateRepositoryId(getRepositoryId());

    		if(isLoggingDebug()) {
    			logDebug("Flow :: Updating shallow profile:" + profileItem);	
    		}
    		logDebug("Create Success URL is " + getCreateSuccessURL());
    		logDebug("Create Error URL is " + getCreateErrorURL());
    		setUpdateSuccessURL(getCreateSuccessURL());
    		setUpdateErrorURL(getCreateErrorURL());
    		setShallowProfileUpdateRequest(true);  				
    		getSessionBean().setPreSelectedAddress(true);
    		handleUpdate(pRequest, pResponse);
    		boolean copyOrderDetailsFlag = Boolean.valueOf(pRequest.getParameter(COPY_ORDER_DETAILS));
    		if (getBBBOrder() != null && copyOrderDetailsFlag) {
    			getClientLockManager();
    			boolean acquireLock = false;
    			try {
    				acquireLock = !getClientLockManager().hasWriteLock(getProfile().getRepositoryId(), Thread.currentThread());
    				if (acquireLock) {
    					getClientLockManager().acquireWriteLock(getProfile().getRepositoryId(), Thread.currentThread());
    				}
    				TransactionDemarcation td = new TransactionDemarcation();
    				td.begin(getTransactionManager());
    				boolean shouldRollback = true;
    				try {
    					synchronized (getBBBOrder()) {
    						getProfileManager().checkoutRegistration(getBBBOrder(), this.getProfile(), getSiteContext()
    								.getSite().getId(),getSessionBean().isPreSelectedAddress());
    						shouldRollback = false;
    					}
    				} catch (BBBSystemException systemException) {
    					this.logError(LogMessageFormatter.formatMessage(pRequest,
    							BBBCoreConstants.ERR_CC_SYS_EXCEPTION, BBBCoreErrorConstants.ACCOUNT_ERROR_1119),
    							systemException);
    					final String registerationError = getMessageHandler().getErrMsg(
    							BBBCoreConstants.ERR_CC_SYS_EXCEPTION, pRequest.getLocale().getLanguage(), null,
    							null);
    					this.addFormException(new DropletException(registerationError,
    							BBBCoreErrorConstants.ACCOUNT_ERROR_1303));
    					shouldRollback = true;
    				} catch (final BBBBusinessException businessException) {
                            this.logError(LogMessageFormatter.formatMessage(pRequest,
                                    BBBCoreConstants.ERR_CC_BSYS_EXCEPTION, BBBCoreErrorConstants.ACCOUNT_ERROR_1120),
                                    businessException);
                            final String registerationError = getMessageHandler().getErrMsg(
                                    BBBCoreConstants.ERR_CC_BSYS_EXCEPTION, pRequest.getLocale().getLanguage(), null,
                                    null);
                            this.addFormException(new DropletException(registerationError,
                                    BBBCoreErrorConstants.ACCOUNT_ERROR_1304));
                            this.errorMap.put(BBBCoreConstants.REGISTER_ERROR, registerationError);
                            shouldRollback = true;
					} finally {
    					td.end(shouldRollback);
    				}
    			} catch (TransactionDemarcationException td) {
    				logError("Exception in BBBProfileFormHandler.updateShallowAccount" + td);
    			} catch (DeadlockException de) {
    				logError("Exception in BBBProfileFormHandler.updateShallowAccount" + de);
    			} finally {
    				releaseWriteLock(acquireLock);

    			}
    		}
    		setShallowProfileUpdateRequest(false);
    		getSessionBean().setPreSelectedAddress(false);


    		profileItem =  getProfileTools().getItemFromEmail(loginEmail);
    		logDebug("Registration Success URL is " + getRegistrationSuccessURL());
    		logDebug("Registration Error URL is " + getRegistrationErrorURL());
    		logDebug("Update Success URL is " + getUpdateSuccessURL());
    		logDebug("Update Error URL is " + getUpdateErrorURL());
    		logDebug("Form Exceptions that occured are " + this.getFormExceptions());
    		if(isLoggingDebug())logDebug("Successfully updated shallow profile for email  " +loginEmail+" and item is "+ profileItem);  
    		return checkFormRedirect(getUpdateSuccessURL(), getUpdateErrorURL(), pRequest, pResponse);  				  				  		        	
    	}         
         return false;
    }


	/**
	 * @param acquireLock
	 * @throws ServletException
	 */
	protected void releaseWriteLock(boolean acquireLock)
			throws ServletException {
		try {
			if (acquireLock) {
				getClientLockManager().releaseWriteLock(getProfile().getRepositoryId(), Thread.currentThread());
			}
		} catch (LockManagerException lm) {
			logError("Error Ocuccrened", lm);
			throw new ServletException(lm.getMessage());
		}
	}
	
    /** wrapper handler method for create account
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleCreateAccOrderReg(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleCreateOrderReg() method started");
    	 String key = "createAccOrderReg";
         setCreateSuccessURL(pRequest.getContextPath() +getSuccessUrlMap().get(key));
         setCreateErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(key));
    	return handleCreate(pRequest,pResponse);
        
    }
    
    /** wrapper handler method for create account
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleCreateAcct(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleCreateAcct() method started");
    	 String key = "createAcct";
    	 String load_fb_info = pRequest.getParameter(BBBCoreConstants.LOAD_FB_INFO);
    	 String memberId = pRequest.getParameter(BBBCoreConstants.MEMBER_ID);
    	 if(getSessionBean().isFrmWalletRegPage()){
    		 setCreateSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get("walletOffersPage"));
    		 getSessionBean().setFrmWalletRegPage(false);
    	 } else if(BBBUtility.isNotEmpty(this.getWriteReviewSuccessURL())) {    		
    	     setCreateSuccessURL(StringEscapeUtils.unescapeXml(this.getWriteReviewSuccessURL()));    		
    	 } else {
         setCreateSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(key));
    	 }
         if(null != load_fb_info){
        	 setCreateErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(key) + BBBCoreConstants.LOAD_FB_INFO_ERROR_URL + memberId);
         }else{
        	 setCreateErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(key) + BBBCoreConstants.MEMBER_ID_ERROR_URL + memberId );
         }
        
    	return handleCreate(pRequest,pResponse);
        
    }
    
    /** wrapper handler method for create account
     * @param pRequest
     * @param pResponse
     * @return success/failure
     * @throws ServletException
     * @throws IOException
     */
    public final boolean handleCreateAcctMyReg(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	this.logDebug("BBBProfileFormHandler.handleCreateOrderReg() method started");
    	 String key = "createAcctMyReg";
    	 String load_fb_info = pRequest.getParameter("load_fb_info");
    	 String memberId = pRequest.getParameter("memberId");
         setCreateSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(key));
         if(null != load_fb_info){
        	 setCreateErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(key) + "load_fb_info=true&memberId=" + memberId);
         }else{
        	 setCreateErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(key) + "memberId=" + memberId );
         }    	
         return handleCreate(pRequest,pResponse);
        
    }
    
    /** This method is overridden from BBBProfileFormHandler. Performs the following 1. converts login-id(email) to lower
     * case 2. calls handleCreateUser for ProfileFormHandler class
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */

    public final boolean handlePlaceOrder(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        
    	this.logDebug("BBBProfileFormHandler.handlePlaceOrder() method started");
    	String key = "placeOrder";
    	setCreateSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(key));
    	setCreateErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(key));
        // preCreateUser(pRequest, pResponse);
        // super.handleCreate(pRequest, pResponse);
        getPropertyManager().setEmailAddressPropertyName(getPropertyManager().getEmailAddressPropertyName());
        getPropertyManager().setFirstNamePropertyName(getPropertyManager().getFirstNamePropertyName());
        getPropertyManager().setLastNamePropertyName(getPropertyManager().getLastNamePropertyName());
        // postCreateUser(pRequest, pResponse);

        this.logDebug("BBBProfileFormHandler.handlePlaceOrder() method ends");

        return this.checkFormRedirect(this.getCreateSuccessURL(), this.getCreateErrorURL(), pRequest, pResponse);
    }

    /** Override method from ATG OOTB and does the validation for email and password.
     * 1. If mAssoSite is not null then current profile 
     *     when user logged in is extended to the current site. 
     * 2. In case if the user is the valid legacy user, 
     * 	   he should be redirected to the account registration page 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException if there was an error while executing the code
     * @throws IOException if there was an error with servlet IO */
    @Override
    public void preLoginUser(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        final String methodName = BBBCoreConstants.PRE_LOGIN_USER;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.SIGNIN_PRE_LOGIN, methodName);
        /* BBB-2136 | DSK-Extend Modal Account Functionality for Recognized User & MPC */
		if (StringUtils.isNotEmpty(getSessionBean().getFromPage())) {
			
			StringBuilder appendData = new StringBuilder(BBBCoreConstants.BLANK);
			if (StringUtils.isNotEmpty(getSessionBean().getQueryParam())) {
				appendData.append(BBBCoreConstants.QUESTION_MARK);
				appendData.append(URLDecoder.decode(getSessionBean().getQueryParam(), BBBCoreConstants.UTF_ENCODING));
			}
			StringBuilder redirectURL = new StringBuilder(BBBCoreConstants.BLANK);
			redirectURL.append(getSuccessUrlMap().get(getSessionBean().getFromPage())).append(appendData);
			setRedirectPage(pRequest.getContextPath()+ redirectURL.toString());
			getSessionBean().setFromPage(BBBCoreConstants.BLANK);
			getSessionBean().setQueryParam(BBBCoreConstants.BLANK);
		}
        if(pRequest.getContextPath().equalsIgnoreCase(BBBCoreConstants.CONTEXT_TBS)){
        	preTBSLoginUser(pRequest);
        }
        
        super.preLoginUser(pRequest, pResponse);
        try {
            this.logDebug("BBBProfileFormHandler.preLoginUser() method started");
            final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
            @SuppressWarnings ("unchecked")
            final Dictionary<String, String> value = this.getValue();
            this.mTools = (BBBProfileTools) this.getProfileTools();
			//changes starts for defect BBBSL-7637
            Order order = this.getOrder();
            if(null != order){
	            final List<RepositoryItem> srcAppliedCoupons = ((BBBPromotionTools)getPromotionTools()).getCouponListFromOrder(order);
	            setSrcAppliedCoupons(srcAppliedCoupons);           
            }
            //changes ends for defect BBBSL-7637
            
            String loginEmail = this.getStringValueProperty(BBBCoreConstants.LOGIN);
            String password = this.getStringValueProperty(BBBCoreConstants.PASSWORD);
            RepositoryItem profileItem = null;
            
            populateRememberMe();
            setLoginEmail(loginEmail);
          if (!BBBUtility.isEmpty(loginEmail)) {
    		    loginEmail = loginEmail.toLowerCase();
    		    value.put(getPropertyManager().getLoginPropertyName(), loginEmail);
    		}
            if (!BBBUtility.isEmpty(password)) {
    		    password = password.trim();
    		    value.put(getPropertyManager().getPasswordPropertyName(), password);
    		}
            this.errorMap = new HashMap<String, String>();
            profileItem = processLoginEmail(pRequest, errorPlaceHolderMap, value, loginEmail, password, profileItem);
            processPasswordPreLogin(pRequest, errorPlaceHolderMap, loginEmail, password, profileItem);
            restrictCartMerge(pRequest);
            this.logDebug("BBBProfileFormHandler.preLoginUser() method Ends");
            /**
        	 * code for incorrect legacy password json - for the JS error message / url redirection
        	 */
            if (this.getFormError()) {
    		    this.setFormErrorVal(true);
    		} else {
    		    this.setFormErrorVal(false);
    		}
           } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.SIGNIN_PRE_LOGIN, methodName);
        }
    }

	private void processPasswordPreLogin(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap, String loginEmail, String password,
			RepositoryItem profileItem) {
		
		processEmptyInvalidPassword(pRequest, errorPlaceHolderMap, password);
		processPasswordErrorsPreLogin(pRequest, errorPlaceHolderMap, loginEmail, profileItem);
	}


	private RepositoryItem processLoginEmail(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap, final Dictionary<String, String> value, String loginEmail,
			String password, RepositoryItem profileItem) {
		if (BBBUtility.isEmpty(loginEmail)) {
		    processEmptyLoginEmail(pRequest, errorPlaceHolderMap);
		} else if (!BBBUtility.isValidEmail(loginEmail)) {
		    processInvalidEmailLogin(pRequest, errorPlaceHolderMap);
		} else if (this.errorMap.isEmpty()) {
		    profileItem = this.mTools.getItemFromEmail(loginEmail);
		    processRecognizedUser(pRequest, errorPlaceHolderMap, value, loginEmail, password, profileItem); 
		    }
		return profileItem;
	}


	private void processEmptyLoginEmail(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap) {
		final String loginEmptyError = getMessageHandler().getErrMsg(
		        BBBCoreConstants.ERR_LOGIN_EMPTY_ERROR, pRequest.getLocale().getLanguage(),
		        errorPlaceHolderMap, null);
		this.addFormException(new DropletException(loginEmptyError, BBBCoreConstants.ERR_LOGIN_EMPTY_ERROR));
		this.errorMap.put(BBBCoreConstants.LOGIN_PASSWORD_ERROR, loginEmptyError);
	}


	private void processInvalidEmailLogin(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap) {
		final String loginEmailFormatError = getMessageHandler().getErrMsg(
		        BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR, pRequest.getLocale().getLanguage(),
		        errorPlaceHolderMap, null);
		this.addFormException(new DropletException(loginEmailFormatError,
		        BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR));
		this.errorMap.put(BBBCoreConstants.LOGIN_PASSWORD_ERROR, loginEmailFormatError);
	}

   /**
	 * This is set to restrict merging of cart if user logged in checkout flow
	 */
	private void restrictCartMerge(final DynamoHttpServletRequest pRequest) {
		if (this.getUserCheckingOut() != null) {
		    pRequest.setParameter("userCheckingOut", "userCheckingOut");
		    pRequest.getSession().setAttribute("userCheckingOut", TRUE);
		}
	}

    
	/**
	 * Recognized user flow - PreLogin
	 * 
	 */
	private void processRecognizedUser(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap, final Dictionary<String, String> value, String loginEmail,
			String password, RepositoryItem profileItem) {
		if ((profileItem != null) && this.mTools.isDuplicateEmailAddress(loginEmail, getSiteContext().getSite().getId())) {
			//Recognized user flow
			setRememberMePreLogin(pRequest);			
			addAutoLoginCookiePreUserLogin(pRequest, profileItem);			
			populateProfileFlags(profileItem);		    
		    validateAddSessionDataPreLogin(pRequest, errorPlaceHolderMap, value, loginEmail, password);
		    processMigration(pRequest, errorPlaceHolderMap, loginEmail, password, profileItem);
		 }
	}


	private void setRememberMePreLogin(final DynamoHttpServletRequest pRequest) {
		if (!pRequest.getContextPath().equalsIgnoreCase(BBBCoreConstants.CONTEXT_TBS)) {
			this.setRememberMe(true);
		}
	}

	/**
	 * R 2.2 Start : Added for Cookie Auto Login for Mobile App
	 */
	private void populateRememberMe() {
		String rememberMe = this.getStringValueProperty(BBBCoreConstants.REMEMBER_ME);
		if(rememberMe != null && rememberMe.equalsIgnoreCase(BBBCoreConstants.TRUE)){
			setRememberMe(true);
		}
	}

	private void processPasswordErrorsPreLogin(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap, String loginEmail, RepositoryItem profileItem) {
		if (this.errorMap.isEmpty()
		        && (!this.mTools.isDuplicateEmailAddress(loginEmail, getSiteContext().getSite().getId()))
		        && (getAssoSite() == null) && (getSessionBean().getLegacyEmailId() == null)) {
		    processInvalidEmailLogin(pRequest, errorPlaceHolderMap);

		} else if (this.errorMap.isEmpty() && (getAssoSite() != null)) {

			final RepositoryItem item = this.mTools.getItemFromEmail(loginEmail);
			if (item == null) {
				processNoProfileError(pRequest, errorPlaceHolderMap);
			}
			if ((item != null) && this.mTools.isAccountLocked(item)) {
				processLockedValidProfilePreLogin(pRequest, errorPlaceHolderMap);
			}
		   
		} else {
		    if (this.errorMap.isEmpty() && (profileItem != null) && this.mTools.isAccountLocked(profileItem)) {
		        processLockedValidProfilePreLogin(pRequest, errorPlaceHolderMap);
		    }
		}
	}


	private void processNoProfileError(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap) {
		final String noProfileExist = getMessageHandler().getErrMsg(
		        BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR, pRequest.getLocale().getLanguage(),
		        errorPlaceHolderMap, null);
		this.errorMap.put(BBBCoreConstants.LOGIN_PASSWORD_ERROR, noProfileExist);
		this.addFormException(new DropletException(noProfileExist,
		        BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR));
		this.errorMap.put(BBBCoreConstants.LOGIN_PASSWORD_ERROR, noProfileExist);
	}


	private void processEmptyInvalidPassword(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap, String password) {
		
		if (this.errorMap.isEmpty() && BBBUtility.isEmpty(password)) {
		    final String passwordEmptyError = getMessageHandler().getErrMsg(
		            BBBCoreConstants.ERR_PASSWORD_EMPTY_ERROR, pRequest.getLocale().getLanguage(),
		            errorPlaceHolderMap, null);
		    this.addFormException(new DropletException(passwordEmptyError,
		            BBBCoreConstants.ERR_PASSWORD_EMPTY_ERROR));
		    this.errorMap.put(BBBCoreConstants.LOGIN_PASSWORD_ERROR, passwordEmptyError);
		} else if (this.errorMap.isEmpty() && !this.migrationFlag
		        && !BBBUtility.isStringLengthValid(password, BBBCoreConstants.ONE, BBBCoreConstants.TWENTY)) {
		    processInvalidEmailLogin(pRequest, errorPlaceHolderMap);
		}
	}


	private void processMigration(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap, String loginEmail, String password,
			RepositoryItem profileItem) {
		if (this.migrationFlag && !isUserMigratedLoginProp()) {
		    boolean success = false;
		    try {
		        success = getProfileManager().isOldAccountValid(loginEmail, getSiteContext().getSite()
		                .getId(), password);
		    } catch (final BBBBusinessException e) {
		        this.logError(LogMessageFormatter.formatMessage(null,
		                "BBBBusinessException in BBBProfileFormHandler while calling isOldAccountValid",
		                BBBCoreErrorConstants.ACCOUNT_ERROR_1160), e);
		    } catch (final BBBSystemException e) {
		        this.logError(LogMessageFormatter.formatMessage(null,
		                "BBBSystemException in BBBProfileFormHandler while calling isOldAccountValid",
		                BBBCoreErrorConstants.ACCOUNT_ERROR_1160), e);
		    }

		    this.setMigratedAccount(success);
		    if (success) {
		    	if(!BBBCoreConstants.ATG_REST_IGNORE_REDIRECT.equalsIgnoreCase(this.getLoginErrorURL())){
		    		pRequest.getSession().setAttribute(BBBCoreConstants.EMAIL_ADDR, loginEmail);
		    		pRequest.getSession().setAttribute(OLD_PWD, password);
		            pRequest.getSession().setAttribute("fName",
		                    profileItem.getPropertyValue(getPropertyManager().getFirstNamePropertyName()));
		            pRequest.getSession().setAttribute("lName",
		                    profileItem.getPropertyValue(getPropertyManager().getLastNamePropertyName()));
		    	}else{
		    		getProfile().setPropertyValue(BBBCoreConstants.EMAIL, loginEmail);
		    		getProfile().setPropertyValue("firstName", profileItem.getPropertyValue(getPropertyManager().getFirstNamePropertyName()));
					getProfile().setPropertyValue("lastName", profileItem.getPropertyValue(getPropertyManager().getLastNamePropertyName()));
					this.addFormException(new DropletException(getMessageHandler().getErrMsg(
		                    BBBCoreConstants.ERR_CHANGE_PASSWORD_NOTIFICATION_LEGACY, pRequest.getLocale().getLanguage(),null), "330"));
		    	}
		        
		    } else {
		        final String passwordLengthError = getMessageHandler().getErrMsg(
		                BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR, pRequest.getLocale().getLanguage(),
		                errorPlaceHolderMap, null);
		        this.addFormException(new DropletException(passwordLengthError));
		        this.errorMap.put(BBBCoreConstants.LOGIN_PASSWORD_ERROR, passwordLengthError);
		    }
		    //getLegacyUser() = BBBCoreConstants.YES;
		    setLegacyUser(BBBCoreConstants.YES);
		    pRequest.getSession().setAttribute(BBBCoreConstants.EMAIL_ADDR, loginEmail);
		}
	}

	/**
	 * BBBSL-1017 if user is coming from forgot password, then validate the
	 * profile and add some attributes in session so that we can read them on
	 * JSP
	 */

	private void validateAddSessionDataPreLogin(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap, final Dictionary<String, String> value, String loginEmail,
			String password) {
		if (this.resetPasswordFlag) {
		    final RepositoryItem validProfile = this.mTools.getItem(
		            value.get(getPropertyManager().getLoginPropertyName()),
		            value.get(getPropertyManager().getPasswordPropertyName()));
		    // addtional check is added for rest as rest flow was breaking
			if (null != validProfile) {
				if (!BBBCoreConstants.ATG_REST_IGNORE_REDIRECT.equalsIgnoreCase(this.getLoginErrorURL())) {
					if (this.mTools.isAccountLocked(validProfile)) {
						processLockedValidProfilePreLogin(pRequest, errorPlaceHolderMap);
					} else {
						populateSessionPreLoginOnError(pRequest, loginEmail, password, validProfile);
					}
				} else {
					if (this.mTools.isAccountLocked(validProfile)) {
						processLockedValidProfilePreLogin(pRequest, errorPlaceHolderMap);
					} else {
						populateSessionAndProfilePreLogin(pRequest, loginEmail, password, validProfile);
					}
				}
			}
		}
	}


	private void populateSessionAndProfilePreLogin(final DynamoHttpServletRequest pRequest, String loginEmail,
			String password, final RepositoryItem validProfile) {
		
		pRequest.getSession().setAttribute(BBBCoreConstants.EMAIL_ADDR, loginEmail);
		pRequest.getSession().setAttribute(OLD_PWD, password);
		pRequest.getSession().setAttribute("fstName",
				validProfile.getPropertyValue(getPropertyManager().getFirstNamePropertyName()));
		pRequest.getSession().setAttribute("lstName",
				validProfile.getPropertyValue(getPropertyManager().getLastNamePropertyName()));
		getProfile().setPropertyValue(BBBCoreConstants.EMAIL, loginEmail);
		getProfile().setPropertyValue("firstName",
				validProfile.getPropertyValue(getPropertyManager().getFirstNamePropertyName()));
		getProfile().setPropertyValue("lastName",
				validProfile.getPropertyValue(getPropertyManager().getLastNamePropertyName()));
		getProfile().setPropertyValue("password", password);
		this.addFormException(new DropletException(getMessageHandler().getErrMsg(
				BBBCoreConstants.ERR_CHANGE_PASSWORD_NOTIFICATION, pRequest.getLocale().getLanguage(), null), "330"));
	}


	private void populateSessionPreLoginOnError(final DynamoHttpServletRequest pRequest, String loginEmail,
			String password, final RepositoryItem validProfile) {
		this.addFormException(new DropletException(
				BBBCoreConstants.BLANK, BBBCoreConstants.ERR_LOGIN_EMPTY_ERROR));
		this.mLoginErrorURL = this.mLoginErrorURL + BBBCoreConstants.FROM_RESET;
		pRequest.getSession().setAttribute(BBBCoreConstants.EMAIL_ADDR, loginEmail);
		pRequest.getSession().setAttribute(OLD_PWD, password);
		pRequest.getSession().setAttribute("fstName",
						validProfile.getPropertyValue(getPropertyManager()
						.getFirstNamePropertyName()));
		pRequest.getSession().setAttribute("lstName", validProfile
								.getPropertyValue(getPropertyManager().getLastNamePropertyName()));
	}


	private void processLockedValidProfilePreLogin(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap) {
		final String accountLockedErrorMsg = getMessageHandler().getErrMsg(
						BBBCoreConstants.ERR_ACCOUNT_LOCKED_ERROR,
						pRequest.getLocale().getLanguage(),
						errorPlaceHolderMap, null);
		this.addFormException(new DropletException(
				accountLockedErrorMsg, BBBCoreConstants.ERR_ACCOUNT_LOCKED_ERROR));
		this.errorMap.put(BBBCoreConstants.LOGIN_PASSWORD_ERROR, accountLockedErrorMsg);
	}


	private void populateProfileFlags(RepositoryItem profileItem) {
		final Object tempMigrationFlag = profileItem.getPropertyValue(getPropertyManager()
		        .getMigratedAccount());
		final Object tempUserMigratedLoginProp = profileItem.getPropertyValue(getPropertyManager()
		        .getLoggedIn());
		final Object tempResetPasswordFlag = profileItem.getPropertyValue(getPropertyManager()
		        .getResetPasswordPropertyName());
		if ((tempMigrationFlag != null) && (tempUserMigratedLoginProp != null)) {
		    this.migrationFlag = ((Boolean) profileItem.getPropertyValue(getPropertyManager()
		            .getMigratedAccount())).booleanValue();		   
		    setUserMigratedLoginProp(((Boolean) profileItem.getPropertyValue(getPropertyManager()
		            .getLoggedIn())).booleanValue());
		}
		if (tempResetPasswordFlag != null) {
		    this.resetPasswordFlag = ((Boolean) profileItem.getPropertyValue(getPropertyManager()
		            .getResetPasswordPropertyName())).booleanValue();
		}
	}

	/**
	 * Added for Cookie Auto Login for Mobile App
	 * SAP-255 Changed code to get the source of traffic from Header field "origin_of_traffic" 
	 * set in Mobile code for both Mobile Web and Mobile App.
	 */
	private void addAutoLoginCookiePreUserLogin(final DynamoHttpServletRequest pRequest, RepositoryItem profileItem) {
		if(isRememberMe()){
			RepositoryItem profileItemForCookie = null;
		    this.mTools = (BBBProfileTools) this.getProfileTools();
			profileItemForCookie = this.mTools.getItemFromEmail(getLoginEmail().toLowerCase());
			if(profileItemForCookie != null){
				if(isLoggingDebug()){
					logDebug("Updating the User Profile Auto Login Property Status");
				}
				pRequest.setAttribute(getPropertyManager().getAutoLoginPropertyName(), isRememberMe());
				this.updateAutoLoginProperty(profileItem, isRememberMe(), pRequest);
			}
		}
		else{
			if(this.mTools.getAutoLogin(profileItem)){
				this.updateAutoLoginProperty(profileItem, false, pRequest);
			}
		}
	}

	private void preTBSLoginUser(final DynamoHttpServletRequest pRequest) {
		String lLoginEmail = getStringValueProperty(BBBCoreConstants.LOGIN);
		if(null != pRequest.getHeader("Referer") &&pRequest.getHeader("Referer").contains("shippingGr=multi")){
			vlogDebug("setting success url to multishipping page");
			setSuccessURL(pRequest, "checkoutPageLoginMulti");
		}
		if(!StringUtils.isBlank(lLoginEmail)){
			RepositoryItem lProfileItem = getProfileTools().getItemFromEmail(lLoginEmail);
			
			if(lProfileItem != null){
				Map lUserSitesMap = (Map) lProfileItem.getPropertyValue(getPropertyManager().getUserSiteItemsPropertyName());
				Set lUserSites = lUserSitesMap.keySet();
				
				String lCurrentSiteId = SiteContextManager.getCurrentSiteId();
				String lSiteId = getTbsBBBSiteMap().get(lCurrentSiteId);
				if(!StringUtils.isBlank(lSiteId)){
					if(lUserSites.contains(lSiteId) && !lUserSites.contains(lCurrentSiteId)){
						vlogDebug("setting TBS site association when user logs in with the existing account created in store");
						setAssoSite(lCurrentSiteId);
						
					}
				}
			}
		}
	}

    /** Override method from ATG OOTB and if user login fails it will add error to the error map
     * 
     * @param pRequest DynamoHttpServletRequest 
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException if there was an error while executing the code
     * @throws IOException if there was an error with servlet io */
    @Override
    public void postLoginUser(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
    	
        final String methodName = BBBCoreConstants.POST_LOGIN_USER;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.SIGNIN_POST_LOGIN, methodName);
        final long startTime = System.currentTimeMillis();        
       
        populateCouponMailPostLogin();        
        processAutoLogin(pRequest, pResponse);
        
        try {

            this.logDebug("BBBProfileFormHandler.postLoginUser() method started");

            String siteId = getSiteContext().getSite().getId();
            final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
            final boolean isLoggedIn = !this.getProfile().isTransient();
            
            if (!BBBUtility.isEmpty(siteId) && (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_US)) || siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BBB)) {
				updateProfileCountryAndCurrency(pRequest, getSessionBean(), this.getProfile() );
            }
            addSuccessUrlKickStarterPostLogin(pRequest);            
            
            this.mTools = (BBBProfileTools) this.getProfileTools();
            processUserPostLogin(pRequest, pResponse, errorPlaceHolderMap, isLoggedIn);
        } catch (final RepositoryException e) {
            this.logError("RepositoryException: postLoginUser", e);
        } finally {
            final long totalTime = System.currentTimeMillis() - startTime;
            BBBPerformanceMonitor.end(BBBPerformanceConstants.SIGNIN_POST_LOGIN, methodName);
        }        
        if(pRequest.getContextPath().equalsIgnoreCase(BBBCoreConstants.CONTEXT_TBS)){
        	postTBSLoginUser(pRequest);
        }
    }


	private void processAutoLogin(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		
		final String isFBAutoLogin = pRequest.getParameter("autoLoginFlag");
        
		this.setAutoLoginAfterChangePassword(false);
        
        if (isFBAutoLogin == null) {
            // Redirect to success page
            this.logDebug("Redirecting before post login");
            this.checkFormRedirect(this.getLoginSuccessURL(), this.getLoginErrorURL(), pRequest, pResponse);
        }
	}


	private void processUserPostLogin(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, final Map<String, String> errorPlaceHolderMap,
			final boolean isLoggedIn) throws ServletException, IOException, RepositoryException {
		if (!this.isLegacyUserBoolean()) {
		    super.postLoginUser(pRequest, pResponse);
			if (!isLoggedIn && !this.isLegacyUserBoolean()) {

				processTransientLegacyUserPostLogin(pRequest, errorPlaceHolderMap);

			} else if ((this.errorMap == null) || this.errorMap.isEmpty()) {

				processNonTransientLegacyUserPostLogin(pRequest, pResponse, errorPlaceHolderMap);
			}
		    procMigratedNonMigratedAccPostLogin(pRequest, isLoggedIn);               

		    this.logDebug("BBBProfileFormHandler.postLoginUser() method end");
		    this.setLogin(true);
			this.removeOrderCookie(pRequest, pResponse);
		    processSocialRecommendationPostLogin(pRequest);                
		} else {
		    processATGUserPostLogin(pRequest);
		}
		pRequest.getSession().removeAttribute(BBBCoreConstants.FIRST_VISIT);
	}


	private void processATGUserPostLogin(final DynamoHttpServletRequest pRequest) {
		this.getFormExceptions().clear();
		final NameContext nameContext = pRequest.getRequestScope();
		if (nameContext != null) {
		    final Vector tempVector = new Vector();
		    pRequest.setAttribute(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE, tempVector);
		    nameContext.putElement(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE, tempVector);
		}
	}

    /**
     * changes for BBBP-5042, when user login coupon mail shuold be updated with login
     * @throws IOException 
     * @throws ServletException 
     */
	private void populateCouponMailPostLogin() throws ServletException, IOException {
		
		if(null != this.getSrcAppliedCoupons() && this.getSrcAppliedCoupons().size() > 0)
        	((BBBPromotionTools)getPromotionTools()).removeAlreadyAppliedCoupons(this.getSrcAppliedCoupons(), this.getProfile(), this.getOrder().getSiteId());
		getSessionBean().setCouponEmail(getLoginEmail());
	}


	private void processNonTransientLegacyUserPostLogin(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, final Map<String, String> errorPlaceHolderMap)
					throws ServletException, RepositoryException, IOException {
		
		this.updateLoginAttemptCount(this.getProfile().getDataSource(), true);
		
		processAssoSite(pRequest, errorPlaceHolderMap);
		processSyncRegistryWithLegacyDb();
		
		@SuppressWarnings ("unchecked")
		List<String> pos = (List<String>) pRequest.getSession().getAttribute(BBBCoreConstants.ITEM);
		// BBBSL-2735. Added synchronization on order object.
		synchronized (getOrder()) {
			processCommItemsPostLogin(pRequest, pos);
		}
		iterateAndProcessWishListPostLogin(pRequest);

		final Object redirectURL = pRequest.getSession().getAttribute(BBBCoreConstants.REDIRECT_URL);
		final BBBSavedItemsSessionBean saved = this.getSavedItemsSessionBean();
		processUserCheckingOutNull(pRequest, pResponse, redirectURL, saved);
		//Check Gift List items if shipping method is unavailable
		checkGiftListItemsForShipAvailability(pRequest);
		this.setProfileID(this.getProfile().getRepositoryId());
		populateAddProfileCookie(pResponse);
		invokeBazaarVoiceCallPostLogin(pRequest);
		populateActivateGuidePostLogin();
	}

	/**
	 *  <!-- Social Recommendation Flow interceptor BPS-456-->
	 */
	private void processSocialRecommendationPostLogin(final DynamoHttpServletRequest pRequest) {
		if (getSessionBean().isRecommenderFlow()) {
		    this.setLoginSuccessURL(getSessionBean().getRecommenderRedirectUrl());
		}
		try {
			List<RegistrySummaryVO> recommendRegistry = getGiftRegistryManager().recommendRegistryList(this.getProfile().getRepositoryId());
			if(null != recommendRegistry) {
				getSessionBean().setRegistrySummaryVO(recommendRegistry);
			}
		} catch (BBBSystemException bbbse) {
		    this.logError(
		            LogMessageFormatter.formatMessage(pRequest, "Recommender Registry BBB Profile mapping"
		                    + FROM_POST_CREATE_USER, BBBCoreErrorConstants.GIFTREGISTRY_ERROR_20133), bbbse);
		}
	}

	/**
	 * process migrated and non-migrated accounts post login
	 * 
	 * @param pRequest
	 * @param isLoggedIn
	 */

	private void procMigratedNonMigratedAccPostLogin(final DynamoHttpServletRequest pRequest,
			final boolean isLoggedIn) {
		if (this.isMigratedAccount()
		        && !isLoggedIn
		        && ((this.errorMap == null) || this.errorMap.isEmpty())
		        && !org.apache.commons.lang.StringUtils.equalsIgnoreCase(this.getLoginErrorURL(),
		                BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
		    this.getFormExceptions().clear();
		    this.mLoginSuccessURL = this.mLoginErrorURL + BBBCoreConstants.SHOW_POP_UP;
		} else if (this.isMigratedAccount()
		        && !isLoggedIn
		        && ((this.errorMap == null) || this.errorMap.isEmpty())
		        && org.apache.commons.lang.StringUtils.equalsIgnoreCase(this.getLoginErrorURL(),
		                BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
		    this.getFormExceptions().clear();
		    if (this.isLoginFromCheckout()) {
		        this.addFormException(new DropletException(getMessageHandler().getErrMsg(
		                BBBCoreConstants.ERR_CHECKOUT_LEGACY_USER_MOB, pRequest.getLocale().getLanguage(),
		                null, null), BBBCoreConstants.ERR_CHECKOUT_LEGACY_USER_MOB));
		    } else {
		        this.addFormException(new DropletException(getMessageHandler().getErrMsg(
		                BBBCoreConstants.ERR_LEGACY_USER_LOGIN_MOB, pRequest.getLocale().getLanguage(), null,
		                null), BBBCoreConstants.ERR_LEGACY_USER_LOGIN_MOB));
		    }
		}
		pRequest.getSession().setAttribute(BBBCoreConstants.RETURNING_USER, BBBCoreConstants.YES);
	}


	private void populateActivateGuidePostLogin() {
		getSessionBean().setActivateGuideInRegistryRibbon(false);
		List<RegistrySkinnyVO> registrySkinnyVOs = (List<RegistrySkinnyVO>) getSessionBean().getValues().get("registrySkinnyVOList");
		    List<NonRegistryGuideVO> nonRegistryGuideVOs = (List<NonRegistryGuideVO>) getSessionBean().getValues().get(BBBGiftRegistryConstants.GUIDE_VO_LIST);
		    
		    if (!BBBUtility.isListEmpty(registrySkinnyVOs)) {
				getSessionBean().setActivateGuideInRegistryRibbon(false);
			} else if (BBBUtility.isListEmpty(registrySkinnyVOs) && BBBUtility.isListEmpty(nonRegistryGuideVOs)) {
				getSessionBean().setActivateGuideInRegistryRibbon(false);
			}
	}


	private void invokeBazaarVoiceCallPostLogin(final DynamoHttpServletRequest pRequest) {
		try {
		    // Method call for Bazaar Voice product ratings and
		    // reviews
			// PS-24181 Website Issue Reported - BV | In case of fbconnect or forgot password flow this.getLoginEmail() is null, which 
			// inturn made bazaar_voice_token to be null and displaying incorrect value for email on review page so took from request 
			String bvTokenLoginEmail = this.getLoginEmail();
			if(BBBUtility.isEmpty(bvTokenLoginEmail) && null != pRequest.getAttribute("autoLoginEmail")) {
				bvTokenLoginEmail = (String) pRequest.getAttribute("autoLoginEmail");
			}
		    this.checkUserTokenBVRR(pRequest, null, false, bvTokenLoginEmail);
		} catch (final BBBBusinessException e) {
		    // Below Line is commented because we don't want to
		    // prevent login if calls fails
		    // addFormException(new
		    // DropletException(getLblTxtTemplateManager().getErrMsg(BBBCoreConstants.BAZAAR_VOICE_ERROR,pRequest.getLocale().getLanguage(),null,
		    // null)));
		    this.logError(
		            LogMessageFormatter.formatMessage(pRequest, BBB_BUSINESS
		                    + EXCEPTION_CHECK_USER_TOKEN_BVRR, BBBCoreErrorConstants.ACCOUNT_ERROR_1023), e);
		}
	}


	private void processUserCheckingOutNull(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, final Object redirectURL, final BBBSavedItemsSessionBean saved)
					throws ServletException, IOException, RepositoryException {
		if (this.getUserCheckingOut() == null) {
		    if (redirectURL != null) {
		        if (getAssoSite() != null) {
		        	//this.mAssoSite()= redirectURL.toString();
		            setAssoSite(redirectURL.toString());
		        } else {
		            this.mLoginSuccessURL = redirectURL.toString();
		        }
		        pRequest.getSession().removeAttribute(BBBCoreConstants.REDIRECT_URL);
		        String registryTab= pRequest.getParameter(BBBCoreConstants.REGISTRY_TAB);
		        if(!BBBUtility.isEmpty(registryTab)){
		        	this.mLoginSuccessURL = this.mLoginSuccessURL+registryTab; 
		        }
		    } else if (((saved != null) && (saved.getGiftListVO() != null))
		            || (pRequest.getSession().getAttribute(BBBCoreConstants.ADDED) != null)) {
		        getGiftListHandler().addSavedItemTOWishList(pRequest, pResponse);
		        this.addItemTOWishList(pRequest, pResponse);
		        if (pRequest.getSession().getAttribute(BBBCoreConstants.ADDED) != null) {
		            this.setLoginSuccessURL(getSessionBean().getProductDetailsRedirectUrl());
		        }
		    } else if (getSessionBean().getMoveCartItemToWishSuccessUrl() != null) {
		        this.addItemTOGiftList(pRequest, pResponse);
		    } else if (getSessionBean().getImportRegistryRedirectUrl() != null) {
		        this.importRegistryPostLogin(pRequest, pResponse);
		    } else if (getSessionBean().getGiftRegistryViewBean() != null) {
		        this.addToItemPostLoginUser(pRequest, pResponse);
		    }

		}
	}


	private void iterateAndProcessWishListPostLogin(final DynamoHttpServletRequest pRequest) {
		final RepositoryItem wishList = ((RepositoryItem) this.getProfile().getPropertyValue(
		        BBBCoreConstants.WISHLIST));
		if (wishList != null) {
		    @SuppressWarnings ("unchecked")
		    final List<RepositoryItem> wishtListItems = (List<RepositoryItem>) (wishList
		            .getPropertyValue(BBBCoreConstants.GIFT_LIST_ITEMS));
		    for (final RepositoryItem itemsz : wishtListItems) {
		        try {
		            final MutableRepositoryItem item = (MutableRepositoryItem) itemsz;
		            item.setPropertyValue(BBBCoreConstants.MSGSHOWNFLAGOFF, Boolean.FALSE);
		            ((MutableRepository) this.getGiftListManager().getGiftlistTools()
		                    .getGiftlistRepository()).updateItem(item);
		        } catch (final RepositoryException e) {
		            this.logError(LogMessageFormatter.formatMessage(pRequest,
		                    BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP,
		                    BBBCoreErrorConstants.GIFT_ERROR_1000), e);
		            this.logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP);
		        }
		    }
		}
	}

	/**
	 * process commerce items post login
	 * 
	 * @param pRequest
	 * @param pos
	 */
	private void processCommItemsPostLogin(final DynamoHttpServletRequest pRequest, List<String> pos) {
		final List<CommerceItem> comItemObjList = getOrder().getCommerceItems();
		if (comItemObjList != null) {
			if (pos == null) {
				pos = new ArrayList<String>();
			}
			for (final CommerceItem comItemObj : comItemObjList) {
				if (comItemObj instanceof BBBCommerceItem) {
					if (!StringUtils.isEmpty(((BBBCommerceItem) comItemObj).getRegistryId())) {
						pos.add(0, comItemObj.getId());
					} else {
						pos.add(comItemObj.getId());
					}
					((BBBCommerceItem) comItemObj).setMsgShownFlagOff(false);
				}
			}
			pRequest.getSession().setAttribute(BBBCoreConstants.ITEM, pos);
			try {
				this.getOrderManager().updateOrder(getOrder());
			} catch (final CommerceException e) {
				this.logError(BBBCoreErrorConstants.CART_ERROR_1021 + ": commerceException", e);
			}
		}
	}


	private void processSyncRegistryWithLegacyDb() throws RepositoryException {
		try {
		    // Method call to sync the User Registries status with
		    // legacy database
			getGiftRegistryManager().syncRegistryStatusWithECOM(this.getProfile(), getSiteContext().getSite().getId());
		} catch (final BBBSystemException e) {
		    this.logError(BBB_SYSTEM + EXCEPTION_GIFT_REGISTRY_MANAGER_UPDATE_PROFILE_REGISTRIES_STATUS, e);
		} catch (final BBBBusinessException e) {
		    this.logError(BBB_BUSINESS + EXCEPTION_GIFT_REGISTRY_MANAGER_UPDATE_PROFILE_REGISTRIES_STATUS,
		            e);
		}
	}


	private void processAssoSite(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap) throws ServletException {
		if ((getAssoSite() != null)
		        && getAssoSite().equalsIgnoreCase(getSiteContext().getSite().getId())) {
		    final String loginEmail = getPropertyManager().getLoginPropertyName();
		    final String email = (String) this.getProfile().getPropertyValue(loginEmail);
		    populateEmailOptIn(pRequest, email);
		    if (getBBBOrder() != null) {
		        triggerCheckoutRegistration(pRequest, errorPlaceHolderMap);
		    }
		}
	}


	private void triggerCheckoutRegistration(final DynamoHttpServletRequest pRequest, final Map<String, String> errorPlaceHolderMap)
			throws ServletException {
		final TransactionManager tm = this.getTransactionManager();
		final TransactionDemarcation td = this.getTransactionDemarcation();
		try {
		    td.begin(tm, TransactionDemarcation.REQUIRED);
		    // BBBSL-2735. Added synchronization on order object.
		    synchronized (getBBBOrder()) {
		    getProfileManager().checkoutRegistration(getBBBOrder(), this.getProfile(),
		            getSiteContext().getSite().getId(), getSessionBean()
		            .isPreSelectedAddress());
		    }
		    getSessionBean().setPreSelectedAddress(false);
		} catch (final TransactionDemarcationException e) {
		    this.logError(
		            LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
		                    + EXCEPTION_POST_LOGIN_USER, BBBCoreErrorConstants.ACCOUNT_ERROR_1384),
		                    e);
		    throw new ServletException(e);
		} catch (final BBBSystemException systemException) {
		    this.logError(LogMessageFormatter
		            .formatMessage(pRequest, BBBCoreConstants.ERR_CC_SYS_EXCEPTION,
		                    BBBCoreErrorConstants.ACCOUNT_ERROR_1119), systemException);
		    this.addFormException(new DropletException(getMessageHandler().getErrMsg(
		            BBBCoreConstants.ERR_CC_SYS_EXCEPTION, pRequest.getLocale().getLanguage(),
		            null, null), BBBCoreConstants.ERR_CC_SYS_EXCEPTION));
		    final String noProfileExist = getMessageHandler().getErrMsg(
		            BBBCoreConstants.ERR_CC_SYS_EXCEPTION, pRequest.getLocale().getLanguage(),
		            errorPlaceHolderMap, null);
		    this.errorMap.put(BBBCoreConstants.LOGIN_PASSWORD_ERROR, noProfileExist);

		} catch (final BBBBusinessException businessException) {
		    this.logError(LogMessageFormatter.formatMessage(pRequest,
		            BBBCoreConstants.ERR_CC_BSYS_EXCEPTION,
		            BBBCoreErrorConstants.ACCOUNT_ERROR_1120), businessException);

		    this.addFormException(new DropletException(getMessageHandler().getErrMsg(
		            BBBCoreConstants.ERR_CC_BSYS_EXCEPTION, pRequest.getLocale().getLanguage(),
		            null, null), BBBCoreConstants.ERR_CC_BSYS_EXCEPTION));
		    final String noProfileExist = getMessageHandler().getErrMsg(
		            BBBCoreConstants.ERR_CC_BSYS_EXCEPTION, pRequest.getLocale().getLanguage(),
		            errorPlaceHolderMap, null);
		    this.errorMap.put(BBBCoreConstants.LOGIN_PASSWORD_ERROR, noProfileExist);

		} finally {
		    try {
		        if (tm != null) {
		            td.end();
		        }
		    } catch (final TransactionDemarcationException e) {
		        this.logError("Error Occured during checkoutRegistration", e);
		    }
		} // End Try
	}


	private void populateEmailOptIn(final DynamoHttpServletRequest pRequest, final String email) {
		try {
			String emailOptIn = this.isExtendEmailOptn() ? BBBCoreConstants.YES : BBBCoreConstants.NO;
		    this.mTools.createSiteItem(email, SiteContextManager.getCurrentSiteId(), null, null, emailOptIn);
		    setAssoSite("");
		} catch (final BBBSystemException e) {
		    this.logError(LogMessageFormatter.formatMessage(pRequest, BBB_SYSTEM
		            + EXCEPTION_POST_LOGIN_USER + "while adding item to the User Profile",
		            BBBCoreErrorConstants.ACCOUNT_ERROR_1020), e);
		}
	}


	private void processTransientLegacyUserPostLogin(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap) {
		RepositoryItem profileItem;
		if (!this.isMigratedAccount()) {
		    this.getFormExceptions().clear();
		    processInvalidEmailLogin(pRequest, errorPlaceHolderMap);
		}
		final String loginEmail = getLoginEmail();
		if (!BBBUtility.isEmpty(loginEmail)) {
		    profileItem = this.mTools.getItemFromEmail(loginEmail.toLowerCase());
		    //if (profileItem != null) {
				if ((profileItem != null) && (BBBUtility.isEmpty(getLegacyUser())
						|| !getLegacyUser().equalsIgnoreCase(BBBCoreConstants.YES))) {
		        	boolean isAccountLocked = this.mTools.isAccountLocked(profileItem);					
						this.updateLoginAttemptCount(profileItem, false);				
		       }
	       //}
		}
	}

	/**
	 * adding Success URL if User is coming from Kick Starter Page to Manage his
	 * active registry.
	 * 
	 * @param pRequest
	 */
	private void addSuccessUrlKickStarterPostLogin(final DynamoHttpServletRequest pRequest) {
		if(getSessionBean().getMngActRegistry()!=null ){
			if(getSessionBean().getKickStarterId()!=null){
		    	if(getSessionBean().getKickStarterEventType()!=null && getSessionBean().getKickStarterEventType().equals(BBBGiftRegistryConstants.TOP_CONSULTANT)){
		    		setLoginSuccessURL(pRequest.getContextPath()+"/topconsultant/"+getSessionBean().getKickStarterId());
		    	}else{
		    		setLoginSuccessURL(pRequest.getContextPath()+"/shopthislook/"+getSessionBean().getKickStarterId());
		    	}
		    	}else{
		    		setLoginSuccessURL(pRequest.getContextPath()+"/kickstarters");
		    	}
		    }
	}

	private void postTBSLoginUser(final DynamoHttpServletRequest pRequest) {
		String contextPath = pRequest.getContextPath();
		
		GiftlistManager mgr = getGiftListManager();
		BBBOrderManager orderManager = (BBBOrderManager) getOrderManager();
		String displayName = null;
		String description = null;
		String siteId = null;
		Map<String, Long> removableItemQuantityMap = new HashMap<String, Long>();
		List<CommerceItem> removableItems = new ArrayList<CommerceItem>();
		
		vlogDebug("Updating the gift lists in post login");

		try {
			setGiftlistId((String) ((RepositoryItem) getProfile()
					.getPropertyValue("wishlist")).getPropertyValue("id"));
			synchronized (getOrder()) {
				List<CommerceItem> comItemObjList = getOrder()
						.getCommerceItems();
				
				for (CommerceItem comItemObj : comItemObjList) {

					if (comItemObj instanceof TBSCommerceItem) {
						TBSCommerceItem item = (TBSCommerceItem) comItemObj;

						String skuId = item.getCatalogRefId();
						if (skuId == null)
							return;
						RepositoryItem sku = (RepositoryItem) item
								.getAuxiliaryData().getCatalogRef();
						if (sku != null) {
							boolean webOffered = false;
					            if (sku.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) != null) {
					                webOffered = ((Boolean) sku
					                                .getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME)).booleanValue();
					            }

							if (!webOffered) {
								long quantity = item.getQuantity();
								String productId = item.getAuxiliaryData()
										.getProductId();
								if (productId == null)
									return;

								siteId = item.getAuxiliaryData().getSiteId();
								RepositoryItem product = (RepositoryItem) item
										.getAuxiliaryData().getProductRef();
								if (product != null) {
									productId = product.getRepositoryId();
									displayName = (String) product
											.getPropertyValue(mgr
													.getGiftlistTools()
													.getDisplayNameProperty());
									description = (String) product
											.getPropertyValue(mgr
													.getGiftlistTools()
													.getDescriptionProperty());
								}
								// if item is in giftlist, increment quantity otherwise add
								String giftId = mgr.getGiftlistItemId(
										getGiftlistId(), skuId, productId,
										siteId);
								if (quantity == -9999999) {
									return;
								}

								try {
									if (giftId != null){
										vlogDebug("Updating gift item with sku id :: " + skuId);
										mgr.increaseGiftlistItemQuantityDesired(
												getGiftlistId(), giftId,
												quantity);
									}
									else {
										vlogDebug("Adding the sku with id" + skuId +  " to giftlist");
										String itemId = null;
										if (siteId != null) {
											itemId = mgr.createGiftlistItem(
													skuId, sku, productId,
													product, quantity,
													displayName, description,
													siteId);
										} else {
											itemId = mgr.createGiftlistItem(
													skuId, sku, productId,
													product, quantity,
													displayName, description);
										}
										mgr.addItemToGiftlist(getGiftlistId(),
												itemId);
									}
									// update order quantity
									removableItems.add(item);
									removableItemQuantityMap.put(item.getId(), quantity);
									
								} catch (CommerceException e) {
									if(isLoggingError()){
										logError(e);
									}
								}
							}
						}
					}
				}
				
				orderManager.moveGiftItemsFromOrder(removableItems, removableItemQuantityMap, getOrder());
			}
		} catch (RepositoryException e) {
			if(isLoggingError()){
				logError(e);
			}
		} catch (CommerceException e) {
			if(isLoggingError()){
				logError(e);
			}
		}
	}
    /** Method to set Country Code, Currency Code in Profile
     *
     * @param pRequest
     * @param sessionBean
     * @param profile
     *
     * */

    private void updateProfileCountryAndCurrency(DynamoHttpServletRequest pRequest, BBBSessionBean sessionBean, Profile profile) {
    	this.logDebug("BBBProfileFormHandler.updateProfileCountryAndCurrency() method started");
    	if ((sessionBean == null || profile == null) || BBBUtility.isMapNullOrEmpty(sessionBean.getValues())) {
			return;
		}
    	if (profile.getPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE) == null ||
    			profile.getPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE) == null ||
    			profile.getPropertyValue(BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT) == null ) {
    		this.logInfo("Country Code, Currency Code or InternationalShippingContext is not present in Profile: [ "
    				+ profile.getRepositoryId() + " ] and profile is transient [ " + profile.isTransient() + " ] Session Id : [ " + pRequest.getSession().getId() + " ] ");
		}
    	String selectedCountryCode = (String) sessionBean.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
    	String selectedCurrencyCode = (String) sessionBean.getValues().get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY);
    	this.logDebug("Country Code in Session : [ " + selectedCountryCode + " ] Currency Code in session : [ " + selectedCurrencyCode + " ] ");
    	profile.setPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE, selectedCountryCode);
    	profile.setPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE, selectedCurrencyCode);
    	profile.setPropertyValue(BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT, !BBBInternationalShippingConstants.DEFAULT_COUNTRY.equals(selectedCountryCode));
    	this.logDebug("BBBProfileFormHandler.updateProfileCountryAndCurrency() method End");
	}
    /** Method to return JSON Object for the Profile cookie
     * 
     * @param profileId
     * @param siteId
     * @return jsonRootObject */
    private JSONObject createProfileJSONObject(final String profileId, final String siteId) {
        final JSONObject jsonRootObject = new JSONObject();
        jsonRootObject.element(PROFILE_ID, profileId);
        jsonRootObject.element(SITE_ID, siteId);

        this.logDebug("Profile Json object created successfully");

        return jsonRootObject;
    }

    /** Add item to giftlist
     * 
     * @param pRequest
     * @param pResponse
     * @throws ServletException
     * @throws IOException */
    private void addItemTOGiftList(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

        final String methodName = BBBCoreConstants.ADD_ITEM_TO_GIFTLIST;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_ITEM_TO_GIFT_LIST, methodName);

        if (getSessionBean().getMoveCartItemToWishSuccessUrl() != null) {

            try {
                if (getAssoSite() == null) {
                    this.mLoginSuccessURL = getSessionBean().getMoveCartItemToWishSuccessUrl();
                    this.mCreateSuccessURL = getSessionBean().getMoveCartItemToWishSuccessUrl();
                } else {
                	//this.mAssoSite = getSessionBean().getMoveCartItemToWishSuccessUrl();
                    setAssoSite(getSessionBean().getMoveCartItemToWishSuccessUrl()) ;
                }
                final StoreGiftlistFormHandler handler = getGiftListHandler();
                handler.getGiftListItemFromSession(pRequest);
                if (!this.getFormError()) {
                    handler.moveItemsFromCart(pRequest, pResponse);
                }
                getSessionBean().clearMoveCartItemToWishBean();

            } catch (final CommerceException e) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, COMMERCE + EXCEPTION + ADD_ITEM_TO_GIFT_LIST,
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1024), e);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.ADD_ITEM_TO_GIFT_LIST, methodName);
            }
        }

    }

    /** Add item to giftlist
     * 
     * @param pRequest
     * @param pResponse
     * @throws ServletException
     * @throws IOException */
    private void addItemTOWishList(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

        final String methodName = BBBCoreConstants.ADD_ITEM_TO_WISHLIST;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_ITEM_TO_WISH_LIST, methodName);

        if (getSessionBean().getMoveCartItemToWishSuccessUrl() != null) {

            try {
                if (getAssoSite() != null) {
                	//this.mAssoSite = getSessionBean().getMoveCartItemToWishSuccessUrl();
                	setAssoSite(getSessionBean().getMoveCartItemToWishSuccessUrl()); 
                    // R2.1 changes start
                    // mLoginSuccessURL = getCartURL();
                    // mCreateSuccessURL = getCartURL();
                    // R2.1 changes end
                }
                final StoreGiftlistFormHandler handler = getGiftListHandler();

                if ((getSessionBean().getProductId() != null)
                        && !StringUtils.isEmpty(getSessionBean().getProductId())) {
                    handler.getGiftListItemFromSession(pRequest);
                    handler.handleAddItemToGiftlist(pRequest, pResponse);
                    getSessionBean().setProductId(null);
                    getSessionBean().setCatalogRefId(null);
                } else if (getSessionBean().getGiftListVO() != null) {
                    // mLoginSuccessURL =
                    // getSessionBean().getProductDetailsRedirectUrl();

                    handler.getGiftListItemFromSession(pRequest);
                    handler.handleAddItemListToGiftlist(pRequest, pResponse);
                    final StringBuilder successUrl = new StringBuilder(getSessionBean()
                            .getProductDetailsRedirectUrl());
                    if (getAssoSite() == null) {
                        this.mLoginSuccessURL = successUrl.append(handler.isAddWishlistSuccessFlag()).toString();
                        this.mCreateSuccessURL = this.mLoginSuccessURL;
                    } else {
                    	//this.mAssoSite = successUrl.append(handler.isAddWishlistSuccessFlag()).toString();
                        setAssoSite(successUrl.append(handler.isAddWishlistSuccessFlag()).toString());
                    }
                    getSessionBean().setGiftListVO(null);
                   
                    getSessionBean().setProductDetailsRedirectUrl(null);
                }
                // getSessionBean().clearMoveCartItemToWishBean();

            } catch (final Exception e) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, EXCEPTION + ADD_ITEM_TO_WISH_LIST,
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1025), e);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.ADD_ITEM_TO_WISH_LIST, methodName);
            }
        }

    }

    /** add item to gift registry
     * 
     * @param pRequest
     * @param pResponse
     * @throws ServletException
     * @throws IOException */
    private void addToItemPostLoginUser(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        String appendCharacter = "";

        this.logDebug("BBBProfileFormHandler.addToItemPostLoginUser() method started");

        final String methodName = BBBCoreConstants.ADD_TO_ITEM_POST_LOGIN_USER;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_TOITEM_POST_LOGIN_USER, methodName);
        boolean fromComparePage = false;
        final GiftRegistryViewBean giftRegistryViewBean = getSessionBean().getGiftRegistryViewBean();
        if ((giftRegistryViewBean != null) && (giftRegistryViewBean.getSuccessURL() != null)) {
            String addItemRedirectURL = giftRegistryViewBean.getSuccessURL();
            final String siteId = atg.multisite.SiteContextManager.getCurrentSiteId();
            
            fromComparePage = populateFromComparePageFlag(fromComparePage, addItemRedirectURL, siteId);
            boolean isKickStarterPage = isURLContainsKickStarterId(giftRegistryViewBean.getSuccessURL());
            String appendStringForPopup = "";
            String ltlShipMethod = populateLtlShipMethod(giftRegistryViewBean);
            
            try {                
                final Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
                appendCharacter = populateAppendCharacter(addItemRedirectURL);
                if (!profile.isTransient()) {
                    boolean itemAdded = false;
                    final List<RegistrySkinnyVO> registrySkinnyVOList = getGiftRegistryManager().getUserRegistryList(
                            profile, siteId);
                    
                    
                    if ((registrySkinnyVOList != null) && (registrySkinnyVOList.size() == 1)) {
                    	String skuId = giftRegistryViewBean.getAdditem().get(0).getSku();
                    	RegistrySkinnyVO regSkinVO = registrySkinnyVOList.get(0);
                        populateGiftRegViewBean(giftRegistryViewBean, registrySkinnyVOList, regSkinVO);
                        // added for story BBBP-1009 - post login show the alternate phone popup for LTL items
                        
                        boolean checkForLTLModal = checkForLTLModal(regSkinVO, siteId, skuId, fromComparePage);                       
                        boolean isNotifyRegistrant = false;
                        String notifyRegDisplay = null;
                        
            			List<String> notifyRegFlagList = getBbbCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBGiftRegistryConstants.NOTIFY_REGISTRANT_FLAG);
            			if (notifyRegFlagList != null && !notifyRegFlagList.isEmpty()){
            				isNotifyRegistrant = Boolean.parseBoolean(notifyRegFlagList.get(0));
            			}
            			if(isNotifyRegistrant && !siteId.contains(BBBCoreConstants.TBS)){
            				notifyRegDisplay = getGiftRegistryManager().getNotifyRegistrantMsgType(skuId, regSkinVO.getEventDate());                            
            			} 
            			
            			addItemRedirectURL = populateAddItemRedirectUrl(fromComparePage, giftRegistryViewBean,
								addItemRedirectURL, notifyRegDisplay);
            			if(checkForLTLModal || !(BBBUtility.isEmpty(notifyRegDisplay))){
            				appendStringForPopup = populateStringForPopupFromLtl(appendCharacter, giftRegistryViewBean,
									siteId, regSkinVO, checkForLTLModal, notifyRegDisplay); 
            				itemAdded = true;
            			} 
            			else{
                        	appendStringForPopup = populatePopupStringWithErrorParam(appendCharacter,
									giftRegistryViewBean, registrySkinnyVOList);
                            itemAdded = true;
                            
                            getGiftRegistryManager().getGiftRegistryTools().invalidateRegistry(giftRegistryViewBean);
                        }
                        
					} else if ((registrySkinnyVOList != null) && (registrySkinnyVOList.size() > 1) && !isKickStarterPage) {
                        for (final RegistrySkinnyVO registry : registrySkinnyVOList) {

                            if (registry.getRegistryId().equalsIgnoreCase(giftRegistryViewBean.getRegistryId())) {
                                appendStringForPopup = populateGiftRegistryViewPopupString(appendCharacter,
										giftRegistryViewBean, registry);
                                itemAdded = true;
                                
                                //cache call required or not
                            }

                        }

                    }  else if(registrySkinnyVOList != null && registrySkinnyVOList.size() == 0){
                    	//BBBSL-4294 :: If user does not have any registry than add a variable in url to show up the pop up of create Registry
                    	appendStringForPopup = appendCharacter + "&noregpopup=true";
                    	itemAdded=true;
                    }
                    if (!itemAdded) {
                        appendStringForPopup = appendCharacter + "showpopup=false";
                    }
                    if(!BBBUtility.isEmpty(ltlShipMethod)){
                    	// added for story BBBP-1009 - post login show the LTL method selected on PDP
                    	appendStringForPopup = appendStringForPopup + "&sopt=" + ltlShipMethod;
                    }

                }

            } catch (final BBBBusinessException e) {
                this.logError(
                        LogMessageFormatter.formatMessage(pRequest, BBB_BUSINESS + EXCEPTION
                                + WHILE_ADDING_ITEM_TO_THE_GIFT_REGISTRY, BBBCoreErrorConstants.ACCOUNT_ERROR_1026), e);
                this.mLoginErrorURL = giftRegistryViewBean.getSuccessURL() + appendCharacter
                        + "additemflagerror=true&showpopup=true";
                // Below Line is commented because we don't want to prevent
                // login if calls fails
                // addFormException(new
                // DropletException(getLblTxtTemplateManager().getErrMsg("err_regsearch_biz_exception",
                // pRequest.getLocale().getLanguage(), null, null)));
            } catch (final BBBSystemException es) {
                this.logError(
                        LogMessageFormatter.formatMessage(pRequest, BBB_SYSTEM + EXCEPTION
                                + WHILE_ADDING_ITEM_TO_THE_GIFT_REGISTRY, BBBCoreErrorConstants.ACCOUNT_ERROR_1027), es);
                this.mLoginErrorURL = giftRegistryViewBean.getSuccessURL() + appendCharacter
                        + "additemflagerror=true&showpopup=true";
                /*
                 * Below Line is commented because we don't want to prevent login if calls fails
                 */
                // addFormException(new
                // DropletException(getLblTxtTemplateManager().getErrMsg("err_regsearch_sys_exception",
                // pRequest.getLocale().getLanguage(), null, null)));
            } catch (final RepositoryException e) {
                this.logError(LogMessageFormatter.formatMessage(pRequest,
                        "RepositoryException - while adding item to the gift registry",
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1028), e);
                this.mLoginErrorURL = giftRegistryViewBean.getSuccessURL() + appendCharacter
                        + "additemflagerror=true&showpopup=true";
                /*
                 * Below Line is commented because we don't want to prevent login if calls fails
                 */
                // addFormException(new
                // DropletException(getLblTxtTemplateManager().getErrMsg("err_regsearch_repo_exception",
                // pRequest.getLocale().getLanguage(), null, null)));

            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.ADD_TOITEM_POST_LOGIN_USER, methodName);
            }
            populateLoginCreateSuccessUrl(pRequest, addItemRedirectURL, appendStringForPopup);            
            pRequest.getSession()
            .setAttribute(BBBCoreConstants.REDIRECT_URL, addItemRedirectURL + appendStringForPopup);
          
            // mSessionBean.setGiftRegistryViewBean(null);
        }

        this.logDebug("BBBProfileFormHandler.addToItemPostLoginUser() method end");

    }


	private String populateLtlShipMethod(final GiftRegistryViewBean giftRegistryViewBean) {
		String ltlShipMethod = "";
		if(null != giftRegistryViewBean.getAdditem() && giftRegistryViewBean.getAdditem().size() > 0){
			ltlShipMethod = giftRegistryViewBean.getAdditem().get(0).getLtlDeliveryServices();
		}
		return ltlShipMethod;
	}


	private void populateLoginCreateSuccessUrl(final DynamoHttpServletRequest pRequest, String addItemRedirectURL,
			String appendStringForPopup) {
		final Object redirectURL = pRequest.getSession().getAttribute(BBBCoreConstants.REDIRECT_URL);
		if (BBBUtility.isEmpty(getSessionBean().getExtendModal()) || !getSessionBean().getExtendModal().equals(BBBCoreConstants.TRUE)) {
		    if (redirectURL != null) {
		        this.mLoginSuccessURL = redirectURL.toString();
		        this.mCreateSuccessURL = this.mLoginSuccessURL;
		    } else {
		        this.mLoginSuccessURL = addItemRedirectURL + appendStringForPopup;
		        this.mCreateSuccessURL = this.mLoginSuccessURL;
		    }
		    pRequest.getSession().removeAttribute(BBBCoreConstants.REDIRECT_URL);
		} else {
		    //this.mAssoSite= addItemRedirectURL + appendStringForPopup;
		    setAssoSite(addItemRedirectURL + appendStringForPopup);
		}
	}


	private String populateGiftRegistryViewPopupString(String appendCharacter,
			final GiftRegistryViewBean giftRegistryViewBean, final RegistrySkinnyVO registry)
					throws BBBBusinessException, BBBSystemException {
		String appendStringForPopup;
		giftRegistryViewBean.setRegistryId(registry.getRegistryId());
		giftRegistryViewBean.setRegistryName(registry.getEventType());

		final boolean error = getGiftRegistryManager()
		        .addItemToGiftRegistry(giftRegistryViewBean).getServiceErrorVO()
		        .isErrorExists();
		appendStringForPopup = appendCharacter + "additemflagerror=" + error
		        + "&showpopup=true" + "&registryId=" + registry.getRegistryId()
		        + "&registryName=" + registry.getEventType() + "&totQuantity="
		        + giftRegistryViewBean.getTotQuantity();
		return appendStringForPopup;
	}


	private String populatePopupStringWithErrorParam(String appendCharacter,
			final GiftRegistryViewBean giftRegistryViewBean, final List<RegistrySkinnyVO> registrySkinnyVOList)
					throws BBBBusinessException, BBBSystemException {
		String appendStringForPopup;
		final boolean error = getGiftRegistryManager().addItemToGiftRegistry(giftRegistryViewBean)
		        .getServiceErrorVO().isErrorExists();
		appendStringForPopup = appendCharacter + "additemflagerror=" + error + "&showpopup=true"
		        + "&registryId=" + registrySkinnyVOList.get(0).getRegistryId() + "&registryName="
		        + registrySkinnyVOList.get(0).getEventType() + "&totQuantity="
		        + giftRegistryViewBean.getTotQuantity();
		return appendStringForPopup;
	}


	private String populateStringForPopupFromLtl(String appendCharacter,
			final GiftRegistryViewBean giftRegistryViewBean, final String siteId, RegistrySkinnyVO regSkinVO,
			boolean checkForLTLModal, String notifyRegDisplay) {
		String appendStringForPopup;
		appendStringForPopup = appendCharacter + "registryId=" + regSkinVO.getRegistryId() + "&registryName="
				+ regSkinVO.getEventType() + "&totQuantity="
				+ giftRegistryViewBean.getTotQuantity() + "&sm=true";
		if(!(BBBUtility.isEmpty(notifyRegDisplay))){
			appendStringForPopup = appendStringForPopup + "&showNotifyRegModal=true";
		}
		if(checkForLTLModal && !siteId.contains(BBBCoreConstants.TBS)){                        	
			if(regSkinVO.isPoBoxAddress()){
				appendStringForPopup = appendStringForPopup + "&po=true";
			} else if(BBBUtility.isEmpty(regSkinVO.getAlternatePhone())){
				appendStringForPopup = appendStringForPopup + "&ph=true";
			}                            	
		}
		return appendStringForPopup;
	}


	private String populateAddItemRedirectUrl(boolean fromComparePage, final GiftRegistryViewBean giftRegistryViewBean,
			String addItemRedirectURL, String notifyRegDisplay) {
		if (!BBBUtility.isEmpty(notifyRegDisplay) && !BBBUtility.isEmpty(giftRegistryViewBean.getSuccessURLforChildProduct())){
				addItemRedirectURL = giftRegistryViewBean.getSuccessURLforChildProduct();
		}
		if(fromComparePage && !BBBUtility.isEmpty(notifyRegDisplay)){
			addItemRedirectURL = giftRegistryViewBean.getSuccessURLforNotifyProduct();
		}
		return addItemRedirectURL;
	}


	private void populateGiftRegViewBean(final GiftRegistryViewBean giftRegistryViewBean,
			final List<RegistrySkinnyVO> registrySkinnyVOList, RegistrySkinnyVO regSkinVO) {
		giftRegistryViewBean.setRegistryId(regSkinVO.getRegistryId());
		giftRegistryViewBean.setRegistryName(regSkinVO.getEventType());
		giftRegistryViewBean.setRegistrySize(registrySkinnyVOList.size());
	}


	private String populateAppendCharacter(String addItemRedirectURL) {
		String appendCharacter;
		if (addItemRedirectURL.contains("?")) {
		    appendCharacter = "&";
		} else {
		    appendCharacter = "?";
		}
		return appendCharacter;
	}


	private boolean populateFromComparePageFlag(boolean fromComparePage, String addItemRedirectURL,
			final String siteId) {
		if(addItemRedirectURL.contains(BBBGiftRegistryConstants.COMPARE_SUCCESS_URL)){
			fromComparePage = true;
		}            
		else if(siteId.contains(BBBCoreConstants.TBS) && addItemRedirectURL.contains(BBBGiftRegistryConstants.COMPARE_SUCCESS_URL_TBS)){
			fromComparePage = true;
		}
		return fromComparePage;
	}
    
    /**
     * Check conditions for displaying ltl modal for PO Box address, alternate phone number and compare page flow.
     *
     * @param regSkinVO the reg skin vo
     * @param siteId the site id
     * @param skuId the sku id
     * @param fromComparePage the from compare page
     * @return true, if successful
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
    public boolean checkForLTLModal(RegistrySkinnyVO regSkinVO, String siteId, String skuId, boolean fromComparePage) throws BBBSystemException, BBBBusinessException{
    	boolean showModal = false;
    	if(getBbbCatalogTools().isSkuLtl(siteId, skuId)){
    		if(siteId.contains(BBBCoreConstants.TBS)){
    			if(fromComparePage){
        			showModal = true;
        		}
    		}
    		else{
    			if((regSkinVO.isPoBoxAddress() || BBBUtility.isEmpty(regSkinVO.getAlternatePhone()) || fromComparePage)){
        			showModal = true;
        		}
    		}    		
    	}
    	return showModal;
    }
    
    
    /** This method is used to check kick starter id in the query parameter
     * 
     * @param pUrl - Success url after login
     * @return boolean true if id contains in the url otherwise it will be false.
     * 
     * */
    public boolean isURLContainsKickStarterId(String pUrl){
	 String queryParameterString = pUrl.substring(pUrl.lastIndexOf(BBBGiftRegistryConstants.QUESTION_MARK) + 1);
		String queries[]= queryParameterString.split(BBBGiftRegistryConstants.AMPERSAND);		
		if(queries!=null){
					if(queries[0]!=null && queries[0].contains(BBBGiftRegistryConstants.ID)){
					 return true;
					}		
		}
		return false;
     }
    /** This method is overridden and performs the following 1> calls a new credit card and creates new billing address
     * method OR 3> calls a new credit card for the existing address
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception */

    public final void handleAddCreditCardToProfile(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.handleCreateCreditCard(pRequest, pResponse);
    }

    /** This method is overridden and performs the following 1> calls a new credit card and creates new billing address
     * method OR 3> calls a new credit card for the existing address
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception */

    public final void handleCreateCreditCard(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        final String methodName = BBBCoreConstants.HANDLE_CREATE_CREDIT_CARD;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.CREATE_CREDIT_CARD, methodName);
        try {

            this.logDebug(LogMessageFormatter.formatMessage(pRequest,
                    "BBBProfileFormHandler.handleCreateCreditCard() method Starts"));

            final Map<String, Object> newCard = this.getEditValue();
            final Map<String, String> newAddress = this.getBillAddrValue();
			/*BBBH-1893 Add Credit Card functionality is not working |start*/ 
           // String siteId = SiteContextManager.getCurrentSiteId();
			if (getFromPage() != null) {
        		setCreateCardSuccessURL(pRequest.getContextPath()
    					+ getSuccessUrlMap().get(getFromPage()));
        		setCreateCardErrorURL(pRequest.getContextPath()
    					+ getErrorUrlMap().get(getFromPage()));
    		}
            /*BBBH-1893 Add Credit Card functionality is not working |end*/
            this.logDebug(LogMessageFormatter.formatMessage(pRequest,
                    "BBBProfileFormHandler.handleCreateCreditCard() method (String) newAddress.get(newNickname);")
                    + newAddress.get(NEW_NICKNAME));

            this.logDebug(LogMessageFormatter.formatMessage(pRequest,
                    "BBBProfileFormHandler.handleCreateCreditCard() (String) newCard.get(address);")
                    + (String) newCard.get(ADDRESS_TO_SHIP));

            if ((newAddress.get(NEW_NICKNAME) != null)
                    && newAddress.get(NEW_NICKNAME).equals(NEW_BILLING_ADDRESS)) {
                this.handleCreateNewCreditCardAndAddress(pRequest, pResponse);

            } else if ((newAddress.get(NEW_NICKNAME) != null)
                    && !StringUtils.isBlank(newAddress.get(NEW_NICKNAME).toString())
                    && (newAddress.get(MAKE_BILLING) != null)
                    && !StringUtils.isBlank(newAddress.get(MAKE_BILLING).toString())) {
                this.handleCreateNewCreditCard(pRequest, pResponse);
            } else {
                this.addFormException(new DropletException(BBBCoreConstants.MSG_SELECT_RADIO_OPTION,
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1291));
            }

            this.logDebug(LogMessageFormatter.formatMessage(pRequest,
                    "BBBProfileFormHandler.handleCreateCreditCard() method ends"));

        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CREATE_CREDIT_CARD, methodName);
        }

    }

    /** This method is overridden and performs the following 1> creates a new credit card 2> creates new billing address
     * 3> option to make the billing address the preferred billing address
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */

    public final boolean handleCreateNewCreditCardAndAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug(LogMessageFormatter.formatMessage(pRequest, HANDLE_CREATE_NEW_CREDIT_CARD_AND_ADDRESS_METHOD
                + START));

        // validate credit card information
        if (!this.validateCreateCreditCardInformation(pRequest, pResponse, true)) {
            return this.checkFormRedirect(null, this.getCreateCardErrorURL(), pRequest, pResponse);
        }
        final TransactionManager tm = this.getTransactionManager();
        final TransactionDemarcation td = this.getTransactionDemarcation();
        final BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();
        final Profile profile = this.getProfile();
        // Get editValue map, containing the credit card properties
        final Map<String, Object> newCard = this.getEditValue();
        final Map<String, String> newAddress = this.getBillAddrValue();

        try {
            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRED);
            }
            final String secondaryAddrNickname = newAddress.get(this.getShippingAddressNicknameMapKey());
            String cardNickname = (String) newCard.get(getPropertyManager().getCreditCardNicknamePropertyName());
            try {
                // Create credit card and add to profile
                final List addresses = ((BBBProfileTools) this.getProfileTools()).getAllAvailableAddresses(this
                        .getProfile());
                if ((addresses == null) || addresses.isEmpty()) {
                    this.setMakePreferredSet(true);
                }
                cardNickname = profileTools.createProfileCreditCard(profile, newCard, cardNickname, newAddress,
                        secondaryAddrNickname, BBBCoreConstants.RETURN_TRUE, this.isMakePreferredSet());
                this.handleMakeDefaultCreditCard(pRequest, pResponse);
                newCard.clear();
                newAddress.clear();
            } catch (final RepositoryException repositoryExc) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, REPOSITORY + EXCEPTION
                        + HANDLE_CREATE_NEW_CREDIT_CARD_AND_ADDRESS_METHOD, BBBCoreErrorConstants.ACCOUNT_ERROR_1029),
                        repositoryExc);
            } catch (final InstantiationException ex) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, INSTANTIATION + EXCEPTION
                        + HANDLE_CREATE_NEW_CREDIT_CARD_AND_ADDRESS_METHOD, BBBCoreErrorConstants.ACCOUNT_ERROR_1030),
                        ex);
                throw new ServletException(ex);
            } catch (final IllegalAccessException ex) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, ILLEGAL_ACCESS + EXCEPTION
                        + HANDLE_CREATE_NEW_CREDIT_CARD_AND_ADDRESS_METHOD, BBBCoreErrorConstants.ACCOUNT_ERROR_1031),
                        ex);
                throw new ServletException(ex);
            } catch (final ClassNotFoundException ex) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, CLASS_NOT_FOUND + EXCEPTION
                        + HANDLE_CREATE_NEW_CREDIT_CARD_AND_ADDRESS_METHOD, BBBCoreErrorConstants.ACCOUNT_ERROR_1032),
                        ex);
                throw new ServletException(ex);
            } catch (final IntrospectionException ex) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, INTROSPECTION + EXCEPTION
                        + HANDLE_CREATE_NEW_CREDIT_CARD_AND_ADDRESS_METHOD, BBBCoreErrorConstants.ACCOUNT_ERROR_1033),
                        ex);
                throw new ServletException(ex);
            } catch (final PropertyNotFoundException ex) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, PROPERTY_NOT_FOUND + EXCEPTION
                        + HANDLE_CREATE_NEW_CREDIT_CARD_AND_ADDRESS_METHOD, BBBCoreErrorConstants.ACCOUNT_ERROR_1034),
                        ex);
                throw new ServletException(ex);
            }

            this.logDebug(LogMessageFormatter.formatMessage(pRequest,
                    "BBBProfileFormHandler.handleCreateNewCreditCardAndAddress() method Ends"));

            return this.checkFormRedirect(this.getCreateCardSuccessURL(), this.getCreateCardErrorURL(), pRequest,
                    pResponse);
        } catch (final TransactionDemarcationException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest,
                    "TransactionDemarcationException - BBBProfileFormHandler.handleCreateNewCreditCardAndAddress() ",
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1035), e);
            throw new ServletException(e);
        } finally {
            try {
                if (tm != null) {
                    td.end();
                }
            } catch (final TransactionDemarcationException e) {
                this.logError(
                        LogMessageFormatter
                        .formatMessage(
                                pRequest,
                                "TransactionDemarcationException - BBBProfileFormHandler.handleCreateNewCreditCardAndAddress() ",
                                BBBCoreErrorConstants.ACCOUNT_ERROR_1036), e);
            }
        }
    }

    /** This method is overridden and performs the following 1> creates a new credit card 2> option to select the
     * available billing address 3> option to make the billing address the preferred billing address
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleCreateNewCreditCard(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.handleCreateNewCreditCard() method started");

        // validate credit card information
        if (!this.validateCreateCreditCardInformation(pRequest, pResponse, false)) {
            return this.checkFormRedirect(null, this.getCreateCardErrorURL(), pRequest, pResponse);
        }
        final TransactionManager tm = this.getTransactionManager();
        final TransactionDemarcation td = this.getTransactionDemarcation();
        final BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();
        final Profile profile = this.getProfile();
        // Get editValue map, containing the credit card properties
        final Map<String, Object> newCard = this.getEditValue();
        final Map<String, String> newAddress = this.getBillAddrValue();
        String siteId = SiteContextManager.getCurrentSiteId();
    	if (!BBBUtility.siteIsTbs(siteId)
				&& getCreateCardSuccessURL() != null
				&& getCreateCardErrorURL() != null
				&& !getCreateCardSuccessURL().equalsIgnoreCase(
						BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)
				&& !getCreateCardErrorURL().equalsIgnoreCase(
						BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
    		setCreateCardSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(BBBURLConstants.CREATE_CARD));
    		setCreateCardErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(BBBURLConstants.CREATE_CARD));
		}
        
        
        try {
            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRED);
            }
            // Check to see if user is selecting existing address
            final String secondaryAddrNickname = newAddress.get(this.getNewNicknameValueMapKey());

            this.logDebug("BBBProfileFormHandler | handleCreateNewCreditCard | newAddress.get(makeBilling) |"
                    + newAddress.get(MAKE_BILLING));

            newAddress.get(BBBCoreConstants.MAKE_BILLING);

            this.logDebug("BBBProfileFormHandler.handleCreateNewCreditCard() newAddress.get(makeBilling)) is"
                    + newAddress.get(MAKE_BILLING));

            // Get credit card's nickname
            String cardNickname = (String) newCard.get(getPropertyManager().getCreditCardNicknamePropertyName());
            try {
                // Create credit card and add to profile

                this.logDebug("BBBProfileFormHandler.handleCreateNewCreditCard() getBillAddrValue().get(makeBilling)"
                        + this.getBillAddrValue().get(MAKE_BILLING));

                boolean isMakeBillingAddress = false;
                if (this.getBillAddrValue().get(BBBCoreConstants.MAKE_BILLING).toString().equals(BBBCoreConstants.TRUE)) {
                    isMakeBillingAddress = true;
                }
                cardNickname = profileTools.createProfileCreditCard(profile, newCard, cardNickname, newAddress,
                        secondaryAddrNickname, false, isMakeBillingAddress);
                this.handleMakeDefaultCreditCard(pRequest, pResponse);
                // empty out the map
                newCard.clear();
                newAddress.clear();

                this.logDebug("BBBProfileFormHandler.handleCreateNewCreditCard() method ends");

            } catch (final RepositoryException repositoryExc) {
                this.logError(LogMessageFormatter.formatMessage(pRequest,
                        "RepositoryException - BBBProfileFormHandler.handleCreateNewCreditCard() ",
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1037), repositoryExc);
            } catch (final InstantiationException ex) {
                this.logError(LogMessageFormatter.formatMessage(pRequest,
                        "InstantiationException - BBBProfileFormHandler.handleCreateNewCreditCard() ",
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1038), ex);
                throw new ServletException(ex);
            } catch (final IllegalAccessException ex) {
                this.logError(LogMessageFormatter.formatMessage(pRequest,
                        "IllegalAccessException - BBBProfileFormHandler.handleCreateNewCreditCard() ",
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1039), ex);
                throw new ServletException(ex);
            } catch (final ClassNotFoundException ex) {
                this.logError(LogMessageFormatter.formatMessage(pRequest,
                        "ClassNotFoundException - BBBProfileFormHandler.handleCreateNewCreditCard() ",
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1040), ex);
                throw new ServletException(ex);
            } catch (final IntrospectionException ex) {
                this.logError(LogMessageFormatter.formatMessage(pRequest,
                        "IntrospectionException - BBBProfileFormHandler.handleCreateNewCreditCard() ",
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1041), ex);
                throw new ServletException(ex);
            } catch (final PropertyNotFoundException ex) {
                this.logError(LogMessageFormatter.formatMessage(pRequest,
                        "PropertyNotFoundException - BBBProfileFormHandler.handleCreateNewCreditCard() ",
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1042), ex);
                throw new ServletException(ex);
            }
            return this.checkFormRedirect(this.getCreateCardSuccessURL(), this.getCreateCardErrorURL(), pRequest,
                    pResponse);
        } catch (final TransactionDemarcationException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest,
                    "TransactionDemarcationException - BBBProfileFormHandler.handleCreateNewCreditCard() ",
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1043), e);
            throw new ServletException(e);
        } finally {
            try {
                if (tm != null) {
                    td.end();
                }
            } catch (final TransactionDemarcationException e) {
                this.logError(LogMessageFormatter.formatMessage(pRequest,
                        "TransactionDemarcationException - BBBProfileFormHandler.handleCreateNewCreditCard() ",
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1044), e);
            }
        }
    }

    /** This method is overridden and performs the following 1> validates credit card details 2> validates address
     * details UC_CC_WALLET
     * 
     * @param pRequest
     * @param pResponse
     * @param pIsNewAddress
     * @return boolean */
    private boolean validateCreateCreditCardInformation(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse, final boolean pIsNewAddress) {

        this.logDebug("BBBProfileFormHandler.validateCreateCreditCardInformation() method started");

        // return false if there were missing required properties
        if (this.getFormError()) {
            return BBBCoreConstants.RETURN_FALSE;
        }
        final Map<String, Object> newCard = this.getEditValue();
        // String shippingAddrNickname = (String)
        // newAddress.get(getShippingAddressNicknameMapKey());
        // validate credit card fields
        this.validateCreditCardFields(pRequest, pResponse);
        // if new address should be created validate all address properties
        // and country/state combination
        if (pIsNewAddress) {
            this.validateBillingAddressFields(pRequest, pResponse);
        }
        if (this.getFormError()) {
            return BBBCoreConstants.RETURN_FALSE;
        }

        if (!this.validateCreditCard(newCard)) {
            return BBBCoreConstants.RETURN_FALSE;
        }
        // Check that the nickname is not already used for a credit card
        final BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();
        final Profile profile = this.getProfile();
        final String cardNickname = (String) newCard.get(getPropertyManager().getCreditCardNicknamePropertyName());
        if (profileTools.isDuplicateCreditCardNickname(profile, cardNickname)) {
            this.addFormException(new DropletException(BBBCoreConstants.ERROR_DUPLICATE_CC_NICKNAME,
                    "error_duplicate_cc_nickname"));
            this.errorMap.put(BBBCoreConstants.MSG_ERROR_DUPLICATE_CC_NICKNAME, "error_duplicate_cc_nickname");
            return BBBCoreConstants.RETURN_FALSE;
        }

        this.logDebug("BBBProfileFormHandler.validateCreateCreditCardInformation() method ends");

        return BBBCoreConstants.RETURN_TRUE;
    }

    /** This method is overridden and performs the following 1> validates credit card fields UC_CC_WALLET
     * 
     * @param pRequest
     * @param pResponse
     * @return boolean */

    private boolean validateCreditCardFields(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) {

        this.logDebug("BBBProfileFormHandler.validateCreditCardFields() method started");

        boolean missingFields = false;
        Object property = null;
        String propertyName = null;

        final Map<String, Object> newCard = this.getEditValue();
        final String[] cardProps = this.getCardProperties();

        // Verify all required fields entered for credit card
        for (final String cardProp : cardProps) {
            propertyName = cardProp;
            // not check here billingAddress Property
            if (propertyName.equals(getPropertyManager().getCreditCardBillingAddressPropertyName())) {
                continue;
            }
            property = newCard.get(propertyName);
            if (StringUtils.isBlank((String) property) || StringUtils.isEmpty((String) property)) {

                this.logDebug("BBBProfileFormHandler | validateCreditCardFields | ppty name" + propertyName + "ppty"
                        + property);

                if (propertyName.equals(BBBCoreConstants.CC_NUMBER)) {
                    this.errorMap.put(BBBCoreConstants.MSG_CC_NUMBER_EMPTY, BBBCoreConstants.ERR_CC_NUMBER_EMPTY);
                    this.addFormException(new DropletException(getMessageHandler().getErrMsg(
                            BBBCoreConstants.ERR_CC_NUMBER_EMPTY, pRequest.getLocale().getLanguage(), this.errorMap,
                            null)));
                } else if (propertyName.equals(BBBCoreConstants.CC_TYPE)) {
                    this.errorMap.put(BBBCoreConstants.MSG_CC_TYPE_EMPTY, BBBCoreConstants.ERR_CC_TYPE_EMPTY);
                    this.addFormException(new DropletException(getMessageHandler()
                            .getErrMsg(BBBCoreConstants.ERR_CC_TYPE_EMPTY, pRequest.getLocale().getLanguage(),
                                    this.errorMap, null)));
                } else if (propertyName.equals(BBBCoreConstants.CC_NOC)) {
                    this.errorMap.put(BBBCoreConstants.MSG_CC_NOC_EMPTY, BBBCoreConstants.ERR_CC_NOC_EMPTY);
                    this.addFormException(new DropletException(getMessageHandler().getErrMsg(
                            BBBCoreConstants.ERR_CC_NOC_EMPTY, pRequest.getLocale().getLanguage(), this.errorMap, null)));
                } else if (propertyName.equals(BBBCoreConstants.CC_EXP_MONTH)) {
                    this.errorMap.put(BBBCoreConstants.MSG_CC_EXP_MONTH_EMPTY, BBBCoreConstants.ERR_CC_EXP_MONTH_EMPTY);
                    this.addFormException(new DropletException(getMessageHandler().getErrMsg(
                            BBBCoreConstants.ERR_CC_EXP_MONTH_EMPTY, pRequest.getLocale().getLanguage(), this.errorMap,
                            null)));
                } else if (propertyName.equals(BBBCoreConstants.CC_EXP_YEAR)) {
                    this.errorMap.put(BBBCoreConstants.MSG_CC_EXP_YEAR_EMPTY, BBBCoreConstants.ERR_CC_EXP_YEAR_EMPTY);
                    this.addFormException(new DropletException(getMessageHandler().getErrMsg(
                            BBBCoreConstants.ERR_CC_EXP_YEAR_EMPTY, pRequest.getLocale().getLanguage(), this.errorMap,
                            null)));
                }
                missingFields = true;
            } else {
                if (propertyName.equals(BBBCoreConstants.CC_NUMBER)) {
                    if (!BBBUtility.isStringLengthValid((String) property, BBBCoreConstants.FIFTEEN,
                            BBBCoreConstants.TWENTY)) {
                        this.addFormException(new DropletException(BBBCoreConstants.ERR_CC_NUMBER_LENGTH_INCORRECT,
                                BBBCoreConstants.ERR_CC_NUMBER_LENGTH_INCORRECT));
                    } else if (!BBBUtility.isNumericOnly((String) property)) {
                        this.addFormException(new DropletException(BBBCoreConstants.ERR_CC_NUMBER_LENGTH_INCORRECT,
                                BBBCoreConstants.ERR_CC_NUMBER_LENGTH_INCORRECT));
                    }

                } else if (propertyName.equals(BBBCoreConstants.CC_NOC)
                        && !BBBUtility.isStringLengthValid((String) property, BBBCoreConstants.FOUR,
                                BBBCoreConstants.SIXTYONE)) {
                    this.addFormException(new DropletException(BBBCoreConstants.ERR_CC_NOC_LENGTH_INCORRECT,
                            BBBCoreConstants.ERR_CC_NOC_LENGTH_INCORRECT));
                } else if (propertyName.equals(BBBCoreConstants.CC_NOC)
                        && !BBBUtility.isValidCreditCardName((String) property)) {
                    this.addFormException((new DropletException(BBBCoreConstants.ERR_CHECKOUT_INVALID_CARD_NAME,
                            BBBCoreConstants.ERR_CHECKOUT_INVALID_CARD_NAME)));

                } else if (propertyName.equals(BBBCoreConstants.CC_NOC)
                        && BBBUtility.isCrossSiteScripting((String) property)) {
                    this.addFormException(new DropletException(BBBCoreConstants.ERROR_INVALID_CARD_NAME,
                            BBBCoreConstants.ERROR_INVALID_CARD_NAME));
                }
            }
        }

        this.logDebug("BBBProfileFormHandler.validateCreditCardFields() method ends");

        return !missingFields;
    }

    /** This method is overridden and performs the following 1> validates credit card fields UC_CC_WALLET
     * 
     * @param pRequest
     * @param pResponse
     * @return boolean */

    private boolean validateBillingAddressFields(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) {

        this.logDebug("BBBProfileFormHandler.validateBillingAddressFields() method starts");

        final Map<?, ?> newAddress = this.getBillAddrValue();
        final Iterator<String> addressPropertyIterator = this.getRequiredBillingAddressPropertyList().iterator();
        boolean missingFields = false;
        Object property = null;
        String propertyName = null;

        // Check to see all the address fields are entered
        while (addressPropertyIterator.hasNext()) {
            propertyName = addressPropertyIterator.next();
            property = newAddress.get(propertyName);

            if ((propertyName != null) && (property != null)) {

                if (propertyName.equals(BBBCoreConstants.CC_FIRST_NAME)
                        && !BBBUtility.isValidFirstName(property.toString())) {
                    missingFields = true;
                    this.errorMap.put(BBBCoreConstants.MSG_EMPTY_CC_FIRST_NAME, BBBCoreConstants.ERR_PROFILE_FIRSTNAME);
                    this.addFormException(new DropletException(BBBCoreConstants.ERR_PROFILE_FIRSTNAME,
                            BBBCoreConstants.ERR_PROFILE_FIRSTNAME));
                }

                if (propertyName.equals(BBBCoreConstants.CC_LAST_NAME)
                        && !BBBUtility.isValidLastName(property.toString())) {
                    missingFields = true;
                    this.errorMap.put(BBBCoreConstants.MSG_EMPTY_CC_LAST_NAME, BBBCoreConstants.ERR_CC_LAST_NAME_EMPTY);
                    this.addFormException(new DropletException(BBBCoreConstants.ERR_CC_LAST_NAME_EMPTY,
                            BBBCoreConstants.ERR_CC_LAST_NAME_EMPTY));
                }

                if (propertyName.equals(BBBCoreConstants.CC_ADDRESS1)
                        && (!BBBUtility.isValidAddressLine1(property.toString()))) {
                    missingFields = true;
                    this.errorMap.put(BBBCoreConstants.MSG_EMPTY_CC_ADDRESS1, BBBCoreConstants.ERR_CC_ADDR1_EMPTY);
                    this.addFormException(new DropletException(BBBCoreConstants.ERR_CC_ADDR1_EMPTY,
                            BBBCoreConstants.ERR_CC_ADDR1_EMPTY));
                }

                if (propertyName.equals(BBBCoreConstants.CC_ADDRESS2)
                        && !BBBUtility.isValidAddressLine2(property.toString())) {
                    this.errorMap.put(BBBCoreConstants.MSG_EMPTY_CC_ADDRESS2, BBBCoreConstants.ERR_CC_ADDR2_EMPTY);
                    this.addFormException(new DropletException(BBBCoreConstants.ERR_CC_ADDR2_EMPTY,
                            BBBCoreConstants.ERR_CC_ADDR2_EMPTY));
                }

                if (propertyName.equals(BBBCoreConstants.CC_STATE) && (0 < property.toString().length())) {
                    missingFields = true;
                    this.errorMap.put(BBBCoreConstants.MSG_EMPTY_CC_STATE, BBBCoreConstants.ERR_CC_STATE_EMPTY);
                    this.addFormException(new DropletException(BBBCoreConstants.ERR_CC_STATE_EMPTY,
                            BBBCoreConstants.ERR_CC_STATE_EMPTY));
                }

                if (propertyName.equals(BBBCoreConstants.CC_CITY) && !BBBUtility.isValidCity(property.toString())) {
                    missingFields = true;
                    this.errorMap.put(BBBCoreConstants.MSG_EMPTY_CC_CITY, BBBCoreConstants.ERR_CC_CITY_EMPTY);
                    this.addFormException(new DropletException(BBBCoreConstants.ERR_CC_CITY_EMPTY,
                            BBBCoreConstants.ERR_CC_CITY_EMPTY));
                }

                if (propertyName.equals(BBBCoreConstants.CC_POSTAL_CODE) && !BBBUtility.isValidZip(property.toString())) {
                    missingFields = true;
                    this.errorMap.put(BBBCoreConstants.MSG_EMPTY_CC_POSTAL_CODE, BBBCoreConstants.ERR_CC_ZIP_EMPTY);
                    this.addFormException(new DropletException(BBBCoreConstants.ERR_CC_ZIP_EMPTY,
                            BBBCoreConstants.ERR_CC_ZIP_EMPTY));
                }

                if (propertyName.equals(BBBCoreConstants.CC_COMPANY_NAME) && !BBBUtility.isEmpty(property.toString())
                        && !BBBUtility.isValidCompanyName(property.toString())) {
                    this.errorMap.put(BBBCoreConstants.MSG_COMPANY_LENGTH_INCORRECT,
                            BBBCoreConstants.ERR_COMPANY_LENGTH_INCORRECT);
                    this.addFormException(new DropletException(BBBCoreConstants.LENGTHINCORRECT + property,
                            BBBCoreConstants.ERR_COMPANY_LENGTH_INCORRECT));
                }

            }

        }
        final String billingAddressNickname = (String) newAddress.get(this.getShippingAddressNicknameMapKey());
        @SuppressWarnings ("unchecked")
        final Map<String, Object> secondaryAddresses = (Map<String, Object>) this.getProfile().getPropertyValue(
                getPropertyManager().getSecondaryAddressPropertyName());
        final boolean duplicateNickname = secondaryAddresses.keySet().contains(billingAddressNickname);
        if (duplicateNickname) {
            this.addFormException(new DropletException("MSG_DUPLICATE_ADDRESS_NICKNAME",
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1292));
            return false;
        }

        this.logDebug("BBBProfileFormHandler.validateBillingAddressFields() method ends");

        return !(missingFields || duplicateNickname);
    }

    /** This method is overridden and performs the following 1> validates credit card fields UC_CC_WALLET
     * 
     * @param pRequest
     * @param pResponse
     * @return boolean */
    private boolean validateCreditCard(final Map<String, ?> pCard) {

        this.logDebug("BBBProfileFormHandler.validateCreditCard() method starts");

        final ExtendableCreditCardTools cardTools = getCreditCardTools();
        final BasicBBBCreditCardInfo ccInfo = new BasicBBBCreditCardInfo();
        ccInfo.setExpirationYear((String) pCard.get(getPropertyManager().getCreditCardExpirationYearPropertyName()));
        ccInfo.setExpirationMonth((String) pCard.get(getPropertyManager().getCreditCardExpirationMonthPropertyName()));
        String ccNumber = (String) pCard.get(getPropertyManager().getCreditCardNumberPropertyName());

        if (ccNumber != null) {
            ccNumber = StringUtils.removeWhiteSpace(ccNumber);
        }
        ccInfo.setCreditCardNumber(ccNumber);
        ccInfo.setCreditCardType((String) pCard.get(getPropertyManager().getCreditCardTypePropertyName()));
        final int ccreturn = cardTools.verifyCreditCard(ccInfo);

        this.logDebug("BBBProfileFormHandler.validateCreditCard() method ccreturn" + ccreturn);

        if (ccreturn != ExtendableCreditCardTools.SUCCESS) {
            String msg = "";
            msg = cardTools.getStatusCodeMessage(ccreturn);
            this.addFormException(new DropletException(msg, BBBCoreErrorConstants.ACCOUNT_ERROR_1293));

            this.logDebug("BBBProfileFormHandler.validateCreditCard() validateCreditCard dollar auth ends false."
                    + " ccreturn | msg | map" + ccreturn + "|" + msg + "|" + this.errorMap);

            return BBBCoreConstants.RETURN_FALSE;
        }

        this.logDebug("BBBProfileFormHandler.validateCreditCard() validateCreditCard dollar auth ends true");

        this.logDebug("BBBProfileFormHandler.validateCreditCard() method ends");

        return BBBCoreConstants.RETURN_TRUE;
    }

    /** This method is overridden and performs the following 1> UC_CC_WALLET
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse */
   /* private void preUpdateCard(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {

        this.logDebug("BBBProfileFormHandler.preUpdateCard() method ends");

        final RepositoryItem cardToUpdate = this.findCurrentCreditCard();
        for (final String propertyName : getImmutableCardProperties()) {
            this.getEditValue().put(propertyName, cardToUpdate.getPropertyValue(propertyName));
        }

        this.logDebug("BBBProfileFormHandler.preUpdateCard() method ends");

    }*/

    /** This method is overridden and performs the following 1> Searches current user's credit card by nick-name from
     * editValue properties. 2> credit card if found UC_CC_WALLET
     * 
     * @param pRequest
     * @param pResponse
     * @return RepositoryItem */
    private RepositoryItem findCurrentCreditCard() {

        this.logDebug("BBBProfileFormHandler.findCurrentCreditCard() method starts");

        final BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();
        final String cardNickname = (String) this.getEditValue().get(this.getNicknameValueMapKey());

        this.logDebug("BBBProfileFormHandler.findCurrentCreditCard() method cardNickname" + cardNickname);

        final RepositoryItem cardToUpdate = profileTools.getCreditCardByNickname(cardNickname, this.getProfile());

        this.logDebug("BBBProfileFormHandler.findCurrentCreditCard() method ends");

        return cardToUpdate;
    }

    /** Updates the profile information as modified by the user.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleUpdateProfile(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        return this.handleUpdate(pRequest, pResponse);

    }

    /** Updates the credit card as modified by the user.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleUpdateCreditCardToProfile(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        return this.handleUpdateCard(pRequest, pResponse);

    }

    /** Updates the credit card as modified by the user.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleUpdateCard(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.handleUpdateCard() method starts");
        // BBBH-391 | Client DOM XSRF changes
        if(StringUtils.isNotEmpty(getFromPage())){
			setUpdateCardSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(BBBURLConstants.UPDATE_CARD));
			setUpdateCardErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(BBBURLConstants.UPDATE_CARD));
		}
        // validate credit card information
        if (!this.validateUpdateCreditCardInformation(pRequest, pResponse)) {
            return this.checkFormRedirect(null, this.getUpdateCardErrorURL(), pRequest, pResponse);
        }
        // commenting below line for defect fix 10469
        // preUpdateCard(pRequest, pResponse);
        final TransactionManager tm = this.getTransactionManager();
        final TransactionDemarcation td = this.getTransactionDemarcation();
        final BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();
        @SuppressWarnings ("unchecked")
        final List<String> addresses = profileTools.getAllShippingAddresses(this.getProfile());
        boolean makeBothDefault = false;
        if ((addresses == null) || addresses.isEmpty()) {
            makeBothDefault = true;
        }
        try {

            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRED);
            }
            final Profile profile = this.getProfile();
            final Map<String, Object> edit = this.getEditValue();
            final Map<String, String> billAddrValue = this.getBillAddrValue();
            this.logDebug("BBBProfileFormHandler.handleUpdateCard() method edit" + edit);
            this.logDebug("BBBProfileFormHandler.handleUpdateCard() method billAddrValue" + billAddrValue);
            final String newNickname = (String) edit.get(this.getNewNicknameValueMapKey());
            
            try {

                this.logDebug("Updating credit card properties");

                // Get credit card to update
                final RepositoryItem cardToUpdate = this.findCurrentCreditCard();
                // Update credit card

                updateDataInCard(profileTools, makeBothDefault, profile, edit, billAddrValue, newNickname,
						cardToUpdate);
                // save this card as default if needed
                processAndUpdateDefaultCard(profileTools, profile, edit, newNickname, cardToUpdate);
                updateDefaultBillAddrPostUpdateCard(profileTools, profile);
                
            } catch (final RepositoryException repositoryExc) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, REPOSITORY + EXCEPTION_HANDLE_UPDATE_CARD,
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1045), repositoryExc);
                this.logDebug("BBBProfileFormHandler.handleUpdateCard() method ends");
                return this.checkFormRedirect(null, this.getUpdateCardErrorURL(), pRequest, pResponse);
            } catch (final InstantiationException ex) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, INSTANTIATION + EXCEPTION_HANDLE_UPDATE_CARD,
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1046), ex);
                throw new ServletException(ex);
            } catch (final IllegalAccessException ex) {
                this.logError(LogMessageFormatter.formatMessage(pRequest,
                        ILLEGAL_ACCESS + EXCEPTION_HANDLE_UPDATE_CARD, BBBCoreErrorConstants.ACCOUNT_ERROR_1047), ex);
                throw new ServletException(ex);
            } catch (final ClassNotFoundException ex) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, CLASS_NOT_FOUND
                        + EXCEPTION_HANDLE_UPDATE_CARD, BBBCoreErrorConstants.ACCOUNT_ERROR_1048), ex);
                throw new ServletException(ex);
            } catch (final IntrospectionException ex) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, INTROSPECTION + EXCEPTION_HANDLE_UPDATE_CARD,
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1049), ex);
                throw new ServletException(ex);
            }
            billAddrValue.clear();
            edit.clear();
            return this.checkFormRedirect(this.getUpdateCardSuccessURL(), this.getUpdateCardErrorURL(), pRequest,
                    pResponse);
        } catch (final TransactionDemarcationException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                    + EXCEPTION_HANDLE_UPDATE_CARD, BBBCoreErrorConstants.ACCOUNT_ERROR_1050), e);
            throw new ServletException(e);
        } finally {
            try {
                if (tm != null) {
                    td.end();
                }
            } catch (final TransactionDemarcationException e) {
                this.logError(
                        LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                                + EXCEPTION_HANDLE_UPDATE_CARD, BBBCoreErrorConstants.ACCOUNT_ERROR_1051), e);
            }
        }
    }


	private void updateDefaultBillAddrPostUpdateCard(final BBBProfileTools profileTools, final Profile profile)
			throws RepositoryException {
		boolean isMakeBillingAddress = false;
		final String makeBilling = this.getBillAddrValue().get(BBBCoreConstants.MAKE_BILLING);
		if (isMakePreferredSet() || ((makeBilling != null) && makeBilling.equals(BBBCoreConstants.TRUE))) {
		    isMakeBillingAddress = true;
		}

		if (isMakeBillingAddress) {
		    profileTools.setDefaultBillingAddress(profile,
		            this.getBillAddrValue().get(BBBCoreConstants.NEW_NICKNAME).toString().trim());
		}
	}


	private void processAndUpdateDefaultCard(final BBBProfileTools profileTools, final Profile profile,
			final Map<String, Object> edit, final String newNickname, final RepositoryItem cardToUpdate)
					throws RepositoryException {
		final String newCreditCard = (String) edit.get(getPropertyManager().getNewCreditCard());
		if (!StringUtils.isEmpty(newCreditCard) && newCreditCard.equalsIgnoreCase(BBBCoreConstants.TRUE)) {
		    profileTools.setDefaultCreditCard(profile, newNickname);
		} else if ((BBBCoreConstants.FALSE).equalsIgnoreCase(newCreditCard)) {
		    // current card should not be default
		    final RepositoryItem defaultCreditCard = profileTools.getDefaultCreditCard(profile);
		    if ((defaultCreditCard != null)
		            && cardToUpdate.getRepositoryId().equals(defaultCreditCard.getRepositoryId())) {
		        // current card is default, make it not to be
		        profileTools.updateProperty(getPropertyManager().getDefaultCreditCardPropertyName(), null,
		                profile);
		        // otherwise we shouldn't change anything more in the
		        // profile
		    }
		}
	}


	private void updateDataInCard(final BBBProfileTools profileTools, boolean makeBothDefault, final Profile profile,
			final Map<String, Object> edit, final Map<String, String> billAddrValue, final String newNickname,
			final RepositoryItem cardToUpdate) throws RepositoryException, InstantiationException,
					IllegalAccessException, ClassNotFoundException, IntrospectionException {
		if ((!StringUtils.isEmpty(billAddrValue.get(BBBCoreConstants.CC_FIRST_NAME).toString()))
		        && billAddrValue.get(BBBCoreConstants.NEW_NICKNAME).toString()
		        .equals(BBBCoreConstants.CC_NEW_BILLING_ADDR)) {

		    final BBBAddressObject addressObject = populateAddressObjectUpdateCard(billAddrValue);

		    final String nickNameNewAdr = ((CommerceProfileTools) this.getProfileTools())
		            .getUniqueShippingAddressNickname(addressObject, profile, null);
		    ((CommerceProfileTools) this.getProfileTools()).createProfileRepositorySecondaryAddress(profile,
		            nickNameNewAdr, addressObject);
		    billAddrValue.put(BBBCoreConstants.NEW_NICKNAME, nickNameNewAdr);
		    profileTools.updateProfileCreditCard(cardToUpdate, profile, edit, newNickname, billAddrValue,
		            profileTools.getBillingAddressClassName());
		    if (makeBothDefault) {
		        ((BBBProfileTools) this.getProfileTools()).setDefaultBillingAddress(profile, nickNameNewAdr);
		        ((BBBProfileTools) this.getProfileTools()).setDefaultShippingAddress(profile, nickNameNewAdr);
		    } else if (this.isMakePreferredSet()) {
		        ((BBBProfileTools) this.getProfileTools()).setDefaultBillingAddress(profile, nickNameNewAdr);
		    }
		} else {
		    profileTools.updateProfileCreditCard(cardToUpdate, profile, edit, newNickname, billAddrValue,
		            profileTools.getBillingAddressClassName());
		}
	}


	private BBBAddressObject populateAddressObjectUpdateCard(final Map<String, String> billAddrValue) {
		final BBBAddressObject addressObject = new BBBAddressObject();
		addressObject.setFirstName(billAddrValue.get(BBBCoreConstants.CC_FIRST_NAME).toString());
		addressObject.setLastName(billAddrValue.get(BBBCoreConstants.CC_LAST_NAME).toString());
		addressObject.setCompanyName(billAddrValue.get(BBBCoreConstants.CC_COMPANY_NAME).toString());
		addressObject.setAddress1(billAddrValue.get(BBBCoreConstants.CC_ADDRESS1).toString());
		addressObject.setAddress2(billAddrValue.get(BBBCoreConstants.CC_ADDRESS2).toString());

		addressObject.setCity(billAddrValue.get(BBBCoreConstants.CC_CITY).toString());
		addressObject.setState(billAddrValue.get(BBBCoreConstants.CC_STATE).toString());
		addressObject.setCountry(billAddrValue.get(BBBCoreConstants.CC_COUNTRY).toString());
		addressObject.setPostalCode(billAddrValue.get(BBBCoreConstants.CC_POSTAL_CODE).toString());
		return addressObject;
	}

    /** Validates updated credit card information entered by user: <li>country/state combination is valid for billing
     * address <li>card expiry date are valid <li>not duplicate credit card nickname is used </ul>
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    private boolean validateUpdateCreditCardInformation(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.validateUpdateCreditCardInformation() method starts");

        // return false if there were missing required properties
        if (this.getFormError()) {
            return BBBCoreConstants.RETURN_FALSE;
        }

        final Map<String, Object> card = this.getEditValue();

        if (!this.validateCreditCardFields(pRequest, pResponse)) {
            return this.checkFormRedirect(null, this.getUpdateCardErrorURL(), pRequest, pResponse);
        }

        this.logDebug("BBBProfileFormHandler.validateUpdateCreditCardInformation() method chk this" + card);

        if (!this.validateCreditCard(card)) {
            return BBBCoreConstants.RETURN_FALSE;
        }
        if (this.getFormError()) {
            return BBBCoreConstants.RETURN_FALSE;
        }

        this.logDebug("BBBProfileFormHandler.validateUpdateCreditCardInformation() method ends");

        return BBBCoreConstants.RETURN_TRUE;
    }

    /** Removes specified in <code>removeCard</code> property credit card from user's credit cards map.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleRemoveCard(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.handleRemoveCard() method starts");

        final TransactionManager tm = this.getTransactionManager();
        final TransactionDemarcation td = this.getTransactionDemarcation();
        final BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();
        final String cardNickname = getRemoveCard();
        if (StringUtils.isBlank(cardNickname)) {

            this.logDebug("A null or empty nickname was provided to handleRemoveAddress");
            this.addFormException(new DropletException("Missing mandatory parameter nickname",
                    BBBCoreErrorConstants.ERROR_EMPTY_NICKNAME));
            // if no nickname provided, do nothing.
            return BBBCoreConstants.RETURN_TRUE;
        }
        try {
            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRED);
            }
            final Profile profile = this.getProfile();
            // remove credit card from user's credit cards map
            profileTools.removeProfileCreditCard(profile, cardNickname);
            this.handleMakeDefaultCreditCard(pRequest, pResponse);
            return BBBCoreConstants.RETURN_TRUE;
        } catch (final TransactionDemarcationException exception) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                    + EXCEPTION_HANDLE_REMOVE_CARD, BBBCoreErrorConstants.ACCOUNT_ERROR_1052), exception);
            throw new ServletException(exception);
        } catch (final RepositoryException exception) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, REPOSITORY + EXCEPTION_HANDLE_REMOVE_CARD,
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1053), exception);
        } finally {
            try {
                if (tm != null) {
                    td.end();
                }
            } catch (final TransactionDemarcationException exception) {
                this.logError(
                        LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                                + EXCEPTION_HANDLE_REMOVE_CARD, BBBCoreErrorConstants.ACCOUNT_ERROR_1054), exception);
            }
        }

        this.logDebug("BBBProfileFormHandler.handleRemoveCard() method ends");

        return BBBCoreConstants.RETURN_FALSE;
    }

    /** Loads the Edit value Map.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleEditCard(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        final TransactionManager tm = this.getTransactionManager();
        final TransactionDemarcation td = this.getTransactionDemarcation();

        try {
            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRED);
            }

            // the nickname of the credit card we want to edit
            final String targetCard = this.getEditCard();

            if ((targetCard == null) || (targetCard.trim().length() == 0)) {
                // we should only get here through a hyperlink that supplies the
                // secondary credit card nickname. Just in case, exit quietly.
                return BBBCoreConstants.RETURN_TRUE;
            }

            if (!this.getFormError()) {
                final Profile profile = this.getProfile();
                @SuppressWarnings ("unchecked")
                final Map<String, MutableRepositoryItem> creditCards = (Map<String, MutableRepositoryItem>) profile
                .getPropertyValue(getPropertyManager().getCreditCardPropertyName());
                final MutableRepositoryItem card = creditCards.get(targetCard);
                // MutableRepositoryItem cardAddress = (MutableRepositoryItem)
                // card.getPropertyValue(propertyManager.getBillingAddressPropertyName());
                final Map<String, Object> edit = this.getEditValue();
                // Map billAddrMap = getBillAddrValue();
                // record the nickname
                edit.put(this.getNicknameValueMapKey(), targetCard);
                edit.put(this.getNewNicknameValueMapKey(), targetCard);
                final String[] cardProps = this.getCardProperties();
                // copy each property to the map
                Object property;
                for (final String cardProp : cardProps) {
                    property = card.getPropertyValue(cardProp);
                    if (property != null) {
                        edit.put(cardProp, property);
                    }
                }
                // now copy billing address properties
                property = null;
            }
            return this.checkFormRedirect(this.getUpdateCardSuccessURL(), this.getUpdateCardErrorURL(), pRequest,
                    pResponse);
        } catch (final TransactionDemarcationException exception) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                    + EXCEPTION_HANDLE_EDIT_CARD, BBBCoreErrorConstants.ACCOUNT_ERROR_1056), exception);
            throw new ServletException(exception);
        } finally {
            try {
                if (tm != null) {
                    td.end();
                }
            } catch (final TransactionDemarcationException exception) {
                this.logError(
                        LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                                + EXCEPTION_HANDLE_EDIT_CARD, BBBCoreErrorConstants.ACCOUNT_ERROR_1056), exception);
            }
        }
    }

    /** Creates a new shipping address using the entries entered in the editValue map. The address will be indexed using
     * the nickname provided by the user.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleNewAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.handleNewAddress() method started");

        this.createNewAddress(pRequest, pResponse);

        this.logDebug("BBBProfileFormHandler.handleNewAddress() method end");

        if (this.getFormExceptions().size() > 0) {
            return this.checkFormRedirect(null, this.getNewAddressErrorURL(), pRequest, pResponse);
        }
        return this.checkFormRedirect(pRequest.getRequestURI(), pRequest.getRequestURI(), pRequest, pResponse);

    }

    /** Creates a new shipping address using the entries entered in the editValue map. The address will be indexed using
     * the nickname provided by the user.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleAddNewProfileAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.handleAddNewProfileAddress() method started");

        this.createNewAddress(pRequest, pResponse);

        this.logDebug("BBBProfileFormHandler.handleAddNewProfileAddress() method end");

        if (this.getFormExceptions().size() > 0) {
            return false;
        }
        return true;

    }
    

    /** Creates a new shipping address using the entries entered in the editValue map. The address will be indexed using
     * the nickname provided by the user.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    private boolean createNewAddress(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.createNewAddress() method started");

        // Validate address data entered by user
        final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
        if (!this.validateAddress(pRequest, pResponse, errorPlaceHolderMap)) {
            return this.checkFormRedirect(null, this.getNewAddressErrorURL(), pRequest, pResponse);
        }

        // Profile
        final Profile profile = this.getProfile();
        final BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();
        final List addresses = profileTools.getAllAvailableAddresses(this.getProfile());
        if ((addresses == null) || addresses.isEmpty()) {
            this.setUseShippingAddressAsDefault(true);
            this.setUseBillingAddressAsDefault(true);
            this.setUseMailingAddressAsDefault(true);
        }
        // Get editValue map, containing the user form data
        final Map<String, Object> newAddress = this.getEditValue();
        final String country = (String) getSiteContext().getSite().getPropertyValue(BBBCoreConstants.DEFAULT_COUNTRY);
        if (!StringUtils.isBlank(country)) {
            newAddress.put(BBBCoreConstants.CC_COUNTRY, country);
        }
       
        //BSL-4694 | Storing mobile number while doing SPC in Profile
		final String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
        if ((channel != null)
                && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel
                        .equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))){
        	final String phoneNumber = (String)newAddress.get(BBBCoreConstants.PHONENUMBER);
        	updatePhoneNumberMob(pRequest, profileTools, phoneNumber);
        }
        // QAS changes
    	newAddress.put(QAS_POBOXADDRESS, false);
    	newAddress.put(QAS_VALIDATED, false);
        if(newAddress.get("poBoxStatus")!=null && ((String)newAddress.get("poBoxStatus")).equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
        	newAddress.put(QAS_VALIDATED, true);
        if(newAddress.get("poBoxFlag") !=null && ((String)newAddress.get("poBoxFlag")).equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
        	newAddress.put(QAS_POBOXADDRESS, true);
        newAddress.remove("poBoxStatus");
        newAddress.remove("poBoxFlag");

        // Generate unique nickname if it is not provided by user
        String nickname = null;
        nickname = (String) newAddress.get(this.getNicknameValueMapKey());
        if (StringUtils.isBlank(nickname)) {
            nickname = profileTools.getUniqueShippingAddressNickname(newAddress, profile, null);
        }

        try {
            getProfileManager().addAddressForProfile(profile, newAddress, this.isUseShippingAddressAsDefault(),
                    this.isUseBillingAddressAsDefault(),this.isUseMailingAddressAsDefault(), this.getNicknameValueMapKey());
        } catch (final BBBSystemException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreConstants.ERR_ADDRESSBOOK_ADDITIONERROR,
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1105), e);
            this.addFormException(new DropletException(getMessageHandler().getErrMsg(
                    BBBCoreConstants.ERR_ADDRESSBOOK_ADDITIONERROR, pRequest.getLocale().getLanguage(),
                    errorPlaceHolderMap, null), BBBCoreConstants.ERR_ADDRESSBOOK_ADDITIONERROR));
            // failure - redirect if an error URL was specified
            return this.checkFormRedirect(null, this.getNewAddressErrorURL(), pRequest, pResponse);
        }
        // empty out the map
        newAddress.clear();
        //this.addressAdded = BBBCoreConstants.TRUE;
        setAddressAdded(BBBCoreConstants.TRUE);

        this.logDebug("BBBProfileFormHandler.createNewAddress() method end");

        return true;
    }

    
    /**
     * 
     * Updates phonenumber in profile when we enter the same during SPC on mobile
     * @param pRequest
     * @param profileTools
     * @param phoneNumber
     */
	protected void updatePhoneNumberMob(
			final DynamoHttpServletRequest pRequest,
			final BBBProfileTools profileTools, final String phoneNumber) {
		this.errorMap = new HashMap<String, String>();
		String errorMessage;
        if (BBBUtility.isValidPhoneNumber(phoneNumber)) {
            final String mobileNumberProperty = getPropertyManager().getMobileNumberPropertyName();
        	try {
				profileTools.updateProperty(mobileNumberProperty, phoneNumber, this.getProfile());
        	}
	             catch (final RepositoryException re) {
	                 this.logError(LogMessageFormatter.formatMessage(pRequest,
	                         REPOSITORY + EXCEPTION_ERROR_IN_HANDLE_UPDATE, BBBCoreErrorConstants.ACCOUNT_ERROR_1001),
	                         re);
			}
        }
	}

    /** Validates new address fields entered by user:
     * <ul>
     * <li>all required fields are specified for new address
     * <li>country/state combination is valid for new address
     * <li>not duplicate address nickname is used for create address or update address operation
     * </ul>
     * 
     * @param pRequest http request
     * @param pResponse http response
     * @return true is validation succeeded
     * @throws ServletException
     * @throws IOException */
    private boolean validateAddress(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse,
            final Map<String, String> errorPlaceHolderMap) throws ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.validateAddress() method started");

        final String siteId = SiteContextManager.getCurrentSiteId();
        String errorMessage;
        final Map<String, Object> newAddress = this.getEditValue();

        if (newAddress != null) {
            final String firstName = (String) newAddress.get(FIRST_NAME);

            if (!BBBUtility.isValidFirstName(firstName)) {
                this.logError("First name field validation : failed");
                errorPlaceHolderMap.put(FIELD_NAME, FIRST_NAME2);
                errorMessage = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_INVALID,
                        pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null);
                this.addFormException(new DropletException(errorMessage, BBBCoreErrorConstants.ACCOUNT_ERROR_1294));
            }

            final String lastName = (String) newAddress.get(LAST_NAME);
            if (!BBBUtility.isValidLastName(lastName)) {
                this.logError("Last name field validation : failed");
                errorPlaceHolderMap.put(FIELD_NAME, LAST_NAME2);
                errorMessage = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_INVALID,
                        pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null);
                this.addFormException(new DropletException(errorMessage, BBBCoreErrorConstants.ACCOUNT_ERROR_1295));
            }

            final String address1 = (String) newAddress.get(ADDRESS_1);
            if (!BBBUtility.isValidAddressLine1(address1)) {
                this.logError("address1 field validation : failed");
                errorPlaceHolderMap.put(FIELD_NAME, "Address 1");
                errorMessage = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_INVALID,
                        pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null);
                this.addFormException(new DropletException(errorMessage, BBBCoreErrorConstants.ACCOUNT_ERROR_1296));
            }

            final String address2 = (String) newAddress.get(ADDRESS_2);
            if (!BBBUtility.isEmpty(address2) && (!BBBUtility.isValidAddressLine2(address2))) {
                this.logError("Address 2 field validation : failed");
                errorPlaceHolderMap.put(FIELD_NAME, "Address 2");
                errorMessage = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_INVALID,
                        pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null);
                this.addFormException(new DropletException(errorMessage, BBBCoreErrorConstants.ACCOUNT_ERROR_1297));
            }

            final String city = (String) newAddress.get(CITY);
            if (!BBBUtility.isValidCity(city)) {
                this.logError("City field validation : failed");
                errorPlaceHolderMap.put(FIELD_NAME, "City");
                errorMessage = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_INVALID,
                        pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null);
                this.addFormException(new DropletException(errorMessage, BBBCoreErrorConstants.ACCOUNT_ERROR_1298));
            }

            final String state = (String) newAddress.get(STATE);
            if (BBBCoreConstants.BLANK.equals(state)) {
                this.logError("State field validation : failed");
                errorPlaceHolderMap.put(FIELD_NAME, "State");
                errorMessage = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_INVALID,
                        pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null);
                this.addFormException(new DropletException(errorMessage, BBBCoreErrorConstants.ACCOUNT_ERROR_1299));
            }

            if (!newAddress.containsKey(POSTAL_CODE)) {
                String zipCode;
                if (siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)) {
                    zipCode = pRequest.getPostParameter("zipCA");
                    newAddress.put(POSTAL_CODE, zipCode);
                } else {
                    zipCode = pRequest.getPostParameter("zipUS");
                    newAddress.put(POSTAL_CODE, zipCode);
                }
            }

            final String postalCode = (String) newAddress.get(POSTAL_CODE);
            this.logDebug("Zip code : " + postalCode);
            if (!BBBUtility.isValidZip(postalCode)) {
                this.logError("postal code field validation : failed : " + postalCode);
                errorPlaceHolderMap.put(FIELD_NAME, "Zip Code");
                errorMessage = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_INVALID,
                        pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null);
                this.addFormException(new DropletException(errorMessage, BBBCoreErrorConstants.ACCOUNT_ERROR_1300));
            }

            final String companyName = (String) newAddress.get(COMPANY_NAME);
            if("undefined".equalsIgnoreCase(companyName)){
            	newAddress.put(COMPANY_NAME,null);
            }
            if (!BBBUtility.isEmpty(companyName) && !BBBUtility.isValidCompanyName(companyName)) {
                this.logError("companyName field validation : failed");
                errorPlaceHolderMap.put(FIELD_NAME, "Company Name");
                errorMessage = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_PROFILE_INVALID,
                        pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null);
                this.addFormException(new DropletException(errorMessage, BBBCoreErrorConstants.ACCOUNT_ERROR_1301));
            }

            // Validate address nickname or new nickname if it's update address
            // operation
            final String nickname = (String) newAddress.get(this.getNicknameValueMapKey());
            final String newNickname = (String) newAddress.get(this.getNewNicknameValueMapKey());
            if (!StringUtils.isBlank(newNickname)) {

                // It's not new address but update address operation So validate
                // only new nickname
                if (!newNickname.equals(nickname)) {
                    final List<String> ignoreList = new ArrayList<String>();
                    ignoreList.add(nickname);
                    this.checkForDuplicateAddress(pRequest, pResponse, newNickname, ignoreList);
                }
            } else {

                // It's new address so validate nickname
                if (!StringUtils.isBlank(nickname)) {
                    this.checkForDuplicateAddress(pRequest, pResponse, nickname, null);
                }
            }
        } else {
            this.addFormException(new DropletException(BBBCoreConstants.ERR_PROFILE_INVALID,
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1302));
        }
        if (this.getFormError()) {
            this.logError("Validate address() method fails");
            return BBBCoreConstants.RETURN_FALSE;
        }

        this.logDebug("BBBProfileFormHandler.validateAddress() method end");

        return BBBCoreConstants.RETURN_TRUE;
    }

    /** Checks if the given nickname already exists in the secondary address map. If it does a form error is added.
     * 
     * @param pRequest http request
     * @param pResponse http response
     * @param pProfile profile
     * @param pNickname nickname
     * @param pIgnoredNames names we wont check for duplicates */
    private void checkForDuplicateAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse, final String pNickname, final List<String> pIgnoredNames) {
        // Get the current nicknames
        final Profile profile = this.getProfile();
        final BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();

        final List<String> profileNickNames = new ArrayList<String>();
        profileNickNames.addAll(profileTools.getProfileAddressNames(profile));

        // Remove the names we want to ignore
        if (pIgnoredNames != null) {
            profileNickNames.removeAll(pIgnoredNames);
        }

        // Check for duplicates
        for (final String profileNickName : profileNickNames) {
            if (profileNickName.equalsIgnoreCase(pNickname)) {
                return;
            }
        }
    }

    /** Update the secondary address as modified by the user.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleUpdateAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug(HANDLE_UPDATE_ADDRESS_METHOD + START);

        final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
        // Validate address data entered by user
        if (!this.validateAddress(pRequest, pResponse, errorPlaceHolderMap)) {
            this.logError("Return from handle update address method : Validate address method failed");
            return this.checkFormRedirect(null, this.getUpdateAddressErrorURL(), pRequest, pResponse);
        }

        this.logDebug("validateAddress() method passed");

        final Map<String, Object> edit = this.getEditValue();
        if (edit.get(BBBCoreConstants.CC_COUNTRY) == null) {
            edit.put(BBBCoreConstants.CC_COUNTRY,
                    getSiteContext().getSite().getPropertyValue(BBBCoreConstants.DEFAULT_COUNTRY));
        }

        // QAS changes
        edit.put(QAS_POBOXADDRESS, false);
        edit.put(QAS_VALIDATED, false);
        if(edit.get("poBoxStatus")!=null && ((String)edit.get("poBoxStatus")).equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
        	edit.put(QAS_VALIDATED, true);
        if(edit.get("poBoxFlag") !=null && ((String)edit.get("poBoxFlag")).equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
        	edit.put(QAS_POBOXADDRESS, true);
        edit.remove("poBoxStatus");
        edit.remove("poBoxFlag");

        try {
            getProfileManager().updateAddressForProfile(this.getProfile(), edit, this.isUseShippingAddressAsDefault(),
                    this.isUseBillingAddressAsDefault(), this.isUseMailingAddressAsDefault(),this.getNicknameValueMapKey(),
                    this.getNewNicknameValueMapKey());
        } catch (final BBBSystemException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreConstants.ERR_ADDRESSBOOK_UPDATIONERROR,
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1106), e);
            this.addFormException(new DropletException(getMessageHandler().getErrMsg(
                    BBBCoreConstants.ERR_ADDRESSBOOK_UPDATIONERROR, pRequest.getLocale().getLanguage(),
                    errorPlaceHolderMap, null), BBBCoreConstants.ERR_ADDRESSBOOK_UPDATIONERROR));

            return this.checkFormRedirect(null, this.getUpdateAddressErrorURL(), pRequest, pResponse);
        } catch (final TransactionDemarcationException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreConstants.ERR_ADDRESSBOOK_UPDATIONERROR,
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1106), e);
            this.addFormException(new DropletException(getMessageHandler().getErrMsg(
                    BBBCoreConstants.ERR_ADDRESSBOOK_UPDATIONERROR, pRequest.getLocale().getLanguage(),
                    errorPlaceHolderMap, null), BBBCoreConstants.ERR_ADDRESSBOOK_UPDATIONERROR));

            return this.checkFormRedirect(null, this.getUpdateAddressErrorURL(), pRequest, pResponse);
        }
        edit.clear();

        this.logDebug(HANDLE_UPDATE_ADDRESS_METHOD + END);

        return this.checkFormRedirect(this.getUpdateAddressSuccessURL(), this.getUpdateAddressErrorURL(), pRequest,
                pResponse);
    }

    /** This handler deletes a secondary address named in the removeAddress property.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */

    public final boolean handleRemoveAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug(HANDLE_REMOVE_ADDRESS_METHOD + START);

        this.genericRemoveAddress(pRequest, pResponse);

        this.logDebug(HANDLE_REMOVE_ADDRESS_METHOD + END);

        return this.checkFormRedirect(pRequest.getRequestURI(), pRequest.getRequestURI(), pRequest, pResponse);
    }

    /** This handler deletes a secondary address named in the removeAddressAPI property.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */

    public final boolean handleRemoveAddressAPI(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        this.logDebug("BBBProfileFormHandler.handleRemoveAddressAPI() method started");
        final String nickname = this.getAddressId();

        if ((nickname == null) || "".equals(nickname)) {

            this.logDebug("A null or empty addressId was provided to handleRemoveAddressAPI");
            this.addFormException(new DropletException(getMessageHandler().getErrMsg(
                    BBBCoreErrorConstants.ERROR_EMPTY_ADDRESSID, pRequest.getLocale().getLanguage(), null, null),
                    BBBCoreErrorConstants.ERROR_EMPTY_ADDRESSID));

        } else {
            this.genericRemoveAddress(pRequest, pResponse);
        }

        this.logDebug("BBBProfileFormHandler.handleRemoveAddressAPI() method ended");

        if (this.getFormExceptions().size() > 0) {
            return false;
        }
        return true;
    }

    /** This a generic function that contains remove address from user profile
     * 
     * @param pRequest the servlet's request
     * @param pResponse the servlet's response
     * @exception ServletException if there was an error while executing the code
     * @exception IOException if there was an error with servlet io */

    private void genericRemoveAddress(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

        this.logDebug(GENERIC_REMOVE_ADDRESS_METHOD + START);

        final String nickname = this.getAddressId();
        try {
            getProfileManager().removeAddressForProfile(this.getProfile(), nickname,
                    this.isUseShippingAddressAsDefault(), this.isUseBillingAddressAsDefault(), this.isUseMailingAddressAsDefault());
        } catch (final BBBSystemException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, BBB_SYSTEM + EXCEPTION_GENERIC_REMOVE_ADDRESS,
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1060), e);
            this.addFormException(new DropletException(BBBCoreConstants.ERR_ADDRESSBOOK_REMOVEADDRESS,
                    BBBCoreConstants.ERR_ADDRESSBOOK_REMOVEADDRESS));
        } catch (final TransactionDemarcationException transactionDemarcationException) {
            this.logError(
                    LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                            + EXCEPTION_GENERIC_REMOVE_ADDRESS, BBBCoreErrorConstants.ACCOUNT_ERROR_1061),
                            transactionDemarcationException);
            this.addFormException(new DropletException(BBBCoreConstants.ERR_ADDRESSBOOK_REMOVEADDRESS,
                    BBBCoreConstants.ERR_ADDRESSBOOK_REMOVEADDRESS));
        }

        this.logDebug(GENERIC_REMOVE_ADDRESS_METHOD + END);

    }

    /** This sets the default shipping address.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws RepositoryException
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleDefaultShippingAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws RepositoryException, ServletException, IOException {

        this.logDebug(HANDLE_DEFAULT_SHIPPING_ADDRESS_METHOD + START);
        this.updateDefaultShippingAddress(pRequest, pResponse);
        this.logDebug(HANDLE_DEFAULT_SHIPPING_ADDRESS_METHOD + END);

        return this.checkFormRedirect(pRequest.getRequestURI(), pRequest.getRequestURI(), pRequest, pResponse);
    }

    /** This generic code for default shipping address.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws RepositoryException
     * @throws IOException */

    private void updateDefaultShippingAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws RepositoryException, ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.updateDefaultShippingAddress() method started");

        final TransactionManager transactionManager = this.getTransactionManager();
        final TransactionDemarcation transactionDemarcation = this.getTransactionDemarcation();

        try {
            if (transactionManager != null) {
                transactionDemarcation.begin(transactionManager, TransactionDemarcation.REQUIRED);
            }

            final Profile profile = this.getProfile();
            final BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();
            final String addressNickname = this.getDefaultShippingAddress();

            if (StringUtils.isBlank(addressNickname)) {

                this.logDebug("A null or empty nickname was provided to handleDefaultShippingAddress");

                // if no nickname provided, do nothing.
            } else {

                // Set requested shipping address as default
                profileTools.setDefaultShippingAddress(profile, addressNickname);

            }
            this.logDebug("BBBProfileFormHandler.handleDefaultShippingAddress() method end");

        } catch (final TransactionDemarcationException transactionDemarcationException) {
            this.logError(
                    LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                            + EXCEPTION_HANDLE_DEFAULT_SHIPPING_ADDRESS, BBBCoreErrorConstants.ACCOUNT_ERROR_1062),
                            transactionDemarcationException);
            throw new ServletException(transactionDemarcationException);

        } finally {
            try {
                if (transactionManager != null) {
                    transactionDemarcation.end();
                }
            } catch (final TransactionDemarcationException demarcationException) {
                this.logError(
                        LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                                + EXCEPTION_HANDLE_DEFAULT_SHIPPING_ADDRESS, BBBCoreErrorConstants.ACCOUNT_ERROR_1063),
                                demarcationException);
            }
        }

    }
    
    /** This sets the default billing address.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws IOException
     * @throws ServletException
     * @throws RepositoryException */
    public final boolean handleDefaultMailingAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException, RepositoryException {

        this.logDebug(HANDLE_DEFAULT_BILLING_ADDRESS_METHOD + START);

        this.updateDefaultMailingAddress(pRequest, pResponse);

        this.logDebug(HANDLE_DEFAULT_BILLING_ADDRESS_METHOD + END);

        return this.checkFormRedirect(pRequest.getRequestURI(), pRequest.getRequestURI(), pRequest, pResponse);

    }
    
    
    /** This generic code for default mailing address.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws RepositoryException
     * @throws IOException */

    private void updateDefaultMailingAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws RepositoryException, ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.updateDefaultMailingAddress() method started");

        final TransactionManager transactionManager = this.getTransactionManager();
        final TransactionDemarcation transactionDemarcation = this.getTransactionDemarcation();

        try {
            if (transactionManager != null) {
                transactionDemarcation.begin(transactionManager, TransactionDemarcation.REQUIRED);
            }

            final Profile profile = this.getProfile();
            final BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();
            final String addressNickname = this.getDefaultMailingAddress();

            if (StringUtils.isBlank(addressNickname)) {

                this.logDebug("A null or empty nickname was provided to updateDefaultMailingAddress");

                // if no nickname provided, do nothing.
            } else {

                // Set requested mailing address as default
                profileTools.setDefaultMailingAddress(profile, addressNickname);

            }
            this.logDebug("BBBProfileFormHandler.updateDefaultMailingAddress() method end");

        } catch (final TransactionDemarcationException transactionDemarcationException) {
            this.logError(
                    LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                            + EXCEPTION_HANDLE_DEFAULT_SHIPPING_ADDRESS, BBBCoreErrorConstants.ACCOUNT_ERROR_1062),
                            transactionDemarcationException);
            throw new ServletException(transactionDemarcationException);

        } finally {
            try {
                if (transactionManager != null) {
                    transactionDemarcation.end();
                }
            } catch (final TransactionDemarcationException demarcationException) {
                this.logError(
                        LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                                + EXCEPTION_HANDLE_DEFAULT_SHIPPING_ADDRESS, BBBCoreErrorConstants.ACCOUNT_ERROR_1063),
                                demarcationException);
            }
        }

    }
    /** This sets the default shipping address to expose rest api.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws RepositoryException
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleMakeDefaultShippingAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws RepositoryException, ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.handleMakeDefaultShippingAddress() method started");

        this.updateDefaultShippingAddress(pRequest, pResponse);

        this.logDebug("BBBProfileFormHandler.handleMakeDefaultShippingAddress() method end");

        if (this.getFormExceptions().size() > 0) {
            return false;
        }
        return true;
    }

    /** This sets the default billing address.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws IOException
     * @throws ServletException
     * @throws RepositoryException */
    public final boolean handleDefaultBillingAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException, RepositoryException {

        this.logDebug(HANDLE_DEFAULT_BILLING_ADDRESS_METHOD + START);

        this.updateDefaultBillingAddress(pRequest, pResponse);

        this.logDebug(HANDLE_DEFAULT_BILLING_ADDRESS_METHOD + END);

        return this.checkFormRedirect(pRequest.getRequestURI(), pRequest.getRequestURI(), pRequest, pResponse);

    }
    
    /** This sets the default shipping address to expose rest api.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws RepositoryException
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleMakeDefaultMailingAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws RepositoryException, ServletException, IOException {

        this.logDebug("BBBProfileFormHandler.handleMakeDefaultMailingAddress() method started");

        this.updateDefaultMailingAddress(pRequest, pResponse);

        this.logDebug("BBBProfileFormHandler.handleMakeDefaultMailingAddress() method end");

        if (this.getFormExceptions().size() > 0) {
            return false;
        }
        return true;
    }

    /** This generic code for default billing address .
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws RepositoryException
     * @throws IOException */

    private void updateDefaultBillingAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws RepositoryException, ServletException, IOException {

        this.logDebug(UPDATE_DEFAULT_BILLING_ADDRESS_METHOD + START);

        final TransactionManager transactionManager = this.getTransactionManager();
        final TransactionDemarcation transactionDemarcation = this.getTransactionDemarcation();

        try {
            if (transactionManager != null) {
                transactionDemarcation.begin(transactionManager, TransactionDemarcation.REQUIRED);
            }

            final Profile profile = this.getProfile();
            final BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();
            final String addressNickname = this.getDefaultBillingAddress();

            if (StringUtils.isBlank(addressNickname)) {

                this.logDebug("A null or empty nickname was provided to defaultBillingAddress");

                // if no nickname provided, do nothing.
            } else {

                // Set requested billing addres as default
                profileTools.setDefaultBillingAddress(profile, addressNickname);

            }
            this.logDebug(UPDATE_DEFAULT_BILLING_ADDRESS_METHOD + START);

        } catch (final TransactionDemarcationException transactionDemarcationException) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                    + EXCEPTION_BBB_PROFILE_FORM_HANDLER_HANDLE_DEFAULT_BILLING_ADDRESS,
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1064), transactionDemarcationException);
            throw new ServletException(transactionDemarcationException);

        } finally {
            try {
                if (transactionManager != null) {
                    transactionDemarcation.end();
                }
            } catch (final TransactionDemarcationException demarcationException) {
                this.logError(LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                        + EXCEPTION_BBB_PROFILE_FORM_HANDLER_HANDLE_DEFAULT_BILLING_ADDRESS,
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1065), demarcationException);
            }
        }

    }


    /** This sets the default billing address for rest API.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return true for success, false - otherwise
     * @throws RepositoryException Exception
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleMakeDefaultBillingAddress(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws RepositoryException, ServletException, IOException {

        this.logDebug(HANDLE_MAKE_DEFAULT_BILLING_ADDRESS_METHOD + START);

        this.updateDefaultBillingAddress(pRequest, pResponse);

        this.logDebug(HANDLE_MAKE_DEFAULT_BILLING_ADDRESS_METHOD + END);

        if (this.getFormExceptions().size() > 0) {
            return false;
        }
        return true;
    }

    /** Determine the user's current locale, if available.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @return Locale Request's Locale */
    protected final Locale getLocale(final DynamoHttpServletRequest pRequest) {
        RequestLocale reqLocale = pRequest.getRequestLocale();

        if (reqLocale == null) {
            reqLocale = this.getRequestLocale();
        }

        if (reqLocale == null) {
            return null;
        }
        return reqLocale.getLocale();
    }

    /** This method is overridden from BBBProfileFormHandler. Performs the following 1. <fill here> 2. <fill here>
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */

    public final boolean handleCheckoutDefaults(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug(LogMessageFormatter.formatMessage(pRequest, HANDLE_CHECKOUT_DEFAULTS_METHOD + START));

        this.updateCheckoutDefaultValues(pRequest);

        this.logDebug(LogMessageFormatter.formatMessage(pRequest, HANDLE_CHECKOUT_DEFAULTS_METHOD + END));

        return this.checkFormRedirect(pRequest.getRequestURI(), this.getUpdateErrorURL(), pRequest, pResponse);

    }

    /** This method is overridden from BBBProfileFormHandler. Performs the following 1. <fill here> 2. <fill here>
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final void handleMakeDefaultCreditCard(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug(LogMessageFormatter.formatMessage(pRequest,
                "BBBProfileFormHandler.handleMakeDefaultCreditCard() method Starts"));

        final BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();
        final String creditCardProperty = getPropertyManager().getDefaultCreditCardPropertyName();
        final Map ccDetails = (Map) this.getProfile().getPropertyValue(BBBCoreConstants.CREDIT_CARDS);
        final TransactionManager tm = this.getTransactionManager();
        final TransactionDemarcation td = this.getTransactionDemarcation();
        try {
            td.begin(tm, TransactionDemarcation.REQUIRED);
            if ((ccDetails.size() > 0)
                    && ((this.getProfile().getPropertyValue(BBBCoreConstants.DEFAULT_CREDIT_CARD) == null) || this
                            .getProfile().getPropertyValue(BBBCoreConstants.DEFAULT_CREDIT_CARD)
                            .equals(BBBCoreConstants.NULL_VALUE))) {
                profileTools.updateProperty(creditCardProperty, ccDetails.values().toArray()[ccDetails.size() - 1],
                        this.getProfile());
            }
        } catch (final TransactionDemarcationException e) {
            this.logError(
                    LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                            + EXCEPTION_HANDLE_MAKE_DEFAULT_CREDIT_CARD, BBBCoreErrorConstants.ACCOUNT_ERROR_1069), e);
            throw new ServletException(e);
        } catch (final RepositoryException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, REPOSITORY
                    + EXCEPTION_HANDLE_MAKE_DEFAULT_CREDIT_CARD, BBBCoreErrorConstants.ACCOUNT_ERROR_1070), e);
            throw new ServletException(e);
        } finally {
            try {
                td.end();
            } catch (final TransactionDemarcationException e) {
                this.logError(
                        LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                                + EXCEPTION_HANDLE_MAKE_DEFAULT_CREDIT_CARD, BBBCoreErrorConstants.ACCOUNT_ERROR_1071),
                                e);
            }
        }

        this.logDebug(LogMessageFormatter.formatMessage(pRequest,
                "BBBProfileFormHandler.handleMakeDefaultCreditCard() method Ends"));

    }

    /** This method gets the bazaar voice key for the current Site from Config Keys repository
     * 
     * @return
     * @throws BBBBusinessException
     * @throws BBBSystemException */
    public String getBazaarVoiceKey() throws BBBBusinessException, BBBSystemException {

        final String siteId = atg.multisite.SiteContextManager.getCurrentSiteId();

        final String key = siteId + "_BazaarVoiceKey";

        List<String> listKeys = null;
        String bazaarVoicekey = null;

        try {
            listKeys = getBbbCatalogTools().getAllValuesForKey("ContentCatalogKeys", key);

            if ((listKeys != null) && (listKeys.size() > 0)) {
                bazaarVoicekey = listKeys.get(0);
            }

        } catch (final BBBSystemException e) {
            this.logError(
                    LogMessageFormatter.formatMessage(null, BBB_SYSTEM + EXCEPTION_BAZAAR_VOICE_KEY_NOT_FOUND_FOR_SITE
                            + siteId, BBBCoreErrorConstants.ACCOUNT_ERROR_1072), e);
            throw e;
        } catch (final BBBBusinessException e) {
            this.logError(
                    LogMessageFormatter.formatMessage(null, BBB_BUSINESS
                            + EXCEPTION_BAZAAR_VOICE_KEY_NOT_FOUND_FOR_SITE + siteId,
                            BBBCoreErrorConstants.ACCOUNT_ERROR_1073), e);
            throw e;
        }

        return bazaarVoicekey;
    }

    /** This method would be called in case of Bazaar voice ratings and reviews. User Token is required by Bazaar voice
     * to authenticate the user. This method is calling another method createUserTokenBVRR() to get userToken. This
     * method is also responsible for redirecting the request to the page from which it came.
     * 
     * @param pRequest
     * @param redirectURL
     * @param flag
     * @return Object
     * @throws BBBBusinessException */
    public final Object checkUserTokenBVRR(final DynamoHttpServletRequest pRequest, final Object redirectURL,
            final boolean flag,String emailId) throws BBBBusinessException {

        final String methodName = BBBCoreConstants.CHECK_USER_TOKEN_BVRR;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.CHECKUSER_TOKEN_BVRR, methodName);

        Object redirectString = redirectURL;
        final String bvWriteReviewUrl = (String) pRequest.getSession().getAttribute(
                BBBCoreConstants.BV_WRITE_REVIEW_URL);
        String userTokenBVRR = null;
        try {
        	//PS-18177. Updated UAS to generate token for BV
            userTokenBVRR = BazaarVoiceUtil.createUserTokenBVRR(BazaarVoiceUtil.generateMD5((getEmailMD5HashPrefix()+emailId).toLowerCase()),
                    this.getBazaarVoiceKey(),emailId);
            if (userTokenBVRR.equalsIgnoreCase(BBBCoreConstants.BAZAAR_VOICE_ERROR)) {
                throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1275,
                        BBBCoreConstants.BAZAAR_VOICE_ERROR);
            }
        } catch (final UnsupportedEncodingException exp) {
            throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1275,
                    BBBCoreConstants.BAZAAR_VOICE_ERROR, exp);
        } catch (final BBBSystemException sysExp) {
            throw new BBBBusinessException(sysExp.getErrorCode(), BBBCoreConstants.BAZAAR_VOICE_ERROR, sysExp);
        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CHECKUSER_TOKEN_BVRR, methodName);
        }
        pRequest.getSession().setAttribute(BBBCoreConstants.USER_TOKEN_BVRR, userTokenBVRR);

        if ((bvWriteReviewUrl != null) && bvWriteReviewUrl.toString().contains(BBBCoreConstants.BV_DO_LOGIN)) {
            StringBuilder returnPage = null;

            if (bvWriteReviewUrl.toString().contains(BBBCoreConstants.BV_DO_WRITE_REVIEW)) {
                returnPage = new StringBuilder((String) pRequest.getSession().getAttribute(BBBCoreConstants.PAGE_NAME));
                pRequest.getSession().removeAttribute(BBBCoreConstants.BV_WRITE_REVIEW_URL);
                pRequest.getSession().removeAttribute(BBBCoreConstants.PAGE_NAME);
            } else {
                returnPage = new StringBuilder((String) pRequest.getSession()
                        .getAttribute(BBBCoreConstants.RETURN_PAGE));
                pRequest.getSession().removeAttribute(BBBCoreConstants.BV_WRITE_REVIEW_URL);
                pRequest.getSession().removeAttribute(BBBCoreConstants.RETURN_PAGE);
            }

            returnPage.append("&userToken=" + userTokenBVRR);
            returnPage.append("&showSubmitReviews=true");
            redirectString = returnPage;
            if (flag == true) {
                this.setCreateSuccessURL(returnPage.toString());
                return null;
            } else if (getAssoSite() != null) {
            	//this.mAssoSite = returnPage.toString();
            	setAssoSite(returnPage.toString());
            } else {
                this.mLoginSuccessURL = returnPage.toString();
                this.mCreateSuccessURL = returnPage.toString();
            }

        }

        return redirectString;
    }

    /** This method is used to register the guest user information like first name ,last name, email address,
     * opt-email,login email address, billing/shipping address and credit card information in profile repository
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */

    public boolean handleRegistration(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	String key = "registration";
    	boolean isSiteAssociationReg = true;
    	   
    	
       	populateErrorSuccessUrlRegn(pRequest, key);

		// Client DOM XSRF changes
		final String siteId = getSiteContext().getSite().getId();
        if (getExtenstionSuccessURL() != null && !getExtenstionSuccessURL().equalsIgnoreCase(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)
        		&& getRegistrationSuccessURL() != null && !getRegistrationSuccessURL().equalsIgnoreCase(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
        	setExtenstionSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(BBBURLConstants.EXTENSION_CHECKOUT));
        	setRegistrationSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(BBBURLConstants.REGISTRATION));
		}
    	
        this.logDebug(LogMessageFormatter.formatMessage(pRequest,
                "BBBProfileFormHandler.handleRegistration() method Started"));

        String firstName = null;
        String lastName = null;
        String mobileNumber = null;
        processCreditCard(pRequest);
       
        if (getBBBOrder() != null) {

            boolean foundValidCreditCard = false;

            @SuppressWarnings ("unchecked")
            final List<PaymentGroup> listPaymentGroups = getBBBOrder().getPaymentGroups();
            if ((listPaymentGroups != null) && (listPaymentGroups.size() > 0)) {
                for (final PaymentGroup paymentGroup : listPaymentGroups) {
                    if (paymentGroup instanceof CreditCard) {
                        foundValidCreditCard = true;
                        if (((CreditCardInfo) paymentGroup).getBillingAddress() != null) {
                            firstName = ((CreditCardInfo) paymentGroup).getBillingAddress().getFirstName();
                            lastName = ((CreditCardInfo) paymentGroup).getBillingAddress().getLastName();

                        }

                    }
                }

            }
            if (!foundValidCreditCard && (getBBBOrder().getBillingAddress() != null)) {
                firstName = getBBBOrder().getBillingAddress().getFirstName();
                lastName = getBBBOrder().getBillingAddress().getLastName();
            }

            @SuppressWarnings ("unchecked")
            final Dictionary<String, String> value = this.getValue();
            
            String loginEmail = populateValueDictionaryRegistration(firstName, lastName, value);
            
            //BSL-4694 | Storing mobile number while doing SPC in Profile
    		final String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
            storeMobileNumWhileSpc(value, channel);
            
            final RepositoryItem repItem = getProfileTools().getItemFromEmail(loginEmail);
            if (((BBBProfileTools) this.getProfileTools()).isProfileStatusShallow(loginEmail.toLowerCase())) {
            	logDebug("registrationSuccessURL::" + getRegistrationSuccessURL());
            	value.put(getPropertyManager().getAutoLoginPropertyName(), BBBCoreConstants.TRUE);
    			if (!pRequest.getContextPath().equals(BBBCoreConstants.CONTEXT_REST)) {
            		setCreateSuccessURL(getRegistrationSuccessURL());
        		}
            	pRequest.setParameter(COPY_ORDER_DETAILS, true);
            	return  updateShallowAccount(pRequest, pResponse, loginEmail);
            }
            logDebug("Shallow Account not found.");
            
            this.preCreateUser(pRequest, pResponse);

            if (!this.getFormError()) {

                isSiteAssociationReg = processRegistrationData(pRequest, pResponse, isSiteAssociationReg, loginEmail,
						repItem);
            } // Add Form Exception

        } else {
            processNullOrder(pRequest);
        } // order is null

		//Added for Email Opt-in - Start
        processEmailOptIn(pRequest);
		String memberId = null;
        if (!BBBUtility.isEmpty(getSessionBean().getLegacyMemberId())) {
            memberId = getSessionBean().getLegacyMemberId();
        }
		processAddSiteToProfile(pRequest, isSiteAssociationReg, siteId, memberId);

		//Added for Email Opt-in - End
		
        this.logDebug(LogMessageFormatter.formatMessage(pRequest,
                "BBBProfileFormHandler.handleRegistration() method Ended"));

        return this.checkFormRedirect(this.getPreRegisterSuccessURL(), this.getPreRegisterErrorURL(), pRequest,
                pResponse);
    }


	private void processCreditCard(final DynamoHttpServletRequest pRequest) {
		String internationalCreditCardFlag = "false";
        internationalCreditCardFlag = (String) pRequest.getSession().getAttribute(
                BBBCoreConstants.INERNATIONAL_CREDIT_CARDS);
        if ((internationalCreditCardFlag != null) && internationalCreditCardFlag.equalsIgnoreCase(TRUE)) {            
            setSaveCreditCardInfoToProfile(false);
            getSessionBean().setPreSelectedAddress(false);
        } else {
            if (!this.isSaveCreditCardInfoToProfile()) {
                getSessionBean().setPreSelectedAddress(true);
            }
        }
	}


	private void processAddSiteToProfile(final DynamoHttpServletRequest pRequest, boolean isSiteAssociationReg,
			final String siteId, String memberId) {
		final String sEmail = (String) this.getValueProperty(this
				.getPropertyManager().getEmailAddressPropertyName());

		final String emailOptInFlag = this.isEmailOptIn() ? BBBCoreConstants.YES
				: BBBCoreConstants.NO;
		final String emailOptInBabyCAFlag = this.isEmailOptIn_BabyCA() ? BBBCoreConstants.YES
				: BBBCoreConstants.NO;
	    final String emailOptInSharedSite = this.isEmailOptInSharedSite() ? BBBCoreConstants.YES
	    		: BBBCoreConstants.NO;

		try {
			if (!this.getFormError() && isSiteAssociationReg){
				if ((siteId != null) && getSharedCheckBoxEnabled()) {
					
					if(siteId.equals(BBBCoreConstants.SITE_BAB_US)){
						getProfileManager().addSiteToProfile(sEmail, TBSConstants.SITE_TBS_BAB_US,
								memberId, null, emailOptInFlag);
						getProfileManager().addSiteToProfile(sEmail, TBSConstants.SITE_TBS_BBB,
								memberId, null, emailOptInSharedSite);
					}else if(siteId.equals(BBBCoreConstants.SITE_BBB)){
						getProfileManager().addSiteToProfile(sEmail, TBSConstants.SITE_TBS_BAB_US,
								memberId, null, emailOptInSharedSite);
						getProfileManager().addSiteToProfile(sEmail, TBSConstants.SITE_TBS_BBB,
								memberId, null, emailOptInFlag);
					}else if(siteId.equals(TBSConstants.SITE_TBS_BAB_US)){
						getProfileManager().addSiteToProfile(sEmail, TBSConstants.SITE_TBS_BBB,
								memberId, null, emailOptInFlag);
					}else if(siteId.equals(TBSConstants.SITE_TBS_BBB)){
						getProfileManager().addSiteToProfile(sEmail, TBSConstants.SITE_TBS_BAB_US,
								memberId, null, emailOptInFlag);
					}
				} else {
					this.mTools.createSiteItemRedirect(sEmail, siteId, memberId,
							null, emailOptInFlag, emailOptInBabyCAFlag);
				}
			}
		} catch (final BBBSystemException e) {
			this.logError(LogMessageFormatter.formatMessage(pRequest,
					BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE,
					BBBCoreErrorConstants.ACCOUNT_ERROR_1128), e);
			this.addFormException(new DropletException(
					BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE,
					BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE));
			this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_SYSTEM_ERROR,
					BBBCoreConstants.ERR_PROFILE_SYSTEM_ERROR_SHARE_SITE);
		}
	}


	private void processEmailOptIn(final DynamoHttpServletRequest pRequest) {
		if (isEmailOptIn() && !this.getFormError()) {
			pRequest.getSession().setAttribute(BBBCoreConstants.SUBSCRIBE,
					BBBCoreConstants.YES);
		}

		populateSubscribeProperty(pRequest);
	}


	private void processNullOrder(final DynamoHttpServletRequest pRequest) {
		this.logDebug("order is null");

		final String registerationError = getMessageHandler().getErrMsg(
		        BBBCoreConstants.ERR_CC_ORDER_NOT_EXIST, pRequest.getLocale().getLanguage(), null, null);
		this.addFormException(new DropletException(BBBCoreConstants.ERR_CC_ORDER_NOT_EXIST,
		        BBBCoreConstants.ERR_CC_ORDER_NOT_EXIST));
		this.errorMap.put(BBBCoreConstants.REGISTER_ERROR, registerationError);
	}


	private boolean processRegistrationData(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, boolean isSiteAssociationReg, String loginEmail,
			final RepositoryItem repItem) throws ServletException, IOException {
		
		this.mTools = (BBBProfileTools) this.getProfileTools();
		if (this.mTools.isDuplicateEmailAddress(loginEmail, getSiteContext().getSite().getId())) {
		    final String registerationError = getMessageHandler().getErrMsg(
		            BBBCoreConstants.ERR_CC_PROFILE_EMAILALREADYEXIST, pRequest.getLocale().getLanguage(),
		            null, null);
		    this.addFormException(new DropletException(registerationError,
		            BBBCoreConstants.ERR_CC_PROFILE_EMAILALREADYEXIST));
		    this.errorMap.put(BBBCoreConstants.REGISTER_ERROR, registerationError);
		} else {
			
		    if (repItem != null) {
		        isSiteAssociationReg = processValidProfile(pRequest, isSiteAssociationReg, loginEmail, repItem);
		    }
		    if (getLoginEmail() != null) {
		    	
		        setPreRegisterSuccessURL(getExtenstionSuccessURL() + getLoginEmail());

		    } else {

		    	TransactionManager tm = this.getTransactionManager();
		    	TransactionDemarcation td = this.getTransactionDemarcation();
		    	triggerCreatePaswdSalt(pRequest, pResponse, loginEmail, tm, td);
		        setPreRegisterSuccessURL(getRegistrationSuccessURL());
		        triggerCheckoutRegistration(pRequest, tm, td);

		    }
		} // Login Id already Exist
		return isSiteAssociationReg;
	}


	private void triggerCheckoutRegistration(final DynamoHttpServletRequest pRequest, TransactionManager tm,
			TransactionDemarcation td) throws ServletException {
		try {
		    td.begin(tm, TransactionDemarcation.REQUIRED);
		    getProfileManager().checkoutRegistration(getBBBOrder(), this.getProfile(), getSiteContext()
		            .getSite().getId(), getSessionBean().isPreSelectedAddress());
		    getSessionBean().setPreSelectedAddress(false);
		} catch (final TransactionDemarcationException e) {
		    this.logError(
		            LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
		                    + EXCEPTION_HANDLE_REGISTRATION, BBBCoreErrorConstants.ACCOUNT_ERROR_1385),
		                    e);
		    throw new ServletException(e);
		} catch (final BBBSystemException systemException) {
		    this.logError(LogMessageFormatter.formatMessage(pRequest,
		            BBBCoreConstants.ERR_CC_SYS_EXCEPTION, BBBCoreErrorConstants.ACCOUNT_ERROR_1119),
		            systemException);
		    final String registerationError = getMessageHandler().getErrMsg(
		            BBBCoreConstants.ERR_CC_SYS_EXCEPTION, pRequest.getLocale().getLanguage(), null,
		            null);
		    this.addFormException(new DropletException(registerationError,
		            BBBCoreErrorConstants.ACCOUNT_ERROR_1303));

		    this.errorMap.put(BBBCoreConstants.REGISTER_ERROR, registerationError);
		} catch (final BBBBusinessException businessException) {
		    this.logError(LogMessageFormatter.formatMessage(pRequest,
		            BBBCoreConstants.ERR_CC_BSYS_EXCEPTION, BBBCoreErrorConstants.ACCOUNT_ERROR_1120),
		            businessException);
		    final String registerationError = getMessageHandler().getErrMsg(
		            BBBCoreConstants.ERR_CC_BSYS_EXCEPTION, pRequest.getLocale().getLanguage(), null,
		            null);
		    this.addFormException(new DropletException(registerationError,
		            BBBCoreErrorConstants.ACCOUNT_ERROR_1304));
		    this.errorMap.put(BBBCoreConstants.REGISTER_ERROR, registerationError);
		} finally {
		    try {
		        if (tm != null) {
		            td.end();
		        }
		    } catch (final TransactionDemarcationException e) {
		        this.logError("Error Occured during checkoutRegistration", e);
		    }
		} // End Try
	}


	private void triggerCreatePaswdSalt(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, String loginEmail, TransactionManager tm,
			TransactionDemarcation td) throws ServletException, IOException {
		try {
			if (tm != null){
				td.begin(tm, TransactionDemarcation.REQUIRES_NEW);
			}
			 // Creating Salt Value
		    //if(loginEmail!=null){
		    	if(loginEmail!=null && !createPasswordSalt(loginEmail.toLowerCase())){
		    		logDebug("fail to create salt in HandleCreate Method");
		    	}
		    //}
			super.handleCreate(pRequest, pResponse);
		} catch (TransactionDemarcationException e) {
		    this.logError(
		            LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
		                    + EXCEPTION_HANDLE_REGISTRATION, BBBCoreErrorConstants.ACCOUNT_ERROR_1385),
		                    e);
		    throw new ServletException(e);
		}
		finally {
			try {
				if (tm != null){
					td.end();
				}
			}catch (TransactionDemarcationException e) {
				this.logError("Error Occured during checkoutRegistration", e);
			}
		}
	}


	private boolean processValidProfile(final DynamoHttpServletRequest pRequest, boolean isSiteAssociationReg,
			String loginEmail, final RepositoryItem repItem) {
		if (getProfileManager().isUserPresentToOtherGroup(repItem, getSiteContext().getSite()
		        .getId())) {
		    final String loginEmptyError = getMessageHandler().getErrMsg(
		            BBBCoreConstants.ERR_USER_ALREADY_ASSOCIATED_TO_SITE,
		            pRequest.getLocale().getLanguage(), null, null);
		    this.addFormException(new DropletException(loginEmptyError,
		            BBBCoreConstants.ERR_USER_ALREADY_ASSOCIATED_TO_SITE));
		    this.errorMap.put(BBBCoreConstants.REGISTER_ERROR, loginEmptyError);
		} else {
			RepositoryItem[] items = null;
		    try {
				items = mTools.lookupUsers(loginEmail, loginEmail, BBBProfileServicesConstant.USER);
			} catch (RepositoryException e) {
				logDebug("Exception occured during searching for duplicate user", e);
			}
		    if(items!=null && items.length!=0){
		    	setLoginEmail(loginEmail);
		    	isSiteAssociationReg = false;
		}  
     }
		return isSiteAssociationReg;
	}


	private void storeMobileNumWhileSpc(final Dictionary<String, String> value, final String channel) {
		String mobileNumber;
		if ((channel != null)
		        && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel
		                .equalsIgnoreCase(BBBCoreConstants.MOBILEAPP))){
		mobileNumber= getBBBOrder().getBillingAddress().getMobileNumber();
		value.put(getPropertyManager().getMobileNumberPropertyName(), mobileNumber);
		}
		
		if(isShallowProfileChanges()){
			value.put(getPropertyManager().getStatusPropertyName() , BBBCoreConstants.FULL_PROFILE_STATUS_VALUE);
		}
	}


	private String populateValueDictionaryRegistration(String firstName, String lastName,
			final Dictionary<String, String> value) {
		if (StringUtils.isEmpty(value.get(getPropertyManager().getEmailAddressPropertyName()))) {
		    value.put(getPropertyManager().getEmailAddressPropertyName(), getBBBOrder().getBillingAddress()
		            .getEmail().toLowerCase());
		}
		String loginEmail = value.get(getPropertyManager().getEmailAddressPropertyName());
		loginEmail = loginEmail.toLowerCase();
		value.put(getPropertyManager().getLoginPropertyName(), loginEmail);
		value.put(getPropertyManager().getEmailAddressPropertyName(), loginEmail);
		value.put(getPropertyManager().getFirstNamePropertyName(), firstName);
		value.put(getPropertyManager().getLastNamePropertyName(), lastName);
		value.put(getPropertyManager().getSaveCreditCardInfoToProfile(),
		        String.valueOf(this.isSaveCreditCardInfoToProfile()));
		return loginEmail;
	}


	private void populateErrorSuccessUrlRegn(final DynamoHttpServletRequest pRequest, String key) {
		if(getPreRegisterErrorURL() != null && !getPreRegisterErrorURL().equalsIgnoreCase(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
    		setPreRegisterErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(key));
    	}
    	if(getPreRegisterSuccessURL() != null && !getPreRegisterSuccessURL().equalsIgnoreCase(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
        	setPreRegisterSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(key));
    	}
	}

    /** this method login BBB user if the user is Facebook authenticated
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleFbLogin(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    	String key = "fbLogin";
    	setSuccessURL(pRequest,key);
    	setErrorURL(pRequest,key);
    	
        final String opName = HANDLE_FB_LOGIN;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);

        this.logDebug(opName + START);

        final UserVO fbUser = (UserVO) pRequest.getSession().getAttribute(FBConstants.FB_BASIC_INFO);
        boolean status = false;
        Transaction transaction = null;
        boolean isException = false;

        if (fbUser != null) {
            try {
                transaction = this.ensureTransaction();
                final String bbbProfileEmailAddress = this.getFacebookProfileTool().getBBBEmailForFBProfile(
                        fbUser.getUserName());
                status = this.autoSignIn(bbbProfileEmailAddress, pRequest, pResponse);
                pRequest.getSession().removeAttribute(FBConstants.FB_BASIC_INFO);
            } catch (final BBBSystemException e) {
                isException = true;
                this.logError(LogMessageFormatter.formatMessage(pRequest, FBConstants.ERROR_FB_UNEXPECTED,
                        BBBCoreErrorConstants.ACCOUNT_ERROR_1125), e);
                this.addFormException(new DropletException(getMessageHandler().getErrMsg(
                        FBConstants.ERROR_FB_UNEXPECTED, pRequest.getLocale().getLanguage(), null, null),
                        FBConstants.ERROR_FB_UNEXPECTED));

            } finally {
                this.endTransaction(isException, transaction);
            }
        } else {
            pRequest.getSession().setAttribute(BBBCoreConstants.REDIRECT_URL, this.getLoginErrorURL());
        }

        this.logDebug(opName + END);

        BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
        return status;
    }

    /** this method will validate the password and perform the login to extend the Account
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleValidateAndExtendAccount(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        final String opName = HANDLE_VALIDATE_AND_EXTEND_ACCOUNT;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
        String key = "validateAndExtendAcct";
        setSuccessURL(pRequest,key);
        setErrorURL(pRequest,key);
        
        this.logDebug(opName + START);

        Transaction transaction = null;
        boolean isException = true;

        try {
            final BBBProfileTools bbbProfileTools = (BBBProfileTools) this.getProfileTools();
            transaction = this.ensureTransaction();
            String emailoOptIn = this.isExtendEmailOptn() ? BBBCoreConstants.YES : BBBCoreConstants.NO;
            bbbProfileTools.createSiteItem(this.getEmailAddress(), getSiteContext().getSite().getId(), null, emailoOptIn,
                    null);
            this.handleLogin(pRequest, pResponse);

            if (!this.getProfile().isTransient()) {
                final RepositoryItem bbbUserProfile = this.getProfile().getDataSource();
                final UserVO fbUser = (UserVO) pRequest.getSession().getAttribute(FBConstants.FB_BASIC_INFO);

                if (fbUser != null) {
                    this.getFacebookProfileTool().linkBBBProfileWithFBProfile(bbbUserProfile, fbUser);
                    isException = false;
                }
                if (!isException) {
                    this.getProfile().setPropertyValue(getPropertyManager().getFbProfileExtended(), Boolean.TRUE);
                }
            }
            if (!this.getFormError()) {
                pResponse.setHeader(FBConstants.FB_HEADER_PARAM, this.getLoginSuccessURL());
            }

        } catch (final BBBSystemException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, FBConstants.ERROR_FB_UNEXPECTED,
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1125), e);
            this.addFormException(new DropletException(getMessageHandler().getErrMsg(
                    FBConstants.ERROR_FB_UNEXPECTED, pRequest.getLocale().getLanguage(), null, null),
                    FBConstants.ERROR_FB_UNEXPECTED));

        } finally {
            this.endTransaction(isException, transaction);
        }

        this.logDebug(opName + END);

        BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
        return this.checkFormRedirect(this.getLoginSuccessURL(), this.getLoginErrorURL(), pRequest, pResponse);
    }

    /** this method extend the profile if facebook profile is connected with sister site
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return Success / Failure
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleExtendAccount(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        final String opName = HANDLE_EXTEND_ACCOUNT;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
        String key = "extendAccount";
        setSuccessURL(pRequest,key);
        setErrorURL(pRequest,key);
        
        this.logDebug(opName + START);

        Transaction transaction = null;
        boolean isException = true;

        try {
            final BBBProfileTools bbbProfileTools = (BBBProfileTools) this.getProfileTools();
            transaction = this.ensureTransaction();
            final UserVO fbUser = (UserVO) pRequest.getSession().getAttribute(FBConstants.FB_BASIC_INFO);
            if (fbUser != null) {
                final String bbbProfileEmailAddress = this.getFacebookProfileTool().getSisterSiteBBBEmailForFBProfile(
                        fbUser.getUserName());
                bbbProfileTools.createSiteItem(bbbProfileEmailAddress, getSiteContext().getSite().getId(), null,
                        null, null);

                this.autoSignIn(bbbProfileEmailAddress, pRequest, pResponse);

                if (!this.getProfile().isTransient()) {
                    final RepositoryItem bbbUserProfile = this.getProfile().getDataSource();
                    this.getFacebookProfileTool().linkBBBProfileWithFBProfile(bbbUserProfile, fbUser);
                    isException = false;
                }
                if (!isException) {
                    this.getProfile().setPropertyValue(getPropertyManager().getFbProfileExtended(), Boolean.TRUE);
                }
                pResponse.setHeader(FBConstants.FB_HEADER_PARAM, this.getLoginSuccessURL());

            }

        } catch (final BBBSystemException e) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, FBConstants.ERROR_FB_UNEXPECTED,
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1125), e);
            this.addFormException(new DropletException(getMessageHandler().getErrMsg(
                    FBConstants.ERROR_FB_UNEXPECTED, pRequest.getLocale().getLanguage(), null, null),
                    FBConstants.ERROR_FB_UNEXPECTED));

        } finally {
            this.endTransaction(isException, transaction);
        }

        this.logDebug(opName + " : End");

        BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
        return this.checkFormRedirect(this.getLoginSuccessURL(), this.getLoginErrorURL(), pRequest, pResponse);
    }

    private void endTransaction(final boolean isError, final Transaction pTransaction) {
        final String opName = END_TRANSACTION;
        BBBPerformanceMonitor.start(BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);
        try {
            if (isError) {
                if (pTransaction != null) {
                    pTransaction.rollback();
                }
            } else {
                if (pTransaction != null) {
                    pTransaction.commit();
                }
            }
        } catch (final SecurityException e) {
            this.logError(LogMessageFormatter.formatMessage(null, "Security"
                    + EXCEPTION_BBB_PROFILE_FORM_HANDLER_END_TRANSACTION, BBBCoreErrorConstants.ACCOUNT_ERROR_1079), e);
        } catch (final IllegalStateException e) {
            this.logError(LogMessageFormatter.formatMessage(null, "IllegalState"
                    + EXCEPTION_BBB_PROFILE_FORM_HANDLER_END_TRANSACTION, BBBCoreErrorConstants.ACCOUNT_ERROR_1080), e);
        } catch (final RollbackException e) {
            this.logError(LogMessageFormatter.formatMessage(null, "Rollback"
                    + EXCEPTION_BBB_PROFILE_FORM_HANDLER_END_TRANSACTION, BBBCoreErrorConstants.ACCOUNT_ERROR_1081), e);
        } catch (final HeuristicMixedException e) {
            this.logError(LogMessageFormatter.formatMessage(null, "HeuristicMixed"
                    + EXCEPTION_BBB_PROFILE_FORM_HANDLER_END_TRANSACTION, BBBCoreErrorConstants.ACCOUNT_ERROR_1082), e);
        } catch (final HeuristicRollbackException e) {
            this.logError(LogMessageFormatter.formatMessage(null, "HeuristicRollback"
                    + EXCEPTION_BBB_PROFILE_FORM_HANDLER_END_TRANSACTION, BBBCoreErrorConstants.ACCOUNT_ERROR_1083), e);
        } catch (final SystemException e) {
            this.logError(LogMessageFormatter.formatMessage(null, "System"
                    + EXCEPTION_BBB_PROFILE_FORM_HANDLER_END_TRANSACTION, BBBCoreErrorConstants.ACCOUNT_ERROR_1084), e);
        }

        BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);

    }

    /** method to auto login user if user is authenticated via Facebook
     * 
     * @param pFBAccountId
     * @return */
    private boolean autoSignIn(final String bbbProfileEmailAddress, final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        final TransactionManager tm = this.getTransactionManager();
        final TransactionDemarcation td = this.getTransactionDemarcation();
        try {

            if (tm != null) {
                td.begin(tm, TransactionDemarcation.REQUIRED);
            }

            final String opName = AUTO_SIGN_IN;
            BBBPerformanceMonitor.start(BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);

            this.logDebug(opName + ": Start");

            boolean result = false;
            final RepositoryItem preLoginDataSource = this.getProfile().getDataSource();
            if (!BBBUtility.isEmpty(bbbProfileEmailAddress)) {
                this.getProfile().setDataSource(this.getProfileTools().getItemFromEmail(bbbProfileEmailAddress));
                this.getProfile().setPropertyValue(FBConstants.SECURITY_STATUS, FACEBOOK_SECURITY_STATUS);
                pRequest.setParameter("HANDLE_LOGIN", HANDLE_SUCCESS);

                result = true;
            }
            this.sendProfileSwapEvent(0, preLoginDataSource, this.getProfile().getDataSource());

            if (this.getUserCheckingOut() != null) {
                pRequest.setParameter("userCheckingOut", "userCheckingOut");
            }

            pRequest.setParameter("autoLoginFlag", TRUE);
			// PS-24181 Website Issue Reported - BV | In case of fbconnect or forgot password flow getLoginEmail() is null
			// so setting explicitly
			logDebug("Email Id on auto logging: "+bbbProfileEmailAddress);
            if(BBBUtility.isEmpty(this.getLoginEmail()) && BBBUtility.isNotEmpty(bbbProfileEmailAddress)) {
            	this.setLoginEmail(bbbProfileEmailAddress);
            }
            this.postLoginUser(pRequest, pResponse);
            if (result) {
                pRequest.getSession().setAttribute(BBBCoreConstants.REDIRECT_URL, this.getLoginSuccessURL());
                pResponse.setHeader(FBConstants.FB_HEADER_PARAM, this.getLoginSuccessURL());
            } else {
                pRequest.getSession().setAttribute(BBBCoreConstants.REDIRECT_URL, this.getLoginErrorURL());
            }
            this.logDebug(opName + END);

            BBBPerformanceMonitor.end(BBBPerformanceConstants.FACEBOOK_INTEGRATION, opName);

            return result;
        } catch (final TransactionDemarcationException e) {
            this.logError(TRANSACTION_DEMARCATION_ERROR_IN_COMMITING_TRANSACTION, e);
            throw new ServletException(e);
        } finally {
            try {
                if (tm != null) {
                    td.end();
                }
            } catch (final TransactionDemarcationException e) {
                this.logError(TRANSACTION_DEMARCATION_ERROR_IN_COMMITING_TRANSACTION, e);
            }
        }

    }

    private void sendRegistrationEmail(final DynamoHttpServletRequest pRequest, final String sProfileType,
            final String sEmail, final String siteId) {

        try {
            final Map<String, Map<String, String>> pTemplateParams = new HashMap<String, Map<String, String>>();
            final TemplateEmailInfoImpl templateInfo = (TemplateEmailInfoImpl) getRegistrationTemplateEmailInfo();
            String loginURL = null;
            String contextPath = pRequest.getContextPath();
            String isFromThirdParty = pRequest.getParameter(BBBGiftRegistryConstants.IS_FROM_THIRD_PARTY_REST);
            if(!BBBUtility.isEmpty(isFromThirdParty) && isFromThirdParty.equalsIgnoreCase(BBBCoreConstants.TRUE)){
            	contextPath = BBBCoreConstants.CONTEXT_STORE;
            }
            logDebug("Context Path is  + " + contextPath);
            if (templateInfo == null) {
                final String msg = this.formatUserMessage(BBBCoreErrorConstants.ACCOUNT_ERROR_1316, pRequest);
                this.addFormException(new DropletException(msg,
                        BBBCoreConstants.ERR_PROFILE_SYSTEM_EMAIL_TEMPLATE_NOT_SET));
                this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_SYSTEM_ERROR,
                        BBBCoreConstants.ERR_PROFILE_SYSTEM_EMAIL_TEMPLATE_NOT_SET);
            } else {
                final String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
                if ((channel != null)
                        && (channel.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB) || channel
                                .equalsIgnoreCase(BBBCoreConstants.MOBILEAPP) || (!BBBUtility.isEmpty(isFromThirdParty) && isFromThirdParty.equalsIgnoreCase(BBBCoreConstants.TRUE)))) {
                    try {
                        // set context path from properties file in case of
                        // channel as Mobile Web or mobile App
                        templateInfo.setTemplateURL(this.getStoreContextPath() + templateInfo.getTemplateURL());
                        final List<String> configValue = getBbbCatalogTools().getAllValuesForKey(
                                BBBCoreConstants.MOBILEWEB_CONFIG_TYPE, BBBCoreConstants.REQUESTDOMAIN_CONFIGURE);
                        if ((configValue != null) && (configValue.size() > 0)) {
                            // set serverName from config key and context path
                            // from properties
                            loginURL = pRequest.getScheme() + BBBAccountConstants.SCHEME_APPEND + configValue.get(0)
                                    + this.getStoreContextPath() + this.getLoginURL();
                        }
                    } catch (final BBBSystemException e) {
                        this.logError(SEND_REGISTRATION_EMAIL + BBB_SYSTEM
                                + EXCEPTION_OCCURED_WHILE_FETCHING_CONFIG_VALUE_FOR_CONFIG_KEY
                                + BBBCoreConstants.REQUESTDOMAIN_CONFIGURE + CONFIG_TYPE
                                + BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
                    } catch (final BBBBusinessException e) {
                        this.logError(SEND_REGISTRATION_EMAIL + "Business"
                                + EXCEPTION_OCCURED_WHILE_FETCHING_CONFIG_VALUE_FOR_CONFIG_KEY
                                + BBBCoreConstants.REQUESTDOMAIN_CONFIGURE + CONFIG_TYPE
                                + BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
                    }
                } else {
                    templateInfo.setTemplateURL(contextPath + templateInfo.getTemplateURL());
                    loginURL = pRequest.getScheme() + BBBAccountConstants.SCHEME_APPEND + pRequest.getServerName()
                            + pRequest.getContextPath() + this.getLoginURL();
                }
                templateInfo.setMessageTo(sEmail);
                templateInfo.setMailingId(sEmail);
                final Map<String, String> placeHolderMap = new HashMap<String, String>();
                final Calendar currentDate = Calendar.getInstance();
                final long uniqueKeyDate = currentDate.getTimeInMillis();
                final RepositoryItem profileItem = this.getProfileItem();
                final String profileId = profileItem.getRepositoryId();
                final String emailPersistId = profileId + uniqueKeyDate;
                placeHolderMap.put(BBBCoreConstants.EMAIL_TYPE, BBBCoreConstants.ET_CREATE_ACCOUNT);
                placeHolderMap.put(BBBCoreConstants.FORM_SITE, siteId);
                placeHolderMap.put(BBBCoreConstants.FORM_FNAME,
                        (String) this.getValueProperty(getPropertyManager().getFirstNamePropertyName()));
                placeHolderMap.put(BBBCoreConstants.FORM_LNAME,
                        (String) this.getValueProperty(getPropertyManager().getLastNamePropertyName()));
                placeHolderMap.put(BBBCoreConstants.FORM_EMAIL,
                        (String) this.getValueProperty(getPropertyManager().getEmailAddressPropertyName()));

                // Added New place holder value EmailId for #69
                placeHolderMap.put(BBBCoreConstants.EMAIL_PERSIST_ID, emailPersistId);
                pTemplateParams.put(BBBCoreConstants.PLACE_HOLDER, placeHolderMap);
                placeHolderMap.put(BBBAccountConstants.ACCOUNT_LOGIN_URL, loginURL);
                final MutableRepositoryItem[] recipients = this.mTools.lookupUsers(sEmail, sEmail, sProfileType);
                if (recipients == null) {
                    final String msg = this.formatUserMessage(BBBCoreErrorConstants.ACCOUNT_ERROR_1316, pRequest);
                    this.addFormException(new DropletException(msg, BBBCoreErrorConstants.ACCOUNT_ERROR_1316));
                    this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_SYSTEM_ERROR,
                            BBBCoreErrorConstants.ACCOUNT_ERROR_1316);
                } else {

                    this.mTools.sendEmail(recipients, pTemplateParams, templateInfo);
                }

            }
        } catch (final RepositoryException exc) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreConstants.ERR_PROFILE_LOOKUP_USER_ERROR,
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1127), exc);
            final String msg = this.formatUserMessage(BBBCoreConstants.ERR_PROFILE_LOOKUP_USER_ERROR, pRequest);
            this.addFormException(new DropletException(msg, exc, BBBCoreConstants.ERR_PROFILE_LOOKUP_USER_ERROR));
            this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_SYSTEM_ERROR,
                    BBBCoreConstants.ERR_PROFILE_LOOKUP_USER_ERROR);
        } catch (final BBBSystemException exception) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreConstants.ERR_PROFILE_SEND_EMAIL,
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1123), exception);
            this.addFormException(new DropletException(BBBCoreConstants.ERR_PROFILE_SEND_EMAIL,
                    BBBCoreConstants.ERR_PROFILE_SEND_EMAIL));
            this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_SYSTEM_ERROR, BBBCoreConstants.ERR_PROFILE_SEND_EMAIL);
        }

    }

    /** Method to send Registration Email using TIBCO.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param siteId Site ID */
    private void sendRegistrationTibcoEmail(final DynamoHttpServletRequest pRequest, final String siteId) {

        try {
            final Map<String, Object> emailParams = this.createEmailParameters(siteId);
            boolean sendTibcoEmailInNewThread = false;
            try {
	    		List<String> sendTibcoEmailInNewThreadList = getBbbCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "Send_Tibco_Message_New_Thread");
	    		if (!BBBUtility.isListEmpty(sendTibcoEmailInNewThreadList)) {
	    			sendTibcoEmailInNewThread = Boolean.parseBoolean((String) sendTibcoEmailInNewThreadList.get(0));
	    		}
            } catch(BBBBusinessException be) {
    			logError("BBBBusinessException occurred while fetching config key value :: Send_Tibco_Message_New_Thread " + be);
    		} catch(BBBSystemException se) {
    			logError("BBBSystemException occurred while fetching config key value :: Send_Tibco_Message_New_Thread " + se);
    		}
    		
            if (sendTibcoEmailInNewThread) {
            	sendTibcoEmailInNewThread(emailParams);
			} else {
				getProfileManager().sendProfileRegistrationTibcoEmail(emailParams);
			}
        } catch (final BBBSystemException bbbSystemException) {
            this.logError(LogMessageFormatter.formatMessage(pRequest, BBBCoreConstants.ERR_PROFILE_SEND_EMAIL,
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1123), bbbSystemException);
            this.addFormException(new DropletException(BBBCoreConstants.ERR_PROFILE_SEND_EMAIL,
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1305));
            this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_SYSTEM_ERROR, BBBCoreConstants.ERR_PROFILE_SEND_EMAIL);
        } catch (final BBBBusinessException bbbBusinessException) {
            this.logError(LogMessageFormatter.formatMessage(pRequest,
                    "BBBBusinessException - BBBProfileFormHandler.sendRegistrationTibcoEmail",
                    BBBCoreErrorConstants.ACCOUNT_ERROR_1088), bbbBusinessException);
        }
    }

    /** This method will set email parameter for Profile Creation Email Notification.
     * 
     * @param sProfileType
     * @param sEmail
     * @param siteId
     * @return Map */
    private Map<String, Object> createEmailParameters(final String siteId) throws BBBSystemException,
    BBBBusinessException {
        final Map<String, Object> emailParams = new HashMap<String, Object>();
        emailParams.put(BBBCoreConstants.EMAIL_TYPE, BBBCoreConstants.ET_CREATE_ACCOUNT);
        emailParams.put(BBBCoreConstants.FORM_SITE, siteId);
        emailParams.put(BBBCoreConstants.FORM_FNAME,
                this.getValueProperty(getPropertyManager().getFirstNamePropertyName()));
        emailParams.put(BBBCoreConstants.FORM_LNAME,
                this.getValueProperty(getPropertyManager().getLastNamePropertyName()));
        emailParams.put(BBBCoreConstants.FORM_EMAIL,
                this.getValueProperty(getPropertyManager().getEmailAddressPropertyName()));
        emailParams.put(BBBCoreConstants.FORM_PHONE_NUMBER,
                this.getValueProperty(getPropertyManager().getPhoneNumberPropertyName()));
        emailParams.put(BBBCoreConstants.FORM_MOBILE_NUMBER,
                this.getValueProperty(getPropertyManager().getMobileNumberPropertyName()));
        if (this.isEmailOptIn()) {
            emailParams.put(BBBCoreConstants.FORM_OPTIN, BBBCoreConstants.TRUE);
        } else {
            emailParams.put(BBBCoreConstants.FORM_OPTIN, BBBCoreConstants.FALSE);
        }

        emailParams.put(BBBCoreConstants.FORM_PROFILEID, this.getProfile().getRepositoryId().toString());
        emailParams.put(BBBCoreConstants.FORM_SHARE_ACCOUNT, Boolean.valueOf(this.getSharedCheckBoxEnabled()));
        final List<String> siteIds = getBbbCatalogTools().getAllValuesForKey(BBBCoreConstants.TIBCO_KEYS, siteId);
        String siteFlag = BBBCoreConstants.BLANK;
        if ((null != siteIds) && !siteIds.isEmpty()) {
            siteFlag = siteIds.get(0).toString();
        }
        emailParams.put(BBBCoreConstants.SITE_FLAG_PARAM_NAME, siteFlag);
        emailParams.put(BBBCoreConstants.REQUESTED_DT_PARAM_NAME, BBBUtility.getXMLCalendar(new java.util.Date()));
        return emailParams;
    }

    /** Call BBBProfileManager updateRegistryWithAtgInfo method to update the user registry in BBB system.
     * 
     * @return boolean
     * @throws BBBSystemException Exception
     * @throws BBBBusinessException Exception */
    public final boolean updateRegistry() throws BBBSystemException, BBBBusinessException {

        this.logDebug("updateRegistry method Start");

        final ProfileSyncRequestVO profileSyncRequestVO = new ProfileSyncRequestVO();
        profileSyncRequestVO.setProfileId(this.getProfileItem().getRepositoryId());
        profileSyncRequestVO.setEmailAddress(this.getStringValueProperty(getPropertyManager()
                .getEmailAddressPropertyName()));
        profileSyncRequestVO.setFirstName(this.getStringValueProperty(getPropertyManager()
                .getFirstNamePropertyName()));
        profileSyncRequestVO.setLastName(this.getStringValueProperty(getPropertyManager()
                .getLastNamePropertyName()));
        profileSyncRequestVO.setPhoneNumber(this.getStringValueProperty(getPropertyManager()
                .getPhoneNumberPropertyName()));
        profileSyncRequestVO.setMobileNumber(this.getStringValueProperty(getPropertyManager()
                .getMobileNumberPropertyName()));

        final boolean success = getProfileManager().updateRegistryWithAtgInfo(this.getProfileItem(),
                getSiteContext().getSite().getId(), profileSyncRequestVO);

        this.logDebug("updateRegistry method" + END);

        return success;
    }


    /** Override method from ATG OOTB for Logout functionality to send user to home Page after logout.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException if there was an error while executing the code
     * @throws IOException if there was an error with Servlet IO
     * @return true if success, false - otherwise */
    @Override
    public boolean handleLogout(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        pRequest.setRequestURI(pRequest.getContextPath());
        pRequest.getSession().removeAttribute(BBBCoreConstants.USER_TOKEN_BVRR);
         
		final Integer securityStatus = (Integer) this.getProfile().getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS);
		if(COOKIE_LOGIN_SECURITY_STATUS.equals(securityStatus))
		{
			removeOrderCookie(pRequest, pResponse);   
		}
        return super.handleLogout(pRequest, pResponse);

    }

	/**
	 * This method is used for recognized user to Logout functionality and
	 * refresh the current page.
	 * 
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws ServletException
	 *             if there was an error while executing the code
	 * @throws IOException
	 *             if there was an error with Servlet IO
	 * @return true if success, false - otherwise
	 */
   public boolean handleLogoutAndRefresh(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	   // If request origin is secure page then set logout success url as Referer otherwise home page
	   if(pRequest.isSecure()) {
		   pRequest.setRequestURI(pRequest.getHeader(BBBCoreConstants.REFERRER));
	   } else {
		   pRequest.setRequestURI(pRequest.getContextPath());
	   }
        pRequest.getSession().removeAttribute(BBBCoreConstants.USER_TOKEN_BVRR);
        final Integer securityStatus = (Integer) this.getProfile().getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS);
		if(COOKIE_LOGIN_SECURITY_STATUS.equals(securityStatus))
		{
			removeOrderCookie(pRequest, pResponse);   
		}
        return super.handleLogout(pRequest, pResponse);
    }    
    
	/**
	 * This method is used to handle guest checkout flow for recognized user
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
    public boolean handleRecUserAsGuestCheckout(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		this.logDebug("BBBProfileFormHandler.handleGuestCheckout method Start");
		final String key = "recUserLogin";
		setUserCheckingOut("userCheckingOut");
		setLoginSuccessURL((String) pRequest.getSession().getAttribute(BBBCoreConstants.REDIRECT_URL));
		setErrorURL(pRequest, key);
		this.getCookieManager().expireProfileCookies((Profile) this.getProfile(), pRequest, pResponse);
		this.handleRefreshUserProfile(pRequest, pResponse);
		this.logDebug("BBBProfileFormHandler.handleGuestCheckout method end");
		return this.checkFormRedirect(getLoginSuccessURL(), getLoginErrorURL(), pRequest, pResponse);
	}
    
	/**
	 * This method is used to logout the recognized user before login to new
	 * profile or guest user
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
    public boolean handleRefreshUserProfile(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug("BBBProfileFormHandler.handleRefreshUserProfile method Start");
		if (pRequest.getSession() != null) {
			pRequest.getSession().invalidate();
		}
		
		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = getTransactionDemarcation();
		try {
			if (tm != null) {
				td.begin(tm, TransactionDemarcation.REQUIRED);
			}

			// logout without expiring the session, so that the
			// profile is replaced
			boolean expire = getExpireSessionOnLogout();
			setExpireSessionOnLogout(false);
			logDebug(BBBCoreConstants.PROFILE_SESSION_MESSAGE);
			 RepositoryItem preLogoutDataSource = getCurrentDataSource();
			preLogoutUser(pRequest, pResponse);
			//this.handleLogout(pRequest, pResponse);
			// don't allow the possibility of redirect here. A failed logout
			// at this point is converted into a failed login attempt
			int status = checkFormError(null, pRequest, pResponse);
			if (status != STATUS_SUCCESS) {
				return false;
			}
			sendProfileSwapEvent(ProfileSwapEvent.LOGOUT,
                    preLogoutDataSource,
                    getCurrentDataSource());

			status = checkFormError(getLogoutErrorURL(), pRequest, pResponse);
			if (status != STATUS_SUCCESS){ 
				return false;
			
			}

			postLogoutUser(pRequest, pResponse);

			// restore expire flag
			setExpireSessionOnLogout(expire);
			getSessionBean().getValues().put("anonymousLogin", "true");
		} catch (TransactionDemarcationException e) {
			throw new ServletException(e);
		} finally {
			endTransactionDemarcation(tm, td);
		}
		this.logDebug("BBBProfileFormHandler.handleRefreshUserProfile method end.");
		return false;
	}


    /** Override method from ATG OOTB for Post Logout functionality to set session variable for Baby Canada.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @throws ServletException if there was an error while executing the code
     * @throws IOException if there was an error with Servlet IO
     * @return void */

	@Override
    public final void postLogoutUser(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    	logDebug("Start postLogoutUser" );
    	 boolean googleFlow=false;
         if( pRequest.getSession()
  		.getAttribute(BBBInternationalShippingConstants.GOOGLE_FLOW)!=null && ((String)pRequest.getSession().getAttribute(BBBInternationalShippingConstants.GOOGLE_FLOW)).equalsIgnoreCase(BBBCoreConstants.TRUE)){
      	    googleFlow=true;
         }
    	super.postLogoutUser(pRequest, pResponse);
    	 if(googleFlow){
         	pRequest.getSession()
     		.setAttribute(BBBInternationalShippingConstants.GOOGLE_FLOW,BBBCoreConstants.TRUE);
         }
    	 getSessionBean().getPersonalizedSkus().clear();
    	//pRequest.getSession().setAttribute(BBBCoreConstants.BABY_CANADA_SESSION_ATTRIBUTE, babyCAFlagValue);
    	logDebug("End postLogoutUser" );

    }

    /** Deletes the cookie data.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse */
    public void removeOrderCookie(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) {

        this.logDebug("Start: removeOrderCookie method ");

        // add cookie
        final Cookie cookie = new Cookie(this.getOrderCookieName(), "");
        cookie.setMaxAge(0);
        cookie.setPath(this.getOrderCookiePath());
        BBBUtility.addCookie(pResponse, cookie, true);

        this.logDebug("End: removeOrderCookie" + ": cookie removed after logout");

    }

    private void updateLoginAttemptCount(final RepositoryItem profileItem, final boolean resetFlag) {
    	// Re-factored the code for JIRA ticket BBBSL-11403 moved transaction from this method to BBBProfileTools.updateLoginAttemptCount()
    	this.mTools.updateLoginAttemptCount(profileItem, resetFlag);
    	// JIRA ticket BBBSL-11403 end
    }
    
//  R 2.2 Start : Added for Cookie Auto Login for Mobile App 
    
    /**
     * Update the Auto Login Flag for Repository Item
     * @param profileItem
     * @param autoLoginFlag
     * @param pRequest
     */
    private void updateAutoLoginProperty(final RepositoryItem profileItem, final boolean autoLoginFlag, DynamoHttpServletRequest pRequest) {

        final TransactionManager tm = this.getTransactionManager();
        final TransactionDemarcation td = new TransactionDemarcation();
        boolean isException = true;
        boolean autoLoginStatus = false;

        try {

            td.begin(tm, TransactionDemarcation.REQUIRES_NEW);
            autoLoginStatus = this.mTools.updateAutoLogin(profileItem, autoLoginFlag, pRequest);
            if(autoLoginStatus){
            	isException = false;
            }
            if(isLoggingDebug()){
            	logDebug("Auto Login for Profile Updated : " + autoLoginStatus);
            }

        } catch (final TransactionDemarcationException e) {
            this.logError(ERROR_OCCURED_WHILE_UPDATING_AUTO_LOGIN, e);

        } finally {
            try {
                td.end(isException);
            } catch (final TransactionDemarcationException e) {
                this.logError(ERROR_OCCURED_WHILE_UPDATING_AUTO_LOGIN, e);
            }

        }

    }
//  R 2.2 End : Added for Cookie Auto Login for Mobile App 
    
    private void updateCheckoutDefaultValues(final DynamoHttpServletRequest pRequest) throws ServletException {

        this.logDebug(LogMessageFormatter.formatMessage(pRequest,
                "BBBProfileFormHandler.CheckoutDefaults() method Starts"));

        final TransactionManager tm = this.getTransactionManager();
        final TransactionDemarcation td = this.getTransactionDemarcation();
        try {
            td.begin(tm, TransactionDemarcation.REQUIRED);
            final BBBProfileTools profileTools = (BBBProfileTools) this.getProfileTools();
            final String shippingAddressProperty = getPropertyManager().getShippingAddressPropertyName();
            final String shippingAddressName = (String) this.getValue().get(shippingAddressProperty);
            if (!StringUtils.isEmpty(shippingAddressName)) {
                final RepositoryItem shippingAddress = profileTools.getProfileAddress(this.getProfile(),
                        shippingAddressName);
                profileTools.updateProperty(shippingAddressProperty, shippingAddress, this.getProfile());
            }
            final String creditCardProperty = getPropertyManager().getDefaultCreditCardPropertyName();
            final String creditCardName = (String) this.getValue().get(creditCardProperty);

            if (!StringUtils.isEmpty(creditCardName)) {
                final RepositoryItem creditCard = profileTools.getCreditCardByNickname(creditCardName,
                        this.getProfile());
                profileTools.updateProperty(creditCardProperty, creditCard, this.getProfile());
            }
            final String defaultCarrierProperty = getPropertyManager().getDefaultShippingMethodPropertyName();
            profileTools.updateProperty(defaultCarrierProperty, this.getValue().get(defaultCarrierProperty),
                    this.getProfile());

            this.logDebug(LogMessageFormatter.formatMessage(pRequest,
                    "BBBProfileFormHandler.updateCheckoutDefaultValues() method ended"));

        } catch (final TransactionDemarcationException e) {
            this.logError(
                    LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                            + EXCEPTION_UPDATE_CHECKOUT_DEFAULT_VALUES, BBBCoreErrorConstants.ACCOUNT_ERROR_1066), e);
            throw new ServletException(e);
        } catch (final RepositoryException e) {
            this.logError(
                    LogMessageFormatter.formatMessage(pRequest, "RepositoryException"
                            + EXCEPTION_UPDATE_CHECKOUT_DEFAULT_VALUES, BBBCoreErrorConstants.ACCOUNT_ERROR_1067), e);
            throw new ServletException(e);
        } finally {
            try {
                td.end();
            } catch (final TransactionDemarcationException e) {
                this.logError(
                        LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                                + EXCEPTION_UPDATE_CHECKOUT_DEFAULT_VALUES, BBBCoreErrorConstants.ACCOUNT_ERROR_1068),
                                e);
            }
        }

    }
    
    
    /**
     * @param pRequest
     * @param pResponse
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public boolean handleConfirmPassword(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

    	this.logDebug("BBBProfileFormHandler.handleConfirmPassword() method started");
    	// BBBH-391 | Client DOM XSRF changes
    	String siteId = SiteContextManager.getCurrentSiteId();
        populateErrSuccessUrlForConfirmPswd(pRequest, siteId);
        final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
        boolean status = false;
		//boolean isAccountLocked = false;
		RepositoryItem bbbProfile = null;
        final Dictionary<String, String> value = this.getValue();

        String loginEmail = populatePswdEmailInConfirmPassword(value);
        final boolean emailPattern = !BBBUtility.isValidEmail(loginEmail);
        this.errorMap = new HashMap<String, String>();
        
        if (BBBUtility.isEmpty(loginEmail)) {
            processEmptyLoginEmail(pRequest, errorPlaceHolderMap);
        } else if (emailPattern) {
            processInvalidEmailLogin(pRequest, errorPlaceHolderMap);
        }
        if (this.errorMap.isEmpty()) {
		
        	bbbProfile = this.mTools.getItemFromEmail(loginEmail);
			
			if(bbbProfile != null){

					final RepositoryItem validProfile = findUser(pRequest, pResponse);
					if(validProfile != null){

						final Integer currentSecurityCode = (Integer) bbbProfile.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS);
						if(COOKIE_LOGIN_SECURITY_STATUS.equals(currentSecurityCode)) {
							this.getProfile().setPropertyValue(BBBCheckoutConstants.SECURITY_STATUS, LOGGED_IN_USER_SECURITY_STATUS);
							status = true;
						}
						processRepriceOrder(pRequest, pResponse, validProfile);
					}else{
						processNullProfileConfirmPassword(pRequest, errorPlaceHolderMap, bbbProfile);
					}
				
			}else{
				final String loginEmptyError = getMessageHandler().getErrMsg( BBBCoreConstants.ERR_PROFILE_REPOSITORY_PROFILE_TYPE_FETCHED_NULL, pRequest.getLocale().getLanguage(), errorPlaceHolderMap, null);
	            this.addFormException(new DropletException(BBBCoreConstants.ERR_PROFILE_REPOSITORY_PROFILE_TYPE_FETCHED_NULL, BBBCoreConstants.ERR_PROFILE_REPOSITORY_PROFILE_TYPE_FETCHED_NULL));
                this.errorMap.put(BBBCoreConstants.CREATE_PROFILE_SYSTEM_ERROR, loginEmptyError);
			}
			
		}
		if (isLoggingDebug()) {
			logDebug("BBBProfileFormHandlerCheckPassword | Validation | ends");
		}
		getSessionBean().getValues().put("anonymousLogin", "true");
		if (pRequest.getContextPath().equals(BBBCoreConstants.CONTEXT_REST)) {
			return status;

		} else {
			if(BBBCoreConstants.TRUE.equals(pRequest.getParameter(BBBCoreConstants.RECGONIZED_USER_FLOW)))
				return this.checkFormRedirect(getLoginSuccessURL(),getLoginErrorURL(),pRequest, pResponse);
			else
			return this.checkFormRedirect((String) pRequest.getSession()
					.getAttribute(BBBCoreConstants.REDIRECT_URL),
					pRequest.getContextPath() + getConfirmPasswordErrorUrl(),
					pRequest, pResponse);
		}
		
	}


	private void processNullProfileConfirmPassword(final DynamoHttpServletRequest pRequest,
			final Map<String, String> errorPlaceHolderMap, RepositoryItem bbbProfile) {
		boolean isAccountLocked;
		isAccountLocked = this.mTools.isAccountLocked(bbbProfile);
		

		if (isAccountLocked) {
			processLockedValidProfilePreLogin(pRequest, errorPlaceHolderMap);
		}else{
			processInvalidEmailLogin(pRequest, errorPlaceHolderMap);
		    this.updateLoginAttemptCount(bbbProfile, false);
		}
	}


	private void processRepriceOrder(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, final RepositoryItem validProfile)
					throws ServletException, IOException {
		try {
			repriceOrder(pRequest, pResponse, validProfile);
		} catch (final CommerceException e) {
			final String errMsg = getLblTxtTemplateManager().getErrMsg(BBBCoreConstants.ERR_LBL_PRICING_EXCEPTION_LOGIN, pRequest.getLocale().getLanguage(), 
					null);
			logError(LogMessageFormatter.formatMessage(null, errMsg, BBBCoreErrorConstants.CHECKOUT_ERROR_1052), e);
			if (getOrder() instanceof OrderImpl) {
				((OrderImpl)getOrder()).invalidateOrder();
			}
			try {
				repriceOrder(pRequest, pResponse, validProfile);
			} catch (CommerceException e1) {
				logError(LogMessageFormatter.formatMessage(null, errMsg, BBBCoreErrorConstants.CHECKOUT_ERROR_1052), e1);
				this.addFormException(new DropletException(errMsg, BBBCoreErrorConstants.CHECKOUT_ERROR_1052));
				this.errorMap.put(BBBCoreConstants.LOGIN_PASSWORD_ERROR, errMsg);
			}
		}
	}


	private void repriceOrder(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse, final RepositoryItem validProfile)
			throws ServletException, IOException, CommerceException {
		try {
			/***
			 * BBBP-7014 : This method is used to reprice the order.
			 * When user directly access the /checkout page that
			 * time getPriceInfo() is not available with current
			 * order details which results in NPE.
			 */
			if (null != getOrder()
					&& null == getOrder().getPriceInfo()) {
				this.mTools.repriceOrder(getOrder(),
						validProfile, this.getUserPricingModels(),
						getUserLocale(pRequest, pResponse),
						this.mTools.getRepriceOrderPricingOp());
			}
			// BBBP-7014 End.
			updateFavouriteStore(pRequest, pResponse);
		} catch (final RepositoryException e) {
			final String errMsg = "RepositoryException : BBBProfileFormHandler.updateFavouriteStore while updating the user Profile";
			logError(LogMessageFormatter.formatMessage(null,
					errMsg,
					BBBCoreErrorConstants.ACCOUNT_ERROR_1159), e);
			this.addFormException(new DropletException(errMsg,
					BBBCoreErrorConstants.ACCOUNT_ERROR_1159));
			this.errorMap.put(
					BBBCoreConstants.LOGIN_PASSWORD_ERROR, errMsg);
		} catch (final NotSupportedException exc) {
			final String errMsg = "NotSupportedException in BBBProfileFormHandler.updateFavouriteStore";
			logError(LogMessageFormatter.formatMessage(null,
					errMsg,
					BBBCoreErrorConstants.ACCOUNT_ERROR_1237), exc);
			this.addFormException(new DropletException(errMsg,
					BBBCoreErrorConstants.ACCOUNT_ERROR_1237));
			this.errorMap.put(
					BBBCoreConstants.LOGIN_PASSWORD_ERROR, errMsg);
		} catch (final SystemException exc) {
			final String errMsg = "SystemException in BBBProfileFormHandler.updateFavouriteStore";
			logError(LogMessageFormatter.formatMessage(null,
					errMsg,
					BBBCoreErrorConstants.ACCOUNT_ERROR_1238), exc);
			this.addFormException(new DropletException(errMsg,
					BBBCoreErrorConstants.ACCOUNT_ERROR_1238));
			this.errorMap.put(
					BBBCoreConstants.LOGIN_PASSWORD_ERROR, errMsg);
		}
	}


	private String populatePswdEmailInConfirmPassword(final Dictionary<String, String> value) {
		String loginEmail = this.getStringValueProperty(BBBCoreConstants.EMAIL);
        String password = this.getStringValueProperty(BBBCoreConstants.PASSWORD);
        this.mTools = (BBBProfileTools) this.getProfileTools();
    	PropertyManager pmgr = mTools.getPropertyManager();
        String loginPropertyName = pmgr.getLoginPropertyName();
        if(null == loginEmail)
        {
        loginEmail = getStringValueProperty(loginPropertyName);
        }
        if (!BBBUtility.isEmpty(loginEmail)) {
		    loginEmail = loginEmail.toLowerCase();
		    value.put(getPropertyManager().getLoginPropertyName(), loginEmail);
		}
        if (!BBBUtility.isEmpty(password)) {
		    password = password.trim();
		    value.put(getPropertyManager().getPasswordPropertyName(), password);
		}
		return loginEmail;
	}


	private void populateErrSuccessUrlForConfirmPswd(final DynamoHttpServletRequest pRequest, String siteId) {
		if (!BBBUtility.siteIsTbs(siteId)) {
        	setConfirmPasswordSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(BBBURLConstants.CONFIRM_PASSWORD));
        	setConfirmPasswordErrorUrl(pRequest.getContextPath() + getErrorUrlMap().get(BBBURLConstants.CONFIRM_PASSWORD));
		}
    	if (BBBCoreConstants.REC_USER_CHECKOUT.equals(pRequest.getSession().getAttribute(BBBCoreConstants.REC_USER_CHECKOUT))) {
    		setConfirmPasswordErrorUrl(getConfirmPasswordErrorUrl() + BBBCoreConstants.CHECKOUT_TRUE);
		}
	}
        
    /** This method is used to make default credit card API.
     * 
     * @param pRequest DynamoHttpServletRequest
     * @param pResponse DynamoHttpServletResponse
     * @return boolean
     * @throws ServletException Exception
     * @throws IOException Exception */
    public final boolean handleCheckoutDefaultsAPI(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

        this.logDebug(LogMessageFormatter.formatMessage(pRequest,
                "BBBProfileFormHandler.handleCheckoutDefaultsAPI() method Starts"));

        this.updateCheckoutDefaultValues(pRequest);

        this.logDebug(LogMessageFormatter.formatMessage(pRequest,
                "BBBProfileFormHandler.handleCheckoutDefaultsAPI() method ended"));
        if (this.getFormExceptions().size() > 0) {
            return false;
        }
        return true;
    }

    @Override
    public final void logError(final String message) {
        if (this.isLoggingError()) {
            this.logError(message, null);
        }
    }

    @Override
    public final void logDebug(final String message) {
        if (this.isLoggingDebug()) {
            this.logDebug(message, null);
        }
    }

    /** @param message */
    public final void logMethodError(final String message) {
        if (this.isLoggingError()) {
            this.logError(Thread.currentThread().getStackTrace()[2].getMethodName() + message, null);
        }
    }

    /** @param message */
    public final void logMethodDebug(final String message) {
        if (this.isLoggingDebug()) {
            this.logDebug(Thread.currentThread().getStackTrace()[2].getMethodName() + message, null);
        }
    }
    /**
     * Changes the users password. 
     * If any errors occur in the process, form errors will be added.
     *
     * @return true if the password is changed correctly
     * @param pRequest the servlet's request
     * @param pResponse the servlet's response
     * @exception ServletException if there was an error while executing the code
     * @exception IOException if there was an error with servlet io
     */
    protected boolean changePassword(DynamoHttpServletRequest pRequest,
    		DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException 
  	{
    	ProfileTools ptools = getProfileTools();
    	PropertyManager pmgr = ptools.getPropertyManager();
    	
    	try {
    		boolean changePassword = true;
    		String password = getStringValueProperty(pmgr.getPasswordPropertyName());
    		String oldPassword = getStringValueProperty(OLDPASSWORD_PARAM);      
    		
          if (oldPassword == null || oldPassword.trim().length() == 0) {
            oldPassword = null;

            if (isConfirmOldPassword()) {
              changePassword = false;
              if (isLoggingDebug()){
            	  logDebug("handleChangePassword: missing old password");  
              }
              String msg = formatUserMessage(MSG_MISSING_OLD_PASSWORD, pRequest);
              String propertyPath = generatePropertyPath(pmgr.getPasswordPropertyName());
              addFormException(new DropletFormException(msg, propertyPath,
                  MSG_MISSING_OLD_PASSWORD));
            }
          }
          
    		changePassword = populateChangePassword(pRequest, ptools, pmgr.getLoginPropertyName(),
    				pmgr.getPasswordPropertyName(), changePassword, password, oldPassword);

    		if (changePassword) {
    			MutableRepository repository = ptools.getProfileRepository();
    			MutableRepositoryItem mutableItem = repository.getItemForUpdate(getRepositoryId(), getCreateProfileType());
    			
    			String login = null;
    			login = populateLoginChangePassword(pRequest, pmgr.getLoginPropertyName(), mutableItem, login);
    			
    			String currentPassword = (String) DynamicBeans.
  				getSubPropertyValue(mutableItem, pmgr.getPasswordPropertyName());
    			String oldPasswordValue = null;
    			
    			oldPasswordValue = validateOldPassword(pmgr, oldPassword, login, currentPassword);    			
    			
    			if (currentPassword == null || currentPassword.trim().length() == 0){
    				currentPassword = null;
    			}
    			if ((currentPassword == null && oldPasswordValue == null) ||
    					(currentPassword != null && oldPasswordValue != null && 
    							currentPassword.equals(oldPasswordValue))) {

    				updateProfileWithNewPassword(pmgr, pmgr.getPasswordPropertyName(), pmgr.getLastPasswordUpdatePropertyName(),
    						pmgr.getGeneratedPasswordPropertyName(), password, mutableItem, login);
    				
    				// update and set the previous password array property
    				if (ptools.getPreviousNPasswordManager()!=null){
    					ptools.getPreviousNPasswordManager().updatePreviousPasswordsProperty(mutableItem, currentPassword);
    				}
    				// remove the passwordexpired flag from session
      			pRequest.getSession().setAttribute("passwordexpired", 
      					Boolean.FALSE);  				
    				
    				if (!getFormError()) {
    					if (isLoggingDebug())
    						logDebug("updating the password in the repository");
    					repository.updateItem(mutableItem);
    					
    					return true;
    				}    
    			}
    			else {
    				String msg = formatUserMessage(MSG_PERMISSION_DEFINED_PASSWORD_CHANGE, pRequest);
    				addFormException(new DropletException(msg, MSG_PERMISSION_DEFINED_PASSWORD_CHANGE));
    			}            
    		}       
    	}
    	catch (RepositoryException exc) {
    		String msg = formatUserMessage(MSG_ERR_UPDATING_PROFILE, pRequest);
    		addFormException(new DropletException(msg, exc, MSG_ERR_UPDATING_PROFILE));      
    		if (isLoggingError())
    			logError(exc);
    	}
    	catch (PropertyNotFoundException exc) {
    		String msg = formatUserMessage(MSG_NO_SUCH_PROFILE_PROPERTY, pmgr.getPasswordPropertyName(), pRequest);
    		String propertyPath = generatePropertyPath(pmgr.getPasswordPropertyName());
    		addFormException(new DropletFormException(msg, exc, propertyPath, MSG_NO_SUCH_PROFILE_PROPERTY));
    		if (isLoggingError())
    			logError(exc);
    	}	
    	
    	return false;
  	}


	private void updateProfileWithNewPassword(PropertyManager pmgr, String passwordPropertyName,
			String lastPasswordUpdatePropertyName, String generatedPwdPropertyName, String password,
			MutableRepositoryItem mutableItem, String login) throws PropertyNotFoundException {
		DynamicBeans.setSubPropertyValue(mutableItem, 
				passwordPropertyName, 
				pmgr.generatePassword(login, password));

		// set the password last update property to now
		DynamicBeans.setSubPropertyValue(mutableItem, 
				lastPasswordUpdatePropertyName, 
				Calendar.getInstance().getTime());

		// flag that this passwords was not generated
		DynamicBeans.setSubPropertyValue(mutableItem, 
				generatedPwdPropertyName, 
				Boolean.FALSE);
	}


	private String validateOldPassword(PropertyManager pmgr, String oldPassword, String login,
			String currentPassword) {
		String oldPasswordValue;
		if(isConfirmOldPassword()){
			RepositoryItem repoItem = PBKDF2PasswordHasher.mPwdHashingService.getPasswordSalt(PBKDF2PasswordHasher.mPwdHashingService.sha256(login));
			String saltValue = null;
			if(repoItem!=null){
			  saltValue = (String)repoItem.getPropertyValue(PasswordHashingService.PROPERTY_NAME_VALUE);
		      }
		     if(saltValue!=null){
				 oldPasswordValue = pmgr.generatePassword(login, oldPassword);
			 }else{
			    	// Creating Salt Value
			          if(login!=null){
			          	if(!createPasswordSalt(login.toLowerCase())){
			          		logDebug("fail to create salt in changePassword Method");
			          	}
			          }
				 oldPasswordValue =	PBKDF2PasswordHasher.mDgestPwdHasher.encryptPassword(oldPassword);
			 }
		}
		else{
			oldPasswordValue = currentPassword;
		}
		return oldPasswordValue;
	}


	private String populateLoginChangePassword(DynamoHttpServletRequest pRequest, String loginPropertyName,
			MutableRepositoryItem mutableItem, String login) {
		try {
			login = (String) DynamicBeans.
			getSubPropertyValue(mutableItem, loginPropertyName);
		}
		catch (PropertyNotFoundException exc) {
			String msg = formatUserMessage
			(MSG_NO_SUCH_PROFILE_PROPERTY, loginPropertyName, pRequest);
			String propertyPath = generatePropertyPath(loginPropertyName);
			addFormException(new DropletFormException
					(msg, exc, propertyPath, MSG_NO_SUCH_PROFILE_PROPERTY));
			if (isLoggingError())
				logError(exc);
		}
		return login;
	}


	private boolean populateChangePassword(DynamoHttpServletRequest pRequest, ProfileTools ptools,
			String loginPropertyName, String passwordPropertyName, boolean changePassword, String password,
			String oldPassword) throws RepositoryException {
		if ((password == null) || (password.equals(""))) {
			changePassword = false;
			if (isLoggingDebug())
				logDebug("handleChangePassword: missing password");
			String msg = formatUserMessage(MSG_MISSING_PASSWORD, pRequest);
			String propertyPath = generatePropertyPath(passwordPropertyName);
			addFormException(new DropletFormException(msg, propertyPath, MSG_MISSING_PASSWORD));
		}
		
		if ((changePassword) && (isConfirmPassword())) {
			String confirmPassword = getStringValueProperty(CONFIRMPASSWORD_PARAM);
			if ((confirmPassword == null) || (confirmPassword.equals("")) ||
					(! password.equals(confirmPassword))) {
				changePassword = false;
				String msg = formatUserMessage(MSG_PASSWORDS_DO_NOT_MATCH, pRequest);
				addFormException(new DropletException(msg, MSG_PASSWORDS_DO_NOT_MATCH));
			}
		}

		// make sure that old and new passwords are not equal
		if (changePassword) {
			if (oldPassword!=null && password!=null && password.equals(oldPassword)) {
				changePassword = false;
				String msg = formatUserMessage(MSG_PASSWORD_SAME_AS_OLD_PASSWORD, pRequest);
				addFormException(new DropletException(msg, MSG_PASSWORD_SAME_AS_OLD_PASSWORD));
			}
		}
		
		// Run password through the password checker
		if (changePassword && ptools.getPasswordRuleChecker()!=null && ptools.getPasswordRuleChecker().isEnabled()) {

			MutableRepository repository = ptools.getProfileRepository();
			MutableRepositoryItem mutableItem = repository.getItemForUpdate(getRepositoryId(), getCreateProfileType());
			
			String login = null;
			login = populateLoginChangePassword(pRequest, loginPropertyName, mutableItem, login);

			Map<String,String> map = null;
			if (login!=null) {
				map = new HashMap<String,String>();
				map.put(loginPropertyName, login);
			}
				
			boolean passedRules = ptools.getPasswordRuleChecker().checkRules(password, map);
			
			if (!passedRules) {
				changePassword = false;
				
				addFormException(new DropletException(ptools.getPasswordRuleChecker().getLastRuleCheckedDescription()));
			}
		}
		return changePassword;
	}

    /**
     * Check Gift List Items For Shipping Method Availability and sets property to true if not available
     *
     * @return
     * @param pRequest the servlet's request
     */
    public void checkGiftListItemsForShipAvailability(final DynamoHttpServletRequest pRequest){

    	this.logDebug("BBBProfileFormHandler.checkGiftListItemsForShipAvailability() method started");
    	final RepositoryItem destWishList = ((RepositoryItem) this.getProfile().getPropertyValue(
                BBBCoreConstants.WISHLIST));
        if (destWishList != null) {
            @SuppressWarnings ("unchecked")
            //Iterate over the wish list items
            final List<RepositoryItem> destWishtListItems = (List<RepositoryItem>) (destWishList
                    .getPropertyValue(BBBCoreConstants.GIFT_LIST_ITEMS));
            for (final RepositoryItem destItemsz : destWishtListItems) {
            	final MutableRepositoryItem destItem = (MutableRepositoryItem) destItemsz;
                String skuId = (String)destItem.getPropertyValue(BBBCoreConstants.CATALOG_REF_ID);
                try {
                    String ltlShipMethod = (String)destItem.getPropertyValue(BBBCatalogConstants.LTL_SHIP_METHOD);
                    String siteId = (String)destItem.getPropertyValue("siteId");
                    boolean isSkuLtl = getBbbCatalogTools().isSkuLtl(siteId, skuId);

                    //Only continue when the sku is LTL and shipping method is not empty
                    if(BBBUtility.isNotEmpty(ltlShipMethod) && isSkuLtl){
                    		boolean isAssemblyOffered = false;
                    		if(ltlShipMethod.equalsIgnoreCase(BBBCatalogConstants.WHITE_GLOVE_ASSEMBLY_SHIP_METHOD)){
                    			ltlShipMethod = BBBCatalogConstants.WHITE_GLOVE_SHIP_METHOD;
                    			isAssemblyOffered = true;
                    		}

                    		//Get the shipping method avaliablity and set in transient property if Gift List
                    		boolean isShippingMethodExistsForSku = getBbbCatalogTools().
                    				isShippingMethodExistsForSku(siteId, skuId, ltlShipMethod, isAssemblyOffered);
                    		logDebug("The shipping method : " + ltlShipMethod + "availability is " + isShippingMethodExistsForSku + " for sku " + skuId);
                    		destItem.setPropertyValue(BBBCoreConstants.MSGSHIPMETHODUNSUPPORTED, Boolean.valueOf(!isShippingMethodExistsForSku));

                    		((MutableRepository) this.getGiftListManager().getGiftlistTools()
                    				.getGiftlistRepository()).updateItem(destItem);
                   }
                } catch (final RepositoryException e) {
                    this.logError(LogMessageFormatter.formatMessage(pRequest,
                            BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP,
                            BBBCoreErrorConstants.GIFT_ERROR_1000), e);
                    this.logDebug(BBBCoreErrorConstants.ERROR_GIFT_CHECK_REP);
                } catch (final BBBBusinessException e) {
                	String msg = "Error while retrieving product details for item [" + skuId + "]";
                	this.logError(msg, e);
                }catch(BBBSystemException e){
                	String msg = "Error while retrieving product details for item [" + skuId + "]";
                	this.logError(msg, e);
            	}
            }
            this.logDebug("BBBProfileFormHandler.checkGiftListItemsForShipAvailability() method ends");
        }

    }
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBProfileFormHandler [siteContext=").append(getSiteContext()).append(", mRegistrySearchVO=")
        .append(getRegistrySearchVO()).append(", registrationTemplateEmailInfo=")
        .append(getRegistrationTemplateEmailInfo()).append(", mOrder=").append(getOrder())
        .append(", messageHandler=").append(getMessageHandler()).append(", facebookProfileTool=")
        .append(getFacebookProfileTool()).append(", mGiftRegistryManager=").append(getGiftRegistryManager())
        .append(", mSiteGroup=").append(getSiteGroup()).append(", mGiftListHandler=")
        .append(getGiftListHandler()).append(", mShippingGroupMapContainer=")
        .append(getShippingGroupMapContainer()).append(", creditCardTools=").append(getCreditCardTools())
        .append(", profileManager=").append(getProfileManager()).append(", mTools=").append(this.mTools)
        .append(", mCatalogTools=").append(getBbbCatalogTools()).append(", propertyManager=")
        .append(getPropertyManager()).append(", giftlistManager=").append(getGiftListManager())
        .append(", profileService=").append(getProfileService()).append(", paymentGroupManager=")
        .append(getPaymentGroupManager()).append(", savedItemsSessionBean=").append(getSavedItemsSessionBean())
        .append(", bbbOrder=").append(getBBBOrder()).append(", mSessionBean=").append(getSessionBean())
        .append(", errorMap=").append(this.errorMap).append(", mAssoSite=").append(getAssoSite())
        .append(", mLoginEmail=").append(getLoginEmail()).append(", mReclaimLegacyAccountSuccessURL=")
        .append(getReclaimLegacyAccountSuccessURL()).append(", mReclaimLegacyAccountIncorrectPasswordURL=")
        .append(getReclaimLegacyAccountIncorrectPasswordURL()).append(", mLegacyPasswordSuccessURL=")
        .append(getLegacyPasswordSuccessURL()).append(", mStrReminderSent=").append(getStrReminderSent())
        .append(", mReclaimLegacyIncorrectPasswordSuccessURL=")
        .append(getReclaimLegacyIncorrectPasswordSuccessURL()).append(", addressAdded=")
        .append(getAddressAdded()).append(", legacyUser=").append(getLegacyUser())
        .append(", legacyPasswordPopupURL=").append(getLegacyPasswordPopupURL()).append(", userCheckingOut=")
        .append(getUserCheckingOut()).append(", loginURL=").append(getLoginURL()).append(", orderCookieName=")
        .append(getOrderCookieName()).append(", profileID=").append(getProfileID())
        .append(", orderCookiePath=").append(getOrderCookiePath()).append(", cartURL=").append(getCartURL())
        .append(", migratedUserPopupURL=").append(getMigratedUserPopupURL()).append(", profileCookieName=")
        .append(getProfileCookieName()).append(", profileCookiePath=").append(getProfileCookiePath())
        .append(", storeContextPath=").append(getStoreContextPath()).append(", emailAddress=")
        .append(getEmailAddress()).append(", facebookLinking=").append(getFacebookLinking())
        .append(", mUpdateCardErrorURL=").append(getUpdateCardErrorURL()).append(", mUpdateCardSuccessURL=")
        .append(getUpdateCardSuccessURL()).append(", mCreateCardErrorURL=").append(getCreateCardErrorURL())
        .append(", updateAddressErrorURL=").append(getUpdateAddressErrorURL()).append(", newAddressErrorURL=")
        .append(getNewAddressErrorURL()).append(", mRemoveCard=").append(getRemoveCard())
        .append(", updateAddressSuccessURL=").append(getUpdateAddressSuccessURL())
        .append(", mRegistrationSuccessURL=").append(getRegistrationSuccessURL())
        .append(", mRegistrationErrorURL=").append(getRegistrationErrorURL())
        .append(", mExtenstionSuccessURL=").append(getExtenstionSuccessURL())
        .append(", mCreateCardSuccessURL=").append(getCreateCardSuccessURL())
        .append(", mDefaultShippingAddress=").append(getDefaultShippingAddress())
        .append(", mDefaultBillingAddress=").append(getDefaultBillingAddress())
        .append(", mNicknameValueMapKey=").append(getNicknameValueMapKey()).append(", mAddressIdValueMapKey=")
        .append(getAddressIdValueMapKey()).append(", mNewNicknameValueMapKey=")
        .append(getNewNicknameValueMapKey()).append(", mShippingAddressNicknameMapKey=")
        .append(getShippingAddressNicknameMapKey()).append(", mAddressId=").append(getAddressId())
        .append(", mEditAddress=").append(getEditAddress()).append(", mDefaultCard=")
        .append(getDefaultCard()).append(", mEditCard=").append(getEditCard())
        .append(", mPreRegisterErrorURL=").append(getPreRegisterErrorURL())
        .append(", mPreRegisterSuccessURL=").append(getPreRegisterSuccessURL())
        .append(", mRemoveCardErrorURL=").append(getRemoveCardErrorURL()).append(", mRemoveCardSuccessURL=")
        .append(getRemoveCardSuccessURL()).append(", mCreditCardYearMaxLimit=")
        .append(getCreditCardYearMaxLimit()).append(", profileCookieAge=").append(getProfileCookieAge())
        .append(", emailOptIn=").append(isEmailOptIn()).append(", showMigratedUserPopupURL=")
        .append(isShowMigratedUserPopupURL()).append(", mSharedCheckBoxEnabled=")
        .append(getSharedCheckBoxEnabled()).append(", mFormErrorVal=").append(isFormErrorVal())
        .append(", mMakePreferredSet=").append(isMakePreferredSet()).append(", mSuccessMessage=")
        .append(isSuccessMessage()).append(", mChangePasswordSuccessMessage=")
        .append(isChangePasswordSuccessMessage()).append(", mLegacyForgetPasswordStatus=")
        .append(isLegacyForgetPasswordStatus()).append(", migrationFlag=").append(this.migrationFlag)
        .append(", resetPasswordFlag=").append(this.resetPasswordFlag).append(", login=").append(isLogin())
        .append(", legacyUserBoolean=").append(isLegacyUserBoolean()).append(", saveCreditCardInfoToProfile=")
        .append(isSaveCreditCardInfoToProfile()).append(", fromMobile=").append(isFromMobile())
        .append(", migratedAccount=").append(isMigratedAccount()).append(", loginFromCheckout=")
        .append(isLoginFromCheckout()).append(", userMigratedLoginProp=").append(isUserMigratedLoginProp())
        .append(", mUseShippingAddressAsDefault=").append(isUseShippingAddressAsDefault())
        .append(", mUseBillingAddressAsDefault=").append(isUseBillingAddressAsDefault())
        .append(", autoLoginAfterChangePassword=").append(isAutoLoginAfterChangePassword())
        .append(", mEditValue=").append(this.mEditValue).append(", mBillAddrValue=")
        .append(getBillAddrValue()).append(", mBillingAddressPropertyList=")
        .append(getBillingAddressPropertyList()).append(", mRequiredBillingAddressPropertyList=")
        .append(getRequiredBillingAddressPropertyList()).append(", mAddressProperties=")
        .append(Arrays.toString(getAddressProperties())).append(", mCardProperties=")
        .append(Arrays.toString(getCardProperties())).append(", mImmutableCardProperties=")
        .append(Arrays.toString(getImmutableCardProperties())).append(", idGenerator=")
        .append(this.getIdGenerator()).append(", newAddressSuccessURL=").append(getNewAddressSuccessURL())
        .append("]");
        return builder.toString();
    }
    
    
    public final boolean handleResetPasswordToken(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug("BBBProfileFormHandler.handleResetPasswordWithToken() method started");
		this.logDebug("BBBProfileFormHandler.handleResetPasswordToken() method started");
		// BBBH-391 | Client DOM XSRF changes
		final String siteId = getSiteContext().getSite().getId();
        if (!BBBUtility.siteIsTbs(siteId)) {
    		setChangePasswordSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(BBBURLConstants.RESET_PASSWORD));
    		setChangePasswordErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(BBBURLConstants.RESET_PASSWORD));
		}
        String email = (String) pRequest.getSession().getAttribute(BBBCoreConstants.EMAIL_ADDR);
        String oldPassword = (String) pRequest.getSession().getAttribute(BBBCoreConstants.OLD_PASSWORD);
//      email=getSessionBean().getAccountVo().getEmail();
//      oldPassword=getSessionBean().getAccountVo().getPassword();
		
		email=(String) this.getValue().get(BBBCoreConstants.LOGIN_EMAIL_ID);
        if (this.errorMap.isEmpty() && BBBUtility.isNotEmpty(email)) {
            email = email.toLowerCase().trim();
            this.preChangePassword(pRequest, pResponse);
            if(this.getFormExceptions() != null && this.getFormExceptions().size() > 0)
            {
            	this.setChangePasswordErrorURL(this.getChangePasswordErrorURL()+BBBCoreConstants.ADD_TOKEN+ URLEncoder.encode((String) this.getValue().get(BBBCoreConstants.URL_TOKEN),BBBCoreConstants.UTF_8));
            	this.checkFormRedirect(this.getChangePasswordSuccessURL(), this.getChangePasswordErrorURL(), pRequest,
                        pResponse);
            }
            if(!isAccountVoNull())
            {
            	oldPassword=getSessionBean().getAccountVo().getPassword();
            }
            if (BBBUtility.isNotEmpty(oldPassword)) {
    			this.getValue().put(BBBCoreConstants.OLD_PASSWORD, oldPassword);
            }
            final TransactionManager tm = this.getTransactionManager();
            final TransactionDemarcation td = this.getTransactionDemarcation();
            if (tm != null) {
                try {
					td.begin(tm, TransactionDemarcation.REQUIRED);
				   if (this.getFormExceptions().size() == 0) {
                final String sPassword = this.getStringValueProperty(getPropertyManager().getPasswordPropertyName());
                final boolean isUpdated = getProfileManager().updatePasswordFromToken(email, sPassword);
                if (isUpdated) {
                    this.getProfile().setPropertyValue(getPropertyManager().getResetPasswordPropertyName(),
                            Boolean.FALSE);
                   /* pRequest.getSession().removeAttribute(BBBCoreConstants.EMAIL_ADDR);
                    pRequest.getSession().removeAttribute(OLD_PWD);
                    pRequest.getSession().removeAttribute("fstName");
                    pRequest.getSession().removeAttribute("lstName");*/
                    getSessionBean().setAccountVo(null);
                    pRequest.getSession().removeAttribute(BBBCoreConstants.FORGET_EMAIL);
                       } else if (!isUpdated) {
                    final String errMsg = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR,
                            pRequest.getLocale().getLanguage(), null, null);
                    this.addFormException(new DropletException(errMsg, BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR));
                    this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, errMsg);
                }
            }} catch (TransactionDemarcationException e) {
            	
				this.logError(
                        LogMessageFormatter.formatMessage(pRequest, TRANSACTION_DEMARCATION
                                + EXCEPTION_RESET_PASSWORD_TOKEN, BBBCoreErrorConstants.ACCOUNT_ERROR_1384),
                                e);
                throw new ServletException(e);
			}finally
            {try {
                if (tm != null) {
                    td.end();
                }
            } catch (final TransactionDemarcationException e) {
                this.logError(TRANSACTION_DEMARCATION_ERROR_IN_COMMITING_TRANSACTION, e);
            }
				
            	}
            }
        }
        if (BBBUtility.isEmpty(email)) {
            this.logDebug("Email is null in current Request-session");
            final Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
            if (!profile.isTransient()) {
                super.handleChangePassword(pRequest, pResponse);
                if (this.getFormExceptions().size() == 0) {
                    //isChangePasswordSuccessMessage() = true;
                	setChangePasswordSuccessMessage(true);
                }
            } else {
                final String errMsg = getMessageHandler().getErrMsg(BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR,
                        pRequest.getLocale().getLanguage(), null, null);
                this.addFormException(new DropletException(errMsg, BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR));
                this.errorMap.put(BBBCoreConstants.PASSWORD_ERROR, errMsg);
            }
        }
        this.logDebug("BBBProfileFormHandler.handleResetPasswordToken() method ends");

        return this.checkFormRedirect(this.getChangePasswordSuccessURL(), this.getChangePasswordErrorURL(), pRequest,
                pResponse);
    
    	
            }

	/**
	 * This method is used to update favorite store when logged-in
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws SystemException
	 * @throws NotSupportedException
	 * @throws RepositoryException 
	 */
	private void updateFavouriteStore(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws SystemException,
			NotSupportedException, RepositoryException {
		final String favStoreId = (String) pRequest.getSession().getAttribute(
				BBBCoreConstants.MAKE_FAVOURITE_STORE_ID);
		if (isLoggingDebug()) {
			logDebug("BBBProfileFormHandler.updateFavouriteStore() method started");
		}
		final Integer currentSecurityCode = (Integer) this.getProfile()
				.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS);
		Transaction pTransaction = null;
		boolean status = false;
		boolean isError = false;
		if (!BBBUtility.isEmpty(favStoreId)
				&& !COOKIE_LOGIN_SECURITY_STATUS.equals(currentSecurityCode)) {
			try {
				pTransaction = this.ensureTransaction();
				status = getProfileManager().updateSiteItem(
						this.getProfile(),
						SiteContextManager.getCurrentSiteId(), favStoreId,
						BBBCoreConstants.FAVOURITE_STORE_ID);
			} finally {
				endTransaction(isError, pTransaction);
				pRequest.getSession().removeAttribute(
						BBBCoreConstants.MAKE_FAVOURITE_STORE_ID);

			}
		}
		if (isLoggingInfo()) {
			logInfo("User Profile favStore update status = " + status
					+ " for store id " + favStoreId + " & site "
					+ SiteContextManager.getCurrentSiteId());
		}
		logDebug("BBBProfileFormHandler.updateFavouriteStores() method end.");
	}
	
	/**
	 * Method to store User challenge Question in Challenge Question Repository
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	
	public final boolean handleChallengeQuestion(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		this.logDebug("BBBProfileFormHandler.handleChallengeQuestion() method started");
		 Map<String, Object> questionMap=  this.getEditValue();
		 BBBProfileTools profileTool = (BBBProfileTools) getProfileTools();
		 String profileId = getProfile().getDataSource().getRepositoryId();
		 final Collection<Object> values = questionMap.values();
		 boolean updateChallengeQuestion = true;
		if (values != null && !values.isEmpty()) {
			 for (final Object obj : values) {
				 if(obj == null || obj.toString().isEmpty())
				 {
					 updateChallengeQuestion = false;
				 }
			}
		}
		 if (updateChallengeQuestion) {
			profileTool.storeChallengeQuestionInProfile(profileId, questionMap);
			this.setChallengeQuestionUpdated(true);
		}
		this.logDebug("BBBProfileFormHandler.handleChallengeQuestion() method ends");
        return this.checkFormRedirect(getHandleChallengeQuestionSuccesUrl(), getHandleChallengeQuestionFailureUrl(), pRequest,
                pResponse);
                }
			
	public final boolean handleStoreChallengeQuestion(
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
	
		this.setHandleChallengeQuestionSuccesUrl("");
		this.setHandleChallengeQuestionFailureUrl("");
        final boolean status = this.handleChallengeQuestion(pRequest,pResponse);
     
        return status;
	}
	
	
	
	/* (non-Javadoc)
	 * @see atg.userprofiling.ProfileForm#updatePasswordValue(java.lang.Object, java.lang.String, atg.servlet.DynamoHttpServletRequest, atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	protected Object updatePasswordValue(Object pValue, String pSalt,
			DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		// Passing salt value as null - If we are passing value for salt then it generating hash key based on salt value rather than login
		return super.updatePasswordValue(pValue, null, pRequest, pResponse);
	}
	
	/**
	 * This method is for updating login attempt count in case of user provides wrong password.
	 * @param pRequest
	 * @param pResponse
	 * @return 
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected RepositoryItem findUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws ServletException, IOException {
		logDebug("BBBProfileFormHandler.findUser : Starts");
		RepositoryItem user = super.findUser(pRequest, pResponse);
		if (null != user) {
			logDebug("BBBProfileFormHandler.findUser : User exists");
			return user;
		}
		if (!BBBUtility.isEmpty(getLoginEmail())) {
			RepositoryItem profileItem = mTools.getItemFromEmail(getLoginEmail().toLowerCase());
			if (profileItem != null
					&& (BBBUtility.isEmpty(getLegacyUser()) || !getLegacyUser().equalsIgnoreCase(BBBCoreConstants.YES))) {
				logInfo("BBBProfileFormHandler.findUser : User has entered wrong password, increasing login attempt count and adding error to errorMap");
				updateLoginAttemptCount(profileItem, false);
				final Map<String, String> errorPlaceHolderMap = new HashMap<String, String>();
				final String loginPasswordError = getMessageHandler().getErrMsg(
						BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR, pRequest.getLocale().getLanguage(),
						errorPlaceHolderMap, null);
				addFormException(new DropletException(loginPasswordError, BBBCoreConstants.ERR_LOGIN_PASSWORD_ERROR));
				this.errorMap.put(BBBCoreConstants.LOGIN_PASSWORD_ERROR, loginPasswordError);
			}
		}
		logDebug("BBBProfileFormHandler.findUser : Ends");
		return null;
	}
	
	 	
		public Boolean validateToken(String HashValue, String URLToken, DynamoHttpServletRequest pRequest)
		{
			String SaltValue=null;
			try 
			{
				RepositoryItem[] secRandomRepositoryItem = null;
				BBBProfileTools profileTools=(com.bbb.account.BBBProfileTools) getProfileTools();
				final Object[] params = new Object[1];
				params[0] = HashValue;
				secRandomRepositoryItem = profileTools.executeRQLQuery(
						this.getForgotPasswordTokenQuery(), params,
							BBBCoreConstants.PASSWORD_SALT,
						this.getPasswordSaltRepository());
				if(null != secRandomRepositoryItem)
				{	
					if(!BBBUtility.isEmpty((String) secRandomRepositoryItem[0].getPropertyValue(BBBCoreConstants.VALUE)))
					{	
						SaltValue= (String) secRandomRepositoryItem[0].getPropertyValue(BBBCoreConstants.VALUE);
					}
				}
			}catch (BBBSystemException e) {
				logError(
						LogMessageFormatter.formatMessage(
								pRequest,
								"BBBBusinessException from service of ResetPasswordDroplet",
								BBBCoreErrorConstants.ERR_FETCHING_TOKEN), e);
				processPasswordNotGenreatedError(pRequest);
			} catch (BBBBusinessException e) {
				logError(
						LogMessageFormatter.formatMessage(
								pRequest,
								"BBBBusinessException from service of ResetPasswordDroplet",
								BBBCoreErrorConstants.ERR_FETCHING_TOKEN), e);
				processPasswordNotGenreatedError(pRequest);
			}	
			
			if(BBBUtility.isEmpty(SaltValue)||BBBUtility.isEmpty(URLToken)||!URLToken.equals(SaltValue)) 
			{
					processPasswordNotGenreatedError(pRequest);
					return false;
			}
			return true;
		}
		/** Used for Recognized user journey
	     * @param pRequest
	     * @param pResponse
	     * @return success/failure
	     * @throws ServletException
	     * @throws IOException
	     */
	    public final boolean handleRecogUserLogin(final DynamoHttpServletRequest pRequest,
	            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	    	this.logDebug("BBBProfileFormHandler.handleRegularLogin() method started");
	    	
	    	 
			boolean status=false;
			this.errorMap.clear();
	    	this.getFormExceptions().clear();
			String sessionEmailId=(String) getProfile().getPropertyValue("email");
			String loginEmail = (String) this.getValue().get("login");
			
			setRedirectURL(getRedirectURL());
			
			if (BBBUtility.isEmpty(getSessionBean().getRegistryTypesEvent()) || !getSessionBean().getRegistryTypesEvent().equalsIgnoreCase(getRegEventType())) {
				
				if (!BBBUtility.isEmpty(getRegEventType())) {
					// If Registry Event Type is coming form JSP once user confirms password set that
					getSessionBean().setRegistryTypesEvent(getRegEventType());
				} else {
					
					// Else set default Registry Event Type as session was expired post selecting Registry Event Type which is lost from session
					String defaultRegistryEventType = BBBCoreConstants.BLANK;
					try {
						List<String> regTypesList = getBbbCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE, BBBGiftRegistryConstants.DEFAULT_REG_TYPE);
						if (!BBBUtility.isListEmpty(regTypesList)) {
							defaultRegistryEventType = regTypesList.get(0);
						}
					} catch (BBBSystemException e) {
						logError("BBBSystemException occurred while fetching config key value for key default_reg_type ", e );
					} catch (BBBBusinessException e) {
						logError("BBBBusinessException occurred while fetching config key value for key default_reg_type ", e );
					}
					getSessionBean().setRegistryTypesEvent(defaultRegistryEventType);
				}
				
			}
			
			
			/* If user login with Same loginId ,invoke handleConfirmPassword else login */
			if(StringUtils.isNotBlank(sessionEmailId) && sessionEmailId.equalsIgnoreCase(loginEmail))
			{ 
				this.logDebug("BBBProfileFormHandler.handleRecogUserLogin() handleConfirmPassword");
				pRequest.setParameter(BBBCoreConstants.RECGONIZED_USER_FLOW,BBBCoreConstants.TRUE);
				status=handleConfirmPassword(pRequest, pResponse);
				
			}
			else{ 
				this.logDebug("BBBProfileFormHandler.handleRecogUserLogin() handleLoginUser");
				indirectRequest = true;
				//dont merge cart with saved shopping cart
				setUserCheckingOut("userCheckingOut");
				
				setRedirectionURLForNewUser();
				
				status= handleLoginUser(pRequest,pResponse);
				 
				}
			
			
			
			if(this.errorMap.size() <=  0 && getFormError() && getFormExceptions().size() > 0)
				{
				for(int i=0; i<getFormExceptions().size(); i++){
					DropletException formEx= (DropletException) getFormExceptions().get(i);
					this.errorMap.put(BBBCoreConstants.LOGIN_PASSWORD_ERROR, formEx.getMessage());
					break;
				 	}
				} else {
					getGiftRegistryManager().populateRegistryMapInOrder(pRequest, (BBBOrderImpl)getOrder());
				}
				
		return status;
	 }
	    /**
	     * used to remove items from order and logout currunt user.once user gets logout ,new order object will be populated from CartCookie
	     * @param pRequest
	     * @param pResponse
	     * @return
	     * @throws ServletException
	     * @throws IOException
	     */
	    public boolean handleRemoveItemsFromOrderAndLogOut(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
	            throws ServletException, IOException {
	    	boolean status=false;
	    	this.errorMap.clear();
	    	this.getFormExceptions().clear();
	    	boolean singlePageChkOut = false;
	    	if(getSessionBean().isSinglePageCheckout())
	    	{
	    		singlePageChkOut = true;
	    	}
			/*try {
				((BBBProfileTools)this.getProfileTools()).removeAllItemsFromOrder(pRequest, getOrder());
				} catch (CommerceException | BBBSystemException e) {
					 this.errorMap.put(BBBCoreConstants.ERROR, BBBCoreConstants.ERR_REMOVE_ITEM);
					 this.logError("BBBProfileFOrmHandler handleRemoveItemsFromOrderAndLogOut:"+e.getMessage());
				}*/
			
			/*  in case of GuestCheckout redirect to last viewed page instead on json Jsp */
			if(BBBCoreConstants.TRUE.equalsIgnoreCase((String) this.getValue().get(BBBCoreConstants.GUEST_CHECKOUT))){
				setRedirectionURLForNewUser();
				setLogoutSuccessURL(getRedirectURL());
			} else {
				setLogoutSuccessURL(getRedirectURL());
			}
			
			  status=super.handleLogout(pRequest, pResponse);
			/* add error in error map if any*/  
			if(this.errorMap.size() <=  0 && this.getFormExceptions().size() > 0)
			{
				this.errorMap.put(BBBCoreConstants.ERROR, BBBCoreConstants.ERR_IN_LOG_OUT);
			}else if(BBBCoreConstants.TRUE.equalsIgnoreCase((String) this.getValue().get(BBBCoreConstants.GUEST_CHECKOUT))){ 
				  pRequest.getSession().setAttribute(BBBCoreConstants.RECGONIZED_GUEST_USER_FLOW, BBBCoreConstants.TRUE);
				  pRequest.getSession().setAttribute(BBBCoreConstants.SESSIONIDFORSPC,pRequest.getSession().getId());
				  
				  //BBBP-10589 | setting session bean again after log out in case of recongnized user
				  BBBSessionBean bbbSessionBean = (BBBSessionBean) ServletUtil.getCurrentRequest().resolveName("/com/bbb/profile/session/SessionBean");
				  bbbSessionBean.setSinglePageCheckout(singlePageChkOut);
				  ServletUtil.getCurrentRequest().getSession().setAttribute("sessionBean", bbbSessionBean);
			}
				 
		 return status;
		}
	   
	
	private void setRedirectionURLForNewUser()
	{
		if(StringUtils.isNotBlank(getRedirectURL()))
		{ 
			String url=getRedirectURL();
			String appendContPath="";
			if(url.contains("/store/")){
				url=url.replaceFirst("/store/", "/");
				appendContPath="/store";
			}
			if(StringUtils.isNotBlank(url) && url.contains("?")){
				url=url.substring(0,url.indexOf("?"));
			}   
			/* if user login with new UserId from recognize state,dont redirect  new user to last user's profile specific pages */ 
			if(getRecUserRedirectionURL().containsKey(url) &&  StringUtils.isNotBlank(getRecUserRedirectionURL().get(url)))
			{
				setRedirectURL(appendContPath+getRecUserRedirectionURL().get(url));
			}
		}
	}


	
	public void clearCartCookie(final DynamoHttpServletResponse pResponse){
		final Cookie emptyCookie = new Cookie(getOrderCookieName(), "");
		emptyCookie.setMaxAge(0);
		emptyCookie.setPath(getOrderCookiePath());
		BBBUtility.addCookie(pResponse, emptyCookie, true);
	}
	
	
	/**
	 * Getters & setters section
	 *  
	 */
	
	
	
	/** @return The value of the property EditValue. This is a map that stores the pending values for an editing
     *         operations on the B2CStore profile map storing edit value for the B2CStore profile. */
    public final Map<String, Object> getEditValue() {
        return this.mEditValue;
    }

    /** @param pEditValue */
    public final void setEditValue(final Map<String, Object> pEditValue) {
        this.mEditValue = pEditValue;
    }
    
    /**
	 * Sets property giftlistId.
	 * 
	 * @param pGiftlistId
	 *            The property to store the giftlistId property of the current
	 *            giftlist.
	 * @beaninfo description: The property to store the giftlistId property of
	 *           the current giftlist.
	 **/
	public void setGiftlistId(String pGiftlistId) {
		mGiftlistId = pGiftlistId;
	}

	/**
	 * Returns property giftlistId.
	 * 
	 * @return The value of the property giftlistId.
	 **/
	public String getGiftlistId() {
		return mGiftlistId;
	}

	   /** @return mErrorMap */
    public  Map<String, String> getErrorMap() {
        return this.errorMap;
    }

    /** @param errorMap Error Map */
    public final void setErrorMap(final Map<String, String> errorMap) {
        this.errorMap = errorMap;
    }

}

    
       

