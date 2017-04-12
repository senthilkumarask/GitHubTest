package com.bbb.email.internationalDetail;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.repository.MutableRepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.internationalshipping.utils.InternationalShipTools;
import com.bbb.internationalshipping.vo.BBBInternationalContextVO;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * The Class UpdateEmailProperty.
 */
public class UpdateEmailPropertyFormHandler extends BBBGenericFormHandler {

	

	/** The success url. */
	private String successURL;
	
	/** The error url. */
	private String errorURL;
	
	
	private Profile profile;
	
	
	/** The tools. */
	private InternationalShipTools tools;
	
	/** The user selected country. */
	String userSelectedCountry;
	
	/** The user selected currency. */
	String userSelectedCurrency;
	
	private BBBSessionBean sessionBean;
	
	private String usCountryCode;
	
	private String usCurrencyCode;	
	
	private BBBInternationalContextVO intContextVO;
	

	/**
	 * @return the intContextVO
	 */
	public BBBInternationalContextVO getIntContextVO() {
		return intContextVO;
	}


	/**
	 * @param intContextVO the intContextVO to set
	 */
	public void setIntContextVO(BBBInternationalContextVO intContextVO) {
		this.intContextVO = intContextVO;
	}

	/**
	 * @return the usCountryCode
	 */
	public String getUsCountryCode() {
		return usCountryCode;
	}


	/**
	 * @param usCountryCode the usCountryCode to set
	 */
	public void setUsCountryCode(String usCountryCode) {
		this.usCountryCode = usCountryCode;
	}


	/**
	 * @return the usCurrencyCode
	 */
	public String getUsCurrencyCode() {
		return usCurrencyCode;
	}


	/**
	 * @param usCurrencyCode the usCurrencyCode to set
	 */
	public void setUsCurrencyCode(String usCurrencyCode) {
		this.usCurrencyCode = usCurrencyCode;
	}


	/**
	 * @return the sessionBean
	 */
	public BBBSessionBean getSessionBean() {
		return sessionBean;
	}


	/**
	 * @param sessionBean the sessionBean to set
	 */
	public void setSessionBean(BBBSessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}


	/**
	 * Gets the tools.
	 *
	 * @return the tools
	 */
	public InternationalShipTools getTools() {
		return tools;
	}


	/**
	 * Sets the tools.
	 *
	 * @param tools the new tools
	 */
	public void setTools(final InternationalShipTools tools) {
		this.tools = tools;
	}


	/**
	 * Gets the user selected country.
	 *
	 * @return the user selected country
	 */
	public String getUserSelectedCountry() {
		return userSelectedCountry;
	}


	/**
	 * Sets the user selected country.
	 *
	 * @param userSelectedCountry the new user selected country
	 */
	public void setUserSelectedCountry(final String userSelectedCountry) {
		this.userSelectedCountry = userSelectedCountry;
	}


	/**
	 * Gets the user selected currency.
	 *
	 * @return the user selected currency
	 */
	public String getUserSelectedCurrency() {
		return userSelectedCurrency;
	}


	/**
	 * Sets the user selected currency.
	 *
	 * @param userSelectedCurrency the new user selected currency
	 */
	public void setUserSelectedCurrency(final String userSelectedCurrency) {
		this.userSelectedCurrency = userSelectedCurrency;
	}


	/**
	 * Gets the success url.
	 *
	 * @return the success url
	 */
	public String getSuccessURL() {
		return successURL;
	}


	/**
	 * Sets the success url.
	 *
	 * @param successURL the new success url
	 */
	public void setSuccessURL(String successURL) {
		this.successURL = successURL;
	}


	/**
	 * Gets the error url.
	 *
	 * @return the error url
	 */
	public String getErrorURL() {
		return errorURL;
	}


	/**
	 * Sets the error url.
	 *
	 * @param errorURL the new error url
	 */
	public void setErrorURL(final String errorURL) {
		this.errorURL = errorURL;
	}


	 public Profile getProfile() {
			return this.profile;
		}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	
		
		/**
		 * Handle currency country for Email selector.the form handler sets cookie to store country and currency
		 * @param pRequest the request
		 * @param pResponse the response
		 * @return true, if successful
		 * @throws ServletException the servlet exception
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public final boolean handleUpdateUserContextForEmail(final DynamoHttpServletRequest pRequest,final DynamoHttpServletResponse pResponse)
				throws ServletException,IOException {
			this.logDebug("Entering - UpdateEmailProperty " + "Method Name [handleUpdateUserContextForEmail]");
			
			String redirectUrl= pRequest.getHeader(BBBCoreConstants.REFERRER);
			if (BBBUtility.isNotEmpty(redirectUrl) && !redirectUrl.contains(BBBCoreConstants.CONTEXT_STORE)){
				redirectUrl=pRequest.getScheme() + BBBCoreConstants.CONSTANT_SLASH +pRequest.getServerName()+BBBCoreConstants.CONTEXT_STORE;
			}
			this.setSuccessURL(redirectUrl);
			this.setErrorURL(redirectUrl);
			
			if(!StringUtils.isEmpty(getUserSelectedCountry()) && !StringUtils.isEmpty( getUserSelectedCurrency())){
				try {
				BBBUtility.addCookie(pResponse, this.getTools().setInternationalShipCookie(getUserSelectedCountry(), getUserSelectedCurrency(), pRequest), false);
					BBBInternationalContextVO contextVO = getTools().getInternationalShippingBuilder().buildContextBasedOnCountryCode(getUserSelectedCountry());
					this.logDebug("Setting user profile and session with country: " + getUserSelectedCountry() + " and currency: " + getUserSelectedCurrency());
					getSessionBean().getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT, getUserSelectedCountry());
					getSessionBean().getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY, getUserSelectedCurrency());
					getSessionBean().setInternationalShippingContext(!getUserSelectedCountry().equals(getUsCountryCode()));
					((MutableRepositoryItem) this.getProfile()).setPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE, getUserSelectedCountry());
					((MutableRepositoryItem) this.getProfile()).setPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE, getUserSelectedCurrency());
					((MutableRepositoryItem) this.getProfile()).setPropertyValue(BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT,
							!getUserSelectedCountry().equals(getUsCountryCode()));
					if (contextVO != null && contextVO.getShippingLocation() != null) {
						this.setIntContextVO(contextVO);
						getSessionBean().getValues().put(BBBInternationalShippingConstants.KEY_FOR_SHIPPING_ENABLED, contextVO.getShippingLocation().isShippingEnabled());
					}
					
				} catch (BBBSystemException e) {
					this.logError("System Exception in UpdateEmailProperty:handleUpdateUserContextForEmail");
				} catch (BBBBusinessException e) {
					this.logError("Business Exception in UpdateEmailProperty:handleUpdateUserContextForEmail");
				} 
			}
			return this.checkFormRedirect(this.successURL, this.errorURL, pRequest, pResponse);
		}
		
}

