package com.bbb.pipeline;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.servlet.pipeline.InsertableServletImpl;
import atg.userprofiling.Profile;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCertonaConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.internationalshipping.utils.BBBInternationalShippingBuilder;
import com.bbb.internationalshipping.utils.InternationalShipTools;
import com.bbb.internationalshipping.vo.BBBInternationalContextVO;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * The class is a servlet to fetch the default country code of the user on the basis 
 * 
 *
 */
public class InternationalUserDetectionServlet extends InsertableServletImpl {


	public String defaultCountry;
	/** The international shipping builder. */
	private BBBInternationalShippingBuilder internationalShippingBuilder;
	
	private BBBCatalogTools catalogTools;
	private InternationalShipTools tools;
	private String defaultCurrency;
	
	
	public InternationalShipTools getTools() {
		return tools;
	}

	public String getDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(String defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	public void setTools(InternationalShipTools tools) {
		this.tools = tools;
	}

	/**
	 * @return the catalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public final void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}


	/**
	 * @return the internationalShippingBuilder
	 */
	public final BBBInternationalShippingBuilder getInternationalShippingBuilder() {
		return internationalShippingBuilder;
	}


	/**
	 * @param internationalShippingBuilder the internationalShippingBuilder to set
	 */
	public final void setInternationalShippingBuilder(
			BBBInternationalShippingBuilder internationalShippingBuilder) {
		this.internationalShippingBuilder = internationalShippingBuilder;
	}


	public String getDefaultCountry() {
		return defaultCountry;
	}


	public void setDefaultCountry(String defaultCountry) {
		this.defaultCountry = defaultCountry;
	}

	/**
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws ServletException
	 *             if there was an error while executing the code
	 * @throws IOException
	 *             if there was an error with servlet io
	 * @return void
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
		
		String defaultCountryCode = null;
		String defaultCurrencyCode = null;
		List<String> cookieValues = null;
		Boolean isIntShipOn=this.getTools().checkIntSwitch();
		if(isLoggingDebug()){
			logDebug(" International user Detection Servlet service method : is international shiping on ? "+isIntShipOn);
		}
		//check only if international shipping is ON
		if(isIntShipOn && (BBBCoreConstants.CONTEXT_STORE.equals(pRequest.getContextPath()) || 
					BBBCoreConstants.CONTEXT_REST.equals(pRequest.getContextPath()))){
			try {
				
				BBBSessionBean sessionBean = (BBBSessionBean) pRequest
						.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME);
				if (sessionBean == null) {
					sessionBean = (BBBSessionBean)pRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
					pRequest.setAttribute(BBBCoreConstants.SESSION_BEAN_NAME, sessionBean);
				}
				
				 
				final HashMap sessionMap = sessionBean.getValues();
				defaultCountryCode = (String)sessionMap.get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
				defaultCurrencyCode = (String)sessionMap.get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY);
				if(isLoggingDebug()){
					logDebug("Country code of user from the session  "+defaultCountryCode);
					logDebug("Currency code of user from the session  "+defaultCurrencyCode);
				}
				//if value is empty in session bean than its a fresh session so trying to fetch it from cookie first.
				if (BBBUtility.isEmpty(defaultCountryCode)) {
					if(isLoggingDebug()){
						logDebug("Country and Currency not available in session, hence trying to fetch from cookie");
					}
					cookieValues = checkInternationalCookie(pRequest);
					if (!BBBUtility.isListEmpty(cookieValues)) {
						Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
						defaultCountryCode = cookieValues.get(BBBCoreConstants.ZERO);
						defaultCurrencyCode = cookieValues.get(BBBCoreConstants.ONE);
						BBBInternationalContextVO contextVO = this.getInternationalShippingBuilder().buildContextBasedOnCountryCode(defaultCountryCode);
						if(contextVO != null && contextVO.getShippingLocation() != null){
							sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SHIPPING_ENABLED, contextVO.getShippingLocation().isShippingEnabled());
						}
						if(isLoggingDebug()){
							logDebug("Selected country fetched from cookie: "+ defaultCountryCode);
							logDebug("Selected currency fetched from cookie: "+ defaultCurrencyCode);
							logDebug("Setting user profile and session with country: " + defaultCountryCode + " and Currency: " + defaultCurrencyCode);
						}
						defaultCurrencyCode = this.mexicoCurrencyCorrectionAndCookieUpdate(pRequest, pResponse, defaultCountryCode, defaultCurrencyCode);
						sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT, defaultCountryCode);
						sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY, defaultCurrencyCode);
						sessionBean.setInternationalShippingContext(!this.getDefaultCountry().equals(defaultCountryCode));
						profile.setPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE, defaultCountryCode);
						profile.setPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE, defaultCurrencyCode);
						profile.setPropertyValue(BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT, !this.getDefaultCountry().equals(defaultCountryCode));
					}
				}

				// country and currency codes are not present in session or cookie so making a DB call to fetch these values and then setting them in cookie and session again.
				if(StringUtils.isEmpty(defaultCountryCode)){
					if(isLoggingDebug()){
						logDebug("New country and currency code to be set in user profile is based on ip of user ");
					}
					String userIpAddress=tools.getUserIpAddress(pRequest);
					//BBBSL-4291 enhancement. Added BOT check for the user request. If the request is from BOT, don't do the country code lookup.
					boolean isRobot = false;
					BBBInternationalContextVO contextVO = null ;
					isRobot = BBBUtility.isRobot(pRequest);//BrowserTyper.isBrowserType(this.getmRobot(), pRequest.getHeader(BBBCertonaConstants.USER_AGENT));
					if(isRobot){
						if(isLoggingDebug()){
							this.logDebug("User's request is from BOT hence setting default country and currency codes");
						}
					}
					defaultCountryCode=getDefaultCountry();
					defaultCurrencyCode=getDefaultCurrency();
					if(!(StringUtils.isEmpty(userIpAddress) || isRobot)){
						if(isLoggingDebug()){
							logDebug("Current request is NOT from BOT and the user's IP address is not empty hence making a DB call to fetch the country and currency codes");
						}
						
						contextVO = this.getCountryCodeFromVO(userIpAddress);
						if(contextVO != null){
							if(contextVO.getShippingLocation() != null){
								defaultCountryCode=contextVO.getShippingLocation().getCountryCode();
							}
							if(contextVO.getShoppingCurrency() != null){
								defaultCurrencyCode = contextVO.getShoppingCurrency().getCurrencyCode();
							}
						}
					}

					BBBUtility.addCookie(pResponse, this.getTools().setInternationalShipCookie(defaultCountryCode, defaultCurrencyCode, pRequest), false);
					if(isLoggingDebug()){
						logDebug(new StringBuffer("Setting user profile and session with country: ")
								.append(defaultCountryCode).append(" and Currency: ")
								.append(defaultCurrencyCode).append(" of ip ").append(userIpAddress).toString());
						logDebug("Selected country code based on ip "+userIpAddress+" is "+defaultCountryCode);
						logDebug("Selected currency code based on ip "+userIpAddress+" is "+defaultCurrencyCode);
						logDebug(new StringBuffer("Cookie is created with Country code :  ")
								.append(defaultCountryCode).append(" and Currency: ").append(defaultCurrencyCode)
								.append(" and IP Address: ").append(userIpAddress).toString());
		}
					Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
					sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT, defaultCountryCode);
					sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY, defaultCurrencyCode);
					sessionBean.setInternationalShippingContext(!this.getDefaultCountry().equals(defaultCountryCode));
					profile.setPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE, defaultCountryCode);
					profile.setPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE, defaultCurrencyCode);
					profile.setPropertyValue("internationalShippingContext", !this.getDefaultCountry().equals(defaultCountryCode));
				}
			} catch (BBBSystemException e) {
				logError(LogMessageFormatter.formatMessage(null, "BBBSystemException from service of InternationalUserDetectionServlet :"), e);
			} catch (BBBBusinessException e) {
				logError(LogMessageFormatter.formatMessage(null, "BBBBusinessException from service of InternationalUserDetectionServlet :"), e);
			}
		}
		
		passRequest(pRequest, pResponse);
	}

	

	/**
	 * The method gets the country code of the country to which the user IP belongs.
	 *
	 * @param ipAddress the ip address
	 * @return the country code from vo
	 */

	public BBBInternationalContextVO getCountryCodeFromVO(String ipAddress){
		if(isLoggingDebug()){
			logDebug(" enter method getCountryCodeFromVO :IpAddress "+ipAddress);
		}	
		try {
			final BBBInternationalContextVO contextVO=this.getInternationalShippingBuilder().buildContextFromIP(ipAddress);
			if(contextVO!=null && contextVO.getShippingLocation()!=null){
				String countryFromIP=contextVO.getShippingLocation().getCountryCode();
				final BBBSessionBean sessionBean =
						(BBBSessionBean)ServletUtil.getCurrentRequest().resolveName("/com/bbb/profile/session/SessionBean");
				sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SHIPPING_ENABLED,(contextVO.getShippingLocation().isShippingEnabled()));
				if(isLoggingDebug()){
					logDebug(" Country code of the Ip address is :"+countryFromIP);
				}	
				return contextVO;
			}
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBSystemException from service of InternationalShipTools : getCountryCodeFromVO"), e);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBBusinessException from service of InternationalShipTools : getCountryCodeFromVO"), e);
		}
		if(isLoggingDebug()){
			logDebug(" returning default value of country code :"+this.getDefaultCountry());
		}
		return null;
	}

	/**
	 * This method gets the default country selected by user from cookie.
	 *
	 * @param pRequest the new checkout param
	 * @return List
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public List<String> checkInternationalCookie(final DynamoHttpServletRequest pRequest ) throws BBBSystemException, BBBBusinessException {

		if(isLoggingDebug()){
			this.logDebug(LogMessageFormatter.formatMessage(null,
				"Inside  method of InternationalUserDetectionServlet : checkInternationalCookie"));
		}
				
		Cookie cookie=null;
		cookie=this.getTools().getIntShipCookieAvailable(pRequest);
		List<String> intUserCookieSelection = null;
		if(cookie !=null){
			if(isLoggingDebug()){
				logDebug(" International cookie is set with value :"+cookie.getValue());
			}
			intUserCookieSelection= this.getTools().getDefaultFrmCookie(cookie);
		}
		return intUserCookieSelection;
	}
	/**
	 * This method checks for the selected country and returns the updated currency
	 * @param pRequest the new checkout param
	 * @return List
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	
	public String mexicoCurrencyCorrectionAndCookieUpdate(DynamoHttpServletRequest pRequest, 
			DynamoHttpServletResponse pResponse, String countryCode, String currencyCode) {
		String currencyCodeUpdated = currencyCode;
		if (countryCode.equalsIgnoreCase(BBBInternationalShippingConstants.MEXICO_COUNTRY) && !currencyCode.equalsIgnoreCase(BBBInternationalShippingConstants.CURRENCY_MEXICO)) {
			currencyCodeUpdated = BBBInternationalShippingConstants.CURRENCY_MEXICO;
			BBBUtility.addCookie(pResponse, this.getTools().setInternationalShipCookie(countryCode, currencyCodeUpdated, pRequest), false);
			if (isLoggingDebug()) { 
				this.logDebug(new StringBuffer("Update Currency for user Session ")
						.append(pRequest.getSession().getId()).append("from Currency ").append(currencyCode)
						.append("to currency").append(currencyCodeUpdated).toString());
			}
		}
		return currencyCodeUpdated;
	}


}
